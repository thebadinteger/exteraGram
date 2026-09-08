package com.exteragram.messenger.utils.ui;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.PathInterpolator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.ViewPagerActivity;

public final class PredictiveBackAnimationHelper {
    private float currentCornerRadius;
    private float initialTouchY;
    private float interpolatedProgress;
    private float progress;
    private float startCornerRadius;
    private float targetCornerRadius;
    private final RectF startClosingRect = new RectF();
    private final RectF targetClosingRect = new RectF();
    private final RectF currentClosingRect = new RectF();
    private final RectF startEnteringRect = new RectF();
    private final RectF targetEnteringRect = new RectF();
    private final RectF currentEnteringRect = new RectF();
    private final RectF commitStartClosingRect = new RectF();
    private final RectF commitTargetClosingRect = new RectF();
    private final RectF commitStartEnteringRect = new RectF();
    private final RectF commitTargetEnteringRect = new RectF();
    private final Interpolator gestureInterpolator = new PathInterpolator(0.1f, 0.1f, 0.0f, 1.0f);
    private final Interpolator postCommitInterpolator = createEmphasizedInterpolator();
    private final Interpolator verticalMoveInterpolator = new DecelerateInterpolator();
    private float closingAlpha = 1.0f;
    private float scrimAlphaMultiplier = 1.0f;

    public int getPostCommitDuration() {
        return 375;
    }

