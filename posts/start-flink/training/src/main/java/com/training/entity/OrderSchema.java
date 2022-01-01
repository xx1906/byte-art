package com.training.entity;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

@Slf4j
public class OrderSchema implements DeserializationSchema<Order> {
    @Override
    public Order deserialize(byte[] bytes) throws IOException {
        log.info("data:{}", new String(bytes));
        return (Order) JSON.parseObject(bytes, Order.class);
    }

    @Override
    public boolean isEndOfStream(Order order) {
        return false;
    }

    @Override
    public TypeInformation<Order> getProducedType() {
        return TypeInformation.of(Order.class);
    }
}
