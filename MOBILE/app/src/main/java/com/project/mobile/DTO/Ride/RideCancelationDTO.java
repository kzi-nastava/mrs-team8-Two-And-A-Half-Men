package com.project.mobile.DTO.Ride;

public class RideCancelationDTO {
    private String reason;
    private String cancelledBy;

    public RideCancelationDTO() {
    }

    public RideCancelationDTO(String reason, String cancelledBy) {
        this.reason = reason;
        this.cancelledBy = cancelledBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCancelledBy() {
        return cancelledBy;
    }

    public void setCancelledBy(String cancelledBy) {
        this.cancelledBy = cancelledBy;
    }
}