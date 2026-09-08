package com.exteragram.messenger.pillstack.core;

import android.content.SharedPreferences;
import com.exteragram.messenger.backup.PreferencesUtils;
import com.exteragram.messenger.config.BasePref;
import com.exteragram.messenger.config.BooleanPref;
import com.exteragram.messenger.config.IntegerPref;
import com.exteragram.messenger.config.NullableStringPref;
import com.exteragram.messenger.config.StringPref;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.MutablePropertyReference0Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.NotificationCenter;

@Metadata(d1 = {"\u0000b\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\u0015\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b1\n\u0002\u0010!\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0006\u001a)\u0010\u0005\u001a\u0012\u0012\u0004\u0012\u00020\u00030\u0002j\b\u0012\u0004\u0012\u00020\u0003`\u00042\b\u0010\u0001\u001a\u0004\u0018\u00010\u0000H\u0002¢\u0006\u0004\b\u0005\u0010\u0006\u001a\u001d\u0010\t\u001a\u00020\u00002\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00030\u0007H\u0002¢\u0006\u0004\b\t\u0010\n\u001a\u001d\u0010\u000b\u001a\u0012\u0012\u0004\u0012\u00020\u00030\u0002j\b\u0012\u0004\u0012\u00020\u0003`\u0004¢\u0006\u0004\b\u000b\u0010\f\u001a\r\u0010\u000e\u001a\u00020\r¢\u0006\u0004\b\u000e\u0010\u000f\u001a\r\u0010\u0010\u001a\u00020\r¢\u0006\u0004\b\u0010\u0010\u000f\u001a\r\u0010\u0011\u001a\u00020\r¢\u0006\u0004\b\u0011\u0010\u000f\u001a\r\u0010\u0012\u001a\u00020\r¢\u0006\u0004\b\u0012\u0010\u000f\u001a\u0015\u0010\u0014\u001a\u00020\r2\u0006\u0010\u0013\u001a\u00020\u0003¢\u0006\u0004\b\u0014\u0010\u0015\u001a\u0019\u0010\u0018\u001a\u00020\r2\n\u0010\u0017\u001a\u00020\u0016\"\u00020\u0003¢\u0006\u0004\b\u0018\u0010\u0019\u001a\u0015\u0010\u001c\u001a\u00020\u001b2\u0006\u0010\u001a\u001a\u00020\u0003¢\u0006\u0004\b\u001c\u0010\u001d\u001a+\u0010!\u001a\u00020\u001b2\u0010\u0010 \u001a\f\u0012\u0006\b\u0001\u0012\u00020\u001f\u0018\u00010\u001e2\n\u0010\u0017\u001a\u00020\u0016\"\u00020\u0003¢\u0006\u0004\b!\u0010\"\"\u0017\u0010$\u001a\u00020#8\u0006¢\u0006\f\n\u0004\b$\u0010%\u001a\u0004\b&\u0010'\"\u0017\u0010)\u001a\u00020(8\u0006¢\u0006\f\n\u0004\b)\u0010*\u001a\u0004\b+\u0010,\"\u0014\u0010-\u001a\u00020\u001f8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b-\u0010.\"\"\u0010/\u001a\u00020\u001b8\u0006@\u0006X\u0086\u000e¢\u0006\u0012\n\u0004\b/\u00100\u001a\u0004\b1\u00102\"\u0004\b3\u00104\"+\u0010:\u001a\u00020\u001b2\u0006\u00105\u001a\u00020\u001b8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b6\u00107\u001a\u0004\b8\u00102\"\u0004\b9\u00104\"/\u0010@\u001a\u0004\u0018\u00010\u00002\b\u00105\u001a\u0004\u0018\u00010\u00008F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b;\u00107\u001a\u0004\b<\u0010=\"\u0004\b>\u0010?\"/\u0010D\u001a\u0004\u0018\u00010\u00002\b\u00105\u001a\u0004\u0018\u00010\u00008F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bA\u00107\u001a\u0004\bB\u0010=\"\u0004\bC\u0010?\"+\u0010H\u001a\u00020\u001b2\u0006\u00105\u001a\u00020\u001b8F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bE\u00107\u001a\u0004\bF\u00102\"\u0004\bG\u00104\"+\u0010L\u001a\u00020\u00002\u0006\u00105\u001a\u00020\u00008F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bI\u00107\u001a\u0004\bJ\u0010=\"\u0004\bK\u0010?\"+\u0010P\u001a\u00020\u00002\u0006\u00105\u001a\u00020\u00008F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bM\u00107\u001a\u0004\bN\u0010=\"\u0004\bO\u0010?\"+\u0010T\u001a\u00020\u00002\u0006\u00105\u001a\u00020\u00008F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bQ\u00107\u001a\u0004\bR\u0010=\"\u0004\bS\u0010?\"+\u0010Y\u001a\u00020\u00032\u0006\u00105\u001a\u00020\u00038F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\bU\u00107\u001a\u0004\bV\u0010W\"\u0004\bX\u0010\u0015\"(\u0010[\u001a\b\u0012\u0004\u0012\u00020\u00030Z8\u0006@\u0006X\u0086\u000e¢\u0006\u0012\n\u0004\b[\u0010\\\u001a\u0004\b]\u0010^\"\u0004\b_\u0010`\"(\u0010a\u001a\b\u0012\u0004\u0012\u00020\u00030Z8\u0006@\u0006X\u0086\u000e¢\u0006\u0012\n\u0004\ba\u0010\\\u001a\u0004\bb\u0010^\"\u0004\bc\u0010`\"\u001a\u0010e\u001a\b\u0012\u0004\u0012\u00020\u00030d8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\be\u0010f\"\u001a\u0010g\u001a\u00020\r8\u0002X\u0082\u0004¢\u0006\f\n\u0004\bg\u0010h\u0012\u0004\bi\u0010\u000f¨\u0006j"}, d2 = {_UrlKt.FRAGMENT_ENCODE_SET, "data", "Ljava/util/ArrayList;", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlin/collections/ArrayList;", "parsePillsList", "(Ljava/lang/String;)Ljava/util/ArrayList;", _UrlKt.FRAGMENT_ENCODE_SET, "list", "serializePillsList", "(Ljava/util/List;)Ljava/lang/String;", "getDefaultActivePills", "()Ljava/util/ArrayList;", _UrlKt.FRAGMENT_ENCODE_SET, "loadConfig", "()V", "reloadConfig", "sanitizePills", "savePillsLayout", "id", "saveLastActivePillId", "(I)V", _UrlKt.FRAGMENT_ENCODE_SET, "pillIds", "notifySettingsChanged", "([I)V", "pillId", _UrlKt.FRAGMENT_ENCODE_SET, "checkAndClearPendingUpdate", "(I)Z", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "args", "shouldUpdatePill", "([Ljava/lang/Object;[I)Z", "Landroid/content/SharedPreferences;", "preferences", "Landroid/content/SharedPreferences;", "getPreferences", "()Landroid/content/SharedPreferences;", "Landroid/content/SharedPreferences$Editor;", "editor", "Landroid/content/SharedPreferences$Editor;", "getEditor", "()Landroid/content/SharedPreferences$Editor;", "sync", "Ljava/lang/Object;", "configLoaded", "Z", "getConfigLoaded", "()Z", "setConfigLoaded", "(Z)V", "<set-?>", "useCurrentLocation$delegate", "Lcom/exteragram/messenger/config/BasePref;", "getUseCurrentLocation", "setUseCurrentLocation", "useCurrentLocation", "customWeatherLocation$delegate", "getCustomWeatherLocation", "()Ljava/lang/String;", "setCustomWeatherLocation", "(Ljava/lang/String;)V", "customWeatherLocation", "customWeatherAddress$delegate", "getCustomWeatherAddress", "setCustomWeatherAddress", "customWeatherAddress", "infiniteScrolling$delegate", "getInfiniteScrolling", "setInfiniteScrolling", "infiniteScrolling", "gramTargetCurrency$delegate", "getGramTargetCurrency", "setGramTargetCurrency", "gramTargetCurrency", "btcTargetCurrency$delegate", "getBtcTargetCurrency", "setBtcTargetCurrency", "btcTargetCurrency", "usdTargetCurrency$delegate", "getUsdTargetCurrency", "setUsdTargetCurrency", "usdTargetCurrency", "lastActivePillId$delegate", "getLastActivePillId", "()I", "setLastActivePillId", "lastActivePillId", _UrlKt.FRAGMENT_ENCODE_SET, "activePills", "Ljava/util/List;", "getActivePills", "()Ljava/util/List;", "setActivePills", "(Ljava/util/List;)V", "hiddenPills", "getHiddenPills", "setHiddenPills", "Ljava/util/HashSet;", "pendingUpdates", "Ljava/util/HashSet;", "init", "Lkotlin/Unit;", "getInit$annotations", "TMessagesProj"}, k = 2, mv = {2, 2, 0}, xi = 48)
@JvmName(name = "PillStackConfig")
@SourceDebugExtension({"SMAP\nPillStackConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PillStackConfig.kt\ncom/exteragram/messenger/pillstack/core/PillStackConfig\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,177:1\n1915#2:178\n1916#2:180\n1#3:179\n14069#4,2:181\n*S KotlinDebug\n*F\n+ 1 PillStackConfig.kt\ncom/exteragram/messenger/pillstack/core/PillStackConfig\n*L\n66#1:178\n66#1:180\n150#1:181,2\n*E\n"})
public abstract class PillStackConfig {
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties;
    private static List<Integer> activePills;
    private static final BasePref btcTargetCurrency$delegate;
    private static boolean configLoaded;
    private static final BasePref customWeatherAddress$delegate;
    private static final BasePref customWeatherLocation$delegate;
    private static final SharedPreferences.Editor editor;
    private static final BasePref gramTargetCurrency$delegate;
    private static List<Integer> hiddenPills;
    private static final BasePref infiniteScrolling$delegate;
    private static final Unit init;
    private static final BasePref lastActivePillId$delegate;
    private static final HashSet<Integer> pendingUpdates;
    private static final SharedPreferences preferences;
    private static final Object sync;
    private static final BasePref usdTargetCurrency$delegate;
    private static final BasePref useCurrentLocation$delegate;

    static {
        KProperty<?>[] kPropertyArr = {Reflection.mutableProperty0(new MutablePropertyReference0Impl(PillStackConfig.class, "useCurrentLocation", "getUseCurrentLocation()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(PillStackConfig.class, "customWeatherLocation", "getCustomWeatherLocation()Ljava/lang/String;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(PillStackConfig.class, "customWeatherAddress", "getCustomWeatherAddress()Ljava/lang/String;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(PillStackConfig.class, "infiniteScrolling", "getInfiniteScrolling()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(PillStackConfig.class, "gramTargetCurrency", "getGramTargetCurrency()Ljava/lang/String;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(PillStackConfig.class, "btcTargetCurrency", "getBtcTargetCurrency()Ljava/lang/String;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(PillStackConfig.class, "usdTargetCurrency", "getUsdTargetCurrency()Ljava/lang/String;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(PillStackConfig.class, "lastActivePillId", "getLastActivePillId()I", 1))};
        $$delegatedProperties = kPropertyArr;
        SharedPreferences preferences2 = PreferencesUtils.getPreferences("pillstackconfig");
        preferences = preferences2;
        editor = preferences2.edit();
        sync = new Object();
        useCurrentLocation$delegate = new BooleanPref(true, null, 2, null).provideDelegate(null, kPropertyArr[0]);
        customWeatherLocation$delegate = new NullableStringPref(null, null, 2, null).provideDelegate(null, kPropertyArr[1]);
        customWeatherAddress$delegate = new NullableStringPref(null, null, 2, null).provideDelegate(null, kPropertyArr[2]);
        infiniteScrolling$delegate = new BooleanPref(true, null, 2, null).provideDelegate(null, kPropertyArr[3]);
        gramTargetCurrency$delegate = new StringPref("AUTO", null, 2, null).provideDelegate(null, kPropertyArr[4]);
        btcTargetCurrency$delegate = new StringPref("AUTO", null, 2, null).provideDelegate(null, kPropertyArr[5]);
        usdTargetCurrency$delegate = new StringPref("AUTO", null, 2, null).provideDelegate(null, kPropertyArr[6]);
        lastActivePillId$delegate = new IntegerPref(-1, "lastActivePillId").provideDelegate(null, kPropertyArr[7]);
        activePills = new ArrayList();
        hiddenPills = new ArrayList();
        pendingUpdates = new HashSet<>();
        loadConfig();
        init = Unit.INSTANCE;
    }

    public static final SharedPreferences getPreferences() {
        return preferences;
    }

    public static final SharedPreferences.Editor getEditor() {
        return editor;
    }

    public static final boolean getConfigLoaded() {
        return configLoaded;
    }

    public static final boolean getUseCurrentLocation() {
        return ((Boolean) useCurrentLocation$delegate.getValue(null, $$delegatedProperties[0])).booleanValue();
    }

    public static final void setUseCurrentLocation(boolean z) {
        useCurrentLocation$delegate.setValue(null, $$delegatedProperties[0], Boolean.valueOf(z));
    }

    public static final String getCustomWeatherLocation() {
        return (String) customWeatherLocation$delegate.getValue(null, $$delegatedProperties[1]);
    }

    public static final void setCustomWeatherLocation(String str) {
        customWeatherLocation$delegate.setValue(null, $$delegatedProperties[1], str);
    }

    public static final String getCustomWeatherAddress() {
        return (String) customWeatherAddress$delegate.getValue(null, $$delegatedProperties[2]);
    }

    public static final void setCustomWeatherAddress(String str) {
        customWeatherAddress$delegate.setValue(null, $$delegatedProperties[2], str);
    }

    public static final boolean getInfiniteScrolling() {
        return ((Boolean) infiniteScrolling$delegate.getValue(null, $$delegatedProperties[3])).booleanValue();
    }

    public static final void setInfiniteScrolling(boolean z) {
        infiniteScrolling$delegate.setValue(null, $$delegatedProperties[3], Boolean.valueOf(z));
    }

    public static final String getGramTargetCurrency() {
        return (String) gramTargetCurrency$delegate.getValue(null, $$delegatedProperties[4]);
    }

    public static final void setGramTargetCurrency(String str) {
        gramTargetCurrency$delegate.setValue(null, $$delegatedProperties[4], str);
    }

    public static final String getBtcTargetCurrency() {
        return (String) btcTargetCurrency$delegate.getValue(null, $$delegatedProperties[5]);
    }

    public static final void setBtcTargetCurrency(String str) {
        btcTargetCurrency$delegate.setValue(null, $$delegatedProperties[5], str);
    }

    public static final String getUsdTargetCurrency() {
        return (String) usdTargetCurrency$delegate.getValue(null, $$delegatedProperties[6]);
    }

    public static final void setUsdTargetCurrency(String str) {
        usdTargetCurrency$delegate.setValue(null, $$delegatedProperties[6], str);
    }

    public static final int getLastActivePillId() {
        return ((Number) lastActivePillId$delegate.getValue(null, $$delegatedProperties[7])).intValue();
    }

    public static final void setLastActivePillId(int i) {
        lastActivePillId$delegate.setValue(null, $$delegatedProperties[7], Integer.valueOf(i));
    }

    public static final List<Integer> getActivePills() {
        return activePills;
    }

    public static final List<Integer> getHiddenPills() {
        return hiddenPills;
    }

    private static final ArrayList<Integer> parsePillsList(String str) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        if (str != null && str.length() != 0) {
            if (StringsKt.startsWith$default(str, "[", false, 2, (Object) null)) {
                str = new Regex("[\\[\\]\"]").replace(str, _UrlKt.FRAGMENT_ENCODE_SET);
            }
            Iterator it = StringsKt.split$default((CharSequence) str, new String[]{","}, false, 0, 6, (Object) null).iterator();
            while (it.hasNext()) {
                Integer intOrNull = StringsKt.toIntOrNull(StringsKt.trim((CharSequence) it.next()).toString());
                if (intOrNull != null) {
                    arrayList.add(Integer.valueOf(intOrNull.intValue()));
                }
            }
        }
        return arrayList;
    }

    private static final String serializePillsList(List<Integer> list) {
        return list.isEmpty() ? _UrlKt.FRAGMENT_ENCODE_SET : CollectionsKt.joinToString$default(list, ",", null, null, 0, null, null, 62, null);
    }

    public static final ArrayList<Integer> getDefaultActivePills() {
        return new ArrayList<>();
    }

    public static final void loadConfig() {
        List<Integer> arrayList;
        synchronized (sync) {
            try {
                if (configLoaded) {
                    return;
                }
                SharedPreferences sharedPreferences = preferences;
                String string = sharedPreferences.getString("activePills", null);
                String string2 = sharedPreferences.getString("hiddenPills", null);
                if (string != null) {
                    activePills = CollectionsKt.toMutableList((Collection) parsePillsList(string));
                    if (string2 != null) {
                        arrayList = CollectionsKt.toMutableList((Collection) parsePillsList(string2));
                    } else {
                        arrayList = new ArrayList<>();
                    }
                    hiddenPills = arrayList;
                } else {
                    activePills = CollectionsKt.toMutableList((Collection) getDefaultActivePills());
                    hiddenPills = new ArrayList();
                    for (PillRegistry.PillInfo pillInfo : PillRegistry.getRegisteredPills()) {
                        if (!activePills.contains(Integer.valueOf(pillInfo.id()))) {
                            hiddenPills.add(Integer.valueOf(pillInfo.id()));
                        }
                    }
                    savePillsLayout();
                }
                sanitizePills();
                configLoaded = true;
                Unit unit = Unit.INSTANCE;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static final void reloadConfig() {
        synchronized (sync) {
            configLoaded = false;
            loadConfig();
            Unit unit = Unit.INSTANCE;
        }
    }

    public static boolean $r8$lambda$XnIFmMud_pPNgK35OdVOBTqEozY(int i) {
        return !PillRegistry.isRegistered(i);
    }

    public static final void sanitizePills() {
        boolean z = CollectionsKt.removeAll((List) activePills, new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(PillStackConfig.$r8$lambda$XnIFmMud_pPNgK35OdVOBTqEozY(((Integer) obj).intValue()));
            }
        }) || CollectionsKt.removeAll((List) hiddenPills, new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(PillStackConfig.m1276$r8$lambda$5gYaUoAnQKlUoXWkxCTgILMvsk(((Integer) obj).intValue()));
            }
        });
        for (PillRegistry.PillInfo pillInfo : PillRegistry.getRegisteredPills()) {
            if (!activePills.contains(Integer.valueOf(pillInfo.id())) && !hiddenPills.contains(Integer.valueOf(pillInfo.id()))) {
                hiddenPills.add(Integer.valueOf(pillInfo.id()));
                z = true;
            }
        }
        if (z) {
            savePillsLayout();
        }
    }

    public static boolean m1276$r8$lambda$5gYaUoAnQKlUoXWkxCTgILMvsk(int i) {
        return !PillRegistry.isRegistered(i);
    }

    public static final void savePillsLayout() {
        editor.putString("activePills", serializePillsList(activePills)).putString("hiddenPills", serializePillsList(hiddenPills)).apply();
    }

    public static final void saveLastActivePillId(int i) {
        setLastActivePillId(i);
        editor.putInt("lastActivePillId", i).apply();
    }

    public static final void notifySettingsChanged(int... iArr) {
        if (iArr.length == 0) {
            Iterator<PillRegistry.PillInfo> it = PillRegistry.getRegisteredPills().iterator();
            while (it.hasNext()) {
                pendingUpdates.add(Integer.valueOf(it.next().id()));
            }
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pillStackSettingsChanged, new Object[0]);
            return;
        }
        for (int i : iArr) {
            pendingUpdates.add(Integer.valueOf(i));
        }
        Integer[] typedArray = ArraysKt.toTypedArray(iArr);
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pillStackSettingsChanged, Arrays.copyOf(typedArray, typedArray.length));
    }

    public static final boolean checkAndClearPendingUpdate(int i) {
        return pendingUpdates.remove(Integer.valueOf(i));
    }

    public static final boolean shouldUpdatePill(Object[] objArr, int... iArr) {
        if (objArr == null || objArr.length == 0 || iArr.length == 0) {
            return true;
        }
        for (Object obj : objArr) {
            if ((obj instanceof Integer) && ArraysKt.contains(iArr, ((Number) obj).intValue())) {
                return true;
            }
        }
        return false;
    }
}
