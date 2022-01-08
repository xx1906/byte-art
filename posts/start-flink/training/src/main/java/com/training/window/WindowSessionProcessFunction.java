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
import org.apache.flink.streaming.api.windowing.assigners.EventTimeSessionWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import java.util.Date;
import java.util.Iterator;

@Slf4j
public class WindowSessionProcessFunction {
    public static void main(String[] args) throws Exception {
        log.info("data: {}", WindowSessionProcessFunction.class.getName());

        // 执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        // 设置禁止共享槽
        env.disableOperatorChaining();
        // 设置对于时间的处理方式
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);
        // 添加数据源
        DataStreamSource<WindowSessionProcessFunctionData> source = env.addSource(new WindowSessionSource());
        // 提取 水位线
        SingleOutputStreamOperator<WindowSessionProcessFunctionData> assingWatermarkSource = source.assignTimestampsAndWatermarks(new AscendingTimestampExtractor<WindowSessionProcessFunctionData>() {
            @Override
            public long extractAscendingTimestamp(WindowSessionProcessFunctionData element) {
                return element.eventTime;
            }
        });
        // 按照 key 分组
        KeyedStream<WindowSessionProcessFunctionData, String> keyBy = assingWatermarkSource.keyBy(new KeySelector<WindowSessionProcessFunctionData, String>() {
            @Override
            public String getKey(WindowSessionProcessFunctionData value) throws Exception {
                return value.data;
            }
        });

        // session window 这里固定是每 5 秒生成一个新的窗口
        //
        WindowedStream<WindowSessionProcessFunctionData, String, TimeWindow> sessionWindow = keyBy.window(EventTimeSessionWindows.withGap(Time.seconds(5)));

        // 进行窗口内的数据处理
        SingleOutputStreamOperator<WindowSessionProcessFunctionData> process = sessionWindow.process(
                new ProcessWindowFunction<WindowSessionProcessFunctionData, WindowSessionProcessFunctionData, String, TimeWindow>() {
                    @Override
                    public void process(String key, Context context, Iterable<WindowSessionProcessFunctionData> elements, Collector<WindowSessionProcessFunctionData> out) throws Exception {
                        System.out.println("key is " + key);
                        long watermark = context.currentWatermark();
                        Iterator<WindowSessionProcessFunctionData> iterator = elements.iterator();
                        for (; iterator.hasNext(); ) {
                            WindowSessionProcessFunctionData next = iterator.next();
                            System.out.println(getRuntimeContext().getIndexOfThisSubtask() + ">" + "watermark:" + watermark + ", " + next);
                        }
                    }
                });

        process.print();

        // 执行 Flink 程序
        env.execute();
    }

    public static class WindowSessionProcessFunctionData {
        public String data = "";
        public long eventTime = 0;
        public int sn = 0;

        @Override
        public String toString() {
            return "WindowSessionProcessFunctionData{" +
                    "data='" + data + '\'' +
                    ", eventTime=" + eventTime +
                    ", sn=" + sn +
                    '}';
        }
    }

    public static class WindowSessionSource implements SourceFunction<WindowSessionProcessFunctionData> {
        private int sn = 0;
        private boolean isCancel = false;

        @Override
        public void run(SourceContext<WindowSessionProcessFunctionData> ctx) throws Exception {
            while (!isCancel) {
                WindowSessionProcessFunctionData record = new WindowSessionProcessFunctionData();
                if (sn % 5 == 0) {
                    Thread.sleep(5000);
                } else {
                    Thread.sleep(1000);
                }
                record.sn = sn++;
                record.data = "foo.baz";
                record.eventTime = System.currentTimeMillis();
                ctx.collect(record);
            }
        }

        @Override
        public void cancel() {
            isCancel = true;
            System.out.println("cancel : " + new Date());
        }
    }
}
