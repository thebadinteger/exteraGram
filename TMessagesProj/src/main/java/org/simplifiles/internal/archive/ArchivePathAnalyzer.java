package org.simplifiles.internal.archive;

import kotlin.Metadata;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J \u0010\b\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0002J \u0010\r\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000bH\u0002J\u0010\u0010\u000e\u001a\u00020\t2\u0006\u0010\u0006\u001a\u00020\u0007H\u0002¨\u0006\u000f"}, d2 = {"Lorg/simplifiles/internal/archive/ArchivePathAnalyzer;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "analyze", "Lorg/simplifiles/internal/archive/ArchivePathAnalysis;", "path", _UrlKt.FRAGMENT_ENCODE_SET, "isCurrentDirectorySegment", _UrlKt.FRAGMENT_ENCODE_SET, "start", _UrlKt.FRAGMENT_ENCODE_SET, "length", "isParentDirectorySegment", "isWindowsDriveAbsolute", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
public final class ArchivePathAnalyzer {
    public static final ArchivePathAnalyzer INSTANCE = new ArchivePathAnalyzer();

    private ArchivePathAnalyzer() {
    }

    public final ArchivePathAnalysis analyze(String path) {
        boolean z;
        boolean z2;
        boolean z3 = false;
        String string = null;
        boolean z4 = path.startsWith("/") || path.startsWith("\\") || isWindowsDriveAbsolute(path);
        if (path.length() == 0) {
            return new ArchivePathAnalysis(null, true, z4, false);
        }
        StringBuilder sb = new StringBuilder(path.length());
        int length = path.length();
        if (length >= 0) {
            int i = 0;
            int i2 = 0;
            z = false;
            z2 = false;
            while (true) {
                if (i >= path.length() || path.charAt(i) == '/' || path.charAt(i) == '\\') {
                    int i3 = i - i2;
                    if (i3 > 0 && !isCurrentDirectorySegment(path, i2, i3)) {
                        if (isParentDirectorySegment(path, i2, i3)) {
                            z = true;
                            z2 = true;
                        } else {
                            if (!z) {
                                if (sb.length() > 0) {
                                    sb.append('/');
                                }
                                sb.append((CharSequence) path, i2, i);
                            }
                            z2 = true;
                        }
                    }
                    i2 = i + 1;
                }
                if (i == length) {
                    break;
                }
                i++;
            }
        } else {
            z = false;
            z2 = false;
        }
        if (!z && sb.length() != 0) {
            string = sb.toString();
        }
        if (!z && !z2) {
            z3 = true;
        }
        return new ArchivePathAnalysis(string, z3, z4, z);
    }

    private final boolean isCurrentDirectorySegment(String path, int start, int length) {
        return length == 1 && path.charAt(start) == '.';
    }

    private final boolean isParentDirectorySegment(String path, int start, int length) {
        return length == 2 && path.charAt(start) == '.' && path.charAt(start + 1) == '.';
    }

    private final boolean isWindowsDriveAbsolute(String path) {
        return path.length() >= 2 && path.charAt(1) == ':' && Character.isLetter(path.charAt(0)) && (path.length() == 2 || path.charAt(2) == '/' || path.charAt(2) == '\\');
    }
}
