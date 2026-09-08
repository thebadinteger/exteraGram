package org.telegram.messenger.camera;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Range;
import android.view.Surface;
import android.view.WindowManager;
import com.exteragram.messenger.ExteraConfig;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;

public class Camera2Session {
    private CameraCharacteristics cameraCharacteristics;
    private CameraDevice cameraDevice;
    public final String cameraId;
    private final CameraManager cameraManager;
    private final CameraDevice.StateCallback cameraStateCallback;
    private CaptureRequest.Builder captureRequestBuilder;
    private CameraCaptureSession captureSession;
    private final CameraCaptureSession.StateCallback captureStateCallback;
    private Runnable doneCallback;
    private boolean flashing;
    private Handler handler;
    private ImageReader imageReader;
    private boolean isClosed;
    private boolean isError;
    private final boolean isFront;
    private boolean isSuccess;
    private long lastTime;
    private float maxZoom;
    private boolean nightMode;
    private final android.util.Size previewSize;
    private boolean recordingVideo;
    private boolean scanningBarcode;
    private Rect sensorSize;
    private Surface surface;
    private SurfaceTexture surfaceTexture;
    private HandlerThread thread;
    private float currentZoom = 1.0f;
    private volatile int recordingFrameRate = 30;
    private boolean opened = false;
    private final Rect cropRegion = new Rect();

    public float getMinZoom() {
        return 1.0f;
    }

    String val$cameraId;

        public AnonymousClass1(String str) {
            this.val$cameraId = str;
        }

        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public void onOpened(CameraDevice cameraDevice) {
            Camera2Session.this.cameraDevice = cameraDevice;
            Camera2Session.this.lastTime = System.currentTimeMillis();
            FileLog.d("Camera2Session camera #" + this.val$cameraId + " opened");
            Camera2Session.this.checkOpen();
        }

        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public void onDisconnected(CameraDevice cameraDevice) {
            Camera2Session.this.cameraDevice = cameraDevice;
            FileLog.d("Camera2Session camera #" + this.val$cameraId + " disconnected");
        }

