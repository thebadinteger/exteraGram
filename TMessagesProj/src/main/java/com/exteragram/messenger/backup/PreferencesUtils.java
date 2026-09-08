package com.exteragram.messenger.backup;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import androidx.collection.LongSparseArray;
import com.chaquo.python.internal.Common;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.ai.AiConfig;
import com.exteragram.messenger.pillstack.core.PillStackConfig;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ShareAlert;

public class PreferencesUtils {
    private static PreferencesUtils instance;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final Set<String> excludedExteraKeys = PreferencesUtils$$ExternalSyntheticBackport1.m(new Object[]{"editingIconPackId", "iconPacksLayout", "iconPacksHidden", "updateScheduleTimestamp", "sdkUpdateScheduleTimestamp", "selectedService", "lastActivePillId", "debugCameraMetrics", "forceCompactSavedMusic", "debugSectionRadiusOption"});
    private static final BackupItem[] extraExteraKeys = {new BackupItem("bottomNavigationBarMode", Integer.class), new BackupItem("mainMenuLayout", String.class), new BackupItem("mainMenuHiddenItems", String.class), new BackupItem("targetLangSend", String.class), new BackupItem("pluginsEngine", Boolean.class), new BackupItem("pinnedPlugins", Set.class), new BackupItem("saveHistory", Boolean.class), new BackupItem("responseStreaming", Boolean.class), new BackupItem("temperature", Integer.class), new BackupItem("showResponseOnly", Boolean.class), new BackupItem("insertAsQuote", Boolean.class), new BackupItem("selectedRole", String.class), new BackupItem("infiniteScrolling", Boolean.class), new BackupItem("gramTargetCurrency", String.class), new BackupItem("btcTargetCurrency", String.class), new BackupItem("usdTargetCurrency", String.class)};
    private static final BackupItem[] aiConfigKeys = {new BackupItem("roles", String.class)};
    private static final BackupItem[] pillStackConfigKeys = {new BackupItem("activePills", String.class), new BackupItem("hiddenPills", String.class)};
    private static final BackupItem[] mainConfigKeys = {new BackupItem("ChatSwipeAction", Integer.class), new BackupItem("mediaColumnsCount", Integer.class), new BackupItem("bubbleRadius", Integer.class), new BackupItem("fons_size", Integer.class)};
    private static final String[] configs = {"exteraconfig", "aiConfig", "pillstackconfig", "mainconfig"};

    public static PreferencesUtils getInstance() {
        if (instance == null) {
            instance = new PreferencesUtils();
        }
        return instance;
    }

    public static void clearPreferences() {
        AiConfig.getEditor().clear().apply();
        PillStackConfig.getEditor().clear().apply();
        ExteraConfig.getEditor().clear().apply();
        ExteraConfig.reloadConfig();
        PillStackConfig.reloadConfig();
    }

    private static Context getContext() {
        if (ApplicationLoader.applicationContext != null) {
            return ApplicationLoader.applicationContext;
        }
        return AndroidUtilities.getActivity();
    }

    public static SharedPreferences getPreferences(String str) {
        return getContext().getSharedPreferences(str, 0);
    }

    public static String generateBackupName(String str) {
        StringBuilder sb = new StringBuilder();
        if (str == null) {
            str = "backup";
        }
        sb.append(str);
        sb.append("-");
        sb.append(Utilities.generateRandomString(4));
        sb.append(".extera");
        return sb.toString();
    }

