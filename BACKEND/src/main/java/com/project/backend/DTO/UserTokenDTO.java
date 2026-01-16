package com.project.backend.DTO;

public class UserTokenDTO {
    private String accessToken;
    private Long expiresIn;
    private String email = null;
    private String firstName = null;
    private String lastName = null;
    private String imgUrl = null;

    public UserTokenDTO() {
        this.accessToken = null;
        this.expiresIn = null;
    }

    public UserTokenDTO(String accessToken, long expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }

    public UserTokenDTO(String accessToken, long expiresIn, String email, String firstName, String lastName, String imgUrl) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.imgUrl = imgUrl;
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
}
