package com.training.window;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;


@Slf4j
public class GeneratorWaterMarkInSource {
    public static void main(String[] args) throws Exception {
        log.info("data:{}", GeneratorWaterMarkInSource.class.getName());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        // 设置事件对于时间的处理类型, 默认是 processingTime
        // 这里设定为 EventTime
        //
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        DataStreamSource<EventBaz> source = env.addSource(new SourceFunction<EventBaz>() {
            private Boolean isCancel = false;

            @Override
            public void run(SourceContext<EventBaz> ctx) throws Exception {
                while (!isCancel) {
                    long timeStamp = System.currentTimeMillis();
                    EventBaz eventBaz = new EventBaz();
                    eventBaz.event = "test";
                    eventBaz.eventTime = timeStamp;
                    // 发送元素
                    ctx.collectWithTimestamp(eventBaz, eventBaz.eventTime);
                    // 发送 时间水位线
                    ctx.emitWatermark(new Watermark(eventBaz.eventTime - 1000));
                    Thread.sleep(1000);
                }
            }

            @Override
            public void cancel() {
                System.out.println("isCancel " + System.currentTimeMillis());
                isCancel = true;
            }
        });


        source.print();

        env.execute();


    }

    public static class EventBaz {
        public long eventTime;
        public String event;

        @Override
        public String toString() {

            return "EventBaz{" +
                    "eventTime=" + eventTime +
                    ", event='" + event + '\'' +
                    '}';
        }
    }
}
