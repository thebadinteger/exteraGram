package com.exteragram.messenger.camera;

import android.content.Context;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.util.Range;
import androidx.camera.camera2.interop.Camera2CameraInfo;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraInfo;
import androidx.camera.core.CameraSelector;
import androidx.camera.lifecycle.ProcessCameraProvider;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.camera.Camera2Session;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraSession;
import org.telegram.messenger.camera.Size;

public abstract class CameraDebugUtils {
    public static String getCameraXAvailableCameraList(CameraXSession cameraXSession) {
        ProcessCameraProvider processCameraProvider = cameraXSession.provider;
        if (processCameraProvider == null) {
            return "provider=null";
        }
        StringBuilder sb = new StringBuilder();
        try {
            if (cameraXSession.isDualMode()) {
                int i = 0;
                for (List<CameraInfo> list : processCameraProvider.getAvailableConcurrentCameraInfos()) {
                    StringBuilder sb2 = new StringBuilder();
                    boolean z = false;
                    boolean z2 = false;
                    for (CameraInfo cameraInfo : list) {
                        int lensFacing = cameraInfo.getLensFacing();
                        if (lensFacing == 0) {
                            z = true;
                        } else if (lensFacing == 1) {
                            z2 = true;
                        }
                        if (sb2.length() > 0) {
                            sb2.append(" + ");
                        }
                        sb2.append(formatCameraXInfo(cameraInfo));
                    }
                    if (z && z2) {
                        if (sb.length() > 0) {
                            sb.append('\n');
                        }
                        sb.append("pair");
                        sb.append(i);
                        sb.append(": ");
                        sb.append((CharSequence) sb2);
                        i++;
                    }
                }
            } else {
                for (CameraInfo cameraInfo2 : processCameraProvider.getAvailableCameraInfos()) {
                    if (sb.length() > 0) {
                        sb.append(", ");
                    }
                    sb.append(formatCameraXInfo(cameraInfo2));
                }
            }
            return sb.length() == 0 ? "none" : sb.toString();
        } catch (Exception e) {
            FileLog.e(e);
            return "error=".concat(e.getClass().getSimpleName());
        }
    }

    public static String getCameraXBoundCameraList(CameraXSession cameraXSession) {
        StringBuilder sb = new StringBuilder();
        Camera camera = cameraXSession.cameraFront;
        Camera camera2 = cameraXSession.cameraBack;
        Camera camera3 = cameraXSession.camera;
        if (camera != null) {
            sb.append("front=");
            sb.append(formatCameraXInfo(camera.getCameraInfo()));
        }
        if (camera2 != null) {
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append("back=");
            sb.append(formatCameraXInfo(camera2.getCameraInfo()));
        }
        if (sb.length() == 0 && camera3 != null) {
            sb.append("active=");
            sb.append(formatCameraXInfo(camera3.getCameraInfo()));
        }
        return sb.length() == 0 ? "none" : sb.toString();
    }

    public static String getCameraXPhysicalCameraList(CameraXSession cameraXSession) {
        Camera camera = cameraXSession.camera;
        if (camera == null) {
            return "none";
        }
        try {
            StringBuilder sb = new StringBuilder();
            for (CameraInfo cameraInfo : camera.getCameraInfo().getPhysicalCameraInfos()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(formatCameraXInfo(cameraInfo));
            }
            return sb.length() == 0 ? "none" : sb.toString();
        } catch (Exception e) {
            FileLog.e(e);
            return "error=".concat(e.getClass().getSimpleName());
        }
    }

