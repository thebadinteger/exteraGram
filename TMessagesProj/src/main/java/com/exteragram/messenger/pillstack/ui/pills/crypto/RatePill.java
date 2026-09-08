package com.exteragram.messenger.pillstack.ui.pills.crypto;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.exteragram.messenger.pillstack.core.PillStackConfig;
import com.exteragram.messenger.pillstack.ui.PillStackPreferencesActivity;
import com.exteragram.messenger.pillstack.ui.pills.BasePill;
import com.exteragram.messenger.pillstack.ui.pills.crypto.utils.ColoredBackground;
import com.exteragram.messenger.pillstack.ui.pills.crypto.utils.ExchangeRates;
import com.exteragram.messenger.pillstack.ui.pills.crypto.utils.PillStackCurrencies;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LoadingDrawable;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.LaunchActivity;

@SuppressLint({"ViewConstructor"})
public abstract class RatePill extends BasePill implements NotificationCenter.NotificationCenterDelegate {
    private final ColoredBackground background;
    private final String baseCurrency;
    private final RateCache cache;
    private final int iconResId;
    private final ImageView iconView;
    private final LinearLayout layout;
    private boolean requestInFlight;
    private final int scale;
    private final AnimatedTextView textView;

    public static final class RateCache {
        private final AtomicReference<String> cachedPrice = new AtomicReference<>();
        private final AtomicReference<String> cachedCurrency = new AtomicReference<>();
    }

    @Override 
    public long getRefreshInterval() {
        return 300000L;
    }

    public abstract String getTargetSelection();

    public abstract void setTargetSelection(String str);

    public RatePill(Context context, Theme.ResourcesProvider resourcesProvider, RateCache rateCache, String str, int i, int i2, ColoredBackground coloredBackground) {
        super(context, resourcesProvider);
        this.cache = rateCache;
        this.baseCurrency = str;
        this.scale = i;
        this.iconResId = i2;
        this.background = coloredBackground;
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
        animatedTextView.setIncludeFontPadding(false);
        animatedTextView.setTypeface(AndroidUtilities.bold());
        animatedTextView.adaptWidth = true;
        linearLayout.addView(animatedTextView, LayoutHelper.createLinear(-2, -2, 16));
        setLoadingTargetView(linearLayout);
        updateColors();
        ScaleStateListAnimator.apply(linearLayout);
        String str2 = (String) rateCache.cachedPrice.get();
        if (str2 != null) {
            setData(str2, false);
        }
    }

    @Override 
    public void onPillClicked() {
        if (this.iconView.getVisibility() == 0 && this.textView.getText() != null && TextUtils.equals(this.textView.getText(), LocaleController.getString(R.string.Retry))) {
            onUpdateData(true);
        } else {
            onPillLongClicked();
        }
    }

