package com.exteragram.messenger.feed;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import org.telegram.messenger.MessageObject;

public final class FeedStore {
    private int count;
    private boolean endReached;
    private final ArrayList<MessageObject> messages = new ArrayList<>();
    private final FeedMessageIdentityMap identityMap = new FeedMessageIdentityMap();
    private final HashSet<Long> hiddenDialogIds = new HashSet<>();
    private final FeedTimelineLoader.Cursor oldestCursor = new FeedTimelineLoader.Cursor();
    private final FeedTimelineLoader.Cursor newestCursor = new FeedTimelineLoader.Cursor();

    public ArrayList<MessageObject> getMessages() {
        return this.messages;
    }

    public ArrayList<MessageObject> getVisibleMessages() {
        if (this.hiddenDialogIds.isEmpty()) {
            return new ArrayList<>(this.messages);
        }
        ArrayList<MessageObject> arrayList = new ArrayList<>(this.messages.size());
        for (int i = 0; i < this.messages.size(); i++) {
            MessageObject messageObject = this.messages.get(i);
            if (messageObject != null && !this.hiddenDialogIds.contains(Long.valueOf(messageObject.getDialogId()))) {
                arrayList.add(messageObject);
            }
        }
        return arrayList;
    }

    public boolean isEmpty() {
        return this.messages.isEmpty();
    }

    public int getVisibleCount() {
        if (this.hiddenDialogIds.isEmpty()) {
            return this.messages.size();
        }
        int i = 0;
        for (int i2 = 0; i2 < this.messages.size(); i2++) {
            MessageObject messageObject = this.messages.get(i2);
            if (messageObject != null && !this.hiddenDialogIds.contains(Long.valueOf(messageObject.getDialogId()))) {
                i++;
            }
        }
        return i;
    }

    public boolean hasMessagesForDialog(long j) {
        for (int i = 0; i < this.messages.size(); i++) {
            MessageObject messageObject = this.messages.get(i);
            if (messageObject != null && messageObject.getDialogId() == j) {
                return true;
            }
        }
        return false;
    }

    public HashSet<Long> getLoadedDialogIds() {
        HashSet<Long> hashSet = new HashSet<>();
        for (int i = 0; i < this.messages.size(); i++) {
            MessageObject messageObject = this.messages.get(i);
            if (messageObject != null) {
                hashSet.add(Long.valueOf(messageObject.getDialogId()));
            }
        }
        return hashSet;
    }

    public HashSet<Long> getHiddenSnapshot() {
        return new HashSet<>(this.hiddenDialogIds);
    }

    public boolean setHidden(long j, boolean z) {
        HashSet<Long> hashSet = this.hiddenDialogIds;
        Long lValueOf = Long.valueOf(j);
        boolean zAdd = z ? hashSet.add(lValueOf) : hashSet.remove(lValueOf);
        if (zAdd) {
            updateCount();
        }
        return zAdd;
    }

    public boolean applyIncludedDialogs(HashSet<Long> hashSet) {
        HashSet<Long> loadedDialogIds = getLoadedDialogIds();
        boolean zAdd = false;
        for (Long l : loadedDialogIds) {
            if (!hashSet.contains(l)) {
                zAdd |= this.hiddenDialogIds.add(l);
            }
        }
        Iterator<Long> it = this.hiddenDialogIds.iterator();
        while (it.hasNext()) {
            Long next = it.next();
            if (hashSet.contains(next) || !loadedDialogIds.contains(next)) {
                it.remove();
                zAdd = true;
            }
        }
        if (zAdd) {
            updateCount();
        }
        return zAdd;
    }

    public FeedTimelineLoader.Cursor getOldestCursor() {
        return this.oldestCursor;
    }

    public FeedTimelineLoader.Cursor getNewestCursor() {
        return this.newestCursor;
    }

    public boolean isEndReached() {
        return this.endReached;
    }

    public void setEndReached(boolean z) {
        this.endReached = z;
        updateCount();
    }

    public int getCount() {
        return this.count;
    }

    private void updateCount() {
        int visibleCount = 0;
        if (!this.messages.isEmpty()) {
            visibleCount = (this.endReached ? 0 : 3) + getVisibleCount();
        }
        this.count = visibleCount;
    }

    public void clear() {
        this.messages.clear();
        this.identityMap.clear();
        this.hiddenDialogIds.clear();
        this.endReached = false;
        this.count = 0;
        this.oldestCursor.set(0, 0L, 0);
        this.newestCursor.set(0, 0L, 0);
    }

