package com.project.mobile.DTO.Auth;

public class UserLoginRequest {

    private String username;
    private String password;

    public UserLoginRequest() {
    }

    public UserLoginRequest(String email, String password) {
        this.username = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String email) {
        this.username = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
