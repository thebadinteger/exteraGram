package com.exteragram.messenger.utils.network;

import android.content.SharedPreferences;
import com.exteragram.messenger.utils.AppUtils;
import com.exteragram.messenger.utils.chats.ChatUtils;
import java.util.concurrent.TimeUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public abstract class RemoteUtils {
    private static final long CONFIG_REFRESH_INTERVAL = TimeUnit.MINUTES.toMillis(10);
    private static final Object messagesRequestLock = new Object();
    private static ArrayList<Utilities.Callback2<TLRPC.messages_Messages, TLRPC.TL_error>> pendingMessagesCallbacks;
    public static SharedPreferences sharedPreferences;

    public static void initCached() {
        if (sharedPreferences == null) {
            sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("exteraremoteconfig", 0);
        }
    }

    public static void init() {
        initCached();
        long jCurrentTimeMillis = System.currentTimeMillis();
        if (Math.abs(jCurrentTimeMillis - sharedPreferences.getLong("__last_fetch_attempt_time", 0L)) < CONFIG_REFRESH_INTERVAL) {
            return;
        }
        sharedPreferences.edit().putLong("__last_fetch_attempt_time", jCurrentTimeMillis).apply();
        loadConfig();
    }

    public static void forceRefresh() {
        initCached();
        sharedPreferences.edit().putLong("__last_fetch_attempt_time", System.currentTimeMillis()).apply();
        loadConfig();
    }

    private static void loadConfig() {
        getMessages(new Utilities.Callback2<TLRPC.messages_Messages, TLRPC.TL_error>() {
            @Override
            public final void run(TLRPC.messages_Messages obj, TLRPC.TL_error obj2) {
                RemoteUtils.$r8$lambda$LmfFRuJdEdEVAug9vbi59pl5km0(obj, obj2);
            }
        });
    }

    private static SharedPreferences getPrefs() {
        if (sharedPreferences == null) {
            initCached();
        }
        return sharedPreferences;
    }

    public static void $r8$lambda$LmfFRuJdEdEVAug9vbi59pl5km0(TLRPC.messages_Messages messages_messages, TLRPC.TL_error tL_error) {
        if (tL_error != null || messages_messages == null) {
            return;
        }
        HashSet hashSet = new HashSet();
        ArrayList<TLRPC.Message> arrayList = messages_messages.messages;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            TLRPC.Message message = arrayList.get(i);
            i++;
            TLRPC.Message message2 = message;
            if ((message2 instanceof TLRPC.TL_message) && message2.message.startsWith("remote_config")) {
                String[] strArrSplit = message2.message.split("\n");
                if (strArrSplit.length > 1) {
                    for (String str : strArrSplit) {
                        String[] strArrSplit2 = str.split("=", 2);
                        if (strArrSplit2.length == 2) {
                            String strTrim = strArrSplit2[0].trim();
                            String strTrim2 = strArrSplit2[1].trim();
                            if (!strTrim2.equals("null")) {
                                updateValue(strTrim, strTrim2);
                                hashSet.add(strTrim);
                            }
                        }
                    }
                }
            }
        }
        removeOldPreferences(hashSet);
    }

    private static void removeOldPreferences(Set<String> set) {
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        for (String str : sharedPreferences.getAll().keySet()) {
            if (!set.contains(str) && !"__last_fetch_attempt_time".equals(str)) {
                editorEdit.remove(str);
            }
        }
        editorEdit.apply();
    }

    private static void updateValue(String str, String str2) {
        if (areValuesEqual(sharedPreferences.getAll().get(str), parseConfigValue(str2))) {
            return;
        }
        saveToPreferences(str, str2);
    }

    private static boolean areValuesEqual(Object obj, Object obj2) {
        if (obj == null || obj2 == null) {
            return obj == obj2;
        }
        return obj.equals(obj2);
    }

    private static void saveToPreferences(String str, String str2) {
        SharedPreferences.Editor editorEdit = sharedPreferences.edit();
        saveConfigValueToPreferences(editorEdit, str, parseConfigValue(str2));
        editorEdit.apply();
    }

    private static Object parseConfigValue(String str) {
        if (str.matches("-?\\d+")) {
            try {
                return Long.valueOf(Long.parseLong(str));
            } catch (NumberFormatException unused) {
                return Float.valueOf(Float.parseFloat(str));
            }
        }
        if (str.matches("-?\\d+(\\.\\d+)")) {
            return Float.valueOf(Float.parseFloat(str));
        }
        if (str.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (str.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (!str.startsWith("[") || !str.endsWith("]")) {
            return str;
        }
        String strSubstring = str.substring(1, str.length() - 1);
        if (strSubstring.isEmpty()) {
            return new HashSet();
        }
        return new HashSet(Arrays.asList(strSubstring.split(",\\s*")));
    }

    private static void saveConfigValueToPreferences(SharedPreferences.Editor editor, String str, Object obj) {
        if (obj instanceof Long) {
            editor.putLong(str, ((Long) obj).longValue());
            return;
        }
        if (obj instanceof Float) {
            editor.putFloat(str, ((Float) obj).floatValue());
            return;
        }
        if (obj instanceof Boolean) {
            editor.putBoolean(str, ((Boolean) obj).booleanValue());
        } else if (obj instanceof Set) {
            editor.putStringSet(str, (Set) obj);
        } else if (obj instanceof String) {
            editor.putString(str, (String) obj);
        }
    }

    public static void getMessages(Utilities.Callback2<TLRPC.messages_Messages, TLRPC.TL_error> callback2) {
        synchronized (messagesRequestLock) {
            try {
                ArrayList<Utilities.Callback2<TLRPC.messages_Messages, TLRPC.TL_error>> arrayList = pendingMessagesCallbacks;
                if (arrayList != null) {
                    arrayList.add(callback2);
                    return;
                }
                ArrayList<Utilities.Callback2<TLRPC.messages_Messages, TLRPC.TL_error>> arrayList2 = new ArrayList<>();
                pendingMessagesCallbacks = arrayList2;
                arrayList2.add(callback2);
                final AccountInstance accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
                final TLRPC.TL_messages_getHistory tL_messages_getHistory = new TLRPC.TL_messages_getHistory();
                tL_messages_getHistory.peer = accountInstance.getMessagesController().getInputPeer(-2227431611L);
                tL_messages_getHistory.offset_id = 0;
                tL_messages_getHistory.limit = 75;
                final Runnable runnable = new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        accountInstance.getConnectionsManager().sendRequest(tL_messages_getHistory, new RequestDelegate() { 
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                RemoteUtils.$r8$lambda$V4VBJH1NEvfcBnItgWjxPBLnJKs(tLObject, tL_error);
                            }
                        });
                    }
                };
                if (tL_messages_getHistory.peer.access_hash != 0) {
                    AndroidUtilities.runOnUIThread(runnable);
                } else {
                    ChatUtils.getInstance().resolveChannel("XS6GEcz5ZXMu82UvXQc", new Utilities.Callback() { 
                        @Override 
                        public final void run(Object obj) {
                            RemoteUtils.m1510$r8$lambda$wpFmLdCTd4P2BQcJG8DkR6gPJ8(tL_messages_getHistory, runnable, (TLRPC.Chat) obj);
                        }
                    });
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void $r8$lambda$eNTNmIzDWEebkWIkwoFUzANQvM8() {
    }

    public static void $r8$lambda$V4VBJH1NEvfcBnItgWjxPBLnJKs(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null || tLObject == null) {
            deliverMessagesResult(null, tL_error);
        } else {
            deliverMessagesResult((TLRPC.messages_Messages) tLObject, null);
        }
    }

    public static void m1510$r8$lambda$wpFmLdCTd4P2BQcJG8DkR6gPJ8(TLRPC.TL_messages_getHistory tL_messages_getHistory, Runnable runnable, TLRPC.Chat chat) {
        if (chat != null && chat.id == -2227431611L) {
            TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
            tL_messages_getHistory.peer = tL_inputPeerChannel;
            tL_inputPeerChannel.channel_id = chat.id;
            tL_inputPeerChannel.access_hash = chat.access_hash;
            AndroidUtilities.runOnUIThread(runnable);
            return;
        }
        TLRPC.TL_error tL_error = new TLRPC.TL_error();
        tL_error.code = 400;
        tL_error.text = "CHANNEL_RESOLVE_FAILED";
        deliverMessagesResult(null, tL_error);
    }

    public static void m1508$r8$lambda$UB_3HLxwoCbba6lUHVL1F5wfA(AtomicBoolean atomicBoolean, Utilities.Callback2 callback2) {
        if (atomicBoolean.compareAndSet(false, true)) {
            TLRPC.TL_error tL_error = new TLRPC.TL_error();
            tL_error.code = 408;
            tL_error.text = "REQUEST_TIMEOUT";
            callback2.run(null, tL_error);
        }
    }

    public static void m1509$r8$lambda$XRVIe9RERxrqY2e8aTEDV9B8B8(final AtomicBoolean atomicBoolean, AtomicInteger atomicInteger, AtomicInteger atomicInteger2, AccountInstance accountInstance, TLRPC.TL_messages_search tL_messages_search, final AtomicReference atomicReference, final Utilities.Callback2 callback2) {
        if (atomicBoolean.get()) {
            return;
        }
        atomicInteger.incrementAndGet();
        atomicInteger2.set(accountInstance.getConnectionsManager().sendRequest(tL_messages_search, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                RemoteUtils.m1507$r8$lambda$DT1VPJLc_jQIEOnKaPDKhvhjOA(atomicBoolean, atomicReference, callback2, tLObject, tL_error);
            }
        }));
    }

    private static void deliverMessagesResult(TLRPC.messages_Messages messages_messages, TLRPC.TL_error tL_error) {
        ArrayList<Utilities.Callback2<TLRPC.messages_Messages, TLRPC.TL_error>> arrayList;
        synchronized (messagesRequestLock) {
            arrayList = pendingMessagesCallbacks;
            pendingMessagesCallbacks = null;
        }
        if (arrayList == null) {
            return;
        }
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Utilities.Callback2<TLRPC.messages_Messages, TLRPC.TL_error> callback2 = arrayList.get(i);
            i++;
            callback2.run(messages_messages, tL_error);
        }
    }

    public static void searchMessages(String str, TLRPC.MessagesFilter messagesFilter, Utilities.Callback2<TLRPC.messages_Messages, TLRPC.TL_error> callback2, int i) {
        searchMessages(50, str, messagesFilter, callback2, i);
    }

    public static void searchMessages(int i, String str, TLRPC.MessagesFilter messagesFilter, final Utilities.Callback2<TLRPC.messages_Messages, TLRPC.TL_error> callback2, final int i2) {
        final AccountInstance accountInstance = AccountInstance.getInstance(UserConfig.selectedAccount);
        final TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
        tL_messages_search.peer = accountInstance.getMessagesController().getInputPeer(-2227431611L);
        tL_messages_search.q = str;
        tL_messages_search.offset_id = 0;
        tL_messages_search.limit = i;
        tL_messages_search.filter = messagesFilter;
        final AtomicBoolean atomicBoolean = new AtomicBoolean(false);
        final AtomicInteger atomicInteger = new AtomicInteger();
        final AtomicInteger atomicInteger2 = new AtomicInteger();
        final AtomicReference atomicReference = new AtomicReference(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                RemoteUtils.$r8$lambda$eNTNmIzDWEebkWIkwoFUzANQvM8();
            }
        });
        final Runnable runnable = new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                RemoteUtils.m1508$r8$lambda$UB_3HLxwoCbba6lUHVL1F5wfA(atomicBoolean, callback2);
            }
        };
        final Runnable runnable2 = new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                RemoteUtils.m1509$r8$lambda$XRVIe9RERxrqY2e8aTEDV9B8B8(atomicBoolean, atomicInteger, atomicInteger2, accountInstance, tL_messages_search, atomicReference, callback2);
            }
        };
        atomicReference.set(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                RemoteUtils.m1506$r8$lambda$DWE3IlXIJYUbvGajyIcIoAEsuk(atomicBoolean, accountInstance, atomicInteger2, atomicInteger, runnable2, atomicReference, i2, runnable);
            }
        });
        if (tL_messages_search.peer.access_hash != 0) {
            AndroidUtilities.runOnUIThread(runnable2);
            AndroidUtilities.runOnUIThread((Runnable) atomicReference.get(), i2);
        } else {
            ChatUtils.getInstance().resolveChannel("XS6GEcz5ZXMu82UvXQc", new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    RemoteUtils.$r8$lambda$AjHQ7MB3X57c7essAWT021npCFk(tL_messages_search, runnable2, atomicReference, i2, atomicBoolean, callback2, (TLRPC.Chat) obj);
                }
            });
        }
    }

    public static void m1507$r8$lambda$DT1VPJLc_jQIEOnKaPDKhvhjOA(AtomicBoolean atomicBoolean, AtomicReference atomicReference, Utilities.Callback2 callback2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (atomicBoolean.compareAndSet(false, true)) {
            AndroidUtilities.cancelRunOnUIThread((Runnable) atomicReference.get());
            if (tL_error != null || tLObject == null) {
                callback2.run(null, tL_error);
            } else {
                callback2.run((TLRPC.messages_Messages) tLObject, null);
            }
        }
    }

    public static /* synthetic */ void m1506$r8$lambda$DWE3IlXIJYUbvGajyIcIoAEsuk(AtomicBoolean atomicBoolean, AccountInstance accountInstance, AtomicInteger atomicInteger, AtomicInteger atomicInteger2, Runnable runnable, AtomicReference atomicReference, int i, Runnable runnable2) {
        if (atomicBoolean.get()) {
            return;
        }
        accountInstance.getConnectionsManager().cancelRequest(atomicInteger.get(), false);
        if (atomicInteger2.get() < 3) {
            AndroidUtilities.runOnUIThread(runnable);
            AndroidUtilities.runOnUIThread((Runnable) atomicReference.get(), i);
        } else {
            runnable2.run();
        }
    }

    public static /* synthetic */ void $r8$lambda$AjHQ7MB3X57c7essAWT021npCFk(TLRPC.TL_messages_search tL_messages_search, Runnable runnable, AtomicReference atomicReference, int i, AtomicBoolean atomicBoolean, Utilities.Callback2 callback2, TLRPC.Chat chat) {
        if (chat != null && chat.id == -2227431611L) {
            TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
            tL_messages_search.peer = tL_inputPeerChannel;
            tL_inputPeerChannel.channel_id = chat.id;
            tL_inputPeerChannel.access_hash = chat.access_hash;
            AndroidUtilities.runOnUIThread(runnable);
            AndroidUtilities.runOnUIThread((Runnable) atomicReference.get(), i);
            return;
        }
        if (atomicBoolean.compareAndSet(false, true)) {
            TLRPC.TL_error tL_error = new TLRPC.TL_error();
            tL_error.code = 400;
            tL_error.text = "CHANNEL_RESOLVE_FAILED";
            callback2.run(null, tL_error);
        }
    }

    public static Integer getIntConfigValue(String str, int i) {
        try {
            SharedPreferences prefs = getPrefs();
            if (prefs == null) {
                return Integer.valueOf(i);
            }
            Object obj = prefs.getAll().get(str);
            if (obj instanceof String) {
                return Integer.valueOf(Integer.parseInt((String) obj));
            }
            if (obj instanceof Long) {
                return Integer.valueOf(((Long) obj).intValue());
            }
            if (obj instanceof Integer) {
                return (Integer) obj;
            }
            return Integer.valueOf(i);
        } catch (Exception e) {
            AppUtils.log("Error getting int config value for key: " + str, e);
            return Integer.valueOf(i);
        }
    }

    public static Float getFloatConfigValue(String str, float f) {
        try {
            SharedPreferences prefs = getPrefs();
            if (prefs == null) {
                return Float.valueOf(f);
            }
            Object obj = prefs.getAll().get(str);
            if (obj instanceof String) {
                return Float.valueOf(Float.parseFloat((String) obj));
            }
            if (obj instanceof Float) {
                return (Float) obj;
            }
            if (obj instanceof Long) {
                return Float.valueOf(((Long) obj).floatValue());
            }
            if (obj instanceof Integer) {
                return Float.valueOf(((Integer) obj).floatValue());
            }
            return Float.valueOf(f);
        } catch (Exception e) {
            AppUtils.log("Error getting value for key: " + str, e);
            return Float.valueOf(f);
        }
    }

    public static Boolean getBooleanConfigValue(String str, boolean z) {
        try {
            SharedPreferences prefs = getPrefs();
            if (prefs == null) {
                return Boolean.valueOf(z);
            }
            try {
                return Boolean.valueOf(prefs.getBoolean(str, z));
            } catch (ClassCastException unused) {
                Object obj = prefs.getAll().get(str);
                return Boolean.valueOf(obj instanceof String ? Boolean.parseBoolean((String) obj) : z);
            }
        } catch (Exception e) {
            AppUtils.log("Error getting value for key: " + str, e);
            return Boolean.valueOf(z);
        }
    }

    public static Set<String> getStringSetConfigValue(String str, Set<String> set) {
        try {
            SharedPreferences prefs = getPrefs();
            if (prefs != null) {
                Object obj = prefs.getAll().get(str);
                if (obj instanceof Set) {
                    return (Set) obj;
                }
                if (obj instanceof String) {
                    return new HashSet(Arrays.asList(((String) obj).split(",\\s*")));
                }
            }
            return set;
        } catch (Exception e) {
            AppUtils.log("Error getting value for key: " + str, e);
            return set;
        }
    }

    public static String getStringConfigValue(String str, String str2) {
        Object obj;
        try {
            SharedPreferences prefs = getPrefs();
            if (prefs != null && (obj = prefs.getAll().get(str)) != null) {
                return String.valueOf(obj);
            }
            return str2;
        } catch (Exception e) {
            AppUtils.log("Error getting value for key: " + str, e);
            return str2;
        }
    }
}
