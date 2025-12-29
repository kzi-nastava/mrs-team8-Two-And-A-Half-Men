package com.project.mobile.models;

public class UserProfile {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String email;
    private String photoUrl;

    public UserProfile() {}

    public UserProfile(String firstName, String lastName, String phoneNumber,
                       String address, String email, String photoUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.email = email;
        this.photoUrl = photoUrl;
    }

    // Getters
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getEmail() { return email; }
    public String getPhotoUrl() { return photoUrl; }

    // Setters
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(String address) { this.address = address; }
    public void setEmail(String email) { this.email = email; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
