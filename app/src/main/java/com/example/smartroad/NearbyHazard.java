package com.example.smartroad;

public class NearbyHazard {

    private String hazardId;
    private String hazardType;
    private String distance;

    public NearbyHazard(String hazardId, String hazardType, String distance) {
        this.hazardId = hazardId;
        this.hazardType = hazardType;
        this.distance = distance;
    }

    public String getHazardId() {
        return hazardId;
    }

    public String getHazardType() {
        return hazardType;
    }

    public String getDistance() {
        return distance;
    }
}