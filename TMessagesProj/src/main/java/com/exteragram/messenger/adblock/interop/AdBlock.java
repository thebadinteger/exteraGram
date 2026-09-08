package com.exteragram.messenger.adblock.interop;

import com.exteragram.messenger.adblock.backend.ScriptletsManager;
import com.exteragram.messenger.adblock.backend.SubscriptionsManager;
import com.exteragram.messenger.adblock.data.BlockResult;
import com.exteragram.messenger.adblock.data.UrlCosmeticResources;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.DispatchQueue;

public class AdBlock {
    private static final DispatchQueue queue = new DispatchQueue("adblock");
    private static final Object lock = new Object();
    private static long enginePtr = 0;
    private static long filterSetPtr = 0;

    public static void initialize() {
        queue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                AdBlock.$r8$lambda$izUs8blSAW4dbII3p0uS4hFwS4A();
            }
        });
    }

    public static void $r8$lambda$SrkwfasH49b3Koy0_f99c2F0mmE() {
        synchronized (lock) {
            try {
                long j = enginePtr;
                if (j != 0) {
                    NativeAdBlock.destroyEngine(j);
                    enginePtr = 0L;
                }
                if (filterSetPtr != 0) {
                    filterSetPtr = 0L;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void reload() {
        destroy();
        initialize();
    }

    private static void initializeInner() {
        filterSetPtr = NativeAdBlock.createFilterSet(new String[0]);
        Iterator<String> it = SubscriptionsManager.getInstance().getSubscriptionFilePaths().iterator();
        while (it.hasNext()) {
            NativeAdBlock.addFilters(filterSetPtr, it.next());
        }
        long jCreateEngine = NativeAdBlock.createEngine(filterSetPtr);
        Collection<ScriptletsManager.Scriptlet> collectionIterScriptlets = ScriptletsManager.getInstance().iterScriptlets();
        int size = collectionIterScriptlets.size();
        String[] strArr = new String[size];
        String[][] strArr2 = new String[size][];
        String[] strArr3 = new String[size];
        String[] strArr4 = new String[size];
        int i = 0;
        for (ScriptletsManager.Scriptlet scriptlet : collectionIterScriptlets) {
            strArr[i] = scriptlet.filename;
            List<String> list = scriptlet.aliases;
            strArr2[i] = list != null ? (String[]) list.toArray(new String[0]) : new String[0];
            strArr3[i] = ScriptletsManager.getExtension(scriptlet.filename);
            strArr4[i] = scriptlet.content;
            i++;
        }
        NativeAdBlock.useResources(jCreateEngine, strArr, strArr2, strArr3, strArr4);
        enginePtr = jCreateEngine;
    }

    public static BlockResult getBlockResult(String str, String str2, String str3) {
        long j = enginePtr;
        if (j == 0) {
            return null;
        }
        return NativeAdBlock.shouldBlock(j, str, str2, str3);
    }

    public static UrlCosmeticResources getCosmeticResources(String str) {
        long j = enginePtr;
        if (j == 0) {
            return null;
        }
        return NativeAdBlock.getCosmeticResources(j, str);
    }

    public static String[] getHiddenSelectors(String[] strArr, String[] strArr2, String[] strArr3) {
        long j = enginePtr;
        if (j == 0) {
            return null;
        }
        return NativeAdBlock.getHiddenSelectors(j, strArr, strArr2, strArr3);
    }
}
