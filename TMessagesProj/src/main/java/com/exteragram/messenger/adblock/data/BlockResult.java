package com.exteragram.messenger.adblock.data;

/* JADX INFO: loaded from: classes4.dex */
public class BlockResult {
    private final String exception;
    private final String filter;
    private final boolean important;
    private final boolean matched;
    private final String redirect;
    private final String rewrittenUrl;

    public BlockResult(boolean z, boolean z2, String str, String str2, String str3, String str4) {
        this.matched = z;
        this.important = z2;
        this.redirect = str;
        this.rewrittenUrl = str2;
        this.exception = str3;
        this.filter = str4;
    }

    public boolean isMatched() {
        return this.matched;
    }

    public boolean isImportant() {
        return this.important;
    }

    public String getRedirect() {
        return this.redirect;
    }

    public String getRewrittenUrl() {
        return this.rewrittenUrl;
    }

    public String getException() {
        return this.exception;
    }

    public String getFilter() {
        return this.filter;
    }
}
