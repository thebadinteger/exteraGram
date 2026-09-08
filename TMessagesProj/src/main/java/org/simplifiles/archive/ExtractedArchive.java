package org.simplifiles.archive;

import java.nio.file.Path;
import kotlin.Metadata;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.simplifiles.internal.archive.ArchivePathResolver;
import org.simplifiles.internal.io.FileTreeCleaner;

@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0007\u0018\u00002\u00060\u0001j\u0002`\u0002B\u0019\b\u0000\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bJ\u0015\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\t¢\u0006\u0004\b\f\u0010\rJ\u000f\u0010\u000f\u001a\u00020\u000eH\u0016¢\u0006\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0004\u001a\u00020\u00038\u0006¢\u0006\f\n\u0004\b\u0004\u0010\u0011\u001a\u0004\b\u0012\u0010\u0013R\u0014\u0010\u0006\u001a\u00020\u00058\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0006\u0010\u0014¨\u0006\u0015"}, d2 = {"Lorg/simplifiles/archive/ExtractedArchive;", "Ljava/lang/AutoCloseable;", "Lkotlin/AutoCloseable;", "Ljava/nio/file/Path;", "root", _UrlKt.FRAGMENT_ENCODE_SET, "cleanupOnClose", "<init>", "(Ljava/nio/file/Path;Z)V", _UrlKt.FRAGMENT_ENCODE_SET, "path", "Lorg/simplifiles/archive/ArchiveFile;", "file", "(Ljava/lang/String;)Lorg/simplifiles/archive/ArchiveFile;", _UrlKt.FRAGMENT_ENCODE_SET, "close", "()V", "Ljava/nio/file/Path;", "getRoot", "()Ljava/nio/file/Path;", "Z", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nExtractedArchive.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ExtractedArchive.kt\norg/simplifiles/archive/ExtractedArchive\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,149:1\n777#2:150\n873#2,2:151\n*S KotlinDebug\n*F\n+ 1 ExtractedArchive.kt\norg/simplifiles/archive/ExtractedArchive\n*L\n94#1:150\n94#1:151,2\n*E\n"})
public final class ExtractedArchive implements AutoCloseable {
    private final boolean cleanupOnClose;
    private final Path root;

    public ExtractedArchive(Path path, boolean z) {
        this.root = path;
        this.cleanupOnClose = z;
    }

    public final Path getRoot() {
        return this.root;
    }

    public final ArchiveFile file(String path) {
        Path pathResolve$default = ArchivePathResolver.resolve$default(ArchivePathResolver.INSTANCE, this.root, path, false, 4, null);
        Path path2 = this.root;
        return new ArchiveFile(path2, StringsKt.replace$default(path2.relativize(pathResolve$default).toString(), '\\', '/', false, 4, (Object) null), pathResolve$default);
    }

    @Override // java.lang.AutoCloseable
    public void close() throws Exception {
        if (this.cleanupOnClose) {
            FileTreeCleaner.INSTANCE.deleteRecursively(this.root);
        }
    }
}
