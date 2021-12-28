package com.training.operator;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class FlatMapTask {
    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        Collection<String> col = new ConcurrentLinkedQueue<>();
        for (int i = 0; i < 10; i++) {
            col.add(String.valueOf(i) + "_");
        }

        DataStreamSource<String> source = env.fromCollection(col);
        SingleOutputStreamOperator<String> flatMap = source.flatMap(new FlatMapFunction<String, String>() {
            @Override
            public void flatMap(String value, Collector<String> out) throws Exception {
                for (int i = 0; i < 3; i++) {
                    out.collect(value);
                }
            }
        });

        flatMap.print();
        env.execute("flat_map_collect");

    }
}
