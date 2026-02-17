package com.project.mobile.DTO.users;

public class UserFilters {
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private Boolean isBlocked;
    private String driverStatus;
    private Boolean hasPendingRequests;

    public UserFilters() {}

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getIsBlocked() { return isBlocked; }
    public void setIsBlocked(Boolean isBlocked) { this.isBlocked = isBlocked; }

    public String getDriverStatus() { return driverStatus; }
    public void setDriverStatus(String driverStatus) { this.driverStatus = driverStatus; }

    public Boolean getHasPendingRequests() { return hasPendingRequests; }
    public void setHasPendingRequests(Boolean hasPendingRequests) { 
        this.hasPendingRequests = hasPendingRequests; 
    }

    public boolean hasAnyFilter() {
        return email != null || firstName != null || lastName != null || 
               role != null || isBlocked != null || driverStatus != null || 
               hasPendingRequests != null;
    }

    public void clear() {
        email = null;
        firstName = null;
        lastName = null;
        role = null;
        isBlocked = null;
        driverStatus = null;
        hasPendingRequests = null;
    }
}
