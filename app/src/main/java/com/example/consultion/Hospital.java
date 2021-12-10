package com.example.consultion;

public class Hospital {
    String longitude , latitude;
    String name;
    public Hospital(String longitude, String latitude, String name) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.name = name;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getName() {
        return name;
    }
}
