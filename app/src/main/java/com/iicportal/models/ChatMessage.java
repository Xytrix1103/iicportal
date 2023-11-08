package com.iicportal.models;

public class ChatMessage {
    private String uid;
    private String username;
    private String userProfilePicture;
    private String message;
    private Long timestamp;
    private boolean readByUser;

    public ChatMessage() {

    }

    public ChatMessage(String uid, String username, String userProfilePicture, String message, Long timestamp, boolean readByUser) {
        this.uid = uid;
        this.username = username;
        this.userProfilePicture = userProfilePicture;
        this.message = message;
        this.timestamp = timestamp;
        this.readByUser = readByUser;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }

    public String getMessage() {
        return message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public boolean isReadByUser() {
        return readByUser;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setUserProfilePicture(String userProfilePicture) {
        this.userProfilePicture = userProfilePicture;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public void setReadByUser(boolean readByUser) {
        this.readByUser = readByUser;
    }
}
