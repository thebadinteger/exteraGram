package com.yandex.mapkit.map;

import com.yandex.mapkit.geometry.Point;
import com.yandex.runtime.image.ImageProvider;

public interface PlacemarkMapObject extends MapObject {
    Point getGeometry();
    void setGeometry(Point point);
    void setIcon(ImageProvider imageProvider);
    void setIconStyle(IconStyle iconStyle);
    void setDirection(float f);
}
