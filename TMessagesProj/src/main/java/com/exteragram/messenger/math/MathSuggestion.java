package com.exteragram.messenger.math;

import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\f¨\u0006\u000e"}, d2 = {"Lcom/exteragram/messenger/math/MathSuggestion;", _UrlKt.FRAGMENT_ENCODE_SET, "insertAt", _UrlKt.FRAGMENT_ENCODE_SET, "insertText", _UrlKt.FRAGMENT_ENCODE_SET, "value", "<init>", "(ILjava/lang/String;Ljava/lang/String;)V", "getInsertAt", "()I", "getInsertText", "()Ljava/lang/String;", "getValue", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class MathSuggestion {
    private final int insertAt;
    private final String insertText;
    private final String value;

    public MathSuggestion(int i, String str, String str2) {
        this.insertAt = i;
        this.insertText = str;
        this.value = str2;
    }

    public final int getInsertAt() {
        return this.insertAt;
    }

    public final String getInsertText() {
        return this.insertText;
    }

    public final String getValue() {
        return this.value;
    }
}
