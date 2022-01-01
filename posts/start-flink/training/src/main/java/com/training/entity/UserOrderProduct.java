package com.training.entity;

import com.alibaba.fastjson.JSON;

public class UserOrderProduct {
    public UserOrderProduct() {
    }

    public UserOrderProduct(String userId, String productId, String orderId, String productName, double price, long orderTime, long pushedTime, long finishedTime) {
        this.userId = userId;
        this.productId = productId;
        this.orderId = orderId;
        this.productName = productName;
        this.price = price;
        this.orderTime = orderTime;
        this.pushedTime = pushedTime;
        this.finishedTime = finishedTime;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
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

    private String userId;
    private String productId;
    private String orderId;
    private String productName;
    private double price;
    private long orderTime;
    private long pushedTime;
    private long finishedTime;
}
