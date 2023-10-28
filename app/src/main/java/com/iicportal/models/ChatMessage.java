package com.iicportal.models;

public class ChatMessage {
    private String uid;
    private String message;
    private Long timestamp;

    public ChatMessage() {

    }

    public ChatMessage(String uid, String message, Long timestamp) {
        this.uid = uid;
        this.message = message;
        this.timestamp = timestamp;
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