    @Override 
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (PillStackConfig.checkAndClearPendingUpdate(getPillId()) || this.cache.cachedPrice.get() == null || isRefreshDue()) {
            onUpdateData(true);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pillStackSettingsChanged);
    }

    @Override 
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pillStackSettingsChanged);
    }

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.pillStackSettingsChanged && PillStackConfig.shouldUpdatePill(objArr, getPillId()) && getTargetSelection().equals("AUTO")) {
            PillStackConfig.checkAndClearPendingUpdate(getPillId());
            onUpdateData(true);
        }
    }

    @Override 
    public boolean onPillLongClicked() {
        final BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return false;
        }
        final ItemOptions itemOptionsMakeOptions = ItemOptions.makeOptions(safeLastFragment, (View) this, true);
        final ItemOptions itemOptionsAddGap = itemOptionsMakeOptions.makeSwipeback(true).add(R.drawable.ic_ab_back, LocaleController.getString(R.string.Back), new RatePill$$ExternalSyntheticLambda1(itemOptionsMakeOptions)).addGap();
        final String targetSelection = getTargetSelection();
        for (final String str : getTargetCurrencies()) {
            itemOptionsAddGap.addChecked(str.equalsIgnoreCase(targetSelection), PillStackCurrencies.getTargetCurrencyLabel(str), new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onPillLongClicked$0(itemOptionsMakeOptions, str, targetSelection);
                }
            });
        }
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(itemOptionsMakeOptions.getContext(), false, false, this.resourcesProvider);
        actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(R.string.CryptoPillTargetCurrency), R.drawable.msg_language);
        actionBarMenuSubItem.setSubtext(PillStackCurrencies.getTargetCurrencySubtext(getTargetSelection()));
        actionBarMenuSubItem.setItemHeight(56);
        actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                itemOptionsMakeOptions.openSwipeback(itemOptionsAddGap);
            }
        });
        itemOptionsMakeOptions.add(actionBarMenuSubItem).addGap().add(R.drawable.msg_retry, LocaleController.getString(R.string.Refresh), new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onPillLongClicked$2();
            }
        }).add(R.drawable.msg_settings, LocaleController.getString(R.string.Settings), new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                safeLastFragment.presentFragment(new PillStackPreferencesActivity());
            }
        }).setSwipebackGravity(!LocaleController.isRTL, false).forceBelowScrim(true).setDrawScrim(false).setGravity(LocaleController.isRTL ? 3 : 5).setDimAlpha(0).show();
        return true;
    }

    public void lambda$onUpdateData$4(String str, ExchangeRates.State state) {
        this.requestInFlight = false;
        if (state == null) {
            String str2 = (String) this.cache.cachedPrice.get();
            if (str2 != null) {
                setData(str2, true);
                return;
            } else {
                setErrorState(true);
                return;
            }
        }
        BigDecimal rate = state.getRate(this.baseCurrency, str);
        if (rate == null) {
            String str3 = (String) this.cache.cachedPrice.get();
            if (str3 != null) {
                setData(str3, true);
                return;
            } else {
                setErrorState(true);
                return;
            }
        }
        String price = formatPrice(rate, str);
        this.cache.cachedPrice.set(price);
        this.cache.cachedCurrency.set(str);
        setData(price, true);
        markDataUpdated();
    }

    public String formatPrice(BigDecimal bigDecimal, String str) {
        String fiatPrice = PillStackCurrencies.formatFiatPrice(bigDecimal, str);
        if (fiatPrice != null) {
            return fiatPrice;
        }
        return bigDecimal.setScale(this.scale, RoundingMode.HALF_UP).toPlainString() + " " + str;
    }

    public String[] getTargetCurrencies() {
        return PillStackCurrencies.TARGET_CURRENCIES;
    }

    private void setErrorState(boolean z) {
        stopLoading();
        if (z) {
            animateSizeChange();
        }
        this.iconView.setImageResource(R.drawable.msg_retry);
        this.iconView.setVisibility(0);
        this.textView.setText(LocaleController.getString(R.string.Retry), z);
        this.textView.setVisibility(0);
    }

    private void setData(String str, boolean z) {
        stopLoading();
        if (z) {
            animateSizeChange();
        }
        this.iconView.setImageResource(this.iconResId);
        this.iconView.setVisibility(0);
        this.textView.setText(str, z);
        this.textView.setVisibility(0);
    }

    @Override // android.view.View
    public void setPressed(boolean z) {
        if (this.loading) {
            z = false;
        }
        super.setPressed(z);
        this.layout.setPressed(z);
    }

    @Override 
    public void updateColors() {
        this.layout.setBackground(this.background);
        this.textView.setTextColor(-1);
        this.iconView.setColorFilter(-1);
        updateLoadingColors();
    }

    @Override 
    public void updateLoadingColors() {
        LoadingDrawable loadingDrawable = this.loadingDrawable;
        if (loadingDrawable != null) {
            loadingDrawable.setColors(Theme.multAlpha(-1, 0.1f), Theme.multAlpha(-1, 0.3f));
        }
    }
}
