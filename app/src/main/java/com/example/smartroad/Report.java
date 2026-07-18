package com.example.smartroad;

public class Report {
    public String hazardId;
    public String userId;
    public String username;
    public String description;
    public String hazardType;
    public String latitude;  // Diubah ke String mengikut contoh Firebase yang diberikan
    public String longitude; // Diubah ke String mengikut contoh Firebase yang diberikan
    public String location;
    public String date;
    public String time;
    public String status;
    public String image;
    public String userAgent; // Medan baharu untuk maklumat peranti

    public Report() {
        // Diperlukan untuk Firebase
    }

    public Report(String hazardId, String userId, String username, String description, String hazardType,
                  String latitude, String longitude, String location, String date, String time, String status, String image, String userAgent) {
        this.hazardId = hazardId;
        this.userId = userId;
        this.username = username;
        this.description = description;
        this.hazardType = hazardType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.location = location;
        this.date = date;
        this.time = time;
        this.status = status;
        this.image = image;
        this.userAgent = userAgent;
    }
}
