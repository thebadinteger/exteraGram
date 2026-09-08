package org.simplifiles.internal.archive.zip;

import androidx.room.BaseRoomConnectionManager$$ExternalSyntheticBUOutline0;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import kotlin.LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jdk7.AutoCloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.LongCompanionObject;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.sequences.SequencesKt;
import kotlin.text.StringsKt;
import kotlin.time.InstantKt$$ExternalSyntheticBUOutline0;
import okhttp3.internal.url._UrlKt;
import org.mvel2.asm.signature.SignatureVisitor;
import org.simplifiles.archive.ArchiveEntryInfo;
import org.simplifiles.archive.ArchiveExtractionOptions;
import org.simplifiles.archive.ArchiveFormat;
import org.simplifiles.archive.ArchiveIssue;
import org.simplifiles.archive.ArchiveIssueSeverity;
import org.simplifiles.archive.ExtractedArchive;
import org.simplifiles.archive.ExtractionTargetPolicy;
import org.simplifiles.archive.ValidationReport;
import org.simplifiles.archive.security.DuplicatePolicy;
import org.simplifiles.archive.security.SecurityPolicy;
import org.simplifiles.exception.ArchiveOperationCanceledException;
import org.simplifiles.exception.ArchiveValidationException;
import org.simplifiles.exception.ExtractionTargetException;
import org.simplifiles.internal.archive.ArchivePathAnalyzer;
import org.simplifiles.internal.archive.ArchivePathResolver;
import org.simplifiles.internal.io.FileTreeCleaner;

@Metadata(d1 = {"\u0000\u0082\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u0001\n\u0002\b\u0004\bÀ\u0002\u0018\u00002\u00020\u0001:\u00018B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J<\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\u0010\u001a\u00020\u0011J\u0018\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\u0016H\u0002J\u0010\u0010\u0017\u001a\u00020\f2\u0006\u0010\u0014\u001a\u00020\u0007H\u0002J0\u0010\u0018\u001a\u00020\u00132\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0014\u001a\u00020\u00072\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J(\u0010\u001d\u001a\u0004\u0018\u00010\u001e2\u0006\u0010\u001f\u001a\u00020 2\f\u0010!\u001a\b\u0012\u0004\u0012\u00020\u001e0\"2\u0006\u0010\t\u001a\u00020\nH\u0002J\u001e\u0010#\u001a\u00020\u001e2\u0006\u0010$\u001a\u00020\u001e2\f\u0010!\u001a\b\u0012\u0004\u0012\u00020\u001e0\"H\u0002JH\u0010%\u001a\u00020&2\u0006\u0010'\u001a\u00020(2\u0006\u0010)\u001a\u00020*2\u0006\u0010\u001f\u001a\u00020 2\u0006\u0010+\u001a\u00020&2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u0019\u001a\u00020\u001a2\u0006\u0010,\u001a\u00020\u001e2\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J\u0016\u0010-\u001a\u00020&2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000eH\u0002J(\u0010.\u001a\u00020&2\u0006\u0010/\u001a\u00020&2\u0006\u00100\u001a\u00020&2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010$\u001a\u00020\u001eH\u0002J(\u00101\u001a\u00020&2\u0006\u0010/\u001a\u00020&2\u0006\u00100\u001a\u00020&2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010$\u001a\u00020\u001eH\u0002J \u00102\u001a\u00020\u00132\u0006\u0010\u001f\u001a\u00020 2\u0006\u00103\u001a\u00020&2\u0006\u0010\t\u001a\u00020\nH\u0002J$\u00104\u001a\u0002052\u0006\u00106\u001a\u00020\u001e2\u0006\u00107\u001a\u00020\u001e2\n\b\u0002\u0010$\u001a\u0004\u0018\u00010\u001eH\u0002¨\u00069"}, d2 = {"Lorg/simplifiles/internal/archive/zip/ZipArchiveExtractor;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "extract", "Lorg/simplifiles/archive/ExtractedArchive;", "source", "Ljava/nio/file/Path;", "targetRoot", "policy", "Lorg/simplifiles/archive/security/SecurityPolicy;", "cleanupOnClose", _UrlKt.FRAGMENT_ENCODE_SET, "entries", _UrlKt.FRAGMENT_ENCODE_SET, "Lorg/simplifiles/archive/ArchiveEntryInfo;", "options", "Lorg/simplifiles/archive/ArchiveExtractionOptions;", "prepareTarget", _UrlKt.FRAGMENT_ENCODE_SET, "root", "targetPolicy", "Lorg/simplifiles/archive/ExtractionTargetPolicy;", "isDirectoryEmpty", "extractEntries", "progress", "Lorg/simplifiles/internal/archive/zip/ZipArchiveExtractor$ExtractionProgress;", "bufferSize", _UrlKt.FRAGMENT_ENCODE_SET, "destinationPathFor", _UrlKt.FRAGMENT_ENCODE_SET, "entry", "Ljava/util/zip/ZipEntry;", "destinationPaths", _UrlKt.FRAGMENT_ENCODE_SET, "renamedPath", "path", "copyWithLimits", _UrlKt.FRAGMENT_ENCODE_SET, "input", "Ljava/io/InputStream;", "output", "Ljava/io/OutputStream;", "totalWritten", "destinationPath", "knownUncompressedSize", "checkedAddEntry", "current", "added", "checkedAddTotal", "validateRuntimeCompressionRatio", "written", "failValidation", _UrlKt.FRAGMENT_ENCODE_SET, "code", "message", "ExtractionProgress", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nZipArchiveExtractor.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ZipArchiveExtractor.kt\norg/simplifiles/internal/archive/zip/ZipArchiveExtractor\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,397:1\n1#2:398\n1313#3,3:399\n*S KotlinDebug\n*F\n+ 1 ZipArchiveExtractor.kt\norg/simplifiles/internal/archive/zip/ZipArchiveExtractor\n*L\n256#1:399,3\n*E\n"})
public final class ZipArchiveExtractor {
    public static final ZipArchiveExtractor INSTANCE = new ZipArchiveExtractor();

