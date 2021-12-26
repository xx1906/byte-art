package com.traing.wordcount.simplewordcount;
/**
 * 简单版本的 flink 程序， 入门使用
 */

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.ExecutionConfig;
import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SocketTextStreamFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.api.java.utils.ParameterTool;


@Slf4j
@Data
public class SimpleWordCount {

    public static void main(String[] args) throws Exception {
        ParameterTool parameters = ParameterTool.fromArgs(args);
        String hostname = "hadoop1";
        int port = 6666;

        log.info("simple word count");

        hostname = parameters.get("hostname");
        port = parameters.getInt("port");
        log.info("hostname:{}", hostname);
        log.info("port:{}", port);

        // 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        ExecutionConfig config = env.getConfig();
        log.info("config {}", config);

        // 构造数据源
        SocketTextStreamFunction socketSource = new SocketTextStreamFunction(hostname, port, "\n", 6);

        // 添加数据源
        DataStreamSource<String> source = env.addSource(socketSource);

        // 增加数据过滤器 process function
        SingleOutputStreamOperator<String> filter = source.filter(new WordFilter());
        // filter.slotSharingGroup("filter_input"); // 设置槽的名称

        // 增加单词分组 process function
        SingleOutputStreamOperator<String> flatMap = filter.flatMap(new WordFlatMap());
        flatMap.setParallelism(3); // 设置并行度

        // 将分组转成元组 process function
        SingleOutputStreamOperator<Tuple2<String, Integer>> map = flatMap.map(new WordMap());
        map.setParallelism(2); // 设置小的并行度

        // 将元组按照下标分组的 process function
        KeyedStream<Tuple2<String, Integer>, Tuple> keyBy = map.keyBy(0);

        // 将分组之后的数据，按照位置求和
        SingleOutputStreamOperator<Tuple2<String, Integer>> sum = keyBy.sum(1);

        // 执行 data sink
        sum.print();

        // 打印执行计划
        log.info("ExecutionPlan:{}", env.getExecutionPlan());

        // 执行 flink 程序
        env.execute("simple_word_count");
    }
}

/**
 * 将输入的单词过滤
 */
@Slf4j
class WordFilter implements FilterFunction<String> {
    public WordFilter() {
        log.info("com.traing.wordcount.simplewordcount.WordFilter build");
    }

    @Override
    public boolean filter(String value) throws Exception {
        return true;
    }
}

/**
 * 根据输入的单词执行切割操作，完成之后， 将单词分组
 */
@Slf4j
class WordFlatMap implements FlatMapFunction<String, String> {
    private String regex = " ";

    public WordFlatMap(String regex) {
        this.regex = regex;
    }

    public WordFlatMap() {

    }

    /**
     * @param input input 是上一个算子传进来的值
     * @param out   是当前算子处理之后，输出的值
     * @throws Exception
     */
    @Override
    public void flatMap(String input, Collector<String> out) throws Exception {
        String[] sps = input.split(this.regex);
        for (String s : sps) {
            out.collect(s);
        }
    }
}


/**
 * wordMap 是将分组后的单词转成元组
 */
@Slf4j
class WordMap implements MapFunction<String, Tuple2<String, Integer>> {
    /**
     * @param input 传入给当前算子处理的值
     * @return 返回值是，当前算子处理之后输出的值
     * @throws Exception
     */
    @Override
    public Tuple2<String, Integer> map(String input) throws Exception {
        return Tuple2.of(input, 1);
    }
}