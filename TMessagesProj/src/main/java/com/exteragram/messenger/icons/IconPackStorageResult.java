package com.exteragram.messenger.icons;

import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u0000*\u0006\b\u0000\u0010\u0001 \u00012\u00020\u0002:\u0002\u0005\u0006B\t\b\u0004¢\u0006\u0004\b\u0003\u0010\u0004\u0082\u0001\u0002\u0007\b¨\u0006\t"}, d2 = {"Lcom/exteragram/messenger/icons/IconPackStorageResult;", "T", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "Success", "Failure", "Lcom/exteragram/messenger/icons/IconPackStorageResult$Failure;", "Lcom/exteragram/messenger/icons/IconPackStorageResult$Success;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public abstract class IconPackStorageResult<T> {
    public /* synthetic */ IconPackStorageResult(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    @Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u0086\b\u0018\u0000*\u0006\b\u0001\u0010\u0001 \u00012\b\u0012\u0004\u0012\u00028\u00010\u0002B\u000f\u0012\u0006\u0010\u0003\u001a\u00028\u0001¢\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\u0007\u001a\u00020\u0006HÖ\u0001¢\u0006\u0004\b\u0007\u0010\bJ\u0010\u0010\n\u001a\u00020\tHÖ\u0001¢\u0006\u0004\b\n\u0010\u000bJ\u001a\u0010\u000f\u001a\u00020\u000e2\b\u0010\r\u001a\u0004\u0018\u00010\fHÖ\u0003¢\u0006\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0003\u001a\u00028\u00018\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u0011\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u0014"}, d2 = {"Lcom/exteragram/messenger/icons/IconPackStorageResult$Success;", "T", "Lcom/exteragram/messenger/icons/IconPackStorageResult;", "value", "<init>", "(Ljava/lang/Object;)V", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "()Ljava/lang/String;", _UrlKt.FRAGMENT_ENCODE_SET, "hashCode", "()I", _UrlKt.FRAGMENT_ENCODE_SET, "other", _UrlKt.FRAGMENT_ENCODE_SET, "equals", "(Ljava/lang/Object;)Z", "Ljava/lang/Object;", "getValue", "()Ljava/lang/Object;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final /* data */ class Success<T> extends IconPackStorageResult<T> {
        private final T value;

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            return (other instanceof Success) && Intrinsics.areEqual(this.value, ((Success) other).value);
        }

        public int hashCode() {
            T t = this.value;
            if (t == null) {
                return 0;
            }
            return t.hashCode();
        }

        public String toString() {
            return "Success(value=" + this.value + ')';
        }

        public Success(T t) {
            super(null);
            this.value = t;
        }

        public final T getValue() {
            return this.value;
        }
    }

    private IconPackStorageResult() {
    }

    @Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0001\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\b\u0086\b\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u000f\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u0010\u0010\b\u001a\u00020\u0007HÖ\u0001¢\u0006\u0004\b\b\u0010\tJ\u0010\u0010\u000b\u001a\u00020\nHÖ\u0001¢\u0006\u0004\b\u000b\u0010\fJ\u001a\u0010\u0010\u001a\u00020\u000f2\b\u0010\u000e\u001a\u0004\u0018\u00010\rHÖ\u0003¢\u0006\u0004\b\u0010\u0010\u0011R\u0017\u0010\u0004\u001a\u00020\u00038\u0006¢\u0006\f\n\u0004\b\u0004\u0010\u0012\u001a\u0004\b\u0013\u0010\u0014¨\u0006\u0015"}, d2 = {"Lcom/exteragram/messenger/icons/IconPackStorageResult$Failure;", "Lcom/exteragram/messenger/icons/IconPackStorageResult;", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/icons/IconPackStorageError;", "", "<init>", "(Lcom/exteragram/messenger/icons/IconPackStorageError;)V", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "()Ljava/lang/String;", _UrlKt.FRAGMENT_ENCODE_SET, "hashCode", "()I", _UrlKt.FRAGMENT_ENCODE_SET, "other", _UrlKt.FRAGMENT_ENCODE_SET, "equals", "(Ljava/lang/Object;)Z", "Lcom/exteragram/messenger/icons/IconPackStorageError;", "getError", "()Lcom/exteragram/messenger/icons/IconPackStorageError;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final /* data */ class Failure extends IconPackStorageResult {
        private final IconPackStorageError error;

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            return (other instanceof Failure) && this.error == ((Failure) other).error;
        }

        public int hashCode() {
            return this.error.hashCode();
        }

        public String toString() {
            return "Failure(error=" + this.error + ')';
        }

        public Failure(IconPackStorageError iconPackStorageError) {
            super(null);
            this.error = iconPackStorageError;
        }

        public final IconPackStorageError getError() {
            return this.error;
        }
    }
}
