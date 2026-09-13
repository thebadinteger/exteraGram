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

    @Override
    public boolean removeEldestEntry(Map.Entry<String, IpAddressInfoController.CacheEntry> eldest) {
        return size() > 100;
    }
}
