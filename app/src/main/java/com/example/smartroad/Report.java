package com.example.smartroad;

public class Report {
    public String reportId;
    public String userId;
    public String username; // Added username
    public String description;
    public String hazardType;
    public double latitude;
    public double longitude;
    public String address;
    public String date;
    public String time;
    public String status;
    public String imageUrl; // Added imageUrl
    public String location;

    public Report() {
        // Default constructor required for calls to DataSnapshot.getValue(Report.class)
    }

    public Report(String reportId, String userId, String username, String description, String hazardType,
                  double latitude, double longitude, String address, String date, String time, String status, String imageUrl) {
        this.reportId = reportId;
        this.userId = userId;
        this.username = username;
        this.description = description;
        this.hazardType = hazardType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.date = date;
        this.time = time;
        this.status = status;
        this.imageUrl = imageUrl;
        this.location = location;
    }
}
