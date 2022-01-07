package com.training.window;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.typeinfo.TypeInfo;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.functions.KeySelector;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.TimeCharacteristic;
import org.apache.flink.streaming.api.datastream.KeyedStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SourceFunction;
import org.apache.flink.streaming.api.functions.timestamps.AscendingTimestampExtractor;
import org.apache.flink.streaming.api.functions.windowing.ProcessWindowFunction;
import org.apache.flink.streaming.api.windowing.assigners.TumblingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;
import org.apache.flink.util.OutputTag;

import java.util.Date;
import java.util.Iterator;
import java.util.Random;

// 处理延迟数据
@Slf4j
public class GeneratorWaterMarkBoundedOutOfOrdernessTimestampExtractorLatency {
    public static void main(String[] args) throws Exception {
        final OutputTag<EventBaz> late = new OutputTag<EventBaz>("late", TypeInformation.of(EventBaz.class)) {
        };
        log.info("data:{}", new Date().toString());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.createLocalEnvironmentWithWebUI(new Configuration());
        env.setStreamTimeCharacteristic(TimeCharacteristic.EventTime);

        env.setParallelism(1);

        SingleOutputStreamOperator<EventBaz> source = env.addSource(new SourceFunction<EventBaz>() {
            private Boolean isCancel = false;
            private int sn = 0;

            @Override
            public void run(SourceContext<EventBaz> ctx) throws Exception {
                while (!isCancel) {
                    sn++;
                    EventBaz record = new EventBaz();
                    record.eventTime = System.currentTimeMillis();
                    if (sn % 2 == 0) {
                        record.eventTime -= (4000);
                    }
                    record.event = "data: " + sn;
                    ctx.collect(record);
                    Thread.sleep(1000);
                }
            }

            @Override
            public void cancel() {
                isCancel = true;
                System.out.println("cancel");
            }
        }).assignTimestampsAndWatermarks(new AscendingTimestampExtractor<EventBaz>() {
            @Override
            public long extractAscendingTimestamp(EventBaz element) {
                return element.eventTime;
            }
        });
        KeyedStream<EventBaz, String> keyBy = source.keyBy(new KeySelector<EventBaz, String>() {
            @Override
            public String getKey(EventBaz value) throws Exception {
                return value.event;
            }
        });
        SingleOutputStreamOperator<EventBaz> process = keyBy.window(TumblingEventTimeWindows.of(Time.seconds(2)))
                .allowedLateness(Time.seconds(1)) //
                .sideOutputLateData(late) // 延迟的数据发送到分流器里面去
                .process(new ProcessWindowFunction<EventBaz, EventBaz, String, TimeWindow>() {
                    @Override
                    public void process(String s, Context context, Iterable<EventBaz> elements, Collector<EventBaz> out) throws Exception {
                        long watermark = context.currentWatermark();
                        long processingTime = context.currentProcessingTime();
                        long start = context.window().getStart();
                        long end = context.window().getEnd();
                        int subtask = getRuntimeContext().getIndexOfThisSubtask();
                        System.out.println("subTask " + subtask + ", processingTime " + processingTime + ", " +
                                "watermark " + watermark + ", windowStart " + start + ", windowEnd +" + end
                        );

                        for (Iterator<EventBaz> iterator = elements.iterator(); iterator.hasNext(); ) {
                            EventBaz data = iterator.next();
                            data.event = "-------on time: " + data.event;
                         //   System.out.println(subtask + ">" + data.toString());
                            out.collect(data);
                        }
                    }
                });
        process.print();
        process.getSideOutput(late).print();

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
