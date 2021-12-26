package com.training.keybyconnnect2kafkainputsource;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.HashMap;
import java.util.Properties;

@Slf4j
public class KeyByConnect2KafkaInputSource {
    public static void main(String[] args) throws Exception {
        log.info("run:{}", KeyByConnect2KafkaInputSource.class.getName());
        ParameterTool params = ParameterTool.fromArgs(args);
        Properties props = new Properties();
        String boot = "hadoop1:9092";
        if (params.has("boot")) {
            boot = params.get("boot");
        }

        String topic1 = "flink_connect_1";
        String topic2 = "flink_connect_2";

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, boot);

        // 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 构造两个数据源
        FlinkKafkaConsumer010<String> input1 = new FlinkKafkaConsumer010<String>(topic1, new SimpleStringSchema(), props);
        FlinkKafkaConsumer010<String> input2 = new FlinkKafkaConsumer010<String>(topic2, new SimpleStringSchema(), props);
        // 设置 kafka 数据从最开始读
        input1.setStartFromEarliest();
        input2.setStartFromEarliest();

        // 添加数据源
        DataStreamSource<String> source1 = env.addSource(input1);
        DataStreamSource<String> source2 = env.addSource(input2);

        // 使用 KeyBy 对 source1 和 source2 分组
        // 使用 map 对输入的数据进行元组化
        KeyedStream<Tuple2<String, String>, String> tuple2Tuple2KeyedStream = source1.map(new MapFunction<String, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> map(String input) throws Exception {
                String[] sps = input.split(" ");
                if (sps.length != 2) {
                    return Tuple2.of("", "");
                }
                return Tuple2.of(sps[0], sps[1]);
            }
            // 对元组化的数据进行 key by
            /**
             * 第一个类型是输入参数
             * 第二个类型是 key 类型
             */
        }).keyBy(new KeySelector<Tuple2<String, String>, String>() {
            @Override
            public String getKey(Tuple2<String, String> input) throws Exception {
                return input.f0;
            }
        });


        KeyedStream<Tuple2<String, String>, String> tuple2StringKeyedStream = source2.map(new MapFunction<String, Tuple2<String, String>>() {
            @Override
            public Tuple2<String, String> map(String input) throws Exception {
                String[] sps = input.split(" ");
                if (sps.length != 2) {
                    return Tuple2.of("", "");
                }
                return Tuple2.of(sps[0], sps[1]);
            }
        }).keyBy(new KeySelector<Tuple2<String, String>, String>() {
            @Override
            public String getKey(Tuple2<String, String> input) throws Exception {
                return input.f0;
            }
        });

        // connect 两个 dataStream
        ConnectedStreams<Tuple2<String, String>, Tuple2<String, String>> connect = tuple2StringKeyedStream.connect(tuple2Tuple2KeyedStream);

        // 使用 process 方法对两个流进行处理
        SingleOutputStreamOperator<String> process = connect.process(new CoProcessFunction<Tuple2<String, String>, Tuple2<String, String>, String>() {

            private HashMap<String, String> source1Map = new HashMap<String, String>();
            private HashMap<String, String> source2Map = new HashMap<String, String>();

            @Override
            public void processElement1(Tuple2<String, String> input, Context ctx, Collector<String> out) throws Exception {
                source1Map.put(input.f0, input.f1);
                String address = source2Map.get(input.f0);
                if (address == null) {
                    return;
                }
                out.collect("id:" + input.f0 + ", name:" + input.f1 + ", address:" + address);
            }

            @Override
            public void processElement2(Tuple2<String, String> input, Context ctx, Collector<String> out) throws Exception {
                source2Map.put(input.f0, input.f1);
                String name = source1Map.get(input.f0);
                if (name == null) {
                    return;
                }
                out.collect("id:" + input.f0 + ", address:" + input.f1 + ", name:" + name);
            }
        });

        process.print();

        // 执行 Flink 程序
        env.execute("keyByConnect2KafkaInputSource");
    }
}
