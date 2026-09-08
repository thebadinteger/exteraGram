package com.exteragram.messenger.preferences;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.TextView;
import com.android.tools.r8.RecordTag;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.api.db.DatabaseHelper;
import com.exteragram.messenger.api.dto.BoostySubscriberDTO;
import com.exteragram.messenger.backup.PreferencesUtils;
import com.exteragram.messenger.badges.BadgesController;
import com.exteragram.messenger.components.BoostyBottomSheet;
import com.exteragram.messenger.components.SupporterBottomSheet;
import com.exteragram.messenger.export.ui.ExportActivity;
import com.exteragram.messenger.plugins.Plugin;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.utils.network.RemoteUtils;
import com.exteragram.messenger.utils.system.VibratorUtils;
import com.google.android.gms.cast.framework.media.NotificationOptions;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LinkifyPort;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;

public class OtherPreferencesActivity extends BasePreferencesActivity {
    private List<Donate> donates = new ArrayList();
    private final List<BoostySubscriberDTO> subscribers = new ArrayList();

    public static List<Donate> getDonates() {
        Set<String> stringSetConfigValue = RemoteUtils.getStringSetConfigValue("donates", Collections.EMPTY_SET);
        ArrayList arrayList = new ArrayList();
        Iterator<String> it = stringSetConfigValue.iterator();
        while (it.hasNext()) {
            String[] strArrSplit = it.next().split("#");
            if (strArrSplit.length == 2) {
                arrayList.add(new Donate(strArrSplit[0], strArrSplit[1]));
            }
        }
        return arrayList;
    }

    public enum OtherItem {
        CRASHLYTICS,
        ANALYTICS,
        EXPORT_SETTINGS,
        EXPORT_DATA,
        RESET_SETTINGS,
        DELETE_ACCOUNT,
        DONATE;

        public int getId() {
            return ordinal() + 1;
        }
    }

