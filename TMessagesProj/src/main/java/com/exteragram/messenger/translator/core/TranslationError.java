package com.exteragram.messenger.translator.core;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0005\u0007\b\t\n\u000bB\t\b\u0004¢\u0006\u0004\b\u0002\u0010\u0003R\u0011\u0010\u0004\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0006\u0082\u0001\u0005\f\r\u000e\u000f\u0010¨\u0006\u0011"}, d2 = {"Lcom/exteragram/messenger/translator/core/TranslationError;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "isRetryable", _UrlKt.FRAGMENT_ENCODE_SET, "()Z", "RateLimited", "Transient", "LanguageUnsupported", "Fatal", "Cancelled", "Lcom/exteragram/messenger/translator/core/TranslationError$Cancelled;", "Lcom/exteragram/messenger/translator/core/TranslationError$Fatal;", "Lcom/exteragram/messenger/translator/core/TranslationError$LanguageUnsupported;", "Lcom/exteragram/messenger/translator/core/TranslationError$RateLimited;", "Lcom/exteragram/messenger/translator/core/TranslationError$Transient;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public abstract class TranslationError {
    public /* synthetic */ TranslationError(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    private TranslationError() {
    }

    @Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u0086\b\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0007\u001a\u00020\u0006HÖ\u0001¢\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\n\u001a\u00020\tHÖ\u0001¢\u0006\u0004\b\n\u0010\u000bJ\u001a\u0010\u000f\u001a\u00020\u000e2\b\u0010\r\u001a\u0004\u0018\u00010\fHÖ\u0003¢\u0006\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0003\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u0011\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u0014"}, d2 = {"Lcom/exteragram/messenger/translator/core/TranslationError$RateLimited;", "Lcom/exteragram/messenger/translator/core/TranslationError;", _UrlKt.FRAGMENT_ENCODE_SET, "retryAfterMs", "<init>", "(J)V", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "()Ljava/lang/String;", _UrlKt.FRAGMENT_ENCODE_SET, "hashCode", "()I", _UrlKt.FRAGMENT_ENCODE_SET, "other", _UrlKt.FRAGMENT_ENCODE_SET, "equals", "(Ljava/lang/Object;)Z", "J", "getRetryAfterMs", "()J", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final /* data */ class RateLimited extends TranslationError {
        private final long retryAfterMs;

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            return (other instanceof RateLimited) && this.retryAfterMs == ((RateLimited) other).retryAfterMs;
        }

        public int hashCode() {
            return Long.hashCode(this.retryAfterMs);
        }

        public String toString() {
            return "RateLimited(retryAfterMs=" + this.retryAfterMs + ")";
        }

        public RateLimited(long j) {
            super(null);
            this.retryAfterMs = j;
        }

        public final long getRetryAfterMs() {
            return this.retryAfterMs;
        }
    }

    @Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\bÆ\n\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0013\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007HÖ\u0003J\t\u0010\b\u001a\u00020\tHÖ\u0001J\t\u0010\n\u001a\u00020\u000bHÖ\u0001¨\u0006\f"}, d2 = {"Lcom/exteragram/messenger/translator/core/TranslationError$Transient;", "Lcom/exteragram/messenger/translator/core/TranslationError;", "<init>", "()V", "equals", _UrlKt.FRAGMENT_ENCODE_SET, "other", _UrlKt.FRAGMENT_ENCODE_SET, "hashCode", _UrlKt.FRAGMENT_ENCODE_SET, "toString", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final /* data */ class Transient extends TranslationError {
        public static final Transient INSTANCE = new Transient();

        public boolean equals(Object other) {
            return this == other || (other instanceof Transient);
        }

        public int hashCode() {
            return -897274016;
        }

        public String toString() {
            return "Transient";
        }

        private Transient() {
            super(null);
        }
    }

    @Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\bÆ\n\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0013\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007HÖ\u0003J\t\u0010\b\u001a\u00020\tHÖ\u0001J\t\u0010\n\u001a\u00020\u000bHÖ\u0001¨\u0006\f"}, d2 = {"Lcom/exteragram/messenger/translator/core/TranslationError$LanguageUnsupported;", "Lcom/exteragram/messenger/translator/core/TranslationError;", "<init>", "()V", "equals", _UrlKt.FRAGMENT_ENCODE_SET, "other", _UrlKt.FRAGMENT_ENCODE_SET, "hashCode", _UrlKt.FRAGMENT_ENCODE_SET, "toString", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final /* data */ class LanguageUnsupported extends TranslationError {
        public static final LanguageUnsupported INSTANCE = new LanguageUnsupported();

        public boolean equals(Object other) {
            return this == other || (other instanceof LanguageUnsupported);
        }

        public int hashCode() {
            return 1062709523;
        }

        public String toString() {
            return "LanguageUnsupported";
        }

        private LanguageUnsupported() {
            super(null);
        }
    }

    @Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\bÆ\n\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0013\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007HÖ\u0003J\t\u0010\b\u001a\u00020\tHÖ\u0001J\t\u0010\n\u001a\u00020\u000bHÖ\u0001¨\u0006\f"}, d2 = {"Lcom/exteragram/messenger/translator/core/TranslationError$Fatal;", "Lcom/exteragram/messenger/translator/core/TranslationError;", "<init>", "()V", "equals", _UrlKt.FRAGMENT_ENCODE_SET, "other", _UrlKt.FRAGMENT_ENCODE_SET, "hashCode", _UrlKt.FRAGMENT_ENCODE_SET, "toString", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final /* data */ class Fatal extends TranslationError {
        public static final Fatal INSTANCE = new Fatal();

        public boolean equals(Object other) {
            return this == other || (other instanceof Fatal);
        }

        public int hashCode() {
            return 1922309338;
        }

        public String toString() {
            return "Fatal";
        }

        private Fatal() {
            super(null);
        }
    }

    @Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\bÆ\n\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0013\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007HÖ\u0003J\t\u0010\b\u001a\u00020\tHÖ\u0001J\t\u0010\n\u001a\u00020\u000bHÖ\u0001¨\u0006\f"}, d2 = {"Lcom/exteragram/messenger/translator/core/TranslationError$Cancelled;", "Lcom/exteragram/messenger/translator/core/TranslationError;", "<init>", "()V", "equals", _UrlKt.FRAGMENT_ENCODE_SET, "other", _UrlKt.FRAGMENT_ENCODE_SET, "hashCode", _UrlKt.FRAGMENT_ENCODE_SET, "toString", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final /* data */ class Cancelled extends TranslationError {
        public static final Cancelled INSTANCE = new Cancelled();

        public boolean equals(Object other) {
            return this == other || (other instanceof Cancelled);
        }

        public int hashCode() {
            return -1473432025;
        }

        public String toString() {
            return "Cancelled";
        }

        private Cancelled() {
            super(null);
        }
    }

    public final boolean isRetryable() {
        return (this instanceof RateLimited) || (this instanceof Transient);
    }
}
