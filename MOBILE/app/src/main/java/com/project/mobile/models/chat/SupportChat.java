package com.project.mobile.models.chat;

import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

public class SupportChat {
    @SerializedName("id")
    private Long id;

    @SerializedName("userId")
    private Long userId;

    @SerializedName("userEmail")
    private String userEmail;

    @SerializedName("userType")
    private String userType;

    @SerializedName("status")
    private ChatStatus status;

    @SerializedName("messages")
    private List<Message> messages = new ArrayList<>();

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserType() { return userType; }
    public void setUserType(String userType) { this.userType = userType; }

    public ChatStatus getStatus() { return status; }
    public void setStatus(ChatStatus status) { this.status = status; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }

    public Message getLastMessage() {
        if (messages == null || messages.isEmpty()) return null;
        return messages.get(messages.size() - 1);
    }

    public enum ChatStatus {
        @SerializedName("ACTIVE")
        ACTIVE,

        @SerializedName("CLOSED")
        CLOSED
    }
}