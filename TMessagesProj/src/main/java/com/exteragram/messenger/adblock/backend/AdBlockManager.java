package com.exteragram.messenger.adblock.backend;

import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.adblock.interop.AdBlock;
import com.exteragram.messenger.adblock.interop.NativeAdBlock;
import com.exteragram.messenger.utils.network.RemoteUtils;
import com.google.android.gms.cast.framework.media.NotificationOptions;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Utilities;

/* JADX INFO: loaded from: classes.dex */
public abstract class AdBlockManager {
    private static boolean loading;
    private static DispatchQueue queue;
    private static final String[] FILTERS = {"https://ublockorigin.github.io/uAssetsCDN/filters/filters.min.txt", "https://ublockorigin.github.io/uAssetsCDN/filters/badware.min.txt", "https://ublockorigin.github.io/uAssetsCDN/filters/privacy.min.txt", "https://ublockorigin.github.io/uAssetsCDN/filters/unbreak.min.txt", "https://ublockorigin.github.io/uAssetsCDN/filters/quick-fixes.min.txt", "https://filters.adtidy.org/extension/ublock/filters/11.txt", "https://filters.adtidy.org/extension/ublock/filters/2_without_easylist.txt", "https://cdn.jsdelivr.net/gh/uBlockOrigin/uAssetsCDN@main/thirdparties/easylist.txt", "https://cdn.jsdelivr.net/gh/uBlockOrigin/uAssetsCDN@main/thirdparties/easyprivacy.txt", "https://cdn.jsdelivr.net/gh/dimisa-RUAdList/RUAdListCDN@main/lists/ruadlist.ubo.min.txt"};
    private static final ArrayList<Runnable> readyCallbacks = new ArrayList<>();

    private static synchronized DispatchQueue getQueue() {
        try {
            if (queue == null) {
                queue = new DispatchQueue("AdBlockManager");
            }
        } catch (Throwable th) {
            throw th;
        }
        return queue;
    }

    public static boolean isAvailable() {
        return RemoteUtils.getBooleanConfigValue("use_adblock", false).booleanValue() && !NativeAdBlock.isLoadFailed();
    }

    public static boolean isActive() {
        return ExteraConfig.getEnableAdBlock() && AdBlock.isReady();
    }

    public static void initialize() {
        if (ExteraConfig.getEnableAdBlock()) {
            getQueue().postRunnable(new Runnable() { // from class: com.exteragram.messenger.adblock.backend.AdBlockManager$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    AdBlockManager.load(null);
                }
            });
        }
    }

    public static void preload() {
        if (ExteraConfig.getEnableAdBlock()) {
            getQueue().postRunnable(new Runnable() { // from class: com.exteragram.messenger.adblock.backend.AdBlockManager$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AdBlockManager.load(null);
                }
            }, NotificationOptions.SKIP_STEP_TEN_SECONDS_IN_MS);
        }
    }

    public static void setEnabled(final boolean z, final Runnable runnable) {
        ExteraConfig.setEnableAdBlock(z);
        getQueue().postRunnable(new Runnable() { // from class: com.exteragram.messenger.adblock.backend.AdBlockManager$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                AdBlockManager.$r8$lambda$VkMrvF4u0rpTiyBLPhtCni5XDeE(z, runnable);
            }
        });
    }

    public static /* synthetic */ void $r8$lambda$VkMrvF4u0rpTiyBLPhtCni5XDeE(boolean z, Runnable runnable) {
        if (z) {
            load(runnable);
        } else {
            readyCallbacks.clear();
            AdBlock.destroy();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void load(Runnable runnable) {
        if (ExteraConfig.getEnableAdBlock()) {
            if (AdBlock.isReady()) {
                if (runnable != null) {
                    AndroidUtilities.runOnUIThread(runnable);
                    return;
                }
                return;
            }
            if (RemoteUtils.getBooleanConfigValue("use_adblock", false).booleanValue() && NativeAdBlock.loadLibraries()) {
                if (runnable != null) {
                    readyCallbacks.add(runnable);
                }
                if (loading) {
                    return;
                }
                loading = true;
                ScriptletsManager scriptletsManager = ScriptletsManager.getInstance();
                if (!scriptletsManager.isDownloaded()) {
                    scriptletsManager.download(new Utilities.Callback() { // from class: com.exteragram.messenger.adblock.backend.AdBlockManager$$ExternalSyntheticLambda2
                        @Override // org.telegram.messenger.Utilities.Callback
                        public final void run(Object obj) {
                            AdBlockManager.getQueue().postRunnable(new Runnable() { // from class: com.exteragram.messenger.adblock.backend.AdBlockManager$$ExternalSyntheticLambda6
                                @Override // java.lang.Runnable
                                public final void run() {
                                    AdBlock.applyResources();
                                }
                            });
                        }
                    });
                }
                SubscriptionsManager subscriptionsManager = SubscriptionsManager.getInstance();
                if (subscriptionsManager.hasFilters()) {
                    build();
                    subscriptionsManager.update(FILTERS, null);
                } else {
                    subscriptionsManager.update(FILTERS, new Runnable() { // from class: com.exteragram.messenger.adblock.backend.AdBlockManager$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            AdBlockManager.getQueue().postRunnable(new Runnable() { // from class: com.exteragram.messenger.adblock.backend.AdBlockManager$$ExternalSyntheticLambda4
                                @Override // java.lang.Runnable
                                public final void run() {
                                    AdBlockManager.build();
                                }
                            });
                        }
                    });
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void build() {
        loading = false;
        if (ExteraConfig.getEnableAdBlock()) {
            AdBlock.build();
        }
        ArrayList<Runnable> arrayList = readyCallbacks;
        if (arrayList.isEmpty()) {
            return;
        }
        final ArrayList arrayList2 = new ArrayList(arrayList);
        arrayList.clear();
        if (AdBlock.isReady()) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.adblock.backend.AdBlockManager$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    AdBlockManager.$r8$lambda$VvvmAH8sWhBT5SVzdcMZl989bxQ(arrayList2);
                }
            });
        }
    }

    public static /* synthetic */ void $r8$lambda$VvvmAH8sWhBT5SVzdcMZl989bxQ(ArrayList arrayList) {
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            ((Runnable) obj).run();
        }
    }
}
