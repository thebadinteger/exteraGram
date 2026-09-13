package com.yandex.mapkit.location;

public interface LocationManager {
    void requestSingleUpdate(LocationListener locationListener);
    void resume();
    void subscribeForLocationUpdates(SubscriptionSettings subscriptionSettings, LocationListener locationListener);
    void suspend();
    void unsubscribe(LocationListener locationListener);
}
