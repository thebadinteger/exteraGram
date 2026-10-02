package com.exteragram.messenger.appicons.ui.components;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.appicons.AppIcon;
import com.exteragram.messenger.appicons.AppIconController;
import com.exteragram.messenger.appicons.AppIconPreviewLoader;
import com.exteragram.messenger.utils.text.LocaleUtils;
import java.util.Iterator;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;

/* JADX INFO: loaded from: classes4.dex */
@SuppressLint({"ViewConstructor"})
public class AppIconHeroView extends LinearLayout implements Theme.Colorable {
    private float collapse;
    private final BaseFragment fragment;
    private AppIcon icon;
    private int linkColor;
    private ValueAnimator linkColorAnimator;
    private ClickableSpan pressedLink;
    private Runnable previewReadyListener;
    private int previewSizeDp;
    private final AppIconPreviewView previewView;
    private final Theme.ResourcesProvider resourcesProvider;
    private CharSequence subtitleLinks;
    private final AnimatedTextView subtitleView;
    private final AnimatedTextView titleView;

    @Override // org.telegram.ui.ActionBar.Theme.Colorable
    public /* bridge */ /* synthetic */ int[] getColorKeys() {
        return null;
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public AppIconHeroView(Context context, BaseFragment baseFragment) {
        super(context);
        this.previewSizeDp = 128;
        this.fragment = baseFragment;
        Theme.ResourcesProvider resourceProvider = baseFragment.getResourceProvider();
        this.resourcesProvider = resourceProvider;
        setOrientation(1);
        setGravity(1);
        setClipToPadding(false);
        setClipChildren(false);
        AppIconPreviewView appIconPreviewView = new AppIconPreviewView(context, resourceProvider);
        this.previewView = appIconPreviewView;
        appIconPreviewView.setCrossfade(true);
        appIconPreviewView.setOnPreviewReady(new Runnable() { // from class: com.exteragram.messenger.appicons.ui.components.AppIconHeroView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AppIconHeroView.this.onPreviewReady();
            }
        });
        addView(appIconPreviewView, LayoutHelper.createLinear(128, 128, 1));
        this.titleView = addText(22, AndroidUtilities.bold(), 28, 18);
        this.subtitleView = addText(14, AndroidUtilities.regular(), 20, 6);
        updateColors();
        Iterator<AppIcon> it = AppIconController.getAvailableIcons().iterator();
        boolean z = false;
        while (it.hasNext()) {
            z |= subtitleOf(it.next()) != null;
        }
        this.subtitleView.setVisibility(z ? 0 : 8);
    }

    private static CharSequence subtitleOf(AppIcon appIcon) {
        CharSequence description = appIcon.getDescription();
        return !TextUtils.isEmpty(description) ? description : appIcon.getAuthor();
    }

