package com.exteragram.messenger.feed;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.tgnet.TLRPC;

public class FeedChannelRegistry implements NotificationCenter.NotificationCenterDelegate {
    private static final FeedChannelRegistry[] instances = new FeedChannelRegistry[16];
    private static final Object[] locks = new Object[16];
    private boolean built;
    public final int currentAccount;
    private boolean rebuildScheduled;
    private final HashSet<Long> channelIds = new HashSet<>();
    private final ArrayList<Listener> listeners = new ArrayList<>();
    private final Runnable rebuildRunnable = new Runnable() { 
        @Override // java.lang.Runnable
        public final void run() {
            FeedChannelRegistry.this.rebuildScheduled = false;
            FeedChannelRegistry.this.rebuild(true);
        }
    };

    public interface Listener {
        void onFeedChannelsChanged(HashSet<Long> hashSet, HashSet<Long> hashSet2);
    }

    static {
        for (int i = 0; i < 16; i++) {
            locks[i] = new Object();
        }
    }

    private FeedChannelRegistry(final int i) {
        this.currentAccount = i;
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                NotificationCenter.getInstance(i).addObserver(FeedChannelRegistry.this, NotificationCenter.dialogsNeedReload);
            }
        });
    }

    public static FeedChannelRegistry getInstance(int i) {
        FeedChannelRegistry feedChannelRegistry;
        FeedChannelRegistry[] feedChannelRegistryArr = instances;
        FeedChannelRegistry feedChannelRegistry2 = feedChannelRegistryArr[i];
        if (feedChannelRegistry2 != null) {
            return feedChannelRegistry2;
        }
        synchronized (locks[i]) {
            try {
                feedChannelRegistry = feedChannelRegistryArr[i];
                if (feedChannelRegistry == null) {
                    feedChannelRegistry = new FeedChannelRegistry(i);
                    feedChannelRegistryArr[i] = feedChannelRegistry;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return feedChannelRegistry;
    }

    public void addListener(Listener listener) {
        ensureBuilt();
        if (this.listeners.contains(listener)) {
            return;
        }
        this.listeners.add(listener);
    }

    private void ensureBuilt() {
        if (this.built) {
            return;
        }
        this.built = true;
        rebuild(false);
    }

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.dialogsNeedReload) {
            ensureBuilt();
            if (this.rebuildScheduled) {
                return;
            }
            this.rebuildScheduled = true;
            AndroidUtilities.runOnUIThread(this.rebuildRunnable, 500L);
        }
    }

    private void rebuild(boolean z) {
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        LongSparseArray<TLRPC.Dialog> longSparseArray = messagesController.dialogs_dict;
        HashSet<Long> hashSet = new HashSet();
        for (int i = 0; i < longSparseArray.size(); i++) {
            TLRPC.Dialog dialogValueAt = longSparseArray.valueAt(i);
            if (dialogValueAt != null && DialogObject.isChatDialog(dialogValueAt.id) && FeedController.isEligibleChannel(messagesController.getChat(Long.valueOf(-dialogValueAt.id)))) {
                hashSet.add(Long.valueOf(dialogValueAt.id));
            }
        }
        HashSet<Long> hashSet2 = null;
        HashSet<Long> hashSet3 = null;
        for (Long l : hashSet) {
            if (!this.channelIds.contains(l)) {
                if (hashSet3 == null) {
                    hashSet3 = new HashSet<>();
                }
                hashSet3.add(l);
            }
        }
        for (Long l2 : this.channelIds) {
            if (!hashSet.contains(l2)) {
                if (hashSet2 == null) {
                    hashSet2 = new HashSet<>();
                }
                hashSet2.add(l2);
            }
        }
        if (hashSet3 == null && hashSet2 == null) {
            return;
        }
        this.channelIds.clear();
        this.channelIds.addAll(hashSet);
        if (z) {
            if (hashSet3 == null) {
                hashSet3 = new HashSet<>();
            }
            if (hashSet2 == null) {
                hashSet2 = new HashSet<>();
            }
            for (int size = this.listeners.size() - 1; size >= 0; size--) {
                this.listeners.get(size).onFeedChannelsChanged(hashSet3, hashSet2);
            }
        }
    }
}