    private BackupItem findBackupItem(String str, String str2) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return null;
        }
        str.getClass();
        switch (str) {
            case "aiConfig":
                return findBackupItem(aiConfigKeys, str2);
            case "pillstackconfig":
                return findBackupItem(pillStackConfigKeys, str2);
            case "exteraconfig":
                for (BackupItem backupItem : extraExteraKeys) {
                    if (backupItem.key.equals(str2)) {
                        return backupItem;
                    }
                }
                if (excludedExteraKeys.contains(str2)) {
                    return null;
                }
                for (BackupItem backupItem2 : ExteraConfig.getBackupKeys()) {
                    if (backupItem2.key.equals(str2)) {
                        return backupItem2;
                    }
                }
                return null;
            case "mainconfig":
                return findBackupItem(mainConfigKeys, str2);
            default:
                return null;
        }
    }

    private BackupItem findBackupItem(BackupItem[] backupItemArr, String str) {
        for (BackupItem backupItem : backupItemArr) {
            if (backupItem.key.equals(str)) {
                return backupItem;
            }
        }
        return null;
    }

    private boolean isExpectedValue(String str, String str2, Object obj) {
        JsonElement jsonTree;
        BackupItem backupItemFindBackupItem = findBackupItem(str, str2);
        if (backupItemFindBackupItem != null && obj != null) {
            if (obj instanceof JsonElement) {
                jsonTree = (JsonElement) obj;
            } else {
                jsonTree = this.gson.toJsonTree(obj);
            }
            try {
                if (backupItemFindBackupItem.clazz.equals(Boolean.class)) {
                    return jsonTree.isJsonPrimitive() && jsonTree.getAsJsonPrimitive().isBoolean();
                }
                if (backupItemFindBackupItem.clazz.equals(Float.class)) {
                    return jsonTree.isJsonPrimitive() && jsonTree.getAsJsonPrimitive().isNumber() && isExpectedFloat(str2, jsonTree.getAsFloat());
                }
                if (backupItemFindBackupItem.clazz.equals(String.class)) {
                    return jsonTree.isJsonPrimitive() && jsonTree.getAsJsonPrimitive().isString() && isExpectedString(str, str2, jsonTree.getAsString());
                }
                if (backupItemFindBackupItem.clazz.equals(Set.class)) {
                    return isExpectedStringSet(str2, jsonTree);
                }
                Integer exactInteger = getExactInteger(jsonTree);
                return exactInteger != null && isExpectedInteger(str2, exactInteger.intValue());
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return false;
    }

    private Integer getExactInteger(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            try {
                return Integer.valueOf(new BigDecimal(jsonElement.getAsString()).intValueExact());
            } catch (ArithmeticException | NumberFormatException unused) {
            }
        }
        return null;
    }

    private Long getExactLong(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive() && jsonElement.getAsJsonPrimitive().isNumber()) {
            try {
                return Long.valueOf(new BigDecimal(jsonElement.getAsString()).longValueExact());
            } catch (ArithmeticException | NumberFormatException unused) {
            }
        }
        return null;
    }

    private boolean isExpectedFloat(String str, float f) {
        if (!Float.isFinite(f)) {
            return false;
        }
        str.getClass();
        switch (str) {
            case "sectionRadius":
                return f >= 0.0f && f <= 28.0f;
            case "avatarCorners":
                return f >= 0.0f && f <= 28.0f;
            case "flashIntensity":
            case "flashWarmth":
                return f >= 0.0f && f <= 1.0f;
            case "stickerSize":
                return f >= 4.0f && f <= 20.0f;
            case "predictiveBackIntensity":
                return f >= 0.0f && f <= 5.0f;
            default:
                return false;
        }
    }

    private boolean isExpectedInteger(String str, int i) {
        str.getClass();
        switch (str) {
            case "titleText":
            case "doubleTapSeekDuration":
            case "translationProvider":
                return i >= 0 && i <= 3;
            case "stickerShape":
            case "cameraType":
            case "downloadSpeedBoost":
            case "tabletMode":
            case "videoMessagesCamera":
            case "tabIcons":
            case "iconPack":
            case "bottomNavigationBarMode":
            case "translationFormality":
            case "bottomButton":
            case "showIdAndDc":
                return i >= 0 && i <= 2;
            case "bubbleRadius":
                return i >= 0 && i <= 17;
            case "ChatSwipeAction":
                return i >= 0 && i <= 5;
            case "mediaColumnsCount":
                return i >= 2 && i <= 9;
            case "fons_size":
                return i >= 12 && i <= 30;
            case "doubleTapAction":
                return i >= 0 && i <= 8;
            case "eventType":
                return i >= 0 && i <= 4;
            case "temperature":
                return i >= 0 && i <= 20;
            case "doubleTapActionOutOwner":
                return i >= 0 && i <= 9;
            default:
                return false;
        }
    }

    private boolean isExpectedString(String str, String str2, String str3) {
        if (str3 != null && str3.length() <= 1048576) {
            str2.getClass();
            switch (str2) {
                case "selectedRole":
                    if (!TextUtils.isEmpty(str3) && str3.length() <= 256) {
                        return true;
                    }
                    break;
                case "customSavePath":
                    return str3.matches("^(?!\\.{1,2}$)[A-Za-z0-9._ -]{1,255}$");
                case "mainMenuLayout":
                    return isExpectedMainMenuLayout(str3, true);
                case "targetLang":
                case "targetLangSend":
                    return str3.equalsIgnoreCase(Common.ASSET_APP) || str3.matches("^[a-zA-Z]{1,8}(-[a-zA-Z0-9]{1,8})*$");
                case "mainMenuHiddenItems":
                    return isExpectedMainMenuLayout(str3, false);
                default:
                    if ("aiConfig".equals(str) && str2.equals("roles")) {
                        return isExpectedRoles(str3);
                    }
                    if ("pillstackconfig".equals(str) && (str2.equals("activePills") || str2.equals("hiddenPills"))) {
                        return isExpectedPillsLayout(str3);
                    }
                    if (str2.equals("gramTargetCurrency") || str2.equals("btcTargetCurrency") || str2.equals("usdTargetCurrency")) {
                        return str3.matches("^[A-Z]{3,5}$");
                    }
                    return !TextUtils.isEmpty(str3);
            }
        }
        return false;
    }

    private boolean isExpectedStringSet(String str, JsonElement jsonElement) {
        if (!str.equals("pinnedPlugins") || !jsonElement.isJsonArray() || jsonElement.getAsJsonArray().size() > 1000) {
            return false;
        }
        HashSet hashSet = new HashSet();
        for (JsonElement jsonElement2 : jsonElement.getAsJsonArray()) {
            if (jsonElement2.isJsonPrimitive() && jsonElement2.getAsJsonPrimitive().isString()) {
                String asString = jsonElement2.getAsString();
                if (TextUtils.isEmpty(asString) || asString.length() > 255 || !hashSet.add(asString)) {
                }
            }
            return false;
        }
        return true;
    }

    private boolean isExpectedMainMenuLayout(String str, boolean z) {
        JsonElement jsonElement = (JsonElement) this.gson.fromJson(str, JsonElement.class);
        if (jsonElement == null || !jsonElement.isJsonArray() || jsonElement.getAsJsonArray().size() > 100) {
            return false;
        }
        HashSet hashSet = new HashSet();
        Iterator<JsonElement> it = jsonElement.getAsJsonArray().iterator();
        while (it.hasNext()) {
            Integer exactInteger = getExactInteger(it.next());
            if (exactInteger == null) {
                return false;
            }
            if (exactInteger.intValue() == -1) {
                if (!z) {
                    return false;
                }
            } else if (!hashSet.add(exactInteger)) {
                return false;
            }
        }
        return true;
    }

    private boolean isExpectedRoles(String str) {
        JsonElement jsonElement = (JsonElement) this.gson.fromJson(str, JsonElement.class);
        if (jsonElement == null || !jsonElement.isJsonArray() || jsonElement.getAsJsonArray().size() > 100) {
            return false;
        }
        HashSet hashSet = new HashSet();
        for (JsonElement jsonElement2 : jsonElement.getAsJsonArray()) {
            if (!jsonElement2.isJsonObject()) {
                return false;
            }
            JsonObject asJsonObject = jsonElement2.getAsJsonObject();
            if (!isRequiredString(asJsonObject, "name", 256) || !isRequiredString(asJsonObject, "prompt", 65536) || !hashSet.add(asJsonObject.get("name").getAsString()) || (asJsonObject.has("emojiId") && !asJsonObject.get("emojiId").isJsonNull() && getExactLong(asJsonObject.get("emojiId")) == null)) {
                return false;
            }
            if (asJsonObject.has("isSuggestion") && !asJsonObject.get("isSuggestion").isJsonNull() && (!asJsonObject.get("isSuggestion").isJsonPrimitive() || !asJsonObject.get("isSuggestion").getAsJsonPrimitive().isBoolean())) {
                return false;
            }
        }
        return true;
    }

    private boolean isRequiredString(JsonObject jsonObject, String str, int i) {
        return jsonObject.has(str) && !jsonObject.get(str).isJsonNull() && jsonObject.get(str).isJsonPrimitive() && jsonObject.get(str).getAsJsonPrimitive().isString() && !TextUtils.isEmpty(jsonObject.get(str).getAsString()) && jsonObject.get(str).getAsString().length() <= i;
    }

    private boolean isExpectedPillsLayout(String str) {
        if (str.length() > 4096 || TextUtils.isEmpty(str)) {
            return TextUtils.isEmpty(str);
        }
        HashSet hashSet = new HashSet();
        for (String str2 : str.split(",")) {
            try {
                int i = Integer.parseInt(str2.trim());
                if (i <= 0 || i > 100000 || !hashSet.add(Integer.valueOf(i))) {
                    return false;
                }
            } catch (NumberFormatException unused) {
            }
        }
        return hashSet.size() <= 100;
    }

    public void exportSettings(BaseFragment baseFragment) {
        File file = new File(FileLoader.getDirectory(4), generateBackupName(null));
        if (file.exists()) {
            file.delete();
        }
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
            outputStreamWriter.write(getBackup(true));
            outputStreamWriter.flush();
            outputStreamWriter.close();
            baseFragment.showDialog(new AnonymousClass1(baseFragment.getParentActivity(), null, null, file.getAbsolutePath(), null, null, false, null, null, false, false, false, null, null, baseFragment));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public class AnonymousClass1 extends ShareAlert {
        final /* synthetic */ BaseFragment val$fragment;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AnonymousClass1(Context context, ChatActivity chatActivity, ArrayList arrayList, String str, String str2, String str3, boolean z, String str4, String str5, boolean z2, boolean z3, boolean z4, Integer num, Theme.ResourcesProvider resourcesProvider, BaseFragment baseFragment) {
            super(context, chatActivity, arrayList, str, str2, str3, z, str4, str5, z2, z3, z4, num, resourcesProvider);
            this.val$fragment = baseFragment;
        }

        @Override // org.telegram.ui.Components.ShareAlert
        public void onSend(LongSparseArray<TLRPC.Dialog> longSparseArray, int i, TLRPC.TL_forumTopic tL_forumTopic, boolean z) {
            if (z) {
                final BaseFragment baseFragment = this.val$fragment;
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        BulletinFactory.of(baseFragment).createSimpleBulletin(R.raw.contact_check, LocaleController.getString(R.string.SettingsSaved)).show();
                    }
                }, 250L);
            }
        }
    }

    public String getBackup(boolean z) {
        AiConfig.ensureConfigMigrated();
        JsonObject jsonObject = new JsonObject();
        for (String str : configs) {
            JsonObject jsonObject2 = toJsonObject(str, getPreferences(str).getAll());
            if (!jsonObject2.isEmpty()) {
                jsonObject.add(str, jsonObject2);
            }
        }
        String json = this.gson.toJson((JsonElement) jsonObject);
        return z ? InvisibleEncryptor.encode(json) : json;
    }

    private JsonObject toJsonObject(String str, Map<String, ?> map) {
        JsonObject jsonObject = new JsonObject();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (isExpectedValue(str, key, value)) {
                jsonObject.add(key, this.gson.toJsonTree(value));
            }
        }
        return jsonObject;
    }

    public void importSettings(File file, Activity activity, INavigationLayout iNavigationLayout) {
        if (isBackup(file)) {
            AiConfig.ensureConfigMigrated();
            JsonObject jsonObject = getJsonObject(file);
            for (String str : configs) {
                importConfig(jsonObject, str);
            }
            ExteraConfig.reloadConfig();
            PillStackConfig.reloadConfig();
            SharedConfig.reloadConfig();
            PluginsController.getInstance().restart();
            LocaleController.getInstance().recreateFormatters();
            Theme.reloadAllResources(activity);
            iNavigationLayout.rebuildAllFragmentViews(false, false);
            NotificationCenter notificationCenter = AccountInstance.getInstance(UserConfig.selectedAccount).getNotificationCenter();
            notificationCenter.lambda$postNotificationNameOnUIThread$1(NotificationCenter.reloadInterface, new Object[0]);
            notificationCenter.lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_CHAT));
            notificationCenter.lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
            notificationCenter.lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        }
    }

    private void importConfig(JsonObject jsonObject, String str) {
        JsonObject configObject = getConfigObject(jsonObject, str);
        if (configObject != null) {
            SharedPreferences.Editor editorEdit = getPreferences(str).edit();
            for (String str2 : configObject.keySet()) {
                JsonElement jsonElement = configObject.get(str2);
                BackupItem backupItemFindBackupItem = findBackupItem(str, str2);
                if (backupItemFindBackupItem != null && isExpectedValue(str, str2, jsonElement)) {
                    if (backupItemFindBackupItem.clazz.equals(Boolean.class)) {
                        editorEdit.putBoolean(str2, jsonElement.getAsBoolean());
                    } else if (backupItemFindBackupItem.clazz.equals(Float.class)) {
                        editorEdit.putFloat(str2, jsonElement.getAsFloat());
                    } else if (backupItemFindBackupItem.clazz.equals(String.class)) {
                        editorEdit.putString(str2, jsonElement.getAsString());
                    } else if (backupItemFindBackupItem.clazz.equals(Set.class)) {
                        HashSet hashSet = new HashSet();
                        Iterator<JsonElement> it = jsonElement.getAsJsonArray().iterator();
                        while (it.hasNext()) {
                            hashSet.add(it.next().getAsString());
                        }
                        editorEdit.putStringSet(str2, hashSet);
                    } else {
                        editorEdit.putInt(str2, jsonElement.getAsInt());
                    }
                    if ("exteraconfig".equals(str) && str2.equals("iconPack")) {
                        editorEdit.remove("iconPacksLayout");
                        editorEdit.remove("iconPacksHidden");
                    }
                }
            }
            editorEdit.apply();
        }
    }

    private JsonObject getConfigObject(JsonObject jsonObject, String str) {
        if (jsonObject.has(str) && jsonObject.get(str).isJsonObject()) {
            return jsonObject.getAsJsonObject(str);
        }
        if (!"mainconfig".equals(str)) {
            return null;
        }
        for (String str2 : jsonObject.keySet()) {
            if (str2.matches("^mainconfig\\d+$") && jsonObject.get(str2).isJsonObject()) {
                return jsonObject.getAsJsonObject(str2);
            }
        }
        return null;
    }

    public JsonObject getJsonObject(File file) {
        try {
            String andDecryptFile = readAndDecryptFile(file);
            if (andDecryptFile != null) {
                return (JsonObject) this.gson.fromJson(andDecryptFile, JsonObject.class);
            }
            return null;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    private String readAndDecryptFile(File file) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        try {
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = bufferedReader.readLine();
                if (line == null) {
                    break;
                }
                sb.append(line);
            }
            String string = sb.toString();
            if (InvisibleEncryptor.isEncrypted(string)) {
                string = InvisibleEncryptor.decode(string);
            }
            bufferedReader.close();
            return string;
        } catch (Throwable th) {
            try {
                bufferedReader.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    private boolean checkKeys(JsonObject jsonObject) {
        for (String str : configs) {
            JsonObject configObject = getConfigObject(jsonObject, str);
            if (configObject != null) {
                for (String str2 : configObject.keySet()) {
                    JsonElement jsonElement = configObject.get(str2);
                    if (isExpectedValue(str, str2, jsonElement)) {
                        return true;
                    }
                    FileLog.e("Unexpected value: " + str2 + " " + jsonElement);
                }
            }
        }
        return false;
    }

    public boolean isBackup(MessageObject messageObject) {
        String pathToMessage = ChatUtils.getInstance().getPathToMessage(messageObject);
        return (messageObject == null || messageObject.getDocumentName() == null || TextUtils.isEmpty(pathToMessage) || !isBackup(new File(pathToMessage))) ? false : true;
    }

    public boolean isBackup(File file) {
        JsonObject jsonObject;
        return file != null && file.getName().toLowerCase().endsWith(".extera") && (jsonObject = getJsonObject(file)) != null && checkKeys(jsonObject);
    }

    public int getDiff(File file) {
        return getDiff(getJsonObject(file));
    }

    public int getDiff(JsonObject jsonObject) {
        int i = 0;
        if (jsonObject == null) {
            return 0;
        }
        JsonObject jsonObject2 = (JsonObject) this.gson.fromJson(getBackup(false), JsonObject.class);
        for (String str : jsonObject.keySet()) {
            String str2 = str.matches("^mainconfig\\d+$") ? "mainconfig" : str;
            if (jsonObject.get(str).isJsonObject()) {
                JsonObject asJsonObject = jsonObject.getAsJsonObject(str);
                if (jsonObject2.has(str2)) {
                    JsonObject asJsonObject2 = jsonObject2.getAsJsonObject(str2);
                    for (String str3 : asJsonObject.keySet()) {
                        JsonElement jsonElement = asJsonObject.get(str3);
                        JsonElement jsonElement2 = asJsonObject2.get(str3);
                        if (!asJsonObject2.has(str3) || !jsonElement.equals(jsonElement2)) {
                            if (isExpectedValue(str2, str3, jsonElement)) {
                                i++;
                            }
                        }
                    }
                } else {
                    for (String str4 : asJsonObject.keySet()) {
                        if (isExpectedValue(str2, str4, asJsonObject.get(str4))) {
                            i++;
                        }
                    }
                }
            }
        }
        return i;
    }

    public static class BackupItem implements Serializable {
        public Class<?> clazz;
        public String key;

        public BackupItem(String str, Class<?> cls) {
            this.key = str;
            this.clazz = cls;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            return this.key.equals(((BackupItem) obj).key);
        }
    }
}
