package com.project.mobile;

public class RideHistory {
    private String userEmail;
    private String scheduled;
    private String started;
    private String ended;
    private String status;
    private String price;
    private int passengers;

    public RideHistory() {
    }

    public RideHistory(String userEmail, String scheduled, String started, String ended,
                       String status, String price, int passengers) {
        this.userEmail = userEmail;
        this.scheduled = scheduled;
        this.started = started;
        this.ended = ended;
        this.status = status;
        this.price = price;
        this.passengers = passengers;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getScheduled() {
        return scheduled;
    }

    public void setScheduled(String scheduled) {
        this.scheduled = scheduled;
    }

    public String getStarted() {
        return started;
    }

    public void setStarted(String started) {
        this.started = started;
    }

    public String getEnded() {
        return ended;
    }

    public void setEnded(String ended) {
        this.ended = ended;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getPassengers() {
        return passengers;
    }

    public void setPassengers(int passengers) {
        this.passengers = passengers;
    }
}