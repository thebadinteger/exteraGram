package com.exteragram.messenger.adblock.data;

/* JADX INFO: loaded from: classes4.dex */
public class UrlCosmeticResources {
    private final String[] exceptions;
    private final boolean genericHide;
    private final String[] hideSelectors;
    private final String injectedScript;
    private final String[] proceduralActions;

    public UrlCosmeticResources(String[] strArr, String[] strArr2, String[] strArr3, String str, boolean z) {
        this.hideSelectors = strArr;
        this.proceduralActions = strArr2;
        this.exceptions = strArr3;
        this.injectedScript = str;
        this.genericHide = z;
    }

    public String[] getHideSelectors() {
        return this.hideSelectors;
    }

    public String[] getProceduralActions() {
        return this.proceduralActions;
    }

    public String[] getExceptions() {
        return this.exceptions;
    }

    public String getInjectedScript() {
        return this.injectedScript;
    }

    public boolean isGenericHide() {
        return this.genericHide;
    }
}
