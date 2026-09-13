package org.simplifiles.internal.files;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.simplifiles.exception.UnsafePathException;

@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\bR\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u000b"}, d2 = {"Lorg/simplifiles/internal/files/SafePathResolver;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "resolveInside", "Ljava/nio/file/Path;", "root", "path", _UrlKt.FRAGMENT_ENCODE_SET, "WINDOWS_ABSOLUTE_PATH", "Lkotlin/text/Regex;", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nSafePathResolver.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SafePathResolver.kt\norg/simplifiles/internal/files/SafePathResolver\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,40:1\n1807#2,3:41\n*S KotlinDebug\n*F\n+ 1 SafePathResolver.kt\norg/simplifiles/internal/files/SafePathResolver\n*L\n20#1:41,3\n*E\n"})
public final class SafePathResolver {
    public static final SafePathResolver INSTANCE = new SafePathResolver();
    private static final Regex WINDOWS_ABSOLUTE_PATH = new Regex("^[A-Za-z]:[/\\\\].*");

    private SafePathResolver() {
    }

    public final Path resolveInside(Path root, String path) throws UnsafePathException {
        if (StringsKt.isBlank(path)) {
            throw new UnsafePathException(path, "path must not be blank");
        }
        if (path.startsWith("/") || path.startsWith("\\") || WINDOWS_ABSOLUTE_PATH.matches(path)) {
            throw new UnsafePathException(path, "path must be relative");
        }
        String[] parts = path.split("[/\\\\]");
        for (String part : parts) {
            if (Intrinsics.areEqual(part, "..")) {
                throw new UnsafePathException(path, "path must not contain parent traversal");
            }
        }
        Path path2 = Paths.get(path, new String[0]);
        if (path2.isAbsolute()) {
            throw new UnsafePathException(path, "path must be relative");
        }
        Path pathNormalize = root.toAbsolutePath().normalize();
        Path pathNormalize2 = pathNormalize.resolve(path2).normalize();
        if (pathNormalize2.startsWith(pathNormalize)) {
            return pathNormalize2;
        }
        throw new UnsafePathException(path, "path escapes root");
    }
}
