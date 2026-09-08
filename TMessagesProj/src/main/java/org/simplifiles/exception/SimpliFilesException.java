package org.simplifiles.exception;

import java.io.IOException;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0003\n\u0002\b\u0002\b\u0016\u0018\u00002\u00020\u0001B\u0011\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005B\u0019\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\u0004\u0010\b¨\u0006\t"}, d2 = {"Lorg/simplifiles/exception/SimpliFilesException;", "Ljava/io/IOException;", "message", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;)V", "cause", _UrlKt.FRAGMENT_ENCODE_SET, "(Ljava/lang/String;Ljava/lang/Throwable;)V", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
public abstract class SimpliFilesException extends IOException {
    public SimpliFilesException(String str) {
        super(str);
    }

    public SimpliFilesException(String str, Throwable th) {
        super(str, th);
    }
}
