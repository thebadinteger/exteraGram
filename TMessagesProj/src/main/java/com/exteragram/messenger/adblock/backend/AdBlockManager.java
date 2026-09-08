package com.exteragram.messenger.adblock.backend;

import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.adblock.interop.AdBlock;
import com.exteragram.messenger.adblock.interop.NativeAdBlock;
import com.exteragram.messenger.utils.network.RemoteUtils;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.FileLog;

public abstract class AdBlockManager {
    private static final String[] FILTERS = {"https://ublockorigin.github.io/uAssetsCDN/filters/filters.min.txt", "https://ublockorigin.github.io/uAssetsCDN/filters/badware.min.txt", "https://ublockorigin.github.io/uAssetsCDN/filters/privacy.min.txt", "https://ublockorigin.github.io/uAssetsCDN/filters/unbreak.min.txt", "https://ublockorigin.github.io/uAssetsCDN/filters/quick-fixes.min.txt", "https://filters.adtidy.org/extension/ublock/filters/11.txt", "https://filters.adtidy.org/extension/ublock/filters/2_without_easylist.txt", "https://cdn.jsdelivr.net/gh/uBlockOrigin/uAssetsCDN@main/thirdparties/easylist.txt", "https://cdn.jsdelivr.net/gh/dimisa-RUAdList/RUAdListCDN@main/lists/ruadlist.ubo.min.txt"};

    public static void initialize() {
        if (RemoteUtils.getBooleanConfigValue("use_adblock", false).booleanValue() && ExteraConfig.getEnableAdBlock() && NativeAdBlock.loadLibraries()) {
            if (!ScriptletsManager.getInstance().isDownloaded()) {
                ScriptletsManager.getInstance().download(new ScriptletsManager.DownloadCallback() { 
                    @Override 
                    public void onProgress(int i, int i2) {
                        FileLog.d("scriptlet download progress: " + i + "/" + i2);
                        if (i == i2) {
                            AdBlockManager.continueInitialize();
                        }
                    }

                    @Override 
                    public void onError() {
                        FileLog.e("unable to download all scriptlets");
                        AdBlockManager.continueInitialize();
                    }
                });
            } else {
                continueInitialize();
            }
        }
    }

    public static void continueInitialize() {
        if (SubscriptionsManager.getInstance().getSubscriptions().isEmpty()) {
            addDefaultFilters();
        } else {
            SubscriptionsManager.getInstance().initialize(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    AdBlock.reload();
                }
            });
        }
    }

    private static void addDefaultFilters() {
        final AtomicInteger atomicInteger = new AtomicInteger(0);
        for (final String str : FILTERS) {
            SubscriptionsManager.getInstance().subscribe(str, new SubscriptionsManager.SubscriptionCallback() { 
                @Override 
                public final void onComplete(boolean z) {
                    AdBlockManager.m832$r8$lambda$fWhGQ48lIRq6JSa8wErVhU5_b8(str, atomicInteger, z);
                }
            });
        }
    }

    public static /* synthetic */ void m832$r8$lambda$fWhGQ48lIRq6JSa8wErVhU5_b8(String str, AtomicInteger atomicInteger, boolean z) {
        if (z) {
            FileLog.d("filter loaded: " + str);
        } else {
            FileLog.e("filter failed to load: " + str);
        }
        if (atomicInteger.incrementAndGet() == FILTERS.length) {
            FileLog.d("all filters loaded");
            AdBlock.reload();
        }
    }
}
