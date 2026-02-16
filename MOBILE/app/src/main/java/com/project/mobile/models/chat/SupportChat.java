package com.project.mobile.models.chat;

import java.util.List;

public class SupportChat {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userType;
    private ChatStatus status;
    private List<Message> messages;

    public enum ChatStatus {
        ACTIVE, CLOSED
    }

    public SupportChat() {
    }

    public SupportChat(Long id, Long userId, String userEmail, String userType, ChatStatus status, List<Message> messages) {
        this.id = id;
        this.userId = userId;
        this.userEmail = userEmail;
        this.userType = userType;
        this.status = status;
        this.messages = messages;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public ChatStatus getStatus() {
        return status;
    }

    public void setStatus(ChatStatus status) {
        this.status = status;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public Message getLastMessage() {
        if (messages == null || messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }
}
