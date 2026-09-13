package com.yandex.mapkit.map;

import com.yandex.mapkit.geometry.Point;

public class CameraPosition {
    private Point target;
    private float zoom;
    private float azimuth;
    private float tilt;

    public CameraPosition() {}
    public CameraPosition(Point target, float zoom, float azimuth, float tilt) {
        this.target = target;
        this.zoom = zoom;
        this.azimuth = azimuth;
        this.tilt = tilt;
    }
    public Point getTarget() { return this.target; }
    public float getZoom() { return this.zoom; }
    public float getAzimuth() { return this.azimuth; }
    public float getTilt() { return this.tilt; }
}
