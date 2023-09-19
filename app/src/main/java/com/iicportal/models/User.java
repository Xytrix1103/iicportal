package com.iicportal.models;

public class User {
    private String fullName;
    private String phoneNumber;

    public User() {
    }

    public User(String fullName, String phoneNumber) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
