package com.exteragram.messenger.feed;

import android.util.SparseArray;
import android.util.SparseIntArray;
import androidx.collection.LongSparseArray;
import com.exteragram.messenger.ExteraConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;

public class FeedController implements NotificationCenter.NotificationCenterDelegate {
    private static final FeedController[] Instance = new FeedController[16];
    private static final Object[] lockObjects = new Object[16];
    private int attemptRounds;
    private final FeedBackfillCoordinator backfill;
    private int cachedIncludedChannelCount;
    private final int closedRefreshGuid;
    private final Runnable closedRefreshRunnable;
    private boolean closedRefreshScheduled;
    private int configGeneration;
    public final int currentAccount;
    private SavedScrollPosition drawerScrollPosition;
    private boolean hasChannels;
    private boolean hasIncludedChannels;
    private int heldGuid;
    private int heldLoadIndex;
    private final ArrayList<int[]> initialLoadWaiters;
    private boolean initialUnreadScrollPending;
    private final FeedTimelineLoader loader;
    private boolean loading;
    private boolean loadingNewer;
    private boolean newerPagingBoundsDirty;
    private boolean olderPagingBoundsDirty;
    private int resumedUiClients;
    private int sessionGeneration;
    private int staleEnumerationRetries;
    private final FeedStore store;
    private int uiActiveClients;
    private final FeedUnreadTracker unreadTracker;

    public interface ChannelsCallback {
        void onChannels(ArrayList<TLRPC.Chat> arrayList, int i, boolean z, int i2);
    }

    static {
        for (int i = 0; i < 16; i++) {
            lockObjects[i] = new Object();
        }
    }

    public static FeedController peekInstance(int i) {
        return Instance[i];
    }

