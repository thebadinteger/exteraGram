package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.annotation.Keep;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec;
import com.google.android.material.progressindicator.DeterminateDrawable;
import com.google.android.material.progressindicator.IndeterminateDrawable;
import java.util.Locale;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;

public class RadialProgress2 implements Drawable.Callback {
    private int backgroundStroke;
    private float circleCheckProgress;
    private int circleColor;
    private int circleColorKey;
    private int circleCrossfadeColorKey;
    private float circleCrossfadeColorProgress;
    private Paint circleMiniPaint;
    public Paint circlePaint;
    private int circlePressedColor;
    private int circlePressedColorKey;
    private int circleRadius;
    private boolean drawBackground;
    private boolean drawMiniIcon;
    public float drawScale;
    public int iconColor;
    public int iconColorKey;
    private int iconPressedColor;
    private int iconPressedColorKey;
    public float iconScale;
    private IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawable;
    private int indicatorColor;
    private int indicatorPressedColor;
    private boolean invertColors;
    private boolean isPressed;
    private boolean isPressedMini;
    private int lastMainProgressLevel;
    private int lastMiniProgressLevel;
    private int maxIconSize;
    public MediaActionDrawable mediaActionDrawable;
    private Bitmap miniDrawBitmap;
    private Canvas miniDrawCanvas;
    private float miniIconScale;
    private IndeterminateDrawable<CircularProgressIndicatorSpec> miniIndeterminateDrawable;
    private MediaActionDrawable miniMediaActionDrawable;
    private Paint miniProgressBackgroundPaint;
    private DeterminateDrawable<CircularProgressIndicatorSpec> miniProgressDrawable;
    private CircularProgressIndicatorSpec miniProgressSpec;
    private boolean needDrawBackground;
    private float overlayImageAlpha;
    public ImageReceiver overlayImageView;
    private Paint overlayPaint;
    private float overrideAlpha;
    public float overrideCircleAlpha;
    private View parent;
    private int progressColor;
    private DeterminateDrawable<CircularProgressIndicatorSpec> progressDrawable;
    public RectF progressRect;
    private CircularProgressIndicatorSpec progressSpec;
    private Theme.ResourcesProvider resourcesProvider;
    protected int style;
    private int trackColor;
    private int trackPressedColor;
    private int waveAmplitude;

    private static boolean iconIsCancel(int i) {
        return i == 3 || i == 14 || i == 12 || i == 13;
    }

    public RadialProgress2(View view) {
        this(view, null);
    }

    public RadialProgress2(View view, Theme.ResourcesProvider resourcesProvider) {
        this.progressRect = new RectF();
        this.progressColor = -1;
        this.overlayPaint = new Paint(1);
        this.circlePaint = new Paint(1);
        this.circleMiniPaint = new Paint(1);
        this.needDrawBackground = true;
        this.invertColors = false;
        this.lastMainProgressLevel = -1;
        this.lastMiniProgressLevel = -1;
        this.indicatorColor = -1;
        this.indicatorPressedColor = -1;
        this.trackColor = -1;
        this.trackPressedColor = -1;
        this.miniIconScale = 1.0f;
        this.circleColorKey = -1;
        this.circleCrossfadeColorKey = -1;
        this.circleCheckProgress = 1.0f;
        this.circlePressedColorKey = -1;
        this.iconColorKey = -1;
        this.iconPressedColorKey = -1;
        this.overrideCircleAlpha = 1.0f;
        this.drawBackground = true;
        this.overrideAlpha = 1.0f;
        this.overlayImageAlpha = 1.0f;
        this.iconScale = 1.0f;
        this.drawScale = 1.0f;
        this.resourcesProvider = resourcesProvider;
        this.miniProgressBackgroundPaint = new Paint(1);
        this.parent = view;
        ImageReceiver imageReceiver = new ImageReceiver(view);
        this.overlayImageView = imageReceiver;
        imageReceiver.setInvalidateAll(true);
        this.mediaActionDrawable = new MediaActionDrawable();
        MediaActionDrawable mediaActionDrawable = new MediaActionDrawable();
        this.miniMediaActionDrawable = mediaActionDrawable;
        mediaActionDrawable.setMini(true);
        this.miniMediaActionDrawable.setIcon(4, false);
        int iDp = AndroidUtilities.dp(22.0f);
        this.circleRadius = iDp;
        this.overlayImageView.setRoundRadius(iDp);
        this.overlayPaint.setColor(1677721600);
        if (view != null) {
            this.mediaActionDrawable.setDelegate(new RadialProgress2$$ExternalSyntheticLambda0(view));
            this.miniMediaActionDrawable.setDelegate(new RadialProgress2$$ExternalSyntheticLambda0(view));
        }
    }

    public void setParent(View view) {
        this.parent = view;
        this.overlayImageView.setParentView(view);
        MediaActionDrawable mediaActionDrawable = this.mediaActionDrawable;
        Objects.requireNonNull(view);
        mediaActionDrawable.setDelegate(new RadialProgress2$$ExternalSyntheticLambda0(view));
        this.miniMediaActionDrawable.setDelegate(new RadialProgress2$$ExternalSyntheticLambda0(view));
    }

    @Keep
    public void setStyle(int i) {
        if (!ExteraConfig.getNewLoadingStyle() && i != 0) {
            i = 0;
        }
        if (this.style == i) {
            return;
        }
        this.style = i;
        if (i == 0) {
            this.mediaActionDrawable.drawProgressCircle = true;
            this.miniMediaActionDrawable.drawProgressCircle = true;
            stopMdcDrawable(this.progressDrawable, true);
            stopMdcDrawable(this.indeterminateDrawable, true);
            stopMdcDrawable(this.miniProgressDrawable, true);
            stopMdcDrawable(this.miniIndeterminateDrawable, true);
            this.progressDrawable = null;
            this.indeterminateDrawable = null;
            this.miniProgressDrawable = null;
            this.miniIndeterminateDrawable = null;
            this.progressSpec = null;
            this.miniProgressSpec = null;
            return;
        }
        if (i == 1 || i == 2) {
            this.mediaActionDrawable.drawProgressCircle = false;
            this.miniMediaActionDrawable.drawProgressCircle = false;
            initMdcDrawables(this.parent.getContext());
            int i2 = this.lastMainProgressLevel;
            if (i2 >= 0) {
                this.progressDrawable.setLevel(i2);
            }
            int i3 = this.lastMiniProgressLevel;
            if (i3 >= 0) {
                this.miniProgressDrawable.setLevel(i3);
            }
            setWavy(i == 2);
        }
    }

