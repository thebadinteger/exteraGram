package com.yandex.mapkit.map;

import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Point;

public interface MapObjectCollection {
    void addTapListener(MapObjectTapListener listener);
    void remove(MapObject mapObject);
    void clear();
    PlacemarkMapObject addPlacemark(Point point);
    CircleMapObject addCircle(Circle circle);
}
