package com.iicportal.models;

public class Timeslot{
    String time;

    public Timeslot(){
    }

    public Timeslot(String time){
        this.time = time;
    }

    public String getTime(){
        return time;
    }

    public void setTime(String time){
        this.time = time;
    }
}