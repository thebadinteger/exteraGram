package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.material.loadingindicator.LoadingIndicator;
import com.google.android.material.loadingindicator.LoadingIndicatorDrawable;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.IndeterminateDrawable;
import org.telegram.messenger.AndroidUtilities;

public class CircularProgressDrawable extends Drawable implements Drawable.Callback {
    public static final FastOutSlowInInterpolator interpolator = new FastOutSlowInInterpolator();
    private float angleOffset;
    private final RectF bounds;
    private IndeterminateDrawable<CircularProgressIndicatorSpec> circularIndicatorDrawable;
    private CircularProgressIndicatorSpec circularIndicatorSpec;
    private int currentColor;
    private int currentStyle;
    private long lastDrawTime;
    private Drawable m3Drawable;
    private LoadingIndicator m3IndicatorView;
    private final Paint paint;
    private float[] segment;
    public float size;
    private long start;
    public float thickness;
    private int trackColor;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    public CircularProgressDrawable() {
        this(-1);
    }

    public CircularProgressDrawable(int i) {
        this.currentStyle = 0;
        this.size = AndroidUtilities.dp(18.0f);
        this.thickness = AndroidUtilities.dp(2.25f);
        this.start = -1L;
        this.segment = new float[2];
        Paint paint = new Paint();
        this.paint = paint;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        this.bounds = new RectF();
        this.currentColor = i;
        setStyle(1, AndroidUtilities.getActivity());
        setColor(i);
    }

    public CircularProgressDrawable(float f, float f2, int i) {
        this.currentStyle = 0;
        this.size = AndroidUtilities.dp(18.0f);
        this.thickness = AndroidUtilities.dp(2.25f);
        this.start = -1L;
        this.segment = new float[2];
        Paint paint = new Paint();
        this.paint = paint;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        this.bounds = new RectF();
        this.size = f;
        this.thickness = f2;
        this.currentColor = i;
        setStyle(1, AndroidUtilities.getActivity());
        setColor(i);
    }

    public CircularProgressDrawable(float f, float f2, int i, int i2) {
        this.currentStyle = 0;
        this.size = AndroidUtilities.dp(18.0f);
        this.thickness = AndroidUtilities.dp(2.25f);
        this.start = -1L;
        this.segment = new float[2];
        Paint paint = new Paint();
        this.paint = paint;
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeJoin(Paint.Join.ROUND);
        this.bounds = new RectF();
        this.size = f;
        this.thickness = f2;
        this.trackColor = i;
        this.currentColor = i2;
        setStyle(2, AndroidUtilities.getActivity());
        setColor(i2);
        setTrackColor(i);
    }

