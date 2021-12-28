package com.training.entity;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class TrainingRecord {
    final static Gson gson = new Gson();

    public static void main(String[] args) {
        TrainingRecord r = new TrainingRecord();
        r.setRecord("demo");
        log.info("test {}", r.toString());
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

//    public TrainingRecord(String record) {
//        this.record = record;
//    }

    @SerializedName("record")
    private String record;

    public TrainingRecord() {
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    @Test
   public void testSetRecord() {
        TrainingRecord trainingRecord = new TrainingRecord();
        trainingRecord.setRecord("demo");
        log.info("TestSetRecord:{}",trainingRecord.toString());
    }
    @Test
    public void testGetRecord() {
        TrainingRecord trainingRecord = new TrainingRecord();
        trainingRecord.setRecord("demo");
        log.info("testGetRecord:{}, value:{}",trainingRecord.toString(),trainingRecord.getRecord());
    }

}
