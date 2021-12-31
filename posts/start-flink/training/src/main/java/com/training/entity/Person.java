package com.training.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Person {
    private final static Gson gson = new Gson();

    public static void main(String[] args) {
        Person p = new Person();
        p.age = 1;
        p.name = "xkk";
        log.info("{}", p.toString());
        p = new Person("x", 1);
        log.info("{}", p.toString());
    }

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("age")
    @Expose
    public Integer age;

    public Person() {
    }

    public Person(String name, Integer age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

}