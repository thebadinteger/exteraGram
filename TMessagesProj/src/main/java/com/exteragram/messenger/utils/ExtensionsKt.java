package com.exteragram.messenger.utils;

import java.util.List;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010!\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\u001a+\u0010\u0000\u001a\u00020\u0001\"\u0004\b\u0000\u0010\u0002*\b\u0012\u0004\u0012\u0002H\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u0002H\u0002¢\u0006\u0002\u0010\u0007¨\u0006\b"}, d2 = {"addIf", _UrlKt.FRAGMENT_ENCODE_SET, "T", _UrlKt.FRAGMENT_ENCODE_SET, "condition", _UrlKt.FRAGMENT_ENCODE_SET, "element", "(Ljava/util/List;ZLjava/lang/Object;)V", "TMessagesProj"}, k = 2, mv = {2, 2, 0}, xi = 48)
public abstract class ExtensionsKt {
    public static final <T> void addIf(List<T> list, boolean z, T t) {
        if (z) {
            list.add(t);
        }
    }
}
