package com.iicportal.models;

import java.util.List;

public class Order {
    String uid;
    Long timestamp, readyTimestamp, completedTimestamp;
    String paymentMethod;
    String orderOption;
    double orderTotal;
    double takeawayFee;
    double total;
    String status;

    List<CartItem> items;

    public Order() {
    }

    public Order(String uid, Long timestamp, Long readyTimestamp, Long completedTimestamp, String paymentMethod, String orderOption, double orderTotal, double takeawayFee, double total, String status, List<CartItem> items) {
        this.uid = uid;
        this.timestamp = timestamp;
        this.readyTimestamp = readyTimestamp;
        this.completedTimestamp = completedTimestamp;
        this.paymentMethod = paymentMethod;
        this.orderOption = orderOption;
        this.orderTotal = orderTotal;
        this.takeawayFee = takeawayFee;
        this.total = total;
        this.items = items;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public String getOrderOption() {
        return orderOption;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public double getTakeawayFee() {
        return takeawayFee;
    }

    public double getTotal() {
        return total;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setOrderOption(String orderOption) {
        this.orderOption = orderOption;
    }

    public void setOrderTotal(double orderTotal) {
        this.orderTotal = orderTotal;
    }

    public void setTakeawayFee(double takeawayFee) {
        this.takeawayFee = takeawayFee;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getReadyTimestamp() {
        return readyTimestamp;
    }

    public void setReadyTimestamp(Long readyTimestamp) {
        this.readyTimestamp = readyTimestamp;
    }

    public Long getCompletedTimestamp() {
        return completedTimestamp;
    }

    public void setCompletedTimestamp(Long completedTimestamp) {
        this.completedTimestamp = completedTimestamp;
    }
}
