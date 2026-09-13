package org.simplifiles.files;

import c.f$$ExternalSyntheticBUOutline1;
import com.google.android.gms.cast.MediaStatus;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;
import kotlin.LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.io.CloseableKt;
import kotlin.io.TextStreamsKt;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.LongCompanionObject;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Charsets;
import okhttp3.internal.url._UrlKt;
import org.simplifiles.exception.FileOperationException;

@Metadata(d1 = {"\u0000^\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0010\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\n\u0018\u00002\u00020\u0001B\u0011\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0002¢\u0006\u0004\b\u0004\u0010\u0005J'\u0010\f\u001a\u00020\n2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\nH\u0002¢\u0006\u0004\b\f\u0010\rJ\r\u0010\u000f\u001a\u00020\u000e¢\u0006\u0004\b\u000f\u0010\u0010J\r\u0010\u0011\u001a\u00020\u0006¢\u0006\u0004\b\u0011\u0010\u0012J\r\u0010\u0014\u001a\u00020\u0013¢\u0006\u0004\b\u0014\u0010\u0015J\u0015\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\n¢\u0006\u0004\b\u0014\u0010\u0016J!\u0010\u001a\u001a\u00020\u00192\u0006\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\u0018\u001a\u00020\u0017H\u0007¢\u0006\u0004\b\u001a\u0010\u001bJ5\u0010\u001f\u001a\u00020\u001d2\u0006\u0010\u000b\u001a\u00020\n2\b\b\u0002\u0010\u0018\u001a\u00020\u00172\u0012\u0010\u001e\u001a\u000e\u0012\u0004\u0012\u00020\u0019\u0012\u0004\u0012\u00020\u001d0\u001cH\u0007¢\u0006\u0004\b\u001f\u0010 J!\u0010!\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\u000b\u001a\u00020\nH\u0007¢\u0006\u0004\b!\u0010\"J!\u0010$\u001a\u00020\u001d2\u0006\u0010#\u001a\u00020\u00192\b\b\u0002\u0010\u0018\u001a\u00020\u0017H\u0007¢\u0006\u0004\b$\u0010%J\u0015\u0010'\u001a\u00020\u001d2\u0006\u0010&\u001a\u00020\u0013¢\u0006\u0004\b'\u0010(J!\u0010)\u001a\u00020\u00002\u0006\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\u000b\u001a\u00020\nH\u0007¢\u0006\u0004\b)\u0010\"J\r\u0010*\u001a\u00020\u0000¢\u0006\u0004\b*\u0010+J\r\u0010,\u001a\u00020\u000e¢\u0006\u0004\b,\u0010\u0010J\u001d\u00100\u001a\u00020\u00002\u0006\u0010-\u001a\u00020\u00022\u0006\u0010/\u001a\u00020.¢\u0006\u0004\b0\u00101J\u001d\u00100\u001a\u00020\u00002\u0006\u0010-\u001a\u0002022\u0006\u0010/\u001a\u00020.¢\u0006\u0004\b0\u00103J\u001d\u00104\u001a\u00020\u00002\u0006\u0010-\u001a\u00020\u00022\u0006\u0010/\u001a\u00020.¢\u0006\u0004\b4\u00101J\u001d\u00104\u001a\u00020\u00002\u0006\u0010-\u001a\u0002022\u0006\u0010/\u001a\u00020.¢\u0006\u0004\b4\u00103R\u0017\u0010\u0003\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u00105\u001a\u0004\b6\u00107R\u0011\u0010:\u001a\u0002028F¢\u0006\u0006\u001a\u0004\b8\u00109R\u0011\u0010\u000f\u001a\u00020\u000e8F¢\u0006\u0006\u001a\u0004\b;\u0010\u0010¨\u0006<"}, d2 = {"Lorg/simplifiles/files/SimpliFile;", _UrlKt.FRAGMENT_ENCODE_SET, "Ljava/nio/file/Path;", "path", "<init>", "(Ljava/nio/file/Path;)V", "Ljava/io/InputStream;", "input", "Ljava/io/OutputStream;", "output", _UrlKt.FRAGMENT_ENCODE_SET, "maxBytes", "copyFrom", "(Ljava/io/InputStream;Ljava/io/OutputStream;J)J", _UrlKt.FRAGMENT_ENCODE_SET, "exists", "()Z", "inputStream", "()Ljava/io/InputStream;", _UrlKt.FRAGMENT_ENCODE_SET, "readBytes", "()[B", "(J)[B", "Ljava/nio/charset/Charset;", "charset", _UrlKt.FRAGMENT_ENCODE_SET, "readText", "(JLjava/nio/charset/Charset;)Ljava/lang/String;", "Lkotlin/Function1;", _UrlKt.FRAGMENT_ENCODE_SET, "block", "forEachLine", "(JLjava/nio/charset/Charset;Lkotlin/jvm/functions/Function1;)V", "writeFrom", "(Ljava/io/InputStream;J)Lorg/simplifiles/files/SimpliFile;", "text", "writeTextAtomic", "(Ljava/lang/String;Ljava/nio/charset/Charset;)V", "bytes", "writeBytesAtomic", "([B)V", "writeFromAtomic", "touch", "()Lorg/simplifiles/files/SimpliFile;", "delete", "target", "Lorg/simplifiles/files/OverwritePolicy;", "overwritePolicy", "copyTo", "(Ljava/nio/file/Path;Lorg/simplifiles/files/OverwritePolicy;)Lorg/simplifiles/files/SimpliFile;", "Ljava/io/File;", "(Ljava/io/File;Lorg/simplifiles/files/OverwritePolicy;)Lorg/simplifiles/files/SimpliFile;", "moveTo", "Ljava/nio/file/Path;", "getPath", "()Ljava/nio/file/Path;", "getFile", "()Ljava/io/File;", "file", "getExists", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nSimpliFile.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SimpliFile.kt\norg/simplifiles/files/SimpliFile\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 ReadWrite.kt\nkotlin/io/TextStreamsKt\n+ 4 _Sequences.kt\nkotlin/sequences/SequencesKt___SequencesKt\n*L\n1#1,393:1\n1#2:394\n1#2:396\n66#3:395\n1342#4,2:397\n*S KotlinDebug\n*F\n+ 1 SimpliFile.kt\norg/simplifiles/files/SimpliFile\n*L\n98#1:396\n98#1:395\n99#1:397,2\n*E\n"})
public final class SimpliFile {
    private final Path path;

    @Metadata(k = 3, mv = {2, 3, 0}, xi = 48)
    public static final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[OverwritePolicy.values().length];
            try {
                iArr[OverwritePolicy.ERROR.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[OverwritePolicy.REPLACE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[OverwritePolicy.SKIP.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    public SimpliFile(Path path) {
        this.path = path;
    }

    public final File getFile() {
        return new File(this.path.toString());
    }

    public final boolean getExists() {
        return Files.exists(this.path, new LinkOption[0]);
    }

    public final boolean exists() {
        return getExists();
    }

    public final InputStream inputStream() {
        try {
            return Files.newInputStream(this.path, new OpenOption[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final byte[] readBytes() {
        try {
            return Files.readAllBytes(this.path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final byte[] readBytes(long maxBytes) throws FileOperationException {
        if (maxBytes < 0) {
            f$$ExternalSyntheticBUOutline1.m("maxBytes must not be negative.");
            return null;
        }
        try {
            if (Files.size(this.path) > maxBytes) {
                SimpliFile$$ExternalSyntheticBUOutline2.m("File exceeds read limit of ", maxBytes, this.path);
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return readBytes();
    }

    public static /* synthetic */ String readText$default(SimpliFile simpliFile, long j, Charset charset, int i, Object obj) {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        return simpliFile.readText(j, charset);
    }

    @JvmOverloads
    public final String readText(long maxBytes, Charset charset) {
        try {
            return new String(readBytes(maxBytes), charset);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static /* synthetic */ void forEachLine$default(SimpliFile simpliFile, long j, Charset charset, Function1 function1, int i, Object obj) {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        simpliFile.forEachLine(j, charset, function1);
    }

    @JvmOverloads
    public final void forEachLine(long maxBytes, Charset charset, Function1<? super String, Unit> block) {
        if (maxBytes < 0) {
            f$$ExternalSyntheticBUOutline1.m("maxBytes must not be negative.");
            return;
        }
        InputStream inputStream = inputStream();
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new BoundedInputStream(inputStream, maxBytes, this.path), charset), 8192);
            try {
                Iterator<String> it = TextStreamsKt.lineSequence(bufferedReader).iterator();
                while (it.hasNext()) {
                    block.invoke(it.next());
                }
                Unit unit = Unit.INSTANCE;
                CloseableKt.closeFinally(bufferedReader, null);
                CloseableKt.closeFinally(inputStream, null);
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    CloseableKt.closeFinally(bufferedReader, th);
                    throw th2;
                }
            }
        } catch (Throwable th3) {
            try {
                throw th3;
            } catch (Throwable th4) {
                CloseableKt.closeFinally(inputStream, th3);
                throw th4;
            }
        }
    }

    public static /* synthetic */ SimpliFile writeFrom$default(SimpliFile simpliFile, InputStream inputStream, long j, int i, Object obj) throws IOException {
        if ((i & 2) != 0) {
            j = LongCompanionObject.MAX_VALUE;
        }
        return simpliFile.writeFrom(inputStream, j);
    }

    @JvmOverloads
    public final SimpliFile writeFrom(InputStream input, long maxBytes) throws IOException {
        Path parent = this.path.getParent();
        if (parent != null) {
            Files.createDirectories(parent, new FileAttribute[0]);
        }
        OutputStream outputStreamNewOutputStream = Files.newOutputStream(this.path, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
        try {
            copyFrom(input, outputStreamNewOutputStream, maxBytes);
            CloseableKt.closeFinally(outputStreamNewOutputStream, null);
            return this;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                CloseableKt.closeFinally(outputStreamNewOutputStream, th);
                throw th2;
            }
        }
    }

    public static /* synthetic */ void writeTextAtomic$default(SimpliFile simpliFile, String str, Charset charset, int i, Object obj) throws IOException {
        if ((i & 2) != 0) {
            charset = Charsets.UTF_8;
        }
        simpliFile.writeTextAtomic(str, charset);
    }

    @JvmOverloads
    public final void writeTextAtomic(String text, Charset charset) throws IOException {
        writeBytesAtomic(text.getBytes(charset));
    }

    public final void writeBytesAtomic(byte[] bytes) throws IOException {
        String string;
        Path parent = this.path.getParent();
        if (parent == null) {
            parent = Paths.get(".", new String[0]).toAbsolutePath().normalize();
        }
        Files.createDirectories(parent, new FileAttribute[0]);
        Path fileName = this.path.getFileName();
        if (fileName == null || (string = fileName.toString()) == null) {
            throw new FileOperationException("File path must include a file name.");
        }
        Path pathCreateTempFile = Files.createTempFile(parent, "." + string + '.', ".tmp", new FileAttribute[0]);
        try {
            Files.write(pathCreateTempFile, bytes, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            try {
                Files.move(pathCreateTempFile, this.path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
            } catch (AtomicMoveNotSupportedException unused) {
                Files.move(pathCreateTempFile, this.path, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Throwable th) {
            Files.deleteIfExists(pathCreateTempFile);
            throw th;
        }
    }

    public static /* synthetic */ SimpliFile writeFromAtomic$default(SimpliFile simpliFile, InputStream inputStream, long j, int i, Object obj) throws IOException {
        if ((i & 2) != 0) {
            j = LongCompanionObject.MAX_VALUE;
        }
        return simpliFile.writeFromAtomic(inputStream, j);
    }

    @JvmOverloads
    public final SimpliFile writeFromAtomic(InputStream input, long maxBytes) throws IOException {
        String string;
        Path parent = this.path.getParent();
        if (parent == null) {
            parent = Paths.get(".", new String[0]).toAbsolutePath().normalize();
        }
        Files.createDirectories(parent, new FileAttribute[0]);
        Path fileName = this.path.getFileName();
        if (fileName == null || (string = fileName.toString()) == null) {
            throw new FileOperationException("File path must include a file name.");
        }
        Path pathCreateTempFile = Files.createTempFile(parent, "." + string + '.', ".tmp", new FileAttribute[0]);
        try {
            OutputStream outputStreamNewOutputStream = Files.newOutputStream(pathCreateTempFile, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.WRITE);
            try {
                copyFrom(input, outputStreamNewOutputStream, maxBytes);
                CloseableKt.closeFinally(outputStreamNewOutputStream, null);
                try {
                    Files.move(pathCreateTempFile, this.path, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
                    return this;
                } catch (AtomicMoveNotSupportedException unused) {
                    Files.move(pathCreateTempFile, this.path, StandardCopyOption.REPLACE_EXISTING);
                    return this;
                }
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    CloseableKt.closeFinally(outputStreamNewOutputStream, th);
                    throw th2;
                }
            }
        } catch (Throwable th3) {
            Files.deleteIfExists(pathCreateTempFile);
            throw th3;
        }
    }

    public final SimpliFile touch() throws IOException {
        boolean zIsDirectory = Files.isDirectory(this.path, new LinkOption[0]);
        Path path = this.path;
        if (zIsDirectory) {
            SimpliFile$$ExternalSyntheticBUOutline1.m("Path is a directory: ", path);
            return null;
        }
        Path parent = path.getParent();
        if (parent != null) {
            Files.createDirectories(parent, new FileAttribute[0]);
        }
        Files.write(this.path, new byte[0], StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        Files.setLastModifiedTime(this.path, FileTime.fromMillis(System.currentTimeMillis()));
        return this;
    }

    public final boolean delete() {
        try {
            return Files.deleteIfExists(this.path);
        } catch (IOException e) {
            return false;
        }
    }

    public final SimpliFile copyTo(Path target, OverwritePolicy overwritePolicy) throws IOException {
        Path parent = target.getParent();
        if (parent != null) {
            Files.createDirectories(parent, new FileAttribute[0]);
        }
        int i = WhenMappings.$EnumSwitchMapping$0[overwritePolicy.ordinal()];
        if (i != 1) {
            if (i == 2) {
                Files.copy(this.path, target, StandardCopyOption.REPLACE_EXISTING);
            } else {
                if (i != 3) {
                    LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0.m();
                    return null;
                }
                if (!Files.exists(target, new LinkOption[0])) {
                    Files.copy(this.path, target, new CopyOption[0]);
                }
                Unit unit = Unit.INSTANCE;
            }
        } else {
            if (Files.exists(target, new LinkOption[0])) {
                SimpliFile$$ExternalSyntheticBUOutline1.m("Target already exists: ", target);
                return null;
            }
            Files.copy(this.path, target, new CopyOption[0]);
        }
        return new SimpliFile(target);
    }

    public final SimpliFile copyTo(File target, OverwritePolicy overwritePolicy) {
        try {
            return copyTo(Paths.get(target.getPath(), new String[0]), overwritePolicy);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final SimpliFile moveTo(Path target, OverwritePolicy overwritePolicy) throws IOException {
        Path parent = target.getParent();
        if (parent != null) {
            Files.createDirectories(parent, new FileAttribute[0]);
        }
        int i = WhenMappings.$EnumSwitchMapping$0[overwritePolicy.ordinal()];
        if (i != 1) {
            if (i == 2) {
                Files.move(this.path, target, StandardCopyOption.REPLACE_EXISTING);
            } else {
                if (i != 3) {
                    LazyKt__LazyJVMKt$$ExternalSyntheticBUOutline0.m();
                    return null;
                }
                if (!Files.exists(target, new LinkOption[0])) {
                    Files.move(this.path, target, new CopyOption[0]);
                }
                Unit unit = Unit.INSTANCE;
            }
        } else {
            if (Files.exists(target, new LinkOption[0])) {
                SimpliFile$$ExternalSyntheticBUOutline1.m("Target already exists: ", target);
                return null;
            }
            Files.move(this.path, target, new CopyOption[0]);
        }
        return new SimpliFile(target);
    }

    public final SimpliFile moveTo(File target, OverwritePolicy overwritePolicy) {
        try {
            return moveTo(Paths.get(target.getPath(), new String[0]), overwritePolicy);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final long copyFrom(InputStream input, OutputStream output, long maxBytes) throws IOException {
        if (maxBytes < 0) {
            f$$ExternalSyntheticBUOutline1.m("maxBytes must not be negative.");
            return 0L;
        }
        byte[] bArr = new byte[65536];
        long j = 0;
        while (true) {
            long j2 = maxBytes - j;
            if (j2 == 0) {
                if (input.read() < 0) {
                    break;
                }
                SimpliFile$$ExternalSyntheticBUOutline2.m("Input exceeds write limit of ", maxBytes, this.path);
                return 0L;
            }
            int i = input.read(bArr, 0, (int) Math.min(MediaStatus.COMMAND_FOLLOW, j2));
            if (i < 0) {
                break;
            }
            output.write(bArr, 0, i);
            j += (long) i;
        }
        return j;
    }
}
