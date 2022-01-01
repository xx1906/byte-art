package com.training.state;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.checkpoint.ListCheckpointed;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.util.Collector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MyListState {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.enableCheckpointing(1000);
        CheckpointConfig checkpointConfig = env.getCheckpointConfig();
        //保存EXACTLY_ONCE
        checkpointConfig.setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);
        //每次ck之间的间隔，不会重叠
        checkpointConfig.setMinPauseBetweenCheckpoints(2000L);
        //每次ck的超时时间
        checkpointConfig.setCheckpointTimeout(20000L);
        //如果ck执行失败，程序是否停止
        checkpointConfig.setFailOnCheckpointingErrors(true);
        //job在执行CANCE的时候是否删除ck数据
        checkpointConfig.enableExternalizedCheckpoints(CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

        //恢复策略
        env.setRestartStrategy(
                RestartStrategies.fixedDelayRestart(
                        Integer.MAX_VALUE, // number of restart attempts
                        Time.of(0, TimeUnit.SECONDS) // delay
                )
        );

        MemoryStateBackend stateBackend = new MemoryStateBackend(100 * 1024 * 1024, true);
        env.setStateBackend(stateBackend);
        env.setParallelism(2);


        DataStreamSource<Integer> source = env.addSource(new mySource());
        SingleOutputStreamOperator<Integer> flatMap = source.flatMap(new myRichFlatMap());

        flatMap.print();

        env.execute(MyListState.class.getName());
    }

    @Slf4j
    static class mySource implements SourceFunction<Integer>, ListCheckpointed<Integer> {
        private Boolean isCancel = false;
        Integer incr = Integer.valueOf(0);

        @Override
        public void run(SourceContext<Integer> ctx) throws Exception {
            while (!isCancel) {
                ctx.collect(incr);
                System.out.println("发送数据: " + incr);
                incr++;
                Thread.sleep(1000);
                // 这里触发程序异常
                if (incr % 10 == 0) {
                    incr = 1 / 0;
                }
            }
        }

        @Override
        public void cancel() {
        }

        // 将数据持久化
        @Override
        public List<Integer> snapshotState(long checkpointId, long timestamp) throws Exception {
            ArrayList<Integer> integers = new ArrayList<>();
            integers.add(incr);
            System.out.println("持久化 " + incr);
            return integers;
        }

        // 程序出错恢复的时候, 都会调用这个方法, 用户代码中可以获取之间 checkpoint 的值
        @Override
        public void restoreState(List<Integer> state) throws Exception {
            incr = state.get(0);
            incr++; // incr++ 这个是业务代码, 保证 incr % 10 !=0 持续出错的问题
        }
    }

    @Slf4j
    static class myRichFlatMap extends RichFlatMapFunction<Integer, Integer> implements ListCheckpointed<Integer> {

        private Integer sum = Integer.valueOf(0);
        // private ListState<Integer> listState;

        @Override
        public void flatMap(Integer integer, Collector<Integer> collector) throws Exception {
            sum += integer;
            collector.collect(integer);
            System.out.println(getRuntimeContext().getIndexOfThisSubtask() + ">" + sum);
        }

        @Override
        public List<Integer> snapshotState(long checkpointId, long timestamp) throws Exception {
            ArrayList<Integer> integers = new ArrayList<>();
            integers.add(sum);
            System.out.println("持久化 checkpoint" + new Date().toString() + ", sum:" + sum + ", 子任务id:" + getRuntimeContext().getIndexOfThisSubtask());
            return integers;
        }

        @Override
        public void restoreState(List<Integer> state) throws Exception {
            System.out.println("restoreState+++");
            sum = state.get(0);
            if (sum == null) {
                System.out.println("获取不到路径");
            } else {
                System.out.println("成功恢复值 " + sum);
            }
        }

//        @Override
//        public void snapshotState(FunctionSnapshotContext context) throws Exception {
//            listState.clear();
//            listState.add(sum);
//
//            System.out.println("持久化 checkpoint" + new Date().toString() + ", sum:" + sum + ", 子任务id:" + getRuntimeContext().getIndexOfThisSubtask());
//        }
//
//        @Override
//        public void initializeState(FunctionInitializationContext context) throws Exception {
//            ListStateDescriptor<Integer> descriptor = new ListStateDescriptor<>( "_" + "list_value", TypeInformation.of(Integer.class));
//            listState = context.getOperatorStateStore().getListState(descriptor);
//            if (context.isRestored()) {
//                sum = listState.get().iterator().next();
//                System.out.println("获取到之前保存的值" + sum);
//                log.info("获取到之前保存的值:{}", sum);
//            }else {
//                System.out.println("没有获取到之前保存的值");
//            }
//        }
    }

}
