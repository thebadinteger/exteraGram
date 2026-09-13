package com.exteragram.messenger.preferences.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.utils.ui.MaterialSliderUiHelper;
import com.google.android.material.slider.Slider;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;

@SuppressLint({"ViewConstructor"})
public class AltSeekbar extends FrameLayout {
    protected float currentValue;
    private final AnimatedTextView headerValue;
    protected final TextView leftTextView;
    private final int max;
    private final int min;
    private final OnDrag onDrag;
    protected final TextView rightTextView;
    private int roundedValue;
    public SeekBarView seekBarView;
    public Slider slider;
    private int vibro;

    public interface OnDrag {
        void run(float f);
    }

    public boolean useExactEndpointHaptic() {
        return false;
    }

    public AltSeekbar(Context context, OnDrag onDrag, int i, int i2, String str, String str2, String str3) {
        super(context);
        this.vibro = -1;
        this.onDrag = onDrag;
        this.max = i2;
        this.min = i;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setGravity(LocaleController.isRTL ? 5 : 3);
        TextView textView = new TextView(context);
        textView.setTextSize(1, 15.0f);
        textView.setTypeface(AndroidUtilities.bold());
        int i3 = Theme.key_windowBackgroundWhiteBlueHeader;
        textView.setTextColor(Theme.getColor(i3));
        textView.setGravity(LocaleController.isRTL ? 5 : 3);
        textView.setText(str);
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 16));
        AnimatedTextView animatedTextView = new AnimatedTextView(context, false, true, true) { // from class: com.exteragram.messenger.preferences.components.AltSeekbar.1
            final Drawable backgroundDrawable = Theme.createRoundRectDrawable(AndroidUtilities.dp(4.0f), Theme.multAlpha(Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader), 0.15f));

            @Override // org.telegram.ui.Components.AnimatedTextView, android.view.View
            public void onDraw(Canvas canvas) {
                this.backgroundDrawable.setBounds(0, 0, (int) (getPaddingLeft() + getDrawable().getCurrentWidth() + getPaddingRight()), getMeasuredHeight());
                this.backgroundDrawable.draw(canvas);
                super.onDraw(canvas);
            }
        };
        this.headerValue = animatedTextView;
        animatedTextView.setAnimationProperties(0.45f, 0L, 240L, CubicBezierInterpolator.EASE_OUT_QUINT);
        animatedTextView.setAllowCancel(true);
        animatedTextView.setTypeface(AndroidUtilities.bold());
        animatedTextView.setPadding(AndroidUtilities.dp(5.33f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(5.33f), AndroidUtilities.dp(2.0f));
        animatedTextView.setTextSize(AndroidUtilities.dp(12.0f));
        animatedTextView.setTextColor(Theme.getColor(i3));
        linearLayout.addView(animatedTextView, LayoutHelper.createLinear(-2, 17, 16, 6, 1, 0, 0));
        addView(linearLayout, LayoutHelper.createFrame(-1, -2.0f, 55, 21.0f, 17.0f, 21.0f, 0.0f));
        FrameLayout frameLayout = new FrameLayout(context);
        TextView textView2 = new TextView(context);
        this.leftTextView = textView2;
        textView2.setTextSize(1, 13.0f);
        int i4 = Theme.key_windowBackgroundWhiteGrayText;
        textView2.setTextColor(Theme.getColor(i4));
        textView2.setGravity(3);
        textView2.setText(str2);
        frameLayout.addView(textView2, LayoutHelper.createFrame(-2, -2, 19));
        TextView textView3 = new TextView(context);
        this.rightTextView = textView3;
        textView3.setTextSize(1, 13.0f);
        textView3.setTextColor(Theme.getColor(i4));
        textView3.setGravity(5);
        textView3.setText(str3);
        frameLayout.addView(textView3, LayoutHelper.createFrame(-2, -2, 21));
        addView(frameLayout, LayoutHelper.createFrame(-1, -2.0f, 55, 21.0f, 52.0f, 21.0f, 0.0f));
        initSlider();
    }

    private void updateValues() {
        int i = this.max;
        int i2 = this.min;
        int i3 = ((i - i2) / 2) + i2;
        float f = this.currentValue;
        float f2 = i3 * 1.5f;
        if (f >= f2 - (i2 * 0.5f)) {
            TextView textView = this.rightTextView;
            int i4 = Theme.key_windowBackgroundWhiteGrayText;
            int color = Theme.getColor(i4);
            int color2 = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText);
            float f3 = this.currentValue;
            int i5 = this.min;
            textView.setTextColor(ColorUtils.blendARGB(color, color2, (f3 - (f2 - (i5 * 0.5f))) / (this.max - (f2 - (i5 * 0.5f)))));
            this.leftTextView.setTextColor(Theme.getColor(i4));
            return;
        }
        float f4 = (i2 + i3) * 0.5f;
        TextView textView2 = this.leftTextView;
        if (f <= f4) {
            int i6 = Theme.key_windowBackgroundWhiteGrayText;
            int color3 = Theme.getColor(i6);
            int color4 = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText);
            float f5 = this.currentValue;
            int i7 = this.min;
            textView2.setTextColor(ColorUtils.blendARGB(color3, color4, (f5 - ((i3 + i7) * 0.5f)) / (i7 - ((i3 + i7) * 0.5f))));
            this.rightTextView.setTextColor(Theme.getColor(i6));
            return;
        }
        int i8 = Theme.key_windowBackgroundWhiteGrayText;
        textView2.setTextColor(Theme.getColor(i8));
        this.rightTextView.setTextColor(Theme.getColor(i8));
    }

    public void setProgress(float f) {
        this.currentValue = f;
        this.roundedValue = Math.round(f);
        Slider slider = this.slider;
        if (slider == null) {
            SeekBarView seekBarView = this.seekBarView;
            if (seekBarView != null) {
                int i = this.min;
                seekBarView.setProgress((f - i) / (this.max - i));
            }
        } else if (slider.getValue() != f) {
            this.slider.setValue(f);
        }
        this.headerValue.cancelAnimation();
        this.headerValue.setText(getTextForHeader(), true);
        checkEndpointHaptic(f);
        updateValues();
    }

    public void updateHeader(float f) {
        this.currentValue = f;
        this.roundedValue = Math.round(f);
        CharSequence textForHeader = getTextForHeader();
        if (!TextUtils.equals(this.headerValue.getText(), textForHeader)) {
            this.headerValue.setText(textForHeader, true);
        }
        checkEndpointHaptic(f);
        updateValues();
    }

    /* JADX WARN: Code duplicated, block: B:15:0x0022  */
    private void checkEndpointHaptic(float f) {
        int i;
        if (useExactEndpointHaptic()) {
            i = this.min;
            if (f > i) {
                i = this.max;
                if (f < i) {
                    i = -1;
                }
            }
        } else {
            i = this.roundedValue;
            if (i != this.min && i != this.max) {
                i = -1;
            }
        }
        if (i != -1) {
            if (i != this.vibro) {
                this.vibro = i;
                performHapticFeedback(4, 2);
                return;
            }
            return;
        }
        this.vibro = -1;
    }

    public CharSequence getTextForHeader() {
        CharSequence charSequenceValueOf;
        int i = this.roundedValue;
        if (i == this.min) {
            charSequenceValueOf = this.leftTextView.getText();
        } else if (i == this.max) {
            charSequenceValueOf = this.rightTextView.getText();
        } else {
            charSequenceValueOf = String.valueOf(i);
        }
        return charSequenceValueOf.toString().toUpperCase();
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(112.0f), TLObject.FLAG_30));
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof AltSeekbar) {
            AltSeekbar altSeekbar = (AltSeekbar) obj;
            if (Objects.equals(this.headerValue, altSeekbar.headerValue) && Objects.equals(this.leftTextView, altSeekbar.leftTextView) && Objects.equals(this.rightTextView, altSeekbar.rightTextView) && Objects.equals(this.seekBarView, altSeekbar.seekBarView) && Objects.equals(this.slider, altSeekbar.slider) && this.min == altSeekbar.min && this.max == altSeekbar.max && Float.compare(this.currentValue, altSeekbar.currentValue) == 0 && this.roundedValue == altSeekbar.roundedValue && this.vibro == altSeekbar.vibro) {
                return true;
            }
        }
        return false;
    }

    private void initSlider() {
        if (ExteraConfig.getNewSliderStyle()) {
            Slider sliderCreate = MaterialSliderUiHelper.create(getContext());
            this.slider = sliderCreate;
            MaterialSliderUiHelper.applyContinuousStyle(sliderCreate);
            this.slider.addOnChangeListener(new Slider.OnChangeListener() { // from class: com.exteragram.messenger.preferences.components.AltSeekbar$$ExternalSyntheticLambda0
                @Override // com.google.android.material.slider.Slider.OnChangeListener
                public final void onValueChange(Slider slider, float f, boolean z) {
                    AltSeekbar.this.lambda$initSlider$0(slider, f, z);
                }
            });
            MaterialSliderUiHelper.applyColors(this.slider, Theme.getColor(Theme.key_player_progress), Theme.getColor(Theme.key_player_progressBackground));
            this.slider.setValueFrom(this.min);
            this.slider.setValueTo(this.max);
            addView(this.slider, LayoutHelper.createFrame(-1, 56.0f, 48, 7.0f, 68.0f, 7.0f, 6.0f));
        } else {
            SeekBarView seekBarView = new SeekBarView(getContext(), true, null);
            this.seekBarView = seekBarView;
            seekBarView.setReportChanges(true);
            this.seekBarView.setDelegate(new SeekBarView.SeekBarViewDelegate() { // from class: com.exteragram.messenger.preferences.components.AltSeekbar$$ExternalSyntheticLambda1
                @Override // org.telegram.ui.Components.SeekBarView.SeekBarViewDelegate
                public final void onSeekBarDrag(boolean z, float f) {
                    AltSeekbar.this.lambda$initSlider$1(z, f);
                }
            });
            addView(this.seekBarView, LayoutHelper.createFrame(-1, 44.0f, 48, 6.0f, 68.0f, 6.0f, 0.0f));
        }
        setProgress(this.currentValue);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initSlider$0(Slider slider, float f, boolean z) {
        if (z) {
            this.onDrag.run(f);
        }
        if (Math.round(f) != this.roundedValue) {
            setProgress(f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$initSlider$1(boolean z, float f) {
        int i = this.min;
        float f2 = i + ((this.max - i) * f);
        this.onDrag.run(f2);
        if (Math.round(f2) != this.roundedValue) {
            setProgress(f2);
        }
    }

    public void updateStyle() {
        Slider slider = this.slider;
        if (slider != null) {
            removeView(slider);
            this.slider = null;
        }
        SeekBarView seekBarView = this.seekBarView;
        if (seekBarView != null) {
            removeView(seekBarView);
            this.seekBarView = null;
        }
        initSlider();
    }
}
