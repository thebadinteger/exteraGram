package org.simplifiles.internal.archive.zip;

import java.nio.file.Path;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.sequences.SequencesKt;
import okhttp3.internal.url._UrlKt;
import org.simplifiles.archive.ArchiveEntryInfo;
import org.simplifiles.archive.ArchiveFormat;
import org.simplifiles.archive.ArchiveInspection;
import org.simplifiles.exception.CorruptedArchiveException;
import org.simplifiles.internal.archive.ArchivePathAnalyzer;

@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\b"}, d2 = {"Lorg/simplifiles/internal/archive/zip/ZipArchiveReader;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "inspect", "Lorg/simplifiles/archive/ArchiveInspection;", "path", "Ljava/nio/file/Path;", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final class ZipArchiveReader {
    public static final ZipArchiveReader INSTANCE = new ZipArchiveReader();

    private ZipArchiveReader() {
    }

    public final ArchiveInspection inspect(Path path) throws CorruptedArchiveException {
        try {
            ZipFile zipFile = new ZipFile(path.toFile());
            try {
                List list = SequencesKt.toList(SequencesKt.map(SequencesKt.asSequence(CollectionsKt.iterator(zipFile.entries())), new Function1() { // from class: org.simplifiles.internal.archive.zip.ZipArchiveReader$$ExternalSyntheticLambda0
                    @Override // kotlin.jvm.functions.Function1
                    public final Object invoke(Object obj) {
                        return ZipArchiveReader.inspect$lambda$0$0((ZipEntry) obj);
                    }
                }));
                CloseableKt.closeFinally(zipFile, null);
                return new ArchiveInspection(ArchiveFormat.ZIP, list);
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    CloseableKt.closeFinally(zipFile, th);
                    throw th2;
                }
            }
        } catch (ZipException e) {
            throw new CorruptedArchiveException(path, e);
        }
    }

    public static final ArchiveEntryInfo inspect$lambda$0$0(ZipEntry zipEntry) {
        return new ArchiveEntryInfo(zipEntry.getName(), ArchivePathAnalyzer.INSTANCE.analyze(zipEntry.getName()).getNormalizedPath(), zipEntry.isDirectory(), zipEntry.getCompressedSize(), zipEntry.getSize(), zipEntry.getMethod());
    }
}