    public ArrayList<MessageObject> appendMessages(ArrayList<MessageObject> arrayList, boolean z) {
        ArrayList<MessageObject> arrayList2 = new ArrayList<>(arrayList.size());
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            MessageObject messageObject = arrayList.get(i);
            i++;
            MessageObject messageObject2 = messageObject;
            if (this.identityMap.register(messageObject2)) {
                arrayList2.add(messageObject2);
            }
        }
        if (z) {
            ArrayList arrayList3 = new ArrayList(arrayList2);
            Collections.reverse(arrayList3);
            this.messages.addAll(0, arrayList3);
        } else {
            this.messages.addAll(arrayList2);
        }
        updateCount();
        return arrayList2;
    }

    public ArrayList<MessageObject> mergeRows(ArrayList<MessageObject> arrayList) {
        ArrayList<MessageObject> arrayList2 = new ArrayList<>(arrayList.size());
        int size = arrayList.size();
        int i = 0;
        int i2 = 0;
        while (i2 < size) {
            MessageObject messageObject = arrayList.get(i2);
            i2++;
            MessageObject messageObject2 = messageObject;
            if (this.identityMap.register(messageObject2)) {
                arrayList2.add(messageObject2);
            }
        }
        int iFindMergeIndex = 0;
        while (i < arrayList2.size()) {
            MessageObject messageObject3 = arrayList2.get(i);
            int i3 = i + 1;
            long groupId = messageObject3.getGroupId();
            while (groupId != 0 && i3 < arrayList2.size() && arrayList2.get(i3).getGroupId() == groupId && arrayList2.get(i3).getDialogId() == messageObject3.getDialogId()) {
                i3++;
            }
            iFindMergeIndex = findMergeIndex(messageObject3, iFindMergeIndex);
            while (i < i3) {
                this.messages.add(iFindMergeIndex, arrayList2.get(i));
                i++;
                iFindMergeIndex++;
            }
            i = i3;
        }
        updateCount();
        return arrayList2;
    }

    private int findMergeIndex(MessageObject messageObject, int i) {
        MessageObject messageObject2;
        while (i < this.messages.size() && ((messageObject2 = this.messages.get(i)) == null || compareTimeline(messageObject2.messageOwner.date, messageObject2.getDialogId(), messageObject2.getRealId(), messageObject.messageOwner.date, messageObject.getDialogId(), messageObject.getRealId()) >= 0)) {
            i++;
        }
        while (i > 0 && i < this.messages.size()) {
            MessageObject messageObject3 = this.messages.get(i - 1);
            MessageObject messageObject4 = this.messages.get(i);
            if (messageObject3 == null || messageObject4 == null || messageObject3.getGroupId() == 0 || messageObject3.getGroupId() != messageObject4.getGroupId() || messageObject3.getDialogId() != messageObject4.getDialogId()) {
                break;
            }
            i++;
        }
        return i;
    }

    public void replaceMessage(MessageObject messageObject, MessageObject messageObject2) {
        if (messageObject == null || messageObject2 == null) {
            return;
        }
        int iIndexOf = this.messages.indexOf(messageObject);
        if (iIndexOf >= 0) {
            this.messages.set(iIndexOf, messageObject2);
        }
        this.identityMap.replace(messageObject2);
    }

    public ArrayList<Integer> deleteMessages(long j, ArrayList<Integer> arrayList, boolean[] zArr) {
        ArrayList<Integer> arrayList2 = new ArrayList<>();
        if (arrayList == null) {
            return arrayList2;
        }
        HashSet hashSet = new HashSet(arrayList);
        HashSet<Integer> hashSet2 = new HashSet<>();
        boolean zRemove = false;
        for (int i = 0; i < arrayList.size(); i++) {
            MessageObject byRealId = this.identityMap.getByRealId(j, arrayList.get(i).intValue());
            if (byRealId != null) {
                zRemove |= this.messages.remove(byRealId);
                purgeRow(byRealId, arrayList2, hashSet2);
            }
        }
        for (int size = this.messages.size() - 1; size >= 0; size--) {
            MessageObject messageObject = this.messages.get(size);
            if (messageObject != null && messageObject.getDialogId() == j && hashSet.contains(Integer.valueOf(messageObject.getRealId()))) {
                this.messages.remove(size);
                purgeRow(messageObject, arrayList2, hashSet2);
                zRemove = true;
            }
        }
        if (zRemove) {
            onRowsRemoved();
        }
        zArr[0] = zRemove;
        return arrayList2;
    }

    public ArrayList<Integer> deleteHistory(long j, int i, boolean[] zArr) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        HashSet<Integer> hashSet = new HashSet<>();
        boolean z = false;
        for (int size = this.messages.size() - 1; size >= 0; size--) {
            MessageObject messageObject = this.messages.get(size);
            if (messageObject != null && messageObject.getDialogId() == j && messageObject.getRealId() > 0 && messageObject.getRealId() <= i) {
                this.messages.remove(size);
                purgeRow(messageObject, arrayList, hashSet);
                z = true;
            }
        }
        if (z) {
            if (!hasMessagesForDialog(j)) {
                this.hiddenDialogIds.remove(Long.valueOf(j));
            }
            onRowsRemoved();
        }
        zArr[0] = z;
        return arrayList;
    }

    public boolean trim(int i) {
        if (this.messages.size() <= i) {
            return false;
        }
        MessageObject messageObject = this.messages.get(i - 1);
        int i2 = messageObject.messageOwner.date;
        long dialogId = messageObject.getDialogId();
        int realId = messageObject.getRealId();
        boolean z = false;
        for (int size = this.messages.size() - 1; size >= 0; size--) {
            MessageObject messageObject2 = this.messages.get(size);
            if (messageObject2 != null && compareTimeline(messageObject2.messageOwner.date, messageObject2.getDialogId(), messageObject2.getRealId(), i2, dialogId, realId) < 0) {
                this.messages.remove(size);
                this.identityMap.releaseRow(messageObject2);
                z = true;
            }
        }
        if (!z) {
            return false;
        }
        if (this.messages.isEmpty()) {
            this.oldestCursor.set(0, 0L, 0);
        } else {
            int i3 = 0;
            int realId2 = 0;
            long j = 0;
            for (int i4 = 0; i4 < this.messages.size(); i4++) {
                MessageObject messageObject3 = this.messages.get(i4);
                if (messageObject3 != null && (i3 == 0 || compareTimeline(messageObject3.messageOwner.date, messageObject3.getDialogId(), messageObject3.getRealId(), i3, j, realId2) < 0)) {
                    i3 = messageObject3.messageOwner.date;
                    long dialogId2 = messageObject3.getDialogId();
                    realId2 = messageObject3.getRealId();
                    j = dialogId2;
                }
            }
            this.oldestCursor.set(i3, j, realId2);
        }
        this.endReached = false;
        updateCount();
        return true;
    }

    private void onRowsRemoved() {
        if (!rebuildPagingCursorsFromLoadedRows()) {
            this.endReached = false;
        }
        updateCount();
    }

    /* JADX WARN: Code duplicated, block: B:21:0x0064  */
    private boolean rebuildPagingCursorsFromLoadedRows() {
        long j;
        int i;
        long j2;
        int i2;
        long j3;
        int i3;
        boolean zIsEmpty = this.oldestCursor.isEmpty();
        this.newestCursor.isEmpty();
        int i4 = 0;
        int i5 = 0;
        int i6 = 0;
        int i7 = 0;
        long j4 = 0;
        long j5 = 0;
        for (int i8 = 0; i8 < this.messages.size(); i8++) {
            MessageObject messageObject = this.messages.get(i8);
            if (isPagingRow(messageObject)) {
                int i9 = messageObject.messageOwner.date;
                long dialogId = messageObject.getDialogId();
                int realId = messageObject.getRealId();
                if (i4 != 0) {
                    int iCompareTimeline = compareTimeline(i9, dialogId, realId, i4, j4, i5);
                    i2 = realId;
                    i3 = i5;
                    j2 = dialogId;
                    j3 = j4;
                    if (iCompareTimeline > 0) {
                    }
                    if (i6 != 0 || compareTimeline(i9, j2, i2, i6, j5, i7) < 0) {
                        i6 = i9;
                        j5 = j2;
                        i7 = i2;
                    }
                    j4 = j3;
                    i5 = i3;
                } else {
                    j2 = dialogId;
                    i2 = realId;
                }
                i4 = i9;
                j3 = j2;
                i3 = i2;
                if (i6 != 0) {
                    i6 = i9;
                    j5 = j2;
                    i7 = i2;
                } else {
                    i6 = i9;
                    j5 = j2;
                    i7 = i2;
                }
                j4 = j3;
                i5 = i3;
            }
        }
        long j6 = j4;
        int i10 = i5;
        if (i4 == 0) {
            this.oldestCursor.set(0, 0L, 0);
            this.newestCursor.set(0, 0L, 0);
            return false;
        }
        if (zIsEmpty) {
            j = j5;
            i = i7;
        } else {
            FeedTimelineLoader.Cursor cursor = this.oldestCursor;
            int i11 = i6;
            j = j5;
            i = i7;
            i6 = i11;
            if (compareTimeline(i11, j, i, cursor.date, cursor.uid, cursor.mid) > 0) {
                this.endReached = false;
            }
        }
        this.newestCursor.set(i4, j6, i10);
        this.oldestCursor.set(i6, j, i);
        return true;
    }

    private static boolean isPagingRow(MessageObject messageObject) {
        return (messageObject == null || messageObject.isDateObject || messageObject.messageOwner == null || messageObject.getRealId() <= 0) ? false : true;
    }

    public static int compareTimeline(int i, long j, int i2, int i3, long j2, int i4) {
        if (i != i3) {
            return Integer.compare(i, i3);
        }
        if (j != j2) {
            return Long.compare(j, j2);
        }
        return Integer.compare(i2, i4);
    }

    private void purgeRow(MessageObject messageObject, ArrayList<Integer> arrayList, HashSet<Integer> hashSet) {
        this.identityMap.purge(messageObject);
        if (hashSet.add(Integer.valueOf(messageObject.getId()))) {
            arrayList.add(Integer.valueOf(messageObject.getId()));
        }
    }

    public boolean hasNoSyntheticIds() {
        return this.identityMap.isEmpty();
    }

    public MessageObject getMessage(long j, int i) {
        return this.identityMap.getByAnyId(j, i);
    }

    public int resolveRealMessageId(long j, int i) {
        return this.identityMap.resolveRealMessageId(j, i);
    }

    public long resolveRealDialogId(int i) {
        return this.identityMap.resolveRealDialogId(i);
    }
}
