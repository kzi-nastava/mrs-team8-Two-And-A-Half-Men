package com.project.mobile.DTO.users;

public class User {
    private long id;
    private String email;
    private String firstName;
    private String lastName;
    private String role; // "ADMIN", "CUSTOMER", "DRIVER"
    private boolean isBlocked;
    private String driverStatus; // "BUSY", "INACTIVE", "AVAILABLE", or null
    private Boolean hasPendingRequests;

    public User() {}

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean blocked) { isBlocked = blocked; }

    public String getDriverStatus() { return driverStatus; }
    public void setDriverStatus(String driverStatus) { this.driverStatus = driverStatus; }

    public Boolean getHasPendingRequests() { return hasPendingRequests; }
    public void setHasPendingRequests(Boolean hasPendingRequests) { 
        this.hasPendingRequests = hasPendingRequests; 
    }
}
