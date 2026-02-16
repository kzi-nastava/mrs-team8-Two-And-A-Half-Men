package com.project.mobile.models.chat;

import java.time.LocalDateTime;

public class Message {
    private Long id;
    private Long chatId;
    private Long senderId;
    private String content;
    private SenderType senderType;
    private Boolean adminRead;
    private Boolean userRead;
    private LocalDateTime timestamp;

    public enum SenderType {
        DRIVER, CUSTOMER, ADMIN
    }

    public Message(Long id, Long chatId, Long senderId, String content, SenderType senderType, Boolean adminRead, Boolean userRead, LocalDateTime timestamp) {
        this.id = id;
        this.chatId = chatId;
        this.senderId = senderId;
        this.content = content;
        this.senderType = senderType;
        this.adminRead = adminRead;
        this.userRead = userRead;
        this.timestamp = timestamp;
    }

    public Message() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public SenderType getSenderType() {
        return senderType;
    }

    public void setSenderType(SenderType senderType) {
        this.senderType = senderType;
    }

    public Boolean getAdminRead() {
        return adminRead;
    }

    public void setAdminRead(Boolean adminRead) {
        this.adminRead = adminRead;
    }

    public Boolean getUserRead() {
        return userRead;
    }

    public void setUserRead(Boolean userRead) {
        this.userRead = userRead;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