    @Metadata(k = 3, mv = {2, 3, 0}, xi = 48)
    public static final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;

        static {
            int[] iArr = new int[ExtractionTargetPolicy.values().length];
            try {
                iArr[ExtractionTargetPolicy.ERROR_IF_NOT_EMPTY.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[ExtractionTargetPolicy.CLEAN.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[ExtractionTargetPolicy.REPLACE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            $EnumSwitchMapping$0 = iArr;
            int[] iArr2 = new int[DuplicatePolicy.values().length];
            try {
                iArr2[DuplicatePolicy.ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                iArr2[DuplicatePolicy.KEEP_FIRST.ordinal()] = 2;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                iArr2[DuplicatePolicy.KEEP_LAST.ordinal()] = 3;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                iArr2[DuplicatePolicy.RENAME.ordinal()] = 4;
            } catch (NoSuchFieldError unused7) {
            }
            $EnumSwitchMapping$1 = iArr2;
        }
    }

    private ZipArchiveExtractor() {
    }

    public final ExtractedArchive extract(Path source, Path targetRoot, SecurityPolicy policy, boolean cleanupOnClose, List<ArchiveEntryInfo> entries, ArchiveExtractionOptions options) throws Exception {
        Path pathNormalize = targetRoot.toAbsolutePath().normalize();
        boolean zExists = Files.exists(pathNormalize, new LinkOption[0]);
        ExtractionProgress extractionProgress = new ExtractionProgress(options, entries.size(), knownUncompressedSize(entries));
        extractionProgress.checkCanceled();
        prepareTarget(pathNormalize, options.getTargetPolicy());
        try {
            extractionProgress.emit(null);
            extractEntries(source, pathNormalize, policy, extractionProgress, options.getBufferSize());
            return new ExtractedArchive(pathNormalize, cleanupOnClose);
        } catch (Throwable th) {
            if (zExists) {
                FileTreeCleaner.INSTANCE.deleteContents(pathNormalize);
                throw th;
            }
            FileTreeCleaner.INSTANCE.deleteRecursively(pathNormalize);
            throw th;
        }
    }

    private final void prepareTarget(Path root, ExtractionTargetPolicy targetPolicy) throws Exception {
        if (Files.exists(root, new LinkOption[0])) {
            int i = WhenMappings.$EnumSwitchMapping$0[targetPolicy.ordinal()];
            if (i == 1) {
                if (!Files.isDirectory(root, new LinkOption[0])) {
                    throw new ExtractionTargetException(root, "target exists but is not a directory");
                }
                if (!isDirectoryEmpty(root)) {
                    throw new ExtractionTargetException(root, "target directory must be empty");
                }
                return;
            }
            if (i == 2) {
                if (!Files.isDirectory(root, new LinkOption[0])) {
                    throw new ExtractionTargetException(root, "target exists but is not a directory");
                }
                FileTreeCleaner.INSTANCE.deleteContents(root);
                return;
            } else {
                if (i != 3) {
                    LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0.m();
                    return;
                }
                FileTreeCleaner.INSTANCE.deleteRecursively(root);
            }
        }
        Files.createDirectories(root, new FileAttribute[0]);
    }

    private final boolean isDirectoryEmpty(Path root) throws Exception {
        Stream<Path> list = Files.list(root);
        try {
            boolean z = !list.findAny().isPresent();
            AutoCloseableKt.closeFinally(list, null);
            return z;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                AutoCloseableKt.closeFinally(list, th);
                throw th2;
            }
        }
    }

    private final void extractEntries(Path source, Path root, SecurityPolicy policy, ExtractionProgress progress, int bufferSize) {
        SecurityPolicy securityPolicy = policy;
        ?? zipFile = new ZipFile(source.toFile());
        try {
            LinkedHashSet linkedHashSet = new LinkedHashSet();
            long jCheckedAddTotal = 0;
            for (?? r3 : SequencesKt.asSequence(CollectionsKt.iterator(zipFile.entries()))) {
                progress.checkCanceled();
                ?? r0 = INSTANCE;
                String strDestinationPathFor = r0.destinationPathFor(r3, linkedHashSet, securityPolicy);
                if (strDestinationPathFor == null) {
                    progress.entryCompleted(r3.getName());
                } else {
                    Path pathResolve = ArchivePathResolver.INSTANCE.resolve(root, strDestinationPathFor, securityPolicy.getAllowAbsolutePaths());
                    if (r3.isDirectory()) {
                        Files.createDirectories(pathResolve, new FileAttribute[0]);
                        progress.entryCompleted(strDestinationPathFor);
                    } else {
                        Files.createDirectories(pathResolve.getParent(), new FileAttribute[0]);
                        if (securityPolicy.getDuplicatePolicy() == DuplicatePolicy.KEEP_LAST) {
                            Files.deleteIfExists(pathResolve);
                        }
                        long j = jCheckedAddTotal;
                        InputStream inputStream = zipFile.getInputStream(r3);
                        try {
                            OutputStream outputStreamNewOutputStream = Files.newOutputStream(pathResolve, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);
                            try {
                                long jCopyWithLimits = r0.copyWithLimits(inputStream, outputStreamNewOutputStream, r3, j, securityPolicy, progress, strDestinationPathFor, bufferSize);
                                r3 = inputStream;
                                try {
                                    CloseableKt.closeFinally(outputStreamNewOutputStream, null);
                                    CloseableKt.closeFinally(r3, null);
                                    securityPolicy = policy;
                                    jCheckedAddTotal = r0.checkedAddTotal(j, jCopyWithLimits, securityPolicy, r3.getName());
                                    r0.validateRuntimeCompressionRatio(r3, jCopyWithLimits, securityPolicy);
                                    progress.entryCompleted(strDestinationPathFor);
                                } catch (Throwable th) {
                                    th = th;
                                    Throwable th2 = th;
                                    try {
                                        throw th2;
                                    } catch (Throwable th3) {
                                        CloseableKt.closeFinally(r3, th2);
                                        throw th3;
                                    }
                                }
                            } catch (Throwable th4) {
                                r3 = inputStream;
                                try {
                                    throw th4;
                                } catch (Throwable th5) {
                                    CloseableKt.closeFinally(outputStreamNewOutputStream, th4);
                                    throw th5;
                                }
                            }
                        } catch (Throwable th6) {
                            th = th6;
                            r3 = inputStream;
                        }
                    }
                }
            }
            Unit unit = Unit.INSTANCE;
            CloseableKt.closeFinally(zipFile, null);
        } catch (Throwable th7) {
            try {
                throw th7;
            } catch (Throwable th8) {
                CloseableKt.closeFinally(zipFile, th7);
                throw th8;
            }
        }
    }

    private final String destinationPathFor(ZipEntry entry, Set<String> destinationPaths, SecurityPolicy policy) throws ArchiveValidationException {
        String normalizedPath = ArchivePathAnalyzer.INSTANCE.analyze(entry.getName()).getNormalizedPath();
        if (normalizedPath == null) {
            failValidation("archive.entry.path.invalid", "Archive entry path is invalid.", entry.getName());
            InstantKt$$ExternalSyntheticBUOutline0.m();
            return null;
        }
        if (!destinationPaths.add(normalizedPath)) {
            int i = WhenMappings.$EnumSwitchMapping$1[policy.getDuplicatePolicy().ordinal()];
            if (i == 1) {
                failValidation("archive.entry.duplicate", "Archive contains duplicate entry path: " + normalizedPath + '.', entry.getName());
                InstantKt$$ExternalSyntheticBUOutline0.m();
                return null;
            }
            if (i == 2) {
                return null;
            }
            if (i != 3) {
                if (i != 4) {
                    LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0.m();
                    return null;
                }
                return renamedPath(normalizedPath, destinationPaths);
            }
        }
        return normalizedPath;
    }

    private final String renamedPath(String path, Set<String> destinationPaths) {
        String str;
        String strSubstringBeforeLast = StringsKt.substringBeforeLast(path, '/', _UrlKt.FRAGMENT_ENCODE_SET);
        String strSubstringAfterLast$default = StringsKt.substringAfterLast$default(path, '/', (String) null, 2, (Object) null);
        String strSubstringBeforeLast2 = StringsKt.substringBeforeLast(strSubstringAfterLast$default, '.', strSubstringAfterLast$default);
        String strSubstringAfterLast = StringsKt.substringAfterLast(strSubstringAfterLast$default, '.', _UrlKt.FRAGMENT_ENCODE_SET);
        int i = 1;
        while (true) {
            if (strSubstringAfterLast.length() == 0) {
                str = strSubstringBeforeLast2 + SignatureVisitor.SUPER + i;
            } else {
                str = strSubstringBeforeLast2 + SignatureVisitor.SUPER + i + '.' + strSubstringAfterLast;
            }
            if (strSubstringBeforeLast.length() != 0) {
                str = strSubstringBeforeLast + '/' + str;
            }
            if (destinationPaths.add(str)) {
                return str;
            }
            if (i == Integer.MAX_VALUE) {
                BaseRoomConnectionManager$$ExternalSyntheticBUOutline0.m("Unable to generate unique archive entry name for ", path, 46);
                return null;
            }
            i++;
        }
    }

    private final long copyWithLimits(InputStream input, OutputStream output, ZipEntry entry, long totalWritten, SecurityPolicy policy, ExtractionProgress progress, String destinationPath, int bufferSize) throws IOException {
        byte[] bArr = new byte[bufferSize];
        long j = 0;
        while (true) {
            progress.checkCanceled();
            int i = input.read(bArr);
            if (i < 0) {
                return j;
            }
            long j2 = i;
            long jCheckedAddEntry = checkedAddEntry(j, j2, policy, entry.getName());
            checkedAddTotal(totalWritten, jCheckedAddEntry, policy, entry.getName());
            output.write(bArr, 0, i);
            progress.bytesWritten(j2, destinationPath);
            j = jCheckedAddEntry;
        }
    }

    private final long knownUncompressedSize(List<ArchiveEntryInfo> entries) {
        Iterator it = SequencesKt.filter(SequencesKt.map(SequencesKt.filterNot(CollectionsKt.asSequence(entries), new Function1() { // from class: org.simplifiles.internal.archive.zip.ZipArchiveExtractor$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(((ArchiveEntryInfo) obj).getIsDirectory());
            }
        }), new Function1() { // from class: org.simplifiles.internal.archive.zip.ZipArchiveExtractor$$ExternalSyntheticLambda1
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Long.valueOf(((ArchiveEntryInfo) obj).getUncompressedSize());
            }
        }), new Function1() { // from class: org.simplifiles.internal.archive.zip.ZipArchiveExtractor$$ExternalSyntheticLambda2
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(ZipArchiveExtractor.$r8$lambda$I0VupinfQHn1k287rurTObXXIUI(((Long) obj).longValue()));
            }
        }).iterator();
        long j = 0;
        while (it.hasNext()) {
            long jLongValue = ((Number) it.next()).longValue();
            j = LongCompanionObject.MAX_VALUE - j < jLongValue ? Long.MAX_VALUE : j + jLongValue;
        }
        return j;
    }

