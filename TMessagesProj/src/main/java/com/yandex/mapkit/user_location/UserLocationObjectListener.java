package com.yandex.mapkit.user_location;

public interface UserLocationObjectListener {
    void onObjectAdded(UserLocationView userLocationView);
    void onObjectRemoved(UserLocationView userLocationView);
    void onObjectUpdated(UserLocationView userLocationView, com.yandex.mapkit.layers.ObjectEvent objectEvent);
}
