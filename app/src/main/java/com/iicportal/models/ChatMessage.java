package com.iicportal.models;

public class ChatMessage {
    private String uid;
    private String chat;
    private String message;
    private Long timestamp;
    private boolean read;

    public ChatMessage() {

    }

    public ChatMessage(String uid, String message, Long timestamp, boolean read) {
        this.uid = uid;
        this.message = message;
        this.timestamp = timestamp;
        this.read = read;
    }

    public String getUid() {
        return uid;
    }

    public String getMessage() {
        return message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
