package com.training.socketwordcountprocess;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.source.SocketTextStreamFunction;
import org.apache.flink.util.Collector;


@Slf4j
@Data
public class SocketWordCount {
    public static void main(String[] args) throws Exception {
        log.info("Socket Word Count process");
        String hostname = "hadoop1";
        int port = 6667;

        ParameterTool param = ParameterTool.fromArgs(args);

        if (param.has("hostname")) {
            hostname = param.get("hostname");
        }

        port = param.getInt("port", port);

        log.info("hostname: {}", hostname);
        log.info("port:{}", port);

        // flink 程序编程模型
        //1. 设置运行环境
        //2. 添加数据源
        //3. 进行 transformation
        //4. 数据 sink
        //5. 执行 flink 程序

        /// 设置运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 构造数据源 添加数据源
        SocketTextStreamFunction dataSource = new SocketTextStreamFunction(hostname, port, "\n", 6);
        // 添加数据源
        DataStreamSource<String> socketSrc = env.addSource(dataSource);


        // 进行算子的转换 operator transformation

        // 将输出入的数据分组
        SingleOutputStreamOperator<String> flatMap = socketSrc.flatMap(new FlatMapFunction<String, String>() {
            /**
             *
             * @param input 输入参数
             * @param out 输出参数， 这里是将 输入的数据按照空格切分，然后塞进一个集合
             * @throws Exception
             */
            @Override
            public void flatMap(String input, Collector<String> out) throws Exception {

                String[] sps = input.split(" ");

                for (String s : sps) {
                    out.collect(s);
                }
            }
        });

        /**
         *  将集合输入的元素按照， 单词转成元组，每个元组的第一个元素是单词本身， 第二个元素是单词的个数
         */
//        SingleOutputStreamOperator<Tuple2<String, Integer>> map = flatMap.map(new MapFunction<String, Tuple2<String, Integer>>() {
//            @Override
//            public Tuple2<String, Integer> map(String input) throws Exception {
//                return Tuple2.of(input, 1);
//            }
//        });
        // 使用 process Function 来完成输入的分组
        SingleOutputStreamOperator<Tuple2<String, Integer>> map = flatMap.process(new ProcessFunction<String, Tuple2<String, Integer>>() {
            @Override
            public void processElement(String input, Context ctx, Collector<Tuple2<String, Integer>> out) throws Exception {
                log.info("subTask:{}", getRuntimeContext().getIndexOfThisSubtask());
                out.collect(Tuple2.of(input, 1));
            }
        });

        // 将单词按照第一个元素进行分组
        KeyedStream<Tuple2<String, Integer>, Tuple> keyBy = map.keyBy(0);
        // 将分组之后的元组求和， 按照第二个字段进行求和
        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = keyBy.sum(1);


        // sink
        sum.print();
        // 执行 flink 程序
        env.execute(SocketWordCount.class.getName());
    }
}
