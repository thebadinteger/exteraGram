package com.yandex.mapkit.geometry;

public class Point {
    private double latitude;
    private double longitude;

    public Point() {
    }

    public Point(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return this.latitude;
    }

    public double getLongitude() {
        return this.longitude;
    }
}
