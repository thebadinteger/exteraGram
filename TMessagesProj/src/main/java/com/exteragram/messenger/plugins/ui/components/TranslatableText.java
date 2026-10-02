package com.exteragram.messenger.plugins.ui.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.text.Layout;
import android.widget.TextView;
import com.exteragram.messenger.plugins.PluginsConstants;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.LoadingDrawable;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\r\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u0002\u0018\u00002\u00020\u0001B7\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0016\u0010\u0004\u001a\u0012\u0012\u0006\u0012\u0004\u0018\u00010\u0003\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\u0004\b\n\u0010\u000bJ\u0006\u0010+\u001a\u00020\tJ\u0006\u0010,\u001a\u00020\tJ\u000e\u0010-\u001a\u00020\t2\u0006\u0010.\u001a\u00020#J\u000e\u0010/\u001a\u00020&2\u0006\u00100\u001a\u00020&J\u000e\u00101\u001a\u00020&2\u0006\u00102\u001a\u000203J\u0016\u00104\u001a\u00020\t2\u0006\u00102\u001a\u0002032\u0006\u00105\u001a\u00020&J\u0010\u00106\u001a\u00020\t2\u0006\u00102\u001a\u000203H\u0002R\u0013\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u001e\u0010\u0004\u001a\u0012\u0012\u0006\u0012\u0004\u0018\u00010\u0003\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001c\u0010\u000e\u001a\u0004\u0018\u00010\u000fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001c\u0010\u0014\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\r\"\u0004\b\u0016\u0010\u0017R\u001c\u0010\u0018\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\r\"\u0004\b\u001a\u0010\u0017R\u0010\u0010\u001b\u001a\u0004\u0018\u00010\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u001dX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u001e\u001a\u00020\u001fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010 \u001a\u0004\u0018\u00010!X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\"\u001a\u00020#X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010$\u001a\u0004\u0018\u00010!X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010%\u001a\u00020&X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010'\u001a\u00020(X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010)\u001a\u0004\u0018\u00010*X\u0082\u000e¢\u0006\u0002\n\u0000¨\u00067"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/TranslatableText;", _UrlKt.FRAGMENT_ENCODE_SET, "original", _UrlKt.FRAGMENT_ENCODE_SET, "format", "Lkotlin/Function1;", _UrlKt.FRAGMENT_ENCODE_SET, "onTransitionUpdate", "Lkotlin/Function0;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;)V", "getOriginal", "()Ljava/lang/String;", PluginsConstants.Settings.VIEW, "Landroid/widget/TextView;", "getView", "()Landroid/widget/TextView;", "setView", "(Landroid/widget/TextView;)V", "language", "getLanguage", "setLanguage", "(Ljava/lang/String;)V", "translation", "getTranslation", "setTranslation", "shownText", "loadingPath", "Lorg/telegram/ui/Components/LinkPath;", "loadingDrawable", "Lorg/telegram/ui/Components/LoadingDrawable;", "loadingLayout", "Landroid/text/Layout;", "loading", _UrlKt.FRAGMENT_ENCODE_SET, "oldLayout", "oldHeight", _UrlKt.FRAGMENT_ENCODE_SET, "transitionProgress", _UrlKt.FRAGMENT_ENCODE_SET, "transitionAnimator", "Landroid/animation/ValueAnimator;", "startLoading", "stopLoading", "show", "translated", "getHeight", "measuredHeight", "beforeDraw", "canvas", "Landroid/graphics/Canvas;", "afterDraw", "saveCount", "drawLoading", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
final class TranslatableText {
    private final Function1<String, CharSequence> format;
    private String language;
    private boolean loading;
    private final LoadingDrawable loadingDrawable;
    private Layout loadingLayout;
    private final LinkPath loadingPath;
    private int oldHeight;
    private Layout oldLayout;
    private final Function0<Unit> onTransitionUpdate;
    private final String original;
    private String shownText;
    private ValueAnimator transitionAnimator;
    private float transitionProgress;
    private String translation;
    private TextView view;

    /* JADX WARN: Multi-variable type inference failed */
    public TranslatableText(String str, Function1<String, CharSequence> function1, Function0<Unit> function0) {
        this.original = str;
        this.format = function1;
        this.onTransitionUpdate = function0;
        this.shownText = str;
        LinkPath linkPath = new LinkPath(true);
        this.loadingPath = linkPath;
        LoadingDrawable loadingDrawable = new LoadingDrawable();
        loadingDrawable.usePath(linkPath);
        loadingDrawable.setAppearByGradient(true);
        loadingDrawable.setRadiiDp(4.0f);
        this.loadingDrawable = loadingDrawable;
        this.transitionProgress = 1.0f;
    }

