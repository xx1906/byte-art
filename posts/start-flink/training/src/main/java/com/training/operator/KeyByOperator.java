package com.training.operator;

import akka.routing.MurmurHash;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.functions.RuntimeContext;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;

import java.util.Date;

/**
 * 这个程序使用 FlatMapProcessFunction 对输入的数据进行按照空格切分处理，将输入的数据进行多个分组处理
 * 然后对分组的元素进行 元组化， 最后对元组化的数据进行 keyBy 分组
 * <p>
 * 可配置化:
 * <p>
 * hostname 监听的主机
 * port  监听主机的端口
 */
@Slf4j
public class KeyByOperator {
    public static void main(String[] args) throws Exception {
        log.info("starting... {}", new Date());
        ParameterTool params = ParameterTool.fromArgs(args);
        String hostname = params.get("hostname", "hadoop1");
        int port = params.getInt("port", 6666);
        log.info("config:{}", params.toMap().entrySet());

        // 1. 获取 flink 运行时华宁
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // 2. 添加数据源
        DataStreamSource<String> socket = env.socketTextStream(hostname, port, "\n");

        // 3. 使用算子进行 transformation
        // flatMap 运算， 将输入的元素分组
        SingleOutputStreamOperator<String> flatMap = socket.process(new FlatMapOperatorWithProcessFunction());
        // 将分组后的元素进行 Tuple2 的类型转换
        SingleOutputStreamOperator<Tuple2<String, Integer>> map = flatMap.map(new MapOperatorWithRichMapFunction());
        // keyBy 运算， 这里进行两个 keyBy 操作
        KeyedStream<Tuple2<String, Integer>, Integer> keyBy = map.keyBy(new KeyByOperator2()).keyBy(new KeyBySelector3Int());

        // 4. 进行数据的 sink
        keyBy.print();

        // 5. 运行 flink 程序
        env.execute("keyBy_operator");
    }
}

/**
 * 第一个泛型参数是输入参数的类型
 * 第二个泛型参数是输出参数的类型
 *
 * @description 对第一个参数进行分组， 分组之后的类型是 第二个
 */
@Slf4j
class FlatMapOperatorWithProcessFunction extends ProcessFunction<String, String> {
    /**
     * @param input 输入数据
     * @param ctx   运行上下文环境
     * @param out   输出的数据
     * @throws Exception
     */
    @Override
    public void processElement(String input, Context ctx, Collector<String> out) throws Exception {
        RuntimeContext runtimeContext = getRuntimeContext();
        System.out.println("runtime:" + runtimeContext.getTaskName() + ", subTask:" + runtimeContext.getIndexOfThisSubtask() + ", input:" + input);
        for (String s : input.split(" ")) {
            out.collect(s);
        }
    }
}

/**
 * 对输入的数据进行元组化
 */
@Slf4j
class MapOperatorWithRichMapFunction extends RichMapFunction<String, Tuple2<String, Integer>> {
    /**
     * @param input 输入参数
     * @return
     */
    @Override
    public Tuple2<String, Integer> map(String input) {
        log.info("mapOperator.map:" + getRuntimeContext().getTaskName() + ", subTask:" + getRuntimeContext().getIndexOfThisSubtask());
        return Tuple2.of(input, 1);
    }
}

// 第一个参数是输入参数
// 第二个是 keyBy 的数据类型
@Slf4j
class KeyByOperator2 implements KeySelector<Tuple2<String, Integer>, String> {
    /**
     * @param input 输入参数
     * @return 返回的是 输入参数的第一个元素
     */
    @Override
    public String getKey(Tuple2<String, Integer> input) {
        log.info("getKey.input:{}", input);
        return input.f0;
    }
}

// KeyBySelector 的实现
// 这里选择的输入参数是 Tuple2<String,Integer>
// 选择的 Key 是 Integer
class KeyBySelector3Int implements KeySelector<Tuple2<String, Integer>, Integer> {

    @Override
    public Integer getKey(Tuple2<String, Integer> value) {
        // 使用 murmurhash 将输入的参数的第一个值进行 hash 计算
        return MurmurHash.stringHash(value.f0);
    }
}