package com.exteragram.messenger.export.api;

import okhttp3.internal.url._UrlKt;

public class ApiWrap$File {
    public byte[] content;
    public ApiWrap$FileLocation location;
    public String suggestedPath;
    public long size = 0;
    public int dcId = 0;
    public String relativePath = _UrlKt.FRAGMENT_ENCODE_SET;
    public SkipReason skipReason = SkipReason.None;

    public enum SkipReason {
        None,
        Unavailable,
        FileType,
        FileSize,
        DateLimits
    }
}
