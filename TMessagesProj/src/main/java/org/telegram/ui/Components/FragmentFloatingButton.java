package org.telegram.ui.Components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.utils.ui.UIUtil;
import java.util.ArrayList;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.utils.ViewOutlineProviderImpl;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProviderThemed;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceColor;

@SuppressLint({"ViewConstructor"})
public class FragmentFloatingButton extends FrameLayout implements FactorAnimator.Target {
    private final int ANIMATOR_ID_BUTTON_VISIBLE;
    private final int ANIMATOR_ID_PROGRESS_VISIBLE;
    private ArrayList<View> additionalContentViews;
    private float additionalTranslationY;
    private final BoolAnimator animatorButtonVisible;
    private final BoolAnimator animatorProgressVisible;
    private BlurredBackgroundDrawable iBlur3Background;
    private BlurredBackgroundColorProviderThemed iBlur3ColorProviderTabs;
    private BlurredBackgroundSourceColor iBlur3SourceColor;
    public final RLottieImageView imageView;
    private float internalTranslationY;
    private final boolean isSubButton;
    public final RadialProgressView progressView;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean setTranslationInternal;

    public FragmentFloatingButton(Context context, Theme.ResourcesProvider resourcesProvider) {
        this(context, resourcesProvider, false);
    }

