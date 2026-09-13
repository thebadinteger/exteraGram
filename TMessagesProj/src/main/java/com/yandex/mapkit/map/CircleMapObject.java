package com.yandex.mapkit.map;

import com.yandex.mapkit.geometry.Circle;

public interface CircleMapObject extends MapObject {
    Circle getGeometry();
    void setGeometry(Circle circle);
    void setStrokeColor(int color);
    int getStrokeColor();
    void setStrokeWidth(float width);
    float getStrokeWidth();
    void setFillColor(int color);
    int getFillColor();
    void setZIndex(float f);
}
