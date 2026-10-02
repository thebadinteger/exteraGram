package com.exteragram.messenger.plugins.ui.components;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\u0018\u0000 \u00112\u00020\u0001:\u0001\u0011B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\u0004\b\u0007\u0010\bJ(\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u000eH\u0002¨\u0006\u0012"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/UnknownSourcesBottomSheet;", "Lorg/telegram/ui/ActionBar/BottomSheet;", "fragment", "Lorg/telegram/ui/ActionBar/BaseFragment;", "onConfirm", "Lkotlin/Function0;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Lorg/telegram/ui/ActionBar/BaseFragment;Lkotlin/jvm/functions/Function0;)V", "makeHint", "Landroid/view/View;", "context", "Landroid/content/Context;", "iconResId", _UrlKt.FRAGMENT_ENCODE_SET, "title", "info", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class UnknownSourcesBottomSheet extends BottomSheet {
    private static final float ICON_SIZE = 24.0f;
    private static final float ITEM_HORIZONTAL_PADDING = 27.0f;
    private static final float ITEM_TEXT_PADDING = 68.0f;
    private static final int READ_TIME = 30;

    @Override // org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
    public /* bridge */ /* synthetic */ void setLastVisible(boolean z) {
        super.setLastVisible(z);
    }

    public UnknownSourcesBottomSheet(BaseFragment baseFragment, final Function0<Unit> function0) {
        super(baseFragment.getParentActivity(), false, baseFragment.getResourceProvider());
        Context parentActivity = baseFragment.getParentActivity();
        fixNavigationBar();
        FrameLayout frameLayout = new FrameLayout(parentActivity);
        LinearLayout linearLayout = new LinearLayout(parentActivity);
        linearLayout.setOrientation(1);
        frameLayout.addView(linearLayout);
        RLottieImageView rLottieImageView = new RLottieImageView(parentActivity);
        rLottieImageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chats_actionIcon), PorterDuff.Mode.SRC_IN));
        rLottieImageView.setAnimation(R.raw.ic_ban, 50, 50);
        rLottieImageView.playAnimation();
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        rLottieImageView.setPadding(AndroidUtilities.dp(3.0f), 0, 0, AndroidUtilities.dp(3.0f));
        rLottieImageView.setBackground(Theme.createCircleDrawable(AndroidUtilities.dp(80.0f), getThemedColor(Theme.key_windowBackgroundWhiteValueText)));
        linearLayout.addView(rLottieImageView, LayoutHelper.createLinear(80, 80, 17, 0.0f, 14.0f, 0.0f, 0.0f));
        TextView textView = new TextView(parentActivity);
        textView.setGravity(1);
        textView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setText(LocaleController.getString(R.string.PluginsUnknownSources));
        linearLayout.addView(textView, LayoutHelper.createFrame(-1, -2.0f, 0, 40.0f, 20.0f, 40.0f, 0.0f));
        TextView textView2 = new TextView(parentActivity);
        textView2.setGravity(1);
        textView2.setTypeface(AndroidUtilities.regular());
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
        textView2.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.PluginsUnknownSourcesInfo)));
        linearLayout.addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 0, 21.0f, 8.0f, 21.0f, 0.0f));
        linearLayout.addView(makeHint(parentActivity, R.drawable.msg_permissions, R.string.PluginsUnknownSourcesAccount, R.string.PluginsUnknownSourcesAccountInfo), LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 20.0f, 0.0f, 0.0f));
        linearLayout.addView(makeHint(parentActivity, R.drawable.msg_folders, R.string.PluginsUnknownSourcesFiles, R.string.PluginsUnknownSourcesFilesInfo), LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 16.0f, 0.0f, 0.0f));
        linearLayout.addView(makeHint(parentActivity, R.drawable.msg_language, R.string.PluginsUnknownSourcesCode, R.string.PluginsUnknownSourcesCodeInfo), LayoutHelper.createFrame(-1, -2.0f, 0, 0.0f, 16.0f, 0.0f, 0.0f));
        int themedColor = getThemedColor(Theme.key_text_RedBold);
        TextView textView3 = new TextView(parentActivity);
        textView3.setGravity(1);
        textView3.setTypeface(AndroidUtilities.bold());
        textView3.setTextSize(1, 14.0f);
        textView3.setTextColor(themedColor);
        textView3.setPadding(AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(21.0f), AndroidUtilities.dp(10.0f));
        textView3.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(12.0f), Theme.multAlpha(themedColor, Theme.isCurrentThemeDark() ? 0.2f : 0.15f)));
        textView3.setText(LocaleController.getString(R.string.PluginsUnknownSourcesWarning));
        linearLayout.addView(textView3, LayoutHelper.createFrame(-1, -2.0f, 0, 16.0f, 20.0f, 16.0f, 0.0f));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(parentActivity, true, this.resourcesProvider);
        buttonWithCounterView.setRound();
        buttonWithCounterView.setText(LocaleController.getString(R.string.Cancel), false);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.plugins.ui.components.UnknownSourcesBottomSheet$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                UnknownSourcesBottomSheet.this.dismiss();
            }
        });
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 15.0f, 16.0f, 8.0f));
        final ButtonWithCounterView buttonWithCounterView2 = new ButtonWithCounterView(parentActivity, false, this.resourcesProvider);
        buttonWithCounterView2.setRound();
        buttonWithCounterView2.setNeutral();
        buttonWithCounterView2.setText(LocaleController.getString(R.string.Allow), false);
        linearLayout.addView(buttonWithCounterView2, LayoutHelper.createFrame(-1, 48.0f, 0, 16.0f, 0.0f, 16.0f, 0.0f));
        buttonWithCounterView2.setTimer(30, null);
        buttonWithCounterView2.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.plugins.ui.components.UnknownSourcesBottomSheet$$ExternalSyntheticLambda1
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                UnknownSourcesBottomSheet.$r8$lambda$Qx_wXefC8pnxHnFFUAF9QhBqceI(buttonWithCounterView2, UnknownSourcesBottomSheet.this, function0, view);
            }
        });
        ScrollView scrollView = new ScrollView(parentActivity);
        scrollView.addView(frameLayout);
        setCustomView(scrollView);
    }

    public static void $r8$lambda$Qx_wXefC8pnxHnFFUAF9QhBqceI(ButtonWithCounterView buttonWithCounterView, UnknownSourcesBottomSheet unknownSourcesBottomSheet, Function0 function0, View view) {
        if (buttonWithCounterView.isTimerActive()) {
            AndroidUtilities.shakeViewSpring(buttonWithCounterView, 3.0f);
            BotWebViewVibrationEffect.APP_ERROR.vibrate();
        } else {
            unknownSourcesBottomSheet.dismiss();
            function0.invoke();
        }
    }

    private final View makeHint(Context context, int iconResId, int title, int info) {
        FrameLayout frameLayout = new FrameLayout(context);
        ImageView imageView = new ImageView(context);
        imageView.setImageResource(iconResId);
        int i = Theme.key_windowBackgroundWhiteBlackText;
        imageView.setColorFilter(getThemedColor(i));
        frameLayout.addView(imageView, LayoutHelper.createFrameRelatively(ICON_SIZE, ICON_SIZE, 8388659, ITEM_HORIZONTAL_PADDING, 6.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        TextView textView = new TextView(context);
        textView.setGravity(LayoutHelper.getAbsoluteGravityStart());
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextSize(1, 14.0f);
        textView.setTextColor(getThemedColor(i));
        textView.setText(LocaleController.getString(title));
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2));
        TextView textView2 = new TextView(context);
        textView2.setGravity(LayoutHelper.getAbsoluteGravityStart());
        textView2.setTypeface(AndroidUtilities.regular());
        textView2.setTextSize(1, 14.0f);
        textView2.setTextColor(getThemedColor(Theme.key_dialogTextGray3));
        textView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
        textView2.setText(LocaleController.getString(info));
        linearLayout.addView(textView2, LayoutHelper.createLinear(-1, -2, 0.0f, 2.0f, 0.0f, 0.0f));
        frameLayout.addView(linearLayout, LayoutHelper.createFrameRelatively(-1.0f, -2.0f, 8388659, ITEM_TEXT_PADDING, 0.0f, ITEM_HORIZONTAL_PADDING, 0.0f));
        return frameLayout;
    }
}
