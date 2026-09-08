package org.telegram.ui.Components;

import android.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.widget.Button;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import com.exteragram.messenger.DividerStyle;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.IconPackType;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.utils.system.VibratorUtils;
import com.google.android.gms.cast.MediaError;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ProfileActivity;

@SuppressLint({"ViewConstructor"})
public class ProfileActionsView extends View {
    private AccessibilityNodeProvider accessibilityNodeProvider;
    private final List<Action> actions;
    private int activeCount;
    private final Set<Integer> allAvailableActions;
    private ProfileActivity.AvatarImageView avatarView;
    private Action callAction;
    private boolean callAnimationStateLoaded;
    private float callBackwardAnimateFromX;
    private float callBackwardAnimateFromY;
    private final Path clipAvatarPath;
    public float clipHeight;
    private final Path clipPath;
    private int color;
    private float currentHeight;
    private long downTime;
    private float downX;
    private float downY;
    private Action firstAction;
    private boolean hasColorById;
    private Action hit;
    private boolean ignoreRect;
    public boolean isAnimatingCallAction;
    private boolean isApplying;
    private boolean isNotificationsEnabled;
    public boolean isOpeningLayout;
    private Action lastAction;
    private ColorFilter lastColorFilter;
    private int lastColorFilterColor;
    private final Matrix matrix;
    public int mode;
    private OnActionClickListener onActionClickListener;
    private final Paint paint;
    private float parentExpanded;
    private final Path pathTmp;
    private RadialGradient radialGradient;
    private final float[] radii;
    private RenderNode renderNode;
    private float renderNodeScale;
    private float renderNodeTranslateY;
    private final Paint shaderPaint;
    private final int targetHeight;
    private int textColor;
    final float textPadding;
    final float top;
    final float xpadding;
    final float ypadding;

    public interface OnActionClickListener {
        void onClick(int i, float f, float f2);
    }

    private void checkPaints() {
    }

    public boolean isSegmentedMode() {
        return ExteraConfig.getDividerStyle() == DividerStyle.SEGMENTS;
    }

    private void setRadii(boolean z, boolean z2, float f, float f2) {
        float[] fArr = this.radii;
        float f3 = z ? f : f2;
        fArr[1] = f3;
        fArr[0] = f3;
        float f4 = z2 ? f : f2;
        fArr[3] = f4;
        fArr[2] = f4;
        float f5 = z2 ? f : f2;
        fArr[5] = f5;
        fArr[4] = f5;
        if (!z) {
            f = f2;
        }
        fArr[7] = f;
        fArr[6] = f;
    }

    private void updateClipPath(Action action, float f, float f2, Path path) {
        boolean z;
        path.rewind();
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(action.rect);
        rectF.inset((action.rect.width() / 2.0f) * (1.0f - action.getScale()), (action.rect.height() / 2.0f) * (1.0f - action.getScale()));
        float f3 = -f2;
        rectF.inset(f3, f3);
        if (isSegmentedMode()) {
            float innerRoundRadius = getInnerRoundRadius();
            boolean z2 = action.isDeleting;
            boolean z3 = false;
            if (z2) {
                z = action.wasFirst;
            } else {
                z = action == this.firstAction;
            }
            if (z2) {
                z3 = action.wasLast;
            } else if (action == this.lastAction) {
                z3 = true;
            }
            setRadii(z, z3, f, innerRoundRadius);
            path.addRoundRect(rectF, this.radii, Path.Direction.CCW);
            return;
        }
        path.addRoundRect(rectF, f, f, Path.Direction.CCW);
    }

    public ProfileActionsView(Context context, int i) {
        super(context);
        this.actions = new ArrayList();
        Paint paint = new Paint();
        this.paint = paint;
        this.shaderPaint = new Paint();
        this.isAnimatingCallAction = false;
        this.isOpeningLayout = true;
        this.clipHeight = -1.0f;
        this.clipAvatarPath = new Path();
        this.clipPath = new Path();
        this.pathTmp = new Path();
        this.radii = new float[8];
        this.activeCount = 0;
        this.ignoreRect = false;
        this.currentHeight = 0.0f;
        this.onActionClickListener = null;
        this.allAvailableActions = new HashSet();
        this.mode = 6;
        this.callAction = null;
        this.color = 0;
        this.matrix = new Matrix();
        this.textColor = -1;
        this.hit = null;
        this.callAnimationStateLoaded = false;
        this.callBackwardAnimateFromX = -1.0f;
        this.callBackwardAnimateFromY = -1.0f;
        paint.setColor(-16777216);
        paint.setAlpha(40);
        float fDpf2 = AndroidUtilities.dpf2(12.0f);
        this.ypadding = fDpf2;
        this.xpadding = fDpf2;
        float fDpf3 = AndroidUtilities.dpf2(8.0f);
        this.top = fDpf3;
        this.textPadding = AndroidUtilities.dpf2(4.0f);
        this.targetHeight = (int) ((i - fDpf2) - fDpf3);
        setBackgroundColor(0);
        setImportantForAccessibility(1);
    }

    public void drawingBlur(boolean z) {
        if (this.ignoreRect == z && this.renderNode == null) {
            return;
        }
        this.ignoreRect = z;
        this.renderNode = null;
        this.avatarView = null;
        invalidate();
    }

