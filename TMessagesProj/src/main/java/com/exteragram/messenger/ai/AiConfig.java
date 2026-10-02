package com.exteragram.messenger.ai;

import android.content.SharedPreferences;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.ai.data.Message;
import com.exteragram.messenger.ai.data.Role;
import com.exteragram.messenger.ai.data.Service;
import com.exteragram.messenger.ai.data.Suggestions;
import com.exteragram.messenger.backup.PreferencesUtils;
import com.exteragram.messenger.config.BasePref;
import com.exteragram.messenger.config.BooleanPref;
import com.exteragram.messenger.config.IntegerPref;
import com.exteragram.messenger.config.NullableStringPref;
import com.exteragram.messenger.config.StringPref;
import com.exteragram.messenger.plugins.PluginsConstants;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.List;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference0Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.UserConfig;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000R\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b,\n\u0002\u0010\u000e\n\u0002\b\u000f\u001a\u000f\u0010\u0001\u001a\u00020\u0000H\u0002¢\u0006\u0004\b\u0001\u0010\u0002\u001a\r\u0010\u0003\u001a\u00020\u0000¢\u0006\u0004\b\u0003\u0010\u0002\u001a\u0019\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u0007¢\u0006\u0004\b\u0007\u0010\b\u001a\u0015\u0010\n\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0006¢\u0006\u0004\b\n\u0010\u000b\u001a\u0019\u0010\f\u001a\u00020\u00062\b\b\u0002\u0010\u0005\u001a\u00020\u0004H\u0007¢\u0006\u0004\b\f\u0010\b\u001a\u0015\u0010\r\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u0006¢\u0006\u0004\b\r\u0010\u000b\u001a\r\u0010\u000f\u001a\u00020\u000e¢\u0006\u0004\b\u000f\u0010\u0010\u001a\u0015\u0010\u0012\u001a\u00020\u00002\u0006\u0010\u0011\u001a\u00020\u000e¢\u0006\u0004\b\u0012\u0010\u0013\u001a\r\u0010\u0014\u001a\u00020\u0000¢\u0006\u0004\b\u0014\u0010\u0002\u001a\u001d\u0010\u0017\u001a\u0012\u0012\u0004\u0012\u00020\u000e0\u0015j\b\u0012\u0004\u0012\u00020\u000e`\u0016¢\u0006\u0004\b\u0017\u0010\u0018\u001a%\u0010\u001a\u001a\u00020\u00002\u0016\u0010\u0019\u001a\u0012\u0012\u0004\u0012\u00020\u000e0\u0015j\b\u0012\u0004\u0012\u00020\u000e`\u0016¢\u0006\u0004\b\u001a\u0010\u001b\u001a%\u0010\u001e\u001a\u00020\u00002\u0016\u0010\u001d\u001a\u0012\u0012\u0004\u0012\u00020\u001c0\u0015j\b\u0012\u0004\u0012\u00020\u001c`\u0016¢\u0006\u0004\b\u001e\u0010\u001b\u001a\u001d\u0010\u001f\u001a\u0012\u0012\u0004\u0012\u00020\u001c0\u0015j\b\u0012\u0004\u0012\u00020\u001c`\u0016¢\u0006\u0004\b\u001f\u0010\u0018\u001a\u0015\u0010!\u001a\u00020\u00002\u0006\u0010 \u001a\u00020\u001c¢\u0006\u0004\b!\u0010\"\u001a\u001d\u0010$\u001a\u0012\u0012\u0004\u0012\u00020#0\u0015j\b\u0012\u0004\u0012\u00020#`\u0016¢\u0006\u0004\b$\u0010\u0018\u001a%\u0010&\u001a\u00020\u00002\u0016\u0010%\u001a\u0012\u0012\u0004\u0012\u00020#0\u0015j\b\u0012\u0004\u0012\u00020#`\u0016¢\u0006\u0004\b&\u0010\u001b\u001a\r\u0010'\u001a\u00020\u0000¢\u0006\u0004\b'\u0010\u0002\u001a\r\u0010(\u001a\u00020\u0000¢\u0006\u0004\b(\u0010\u0002\"\u0014\u0010)\u001a\u00020\u000e8\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b)\u0010*\"\u0014\u0010+\u001a\u00020\u000e8\u0006X\u0087\u0004¢\u0006\u0006\n\u0004\b+\u0010*\"\u0017\u0010-\u001a\u00020,8\u0006¢\u0006\f\n\u0004\b-\u0010.\u001a\u0004\b/\u00100\"\u001b\u00106\u001a\u0002018FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b2\u00103\u001a\u0004\b4\u00105\"\u0014\u00107\u001a\u00020\u00008\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b7\u00108\"+\u0010?\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u00068F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b:\u0010;\u001a\u0004\b<\u0010=\"\u0004\b>\u0010\u000b\"+\u0010C\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u00068F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b@\u0010;\u001a\u0004\bA\u0010=\"\u0004\bB\u0010\u000b\"+\u0010I\u001a\u00020\u00042\u0006\u00109\u001a\u00020\u00048F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bD\u0010;\u001a\u0004\bE\u0010F\"\u0004\bG\u0010H\"+\u0010M\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u00068F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bJ\u0010;\u001a\u0004\bK\u0010=\"\u0004\bL\u0010\u000b\"+\u0010Q\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u00068F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bN\u0010;\u001a\u0004\bO\u0010=\"\u0004\bP\u0010\u000b\"+\u0010U\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u00068F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bR\u0010;\u001a\u0004\bS\u0010=\"\u0004\bT\u0010\u000b\"+\u0010Y\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u00068F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bV\u0010;\u001a\u0004\bW\u0010=\"\u0004\bX\u0010\u000b\"+\u0010]\u001a\u00020\u00062\u0006\u00109\u001a\u00020\u00068F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bZ\u0010;\u001a\u0004\b[\u0010=\"\u0004\b\\\u0010\u000b\"/\u0010d\u001a\u0004\u0018\u00010^2\b\u00109\u001a\u0004\u0018\u00010^8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b_\u0010;\u001a\u0004\b`\u0010a\"\u0004\bb\u0010c\"+\u0010h\u001a\u00020\u00042\u0006\u00109\u001a\u00020\u00048B@BX\u0082\u008e\u0002¢\u0006\u0012\n\u0004\be\u0010;\u001a\u0004\bf\u0010F\"\u0004\bg\u0010H\"+\u0010l\u001a\u00020^2\u0006\u00109\u001a\u00020^8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bi\u0010;\u001a\u0004\bj\u0010a\"\u0004\bk\u0010c¨\u0006m"}, d2 = {_UrlKt.FRAGMENT_ENCODE_SET, "migrateLegacyConfig", "()V", "ensureConfigMigrated", _UrlKt.FRAGMENT_ENCODE_SET, "account", _UrlKt.FRAGMENT_ENCODE_SET, "getReplaceTelegramEditor", "(I)Z", "value", "setReplaceTelegramEditor", "(Z)V", "getReplaceTelegramSummaries", "setReplaceTelegramSummaries", "Lcom/exteragram/messenger/ai/data/Service;", "getSelectedService", "()Lcom/exteragram/messenger/ai/data/Service;", "service", "setSelectedServices", "(Lcom/exteragram/messenger/ai/data/Service;)V", "clearSelectedService", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "getServices", "()Ljava/util/ArrayList;", "services", "saveServices", "(Ljava/util/ArrayList;)V", "Lcom/exteragram/messenger/ai/data/Role;", "roles", "saveRoles", "getRoles", "role", "setSelectedAiRole", "(Lcom/exteragram/messenger/ai/data/Role;)V", "Lcom/exteragram/messenger/ai/data/Message;", "getConversationHistory", "history", "saveConversationHistory", "clearConversationHistory", "removeLastFromHistory", "DEFAULT_SERVICE", "Lcom/exteragram/messenger/ai/data/Service;", "ON_DEVICE_SERVICE", "Landroid/content/SharedPreferences;", "preferences", "Landroid/content/SharedPreferences;", "getPreferences", "()Landroid/content/SharedPreferences;", "Landroid/content/SharedPreferences$Editor;", "editor$delegate", "Lkotlin/Lazy;", "getEditor", "()Landroid/content/SharedPreferences$Editor;", "editor", "legacyConfigMigrated", "Lkotlin/Unit;", "<set-?>", "saveHistory$delegate", "Lcom/exteragram/messenger/config/BasePref;", "getSaveHistory", "()Z", "setSaveHistory", "saveHistory", "responseStreaming$delegate", "getResponseStreaming", "setResponseStreaming", "responseStreaming", "temperature$delegate", "getTemperature", "()I", "setTemperature", "(I)V", "temperature", "showResponseOnly$delegate", "getShowResponseOnly", "setShowResponseOnly", "showResponseOnly", "insertAsQuote$delegate", "getInsertAsQuote", "setInsertAsQuote", "insertAsQuote", "onDeviceReasoning$delegate", "getOnDeviceReasoning", "setOnDeviceReasoning", "onDeviceReasoning", "onDevicePreviewModel$delegate", "getOnDevicePreviewModel", "setOnDevicePreviewModel", "onDevicePreviewModel", "onDeviceFastModel$delegate", "getOnDeviceFastModel", "setOnDeviceFastModel", "onDeviceFastModel", _UrlKt.FRAGMENT_ENCODE_SET, "selectedServiceId$delegate", "getSelectedServiceId", "()Ljava/lang/String;", "setSelectedServiceId", "(Ljava/lang/String;)V", "selectedServiceId", "selectedServiceHash$delegate", "getSelectedServiceHash", "setSelectedServiceHash", "selectedServiceHash", "selectedRole$delegate", "getSelectedRole", "setSelectedRole", "selectedRole", "TMessagesProj"}, k = 2, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nAiConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 AiConfig.kt\ncom/exteragram/messenger/ai/AiConfig\n+ 2 SharedPreferences.kt\nandroidx/core/content/SharedPreferencesKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,217:1\n41#2,6:218\n47#2,6:226\n1915#3,2:224\n1915#3,2:233\n1#4:232\n*S KotlinDebug\n*F\n+ 1 AiConfig.kt\ncom/exteragram/messenger/ai/AiConfig\n*L\n65#1:218,6\n65#1:226,6\n66#1:224,2\n149#1:233,2\n*E\n"})
public abstract class AiConfig {
    static final /* synthetic */ KProperty<?>[] $$delegatedProperties;

