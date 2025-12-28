package com.project.backend.DTO;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class HistoryDTO {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private ArrayList<String> route;
    private String cancelationReasons;
    private double cost;
    private String path;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<String> getRoute() {
        return route;
    }

    public void setRoute(String path) {
        this.route.add(path);
    }

    public String getCancelationReasons() {
        return cancelationReasons;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setCancelationReasons(String cancelationReasons) {
        this.cancelationReasons = cancelationReasons;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }
}
