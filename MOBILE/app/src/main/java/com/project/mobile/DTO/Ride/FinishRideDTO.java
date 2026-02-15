package com.project.mobile.DTO.Ride;

public class FinishRideDTO {
    Boolean isInterrupted;
    Boolean isPayed;

    public FinishRideDTO(Boolean isInterrupted, Boolean isPayed) {
        this.isInterrupted = isInterrupted;
        this.isPayed = isPayed;
    }

    public Boolean getInterrupted() {
        return isInterrupted;
    }

    public void setInterrupted(Boolean interrupted) {
        isInterrupted = interrupted;
    }

    public Boolean getPayed() {
        return isPayed;
    }

    public void setPayed(Boolean payed) {
        isPayed = payed;
    }
}
