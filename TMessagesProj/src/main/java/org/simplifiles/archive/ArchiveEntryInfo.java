package org.simplifiles.archive;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0016\b\u0086\b\u0018\u00002\u00020\u0001B9\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0002\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\u000b\u001a\u00020\n¢\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0002HÖ\u0001¢\u0006\u0004\b\u000e\u0010\u000fJ\u0010\u0010\u0010\u001a\u00020\nHÖ\u0001¢\u0006\u0004\b\u0010\u0010\u0011J\u001a\u0010\u0013\u001a\u00020\u00052\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b\u0013\u0010\u0014R\u0017\u0010\u0003\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u0015\u001a\u0004\b\u0016\u0010\u000fR\u0019\u0010\u0004\u001a\u0004\u0018\u00010\u00028\u0006¢\u0006\f\n\u0004\b\u0004\u0010\u0015\u001a\u0004\b\u0017\u0010\u000fR\u0017\u0010\u0006\u001a\u00020\u00058\u0006¢\u0006\f\n\u0004\b\u0006\u0010\u0018\u001a\u0004\b\u0006\u0010\u0019R\u0017\u0010\b\u001a\u00020\u00078\u0006¢\u0006\f\n\u0004\b\b\u0010\u001a\u001a\u0004\b\u001b\u0010\u001cR\u0017\u0010\t\u001a\u00020\u00078\u0006¢\u0006\f\n\u0004\b\t\u0010\u001a\u001a\u0004\b\u001d\u0010\u001cR\u0017\u0010\u000b\u001a\u00020\n8\u0006¢\u0006\f\n\u0004\b\u000b\u0010\u001e\u001a\u0004\b\u001f\u0010\u0011¨\u0006 "}, d2 = {"Lorg/simplifiles/archive/ArchiveEntryInfo;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "path", "normalizedPath", _UrlKt.FRAGMENT_ENCODE_SET, "isDirectory", _UrlKt.FRAGMENT_ENCODE_SET, "compressedSize", "uncompressedSize", _UrlKt.FRAGMENT_ENCODE_SET, "compressionMethod", "<init>", "(Ljava/lang/String;Ljava/lang/String;ZJJI)V", "toString", "()Ljava/lang/String;", "hashCode", "()I", "other", "equals", "(Ljava/lang/Object;)Z", "Ljava/lang/String;", "getPath", "getNormalizedPath", "Z", "()Z", "J", "getCompressedSize", "()J", "getUncompressedSize", "I", "getCompressionMethod", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final /* data */ class ArchiveEntryInfo {
    private final long compressedSize;
    private final int compressionMethod;
    private final boolean isDirectory;
    private final String normalizedPath;
    private final String path;
    private final long uncompressedSize;

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ArchiveEntryInfo)) {
            return false;
        }
        ArchiveEntryInfo archiveEntryInfo = (ArchiveEntryInfo) other;
        return Intrinsics.areEqual(this.path, archiveEntryInfo.path) && Intrinsics.areEqual(this.normalizedPath, archiveEntryInfo.normalizedPath) && this.isDirectory == archiveEntryInfo.isDirectory && this.compressedSize == archiveEntryInfo.compressedSize && this.uncompressedSize == archiveEntryInfo.uncompressedSize && this.compressionMethod == archiveEntryInfo.compressionMethod;
    }

    public int hashCode() {
        int iHashCode = this.path.hashCode() * 31;
        String str = this.normalizedPath;
        return ((((((((iHashCode + (str == null ? 0 : str.hashCode())) * 31) + Boolean.hashCode(this.isDirectory)) * 31) + Long.hashCode(this.compressedSize)) * 31) + Long.hashCode(this.uncompressedSize)) * 31) + Integer.hashCode(this.compressionMethod);
    }

    public String toString() {
        return "ArchiveEntryInfo(path=" + this.path + ", normalizedPath=" + this.normalizedPath + ", isDirectory=" + this.isDirectory + ", compressedSize=" + this.compressedSize + ", uncompressedSize=" + this.uncompressedSize + ", compressionMethod=" + this.compressionMethod + ')';
    }

    public ArchiveEntryInfo(String str, String str2, boolean z, long j, long j2, int i) {
        this.path = str;
        this.normalizedPath = str2;
        this.isDirectory = z;
        this.compressedSize = j;
        this.uncompressedSize = j2;
        this.compressionMethod = i;
    }

    public final String getPath() {
        return this.path;
    }

    public final boolean getIsDirectory() {
        return this.isDirectory;
    }

    public final long getCompressedSize() {
        return this.compressedSize;
    }

    public final long getUncompressedSize() {
        return this.uncompressedSize;
    }

    public final int getCompressionMethod() {
        return this.compressionMethod;
    }
}
