package com.exteragram.messenger.proxy;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u001f\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010'\n\u0000*\u0001\u0000\b\n\u0018\u00002\u000e\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u00030\u0001J\u001e\u0010\u0004\u001a\u00020\u00052\u0014\u0010\u0006\u001a\u0010\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0007H\u0014¨\u0006\b"}, d2 = {"com/exteragram/messenger/proxy/IpAddressInfoController$cache$1", "Ljava/util/LinkedHashMap;", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/proxy/IpAddressInfoController$CacheEntry;", "removeEldestEntry", _UrlKt.FRAGMENT_ENCODE_SET, "eldest", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class IpAddressInfoController$cache$1 extends LinkedHashMap<String, IpAddressInfoController.CacheEntry> {
    public IpAddressInfoController$cache$1() {
        super(100, 0.75f, true);
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public final /* bridge */ boolean containsKey(Object obj) {
        if (obj instanceof String) {
            return containsKey((String) obj);
        }
        return false;
    }

    public /* bridge */ boolean containsKey(String str) {
        return super.containsKey((Object) str);
    }

    public /* bridge */ boolean containsValue(IpAddressInfoController.CacheEntry cacheEntry) {
        return super.containsValue((Object) cacheEntry);
    }

    @Override // java.util.LinkedHashMap, java.util.HashMap, java.util.AbstractMap, java.util.Map
    public final /* bridge */ boolean containsValue(Object obj) {
        if (obj instanceof IpAddressInfoController.CacheEntry) {
            return containsValue((IpAddressInfoController.CacheEntry) obj);
        }
        return false;
    }

    @Override // java.util.LinkedHashMap, java.util.HashMap, java.util.AbstractMap, java.util.Map
    public final /* bridge */ Set<Map.Entry<String, IpAddressInfoController.CacheEntry>> entrySet() {
        return getEntries();
    }

    public /* bridge */ IpAddressInfoController.CacheEntry get(String str) {
        return (IpAddressInfoController.CacheEntry) super.get((Object) str);
    }

    @Override // java.util.LinkedHashMap, java.util.HashMap, java.util.AbstractMap, java.util.Map
    public final /* bridge */ /* synthetic */ Object get(Object obj) {
        if (obj instanceof String) {
            return get((String) obj);
        }
        return null;
    }

    public /* bridge */ Set<Map.Entry<String, IpAddressInfoController.CacheEntry>> getEntries() {
        return super.entrySet();
    }

    public /* bridge */ Set<String> getKeys() {
        return super.keySet();
    }

    public /* bridge */ IpAddressInfoController.CacheEntry getOrDefault(String str, IpAddressInfoController.CacheEntry cacheEntry) {
        return (IpAddressInfoController.CacheEntry) super.getOrDefault((Object) str, cacheEntry);
    }

    @Override // java.util.LinkedHashMap, java.util.HashMap, java.util.Map
    public final /* bridge */ /* synthetic */ Object getOrDefault(Object obj, Object obj2) {
        return !(obj instanceof String) ? obj2 : getOrDefault((String) obj, (IpAddressInfoController.CacheEntry) obj2);
    }

    public /* bridge */ int getSize() {
        return super.size();
    }

    public /* bridge */ Collection<IpAddressInfoController.CacheEntry> getValues() {
        return super.values();
    }

    @Override // java.util.LinkedHashMap, java.util.HashMap, java.util.AbstractMap, java.util.Map
    public final /* bridge */ Set<String> keySet() {
        return getKeys();
    }

    public /* bridge */ IpAddressInfoController.CacheEntry remove(String str) {
        return (IpAddressInfoController.CacheEntry) super.remove((Object) str);
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public final /* bridge */ /* synthetic */ Object remove(Object obj) {
        if (obj instanceof String) {
            return remove((String) obj);
        }
        return null;
    }

    @Override // java.util.HashMap, java.util.Map
    public final /* bridge */ boolean remove(Object obj, Object obj2) {
        if ((obj instanceof String) && (obj2 instanceof IpAddressInfoController.CacheEntry)) {
            return remove((String) obj, (IpAddressInfoController.CacheEntry) obj2);
        }
        return false;
    }

    public /* bridge */ boolean remove(String str, IpAddressInfoController.CacheEntry cacheEntry) {
        return super.remove((Object) str, (Object) cacheEntry);
    }

    @Override // java.util.HashMap, java.util.AbstractMap, java.util.Map
    public final /* bridge */ int size() {
        return getSize();
    }

    @Override // java.util.LinkedHashMap, java.util.HashMap, java.util.AbstractMap, java.util.Map
    public final /* bridge */ Collection<IpAddressInfoController.CacheEntry> values() {
        return getValues();
    }

    @Override // java.util.LinkedHashMap
    public boolean removeEldestEntry(Map.Entry<String, IpAddressInfoController.CacheEntry> eldest) {
        return size() > 100;
    }
}
