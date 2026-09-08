package org.simplifiles.internal.archive;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import kotlin.Metadata;
import kotlin.UByte;
import kotlin.io.CloseableKt;
import okhttp3.internal.url._UrlKt;
import org.simplifiles.archive.ArchiveFormat;

@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\b"}, d2 = {"Lorg/simplifiles/internal/archive/ArchiveFormatDetector;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "detect", "Lorg/simplifiles/archive/ArchiveFormat;", "path", "Ljava/nio/file/Path;", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final class ArchiveFormatDetector {
    public static final ArchiveFormatDetector INSTANCE = new ArchiveFormatDetector();

    private ArchiveFormatDetector() {
    }

    public final ArchiveFormat detect(Path path) throws IOException {
        byte[] bArr = new byte[4];
        InputStream inputStreamNewInputStream = Files.newInputStream(path, new OpenOption[0]);
        try {
            int i = inputStreamNewInputStream.read(bArr);
            CloseableKt.closeFinally(inputStreamNewInputStream, null);
            if (i < 4) {
                return null;
            }
            int i2 = bArr[0] & UByte.MAX_VALUE;
            int i3 = bArr[1] & UByte.MAX_VALUE;
            int i4 = bArr[2] & UByte.MAX_VALUE;
            int i5 = bArr[3] & UByte.MAX_VALUE;
            if (i2 == 80 && i3 == 75 && ((i4 == 3 && i5 == 4) || ((i4 == 5 && i5 == 6) || (i4 == 7 && i5 == 8)))) {
                return ArchiveFormat.ZIP;
            }
            return null;
        } catch (Throwable th) {
            try {
                throw th;
            } catch (Throwable th2) {
                CloseableKt.closeFinally(inputStreamNewInputStream, th);
                throw th2;
            }
        }
    }
}
