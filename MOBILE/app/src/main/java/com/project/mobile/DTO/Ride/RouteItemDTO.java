package com.project.mobile.DTO.Ride;

import com.google.gson.annotations.SerializedName;
import com.project.mobile.DTO.Map.NominatimResult;

public class RouteItemDTO {
    @SerializedName("address")
    private String address;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    public RouteItemDTO() {
    }
    public RouteItemDTO(NominatimResult nominatimResult) {
        this.address = nominatimResult.display_name;
        this.latitude = nominatimResult.getLat();
        this.longitude = nominatimResult.getLon();
    }
    public RouteItemDTO(String address, double latitude, double longitude) {
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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
}
