package com.exteragram.messenger.feed.ads;

import android.os.SystemClock;
import android.text.TextUtils;
import com.exteragram.messenger.badges.BadgesController;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.exteragram.messenger.utils.network.RemoteUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public final class FeedAdController {
    private static final FeedAdController[] instances = new FeedAdController[16];
    private static final Object[] locks = new Object[16];
    public final int currentAccount;
    private long lastLoadTime;
    private boolean loading;
    private int rotationIndex;
    private final ArrayList<FeedAd> allAds = new ArrayList<>();
    private ArrayList<FeedAd> eligibleAds = new ArrayList<>();
    private final ArrayList<FeedAd> rotation = new ArrayList<>();
    private final ArrayList<Runnable> pendingLoadCallbacks = new ArrayList<>();

    static {
        for (int i = 0; i < 16; i++) {
            locks[i] = new Object();
        }
    }

    public static FeedAdController getInstance(int i) {
        FeedAdController feedAdController;
        FeedAdController[] feedAdControllerArr = instances;
        FeedAdController feedAdController2 = feedAdControllerArr[i];
        if (feedAdController2 != null) {
            return feedAdController2;
        }
        synchronized (locks[i]) {
            try {
                feedAdController = feedAdControllerArr[i];
                if (feedAdController == null) {
                    feedAdController = new FeedAdController(i);
                    feedAdControllerArr[i] = feedAdController;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return feedAdController;
    }

    private FeedAdController(int i) {
        this.currentAccount = i;
    }

    public boolean isEnabled() {
        return RemoteUtils.getBooleanConfigValue("feed_ads_enabled", true).booleanValue() && !this.eligibleAds.isEmpty();
    }

    public int getFirstAfter() {
        return Math.max(1, RemoteUtils.getIntConfigValue("feed_ad_first_after", 30).intValue());
    }

    public int getMinTrailing() {
        return Math.max(0, RemoteUtils.getIntConfigValue("feed_ad_min_trailing", 2).intValue());
    }

    public int getBaseEvery() {
        return Math.max(1, RemoteUtils.getIntConfigValue("feed_ad_every", 30).intValue());
    }

    public int getEffectiveEvery() {
        int iMax;
        int baseEvery = getBaseEvery();
        int size = this.eligibleAds.size();
        if (size <= 1) {
            iMax = Math.max(1, RemoteUtils.getIntConfigValue("feed_ad_spacing_pool1", 3).intValue());
        } else {
            if (size != 2) {
                return baseEvery;
            }
            iMax = Math.max(1, RemoteUtils.getIntConfigValue("feed_ad_spacing_pool2", 2).intValue());
        }
        return baseEvery * iMax;
    }

    public FeedAd nextAd() {
        if (this.eligibleAds.isEmpty()) {
            return null;
        }
        if (this.rotation.isEmpty() || this.rotationIndex >= this.rotation.size()) {
            reshuffleRotation();
        }
        ArrayList<FeedAd> arrayList = this.rotation;
        int i = this.rotationIndex;
        this.rotationIndex = i + 1;
        return arrayList.get(i);
    }

    private void reshuffleRotation() {
        this.rotation.clear();
        ArrayList<FeedAd> arrayList = this.eligibleAds;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            FeedAd feedAd = arrayList.get(i);
            i++;
            FeedAd feedAd2 = feedAd;
            for (int i2 = 0; i2 < Math.max(1, feedAd2.weight); i2++) {
                this.rotation.add(feedAd2);
            }
        }
        Collections.shuffle(this.rotation);
        this.rotationIndex = 0;
    }

    public void ensureLoaded(Runnable runnable) {
        if (!this.loading && this.lastLoadTime != 0 && SystemClock.elapsedRealtime() - this.lastLoadTime < 1800000) {
            recomputeEligible();
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        if (runnable != null) {
            this.pendingLoadCallbacks.add(runnable);
        }
        if (this.loading) {
            return;
        }
        this.loading = true;
        fetchHistory(new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$ensureLoaded$0((TLRPC.messages_Messages) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void m1163$r8$lambda$suri1kpT0eJ8sN4FqCkAUB1c0c(TLRPC.TL_error tL_error, TLObject tLObject, Utilities.Callback2 callback2) {
        if (tL_error != null || !(tLObject instanceof TLRPC.messages_Messages)) {
            callback2.run(null, tL_error);
        } else {
            callback2.run((TLRPC.messages_Messages) tLObject, null);
        }
    }

    public static /* synthetic */ void $r8$lambda$F74A0h3yQjwWcCURFdXXPBFC7aY(TLRPC.TL_messages_getHistory tL_messages_getHistory, Runnable runnable, Utilities.Callback2 callback2, TLRPC.Chat chat) {
        if (chat != null && chat.id == 3514621311L) {
            TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
            tL_inputPeerChannel.channel_id = chat.id;
            tL_inputPeerChannel.access_hash = chat.access_hash;
            tL_messages_getHistory.peer = tL_inputPeerChannel;
            runnable.run();
            return;
        }
        callback2.run(null, null);
    }
}
