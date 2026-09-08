package org.simplifiles.files;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0001\u0012\u0006\u0010\u0003\u001a\u00020\u0004\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\b\u0010\n\u001a\u00020\u000bH\u0016J \u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\u000f\u001a\u00020\u000bH\u0016J\b\u0010\u0010\u001a\u00020\u0011H\u0016R\u000e\u0010\u0002\u001a\u00020\u0001X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u0012"}, d2 = {"Lorg/simplifiles/files/BoundedInputStream;", "Ljava/io/InputStream;", "delegate", "maxBytes", _UrlKt.FRAGMENT_ENCODE_SET, "path", "Ljava/nio/file/Path;", "<init>", "(Ljava/io/InputStream;JLjava/nio/file/Path;)V", "readBytes", "read", _UrlKt.FRAGMENT_ENCODE_SET, "buffer", _UrlKt.FRAGMENT_ENCODE_SET, "offset", "length", "close", _UrlKt.FRAGMENT_ENCODE_SET, "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
final class BoundedInputStream extends InputStream {
    private final InputStream delegate;
    private final long maxBytes;
    private final Path path;
    private long readBytes;

    public BoundedInputStream(InputStream inputStream, long j, Path path) {
        this.delegate = inputStream;
        this.maxBytes = j;
        this.path = path;
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        long j = this.readBytes;
        long j2 = this.maxBytes;
        InputStream inputStream = this.delegate;
        if (j == j2) {
            if (inputStream.read() < 0) {
                return -1;
            }
            SimpliFile$$ExternalSyntheticBUOutline2.m("File exceeds read limit of ", this.maxBytes, this.path);
            return 0;
        }
        int i = inputStream.read();
        if (i >= 0) {
            this.readBytes++;
        }
        return i;
    }

    @Override // java.io.InputStream
    public int read(byte[] buffer, int offset, int length) throws IOException {
        if (length == 0) {
            return 0;
        }
        long j = this.readBytes;
        long j2 = this.maxBytes;
        if (j == j2) {
            if (this.delegate.read() < 0) {
                return -1;
            }
            SimpliFile$$ExternalSyntheticBUOutline2.m("File exceeds read limit of ", this.maxBytes, this.path);
            return 0;
        }
        int i = this.delegate.read(buffer, offset, (int) Math.min(length, j2 - j));
        if (i > 0) {
            this.readBytes += (long) i;
        }
        return i;
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.delegate.close();
    }
}
