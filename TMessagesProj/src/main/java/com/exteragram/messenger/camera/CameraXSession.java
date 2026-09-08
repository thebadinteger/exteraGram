package com.exteragram.messenger.camera;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.hardware.display.DisplayManager;
import android.os.Build;
import android.os.Looper;
import android.util.Range;
import android.util.Size;
import android.view.Display;
import android.view.Surface;
import androidx.camera.camera2.interop.Camera2CameraInfo;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraControl;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraState;
import androidx.camera.core.ConcurrentCamera;
import androidx.camera.core.DisplayOrientedMeteringPointFactory;
import androidx.camera.core.FocusMeteringAction;
import androidx.camera.core.Preview;
import androidx.camera.core.SessionConfig;
import androidx.camera.core.SurfaceRequest;
import androidx.camera.core.UseCaseGroup;
import androidx.camera.core.ZoomState;
import androidx.camera.core.resolutionselector.AspectRatioStrategy;
import androidx.camera.core.resolutionselector.ResolutionFilter;
import androidx.camera.core.resolutionselector.ResolutionSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.p003lifecycle.Lifecycle;
import androidx.p003lifecycle.LifecycleOwner;
import androidx.p003lifecycle.LifecycleRegistry;
import androidx.p003lifecycle.LiveData;
import androidx.p003lifecycle.Observer;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.utils.system.SystemUtils;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.Stories.recorder.DualCameraView;

public class CameraXSession {
    private static volatile Boolean seamlessSwitchingAvailableCache;
    Camera camera;
    Camera cameraBack;
    private CameraControl cameraControl;
    private CameraControl cameraControlBack;
    private CameraControl cameraControlFront;
    Camera cameraFront;
    private CameraSelector cameraSelector;
    private boolean isFrontface;
    private final CameraLifecycle lifecycle;
    private Preview previewUseCase;
    private Preview previewUseCaseBack;
    ProcessCameraProvider provider;
    private final Preview.SurfaceProvider surfaceProviderPrimary;
    private Preview.SurfaceProvider surfaceProviderSecondary;
    private static final Map<CameraSelector, Boolean> STABILIZATION_SUPPORT_CACHE = new ConcurrentHashMap();
    private static final Range<Integer> FPS_60_RANGE = new Range<>(60, 60);
    private static final Range<Integer> FPS_30_RANGE = new Range<>(30, 30);
    private static final Size FALLBACK_SENSOR_ASPECT = new Size(4, 3);
    private boolean isInitiated = false;
    private boolean isDualMode = false;
    private boolean isBinding = false;
    private volatile int recordingFrameRate = 30;
    private boolean isTorchOn = false;

    public interface PreviewSizeListener {
        void onPreviewSize(int i, int i2);
    }

    public static class CameraLifecycle implements LifecycleOwner {
        private final LifecycleRegistry lifecycleRegistry;

        public CameraLifecycle() {
            LifecycleRegistry lifecycleRegistry = new LifecycleRegistry(this);
            this.lifecycleRegistry = lifecycleRegistry;
            lifecycleRegistry.setCurrentState(Lifecycle.State.CREATED);
        }

