package com.training.entity;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.flink.api.common.serialization.DeserializationSchema;
import org.apache.flink.api.common.typeinfo.TypeInformation;

import java.io.IOException;

@Slf4j
public class PersonSchema implements DeserializationSchema<Person> {
    private final Gson gson = new Gson();


    static {
        log.info("{}", PersonSchema.class.getName());
    }

    @Override
    public Person deserialize(byte[] message) throws IOException {
        return gson.fromJson(new String(message), Person.class);

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
