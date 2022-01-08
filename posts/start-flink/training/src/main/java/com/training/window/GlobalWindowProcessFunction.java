package com.training.window;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.GlobalWindows;
import org.apache.flink.streaming.api.windowing.evictors.Evictor;
import org.apache.flink.streaming.api.windowing.triggers.CountTrigger;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.runtime.operators.windowing.TimestampedValue;
import org.apache.flink.util.Collector;

import java.util.Date;
import java.util.Iterator;

@Slf4j
public class GlobalWindowProcessFunction {
    public static void main(String[] args) throws Exception {
        log.info("data: global {}", new Date());
        // 获取运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        // 禁止共享槽
        env.disableOperatorChaining();
        // 设置事件对于时间的处理
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        // 添加数据源
        DataStreamSource<GlobalWindowProcessFunctionData> source = env.addSource(new GlobalWindowSource());

        // 提取事件的水位线
        SingleOutputStreamOperator<GlobalWindowProcessFunctionData> asignWatermark = source.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<GlobalWindowProcessFunctionData>() {
            @Override
            public long extractAscendingTimestamp(GlobalWindowProcessFunctionData element) {
                return element.eventTime;
            }
        });

        // 按照 key 进行分组操作
        KeyedStream<GlobalWindowProcessFunctionData, String> keyBy = asignWatermark.keyBy(new KeySelector<GlobalWindowProcessFunctionData, String>() {
            @Override
            public String getKey(GlobalWindowProcessFunctionData value) throws Exception {
                return value.data;
            }
        });

        // 创建窗口
        WindowedStream<GlobalWindowProcessFunctionData, String, GlobalWindow> window = keyBy.window(GlobalWindows.create());

        // 触发器, 触发窗口什么时候开始运算
        // 触发器必须要有,
        WindowedStream<GlobalWindowProcessFunctionData, String, GlobalWindow> trigger = window.trigger(CountTrigger.of(5));

        // 驱逐器,
        // evictBefore 进行窗口运算之前, 哪些元素可以进入窗口运算
        // evictAfter 进行窗口运算之后, 哪些元素要被驱逐出窗口
        // evictor 可以控制进入窗口运算的数据量,
        // 运算完成之后, 还可以控制哪些数据能进入到下一个窗口进行运算
        WindowedStream<GlobalWindowProcessFunctionData, String, GlobalWindow> evictor = trigger.evictor(new Evictor<GlobalWindowProcessFunctionData, GlobalWindow>() {
            @Override
            public void evictBefore(Iterable<TimestampedValue<GlobalWindowProcessFunctionData>> elements, int size, GlobalWindow window, EvictorContext evictorContext) {
                System.out.println("被驱逐之前的数据+++" + window.maxTimestamp() + evictorContext.getCurrentWatermark());
            }

            @Override
            public void evictAfter(Iterable<TimestampedValue<GlobalWindowProcessFunctionData>> elements, int size, GlobalWindow window, EvictorContext evictorContext) {
                Iterator<TimestampedValue<GlobalWindowProcessFunctionData>> iterator = elements.iterator();
                for (; iterator.hasNext(); ) {
                    TimestampedValue<GlobalWindowProcessFunctionData> next = iterator.next();
                    if (next.hasTimestamp() && next.getTimestamp() < window.maxTimestamp()) {
                        iterator.remove(); // 移除元素
                        System.out.println("被驱逐之后的数据移除元素----" + next);
                    }
                }
            }
        });

        // 进行窗口运算
        SingleOutputStreamOperator<GlobalWindowProcessFunctionData> process = evictor.process(new ProcessWindowFunction<GlobalWindowProcessFunctionData, GlobalWindowProcessFunctionData, String, GlobalWindow>() {
            @Override
            public void process(String key, Context context, Iterable<GlobalWindowProcessFunctionData> elements, Collector<GlobalWindowProcessFunctionData> out) throws Exception {
                System.out.println("key " + key);
                Iterator<GlobalWindowProcessFunctionData> iterator = elements.iterator();
                for (; iterator.hasNext(); ) {
                    GlobalWindowProcessFunctionData next = iterator.next();
                    System.out.println("data: " + next);
                    out.collect(next);
                }
            }
        });

        // sink
        process.print();

        // 运行 Flink 程序
        env.execute();
    }

    public static class GlobalWindowProcessFunctionData {
        public String data = "";
        public long eventTime = 0;
        public int sn = 0;

        @Override
        public String toString() {
            return "GlobalWindowProcessFunctionData{" +
                    "data='" + data + '\'' +
                    ", eventTime=" + eventTime +
                    ", sn=" + sn +
                    '}';
        }
    }

    public static class GlobalWindowSource implements SourceFunction<GlobalWindowProcessFunctionData> {
        private int sn = 0;
        private boolean isCancel = false;

        @Override
        public void run(SourceContext<GlobalWindowProcessFunctionData> ctx) throws Exception {
            while (!isCancel) {
                GlobalWindowProcessFunctionData record = new GlobalWindowProcessFunctionData();

                Thread.sleep(1000);

                record.sn = sn++;
                record.data = "foo.baz";
                record.eventTime = System.currentTimeMillis();
                ctx.collect(record);
            }
        }

        @Override
        public void cancel() {
            isCancel = true;
            System.out.println("cancel : " + new Date());
        }
    }
}