    public static Drawable getTransitionBackground(List<BaseFragment> list, BaseFragment baseFragment) {
        BaseFragment currentVisibleFragment;
        if (list != null && list.size() > 1) {
            baseFragment = list.get(list.size() - 2);
        }
        while ((baseFragment instanceof ViewPagerActivity) && (currentVisibleFragment = ((ViewPagerActivity) baseFragment).getCurrentVisibleFragment()) != null && currentVisibleFragment != baseFragment) {
            baseFragment = currentVisibleFragment;
        }
        if (baseFragment instanceof ProfileActivity) {
            return new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray, baseFragment.getResourceProvider()));
        }
        View view = baseFragment != null ? baseFragment.fragmentView : null;
        Drawable background = view != null ? view.getBackground() : null;
        if ((background instanceof ColorDrawable) && Color.alpha(((ColorDrawable) background).getColor()) == 0) {
            background = null;
        }
        if (background != null) {
            return background;
        }
        return new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray, baseFragment != null ? baseFragment.getResourceProvider() : null));
    }

    public static void drawTransitionBackground(Canvas canvas, Drawable drawable, int i, int i2, Rect rect) {
        if (drawable == null) {
            return;
        }
        drawable.copyBounds(rect);
        drawable.setBounds(0, 0, i, i2);
        drawable.draw(canvas);
        drawable.setBounds(rect);
    }

    public void start(int i, int i2, float f, boolean z, float f2) {
        this.initialTouchY = f;
        this.progress = 0.0f;
        this.interpolatedProgress = 0.0f;
        this.closingAlpha = 1.0f;
        this.scrimAlphaMultiplier = 1.0f;
        this.startCornerRadius = f2;
        this.targetCornerRadius = Math.max(f2, AndroidUtilities.dp(40.0f));
        this.currentCornerRadius = f2;
        this.startClosingRect.set(0.0f, 0.0f, Math.max(1, i), Math.max(1, i2));
        this.targetClosingRect.set(this.startClosingRect);
        scaleCentered(this.targetClosingRect, 0.85f);
        if (z) {
            RectF rectF = this.targetClosingRect;
            rectF.offset((this.startClosingRect.right - rectF.right) - AndroidUtilities.dp(8.0f), 0.0f);
        }
        this.currentClosingRect.set(this.startClosingRect);
        this.startEnteringRect.set(this.startClosingRect);
        scaleCentered(this.startEnteringRect, Utilities.clamp((this.startClosingRect.height() - (f2 * 2.0f)) / this.startClosingRect.height(), 0.95f, 0.85f));
        RectF rectF2 = this.startEnteringRect;
        rectF2.offset(-Math.max(rectF2.width() * 0.14999998f, AndroidUtilities.dp(96.0f)), 0.0f);
        this.targetEnteringRect.set(this.startEnteringRect);
        scaleCentered(this.targetEnteringRect, 0.85f);
        this.currentEnteringRect.set(this.startEnteringRect);
    }

    public void update(float f, float f2) {
        float fClamp01 = Utilities.clamp01(f);
        this.progress = fClamp01;
        float interpolation = this.gestureInterpolator.getInterpolation(fClamp01);
        this.interpolatedProgress = interpolation;
        this.closingAlpha = 1.0f;
        this.scrimAlphaMultiplier = 1.0f;
        interpolate(this.currentClosingRect, this.startClosingRect, this.targetClosingRect, interpolation);
        float yOffset = getYOffset(this.currentClosingRect.height(), f2);
        this.currentClosingRect.offset(0.0f, yOffset);
        interpolate(this.currentEnteringRect, this.startEnteringRect, this.targetEnteringRect, this.interpolatedProgress);
        this.currentEnteringRect.offset(0.0f, yOffset);
        this.currentCornerRadius = AndroidUtilities.lerp(this.startCornerRadius, this.targetCornerRadius, this.interpolatedProgress);
    }

    private float getYOffset(float f, float f2) {
        float fHeight = this.startClosingRect.height();
        float f3 = f2 - this.initialTouchY;
        float f4 = fHeight / 2.0f;
        return (f3 < 0.0f ? -1.0f : 1.0f) * this.verticalMoveInterpolator.getInterpolation(Math.min(f4, Math.abs(f3)) / f4) * Math.max(0.0f, ((fHeight - f) / 2.0f) - AndroidUtilities.dp(8.0f));
    }

    public void getClosingRect(RectF rectF) {
        rectF.set(this.currentClosingRect);
    }

    public void getEnteringRect(RectF rectF) {
        rectF.set(this.currentEnteringRect);
    }

    public float getClosingScale() {
        return this.currentClosingRect.width() / this.startClosingRect.width();
    }

    public float getEnteringScale() {
        return this.currentEnteringRect.width() / this.startClosingRect.width();
    }

    public float getClosingAlpha() {
        return this.closingAlpha;
    }

    public int getScrimAlpha(boolean z) {
        return (int) ((z ? 0.8f : 0.2f) * 255.0f * this.scrimAlphaMultiplier);
    }

    public void setScrimAlphaMultiplier(float f) {
        this.scrimAlphaMultiplier = Utilities.clamp01(f);
    }

    public float getCornerRadius() {
        return this.currentCornerRadius;
    }

    public float getProgress() {
        return this.progress;
    }

    public float getSlideDistance() {
        return this.interpolatedProgress * AndroidUtilities.dp(336.0f);
    }

    public void prepareCommit() {
        this.commitStartClosingRect.set(this.currentClosingRect);
        this.commitStartEnteringRect.set(this.currentEnteringRect);
        this.commitTargetEnteringRect.set(this.startClosingRect);
        this.commitTargetClosingRect.set(this.startClosingRect);
        this.commitTargetClosingRect.offset(this.currentClosingRect.left + AndroidUtilities.dp(96.0f), 0.0f);
    }

    public void updateCommitProgress(float f) {
        float fClamp01 = Utilities.clamp01(f);
        float interpolation = this.postCommitInterpolator.getInterpolation(fClamp01);
        this.closingAlpha = Math.max(1.0f - (5.0f * fClamp01), 0.0f);
        this.scrimAlphaMultiplier = 1.0f - fClamp01;
        interpolate(this.currentClosingRect, this.commitStartClosingRect, this.commitTargetClosingRect, interpolation);
        interpolate(this.currentEnteringRect, this.commitStartEnteringRect, this.commitTargetEnteringRect, interpolation);
        this.currentCornerRadius = AndroidUtilities.lerp(this.targetCornerRadius, this.startCornerRadius, interpolation);
    }

    public void reset() {
        this.startClosingRect.setEmpty();
        this.targetClosingRect.setEmpty();
        this.currentClosingRect.setEmpty();
        this.startEnteringRect.setEmpty();
        this.targetEnteringRect.setEmpty();
        this.currentEnteringRect.setEmpty();
        this.commitStartClosingRect.setEmpty();
        this.commitTargetClosingRect.setEmpty();
        this.commitStartEnteringRect.setEmpty();
        this.commitTargetEnteringRect.setEmpty();
        this.progress = 0.0f;
        this.interpolatedProgress = 0.0f;
        this.closingAlpha = 1.0f;
        this.scrimAlphaMultiplier = 1.0f;
        this.initialTouchY = 0.0f;
        this.startCornerRadius = 0.0f;
        this.targetCornerRadius = 0.0f;
        this.currentCornerRadius = 0.0f;
    }

    private static void interpolate(RectF rectF, RectF rectF2, RectF rectF3, float f) {
        rectF.set(AndroidUtilities.lerp(rectF2.left, rectF3.left, f), AndroidUtilities.lerp(rectF2.top, rectF3.top, f), AndroidUtilities.lerp(rectF2.right, rectF3.right, f), AndroidUtilities.lerp(rectF2.bottom, rectF3.bottom, f));
    }

    private static void scaleCentered(RectF rectF, float f) {
        float fCenterX = rectF.centerX();
        float fCenterY = rectF.centerY();
        float fWidth = (rectF.width() * f) / 2.0f;
        float fHeight = (rectF.height() * f) / 2.0f;
        rectF.set(fCenterX - fWidth, fCenterY - fHeight, fCenterX + fWidth, fCenterY + fHeight);
    }

    private static Interpolator createEmphasizedInterpolator() {
        Path path = new Path();
        path.moveTo(0.0f, 0.0f);
        path.cubicTo(0.05f, 0.0f, 0.133333f, 0.06f, 0.166666f, 0.4f);
        path.cubicTo(0.208333f, 0.82f, 0.25f, 1.0f, 1.0f, 1.0f);
        return new PathInterpolator(path);
    }
}
