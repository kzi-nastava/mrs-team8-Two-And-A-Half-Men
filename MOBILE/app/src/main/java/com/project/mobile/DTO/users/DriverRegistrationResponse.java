package com.project.mobile.DTO.users;

public class DriverRegistrationResponse {
    private long vehicleId;
    private long driverId;
    private boolean ok;
    private String message;

    public DriverRegistrationResponse() {}

    public long getVehicleId() { return vehicleId; }
    public void setVehicleId(long vehicleId) { this.vehicleId = vehicleId; }

    public long getDriverId() { return driverId; }
    public void setDriverId(long driverId) { this.driverId = driverId; }

    public boolean isOk() { return ok; }
    public void setOk(boolean ok) { this.ok = ok; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
