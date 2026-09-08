package org.telegram.ui.Components;

import android.R;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Build;
import android.util.StateSet;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import androidx.annotation.Keep;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.utils.ui.MonetUtils;
import me.vkryl.android.animator.BoolAnimator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.BaseCell;

public class Switch extends View {
    private static final FloatPropertyCompat<Switch> PROGRESS_PROPERTY = new FloatPropertyCompat<Switch>("progress") { // from class: org.telegram.ui.Components.Switch.1
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(Switch r1) {
            return r1.getProgress();
        }

        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(Switch r1, float f) {
            r1.setProgress(f);
        }
    };
    private final BoolAnimator animatorIconVisibility;
    private boolean attachedToWindow;
    private boolean bitmapsCreated;
    private ObjectAnimator checkAnimator;
    private SpringAnimation checkSpringAnimator;
    private int colorSet;
    private int drawIconType;
    private boolean drawRipple;
    private ObjectAnimator iconAnimator;
    private Drawable iconDrawable;
    private float iconProgress;
    private boolean isChecked;
    private int lastIconColor;
    private Bitmap[] overlayBitmap;
    private Canvas[] overlayCanvas;
    private float overlayCx;
    private float overlayCy;
    private Paint overlayEraserPaint;
    private Bitmap overlayMaskBitmap;
    private Canvas overlayMaskCanvas;
    private Paint overlayMaskPaint;
    private float overlayRad;
    private float overrideAlpha;
    private int overrideColorProgress;
    private Paint paint;
    private Paint paint2;
    private int[] pressedState;
    private float progress;
    private RectF rectF;
    private Theme.ResourcesProvider resourcesProvider;
    private RippleDrawable rippleDrawable;
    private Paint ripplePaint;
    private int thumbCheckedColorKey;
    private int thumbColorKey;
    private int trackCheckedColorKey;
    private int trackColorKey;

    public interface OnCheckedChangeListener {
    }

    public int processColor(int i) {
        return i;
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener onCheckedChangeListener) {
    }

    private int getOverlayPadding() {
        if (ExteraConfig.getNewSwitchStyle()) {
            return AndroidUtilities.dp(5.0f);
        }
        return 0;
    }

    public Switch(Context context) {
        this(context, null);
    }

    public Switch(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.animatorIconVisibility = new BoolAnimator((View) this, (Interpolator) CubicBezierInterpolator.EASE_OUT_QUINT, 380L, true);
        this.iconProgress = 1.0f;
        this.trackColorKey = Theme.key_fill_RedNormal;
        this.trackCheckedColorKey = Theme.key_switch2TrackChecked;
        int i = Theme.key_windowBackgroundWhite;
        this.thumbColorKey = i;
        this.thumbCheckedColorKey = i;
        this.pressedState = new int[]{R.attr.state_enabled, R.attr.state_pressed};
        this.overrideAlpha = 1.0f;
        this.resourcesProvider = resourcesProvider;
        this.rectF = new RectF();
        this.paint = new Paint(1);
        Paint paint = new Paint(1);
        this.paint2 = paint;
        paint.setStyle(Paint.Style.STROKE);
        this.paint2.setStrokeCap(Paint.Cap.ROUND);
        this.paint2.setStrokeWidth(AndroidUtilities.dp(2.0f));
        setHapticFeedbackEnabled(true);
    }

    @Keep
    public void setProgress(float f) {
        if (this.progress == f) {
            return;
        }
        this.progress = f;
        invalidate();
    }

    @Keep
    public float getProgress() {
        return this.progress;
    }

    @Keep
    public void setIconProgress(float f) {
        if (this.iconProgress == f) {
            return;
        }
        this.iconProgress = f;
        invalidate();
    }

    @Keep
    public float getIconProgress() {
        return this.iconProgress;
    }

