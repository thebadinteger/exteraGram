package com.exteragram.messenger.camera;

import android.content.Context;
import android.graphics.Rect;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.util.Size;
import android.util.SizeF;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;

/* JADX INFO: loaded from: classes4.dex */
public abstract class CameraLensStops {
    private static final float[] NO_RATIOS = new float[0];
    private static final float[] SNAP_RATIOS = {1.0f, 2.0f, 3.0f, 4.0f, 5.0f, 6.0f, 7.0f, 8.0f, 10.0f, 12.0f, 15.0f, 20.0f};
    private static final float[] ROUND_RATIOS = {1.5f, 2.0f, 3.0f, 5.0f, 10.0f, 15.0f, 20.0f, 30.0f};
    private static final float[] RULER_LADDER = {1.0f, 2.0f, 5.0f, 10.0f, 30.0f};
    private static final Map<String, float[]> RATIO_CACHE = new HashMap();

    public static float[] opticalZoomRatios(Context context, String str, float f) {
        float[] opticalZoomRatios;
        if (context == null || str == null || Build.VERSION.SDK_INT < 28) {
            return NO_RATIOS;
        }
        Map<String, float[]> map = RATIO_CACHE;
        synchronized (map) {
            opticalZoomRatios = map.get(str);
        }
        if (opticalZoomRatios == null) {
            opticalZoomRatios = readOpticalZoomRatios(context, str);
            synchronized (map) {
                map.put(str, opticalZoomRatios);
            }
        }
        return normalizeRatios(opticalZoomRatios, f);
    }

    /* JADX WARN: Code duplicated, block: B:11:0x001a  */
    private static float[] normalizeRatios(float[] fArr, float f) {
        float f2;
        if (fArr.length < 2) {
            return NO_RATIOS;
        }
        float f3 = 0.0f;
        if (f > 0.0f) {
            float f4 = fArr[0];
            if (f4 > 1.25f * f) {
                f2 = f / f4;
            } else {
                f2 = 1.0f;
            }
        } else {
            f2 = 1.0f;
        }
        double d = Double.MAX_VALUE;
        for (float f5 : fArr) {
            float f6 = f5 * f2;
            double dAbs = Math.abs(octaves(1.0f, f6));
            if (dAbs < d) {
                f3 = f6;
                d = dAbs;
            }
        }
        if (Math.abs(f3 - 1.0f) > 0.12f) {
            return NO_RATIOS;
        }
        ArrayList arrayList = new ArrayList(fArr.length);
        for (float f7 : fArr) {
            addDistinctStop(arrayList, snapToNiceRatio((f7 * f2) / f3));
        }
        return toArray(arrayList);
    }

