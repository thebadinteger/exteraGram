package com.yandex.mapkit.geometry;

public class Circle {
    private Point center;
    private float radius;

    public Circle() {}

    public Circle(Point center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public Point getCenter() { return this.center; }
    public float getRadius() { return this.radius; }
}
