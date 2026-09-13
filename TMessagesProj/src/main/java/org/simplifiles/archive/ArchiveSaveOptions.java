package org.simplifiles.archive;

import c.f$$ExternalSyntheticBUOutline1;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;
import org.simplifiles.files.OverwritePolicy;

@Metadata(d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0016\u0018\u0000 \u001f2\u00020\u0001:\u0002\u001f BG\b\u0007\u0012\n\b\u0002\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004\u0012\b\b\u0002\u0010\u0007\u001a\u00020\u0006\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\n\u001a\u00020\u0006\u0012\b\b\u0002\u0010\f\u001a\u00020\u000b¢\u0006\u0004\b\r\u0010\u000eR\u0017\u0010\u0005\u001a\u00020\u00048\u0006¢\u0006\f\n\u0004\b\u0005\u0010\u000f\u001a\u0004\b\u0010\u0010\u0011R\u0017\u0010\u0007\u001a\u00020\u00068\u0006¢\u0006\f\n\u0004\b\u0007\u0010\u0012\u001a\u0004\b\u0013\u0010\u0014R\u0017\u0010\t\u001a\u00020\b8\u0006¢\u0006\f\n\u0004\b\t\u0010\u0015\u001a\u0004\b\u0016\u0010\u0017R\u0017\u0010\n\u001a\u00020\u00068\u0006¢\u0006\f\n\u0004\b\n\u0010\u0012\u001a\u0004\b\u0018\u0010\u0014R\u0017\u0010\f\u001a\u00020\u000b8\u0006¢\u0006\f\n\u0004\b\f\u0010\u0019\u001a\u0004\b\u001a\u0010\u001bR\u0019\u0010\u0003\u001a\u0004\u0018\u00010\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u001c\u001a\u0004\b\u001d\u0010\u001e¨\u0006!"}, d2 = {"Lorg/simplifiles/archive/ArchiveSaveOptions;", _UrlKt.FRAGMENT_ENCODE_SET, "Lorg/simplifiles/archive/ArchiveSaveProgressListener;", "progressListener", "Lorg/simplifiles/archive/CancellationToken;", "cancellationToken", _UrlKt.FRAGMENT_ENCODE_SET, "bufferSize", "Lorg/simplifiles/files/OverwritePolicy;", "overwritePolicy", "compressionLevel", "Lorg/simplifiles/archive/ArchiveEntryFilter;", "entryFilter", "<init>", "(Lorg/simplifiles/archive/ArchiveSaveProgressListener;Lorg/simplifiles/archive/CancellationToken;ILorg/simplifiles/files/OverwritePolicy;ILorg/simplifiles/archive/ArchiveEntryFilter;)V", "Lorg/simplifiles/archive/CancellationToken;", "getCancellationToken", "()Lorg/simplifiles/archive/CancellationToken;", "I", "getBufferSize", "()I", "Lorg/simplifiles/files/OverwritePolicy;", "getOverwritePolicy", "()Lorg/simplifiles/files/OverwritePolicy;", "getCompressionLevel", "Lorg/simplifiles/archive/ArchiveEntryFilter;", "getEntryFilter", "()Lorg/simplifiles/archive/ArchiveEntryFilter;", "Lorg/simplifiles/archive/ArchiveSaveProgressListener;", "getProgressListener", "()Lorg/simplifiles/archive/ArchiveSaveProgressListener;", "Companion", "Builder", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nArchiveSaveOptions.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArchiveSaveOptions.kt\norg/simplifiles/archive/ArchiveSaveOptions\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,95:1\n1#2:96\n*E\n"})
public final class ArchiveSaveOptions {

    public static final Companion INSTANCE = new Companion(null);
    private final int bufferSize;
    private final CancellationToken cancellationToken;
    private final int compressionLevel;
    private final ArchiveEntryFilter entryFilter;
    private final OverwritePolicy overwritePolicy;

    public final ArchiveSaveProgressListener getProgressListener() {
        return null;
    }

    @JvmOverloads
    public ArchiveSaveOptions(ArchiveSaveProgressListener archiveSaveProgressListener, CancellationToken cancellationToken, int i, OverwritePolicy overwritePolicy, int i2, ArchiveEntryFilter archiveEntryFilter) {
        this.cancellationToken = cancellationToken;
        this.bufferSize = i;
        this.overwritePolicy = overwritePolicy;
        this.compressionLevel = i2;
        this.entryFilter = archiveEntryFilter;
        if (i <= 0) {
            f$$ExternalSyntheticBUOutline1.m("bufferSize must be positive.");
            throw null;
        }
        if (-1 > i2 || i2 >= 10) {
            f$$ExternalSyntheticBUOutline1.m("compressionLevel must be between -1 and 9.");
            throw null;
        }
    }

    public /* synthetic */ ArchiveSaveOptions(ArchiveSaveProgressListener archiveSaveProgressListener, CancellationToken cancellationToken, int i, OverwritePolicy overwritePolicy, int i2, ArchiveEntryFilter archiveEntryFilter, int i3, DefaultConstructorMarker defaultConstructorMarker) {
        this((i3 & 1) != 0 ? null : archiveSaveProgressListener, (i3 & 2) != 0 ? CancellationToken.INSTANCE.none() : cancellationToken, (i3 & 4) != 0 ? 65536 : i, (i3 & 8) != 0 ? OverwritePolicy.ERROR : overwritePolicy, (i3 & 16) != 0 ? -1 : i2, (i3 & 32) != 0 ? ArchiveEntryFilter.INSTANCE.includeAll() : archiveEntryFilter);
    }

