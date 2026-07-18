package com.example.smartroad;

public class NearbyHazard {

    private String hazardType;
    private String distance;

    public NearbyHazard(String hazardType, String distance) {
        this.hazardType = hazardType;
        this.distance = distance;
    }

    public String getHazardType() {
        return hazardType;
    }

    public String getDistance() {
        return distance;
    }
}