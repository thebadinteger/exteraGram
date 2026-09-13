package com.yandex.mapkit;

import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.map.MapWindow;
import com.yandex.mapkit.user_location.UserLocationLayer;

public interface MapKit {
    LocationManager createLocationManager();
    UserLocationLayer createUserLocationLayer(MapWindow mapWindow);
    void onStart();
    void onStop();
    void onTerminate();
    void resetLocationManagerToDefault();
    void setApiKey(String str);
    void setUserId(String str);
    boolean isValid();
}
