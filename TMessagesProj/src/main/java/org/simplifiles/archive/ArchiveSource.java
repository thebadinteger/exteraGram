package org.simplifiles.archive;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import kotlin.LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.CollectionsKt;
import kotlin.jdk7.AutoCloseableKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;
import org.simplifiles.archive.security.SecurityPolicy;
import org.simplifiles.exception.ArchiveOperationCanceledException;
import org.simplifiles.exception.ArchiveValidationException;
import org.simplifiles.exception.CorruptedArchiveException;
import org.simplifiles.files.SimpliDirectory;
import org.simplifiles.internal.archive.ArchiveFormatDetector;
import org.simplifiles.internal.archive.ArchiveValidator;
import org.simplifiles.internal.archive.zip.ZipArchiveExtractor;
import org.simplifiles.internal.archive.zip.ZipArchiveReader;

@Metadata(d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\u0018\u00002\u00020\u0001B\u001b\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0004¢\u0006\u0004\b\u0006\u0010\u0007J\u0017\u0010\u000b\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\bH\u0002¢\u0006\u0004\b\u000b\u0010\fJ\u0015\u0010\r\u001a\u00020\u00002\u0006\u0010\u0005\u001a\u00020\u0004¢\u0006\u0004\b\r\u0010\u000eJ\r\u0010\u0010\u001a\u00020\u000f¢\u0006\u0004\b\u0010\u0010\u0011J\u0015\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0003\u001a\u00020\u0002¢\u0006\u0004\b\u0013\u0010\u0014J\u001d\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0003\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\b¢\u0006\u0004\b\u0013\u0010\u0015J\u0015\u0010\u0013\u001a\u00020\u00122\u0006\u0010\u0017\u001a\u00020\u0016¢\u0006\u0004\b\u0013\u0010\u0018J\u001d\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u0003\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\b¢\u0006\u0004\b\u001a\u0010\u001bJ\u001d\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u0017\u001a\u00020\u00162\u0006\u0010\t\u001a\u00020\b¢\u0006\u0004\b\u001a\u0010\u001cR\u0017\u0010\u0003\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u001d\u001a\u0004\b\u001e\u0010\u001fR\u0017\u0010\u0005\u001a\u00020\u00048\u0006¢\u0006\f\n\u0004\b\u0005\u0010 \u001a\u0004\b!\u0010\"¨\u0006#"}, d2 = {"Lorg/simplifiles/archive/ArchiveSource;", _UrlKt.FRAGMENT_ENCODE_SET, "Ljava/nio/file/Path;", "path", "Lorg/simplifiles/archive/security/SecurityPolicy;", "policy", "<init>", "(Ljava/nio/file/Path;Lorg/simplifiles/archive/security/SecurityPolicy;)V", "Lorg/simplifiles/archive/ArchiveExtractionOptions;", "options", _UrlKt.FRAGMENT_ENCODE_SET, "ensureNotCanceled", "(Lorg/simplifiles/archive/ArchiveExtractionOptions;)V", "withPolicy", "(Lorg/simplifiles/archive/security/SecurityPolicy;)Lorg/simplifiles/archive/ArchiveSource;", "Lorg/simplifiles/archive/ValidationReport;", "validate", "()Lorg/simplifiles/archive/ValidationReport;", "Lorg/simplifiles/archive/ExtractedArchive;", "extractTo", "(Ljava/nio/file/Path;)Lorg/simplifiles/archive/ExtractedArchive;", "(Ljava/nio/file/Path;Lorg/simplifiles/archive/ArchiveExtractionOptions;)Lorg/simplifiles/archive/ExtractedArchive;", "Ljava/io/File;", "file", "(Ljava/io/File;)Lorg/simplifiles/archive/ExtractedArchive;", "Lorg/simplifiles/files/SimpliDirectory;", "extractToDirectory", "(Ljava/nio/file/Path;Lorg/simplifiles/archive/ArchiveExtractionOptions;)Lorg/simplifiles/files/SimpliDirectory;", "(Ljava/io/File;Lorg/simplifiles/archive/ArchiveExtractionOptions;)Lorg/simplifiles/files/SimpliDirectory;", "Ljava/nio/file/Path;", "getPath", "()Ljava/nio/file/Path;", "Lorg/simplifiles/archive/security/SecurityPolicy;", "getPolicy", "()Lorg/simplifiles/archive/security/SecurityPolicy;", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final class ArchiveSource {
    private final Path path;
    private final SecurityPolicy policy;

    @Metadata(k = 3, mv = {2, 3, 0}, xi = 48)
    public static final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[ArchiveFormat.values().length];
            try {
                iArr[ArchiveFormat.ZIP.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public ArchiveSource(Path path, SecurityPolicy securityPolicy) {
        this.path = path;
        this.policy = securityPolicy;
    }

    public /* synthetic */ ArchiveSource(Path path, SecurityPolicy securityPolicy, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(path, (i & 2) != 0 ? SecurityPolicy.INSTANCE.strict() : securityPolicy);
    }

    public final ArchiveSource withPolicy(SecurityPolicy policy) {
        return new ArchiveSource(this.path, policy);
    }

    public final ValidationReport validate() throws IOException {
        ArchiveFormat archiveFormatDetect = ArchiveFormatDetector.INSTANCE.detect(this.path);
        if (archiveFormatDetect == null) {
            return new ValidationReport(null, null, CollectionsKt.listOf(new ArchiveIssue(ArchiveIssueSeverity.BLOCKER, "archive.format.unsupported", "Unsupported archive format.", null, 8, null)), 3, null);
        }
        try {
            if (WhenMappings.$EnumSwitchMapping$0[archiveFormatDetect.ordinal()] != 1) {
                throw new NoWhenBranchMatchedException();
            }
            return ArchiveValidator.INSTANCE.validate(ZipArchiveReader.INSTANCE.inspect(this.path), this.policy);
        } catch (CorruptedArchiveException e) {
            ArchiveIssueSeverity archiveIssueSeverity = ArchiveIssueSeverity.BLOCKER;
            String message = e.getMessage();
            if (message == null) {
                message = "Archive is corrupted.";
            }
            return new ValidationReport(archiveFormatDetect, null, CollectionsKt.listOf(new ArchiveIssue(archiveIssueSeverity, "archive.corrupted", message, null, 8, null)), 2, null);
        }
    }

    public final ExtractedArchive extractTo(Path path) {
        try {
            return extractTo(path, ArchiveExtractionOptions.INSTANCE.defaults());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final ExtractedArchive extractTo(Path path, ArchiveExtractionOptions options) {
        try {
            ensureNotCanceled(options);
            ValidationReport validationReportValidate = validate();
            if (!validationReportValidate.isSafe()) {
                throw new ArchiveValidationException(validationReportValidate);
            }
            ensureNotCanceled(options);
            ArchiveFormat format = validationReportValidate.getFormat();
            int i = format == null ? -1 : WhenMappings.$EnumSwitchMapping$0[format.ordinal()];
            if (i == -1) {
                throw new ArchiveValidationException(validationReportValidate);
            }
            if (i != 1) {
                LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0.m();
                return null;
            }
            return ZipArchiveExtractor.INSTANCE.extract(this.path, path, this.policy, false, validationReportValidate.getEntries(), options);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final ExtractedArchive extractTo(File file) {
        return extractTo(Paths.get(file.getPath(), new String[0]));
    }

    public final SimpliDirectory extractToDirectory(Path path, ArchiveExtractionOptions options) {
        try {
            ExtractedArchive extractedArchiveExtractTo = extractTo(path, options);
            try {
                SimpliDirectory simpliDirectory = new SimpliDirectory(extractedArchiveExtractTo.getRoot());
                AutoCloseableKt.closeFinally(extractedArchiveExtractTo, null);
                return simpliDirectory;
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    AutoCloseableKt.closeFinally(extractedArchiveExtractTo, th);
                    throw th2;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final SimpliDirectory extractToDirectory(File file, ArchiveExtractionOptions options) {
        return extractToDirectory(Paths.get(file.getPath(), new String[0]), options);
    }

    private final void ensureNotCanceled(ArchiveExtractionOptions options) throws ArchiveOperationCanceledException {
        if (options.getCancellationToken().isCancellationRequested()) {
            throw new ArchiveOperationCanceledException();
        }
    }
}
