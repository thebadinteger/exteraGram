package com.exteragram.messenger.maps.yandex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.location.Location;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import androidx.core.util.Consumer;
import com.exteragram.messenger.backup.InvisibleEncryptor;
import com.yandex.mapkit.Animation;
import com.yandex.mapkit.MapKitFactory;
import com.yandex.mapkit.ScreenPoint;
import com.yandex.mapkit.geometry.BoundingBox;
import com.yandex.mapkit.geometry.BoundingBoxHelper;
import com.yandex.mapkit.geometry.Circle;
import com.yandex.mapkit.geometry.Geometry;
import com.yandex.mapkit.geometry.Point;
import com.yandex.mapkit.layers.ObjectEvent;
import com.yandex.mapkit.logo.Alignment;
import com.yandex.mapkit.logo.HorizontalAlignment;
import com.yandex.mapkit.logo.Padding;
import com.yandex.mapkit.logo.VerticalAlignment;
import com.yandex.mapkit.map.CameraListener;
import com.yandex.mapkit.map.CameraPosition;
import com.yandex.mapkit.map.CameraUpdateReason;
import com.yandex.mapkit.map.CircleMapObject;
import com.yandex.mapkit.map.IconStyle;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapLoadStatistics;
import com.yandex.mapkit.map.MapLoadedListener;
import com.yandex.mapkit.map.MapObject;
import com.yandex.mapkit.map.MapObjectCollection;
import com.yandex.mapkit.map.MapObjectTapListener;
import com.yandex.mapkit.map.MapType;
import com.yandex.mapkit.map.PlacemarkMapObject;
import com.yandex.mapkit.mapview.MapView;
import com.yandex.mapkit.user_location.UserLocationLayer;
import com.yandex.mapkit.user_location.UserLocationObjectListener;
import com.yandex.mapkit.user_location.UserLocationView;
import com.yandex.runtime.image.ImageProvider;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ILocationServiceProvider;
import org.telegram.messenger.IMapsProvider;
import org.telegram.messenger.R;

public class YandexMapsProvider implements IMapsProvider {
    private static int activeMapViews;
    private static final String YANDEX_API_KEY = InvisibleEncryptor.decode("\u2001\u2002\u206a\u200c\u2000\u206e\u200c\u2000\u206e\u200f\u2000\u206e\u200f\u2000\u206a\u200b\u2000 \u206d\u2000 \u206b\u2000 \u206a\u2000 \u200b\u2000 \u206d\u2000\u206a\u200a\u2000 \u206c\u2000 \u206f\u2000 \u200b\u2000 \u206d\u2000\u206a\u200c\u2000\u206d\u206f\u2000 \u206f\u2000 \u200b\u2000\u206a\u200b\u2000\u206e\u200a\u2000\u206e\u200a\u2000 \u206f\u2000 \u200b\u2000\u206e\u200a\u2000\u206e\u200f\u2000 \u206f\u2000 \u206e\u2000 \u206f\u2000\u206a\u200b\u2000  \u2000\u206e\u200f\u2000\u206e\u200a\u2000\u206e\u200a\u2000 \u206a\u2000\u206a\u200b");
    private static boolean isKeySet = false;
    private static boolean isMapsInitialized = false;

    public static boolean isSupported() {
        return true;
    }

    @Override // org.telegram.messenger.IMapsProvider
    public boolean isApplicationRequired() {
        return false;
    }

