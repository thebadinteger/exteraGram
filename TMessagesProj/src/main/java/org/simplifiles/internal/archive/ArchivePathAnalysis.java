package org.simplifiles.internal.archive;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\b\n\u0002\b\u000b\b\u0080\b\u0018\u00002\u00020\u0001B)\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0004\u0012\u0006\u0010\u0007\u001a\u00020\u0004¢\u0006\u0004\b\b\u0010\tJ\u0010\u0010\n\u001a\u00020\u0002HÖ\u0001¢\u0006\u0004\b\n\u0010\u000bJ\u0010\u0010\r\u001a\u00020\fHÖ\u0001¢\u0006\u0004\b\r\u0010\u000eJ\u001a\u0010\u0010\u001a\u00020\u00042\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b\u0010\u0010\u0011R\u0019\u0010\u0003\u001a\u0004\u0018\u00010\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u0012\u001a\u0004\b\u0013\u0010\u000bR\u0017\u0010\u0005\u001a\u00020\u00048\u0006¢\u0006\f\n\u0004\b\u0005\u0010\u0014\u001a\u0004\b\u0005\u0010\u0015R\u0017\u0010\u0006\u001a\u00020\u00048\u0006¢\u0006\f\n\u0004\b\u0006\u0010\u0014\u001a\u0004\b\u0006\u0010\u0015R\u0017\u0010\u0007\u001a\u00020\u00048\u0006¢\u0006\f\n\u0004\b\u0007\u0010\u0014\u001a\u0004\b\u0016\u0010\u0015¨\u0006\u0017"}, d2 = {"Lorg/simplifiles/internal/archive/ArchivePathAnalysis;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "normalizedPath", _UrlKt.FRAGMENT_ENCODE_SET, "isEmpty", "isAbsolute", "containsParentTraversal", "<init>", "(Ljava/lang/String;ZZZ)V", "toString", "()Ljava/lang/String;", _UrlKt.FRAGMENT_ENCODE_SET, "hashCode", "()I", "other", "equals", "(Ljava/lang/Object;)Z", "Ljava/lang/String;", "getNormalizedPath", "Z", "()Z", "getContainsParentTraversal", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final /* data */ class ArchivePathAnalysis {
    private final boolean containsParentTraversal;
    private final boolean isAbsolute;
    private final boolean isEmpty;
    private final String normalizedPath;

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ArchivePathAnalysis)) {
            return false;
        }
        ArchivePathAnalysis archivePathAnalysis = (ArchivePathAnalysis) other;
        return Intrinsics.areEqual(this.normalizedPath, archivePathAnalysis.normalizedPath) && this.isEmpty == archivePathAnalysis.isEmpty && this.isAbsolute == archivePathAnalysis.isAbsolute && this.containsParentTraversal == archivePathAnalysis.containsParentTraversal;
    }

    public int hashCode() {
        String str = this.normalizedPath;
        return ((((((str == null ? 0 : str.hashCode()) * 31) + Boolean.hashCode(this.isEmpty)) * 31) + Boolean.hashCode(this.isAbsolute)) * 31) + Boolean.hashCode(this.containsParentTraversal);
    }

    public String toString() {
        return "ArchivePathAnalysis(normalizedPath=" + this.normalizedPath + ", isEmpty=" + this.isEmpty + ", isAbsolute=" + this.isAbsolute + ", containsParentTraversal=" + this.containsParentTraversal + ')';
    }

    public ArchivePathAnalysis(String str, boolean z, boolean z2, boolean z3) {
        this.normalizedPath = str;
        this.isEmpty = z;
        this.isAbsolute = z2;
        this.containsParentTraversal = z3;
    }

    public final String getNormalizedPath() {
        return this.normalizedPath;
    }

    public final boolean getIsEmpty() {
        return this.isEmpty;
    }

    public final boolean getIsAbsolute() {
        return this.isAbsolute;
    }

    public final boolean getContainsParentTraversal() {
        return this.containsParentTraversal;
    }
}
