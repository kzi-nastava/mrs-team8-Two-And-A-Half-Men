package com.project.mobile.DTO;

public class ActivateRequestDTO {
    private String token;
    public ActivateRequestDTO() {
    }
    public ActivateRequestDTO(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }
    public void setToken(String token) {
        this.token = token;
    }
}
