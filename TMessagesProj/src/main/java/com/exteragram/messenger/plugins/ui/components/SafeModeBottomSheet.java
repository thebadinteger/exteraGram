package com.exteragram.messenger.plugins.ui.components;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.plugins.PluginsController;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.SourceDebugExtension;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.StickerImageView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

@Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005¨\u0006\u0006"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/SafeModeBottomSheet;", "Lorg/telegram/ui/ActionBar/BottomSheet;", "fragment", "Lorg/telegram/ui/ActionBar/BaseFragment;", "<init>", "(Lorg/telegram/ui/ActionBar/BaseFragment;)V", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nSafeModeBottomSheet.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafeModeBottomSheet.kt\ncom/exteragram/messenger/plugins/ui/components/SafeModeBottomSheet\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,115:1\n1#2:116\n*E\n"})
public final class SafeModeBottomSheet extends BottomSheet {
    @Override // org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
    public /* bridge */ /* synthetic */ void setLastVisible(boolean z) {
        super.setLastVisible(z);
    }

    public SafeModeBottomSheet(BaseFragment baseFragment) {
        super(baseFragment.getParentActivity(), false, baseFragment.getResourceProvider());
        Activity parentActivity = baseFragment.getParentActivity();
        fixNavigationBar();
        FrameLayout frameLayout = new FrameLayout(parentActivity);
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        frameLayout.addView(linearLayout);
        StickerImageView stickerImageView = new StickerImageView(parentActivity, this.currentAccount);
        stickerImageView.setStickerPackName("exteraGramPlaceholders");
        stickerImageView.setStickerNum(10);
        stickerImageView.getImageReceiver().setAutoRepeat(1);
        linearLayout.addView(stickerImageView, LayoutHelper.createLinear(144, 144, 1, 0.0f, 16.0f, 0.0f, 0.0f));
        TextView textView = new TextView(parentActivity);
        textView.setGravity(1);
        textView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setText(LocaleController.getString(R.string.PluginsSafeMode));
        linearLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 0, 40.0f, 20.0f, 40.0f, 0.0f));
        TextView textView2 = new TextView(parentActivity);
        textView2.setGravity(1);
        textView2.setTypeface(AndroidUtilities.regular());
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
        textView2.setText(LocaleController.getString(R.string.PluginsSafeModeInfo));
        linearLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 8.0f, 21.0f, 0.0f));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(parentActivity, true, this.resourcesProvider);
        buttonWithCounterView.setRound();
        buttonWithCounterView.setText(LocaleController.getString(R.string.Disable), false);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                SafeModeBottomSheet.m1362$r8$lambda$akwXYwH2g7Yc4GuPo7nIqbaSJU(SafeModeBottomSheet.this, view);
            }
        });
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 28.0f, 16.0f, 16.0f));
        setCustomView(frameLayout);
    }

    public static void m1362$r8$lambda$akwXYwH2g7Yc4GuPo7nIqbaSJU(SafeModeBottomSheet safeModeBottomSheet, View view) {
        safeModeBottomSheet.dismiss();
        SharedPreferences.Editor editor = ExteraConfig.getEditor();
        ExteraConfig.setPluginsSafeMode(false);
        Unit unit = Unit.INSTANCE;
        editor.putBoolean("pluginsSafeMode", false).apply();
        PluginsController.INSTANCE.getInstance().restart(false);
    }
}