    public void drawingBlur(RenderNode renderNode, ProfileActivity.AvatarImageView avatarImageView, float f, float f2) {
        this.ignoreRect = false;
        this.renderNode = renderNode;
        this.avatarView = avatarImageView;
        this.renderNodeScale = f;
        this.renderNodeTranslateY = f2;
        invalidate();
    }

    public void setOnActionClickListener(OnActionClickListener onActionClickListener) {
        this.onActionClickListener = onActionClickListener;
    }

    public void setParentExpanded(float f) {
        if (this.parentExpanded != f) {
            this.parentExpanded = f;
            checkPaints();
            invalidate();
        }
    }

    public void setActionsColor(int i, int i2, boolean z) {
        if (this.radialGradient != null && this.color == i && this.textColor == i2 && this.hasColorById == z) {
            return;
        }
        this.color = i;
        this.textColor = i2;
        this.hasColorById = z;
        createColorShader();
        checkPaints();
    }

    private boolean isButtonColorLight() {
        return AndroidUtilities.computePerceivedBrightness(this.color) > 0.72f;
    }

    private void createColorShader() {
        int i = this.color;
        if (i == 0) {
            return;
        }
        if (!this.hasColorById) {
            this.paint.setColor(i);
            return;
        }
        int measuredWidth = getMeasuredWidth();
        if (measuredWidth <= 0) {
            return;
        }
        float gap = ((measuredWidth - (getGap() * Math.max(0, this.activeCount - 1))) - (this.xpadding * 2.0f)) / Math.max(1, this.activeCount);
        RadialGradient radialGradient = new RadialGradient(gap / 2.0f, this.targetHeight / 2.0f, this.hasColorById ? gap * 0.65f : 1.0f, Theme.multAlpha(this.color, 0.8f), this.color, Shader.TileMode.CLAMP);
        this.radialGradient = radialGradient;
        this.shaderPaint.setShader(radialGradient);
    }

    private void measureActions() {
        if (getMeasuredWidth() <= 0 || this.activeCount <= 0) {
            return;
        }
        float itemWidth = getItemWidth();
        for (int i = 0; i < this.actions.size(); i++) {
            Action action = this.actions.get(i);
            if (action.text != null) {
                action.text.setMaxWidth(itemWidth - AndroidUtilities.dp(2.0f));
                action.textScale = action.text.getLineCount() >= 3 ? 0.75f : action.text.getLineCount() >= 2 ? 0.85f : 1.0f;
            }
        }
    }

