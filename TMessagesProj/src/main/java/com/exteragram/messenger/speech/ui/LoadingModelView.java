package com.exteragram.messenger.speech.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.core.math.MathUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.StickerImageView;

public class LoadingModelView extends FrameLayout {
    StickerImageView imageView;
    AnimatedTextView percentsTextView;
    ProgressView progressView;
    TextView subtitle;
    TextView title;

    public LoadingModelView(Context context) {
        super(context);
        StickerImageView stickerImageView = new StickerImageView(context, UserConfig.selectedAccount);
        this.imageView = stickerImageView;
        stickerImageView.getImageReceiver().setAutoRepeat(1);
        this.imageView.setStickerPackName("UtyaDuck");
        this.imageView.setStickerNum(16);
        addView(this.imageView, LayoutHelper.createFrame(150, 150.0f, 49, 0.0f, 16.0f, 0.0f, 0.0f));
        AnimatedTextView animatedTextView = new AnimatedTextView(context, false, true, true);
        this.percentsTextView = animatedTextView;
        animatedTextView.setAnimationProperties(0.35f, 0L, 120L, CubicBezierInterpolator.EASE_OUT);
        this.percentsTextView.setGravity(1);
        AnimatedTextView animatedTextView2 = this.percentsTextView;
        int i = Theme.key_dialogTextBlack;
        animatedTextView2.setTextColor(Theme.getColor(i));
        this.percentsTextView.setTextSize(AndroidUtilities.dp(24.0f));
        this.percentsTextView.setTypeface(AndroidUtilities.bold());
        addView(this.percentsTextView, LayoutHelper.createFrame(-1, 32.0f, 49, 0.0f, 176.0f, 0.0f, 0.0f));
        ProgressView progressView = new ProgressView(context);
        this.progressView = progressView;
        addView(progressView, LayoutHelper.createFrame(240, 5.0f, 49, 0.0f, 226.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.title = textView;
        textView.setGravity(1);
        this.title.setTextColor(Theme.getColor(i));
        this.title.setTextSize(1, 16.0f);
        this.title.setTypeface(AndroidUtilities.bold());
        this.title.setText(LocaleController.getString(R.string.DownloadingModel));
        addView(this.title, LayoutHelper.createFrame(-1, -2.0f, 49, 0.0f, 261.0f, 0.0f, 0.0f));
        TextView textView2 = new TextView(context);
        this.subtitle = textView2;
        textView2.setGravity(1);
        this.subtitle.setTextColor(Theme.getColor(i));
        this.subtitle.setTextSize(1, 14.0f);
        this.subtitle.setText(LocaleController.getString(R.string.DownloadingModelInfo));
        addView(this.subtitle, LayoutHelper.createFrame(240, -2.0f, 49, 0.0f, 289.0f, 0.0f, 0.0f));
        setProgress(0.0f);
    }

    public void setProgress(float f) {
        int iCeil = (int) Math.ceil(MathUtils.clamp(f, 0.0f, 1.0f) * 100.0f);
        this.percentsTextView.cancelAnimation();
        this.percentsTextView.setText(iCeil == 100 ? LocaleController.getString(R.string.ModelUnzipping) : String.format("%s%%", Integer.valueOf(iCeil)), !LocaleController.isRTL);
        this.progressView.setProgress(f);
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(350.0f), TLObject.FLAG_30));
    }

    public static class ProgressView extends View {
        Paint in;
        Paint out;
        float progress;
        AnimatedFloat progressT;

        public ProgressView(Context context) {
            super(context);
            this.in = new Paint(1);
            this.out = new Paint(1);
            this.progressT = new AnimatedFloat(this, 350L, CubicBezierInterpolator.EASE_OUT);
            Paint paint = this.in;
            int i = Theme.key_switchTrackChecked;
            paint.setColor(Theme.getColor(i));
            this.out.setColor(Theme.multAlpha(Theme.getColor(i), 0.2f));
        }

        public void setProgress(float f) {
            this.progress = f;
            invalidate();
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight());
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), this.out);
            rectF.set(0.0f, 0.0f, getMeasuredWidth() * this.progressT.set(this.progress), getMeasuredHeight());
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), this.in);
        }
    }
}