    public final String getOriginal() {
        return this.original;
    }

    public final TextView getView() {
        return this.view;
    }

    public final void setView(TextView textView) {
        this.view = textView;
    }

    public final String getLanguage() {
        return this.language;
    }

    public final void setLanguage(String str) {
        this.language = str;
    }

    public final String getTranslation() {
        return this.translation;
    }

    public final void setTranslation(String str) {
        this.translation = str;
    }

    public final void startLoading() {
        this.loading = true;
        this.loadingDrawable.reset();
        this.loadingDrawable.resetDisappear();
        TextView textView = this.view;
        if (textView != null) {
            textView.invalidate();
        }
    }

    public final void stopLoading() {
        this.loadingDrawable.disappear();
        TextView textView = this.view;
        if (textView != null) {
            textView.invalidate();
        }
    }

    public final void show(boolean translated) {
        String str;
        final TextView textView = this.view;
        if (textView == null) {
            return;
        }
        if (!translated || (str = this.translation) == null) {
            str = this.original;
        }
        if (Intrinsics.areEqual(str, this.shownText)) {
            return;
        }
        this.shownText = str;
        ValueAnimator valueAnimator = this.transitionAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        this.oldLayout = textView.getLayout();
        this.oldHeight = textView.getHeight();
        textView.setText(this.format.invoke(str));
        this.transitionProgress = 0.0f;
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.setDuration(350L);
        valueAnimatorOfFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.exteragram.messenger.plugins.ui.components.TranslatableText$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                TranslatableText.$r8$lambda$JRgU_sneSrKGk0HLUPOQr7eYGE4(TranslatableText.this, textView, valueAnimator2);
            }
        });
        valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: com.exteragram.messenger.plugins.ui.components.TranslatableText$show$1$2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                TranslatableText.this.oldLayout = null;
                TranslatableText.this.transitionProgress = 1.0f;
                TranslatableText.this.transitionAnimator = null;
                textView.requestLayout();
                textView.invalidate();
            }
        });
        valueAnimatorOfFloat.start();
        this.transitionAnimator = valueAnimatorOfFloat;
    }

    public static void $r8$lambda$JRgU_sneSrKGk0HLUPOQr7eYGE4(TranslatableText translatableText, TextView textView, ValueAnimator valueAnimator) {
        translatableText.transitionProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        textView.requestLayout();
        textView.invalidate();
        translatableText.onTransitionUpdate.invoke();
    }

    public final int getHeight(int measuredHeight) {
        return this.oldLayout == null ? measuredHeight : AndroidUtilities.lerp(this.oldHeight, measuredHeight, this.transitionProgress);
    }

    public final int beforeDraw(Canvas canvas) {
        Layout layout;
        TextView textView = this.view;
        if (textView == null || (layout = this.oldLayout) == null) {
            return -1;
        }
        canvas.save();
        canvas.clipRect(0, 0, textView.getWidth(), textView.getHeight());
        canvas.translate(textView.getTotalPaddingLeft(), textView.getTotalPaddingTop());
        canvas.saveLayerAlpha(0.0f, 0.0f, layout.getWidth(), layout.getHeight(), (int) ((1.0f - this.transitionProgress) * 255.0f));
        layout.draw(canvas);
        canvas.restore();
        canvas.restore();
        return canvas.saveLayerAlpha(0.0f, 0.0f, textView.getWidth(), textView.getHeight(), (int) (255.0f * this.transitionProgress));
    }

    public final void afterDraw(Canvas canvas, int saveCount) {
        if (saveCount >= 0) {
            canvas.restoreToCount(saveCount);
        }
        drawLoading(canvas);
    }

    private final void drawLoading(Canvas canvas) {
        TextView textView = this.view;
        if (textView != null && this.loading) {
            if (this.loadingDrawable.isDisappeared()) {
                this.loading = false;
                return;
            }
            Layout layout = textView.getLayout();
            if (layout == null) {
                return;
            }
            if (layout != this.loadingLayout) {
                this.loadingLayout = layout;
                this.loadingPath.rewind();
                this.loadingPath.setCurrentLayout(layout, 0, textView.getTotalPaddingLeft(), textView.getTotalPaddingTop());
                layout.getSelectionPath(0, layout.getText().length(), this.loadingPath);
                this.loadingDrawable.updateBounds();
            }
            int currentTextColor = textView.getCurrentTextColor();
            this.loadingDrawable.setColors(Theme.multAlpha(currentTextColor, 0.05f), Theme.multAlpha(currentTextColor, 0.15f), Theme.multAlpha(currentTextColor, 0.1f), Theme.multAlpha(currentTextColor, 0.3f));
            this.loadingDrawable.draw(canvas);
            textView.invalidate();
        }
    }
}