    public static FeedController getInstance(int i) {
        FeedController feedController;
        FeedController[] feedControllerArr = Instance;
        FeedController feedController2 = feedControllerArr[i];
        if (feedController2 != null) {
            return feedController2;
        }
        synchronized (lockObjects[i]) {
            try {
                feedController = feedControllerArr[i];
                if (feedController == null) {
                    feedController = new FeedController(i);
                    feedControllerArr[i] = feedController;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return feedController;
    }

    public static final class SavedScrollPosition {
        public final long dialogId;
        public final int messageId;
        public final int offsetTop;

        private SavedScrollPosition(long j, int i, int i2) {
            this.dialogId = j;
            this.messageId = i;
            this.offsetTop = i2;
        }
    }

    private FeedController(final int i) {
        FeedStore feedStore = new FeedStore();
        this.store = feedStore;
        this.initialUnreadScrollPending = true;
        this.initialLoadWaiters = new ArrayList<>();
        this.closedRefreshGuid = ConnectionsManager.generateClassGuid();
        this.closedRefreshRunnable = new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                FeedController.this.runClosedRefresh();
            }
        };
        this.currentAccount = i;
        this.unreadTracker = new FeedUnreadTracker(i, feedStore.getMessages());
        this.loader = new FeedTimelineLoader(i);
        this.backfill = new FeedBackfillCoordinator(i, new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                FeedController.this.onBackfillRoundFinished();
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                FeedController.this.lambda$new$0(i);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0(int i) {
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagesDidLoad);
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.loadingMessagesFailed);
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagesDeleted);
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.historyCleared);
        NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.didReceiveNewMessages);
        FeedChannelRegistry.getInstance(i).addListener(new FeedChannelRegistry.Listener() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda7
            @Override // com.exteragram.messenger.feed.FeedChannelRegistry.Listener
            public final void onFeedChannelsChanged(HashSet hashSet, HashSet hashSet2) {
                FeedController.this.onFeedChannelsChanged(hashSet, hashSet2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onFeedChannelsChanged(HashSet<Long> hashSet, HashSet<Long> hashSet2) {
        this.loader.invalidateChannelCache();
        Iterator<Long> it = hashSet2.iterator();
        while (it.hasNext()) {
            deleteHistory(it.next().longValue(), Integer.MAX_VALUE);
        }
        if (hashSet.isEmpty()) {
            NotificationCenter.getInstance(this.currentAccount).postNotificationNameOnUIThread(NotificationCenter.feedNeedReload, Boolean.FALSE);
        } else {
            reconcileChannelSet(new Utilities.Callback() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda19
                @Override // org.telegram.messenger.Utilities.Callback
                public final void run(Object obj) {
                    FeedController.this.lambda$onFeedChannelsChanged$1((Boolean) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onFeedChannelsChanged$1(Boolean bool) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationNameOnUIThread(NotificationCenter.feedNeedReload, bool);
    }

    public FeedStore getStore() {
        return this.store;
    }

    public ArrayList<MessageObject> getMessages() {
        return this.store.getMessages();
    }

    public boolean isLoading() {
        return this.loading || this.loadingNewer;
    }

    public boolean hasMessagesForDialog(long j) {
        return this.store.hasMessagesForDialog(j);
    }

    public boolean hasChannels() {
        return this.hasChannels;
    }

    public boolean hasIncludedChannels() {
        return this.hasIncludedChannels;
    }

    public int getIncludedChannelCount() {
        return this.cachedIncludedChannelCount;
    }

    public void setUiActive(boolean z) {
        int i = this.uiActiveClients;
        if (!z) {
            if (i == 0) {
                return;
            }
            int i2 = i - 1;
            this.uiActiveClients = i2;
            if (i2 == 0) {
                cancelLoads();
                trimForInactiveCache();
                return;
            }
            return;
        }
        int i3 = i + 1;
        this.uiActiveClients = i3;
        if (i3 > 1) {
            return;
        }
        if (this.closedRefreshScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.closedRefreshRunnable);
            this.closedRefreshScheduled = false;
        }
        if (this.loadingNewer) {
            cancelLoads();
        }
    }

    private boolean isUiActive() {
        return this.uiActiveClients > 0;
    }

    public void setUiResumed(boolean z) {
        int i = this.resumedUiClients;
        if (z) {
            this.resumedUiClients = i + 1;
        } else if (i > 0) {
            this.resumedUiClients = i - 1;
        }
    }

    public void clear() {
        this.sessionGeneration++;
        this.configGeneration = FeedConfig.getInstance(this.currentAccount).getGeneration();
        this.unreadTracker.clear();
        this.drawerScrollPosition = null;
        this.store.clear();
        this.loading = false;
        this.loadingNewer = false;
        this.olderPagingBoundsDirty = false;
        this.newerPagingBoundsDirty = false;
        this.attemptRounds = 0;
        this.staleEnumerationRetries = 0;
        this.initialLoadWaiters.clear();
        this.backfill.cancel();
        this.backfill.clearExhausted();
        if (this.closedRefreshScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.closedRefreshRunnable);
            this.closedRefreshScheduled = false;
        }
    }

    public void cancelLoads() {
        this.sessionGeneration++;
        this.loading = false;
        this.loadingNewer = false;
        this.olderPagingBoundsDirty = false;
        this.newerPagingBoundsDirty = false;
        this.attemptRounds = 0;
        this.staleEnumerationRetries = 0;
        this.initialLoadWaiters.clear();
        this.backfill.cancel();
    }

    private static int getInactiveCacheCap() {
        int devicePerformanceClass = SharedConfig.getDevicePerformanceClass();
        if (devicePerformanceClass == 0) {
            return 300;
        }
        if (devicePerformanceClass != 2) {
            return 600;
        }
        return 1000;
    }

    public void trimForInactiveCache() {
        if (isUiActive() || this.store.isEmpty()) {
            return;
        }
        this.store.trim(getInactiveCacheCap());
    }

    public boolean isIncludedChannelPost(long j) {
        if (!DialogObject.isChatDialog(j) || FeedConfig.getInstance(this.currentAccount).isExcluded(j)) {
            return false;
        }
        return isEligibleChannel(MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j)));
    }

    public static boolean isEligibleChannel(TLRPC.Chat chat) {
        return (chat == null || !ChatObject.isChannelAndNotMegaGroup(chat) || ChatObject.isCommunity(chat) || ChatObject.isNotInChat(chat)) ? false : true;
    }

    public boolean consumeInitialUnreadScroll() {
        boolean z = this.initialUnreadScrollPending;
        this.initialUnreadScrollPending = false;
        return z;
    }

    public int getUnreadCount() {
        if (ExteraConfig.getShowFeedUnreadCounter()) {
            return this.unreadTracker.getUnreadCount();
        }
        return 0;
    }

    public void onPostSeen(long j, int i) {
        this.unreadTracker.onPostSeen(j, i);
    }

    public void markAllRead() {
        this.unreadTracker.markAllRead();
    }

    public int findFirstUnreadIndex(ArrayList<MessageObject> arrayList) {
        return this.unreadTracker.findFirstUnreadIndex(arrayList);
    }

    public int countUnreadBelow(ArrayList<MessageObject> arrayList, int i) {
        return this.unreadTracker.countUnreadBelow(arrayList, i);
    }

    public void saveDrawerScrollPosition(long j, int i, int i2) {
        if (j == 0 || i <= 0) {
            return;
        }
        this.drawerScrollPosition = new SavedScrollPosition(j, i, i2);
    }

    public SavedScrollPosition getDrawerScrollPosition() {
        return this.drawerScrollPosition;
    }

    public boolean hasNoSyntheticIds() {
        return this.store.hasNoSyntheticIds();
    }

    public MessageObject getMessage(long j, int i) {
        return this.store.getMessage(j, i);
    }

    public int resolveRealMessageId(long j, int i) {
        return this.store.resolveRealMessageId(j, i);
    }

    public long resolveRealDialogId(int i) {
        return this.store.resolveRealDialogId(i);
    }

    public boolean loadInitial(final int i, final int i2) {
        ensureCurrentConfig();
        final FeedConfig feedConfig = FeedConfig.getInstance(this.currentAccount);
        final int generation = feedConfig.getGeneration();
        final int channelCacheEpoch = this.loader.getChannelCacheEpoch();
        if (this.store.isEmpty()) {
            if (!loadMore(i, i2)) {
                this.initialLoadWaiters.add(new int[]{i, i2});
            }
            return false;
        }
        final ArrayList<MessageObject> visibleMessages = this.store.getVisibleMessages();
        int size = visibleMessages.size();
        int i3 = 0;
        while (i3 < size) {
            MessageObject messageObject = visibleMessages.get(i3);
            i3++;
            messageObject.viewsReloaded = false;
        }
        if (visibleMessages.isEmpty() && !this.store.isEndReached()) {
            if (!loadMore(i, i2)) {
                this.initialLoadWaiters.add(new int[]{i, i2});
            }
            return false;
        }
        final int i4 = this.sessionGeneration;
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                FeedController.this.lambda$loadInitial$3(feedConfig, i4, generation, channelCacheEpoch, i, i2, visibleMessages);
            }
        });
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadInitial$3(final FeedConfig feedConfig, final int i, final int i2, final int i3, final int i4, final int i5, final ArrayList arrayList) {
        final FeedTimelineLoader.ChannelEnumeration channelEnumerationEnumerateChannels = this.loader.enumerateChannels(feedConfig, i, true);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                FeedController.this.lambda$loadInitial$2(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, i4, i5, arrayList);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadInitial$2(int i, FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i2, int i3, int i4, int i5, ArrayList arrayList) {
        if (i != this.sessionGeneration) {
            return;
        }
        if (!isEnumerationCurrent(channelEnumeration, feedConfig, i2, i3)) {
            postFeedResults(i4, i5, new ArrayList<>(), 0, false, true);
            postFeedCount(i4);
        } else {
            applyEnumeration(channelEnumeration);
            postFeedResults(i4, i5, arrayList, 0, false, channelEnumeration.failed);
            postFeedCount(i4);
        }
    }

    private void ensureCurrentConfig() {
        if (this.configGeneration != FeedConfig.getInstance(this.currentAccount).getGeneration()) {
            applyConfigChange(new Utilities.Callback() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda3
                @Override // org.telegram.messenger.Utilities.Callback
                public final void run(Object obj) {
                    FeedController.this.lambda$ensureCurrentConfig$4((Boolean) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$ensureCurrentConfig$4(Boolean bool) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationNameOnUIThread(NotificationCenter.feedNeedReload, bool);
    }

    public void markConfigApplied() {
        this.configGeneration = FeedConfig.getInstance(this.currentAccount).getGeneration();
    }

    public void applyConfigChange(Utilities.Callback<Boolean> callback) {
        reconcileChannelSet(callback);
    }

    private void reconcileChannelSet(final Utilities.Callback<Boolean> callback) {
        final int i = this.sessionGeneration;
        final FeedConfig feedConfig = FeedConfig.getInstance(this.currentAccount);
        final int generation = feedConfig.getGeneration();
        final int channelCacheEpoch = this.loader.getChannelCacheEpoch();
        if (this.store.isEmpty()) {
            loadChannels(new ChannelsCallback() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda12
                @Override // com.exteragram.messenger.feed.FeedController.ChannelsCallback
                public final void onChannels(ArrayList arrayList, int i2, boolean z, int i3) {
                    FeedController.this.lambda$reconcileChannelSet$5(callback, arrayList, i2, z, i3);
                }
            });
            return;
        }
        final HashSet<Long> loadedDialogIds = this.store.getLoadedDialogIds();
        final HashSet<Long> hiddenSnapshot = this.store.getHiddenSnapshot();
        final FeedTimelineLoader.Cursor cursor = new FeedTimelineLoader.Cursor();
        final FeedTimelineLoader.Cursor cursor2 = new FeedTimelineLoader.Cursor();
        cursor.set(this.store.getNewestCursor().date, this.store.getNewestCursor().uid, this.store.getNewestCursor().mid);
        cursor2.set(this.store.getOldestCursor().date, this.store.getOldestCursor().uid, this.store.getOldestCursor().mid);
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                FeedController.this.lambda$reconcileChannelSet$9(feedConfig, i, generation, channelCacheEpoch, callback, loadedDialogIds, hiddenSnapshot, cursor, cursor2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reconcileChannelSet$5(Utilities.Callback callback, ArrayList arrayList, int i, boolean z, int i2) {
        if (!z) {
            this.configGeneration = i2;
        }
        if (callback != null) {
            callback.run(Boolean.FALSE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reconcileChannelSet$9(final FeedConfig feedConfig, final int i, final int i2, final int i3, final Utilities.Callback callback, HashSet hashSet, HashSet hashSet2, FeedTimelineLoader.Cursor cursor, FeedTimelineLoader.Cursor cursor2) {
        final FeedTimelineLoader.ChannelEnumeration channelEnumerationEnumerateChannels = this.loader.enumerateChannels(feedConfig, i, true);
        if (channelEnumerationEnumerateChannels.failed) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda21
                @Override // java.lang.Runnable
                public final void run() {
                    FeedController.this.lambda$reconcileChannelSet$6(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, callback);
                }
            });
            return;
        }
        ArrayList<Long> arrayList = new ArrayList<>();
        ArrayList<FeedTimelineLoader.ChannelSnapshot> arrayList2 = channelEnumerationEnumerateChannels.included;
        int size = arrayList2.size();
        int i4 = 0;
        while (i4 < size) {
            FeedTimelineLoader.ChannelSnapshot channelSnapshot = arrayList2.get(i4);
            i4++;
            FeedTimelineLoader.ChannelSnapshot channelSnapshot2 = channelSnapshot;
            if (!hashSet.contains(Long.valueOf(channelSnapshot2.dialogId)) || hashSet2.contains(Long.valueOf(channelSnapshot2.dialogId))) {
                arrayList.add(Long.valueOf(channelSnapshot2.dialogId));
            }
        }
        final FeedTimelineLoader.WindowPage windowPageLoadChannelWindow = arrayList.isEmpty() ? null : this.loader.loadChannelWindow(arrayList, cursor, cursor2);
        if (windowPageLoadChannelWindow != null && windowPageLoadChannelWindow.failed) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda22
                @Override // java.lang.Runnable
                public final void run() {
                    FeedController.this.lambda$reconcileChannelSet$7(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, callback);
                }
            });
            return;
        }
        final ArrayList<MessageObject> arrayListCreateMessageObjects = windowPageLoadChannelWindow != null ? createMessageObjects(windowPageLoadChannelWindow.messages, windowPageLoadChannelWindow.users, windowPageLoadChannelWindow.chats) : null;
        final boolean z = !arrayList.isEmpty();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                FeedController.this.lambda$reconcileChannelSet$8(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, callback, windowPageLoadChannelWindow, arrayListCreateMessageObjects, z);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reconcileChannelSet$6(int i, FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i2, int i3, Utilities.Callback callback) {
        if (i != this.sessionGeneration) {
            return;
        }
        if (!isEnumerationCurrent(channelEnumeration, feedConfig, i2, i3) && canRetryStaleEnumeration()) {
            reconcileChannelSet(callback);
        } else if (callback != null) {
            callback.run(Boolean.FALSE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reconcileChannelSet$7(int i, FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i2, int i3, Utilities.Callback callback) {
        if (i != this.sessionGeneration) {
            return;
        }
        if (!isEnumerationCurrent(channelEnumeration, feedConfig, i2, i3) && canRetryStaleEnumeration()) {
            reconcileChannelSet(callback);
        } else if (callback != null) {
            callback.run(Boolean.FALSE);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$reconcileChannelSet$8(int i, FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i2, int i3, Utilities.Callback callback, FeedTimelineLoader.WindowPage windowPage, ArrayList arrayList, boolean z) {
        if (i != this.sessionGeneration) {
            return;
        }
        if (!isEnumerationCurrent(channelEnumeration, feedConfig, i2, i3) && canRetryStaleEnumeration()) {
            reconcileChannelSet(callback);
            return;
        }
        applyEnumeration(channelEnumeration);
        this.configGeneration = channelEnumeration.configGeneration;
        HashSet<Long> hashSet = new HashSet<>();
        ArrayList<FeedTimelineLoader.ChannelSnapshot> arrayList2 = channelEnumeration.included;
        int size = arrayList2.size();
        int i4 = 0;
        while (i4 < size) {
            FeedTimelineLoader.ChannelSnapshot channelSnapshot = arrayList2.get(i4);
            i4++;
            hashSet.add(Long.valueOf(channelSnapshot.dialogId));
        }
        this.store.applyIncludedDialogs(hashSet);
        boolean z2 = windowPage != null && windowPage.truncated;
        if (windowPage != null && !z2 && !arrayList.isEmpty()) {
            MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            messagesController.putUsers(windowPage.users, true);
            messagesController.putChats(windowPage.chats, true);
            this.store.mergeRows(arrayList);
        }
        if (z) {
            this.store.setEndReached(false);
            if (this.loading) {
                this.olderPagingBoundsDirty = true;
            }
            if (this.loadingNewer) {
                this.newerPagingBoundsDirty = true;
            }
        }
        if (callback != null) {
            callback.run(Boolean.valueOf(z2));
        }
    }

    public void refreshReadState(final Runnable runnable) {
        final int i = this.sessionGeneration;
        final FeedConfig feedConfig = FeedConfig.getInstance(this.currentAccount);
        final int generation = feedConfig.getGeneration();
        final int channelCacheEpoch = this.loader.getChannelCacheEpoch();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                FeedController.this.lambda$refreshReadState$11(feedConfig, i, generation, channelCacheEpoch, runnable);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshReadState$11(final FeedConfig feedConfig, final int i, final int i2, final int i3, final Runnable runnable) {
        final FeedTimelineLoader.ChannelEnumeration channelEnumerationEnumerateChannels = this.loader.enumerateChannels(feedConfig, i, true);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda20
            @Override // java.lang.Runnable
            public final void run() {
                FeedController.this.lambda$refreshReadState$10(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, runnable);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$refreshReadState$10(int i, FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i2, int i3, Runnable runnable) {
        if (i != this.sessionGeneration) {
            return;
        }
        if (isEnumerationCurrent(channelEnumeration, feedConfig, i2, i3)) {
            applyEnumeration(channelEnumeration);
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public boolean loadMore(int i, int i2) {
        ensureCurrentConfig();
        if (this.loading || (this.store.isEndReached() && !this.store.getOldestCursor().isEmpty())) {
            return false;
        }
        this.loading = true;
        this.heldGuid = i;
        this.heldLoadIndex = i2;
        this.attemptRounds = 0;
        runAttempt();
        return true;
    }

    private void runAttempt() {
        final int i = this.heldGuid;
        final int i2 = this.heldLoadIndex;
        final int i3 = this.sessionGeneration;
        final FeedConfig feedConfig = FeedConfig.getInstance(this.currentAccount);
        final int generation = feedConfig.getGeneration();
        final int channelCacheEpoch = this.loader.getChannelCacheEpoch();
        final boolean zIsEmpty = this.store.getOldestCursor().isEmpty();
        final FeedTimelineLoader.Cursor cursor = new FeedTimelineLoader.Cursor();
        cursor.set(this.store.getOldestCursor().date, this.store.getOldestCursor().uid, this.store.getOldestCursor().mid);
        final HashSet<Long> exhaustedSnapshot = this.backfill.getExhaustedSnapshot();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                FeedController.this.lambda$runAttempt$15(feedConfig, i3, generation, channelCacheEpoch, i, i2, cursor, exhaustedSnapshot, zIsEmpty);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runAttempt$15(final FeedConfig feedConfig, final int i, final int i2, final int i3, final int i4, final int i5, FeedTimelineLoader.Cursor cursor, HashSet hashSet, final boolean z) {
        final FeedTimelineLoader.ChannelEnumeration channelEnumerationEnumerateChannels = this.loader.enumerateChannels(feedConfig, i, false);
        ArrayList<FeedTimelineLoader.ChannelSnapshot> arrayList = channelEnumerationEnumerateChannels.included;
        if (channelEnumerationEnumerateChannels.failed) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    FeedController.this.lambda$runAttempt$12(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, i4, i5);
                }
            });
        } else {
            if (arrayList.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda16
                    @Override // java.lang.Runnable
                    public final void run() {
                        FeedController.this.lambda$runAttempt$13(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, i4, i5);
                    }
                });
                return;
            }
            final FeedTimelineLoader.OlderPage olderPageLoadOlderPage = this.loader.loadOlderPage(arrayList, cursor, hashSet);
            final ArrayList<MessageObject> arrayListCreateMessageObjects = createMessageObjects(olderPageLoadOlderPage.messages, olderPageLoadOlderPage.users, olderPageLoadOlderPage.chats);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    FeedController.this.lambda$runAttempt$14(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, olderPageLoadOlderPage, i4, i5, z, arrayListCreateMessageObjects);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runAttempt$12(int i, FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i2, int i3, int i4, int i5) {
        if (i != this.sessionGeneration) {
            return;
        }
        if (!isEnumerationCurrent(channelEnumeration, feedConfig, i2, i3) && canRetryStaleEnumeration()) {
            this.attemptRounds = 0;
            runAttempt();
        } else {
            this.loading = false;
            postFeedResults(i4, i5, new ArrayList<>(), 2, false, true);
            postFeedCount(i4);
            flushInitialLoadWaiters(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runAttempt$13(int i, FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i2, int i3, int i4, int i5) {
        if (i != this.sessionGeneration) {
            return;
        }
        if (!isEnumerationCurrent(channelEnumeration, feedConfig, i2, i3)) {
            if (canRetryStaleEnumeration()) {
                this.olderPagingBoundsDirty = false;
                this.attemptRounds = 0;
                runAttempt();
                return;
            } else {
                this.loading = false;
                postFeedResults(i4, i5, new ArrayList<>(), 2, false, true);
                postFeedCount(i4);
                flushInitialLoadWaiters(true);
                return;
            }
        }
        applyEnumeration(channelEnumeration);
        this.olderPagingBoundsDirty = false;
        this.unreadTracker.clear();
        this.loading = false;
        this.store.setEndReached(true);
        postFeedResults(i4, i5, new ArrayList<>(), 2);
        postFeedCount(i4);
        flushInitialLoadWaiters();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runAttempt$14(int i, FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i2, int i3, FeedTimelineLoader.OlderPage olderPage, int i4, int i5, boolean z, ArrayList arrayList) {
        int i6;
        if (i != this.sessionGeneration) {
            return;
        }
        if (!isEnumerationCurrent(channelEnumeration, feedConfig, i2, i3) && canRetryStaleEnumeration()) {
            this.olderPagingBoundsDirty = false;
            this.attemptRounds = 0;
            runAttempt();
            return;
        }
        if (this.olderPagingBoundsDirty) {
            this.olderPagingBoundsDirty = false;
            this.attemptRounds = 0;
            runAttempt();
            return;
        }
        applyEnumeration(channelEnumeration);
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        pruneStaleExclusions(FeedConfig.getInstance(this.currentAccount), messagesController);
        if (olderPage.failed) {
            this.loading = false;
            postFeedResults(i4, i5, new ArrayList<>(), 2, false, true);
            postFeedCount(i4);
            flushInitialLoadWaiters(true);
            return;
        }
        FeedTimelineLoader.Cursor oldestCursor = this.store.getOldestCursor();
        FeedTimelineLoader.Cursor cursor = olderPage.last;
        oldestCursor.set(cursor.date, cursor.uid, cursor.mid);
        if (z && !olderPage.first.isEmpty()) {
            FeedTimelineLoader.Cursor newestCursor = this.store.getNewestCursor();
            FeedTimelineLoader.Cursor cursor2 = olderPage.first;
            newestCursor.set(cursor2.date, cursor2.uid, cursor2.mid);
        }
        messagesController.putUsers(olderPage.users, true);
        messagesController.putChats(olderPage.chats, true);
        ArrayList<MessageObject> arrayListAppendMessages = this.store.appendMessages(arrayList, false);
        if (arrayListAppendMessages.isEmpty() && olderPage.lastChunkRowCount == 30) {
            runAttempt();
            return;
        }
        boolean z2 = !olderPage.hasIncomplete && olderPage.lastChunkRowCount < 30;
        if (!arrayListAppendMessages.isEmpty() || z2 || olderPage.backfillCandidates.isEmpty() || (i6 = this.attemptRounds) >= 3) {
            this.loading = false;
            this.store.setEndReached(z2);
            postFeedResults(i4, i5, arrayListAppendMessages, 2);
            postFeedCount(i4);
            flushInitialLoadWaiters();
            return;
        }
        this.attemptRounds = i6 + 1;
        this.backfill.startRound(olderPage.backfillCandidates);
    }

    private void postFeedResults(int i, int i2, ArrayList<MessageObject> arrayList, int i3) {
        postFeedResults(i, i2, arrayList, i3, false, false);
    }

    private void postFeedResults(int i, int i2, ArrayList<MessageObject> arrayList, int i3, boolean z, boolean z2) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationNameOnUIThread(NotificationCenter.messagesDidLoad, 0L, Integer.valueOf(arrayList.size()), arrayList, Boolean.FALSE, 0, 0, 0, 0, Integer.valueOf(i3), Boolean.TRUE, Integer.valueOf(i), Integer.valueOf(i2), 0, 0, 7, Boolean.valueOf(z), Boolean.valueOf(z2));
    }

    private void postFeedCount(int i) {
        NotificationCenter.getInstance(this.currentAccount).postNotificationNameOnUIThread(NotificationCenter.hashtagSearchUpdated, Integer.valueOf(i), Integer.valueOf(this.store.getCount()), Boolean.valueOf(this.store.isEndReached()), 0, 0, 0);
    }

    private boolean isEnumerationCurrent(FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i, int i2) {
        boolean z = this.loader.isEnumerationCurrent(channelEnumeration) && channelEnumeration.configGeneration == feedConfig.getGeneration() && channelEnumeration.configGeneration == i && channelEnumeration.cacheEpoch == i2;
        if (z) {
            this.staleEnumerationRetries = 0;
        }
        return z;
    }

    private boolean canRetryStaleEnumeration() {
        int i = this.staleEnumerationRetries;
        if (i >= 3) {
            this.staleEnumerationRetries = 0;
            return false;
        }
        this.staleEnumerationRetries = i + 1;
        return true;
    }

    private void applyEnumeration(FeedTimelineLoader.ChannelEnumeration channelEnumeration) {
        if (channelEnumeration.failed) {
            return;
        }
        this.hasChannels = channelEnumeration.hasChannels;
        this.hasIncludedChannels = !channelEnumeration.included.isEmpty();
        this.cachedIncludedChannelCount = channelEnumeration.included.size();
        ArrayList<FeedTimelineLoader.ChannelSnapshot> arrayList = channelEnumeration.included;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            FeedTimelineLoader.ChannelSnapshot channelSnapshot = arrayList.get(i);
            i++;
            FeedTimelineLoader.ChannelSnapshot channelSnapshot2 = channelSnapshot;
            int i2 = channelSnapshot2.readInboxMax;
            if (i2 <= 0 && channelSnapshot2.unreadCount <= 0) {
                i2 = channelSnapshot2.topMessage;
            }
            this.unreadTracker.applyReadInboxMax(channelSnapshot2.dialogId, i2);
        }
    }

    private void flushInitialLoadWaiters() {
        flushInitialLoadWaiters(false);
    }

    private void flushInitialLoadWaiters(boolean z) {
        if (this.initialLoadWaiters.isEmpty()) {
            return;
        }
        ArrayList arrayList = new ArrayList(this.initialLoadWaiters);
        this.initialLoadWaiters.clear();
        ArrayList<MessageObject> visibleMessages = this.store.getVisibleMessages();
        for (int i = 0; i < arrayList.size(); i++) {
            int[] iArr = (int[]) arrayList.get(i);
            postFeedResults(iArr[0], iArr[1], visibleMessages, 0, false, z);
            postFeedCount(iArr[0]);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onBackfillRoundFinished() {
        if (this.loading) {
            runAttempt();
        }
    }

    public boolean loadNewer(int i, int i2) {
        ensureCurrentConfig();
        if (this.loadingNewer || this.store.getNewestCursor().isEmpty()) {
            return false;
        }
        this.loadingNewer = true;
        runLoadNewer(i, i2);
        return true;
    }

    private void runLoadNewer(final int i, final int i2) {
        final int i3 = this.sessionGeneration;
        final FeedConfig feedConfig = FeedConfig.getInstance(this.currentAccount);
        final int generation = feedConfig.getGeneration();
        final int channelCacheEpoch = this.loader.getChannelCacheEpoch();
        final FeedTimelineLoader.Cursor cursor = new FeedTimelineLoader.Cursor();
        cursor.set(this.store.getNewestCursor().date, this.store.getNewestCursor().uid, this.store.getNewestCursor().mid);
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                FeedController.this.lambda$runLoadNewer$19(feedConfig, i3, generation, channelCacheEpoch, i, i2, cursor);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runLoadNewer$19(final FeedConfig feedConfig, final int i, final int i2, final int i3, final int i4, final int i5, FeedTimelineLoader.Cursor cursor) {
        final FeedTimelineLoader.ChannelEnumeration channelEnumerationEnumerateChannels = this.loader.enumerateChannels(feedConfig, i, false);
        ArrayList<FeedTimelineLoader.ChannelSnapshot> arrayList = channelEnumerationEnumerateChannels.included;
        if (channelEnumerationEnumerateChannels.failed) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    FeedController.this.lambda$runLoadNewer$16(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, i4, i5);
                }
            });
        } else {
            if (arrayList.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda10
                    @Override // java.lang.Runnable
                    public final void run() {
                        FeedController.this.lambda$runLoadNewer$17(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, i4, i5);
                    }
                });
                return;
            }
            final FeedTimelineLoader.NewerPage newerPageLoadNewerPage = this.loader.loadNewerPage(arrayList, cursor);
            final ArrayList<MessageObject> arrayListCreateMessageObjects = createMessageObjects(newerPageLoadNewerPage.messages, newerPageLoadNewerPage.users, newerPageLoadNewerPage.chats);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    FeedController.this.lambda$runLoadNewer$18(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, i4, i5, newerPageLoadNewerPage, arrayListCreateMessageObjects);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runLoadNewer$16(int i, FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i2, int i3, int i4, int i5) {
        if (i != this.sessionGeneration) {
            return;
        }
        if (!isEnumerationCurrent(channelEnumeration, feedConfig, i2, i3) && canRetryStaleEnumeration()) {
            runLoadNewer(i4, i5);
        } else {
            this.loadingNewer = false;
            postNewerMessagesLoaded(i4, i5, null, false, true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runLoadNewer$17(int i, FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i2, int i3, int i4, int i5) {
        if (i != this.sessionGeneration) {
            return;
        }
        if (!isEnumerationCurrent(channelEnumeration, feedConfig, i2, i3) && canRetryStaleEnumeration()) {
            this.newerPagingBoundsDirty = false;
            runLoadNewer(i4, i5);
        } else {
            this.newerPagingBoundsDirty = false;
            this.loadingNewer = false;
            postNewerMessagesLoaded(i4, i5, null, false);
            postFeedCount(i4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$runLoadNewer$18(int i, FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i2, int i3, int i4, int i5, FeedTimelineLoader.NewerPage newerPage, ArrayList arrayList) {
        if (i != this.sessionGeneration) {
            return;
        }
        if (!isEnumerationCurrent(channelEnumeration, feedConfig, i2, i3) && canRetryStaleEnumeration()) {
            this.newerPagingBoundsDirty = false;
            runLoadNewer(i4, i5);
            return;
        }
        if (this.newerPagingBoundsDirty) {
            this.newerPagingBoundsDirty = false;
            if (this.store.getNewestCursor().isEmpty()) {
                this.loadingNewer = false;
                postNewerMessagesLoaded(i4, i5, null, false);
                postFeedCount(i4);
                return;
            }
            runLoadNewer(i4, i5);
            return;
        }
        this.loadingNewer = false;
        applyEnumeration(channelEnumeration);
        if (newerPage.failed) {
            postNewerMessagesLoaded(i4, i5, null, false, true);
            return;
        }
        FeedTimelineLoader.Cursor newestCursor = this.store.getNewestCursor();
        FeedTimelineLoader.Cursor cursor = newerPage.first;
        newestCursor.set(cursor.date, cursor.uid, cursor.mid);
        if (newerPage.messages.isEmpty()) {
            postNewerMessagesLoaded(i4, i5, null, newerPage.hasMore);
            if (newerPage.hasMore) {
                return;
            }
            postFeedCount(i4);
            return;
        }
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        messagesController.putUsers(newerPage.users, true);
        messagesController.putChats(newerPage.chats, true);
        postNewerMessagesLoaded(i4, i5, this.store.appendMessages(arrayList, true), newerPage.hasMore);
        if (!newerPage.hasMore) {
            postFeedCount(i4);
        }
        trimForInactiveCache();
    }

    private void postNewerMessagesLoaded(int i, int i2, ArrayList<MessageObject> arrayList, boolean z) {
        postNewerMessagesLoaded(i, i2, arrayList, z, false);
    }

    private void postNewerMessagesLoaded(int i, int i2, ArrayList<MessageObject> arrayList, boolean z, boolean z2) {
        int i3;
        ArrayList<MessageObject> arrayList2 = new ArrayList<>();
        if (arrayList == null || arrayList.isEmpty()) {
            i3 = 0;
        } else {
            arrayList2.addAll(arrayList);
            Collections.reverse(arrayList2);
            i3 = 1;
        }
        postFeedResults(i, i2, arrayList2, i3, z, z2);
    }

    private ArrayList<MessageObject> createMessageObjects(ArrayList<TLRPC.Message> arrayList, ArrayList<TLRPC.User> arrayList2, ArrayList<TLRPC.Chat> arrayList3) {
        HashMap map = new HashMap();
        HashMap map2 = new HashMap();
        int size = arrayList2.size();
        int i = 0;
        int i2 = 0;
        while (i2 < size) {
            TLRPC.User user = arrayList2.get(i2);
            i2++;
            TLRPC.User user2 = user;
            map.put(Long.valueOf(user2.id), user2);
        }
        int size2 = arrayList3.size();
        int i3 = 0;
        while (i3 < size2) {
            TLRPC.Chat chat = arrayList3.get(i3);
            i3++;
            TLRPC.Chat chat2 = chat;
            map2.put(Long.valueOf(chat2.id), chat2);
        }
        ArrayList<MessageObject> arrayList4 = new ArrayList<>(arrayList.size());
        for (int size3 = arrayList.size(); i < size3; size3 = size3) {
            int i4 = i + 1;
            TLRPC.Message message = arrayList.get(i);
            ArrayList<MessageObject> arrayList5 = arrayList4;
            arrayList5.add(new MessageObject(this.currentAccount, message, null, map, map2, null, null, true, true, 0L, false, false, false, 4));
            arrayList4 = arrayList5;
            i = i4;
        }
        return arrayList4;
    }

    public void loadChannels(ChannelsCallback channelsCallback) {
        loadChannels(false, channelsCallback);
    }

    public void loadChannels(final boolean z, final ChannelsCallback channelsCallback) {
        final FeedConfig feedConfig = FeedConfig.getInstance(this.currentAccount);
        final int i = this.sessionGeneration;
        final int generation = feedConfig.getGeneration();
        final int channelCacheEpoch = this.loader.getChannelCacheEpoch();
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                FeedController.this.lambda$loadChannels$21(feedConfig, i, z, generation, channelCacheEpoch, channelsCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadChannels$21(final FeedConfig feedConfig, final int i, boolean z, final int i2, final int i3, final ChannelsCallback channelsCallback) {
        final FeedTimelineLoader.ChannelEnumeration channelEnumerationEnumerateChannels = this.loader.enumerateChannels(feedConfig, i, z);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.feed.FeedController$$ExternalSyntheticLambda14
            @Override // java.lang.Runnable
            public final void run() {
                FeedController.this.lambda$loadChannels$20(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, channelsCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadChannels$20(int i, FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i2, int i3, ChannelsCallback channelsCallback) {
        if (i != this.sessionGeneration) {
            return;
        }
        if (!isEnumerationCurrent(channelEnumeration, feedConfig, i2, i3)) {
            if (channelsCallback != null) {
                channelsCallback.onChannels(new ArrayList<>(), 0, true, channelEnumeration.configGeneration);
            }
        } else {
            applyEnumeration(channelEnumeration);
            if (!channelEnumeration.failed) {
                MessagesController.getInstance(this.currentAccount).putChats(channelEnumeration.channels, true);
            }
            if (channelsCallback != null) {
                channelsCallback.onChannels(channelEnumeration.channels, channelEnumeration.included.size(), channelEnumeration.failed, channelEnumeration.configGeneration);
            }
        }
    }

    private void pruneStaleExclusions(FeedConfig feedConfig, MessagesController messagesController) {
        HashSet hashSet = null;
        for (Long l : feedConfig.getExcludedSnapshot()) {
            TLRPC.Chat chat = messagesController.getChat(Long.valueOf(-l.longValue()));
            if (chat != null && !isEligibleChannel(chat)) {
                if (hashSet == null) {
                    hashSet = new HashSet();
                }
                hashSet.add(l);
            }
        }
        if (hashSet != null) {
            feedConfig.removeExcluded(hashSet);
            markConfigApplied();
        }
    }

    public void replaceMessage(MessageObject messageObject, MessageObject messageObject2) {
        this.store.replaceMessage(messageObject, messageObject2);
    }

    public ArrayList<Integer> deleteMessages(long j, ArrayList<Integer> arrayList) {
        boolean[] zArr = new boolean[1];
        ArrayList<Integer> arrayListDeleteMessages = this.store.deleteMessages(j, arrayList, zArr);
        if (zArr[0]) {
            onFeedRowsRemoved();
        }
        return arrayListDeleteMessages;
    }

    public ArrayList<Integer> deleteHistory(long j, int i) {
        boolean[] zArr = new boolean[1];
        ArrayList<Integer> arrayListDeleteHistory = this.store.deleteHistory(j, i, zArr);
        if (zArr[0]) {
            onFeedRowsRemoved();
        }
        return arrayListDeleteHistory;
    }

    private void onFeedRowsRemoved() {
        if (this.loading) {
            this.olderPagingBoundsDirty = true;
        }
        if (this.loadingNewer) {
            this.newerPagingBoundsDirty = true;
        }
    }

    public ArrayList<MessageObject> updateViews(LongSparseArray<SparseIntArray> longSparseArray, LongSparseArray<SparseIntArray> longSparseArray2, LongSparseArray<SparseArray<TLRPC.MessageReplies>> longSparseArray3, boolean z) {
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        updateCounters(longSparseArray, true, arrayList);
        updateCounters(longSparseArray2, false, arrayList);
        updateReplies(longSparseArray3, z, arrayList);
        return arrayList;
    }

    private void updateCounters(LongSparseArray<SparseIntArray> longSparseArray, boolean z, ArrayList<MessageObject> arrayList) {
        if (longSparseArray == null) {
            return;
        }
        for (int i = 0; i < longSparseArray.size(); i++) {
            long jKeyAt = longSparseArray.keyAt(i);
            SparseIntArray sparseIntArrayValueAt = longSparseArray.valueAt(i);
            for (int i2 = 0; i2 < sparseIntArrayValueAt.size(); i2++) {
                MessageObject message = getMessage(jKeyAt, sparseIntArrayValueAt.keyAt(i2));
                if (message != null) {
                    int iValueAt = sparseIntArrayValueAt.valueAt(i2);
                    TLRPC.Message message2 = message.messageOwner;
                    if (z) {
                        if (iValueAt > message2.views) {
                            message2.views = iValueAt;
                            addUpdated(arrayList, message);
                        }
                    } else if (iValueAt > message2.forwards) {
                        message2.forwards = iValueAt;
                        addUpdated(arrayList, message);
                    }
                }
            }
        }
    }

    private void updateReplies(LongSparseArray<SparseArray<TLRPC.MessageReplies>> longSparseArray, boolean z, ArrayList<MessageObject> arrayList) {
        TLRPC.Message message;
        if (longSparseArray == null) {
            return;
        }
        for (int i = 0; i < longSparseArray.size(); i++) {
            long jKeyAt = longSparseArray.keyAt(i);
            SparseArray<TLRPC.MessageReplies> sparseArrayValueAt = longSparseArray.valueAt(i);
            for (int i2 = 0; i2 < sparseArrayValueAt.size(); i2++) {
                MessageObject message2 = getMessage(jKeyAt, sparseArrayValueAt.keyAt(i2));
                TLRPC.MessageReplies messageRepliesValueAt = sparseArrayValueAt.valueAt(i2);
                if (message2 != null && messageRepliesValueAt != null) {
                    TLRPC.Message message3 = message2.messageOwner;
                    if (z) {
                        if (message3.replies == null) {
                            message3.replies = new TLRPC.TL_messageReplies();
                        }
                        message2.messageOwner.replies.replies += messageRepliesValueAt.replies;
                        int i3 = 0;
                        while (true) {
                            int size = messageRepliesValueAt.recent_repliers.size();
                            message = message2.messageOwner;
                            if (i3 >= size) {
                                break;
                            }
                            message.replies.recent_repliers.remove(messageRepliesValueAt.recent_repliers.get(i3));
                            i3++;
                        }
                        message.replies.recent_repliers.addAll(0, messageRepliesValueAt.recent_repliers);
                        while (message2.messageOwner.replies.recent_repliers.size() > 3) {
                            message2.messageOwner.replies.recent_repliers.remove(0);
                        }
                    } else {
                        TLRPC.MessageReplies messageReplies = message3.replies;
                        if (messageReplies == null || messageRepliesValueAt.replies_pts > messageReplies.replies_pts || messageRepliesValueAt.read_max_id > messageReplies.read_max_id || messageRepliesValueAt.max_id > messageReplies.max_id) {
                            message3.replies = messageRepliesValueAt;
                        }
                    }
                    message2.animateComments = true;
                    addUpdated(arrayList, message2);
                }
            }
        }
    }

    private static void addUpdated(ArrayList<MessageObject> arrayList, MessageObject messageObject) {
        if (arrayList.contains(messageObject)) {
            return;
        }
        arrayList.add(messageObject);
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.messagesDidLoad) {
            this.backfill.onMessagesDidLoad(objArr);
            return;
        }
        if (i == NotificationCenter.loadingMessagesFailed) {
            this.backfill.onLoadingMessagesFailed(objArr);
            return;
        }
        if (i == NotificationCenter.messagesDeleted) {
            if (isUiActive() || ((Boolean) objArr[2]).booleanValue()) {
                return;
            }
            long jLongValue = ((Long) objArr[1]).longValue();
            if (jLongValue == 0) {
                return;
            }
            if (jLongValue > 0) {
                jLongValue = -jLongValue;
            }
            deleteMessages(jLongValue, (ArrayList) objArr[0]);
            return;
        }
        if (i == NotificationCenter.historyCleared) {
            if (isUiActive()) {
                return;
            }
            long jLongValue2 = ((Long) objArr[0]).longValue();
            if (DialogObject.isChatDialog(jLongValue2)) {
                deleteHistory(jLongValue2, ((Integer) objArr[1]).intValue());
                return;
            }
            return;
        }
        if (i != NotificationCenter.didReceiveNewMessages || isUiActive() || ((Boolean) objArr[2]).booleanValue() || this.store.isEmpty() || this.store.getNewestCursor().isEmpty() || !isIncludedChannelPost(((Long) objArr[0]).longValue())) {
            return;
        }
        scheduleClosedRefresh();
    }

    private void scheduleClosedRefresh() {
        if (this.closedRefreshScheduled) {
            return;
        }
        this.closedRefreshScheduled = true;
        AndroidUtilities.runOnUIThread(this.closedRefreshRunnable, 1000L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void runClosedRefresh() {
        this.closedRefreshScheduled = false;
        if (isUiActive() || this.loadingNewer || this.store.isEmpty() || this.store.getNewestCursor().isEmpty()) {
            return;
        }
        loadNewer(this.closedRefreshGuid, 0);
    }
}
