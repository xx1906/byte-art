package com.training.state;

import com.training.entity.Person;
import com.training.entity.PersonSchema;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.state.*;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.state.memory.MemoryStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.CheckpointConfig;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.KeyedProcessFunction;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.co.KeyedCoProcessFunction;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer010;
import org.apache.flink.util.Collector;
import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MapStateOperator {

    public static void main(String[] args) throws Exception {
        log.info("start " + new Date().toString());

        // 获取输入变量
        ParameterTool params = ParameterTool.fromArgs(args);
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, params.get("endpoint", "hadoop1:9092"));
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "flink_map_state");
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "flink_demo2");
        String topic = params.get("topic", "flink_person");

        log.info("props:{}", props);

        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
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
        //  env.setParallelism(1);
        MemoryStateBackend stateBackend = new MemoryStateBackend(10 * 1024 * 1024, false);
        env.setStateBackend(stateBackend);

        FlinkKafkaConsumer010<Person> personSource = new FlinkKafkaConsumer010<>(topic, new PersonSchema(), props);
        personSource.setStartFromLatest();
        DataStreamSource<Person> personStream = env.addSource(personSource);
        KeyedStream<Person, String> keyByStream = personStream.keyBy(new KeySelector<Person, String>() {
            @Override
            public String getKey(Person person) throws Exception {
                return person.getId() == null ? "" : person.getId();
            }
        });

        // keyByStream.print();

        SingleOutputStreamOperator<Person> process = keyByStream.process(new KeyedProcessFunction<String, Person, Person>() {
            @Override
            public void processElement(Person value, Context ctx, Collector<Person> out) throws Exception {
                mapOfPerson.put(value.getId(), value);

                Iterable<Map.Entry<String, Person>> entries = mapOfPerson.entries();
                if (entries != null) {
                    for (Map.Entry<String, Person> entry : entries) {
                        System.out.println("恢复数据 " + ctx.getCurrentKey() + "  " + getRuntimeContext().getIndexOfThisSubtask() + "  " + entry.toString());
                    }
                }
//                incr.update(incr.value() == null ? 0 : incr.value() + 1);
//                if (incr.value() % 10 == 0) {
//                    incr.update(incr.value() + 1);
//                    int a = 1 / 0; // 触发容错
//                }
                out.collect(value);
                // 触发程序异常
                if (new Random(new Date().getTime()).nextInt() % 10 == 0) {
                    int a = 1 / 0;
                }
            }

            private MapState<String, Person> mapOfPerson;

            @Override
            public void open(Configuration parameters) throws Exception {
                //super.open(parameters);
                StateTtlConfig ttoConfig = StateTtlConfig.newBuilder(Time.minutes(6))
                        .setUpdateType(StateTtlConfig.UpdateType.OnReadAndWrite)
                        .setStateVisibility(StateTtlConfig.StateVisibility.ReturnExpiredIfNotCleanedUp)
                        .setTtlTimeCharacteristic(StateTtlConfig.TtlTimeCharacteristic.ProcessingTime)
                        .build();

                MapStateDescriptor<String, Person> mapDesc = new MapStateDescriptor<String, Person>("map", TypeInformation.of(String.class), TypeInformation.of(Person.class));
                mapDesc.enableTimeToLive(ttoConfig);

                mapOfPerson = getRuntimeContext().getMapState(mapDesc);
//                Iterable<Map.Entry<String, Person>> entries = mapOfPerson.entries();
//                if (entries !=null) {
//                    for (Map.Entry<String, Person> entry : entries) {
//                        System.out.println("恢复数据 " + entry.toString());
//                    }
//                }

                // value state
//                ValueStateDescriptor<Integer> incrDesc = new ValueStateDescriptor<>("incr", TypeInformation.of(Integer.class));
//                incr = getRuntimeContext().getState(incrDesc);
//                System.out.println("容错恢复: "+incr.value());
//                incr.update(incr.value() == null?0: incr.value()+1);

            }


        });

        process.print();

        env.execute("map_state_of_person");
    }
}
