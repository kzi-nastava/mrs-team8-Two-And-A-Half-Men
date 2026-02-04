package com.project.mobile.DTO;

public class UserLoginResponseDto {
    private String accessToken;
    private Long expiresIn;
    private String email = null;
    private String firstName = null;
    private String lastName = null;
    private String imgUrl = null;
    private String role = null;

    public UserLoginResponseDto() {
    }

    public UserLoginResponseDto(String accessToken, Long expiresIn, String email, String firstName, String lastName, String imgUrl, String role) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imgUrl = imgUrl;
        this.role = role;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
