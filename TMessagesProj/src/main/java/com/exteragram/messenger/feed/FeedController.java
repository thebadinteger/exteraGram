package com.exteragram.messenger.feed;

import android.util.SparseArray;
import android.util.SparseIntArray;
import androidx.collection.LongSparseArray;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.gms.cast.MediaError;
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
        this.closedRefreshRunnable = new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.runClosedRefresh();
            }
        };
        this.currentAccount = i;
        this.unreadTracker = new FeedUnreadTracker(i, feedStore.getMessages());
        this.loader = new FeedTimelineLoader(i);
        this.backfill = new FeedBackfillCoordinator(i, new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.onBackfillRoundFinished();
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$0(i);
            }
        });
    }

    public void lambda$onFeedChannelsChanged$1(Boolean bool) {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.feedNeedReload, bool);
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
            return MediaError.DetailedErrorCode.TEXT_UNKNOWN;
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
        MessagesStorage.getInstance(this.currentAccount).getStorageQueue().postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadInitial$3(feedConfig, i4, generation, channelCacheEpoch, i, i2, visibleMessages);
            }
        });
        return true;
    }

    public void lambda$loadInitial$2(int i, FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i2, int i3, int i4, int i5, ArrayList arrayList) {
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
            applyConfigChange(new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.lambda$ensureCurrentConfig$4((Boolean) obj);
                }
            });
        }
    }

    public void lambda$reconcileChannelSet$5(Utilities.Callback callback, ArrayList arrayList, int i, boolean z, int i2) {
        if (!z) {
            this.configGeneration = i2;
        }
        if (callback != null) {
            callback.run(Boolean.FALSE);
        }
    }

    public void lambda$reconcileChannelSet$6(int i, FeedTimelineLoader.ChannelEnumeration channelEnumeration, FeedConfig feedConfig, int i2, int i3, Utilities.Callback callback) {
        if (i != this.sessionGeneration) {
            return;
        }
        if (!isEnumerationCurrent(channelEnumeration, feedConfig, i2, i3) && canRetryStaleEnumeration()) {
            reconcileChannelSet(callback);
        } else if (callback != null) {
            callback.run(Boolean.FALSE);
        }
    }

    public void lambda$refreshReadState$11(final FeedConfig feedConfig, final int i, final int i2, final int i3, final Runnable runnable) {
        final FeedTimelineLoader.ChannelEnumeration channelEnumerationEnumerateChannels = this.loader.enumerateChannels(feedConfig, i, true);
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$refreshReadState$10(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, runnable);
            }
        });
    }

    public void lambda$runAttempt$15(final FeedConfig feedConfig, final int i, final int i2, final int i3, final int i4, final int i5, FeedTimelineLoader.Cursor cursor, HashSet hashSet, final boolean z) {
        final FeedTimelineLoader.ChannelEnumeration channelEnumerationEnumerateChannels = this.loader.enumerateChannels(feedConfig, i, false);
        ArrayList<FeedTimelineLoader.ChannelSnapshot> arrayList = channelEnumerationEnumerateChannels.included;
        if (channelEnumerationEnumerateChannels.failed) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$runAttempt$12(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, i4, i5);
                }
            });
        } else {
            if (arrayList.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$runAttempt$13(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, i4, i5);
                    }
                });
                return;
            }
            final FeedTimelineLoader.OlderPage olderPageLoadOlderPage = this.loader.loadOlderPage(arrayList, cursor, hashSet);
            final ArrayList<MessageObject> arrayListCreateMessageObjects = createMessageObjects(olderPageLoadOlderPage.messages, olderPageLoadOlderPage.users, olderPageLoadOlderPage.chats);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$runAttempt$14(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, olderPageLoadOlderPage, i4, i5, z, arrayListCreateMessageObjects);
                }
            });
        }
    }

    public void lambda$runLoadNewer$19(final FeedConfig feedConfig, final int i, final int i2, final int i3, final int i4, final int i5, FeedTimelineLoader.Cursor cursor) {
        final FeedTimelineLoader.ChannelEnumeration channelEnumerationEnumerateChannels = this.loader.enumerateChannels(feedConfig, i, false);
        ArrayList<FeedTimelineLoader.ChannelSnapshot> arrayList = channelEnumerationEnumerateChannels.included;
        if (channelEnumerationEnumerateChannels.failed) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$runLoadNewer$16(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, i4, i5);
                }
            });
        } else {
            if (arrayList.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$runLoadNewer$17(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, i4, i5);
                    }
                });
                return;
            }
            final FeedTimelineLoader.NewerPage newerPageLoadNewerPage = this.loader.loadNewerPage(arrayList, cursor);
            final ArrayList<MessageObject> arrayListCreateMessageObjects = createMessageObjects(newerPageLoadNewerPage.messages, newerPageLoadNewerPage.users, newerPageLoadNewerPage.chats);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$runLoadNewer$18(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, i4, i5, newerPageLoadNewerPage, arrayListCreateMessageObjects);
                }
            });
        }
    }

    public void lambda$loadChannels$21(final FeedConfig feedConfig, final int i, boolean z, final int i2, final int i3, final ChannelsCallback channelsCallback) {
        final FeedTimelineLoader.ChannelEnumeration channelEnumerationEnumerateChannels = this.loader.enumerateChannels(feedConfig, i, z);
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadChannels$20(i, channelEnumerationEnumerateChannels, feedConfig, i2, i3, channelsCallback);
            }
        });
    }

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

    @Override 
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

    public void runClosedRefresh() {
        this.closedRefreshScheduled = false;
        if (isUiActive() || this.loadingNewer || this.store.isEmpty() || this.store.getNewestCursor().isEmpty()) {
            return;
        }
        loadNewer(this.closedRefreshGuid, 0);
    }
}
