package com.exteragram.messenger.pillstack.ui.pills.system;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.exteragram.messenger.pillstack.core.PillType;
import com.exteragram.messenger.pillstack.ui.PillStackPreferencesActivity;
import com.exteragram.messenger.pillstack.ui.pills.BasePill;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProxyListActivity;

@SuppressLint({"ViewConstructor"})
public class ProxyPill extends BasePill implements NotificationCenter.NotificationCenterDelegate {
    private final ImageView iconView;
    private int lastAccount;
    private final LinearLayout layout;
    private final AnimatedTextView textView;

    @Override 
    public long getRefreshInterval() {
        return 0L;
    }

    public ProxyPill(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context, resourcesProvider);
        LinearLayout linearLayout = new LinearLayout(context);
        this.layout = linearLayout;
        linearLayout.setOrientation(0);
        linearLayout.setGravity(17);
        linearLayout.setMinimumWidth(AndroidUtilities.dp(48.0f));
        linearLayout.setPadding(AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(10.0f), 0);
        addView(linearLayout, LayoutHelper.createFrame(-2, 28, (LocaleController.isRTL ? 3 : 5) | 16));
        ImageView imageView = new ImageView(context);
        this.iconView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        linearLayout.addView(imageView, LayoutHelper.createLinear(16, 16, 16, 0, 0, 2, 0));
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
        onUpdateData(false);
    }

    @Override 
    public int getPillId() {
        return PillType.PROXY.getId();
    }

    @Override 
    public void onUpdateData(boolean z) {
        String string;
        SharedConfig.ProxyInfo proxyInfo;
        boolean zIsProxyEnabled = SharedConfig.isProxyEnabled();
        int connectionState = ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState();
        boolean z2 = connectionState == 3 || connectionState == 5;
        String string2 = this.textView.getText() != null ? this.textView.getText().toString() : _UrlKt.FRAGMENT_ENCODE_SET;
        if (!zIsProxyEnabled || (proxyInfo = SharedConfig.currentProxy) == null) {
            this.iconView.setImageResource(R.drawable.drawer_proxy_off);
            string = LocaleController.getString(R.string.Proxy);
            stopLoading();
        } else if (z2) {
            long jClamp = Utilities.clamp(proxyInfo.ping, 9999L, 0L);
            this.iconView.setImageResource(R.drawable.drawer_proxy_on);
            if (jClamp > 0) {
                string = LocaleController.formatString(R.string.NavigationDrawerProxyPingShort, Long.valueOf(jClamp));
            } else {
                string = LocaleController.getString(R.string.MenuProxyConnected);
            }
            stopLoading();
        } else {
            this.iconView.setImageResource(R.drawable.drawer_proxy_off);
            string = LocaleController.getString(R.string.MenuProxyConnecting);
            startLoading();
        }
        if (z || !TextUtils.equals(string2, string)) {
            if (z) {
                animateSizeChange();
            }
            this.textView.setText(string, z);
        }
        updateColors();
    }

    @Override 
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        onUpdateData(true);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxyPingUpdated);
        int i = UserConfig.selectedAccount;
        this.lastAccount = i;
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.didUpdateConnectionState);
    }

    @Override 
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxyPingUpdated);
        NotificationCenter.getInstance(this.lastAccount).removeObserver(this, NotificationCenter.didUpdateConnectionState);
    }

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.proxySettingsChanged || i == NotificationCenter.proxyPingUpdated || i == NotificationCenter.didUpdateConnectionState) {
            onUpdateData(true);
        }
    }

    @Override 
    public void onPillClicked() {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            safeLastFragment.presentFragment(new ProxyListActivity());
        }
    }

    @Override 
    public boolean onPillLongClicked() {
        final BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return false;
        }
        ItemOptions.makeOptions(safeLastFragment, this).add(R.drawable.msg_settings, LocaleController.getString(R.string.Settings), new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                safeLastFragment.presentFragment(new PillStackPreferencesActivity());
            }
        }).setDrawScrim(false).setDimAlpha(0).show();
        return true;
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

    @Override 
    public void updateColors() {
        int themedColor;
        boolean zIsProxyEnabled = SharedConfig.isProxyEnabled();
        int connectionState = ConnectionsManager.getInstance(UserConfig.selectedAccount).getConnectionState();
        boolean z = connectionState == 3 || connectionState == 5;
        if (zIsProxyEnabled && SharedConfig.currentProxy != null && z) {
            themedColor = getThemedColor(Theme.key_windowBackgroundWhiteGreenText);
        } else {
            themedColor = getThemedColor(Theme.key_windowBackgroundWhiteBlackText, 0.75f);
        }
        this.layout.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(14.0f), Theme.isCurrentThemeDark() ? getThemedColor(Theme.key_windowBackgroundWhite) : Theme.multAlpha(themedColor, 0.09f), Theme.multAlpha(themedColor, 0.1f)));
        this.textView.setTextColor(themedColor);
        this.iconView.setColorFilter(new PorterDuffColorFilter(themedColor, PorterDuff.Mode.MULTIPLY));
        updateLoadingColors();
    }
}
