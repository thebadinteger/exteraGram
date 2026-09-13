package com.yandex.mapkit.location;

public interface LocationListener {
    void onLocationUpdated(Location location);
    void onLocationStatusUpdated(LocationStatus locationStatus);
}
