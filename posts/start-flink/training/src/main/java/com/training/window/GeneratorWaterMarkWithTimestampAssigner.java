package com.training.window;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.AssignerWithPeriodicWatermarks;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.watermark.Watermark;

import java.util.Date;

@Slf4j
public class GeneratorWaterMarkWithTimestampAssigner {
    public static void main(String[] args) throws Exception {
        log.info("data:{}", GeneratorWaterMarkWithTimestampAssigner.class.getName());

        // 1.获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());

        env.disableOperatorChaining();
        // 设置自动设置 watermark 的时间间隔
        env.getConfig().setAutoWatermarkInterval(1000);

        // 设置时间的间隔
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        // 2. 添加数据源
        DataStreamSource<SymbolRecord> source = env.addSource(new SourceFunction<SymbolRecord>() {
            private Boolean isCancel = false;

            @Override
            public void run(SourceContext<SymbolRecord> ctx) throws Exception {
                while (!isCancel) {
                    // 构造数据
                    SymbolRecord record = new SymbolRecord();
                    record.data = "hello world";
                    record.eventTime = System.currentTimeMillis();
                    //发送数据
                    ctx.collect(record);

                    // source 睡眠
                    Thread.sleep(1000);
                }
            }

            @Override
            public void cancel() {
                isCancel = true;
                System.out.println("cancel " + new Date().toString());
            }
        });

        // 配置基于时间的 water mark
        SingleOutputStreamOperator<SymbolRecord> symbolRecordSingleOutputStreamOperator = source.assignTimestampsAndWatermarks(new AssignerWithPeriodicWatermarks<SymbolRecord>() {
            private long waterMarkTime;

            @Override
            public Watermark getCurrentWatermark() {
                return new Watermark(waterMarkTime);
            }

            @Override
            public long extractTimestamp(SymbolRecord element, long previousElementTimestamp) {
                long boundOfWatermark = 100000;
                waterMarkTime = element.eventTime - boundOfWatermark;
                return element.eventTime;
            }
        });

        // 4. sink
        symbolRecordSingleOutputStreamOperator.print();


        // 5. 执行程序
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
