package com.iicportal.models;

import java.util.List;

public class Order {
    String uid;
    String timestamp;
    String paymentMethod;
    String orderOption;
    double orderTotal;
    double takeawayFee;
    double total;
    String status;

    List<CartItem> items;

    public Order() {
    }

    public Order(String uid, String timestamp, String paymentMethod, String orderOption, double orderTotal, double takeawayFee, double total, String status, List<CartItem> items) {
        this.uid = uid;
        this.timestamp = timestamp;
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

    public String getTimestamp() {
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

    public void setTimestamp(String timestamp) {
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
}
