package com.yandex.mapkit.map;

import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.geometry.Point;

public interface MapWindow {
    Map getMap();
    ScreenPoint worldToScreen(Point point);
}