    @Override // android.view.View
    public void onMeasure(int i, int i2) {
        setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.makeMeasureSpec((int) (this.targetHeight + this.top + this.ypadding), TLObject.FLAG_30));
        measureActions();
    }

    public void updatePosition(float f, float f2) {
        this.currentHeight = f2;
        setTranslationY(f);
        invalidate();
    }

    private float getGap() {
        return isSegmentedMode() ? AndroidUtilities.dp(2.0f) : this.xpadding / 2.0f;
    }

    private float getItemWidth() {
        int measuredWidth = getMeasuredWidth();
        float gap = getGap();
        int i = this.activeCount;
        return ((measuredWidth - (gap * (i - 1))) - (this.xpadding * 2.0f)) / i;
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        float f = this.clipHeight;
        if (f >= 0.0f) {
            float y = f - getY();
            if (y <= 0.0f) {
                return;
            } else {
                canvas.clipRect(0.0f, 0.0f, getMeasuredWidth(), y);
            }
        }
        float fMax = Math.max(0.0f, (this.currentHeight - this.ypadding) - this.top);
        if (fMax <= 0.0f) {
            return;
        }
        float gap = getGap();
        float itemWidth = getItemWidth();
        float f2 = this.xpadding;
        float roundRadius = getRoundRadius();
        if (this.renderNode != null) {
            this.clipPath.rewind();
        }
        int size = this.actions.size();
        Action action = null;
        Action action2 = null;
        for (int i = 0; i < size; i++) {
            Action action3 = this.actions.get(i);
            if (!action3.isDeleted) {
                if (!action3.isDeleting) {
                    RectF rectF = action3.rect;
                    float f3 = this.top;
                    rectF.set(f2, f3, f2 + itemWidth, f3 + fMax);
                    f2 += itemWidth + gap;
                    if (action == null) {
                        action = action3;
                    }
                    action2 = action3;
                }
                action3.updatePosition();
            }
        }
        this.firstAction = action;
        this.lastAction = action2;
        if (this.renderNode != null) {
            for (int i2 = 0; i2 < size; i2++) {
                Action action4 = this.actions.get(i2);
                if (!action4.isDeleted) {
                    updateClipPath(action4, roundRadius, 1.0f, this.pathTmp);
                    this.clipPath.addPath(this.pathTmp);
                }
            }
        }
        float fClamp01 = Utilities.clamp01(fMax / this.targetHeight);
        float fClamp02 = Utilities.clamp01((fClamp01 - 0.2f) / 0.8f);
        if (fClamp02 <= 0.0f) {
            return;
        }
        if (!this.ignoreRect) {
            for (int i3 = 0; i3 < size; i3++) {
                Action action5 = this.actions.get(i3);
                if (!action5.isDeleted) {
                    int alpha = this.paint.getAlpha();
                    this.paint.setAlpha((int) (((int) (action5.getAlpha() * fClamp02 * alpha)) * (this.radialGradient != null ? 0.1f : 1.0f)));
                    updateClipPath(action5, roundRadius, 0.0f, this.pathTmp);
                    if (isSegmentedMode()) {
                        canvas.save();
                        canvas.clipPath(this.pathTmp);
                        RectF rectF2 = AndroidUtilities.rectTmp;
                        canvas.drawRect(rectF2, this.paint);
                        drawGradient(canvas, action5, fClamp02);
                        this.paint.setAlpha(alpha);
                        action5.rippleDrawable.setBounds((int) rectF2.left, (int) rectF2.top, (int) rectF2.right, (int) rectF2.bottom);
                        action5.rippleDrawable.draw(canvas);
                        canvas.restore();
                    } else {
                        RectF rectF3 = AndroidUtilities.rectTmp;
                        canvas.drawRoundRect(rectF3, roundRadius, roundRadius, this.paint);
                        if (this.radialGradient != null) {
                            drawGradient(canvas, action5, fClamp02);
                        }
                        this.paint.setAlpha(alpha);
                        action5.rippleDrawable.setBounds((int) rectF3.left, (int) rectF3.top, (int) rectF3.right, (int) rectF3.bottom);
                        action5.rippleDrawable.draw(canvas);
                    }
                }
            }
        }
        drawRenderNode(canvas);
        float fClamp03 = Utilities.clamp01((fClamp01 - 0.4f) / 0.6f);
        if (fClamp03 > 0.0f) {
            for (int i4 = 0; i4 < size; i4++) {
                drawAction(canvas, this.actions.get(i4), fClamp01, fClamp03);
            }
        }
    }

    private void drawGradient(Canvas canvas, Action action, float f) {
        if (this.radialGradient != null) {
            int alpha = this.shaderPaint.getAlpha();
            this.shaderPaint.setAlpha((int) (action.getAlpha() * f * alpha));
            Matrix matrix = this.matrix;
            RectF rectF = AndroidUtilities.rectTmp;
            matrix.setTranslate(rectF.left, rectF.top);
            this.radialGradient.setLocalMatrix(this.matrix);
            if (isSegmentedMode()) {
                canvas.drawRect(rectF, this.shaderPaint);
            } else {
                float roundRadius = getRoundRadius();
                canvas.drawRoundRect(rectF, roundRadius, roundRadius, this.shaderPaint);
            }
            this.shaderPaint.setAlpha(alpha);
        }
    }

    private void drawRenderNode(Canvas canvas) {
        RenderNode renderNode = this.renderNode;
        if (renderNode == null || Build.VERSION.SDK_INT < 29 || !renderNode.hasDisplayList() || !canvas.isHardwareAccelerated()) {
            return;
        }
        canvas.save();
        ProfileActivity.AvatarImageView avatarImageView = this.avatarView;
        if (avatarImageView != null) {
            View view = (View) avatarImageView.getParent();
            float x = view.getX();
            float y = view.getY() - getTranslationY();
            float width = view.getWidth() * view.getScaleX();
            float height = view.getHeight() * view.getScaleY();
            this.clipAvatarPath.rewind();
            this.clipAvatarPath.addRoundRect(x, y, width + x, height + y, this.avatarView.getRoundRadiusForExpand() * view.getScaleX(), this.avatarView.getRoundRadiusForExpand() * view.getScaleY(), Path.Direction.CCW);
            canvas.clipPath(this.clipAvatarPath);
        }
        canvas.clipPath(this.clipPath);
        canvas.translate(0.0f, this.renderNodeTranslateY);
        float f = this.renderNodeScale;
        canvas.scale(f, f);
        canvas.drawRenderNode(this.renderNode);
        canvas.restore();
    }

    public void stopLoading(int i) {
        stopLoading(find(i));
    }

    private void stopLoading(Action action) {
        if (action == null || !action.isLoading) {
            return;
        }
        action.isLoading = false;
        invalidate();
    }

    private void updateBounds(Action action) {
        float fCenterX = action.rect.centerX();
        action.rect.centerY();
        float fDp = AndroidUtilities.dp(24.0f);
        float f = 0.5f * fDp;
        float fMax = Math.max(0.0f, ((this.targetHeight - (action.text.getHeight() * action.textScale)) / 3.0f) + AndroidUtilities.dpf2(2.0f));
        action.setBounds((int) (fCenterX - f), (int) fMax, (int) (fCenterX + f), (int) (fMax + fDp));
    }

    private void drawAction(Canvas canvas, Action action, float f, float f2) {
        if (action == null || action.isDeleted) {
            return;
        }
        boolean zIsButtonColorLight = isButtonColorLight();
        float fClamp = !zIsButtonColorLight ? 1.0f : MathUtils.clamp((this.parentExpanded - 0.75f) / 0.25f, 0.0f, 1.0f);
        if (zIsButtonColorLight && Build.VERSION.SDK_INT < 31) {
            fClamp = 0.0f;
        }
        int iBlendARGB = ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText), -1, fClamp);
        if (this.lastColorFilter == null || this.lastColorFilterColor != iBlendARGB) {
            this.lastColorFilterColor = iBlendARGB;
            this.lastColorFilter = new PorterDuffColorFilter(iBlendARGB, PorterDuff.Mode.SRC_IN);
        }
        canvas.save();
        float alpha = f2 * action.getAlpha();
        float fCenterX = action.rect.centerX();
        float fCenterY = action.rect.centerY();
        float scale = f * action.getScale();
        canvas.scale(scale, scale, fCenterX, fCenterY);
        canvas.clipRect(action.rect);
        updateBounds(action);
        float height = ((action.bounds.bottom + action.bounds.top) - ((action.text.getHeight() * action.textScale) / 2.0f)) - AndroidUtilities.dp(6.0f);
        canvas.save();
        canvas.scale(action.textScale, action.textScale, fCenterX, ((action.text.getHeight() * action.textScale) / 2.0f) + height);
        action.text.draw(canvas, fCenterX - (action.text.getWidth() / 2.0f), height, iBlendARGB, alpha);
        canvas.restore();
        int i = action.iconTranslationY;
        if (i != 0) {
            canvas.translate(0.0f, i);
        }
        float f3 = action.iconScale;
        if (f3 != 1.0f) {
            canvas.scale(f3, f3, action.bounds.centerX(), action.bounds.centerY());
        }
        if (!this.isAnimatingCallAction || action.key != 5) {
            boolean zIsBasePackOnly = IconManager.INSTANCE.isBasePackOnly(IconPackType.DEFAULT);
            float f4 = zIsBasePackOnly ? (1.0f - fClamp) * alpha : 0.0f;
            float f5 = !zIsBasePackOnly ? alpha : fClamp * alpha;
            if (action.drawableAnimated != null) {
                if (action.key == 1) {
                    drawActionDrawable(canvas, action.drawableOutline, f4);
                    drawActionDrawable(canvas, action.drawableAnimated, f5);
                } else {
                    drawActionDrawable(canvas, action.drawableAnimated, alpha);
                }
            } else {
                drawActionDrawable(canvas, action.drawableOutline, f4);
                drawActionDrawable(canvas, action.drawableFilled, f5);
            }
        }
        canvas.restore();
        drawLoading(canvas, action, alpha);
    }

    private void drawActionDrawable(Canvas canvas, Drawable drawable, float f) {
        if (drawable == null) {
            return;
        }
        drawable.setColorFilter(this.lastColorFilter);
        drawable.setAlpha((int) (f * 255.0f));
        drawable.draw(canvas);
    }

    private void drawLoading(Canvas canvas, Action action, float f) {
        boolean z;
        boolean z2 = false;
        if (action.stopDelay > 0 && System.currentTimeMillis() > ((long) action.stopDelay) + action.startTime) {
            action.isLoading = false;
        }
        boolean z3 = action.isLoading;
        LoadingDrawable loadingDrawable = action.loadingDrawable;
        if (z3) {
            if (loadingDrawable == null) {
                LoadingDrawable loadingDrawable2 = new LoadingDrawable();
                action.loadingDrawable = loadingDrawable2;
                loadingDrawable2.setCallback(this);
                action.loadingDrawable.setColors(Theme.multAlpha(-1, 0.1f), Theme.multAlpha(-1, 0.3f), Theme.multAlpha(-1, 0.35f), Theme.multAlpha(-1, 0.8f));
                action.loadingDrawable.setAppearByGradient(true);
                action.loadingDrawable.strokePaint.setStrokeWidth(AndroidUtilities.dpf2(1.25f));
            } else if (loadingDrawable.isDisappeared() || action.loadingDrawable.isDisappearing()) {
                action.loadingDrawable.reset();
                action.loadingDrawable.resetDisappear();
            }
        } else if (loadingDrawable != null && !loadingDrawable.isDisappearing() && !action.loadingDrawable.isDisappeared()) {
            action.loadingDrawable.disappear();
        }
        LoadingDrawable loadingDrawable3 = action.loadingDrawable;
        if (loadingDrawable3 != null) {
            loadingDrawable3.setBounds(action.rect);
            if (isSegmentedMode()) {
                float roundRadius = getRoundRadius();
                float innerRoundRadius = getInnerRoundRadius();
                boolean z4 = action.isDeleting;
                if (z4) {
                    z = action.wasFirst;
                } else {
                    z = action == this.firstAction;
                }
                if (z4) {
                    z2 = action.wasLast;
                } else if (action == this.lastAction) {
                    z2 = true;
                }
                LoadingDrawable loadingDrawable4 = action.loadingDrawable;
                float f2 = z ? roundRadius : innerRoundRadius;
                float f3 = z2 ? roundRadius : innerRoundRadius;
                float f4 = z2 ? roundRadius : innerRoundRadius;
                if (!z) {
                    roundRadius = innerRoundRadius;
                }
                loadingDrawable4.setRadii(f2, f3, f4, roundRadius);
            } else {
                action.loadingDrawable.setRadii(getRoundRadius());
            }
            action.loadingDrawable.setAlpha((int) (f * 255.0f));
            action.loadingDrawable.draw(canvas);
        }
    }

    public float getRoundRadius() {
        return AndroidUtilities.dp(ExteraConfig.getSectionRadiusDp());
    }

    public float getInnerRoundRadius() {
        return Math.min(AndroidUtilities.dp(4.0f), ExteraConfig.getSectionRadiusDp());
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        Action action;
        if (this.currentHeight < AndroidUtilities.dp(8.0f)) {
            return false;
        }
        float x = motionEvent.getX();
        float y = motionEvent.getY();
        int action2 = motionEvent.getAction();
        if (action2 == 0) {
            this.hit = null;
            int size = this.actions.size();
            for (int i = 0; i < size; i++) {
                Action action3 = this.actions.get(i);
                if (!action3.isDeleting && action3.rect.contains(x, y)) {
                    this.hit = action3;
                    this.downX = x;
                    this.downY = y;
                    this.downTime = System.currentTimeMillis();
                    this.hit.bounce.setPressed(true);
                    this.hit.rippleDrawable.setHotspot(x, y);
                    this.hit.rippleDrawable.setState(new int[]{R.attr.state_pressed, R.attr.state_enabled});
                    break;
                }
            }
        } else if (action2 == 2) {
            if (this.hit != null && (Math.abs(x - this.downX) > 20.0f || Math.abs(y - this.downY) > 20.0f)) {
                this.hit.bounce.setPressed(false);
                this.hit.rippleDrawable.setState(new int[0]);
                this.hit = null;
            }
        } else if ((action2 == 1 || action2 == 3) && (action = this.hit) != null) {
            action.bounce.setPressed(false);
            this.hit.rippleDrawable.setState(new int[0]);
            if (action2 == 1 && this.hit.rect.contains(x, y)) {
                if (System.currentTimeMillis() - this.downTime > 250) {
                    try {
                        performHapticFeedback(VibratorUtils.getType(0), 1);
                    } catch (Exception unused) {
                    }
                }
                Action action4 = this.hit;
                if (action4.supportsLoading && !action4.isLoading) {
                    action4.isLoading = true;
                    invalidate();
                }
                if (this.hit.supportsAnimate != 0 && IconManager.INSTANCE.isBasePackOnly(IconPackType.DEFAULT)) {
                    Action action5 = this.hit;
                    action5.updateDrawable(true, action5.supportsAnimate);
                }
                this.hit.startTime = System.currentTimeMillis();
                final Action action6 = this.hit;
                OnActionClickListener onActionClickListener = this.onActionClickListener;
                if (onActionClickListener != null) {
                    if (action6.callDelay == 0) {
                        int i2 = action6.key;
                        RectF rectF = action6.rect;
                        onActionClickListener.onClick(i2, rectF.left, rectF.top);
                    } else {
                        postDelayed(new Runnable() { // from class: org.telegram.ui.Components.ProfileActionsView$$ExternalSyntheticLambda2
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$onTouchEvent$0(action6);
                            }
                        }, action6.callDelay);
                    }
                }
            }
            this.hit = null;
            return true;
        }
        return this.hit != null;
    }

    public void lambda$applyVisibleActions$2(List list) {
        int i = this.activeCount;
        int size = list.size();
        this.activeCount = size;
        if (i != size && this.radialGradient != null) {
            createColorShader();
        }
        int size2 = this.actions.size();
        int i2 = 0;
        while (true) {
            List<Action> list2 = this.actions;
            if (i2 < size2) {
                Action action = list2.get(i2);
                if (action.isDeleting && !action.isDeleted) {
                    list.add(action);
                } else if (find(list, action.key) == null) {
                    action.delete();
                    list.add(action);
                }
                i2++;
            } else {
                list2.clear();
                this.actions.addAll(list);
                invalidate();
                return;
            }
        }
    }

    private void insertIfAvailable(List<Action> list, int i) {
        if (this.allAvailableActions.contains(Integer.valueOf(i))) {
            list.add(getOrCreate(i));
        }
    }

    private void insertIfNotAvailable(List<Action> list, int i, int i2) {
        if (!this.allAvailableActions.contains(Integer.valueOf(i)) || this.allAvailableActions.contains(Integer.valueOf(i2))) {
            return;
        }
        list.add(getOrCreate(i));
    }

    private void insertIfNotAvailable2(List<Action> list, int i, int i2, int i3) {
        if (!this.allAvailableActions.contains(Integer.valueOf(i)) || this.allAvailableActions.contains(Integer.valueOf(i2)) || this.allAvailableActions.contains(Integer.valueOf(i3))) {
            return;
        }
        list.add(getOrCreate(i));
    }

    private boolean hasJoin() {
        return this.allAvailableActions.contains(7) && !this.allAvailableActions.contains(9);
    }

    private Action getOrCreate(int i) {
        Action actionFind = find(i);
        if (actionFind != null) {
            if (i == 1) {
                updateNotification(actionFind, false);
            }
            return actionFind;
        }
        if (i != 18) {
            switch (i) {
                case 0:
                    actionFind = new Action(ActionButton.MESSAGE);
                    break;
                case 1:
                    actionFind = new Action();
                    updateNotification(actionFind, false);
                    break;
                case 2:
                    actionFind = new Action(ActionButton.DISCUSS);
                    break;
                case 3:
                    actionFind = new Action(ActionButton.GIFT);
                    actionFind.supportsLoading = true;
                    actionFind.stopDelay = 200;
                    break;
                case 4:
                    actionFind = new Action(ActionButton.SHARE);
                    break;
                case 5:
                    actionFind = new Action(ActionButton.CALL);
                    this.callAction = actionFind;
                    actionFind.supportsLoading = true;
                    actionFind.stopDelay = MediaError.DetailedErrorCode.SEGMENT_UNKNOWN;
                    break;
                case 6:
                    actionFind = new Action(ActionButton.VIDEO);
                    actionFind.supportsLoading = true;
                    actionFind.stopDelay = MediaError.DetailedErrorCode.SEGMENT_UNKNOWN;
                    break;
                case 7:
                    actionFind = new Action(ActionButton.JOIN);
                    actionFind.supportsLoading = true;
                    actionFind.callDelay = 300;
                    break;
                case 8:
                    actionFind = new Action(ActionButton.REPORT);
                    actionFind.supportsLoading = true;
                    actionFind.stopDelay = MediaError.DetailedErrorCode.SEGMENT_UNKNOWN;
                    break;
                case 9:
                    actionFind = new Action(ActionButton.LEAVE);
                    actionFind.supportsLoading = true;
                    actionFind.supportsAnimate = org.telegram.messenger.R.raw.profile_leave;
                    actionFind.stopDelay = 300;
                    break;
                case 10:
                    actionFind = new Action(ActionButton.VOICE_CHAT);
                    actionFind.supportsLoading = true;
                    actionFind.supportsAnimate = org.telegram.messenger.R.raw.profile_voicechat;
                    actionFind.stopDelay = MediaError.DetailedErrorCode.SEGMENT_UNKNOWN;
                    break;
                case 11:
                    actionFind = new Action(ActionButton.STREAM);
                    actionFind.supportsLoading = true;
                    actionFind.supportsAnimate = org.telegram.messenger.R.raw.profile_voicechat;
                    actionFind.stopDelay = MediaError.DetailedErrorCode.SEGMENT_UNKNOWN;
                    break;
                case 12:
                    actionFind = new Action(ActionButton.STORY);
                    break;
                case 13:
                    actionFind = new Action(ActionButton.STOP);
                    actionFind.supportsLoading = true;
                    actionFind.stopDelay = 300;
                    break;
            }
        } else {
            actionFind = new Action(ActionButton.OPEN_CHANNEL);
        }
        if (actionFind != null) {
            actionFind.key = i;
        }
        return actionFind;
    }

    private Action find(int i) {
        return find(this.actions, i);
    }

    private Action find(List<Action> list, int i) {
        int size = list.size();
        for (int i2 = 0; i2 < size; i2++) {
            Action action = list.get(i2);
            if (!action.isDeleting && action.key == i) {
                return action;
            }
        }
        return null;
    }

    public boolean hasCall() {
        return this.allAvailableActions.contains(5) && this.callAction != null;
    }

    public class Action {
        private final ButtonBounce bounce;
        private RLottieDrawable drawableAnimated;
        private Drawable drawableFilled;
        private Drawable drawableOutline;
        boolean isLoading;
        int key;
        LoadingDrawable loadingDrawable;
        private final AnimatedFloat positionFraction;
        long startTime;
        int stopDelay;
        int supportsAnimate;
        boolean supportsLoading;
        private Text text;
        boolean wasFirst;
        boolean wasLast;
        final RectF prevRect = new RectF();
        final RectF rect = new RectF();
        private final RectF to = new RectF();
        private final RectF from = new RectF();
        private final Rect bounds = new Rect();
        private float textScale = 1.0f;
        boolean isOpening = false;
        boolean isDeleting = false;
        boolean isDeleted = false;
        int iconTranslationY = 0;
        float iconScale = 1.0f;
        int callDelay = 0;
        private final Drawable rippleDrawable = createRippleDrawable();

        public void setBounds(int i, int i2, int i3, int i4) {
            this.bounds.set(i, i2, i3, i4);
            checkBounds();
        }

        private void checkBounds() {
            RLottieDrawable rLottieDrawable = this.drawableAnimated;
            if (rLottieDrawable != null) {
                rLottieDrawable.setBounds(this.bounds);
            }
            Drawable drawable = this.drawableFilled;
            if (drawable != null) {
                drawable.setBounds(this.bounds);
            }
            Drawable drawable2 = this.drawableOutline;
            if (drawable2 != null) {
                drawable2.setBounds(this.bounds);
            }
        }

        public void setText(CharSequence charSequence) {
            this.text = new Text(charSequence, 11.0f, AndroidUtilities.bold()).multiline(3).align(Layout.Alignment.ALIGN_CENTER);
        }

        @Deprecated
        public Action() {
            this.bounce = new ButtonBounce(ProfileActionsView.this);
            this.positionFraction = new AnimatedFloat(ProfileActionsView.this, 0L, 250L, CubicBezierInterpolator.DEFAULT);
        }

        public Action(ActionButton actionButton) {
            this.bounce = new ButtonBounce(ProfileActionsView.this);
            this.positionFraction = new AnimatedFloat(ProfileActionsView.this, 0L, 250L, CubicBezierInterpolator.DEFAULT);
            update(actionButton);
        }

        public float getAlpha() {
            if (this.isDeleting) {
                return 1.0f - this.positionFraction.set(1.0f);
            }
            if (this.isOpening) {
                return this.positionFraction.set(1.0f);
            }
            return 1.0f;
        }

        private Drawable createRippleDrawable() {
            int sectionRadiusDp = ProfileActionsView.this.isSegmentedMode() ? 0 : ExteraConfig.getSectionRadiusDp();
            Drawable drawableCreateRadSelectorDrawable = Theme.createRadSelectorDrawable(0, 285212671, sectionRadiusDp, sectionRadiusDp);
            drawableCreateRadSelectorDrawable.setCallback(ProfileActionsView.this);
            return drawableCreateRadSelectorDrawable;
        }

        public void delete() {
            LoadingDrawable loadingDrawable = this.loadingDrawable;
            boolean z = false;
            if (loadingDrawable != null) {
                loadingDrawable.disappear();
                this.supportsLoading = false;
                this.isLoading = false;
            }
            this.isDeleting = true;
            this.wasFirst = this == ProfileActionsView.this.firstAction;
            this.wasLast = this == ProfileActionsView.this.lastAction;
            RectF rectF = this.prevRect;
            float f = rectF.left - 1.0f;
            ProfileActionsView profileActionsView = ProfileActionsView.this;
            boolean z2 = f <= profileActionsView.xpadding;
            boolean z3 = rectF.right + 1.0f >= ((float) profileActionsView.getMeasuredWidth()) - ProfileActionsView.this.xpadding;
            if (z2 && z3) {
                z3 = false;
            } else {
                z = z2;
            }
            this.from.set(this.prevRect);
            this.to.set(this.prevRect);
            if (z) {
                RectF rectF2 = this.to;
                rectF2.right = rectF2.left;
            } else if (z3) {
                RectF rectF3 = this.to;
                rectF3.left = rectF3.right;
            } else {
                int i = this.key;
                if ((i == 3 || i == 2) && ProfileActionsView.this.mode == 1) {
                    RectF rectF4 = this.to;
                    rectF4.left = rectF4.right;
                } else {
                    RectF rectF5 = this.to;
                    float fCenterX = rectF5.centerX();
                    rectF5.right = fCenterX;
                    rectF5.left = fCenterX;
                }
            }
            this.positionFraction.set(0.0f, true);
        }

        @Deprecated
        public void updateDrawable(boolean z, int i) {
            if (z) {
                updateDrawable(i, 0, 0);
            } else {
                updateDrawable(0, i, i);
            }
        }

        ActionButton[] $values() {
            return new ActionButton[]{MESSAGE, NOTIFICATION_MUTE, NOTIFICATION_UNMUTE, DISCUSS, GIFT, SHARE, CALL, VIDEO, JOIN, REPORT, LEAVE, VOICE_CHAT, STREAM, STORY, OPEN_CHANNEL, STOP, SET_PHOTO, EDIT_USERNAME, EDIT_INFO, SETTINGS};
        }

        public static ActionButton valueOf(String str) {
            return (ActionButton) Enum.valueOf(ActionButton.class, str);
        }

        public static ActionButton[] values() {
            return (ActionButton[]) $VALUES.clone();
        }

        static {
            int i = org.telegram.messenger.R.string.ProfileActionsLeave;
            int i2 = org.telegram.messenger.R.drawable.leave;
            LEAVE = new ActionButton("LEAVE", 10, i, i2, i2);
            int i3 = org.telegram.messenger.R.string.ProfileActionsVoiceChat;
            int i4 = org.telegram.messenger.R.drawable.live_stream;
            VOICE_CHAT = new ActionButton("VOICE_CHAT", 11, i3, i4, i4);
            int i5 = org.telegram.messenger.R.string.ProfileActionsLiveStream;
            int i6 = org.telegram.messenger.R.drawable.live_stream;
            STREAM = new ActionButton("STREAM", 12, i5, i6, i6);
            STORY = new ActionButton("STORY", 13, org.telegram.messenger.R.string.Story, org.telegram.messenger.R.drawable.filled_profile_story, org.telegram.messenger.R.drawable.outline_profile_story);
            OPEN_CHANNEL = new ActionButton("OPEN_CHANNEL", 14, org.telegram.messenger.R.string.ProfileChannel, org.telegram.messenger.R.drawable.msg_channel_filled, org.telegram.messenger.R.drawable.msg_channel);
            STOP = new ActionButton("STOP", 15, org.telegram.messenger.R.string.ProfileActionsStop, org.telegram.messenger.R.drawable.filled_profile_stop_24, org.telegram.messenger.R.drawable.outline_profile_stop_24);
            SET_PHOTO = new ActionButton("SET_PHOTO", 16, org.telegram.messenger.R.string.ProfileActionsEditPhoto2, org.telegram.messenger.R.drawable.filled_profile_photo, org.telegram.messenger.R.drawable.outline_profile_photo);
            EDIT_USERNAME = new ActionButton("EDIT_USERNAME", 17, org.telegram.messenger.R.string.ProfileActionsEditUsername, org.telegram.messenger.R.drawable.filled_profile_edit_24, org.telegram.messenger.R.drawable.outline_profile_edit_24);
            EDIT_INFO = new ActionButton("EDIT_INFO", 18, org.telegram.messenger.R.string.ProfileActionsEditInfo, org.telegram.messenger.R.drawable.filled_profile_edit_24, org.telegram.messenger.R.drawable.outline_profile_edit_24);
            SETTINGS = new ActionButton("SETTINGS", 19, org.telegram.messenger.R.string.Settings, org.telegram.messenger.R.drawable.filled_profile_settings, org.telegram.messenger.R.drawable.outline_profile_settings);
            $VALUES = $values();
        }

        private ActionButton(String str, int i, int i2, int i3, int i4) {
            super(str, i);
            this.title = i2;
            this.filledIcon = i3;
            this.outlineIcon = i4;
        }
    }

    @Override // android.view.View
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        if (this.accessibilityNodeProvider == null) {
            this.accessibilityNodeProvider = new AccessibilityNodeProvider() { // from class: org.telegram.ui.Components.ProfileActionsView.1
                /* JADX WARN: Type inference fix 'apply assigned field type' failed
                java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
                	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
                	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
                	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
                	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
                	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
                	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
                	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
                 */
                @Override // android.view.accessibility.AccessibilityNodeProvider
                public AccessibilityNodeInfo createAccessibilityNodeInfo(int i) {
                    Action action;
                    int[] iArr = {0, 0};
                    ProfileActionsView.this.getLocationOnScreen(iArr);
                    if (i == -1) {
                        AccessibilityNodeInfo accessibilityNodeInfoObtain = AccessibilityNodeInfo.obtain(ProfileActionsView.this);
                        ProfileActionsView.this.onInitializeAccessibilityNodeInfo(accessibilityNodeInfoObtain);
                        accessibilityNodeInfoObtain.setEnabled(true);
                        for (int i2 = 0; i2 < ProfileActionsView.this.actions.size(); i2++) {
                            ProfileActionsView profileActionsView = ProfileActionsView.this;
                            accessibilityNodeInfoObtain.addChild(profileActionsView, ((Action) profileActionsView.actions.get(i2)).key);
                        }
                        return accessibilityNodeInfoObtain;
                    }
                    int i3 = 0;
                    while (true) {
                        if (i3 >= ProfileActionsView.this.actions.size()) {
                            action = null;
                            break;
                        }
                        if (((Action) ProfileActionsView.this.actions.get(i3)).key == i) {
                            action = (Action) ProfileActionsView.this.actions.get(i3);
                            break;
                        }
                        i3++;
                    }
                    if (action == null || action.rect.isEmpty()) {
                        return null;
                    }
                    AccessibilityNodeInfo accessibilityNodeInfoObtain2 = AccessibilityNodeInfo.obtain();
                    accessibilityNodeInfoObtain2.setSource(ProfileActionsView.this, i);
                    accessibilityNodeInfoObtain2.setParent(ProfileActionsView.this);
                    accessibilityNodeInfoObtain2.setPackageName(ProfileActionsView.this.getContext().getPackageName());
                    accessibilityNodeInfoObtain2.addAction(16);
                    accessibilityNodeInfoObtain2.addAction(64);
                    accessibilityNodeInfoObtain2.setClickable(true);
                    accessibilityNodeInfoObtain2.setFocusable(true);
                    accessibilityNodeInfoObtain2.setEnabled(true);
                    accessibilityNodeInfoObtain2.setVisibleToUser(true);
                    accessibilityNodeInfoObtain2.setClassName(Button.class.getName());
                    accessibilityNodeInfoObtain2.setText(action.text.getText());
                    RectF rectF = action.rect;
                    Rect rect = new Rect((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                    accessibilityNodeInfoObtain2.setBoundsInParent(rect);
                    rect.offset(iArr[0], iArr[1]);
                    accessibilityNodeInfoObtain2.setBoundsInScreen(rect);
                    return accessibilityNodeInfoObtain2;
                }

                @Override // android.view.accessibility.AccessibilityNodeProvider
                public boolean performAction(int i, int i2, Bundle bundle) {
                    Action action;
                    if (i == -1) {
                        return ProfileActionsView.this.performAccessibilityAction(i2, bundle);
                    }
                    int i3 = 0;
                    while (true) {
                        if (i3 >= ProfileActionsView.this.actions.size()) {
                            action = null;
                            break;
                        }
                        if (((Action) ProfileActionsView.this.actions.get(i3)).key == i) {
                            action = (Action) ProfileActionsView.this.actions.get(i3);
                            break;
                        }
                        i3++;
                    }
                    if (action == null) {
                        return false;
                    }
                    if (i2 == 64) {
                        sendAccessibilityEventForVirtualView(i, 32768);
                        return true;
                    }
                    if (i2 != 16) {
                        return false;
                    }
                    if (ProfileActionsView.this.onActionClickListener != null) {
                        ProfileActionsView.this.onActionClickListener.onClick(i, 0.0f, 0.0f);
                    }
                    return true;
                }

                private void sendAccessibilityEventForVirtualView(int i, int i2) {
                    sendAccessibilityEventForVirtualView(i, i2, null);
                }

                private void sendAccessibilityEventForVirtualView(int i, int i2, String str) {
                    if (((AccessibilityManager) ProfileActionsView.this.getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
                        AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(i2);
                        accessibilityEventObtain.setPackageName(ProfileActionsView.this.getContext().getPackageName());
                        accessibilityEventObtain.setSource(ProfileActionsView.this, i);
                        if (str != null) {
                            accessibilityEventObtain.getText().add(str);
                        }
                        if (ProfileActionsView.this.getParent() != null) {
                            ProfileActionsView.this.getParent().requestSendAccessibilityEvent(ProfileActionsView.this, accessibilityEventObtain);
                        }
                    }
                }
            };
        }
        return this.accessibilityNodeProvider;
    }
}
