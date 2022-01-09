package com.training.accumulator;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.accumulators.IntCounter;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.elasticsearch.common.collect.HppcMaps;

@Slf4j
public class IntegerAccumulatorV {
    public static void main(String[] args) throws Exception {
        log.info("data:{}", HppcMaps.Object.Integer.class);
        // 统计累加数据
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        // 设置并行度为 1
        env.setParallelism(1);

        // 从socket 读取数据
        DataStreamSource<String> socket = env.socketTextStream("hadoop1", 6666);
        // 进行数据转换
        SingleOutputStreamOperator<String> map = socket.map(new RichMapFunction<String, String>() {
            // 使用内置的 IntCounter
            private IntCounter inc = null;

            @Override
            public void open(Configuration parameters) throws Exception {
                // 初始化 IntCounter
                super.open(parameters);
                inc = new IntCounter();
                // 加入 IntCounter
                getRuntimeContext().addAccumulator("accumulator_integer_inc", inc);
            }

            @Override
            public String map(String value) throws Exception {
                inc.add(1);
                return value;
            }

            @Override
            public void close() throws Exception {
                super.close();
            }
        });
        map.print();

        // 获取运行时返回值
        JobExecutionResult execute = env.execute();
        // 获取计算值
        Integer accumulator_integer_inc = execute.getAccumulatorResult("accumulator_integer_inc");

        System.out.println("Accumulator " + accumulator_integer_inc);


    }
}
