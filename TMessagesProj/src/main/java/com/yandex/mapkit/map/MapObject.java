package com.yandex.mapkit.map;

public interface MapObject {
    void addTapListener(MapObjectTapListener mapObjectTapListener);
    void removeTapListener(MapObjectTapListener mapObjectTapListener);
    void setZIndex(float f);
    float getZIndex();
    void setVisible(boolean z);
    boolean isVisible();
    void setUserData(Object obj);
    Object getUserData();
    MapObjectCollection getParent();
}
