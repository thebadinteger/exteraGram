package com.exteragram.messenger.utils.ui;

import com.exteragram.messenger.ExteraConfig;

/* JADX INFO: loaded from: classes4.dex */
public abstract class FabUiHelper {
    public static boolean isMaterial3Fab() {
        return ExteraConfig.getNewFabStyle();
    }

    public static int getFabSizeDp() {
        return isMaterial3Fab() ? 56 : 48;
    }

    public static int getFabBottomMarginDp() {
        return isMaterial3Fab() ? 16 : 14;
    }

    public static int getSubFabBottomMarginDp() {
        return isMaterial3Fab() ? 24 : 14;
    }

    public static float getSubFabPaddingDp() {
        return isMaterial3Fab() ? 8.0f : 5.66f;
    }

    public static float getFabElevationDp() {
        return isMaterial3Fab() ? 6.0f : 0.5f;
    }

    public static float getSubFabShadowRadiusDp() {
        return isMaterial3Fab() ? 6.0f : 2.667f;
    }

    public static float getSubFabShadowDyDp() {
        return isMaterial3Fab() ? 2.0f : 0.85f;
    }

    public static int getSubFabShadowColor() {
        return isMaterial3Fab() ? 805306368 : 536870912;
    }

    public static float getFabCornerRadiusDp() {
        if (ExteraConfig.getSquareFab()) {
            return UIUtil.getFabSquareCornerRadiusDp(getFabSizeDp());
        }
        return getFabSizeDp() / 2.0f;
    }

    public static float getSubFabBackgroundRadiusDp() {
        if (ExteraConfig.getSquareFab()) {
            return isMaterial3Fab() ? 12.0f : 10.0f;
        }
        return (getFabSizeDp() - (getSubFabPaddingDp() * 2.0f)) / 2.0f;
    }
}
