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
 * **由于这个程序中没有使用 keyBy 和分区，为了算子能够正确运行，这里只能将程序的并行度设置为 1**
 */
package com.training.transformation;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
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
public class ConnectTransformation {

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
        // 这里必须设置并行度为 1 才能让程序正确运行
        // 原因是: 由于 每个算子可能由多个 subTask， 在没有使用 keyBy 进行分组的情况下， 相同 id 的数据可能发送到不同的 subTask, 这里没有使用
        // keyBy 将 数据按照 id 进行分组, 而是简单地设置 并行度为 1
        env.setParallelism(1);

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

        // 将字符串转成 id2Name 对象输出
        SingleOutputStreamOperator<id2Name> mapId2Name = filter1.map(new MapFunction<String, id2Name>() {
            @Override
            public id2Name map(String value) {
                String[] sps = value.split(" ");
                return new id2Name(sps[0], sps[1]);
            }
        });

        // 将 字符串转成 id2Job 对象输出
        SingleOutputStreamOperator<id2Job> mapId2Job = filter2.map((MapFunction<String, id2Job>) value -> {
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

        // 执行 Flink 的程序
        env.execute("connect_trans");
    }
}

// 这里的实现是没有 状态的 coProcessFunction
@Slf4j
class joinId2NameJobCoProcessFunctionWithoutState extends CoProcessFunction<id2Name, id2Job, idNameJob> {

    private HashMap<String, id2Name> srcMap1 = new HashMap<>(); // 保存的是 id:name
    private HashMap<String, id2Job> srcMap2 = new HashMap<>(); // 保存的是 id:job

    /**
     * @param input 输入的 id2Name 的映射字段
     * @param ctx   运行上下文
     * @param out   宽表对象
     * @throws Exception
     */
    @Override
    public void processElement1(id2Name input, Context ctx, Collector<idNameJob> out) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        srcMap1.put(input.getId(), input);
        id2Job job = srcMap2.get(input.getId()); // 尝试从 srcMap2 中获取 job 的值， 如果没有找到返回
        if (job == null) {
            return;
        }
        // 找到 job 的值, 合并成宽表输出
        out.collect(new idNameJob(input.getId(), input.getName(), job.getJob()));
        log.info("processElement1.subIndexTask:{}", runtimeContext.getIndexOfThisSubtask());
    }

    /**
     * @param input id2Job 的对象字段
     * @param ctx   上下文
     * @param out   宽表对象
     */
    @Override
    public void processElement2(id2Job input, Context ctx, Collector<idNameJob> out) {
        RuntimeContext runtimeContext = getRuntimeContext();
        srcMap2.put(input.getId(), input);
        // 尝试从 srcMap1 中获取 id2Name 的值
        id2Name name = srcMap1.get(input.getId());
        if (name == null) {
            return;
        }
        // 合并成宽表输出
        out.collect(new idNameJob(input.getId(), name.getName(), input.getJob()));
        log.info("processElement2.subIndexTask:{}", runtimeContext.getIndexOfThisSubtask());
    }
}


class id2Name {
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    id2Name(String id, String name) {
        this.id = id;
        this.name = name;
    }
}

class id2Job {
    private String id;
    private String job;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    id2Job(String id, String job) {
        this.id = id;
        this.job = job;
    }
}

class idNameJob implements SerializationSchema {

    private String id;
    private String name;
    private String job;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    idNameJob(String id, String name, String job) {
        this.id = id;
        this.name = name;
        this.job = job;
    }

    public idNameJob() {
    }

    @Override
    public String toString() {
        return "idNameJob{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", job='" + job + '\'' +
                '}';
    }

    @Override
    public byte[] serialize(Object element) {
        return element.toString().getBytes();
    }
}


