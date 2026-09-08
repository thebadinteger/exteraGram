package org.simplifiles.internal.io;

import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Stream;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.path.DirectoryEntriesReader$$ExternalSyntheticApiModelOutline0;
import kotlin.jdk7.AutoCloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.sequences.SequencesKt;
import kotlin.streams.jdk8.StreamsKt;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\t"}, d2 = {"Lorg/simplifiles/internal/io/FileTreeCleaner;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "deleteContents", _UrlKt.FRAGMENT_ENCODE_SET, "root", "Ljava/nio/file/Path;", "deleteRecursively", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nFileTreeCleaner.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FileTreeCleaner.kt\norg/simplifiles/internal/io/FileTreeCleaner\n+ 2 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,34:1\n628#2:35\n1342#2,2:36\n628#2:38\n1342#2,2:39\n*S KotlinDebug\n*F\n+ 1 FileTreeCleaner.kt\norg/simplifiles/internal/io/FileTreeCleaner\n*L\n17#1:35\n18#1:36,2\n29#1:38\n30#1:39,2\n*E\n"})
public final class FileTreeCleaner {
    public static final FileTreeCleaner INSTANCE = new FileTreeCleaner();

    private FileTreeCleaner() {
    }

    public final void deleteContents(final Path root) throws Exception {
        if (Files.exists(root, (LinkOption[]) Arrays.copyOf(new LinkOption[0], 0))) {
            Stream<Path> streamWalk = Files.walk(root, new FileVisitOption[0]);
            try {
                Iterator it = SequencesKt.sortedWith(SequencesKt.filter(StreamsKt.asSequence(streamWalk), new Function1() { // from class: org.simplifiles.internal.io.FileTreeCleaner$$ExternalSyntheticLambda0
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return Boolean.valueOf(FileTreeCleaner.deleteContents$lambda$0$0(root, (Path) obj));
                    }
                }), new Comparator() { // from class: org.simplifiles.internal.io.FileTreeCleaner$deleteContents$lambda$0$$inlined$sortedByDescending$1
                    @Override // java.util.Comparator
                    public final int compare(T t, T t2) {
                        return ComparisonsKt.compareValues(Integer.valueOf(DirectoryEntriesReader$$ExternalSyntheticApiModelOutline0.m(t2).getNameCount()), Integer.valueOf(DirectoryEntriesReader$$ExternalSyntheticApiModelOutline0.m(t).getNameCount()));
                    }
                }).iterator();
                while (it.hasNext()) {
                    Files.deleteIfExists(DirectoryEntriesReader$$ExternalSyntheticApiModelOutline0.m(it.next()));
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

    public static final boolean deleteContents$lambda$0$0(Path path, Path path2) {
        return !Intrinsics.areEqual(path2, path);
    }

    public final void deleteRecursively(Path root) throws Exception {
        if (Files.exists(root, (LinkOption[]) Arrays.copyOf(new LinkOption[0], 0))) {
            Stream<Path> streamWalk = Files.walk(root, new FileVisitOption[0]);
            try {
                Iterator it = SequencesKt.sortedWith(StreamsKt.asSequence(streamWalk), new Comparator() { // from class: org.simplifiles.internal.io.FileTreeCleaner$deleteRecursively$lambda$0$$inlined$sortedByDescending$1
                    @Override // java.util.Comparator
                    public final int compare(T t, T t2) {
                        return ComparisonsKt.compareValues(Integer.valueOf(DirectoryEntriesReader$$ExternalSyntheticApiModelOutline0.m(t2).getNameCount()), Integer.valueOf(DirectoryEntriesReader$$ExternalSyntheticApiModelOutline0.m(t).getNameCount()));
                    }
                }).iterator();
                while (it.hasNext()) {
                    Files.deleteIfExists(DirectoryEntriesReader$$ExternalSyntheticApiModelOutline0.m(it.next()));
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
}
