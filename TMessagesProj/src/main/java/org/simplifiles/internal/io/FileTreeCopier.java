package org.simplifiles.internal.io;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.path.DirectoryEntriesReader$$ExternalSyntheticApiModelOutline0;
import kotlin.jdk7.AutoCloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.sequences.SequencesKt;
import kotlin.streams.jdk8.StreamsKt;
import okhttp3.internal.url._UrlKt;
import org.simplifiles.exception.FileOperationException;
import org.simplifiles.files.DirectoryTransferOptions;

@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ \u0010\n\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u000b\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\t¨\u0006\f"}, d2 = {"Lorg/simplifiles/internal/io/FileTreeCopier;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "validateDirectory", _UrlKt.FRAGMENT_ENCODE_SET, "source", "Ljava/nio/file/Path;", "options", "Lorg/simplifiles/files/DirectoryTransferOptions;", "copyDirectory", "target", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nFileTreeCopier.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileTreeCopier.kt\norg/simplifiles/internal/io/FileTreeCopier\n+ 2 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,65:1\n1342#2,2:66\n614#2:68\n1342#2,2:69\n*S KotlinDebug\n*F\n+ 1 FileTreeCopier.kt\norg/simplifiles/internal/io/FileTreeCopier\n*L\n21#1:66,2\n44#1:68\n45#1:69,2\n*E\n"})
public final class FileTreeCopier {
    public static final FileTreeCopier INSTANCE = new FileTreeCopier();

    private FileTreeCopier() {
    }

    public final void validateDirectory(Path source, DirectoryTransferOptions options) throws Exception {
        Stream<Path> streamWalk = Files.walk(source, new FileVisitOption[0]);
        try {
            Iterator it = SequencesKt.filter(StreamsKt.asSequence(streamWalk), new Function1() { // from class: org.simplifiles.internal.io.FileTreeCopier$$ExternalSyntheticLambda0
                @Override // kotlin.jvm.functions.Function1
                public final Object invoke(Object obj) {
                    return Boolean.valueOf(FileTreeCopier.validateDirectory$lambda$0$0((Path) obj));
                }
            }).iterator();
            long j = 0;
            long size = 0;
            while (it.hasNext()) {
                Path pathM = (Path) it.next();
                j++;
                if (j > options.getMaxFiles()) {
                    throw new FileOperationException("Directory exceeds copy limit of " + options.getMaxFiles() + " files: " + source);
                }
                size += Files.size(pathM);
                if (size > options.getMaxBytes()) {
                    throw new FileOperationException("Directory exceeds copy limit of " + options.getMaxBytes() + " bytes: " + source);
                }
            }
            Unit unit = Unit.INSTANCE;
            AutoCloseableKt.closeFinally(streamWalk, null);
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                AutoCloseableKt.closeFinally(streamWalk, th);
                throw th2;
            }
        }
    }

    public static final boolean validateDirectory$lambda$0$0(Path path) {
        return Files.isRegularFile(path, new LinkOption[0]);
    }

    public final void copyDirectory(Path source, Path target, DirectoryTransferOptions options) throws Exception {
        validateDirectory(source, options);
        Stream<Path> streamWalk = Files.walk(source, new FileVisitOption[0]);
        try {
            Iterator it = SequencesKt.sortedWith(StreamsKt.asSequence(streamWalk), new Comparator<Path>() { // from class: org.simplifiles.internal.io.FileTreeCopier$copyDirectory$lambda$0$$inlined$sortedBy$1
                @Override // java.util.Comparator
                public final int compare(Path t, Path t2) {
                    return ComparisonsKt.compareValues(Integer.valueOf(t.getNameCount()), Integer.valueOf(t2.getNameCount()));
                }
            }).iterator();
            while (it.hasNext()) {
                Path pathM = (Path) it.next();
                Path pathResolve = target.resolve(source.relativize(pathM));
                if (Files.isDirectory(pathM, new LinkOption[0])) {
                    if (Files.exists(pathResolve, new LinkOption[0]) && !Files.isDirectory(pathResolve, new LinkOption[0])) {
                        Files.deleteIfExists(pathResolve);
                    }
                    Files.createDirectories(pathResolve, new FileAttribute[0]);
                } else {
                    Files.createDirectories(pathResolve.getParent(), new FileAttribute[0]);
                    if (Files.isDirectory(pathResolve, new LinkOption[0])) {
                        FileTreeCleaner.INSTANCE.deleteRecursively(pathResolve);
                    }
                    Files.copy(pathM, pathResolve, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            Unit unit = Unit.INSTANCE;
            AutoCloseableKt.closeFinally(streamWalk, null);
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                AutoCloseableKt.closeFinally(streamWalk, th);
                throw th2;
            }
        }
    }
}
