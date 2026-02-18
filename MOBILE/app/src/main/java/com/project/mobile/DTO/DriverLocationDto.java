package com.project.mobile.DTO;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class DriverLocationDto {
    private String driverId;
    private String driverName;
    private String driverEmail;

    private double latitude;
    private double longitude;
    private boolean isOccupied;
    private boolean isAvailable;

    private Long currentRideId;

    private String vehicleTypeName;

    private Long timestamp;

    public DriverLocationDto() {
    }

    public DriverLocationDto(String driverId, String driverName, String driverEmail, double latitude, double longitude, boolean isOccupied, boolean isAvailable, Long currentRideId, String vehicleTypeName, Long timestamp) {
        this.driverId = driverId;
        this.driverName = driverName;
        this.driverEmail = driverEmail;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isOccupied = isOccupied;
        this.isAvailable = isAvailable;
        this.currentRideId = currentRideId;
        this.vehicleTypeName = vehicleTypeName;
        this.timestamp = timestamp;
    }
    public static DriverLocationDto fromJson(String locationUpdate) {
        try {
            JSONObject json = new JSONObject(locationUpdate);

            DriverLocationDto dto = new DriverLocationDto();

            // Parse required fields
            dto.setDriverId(json.optString("driverId", null));
            dto.setDriverName(json.optString("driverName", null));
            dto.setDriverEmail(json.optString("driverEmail", null));
            dto.setLatitude(json.optDouble("latitude", 0.0));
            dto.setLongitude(json.optDouble("longitude", 0.0));
            dto.setOccupied(json.optBoolean("isOccupied", false));
            dto.setAvailable(json.optBoolean("isActive", true)); // Note: JSON has "isActive"
            dto.setVehicleTypeName(json.optString("vehicleTypeName", null));
            dto.setTimestamp(json.optLong("timestamp", System.currentTimeMillis()));

            // Parse nullable field
            if (json.has("currentRideId") && !json.isNull("currentRideId")) {
                dto.setCurrentRideId(json.getLong("currentRideId"));
            } else {
                dto.setCurrentRideId(null);
            }

            return dto;

        } catch (JSONException e) {
            Log.e("DriverLocationDto", "Error parsing JSON: " + locationUpdate, e);
            return null;
        }
    }

    public String getDriverId() {
        return driverId;
    }

    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverEmail() {
        return driverEmail;
    }

    public void setDriverEmail(String driverEmail) {
        this.driverEmail = driverEmail;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isOccupied() {
        return isOccupied;
    }

    public void setOccupied(boolean occupied) {
        isOccupied = occupied;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public Long getCurrentRideId() {
        return currentRideId;
    }

    public void setCurrentRideId(Long currentRideId) {
        this.currentRideId = currentRideId;
    }

    public String getVehicleTypeName() {
        return vehicleTypeName;
    }

    public void setVehicleTypeName(String vehicleTypeName) {
        this.vehicleTypeName = vehicleTypeName;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
