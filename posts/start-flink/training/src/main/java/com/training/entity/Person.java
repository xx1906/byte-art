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
        p = new Person("233", "x", 1);
        log.info("{}", p.toString());
    }

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("age")
    @Expose
    public Integer age;

    @SerializedName("id")
    public String id;

    public Person() {
    }

    public Person(String id, String name, Integer age) {
        this.name = name;
        this.age = age;
        this.id = id;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}