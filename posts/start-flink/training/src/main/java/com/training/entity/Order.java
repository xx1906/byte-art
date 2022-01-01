package com.training.entity;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

public class Order {

    public Order() {
    }

    public Order(String id, String userId, String productId, String name, double price, long orderTime, long pushedTime, long finishedTime) {
        this.id = id;
        this.userId = userId;
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.orderTime = orderTime;
        this.pushedTime = pushedTime;
        this.finishedTime = finishedTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public long getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(long orderTime) {
        this.orderTime = orderTime;
    }

    public long getPushedTime() {
        return pushedTime;
    }

    public void setPushedTime(long pushedTime) {
        this.pushedTime = pushedTime;
    }

    public long getFinishedTime() {
        return finishedTime;
    }

    public void setFinishedTime(long finishedTime) {
        this.finishedTime = finishedTime;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    private String id;

    @JSONField(name = "order_id")
    private String userId;

    @JSONField(name = "product_id")
    private String productId;

    private String name;
    private double price;

    @JSONField(name = "order_time")
    private long orderTime;

    @JSONField(name = "pushed_time")
    private long pushedTime;

    @JSONField(name = "finished_time")
    private long finishedTime;

}
