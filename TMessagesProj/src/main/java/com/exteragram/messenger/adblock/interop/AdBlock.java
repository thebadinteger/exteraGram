package com.exteragram.messenger.adblock.interop;

import com.exteragram.messenger.adblock.backend.ScriptletsManager;
import com.exteragram.messenger.adblock.backend.SubscriptionsManager;
import com.exteragram.messenger.adblock.data.BlockResult;
import com.exteragram.messenger.adblock.data.UrlCosmeticResources;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/* JADX INFO: loaded from: classes.dex */
public class AdBlock {
    private static final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private static volatile long enginePtr = 0;

    public static boolean isReady() {
        return enginePtr != 0;
    }

    public static void build() {
        List<String> subscriptionFilePaths = SubscriptionsManager.getInstance().getSubscriptionFilePaths();
        if (subscriptionFilePaths.isEmpty()) {
            return;
        }
        long jCreateFilterSet = NativeAdBlock.createFilterSet(new String[0]);
        Iterator<String> it = subscriptionFilePaths.iterator();
        while (it.hasNext()) {
            NativeAdBlock.addFilters(jCreateFilterSet, it.next());
        }
        long jCreateEngine = NativeAdBlock.createEngine(jCreateFilterSet);
        useResources(jCreateEngine, ScriptletsManager.getInstance().iterScriptlets());
        swap(jCreateEngine);
    }

    public static void destroy() {
        swap(0L);
    }

    public static void applyResources() {
        Collection<ScriptletsManager.Scriptlet> collectionIterScriptlets = ScriptletsManager.getInstance().iterScriptlets();
        lock.writeLock().lock();
        try {
            if (enginePtr != 0) {
                useResources(enginePtr, collectionIterScriptlets);
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private static void swap(long j) {
        ReentrantReadWriteLock reentrantReadWriteLock = lock;
        reentrantReadWriteLock.writeLock().lock();
        try {
            long j2 = enginePtr;
            enginePtr = j;
            reentrantReadWriteLock.writeLock().unlock();
            if (j2 != 0) {
                NativeAdBlock.destroyEngine(j2);
            }
        } catch (Throwable th) {
            lock.writeLock().unlock();
            throw th;
        }
    }

    private static void useResources(long j, Collection<ScriptletsManager.Scriptlet> collection) {
        int size = collection.size();
        String[] strArr = new String[size];
        String[][] strArr2 = new String[size][];
        String[] strArr3 = new String[size];
        String[] strArr4 = new String[size];
        int i = 0;
        for (ScriptletsManager.Scriptlet scriptlet : collection) {
            strArr[i] = scriptlet.filename;
            List<String> list = scriptlet.aliases;
            strArr2[i] = list != null ? (String[]) list.toArray(new String[0]) : new String[0];
            strArr3[i] = ScriptletsManager.getExtension(scriptlet.filename);
            strArr4[i] = scriptlet.content;
            i++;
        }
        NativeAdBlock.useResources(j, strArr, strArr2, strArr3, strArr4);
    }

    public static BlockResult getBlockResult(String str, String str2, String str3) {
        lock.readLock().lock();
        try {
            return enginePtr != 0 ? NativeAdBlock.shouldBlock(enginePtr, str, str2, str3) : null;
        } finally {
            lock.readLock().unlock();
        }
    }

    public static UrlCosmeticResources getCosmeticResources(String str) {
        lock.writeLock().lock();
        try {
            return enginePtr != 0 ? NativeAdBlock.getCosmeticResources(enginePtr, str) : null;
        } finally {
            lock.writeLock().unlock();
        }
    }

    public static String[] getHiddenSelectors(String[] strArr, String[] strArr2, String[] strArr3) {
        lock.writeLock().lock();
        try {
            return enginePtr != 0 ? NativeAdBlock.getHiddenSelectors(enginePtr, strArr, strArr2, strArr3) : null;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