        @Override // android.hardware.camera2.CameraDevice.StateCallback
        public void onError(CameraDevice cameraDevice, int i) {
            Camera2Session.this.cameraDevice = cameraDevice;
            FileLog.e("Camera2Session camera #" + this.val$cameraId + " received " + i + " error");
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onError$0();
                }
            });
        }

        public void lambda$onConfigured$0() {
            Camera2Session.this.isSuccess = true;
            if (Camera2Session.this.doneCallback != null) {
                Camera2Session.this.doneCallback.run();
                Camera2Session.this.doneCallback = null;
            }
        }

        @Override // android.hardware.camera2.CameraCaptureSession.StateCallback
        public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
            Camera2Session.this.captureSession = cameraCaptureSession;
            FileLog.e("Camera2Session camera #" + this.val$cameraId + " capture session failed to configure");
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onConfigureFailed$1();
                }
            });
        }

        public void lambda$open$1(SurfaceTexture surfaceTexture) {
        this.surfaceTexture = surfaceTexture;
        if (surfaceTexture != null) {
            surfaceTexture.setDefaultBufferSize(getPreviewWidth(), getPreviewHeight());
        }
        checkOpen();
    }

    public void checkOpen() {
        if (this.opened || this.surfaceTexture == null || this.cameraDevice == null) {
            return;
        }
        this.opened = true;
        this.surface = new Surface(this.surfaceTexture);
        try {
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.surface);
            arrayList.add(this.imageReader.getSurface());
            this.cameraDevice.createCaptureSession(arrayList, this.captureStateCallback, null);
        } catch (Exception e) {
            FileLog.e(e);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$checkOpen$2();
                }
            });
        }
    }

    public void lambda$destroy$4(final Runnable runnable) {
        CameraCaptureSession cameraCaptureSession = this.captureSession;
        if (cameraCaptureSession != null) {
            cameraCaptureSession.close();
            this.captureSession = null;
        }
        CameraDevice cameraDevice = this.cameraDevice;
        if (cameraDevice != null) {
            cameraDevice.close();
            this.cameraDevice = null;
        }
        ImageReader imageReader = this.imageReader;
        if (imageReader != null) {
            imageReader.close();
            this.imageReader = null;
        }
        this.thread.quitSafely();
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$destroy$3(runnable);
            }
        });
    }

    public File val$file;
        final void $r8$lambda$C8GruD4YcNmICIAfGZxauIzKYfU(Utilities.Callback callback, int i) {
            if (callback != null) {
                callback.run(Integer.valueOf(i));
            }
        }
    }

    private void chooseStabilizationMode(CaptureRequest.Builder builder) {
        int[] iArr = (int[]) this.cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_OPTICAL_STABILIZATION);
        if (iArr != null) {
            for (int i : iArr) {
                if (i == 1) {
                    builder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, 1);
                    builder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, 0);
                    FileLog.d("Using optical stabilization.");
                    return;
                }
            }
        }
        for (int i2 : (int[]) this.cameraCharacteristics.get(CameraCharacteristics.CONTROL_AVAILABLE_VIDEO_STABILIZATION_MODES)) {
            if (i2 == 1) {
                builder.set(CaptureRequest.CONTROL_VIDEO_STABILIZATION_MODE, 1);
                builder.set(CaptureRequest.LENS_OPTICAL_STABILIZATION_MODE, 0);
                FileLog.d("Using video stabilization.");
                return;
            }
        }
        FileLog.d("Stabilization not available.");
    }

    private void chooseFocusMode(CaptureRequest.Builder builder) {
        for (int i : (int[]) this.cameraCharacteristics.get(CameraCharacteristics.CONTROL_AF_AVAILABLE_MODES)) {
            if (i == 3) {
                builder.set(CaptureRequest.CONTROL_AF_MODE, 3);
                FileLog.d("Using continuous video auto-focus.");
                return;
            }
        }
        FileLog.d("Auto-focus is not available.");
    }

    public static android.util.Size chooseOptimalSize(android.util.Size[] sizeArr, int i, int i2, boolean z) {
        ArrayList arrayList = new ArrayList(sizeArr.length);
        ArrayList arrayList2 = new ArrayList(sizeArr.length);
        for (android.util.Size size : sizeArr) {
            if (!z || (size.getHeight() <= i2 && size.getWidth() <= i)) {
                if (size.getHeight() == (size.getWidth() * i2) / i && size.getWidth() >= i && size.getHeight() >= i2) {
                    arrayList.add(size);
                } else if (size.getHeight() * size.getWidth() <= i * i2 * 4 && size.getWidth() >= i && size.getHeight() >= i2) {
                    arrayList2.add(size);
                }
            }
        }
        if (arrayList.size() > 0) {
            return (android.util.Size) Collections.min(arrayList, new CompareSizesByArea());
        }
        if (arrayList2.size() > 0) {
            return (android.util.Size) Collections.min(arrayList2, new CompareSizesByArea());
        }
        return (android.util.Size) Collections.max(Arrays.asList(sizeArr), new CompareSizesByArea());
    }

    public static class CompareSizesByArea implements Comparator<android.util.Size> {
        @Override // java.util.Comparator
        public int compare(android.util.Size size, android.util.Size size2) {
            return Long.signum((((long) size.getWidth()) * ((long) size.getHeight())) - (((long) size2.getWidth()) * ((long) size2.getHeight())));
        }
    }

    public boolean isTorchAvailable(boolean z) {
        String strFindCameraId = findCameraId(z);
        if (strFindCameraId == null) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(this.cameraManager.getCameraCharacteristics(strFindCameraId).get(CameraCharacteristics.FLASH_INFO_AVAILABLE));
        } catch (CameraAccessException e) {
            FileLog.e(e);
            return false;
        }
    }

    private String findCameraId(boolean z) {
        int i;
        try {
            String[] cameraIdList = this.cameraManager.getCameraIdList();
            int length = cameraIdList.length;
            while (i < length) {
                String str = cameraIdList[i];
                Integer num = (Integer) this.cameraManager.getCameraCharacteristics(str).get(CameraCharacteristics.LENS_FACING);
                i = (num == null || (!(z && num.intValue() == 0) && (z || num.intValue() != 1))) ? i + 1 : 0;
                return str;
            }
            return null;
        } catch (CameraAccessException e) {
            FileLog.e(e);
            return null;
        }
    }
}
