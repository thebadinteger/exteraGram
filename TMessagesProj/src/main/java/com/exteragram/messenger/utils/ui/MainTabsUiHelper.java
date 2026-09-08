package com.exteragram.messenger.utils.ui;

import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.TextView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.config.BottomNavigationBar;
import me.vkryl.android.animator.BoolAnimator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundProviderBuilder;
import org.telegram.ui.MainTabsLayout;

public abstract class MainTabsUiHelper {
    public static float getMaterial3MainTabIconTopDp() {
        return 10.0f;
    }

    private static float lerp(float f, float f2, float f3) {
        return f + ((f2 - f) * f3);
    }

    public static boolean isMaterial3NavigationBar() {
        return ExteraConfig.getNewNavigationBarStyle();
    }

    public static int getTabsViewHeightDp() {
        return isMaterial3NavigationBar() ? 64 : 72;
    }

    public static int getTabsFabOffsetDp() {
        isMaterial3NavigationBar();
        return 64;
    }

    public static int getAdditionalNavigationBarHeight(boolean z) {
        if (!z || BottomNavigationBar.hidden() || BottomNavigationBar.floating()) {
            return 0;
        }
        return AndroidUtilities.dp(getTabsViewHeightDp());
    }

    public static int getFloatingTabsPadding(boolean z) {
        if (z && BottomNavigationBar.floating()) {
            return AndroidUtilities.dp(getTabsViewHeightDp());
        }
        return 0;
    }

    public static int getTabsFabOffset(boolean z) {
        if (z && BottomNavigationBar.visible()) {
            return AndroidUtilities.dp(getTabsFabOffsetDp());
        }
        return 0;
    }

    private static int getTabsViewHeight(int i) {
        if (isMaterial3NavigationBar()) {
            return AndroidUtilities.dp(64.0f) + i;
        }
        return AndroidUtilities.dp(72.0f);
    }

    private static int getTabsInnerPaddingVertical() {
        if (isMaterial3NavigationBar()) {
            return 0;
        }
        return AndroidUtilities.dp(12.0f);
    }

    public static void applyTabsLayoutStyle(MainTabsLayout mainTabsLayout) {
        boolean zIsMaterial3NavigationBar = isMaterial3NavigationBar();
        int iDp = zIsMaterial3NavigationBar ? 0 : AndroidUtilities.dp(12.0f);
        int tabsInnerPaddingVertical = getTabsInnerPaddingVertical();
        mainTabsLayout.setPadding(iDp, tabsInnerPaddingVertical, iDp, tabsInnerPaddingVertical);
        mainTabsLayout.setMaxWidth(zIsMaterial3NavigationBar ? 0 : AndroidUtilities.dp(344.0f));
        mainTabsLayout.setFillAvailableWidth(zIsMaterial3NavigationBar);
        mainTabsLayout.setSwipeSelectionEnabled(!zIsMaterial3NavigationBar);
        mainTabsLayout.setDrawTopDivider(zIsMaterial3NavigationBar);
    }

