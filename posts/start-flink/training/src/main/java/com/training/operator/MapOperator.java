package com.training.operator;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.JobExecutionResult;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.util.Random;

@Slf4j
public class MapOperator {
    public static void main(String[] args) throws Exception {
        int version = new Random(49).nextInt();
        ParameterTool params = ParameterTool.fromArgs(args);
        log.info("params: {}", params.toMap().entrySet());
        if (params.has("version")) {
            log.info("version:{}", version);
            System.out.println("version:" + version);
            return;
        }

        String hostname = params.get("hostname", "hadoop1");
        int port = params.getInt("port", 6666);

        //1. 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        //2. 添加数据源
        DataStreamSource<String> socket = env.socketTextStream(hostname, port, "\n");

        // 3. 添加算子 4. 执行 sink 操作
        socket.map(new MyMapOperator()).print();
        // 5. 执行 flink 程序
        JobExecutionResult job = env.execute("map_operator");
        System.out.println("executePlain:" + env.getExecutionPlan());
        log.info("runtime_name:{}", job.getNetRuntime());
    }
}


// 将输入的数据转成一个元组
@Slf4j
class MyMapOperator implements MapFunction<String, Tuple2<String, Integer>> {
    @Override
    public Tuple2<String, Integer> map(String value) throws Exception {
        log.debug("input:{}", value);
        return Tuple2.of(value, 1);
    }
}
