package com.project.mobile.DTO;

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
}
