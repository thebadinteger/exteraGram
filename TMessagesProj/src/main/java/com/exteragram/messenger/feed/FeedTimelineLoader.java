package com.exteragram.messenger.feed;

import android.text.TextUtils;
import androidx.collection.LongSparseArray;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;

final class FeedTimelineLoader {
    private final AtomicInteger channelCacheEpoch = new AtomicInteger();
    private volatile ChannelSet channelSetCache;
    private final int currentAccount;

    public static final class ChannelEnumeration {
        int cacheEpoch;
        int configGeneration;
        boolean failed;
        boolean hasChannels;
        final ArrayList<ChannelSnapshot> included = new ArrayList<>();
        final ArrayList<TLRPC.Chat> channels = new ArrayList<>();
    }

    public static final class NewerPage {
        boolean failed;
        boolean hasMore;
        final ArrayList<TLRPC.Message> messages = new ArrayList<>();
        final ArrayList<TLRPC.User> users = new ArrayList<>();
        final ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        final Cursor first = new Cursor();
    }

    public static final class OlderPage {
        boolean failed;
        boolean hasIncomplete;
        int lastChunkRowCount;
        final ArrayList<TLRPC.Message> messages = new ArrayList<>();
        final ArrayList<TLRPC.User> users = new ArrayList<>();
        final ArrayList<TLRPC.Chat> chats = new ArrayList<>();
        final ArrayList<long[]> backfillCandidates = new ArrayList<>();
        final Cursor last = new Cursor();
        final Cursor first = new Cursor();
    }

    public static final class WindowPage {
        boolean failed;
        boolean truncated;
        final ArrayList<TLRPC.Message> messages = new ArrayList<>();
        final ArrayList<TLRPC.User> users = new ArrayList<>();
        final ArrayList<TLRPC.Chat> chats = new ArrayList<>();
    }

    public FeedTimelineLoader(int i) {
        this.currentAccount = i;
    }

    public static final class Cursor {
        int date;
        int mid;
        long uid;

        public boolean isEmpty() {
            return this.date == 0;
        }

        public void set(int i, long j, int i2) {
            this.date = i;
            this.uid = j;
            this.mid = i2;
        }
    }

    public static final class ChannelSnapshot {
        int depthDate;
        int depthMid;
        final long dialogId;
        boolean hasCached;
        boolean hasHole;
        int holeEnd;
        boolean incomplete;
        boolean localStartReached;
        final int readInboxMax;
        final int topMessage;
        final int unreadCount;

        public ChannelSnapshot(long j, int i, int i2, int i3) {
            this.dialogId = j;
            this.readInboxMax = i;
            this.unreadCount = i2;
            this.topMessage = i3;
        }
    }

    public static final class ChannelSet {
        final int configGen;
        boolean failed;
        boolean hasChannels;
        final int sessionGen;
        final ArrayList<long[]> includedRows = new ArrayList<>();
        final ArrayList<TLRPC.Chat> channels = new ArrayList<>();

        public ChannelSet(int i, int i2) {
            this.sessionGen = i;
            this.configGen = i2;
        }
    }

    public synchronized void invalidateChannelCache() {
        this.channelCacheEpoch.incrementAndGet();
        this.channelSetCache = null;
    }

