package com.iicportal.models;

public class BookingItem {
    private String name;
    private String image;
    private double price;
    private String time;
    private String date;

    public BookingItem() {
    }

    public BookingItem(String name, String image, double price, String time, String date) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.time = time;
        this.date = date;
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

}