    @Override 
    public View createView(Context context) {
        final List<BoostySubscriberDTO> list = this.subscribers;
        Objects.requireNonNull(list);
        DatabaseHelper.getBoostySubscribers(new Consumer() { 
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                list.addAll((List) obj);
            }
        });
        return super.createView(context);
    }

    @Override 
    public String getTitle() {
        return R.string.LocalOther);
    }

    @Override 
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        Map mapM = com.exteragram.messenger.utils.RecordUtils.mapOf(new Map.Entry[]{new AbstractMap.SimpleEntry("mastercard", new IconInfo(R.drawable.mastercard_icon, Theme.isCurrentThemeDark() ? "M50,0A50,50,0,0,1,50,100A50,50,0,0,1,50,0" : "100\u0006n2_200\u0006n2_300\u0006n2_400\u0006n2_500\u0006n2_600\u0006n2_700\u0006n2_800\u0006n2_900%android.intent.action.OVERLAY_CHANG"), new AbstractMap.SimpleEntry("tonkeeper", new IconInfo(R.drawable.ton_icon, Theme.isCurrentThemeDark() ? -14207411 : -15722977)), new AbstractMap.SimpleEntry("space", new IconInfo(R.drawable.ton_space_icon, -13587978)), new AbstractMap.SimpleEntry("boosty", new IconInfo(R.drawable.boosty_icon, Theme.isCurrentThemeDark() ? -1118482 : -14406868))});
        List<Donate> donates = getDonates();
        this.donates = donates;
        if (!donates.isEmpty()) {
            arrayList.add(UItem.asHeader(LocaleController.getString(R.string.Support)));
            int i = 0;
            for (int i2 = 0; i2 < this.donates.size(); i2++) {
                Donate donate = this.donates.get(i2);
                String lowerCase = donate.name().toLowerCase();
                IconInfo iconInfo = new IconInfo(R.drawable.msg_payment_card, i);
                for (Map.Entry entry : mapM.entrySet()) {
                    if (lowerCase.contains((CharSequence) entry.getKey())) {
                        iconInfo = (IconInfo) entry.getValue();
                        break;
                    }
                }
                UItem searchable = UItem.asButton(OtherItem.DONATE.getId() + i2, donate.name()).setSearchable(this);
                if (iconInfo.iconColor == 0) {
                    searchable.setIcon(iconInfo.iconResId);
                } else {
                    searchable.setColorfulIcon(iconInfo.iconResId, iconInfo.iconColor);
                }
                arrayList.add(searchable);
            }
            arrayList.add(UItem.asShadow(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.GetBadgeInfo), new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$fillItems$0();
                }
            })));
        }
        arrayList.add(UItem.asHeader("Google"));
        arrayList.add(UItem.asCheck(OtherItem.CRASHLYTICS.getId(), "Crashlytics", R.drawable.msg_report).setChecked(ExteraConfig.getUseGoogleCrashlytics()).setSearchable(this).setLinkAlias("crashlytics", this));
        arrayList.add(UItem.asCheck(OtherItem.ANALYTICS.getId(), "Analytics", R.drawable.msg_data).setChecked(ExteraConfig.getUseGoogleAnalytics()).setSearchable(this).setLinkAlias("analytics", this));
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.AnalyticsInfo)));
        arrayList.add(UItem.asButton(OtherItem.EXPORT_SETTINGS.getId(), R.drawable.msg_settings, LocaleController.getString(R.string.ExportSettings)).setSearchable(this).setLinkAlias("exportSettings", this));
        int iCount = (int) PluginsController.getInstance().getPlugins().values().stream().filter(new Predicate() { 
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((Plugin) obj).isEnabled();
            }
        }).count();
        if (BadgesController.INSTANCE.isDeveloper() && iCount <= 1) {
            arrayList.add(UItem.asButton(OtherItem.EXPORT_DATA.getId(), R.drawable.msg_archive, LocaleController.getString(R.string.ExportData)).setSearchable(this).setLinkAlias("exportData", this));
        }
        arrayList.add(UItem.asButton(OtherItem.RESET_SETTINGS.getId(), R.drawable.msg_reset, LocaleController.getString(R.string.ResetSettings)).setSearchable(this).setLinkAlias("resetSettings", this));
        arrayList.add(UItem.asButton(OtherItem.DELETE_ACCOUNT.getId(), R.drawable.msg_clearcache, LocaleController.getString(R.string.DeleteAccount)).red().setSearchable(this).setLinkAlias("deleteAccount", this));
        arrayList.add(UItem.asShadow());
    }

    public class AnonymousClass3 {
        static final void lambda$handleResetSettingsClick$1(AlertDialog alertDialog, int i) {
        PreferencesUtils.clearPreferences();
        this.parentLayout.rebuildFragments(0);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        LocaleController.getInstance().recreateFormatters();
        Theme.reloadAllResources(getParentActivity());
        BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.ResetPreferences), getResourceProvider()).show();
    }

    private void handleDeleteAccountClick() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setMessage(LocaleController.getString(R.string.TosDeclineDeleteAccount));
        builder.setTitle(LocaleController.getString(R.string.DeleteAccount));
        builder.setPositiveButton(LocaleController.getString(R.string.Deactivate), new AlertDialog.OnButtonClickListener() { 
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                this.f$0.lambda$handleDeleteAccountClick$5(alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        final AlertDialog alertDialogCreate = builder.create();
        alertDialogCreate.setOnShowListener(new DialogInterface.OnShowListener() { 
            @Override // android.content.DialogInterface.OnShowListener
            public final void onShow(DialogInterface dialogInterface) {
                this.f$0.lambda$handleDeleteAccountClick$6(alertDialogCreate, dialogInterface);
            }
        });
        showDialog(alertDialogCreate);
    }

    public void lambda$handleDeleteAccountClick$4(final AlertDialog alertDialog) {
        TL_account.deleteAccount deleteaccount = new TL_account.deleteAccount();
        deleteaccount.reason = "ЭКСТЕРАГРАМ";
        getConnectionsManager().sendRequest(deleteaccount, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$handleDeleteAccountClick$3(alertDialog, tLObject, tL_error);
            }
        });
    }

    public void lambda$handleDeleteAccountClick$2(AlertDialog alertDialog, TLObject tLObject, TLRPC.TL_error tL_error) {
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            getMessagesController().performLogout(0);
            return;
        }
        if (tL_error == null || tL_error.code != -1000) {
            String string = LocaleController.getString(R.string.ErrorOccurred);
            if (tL_error != null) {
                string = string + "\n" + tL_error.text;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.AppName));
            builder.setMessage(string);
            builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
            builder.show();
        }
    }

    public boolean $record$equals(Object obj) {
            if (!(obj instanceof IconInfo)) {
                return false;
            }
            IconInfo iconInfo = (IconInfo) obj;
            return this.iconResId == iconInfo.iconResId && this.iconColor == iconInfo.iconColor;
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Integer.valueOf(this.iconResId), Integer.valueOf(this.iconColor)};
        }

        private IconInfo(int i, int i2) {
            this.iconResId = i;
            this.iconColor = i2;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return java.util.Objects.hash(this.iconResId, this.iconColor);
        }

        public final String toString() {
            return com.exteragram.messenger.utils.RecordUtils.recordToString($record$getFieldsAsObjects(), IconInfo.class, "iconResId;iconColor");
        }
    }

    public static final class Donate extends RecordTag {
        private final String details;
        private final String name;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof Donate)) {
                return false;
            }
            Donate donate = (Donate) obj;
            return Objects.equals(this.name, donate.name) && Objects.equals(this.details, donate.details);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.name, this.details};
        }

        public Donate(String str, String str2) {
            this.name = str;
            this.details = str2;
        }

        public String details() {
            return this.details;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return java.util.Objects.hash(this.name, this.details);
        }

        public String name() {
            return this.name;
        }

        public final String toString() {
            return com.exteragram.messenger.utils.RecordUtils.recordToString($record$getFieldsAsObjects(), Donate.class, "name;details");
        }
    }
}