    public ChannelEnumeration enumerateChannels(FeedConfig feedConfig, int i, boolean z) {
        ChannelSet channelSetBuildChannelSet;
        int i2;
        boolean z2 = z;
        int i3 = 0;
        while (true) {
            synchronized (this) {
                channelSetBuildChannelSet = this.channelSetCache;
                i2 = this.channelCacheEpoch.get();
            }
            FeedConfig.Snapshot snapshot = feedConfig.snapshot();
            int generation = snapshot.getGeneration();
            if (z2 || channelSetBuildChannelSet == null || channelSetBuildChannelSet.sessionGen != i || channelSetBuildChannelSet.configGen != generation) {
                channelSetBuildChannelSet = buildChannelSet(snapshot.getIncludeArchived(), new HashSet<>(snapshot.getExcludedChannels()), i, generation);
                if (!channelSetBuildChannelSet.failed) {
                    synchronized (this) {
                        try {
                            if (i2 == this.channelCacheEpoch.get()) {
                                this.channelSetCache = channelSetBuildChannelSet;
                            }
                        } catch (Throwable th) {
                            throw th;
                        }
                    }
                }
            }
            if (!channelSetBuildChannelSet.failed && i3 < 3) {
                synchronized (this) {
                    try {
                        if (i2 == this.channelCacheEpoch.get()) {
                        }
                    } catch (Throwable th2) {
                        throw th2;
                    }
                }
                break;
            }
            break;
            i3++;
            z2 = true;
        }
        ChannelEnumeration channelEnumeration = new ChannelEnumeration();
        channelEnumeration.hasChannels = channelSetBuildChannelSet.hasChannels;
        channelEnumeration.failed = channelSetBuildChannelSet.failed;
        channelEnumeration.configGeneration = channelSetBuildChannelSet.configGen;
        channelEnumeration.cacheEpoch = i2;
        channelEnumeration.channels.addAll(channelSetBuildChannelSet.channels);
        for (int i4 = 0; i4 < channelSetBuildChannelSet.includedRows.size(); i4++) {
            long[] jArr = channelSetBuildChannelSet.includedRows.get(i4);
            channelEnumeration.included.add(new ChannelSnapshot(jArr[0], (int) jArr[1], (int) jArr[2], (int) jArr[3]));
        }
        return channelEnumeration;
    }

    public synchronized int getChannelCacheEpoch() {
        return this.channelCacheEpoch.get();
    }

    public synchronized boolean isEnumerationCurrent(ChannelEnumeration channelEnumeration) {
        boolean z;
        if (channelEnumeration == null) {
            z = false;
        } else if (channelEnumeration.cacheEpoch == this.channelCacheEpoch.get()) {
            z = true;
        } else {
            z = false;
        }
        return z;
    }