    public final CancellationToken getCancellationToken() {
        return this.cancellationToken;
    }

    public final int getBufferSize() {
        return this.bufferSize;
    }

    public final OverwritePolicy getOverwritePolicy() {
        return this.overwritePolicy;
    }

    public final int getCompressionLevel() {
        return this.compressionLevel;
    }

    public final ArchiveEntryFilter getEntryFilter() {
        return this.entryFilter;
    }

    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\n\u001a\u00020\u000bH\u0007J\b\u0010\f\u001a\u00020\rH\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lorg/simplifiles/archive/ArchiveSaveOptions$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "DEFAULT_BUFFER_SIZE", _UrlKt.FRAGMENT_ENCODE_SET, "DEFAULT_COMPRESSION_LEVEL", "NO_COMPRESSION_LEVEL", "BEST_SPEED_LEVEL", "BEST_COMPRESSION_LEVEL", "defaults", "Lorg/simplifiles/archive/ArchiveSaveOptions;", "builder", "Lorg/simplifiles/archive/ArchiveSaveOptions$Builder;", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final ArchiveSaveOptions defaults() {
            return new ArchiveSaveOptions(null, null, 0, null, 0, null, 63, null);
        }

        @JvmStatic
        public final Builder builder() {
            return new Builder(null, 1, null);
        }
    }

    @Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0013\b\u0000\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002¢\u0006\u0004\b\u0004\u0010\u0005J\u0015\u0010\b\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\u0006¢\u0006\u0004\b\b\u0010\tJ\u0015\u0010\f\u001a\u00020\u00002\u0006\u0010\u000b\u001a\u00020\n¢\u0006\u0004\b\f\u0010\rJ\r\u0010\u000e\u001a\u00020\u0002¢\u0006\u0004\b\u000e\u0010\u000fR\u0016\u0010\u0011\u001a\u00020\u00108\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\u0011\u0010\u0012R\u0016\u0010\u0013\u001a\u00020\n8\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\u0013\u0010\u0014R\u0016\u0010\b\u001a\u00020\u00068\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\b\u0010\u0015R\u0016\u0010\f\u001a\u00020\n8\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\f\u0010\u0014R\u0016\u0010\u0017\u001a\u00020\u00168\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\u0017\u0010\u0018¨\u0006\u0019"}, d2 = {"Lorg/simplifiles/archive/ArchiveSaveOptions$Builder;", _UrlKt.FRAGMENT_ENCODE_SET, "Lorg/simplifiles/archive/ArchiveSaveOptions;", "options", "<init>", "(Lorg/simplifiles/archive/ArchiveSaveOptions;)V", "Lorg/simplifiles/files/OverwritePolicy;", "policy", "overwritePolicy", "(Lorg/simplifiles/files/OverwritePolicy;)Lorg/simplifiles/archive/ArchiveSaveOptions$Builder;", _UrlKt.FRAGMENT_ENCODE_SET, "value", "compressionLevel", "(I)Lorg/simplifiles/archive/ArchiveSaveOptions$Builder;", "build", "()Lorg/simplifiles/archive/ArchiveSaveOptions;", "Lorg/simplifiles/archive/CancellationToken;", "cancellationToken", "Lorg/simplifiles/archive/CancellationToken;", "bufferSize", "I", "Lorg/simplifiles/files/OverwritePolicy;", "Lorg/simplifiles/archive/ArchiveEntryFilter;", "entryFilter", "Lorg/simplifiles/archive/ArchiveEntryFilter;", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
    public static final class Builder {
        private int bufferSize;
        private CancellationToken cancellationToken;
        private int compressionLevel;
        private ArchiveEntryFilter entryFilter;
        private OverwritePolicy overwritePolicy;

        public Builder(ArchiveSaveOptions archiveSaveOptions) {
            archiveSaveOptions.getProgressListener();
            this.cancellationToken = archiveSaveOptions.getCancellationToken();
            this.bufferSize = archiveSaveOptions.getBufferSize();
            this.overwritePolicy = archiveSaveOptions.getOverwritePolicy();
            this.compressionLevel = archiveSaveOptions.getCompressionLevel();
            this.entryFilter = archiveSaveOptions.getEntryFilter();
        }

        public /* synthetic */ Builder(ArchiveSaveOptions archiveSaveOptions, int i, DefaultConstructorMarker defaultConstructorMarker) {
            this((i & 1) != 0 ? ArchiveSaveOptions.INSTANCE.defaults() : archiveSaveOptions);
        }

        public final Builder overwritePolicy(OverwritePolicy policy) {
            this.overwritePolicy = policy;
            return this;
        }

        public final Builder compressionLevel(int value) {
            this.compressionLevel = value;
            return this;
        }

        public final ArchiveSaveOptions build() {
            return new ArchiveSaveOptions(null, this.cancellationToken, this.bufferSize, this.overwritePolicy, this.compressionLevel, this.entryFilter);
        }
    }
}
