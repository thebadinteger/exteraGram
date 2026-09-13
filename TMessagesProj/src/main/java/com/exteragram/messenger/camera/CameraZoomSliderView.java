package com.exteragram.messenger.camera;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.SeekBar;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import java.util.Arrays;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;

public abstract class CameraZoomSliderView extends View {
    private float animatedControlWidth;
    private float animatedSelectorOffset;
    private final Runnable autoCollapseRunnable;
    private final Paint backgroundPaint;
    private final RectF bubbleBounds;
    private final Paint bubblePaint;
    private final Paint bubbleTextPaint;
    private final RectF compactBounds;
    private boolean compactGestureDown;
    private final RectF compactTouchBounds;
    private final RectF controlBounds;
    private float displayNormalizationFactor;
    private float downX;
    private float downY;
    private int dragPrimarySegment;
    private boolean dragStartedFromCompact;
    private float dragTick;
    private boolean dragging;
    private final Paint edgeFadePaint;
    private boolean expanded;
    private ValueAnimator expandedAnimator;
    private float expandedProgress;
    private boolean externalZoomGesture;
    private int intervalCount;
    private int lastDescribedZoom;
    private float lastTouchX;
    private final Runnable longPressRunnable;
    private final Paint markerPaint;
    private float maxZoom;
    private float minZoom;
    private int minorTickColor;
    private boolean movedPastSlop;
    private int onSecondaryFixedColor;
    private OnZoomChangeListener onZoomChangeListener;
    private int oneXTick;
    private float pendingConfigurationSelectorX;
    private int pressedToggleIndex;
    private int primaryColor;
    private final SparseArray<String> primaryLabels;
    private int protectionBackgroundColor;
    private int[] rebuiltPrimaryTickIndices;
    private final RectF rulerBounds;
    private final Paint rulerLabelPaint;
    private float[] rulerStops;
    private final RectF rulerTouchBounds;
    private int secondaryFixedColor;
    private boolean selectedShowsStopValue;
    private int selectedToggleIndex;
    private final Paint selectedToggleTextPaint;
    private final RectF selectorBounds;
    private final Paint selectorPaint;
    private final SpringAnimation selectorSpring;
    private float stickyDistance;
    private float stickyFactor;
    private int stickyTick;
    private final Paint tickPaint;
    private float tickSpacing;
    private float[] toggleStops;
    private final Paint toggleTextPaint;
    private final int touchSlop;
    private int unselectedToggleColor;
    private VelocityTracker velocityTracker;
    private final SpringAnimation widthSpring;
    private float zoom;
    private ValueAnimator zoomAnimator;
    private static final double LOG_2 = Math.log(2.0d);
    private static final TimeInterpolator ZOOM_INTERPOLATOR = new ZoomLookupInterpolator();
    private static final TimeInterpolator MORPH_INTERPOLATOR = new CubicBezierInterpolator(0.4f, 0.0f, 0.2f, 1.0f);
    private static final PorterDuffXfermode XOR_XFERMODE = new PorterDuffXfermode(PorterDuff.Mode.XOR);
    private static final PorterDuffXfermode DST_OVER_XFERMODE = new PorterDuffXfermode(PorterDuff.Mode.DST_OVER);
    private static final PorterDuffXfermode DST_IN_XFERMODE = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    private static final FloatPropertyCompat<CameraZoomSliderView> CONTROL_WIDTH = new FloatPropertyCompat<CameraZoomSliderView>("controlWidth") { // from class: com.exteragram.messenger.camera.CameraZoomSliderView.1
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(CameraZoomSliderView cameraZoomSliderView) {
            return cameraZoomSliderView.animatedControlWidth;
        }

        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(CameraZoomSliderView cameraZoomSliderView, float f) {
            cameraZoomSliderView.animatedControlWidth = Math.max(0.0f, f);
            cameraZoomSliderView.invalidate();
        }
    };
    private static final FloatPropertyCompat<CameraZoomSliderView> SELECTOR_OFFSET = new FloatPropertyCompat<CameraZoomSliderView>("selectorOffset") { // from class: com.exteragram.messenger.camera.CameraZoomSliderView.2
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(CameraZoomSliderView cameraZoomSliderView) {
            return cameraZoomSliderView.animatedSelectorOffset;
        }

        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(CameraZoomSliderView cameraZoomSliderView, float f) {
            cameraZoomSliderView.animatedSelectorOffset = f;
            cameraZoomSliderView.invalidate();
        }
    };

    public interface OnZoomChangeListener {
        void onZoomChanged(float f);
    }

    public abstract boolean drawPillBackground(Canvas canvas, RectF rectF, float f);

    public CameraZoomSliderView(Context context) {
        this(context, null);
    }

