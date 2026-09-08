package org.simplifiles.archive.security;

import c.f$$ExternalSyntheticBUOutline1;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0006\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u001c\b\u0086\b\u0018\u0000 ,2\u00020\u0001:\u0002,-Bc\b\u0007\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0006\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\u000b\u001a\u00020\n\u0012\b\b\u0002\u0010\f\u001a\u00020\n\u0012\b\b\u0002\u0010\r\u001a\u00020\n\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u000e¢\u0006\u0004\b\u0010\u0010\u0011J\u0010\u0010\u0013\u001a\u00020\u0012HÖ\u0001¢\u0006\u0004\b\u0013\u0010\u0014J\u0010\u0010\u0015\u001a\u00020\bHÖ\u0001¢\u0006\u0004\b\u0015\u0010\u0016J\u001a\u0010\u0018\u001a\u00020\n2\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b\u0018\u0010\u0019R\u0017\u0010\u0003\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u001a\u001a\u0004\b\u001b\u0010\u001cR\u0017\u0010\u0004\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0004\u0010\u001a\u001a\u0004\b\u001d\u0010\u001cR\u0017\u0010\u0005\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0005\u0010\u001a\u001a\u0004\b\u001e\u0010\u001cR\u0017\u0010\u0007\u001a\u00020\u00068\u0006¢\u0006\f\n\u0004\b\u0007\u0010\u001f\u001a\u0004\b \u0010!R\u0017\u0010\t\u001a\u00020\b8\u0006¢\u0006\f\n\u0004\b\t\u0010\"\u001a\u0004\b#\u0010\u0016R\u0017\u0010\u000b\u001a\u00020\n8\u0006¢\u0006\f\n\u0004\b\u000b\u0010$\u001a\u0004\b%\u0010&R\u0017\u0010\f\u001a\u00020\n8\u0006¢\u0006\f\n\u0004\b\f\u0010$\u001a\u0004\b'\u0010&R\u0017\u0010\r\u001a\u00020\n8\u0006¢\u0006\f\n\u0004\b\r\u0010$\u001a\u0004\b(\u0010&R\u0017\u0010\u000f\u001a\u00020\u000e8\u0006¢\u0006\f\n\u0004\b\u000f\u0010)\u001a\u0004\b*\u0010+¨\u0006."}, d2 = {"Lorg/simplifiles/archive/security/SecurityPolicy;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "maxEntries", "maxTotalUncompressedSize", "maxSingleFileSize", _UrlKt.FRAGMENT_ENCODE_SET, "maxCompressionRatio", _UrlKt.FRAGMENT_ENCODE_SET, "maxNestedArchiveDepth", _UrlKt.FRAGMENT_ENCODE_SET, "allowSymlinks", "allowHardlinks", "allowAbsolutePaths", "Lorg/simplifiles/archive/security/DuplicatePolicy;", "duplicatePolicy", "<init>", "(JJJDIZZZLorg/simplifiles/archive/security/DuplicatePolicy;)V", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "()Ljava/lang/String;", "hashCode", "()I", "other", "equals", "(Ljava/lang/Object;)Z", "J", "getMaxEntries", "()J", "getMaxTotalUncompressedSize", "getMaxSingleFileSize", "D", "getMaxCompressionRatio", "()D", "I", "getMaxNestedArchiveDepth", "Z", "getAllowSymlinks", "()Z", "getAllowHardlinks", "getAllowAbsolutePaths", "Lorg/simplifiles/archive/security/DuplicatePolicy;", "getDuplicatePolicy", "()Lorg/simplifiles/archive/security/DuplicatePolicy;", "Companion", "Builder", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nSecurityPolicy.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SecurityPolicy.kt\norg/simplifiles/archive/security/SecurityPolicy\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,111:1\n1#2:112\n*E\n"})
public final /* data */ class SecurityPolicy {

    public static final Companion INSTANCE = new Companion(null);
    private final boolean allowAbsolutePaths;
    private final boolean allowHardlinks;
    private final boolean allowSymlinks;
    private final DuplicatePolicy duplicatePolicy;
    private final double maxCompressionRatio;
    private final long maxEntries;
    private final int maxNestedArchiveDepth;
    private final long maxSingleFileSize;
    private final long maxTotalUncompressedSize;

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SecurityPolicy)) {
            return false;
        }
        SecurityPolicy securityPolicy = (SecurityPolicy) other;
        return this.maxEntries == securityPolicy.maxEntries && this.maxTotalUncompressedSize == securityPolicy.maxTotalUncompressedSize && this.maxSingleFileSize == securityPolicy.maxSingleFileSize && Double.compare(this.maxCompressionRatio, securityPolicy.maxCompressionRatio) == 0 && this.maxNestedArchiveDepth == securityPolicy.maxNestedArchiveDepth && this.allowSymlinks == securityPolicy.allowSymlinks && this.allowHardlinks == securityPolicy.allowHardlinks && this.allowAbsolutePaths == securityPolicy.allowAbsolutePaths && this.duplicatePolicy == securityPolicy.duplicatePolicy;
    }

    public int hashCode() {
        return (((((((((((((((Long.hashCode(this.maxEntries) * 31) + Long.hashCode(this.maxTotalUncompressedSize)) * 31) + Long.hashCode(this.maxSingleFileSize)) * 31) + Double.hashCode(this.maxCompressionRatio)) * 31) + Integer.hashCode(this.maxNestedArchiveDepth)) * 31) + Boolean.hashCode(this.allowSymlinks)) * 31) + Boolean.hashCode(this.allowHardlinks)) * 31) + Boolean.hashCode(this.allowAbsolutePaths)) * 31) + this.duplicatePolicy.hashCode();
    }

    public String toString() {
        return "SecurityPolicy(maxEntries=" + this.maxEntries + ", maxTotalUncompressedSize=" + this.maxTotalUncompressedSize + ", maxSingleFileSize=" + this.maxSingleFileSize + ", maxCompressionRatio=" + this.maxCompressionRatio + ", maxNestedArchiveDepth=" + this.maxNestedArchiveDepth + ", allowSymlinks=" + this.allowSymlinks + ", allowHardlinks=" + this.allowHardlinks + ", allowAbsolutePaths=" + this.allowAbsolutePaths + ", duplicatePolicy=" + this.duplicatePolicy + ')';
    }

    @JvmOverloads
    public SecurityPolicy(long j, long j2, long j3, double d, int i, boolean z, boolean z2, boolean z3, DuplicatePolicy duplicatePolicy) {
        this.maxEntries = j;
        this.maxTotalUncompressedSize = j2;
        this.maxSingleFileSize = j3;
        this.maxCompressionRatio = d;
        this.maxNestedArchiveDepth = i;
        this.allowSymlinks = z;
        this.allowHardlinks = z2;
        this.allowAbsolutePaths = z3;
        this.duplicatePolicy = duplicatePolicy;
        if (j <= 0) {
            f$$ExternalSyntheticBUOutline1.m("maxEntries must be positive.");
            throw null;
        }
        if (j2 <= 0) {
            f$$ExternalSyntheticBUOutline1.m("maxTotalUncompressedSize must be positive.");
            throw null;
        }
        if (j3 <= 0) {
            f$$ExternalSyntheticBUOutline1.m("maxSingleFileSize must be positive.");
            throw null;
        }
        if (d <= 0.0d) {
            f$$ExternalSyntheticBUOutline1.m("maxCompressionRatio must be positive.");
            throw null;
        }
        if (i >= 0) {
            return;
        }
        f$$ExternalSyntheticBUOutline1.m("maxNestedArchiveDepth must not be negative.");
        throw null;
    }

    public final long getMaxEntries() {
        return this.maxEntries;
    }

    public final long getMaxTotalUncompressedSize() {
        return this.maxTotalUncompressedSize;
    }

    public final long getMaxSingleFileSize() {
        return this.maxSingleFileSize;
    }

    public final double getMaxCompressionRatio() {
        return this.maxCompressionRatio;
    }

    public final int getMaxNestedArchiveDepth() {
        return this.maxNestedArchiveDepth;
    }

    public final boolean getAllowSymlinks() {
        return this.allowSymlinks;
    }

    public final boolean getAllowHardlinks() {
        return this.allowHardlinks;
    }

    public final boolean getAllowAbsolutePaths() {
        return this.allowAbsolutePaths;
    }

    public /* synthetic */ SecurityPolicy(long j, long j2, long j3, double d, int i, boolean z, boolean z2, boolean z3, DuplicatePolicy duplicatePolicy, int i2, DefaultConstructorMarker defaultConstructorMarker) {
        this((i2 & 1) != 0 ? 10000L : j, (i2 & 2) != 0 ? 1000000000L : j2, (i2 & 4) != 0 ? 100000000L : j3, (i2 & 8) != 0 ? 100.0d : d, (i2 & 16) != 0 ? 0 : i, (i2 & 32) != 0 ? false : z, (i2 & 64) != 0 ? false : z2, (i2 & 128) == 0 ? z3 : false, (i2 & 256) != 0 ? DuplicatePolicy.ERROR : duplicatePolicy);
    }

    public final DuplicatePolicy getDuplicatePolicy() {
        return this.duplicatePolicy;
    }

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H\u0007J\b\u0010\u0006\u001a\u00020\u0007H\u0007¨\u0006\b"}, d2 = {"Lorg/simplifiles/archive/security/SecurityPolicy$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "strict", "Lorg/simplifiles/archive/security/SecurityPolicy;", "builder", "Lorg/simplifiles/archive/security/SecurityPolicy$Builder;", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final SecurityPolicy strict() {
            return new SecurityPolicy(0L, 0L, 0L, 0.0d, 0, false, false, false, null, 511, null);
        }

        /* JADX WARN: Multi-variable type inference failed */
        @JvmStatic
        public final Builder builder() {
            return new Builder(null, 1, 0 == true ? 1 : 0);
        }
    }

    @Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0010\u0006\n\u0002\b\u0006\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0013\b\u0000\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002¢\u0006\u0004\b\u0004\u0010\u0005J\u0015\u0010\b\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\u0006¢\u0006\u0004\b\b\u0010\tJ\u0015\u0010\n\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\u0006¢\u0006\u0004\b\n\u0010\tJ\u0015\u0010\u000b\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\u0006¢\u0006\u0004\b\u000b\u0010\tJ\u0015\u0010\r\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eJ\r\u0010\u000f\u001a\u00020\u0002¢\u0006\u0004\b\u000f\u0010\u0010R\u0016\u0010\b\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\b\u0010\u0011R\u0016\u0010\n\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\n\u0010\u0011R\u0016\u0010\u000b\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\u000b\u0010\u0011R\u0016\u0010\r\u001a\u00020\f8\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\r\u0010\u0012R\u0016\u0010\u0014\u001a\u00020\u00138\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\u0014\u0010\u0015R\u0016\u0010\u0017\u001a\u00020\u00168\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\u0017\u0010\u0018R\u0016\u0010\u0019\u001a\u00020\u00168\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\u0019\u0010\u0018R\u0016\u0010\u001a\u001a\u00020\u00168\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\u001a\u0010\u0018R\u0016\u0010\u001c\u001a\u00020\u001b8\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\u001c\u0010\u001d¨\u0006\u001e"}, d2 = {"Lorg/simplifiles/archive/security/SecurityPolicy$Builder;", _UrlKt.FRAGMENT_ENCODE_SET, "Lorg/simplifiles/archive/security/SecurityPolicy;", "policy", "<init>", "(Lorg/simplifiles/archive/security/SecurityPolicy;)V", _UrlKt.FRAGMENT_ENCODE_SET, "value", "maxEntries", "(J)Lorg/simplifiles/archive/security/SecurityPolicy$Builder;", "maxTotalUncompressedSize", "maxSingleFileSize", _UrlKt.FRAGMENT_ENCODE_SET, "maxCompressionRatio", "(D)Lorg/simplifiles/archive/security/SecurityPolicy$Builder;", "build", "()Lorg/simplifiles/archive/security/SecurityPolicy;", "J", "D", _UrlKt.FRAGMENT_ENCODE_SET, "maxNestedArchiveDepth", "I", _UrlKt.FRAGMENT_ENCODE_SET, "allowSymlinks", "Z", "allowHardlinks", "allowAbsolutePaths", "Lorg/simplifiles/archive/security/DuplicatePolicy;", "duplicatePolicy", "Lorg/simplifiles/archive/security/DuplicatePolicy;", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
    public static final class Builder {
        private boolean allowAbsolutePaths;
        private boolean allowHardlinks;
        private boolean allowSymlinks;
        private DuplicatePolicy duplicatePolicy;
        private double maxCompressionRatio;
        private long maxEntries;
        private int maxNestedArchiveDepth;
        private long maxSingleFileSize;
        private long maxTotalUncompressedSize;

        public Builder(SecurityPolicy securityPolicy) {
            this.maxEntries = securityPolicy.getMaxEntries();
            this.maxTotalUncompressedSize = securityPolicy.getMaxTotalUncompressedSize();
            this.maxSingleFileSize = securityPolicy.getMaxSingleFileSize();
            this.maxCompressionRatio = securityPolicy.getMaxCompressionRatio();
            this.maxNestedArchiveDepth = securityPolicy.getMaxNestedArchiveDepth();
            this.allowSymlinks = securityPolicy.getAllowSymlinks();
            this.allowHardlinks = securityPolicy.getAllowHardlinks();
            this.allowAbsolutePaths = securityPolicy.getAllowAbsolutePaths();
            this.duplicatePolicy = securityPolicy.getDuplicatePolicy();
        }

        public /* synthetic */ Builder(SecurityPolicy securityPolicy, int i, DefaultConstructorMarker defaultConstructorMarker) {
            this((i & 1) != 0 ? SecurityPolicy.INSTANCE.strict() : securityPolicy);
        }

        public final Builder maxEntries(long value) {
            this.maxEntries = value;
            return this;
        }

        public final Builder maxTotalUncompressedSize(long value) {
            this.maxTotalUncompressedSize = value;
            return this;
        }

        public final Builder maxSingleFileSize(long value) {
            this.maxSingleFileSize = value;
            return this;
        }

        public final Builder maxCompressionRatio(double value) {
            this.maxCompressionRatio = value;
            return this;
        }

        public final SecurityPolicy build() {
            return new SecurityPolicy(this.maxEntries, this.maxTotalUncompressedSize, this.maxSingleFileSize, this.maxCompressionRatio, this.maxNestedArchiveDepth, this.allowSymlinks, this.allowHardlinks, this.allowAbsolutePaths, this.duplicatePolicy);
        }
    }
}
