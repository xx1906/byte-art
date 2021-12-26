package com.training.kafkaDataSource;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Properties;

//
// flink 编程模式
// 1. 获取flink 执行程序
// 2. 添加程序数据源
// 3. 将 dataStream 进行各种转换
// 4. 将 数据 sink
// 5.  execute flink 程序
@Slf4j
public class KafkaDataSource {
    public static void main(String[] args) throws Exception {
        log.info(KafkaDataSource.class.getName());

        // 1. 获取 flink 程序的运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        Properties props = new Properties();
        ParameterTool params = ParameterTool.fromArgs(args);
        String boot = "hadoop1:9092";
        if (params.has("boot")) {
            boot = params.get("boot");
        }
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boot);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        log.info("props:{}", props);
        // 2. 添加数据源
        FlinkKafkaConsumer010<String> flinkInput = new FlinkKafkaConsumer010<>("flink_input", new SimpleStringSchema(), props);

        DataStreamSource src = env.addSource(flinkInput);

        // 3. 进行数据转换
        SingleOutputStreamOperator filter = src.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String input) throws Exception {
                return input.split(" ").length == 2;
            }
        }).setMaxParallelism(4);

        // 4. 输出数据
        filter.print();

        //5. 运行 flink 程序
        env.execute(KafkaDataSource.class.getName());
    }
}
