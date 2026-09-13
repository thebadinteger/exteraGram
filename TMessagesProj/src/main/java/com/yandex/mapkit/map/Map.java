package com.yandex.mapkit.map;

import com.yandex.mapkit.Animation;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.logo.Logo;

public interface Map {
    public interface CameraCallback {
        void onMoveFinished(boolean z);
    }

    void addCameraListener(CameraListener cameraListener);
    void removeCameraListener(CameraListener cameraListener);
    CameraPosition cameraPosition(Geometry geometry);
    CameraPosition getCameraPosition();
    CameraBounds getCameraBounds();
    Logo getLogo();
    MapObjectCollection getMapObjects();
    MapType getMapType();
    void setMapType(MapType mapType);
    void move(CameraPosition cameraPosition);
    void move(CameraPosition cameraPosition, Animation animation, CameraCallback cameraCallback);
    boolean isRotateGesturesEnabled();
    void setRotateGesturesEnabled(boolean z);
    boolean isScrollGesturesEnabled();
    void setScrollGesturesEnabled(boolean z);
    boolean isTiltGesturesEnabled();
    void setTiltGesturesEnabled(boolean z);
    boolean isZoomGesturesEnabled();
    void setZoomGesturesEnabled(boolean z);
    boolean isNightModeEnabled();
    void setNightModeEnabled(boolean z);
    void setMapStyle(String style);
    void setMapStyle(int id, String style);
    void setMapLoadedListener(MapLoadedListener listener);
}
