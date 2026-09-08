package com.exteragram.messenger.adblock.interop;

import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.adblock.data.BlockResult;
import com.exteragram.messenger.adblock.data.FilterListMetadata;
import com.exteragram.messenger.adblock.data.UrlCosmeticResources;

public class NativeAdBlock {
    public static native FilterListMetadata addFilters(long j, String str);

    public static native long createEngine(long j);

    public static native long createFilterSet(String[] strArr);

    public static native void destroyEngine(long j);

    public static native void destroyFilterSet(long j);

    public static native UrlCosmeticResources getCosmeticResources(long j, String str);

    public static native String[] getHiddenSelectors(long j, String[] strArr, String[] strArr2, String[] strArr3);

    public static native BlockResult shouldBlock(long j, String str, String str2, String str3);

    public static native void useResources(long j, String[] strArr, String[][] strArr2, String[] strArr3, String[] strArr4);

    public static boolean loadLibraries() {
        try {
            System.loadLibrary("etgadblock");
            return true;
        } catch (Throwable unused) {
            ExteraConfig.setEnableAdBlock(false);
            return false;
        }
    }
}
