package com.project.mobile.DTO.profile;

public class ImageUploadResponse {
    private boolean success;
    private String message;
    private String imgSrc;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getImgSrc() {
        return imgSrc;
    }
}
