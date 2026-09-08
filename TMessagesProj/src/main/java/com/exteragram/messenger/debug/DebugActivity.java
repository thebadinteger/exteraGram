package com.exteragram.messenger.debug;

import android.app.Activity;
import android.text.Editable;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import com.exteragram.messenger.api.ApiController;
import com.exteragram.messenger.api.db.ExteraDatabase;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;

@Metadata(d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0005\u0018\u00002\u00020\u0001:\u0001\u0019B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0016J(\u0010\u0006\u001a\u00020\u00072\u0016\u0010\b\u001a\u0012\u0012\u0004\u0012\u00020\n0\tj\b\u0012\u0004\u0012\u00020\n`\u000b2\u0006\u0010\f\u001a\u00020\rH\u0014J0\u0010\u000e\u001a\u00020\u00072\u0006\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\u0006\u0010\u0016\u001a\u00020\u0015H\u0014J\b\u0010\u0017\u001a\u00020\u0005H\u0002J\b\u0010\u0018\u001a\u00020\u0007H\u0002¨\u0006\u001a"}, d2 = {"Lcom/exteragram/messenger/debug/DebugActivity;", "Lcom/exteragram/messenger/preferences/BasePreferencesActivity;", "<init>", "()V", "getTitle", _UrlKt.FRAGMENT_ENCODE_SET, "fillItems", _UrlKt.FRAGMENT_ENCODE_SET, "items", "Ljava/util/ArrayList;", "Lorg/telegram/ui/Components/UItem;", "Lkotlin/collections/ArrayList;", "adapter", "Lorg/telegram/ui/Components/UniversalAdapter;", "onClick", "item", "view", "Landroid/view/View;", "position", _UrlKt.FRAGMENT_ENCODE_SET, "x", _UrlKt.FRAGMENT_ENCODE_SET, "y", "getIpConfigOverrideValue", "showIpConfigOverrideDialog", "DebugItem", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nDebugActivity.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DebugActivity.kt\ncom/exteragram/messenger/debug/DebugActivity\n+ 2 SparseArray.kt\nandroidx/core/util/SparseArrayKt\n*L\n1#1,219:1\n25#2:220\n*S KotlinDebug\n*F\n+ 1 DebugActivity.kt\ncom/exteragram/messenger/debug/DebugActivity\n*L\n113#1:220\n*E\n"})
public final class DebugActivity extends BasePreferencesActivity {

    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    public static final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[DebugItem.values().length];
            try {
                iArr[DebugItem.DEBUG_CAMERA_METRICS.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[DebugItem.FORCE_COMPACT_SAVED_MUSIC.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[DebugItem.DISABLE_CHAT_FADE_WALLPAPER_BLEND.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                iArr[DebugItem.CHAT_FADE_USE_WHITE_BACKGROUND.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                iArr[DebugItem.GLASS_HEADER_MENU.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                iArr[DebugItem.CLEAR_DB.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                iArr[DebugItem.CLEAR_TRANSLATIONS.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                iArr[DebugItem.SET_IPCONFIG_OVERRIDE.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                iArr[DebugItem.CLEAR_IPCONFIG_OVERRIDE.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\f\n\u0002\u0010\b\n\u0000\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\r\u001a\u00020\u000ej\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\f¨\u0006\u000f"}, d2 = {"Lcom/exteragram/messenger/debug/DebugActivity$DebugItem;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;I)V", "DEBUG_CAMERA_METRICS", "FORCE_COMPACT_SAVED_MUSIC", "DISABLE_CHAT_FADE_WALLPAPER_BLEND", "CHAT_FADE_USE_WHITE_BACKGROUND", "GLASS_HEADER_MENU", "CLEAR_DB", "CLEAR_TRANSLATIONS", "SET_IPCONFIG_OVERRIDE", "CLEAR_IPCONFIG_OVERRIDE", "getId", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public enum DebugItem {
        DEBUG_CAMERA_METRICS,
        FORCE_COMPACT_SAVED_MUSIC,
        DISABLE_CHAT_FADE_WALLPAPER_BLEND,
        CHAT_FADE_USE_WHITE_BACKGROUND,
        GLASS_HEADER_MENU,
        CLEAR_DB,
        CLEAR_TRANSLATIONS,
        SET_IPCONFIG_OVERRIDE,
        CLEAR_IPCONFIG_OVERRIDE;

        private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());

        public static EnumEntries<DebugItem> getEntries() {
            return $ENTRIES;
        }

        public final int getId() {
            return ordinal() + 1;
        }
    }

    @Override 
    public String getTitle() {
        return "Debug";
    }

    @Override 
    public void fillItems(ArrayList<UItem> items, UniversalAdapter adapter) {
        items.add(UItem.asCheck(DebugItem.DEBUG_CAMERA_METRICS.getId(), "Metrics in InstantCameraView").setChecked(DebugConfig.getDebugCameraMetrics()));
        items.add(UItem.asCheck(DebugItem.FORCE_COMPACT_SAVED_MUSIC.getId(), "Compact Saved Music view").setChecked(DebugConfig.getForceCompactSavedMusic()));
        items.add(UItem.asCheck(DebugItem.DISABLE_CHAT_FADE_WALLPAPER_BLEND.getId(), "Disable chat fade wallpaper blend").setChecked(DebugConfig.getDisableChatFadeWallpaperBlend()));
        items.add(UItem.asCheck(DebugItem.CHAT_FADE_USE_WHITE_BACKGROUND.getId(), "Use windowBackgroundWhite for chat fade").setChecked(DebugConfig.getChatFadeUseWhiteBackground()));
        items.add(UItem.asCheck(DebugItem.GLASS_HEADER_MENU.getId(), "Glass three-dot menu").setChecked(DebugConfig.getGlassHeaderMenu()));
        items.add(UItem.asShadow());
        items.add(UItem.asButton(DebugItem.CLEAR_DB.getId(), "Clear exteraDatabase"));
        items.add(UItem.asButton(DebugItem.CLEAR_TRANSLATIONS.getId(), "Clear translations cache"));
        items.add(UItem.asShadow());
        items.add(UItem.asButton(DebugItem.SET_IPCONFIG_OVERRIDE.getId(), "Set ipconfigv3 override", getIpConfigOverrideValue()));
        items.add(UItem.asButton(DebugItem.CLEAR_IPCONFIG_OVERRIDE.getId(), "Clear ipconfigv3 override"));
    }

    @Override 
    public void onClick(UItem item, View view, int position, float x, float y) {
        int i = item.id;
        if (i <= 0 || i > DebugItem.getEntries().size()) {
            return;
        }
        switch (WhenMappings.$EnumSwitchMapping$0[DebugItem.getEntries().get(item.id - 1).ordinal()]) {
            case 1:
                toggleBooleanSettingAndRefresh(item, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        DebugConfig.setDebugCameraMetrics(((Boolean) obj).booleanValue());
                    }
                });
                break;
            case 2:
                toggleBooleanSettingAndRefresh(item, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        DebugConfig.setForceCompactSavedMusic(((Boolean) obj).booleanValue());
                    }
                });
                break;
            case 3:
                toggleBooleanSettingAndRefresh(item, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        DebugConfig.setDisableChatFadeWallpaperBlend(((Boolean) obj).booleanValue());
                    }
                });
                break;
            case 4:
                toggleBooleanSettingAndRefresh(item, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        DebugConfig.setChatFadeUseWhiteBackground(((Boolean) obj).booleanValue());
                    }
                });
                break;
            case 5:
                toggleBooleanSettingAndRefresh(item, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        DebugConfig.setGlassHeaderMenu(((Boolean) obj).booleanValue());
                    }
                });
                break;
            case 6:
                Utilities.globalQueue.postRunnable(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        DebugActivity.$r8$lambda$zzHFi_Grpm9hEdP3kmCcKKwe6fc(this.f$0);
                    }
                });
                break;
            case 7:
                getMessagesController().getTranslateController().clearTranslationCache();
                LongSparseArray<ArrayList<MessageObject>> longSparseArray = getMessagesController().dialogMessage;
                if (longSparseArray != null) {
                    int size = longSparseArray.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        ArrayList<MessageObject> arrayListValueAt = longSparseArray.valueAt(i2);
                        if (arrayListValueAt != null) {
                            int size2 = arrayListValueAt.size();
                            for (int i3 = 0; i3 < size2; i3++) {
                                getMessagesController().getTranslateController().clearMessageTranslationState(arrayListValueAt.get(i3));
                            }
                        }
                    }
                }
                SparseArray<MessageObject> sparseArray = getMessagesController().dialogMessagesByIds;
                if (sparseArray != null) {
                    int size3 = sparseArray.size();
                    for (int i4 = 0; i4 < size3; i4++) {
                        getMessagesController().getTranslateController().clearMessageTranslationState(sparseArray.valueAt(i4));
                    }
                }
                LongSparseArray<MessageObject> longSparseArray2 = getMessagesController().dialogMessagesByRandomIds;
                if (longSparseArray2 != null) {
                    int size4 = longSparseArray2.size();
                    for (int i5 = 0; i5 < size4; i5++) {
                        getMessagesController().getTranslateController().clearMessageTranslationState(longSparseArray2.valueAt(i5));
                    }
                }
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        BulletinFactory.of(this.f$0).createSimpleBulletin(R.raw.contact_check, "Translation cache cleared.").show();
                    }
                });
                break;
            case 8:
                showIpConfigOverrideDialog();
                break;
            case 9:
                ConnectionsManager.setDebugDnsConfigOverride(null);
                this.listView.adapter.update(true);
                BulletinFactory.of(this).createSimpleBulletin(R.raw.contact_check, "ipconfigv3 override cleared.").show();
                break;
            default:
                LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0.m();
                break;
        }
    }

    public static void $r8$lambda$zzHFi_Grpm9hEdP3kmCcKKwe6fc(final DebugActivity debugActivity) {
        ExteraDatabase.INSTANCE.getInstance().clearAllTables();
        ApiController.resetSyncState$default(null, 1, null);
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                DebugActivity.onClick$lambda$5$0(this.f$0);
            }
        });
    }

    public static final void onClick$lambda$5$0(DebugActivity debugActivity) {
        BulletinFactory.of(debugActivity).createSimpleBulletin(R.raw.contact_check, "Successfully cleared all tables.").show();
    }

    private final String getIpConfigOverrideValue() {
        String debugDnsConfigOverride = ConnectionsManager.getDebugDnsConfigOverride();
        if (debugDnsConfigOverride == null || StringsKt.isBlank(debugDnsConfigOverride)) {
            return "Not set";
        }
        return debugDnsConfigOverride.length() + " chars";
    }

    private final void showIpConfigOverrideDialog() {
        Activity parentActivity = getParentActivity();
        if (parentActivity == null) {
            return;
        }
        final EditTextBoldCursor editTextBoldCursor = new EditTextBoldCursor(parentActivity);
        editTextBoldCursor.lineYFix = true;
        editTextBoldCursor.setTextSize(1, 18.0f);
        editTextBoldCursor.setText(ConnectionsManager.getDebugDnsConfigOverride());
        editTextBoldCursor.setTextColor(Theme.getColor(Theme.key_dialogTextBlack, this.resourceProvider));
        editTextBoldCursor.setHintColor(Theme.getColor(Theme.key_dialogTextHint, this.resourceProvider));
        editTextBoldCursor.setHintText("Base64 from Firebase Remote Config");
        editTextBoldCursor.setGravity(8388659);
        editTextBoldCursor.setMinLines(6);
        editTextBoldCursor.setMaxLines(10);
        editTextBoldCursor.setInputType(655361);
        int i = Theme.key_dialogInputFieldActivated;
        editTextBoldCursor.setCursorColor(Theme.getColor(i, this.resourceProvider));
        editTextBoldCursor.setLineColors(Theme.getColor(Theme.key_dialogInputField, this.resourceProvider), Theme.getColor(i, this.resourceProvider), Theme.getColor(Theme.key_text_RedRegular, this.resourceProvider));
        editTextBoldCursor.setBackground(null);
        editTextBoldCursor.setPadding(0, AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f));
        Editable text = editTextBoldCursor.getText();
        editTextBoldCursor.setSelection(text != null ? text.length() : 0);
        FrameLayout frameLayout = new FrameLayout(parentActivity);
        frameLayout.addView(editTextBoldCursor, LayoutHelper.createFrame(-1, -2.0f, 8388659, 24.0f, 12.0f, 24.0f, 0.0f));
        showDialog(new AlertDialog.Builder(parentActivity).setTitle("ipconfigv3 override").setView(frameLayout).setPositiveButton("Apply", new AlertDialog.OnButtonClickListener() { 
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i2) {
                DebugActivity.$r8$lambda$6gvSBDYHjPsw8Dfps2dMtzBMnI4(editTextBoldCursor, this, alertDialog, i2);
            }
        }).setNegativeButton("Cancel", null).create());
    }

    public static void $r8$lambda$6gvSBDYHjPsw8Dfps2dMtzBMnI4(EditTextBoldCursor editTextBoldCursor, DebugActivity debugActivity, AlertDialog alertDialog, int i) {
        String string;
        Editable text = editTextBoldCursor.getText();
        String string2 = (text == null || (string = text.toString()) == null) ? null : StringsKt.trim((CharSequence) string).toString();
        if (string2 == null) {
            string2 = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        if (TextUtils.isEmpty(string2)) {
            ConnectionsManager.setDebugDnsConfigOverride(null);
            debugActivity.listView.adapter.update(true);
            BulletinFactory.of(debugActivity).createSimpleBulletin(R.raw.contact_check, "ipconfigv3 override cleared.").show();
        } else {
            boolean andApplyDebugDnsConfigOverride = ConnectionsManager.setAndApplyDebugDnsConfigOverride(debugActivity.currentAccount, string2);
            debugActivity.listView.adapter.update(true);
            BulletinFactory.of(debugActivity).createSimpleBulletin(andApplyDebugDnsConfigOverride ? R.raw.contact_check : R.raw.error, andApplyDebugDnsConfigOverride ? "ipconfigv3 override saved and submitted." : "Failed to decode ipconfigv3 override.").show();
        }
    }
}
