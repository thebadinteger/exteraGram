package com.exteragram.messenger.export.api;

import com.android.tools.r8.RecordTag;
import com.exteragram.messenger.ai.network.Client$ImagePayload$$ExternalSyntheticRecord1;
import java.util.Objects;
import okhttp3.internal.url._UrlKt;

public final class ApiWrap$DownloadProgress extends RecordTag {
    private final int itemIndex;
    private final String path;
    private final long randomId;
    private final long ready;
    private final long total;

    private /* synthetic */ boolean $record$equals(Object obj) {
        if (!(obj instanceof ApiWrap$DownloadProgress)) {
            return false;
        }
        ApiWrap$DownloadProgress apiWrap$DownloadProgress = (ApiWrap$DownloadProgress) obj;
        return this.itemIndex == apiWrap$DownloadProgress.itemIndex && this.randomId == apiWrap$DownloadProgress.randomId && this.ready == apiWrap$DownloadProgress.ready && this.total == apiWrap$DownloadProgress.total && Objects.equals(this.path, apiWrap$DownloadProgress.path);
    }

    private /* synthetic */ Object[] $record$getFieldsAsObjects() {
        return new Object[]{Long.valueOf(this.randomId), this.path, Integer.valueOf(this.itemIndex), Long.valueOf(this.ready), Long.valueOf(this.total)};
    }

    public ApiWrap$DownloadProgress(long j, String str, int i, long j2, long j3) {
        this.randomId = j;
        this.path = str;
        this.itemIndex = i;
        this.ready = j2;
        this.total = j3;
    }

    public final boolean equals(Object obj) {
        return $record$equals(obj);
    }

    public final int hashCode() {
        return ApiWrap$DownloadProgress$$ExternalSyntheticRecord0.m(this.itemIndex, this.randomId, this.ready, this.total, this.path);
    }

    public int itemIndex() {
        return this.itemIndex;
    }

    public String path() {
        return this.path;
    }

    public long randomId() {
        return this.randomId;
    }

    public long ready() {
        return this.ready;
    }

    public final String toString() {
        return Client$ImagePayload$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), ApiWrap$DownloadProgress.class, "randomId;path;itemIndex;ready;total");
    }

    public long total() {
        return this.total;
    }

    public ApiWrap$DownloadProgress() {
        this(0L, _UrlKt.FRAGMENT_ENCODE_SET, 0, 0L, 0L);
    }
}
