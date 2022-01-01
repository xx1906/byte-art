// 分流器的简单样例
package com.training.sideoutput;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.streaming.api.functions.sink.SinkFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
public class MySideOutput {
    // 分流器
    final static OutputTag<String> sideOutput = new OutputTag<String>(MySideOutput.class.getName()) {
    };
    final static String sideOutputName = "foo.baz";

    public static void main(String[] args) throws Exception {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        ConcurrentLinkedQueue<String> col = new ConcurrentLinkedQueue<String>();
        col.add("hello");
        col.add("world");
        col.add("foo.baz");
        col.add("foo.baz2");
        col.add("foo.baz233");
        DataStreamSource<String> source = env.fromCollection(col);
        SingleOutputStreamOperator<String> process = source.process(new ProcessFunction<String, String>() {
            @Override
            public void processElement(String value, Context ctx, Collector<String> out) throws Exception {
                if (value.contains(sideOutputName)) {
                    ctx.output(sideOutput, "side.output.tag:" + value);
                }
                out.collect(value);
            }
        });

        // 获取分流器的流
        process.getSideOutput(sideOutput).addSink(new SinkFunction<String>() {
            @Override
            public void invoke(String value, Context context) throws Exception {
                System.out.println(context.currentProcessingTime() + value);
            }
        });

        // 正常的数据
        process.addSink(new SinkFunction<String>() {
            @Override
            public void invoke(String value, Context context) throws Exception {
                System.out.println(value);
            }
        });

        env.execute(MySideOutput.class.getName());
    }
}
