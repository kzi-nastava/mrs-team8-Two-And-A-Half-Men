package com.project.mobile.models.chat;

import com.google.gson.annotations.SerializedName;

public class Message {
    @SerializedName("id")
    private Long id;

    @SerializedName("chatId")
    private Long chatId;

    @SerializedName("senderId")
    private Long senderId;

    @SerializedName("content")
    private String content;

    @SerializedName("senderType")
    private SenderType senderType;

    @SerializedName("adminRead")
    private boolean adminRead;

    @SerializedName("userRead")
    private boolean userRead;

    @SerializedName("timestamp")
    private String timestamp;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public SenderType getSenderType() { return senderType; }
    public void setSenderType(SenderType senderType) { this.senderType = senderType; }

    public boolean isAdminRead() { return adminRead; }
    public void setAdminRead(boolean adminRead) { this.adminRead = adminRead; }

    public boolean isUserRead() { return userRead; }
    public void setUserRead(boolean userRead) { this.userRead = userRead; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public String getFormattedTime() {
        if (timestamp == null) return "";
        try {
            // Parse "2024-01-05T15:45:00" format
            String[] parts = timestamp.split("T");
            if (parts.length > 1) {
                String time = parts[1];
                String[] timeParts = time.split(":");
                if (timeParts.length >= 2) {
                    return timeParts[0] + ":" + timeParts[1];
                }
            }
        } catch (Exception e) {
            return "";
        }
        return timestamp;
    }

    public enum SenderType {
        @SerializedName("DRIVER")
        DRIVER,

        @SerializedName("CUSTOMER")
        CUSTOMER,

        @SerializedName("ADMIN")
        ADMIN
    }
}