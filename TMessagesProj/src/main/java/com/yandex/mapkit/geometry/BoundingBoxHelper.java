package com.yandex.mapkit.geometry;

public class BoundingBoxHelper {
    public static BoundingBox getBounds(Point point) {
        return new BoundingBox(point, point);
    }
    public static BoundingBox getBounds(BoundingBox b1, BoundingBox b2) {
        return b1 != null ? b1 : b2;
    }
}
