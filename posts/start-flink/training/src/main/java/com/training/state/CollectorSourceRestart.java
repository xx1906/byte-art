package com.training.state;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.util.Collector;

import java.util.concurrent.ConcurrentLinkedQueue;

// Flink 程序的恢复策略可以在 flink-conf 中配置, 这个是全局的配置, 也可以在代码中动态指定配置, 如果在代码中指定
// 则表示这个是动态指定的, 只对当前的 flink 程序生效
@Slf4j
public class CollectorSourceRestart {
    public static void main(String[] args) throws Exception {

        log.info("start .... {}", CollectorSourceRestart.class.getName());
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 固定延迟（Fixed delay）
        // 设置 Flink 程序的恢复策略
        // 连续两次重启,会等待指定的时间间隔
        // env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, Time.seconds(3)));

        // 恢复机制, 根据失败率来进行恢复
        // 每个时间间隔, 失败失败率超过指定的值是, 尝试重启,
        // delay 是连续两次重启尝试等待的固定时间间隔
        env.setRestartStrategy(RestartStrategies.failureRateRestart(2, Time.seconds(8), Time.seconds(3)));

        // 不恢复（No restart）

        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();
        queue.add(2);
        queue.add(3);
        DataStreamSource<Integer> source = env.fromCollection(queue);
        SingleOutputStreamOperator<Integer> flatMap = source.flatMap(new FlatMapFunction<Integer, Integer>() {
            @Override
            public void flatMap(Integer integer, Collector<Integer> collector) throws Exception {
                if (integer % 2 == 1) {
                    // 这里的操作会触发程序的恢复策略
                    integer = integer / 0;
                }
                collector.collect(integer);
            }
        });

        flatMap.print();

        env.execute(CollectorSourceRestart.class.getName());


    }

}
