package com.training.window;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.watermark.Watermark;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Date;
import java.util.Iterator;

@Slf4j
public class WindowTumblingProcessFunction {
    public static void main(String[] args) throws Exception {
        log.info("data:{}", WindowTumblingProcessFunction.class.getName());
        // 获取运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        // 设置并行度为 3
        env.setParallelism(3);
        // 设定根据事件的发生时间来进行处理
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        // 添加数据源
        DataStreamSource<WindowTumblingData> dataDataStreamSource = env.addSource(new WindowTumblingProcessFunctionSource());
        // 提取水位线
        SingleOutputStreamOperator<WindowTumblingData> timestampsAndWatermarks = dataDataStreamSource.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<WindowTumblingData>() {
            @Override
            public long extractAscendingTimestamp(WindowTumblingData element) {
                return element.eventTime;
            }
        });
        // 按照 key 进行分组
        KeyedStream<WindowTumblingData, String> keyedStream = timestampsAndWatermarks.keyBy(new KeySelector<WindowTumblingData, String>() {
            @Override
            public String getKey(WindowTumblingData value) throws Exception {
                return value.key;
            }
        });

        // 分配窗口大小 TumblingEventTimeWindows
        keyedStream.window(TumblingEventTimeWindows.of(Time.seconds(4)))
                // 进行窗口内数据处理
                .process(new ProcessWindowFunction<WindowTumblingData, WindowTumblingData, String, TimeWindow>() {
                    @Override
                    public void process(String key, Context context, Iterable<WindowTumblingData> elements, Collector<WindowTumblingData> out) throws Exception {
                        System.out.println("key " + key);
                        int windowDataSum = 0;
                        Iterator<WindowTumblingData> iterator = elements.iterator();
                        for (; iterator.hasNext(); ) {
                            WindowTumblingData next = iterator.next();
                            System.out.println(next);
                            windowDataSum += next.sn;
                            //out.collect(next);
                        }
                        System.out.println("统计窗口内 sn 的和: " + windowDataSum);
                    }
                }).print();


        env.execute();


    }

    public static class WindowTumblingData {
        public int sn;
        public long eventTime;
        public String key = "";

        @Override
        public String toString() {
            return JSON.toJSONString(this);
        }
    }

    public static class WindowTumblingProcessFunctionSource implements SourceFunction<WindowTumblingData> {
        private Boolean isCancel = false;
        private int sn = 0;

        @Override
        public void run(SourceContext<WindowTumblingData> ctx) throws Exception {
            while (!isCancel) {
                WindowTumblingData record = new WindowTumblingData();
                record.eventTime = System.currentTimeMillis();
                record.sn = this.sn++;
                record.key = "demo";
                ctx.collect(record);
                Thread.sleep(1000);

            }
        }

        @Override
        public void cancel() {
            isCancel = true;
            System.out.println("cancel : " + new Date().toString());
//            System.out.println();
        }
    }
}
