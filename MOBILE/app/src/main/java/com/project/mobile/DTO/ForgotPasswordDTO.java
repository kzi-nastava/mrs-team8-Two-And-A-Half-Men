package com.project.mobile.DTO;

public class ForgotPasswordDTO {
    private String email;

    public ForgotPasswordDTO(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