    public CameraZoomSliderView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public CameraZoomSliderView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.backgroundPaint = new Paint(1);
        this.selectorPaint = new Paint(1);
        this.toggleTextPaint = new Paint(129);
        this.selectedToggleTextPaint = new Paint(129);
        this.tickPaint = new Paint(1);
        this.markerPaint = new Paint(1);
        this.rulerLabelPaint = new Paint(129);
        this.edgeFadePaint = new Paint(1);
        this.bubblePaint = new Paint(1);
        this.bubbleTextPaint = new Paint(129);
        this.controlBounds = new RectF();
        this.compactBounds = new RectF();
        this.rulerBounds = new RectF();
        this.compactTouchBounds = new RectF();
        this.rulerTouchBounds = new RectF();
        this.selectorBounds = new RectF();
        this.bubbleBounds = new RectF();
        this.primaryLabels = new SparseArray<>();
        this.minZoom = 0.5f;
        this.maxZoom = 30.0f;
        this.zoom = 1.0f;
        this.toggleStops = new float[]{0.5f, 1.0f, 2.0f, 5.0f};
        this.rulerStops = new float[]{0.5f, 1.0f, 2.0f, 5.0f, 10.0f, 30.0f};
        this.rebuiltPrimaryTickIndices = new int[0];
        this.oneXTick = -1;
        this.protectionBackgroundColor = -1728053248;
        this.primaryColor = -5715974;
        this.minorTickColor = -1842205;
        this.secondaryFixedColor = -2628615;
        this.onSecondaryFixedColor = -14142651;
        this.unselectedToggleColor = -1;
        this.displayNormalizationFactor = 1.0f;
        this.pendingConfigurationSelectorX = Float.NaN;
        this.pressedToggleIndex = -1;
        this.lastDescribedZoom = Integer.MIN_VALUE;
        this.stickyTick = -1;
        this.dragPrimarySegment = Integer.MIN_VALUE;
        this.longPressRunnable = new Runnable() { // from class: com.exteragram.messenger.camera.CameraZoomSliderView.3
            @Override // java.lang.Runnable
            public void run() {
                if (!CameraZoomSliderView.this.compactGestureDown || CameraZoomSliderView.this.movedPastSlop || CameraZoomSliderView.this.expanded) {
                    return;
                }
                CameraZoomSliderView.this.dragStartedFromCompact = true;
                CameraZoomSliderView cameraZoomSliderView = CameraZoomSliderView.this;
                cameraZoomSliderView.beginDrag(cameraZoomSliderView.downX);
                CameraZoomSliderView.this.setExpanded(true, true);
            }
        };
        this.autoCollapseRunnable = new Runnable() { // from class: com.exteragram.messenger.camera.CameraZoomSliderView.4
            @Override // java.lang.Runnable
            public void run() {
                if (!CameraZoomSliderView.this.expanded || CameraZoomSliderView.this.dragging || CameraZoomSliderView.this.externalZoomGesture) {
                    return;
                }
                CameraZoomSliderView.this.setExpanded(false, true);
            }
        };
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        this.tickSpacing = Math.max(1.0f, Math.round(AndroidUtilities.dp(8.0f)));
        this.widthSpring = createSpring(CONTROL_WIDTH, 1.0f, 3800.0f, AndroidUtilities.dp(0.1f));
        this.selectorSpring = createSpring(SELECTOR_OFFSET, 0.8f, 800.0f, 1.0f);
        configurePaints();
        rebuildScale();
        int iFindToggleSegment = findToggleSegment(this.zoom);
        this.selectedToggleIndex = iFindToggleSegment;
        this.animatedSelectorOffset = getSelectorOffset(iFindToggleSegment);
        this.animatedControlWidth = getCompactWidth();
        setClickable(true);
        setFocusable(true);
        updateAccessibilityDescription();
    }

    private SpringAnimation createSpring(FloatPropertyCompat<CameraZoomSliderView> floatPropertyCompat, float f, float f2, float f3) {
        SpringAnimation springAnimation = new SpringAnimation(this, floatPropertyCompat);
        springAnimation.setSpring(new SpringForce().setDampingRatio(f).setStiffness(f2));
        springAnimation.setMinimumVisibleChange(f3);
        return springAnimation;
    }

    public void prepareZoomConfigurationTransition() {
        this.pendingConfigurationSelectorX = Float.NaN;
        if (!isLaidOut() || this.toggleStops.length == 0) {
            return;
        }
        updateLayoutBounds();
        if (this.compactBounds.isEmpty()) {
            return;
        }
        this.pendingConfigurationSelectorX = this.compactBounds.left + this.animatedSelectorOffset;
    }

    public void cancelZoomConfigurationTransition() {
        this.pendingConfigurationSelectorX = Float.NaN;
    }

    public void setZoomConfiguration(float f, float f2, float[] fArr, float[] fArr2, float f3, boolean z) {
        if (!Float.isFinite(f) || !Float.isFinite(f2) || f <= 0.0f || f2 <= f) {
            throw new IllegalArgumentException("Zoom range must satisfy 0 < minZoom < maxZoom");
        }
        float f4 = this.pendingConfigurationSelectorX;
        this.pendingConfigurationSelectorX = Float.NaN;
        boolean z2 = z && isLaidOut() && Float.isFinite(f4);
        this.minZoom = f;
        this.maxZoom = f2;
        this.toggleStops = sanitizeStops(fArr, f, f2);
        this.rulerStops = sanitizeStops(fArr2, f, f2);
        this.zoom = clamp(f3, f, f2);
        rebuildScale();
        if (z2) {
            this.selectedToggleIndex = findToggleSegment(this.zoom);
            float fMax = Math.max(0.0f, (getWidth() - getPaddingLeft()) - getPaddingRight());
            float fCenteredChildLeft = centeredChildLeft(fMax, Math.min(Math.max(0.0f, fMax - (AndroidUtilities.dp(8.0f) * 2.0f)), Math.round(getCompactWidth())));
            this.selectorSpring.cancel();
            this.animatedSelectorOffset = f4 - fCenteredChildLeft;
            animateSelectorTo(this.selectedToggleIndex, true);
            updateTargetControlWidth(true);
        } else {
            syncSelectedToggle(false);
            updateTargetControlWidth(false);
        }
        updateAccessibilityDescription();
        requestLayout();
        invalidate();
    }

    public void setZoom(float f) {
        setZoom(f, false);
    }

    public void setZoom(float f, boolean z) {
        float fClamp = clamp(f, this.minZoom, this.maxZoom);
        cancelZoomAnimator();
        if (z && isLaidOut()) {
            animateZoomTo(fClamp, false);
        } else {
            setZoomInternal(fClamp, false, true);
        }
    }

    public float getZoom() {
        return this.zoom;
    }

    public float getMinimumZoom() {
        return this.minZoom;
    }

    public float getMaximumZoom() {
        return this.maxZoom;
    }

    public void setExternalZoomGestureActive(boolean z) {
        if (this.externalZoomGesture == z) {
            return;
        }
        this.externalZoomGesture = z;
        if (z) {
            setExpanded(true, true);
        } else {
            resetAutoCollapseTimeout();
        }
    }

    public void setExpanded(boolean z, boolean z2) {
        float f = z ? 1.0f : 0.0f;
        float expandedBackgroundWidth = z ? getExpandedBackgroundWidth() : getCompactWidth();
        if (this.expanded == z && Math.abs(this.expandedProgress - f) < 1.0E-4f && Math.abs(this.animatedControlWidth - expandedBackgroundWidth) < 0.1f) {
            if (z) {
                resetAutoCollapseTimeout();
                return;
            }
            return;
        }
        this.expanded = z;
        removeCallbacks(this.longPressRunnable);
        removeCallbacks(this.autoCollapseRunnable);
        animateExpandedProgress(f, z2);
        if (!z2 || !isLaidOut()) {
            this.widthSpring.cancel();
            this.animatedControlWidth = expandedBackgroundWidth;
            invalidate();
        } else {
            this.widthSpring.animateToFinalPosition(expandedBackgroundWidth);
        }
        if (z && !this.dragging) {
            resetAutoCollapseTimeout();
        }
        updateAccessibilityDescription();
        sendAccessibilityEvent(2048);
    }

    public void setColors(int i, int i2, int i3, int i4, int i5) {
        this.protectionBackgroundColor = i;
        this.minorTickColor = i2;
        this.primaryColor = i3;
        this.secondaryFixedColor = i4;
        this.onSecondaryFixedColor = i5;
        configurePaintColors();
        invalidate();
    }

    public void setToggleTextColor(int i) {
        this.unselectedToggleColor = i;
        invalidate();
    }

    public void setDisplayNormalizationFactor(float f) {
        if (!Float.isFinite(f) || f <= 0.0f) {
            f = 1.0f;
        }
        if (Math.abs(this.displayNormalizationFactor - f) < 1.0E-4f) {
            return;
        }
        this.displayNormalizationFactor = f;
        rebuildScale();
        syncSelectedToggle(false);
        updateAccessibilityDescription();
        invalidate();
    }

    public void setOnZoomChangeListener(OnZoomChangeListener onZoomChangeListener) {
        this.onZoomChangeListener = onZoomChangeListener;
    }

    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(View.resolveSize(Math.round(Math.max(getExpandedBackgroundWidth(), getCompactWidth()) + (AndroidUtilities.dp(8.0f) * 2.0f)) + getPaddingLeft() + getPaddingRight(), i), View.resolveSize(Math.round(getBubbleHeight() + (AndroidUtilities.dp(8.0f) * 2.0f) + AndroidUtilities.dp(64.0f)) + getPaddingTop() + getPaddingBottom(), i2));
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        updateLayoutBounds();
        boolean z = !this.controlBounds.isEmpty() && drawPillBackground(canvas, this.controlBounds, (float) AndroidUtilities.dp(24.0f));
        float fClamp = clamp(1.0f - this.expandedProgress, 0.0f, 1.0f);
        float fClamp2 = clamp(this.expandedProgress, 0.0f, 1.0f);
        int iSaveLayer = canvas.saveLayer(0.0f, 0.0f, getWidth(), getHeight(), null);
        if (fClamp > 0.001f) {
            int iSaveLayerAlpha = canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), Math.round(fClamp * 255.0f));
            drawToggleRow(canvas, 1.0f);
            canvas.restoreToCount(iSaveLayerAlpha);
        }
        if (fClamp2 > 0.001f) {
            int iSaveLayerAlpha2 = canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), Math.round(fClamp2 * 255.0f));
            drawBubble(canvas, 1.0f);
            drawRuler(canvas, 1.0f);
            canvas.restoreToCount(iSaveLayerAlpha2);
        }
        if (!z) {
            drawProtectionBackground(canvas);
        }
        canvas.restoreToCount(iSaveLayer);
    }

    private void updateLayoutBounds() {
        float fMax = Math.max(0.0f, (getWidth() - getPaddingLeft()) - getPaddingRight());
        float fMax2 = Math.max(0.0f, fMax - (AndroidUtilities.dp(8.0f) * 2.0f));
        float height = getHeight() - getPaddingBottom();
        float fDp = height - AndroidUtilities.dp(64.0f);
        float fDp2 = AndroidUtilities.dp(48.0f) + fDp;
        float fMin = Math.min(fMax2, Math.round(getExpandedBackgroundWidth()));
        float fMin2 = Math.min(fMax2, Math.round(getCompactWidth()));
        if (this.animatedControlWidth <= 0.0f) {
            if (!this.expanded) {
                fMin = fMin2;
            }
            this.animatedControlWidth = fMin;
        }
        float fMin3 = Math.min(fMax2, Math.round(this.animatedControlWidth));
        float fCenteredChildLeft = centeredChildLeft(fMax, fMin3);
        this.controlBounds.set(fCenteredChildLeft, fDp, fMin3 + fCenteredChildLeft, fDp2);
        float fCenteredChildLeft2 = centeredChildLeft(fMax, fMin2);
        this.compactBounds.set(fCenteredChildLeft2, fDp, fMin2 + fCenteredChildLeft2, fDp2);
        float fMin4 = Math.min(fMax2, getExpandedRulerWidth());
        float fCenteredChildLeft3 = centeredChildLeft(fMax, fMin4);
        this.rulerBounds.set(fCenteredChildLeft3, fDp, fMin4 + fCenteredChildLeft3, height);
        this.compactTouchBounds.set(this.compactBounds);
        this.compactTouchBounds.bottom = height;
        this.rulerTouchBounds.set(this.rulerBounds);
    }

    private float centeredChildLeft(float f, float f2) {
        return getPaddingLeft() + ((int) ((f - f2) / 2.0f));
    }

    private void drawProtectionBackground(Canvas canvas) {
        if (this.controlBounds.isEmpty()) {
            return;
        }
        this.backgroundPaint.setColor(this.protectionBackgroundColor);
        this.backgroundPaint.setXfermode(DST_OVER_XFERMODE);
        canvas.drawRoundRect(this.controlBounds, AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f), this.backgroundPaint);
        this.backgroundPaint.setXfermode(null);
    }

    private void drawToggleRow(Canvas canvas, float f) {
        if (this.toggleStops.length == 0 || this.compactBounds.isEmpty()) {
            return;
        }
        float fDp = AndroidUtilities.dp(2.0f);
        float fDp2 = AndroidUtilities.dp(44.0f);
        float fRound = this.compactBounds.left + Math.round(this.animatedSelectorOffset) + fDp;
        RectF rectF = this.selectorBounds;
        float f2 = this.compactBounds.top;
        rectF.set(fRound, f2 + fDp, fRound + fDp2, f2 + fDp + fDp2);
        drawToggleLabels(canvas, f, this.unselectedToggleColor);
        if (this.expanded) {
            return;
        }
        this.selectorPaint.setColor(Theme.multAlpha(this.secondaryFixedColor, f));
        this.selectorPaint.setXfermode(XOR_XFERMODE);
        canvas.drawOval(this.selectorBounds, this.selectorPaint);
        this.selectorPaint.setColor(Theme.multAlpha(this.onSecondaryFixedColor, f));
        this.selectorPaint.setXfermode(DST_OVER_XFERMODE);
        canvas.drawOval(this.selectorBounds, this.selectorPaint);
        this.selectorPaint.setXfermode(null);
    }

    private void drawToggleLabels(Canvas canvas, float f, int i) {
        int i2 = 0;
        while (i2 < this.toggleStops.length) {
            Paint paint = i2 == this.selectedToggleIndex ? this.selectedToggleTextPaint : this.toggleTextPaint;
            paint.setColor(Theme.multAlpha(i, f));
            float fDp = this.compactBounds.left + ((i2 + 0.5f) * AndroidUtilities.dp(48.0f));
            Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
            int i3 = fontMetricsInt.descent - fontMetricsInt.ascent;
            RectF rectF = this.compactBounds;
            canvas.drawText(getToggleLabel(i2), fDp, (rectF.top + ((int) ((rectF.height() - i3) / 2.0f))) - fontMetricsInt.ascent, paint);
            i2++;
        }
    }

    private void drawRuler(Canvas canvas, float f) {
        if (this.rulerBounds.isEmpty()) {
            return;
        }
        RectF rectF = this.rulerBounds;
        float f2 = rectF.left;
        RectF rectF2 = this.controlBounds;
        int iSaveLayer = canvas.saveLayer(f2, rectF2.top, rectF.right, rectF2.bottom, null);
        RectF rectF3 = this.rulerBounds;
        float f3 = rectF3.left;
        RectF rectF4 = this.controlBounds;
        canvas.clipRect(f3, rectF4.top, rectF3.right, rectF4.bottom);
        float fCenterX = this.rulerBounds.centerX();
        float fZoomToTick = zoomToTick(this.zoom);
        float fDp = this.controlBounds.bottom - AndroidUtilities.dp(22.0f);
        float fDp2 = this.controlBounds.bottom - AndroidUtilities.dp(23.0f);
        boolean z = false;
        int iMax = Math.max(0, ((int) Math.floor(fZoomToTick - ((this.rulerBounds.width() / 2.0f) / this.tickSpacing))) - 1);
        int iMin = Math.min(this.intervalCount, ((int) Math.ceil(((this.rulerBounds.width() / 2.0f) / this.tickSpacing) + fZoomToTick)) + 1);
        Paint.FontMetricsInt fontMetricsInt = this.rulerLabelPaint.getFontMetricsInt();
        float fDp3 = ((this.controlBounds.bottom - AndroidUtilities.dp(4.0f)) - (fontMetricsInt.descent - fontMetricsInt.ascent)) - fontMetricsInt.ascent;
        while (iMax <= iMin) {
            float f4 = ((iMax - fZoomToTick) * this.tickSpacing) + fCenterX;
            String str = this.primaryLabels.get(iMax);
            boolean z2 = str != null ? true : z;
            this.tickPaint.setColor(Theme.multAlpha(z2 ? this.primaryColor : this.minorTickColor, f));
            this.tickPaint.setStrokeWidth(AndroidUtilities.dp(1.0f));
            int i = iMax;
            canvas.drawLine(f4, fDp - AndroidUtilities.dp(z2 ? 12.0f : 6.0f), f4, fDp, this.tickPaint);
            if (z2) {
                this.rulerLabelPaint.setColor(Theme.multAlpha(this.primaryColor, f));
                canvas.drawText(str, f4, fDp3, this.rulerLabelPaint);
            }
            iMax = i + 1;
            z = false;
        }
        this.markerPaint.setColor(Theme.multAlpha(this.primaryColor, f));
        this.markerPaint.setStrokeWidth(AndroidUtilities.dp(4.0f));
        canvas.drawLine(fCenterX, fDp2 - AndroidUtilities.dp(12.0f), fCenterX, fDp2, this.markerPaint);
        Paint paint = this.edgeFadePaint;
        RectF rectF5 = this.rulerBounds;
        paint.setShader(new LinearGradient(rectF5.left, 0.0f, rectF5.right, 0.0f, new int[]{855638016, -16777216, -16777216, 855638016}, new float[]{0.0f, 0.33333334f, 0.6666667f, 1.0f}, Shader.TileMode.CLAMP));
        this.edgeFadePaint.setXfermode(DST_IN_XFERMODE);
        RectF rectF6 = this.rulerBounds;
        float f5 = rectF6.left;
        RectF rectF7 = this.controlBounds;
        canvas.drawRect(f5, rectF7.top, rectF6.right, rectF7.bottom, this.edgeFadePaint);
        this.edgeFadePaint.setShader(null);
        this.edgeFadePaint.setXfermode(null);
        canvas.restoreToCount(iSaveLayer);
    }

    private void drawBubble(Canvas canvas, float f) {
        String bubble = formatBubble(this.zoom);
        if (bubble.isEmpty()) {
            return;
        }
        Paint.FontMetricsInt fontMetricsInt = this.bubbleTextPaint.getFontMetricsInt();
        float fMax = Math.max(AndroidUtilities.dp(40.0f), (float) Math.ceil(this.bubbleTextPaint.measureText(bubble))) + (AndroidUtilities.dp(4.0f) * 2.0f);
        float bubbleHeight = getBubbleHeight();
        float fDp = AndroidUtilities.dp(8.0f);
        float fCenterX = this.controlBounds.centerX();
        float f2 = bubbleHeight / 2.0f;
        float f3 = (this.controlBounds.top - fDp) - f2;
        float f4 = fMax / 2.0f;
        this.bubbleBounds.set(fCenterX - f4, f3 - f2, fCenterX + f4, f3 + f2);
        float fClamp = clamp(f, 0.0f, 1.0f);
        if (fClamp <= 0.001f) {
            return;
        }
        this.bubblePaint.setColor(this.secondaryFixedColor);
        this.bubbleTextPaint.setColor(this.onSecondaryFixedColor);
        float fDp2 = (this.bubbleBounds.top + AndroidUtilities.dp(6.0f)) - fontMetricsInt.ascent;
        int iSaveLayerAlpha = canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), Math.round(fClamp * 255.0f));
        canvas.drawRoundRect(this.bubbleBounds, f2, f2, this.bubblePaint);
        canvas.drawText(bubble, this.bubbleBounds.centerX(), fDp2, this.bubbleTextPaint);
        canvas.restoreToCount(iSaveLayerAlpha);
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (!isEnabled() || this.toggleStops.length == 0) {
            return false;
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            updateLayoutBounds();
            boolean z = this.expanded;
            if (!(z ? this.rulerTouchBounds : this.compactTouchBounds).contains(motionEvent.getX(), motionEvent.getY())) {
                return false;
            }
            this.downX = motionEvent.getX();
            float y = motionEvent.getY();
            this.downY = y;
            float f = this.downX;
            this.lastTouchX = f;
            this.movedPastSlop = false;
            this.compactGestureDown = !z;
            this.dragStartedFromCompact = false;
            this.pressedToggleIndex = (z || !this.compactBounds.contains(f, y)) ? -1 : findToggleIndexAt(this.downX);
            setPressed(true);
            requestParentIntercept(false);
            obtainVelocityTracker(motionEvent);
            if (z) {
                beginDrag(this.downX);
            } else {
                postDelayed(this.longPressRunnable, ViewConfiguration.getLongPressTimeout());
            }
            return true;
        }
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker != null) {
            velocityTracker.addMovement(motionEvent);
        }
        if (actionMasked == 2) {
            if (this.dragging) {
                float fHypot = (float) Math.hypot(motionEvent.getX() - this.downX, motionEvent.getY() - this.downY);
                if (!this.movedPastSlop && fHypot > this.touchSlop) {
                    this.movedPastSlop = true;
                }
                moveDrag(motionEvent.getX());
                return true;
            }
            if (!this.compactGestureDown) {
                return true;
            }
            float x = motionEvent.getX() - this.downX;
            float y2 = motionEvent.getY() - this.downY;
            if (!this.movedPastSlop && Math.hypot(x, y2) > this.touchSlop) {
                this.movedPastSlop = true;
                removeCallbacks(this.longPressRunnable);
                if (Math.abs(x) >= Math.abs(y2)) {
                    this.dragStartedFromCompact = true;
                    beginDrag(this.downX);
                    setExpanded(true, true);
                    moveDrag(motionEvent.getX());
                }
            }
            return true;
        }
        if (actionMasked != 1) {
            if (actionMasked == 3) {
                removeCallbacks(this.longPressRunnable);
                if (this.dragging) {
                    finishDrag(false);
                } else {
                    clearTouchState();
                }
                recycleVelocityTracker();
                return true;
            }
            return super.onTouchEvent(motionEvent);
        }
        removeCallbacks(this.longPressRunnable);
        boolean z2 = this.dragging;
        boolean z3 = this.movedPastSlop;
        if (z2) {
            if (!z3 && !this.dragStartedFromCompact) {
                setZoomFromRulerTap(motionEvent.getX());
            }
            finishDrag(true);
        } else if (!z3 && this.pressedToggleIndex >= 0) {
            int iFindToggleIndexAt = findToggleIndexAt(motionEvent.getX());
            if (this.compactBounds.contains(motionEvent.getX(), motionEvent.getY()) && iFindToggleIndexAt == this.pressedToggleIndex) {
                selectToggle(iFindToggleIndexAt);
            }
            clearTouchState();
            performClick();
        } else {
            clearTouchState();
        }
        recycleVelocityTracker();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void beginDrag(float f) {
        if (stopZoomAnimator()) {
            syncSelectedToggle(true);
        }
        this.dragging = true;
        this.lastTouchX = f;
        this.dragTick = zoomToTick(this.zoom);
        this.dragPrimarySegment = Integer.MIN_VALUE;
        this.stickyTick = -1;
        this.stickyDistance = 0.0f;
        this.stickyFactor = 0.0f;
        removeCallbacks(this.autoCollapseRunnable);
    }

    private void moveDrag(float f) {
        float fCopySign = f - this.lastTouchX;
        this.lastTouchX = f;
        if (Math.abs(fCopySign) < 1.0E-4f) {
            return;
        }
        float f2 = this.dragTick;
        float currentXVelocity = getCurrentXVelocity();
        if (this.stickyTick >= 0) {
            float fMax = Math.max(0.0f, (this.tickSpacing * this.stickyFactor) - this.stickyDistance);
            float fAbs = Math.abs(fCopySign);
            if (fAbs <= fMax) {
                this.stickyDistance += fAbs;
                setDragTick(this.stickyTick);
                return;
            } else {
                fCopySign = Math.copySign(fAbs - fMax, fCopySign);
                f2 = this.stickyTick;
                this.stickyTick = -1;
                this.stickyDistance = 0.0f;
                this.stickyFactor = 0.0f;
            }
        }
        if (this.rebuiltPrimaryTickIndices.length > 0) {
            int iFindPrimarySegment = findPrimarySegment(f2, fCopySign);
            int i = this.dragPrimarySegment;
            if (i == Integer.MIN_VALUE) {
                this.dragPrimarySegment = iFindPrimarySegment;
            } else if (iFindPrimarySegment != i) {
                this.dragPrimarySegment = iFindPrimarySegment;
                float fCalculateStickiness = calculateStickiness(currentXVelocity);
                if (fCalculateStickiness > 0.0f) {
                    int iMax = Math.max(i, iFindPrimarySegment);
                    float f3 = this.tickSpacing * fCalculateStickiness;
                    float f4 = iMax;
                    float fAbs2 = Math.abs(f2 - f4) * this.tickSpacing;
                    if (fAbs2 < f3) {
                        float f5 = f3 - fAbs2;
                        float fAbs3 = Math.abs(fCopySign);
                        if (fAbs3 <= f5) {
                            this.stickyTick = iMax;
                            this.stickyDistance = fAbs2 + fAbs3;
                            this.stickyFactor = fCalculateStickiness;
                            setDragTick(iMax);
                            return;
                        }
                        fCopySign = Math.copySign(fAbs3 - f5, fCopySign);
                        f2 = f4;
                    }
                }
            }
        }
        setDragTick(clamp(f2 - (fCopySign / this.tickSpacing), 0.0f, this.intervalCount));
    }

    private void setDragTick(float f) {
        float fClamp = clamp(f, 0.0f, this.intervalCount);
        this.dragTick = fClamp;
        setTickInternal(fClamp, true);
    }

    private void finishDrag(boolean z) {
        this.dragging = false;
        this.compactGestureDown = false;
        this.dragStartedFromCompact = false;
        this.stickyTick = -1;
        this.stickyDistance = 0.0f;
        this.stickyFactor = 0.0f;
        this.dragPrimarySegment = Integer.MIN_VALUE;
        this.pressedToggleIndex = -1;
        setPressed(false);
        requestParentIntercept(true);
        resetAutoCollapseTimeout();
        if (z) {
            performClick();
        }
    }

    private void setZoomFromRulerTap(float f) {
        animateZoomTo(tickToZoom(zoomToTick(this.zoom) + ((f - this.rulerBounds.centerX()) / this.tickSpacing)), true);
    }

    private void selectToggle(int i) {
        if (i >= 0) {
            float[] fArr = this.toggleStops;
            if (i >= fArr.length) {
                return;
            }
            if (i == this.selectedToggleIndex && this.selectedShowsStopValue) {
                return;
            }
            animateZoomTo(fArr[i], true, i);
        }
    }

    private void clearTouchState() {
        this.dragging = false;
        this.compactGestureDown = false;
        this.dragStartedFromCompact = false;
        this.movedPastSlop = false;
        this.pressedToggleIndex = -1;
        this.stickyTick = -1;
        this.stickyDistance = 0.0f;
        this.stickyFactor = 0.0f;
        this.dragPrimarySegment = Integer.MIN_VALUE;
        setPressed(false);
        requestParentIntercept(true);
    }

    @Override // android.view.View
    public boolean performClick() {
        super.performClick();
        return true;
    }

    @Override // android.view.View
    public CharSequence getAccessibilityClassName() {
        return (this.expanded ? SeekBar.class : View.class).getName();
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName(getAccessibilityClassName());
        accessibilityNodeInfo.setContentDescription(formatBubble(this.zoom));
        if (this.expanded) {
            accessibilityNodeInfo.setScrollable(true);
            accessibilityNodeInfo.setRangeInfo(AccessibilityNodeInfo.RangeInfo.obtain(1, this.minZoom, this.maxZoom, this.zoom));
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_FORWARD);
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SCROLL_BACKWARD);
            accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS);
            return;
        }
        accessibilityNodeInfo.addAction(AccessibilityNodeInfo.AccessibilityAction.ACTION_CLICK);
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        if (i == 16) {
            setExpanded(!this.expanded, true);
            return true;
        }
        if (i == 4096) {
            cancelZoomAnimator();
            setTickInternal(zoomToTick(this.zoom) + 1.0f, true);
            resetAutoCollapseTimeout();
            return true;
        }
        if (i == 8192) {
            cancelZoomAnimator();
            setTickInternal(zoomToTick(this.zoom) - 1.0f, true);
            resetAutoCollapseTimeout();
            return true;
        }
        if (i == AccessibilityNodeInfo.AccessibilityAction.ACTION_SET_PROGRESS.getId() && bundle != null && bundle.containsKey("android.view.accessibility.action.ARGUMENT_PROGRESS_VALUE")) {
            cancelZoomAnimator();
            setZoomInternal(bundle.getFloat("android.view.accessibility.action.ARGUMENT_PROGRESS_VALUE"), true, true);
            resetAutoCollapseTimeout();
            return true;
        }
        return super.performAccessibilityAction(i, bundle);
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        cancelTransientSprings();
        this.tickSpacing = Math.max(1.0f, Math.round(AndroidUtilities.dp(8.0f)));
        configurePaints();
        rebuildScale();
        settleTransientAnimationValues();
        requestLayout();
        invalidate();
    }

    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        cancelTransientSprings();
        settleTransientAnimationValues();
        if (!this.expanded || this.dragging) {
            return;
        }
        resetAutoCollapseTimeout();
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        cancelZoomConfigurationTransition();
        removeCallbacks(this.longPressRunnable);
        removeCallbacks(this.autoCollapseRunnable);
        animate().cancel();
        cancelZoomAnimator();
        cancelTransientSprings();
        settleTransientAnimationValues();
        clearTouchState();
        recycleVelocityTracker();
    }

    private void animateExpandedProgress(float f, boolean z) {
        ValueAnimator valueAnimator = this.expandedAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.expandedAnimator = null;
        }
        if (!z || !isLaidOut()) {
            this.expandedProgress = f;
            return;
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.expandedProgress, f);
        this.expandedAnimator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(217L);
        valueAnimatorOfFloat.setInterpolator(MORPH_INTERPOLATOR);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.exteragram.messenger.camera.CameraZoomSliderView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                CameraZoomSliderView.this.lambda$animateExpandedProgress$0(valueAnimator2);
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.exteragram.messenger.camera.CameraZoomSliderView.5
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (CameraZoomSliderView.this.expandedAnimator == animator) {
                    CameraZoomSliderView.this.expandedAnimator = null;
                }
            }
        });
        valueAnimatorOfFloat.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateExpandedProgress$0(ValueAnimator valueAnimator) {
        this.expandedProgress = clamp(((Float) valueAnimator.getAnimatedValue()).floatValue(), 0.0f, 1.0f);
        invalidate();
    }

    private void cancelTransientSprings() {
        ValueAnimator valueAnimator = this.expandedAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.expandedAnimator = null;
        }
        this.widthSpring.cancel();
        this.selectorSpring.cancel();
    }

    private void settleTransientAnimationValues() {
        boolean z = this.expanded;
        this.expandedProgress = z ? 1.0f : 0.0f;
        this.animatedControlWidth = z ? getExpandedBackgroundWidth() : getCompactWidth();
        this.animatedSelectorOffset = getSelectorOffset(this.selectedToggleIndex);
    }

    private void rebuildScale() {
        double dLog = Math.log(this.maxZoom / this.minZoom);
        double d = LOG_2;
        float f = (float) (dLog / d);
        float f2 = this.displayNormalizationFactor;
        float f3 = this.minZoom;
        if (f3 < f2 && this.maxZoom >= f2) {
            this.oneXTick = Math.max(3, Math.round(((float) (Math.log(f2 / f3) / d)) * 5.0f));
            float f4 = this.maxZoom;
            this.intervalCount = this.oneXTick + (f4 > f2 ? Math.max(1, Math.round(((float) (Math.log(f4 / f2) / d)) * 5.0f)) : 0);
        } else {
            this.oneXTick = -1;
            this.intervalCount = Math.max(1, Math.round(f * 5.0f));
        }
        this.primaryLabels.clear();
        float[] fArr = this.rulerStops;
        int[] iArr = new int[fArr.length];
        int i = 0;
        for (float f5 : fArr) {
            if (f5 >= this.minZoom && f5 <= this.maxZoom) {
                int iRound = Math.round(zoomToTick(f5));
                this.primaryLabels.put(iRound, formatRuler(f5));
                int i2 = 0;
                while (true) {
                    if (i2 < i) {
                        if (iArr[i2] == iRound) {
                            break;
                        } else {
                            i2++;
                        }
                    } else {
                        iArr[i] = iRound;
                        i++;
                        break;
                    }
                }
            }
        }
        int[] iArrCopyOf = Arrays.copyOf(iArr, i);
        this.rebuiltPrimaryTickIndices = iArrCopyOf;
        Arrays.sort(iArrCopyOf);
    }

    private void setTickInternal(float f, boolean z) {
        setZoomInternal(tickToZoom(f), z, true);
    }

    private float tickToZoom(float f) {
        double d;
        double dExp;
        float fClamp = clamp(f, 0.0f, this.intervalCount);
        if (fClamp <= 0.0f) {
            return this.minZoom;
        }
        int i = this.intervalCount;
        if (fClamp >= i) {
            return this.maxZoom;
        }
        int i2 = this.oneXTick;
        if (i2 < 0) {
            float f2 = fClamp / i;
            float f3 = this.minZoom;
            d = f3;
            dExp = Math.exp(Math.log(this.maxZoom / f3) * ((double) f2));
        } else {
            if (fClamp == i2) {
                return this.displayNormalizationFactor;
            }
            if (fClamp <= i2) {
                float f4 = fClamp / i2;
                float f5 = this.minZoom;
                d = f5;
                dExp = Math.exp(Math.log(this.displayNormalizationFactor / f5) * ((double) f4));
            } else {
                float f6 = (fClamp - i2) / (i - i2);
                float f7 = this.displayNormalizationFactor;
                d = f7;
                dExp = Math.exp(Math.log(this.maxZoom / f7) * ((double) f6));
            }
        }
        return (float) (d * dExp);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setZoomInternal(float f, boolean z, boolean z2) {
        OnZoomChangeListener onZoomChangeListener;
        this.zoom = clamp(f, this.minZoom, this.maxZoom);
        if (z2) {
            syncSelectedToggle(!this.expanded);
        }
        updateAccessibilityDescription();
        invalidate();
        if (!z || (onZoomChangeListener = this.onZoomChangeListener) == null) {
            return;
        }
        onZoomChangeListener.onZoomChanged(this.zoom);
    }

    private float zoomToTick(float f) {
        float fLog;
        int i;
        float fClamp = clamp(f, this.minZoom, this.maxZoom);
        float f2 = this.minZoom;
        if (fClamp <= f2) {
            return 0.0f;
        }
        if (fClamp >= this.maxZoom) {
            return this.intervalCount;
        }
        int i2 = this.oneXTick;
        if (i2 >= 0) {
            float f3 = this.displayNormalizationFactor;
            if (fClamp == f3) {
                return i2;
            }
            if (fClamp <= f3) {
                fLog = (float) (Math.log(fClamp / f2) / Math.log(this.displayNormalizationFactor / this.minZoom));
                i = this.oneXTick;
            } else {
                float fLog2 = (float) (Math.log(fClamp / f3) / Math.log(this.maxZoom / this.displayNormalizationFactor));
                int i3 = this.oneXTick;
                return i3 + (fLog2 * (this.intervalCount - i3));
            }
        } else {
            fLog = (float) (Math.log(fClamp / f2) / Math.log(this.maxZoom / this.minZoom));
            i = this.intervalCount;
        }
        return fLog * i;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void syncSelectedToggle(boolean z) {
        this.selectedShowsStopValue = false;
        int iFindToggleSegment = findToggleSegment(this.zoom);
        if (iFindToggleSegment < 0) {
            this.selectedToggleIndex = -1;
            this.animatedSelectorOffset = 0.0f;
        } else if (this.selectedToggleIndex != iFindToggleSegment) {
            this.selectedToggleIndex = iFindToggleSegment;
            animateSelectorTo(iFindToggleSegment, z);
        } else {
            if (z) {
                return;
            }
            this.selectorSpring.cancel();
            this.animatedSelectorOffset = getSelectorOffset(iFindToggleSegment);
        }
    }

    private int findToggleSegment(float f) {
        if (this.toggleStops.length == 0) {
            return -1;
        }
        int i = 1;
        int i2 = 0;
        while (true) {
            float[] fArr = this.toggleStops;
            if (i >= fArr.length || f < fArr[i]) {
                break;
            }
            i2 = i;
            i++;
        }
        return i2;
    }

    private void animateSelectorTo(int i, boolean z) {
        float selectorOffset = getSelectorOffset(i);
        if (!z || !isLaidOut()) {
            this.selectorSpring.cancel();
            this.animatedSelectorOffset = selectorOffset;
            invalidate();
            return;
        }
        this.selectorSpring.animateToFinalPosition(selectorOffset);
    }

    private void animateZoomTo(float f, boolean z) {
        animateZoomTo(f, z, -1);
    }

    private void animateZoomTo(float f, final boolean z, int i) {
        final boolean z2 = i >= 0 && i < this.toggleStops.length;
        if (z2) {
            stopZoomAnimator();
        } else {
            cancelZoomAnimator(true);
        }
        final float fClamp = clamp(f, this.minZoom, this.maxZoom);
        if (z2) {
            this.selectedToggleIndex = i;
            this.selectedShowsStopValue = true;
            animateSelectorTo(i, true);
        }
        if (Math.abs(fClamp - this.zoom) < 1.0E-4f) {
            setZoomInternal(fClamp, z, !z2);
            return;
        }
        float f2 = this.zoom;
        long jMin = Math.min(500L, (long) Math.rint(((Math.max(f2, fClamp) / Math.min(f2, fClamp)) * 500.0f) / 3.0f));
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(f2, fClamp);
        this.zoomAnimator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(jMin);
        valueAnimatorOfFloat.setInterpolator(ZOOM_INTERPOLATOR);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.exteragram.messenger.camera.CameraZoomSliderView$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                CameraZoomSliderView.this.lambda$animateZoomTo$1(z, z2, valueAnimator);
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.exteragram.messenger.camera.CameraZoomSliderView.6
            private boolean cancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                this.cancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (!this.cancelled && Math.abs(CameraZoomSliderView.this.zoom - fClamp) > 1.0E-4f) {
                    CameraZoomSliderView.this.setZoomInternal(fClamp, z, !z2);
                }
                if (CameraZoomSliderView.this.zoomAnimator == animator) {
                    CameraZoomSliderView.this.zoomAnimator = null;
                    CameraZoomSliderView.this.syncSelectedToggle(true);
                }
            }
        });
        valueAnimatorOfFloat.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateZoomTo$1(boolean z, boolean z2, ValueAnimator valueAnimator) {
        setZoomInternal(((Float) valueAnimator.getAnimatedValue()).floatValue(), z, !z2);
    }

    private boolean stopZoomAnimator() {
        ValueAnimator valueAnimator = this.zoomAnimator;
        if (valueAnimator == null) {
            return false;
        }
        this.zoomAnimator = null;
        valueAnimator.cancel();
        return true;
    }

    private void cancelZoomAnimator() {
        cancelZoomAnimator(false);
    }

    private void cancelZoomAnimator(boolean z) {
        if (stopZoomAnimator()) {
            syncSelectedToggle(z);
        }
    }

    private int findToggleIndexAt(float f) {
        RectF rectF = this.compactBounds;
        float f2 = rectF.left;
        if (f < f2 || f > rectF.right || this.toggleStops.length == 0) {
            return -1;
        }
        return Math.max(0, Math.min(this.toggleStops.length - 1, (int) ((f - f2) / AndroidUtilities.dp(48.0f))));
    }

    private int findPrimarySegment(float f, float f2) {
        int[] iArr;
        int[] iArr2 = this.rebuiltPrimaryTickIndices;
        if (iArr2.length == 0) {
            return Integer.MIN_VALUE;
        }
        int i = 0;
        float f3 = iArr2[0];
        if (f < f3) {
            return -1;
        }
        if (f == f3 && f2 >= 0.0f) {
            return -1;
        }
        while (true) {
            iArr = this.rebuiltPrimaryTickIndices;
            if (i < iArr.length - 1) {
                int i2 = i + 1;
                float f4 = iArr[i2];
                if (f2 < 0.0f) {
                    if (f < f4) {
                        break;
                    }
                    i = i2;
                } else {
                    if (f <= f4) {
                        break;
                    }
                    i = i2;
                }
            } else {
                return iArr[iArr.length - 1];
            }
        }
        return iArr[i];
    }

    private float calculateStickiness(float f) {
        float fAbs = Math.abs(f);
        if (fAbs <= 100.0f) {
            return 0.7f;
        }
        if (fAbs >= 1000.0f) {
            return 0.0f;
        }
        return ((1000.0f - fAbs) / 900.0f) * 0.7f;
    }

    private void obtainVelocityTracker(MotionEvent motionEvent) {
        recycleVelocityTracker();
        VelocityTracker velocityTrackerObtain = VelocityTracker.obtain();
        this.velocityTracker = velocityTrackerObtain;
        velocityTrackerObtain.addMovement(motionEvent);
    }

    private float getCurrentXVelocity() {
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker == null) {
            return 0.0f;
        }
        velocityTracker.computeCurrentVelocity(1000);
        return this.velocityTracker.getXVelocity();
    }

    private void recycleVelocityTracker() {
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.velocityTracker = null;
        }
    }

    private void updateTargetControlWidth(boolean z) {
        float expandedBackgroundWidth = this.expanded ? getExpandedBackgroundWidth() : getCompactWidth();
        if (!z || !isLaidOut()) {
            this.widthSpring.cancel();
            this.animatedControlWidth = expandedBackgroundWidth;
            invalidate();
            return;
        }
        this.widthSpring.animateToFinalPosition(expandedBackgroundWidth);
    }

    private void resetAutoCollapseTimeout() {
        removeCallbacks(this.autoCollapseRunnable);
        AccessibilityManager accessibilityManager = (AccessibilityManager) getContext().getSystemService("accessibility");
        boolean z = accessibilityManager != null && accessibilityManager.isTouchExplorationEnabled();
        if (!this.expanded || this.dragging || this.externalZoomGesture || z) {
            return;
        }
        postDelayed(this.autoCollapseRunnable, 1500L);
    }

    private void requestParentIntercept(boolean z) {
        ViewParent parent = getParent();
        if (parent != null) {
            parent.requestDisallowInterceptTouchEvent(!z);
        }
    }

    private void configurePaints() {
        Paint paint = this.backgroundPaint;
        Paint.Style style = Paint.Style.FILL;
        paint.setStyle(style);
        this.selectorPaint.setStyle(style);
        this.bubblePaint.setStyle(style);
        Paint paint2 = this.tickPaint;
        Paint.Cap cap = Paint.Cap.ROUND;
        paint2.setStrokeCap(cap);
        this.markerPaint.setStrokeCap(cap);
        Paint paint3 = this.toggleTextPaint;
        Paint.Align align = Paint.Align.CENTER;
        paint3.setTextAlign(align);
        this.toggleTextPaint.setTextSize(AndroidUtilities.dp(14.0f));
        this.toggleTextPaint.setTypeface(AndroidUtilities.bold());
        this.selectedToggleTextPaint.setTextAlign(align);
        this.selectedToggleTextPaint.setTextSize(AndroidUtilities.dp(16.0f));
        this.selectedToggleTextPaint.setTypeface(AndroidUtilities.bold());
        this.rulerLabelPaint.setTextAlign(align);
        this.rulerLabelPaint.setTextSize(AndroidUtilities.dp(11.0f));
        this.rulerLabelPaint.setTypeface(AndroidUtilities.bold());
        this.bubbleTextPaint.setTextAlign(align);
        this.bubbleTextPaint.setTextSize(AndroidUtilities.dp(16.0f));
        this.bubbleTextPaint.setTypeface(AndroidUtilities.bold());
        configurePaintColors();
    }

    private void configurePaintColors() {
        this.tickPaint.setColor(this.minorTickColor);
        this.markerPaint.setColor(this.primaryColor);
        this.rulerLabelPaint.setColor(this.primaryColor);
        this.selectorPaint.setColor(this.secondaryFixedColor);
        this.bubblePaint.setColor(this.secondaryFixedColor);
        this.bubbleTextPaint.setColor(this.onSecondaryFixedColor);
    }

    private String getToggleLabel(int i) {
        if (i == this.selectedToggleIndex) {
            return formatBubble(this.selectedShowsStopValue ? this.toggleStops[i] : this.zoom);
        }
        return formatToggle(this.toggleStops[i]);
    }

    private String formatBubble(float f) {
        return formatZoomNumber(f) + "×";
    }

    private String formatToggle(float f) {
        return formatZoomNumber(f);
    }

    private String formatRuler(float f) {
        return formatZoomNumber(f);
    }

    private String formatZoomNumber(float f) {
        float fNormalizeDisplayZoom = normalizeDisplayZoom(f);
        Locale locale = Locale.getDefault();
        if (fNormalizeDisplayZoom % 1.0f == 0.0f) {
            return String.format(locale, "%.0f", Float.valueOf(fNormalizeDisplayZoom));
        }
        String str = String.format(locale, "%.1f", Float.valueOf(fNormalizeDisplayZoom));
        return str.startsWith("0") ? str.substring(1) : str;
    }

    private float normalizeDisplayZoom(float f) {
        float f2 = f / this.displayNormalizationFactor;
        float f3 = f2 * 10.0f;
        if (f2 < 1.0f) {
            return ((float) Math.floor(f3)) / 10.0f;
        }
        double d = f3;
        float fFloor = (float) Math.floor(d);
        if (fFloor % 5.0f == 0.0f) {
            f3 = fFloor;
        } else {
            float fCeil = (float) Math.ceil(d);
            if (fCeil % 5.0f == 0.0f) {
                f3 = fCeil;
            }
        }
        float fRint = ((float) Math.rint(f3)) / 10.0f;
        return (fRint % 1.0f == 0.0f || fRint >= 8.0f) ? (float) Math.rint(f2) : fRint;
    }

    private void updateAccessibilityDescription() {
        int iRound = Math.round(normalizeDisplayZoom(this.zoom) * 10.0f);
        if (iRound == this.lastDescribedZoom) {
            return;
        }
        this.lastDescribedZoom = iRound;
        setContentDescription(formatBubble(this.zoom));
    }

    private float getExpandedBackgroundWidth() {
        return AndroidUtilities.dp(288.0f);
    }

    private float getCompactWidth() {
        return AndroidUtilities.dp(48.0f) * this.toggleStops.length;
    }

    private float getExpandedRulerWidth() {
        return AndroidUtilities.dp(258.0f);
    }

    private float getSelectorOffset(int i) {
        return AndroidUtilities.dp(48.0f) * Math.max(0, i);
    }

    private float getBubbleHeight() {
        Paint.FontMetricsInt fontMetricsInt = this.bubbleTextPaint.getFontMetricsInt();
        return (fontMetricsInt.descent - fontMetricsInt.ascent) + (AndroidUtilities.dp(6.0f) * 2.0f);
    }

    private static float[] sanitizeStops(float[] fArr, float f, float f2) {
        if (fArr == null || fArr.length == 0) {
            return new float[0];
        }
        float[] fArr2 = new float[fArr.length];
        int i = 0;
        for (float f3 : fArr) {
            if (Float.isFinite(f3) && f3 >= f && f3 <= f2) {
                fArr2[i] = f3;
                i++;
            }
        }
        float[] fArrCopyOf = Arrays.copyOf(fArr2, i);
        Arrays.sort(fArrCopyOf);
        if (fArrCopyOf.length < 2) {
            return fArrCopyOf;
        }
        int i2 = 1;
        for (int i3 = 1; i3 < fArrCopyOf.length; i3++) {
            if (Float.compare(fArrCopyOf[i3], fArrCopyOf[i2 - 1]) != 0) {
                fArrCopyOf[i2] = fArrCopyOf[i3];
                i2++;
            }
        }
        return Arrays.copyOf(fArrCopyOf, i2);
    }

    private static float clamp(float f, float f2, float f3) {
        return Math.max(f2, Math.min(f3, f));
    }

    public static final class ZoomLookupInterpolator implements TimeInterpolator {
        private static final float[] VALUES = {0.0f, 8.0E-4f, 0.0016f, 0.0024f, 0.0032f, 0.0057f, 0.0083f, 0.0109f, 0.0134f, 0.0171f, 0.0218f, 0.0266f, 0.0313f, 0.036f, 0.0431f, 0.0506f, 0.0581f, 0.0656f, 0.0733f, 0.0835f, 0.0937f, 0.1055f, 0.1179f, 0.1316f, 0.1466f, 0.1627f, 0.181f, 0.2003f, 0.2226f, 0.2468f, 0.2743f, 0.306f, 0.3408f, 0.3852f, 0.4317f, 0.4787f, 0.5177f, 0.5541f, 0.5834f, 0.6123f, 0.6333f, 0.6542f, 0.6739f, 0.6887f, 0.7035f, 0.7183f, 0.7308f, 0.7412f, 0.7517f, 0.7621f, 0.7725f, 0.7805f, 0.7879f, 0.7953f, 0.8027f, 0.8101f, 0.8175f, 0.823f, 0.8283f, 0.8336f, 0.8388f, 0.8441f, 0.8494f, 0.8546f, 0.8592f, 0.863f, 0.8667f, 0.8705f, 0.8743f, 0.878f, 0.8818f, 0.8856f, 0.8893f, 0.8927f, 0.8953f, 0.898f, 0.9007f, 0.9034f, 0.9061f, 0.9087f, 0.9114f, 0.9141f, 0.9168f, 0.9194f, 0.9218f, 0.9236f, 0.9255f, 0.9274f, 0.9293f, 0.9312f, 0.9331f, 0.935f, 0.9368f, 0.9387f, 0.9406f, 0.9425f, 0.9444f, 0.946f, 0.9473f, 0.9486f, 0.9499f, 0.9512f, 0.9525f, 0.9538f, 0.9551f, 0.9564f, 0.9577f, 0.959f, 0.9603f, 0.9616f, 0.9629f, 0.9642f, 0.9654f, 0.9663f, 0.9672f, 0.968f, 0.9689f, 0.9697f, 0.9706f, 0.9715f, 0.9723f, 0.9732f, 0.9741f, 0.9749f, 0.9758f, 0.9766f, 0.9775f, 0.9784f, 0.9792f, 0.9801f, 0.9808f, 0.9813f, 0.9819f, 0.9824f, 0.9829f, 0.9835f, 0.984f, 0.9845f, 0.985f, 0.9856f, 0.9861f, 0.9866f, 0.9872f, 0.9877f, 0.9882f, 0.9887f, 0.9893f, 0.9898f, 0.9903f, 0.9909f, 0.9914f, 0.9917f, 0.992f, 0.9922f, 0.9925f, 0.9928f, 0.9931f, 0.9933f, 0.9936f, 0.9939f, 0.9942f, 0.9944f, 0.9947f, 0.995f, 0.9953f, 0.9955f, 0.9958f, 0.9961f, 0.9964f, 0.9966f, 0.9969f, 0.9972f, 0.9975f, 0.9977f, 0.9979f, 0.9981f, 0.9982f, 0.9983f, 0.9984f, 0.9986f, 0.9987f, 0.9988f, 0.9989f, 0.9991f, 0.9992f, 0.9993f, 0.9994f, 0.9995f, 0.9995f, 0.9996f, 0.9996f, 0.9997f, 0.9997f, 0.9997f, 0.9998f, 0.9998f, 0.9998f, 0.9999f, 0.9999f, 1.0f, 1.0f};

        private ZoomLookupInterpolator() {
        }

        @Override // android.animation.TimeInterpolator
        public float getInterpolation(float f) {
            if (f <= 0.0f) {
                return 0.0f;
            }
            if (f >= 1.0f) {
                return 1.0f;
            }
            int iMin = Math.min((int) (200.0f * f), 199);
            float f2 = (f - (iMin * 0.005f)) / 0.005f;
            float[] fArr = VALUES;
            float f3 = fArr[iMin];
            return f3 + (f2 * (fArr[iMin + 1] - f3));
        }
    }
}
