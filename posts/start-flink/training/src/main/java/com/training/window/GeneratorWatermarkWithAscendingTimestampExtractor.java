package com.training.window;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;

import java.util.Date;

// 使用升序的 自动生成 watermark 的方法
@Slf4j
public class GeneratorWatermarkWithAscendingTimestampExtractor {
    public static void main(String[] args) throws Exception {
        log.info("data:{}", GeneratorWatermarkWithAscendingTimestampExtractor.class.getName());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        SingleOutputStreamOperator<SymbolEvent> symbolEventSingleOutputStreamOperator = env.addSource(new SourceFunction<SymbolEvent>() {
            private Boolean isCancel = false;
            final long second = 1000;

            @Override
            public void run(SourceContext<SymbolEvent> ctx) throws Exception {
                while (!isCancel) {
                    SymbolEvent event = new SymbolEvent();
                    event.data = "foo. baz";
                    event.eventTime = System.currentTimeMillis();
                    ctx.collect(event);
                    Thread.sleep(second);
                }
            }

            @Override
            public void cancel() {
                isCancel = true;
                System.out.println("cancel:{}" + new Date().toString());
            }

            // (升序)使用现成的方法发送 watermark
        }).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<SymbolEvent>() {
            @Override
            public long extractAscendingTimestamp(SymbolEvent element) {
                log.info("data:{}",element);
                return element.eventTime - 1000;
            }
        });

        symbolEventSingleOutputStreamOperator.print();


        env.execute();
    }

    public static class SymbolEvent {
        public long eventTime;
        public String data;

        @Override
        public String toString() {
            return "SymbolEvent{" +
                    "eventTime=" + eventTime +
                    ", data='" + data + '\'' +
                    '}';
        }
    }
}