        public void start() {
            try {
                if (this.lifecycleRegistry.getState() != Lifecycle.State.DESTROYED) {
                    this.lifecycleRegistry.setCurrentState(Lifecycle.State.RESUMED);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void stop() {
            try {
                this.lifecycleRegistry.setCurrentState(Lifecycle.State.DESTROYED);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.p003lifecycle.LifecycleOwner
        public Lifecycle getLifecycle() {
            return this.lifecycleRegistry;
        }
    }

    public CameraXSession(CameraLifecycle cameraLifecycle, Preview.SurfaceProvider surfaceProvider) {
        this.lifecycle = cameraLifecycle;
        this.surfaceProviderPrimary = surfaceProvider;
    }

    public static Preview.SurfaceProvider createSurfaceProvider(final Context context, final SurfaceTexture surfaceTexture, final PreviewSizeListener previewSizeListener) {
        return new Preview.SurfaceProvider() { 
            @Override // androidx.camera.core.Preview.SurfaceProvider
            public final void onSurfaceRequested(SurfaceRequest surfaceRequest) {
                CameraXSession.$r8$lambda$293GI9xWXKNG5kPKaSxWt6Uy9f4(previewSizeListener, context, surfaceTexture, surfaceRequest);
            }
        };
    }

    public static void $r8$lambda$TRled3lKaEUY2eaet8nSIa2avwg(Size size, PreviewSizeListener previewSizeListener, SurfaceRequest.TransformationInfo transformationInfo) {
        Rect cropRect = transformationInfo.getCropRect();
        previewSizeListener.onPreviewSize(cropRect.width() > 0 ? cropRect.width() : size.getWidth(), cropRect.height() > 0 ? cropRect.height() : size.getHeight());
    }

    public static public void $r8$lambda$rb0Pnzaeyz85DEsbXVziDkXE8Vk(CameraState cameraState) {
        CameraState.StateError error = cameraState.getError();
        if (error != null) {
            FileLog.e("CameraX camera state error: code=" + error.getCode() + " type=" + cameraState.getType());
            if (error.getCause() != null) {
                FileLog.e(error.getCause());
            }
        }
    }

    private void applyInitialZoom(final Camera camera, final CameraControl cameraControl) {
        if (cameraControl == null || camera == null) {
            return;
        }
        cameraControl.setZoomRatio(1.0f);
        if (wantsWideAngleStart(camera)) {
            final LiveData<ZoomState> zoomState = camera.getCameraInfo().getZoomState();
            if (zoomState.getValue() != null) {
                applyWideAngle(cameraControl, zoomState.getValue());
            } else {
                zoomState.observe(this.lifecycle, new Observer<ZoomState>() { 
                    @Override // androidx.p003lifecycle.Observer
                    public void onChanged(ZoomState zoomState2) {
                        if (zoomState2 == null) {
                            return;
                        }
                        zoomState.removeObserver(this);
                        if (CameraXSession.this.wantsWideAngleStart(camera)) {
                            CameraXSession.this.applyWideAngle(cameraControl, zoomState2);
                        }
                    }
                });
            }
        }
    }

    public void applyWideAngle(CameraControl cameraControl, ZoomState zoomState) {
        if (zoomState == null || zoomState.getMinZoomRatio() >= 1.0f) {
            return;
        }
        cameraControl.setZoomRatio(zoomState.getMinZoomRatio());
    }

    public boolean wantsWideAngleStart(Camera camera) {
        return ExteraConfig.getStartWithWideAngleCamera() && camera != null && camera.getCameraInfo().getLensFacing() == 1;
    }

    public float getLinearZoom() {
        ZoomState value;
        Camera camera = this.camera;
        if (camera == null || (value = camera.getCameraInfo().getZoomState().getValue()) == null) {
            return 0.0f;
        }
        return value.getLinearZoom();
    }

    public float getZoomRatio() {
        ZoomState value;
        Camera camera = this.camera;
        if (camera == null || (value = camera.getCameraInfo().getZoomState().getValue()) == null) {
            return 1.0f;
        }
        return value.getZoomRatio();
    }

    public void setZoomRatio(float f) {
        CameraControl cameraControl = this.cameraControl;
        if (cameraControl == null) {
            return;
        }
        cameraControl.setZoomRatio(f);
    }

    public float getMinZoomRatio() {
        ZoomState value;
        Camera camera = this.camera;
        if (camera == null || (value = camera.getCameraInfo().getZoomState().getValue()) == null) {
            return 1.0f;
        }
        return value.getMinZoomRatio();
    }

    public float getMaxZoomRatio() {
        ZoomState value;
        Camera camera = this.camera;
        if (camera == null || (value = camera.getCameraInfo().getZoomState().getValue()) == null) {
            return 1.0f;
        }
        return value.getMaxZoomRatio();
    }

    public void focusToPoint(float f, float f2, float f3, float f4) {
        Display defaultDisplay;
        if (this.cameraControl == null || this.camera == null || f3 <= 0.0f || f4 <= 0.0f || (defaultDisplay = getDefaultDisplay()) == null) {
            return;
        }
        try {
            this.cameraControl.startFocusAndMetering(new FocusMeteringAction.Builder(new DisplayOrientedMeteringPointFactory(defaultDisplay, this.camera.getCameraInfo(), f3, f4).createPoint(f, f2), 3).build());
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public int getDisplayOrientation() {
        Display defaultDisplay = getDefaultDisplay();
        int rotation = defaultDisplay != null ? defaultDisplay.getRotation() : 0;
        if (rotation == 1) {
            return 90;
        }
        if (rotation != 2) {
            return rotation != 3 ? 0 : 270;
        }
        return 180;
    }

    private Size getSensorAspect(CameraSelector cameraSelector) {
        Rect rect;
        try {
            ProcessCameraProvider processCameraProvider = this.provider;
            if (processCameraProvider != null && cameraSelector != null && processCameraProvider.hasCamera(cameraSelector) && (rect = (Rect) Camera2CameraInfo.from(this.provider.getCameraInfo(cameraSelector)).getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)) != null && rect.width() > 0 && rect.height() > 0) {
                return new Size(rect.width(), rect.height());
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return FALLBACK_SENSOR_ASPECT;
    }

    private Set<Size> get60FpsCapableSizes(CameraSelector cameraSelector) {
        HashSet hashSet = new HashSet();
        try {
            ProcessCameraProvider processCameraProvider = this.provider;
            if (processCameraProvider != null && cameraSelector != null && processCameraProvider.hasCamera(cameraSelector)) {
                StreamConfigurationMap streamConfigurationMap = (StreamConfigurationMap) Camera2CameraInfo.from(this.provider.getCameraInfo(cameraSelector)).getCameraCharacteristic(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Size[] outputSizes = streamConfigurationMap != null ? streamConfigurationMap.getOutputSizes(SurfaceTexture.class) : null;
                if (outputSizes != null) {
                    for (Size size : outputSizes) {
                        long outputMinFrameDuration = streamConfigurationMap.getOutputMinFrameDuration(SurfaceTexture.class, size);
                        if (outputMinFrameDuration > 0 && outputMinFrameDuration <= 16666667) {
                            hashSet.add(size);
                        }
                    }
                }
            }
            return hashSet;
        } catch (Exception e) {
            FileLog.e(e);
            return hashSet;
        }
    }

    public static List<Size> sortRoundPreviewSizes(List<Size> list, Size size, final Set<Size> set) throws Throwable {
        ArrayList arrayList = new ArrayList(list);
        final int roundVideoResolution = SystemUtils.getRoundVideoResolution();
        final int i = roundVideoResolution * 2;
        final int iMin = Math.min(size.getWidth(), size.getHeight());
        final int iMax = Math.max(size.getWidth(), size.getHeight());
        arrayList.sort(new Comparator() { 
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return CameraXSession.compareRoundPreviewSizes((Size) obj, (Size) obj2, roundVideoResolution, i, iMin, iMax, set);
            }
        });
        return arrayList;
    }

    public static int compareRoundPreviewSizes(Size size, Size size2, int i, int i2, int i3, int i4, Set<Size> set) {
        int iCompare;
        int iCompare2;
        int iMin = Math.min(size.getWidth(), size.getHeight());
        int iMax = Math.max(size.getWidth(), size.getHeight());
        int iMin2 = Math.min(size2.getWidth(), size2.getHeight());
        int iMax2 = Math.max(size2.getWidth(), size2.getHeight());
        boolean z = iMin >= i;
        if (z != (iMin2 >= i)) {
            return z ? -1 : 1;
        }
        if (!z && (iCompare2 = Integer.compare(iMin2, iMin)) != 0) {
            return iCompare2;
        }
        boolean zContains = set.contains(size);
        if (zContains != set.contains(size2)) {
            return zContains ? -1 : 1;
        }
        boolean z2 = iMin >= i2;
        if (z2 != (iMin2 >= i2)) {
            return z2 ? -1 : 1;
        }
        if (!z2 && (iCompare = Integer.compare(iMin2, iMin)) != 0) {
            return iCompare;
        }
        int iCompare3 = Long.compare(calculateFieldOfViewPenalty(iMin, iMax, i3, i4) * ((long) iMin2), calculateFieldOfViewPenalty(iMin2, iMax2, i3, i4) * ((long) iMin));
        if (iCompare3 != 0) {
            return iCompare3;
        }
        int iCompare4 = Integer.compare(Math.abs(iMin - i2), Math.abs(iMin2 - i2));
        return iCompare4 != 0 ? iCompare4 : Long.compare(((long) size2.getWidth()) * ((long) size2.getHeight()), ((long) size.getWidth()) * ((long) size.getHeight()));
    }

    private static long calculateFieldOfViewPenalty(int i, int i2, int i3, int i4) {
        long j = ((long) i) * ((long) i4);
        return Math.max(0L, (((((long) i2) * ((long) i3)) - j) * 100) - j);
    }

    private static Display getDefaultDisplay() {
        DisplayManager displayManager = (DisplayManager) ApplicationLoader.applicationContext.getSystemService("display");
        if (displayManager != null) {
            return displayManager.getDisplay(0);
        }
        return null;
    }

    public static boolean isRoundDualAvailable(Context context) {
        return DualCameraView.roundDualAvailableStatic(context) && isSeamlessSwitchingAvailable(context);
    }

    public static boolean isSeamlessSwitchingAvailable(Context context) {
        if (seamlessSwitchingAvailableCache != null) {
            return seamlessSwitchingAvailableCache.booleanValue();
        }
        seamlessSwitchingAvailableCache = Boolean.valueOf(SharedConfig.getDevicePerformanceClass() >= 1 && SharedConfig.allowPreparingHevcPlayers() && hasConcurrentFrontBackPair(context));
        return seamlessSwitchingAvailableCache.booleanValue();
    }

    private static boolean hasConcurrentFrontBackPair(Context context) {
        PackageManager packageManager;
        CameraManager cameraManager;
        if (context == null || Build.VERSION.SDK_INT < 30 || (packageManager = context.getPackageManager()) == null || !packageManager.hasSystemFeature("android.hardware.camera.concurrent") || (cameraManager = (CameraManager) context.getSystemService(CameraManager.class)) == null) {
            return false;
        }
        try {
            Iterator<Set<String>> it = cameraManager.getConcurrentCameraIds().iterator();
            while (it.hasNext()) {
                Iterator<String> it2 = it.next().iterator();
                boolean z = false;
                boolean z2 = false;
                while (it2.hasNext()) {
                    Integer num = (Integer) cameraManager.getCameraCharacteristics(it2.next()).get(CameraCharacteristics.LENS_FACING);
                    if (num != null) {
                        if (num.intValue() == 0) {
                            z = true;
                        } else if (num.intValue() == 1) {
                            z2 = true;
                        }
                    }
                }
                if (z && z2) {
                    return true;
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return false;
    }
}