    @Keep
    public int getStyle() {
        return this.style;
    }

    public boolean isMaterial3Style() {
        int i = this.style;
        return i == 1 || i == 2;
    }

    @Keep
    public void setSpecValues(int i, int i2, int i3, int i4) {
        if (isMaterial3Style()) {
            CircularProgressIndicatorSpec circularProgressIndicatorSpec = this.progressSpec;
            circularProgressIndicatorSpec.indicatorSize = i;
            circularProgressIndicatorSpec.trackThickness = i2;
            circularProgressIndicatorSpec.trackCornerRadius = i3;
            circularProgressIndicatorSpec.useRelativeTrackCornerRadius = true;
            circularProgressIndicatorSpec.trackCornerRadiusFraction = 0.5f;
            circularProgressIndicatorSpec.indicatorTrackGapSize = i4;
        }
    }

    @Keep
    public void setMiniSpecValues(int i, int i2, int i3, int i4) {
        if (isMaterial3Style()) {
            CircularProgressIndicatorSpec circularProgressIndicatorSpec = this.miniProgressSpec;
            circularProgressIndicatorSpec.indicatorSize = i;
            circularProgressIndicatorSpec.trackThickness = i2;
            circularProgressIndicatorSpec.trackCornerRadius = i3;
            circularProgressIndicatorSpec.useRelativeTrackCornerRadius = true;
            circularProgressIndicatorSpec.trackCornerRadiusFraction = 0.5f;
            circularProgressIndicatorSpec.indicatorTrackGapSize = i4;
        }
    }

    private void initMdcDrawables(Context context) {
        this.progressSpec = new CircularProgressIndicatorSpec(context, null);
        CircularProgressIndicatorSpec circularProgressIndicatorSpec = new CircularProgressIndicatorSpec(context, null);
        this.miniProgressSpec = circularProgressIndicatorSpec;
        CircularProgressIndicatorSpec circularProgressIndicatorSpec2 = this.progressSpec;
        circularProgressIndicatorSpec2.hideAnimationBehavior = 1;
        circularProgressIndicatorSpec2.showAnimationBehavior = 2;
        circularProgressIndicatorSpec.hideAnimationBehavior = 1;
        circularProgressIndicatorSpec.showAnimationBehavior = 2;
        setSpecValues(this.circleRadius * 2, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(4.0f));
        setMiniSpecValues(AndroidUtilities.dp(24.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f));
        DeterminateDrawable<CircularProgressIndicatorSpec> determinateDrawableCreateCircularDrawable = DeterminateDrawable.createCircularDrawable(context, this.progressSpec);
        this.progressDrawable = determinateDrawableCreateCircularDrawable;
        determinateDrawableCreateCircularDrawable.setCallback(this);
        this.progressDrawable.setVisible(false, false, false);
        IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawableCreateCircularDrawable = IndeterminateDrawable.createCircularDrawable(context, this.progressSpec);
        this.indeterminateDrawable = indeterminateDrawableCreateCircularDrawable;
        indeterminateDrawableCreateCircularDrawable.setCallback(this);
        this.indeterminateDrawable.setVisible(false, false, false);
        DeterminateDrawable<CircularProgressIndicatorSpec> determinateDrawableCreateCircularDrawable2 = DeterminateDrawable.createCircularDrawable(context, this.miniProgressSpec);
        this.miniProgressDrawable = determinateDrawableCreateCircularDrawable2;
        determinateDrawableCreateCircularDrawable2.setCallback(this);
        this.miniProgressDrawable.setVisible(false, false, false);
        IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawableCreateCircularDrawable2 = IndeterminateDrawable.createCircularDrawable(context, this.miniProgressSpec);
        this.miniIndeterminateDrawable = indeterminateDrawableCreateCircularDrawable2;
        indeterminateDrawableCreateCircularDrawable2.setCallback(this);
        this.miniIndeterminateDrawable.setVisible(false, false, false);
        updateM3Colors();
    }

