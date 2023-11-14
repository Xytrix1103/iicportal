package com.iicportal.models;

import java.util.Map;

public class Chat {
    private Map<String, Boolean> members;

    public Chat() {

    }

    public Chat(Map<String, Boolean> members) {
        this.members = members;
    }

    public Map<String, Boolean> getMembers() {
        return members;
    }

    public void setMembers(Map<String, Boolean> members) {
        this.members = members;
    }
}
