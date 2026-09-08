package com.exteragram.messenger.utils.system;

import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.view.ViewGroup;
import com.exteragram.messenger.ExteraConfig;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

public abstract class VibratorUtils {
    private static final Vibrator vibrator = (Vibrator) ApplicationLoader.applicationContext.getSystemService("vibrator");

    private static boolean isVibrationAllowed() {
        return ExteraConfig.getInAppVibration() && vibrator.hasVibrator();
    }

    public static void vibrate(long j) {
        if (isVibrationAllowed()) {
            if (Build.VERSION.SDK_INT >= 26) {
                try {
                    vibrator.vibrate(VibrationEffect.createOneShot(j, -1));
                    return;
                } catch (Exception e) {
                    FileLog.e(e);
                    return;
                }
            }
            try {
                vibrator.vibrate(j);
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
    }

    public static void vibrate() {
        vibrate(200L);
    }

    public static void vibrateEffect(VibrationEffect vibrationEffect) {
        if (Build.VERSION.SDK_INT < 26 || !isVibrationAllowed()) {
            return;
        }
        try {
            vibrator.cancel();
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            vibrator.vibrate(vibrationEffect);
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public static void disableHapticFeedback(View view) {
        if (view == null) {
            return;
        }
        view.setHapticFeedbackEnabled(false);
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                disableHapticFeedback(viewGroup.getChildAt(i));
            }
        }
    }

    public static int getType(int i) {
        if (ExteraConfig.getInAppVibration()) {
            return i;
        }
        return -1;
    }
}
