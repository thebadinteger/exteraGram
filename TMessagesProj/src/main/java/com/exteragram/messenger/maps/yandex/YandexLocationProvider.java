package com.exteragram.messenger.maps.yandex;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import androidx.core.util.Consumer;
import com.exteragram.messenger.utils.AppUtils;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.location.LocationListener;
import com.yandex.mapkit.location.LocationManager;
import com.yandex.mapkit.location.LocationStatus;
import com.yandex.mapkit.location.Purpose;
import com.yandex.mapkit.location.SubscriptionSettings;
import com.yandex.mapkit.location.UseInBackground;
import java.util.HashMap;
import java.util.Objects;
import org.telegram.messenger.ILocationServiceProvider;

@SuppressLint({"MissingPermission"})
public class YandexLocationProvider implements ILocationServiceProvider {
    private HashMap<ILocationServiceProvider.ILocationListener, LocationListener> locationListeners;
    private LocationManager locationManager;

    public static Location convertYandexLocationToAndroid(com.yandex.mapkit.location.Location location) {
        Location location2 = new Location("YandexProvider");
        location2.setLatitude(location.getPosition().getLatitude());
        location2.setLongitude(location.getPosition().getLongitude());
        if (location.getAccuracy() != null) {
            location2.setAccuracy(location.getAccuracy().floatValue());
        }
        location2.setTime(location.getAbsoluteTimestamp());
        if (location.getAltitude() != null) {
            location2.setAltitude(location.getAltitude().doubleValue());
        }
        if (location.getSpeed() != null) {
            location2.setSpeed(location.getSpeed().floatValue());
        }
        if (location.getHeading() != null) {
            location2.setBearing(location.getHeading().floatValue());
        }
        location2.setElapsedRealtimeNanos(location.getAbsoluteTimestamp());
        return location2;
    }

    @Override 
    public void init(Context context) {
        YandexMapsProvider.initialize(context);
        this.locationListeners = new HashMap<>();
    }

    public LocationManager getLocationManager() {
        if (this.locationManager == null) {
            MapKitFactory.getInstance().resetLocationManagerToDefault();
            this.locationManager = MapKitFactory.getInstance().createLocationManager();
        }
        return this.locationManager;
    }

    @Override 
    public ILocationServiceProvider.ILocationRequest onCreateLocationRequest() {
        return new YandexLocationRequest();
    }

    @Override 
    public void getLastLocation(final Consumer<Location> consumer) {
        AppUtils.ensureRunningOnUi(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getLastLocation$0(consumer);
            }
        });
    }

    public void lambda$requestLocationUpdates$1(ILocationServiceProvider.ILocationListener iLocationListener, ILocationServiceProvider.ILocationRequest iLocationRequest) {
        ConvertedLocationListener convertedLocationListener = new ConvertedLocationListener(iLocationListener);
        LocationManager locationManager = getLocationManager();
        LocationListener locationListenerRemove = this.locationListeners.remove(iLocationListener);
        if (locationListenerRemove != null) {
            locationManager.unsubscribe(locationListenerRemove);
        }
        UseInBackground useInBackground = UseInBackground.DISALLOW;
        Purpose purpose = Purpose.GENERAL;
        if (iLocationRequest instanceof YandexLocationRequest) {
            useInBackground = UseInBackground.ALLOW;
            purpose = ((YandexLocationRequest) iLocationRequest).purpose;
        }
        locationManager.subscribeForLocationUpdates(new SubscriptionSettings(useInBackground, purpose), convertedLocationListener);
        this.locationListeners.put(iLocationListener, convertedLocationListener);
    }

    @Override 
    public void removeLocationUpdates(final ILocationServiceProvider.ILocationListener iLocationListener) {
        AppUtils.ensureRunningOnUi(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$removeLocationUpdates$2(iLocationListener);
            }
        });
    }

    public void lambda$checkLocationSettings$3(Consumer consumer) {
        consumer.accept(Integer.valueOf(checkServices() ? 0 : 2));
    }

    @Override 
    public ILocationServiceProvider.IMapApiClient onCreateLocationServicesAPI(Context context, ILocationServiceProvider.IAPIConnectionCallbacks iAPIConnectionCallbacks, ILocationServiceProvider.IAPIOnConnectionFailedListener iAPIOnConnectionFailedListener) {
        return new YandexApiClientImpl(iAPIConnectionCallbacks, iAPIOnConnectionFailedListener);
    }

    @Override 
    public boolean checkServices() {
        return MapKitFactory.getInstance() != null && MapKitFactory.getInstance().isValid();
    }

    public void checkDisposal() {
        AppUtils.ensureRunningOnUi(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$checkDisposal$4();
            }
        });
    }

    public void lambda$connect$0() {
            YandexLocationProvider.this.getLocationManager().resume();
            this.connectionCallbacks.onConnected(null);
        }

        @Override 
        public void disconnect() {
            YandexLocationProvider.this.checkDisposal();
        }
    }
}