    public FragmentFloatingButton(Context context, Theme.ResourcesProvider resourcesProvider, boolean z) {
        ViewOutlineProvider viewOutlineProviderBoundsWithPaddingRoundRect;
        super(context);
        this.ANIMATOR_ID_BUTTON_VISIBLE = 0;
        this.ANIMATOR_ID_PROGRESS_VISIBLE = 1;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.animatorButtonVisible = new BoolAnimator(0, this, cubicBezierInterpolator, 380L, true);
        this.animatorProgressVisible = new BoolAnimator(1, this, cubicBezierInterpolator, 380L);
        this.resourcesProvider = resourcesProvider;
        this.isSubButton = z;
        RLottieImageView rLottieImageView = new RLottieImageView(context);
        this.imageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        addView(rLottieImageView, LayoutHelper.createFrame(-1, -1.0f));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.progressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(18.0f));
        radialProgressView.setStrokeWidth(2.0f);
        addView(radialProgressView, LayoutHelper.createFrame(-1, -1.0f));
        setAnimatedVisibility(radialProgressView, 0.0f);
        ScaleStateListAnimator.apply(this);
        if (!z) {
            if (ExteraConfig.getSquareFab()) {
                viewOutlineProviderBoundsWithPaddingRoundRect = ViewOutlineProviderImpl.boundsWithPaddingRoundRect(0, AndroidUtilities.dp(14.0f));
            } else {
                viewOutlineProviderBoundsWithPaddingRoundRect = ViewOutlineProviderImpl.BOUNDS_OVAL;
            }
            setOutlineProvider(viewOutlineProviderBoundsWithPaddingRoundRect);
            setTranslationZ(AndroidUtilities.dpf2(0.5f));
        }
        if (z) {
            this.iBlur3ColorProviderTabs = new BlurredBackgroundColorProviderThemed(null, Theme.key_dialogBackground) { // from class: org.telegram.ui.Components.FragmentFloatingButton.1
                @Override // org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProviderThemed, org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider
                public int getStrokeColorTop() {
                    return isDark() ? 117440511 : 285212672;
                }

                @Override // org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProviderThemed, org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider
                public int getStrokeColorBottom() {
                    return isDark() ? 301989887 : 536870912;
                }

                @Override // org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProviderThemed, org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider
                public int getShadowColor() {
                    return isDark() ? 83886079 : 536870912;
                }
            };
            BlurredBackgroundSourceColor blurredBackgroundSourceColor = new BlurredBackgroundSourceColor();
            this.iBlur3SourceColor = blurredBackgroundSourceColor;
            BlurredBackgroundDrawable blurredBackgroundDrawableCreateDrawable = blurredBackgroundSourceColor.createDrawable();
            this.iBlur3Background = blurredBackgroundDrawableCreateDrawable;
            blurredBackgroundDrawableCreateDrawable.setColorProvider(this.iBlur3ColorProviderTabs);
            this.iBlur3Background.setStrokeWidth(AndroidUtilities.dpf2(0.4f), AndroidUtilities.dpf2(0.4f));
            this.iBlur3Background.setRadius(AndroidUtilities.dp(ExteraConfig.getSquareFab() ? 10.0f : 18.0f));
            this.iBlur3Background.setPadding(AndroidUtilities.dp(5.66f));
        }
        updateColors();
    }

    public void setProgressVisible(boolean z, boolean z2) {
        this.animatorProgressVisible.setValue(z, z2);
    }

    public void setButtonVisible(boolean z, boolean z2) {
        this.animatorButtonVisible.setValue(z, z2);
    }

    public boolean getButtonVisible() {
        return this.animatorButtonVisible.getValue();
    }

    public boolean getProgressVisible() {
        return this.animatorProgressVisible.getValue();
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        int i2 = 0;
        if (i == 0) {
            setAnimatedVisibility(this, f);
            setClickable(f >= 0.99f);
            setAdditionalTranslationY(AndroidUtilities.dp(this.isSubButton ? 64.0f : 40.0f) * (1.0f - f));
        } else if (i == 1) {
            setAnimatedVisibility(this.progressView, f);
            float f3 = 1.0f - f;
            setAnimatedVisibility(this.imageView, f3);
            ArrayList<View> arrayList = this.additionalContentViews;
            if (arrayList != null) {
                int size = arrayList.size();
                while (i2 < size) {
                    View view = arrayList.get(i2);
                    i2++;
                    setAnimatedVisibility(view, f3);
                }
            }
        }
    }

    public void setAnimation(int i, int i2) {
        this.imageView.setAnimation(i, i2, i2);
    }

    public void setImageResource(int i) {
        this.imageView.setImageResource(i);
    }

    public void updateColors() {
        boolean z = this.isSubButton;
        RLottieImageView rLottieImageView = this.imageView;
        if (z) {
            int i = Theme.key_actionBarDefaultIcon;
            rLottieImageView.setColorFilter(Theme.getColor(i, this.resourcesProvider), PorterDuff.Mode.SRC_IN);
            this.progressView.setProgressColor(Theme.getColor(i, this.resourcesProvider));
            this.iBlur3SourceColor.setColor(Theme.getColor(Theme.key_windowBackgroundWhite));
            this.iBlur3ColorProviderTabs.updateColors();
            this.iBlur3Background.updateColors();
            invalidate();
            setBackground(Theme.createInsetRoundRectDrawable(Theme.getColor(Theme.key_listSelector, this.resourcesProvider), AndroidUtilities.dp(ExteraConfig.getSquareFab() ? 10.0f : 18.0f), AndroidUtilities.dp(6.0f)));
            return;
        }
        int i2 = Theme.key_chats_actionIcon;
        rLottieImageView.setColorFilter(Theme.getColor(i2, this.resourcesProvider), PorterDuff.Mode.SRC_IN);
        this.progressView.setProgressColor(Theme.getColor(i2, this.resourcesProvider));
        setBackground(UIUtil.createFabBackground(48, Theme.getColor(Theme.key_featuredStickers_addButton, this.resourcesProvider), Theme.getColor(Theme.key_featuredStickers_addButtonPressed, this.resourcesProvider)));
    }

    public static FrameLayout.LayoutParams createSubButtonLayoutParams() {
        return LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 80, 20.0f, 0.0f, 20.0f, 14.0f);
    }

    public static FrameLayout.LayoutParams createDefaultLayoutParams() {
        return LayoutHelper.createFrame(48, 48.0f, (LocaleController.isRTL ? 3 : 5) | 80, 20.0f, 0.0f, 20.0f, 14.0f);
    }

    public static FrameLayout.LayoutParams createDefaultLayoutParamsBig() {
        return LayoutHelper.createFrame(56, 56.0f, (LocaleController.isRTL ? 3 : 5) | 80, 20.0f, 0.0f, 20.0f, 14.0f);
    }

    private void setAdditionalTranslationY(float f) {
        if (this.additionalTranslationY != f) {
            this.setTranslationInternal = true;
            super.setTranslationY(this.internalTranslationY + f);
            this.setTranslationInternal = false;
            this.additionalTranslationY = f;
        }
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        BlurredBackgroundDrawable blurredBackgroundDrawable = this.iBlur3Background;
        if (blurredBackgroundDrawable != null) {
            blurredBackgroundDrawable.setBounds(0, 0, i, i2);
        }
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        BlurredBackgroundDrawable blurredBackgroundDrawable = this.iBlur3Background;
        if (blurredBackgroundDrawable != null) {
            blurredBackgroundDrawable.draw(canvas);
        }
        super.draw(canvas);
    }

    @Override // android.view.View
    public void setTranslationY(float f) {
        if (this.internalTranslationY != f) {
            this.setTranslationInternal = true;
            super.setTranslationY(this.additionalTranslationY + f);
            this.setTranslationInternal = false;
            this.internalTranslationY = f;
        }
    }

    @Override // android.view.View
    public float getTranslationY() {
        if (this.setTranslationInternal) {
            return super.getTranslationY();
        }
        return this.internalTranslationY;
    }

    public void addAdditionalView(View view) {
        if (this.additionalContentViews == null) {
            this.additionalContentViews = new ArrayList<>();
        }
        this.additionalContentViews.add(view);
        setAnimatedVisibility(view, 1.0f - this.animatorProgressVisible.getFloatValue());
    }

    public static void setAnimatedVisibility(View view, float f) {
        if (view == null) {
            return;
        }
        view.setAlpha(f);
        view.setScaleX(AndroidUtilities.lerp(0.4f, 1.0f, f));
        view.setScaleY(AndroidUtilities.lerp(0.4f, 1.0f, f));
        view.setVisibility(f > 0.0f ? 0 : 8);
    }
}
