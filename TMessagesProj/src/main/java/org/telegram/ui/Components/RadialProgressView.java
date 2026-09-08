package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import androidx.annotation.Keep;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.material.loadingindicator.LoadingIndicator;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class RadialProgressView extends View {
    private AccelerateInterpolator accelerateInterpolator;
    private float animatedProgress;
    private RectF cicleRect;
    private float currentCircleLength;
    private float currentProgress;
    private float currentProgressTime;
    private int currentStyle;
    private DecelerateInterpolator decelerateInterpolator;
    private float drawingCircleLenght;
    private long lastUpdateTime;
    private CircularProgressIndicator m3CircularProgressIndicator;
    private Drawable m3Drawable;
    private LoadingIndicator m3IndicatorView;
    private boolean noProgress;
    private float progressAnimationStart;
    private int progressColor;
    private Paint progressPaint;
    private int progressTime;
    private float radOffset;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean risingCircleLength;
    private int size;
    private boolean toCircle;
    private float toCircleProgress;
    private boolean useSelfAlpha;

    public RadialProgressView(Context context) {
        this(context, null);
    }

    public RadialProgressView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentStyle = 0;
        this.cicleRect = new RectF();
        this.noProgress = true;
        this.resourcesProvider = resourcesProvider;
        this.size = AndroidUtilities.dp(40.0f);
        this.progressColor = getThemedColor(Theme.key_progressCircle);
        this.decelerateInterpolator = new DecelerateInterpolator();
        this.accelerateInterpolator = new AccelerateInterpolator();
        Paint paint = new Paint(1);
        this.progressPaint = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.progressPaint.setStrokeCap(Paint.Cap.ROUND);
        this.progressPaint.setStrokeWidth(AndroidUtilities.dp(3.0f));
        this.progressPaint.setColor(this.progressColor);
    }

    public void setUseSelfAlpha(boolean z) {
        this.useSelfAlpha = z;
    }

    @Override // android.view.View
    @Keep
    public void setAlpha(float f) {
        super.setAlpha(f);
        if (this.useSelfAlpha) {
            Drawable background = getBackground();
            int i = (int) (f * 255.0f);
            if (background != null) {
                background.setAlpha(i);
            }
            this.progressPaint.setAlpha(i);
            Drawable drawable = this.m3Drawable;
            if (drawable != null) {
                drawable.setAlpha(i);
            }
        }
    }

    public void setNoProgress(boolean z) {
        this.noProgress = z;
    }

    public void setStyle(int i) {
        if (!ExteraConfig.getNewLoadingStyle() && i != 0 && i != 1) {
            i = 0;
        }
        if (this.currentStyle != i) {
            Drawable drawable = this.m3Drawable;
            this.currentStyle = i;
            if (i == 1) {
                if (this.m3IndicatorView == null) {
                    this.m3IndicatorView = new LoadingIndicator(getContext());
                }
                this.m3IndicatorView.setIndicatorColor(this.progressColor);
                this.m3Drawable = this.m3IndicatorView.getDrawable();
            } else if (i == 2 || i == 3) {
                if (this.m3CircularProgressIndicator == null) {
                    CircularProgressIndicator circularProgressIndicator = new CircularProgressIndicator(getContext());
                    this.m3CircularProgressIndicator = circularProgressIndicator;
                    circularProgressIndicator.setIndeterminate(true);
                }
                this.m3CircularProgressIndicator.setIndicatorColor(this.progressColor);
                this.m3CircularProgressIndicator.setIndicatorSize(this.size);
                this.m3CircularProgressIndicator.setTrackThickness((int) this.progressPaint.getStrokeWidth());
                this.m3CircularProgressIndicator.setTrackCornerRadius(AndroidUtilities.dp(2.0f));
                this.m3CircularProgressIndicator.setIndicatorTrackGapSize(AndroidUtilities.dp(2.0f));
                setWavy(this.currentStyle == 3);
                this.m3Drawable = this.m3CircularProgressIndicator.getIndeterminateDrawable();
            } else {
                this.m3Drawable = null;
            }
            if (drawable != null && drawable != this.m3Drawable) {
                drawable.setVisible(false, false);
                drawable.setCallback(null);
            }
            Drawable drawable2 = this.m3Drawable;
            if (drawable2 != null) {
                drawable2.setCallback(this);
                setM3Visible(isAttachedToWindow(), true);
            }
            invalidate();
        }
    }

    public boolean isMaterial3ProgressStyle() {
        int i = this.currentStyle;
        return i == 2 || i == 3;
    }

    @Keep
    public void setSpecValues(int i, int i2, int i3, int i4, int i5, int i6) {
        if (isMaterial3ProgressStyle()) {
            this.m3CircularProgressIndicator.setIndicatorSize(i);
            this.m3CircularProgressIndicator.setTrackThickness(i2);
            this.m3CircularProgressIndicator.setTrackCornerRadius(i3);
            this.m3CircularProgressIndicator.setIndicatorTrackGapSize(i4);
            this.m3CircularProgressIndicator.setIndicatorColor(i5);
            this.m3CircularProgressIndicator.setTrackColor(i6);
        }
    }

    @Keep
    public void setWavy(boolean z) {
        CircularProgressIndicator circularProgressIndicator = this.m3CircularProgressIndicator;
        if (z) {
            circularProgressIndicator.setIndicatorInset(0);
            setWavyValues(AndroidUtilities.dp(15.0f), AndroidUtilities.dp(1.6f), AndroidUtilities.dp(5.0f), 0.05f);
        } else {
            circularProgressIndicator.setIndicatorInset(0);
            setWavyValues(0, 0, 0, 1.0f);
        }
    }

    public void setWavyValues(int i, int i2, int i3, float f) {
        if (this.currentStyle != 3) {
            return;
        }
        this.m3CircularProgressIndicator.setWavelengthIndeterminate(i);
        this.m3CircularProgressIndicator.setWaveAmplitude(i2);
        this.m3CircularProgressIndicator.setWaveSpeed(i3);
        this.m3CircularProgressIndicator.setWaveAmplitudeRampProgressMin(f);
    }

    public void setProgress(float f) {
        this.currentProgress = f;
        if (this.animatedProgress > f) {
            this.animatedProgress = f;
        }
        this.progressAnimationStart = this.animatedProgress;
        this.progressTime = 0;
    }

    public void sync(RadialProgressView radialProgressView) {
        this.lastUpdateTime = radialProgressView.lastUpdateTime;
        this.radOffset = radialProgressView.radOffset;
        this.toCircle = radialProgressView.toCircle;
        this.toCircleProgress = radialProgressView.toCircleProgress;
        this.noProgress = radialProgressView.noProgress;
        this.currentCircleLength = radialProgressView.currentCircleLength;
        this.drawingCircleLenght = radialProgressView.drawingCircleLenght;
        this.currentProgressTime = radialProgressView.currentProgressTime;
        this.currentProgress = radialProgressView.currentProgress;
        this.progressTime = radialProgressView.progressTime;
        this.animatedProgress = radialProgressView.animatedProgress;
        this.risingCircleLength = radialProgressView.risingCircleLength;
        this.progressAnimationStart = radialProgressView.progressAnimationStart;
        updateAnimation(85L);
    }

    private void updateAnimation() {
        long jCurrentTimeMillis = System.currentTimeMillis();
        long j = jCurrentTimeMillis - this.lastUpdateTime;
        if (j > 17) {
            j = 17;
        }
        this.lastUpdateTime = jCurrentTimeMillis;
        updateAnimation(j);
    }

    /* JADX WARN: Code duplicated, block: B:10:0x0031  */
    /* JADX WARN: Code duplicated, block: B:12:0x0037  */
    /* JADX WARN: Code duplicated, block: B:14:0x0041  */
    /* JADX WARN: Code duplicated, block: B:9:0x002f A[DONT_INVERT] */
    private void updateAnimation(long j) {
        float f;
        float f2;
        float f3 = this.radOffset + ((360 * j) / 2000.0f);
        this.radOffset = f3 - (((int) (f3 / 360.0f)) * 360);
        boolean z = this.toCircle;
        if (z) {
            float f4 = this.toCircleProgress;
            if (f4 != 1.0f) {
                float f5 = f4 + 0.07272727f;
                this.toCircleProgress = f5;
                if (f5 > 1.0f) {
                    this.toCircleProgress = 1.0f;
                }
            } else if (!z) {
                f = this.toCircleProgress;
                if (f != 0.0f) {
                    f2 = f - 0.04f;
                    this.toCircleProgress = f2;
                    if (f2 < 0.0f) {
                        this.toCircleProgress = 0.0f;
                    }
                }
            }
        } else if (!z) {
            f = this.toCircleProgress;
            if (f != 0.0f) {
                f2 = f - 0.04f;
                this.toCircleProgress = f2;
                if (f2 < 0.0f) {
                    this.toCircleProgress = 0.0f;
                }
            }
        }
        if (!this.noProgress) {
            float f6 = this.currentProgress;
            float f7 = this.progressAnimationStart;
            float f8 = f6 - f7;
            if (f8 > 0.0f) {
                int i = (int) (((long) this.progressTime) + j);
                this.progressTime = i;
                if (i >= 200.0f) {
                    this.progressAnimationStart = f6;
                    this.animatedProgress = f6;
                    this.progressTime = 0;
                } else {
                    this.animatedProgress = f7 + (f8 * AndroidUtilities.decelerateInterpolator.getInterpolation(i / 200.0f));
                }
            }
            this.currentCircleLength = Math.max(4.0f, this.animatedProgress * 360.0f);
        } else if (this.toCircleProgress == 0.0f) {
            float f9 = this.currentProgressTime + j;
            this.currentProgressTime = f9;
            if (f9 >= 500.0f) {
                this.currentProgressTime = 500.0f;
            }
            if (this.risingCircleLength) {
                this.currentCircleLength = (this.accelerateInterpolator.getInterpolation(this.currentProgressTime / 500.0f) * 266.0f) + 4.0f;
            } else {
                this.currentCircleLength = 4.0f - ((1.0f - this.decelerateInterpolator.getInterpolation(this.currentProgressTime / 500.0f)) * 270.0f);
            }
            if (this.currentProgressTime == 500.0f) {
                boolean z2 = this.risingCircleLength;
                if (z2) {
                    this.radOffset += 270.0f;
                    this.currentCircleLength = -266.0f;
                }
                this.risingCircleLength = !z2;
                this.currentProgressTime = 0.0f;
            }
        } else {
            boolean z3 = this.risingCircleLength;
            float f10 = this.currentCircleLength;
            if (z3) {
                float interpolation = (this.accelerateInterpolator.getInterpolation(this.currentProgressTime / 500.0f) * 266.0f) + 4.0f + (this.toCircleProgress * 360.0f);
                this.currentCircleLength = interpolation;
                if (f10 - interpolation > 0.0f) {
                    this.radOffset += f10 - interpolation;
                }
            } else {
                float interpolation2 = (4.0f - ((1.0f - this.decelerateInterpolator.getInterpolation(this.currentProgressTime / 500.0f)) * 270.0f)) - (this.toCircleProgress * 364.0f);
                this.currentCircleLength = interpolation2;
                if (f10 - interpolation2 > 0.0f) {
                    this.radOffset += f10 - interpolation2;
                }
            }
        }
        invalidate();
    }

    public void setSize(int i) {
        this.size = i;
        CircularProgressIndicator circularProgressIndicator = this.m3CircularProgressIndicator;
        if (circularProgressIndicator != null) {
            circularProgressIndicator.setIndicatorSize(i);
        }
        invalidate();
    }

    public void setStrokeWidth(float f) {
        this.progressPaint.setStrokeWidth(AndroidUtilities.dp(f));
        CircularProgressIndicator circularProgressIndicator = this.m3CircularProgressIndicator;
        if (circularProgressIndicator != null) {
            circularProgressIndicator.setTrackThickness(AndroidUtilities.dp(f));
        }
    }

    public void setProgressColor(int i) {
        this.progressColor = i;
        this.progressPaint.setColor(i);
        LoadingIndicator loadingIndicator = this.m3IndicatorView;
        if (loadingIndicator != null) {
            loadingIndicator.setIndicatorColor(this.progressColor);
        }
        CircularProgressIndicator circularProgressIndicator = this.m3CircularProgressIndicator;
        if (circularProgressIndicator != null) {
            circularProgressIndicator.setIndicatorColor(this.progressColor);
        }
    }

    public void setTrackColor(int i) {
        CircularProgressIndicator circularProgressIndicator = this.m3CircularProgressIndicator;
        if (circularProgressIndicator != null) {
            circularProgressIndicator.setTrackColor(i);
        }
    }

    public void toCircle(boolean z, boolean z2) {
        this.toCircle = z;
        if (z2) {
            return;
        }
        this.toCircleProgress = z ? 1.0f : 0.0f;
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (this.currentStyle != 0 && this.m3Drawable != null) {
            int measuredWidth = (getMeasuredWidth() - this.size) / 2;
            int measuredHeight = getMeasuredHeight();
            int i = this.size;
            int i2 = (measuredHeight - i) / 2;
            this.m3Drawable.setBounds(measuredWidth, i2, measuredWidth + i, i + i2);
            this.m3Drawable.draw(canvas);
            return;
        }
        int measuredWidth2 = (getMeasuredWidth() - this.size) / 2;
        int measuredHeight2 = getMeasuredHeight();
        int i3 = this.size;
        int i4 = (measuredHeight2 - i3) / 2;
        this.cicleRect.set(measuredWidth2, i4, measuredWidth2 + i3, i4 + i3);
        RectF rectF = this.cicleRect;
        float f = this.radOffset;
        float f2 = this.currentCircleLength;
        this.drawingCircleLenght = f2;
        canvas.drawArc(rectF, f, f2, false, this.progressPaint);
        updateAnimation();
    }

    public void draw(Canvas canvas, float f, float f2) {
        Drawable drawable;
        if (this.currentStyle != 0 && (drawable = this.m3Drawable) != null) {
            int i = this.size;
            int i2 = (int) (f - (i / 2.0f));
            int i3 = (int) (f2 - (i / 2.0f));
            drawable.setBounds(i2, i3, i2 + i, i + i3);
            this.m3Drawable.draw(canvas);
            return;
        }
        RectF rectF = this.cicleRect;
        int i4 = this.size;
        rectF.set(f - (i4 / 2.0f), f2 - (i4 / 2.0f), f + (i4 / 2.0f), f2 + (i4 / 2.0f));
        RectF rectF2 = this.cicleRect;
        float f3 = this.radOffset;
        float f4 = this.currentCircleLength;
        this.drawingCircleLenght = f4;
        canvas.drawArc(rectF2, f3, f4, false, this.progressPaint);
        updateAnimation();
    }

    public boolean isCircle() {
        return Math.abs(this.drawingCircleLenght) >= 360.0f;
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    @Override // android.view.View
    public boolean verifyDrawable(Drawable drawable) {
        if (drawable == this.m3Drawable) {
            return true;
        }
        return super.verifyDrawable(drawable);
    }

    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        setM3Visible(true, false);
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        setM3Visible(false, false);
        super.onDetachedFromWindow();
    }

    private void setM3Visible(boolean z, boolean z2) {
        Drawable drawable = this.m3Drawable;
        if (drawable == null) {
            return;
        }
        drawable.setVisible(z, z2 && z);
    }
}
