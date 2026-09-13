package com.yandex.mapkit.geometry;

public class BoundingBox {
    private Point southWest;
    private Point northEast;

    public BoundingBox() {}
    public BoundingBox(Point southWest, Point northEast) {
        this.southWest = southWest;
        this.northEast = northEast;
    }
    public Point getSouthWest() { return this.southWest; }
    public Point getNorthEast() { return this.northEast; }
}
