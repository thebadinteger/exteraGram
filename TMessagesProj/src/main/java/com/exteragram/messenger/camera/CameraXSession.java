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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleRegistry;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
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
                if (this.lifecycleRegistry.getCurrentState() != Lifecycle.State.DESTROYED) {
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

        @Override // androidx.lifecycle.LifecycleOwner
        public Lifecycle getLifecycle() {
            return this.lifecycleRegistry;
        }
    }

    public CameraXSession(CameraLifecycle cameraLifecycle, Preview.SurfaceProvider surfaceProvider) {
        this.lifecycle = cameraLifecycle;
        this.surfaceProviderPrimary = surfaceProvider;
    }

    public static Preview.SurfaceProvider createSurfaceProvider(final Context context, final SurfaceTexture surfaceTexture, final PreviewSizeListener previewSizeListener) {
        return new Preview.SurfaceProvider() { // from class: com.exteragram.messenger.camera.CameraXSession$$ExternalSyntheticLambda7
            @Override // androidx.camera.core.Preview.SurfaceProvider
            public final void onSurfaceRequested(SurfaceRequest surfaceRequest) {
                CameraXSession.$r8$lambda$293GI9xWXKNG5kPKaSxWt6Uy9f4(previewSizeListener, context, surfaceTexture, surfaceRequest);
            }
        };
    }

    public static /* synthetic */ void $r8$lambda$293GI9xWXKNG5kPKaSxWt6Uy9f4(final PreviewSizeListener previewSizeListener, Context context, SurfaceTexture surfaceTexture, final SurfaceRequest surfaceRequest) {
        try {
            final Size resolution = surfaceRequest.getResolution();
            previewSizeListener.onPreviewSize(resolution.getWidth(), resolution.getHeight());
            surfaceRequest.setTransformationInfoListener(ContextCompat.getMainExecutor(context), new SurfaceRequest.TransformationInfoListener() { // from class: com.exteragram.messenger.camera.CameraXSession$$ExternalSyntheticLambda0
                @Override // androidx.camera.core.SurfaceRequest.TransformationInfoListener
                public final void onTransformationInfoUpdate(SurfaceRequest.TransformationInfo transformationInfo) {
                    CameraXSession.$r8$lambda$TRled3lKaEUY2eaet8nSIa2avwg(resolution, previewSizeListener, transformationInfo);
                }
            });
            surfaceTexture.setDefaultBufferSize(resolution.getWidth(), resolution.getHeight());
            final Surface surface = new Surface(surfaceTexture);
            surfaceRequest.provideSurface(surface, ContextCompat.getMainExecutor(context), new Consumer() { // from class: com.exteragram.messenger.camera.CameraXSession$$ExternalSyntheticLambda1
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    CameraXSession.m903$r8$lambda$jewnXiGL03oQsgonhL2QZ_SOiI(surfaceRequest, surface, (SurfaceRequest.Result) obj);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
            surfaceRequest.willNotProvideSurface();
        }
    }

    public static /* synthetic */ void $r8$lambda$TRled3lKaEUY2eaet8nSIa2avwg(Size size, PreviewSizeListener previewSizeListener, SurfaceRequest.TransformationInfo transformationInfo) {
        Rect cropRect = transformationInfo.getCropRect();
        previewSizeListener.onPreviewSize(cropRect.width() > 0 ? cropRect.width() : size.getWidth(), cropRect.height() > 0 ? cropRect.height() : size.getHeight());
    }

    /* JADX INFO: renamed from: $r8$lambda$jewnXiGL03oQsgonhL2Q-Z_SOiI, reason: not valid java name */
    public static /* synthetic */ void m903$r8$lambda$jewnXiGL03oQsgonhL2QZ_SOiI(SurfaceRequest surfaceRequest, Surface surface, SurfaceRequest.Result result) {
        surfaceRequest.clearTransformationInfoListener();
        surface.release();
    }

    public void setSecondSurfaceProvider(Preview.SurfaceProvider surfaceProvider) {
        Preview preview;
        this.surfaceProviderSecondary = surfaceProvider;
        if (this.isInitiated && this.isDualMode && (preview = this.previewUseCaseBack) != null) {
            preview.setSurfaceProvider(surfaceProvider);
        }
    }

    public boolean isInitiated() {
        return this.isInitiated;
    }

    public boolean isReady() {
        return (!this.isInitiated || this.isBinding || this.camera == null) ? false : true;
    }

    public boolean isDualMode() {
        return this.isDualMode;
    }

    public boolean isFrontface() {
        return this.isFrontface;
    }

    public boolean isActiveCameraFrontface() {
        Camera camera = this.camera;
        if (camera == null) {
            return this.isFrontface;
        }
        int lensFacing = camera.getCameraInfo().getLensFacing();
        if (lensFacing == 0) {
            return true;
        }
        if (lensFacing == 1) {
            return false;
        }
        return this.isFrontface;
    }

    public int getRecordingFrameRate() {
        return this.recordingFrameRate;
    }

    public void initCamera(Context context, boolean z, final boolean z2, final Runnable runnable) {
        this.isFrontface = z;
        final ListenableFuture<ProcessCameraProvider> processCameraProvider = ProcessCameraProvider.getInstance(context);
        processCameraProvider.addListener(new Runnable() { // from class: com.exteragram.messenger.camera.CameraXSession$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                CameraXSession.this.lambda$initCamera$3(processCameraProvider, z2, runnable);
            }
        }, ContextCompat.getMainExecutor(context));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Multi-variable type inference failed */
    public /* synthetic */ void lambda$initCamera$3(ListenableFuture listenableFuture, boolean z, Runnable runnable) {
        try {
            this.provider = (ProcessCameraProvider) listenableFuture.get();
            if (this.lifecycle.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED) {
                return;
            }
            this.isDualMode = z && supportsConcurrentFrontBackPair();
            rebindCamera();
            this.lifecycle.start();
            if (runnable != null) {
                runnable.run();
            }
        } catch (Exception e) {
            FileLog.e(e);
            this.isInitiated = false;
        }
    }

    private boolean supportsConcurrentFrontBackPair() {
        ProcessCameraProvider processCameraProvider = this.provider;
        if (processCameraProvider == null) {
            return false;
        }
        Iterator<List<CameraInfo>> it = processCameraProvider.getAvailableConcurrentCameraInfos().iterator();
        while (it.hasNext()) {
            boolean z = false;
            boolean z2 = false;
            for (CameraInfo cameraInfo : it.next()) {
                if (cameraInfo.getLensFacing() == 0) {
                    z = true;
                }
                if (cameraInfo.getLensFacing() == 1) {
                    z2 = true;
                }
            }
            if (z && z2) {
                return true;
            }
        }
        return false;
    }

    public void switchCamera() {
        boolean z = !this.isFrontface;
        this.isFrontface = z;
        if (this.isDualMode && this.cameraFront != null && this.cameraBack != null) {
            updateActiveControl(z);
            updateTorchState();
        } else {
            rebindCamera();
        }
    }

    private void updateActiveControl(boolean z) {
        if (z) {
            this.camera = this.cameraFront;
            this.cameraControl = this.cameraControlFront;
        } else {
            this.camera = this.cameraBack;
            this.cameraControl = this.cameraControlBack;
        }
    }

    public void closeCamera() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.camera.CameraXSession$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    CameraXSession.this.closeCamera();
                }
            });
            return;
        }
        try {
            ProcessCameraProvider processCameraProvider = this.provider;
            if (processCameraProvider != null) {
                processCameraProvider.unbindAll();
            }
        } catch (Exception e) {
            FileLog.e(e);
        } finally {
            this.lifecycle.stop();
            this.isInitiated = false;
            clearCameraReferences();
        }
    }

    private void clearCameraReferences() {
        this.cameraBack = null;
        this.cameraFront = null;
        this.camera = null;
        this.cameraControlBack = null;
        this.cameraControlFront = null;
        this.cameraControl = null;
        this.previewUseCaseBack = null;
        this.previewUseCase = null;
    }

    public void setTorchEnabled(boolean z) {
        this.isTorchOn = z;
        updateTorchState();
    }

    private void updateTorchState() {
        Camera camera;
        try {
            boolean z = true;
            if (this.isDualMode) {
                CameraControl cameraControl = this.cameraControlFront;
                if (cameraControl != null) {
                    cameraControl.enableTorch(false);
                }
                if (this.cameraControlBack == null || (camera = this.cameraBack) == null || !camera.getCameraInfo().hasFlashUnit()) {
                    return;
                }
                CameraControl cameraControl2 = this.cameraControlBack;
                if (!this.isTorchOn || this.isFrontface) {
                    z = false;
                }
                cameraControl2.enableTorch(z);
                return;
            }
            Camera camera2 = this.camera;
            if (camera2 == null || this.cameraControl == null || !camera2.getCameraInfo().hasFlashUnit()) {
                return;
            }
            boolean z2 = this.camera.getCameraInfo().getLensFacing() == 1;
            CameraControl cameraControl3 = this.cameraControl;
            if (!this.isTorchOn || !z2) {
                z = false;
            }
            cameraControl3.enableTorch(z);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private Preview buildPreview(CameraSelector cameraSelector, boolean z, Range<Integer> range, boolean z2) {
        final Size sensorAspect = getSensorAspect(cameraSelector);
        final Set<Size> set = z2 ? get60FpsCapableSizes(cameraSelector) : Collections.EMPTY_SET;
        Preview.Builder resolutionSelector = new Preview.Builder().setResolutionSelector(new ResolutionSelector.Builder().setAspectRatioStrategy(AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY).setResolutionFilter(new ResolutionFilter() { // from class: com.exteragram.messenger.camera.CameraXSession$$ExternalSyntheticLambda3
            @Override // androidx.camera.core.resolutionselector.ResolutionFilter
            public final List filter(List list, int i) {
                return CameraXSession.sortRoundPreviewSizes(list, sensorAspect, set);
            }
        }).setAllowedResolutionMode(0).build());
        if (range != null) {
            resolutionSelector.setTargetFrameRate(range);
        }
        resolutionSelector.setPreviewStabilizationEnabled(!this.isDualMode && z);
        if (!ExteraConfig.getCameraMirrorMode()) {
            resolutionSelector.setMirrorMode(0);
        }
        return resolutionSelector.build();
    }

    private CameraInfo getCameraInfo(CameraSelector cameraSelector) {
        try {
            if (this.provider != null && cameraSelector != null) {
                List<CameraInfo> infos = cameraSelector.filter(this.provider.getAvailableCameraInfos());
                if (!infos.isEmpty()) {
                    return infos.get(0);
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        return null;
    }

    private boolean isPreviewStabilizationSupported(CameraSelector cameraSelector) {
        if (cameraSelector != null) {
            try {
                ProcessCameraProvider processCameraProvider = this.provider;
                if (processCameraProvider != null && processCameraProvider.hasCamera(cameraSelector)) {
                    Map<CameraSelector, Boolean> map = STABILIZATION_SUPPORT_CACHE;
                    Boolean bool = map.get(cameraSelector);
                    if (bool != null) {
                        return bool.booleanValue();
                    }
                    CameraInfo info = getCameraInfo(cameraSelector);
                    boolean zIsStabilizationSupported = info != null && Preview.getPreviewCapabilities(info).isStabilizationSupported();
                    map.put(cameraSelector, Boolean.valueOf(zIsStabilizationSupported));
                    return zIsStabilizationSupported;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return false;
    }

    private Camera tryBindSingleAtFrameRate(Range<Integer> range) {
        return null;
    }

    private Camera tryBindSingleAtExtendedFrameRate() {
        return null;
    }

    private Camera tryBindSingleSession(SessionConfig sessionConfig, int i) {
        return null;
    }

    private void rebindCamera() {
        if (this.provider == null || this.lifecycle.getLifecycle().getCurrentState() == Lifecycle.State.DESTROYED || this.isBinding) {
            return;
        }
        boolean z = true;
        this.isBinding = true;
        this.recordingFrameRate = 30;
        clearCameraReferences();
        try {
            this.provider.unbindAll();
            if (this.isDualMode) {
                bindDualUseCases();
            } else {
                bindSingleUseCases();
            }
            if (this.camera == null) {
                z = false;
            }
            this.isInitiated = z;
            return;
        } catch (Exception e) {
            FileLog.e(e);
            this.isInitiated = false;
            this.provider.unbindAll();
        } finally {
            this.isBinding = false;
        }
        clearCameraReferences();
    }

    private void bindSingleUseCases() {
        try {
            CameraSelector cameraSelector = this.isFrontface ? CameraSelector.DEFAULT_FRONT_CAMERA : CameraSelector.DEFAULT_BACK_CAMERA;
            this.cameraSelector = cameraSelector;
            if (!this.provider.hasCamera(cameraSelector)) {
                this.isInitiated = false;
                return;
            }
            boolean extendedFramesPerSecond = ExteraConfig.getExtendedFramesPerSecond();
            boolean z = ExteraConfig.getCameraStabilization() && isPreviewStabilizationSupported(this.cameraSelector);
            Preview previewBuildPreview = buildPreview(this.cameraSelector, z, null, extendedFramesPerSecond);
            this.previewUseCase = previewBuildPreview;
            previewBuildPreview.setSurfaceProvider(this.surfaceProviderPrimary);
            if (extendedFramesPerSecond) {
                Camera cameraTryBindSingleAtExtendedFrameRate = tryBindSingleAtExtendedFrameRate();
                this.camera = cameraTryBindSingleAtExtendedFrameRate;
                if (cameraTryBindSingleAtExtendedFrameRate == null && z) {
                    Preview previewBuildPreview2 = buildPreview(this.cameraSelector, false, null, true);
                    this.previewUseCase = previewBuildPreview2;
                    previewBuildPreview2.setSurfaceProvider(this.surfaceProviderPrimary);
                    this.camera = tryBindSingleAtExtendedFrameRate();
                }
                if (this.camera == null) {
                    Preview previewBuildPreview3 = buildPreview(this.cameraSelector, z, null, false);
                    this.previewUseCase = previewBuildPreview3;
                    previewBuildPreview3.setSurfaceProvider(this.surfaceProviderPrimary);
                    this.camera = tryBindSingleAtFrameRate(FPS_30_RANGE);
                }
            } else {
                this.camera = tryBindSingleAtFrameRate(FPS_30_RANGE);
            }
            if (this.camera == null) {
                try {
                    this.camera = this.provider.bindToLifecycle(this.lifecycle, this.cameraSelector, this.previewUseCase);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            if (this.camera == null && z) {
                Preview previewBuildPreview4 = buildPreview(this.cameraSelector, false, null, false);
                this.previewUseCase = previewBuildPreview4;
                previewBuildPreview4.setSurfaceProvider(this.surfaceProviderPrimary);
                this.camera = this.provider.bindToLifecycle(this.lifecycle, this.cameraSelector, this.previewUseCase);
            }
            Camera camera = this.camera;
            if (camera == null) {
                this.isInitiated = false;
                return;
            }
            this.cameraControl = camera.getCameraControl();
            observeCameraState(this.camera);
            applyInitialZoom(this.camera, this.cameraControl);
            updateTorchState();
        } catch (Exception e2) {
            FileLog.e(e2);
            this.isInitiated = false;
        }
    }

    private void bindDualUseCases() {
        boolean extendedFramesPerSecond = ExteraConfig.getExtendedFramesPerSecond();
        Range<Integer> range = extendedFramesPerSecond ? FPS_60_RANGE : FPS_30_RANGE;
        ConcurrentCamera concurrentCameraTryBindConcurrentCameras = tryBindConcurrentCameras(range);
        if (concurrentCameraTryBindConcurrentCameras == null && extendedFramesPerSecond) {
            range = FPS_30_RANGE;
            concurrentCameraTryBindConcurrentCameras = tryBindConcurrentCameras(range);
        }
        if (concurrentCameraTryBindConcurrentCameras == null && range != null) {
            range = null;
            concurrentCameraTryBindConcurrentCameras = tryBindConcurrentCameras(null);
        }
        if (concurrentCameraTryBindConcurrentCameras == null) {
            this.isDualMode = false;
            bindSingleUseCases();
            return;
        }
        if (range != null) {
            this.recordingFrameRate = ((Integer) range.getLower()).intValue();
        }
        for (Camera camera : concurrentCameraTryBindConcurrentCameras.getCameras()) {
            if (camera.getCameraInfo().getLensFacing() == 0) {
                this.cameraFront = camera;
                CameraControl cameraControl = camera.getCameraControl();
                this.cameraControlFront = cameraControl;
                applyInitialZoom(this.cameraFront, cameraControl);
            } else {
                this.cameraBack = camera;
                CameraControl cameraControl2 = camera.getCameraControl();
                this.cameraControlBack = cameraControl2;
                applyInitialZoom(this.cameraBack, cameraControl2);
            }
            observeCameraState(camera);
        }
        updateActiveControl(this.isFrontface);
        updateTorchState();
    }

    private ConcurrentCamera tryBindConcurrentCameras(Range<Integer> range) {
        try {
            boolean zEquals = FPS_60_RANGE.equals(range);
            CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;
            Preview previewBuildPreview = buildPreview(cameraSelector, false, range, zEquals);
            this.previewUseCase = previewBuildPreview;
            previewBuildPreview.setSurfaceProvider(this.surfaceProviderPrimary);
            CameraSelector cameraSelector2 = CameraSelector.DEFAULT_BACK_CAMERA;
            Preview previewBuildPreview2 = buildPreview(cameraSelector2, false, range, zEquals);
            this.previewUseCaseBack = previewBuildPreview2;
            Preview.SurfaceProvider surfaceProvider = this.surfaceProviderSecondary;
            if (surfaceProvider != null) {
                previewBuildPreview2.setSurfaceProvider(surfaceProvider);
            }
            ConcurrentCamera.SingleCameraConfig singleCameraConfig = new ConcurrentCamera.SingleCameraConfig(cameraSelector, new UseCaseGroup.Builder().addUseCase(this.previewUseCase).build(), this.lifecycle);
            ConcurrentCamera.SingleCameraConfig singleCameraConfig2 = new ConcurrentCamera.SingleCameraConfig(cameraSelector2, new UseCaseGroup.Builder().addUseCase(this.previewUseCaseBack).build(), this.lifecycle);
            ArrayList arrayList = new ArrayList(2);
            arrayList.add(singleCameraConfig);
            arrayList.add(singleCameraConfig2);
            return this.provider.bindToLifecycle(arrayList);
        } catch (Exception e) {
            FileLog.e(e);
            try {
                this.provider.unbindAll();
                return null;
            } catch (Exception e2) {
                FileLog.e(e2);
                return null;
            }
        }
    }

    private void observeCameraState(Camera camera) {
        try {
            camera.getCameraInfo().getCameraState().observe(this.lifecycle, new Observer() { // from class: com.exteragram.messenger.camera.CameraXSession$$ExternalSyntheticLambda4
                @Override // androidx.lifecycle.Observer
                public final void onChanged(Object obj) {
                    CameraXSession.$r8$lambda$rb0Pnzaeyz85DEsbXVziDkXE8Vk((CameraState) obj);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ void $r8$lambda$rb0Pnzaeyz85DEsbXVziDkXE8Vk(CameraState cameraState) {
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
                zoomState.observe(this.lifecycle, new Observer<ZoomState>() { // from class: com.exteragram.messenger.camera.CameraXSession.1
                    @Override // androidx.lifecycle.Observer
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

    /* JADX INFO: Access modifiers changed from: private */
    public void applyWideAngle(CameraControl cameraControl, ZoomState zoomState) {
        if (zoomState == null || zoomState.getMinZoomRatio() >= 1.0f) {
            return;
        }
        cameraControl.setZoomRatio(zoomState.getMinZoomRatio());
    }

    /* JADX INFO: Access modifiers changed from: private */
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
            if (processCameraProvider != null && cameraSelector != null && processCameraProvider.hasCamera(cameraSelector) && (rect = (Rect) Camera2CameraInfo.from(getCameraInfo(cameraSelector)).getCameraCharacteristic(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE)) != null && rect.width() > 0 && rect.height() > 0) {
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
                StreamConfigurationMap streamConfigurationMap = (StreamConfigurationMap) Camera2CameraInfo.from(getCameraInfo(cameraSelector)).getCameraCharacteristic(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
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

    /* JADX INFO: Access modifiers changed from: private */
    public static List<Size> sortRoundPreviewSizes(List<Size> list, Size size, final Set<Size> set) {
        ArrayList arrayList = new ArrayList(list);
        final int roundVideoResolution = SystemUtils.getRoundVideoResolution();
        final int i = roundVideoResolution * 2;
        final int iMin = Math.min(size.getWidth(), size.getHeight());
        final int iMax = Math.max(size.getWidth(), size.getHeight());
        arrayList.sort(new Comparator() { // from class: com.exteragram.messenger.camera.CameraXSession$$ExternalSyntheticLambda2
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return CameraXSession.compareRoundPreviewSizes((Size) obj, (Size) obj2, roundVideoResolution, i, iMin, iMax, set);
            }
        });
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: private */
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
