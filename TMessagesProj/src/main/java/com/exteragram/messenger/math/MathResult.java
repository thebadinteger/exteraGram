package com.exteragram.messenger.math;

import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\f\n\u0002\b\t\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u0010"}, d2 = {"Lcom/exteragram/messenger/math/MathResult;", _UrlKt.FRAGMENT_ENCODE_SET, "value", _UrlKt.FRAGMENT_ENCODE_SET, "hasOperation", _UrlKt.FRAGMENT_ENCODE_SET, "decimalSeparator", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(DZC)V", "getValue", "()D", "getHasOperation", "()Z", "getDecimalSeparator", "()C", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class MathResult {
    private final char decimalSeparator;
    private final boolean hasOperation;
    private final double value;

    public MathResult(double d, boolean z, char c2) {
        this.value = d;
        this.hasOperation = z;
        this.decimalSeparator = c2;
    }

    public final char getDecimalSeparator() {
        return this.decimalSeparator;
    }

    public final boolean getHasOperation() {
        return this.hasOperation;
    }

    public final double getValue() {
        return this.value;
    }
}