    @Override // org.telegram.ui.ActionBar.Theme.Colorable
    public void updateColors() {
        int color = Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, this.resourcesProvider);
        this.titleView.setTextColor(color);
        this.subtitleView.setTextColor(ColorUtils.setAlphaComponent(color, 179));
        updateLinkColor(false);
    }

    private void updateLinkColor(boolean z) {
        final int i;
        final int accentTextColor = AppIconPreviewLoader.getAccentTextColor(this.icon, Theme.key_windowBackgroundWhiteLinkText, this.resourcesProvider);
        if (this.linkColor == accentTextColor) {
            return;
        }
        ValueAnimator valueAnimator = this.linkColorAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.linkColorAnimator = null;
        }
        if (!z || (i = this.linkColor) == 0) {
            this.linkColor = accentTextColor;
            this.subtitleView.invalidate();
            return;
        }
        ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(280L);
        this.linkColorAnimator = duration;
        duration.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.linkColorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.exteragram.messenger.appicons.ui.components.AppIconHeroView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                AppIconHeroView.this.lambda$updateLinkColor$0(i, accentTextColor, valueAnimator2);
            }
        });
        this.linkColorAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$updateLinkColor$0(int i, int i2, ValueAnimator valueAnimator) {
        this.linkColor = ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue());
        this.subtitleView.invalidate();
    }

    private AnimatedTextView addText(int i, Typeface typeface, int i2, int i3) {
        AnimatedTextView animatedTextView = new AnimatedTextView(getContext(), true, true, false);
        animatedTextView.setGravity(17);
        animatedTextView.setTypeface(typeface);
        animatedTextView.setTextSize(AndroidUtilities.dp(i));
        animatedTextView.setIncludeFontPadding(false);
        animatedTextView.setAllowCancel(true);
        animatedTextView.setAnimationProperties(0.35f, 0L, 260L, CubicBezierInterpolator.EASE_OUT_QUINT);
        addView(animatedTextView, LayoutHelper.createLinear(-1, i2, 1, 24, i3, 24, 0));
        return animatedTextView;
    }

    public void setOnPreviewReady(Runnable runnable) {
        this.previewReadyListener = runnable;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPreviewReady() {
        updateLinkColor(true);
        Runnable runnable = this.previewReadyListener;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void set(AppIcon appIcon) {
        AppIcon appIcon2 = this.icon;
        if (appIcon2 == appIcon) {
            return;
        }
        boolean z = appIcon2 != null;
        this.icon = appIcon;
        this.previewView.setIcon(appIcon);
        updateLinkColor(z);
        this.titleView.setText(appIcon.getTitle(), z);
        CharSequence charSequenceSubtitleOf = subtitleOf(appIcon);
        CharSequence withUsernames = charSequenceSubtitleOf == null ? _UrlKt.FRAGMENT_ENCODE_SET : LocaleUtils.formatWithUsernames(charSequenceSubtitleOf, this.fragment);
        this.subtitleLinks = withUsernames;
        this.subtitleView.setText(colorizeLinks(withUsernames), z);
    }

    private CharSequence colorizeLinks(CharSequence charSequence) {
        if (!(charSequence instanceof Spanned)) {
            return charSequence;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        for (ClickableSpan clickableSpan : (ClickableSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), ClickableSpan.class)) {
            int spanStart = spannableStringBuilder.getSpanStart(clickableSpan);
            int spanEnd = spannableStringBuilder.getSpanEnd(clickableSpan);
            spannableStringBuilder.removeSpan(clickableSpan);
            spannableStringBuilder.setSpan(new CharacterStyle() { // from class: com.exteragram.messenger.appicons.ui.components.AppIconHeroView.1
                @Override // android.text.style.CharacterStyle
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setColor(Theme.multAlpha(AppIconHeroView.this.linkColor, textPaint.getAlpha() / 255.0f));
                }
            }, spanStart, spanEnd, 33);
        }
        return spannableStringBuilder;
    }

    public boolean dispatchLinkTouch(MotionEvent motionEvent) {
        float x = (motionEvent.getX() - getX()) - this.subtitleView.getX();
        float y = (motionEvent.getY() - getY()) - this.subtitleView.getY();
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            ClickableSpan clickableSpanHitLink = hitLink(x, y);
            this.pressedLink = clickableSpanHitLink;
            return clickableSpanHitLink != null;
        }
        ClickableSpan clickableSpan = this.pressedLink;
        if (clickableSpan == null) {
            return false;
        }
        if (actionMasked == 1 && clickableSpan == hitLink(x, y)) {
            this.pressedLink.onClick(this.subtitleView);
        }
        if (actionMasked == 1 || actionMasked == 3) {
            this.pressedLink = null;
        }
        return true;
    }

    private ClickableSpan hitLink(float f, float f2) {
        if (this.subtitleView.getVisibility() == 0 && this.subtitleView.getAlpha() > 0.0f && f2 >= 0.0f && f2 <= this.subtitleView.getHeight()) {
            CharSequence charSequence = this.subtitleLinks;
            if (charSequence instanceof Spanned) {
                Spanned spanned = (Spanned) charSequence;
                TextPaint paint = this.subtitleView.getPaint();
                float width = (this.subtitleView.getWidth() - this.subtitleView.getDrawable().getCurrentWidth()) / 2.0f;
                for (ClickableSpan clickableSpan : (ClickableSpan[]) spanned.getSpans(0, spanned.length(), ClickableSpan.class)) {
                    int spanStart = spanned.getSpanStart(clickableSpan);
                    int spanEnd = spanned.getSpanEnd(clickableSpan);
                    float fMeasureText = (paint.measureText(spanned, 0, spanStart) + width) - AndroidUtilities.dp(4.0f);
                    float fMeasureText2 = paint.measureText(spanned, spanStart, spanEnd) + fMeasureText + (AndroidUtilities.dp(4.0f) * 2);
                    if (f >= fMeasureText && f <= fMeasureText2) {
                        return clickableSpan;
                    }
                }
            }
        }
        return null;
    }

    public void setCollapse(float f) {
        if (this.collapse == f) {
            return;
        }
        this.collapse = f;
        applyCollapse();
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        applyCollapse();
    }

    private void applyCollapse() {
        float fMin = Math.min(1.0f, this.collapse / 0.35f);
        float f = (-AndroidUtilities.dp(12.0f)) * fMin;
        float f2 = 1.0f - fMin;
        this.titleView.setAlpha(f2);
        this.titleView.setTranslationY(f);
        this.subtitleView.setAlpha(f2);
        this.subtitleView.setTranslationY(f);
        int measuredHeight = this.previewView.getMeasuredHeight();
        if (measuredHeight <= 0) {
            return;
        }
        float f3 = measuredHeight;
        float f4 = f3 / 2.0f;
        float top = this.previewView.getTop() + f4;
        float currentActionBarHeight = (AndroidUtilities.statusBarHeight + (ActionBar.getCurrentActionBarHeight() / 2.0f)) - getTop();
        float fLerp = AndroidUtilities.lerp(1.0f, AndroidUtilities.dp(40.0f) / f3, this.collapse);
        AppIconPreviewView appIconPreviewView = this.previewView;
        appIconPreviewView.setPivotX(appIconPreviewView.getMeasuredWidth() / 2.0f);
        this.previewView.setPivotY(f4);
        this.previewView.setScaleX(fLerp);
        this.previewView.setScaleY(fLerp);
        this.previewView.setTranslationY((currentActionBarHeight - top) * this.collapse);
    }

    public int getTextBlockHeight() {
        int iDp = AndroidUtilities.dp(46.0f);
        return this.subtitleView.getVisibility() == 0 ? iDp + AndroidUtilities.dp(26.0f) : iDp;
    }

    public void setPreviewSizeDp(int i) {
        if (this.previewSizeDp == i) {
            return;
        }
        this.previewSizeDp = i;
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) this.previewView.getLayoutParams();
        int iDp = AndroidUtilities.dp(i);
        layoutParams.height = iDp;
        layoutParams.width = iDp;
        this.previewView.requestLayout();
    }

    public int getPreviewSize() {
        return AndroidUtilities.dp(this.previewSizeDp);
    }
}
