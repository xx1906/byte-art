// 这个程序模拟两个小表合并宽表的操作

package com.training.connect2kafkainputsource;


import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.ConnectedStreams;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.co.CoProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.HashMap;
import java.util.Properties;


// kafka topic 接收的数据值是  "objectId 值"

//  使用 connect 合并两个 kafka 的数据源
// connect只能连接两个流

// 程序可以接受以下的输入参数
// --boot hadoop:9092
// -- topic1 flink_connect_1
// --topic2 flink_connect_2

// 由于程序的 subTask 的问题，所以必须设置程序的并行度为 1 才能够正确运行， 否则必须使用 keyBy 进行分组
// 编程套路:
// 1. 获取执行环境
// 2. 添加数据源
// 3. 进行 transformation
// 4. 进行数据的 sink 操作
// 5. 执行 Flink 程序
@Slf4j
public class Connect2KafkaInputDataSource {
    public static void main(String[] args) throws Exception {
        log.info("run:{}", Connect2KafkaInputDataSource.class.getName());
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
        env.setParallelism(1); // 设置最大并行度

        // 构造两个数据源
        FlinkKafkaConsumer010<String> input1 = new FlinkKafkaConsumer010<String>(topic1, new SimpleStringSchema(), props);
        FlinkKafkaConsumer010<String> input2 = new FlinkKafkaConsumer010<String>(topic2, new SimpleStringSchema(), props);


        // 添加数据源
        DataStreamSource<String> source1 = env.addSource(input1);
        DataStreamSource<String> source2 = env.addSource(input2);


        // 使用 connect 合并两个数据源
        ConnectedStreams<String, String> connect = source1.connect(source2);

        // 进行数据的 transformation
        SingleOutputStreamOperator<String> process = connect.process(new CoProcessFunction<String, String, String>() {
            private HashMap<String, String> source1Map = new HashMap<String, String>();
            private HashMap<String, String> source2Map = new HashMap<String, String>();

            @Override
            public void processElement1(String input, Context ctx, Collector<String> out) throws Exception {
                log.info("processElement1:{}", input);
                String[] sps = input.split(" ");
                if (sps.length != 2) {
                    return;
                }
                source1Map.put(sps[0], sps[1]);
                String address = source2Map.get(sps[0]);
                if (address == null) {
                    return;
                }
                out.collect("id:" + sps[0] + ", name:" + sps[1] + ", address:" + address);
            }

            @Override
            public void processElement2(String input, Context ctx, Collector<String> out) throws Exception {
                log.info("processElement2:{}", input);

                String[] sps = input.split(" ");
                if (sps.length != 2) {
                    return;
                }
                source2Map.put(sps[0], sps[1]);
                String name = source1Map.get(sps[0]);
                if (name == null) {
                    return;
                }
                out.collect("id:" + sps[0] + ", address:" + sps[1] + ", name:" + name);
            }
        });

        // 进行 sink 操作
        process.print();

        // 执行 Flink 程序
        env.execute(Connect2KafkaInputDataSource.class.getName());
    }
}
