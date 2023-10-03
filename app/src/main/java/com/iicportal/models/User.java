package com.iicportal.models;

public class User {
    private String email;
    private String fullName;
    private String phoneNumber;
    private String role;

    public User() {
    }

    public User(String fullName, String phoneNumber, String email, String role) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
    }
}
