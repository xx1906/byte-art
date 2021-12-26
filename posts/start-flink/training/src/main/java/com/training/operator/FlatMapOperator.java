package com.training.operator;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.RichFlatMapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

@Slf4j
public class FlatMapOperator {
    public static void main(String[] args) throws Exception {
        log.info("starting...{}", new Date());
        ParameterTool params = ParameterTool.fromArgs(args);
        log.info("params {}", params.toMap().entrySet());

        String hostname = params.get("hostname", "hadoop1");
        int port = params.getInt("port", 6666);

        log.info("hostname:{}", hostname);
        log.info("port:{}", port);

        // 1. 获取运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2. 添加数据源
        DataStreamSource<String> socket = env.socketTextStream(hostname, port, "\n");

        // 3. operator: 使用 flat map 算子， 将从 socket 的数据切分成多个输出
        SingleOutputStreamOperator<String> flatMap = socket.flatMap(new MyFlatMap());

        // 4. sink: 将切分的元素输出
        flatMap.print();

        // 5. 运行 flink 程序
        env.execute("flat_map_operator");
    }
}


/**
 * 将输入的元素， 按照空格切分成多个输出， 该类实现 RichFlatMapFunction
 * RichFlatMapFunction 这个类里面可以使用 flink runtime 相关的函数
 */
class MyFlatMap extends RichFlatMapFunction<String, String> {

    private static final Logger log = LoggerFactory.getLogger(MyFlatMap.class);

    public MyFlatMap() {
        super();
        log.info("constructor:{}", MyFlatMap.class.getName());
    }

    @Override
    public void flatMap(String input, Collector<String> out) throws Exception {
        System.out.println(input);
        for (String s : input.split(" ")) {
            out.collect("" + getRuntimeContext().getIndexOfThisSubtask() + ":" + s);
        }
    }
}
