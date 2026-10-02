package com.exteragram.messenger.utils.chats;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.exteragram.messenger.ExteraConfig;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MediaDataController;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.CubicBezierInterpolator;

/* JADX INFO: loaded from: classes4.dex */
public class SwipeActionsHelper {
    private float anchorY;
    private AnimatedEmojiDrawable animatedReaction;
    private long animatedReactionId;
    private boolean armed;
    private boolean beyondMax;
    private int direction;
    private float driftX;
    private float driftY;
    private final FloatValueHolder fill;
    private final SpringAnimation fillSpring;
    private boolean frameDark;
    private boolean frameGradient;
    private Paint framePaint;
    private ColorFilter iconFilter;
    private int iconFilterColor;
    private float lastX;
    private float lastY;
    private final AnimatedFloat liveAlpha;
    private boolean looped;
    private final Paint outlineDarkenPaint;
    private final Paint outlinePaint;
    private final View parent;
    private final Path path;
    private final FloatValueHolder position;
    private final SpringAnimation positionSpring;
    private Drawable reactionDrawable;
    private String reactionEmojicon;
    private ImageReceiver reactionImage;
    private final RectF rect;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean reversed;
    private final FloatValueHolder ring;
    private final SpringAnimation ringSpring;
    private boolean scrubbing;
    private int selected;
    private int slot;
    private boolean stepped;
    private final FloatValueHolder visibility;
    private final SpringAnimation visibilitySpring;
    private final List<SwipeAction> actions = new ArrayList();
    private final SparseArray<Drawable> icons = new SparseArray<>();
    private final SparseArray<Drawable> spareIcons = new SparseArray<>();
    private final SparseArray<Float> iconFits = new SparseArray<>();

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(DynamicAnimation dynamicAnimation, float f, float f2) {
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$1(DynamicAnimation dynamicAnimation, float f, float f2) {
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$2(DynamicAnimation dynamicAnimation, float f, float f2) {
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$3(DynamicAnimation dynamicAnimation, float f, float f2) {
        invalidate();
    }

    public SwipeActionsHelper(View view, Theme.ResourcesProvider resourcesProvider) {
        Paint paint = new Paint(1);
        this.outlinePaint = paint;
        Paint paint2 = new Paint(1);
        this.outlineDarkenPaint = paint2;
        this.path = new Path();
        this.rect = new RectF();
        this.liveAlpha = new AnimatedFloat(new Runnable() { // from class: com.exteragram.messenger.utils.chats.SwipeActionsHelper$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                SwipeActionsHelper.this.invalidate();
            }
        }, 0L, 280L, CubicBezierInterpolator.EASE_OUT);
        FloatValueHolder floatValueHolder = new FloatValueHolder(0.0f);
        this.position = floatValueHolder;
        this.positionSpring = new SpringAnimation(floatValueHolder).setSpring(new SpringForce(0.0f).setStiffness(430.0f).setDampingRatio(0.55f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: com.exteragram.messenger.utils.chats.SwipeActionsHelper$$ExternalSyntheticLambda1
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                SwipeActionsHelper.this.lambda$new$0(dynamicAnimation, f, f2);
            }
        });
        FloatValueHolder floatValueHolder2 = new FloatValueHolder(0.0f);
        this.visibility = floatValueHolder2;
        this.visibilitySpring = new SpringAnimation(floatValueHolder2).setMinValue(0.0f).setMaxValue(2000.0f).setSpring(new SpringForce(0.0f).setStiffness(1500.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: com.exteragram.messenger.utils.chats.SwipeActionsHelper$$ExternalSyntheticLambda2
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                SwipeActionsHelper.this.lambda$new$1(dynamicAnimation, f, f2);
            }
        });
        FloatValueHolder floatValueHolder3 = new FloatValueHolder(0.0f);
        this.fill = floatValueHolder3;
        this.fillSpring = new SpringAnimation(floatValueHolder3).setMinValue(0.0f).setSpring(new SpringForce(0.0f).setStiffness(400.0f).setDampingRatio(0.5f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: com.exteragram.messenger.utils.chats.SwipeActionsHelper$$ExternalSyntheticLambda3
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                SwipeActionsHelper.this.lambda$new$2(dynamicAnimation, f, f2);
            }
        });
        FloatValueHolder floatValueHolder4 = new FloatValueHolder(0.0f);
        this.ring = floatValueHolder4;
        this.ringSpring = new SpringAnimation(floatValueHolder4).setMinValue(0.0f).setSpring(new SpringForce(0.0f).setStiffness(200.0f).setDampingRatio(1.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: com.exteragram.messenger.utils.chats.SwipeActionsHelper$$ExternalSyntheticLambda4
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                SwipeActionsHelper.this.lambda$new$3(dynamicAnimation, f, f2);
            }
        });
        this.parent = view;
        this.resourcesProvider = resourcesProvider;
        Paint.Style style = Paint.Style.STROKE;
        paint.setStyle(style);
        Paint.Cap cap = Paint.Cap.ROUND;
        paint.setStrokeCap(cap);
        paint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        paint2.setStyle(style);
        paint2.setStrokeCap(cap);
        paint2.setStrokeWidth(AndroidUtilities.dp(2.0f));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidate() {
        this.parent.invalidate();
    }

    public void setReaction(int i, String str, long j) {
        if (j != 0) {
            if (this.animatedReactionId != j) {
                detach();
                AnimatedEmojiDrawable animatedEmojiDrawableMake = AnimatedEmojiDrawable.make(i, 0, j);
                this.animatedReaction = animatedEmojiDrawableMake;
                animatedEmojiDrawableMake.addView(this.parent);
                this.animatedReactionId = j;
            }
            this.reactionDrawable = this.animatedReaction;
            return;
        }
        TLRPC.TL_availableReaction tL_availableReaction = str != null ? MediaDataController.getInstance(i).getReactionsMap().get(str) : null;
        if (tL_availableReaction != null && tL_availableReaction.center_icon != null) {
            if (this.reactionImage == null || !TextUtils.equals(this.reactionEmojicon, str)) {
                detach();
                ImageReceiver imageReceiver = new ImageReceiver(this.parent);
                this.reactionImage = imageReceiver;
                imageReceiver.ignoreNotifications = true;
                imageReceiver.onAttachedToWindow();
                this.reactionImage.setImage(ImageLocation.getForDocument(tL_availableReaction.center_icon), "40_40_lastreactframe", DocumentObject.getSvgThumb(tL_availableReaction.static_icon, Theme.key_windowBackgroundGray, 1.0f), "webp", tL_availableReaction, 1);
                this.reactionEmojicon = str;
                return;
            }
            return;
        }
        detach();
        this.reactionDrawable = str != null ? Emoji.getEmojiDrawable(str) : null;
    }

    public void detach() {
        AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedReaction;
        if (animatedEmojiDrawable != null) {
            animatedEmojiDrawable.removeView(this.parent);
            this.animatedReaction = null;
            this.animatedReactionId = 0L;
        }
        ImageReceiver imageReceiver = this.reactionImage;
        if (imageReceiver != null) {
            imageReceiver.onDetachedFromWindow();
            this.reactionImage = null;
            this.reactionEmojicon = null;
        }
        this.reactionDrawable = null;
    }

    public void start(List<SwipeAction> list) {
        this.actions.clear();
        this.actions.addAll(list);
        this.looped = ExteraConfig.getSwipeActionsLoop();
        this.reversed = ExteraConfig.getSwipeActionsReversed();
        this.selected = 0;
        this.slot = 0;
        this.armed = false;
        this.stepped = false;
        this.scrubbing = false;
        this.liveAlpha.set(0.0f, true);
        this.anchorY = 0.0f;
        this.driftX = 0.0f;
        this.driftY = 0.0f;
        this.direction = 0;
        this.positionSpring.cancel();
        this.positionSpring.getSpring().setFinalPosition(0.0f);
        this.position.setValue(0.0f);
        this.visibilitySpring.cancel();
        this.visibilitySpring.getSpring().setFinalPosition(0.0f);
        this.visibility.setValue(0.0f);
        this.fillSpring.cancel();
        this.fillSpring.getSpring().setFinalPosition(0.0f);
        this.fill.setValue(0.0f);
        this.ringSpring.cancel();
        this.ringSpring.getSpring().setFinalPosition(0.0f);
        this.ring.setValue(0.0f);
        this.beyondMax = false;
    }

    public int size() {
        return this.actions.size();
    }

    public SwipeAction getSelected() {
        int i = this.selected;
        if (i < 0 || i >= this.actions.size()) {
            return null;
        }
        return this.actions.get(this.selected);
    }

    public void update(float f, float f2, float f3) {
        if (this.actions.size() < 2) {
            return;
        }
        if (Math.abs(f) < AndroidUtilities.dp(50.0f)) {
            this.armed = false;
            this.scrubbing = false;
            return;
        }
        if (!this.armed) {
            this.armed = true;
            this.lastX = f2;
            this.lastY = f3;
            this.driftX = 0.0f;
            this.driftY = 0.0f;
            return;
        }
        float fAbs = Math.abs(f2 - this.lastX);
        float f4 = f3 - this.lastY;
        this.lastX = f2;
        this.lastY = f3;
        if (!this.scrubbing) {
            this.driftX = (this.driftX * 0.7f) + (fAbs * 0.3f);
            float fAbs2 = (this.driftY * 0.7f) + (Math.abs(f4) * 0.3f);
            this.driftY = fAbs2;
            if (fAbs2 < AndroidUtilities.dp(0.5f) || this.driftY <= this.driftX * 1.5f) {
                return;
            }
            this.scrubbing = true;
            this.anchorY = f3;
            return;
        }
        float f5 = (f3 - this.anchorY) * (this.direction < 0 ? -1 : 1) * (this.reversed ? -1 : 1);
        float fDp = AndroidUtilities.dp(this.stepped ? 36.0f : 44.0f);
        if (f5 >= fDp) {
            this.anchorY = f3;
            this.stepped = true;
            if (this.looped || this.selected < this.actions.size() - 1) {
                move(1);
                return;
            }
            return;
        }
        if (f5 <= (-fDp)) {
            this.anchorY = f3;
            this.stepped = true;
            if (this.looped || this.selected > 0) {
                move(-1);
            }
        }
    }

    private int chooseDirection(float f) {
        float fDp = AndroidUtilities.dp(44.0f) + ((this.actions.size() - 2) * AndroidUtilities.dp(36.0f));
        float height = this.parent.getHeight() - f;
        return (height >= fDp || height >= f) ? 1 : -1;
    }

    public void select(int i) {
        this.scrubbing = true;
        if (i == this.selected || i < 0 || i >= this.actions.size()) {
            return;
        }
        int iFloorMod = i - this.selected;
        if (this.looped && (iFloorMod = Math.floorMod(iFloorMod, this.actions.size())) > this.actions.size() / 2) {
            iFloorMod -= this.actions.size();
        }
        this.selected = i;
        this.slot += iFloorMod;
        this.positionSpring.getSpring().setFinalPosition(this.slot * 2000.0f);
        this.positionSpring.start();
        invalidate();
    }

    public boolean remove(SwipeAction swipeAction) {
        int iIndexOf;
        boolean z = false;
        if (!this.scrubbing && this.actions.size() >= 2 && (iIndexOf = this.actions.indexOf(swipeAction)) >= 0 && (iIndexOf != this.selected || this.visibility.getValue() == 0.0f)) {
            this.actions.remove(iIndexOf);
            z = true;
            if (this.selected >= this.actions.size()) {
                int size = this.actions.size() - 1;
                this.selected = size;
                this.slot = size;
                this.positionSpring.cancel();
                this.positionSpring.getSpring().setFinalPosition(this.selected * 2000.0f);
                this.position.setValue(this.selected * 2000.0f);
            }
            invalidate();
        }
        return z;
    }

    private void move(int i) {
        int i2 = this.slot + i;
        this.slot = i2;
        this.selected = Math.floorMod(i2, this.actions.size());
        try {
            this.parent.performHapticFeedback(4, 2);
        } catch (Exception unused) {
        }
        this.positionSpring.getSpring().setFinalPosition(this.slot * 2000.0f);
        this.positionSpring.start();
        invalidate();
    }

    public void draw(Canvas canvas, float f, boolean z, float f2, float f3) {
        int i;
        float f4;
        float f5;
        float f6;
        float f7;
        if (this.actions.isEmpty() || Thread.currentThread() != Looper.getMainLooper().getThread()) {
            return;
        }
        if (this.direction == 0) {
            this.direction = chooseDirection(f2);
        }
        Paint themePaint = Theme.getThemePaint("paintChatActionBackground", this.resourcesProvider);
        Paint paint = Theme.chat_actionBackgroundGradientDarkenPaint;
        if (this.outlinePaint.getColor() != themePaint.getColor()) {
            this.outlinePaint.setColor(themePaint.getColor());
        }
        if (this.outlineDarkenPaint.getColor() != paint.getColor()) {
            this.outlineDarkenPaint.setColor(paint.getColor());
        }
        if (this.outlinePaint.getShader() != themePaint.getShader()) {
            this.outlinePaint.setShader(themePaint.getShader());
        }
        if (this.outlineDarkenPaint.getShader() != paint.getShader()) {
            this.outlineDarkenPaint.setShader(paint.getShader());
        }
        this.framePaint = themePaint;
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        this.frameGradient = resourcesProvider != null && resourcesProvider.hasGradientService();
        this.frameDark = ColorUtils.calculateLuminance(Theme.getColor(Theme.key_windowBackgroundWhite, this.resourcesProvider)) <= 0.5d;
        int color = this.outlineDarkenPaint.getColor();
        float value = this.fill.getValue() / 2000.0f;
        if (value > 1.0f) {
            this.beyondMax = true;
        }
        if (this.visibility.getValue() == 0.0f) {
            this.fillSpring.cancel();
            this.fillSpring.getSpring().setFinalPosition(0.0f);
            this.fill.setValue(0.0f);
            this.ringSpring.cancel();
            this.ringSpring.getSpring().setFinalPosition(0.0f);
            this.ring.setValue(0.0f);
            this.beyondMax = false;
        }
        boolean z2 = this.fillSpring.getSpring().getFinalPosition() == 2000.0f;
        float fClamp = z2 ? 1.0f : MathUtils.clamp(((-f) - AndroidUtilities.dp(20.0f)) / AndroidUtilities.dp(30.0f), 0.0f, 1.0f);
        if (fClamp == 1.0f && !z2) {
            this.fillSpring.getSpring().setFinalPosition(2000.0f);
            this.fillSpring.start();
            this.ringSpring.getSpring().setFinalPosition(2000.0f);
            this.ringSpring.start();
        }
        float f8 = f <= ((float) (-AndroidUtilities.dp(20.0f))) ? 2000.0f : 0.0f;
        if (f8 != this.visibilitySpring.getSpring().getFinalPosition()) {
            this.visibilitySpring.getSpring().setFinalPosition(f8);
            if (!this.visibilitySpring.isRunning()) {
                this.visibilitySpring.start();
            }
        }
        float value2 = this.visibility.getValue() / 2000.0f;
        float measuredWidth = this.parent.getMeasuredWidth();
        float fMax = Math.max((f * (z ? 0.5f : 1.0f)) + measuredWidth, Math.min((f3 + measuredWidth) / 2.0f, measuredWidth));
        boolean z3 = this.beyondMax;
        float f9 = z3 ? value : value2;
        float f10 = z3 ? 0.0f : 1.0f - value;
        float value3 = this.position.getValue() / 2000.0f;
        float fDp = AndroidUtilities.dp(36.0f) * this.direction;
        float f11 = this.liveAlpha.set(this.scrubbing);
        int size = this.actions.size();
        int iCeil = (int) Math.ceil(value3 - 2.0f);
        int iFloor = (int) Math.floor(value3 + 2.0f);
        if (!this.looped || size < 2) {
            iCeil = Math.max(iCeil, 0);
            iFloor = Math.min(iFloor, size - 1);
        }
        int i2 = iFloor;
        while (true) {
            int i3 = this.slot;
            if (iCeil > i2) {
                drawBubble(canvas, fMax, f2 + ((i3 - value3) * fDp), f9, f10, value2, fClamp, Math.abs(i3 - value3), 1.0f, this.slot, true);
                this.outlineDarkenPaint.setColor(color);
                paint.setColor(color);
                return;
            }
            if (iCeil != i3) {
                float f12 = iCeil - value3;
                f6 = f10;
                float f13 = fClamp;
                i = iCeil;
                f4 = value2;
                f5 = f13;
                f7 = fMax;
                drawBubble(canvas, f7, f2 + (f12 * fDp), f9, f6, f4, f5, Math.abs(f12), f11, i, false);
            } else {
                float f14 = fClamp;
                i = iCeil;
                f4 = value2;
                f5 = f14;
                f6 = f10;
                f7 = fMax;
            }
            int i4 = i + 1;
            f10 = f6;
            fClamp = f5;
            value2 = f4;
            iCeil = i4;
            fMax = f7;
            fDp = fDp;
        }
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0121  */
    /* JADX WARN: Code duplicated, block: B:26:0x0127  */
    /* JADX WARN: Code duplicated, block: B:30:0x013c  */
    /* JADX WARN: Code duplicated, block: B:33:0x0181  */
    /* JADX WARN: Code duplicated, block: B:35:0x0187  */
    /* JADX WARN: Code duplicated, block: B:38:0x019a  */
    /* JADX WARN: Code duplicated, block: B:47:0x01bf  */
    /* JADX WARN: Code duplicated, block: B:49:0x01ef  */
    /* JADX WARN: Code duplicated, block: B:51:0x01f7  */
    /* JADX WARN: Code duplicated, block: B:70:0x02b1  */
    /* JADX WARN: Code duplicated, block: B:72:0x02b4  */
    /* JADX WARN: Code duplicated, block: B:73:0x02bd  */
    /* JADX WARN: Code duplicated, block: B:75:0x02ca  */
    /* JADX WARN: Code duplicated, block: B:78:0x02d1 A[ORIG_RETURN, RETURN] */
    /* JADX WARN: Code duplicated, block: B:79:0x02d2  */
    /* JADX WARN: Code duplicated, block: B:81:0x02de  */
    /* JADX WARN: Code duplicated, block: B:82:0x02e1  */
    /* JADX WARN: Code duplicated, block: B:84:0x02e7  */
    /* JADX WARN: Code duplicated, block: B:85:0x02ec  */
    private void drawBubble(Canvas canvas, float f, float f2, float f3, float f4, float f5, float f6, float f7, float f8, int i, boolean z) {
        boolean z2;
        boolean z3;
        float f9;
        float f10;
        float f11;
        float f12;
        float f13;
        int i2;
        Paint paint;
        Paint paint2;
        float f14;
        Canvas canvas2 = canvas;
        Path.Direction direction;
        float f15;
        float f16;
        float value;
        SwipeAction swipeAction;
        SwipeAction swipeAction2;
        boolean z4;
        Drawable icon;
        Float f17;
        float fFloatValue;
        float f18;
        float f19;
        float strokeWidth;
        float f20;
        float f21;
        if (f7 >= 2.0f) {
            return;
        }
        float fMax = Math.max(0.0f, 1.0f - f7);
        float fMax2 = Math.max(fMax, (2.0f - f7) * 0.45f);
        float interpolation = ((fMax * 0.3f) + 0.7f) * ((CubicBezierInterpolator.EASE_OUT_BACK.getInterpolation(f8) * 0.15f) + 0.85f);
        float f22 = f3 * interpolation;
        float f23 = f4 * interpolation;
        float f24 = f5 * fMax2 * f8;
        if (f24 <= 0.0f) {
            return;
        }
        Paint paint3 = this.framePaint;
        Paint paint4 = Theme.chat_actionBackgroundGradientDarkenPaint;
        boolean z5 = this.frameGradient;
        boolean z6 = this.frameDark;
        float fDp = AndroidUtilities.dp(16.0f);
        if (z && f6 > 0.0f && this.fill.getValue() == 0.0f) {
            setRect(f, f2, (fDp * f22) - (this.outlinePaint.getStrokeWidth() / 2.0f));
            applyServiceShaderMatrix();
            int alpha = this.outlinePaint.getAlpha();
            f9 = 2.0f;
            f10 = 1.0f;
            this.outlinePaint.setAlpha((int) (alpha * f24));
            float f25 = 360.0f * f6;
            z2 = z5;
            f12 = f22;
            f13 = f23;
            i2 = -1;
            f11 = 0.0f;
            f14 = fDp;
            z3 = z6;
            paint2 = paint4;
            paint = paint3;
            canvas.drawArc(this.rect, -90.0f, f25, false, this.outlinePaint);
            this.outlinePaint.setAlpha(alpha);
            if (z2) {
                int alpha2 = this.outlineDarkenPaint.getAlpha();
                if (z3) {
                    this.outlineDarkenPaint.setColor(-1);
                }
                this.outlineDarkenPaint.setAlpha((int) (alpha2 * f24));
                canvas2 = canvas;
                canvas2.drawArc(this.rect, -90.0f, f25, false, this.outlineDarkenPaint);
            }
            float f26 = f14 * f12;
            setRect(f, f2, f26);
            applyServiceShaderMatrix();
            this.path.rewind();
            Path path = this.path;
            RectF rectF = this.rect;
            direction = Path.Direction.CW;
            path.addRoundRect(rectF, f26, f26, direction);
            int alpha3 = paint.getAlpha();
            f15 = 0.6f * f24 * f6;
            paint.setAlpha((int) (alpha3 * f15));
            canvas2.drawPath(this.path, paint);
            paint.setAlpha(alpha3);
            if (z2) {
                int alpha4 = paint2.getAlpha();
                if (z3) {
                    paint2.setColor(i2);
                }
                paint2.setAlpha((int) (f15 * alpha4));
                canvas2.drawPath(this.path, paint2);
                paint2.setAlpha(alpha4);
            }
            if (f13 != f11) {
                setRect(f, f2, f14 * f13);
                this.path.rewind();
                this.path.addRoundRect(this.rect, f14, f14, direction);
                canvas2.save();
                canvas2.clipPath(this.path, Region.Op.DIFFERENCE);
            }
            setRect(f, f2, f26);
            applyServiceShaderMatrix();
            this.path.rewind();
            this.path.addRoundRect(this.rect, f26, f26, direction);
            int alpha5 = paint.getAlpha();
            f16 = 0.4f * f24;
            paint.setAlpha((int) (alpha5 * f16));
            canvas2.drawPath(this.path, paint);
            paint.setAlpha(alpha5);
            if (z2) {
                int alpha6 = paint2.getAlpha();
                if (z3) {
                    paint2.setColor(i2);
                }
                paint2.setAlpha((int) (f16 * alpha6));
                canvas2.drawPath(this.path, paint2);
                paint2.setAlpha(alpha6);
            }
            if (f13 != f11) {
                canvas2.restore();
            }
            value = this.ring.getValue() / 2000.0f;
            if (z && value != f11 && value != f10) {
                f19 = value + f10;
                strokeWidth = this.outlinePaint.getStrokeWidth();
                f20 = (f10 - value) * strokeWidth;
                if (f20 != f11) {
                    f21 = f14 * f19;
                    setRect(f, f2, f21 - f20);
                    applyServiceShaderMatrix();
                    int alpha7 = this.outlinePaint.getAlpha();
                    this.outlinePaint.setAlpha((int) (alpha7 * f24));
                    this.outlinePaint.setStrokeWidth(f20);
                    canvas2.drawRoundRect(this.rect, f21, f21, this.outlinePaint);
                    this.outlinePaint.setStrokeWidth(strokeWidth);
                    this.outlinePaint.setAlpha(alpha7);
                    if (z2) {
                        int alpha8 = this.outlineDarkenPaint.getAlpha();
                        if (z3) {
                            this.outlineDarkenPaint.setColor(i2);
                        }
                        this.outlineDarkenPaint.setAlpha((int) (alpha8 * f24));
                        this.outlineDarkenPaint.setStrokeWidth(f20);
                        canvas2.drawRoundRect(this.rect, f21, f21, this.outlineDarkenPaint);
                        this.outlineDarkenPaint.setStrokeWidth(strokeWidth);
                    }
                }
            }
            List<SwipeAction> list = this.actions;
            swipeAction = list.get(Math.floorMod(i, list.size()));
            swipeAction2 = SwipeAction.REACTION;
            if (swipeAction != swipeAction2 && this.reactionImage != null) {
                float fDp2 = (AndroidUtilities.dp(20.0f) / f9) * f12;
                float f27 = f - fDp2;
                float f28 = f2 - fDp2;
                float f29 = fDp2 * f9;
                this.reactionImage.setImageCoords(f27, f28, f29, f29);
                this.reactionImage.setAlpha(f24);
                this.reactionImage.draw(canvas2);
                return;
            }
            if (swipeAction != swipeAction2 && this.reactionDrawable != null) {
                float fDp3 = (AndroidUtilities.dp(20.0f) / f9) * f12;
                setRect(f, f2, fDp3);
                this.path.rewind();
                float f30 = 0.35f * fDp3;
                this.path.addRoundRect(this.rect, f30, f30, direction);
                canvas2.save();
                canvas2.clipPath(this.path);
                this.reactionDrawable.setAlpha((int) (f24 * 255.0f));
                this.reactionDrawable.setBounds((int) (f - fDp3), (int) (f2 - fDp3), (int) (f + fDp3), (int) (fDp3 + f2));
                this.reactionDrawable.draw(canvas2);
                this.reactionDrawable.setAlpha(255);
                canvas2.restore();
                return;
            }
            if (this.actions.size() == 1 || swipeAction != SwipeAction.REPLY) {
                z4 = false;
            } else {
                z4 = true;
            }
            if (z4) {
                icon = Theme.getThemeDrawable("drawableReplyIcon", this.resourcesProvider);
            } else {
                icon = getIcon(swipeAction, (Math.floorDiv(i, this.actions.size()) & 1) != 0);
            }
            if (icon == null) {
                return;
            }
            f17 = this.iconFits.get(swipeAction.iconRes);
            if (z4) {
                f18 = f12;
            } else {
                float f31 = f12 * swipeAction.iconTrim;
                if (f17 != null) {
                    fFloatValue = f17.floatValue();
                } else {
                    fFloatValue = f10;
                }
                f18 = f31 * fFloatValue;
            }
            float intrinsicWidth = (icon.getIntrinsicWidth() / f9) * f18;
            float intrinsicHeight = (icon.getIntrinsicHeight() / f9) * f18;
            icon.setColorFilter(getIconFilter());
            icon.setAlpha((int) (f24 * 255.0f));
            icon.setBounds((int) (f - intrinsicWidth), (int) (f2 - intrinsicHeight), (int) (intrinsicWidth + f), (int) (intrinsicHeight + f2));
            icon.draw(canvas2);
        }
        z2 = z5;
        z3 = z6;
        f9 = 2.0f;
        f10 = 1.0f;
        f11 = 0.0f;
        f12 = f22;
        f13 = f23;
        i2 = -1;
        paint = paint3;
        paint2 = paint4;
        f14 = fDp;
        canvas2 = canvas;
        float f210 = f14 * f12;
        setRect(f, f2, f210);
        applyServiceShaderMatrix();
        this.path.rewind();
        Path path2 = this.path;
        RectF rectF2 = this.rect;
        direction = Path.Direction.CW;
        path2.addRoundRect(rectF2, f210, f210, direction);
        int alpha9 = paint.getAlpha();
        f15 = 0.6f * f24 * f6;
        paint.setAlpha((int) (alpha9 * f15));
        canvas2.drawPath(this.path, paint);
        paint.setAlpha(alpha9);
        if (z2) {
            int alpha10 = paint2.getAlpha();
            if (z3) {
                paint2.setColor(i2);
            }
            paint2.setAlpha((int) (f15 * alpha10));
            canvas2.drawPath(this.path, paint2);
            paint2.setAlpha(alpha10);
        }
        if (f13 != f11) {
            setRect(f, f2, f14 * f13);
            this.path.rewind();
            this.path.addRoundRect(this.rect, f14, f14, direction);
            canvas2.save();
            canvas2.clipPath(this.path, Region.Op.DIFFERENCE);
        }
        setRect(f, f2, f210);
        applyServiceShaderMatrix();
        this.path.rewind();
        this.path.addRoundRect(this.rect, f210, f210, direction);
        int alpha11 = paint.getAlpha();
        f16 = 0.4f * f24;
        paint.setAlpha((int) (alpha11 * f16));
        canvas2.drawPath(this.path, paint);
        paint.setAlpha(alpha11);
        if (z2) {
            int alpha12 = paint2.getAlpha();
            if (z3) {
                paint2.setColor(i2);
            }
            paint2.setAlpha((int) (f16 * alpha12));
            canvas2.drawPath(this.path, paint2);
            paint2.setAlpha(alpha12);
        }
        if (f13 != f11) {
            canvas2.restore();
        }
        value = this.ring.getValue() / 2000.0f;
        if (z) {
            f19 = value + f10;
            strokeWidth = this.outlinePaint.getStrokeWidth();
            f20 = (f10 - value) * strokeWidth;
            if (f20 != f11) {
                f21 = f14 * f19;
                setRect(f, f2, f21 - f20);
                applyServiceShaderMatrix();
                int alpha13 = this.outlinePaint.getAlpha();
                this.outlinePaint.setAlpha((int) (alpha13 * f24));
                this.outlinePaint.setStrokeWidth(f20);
                canvas2.drawRoundRect(this.rect, f21, f21, this.outlinePaint);
                this.outlinePaint.setStrokeWidth(strokeWidth);
                this.outlinePaint.setAlpha(alpha13);
                if (z2) {
                    int alpha14 = this.outlineDarkenPaint.getAlpha();
                    if (z3) {
                        this.outlineDarkenPaint.setColor(i2);
                    }
                    this.outlineDarkenPaint.setAlpha((int) (alpha14 * f24));
                    this.outlineDarkenPaint.setStrokeWidth(f20);
                    canvas2.drawRoundRect(this.rect, f21, f21, this.outlineDarkenPaint);
                    this.outlineDarkenPaint.setStrokeWidth(strokeWidth);
                }
            }
        }
        List<SwipeAction> list2 = this.actions;
        swipeAction = list2.get(Math.floorMod(i, list2.size()));
        swipeAction2 = SwipeAction.REACTION;
        if (swipeAction != swipeAction2) {
        }
        if (swipeAction != swipeAction2) {
        }
        if (this.actions.size() == 1) {
            z4 = false;
        } else {
            z4 = false;
        }
        if (z4) {
            icon = Theme.getThemeDrawable("drawableReplyIcon", this.resourcesProvider);
        } else {
            icon = getIcon(swipeAction, (Math.floorDiv(i, this.actions.size()) & 1) != 0);
        }
        if (icon == null) {
            return;
        }
        f17 = this.iconFits.get(swipeAction.iconRes);
        if (z4) {
            f18 = f12;
        } else {
            float f32 = f12 * swipeAction.iconTrim;
            if (f17 != null) {
                fFloatValue = f17.floatValue();
            } else {
                fFloatValue = f10;
            }
            f18 = f32 * fFloatValue;
        }
        float intrinsicWidth2 = (icon.getIntrinsicWidth() / f9) * f18;
        float intrinsicHeight2 = (icon.getIntrinsicHeight() / f9) * f18;
        icon.setColorFilter(getIconFilter());
        icon.setAlpha((int) (f24 * 255.0f));
        icon.setBounds((int) (f - intrinsicWidth2), (int) (f2 - intrinsicHeight2), (int) (intrinsicWidth2 + f), (int) (intrinsicHeight2 + f2));
        icon.draw(canvas2);
    }

    private void setRect(float f, float f2, float f3) {
        this.rect.set(f - f3, f2 - f3, f + f3, f2 + f3);
    }

    private void applyServiceShaderMatrix() {
        int measuredWidth = this.parent.getMeasuredWidth();
        float y = this.parent.getY() + this.rect.top;
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (resourcesProvider != null) {
            resourcesProvider.applyServiceShaderMatrix(measuredWidth, AndroidUtilities.displaySize.y, 0.0f, y);
        } else {
            Theme.applyServiceShaderMatrix(measuredWidth, AndroidUtilities.displaySize.y, 0.0f, y);
        }
    }

    private ColorFilter getIconFilter() {
        int color = Theme.getColor(Theme.key_chat_serviceIcon, this.resourcesProvider);
        if (this.iconFilter == null || this.iconFilterColor != color) {
            this.iconFilterColor = color;
            this.iconFilter = new PorterDuffColorFilter(color, PorterDuff.Mode.MULTIPLY);
        }
        return this.iconFilter;
    }

    private float measureIconFit(Drawable drawable) {
        int intrinsicWidth = drawable.getIntrinsicWidth();
        int intrinsicHeight = drawable.getIntrinsicHeight();
        if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            return 1.0f;
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888);
        drawable.setBounds(0, 0, intrinsicWidth, intrinsicHeight);
        drawable.draw(new Canvas(bitmapCreateBitmap));
        int[] iArr = new int[intrinsicWidth * intrinsicHeight];
        bitmapCreateBitmap.getPixels(iArr, 0, intrinsicWidth, 0, 0, intrinsicWidth, intrinsicHeight);
        bitmapCreateBitmap.recycle();
        int i = -1;
        int i2 = -1;
        int i3 = intrinsicWidth;
        int i4 = intrinsicHeight;
        for (int i5 = 0; i5 < intrinsicHeight; i5++) {
            for (int i6 = 0; i6 < intrinsicWidth; i6++) {
                if ((iArr[(i5 * intrinsicWidth) + i6] >>> 24) >= 8) {
                    if (i6 < i3) {
                        i3 = i6;
                    }
                    if (i6 > i) {
                        i = i6;
                    }
                    if (i5 < i4) {
                        i4 = i5;
                    }
                    if (i5 > i2) {
                        i2 = i5;
                    }
                }
            }
        }
        if (i < i3 || i2 < i4) {
            return 1.0f;
        }
        return Math.min(AndroidUtilities.dpf2(15.0f) / ((i - i3) + 1), AndroidUtilities.dpf2(16.5f) / ((i2 - i4) + 1));
    }

    private Drawable getIcon(SwipeAction swipeAction, boolean z) {
        SparseArray<Drawable> sparseArray = z ? this.spareIcons : this.icons;
        Drawable drawable = sparseArray.get(swipeAction.iconRes);
        if (drawable != null) {
            return drawable;
        }
        Drawable drawable2 = this.parent.getContext().getDrawable(swipeAction.iconRes);
        if (drawable2 == null) {
            return null;
        }
        if (this.iconFits.get(swipeAction.iconRes) == null) {
            this.iconFits.put(swipeAction.iconRes, Float.valueOf(measureIconFit(drawable2)));
        }
        Drawable drawableMutate = drawable2.mutate();
        sparseArray.put(swipeAction.iconRes, drawableMutate);
        return drawableMutate;
    }
}
