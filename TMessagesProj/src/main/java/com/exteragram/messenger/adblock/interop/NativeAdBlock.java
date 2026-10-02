package com.exteragram.messenger.adblock.interop;

import com.exteragram.messenger.adblock.data.BlockResult;
import com.exteragram.messenger.adblock.data.FilterListMetadata;
import com.exteragram.messenger.adblock.data.UrlCosmeticResources;
import org.telegram.messenger.FileLog;

/* JADX INFO: loaded from: classes.dex */
public class NativeAdBlock {
    private static volatile Boolean loaded;

    public static native FilterListMetadata addFilters(long j, String str);

    public static native long createEngine(long j);

    public static native long createFilterSet(String[] strArr);

    public static native void destroyEngine(long j);

    public static native void destroyFilterSet(long j);

    public static native UrlCosmeticResources getCosmeticResources(long j, String str);

    public static native String[] getHiddenSelectors(long j, String[] strArr, String[] strArr2, String[] strArr3);

    public static native BlockResult shouldBlock(long j, String str, String str2, String str3);

    public static native void useResources(long j, String[] strArr, String[][] strArr2, String[] strArr3, String[] strArr4);

    public static synchronized boolean loadLibraries() {
        try {
            if (loaded == null) {
                try {
                    System.loadLibrary("etgadblock");
                    loaded = Boolean.TRUE;
                } catch (Throwable th) {
                    FileLog.e(th);
                    loaded = Boolean.FALSE;
                }
            }
        } catch (Throwable th2) {
            throw th2;
        }
        return loaded.booleanValue();
    }

    public static boolean isLoadFailed() {
        return Boolean.FALSE.equals(loaded);
    }
}