    private ChannelSet buildChannelSet(boolean z, HashSet<Long> hashSet, int i, int i2) {
        MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
        ChannelSet channelSet = new ChannelSet(i, i2);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        try {
            SQLiteCursor sQLiteCursorQueryFinalized = messagesStorage.getDatabase().queryFinalized("SELECT did, inbox_max, unread_count, last_mid, folder_id FROM dialogs WHERE did < 0", new Object[0]);
            while (sQLiteCursorQueryFinalized.next()) {
                long jLongValue = sQLiteCursorQueryFinalized.longValue(0);
                if (DialogObject.isChatDialog(jLongValue)) {
                    arrayList.add(new long[]{jLongValue, sQLiteCursorQueryFinalized.intValue(1), sQLiteCursorQueryFinalized.intValue(2), sQLiteCursorQueryFinalized.intValue(3), sQLiteCursorQueryFinalized.intValue(4)});
                    arrayList2.add(Long.valueOf(-jLongValue));
                }
            }
            sQLiteCursorQueryFinalized.dispose();
            if (!arrayList.isEmpty()) {
                ArrayList<TLRPC.Chat> arrayList3 = new ArrayList<>();
                try {
                    messagesStorage.getChatsInternal(TextUtils.join(",", arrayList2), arrayList3);
                    LongSparseArray longSparseArray = new LongSparseArray();
                    for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                        longSparseArray.put(arrayList3.get(i3).id, arrayList3.get(i3));
                    }
                    for (int i4 = 0; i4 < arrayList.size(); i4++) {
                        long[] jArr = (long[]) arrayList.get(i4);
                        long j = jArr[0];
                        TLRPC.Chat chat = (TLRPC.Chat) longSparseArray.get(-j);
                        if (FeedController.isEligibleChannel(chat) && (jArr[4] != 1 || z)) {
                            channelSet.hasChannels = true;
                            channelSet.channels.add(chat);
                            if (!hashSet.contains(Long.valueOf(j))) {
                                channelSet.includedRows.add(new long[]{j, jArr[1], jArr[2], jArr[3]});
                            }
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                    channelSet.failed = true;
                    return channelSet;
                }
            }
            return channelSet;
        } catch (Exception e2) {
            FileLog.e(e2);
            channelSet.failed = true;
            return channelSet;
        }
    }

    public OlderPage loadOlderPage(ArrayList<ChannelSnapshot> arrayList, Cursor cursor, HashSet<Long> hashSet) {
        boolean z;
        int i;
        boolean z2;
        long j;
        OlderPage olderPage = new OlderPage();
        boolean zIsEmpty = cursor.isEmpty();
        olderPage.last.set(cursor.date, cursor.uid, cursor.mid);
        boolean z3 = true;
        z3 = true;
        try {
            ArrayList arrayList2 = new ArrayList(arrayList.size());
            int size = arrayList.size();
            int i2 = 0;
            int i3 = 0;
            while (i3 < size) {
                ChannelSnapshot channelSnapshot = arrayList.get(i3);
                i3++;
                arrayList2.add(Long.valueOf(channelSnapshot.dialogId));
            }
            String strJoin = TextUtils.join(",", arrayList2);
            MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
            HashMap map = new HashMap();
            SQLiteCursor sQLiteCursorQueryFinalized = messagesStorage.getDatabase().queryFinalized("SELECT uid, max(end) FROM messages_holes WHERE uid IN (" + strJoin + ") GROUP BY uid", new Object[0]);
            while (sQLiteCursorQueryFinalized.next()) {
                map.put(Long.valueOf(sQLiteCursorQueryFinalized.longValue(0)), Integer.valueOf(sQLiteCursorQueryFinalized.intValue(1)));
            }
            sQLiteCursorQueryFinalized.dispose();
            int size2 = arrayList.size();
            int i4 = 0;
            while (i4 < size2) {
                ChannelSnapshot channelSnapshot2 = arrayList.get(i4);
                i4++;
                ChannelSnapshot channelSnapshot3 = channelSnapshot2;
                Integer num = (Integer) map.get(Long.valueOf(channelSnapshot3.dialogId));
                boolean z4 = num != null;
                channelSnapshot3.hasHole = z4;
                channelSnapshot3.holeEnd = z4 ? num.intValue() : 0;
            }
            loadChannelDepths(messagesStorage, arrayList);
            int size3 = arrayList.size();
            boolean z5 = zIsEmpty;
            int i5 = 0;
            int i6 = 0;
            while (i6 < size3) {
                ChannelSnapshot channelSnapshot4 = arrayList.get(i6);
                i6++;
                ChannelSnapshot channelSnapshot5 = channelSnapshot4;
                boolean z6 = (channelSnapshot5.localStartReached || hashSet.contains(Long.valueOf(channelSnapshot5.dialogId))) ? i2 : z3 ? 1 : 0;
                channelSnapshot5.incomplete = z6;
                if (z6 != 0) {
                    olderPage.hasIncomplete = z3;
                    int iMax = Math.max(i5, channelSnapshot5.depthDate);
                    if (channelSnapshot5.hasCached) {
                        i = i2;
                        z2 = z5;
                        j = channelSnapshot5.depthMid;
                    } else {
                        i = i2;
                        z2 = z5;
                        int iMax2 = Math.max(channelSnapshot5.holeEnd, channelSnapshot5.topMessage);
                        j = iMax2 > 0 ? iMax2 + 1 : 0L;
                    }
                    ArrayList<long[]> arrayList3 = olderPage.backfillCandidates;
                    z = z3 ? 1 : 0;
                    try {
                        long j2 = channelSnapshot5.dialogId;
                        long j3 = channelSnapshot5.depthDate;
                        long[] jArr = new long[3];
                        jArr[i] = j2;
                        jArr[z ? 1 : 0] = j;
                        jArr[2] = j3;
                        arrayList3.add(jArr);
                        i5 = iMax;
                    } catch (Exception e) {
                        e = e;
                        FileLog.e(e);
                        olderPage.failed = z;
                        clusterGroupedMessages(olderPage.messages);
                        return olderPage;
                    }
                } else {
                    z = z3 ? 1 : 0;
                    i = i2;
                    z2 = z5;
                }
                i2 = i;
                z5 = z2;
                z3 = z;
                arrayList2 = arrayList2;
                strJoin = strJoin;
            }
            String str = strJoin;
            z = z3 ? 1 : 0;
            ArrayList arrayList4 = arrayList2;
            int i7 = i2;
            boolean z7 = z5;
            olderPage.backfillCandidates.sort(new Comparator() { 
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return Long.compare(((long[]) obj2)[2], ((long[]) obj)[2]);
                }
            });
            if (i5 == Integer.MAX_VALUE) {
                return olderPage;
            }
            Cursor cursorFindUnreadBoundary = z7 ? findUnreadBoundary(messagesStorage, arrayList, i5) : null;
            ArrayList<Long> arrayList5 = new ArrayList<>();
            ArrayList<Long> arrayList6 = new ArrayList<>();
            int i8 = i7;
            do {
                int iLoadChunk = loadChunk(messagesStorage, str, i5, olderPage, arrayList5, arrayList6);
                olderPage.lastChunkRowCount = iLoadChunk;
                i8 += iLoadChunk;
                if (iLoadChunk < 30 || cursorFindUnreadBoundary == null || i8 >= 200) {
                    break;
                    break;
                    break;
                }
            } while (compareDesc(olderPage.last, cursorFindUnreadBoundary) < 0);
            completeTrailingAlbum(messagesStorage, olderPage, arrayList5, arrayList6);
            int size4 = arrayList4.size();
            int i9 = i7;
            while (i9 < size4) {
                ArrayList arrayList7 = arrayList4;
                Object obj = arrayList7.get(i9);
                i9++;
                long j4 = -((Long) obj).longValue();
                if (!arrayList6.contains(Long.valueOf(j4))) {
                    arrayList6.add(Long.valueOf(j4));
                }
                arrayList4 = arrayList7;
            }
            if (!arrayList5.isEmpty()) {
                messagesStorage.getUsersInternal(arrayList5, olderPage.users);
            }
            if (!arrayList6.isEmpty()) {
                messagesStorage.getChatsInternal(TextUtils.join(-50605321373231L), arrayList6), olderPage.chats);
            }
            clusterGroupedMessages(olderPage.messages);
            return olderPage;
        } catch (Exception e2) {
            e = e2;
            z = z3;
        }
    }

    private int loadChunk(MessagesStorage messagesStorage, String str, int i, OlderPage olderPage, ArrayList<Long> arrayList, ArrayList<Long> arrayList2) {
        StringBuilder sb = new StringBuilder("SELECT data, mid, date, uid FROM messages_v2 WHERE uid IN (");
        sb.append(str);
        sb.append(") AND mid > 0");
        if (i > 0) {
            sb.append(" AND date >= ");
            sb.append(i);
        }
        int i2 = 0;
        if (!olderPage.last.isEmpty()) {
            appendCursorBound(sb, olderPage.last, true, false);
        }
        sb.append(" ORDER BY date DESC, uid DESC, mid DESC LIMIT ");
        sb.append(30);
        SQLiteCursor sQLiteCursorQueryFinalized = messagesStorage.getDatabase().queryFinalized(sb.toString(), new Object[0]);
        while (sQLiteCursorQueryFinalized.next()) {
            i2++;
            olderPage.last.set(sQLiteCursorQueryFinalized.intValue(2), sQLiteCursorQueryFinalized.longValue(3), sQLiteCursorQueryFinalized.intValue(1));
            if (olderPage.first.isEmpty()) {
                Cursor cursor = olderPage.first;
                Cursor cursor2 = olderPage.last;
                cursor.set(cursor2.date, cursor2.uid, cursor2.mid);
            }
            TLRPC.Message message = readMessage(sQLiteCursorQueryFinalized);
            if (message != null) {
                olderPage.messages.add(message);
                MessagesStorage.addUsersAndChatsFromMessage(message, arrayList, arrayList2, null);
            }
        }
        sQLiteCursorQueryFinalized.dispose();
        return i2;
    }

    private Cursor findUnreadBoundary(MessagesStorage messagesStorage, ArrayList<ChannelSnapshot> arrayList, int i) {
        StringBuilder sb = new StringBuilder();
        Cursor cursor = null;
        int i2 = 0;
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            ChannelSnapshot channelSnapshot = arrayList.get(i3);
            if (channelSnapshot.topMessage > channelSnapshot.readInboxMax || channelSnapshot.unreadCount > 0) {
                if (sb.length() > 0) {
                    sb.append(" OR ");
                }
                sb.append("uid = ");
                sb.append(channelSnapshot.dialogId);
                sb.append(" AND mid > ");
                sb.append(channelSnapshot.readInboxMax);
                i2++;
            }
            if (i2 > 0 && (i2 == 64 || i3 == arrayList.size() - 1)) {
                Cursor cursorQueryUnreadBoundary = queryUnreadBoundary(messagesStorage, sb, i);
                if (cursorQueryUnreadBoundary != null && (cursor == null || compareDesc(cursorQueryUnreadBoundary, cursor) > 0)) {
                    cursor = cursorQueryUnreadBoundary;
                }
                sb.setLength(0);
                i2 = 0;
            }
        }
        return cursor;
    }

    private Cursor queryUnreadBoundary(MessagesStorage messagesStorage, StringBuilder sb, int i) {
        StringBuilder sb2 = new StringBuilder("SELECT date, uid, mid FROM messages_v2 WHERE mid > 0 AND (");
        sb2.append((CharSequence) sb);
        sb2.append(")");
        if (i > 0) {
            sb2.append(" AND date >= ");
            sb2.append(i);
        }
        sb2.append(" ORDER BY date ASC, uid ASC, mid ASC LIMIT 1");
        try {
            SQLiteCursor sQLiteCursorQueryFinalized = messagesStorage.getDatabase().queryFinalized(sb2.toString(), new Object[0]);
            try {
                if (!sQLiteCursorQueryFinalized.next()) {
                    return null;
                }
                Cursor cursor = new Cursor();
                cursor.set(sQLiteCursorQueryFinalized.intValue(0), sQLiteCursorQueryFinalized.longValue(1), sQLiteCursorQueryFinalized.intValue(2));
                return cursor;
            } finally {
                sQLiteCursorQueryFinalized.dispose();
            }
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    private static void appendCursorBound(StringBuilder sb, Cursor cursor, boolean z, boolean z2) {
        String string = z ? "<" : ">";
        sb.append(" AND (date ");
        sb.append(string);
        sb.append(' ');
        sb.append(cursor.date);
        sb.append(" OR date = ");
        sb.append(cursor.date);
        sb.append(" AND (uid ");
        sb.append(string);
        sb.append(' ');
        sb.append(cursor.uid);
        sb.append(" OR uid = ");
        sb.append(cursor.uid);
        sb.append(" AND mid ");
        sb.append(string);
        sb.append(z2 ? "= " : " ");
        sb.append(cursor.mid);
        sb.append("))");
    }

    private static int compareDesc(Cursor cursor, Cursor cursor2) {
        int i = cursor.date;
        int i2 = cursor2.date;
        if (i != i2) {
            return i > i2 ? -1 : 1;
        }
        long j = cursor.uid;
        long j2 = cursor2.uid;
        if (j != j2) {
            return j > j2 ? -1 : 1;
        }
        return -Integer.compare(cursor.mid, cursor2.mid);
    }

    public NewerPage loadNewerPage(ArrayList<ChannelSnapshot> arrayList, Cursor cursor) {
        NewerPage newerPage = new NewerPage();
        newerPage.first.set(cursor.date, cursor.uid, cursor.mid);
        try {
            ArrayList arrayList2 = new ArrayList(arrayList.size());
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                ChannelSnapshot channelSnapshot = arrayList.get(i);
                i++;
                arrayList2.add(Long.valueOf(channelSnapshot.dialogId));
            }
            MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
            ArrayList<Long> arrayList3 = new ArrayList<>();
            ArrayList arrayList4 = new ArrayList();
            StringBuilder sb = new StringBuilder("SELECT data, mid, date, uid FROM messages_v2 WHERE uid IN (");
            sb.append(TextUtils.join(",", arrayList2));
            sb.append(") AND mid > 0");
            appendCursorBound(sb, cursor, false, false);
            sb.append(" ORDER BY date ASC, uid ASC, mid ASC LIMIT ");
            sb.append(50);
            SQLiteCursor sQLiteCursorQueryFinalized = messagesStorage.getDatabase().queryFinalized(sb.toString(), new Object[0]);
            int i2 = 0;
            while (sQLiteCursorQueryFinalized.next()) {
                i2++;
                newerPage.first.set(sQLiteCursorQueryFinalized.intValue(2), sQLiteCursorQueryFinalized.longValue(3), sQLiteCursorQueryFinalized.intValue(1));
                TLRPC.Message message = readMessage(sQLiteCursorQueryFinalized);
                if (message != null) {
                    newerPage.messages.add(message);
                    MessagesStorage.addUsersAndChatsFromMessage(message, arrayList3, arrayList4, null);
                }
            }
            sQLiteCursorQueryFinalized.dispose();
            newerPage.hasMore = i2 == 50;
            if (!arrayList3.isEmpty()) {
                messagesStorage.getUsersInternal(arrayList3, newerPage.users);
            }
            if (!arrayList4.isEmpty()) {
                messagesStorage.getChatsInternal(TextUtils.join(",", arrayList4), newerPage.chats);
            }
        } catch (Exception e) {
            FileLog.e(e);
            newerPage.failed = true;
        }
        clusterGroupedMessages(newerPage.messages);
        return newerPage;
    }

    public WindowPage loadChannelWindow(ArrayList<Long> arrayList, Cursor cursor, Cursor cursor2) {
        WindowPage windowPage = new WindowPage();
        if (!arrayList.isEmpty() && !cursor.isEmpty() && !cursor2.isEmpty()) {
            try {
                MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
                ArrayList<Long> arrayList2 = new ArrayList<>();
                ArrayList arrayList3 = new ArrayList();
                StringBuilder sb = new StringBuilder("SELECT data, mid, date, uid FROM messages_v2 WHERE uid IN (");
                sb.append(TextUtils.join(",", arrayList));
                sb.append(") AND mid > 0");
                appendCursorBound(sb, cursor, true, true);
                int i = 0;
                appendCursorBound(sb, cursor2, false, true);
                sb.append(" ORDER BY date DESC, uid DESC, mid DESC LIMIT ");
                sb.append(501);
                SQLiteCursor sQLiteCursorQueryFinalized = messagesStorage.getDatabase().queryFinalized(sb.toString(), new Object[0]);
                while (sQLiteCursorQueryFinalized.next()) {
                    i++;
                    if (i > 500) {
                        windowPage.truncated = true;
                        break;
                    }
                    TLRPC.Message message = readMessage(sQLiteCursorQueryFinalized);
                    if (message != null) {
                        windowPage.messages.add(message);
                        MessagesStorage.addUsersAndChatsFromMessage(message, arrayList2, arrayList3, null);
                    }
                }
                sQLiteCursorQueryFinalized.dispose();
                if (!arrayList2.isEmpty()) {
                    messagesStorage.getUsersInternal(arrayList2, windowPage.users);
                }
                if (!arrayList3.isEmpty()) {
                    messagesStorage.getChatsInternal(TextUtils.join(",", arrayList3), windowPage.chats);
                }
            } catch (Exception e) {
                FileLog.e(e);
                windowPage.failed = true;
                windowPage.messages.clear();
                windowPage.users.clear();
                windowPage.chats.clear();
            }
            clusterGroupedMessages(windowPage.messages);
        }
        return windowPage;
    }

    private void completeTrailingAlbum(MessagesStorage messagesStorage, OlderPage olderPage, ArrayList<Long> arrayList, ArrayList<Long> arrayList2) {
        TLRPC.Message message;
        if (olderPage.messages.isEmpty()) {
            return;
        }
        ArrayList<TLRPC.Message> arrayList3 = olderPage.messages;
        TLRPC.Message message2 = arrayList3.get(arrayList3.size() - 1);
        if (message2.grouped_id == 0) {
            return;
        }
        SQLiteCursor sQLiteCursorQueryFinalized = messagesStorage.getDatabase().queryFinalized("SELECT data, mid, date, uid FROM messages_v2 WHERE uid = " + message2.dialog_id + " AND mid > 0 AND mid < " + message2.id + " ORDER BY date DESC, mid DESC LIMIT 9", new Object[0]);
        while (sQLiteCursorQueryFinalized.next() && (message = readMessage(sQLiteCursorQueryFinalized)) != null && message.grouped_id == message2.grouped_id) {
            try {
                olderPage.messages.add(message);
                MessagesStorage.addUsersAndChatsFromMessage(message, arrayList, arrayList2, null);
            } catch (Throwable th) {
                sQLiteCursorQueryFinalized.dispose();
                throw th;
            }
        }
        sQLiteCursorQueryFinalized.dispose();
    }

    private TLRPC.Message readMessage(SQLiteCursor sQLiteCursor) {
        NativeByteBuffer nativeByteBufferByteBufferValue = sQLiteCursor.byteBufferValue(0);
        if (nativeByteBufferByteBufferValue == null) {
            return null;
        }
        TLRPC.Message messageTLdeserialize = TLRPC.Message.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false);
        if (messageTLdeserialize == null) {
            nativeByteBufferByteBufferValue.reuse();
            return null;
        }
        messageTLdeserialize.readAttachPath(nativeByteBufferByteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
        nativeByteBufferByteBufferValue.reuse();
        if ((messageTLdeserialize instanceof TLRPC.TL_messageEmpty) || messageTLdeserialize.action != null) {
            return null;
        }
        messageTLdeserialize.id = sQLiteCursor.intValue(1);
        messageTLdeserialize.date = sQLiteCursor.intValue(2);
        messageTLdeserialize.dialog_id = sQLiteCursor.longValue(3);
        return messageTLdeserialize;
    }

    private static void loadChannelDepths(MessagesStorage messagesStorage, ArrayList<ChannelSnapshot> arrayList) {
        LongSparseArray longSparseArray = new LongSparseArray(arrayList.size());
        for (int i = 0; i < arrayList.size(); i++) {
            ChannelSnapshot channelSnapshot = arrayList.get(i);
            channelSnapshot.depthMid = 0;
            channelSnapshot.depthDate = Integer.MAX_VALUE;
            channelSnapshot.hasCached = false;
            channelSnapshot.localStartReached = false;
            longSparseArray.put(channelSnapshot.dialogId, channelSnapshot);
        }
        int i2 = 0;
        while (i2 < arrayList.size()) {
            int i3 = i2 + 64;
            int iMin = Math.min(i3, arrayList.size());
            StringBuilder sb = new StringBuilder();
            while (i2 < iMin) {
                if (sb.length() > 0) {
                    sb.append(" UNION ALL ");
                }
                ChannelSnapshot channelSnapshot2 = arrayList.get(i2);
                int iMax = Math.max(channelSnapshot2.holeEnd, 1);
                sb.append("SELECT uid, mid, date FROM (SELECT uid, mid, date FROM messages_v2 WHERE uid = ");
                sb.append(channelSnapshot2.dialogId);
                sb.append(" AND mid >= ");
                sb.append(iMax);
                sb.append(" ORDER BY date ASC, mid ASC LIMIT 1)");
                i2++;
            }
            SQLiteCursor sQLiteCursorQueryFinalized = messagesStorage.getDatabase().queryFinalized(sb.toString(), new Object[0]);
            while (sQLiteCursorQueryFinalized.next()) {
                try {
                    ChannelSnapshot channelSnapshot3 = (ChannelSnapshot) longSparseArray.get(sQLiteCursorQueryFinalized.longValue(0));
                    if (channelSnapshot3 != null) {
                        channelSnapshot3.depthMid = sQLiteCursorQueryFinalized.intValue(1);
                        channelSnapshot3.depthDate = sQLiteCursorQueryFinalized.intValue(2);
                        channelSnapshot3.hasCached = true;
                    }
                } catch (Throwable th) {
                    sQLiteCursorQueryFinalized.dispose();
                    throw th;
                }
            }
            sQLiteCursorQueryFinalized.dispose();
            i2 = i3;
        }
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            ChannelSnapshot channelSnapshot4 = arrayList.get(i4);
            channelSnapshot4.localStartReached = !channelSnapshot4.hasHole && channelSnapshot4.hasCached;
        }
    }

    private static void clusterGroupedMessages(ArrayList<TLRPC.Message> arrayList) {
        if (arrayList.size() < 3) {
            return;
        }
        HashMap map = new HashMap();
        boolean z = false;
        for (int i = 0; i < arrayList.size(); i++) {
            long j = arrayList.get(i).grouped_id;
            if (j != 0) {
                ArrayList arrayList2 = (ArrayList) map.get(Long.valueOf(j));
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                    map.put(Long.valueOf(j), arrayList2);
                } else {
                    z = true;
                }
                arrayList2.add(arrayList.get(i));
            }
        }
        if (z) {
            ArrayList arrayList3 = new ArrayList(arrayList.size());
            HashSet hashSet = new HashSet();
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                TLRPC.Message message = arrayList.get(i2);
                long j2 = message.grouped_id;
                if (j2 == 0) {
                    arrayList3.add(message);
                } else if (hashSet.add(Long.valueOf(j2))) {
                    arrayList3.addAll((Collection) map.get(Long.valueOf(j2)));
                }
            }
            arrayList.clear();
            arrayList.addAll(arrayList3);
        }
    }
}
