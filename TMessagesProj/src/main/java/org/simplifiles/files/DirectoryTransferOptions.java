package org.simplifiles.files;

import c.f$$ExternalSyntheticBUOutline1;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\r\u0018\u0000 \u00102\u00020\u0001:\u0001\u0010B'\b\u0007\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0004¢\u0006\u0004\b\u0007\u0010\bR\u0017\u0010\u0003\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010\t\u001a\u0004\b\n\u0010\u000bR\u0017\u0010\u0005\u001a\u00020\u00048\u0006¢\u0006\f\n\u0004\b\u0005\u0010\f\u001a\u0004\b\r\u0010\u000eR\u0017\u0010\u0006\u001a\u00020\u00048\u0006¢\u0006\f\n\u0004\b\u0006\u0010\f\u001a\u0004\b\u000f\u0010\u000e¨\u0006\u0011"}, d2 = {"Lorg/simplifiles/files/DirectoryTransferOptions;", _UrlKt.FRAGMENT_ENCODE_SET, "Lorg/simplifiles/files/DirectoryOverwritePolicy;", "overwritePolicy", _UrlKt.FRAGMENT_ENCODE_SET, "maxFiles", "maxBytes", "<init>", "(Lorg/simplifiles/files/DirectoryOverwritePolicy;JJ)V", "Lorg/simplifiles/files/DirectoryOverwritePolicy;", "getOverwritePolicy", "()Lorg/simplifiles/files/DirectoryOverwritePolicy;", "J", "getMaxFiles", "()J", "getMaxBytes", "Companion", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nDirectoryTransferOptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DirectoryTransferOptions.kt\norg/simplifiles/files/DirectoryTransferOptions\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,64:1\n1#2:65\n*E\n"})
public final class DirectoryTransferOptions {
    private final long maxBytes;
    private final long maxFiles;
    private final DirectoryOverwritePolicy overwritePolicy;

    @JvmOverloads
    public DirectoryTransferOptions(DirectoryOverwritePolicy directoryOverwritePolicy, long j, long j2) {
        this.overwritePolicy = directoryOverwritePolicy;
        this.maxFiles = j;
        this.maxBytes = j2;
        if (j < 0) {
            f$$ExternalSyntheticBUOutline1.m("maxFiles must not be negative.");
            throw null;
        }
        if (j2 >= 0) {
            return;
        }
        f$$ExternalSyntheticBUOutline1.m("maxBytes must not be negative.");
        throw null;
    }

    public /* synthetic */ DirectoryTransferOptions(DirectoryOverwritePolicy directoryOverwritePolicy, long j, long j2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? DirectoryOverwritePolicy.ERROR : directoryOverwritePolicy, (i & 2) != 0 ? Long.MAX_VALUE : j, (i & 4) != 0 ? Long.MAX_VALUE : j2);
    }

    public final DirectoryOverwritePolicy getOverwritePolicy() {
        return this.overwritePolicy;
    }

    public final long getMaxFiles() {
        return this.maxFiles;
    }

    public final long getMaxBytes() {
        return this.maxBytes;
    }
}
