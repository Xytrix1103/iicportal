package com.iicportal.models;

public class OrderOption {
    String icon;
    String option;

    public OrderOption() {
    }

    public OrderOption(String icon, String option) {
        this.icon = icon;
        this.option = option;
    }

    public String getIcon() {
        return icon;
    }

    public String getOption() {
        return option;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setOption(String option) {
        this.option = option;
    }
}
