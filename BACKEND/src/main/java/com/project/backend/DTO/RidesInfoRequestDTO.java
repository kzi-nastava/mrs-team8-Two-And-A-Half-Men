package com.project.backend.DTO;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class RidesInfoRequestDTO {

    private String vihecleType;
    private ArrayList<String> additionalServices;
    private ArrayList<String> addreessPoints;
    private LocalDateTime scheduledTime;
    private LocalDateTime sheculedAt;



    public String getVihecleType() {
        return vihecleType;
    }

    public void setVihecleType(String vihecleType) {
        this.vihecleType = vihecleType;
    }

    public ArrayList<String> getAdditionalServices() {
        return additionalServices;
    }

    public void setAdditionalServices(ArrayList<String> additionalServices) {
        this.additionalServices = additionalServices;
    }

    public ArrayList<String> getAddreessPoints() {
        return addreessPoints;
    }

    public void setAddreessPoints(ArrayList<String> addreessPoints) {
        this.addreessPoints = addreessPoints;
    }

    public LocalDateTime getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(LocalDateTime scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public LocalDateTime getSheculedAt() {
        return sheculedAt;
    }

    public void setSheculedAt(LocalDateTime sheculedAt) {
        this.sheculedAt = sheculedAt;
    }

}
