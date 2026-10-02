package com.exteragram.messenger.adblock.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.adblock.WebAdBlocker;
import com.exteragram.messenger.adblock.backend.AdBlockManager;
import com.exteragram.messenger.utils.ui.SwitchUiHelper;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Switch;

/* JADX INFO: loaded from: classes4.dex */
@SuppressLint({"ViewConstructor"})
public class AdBlockMenuItem extends ActionBarMenuSubItem {
    private static final int[] BLOCKED_COUNT_SAMPLES = {991, 992, 999};
    private final WebAdBlocker adBlocker;
    private final ItemOptions options;
    private CharSequence subtext;
    private final Switch switchView;
    private boolean toggled;
    private final Runnable updateRunnable;

    public static void addTo(ItemOptions itemOptions, WebAdBlocker webAdBlocker) {
        if (webAdBlocker == null || !AdBlockManager.isAvailable()) {
            return;
        }
        if (ExteraConfig.getEnableAdBlock() && !AdBlockManager.isActive()) {
            AdBlockManager.initialize();
        }
        AdBlockMenuItem adBlockMenuItem = new AdBlockMenuItem(itemOptions.getContext(), webAdBlocker, itemOptions);
        itemOptions.add(adBlockMenuItem);
        adBlockMenuItem.alignSwitch();
        adBlockMenuItem.fitWidth();
        itemOptions.addGap();
    }

    private AdBlockMenuItem(Context context, WebAdBlocker webAdBlocker, ItemOptions itemOptions) {
        super(context, false, false, (Theme.ResourcesProvider) null);
        this.updateRunnable = new Runnable() { // from class: com.exteragram.messenger.adblock.ui.AdBlockMenuItem.1
            @Override // java.lang.Runnable
            public void run() {
                if (AdBlockMenuItem.this.toggled) {
                    return;
                }
                AdBlockMenuItem.this.update();
                AndroidUtilities.runOnUIThread(this, 500L);
            }
        };
        this.adBlocker = webAdBlocker;
        this.options = itemOptions;
        setTextAndIcon(LocaleController.getString(R.string.BlockAds), R.drawable.msg_policy);
        setClipToPadding(false);
        Switch r4 = new Switch(context);
        this.switchView = r4;
        int i = Theme.key_switchTrack;
        int i2 = Theme.key_switchTrackChecked;
        int i3 = Theme.key_windowBackgroundWhite;
        r4.setColors(i, i2, i3, i3);
        r4.setImportantForAccessibility(2);
        r4.setChecked(ExteraConfig.getEnableAdBlock(), false);
        if (SwitchUiHelper.isMaterial3SwitchStyle()) {
            r4.setScaleX(0.85f);
            r4.setScaleY(0.85f);
        }
        addView(r4, LayoutHelper.createFrame(44, 28, (LocaleController.isRTL ? 3 : 5) | 16));
        setOnClickListener(view -> toggle());
        update();
    }

