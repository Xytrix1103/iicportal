package com.iicportal.models;

public class OrderStatus extends Status {
    private String orderId;
    private String orderStatus;

    public OrderStatus() {
    }

    public OrderStatus(String uid, String title, String description, String type, Long timestamp, String orderId, String orderStatus) {
        super(uid, title, description, type, timestamp);
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
