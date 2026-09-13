package com.exteragram.messenger.proxy;

import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.IDN;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import okhttp3.internal.url._UrlKt;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;

public final class ProxyController {
    private static final ProxyController INSTANCE = new ProxyController();
    private SharedConfig.ProxyInfo currentProxy;
    private boolean loaded;
    private final ArrayList<SharedConfig.ProxyInfo> proxyList = new ArrayList<>();
    private final HashSet<String> pinnedProxies = new HashSet<>();
    private final ArrayList<String> pinnedProxyOrder = new ArrayList<>();
    private final HashMap<String, String> proxyNames = new HashMap<>();

    public enum PinOperationResult {
        NO_CHANGE,
        CHANGED,
        LIMIT_REACHED
    }

    public interface ProxyCountryCallback {
        void onCountryResolved(String str);
    }

    public int getMaxPinnedProxies() {
        return 10;
    }

    public static ProxyController getInstance() {
        return INSTANCE;
    }

    private ProxyController() {
    }

    public synchronized ArrayList<SharedConfig.ProxyInfo> getProxyList() {
        ensureLoaded();
        return new ArrayList<>(this.proxyList);
    }

    public synchronized SharedConfig.ProxyInfo getCurrentProxy() {
        ensureLoaded();
        return this.currentProxy;
    }

    public synchronized void setCurrentProxy(SharedConfig.ProxyInfo proxyInfo) {
        ensureLoaded();
        this.currentProxy = proxyInfo;
        SharedConfig.currentProxy = proxyInfo;
    }

    public synchronized void loadProxyList() {
        ensureLoaded();
    }

