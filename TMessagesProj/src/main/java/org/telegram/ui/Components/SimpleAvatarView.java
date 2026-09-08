package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import com.exteragram.messenger.ExteraConfig;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ImageReceiver;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;

public class SimpleAvatarView extends View {
    private ValueAnimator animator;
    private AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private boolean isAvatarHidden;
    private Paint selectPaint;
    private float selectProgress;

    public SimpleAvatarView(Context context) {
        super(context);
        this.avatarImage = new ImageReceiver(this);
        this.avatarDrawable = new AvatarDrawable();
        this.selectPaint = new Paint(1);
        this.avatarImage.setRoundRadius(ExteraConfig.getAvatarCorners(56.0f));
        this.selectPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
        this.selectPaint.setStyle(Paint.Style.STROKE);
    }

    @Override // android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.avatarImage.onAttachedToWindow();
    }

    @Override // android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.avatarImage.onDetachedFromWindow();
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        Canvas canvas2;
        super.onDraw(canvas);
        canvas.save();
        float f = (this.selectProgress * 0.1f) + 0.9f;
        canvas.scale(f, f);
        this.selectPaint.setColor(Theme.getColor(Theme.key_dialogTextBlue));
        Paint paint = this.selectPaint;
        paint.setAlpha((int) (Color.alpha(paint.getColor()) * this.selectProgress));
        float strokeWidth = this.selectPaint.getStrokeWidth();
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(strokeWidth, strokeWidth, getWidth() - strokeWidth, getHeight() - strokeWidth);
        if (ExteraConfig.getAvatarCorners() != 28.0f) {
            float f2 = strokeWidth * 2.0f;
            canvas.drawRoundRect(rectF, ExteraConfig.getAvatarCorners(getWidth() - f2, true), ExteraConfig.getAvatarCorners(getWidth() - f2, true), this.selectPaint);
            canvas2 = canvas;
        } else {
            canvas2 = canvas;
            canvas2.drawArc(rectF, -90.0f, this.selectProgress * 360.0f, false, this.selectPaint);
        }
        canvas2.restore();
        if (this.isAvatarHidden) {
            return;
        }
        float strokeWidth2 = this.selectPaint.getStrokeWidth() * 2.5f * this.selectProgress;
        float f3 = 2.0f * strokeWidth2;
        this.avatarImage.setRoundRadius(ExteraConfig.getAvatarCorners(getWidth() - f3, true));
        this.avatarImage.setImageCoords(strokeWidth2, strokeWidth2, getWidth() - f3, getHeight() - f3);
        this.avatarImage.draw(canvas2);
    }

    public void setAvatar(TLObject tLObject) {
        this.avatarDrawable.setInfo(tLObject);
        this.avatarImage.setForUserOrChat(tLObject, this.avatarDrawable);
    }

    public void setAvatarCorners(int i) {
        this.avatarImage.setRoundRadius(i);
    }

    @Override // android.view.View
    public boolean isSelected() {
        return this.selectProgress == 1.0f;
    }

    public void setSelected(boolean z, boolean z2) {
        ValueAnimator valueAnimator = this.animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        if (z2) {
            ValueAnimator duration = ValueAnimator.ofFloat(this.selectProgress, z ? 1.0f : 0.0f).setDuration(250L);
            duration.setInterpolator(CubicBezierInterpolator.DEFAULT);
            duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.SimpleAvatarView$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    this.f$0.lambda$setSelected$0(valueAnimator2);
                }
            });
            duration.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.SimpleAvatarView.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (SimpleAvatarView.this.animator == animator) {
                        SimpleAvatarView.this.animator = null;
                    }
                }
            });
            duration.start();
            this.animator = duration;
            return;
        }
        this.selectProgress = z ? 1.0f : 0.0f;
        invalidate();
    }

    public /* synthetic */ void lambda$setSelected$0(ValueAnimator valueAnimator) {
        this.selectProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public void setHideAvatar(boolean z) {
        this.isAvatarHidden = z;
        invalidate();
    }
}
