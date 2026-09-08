package org.simplifiles.archive;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import kotlin.Metadata;
import kotlin.jvm.JvmOverloads;
import kotlin.text.Charsets;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0012\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\t\n\u0002\b\u0004\u0018\u00002\u00020\u0001B!\b\u0000\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0005\u001a\u00020\u0004\u0012\u0006\u0010\u0006\u001a\u00020\u0002¢\u0006\u0004\b\u0007\u0010\bJ\r\u0010\n\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\r\u0010\r\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eJ\u0019\u0010\u0011\u001a\u00020\u00042\b\b\u0002\u0010\u0010\u001a\u00020\u000fH\u0007¢\u0006\u0004\b\u0011\u0010\u0012R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0003\u0010\u0013R\u0017\u0010\u0005\u001a\u00020\u00048\u0006¢\u0006\f\n\u0004\b\u0005\u0010\u0014\u001a\u0004\b\u0015\u0010\u0016R\u0017\u0010\u0006\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0006\u0010\u0013\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\n\u001a\u00020\t8F¢\u0006\u0006\u001a\u0004\b\u0019\u0010\u000bR\u0011\u0010\u001d\u001a\u00020\u001a8F¢\u0006\u0006\u001a\u0004\b\u001b\u0010\u001c¨\u0006\u001e"}, d2 = {"Lorg/simplifiles/archive/ArchiveFile;", _UrlKt.FRAGMENT_ENCODE_SET, "Ljava/nio/file/Path;", "root", _UrlKt.FRAGMENT_ENCODE_SET, "path", "absolutePath", "<init>", "(Ljava/nio/file/Path;Ljava/lang/String;Ljava/nio/file/Path;)V", _UrlKt.FRAGMENT_ENCODE_SET, "exists", "()Z", _UrlKt.FRAGMENT_ENCODE_SET, "readBytes", "()[B", "Ljava/nio/charset/Charset;", "charset", "readText", "(Ljava/nio/charset/Charset;)Ljava/lang/String;", "Ljava/nio/file/Path;", "Ljava/lang/String;", "getPath", "()Ljava/lang/String;", "getAbsolutePath", "()Ljava/nio/file/Path;", "getExists", _UrlKt.FRAGMENT_ENCODE_SET, "getSize", "()J", "size", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final class ArchiveFile {
    private final Path absolutePath;
    private final String path;
    private final Path root;

    public ArchiveFile(Path path, String str, Path path2) {
        this.root = path;
        this.path = str;
        this.absolutePath = path2;
    }

    public final boolean getExists() {
        return Files.exists(this.absolutePath, new LinkOption[0]);
    }

    public final boolean exists() {
        return getExists();
    }

    public final long getSize() {
        return Files.size(this.absolutePath);
    }

    public final byte[] readBytes() {
        return Files.readAllBytes(this.absolutePath);
    }

    public static /* synthetic */ String readText$default(ArchiveFile archiveFile, Charset charset, int i, Object obj) {
        if ((i & 1) != 0) {
            charset = Charsets.UTF_8;
        }
        return archiveFile.readText(charset);
    }

    @JvmOverloads
    public final String readText(Charset charset) {
        return new String(readBytes(), charset);
    }
}
