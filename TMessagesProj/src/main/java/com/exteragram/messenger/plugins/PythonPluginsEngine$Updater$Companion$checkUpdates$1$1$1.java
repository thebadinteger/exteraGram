package com.exteragram.messenger.plugins;

import android.app.Activity;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.updater.UpdateAppAlertDialog;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.Unit;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ScaleStateListAnimator;

@Metadata(d1 = {"\u0000'\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003*\u0001\u0000\b\n\u0018\u00002\u00020\u0001J\b\u0010\u0004\u001a\u00020\u0005H\u0014J\b\u0010\u0006\u001a\u00020\u0005H\u0014J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0014J\u0010\u0010\u000b\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0014J\b\u0010\f\u001a\u00020\bH\u0014R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\r"}, d2 = {"com/exteragram/messenger/plugins/PythonPluginsEngine$Updater$Companion$checkUpdates$1$1$1", "Lcom/exteragram/messenger/updater/UpdateAppAlertDialog;", "enableAutoUpdate", _UrlKt.FRAGMENT_ENCODE_SET, "getDoneButtonText", _UrlKt.FRAGMENT_ENCODE_SET, "getTitleText", "addContentBeforeDoneButton", _UrlKt.FRAGMENT_ENCODE_SET, "container", "Landroid/widget/FrameLayout;", "addContentAfterDoneButton", "onDone", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class PythonPluginsEngine$Updater$Companion$checkUpdates$1$1$1 extends UpdateAppAlertDialog {
    final /* synthetic */ PythonPluginsEngine.Updater.Companion.PythonSdkUpdateInfo $update;
    private boolean enableAutoUpdate;

    @Override // org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
    public /* bridge */ /* synthetic */ void setLastVisible(boolean z) {
        super.setLastVisible(z);
    }

    public PythonPluginsEngine$Updater$Companion$checkUpdates$1$1$1(PythonPluginsEngine.Updater.Companion.PythonSdkUpdateInfo pythonSdkUpdateInfo, Activity activity, int i) {
        super(activity, pythonSdkUpdateInfo, i);
        this.$update = pythonSdkUpdateInfo;
    }

    @Override 
    public String getDoneButtonText() {
        String string = LocaleController.getString(R.string.AppUpdateNow);
        "getString(...)";
        return string;
    }

    @Override 
    public String getTitleText() {
        return super.getTitleText() + " (Plugins PySDK)";
    }

    @Override 
    public void addContentBeforeDoneButton(FrameLayout container) {
        "container";
        final CheckBox2 checkBox2 = new CheckBox2(getContext(), 21, this.resourcesProvider);
        checkBox2.setColor(Theme.key_radioBackgroundChecked, Theme.key_checkboxDisabled, Theme.key_checkboxCheck);
        checkBox2.setDrawUnchecked(true);
        checkBox2.setChecked(false, false);
        checkBox2.setDrawBackgroundAsArc(10);
        TextView textView = new TextView(getContext());
        textView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText));
        textView.setTextSize(1, 14.0f);
        textView.setText(LocaleController.getString(R.string.EnableAutoUpdate));
        FrameLayout frameLayout = new FrameLayout(getContext());
        frameLayout.addView(checkBox2, LayoutHelper.createFrame(21, 21.0f, 17, 0.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(0);
        linearLayout.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(6.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(6.0f));
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(24, 24, 16, 0, 0, 6, 0));
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 16));
        linearLayout.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PythonPluginsEngine$Updater$Companion$checkUpdates$1$1$1.m1322$r8$lambda$pmnr1IP9LzBuJ_MY1EiMd0yh4(checkBox2, this, view);
            }
        });
        ScaleStateListAnimator.apply(linearLayout, 0.05f, 1.2f);
        linearLayout.setBackground(Theme.createRadSelectorDrawable(getThemedColor(Theme.key_listSelector), 8, 8));
        this.linearLayout.addView(linearLayout, LayoutHelper.createLinear(-2, -2, 1, 0, 0, 0, 8));
    }

    public static void m1322$r8$lambda$pmnr1IP9LzBuJ_MY1EiMd0yh4(CheckBox2 checkBox2, PythonPluginsEngine$Updater$Companion$checkUpdates$1$1$1 pythonPluginsEngine$Updater$Companion$checkUpdates$1$1$1, View view) {
        checkBox2.setChecked(!checkBox2.isChecked(), true);
        pythonPluginsEngine$Updater$Companion$checkUpdates$1$1$1.enableAutoUpdate = checkBox2.isChecked();
    }

    @Override 
    public void addContentAfterDoneButton(FrameLayout container) {
        "container";
        addRemindLaterButton(container, new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                PythonPluginsEngine$Updater$Companion$checkUpdates$1$1$1.m1323$r8$lambda$8l3dUB6TjDuyrcg49CEtzm7A(this.f$0);
            }
        });
    }

    public static void m1323$r8$lambda$8l3dUB6TjDuyrcg49CEtzm7A(PythonPluginsEngine$Updater$Companion$checkUpdates$1$1$1 pythonPluginsEngine$Updater$Companion$checkUpdates$1$1$1) {
        SharedPreferences.Editor editor = ExteraConfig.getEditor();
        String string = "sdkUpdateScheduleTimestamp";
        long jCurrentTimeMillis = System.currentTimeMillis();
        ExteraConfig.setSdkUpdateScheduleTimestamp(jCurrentTimeMillis);
        Unit unit = Unit.INSTANCE;
        editor.putLong(string, jCurrentTimeMillis).apply();
        pythonPluginsEngine$Updater$Companion$checkUpdates$1$1$1.lambda$new$0();
    }

    @Override 
    public void onDone() {
        if (this.enableAutoUpdate) {
            SharedPreferences.Editor editor = ExteraConfig.getEditor();
            String string = "pluginsPySdkAutoUpdate";
            ExteraConfig.setPluginsPySdkAutoUpdate(true);
            Unit unit = Unit.INSTANCE;
            editor.putBoolean(string, true).apply();
            if (PythonPluginsEngine.Updater.INSTANCE.getNotifyWhenChangeStatus()) {
                PythonPluginsEngine.Updater.notifyRunnable.run();
            }
        }
        try {
            PythonPluginsEngine.Updater.INSTANCE.savePythonSdkArchive(this.$update.getMessage(), this.$update.document, true);
        } catch (IOException e) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to load python-plugins-sdk file (");
            sb.append(this.$update.getChannel());
            sb.append(", message id = ");
            TLRPC.Message message = this.$update.getMessage();
            sb.append(message != null ? Integer.valueOf(message.id) : null);
            sb.append(')');
            FileLog.e(sb.toString(), e);
        }
        lambda$new$0();
    }
}