    @JvmField
    public static final Service DEFAULT_SERVICE;

    @JvmField
    public static final Service ON_DEVICE_SERVICE;
    private static final Lazy editor$delegate;
    private static final BasePref insertAsQuote$delegate;
    private static final Unit legacyConfigMigrated;
    private static final BasePref onDeviceFastModel$delegate;
    private static final BasePref onDevicePreviewModel$delegate;
    private static final BasePref onDeviceReasoning$delegate;
    private static final SharedPreferences preferences;
    private static final BasePref responseStreaming$delegate;
    private static final BasePref saveHistory$delegate;
    private static final BasePref selectedRole$delegate;
    private static final BasePref selectedServiceHash$delegate;
    private static final BasePref selectedServiceId$delegate;
    private static final BasePref showResponseOnly$delegate;
    private static final BasePref temperature$delegate;

    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    public static final /* synthetic */ class EntriesMappings {
        public static final /* synthetic */ EnumEntries<Suggestions> entries$0 = EnumEntriesKt.enumEntries(Suggestions.values());
    }

    public static final void ensureConfigMigrated() {
    }

    @JvmOverloads
    public static final boolean getReplaceTelegramEditor() {
        return getReplaceTelegramEditor$default(0, 1, null);
    }

    @JvmOverloads
    public static final boolean getReplaceTelegramSummaries() {
        return getReplaceTelegramSummaries$default(0, 1, null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    static {
        boolean z = true;
        boolean z2 = false;
        int i = 2;
        KProperty<?>[] kPropertyArr = {Reflection.mutableProperty0(new MutablePropertyReference0Impl(AiConfig.class, "saveHistory", "getSaveHistory()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(AiConfig.class, "responseStreaming", "getResponseStreaming()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(AiConfig.class, "temperature", "getTemperature()I", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(AiConfig.class, "showResponseOnly", "getShowResponseOnly()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(AiConfig.class, "insertAsQuote", "getInsertAsQuote()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(AiConfig.class, "onDeviceReasoning", "getOnDeviceReasoning()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(AiConfig.class, "onDevicePreviewModel", "getOnDevicePreviewModel()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(AiConfig.class, "onDeviceFastModel", "getOnDeviceFastModel()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(AiConfig.class, "selectedServiceId", "getSelectedServiceId()Ljava/lang/String;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(AiConfig.class, "selectedServiceHash", "getSelectedServiceHash()I", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(AiConfig.class, "selectedRole", "getSelectedRole()Ljava/lang/String;", 1))};
        $$delegatedProperties = kPropertyArr;
        Service service = new Service(PluginsConstants.Settings.DEFAULT, "https://generativelanguage.googleapis.com/v1beta", "gemini-3.5-flash", (String) null);
        DEFAULT_SERVICE = service;
        Service service2 = new Service("on-device", (String) null, "Gemini Nano", (String) null);
        service2.setKind(Service.Kind.ON_DEVICE);
        ON_DEVICE_SERVICE = service2;
        preferences = PreferencesUtils.getPreferences("aiConfig");
        editor$delegate = LazyKt.lazy(() -> preferences.edit());
        migrateLegacyConfig();
        legacyConfigMigrated = Unit.INSTANCE;
        saveHistory$delegate = new BooleanPref(z, (String) null).provideDelegate(null, kPropertyArr[0]);
        responseStreaming$delegate = new BooleanPref(z, (String) null).provideDelegate(null, kPropertyArr[1]);
        temperature$delegate = new IntegerPref(10, (String) null).provideDelegate(null, kPropertyArr[2]);
        showResponseOnly$delegate = new BooleanPref(z2, (String) null).provideDelegate(null, kPropertyArr[3]);
        insertAsQuote$delegate = new BooleanPref(z, (String) null).provideDelegate(null, kPropertyArr[4]);
        onDeviceReasoning$delegate = new BooleanPref(z2, (String) null).provideDelegate(null, kPropertyArr[5]);
        onDevicePreviewModel$delegate = new BooleanPref(z2, (String) null).provideDelegate(null, kPropertyArr[6]);
        onDeviceFastModel$delegate = new BooleanPref(z2, (String) null).provideDelegate(null, kPropertyArr[7]);
        selectedServiceId$delegate = new NullableStringPref(null, "selectedServiceId").provideDelegate(null, kPropertyArr[8]);
        selectedServiceHash$delegate = new IntegerPref(service.getLegacyHash(), "selectedService").provideDelegate(null, kPropertyArr[9]);
        selectedRole$delegate = new StringPref(EntriesMappings.entries$0.get(0).getRole().getName(), (String) null).provideDelegate(null, kPropertyArr[10]);
    }

    public static final SharedPreferences getPreferences() {
        return preferences;
    }

    public static final SharedPreferences.Editor getEditor() {
        return (SharedPreferences.Editor) editor$delegate.getValue();
    }

    private static final void migrateLegacyConfig() {
        SharedPreferences preferences2 = PreferencesUtils.getPreferences("pillstackconfig");
        SharedPreferences.Editor editorEdit = preferences.edit();
        ArrayList arrayList = new ArrayList();
        String[] strArr = {"services", "roles", "history"};
        int i = 0;
        for (int i2 = 0; i2 < 3; i2++) {
            String str = strArr[i2];
            if (!preferences.contains(str)) {
                Object obj = preferences2.getAll().get(str);
                String str2 = obj instanceof String ? (String) obj : null;
                if (str2 != null) {
                    editorEdit.putString(str, str2);
                    arrayList.add(str);
                }
            }
        }
        if (arrayList.isEmpty() || !editorEdit.commit()) {
            return;
        }
        SharedPreferences.Editor editorEdit2 = preferences2.edit();
        int size = arrayList.size();
        while (i < size) {
            Object obj2 = arrayList.get(i);
            i++;
            editorEdit2.remove((String) obj2);
        }
        editorEdit2.apply();
    }

    public static final boolean getSaveHistory() {
        return ((Boolean) saveHistory$delegate.getValue(null, $$delegatedProperties[0])).booleanValue();
    }

    public static final void setSaveHistory(boolean z) {
        saveHistory$delegate.setValue(null, $$delegatedProperties[0], Boolean.valueOf(z));
    }

    public static final boolean getResponseStreaming() {
        return ((Boolean) responseStreaming$delegate.getValue(null, $$delegatedProperties[1])).booleanValue();
    }

    public static final void setResponseStreaming(boolean z) {
        responseStreaming$delegate.setValue(null, $$delegatedProperties[1], Boolean.valueOf(z));
    }

    public static final int getTemperature() {
        return ((Number) temperature$delegate.getValue(null, $$delegatedProperties[2])).intValue();
    }

    public static final void setTemperature(int i) {
        temperature$delegate.setValue(null, $$delegatedProperties[2], Integer.valueOf(i));
    }

    public static final boolean getShowResponseOnly() {
        return ((Boolean) showResponseOnly$delegate.getValue(null, $$delegatedProperties[3])).booleanValue();
    }

    public static final void setShowResponseOnly(boolean z) {
        showResponseOnly$delegate.setValue(null, $$delegatedProperties[3], Boolean.valueOf(z));
    }

    public static final boolean getInsertAsQuote() {
        return ((Boolean) insertAsQuote$delegate.getValue(null, $$delegatedProperties[4])).booleanValue();
    }

    public static final void setInsertAsQuote(boolean z) {
        insertAsQuote$delegate.setValue(null, $$delegatedProperties[4], Boolean.valueOf(z));
    }

    public static final boolean getOnDeviceReasoning() {
        return ((Boolean) onDeviceReasoning$delegate.getValue(null, $$delegatedProperties[5])).booleanValue();
    }

    public static final void setOnDeviceReasoning(boolean z) {
        onDeviceReasoning$delegate.setValue(null, $$delegatedProperties[5], Boolean.valueOf(z));
    }

    public static final boolean getOnDevicePreviewModel() {
        return ((Boolean) onDevicePreviewModel$delegate.getValue(null, $$delegatedProperties[6])).booleanValue();
    }

    public static final void setOnDevicePreviewModel(boolean z) {
        onDevicePreviewModel$delegate.setValue(null, $$delegatedProperties[6], Boolean.valueOf(z));
    }

    public static final boolean getOnDeviceFastModel() {
        return ((Boolean) onDeviceFastModel$delegate.getValue(null, $$delegatedProperties[7])).booleanValue();
    }

    public static final void setOnDeviceFastModel(boolean z) {
        onDeviceFastModel$delegate.setValue(null, $$delegatedProperties[7], Boolean.valueOf(z));
    }

    public static /* synthetic */ boolean getReplaceTelegramEditor$default(int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = UserConfig.selectedAccount;
        }
        return getReplaceTelegramEditor(i);
    }

    @JvmOverloads
    public static final boolean getReplaceTelegramEditor(int i) {
        return ExteraConfig.getPreferences().getBoolean("replaceTelegramEditor", !UserConfig.getInstance(i).isPremium());
    }

    public static final void setReplaceTelegramEditor(boolean z) {
        ExteraConfig.getEditor().putBoolean("replaceTelegramEditor", z).apply();
    }

    public static /* synthetic */ boolean getReplaceTelegramSummaries$default(int i, int i2, Object obj) {
        if ((i2 & 1) != 0) {
            i = UserConfig.selectedAccount;
        }
        return getReplaceTelegramSummaries(i);
    }

    @JvmOverloads
    public static final boolean getReplaceTelegramSummaries(int i) {
        return ExteraConfig.getPreferences().getBoolean("replaceTelegramSummaries", !UserConfig.getInstance(i).isPremium());
    }

    public static final void setReplaceTelegramSummaries(boolean z) {
        ExteraConfig.getEditor().putBoolean("replaceTelegramSummaries", z).apply();
    }

    public static final String getSelectedServiceId() {
        return (String) selectedServiceId$delegate.getValue(null, $$delegatedProperties[8]);
    }

    public static final void setSelectedServiceId(String str) {
        selectedServiceId$delegate.setValue(null, $$delegatedProperties[8], str);
    }

    private static final int getSelectedServiceHash() {
        return ((Number) selectedServiceHash$delegate.getValue(null, $$delegatedProperties[9])).intValue();
    }

    public static final String getSelectedRole() {
        return (String) selectedRole$delegate.getValue(null, $$delegatedProperties[10]);
    }

    public static final void setSelectedRole(String str) {
        selectedRole$delegate.setValue(null, $$delegatedProperties[10], str);
    }

    public static final Service getSelectedService() {
        Service service;
        ArrayList<Service> services = getServices();
        String selectedServiceId = getSelectedServiceId();
        int i = 0;
        Service service2 = null;
        if (selectedServiceId != null && selectedServiceId.length() != 0) {
            int size = services.size();
            int i2 = 0;
            do {
                if (i2 >= size) {
                    service = null;
                    break;
                }
                service = services.get(i2);
                i2++;
            } while (!Intrinsics.areEqual(service.getId(), selectedServiceId));
            Service service3 = service;
            if (service3 != null) {
                return service3;
            }
            setSelectedServiceId(null);
        }
        if (ExteraConfig.getPreferences().contains("selectedService")) {
            int selectedServiceHash = getSelectedServiceHash();
            int size2 = services.size();
            while (i < size2) {
                Service service4 = services.get(i);
                i++;
                if (service4.getLegacyHash() == selectedServiceHash) {
                    service2 = service4;
                    break;
                }
            }
            Service service5 = service2;
            if (service5 != null) {
                setSelectedServiceId(service5.getId());
                ExteraConfig.getEditor().remove("selectedService").apply();
                return service5;
            }
            ExteraConfig.getEditor().remove("selectedService").apply();
        }
        Service service6 = (Service) CollectionsKt.firstOrNull((List) services);
        return service6 == null ? DEFAULT_SERVICE : service6;
    }

    public static final void setSelectedServices(Service service) {
        setSelectedServiceId(service.getId());
        ExteraConfig.getEditor().remove("selectedService").apply();
    }

    public static final void clearSelectedService() {
        setSelectedServiceId(null);
        ExteraConfig.getEditor().remove("selectedService").apply();
    }

    public static final ArrayList<Service> getServices() {
        String string = preferences.getString("services", null);
        if (string == null) {
            return new ArrayList<>();
        }
        try {
            ArrayList<Service> arrayList = (ArrayList) ExteraConfig.getGSON().fromJson(string, new TypeToken<ArrayList<Service>>() { // from class: com.exteragram.messenger.ai.AiConfig$getServices$type$1
            }.getType());
            if (arrayList == null) {
                arrayList = new ArrayList<>();
            }
            int size = arrayList.size();
            boolean z = false;
            int i = 0;
            while (i < size) {
                Service service = arrayList.get(i);
                i++;
                if (service.ensureId()) {
                    z = true;
                }
            }
            if (z) {
                saveServices(arrayList);
            }
            return arrayList;
        } catch (Exception e) {
            FileLog.e(e);
            return new ArrayList<>();
        }
    }

    public static final void saveServices(ArrayList<Service> arrayList) {
        getEditor().putString("services", ExteraConfig.getGSON().toJson(arrayList)).apply();
    }

    public static final void saveRoles(ArrayList<Role> arrayList) {
        getEditor().putString("roles", ExteraConfig.getGSON().toJson(arrayList)).apply();
    }

    public static final ArrayList<Role> getRoles() {
        String string = preferences.getString("roles", null);
        if (string == null) {
            return new ArrayList<>();
        }
        try {
            ArrayList<Role> arrayList = (ArrayList) ExteraConfig.getGSON().fromJson(string, new TypeToken<ArrayList<Role>>() { // from class: com.exteragram.messenger.ai.AiConfig$getRoles$type$1
            }.getType());
            return arrayList == null ? new ArrayList<>() : arrayList;
        } catch (Exception e) {
            FileLog.e(e);
            return new ArrayList<>();
        }
    }

    public static final void setSelectedAiRole(Role role) {
        setSelectedRole(role.getName());
    }

    public static final ArrayList<Message> getConversationHistory() {
        String string = preferences.getString("history", null);
        if (string == null) {
            return new ArrayList<>();
        }
        try {
            ArrayList<Message> arrayList = (ArrayList) ExteraConfig.getGSON().fromJson(string, new TypeToken<ArrayList<Message>>() { // from class: com.exteragram.messenger.ai.AiConfig$getConversationHistory$type$1
            }.getType());
            return arrayList == null ? new ArrayList<>() : arrayList;
        } catch (Exception e) {
            FileLog.e(e);
            return new ArrayList<>();
        }
    }

    public static final void saveConversationHistory(ArrayList<Message> arrayList) {
        getEditor().putString("history", ExteraConfig.getGSON().toJson(arrayList)).apply();
    }

    public static final void clearConversationHistory() {
        getEditor().remove("history").apply();
    }

    public static final void removeLastFromHistory() {
        ArrayList<Message> conversationHistory = getConversationHistory();
        if (conversationHistory.size() >= 2) {
            conversationHistory.remove(conversationHistory.size() - 1);
            conversationHistory.remove(conversationHistory.size() - 1);
            saveConversationHistory(conversationHistory);
        }
    }
}