    public static boolean $r8$lambda$I0VupinfQHn1k287rurTObXXIUI(long j) {
        return j > 0;
    }

    private final long checkedAddEntry(long current, long added, SecurityPolicy policy, String path) throws ArchiveValidationException {
        if (LongCompanionObject.MAX_VALUE - current < added) {
            failValidation("archive.entry.size.overflow", "Archive entry size overflows Long.", path);
            InstantKt$$ExternalSyntheticBUOutline0.m();
            return 0L;
        }
        long j = current + added;
        if (j <= policy.getMaxSingleFileSize()) {
            return j;
        }
        failValidation("archive.entry.size.too_large", "Archive entry exceeded " + policy.getMaxSingleFileSize() + " bytes while extracting.", path);
        InstantKt$$ExternalSyntheticBUOutline0.m();
        return 0L;
    }

    private final long checkedAddTotal(long current, long added, SecurityPolicy policy, String path) throws ArchiveValidationException {
        if (LongCompanionObject.MAX_VALUE - current < added) {
            failValidation("archive.total_size.overflow", "Archive total size overflows Long.", path);
            InstantKt$$ExternalSyntheticBUOutline0.m();
            return 0L;
        }
        long j = current + added;
        if (j <= policy.getMaxTotalUncompressedSize()) {
            return j;
        }
        failValidation("archive.total_size.too_large", "Archive exceeded " + policy.getMaxTotalUncompressedSize() + " total bytes while extracting.", path);
        InstantKt$$ExternalSyntheticBUOutline0.m();
        return 0L;
    }

