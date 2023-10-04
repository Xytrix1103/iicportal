package com.iicportal.models;

public class Timeslot{
    String name;
    String image;
    double price;
    String time;
    String date;

    public Timeslot(){
    }

    public Timeslot(String time){
        this.time = time;
    }

    public Timeslot(Facilities facilities, Timeslot timeslot) {
        this.time = timeslot.getTime(); // Set the time from the given timeslot
        // You can set other fields based on the 'facilities' parameter if needed
        this.name= facilities.getName();
        this.image = facilities.getImage();
        this.price = facilities.getPrice();


    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time = time;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getImage(){
        return image;
    }

    public void setImage(String image){
        this.image = image;
    }

    public double getPrice(){
        return price;
    }

    public void setPrice(double price){
        this.price = price;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

}