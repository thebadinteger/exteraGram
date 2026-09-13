package com.yandex.mapkit.map;

public interface CameraListener {
    void onCameraPositionChanged(Map map, CameraPosition cameraPosition, CameraUpdateReason cameraUpdateReason, boolean z);
}
