package com.exteragram.messenger.feed;

import androidx.collection.LongSparseArray;
import com.exteragram.messenger.feed.ads.FeedAdController;
import com.exteragram.messenger.feed.ads.FeedAdInjector;
import java.util.ArrayList;
import java.util.HashSet;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_update;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Components.BulletinFactory;

public class FeedChatIntegration {
    private final FeedAdInjector adInjector;
    private Runnable channelsChangedCallback;
    private final int currentAccount;
    private boolean destroyed;
    private final Host host;
    private boolean initialScrollApplied;
    private boolean pagedownShownByScroll;
    private boolean pendingDividerScroll;
    private long pendingHideDialogId;
    private ScrollAnchor pendingInitialScrollRestore;
    private boolean reactionsRefreshScheduled;
    private boolean readyToMarkAsRead;
    private final boolean restoreDrawerScrollPosition;
    private boolean scrollPreservedNewerToUnread;
    private boolean settleAtNewestScheduled;
    private int totalScrollDy;
    private MessageObject unreadDivider;
    private boolean viewportActive;
    private static final int PAGEDOWN_SCROLL_THRESHOLD = AndroidUtilities.dp(100.0f);
    private static final int NEAR_NEWEST_THRESHOLD = AndroidUtilities.dp(160.0f);
    private int preserveScrollLoadIndex = -1;
    private int lastPagedownCount = -1;
    private final Runnable settleAtNewestRunnable = new Runnable() { 
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.settleAtNewestNow();
        }
    };
    private final int reactionsRequestGuid = ConnectionsManager.generateClassGuid();
    private final LongSparseArray<Long> reactionsLastCheckTimes = new LongSparseArray<>();
    private final LongSparseArray<ArrayList<Integer>> pendingReactionIds = new LongSparseArray<>();
    private final Runnable reactionsRefreshRunnable = new Runnable() { 
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.flushReactionsRefresh();
        }
    };

    public interface Host {
        boolean canScrollToNewer();

        ScrollAnchor captureScrollAnchor();

        void deleteRows(ArrayList<Integer> arrayList);

        int getDistanceToNewerPx();

        BaseFragment getFragment();

        int getLastVisibleMessageIndex();

        ArrayList<MessageObject> getMessages();

        int getNewestVisibleMessageIndex();

        void invalidateVisiblePart();

        boolean isFirstLoadComplete();

        boolean isListReady();

        boolean isListScrollIdle();

        boolean isPagedownButtonVisible();

        boolean isScrollAnimationRunning();

        void materializeRow(MessageObject messageObject);

        int nextStableId();

        void notifyAllMessagesChanged();

        void notifyMessageInserted(int i);

        void notifyMessageRemoved(int i);

        void onFeedListChanged();

        void reloadFeed();

        void requestOlderFeedPage();

        void restoreScrollAnchor(ScrollAnchor scrollAnchor);

        void scrollToMessage(int i, int i2);

        void scrollToMessageAnimated(int i, int i2);

        void setPagedownButtonVisible(boolean z);

        void setPagedownCount(int i);

        void showEmptyFeedProgress();

        void showEmptyFeedState();

        int stableIdForDateHeader(int i);
    }

    public static final class ScrollAnchor {
        public final int offsetTop;
        public final MessageObject row;

        public ScrollAnchor(MessageObject messageObject, int i) {
            this.row = messageObject;
            this.offsetTop = i;
        }
    }

    public void settleAtNewestNow() {
        this.settleAtNewestScheduled = false;
        if (this.destroyed || !this.viewportActive || !this.host.isListReady() || this.host.isScrollAnimationRunning() || this.host.canScrollToNewer()) {
            return;
        }
        settleUnreadDivider();
    }

    public FeedChatIntegration(int i, Host host, boolean z) {
        this.currentAccount = i;
        this.host = host;
        this.restoreDrawerScrollPosition = z;
        this.adInjector = new FeedAdInjector(i, host);
    }

    public void refreshAds() {
        this.adInjector.refresh(this.unreadDivider);
        requestPendingInitialPosition();
    }

    public void setChannelsChangedCallback(Runnable runnable) {
        this.channelsChangedCallback = runnable;
    }

    public void notifyChannelsChanged() {
        Runnable runnable = this.channelsChangedCallback;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void resetUiState() {
        resetMetadataRefresh();
        this.initialScrollApplied = false;
        this.readyToMarkAsRead = false;
        this.pendingDividerScroll = false;
        this.pendingInitialScrollRestore = null;
        this.scrollPreservedNewerToUnread = false;
        this.preserveScrollLoadIndex = -1;
        this.unreadDivider = null;
        this.lastPagedownCount = -1;
        this.pagedownShownByScroll = false;
        this.totalScrollDy = 0;
        this.pendingHideDialogId = 0L;
        if (this.settleAtNewestScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.settleAtNewestRunnable);
            this.settleAtNewestScheduled = false;
        }
        this.adInjector.clear();
    }

    private boolean hasMaterializedPostRows() {
        ArrayList<MessageObject> messages = this.host.getMessages();
        for (int i = 0; i < messages.size(); i++) {
            if (FeedMessageUtils.isPostRow(messages.get(i))) {
                return true;
            }
        }
        return false;
    }

    public void onMessagesLoaded() {
        if (!this.host.getFragment().isPaused() && this.host.isListReady() && hasMaterializedPostRows()) {
            if (!this.initialScrollApplied) {
                this.initialScrollApplied = true;
                FeedController feedController = FeedController.getInstance(this.currentAccount);
                boolean zConsumeInitialUnreadScroll = feedController.consumeInitialUnreadScroll();
                FeedController.SavedScrollPosition drawerScrollPosition = this.restoreDrawerScrollPosition ? feedController.getDrawerScrollPosition() : null;
                MessageObject message = drawerScrollPosition != null ? feedController.getMessage(drawerScrollPosition.dialogId, drawerScrollPosition.messageId) : null;
                if (message != null && this.host.getMessages().contains(message)) {
                    applyUnreadDivider(false);
                    refreshAds();
                    this.pendingInitialScrollRestore = new ScrollAnchor(message, drawerScrollPosition.offsetTop);
                    requestPendingInitialPosition();
                } else {
                    applyUnreadDivider((zConsumeInitialUnreadScroll || this.host.getDistanceToNewerPx() > NEAR_NEWEST_THRESHOLD) ? zConsumeInitialUnreadScroll : true);
                }
            }
            FeedAdController.getInstance(this.currentAccount).ensureLoaded(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.refreshAds();
                }
            });
        }
    }

    public void onHostResumed() {
        if (!this.host.getMessages().isEmpty()) {
            onMessagesLoaded();
        }
        requestPendingInitialPosition();
    }

    private boolean hasPendingInitialPosition() {
        return this.pendingInitialScrollRestore != null || this.pendingDividerScroll;
    }

    private void requestPendingInitialPosition() {
        if (this.host.getFragment().isPaused() || !this.host.isListReady()) {
            return;
        }
        ScrollAnchor scrollAnchor = this.pendingInitialScrollRestore;
        if (scrollAnchor != null) {
            this.host.restoreScrollAnchor(scrollAnchor);
            return;
        }
        if (this.pendingDividerScroll) {
            int iIndexOf = this.unreadDivider == null ? -1 : this.host.getMessages().indexOf(this.unreadDivider);
            if (iIndexOf < 0) {
                this.pendingDividerScroll = false;
                this.readyToMarkAsRead = true;
            } else {
                this.host.scrollToMessage(iIndexOf, AndroidUtilities.dp(48.0f));
            }
        }
    }

    public void setViewportActive(boolean z) {
        if (this.viewportActive == z) {
            return;
        }
        this.viewportActive = z;
        if (!z) {
            if (this.settleAtNewestScheduled) {
                AndroidUtilities.cancelRunOnUIThread(this.settleAtNewestRunnable);
                this.settleAtNewestScheduled = false;
            }
            cancelPendingReactionsRefresh();
            return;
        }
        onHostResumed();
        if (this.host.getMessages().isEmpty()) {
            return;
        }
        onVisiblePartInvalidated();
    }

    public boolean canMarkVisibleAsRead() {
        return this.viewportActive && !this.host.getFragment().isPaused() && this.initialScrollApplied && this.readyToMarkAsRead && !this.pendingDividerScroll && this.pendingInitialScrollRestore == null && !BaseFragment.hasSheets(this.host.getFragment());
    }

    public void onPostCellVisible(MessageObject messageObject, boolean z, boolean z2) {
        if (messageObject == null || messageObject.isSponsored()) {
            return;
        }
        requestReactionsRefresh(messageObject);
        if (canMarkVisibleAsRead()) {
            if (z || z2) {
                FeedController.getInstance(this.currentAccount).onPostSeen(messageObject.getDialogId(), messageObject.getRealId());
            }
        }
    }

    private void requestReactionsRefresh(MessageObject messageObject) {
        if (this.destroyed || !this.viewportActive || messageObject.messageOwner == null) {
            return;
        }
        int realId = messageObject.getRealId();
        long dialogId = messageObject.getDialogId();
        if (realId <= 0 || dialogId == 0) {
            return;
        }
        int id = messageObject.getId();
        if (messageObject.messageOwner.action == null || messageObject.canSetReaction()) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            long j = id;
            if (jCurrentTimeMillis - this.reactionsLastCheckTimes.get(j, 0L).longValue() <= 15000) {
                return;
            }
            this.reactionsLastCheckTimes.put(j, Long.valueOf(jCurrentTimeMillis));
            ArrayList<Integer> arrayList = this.pendingReactionIds.get(dialogId);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.pendingReactionIds.put(dialogId, arrayList);
            }
            arrayList.add(Integer.valueOf(realId));
            if (this.reactionsRefreshScheduled) {
                return;
            }
            this.reactionsRefreshScheduled = true;
            AndroidUtilities.runOnUIThread(this.reactionsRefreshRunnable);
        }
    }

    public void flushReactionsRefresh() {
        this.reactionsRefreshScheduled = false;
        if (this.destroyed || !this.viewportActive) {
            this.pendingReactionIds.clear();
            return;
        }
        for (int i = 0; i < this.pendingReactionIds.size(); i++) {
            TLRPC.TL_messages_getMessagesReactions tL_messages_getMessagesReactions = new TLRPC.TL_messages_getMessagesReactions();
            tL_messages_getMessagesReactions.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.pendingReactionIds.keyAt(i));
            tL_messages_getMessagesReactions.id.addAll(this.pendingReactionIds.valueAt(i));
            ConnectionsManager.getInstance(this.currentAccount).bindRequestToGuid(ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getMessagesReactions, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$flushReactionsRefresh$0(tLObject, tL_error);
                }
            }), this.reactionsRequestGuid);
        }
        this.pendingReactionIds.clear();
    }

    public void lambda$hideChannelWithUndo$1(long j) {
        if (this.pendingHideDialogId == j) {
            this.pendingHideDialogId = 0L;
        }
    }

    public void undoHideChannel() {
        long j = this.pendingHideDialogId;
        if (j == 0) {
            return;
        }
        this.pendingHideDialogId = 0L;
        FeedController feedController = FeedController.getInstance(this.currentAccount);
        FeedConfig.getInstance(this.currentAccount).setExcluded(j, false);
        feedController.markConfigApplied();
        feedController.getStore().setHidden(j, false);
        reconcileWithStore();
        onFeedExclusionsChanged();
        notifyChannelsChanged();
    }

    public void onFeedExclusionsChanged() {
        this.lastPagedownCount = -1;
        updatePagedownCounter();
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE));
    }
}
