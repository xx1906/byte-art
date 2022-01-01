package com.training.entity;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

@Slf4j
public class ProductSchema implements DeserializationSchema<Product> {
    @Override
    public Product deserialize(byte[] bytes) throws IOException {
        log.info("data:{}", new String(bytes));
        return JSON.parseObject(bytes, Product.class);
    }

    @Override
    public boolean isEndOfStream(Product product) {
        return false;
    }

    @Override
    public TypeInformation<Product> getProducedType() {
        return TypeInformation.of(Product.class);
    }
}
