/**
 * 描述:程序的功能是使用两个kafka 的输入流，然后合并成一个流再写入到 kafka
 * <p>
 * 1. 构造两个 kafka 对象的输入流
 * 2. 使用 filter 将输入的流按照空格切分， 并过滤长度不等于 2 的切分值
 * 3. 分别使用:将输入流使用 map 转成 id2Name 对象和 将输入流使用 map转成 id2Job 对象
 * 4. 使用 Connect 合并 map 之后的流对象
 * 5. 使用 CoProcessFunction 将 connect 流合并成宽表对象流
 * 6. 将块表对象流的结果写入到 kafka 中
 * 7. 启动 kafka 程序
 * <p>
 * **使用 KeyBy 并行度只能设置为 1 的问题,::: keyBy 将相同的 Key 发送到相同的 subTask**
 */
package com.training.transformation;

import akka.routing.MurmurHash;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer010;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

@Slf4j
public class ConnectTransformationKeyedBy {

    public static void main(String[] args) throws Exception {
        log.info("starting...{}", new Date());
        ParameterTool params = ParameterTool.fromArgs(args);
        log.info("params:{}", params.toMap().entrySet());
        String endpoint = params.get("endpoint", "hadoop1:9092");
        String topic1 = params.get("topic1", "flink_connect_1");
        String topic2 = params.get("topic2", "flink_connect_2");
        String outputTopic = params.get("output", "flink_connect_output");
        log.info("endpoint:{}， topic1:{}, topic2:{}, outputTopic:{}", endpoint, topic1, topic2, outputTopic);

        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, endpoint);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        log.info("props:{}", props);
        // 1. 获取 Flink 执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 创建数据源
        FlinkKafkaConsumer010<String> flinkKafkaConsumer01 = new FlinkKafkaConsumer010<>(topic1, new SimpleStringSchema(), props);
        FlinkKafkaConsumer010<String> flinkKafkaConsumer02 = new FlinkKafkaConsumer010<>(topic2, new SimpleStringSchema(), props);

        // 设置从最早的数据开始消费
        flinkKafkaConsumer01.setStartFromEarliest();
        flinkKafkaConsumer02.setStartFromEarliest();
        // 添加数据源
        DataStreamSource<String> kafkaSource1 = env.addSource(flinkKafkaConsumer01);
        DataStreamSource<String> kafkaSource2 = env.addSource(flinkKafkaConsumer02);

        // 过滤长度不符合的字符串
        SingleOutputStreamOperator<String> filter1 = kafkaSource1.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String input) {
                System.out.println(input);
                return input.split(" ").length == 2;
            }
        });

        // 过滤长度不符合的字符串
        SingleOutputStreamOperator<String> filter2 = kafkaSource2.filter(new FilterFunction<String>() {
            @Override
            public boolean filter(String input) {
                System.out.println(input);
                return input.split(" ").length == 2;
            }
        });

        // 1. 将输入的字符串按照空格切分， 然后取第一个值计算murmurhash 进行 keyBY
        // 将字符串转成 id2Name 对象输出
        SingleOutputStreamOperator<id2Name> mapId2Name = filter1.keyBy(new KeyByStringInt()).map(new MapFunction<String, id2Name>() {
            @Override
            public id2Name map(String value) {
                String[] sps = value.split(" ");
                return new id2Name(sps[0], sps[1]);
            }
        });

        // 1. 将输入的字符串按照空格切分， 然后取第一个值计算murmurhash 进行 keyBY
        // 将 字符串转成 id2Job 对象输出
        SingleOutputStreamOperator<id2Job> mapId2Job = filter2.keyBy(new KeyByStringInt()).map((MapFunction<String, id2Job>) value -> {
            String[] sps = value.split(" ");
            return new id2Job(sps[0], sps[1]);
        });

        // connect 两个 OutputStream 然后将两个流的数据按照 id 链接
        ConnectedStreams<id2Name, id2Job> connect = mapId2Name.connect(mapId2Job);

        // 执行两个流的合并运算
        SingleOutputStreamOperator<idNameJob> process = connect.process(new joinId2NameJobCoProcessFunctionWithoutState());

        // 构造 kafka sink
        FlinkKafkaProducer010<idNameJob> producer010 = new FlinkKafkaProducer010<idNameJob>(outputTopic, new idNameJob(), props);
        // 添加 kafka 的 sink
        process.addSink(producer010);
        process.print();

        // 执行 Flink 的程序
        env.execute("connect_trans");
    }


    // 将输入的字符串, 按照空格切分，取第一个值计算哈希值 进行 keyBy 运算
    private static class KeyByStringInt implements KeySelector<String, Integer> {
        @Override
        public Integer getKey(String value) {
            String[] sps = value.split(" ");
            if (sps.length != 2) {
                return 0;
            }
            return MurmurHash.stringHash(sps[0]);
        }
    }
}

