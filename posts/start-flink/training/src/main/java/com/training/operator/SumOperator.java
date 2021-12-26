package com.training.operator;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Date;

@Slf4j
public class SumOperator {
    public static void main(String[] args) throws Exception {
        log.info("starting...{}", new Date());

        ParameterTool params = ParameterTool.fromArgs(args);
        log.info("params:{}", params.toMap().entrySet());
        String hostname = params.get("hostname", "hadoop1");
        int port = params.getInt("port", 6666);
        log.info("hostname:{}", hostname);
        log.info("port:{}", port);

        // 1. 创建 Flink 运行时环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2. 设置数据源
        DataStreamSource<String> socket = env.socketTextStream(hostname, port, "\n");

        // 3. 进行数据的 transformations

        // 将输入的字符串按照空格进行切分， 然后输出一个 string 列表
        SingleOutputStreamOperator<String> flatMap = socket.process(new FlatMapOperatorWithProcessFunction());

        // 将 flatMap 输出的列表转成元组， Typle2<String,Integer>
        SingleOutputStreamOperator<Tuple2<String, Integer>> map = flatMap.map(new MapOperatorWithRichMapFunction());

        // 将 元组按照 key 的 hash 进行 keyBy
        KeyedStream<Tuple2<String, Integer>, Integer> keyBy = map.keyBy(new KeyBySelector3Int());

        // 根据指定的字段进行 sum
        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = keyBy.sum(1);

        // 4. 数据 sink
        sum.print();

        // 5. 运行 Flink 程序
        env.execute("sum_operator");
    }
}