    private void updateM3Colors() {
        int themedColor;
        int i;
        int alphaComponent;
        int themedColor2;
        int themedColor3;
        int i2;
        int alphaComponent2;
        if (isMaterial3Style()) {
            if (this.progressSpec != null) {
                if (this.isPressed) {
                    int i3 = this.iconPressedColorKey;
                    themedColor2 = i3 >= 0 ? getThemedColor(i3) : this.iconPressedColor;
                    int i4 = this.circlePressedColorKey;
                    themedColor3 = i4 >= 0 ? getThemedColor(i4) : this.circlePressedColor;
                } else {
                    int i5 = this.iconColorKey;
                    themedColor2 = i5 >= 0 ? getThemedColor(i5) : this.iconColor;
                    int i6 = this.circleColorKey;
                    themedColor3 = i6 >= 0 ? getThemedColor(i6) : this.circleColor;
                }
                if (this.overlayImageView.hasBitmapImage()) {
                    float currentAlpha = this.overlayImageView.getCurrentAlpha();
                    if (currentAlpha >= 1.0f) {
                        themedColor2 = -1;
                    } else {
                        int iRed = Color.red(themedColor2);
                        int iGreen = Color.green(themedColor2);
                        int iBlue = Color.blue(themedColor2);
                        int iAlpha = Color.alpha(themedColor2);
                        themedColor2 = Color.argb(iAlpha + ((int) ((255 - iAlpha) * currentAlpha)), iRed + ((int) ((255 - iRed) * currentAlpha)), iGreen + ((int) ((255 - iGreen) * currentAlpha)), iBlue + ((int) ((255 - iBlue) * currentAlpha)));
                    }
                }
                if ((this.isPressed && (i2 = this.indicatorPressedColor) != -1) || (i2 = this.indicatorColor) != -1) {
                    themedColor2 = i2;
                }
                boolean z = (!this.overlayImageView.hasBitmapImage() || this.overlayImageView.getCurrentAlpha() < 1.0f) && this.drawBackground && (!isMaterial3Style() || this.needDrawBackground);
                if (!this.invertColors || z) {
                    themedColor3 = themedColor2;
                }
                if ((!this.isPressed || (alphaComponent2 = this.trackPressedColor) == -1) && (alphaComponent2 = this.trackColor) == -1) {
                    alphaComponent2 = ColorUtils.setAlphaComponent(themedColor3, getTrackAlpha());
                }
                CircularProgressIndicatorSpec circularProgressIndicatorSpec = this.progressSpec;
                int[] iArr = circularProgressIndicatorSpec.indicatorColors;
                if (iArr.length == 0 || iArr[0] != themedColor3) {
                    circularProgressIndicatorSpec.indicatorColors = new int[]{themedColor3};
                    PorterDuffColorFilter porterDuffColorFilter = new PorterDuffColorFilter(themedColor3, PorterDuff.Mode.SRC_IN);
                    IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawable = this.indeterminateDrawable;
                    if (indeterminateDrawable != null) {
                        indeterminateDrawable.setColorFilter(porterDuffColorFilter);
                    }
                    DeterminateDrawable<CircularProgressIndicatorSpec> determinateDrawable = this.progressDrawable;
                    if (determinateDrawable != null) {
                        determinateDrawable.setColorFilter(porterDuffColorFilter);
                    }
                }
                CircularProgressIndicatorSpec circularProgressIndicatorSpec2 = this.progressSpec;
                if (circularProgressIndicatorSpec2.trackColor != alphaComponent2) {
                    circularProgressIndicatorSpec2.trackColor = alphaComponent2;
                }
            }
            if (this.miniProgressSpec != null) {
                if (this.isPressedMini && this.circleCrossfadeColorKey < 0) {
                    int i7 = this.iconPressedColorKey;
                    themedColor = i7 >= 0 ? getThemedColor(i7) : this.iconPressedColor;
                } else {
                    int i8 = this.iconColorKey;
                    themedColor = i8 >= 0 ? getThemedColor(i8) : this.iconColor;
                }
                boolean z2 = this.isPressedMini;
                if ((z2 && (i = this.indicatorPressedColor) != -1) || (i = this.indicatorColor) != -1) {
                    themedColor = i;
                }
                if ((!z2 || (alphaComponent = this.trackPressedColor) == -1) && (alphaComponent = this.trackColor) == -1) {
                    alphaComponent = ColorUtils.setAlphaComponent(themedColor, getTrackAlpha());
                }
                CircularProgressIndicatorSpec circularProgressIndicatorSpec3 = this.miniProgressSpec;
                int[] iArr2 = circularProgressIndicatorSpec3.indicatorColors;
                if (iArr2.length == 0 || iArr2[0] != themedColor) {
                    circularProgressIndicatorSpec3.indicatorColors = new int[]{themedColor};
                }
                if (circularProgressIndicatorSpec3.trackColor != alphaComponent) {
                    circularProgressIndicatorSpec3.trackColor = alphaComponent;
                }
            }
        }
    }

    private int getTrackAlpha() {
        return (int) ((Theme.isCurrentThemeDark() ? 0.325f : 0.275f) * 255.0f);
    }

    @Keep
    public void setWavy(boolean z) {
        if (this.style != 2) {
            return;
        }
        if (z) {
            this.miniProgressSpec.indicatorInset = 0;
            this.progressSpec.indicatorInset = 0;
            setWavyValues(AndroidUtilities.dp(15.0f), AndroidUtilities.dp(1.6f), AndroidUtilities.dp(5.0f), 0.05f);
        } else {
            this.progressSpec.indicatorInset = AndroidUtilities.dp(4.0f);
            this.miniProgressSpec.indicatorInset = AndroidUtilities.dp(4.0f);
            setWavyValues(0, 0, 0, 1.0f);
        }
    }

    public void setWavyValues(int i, int i2, int i3, float f) {
        if (this.style != 2) {
            return;
        }
        CircularProgressIndicatorSpec circularProgressIndicatorSpec = this.progressSpec;
        circularProgressIndicatorSpec.wavelengthDeterminate = i;
        circularProgressIndicatorSpec.wavelengthIndeterminate = i;
        this.waveAmplitude = i2;
        circularProgressIndicatorSpec.waveAmplitude = i2;
        circularProgressIndicatorSpec.waveSpeed = i3;
        circularProgressIndicatorSpec.waveAmplitudeRampProgressMin = f;
        invalidateParent();
    }

    public void setNeedDrawBackground(boolean z) {
        this.needDrawBackground = z;
    }

    @Keep
    public boolean isNeedDrawBackground() {
        return this.needDrawBackground;
    }

    public void setInvertColors(boolean z) {
        this.invertColors = z;
    }

    @Keep
    public boolean isInvertColors() {
        return this.invertColors;
    }

    public void setResourcesProvider(Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
    }

    @Keep
    public void setAsMini() {
        this.mediaActionDrawable.setMini(true);
    }

    @Keep
    public void setCircleRadius(int i) {
        this.circleRadius = i;
        this.overlayImageView.setRoundRadius(i);
        if (isMaterial3Style()) {
            this.progressSpec.indicatorSize = this.circleRadius * 2;
        }
    }

    public int getRadius() {
        return this.circleRadius;
    }

    public void setBackgroundDrawable(Theme.MessageDrawable messageDrawable) {
        this.mediaActionDrawable.setBackgroundDrawable(messageDrawable);
        this.miniMediaActionDrawable.setBackgroundDrawable(messageDrawable);
    }

