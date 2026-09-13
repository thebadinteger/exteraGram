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
        fetchHistory(new Utilities.Callback2() { // from class: com.exteragram.messenger.feed.ads.FeedAdController$$ExternalSyntheticLambda0
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                FeedAdController.this.lambda$ensureLoaded$0((TLRPC.messages_Messages) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$ensureLoaded$0(TLRPC.messages_Messages messages_messages, TLRPC.TL_error tL_error) {
        this.loading = false;
        this.lastLoadTime = SystemClock.elapsedRealtime();
        if (tL_error == null && messages_messages != null) {
            ArrayList<FeedAd> arrayList = FeedAdParser.parse(messages_messages);
            this.allAds.clear();
            this.allAds.addAll(arrayList);
            recomputeEligible();
        }
        ArrayList arrayList2 = new ArrayList(this.pendingLoadCallbacks);
        this.pendingLoadCallbacks.clear();
        for (int i = 0; i < arrayList2.size(); i++) {
            ((Runnable) arrayList2.get(i)).run();
        }
    }

    public void recomputeEligible() {
        ArrayList<FeedAd> arrayList = new ArrayList<>(this.allAds.size());
        for (int i = 0; i < this.allAds.size(); i++) {
            if (isEligible(this.allAds.get(i))) {
                arrayList.add(this.allAds.get(i));
            }
        }
        boolean z = arrayList.size() == this.eligibleAds.size();
        for (int i2 = 0; z && i2 < arrayList.size(); i2++) {
            z = TextUtils.equals(arrayList.get(i2).id, this.eligibleAds.get(i2).id) && arrayList.get(i2).weight == this.eligibleAds.get(i2).weight;
        }
        this.eligibleAds = arrayList;
        ArrayList<FeedAd> arrayList2 = this.rotation;
        if (!z) {
            arrayList2.clear();
            this.rotationIndex = 0;
            return;
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        HashMap map = new HashMap();
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            map.put(arrayList.get(i3).id, arrayList.get(i3));
        }
        for (int i4 = 0; i4 < this.rotation.size(); i4++) {
            FeedAd feedAd = (FeedAd) map.get(this.rotation.get(i4).id);
            if (feedAd != null) {
                this.rotation.set(i4, feedAd);
            }
        }
    }

    private boolean isEligible(FeedAd feedAd) {
        if (!matchesLocale(feedAd.locales)) {
            return false;
        }
        boolean zIsPremium = UserConfig.getInstance(this.currentAccount).isPremium();
        int i = feedAd.premium;
        if ((i == 1 && !zIsPremium) || (i == 2 && zIsPremium)) {
            return false;
        }
        boolean zHasBadge = BadgesController.INSTANCE.hasBadge();
        int i2 = feedAd.badge;
        return (i2 != 1 || zHasBadge) && !(i2 == 2 && zHasBadge);
    }

    private boolean matchesLocale(Set<String> set) {
        LocaleController.LocaleInfo currentLocaleInfo;
        return set == null || set.isEmpty() || (currentLocaleInfo = LocaleController.getInstance().getCurrentLocaleInfo()) == null || contains(set, currentLocaleInfo.getLangCode()) || contains(set, currentLocaleInfo.shortName) || contains(set, currentLocaleInfo.baseLangCode);
    }

    private static boolean contains(Set<String> set, String str) {
        return str != null && set.contains(str.toLowerCase());
    }

    private void fetchHistory(final Utilities.Callback2<TLRPC.messages_Messages, TLRPC.TL_error> callback2) {
        final AccountInstance accountInstance = AccountInstance.getInstance(this.currentAccount);
        final TLRPC.TL_messages_getHistory tL_messages_getHistory = new TLRPC.TL_messages_getHistory();
        tL_messages_getHistory.peer = accountInstance.getMessagesController().getInputPeer(-3514621311L);
        tL_messages_getHistory.offset_id = 0;
        tL_messages_getHistory.limit = 75;
        final Runnable runnable = new Runnable() { // from class: com.exteragram.messenger.feed.ads.FeedAdController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                accountInstance.getConnectionsManager().sendRequest(tL_messages_getHistory, new RequestDelegate() { // from class: com.exteragram.messenger.feed.ads.FeedAdController$$ExternalSyntheticLambda3
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.feed.ads.FeedAdController$$ExternalSyntheticLambda4
                            @Override // java.lang.Runnable
                            public final void run() {
                                FeedAdController.m1163$r8$lambda$suri1kpT0eJ8sN4FqCkAUB1c0c(tL_error, tLObject, callback2);
                            }
                        });
                    }
                });
            }
        };
        TLRPC.InputPeer inputPeer = tL_messages_getHistory.peer;
        if (inputPeer != null && inputPeer.access_hash != 0) {
            runnable.run();
        } else {
            ChatUtils.getInstance(this.currentAccount).resolveChannel("exteraFeedAds", new Utilities.Callback() { // from class: com.exteragram.messenger.feed.ads.FeedAdController$$ExternalSyntheticLambda2
                @Override // org.telegram.messenger.Utilities.Callback
                public final void run(Object obj) {
                    FeedAdController.$r8$lambda$F74A0h3yQjwWcCURFdXXPBFC7aY(tL_messages_getHistory, runnable, callback2, (TLRPC.Chat) obj);
                }
            });
        }
    }

    /* JADX INFO: renamed from: $r8$lambda$suri1kp-T0eJ8sN4FqCkAUB1c0c, reason: not valid java name */
    public static /* synthetic */ void m1163$r8$lambda$suri1kpT0eJ8sN4FqCkAUB1c0c(TLRPC.TL_error tL_error, TLObject tLObject, Utilities.Callback2 callback2) {
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
