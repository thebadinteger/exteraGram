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

    /* JADX INFO: Access modifiers changed from: private */
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

    @Override // org.telegram.messenger.ILocationServiceProvider
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

    @Override // org.telegram.messenger.ILocationServiceProvider
    public ILocationServiceProvider.ILocationRequest onCreateLocationRequest() {
        return new YandexLocationRequest();
    }

    @Override // org.telegram.messenger.ILocationServiceProvider
    public void getLastLocation(final Consumer<Location> consumer) {
        AppUtils.ensureRunningOnUi(new Runnable() { // from class: com.exteragram.messenger.maps.yandex.YandexLocationProvider$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                YandexLocationProvider.this.lambda$getLastLocation$0(consumer);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$getLastLocation$0(Consumer consumer) {
        LocationManager locationManager = getLocationManager();
        Objects.requireNonNull(consumer);
        locationManager.requestSingleUpdate(new ConvertedLocationListener(consumer::accept));
    }

    @Override // org.telegram.messenger.ILocationServiceProvider
    public void requestLocationUpdates(final ILocationServiceProvider.ILocationRequest iLocationRequest, final ILocationServiceProvider.ILocationListener iLocationListener) {
        AppUtils.ensureRunningOnUi(new Runnable() { // from class: com.exteragram.messenger.maps.yandex.YandexLocationProvider$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                YandexLocationProvider.this.lambda$requestLocationUpdates$1(iLocationListener, iLocationRequest);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestLocationUpdates$1(ILocationServiceProvider.ILocationListener iLocationListener, ILocationServiceProvider.ILocationRequest iLocationRequest) {
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

    @Override // org.telegram.messenger.ILocationServiceProvider
    public void removeLocationUpdates(final ILocationServiceProvider.ILocationListener iLocationListener) {
        AppUtils.ensureRunningOnUi(new Runnable() { // from class: com.exteragram.messenger.maps.yandex.YandexLocationProvider$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                YandexLocationProvider.this.lambda$removeLocationUpdates$2(iLocationListener);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$removeLocationUpdates$2(ILocationServiceProvider.ILocationListener iLocationListener) {
        LocationListener locationListener = this.locationListeners.get(iLocationListener);
        if (locationListener == null) {
            return;
        }
        this.locationListeners.remove(iLocationListener);
        getLocationManager().unsubscribe(locationListener);
    }

    @Override // org.telegram.messenger.ILocationServiceProvider
    public void checkLocationSettings(ILocationServiceProvider.ILocationRequest iLocationRequest, final Consumer<Integer> consumer) {
        AppUtils.ensureRunningOnUi(new Runnable() { // from class: com.exteragram.messenger.maps.yandex.YandexLocationProvider$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                YandexLocationProvider.this.lambda$checkLocationSettings$3(consumer);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkLocationSettings$3(Consumer consumer) {
        consumer.accept(Integer.valueOf(checkServices() ? 0 : 2));
    }

    @Override // org.telegram.messenger.ILocationServiceProvider
    public ILocationServiceProvider.IMapApiClient onCreateLocationServicesAPI(Context context, ILocationServiceProvider.IAPIConnectionCallbacks iAPIConnectionCallbacks, ILocationServiceProvider.IAPIOnConnectionFailedListener iAPIOnConnectionFailedListener) {
        return new YandexApiClientImpl(iAPIConnectionCallbacks, iAPIOnConnectionFailedListener);
    }

    @Override // org.telegram.messenger.ILocationServiceProvider
    public boolean checkServices() {
        return MapKitFactory.getInstance() != null && MapKitFactory.getInstance().isValid();
    }

    public void checkDisposal() {
        AppUtils.ensureRunningOnUi(new Runnable() { // from class: com.exteragram.messenger.maps.yandex.YandexLocationProvider$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                YandexLocationProvider.this.lambda$checkDisposal$4();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkDisposal$4() {
        LocationManager locationManager;
        if (!this.locationListeners.isEmpty() || (locationManager = this.locationManager) == null) {
            return;
        }
        locationManager.suspend();
        this.locationManager = null;
    }

    public static class ConvertedLocationListener implements LocationListener {
        private final ILocationServiceProvider.ILocationListener listener;

        @Override // com.yandex.mapkit.location.LocationListener
        public void onLocationStatusUpdated(LocationStatus locationStatus) {
        }

        public ConvertedLocationListener(ILocationServiceProvider.ILocationListener iLocationListener) {
            this.listener = iLocationListener;
        }

        @Override // com.yandex.mapkit.location.LocationListener
        public void onLocationUpdated(com.yandex.mapkit.location.Location location) {
            this.listener.onLocationChanged(YandexLocationProvider.convertYandexLocationToAndroid(location));
        }
    }

    public static final class YandexLocationRequest implements ILocationServiceProvider.ILocationRequest {
        private Purpose purpose = Purpose.GENERAL;

        @Override // org.telegram.messenger.ILocationServiceProvider.ILocationRequest
        public void setFastestInterval(long j) {
        }

        @Override // org.telegram.messenger.ILocationServiceProvider.ILocationRequest
        public void setInterval(long j) {
        }

        @Override // org.telegram.messenger.ILocationServiceProvider.ILocationRequest
        public void setPriority(int i) {
            if (i == 0) {
                this.purpose = Purpose.PEDESTRIAN_NAVIGATION;
            } else if (i == 1 || i == 2 || i == 3) {
                this.purpose = Purpose.GENERAL;
            }
        }
    }

    public final class YandexApiClientImpl implements ILocationServiceProvider.IMapApiClient {
        private final ILocationServiceProvider.IAPIConnectionCallbacks connectionCallbacks;
        private final ILocationServiceProvider.IAPIOnConnectionFailedListener failedListener;

        private YandexApiClientImpl(ILocationServiceProvider.IAPIConnectionCallbacks iAPIConnectionCallbacks, ILocationServiceProvider.IAPIOnConnectionFailedListener iAPIOnConnectionFailedListener) {
            this.connectionCallbacks = iAPIConnectionCallbacks;
            this.failedListener = iAPIOnConnectionFailedListener;
        }

        @Override // org.telegram.messenger.ILocationServiceProvider.IMapApiClient
        public void connect() {
            AppUtils.ensureRunningOnUi(new Runnable() { // from class: com.exteragram.messenger.maps.yandex.YandexLocationProvider$YandexApiClientImpl$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    YandexApiClientImpl.this.lambda$connect$0();
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$connect$0() {
            YandexLocationProvider.this.getLocationManager().resume();
            this.connectionCallbacks.onConnected(null);
        }

        @Override // org.telegram.messenger.ILocationServiceProvider.IMapApiClient
        public void disconnect() {
            YandexLocationProvider.this.checkDisposal();
        }
    }
}
