package com.yandex.mapkit.location;

import com.yandex.mapkit.geometry.Point;

public class Location {
    public Point getPosition() { return new Point(0.0, 0.0); }
    public Float getAccuracy() { return 0f; }
    public long getAbsoluteTimestamp() { return 0L; }
    public Double getAltitude() { return 0.0; }
    public Float getSpeed() { return 0f; }
    public Float getHeading() { return 0f; }
}
