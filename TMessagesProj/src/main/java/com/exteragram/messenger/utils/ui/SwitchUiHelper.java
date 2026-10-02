package com.exteragram.messenger.utils.ui;

import android.graphics.RectF;
import android.os.Build;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import androidx.core.graphics.ColorUtils;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.exteragram.messenger.ExteraConfig;
import me.vkryl.android.animator.BoolAnimator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;

/* JADX INFO: loaded from: classes4.dex */
public abstract class SwitchUiHelper {
    private static final float[] MATERIAL3_ICON_CROSS = {6.0f, 6.0f, 18.0f, 18.0f, 6.0f, 18.0f, 18.0f, 6.0f};
    private static final float[] MATERIAL3_ICON_CHECK = {4.5f, 13.1f, 9.0f, 17.6f, 9.0f, 17.6f, 19.8f, 6.8f};
    private static final Interpolator MATERIAL3_THUMB_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    private static final double[] colorLab = new double[3];

    public static boolean isMaterial3SwitchStyle() {
        return ExteraConfig.getNewSwitchStyle();
    }

    private static float scaled(float f) {
        return AndroidUtilities.dpf2(f * 0.8125f);
    }

    public static int getOverlayPadding() {
        if (isMaterial3SwitchStyle()) {
            return AndroidUtilities.dp(5.0f);
        }
        return 0;
    }

    public static int getStateLayerRadius() {
        return isMaterial3SwitchStyle() ? (int) scaled(20.0f) : AndroidUtilities.dp(18.0f);
    }

    public static <K> SpringAnimation createThumbSpring(K k, FloatPropertyCompat<K> floatPropertyCompat) {
        return new SpringAnimation(k, floatPropertyCompat).setMinimumVisibleChange(0.001f).setSpring(new SpringForce().setDampingRatio(0.65f).setStiffness(510.0f));
    }

    public static BoolAnimator createThumbPressedAnimator(View view) {
        return new BoolAnimator(view, CubicBezierInterpolator.Standard, 100L);
    }

    public static void setTrackBounds(RectF rectF, int i, int i2) {
        float fScaled = scaled(26.0f);
        float fScaled2 = scaled(16.0f);
        float f = i / 2.0f;
        float f2 = i2 / 2.0f;
        rectF.set(f - fScaled, f2 - fScaled2, f + fScaled, f2 + fScaled2);
    }

    public static float getTrackOutlineWidth() {
        return scaled(2.0f);
    }

    public static int getUnselectedTrackColor(int i, Theme.ResourcesProvider resourcesProvider) {
        if (!isMaterial3SwitchStyle()) {
            return Theme.getColor(i, resourcesProvider);
        }
        return trackAtTone(i, resourcesProvider, 90.0f, 22.0f);
    }

    public static int getUnselectedThumbColor(int i, int i2, Theme.ResourcesProvider resourcesProvider) {
        if (!isMaterial3SwitchStyle()) {
            return Theme.getColor(i2, resourcesProvider);
        }
        int iTrackAtTone = trackAtTone(i, resourcesProvider, 50.0f, 60.0f);
        if (Theme.isCurrentThemeMonet(resourcesProvider)) {
            return iTrackAtTone;
        }
        return ColorUtils.blendARGB(trackAtTone(i, resourcesProvider, 90.0f, 22.0f), iTrackAtTone, isLightSurface(resourcesProvider) ? 0.55f : 0.8f);
    }

    public static int getSelectedThumbColor(int i, Theme.ResourcesProvider resourcesProvider) {
        if (isMaterial3SwitchStyle() && Build.VERSION.SDK_INT >= 31 && Theme.isCurrentThemeMonet(resourcesProvider)) {
            return Theme.getColor(Theme.key_switchTrackBlueThumbChecked, resourcesProvider);
        }
        return Theme.getColor(i, resourcesProvider);
    }

    private static boolean isLightSurface(Theme.ResourcesProvider resourcesProvider) {
        int color = Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider);
        double[] dArr = colorLab;
        ColorUtils.colorToLAB(color, dArr);
        return dArr[0] > 50.0d;
    }

    private static int trackAtTone(int i, Theme.ResourcesProvider resourcesProvider, float f, float f2) {
        if (!isLightSurface(resourcesProvider)) {
            f = f2;
        }
        int color = Theme.getColor(i, resourcesProvider);
        double[] dArr = colorLab;
        ColorUtils.colorToLAB(color, dArr);
        double dHypot = Math.hypot(dArr[1], dArr[2]);
        if (dHypot > 32.0d) {
            double d = 32.0d / dHypot;
            dArr[1] = dArr[1] * d;
            dArr[2] = dArr[2] * d;
        }
        return ColorUtils.LABToColor(f, dArr[1], dArr[2]);
    }

    public static float getThumbCenterX(int i, float f) {
        return (i / 2.0f) + (((f * 2.0f) - 1.0f) * scaled(10.0f));
    }

    public static void setThumbBounds(RectF rectF, float f, float f2, float f3, boolean z, float f4, float f5) {
        float fLerp;
        float interpolation;
        float fLerp2 = AndroidUtilities.lerp(8.0f, 12.0f, f4);
        float fAcos = (float) (Math.acos(1.0f - (Utilities.clamp01(f3) * 2.0f)) / 3.141592653589793d);
        if (!z) {
            fAcos = 1.0f - fAcos;
        }
        float f6 = z ? 0.6f : 0.4f;
        float f7 = z ? fLerp2 : 12.0f;
        float f8 = z ? 12.0f : fLerp2;
        if (fAcos < f6) {
            interpolation = MATERIAL3_THUMB_INTERPOLATOR.getInterpolation(fAcos / f6);
            fLerp = AndroidUtilities.lerp(f7, 11.0f, interpolation);
        } else {
            float interpolation2 = MATERIAL3_THUMB_INTERPOLATOR.getInterpolation((fAcos - f6) / (1.0f - f6));
            fLerp = AndroidUtilities.lerp(11.0f, f8, interpolation2);
            interpolation = 1.0f - interpolation2;
        }
        float f9 = interpolation * 5.0f;
        if (f5 > 0.0f) {
            fLerp = AndroidUtilities.lerp(fLerp, 14.0f, f5);
            f9 *= 1.0f - f5;
        }
        float fScaled = scaled(fLerp);
        float fScaled2 = scaled(f9);
        rectF.set((f - fScaled2) - fScaled, f2 - fScaled, f + fScaled2 + fScaled, f2 + fScaled);
    }

    public static float getThumbIconScale(int i, int i2) {
        return scaled(13.333333f) / Math.max(i, i2);
    }

    private static float iconGrid(float f) {
        return scaled((f * 16.0f) / 24.0f);
    }

    public static float getIconStrokeWidth() {
        return iconGrid(2.0f);
    }

    public static void setCheckIconLines(float[] fArr, float f, float f2, float f3) {
        float fClamp01 = Utilities.clamp01(f3);
        for (int i = 0; i < fArr.length; i++) {
            fArr[i] = iconGrid(AndroidUtilities.lerp(MATERIAL3_ICON_CROSS[i], MATERIAL3_ICON_CHECK[i], fClamp01) - 12.0f) + (i % 2 == 0 ? f : f2);
        }
    }

    public static float getClockHandLength(boolean z) {
        return iconGrid(z ? 8.5f : 6.8f);
    }
}