    private final void validateRuntimeCompressionRatio(ZipEntry entry, long written, SecurityPolicy policy) throws ArchiveValidationException {
        if (entry.getCompressedSize() < 0 || written <= 0) {
            return;
        }
        double compressedSize = entry.getCompressedSize() == 0 ? Double.POSITIVE_INFINITY : written / entry.getCompressedSize();
        if (compressedSize <= policy.getMaxCompressionRatio()) {
            return;
        }
        failValidation("archive.entry.compression_ratio.too_high", "Archive entry compression ratio is " + compressedSize + ", limit is " + policy.getMaxCompressionRatio() + '.', entry.getName());
        InstantKt$$ExternalSyntheticBUOutline0.m();
    }

    private final Void failValidation(String code, String message, String path) throws ArchiveValidationException {
        throw new ArchiveValidationException(new ValidationReport(ArchiveFormat.ZIP, null, CollectionsKt.listOf(new ArchiveIssue(ArchiveIssueSeverity.ERROR, code, message, path)), 2, null));
    }

    @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0002\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005¢\u0006\u0004\b\u0007\u0010\bJ\u0006\u0010\u000b\u001a\u00020\fJ\u0016\u0010\n\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u000fJ\u000e\u0010\u0010\u001a\u00020\f2\u0006\u0010\u000e\u001a\u00020\u000fJ\u0010\u0010\u0011\u001a\u00020\f2\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0012"}, d2 = {"Lorg/simplifiles/internal/archive/zip/ZipArchiveExtractor$ExtractionProgress;", _UrlKt.FRAGMENT_ENCODE_SET, "options", "Lorg/simplifiles/archive/ArchiveExtractionOptions;", "totalEntries", _UrlKt.FRAGMENT_ENCODE_SET, "totalBytes", "<init>", "(Lorg/simplifiles/archive/ArchiveExtractionOptions;JJ)V", "entriesProcessed", "bytesWritten", "checkCanceled", _UrlKt.FRAGMENT_ENCODE_SET, "bytes", "currentEntryPath", _UrlKt.FRAGMENT_ENCODE_SET, "entryCompleted", "emit", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
    public static final class ExtractionProgress {
        private long bytesWritten;
        private long entriesProcessed;
        private final ArchiveExtractionOptions options;
        private final long totalBytes;
        private final long totalEntries;

        public ExtractionProgress(ArchiveExtractionOptions archiveExtractionOptions, long j, long j2) {
            this.options = archiveExtractionOptions;
            this.totalEntries = j;
            this.totalBytes = j2;
        }

        public final void checkCanceled() throws ArchiveOperationCanceledException {
            if (this.options.getCancellationToken().isCancellationRequested()) {
                throw new ArchiveOperationCanceledException();
            }
        }

        public final void bytesWritten(long bytes, String currentEntryPath) {
            this.bytesWritten += bytes;
            emit(currentEntryPath);
        }

        public final void entryCompleted(String currentEntryPath) {
            this.entriesProcessed++;
            emit(currentEntryPath);
        }

        public final void emit(String currentEntryPath) {
            this.options.getProgressListener();
        }
    }
}
