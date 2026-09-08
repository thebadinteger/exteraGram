package org.telegram.ui.Components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.support.annotation.Keep;
import android.text.TextPaint;
import android.view.animation.DecelerateInterpolator;
import com.exteragram.messenger.plugins.PluginsController;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class MediaActionDrawable extends Drawable {
    private float animatedDownloadProgress;
    private boolean animatingTransition;
    private ColorFilter colorFilter;
    private int currentIcon;
    private MediaActionDrawableDelegate delegate;
    private float downloadProgress;
    private float downloadProgressAnimationStart;
    private float downloadProgressTime;
    private float downloadRadOffset;
    private LinearGradient gradientDrawable;
    private Matrix gradientMatrix;
    private boolean hasOverlayImage;
    private boolean isMini;
    private long lastAnimationTime;
    private Theme.MessageDrawable messageDrawable;
    private int nextIcon;
    private String percentString;
    private int percentStringWidth;
    private float savedTransitionProgress;
    private TextPaint textPaint = new TextPaint(1);
    public Paint paint = new Paint(1);
    private Paint backPaint = new Paint(1);
    public Paint paint2 = new Paint(1);
    private Paint paint3 = new Paint(1);
    private Paint drawablePaint = new Paint(1);
    private RectF rect = new RectF();
    private final ColorFilter whiteColorFilter = new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN);
    private float scale = 1.0f;
    private DecelerateInterpolator interpolator = new DecelerateInterpolator();
    private float transitionAnimationTime = 400.0f;
    private int lastPercent = -1;
    private float overrideAlpha = 1.0f;
    private float transitionProgress = 1.0f;
    public boolean drawProgressCircle = true;
    private float downloadIconScale = 1.0f;

    public interface MediaActionDrawableDelegate {
        void invalidate();
    }

    public static float getCircleValue(float f) {
        while (f > 360.0f) {
            f -= 360.0f;
        }
        return f;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    public static boolean isCustomFileIcon(int i) {
        return i == 16 || i == 17 || i == 18 || PluginsController.isPluginFileIcon(i);
    }

    public void setDownloadIconScale(float f) {
        this.downloadIconScale = f;
    }

    @Keep
    public float getDownloadIconScale() {
        return this.downloadIconScale;
    }

    public MediaActionDrawable() {
        this.paint.setColor(-1);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
        this.paint.setStrokeWidth(AndroidUtilities.dp(3.0f));
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint3.setColor(-1);
        this.textPaint.setTypeface(AndroidUtilities.bold());
        this.textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        this.textPaint.setColor(-1);
        this.paint2.setColor(-1);
    }

    public void setOverrideAlpha(float f) {
        this.overrideAlpha = f;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.colorFilter = colorFilter;
        this.paint.setColorFilter(colorFilter);
        this.paint2.setColorFilter(colorFilter);
        this.paint3.setColorFilter(colorFilter);
        this.textPaint.setColorFilter(colorFilter);
    }

    public void setColor(int i) {
        int i2 = (-16777216) | i;
        this.paint.setColor(i2);
        this.paint2.setColor(i2);
        this.paint3.setColor(i2);
        this.textPaint.setColor(i2);
        this.colorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY);
    }

    public void setBackColor(int i) {
        this.backPaint.setColor(i | (-16777216));
    }

    public void setMini(boolean z) {
        this.isMini = z;
        this.paint.setStrokeWidth(AndroidUtilities.dp(z ? 2.0f : 3.0f));
    }

    private void drawTintedDrawable(Canvas canvas, Drawable drawable, int i, int i2, float f, int i3) {
        int intrinsicWidth = ((int) (drawable.getIntrinsicWidth() * f)) / 2;
        int i4 = i - intrinsicWidth;
        int intrinsicHeight = ((int) (drawable.getIntrinsicHeight() * f)) / 2;
        int i5 = i2 - intrinsicHeight;
        int i6 = i + intrinsicWidth;
        int i7 = i2 + intrinsicHeight;
        applyShaderMatrix(true);
        float f2 = i4;
        float f3 = i5;
        float f4 = i6;
        float f5 = i7;
        int iSaveLayer = canvas.saveLayer(f2, f3, f4, f5, null);
        drawable.setColorFilter(this.whiteColorFilter);
        drawable.setAlpha(i3);
        drawable.setBounds(i4, i5, i6, i7);
        drawable.draw(canvas);
        this.drawablePaint.set(this.paint2);
        this.drawablePaint.setAlpha(i3);
        this.drawablePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawRect(f2, f3, f4, f5, this.drawablePaint);
        this.drawablePaint.setXfermode(null);
        canvas.restoreToCount(iSaveLayer);
    }

    public void setDelegate(MediaActionDrawableDelegate mediaActionDrawableDelegate) {
        this.delegate = mediaActionDrawableDelegate;
    }

    public boolean setIcon(int i, boolean z) {
        int i2;
        int i3;
        if (this.currentIcon == i && (i3 = this.nextIcon) != i) {
            this.currentIcon = i3;
            this.transitionProgress = 1.0f;
        }
        int i4 = this.currentIcon;
        if (z) {
            if (i4 == i || (i2 = this.nextIcon) == i) {
                return false;
            }
            if ((i4 == 0 && i == 1) || (i4 == 1 && i == 0)) {
                this.transitionAnimationTime = 300.0f;
            } else if (i4 == 2 && (i == 3 || i == 14)) {
                this.transitionAnimationTime = this.drawProgressCircle ? 400.0f : 250.0f;
            } else if (i4 != 4 && i == 6) {
                this.transitionAnimationTime = 360.0f;
            } else if ((i4 == 4 && i == 14) || (i4 == 14 && i == 4)) {
                this.transitionAnimationTime = 160.0f;
            } else {
                this.transitionAnimationTime = 220.0f;
            }
            if (this.animatingTransition) {
                this.currentIcon = i2;
            }
            this.animatingTransition = true;
            this.nextIcon = i;
            this.savedTransitionProgress = this.transitionProgress;
            this.transitionProgress = 0.0f;
        } else {
            if (i4 == i) {
                return false;
            }
            this.animatingTransition = false;
            this.nextIcon = i;
            this.currentIcon = i;
            this.savedTransitionProgress = this.transitionProgress;
            this.transitionProgress = 1.0f;
        }
        if (i == 3 || i == 14) {
            this.downloadRadOffset = 112.0f;
            this.animatedDownloadProgress = 0.0f;
            this.downloadProgressAnimationStart = 0.0f;
            this.downloadProgressTime = 0.0f;
        }
        invalidateSelf();
        return true;
    }

    public int getCurrentIcon() {
        return this.nextIcon;
    }

    public int getPreviousIcon() {
        return this.currentIcon;
    }

    public void setProgress(float f, boolean z) {
        if (this.downloadProgress == f) {
            return;
        }
        if (!z) {
            this.animatedDownloadProgress = f;
            this.downloadProgressAnimationStart = f;
        } else {
            if (this.animatedDownloadProgress > f) {
                this.animatedDownloadProgress = f;
            }
            this.downloadProgressAnimationStart = this.animatedDownloadProgress;
        }
        this.downloadProgress = f;
        this.downloadProgressTime = 0.0f;
        invalidateSelf();
    }

    public float getProgress() {
        return this.downloadProgress;
    }

    public float getTransitionProgress() {
        if (this.animatingTransition) {
            return this.transitionProgress;
        }
        return 1.0f;
    }

    public void setBackgroundDrawable(Theme.MessageDrawable messageDrawable) {
        this.messageDrawable = messageDrawable;
    }

    public void setBackgroundGradientDrawable(LinearGradient linearGradient) {
        this.gradientDrawable = linearGradient;
        this.gradientMatrix = new Matrix();
    }

    public void setHasOverlayImage(boolean z) {
        this.hasOverlayImage = z;
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(int i, int i2, int i3, int i4) {
        super.setBounds(i, i2, i3, i4);
        float intrinsicWidth = (i3 - i) / getIntrinsicWidth();
        this.scale = intrinsicWidth;
        if (intrinsicWidth < 0.7f) {
            this.paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void invalidateSelf() {
        super.invalidateSelf();
        MediaActionDrawableDelegate mediaActionDrawableDelegate = this.delegate;
        if (mediaActionDrawableDelegate != null) {
            mediaActionDrawableDelegate.invalidate();
        }
    }

    public void applyShaderMatrix(boolean z) {
        Theme.MessageDrawable messageDrawable = this.messageDrawable;
        if (messageDrawable == null || !messageDrawable.hasGradient() || this.hasOverlayImage) {
            return;
        }
        Rect bounds = getBounds();
        Shader gradientShader = this.messageDrawable.getGradientShader();
        Matrix matrix = this.messageDrawable.getMatrix();
        matrix.reset();
        this.messageDrawable.applyMatrixScale();
        if (z) {
            matrix.postTranslate(-bounds.centerX(), (-this.messageDrawable.getTopY()) + bounds.top);
        } else {
            matrix.postTranslate(0.0f, -this.messageDrawable.getTopY());
        }
        gradientShader.setLocalMatrix(matrix);
    }

    /* JADX WARN: Code duplicated, block: B:102:0x0309  */
    /* JADX WARN: Code duplicated, block: B:105:0x0317  */
    /* JADX WARN: Code duplicated, block: B:106:0x0328  */
    /* JADX WARN: Code duplicated, block: B:165:0x04e0  */
    /* JADX WARN: Code duplicated, block: B:189:0x0574  */
    /* JADX WARN: Code duplicated, block: B:238:0x066d  */
    /* JADX WARN: Code duplicated, block: B:241:0x0677  */
    /* JADX WARN: Code duplicated, block: B:243:0x0681  */
    /* JADX WARN: Code duplicated, block: B:247:0x0695  */
    /* JADX WARN: Code duplicated, block: B:250:0x06f1  */
    /* JADX WARN: Code duplicated, block: B:252:0x06f5  */
    /* JADX WARN: Code duplicated, block: B:254:0x06fa  */
    /* JADX WARN: Code duplicated, block: B:269:0x0721  */
    /* JADX WARN: Code duplicated, block: B:270:0x0724  */
    /* JADX WARN: Code duplicated, block: B:273:0x0744  */
    /* JADX WARN: Code duplicated, block: B:277:0x074b  */
    /* JADX WARN: Code duplicated, block: B:281:0x077a  */
    /* JADX WARN: Code duplicated, block: B:282:0x077f  */
    /* JADX WARN: Code duplicated, block: B:284:0x0782  */
    /* JADX WARN: Code duplicated, block: B:288:0x0789  */
    /* JADX WARN: Code duplicated, block: B:294:0x07ae  */
    /* JADX WARN: Code duplicated, block: B:296:0x07b2  */
    /* JADX WARN: Code duplicated, block: B:298:0x07b6  */
    /* JADX WARN: Code duplicated, block: B:299:0x07bb  */
    /* JADX WARN: Code duplicated, block: B:301:0x07bf  */
    /* JADX WARN: Code duplicated, block: B:303:0x07c4  */
    /* JADX WARN: Code duplicated, block: B:305:0x07c8  */
    /* JADX WARN: Code duplicated, block: B:308:0x07d1  */
    /* JADX WARN: Code duplicated, block: B:309:0x07d8  */
    /* JADX WARN: Code duplicated, block: B:312:0x07e1  */
    /* JADX WARN: Code duplicated, block: B:313:0x07ea  */
    /* JADX WARN: Code duplicated, block: B:316:0x07f1  */
    /* JADX WARN: Code duplicated, block: B:318:0x07f6  */
    /* JADX WARN: Code duplicated, block: B:320:0x07fa  */
    /* JADX WARN: Code duplicated, block: B:322:0x07ff  */
    /* JADX WARN: Code duplicated, block: B:324:0x0805  */
    /* JADX WARN: Code duplicated, block: B:326:0x0809  */
    /* JADX WARN: Code duplicated, block: B:329:0x0814 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:330:0x0816  */
    /* JADX WARN: Code duplicated, block: B:334:0x0828  */
    /* JADX WARN: Code duplicated, block: B:335:0x082b  */
    /* JADX WARN: Code duplicated, block: B:338:0x0844  */
    /* JADX WARN: Code duplicated, block: B:339:0x0851  */
    /* JADX WARN: Code duplicated, block: B:342:0x0892  */
    /* JADX WARN: Code duplicated, block: B:345:0x089b  */
    /* JADX WARN: Code duplicated, block: B:347:0x089f  */
    /* JADX WARN: Code duplicated, block: B:351:0x08b0  */
    /* JADX WARN: Code duplicated, block: B:352:0x08b5  */
    /* JADX WARN: Code duplicated, block: B:355:0x08bc  */
    /* JADX WARN: Code duplicated, block: B:358:0x08c4  */
    /* JADX WARN: Code duplicated, block: B:359:0x08c7  */
    /* JADX WARN: Code duplicated, block: B:362:0x08d9  */
    /* JADX WARN: Code duplicated, block: B:365:0x090f  */
    /* JADX WARN: Code duplicated, block: B:368:0x0918  */
    /* JADX WARN: Code duplicated, block: B:370:0x091c  */
    /* JADX WARN: Code duplicated, block: B:374:0x092b  */
    /* JADX WARN: Code duplicated, block: B:375:0x092e  */
    /* JADX WARN: Code duplicated, block: B:378:0x0933  */
    /* JADX WARN: Code duplicated, block: B:381:0x0954  */
    /* JADX WARN: Code duplicated, block: B:384:0x0965  */
    /* JADX WARN: Code duplicated, block: B:386:0x0969  */
    /* JADX WARN: Code duplicated, block: B:389:0x0998  */
    /* JADX WARN: Code duplicated, block: B:392:0x09a0 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:396:0x09a8 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:397:0x09aa  */
    /* JADX WARN: Code duplicated, block: B:399:0x09ae A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:400:0x09b0  */
    /* JADX WARN: Code duplicated, block: B:402:0x09b4  */
    /* JADX WARN: Code duplicated, block: B:404:0x09ba  */
    /* JADX WARN: Code duplicated, block: B:406:0x09be  */
    /* JADX WARN: Code duplicated, block: B:408:0x09c5  */
    /* JADX WARN: Code duplicated, block: B:410:0x09c8 A[PHI: r3
  0x09c8: PHI (r3v107 int) = (r3v103 int), (r3v128 int) binds: [B:414:0x09d0, B:409:0x09c6] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:411:0x09cb A[PHI: r3
  0x09cb: PHI (r3v106 int) = (r3v103 int), (r3v128 int) binds: [B:414:0x09d0, B:409:0x09c6] A[DONT_GENERATE, DONT_INLINE]] */
    /* JADX WARN: Code duplicated, block: B:412:0x09cd  */
    /* JADX WARN: Code duplicated, block: B:413:0x09cf  */
    /* JADX WARN: Code duplicated, block: B:418:0x09d7 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:419:0x09d9 A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:432:0x0a2c  */
    /* JADX WARN: Code duplicated, block: B:433:0x0a2f  */
    /* JADX WARN: Code duplicated, block: B:436:0x0a38  */
    /* JADX WARN: Code duplicated, block: B:446:0x0a62 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:447:0x0a64  */
    /* JADX WARN: Code duplicated, block: B:451:0x0a6e  */
    /* JADX WARN: Code duplicated, block: B:452:0x0a79  */
    /* JADX WARN: Code duplicated, block: B:454:0x0a7d  */
    /* JADX WARN: Code duplicated, block: B:457:0x0a93  */
    /* JADX WARN: Code duplicated, block: B:459:0x0a96  */
    /* JADX WARN: Code duplicated, block: B:465:0x0ab9  */
    /* JADX WARN: Code duplicated, block: B:467:0x0abd  */
    /* JADX WARN: Code duplicated, block: B:471:0x0aca  */
    /* JADX WARN: Code duplicated, block: B:473:0x0ad0  */
    /* JADX WARN: Code duplicated, block: B:475:0x0ae2  */
    /* JADX WARN: Code duplicated, block: B:476:0x0ae7  */
    /* JADX WARN: Code duplicated, block: B:477:0x0ae9  */
    /* JADX WARN: Code duplicated, block: B:479:0x0af6  */
    /* JADX WARN: Code duplicated, block: B:481:0x0aff  */
    /* JADX WARN: Code duplicated, block: B:482:0x0b0c  */
    /* JADX WARN: Code duplicated, block: B:486:0x0b23  */
    /* JADX WARN: Code duplicated, block: B:488:0x0b59  */
    /* JADX WARN: Code duplicated, block: B:491:0x0b62  */
    /* JADX WARN: Code duplicated, block: B:493:0x0b7f A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:502:0x0bd7  */
    /* JADX WARN: Code duplicated, block: B:504:0x0bdf  */
    /* JADX WARN: Code duplicated, block: B:506:0x0be5  */
    /* JADX WARN: Code duplicated, block: B:507:0x0be8  */
    /* JADX WARN: Code duplicated, block: B:510:0x0bf4  */
    /* JADX WARN: Code duplicated, block: B:511:0x0bfb  */
    /* JADX WARN: Code duplicated, block: B:512:0x0c27  */
    /* JADX WARN: Code duplicated, block: B:515:0x0c2d A[ADDED_TO_REGION] */
    /* JADX WARN: Code duplicated, block: B:518:0x0c42  */
    /* JADX WARN: Code duplicated, block: B:519:0x0c45  */
    /* JADX WARN: Code duplicated, block: B:522:0x0c6f  */
    /* JADX WARN: Code duplicated, block: B:525:0x0c7a  */
    /* JADX WARN: Code duplicated, block: B:528:0x0c84  */
    /* JADX WARN: Code duplicated, block: B:530:0x0c8e  */
    /* JADX WARN: Code duplicated, block: B:531:0x0c91  */
    /* JADX WARN: Code duplicated, block: B:534:0x0cc0  */
    /* JADX WARN: Code duplicated, block: B:537:0x0cc9  */
    /* JADX WARN: Code duplicated, block: B:542:0x0cd8  */
    /* JADX WARN: Code duplicated, block: B:544:0x0cde  */
    /* JADX WARN: Code duplicated, block: B:545:0x0cfc  */
    /* JADX WARN: Code duplicated, block: B:549:0x0d10  */
    /* JADX WARN: Code duplicated, block: B:552:0x0d18  */
    /* JADX WARN: Code duplicated, block: B:562:0x0d2b  */
    /* JADX WARN: Code duplicated, block: B:564:0x0d43  */
    /* JADX WARN: Code duplicated, block: B:566:0x0d4e  */
    /* JADX WARN: Code duplicated, block: B:568:0x0d5a  */
    /* JADX WARN: Code duplicated, block: B:569:0x0d61  */
    /* JADX WARN: Code duplicated, block: B:573:0x0d73  */
    /* JADX WARN: Code duplicated, block: B:575:0x0d7b  */
    /* JADX WARN: Code duplicated, block: B:577:0x0d86  */
    /* JADX WARN: Code duplicated, block: B:581:0x0d95  */
    /* JADX WARN: Code duplicated, block: B:583:? A[RETURN, SYNTHETIC] */
    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        float f;
        int iSave;
        int i;
        float f2;
        float f3;
        float f4;
        float fDp;
        float f5;
        float fDp2;
        float fDp3;
        float fDp4;
        float f6;
        float f7;
        float f8;
        float f9;
        float f10;
        float f11;
        float f12;
        float f13;
        float f14;
        float f15;
        float f16;
        int i2;
        boolean z;
        int i3;
        float fMin;
        float fMax;
        float f17;
        float f18;
        int i4;
        Path[] pathArr;
        Path[] pathArr2;
        Path[] pathArr3;
        Path[] pathArr4;
        Drawable customIconDrawable;
        Drawable customIconDrawable2;
        int i5;
        Drawable drawable;
        Drawable drawable2;
        int i6;
        Rect rect;
        Drawable drawable3;
        int i7;
        int i8;
        float f19;
        int i9;
        int i10;
        float f20;
        int i11;
        int i12;
        int i13;
        float f21;
        float f22;
        int i14;
        int i15;
        int i16;
        float f23;
        int i17;
        float f24;
        int i18;
        float f25;
        int i19;
        boolean z2;
        int i20;
        float f26;
        int i21;
        Paint paint;
        float fMin2;
        float f27;
        int iDp;
        int iDp2;
        int i22;
        float f28;
        float f29;
        float f30;
        Canvas canvas2;
        int i23;
        int i24;
        float f31;
        float f32;
        long j;
        int i25;
        float f33;
        float f34;
        float f35;
        float f36;
        float f37;
        float f38;
        int i26;
        Path path;
        Path path2;
        Paint paint2;
        Path path3;
        int i27;
        Path path4;
        Path path5;
        int i28;
        int i29;
        int i30;
        int i31;
        float f39;
        float f40;
        int iMin;
        float f41;
        float fCenterX;
        float fDp5;
        float f42;
        int i32;
        float f43;
        float f44;
        float fCenterX2;
        int iCenterY;
        int iCenterY2;
        float f45;
        int i33;
        float f46;
        int i34;
        int i35;
        float f47;
        int i36;
        int i37;
        Canvas canvas3 = canvas;
        Rect bounds = getBounds();
        Theme.MessageDrawable messageDrawable = this.messageDrawable;
        if (messageDrawable != null && messageDrawable.hasGradient() && !this.hasOverlayImage) {
            Shader gradientShader = this.messageDrawable.getGradientShader();
            this.paint.setShader(gradientShader);
            this.paint2.setShader(gradientShader);
            this.paint3.setShader(gradientShader);
        } else if (this.gradientDrawable != null && !this.hasOverlayImage) {
            this.gradientMatrix.reset();
            this.gradientMatrix.setTranslate(0.0f, bounds.top);
            this.gradientDrawable.setLocalMatrix(this.gradientMatrix);
            this.paint.setShader(this.gradientDrawable);
            this.paint2.setShader(this.gradientDrawable);
            this.paint3.setShader(this.gradientDrawable);
        } else {
            this.paint.setShader(null);
            this.paint2.setShader(null);
            this.paint3.setShader(null);
        }
        int iCenterX = bounds.centerX();
        int iCenterY3 = bounds.centerY();
        int i38 = this.nextIcon;
        if (i38 == 4) {
            int i39 = this.currentIcon;
            if (i39 == 3 || i39 == 14) {
                f = 0.0f;
                i = 0;
            } else {
                iSave = canvas3.save();
                float f48 = 1.0f - this.transitionProgress;
                f = 0.0f;
                canvas3.scale(f48, f48, iCenterX, iCenterY3);
                i = iSave;
            }
        } else {
            f = 0.0f;
            if ((i38 == 6 || i38 == 10) && this.currentIcon == 4) {
                iSave = canvas3.save();
                float f49 = this.transitionProgress;
                canvas3.scale(f49, f49, iCenterX, iCenterY3);
                i = iSave;
            } else {
                i = 0;
            }
        }
        AndroidUtilities.dp(3.0f);
        float interpolation = 90.0f;
        if (this.currentIcon == 2 || this.nextIcon == 2) {
            applyShaderMatrix(false);
            if (this.drawProgressCircle) {
                float f50 = iCenterY3;
                float fDp6 = f50 - (AndroidUtilities.dp(9.0f) * this.scale);
                float fDp7 = (AndroidUtilities.dp(9.0f) * this.scale) + f50;
                float fDp8 = (AndroidUtilities.dp(12.0f) * this.scale) + f50;
                int i40 = this.currentIcon;
                if ((i40 == 3 || i40 == 14) && this.nextIcon == 2) {
                    this.paint.setAlpha((int) (Math.min(1.0f, this.transitionProgress / 0.5f) * 255.0f));
                    f4 = this.transitionProgress;
                    fDp = (AndroidUtilities.dp(12.0f) * this.scale) + f50;
                    f5 = 1.0f;
                } else {
                    int i41 = this.nextIcon;
                    if (i41 != 3 && i41 != 14 && i41 != 2) {
                        this.paint.setAlpha((int) (Math.min(1.0f, this.savedTransitionProgress / 0.5f) * 255.0f * (1.0f - this.transitionProgress)));
                        f16 = this.savedTransitionProgress;
                    } else {
                        this.paint.setAlpha(255);
                        f16 = this.transitionProgress;
                    }
                    f4 = f16;
                    f5 = 1.0f;
                    fDp = f50 + (AndroidUtilities.dp(1.0f) * this.scale);
                }
                if (this.animatingTransition) {
                    int i42 = this.nextIcon;
                    if (i42 == 2 || f4 <= 0.5f) {
                        if (i42 == 2) {
                            f12 = 1.0f - f4;
                        } else {
                            f12 = f4 / 0.5f;
                            f4 = 1.0f - f12;
                        }
                        fDp6 += (fDp - fDp6) * f12;
                        fDp7 += (fDp8 - fDp7) * f12;
                        float f51 = iCenterX;
                        fDp2 = f51 - ((AndroidUtilities.dp(8.0f) * f4) * this.scale);
                        fDp3 = f51 + (AndroidUtilities.dp(8.0f) * f4 * this.scale);
                        fDp4 = AndroidUtilities.dp(8.0f) * f4;
                        f6 = this.scale;
                    } else {
                        float fDp9 = AndroidUtilities.dp(13.0f);
                        float f52 = this.scale;
                        float fDp10 = (fDp9 * f52 * f52) + (this.isMini ? AndroidUtilities.dp(2.0f) : 0);
                        float f53 = f4 - 0.5f;
                        float f54 = f53 / 0.5f;
                        if (f53 > 0.2f) {
                            f14 = (f53 - 0.2f) / 0.3f;
                            f13 = f5;
                        } else {
                            f13 = f53 / 0.2f;
                            f14 = f;
                        }
                        float f55 = iCenterX;
                        float f56 = f55 - fDp10;
                        float f57 = fDp10 / 2.0f;
                        this.rect.set(f56, fDp8 - f57, f55, f57 + fDp8);
                        float f58 = f14 * 100.0f;
                        canvas3.drawArc(this.rect, f58, (f54 * 104.0f) - f58, false, this.paint);
                        float f59 = fDp + ((fDp8 - fDp) * f13);
                        if (f14 > f) {
                            float f60 = this.nextIcon == 14 ? f : (-45.0f) * (f5 - f14);
                            float fDp11 = AndroidUtilities.dp(7.0f) * f14 * this.scale;
                            int iMin2 = (int) (f14 * 255.0f);
                            int i43 = this.nextIcon;
                            if (i43 != 3 && i43 != 14 && i43 != 2) {
                                float f61 = f5;
                                iMin2 = (int) (iMin2 * (f61 - Math.min(f61, this.transitionProgress / 0.5f)));
                            }
                            int i44 = iMin2;
                            if (f60 != f) {
                                canvas3.save();
                                f15 = f50;
                                canvas3.rotate(f60, f55, f15);
                            } else {
                                f15 = f50;
                            }
                            if (i44 != 0) {
                                this.paint.setAlpha(i44);
                                if (this.nextIcon == 14) {
                                    this.paint3.setAlpha(i44);
                                    this.rect.set(iCenterX - AndroidUtilities.dp(3.5f), iCenterY3 - AndroidUtilities.dp(3.5f), AndroidUtilities.dp(3.5f) + iCenterX, AndroidUtilities.dp(3.5f) + iCenterY3);
                                    canvas3.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), this.paint3);
                                    this.paint.setAlpha((int) (i44 * 0.15f));
                                    int iDp3 = AndroidUtilities.dp(this.isMini ? 2.0f : 4.0f);
                                    this.rect.set(bounds.left + iDp3, bounds.top + iDp3, bounds.right - iDp3, bounds.bottom - iDp3);
                                    canvas3.drawArc(this.rect, 0.0f, 360.0f, false, this.paint);
                                    this.paint.setAlpha(i44);
                                } else {
                                    float f62 = f55 - fDp11;
                                    float f63 = f15 - fDp11;
                                    float f64 = f55 + fDp11;
                                    float f65 = f15 + fDp11;
                                    canvas.drawLine(f62, f63, f64, f65, this.paint);
                                    canvas.drawLine(f64, f63, f62, f65, this.paint);
                                }
                            }
                            if (f60 != f) {
                                canvas.restore();
                            }
                        }
                        fDp7 = fDp8;
                        fDp3 = f55;
                        fDp2 = fDp3;
                        fDp6 = f59;
                    }
                    f7 = fDp3;
                    f8 = fDp7;
                    f9 = fDp8;
                    f10 = fDp2;
                    if (fDp6 != f8) {
                        float f66 = iCenterX;
                        canvas.drawLine(f66, fDp6, f66, f8, this.paint);
                    }
                    f11 = iCenterX;
                    if (f10 != f11) {
                        canvas3 = canvas;
                        canvas3.drawLine(f10, f9, f11, f8, this.paint);
                        canvas3.drawLine(f7, f9, f11, f8, this.paint);
                    } else {
                        canvas3 = canvas;
                    }
                } else {
                    float f67 = iCenterX;
                    fDp2 = f67 - (AndroidUtilities.dp(8.0f) * this.scale);
                    fDp3 = f67 + (AndroidUtilities.dp(8.0f) * this.scale);
                    fDp4 = AndroidUtilities.dp(8.0f);
                    f6 = this.scale;
                }
                fDp8 = fDp7 - (fDp4 * f6);
                f7 = fDp3;
                f8 = fDp7;
                f9 = fDp8;
                f10 = fDp2;
                if (fDp6 != f8) {
                    float f68 = iCenterX;
                    canvas.drawLine(f68, fDp6, f68, f8, this.paint);
                }
                f11 = iCenterX;
                if (f10 != f11) {
                    canvas3 = canvas;
                    canvas3.drawLine(f10, f9, f11, f8, this.paint);
                    canvas3.drawLine(f7, f9, f11, f8, this.paint);
                } else {
                    canvas3 = canvas;
                }
            } else {
                boolean z3 = this.currentIcon == 14 || this.nextIcon == 14;
                int i45 = this.nextIcon;
                float f69 = this.transitionProgress;
                float f70 = i45 == 2 ? 1.0f - f69 : f69;
                float f71 = this.scale * this.downloadIconScale;
                float fDp12 = AndroidUtilities.dp(8.0f) * f71;
                float fDp13 = AndroidUtilities.dp(1.0f) * f71;
                float fDp14 = AndroidUtilities.dp(7.0f) * f71;
                float fDp15 = AndroidUtilities.dp(9.0f) * f71;
                float fDp16 = AndroidUtilities.dp(2.0f) * f71;
                float fDp17 = AndroidUtilities.dp(3.5f);
                if (z3) {
                    float f72 = 1.0f - f70;
                    if (f72 > f) {
                        canvas3.save();
                        float f73 = iCenterX;
                        float f74 = iCenterY3;
                        canvas3.scale(f72, f72, f73, f74);
                        this.paint.setAlpha((int) (f72 * 255.0f * this.overrideAlpha));
                        float f75 = f74 + fDp15;
                        float f76 = f75 - fDp12;
                        canvas3.drawLine(f73, f74 - fDp15, f73, f75, this.paint);
                        canvas.drawLine(f73 - fDp12, f76, f73, f75, this.paint);
                        canvas3 = canvas;
                        canvas3.drawLine(f73 + fDp12, f76, f73, f75, this.paint);
                        canvas3.restore();
                    }
                    if (f70 > f) {
                        canvas3.save();
                        float f77 = iCenterX;
                        float f78 = iCenterY3;
                        canvas3.scale(f70, f70, f77, f78);
                        this.paint3.setAlpha((int) (f70 * 255.0f * this.overrideAlpha));
                        this.rect.set(f77 - fDp17, f78 - fDp17, f77 + fDp17, f78 + fDp17);
                        canvas3.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), this.paint3);
                        canvas3.restore();
                    }
                } else {
                    int i46 = this.currentIcon;
                    if ((i46 == 3 || this.nextIcon == 3) && this.animatingTransition) {
                        canvas3.save();
                        float f79 = iCenterX;
                        float f80 = iCenterY3;
                        canvas3.rotate(f70 * 90.0f, f79, f80);
                        float f81 = 1.0f - f70;
                        if (f81 > f) {
                            this.paint.setAlpha((int) (f81 * 255.0f));
                            float f82 = f81 * fDp15;
                            canvas3.drawLine(f79, f80 - f82, f79, f82 + f80, this.paint);
                            f2 = f79;
                        } else {
                            f2 = f79;
                        }
                        this.paint.setAlpha(255);
                        float f83 = (f80 + fDp13) - (fDp12 * f70);
                        float f84 = (f80 + fDp15) - (fDp16 * f70);
                        float f85 = fDp13 * f70;
                        float f86 = fDp14 * f70;
                        canvas.drawLine((f2 - fDp12) + f85, f83, f2 + f86, f84, this.paint);
                        canvas3 = canvas;
                        canvas3.drawLine((f2 + fDp12) - f85, f83, f2 - f86, f84, this.paint);
                        canvas3.restore();
                    } else {
                        if (this.animatingTransition) {
                            f3 = f70;
                        } else {
                            f3 = i46 == 2 ? f : 1.0f;
                        }
                        float f87 = 1.0f - f3;
                        int i47 = (int) (f87 * 255.0f);
                        if (i47 > 0) {
                            canvas3.save();
                            float f88 = iCenterX;
                            float f89 = iCenterY3;
                            canvas3.scale(f87, f87, f88, f89);
                            this.paint.setAlpha(i47);
                            float f90 = f89 + fDp15;
                            float f91 = f90 - fDp12;
                            canvas3.drawLine(f88, f89 - fDp15, f88, f90, this.paint);
                            canvas.drawLine(f88 - fDp12, f91, f88, f90, this.paint);
                            canvas3 = canvas;
                            canvas3.drawLine(f88 + fDp12, f91, f88, f90, this.paint);
                            canvas3.restore();
                        }
                    }
                }
            }
        }
        int i48 = this.currentIcon;
        float f92 = 360.0f;
        if (i48 != 3 && i48 != 14) {
            if (i48 == 4 && ((i37 = this.nextIcon) == 14 || i37 == 3)) {
                z = false;
                i2 = 15;
            } else if (i48 == 10 || this.nextIcon == 10 || i48 == 13) {
                int i49 = this.nextIcon;
                int i50 = (i49 == 4 || i49 == 6) ? (int) ((1.0f - this.transitionProgress) * 255.0f) : 255;
                if (i50 != 0) {
                    applyShaderMatrix(false);
                    this.paint.setAlpha((int) (i50 * this.overrideAlpha));
                    if (this.drawProgressCircle) {
                        float fMax2 = Math.max(4.0f, this.animatedDownloadProgress * 360.0f);
                        int iDp4 = AndroidUtilities.dp(this.isMini ? 2.0f : 4.0f);
                        this.rect.set(bounds.left + iDp4, bounds.top + iDp4, bounds.right - iDp4, bounds.bottom - iDp4);
                        i2 = 15;
                        canvas3.drawArc(this.rect, this.downloadRadOffset, fMax2, false, this.paint);
                    } else {
                        i2 = 15;
                    }
                } else {
                    i2 = 15;
                }
            } else {
                i2 = 15;
            }
            i3 = this.currentIcon;
            if (i3 == this.nextIcon) {
                f18 = 1.0f;
                f17 = 1.0f;
            } else {
                if (i3 != 4 || i3 == 3 || i3 == 14) {
                    fMin = this.transitionProgress;
                    fMax = 1.0f - fMin;
                } else {
                    fMin = Math.min(1.0f, this.transitionProgress / 0.5f);
                    fMax = Math.max(f, 1.0f - (this.transitionProgress / 0.5f));
                }
                f17 = fMax;
                f18 = fMin;
            }
            i4 = this.nextIcon;
            if (i4 == i2) {
                pathArr = Theme.chat_updatePath;
            } else {
                if (this.currentIcon == i2) {
                    pathArr2 = Theme.chat_updatePath;
                    pathArr = null;
                } else {
                    pathArr = null;
                }
                if (i4 == 5) {
                    pathArr = Theme.chat_filePath;
                } else if (this.currentIcon == 5) {
                    pathArr2 = Theme.chat_filePath;
                }
                pathArr3 = pathArr;
                pathArr4 = pathArr2;
                if (isCustomFileIcon(i4)) {
                    customIconDrawable = getCustomIconDrawable(this.nextIcon);
                } else {
                    customIconDrawable = null;
                }
                if (isCustomFileIcon(this.currentIcon)) {
                    customIconDrawable2 = getCustomIconDrawable(this.currentIcon);
                } else {
                    customIconDrawable2 = null;
                }
                i5 = this.nextIcon;
                if (i5 == 7) {
                    customIconDrawable = Theme.chat_flameIcon;
                } else if (this.currentIcon == 7) {
                    customIconDrawable2 = Theme.chat_flameIcon;
                }
                if (i5 == 8) {
                    customIconDrawable = Theme.chat_gifIcon;
                } else if (this.currentIcon == 8) {
                    customIconDrawable2 = Theme.chat_gifIcon;
                }
                drawable = customIconDrawable;
                drawable2 = customIconDrawable2;
                if (this.currentIcon != 9 || i5 == 9) {
                    applyShaderMatrix(false);
                    Paint paint3 = this.paint;
                    if (this.currentIcon == this.nextIcon) {
                        i6 = 255;
                    } else {
                        i6 = (int) (this.transitionProgress * 255.0f);
                    }
                    paint3.setAlpha(i6);
                    int iDp5 = AndroidUtilities.dp(7.0f) + iCenterY3;
                    int iDp6 = iCenterX - AndroidUtilities.dp(3.0f);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.save();
                        float f93 = this.transitionProgress;
                        canvas3.scale(f93, f93, iCenterX, iCenterY3);
                    }
                    float f94 = iDp6;
                    float f95 = iDp5;
                    rect = bounds;
                    drawable3 = drawable2;
                    canvas3.drawLine(iDp6 - AndroidUtilities.dp(6.0f), iDp5 - AndroidUtilities.dp(6.0f), f94, f95, this.paint);
                    canvas3 = canvas;
                    canvas3.drawLine(f94, f95, iDp6 + AndroidUtilities.dp(12.0f), iDp5 - AndroidUtilities.dp(12.0f), this.paint);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.restore();
                    }
                } else {
                    rect = bounds;
                    drawable3 = drawable2;
                }
                if (this.currentIcon != 12 || this.nextIcon == 12) {
                    applyShaderMatrix(false);
                    i7 = this.currentIcon;
                    i8 = this.nextIcon;
                    if (i7 == i8) {
                        i9 = 13;
                        f19 = 1.0f;
                    } else {
                        f19 = this.transitionProgress;
                        i9 = 13;
                        if (i8 != 13) {
                            f19 = 1.0f - f19;
                        }
                    }
                    Paint paint4 = this.paint;
                    if (i7 == i8) {
                        i10 = 255;
                    } else {
                        i10 = (int) (f19 * 255.0f);
                    }
                    paint4.setAlpha(i10);
                    AndroidUtilities.dp(7.0f);
                    AndroidUtilities.dp(3.0f);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.save();
                        canvas3.scale(f19, f19, iCenterX, iCenterY3);
                    }
                    float fDp18 = AndroidUtilities.dp(7.0f) * this.scale;
                    float f96 = iCenterX;
                    float f97 = f96 - fDp18;
                    float f98 = iCenterY3;
                    float f99 = f98 - fDp18;
                    float f100 = f96 + fDp18;
                    float f101 = fDp18 + f98;
                    f20 = f17;
                    i11 = i9;
                    canvas3.drawLine(f97, f99, f100, f101, this.paint);
                    canvas3 = canvas;
                    canvas3.drawLine(f100, f99, f97, f101, this.paint);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.restore();
                    }
                } else {
                    f20 = f17;
                    i11 = 13;
                }
                if (this.currentIcon != i11 || this.nextIcon == i11) {
                    applyShaderMatrix(false);
                    i12 = this.currentIcon;
                    i13 = this.nextIcon;
                    if (i12 == i13) {
                        f21 = 1.0f;
                    } else {
                        f21 = this.transitionProgress;
                        if (i13 != i11) {
                            f21 = 1.0f - f21;
                        }
                    }
                    this.textPaint.setAlpha((int) (f21 * 255.0f));
                    int iDp7 = AndroidUtilities.dp(5.0f) + iCenterY3;
                    int i51 = iCenterX - (this.percentStringWidth / 2);
                    f22 = 5.0f;
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.save();
                        canvas3.scale(f21, f21, iCenterX, iCenterY3);
                    }
                    i14 = (int) (this.animatedDownloadProgress * 100.0f);
                    if (this.percentString != null || i14 != this.lastPercent) {
                        this.lastPercent = i14;
                        String str = String.format("%d%%", Integer.valueOf(i14));
                        this.percentString = str;
                        this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str));
                    }
                    canvas3.drawText(this.percentString, i51, iDp7, this.textPaint);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.restore();
                    }
                } else {
                    f22 = 5.0f;
                }
                i15 = this.currentIcon;
                if (i15 != 0 || i15 == 1 || (i31 = this.nextIcon) == 0 || i31 == 1) {
                    if (i15 == 0 || this.nextIcon != 1) {
                        if (i15 != 1) {
                            i16 = 1;
                        } else if (this.nextIcon == 0) {
                            z2 = this.animatingTransition;
                            i20 = this.nextIcon;
                            if (z2) {
                                f26 = this.transitionProgress;
                                if (i20 == 0) {
                                    f26 = 1.0f - f26;
                                }
                                f23 = f26;
                                i16 = 1;
                            } else {
                                i16 = 1;
                                if (i20 == 1) {
                                    f23 = 1.0f;
                                } else {
                                    f23 = 0.0f;
                                }
                            }
                        } else {
                            i16 = 1;
                        }
                        if (i15 == i16) {
                            f23 = 1.0f;
                        } else {
                            f23 = 0.0f;
                        }
                    } else {
                        z2 = this.animatingTransition;
                        i20 = this.nextIcon;
                        if (z2) {
                            f26 = this.transitionProgress;
                            if (i20 == 0) {
                                f26 = 1.0f - f26;
                            }
                            f23 = f26;
                            i16 = 1;
                        } else {
                            i16 = 1;
                            if (i20 == 1) {
                                f23 = 1.0f;
                            } else {
                                f23 = 0.0f;
                            }
                        }
                    }
                    i17 = this.nextIcon;
                    if ((i17 == 0 && i17 != i16) || (i15 != 0 && i15 != i16)) {
                        Paint paint5 = this.paint2;
                        if (i17 == 4) {
                            paint5.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f));
                        } else {
                            paint5.setAlpha(i15 == i17 ? 255 : (int) (this.transitionProgress * 255.0f));
                        }
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0 || this.nextIcon != 1) {
                        if (i18 == 1 && this.nextIcon == 0) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                        }
                        canvas3.rotate(f25);
                        i19 = this.currentIcon;
                        if ((i19 == 0 && i19 != 1) || i19 == 4) {
                            canvas3.scale(f18, f18);
                        }
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.scale(1.0f, -1.0f);
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.restore();
                    } else {
                        if (f24 < 384.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 384.0f) * 95.0f;
                        } else if (f24 < 484.0f) {
                            interpolation = 95.0f - (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 384.0f) / 100.0f) * f22);
                        }
                        f24 += 100.0f;
                    }
                    f25 = interpolation;
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                }
                if (this.currentIcon != 6 || this.nextIcon == 6) {
                    applyShaderMatrix(false);
                    if (this.currentIcon != 6) {
                        f28 = this.transitionProgress;
                        if (f28 > 0.5f) {
                            f30 = (f28 - 0.5f) / 0.5f;
                            fMin2 = 1.0f - Math.min(1.0f, f30 / 0.5f);
                            if (f30 > 0.5f) {
                                f29 = (f30 - 0.5f) / 0.5f;
                            } else {
                                f29 = 0.0f;
                            }
                        } else {
                            f29 = 0.0f;
                            fMin2 = 1.0f;
                        }
                        this.paint.setAlpha(255);
                        f27 = f29;
                    } else {
                        i21 = this.nextIcon;
                        paint = this.paint;
                        if (i21 != 6) {
                            paint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f));
                        } else {
                            paint.setAlpha(255);
                        }
                        fMin2 = 0.0f;
                        f27 = 1.0f;
                    }
                    iDp = AndroidUtilities.dp(7.0f) + iCenterY3;
                    iDp2 = iCenterX - AndroidUtilities.dp(3.0f);
                    if (fMin2 < 1.0f) {
                        i22 = iDp2;
                        canvas.drawLine(iDp2 - AndroidUtilities.dp(6.0f), iDp - AndroidUtilities.dp(6.0f), iDp2 - (AndroidUtilities.dp(6.0f) * fMin2), iDp - (AndroidUtilities.dp(6.0f) * fMin2), this.paint);
                    } else {
                        i22 = iDp2;
                    }
                    if (f27 > 0.0f) {
                        float f102 = i22;
                        float f103 = iDp;
                        canvas.drawLine(f102, f103, f102 + (AndroidUtilities.dp(12.0f) * f27), f103 - (AndroidUtilities.dp(12.0f) * f27), this.paint);
                    }
                } else {
                    f18 = f18;
                }
                if (drawable3 != null || drawable3 == drawable) {
                    canvas2 = canvas;
                    i23 = iCenterX;
                    i24 = iCenterY3;
                    f31 = f20;
                } else {
                    int i52 = this.currentIcon;
                    int i53 = i52 == this.nextIcon ? 255 : (int) ((1.0f - this.transitionProgress) * 255.0f);
                    if (isCustomFileIcon(i52)) {
                        canvas2 = canvas;
                        i23 = iCenterX;
                        i24 = iCenterY3;
                        float f104 = f20;
                        drawTintedDrawable(canvas2, drawable3, i23, i24, f104, i53);
                        f31 = f104;
                    } else {
                        canvas2 = canvas;
                        Drawable drawable4 = drawable3;
                        i23 = iCenterX;
                        i24 = iCenterY3;
                        f31 = f20;
                        int intrinsicWidth = (int) (drawable4.getIntrinsicWidth() * f31);
                        int intrinsicHeight = (int) (drawable4.getIntrinsicHeight() * f31);
                        drawable4.setColorFilter(this.colorFilter);
                        drawable4.setAlpha(i53);
                        int i54 = intrinsicWidth / 2;
                        int i55 = intrinsicHeight / 2;
                        drawable4.setBounds(i23 - i54, i24 - i55, i54 + i23, i24 + i55);
                        drawable4.draw(canvas2);
                    }
                }
                if (drawable != null) {
                    i28 = this.currentIcon;
                    i29 = this.nextIcon;
                    if (i28 == i29) {
                        i30 = 255;
                    } else {
                        i30 = (int) (this.transitionProgress * 255.0f);
                    }
                    if (isCustomFileIcon(i29)) {
                        f32 = f18;
                        drawTintedDrawable(canvas2, drawable, i23, i24, f32, i30);
                    } else {
                        f32 = f18;
                        int intrinsicWidth2 = (int) (drawable.getIntrinsicWidth() * f32);
                        int intrinsicHeight2 = (int) (drawable.getIntrinsicHeight() * f32);
                        drawable.setColorFilter(this.colorFilter);
                        drawable.setAlpha(i30);
                        int i56 = intrinsicWidth2 / 2;
                        int i57 = intrinsicHeight2 / 2;
                        drawable.setBounds(i23 - i56, i24 - i57, i56 + i23, i57 + i24);
                        drawable.draw(canvas2);
                    }
                } else {
                    f32 = f18;
                }
                if (pathArr4 != null && pathArr4 != pathArr3) {
                    int iDp8 = AndroidUtilities.dp(24.0f);
                    this.paint2.setStyle(Paint.Style.FILL_AND_STROKE);
                    Paint paint6 = this.paint2;
                    if (this.currentIcon == this.nextIcon) {
                        i27 = 255;
                    } else {
                        i27 = (int) ((1.0f - this.transitionProgress) * 255.0f);
                    }
                    paint6.setAlpha(i27);
                    applyShaderMatrix(true);
                    canvas2.save();
                    canvas2.translate(i23, i24);
                    canvas2.scale(f31, f31);
                    float f105 = (-iDp8) / 2;
                    canvas2.translate(f105, f105);
                    path4 = pathArr4[0];
                    if (path4 != null) {
                        canvas2.drawPath(path4, this.paint2);
                    }
                    path5 = pathArr4[1];
                    if (path5 != null) {
                        canvas2.drawPath(path5, this.backPaint);
                    }
                    canvas2.restore();
                }
                if (pathArr3 != null) {
                    int iDp9 = AndroidUtilities.dp(24.0f);
                    if (this.currentIcon == this.nextIcon) {
                        i26 = 255;
                    } else {
                        i26 = (int) (this.transitionProgress * 255.0f);
                    }
                    this.paint2.setStyle(Paint.Style.FILL_AND_STROKE);
                    this.paint2.setAlpha(i26);
                    applyShaderMatrix(true);
                    canvas2.save();
                    canvas2.translate(i23, i24);
                    canvas2.scale(f32, f32);
                    float f106 = (-iDp9) / 2;
                    canvas2.translate(f106, f106);
                    path = pathArr3[0];
                    if (path != null) {
                        canvas2.drawPath(path, this.paint2);
                    }
                    if (pathArr3.length >= 3 && (path3 = pathArr3[2]) != null) {
                        canvas2.drawPath(path3, this.paint);
                    }
                    path2 = pathArr3[1];
                    if (path2 != null) {
                        paint2 = this.backPaint;
                        if (i26 != 255) {
                            int alpha = paint2.getAlpha();
                            this.backPaint.setAlpha((int) (alpha * (i26 / 255.0f)));
                            canvas2.drawPath(pathArr3[1], this.backPaint);
                            this.backPaint.setAlpha(alpha);
                        } else {
                            canvas2.drawPath(path2, paint2);
                        }
                    }
                    canvas2.restore();
                }
                long jCurrentTimeMillis = System.currentTimeMillis();
                j = jCurrentTimeMillis - this.lastAnimationTime;
                if (j > 17) {
                    j = 17;
                }
                this.lastAnimationTime = jCurrentTimeMillis;
                i25 = this.currentIcon;
                if (i25 != 3 || i25 == 14 || ((i25 == 4 && this.nextIcon == 14) || i25 == 10 || i25 == 13)) {
                    float f107 = this.downloadRadOffset + ((360 * j) / 2500.0f);
                    this.downloadRadOffset = f107;
                    this.downloadRadOffset = getCircleValue(f107);
                    if (this.nextIcon != 2) {
                        f33 = this.downloadProgress;
                        f34 = this.downloadProgressAnimationStart;
                        f35 = f33 - f34;
                        if (f35 > 0.0f) {
                            f36 = this.downloadProgressTime + j;
                            this.downloadProgressTime = f36;
                            if (f36 >= 200.0f) {
                                this.animatedDownloadProgress = f33;
                                this.downloadProgressAnimationStart = f33;
                                this.downloadProgressTime = 0.0f;
                            } else {
                                this.animatedDownloadProgress = f34 + (f35 * this.interpolator.getInterpolation(f36 / 200.0f));
                            }
                        }
                    }
                    invalidateSelf();
                }
                if (this.animatingTransition) {
                    f37 = this.transitionProgress;
                    if (f37 < 1.0f) {
                        f38 = f37 + (j / this.transitionAnimationTime);
                        this.transitionProgress = f38;
                        if (f38 >= 1.0f) {
                            this.currentIcon = this.nextIcon;
                            this.transitionProgress = 1.0f;
                            this.animatingTransition = false;
                        }
                        invalidateSelf();
                    }
                }
                if (i >= 1) {
                    canvas2.restoreToCount(i);
                }
            }
            pathArr2 = null;
            if (i4 == 5) {
                pathArr = Theme.chat_filePath;
            } else if (this.currentIcon == 5) {
                pathArr2 = Theme.chat_filePath;
            }
            pathArr3 = pathArr;
            pathArr4 = pathArr2;
            if (isCustomFileIcon(i4)) {
                customIconDrawable = getCustomIconDrawable(this.nextIcon);
            } else {
                customIconDrawable = null;
            }
            if (isCustomFileIcon(this.currentIcon)) {
                customIconDrawable2 = getCustomIconDrawable(this.currentIcon);
            } else {
                customIconDrawable2 = null;
            }
            i5 = this.nextIcon;
            if (i5 == 7) {
                customIconDrawable = Theme.chat_flameIcon;
            } else if (this.currentIcon == 7) {
                customIconDrawable2 = Theme.chat_flameIcon;
            }
            if (i5 == 8) {
                customIconDrawable = Theme.chat_gifIcon;
            } else if (this.currentIcon == 8) {
                customIconDrawable2 = Theme.chat_gifIcon;
            }
            drawable = customIconDrawable;
            drawable2 = customIconDrawable2;
            if (this.currentIcon != 9) {
                applyShaderMatrix(false);
                Paint paint7 = this.paint;
                if (this.currentIcon == this.nextIcon) {
                    i6 = 255;
                } else {
                    i6 = (int) (this.transitionProgress * 255.0f);
                }
                paint7.setAlpha(i6);
                int iDp10 = AndroidUtilities.dp(7.0f) + iCenterY3;
                int iDp11 = iCenterX - AndroidUtilities.dp(3.0f);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    float f910 = this.transitionProgress;
                    canvas3.scale(f910, f910, iCenterX, iCenterY3);
                }
                float f911 = iDp11;
                float f912 = iDp10;
                rect = bounds;
                drawable3 = drawable2;
                canvas3.drawLine(iDp11 - AndroidUtilities.dp(6.0f), iDp10 - AndroidUtilities.dp(6.0f), f911, f912, this.paint);
                canvas3 = canvas;
                canvas3.drawLine(f911, f912, iDp11 + AndroidUtilities.dp(12.0f), iDp10 - AndroidUtilities.dp(12.0f), this.paint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            } else {
                applyShaderMatrix(false);
                Paint paint8 = this.paint;
                if (this.currentIcon == this.nextIcon) {
                    i6 = 255;
                } else {
                    i6 = (int) (this.transitionProgress * 255.0f);
                }
                paint8.setAlpha(i6);
                int iDp12 = AndroidUtilities.dp(7.0f) + iCenterY3;
                int iDp13 = iCenterX - AndroidUtilities.dp(3.0f);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    float f913 = this.transitionProgress;
                    canvas3.scale(f913, f913, iCenterX, iCenterY3);
                }
                float f914 = iDp13;
                float f915 = iDp12;
                rect = bounds;
                drawable3 = drawable2;
                canvas3.drawLine(iDp13 - AndroidUtilities.dp(6.0f), iDp12 - AndroidUtilities.dp(6.0f), f914, f915, this.paint);
                canvas3 = canvas;
                canvas3.drawLine(f914, f915, iDp13 + AndroidUtilities.dp(12.0f), iDp12 - AndroidUtilities.dp(12.0f), this.paint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            }
            if (this.currentIcon != 12) {
                applyShaderMatrix(false);
                i7 = this.currentIcon;
                i8 = this.nextIcon;
                if (i7 == i8) {
                    i9 = 13;
                    f19 = 1.0f;
                } else {
                    f19 = this.transitionProgress;
                    i9 = 13;
                    if (i8 != 13) {
                        f19 = 1.0f - f19;
                    }
                }
                Paint paint9 = this.paint;
                if (i7 == i8) {
                    i10 = 255;
                } else {
                    i10 = (int) (f19 * 255.0f);
                }
                paint9.setAlpha(i10);
                AndroidUtilities.dp(7.0f);
                AndroidUtilities.dp(3.0f);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    canvas3.scale(f19, f19, iCenterX, iCenterY3);
                }
                float fDp19 = AndroidUtilities.dp(7.0f) * this.scale;
                float f916 = iCenterX;
                float f917 = f916 - fDp19;
                float f918 = iCenterY3;
                float f919 = f918 - fDp19;
                float f108 = f916 + fDp19;
                float f109 = fDp19 + f918;
                f20 = f17;
                i11 = i9;
                canvas3.drawLine(f917, f919, f108, f109, this.paint);
                canvas3 = canvas;
                canvas3.drawLine(f108, f919, f917, f109, this.paint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            } else {
                applyShaderMatrix(false);
                i7 = this.currentIcon;
                i8 = this.nextIcon;
                if (i7 == i8) {
                    i9 = 13;
                    f19 = 1.0f;
                } else {
                    f19 = this.transitionProgress;
                    i9 = 13;
                    if (i8 != 13) {
                        f19 = 1.0f - f19;
                    }
                }
                Paint paint10 = this.paint;
                if (i7 == i8) {
                    i10 = 255;
                } else {
                    i10 = (int) (f19 * 255.0f);
                }
                paint10.setAlpha(i10);
                AndroidUtilities.dp(7.0f);
                AndroidUtilities.dp(3.0f);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    canvas3.scale(f19, f19, iCenterX, iCenterY3);
                }
                float fDp110 = AndroidUtilities.dp(7.0f) * this.scale;
                float f9110 = iCenterX;
                float f9111 = f9110 - fDp110;
                float f9112 = iCenterY3;
                float f9113 = f9112 - fDp110;
                float f1010 = f9110 + fDp110;
                float f1011 = fDp110 + f9112;
                f20 = f17;
                i11 = i9;
                canvas3.drawLine(f9111, f9113, f1010, f1011, this.paint);
                canvas3 = canvas;
                canvas3.drawLine(f1010, f9113, f9111, f1011, this.paint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            }
            if (this.currentIcon != i11) {
                applyShaderMatrix(false);
                i12 = this.currentIcon;
                i13 = this.nextIcon;
                if (i12 == i13) {
                    f21 = 1.0f;
                } else {
                    f21 = this.transitionProgress;
                    if (i13 != i11) {
                        f21 = 1.0f - f21;
                    }
                }
                this.textPaint.setAlpha((int) (f21 * 255.0f));
                int iDp14 = AndroidUtilities.dp(5.0f) + iCenterY3;
                int i58 = iCenterX - (this.percentStringWidth / 2);
                f22 = 5.0f;
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    canvas3.scale(f21, f21, iCenterX, iCenterY3);
                }
                i14 = (int) (this.animatedDownloadProgress * 100.0f);
                if (this.percentString != null) {
                    this.lastPercent = i14;
                    String str2 = String.format("%d%%", Integer.valueOf(i14));
                    this.percentString = str2;
                    this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str2));
                } else {
                    this.lastPercent = i14;
                    String str3 = String.format("%d%%", Integer.valueOf(i14));
                    this.percentString = str3;
                    this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str3));
                }
                canvas3.drawText(this.percentString, i58, iDp14, this.textPaint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            } else {
                applyShaderMatrix(false);
                i12 = this.currentIcon;
                i13 = this.nextIcon;
                if (i12 == i13) {
                    f21 = 1.0f;
                } else {
                    f21 = this.transitionProgress;
                    if (i13 != i11) {
                        f21 = 1.0f - f21;
                    }
                }
                this.textPaint.setAlpha((int) (f21 * 255.0f));
                int iDp15 = AndroidUtilities.dp(5.0f) + iCenterY3;
                int i59 = iCenterX - (this.percentStringWidth / 2);
                f22 = 5.0f;
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    canvas3.scale(f21, f21, iCenterX, iCenterY3);
                }
                i14 = (int) (this.animatedDownloadProgress * 100.0f);
                if (this.percentString != null) {
                    this.lastPercent = i14;
                    String str4 = String.format("%d%%", Integer.valueOf(i14));
                    this.percentString = str4;
                    this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str4));
                } else {
                    this.lastPercent = i14;
                    String str5 = String.format("%d%%", Integer.valueOf(i14));
                    this.percentString = str5;
                    this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str5));
                }
                canvas3.drawText(this.percentString, i59, iDp15, this.textPaint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            }
            i15 = this.currentIcon;
            if (i15 != 0) {
                if (i15 == 0) {
                    if (i15 != 1) {
                        if (this.nextIcon == 0) {
                            z2 = this.animatingTransition;
                            i20 = this.nextIcon;
                            if (z2) {
                                f26 = this.transitionProgress;
                                if (i20 == 0) {
                                    f26 = 1.0f - f26;
                                }
                                f23 = f26;
                                i16 = 1;
                            } else {
                                i16 = 1;
                                if (i20 == 1) {
                                    f23 = 1.0f;
                                } else {
                                    f23 = 0.0f;
                                }
                            }
                        } else {
                            i16 = 1;
                        }
                        i17 = this.nextIcon;
                        if (i17 == 0) {
                            this.paint2.setAlpha(255);
                        } else {
                            this.paint2.setAlpha(255);
                        }
                        applyShaderMatrix(true);
                        canvas3.save();
                        canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                        f24 = f23 * 500.0f;
                        i18 = this.currentIcon;
                        if (i18 == 1) {
                            f25 = 90.0f;
                        } else {
                            f25 = 0.0f;
                        }
                        if (i18 == 0) {
                            if (i18 == 1) {
                                if (f24 < 100.0f) {
                                    interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                                } else if (f24 < 484.0f) {
                                    interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                                }
                                f25 = interpolation;
                            }
                        } else if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                        canvas3.rotate(f25);
                        i19 = this.currentIcon;
                        if (i19 == 0) {
                            canvas3.scale(f18, f18);
                        } else {
                            canvas3.scale(f18, f18);
                        }
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.scale(1.0f, -1.0f);
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.restore();
                    } else {
                        i16 = 1;
                    }
                    if (i15 == i16) {
                        f23 = 1.0f;
                    } else {
                        f23 = 0.0f;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                } else {
                    if (i15 != 1) {
                        if (this.nextIcon == 0) {
                            z2 = this.animatingTransition;
                            i20 = this.nextIcon;
                            if (z2) {
                                f26 = this.transitionProgress;
                                if (i20 == 0) {
                                    f26 = 1.0f - f26;
                                }
                                f23 = f26;
                                i16 = 1;
                            } else {
                                i16 = 1;
                                if (i20 == 1) {
                                    f23 = 1.0f;
                                } else {
                                    f23 = 0.0f;
                                }
                            }
                        } else {
                            i16 = 1;
                        }
                        i17 = this.nextIcon;
                        if (i17 == 0) {
                            this.paint2.setAlpha(255);
                        } else {
                            this.paint2.setAlpha(255);
                        }
                        applyShaderMatrix(true);
                        canvas3.save();
                        canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                        f24 = f23 * 500.0f;
                        i18 = this.currentIcon;
                        if (i18 == 1) {
                            f25 = 90.0f;
                        } else {
                            f25 = 0.0f;
                        }
                        if (i18 == 0) {
                            if (i18 == 1) {
                                if (f24 < 100.0f) {
                                    interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                                } else if (f24 < 484.0f) {
                                    interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                                }
                                f25 = interpolation;
                            }
                        } else if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                        canvas3.rotate(f25);
                        i19 = this.currentIcon;
                        if (i19 == 0) {
                            canvas3.scale(f18, f18);
                        } else {
                            canvas3.scale(f18, f18);
                        }
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.scale(1.0f, -1.0f);
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.restore();
                    } else {
                        i16 = 1;
                    }
                    if (i15 == i16) {
                        f23 = 1.0f;
                    } else {
                        f23 = 0.0f;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                }
            } else if (i15 == 0) {
                if (i15 != 1) {
                    if (this.nextIcon == 0) {
                        z2 = this.animatingTransition;
                        i20 = this.nextIcon;
                        if (z2) {
                            f26 = this.transitionProgress;
                            if (i20 == 0) {
                                f26 = 1.0f - f26;
                            }
                            f23 = f26;
                            i16 = 1;
                        } else {
                            i16 = 1;
                            if (i20 == 1) {
                                f23 = 1.0f;
                            } else {
                                f23 = 0.0f;
                            }
                        }
                    } else {
                        i16 = 1;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                } else {
                    i16 = 1;
                }
                if (i15 == i16) {
                    f23 = 1.0f;
                } else {
                    f23 = 0.0f;
                }
                i17 = this.nextIcon;
                if (i17 == 0) {
                    this.paint2.setAlpha(255);
                } else {
                    this.paint2.setAlpha(255);
                }
                applyShaderMatrix(true);
                canvas3.save();
                canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                f24 = f23 * 500.0f;
                i18 = this.currentIcon;
                if (i18 == 1) {
                    f25 = 90.0f;
                } else {
                    f25 = 0.0f;
                }
                if (i18 == 0) {
                    if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                } else if (i18 == 1) {
                    if (f24 < 100.0f) {
                        interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                    } else if (f24 < 484.0f) {
                        interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                    }
                    f25 = interpolation;
                }
                canvas3.rotate(f25);
                i19 = this.currentIcon;
                if (i19 == 0) {
                    canvas3.scale(f18, f18);
                } else {
                    canvas3.scale(f18, f18);
                }
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.scale(1.0f, -1.0f);
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.restore();
            } else {
                if (i15 != 1) {
                    if (this.nextIcon == 0) {
                        z2 = this.animatingTransition;
                        i20 = this.nextIcon;
                        if (z2) {
                            f26 = this.transitionProgress;
                            if (i20 == 0) {
                                f26 = 1.0f - f26;
                            }
                            f23 = f26;
                            i16 = 1;
                        } else {
                            i16 = 1;
                            if (i20 == 1) {
                                f23 = 1.0f;
                            } else {
                                f23 = 0.0f;
                            }
                        }
                    } else {
                        i16 = 1;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                } else {
                    i16 = 1;
                }
                if (i15 == i16) {
                    f23 = 1.0f;
                } else {
                    f23 = 0.0f;
                }
                i17 = this.nextIcon;
                if (i17 == 0) {
                    this.paint2.setAlpha(255);
                } else {
                    this.paint2.setAlpha(255);
                }
                applyShaderMatrix(true);
                canvas3.save();
                canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                f24 = f23 * 500.0f;
                i18 = this.currentIcon;
                if (i18 == 1) {
                    f25 = 90.0f;
                } else {
                    f25 = 0.0f;
                }
                if (i18 == 0) {
                    if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                } else if (i18 == 1) {
                    if (f24 < 100.0f) {
                        interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                    } else if (f24 < 484.0f) {
                        interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                    }
                    f25 = interpolation;
                }
                canvas3.rotate(f25);
                i19 = this.currentIcon;
                if (i19 == 0) {
                    canvas3.scale(f18, f18);
                } else {
                    canvas3.scale(f18, f18);
                }
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.scale(1.0f, -1.0f);
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.restore();
            }
            if (this.currentIcon != 6) {
                applyShaderMatrix(false);
                if (this.currentIcon != 6) {
                    f28 = this.transitionProgress;
                    if (f28 > 0.5f) {
                        f30 = (f28 - 0.5f) / 0.5f;
                        fMin2 = 1.0f - Math.min(1.0f, f30 / 0.5f);
                        if (f30 > 0.5f) {
                            f29 = (f30 - 0.5f) / 0.5f;
                        } else {
                            f29 = 0.0f;
                        }
                    } else {
                        f29 = 0.0f;
                        fMin2 = 1.0f;
                    }
                    this.paint.setAlpha(255);
                    f27 = f29;
                } else {
                    i21 = this.nextIcon;
                    paint = this.paint;
                    if (i21 != 6) {
                        paint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f));
                    } else {
                        paint.setAlpha(255);
                    }
                    fMin2 = 0.0f;
                    f27 = 1.0f;
                }
                iDp = AndroidUtilities.dp(7.0f) + iCenterY3;
                iDp2 = iCenterX - AndroidUtilities.dp(3.0f);
                if (fMin2 < 1.0f) {
                    i22 = iDp2;
                    canvas.drawLine(iDp2 - AndroidUtilities.dp(6.0f), iDp - AndroidUtilities.dp(6.0f), iDp2 - (AndroidUtilities.dp(6.0f) * fMin2), iDp - (AndroidUtilities.dp(6.0f) * fMin2), this.paint);
                } else {
                    i22 = iDp2;
                }
                if (f27 > 0.0f) {
                    float f1012 = i22;
                    float f1013 = iDp;
                    canvas.drawLine(f1012, f1013, f1012 + (AndroidUtilities.dp(12.0f) * f27), f1013 - (AndroidUtilities.dp(12.0f) * f27), this.paint);
                }
            } else {
                applyShaderMatrix(false);
                if (this.currentIcon != 6) {
                    f28 = this.transitionProgress;
                    if (f28 > 0.5f) {
                        f30 = (f28 - 0.5f) / 0.5f;
                        fMin2 = 1.0f - Math.min(1.0f, f30 / 0.5f);
                        if (f30 > 0.5f) {
                            f29 = (f30 - 0.5f) / 0.5f;
                        } else {
                            f29 = 0.0f;
                        }
                    } else {
                        f29 = 0.0f;
                        fMin2 = 1.0f;
                    }
                    this.paint.setAlpha(255);
                    f27 = f29;
                } else {
                    i21 = this.nextIcon;
                    paint = this.paint;
                    if (i21 != 6) {
                        paint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f));
                    } else {
                        paint.setAlpha(255);
                    }
                    fMin2 = 0.0f;
                    f27 = 1.0f;
                }
                iDp = AndroidUtilities.dp(7.0f) + iCenterY3;
                iDp2 = iCenterX - AndroidUtilities.dp(3.0f);
                if (fMin2 < 1.0f) {
                    i22 = iDp2;
                    canvas.drawLine(iDp2 - AndroidUtilities.dp(6.0f), iDp - AndroidUtilities.dp(6.0f), iDp2 - (AndroidUtilities.dp(6.0f) * fMin2), iDp - (AndroidUtilities.dp(6.0f) * fMin2), this.paint);
                } else {
                    i22 = iDp2;
                }
                if (f27 > 0.0f) {
                    float f1014 = i22;
                    float f1015 = iDp;
                    canvas.drawLine(f1014, f1015, f1014 + (AndroidUtilities.dp(12.0f) * f27), f1015 - (AndroidUtilities.dp(12.0f) * f27), this.paint);
                }
            }
            if (drawable3 != null) {
                canvas2 = canvas;
                i23 = iCenterX;
                i24 = iCenterY3;
                f31 = f20;
            } else {
                canvas2 = canvas;
                i23 = iCenterX;
                i24 = iCenterY3;
                f31 = f20;
            }
            if (drawable != null) {
                i28 = this.currentIcon;
                i29 = this.nextIcon;
                if (i28 == i29) {
                    i30 = 255;
                } else {
                    i30 = (int) (this.transitionProgress * 255.0f);
                }
                if (isCustomFileIcon(i29)) {
                    f32 = f18;
                    drawTintedDrawable(canvas2, drawable, i23, i24, f32, i30);
                } else {
                    f32 = f18;
                    int intrinsicWidth3 = (int) (drawable.getIntrinsicWidth() * f32);
                    int intrinsicHeight3 = (int) (drawable.getIntrinsicHeight() * f32);
                    drawable.setColorFilter(this.colorFilter);
                    drawable.setAlpha(i30);
                    int i510 = intrinsicWidth3 / 2;
                    int i511 = intrinsicHeight3 / 2;
                    drawable.setBounds(i23 - i510, i24 - i511, i510 + i23, i511 + i24);
                    drawable.draw(canvas2);
                }
            } else {
                f32 = f18;
            }
            if (pathArr4 != null) {
                int iDp16 = AndroidUtilities.dp(24.0f);
                this.paint2.setStyle(Paint.Style.FILL_AND_STROKE);
                Paint paint11 = this.paint2;
                if (this.currentIcon == this.nextIcon) {
                    i27 = 255;
                } else {
                    i27 = (int) ((1.0f - this.transitionProgress) * 255.0f);
                }
                paint11.setAlpha(i27);
                applyShaderMatrix(true);
                canvas2.save();
                canvas2.translate(i23, i24);
                canvas2.scale(f31, f31);
                float f1016 = (-iDp16) / 2;
                canvas2.translate(f1016, f1016);
                path4 = pathArr4[0];
                if (path4 != null) {
                    canvas2.drawPath(path4, this.paint2);
                }
                path5 = pathArr4[1];
                if (path5 != null) {
                    canvas2.drawPath(path5, this.backPaint);
                }
                canvas2.restore();
            }
            if (pathArr3 != null) {
                int iDp17 = AndroidUtilities.dp(24.0f);
                if (this.currentIcon == this.nextIcon) {
                    i26 = 255;
                } else {
                    i26 = (int) (this.transitionProgress * 255.0f);
                }
                this.paint2.setStyle(Paint.Style.FILL_AND_STROKE);
                this.paint2.setAlpha(i26);
                applyShaderMatrix(true);
                canvas2.save();
                canvas2.translate(i23, i24);
                canvas2.scale(f32, f32);
                float f1017 = (-iDp17) / 2;
                canvas2.translate(f1017, f1017);
                path = pathArr3[0];
                if (path != null) {
                    canvas2.drawPath(path, this.paint2);
                }
                if (pathArr3.length >= 3) {
                    canvas2.drawPath(path3, this.paint);
                }
                path2 = pathArr3[1];
                if (path2 != null) {
                    paint2 = this.backPaint;
                    if (i26 != 255) {
                        int alpha2 = paint2.getAlpha();
                        this.backPaint.setAlpha((int) (alpha2 * (i26 / 255.0f)));
                        canvas2.drawPath(pathArr3[1], this.backPaint);
                        this.backPaint.setAlpha(alpha2);
                    } else {
                        canvas2.drawPath(path2, paint2);
                    }
                }
                canvas2.restore();
            }
            long jCurrentTimeMillis2 = System.currentTimeMillis();
            j = jCurrentTimeMillis2 - this.lastAnimationTime;
            if (j > 17) {
                j = 17;
            }
            this.lastAnimationTime = jCurrentTimeMillis2;
            i25 = this.currentIcon;
            if (i25 != 3) {
                float f1018 = this.downloadRadOffset + ((360 * j) / 2500.0f);
                this.downloadRadOffset = f1018;
                this.downloadRadOffset = getCircleValue(f1018);
                if (this.nextIcon != 2) {
                    f33 = this.downloadProgress;
                    f34 = this.downloadProgressAnimationStart;
                    f35 = f33 - f34;
                    if (f35 > 0.0f) {
                        f36 = this.downloadProgressTime + j;
                        this.downloadProgressTime = f36;
                        if (f36 >= 200.0f) {
                            this.animatedDownloadProgress = f33;
                            this.downloadProgressAnimationStart = f33;
                            this.downloadProgressTime = 0.0f;
                        } else {
                            this.animatedDownloadProgress = f34 + (f35 * this.interpolator.getInterpolation(f36 / 200.0f));
                        }
                    }
                }
                invalidateSelf();
            } else {
                float f1019 = this.downloadRadOffset + ((360 * j) / 2500.0f);
                this.downloadRadOffset = f1019;
                this.downloadRadOffset = getCircleValue(f1019);
                if (this.nextIcon != 2) {
                    f33 = this.downloadProgress;
                    f34 = this.downloadProgressAnimationStart;
                    f35 = f33 - f34;
                    if (f35 > 0.0f) {
                        f36 = this.downloadProgressTime + j;
                        this.downloadProgressTime = f36;
                        if (f36 >= 200.0f) {
                            this.animatedDownloadProgress = f33;
                            this.downloadProgressAnimationStart = f33;
                            this.downloadProgressTime = 0.0f;
                        } else {
                            this.animatedDownloadProgress = f34 + (f35 * this.interpolator.getInterpolation(f36 / 200.0f));
                        }
                    }
                }
                invalidateSelf();
            }
            if (this.animatingTransition) {
                f37 = this.transitionProgress;
                if (f37 < 1.0f) {
                    f38 = f37 + (j / this.transitionAnimationTime);
                    this.transitionProgress = f38;
                    if (f38 >= 1.0f) {
                        this.currentIcon = this.nextIcon;
                        this.transitionProgress = 1.0f;
                        this.animatingTransition = false;
                    }
                    invalidateSelf();
                }
            }
            if (i >= 1) {
                canvas2.restoreToCount(i);
            }
        }
        i2 = 15;
        z = false;
        applyShaderMatrix(z);
        int i60 = this.nextIcon;
        if (i60 == 2) {
            if (this.drawProgressCircle) {
                float f110 = this.transitionProgress;
                if (f110 <= 0.5f) {
                    float f111 = 1.0f - (f110 / 0.5f);
                    fDp5 = AndroidUtilities.dp(7.0f) * f111 * this.scale;
                    i36 = (int) (f111 * 255.0f);
                } else {
                    fDp5 = f;
                    i36 = 0;
                }
            } else {
                fDp5 = f;
                i36 = 0;
            }
            iMin = i36;
            fCenterX = f;
            f42 = fCenterX;
            f41 = f42;
            f40 = 1.0f;
        } else {
            if (i60 == i2 || i60 == 0 || i60 == 1 || i60 == 5 || isCustomFileIcon(i60) || (i32 = this.nextIcon) == 8 || i32 == 9 || i32 == 7 || i32 == 6) {
                f92 = 360.0f;
                int i61 = this.nextIcon;
                float fMin3 = this.transitionProgress;
                if (i61 == 6) {
                    f39 = 1.0f;
                    fMin3 = Math.min(1.0f, fMin3 / 0.5f);
                } else {
                    f39 = 1.0f;
                }
                f40 = f39 - fMin3;
                float fCenterX3 = bounds.centerX();
                float fCenterY = bounds.centerY();
                float fDp20 = AndroidUtilities.dp(7.0f) * f40 * this.scale;
                iMin = (int) (Math.min(f39, f40 * 2.0f) * 255.0f);
                f41 = fCenterY;
                fCenterX = fCenterX3;
                fDp5 = fDp20;
                f42 = f;
            } else if (i32 == 4) {
                f40 = 1.0f - this.transitionProgress;
                fDp5 = AndroidUtilities.dp(7.0f) * this.scale;
                int i62 = (int) (f40 * 255.0f);
                if (this.currentIcon == 14) {
                    fCenterX = bounds.left;
                    iCenterY2 = bounds.top;
                } else {
                    fCenterX = bounds.centerX();
                    iCenterY2 = bounds.centerY();
                }
                iMin = i62;
                f42 = f;
                f41 = iCenterY2;
            } else if (i32 == 14 || i32 == 3) {
                float f112 = this.transitionProgress;
                float f113 = 1.0f - f112;
                if (this.currentIcon == 4) {
                    f44 = f112;
                    f43 = f;
                } else {
                    f43 = f113 * 45.0f;
                    f44 = 1.0f;
                }
                float fDp21 = AndroidUtilities.dp(7.0f) * this.scale;
                int i63 = (int) (f112 * 255.0f);
                if (this.nextIcon == 14) {
                    fCenterX2 = bounds.left;
                    iCenterY = bounds.top;
                } else {
                    fCenterX2 = bounds.centerX();
                    iCenterY = bounds.centerY();
                }
                f41 = iCenterY;
                iMin = i63;
                f40 = f44;
                fDp5 = fDp21;
                f42 = f43;
                fCenterX = fCenterX2;
            } else {
                fDp5 = AndroidUtilities.dp(7.0f) * this.scale;
                f92 = 360.0f;
                fCenterX = f;
                f42 = fCenterX;
                f41 = f42;
                f40 = 1.0f;
                f39 = 1.0f;
                iMin = 255;
            }
            if (f40 != f39) {
                canvas3.save();
                canvas3.scale(f40, f40, fCenterX, f41);
            }
            if (f42 != f) {
                canvas3.save();
                canvas3.rotate(f42, iCenterX, iCenterY3);
            }
            if (iMin != 0) {
                f47 = iMin;
                this.paint.setAlpha((int) (this.overrideAlpha * f47));
                if (this.currentIcon != 14 || this.nextIcon == 14) {
                    f45 = 4.0f;
                    this.paint3.setAlpha((int) (f47 * this.overrideAlpha));
                    this.rect.set(iCenterX - AndroidUtilities.dp(3.5f), iCenterY3 - AndroidUtilities.dp(3.5f), AndroidUtilities.dp(3.5f) + iCenterX, AndroidUtilities.dp(3.5f) + iCenterY3);
                    canvas3.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), this.paint3);
                } else {
                    float f114 = iCenterX;
                    float f115 = f114 - fDp5;
                    float f116 = iCenterY3;
                    float f117 = f116 - fDp5;
                    float f118 = f114 + fDp5;
                    float f119 = f116 + fDp5;
                    f45 = 4.0f;
                    canvas3.drawLine(f115, f117, f118, f119, this.paint);
                    canvas3 = canvas;
                    canvas3.drawLine(f118, f117, f115, f119, this.paint);
                }
            } else {
                f45 = 4.0f;
            }
            if (f42 != f) {
                canvas3.restore();
            }
            if (f40 != f39) {
                canvas3.restore();
            }
            i33 = this.currentIcon;
            if ((i33 != 3 || i33 == 14 || (i33 == 4 && ((i35 = this.nextIcon) == 14 || i35 == 3))) && iMin != 0 && this.drawProgressCircle) {
                float fMax3 = Math.max(f45, this.animatedDownloadProgress * f92);
                if (this.isMini) {
                    f46 = 2.0f;
                } else {
                    f46 = f45;
                }
                int iDp18 = AndroidUtilities.dp(f46);
                this.rect.set(bounds.left + iDp18, bounds.top + iDp18, bounds.right - iDp18, bounds.bottom - iDp18);
                i34 = this.currentIcon;
                if (i34 != 14 || (i34 == 4 && this.nextIcon == 14)) {
                    this.paint.setAlpha((int) (iMin * 0.15f * this.overrideAlpha));
                    canvas3.drawArc(this.rect, 0.0f, 360.0f, false, this.paint);
                    this.paint.setAlpha(iMin);
                }
                canvas3 = canvas;
                canvas3.drawArc(this.rect, this.downloadRadOffset, fMax3, false, this.paint);
            }
            i3 = this.currentIcon;
            if (i3 == this.nextIcon) {
                f18 = 1.0f;
                f17 = 1.0f;
            } else {
                if (i3 != 4) {
                    fMin = this.transitionProgress;
                    fMax = 1.0f - fMin;
                } else {
                    fMin = this.transitionProgress;
                    fMax = 1.0f - fMin;
                }
                f17 = fMax;
                f18 = fMin;
            }
            i4 = this.nextIcon;
            if (i4 == i2) {
                pathArr = Theme.chat_updatePath;
            } else {
                if (this.currentIcon == i2) {
                    pathArr2 = Theme.chat_updatePath;
                    pathArr = null;
                } else {
                    pathArr = null;
                }
                if (i4 == 5) {
                    pathArr = Theme.chat_filePath;
                } else if (this.currentIcon == 5) {
                    pathArr2 = Theme.chat_filePath;
                }
                pathArr3 = pathArr;
                pathArr4 = pathArr2;
                if (isCustomFileIcon(i4)) {
                    customIconDrawable = getCustomIconDrawable(this.nextIcon);
                } else {
                    customIconDrawable = null;
                }
                if (isCustomFileIcon(this.currentIcon)) {
                    customIconDrawable2 = getCustomIconDrawable(this.currentIcon);
                } else {
                    customIconDrawable2 = null;
                }
                i5 = this.nextIcon;
                if (i5 == 7) {
                    customIconDrawable = Theme.chat_flameIcon;
                } else if (this.currentIcon == 7) {
                    customIconDrawable2 = Theme.chat_flameIcon;
                }
                if (i5 == 8) {
                    customIconDrawable = Theme.chat_gifIcon;
                } else if (this.currentIcon == 8) {
                    customIconDrawable2 = Theme.chat_gifIcon;
                }
                drawable = customIconDrawable;
                drawable2 = customIconDrawable2;
                if (this.currentIcon != 9) {
                    applyShaderMatrix(false);
                    Paint paint12 = this.paint;
                    if (this.currentIcon == this.nextIcon) {
                        i6 = 255;
                    } else {
                        i6 = (int) (this.transitionProgress * 255.0f);
                    }
                    paint12.setAlpha(i6);
                    int iDp19 = AndroidUtilities.dp(7.0f) + iCenterY3;
                    int iDp110 = iCenterX - AndroidUtilities.dp(3.0f);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.save();
                        float f9114 = this.transitionProgress;
                        canvas3.scale(f9114, f9114, iCenterX, iCenterY3);
                    }
                    float f9115 = iDp110;
                    float f9116 = iDp19;
                    rect = bounds;
                    drawable3 = drawable2;
                    canvas3.drawLine(iDp110 - AndroidUtilities.dp(6.0f), iDp19 - AndroidUtilities.dp(6.0f), f9115, f9116, this.paint);
                    canvas3 = canvas;
                    canvas3.drawLine(f9115, f9116, iDp110 + AndroidUtilities.dp(12.0f), iDp19 - AndroidUtilities.dp(12.0f), this.paint);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.restore();
                    }
                } else {
                    applyShaderMatrix(false);
                    Paint paint13 = this.paint;
                    if (this.currentIcon == this.nextIcon) {
                        i6 = 255;
                    } else {
                        i6 = (int) (this.transitionProgress * 255.0f);
                    }
                    paint13.setAlpha(i6);
                    int iDp111 = AndroidUtilities.dp(7.0f) + iCenterY3;
                    int iDp112 = iCenterX - AndroidUtilities.dp(3.0f);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.save();
                        float f9117 = this.transitionProgress;
                        canvas3.scale(f9117, f9117, iCenterX, iCenterY3);
                    }
                    float f9118 = iDp112;
                    float f9119 = iDp111;
                    rect = bounds;
                    drawable3 = drawable2;
                    canvas3.drawLine(iDp112 - AndroidUtilities.dp(6.0f), iDp111 - AndroidUtilities.dp(6.0f), f9118, f9119, this.paint);
                    canvas3 = canvas;
                    canvas3.drawLine(f9118, f9119, iDp112 + AndroidUtilities.dp(12.0f), iDp111 - AndroidUtilities.dp(12.0f), this.paint);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.restore();
                    }
                }
                if (this.currentIcon != 12) {
                    applyShaderMatrix(false);
                    i7 = this.currentIcon;
                    i8 = this.nextIcon;
                    if (i7 == i8) {
                        i9 = 13;
                        f19 = 1.0f;
                    } else {
                        f19 = this.transitionProgress;
                        i9 = 13;
                        if (i8 != 13) {
                            f19 = 1.0f - f19;
                        }
                    }
                    Paint paint14 = this.paint;
                    if (i7 == i8) {
                        i10 = 255;
                    } else {
                        i10 = (int) (f19 * 255.0f);
                    }
                    paint14.setAlpha(i10);
                    AndroidUtilities.dp(7.0f);
                    AndroidUtilities.dp(3.0f);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.save();
                        canvas3.scale(f19, f19, iCenterX, iCenterY3);
                    }
                    float fDp111 = AndroidUtilities.dp(7.0f) * this.scale;
                    float f91110 = iCenterX;
                    float f91111 = f91110 - fDp111;
                    float f91112 = iCenterY3;
                    float f91113 = f91112 - fDp111;
                    float f10110 = f91110 + fDp111;
                    float f10111 = fDp111 + f91112;
                    f20 = f17;
                    i11 = i9;
                    canvas3.drawLine(f91111, f91113, f10110, f10111, this.paint);
                    canvas3 = canvas;
                    canvas3.drawLine(f10110, f91113, f91111, f10111, this.paint);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.restore();
                    }
                } else {
                    applyShaderMatrix(false);
                    i7 = this.currentIcon;
                    i8 = this.nextIcon;
                    if (i7 == i8) {
                        i9 = 13;
                        f19 = 1.0f;
                    } else {
                        f19 = this.transitionProgress;
                        i9 = 13;
                        if (i8 != 13) {
                            f19 = 1.0f - f19;
                        }
                    }
                    Paint paint15 = this.paint;
                    if (i7 == i8) {
                        i10 = 255;
                    } else {
                        i10 = (int) (f19 * 255.0f);
                    }
                    paint15.setAlpha(i10);
                    AndroidUtilities.dp(7.0f);
                    AndroidUtilities.dp(3.0f);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.save();
                        canvas3.scale(f19, f19, iCenterX, iCenterY3);
                    }
                    float fDp112 = AndroidUtilities.dp(7.0f) * this.scale;
                    float f91114 = iCenterX;
                    float f91115 = f91114 - fDp112;
                    float f91116 = iCenterY3;
                    float f91117 = f91116 - fDp112;
                    float f10112 = f91114 + fDp112;
                    float f10113 = fDp112 + f91116;
                    f20 = f17;
                    i11 = i9;
                    canvas3.drawLine(f91115, f91117, f10112, f10113, this.paint);
                    canvas3 = canvas;
                    canvas3.drawLine(f10112, f91117, f91115, f10113, this.paint);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.restore();
                    }
                }
                if (this.currentIcon != i11) {
                    applyShaderMatrix(false);
                    i12 = this.currentIcon;
                    i13 = this.nextIcon;
                    if (i12 == i13) {
                        f21 = 1.0f;
                    } else {
                        f21 = this.transitionProgress;
                        if (i13 != i11) {
                            f21 = 1.0f - f21;
                        }
                    }
                    this.textPaint.setAlpha((int) (f21 * 255.0f));
                    int iDp113 = AndroidUtilities.dp(5.0f) + iCenterY3;
                    int i512 = iCenterX - (this.percentStringWidth / 2);
                    f22 = 5.0f;
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.save();
                        canvas3.scale(f21, f21, iCenterX, iCenterY3);
                    }
                    i14 = (int) (this.animatedDownloadProgress * 100.0f);
                    if (this.percentString != null) {
                        this.lastPercent = i14;
                        String str6 = String.format("%d%%", Integer.valueOf(i14));
                        this.percentString = str6;
                        this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str6));
                    } else {
                        this.lastPercent = i14;
                        String str7 = String.format("%d%%", Integer.valueOf(i14));
                        this.percentString = str7;
                        this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str7));
                    }
                    canvas3.drawText(this.percentString, i512, iDp113, this.textPaint);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.restore();
                    }
                } else {
                    applyShaderMatrix(false);
                    i12 = this.currentIcon;
                    i13 = this.nextIcon;
                    if (i12 == i13) {
                        f21 = 1.0f;
                    } else {
                        f21 = this.transitionProgress;
                        if (i13 != i11) {
                            f21 = 1.0f - f21;
                        }
                    }
                    this.textPaint.setAlpha((int) (f21 * 255.0f));
                    int iDp114 = AndroidUtilities.dp(5.0f) + iCenterY3;
                    int i513 = iCenterX - (this.percentStringWidth / 2);
                    f22 = 5.0f;
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.save();
                        canvas3.scale(f21, f21, iCenterX, iCenterY3);
                    }
                    i14 = (int) (this.animatedDownloadProgress * 100.0f);
                    if (this.percentString != null) {
                        this.lastPercent = i14;
                        String str8 = String.format("%d%%", Integer.valueOf(i14));
                        this.percentString = str8;
                        this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str8));
                    } else {
                        this.lastPercent = i14;
                        String str9 = String.format("%d%%", Integer.valueOf(i14));
                        this.percentString = str9;
                        this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str9));
                    }
                    canvas3.drawText(this.percentString, i513, iDp114, this.textPaint);
                    if (this.currentIcon != this.nextIcon) {
                        canvas3.restore();
                    }
                }
                i15 = this.currentIcon;
                if (i15 != 0) {
                    if (i15 == 0) {
                        if (i15 != 1) {
                            if (this.nextIcon == 0) {
                                z2 = this.animatingTransition;
                                i20 = this.nextIcon;
                                if (z2) {
                                    f26 = this.transitionProgress;
                                    if (i20 == 0) {
                                        f26 = 1.0f - f26;
                                    }
                                    f23 = f26;
                                    i16 = 1;
                                } else {
                                    i16 = 1;
                                    if (i20 == 1) {
                                        f23 = 1.0f;
                                    } else {
                                        f23 = 0.0f;
                                    }
                                }
                            } else {
                                i16 = 1;
                            }
                            i17 = this.nextIcon;
                            if (i17 == 0) {
                                this.paint2.setAlpha(255);
                            } else {
                                this.paint2.setAlpha(255);
                            }
                            applyShaderMatrix(true);
                            canvas3.save();
                            canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                            f24 = f23 * 500.0f;
                            i18 = this.currentIcon;
                            if (i18 == 1) {
                                f25 = 90.0f;
                            } else {
                                f25 = 0.0f;
                            }
                            if (i18 == 0) {
                                if (i18 == 1) {
                                    if (f24 < 100.0f) {
                                        interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                                    } else if (f24 < 484.0f) {
                                        interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                                    }
                                    f25 = interpolation;
                                }
                            } else if (i18 == 1) {
                                if (f24 < 100.0f) {
                                    interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                                } else if (f24 < 484.0f) {
                                    interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                                }
                                f25 = interpolation;
                            }
                            canvas3.rotate(f25);
                            i19 = this.currentIcon;
                            if (i19 == 0) {
                                canvas3.scale(f18, f18);
                            } else {
                                canvas3.scale(f18, f18);
                            }
                            Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                            canvas3.scale(1.0f, -1.0f);
                            Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                            canvas3.restore();
                        } else {
                            i16 = 1;
                        }
                        if (i15 == i16) {
                            f23 = 1.0f;
                        } else {
                            f23 = 0.0f;
                        }
                        i17 = this.nextIcon;
                        if (i17 == 0) {
                            this.paint2.setAlpha(255);
                        } else {
                            this.paint2.setAlpha(255);
                        }
                        applyShaderMatrix(true);
                        canvas3.save();
                        canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                        f24 = f23 * 500.0f;
                        i18 = this.currentIcon;
                        if (i18 == 1) {
                            f25 = 90.0f;
                        } else {
                            f25 = 0.0f;
                        }
                        if (i18 == 0) {
                            if (i18 == 1) {
                                if (f24 < 100.0f) {
                                    interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                                } else if (f24 < 484.0f) {
                                    interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                                }
                                f25 = interpolation;
                            }
                        } else if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                        canvas3.rotate(f25);
                        i19 = this.currentIcon;
                        if (i19 == 0) {
                            canvas3.scale(f18, f18);
                        } else {
                            canvas3.scale(f18, f18);
                        }
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.scale(1.0f, -1.0f);
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.restore();
                    } else {
                        if (i15 != 1) {
                            if (this.nextIcon == 0) {
                                z2 = this.animatingTransition;
                                i20 = this.nextIcon;
                                if (z2) {
                                    f26 = this.transitionProgress;
                                    if (i20 == 0) {
                                        f26 = 1.0f - f26;
                                    }
                                    f23 = f26;
                                    i16 = 1;
                                } else {
                                    i16 = 1;
                                    if (i20 == 1) {
                                        f23 = 1.0f;
                                    } else {
                                        f23 = 0.0f;
                                    }
                                }
                            } else {
                                i16 = 1;
                            }
                            i17 = this.nextIcon;
                            if (i17 == 0) {
                                this.paint2.setAlpha(255);
                            } else {
                                this.paint2.setAlpha(255);
                            }
                            applyShaderMatrix(true);
                            canvas3.save();
                            canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                            f24 = f23 * 500.0f;
                            i18 = this.currentIcon;
                            if (i18 == 1) {
                                f25 = 90.0f;
                            } else {
                                f25 = 0.0f;
                            }
                            if (i18 == 0) {
                                if (i18 == 1) {
                                    if (f24 < 100.0f) {
                                        interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                                    } else if (f24 < 484.0f) {
                                        interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                                    }
                                    f25 = interpolation;
                                }
                            } else if (i18 == 1) {
                                if (f24 < 100.0f) {
                                    interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                                } else if (f24 < 484.0f) {
                                    interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                                }
                                f25 = interpolation;
                            }
                            canvas3.rotate(f25);
                            i19 = this.currentIcon;
                            if (i19 == 0) {
                                canvas3.scale(f18, f18);
                            } else {
                                canvas3.scale(f18, f18);
                            }
                            Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                            canvas3.scale(1.0f, -1.0f);
                            Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                            canvas3.restore();
                        } else {
                            i16 = 1;
                        }
                        if (i15 == i16) {
                            f23 = 1.0f;
                        } else {
                            f23 = 0.0f;
                        }
                        i17 = this.nextIcon;
                        if (i17 == 0) {
                            this.paint2.setAlpha(255);
                        } else {
                            this.paint2.setAlpha(255);
                        }
                        applyShaderMatrix(true);
                        canvas3.save();
                        canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                        f24 = f23 * 500.0f;
                        i18 = this.currentIcon;
                        if (i18 == 1) {
                            f25 = 90.0f;
                        } else {
                            f25 = 0.0f;
                        }
                        if (i18 == 0) {
                            if (i18 == 1) {
                                if (f24 < 100.0f) {
                                    interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                                } else if (f24 < 484.0f) {
                                    interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                                }
                                f25 = interpolation;
                            }
                        } else if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                        canvas3.rotate(f25);
                        i19 = this.currentIcon;
                        if (i19 == 0) {
                            canvas3.scale(f18, f18);
                        } else {
                            canvas3.scale(f18, f18);
                        }
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.scale(1.0f, -1.0f);
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.restore();
                    }
                } else if (i15 == 0) {
                    if (i15 != 1) {
                        if (this.nextIcon == 0) {
                            z2 = this.animatingTransition;
                            i20 = this.nextIcon;
                            if (z2) {
                                f26 = this.transitionProgress;
                                if (i20 == 0) {
                                    f26 = 1.0f - f26;
                                }
                                f23 = f26;
                                i16 = 1;
                            } else {
                                i16 = 1;
                                if (i20 == 1) {
                                    f23 = 1.0f;
                                } else {
                                    f23 = 0.0f;
                                }
                            }
                        } else {
                            i16 = 1;
                        }
                        i17 = this.nextIcon;
                        if (i17 == 0) {
                            this.paint2.setAlpha(255);
                        } else {
                            this.paint2.setAlpha(255);
                        }
                        applyShaderMatrix(true);
                        canvas3.save();
                        canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                        f24 = f23 * 500.0f;
                        i18 = this.currentIcon;
                        if (i18 == 1) {
                            f25 = 90.0f;
                        } else {
                            f25 = 0.0f;
                        }
                        if (i18 == 0) {
                            if (i18 == 1) {
                                if (f24 < 100.0f) {
                                    interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                                } else if (f24 < 484.0f) {
                                    interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                                }
                                f25 = interpolation;
                            }
                        } else if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                        canvas3.rotate(f25);
                        i19 = this.currentIcon;
                        if (i19 == 0) {
                            canvas3.scale(f18, f18);
                        } else {
                            canvas3.scale(f18, f18);
                        }
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.scale(1.0f, -1.0f);
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.restore();
                    } else {
                        i16 = 1;
                    }
                    if (i15 == i16) {
                        f23 = 1.0f;
                    } else {
                        f23 = 0.0f;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                } else {
                    if (i15 != 1) {
                        if (this.nextIcon == 0) {
                            z2 = this.animatingTransition;
                            i20 = this.nextIcon;
                            if (z2) {
                                f26 = this.transitionProgress;
                                if (i20 == 0) {
                                    f26 = 1.0f - f26;
                                }
                                f23 = f26;
                                i16 = 1;
                            } else {
                                i16 = 1;
                                if (i20 == 1) {
                                    f23 = 1.0f;
                                } else {
                                    f23 = 0.0f;
                                }
                            }
                        } else {
                            i16 = 1;
                        }
                        i17 = this.nextIcon;
                        if (i17 == 0) {
                            this.paint2.setAlpha(255);
                        } else {
                            this.paint2.setAlpha(255);
                        }
                        applyShaderMatrix(true);
                        canvas3.save();
                        canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                        f24 = f23 * 500.0f;
                        i18 = this.currentIcon;
                        if (i18 == 1) {
                            f25 = 90.0f;
                        } else {
                            f25 = 0.0f;
                        }
                        if (i18 == 0) {
                            if (i18 == 1) {
                                if (f24 < 100.0f) {
                                    interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                                } else if (f24 < 484.0f) {
                                    interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                                }
                                f25 = interpolation;
                            }
                        } else if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                        canvas3.rotate(f25);
                        i19 = this.currentIcon;
                        if (i19 == 0) {
                            canvas3.scale(f18, f18);
                        } else {
                            canvas3.scale(f18, f18);
                        }
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.scale(1.0f, -1.0f);
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.restore();
                    } else {
                        i16 = 1;
                    }
                    if (i15 == i16) {
                        f23 = 1.0f;
                    } else {
                        f23 = 0.0f;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                }
                if (this.currentIcon != 6) {
                    applyShaderMatrix(false);
                    if (this.currentIcon != 6) {
                        f28 = this.transitionProgress;
                        if (f28 > 0.5f) {
                            f30 = (f28 - 0.5f) / 0.5f;
                            fMin2 = 1.0f - Math.min(1.0f, f30 / 0.5f);
                            if (f30 > 0.5f) {
                                f29 = (f30 - 0.5f) / 0.5f;
                            } else {
                                f29 = 0.0f;
                            }
                        } else {
                            f29 = 0.0f;
                            fMin2 = 1.0f;
                        }
                        this.paint.setAlpha(255);
                        f27 = f29;
                    } else {
                        i21 = this.nextIcon;
                        paint = this.paint;
                        if (i21 != 6) {
                            paint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f));
                        } else {
                            paint.setAlpha(255);
                        }
                        fMin2 = 0.0f;
                        f27 = 1.0f;
                    }
                    iDp = AndroidUtilities.dp(7.0f) + iCenterY3;
                    iDp2 = iCenterX - AndroidUtilities.dp(3.0f);
                    if (fMin2 < 1.0f) {
                        i22 = iDp2;
                        canvas.drawLine(iDp2 - AndroidUtilities.dp(6.0f), iDp - AndroidUtilities.dp(6.0f), iDp2 - (AndroidUtilities.dp(6.0f) * fMin2), iDp - (AndroidUtilities.dp(6.0f) * fMin2), this.paint);
                    } else {
                        i22 = iDp2;
                    }
                    if (f27 > 0.0f) {
                        float f10114 = i22;
                        float f10115 = iDp;
                        canvas.drawLine(f10114, f10115, f10114 + (AndroidUtilities.dp(12.0f) * f27), f10115 - (AndroidUtilities.dp(12.0f) * f27), this.paint);
                    }
                } else {
                    applyShaderMatrix(false);
                    if (this.currentIcon != 6) {
                        f28 = this.transitionProgress;
                        if (f28 > 0.5f) {
                            f30 = (f28 - 0.5f) / 0.5f;
                            fMin2 = 1.0f - Math.min(1.0f, f30 / 0.5f);
                            if (f30 > 0.5f) {
                                f29 = (f30 - 0.5f) / 0.5f;
                            } else {
                                f29 = 0.0f;
                            }
                        } else {
                            f29 = 0.0f;
                            fMin2 = 1.0f;
                        }
                        this.paint.setAlpha(255);
                        f27 = f29;
                    } else {
                        i21 = this.nextIcon;
                        paint = this.paint;
                        if (i21 != 6) {
                            paint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f));
                        } else {
                            paint.setAlpha(255);
                        }
                        fMin2 = 0.0f;
                        f27 = 1.0f;
                    }
                    iDp = AndroidUtilities.dp(7.0f) + iCenterY3;
                    iDp2 = iCenterX - AndroidUtilities.dp(3.0f);
                    if (fMin2 < 1.0f) {
                        i22 = iDp2;
                        canvas.drawLine(iDp2 - AndroidUtilities.dp(6.0f), iDp - AndroidUtilities.dp(6.0f), iDp2 - (AndroidUtilities.dp(6.0f) * fMin2), iDp - (AndroidUtilities.dp(6.0f) * fMin2), this.paint);
                    } else {
                        i22 = iDp2;
                    }
                    if (f27 > 0.0f) {
                        float f10116 = i22;
                        float f10117 = iDp;
                        canvas.drawLine(f10116, f10117, f10116 + (AndroidUtilities.dp(12.0f) * f27), f10117 - (AndroidUtilities.dp(12.0f) * f27), this.paint);
                    }
                }
                if (drawable3 != null) {
                    canvas2 = canvas;
                    i23 = iCenterX;
                    i24 = iCenterY3;
                    f31 = f20;
                } else {
                    canvas2 = canvas;
                    i23 = iCenterX;
                    i24 = iCenterY3;
                    f31 = f20;
                }
                if (drawable != null) {
                    i28 = this.currentIcon;
                    i29 = this.nextIcon;
                    if (i28 == i29) {
                        i30 = 255;
                    } else {
                        i30 = (int) (this.transitionProgress * 255.0f);
                    }
                    if (isCustomFileIcon(i29)) {
                        f32 = f18;
                        drawTintedDrawable(canvas2, drawable, i23, i24, f32, i30);
                    } else {
                        f32 = f18;
                        int intrinsicWidth4 = (int) (drawable.getIntrinsicWidth() * f32);
                        int intrinsicHeight4 = (int) (drawable.getIntrinsicHeight() * f32);
                        drawable.setColorFilter(this.colorFilter);
                        drawable.setAlpha(i30);
                        int i514 = intrinsicWidth4 / 2;
                        int i515 = intrinsicHeight4 / 2;
                        drawable.setBounds(i23 - i514, i24 - i515, i514 + i23, i515 + i24);
                        drawable.draw(canvas2);
                    }
                } else {
                    f32 = f18;
                }
                if (pathArr4 != null) {
                    int iDp115 = AndroidUtilities.dp(24.0f);
                    this.paint2.setStyle(Paint.Style.FILL_AND_STROKE);
                    Paint paint16 = this.paint2;
                    if (this.currentIcon == this.nextIcon) {
                        i27 = 255;
                    } else {
                        i27 = (int) ((1.0f - this.transitionProgress) * 255.0f);
                    }
                    paint16.setAlpha(i27);
                    applyShaderMatrix(true);
                    canvas2.save();
                    canvas2.translate(i23, i24);
                    canvas2.scale(f31, f31);
                    float f10118 = (-iDp115) / 2;
                    canvas2.translate(f10118, f10118);
                    path4 = pathArr4[0];
                    if (path4 != null) {
                        canvas2.drawPath(path4, this.paint2);
                    }
                    path5 = pathArr4[1];
                    if (path5 != null) {
                        canvas2.drawPath(path5, this.backPaint);
                    }
                    canvas2.restore();
                }
                if (pathArr3 != null) {
                    int iDp116 = AndroidUtilities.dp(24.0f);
                    if (this.currentIcon == this.nextIcon) {
                        i26 = 255;
                    } else {
                        i26 = (int) (this.transitionProgress * 255.0f);
                    }
                    this.paint2.setStyle(Paint.Style.FILL_AND_STROKE);
                    this.paint2.setAlpha(i26);
                    applyShaderMatrix(true);
                    canvas2.save();
                    canvas2.translate(i23, i24);
                    canvas2.scale(f32, f32);
                    float f10119 = (-iDp116) / 2;
                    canvas2.translate(f10119, f10119);
                    path = pathArr3[0];
                    if (path != null) {
                        canvas2.drawPath(path, this.paint2);
                    }
                    if (pathArr3.length >= 3) {
                        canvas2.drawPath(path3, this.paint);
                    }
                    path2 = pathArr3[1];
                    if (path2 != null) {
                        paint2 = this.backPaint;
                        if (i26 != 255) {
                            int alpha3 = paint2.getAlpha();
                            this.backPaint.setAlpha((int) (alpha3 * (i26 / 255.0f)));
                            canvas2.drawPath(pathArr3[1], this.backPaint);
                            this.backPaint.setAlpha(alpha3);
                        } else {
                            canvas2.drawPath(path2, paint2);
                        }
                    }
                    canvas2.restore();
                }
                long jCurrentTimeMillis3 = System.currentTimeMillis();
                j = jCurrentTimeMillis3 - this.lastAnimationTime;
                if (j > 17) {
                    j = 17;
                }
                this.lastAnimationTime = jCurrentTimeMillis3;
                i25 = this.currentIcon;
                if (i25 != 3) {
                    float f10120 = this.downloadRadOffset + ((360 * j) / 2500.0f);
                    this.downloadRadOffset = f10120;
                    this.downloadRadOffset = getCircleValue(f10120);
                    if (this.nextIcon != 2) {
                        f33 = this.downloadProgress;
                        f34 = this.downloadProgressAnimationStart;
                        f35 = f33 - f34;
                        if (f35 > 0.0f) {
                            f36 = this.downloadProgressTime + j;
                            this.downloadProgressTime = f36;
                            if (f36 >= 200.0f) {
                                this.animatedDownloadProgress = f33;
                                this.downloadProgressAnimationStart = f33;
                                this.downloadProgressTime = 0.0f;
                            } else {
                                this.animatedDownloadProgress = f34 + (f35 * this.interpolator.getInterpolation(f36 / 200.0f));
                            }
                        }
                    }
                    invalidateSelf();
                } else {
                    float f10121 = this.downloadRadOffset + ((360 * j) / 2500.0f);
                    this.downloadRadOffset = f10121;
                    this.downloadRadOffset = getCircleValue(f10121);
                    if (this.nextIcon != 2) {
                        f33 = this.downloadProgress;
                        f34 = this.downloadProgressAnimationStart;
                        f35 = f33 - f34;
                        if (f35 > 0.0f) {
                            f36 = this.downloadProgressTime + j;
                            this.downloadProgressTime = f36;
                            if (f36 >= 200.0f) {
                                this.animatedDownloadProgress = f33;
                                this.downloadProgressAnimationStart = f33;
                                this.downloadProgressTime = 0.0f;
                            } else {
                                this.animatedDownloadProgress = f34 + (f35 * this.interpolator.getInterpolation(f36 / 200.0f));
                            }
                        }
                    }
                    invalidateSelf();
                }
                if (this.animatingTransition) {
                    f37 = this.transitionProgress;
                    if (f37 < 1.0f) {
                        f38 = f37 + (j / this.transitionAnimationTime);
                        this.transitionProgress = f38;
                        if (f38 >= 1.0f) {
                            this.currentIcon = this.nextIcon;
                            this.transitionProgress = 1.0f;
                            this.animatingTransition = false;
                        }
                        invalidateSelf();
                    }
                }
                if (i >= 1) {
                    canvas2.restoreToCount(i);
                }
            }
            pathArr2 = null;
            if (i4 == 5) {
                pathArr = Theme.chat_filePath;
            } else if (this.currentIcon == 5) {
                pathArr2 = Theme.chat_filePath;
            }
            pathArr3 = pathArr;
            pathArr4 = pathArr2;
            if (isCustomFileIcon(i4)) {
                customIconDrawable = getCustomIconDrawable(this.nextIcon);
            } else {
                customIconDrawable = null;
            }
            if (isCustomFileIcon(this.currentIcon)) {
                customIconDrawable2 = getCustomIconDrawable(this.currentIcon);
            } else {
                customIconDrawable2 = null;
            }
            i5 = this.nextIcon;
            if (i5 == 7) {
                customIconDrawable = Theme.chat_flameIcon;
            } else if (this.currentIcon == 7) {
                customIconDrawable2 = Theme.chat_flameIcon;
            }
            if (i5 == 8) {
                customIconDrawable = Theme.chat_gifIcon;
            } else if (this.currentIcon == 8) {
                customIconDrawable2 = Theme.chat_gifIcon;
            }
            drawable = customIconDrawable;
            drawable2 = customIconDrawable2;
            if (this.currentIcon != 9) {
                applyShaderMatrix(false);
                Paint paint17 = this.paint;
                if (this.currentIcon == this.nextIcon) {
                    i6 = 255;
                } else {
                    i6 = (int) (this.transitionProgress * 255.0f);
                }
                paint17.setAlpha(i6);
                int iDp117 = AndroidUtilities.dp(7.0f) + iCenterY3;
                int iDp118 = iCenterX - AndroidUtilities.dp(3.0f);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    float f91118 = this.transitionProgress;
                    canvas3.scale(f91118, f91118, iCenterX, iCenterY3);
                }
                float f91119 = iDp118;
                float f91120 = iDp117;
                rect = bounds;
                drawable3 = drawable2;
                canvas3.drawLine(iDp118 - AndroidUtilities.dp(6.0f), iDp117 - AndroidUtilities.dp(6.0f), f91119, f91120, this.paint);
                canvas3 = canvas;
                canvas3.drawLine(f91119, f91120, iDp118 + AndroidUtilities.dp(12.0f), iDp117 - AndroidUtilities.dp(12.0f), this.paint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            } else {
                applyShaderMatrix(false);
                Paint paint18 = this.paint;
                if (this.currentIcon == this.nextIcon) {
                    i6 = 255;
                } else {
                    i6 = (int) (this.transitionProgress * 255.0f);
                }
                paint18.setAlpha(i6);
                int iDp119 = AndroidUtilities.dp(7.0f) + iCenterY3;
                int iDp1110 = iCenterX - AndroidUtilities.dp(3.0f);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    float f911110 = this.transitionProgress;
                    canvas3.scale(f911110, f911110, iCenterX, iCenterY3);
                }
                float f911111 = iDp1110;
                float f91121 = iDp119;
                rect = bounds;
                drawable3 = drawable2;
                canvas3.drawLine(iDp1110 - AndroidUtilities.dp(6.0f), iDp119 - AndroidUtilities.dp(6.0f), f911111, f91121, this.paint);
                canvas3 = canvas;
                canvas3.drawLine(f911111, f91121, iDp1110 + AndroidUtilities.dp(12.0f), iDp119 - AndroidUtilities.dp(12.0f), this.paint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            }
            if (this.currentIcon != 12) {
                applyShaderMatrix(false);
                i7 = this.currentIcon;
                i8 = this.nextIcon;
                if (i7 == i8) {
                    i9 = 13;
                    f19 = 1.0f;
                } else {
                    f19 = this.transitionProgress;
                    i9 = 13;
                    if (i8 != 13) {
                        f19 = 1.0f - f19;
                    }
                }
                Paint paint19 = this.paint;
                if (i7 == i8) {
                    i10 = 255;
                } else {
                    i10 = (int) (f19 * 255.0f);
                }
                paint19.setAlpha(i10);
                AndroidUtilities.dp(7.0f);
                AndroidUtilities.dp(3.0f);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    canvas3.scale(f19, f19, iCenterX, iCenterY3);
                }
                float fDp113 = AndroidUtilities.dp(7.0f) * this.scale;
                float f911112 = iCenterX;
                float f911113 = f911112 - fDp113;
                float f911114 = iCenterY3;
                float f911115 = f911114 - fDp113;
                float f101110 = f911112 + fDp113;
                float f101111 = fDp113 + f911114;
                f20 = f17;
                i11 = i9;
                canvas3.drawLine(f911113, f911115, f101110, f101111, this.paint);
                canvas3 = canvas;
                canvas3.drawLine(f101110, f911115, f911113, f101111, this.paint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            } else {
                applyShaderMatrix(false);
                i7 = this.currentIcon;
                i8 = this.nextIcon;
                if (i7 == i8) {
                    i9 = 13;
                    f19 = 1.0f;
                } else {
                    f19 = this.transitionProgress;
                    i9 = 13;
                    if (i8 != 13) {
                        f19 = 1.0f - f19;
                    }
                }
                Paint paint110 = this.paint;
                if (i7 == i8) {
                    i10 = 255;
                } else {
                    i10 = (int) (f19 * 255.0f);
                }
                paint110.setAlpha(i10);
                AndroidUtilities.dp(7.0f);
                AndroidUtilities.dp(3.0f);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    canvas3.scale(f19, f19, iCenterX, iCenterY3);
                }
                float fDp114 = AndroidUtilities.dp(7.0f) * this.scale;
                float f911116 = iCenterX;
                float f911117 = f911116 - fDp114;
                float f911118 = iCenterY3;
                float f911119 = f911118 - fDp114;
                float f101112 = f911116 + fDp114;
                float f101113 = fDp114 + f911118;
                f20 = f17;
                i11 = i9;
                canvas3.drawLine(f911117, f911119, f101112, f101113, this.paint);
                canvas3 = canvas;
                canvas3.drawLine(f101112, f911119, f911117, f101113, this.paint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            }
            if (this.currentIcon != i11) {
                applyShaderMatrix(false);
                i12 = this.currentIcon;
                i13 = this.nextIcon;
                if (i12 == i13) {
                    f21 = 1.0f;
                } else {
                    f21 = this.transitionProgress;
                    if (i13 != i11) {
                        f21 = 1.0f - f21;
                    }
                }
                this.textPaint.setAlpha((int) (f21 * 255.0f));
                int iDp1111 = AndroidUtilities.dp(5.0f) + iCenterY3;
                int i516 = iCenterX - (this.percentStringWidth / 2);
                f22 = 5.0f;
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    canvas3.scale(f21, f21, iCenterX, iCenterY3);
                }
                i14 = (int) (this.animatedDownloadProgress * 100.0f);
                if (this.percentString != null) {
                    this.lastPercent = i14;
                    String str10 = String.format("%d%%", Integer.valueOf(i14));
                    this.percentString = str10;
                    this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str10));
                } else {
                    this.lastPercent = i14;
                    String str11 = String.format("%d%%", Integer.valueOf(i14));
                    this.percentString = str11;
                    this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str11));
                }
                canvas3.drawText(this.percentString, i516, iDp1111, this.textPaint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            } else {
                applyShaderMatrix(false);
                i12 = this.currentIcon;
                i13 = this.nextIcon;
                if (i12 == i13) {
                    f21 = 1.0f;
                } else {
                    f21 = this.transitionProgress;
                    if (i13 != i11) {
                        f21 = 1.0f - f21;
                    }
                }
                this.textPaint.setAlpha((int) (f21 * 255.0f));
                int iDp1112 = AndroidUtilities.dp(5.0f) + iCenterY3;
                int i517 = iCenterX - (this.percentStringWidth / 2);
                f22 = 5.0f;
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    canvas3.scale(f21, f21, iCenterX, iCenterY3);
                }
                i14 = (int) (this.animatedDownloadProgress * 100.0f);
                if (this.percentString != null) {
                    this.lastPercent = i14;
                    String str12 = String.format("%d%%", Integer.valueOf(i14));
                    this.percentString = str12;
                    this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str12));
                } else {
                    this.lastPercent = i14;
                    String str13 = String.format("%d%%", Integer.valueOf(i14));
                    this.percentString = str13;
                    this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str13));
                }
                canvas3.drawText(this.percentString, i517, iDp1112, this.textPaint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            }
            i15 = this.currentIcon;
            if (i15 != 0) {
                if (i15 == 0) {
                    if (i15 != 1) {
                        if (this.nextIcon == 0) {
                            z2 = this.animatingTransition;
                            i20 = this.nextIcon;
                            if (z2) {
                                f26 = this.transitionProgress;
                                if (i20 == 0) {
                                    f26 = 1.0f - f26;
                                }
                                f23 = f26;
                                i16 = 1;
                            } else {
                                i16 = 1;
                                if (i20 == 1) {
                                    f23 = 1.0f;
                                } else {
                                    f23 = 0.0f;
                                }
                            }
                        } else {
                            i16 = 1;
                        }
                        i17 = this.nextIcon;
                        if (i17 == 0) {
                            this.paint2.setAlpha(255);
                        } else {
                            this.paint2.setAlpha(255);
                        }
                        applyShaderMatrix(true);
                        canvas3.save();
                        canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                        f24 = f23 * 500.0f;
                        i18 = this.currentIcon;
                        if (i18 == 1) {
                            f25 = 90.0f;
                        } else {
                            f25 = 0.0f;
                        }
                        if (i18 == 0) {
                            if (i18 == 1) {
                                if (f24 < 100.0f) {
                                    interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                                } else if (f24 < 484.0f) {
                                    interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                                }
                                f25 = interpolation;
                            }
                        } else if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                        canvas3.rotate(f25);
                        i19 = this.currentIcon;
                        if (i19 == 0) {
                            canvas3.scale(f18, f18);
                        } else {
                            canvas3.scale(f18, f18);
                        }
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.scale(1.0f, -1.0f);
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.restore();
                    } else {
                        i16 = 1;
                    }
                    if (i15 == i16) {
                        f23 = 1.0f;
                    } else {
                        f23 = 0.0f;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                } else {
                    if (i15 != 1) {
                        if (this.nextIcon == 0) {
                            z2 = this.animatingTransition;
                            i20 = this.nextIcon;
                            if (z2) {
                                f26 = this.transitionProgress;
                                if (i20 == 0) {
                                    f26 = 1.0f - f26;
                                }
                                f23 = f26;
                                i16 = 1;
                            } else {
                                i16 = 1;
                                if (i20 == 1) {
                                    f23 = 1.0f;
                                } else {
                                    f23 = 0.0f;
                                }
                            }
                        } else {
                            i16 = 1;
                        }
                        i17 = this.nextIcon;
                        if (i17 == 0) {
                            this.paint2.setAlpha(255);
                        } else {
                            this.paint2.setAlpha(255);
                        }
                        applyShaderMatrix(true);
                        canvas3.save();
                        canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                        f24 = f23 * 500.0f;
                        i18 = this.currentIcon;
                        if (i18 == 1) {
                            f25 = 90.0f;
                        } else {
                            f25 = 0.0f;
                        }
                        if (i18 == 0) {
                            if (i18 == 1) {
                                if (f24 < 100.0f) {
                                    interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                                } else if (f24 < 484.0f) {
                                    interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                                }
                                f25 = interpolation;
                            }
                        } else if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                        canvas3.rotate(f25);
                        i19 = this.currentIcon;
                        if (i19 == 0) {
                            canvas3.scale(f18, f18);
                        } else {
                            canvas3.scale(f18, f18);
                        }
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.scale(1.0f, -1.0f);
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.restore();
                    } else {
                        i16 = 1;
                    }
                    if (i15 == i16) {
                        f23 = 1.0f;
                    } else {
                        f23 = 0.0f;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                }
            } else if (i15 == 0) {
                if (i15 != 1) {
                    if (this.nextIcon == 0) {
                        z2 = this.animatingTransition;
                        i20 = this.nextIcon;
                        if (z2) {
                            f26 = this.transitionProgress;
                            if (i20 == 0) {
                                f26 = 1.0f - f26;
                            }
                            f23 = f26;
                            i16 = 1;
                        } else {
                            i16 = 1;
                            if (i20 == 1) {
                                f23 = 1.0f;
                            } else {
                                f23 = 0.0f;
                            }
                        }
                    } else {
                        i16 = 1;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                } else {
                    i16 = 1;
                }
                if (i15 == i16) {
                    f23 = 1.0f;
                } else {
                    f23 = 0.0f;
                }
                i17 = this.nextIcon;
                if (i17 == 0) {
                    this.paint2.setAlpha(255);
                } else {
                    this.paint2.setAlpha(255);
                }
                applyShaderMatrix(true);
                canvas3.save();
                canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                f24 = f23 * 500.0f;
                i18 = this.currentIcon;
                if (i18 == 1) {
                    f25 = 90.0f;
                } else {
                    f25 = 0.0f;
                }
                if (i18 == 0) {
                    if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                } else if (i18 == 1) {
                    if (f24 < 100.0f) {
                        interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                    } else if (f24 < 484.0f) {
                        interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                    }
                    f25 = interpolation;
                }
                canvas3.rotate(f25);
                i19 = this.currentIcon;
                if (i19 == 0) {
                    canvas3.scale(f18, f18);
                } else {
                    canvas3.scale(f18, f18);
                }
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.scale(1.0f, -1.0f);
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.restore();
            } else {
                if (i15 != 1) {
                    if (this.nextIcon == 0) {
                        z2 = this.animatingTransition;
                        i20 = this.nextIcon;
                        if (z2) {
                            f26 = this.transitionProgress;
                            if (i20 == 0) {
                                f26 = 1.0f - f26;
                            }
                            f23 = f26;
                            i16 = 1;
                        } else {
                            i16 = 1;
                            if (i20 == 1) {
                                f23 = 1.0f;
                            } else {
                                f23 = 0.0f;
                            }
                        }
                    } else {
                        i16 = 1;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                } else {
                    i16 = 1;
                }
                if (i15 == i16) {
                    f23 = 1.0f;
                } else {
                    f23 = 0.0f;
                }
                i17 = this.nextIcon;
                if (i17 == 0) {
                    this.paint2.setAlpha(255);
                } else {
                    this.paint2.setAlpha(255);
                }
                applyShaderMatrix(true);
                canvas3.save();
                canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                f24 = f23 * 500.0f;
                i18 = this.currentIcon;
                if (i18 == 1) {
                    f25 = 90.0f;
                } else {
                    f25 = 0.0f;
                }
                if (i18 == 0) {
                    if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                } else if (i18 == 1) {
                    if (f24 < 100.0f) {
                        interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                    } else if (f24 < 484.0f) {
                        interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                    }
                    f25 = interpolation;
                }
                canvas3.rotate(f25);
                i19 = this.currentIcon;
                if (i19 == 0) {
                    canvas3.scale(f18, f18);
                } else {
                    canvas3.scale(f18, f18);
                }
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.scale(1.0f, -1.0f);
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.restore();
            }
            if (this.currentIcon != 6) {
                applyShaderMatrix(false);
                if (this.currentIcon != 6) {
                    f28 = this.transitionProgress;
                    if (f28 > 0.5f) {
                        f30 = (f28 - 0.5f) / 0.5f;
                        fMin2 = 1.0f - Math.min(1.0f, f30 / 0.5f);
                        if (f30 > 0.5f) {
                            f29 = (f30 - 0.5f) / 0.5f;
                        } else {
                            f29 = 0.0f;
                        }
                    } else {
                        f29 = 0.0f;
                        fMin2 = 1.0f;
                    }
                    this.paint.setAlpha(255);
                    f27 = f29;
                } else {
                    i21 = this.nextIcon;
                    paint = this.paint;
                    if (i21 != 6) {
                        paint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f));
                    } else {
                        paint.setAlpha(255);
                    }
                    fMin2 = 0.0f;
                    f27 = 1.0f;
                }
                iDp = AndroidUtilities.dp(7.0f) + iCenterY3;
                iDp2 = iCenterX - AndroidUtilities.dp(3.0f);
                if (fMin2 < 1.0f) {
                    i22 = iDp2;
                    canvas.drawLine(iDp2 - AndroidUtilities.dp(6.0f), iDp - AndroidUtilities.dp(6.0f), iDp2 - (AndroidUtilities.dp(6.0f) * fMin2), iDp - (AndroidUtilities.dp(6.0f) * fMin2), this.paint);
                } else {
                    i22 = iDp2;
                }
                if (f27 > 0.0f) {
                    float f101114 = i22;
                    float f101115 = iDp;
                    canvas.drawLine(f101114, f101115, f101114 + (AndroidUtilities.dp(12.0f) * f27), f101115 - (AndroidUtilities.dp(12.0f) * f27), this.paint);
                }
            } else {
                applyShaderMatrix(false);
                if (this.currentIcon != 6) {
                    f28 = this.transitionProgress;
                    if (f28 > 0.5f) {
                        f30 = (f28 - 0.5f) / 0.5f;
                        fMin2 = 1.0f - Math.min(1.0f, f30 / 0.5f);
                        if (f30 > 0.5f) {
                            f29 = (f30 - 0.5f) / 0.5f;
                        } else {
                            f29 = 0.0f;
                        }
                    } else {
                        f29 = 0.0f;
                        fMin2 = 1.0f;
                    }
                    this.paint.setAlpha(255);
                    f27 = f29;
                } else {
                    i21 = this.nextIcon;
                    paint = this.paint;
                    if (i21 != 6) {
                        paint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f));
                    } else {
                        paint.setAlpha(255);
                    }
                    fMin2 = 0.0f;
                    f27 = 1.0f;
                }
                iDp = AndroidUtilities.dp(7.0f) + iCenterY3;
                iDp2 = iCenterX - AndroidUtilities.dp(3.0f);
                if (fMin2 < 1.0f) {
                    i22 = iDp2;
                    canvas.drawLine(iDp2 - AndroidUtilities.dp(6.0f), iDp - AndroidUtilities.dp(6.0f), iDp2 - (AndroidUtilities.dp(6.0f) * fMin2), iDp - (AndroidUtilities.dp(6.0f) * fMin2), this.paint);
                } else {
                    i22 = iDp2;
                }
                if (f27 > 0.0f) {
                    float f101116 = i22;
                    float f101117 = iDp;
                    canvas.drawLine(f101116, f101117, f101116 + (AndroidUtilities.dp(12.0f) * f27), f101117 - (AndroidUtilities.dp(12.0f) * f27), this.paint);
                }
            }
            if (drawable3 != null) {
                canvas2 = canvas;
                i23 = iCenterX;
                i24 = iCenterY3;
                f31 = f20;
            } else {
                canvas2 = canvas;
                i23 = iCenterX;
                i24 = iCenterY3;
                f31 = f20;
            }
            if (drawable != null) {
                i28 = this.currentIcon;
                i29 = this.nextIcon;
                if (i28 == i29) {
                    i30 = 255;
                } else {
                    i30 = (int) (this.transitionProgress * 255.0f);
                }
                if (isCustomFileIcon(i29)) {
                    f32 = f18;
                    drawTintedDrawable(canvas2, drawable, i23, i24, f32, i30);
                } else {
                    f32 = f18;
                    int intrinsicWidth5 = (int) (drawable.getIntrinsicWidth() * f32);
                    int intrinsicHeight5 = (int) (drawable.getIntrinsicHeight() * f32);
                    drawable.setColorFilter(this.colorFilter);
                    drawable.setAlpha(i30);
                    int i518 = intrinsicWidth5 / 2;
                    int i519 = intrinsicHeight5 / 2;
                    drawable.setBounds(i23 - i518, i24 - i519, i518 + i23, i519 + i24);
                    drawable.draw(canvas2);
                }
            } else {
                f32 = f18;
            }
            if (pathArr4 != null) {
                int iDp1113 = AndroidUtilities.dp(24.0f);
                this.paint2.setStyle(Paint.Style.FILL_AND_STROKE);
                Paint paint111 = this.paint2;
                if (this.currentIcon == this.nextIcon) {
                    i27 = 255;
                } else {
                    i27 = (int) ((1.0f - this.transitionProgress) * 255.0f);
                }
                paint111.setAlpha(i27);
                applyShaderMatrix(true);
                canvas2.save();
                canvas2.translate(i23, i24);
                canvas2.scale(f31, f31);
                float f101118 = (-iDp1113) / 2;
                canvas2.translate(f101118, f101118);
                path4 = pathArr4[0];
                if (path4 != null) {
                    canvas2.drawPath(path4, this.paint2);
                }
                path5 = pathArr4[1];
                if (path5 != null) {
                    canvas2.drawPath(path5, this.backPaint);
                }
                canvas2.restore();
            }
            if (pathArr3 != null) {
                int iDp1114 = AndroidUtilities.dp(24.0f);
                if (this.currentIcon == this.nextIcon) {
                    i26 = 255;
                } else {
                    i26 = (int) (this.transitionProgress * 255.0f);
                }
                this.paint2.setStyle(Paint.Style.FILL_AND_STROKE);
                this.paint2.setAlpha(i26);
                applyShaderMatrix(true);
                canvas2.save();
                canvas2.translate(i23, i24);
                canvas2.scale(f32, f32);
                float f101119 = (-iDp1114) / 2;
                canvas2.translate(f101119, f101119);
                path = pathArr3[0];
                if (path != null) {
                    canvas2.drawPath(path, this.paint2);
                }
                if (pathArr3.length >= 3) {
                    canvas2.drawPath(path3, this.paint);
                }
                path2 = pathArr3[1];
                if (path2 != null) {
                    paint2 = this.backPaint;
                    if (i26 != 255) {
                        int alpha4 = paint2.getAlpha();
                        this.backPaint.setAlpha((int) (alpha4 * (i26 / 255.0f)));
                        canvas2.drawPath(pathArr3[1], this.backPaint);
                        this.backPaint.setAlpha(alpha4);
                    } else {
                        canvas2.drawPath(path2, paint2);
                    }
                }
                canvas2.restore();
            }
            long jCurrentTimeMillis4 = System.currentTimeMillis();
            j = jCurrentTimeMillis4 - this.lastAnimationTime;
            if (j > 17) {
                j = 17;
            }
            this.lastAnimationTime = jCurrentTimeMillis4;
            i25 = this.currentIcon;
            if (i25 != 3) {
                float f10122 = this.downloadRadOffset + ((360 * j) / 2500.0f);
                this.downloadRadOffset = f10122;
                this.downloadRadOffset = getCircleValue(f10122);
                if (this.nextIcon != 2) {
                    f33 = this.downloadProgress;
                    f34 = this.downloadProgressAnimationStart;
                    f35 = f33 - f34;
                    if (f35 > 0.0f) {
                        f36 = this.downloadProgressTime + j;
                        this.downloadProgressTime = f36;
                        if (f36 >= 200.0f) {
                            this.animatedDownloadProgress = f33;
                            this.downloadProgressAnimationStart = f33;
                            this.downloadProgressTime = 0.0f;
                        } else {
                            this.animatedDownloadProgress = f34 + (f35 * this.interpolator.getInterpolation(f36 / 200.0f));
                        }
                    }
                }
                invalidateSelf();
            } else {
                float f10123 = this.downloadRadOffset + ((360 * j) / 2500.0f);
                this.downloadRadOffset = f10123;
                this.downloadRadOffset = getCircleValue(f10123);
                if (this.nextIcon != 2) {
                    f33 = this.downloadProgress;
                    f34 = this.downloadProgressAnimationStart;
                    f35 = f33 - f34;
                    if (f35 > 0.0f) {
                        f36 = this.downloadProgressTime + j;
                        this.downloadProgressTime = f36;
                        if (f36 >= 200.0f) {
                            this.animatedDownloadProgress = f33;
                            this.downloadProgressAnimationStart = f33;
                            this.downloadProgressTime = 0.0f;
                        } else {
                            this.animatedDownloadProgress = f34 + (f35 * this.interpolator.getInterpolation(f36 / 200.0f));
                        }
                    }
                }
                invalidateSelf();
            }
            if (this.animatingTransition) {
                f37 = this.transitionProgress;
                if (f37 < 1.0f) {
                    f38 = f37 + (j / this.transitionAnimationTime);
                    this.transitionProgress = f38;
                    if (f38 >= 1.0f) {
                        this.currentIcon = this.nextIcon;
                        this.transitionProgress = 1.0f;
                        this.animatingTransition = false;
                    }
                    invalidateSelf();
                }
            }
            if (i >= 1) {
                canvas2.restoreToCount(i);
            }
        }
        f39 = 1.0f;
        if (f40 != f39) {
            canvas3.save();
            canvas3.scale(f40, f40, fCenterX, f41);
        }
        if (f42 != f) {
            canvas3.save();
            canvas3.rotate(f42, iCenterX, iCenterY3);
        }
        if (iMin != 0) {
            f47 = iMin;
            this.paint.setAlpha((int) (this.overrideAlpha * f47));
            if (this.currentIcon != 14) {
                f45 = 4.0f;
                this.paint3.setAlpha((int) (f47 * this.overrideAlpha));
                this.rect.set(iCenterX - AndroidUtilities.dp(3.5f), iCenterY3 - AndroidUtilities.dp(3.5f), AndroidUtilities.dp(3.5f) + iCenterX, AndroidUtilities.dp(3.5f) + iCenterY3);
                canvas3.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), this.paint3);
            } else {
                f45 = 4.0f;
                this.paint3.setAlpha((int) (f47 * this.overrideAlpha));
                this.rect.set(iCenterX - AndroidUtilities.dp(3.5f), iCenterY3 - AndroidUtilities.dp(3.5f), AndroidUtilities.dp(3.5f) + iCenterX, AndroidUtilities.dp(3.5f) + iCenterY3);
                canvas3.drawRoundRect(this.rect, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), this.paint3);
            }
        } else {
            f45 = 4.0f;
        }
        if (f42 != f) {
            canvas3.restore();
        }
        if (f40 != f39) {
            canvas3.restore();
        }
        i33 = this.currentIcon;
        if (i33 != 3) {
            float fMax4 = Math.max(f45, this.animatedDownloadProgress * f92);
            if (this.isMini) {
                f46 = 2.0f;
            } else {
                f46 = f45;
            }
            int iDp120 = AndroidUtilities.dp(f46);
            this.rect.set(bounds.left + iDp120, bounds.top + iDp120, bounds.right - iDp120, bounds.bottom - iDp120);
            i34 = this.currentIcon;
            if (i34 != 14) {
                this.paint.setAlpha((int) (iMin * 0.15f * this.overrideAlpha));
                canvas3.drawArc(this.rect, 0.0f, 360.0f, false, this.paint);
                this.paint.setAlpha(iMin);
            } else {
                this.paint.setAlpha((int) (iMin * 0.15f * this.overrideAlpha));
                canvas3.drawArc(this.rect, 0.0f, 360.0f, false, this.paint);
                this.paint.setAlpha(iMin);
            }
            canvas3 = canvas;
            canvas3.drawArc(this.rect, this.downloadRadOffset, fMax4, false, this.paint);
        } else {
            float fMax5 = Math.max(f45, this.animatedDownloadProgress * f92);
            if (this.isMini) {
                f46 = 2.0f;
            } else {
                f46 = f45;
            }
            int iDp121 = AndroidUtilities.dp(f46);
            this.rect.set(bounds.left + iDp121, bounds.top + iDp121, bounds.right - iDp121, bounds.bottom - iDp121);
            i34 = this.currentIcon;
            if (i34 != 14) {
                this.paint.setAlpha((int) (iMin * 0.15f * this.overrideAlpha));
                canvas3.drawArc(this.rect, 0.0f, 360.0f, false, this.paint);
                this.paint.setAlpha(iMin);
            } else {
                this.paint.setAlpha((int) (iMin * 0.15f * this.overrideAlpha));
                canvas3.drawArc(this.rect, 0.0f, 360.0f, false, this.paint);
                this.paint.setAlpha(iMin);
            }
            canvas3 = canvas;
            canvas3.drawArc(this.rect, this.downloadRadOffset, fMax5, false, this.paint);
        }
        i3 = this.currentIcon;
        if (i3 == this.nextIcon) {
            f18 = 1.0f;
            f17 = 1.0f;
        } else {
            if (i3 != 4) {
                fMin = this.transitionProgress;
                fMax = 1.0f - fMin;
            } else {
                fMin = this.transitionProgress;
                fMax = 1.0f - fMin;
            }
            f17 = fMax;
            f18 = fMin;
        }
        i4 = this.nextIcon;
        if (i4 == i2) {
            pathArr = Theme.chat_updatePath;
        } else {
            if (this.currentIcon == i2) {
                pathArr2 = Theme.chat_updatePath;
                pathArr = null;
            } else {
                pathArr = null;
            }
            if (i4 == 5) {
                pathArr = Theme.chat_filePath;
            } else if (this.currentIcon == 5) {
                pathArr2 = Theme.chat_filePath;
            }
            pathArr3 = pathArr;
            pathArr4 = pathArr2;
            if (isCustomFileIcon(i4)) {
                customIconDrawable = getCustomIconDrawable(this.nextIcon);
            } else {
                customIconDrawable = null;
            }
            if (isCustomFileIcon(this.currentIcon)) {
                customIconDrawable2 = getCustomIconDrawable(this.currentIcon);
            } else {
                customIconDrawable2 = null;
            }
            i5 = this.nextIcon;
            if (i5 == 7) {
                customIconDrawable = Theme.chat_flameIcon;
            } else if (this.currentIcon == 7) {
                customIconDrawable2 = Theme.chat_flameIcon;
            }
            if (i5 == 8) {
                customIconDrawable = Theme.chat_gifIcon;
            } else if (this.currentIcon == 8) {
                customIconDrawable2 = Theme.chat_gifIcon;
            }
            drawable = customIconDrawable;
            drawable2 = customIconDrawable2;
            if (this.currentIcon != 9) {
                applyShaderMatrix(false);
                Paint paint112 = this.paint;
                if (this.currentIcon == this.nextIcon) {
                    i6 = 255;
                } else {
                    i6 = (int) (this.transitionProgress * 255.0f);
                }
                paint112.setAlpha(i6);
                int iDp1115 = AndroidUtilities.dp(7.0f) + iCenterY3;
                int iDp1116 = iCenterX - AndroidUtilities.dp(3.0f);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    float f9111110 = this.transitionProgress;
                    canvas3.scale(f9111110, f9111110, iCenterX, iCenterY3);
                }
                float f9111111 = iDp1116;
                float f91122 = iDp1115;
                rect = bounds;
                drawable3 = drawable2;
                canvas3.drawLine(iDp1116 - AndroidUtilities.dp(6.0f), iDp1115 - AndroidUtilities.dp(6.0f), f9111111, f91122, this.paint);
                canvas3 = canvas;
                canvas3.drawLine(f9111111, f91122, iDp1116 + AndroidUtilities.dp(12.0f), iDp1115 - AndroidUtilities.dp(12.0f), this.paint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            } else {
                applyShaderMatrix(false);
                Paint paint113 = this.paint;
                if (this.currentIcon == this.nextIcon) {
                    i6 = 255;
                } else {
                    i6 = (int) (this.transitionProgress * 255.0f);
                }
                paint113.setAlpha(i6);
                int iDp1117 = AndroidUtilities.dp(7.0f) + iCenterY3;
                int iDp1118 = iCenterX - AndroidUtilities.dp(3.0f);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    float f9111112 = this.transitionProgress;
                    canvas3.scale(f9111112, f9111112, iCenterX, iCenterY3);
                }
                float f9111113 = iDp1118;
                float f91123 = iDp1117;
                rect = bounds;
                drawable3 = drawable2;
                canvas3.drawLine(iDp1118 - AndroidUtilities.dp(6.0f), iDp1117 - AndroidUtilities.dp(6.0f), f9111113, f91123, this.paint);
                canvas3 = canvas;
                canvas3.drawLine(f9111113, f91123, iDp1118 + AndroidUtilities.dp(12.0f), iDp1117 - AndroidUtilities.dp(12.0f), this.paint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            }
            if (this.currentIcon != 12) {
                applyShaderMatrix(false);
                i7 = this.currentIcon;
                i8 = this.nextIcon;
                if (i7 == i8) {
                    i9 = 13;
                    f19 = 1.0f;
                } else {
                    f19 = this.transitionProgress;
                    i9 = 13;
                    if (i8 != 13) {
                        f19 = 1.0f - f19;
                    }
                }
                Paint paint114 = this.paint;
                if (i7 == i8) {
                    i10 = 255;
                } else {
                    i10 = (int) (f19 * 255.0f);
                }
                paint114.setAlpha(i10);
                AndroidUtilities.dp(7.0f);
                AndroidUtilities.dp(3.0f);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    canvas3.scale(f19, f19, iCenterX, iCenterY3);
                }
                float fDp115 = AndroidUtilities.dp(7.0f) * this.scale;
                float f9111114 = iCenterX;
                float f9111115 = f9111114 - fDp115;
                float f9111116 = iCenterY3;
                float f9111117 = f9111116 - fDp115;
                float f1011110 = f9111114 + fDp115;
                float f1011111 = fDp115 + f9111116;
                f20 = f17;
                i11 = i9;
                canvas3.drawLine(f9111115, f9111117, f1011110, f1011111, this.paint);
                canvas3 = canvas;
                canvas3.drawLine(f1011110, f9111117, f9111115, f1011111, this.paint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            } else {
                applyShaderMatrix(false);
                i7 = this.currentIcon;
                i8 = this.nextIcon;
                if (i7 == i8) {
                    i9 = 13;
                    f19 = 1.0f;
                } else {
                    f19 = this.transitionProgress;
                    i9 = 13;
                    if (i8 != 13) {
                        f19 = 1.0f - f19;
                    }
                }
                Paint paint115 = this.paint;
                if (i7 == i8) {
                    i10 = 255;
                } else {
                    i10 = (int) (f19 * 255.0f);
                }
                paint115.setAlpha(i10);
                AndroidUtilities.dp(7.0f);
                AndroidUtilities.dp(3.0f);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    canvas3.scale(f19, f19, iCenterX, iCenterY3);
                }
                float fDp116 = AndroidUtilities.dp(7.0f) * this.scale;
                float f9111118 = iCenterX;
                float f9111119 = f9111118 - fDp116;
                float f91111110 = iCenterY3;
                float f91111111 = f91111110 - fDp116;
                float f1011112 = f9111118 + fDp116;
                float f1011113 = fDp116 + f91111110;
                f20 = f17;
                i11 = i9;
                canvas3.drawLine(f9111119, f91111111, f1011112, f1011113, this.paint);
                canvas3 = canvas;
                canvas3.drawLine(f1011112, f91111111, f9111119, f1011113, this.paint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            }
            if (this.currentIcon != i11) {
                applyShaderMatrix(false);
                i12 = this.currentIcon;
                i13 = this.nextIcon;
                if (i12 == i13) {
                    f21 = 1.0f;
                } else {
                    f21 = this.transitionProgress;
                    if (i13 != i11) {
                        f21 = 1.0f - f21;
                    }
                }
                this.textPaint.setAlpha((int) (f21 * 255.0f));
                int iDp1119 = AndroidUtilities.dp(5.0f) + iCenterY3;
                int i5110 = iCenterX - (this.percentStringWidth / 2);
                f22 = 5.0f;
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    canvas3.scale(f21, f21, iCenterX, iCenterY3);
                }
                i14 = (int) (this.animatedDownloadProgress * 100.0f);
                if (this.percentString != null) {
                    this.lastPercent = i14;
                    String str14 = String.format("%d%%", Integer.valueOf(i14));
                    this.percentString = str14;
                    this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str14));
                } else {
                    this.lastPercent = i14;
                    String str15 = String.format("%d%%", Integer.valueOf(i14));
                    this.percentString = str15;
                    this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str15));
                }
                canvas3.drawText(this.percentString, i5110, iDp1119, this.textPaint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            } else {
                applyShaderMatrix(false);
                i12 = this.currentIcon;
                i13 = this.nextIcon;
                if (i12 == i13) {
                    f21 = 1.0f;
                } else {
                    f21 = this.transitionProgress;
                    if (i13 != i11) {
                        f21 = 1.0f - f21;
                    }
                }
                this.textPaint.setAlpha((int) (f21 * 255.0f));
                int iDp11110 = AndroidUtilities.dp(5.0f) + iCenterY3;
                int i5111 = iCenterX - (this.percentStringWidth / 2);
                f22 = 5.0f;
                if (this.currentIcon != this.nextIcon) {
                    canvas3.save();
                    canvas3.scale(f21, f21, iCenterX, iCenterY3);
                }
                i14 = (int) (this.animatedDownloadProgress * 100.0f);
                if (this.percentString != null) {
                    this.lastPercent = i14;
                    String str16 = String.format("%d%%", Integer.valueOf(i14));
                    this.percentString = str16;
                    this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str16));
                } else {
                    this.lastPercent = i14;
                    String str17 = String.format("%d%%", Integer.valueOf(i14));
                    this.percentString = str17;
                    this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str17));
                }
                canvas3.drawText(this.percentString, i5111, iDp11110, this.textPaint);
                if (this.currentIcon != this.nextIcon) {
                    canvas3.restore();
                }
            }
            i15 = this.currentIcon;
            if (i15 != 0) {
                if (i15 == 0) {
                    if (i15 != 1) {
                        if (this.nextIcon == 0) {
                            z2 = this.animatingTransition;
                            i20 = this.nextIcon;
                            if (z2) {
                                f26 = this.transitionProgress;
                                if (i20 == 0) {
                                    f26 = 1.0f - f26;
                                }
                                f23 = f26;
                                i16 = 1;
                            } else {
                                i16 = 1;
                                if (i20 == 1) {
                                    f23 = 1.0f;
                                } else {
                                    f23 = 0.0f;
                                }
                            }
                        } else {
                            i16 = 1;
                        }
                        i17 = this.nextIcon;
                        if (i17 == 0) {
                            this.paint2.setAlpha(255);
                        } else {
                            this.paint2.setAlpha(255);
                        }
                        applyShaderMatrix(true);
                        canvas3.save();
                        canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                        f24 = f23 * 500.0f;
                        i18 = this.currentIcon;
                        if (i18 == 1) {
                            f25 = 90.0f;
                        } else {
                            f25 = 0.0f;
                        }
                        if (i18 == 0) {
                            if (i18 == 1) {
                                if (f24 < 100.0f) {
                                    interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                                } else if (f24 < 484.0f) {
                                    interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                                }
                                f25 = interpolation;
                            }
                        } else if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                        canvas3.rotate(f25);
                        i19 = this.currentIcon;
                        if (i19 == 0) {
                            canvas3.scale(f18, f18);
                        } else {
                            canvas3.scale(f18, f18);
                        }
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.scale(1.0f, -1.0f);
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.restore();
                    } else {
                        i16 = 1;
                    }
                    if (i15 == i16) {
                        f23 = 1.0f;
                    } else {
                        f23 = 0.0f;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                } else {
                    if (i15 != 1) {
                        if (this.nextIcon == 0) {
                            z2 = this.animatingTransition;
                            i20 = this.nextIcon;
                            if (z2) {
                                f26 = this.transitionProgress;
                                if (i20 == 0) {
                                    f26 = 1.0f - f26;
                                }
                                f23 = f26;
                                i16 = 1;
                            } else {
                                i16 = 1;
                                if (i20 == 1) {
                                    f23 = 1.0f;
                                } else {
                                    f23 = 0.0f;
                                }
                            }
                        } else {
                            i16 = 1;
                        }
                        i17 = this.nextIcon;
                        if (i17 == 0) {
                            this.paint2.setAlpha(255);
                        } else {
                            this.paint2.setAlpha(255);
                        }
                        applyShaderMatrix(true);
                        canvas3.save();
                        canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                        f24 = f23 * 500.0f;
                        i18 = this.currentIcon;
                        if (i18 == 1) {
                            f25 = 90.0f;
                        } else {
                            f25 = 0.0f;
                        }
                        if (i18 == 0) {
                            if (i18 == 1) {
                                if (f24 < 100.0f) {
                                    interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                                } else if (f24 < 484.0f) {
                                    interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                                }
                                f25 = interpolation;
                            }
                        } else if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                        canvas3.rotate(f25);
                        i19 = this.currentIcon;
                        if (i19 == 0) {
                            canvas3.scale(f18, f18);
                        } else {
                            canvas3.scale(f18, f18);
                        }
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.scale(1.0f, -1.0f);
                        Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                        canvas3.restore();
                    } else {
                        i16 = 1;
                    }
                    if (i15 == i16) {
                        f23 = 1.0f;
                    } else {
                        f23 = 0.0f;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                }
            } else if (i15 == 0) {
                if (i15 != 1) {
                    if (this.nextIcon == 0) {
                        z2 = this.animatingTransition;
                        i20 = this.nextIcon;
                        if (z2) {
                            f26 = this.transitionProgress;
                            if (i20 == 0) {
                                f26 = 1.0f - f26;
                            }
                            f23 = f26;
                            i16 = 1;
                        } else {
                            i16 = 1;
                            if (i20 == 1) {
                                f23 = 1.0f;
                            } else {
                                f23 = 0.0f;
                            }
                        }
                    } else {
                        i16 = 1;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                } else {
                    i16 = 1;
                }
                if (i15 == i16) {
                    f23 = 1.0f;
                } else {
                    f23 = 0.0f;
                }
                i17 = this.nextIcon;
                if (i17 == 0) {
                    this.paint2.setAlpha(255);
                } else {
                    this.paint2.setAlpha(255);
                }
                applyShaderMatrix(true);
                canvas3.save();
                canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                f24 = f23 * 500.0f;
                i18 = this.currentIcon;
                if (i18 == 1) {
                    f25 = 90.0f;
                } else {
                    f25 = 0.0f;
                }
                if (i18 == 0) {
                    if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                } else if (i18 == 1) {
                    if (f24 < 100.0f) {
                        interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                    } else if (f24 < 484.0f) {
                        interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                    }
                    f25 = interpolation;
                }
                canvas3.rotate(f25);
                i19 = this.currentIcon;
                if (i19 == 0) {
                    canvas3.scale(f18, f18);
                } else {
                    canvas3.scale(f18, f18);
                }
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.scale(1.0f, -1.0f);
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.restore();
            } else {
                if (i15 != 1) {
                    if (this.nextIcon == 0) {
                        z2 = this.animatingTransition;
                        i20 = this.nextIcon;
                        if (z2) {
                            f26 = this.transitionProgress;
                            if (i20 == 0) {
                                f26 = 1.0f - f26;
                            }
                            f23 = f26;
                            i16 = 1;
                        } else {
                            i16 = 1;
                            if (i20 == 1) {
                                f23 = 1.0f;
                            } else {
                                f23 = 0.0f;
                            }
                        }
                    } else {
                        i16 = 1;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                } else {
                    i16 = 1;
                }
                if (i15 == i16) {
                    f23 = 1.0f;
                } else {
                    f23 = 0.0f;
                }
                i17 = this.nextIcon;
                if (i17 == 0) {
                    this.paint2.setAlpha(255);
                } else {
                    this.paint2.setAlpha(255);
                }
                applyShaderMatrix(true);
                canvas3.save();
                canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                f24 = f23 * 500.0f;
                i18 = this.currentIcon;
                if (i18 == 1) {
                    f25 = 90.0f;
                } else {
                    f25 = 0.0f;
                }
                if (i18 == 0) {
                    if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                } else if (i18 == 1) {
                    if (f24 < 100.0f) {
                        interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                    } else if (f24 < 484.0f) {
                        interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                    }
                    f25 = interpolation;
                }
                canvas3.rotate(f25);
                i19 = this.currentIcon;
                if (i19 == 0) {
                    canvas3.scale(f18, f18);
                } else {
                    canvas3.scale(f18, f18);
                }
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.scale(1.0f, -1.0f);
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.restore();
            }
            if (this.currentIcon != 6) {
                applyShaderMatrix(false);
                if (this.currentIcon != 6) {
                    f28 = this.transitionProgress;
                    if (f28 > 0.5f) {
                        f30 = (f28 - 0.5f) / 0.5f;
                        fMin2 = 1.0f - Math.min(1.0f, f30 / 0.5f);
                        if (f30 > 0.5f) {
                            f29 = (f30 - 0.5f) / 0.5f;
                        } else {
                            f29 = 0.0f;
                        }
                    } else {
                        f29 = 0.0f;
                        fMin2 = 1.0f;
                    }
                    this.paint.setAlpha(255);
                    f27 = f29;
                } else {
                    i21 = this.nextIcon;
                    paint = this.paint;
                    if (i21 != 6) {
                        paint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f));
                    } else {
                        paint.setAlpha(255);
                    }
                    fMin2 = 0.0f;
                    f27 = 1.0f;
                }
                iDp = AndroidUtilities.dp(7.0f) + iCenterY3;
                iDp2 = iCenterX - AndroidUtilities.dp(3.0f);
                if (fMin2 < 1.0f) {
                    i22 = iDp2;
                    canvas.drawLine(iDp2 - AndroidUtilities.dp(6.0f), iDp - AndroidUtilities.dp(6.0f), iDp2 - (AndroidUtilities.dp(6.0f) * fMin2), iDp - (AndroidUtilities.dp(6.0f) * fMin2), this.paint);
                } else {
                    i22 = iDp2;
                }
                if (f27 > 0.0f) {
                    float f1011114 = i22;
                    float f1011115 = iDp;
                    canvas.drawLine(f1011114, f1011115, f1011114 + (AndroidUtilities.dp(12.0f) * f27), f1011115 - (AndroidUtilities.dp(12.0f) * f27), this.paint);
                }
            } else {
                applyShaderMatrix(false);
                if (this.currentIcon != 6) {
                    f28 = this.transitionProgress;
                    if (f28 > 0.5f) {
                        f30 = (f28 - 0.5f) / 0.5f;
                        fMin2 = 1.0f - Math.min(1.0f, f30 / 0.5f);
                        if (f30 > 0.5f) {
                            f29 = (f30 - 0.5f) / 0.5f;
                        } else {
                            f29 = 0.0f;
                        }
                    } else {
                        f29 = 0.0f;
                        fMin2 = 1.0f;
                    }
                    this.paint.setAlpha(255);
                    f27 = f29;
                } else {
                    i21 = this.nextIcon;
                    paint = this.paint;
                    if (i21 != 6) {
                        paint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f));
                    } else {
                        paint.setAlpha(255);
                    }
                    fMin2 = 0.0f;
                    f27 = 1.0f;
                }
                iDp = AndroidUtilities.dp(7.0f) + iCenterY3;
                iDp2 = iCenterX - AndroidUtilities.dp(3.0f);
                if (fMin2 < 1.0f) {
                    i22 = iDp2;
                    canvas.drawLine(iDp2 - AndroidUtilities.dp(6.0f), iDp - AndroidUtilities.dp(6.0f), iDp2 - (AndroidUtilities.dp(6.0f) * fMin2), iDp - (AndroidUtilities.dp(6.0f) * fMin2), this.paint);
                } else {
                    i22 = iDp2;
                }
                if (f27 > 0.0f) {
                    float f1011116 = i22;
                    float f1011117 = iDp;
                    canvas.drawLine(f1011116, f1011117, f1011116 + (AndroidUtilities.dp(12.0f) * f27), f1011117 - (AndroidUtilities.dp(12.0f) * f27), this.paint);
                }
            }
            if (drawable3 != null) {
                canvas2 = canvas;
                i23 = iCenterX;
                i24 = iCenterY3;
                f31 = f20;
            } else {
                canvas2 = canvas;
                i23 = iCenterX;
                i24 = iCenterY3;
                f31 = f20;
            }
            if (drawable != null) {
                i28 = this.currentIcon;
                i29 = this.nextIcon;
                if (i28 == i29) {
                    i30 = 255;
                } else {
                    i30 = (int) (this.transitionProgress * 255.0f);
                }
                if (isCustomFileIcon(i29)) {
                    f32 = f18;
                    drawTintedDrawable(canvas2, drawable, i23, i24, f32, i30);
                } else {
                    f32 = f18;
                    int intrinsicWidth6 = (int) (drawable.getIntrinsicWidth() * f32);
                    int intrinsicHeight6 = (int) (drawable.getIntrinsicHeight() * f32);
                    drawable.setColorFilter(this.colorFilter);
                    drawable.setAlpha(i30);
                    int i5112 = intrinsicWidth6 / 2;
                    int i5113 = intrinsicHeight6 / 2;
                    drawable.setBounds(i23 - i5112, i24 - i5113, i5112 + i23, i5113 + i24);
                    drawable.draw(canvas2);
                }
            } else {
                f32 = f18;
            }
            if (pathArr4 != null) {
                int iDp11111 = AndroidUtilities.dp(24.0f);
                this.paint2.setStyle(Paint.Style.FILL_AND_STROKE);
                Paint paint116 = this.paint2;
                if (this.currentIcon == this.nextIcon) {
                    i27 = 255;
                } else {
                    i27 = (int) ((1.0f - this.transitionProgress) * 255.0f);
                }
                paint116.setAlpha(i27);
                applyShaderMatrix(true);
                canvas2.save();
                canvas2.translate(i23, i24);
                canvas2.scale(f31, f31);
                float f1011118 = (-iDp11111) / 2;
                canvas2.translate(f1011118, f1011118);
                path4 = pathArr4[0];
                if (path4 != null) {
                    canvas2.drawPath(path4, this.paint2);
                }
                path5 = pathArr4[1];
                if (path5 != null) {
                    canvas2.drawPath(path5, this.backPaint);
                }
                canvas2.restore();
            }
            if (pathArr3 != null) {
                int iDp11112 = AndroidUtilities.dp(24.0f);
                if (this.currentIcon == this.nextIcon) {
                    i26 = 255;
                } else {
                    i26 = (int) (this.transitionProgress * 255.0f);
                }
                this.paint2.setStyle(Paint.Style.FILL_AND_STROKE);
                this.paint2.setAlpha(i26);
                applyShaderMatrix(true);
                canvas2.save();
                canvas2.translate(i23, i24);
                canvas2.scale(f32, f32);
                float f1011119 = (-iDp11112) / 2;
                canvas2.translate(f1011119, f1011119);
                path = pathArr3[0];
                if (path != null) {
                    canvas2.drawPath(path, this.paint2);
                }
                if (pathArr3.length >= 3) {
                    canvas2.drawPath(path3, this.paint);
                }
                path2 = pathArr3[1];
                if (path2 != null) {
                    paint2 = this.backPaint;
                    if (i26 != 255) {
                        int alpha5 = paint2.getAlpha();
                        this.backPaint.setAlpha((int) (alpha5 * (i26 / 255.0f)));
                        canvas2.drawPath(pathArr3[1], this.backPaint);
                        this.backPaint.setAlpha(alpha5);
                    } else {
                        canvas2.drawPath(path2, paint2);
                    }
                }
                canvas2.restore();
            }
            long jCurrentTimeMillis5 = System.currentTimeMillis();
            j = jCurrentTimeMillis5 - this.lastAnimationTime;
            if (j > 17) {
                j = 17;
            }
            this.lastAnimationTime = jCurrentTimeMillis5;
            i25 = this.currentIcon;
            if (i25 != 3) {
                float f10124 = this.downloadRadOffset + ((360 * j) / 2500.0f);
                this.downloadRadOffset = f10124;
                this.downloadRadOffset = getCircleValue(f10124);
                if (this.nextIcon != 2) {
                    f33 = this.downloadProgress;
                    f34 = this.downloadProgressAnimationStart;
                    f35 = f33 - f34;
                    if (f35 > 0.0f) {
                        f36 = this.downloadProgressTime + j;
                        this.downloadProgressTime = f36;
                        if (f36 >= 200.0f) {
                            this.animatedDownloadProgress = f33;
                            this.downloadProgressAnimationStart = f33;
                            this.downloadProgressTime = 0.0f;
                        } else {
                            this.animatedDownloadProgress = f34 + (f35 * this.interpolator.getInterpolation(f36 / 200.0f));
                        }
                    }
                }
                invalidateSelf();
            } else {
                float f10125 = this.downloadRadOffset + ((360 * j) / 2500.0f);
                this.downloadRadOffset = f10125;
                this.downloadRadOffset = getCircleValue(f10125);
                if (this.nextIcon != 2) {
                    f33 = this.downloadProgress;
                    f34 = this.downloadProgressAnimationStart;
                    f35 = f33 - f34;
                    if (f35 > 0.0f) {
                        f36 = this.downloadProgressTime + j;
                        this.downloadProgressTime = f36;
                        if (f36 >= 200.0f) {
                            this.animatedDownloadProgress = f33;
                            this.downloadProgressAnimationStart = f33;
                            this.downloadProgressTime = 0.0f;
                        } else {
                            this.animatedDownloadProgress = f34 + (f35 * this.interpolator.getInterpolation(f36 / 200.0f));
                        }
                    }
                }
                invalidateSelf();
            }
            if (this.animatingTransition) {
                f37 = this.transitionProgress;
                if (f37 < 1.0f) {
                    f38 = f37 + (j / this.transitionAnimationTime);
                    this.transitionProgress = f38;
                    if (f38 >= 1.0f) {
                        this.currentIcon = this.nextIcon;
                        this.transitionProgress = 1.0f;
                        this.animatingTransition = false;
                    }
                    invalidateSelf();
                }
            }
            if (i >= 1) {
                canvas2.restoreToCount(i);
            }
        }
        pathArr2 = null;
        if (i4 == 5) {
            pathArr = Theme.chat_filePath;
        } else if (this.currentIcon == 5) {
            pathArr2 = Theme.chat_filePath;
        }
        pathArr3 = pathArr;
        pathArr4 = pathArr2;
        if (isCustomFileIcon(i4)) {
            customIconDrawable = getCustomIconDrawable(this.nextIcon);
        } else {
            customIconDrawable = null;
        }
        if (isCustomFileIcon(this.currentIcon)) {
            customIconDrawable2 = getCustomIconDrawable(this.currentIcon);
        } else {
            customIconDrawable2 = null;
        }
        i5 = this.nextIcon;
        if (i5 == 7) {
            customIconDrawable = Theme.chat_flameIcon;
        } else if (this.currentIcon == 7) {
            customIconDrawable2 = Theme.chat_flameIcon;
        }
        if (i5 == 8) {
            customIconDrawable = Theme.chat_gifIcon;
        } else if (this.currentIcon == 8) {
            customIconDrawable2 = Theme.chat_gifIcon;
        }
        drawable = customIconDrawable;
        drawable2 = customIconDrawable2;
        if (this.currentIcon != 9) {
            applyShaderMatrix(false);
            Paint paint117 = this.paint;
            if (this.currentIcon == this.nextIcon) {
                i6 = 255;
            } else {
                i6 = (int) (this.transitionProgress * 255.0f);
            }
            paint117.setAlpha(i6);
            int iDp11113 = AndroidUtilities.dp(7.0f) + iCenterY3;
            int iDp11114 = iCenterX - AndroidUtilities.dp(3.0f);
            if (this.currentIcon != this.nextIcon) {
                canvas3.save();
                float f91111112 = this.transitionProgress;
                canvas3.scale(f91111112, f91111112, iCenterX, iCenterY3);
            }
            float f91111113 = iDp11114;
            float f91124 = iDp11113;
            rect = bounds;
            drawable3 = drawable2;
            canvas3.drawLine(iDp11114 - AndroidUtilities.dp(6.0f), iDp11113 - AndroidUtilities.dp(6.0f), f91111113, f91124, this.paint);
            canvas3 = canvas;
            canvas3.drawLine(f91111113, f91124, iDp11114 + AndroidUtilities.dp(12.0f), iDp11113 - AndroidUtilities.dp(12.0f), this.paint);
            if (this.currentIcon != this.nextIcon) {
                canvas3.restore();
            }
        } else {
            applyShaderMatrix(false);
            Paint paint118 = this.paint;
            if (this.currentIcon == this.nextIcon) {
                i6 = 255;
            } else {
                i6 = (int) (this.transitionProgress * 255.0f);
            }
            paint118.setAlpha(i6);
            int iDp11115 = AndroidUtilities.dp(7.0f) + iCenterY3;
            int iDp11116 = iCenterX - AndroidUtilities.dp(3.0f);
            if (this.currentIcon != this.nextIcon) {
                canvas3.save();
                float f91111114 = this.transitionProgress;
                canvas3.scale(f91111114, f91111114, iCenterX, iCenterY3);
            }
            float f91111115 = iDp11116;
            float f91125 = iDp11115;
            rect = bounds;
            drawable3 = drawable2;
            canvas3.drawLine(iDp11116 - AndroidUtilities.dp(6.0f), iDp11115 - AndroidUtilities.dp(6.0f), f91111115, f91125, this.paint);
            canvas3 = canvas;
            canvas3.drawLine(f91111115, f91125, iDp11116 + AndroidUtilities.dp(12.0f), iDp11115 - AndroidUtilities.dp(12.0f), this.paint);
            if (this.currentIcon != this.nextIcon) {
                canvas3.restore();
            }
        }
        if (this.currentIcon != 12) {
            applyShaderMatrix(false);
            i7 = this.currentIcon;
            i8 = this.nextIcon;
            if (i7 == i8) {
                i9 = 13;
                f19 = 1.0f;
            } else {
                f19 = this.transitionProgress;
                i9 = 13;
                if (i8 != 13) {
                    f19 = 1.0f - f19;
                }
            }
            Paint paint119 = this.paint;
            if (i7 == i8) {
                i10 = 255;
            } else {
                i10 = (int) (f19 * 255.0f);
            }
            paint119.setAlpha(i10);
            AndroidUtilities.dp(7.0f);
            AndroidUtilities.dp(3.0f);
            if (this.currentIcon != this.nextIcon) {
                canvas3.save();
                canvas3.scale(f19, f19, iCenterX, iCenterY3);
            }
            float fDp117 = AndroidUtilities.dp(7.0f) * this.scale;
            float f91111116 = iCenterX;
            float f91111117 = f91111116 - fDp117;
            float f91111118 = iCenterY3;
            float f91111119 = f91111118 - fDp117;
            float f10111110 = f91111116 + fDp117;
            float f10111111 = fDp117 + f91111118;
            f20 = f17;
            i11 = i9;
            canvas3.drawLine(f91111117, f91111119, f10111110, f10111111, this.paint);
            canvas3 = canvas;
            canvas3.drawLine(f10111110, f91111119, f91111117, f10111111, this.paint);
            if (this.currentIcon != this.nextIcon) {
                canvas3.restore();
            }
        } else {
            applyShaderMatrix(false);
            i7 = this.currentIcon;
            i8 = this.nextIcon;
            if (i7 == i8) {
                i9 = 13;
                f19 = 1.0f;
            } else {
                f19 = this.transitionProgress;
                i9 = 13;
                if (i8 != 13) {
                    f19 = 1.0f - f19;
                }
            }
            Paint paint1110 = this.paint;
            if (i7 == i8) {
                i10 = 255;
            } else {
                i10 = (int) (f19 * 255.0f);
            }
            paint1110.setAlpha(i10);
            AndroidUtilities.dp(7.0f);
            AndroidUtilities.dp(3.0f);
            if (this.currentIcon != this.nextIcon) {
                canvas3.save();
                canvas3.scale(f19, f19, iCenterX, iCenterY3);
            }
            float fDp118 = AndroidUtilities.dp(7.0f) * this.scale;
            float f911111110 = iCenterX;
            float f911111111 = f911111110 - fDp118;
            float f911111112 = iCenterY3;
            float f911111113 = f911111112 - fDp118;
            float f10111112 = f911111110 + fDp118;
            float f10111113 = fDp118 + f911111112;
            f20 = f17;
            i11 = i9;
            canvas3.drawLine(f911111111, f911111113, f10111112, f10111113, this.paint);
            canvas3 = canvas;
            canvas3.drawLine(f10111112, f911111113, f911111111, f10111113, this.paint);
            if (this.currentIcon != this.nextIcon) {
                canvas3.restore();
            }
        }
        if (this.currentIcon != i11) {
            applyShaderMatrix(false);
            i12 = this.currentIcon;
            i13 = this.nextIcon;
            if (i12 == i13) {
                f21 = 1.0f;
            } else {
                f21 = this.transitionProgress;
                if (i13 != i11) {
                    f21 = 1.0f - f21;
                }
            }
            this.textPaint.setAlpha((int) (f21 * 255.0f));
            int iDp11117 = AndroidUtilities.dp(5.0f) + iCenterY3;
            int i5114 = iCenterX - (this.percentStringWidth / 2);
            f22 = 5.0f;
            if (this.currentIcon != this.nextIcon) {
                canvas3.save();
                canvas3.scale(f21, f21, iCenterX, iCenterY3);
            }
            i14 = (int) (this.animatedDownloadProgress * 100.0f);
            if (this.percentString != null) {
                this.lastPercent = i14;
                String str18 = String.format("%d%%", Integer.valueOf(i14));
                this.percentString = str18;
                this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str18));
            } else {
                this.lastPercent = i14;
                String str19 = String.format("%d%%", Integer.valueOf(i14));
                this.percentString = str19;
                this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str19));
            }
            canvas3.drawText(this.percentString, i5114, iDp11117, this.textPaint);
            if (this.currentIcon != this.nextIcon) {
                canvas3.restore();
            }
        } else {
            applyShaderMatrix(false);
            i12 = this.currentIcon;
            i13 = this.nextIcon;
            if (i12 == i13) {
                f21 = 1.0f;
            } else {
                f21 = this.transitionProgress;
                if (i13 != i11) {
                    f21 = 1.0f - f21;
                }
            }
            this.textPaint.setAlpha((int) (f21 * 255.0f));
            int iDp11118 = AndroidUtilities.dp(5.0f) + iCenterY3;
            int i5115 = iCenterX - (this.percentStringWidth / 2);
            f22 = 5.0f;
            if (this.currentIcon != this.nextIcon) {
                canvas3.save();
                canvas3.scale(f21, f21, iCenterX, iCenterY3);
            }
            i14 = (int) (this.animatedDownloadProgress * 100.0f);
            if (this.percentString != null) {
                this.lastPercent = i14;
                String str110 = String.format("%d%%", Integer.valueOf(i14));
                this.percentString = str110;
                this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str110));
            } else {
                this.lastPercent = i14;
                String str111 = String.format("%d%%", Integer.valueOf(i14));
                this.percentString = str111;
                this.percentStringWidth = (int) Math.ceil(this.textPaint.measureText(str111));
            }
            canvas3.drawText(this.percentString, i5115, iDp11118, this.textPaint);
            if (this.currentIcon != this.nextIcon) {
                canvas3.restore();
            }
        }
        i15 = this.currentIcon;
        if (i15 != 0) {
            if (i15 == 0) {
                if (i15 != 1) {
                    if (this.nextIcon == 0) {
                        z2 = this.animatingTransition;
                        i20 = this.nextIcon;
                        if (z2) {
                            f26 = this.transitionProgress;
                            if (i20 == 0) {
                                f26 = 1.0f - f26;
                            }
                            f23 = f26;
                            i16 = 1;
                        } else {
                            i16 = 1;
                            if (i20 == 1) {
                                f23 = 1.0f;
                            } else {
                                f23 = 0.0f;
                            }
                        }
                    } else {
                        i16 = 1;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                } else {
                    i16 = 1;
                }
                if (i15 == i16) {
                    f23 = 1.0f;
                } else {
                    f23 = 0.0f;
                }
                i17 = this.nextIcon;
                if (i17 == 0) {
                    this.paint2.setAlpha(255);
                } else {
                    this.paint2.setAlpha(255);
                }
                applyShaderMatrix(true);
                canvas3.save();
                canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                f24 = f23 * 500.0f;
                i18 = this.currentIcon;
                if (i18 == 1) {
                    f25 = 90.0f;
                } else {
                    f25 = 0.0f;
                }
                if (i18 == 0) {
                    if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                } else if (i18 == 1) {
                    if (f24 < 100.0f) {
                        interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                    } else if (f24 < 484.0f) {
                        interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                    }
                    f25 = interpolation;
                }
                canvas3.rotate(f25);
                i19 = this.currentIcon;
                if (i19 == 0) {
                    canvas3.scale(f18, f18);
                } else {
                    canvas3.scale(f18, f18);
                }
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.scale(1.0f, -1.0f);
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.restore();
            } else {
                if (i15 != 1) {
                    if (this.nextIcon == 0) {
                        z2 = this.animatingTransition;
                        i20 = this.nextIcon;
                        if (z2) {
                            f26 = this.transitionProgress;
                            if (i20 == 0) {
                                f26 = 1.0f - f26;
                            }
                            f23 = f26;
                            i16 = 1;
                        } else {
                            i16 = 1;
                            if (i20 == 1) {
                                f23 = 1.0f;
                            } else {
                                f23 = 0.0f;
                            }
                        }
                    } else {
                        i16 = 1;
                    }
                    i17 = this.nextIcon;
                    if (i17 == 0) {
                        this.paint2.setAlpha(255);
                    } else {
                        this.paint2.setAlpha(255);
                    }
                    applyShaderMatrix(true);
                    canvas3.save();
                    canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                    f24 = f23 * 500.0f;
                    i18 = this.currentIcon;
                    if (i18 == 1) {
                        f25 = 90.0f;
                    } else {
                        f25 = 0.0f;
                    }
                    if (i18 == 0) {
                        if (i18 == 1) {
                            if (f24 < 100.0f) {
                                interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                            } else if (f24 < 484.0f) {
                                interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                            }
                            f25 = interpolation;
                        }
                    } else if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                    canvas3.rotate(f25);
                    i19 = this.currentIcon;
                    if (i19 == 0) {
                        canvas3.scale(f18, f18);
                    } else {
                        canvas3.scale(f18, f18);
                    }
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.scale(1.0f, -1.0f);
                    Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                    canvas3.restore();
                } else {
                    i16 = 1;
                }
                if (i15 == i16) {
                    f23 = 1.0f;
                } else {
                    f23 = 0.0f;
                }
                i17 = this.nextIcon;
                if (i17 == 0) {
                    this.paint2.setAlpha(255);
                } else {
                    this.paint2.setAlpha(255);
                }
                applyShaderMatrix(true);
                canvas3.save();
                canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                f24 = f23 * 500.0f;
                i18 = this.currentIcon;
                if (i18 == 1) {
                    f25 = 90.0f;
                } else {
                    f25 = 0.0f;
                }
                if (i18 == 0) {
                    if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                } else if (i18 == 1) {
                    if (f24 < 100.0f) {
                        interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                    } else if (f24 < 484.0f) {
                        interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                    }
                    f25 = interpolation;
                }
                canvas3.rotate(f25);
                i19 = this.currentIcon;
                if (i19 == 0) {
                    canvas3.scale(f18, f18);
                } else {
                    canvas3.scale(f18, f18);
                }
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.scale(1.0f, -1.0f);
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.restore();
            }
        } else if (i15 == 0) {
            if (i15 != 1) {
                if (this.nextIcon == 0) {
                    z2 = this.animatingTransition;
                    i20 = this.nextIcon;
                    if (z2) {
                        f26 = this.transitionProgress;
                        if (i20 == 0) {
                            f26 = 1.0f - f26;
                        }
                        f23 = f26;
                        i16 = 1;
                    } else {
                        i16 = 1;
                        if (i20 == 1) {
                            f23 = 1.0f;
                        } else {
                            f23 = 0.0f;
                        }
                    }
                } else {
                    i16 = 1;
                }
                i17 = this.nextIcon;
                if (i17 == 0) {
                    this.paint2.setAlpha(255);
                } else {
                    this.paint2.setAlpha(255);
                }
                applyShaderMatrix(true);
                canvas3.save();
                canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                f24 = f23 * 500.0f;
                i18 = this.currentIcon;
                if (i18 == 1) {
                    f25 = 90.0f;
                } else {
                    f25 = 0.0f;
                }
                if (i18 == 0) {
                    if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                } else if (i18 == 1) {
                    if (f24 < 100.0f) {
                        interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                    } else if (f24 < 484.0f) {
                        interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                    }
                    f25 = interpolation;
                }
                canvas3.rotate(f25);
                i19 = this.currentIcon;
                if (i19 == 0) {
                    canvas3.scale(f18, f18);
                } else {
                    canvas3.scale(f18, f18);
                }
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.scale(1.0f, -1.0f);
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.restore();
            } else {
                i16 = 1;
            }
            if (i15 == i16) {
                f23 = 1.0f;
            } else {
                f23 = 0.0f;
            }
            i17 = this.nextIcon;
            if (i17 == 0) {
                this.paint2.setAlpha(255);
            } else {
                this.paint2.setAlpha(255);
            }
            applyShaderMatrix(true);
            canvas3.save();
            canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
            f24 = f23 * 500.0f;
            i18 = this.currentIcon;
            if (i18 == 1) {
                f25 = 90.0f;
            } else {
                f25 = 0.0f;
            }
            if (i18 == 0) {
                if (i18 == 1) {
                    if (f24 < 100.0f) {
                        interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                    } else if (f24 < 484.0f) {
                        interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                    }
                    f25 = interpolation;
                }
            } else if (i18 == 1) {
                if (f24 < 100.0f) {
                    interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                } else if (f24 < 484.0f) {
                    interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                }
                f25 = interpolation;
            }
            canvas3.rotate(f25);
            i19 = this.currentIcon;
            if (i19 == 0) {
                canvas3.scale(f18, f18);
            } else {
                canvas3.scale(f18, f18);
            }
            Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
            canvas3.scale(1.0f, -1.0f);
            Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
            canvas3.restore();
        } else {
            if (i15 != 1) {
                if (this.nextIcon == 0) {
                    z2 = this.animatingTransition;
                    i20 = this.nextIcon;
                    if (z2) {
                        f26 = this.transitionProgress;
                        if (i20 == 0) {
                            f26 = 1.0f - f26;
                        }
                        f23 = f26;
                        i16 = 1;
                    } else {
                        i16 = 1;
                        if (i20 == 1) {
                            f23 = 1.0f;
                        } else {
                            f23 = 0.0f;
                        }
                    }
                } else {
                    i16 = 1;
                }
                i17 = this.nextIcon;
                if (i17 == 0) {
                    this.paint2.setAlpha(255);
                } else {
                    this.paint2.setAlpha(255);
                }
                applyShaderMatrix(true);
                canvas3.save();
                canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
                f24 = f23 * 500.0f;
                i18 = this.currentIcon;
                if (i18 == 1) {
                    f25 = 90.0f;
                } else {
                    f25 = 0.0f;
                }
                if (i18 == 0) {
                    if (i18 == 1) {
                        if (f24 < 100.0f) {
                            interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                        } else if (f24 < 484.0f) {
                            interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                        }
                        f25 = interpolation;
                    }
                } else if (i18 == 1) {
                    if (f24 < 100.0f) {
                        interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                    } else if (f24 < 484.0f) {
                        interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                    }
                    f25 = interpolation;
                }
                canvas3.rotate(f25);
                i19 = this.currentIcon;
                if (i19 == 0) {
                    canvas3.scale(f18, f18);
                } else {
                    canvas3.scale(f18, f18);
                }
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.scale(1.0f, -1.0f);
                Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
                canvas3.restore();
            } else {
                i16 = 1;
            }
            if (i15 == i16) {
                f23 = 1.0f;
            } else {
                f23 = 0.0f;
            }
            i17 = this.nextIcon;
            if (i17 == 0) {
                this.paint2.setAlpha(255);
            } else {
                this.paint2.setAlpha(255);
            }
            applyShaderMatrix(true);
            canvas3.save();
            canvas3.translate(rect.centerX() + (AndroidUtilities.dp(1.0f) * (1.0f - f23)), rect.centerY());
            f24 = f23 * 500.0f;
            i18 = this.currentIcon;
            if (i18 == 1) {
                f25 = 90.0f;
            } else {
                f25 = 0.0f;
            }
            if (i18 == 0) {
                if (i18 == 1) {
                    if (f24 < 100.0f) {
                        interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                    } else if (f24 < 484.0f) {
                        interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                    }
                    f25 = interpolation;
                }
            } else if (i18 == 1) {
                if (f24 < 100.0f) {
                    interpolation = CubicBezierInterpolator.EASE_BOTH.getInterpolation(f24 / 100.0f) * (-5.0f);
                } else if (f24 < 484.0f) {
                    interpolation = (CubicBezierInterpolator.EASE_BOTH.getInterpolation((f24 - 100.0f) / 384.0f) * 95.0f) - 5.0f;
                }
                f25 = interpolation;
            }
            canvas3.rotate(f25);
            i19 = this.currentIcon;
            if (i19 == 0) {
                canvas3.scale(f18, f18);
            } else {
                canvas3.scale(f18, f18);
            }
            Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
            canvas3.scale(1.0f, -1.0f);
            Theme.playPauseAnimator.draw(canvas3, this.paint2, f24);
            canvas3.restore();
        }
        if (this.currentIcon != 6) {
            applyShaderMatrix(false);
            if (this.currentIcon != 6) {
                f28 = this.transitionProgress;
                if (f28 > 0.5f) {
                    f30 = (f28 - 0.5f) / 0.5f;
                    fMin2 = 1.0f - Math.min(1.0f, f30 / 0.5f);
                    if (f30 > 0.5f) {
                        f29 = (f30 - 0.5f) / 0.5f;
                    } else {
                        f29 = 0.0f;
                    }
                } else {
                    f29 = 0.0f;
                    fMin2 = 1.0f;
                }
                this.paint.setAlpha(255);
                f27 = f29;
            } else {
                i21 = this.nextIcon;
                paint = this.paint;
                if (i21 != 6) {
                    paint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f));
                } else {
                    paint.setAlpha(255);
                }
                fMin2 = 0.0f;
                f27 = 1.0f;
            }
            iDp = AndroidUtilities.dp(7.0f) + iCenterY3;
            iDp2 = iCenterX - AndroidUtilities.dp(3.0f);
            if (fMin2 < 1.0f) {
                i22 = iDp2;
                canvas.drawLine(iDp2 - AndroidUtilities.dp(6.0f), iDp - AndroidUtilities.dp(6.0f), iDp2 - (AndroidUtilities.dp(6.0f) * fMin2), iDp - (AndroidUtilities.dp(6.0f) * fMin2), this.paint);
            } else {
                i22 = iDp2;
            }
            if (f27 > 0.0f) {
                float f10111114 = i22;
                float f10111115 = iDp;
                canvas.drawLine(f10111114, f10111115, f10111114 + (AndroidUtilities.dp(12.0f) * f27), f10111115 - (AndroidUtilities.dp(12.0f) * f27), this.paint);
            }
        } else {
            applyShaderMatrix(false);
            if (this.currentIcon != 6) {
                f28 = this.transitionProgress;
                if (f28 > 0.5f) {
                    f30 = (f28 - 0.5f) / 0.5f;
                    fMin2 = 1.0f - Math.min(1.0f, f30 / 0.5f);
                    if (f30 > 0.5f) {
                        f29 = (f30 - 0.5f) / 0.5f;
                    } else {
                        f29 = 0.0f;
                    }
                } else {
                    f29 = 0.0f;
                    fMin2 = 1.0f;
                }
                this.paint.setAlpha(255);
                f27 = f29;
            } else {
                i21 = this.nextIcon;
                paint = this.paint;
                if (i21 != 6) {
                    paint.setAlpha((int) ((1.0f - this.transitionProgress) * 255.0f));
                } else {
                    paint.setAlpha(255);
                }
                fMin2 = 0.0f;
                f27 = 1.0f;
            }
            iDp = AndroidUtilities.dp(7.0f) + iCenterY3;
            iDp2 = iCenterX - AndroidUtilities.dp(3.0f);
            if (fMin2 < 1.0f) {
                i22 = iDp2;
                canvas.drawLine(iDp2 - AndroidUtilities.dp(6.0f), iDp - AndroidUtilities.dp(6.0f), iDp2 - (AndroidUtilities.dp(6.0f) * fMin2), iDp - (AndroidUtilities.dp(6.0f) * fMin2), this.paint);
            } else {
                i22 = iDp2;
            }
            if (f27 > 0.0f) {
                float f10111116 = i22;
                float f10111117 = iDp;
                canvas.drawLine(f10111116, f10111117, f10111116 + (AndroidUtilities.dp(12.0f) * f27), f10111117 - (AndroidUtilities.dp(12.0f) * f27), this.paint);
            }
        }
        if (drawable3 != null) {
            canvas2 = canvas;
            i23 = iCenterX;
            i24 = iCenterY3;
            f31 = f20;
        } else {
            canvas2 = canvas;
            i23 = iCenterX;
            i24 = iCenterY3;
            f31 = f20;
        }
        if (drawable != null) {
            i28 = this.currentIcon;
            i29 = this.nextIcon;
            if (i28 == i29) {
                i30 = 255;
            } else {
                i30 = (int) (this.transitionProgress * 255.0f);
            }
            if (isCustomFileIcon(i29)) {
                f32 = f18;
                drawTintedDrawable(canvas2, drawable, i23, i24, f32, i30);
            } else {
                f32 = f18;
                int intrinsicWidth7 = (int) (drawable.getIntrinsicWidth() * f32);
                int intrinsicHeight7 = (int) (drawable.getIntrinsicHeight() * f32);
                drawable.setColorFilter(this.colorFilter);
                drawable.setAlpha(i30);
                int i5116 = intrinsicWidth7 / 2;
                int i5117 = intrinsicHeight7 / 2;
                drawable.setBounds(i23 - i5116, i24 - i5117, i5116 + i23, i5117 + i24);
                drawable.draw(canvas2);
            }
        } else {
            f32 = f18;
        }
        if (pathArr4 != null) {
            int iDp11119 = AndroidUtilities.dp(24.0f);
            this.paint2.setStyle(Paint.Style.FILL_AND_STROKE);
            Paint paint1111 = this.paint2;
            if (this.currentIcon == this.nextIcon) {
                i27 = 255;
            } else {
                i27 = (int) ((1.0f - this.transitionProgress) * 255.0f);
            }
            paint1111.setAlpha(i27);
            applyShaderMatrix(true);
            canvas2.save();
            canvas2.translate(i23, i24);
            canvas2.scale(f31, f31);
            float f10111118 = (-iDp11119) / 2;
            canvas2.translate(f10111118, f10111118);
            path4 = pathArr4[0];
            if (path4 != null) {
                canvas2.drawPath(path4, this.paint2);
            }
            path5 = pathArr4[1];
            if (path5 != null) {
                canvas2.drawPath(path5, this.backPaint);
            }
            canvas2.restore();
        }
        if (pathArr3 != null) {
            int iDp111110 = AndroidUtilities.dp(24.0f);
            if (this.currentIcon == this.nextIcon) {
                i26 = 255;
            } else {
                i26 = (int) (this.transitionProgress * 255.0f);
            }
            this.paint2.setStyle(Paint.Style.FILL_AND_STROKE);
            this.paint2.setAlpha(i26);
            applyShaderMatrix(true);
            canvas2.save();
            canvas2.translate(i23, i24);
            canvas2.scale(f32, f32);
            float f10111119 = (-iDp111110) / 2;
            canvas2.translate(f10111119, f10111119);
            path = pathArr3[0];
            if (path != null) {
                canvas2.drawPath(path, this.paint2);
            }
            if (pathArr3.length >= 3) {
                canvas2.drawPath(path3, this.paint);
            }
            path2 = pathArr3[1];
            if (path2 != null) {
                paint2 = this.backPaint;
                if (i26 != 255) {
                    int alpha6 = paint2.getAlpha();
                    this.backPaint.setAlpha((int) (alpha6 * (i26 / 255.0f)));
                    canvas2.drawPath(pathArr3[1], this.backPaint);
                    this.backPaint.setAlpha(alpha6);
                } else {
                    canvas2.drawPath(path2, paint2);
                }
            }
            canvas2.restore();
        }
        long jCurrentTimeMillis6 = System.currentTimeMillis();
        j = jCurrentTimeMillis6 - this.lastAnimationTime;
        if (j > 17) {
            j = 17;
        }
        this.lastAnimationTime = jCurrentTimeMillis6;
        i25 = this.currentIcon;
        if (i25 != 3) {
            float f10126 = this.downloadRadOffset + ((360 * j) / 2500.0f);
            this.downloadRadOffset = f10126;
            this.downloadRadOffset = getCircleValue(f10126);
            if (this.nextIcon != 2) {
                f33 = this.downloadProgress;
                f34 = this.downloadProgressAnimationStart;
                f35 = f33 - f34;
                if (f35 > 0.0f) {
                    f36 = this.downloadProgressTime + j;
                    this.downloadProgressTime = f36;
                    if (f36 >= 200.0f) {
                        this.animatedDownloadProgress = f33;
                        this.downloadProgressAnimationStart = f33;
                        this.downloadProgressTime = 0.0f;
                    } else {
                        this.animatedDownloadProgress = f34 + (f35 * this.interpolator.getInterpolation(f36 / 200.0f));
                    }
                }
            }
            invalidateSelf();
        } else {
            float f10127 = this.downloadRadOffset + ((360 * j) / 2500.0f);
            this.downloadRadOffset = f10127;
            this.downloadRadOffset = getCircleValue(f10127);
            if (this.nextIcon != 2) {
                f33 = this.downloadProgress;
                f34 = this.downloadProgressAnimationStart;
                f35 = f33 - f34;
                if (f35 > 0.0f) {
                    f36 = this.downloadProgressTime + j;
                    this.downloadProgressTime = f36;
                    if (f36 >= 200.0f) {
                        this.animatedDownloadProgress = f33;
                        this.downloadProgressAnimationStart = f33;
                        this.downloadProgressTime = 0.0f;
                    } else {
                        this.animatedDownloadProgress = f34 + (f35 * this.interpolator.getInterpolation(f36 / 200.0f));
                    }
                }
            }
            invalidateSelf();
        }
        if (this.animatingTransition) {
            f37 = this.transitionProgress;
            if (f37 < 1.0f) {
                f38 = f37 + (j / this.transitionAnimationTime);
                this.transitionProgress = f38;
                if (f38 >= 1.0f) {
                    this.currentIcon = this.nextIcon;
                    this.transitionProgress = 1.0f;
                    this.animatingTransition = false;
                }
                invalidateSelf();
            }
        }
        if (i >= 1) {
            canvas2.restoreToCount(i);
        }
    }

    private Drawable getCustomIconDrawable(int i) {
        if (i == 16) {
            return Theme.chat_pluginIcon;
        }
        if (i == 17) {
            return Theme.chat_settingsIcon;
        }
        if (i == 18) {
            return Theme.chat_stickersIcon;
        }
        return PluginsController.getPluginFileIconDrawable(i);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return AndroidUtilities.dp(48.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return AndroidUtilities.dp(48.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumWidth() {
        return AndroidUtilities.dp(48.0f);
    }

    @Override // android.graphics.drawable.Drawable
    public int getMinimumHeight() {
        return AndroidUtilities.dp(48.0f);
    }
}