    public static String getCamera2CameraList(Context context) {
        try {
            CameraManager cameraManager = (CameraManager) context.getSystemService(CameraManager.class);
            if (cameraManager == null) {
                return "manager=null";
            }
            StringBuilder sb = new StringBuilder();
            for (String str : cameraManager.getCameraIdList()) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(lensFacingToShortString((Integer) cameraManager.getCameraCharacteristics(str).get(CameraCharacteristics.LENS_FACING)));
                sb.append(str);
            }
            return sb.length() == 0 ? "none" : sb.toString();
        } catch (Exception e) {
            FileLog.e(e);
            return "error=".concat(e.getClass().getSimpleName());
        }
    }

    public static String getLegacyCameraList() {
        ArrayList<org.telegram.messenger.camera.CameraInfo> cameras = CameraController.getInstance().getCameras();
        if (cameras == null || cameras.isEmpty()) {
            return "none";
        }
        StringBuilder sb = new StringBuilder();
        int size = cameras.size();
        int i = 0;
        while (i < size) {
            org.telegram.messenger.camera.CameraInfo cameraInfo = cameras.get(i);
            i++;
            org.telegram.messenger.camera.CameraInfo cameraInfo2 = cameraInfo;
            if (sb.length() > 0) {
                sb.append(", ");
            }
            sb.append(cameraInfo2.isFrontface() ? 'f' : 'b');
            sb.append(cameraInfo2.getCameraId());
        }
        return sb.toString();
    }

    public static String getCameraXSupportedFpsRanges(CameraXSession cameraXSession) {
        CameraInfo cameraInfo;
        if (cameraXSession == null) {
            return "none";
        }
        try {
            Camera camera = cameraXSession.camera;
            if (camera != null) {
                cameraInfo = camera.getCameraInfo();
            } else if (cameraXSession.isDualMode()) {
                Camera camera2 = cameraXSession.isFrontface() ? cameraXSession.cameraFront : cameraXSession.cameraBack;
                if (camera2 != null) {
                    cameraInfo = camera2.getCameraInfo();
                } else {
                    cameraInfo = null;
                }
            } else if (cameraXSession.provider == null) {
                cameraInfo = null;
            } else {
                CameraSelector cameraSelector = cameraXSession.isFrontface() ? CameraSelector.DEFAULT_FRONT_CAMERA : CameraSelector.DEFAULT_BACK_CAMERA;
                if (cameraXSession.provider.hasCamera(cameraSelector)) {
                    cameraInfo = cameraXSession.provider.getCameraInfo(cameraSelector);
                } else {
                    cameraInfo = null;
                }
            }
            if (cameraInfo == null) {
                return "none";
            }
            return formatCamera2FpsRanges((Range[]) Camera2CameraInfo.from(cameraInfo).getCameraCharacteristic(CameraCharacteristics.CONTROL_AE_AVAILABLE_TARGET_FPS_RANGES));
        } catch (Exception e) {
            FileLog.e(e);
            return "error=".concat(e.getClass().getSimpleName());
        }
    }

    public static String getCamera2SupportedFpsRanges(Camera2Session camera2Session) {
        if (camera2Session == null) {
            return "none";
        }
        try {
            return formatCamera2FpsRanges(camera2Session.getAvailableFpsRanges());
        } catch (Exception e) {
            FileLog.e(e);
            return "error=".concat(e.getClass().getSimpleName());
        }
    }

    public static String getLegacySupportedFpsRanges(CameraSession cameraSession) {
        org.telegram.messenger.camera.CameraInfo cameraInfo;
        if (cameraSession == null || (cameraInfo = cameraSession.cameraInfo) == null || cameraInfo.getCamera() == null) {
            return "none";
        }
        try {
            return formatLegacyFpsRanges(cameraSession.cameraInfo.getCamera().getParameters().getSupportedPreviewFpsRange());
        } catch (Exception e) {
            FileLog.e(e);
            return "error=".concat(e.getClass().getSimpleName());
        }
    }

    public static String formatCameraXInfo(CameraInfo cameraInfo) {
        String cameraId;
        float intrinsicZoomRatio;
        try {
            cameraId = Camera2CameraInfo.from(cameraInfo).getCameraId();
        } catch (Exception e) {
            FileLog.e(e);
            cameraId = "?";
        }
        try {
            intrinsicZoomRatio = cameraInfo.getIntrinsicZoomRatio();
        } catch (Exception e2) {
            FileLog.e(e2);
            intrinsicZoomRatio = 1.0f;
        }
        return lensFacingToShortString(Integer.valueOf(cameraInfo.getLensFacing())) + cameraId + "@" + intrinsicZoomRatio;
    }

    public static char lensFacingToShortString(Integer num) {
        if (num == null) {
            return '?';
        }
        if (num.intValue() == 0) {
            return 'f';
        }
        return num.intValue() == 1 ? 'b' : '?';
    }

    public static String formatCameraSize(Size size) {
        if (size == null) {
            return "n/a";
        }
        return size.getWidth() + "x" + size.getHeight();
    }

    public static float safeCameraXZoomRatio(CameraXSession cameraXSession) {
        if (cameraXSession == null) {
            return 1.0f;
        }
        try {
            return cameraXSession.getZoomRatio();
        } catch (Exception e) {
            FileLog.e(e);
            return -1.0f;
        }
    }

    public static float safeCameraXMinZoomRatio(CameraXSession cameraXSession) {
        if (cameraXSession == null) {
            return 1.0f;
        }
        try {
            return cameraXSession.getMinZoomRatio();
        } catch (Exception e) {
            FileLog.e(e);
            return -1.0f;
        }
    }

    public static float safeCameraXMaxZoomRatio(CameraXSession cameraXSession) {
        if (cameraXSession == null) {
            return 1.0f;
        }
        try {
            return cameraXSession.getMaxZoomRatio();
        } catch (Exception e) {
            FileLog.e(e);
            return -1.0f;
        }
    }

    private static String formatCamera2FpsRanges(Range<Integer>[] rangeArr) {
        if (rangeArr == null || rangeArr.length == 0) {
            return "none";
        }
        StringBuilder sb = new StringBuilder();
        for (Range<Integer> range : rangeArr) {
            if (range != null) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(formatFpsRange(((Integer) range.getLower()).intValue(), ((Integer) range.getUpper()).intValue(), false));
            }
        }
        return sb.length() == 0 ? "none" : sb.toString();
    }

    private static String formatLegacyFpsRanges(List<int[]> list) {
        if (list == null || list.isEmpty()) {
            return "none";
        }
        StringBuilder sb = new StringBuilder();
        for (int[] iArr : list) {
            if (iArr != null && iArr.length >= 2) {
                if (sb.length() > 0) {
                    sb.append(", ");
                }
                sb.append(formatFpsRange(iArr[0], iArr[1], true));
            }
        }
        return sb.length() == 0 ? "none" : sb.toString();
    }

    private static String formatFpsRange(int i, int i2, boolean z) {
        return formatFpsValue(i, z) + "-" + formatFpsValue(i2, z);
    }

    private static String formatFpsValue(int i, boolean z) {
        float f = i;
        if (z) {
            f /= 1000.0f;
        }
        if (Math.abs(f - Math.round(f)) < 0.05f) {
            return Integer.toString(Math.round(f));
        }
        return String.format(Locale.US, "%.1f", Float.valueOf(f));
    }
}
