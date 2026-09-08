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

    @Override 
    public boolean isApplicationRequired() {
        return false;
    }

    @Override 
    public boolean supportsOtherMapTypes() {
        return false;
    }

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

    @Override 
    public void initializeMaps(Context context) {
        initialize(context);
    }

    @Override 
    public IMapsProvider.IMapView onCreateMapView(Context context) {
        if (!isMapsInitialized) {
            initialize(context);
        }
        return new YandexMapView(context);
    }

    @Override 
    public IMapsProvider.IMarkerOptions onCreateMarkerOptions() {
        return new YandexMarkerOptions();
    }

    @Override 
    public IMapsProvider.ICircleOptions onCreateCircleOptions() {
        return new YandexCircleOptions();
    }

    @Override 
    public IMapsProvider.ILatLngBoundsBuilder onCreateLatLngBoundsBuilder() {
        return new YandexLatLngBoundsBuilder();
    }

    @Override 
    public IMapsProvider.ICameraUpdate newCameraUpdateLatLng(IMapsProvider.LatLng latLng) {
        return new YandexCameraUpdate(new Point(latLng.latitude, latLng.longitude));
    }

    @Override 
    public IMapsProvider.ICameraUpdate newCameraUpdateLatLngZoom(IMapsProvider.LatLng latLng, float f) {
        return new YandexCameraUpdate(new Point(latLng.latitude, latLng.longitude), f);
    }

    @Override 
    public IMapsProvider.ICameraUpdate newCameraUpdateLatLngBounds(IMapsProvider.ILatLngBounds iLatLngBounds, int i) {
        return new YandexCameraUpdate(((YandexLatLngBounds) iLatLngBounds).boundingBox, i);
    }

    @Override 
    public IMapsProvider.IMapStyleOptions loadRawResourceStyle(Context context, int i) {
        return new YandexMapStyleOptions(i == R.raw.mapstyle_night);
    }

    @Override 
    public String getMapsAppPackageName() {
        return "ru.yandex.yandexmaps";
    }

    @Override 
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
            map.getMapObjects().addTapListener(new MapObjectTapListener() { 
                @Override // com.yandex.mapkit.map.MapObjectTapListener
                public final boolean onMapObjectTap(MapObject mapObject, Point point) {
                    return this.f$0.lambda$new$0(map, mapObject, point);
                }
            });
            CameraListener cameraListener = new CameraListener() { 
                @Override // com.yandex.mapkit.map.CameraListener
                public final void onCameraPositionChanged(Map map2, CameraPosition cameraPosition, CameraUpdateReason cameraUpdateReason, boolean z) {
                    this.f$0.lambda$new$1(map2, cameraPosition, cameraUpdateReason, z);
                }
            };
            this.cameraListener = cameraListener;
            map.addCameraListener(cameraListener);
        }

        public void $r8$lambda$HEFBRhUUZGjponxt4riLaZ6dWVo(IMapsProvider.ICancelableCallback iCancelableCallback, boolean z) {
            if (z) {
                iCancelableCallback.onFinish();
            } else {
                iCancelableCallback.onCancel();
            }
        }

        @Override 
        public void moveCamera(IMapsProvider.ICameraUpdate iCameraUpdate) {
            YandexCameraUpdate yandexCameraUpdate = (YandexCameraUpdate) iCameraUpdate;
            if (yandexCameraUpdate.point != null) {
                this.mapView.getMap().move(new CameraPosition(yandexCameraUpdate.point, yandexCameraUpdate.zoom != null ? yandexCameraUpdate.zoom.floatValue() : this.mapView.getMap().getCameraPosition().getZoom(), this.mapView.getMap().getCameraPosition().getAzimuth(), this.mapView.getMap().getCameraPosition().getTilt()));
            } else if (yandexCameraUpdate.boundingBox != null) {
                this.mapView.getMap().move(this.mapView.getMap().cameraPosition(Geometry.fromBoundingBox(yandexCameraUpdate.boundingBox)));
            }
        }

        @Override 
        public float getMaxZoomLevel() {
            return this.mapView.getMap().getCameraBounds().getMaxZoom();
        }

        @Override 
        public float getMinZoomLevel() {
            return this.mapView.getMap().getCameraBounds().getMinZoom();
        }

        @Override 
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

        @Override 
        public IMapsProvider.IUISettings getUiSettings() {
            return new YandexUISettings(this.mapView);
        }

        @Override 
        public void setOnCameraIdleListener(Runnable runnable) {
            this.onCameraIdleListener = runnable;
        }

        @Override 
        public void setOnCameraMoveStartedListener(IMapsProvider.OnCameraMoveStartedListener onCameraMoveStartedListener) {
            this.onCameraMoveStartedListener = onCameraMoveStartedListener;
        }

        @Override 
        public IMapsProvider.CameraPosition getCameraPosition() {
            CameraPosition cameraPosition = this.mapView.getMap().getCameraPosition();
            return new IMapsProvider.CameraPosition(new IMapsProvider.LatLng(cameraPosition.getTarget().getLatitude(), cameraPosition.getTarget().getLongitude()), cameraPosition.getZoom());
        }

        @Override 
        public void setOnMapLoadedCallback(final Runnable runnable) {
            this.mapView.getMap().setMapLoadedListener(new MapLoadedListener() { 
                @Override // com.yandex.mapkit.map.MapLoadedListener
                public final void onMapLoaded(MapLoadStatistics mapLoadStatistics) {
                    runnable.run();
                }
            });
        }

        @Override 
        public IMapsProvider.IProjection getProjection() {
            return new YandexProjection(this.mapView);
        }

        @Override 
        public IMapsProvider.IMap.Padding getFragmentPadding(int i) {
            return new IMapsProvider.IMap.Padding(0, 0, 0, i);
        }

        @Override 
        public void setPadding(int i, int i2, int i3, int i4) {
            this.mapView.setPadding(0, i2, 0, i4);
        }

        @Override 
        public void setLogoPadding(int i, int i2) {
            this.mapView.getMap().getLogo().setPadding(new Padding(i, i2));
        }

        @Override 
        public void setMapStyle(IMapsProvider.IMapStyleOptions iMapStyleOptions) {
            if (iMapStyleOptions == null) {
                this.mapView.getMap().setNightModeEnabled(false);
            } else {
                this.mapView.getMap().setNightModeEnabled(((YandexMapStyleOptions) iMapStyleOptions).isNightMode());
            }
        }

        @Override 
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

        @Override 
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

        @Override 
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
                YandexLocationProvider$$ExternalSyntheticLambda5 yandexLocationProvider$$ExternalSyntheticLambda5 = new YandexLocationProvider$$ExternalSyntheticLambda5(consumer);
                this.locationListener = yandexLocationProvider$$ExternalSyntheticLambda5;
                yandexLocationProvider.requestLocationUpdates(null, yandexLocationProvider$$ExternalSyntheticLambda5);
            }
        }

        @Override 
        public void setOnMarkerClickListener(IMapsProvider.OnMarkerClickListener onMarkerClickListener) {
            this.markerClickListener = onMarkerClickListener;
        }

        @Override 
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

            @Override 
            public void setStrokeColor(int i) {
                this.circle.setStrokeColor(i);
            }

            @Override 
            public void setFillColor(int i) {
                this.circle.setFillColor(i);
            }

            @Override 
            public double getRadius() {
                return this.circle.getGeometry().getRadius();
            }

            @Override 
            public void setRadius(double d) {
                CircleMapObject circleMapObject = this.circle;
                circleMapObject.setGeometry(new Circle(circleMapObject.getGeometry().getCenter(), (float) d));
            }

            @Override 
            public void setCenter(IMapsProvider.LatLng latLng) {
                this.circle.setGeometry(new Circle(new Point(latLng.latitude, latLng.longitude), this.circle.getGeometry().getRadius()));
            }

            @Override 
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

            @Override 
            public Object getTag() {
                return this.placemark.getUserData();
            }

            @Override 
            public void setTag(Object obj) {
                this.placemark.setUserData(obj);
            }

            @Override 
            public IMapsProvider.LatLng getPosition() {
                return new IMapsProvider.LatLng(this.placemark.getGeometry().getLatitude(), this.placemark.getGeometry().getLongitude());
            }

            @Override 
            public void setPosition(IMapsProvider.LatLng latLng) {
                this.placemark.setGeometry(new Point(latLng.latitude, latLng.longitude));
            }

            @Override 
            public void setRotation(int i) {
                this.placemark.setDirection(i);
            }

            @Override 
            public void setIcon(Bitmap bitmap) {
                this.placemark.setIcon(ImageProvider.fromBitmap(bitmap));
            }

            @Override 
            public void setIcon(int i) {
                this.placemark.setIcon(ImageProvider.fromResource(ApplicationLoader.applicationContext, i));
            }

            @Override 
            public void remove() {
                this.placemark.getParent().remove(this.placemark);
                YandexMapImpl.this.markers.remove(this.placemark);
            }
        }
    }

    public static void lambda$getMapAsync$0(Consumer consumer) {
            MapView mapView;
            if (this.destroyed || (mapView = this.mapView) == null) {
                return;
            }
            this.mapImpl = new YandexMapImpl(mapView);
            findGlSurfaceView(this.mapView);
            consumer.accept(this.mapImpl);
        }

        @Override 
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

        @Override 
        public void onPause() {
            stop();
        }

        @Override 
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

        @Override 
        public void setOnDispatchTouchEventInterceptor(IMapsProvider.ITouchInterceptor iTouchInterceptor) {
            this.dispatchTouchInterceptor = iTouchInterceptor;
        }

        @Override 
        public void setOnInterceptTouchEventInterceptor(IMapsProvider.ITouchInterceptor iTouchInterceptor) {
            this.interceptTouchInterceptor = iTouchInterceptor;
        }

        @Override 
        public void setOnLayoutListener(Runnable runnable) {
            this.onLayoutListener = runnable;
        }

        @Override 
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
                    return YandexMapView.this.dispatchTouchInterceptor.onInterceptTouchEvent(motionEvent, new IMapsProvider.ICallableMethod() { 
                        @Override 
                        public final Object call(Object obj) {
                            return this.f$0.lambda$dispatchTouchEvent$0((MotionEvent) obj);
                        }
                    });
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            public Boolean lambda$onInterceptTouchEvent$1(MotionEvent motionEvent) {
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
