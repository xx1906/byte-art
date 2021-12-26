package com.training.restart.strategy;

import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.restartstrategy.RestartStrategies;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.streaming.api.datastream.DataStreamSource;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.source.SocketTextStreamFunction;

import java.util.Date;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TrainingRestartStrategy {
    public static void main(String[] args) throws Exception {
        // 获取执行环境
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        // 设置重试策略
        env.setRestartStrategy(RestartStrategies.fixedDelayRestart(3, Time.of(1, TimeUnit.SECONDS)));
        // 构造数据源
        CoverSocketTextStreamFunction src = new CoverSocketTextStreamFunction("localhost", 6666, "\n", 6);
        // 添加数据源
        DataStreamSource<String> socket = env.addSource(src);
        socket.print();
        // 执行 flink 程序
        env.execute("restart_strategy");
    }
}

@Slf4j
class CoverSocketTextStreamFunction extends SocketTextStreamFunction {

    public CoverSocketTextStreamFunction(String hostname, int port, String delimiter, long maxNumRetries) {
        super(hostname, port, delimiter, maxNumRetries);
    }

    public CoverSocketTextStreamFunction(String hostname, int port, String delimiter, long maxNumRetries, long delayBetweenRetries) {
        super(hostname, port, delimiter, maxNumRetries, delayBetweenRetries);
    }

    @Override
    public void cancel() {
        super.cancel();
        //

        log.error("cancel:{}", new Date().toString());
    }
}