    private void alignSwitch() {
        float fDp;
        float fDp2;
        if (SwitchUiHelper.isMaterial3SwitchStyle()) {
            RectF rectF = new RectF();
            SwitchUiHelper.setTrackBounds(rectF, 0, 0);
            fDp = rectF.width() * 0.85f;
            fDp2 = rectF.height() * 0.85f;
        } else {
            fDp = AndroidUtilities.dp(37.0f);
            fDp2 = AndroidUtilities.dp(20.0f);
        }
        float fDp3 = (AndroidUtilities.dp(this.subtext != null ? 56.0f : 48.0f) - fDp2) / 2.0f;
        float paddingLeft = LocaleController.isRTL ? getPaddingLeft() : getPaddingRight();
        int iRound = Math.round((((fDp - AndroidUtilities.dp(44.0f)) / 2.0f) + fDp3) - paddingLeft);
        int iRound2 = Math.round(((fDp3 + fDp) + AndroidUtilities.dp(12.0f)) - paddingLeft);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.switchView.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.leftMargin = iRound;
            AnimatedEmojiSpan.TextViewEmojis textViewEmojis = this.textView;
            textViewEmojis.setPadding(iRound2, 0, textViewEmojis.getPaddingRight(), 0);
        } else {
            layoutParams.rightMargin = iRound;
            AnimatedEmojiSpan.TextViewEmojis textViewEmojis2 = this.textView;
            textViewEmojis2.setPadding(textViewEmojis2.getPaddingLeft(), 0, iRound2, 0);
        }
        TextView textView = this.subtextView;
        if (textView != null) {
            textView.setPadding(this.textView.getPaddingLeft(), 0, this.textView.getPaddingRight(), 0);
        }
    }

    private void fitWidth() {
        int minimumWidth = getMinimumWidth();
        if (minimumWidth <= 0) {
            return;
        }
        TextPaint textPaint = new TextPaint(1);
        textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        float fMeasureText = textPaint.measureText(LocaleController.getString(R.string.AdBlockFiltersLoading));
        for (int i : BLOCKED_COUNT_SAMPLES) {
            fMeasureText = Math.max(fMeasureText, textPaint.measureText(LocaleController.formatPluralString("BlockedRequests", i, new Object[0])));
        }
        int iMin = Math.min(getPaddingLeft() + getPaddingRight() + this.textView.getPaddingLeft() + this.textView.getPaddingRight() + ((int) Math.ceil(Math.max(this.textView.getPaint().measureText(this.textView.getText().toString()), fMeasureText))), AndroidUtilities.displaySize.x - AndroidUtilities.dp(32.0f));
        if (iMin > minimumWidth) {
            int iCeil = (int) Math.ceil(iMin / AndroidUtilities.density);
            this.options.setMinWidth(iCeil);
            for (int i2 = 0; i2 < this.options.getItemsCount(); i2++) {
                View itemAt = this.options.getItemAt(i2);
                if (itemAt instanceof ActionBarMenuSubItem) {
                    float f = iCeil;
                    itemAt.setMinimumWidth(AndroidUtilities.dp(f));
                    itemAt.getLayoutParams().width = AndroidUtilities.dp(f);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void update() {
        String string;
        if (!ExteraConfig.getEnableAdBlock()) {
            string = null;
        } else if (AdBlockManager.isActive()) {
            string = LocaleController.formatPluralString("BlockedRequests", this.adBlocker.getBlockedCount(), new Object[0]);
        } else {
            string = LocaleController.getString(R.string.AdBlockFiltersLoading);
        }
        if (this.subtextView == null || !TextUtils.equals(this.subtext, string)) {
            this.subtext = string;
            setItemHeight(string != null ? 56 : 48);
            setSubtext(this.subtext);
        }
    }

    private void toggle() {
        if (this.toggled) {
            return;
        }
        this.toggled = true;
        boolean enableAdBlock = ExteraConfig.getEnableAdBlock();
        this.switchView.setChecked(!enableAdBlock, true);
        if (!enableAdBlock) {
            final WebAdBlocker webAdBlocker = this.adBlocker;
            Objects.requireNonNull(webAdBlocker);
            AdBlockManager.setEnabled(true, new Runnable() { // from class: com.exteragram.messenger.adblock.ui.AdBlockMenuItem$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    webAdBlocker.reload();
                }
            });
        } else {
            AdBlockManager.setEnabled(false, null);
            this.adBlocker.reload();
        }
        ItemOptions itemOptions = this.options;
        Objects.requireNonNull(itemOptions);
        AndroidUtilities.runOnUIThread(itemOptions::dismiss, 200L);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        AndroidUtilities.runOnUIThread(this.updateRunnable, 500L);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AndroidUtilities.cancelRunOnUIThread(this.updateRunnable);
    }

    @Override // org.telegram.ui.ActionBar.ActionBarMenuSubItem, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        accessibilityNodeInfo.setClassName("android.widget.Switch");
        accessibilityNodeInfo.setCheckable(true);
        accessibilityNodeInfo.setChecked(this.switchView.isChecked());
    }
}
