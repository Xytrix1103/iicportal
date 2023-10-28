package com.iicportal.models;

public class Chat {
    private String initiatorUid;
    private String initiatorName;
    private String title;
    private String latestMessage;
    private Long latestMessageTimestamp;

    public Chat() {

    }

    public Chat(String initiatorUid, String initiatorName, String title, String latestMessage, Long latestMessageTimestamp) {
        this.initiatorUid = initiatorUid;
        this.initiatorName = initiatorName;
        this.title = title;
        this.latestMessage = latestMessage;
        this.latestMessageTimestamp = latestMessageTimestamp;
    }

    public String getInitiatorUid() {
        return initiatorUid;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public String getTitle() {
        return title;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public Long getLatestMessageTimestamp() {
        return latestMessageTimestamp;
    }

    public void setInitiatorUid(String initiatorUid) {
        this.initiatorUid = initiatorUid;
    }

    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public void setLatestMessageTimestamp(Long latestMessageTimestamp) {
        this.latestMessageTimestamp = latestMessageTimestamp;
    }
}
