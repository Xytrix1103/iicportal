package com.iicportal.models;

public class PaymentMethod {
    String icon;
    String method;

    public PaymentMethod() {
    }

    public PaymentMethod(String icon, String method) {
        this.icon = icon;
        this.method = method;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