    @Keep
    public void setBackgroundGradientDrawable(LinearGradient linearGradient) {
        this.mediaActionDrawable.setBackgroundGradientDrawable(linearGradient);
        this.miniMediaActionDrawable.setBackgroundGradientDrawable(linearGradient);
    }

    public void setImageOverlay(TLRPC.PhotoSize photoSize, TLRPC.Document document, Object obj) {
        this.overlayImageView.setImage(ImageLocation.getForDocument(photoSize, document), String.format(Locale.US, "%d_%d", Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)), null, null, obj, 1);
    }

    public void setImageOverlay(TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2, TLRPC.Document document, Object obj) {
        String str = String.format(Locale.US, "%d_%d", Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2));
        this.overlayImageView.setImage(photoSize == null ? null : ImageLocation.getForDocument(photoSize, document), str, photoSize2 != null ? ImageLocation.getForDocument(photoSize2, document) : null, str, null, 0L, null, obj, 1);
    }

    public void setImageOverlay(String str) {
        this.overlayImageView.setImage(str, str != null ? String.format(Locale.US, "%d_%d", Integer.valueOf(this.circleRadius * 2), Integer.valueOf(this.circleRadius * 2)) : null, null, null, -1L);
    }

    public void setImageOverlay(Bitmap bitmap) {
        this.overlayImageView.setImageBitmap(bitmap);
    }

    public void onAttachedToWindow() {
        this.overlayImageView.onAttachedToWindow();
    }

    public void onDetachedFromWindow() {
        stopMdcDrawable(this.progressDrawable, false);
        stopMdcDrawable(this.indeterminateDrawable, false);
        stopMdcDrawable(this.miniProgressDrawable, false);
        stopMdcDrawable(this.miniIndeterminateDrawable, false);
        this.overlayImageView.onDetachedFromWindow();
    }

    public void setColorKeys(int i, int i2, int i3, int i4) {
        this.circleColorKey = i;
        this.circlePressedColorKey = i2;
        this.iconColorKey = i3;
        this.iconPressedColorKey = i4;
    }

    @Keep
    public void setM3Colors(int i, int i2, int i3, int i4) {
        this.indicatorColor = i;
        this.indicatorPressedColor = i2;
        this.trackColor = i3;
        this.trackPressedColor = i4;
    }

    @Keep
    public void setM3Colors(int i, int i2) {
        setM3Colors(i, i, i2, i2);
    }

    @Keep
    public void setColors(int i, int i2, int i3, int i4) {
        this.circleColor = i;
        this.circlePressedColor = i2;
        this.iconColor = i3;
        this.iconPressedColor = i4;
        this.circleColorKey = -1;
        this.circlePressedColorKey = -1;
        this.iconColorKey = -1;
        this.iconPressedColorKey = -1;
    }

    public void setCircleCrossfadeColor(int i, float f, float f2) {
        this.circleCrossfadeColorKey = i;
        this.circleCrossfadeColorProgress = f;
        this.circleCheckProgress = f2;
        this.miniIconScale = 1.0f;
        if (i >= 0) {
            initMiniIcons();
        }
    }

    public void setDrawBackground(boolean z) {
        this.drawBackground = z;
    }

    public void setProgressRect(int i, int i2, int i3, int i4) {
        this.progressRect.set(i, i2, i3, i4);
    }

    public void setProgressRect(float f, float f2, float f3, float f4) {
        this.progressRect.set(f, f2, f3, f4);
    }

    public RectF getProgressRect() {
        return this.progressRect;
    }

    public void setProgressColor(int i) {
        this.progressColor = i;
    }

    public void setMiniProgressBackgroundColor(int i) {
        this.miniProgressBackgroundPaint.setColor(i);
    }

    public void setProgress(float f, boolean z) {
        setProgress(f, z, false);
    }

    public void setProgress(float f, boolean z, boolean z2) {
        RadialProgress2 radialProgress2;
        if (z2 || !isMaterial3Style() || f != 0.0f || getProgress() <= 0.0f) {
            if (z2 || f != getProgress()) {
                int i = (int) (10000.0f * f);
                if (this.drawMiniIcon) {
                    this.miniMediaActionDrawable.setProgress(f, z);
                    if (isMaterial3Style()) {
                        this.miniProgressDrawable.setLevel(i);
                        updateM3Colors();
                        radialProgress2 = this;
                        radialProgress2.validateVisibleDrawables(f, getMiniIcon(), this.miniProgressDrawable, this.miniIndeterminateDrawable, z);
                    } else {
                        radialProgress2 = this;
                    }
                    radialProgress2.lastMiniProgressLevel = i;
                    return;
                }
                this.mediaActionDrawable.setProgress(f, z);
                if (isMaterial3Style()) {
                    this.progressDrawable.setLevel(i);
                    updateM3Colors();
                    validateVisibleDrawables(f, getIcon(), this.progressDrawable, this.indeterminateDrawable, z);
                }
                this.lastMainProgressLevel = i;
            }
        }
    }

    private void validateVisibleDrawables(float f, int i, DeterminateDrawable<CircularProgressIndicatorSpec> determinateDrawable, IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawable, boolean z) {
        View view;
        if (f >= 1.0f || (!iconIsCancel(i) && (f <= 0.04f || i != 2))) {
            if (determinateDrawable.isVisible()) {
                determinateDrawable.setVisible(false, false, z);
            }
            if (indeterminateDrawable.isVisible()) {
                indeterminateDrawable.setVisible(false, false, z);
                return;
            }
            return;
        }
        if (f <= 0.04f) {
            if (!indeterminateDrawable.isVisible() && (view = this.parent) != null && view.isAttachedToWindow()) {
                indeterminateDrawable.setVisible(true, true, true);
            }
            if (determinateDrawable.isVisible()) {
                determinateDrawable.setVisible(false, false, z);
                return;
            }
            return;
        }
        if (indeterminateDrawable.isVisible()) {
            indeterminateDrawable.setVisible(false, false, false);
        }
        if (determinateDrawable.isVisible()) {
            return;
        }
        determinateDrawable.setVisible(true, false, false);
    }

    public float getProgress() {
        return (this.drawMiniIcon ? this.miniMediaActionDrawable : this.mediaActionDrawable).getProgress();
    }

    private void invalidateParent() {
        int iDp = AndroidUtilities.dp(2.0f);
        View view = this.parent;
        RectF rectF = this.progressRect;
        int i = ((int) rectF.left) - iDp;
        int i2 = ((int) rectF.top) - iDp;
        int i3 = iDp * 2;
        view.invalidate(i, i2, ((int) rectF.right) + i3, ((int) rectF.bottom) + i3);
    }

    public int getIcon() {
        return this.mediaActionDrawable.getCurrentIcon();
    }

    public int getMiniIcon() {
        return this.miniMediaActionDrawable.getCurrentIcon();
    }

    @Keep
    public void setIcon(int i, boolean z, boolean z2) {
        RadialProgress2 radialProgress2;
        int i2;
        boolean z3;
        boolean z4 = i == this.mediaActionDrawable.getCurrentIcon();
        if (z && z4) {
            return;
        }
        if (isMaterial3Style() && this.mediaActionDrawable.getProgress() > 0.999f && iconIsCancel(i)) {
            setProgress(0.0f, false, true);
        }
        if (!isMaterial3Style() || z4) {
            radialProgress2 = this;
            i2 = i;
            z3 = z2;
        } else {
            updateM3Colors();
            radialProgress2 = this;
            i2 = i;
            z3 = z2;
            radialProgress2.validateVisibleDrawables(i2, this.mediaActionDrawable.getCurrentIcon(), getProgress(), this.progressDrawable, this.indeterminateDrawable, z3);
        }
        radialProgress2.mediaActionDrawable.setIcon(i2, z3);
        View view = radialProgress2.parent;
        if (view != null) {
            if (!z3) {
                view.invalidate();
            } else {
                radialProgress2.invalidateParent();
            }
        }
    }

    private void validateVisibleDrawables(int i, int i2, float f, DeterminateDrawable<CircularProgressIndicatorSpec> determinateDrawable, IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawable, boolean z) {
        View view;
        if (iconIsCancel(i) || (f > 0.04f && f < 1.0f && iconIsCancel(i2) && i == 2)) {
            boolean z2 = f <= 0.04f;
            if (determinateDrawable.isVisible() == z2) {
                determinateDrawable.setVisible(!z2, false, false);
            }
            if (indeterminateDrawable.isVisible() != z2) {
                if (!z2 || ((view = this.parent) != null && view.isAttachedToWindow())) {
                    indeterminateDrawable.setVisible(z2, true, z2);
                    return;
                }
                return;
            }
            return;
        }
        if (determinateDrawable.isVisible()) {
            determinateDrawable.setVisible(false, false, z);
        }
        if (indeterminateDrawable.isVisible()) {
            indeterminateDrawable.setVisible(false, false, z);
        }
    }

    public void setMiniIconScale(float f) {
        this.miniIconScale = f;
    }

    public void setMiniIcon(int i, boolean z, boolean z2) {
        RadialProgress2 radialProgress2;
        int i2;
        boolean z3;
        if (i == 2 || i == 3 || i == 4) {
            boolean z4 = i == this.miniMediaActionDrawable.getCurrentIcon();
            if (z && z4) {
                return;
            }
            if (this.drawMiniIcon && isMaterial3Style() && !z4) {
                updateM3Colors();
                radialProgress2 = this;
                i2 = i;
                z3 = z2;
                radialProgress2.validateVisibleDrawables(i2, this.miniMediaActionDrawable.getCurrentIcon(), getProgress(), this.miniProgressDrawable, this.miniIndeterminateDrawable, z3);
            } else {
                radialProgress2 = this;
                i2 = i;
                z3 = z2;
            }
            radialProgress2.miniMediaActionDrawable.setIcon(i2, z3);
            boolean z5 = i2 != 4 || radialProgress2.miniMediaActionDrawable.getTransitionProgress() < 1.0f;
            radialProgress2.drawMiniIcon = z5;
            if (z5) {
                radialProgress2.initMiniIcons();
            }
            if (!z3) {
                radialProgress2.parent.invalidate();
            } else {
                radialProgress2.invalidateParent();
            }
        }
    }

    public void initMiniIcons() {
        if (this.miniDrawBitmap == null) {
            try {
                this.miniDrawBitmap = Bitmap.createBitmap(AndroidUtilities.dp(48.0f), AndroidUtilities.dp(48.0f), Bitmap.Config.ARGB_8888);
                this.miniDrawCanvas = new Canvas(this.miniDrawBitmap);
            } catch (Throwable unused) {
            }
        }
    }

    public void setPressed(boolean z, boolean z2) {
        if (z2) {
            this.isPressedMini = z;
        } else {
            this.isPressed = z;
        }
        updateM3Colors();
        invalidateParent();
    }

    public void setOverrideAlpha(float f) {
        this.overrideAlpha = f;
    }

    public float getOverrideAlpha() {
        return this.overrideAlpha;
    }

    public float getWholeAlpha() {
        float transitionProgress;
        int currentIcon = this.mediaActionDrawable.getCurrentIcon();
        int previousIcon = this.mediaActionDrawable.getPreviousIcon();
        if (this.backgroundStroke != 0) {
            if (currentIcon != 3) {
                if (previousIcon == 3) {
                    return this.mediaActionDrawable.getTransitionProgress();
                }
                return 1.0f;
            }
            transitionProgress = this.mediaActionDrawable.getTransitionProgress();
        } else {
            if ((currentIcon == 3 || currentIcon == 6 || currentIcon == 10 || currentIcon == 8 || currentIcon == 0) && previousIcon == 4) {
                return this.mediaActionDrawable.getTransitionProgress();
            }
            if (currentIcon != 4) {
                return 1.0f;
            }
            transitionProgress = this.mediaActionDrawable.getTransitionProgress();
        }
        return 1.0f - transitionProgress;
    }

    @Keep
    public void draw(Canvas canvas) {
        int themedColor;
        int iCeil;
        int iCeil2;
        float f;
        int i;
        int iSave;
        Canvas canvas2;
        boolean z;
        IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawable;
        boolean z2;
        int i2;
        float f2;
        float fCenterX;
        float fDp;
        int i3;
        int i4;
        float transitionProgress;
        int iSave2;
        Canvas canvas3;
        View view;
        IndeterminateDrawable<CircularProgressIndicatorSpec> indeterminateDrawable2;
        Canvas canvas4;
        Canvas canvas5;
        int iArgb;
        if ((this.mediaActionDrawable.getCurrentIcon() != 4 || this.mediaActionDrawable.getTransitionProgress() < 1.0f) && !this.progressRect.isEmpty()) {
            boolean z3 = false;
            boolean z4 = this.drawScale != 1.0f;
            if (z4) {
                canvas.save();
                float f3 = this.drawScale;
                canvas.scale(f3, f3, this.progressRect.centerX(), this.progressRect.centerY());
            }
            int currentIcon = this.mediaActionDrawable.getCurrentIcon();
            int previousIcon = this.mediaActionDrawable.getPreviousIcon();
            float wholeAlpha = getWholeAlpha();
            if (this.isPressedMini && this.circleCrossfadeColorKey < 0) {
                int i5 = this.iconPressedColorKey;
                MediaActionDrawable mediaActionDrawable = this.miniMediaActionDrawable;
                if (i5 >= 0) {
                    mediaActionDrawable.setColor(getThemedColor(i5));
                } else {
                    mediaActionDrawable.setColor(this.iconPressedColor);
                }
                int i6 = this.circlePressedColorKey;
                Paint paint = this.circleMiniPaint;
                if (i6 >= 0) {
                    paint.setColor(getThemedColor(i6));
                } else {
                    paint.setColor(this.circlePressedColor);
                }
            } else {
                int i7 = this.iconColorKey;
                MediaActionDrawable mediaActionDrawable2 = this.miniMediaActionDrawable;
                if (i7 >= 0) {
                    mediaActionDrawable2.setColor(getThemedColor(i7));
                } else {
                    mediaActionDrawable2.setColor(this.iconColor);
                }
                int i8 = this.circleColorKey;
                if (i8 >= 0) {
                    int i9 = this.circleCrossfadeColorKey;
                    Paint paint2 = this.circleMiniPaint;
                    if (i9 >= 0) {
                        paint2.setColor(AndroidUtilities.getOffsetColor(getThemedColor(i8), getThemedColor(this.circleCrossfadeColorKey), this.circleCrossfadeColorProgress, this.circleCheckProgress));
                    } else {
                        paint2.setColor(getThemedColor(i8));
                    }
                } else {
                    this.circleMiniPaint.setColor(this.circleColor);
                }
            }
            if (this.isPressed) {
                int i10 = this.iconPressedColorKey;
                MediaActionDrawable mediaActionDrawable3 = this.mediaActionDrawable;
                if (i10 >= 0) {
                    themedColor = getThemedColor(i10);
                    mediaActionDrawable3.setColor(themedColor);
                    this.mediaActionDrawable.setBackColor(getThemedColor(this.circlePressedColorKey));
                } else {
                    themedColor = this.iconPressedColor;
                    mediaActionDrawable3.setColor(themedColor);
                    this.mediaActionDrawable.setBackColor(this.circlePressedColor);
                }
                int i11 = this.circlePressedColorKey;
                Paint paint3 = this.circlePaint;
                if (i11 >= 0) {
                    paint3.setColor(getThemedColor(i11));
                } else {
                    paint3.setColor(this.circlePressedColor);
                }
            } else {
                int i12 = this.iconColorKey;
                MediaActionDrawable mediaActionDrawable4 = this.mediaActionDrawable;
                if (i12 >= 0) {
                    themedColor = getThemedColor(i12);
                    mediaActionDrawable4.setColor(themedColor);
                    this.mediaActionDrawable.setBackColor(getThemedColor(this.circleColorKey));
                } else {
                    themedColor = this.iconColor;
                    mediaActionDrawable4.setColor(themedColor);
                    this.mediaActionDrawable.setBackColor(this.circleColor);
                }
                int i13 = this.circleColorKey;
                Paint paint4 = this.circlePaint;
                if (i13 >= 0) {
                    paint4.setColor(getThemedColor(i13));
                } else {
                    paint4.setColor(this.circleColor);
                }
            }
            if ((this.drawMiniIcon || this.circleCrossfadeColorKey >= 0) && this.miniDrawCanvas != null) {
                this.miniDrawBitmap.eraseColor(0);
            }
            this.circlePaint.setAlpha((int) (this.circlePaint.getAlpha() * wholeAlpha * this.overrideAlpha * this.overrideCircleAlpha));
            this.circleMiniPaint.setAlpha((int) (this.circleMiniPaint.getAlpha() * wholeAlpha * this.overrideAlpha));
            if ((this.drawMiniIcon || this.circleCrossfadeColorKey >= 0) && this.miniDrawCanvas != null) {
                iCeil = (int) Math.ceil(this.progressRect.width() / 2.0f);
                iCeil2 = (int) Math.ceil(this.progressRect.height() / 2.0f);
            } else {
                iCeil = (int) this.progressRect.centerX();
                iCeil2 = (int) this.progressRect.centerY();
            }
            if (this.overlayImageView.hasBitmapImage()) {
                float currentAlpha = this.overlayImageView.getCurrentAlpha();
                f = 1.0f;
                this.overlayPaint.setAlpha((int) (this.overrideAlpha * 100.0f * currentAlpha * wholeAlpha));
                if (currentAlpha >= 1.0f) {
                    iArgb = -1;
                    i = 2;
                } else {
                    int iRed = Color.red(themedColor);
                    int iGreen = Color.green(themedColor);
                    int iBlue = Color.blue(themedColor);
                    int iAlpha = Color.alpha(themedColor);
                    i = 2;
                    iArgb = Color.argb(iAlpha + ((int) ((255 - iAlpha) * currentAlpha)), iRed + ((int) ((255 - iRed) * currentAlpha)), iGreen + ((int) ((255 - iGreen) * currentAlpha)), iBlue + ((int) ((255 - iBlue) * currentAlpha)));
                    z3 = true;
                }
                this.mediaActionDrawable.setColor(iArgb);
                ImageReceiver imageReceiver = this.overlayImageView;
                int i14 = this.circleRadius;
                imageReceiver.setImageCoords(iCeil - i14, iCeil2 - i14, i14 * 2, i14 * 2);
            } else {
                z4 = z4;
                f = 1.0f;
                i = 2;
                z3 = true;
            }
            Canvas canvas6 = this.miniDrawCanvas;
            if (canvas6 == null || this.circleCrossfadeColorKey < 0 || this.circleCheckProgress == f) {
                iSave = Integer.MIN_VALUE;
            } else {
                iSave = canvas6.save();
                float f4 = f - ((f - this.circleCheckProgress) * 0.1f);
                this.miniDrawCanvas.scale(f4, f4, iCeil, iCeil2);
            }
            boolean z5 = z3 && this.drawBackground && (!isMaterial3Style() || this.needDrawBackground);
            if (z5) {
                if ((this.drawMiniIcon || this.circleCrossfadeColorKey >= 0) && (canvas5 = this.miniDrawCanvas) != null) {
                    canvas5.drawCircle(iCeil, iCeil2, this.circleRadius, this.circlePaint);
                } else if (currentIcon != 4 || wholeAlpha != 0.0f) {
                    int i15 = this.backgroundStroke;
                    int i16 = this.circleRadius;
                    if (i15 != 0) {
                        canvas.drawCircle(iCeil, iCeil2, i16 - AndroidUtilities.dp(3.5f), this.circlePaint);
                    } else {
                        canvas.drawCircle(iCeil, iCeil2, i16, this.circlePaint);
                    }
                }
            }
            if (this.overlayImageView.hasBitmapImage()) {
                this.overlayImageView.setAlpha(this.overrideAlpha * wholeAlpha * this.overlayImageAlpha);
                if ((this.drawMiniIcon || this.circleCrossfadeColorKey >= 0) && (canvas4 = this.miniDrawCanvas) != null) {
                    this.overlayImageView.draw(canvas4);
                    this.miniDrawCanvas.drawCircle(iCeil, iCeil2, this.circleRadius, this.overlayPaint);
                } else {
                    this.overlayImageView.draw(canvas);
                    canvas.drawCircle(iCeil, iCeil2, this.circleRadius, this.overlayPaint);
                }
            }
            int i17 = this.circleRadius;
            int i18 = this.maxIconSize;
            if (i18 > 0 && i17 > i18) {
                i17 = i18;
            }
            if (this.iconScale != f) {
                canvas.save();
                float f5 = this.iconScale;
                canvas.scale(f5, f5, iCeil, iCeil2);
            }
            int i19 = iCeil - i17;
            int i20 = iCeil2 - i17;
            int i21 = iCeil + i17;
            int i22 = iCeil2 + i17;
            this.mediaActionDrawable.setBounds(i19, i20, i21, i22);
            this.mediaActionDrawable.setHasOverlayImage(this.overlayImageView.hasBitmapImage());
            if ((!this.drawMiniIcon && this.circleCrossfadeColorKey < 0) || (canvas2 = this.miniDrawCanvas) == null) {
                canvas2 = canvas;
            }
            boolean z6 = ExteraConfig.getNewLoadingStyle() && isMaterial3Style();
            float progress = getProgress();
            int currentIcon2 = this.miniMediaActionDrawable.getCurrentIcon();
            if (z6) {
                if ((this.drawMiniIcon || !iconIsCancel(currentIcon)) && (!this.drawMiniIcon || !iconIsCancel(currentIcon2))) {
                    if (progress > 0.04f && progress < f) {
                        boolean z7 = this.drawMiniIcon;
                        int i23 = i;
                        if ((!z7 || currentIcon2 != i23) && (z7 || currentIcon != i23)) {
                        }
                    }
                    z = false;
                }
                z = true;
            } else {
                z = false;
            }
            boolean z8 = !(this.drawMiniIcon || (indeterminateDrawable2 = this.indeterminateDrawable) == null || !indeterminateDrawable2.isVisible()) || (this.drawMiniIcon && (indeterminateDrawable = this.miniIndeterminateDrawable) != null && indeterminateDrawable.isVisible());
            if (z) {
                f2 = 255.0f;
                if (this.drawMiniIcon) {
                    z2 = z;
                    i2 = 2;
                } else {
                    updateM3Colors();
                    z2 = z;
                    i2 = 2;
                    if (this.style == 2) {
                        this.progressSpec.waveAmplitude = z5 ? 0 : (int) (this.waveAmplitude * getWaveScale(currentIcon, previousIcon));
                    }
                    int i24 = (int) (wholeAlpha * 255.0f * this.overrideAlpha);
                    if (progress <= 0.04f && !z8 && (view = this.parent) != null && view.isAttachedToWindow()) {
                        this.indeterminateDrawable.setVisible(true, true, true);
                        z8 = true;
                    }
                    if (!z8) {
                        this.progressDrawable.setBounds(i19, i20, i21, i22);
                        this.progressDrawable.setAlpha(i24);
                        this.progressDrawable.draw(canvas2);
                    } else {
                        this.indeterminateDrawable.setBounds(i19, i20, i21, i22);
                        this.indeterminateDrawable.setAlpha(i24);
                        this.indeterminateDrawable.draw(canvas2);
                    }
                }
            } else {
                z2 = z;
                i2 = 2;
                f2 = 255.0f;
            }
            if (this.drawMiniIcon || this.circleCrossfadeColorKey >= 0) {
                Canvas canvas7 = this.miniDrawCanvas;
                MediaActionDrawable mediaActionDrawable5 = this.mediaActionDrawable;
                if (canvas7 != null) {
                    mediaActionDrawable5.draw(canvas7);
                } else {
                    mediaActionDrawable5.draw(canvas);
                }
            } else {
                this.mediaActionDrawable.setOverrideAlpha(this.overrideAlpha);
                this.mediaActionDrawable.draw(canvas);
            }
            if (iSave != Integer.MIN_VALUE && (canvas3 = this.miniDrawCanvas) != null) {
                canvas3.restoreToCount(iSave);
            }
            if (this.drawMiniIcon || this.circleCrossfadeColorKey >= 0) {
                float fAbs = Math.abs(this.progressRect.width() - AndroidUtilities.dp(44.0f));
                float f6 = AndroidUtilities.density;
                RectF rectF = this.progressRect;
                if (fAbs < f6) {
                    fCenterX = rectF.centerX() + AndroidUtilities.dp(16.0f);
                    fDp = this.progressRect.centerY() + AndroidUtilities.dp(16.0f);
                    i4 = 20;
                    i3 = 0;
                } else {
                    fCenterX = rectF.centerX() + AndroidUtilities.dp(18.0f);
                    fDp = AndroidUtilities.dp(18.0f) + this.progressRect.centerY();
                    i3 = i2;
                    i4 = 22;
                }
                int i25 = i4 / 2;
                if (this.drawMiniIcon) {
                    transitionProgress = this.miniMediaActionDrawable.getCurrentIcon() != 4 ? f : f - this.miniMediaActionDrawable.getTransitionProgress();
                    if (transitionProgress == 0.0f) {
                        this.drawMiniIcon = false;
                    }
                } else {
                    transitionProgress = f;
                }
                Canvas canvas8 = this.miniDrawCanvas;
                if (canvas8 != null) {
                    float f7 = i4 + 18 + i3;
                    canvas8.drawCircle(AndroidUtilities.dp(f7), AndroidUtilities.dp(f7), AndroidUtilities.dp(i25 + 1) * transitionProgress * this.miniIconScale, Theme.checkboxSquare_eraserPaint);
                } else {
                    this.miniProgressBackgroundPaint.setColor(this.progressColor);
                    canvas.drawCircle(fCenterX, fDp, AndroidUtilities.dp(12.0f), this.miniProgressBackgroundPaint);
                }
                if (this.miniDrawCanvas != null) {
                    Bitmap bitmap = this.miniDrawBitmap;
                    RectF rectF2 = this.progressRect;
                    canvas.drawBitmap(bitmap, (int) rectF2.left, (int) rectF2.top, (Paint) null);
                }
                if (this.miniIconScale < f) {
                    iSave2 = canvas.save();
                    float f8 = this.miniIconScale;
                    canvas.scale(f8, f8, fCenterX, fDp);
                } else {
                    iSave2 = Integer.MIN_VALUE;
                }
                float f9 = i25;
                canvas.drawCircle(fCenterX, fDp, (AndroidUtilities.dp(f9) * transitionProgress) + (AndroidUtilities.dp(f) * (f - this.circleCheckProgress)), this.circleMiniPaint);
                if (this.drawMiniIcon) {
                    this.miniMediaActionDrawable.setBounds((int) (fCenterX - (AndroidUtilities.dp(f9) * transitionProgress)), (int) (fDp - (AndroidUtilities.dp(f9) * transitionProgress)), (int) ((AndroidUtilities.dp(f9) * transitionProgress) + fCenterX), (int) ((AndroidUtilities.dp(f9) * transitionProgress) + fDp));
                    if (z2) {
                        updateM3Colors();
                        int iDp = AndroidUtilities.dp(12.0f);
                        if (progress <= 0.04f) {
                            float f10 = iDp * transitionProgress;
                            this.miniIndeterminateDrawable.setBounds((int) (fCenterX - f10), (int) (fDp - f10), (int) (fCenterX + f10), (int) (fDp + f10));
                            this.miniIndeterminateDrawable.setAlpha((int) (transitionProgress * f2));
                            this.miniIndeterminateDrawable.draw(canvas);
                        } else {
                            float f11 = iDp * transitionProgress;
                            this.miniProgressDrawable.setBounds((int) (fCenterX - f11), (int) (fDp - f11), (int) (fCenterX + f11), (int) (fDp + f11));
                            this.miniProgressDrawable.setAlpha((int) (transitionProgress * f2));
                            this.miniProgressDrawable.draw(canvas);
                        }
                    }
                    this.miniMediaActionDrawable.draw(canvas);
                }
                if (iSave2 != Integer.MIN_VALUE) {
                    canvas.restoreToCount(iSave2);
                }
            }
            if (this.iconScale != f) {
                canvas.restore();
            }
            if (z4) {
                canvas.restore();
            }
        }
    }

    private float getWaveScale(int i, int i2) {
        float f = iconIsCancel(i) ? 1.0f : 0.0f;
        float f2 = iconIsCancel(i2) ? 1.0f : 0.0f;
        return f2 + ((f - f2) * this.mediaActionDrawable.getTransitionProgress());
    }

    public int getCircleColorKey() {
        return this.circleColorKey;
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void setMaxIconSize(int i) {
        this.maxIconSize = i;
    }

    public void setOverlayImageAlpha(float f) {
        this.overlayImageAlpha = f;
    }

    public float getTransitionProgress() {
        return (this.drawMiniIcon ? this.miniMediaActionDrawable : this.mediaActionDrawable).getTransitionProgress();
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void invalidateDrawable(Drawable drawable) {
        invalidateParent();
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
        View view = this.parent;
        if (view != null) {
            view.scheduleDrawable(drawable, runnable, j);
        }
    }

    @Override // android.graphics.drawable.Drawable.Callback
    public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
        View view = this.parent;
        if (view != null) {
            view.unscheduleDrawable(drawable, runnable);
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    private static void stopMdcDrawable(Drawable drawable, boolean z) {
        if (drawable == 0) {
            return;
        }
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).stop();
        } else {
            drawable.setVisible(false, false);
        }
        if (z) {
            drawable.setCallback(null);
        }
    }
}
