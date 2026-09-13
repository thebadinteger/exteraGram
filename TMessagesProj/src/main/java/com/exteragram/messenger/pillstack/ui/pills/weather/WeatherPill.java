package com.exteragram.messenger.pillstack.ui.pills.weather;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.location.Location;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.exteragram.messenger.pillstack.core.PillStackConfig;
import com.exteragram.messenger.pillstack.core.PillType;
import com.exteragram.messenger.pillstack.ui.pills.BasePill;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stories.recorder.Weather;

@SuppressLint({"ViewConstructor"})
public class WeatherPill extends BasePill implements NotificationCenter.NotificationCenterDelegate {
    private final ImageView iconView;
    private final LinearLayout layout;
    private boolean showingWeather;
    private final AnimatedTextView textView;

    @Override // com.exteragram.messenger.pillstack.ui.pills.BasePill
    public long getRefreshInterval() {
        return 1200000L;
    }

    public WeatherPill(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        LinearLayout linearLayout = new LinearLayout(context);
        this.layout = linearLayout;
        linearLayout.setOrientation(0);
        linearLayout.setGravity(17);
        linearLayout.setMinimumWidth(AndroidUtilities.dp(48.0f));
        linearLayout.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f), 0);
        addView(linearLayout, LayoutHelper.createFrame(-2, 28, (LocaleController.isRTL ? 3 : 5) | 16));
        ImageView imageView = new ImageView(context);
        this.iconView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        linearLayout.addView(imageView, LayoutHelper.createLinear(16, 16, 16, 0, 0, 4, 0));
        AnimatedTextView animatedTextView = new AnimatedTextView(context, true, true, true);
        this.textView = animatedTextView;
        animatedTextView.setTextSize(AndroidUtilities.dp(13.0f));
        animatedTextView.setTypeface(AndroidUtilities.bold());
        animatedTextView.setIncludeFontPadding(false);
        animatedTextView.adaptWidth = true;
        NotificationCenter.listenEmojiLoading(animatedTextView);
        linearLayout.addView(animatedTextView, LayoutHelper.createLinear(-2, -2, 16));
        setLoadingTargetView(linearLayout);
        updateColors();
        ScaleStateListAnimator.apply(linearLayout);
        Weather.State cached = Weather.getCached();
        if (cached != null) {
            setData(cached, false);
        }
    }

    @Override // com.exteragram.messenger.pillstack.ui.pills.BasePill
    public int getPillId() {
        return PillType.WEATHER.getId();
    }

    @Override // com.exteragram.messenger.pillstack.ui.pills.BasePill
    public void onPillClicked() {
        if (PillStackConfig.getUseCurrentLocation() && !this.showingWeather && (!Weather.isLocationPermissionGranted() || !Weather.isLocationEnabled())) {
            requestLocationAndUpdate();
        } else {
            onPillLongClicked();
        }
    }

    @Override // com.exteragram.messenger.pillstack.ui.pills.BasePill
    public boolean onPillLongClicked() {
        final BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return false;
        }
        ItemOptions.makeOptions(safeLastFragment, this).add(R.drawable.msg_retry, LocaleController.getString(R.string.Refresh), new Runnable() { // from class: com.exteragram.messenger.pillstack.ui.pills.weather.WeatherPill$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                WeatherPill.this.lambda$onPillLongClicked$0();
            }
        }).add(R.drawable.msg_settings, LocaleController.getString(R.string.Settings), new Runnable() { // from class: com.exteragram.messenger.pillstack.ui.pills.weather.WeatherPill$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                safeLastFragment.presentFragment(new WeatherSettingsActivity());
            }
        }).setDrawScrim(false).setDimAlpha(0).show();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onPillLongClicked$0() {
        onUpdateData(true);
    }

    @Override // com.exteragram.messenger.pillstack.ui.pills.BasePill
    public void onUpdateData(boolean z) {
        if (PillStackConfig.getUseCurrentLocation()) {
            if (!Weather.isLocationPermissionGranted()) {
                setLocationState(R.string.WeatherLocationPermissionGrant, this.showingWeather);
                return;
            } else if (!Weather.isLocationEnabled()) {
                setLocationState(R.string.WeatherLocationServicesEnable, this.showingWeather);
                return;
            }
        }
        if (z) {
            Weather.clearCache();
        }
        startLoading();
        Weather.fetchExtera(new Utilities.Callback() { // from class: com.exteragram.messenger.pillstack.ui.pills.weather.WeatherPill$$ExternalSyntheticLambda3
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                WeatherPill.this.lambda$onUpdateData$4((Weather.State) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onUpdateData$4(final Weather.State state) {
        if (state != null) {
            markDataUpdated();
            postDelayed(new Runnable() { // from class: com.exteragram.messenger.pillstack.ui.pills.weather.WeatherPill$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    WeatherPill.this.lambda$onUpdateData$2(state);
                }
            }, 300L);
        } else {
            postDelayed(new Runnable() { // from class: com.exteragram.messenger.pillstack.ui.pills.weather.WeatherPill$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    WeatherPill.this.lambda$onUpdateData$3();
                }
            }, 300L);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onUpdateData$2(Weather.State state) {
        setData(state, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onUpdateData$3() {
        setErrorState(true);
    }

    @Override // com.exteragram.messenger.pillstack.ui.pills.BasePill, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (PillStackConfig.checkAndClearPendingUpdate(getPillId()) || Weather.getCached() == null || isRefreshDue()) {
            onUpdateData(true);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pillStackSettingsChanged);
    }

    @Override // com.exteragram.messenger.pillstack.ui.pills.BasePill, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pillStackSettingsChanged);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.pillStackSettingsChanged && PillStackConfig.shouldUpdatePill(objArr, getPillId())) {
            PillStackConfig.checkAndClearPendingUpdate(getPillId());
            onUpdateData(true);
        }
    }

    private void setLocationState(int i, boolean z) {
        stopLoading();
        if (z) {
            animateSizeChange();
        }
        this.iconView.setImageResource(R.drawable.filled_location);
        this.iconView.setVisibility(0);
        this.textView.setText(LocaleController.getString(i), z);
        this.showingWeather = false;
    }

    private void requestLocationAndUpdate() {
        Weather.getUserLocation(true, new Utilities.Callback() { // from class: com.exteragram.messenger.pillstack.ui.pills.weather.WeatherPill$$ExternalSyntheticLambda2
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                WeatherPill.this.lambda$requestLocationAndUpdate$5((Location) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestLocationAndUpdate$5(Location location) {
        if (location != null) {
            onUpdateData(true);
        }
    }

    private void setErrorState(boolean z) {
        stopLoading();
        if (z) {
            animateSizeChange();
        }
        this.iconView.setImageResource(R.drawable.msg_retry);
        this.iconView.setVisibility(0);
        this.textView.setText(LocaleController.getString(R.string.Retry), z);
        this.showingWeather = false;
    }

    public void setData(Weather.State state, boolean z) {
        stopLoading();
        if (state == null) {
            return;
        }
        if (z) {
            animateSizeChange();
        }
        int weatherIconRes = getWeatherIconRes(state.getEmoji());
        ImageView imageView = this.iconView;
        if (weatherIconRes != 0) {
            imageView.setImageResource(weatherIconRes);
            this.iconView.setVisibility(0);
            this.textView.setText(state.getTemperature(), z);
        } else {
            imageView.setVisibility(8);
            this.textView.setText(Emoji.replaceEmoji(String.format("%s %s", state.getEmoji(), state.getTemperature()), this.textView.getPaint().getFontMetricsInt(), true), z);
        }
        this.showingWeather = true;
    }

    private int getWeatherIconRes(String str) {
        if (str == null) {
            return 0;
        }
        switch (str) {
            case "☀":
                return R.drawable.weather_sunny;
            case "☁":
                return R.drawable.weather_cloudy;
            case "⚡":
            case "⛈":
                return R.drawable.weather_thunderstorm;
            case "⛅":
            case "🌤":
                return R.drawable.weather_partly_cloudy;
            case "❄":
            case "🌨":
                return R.drawable.weather_snowy;
            case "🌓":
            case "🌔":
            case "🌖":
            case "🌗":
            case "🌚":
            case "🌛":
            case "🌜":
            case "🌝":
                return R.drawable.weather_night;
            case "🌦":
            case "🌧":
                return R.drawable.weather_rainy;
            case "😶‍🌫":
                return R.drawable.weather_foggy;
            default:
                return 0;
        }
    }

    @Override // android.view.View
    public void setPressed(boolean z) {
        if (this.loading) {
            z = false;
        }
        super.setPressed(z);
        this.layout.setPressed(z);
    }

    @Override // android.view.View
    public void drawableHotspotChanged(float f, float f2) {
        if (this.loading) {
            return;
        }
        super.drawableHotspotChanged(f, f2);
        LinearLayout linearLayout = this.layout;
        linearLayout.drawableHotspotChanged(f - linearLayout.getLeft(), f2 - this.layout.getTop());
    }

    @Override // com.exteragram.messenger.pillstack.ui.pills.BasePill
    public void updateColors() {
        int themedColor = getThemedColor(Theme.key_windowBackgroundWhiteBlackText, 0.75f);
        this.layout.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(14.0f), Theme.isCurrentThemeDark() ? getThemedColor(Theme.key_windowBackgroundWhite) : Theme.multAlpha(themedColor, 0.09f), Theme.multAlpha(themedColor, 0.1f)));
        this.textView.setTextColor(themedColor);
        this.iconView.setColorFilter(new PorterDuffColorFilter(themedColor, PorterDuff.Mode.MULTIPLY));
        updateLoadingColors();
    }
}
