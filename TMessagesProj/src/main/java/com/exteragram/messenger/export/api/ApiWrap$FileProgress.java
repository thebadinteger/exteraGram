package com.exteragram.messenger.export.api;

import com.android.tools.r8.RecordTag;
import com.exteragram.messenger.ai.network.Client$ImagePayload$$ExternalSyntheticRecord1;

public final class ApiWrap$FileProgress extends RecordTag {
    private final long ready;
    private final long total;

    private /* synthetic */ boolean $record$equals(Object obj) {
        if (!(obj instanceof ApiWrap$FileProgress)) {
            return false;
        }
        ApiWrap$FileProgress apiWrap$FileProgress = (ApiWrap$FileProgress) obj;
        return this.ready == apiWrap$FileProgress.ready && this.total == apiWrap$FileProgress.total;
    }

    private /* synthetic */ Object[] $record$getFieldsAsObjects() {
        return new Object[]{Long.valueOf(this.ready), Long.valueOf(this.total)};
    }

    public ApiWrap$FileProgress(long j, long j2) {
        this.ready = j;
        this.total = j2;
    }

    public final boolean equals(Object obj) {
        return $record$equals(obj);
    }

    public final int hashCode() {
        return ApiWrap$FileProgress$$ExternalSyntheticRecord0.m(this.ready, this.total);
    }

    public long ready() {
        return this.ready;
    }

    public final String toString() {
        return Client$ImagePayload$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), ApiWrap$FileProgress.class, "ready;total");
    }

    public long total() {
        return this.total;
    }
}
