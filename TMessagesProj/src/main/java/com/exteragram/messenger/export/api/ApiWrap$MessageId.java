package com.exteragram.messenger.export.api;

import java.util.Objects;
import okhttp3.internal.url._UrlKt;

public class ApiWrap$MessageId {
    public String didAndMsgId = _UrlKt.FRAGMENT_ENCODE_SET;

    public int hashCode() {
        return this.didAndMsgId.hashCode();
    }

    public boolean equals(Object obj) {
        if (obj instanceof ApiWrap$MessageId) {
            return Objects.equals(((ApiWrap$MessageId) obj).didAndMsgId, this.didAndMsgId);
        }
        return false;
    }
}
