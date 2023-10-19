package com.iicportal.models;

public class BookingItem {
    private String name;
    private String image;
    private double price;
    private String time;
    private String date;
    private String status;
    private String userId;

    public BookingItem() {
    }

    public BookingItem(String name, String image, double price, String time, String date, String status, String userId) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.time = time;
        this.date = date;
        this.status = "Paid";
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public String getImage() {
        return image;
    }

    public double getPrice() {
        return price;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getStatus() {return status;}

    public String getUserId() {return userId;}

    public void setName(String name) {
        this.name = name;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public void setPrice(double price) {
        this.price = price;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setStatus(String status) {this.status = status;}

    public void setUserId(String userId) {this.userId = userId;}

}
