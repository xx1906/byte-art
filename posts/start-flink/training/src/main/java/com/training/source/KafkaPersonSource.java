package com.training.source;

import com.training.entity.Person;
import com.training.entity.PersonSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringSerializer;


import java.util.Properties;

@Slf4j
public class KafkaPersonSource {
    public static void main(String[] args) throws Exception {
        ParameterTool params = ParameterTool.fromArgs(args);
        String endpoint = params.get("endpoint", "hadoop1:9092");
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, endpoint);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "flink_consumer");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "flink_consumer_client");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringSerializer.class);
        log.info("props:{}", props);

        FlinkKafkaConsumer010<Person> consumer = new FlinkKafkaConsumer010<Person>("flink_person", new PersonSchema(), props);
        consumer.setStartFromLatest();

        DataStreamSource<Person> source = env.addSource(consumer);
        env.getConfig().setAutoWatermarkInterval(100);


        KeyedStream<Person, String> keyBy = source.keyBy((KeySelector<Person, String>) Person::getId);
        keyBy.timeWindowAll(Time.seconds(1)).process(new ProcessAllWindowFunction<Person, Person, TimeWindow>() {
            @Override
            public void process(Context context, Iterable<Person> elements, Collector<Person> out) throws Exception {
                for (Person p : elements) {
                    out.collect(p);
                }
            }
        }).process(new ProcessFunction<Person, Person>() {
            @Override
            public void processElement(Person value, Context ctx, Collector<Person> out) throws Exception {
                RuntimeContext runtimeContext = getRuntimeContext();
                StringBuilder sb = new StringBuilder("std.out.sink:");
                for (int i = 0; i <= runtimeContext.getIndexOfThisSubtask(); i++) {
                    sb.append("=");
                }
                sb.append(runtimeContext.getIndexOfThisSubtask() + 1).append(">").append(value.toString());
                System.out.println( sb.toString());
            }
        });

        env.execute("flink_kafka_consumer");


    }
}
