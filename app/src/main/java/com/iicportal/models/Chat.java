package com.iicportal.models;

public class Chat {
    private String initiatorUid;
    private String initiatorName;
    private String latestMessage;
    private Long latestMessageTimestamp;
    private String latestMessageSenderUid;
    private boolean readByAdmin;

    public Chat() {

    }

    public Chat(String initiatorUid, String initiatorName, String latestMessage, Long latestMessageTimestamp, String latestMessageSenderUid, boolean readByAdmin) {
        this.initiatorUid = initiatorUid;
        this.initiatorName = initiatorName;
        this.latestMessage = latestMessage;
        this.latestMessageTimestamp = latestMessageTimestamp;
        this.latestMessageSenderUid = latestMessageSenderUid;
        this.readByAdmin = readByAdmin;
    }

    public String getInitiatorUid() {
        return initiatorUid;
    }

    public String getInitiatorName() {
        return initiatorName;
    }

    public String getLatestMessage() {
        return latestMessage;
    }

    public Long getLatestMessageTimestamp() {
        return latestMessageTimestamp;
    }

    public String getLatestMessageSenderUid() {
        return latestMessageSenderUid;
    }

    public boolean isReadByAdmin() {
        return readByAdmin;
    }

    public void setInitiatorUid(String initiatorUid) {
        this.initiatorUid = initiatorUid;
    }

    public void setInitiatorName(String initiatorName) {
        this.initiatorName = initiatorName;
    }

    public void setLatestMessage(String latestMessage) {
        this.latestMessage = latestMessage;
    }

    public void setLatestMessageTimestamp(Long latestMessageTimestamp) {
        this.latestMessageTimestamp = latestMessageTimestamp;
    }

    public void setLatestMessageSenderUid(String latestMessageSenderUid) {
        this.latestMessageSenderUid = latestMessageSenderUid;
    }

    public void setReadByAdmin(boolean readByAdmin) {
        this.readByAdmin = readByAdmin;
    }
}