    public static void applyTabsBottomInset(MainTabsLayout mainTabsLayout, View view, int i) {
        int tabsViewHeight = getTabsViewHeight(i);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) mainTabsLayout.getLayoutParams();
        if (marginLayoutParams.height != tabsViewHeight) {
            marginLayoutParams.height = tabsViewHeight;
            mainTabsLayout.setLayoutParams(marginLayoutParams);
        }
        int tabsInnerPaddingVertical = getTabsInnerPaddingVertical() + (isMaterial3NavigationBar() ? i : 0);
        if (mainTabsLayout.getPaddingBottom() != tabsInnerPaddingVertical) {
            mainTabsLayout.setPadding(mainTabsLayout.getPaddingLeft(), mainTabsLayout.getPaddingTop(), mainTabsLayout.getPaddingRight(), tabsInnerPaddingVertical);
        }
        if (isMaterial3NavigationBar()) {
            i = 0;
        }
        view.setPadding(0, 0, 0, i);
    }

    public static Drawable createMainTabsScrimBackground(Theme.ResourcesProvider resourcesProvider, boolean z) {
        return createMainTabsScrimBackground(Theme.getColor(Theme.key_windowBackgroundWhite, resourcesProvider), z);
    }

    private static Drawable createMainTabsScrimBackground(int i, boolean z) {
        ShapeDrawable shapeDrawableCreateRoundRectDrawable;
        if (z) {
            shapeDrawableCreateRoundRectDrawable = Theme.createCircleDrawable(AndroidUtilities.dp(40.0f), i);
        } else {
            shapeDrawableCreateRoundRectDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(28.0f), i);
        }
        ShapeDrawable shapeDrawable = shapeDrawableCreateRoundRectDrawable;
        shapeDrawable.getPaint().setShadowLayer(AndroidUtilities.dp(6.0f), 0.0f, AndroidUtilities.dp(1.0f), Theme.multAlpha(-16777216, 0.15f));
        return !isMaterial3NavigationBar() ? shapeDrawable : new InsetDrawable((Drawable) shapeDrawable, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
    }

    public static float getBackgroundRadius() {
        if (isMaterial3NavigationBar()) {
            return 0.0f;
        }
        return AndroidUtilities.dp(28.0f);
    }

    public static int getBackgroundInset() {
        if (isMaterial3NavigationBar()) {
            return 0;
        }
        return AndroidUtilities.dp(7.666f);
    }

    public static float getMaterial3MainTabAvatarTopDp() {
        return getMaterial3MainTabIconTopDp() + 1.0f;
    }

    public static void applyMaterial3MainTabStyle(TextView textView, BoolAnimator boolAnimator) {
        boolAnimator.setDuration(500L);
        boolAnimator.setInterpolator(CubicBezierInterpolator.Emphasized);
        textView.setIncludeFontPadding(false);
        textView.setLetterSpacing(0.04166667f);
        textView.setLayoutParams(LayoutHelper.createFrame(-1, 16.0f, 49, 0.0f, 42.0f, 0.0f, 0.0f));
    }

    public static void setMaterial3MainTabSelected(BoolAnimator boolAnimator, BoolAnimator boolAnimator2, boolean z, boolean z2) {
        Interpolator interpolator;
        boolAnimator.setValue(z, z2);
        boolAnimator2.setDuration(z ? 100L : 200L);
        if (z) {
            interpolator = CubicBezierInterpolator.Emphasized;
        } else {
            interpolator = CubicBezierInterpolator.EmphasizedAccelerate;
        }
        boolAnimator2.setInterpolator(interpolator);
        boolAnimator2.setValue(z, z2);
    }

    public static float getMainTabCounterCenterY(boolean z) {
        return z ? AndroidUtilities.dp(getMaterial3MainTabIconTopDp() + 6.0f) : AndroidUtilities.dpf2(10.0f);
    }

    public static float getSelectedBackgroundScaleX(boolean z, float f) {
        return lerp(z ? 0.4f : 0.6f, 1.0f, f);
    }

    public static float getSelectedBackgroundScaleY(boolean z, float f) {
        if (z) {
            return 1.0f;
        }
        return getSelectedBackgroundScaleX(false, f);
    }

    public static int getMainTabSelectedIndicatorColor(int i, float f) {
        return Theme.multAlpha(i, f * 0.125f);
    }

    public static void setMainTabSelectedIndicatorBounds(RectF rectF, float f, int i) {
        float fMin = Math.min(AndroidUtilities.dp(56.0f), Math.max(0.0f, f - (AndroidUtilities.dp(4.0f) * 2.0f)));
        float fMin2 = Math.min(AndroidUtilities.dp(32.0f), i);
        float f2 = (f - fMin) / 2.0f;
        float fDp = AndroidUtilities.dp(6.0f);
        rectF.set(f2, fDp, fMin + f2, fMin2 + fDp);
    }

    public static void applyBackgroundStroke(BlurredBackgroundProviderBuilder blurredBackgroundProviderBuilder) {
        if (isMaterial3NavigationBar()) {
            blurredBackgroundProviderBuilder.setStrokeColorTop(0, 0).setStrokeColorBottom(0, 0).setStrokeColorFull(0, 0).setStrokeWidth(0.0f, 0.0f);
        } else {
            blurredBackgroundProviderBuilder.setStrokeColorTop(285212672, 117440511).setStrokeColorBottom(536870912, 301989887).setStrokeWidth(AndroidUtilities.dpf2(0.4f), AndroidUtilities.dpf2(0.4f));
        }
    }

    public static void setBlurBounds(RectF rectF, View view, int i) {
        int measuredHeight;
        int iDp;
        if (isMaterial3NavigationBar()) {
            measuredHeight = view.getMeasuredHeight();
            iDp = (measuredHeight - AndroidUtilities.dp(64.0f)) - i;
        } else {
            measuredHeight = (view.getMeasuredHeight() - i) - AndroidUtilities.dp(8.0f);
            iDp = measuredHeight - AndroidUtilities.dp(56.0f);
        }
        rectF.set(0.0f, iDp, view.getMeasuredWidth(), measuredHeight);
    }
}
