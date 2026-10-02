package com.exteragram.messenger.adblock.data;

/* JADX INFO: loaded from: classes.dex */
public class FilterListMetadata {
    private final Integer expires;
    private final String homepage;
    private final String redirect;
    private final String title;

    public FilterListMetadata(String str, String str2, String str3, int i) {
        this.homepage = str;
        this.title = str2;
        this.redirect = str3;
        this.expires = i == 0 ? null : Integer.valueOf(i);
    }

    public String getHomepage() {
        return this.homepage;
    }

    public String getTitle() {
        return this.title;
    }

    public String getRedirect() {
        return this.redirect;
    }

    public Integer getExpires() {
        return this.expires;
    }
}
