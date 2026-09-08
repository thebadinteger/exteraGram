package com.exteragram.messenger.export.api;

import com.android.tools.r8.RecordTag;
import com.exteragram.messenger.ai.network.Client$ImagePayload$$ExternalSyntheticRecord1;
import org.telegram.messenger.NotificationBadge;

public final class ApiWrap$UserpicsInfo extends RecordTag {
    private final int count;

    private /* synthetic */ boolean $record$equals(Object obj) {
        return (obj instanceof ApiWrap$UserpicsInfo) && this.count == ((ApiWrap$UserpicsInfo) obj).count;
    }

    private /* synthetic */ Object[] $record$getFieldsAsObjects() {
        return new Object[]{Integer.valueOf(this.count)};
    }

    public ApiWrap$UserpicsInfo(int i) {
        this.count = i;
    }

    public int count() {
        return this.count;
    }

    public final boolean equals(Object obj) {
        return $record$equals(obj);
    }

    public final int hashCode() {
        return ApiWrap$UserpicsInfo$$ExternalSyntheticRecord0.m(this.count);
    }

    public final String toString() {
        return Client$ImagePayload$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), ApiWrap$UserpicsInfo.class, NotificationBadge.NewHtcHomeBadger.COUNT);
    }
}