    public synchronized void saveProxyList() {
        try {
            ensureLoaded();
            ArrayList arrayList = new ArrayList(this.proxyList);
            Collections.sort(arrayList, new Comparator() { // from class: com.exteragram.messenger.proxy.ProxyController$$ExternalSyntheticLambda0
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return ProxyController.this.lambda$saveProxyList$0((SharedConfig.ProxyInfo) obj, (SharedConfig.ProxyInfo) obj2);
                }
            });
            SerializedData serializedData = new SerializedData();
            serializedData.writeInt32(-1);
            serializedData.writeByte(4);
            int size = arrayList.size();
            serializedData.writeInt32(size);
            for (int i = size - 1; i >= 0; i--) {
                SharedConfig.ProxyInfo proxyInfo = (SharedConfig.ProxyInfo) arrayList.get(i);
                String str = proxyInfo.address;
                if (str == null) {
                    str = _UrlKt.FRAGMENT_ENCODE_SET;
                }
                serializedData.writeString(str);
                serializedData.writeInt32(proxyInfo.port);
                String str2 = proxyInfo.username;
                if (str2 == null) {
                    str2 = _UrlKt.FRAGMENT_ENCODE_SET;
                }
                serializedData.writeString(str2);
                String str3 = proxyInfo.password;
                if (str3 == null) {
                    str3 = _UrlKt.FRAGMENT_ENCODE_SET;
                }
                serializedData.writeString(str3);
                String str4 = proxyInfo.secret;
                if (str4 == null) {
                    str4 = _UrlKt.FRAGMENT_ENCODE_SET;
                }
                serializedData.writeString(str4);
                serializedData.writeInt64(proxyInfo.ping);
                serializedData.writeInt64(proxyInfo.availableCheckTime);
            }
            ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit().putString("proxy_list", Base64.encodeToString(serializedData.toByteArray(), 2)).apply();
            serializedData.cleanup();
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$saveProxyList$0(SharedConfig.ProxyInfo proxyInfo, SharedConfig.ProxyInfo proxyInfo2) {
        SharedConfig.ProxyInfo proxyInfo3 = this.currentProxy;
        long j = proxyInfo3 == proxyInfo ? -200000L : 0L;
        if (!proxyInfo.available) {
            j += 100000;
        }
        long j2 = proxyInfo3 == proxyInfo2 ? -200000L : 0L;
        if (!proxyInfo2.available) {
            j2 += 100000;
        }
        return Long.compare(proxyInfo.ping + j, proxyInfo2.ping + j2);
    }

    public synchronized SharedConfig.ProxyInfo addProxy(SharedConfig.ProxyInfo proxyInfo) {
        ensureLoaded();
        int size = this.proxyList.size();
        int i = 0;
        while (true) {
            ArrayList<SharedConfig.ProxyInfo> arrayList = this.proxyList;
            if (i < size) {
                SharedConfig.ProxyInfo proxyInfo2 = arrayList.get(i);
                if (proxyInfo.address.equals(proxyInfo2.address) && proxyInfo.port == proxyInfo2.port && proxyInfo.username.equals(proxyInfo2.username) && proxyInfo.password.equals(proxyInfo2.password) && proxyInfo.secret.equals(proxyInfo2.secret)) {
                    return proxyInfo2;
                }
                i++;
            } else {
                arrayList.add(0, proxyInfo);
                syncProxyList();
                saveProxyList();
                return proxyInfo;
            }
        }
    }

    public synchronized SharedConfig.ProxyInfo saveProxy(SharedConfig.ProxyInfo proxyInfo, String str, String str2) {
        SharedConfig.ProxyInfo proxyInfoAddProxy;
        try {
            ensureLoaded();
            if (!TextUtils.isEmpty(str) && !TextUtils.equals(str, proxyInfo.getLink())) {
                moveMetadata(str, proxyInfo.getLink());
            }
            proxyInfoAddProxy = addProxy(proxyInfo);
            if (str2 != null) {
                setName(proxyInfoAddProxy, str2);
            }
        } catch (Throwable th) {
            throw th;
        }
        return proxyInfoAddProxy;
    }

    public synchronized String buildShareLink(SharedConfig.ProxyInfo proxyInfo) {
        return buildShareLink(proxyInfo, null);
    }

    public synchronized String buildShareLink(SharedConfig.ProxyInfo proxyInfo, String str) {
        if (proxyInfo == null) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        String link = proxyInfo.getLink();
        String strNormalizeName = normalizeName(str);
        if (TextUtils.isEmpty(strNormalizeName)) {
            strNormalizeName = getName(proxyInfo);
        }
        if (TextUtils.isEmpty(strNormalizeName)) {
            return link;
        }
        try {
            return link + "&title=" + URLEncoder.encode(strNormalizeName, "UTF-8");
        } catch (UnsupportedEncodingException unused) {
            return link;
        }
    }

    public synchronized void deleteProxy(SharedConfig.ProxyInfo proxyInfo) {
        try {
            ensureLoaded();
            String proxyKey = getProxyKey(proxyInfo);
            SharedConfig.ProxyInfo proxyInfo2 = this.currentProxy;
            if (proxyInfo2 == proxyInfo || (proxyKey != null && proxyKey.equals(getProxyKey(proxyInfo2)))) {
                this.currentProxy = null;
                SharedConfig.currentProxy = null;
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                boolean z = globalMainSettings.getBoolean("proxy_enabled", false);
                SharedPreferences.Editor editorEdit = globalMainSettings.edit();
                editorEdit.putString("proxy_ip", _UrlKt.FRAGMENT_ENCODE_SET);
                editorEdit.putString("proxy_pass", _UrlKt.FRAGMENT_ENCODE_SET);
                editorEdit.putString("proxy_user", _UrlKt.FRAGMENT_ENCODE_SET);
                editorEdit.putString("proxy_secret", _UrlKt.FRAGMENT_ENCODE_SET);
                editorEdit.putInt("proxy_port", 1080);
                editorEdit.putBoolean("proxy_enabled", false);
                editorEdit.putBoolean("proxy_enabled_calls", false);
                editorEdit.apply();
                if (z) {
                    ConnectionsManager.setProxySettings(false, _UrlKt.FRAGMENT_ENCODE_SET, 0, _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET);
                }
            }
            this.proxyList.remove(proxyInfo);
            removeProxy(proxyInfo);
            syncProxyList();
            saveProxyList();
        } catch (Throwable th) {
            throw th;
        }
    }

    public synchronized String getDisplayName(SharedConfig.ProxyInfo proxyInfo) {
        if (proxyInfo == null) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        String name = getName(proxyInfo);
        if (TextUtils.isEmpty(name)) {
            name = proxyInfo.address + ":" + proxyInfo.port;
        }
        return name;
    }

    public synchronized String getProxyTypeName(SharedConfig.ProxyInfo proxyInfo) {
        try {
        } catch (Throwable th) {
            throw th;
        }
        return LocaleController.getString(isTelegramProxy(proxyInfo) ? R.string.UseProxyTelegram : R.string.UseProxySocks5);
    }

    public void requestProxyCountry(SharedConfig.ProxyInfo proxyInfo, final ProxyCountryCallback proxyCountryCallback) {
        if (proxyCountryCallback == null) {
            return;
        }
        final String string = LocaleController.getString(R.string.Unknown);
        final String proxyCountryKey = getProxyCountryKey(proxyInfo);
        if (TextUtils.isEmpty(proxyCountryKey)) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.proxy.ProxyController$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    proxyCountryCallback.onCountryResolved(string);
                }
            });
        } else {
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: com.exteragram.messenger.proxy.ProxyController$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    ProxyController.this.lambda$requestProxyCountry$3(string, proxyCountryKey, proxyCountryCallback);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestProxyCountry$3(String str, String str2, final ProxyCountryCallback proxyCountryCallback) {
        String country = str;
        try {
            country = getCountryDisplayName(fetchProxyCountryCode(str2));
        } catch (Exception e) {
            FileLog.e(e);
        }
        final String resolvedCountry = country;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.proxy.ProxyController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                proxyCountryCallback.onCountryResolved(resolvedCountry);
            }
        });
    }

    public synchronized String getName(SharedConfig.ProxyInfo proxyInfo) {
        String name;
        if (proxyInfo == null) {
            name = _UrlKt.FRAGMENT_ENCODE_SET;
        } else {
            try {
                name = getName(getProxyKey(proxyInfo));
            } catch (Throwable th) {
                throw th;
            }
        }
        return name;
    }

    public synchronized String getName(String str) {
        ensureLoaded();
        if (TextUtils.isEmpty(str)) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        String str2 = this.proxyNames.get(str);
        if (str2 == null) {
            str2 = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        return str2;
    }

    public synchronized void setName(SharedConfig.ProxyInfo proxyInfo, String str) {
        setName(getProxyKey(proxyInfo), str);
    }

    public synchronized void setName(String str, String str2) {
        try {
            ensureLoaded();
            if (TextUtils.isEmpty(str)) {
                return;
            }
            String strNormalizeName = normalizeName(str2);
            boolean zIsEmpty = TextUtils.isEmpty(strNormalizeName);
            HashMap<String, String> map = this.proxyNames;
            boolean zEquals = true;
            if (zIsEmpty) {
                if (map.remove(str) == null) {
                    zEquals = false;
                }
            } else {
                zEquals = true ^ TextUtils.equals(map.put(str, strNormalizeName), strNormalizeName);
            }
            if (zEquals) {
                save();
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public synchronized void moveMetadata(String str, String str2) {
        boolean z;
        try {
            ensureLoaded();
            if (!TextUtils.isEmpty(str) && !TextUtils.isEmpty(str2) && !TextUtils.equals(str, str2)) {
                String strRemove = this.proxyNames.remove(str);
                boolean z2 = true;
                if (TextUtils.isEmpty(strRemove)) {
                    z = false;
                } else {
                    this.proxyNames.put(str2, strRemove);
                    z = true;
                }
                int iIndexOf = this.pinnedProxyOrder.indexOf(str);
                if (iIndexOf >= 0) {
                    this.pinnedProxyOrder.remove(iIndexOf);
                    this.pinnedProxies.remove(str);
                    if (!this.pinnedProxyOrder.contains(str2)) {
                        ArrayList<String> arrayList = this.pinnedProxyOrder;
                        arrayList.add(Math.min(iIndexOf, arrayList.size()), str2);
                    }
                    this.pinnedProxies.add(str2);
                } else {
                    z2 = z;
                }
                if (z2) {
                    save();
                }
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    /* JADX WARN: Code duplicated, block: B:10:0x0012  */
    public synchronized boolean isPinned(SharedConfig.ProxyInfo proxyInfo) {
        boolean z;
        if (proxyInfo == null) {
            z = false;
        } else if (isPinned(getProxyKey(proxyInfo))) {
            z = true;
        } else {
            z = false;
        }
        return z;
    }

    public synchronized boolean isPinned(String str) {
        ensureLoaded();
        return !TextUtils.isEmpty(str) && this.pinnedProxies.contains(str);
    }

    public synchronized int getPinnedIndex(SharedConfig.ProxyInfo proxyInfo) {
        ensureLoaded();
        int iIndexOf = -1;
        if (proxyInfo == null) {
            return -1;
        }
        String proxyKey = getProxyKey(proxyInfo);
        if (!TextUtils.isEmpty(proxyKey)) {
            iIndexOf = this.pinnedProxyOrder.indexOf(proxyKey);
        }
        return iIndexOf;
    }

    public synchronized int getPinnedCount() {
        ensureLoaded();
        return this.pinnedProxyOrder.size();
    }

    public synchronized int getSelectedPinAction(List<SharedConfig.ProxyInfo> list) {
        ensureLoaded();
        Iterator<SharedConfig.ProxyInfo> it = list.iterator();
        boolean z = false;
        boolean z2 = false;
        while (it.hasNext()) {
            if (isPinned(it.next())) {
                z = true;
            } else {
                z2 = true;
            }
            if (z && z2) {
                return 0;
            }
        }
        if (z) {
            return 2;
        }
        return z2 ? 1 : 0;
    }

    public synchronized PinOperationResult applySelectedPinAction(List<SharedConfig.ProxyInfo> list) {
        ensureLoaded();
        int selectedPinAction = getSelectedPinAction(list);
        if (selectedPinAction == 0) {
            return PinOperationResult.NO_CHANGE;
        }
        boolean z = false;
        if (selectedPinAction == 1) {
            ArrayList arrayList = new ArrayList();
            Iterator<SharedConfig.ProxyInfo> it = list.iterator();
            while (it.hasNext()) {
                String proxyKey = getProxyKey(it.next());
                if (!TextUtils.isEmpty(proxyKey) && !this.pinnedProxies.contains(proxyKey) && !arrayList.contains(proxyKey)) {
                    arrayList.add(proxyKey);
                }
            }
            if (this.pinnedProxyOrder.size() + arrayList.size() > 10) {
                return PinOperationResult.LIMIT_REACHED;
            }
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Object obj = arrayList.get(i);
                i++;
                String str = (String) obj;
                if (this.pinnedProxies.add(str)) {
                    this.pinnedProxyOrder.add(str);
                    z = true;
                }
            }
        } else {
            Iterator<SharedConfig.ProxyInfo> it2 = list.iterator();
            while (it2.hasNext()) {
                String proxyKey2 = getProxyKey(it2.next());
                if (!TextUtils.isEmpty(proxyKey2)) {
                    if (this.pinnedProxies.remove(proxyKey2)) {
                        z = true;
                    }
                    if (this.pinnedProxyOrder.remove(proxyKey2)) {
                        z = true;
                    }
                }
            }
        }
        if (z) {
            save();
            return PinOperationResult.CHANGED;
        }
        return PinOperationResult.NO_CHANGE;
    }

    public synchronized boolean movePinnedProxy(SharedConfig.ProxyInfo proxyInfo, SharedConfig.ProxyInfo proxyInfo2) {
        ensureLoaded();
        String proxyKey = getProxyKey(proxyInfo);
        String proxyKey2 = getProxyKey(proxyInfo2);
        if (!TextUtils.isEmpty(proxyKey) && !TextUtils.isEmpty(proxyKey2) && !TextUtils.equals(proxyKey, proxyKey2)) {
            if (this.pinnedProxies.contains(proxyKey) && this.pinnedProxies.contains(proxyKey2)) {
                int iIndexOf = this.pinnedProxyOrder.indexOf(proxyKey);
                int iIndexOf2 = this.pinnedProxyOrder.indexOf(proxyKey2);
                if (iIndexOf >= 0 && iIndexOf2 >= 0) {
                    this.pinnedProxyOrder.remove(iIndexOf);
                    this.pinnedProxyOrder.add(iIndexOf2, proxyKey);
                    save();
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    public synchronized void removeProxy(SharedConfig.ProxyInfo proxyInfo) {
        ensureLoaded();
        String proxyKey = getProxyKey(proxyInfo);
        if (TextUtils.isEmpty(proxyKey)) {
            return;
        }
        if (this.pinnedProxyOrder.remove(proxyKey) | (this.proxyNames.remove(proxyKey) != null) | this.pinnedProxies.remove(proxyKey)) {
            save();
        }
    }

    public synchronized void clearAll() {
        ensureLoaded();
        if (this.pinnedProxies.isEmpty() && this.pinnedProxyOrder.isEmpty() && this.proxyNames.isEmpty()) {
            return;
        }
        this.pinnedProxies.clear();
        this.pinnedProxyOrder.clear();
        this.proxyNames.clear();
        save();
    }

    public synchronized void sortProxyList(List<SharedConfig.ProxyInfo> list, final boolean z, final SharedConfig.ProxyInfo proxyInfo) {
        ensureLoaded();
        final ArrayList arrayList = new ArrayList(list);
        Collections.sort(list, new Comparator() { // from class: com.exteragram.messenger.proxy.ProxyController$$ExternalSyntheticLambda1
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ProxyController.this.lambda$sortProxyList$4(proxyInfo, z, arrayList, (SharedConfig.ProxyInfo) obj, (SharedConfig.ProxyInfo) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ int lambda$sortProxyList$4(SharedConfig.ProxyInfo proxyInfo, boolean z, ArrayList arrayList, SharedConfig.ProxyInfo proxyInfo2, SharedConfig.ProxyInfo proxyInfo3) {
        int pinnedIndex = getPinnedIndex(proxyInfo2);
        int pinnedIndex2 = getPinnedIndex(proxyInfo3);
        boolean z2 = pinnedIndex >= 0;
        if (z2 != (pinnedIndex2 >= 0)) {
            return z2 ? -1 : 1;
        }
        if (z2) {
            return Integer.compare(pinnedIndex, pinnedIndex2);
        }
        long j = proxyInfo == proxyInfo2 ? -200000L : 0L;
        if (!proxyInfo2.available) {
            j += 100000;
        }
        long j2 = proxyInfo == proxyInfo3 ? -200000L : 0L;
        if (!proxyInfo3.available) {
            j2 += 100000;
        }
        return Long.compare((!z || proxyInfo2 == proxyInfo) ? j + proxyInfo2.ping : ((long) arrayList.indexOf(proxyInfo2)) * 10000, (!z || proxyInfo3 == proxyInfo) ? proxyInfo3.ping + j2 : ((long) arrayList.indexOf(proxyInfo3)) * 10000);
    }

    private void ensureLoaded() {
        if (this.loaded) {
            return;
        }
        load();
    }

    private void load() {
        boolean z;
        boolean z2;
        int iIndexOf;
        this.proxyList.clear();
        this.currentProxy = null;
        this.pinnedProxies.clear();
        this.pinnedProxyOrder.clear();
        this.proxyNames.clear();
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0);
        String string = sharedPreferences.getString("proxy_ip", _UrlKt.FRAGMENT_ENCODE_SET);
        String string2 = sharedPreferences.getString("proxy_user", _UrlKt.FRAGMENT_ENCODE_SET);
        String string3 = sharedPreferences.getString("proxy_pass", _UrlKt.FRAGMENT_ENCODE_SET);
        String string4 = sharedPreferences.getString("proxy_secret", _UrlKt.FRAGMENT_ENCODE_SET);
        int i = sharedPreferences.getInt("proxy_port", 1080);
        String string5 = sharedPreferences.getString("proxy_list", null);
        if (TextUtils.isEmpty(string5)) {
            z = true;
            z2 = false;
        } else {
            SerializedData serializedData = new SerializedData(Base64.decode(string5, 0));
            int int32 = serializedData.readInt32(false);
            if (int32 == -1) {
                byte b2 = serializedData.readByte(false);
                if (b2 == 4) {
                    int int33 = serializedData.readInt32(false);
                    for (int i2 = 0; i2 < int33; i2++) {
                        SharedConfig.ProxyInfo proxyInfo = new SharedConfig.ProxyInfo(serializedData.readString(false), serializedData.readInt32(false), serializedData.readString(false), serializedData.readString(false), serializedData.readString(false));
                        proxyInfo.ping = serializedData.readInt64(false);
                        proxyInfo.availableCheckTime = SharedConfig.ProxyInfo.normalizeAvailableCheckTime(serializedData.readInt64(false));
                        this.proxyList.add(0, proxyInfo);
                        if (this.currentProxy == null && !TextUtils.isEmpty(string) && string.equals(proxyInfo.address) && i == proxyInfo.port && string2.equals(proxyInfo.username) && string3.equals(proxyInfo.password)) {
                            this.currentProxy = proxyInfo;
                        }
                    }
                    z = true;
                } else {
                    if (b2 == 3) {
                        int int34 = serializedData.readInt32(false);
                        z2 = false;
                        for (int i3 = 0; i3 < int34; i3++) {
                            String string6 = serializedData.readString(false);
                            SharedConfig.ProxyInfo proxyInfo2 = new SharedConfig.ProxyInfo(serializedData.readString(false), serializedData.readInt32(false), serializedData.readString(false), serializedData.readString(false), serializedData.readString(false));
                            boolean z3 = z2;
                            proxyInfo2.ping = serializedData.readInt64(false);
                            proxyInfo2.availableCheckTime = SharedConfig.ProxyInfo.normalizeAvailableCheckTime(serializedData.readInt64(false));
                            this.proxyList.add(0, proxyInfo2);
                            String proxyKey = getProxyKey(proxyInfo2);
                            String strNormalizeName = normalizeName(string6);
                            if (TextUtils.isEmpty(proxyKey) || TextUtils.isEmpty(strNormalizeName)) {
                                z2 = z3;
                            } else {
                                this.proxyNames.put(proxyKey, strNormalizeName);
                                z2 = true;
                            }
                            if (this.currentProxy == null && !TextUtils.isEmpty(string) && string.equals(proxyInfo2.address) && i == proxyInfo2.port && string2.equals(proxyInfo2.username) && string3.equals(proxyInfo2.password)) {
                                this.currentProxy = proxyInfo2;
                            }
                        }
                        z = true;
                    } else {
                        z = true;
                        if (b2 == 2) {
                            int int35 = serializedData.readInt32(false);
                            for (int i4 = 0; i4 < int35; i4++) {
                                SharedConfig.ProxyInfo proxyInfo3 = new SharedConfig.ProxyInfo(serializedData.readString(false), serializedData.readInt32(false), serializedData.readString(false), serializedData.readString(false), serializedData.readString(false));
                                proxyInfo3.ping = serializedData.readInt64(false);
                                proxyInfo3.availableCheckTime = SharedConfig.ProxyInfo.normalizeAvailableCheckTime(serializedData.readInt64(false));
                                this.proxyList.add(0, proxyInfo3);
                                if (this.currentProxy == null && !TextUtils.isEmpty(string) && string.equals(proxyInfo3.address) && i == proxyInfo3.port && string2.equals(proxyInfo3.username) && string3.equals(proxyInfo3.password)) {
                                    this.currentProxy = proxyInfo3;
                                }
                            }
                        } else {
                            FileLog.e("Unknown proxy schema version: " + ((int) b2));
                        }
                    }
                    serializedData.cleanup();
                }
            } else {
                z = true;
                for (int i5 = 0; i5 < int32; i5++) {
                    SharedConfig.ProxyInfo proxyInfo4 = new SharedConfig.ProxyInfo(serializedData.readString(false), serializedData.readInt32(false), serializedData.readString(false), serializedData.readString(false), serializedData.readString(false));
                    this.proxyList.add(0, proxyInfo4);
                    if (this.currentProxy == null && !TextUtils.isEmpty(string) && string.equals(proxyInfo4.address) && i == proxyInfo4.port && string2.equals(proxyInfo4.username) && string3.equals(proxyInfo4.password)) {
                        this.currentProxy = proxyInfo4;
                    }
                }
            }
            z2 = false;
            serializedData.cleanup();
        }
        if (this.currentProxy == null && !TextUtils.isEmpty(string)) {
            SharedConfig.ProxyInfo proxyInfo5 = new SharedConfig.ProxyInfo(string, i, string2, string3, string4);
            this.currentProxy = proxyInfo5;
            this.proxyList.add(0, proxyInfo5);
        }
        String string7 = sharedPreferences.getString("proxy_pinned_order", sharedPreferences.getString("proxy_pinned_links_order", null));
        if (!TextUtils.isEmpty(string7)) {
            for (String str : string7.split("\\n")) {
                if (!TextUtils.isEmpty(str) && this.pinnedProxies.add(str)) {
                    this.pinnedProxyOrder.add(str);
                }
            }
        }
        Set<String> stringSet = sharedPreferences.getStringSet("proxy_pinned", sharedPreferences.getStringSet("proxy_pinned_links", null));
        if (stringSet != null) {
            for (String str2 : stringSet) {
                if (!TextUtils.isEmpty(str2) && this.pinnedProxies.add(str2)) {
                    this.pinnedProxyOrder.add(str2);
                }
            }
        }
        String string8 = sharedPreferences.getString("proxy_names", null);
        if (!TextUtils.isEmpty(string8)) {
            for (String str3 : string8.split("\\n")) {
                if (!TextUtils.isEmpty(str3) && (iIndexOf = str3.indexOf(9)) > 0 && iIndexOf < str3.length() - 1) {
                    String strDecode = decode(str3.substring(0, iIndexOf));
                    String strDecode2 = decode(str3.substring(iIndexOf + 1));
                    if (!TextUtils.isEmpty(strDecode) && !TextUtils.isEmpty(strDecode2)) {
                        this.proxyNames.put(strDecode, strDecode2);
                    }
                }
            }
        }
        this.loaded = z;
        if (z2) {
            save();
        }
        syncProxyList();
    }

    private void save() {
        SharedPreferences.Editor editorEdit = MessagesController.getGlobalMainSettings().edit();
        editorEdit.putStringSet("proxy_pinned", new HashSet(this.pinnedProxies));
        if (this.pinnedProxyOrder.isEmpty()) {
            editorEdit.remove("proxy_pinned_order");
        } else {
            editorEdit.putString("proxy_pinned_order", TextUtils.join("\n", this.pinnedProxyOrder));
        }
        editorEdit.remove("proxy_pinned_links");
        editorEdit.remove("proxy_pinned_links_order");
        if (this.proxyNames.isEmpty()) {
            editorEdit.remove("proxy_names");
        } else {
            ArrayList arrayList = new ArrayList(this.proxyNames.size());
            for (Map.Entry<String, String> entry : this.proxyNames.entrySet()) {
                arrayList.add(encode(entry.getKey()) + "\t" + encode(entry.getValue()));
            }
            editorEdit.putString("proxy_names", TextUtils.join("\n", arrayList));
        }
        editorEdit.apply();
    }

    private String getProxyKey(SharedConfig.ProxyInfo proxyInfo) {
        if (proxyInfo == null || TextUtils.isEmpty(proxyInfo.getLink())) {
            return null;
        }
        return proxyInfo.getLink();
    }

    private static String normalizeName(String str) {
        return str == null ? _UrlKt.FRAGMENT_ENCODE_SET : str.trim();
    }

    private static String encode(String str) {
        if (str == null) {
            str = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        return Base64.encodeToString(str.getBytes(StandardCharsets.UTF_8), 2);
    }

    private static String decode(String str) {
        return new String(Base64.decode(str, 2), StandardCharsets.UTF_8);
    }

    private void syncProxyList() {
        SharedConfig.proxyList.clear();
        SharedConfig.proxyList.addAll(this.proxyList);
        SharedConfig.currentProxy = this.currentProxy;
    }

    private String fetchProxyCountryCode(String str) {
        String strNormalizeProxyCountryKey = normalizeProxyCountryKey(str);
        if (TextUtils.isEmpty(strNormalizeProxyCountryKey)) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        HttpURLConnection httpURLConnection = null;
        BufferedReader bufferedReader = null;
        try {
            InetAddress byName = InetAddress.getByName(IDN.toASCII(strNormalizeProxyCountryKey));
            if (byName.isAnyLocalAddress() || byName.isLoopbackAddress() || byName.isLinkLocalAddress() || byName.isSiteLocalAddress()) {
                return _UrlKt.FRAGMENT_ENCODE_SET;
            }
            httpURLConnection = (HttpURLConnection) new URL("https://ipwho.is/" + URLEncoder.encode(byName.getHostAddress(), "UTF-8")).openConnection();
            httpURLConnection.setConnectTimeout(4000);
            httpURLConnection.setReadTimeout(4000);
            httpURLConnection.setInstanceFollowRedirects(true);
            httpURLConnection.setUseCaches(false);
            httpURLConnection.setRequestMethod("GET");
            if (httpURLConnection.getResponseCode() != 200) {
                return _UrlKt.FRAGMENT_ENCODE_SET;
            }
            bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }
            JSONObject jSONObject = new JSONObject(sb.toString());
            if (!jSONObject.optBoolean("success", false)) {
                return _UrlKt.FRAGMENT_ENCODE_SET;
            }
            String strTrim = jSONObject.optString("country_code", _UrlKt.FRAGMENT_ENCODE_SET).trim();
            if (strTrim.length() == 2) {
                return strTrim.toUpperCase(Locale.US);
            }
            return _UrlKt.FRAGMENT_ENCODE_SET;
        } catch (Exception e) {
            FileLog.e(e);
            return _UrlKt.FRAGMENT_ENCODE_SET;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (Exception ignored) {}
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    private static String getProxyCountryKey(SharedConfig.ProxyInfo proxyInfo) {
        return proxyInfo == null ? _UrlKt.FRAGMENT_ENCODE_SET : normalizeProxyCountryKey(proxyInfo.address);
    }

    private static String normalizeProxyCountryKey(String str) {
        if (TextUtils.isEmpty(str)) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        String strTrim = str.trim();
        if (strTrim.startsWith("[") && strTrim.endsWith("]") && strTrim.length() > 2) {
            strTrim = strTrim.substring(1, strTrim.length() - 1);
        }
        return strTrim.toLowerCase(Locale.US);
    }

    private static String getCountryDisplayName(String str) {
        if (TextUtils.isEmpty(str)) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        Locale currentLocale = LocaleController.getInstance().getCurrentLocale();
        if (currentLocale == null) {
            currentLocale = Locale.getDefault();
        }
        String displayCountry = new Locale(_UrlKt.FRAGMENT_ENCODE_SET, str.toUpperCase(Locale.US)).getDisplayCountry(currentLocale);
        return TextUtils.isEmpty(displayCountry) ? LocaleController.getString(R.string.Unknown) : displayCountry;
    }

    private static boolean isTelegramProxy(SharedConfig.ProxyInfo proxyInfo) {
        return (proxyInfo == null || TextUtils.isEmpty(proxyInfo.secret)) ? false : true;
    }
}
