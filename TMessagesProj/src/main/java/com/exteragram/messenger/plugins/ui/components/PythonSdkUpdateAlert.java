package com.exteragram.messenger.plugins.ui.components;

import android.app.Activity;
import android.content.Context;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.exteragram.messenger.components.CheckBoxRow;
import com.exteragram.messenger.plugins.PluginsConstants;
import com.exteragram.messenger.plugins.PythonPluginsEngine;
import com.exteragram.messenger.updater.UpdateAppAlertDialog;
import java.io.IOException;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0007\u0018\u0000  2\u00020\u0001:\u0001 B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\u000b\u001a\u00020\tH\u0002J\b\u0010\f\u001a\u00020\tH\u0014J\b\u0010\r\u001a\u00020\u000eH\u0014J\b\u0010\u000f\u001a\u00020\u000eH\u0014J\b\u0010\u0010\u001a\u00020\u0011H\u0014J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u0015H\u0014J\b\u0010\u0016\u001a\u00020\u0013H\u0014J<\u0010\u0017\u001a\u00020\u0013*\u00020\u00182\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u00112\u0006\u0010\u001c\u001a\u00020\u001a2\u0006\u0010\u001d\u001a\u00020\u001a2\u0006\u0010\u001e\u001a\u00020\u001a2\u0006\u0010\u001f\u001a\u00020\u001aH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006!"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/PythonSdkUpdateAlert;", "Lcom/exteragram/messenger/updater/UpdateAppAlertDialog;", "fragment", "Lorg/telegram/ui/ActionBar/BaseFragment;", PluginsConstants.UPDATE, "Lcom/exteragram/messenger/plugins/PythonPluginsEngine$Updater$Companion$PythonSdkUpdateInfo;", "<init>", "(Lorg/telegram/ui/ActionBar/BaseFragment;Lcom/exteragram/messenger/plugins/PythonPluginsEngine$Updater$Companion$PythonSdkUpdateInfo;)V", "enableAutoUpdate", _UrlKt.FRAGMENT_ENCODE_SET, "installStarted", "isActivityGone", "hasSecondaryButton", "getDoneButtonText", _UrlKt.FRAGMENT_ENCODE_SET, "getTitleText", "getExtraBottomContentHeight", _UrlKt.FRAGMENT_ENCODE_SET, "addContentAfterDoneButton", _UrlKt.FRAGMENT_ENCODE_SET, "container", "Landroid/widget/FrameLayout;", "onDone", "restyle", "Landroid/widget/TextView;", "textSizeDp", _UrlKt.FRAGMENT_ENCODE_SET, "colorKey", "left", "top", "right", "bottom", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class PythonSdkUpdateAlert extends UpdateAppAlertDialog {
    private static final int AUTO_UPDATE_ROW_HEIGHT = 36;
    private static final int BUTTON_GAP = 12;
    private boolean enableAutoUpdate;
    private boolean installStarted;
    private final PythonPluginsEngine.Updater.Companion.PythonSdkUpdateInfo update;

    @Override // com.exteragram.messenger.updater.UpdateAppAlertDialog
    public int getExtraBottomContentHeight() {
        return 48;
    }

    @Override // com.exteragram.messenger.updater.UpdateAppAlertDialog
    public boolean hasSecondaryButton() {
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
    public /* bridge */ /* synthetic */ void setLastVisible(boolean z) {
        super.setLastVisible(z);
    }

    public PythonSdkUpdateAlert(BaseFragment baseFragment, PythonPluginsEngine.Updater.Companion.PythonSdkUpdateInfo pythonSdkUpdateInfo) {
        super(baseFragment.getParentActivity(), pythonSdkUpdateInfo, baseFragment.getCurrentAccount());
        this.update = pythonSdkUpdateInfo;
        this.textView.setSingleLine(false);
        this.textView.setEllipsize(null);
        this.textView.setGravity(1);
        TextView textView = this.textView;
        int i = Theme.key_windowBackgroundWhiteBlackText;
        restyle(textView, 18.0f, i, 40.0f, 24.0f, 40.0f, 0.0f);
        restyle(this.messageTextView, 14.0f, Theme.key_windowBackgroundWhiteGrayText, 21.0f, 4.0f, 21.0f, 0.0f);
        restyle(this.changelogTextView, 15.0f, i, 22.0f, 20.0f, 22.0f, 12.0f);
        if (this.appUpdate.can_not_skip) {
            setCanDismissWithSwipe(false);
            this.allowNestedScroll = false;
            setCanDismissWithTouchOutside(false);
            setCancelable(false);
            setDelegate(new BottomSheet.BottomSheetDelegate() { // from class: com.exteragram.messenger.plugins.ui.components.PythonSdkUpdateAlert.1
                @Override // org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegate, org.telegram.ui.ActionBar.BottomSheet.BottomSheetDelegateInterface
                public boolean canDismiss() {
                    return PythonSdkUpdateAlert.this.installStarted || PythonSdkUpdateAlert.this.isActivityGone();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final boolean isActivityGone() {
        Context context = getContext();
        Activity activity = context instanceof Activity ? (Activity) context : null;
        if (activity == null) {
            return false;
        }
        return activity.isFinishing() || activity.isDestroyed();
    }

    @Override // com.exteragram.messenger.updater.UpdateAppAlertDialog
    public String getDoneButtonText() {
        return LocaleController.getString(R.string.AppUpdateNow);
    }

    @Override // com.exteragram.messenger.updater.UpdateAppAlertDialog
    public String getTitleText() {
        return LocaleController.getString(R.string.PluginsPySdkUpdate);
    }

    @Override // com.exteragram.messenger.updater.UpdateAppAlertDialog
    public void addContentAfterDoneButton(FrameLayout container) {
        CheckBoxRow checkBoxRow = new CheckBoxRow(getContext(), LocaleController.getString(R.string.EnableAutoUpdate), false, this.resourcesProvider, 4, null);
        checkBoxRow.setOnCheckedChange(new Function1() { // from class: com.exteragram.messenger.plugins.ui.components.PythonSdkUpdateAlert$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return PythonSdkUpdateAlert.$r8$lambda$iuilDbtJf0qlamnol_4f5Zg4NB4(PythonSdkUpdateAlert.this, ((Boolean) obj).booleanValue());
            }
        });
        container.addView(checkBoxRow, LayoutHelper.createFrame(-2, -2.0f, 81, 0.0f, 0.0f, 0.0f, 8.0f));
    }

    public static Unit $r8$lambda$iuilDbtJf0qlamnol_4f5Zg4NB4(PythonSdkUpdateAlert pythonSdkUpdateAlert, boolean z) {
        pythonSdkUpdateAlert.enableAutoUpdate = z;
        return Unit.INSTANCE;
    }

    @Override // com.exteragram.messenger.updater.UpdateAppAlertDialog
    public void onDone() {
        if (this.enableAutoUpdate) {
            com.exteragram.messenger.ExteraConfig.setPluginsPySdkAutoUpdate(true);
        }
        try {
            PythonPluginsEngine.Updater.INSTANCE.savePythonSdkArchive(this.update.getMessage(), this.update.document, true);
        } catch (Exception e) {
            String channel = this.update.getChannel();
            TLRPC.Message message = this.update.getMessage();
            FileLog.e("Failed to load python-plugins-sdk file (" + channel + ", message id = " + (message != null ? Integer.valueOf(message.id) : null) + ")", e);
        }
        this.installStarted = true;
        dismiss();
    }

    private final void restyle(TextView textView, float f, int i, float f2, float f3, float f4, float f5) {
        textView.setTextSize(1, f);
        textView.setTextColor(getThemedColor(i));
        ((LinearLayout.LayoutParams) textView.getLayoutParams()).setMargins(AndroidUtilities.dp(f2), AndroidUtilities.dp(f3), AndroidUtilities.dp(f4), AndroidUtilities.dp(f5));
    }
}
