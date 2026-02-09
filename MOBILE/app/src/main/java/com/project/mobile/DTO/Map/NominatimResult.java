package com.project.mobile.DTO.Map;

public class NominatimResult {
    public String display_name;
    public String lat;
    public String lon;

    public Double getLat() {
        return Double.parseDouble(lat);
    }

    public Double getLon() {
        return Double.parseDouble(lon);
    }

    public NominatimResult(String display_name, String lat, String lon) {
        this.display_name = display_name;
        this.lat = lat;
        this.lon = lon;
    }
    public NominatimResult(String display_name , double lat, double lon) {
        this.display_name = display_name;
        this.lat = String.valueOf(lat);
        this.lon = String.valueOf(lon);
    }
}
