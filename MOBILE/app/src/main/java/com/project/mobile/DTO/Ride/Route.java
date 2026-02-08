package com.project.mobile.DTO.Ride;

import org.osmdroid.views.overlay.Polyline;

public class Route {
    private Polyline routeLine;

    private String name;

    public Route(Polyline routeLine, String name) {
        this.routeLine = routeLine;

        this.name = name;
    }

    public Polyline getRouteLine() {
        return routeLine;
    }

    public void setRouteLine(Polyline routeLine) {
        this.routeLine = routeLine;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
