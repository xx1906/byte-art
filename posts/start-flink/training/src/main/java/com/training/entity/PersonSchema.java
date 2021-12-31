package com.training.entity;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.hadoop.yarn.webapp.hamlet.Hamlet;

import java.util.Arrays;


@Slf4j
public class PersonSchema implements DeserializationSchema<Person> {

    @Override
    public Person deserialize(byte[] message) {
        log.info("data:{}", Arrays.toString(message));
        Person p;
        try {
            p = (Person) JSON.parseObject(message, Person.class);
        } catch (Exception e) {
            log.error("trace:{}", e.getMessage());
            p = new Person();
        }
        return p;

    }

    @Override
    public boolean isEndOfStream(Person nextElement) {
        return false;
    }

    @Override
    public TypeInformation<Person> getProducedType() {
        return TypeInformation.of(Person.class);
    }
}
