package com.training.window;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.BoundedOutOfOrdernessTimestampExtractor;
import org.apache.flink.streaming.api.windowing.time.Time;

import java.util.Date;
import java.util.Random;

// 无序事件发送的 watermark
@Slf4j
public class GeneratorWaterMarkBoundedOutOfOrdernessTimestampExtractor {
    public static void main(String[] args) throws Exception {
        log.info("data:{}", GeneratorWaterMarkBoundedOutOfOrdernessTimestampExtractor.class.getName());

        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        // 禁用优化
        env.disableOperatorChaining();
        // 设置为事件时间
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        SingleOutputStreamOperator<SymbolRecord> symbolRecordSingleOutputStreamOperator = env.addSource(new SourceFunction<SymbolRecord>() {

            private Boolean isCancel = false;
            private int sn = 0;

            @Override
            public void run(SourceContext<SymbolRecord> ctx) throws Exception {
                while (!isCancel) {
                    SymbolRecord record = new SymbolRecord();
                    sn++;
                    record.eventTime = System.currentTimeMillis() - new Random(System.currentTimeMillis()).nextInt(100000);
                    if (sn % 3 == 0) {
                        record.eventTime -= 10000;
                    }
                    record.data = "h:" + sn;
                    ctx.collect(record);
                    Thread.sleep(1000);
                }
            }

            @Override
            public void cancel() {
                isCancel = true;
                System.out.println("isCancel:" + new Date().toString());
            }

            // 实现无序事件的 watermark
        }).assignTimestampsAndWatermarks(new BoundedOutOfOrdernessTimestampExtractor<SymbolRecord>(Time.seconds(10)) {
            @Override
            public long extractTimestamp(SymbolRecord element) {

                System.out.println("element:" + element.toString() + ", " + getCurrentWatermark());
                return element.eventTime;
            }
        });

        symbolRecordSingleOutputStreamOperator.print();

        env.execute();
    }

    public static class SymbolRecord {
        public long eventTime = 0;
        public String data = "";

        @Override
        public String toString() {
            return "SymbolRecord{" +
                    "eventTime=" + eventTime +
                    ", data='" + data + '\'' +
                    '}';
        }
    }
}
