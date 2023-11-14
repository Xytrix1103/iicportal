package com.iicportal.models;

public class Status {
    private String uid;
    private String title;
    private String description;
    private String type;
    private Long timestamp;

    public Status() {
    }
    
    public Status(String uid, String title, String description, String type, Long timestamp) {
        this.uid = uid;
        this.title = title;
        this.description = description;
        this.type = type;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }

    public String getType() {
        return type;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
