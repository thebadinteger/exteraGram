package com.exteragram.messenger.export.api;

import okhttp3.internal.url._UrlKt;

public class ApiWrap$ExportPersonalInfo {
    public String bio;
    public ApiWrap$User user;

    public ApiWrap$ExportPersonalInfo(ApiWrap$ExportPersonalInfo apiWrap$ExportPersonalInfo) {
        this.bio = _UrlKt.FRAGMENT_ENCODE_SET;
        this.user = apiWrap$ExportPersonalInfo.user;
        this.bio = apiWrap$ExportPersonalInfo.bio;
    }

    public ApiWrap$ExportPersonalInfo() {
        this.bio = _UrlKt.FRAGMENT_ENCODE_SET;
    }
}
