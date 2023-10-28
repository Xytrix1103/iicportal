package com.iicportal.models;

public class Message {
    private String uid;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private String message;
    private boolean read;
    private Long timestamp;

    public Message() {
    }

    public Message(String uid, String firstName, String lastName, String fullName, String email, String phone, String message, boolean read, Long timestamp) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.message = message;
        this.read = read;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getMessage() {
        return message;
    }

    public boolean isRead() {
        return read;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
