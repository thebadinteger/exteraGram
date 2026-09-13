package com.exteragram.messenger.feed;

import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;

final class FeedUnreadTracker {
    private final int currentAccount;
    private boolean flushScheduled;
    private final ArrayList<MessageObject> timeline;
    private final LongSparseArray<Integer> readInboxMaxByDialog = new LongSparseArray<>();
    private final LongSparseArray<Integer> pendingMaxReadId = new LongSparseArray<>();
    private final Runnable flushRunnable = new Runnable() { 
        @Override // java.lang.Runnable
        public final void run() {
            FeedUnreadTracker.this.flush();
        }
    };

    public FeedUnreadTracker(int i, ArrayList<MessageObject> arrayList) {
        this.currentAccount = i;
        this.timeline = arrayList;
    }

    public void clear() {
        if (this.flushScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.flushRunnable);
            this.flushScheduled = false;
        }
        flush();
        this.readInboxMaxByDialog.clear();
    }

    public void applyReadInboxMax(long j, int i) {
        if (i > this.readInboxMaxByDialog.get(j, 0).intValue()) {
            this.readInboxMaxByDialog.put(j, Integer.valueOf(i));
        }
    }

    public boolean isUnread(MessageObject messageObject) {
        return (messageObject == null || messageObject.isSponsored() || messageObject.getRealId() <= getEffectiveReadInboxMax(messageObject.getDialogId())) ? false : true;
    }

    private int getEffectiveReadInboxMax(long j) {
        return Math.max(this.readInboxMaxByDialog.get(j, 0).intValue(), this.pendingMaxReadId.get(j, 0).intValue());
    }

    public int findFirstUnreadIndex(ArrayList<MessageObject> arrayList) {
        if (arrayList != null && !this.readInboxMaxByDialog.isEmpty()) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                if (isUnread(arrayList.get(size))) {
                    return size;
                }
            }
        }
        return -1;
    }

    public int countUnreadBelow(ArrayList<MessageObject> arrayList, int i) {
        if (arrayList == null || this.readInboxMaxByDialog.isEmpty()) {
            return 0;
        }
        int iMin = Math.min(i, arrayList.size());
        int i2 = 0;
        for (int i3 = 0; i3 < iMin; i3++) {
            MessageObject messageObject = arrayList.get(i3);
            if (messageObject != null && !messageObject.isDateObject && messageObject.type != 6 && !messageObject.isSponsored() && isUnread(messageObject)) {
                i2++;
            }
        }
        return i2;
    }

    public void onPostSeen(long j, int i) {
        if (j == 0 || i <= 0 || i <= getEffectiveReadInboxMax(j)) {
            return;
        }
        Integer num = this.pendingMaxReadId.get(j);
        if (num == null || num.intValue() < i) {
            this.pendingMaxReadId.put(j, Integer.valueOf(i));
            if (this.flushScheduled) {
                return;
            }
            this.flushScheduled = true;
            AndroidUtilities.runOnUIThread(this.flushRunnable, 1000L);
        }
    }

    public void flush() {
        this.flushScheduled = false;
        if (this.pendingMaxReadId.isEmpty()) {
            return;
        }
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
        int i = 0;
        while (true) {
            int size = this.pendingMaxReadId.size();
            LongSparseArray<Integer> longSparseArray = this.pendingMaxReadId;
            if (i < size) {
                long jKeyAt = longSparseArray.keyAt(i);
                Integer numValueAt = this.pendingMaxReadId.valueAt(i);
                int iIntValue = numValueAt.intValue();
                int iIntValue2 = this.readInboxMaxByDialog.get(jKeyAt, 0).intValue();
                if (iIntValue > iIntValue2) {
                    this.readInboxMaxByDialog.put(jKeyAt, numValueAt);
                    messagesController.markDialogAsRead(jKeyAt, iIntValue, 0, currentTime, false, 0L, Math.max(countTimelineRows(jKeyAt, iIntValue2, iIntValue), 1), true, 0);
                }
                i++;
            } else {
                longSparseArray.clear();
                return;
            }
        }
    }

    private int countTimelineRows(long j, int i, int i2) {
        int realId;
        int i3 = 0;
        for (int i4 = 0; i4 < this.timeline.size(); i4++) {
            MessageObject messageObject = this.timeline.get(i4);
            if (messageObject != null && messageObject.getDialogId() == j && (realId = messageObject.getRealId()) > i && realId <= i2) {
                i3++;
            }
        }
        return i3;
    }

    public void markAllRead() {
        TLRPC.Dialog dialog;
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        HashSet hashSet = new HashSet();
        ArrayList<TLRPC.Dialog> arrayListCollectUnreadFeedDialogs = collectUnreadFeedDialogs();
        int size = arrayListCollectUnreadFeedDialogs.size();
        boolean z = false;
        int i = 0;
        while (i < size) {
            int i2 = i + 1;
            TLRPC.Dialog dialog2 = arrayListCollectUnreadFeedDialogs.get(i);
            messagesController.markMentionsAsRead(dialog2.id, 0L);
            long j = dialog2.id;
            int i3 = dialog2.top_message;
            messagesController.markDialogAsRead(j, i3, i3, dialog2.last_message_date, false, 0L, 0, true, 0);
            this.readInboxMaxByDialog.put(dialog2.id, Integer.valueOf(dialog2.top_message));
            hashSet.add(Long.valueOf(dialog2.id));
            i = i2;
            arrayListCollectUnreadFeedDialogs = arrayListCollectUnreadFeedDialogs;
            z = z;
        }
        boolean z2 = z;
        FeedConfig feedConfig = FeedConfig.getInstance(this.currentAccount);
        boolean includeArchived = feedConfig.getIncludeArchived();
        for (int i4 = z2 ? 1 : 0; i4 < this.timeline.size(); i4++) {
            MessageObject messageObject = this.timeline.get(i4);
            if (messageObject != null) {
                long dialogId = messageObject.getDialogId();
                if (!feedConfig.isExcluded(dialogId) && (includeArchived || (dialog = messagesController.dialogs_dict.get(dialogId)) == null || dialog.folder_id != 1)) {
                    hashSet.add(Long.valueOf(dialogId));
                    int realId = messageObject.getRealId();
                    if (realId > this.readInboxMaxByDialog.get(dialogId, Integer.valueOf(z2 ? 1 : 0)).intValue()) {
                        this.readInboxMaxByDialog.put(dialogId, Integer.valueOf(realId));
                    }
                }
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            this.pendingMaxReadId.remove(((Long) it.next()).longValue());
        }
        if (this.pendingMaxReadId.isEmpty() && this.flushScheduled) {
            AndroidUtilities.cancelRunOnUIThread(this.flushRunnable);
            this.flushScheduled = z2;
        }
    }

    public int getUnreadCount() {
        ArrayList<TLRPC.Dialog> arrayListCollectUnreadFeedDialogs = collectUnreadFeedDialogs();
        int size = arrayListCollectUnreadFeedDialogs.size();
        int i = 0;
        int i2 = 0;
        while (i2 < size) {
            TLRPC.Dialog dialog = arrayListCollectUnreadFeedDialogs.get(i2);
            i2++;
            i += dialog.unread_count;
        }
        return i;
    }

    private ArrayList<TLRPC.Dialog> collectUnreadFeedDialogs() {
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        FeedConfig feedConfig = FeedConfig.getInstance(this.currentAccount);
        boolean includeArchived = feedConfig.getIncludeArchived();
        LongSparseArray<TLRPC.Dialog> longSparseArray = messagesController.dialogs_dict;
        ArrayList<TLRPC.Dialog> arrayList = new ArrayList<>();
        for (int i = 0; i < longSparseArray.size(); i++) {
            TLRPC.Dialog dialogValueAt = longSparseArray.valueAt(i);
            if (dialogValueAt != null && dialogValueAt.unread_count > 0) {
                long j = dialogValueAt.id;
                if (DialogObject.isChatDialog(j) && !feedConfig.isExcluded(j) && ((includeArchived || dialogValueAt.folder_id != 1) && FeedController.isEligibleChannel(messagesController.getChat(Long.valueOf(-j))))) {
                    arrayList.add(dialogValueAt);
                }
            }
        }
        return arrayList;
    }
}
