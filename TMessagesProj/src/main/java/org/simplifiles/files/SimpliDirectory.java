package org.simplifiles.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import kotlin.LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0;
import kotlin.Metadata;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;
import org.simplifiles.archive.ArchiveSaveOptions;
import org.simplifiles.exception.FileOperationException;
import org.simplifiles.internal.archive.zip.ZipArchiveWriter;
import org.simplifiles.internal.files.SafePathResolver;
import org.simplifiles.internal.io.FileTreeCleaner;
import org.simplifiles.internal.io.FileTreeCopier;

@Metadata(d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u000e\u0018\u00002\u00020\u0001B\u0011\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0002¢\u0006\u0004\b\u0004\u0010\u0005J\u000f\u0010\u0007\u001a\u00020\u0006H\u0002¢\u0006\u0004\b\u0007\u0010\bJ\u001f\u0010\f\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\u00022\u0006\u0010\u000b\u001a\u00020\nH\u0002¢\u0006\u0004\b\f\u0010\rJ\u001f\u0010\u0010\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u000eH\u0002¢\u0006\u0004\b\u0010\u0010\u0011J\u0013\u0010\u0013\u001a\u00020\u000e*\u00020\u0012H\u0002¢\u0006\u0004\b\u0013\u0010\u0014J\r\u0010\u0016\u001a\u00020\u0015¢\u0006\u0004\b\u0016\u0010\u0017J\r\u0010\u0018\u001a\u00020\u0000¢\u0006\u0004\b\u0018\u0010\u0019J\r\u0010\u001a\u001a\u00020\u0015¢\u0006\u0004\b\u001a\u0010\u0017J\r\u0010\u001b\u001a\u00020\u0000¢\u0006\u0004\b\u001b\u0010\u0019J\u0015\u0010\u001c\u001a\u00020\u00022\u0006\u0010\u0003\u001a\u00020\n¢\u0006\u0004\b\u001c\u0010\u001dJ\u0015\u0010\u001f\u001a\u00020\u001e2\u0006\u0010\u0003\u001a\u00020\n¢\u0006\u0004\b\u001f\u0010 J\u001d\u0010\"\u001a\u00020\u001e2\u0006\u0010\t\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020!¢\u0006\u0004\b\"\u0010#J\u001d\u0010\"\u001a\u00020\u001e2\u0006\u0010\t\u001a\u00020\u00022\u0006\u0010$\u001a\u00020\u0012¢\u0006\u0004\b\"\u0010%J\u001d\u0010\"\u001a\u00020\u001e2\u0006\u0010\t\u001a\u00020&2\u0006\u0010\u000f\u001a\u00020!¢\u0006\u0004\b\"\u0010'J\u001d\u0010\"\u001a\u00020\u001e2\u0006\u0010\t\u001a\u00020&2\u0006\u0010$\u001a\u00020\u0012¢\u0006\u0004\b\"\u0010(J\u001d\u0010)\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u00022\u0006\u0010$\u001a\u00020\u0012¢\u0006\u0004\b)\u0010*J\u001d\u0010)\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u000e¢\u0006\u0004\b)\u0010+J\u001d\u0010)\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020&2\u0006\u0010$\u001a\u00020\u0012¢\u0006\u0004\b)\u0010,J\u001d\u0010-\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u00022\u0006\u0010$\u001a\u00020\u0012¢\u0006\u0004\b-\u0010*J\u001d\u0010-\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020\u00022\u0006\u0010\u000f\u001a\u00020\u000e¢\u0006\u0004\b-\u0010+J\u001d\u0010-\u001a\u00020\u00002\u0006\u0010\t\u001a\u00020&2\u0006\u0010$\u001a\u00020\u0012¢\u0006\u0004\b-\u0010,R\u0017\u0010\u0003\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010.\u001a\u0004\b/\u00100R\u0011\u0010\u001f\u001a\u00020&8F¢\u0006\u0006\u001a\u0004\b1\u00102R\u0011\u0010\u0016\u001a\u00020\u00158F¢\u0006\u0006\u001a\u0004\b3\u0010\u0017¨\u00064"}, d2 = {"Lorg/simplifiles/files/SimpliDirectory;", _UrlKt.FRAGMENT_ENCODE_SET, "Ljava/nio/file/Path;", "path", "<init>", "(Ljava/nio/file/Path;)V", _UrlKt.FRAGMENT_ENCODE_SET, "ensureExists", "()V", "target", _UrlKt.FRAGMENT_ENCODE_SET, "operation", "rejectSelfTarget", "(Ljava/nio/file/Path;Ljava/lang/String;)V", "Lorg/simplifiles/files/DirectoryTransferOptions;", "options", "validateBeforeReplacing", "(Ljava/nio/file/Path;Lorg/simplifiles/files/DirectoryTransferOptions;)V", "Lorg/simplifiles/files/OverwritePolicy;", "toDirectoryTransferOptions", "(Lorg/simplifiles/files/OverwritePolicy;)Lorg/simplifiles/files/DirectoryTransferOptions;", _UrlKt.FRAGMENT_ENCODE_SET, "exists", "()Z", "create", "()Lorg/simplifiles/files/SimpliDirectory;", "deleteRecursively", "clean", "resolveInside", "(Ljava/lang/String;)Ljava/nio/file/Path;", "Lorg/simplifiles/files/SimpliFile;", "file", "(Ljava/lang/String;)Lorg/simplifiles/files/SimpliFile;", "Lorg/simplifiles/archive/ArchiveSaveOptions;", "zipTo", "(Ljava/nio/file/Path;Lorg/simplifiles/archive/ArchiveSaveOptions;)Lorg/simplifiles/files/SimpliFile;", "overwritePolicy", "(Ljava/nio/file/Path;Lorg/simplifiles/files/OverwritePolicy;)Lorg/simplifiles/files/SimpliFile;", "Ljava/io/File;", "(Ljava/io/File;Lorg/simplifiles/archive/ArchiveSaveOptions;)Lorg/simplifiles/files/SimpliFile;", "(Ljava/io/File;Lorg/simplifiles/files/OverwritePolicy;)Lorg/simplifiles/files/SimpliFile;", "copyTo", "(Ljava/nio/file/Path;Lorg/simplifiles/files/OverwritePolicy;)Lorg/simplifiles/files/SimpliDirectory;", "(Ljava/nio/file/Path;Lorg/simplifiles/files/DirectoryTransferOptions;)Lorg/simplifiles/files/SimpliDirectory;", "(Ljava/io/File;Lorg/simplifiles/files/OverwritePolicy;)Lorg/simplifiles/files/SimpliDirectory;", "moveTo", "Ljava/nio/file/Path;", "getPath", "()Ljava/nio/file/Path;", "getFile", "()Ljava/io/File;", "getExists", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nSimpliDirectory.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SimpliDirectory.kt\norg/simplifiles/files/SimpliDirectory\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,318:1\n288#1,11:319\n288#1,11:330\n1#2:341\n*S KotlinDebug\n*F\n+ 1 SimpliDirectory.kt\norg/simplifiles/files/SimpliDirectory\n*L\n181#1:319,11\n228#1:330,11\n*E\n"})
public final class SimpliDirectory {
    private final Path path;

    @Metadata(k = 3, mv = {2, 3, 0}, xi = 48)
    public static final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;
        public static final /* synthetic */ int[] $EnumSwitchMapping$1;

        static {
            int[] iArr = new int[DirectoryOverwritePolicy.values().length];
            try {
                iArr[DirectoryOverwritePolicy.ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[DirectoryOverwritePolicy.SKIP.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[DirectoryOverwritePolicy.REPLACE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                iArr[DirectoryOverwritePolicy.MERGE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            $EnumSwitchMapping$0 = iArr;
            int[] iArr2 = new int[OverwritePolicy.values().length];
            try {
                iArr2[OverwritePolicy.ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                iArr2[OverwritePolicy.REPLACE.ordinal()] = 2;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                iArr2[OverwritePolicy.SKIP.ordinal()] = 3;
            } catch (NoSuchFieldError unused7) {
            }
            $EnumSwitchMapping$1 = iArr2;
        }
    }

    public SimpliDirectory(Path path) {
        this.path = path;
    }

    public final File getFile() {
        return new File(this.path.toString());
    }

    public final boolean getExists() {
        return Files.isDirectory(this.path, new LinkOption[0]);
    }

    public final boolean exists() {
        return getExists();
    }

    public final SimpliDirectory create() {
        try {
            Files.createDirectories(this.path, new FileAttribute[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public final boolean deleteRecursively() throws Exception {
        if (!getExists()) {
            return false;
        }
        FileTreeCleaner.INSTANCE.deleteRecursively(this.path);
        return true;
    }

    public final SimpliDirectory clean() {
        if (!getExists()) {
            create();
            return this;
        }
        try {
            FileTreeCleaner.INSTANCE.deleteContents(this.path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return this;
    }

    public final Path resolveInside(String path) {
        try {
            return SafePathResolver.INSTANCE.resolveInside(this.path, path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final SimpliFile file(String path) {
        return new SimpliFile(resolveInside(path));
    }

    public final SimpliFile zipTo(Path target, ArchiveSaveOptions options) throws Exception {
        if (!getExists()) {
            throw new FileOperationException("Directory does not exist: " + this.path);
        }
        ZipArchiveWriter.INSTANCE.write(this.path, target, options);
        return new SimpliFile(target);
    }

    public final SimpliFile zipTo(Path target, OverwritePolicy overwritePolicy) {
        try {
            return zipTo(target, ArchiveSaveOptions.INSTANCE.builder().overwritePolicy(overwritePolicy).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final SimpliFile zipTo(File target, ArchiveSaveOptions options) {
        try {
            return zipTo(Paths.get(target.getPath(), new String[0]), options);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final SimpliFile zipTo(File target, OverwritePolicy overwritePolicy) {
        return zipTo(Paths.get(target.getPath(), new String[0]), overwritePolicy);
    }

    public final SimpliDirectory copyTo(Path target, OverwritePolicy overwritePolicy) {
        try {
            return copyTo(target, toDirectoryTransferOptions(overwritePolicy));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final SimpliDirectory copyTo(Path target, DirectoryTransferOptions options) throws Exception {
        ensureExists();
        rejectSelfTarget(target, "copied");
        validateBeforeReplacing(target, options);
        if (Files.exists(target, new LinkOption[0])) {
            int i = WhenMappings.$EnumSwitchMapping$0[options.getOverwritePolicy().ordinal()];
            if (i == 1) {
                SimpliFile$$ExternalSyntheticBUOutline1.m("Target already exists: ", target);
                return null;
            }
            if (i == 2) {
                return new SimpliDirectory(target);
            }
            if (i == 3) {
                FileTreeCleaner.INSTANCE.deleteRecursively(target);
            } else if (i != 4) {
                LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0.m();
                return null;
            }
        }
        FileTreeCopier.INSTANCE.copyDirectory(this.path, target, options);
        return new SimpliDirectory(target);
    }

    public final SimpliDirectory copyTo(File target, OverwritePolicy overwritePolicy) {
        return copyTo(Paths.get(target.getPath(), new String[0]), overwritePolicy);
    }

    public final SimpliDirectory moveTo(Path target, OverwritePolicy overwritePolicy) {
        try {
            return moveTo(target, toDirectoryTransferOptions(overwritePolicy));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public final SimpliDirectory moveTo(Path target, DirectoryTransferOptions options) throws Exception {
        ensureExists();
        rejectSelfTarget(target, "moved");
        validateBeforeReplacing(target, options);
        if (Files.exists(target, new LinkOption[0])) {
            int i = WhenMappings.$EnumSwitchMapping$0[options.getOverwritePolicy().ordinal()];
            if (i == 1) {
                SimpliFile$$ExternalSyntheticBUOutline1.m("Target already exists: ", target);
                return null;
            }
            if (i == 2) {
                return new SimpliDirectory(target);
            }
            if (i == 3) {
                FileTreeCleaner.INSTANCE.deleteRecursively(target);
            } else if (i != 4) {
                LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0.m();
                return null;
            }
        }
        if (options.getOverwritePolicy() == DirectoryOverwritePolicy.MERGE && Files.exists(target, new LinkOption[0])) {
            FileTreeCopier.INSTANCE.copyDirectory(this.path, target, options);
            FileTreeCleaner.INSTANCE.deleteRecursively(this.path);
            return new SimpliDirectory(target);
        }
        FileTreeCopier.INSTANCE.validateDirectory(this.path, options);
        Path parent = target.getParent();
        if (parent != null) {
            Files.createDirectories(parent, new FileAttribute[0]);
        }
        Files.move(this.path, target, StandardCopyOption.REPLACE_EXISTING);
        return new SimpliDirectory(target);
    }

    public final SimpliDirectory moveTo(File target, OverwritePolicy overwritePolicy) {
        return moveTo(Paths.get(target.getPath(), new String[0]), overwritePolicy);
    }

    private final void ensureExists() throws FileOperationException {
        if (getExists()) {
            return;
        }
        throw new FileOperationException("Directory does not exist: " + this.path);
    }

    private final void rejectSelfTarget(Path target, String operation) throws FileOperationException {
        if (target.toAbsolutePath().normalize().startsWith(this.path.toAbsolutePath().normalize())) {
            throw new FileOperationException("Directory cannot be " + operation + " into itself: " + target);
        }
    }

    private final void validateBeforeReplacing(Path target, DirectoryTransferOptions options) throws Exception {
        if (Files.exists(target, new LinkOption[0]) && options.getOverwritePolicy() == DirectoryOverwritePolicy.REPLACE) {
            FileTreeCopier.INSTANCE.validateDirectory(this.path, options);
        }
    }

    private final DirectoryTransferOptions toDirectoryTransferOptions(OverwritePolicy overwritePolicy) {
        DirectoryOverwritePolicy directoryOverwritePolicy;
        int i = WhenMappings.$EnumSwitchMapping$1[overwritePolicy.ordinal()];
        if (i == 1) {
            directoryOverwritePolicy = DirectoryOverwritePolicy.ERROR;
        } else if (i == 2) {
            directoryOverwritePolicy = DirectoryOverwritePolicy.REPLACE;
        } else {
            if (i != 3) {
                LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0.m();
                return null;
            }
            directoryOverwritePolicy = DirectoryOverwritePolicy.SKIP;
        }
        return new DirectoryTransferOptions(directoryOverwritePolicy, 0L, 0L, 6, null);
    }
}
