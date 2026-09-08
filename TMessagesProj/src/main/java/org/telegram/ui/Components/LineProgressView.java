package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.material.R;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.voip.CellFlickerDrawable;

public class LineProgressView extends LinearProgressIndicator {
    private static DecelerateInterpolator decelerateInterpolator;
    private static Paint progressPaint;
    private float animatedAlphaValue;
    private float animatedProgressValue;
    private float animationProgressStart;
    private int backColor;
    private CellFlickerDrawable cellFlickerDrawable;
    private float currentProgress;
    private long currentProgressTime;
    private long lastUpdateTime;
    private int progressColor;
    private final RectF rect;
    public int type;

    public LineProgressView(Context context) {
        this(context, null);
    }

    public LineProgressView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.attr.linearProgressIndicatorStyle);
    }

    public LineProgressView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.animatedAlphaValue = 1.0f;
        this.rect = new RectF();
        if (decelerateInterpolator == null) {
            decelerateInterpolator = new DecelerateInterpolator();
            Paint paint = new Paint(1);
            progressPaint = paint;
            paint.setStrokeCap(Paint.Cap.ROUND);
            progressPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }
        init();
    }

    public void setProgressType(int i) {
        if (!ExteraConfig.getNewLoadingStyle()) {
            this.type = 0;
            return;
        }
        if (this.type == i) {
            return;
        }
        this.type = i;
        if (i == 0) {
            setWaveSpeed(0);
            setWavelength(0);
            setWaveAmplitude(0);
        } else if (i == 1) {
            setWavelengthDeterminate(AndroidUtilities.dp(40.0f));
            setWaveAmplitude(AndroidUtilities.dp(3.0f));
            setWaveSpeed(AndroidUtilities.dp(15.0f));
            setWaveAmplitudeRampProgressMin(0.05f);
        }
    }

    private void init() {
        setMax(1000);
        setTrackThickness(AndroidUtilities.dp(2.0f));
        setTrackCornerRadius(AndroidUtilities.dp(1.0f));
        setTrackStopIndicatorSize(AndroidUtilities.dp(2.0f));
        setIndicatorTrackGapSize(AndroidUtilities.dp(2.0f));
        setTrackCornerRadiusFraction(0.5f);
        setIndeterminate(false);
        setProgressType(0);
    }

    public void setProgressColor(int i) {
        this.progressColor = i;
        if (ExteraConfig.getNewLoadingStyle()) {
            setIndicatorColor(i);
        }
    }

    public void setBackColor(int i) {
        this.backColor = i;
        if (ExteraConfig.getNewLoadingStyle()) {
            setTrackColor(i);
        }
    }

    public void setProgress(float f, boolean z) {
        this.currentProgress = f;
        if (ExteraConfig.getNewLoadingStyle()) {
            setProgressCompat((int) (f * getMax()), z);
            return;
        }
        if (!z) {
            this.animatedProgressValue = f;
            this.animationProgressStart = f;
        } else {
            this.animationProgressStart = this.animatedProgressValue;
        }
        if (f != 1.0f) {
            this.animatedAlphaValue = 1.0f;
        }
        this.currentProgressTime = 0L;
        this.lastUpdateTime = System.currentTimeMillis();
        invalidate();
    }

    public float getCurrentProgress() {
        return this.currentProgress;
    }

    private void updateLegacyAnimation() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j = jCurrentTimeMillis - this.lastUpdateTime;
        this.lastUpdateTime = jCurrentTimeMillis;
        float f = this.animatedProgressValue;
        if (f != 1.0f) {
            float f2 = this.currentProgress;
            if (f != f2) {
                float f3 = this.animationProgressStart;
                float f4 = f2 - f3;
                if (f4 > 0.0f) {
                    long j2 = this.currentProgressTime + j;
                    this.currentProgressTime = j2;
                    if (j2 >= 300) {
                        this.animatedProgressValue = f2;
                        this.animationProgressStart = f2;
                        this.currentProgressTime = 0L;
                    } else {
                        this.animatedProgressValue = f3 + (f4 * decelerateInterpolator.getInterpolation(j2 / 300.0f));
                    }
                }
                invalidate();
            }
        }
        float f5 = this.animatedProgressValue;
        if (f5 < 1.0f || f5 != 1.0f) {
            return;
        }
        float f6 = this.animatedAlphaValue;
        if (f6 != 0.0f) {
            float f7 = f6 - (j / 200.0f);
            this.animatedAlphaValue = f7;
            if (f7 <= 0.0f) {
                this.animatedAlphaValue = 0.0f;
            }
            invalidate();
        }
    }

    @Override // com.google.android.material.progressindicator.BaseProgressIndicator, android.widget.ProgressBar, android.view.View
    public void onDraw(Canvas canvas) {
        if (ExteraConfig.getNewLoadingStyle()) {
            super.onDraw(canvas);
            return;
        }
        int i = this.backColor;
        if (i != 0 && this.animatedProgressValue != 1.0f) {
            progressPaint.setColor(i);
            progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
            this.rect.set(0.0f, 0.0f, getWidth(), getHeight());
            canvas.drawRoundRect(this.rect, getHeight() / 2.0f, getHeight() / 2.0f, progressPaint);
        }
        progressPaint.setColor(this.progressColor);
        progressPaint.setAlpha((int) (this.animatedAlphaValue * 255.0f));
        this.rect.set(0.0f, 0.0f, getWidth() * this.animatedProgressValue, getHeight());
        canvas.drawRoundRect(this.rect, getHeight() / 2.0f, getHeight() / 2.0f, progressPaint);
        if (this.animatedAlphaValue > 0.0f) {
            if (this.cellFlickerDrawable == null) {
                CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable(160, 0);
                this.cellFlickerDrawable = cellFlickerDrawable;
                cellFlickerDrawable.drawFrame = false;
                cellFlickerDrawable.animationSpeedScale = 0.8f;
                cellFlickerDrawable.repeatProgress = 1.2f;
            }
            this.cellFlickerDrawable.setParentWidth(getMeasuredWidth());
            this.cellFlickerDrawable.draw(canvas, this.rect, getHeight() / 2.0f, null);
            invalidate();
        }
        updateLegacyAnimation();
    }
}
