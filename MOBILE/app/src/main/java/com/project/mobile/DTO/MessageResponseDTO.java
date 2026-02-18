package com.project.mobile.DTO;

public class MessageResponseDTO {
    private boolean success;
    private String message;

    public MessageResponseDTO(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
