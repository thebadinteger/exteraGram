package org.telegram.ui.Components.chat.buttons;

import android.content.Context;
import android.graphics.BlendMode;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Build;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.exteragram.messenger.ExteraConfig;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider;

public class ChatActivityBlurredRoundButton extends FrameLayout implements FactorAnimator.Target {
    private final BoolAnimator animatorIsEnabled;
    private final BoolAnimator animatorLoadingVisibility;
    private BlurredBackgroundDrawable backgroundDrawable;
    private float buttonScaleY;
    private int iconColor;
    private ImageView imageView;
    private CircularProgressDrawable loadingIndicatorDrawable;
    private ImageView loadingIndicatorView;
    private Theme.ResourcesProvider resourcesProvider;

    public ChatActivityBlurredRoundButton(Context context) {
        super(context);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.animatorLoadingVisibility = new BoolAnimator(0, this, cubicBezierInterpolator, 320L);
        this.animatorIsEnabled = new BoolAnimator(1, this, cubicBezierInterpolator, 320L, true);
        this.buttonScaleY = 1.0f;
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        this.backgroundDrawable.setBounds(0, 0, i, i2);
    }

    @Override // android.view.View
    public void draw(Canvas canvas) {
        this.backgroundDrawable.draw(canvas);
        super.draw(canvas);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    public void setIcon(int i) {
        setIcon(i, 48);
    }

    public void setIcon(int i, int i2) {
        if (this.imageView == null) {
            if (i == 0) {
                return;
            }
            ImageView imageView = new ImageView(getContext());
            this.imageView = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            addView(this.imageView, LayoutHelper.createFrame(i2, i2, 17));
            checkUi_IconViewVisibility();
        }
        this.imageView.setImageResource(i);
    }

    public void setIconPadding(int i) {
        ImageView imageView = this.imageView;
        if (imageView != null) {
            imageView.setPadding(0, i, 0, 0);
        }
    }

    public void reverseIconByY() {
        this.buttonScaleY = -1.0f;
        checkUi_IconViewVisibility();
    }

    public void setIconColor(int i) {
        ImageView imageView = this.imageView;
        if (imageView == null) {
            return;
        }
        this.iconColor = i;
        if (Build.VERSION.SDK_INT >= 29) {
            ChatActivityBlurredRoundButton$$ExternalSyntheticApiModelOutline1.m();
            imageView.setColorFilter(ChatActivityBlurredRoundButton$$ExternalSyntheticApiModelOutline0.m(i, BlendMode.SRC_IN));
        } else {
            imageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        setEnabled(z, false);
    }

    public void setEnabled(boolean z, boolean z2) {
        super.setEnabled(z);
        this.animatorIsEnabled.setValue(z, z2);
    }

    public void setBlurredBackgroundDrawable(BlurredBackgroundDrawable blurredBackgroundDrawable) {
        this.backgroundDrawable = blurredBackgroundDrawable;
        blurredBackgroundDrawable.setPadding(AndroidUtilities.dp(4.0f));
        this.backgroundDrawable.setRadius(AndroidUtilities.dp(24.0f));
    }

    public void showLoading(boolean z, boolean z2) {
        showLoading(z, z2, 24.0f, 3.0f, 0.75f, 7.0f, 6.0f);
    }

    public void showLoading(boolean z, boolean z2, float f, float f2, float f3, float f4, float f5) {
        if (this.loadingIndicatorView == null) {
            if (!z) {
                return;
            }
            if (ExteraConfig.getNewLoadingStyle()) {
                CircularProgressDrawable circularProgressDrawable = new CircularProgressDrawable(AndroidUtilities.dp(f), AndroidUtilities.dp(f2), -9079435, this.iconColor);
                this.loadingIndicatorDrawable = circularProgressDrawable;
                circularProgressDrawable.setStyle(3, getContext());
                this.loadingIndicatorDrawable.setWavyValues(f3, f4, f5);
            } else {
                this.loadingIndicatorDrawable = new CircularProgressDrawable(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(1.7f), -9079435);
            }
            this.loadingIndicatorDrawable.setAngleOffset(90.0f);
            ImageView imageView = new ImageView(getContext());
            this.loadingIndicatorView = imageView;
            imageView.setBackground(this.loadingIndicatorDrawable);
            this.loadingIndicatorView.setVisibility(8);
            addView(this.loadingIndicatorView, LayoutHelper.createFrame(46, 46, 17));
        }
        if (!this.animatorLoadingVisibility.getValue() && this.animatorLoadingVisibility.getFloatValue() == 0.0f) {
            this.loadingIndicatorDrawable.reset();
        }
        this.animatorLoadingVisibility.setValue(z, z2);
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        if (i == 0) {
            checkUi_IconViewVisibility();
            checkUi_LoadingViewVisibility();
        }
        if (i == 1) {
            checkUi_IconViewVisibility();
            checkUi_LoadingViewVisibility();
        }
    }

    public static ChatActivityBlurredRoundButton create(Context context, BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory, BlurredBackgroundColorProvider blurredBackgroundColorProvider, Theme.ResourcesProvider resourcesProvider) {
        int color = Theme.getColor(Theme.key_glass_defaultIcon, resourcesProvider);
        ChatActivityBlurredRoundButton chatActivityBlurredRoundButton = new ChatActivityBlurredRoundButton(context);
        chatActivityBlurredRoundButton.resourcesProvider = resourcesProvider;
        chatActivityBlurredRoundButton.setBlurredBackgroundDrawable(blurredBackgroundDrawableViewFactory.create(chatActivityBlurredRoundButton, blurredBackgroundColorProvider));
        chatActivityBlurredRoundButton.setIconColor(color);
        chatActivityBlurredRoundButton.setBackground(Theme.createInsetRoundRectDrawable(Theme.multAlpha(color, 0.15f), AndroidUtilities.dp(22.0f), AndroidUtilities.dp(6.0f)));
        return chatActivityBlurredRoundButton;
    }

    public static ChatActivityBlurredRoundButton create(Context context, BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory, BlurredBackgroundColorProvider blurredBackgroundColorProvider, Theme.ResourcesProvider resourcesProvider, int i, int i2) {
        int color = Theme.getColor(Theme.key_glass_defaultIcon, resourcesProvider);
        ChatActivityBlurredRoundButton chatActivityBlurredRoundButton = new ChatActivityBlurredRoundButton(context);
        chatActivityBlurredRoundButton.resourcesProvider = resourcesProvider;
        chatActivityBlurredRoundButton.setBlurredBackgroundDrawable(blurredBackgroundDrawableViewFactory.create(chatActivityBlurredRoundButton, blurredBackgroundColorProvider));
        chatActivityBlurredRoundButton.setIcon(i, i2);
        chatActivityBlurredRoundButton.setIconColor(color);
        chatActivityBlurredRoundButton.setBackground(Theme.createInsetRoundRectDrawable(Theme.multAlpha(color, 0.15f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(4.0f)));
        return chatActivityBlurredRoundButton;
    }

    public void updateColors() {
        BlurredBackgroundDrawable blurredBackgroundDrawable = this.backgroundDrawable;
        if (blurredBackgroundDrawable != null) {
            blurredBackgroundDrawable.updateColors();
            invalidate();
        }
        int i = Theme.key_glass_defaultIcon;
        int color = Theme.getColor(i, this.resourcesProvider);
        setIconColor(Theme.getColor(i, this.resourcesProvider));
        setBackground(Theme.createInsetRoundRectDrawable(Theme.multAlpha(color, 0.15f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(4.0f)));
    }

    private void checkUi_IconViewVisibility() {
        float floatValue = 1.0f - this.animatorLoadingVisibility.getFloatValue();
        float fLerp = AndroidUtilities.lerp(floatValue / 2.0f, floatValue, this.animatorIsEnabled.getFloatValue());
        ImageView imageView = this.imageView;
        if (imageView != null) {
            imageView.setAlpha(fLerp);
            this.imageView.setScaleX(AndroidUtilities.lerp(0.4f, 1.0f, floatValue));
            this.imageView.setScaleY(AndroidUtilities.lerp(0.4f, 1.0f, floatValue) * this.buttonScaleY);
            this.imageView.setVisibility(floatValue > 0.0f ? 0 : 8);
        }
    }

    private void checkUi_LoadingViewVisibility() {
        float floatValue = this.animatorLoadingVisibility.getFloatValue();
        float fLerp = AndroidUtilities.lerp(floatValue / 2.0f, floatValue, this.animatorIsEnabled.getFloatValue());
        ImageView imageView = this.loadingIndicatorView;
        if (imageView != null) {
            imageView.setAlpha(fLerp);
            this.loadingIndicatorView.setScaleX(AndroidUtilities.lerp(0.4f, 1.0f, floatValue));
            this.loadingIndicatorView.setScaleY(AndroidUtilities.lerp(0.4f, 1.0f, floatValue));
            int i = floatValue > 0.0f ? 0 : 8;
            if (this.loadingIndicatorView.getVisibility() != i) {
                this.loadingIndicatorView.setVisibility(i);
                this.loadingIndicatorDrawable.reset();
            }
        }
    }
}
