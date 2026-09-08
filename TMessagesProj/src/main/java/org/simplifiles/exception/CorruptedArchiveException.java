package org.simplifiles.exception;

import java.nio.file.Path;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007¨\u0006\b"}, d2 = {"Lorg/simplifiles/exception/CorruptedArchiveException;", "Lorg/simplifiles/exception/SimpliFilesException;", "path", "Ljava/nio/file/Path;", "cause", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/nio/file/Path;Ljava/lang/Throwable;)V", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final class CorruptedArchiveException extends SimpliFilesException {
    public CorruptedArchiveException(Path path, Throwable th) {
        super("Archive is corrupted or unreadable: " + path, th);
    }
}
