package com.training.window;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.datastream.WindowedStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Date;
import java.util.Iterator;
import java.util.Random;

// 统计一段时间内的窗口数据
// 数据可能有重合
// 窗口的长度固定
//
@Slf4j
public class WindowSlidingProcessFunction {
    public static void main(String[] args) throws Exception {
        log.info("start: {}", WindowSlidingProcessFunction.class.getName());
        // 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        // 设置全局禁止共享 slot
        env.disableOperatorChaining();
        // 设置事件的时间处理类型
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        // 设置 数据源
        DataStreamSource<WindowSlidingProcessFunctionData> source = env.addSource(new WindowSlidingSource());
        // 从事件中提取 event time
        SingleOutputStreamOperator<WindowSlidingProcessFunctionData> extraWatermark = source.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<WindowSlidingProcessFunctionData>() {
            @Override
            public long extractAscendingTimestamp(WindowSlidingProcessFunctionData element) {
                return element.eventTime;
            }
        });
        //  keyed stream
        KeyedStream<WindowSlidingProcessFunctionData, String> keyBy = extraWatermark.keyBy(new KeySelector<WindowSlidingProcessFunctionData, String>() {
            @Override
            public String getKey(WindowSlidingProcessFunctionData value) throws Exception {
                return value.data;
            }
        });

        // 触发窗口运算
        // 窗口大小是 10 秒钟
        // 每 5 秒中触发一次运算
        WindowedStream<WindowSlidingProcessFunctionData, String, TimeWindow> window = keyBy.window(SlidingEventTimeWindows.of(Time.seconds(10), Time.seconds(5)));

        // 窗口处理
        SingleOutputStreamOperator<WindowSlidingProcessFunctionData> process = window.process(new ProcessWindowFunction<WindowSlidingProcessFunctionData, WindowSlidingProcessFunctionData, String, TimeWindow>() {
            @Override
            public void process(String s, Context context, Iterable<WindowSlidingProcessFunctionData> elements, Collector<WindowSlidingProcessFunctionData> out) throws Exception {
                System.out.println("key: " + s + " " + context.currentWatermark());
                Iterator<WindowSlidingProcessFunctionData> iterator = elements.iterator();
                for (; iterator.hasNext(); ) {
                    WindowSlidingProcessFunctionData next = iterator.next();
                    out.collect(next);
                    System.out.println(next);
                }
            }
        });
        process.print();

        // 执行 Flink 程序
        env.execute();

    }

    public static class WindowSlidingProcessFunctionData {
        public String data = "";
        public long eventTime = 0;
        public int sn = 0;

        @Override
        public String toString() {
            return "WindowSlidingProcessFunctionData{" +
                    "data='" + data + '\'' +
                    ", eventTime=" + eventTime +
                    ", sn=" + sn +
                    '}';
        }


    }

    public static class WindowSlidingSource implements SourceFunction<WindowSlidingProcessFunctionData> {

        private int sn = 0;
        private boolean iscancel = false;

        @Override
        public void run(SourceContext<WindowSlidingProcessFunctionData> ctx) throws Exception {
            while (!iscancel) {
                WindowSlidingProcessFunctionData record = new WindowSlidingProcessFunctionData();
                record.data = "foo.baz:" + new Random(System.currentTimeMillis()).nextInt(3);
                record.sn = sn++;
                record.eventTime = System.currentTimeMillis();
                Thread.sleep(1000);
                if (sn % 5 == 0) {
                    Thread.sleep(2000);
                }
                ctx.collect(record);
            }
        }

        @Override
        public void cancel() {
            iscancel = true;
            System.out.println("is cancel " + new Date());
        }
    }
}