    public void setStyle(int i, Context context) {
        if (!ExteraConfig.getNewLoadingStyle() && i != 0 && i != 1) {
            i = 0;
        }
        if (this.currentStyle != i || ((i == 1 && this.m3Drawable == null) || ((i == 2 || i == 3) && this.circularIndicatorDrawable == null))) {
            Drawable drawable = this.m3Drawable;
            IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawable = this.circularIndicatorDrawable;
            this.currentStyle = i;
            if (i == 1 && context != null) {
                this.circularIndicatorDrawable = null;
                this.circularIndicatorSpec = null;
                if (this.m3IndicatorView == null) {
                    this.m3IndicatorView = new LoadingIndicator(context);
                }
                this.m3IndicatorView.setIndicatorSize((int) this.size);
                this.m3IndicatorView.setIndicatorColor(this.currentColor);
                LoadingIndicatorDrawable drawable2 = this.m3IndicatorView.getDrawable();
                this.m3Drawable = drawable2;
                drawable2.setCallback(this);
                forceUpdateM3Visibility();
                if (!getBounds().isEmpty()) {
                    updateM3Bounds(getBounds());
                }
            } else if ((i == 2 || i == 3) && context != null) {
                this.m3Drawable = null;
                this.m3IndicatorView = null;
                CircularProgressIndicatorSpec circularProgressIndicatorSpec = new CircularProgressIndicatorSpec(context, null);
                this.circularIndicatorSpec = circularProgressIndicatorSpec;
                circularProgressIndicatorSpec.indicatorSize = (int) this.size;
                circularProgressIndicatorSpec.trackThickness = (int) this.thickness;
                circularProgressIndicatorSpec.indicatorColors = new int[]{this.currentColor};
                circularProgressIndicatorSpec.trackColor = this.trackColor;
                circularProgressIndicatorSpec.indicatorTrackGapSize = AndroidUtilities.dp(2.0f);
                if (this.currentStyle == 3) {
                    this.circularIndicatorSpec.wavelengthIndeterminate = AndroidUtilities.dp(7.0f);
                    CircularProgressIndicatorSpec circularProgressIndicatorSpec2 = this.circularIndicatorSpec;
                    circularProgressIndicatorSpec2.indicatorInset = 0;
                    circularProgressIndicatorSpec2.waveAmplitude = AndroidUtilities.dp(0.75f);
                    this.circularIndicatorSpec.waveSpeed = AndroidUtilities.dp(6.0f);
                }
                IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawableCreateCircularDrawable = IndeterminateDrawable.createCircularDrawable(context, this.circularIndicatorSpec);
                this.circularIndicatorDrawable = indeterminateDrawableCreateCircularDrawable;
                indeterminateDrawableCreateCircularDrawable.setCallback(this);
                forceUpdateM3Visibility();
                if (!getBounds().isEmpty()) {
                    updateCircularBounds(getBounds());
                }
            } else {
                this.m3Drawable = null;
                this.m3IndicatorView = null;
                this.circularIndicatorDrawable = null;
                this.circularIndicatorSpec = null;
            }
            if (drawable != null && drawable != this.m3Drawable) {
                drawable.setVisible(false, false);
                drawable.setCallback(null);
            }
            if (indeterminateDrawable != null && indeterminateDrawable != this.circularIndicatorDrawable) {
                indeterminateDrawable.setVisible(false, false);
                indeterminateDrawable.setCallback(null);
            }
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void invalidateSelf() {
        super.invalidateSelf();
        if (getCallback() != null || SystemClock.elapsedRealtime() - this.lastDrawTime <= 1000) {
            return;
        }
        stopM3Drawables();
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        scheduleSelf(runnable, j);
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        unscheduleSelf(runnable);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean z, boolean z2) {
        boolean visible = super.setVisible(z, z2);
        updateM3Visibility(z2);
        return visible;
    }

    private void updateSegment() {
        getSegments((SystemClock.elapsedRealtime() - this.start) % 5400, this.segment);
    }

    public static void getSegments(float f, float[] fArr) {
        float f2 = (1520.0f * f) / 5400.0f;
        fArr[0] = Math.max(0.0f, f2 - 20.0f);
        fArr[1] = f2;
        for (int i = 0; i < 4; i++) {
            float f3 = fArr[1];
            FastOutSlowInInterpolator fastOutSlowInInterpolator = interpolator;
            int i2 = i * 1350;
            fArr[1] = f3 + (fastOutSlowInInterpolator.getInterpolation((f - i2) / 667.0f) * 250.0f);
            fArr[0] = fArr[0] + (fastOutSlowInInterpolator.getInterpolation((f - (i2 + 667)) / 667.0f) * 250.0f);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        int i = this.currentStyle;
        if (i == 1 && this.m3Drawable != null) {
            this.lastDrawTime = SystemClock.elapsedRealtime();
            ensureManualDrawVisible();
            updateM3Visibility(false);
            this.m3Drawable.draw(canvas);
            return;
        }
        if ((i == 2 || i == 3) && this.circularIndicatorDrawable != null) {
            this.lastDrawTime = SystemClock.elapsedRealtime();
            ensureManualDrawVisible();
            updateM3Visibility(false);
            this.circularIndicatorDrawable.draw(canvas);
            return;
        }
        if (this.start < 0) {
            this.start = SystemClock.elapsedRealtime();
        }
        updateSegment();
        RectF rectF = this.bounds;
        float f = this.angleOffset;
        float[] fArr = this.segment;
        float f2 = fArr[0];
        canvas.drawArc(rectF, f + f2, fArr[1] - f2, false, this.paint);
        invalidateSelf();
    }

    public void reset() {
        this.start = -1L;
    }

    public void setAngleOffset(float f) {
        this.angleOffset = f;
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        int i5 = i3 - i;
        int i6 = i4 - i2;
        RectF rectF = this.bounds;
        float f = i;
        float f2 = i5;
        float f3 = this.thickness;
        float f4 = this.size;
        float f5 = i2;
        float f6 = i6;
        rectF.set((((f2 - (f3 / 2.0f)) - f4) / 2.0f) + f, (((f6 - (f3 / 2.0f)) - f4) / 2.0f) + f5, f + (((f2 + (f3 / 2.0f)) + f4) / 2.0f), f5 + (((f6 + (f3 / 2.0f)) + f4) / 2.0f));
        this.paint.setStrokeWidth(this.thickness);
        updateM3Bounds(getBounds());
        updateCircularBounds(getBounds());
    }

    private void updateM3Bounds(Rect rect) {
        if (this.m3Drawable != null) {
            int iWidth = rect.width();
            int iHeight = rect.height();
            int i = (int) this.size;
            int i2 = rect.left + ((iWidth - i) / 2);
            int i3 = rect.top + ((iHeight - i) / 2);
            this.m3Drawable.setBounds(i2, i3, i2 + i, i + i3);
        }
    }

    private void updateCircularBounds(Rect rect) {
        if (this.circularIndicatorDrawable != null) {
            int iWidth = rect.width();
            int iHeight = rect.height();
            int i = (int) this.size;
            int i2 = rect.left + ((iWidth - i) / 2);
            int i3 = rect.top + ((iHeight - i) / 2);
            this.circularIndicatorDrawable.setBounds(i2, i3, i2 + i, i + i3);
        }
    }

    public void setColor(int i) {
        this.currentColor = i;
        this.paint.setColor(i);
        LoadingIndicator loadingIndicator = this.m3IndicatorView;
        if (loadingIndicator != null) {
            loadingIndicator.setIndicatorColor(i);
        }
        if (this.circularIndicatorDrawable != null) {
            this.circularIndicatorSpec.indicatorColors = new int[]{i};
        }
    }

    public void setTrackColor(int i) {
        this.trackColor = i;
        if (this.circularIndicatorDrawable != null) {
            this.circularIndicatorSpec.trackColor = i;
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.paint.setAlpha(i);
        Drawable drawable = this.m3Drawable;
        if (drawable != null) {
            drawable.setAlpha(i);
        }
        IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawable = this.circularIndicatorDrawable;
        if (indeterminateDrawable != null) {
            indeterminateDrawable.setAlpha(i);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        Drawable drawable = this.m3Drawable;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
        IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawable = this.circularIndicatorDrawable;
        if (indeterminateDrawable != null) {
            indeterminateDrawable.setColorFilter(colorFilter);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return (int) (this.size + this.thickness);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return (int) (this.size + this.thickness);
    }

    public void setWavyValues(float f, float f2, float f3) {
        CircularProgressIndicatorSpec circularProgressIndicatorSpec = this.circularIndicatorSpec;
        if (circularProgressIndicatorSpec == null) {
            return;
        }
        circularProgressIndicatorSpec.waveAmplitude = AndroidUtilities.dp(f);
        this.circularIndicatorSpec.wavelengthIndeterminate = AndroidUtilities.dp(f2);
        this.circularIndicatorSpec.waveSpeed = AndroidUtilities.dp(f3);
    }

    private void updateM3Visibility(boolean z) {
        boolean zIsVisible = isVisible();
        Drawable drawable = this.m3Drawable;
        boolean z2 = false;
        if (drawable != null && (drawable.isVisible() != zIsVisible || z)) {
            this.m3Drawable.setVisible(zIsVisible, z && zIsVisible);
        }
        IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawable = this.circularIndicatorDrawable;
        if (indeterminateDrawable != null) {
            if (indeterminateDrawable.isVisible() != zIsVisible || z) {
                IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawable2 = this.circularIndicatorDrawable;
                if (z && zIsVisible) {
                    z2 = true;
                }
                indeterminateDrawable2.setVisible(zIsVisible, z2);
            }
        }
    }

    private void forceUpdateM3Visibility() {
        boolean zIsVisible = isVisible();
        Drawable drawable = this.m3Drawable;
        if (drawable != null) {
            drawable.setVisible(zIsVisible, false);
        }
        IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawable = this.circularIndicatorDrawable;
        if (indeterminateDrawable != null) {
            indeterminateDrawable.setVisible(zIsVisible, false);
        }
    }

    private void stopM3Drawables() {
        Drawable drawable = this.m3Drawable;
        if (drawable != null && drawable.isVisible()) {
            this.m3Drawable.setVisible(false, false);
        }
        IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawable = this.circularIndicatorDrawable;
        if (indeterminateDrawable == null || !indeterminateDrawable.isVisible()) {
            return;
        }
        this.circularIndicatorDrawable.setVisible(false, false);
    }

    private void ensureManualDrawVisible() {
        if (getCallback() != null || isVisible()) {
            return;
        }
        setVisible(true, false);
    }
}