    private void cancelCheckAnimator() {
        ObjectAnimator objectAnimator = this.checkAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.checkAnimator = null;
        }
        SpringAnimation springAnimation = this.checkSpringAnimator;
        if (springAnimation != null) {
            springAnimation.cancel();
            this.checkSpringAnimator = null;
        }
    }

    private void cancelIconAnimator() {
        ObjectAnimator objectAnimator = this.iconAnimator;
        if (objectAnimator != null) {
            objectAnimator.cancel();
            this.iconAnimator = null;
        }
    }

    public void setDrawIconType(int i) {
        this.drawIconType = i;
    }

    public void setDrawRipple(boolean z) {
        if (z == this.drawRipple) {
            return;
        }
        this.drawRipple = z;
        if (this.rippleDrawable == null) {
            Paint paint = new Paint(1);
            this.ripplePaint = paint;
            paint.setColor(-1);
            BaseCell.RippleDrawableSafe rippleDrawableSafe = new BaseCell.RippleDrawableSafe(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{0}), null, null);
            this.rippleDrawable = rippleDrawableSafe;
            rippleDrawableSafe.setRadius(AndroidUtilities.dp(18.0f));
            this.rippleDrawable.setCallback(this);
        }
        boolean z2 = this.isChecked;
        if ((z2 && this.colorSet != 2) || (!z2 && this.colorSet != 1)) {
            this.rippleDrawable.setColor(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{processColor(Theme.getColor(z2 ? Theme.key_switchTrackBlueSelectorChecked : Theme.key_switchTrackBlueSelector, this.resourcesProvider))}));
            this.colorSet = this.isChecked ? 2 : 1;
        }
        if (Build.VERSION.SDK_INT >= 28 && z) {
            this.rippleDrawable.setHotspot(this.isChecked ? 0.0f : AndroidUtilities.dp(100.0f), AndroidUtilities.dp(18.0f));
        }
        this.rippleDrawable.setState(z ? this.pressedState : StateSet.NOTHING);
        invalidate();
    }

    @Override // android.view.View
    public boolean verifyDrawable(Drawable drawable) {
        if (super.verifyDrawable(drawable)) {
            return true;
        }
        RippleDrawable rippleDrawable = this.rippleDrawable;
        return rippleDrawable != null && drawable == rippleDrawable;
    }

    public void setColors(int i, int i2, int i3, int i4) {
        this.trackColorKey = i;
        this.trackCheckedColorKey = i2;
        this.thumbColorKey = i3;
        this.thumbCheckedColorKey = i4;
    }

    private void animateToCheckedState(boolean z) {
        cancelCheckAnimator();
        if (ExteraConfig.getNewSwitchStyle()) {
            SpringAnimation startValue = new SpringAnimation(this, PROGRESS_PROPERTY).setSpring(new SpringForce(z ? 1.0f : 0.0f).setDampingRatio(0.9f).setStiffness(1400.0f)).setMinimumVisibleChange(0.001f).setStartValue(this.progress);
            this.checkSpringAnimator = startValue;
            startValue.start();
        } else {
            ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, "progress", z ? 1.0f : 0.0f);
            this.checkAnimator = objectAnimatorOfFloat;
            objectAnimatorOfFloat.setDuration(200L);
            this.checkAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.checkAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Switch.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    Switch.this.checkAnimator = null;
                }
            });
            this.checkAnimator.start();
        }
    }

    private void animateIcon(boolean z) {
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(this, "iconProgress", z ? 1.0f : 0.0f);
        this.iconAnimator = objectAnimatorOfFloat;
        objectAnimatorOfFloat.setDuration(200L);
        this.iconAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.Switch.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                Switch.this.iconAnimator = null;
            }
        });
        this.iconAnimator.start();
    }

    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attachedToWindow = true;
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attachedToWindow = false;
        destroyBitmaps();
    }

    public void setChecked(boolean z, boolean z2) {
        setChecked(z, this.drawIconType, z2);
    }

    public void setChecked(boolean z, int i, boolean z2) {
        if (z != this.isChecked) {
            this.isChecked = z;
            if (this.attachedToWindow && z2) {
                animateToCheckedState(z);
            } else {
                cancelCheckAnimator();
                setProgress(z ? 1.0f : 0.0f);
            }
        }
        setDrawIconType(i, z2);
    }

    public void setIcon(int i) {
        if (i != 0) {
            Drawable drawableMutate = getResources().getDrawable(i).mutate();
            this.iconDrawable = drawableMutate;
            if (drawableMutate != null) {
                int color = Theme.getColor(this.isChecked ? this.trackCheckedColorKey : this.trackColorKey, this.resourcesProvider);
                this.lastIconColor = color;
                drawableMutate.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY));
            }
        } else {
            this.iconDrawable = null;
        }
        invalidate();
    }

    public void setIconVisible(boolean z, boolean z2) {
        this.animatorIconVisibility.setValue(z, z2);
    }

    public void setDrawIconType(int i, boolean z) {
        if (this.drawIconType != i) {
            this.drawIconType = i;
            if (this.attachedToWindow && z) {
                animateIcon(i == 0);
            } else {
                cancelIconAnimator();
                setIconProgress(i == 0 ? 1.0f : 0.0f);
            }
        }
    }

    public boolean hasIcon() {
        return this.iconDrawable != null;
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    public void setOverrideColor(int i) {
        if (this.overrideColorProgress == i) {
            return;
        }
        this.overrideColorProgress = i;
        this.overlayCx = 0.0f;
        this.overlayCy = 0.0f;
        this.overlayRad = 0.0f;
        invalidate();
    }

    public void setOverrideColorProgress(float f, float f2, float f3) {
        this.overlayCx = f;
        this.overlayCy = f2;
        this.overlayRad = f3;
        invalidate();
    }

    private void checkBitmaps() {
        Bitmap[] bitmapArr;
        Bitmap bitmap;
        if (this.overrideColorProgress == 0) {
            return;
        }
        int measuredWidth = getMeasuredWidth() + (getOverlayPadding() * 4);
        int measuredHeight = getMeasuredHeight() + (getOverlayPadding() * 4);
        if (this.bitmapsCreated && (bitmapArr = this.overlayBitmap) != null && (bitmap = bitmapArr[0]) != null && (bitmap.getWidth() != measuredWidth || this.overlayBitmap[0].getHeight() != measuredHeight)) {
            destroyBitmaps();
        }
        if (this.bitmapsCreated || measuredWidth <= 0 || measuredHeight <= 0) {
            return;
        }
        try {
            this.overlayBitmap = new Bitmap[2];
            this.overlayCanvas = new Canvas[2];
            for (int i = 0; i < 2; i++) {
                this.overlayBitmap[i] = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
                this.overlayCanvas[i] = new Canvas(this.overlayBitmap[i]);
            }
            this.overlayMaskBitmap = Bitmap.createBitmap(measuredWidth, measuredHeight, Bitmap.Config.ARGB_8888);
            this.overlayMaskCanvas = new Canvas(this.overlayMaskBitmap);
            Paint paint = new Paint(1);
            this.overlayEraserPaint = paint;
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            Paint paint2 = new Paint(1);
            this.overlayMaskPaint = paint2;
            paint2.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
            this.bitmapsCreated = true;
        } catch (Throwable unused) {
        }
    }

    private void destroyBitmaps() {
        if (this.bitmapsCreated) {
            if (this.overlayBitmap != null) {
                int i = 0;
                while (true) {
                    Bitmap[] bitmapArr = this.overlayBitmap;
                    if (i >= bitmapArr.length) {
                        break;
                    }
                    Bitmap bitmap = bitmapArr[i];
                    if (bitmap != null) {
                        bitmap.recycle();
                        this.overlayBitmap[i] = null;
                    }
                    i++;
                }
                this.overlayBitmap = null;
            }
            Bitmap bitmap2 = this.overlayMaskBitmap;
            if (bitmap2 != null) {
                bitmap2.recycle();
                this.overlayMaskBitmap = null;
            }
        }
        this.overlayCanvas = null;
        this.overlayMaskCanvas = null;
        this.bitmapsCreated = false;
    }

    /* JADX WARN: Code duplicated, block: B:35:0x011d  */
    /* JADX WARN: Code duplicated, block: B:96:0x031b  */
    /* JADX WARN: Code duplicated, block: B:97:0x031e  */
    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        int measuredWidth;
        float measuredHeight;
        float fClamp01;
        int i;
        float f;
        RippleDrawable rippleDrawable;
        Drawable drawable;
        if (getVisibility() != 0) {
            return;
        }
        int i2 = 0;
        if (this.overrideColorProgress != 0) {
            checkBitmaps();
            if (!this.bitmapsCreated) {
                this.overrideColorProgress = 0;
            }
        }
        int iDp = AndroidUtilities.dp(31.0f);
        int iDp2 = AndroidUtilities.dp(26.0f);
        float f2 = 14.0f;
        float f3 = 2.0f;
        int i3 = 2;
        if (ExteraConfig.getNewSwitchStyle()) {
            measuredHeight = (getMeasuredHeight() / 2.0f) - (iDp2 / 2.0f);
            measuredWidth = 0;
        } else {
            measuredWidth = (getMeasuredWidth() - iDp) / 2;
            measuredHeight = (getMeasuredHeight() - AndroidUtilities.dpf2(14.0f)) / 2.0f;
        }
        boolean newSwitchStyle = ExteraConfig.getNewSwitchStyle();
        float fClamp02 = this.progress;
        if (newSwitchStyle) {
            fClamp02 = Utilities.clamp01(fClamp02);
        }
        float f4 = 8.0f;
        int measuredWidth2 = ((getMeasuredWidth() - iDp) / 2) + AndroidUtilities.dp(8.0f) + ((int) (AndroidUtilities.dp(16.0f) * fClamp02));
        int measuredHeight2 = getMeasuredHeight() / 2;
        int i4 = 0;
        while (true) {
            float fClamp03 = 0.0f;
            float f5 = f2;
            if (i4 >= i3) {
                break;
            }
            float f6 = f3;
            if (i4 == 1 && this.overrideColorProgress == 0) {
                iDp = iDp;
                i2 = i2;
                f4 = f4;
            } else {
                Canvas canvas2 = i4 == 0 ? canvas : this.overlayCanvas[i2];
                if (i4 == 1) {
                    this.overlayBitmap[i2].eraseColor(i2);
                    f = 3.0f;
                    this.paint.setColor(-16777216);
                    this.overlayMaskCanvas.drawRect(getOverlayPadding(), getOverlayPadding(), this.overlayMaskBitmap.getWidth() - getOverlayPadding(), this.overlayMaskBitmap.getHeight() - getOverlayPadding(), this.paint);
                    this.overlayMaskCanvas.drawCircle((this.overlayCx - getX()) + getOverlayPadding(), (this.overlayCy - getY()) + getOverlayPadding(), this.overlayRad, this.overlayEraserPaint);
                } else {
                    f = 3.0f;
                }
                int i5 = this.overrideColorProgress;
                if (i5 == 1) {
                    if (i4 != 0) {
                        fClamp03 = 1.0f;
                    }
                } else if (i5 != 2) {
                    fClamp03 = Utilities.clamp01(this.progress);
                } else if (i4 == 0) {
                    fClamp03 = 1.0f;
                }
                int iProcessColor = processColor(Theme.getColor(this.trackColorKey, this.resourcesProvider));
                int iProcessColor2 = processColor(Theme.getColor(this.trackCheckedColorKey, this.resourcesProvider));
                if (i4 == 0 && (drawable = this.iconDrawable) != null) {
                    if (this.lastIconColor != (this.isChecked ? iProcessColor2 : iProcessColor)) {
                        int i6 = this.isChecked ? iProcessColor2 : iProcessColor;
                        this.lastIconColor = i6;
                        drawable.setColorFilter(new PorterDuffColorFilter(i6, PorterDuff.Mode.MULTIPLY));
                    }
                }
                int iRed = Color.red(iProcessColor);
                int iRed2 = Color.red(iProcessColor2);
                int iGreen = Color.green(iProcessColor);
                int iGreen2 = Color.green(iProcessColor2);
                int iBlue = Color.blue(iProcessColor);
                int iBlue2 = Color.blue(iProcessColor2);
                int iAlpha = Color.alpha(iProcessColor);
                int i7 = (int) (iRed + ((iRed2 - iRed) * fClamp03));
                int i8 = (int) (iGreen + ((iGreen2 - iGreen) * fClamp03));
                int i9 = (int) (iBlue + ((iBlue2 - iBlue) * fClamp03));
                int iAlpha2 = (int) (iAlpha + ((Color.alpha(iProcessColor2) - iAlpha) * fClamp03));
                if (ExteraConfig.getNewSwitchStyle()) {
                    iAlpha2 = (int) (iAlpha2 * this.overrideAlpha);
                }
                int i10 = (i9 & 255) | ((iAlpha2 & 255) << 24) | ((i7 & 255) << 16) | ((i8 & 255) << 8);
                this.paint.setColor(i10);
                this.paint2.setColor(i10);
                if (ExteraConfig.getNewSwitchStyle()) {
                    RectF rectF = this.rectF;
                    if (i4 == 0) {
                        rectF.set(measuredWidth - AndroidUtilities.dp(f6), measuredHeight, getMeasuredWidth() + AndroidUtilities.dp(f), (getMeasuredHeight() / f6) + (iDp2 / f6));
                    } else {
                        rectF.set((measuredWidth - AndroidUtilities.dp(f6)) + getOverlayPadding(), getOverlayPadding() + measuredHeight, getMeasuredWidth() + AndroidUtilities.dp(f) + getOverlayPadding(), (getMeasuredHeight() / f6) + (iDp2 / f6) + getOverlayPadding());
                    }
                    canvas2.drawRoundRect(this.rectF, AndroidUtilities.dpf2(f5), AndroidUtilities.dpf2(f5), this.paint);
                } else {
                    this.rectF.set(measuredWidth, measuredHeight, measuredWidth + iDp, AndroidUtilities.dpf2(f5) + measuredHeight);
                    canvas2.drawRoundRect(this.rectF, AndroidUtilities.dpf2(7.0f), AndroidUtilities.dpf2(7.0f), this.paint);
                    if (i4 == 0) {
                        canvas2.drawCircle(measuredWidth2, measuredHeight2, AndroidUtilities.dpf2(10.0f), this.paint);
                    } else {
                        canvas2.drawCircle(getOverlayPadding() + measuredWidth2, getOverlayPadding() + measuredHeight2, AndroidUtilities.dpf2(10.0f), this.paint);
                    }
                }
                if (i4 == 0 && (rippleDrawable = this.rippleDrawable) != null) {
                    rippleDrawable.setBounds(measuredWidth2 - AndroidUtilities.dp(18.0f), measuredHeight2 - AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f) + measuredWidth2, AndroidUtilities.dp(18.0f) + measuredHeight2);
                    this.rippleDrawable.draw(canvas2);
                } else if (i4 == 1) {
                    canvas2.drawBitmap(this.overlayMaskBitmap, -getOverlayPadding(), -getOverlayPadding(), this.overlayMaskPaint);
                }
            }
            i4++;
            f2 = f5;
            f3 = f6;
            f4 = f4;
            i2 = i2;
            iDp = iDp;
            i3 = 2;
        }
        int i11 = i2;
        float f7 = f3;
        float f8 = f4;
        if (this.overrideColorProgress != 0) {
            canvas.drawBitmap(this.overlayBitmap[i11], -getOverlayPadding(), -getOverlayPadding(), (Paint) null);
        }
        int i12 = i11;
        int i13 = 2;
        while (i12 < i13) {
            if (i12 == 1 && this.overrideColorProgress == 0) {
                i = 2;
            } else {
                Canvas canvas3 = i12 == 0 ? canvas : this.overlayCanvas[1];
                if (i12 == 1) {
                    this.overlayBitmap[1].eraseColor(i11);
                }
                int i14 = this.overrideColorProgress;
                if (i14 == 1) {
                    if (i12 == 0) {
                        fClamp01 = 0.0f;
                    } else {
                        fClamp01 = 1.0f;
                    }
                } else if (i14 != 2) {
                    fClamp01 = Utilities.clamp01(this.progress);
                } else if (i12 == 0) {
                    fClamp01 = 1.0f;
                } else {
                    fClamp01 = 0.0f;
                }
                int iProcessColor3 = processColor(Theme.getColor(this.thumbColorKey, this.resourcesProvider));
                int iProcessColor4 = processColor(Theme.getColor(this.thumbCheckedColorKey, this.resourcesProvider));
                if (Build.VERSION.SDK_INT >= 31 && ExteraConfig.getNewSwitchStyle() && Theme.isCurrentThemeMonet()) {
                    if (Theme.isCurrentThemeDark()) {
                        int iProcessColor5 = processColor(MonetUtils.getColor("n1_800"));
                        int iProcessColor6 = processColor(MonetUtils.getColor("a1_800"));
                        if (iProcessColor5 == iProcessColor3) {
                            iProcessColor3 = iProcessColor5;
                        }
                        if (iProcessColor6 == iProcessColor4) {
                            iProcessColor4 = iProcessColor6;
                        }
                    } else {
                        iProcessColor3 = processColor(-1);
                        iProcessColor4 = iProcessColor3;
                    }
                }
                int iRed3 = Color.red(iProcessColor3);
                int iRed4 = Color.red(iProcessColor4);
                int iGreen3 = Color.green(iProcessColor3);
                int iGreen4 = Color.green(iProcessColor4);
                int iBlue3 = Color.blue(iProcessColor3);
                int iBlue4 = Color.blue(iProcessColor4);
                int iAlpha3 = Color.alpha(iProcessColor3);
                this.paint.setColor(((((int) (iRed3 + ((iRed4 - iRed3) * fClamp01))) & 255) << 16) | ((((int) (iAlpha3 + ((Color.alpha(iProcessColor4) - iAlpha3) * fClamp01))) & 255) << 24) | ((((int) (iGreen3 + ((iGreen4 - iGreen3) * fClamp01))) & 255) << 8) | (((int) (iBlue3 + ((iBlue4 - iBlue3) * fClamp01))) & 255));
                if (ExteraConfig.getNewSwitchStyle()) {
                    float fDp = AndroidUtilities.dp(7.0f);
                    float fDp2 = AndroidUtilities.dp(9.0f);
                    int i15 = this.drawIconType;
                    if (i15 != 1 && i15 != 2 && this.iconAnimator == null) {
                        float f9 = fDp + ((fDp2 - fDp) * fClamp02);
                        fDp2 = ((fDp2 - f9) * (this.iconDrawable != null ? this.animatorIconVisibility.getFloatValue() : 0.0f)) + f9;
                    }
                    float overlayPadding = i12 == 0 ? measuredWidth2 : getOverlayPadding() + measuredWidth2;
                    float overlayPadding2 = i12 == 0 ? measuredHeight2 : getOverlayPadding() + measuredHeight2;
                    float fDpf2 = AndroidUtilities.dpf2(3.0f) * (1.0f - Math.abs((fClamp02 * f7) - 1.0f));
                    this.rectF.set((overlayPadding - fDp2) - fDpf2, overlayPadding2 - fDp2, overlayPadding + fDp2 + fDpf2, overlayPadding2 + fDp2);
                    canvas3.drawRoundRect(this.rectF, fDp2, fDp2, this.paint);
                } else {
                    canvas3.drawCircle(i12 == 0 ? measuredWidth2 : getOverlayPadding() + measuredWidth2, i12 == 0 ? measuredHeight2 : getOverlayPadding() + measuredHeight2, AndroidUtilities.dp(f8), this.paint);
                }
                if (i12 != 0) {
                    i = 2;
                } else {
                    if (this.iconDrawable != null) {
                        float floatValue = this.animatorIconVisibility.getFloatValue();
                        if (floatValue > 0.0f) {
                            boolean z = floatValue < 1.0f;
                            if (z) {
                                canvas.save();
                                canvas.scale(floatValue, floatValue, measuredWidth2, measuredHeight2);
                            }
                            if (ExteraConfig.getNewSwitchStyle()) {
                                this.iconDrawable.setAlpha((int) (this.overrideAlpha * 255.0f));
                            }
                            Drawable drawable2 = this.iconDrawable;
                            drawable2.setBounds(measuredWidth2 - (drawable2.getIntrinsicWidth() / 2), measuredHeight2 - (this.iconDrawable.getIntrinsicHeight() / 2), (this.iconDrawable.getIntrinsicWidth() / 2) + measuredWidth2, (this.iconDrawable.getIntrinsicHeight() / 2) + measuredHeight2);
                            this.iconDrawable.draw(canvas3);
                            if (z) {
                                canvas.restore();
                            }
                        }
                    } else {
                        int i16 = this.drawIconType;
                        if (i16 == 1) {
                            measuredWidth2 = (int) (measuredWidth2 - (AndroidUtilities.dp(10.8f) - (AndroidUtilities.dp(1.3f) * this.progress)));
                            measuredHeight2 = (int) (measuredHeight2 - (AndroidUtilities.dp(8.5f) - (AndroidUtilities.dp(0.5f) * this.progress)));
                            int iDpf2 = ((int) AndroidUtilities.dpf2(4.6f)) + measuredWidth2;
                            int iDpf3 = (int) (AndroidUtilities.dpf2(9.5f) + measuredHeight2);
                            int iDp3 = AndroidUtilities.dp(f7) + iDpf2;
                            int iDp4 = AndroidUtilities.dp(f7) + iDpf3;
                            int iDpf4 = ((int) AndroidUtilities.dpf2(7.5f)) + measuredWidth2;
                            int iDpf5 = ((int) AndroidUtilities.dpf2(5.4f)) + measuredHeight2;
                            int iDp5 = AndroidUtilities.dp(7.0f) + iDpf4;
                            int iDp6 = AndroidUtilities.dp(7.0f) + iDpf5;
                            float f10 = iDpf4;
                            float f11 = iDpf2 - iDpf4;
                            float f12 = this.progress;
                            Canvas canvas4 = canvas3;
                            canvas4.drawLine((int) (f10 + (f11 * f12)), (int) (iDpf5 + ((iDpf3 - iDpf5) * f12)), (int) (iDp5 + ((iDp3 - iDp5) * f12)), (int) (iDp6 + ((iDp4 - iDp6) * f12)), this.paint2);
                            int iDpf6 = ((int) AndroidUtilities.dpf2(7.5f)) + measuredWidth2;
                            int iDpf7 = ((int) AndroidUtilities.dpf2(12.5f)) + measuredHeight2;
                            canvas4.drawLine(iDpf6, iDpf7, AndroidUtilities.dp(7.0f) + iDpf6, iDpf7 - AndroidUtilities.dp(7.0f), this.paint2);
                            canvas3 = canvas4;
                        } else {
                            Canvas canvas5 = canvas3;
                            i = 2;
                            if (i16 == 2 || this.iconAnimator != null) {
                                this.paint2.setAlpha((int) ((1.0f - this.iconProgress) * 255.0f * this.overrideAlpha));
                                float f13 = measuredWidth2;
                                float f14 = measuredHeight2;
                                canvas5.drawLine(f13, f14, f13, measuredHeight2 - AndroidUtilities.dp(5.0f), this.paint2);
                                canvas3 = canvas5;
                                canvas3.save();
                                canvas3.rotate(this.iconProgress * (-90.0f), f13, f14);
                                canvas5.drawLine(f13, f14, AndroidUtilities.dp(4.0f) + measuredWidth2, f14, this.paint2);
                                canvas3.restore();
                            } else {
                                canvas3 = canvas5;
                            }
                        }
                    }
                    i = 2;
                }
                if (i12 == 1) {
                    canvas3.drawBitmap(this.overlayMaskBitmap, -getOverlayPadding(), -getOverlayPadding(), this.overlayMaskPaint);
                }
            }
            i12++;
            i13 = i;
            i11 = 0;
        }
        if (this.overrideColorProgress != 0) {
            canvas.drawBitmap(this.overlayBitmap[1], -getOverlayPadding(), -getOverlayPadding(), (Paint) null);
        }
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.Switch");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.isChecked);
    }

    @Override // android.view.View
    public void setAlpha(float f) {
        if (ExteraConfig.getNewSwitchStyle()) {
            this.overrideAlpha = f;
            super.setAlpha(1.0f);
            invalidate();
        } else {
            this.overrideAlpha = 1.0f;
            super.setAlpha(f);
        }
    }
}