    private static float[] readOpticalZoomRatios(Context context, String str) {
        try {
            CameraManager cameraManager = (CameraManager) context.getSystemService("camera");
            if (cameraManager == null) {
                return NO_RATIOS;
            }
            CameraCharacteristics cameraCharacteristics = cameraManager.getCameraCharacteristics(str);
            Set<String> physicalCameraIds = cameraCharacteristics.getPhysicalCameraIds();
            if (physicalCameraIds != null && physicalCameraIds.size() >= 2) {
                float fHalfFieldOfViewTangent = halfFieldOfViewTangent(cameraCharacteristics);
                if (fHalfFieldOfViewTangent <= 0.0f) {
                    return NO_RATIOS;
                }
                ArrayList arrayList = new ArrayList(physicalCameraIds.size());
                Iterator<String> it = physicalCameraIds.iterator();
                while (it.hasNext()) {
                    try {
                        float fHalfFieldOfViewTangent2 = halfFieldOfViewTangent(cameraManager.getCameraCharacteristics(it.next()));
                        if (fHalfFieldOfViewTangent2 > 0.0f) {
                            addDistinctStop(arrayList, fHalfFieldOfViewTangent / fHalfFieldOfViewTangent2);
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
                return toArray(arrayList);
            }
            return NO_RATIOS;
        } catch (Exception e2) {
            FileLog.e(e2);
            return NO_RATIOS;
        }
    }

    private static float halfFieldOfViewTangent(CameraCharacteristics cameraCharacteristics) {
        float width;
        float fWidth;
        int width2;
        float[] fArr = (float[]) cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
        SizeF sizeF = (SizeF) cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
        Rect rect = (Rect) cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
        Size size = (Size) cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE);
        Integer num = (Integer) cameraCharacteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        if (fArr != null && fArr.length != 0 && sizeF != null && rect != null && size != null && num != null) {
            if (num.intValue() % 180 == 90) {
                width = sizeF.getHeight();
                fWidth = rect.height();
                width2 = size.getHeight();
            } else {
                width = sizeF.getWidth();
                fWidth = rect.width();
                width2 = size.getWidth();
            }
            float f = width2;
            float f2 = fArr[0];
            if (f2 > 0.0f && width > 0.0f && fWidth > 0.0f && f > 0.0f) {
                return ((width * fWidth) / f) / (f2 * 2.0f);
            }
        }
        return 0.0f;
    }

    public static float[] buildToggleStops(boolean z, float f, float f2, float[] fArr) {
        float[] fArrTelephotoRatios = telephotoRatios(fArr, f2);
        if (fArrTelephotoRatios.length == 0) {
            return boundStops(z ? new float[]{f, 1.0f, 2.0f} : new float[]{f, 1.0f, 2.0f, 5.0f}, f, f2, false);
        }
        ArrayList arrayList = new ArrayList(6);
        if (f < 0.9999f) {
            addDistinctStop(arrayList, f);
        }
        addDistinctStop(arrayList, Utilities.clamp(1.0f, f2, f));
        for (float f3 : fArrTelephotoRatios) {
            addDistinctStop(arrayList, f3);
        }
        fillWideGaps(arrayList);
        addReachStop(arrayList, fArrTelephotoRatios[fArrTelephotoRatios.length - 1], f2);
        dropCrowdedStops(arrayList);
        return toArray(arrayList);
    }

    public static float[] buildRulerStops(float f, float f2, float[] fArr) {
        ArrayList arrayList = new ArrayList(fArr.length + RULER_LADDER.length + 2);
        addDistinctStop(arrayList, f);
        for (float f3 : fArr) {
            addDistinctStop(arrayList, Utilities.clamp(f3, f2, f));
        }
        addDistinctStop(arrayList, f2);
        for (float f4 : RULER_LADDER) {
            if (f4 >= f - 1.0E-4f && f4 <= 1.0E-4f + f2 && nearestOctaveDistance(arrayList, f4) >= 0.55d) {
                addDistinctStop(arrayList, f4);
            }
        }
        return toArray(arrayList);
    }

    private static float[] telephotoRatios(float[] fArr, float f) {
        if (fArr == null || fArr.length == 0) {
            return NO_RATIOS;
        }
        ArrayList arrayList = new ArrayList(fArr.length);
        for (float f2 : fArr) {
            if (f2 >= 1.15f && f2 <= 1.0E-4f + f) {
                addDistinctStop(arrayList, f2);
            }
        }
        return toArray(arrayList);
    }

    private static void fillWideGaps(ArrayList<Float> arrayList) {
        while (arrayList.size() < 4) {
            int i = -1;
            double d = 2.0d;
            for (int i2 = 1; i2 < arrayList.size(); i2++) {
                double dOctaves = octaves(arrayList.get(i2 - 1).floatValue(), arrayList.get(i2).floatValue());
                if (dOctaves > d) {
                    i = i2;
                    d = dOctaves;
                }
            }
            if (i < 0) {
                return;
            }
            float fChooseRoundRatio = chooseRoundRatio(arrayList.get(i - 1).floatValue(), arrayList.get(i).floatValue());
            if (fChooseRoundRatio <= 0.0f) {
                return;
            }
            int size = arrayList.size();
            addDistinctStop(arrayList, fChooseRoundRatio);
            if (arrayList.size() == size) {
                return;
            }
        }
    }

    private static float chooseRoundRatio(float f, float f2) {
        double dSqrt = Math.sqrt(((double) f) * ((double) f2));
        float f3 = 0.0f;
        double d = Double.MAX_VALUE;
        for (float f4 : ROUND_RATIOS) {
            if (f4 > f + 1.0E-4f && f4 < f2 - 1.0E-4f) {
                double dAbs = Math.abs(octaves(f4, (float) dSqrt));
                if (dAbs < d) {
                    f3 = f4;
                    d = dAbs;
                }
            }
        }
        return f3;
    }

    private static void addReachStop(ArrayList<Float> arrayList, float f, float f2) {
        if (arrayList.size() >= 4 || arrayList.isEmpty()) {
            return;
        }
        float fSnapToNiceRatio = snapToNiceRatio(f * 2.0f);
        if (fSnapToNiceRatio > f2 + 1.0E-4f || fSnapToNiceRatio <= arrayList.get(arrayList.size() - 1).floatValue() + 1.0E-4f) {
            return;
        }
        addDistinctStop(arrayList, fSnapToNiceRatio);
    }

    private static void dropCrowdedStops(ArrayList<Float> arrayList) {
        while (arrayList.size() > 5) {
            int i = -1;
            double d = Double.MAX_VALUE;
            for (int i2 = 1; i2 < arrayList.size() - 1; i2++) {
                double dOctaves = octaves(arrayList.get(i2 - 1).floatValue(), arrayList.get(i2).floatValue());
                if (dOctaves < d) {
                    i = i2;
                    d = dOctaves;
                }
            }
            if (i < 0) {
                return;
            } else {
                arrayList.remove(i);
            }
        }
    }

    private static float snapToNiceRatio(float f) {
        if (!Float.isFinite(f) || f <= 0.0f) {
            return 0.0f;
        }
        float f2 = Float.MAX_VALUE;
        float f3 = 0.0f;
        for (float f4 : SNAP_RATIOS) {
            float f5 = (f4 - f) / f;
            if (f5 >= -0.03f && f5 <= 0.1f) {
                float fAbs = Math.abs(f5);
                if (fAbs < f2) {
                    f3 = f4;
                    f2 = fAbs;
                }
            }
        }
        return f3 > 0.0f ? f3 : Math.round(f * 10.0f) / 10.0f;
    }

    private static double nearestOctaveDistance(ArrayList<Float> arrayList, float f) {
        double dMin = Double.MAX_VALUE;
        for (int i = 0; i < arrayList.size(); i++) {
            dMin = Math.min(dMin, Math.abs(octaves(arrayList.get(i).floatValue(), f)));
        }
        return dMin;
    }

    private static double octaves(float f, float f2) {
        if (f <= 0.0f || f2 <= 0.0f) {
            return 0.0d;
        }
        return Math.log(((double) f2) / ((double) f)) / Math.log(2.0d);
    }

    private static float[] boundStops(float[] fArr, float f, float f2, boolean z) {
        ArrayList arrayList = new ArrayList(fArr.length + 2);
        if (z) {
            addDistinctStop(arrayList, f);
        }
        for (float f3 : fArr) {
            if (f3 > 0.0f && Float.isFinite(f3)) {
                if (f3 < f - 1.0E-4f) {
                    if (!z) {
                        addDistinctStop(arrayList, f);
                    }
                } else if (f3 <= 1.0E-4f + f2) {
                    addDistinctStop(arrayList, Utilities.clamp(f3, f2, f));
                }
            }
        }
        if (z) {
            addDistinctStop(arrayList, f2);
        }
        return toArray(arrayList);
    }

    private static void addDistinctStop(ArrayList<Float> arrayList, float f) {
        if (f <= 0.0f || !Float.isFinite(f)) {
            return;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            float fFloatValue = arrayList.get(i).floatValue();
            if (Math.abs(fFloatValue - f) <= 1.0E-4f) {
                return;
            }
            if (fFloatValue > f) {
                arrayList.add(i, Float.valueOf(f));
                return;
            }
        }
        arrayList.add(Float.valueOf(f));
    }

    private static float[] toArray(ArrayList<Float> arrayList) {
        int size = arrayList.size();
        float[] fArr = new float[size];
        for (int i = 0; i < size; i++) {
            fArr[i] = arrayList.get(i).floatValue();
        }
        return fArr;
    }
}
