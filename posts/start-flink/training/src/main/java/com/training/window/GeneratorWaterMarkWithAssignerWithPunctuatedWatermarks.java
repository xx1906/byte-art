package com.training.window;


import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPunctuatedWatermarks;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

@Slf4j
public class GeneratorWaterMarkWithAssignerWithPunctuatedWatermarks {
    public static void main(String[] args) throws Exception {
        log.info("data {}", GeneratorWaterMarkWithAssignerWithPunctuatedWatermarks.class.getName());

        // 1. 获取运行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        // 设置　事件处理时间　
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        // 2. 添加 Flink 事件源
        SingleOutputStreamOperator<SymbolEvent> symbolEventSingleOutputStreamOperator = env.addSource(new SourceFunction<SymbolEvent>() {
            private Boolean isCancel = false;
            private ArrayList<String> array = new ArrayList<>();

            @Override
            public void run(SourceContext<SymbolEvent> ctx) throws Exception {
                for (int i = 0; i < 10; i++) {
                    array.add(String.valueOf(i));
                }
                while (!isCancel) {
                    SymbolEvent record = new SymbolEvent();
                    record.data = "foo baz" + array.get(new Random(System.currentTimeMillis()).nextInt(array.size()));
                    record.eventTime = System.currentTimeMillis();
                    ctx.collect(record);
                    Thread.sleep(1000);
                }
            }

            @Override
            public void cancel() {
                isCancel = true;
                System.out.println("cancel:" + new Date().toString());
            }

            // 根据事件发送 watermark
        }).assignTimestampsAndWatermarks(new AssignerWithPunctuatedWatermarks<SymbolEvent>() {

            // 抽取事件的时间戳
            @Override
            public long extractTimestamp(SymbolEvent element, long previousElementTimestamp) {
                log.info("extraTimestamp: {}, previousElementTimestamp:{}", element.eventTime, previousElementTimestamp);
                return element.eventTime;
            }

            // 根据事件生成水位线
            @Override
            public Watermark checkAndGetNextWatermark(SymbolEvent lastElement, long extractedTimestamp) {
                if (lastElement.data.contains("9")) {
                    return new Watermark(extractedTimestamp);
                }
                return null;
            }
        });


        // 4. sink
        symbolEventSingleOutputStreamOperator.print();

        // 5. 运行 flink 程序
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
