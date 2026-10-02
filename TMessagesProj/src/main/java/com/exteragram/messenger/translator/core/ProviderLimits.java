package com.exteragram.messenger.translator.core;

import com.google.android.gms.cast.framework.media.NotificationOptions;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import okhttp3.internal.url._UrlKt;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u000f\b\u0086\b\u0018\u0000 \"2\u00020\u0001:\u0001\"BG\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0002\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0002\u0012\u0006\u0010\n\u001a\u00020\u0007\u0012\u0006\u0010\u000b\u001a\u00020\u0007¢\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000f\u001a\u00020\u000eHÖ\u0001¢\u0006\u0004\b\u000f\u0010\u0010J\u0010\u0010\u0011\u001a\u00020\u0002HÖ\u0001¢\u0006\u0004\b\u0011\u0010\u0012J\u001a\u0010\u0015\u001a\u00020\u00142\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b\u0015\u0010\u0016R\u0017\u0010\u0003\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u0017\u001a\u0004\b\u0018\u0010\u0012R\u0017\u0010\u0004\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0004\u0010\u0017\u001a\u0004\b\u0019\u0010\u0012R\u0017\u0010\u0005\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0005\u0010\u0017\u001a\u0004\b\u001a\u0010\u0012R\u0017\u0010\u0006\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0006\u0010\u0017\u001a\u0004\b\u001b\u0010\u0012R\u0017\u0010\b\u001a\u00020\u00078\u0006¢\u0006\f\n\u0004\b\b\u0010\u001c\u001a\u0004\b\u001d\u0010\u001eR\u0017\u0010\t\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\t\u0010\u0017\u001a\u0004\b\u001f\u0010\u0012R\u0017\u0010\n\u001a\u00020\u00078\u0006¢\u0006\f\n\u0004\b\n\u0010\u001c\u001a\u0004\b \u0010\u001eR\u0017\u0010\u000b\u001a\u00020\u00078\u0006¢\u0006\f\n\u0004\b\u000b\u0010\u001c\u001a\u0004\b!\u0010\u001e¨\u0006#"}, d2 = {"Lcom/exteragram/messenger/translator/core/ProviderLimits;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "maxTextsPerRequest", "maxCharsPerText", "maxCharsPerRequest", "maxConcurrent", _UrlKt.FRAGMENT_ENCODE_SET, "minIntervalMs", "maxAttempts", "baseBackoffMs", "maxBackoffMs", "<init>", "(IIIIJIJJ)V", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "()Ljava/lang/String;", "hashCode", "()I", "other", _UrlKt.FRAGMENT_ENCODE_SET, "equals", "(Ljava/lang/Object;)Z", "I", "getMaxTextsPerRequest", "getMaxCharsPerText", "getMaxCharsPerRequest", "getMaxConcurrent", "J", "getMinIntervalMs", "()J", "getMaxAttempts", "getBaseBackoffMs", "getMaxBackoffMs", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final /* data */ class ProviderLimits {
    private final long baseBackoffMs;
    private final int maxAttempts;
    private final long maxBackoffMs;
    private final int maxCharsPerRequest;
    private final int maxCharsPerText;
    private final int maxConcurrent;
    private final int maxTextsPerRequest;
    private final long minIntervalMs;

    @JvmField
    public static final ProviderLimits GOOGLE = new ProviderLimits(20, 5000, 8000, 2, 0, 4, 1000, NotificationOptions.SKIP_STEP_THIRTY_SECONDS_IN_MS);

    @JvmField
    public static final ProviderLimits YANDEX = new ProviderLimits(20, 5000, 5000, 2, 0, 4, 1000, NotificationOptions.SKIP_STEP_THIRTY_SECONDS_IN_MS);

    @JvmField
    public static final ProviderLimits MICROSOFT = new ProviderLimits(20, 5000, 10000, 4, 0, 4, 1000, NotificationOptions.SKIP_STEP_THIRTY_SECONDS_IN_MS);

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ProviderLimits)) {
            return false;
        }
        ProviderLimits providerLimits = (ProviderLimits) other;
        return this.maxTextsPerRequest == providerLimits.maxTextsPerRequest && this.maxCharsPerText == providerLimits.maxCharsPerText && this.maxCharsPerRequest == providerLimits.maxCharsPerRequest && this.maxConcurrent == providerLimits.maxConcurrent && this.minIntervalMs == providerLimits.minIntervalMs && this.maxAttempts == providerLimits.maxAttempts && this.baseBackoffMs == providerLimits.baseBackoffMs && this.maxBackoffMs == providerLimits.maxBackoffMs;
    }

    public int hashCode() {
        return (((((((((((((Integer.hashCode(this.maxTextsPerRequest) * 31) + Integer.hashCode(this.maxCharsPerText)) * 31) + Integer.hashCode(this.maxCharsPerRequest)) * 31) + Integer.hashCode(this.maxConcurrent)) * 31) + Long.hashCode(this.minIntervalMs)) * 31) + Integer.hashCode(this.maxAttempts)) * 31) + Long.hashCode(this.baseBackoffMs)) * 31) + Long.hashCode(this.maxBackoffMs);
    }

    public String toString() {
        return "ProviderLimits(maxTextsPerRequest=" + this.maxTextsPerRequest + ", maxCharsPerText=" + this.maxCharsPerText + ", maxCharsPerRequest=" + this.maxCharsPerRequest + ", maxConcurrent=" + this.maxConcurrent + ", minIntervalMs=" + this.minIntervalMs + ", maxAttempts=" + this.maxAttempts + ", baseBackoffMs=" + this.baseBackoffMs + ", maxBackoffMs=" + this.maxBackoffMs + ")";
    }

    public ProviderLimits(int i, int i2, int i3, int i4, long j, int i5, long j2, long j3) {
        this.maxTextsPerRequest = i;
        this.maxCharsPerText = i2;
        this.maxCharsPerRequest = i3;
        this.maxConcurrent = i4;
        this.minIntervalMs = j;
        this.maxAttempts = i5;
        this.baseBackoffMs = j2;
        this.maxBackoffMs = j3;
    }

    public final int getMaxTextsPerRequest() {
        return this.maxTextsPerRequest;
    }

    public final int getMaxCharsPerText() {
        return this.maxCharsPerText;
    }

    public final int getMaxCharsPerRequest() {
        return this.maxCharsPerRequest;
    }

    public final int getMaxConcurrent() {
        return this.maxConcurrent;
    }

    public final long getMinIntervalMs() {
        return this.minIntervalMs;
    }

    public final int getMaxAttempts() {
        return this.maxAttempts;
    }

    public final long getBaseBackoffMs() {
        return this.baseBackoffMs;
    }

    public final long getMaxBackoffMs() {
        return this.maxBackoffMs;
    }
}
