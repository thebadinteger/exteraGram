package com.yandex.mapkit.map;

import com.yandex.mapkit.geometry.Point;

public interface MapObjectTapListener {
    boolean onMapObjectTap(MapObject mapObject, Point point);
}
