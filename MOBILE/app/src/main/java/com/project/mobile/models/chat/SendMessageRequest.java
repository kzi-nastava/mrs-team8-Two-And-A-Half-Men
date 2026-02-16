package com.project.mobile.models.chat;

public class SendMessageRequest {
    private String content;

    public SendMessageRequest() {

    }

    public SendMessageRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
