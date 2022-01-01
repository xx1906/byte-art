package com.training.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;

public class PersonOrder {
    public PersonOrder() {
    }

    public PersonOrder(String userId, String orderId, String productId, double price, long orderTime, long pushedTime, long finishedTime) {
        this.userId = userId;
        this.orderId = orderId;
        this.productId = productId;
        this.price = price;
        this.orderTime = orderTime;
        this.pushedTime = pushedTime;
        this.finishedTime = finishedTime;
    }

    public String toString() {
        return JSON.toJSONString(this);
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
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

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @JSONField(name = "user_id")
    private String userId;

    @JSONField(name = "order_id")
    private String orderId;

    @JSONField(name = "product_id")
    private String productId;

    private double price;

    @JSONField(name = "order_time")
    private long orderTime;

    @JSONField(name = "pushed_time")
    private long pushedTime;

    @JSONField(name = "finished_time")
    private long finishedTime;
}