    @Override // org.telegram.messenger.IMapsProvider
    public boolean supportsOtherMapTypes() {
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void acquireMapKit() {
        try {
            if (activeMapViews == 0) {
                MapKitFactory.getInstance().onStart();
            }
            activeMapViews++;
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static synchronized void releaseMapKit() {
        int i = activeMapViews;
        if (i == 0) {
            return;
        }
        int i2 = i - 1;
        activeMapViews = i2;
        if (i2 == 0) {
            MapKitFactory.getInstance().onStop();
        }
    }

    public static void initialize(Context context) {
        if (!isKeySet) {
            MapKitFactory.setApiKey(YANDEX_API_KEY);
            isKeySet = true;
        }
        if (isMapsInitialized) {
            return;
        }
        MapKitFactory.initialize(context);
        isMapsInitialized = true;
    }

    public static void terminate() {
        if (isMapsInitialized) {
            MapKitFactory.getInstance().onTerminate();
        }
    }

    @Override // org.telegram.messenger.IMapsProvider
    public void initializeMaps(Context context) {
        initialize(context);
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.IMapView onCreateMapView(Context context) {
        if (!isMapsInitialized) {
            initialize(context);
        }
        return new YandexMapView(context);
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.IMarkerOptions onCreateMarkerOptions() {
        return new YandexMarkerOptions();
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.ICircleOptions onCreateCircleOptions() {
        return new YandexCircleOptions();
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.ILatLngBoundsBuilder onCreateLatLngBoundsBuilder() {
        return new YandexLatLngBoundsBuilder();
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.ICameraUpdate newCameraUpdateLatLng(IMapsProvider.LatLng latLng) {
        return new YandexCameraUpdate(new Point(latLng.latitude, latLng.longitude));
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.ICameraUpdate newCameraUpdateLatLngZoom(IMapsProvider.LatLng latLng, float f) {
        return new YandexCameraUpdate(new Point(latLng.latitude, latLng.longitude), f);
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.ICameraUpdate newCameraUpdateLatLngBounds(IMapsProvider.ILatLngBounds iLatLngBounds, int i) {
        return new YandexCameraUpdate(((YandexLatLngBounds) iLatLngBounds).boundingBox, i);
    }

    @Override // org.telegram.messenger.IMapsProvider
    public IMapsProvider.IMapStyleOptions loadRawResourceStyle(Context context, int i) {
        return new YandexMapStyleOptions(i == R.raw.mapstyle_night);
    }

    @Override // org.telegram.messenger.IMapsProvider
    public String getMapsAppPackageName() {
        return "ru.yandex.yandexmaps";
    }

    @Override // org.telegram.messenger.IMapsProvider
    public int getInstallMapsString() {
        return R.string.InstallYandexMaps;
    }

    public static class YandexMapImpl implements IMapsProvider.IMap {
        private final CameraListener cameraListener;
        private ILocationServiceProvider.ILocationListener locationListener;
        private final MapObjectCollection mapObjects;
        private MapView mapView;
        private IMapsProvider.OnMarkerClickListener markerClickListener;
        private boolean moving;
        private Runnable onCameraIdleListener;
        private Runnable onCameraMoveListener;
        private IMapsProvider.OnCameraMoveStartedListener onCameraMoveStartedListener;
        private UserLocationLayer userLocationLayer;
        private final HashMap<CircleMapObject, YandexCircle> circles = new HashMap<>();
        private final HashMap<PlacemarkMapObject, YandexMarker> markers = new HashMap<>();
        private final LocationListener styleUpdater = new LocationListener();

        public YandexMapImpl(MapView mapView) {
            this.mapView = mapView;
            this.mapObjects = mapView.getMap().getMapObjects();
            final Map map = mapView.getMap();
            map.setScrollGesturesEnabled(true);
            map.setRotateGesturesEnabled(true);
            map.setZoomGesturesEnabled(true);
            map.getLogo().setAlignment(new Alignment(HorizontalAlignment.LEFT, VerticalAlignment.BOTTOM));
            map.getMapObjects().addTapListener(new MapObjectTapListener() { // from class: com.exteragram.messenger.maps.yandex.YandexMapsProvider$YandexMapImpl$$ExternalSyntheticLambda1
                @Override // com.yandex.mapkit.map.MapObjectTapListener
                public final boolean onMapObjectTap(MapObject mapObject, Point point) {
                    return YandexMapImpl.this.lambda$new$0(map, mapObject, point);
                }
            });
            CameraListener cameraListener = new CameraListener() { // from class: com.exteragram.messenger.maps.yandex.YandexMapsProvider$YandexMapImpl$$ExternalSyntheticLambda2
                @Override // com.yandex.mapkit.map.CameraListener
                public final void onCameraPositionChanged(Map map2, com.yandex.mapkit.map.CameraPosition cameraPosition, CameraUpdateReason cameraUpdateReason, boolean z) {
                    YandexMapImpl.this.lambda$new$1(map2, cameraPosition, cameraUpdateReason, z);
                }
            };
            this.cameraListener = cameraListener;
            map.addCameraListener(cameraListener);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$new$1(Map map, com.yandex.mapkit.map.CameraPosition cameraPosition, CameraUpdateReason cameraUpdateReason, boolean z) {
            if (!this.moving && !z) {
                int i = 1;
                this.moving = true;
                if (this.onCameraMoveStartedListener != null) {
                    int i2 = AnonymousClass1.$SwitchMap$com$yandex$mapkit$map$CameraUpdateReason[cameraUpdateReason.ordinal()];
                    if (i2 != 1) {
                        i = 2;
                        if (i2 != 2) {
                            throw new IncompatibleClassChangeError();
                        }
                    }
                    this.onCameraMoveStartedListener.onCameraMoveStarted(i);
                }
            }
            Runnable runnable = this.onCameraMoveListener;
            if (runnable != null) {
                runnable.run();
            }
            if (this.moving && z) {
                this.moving = false;
                Runnable runnable2 = this.onCameraIdleListener;
                if (runnable2 != null) {
                    runnable2.run();
                }
            }
        }

        public void onDestroy() {
            this.onCameraIdleListener = null;
            this.onCameraMoveStartedListener = null;
            this.onCameraMoveListener = null;
            this.markerClickListener = null;
            this.locationListener = null;
            this.mapView = null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* JADX INFO: renamed from: interceptObjectTap, reason: merged with bridge method [inline-methods] */
        public boolean lambda$new$0(Map map, MapObject mapObject, Point point) {
            if (!(mapObject instanceof PlacemarkMapObject)) {
                return false;
            }
            PlacemarkMapObject placemarkMapObject = (PlacemarkMapObject) mapObject;
            YandexMarker yandexMarker = this.markers.get(placemarkMapObject);
            if (yandexMarker == null) {
                yandexMarker = new YandexMarker(placemarkMapObject);
                this.markers.put(placemarkMapObject, yandexMarker);
            }
            IMapsProvider.OnMarkerClickListener onMarkerClickListener = this.markerClickListener;
            if (onMarkerClickListener == null) {
                return false;
            }
            return onMarkerClickListener.onClick(yandexMarker);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setMapType(int i) {
            if (i == 0) {
                this.mapView.getMap().setMapType(MapType.MAP);
            } else if (i == 1) {
                this.mapView.getMap().setMapType(MapType.SATELLITE);
            } else {
                if (i != 2) {
                    return;
                }
                this.mapView.getMap().setMapType(MapType.HYBRID);
            }
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void animateCamera(IMapsProvider.ICameraUpdate iCameraUpdate) {
            animateCamera(iCameraUpdate, 250, null);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void animateCamera(IMapsProvider.ICameraUpdate iCameraUpdate, IMapsProvider.ICancelableCallback iCancelableCallback) {
            animateCamera(iCameraUpdate, 1000, iCancelableCallback);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void animateCamera(IMapsProvider.ICameraUpdate iCameraUpdate, int i, final IMapsProvider.ICancelableCallback iCancelableCallback) {
            YandexCameraUpdate yandexCameraUpdate = (YandexCameraUpdate) iCameraUpdate;
            if (yandexCameraUpdate.point == null && yandexCameraUpdate.boundingBox == null) {
                if (iCancelableCallback != null) {
                    iCancelableCallback.onCancel();
                    return;
                }
                return;
            }
            Map.CameraCallback cameraCallback = iCancelableCallback == null ? null : new Map.CameraCallback() { // from class: com.exteragram.messenger.maps.yandex.YandexMapsProvider$YandexMapImpl$$ExternalSyntheticLambda0
                @Override // com.yandex.mapkit.map.Map.CameraCallback
                public final void onMoveFinished(boolean z) {
                    YandexMapsProvider.YandexMapImpl.$r8$lambda$HEFBRhUUZGjponxt4riLaZ6dWVo(iCancelableCallback, z);
                }
            };
            Point point = yandexCameraUpdate.point;
            MapView mapView = this.mapView;
            if (point != null) {
                mapView.getMap().move(new com.yandex.mapkit.map.CameraPosition(yandexCameraUpdate.point, yandexCameraUpdate.zoom != null ? yandexCameraUpdate.zoom.floatValue() : this.mapView.getMap().getCameraPosition().getZoom(), this.mapView.getMap().getCameraPosition().getAzimuth(), this.mapView.getMap().getCameraPosition().getTilt()), new Animation(Animation.Type.SMOOTH, i / 1000.0f), cameraCallback);
            } else {
                mapView.getMap().move(this.mapView.getMap().cameraPosition(Geometry.fromBoundingBox(yandexCameraUpdate.boundingBox)), new Animation(Animation.Type.SMOOTH, i / 1000.0f), cameraCallback);
            }
        }

        public static /* synthetic */ void $r8$lambda$HEFBRhUUZGjponxt4riLaZ6dWVo(IMapsProvider.ICancelableCallback iCancelableCallback, boolean z) {
            if (z) {
                iCancelableCallback.onFinish();
            } else {
                iCancelableCallback.onCancel();
            }
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void moveCamera(IMapsProvider.ICameraUpdate iCameraUpdate) {
            YandexCameraUpdate yandexCameraUpdate = (YandexCameraUpdate) iCameraUpdate;
            if (yandexCameraUpdate.point != null) {
                this.mapView.getMap().move(new com.yandex.mapkit.map.CameraPosition(yandexCameraUpdate.point, yandexCameraUpdate.zoom != null ? yandexCameraUpdate.zoom.floatValue() : this.mapView.getMap().getCameraPosition().getZoom(), this.mapView.getMap().getCameraPosition().getAzimuth(), this.mapView.getMap().getCameraPosition().getTilt()));
            } else if (yandexCameraUpdate.boundingBox != null) {
                this.mapView.getMap().move(this.mapView.getMap().cameraPosition(Geometry.fromBoundingBox(yandexCameraUpdate.boundingBox)));
            }
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public float getMaxZoomLevel() {
            return this.mapView.getMap().getCameraBounds().getMaxZoom();
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public float getMinZoomLevel() {
            return this.mapView.getMap().getCameraBounds().getMinZoom();
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setMyLocationEnabled(boolean z) {
            if (this.userLocationLayer == null && z) {
                UserLocationLayer userLocationLayerCreateUserLocationLayer = MapKitFactory.getInstance().createUserLocationLayer(this.mapView.getMapWindow());
                this.userLocationLayer = userLocationLayerCreateUserLocationLayer;
                userLocationLayerCreateUserLocationLayer.setAutoZoomEnabled(false);
                this.userLocationLayer.setHeadingModeActive(true);
                this.userLocationLayer.setObjectListener(this.styleUpdater);
            }
            UserLocationLayer userLocationLayer = this.userLocationLayer;
            if (userLocationLayer != null) {
                userLocationLayer.setVisible(z);
            }
            if (z) {
                return;
            }
            setOnMyLocationChangeListener(null);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public IMapsProvider.IUISettings getUiSettings() {
            return new YandexUISettings(this.mapView);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setOnCameraIdleListener(Runnable runnable) {
            this.onCameraIdleListener = runnable;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setOnCameraMoveStartedListener(IMapsProvider.OnCameraMoveStartedListener onCameraMoveStartedListener) {
            this.onCameraMoveStartedListener = onCameraMoveStartedListener;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public IMapsProvider.CameraPosition getCameraPosition() {
            com.yandex.mapkit.map.CameraPosition cameraPosition = this.mapView.getMap().getCameraPosition();
            return new IMapsProvider.CameraPosition(new IMapsProvider.LatLng(cameraPosition.getTarget().getLatitude(), cameraPosition.getTarget().getLongitude()), cameraPosition.getZoom());
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setOnMapLoadedCallback(final Runnable runnable) {
            this.mapView.getMap().setMapLoadedListener(new MapLoadedListener() { // from class: com.exteragram.messenger.maps.yandex.YandexMapsProvider$YandexMapImpl$$ExternalSyntheticLambda3
                @Override // com.yandex.mapkit.map.MapLoadedListener
                public final void onMapLoaded(MapLoadStatistics mapLoadStatistics) {
                    runnable.run();
                }
            });
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public IMapsProvider.IProjection getProjection() {
            return new YandexProjection(this.mapView);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public IMapsProvider.IMap.Padding getFragmentPadding(int i) {
            return new IMapsProvider.IMap.Padding(0, 0, 0, i);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setPadding(int i, int i2, int i3, int i4) {
            this.mapView.setPadding(0, i2, 0, i4);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setLogoPadding(int i, int i2) {
            this.mapView.getMap().getLogo().setPadding(new com.yandex.mapkit.logo.Padding(i, i2));
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setMapStyle(IMapsProvider.IMapStyleOptions iMapStyleOptions) {
            if (iMapStyleOptions == null) {
                this.mapView.getMap().setNightModeEnabled(false);
            } else {
                this.mapView.getMap().setNightModeEnabled(((YandexMapStyleOptions) iMapStyleOptions).isNightMode());
            }
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public IMapsProvider.IMarker addMarker(IMapsProvider.IMarkerOptions iMarkerOptions) {
            YandexMarkerOptions yandexMarkerOptions = (YandexMarkerOptions) iMarkerOptions;
            PlacemarkMapObject placemarkMapObjectAddPlacemark = this.mapObjects.addPlacemark(yandexMarkerOptions.position);
            if (yandexMarkerOptions.icon != null) {
                placemarkMapObjectAddPlacemark.setIcon(ImageProvider.fromBitmap(yandexMarkerOptions.icon));
            }
            IconStyle iconStyle = new IconStyle();
            iconStyle.setAnchor(new PointF(yandexMarkerOptions.anchorU, yandexMarkerOptions.anchorV));
            iconStyle.setFlat(Boolean.valueOf(yandexMarkerOptions.flat));
            placemarkMapObjectAddPlacemark.setIconStyle(iconStyle);
            YandexMarker yandexMarker = new YandexMarker(placemarkMapObjectAddPlacemark);
            this.markers.put(placemarkMapObjectAddPlacemark, yandexMarker);
            return yandexMarker;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public IMapsProvider.ICircle addCircle(IMapsProvider.ICircleOptions iCircleOptions) {
            YandexCircleOptions yandexCircleOptions = (YandexCircleOptions) iCircleOptions;
            CircleMapObject circleMapObjectAddCircle = this.mapObjects.addCircle(new Circle(yandexCircleOptions.center, (float) yandexCircleOptions.radius));
            circleMapObjectAddCircle.setStrokeColor(yandexCircleOptions.strokeColor);
            circleMapObjectAddCircle.setStrokeWidth(yandexCircleOptions.strokeWidth);
            circleMapObjectAddCircle.setFillColor(yandexCircleOptions.fillColor);
            YandexCircle yandexCircle = new YandexCircle(circleMapObjectAddCircle);
            this.circles.put(circleMapObjectAddCircle, yandexCircle);
            return yandexCircle;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setOnMyLocationChangeListener(Consumer<Location> consumer) {
            ILocationServiceProvider locationServiceProvider = ApplicationLoader.getLocationServiceProvider();
            if (locationServiceProvider instanceof YandexLocationProvider) {
                YandexLocationProvider yandexLocationProvider = (YandexLocationProvider) locationServiceProvider;
                ILocationServiceProvider.ILocationListener iLocationListener = this.locationListener;
                if (consumer == null) {
                    yandexLocationProvider.removeLocationUpdates(iLocationListener);
                    return;
                }
                if (iLocationListener != null) {
                    yandexLocationProvider.removeLocationUpdates(iLocationListener);
                }
                ILocationServiceProvider.ILocationListener listener = location -> consumer.accept(location);
                this.locationListener = listener;
                yandexLocationProvider.requestLocationUpdates(null, listener);
            }
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setOnMarkerClickListener(IMapsProvider.OnMarkerClickListener onMarkerClickListener) {
            this.markerClickListener = onMarkerClickListener;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMap
        public void setOnCameraMoveListener(Runnable runnable) {
            this.onCameraMoveListener = runnable;
        }

        public class LocationListener implements UserLocationObjectListener {
            public LocationListener() {
            }

            private boolean isNightMode() {
                return YandexMapImpl.this.mapView.getMap().isNightModeEnabled();
            }

            private void updateLocationIcon(UserLocationView userLocationView) {
                userLocationView.getArrow().setIcon(ImageProvider.fromResource(ApplicationLoader.applicationContext, R.drawable.map_pin_cone2));
                userLocationView.getAccuracyCircle().setStrokeColor(isNightMode() ? -1 : -16777216);
                userLocationView.getAccuracyCircle().setStrokeWidth(1.0f);
                userLocationView.getAccuracyCircle().setFillColor(isNightMode() ? 553648127 : 536870912);
                userLocationView.getPin().setIcon(ImageProvider.fromResource(ApplicationLoader.applicationContext, R.drawable.map_pin_circle));
            }

            @Override // com.yandex.mapkit.user_location.UserLocationObjectListener
            public void onObjectAdded(UserLocationView userLocationView) {
                updateLocationIcon(userLocationView);
            }

            @Override // com.yandex.mapkit.user_location.UserLocationObjectListener
            public void onObjectRemoved(UserLocationView userLocationView) {
                updateLocationIcon(userLocationView);
            }

            @Override // com.yandex.mapkit.user_location.UserLocationObjectListener
            public void onObjectUpdated(UserLocationView userLocationView, ObjectEvent objectEvent) {
                updateLocationIcon(userLocationView);
            }
        }

        public class YandexCircle implements IMapsProvider.ICircle {
            private final CircleMapObject circle;

            public YandexCircle(CircleMapObject circleMapObject) {
                this.circle = circleMapObject;
            }

            @Override // org.telegram.messenger.IMapsProvider.ICircle
            public void setStrokeColor(int i) {
                this.circle.setStrokeColor(i);
            }

            @Override // org.telegram.messenger.IMapsProvider.ICircle
            public void setFillColor(int i) {
                this.circle.setFillColor(i);
            }

            @Override // org.telegram.messenger.IMapsProvider.ICircle
            public double getRadius() {
                return this.circle.getGeometry().getRadius();
            }

            @Override // org.telegram.messenger.IMapsProvider.ICircle
            public void setRadius(double d) {
                CircleMapObject circleMapObject = this.circle;
                circleMapObject.setGeometry(new Circle(circleMapObject.getGeometry().getCenter(), (float) d));
            }

            @Override // org.telegram.messenger.IMapsProvider.ICircle
            public void setCenter(IMapsProvider.LatLng latLng) {
                this.circle.setGeometry(new Circle(new Point(latLng.latitude, latLng.longitude), this.circle.getGeometry().getRadius()));
            }

            @Override // org.telegram.messenger.IMapsProvider.ICircle
            public void remove() {
                this.circle.getParent().remove(this.circle);
                YandexMapImpl.this.circles.remove(this.circle);
            }
        }

        public class YandexMarker implements IMapsProvider.IMarker {
            private final PlacemarkMapObject placemark;

            public YandexMarker(PlacemarkMapObject placemarkMapObject) {
                this.placemark = placemarkMapObject;
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public Object getTag() {
                return this.placemark.getUserData();
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public void setTag(Object obj) {
                this.placemark.setUserData(obj);
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public IMapsProvider.LatLng getPosition() {
                return new IMapsProvider.LatLng(this.placemark.getGeometry().getLatitude(), this.placemark.getGeometry().getLongitude());
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public void setPosition(IMapsProvider.LatLng latLng) {
                this.placemark.setGeometry(new Point(latLng.latitude, latLng.longitude));
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public void setRotation(int i) {
                this.placemark.setDirection(i);
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public void setIcon(Bitmap bitmap) {
                this.placemark.setIcon(ImageProvider.fromBitmap(bitmap));
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public void setIcon(int i) {
                this.placemark.setIcon(ImageProvider.fromResource(ApplicationLoader.applicationContext, i));
            }

            @Override // org.telegram.messenger.IMapsProvider.IMarker
            public void remove() {
                this.placemark.getParent().remove(this.placemark);
                YandexMapImpl.this.markers.remove(this.placemark);
            }
        }
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.maps.yandex.YandexMapsProvider$1, reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$yandex$mapkit$map$CameraUpdateReason;

        static {
            int[] iArr = new int[CameraUpdateReason.values().length];
            $SwitchMap$com$yandex$mapkit$map$CameraUpdateReason = iArr;
            try {
                iArr[CameraUpdateReason.GESTURES.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$yandex$mapkit$map$CameraUpdateReason[CameraUpdateReason.APPLICATION.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public static class YandexMarkerOptions implements IMapsProvider.IMarkerOptions {
        private float anchorU = 0.5f;
        private float anchorV = 1.0f;
        private boolean flat;
        private Bitmap icon;
        private Point position;
        private String snippet;
        private String title;

        @Override // org.telegram.messenger.IMapsProvider.IMarkerOptions
        public IMapsProvider.IMarkerOptions position(IMapsProvider.LatLng latLng) {
            this.position = new Point(latLng.latitude, latLng.longitude);
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMarkerOptions
        public IMapsProvider.IMarkerOptions icon(Bitmap bitmap) {
            this.icon = bitmap;
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMarkerOptions
        public IMapsProvider.IMarkerOptions icon(int i) {
            this.icon = BitmapFactory.decodeResource(ApplicationLoader.applicationContext.getResources(), i);
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMarkerOptions
        public IMapsProvider.IMarkerOptions anchor(float f, float f2) {
            this.anchorU = f;
            this.anchorV = f2;
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMarkerOptions
        public IMapsProvider.IMarkerOptions title(String str) {
            this.title = str;
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMarkerOptions
        public IMapsProvider.IMarkerOptions snippet(String str) {
            this.snippet = str;
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMarkerOptions
        public IMapsProvider.IMarkerOptions flat(boolean z) {
            this.flat = z;
            return this;
        }
    }

    public static class YandexCircleOptions implements IMapsProvider.ICircleOptions {
        private Point center;
        private int fillColor;
        private double radius;
        private int strokeColor;
        private float strokeWidth;

        @Override // org.telegram.messenger.IMapsProvider.ICircleOptions
        public IMapsProvider.ICircleOptions strokePattern(List<IMapsProvider.PatternItem> list) {
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.ICircleOptions
        public IMapsProvider.ICircleOptions center(IMapsProvider.LatLng latLng) {
            this.center = new Point(latLng.latitude, latLng.longitude);
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.ICircleOptions
        public IMapsProvider.ICircleOptions radius(double d) {
            this.radius = d;
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.ICircleOptions
        public IMapsProvider.ICircleOptions strokeColor(int i) {
            this.strokeColor = i;
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.ICircleOptions
        public IMapsProvider.ICircleOptions fillColor(int i) {
            this.fillColor = i;
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.ICircleOptions
        public IMapsProvider.ICircleOptions strokeWidth(int i) {
            this.strokeWidth = i;
            return this;
        }
    }

    public static class YandexLatLngBoundsBuilder implements IMapsProvider.ILatLngBoundsBuilder {
        private BoundingBox boundingBox;

        @Override // org.telegram.messenger.IMapsProvider.ILatLngBoundsBuilder
        public IMapsProvider.ILatLngBoundsBuilder include(IMapsProvider.LatLng latLng) {
            BoundingBox boundingBox = this.boundingBox;
            this.boundingBox = boundingBox == null ? BoundingBoxHelper.getBounds(new Point(latLng.latitude, latLng.longitude)) : BoundingBoxHelper.getBounds(boundingBox, BoundingBoxHelper.getBounds(new Point(latLng.latitude, latLng.longitude)));
            return this;
        }

        @Override // org.telegram.messenger.IMapsProvider.ILatLngBoundsBuilder
        public IMapsProvider.ILatLngBounds build() {
            return new YandexLatLngBounds(this.boundingBox);
        }
    }

    public static class YandexLatLngBounds implements IMapsProvider.ILatLngBounds {
        private final BoundingBox boundingBox;

        public YandexLatLngBounds(BoundingBox boundingBox) {
            this.boundingBox = boundingBox;
        }

        @Override // org.telegram.messenger.IMapsProvider.ILatLngBounds
        public IMapsProvider.LatLng getCenter() {
            Point northEast = this.boundingBox.getNorthEast();
            Point southWest = this.boundingBox.getSouthWest();
            Point point = new Point((northEast.getLatitude() + southWest.getLatitude()) / 2.0d, (northEast.getLongitude() + southWest.getLongitude()) / 2.0d);
            return new IMapsProvider.LatLng(point.getLatitude(), point.getLongitude());
        }
    }

    public static class YandexProjection implements IMapsProvider.IProjection {
        private final MapView mapView;

        public YandexProjection(MapView mapView) {
            this.mapView = mapView;
        }

        @Override // org.telegram.messenger.IMapsProvider.IProjection
        public android.graphics.Point toScreenLocation(IMapsProvider.LatLng latLng) {
            ScreenPoint screenPointWorldToScreen = this.mapView.getMapWindow().worldToScreen(new Point(latLng.latitude, latLng.longitude));
            return new android.graphics.Point((int) screenPointWorldToScreen.getX(), (int) screenPointWorldToScreen.getY());
        }
    }

    public static class YandexUISettings implements IMapsProvider.IUISettings {
        @Override // org.telegram.messenger.IMapsProvider.IUISettings
        public void setCompassEnabled(boolean z) {
        }

        @Override // org.telegram.messenger.IMapsProvider.IUISettings
        public void setMyLocationButtonEnabled(boolean z) {
        }

        @Override // org.telegram.messenger.IMapsProvider.IUISettings
        public void setZoomControlsEnabled(boolean z) {
        }

        public YandexUISettings(MapView mapView) {
        }
    }

    public static class YandexCameraUpdate implements IMapsProvider.ICameraUpdate {
        private final BoundingBox boundingBox;
        private final Point point;
        private final Float zoom;

        public YandexCameraUpdate(Point point) {
            this.point = point;
            this.zoom = null;
            this.boundingBox = null;
        }

        public YandexCameraUpdate(Point point, float f) {
            this.point = point;
            this.zoom = Float.valueOf(f);
            this.boundingBox = null;
        }

        public YandexCameraUpdate(BoundingBox boundingBox, int i) {
            this.point = null;
            this.zoom = null;
            this.boundingBox = boundingBox;
        }
    }

    public static class YandexMapStyleOptions implements IMapsProvider.IMapStyleOptions {
        private final boolean nightMode;

        public YandexMapStyleOptions(boolean z) {
            this.nightMode = z;
        }

        public boolean isNightMode() {
            return this.nightMode;
        }
    }

    public class YandexMapView implements IMapsProvider.IMapView {
        private boolean destroyed;
        private IMapsProvider.ITouchInterceptor dispatchTouchInterceptor;
        private GLSurfaceView glSurfaceView;
        private IMapsProvider.ITouchInterceptor interceptTouchInterceptor;
        private YandexMapImpl mapImpl;
        private MapView mapView;
        private Runnable onLayoutListener;
        private boolean started;

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void onCreate(Bundle bundle) {
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void onLowMemory() {
        }

        public YandexMapView(Context context) {
            this.mapView = new CustomMapView(context);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public View getView() {
            return this.mapView;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void getMapAsync(final Consumer<IMapsProvider.IMap> consumer) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.maps.yandex.YandexMapsProvider$YandexMapView$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    YandexMapView.this.lambda$getMapAsync$0(consumer);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$getMapAsync$0(Consumer consumer) {
            MapView mapView;
            if (this.destroyed || (mapView = this.mapView) == null) {
                return;
            }
            this.mapImpl = new YandexMapImpl(mapView);
            findGlSurfaceView(this.mapView);
            consumer.accept(this.mapImpl);
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void onResume() {
            if (this.started || this.destroyed) {
                return;
            }
            YandexMapsProvider.acquireMapKit();
            try {
                this.mapView.onStart();
                this.started = true;
            } catch (Throwable th) {
                if (!this.started) {
                    YandexMapsProvider.releaseMapKit();
                }
                throw th;
            }
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void onPause() {
            stop();
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void onDestroy() {
            if (this.destroyed) {
                return;
            }
            try {
                stop();
            } catch (Throwable th) {
                FileLog.e(th);
            }
            this.destroyed = true;
            this.dispatchTouchInterceptor = null;
            this.interceptTouchInterceptor = null;
            this.onLayoutListener = null;
            YandexMapImpl yandexMapImpl = this.mapImpl;
            if (yandexMapImpl != null) {
                yandexMapImpl.setOnMyLocationChangeListener(null);
            }
            ILocationServiceProvider locationServiceProvider = ApplicationLoader.getLocationServiceProvider();
            if (locationServiceProvider instanceof YandexLocationProvider) {
                ((YandexLocationProvider) locationServiceProvider).checkDisposal();
            }
            YandexMapImpl yandexMapImpl2 = this.mapImpl;
            if (yandexMapImpl2 != null) {
                yandexMapImpl2.onDestroy();
                this.mapImpl = null;
            }
            this.glSurfaceView = null;
            this.mapView = null;
        }

        private void stop() {
            if (this.started) {
                try {
                    this.mapView.onStop();
                } finally {
                    this.started = false;
                    YandexMapsProvider.releaseMapKit();
                }
            }
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void setOnDispatchTouchEventInterceptor(IMapsProvider.ITouchInterceptor iTouchInterceptor) {
            this.dispatchTouchInterceptor = iTouchInterceptor;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void setOnInterceptTouchEventInterceptor(IMapsProvider.ITouchInterceptor iTouchInterceptor) {
            this.interceptTouchInterceptor = iTouchInterceptor;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public void setOnLayoutListener(Runnable runnable) {
            this.onLayoutListener = runnable;
        }

        @Override // org.telegram.messenger.IMapsProvider.IMapView
        public GLSurfaceView getGlSurfaceView() {
            return this.glSurfaceView;
        }

        private void findGlSurfaceView(View view) {
            if (view instanceof GLSurfaceView) {
                this.glSurfaceView = (GLSurfaceView) view;
            }
            if (view instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) view;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    findGlSurfaceView(viewGroup.getChildAt(i));
                }
            }
        }

        public class CustomMapView extends MapView {
            public CustomMapView(Context context) {
                super(context);
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (YandexMapView.this.dispatchTouchInterceptor != null) {
                    return YandexMapView.this.dispatchTouchInterceptor.onInterceptTouchEvent(motionEvent, new IMapsProvider.ICallableMethod() { // from class: com.exteragram.messenger.maps.yandex.YandexMapsProvider$YandexMapView$CustomMapView$$ExternalSyntheticLambda1
                        @Override // org.telegram.messenger.IMapsProvider.ICallableMethod
                        public final Object call(Object obj) {
                            return CustomMapView.this.lambda$dispatchTouchEvent$0((MotionEvent) obj);
                        }
                    });
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ Boolean lambda$dispatchTouchEvent$0(MotionEvent motionEvent) {
                return Boolean.valueOf(super.dispatchTouchEvent(motionEvent));
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (YandexMapView.this.interceptTouchInterceptor != null) {
                    return YandexMapView.this.interceptTouchInterceptor.onInterceptTouchEvent(motionEvent, new IMapsProvider.ICallableMethod() { // from class: com.exteragram.messenger.maps.yandex.YandexMapsProvider$YandexMapView$CustomMapView$$ExternalSyntheticLambda0
                        @Override // org.telegram.messenger.IMapsProvider.ICallableMethod
                        public final Object call(Object obj) {
                            return CustomMapView.this.lambda$onInterceptTouchEvent$1((MotionEvent) obj);
                        }
                    });
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ Boolean lambda$onInterceptTouchEvent$1(MotionEvent motionEvent) {
                return Boolean.valueOf(super.onInterceptTouchEvent(motionEvent));
            }

            @Override // android.widget.RelativeLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                if (YandexMapView.this.onLayoutListener != null) {
                    YandexMapView.this.onLayoutListener.run();
                }
            }
        }
    }
}
