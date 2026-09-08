package com.exteragram.messenger.icons.ui.picker;

import android.annotation.SuppressLint;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import com.exteragram.messenger.ExteraConfig;
import org.telegram.ui.LaunchActivity;

public abstract class IconPickerController {
    private static boolean isInitializing;

    @SuppressLint({"StaticFieldLeak"})
    private static IconPickerView pickerView;

    public static void onDestroy() {
        IconPickerView iconPickerView = pickerView;
        if (iconPickerView != null) {
            iconPickerView.saveConfig();
            if (pickerView.getParent() instanceof ViewGroup) {
                ((ViewGroup) pickerView.getParent()).removeView(pickerView);
            }
        }
        pickerView = null;
    }

    public static boolean onBackPressed(boolean z) {
        IconPickerView iconPickerView = pickerView;
        return iconPickerView != null && iconPickerView.onBackPressed(z);
    }

    public static void setActive(LaunchActivity launchActivity, boolean z) {
        if (launchActivity == null || launchActivity.isFinishing() || launchActivity.isDestroyed()) {
            return;
        }
        if (z == (pickerView != null)) {
            return;
        }
        if (z) {
            showPicker(launchActivity);
        } else {
            hidePicker(launchActivity);
        }
    }

    private static void showPicker(LaunchActivity launchActivity) {
        if (ExteraConfig.getEditingIconPackId() == null || pickerView != null || isInitializing) {
            return;
        }
        isInitializing = true;
        try {
            pickerView = new IconPickerView(launchActivity);
            FrameLayout mainContainerFrameLayout = launchActivity.getMainContainerFrameLayout();
            if (mainContainerFrameLayout != null) {
                mainContainerFrameLayout.addView(pickerView, new FrameLayout.LayoutParams(-1, -1));
                pickerView.showFab();
            }
        } finally {
            isInitializing = false;
        }
    }

    private static void hidePicker(final LaunchActivity launchActivity) {
        if (pickerView == null) {
            return;
        }
        IconObserver.INSTANCE.clear();
        final IconPickerView iconPickerView = pickerView;
        pickerView = null;
        iconPickerView.dismiss(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                IconPickerController.m1216$r8$lambda$OfRV3mAQKhYExDNLrcksaoqPBI(launchActivity, iconPickerView);
            }
        });
    }

    public static /* synthetic */ void m1216$r8$lambda$OfRV3mAQKhYExDNLrcksaoqPBI(LaunchActivity launchActivity, IconPickerView iconPickerView) {
        FrameLayout mainContainerFrameLayout = launchActivity.getMainContainerFrameLayout();
        if (mainContainerFrameLayout != null) {
            mainContainerFrameLayout.removeView(iconPickerView);
        } else if (iconPickerView.getParent() instanceof ViewGroup) {
            ((ViewGroup) iconPickerView.getParent()).removeView(iconPickerView);
        }
    }
}
