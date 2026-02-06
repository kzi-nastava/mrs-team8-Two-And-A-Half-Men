package com.project.mobile.DTO;

import android.graphics.drawable.Drawable;

public class MarkerPointIcon {
    private double latitude;
    private double longitude;
    private String name;

    private Drawable icon;

    public MarkerPointIcon(double latitude, double longitude, String name, Drawable icon) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.icon = icon;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }
}
