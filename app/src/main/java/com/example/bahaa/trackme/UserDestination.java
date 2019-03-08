package com.example.bahaa.trackme;

import com.google.gson.annotations.SerializedName;

public class UserDestination {
    @SerializedName("lat")
    private double lat;
    @SerializedName("lon")
    private double lon;
    @SerializedName("address")
    private String address;
    @SerializedName("title")
    private String title;

    public UserDestination(double lat,double lon,String address,String title){
        this.setLat(lat);
        this.setLon(lon);
        this.setAddress(address);
        this.setTitle(title);
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
