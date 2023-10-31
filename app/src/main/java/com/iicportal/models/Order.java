package com.iicportal.models;

import java.util.List;

public class Order {
    String uid;
    String orderID;
    Long timestamp, readyTimestamp, completedTimestamp;
    PaymentMethod paymentMethod;
    OrderOption orderOption;
    double orderTotal;
    double takeawayFee;
    double total;
    String status;
    boolean ready, completed;

    List<CartItem> items;

    public Order() {

    }

    public Order(String orderID, String uid, Long timestamp, Long readyTimestamp, Long completedTimestamp, PaymentMethod paymentMethod, OrderOption orderOption, double orderTotal, double takeawayFee, double total, String status, List<CartItem> items) {
        this.orderID = orderID;
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
        this.ready = readyTimestamp != null;
        this.completed = completedTimestamp != null;
    }

    public String getOrderID() {
        return orderID;
    }

    public String getUid() {
        return uid;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public OrderOption getOrderOption() {
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

    public boolean isReady() {
        return ready;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setOrderOption(OrderOption orderOption) {
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

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
