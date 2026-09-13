package com.exteragram.messenger.preferences.chats.components;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.text.TextPaint;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.preferences.components.CustomPreferenceCell;
import com.exteragram.messenger.preferences.components.PreviewBackgroundDrawable;
import com.exteragram.messenger.preferences.components.PreviewColors;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ScaleStateListAnimator;

public abstract class StickerShapeCell extends LinearLayout implements CustomPreferenceCell {
    private final StickerShape[] stickerShape;

    public abstract void updateStickerPreview();

    public StickerShapeCell(Context context) {
        super(context);
        this.stickerShape = new StickerShape[3];
        setOrientation(0);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        setPadding(AndroidUtilities.dp(13.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(13.0f), 0);
        for (int i = 0; i < 3; i++) {
            boolean z = i == 2;
            boolean z2 = i == 1;
            this.stickerShape[i] = new StickerShape(context, z2, z);
            ScaleStateListAnimator.apply(this.stickerShape[i], 0.03f, 1.5f);
            addView(this.stickerShape[i], LayoutHelper.createLinear(-1, -1, 0.5f, 8, 0, 8, 0));
            final int index = i;
            this.stickerShape[i].setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.preferences.chats.components.StickerShapeCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    StickerShapeCell.this.lambda$new$0(index, view);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i, View view) {
        for (int i2 = 0; i2 < 3; i2++) {
            StickerShape stickerShape = this.stickerShape[i2];
            stickerShape.setSelected(view == stickerShape, true);
        }
        ExteraConfig.setStickerShape(i);
        updateStickerPreview();
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        for (int i = 0; i < 3; i++) {
            this.stickerShape[i].invalidate();
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(130.0f), TLObject.FLAG_30));
    }

    @Override // com.exteragram.messenger.preferences.components.CustomPreferenceCell
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof StickerShapeCell) && this.stickerShape == ((StickerShapeCell) obj).stickerShape;
    }

    public static class StickerShape extends FrameLayout {
        private final PreviewBackgroundDrawable backgroundDrawable;
        private final boolean isRounded;
        private final boolean isRoundedAsMsg;
        private float progress;
        private final RectF rect;
        private final TextPaint textPaint;

        public StickerShape(Context context, boolean z, boolean z2) {
            super(context);
            boolean z3 = true;
            TextPaint textPaint = new TextPaint(1);
            this.textPaint = textPaint;
            this.rect = new RectF();
            this.backgroundDrawable = new PreviewBackgroundDrawable(10.0f);
            setWillNotDraw(false);
            this.isRounded = z;
            this.isRoundedAsMsg = z2;
            textPaint.setTextSize(AndroidUtilities.dp(13.0f));
            if ((z || z2 || ExteraConfig.getStickerShape() != 0) && ((!z || ExteraConfig.getStickerShape() != 1) && (!z2 || ExteraConfig.getStickerShape() != 2))) {
                z3 = false;
            }
            setSelected(z3, false);
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            int i;
            this.backgroundDrawable.setBounds(0, 0, getMeasuredWidth(), AndroidUtilities.dp(80.0f));
            this.backgroundDrawable.draw(canvas);
            if (this.isRounded) {
                i = R.string.StickerShapeRounded;
            } else {
                i = this.isRoundedAsMsg ? R.string.StickerShapeRoundedMsg : R.string.Default;
            }
            String string = LocaleController.getString(i);
            canvas.drawText(string, (getMeasuredWidth() - ((int) Math.ceil(this.textPaint.measureText(string)))) >> 1, AndroidUtilities.dp(102.0f), this.textPaint);
            this.rect.set(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), getMeasuredWidth() - AndroidUtilities.dp(10.0f), AndroidUtilities.dp(70.0f));
            Theme.dialogs_onlineCirclePaint.setColor(PreviewColors.getMockColor(false));
            boolean z = this.isRounded;
            if (!z && !this.isRoundedAsMsg) {
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(0.0f), AndroidUtilities.dp(0.0f), Theme.dialogs_onlineCirclePaint);
                return;
            }
            if (z) {
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(6.0f), AndroidUtilities.dp(6.0f), Theme.dialogs_onlineCirclePaint);
                return;
            }
            Rect rect = new Rect();
            this.rect.round(rect);
            int iDp = AndroidUtilities.dp(SharedConfig.bubbleRadius);
            float f = iDp;
            float f2 = iDp / 3;
            ShapeDrawable shapeDrawable = new ShapeDrawable(new RoundRectShape(new float[]{f, f, f, f, f, f, f2, f2}, null, null));
            shapeDrawable.getPaint().setColor(Theme.dialogs_onlineCirclePaint.getColor());
            shapeDrawable.setBounds(rect);
            shapeDrawable.draw(canvas);
        }

        private void setProgress(float f) {
            this.progress = f;
            this.textPaint.setColor(ColorUtils.blendARGB(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), Theme.getColor(Theme.key_windowBackgroundWhiteValueText), f));
            this.textPaint.setTypeface(f >= 0.5f ? AndroidUtilities.bold() : AndroidUtilities.regular());
            this.backgroundDrawable.setSelectionProgress(f);
            invalidate();
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setSelected(boolean z, boolean z2) {
            float f = z ? 1.0f : 0.0f;
            float f2 = this.progress;
            if (f == f2 && z2) {
                return;
            }
            if (z2) {
                ValueAnimator duration = ValueAnimator.ofFloat(f2, f).setDuration(250L);
                duration.setInterpolator(Easings.easeInOutQuad);
                duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.exteragram.messenger.preferences.chats.components.StickerShapeCell$StickerShape$$ExternalSyntheticLambda0
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        StickerShape.this.lambda$setSelected$0(valueAnimator);
                    }
                });
                duration.start();
                return;
            }
            setProgress(f);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$setSelected$0(ValueAnimator valueAnimator) {
            setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }
    }
}
