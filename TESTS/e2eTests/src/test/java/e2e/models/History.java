package e2e.models;

import java.time.LocalDateTime;

public class History {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long cost;
    private String scheculedTime;
    private String status;
    private String driverName;
    private String customerOwner;
    private String startLocation;
    private String endLocation;

    public History(LocalDateTime startTime, LocalDateTime endTime, Long cost, String status, String driverName, String customerOwner, String startLocation, String endLocation, String scheculedTime) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.cost = cost;
        this.status = status;
        this.driverName = driverName;
        this.customerOwner = customerOwner;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.scheculedTime = scheculedTime;
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

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getCustomerOwner() {
        return customerOwner;
    }

    public void setCustomerOwner(String customerOwner) {
        this.customerOwner = customerOwner;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getScheculedTime() {
        return scheculedTime;
    }

    public void setScheculedTime(String scheculedTime) {
        this.scheculedTime = scheculedTime;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        History history = (History) obj;
        if (!startTime.equals(history.startTime)) return false;
        if (!endTime.equals(history.endTime)) return false;
        if (!cost.equals(history.cost)) return false;
        if (!status.equals(history.status)) return false;
        if (!driverName.equals(history.driverName)) return false;
        if (!customerOwner.equals(history.customerOwner)) return false;
        if (!startLocation.equals(history.startLocation)) return false;
        if(!scheculedTime.equals(history.scheculedTime)) return false;
        return endLocation.equals(history.endLocation);
    }

}
