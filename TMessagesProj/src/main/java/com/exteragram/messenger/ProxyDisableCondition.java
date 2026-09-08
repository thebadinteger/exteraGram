package com.exteragram.messenger;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\b\n\u0002\b\b\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n¨\u0006\u000b"}, d2 = {"Lcom/exteragram/messenger/ProxyDisableCondition;", _UrlKt.FRAGMENT_ENCODE_SET, "flag", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;II)V", "getFlag", "()I", "VPN", "MOBILE_DATA", "WIFI", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public enum ProxyDisableCondition {
    VPN(1),
    MOBILE_DATA(2),
    WIFI(4);

    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());
    private final int flag;

    ProxyDisableCondition(int i) {
        this.flag = i;
    }

    public final int getFlag() {
        return this.flag;
    }
}
