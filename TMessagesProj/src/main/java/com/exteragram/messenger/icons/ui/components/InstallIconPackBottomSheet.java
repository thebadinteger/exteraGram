package com.exteragram.messenger.icons.ui.components;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StrikethroughSpan;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.components.VerticalImageSpan;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.icons.IconPack;
import com.exteragram.messenger.utils.text.LocaleUtils;
import java.io.File;
import kotlin.Metadata;
import kotlin.io.FilesKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.EffectsTextView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001\fB\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u000b2\u0006\u0010\u0002\u001a\u00020\u0003H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"Lcom/exteragram/messenger/icons/ui/components/InstallIconPackBottomSheet;", "Lorg/telegram/ui/ActionBar/BottomSheet;", "context", "Landroid/content/Context;", "iconPack", "Lcom/exteragram/messenger/icons/IconPack;", "installDelegate", "Lcom/exteragram/messenger/icons/ui/components/InstallIconPackBottomSheet$InstallDelegate;", "<init>", "(Landroid/content/Context;Lcom/exteragram/messenger/icons/IconPack;Lcom/exteragram/messenger/icons/ui/components/InstallIconPackBottomSheet$InstallDelegate;)V", "createView", "Landroid/view/View;", "InstallDelegate", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class InstallIconPackBottomSheet extends BottomSheet {
    private final IconPack iconPack;
    private final InstallDelegate installDelegate;

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\bæ\u0080\u0001\u0018\u00002\u00020\u0001J\u0018\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0005H&¨\u0006\u0007À\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/icons/ui/components/InstallIconPackBottomSheet$InstallDelegate;", _UrlKt.FRAGMENT_ENCODE_SET, "onInstall", _UrlKt.FRAGMENT_ENCODE_SET, "enable", _UrlKt.FRAGMENT_ENCODE_SET, "update", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public interface InstallDelegate {
        void onInstall(boolean enable, boolean update);
    }

    public InstallIconPackBottomSheet(Context context, IconPack iconPack, InstallDelegate installDelegate) {
        super(context, false);
        this.iconPack = iconPack;
        this.installDelegate = installDelegate;
        setCustomView(createView(context));
        setOnDismissListener(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        Utilities.globalQueue.postRunnable(new Runnable() { 
                            @Override // java.lang.Runnable
                            public final void run() {
                                InstallIconPackBottomSheet.m1209$r8$lambda$lAHL0JphJT6_nJA9poZvzL8HV0(installIconPackBottomSheet);
                            }
                        });
                    }
                }, 1000L);
            }
        });
    }

    public static void m1209$r8$lambda$lAHL0JphJT6_nJA9poZvzL8HV0(InstallIconPackBottomSheet installIconPackBottomSheet) {
        File location = installIconPackBottomSheet.iconPack.getLocation();
        if (location != null) {
            FilesKt.deleteRecursively(location);
        }
    }

    private final View createView(Context context) {
        final CheckBox2 checkBox2;
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        linearLayout.setPadding(0, AndroidUtilities.dp(16.0f), 0, 0);
        IconPackPreviewView iconPackPreviewView = new IconPackPreviewView(context);
        iconPackPreviewView.setCircularMode(true);
        iconPackPreviewView.setRefreshTime(3000);
        iconPackPreviewView.setIconPack(this.iconPack);
        linearLayout.addView(iconPackPreviewView, LayoutHelper.createLinear(-1, -2, 1, 0, 0, 0, 16));
        TextView textView = new TextView(context);
        textView.setTextSize(1, 20.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setGravity(17);
        textView.setTextColor(Theme.getColor(Theme.key_dialogTextBlack));
        textView.setText(this.iconPack.getName());
        linearLayout.addView(textView, LayoutHelper.createLinear(-1, -2, 24.0f, 0.0f, 24.0f, 0.0f));
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        IconPack iconPackFindPackById = IconManager.INSTANCE.findPackById(this.iconPack.getId());
        final boolean z = iconPackFindPackById != null;
        EffectsTextView effectsTextView = new EffectsTextView(context, this.resourcesProvider);
        effectsTextView.setGravity(1);
        effectsTextView.setTypeface(AndroidUtilities.regular());
        effectsTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        effectsTextView.setLinkTextColor(getThemedColor(Theme.key_dialogTextLink));
        effectsTextView.setTextSize(1, 14.0f);
        int i = Theme.key_windowBackgroundWhiteGrayText;
        effectsTextView.setTextColor(getThemedColor(i));
        SpannableStringBuilder spannableStringBuilderAppend = new SpannableStringBuilder(LocaleController.getString(R.string.PluginVersion)).append((CharSequence) " ");
        int length = spannableStringBuilderAppend.length();
        if (iconPackFindPackById != null) {
            String version = iconPackFindPackById.getVersion();
            spannableStringBuilderAppend.append((CharSequence) version).append((CharSequence) " -> ").append((CharSequence) this.iconPack.getVersion());
            spannableStringBuilderAppend = VerticalImageSpan.createSpan(context, R.drawable.msg_mini_arrow_mediathin, spannableStringBuilderAppend.toString(), "->", i, this.resourcesProvider);
            spannableStringBuilderAppend.setSpan(new StrikethroughSpan(), length, version.length() + length, 33);
        } else if (!TextUtils.isEmpty(this.iconPack.getVersion())) {
            spannableStringBuilderAppend.append((CharSequence) this.iconPack.getVersion());
        }
        if (!TextUtils.isEmpty(this.iconPack.getAuthor())) {
            spannableStringBuilderAppend.append((CharSequence) " • ").append(LocaleUtils.formatWithUsernames(this.iconPack.getAuthor(), safeLastFragment, new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$new$0();
                }
            }));
        }
        effectsTextView.setText(spannableStringBuilderAppend);
        linearLayout.addView(effectsTextView, LayoutHelper.createLinear(-1, -2, 24.0f, 4.0f, 24.0f, 24.0f));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, true, this.resourcesProvider);
        buttonWithCounterView.setRound();
        buttonWithCounterView.setCount(LocaleController.formatPluralString("IconCount", this.iconPack.getIcons().size(), new Object[0]), false);
        if (z) {
            buttonWithCounterView.setText(LocaleController.getString(R.string.UpdatePack), false);
        } else {
            buttonWithCounterView.setText(LocaleController.getString(R.string.InstallPack), false);
        }
        linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(-1, 48, 0, 16, 0, 16, 16));
        if (ExteraConfig.getIconPacksLayout().contains(this.iconPack.getId())) {
            checkBox2 = null;
        } else {
            checkBox2 = new CheckBox2(context, 21, this.resourcesProvider);
            checkBox2.setColor(Theme.key_radioBackgroundChecked, Theme.key_checkboxDisabled, Theme.key_checkboxCheck);
            checkBox2.setDrawUnchecked(true);
            checkBox2.setChecked(true, false);
            checkBox2.setDrawBackgroundAsArc(10);
            TextView textView2 = new TextView(context);
            textView2.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
            textView2.setTextSize(1, 14.0f);
            textView2.setText(LocaleController.getString(R.string.EnableAfterInstallation));
            FrameLayout frameLayout = new FrameLayout(context);
            frameLayout.addView(checkBox2, LayoutHelper.createFrame(21, 21.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
            LinearLayout linearLayout2 = new LinearLayout(context);
            linearLayout2.setOrientation(0);
            linearLayout2.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(6.0f));
            linearLayout2.addView(frameLayout, LayoutHelper.createLinear(24, 24, 16, 0, 0, 6, 0));
            linearLayout2.addView(textView2, LayoutHelper.createLinear(-2, -2, 16));
            linearLayout2.setOnClickListener(new View.OnClickListener() { 
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    CheckBox2 checkBox3 = checkBox2;
                    checkBox3.setChecked(!checkBox3.isChecked(), true);
                }
            });
            ScaleStateListAnimator.apply(linearLayout2, 0.05f, 1.2f);
            linearLayout2.setBackground(Theme.createRadSelectorDrawable(getThemedColor(Theme.key_listSelector), 8, 8));
            linearLayout.addView(linearLayout2, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 8));
        }
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                InstallIconPackBottomSheet.m1208$r8$lambda$BJEv0hgA7mAY3J925oH7h0dwyM(this.f$0, checkBox2, z, view);
            }
        });
        return linearLayout;
    }

    public static void m1208$r8$lambda$BJEv0hgA7mAY3J925oH7h0dwyM(final InstallIconPackBottomSheet installIconPackBottomSheet, final CheckBox2 checkBox2, final boolean z, View view) {
        installIconPackBottomSheet.lambda$new$0();
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                InstallIconPackBottomSheet.createView$lambda$1$0(this.f$0, checkBox2, z);
            }
        }, 200L);
    }

    public static final void createView$lambda$1$0(InstallIconPackBottomSheet installIconPackBottomSheet, CheckBox2 checkBox2, boolean z) {
        installIconPackBottomSheet.installDelegate.onInstall(checkBox2 != null && checkBox2.isChecked(), z);
    }
}
