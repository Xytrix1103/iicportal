package com.iicportal.models;

public class User {
    private String email;
    private String fullName;
    private String phoneNumber;
    private String role;
    private String password;
    private String image;

    public User() {
    }

    public User(String fullName, String phoneNumber, String email, String role, String password, String image){
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.role = role;
        this.password = password;
        this.image = image;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return this.role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
