package com.project.backend.DTO.Vehicle;

public class ActiveVehicleDTO {
    private Long id;
    private String model;
    private String licensePlate;
    private String vehicleTypeName;
    private Double latitude;
    private Double longitude;
    private boolean occupied;

    public ActiveVehicleDTO() {}

    public ActiveVehicleDTO(Long id, String model, String licensePlate,
                            String vehicleTypeName, Double latitude,
                            Double longitude, boolean occupied) {
        this.id = id;
        this.model = model;
        this.licensePlate = licensePlate;
        this.vehicleTypeName = vehicleTypeName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.occupied = occupied;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }

    public String getVehicleTypeName() { return vehicleTypeName; }
    public void setVehicleTypeName(String vehicleTypeName) { this.vehicleTypeName = vehicleTypeName; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public boolean isOccupied() { return occupied; }
    public void setOccupied(boolean occupied) { this.occupied = occupied; }
}