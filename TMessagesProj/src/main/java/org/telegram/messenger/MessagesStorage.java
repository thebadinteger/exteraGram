package org.telegram.messenger;

import android.appwidget.AppWidgetManager;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseIntArray;
import androidx.collection.LongSparseArray;
import com.exteragram.messenger.feed.FeedController;
import com.exteragram.messenger.utils.text.LocaleUtils;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import me.vkryl.core.BitwiseUtils;
import okhttp3.internal.url._UrlKt;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_communities;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.tgnet.tl.TL_update;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.Reactions.ReactionsUtils;
import org.telegram.ui.Stories.StoriesController;

public class MessagesStorage extends BaseController {
    public static final String[] DATABASE_TABLES;
    public static final int FORUM_TYPE_BOT = 8;
    public static final int FORUM_TYPE_CHAT = 1;
    public static final int FORUM_TYPE_CHAT_TABS = 2;
    public static final int FORUM_TYPE_DIRECT = 4;
    public static final int LAST_DB_VERSION = 176;
    public static final int SENT_FILE_TYPE_AUDIO = 1;
    public static final int SENT_FILE_TYPE_AUDIO_ENCRYPTED = 4;
    public static final int SENT_FILE_TYPE_PHOTO = 0;
    public static final int SENT_FILE_TYPE_PHOTO_ENCRYPTED = 3;
    public static final int SENT_FILE_TYPE_PHOTO_HIGH_QUALITY = 6;
    public static final int SENT_FILE_TYPE_PHOTO_HIGH_QUALITY_ENCRYPTED = 7;
    public static final int SENT_FILE_TYPE_VIDEO = 2;
    public static final int SENT_FILE_TYPE_VIDEO_ENCRYPTED = 5;
    public static final int SENT_FILE_TYPE_VIDEO_HIGH_QUALITY = 8;
    private int archiveUnreadCount;
    private int[][] bots;
    private File cacheFile;
    private int[][] channels;
    private int[][] communities;
    private int[][] contacts;
    private SQLiteDatabase database;
    private boolean databaseCreated;
    private boolean databaseMigrationInProgress;
    private final ArrayList<MessagesController.DialogFilter> dialogFilters;
    private final SparseArray<MessagesController.DialogFilter> dialogFiltersMap;
    private final LongSparseIntArray dialogIsForumTyped;
    private LongSparseArray<Integer> dialogsWithMentions;
    private LongSparseArray<Integer> dialogsWithUnread;
    private int[][] groups;
    private int lastDateValue;
    private int lastPtsValue;
    private int lastQtsValue;
    private int lastSavedDate;
    private int lastSavedPts;
    private int lastSavedQts;
    private int lastSavedSeq;
    private int lastSecretVersion;
    private int lastSeqValue;
    private final AtomicLong lastTaskId;
    private int mainUnreadCount;
    private int[] mentionChannels;
    private int[] mentionGroups;
    private int[][] nonContacts;
    private final CountDownLatch openSync;
    private volatile int pendingArchiveUnreadCount;
    private volatile int pendingMainUnreadCount;
    private int secretG;
    private byte[] secretPBytes;
    private File shmCacheFile;
    public boolean showClearDatabaseAlert;
    private DispatchQueue storageQueue;
    private final SparseArray<ArrayList<Runnable>> tasks;
    boolean tryRecover;
    private final LongSparseArray<Boolean> unknownDialogsIds;
    private File walCacheFile;
    private static volatile MessagesStorage[] Instance = new MessagesStorage[16];
    private static final Object[] lockObjects = new Object[16];

    public interface BooleanCallback {
        void run(boolean z);
    }

    public interface IntCallback {
        void run(int i);
    }

    public interface LongCallback {
        void run(long j);
    }

    public interface StringCallback {
        void run(String str);
    }

    static {
        for (int i = 0; i < 16; i++) {
            lockObjects[i] = new Object();
        }
        DATABASE_TABLES = new String[]{"messages_holes", "media_holes_v2", "scheduled_messages_v2", "quick_replies", "messages_v2", "download_queue", "user_contacts_v7", "user_phones_v7", "dialogs", "dialog_filter", "dialog_filter_ep", "dialog_filter_pin_v2", "randoms_v2", "enc_tasks_v4", "messages_seq", "params", "media_v4", "bot_keyboard", "bot_keyboard_topics", "chat_settings_v2", "user_settings", "chat_pinned_v2", "chat_pinned_count", "chat_hints", "botcache", "users_data", "users", "chats", "enc_chats", "channel_users_v2", "channel_admins_v3", "contacts", "dialog_photos", "dialog_settings", "web_recent_v3", "stickers_v2", "stickers_featured", "stickers_dice", "stickersets", "hashtag_recent_v2", "webpage_pending_v2", "sent_files_v2", "search_recent", "media_counts_v2", "keyvalue", "bot_info_v2", "pending_tasks", "requested_holes", "sharing_locations", "shortcut_widget", "emoji_keywords_v2", "emoji_keywords_info_v2", "wallpapers2", "unread_push_messages", "polls_v2", "reactions", "reaction_mentions", "downloading_documents", "animated_emoji", "attach_menu_bots", "premium_promo", "emoji_statuses", "messages_holes_topics", "messages_topics", "saved_dialogs", "media_topics", "media_holes_topics", "topics", "media_counts_topics", "reaction_mentions_topics", "emoji_groups", "poll_votes_mentions", "poll_votes_mentions_topics", "ephemeral_messages"};
    }

    private int resolveFeedMessageId(long j, int i) {
        FeedController feedControllerPeekInstance = FeedController.peekInstance(this.currentAccount);
        return feedControllerPeekInstance == null ? i : feedControllerPeekInstance.resolveRealMessageId(j, i);
    }

    public static MessagesStorage getInstance(int i) {
        MessagesStorage messagesStorage;
        MessagesStorage messagesStorage2 = Instance[i];
        if (messagesStorage2 != null) {
            return messagesStorage2;
        }
        synchronized (lockObjects[i]) {
            try {
                messagesStorage = Instance[i];
                if (messagesStorage == null) {
                    MessagesStorage[] messagesStorageArr = Instance;
                    MessagesStorage messagesStorage3 = new MessagesStorage(i);
                    messagesStorageArr[i] = messagesStorage3;
                    messagesStorage = messagesStorage3;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return messagesStorage;
    }

    private void ensureOpened() {
        try {
            this.openSync.await();
        } catch (Throwable unused) {
        }
    }

    public int getLastDateValue() {
        ensureOpened();
        return this.lastDateValue;
    }

    public void setLastDateValue(int i) {
        ensureOpened();
        this.lastDateValue = i;
    }

    public int getLastPtsValue() {
        ensureOpened();
        return this.lastPtsValue;
    }

    public int getMainUnreadCount() {
        return this.mainUnreadCount;
    }

    public int getArchiveUnreadCount() {
        return this.archiveUnreadCount;
    }

    public void setLastPtsValue(int i) {
        ensureOpened();
        this.lastPtsValue = i;
    }

    public int getLastQtsValue() {
        ensureOpened();
        return this.lastQtsValue;
    }

    public void setLastQtsValue(int i) {
        ensureOpened();
        this.lastQtsValue = i;
    }

    public int getLastSeqValue() {
        ensureOpened();
        return this.lastSeqValue;
    }

    public void setLastSeqValue(int i) {
        ensureOpened();
        this.lastSeqValue = i;
    }

    public int getLastSecretVersion() {
        ensureOpened();
        return this.lastSecretVersion;
    }

    public void setLastSecretVersion(int i) {
        ensureOpened();
        this.lastSecretVersion = i;
    }

    public byte[] getSecretPBytes() {
        ensureOpened();
        return this.secretPBytes;
    }

    public void setSecretPBytes(byte[] bArr) {
        ensureOpened();
        this.secretPBytes = bArr;
    }

    public int getSecretG() {
        ensureOpened();
        return this.secretG;
    }

    public void setSecretG(int i) {
        ensureOpened();
        this.secretG = i;
    }

    public MessagesStorage(int i) {
        super(i);
        this.lastTaskId = new AtomicLong(System.currentTimeMillis());
        this.tasks = new SparseArray<>();
        this.lastDateValue = 0;
        this.lastPtsValue = 0;
        this.lastQtsValue = 0;
        this.lastSeqValue = 0;
        this.lastSecretVersion = 0;
        this.secretPBytes = null;
        this.secretG = 0;
        this.lastSavedSeq = 0;
        this.lastSavedPts = 0;
        this.lastSavedDate = 0;
        this.lastSavedQts = 0;
        this.dialogFilters = new ArrayList<>();
        this.dialogFiltersMap = new SparseArray<>();
        this.unknownDialogsIds = new LongSparseArray<>();
        this.openSync = new CountDownLatch(1);
        this.dialogIsForumTyped = new LongSparseIntArray();
        this.contacts = new int[][]{new int[2], new int[2]};
        this.nonContacts = new int[][]{new int[2], new int[2]};
        this.bots = new int[][]{new int[2], new int[2]};
        this.channels = new int[][]{new int[2], new int[2]};
        this.groups = new int[][]{new int[2], new int[2]};
        this.communities = new int[][]{new int[2], new int[2]};
        this.mentionChannels = new int[2];
        this.mentionGroups = new int[2];
        this.dialogsWithMentions = new LongSparseArray<>();
        this.dialogsWithUnread = new LongSparseArray<>();
        DispatchQueue dispatchQueue = new DispatchQueue("storageQueue_" + i);
        this.storageQueue = dispatchQueue;
        dispatchQueue.setPriority(8);
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$0();
            }
        });
    }

    public void lambda$openDatabase$1() {
        if (this.databaseMigrationInProgress) {
            this.databaseMigrationInProgress = false;
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDatabaseMigration, Boolean.FALSE);
        }
    }

    public void lambda$updateDbToLastVersion$3() {
        this.databaseMigrationInProgress = true;
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onDatabaseMigration, Boolean.TRUE);
    }

    public void lambda$cleanup$6(boolean z) {
        cleanupInternal(true);
        openDatabase(1);
        if (z) {
            Utilities.stageQueue.postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$cleanup$5();
                }
            });
        }
    }

    public void lambda$saveSecretParams$7(int i, int i2, byte[] bArr) {
        try {
            SQLitePreparedStatement sQLitePreparedStatementExecuteFast = this.database.executeFast("UPDATE params SET lsv = ?, sg = ?, pbytes = ? WHERE id = 1");
            sQLitePreparedStatementExecuteFast.bindInteger(1, i);
            sQLitePreparedStatementExecuteFast.bindInteger(2, i2);
            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(bArr != null ? bArr.length : 1);
            if (bArr != null) {
                nativeByteBuffer.writeBytes(bArr);
            }
            sQLitePreparedStatementExecuteFast.bindByteBuffer(3, nativeByteBuffer);
            sQLitePreparedStatementExecuteFast.step();
            sQLitePreparedStatementExecuteFast.dispose();
            nativeByteBuffer.reuse();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void checkSQLException(Throwable th) {
        checkSQLException(th, true);
    }

    private void checkSQLException(Throwable th, boolean z) {
        if ((th instanceof SQLiteException) && th.getMessage() != null && th.getMessage().contains("is malformed") && !this.tryRecover) {
            this.tryRecover = true;
            FileLog.e("disk image malformed detected, try recover");
            if (recoverDatabase()) {
                this.tryRecover = false;
                clearLoadingDialogsOffsets();
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$checkSQLException$8();
                    }
                });
                FileLog.e(new Exception("database restored!!"));
                return;
            }
            FileLog.e(new Exception(th));
            return;
        }
        FileLog.e(th);
    }

    public void lambda$fixNotificationSettings$9() {
        try {
            LongSparseArray longSparseArray = new LongSparseArray();
            Map<String, ?> all = MessagesController.getNotificationsSettings(this.currentAccount).getAll();
            for (Map.Entry<String, ?> entry : all.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith(NotificationsSettingsFacade.PROPERTY_NOTIFY)) {
                    Integer num = (Integer) entry.getValue();
                    if (num.intValue() == 2 || num.intValue() == 3) {
                        String strReplace = key.replace(NotificationsSettingsFacade.PROPERTY_NOTIFY, _UrlKt.FRAGMENT_ENCODE_SET);
                        long jIntValue = 1;
                        if (num.intValue() != 2) {
                            Integer num2 = (Integer) all.get(NotificationsSettingsFacade.PROPERTY_NOTIFY_UNTIL + strReplace);
                            if (num2 != null) {
                                jIntValue = 1 | (((long) num2.intValue()) << 32);
                            }
                        }
                        try {
                            longSparseArray.put(Long.parseLong(strReplace), Long.valueOf(jIntValue));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            try {
                this.database.beginTransaction();
                SQLitePreparedStatement sQLitePreparedStatementExecuteFast = this.database.executeFast("REPLACE INTO dialog_settings VALUES(?, ?)");
                for (int i = 0; i < longSparseArray.size(); i++) {
                    sQLitePreparedStatementExecuteFast.requery();
                    sQLitePreparedStatementExecuteFast.bindLong(1, longSparseArray.keyAt(i));
                    sQLitePreparedStatementExecuteFast.bindLong(2, ((Long) longSparseArray.valueAt(i)).longValue());
                    sQLitePreparedStatementExecuteFast.step();
                }
                sQLitePreparedStatementExecuteFast.dispose();
                this.database.commitTransaction();
            } catch (Exception e2) {
                checkSQLException(e2);
            }
        } catch (Throwable th) {
            checkSQLException(th);
        }
    }

    public long createPendingTask(final NativeByteBuffer nativeByteBuffer) {
        if (nativeByteBuffer == null) {
            return 0L;
        }
        final long andAdd = this.lastTaskId.getAndAdd(1L);
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$createPendingTask$10(andAdd, nativeByteBuffer);
            }
        });
        return andAdd;
    }

    public void lambda$removePendingTask$11(long j) {
        try {
            this.database.executeFast("DELETE FROM pending_tasks WHERE id = " + j).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    private void loadPendingTasks() {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadPendingTasks$33();
            }
        });
    }

    void lambda$loadPendingTasks$12(TLRPC.Chat chat, long j) {
        getMessagesController().loadUnknownChannel(chat, j);
    }

    public void lambda$saveChannelPts$34(int i, long j) {
        try {
            SQLitePreparedStatement sQLitePreparedStatementExecuteFast = this.database.executeFast("UPDATE dialogs SET pts = ? WHERE did = ?");
            sQLitePreparedStatementExecuteFast.bindInteger(1, i);
            sQLitePreparedStatementExecuteFast.bindLong(2, -j);
            sQLitePreparedStatementExecuteFast.step();
            sQLitePreparedStatementExecuteFast.dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void lambda$saveDiffParams$35(int i, int i2, int i3, int i4) {
        try {
            if (this.lastSavedSeq == i && this.lastSavedPts == i2 && this.lastSavedDate == i3 && this.lastQtsValue == i4) {
                return;
            }
            SQLitePreparedStatement sQLitePreparedStatementExecuteFast = this.database.executeFast("UPDATE params SET seq = ?, pts = ?, date = ?, qts = ? WHERE id = 1");
            sQLitePreparedStatementExecuteFast.bindInteger(1, i);
            sQLitePreparedStatementExecuteFast.bindInteger(2, i2);
            sQLitePreparedStatementExecuteFast.bindInteger(3, i3);
            sQLitePreparedStatementExecuteFast.bindInteger(4, i4);
            sQLitePreparedStatementExecuteFast.step();
            sQLitePreparedStatementExecuteFast.dispose();
            this.lastSavedSeq = i;
            this.lastSavedPts = i2;
            this.lastSavedDate = i3;
            this.lastSavedQts = i4;
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void saveDiffParams(final int i, final int i2, final int i3, final int i4) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$saveDiffParams$35(i, i2, i3, i4);
            }
        });
    }

    public void lambda$setDialogFlags$37(long j, long j2) {
        try {
            SQLiteCursor sQLiteCursorQueryFinalized = this.database.queryFinalized("SELECT flags FROM dialog_settings WHERE did = " + j, new Object[0]);
            int iIntValue = sQLiteCursorQueryFinalized.next() ? sQLiteCursorQueryFinalized.intValue(0) : 0;
            sQLiteCursorQueryFinalized.dispose();
            if (j2 == iIntValue) {
                return;
            }
            this.database.executeFast(String.format(Locale.US, "REPLACE INTO dialog_settings VALUES(%d, %d)", Long.valueOf(j), Long.valueOf(j2))).stepThis().dispose();
            resetAllUnreadCounters(true);
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void putStoryPushMessage(final NotificationsController.StoryNotification storyNotification) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$putStoryPushMessage$38(storyNotification);
            }
        });
    }

    public void lambda$deleteStoryPushMessage$39(long j) {
        try {
            this.database.executeFast("DELETE FROM story_pushes WHERE uid = " + j).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void deleteAllStoryPushMessages() {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$deleteAllStoryPushMessages$40();
            }
        });
    }

    public void lambda$deleteAllStoryReactionPushMessages$41() {
        try {
            this.database.executeFast("DELETE FROM unread_push_messages WHERE is_reaction = 2").stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void putPushMessage(final MessageObject messageObject) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$putPushMessage$42(messageObject);
            }
        });
    }

    public void lambda$clearLocalDatabase$43() {
        getMessagesController().getSavedMessagesController().cleanup();
    }

    public void updateRanksInLastMessages(final long j, final long j2, final String str) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$updateRanksInLastMessages$46(j, j2, str);
            }
        });
    }

    void lambda$updateRanksInLastMessages$45(long j, long j2, String str) {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, 0);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updatedChatRanks, Long.valueOf(-j), Long.valueOf(j2), str);
    }

    public void saveTopics(final long j, final List<TLRPC.TL_forumTopic> list, final boolean z, boolean z2, final int i) throws Throwable {
        if (z2) {
            this.storageQueue.postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() throws Throwable {
                    this.f$0.lambda$saveTopics$47(j, list, z, i);
                }
            });
        } else {
            saveTopicsInternal(j, list, z, false, i);
        }
    }

    public void lambda$updateTopicData$48(long j, TLRPC.TL_forumTopic tL_forumTopic, int i) {
        getMessagesController().getTopicsController().updateTopicInUi(j, tL_forumTopic, i);
    }

    public void loadTopics(final long j, final Consumer<ArrayList<TLRPC.TL_forumTopic>> consumer) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$loadTopics$51(j, consumer);
            }
        });
    }

    void lambda$loadTopics$50(ArrayList arrayList, ArrayList arrayList2) {
        if (!arrayList.isEmpty()) {
            getMessagesController().putUsers(arrayList, true);
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        getMessagesController().putChats(arrayList2, true);
    }

    public void loadGroupedMessagesForTopicUpdates(ArrayList<TopicsController.TopicUpdate> arrayList) {
        if (arrayList == null) {
            return;
        }
        try {
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i = 0; i < arrayList.size(); i++) {
                if (!arrayList.get(i).reloadTopic && !arrayList.get(i).onlyCounters && arrayList.get(i).topMessage != null) {
                    long j = arrayList.get(i).topMessage.grouped_id;
                    if (j != 0) {
                        ArrayList arrayList2 = (ArrayList) longSparseArray.get(j);
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList();
                            longSparseArray.put(j, arrayList2);
                        }
                        arrayList2.add(arrayList.get(i));
                    }
                }
            }
            for (int i2 = 0; i2 < longSparseArray.size(); i2++) {
                long jKeyAt = longSparseArray.keyAt(i2);
                ArrayList arrayList3 = (ArrayList) longSparseArray.valueAt(i2);
                SQLiteCursor sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE uid = %s AND group_id = %s ORDER BY date DESC", Long.valueOf(((TopicsController.TopicUpdate) arrayList3.get(0)).dialogId), Long.valueOf(jKeyAt)), new Object[0]);
                ArrayList<MessageObject> arrayList4 = null;
                while (sQLiteCursorQueryFinalized.next()) {
                    NativeByteBuffer nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized.byteBufferValue(0);
                    TLRPC.Message messageTLdeserialize = TLRPC.Message.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false);
                    if (messageTLdeserialize != null) {
                        messageTLdeserialize.readAttachPath(nativeByteBufferByteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                    }
                    if (arrayList4 == null) {
                        arrayList4 = new ArrayList<>();
                    }
                    arrayList4.add(new MessageObject(this.currentAccount, messageTLdeserialize, false, false));
                    nativeByteBufferByteBufferValue.reuse();
                }
                sQLiteCursorQueryFinalized.dispose();
                for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                    ((TopicsController.TopicUpdate) arrayList3.get(i3)).groupedMessages = arrayList4;
                }
            }
        } catch (Throwable th) {
            checkSQLException(th);
        }
    }

    public void loadGroupedMessagesForTopics(long j, ArrayList<TLRPC.TL_forumTopic> arrayList) {
        if (arrayList == null) {
            return;
        }
        try {
            LongSparseArray longSparseArray = new LongSparseArray();
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i).topMessage != null) {
                    long j2 = arrayList.get(i).topMessage.grouped_id;
                    if (j2 != 0) {
                        ArrayList arrayList2 = (ArrayList) longSparseArray.get(j2);
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList();
                            longSparseArray.put(j2, arrayList2);
                        }
                        arrayList2.add(arrayList.get(i));
                    }
                }
            }
            for (int i2 = 0; i2 < longSparseArray.size(); i2++) {
                long jKeyAt = longSparseArray.keyAt(i2);
                ArrayList arrayList3 = (ArrayList) longSparseArray.valueAt(i2);
                SQLiteCursor sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE uid = %s AND group_id = %s ORDER BY date DESC", Long.valueOf(j), Long.valueOf(jKeyAt)), new Object[0]);
                ArrayList<MessageObject> arrayList4 = null;
                while (sQLiteCursorQueryFinalized.next()) {
                    NativeByteBuffer nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized.byteBufferValue(0);
                    TLRPC.Message messageTLdeserialize = TLRPC.Message.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false);
                    if (messageTLdeserialize != null) {
                        messageTLdeserialize.readAttachPath(nativeByteBufferByteBufferValue, UserConfig.getInstance(this.currentAccount).clientUserId);
                    }
                    if (arrayList4 == null) {
                        arrayList4 = new ArrayList<>();
                    }
                    arrayList4.add(new MessageObject(this.currentAccount, messageTLdeserialize, false, false));
                    nativeByteBufferByteBufferValue.reuse();
                }
                sQLiteCursorQueryFinalized.dispose();
                for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                    ((TLRPC.TL_forumTopic) arrayList3.get(i3)).groupedMessages = arrayList4;
                }
            }
        } catch (Throwable th) {
            checkSQLException(th);
        }
    }

    public void getSavedDialogMaxMessageId(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getSavedDialogMaxMessageId$53(j, intCallback);
            }
        });
    }

    public void lambda$deleteSavedDialog$55(long j) throws Throwable {
        Throwable th;
        Exception exc;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                final long clientUserId = getUserConfig().getClientUserId();
                SQLiteCursor sQLiteCursorQueryFinalized = this.database.queryFinalized("SELECT mid FROM messages_topics WHERE uid = ? AND topic_id = ?", Long.valueOf(clientUserId), Long.valueOf(j));
                try {
                    final ArrayList<Integer> arrayList = new ArrayList<>();
                    while (sQLiteCursorQueryFinalized.next()) {
                        arrayList.add(Integer.valueOf(sQLiteCursorQueryFinalized.intValue(0)));
                    }
                    sQLiteCursorQueryFinalized.dispose();
                    sQLiteCursorQueryFinalized = this.database.queryFinalized("SELECT mid, data FROM messages_v2 WHERE uid = ?", Long.valueOf(clientUserId));
                    while (sQLiteCursorQueryFinalized.next()) {
                        int iIntValue = sQLiteCursorQueryFinalized.intValue(0);
                        if (!arrayList.contains(Integer.valueOf(iIntValue))) {
                            NativeByteBuffer nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized.byteBufferValue(1);
                            if (MessageObject.getSavedDialogId(clientUserId, TLRPC.Message.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false)) == j) {
                                arrayList.add(Integer.valueOf(iIntValue));
                            }
                            nativeByteBufferByteBufferValue.reuse();
                        }
                    }
                    sQLiteCursorQueryFinalized.dispose();
                    if (arrayList.isEmpty()) {
                        return;
                    }
                    try {
                        lambda$markMessagesAsDeleted$226(clientUserId, arrayList, true, 0, 0);
                        updateDialogsWithDeletedMessages(clientUserId, -clientUserId, arrayList, null);
                        this = this;
                        AndroidUtilities.runOnUIThread(new Runnable() { 
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$deleteSavedDialog$54(clientUserId, arrayList);
                            }
                        });
                        return;
                    } catch (Exception e) {
                        e = e;
                        this = this;
                    }
                } catch (Exception e2) {
                    exc = e2;
                    sQLiteCursor = sQLiteCursorQueryFinalized;
                } catch (Throwable th2) {
                    th = th2;
                    sQLiteCursor = sQLiteCursorQueryFinalized;
                    if (sQLiteCursor != null) {
                        sQLiteCursor.dispose();
                        throw th;
                    }
                    throw th;
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Throwable th3) {
            th = th3;
        }
        exc = e;
        this.checkSQLException(exc);
        if (sQLiteCursor != null) {
            sQLiteCursor.dispose();
        }
    }

    public void lambda$removeAllTopics$56(long j) {
        try {
            this.database.executeFast(String.format(Locale.US, "DELETE FROM topics WHERE did = %d", Long.valueOf(j))).stepThis().dispose();
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    public void removeTopic(final long j, final long j2) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$removeTopic$57(j, j2);
            }
        });
    }

    public void lambda$removeTopics$58(ArrayList arrayList, long j) {
        try {
            String strJoin = TextUtils.join(", ", arrayList);
            SQLiteDatabase sQLiteDatabase = this.database;
            Locale locale = Locale.US;
            sQLiteDatabase.executeFast(String.format(locale, "DELETE FROM topics WHERE did = %d AND topic_id IN (%s)", Long.valueOf(j), strJoin)).stepThis().dispose();
            try {
                this.database.executeFast(String.format(locale, "DELETE FROM messages_v2 WHERE uid = %d AND mid IN (SELECT mid FROM messages_topics WHERE uid = %d AND topic_id IN (%s))", Long.valueOf(j), Long.valueOf(j), strJoin)).stepThis().dispose();
            } catch (SQLiteException e) {
                e.printStackTrace();
            }
            this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_topics WHERE uid = %d AND topic_id IN (%s)", Long.valueOf(j), strJoin)).stepThis().dispose();
        } catch (SQLiteException e2) {
            e2.printStackTrace();
        }
    }

    public void updateTopicsWithReadMessages(final HashMap<TopicKey, Integer> map) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$updateTopicsWithReadMessages$59(map);
            }
        });
    }

    public void lambda$setDialogTtl$60(int i, long j) {
        try {
            this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET ttl_period = %d WHERE did = %d", Integer.valueOf(i), Long.valueOf(j))).stepThis().dispose();
        } catch (SQLiteException e) {
            checkSQLException(e);
        }
    }

    public ArrayList<File> getDatabaseFiles() {
        ArrayList<File> arrayList = new ArrayList<>();
        arrayList.add(this.cacheFile);
        arrayList.add(this.walCacheFile);
        arrayList.add(this.shmCacheFile);
        return arrayList;
    }

    public void reset() {
        clearDatabaseValues();
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$reset$61();
            }
        });
    }

    public void lambda$fullReset$63() {
        cleanupInternal(true);
        clearLoadingDialogsOffsets();
        openDatabase(1);
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$fullReset$62();
            }
        });
    }

    public void lambda$readAllDialogs$64(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, LongSparseArray longSparseArray) {
        MessagesStorage messagesStorage;
        getMessagesController().putUsers(arrayList, true);
        getMessagesController().putChats(arrayList2, true);
        getMessagesController().putEncryptedChats(arrayList3, true);
        for (int i = 0; i < longSparseArray.size(); i++) {
            long jKeyAt = longSparseArray.keyAt(i);
            ReadDialog readDialog = (ReadDialog) longSparseArray.valueAt(i);
            if (getMessagesController().isForum(jKeyAt)) {
                messagesStorage = this;
            } else {
                messagesStorage = this;
                if (messagesStorage.isForum(jKeyAt, 8) || messagesStorage.getMessagesController().isMonoForumWithManageRights(jKeyAt)) {
                }
                MessagesController messagesController = messagesStorage.getMessagesController();
                int i2 = readDialog.lastMid;
                messagesController.markDialogAsRead(jKeyAt, i2, i2, readDialog.date, false, 0L, readDialog.unreadCount, true, 0);
            }
            messagesStorage.getMessagesController().markAllTopicsAsRead(jKeyAt);
            MessagesController messagesController2 = messagesStorage.getMessagesController();
            int i3 = readDialog.lastMid;
            messagesController2.markDialogAsRead(jKeyAt, i3, i3, readDialog.date, false, 0L, readDialog.unreadCount, true, 0);
        }
    }

    int $r8$lambda$_ng5JFyNeHWBFF89GRgGwxKLEVA(MessagesController.DialogFilter dialogFilter, MessagesController.DialogFilter dialogFilter2) {
        int i = dialogFilter.order;
        int i2 = dialogFilter2.order;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    int m4933$r8$lambda$Z9nhSVW5ENneeNzo5QTxNWoQCw(LongSparseIntArray longSparseIntArray, Long l, Long l2) {
        int i = longSparseIntArray.get(l.longValue());
        int i2 = longSparseIntArray.get(l2.longValue());
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public void lambda$processLoadedFilterPeers$71(TLRPC.messages_Dialogs messages_dialogs, TLRPC.messages_Dialogs messages_dialogs2, ArrayList<TLRPC.User> arrayList, ArrayList<TLRPC.Chat> arrayList2, ArrayList<MessagesController.DialogFilter> arrayList3, SparseArray<MessagesController.DialogFilter> sparseArray, ArrayList<Integer> arrayList4, HashMap<Integer, HashSet<Long>> map, HashSet<Integer> hashSet, Runnable runnable) throws Throwable {
        putUsersAndChats(arrayList, arrayList2, true, false);
        int size = sparseArray.size();
        int i = 0;
        boolean z = false;
        while (i < size) {
            lambda$deleteDialogFilter$72(sparseArray.valueAt(i));
            i++;
            z = true;
        }
        Iterator<Integer> it = hashSet.iterator();
        while (it.hasNext()) {
            MessagesController.DialogFilter dialogFilter = this.dialogFiltersMap.get(it.next().intValue());
            if (dialogFilter != null) {
                dialogFilter.pendingUnreadCount = -1;
            }
        }
        for (Map.Entry<Integer, HashSet<Long>> entry : map.entrySet()) {
            MessagesController.DialogFilter dialogFilter2 = this.dialogFiltersMap.get(entry.getKey().intValue());
            if (dialogFilter2 != null) {
                Iterator<Long> it2 = entry.getValue().iterator();
                while (it2.hasNext()) {
                    dialogFilter2.pinnedDialogs.delete(it2.next().longValue());
                }
                z = true;
            }
        }
        int size2 = arrayList3.size();
        int i2 = 0;
        while (i2 < size2) {
            saveDialogFilterInternal(arrayList3.get(i2), false, true);
            i2++;
            z = true;
        }
        int size3 = this.dialogFilters.size();
        boolean z2 = false;
        for (int i3 = 0; i3 < size3; i3++) {
            MessagesController.DialogFilter dialogFilter3 = this.dialogFilters.get(i3);
            int iIndexOf = arrayList4.indexOf(Integer.valueOf(dialogFilter3.id));
            if (dialogFilter3.order != iIndexOf) {
                dialogFilter3.order = iIndexOf;
                z2 = true;
                z = true;
            }
        }
        if (z2) {
            Collections.sort(this.dialogFilters, new Comparator() { 
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return MessagesStorage.$r8$lambda$z0lzviQ1XWVCHE5oAYDahBnFa7k((MessagesController.DialogFilter) obj, (MessagesController.DialogFilter) obj2);
                }
            });
            saveDialogFiltersOrderInternal();
        }
        int i4 = z ? 1 : 2;
        calcUnreadCounters(true);
        getMessagesController().processLoadedDialogFilters(new ArrayList<>(this.dialogFilters), messages_dialogs, messages_dialogs2, arrayList, arrayList2, null, i4, runnable);
    }

    public static void lambda$saveDialogFilter$74(MessagesController.DialogFilter dialogFilter, boolean z, boolean z2) throws Throwable {
        saveDialogFilterInternal(dialogFilter, z, z2);
        calcUnreadCounters(false);
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$saveDialogFilter$73();
            }
        });
    }

    public void lambda$saveDialogFiltersOrder$75(ArrayList arrayList) {
        this.dialogFilters.clear();
        this.dialogFiltersMap.clear();
        this.dialogFilters.addAll(arrayList);
        for (int i = 0; i < arrayList.size(); i++) {
            ((MessagesController.DialogFilter) arrayList.get(i)).order = i;
            this.dialogFiltersMap.put(((MessagesController.DialogFilter) arrayList.get(i)).id, (MessagesController.DialogFilter) arrayList.get(i));
        }
        saveDialogFiltersOrderInternal();
    }

    public static void addReplyMessages(TLRPC.Message message, LongSparseArray<SparseArray<ArrayList<TLRPC.Message>>> longSparseArray, LongSparseArray<ArrayList<Integer>> longSparseArray2) {
        int i = message.reply_to.reply_to_msg_id;
        long replyToDialogId = (message.flags & TLObject.FLAG_30) != 0 ? message.quick_reply_shortcut_id : MessageObject.getReplyToDialogId(message);
        SparseArray<ArrayList<TLRPC.Message>> sparseArray = longSparseArray.get(replyToDialogId);
        ArrayList<Integer> arrayList = longSparseArray2.get(replyToDialogId);
        if (sparseArray == null) {
            sparseArray = new SparseArray<>();
            longSparseArray.put(replyToDialogId, sparseArray);
        }
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            longSparseArray2.put(replyToDialogId, arrayList);
        }
        ArrayList<TLRPC.Message> arrayList2 = sparseArray.get(message.reply_to.reply_to_msg_id);
        if (arrayList2 == null) {
            arrayList2 = new ArrayList<>();
            sparseArray.put(message.reply_to.reply_to_msg_id, arrayList2);
            if (!arrayList.contains(Integer.valueOf(message.reply_to.reply_to_msg_id))) {
                arrayList.add(Integer.valueOf(message.reply_to.reply_to_msg_id));
            }
        }
        arrayList2.add(message);
    }

    throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.loadReplyMessages(androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, java.util.ArrayList, java.util.ArrayList, int):void");
    }

    public void loadUnreadMessages() {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$loadUnreadMessages$77();
            }
        });
    }

    void lambda$loadUnreadMessages$76(LongSparseArray longSparseArray, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, ArrayList arrayList5, HashMap map) {
        getNotificationsController().processLoadedUnreadMessages(longSparseArray, arrayList, arrayList2, arrayList3, arrayList4, arrayList5, map.values());
    }

    public void putWallpapers(final ArrayList<TLRPC.WallPaper> arrayList, final int i) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$putWallpapers$78(i, arrayList);
            }
        });
    }

    void lambda$deleteWallpaper$79(long j) {
        try {
            this.database.executeFast("DELETE FROM wallpapers2 WHERE uid = " + j).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void getWallpapers() {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getWallpapers$81();
            }
        });
    }

    public void lambda$addRecentLocalFile$82(TLRPC.Document document, String str, String str2) {
        SQLiteDatabase sQLiteDatabase = this.database;
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = null;
        try {
            if (document != null) {
                sQLitePreparedStatementExecuteFast = sQLiteDatabase.executeFast("UPDATE web_recent_v3 SET document = ? WHERE image_url = ?");
                sQLitePreparedStatementExecuteFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(document.getObjectSize());
                document.serializeToStream(nativeByteBuffer);
                sQLitePreparedStatementExecuteFast.bindByteBuffer(1, nativeByteBuffer);
                sQLitePreparedStatementExecuteFast.bindString(2, str);
                sQLitePreparedStatementExecuteFast.step();
                sQLitePreparedStatementExecuteFast.dispose();
                nativeByteBuffer.reuse();
            } else {
                sQLitePreparedStatementExecuteFast = sQLiteDatabase.executeFast("UPDATE web_recent_v3 SET local_url = ? WHERE image_url = ?");
                sQLitePreparedStatementExecuteFast.requery();
                sQLitePreparedStatementExecuteFast.bindString(1, str2);
                sQLitePreparedStatementExecuteFast.bindString(2, str);
                sQLitePreparedStatementExecuteFast.step();
                sQLitePreparedStatementExecuteFast.dispose();
            }
            sQLitePreparedStatementExecuteFast.dispose();
        } catch (Exception e) {
            checkSQLException(e);
        } finally {
            if (sQLitePreparedStatementExecuteFast != null) {
                sQLitePreparedStatementExecuteFast.dispose();
            }
        }
    }

    public void deleteAllReactionsFromChat(final long j, final long j2, final int i) {
        executeInStorageQueue(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$deleteAllReactionsFromChat$84(i, j, j2);
            }
        });
    }

    void lambda$deleteAllReactionsFromChat$83(SparseArray sparseArray, long j) {
        if (sparseArray.size() != 0) {
            int iMin = Math.min(sparseArray.size(), 100);
            for (int i = 0; i < iMin; i++) {
                int iKeyAt = sparseArray.keyAt(i);
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didUpdateReactions, Long.valueOf(j), Integer.valueOf(iKeyAt), (TLRPC.TL_messageReactions) sparseArray.valueAt(i));
            }
        }
    }

    public void deleteUserChatHistory(final long j, final long j2) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$deleteUserChatHistory$87(j, j2);
            }
        });
    }

    void lambda$deleteUserChatHistory$85(ArrayList arrayList, long j, ArrayList arrayList2) {
        getFileLoader().cancelLoadFiles(arrayList);
        getMessagesController().markDialogMessageAsDeleted(j, arrayList2);
    }

    public public void lambda$deleteDialog$88(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    public void lambda$onDeleteQueryComplete$91(long j) {
        try {
            this.database.executeFast("DELETE FROM media_counts_v2 WHERE uid = " + j).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void clearUserPhotos(final long j) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$clearUserPhotos$92(j);
            }
        });
    }

    public void lambda$clearUserPhoto$93(long j, long j2) {
        try {
            this.database.executeFast("DELETE FROM dialog_photos WHERE uid = " + j + " AND id = " + j2).stepThis().dispose();
            this.database.executeFast("UPDATE dialog_photos_count SET count = count - 1 WHERE uid = " + j + " AND count > 0").stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void resetDialogs(final TLRPC.messages_Dialogs messages_dialogs, final int i, final int i2, final int i3, final int i4, final int i5, final LongSparseArray<TLRPC.Dialog> longSparseArray, final LongSparseArray<ArrayList<MessageObject>> longSparseArray2, final TLRPC.Message message, final int i6) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$resetDialogs$95(messages_dialogs, i6, i2, i3, i4, i5, message, i, longSparseArray, longSparseArray2);
            }
        });
    }

    int m4958$r8$lambda$nTdaU5Wn9V4xKyrW38hNDE5z7A(LongSparseIntArray longSparseIntArray, Long l, Long l2) {
        int i = longSparseIntArray.get(l.longValue());
        int i2 = longSparseIntArray.get(l2.longValue());
        if (i < i2) {
            return 1;
        }
        return i > i2 ? -1 : 0;
    }

    public void emptyMessagesMedia(final long j, final ArrayList<Integer> arrayList) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$emptyMessagesMedia$99(arrayList, j);
            }
        });
    }

    void lambda$emptyMessagesMedia$96(ArrayList arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateMessageMedia, arrayList.get(i));
        }
    }

    public void lambda$onReactionsUpdate$105(ArrayList arrayList) {
        HashSet<Long> hashSet = new HashSet<>();
        LongSparseArray longSparseArray = new LongSparseArray();
        LongSparseArray longSparseArray2 = new LongSparseArray();
        boolean z = false;
        for (int i = 0; i < arrayList.size(); i++) {
            SavedReactionsUpdate savedReactionsUpdate = (SavedReactionsUpdate) arrayList.get(i);
            TLRPC.TL_messageReactions tL_messageReactions = savedReactionsUpdate.old;
            TLRPC.TL_messageReactions tL_messageReactions2 = savedReactionsUpdate.last;
            longSparseArray.clear();
            longSparseArray2.clear();
            if (tL_messageReactions != null && tL_messageReactions.results != null && tL_messageReactions.reactions_as_tags) {
                for (int i2 = 0; i2 < tL_messageReactions.results.size(); i2++) {
                    ReactionsLayoutInBubble.VisibleReaction visibleReactionFromTL = ReactionsLayoutInBubble.VisibleReaction.fromTL(tL_messageReactions.results.get(i2).reaction);
                    if (visibleReactionFromTL != null) {
                        longSparseArray.put(visibleReactionFromTL.hash, visibleReactionFromTL);
                    }
                }
            }
            if (tL_messageReactions2 != null && tL_messageReactions2.results != null && tL_messageReactions2.reactions_as_tags) {
                for (int i3 = 0; i3 < tL_messageReactions2.results.size(); i3++) {
                    ReactionsLayoutInBubble.VisibleReaction visibleReactionFromTL2 = ReactionsLayoutInBubble.VisibleReaction.fromTL(tL_messageReactions2.results.get(i3).reaction);
                    if (visibleReactionFromTL2 != null) {
                        longSparseArray2.put(visibleReactionFromTL2.hash, visibleReactionFromTL2);
                    }
                }
            }
            for (int i4 = 0; i4 < longSparseArray.size(); i4++) {
                long jKeyAt = longSparseArray.keyAt(i4);
                ReactionsLayoutInBubble.VisibleReaction visibleReaction = (ReactionsLayoutInBubble.VisibleReaction) longSparseArray.valueAt(i4);
                if (!longSparseArray2.containsKey(jKeyAt) && getMessagesController().updateSavedReactionTags(savedReactionsUpdate.topic_id, visibleReaction, false, false)) {
                    hashSet.add(Long.valueOf(savedReactionsUpdate.topic_id));
                    z = true;
                }
            }
            for (int i5 = 0; i5 < longSparseArray2.size(); i5++) {
                long jKeyAt2 = longSparseArray2.keyAt(i5);
                ReactionsLayoutInBubble.VisibleReaction visibleReaction2 = (ReactionsLayoutInBubble.VisibleReaction) longSparseArray2.valueAt(i5);
                if (!longSparseArray.containsKey(jKeyAt2) && getMessagesController().updateSavedReactionTags(savedReactionsUpdate.topic_id, visibleReaction2, true, false)) {
                    hashSet.add(Long.valueOf(savedReactionsUpdate.topic_id));
                    z = true;
                }
            }
        }
        if (!z || hashSet.isEmpty()) {
            return;
        }
        getMessagesController().updateSavedReactionTags(hashSet);
    }

    private void onReactionsUpdate(final long j, final TLRPC.TL_messageReactions tL_messageReactions, final TLRPC.TL_messageReactions tL_messageReactions2) {
        ArrayList<TLRPC.ReactionCount> arrayList;
        if (tL_messageReactions == null || (arrayList = tL_messageReactions.results) == null) {
            return;
        }
        if (arrayList == null || !arrayList.isEmpty() || tL_messageReactions2 == null || !tL_messageReactions2.results.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onReactionsUpdate$106(tL_messageReactions, tL_messageReactions2, j);
                }
            });
        }
    }

    public void lambda$updateMessageVoiceTranscriptionOpen$107(int i, long j, TLRPC.Message message) throws Throwable {
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                TLRPC.Message messageWithCustomParamsOnlyInternal = getMessageWithCustomParamsOnlyInternal(i, j);
                messageWithCustomParamsOnlyInternal.voiceTranscriptionOpen = message.voiceTranscriptionOpen;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionRated = message.voiceTranscriptionRated;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionFinal = message.voiceTranscriptionFinal;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionForce = message.voiceTranscriptionForce;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionId = message.voiceTranscriptionId;
                for (int i2 = 0; i2 < 2; i2++) {
                    SQLiteDatabase sQLiteDatabase = this.database;
                    if (i2 == 0) {
                        sQLitePreparedStatementExecuteFast = sQLiteDatabase.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
                    } else {
                        sQLitePreparedStatementExecuteFast = sQLiteDatabase.executeFast("UPDATE messages_topics SET custom_params = ? WHERE mid = ? AND uid = ?");
                    }
                    try {
                        sQLitePreparedStatementExecuteFast.requery();
                        NativeByteBuffer nativeByteBufferWriteLocalParams = MessageCustomParamsHelper.writeLocalParams(messageWithCustomParamsOnlyInternal);
                        if (nativeByteBufferWriteLocalParams != null) {
                            sQLitePreparedStatementExecuteFast.bindByteBuffer(1, nativeByteBufferWriteLocalParams);
                        } else {
                            sQLitePreparedStatementExecuteFast.bindNull(1);
                        }
                        sQLitePreparedStatementExecuteFast.bindInteger(2, i);
                        sQLitePreparedStatementExecuteFast.bindLong(3, j);
                        sQLitePreparedStatementExecuteFast.step();
                        sQLitePreparedStatementExecuteFast.dispose();
                        if (nativeByteBufferWriteLocalParams != null) {
                            nativeByteBufferWriteLocalParams.reuse();
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLitePreparedStatement = sQLitePreparedStatementExecuteFast;
                        checkSQLException(e);
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        if (sQLiteDatabase2 != null) {
                            sQLiteDatabase2.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                            return;
                        }
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLitePreparedStatement = sQLitePreparedStatementExecuteFast;
                        SQLiteDatabase sQLiteDatabase3 = this.database;
                        if (sQLiteDatabase3 != null) {
                            sQLiteDatabase3.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                }
                this.database.commitTransaction();
                SQLiteDatabase sQLiteDatabase4 = this.database;
                if (sQLiteDatabase4 != null) {
                    sQLiteDatabase4.commitTransaction();
                }
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void updateMessageVoiceTranscription(final long j, int i, final String str, final long j2, final boolean z) {
        final int iResolveFeedMessageId = resolveFeedMessageId(j, i);
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$updateMessageVoiceTranscription$108(iResolveFeedMessageId, j, z, j2, str);
            }
        });
    }

    public void lambda$updateMessageVoiceTranscription$109(int i, long j, TLRPC.Message message, String str) throws Throwable {
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                TLRPC.Message messageWithCustomParamsOnlyInternal = getMessageWithCustomParamsOnlyInternal(i, j);
                messageWithCustomParamsOnlyInternal.voiceTranscriptionOpen = message.voiceTranscriptionOpen;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionRated = message.voiceTranscriptionRated;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionFinal = message.voiceTranscriptionFinal;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionForce = message.voiceTranscriptionForce;
                messageWithCustomParamsOnlyInternal.voiceTranscriptionId = message.voiceTranscriptionId;
                messageWithCustomParamsOnlyInternal.voiceTranscription = str;
                for (int i2 = 0; i2 < 2; i2++) {
                    SQLiteDatabase sQLiteDatabase = this.database;
                    if (i2 == 0) {
                        sQLitePreparedStatementExecuteFast = sQLiteDatabase.executeFast("UPDATE messages_v2 SET custom_params = ? WHERE mid = ? AND uid = ?");
                    } else {
                        sQLitePreparedStatementExecuteFast = sQLiteDatabase.executeFast("UPDATE messages_topics SET custom_params = ? WHERE mid = ? AND uid = ?");
                    }
                    try {
                        sQLitePreparedStatementExecuteFast.requery();
                        NativeByteBuffer nativeByteBufferWriteLocalParams = MessageCustomParamsHelper.writeLocalParams(messageWithCustomParamsOnlyInternal);
                        if (nativeByteBufferWriteLocalParams != null) {
                            sQLitePreparedStatementExecuteFast.bindByteBuffer(1, nativeByteBufferWriteLocalParams);
                        } else {
                            sQLitePreparedStatementExecuteFast.bindNull(1);
                        }
                        sQLitePreparedStatementExecuteFast.bindInteger(2, i);
                        sQLitePreparedStatementExecuteFast.bindLong(3, j);
                        sQLitePreparedStatementExecuteFast.step();
                        sQLitePreparedStatementExecuteFast.dispose();
                        this.database.commitTransaction();
                        if (nativeByteBufferWriteLocalParams != null) {
                            nativeByteBufferWriteLocalParams.reuse();
                        }
                    } catch (Exception e) {
                        e = e;
                        sQLitePreparedStatement = sQLitePreparedStatementExecuteFast;
                        checkSQLException(e);
                        SQLiteDatabase sQLiteDatabase2 = this.database;
                        if (sQLiteDatabase2 != null) {
                            sQLiteDatabase2.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                            return;
                        }
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLitePreparedStatement = sQLitePreparedStatementExecuteFast;
                        SQLiteDatabase sQLiteDatabase3 = this.database;
                        if (sQLiteDatabase3 != null) {
                            sQLiteDatabase3.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                }
                SQLiteDatabase sQLiteDatabase4 = this.database;
                if (sQLiteDatabase4 != null) {
                    sQLiteDatabase4.commitTransaction();
                }
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void updateMessageCustomParams(long j, TLRPC.Message message) {
        updateMessageCustomParams(j, message, message.id);
    }

    public void updateMessageCustomParams(final long j, final TLRPC.Message message, int i) {
        final int iResolveFeedMessageId = resolveFeedMessageId(j, i);
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$updateMessageCustomParams$110(iResolveFeedMessageId, j, message);
            }
        });
    }

    public void lambda$clearAllMessageCustomParams$111() {
        try {
            this.database.executeFast("UPDATE messages_v2 SET custom_params = NULL").stepThis().dispose();
            this.database.executeFast("UPDATE messages_topics SET custom_params = NULL").stepThis().dispose();
            this.database.executeFast("UPDATE stories SET custom_params = NULL").stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public TLRPC.Message getMessageWithCustomParamsOnlyInternal(int i, long j) {
        boolean z;
        TLRPC.TL_message tL_message = new TLRPC.TL_message();
        SQLiteCursor sQLiteCursorQueryFinalized = null;
        try {
            try {
                SQLiteCursor sQLiteCursorQueryFinalized2 = this.database.queryFinalized("SELECT custom_params FROM messages_v2 WHERE mid = ? AND uid = ?", Integer.valueOf(i), Long.valueOf(j));
                try {
                    if (sQLiteCursorQueryFinalized2.next()) {
                        NativeByteBuffer nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized2.byteBufferValue(0);
                        MessageCustomParamsHelper.readLocalParams(tL_message, nativeByteBufferByteBufferValue);
                        if (nativeByteBufferByteBufferValue != null) {
                            nativeByteBufferByteBufferValue.reuse();
                        }
                        z = true;
                    } else {
                        z = false;
                    }
                    sQLiteCursorQueryFinalized2.dispose();
                    if (!z) {
                        sQLiteCursorQueryFinalized = this.database.queryFinalized("SELECT custom_params FROM messages_topics WHERE mid = ? AND uid = ?", Integer.valueOf(i), Long.valueOf(j));
                        if (sQLiteCursorQueryFinalized.next()) {
                            NativeByteBuffer nativeByteBufferByteBufferValue2 = sQLiteCursorQueryFinalized.byteBufferValue(0);
                            MessageCustomParamsHelper.readLocalParams(tL_message, nativeByteBufferByteBufferValue2);
                            if (nativeByteBufferByteBufferValue2 != null) {
                                nativeByteBufferByteBufferValue2.reuse();
                            }
                        }
                        sQLiteCursorQueryFinalized.dispose();
                        return tL_message;
                    }
                } catch (SQLiteException e) {
                    e = e;
                    sQLiteCursorQueryFinalized = sQLiteCursorQueryFinalized2;
                    checkSQLException(e);
                    if (sQLiteCursorQueryFinalized != null) {
                        sQLiteCursorQueryFinalized.dispose();
                    }
                } catch (Throwable th) {
                    th = th;
                    sQLiteCursorQueryFinalized = sQLiteCursorQueryFinalized2;
                    if (sQLiteCursorQueryFinalized != null) {
                        sQLiteCursorQueryFinalized.dispose();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (SQLiteException e2) {
            e = e2;
        }
        return tL_message;
    }

    public void getNewTask(final LongSparseArray<ArrayList<Integer>> longSparseArray, final LongSparseArray<ArrayList<Integer>> longSparseArray2) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$getNewTask$112(longSparseArray, longSparseArray2);
            }
        });
    }

    void lambda$markMentionMessageAsRead$113(int i, long j, long j2) throws Throwable {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                SQLiteDatabase sQLiteDatabase = this.database;
                Locale locale = Locale.US;
                sQLiteDatabase.executeFast(String.format(locale, "UPDATE messages_v2 SET read_state = read_state | 2 WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j))).stepThis().dispose();
                SQLiteCursor sQLiteCursorQueryFinalized = this.database.queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + j2, new Object[0]);
                try {
                    int iMax = sQLiteCursorQueryFinalized.next() ? Math.max(0, sQLiteCursorQueryFinalized.intValue(0) - 1) : 0;
                    sQLiteCursorQueryFinalized.dispose();
                    this.database.executeFast(String.format(locale, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", Integer.valueOf(iMax), Long.valueOf(j2))).stepThis().dispose();
                    LongSparseIntArray longSparseIntArray = new LongSparseIntArray(1);
                    longSparseIntArray.put(j2, iMax);
                    if (iMax == 0) {
                        updateFiltersReadCounter(null, longSparseIntArray, true);
                    }
                    getMessagesController().processDialogsUpdateRead(null, longSparseIntArray);
                    this.database.executeFast(String.format(locale, "UPDATE messages_topics SET read_state = read_state | 2 WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j))).stepThis().dispose();
                    sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(locale, "SELECT data FROM messages_topics WHERE mid = %d AND uid = %d", Integer.valueOf(i), Long.valueOf(j)), new Object[0]);
                    long topicId = 0;
                    while (sQLiteCursorQueryFinalized.next()) {
                        NativeByteBuffer nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized.byteBufferValue(0);
                        if (nativeByteBufferByteBufferValue != null) {
                            TLRPC.Message messageTLdeserialize = TLRPC.Message.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false);
                            nativeByteBufferByteBufferValue.reuse();
                            topicId = MessageObject.getTopicId(this.currentAccount, messageTLdeserialize, getForumTypeFlags(j));
                        }
                    }
                    sQLiteCursorQueryFinalized.dispose();
                    if (topicId == 0) {
                        return;
                    }
                    SQLiteDatabase sQLiteDatabase2 = this.database;
                    Locale locale2 = Locale.US;
                    SQLiteCursor sQLiteCursorQueryFinalized2 = sQLiteDatabase2.queryFinalized(String.format(locale2, "SELECT unread_mentions FROM topics WHERE did = %d AND topic_id = %d", Long.valueOf(j2), Long.valueOf(topicId)), new Object[0]);
                    try {
                        int iMax2 = sQLiteCursorQueryFinalized2.next() ? Math.max(0, sQLiteCursorQueryFinalized2.intValue(0) - 1) : 0;
                        sQLiteCursorQueryFinalized2.dispose();
                        this.database.executeFast(String.format(locale2, "UPDATE topics SET unread_mentions = %d WHERE did = %d AND topic_id = %d", Integer.valueOf(iMax2), Long.valueOf(j), Long.valueOf(topicId))).stepThis().dispose();
                        getMessagesController().getTopicsController().updateMentionsUnread(j, topicId, iMax2);
                        return;
                    } catch (Exception e) {
                        e = e;
                        sQLiteCursor = sQLiteCursorQueryFinalized2;
                    } catch (Throwable th) {
                        th = th;
                        sQLiteCursor = sQLiteCursorQueryFinalized2;
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        throw th;
                    }
                } catch (Exception e2) {
                    e = e2;
                    sQLiteCursor = sQLiteCursorQueryFinalized;
                } catch (Throwable th2) {
                    th = th2;
                    sQLiteCursor = sQLiteCursorQueryFinalized;
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Throwable th3) {
            th = th3;
        }
        checkSQLException(e);
        if (sQLiteCursor != null) {
            sQLiteCursor.dispose();
        }
    }

    public void markMessageAsMention(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$markMessageAsMention$114(i, j);
            }
        });
    }

    public void lambda$resetMentionsCount$115(long j, long j2, int i) throws Throwable {
        SQLiteDatabase sQLiteDatabase = this.database;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                if (j == 0) {
                    SQLiteCursor sQLiteCursorQueryFinalized = sQLiteDatabase.queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + j2, new Object[0]);
                    try {
                        int iIntValue = sQLiteCursorQueryFinalized.next() ? sQLiteCursorQueryFinalized.intValue(0) : 0;
                        sQLiteCursorQueryFinalized.dispose();
                        if (iIntValue == 0 && i == 0) {
                            return;
                        }
                        if (i == 0) {
                            this.database.executeFast(String.format(Locale.US, "UPDATE messages_v2 SET read_state = read_state | 2 WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", Long.valueOf(j2))).stepThis().dispose();
                        }
                        this.database.executeFast(String.format(Locale.US, "UPDATE dialogs SET unread_count_i = %d WHERE did = %d", Integer.valueOf(i), Long.valueOf(j2))).stepThis().dispose();
                        LongSparseIntArray longSparseIntArray = new LongSparseIntArray(1);
                        longSparseIntArray.put(j2, i);
                        getMessagesController().processDialogsUpdateRead(null, longSparseIntArray);
                        if (i == 0) {
                            updateFiltersReadCounter(null, longSparseIntArray, true);
                            return;
                        }
                        return;
                    } catch (Exception e) {
                        e = e;
                        sQLiteCursor = sQLiteCursorQueryFinalized;
                        checkSQLException(e);
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                            return;
                        }
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLiteCursor = sQLiteCursorQueryFinalized;
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        throw th;
                    }
                }
                sQLiteDatabase.executeFast(String.format(Locale.US, "UPDATE topics SET unread_mentions = %d WHERE did = %d AND topic_id = %d", Integer.valueOf(i), Long.valueOf(j2), Long.valueOf(j))).stepThis().dispose();
                TopicsController.TopicUpdate topicUpdate = new TopicsController.TopicUpdate();
                topicUpdate.dialogId = j2;
                topicUpdate.topicId = j;
                topicUpdate.onlyCounters = true;
                topicUpdate.unreadMentions = i;
                topicUpdate.unreadCount = -1;
                getMessagesController().getTopicsController().processUpdate(Collections.singletonList(topicUpdate));
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void createTaskForMid(final long j, final int i, final int i2, final int i3, final int i4, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$createTaskForMid$117(i2, i3, i4, i, z, j);
            }
        });
    }

    public void lambda$createTaskForSecretChat$118(long j, ArrayList arrayList) {
        markMessagesContentAsRead(j, arrayList, 0, 0);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesReadContent, Long.valueOf(j), arrayList);
    }

    void lambda$updateFiltersReadCounter$120() {
        ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).unreadCount = arrayList.get(i).pendingUnreadCount;
        }
        this.mainUnreadCount = this.pendingMainUnreadCount;
        this.archiveUnreadCount = this.pendingArchiveUnreadCount;
    }

    private boolean isUserCollapsedInCommunity(LongSparseArray<TLRPC.Chat> longSparseArray, TLRPC.User user) {
        long j = user.linked_community_id;
        if (j != 0) {
            TLRPC.Chat chat = longSparseArray.get(j);
            if (chat == null) {
                chat = getChat(user.linked_community_id);
                longSparseArray.put(user.linked_community_id, chat);
            }
            if (chat != null && chat.collapsed_in_dialogs) {
                return true;
            }
        }
        return false;
    }

    private boolean isChatCollapsedInCommunity(LongSparseArray<TLRPC.Chat> longSparseArray, TLRPC.Chat chat) {
        long j = chat.linked_community_id;
        if (j != 0) {
            TLRPC.Chat chat2 = longSparseArray.get(j);
            if (chat2 == null) {
                chat2 = getChat(chat.linked_community_id);
                longSparseArray.put(chat.linked_community_id, chat2);
            }
            if (chat2 != null && chat2.collapsed_in_dialogs) {
                return true;
            }
        }
        return false;
    }

    private void updateDialogsWithReadMessagesInternal(ArrayList<Integer> arrayList, LongSparseIntArray longSparseIntArray, LongSparseIntArray longSparseIntArray2, LongSparseArray<ArrayList<Integer>> longSparseArray, LongSparseIntArray longSparseIntArray3) {
        boolean z;
        int i;
        int i2;
        int i3;
        int i4;
        LongSparseIntArray longSparseIntArray4 = longSparseIntArray;
        try {
            LongSparseIntArray longSparseIntArray5 = new LongSparseIntArray();
            LongSparseIntArray longSparseIntArray6 = new LongSparseIntArray();
            ArrayList<Long> arrayList2 = new ArrayList<>();
            int i5 = 0;
            if (!isEmpty(arrayList)) {
                SQLiteCursor sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, read_state, out FROM messages_v2 WHERE mid IN(%s) AND is_channel = 0", TextUtils.join(",", arrayList)), new Object[0]);
                while (sQLiteCursorQueryFinalized.next()) {
                    if (sQLiteCursorQueryFinalized.intValue(2) == 0 && sQLiteCursorQueryFinalized.intValue(1) == 0) {
                        long jLongValue = sQLiteCursorQueryFinalized.longValue(0);
                        int i6 = longSparseIntArray5.get(jLongValue);
                        if (i6 == 0) {
                            longSparseIntArray5.put(jLongValue, 1);
                        } else {
                            longSparseIntArray5.put(jLongValue, i6 + 1);
                        }
                    }
                }
                sQLiteCursorQueryFinalized.dispose();
            } else {
                if (!isEmpty(longSparseIntArray4)) {
                    int i7 = 0;
                    while (i7 < longSparseIntArray4.size()) {
                        long jKeyAt = longSparseIntArray4.keyAt(i7);
                        int i8 = longSparseIntArray4.get(jKeyAt);
                        int i9 = longSparseIntArray3 == null ? -2 : longSparseIntArray3.get(jKeyAt, -2);
                        if (i9 >= 0) {
                            longSparseIntArray5.put(jKeyAt, i9);
                            if (BuildVars.DEBUG_VERSION) {
                                FileLog.d(jKeyAt + " update unread messages count by still unread " + i9);
                            }
                        } else {
                            if (longSparseIntArray3 == null || i9 == -2) {
                                z = true;
                            } else {
                                SQLiteCursor sQLiteCursorQueryFinalized2 = this.database.queryFinalized(String.format(Locale.US, "SELECT start, end FROM messages_holes WHERE uid = %d AND end > %d", Long.valueOf(jKeyAt), Integer.valueOf(i8)), new Object[0]);
                                z = true;
                                while (sQLiteCursorQueryFinalized2.next()) {
                                    z = false;
                                }
                                sQLiteCursorQueryFinalized2.dispose();
                            }
                            if (z) {
                                SQLiteCursor sQLiteCursorQueryFinalized3 = this.database.queryFinalized(String.format(Locale.US, "SELECT COUNT(mid) FROM messages_v2 WHERE uid = %d AND mid > %d AND read_state IN(0,2) AND out = 0", Long.valueOf(jKeyAt), Integer.valueOf(i8)), new Object[0]);
                                if (sQLiteCursorQueryFinalized3.next()) {
                                    int iIntValue = sQLiteCursorQueryFinalized3.intValue(0);
                                    longSparseIntArray5.put(jKeyAt, iIntValue);
                                    if (BuildVars.DEBUG_VERSION) {
                                        FileLog.d(jKeyAt + " update unread messages count " + iIntValue);
                                    }
                                } else if (BuildVars.DEBUG_VERSION) {
                                    FileLog.d(jKeyAt + " can't update unread messages count cursor trouble");
                                }
                                sQLiteCursorQueryFinalized3.dispose();
                            } else if (BuildVars.DEBUG_VERSION) {
                                FileLog.d(jKeyAt + " can't update unread messages count");
                            }
                        }
                        SQLiteCursor sQLiteCursorQueryFinalized4 = this.database.queryFinalized("SELECT inbox_max FROM dialogs WHERE did = " + jKeyAt, new Object[0]);
                        int iIntValue2 = sQLiteCursorQueryFinalized4.next() ? sQLiteCursorQueryFinalized4.intValue(0) : 0;
                        sQLiteCursorQueryFinalized4.dispose();
                        FileLog.d(jKeyAt + " set inbox max " + i8);
                        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = this.database.executeFast("UPDATE dialogs SET inbox_max = max((SELECT inbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                        sQLitePreparedStatementExecuteFast.requery();
                        sQLitePreparedStatementExecuteFast.bindLong(1, jKeyAt);
                        sQLitePreparedStatementExecuteFast.bindInteger(2, i8);
                        sQLitePreparedStatementExecuteFast.bindLong(3, jKeyAt);
                        sQLitePreparedStatementExecuteFast.step();
                        sQLitePreparedStatementExecuteFast.dispose();
                        if (isForum(jKeyAt, 14)) {
                            i = 0;
                            updateTopicsWithReadFromAllInternal(jKeyAt, iIntValue2, i8);
                        } else {
                            i = 0;
                        }
                        i7++;
                        i5 = i;
                        longSparseIntArray4 = longSparseIntArray;
                    }
                }
                int i10 = i5;
                int i11 = 3;
                if (!isEmpty(longSparseArray)) {
                    int size = longSparseArray.size();
                    int i12 = i10;
                    while (i12 < size) {
                        ArrayList<Integer> arrayListValueAt = longSparseArray.valueAt(i12);
                        ArrayList arrayList3 = new ArrayList(arrayListValueAt);
                        SQLiteCursor sQLiteCursorQueryFinalized5 = this.database.queryFinalized(String.format(Locale.US, "SELECT uid, read_state, out, mention, mid, is_channel FROM messages_v2 WHERE mid IN(%s)", TextUtils.join(",", arrayListValueAt)), new Object[i10]);
                        long jLongValue2 = 0;
                        while (sQLiteCursorQueryFinalized5.next()) {
                            int i13 = size;
                            int i14 = i12;
                            long jLongValue3 = sQLiteCursorQueryFinalized5.longValue(i10);
                            arrayList3.remove(Integer.valueOf(sQLiteCursorQueryFinalized5.intValue(4)));
                            if (sQLiteCursorQueryFinalized5.intValue(1) < 2 && sQLiteCursorQueryFinalized5.intValue(2) == 0 && sQLiteCursorQueryFinalized5.intValue(i11) == 1) {
                                int i15 = longSparseIntArray6.get(jLongValue3, -1);
                                if (i15 < 0) {
                                    SQLiteCursor sQLiteCursorQueryFinalized6 = this.database.queryFinalized("SELECT unread_count_i FROM dialogs WHERE did = " + jLongValue3, new Object[0]);
                                    int iIntValue3 = sQLiteCursorQueryFinalized6.next() ? sQLiteCursorQueryFinalized6.intValue(0) : 0;
                                    sQLiteCursorQueryFinalized6.dispose();
                                    longSparseIntArray6.put(jLongValue3, Math.max(0, iIntValue3 - 1));
                                } else {
                                    longSparseIntArray6.put(jLongValue3, Math.max(0, i15 - 1));
                                }
                            }
                            jLongValue2 = sQLiteCursorQueryFinalized5.longValue(5);
                            size = i13;
                            i12 = i14;
                            i10 = 0;
                            i11 = 3;
                        }
                        int i16 = size;
                        int i17 = i12;
                        sQLiteCursorQueryFinalized5.dispose();
                        if (!arrayList3.isEmpty() && jLongValue2 != 0 && !arrayList2.contains(Long.valueOf(jLongValue2))) {
                            arrayList2.add(Long.valueOf(jLongValue2));
                        }
                        i12 = i17 + 1;
                        size = i16;
                        i10 = 0;
                        i11 = 3;
                    }
                }
                if (!isEmpty(longSparseIntArray2)) {
                    for (int i18 = 0; i18 < longSparseIntArray2.size(); i18++) {
                        long jKeyAt2 = longSparseIntArray2.keyAt(i18);
                        int i19 = longSparseIntArray2.get(jKeyAt2);
                        SQLitePreparedStatement sQLitePreparedStatementExecuteFast2 = this.database.executeFast("UPDATE dialogs SET outbox_max = max((SELECT outbox_max FROM dialogs WHERE did = ?), ?) WHERE did = ?");
                        sQLitePreparedStatementExecuteFast2.requery();
                        sQLitePreparedStatementExecuteFast2.bindLong(1, jKeyAt2);
                        sQLitePreparedStatementExecuteFast2.bindInteger(2, i19);
                        sQLitePreparedStatementExecuteFast2.bindLong(3, jKeyAt2);
                        sQLitePreparedStatementExecuteFast2.step();
                        sQLitePreparedStatementExecuteFast2.dispose();
                    }
                }
            }
            if (longSparseIntArray5.size() > 0 || longSparseIntArray6.size() > 0) {
                this.database.beginTransaction();
                if (longSparseIntArray5.size() > 0) {
                    ArrayList<Long> arrayList4 = new ArrayList<>();
                    SQLitePreparedStatement sQLitePreparedStatementExecuteFast3 = this.database.executeFast("UPDATE dialogs SET unread_count = ? WHERE did = ?");
                    int i20 = 0;
                    while (i20 < longSparseIntArray5.size()) {
                        long jKeyAt3 = longSparseIntArray5.keyAt(i20);
                        if (isForum(jKeyAt3, 13)) {
                            longSparseIntArray5.removeAt(i20);
                            i20--;
                            i4 = 1;
                        } else {
                            int iValueAt = longSparseIntArray5.valueAt(i20);
                            SQLiteCursor sQLiteCursorQueryFinalized7 = this.database.queryFinalized("SELECT unread_count FROM dialogs WHERE did = " + jKeyAt3, new Object[0]);
                            int iIntValue4 = sQLiteCursorQueryFinalized7.next() ? sQLiteCursorQueryFinalized7.intValue(0) : 0;
                            sQLiteCursorQueryFinalized7.dispose();
                            if (iIntValue4 == iValueAt) {
                                longSparseIntArray5.removeAt(i20);
                                i20--;
                                i4 = 1;
                            } else {
                                sQLitePreparedStatementExecuteFast3.requery();
                                i4 = 1;
                                sQLitePreparedStatementExecuteFast3.bindInteger(1, iValueAt);
                                sQLitePreparedStatementExecuteFast3.bindLong(2, jKeyAt3);
                                sQLitePreparedStatementExecuteFast3.step();
                                arrayList4.add(Long.valueOf(jKeyAt3));
                            }
                        }
                        i20 += i4;
                    }
                    i2 = 0;
                    sQLitePreparedStatementExecuteFast3.dispose();
                    updateWidgets(arrayList4);
                } else {
                    i2 = 0;
                }
                if (longSparseIntArray6.size() > 0) {
                    SQLitePreparedStatement sQLitePreparedStatementExecuteFast4 = this.database.executeFast("UPDATE dialogs SET unread_count_i = ? WHERE did = ?");
                    int i21 = i2;
                    while (i21 < longSparseIntArray6.size()) {
                        long jKeyAt4 = longSparseIntArray6.keyAt(i21);
                        if (isForum(jKeyAt4, 13)) {
                            longSparseIntArray6.removeAt(i21);
                            i21--;
                            i3 = 1;
                        } else {
                            sQLitePreparedStatementExecuteFast4.requery();
                            i3 = 1;
                            sQLitePreparedStatementExecuteFast4.bindInteger(1, longSparseIntArray6.valueAt(i21));
                            sQLitePreparedStatementExecuteFast4.bindLong(2, jKeyAt4);
                            sQLitePreparedStatementExecuteFast4.step();
                        }
                        i21 += i3;
                    }
                    sQLitePreparedStatementExecuteFast4.dispose();
                }
                this.database.commitTransaction();
            }
            updateFiltersReadCounter(longSparseIntArray5, longSparseIntArray6, true);
            getMessagesController().processDialogsUpdateRead(longSparseIntArray5, longSparseIntArray6);
            if (arrayList2.isEmpty()) {
                return;
            }
            getMessagesController().reloadMentionsCountForChannels(arrayList2);
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    private static boolean isEmpty(SparseArray<?> sparseArray) {
        return sparseArray == null || sparseArray.size() == 0;
    }

    private static boolean isEmpty(LongSparseIntArray longSparseIntArray) {
        return longSparseIntArray == null || longSparseIntArray.size() == 0;
    }

    private static boolean isEmpty(List<?> list) {
        return list == null || list.isEmpty();
    }

    private static boolean isEmpty(SparseIntArray sparseIntArray) {
        return sparseIntArray == null || sparseIntArray.size() == 0;
    }

    private static boolean isEmpty(LongSparseArray<?> longSparseArray) {
        return longSparseArray == null || longSparseArray.size() == 0;
    }

    public void updateDialogsWithReadMessages(final LongSparseIntArray longSparseIntArray, final LongSparseIntArray longSparseIntArray2, final LongSparseArray<ArrayList<Integer>> longSparseArray, final LongSparseIntArray longSparseIntArray3, boolean z) {
        if (isEmpty(longSparseIntArray) && isEmpty(longSparseIntArray2) && isEmpty(longSparseArray) && isEmpty(longSparseIntArray3)) {
            return;
        }
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$updateDialogsWithReadMessages$121(longSparseIntArray, longSparseIntArray2, longSparseArray, longSparseIntArray3);
                }
            });
        } else {
            updateDialogsWithReadMessagesInternal(null, longSparseIntArray, longSparseIntArray2, longSparseArray, longSparseIntArray3);
        }
    }

    public void lambda$updateChatParticipants$123(TLRPC.ChatParticipants chatParticipants) throws Throwable {
        final TLRPC.ChatFull chatFullTLdeserialize;
        NativeByteBuffer nativeByteBufferByteBufferValue;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                SQLiteCursor sQLiteCursorQueryFinalized = this.database.queryFinalized("SELECT info, pinned, online, inviter FROM chat_settings_v2 WHERE uid = " + chatParticipants.chat_id, new Object[0]);
                try {
                    new ArrayList();
                    if (!sQLiteCursorQueryFinalized.next() || (nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized.byteBufferValue(0)) == null) {
                        chatFullTLdeserialize = null;
                    } else {
                        chatFullTLdeserialize = TLRPC.ChatFull.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false);
                        nativeByteBufferByteBufferValue.reuse();
                        chatFullTLdeserialize.pinned_msg_id = sQLiteCursorQueryFinalized.intValue(1);
                        chatFullTLdeserialize.online_count = sQLiteCursorQueryFinalized.intValue(2);
                        chatFullTLdeserialize.inviterId = sQLiteCursorQueryFinalized.longValue(3);
                    }
                    sQLiteCursorQueryFinalized.dispose();
                    if (chatFullTLdeserialize instanceof TLRPC.TL_chatFull) {
                        chatFullTLdeserialize.participants = chatParticipants;
                        AndroidUtilities.runOnUIThread(new Runnable() { 
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$updateChatParticipants$122(chatFullTLdeserialize);
                            }
                        });
                        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = this.database.executeFast("REPLACE INTO chat_settings_v2 VALUES(?, ?, ?, ?, ?, ?, ?)");
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(chatFullTLdeserialize.getObjectSize());
                        chatFullTLdeserialize.serializeToStream(nativeByteBuffer);
                        sQLitePreparedStatementExecuteFast.bindLong(1, chatFullTLdeserialize.id);
                        sQLitePreparedStatementExecuteFast.bindByteBuffer(2, nativeByteBuffer);
                        sQLitePreparedStatementExecuteFast.bindInteger(3, chatFullTLdeserialize.pinned_msg_id);
                        sQLitePreparedStatementExecuteFast.bindInteger(4, chatFullTLdeserialize.online_count);
                        sQLitePreparedStatementExecuteFast.bindLong(5, chatFullTLdeserialize.inviterId);
                        sQLitePreparedStatementExecuteFast.bindInteger(6, chatFullTLdeserialize.invitesCount);
                        sQLitePreparedStatementExecuteFast.bindInteger(7, chatFullTLdeserialize.participants_count);
                        sQLitePreparedStatementExecuteFast.step();
                        sQLitePreparedStatementExecuteFast.dispose();
                        nativeByteBuffer.reuse();
                        return;
                    }
                    return;
                } catch (Exception e) {
                    e = e;
                    sQLiteCursor = sQLiteCursorQueryFinalized;
                } catch (Throwable th) {
                    th = th;
                    sQLiteCursor = sQLiteCursorQueryFinalized;
                    if (sQLiteCursor != null) {
                        sQLiteCursor.dispose();
                    }
                    throw th;
                }
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
        checkSQLException(e);
        if (sQLiteCursor != null) {
            sQLiteCursor.dispose();
        }
    }

    public void lambda$loadChannelAdmins$124(long j) throws Throwable {
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                SQLiteCursor sQLiteCursorQueryFinalized = this.database.queryFinalized("SELECT uid, data FROM channel_admins_v3 WHERE did = " + j, new Object[0]);
                try {
                    LongSparseArray<TLRPC.ChannelParticipant> longSparseArray = new LongSparseArray<>();
                    while (sQLiteCursorQueryFinalized.next()) {
                        NativeByteBuffer nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized.byteBufferValue(1);
                        if (nativeByteBufferByteBufferValue != null) {
                            TLRPC.ChannelParticipant channelParticipantTLdeserialize = TLRPC.ChannelParticipant.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false);
                            nativeByteBufferByteBufferValue.reuse();
                            if (channelParticipantTLdeserialize != null) {
                                longSparseArray.put(sQLiteCursorQueryFinalized.longValue(0), channelParticipantTLdeserialize);
                            }
                        }
                    }
                    sQLiteCursorQueryFinalized.dispose();
                    getMessagesController().processLoadedChannelAdmins(longSparseArray, j, true);
                } catch (Exception e) {
                    e = e;
                    sQLiteCursor = sQLiteCursorQueryFinalized;
                    checkSQLException(e);
                    if (sQLiteCursor != null) {
                        sQLiteCursor.dispose();
                    }
                } catch (Throwable th) {
                    th = th;
                    sQLiteCursor = sQLiteCursorQueryFinalized;
                    if (sQLiteCursor != null) {
                        sQLiteCursor.dispose();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public void putChannelAdmins(final long j, final LongSparseArray<TLRPC.ChannelParticipant> longSparseArray) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$putChannelAdmins$125(j, longSparseArray);
            }
        });
    }

    void lambda$saveBotCache$127(TLObject tLObject, String str) throws Throwable {
        int i;
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                int currentTime = getConnectionsManager().getCurrentTime();
                try {
                    if (tLObject instanceof TLRPC.TL_messages_botCallbackAnswer) {
                        i = ((TLRPC.TL_messages_botCallbackAnswer) tLObject).cache_time;
                    } else {
                        if (tLObject instanceof TLRPC.TL_messages_botResults) {
                            i = ((TLRPC.TL_messages_botResults) tLObject).cache_time;
                        }
                        sQLitePreparedStatementExecuteFast = this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tLObject.getObjectSize());
                        tLObject.serializeToStream(nativeByteBuffer);
                        sQLitePreparedStatementExecuteFast.bindString(1, str);
                        sQLitePreparedStatementExecuteFast.bindInteger(2, currentTime);
                        sQLitePreparedStatementExecuteFast.bindByteBuffer(3, nativeByteBuffer);
                        sQLitePreparedStatementExecuteFast.step();
                        sQLitePreparedStatementExecuteFast.dispose();
                        nativeByteBuffer.reuse();
                        return;
                    }
                    NativeByteBuffer nativeByteBuffer2 = new NativeByteBuffer(tLObject.getObjectSize());
                    tLObject.serializeToStream(nativeByteBuffer2);
                    sQLitePreparedStatementExecuteFast.bindString(1, str);
                    sQLitePreparedStatementExecuteFast.bindInteger(2, currentTime);
                    sQLitePreparedStatementExecuteFast.bindByteBuffer(3, nativeByteBuffer2);
                    sQLitePreparedStatementExecuteFast.step();
                    sQLitePreparedStatementExecuteFast.dispose();
                    nativeByteBuffer2.reuse();
                    return;
                } catch (Exception e) {
                    e = e;
                    sQLitePreparedStatement = sQLitePreparedStatementExecuteFast;
                    checkSQLException(e);
                    if (sQLitePreparedStatement != null) {
                        sQLitePreparedStatement.dispose();
                        return;
                    }
                    return;
                } catch (Throwable th) {
                    th = th;
                    sQLitePreparedStatement = sQLitePreparedStatementExecuteFast;
                    if (sQLitePreparedStatement != null) {
                        sQLitePreparedStatement.dispose();
                    }
                    throw th;
                }
                currentTime += i;
                sQLitePreparedStatementExecuteFast = this.database.executeFast("REPLACE INTO botcache VALUES(?, ?, ?)");
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public void getBotCache(final String str, final RequestDelegate requestDelegate) {
        if (str == null || requestDelegate == null) {
            return;
        }
        final int currentTime = getConnectionsManager().getCurrentTime();
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$getBotCache$128(currentTime, str, requestDelegate);
            }
        });
    }

    void lambda$loadUserInfo$129(ArrayList arrayList) {
        getMessagesController().putChats(arrayList, true);
    }

    public void updateUserInfo(final TLRPC.UserFull userFull, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$updateUserInfo$131(userFull, z);
            }
        });
    }

    public void lambda$saveChatInviter$133(long j, long j2) {
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = null;
        try {
            try {
                sQLitePreparedStatementExecuteFast = this.database.executeFast("UPDATE chat_settings_v2 SET inviter = ? WHERE uid = ?");
                sQLitePreparedStatementExecuteFast.requery();
                sQLitePreparedStatementExecuteFast.bindLong(1, j);
                sQLitePreparedStatementExecuteFast.bindLong(2, j2);
                sQLitePreparedStatementExecuteFast.step();
                sQLitePreparedStatementExecuteFast.dispose();
                sQLitePreparedStatementExecuteFast.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLitePreparedStatementExecuteFast != null) {
                    sQLitePreparedStatementExecuteFast.dispose();
                }
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatementExecuteFast != null) {
                sQLitePreparedStatementExecuteFast.dispose();
            }
            throw th;
        }
    }

    public void saveChatLinksCount(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$saveChatLinksCount$134(i, j);
            }
        });
    }

    public void lambda$updateChatOnlineCount$136(int i, long j) {
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = null;
        try {
            try {
                sQLitePreparedStatementExecuteFast = this.database.executeFast("UPDATE chat_settings_v2 SET online = ? WHERE uid = ?");
                sQLitePreparedStatementExecuteFast.requery();
                sQLitePreparedStatementExecuteFast.bindInteger(1, i);
                sQLitePreparedStatementExecuteFast.bindLong(2, j);
                sQLitePreparedStatementExecuteFast.step();
                sQLitePreparedStatementExecuteFast.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLitePreparedStatementExecuteFast != null) {
                    sQLitePreparedStatementExecuteFast.dispose();
                }
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatementExecuteFast != null) {
                sQLitePreparedStatementExecuteFast.dispose();
            }
            throw th;
        }
    }

    public void updatePinnedMessages(final long j, final ArrayList<Integer> arrayList, final boolean z, final int i, final int i2, final boolean z2, final HashMap<Integer, MessageObject> map) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$updatePinnedMessages$139(z, map, i2, j, arrayList, i, z2);
            }
        });
    }

    void lambda$updatePinnedMessages$137(long j, ArrayList arrayList, HashMap map, int i, int i2, boolean z) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didLoadPinnedMessages, Long.valueOf(j), arrayList, Boolean.TRUE, null, map, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z));
    }

    public public void lambda$updateChatInfo$140(TLRPC.ChatFull chatFull) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.chatInfoDidLoad;
        Boolean bool = Boolean.FALSE;
        notificationCenter.lambda$postNotificationNameOnUIThread$1(i, chatFull, 0, bool, bool);
    }

    public boolean isMigratedChat(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$isMigratedChat$142(j, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return zArr[0];
    }

    public void lambda$getMessage$143(long j, long j2, AtomicReference atomicReference, CountDownLatch countDownLatch) {
        SQLiteCursor sQLiteCursorQueryFinalized = null;
        try {
            try {
                sQLiteCursorQueryFinalized = this.database.queryFinalized("SELECT data FROM messages_v2 WHERE uid = " + j + " AND mid = " + j2 + " LIMIT 1", new Object[0]);
                while (sQLiteCursorQueryFinalized.next()) {
                    NativeByteBuffer nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized.byteBufferValue(0);
                    if (nativeByteBufferByteBufferValue != null) {
                        TLRPC.Message messageTLdeserialize = TLRPC.Message.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false);
                        nativeByteBufferByteBufferValue.reuse();
                        atomicReference.set(messageTLdeserialize);
                    }
                }
                sQLiteCursorQueryFinalized.dispose();
                countDownLatch.countDown();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursorQueryFinalized != null) {
                    sQLiteCursorQueryFinalized.dispose();
                }
                countDownLatch.countDown();
            }
        } catch (Throwable th) {
            if (sQLiteCursorQueryFinalized != null) {
                sQLiteCursorQueryFinalized.dispose();
            }
            countDownLatch.countDown();
            throw th;
        }
    }

    public boolean hasInviteMeMessage(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final boolean[] zArr = new boolean[1];
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$hasInviteMeMessage$144(j, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return zArr[0];
    }

    public void lambda$loadChatInfo$145(TLRPC.ChatFull[] chatFullArr, long j, boolean z, boolean z2, boolean z3, int i, CountDownLatch countDownLatch) {
        chatFullArr[0] = loadChatInfoInternal(j, z, z2, z3, i);
        if (countDownLatch != null) {
            countDownLatch.countDown();
        }
    }

    public TLRPC.ChatFull loadChatInfoInQueue(long j, boolean z, boolean z2, boolean z3, int i) {
        return loadChatInfoInternal(j, z, z2, z3, i);
    }

    public void processPendingRead(final long j, final int i, final int i2, final int i3) {
        final int i4 = this.lastSavedDate;
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$processPendingRead$146(j, i, i3, i4, i2);
            }
        });
    }

    void lambda$deleteContacts$148(ArrayList arrayList) {
        try {
            String strJoin = TextUtils.join(",", arrayList);
            this.database.executeFast("DELETE FROM contacts WHERE uid IN(" + strJoin + ")").stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void applyPhoneBookUpdates(final String str, final String str2) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$applyPhoneBookUpdates$149(str, str2);
            }
        });
    }

    public void lambda$checkMessageByRandomId$154(long j, boolean[] zArr, CountDownLatch countDownLatch) {
        SQLiteCursor sQLiteCursorQueryFinalized = null;
        try {
            try {
                sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT random_id FROM randoms_v2 WHERE random_id = %d", Long.valueOf(j)), new Object[0]);
                if (sQLiteCursorQueryFinalized.next()) {
                    zArr[0] = true;
                }
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursorQueryFinalized != null) {
                }
                countDownLatch.countDown();
            }
            sQLiteCursorQueryFinalized.dispose();
            countDownLatch.countDown();
        } catch (Throwable th) {
            if (sQLiteCursorQueryFinalized != null) {
                sQLiteCursorQueryFinalized.dispose();
            }
            throw th;
        }
    }

    public boolean checkMessageId(final long j, final int i) {
        final boolean[] zArr = new boolean[1];
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$checkMessageId$155(j, i, zArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return zArr[0];
    }

    public void lambda$getUnreadMention$157(long j, long j2, final IntCallback intCallback) {
        SQLiteCursor sQLiteCursorQueryFinalized;
        SQLiteDatabase sQLiteDatabase = this.database;
        SQLiteCursor sQLiteCursor = null;
        try {
            if (j != 0) {
                sQLiteCursorQueryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT MIN(mid) FROM messages_topics WHERE uid = %d AND topic_id = %d AND mention = 1 AND read_state IN(0, 1)", Long.valueOf(j2), Long.valueOf(j)), new Object[0]);
            } else {
                sQLiteCursorQueryFinalized = sQLiteDatabase.queryFinalized(String.format(Locale.US, "SELECT MIN(mid) FROM messages_v2 WHERE uid = %d AND mention = 1 AND read_state IN(0, 1)", Long.valueOf(j2)), new Object[0]);
            }
            sQLiteCursor = sQLiteCursorQueryFinalized;
            final int iIntValue = sQLiteCursor.next() ? sQLiteCursor.intValue(0) : 0;
            sQLiteCursor.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    intCallback.run(iIntValue);
                }
            });
            sQLiteCursor.dispose();
        } catch (Exception e) {
            checkSQLException(e);
        } finally {
            if (sQLiteCursor != null) {
                sQLiteCursor.dispose();
            }
        }
    }

    public void getMessagesCount(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getMessagesCount$159(j, intCallback);
            }
        });
    }

    public throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.getMessagesInternal(long, long, int, int, int, int, int, int, int, long, int, boolean, boolean, org.telegram.messenger.Timer):java.lang.Runnable");
    }

    public static void lambda$getMessagesInternal$161(TLRPC.TL_messages_messages tL_messages_messages, int i, long j, long j2, int i2, int i3, int i4, int i5, int i6, int i7, int i8, int i9, int i10, boolean z, int i11, long j3, int i12, boolean z2, int i13, boolean z3, boolean z4, Timer timer) {
        getMessagesController().processLoadedMessages(tL_messages_messages, i, j, j2, i2, i3, i4, true, i5, i6, i7, i8, i9, i10, z, i11, j3, i12, z2, i13, z3, z4, timer);
    }

    public void getAnimatedEmoji(String str, ArrayList<TLRPC.Document> arrayList) {
        SQLiteCursor sQLiteCursorQueryFinalized = null;
        try {
            try {
                sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM animated_emoji WHERE document_id IN (%s)", str), new Object[0]);
                while (sQLiteCursorQueryFinalized.next()) {
                    NativeByteBuffer nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized.byteBufferValue(0);
                    try {
                        TLRPC.Document documentTLdeserialize = TLRPC.Document.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(true), true);
                        if (documentTLdeserialize != null && documentTLdeserialize.id != 0) {
                            arrayList.add(documentTLdeserialize);
                        }
                    } catch (Exception e) {
                        checkSQLException(e);
                    }
                    if (nativeByteBufferByteBufferValue != null) {
                        nativeByteBufferByteBufferValue.reuse();
                    }
                }
            } catch (SQLiteException e2) {
                e2.printStackTrace();
                if (sQLiteCursorQueryFinalized == null) {
                    return;
                }
            }
            sQLiteCursorQueryFinalized.dispose();
        } catch (Throwable th) {
            if (sQLiteCursorQueryFinalized != null) {
                sQLiteCursorQueryFinalized.dispose();
            }
            throw th;
        }
    }

    public void getMessages(final long j, final long j2, boolean z, final int i, final int i2, final int i3, final int i4, final int i5, final int i6, final int i7, final long j3, final int i8, final boolean z2, final boolean z3, final Timer timer) {
        final Timer.Task taskStart = Timer.start(timer, "MessagesStorage.getMessages: storageQueue.postRunnable");
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getMessages$163(taskStart, timer, j, j2, i, i2, i3, i4, i5, i6, i7, j3, i8, z2, z3);
            }
        });
    }

    public void $r8$lambda$xG1Vi8fakdklTu0GZCTd0I2FKZY(Timer.Task task, Runnable runnable) {
        Timer.done(task);
        runnable.run();
    }

    public void clearSentMedia() {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$clearSentMedia$164();
            }
        });
    }

    public void lambda$getSentFile$165(String str, int i, Object[] objArr, CountDownLatch countDownLatch) {
        NativeByteBuffer nativeByteBufferByteBufferValue;
        try {
            String strMD5 = Utilities.MD5(str);
            if (strMD5 != null) {
                SQLiteCursor sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data, parent FROM sent_files_v2 WHERE uid = '%s' AND type = %d", strMD5, Integer.valueOf(i)), new Object[0]);
                if (sQLiteCursorQueryFinalized.next() && (nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized.byteBufferValue(0)) != null) {
                    TLRPC.MessageMedia messageMediaTLdeserialize = TLRPC.MessageMedia.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false);
                    nativeByteBufferByteBufferValue.reuse();
                    if (messageMediaTLdeserialize instanceof TLRPC.TL_messageMediaDocument) {
                        objArr[0] = ((TLRPC.TL_messageMediaDocument) messageMediaTLdeserialize).document;
                    } else if (messageMediaTLdeserialize instanceof TLRPC.TL_messageMediaPhoto) {
                        objArr[0] = ((TLRPC.TL_messageMediaPhoto) messageMediaTLdeserialize).photo;
                    }
                    if (objArr[0] != null) {
                        objArr[1] = sQLiteCursorQueryFinalized.stringValue(1);
                    }
                }
                sQLiteCursorQueryFinalized.dispose();
            }
        } catch (Exception e) {
            checkSQLException(e);
        } finally {
            countDownLatch.countDown();
        }
    }

    private void updateWidgets(long j) {
        ArrayList<Long> arrayList = new ArrayList<>();
        arrayList.add(Long.valueOf(j));
        updateWidgets(arrayList);
    }

    private void updateWidgets(ArrayList<Long> arrayList) {
        if (arrayList.isEmpty()) {
            return;
        }
        try {
            SQLiteCursor sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT DISTINCT id FROM shortcut_widget WHERE did IN(%s,-1)", TextUtils.join(",", arrayList)), new Object[0]);
            final ArrayList arrayList2 = null;
            while (sQLiteCursorQueryFinalized.next()) {
                if (arrayList2 == null) {
                    arrayList2 = new ArrayList();
                }
                arrayList2.add(Integer.valueOf(sQLiteCursorQueryFinalized.intValue(0)));
            }
            sQLiteCursorQueryFinalized.dispose();
            if (arrayList2 != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesStorage.$r8$lambda$EHG1_y5e86ZJFYgMLnAfDuTdzpE(arrayList2);
                    }
                });
            }
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public static void lambda$putWidgetDialogs$167(int i, ArrayList arrayList) {
        try {
            this.database.beginTransaction();
            this.database.executeFast("DELETE FROM shortcut_widget WHERE id = " + i).stepThis().dispose();
            SQLitePreparedStatement sQLitePreparedStatementExecuteFast = this.database.executeFast("REPLACE INTO shortcut_widget VALUES(?, ?, ?)");
            if (arrayList.isEmpty()) {
                sQLitePreparedStatementExecuteFast.requery();
                sQLitePreparedStatementExecuteFast.bindInteger(1, i);
                sQLitePreparedStatementExecuteFast.bindLong(2, -1L);
                sQLitePreparedStatementExecuteFast.bindInteger(3, 0);
                sQLitePreparedStatementExecuteFast.step();
            } else {
                int size = arrayList.size();
                for (int i2 = 0; i2 < size; i2++) {
                    long j = ((TopicKey) arrayList.get(i2)).dialogId;
                    sQLitePreparedStatementExecuteFast.requery();
                    sQLitePreparedStatementExecuteFast.bindInteger(1, i);
                    sQLitePreparedStatementExecuteFast.bindLong(2, j);
                    sQLitePreparedStatementExecuteFast.bindInteger(3, i2);
                    sQLitePreparedStatementExecuteFast.step();
                }
            }
            sQLitePreparedStatementExecuteFast.dispose();
            this.database.commitTransaction();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void clearWidgetDialogs(final int i) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$clearWidgetDialogs$168(i);
            }
        });
    }

    public void lambda$getWidgetDialogIds$169(int i, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, boolean z, int i2, CountDownLatch countDownLatch) throws Throwable {
        Throwable th;
        Exception exc;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                ArrayList<Long> arrayList4 = new ArrayList<>();
                ArrayList arrayList5 = new ArrayList();
                SQLiteCursor sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT did FROM shortcut_widget WHERE id = %d ORDER BY ord ASC", Integer.valueOf(i)), new Object[0]);
                while (sQLiteCursorQueryFinalized.next()) {
                    try {
                        long jLongValue = sQLiteCursorQueryFinalized.longValue(0);
                        if (jLongValue != -1) {
                            arrayList.add(Long.valueOf(jLongValue));
                            if (arrayList2 != null && arrayList3 != null) {
                                if (DialogObject.isUserDialog(jLongValue)) {
                                    arrayList4.add(Long.valueOf(jLongValue));
                                } else {
                                    arrayList5.add(Long.valueOf(-jLongValue));
                                }
                            }
                        }
                    } catch (Exception e) {
                        exc = e;
                        sQLiteCursor = sQLiteCursorQueryFinalized;
                        checkSQLException(exc);
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        countDownLatch.countDown();
                        return;
                    } catch (Throwable th2) {
                        th = th2;
                        sQLiteCursor = sQLiteCursorQueryFinalized;
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        countDownLatch.countDown();
                        throw th;
                    }
                }
                sQLiteCursorQueryFinalized.dispose();
                if (!z && arrayList.isEmpty()) {
                    if (i2 == 0) {
                        SQLiteCursor sQLiteCursorQueryFinalized2 = this.database.queryFinalized("SELECT did FROM dialogs WHERE folder_id = 0 ORDER BY pinned DESC, date DESC LIMIT 0,10", new Object[0]);
                        while (sQLiteCursorQueryFinalized2.next()) {
                            long jLongValue2 = sQLiteCursorQueryFinalized2.longValue(0);
                            if (!DialogObject.isFolderDialogId(jLongValue2)) {
                                arrayList.add(Long.valueOf(jLongValue2));
                                if (arrayList2 != null && arrayList3 != null) {
                                    if (DialogObject.isUserDialog(jLongValue2)) {
                                        arrayList4.add(Long.valueOf(jLongValue2));
                                    } else {
                                        arrayList5.add(Long.valueOf(-jLongValue2));
                                    }
                                }
                            }
                        }
                        sQLiteCursorQueryFinalized2.dispose();
                    } else {
                        SQLiteCursor sQLiteCursorQueryFinalized3 = getMessagesStorage().getDatabase().queryFinalized("SELECT did FROM chat_hints WHERE type = 0 ORDER BY rating DESC LIMIT 4", new Object[0]);
                        while (sQLiteCursorQueryFinalized3.next()) {
                            long jLongValue3 = sQLiteCursorQueryFinalized3.longValue(0);
                            arrayList.add(Long.valueOf(jLongValue3));
                            if (arrayList2 != null && arrayList3 != null) {
                                if (DialogObject.isUserDialog(jLongValue3)) {
                                    arrayList4.add(Long.valueOf(jLongValue3));
                                } else {
                                    arrayList5.add(Long.valueOf(-jLongValue3));
                                }
                            }
                        }
                        sQLiteCursorQueryFinalized3.dispose();
                    }
                }
                if (arrayList2 != null && arrayList3 != null) {
                    if (!arrayList5.isEmpty()) {
                        getChatsInternal(TextUtils.join(",", arrayList5), arrayList3);
                    }
                    if (!arrayList4.isEmpty()) {
                        getUsersInternal(arrayList4, (ArrayList<TLRPC.User>) arrayList2);
                    }
                }
                countDownLatch.countDown();
            } catch (Exception e2) {
                exc = e2;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public void getWidgetDialogs(final int i, final int i2, final ArrayList<Long> arrayList, final LongSparseArray<TLRPC.Dialog> longSparseArray, final LongSparseArray<TLRPC.Message> longSparseArray2, final ArrayList<TLRPC.User> arrayList2, final ArrayList<TLRPC.Chat> arrayList3) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$getWidgetDialogs$170(i, arrayList, i2, longSparseArray, longSparseArray2, arrayList3, arrayList2, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public public void lambda$updateEncryptedChatSeq$172(TLRPC.EncryptedChat encryptedChat, boolean z) {
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = null;
        try {
            try {
                sQLitePreparedStatementExecuteFast = this.database.executeFast("UPDATE enc_chats SET seq_in = ?, seq_out = ?, use_count = ?, in_seq_no = ?, mtproto_seq = ? WHERE uid = ?");
                sQLitePreparedStatementExecuteFast.bindInteger(1, encryptedChat.seq_in);
                sQLitePreparedStatementExecuteFast.bindInteger(2, encryptedChat.seq_out);
                sQLitePreparedStatementExecuteFast.bindInteger(3, (encryptedChat.key_use_count_in << 16) | encryptedChat.key_use_count_out);
                sQLitePreparedStatementExecuteFast.bindInteger(4, encryptedChat.in_seq_no);
                sQLitePreparedStatementExecuteFast.bindInteger(5, encryptedChat.mtproto_seq);
                sQLitePreparedStatementExecuteFast.bindInteger(6, encryptedChat.id);
                sQLitePreparedStatementExecuteFast.step();
                if (z && encryptedChat.in_seq_no != 0) {
                    long encryptedChatId = DialogObject.getEncryptedChatId(encryptedChat.id);
                    this.database.executeFast(String.format(Locale.US, "DELETE FROM messages_v2 WHERE mid IN (SELECT m.mid FROM messages_v2 as m LEFT JOIN messages_seq as s ON m.mid = s.mid WHERE m.uid = %d AND m.date = 0 AND m.mid < 0 AND s.seq_out <= %d) AND uid = %d", Long.valueOf(encryptedChatId), Integer.valueOf(encryptedChat.in_seq_no), Long.valueOf(encryptedChatId))).stepThis().dispose();
                }
                sQLitePreparedStatementExecuteFast.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLitePreparedStatementExecuteFast != null) {
                    sQLitePreparedStatementExecuteFast.dispose();
                }
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatementExecuteFast != null) {
                sQLitePreparedStatementExecuteFast.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChatTTL(final TLRPC.EncryptedChat encryptedChat) {
        if (encryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$updateEncryptedChatTTL$173(encryptedChat);
            }
        });
    }

    public void lambda$updateEncryptedChatLayer$174(TLRPC.EncryptedChat encryptedChat) {
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = null;
        try {
            try {
                sQLitePreparedStatementExecuteFast = this.database.executeFast("UPDATE enc_chats SET layer = ? WHERE uid = ?");
                sQLitePreparedStatementExecuteFast.bindInteger(1, encryptedChat.layer);
                sQLitePreparedStatementExecuteFast.bindInteger(2, encryptedChat.id);
                sQLitePreparedStatementExecuteFast.step();
                sQLitePreparedStatementExecuteFast.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLitePreparedStatementExecuteFast != null) {
                    sQLitePreparedStatementExecuteFast.dispose();
                }
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatementExecuteFast != null) {
                sQLitePreparedStatementExecuteFast.dispose();
            }
            throw th;
        }
    }

    public void updateEncryptedChat(final TLRPC.EncryptedChat encryptedChat) {
        if (encryptedChat == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$updateEncryptedChat$175(encryptedChat);
            }
        });
    }

    public void lambda$hasAuthMessage$177(int i, boolean[] zArr, CountDownLatch countDownLatch) {
        SQLiteCursor sQLiteCursorQueryFinalized = null;
        try {
            try {
                sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages_v2 WHERE uid = 777000 AND date = %d AND mid < 0 LIMIT 1", Integer.valueOf(i)), new Object[0]);
                zArr[0] = sQLiteCursorQueryFinalized.next();
                sQLiteCursorQueryFinalized.dispose();
                countDownLatch.countDown();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursorQueryFinalized != null) {
                    sQLiteCursorQueryFinalized.dispose();
                }
                countDownLatch.countDown();
            }
        } catch (Throwable th) {
            if (sQLiteCursorQueryFinalized != null) {
                sQLiteCursorQueryFinalized.dispose();
            }
            countDownLatch.countDown();
            throw th;
        }
    }

    public void getEncryptedChat(final long j, final CountDownLatch countDownLatch, final ArrayList<TLObject> arrayList) {
        if (countDownLatch == null || arrayList == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getEncryptedChat$178(j, arrayList, countDownLatch);
            }
        });
    }

    public public void lambda$containsLocalDialog$180(long j, Boolean[] boolArr, CountDownLatch countDownLatch) {
        SQLiteCursor sQLiteCursorQueryFinalized = null;
        try {
            try {
                sQLiteCursorQueryFinalized = this.database.queryFinalized("SELECT date FROM dialogs WHERE did = " + j, new Object[0]);
                boolArr[0] = Boolean.valueOf(sQLiteCursorQueryFinalized.next());
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursorQueryFinalized != null) {
                }
                countDownLatch.countDown();
            }
            sQLiteCursorQueryFinalized.dispose();
            countDownLatch.countDown();
        } catch (Throwable th) {
            if (sQLiteCursorQueryFinalized != null) {
                sQLiteCursorQueryFinalized.dispose();
            }
            throw th;
        }
    }

    private void putUsersInternal(List<TLRPC.User> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = this.database.executeFast("REPLACE INTO users VALUES(?, ?, ?, ?)");
        for (int i = 0; i < list.size(); i++) {
            TLRPC.User user = list.get(i);
            if (user != null) {
                if (user.min) {
                    SQLiteCursor sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM users WHERE uid = %d", Long.valueOf(user.id)), new Object[0]);
                    if (sQLiteCursorQueryFinalized.next()) {
                        try {
                            NativeByteBuffer nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized.byteBufferValue(0);
                            if (nativeByteBufferByteBufferValue != null) {
                                TLRPC.User userTLdeserialize = TLRPC.User.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false);
                                nativeByteBufferByteBufferValue.reuse();
                                if (userTLdeserialize != null) {
                                    String str = user.username;
                                    if (str != null) {
                                        userTLdeserialize.username = str;
                                        userTLdeserialize.flags |= 8;
                                    } else {
                                        userTLdeserialize.username = null;
                                        userTLdeserialize.flags &= -9;
                                    }
                                    if (user.apply_min_photo) {
                                        TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
                                        if (userProfilePhoto != null) {
                                            userTLdeserialize.photo = userProfilePhoto;
                                            userTLdeserialize.flags |= 32;
                                        } else {
                                            userTLdeserialize.photo = null;
                                            userTLdeserialize.flags &= -33;
                                        }
                                    }
                                    user = userTLdeserialize;
                                }
                            }
                        } catch (Exception e) {
                            checkSQLException(e);
                        }
                    }
                    sQLiteCursorQueryFinalized.dispose();
                }
                sQLitePreparedStatementExecuteFast.requery();
                NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(user.getObjectSize());
                user.serializeToStream(nativeByteBuffer);
                sQLitePreparedStatementExecuteFast.bindLong(1, user.id);
                sQLitePreparedStatementExecuteFast.bindString(2, formatUserSearchName(user));
                TLRPC.UserStatus userStatus = user.status;
                if (userStatus != null) {
                    if (userStatus instanceof TLRPC.TL_userStatusRecently) {
                        userStatus.expires = userStatus.by_me ? -1000 : -100;
                    } else if (userStatus instanceof TLRPC.TL_userStatusLastWeek) {
                        userStatus.expires = userStatus.by_me ? -1001 : -101;
                    } else if (userStatus instanceof TLRPC.TL_userStatusLastMonth) {
                        userStatus.expires = userStatus.by_me ? -1002 : -102;
                    }
                    sQLitePreparedStatementExecuteFast.bindInteger(3, userStatus.expires);
                } else {
                    sQLitePreparedStatementExecuteFast.bindInteger(3, 0);
                }
                sQLitePreparedStatementExecuteFast.bindByteBuffer(4, nativeByteBuffer);
                sQLitePreparedStatementExecuteFast.step();
                nativeByteBuffer.reuse();
                isForumCacheInvalidate(user.id);
            }
        }
        sQLitePreparedStatementExecuteFast.dispose();
    }

    public void updateChatDefaultBannedRights(final long j, final TLRPC.TL_chatBannedRights tL_chatBannedRights, final int i) {
        if (tL_chatBannedRights == null || j == 0) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$updateChatDefaultBannedRights$181(j, i, tL_chatBannedRights);
            }
        });
    }

    void lambda$removeFromDownloadQueue$183(boolean z, int i, long j) throws Throwable {
        SQLiteDatabase sQLiteDatabase = this.database;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                if (z) {
                    Locale locale = Locale.US;
                    SQLiteCursor sQLiteCursorQueryFinalized = sQLiteDatabase.queryFinalized(String.format(locale, "SELECT min(date) FROM download_queue WHERE type = %d", Integer.valueOf(i)), new Object[0]);
                    try {
                        int iIntValue = sQLiteCursorQueryFinalized.next() ? sQLiteCursorQueryFinalized.intValue(0) : -1;
                        sQLiteCursorQueryFinalized.dispose();
                        if (iIntValue != -1) {
                            this.database.executeFast(String.format(locale, "UPDATE download_queue SET date = %d WHERE uid = %d AND type = %d", Integer.valueOf(iIntValue - 1), Long.valueOf(j), Integer.valueOf(i))).stepThis().dispose();
                            return;
                        }
                        return;
                    } catch (Exception e) {
                        e = e;
                        sQLiteCursor = sQLiteCursorQueryFinalized;
                        checkSQLException(e);
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                            return;
                        }
                        return;
                    } catch (Throwable th) {
                        th = th;
                        sQLiteCursor = sQLiteCursorQueryFinalized;
                        if (sQLiteCursor != null) {
                            sQLiteCursor.dispose();
                        }
                        throw th;
                    }
                }
                sQLiteDatabase.executeFast(String.format(Locale.US, "DELETE FROM download_queue WHERE uid = %d AND type = %d", Long.valueOf(j), Integer.valueOf(i))).stepThis().dispose();
            } catch (Exception e2) {
                e = e2;
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    void lambda$deleteFromDownloadQueue$184(ArrayList arrayList) {
        getDownloadController().cancelDownloading(arrayList);
    }

    public void clearDownloadQueue(final int i) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$clearDownloadQueue$185(i);
            }
        });
    }

    public public void lambda$getDownloadQueue$186(int i, ArrayList arrayList) {
        getDownloadController().processDownloadObjects(i, arrayList);
    }

    public int getMessageMediaType(TLRPC.Message message) {
        if (message instanceof TLRPC.TL_message_secret) {
            if (!(message.media instanceof TLRPC.TL_messageMediaPhoto) && !MessageObject.isGifMessage(message) && !MessageObject.isVoiceMessage(message) && !MessageObject.isVideoMessage(message) && !MessageObject.isRoundVideoMessage(message)) {
                return -1;
            }
            int i = message.ttl;
            return (i <= 0 || i > 60) ? 0 : 1;
        }
        if (message instanceof TLRPC.TL_message) {
            TLRPC.MessageMedia messageMedia = message.media;
            if (((messageMedia instanceof TLRPC.TL_messageMediaPhoto) || (messageMedia instanceof TLRPC.TL_messageMediaDocument)) && messageMedia.ttl_seconds != 0) {
                return 1;
            }
        }
        return ((message.media instanceof TLRPC.TL_messageMediaPhoto) || MessageObject.isVideoMessage(message)) ? 0 : -1;
    }

    public void putWebPages(final LongSparseArray<TLRPC.WebPage> longSparseArray) {
        if (isEmpty(longSparseArray)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$putWebPages$189(longSparseArray);
            }
        });
    }

    void lambda$putWebPages$188(ArrayList arrayList) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didReceivedWebpages, arrayList);
    }

    public void overwriteChannel(final long j, final TLRPC.TL_updates_channelDifferenceTooLong tL_updates_channelDifferenceTooLong, final int i, final Runnable runnable) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$overwriteChannel$191(j, i, tL_updates_channelDifferenceTooLong, runnable);
            }
        });
    }

    void lambda$overwriteChannel$190(long j, TLRPC.TL_updates_channelDifferenceTooLong tL_updates_channelDifferenceTooLong) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.removeAllMessagesFromDialog, Long.valueOf(j), Boolean.TRUE, tL_updates_channelDifferenceTooLong);
    }

    public void putChannelViews(final LongSparseArray<SparseIntArray> longSparseArray, final LongSparseArray<SparseIntArray> longSparseArray2, final LongSparseArray<SparseArray<TLRPC.MessageReplies>> longSparseArray3, final boolean z) {
        if (isEmpty(longSparseArray) && isEmpty(longSparseArray2) && isEmpty(longSparseArray3)) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$putChannelViews$192(longSparseArray, longSparseArray2, longSparseArray3, z);
            }
        });
    }

    void lambda$updateRepliesMaxReadIdInternal$193(long j, long j2, int i, int i2, int i3) {
        getMessagesController().getTopicsController().updateMaxReadId(-j, j2, i, i2, i3);
    }

    private void resetForumBadgeIfNeed(long j) {
        LongSparseIntArray longSparseIntArray;
        SQLiteCursor sQLiteCursor = null;
        try {
            SQLiteDatabase sQLiteDatabase = this.database;
            Locale locale = Locale.ENGLISH;
            SQLiteCursor sQLiteCursorQueryFinalized = sQLiteDatabase.queryFinalized(String.format(locale, "SELECT topic_id FROM topics WHERE did = %d AND unread_count > 0", Long.valueOf(j)), new Object[0]);
            try {
                if (sQLiteCursorQueryFinalized.next()) {
                    longSparseIntArray = null;
                } else {
                    longSparseIntArray = new LongSparseIntArray();
                    longSparseIntArray.put(j, 0);
                }
                sQLiteCursorQueryFinalized.dispose();
                if (longSparseIntArray != null) {
                    this.database.executeFast(String.format(locale, "UPDATE dialogs SET unread_count = 0, unread_count_i = 0 WHERE did = %d", Long.valueOf(j))).stepThis().dispose();
                }
                updateFiltersReadCounter(longSparseIntArray, null, true);
                getMessagesController().processDialogsUpdateRead(longSparseIntArray, null);
            } catch (Throwable th) {
                th = th;
                sQLiteCursor = sQLiteCursorQueryFinalized;
                try {
                    checkSQLException(th);
                } finally {
                    if (sQLiteCursor != null) {
                        sQLiteCursor.dispose();
                    }
                }
            }
        } catch (Throwable th2) {
            th = th2;
        }
    }

    public void lambda$updateMessageVerifyFlags$196(ArrayList arrayList) throws Throwable {
        SQLiteDatabase sQLiteDatabase;
        SQLiteDatabase sQLiteDatabase2;
        boolean z = false;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                try {
                    SQLitePreparedStatement sQLitePreparedStatementExecuteFast = this.database.executeFast("UPDATE messages_v2 SET imp = ? WHERE mid = ? AND uid = ?");
                    try {
                        int size = arrayList.size();
                        for (int i = 0; i < size; i++) {
                            TLRPC.Message message = (TLRPC.Message) arrayList.get(i);
                            sQLitePreparedStatementExecuteFast.requery();
                            int i2 = message.stickerVerified;
                            sQLitePreparedStatementExecuteFast.bindInteger(1, i2 == 0 ? 1 : i2 == 2 ? 2 : 0);
                            sQLitePreparedStatementExecuteFast.bindInteger(2, message.id);
                            sQLitePreparedStatementExecuteFast.bindLong(3, MessageObject.getDialogId(message));
                            sQLitePreparedStatementExecuteFast.step();
                        }
                        sQLitePreparedStatementExecuteFast.dispose();
                        this.database.commitTransaction();
                    } catch (Exception e) {
                        e = e;
                        z = true;
                        sQLitePreparedStatement = sQLitePreparedStatementExecuteFast;
                        checkSQLException(e);
                        if (z && (sQLiteDatabase2 = this.database) != null) {
                            sQLiteDatabase2.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                    } catch (Throwable th) {
                        th = th;
                        z = true;
                        sQLitePreparedStatement = sQLitePreparedStatementExecuteFast;
                        if (z && (sQLiteDatabase = this.database) != null) {
                            sQLiteDatabase.commitTransaction();
                        }
                        if (sQLitePreparedStatement != null) {
                            sQLitePreparedStatement.dispose();
                        }
                        throw th;
                    }
                } catch (Exception e2) {
                    e = e2;
                    z = true;
                } catch (Throwable th2) {
                    th = th2;
                    z = true;
                }
            } catch (Exception e3) {
                e = e3;
            }
        } catch (Throwable th3) {
            th = th3;
        }
    }

    throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putMessages$201(java.util.ArrayList, boolean, boolean, int, boolean, int, long):void");
    }

    public void lambda$createOrEditTopic$200(long j, TLRPC.TL_forumTopic tL_forumTopic) {
        getMessagesController().getTopicsController().onTopicCreated(j, tL_forumTopic, false);
    }

    public void putMessages(ArrayList<TLRPC.Message> arrayList, boolean z, boolean z2, boolean z3, int i, int i2, long j) {
        putMessages(arrayList, z, z2, z3, i, false, i2, j);
    }

    public void putMessages(final ArrayList<TLRPC.Message> arrayList, final boolean z, boolean z2, final boolean z3, final int i, final boolean z4, final int i2, final long j) {
        if (arrayList.size() == 0) {
            return;
        }
        if (z2) {
            this.storageQueue.postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() throws Throwable {
                    this.f$0.lambda$putMessages$201(arrayList, z, z3, i, z4, i2, j);
                }
            });
        } else {
            lambda$putMessages$201(arrayList, z, z3, i, z4, i2, j);
        }
    }

    public void putEphemeralMessages(final ArrayList<TLRPC.EphemeralMessage> arrayList, final boolean z) {
        executeInStorageQueue(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$putEphemeralMessages$202(arrayList, z);
            }
        });
    }

    public void lambda$putEphemeralMessages$202(ArrayList<TLRPC.EphemeralMessage> arrayList, boolean z) {
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = null;
        if (z) {
            try {
                this.database.beginTransaction();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLitePreparedStatementExecuteFast != null) {
                    return;
                } else {
                    return;
                }
            } finally {
                if (sQLitePreparedStatementExecuteFast != null) {
                    sQLitePreparedStatementExecuteFast.dispose();
                }
            }
        }
        sQLitePreparedStatementExecuteFast = this.database.executeFast("INSERT OR REPLACE INTO ephemeral_messages (dialog_id, id, topic_id, date, data) VALUES (?, ?, ?, ?, ?);");
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            TLRPC.EphemeralMessage ephemeralMessage = arrayList.get(i);
            i++;
            TLRPC.EphemeralMessage ephemeralMessage2 = ephemeralMessage;
            sQLitePreparedStatementExecuteFast.requery();
            sQLitePreparedStatementExecuteFast.bindLong(1, DialogObject.getPeerDialogId(ephemeralMessage2.peer_id));
            sQLitePreparedStatementExecuteFast.bindInteger(2, ephemeralMessage2.id);
            sQLitePreparedStatementExecuteFast.bindInteger(3, 0);
            sQLitePreparedStatementExecuteFast.bindInteger(4, ephemeralMessage2.date);
            sQLitePreparedStatementExecuteFast.bindTlObject(5, ephemeralMessage2);
            sQLitePreparedStatementExecuteFast.step();
        }
        if (z) {
            this.database.commitTransaction();
        }
    }

    public void deleteEphemeralMessages(long j, int i) {
        LongSparseArray<ArrayList<Integer>> longSparseArray = new LongSparseArray<>(1);
        ArrayList<Integer> arrayList = new ArrayList<>(1);
        arrayList.add(Integer.valueOf(i));
        longSparseArray.put(j, arrayList);
        deleteEphemeralMessages(longSparseArray, false);
    }

    public void deleteEphemeralMessages(final LongSparseArray<ArrayList<Integer>> longSparseArray, final boolean z) {
        executeInStorageQueue(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$deleteEphemeralMessages$203(longSparseArray, z);
            }
        });
    }

    public void lambda$deleteEphemeralMessages$203(LongSparseArray<ArrayList<Integer>> longSparseArray, boolean z) {
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = null;
        if (z) {
            try {
                this.database.beginTransaction();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLitePreparedStatementExecuteFast != null) {
                    return;
                } else {
                    return;
                }
            } finally {
                if (sQLitePreparedStatementExecuteFast != null) {
                    sQLitePreparedStatementExecuteFast.dispose();
                }
            }
        }
        sQLitePreparedStatementExecuteFast = this.database.executeFast("DELETE FROM ephemeral_messages WHERE dialog_id = ? AND id = ?;");
        for (int i = 0; i < longSparseArray.size(); i++) {
            long jKeyAt = longSparseArray.keyAt(i);
            ArrayList<Integer> arrayListValueAt = longSparseArray.valueAt(i);
            if (arrayListValueAt != null) {
                for (int i2 = 0; i2 < arrayListValueAt.size(); i2++) {
                    sQLitePreparedStatementExecuteFast.requery();
                    sQLitePreparedStatementExecuteFast.bindLong(1, jKeyAt);
                    sQLitePreparedStatementExecuteFast.bindInteger(2, arrayListValueAt.get(i2).intValue());
                    sQLitePreparedStatementExecuteFast.step();
                }
            }
        }
        if (z) {
            this.database.commitTransaction();
        }
    }

    public void lambda$markMessageAsSendError$206(int i, TLRPC.Message message) {
        try {
            long j = message.id;
            if (MessageObject.isQuickReply(message)) {
                i = 5;
            }
            if (i == 5) {
                this.database.executeFast(String.format(Locale.US, "UPDATE quick_replies_messages SET send_state = 2 WHERE mid = %d AND topic_id = %d", Long.valueOf(j), Integer.valueOf(MessageObject.getQuickReplyId(this.currentAccount, message)))).stepThis().dispose();
                return;
            }
            SQLiteDatabase sQLiteDatabase = this.database;
            if (i == 1) {
                sQLiteDatabase.executeFast(String.format(Locale.US, "UPDATE scheduled_messages_v2 SET send_state = 2 WHERE mid = %d AND uid = %d", Long.valueOf(j), Long.valueOf(MessageObject.getDialogId(message)))).stepThis().dispose();
                return;
            }
            Locale locale = Locale.US;
            sQLiteDatabase.executeFast(String.format(locale, "UPDATE messages_v2 SET send_state = 2 WHERE mid = %d AND uid = %d", Long.valueOf(j), Long.valueOf(MessageObject.getDialogId(message)))).stepThis().dispose();
            this.database.executeFast(String.format(locale, "UPDATE messages_topics SET send_state = 2 WHERE mid = %d AND uid = %d", Long.valueOf(j), Long.valueOf(MessageObject.getDialogId(message)))).stepThis().dispose();
        } catch (Exception e) {
            checkSQLException(e);
        }
    }

    public void markMessageAsSendErrorWithParams(final TLRPC.Message message, long j, long j2) {
        final long clientUserId = getUserConfig().getClientUserId();
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$markMessageAsSendErrorWithParams$207(message, clientUserId);
            }
        });
    }

    void lambda$setMessageSeq$208(int i, int i2, int i3) {
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = null;
        try {
            try {
                sQLitePreparedStatementExecuteFast = this.database.executeFast("REPLACE INTO messages_seq VALUES(?, ?, ?)");
                sQLitePreparedStatementExecuteFast.requery();
                sQLitePreparedStatementExecuteFast.bindInteger(1, i);
                sQLitePreparedStatementExecuteFast.bindInteger(2, i2);
                sQLitePreparedStatementExecuteFast.bindInteger(3, i3);
                sQLitePreparedStatementExecuteFast.step();
                sQLitePreparedStatementExecuteFast.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLitePreparedStatementExecuteFast != null) {
                    sQLitePreparedStatementExecuteFast.dispose();
                }
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatementExecuteFast != null) {
                sQLitePreparedStatementExecuteFast.dispose();
            }
            throw th;
        }
    }

    void lambda$updateMessageStateAndIdInternal$209(TLRPC.TL_updates tL_updates) {
        getMessagesController().processUpdates(tL_updates, false);
    }

    public long[] updateMessageStateAndId(final long j, final long j2, final Integer num, final int i, final int i2, boolean z, final int i3, final int i4) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() throws Throwable {
                    this.f$0.lambda$updateMessageStateAndId$210(j, j2, num, i, i2, i3, i4);
                }
            });
            return null;
        }
        return lambda$updateMessageStateAndId$210(j, j2, num, i, i2, i3, i4);
    }

    public void updateMessageTopicId(final long j, final long j2, final int i) {
        executeInStorageQueue(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$updateMessageTopicId$211(i, j, j2);
            }
        });
    }

    public void lambda$markVoiceMessageContentAsRead$214(ArrayList arrayList, long j) {
        final ArrayList arrayList2 = new ArrayList();
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                SQLiteCursor sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM messages_v2 WHERE uid = %d AND mid IN (%s)", Long.valueOf(j), TextUtils.join(",", arrayList)), new Object[0]);
                while (sQLiteCursorQueryFinalized.next()) {
                    NativeByteBuffer nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized.byteBufferValue(0);
                    if (nativeByteBufferByteBufferValue != null) {
                        try {
                            TLRPC.Message messageTLdeserialize = TLRPC.Message.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false);
                            if (messageTLdeserialize != null) {
                                messageTLdeserialize.readAttachPath(nativeByteBufferByteBufferValue, getUserConfig().clientUserId);
                                if (!messageTLdeserialize.out && messageTLdeserialize.media_unread && MessageObject.isVoiceMessage(messageTLdeserialize)) {
                                    arrayList2.add(new MessageObject(this.currentAccount, messageTLdeserialize, false, false));
                                }
                            }
                            nativeByteBufferByteBufferValue.reuse();
                        } catch (Throwable th) {
                            nativeByteBufferByteBufferValue.reuse();
                            throw th;
                        }
                    }
                }
                sQLiteCursorQueryFinalized.dispose();
            } catch (Throwable th2) {
                if (0 != 0) {
                    sQLiteCursor.dispose();
                }
                throw th2;
            }
        } catch (Exception e) {
            checkSQLException(e);
            if (0 != 0) {
                sQLiteCursor.dispose();
            }
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$markVoiceMessageContentAsRead$213(arrayList2);
            }
        });
    }

    public public void lambda$markMessagesAsDeletedByRandoms$218(ArrayList arrayList) throws Throwable {
        MessagesStorage messagesStorage;
        Throwable th;
        SQLiteCursor sQLiteCursor = null;
        try {
            try {
                String strJoin = TextUtils.join(",", arrayList);
                int i = 0;
                SQLiteCursor sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid, uid FROM randoms_v2 WHERE random_id IN(%s)", strJoin), new Object[0]);
                try {
                    LongSparseArray longSparseArray = new LongSparseArray();
                    while (sQLiteCursorQueryFinalized.next()) {
                        long jLongValue = sQLiteCursorQueryFinalized.longValue(1);
                        ArrayList arrayList2 = (ArrayList) longSparseArray.get(jLongValue);
                        if (arrayList2 == null) {
                            arrayList2 = new ArrayList();
                            longSparseArray.put(jLongValue, arrayList2);
                        }
                        arrayList2.add(Integer.valueOf(sQLiteCursorQueryFinalized.intValue(0)));
                    }
                    sQLiteCursorQueryFinalized.dispose();
                    if (longSparseArray.isEmpty()) {
                        return;
                    }
                    int size = longSparseArray.size();
                    while (i < size) {
                        long jKeyAt = longSparseArray.keyAt(i);
                        final ArrayList<Integer> arrayList3 = (ArrayList) longSparseArray.valueAt(i);
                        AndroidUtilities.runOnUIThread(new Runnable() { 
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$markMessagesAsDeletedByRandoms$217(arrayList3);
                            }
                        });
                        MessagesStorage messagesStorage2 = this;
                        try {
                            messagesStorage2.updateDialogsWithReadMessagesInternal(arrayList3, null, null, null, null);
                            messagesStorage = messagesStorage2;
                            try {
                                messagesStorage.lambda$markMessagesAsDeleted$226(jKeyAt, arrayList3, true, 0, 0);
                                messagesStorage.lambda$updateDialogsWithDeletedMessages$225(jKeyAt, 0L, arrayList3, null);
                                i++;
                                this = messagesStorage;
                            } catch (Exception e) {
                                e = e;
                            }
                        } catch (Exception e2) {
                            e = e2;
                            messagesStorage = messagesStorage2;
                        }
                    }
                    return;
                } catch (Exception e3) {
                    e = e3;
                    messagesStorage = this;
                    sQLiteCursor = sQLiteCursorQueryFinalized;
                } catch (Throwable th2) {
                    th = th2;
                    sQLiteCursor = sQLiteCursorQueryFinalized;
                    if (sQLiteCursor != null) {
                        sQLiteCursor.dispose();
                        throw th;
                    }
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Exception e4) {
            e = e4;
            messagesStorage = this;
        }
        messagesStorage.checkSQLException(e);
        if (sQLiteCursor != null) {
            sQLiteCursor.dispose();
        }
    }

    public void lambda$broadcastScheduledMessagesChange$219(Long l, int i) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.scheduledMessagesUpdated, l, Integer.valueOf(i), Boolean.TRUE);
    }

    private void broadcastQuickRepliesMessagesChange(Long l, long j) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$broadcastQuickRepliesMessagesChange$220();
            }
        });
    }

    public void lambda$markMessagesAsDeletedInternal$221(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    public void lambda$markMessagesAsDeletedInternal$224(ArrayList arrayList) {
        HashSet<Long> hashSet = new HashSet<>();
        long[] jArr = new long[1];
        boolean z = false;
        for (int i = 0; i < arrayList.size(); i++) {
            if (getMediaDataController().processDeletedMessage(((Integer) arrayList.get(i)).intValue(), jArr)) {
                hashSet.add(Long.valueOf(jArr[0]));
                z = true;
            }
        }
        if (z) {
            getMessagesController().updateSavedReactionTags(hashSet);
        }
    }

    public java.util.ArrayList<java.lang.Long> lambda$markMessagesAsDeleted$228(long r23, int r25, boolean r26) {
        void lambda$markMessagesAsDeletedInternal$227(ArrayList arrayList) {
        getFileLoader().cancelLoadFiles(arrayList);
    }

    public ArrayList<Long> markMessagesAsDeleted(final long j, final int i, boolean z, final boolean z2) {
        if (z) {
            this.storageQueue.postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$markMessagesAsDeleted$228(j, i, z2);
                }
            });
            return null;
        }
        return lambda$markMessagesAsDeleted$228(j, i, z2);
    }

    private void fixUnsupportedMedia(TLRPC.Message message) {
        if (message == null) {
            return;
        }
        TLRPC.MessageMedia messageMedia = message.media;
        if (messageMedia instanceof TLRPC.TL_messageMediaUnsupported_old) {
            if (messageMedia.bytes.length == 0) {
                messageMedia.bytes = Utilities.intToBytes(228);
            }
        } else if (messageMedia instanceof TLRPC.TL_messageMediaUnsupported) {
            TLRPC.TL_messageMediaUnsupported_old tL_messageMediaUnsupported_old = new TLRPC.TL_messageMediaUnsupported_old();
            message.media = tL_messageMediaUnsupported_old;
            tL_messageMediaUnsupported_old.bytes = Utilities.intToBytes(228);
            message.flags |= 512;
        }
    }

    private void doneHolesInTable(String str, long j, int i, long j2) {
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast;
        if (j2 != 0) {
            SQLiteDatabase sQLiteDatabase = this.database;
            if (i == 0) {
                sQLiteDatabase.executeFast(String.format(Locale.US, "DELETE FROM " + str + " WHERE uid = %d AND topic_id = %d", Long.valueOf(j), Long.valueOf(j2))).stepThis().dispose();
            } else {
                sQLiteDatabase.executeFast(String.format(Locale.US, "DELETE FROM " + str + " WHERE uid = %d AND topic_id = %d AND start = 0", Long.valueOf(j), Long.valueOf(j2))).stepThis().dispose();
            }
        } else {
            SQLiteDatabase sQLiteDatabase2 = this.database;
            if (i == 0) {
                sQLiteDatabase2.executeFast(String.format(Locale.US, "DELETE FROM " + str + " WHERE uid = %d", Long.valueOf(j))).stepThis().dispose();
            } else {
                sQLiteDatabase2.executeFast(String.format(Locale.US, "DELETE FROM " + str + " WHERE uid = %d AND start = 0", Long.valueOf(j))).stepThis().dispose();
            }
        }
        SQLiteDatabase sQLiteDatabase3 = this.database;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                if (j2 != 0) {
                    sQLitePreparedStatementExecuteFast = sQLiteDatabase3.executeFast("REPLACE INTO " + str + " VALUES(?, ?, ?, ?)");
                } else {
                    sQLitePreparedStatementExecuteFast = sQLiteDatabase3.executeFast("REPLACE INTO " + str + " VALUES(?, ?, ?)");
                }
                sQLitePreparedStatement = sQLitePreparedStatementExecuteFast;
                sQLitePreparedStatement.requery();
                sQLitePreparedStatement.bindLong(1, j);
                int i2 = 2;
                if (j2 != 0) {
                    sQLitePreparedStatement.bindLong(2, j2);
                    i2 = 3;
                }
                sQLitePreparedStatement.bindInteger(i2, 1);
                sQLitePreparedStatement.bindInteger(i2 + 1, 1);
                sQLitePreparedStatement.step();
                sQLitePreparedStatement.dispose();
            } catch (Exception e) {
                throw e;
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatement != null) {
                sQLitePreparedStatement.dispose();
            }
            throw th;
        }
    }

    void lambda$replaceMessageIfExists$229(MessageObject messageObject, ArrayList arrayList) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.replaceMessagesObjects, Long.valueOf(messageObject.getDialogId()), arrayList);
    }

    public void lambda$loadMessageAttachPaths$232(ArrayList arrayList, Runnable runnable) {
        NativeByteBuffer nativeByteBufferByteBufferValue;
        long clientUserId = getUserConfig().getClientUserId();
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            MessageObject messageObject = (MessageObject) obj;
            if (!messageObject.scheduled && !messageObject.isQuickReply()) {
                SQLiteCursor sQLiteCursorQueryFinalized = null;
                try {
                    try {
                        sQLiteCursorQueryFinalized = this.database.queryFinalized("SELECT data FROM messages_v2 WHERE uid = ? AND mid = ?", Long.valueOf(messageObject.getDialogId()), Integer.valueOf(messageObject.getId()));
                        if (sQLiteCursorQueryFinalized.next() && (nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized.byteBufferValue(0)) != null) {
                            TLRPC.Message messageTLdeserialize = TLRPC.Message.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false);
                            messageTLdeserialize.readAttachPath(nativeByteBufferByteBufferValue, clientUserId);
                            nativeByteBufferByteBufferValue.reuse();
                            messageObject.messageOwner.attachPath = messageTLdeserialize.attachPath;
                            messageObject.checkMediaExistance();
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                        if (sQLiteCursorQueryFinalized != null) {
                        }
                    }
                    sQLiteCursorQueryFinalized.dispose();
                } catch (Throwable th) {
                    if (sQLiteCursorQueryFinalized != null) {
                        sQLiteCursorQueryFinalized.dispose();
                    }
                    throw th;
                }
            }
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public void putMessages(final TLRPC.messages_Messages messages_messages, final long j, final int i, final int i2, final boolean z, final int i3, final long j2) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$putMessages$235(i3, messages_messages, j, j2, i, i2, z);
            }
        });
    }

    throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.lambda$putMessages$235(int, org.telegram.tgnet.TLRPC$messages_Messages, long, long, int, int, boolean):void");
    }

    public void lambda$getDialogs$236(LongSparseArray longSparseArray) {
        MediaDataController mediaDataController = getMediaDataController();
        mediaDataController.clearDraftsFolderIds();
        if (longSparseArray != null) {
            int size = longSparseArray.size();
            for (int i = 0; i < size; i++) {
                mediaDataController.setDraftFolderId(longSparseArray.keyAt(i), ((Integer) longSparseArray.valueAt(i)).intValue());
            }
        }
    }

    public static void createFirstHoles(long j, SQLitePreparedStatement sQLitePreparedStatement, SQLitePreparedStatement sQLitePreparedStatement2, int i, long j2) {
        int i2;
        int i3;
        sQLitePreparedStatement.requery();
        sQLitePreparedStatement.bindLong(1, j);
        if (j2 != 0) {
            sQLitePreparedStatement.bindLong(2, j2);
            i2 = 3;
        } else {
            i2 = 2;
        }
        int i4 = i2 + 1;
        sQLitePreparedStatement.bindInteger(i2, i == 1 ? 1 : 0);
        sQLitePreparedStatement.bindInteger(i4, i);
        sQLitePreparedStatement.step();
        for (int i5 = 0; i5 < 9; i5++) {
            sQLitePreparedStatement2.requery();
            sQLitePreparedStatement2.bindLong(1, j);
            if (j2 != 0) {
                sQLitePreparedStatement2.bindLong(2, j2);
                i3 = 3;
            } else {
                i3 = 2;
            }
            int i6 = i3 + 1;
            sQLitePreparedStatement2.bindInteger(i3, i5);
            int i7 = i3 + 2;
            sQLitePreparedStatement2.bindInteger(i6, i == 1 ? 1 : 0);
            sQLitePreparedStatement2.bindInteger(i7, i);
            sQLitePreparedStatement2.step();
        }
    }

    public void updateDialogData(final TLRPC.Dialog dialog) {
        if (dialog == null) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$updateDialogData$238(dialog);
            }
        });
    }

    throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesStorage.putDialogsInternal(org.telegram.tgnet.TLRPC$messages_Dialogs, int):void");
    }

    private int getDialogFolderIdInternal(long j) {
        SQLiteCursor sQLiteCursorQueryFinalized = null;
        try {
            try {
                int iIntValue = -1;
                if (this.unknownDialogsIds.get(j) == null) {
                    sQLiteCursorQueryFinalized = this.database.queryFinalized("SELECT folder_id FROM dialogs WHERE did = ?", Long.valueOf(j));
                    iIntValue = sQLiteCursorQueryFinalized.next() ? sQLiteCursorQueryFinalized.intValue(0) : -1;
                    sQLiteCursorQueryFinalized.dispose();
                }
                return iIntValue;
            } catch (Exception e) {
                checkSQLException(e);
                return 0;
            }
        } finally {
            if (sQLiteCursorQueryFinalized != null) {
                sQLiteCursorQueryFinalized.dispose();
            }
        }
    }

    public void getDialogFolderId(final long j, final IntCallback intCallback) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getDialogFolderId$240(j, intCallback);
            }
        });
    }

    public void lambda$setDialogsFolderId$241(ArrayList arrayList, ArrayList arrayList2, int i, long j) throws Throwable {
        boolean z;
        SQLitePreparedStatement sQLitePreparedStatement = null;
        try {
            try {
                this.database.beginTransaction();
                SQLitePreparedStatement sQLitePreparedStatementExecuteFast = this.database.executeFast("UPDATE dialogs SET folder_id = ?, pinned = ? WHERE did = ?");
                try {
                    if (arrayList != null) {
                        int size = arrayList.size();
                        z = false;
                        for (int i2 = 0; i2 < size; i2++) {
                            TLRPC.TL_folderPeer tL_folderPeer = (TLRPC.TL_folderPeer) arrayList.get(i2);
                            long peerDialogId = DialogObject.getPeerDialogId(tL_folderPeer.peer);
                            sQLitePreparedStatementExecuteFast.requery();
                            sQLitePreparedStatementExecuteFast.bindInteger(1, tL_folderPeer.folder_id);
                            if (tL_folderPeer.folder_id == 1) {
                                z = true;
                            }
                            sQLitePreparedStatementExecuteFast.bindInteger(2, 0);
                            sQLitePreparedStatementExecuteFast.bindLong(3, peerDialogId);
                            sQLitePreparedStatementExecuteFast.step();
                            this.unknownDialogsIds.remove(peerDialogId);
                        }
                    } else if (arrayList2 != null) {
                        int size2 = arrayList2.size();
                        z = false;
                        for (int i3 = 0; i3 < size2; i3++) {
                            TLRPC.TL_inputFolderPeer tL_inputFolderPeer = (TLRPC.TL_inputFolderPeer) arrayList2.get(i3);
                            long peerDialogId2 = DialogObject.getPeerDialogId(tL_inputFolderPeer.peer);
                            sQLitePreparedStatementExecuteFast.requery();
                            sQLitePreparedStatementExecuteFast.bindInteger(1, tL_inputFolderPeer.folder_id);
                            if (tL_inputFolderPeer.folder_id == 1) {
                                z = true;
                            }
                            sQLitePreparedStatementExecuteFast.bindInteger(2, 0);
                            sQLitePreparedStatementExecuteFast.bindLong(3, peerDialogId2);
                            sQLitePreparedStatementExecuteFast.step();
                            this.unknownDialogsIds.remove(peerDialogId2);
                        }
                    } else {
                        sQLitePreparedStatementExecuteFast.requery();
                        sQLitePreparedStatementExecuteFast.bindInteger(1, i);
                        boolean z2 = i == 1;
                        sQLitePreparedStatementExecuteFast.bindInteger(2, 0);
                        sQLitePreparedStatementExecuteFast.bindLong(3, j);
                        sQLitePreparedStatementExecuteFast.step();
                        z = z2;
                    }
                    sQLitePreparedStatementExecuteFast.dispose();
                    this.database.commitTransaction();
                    if (!z) {
                        lambda$checkIfFolderEmpty$243(1);
                    }
                    resetAllUnreadCounters(false);
                    SQLiteDatabase sQLiteDatabase = this.database;
                    if (sQLiteDatabase != null) {
                        sQLiteDatabase.commitTransaction();
                    }
                } catch (Exception e) {
                    e = e;
                    sQLitePreparedStatement = sQLitePreparedStatementExecuteFast;
                    checkSQLException(e);
                    SQLiteDatabase sQLiteDatabase2 = this.database;
                    if (sQLiteDatabase2 != null) {
                        sQLiteDatabase2.commitTransaction();
                    }
                    if (sQLitePreparedStatement != null) {
                        sQLitePreparedStatement.dispose();
                    }
                } catch (Throwable th) {
                    th = th;
                    sQLitePreparedStatement = sQLitePreparedStatementExecuteFast;
                    SQLiteDatabase sQLiteDatabase3 = this.database;
                    if (sQLiteDatabase3 != null) {
                        sQLiteDatabase3.commitTransaction();
                    }
                    if (sQLitePreparedStatement != null) {
                        sQLitePreparedStatement.dispose();
                    }
                    throw th;
                }
            } catch (Throwable th2) {
                th = th2;
            }
        } catch (Exception e2) {
            e = e2;
        }
    }

    public void lambda$checkIfFolderEmpty$243(final int i) {
        boolean z;
        SQLiteCursor sQLiteCursorQueryFinalized = null;
        try {
            try {
                sQLiteCursorQueryFinalized = this.database.queryFinalized("SELECT did FROM dialogs WHERE folder_id = ?", Integer.valueOf(i));
                while (true) {
                    if (!sQLiteCursorQueryFinalized.next()) {
                        z = true;
                        break;
                    }
                    z = false;
                    long jLongValue = sQLiteCursorQueryFinalized.longValue(0);
                    if (!DialogObject.isUserDialog(jLongValue) && !DialogObject.isEncryptedDialog(jLongValue)) {
                        TLRPC.Chat chat = getChat(-jLongValue);
                        if (!ChatObject.isNotInChat(chat) && chat.migrated_to == null) {
                            break;
                        }
                    } else {
                        break;
                        break;
                    }
                }
                sQLiteCursorQueryFinalized.dispose();
                if (z) {
                    AndroidUtilities.runOnUIThread(new Runnable() { 
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$checkIfFolderEmptyInternal$242(i);
                        }
                    });
                    this.database.executeFast("DELETE FROM dialogs WHERE did = " + DialogObject.makeFolderDialogId(i)).stepThis().dispose();
                }
                sQLiteCursorQueryFinalized.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursorQueryFinalized != null) {
                    sQLiteCursorQueryFinalized.dispose();
                }
            }
        } catch (Throwable th) {
            if (sQLiteCursorQueryFinalized != null) {
                sQLiteCursorQueryFinalized.dispose();
            }
            throw th;
        }
    }

    public public void lambda$resetAllUnreadCounters$247() {
        ArrayList<MessagesController.DialogFilter> arrayList = getMessagesController().dialogFilters;
        int size = arrayList.size();
        for (int i = 0; i < size; i++) {
            arrayList.get(i).unreadCount = arrayList.get(i).pendingUnreadCount;
        }
        this.mainUnreadCount = this.pendingMainUnreadCount;
        this.archiveUnreadCount = this.pendingArchiveUnreadCount;
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_READ_DIALOG_MESSAGE));
    }

    public void setDialogPinned(final long j, final int i) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setDialogPinned$248(i, j);
            }
        });
    }

    public void lambda$setDialogsPinned$249(ArrayList arrayList, ArrayList arrayList2) {
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = null;
        try {
            try {
                sQLitePreparedStatementExecuteFast = this.database.executeFast("UPDATE dialogs SET pinned = ? WHERE did = ?");
                int size = arrayList.size();
                for (int i = 0; i < size; i++) {
                    sQLitePreparedStatementExecuteFast.requery();
                    sQLitePreparedStatementExecuteFast.bindInteger(1, ((Integer) arrayList2.get(i)).intValue());
                    sQLitePreparedStatementExecuteFast.bindLong(2, ((Long) arrayList.get(i)).longValue());
                    sQLitePreparedStatementExecuteFast.step();
                }
                sQLitePreparedStatementExecuteFast.dispose();
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLitePreparedStatementExecuteFast != null) {
                    sQLitePreparedStatementExecuteFast.dispose();
                }
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatementExecuteFast != null) {
                sQLitePreparedStatementExecuteFast.dispose();
            }
            throw th;
        }
    }

    public void putDialogs(final TLRPC.messages_Dialogs messages_dialogs, final int i) {
        if (messages_dialogs.dialogs.isEmpty()) {
            return;
        }
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$putDialogs$250(messages_dialogs, i);
            }
        });
    }

    public void lambda$getDialogMaxMessageId$252(long j, final IntCallback intCallback) {
        final int[] iArr = new int[1];
        SQLiteCursor sQLiteCursorQueryFinalized = null;
        try {
            try {
                sQLiteCursorQueryFinalized = this.database.queryFinalized("SELECT MAX(mid) FROM messages_v2 WHERE uid = " + j, new Object[0]);
                if (sQLiteCursorQueryFinalized.next()) {
                    iArr[0] = sQLiteCursorQueryFinalized.intValue(0);
                }
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursorQueryFinalized != null) {
                }
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        intCallback.run(iArr[0]);
                    }
                });
            }
            sQLiteCursorQueryFinalized.dispose();
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    intCallback.run(iArr[0]);
                }
            });
        } catch (Throwable th) {
            if (sQLiteCursorQueryFinalized != null) {
                sQLiteCursorQueryFinalized.dispose();
            }
            throw th;
        }
    }

    void lambda$getDialogReadMax$253(boolean z, long j, Integer[] numArr, CountDownLatch countDownLatch) {
        SQLiteDatabase sQLiteDatabase = this.database;
        SQLiteCursor sQLiteCursorQueryFinalized = null;
        try {
            try {
                if (z) {
                    sQLiteCursorQueryFinalized = sQLiteDatabase.queryFinalized("SELECT outbox_max FROM dialogs WHERE did = " + j, new Object[0]);
                    if (sQLiteCursorQueryFinalized.next()) {
                        numArr[0] = Integer.valueOf(sQLiteCursorQueryFinalized.intValue(0));
                    }
                } else {
                    sQLiteCursorQueryFinalized = sQLiteDatabase.queryFinalized("SELECT last_mid, inbox_max FROM dialogs WHERE did = " + j, new Object[0]);
                    if (sQLiteCursorQueryFinalized.next()) {
                        int iIntValue = sQLiteCursorQueryFinalized.intValue(0);
                        int iIntValue2 = sQLiteCursorQueryFinalized.intValue(1);
                        if (iIntValue2 > iIntValue) {
                            numArr[0] = 0;
                        } else {
                            numArr[0] = Integer.valueOf(iIntValue2);
                        }
                    }
                }
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursorQueryFinalized != null) {
                }
                countDownLatch.countDown();
            }
            sQLiteCursorQueryFinalized.dispose();
            countDownLatch.countDown();
        } catch (Throwable th) {
            if (sQLiteCursorQueryFinalized != null) {
                sQLiteCursorQueryFinalized.dispose();
            }
            throw th;
        }
    }

    public int getChannelPtsSync(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final Integer[] numArr = {0};
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getChannelPtsSync$254(j, numArr, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return numArr[0].intValue();
    }

    public void lambda$getUserSync$255(TLRPC.User[] userArr, long j, CountDownLatch countDownLatch) {
        userArr[0] = getUser(j);
        countDownLatch.countDown();
    }

    public TLRPC.Chat getChatSync(final long j) {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        final TLRPC.Chat[] chatArr = new TLRPC.Chat[1];
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getChatSync$256(chatArr, j, countDownLatch);
            }
        });
        try {
            countDownLatch.await();
        } catch (Exception e) {
            checkSQLException(e);
        }
        return chatArr[0];
    }

    public int $r8$lambda$85DRfho3RvgkgylWGJ2zrHEP96s(DialogsSearchAdapter.DialogSearchResult dialogSearchResult, DialogsSearchAdapter.DialogSearchResult dialogSearchResult2) {
        int i = dialogSearchResult.date;
        int i2 = dialogSearchResult2.date;
        if (i < i2) {
            return 1;
        }
        return i > i2 ? -1 : 0;
    }

    public ArrayList<Integer> getCachedMessagesInRange(long j, int i, int i2) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        SQLiteCursor sQLiteCursorQueryFinalized = null;
        try {
            try {
                sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT mid FROM messages_v2 WHERE uid = %d AND date >= %d AND date <= %d", Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2)), new Object[0]);
                while (sQLiteCursorQueryFinalized.next()) {
                    try {
                        arrayList.add(Integer.valueOf(sQLiteCursorQueryFinalized.intValue(0)));
                    } catch (Exception e) {
                        checkSQLException(e);
                    }
                }
                sQLiteCursorQueryFinalized.dispose();
            } catch (Exception e2) {
                checkSQLException(e2);
                if (0 != 0) {
                }
                return arrayList;
            }
            sQLiteCursorQueryFinalized.dispose();
            return arrayList;
        } catch (Throwable th) {
            if (0 != 0) {
                sQLiteCursorQueryFinalized.dispose();
            }
            throw th;
        }
    }

    public void updateUnreadReactionsCount(long j, long j2, int i) {
        updateUnreadReactionsCount(j, j2, i, false);
    }

    public void updateUnreadReactionsCount(long j, long j2, int i, boolean z) {
        updateUnreadReactionsCountInternal("reaction_mentions", "reaction_mentions_topics", "unread_reactions", "unread_reactions", j, j2, i, z);
    }

    public void updateUnreadPollVotesCount(long j, long j2, int i) {
        updateUnreadPollVotesCount(j, j2, i, false);
    }

    public void updateUnreadPollVotesCount(long j, long j2, int i, boolean z) {
        updateUnreadReactionsCountInternal("poll_votes_mentions", "poll_votes_mentions_topics", "unread_poll_votes", "unread_poll_votes", j, j2, i, z);
    }

    private void updateUnreadReactionsCountInternal(final String str, final String str2, final String str3, final String str4, final long j, final long j2, final int i, final boolean z) {
        this.storageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$updateUnreadReactionsCountInternal$258(j2, z, str4, j, i, str2, str3, str);
            }
        });
    }

    void lambda$markMessagePollVotesAsRead$260(long j, long j2, int i) throws Throwable {
        markMessageReactionsAsReadInternal("poll_votes_mentions", "poll_votes_mentions_topics", j, j2, i, false);
    }

    public void markMessagePollVotesAsRead(final long j, final long j2, final int i) {
        executeInStorageQueue(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$markMessagePollVotesAsRead$260(j, j2, i);
            }
        });
    }

    void lambda$putGiftChatThemes$262(List list) {
        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = null;
        try {
            try {
                try {
                    sQLitePreparedStatementExecuteFast = this.database.executeFast("REPLACE INTO gift_themes VALUES(?, ?)");
                    Iterator it = list.iterator();
                    while (it.hasNext()) {
                        TLRPC.ChatTheme chatTheme = (TLRPC.ChatTheme) it.next();
                        if (chatTheme instanceof TLRPC.TL_chatThemeUniqueGift) {
                            TLRPC.TL_chatThemeUniqueGift tL_chatThemeUniqueGift = (TLRPC.TL_chatThemeUniqueGift) chatTheme;
                            sQLitePreparedStatementExecuteFast.requery();
                            sQLitePreparedStatementExecuteFast.bindString(1, tL_chatThemeUniqueGift.gift.slug);
                            NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_chatThemeUniqueGift.getObjectSize());
                            tL_chatThemeUniqueGift.serializeToStream(nativeByteBuffer);
                            sQLitePreparedStatementExecuteFast.bindByteBuffer(2, nativeByteBuffer);
                            nativeByteBuffer.reuse();
                            sQLitePreparedStatementExecuteFast.step();
                        }
                    }
                    sQLitePreparedStatementExecuteFast.dispose();
                } catch (Exception e) {
                    FileLog.e(e);
                    if (sQLitePreparedStatementExecuteFast == null) {
                        return;
                    }
                    sQLitePreparedStatementExecuteFast.dispose();
                }
            } catch (SQLiteException e2) {
                checkSQLException(e2);
                if (sQLitePreparedStatementExecuteFast == null) {
                    return;
                }
                sQLitePreparedStatementExecuteFast.dispose();
            }
        } catch (Throwable th) {
            if (sQLitePreparedStatementExecuteFast != null) {
                sQLitePreparedStatementExecuteFast.dispose();
            }
            throw th;
        }
    }

    public void loadGiftChatTheme(final Utilities.Callback<List<TLRPC.TL_chatThemeUniqueGift>> callback) {
        executeInStorageQueue(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$loadGiftChatTheme$265(callback);
            }
        });
    }

    void lambda$loadStoryAlbumsCache$267(long j, Consumer consumer) {
        ArrayList arrayList = new ArrayList();
        SQLiteCursor sQLiteCursorQueryFinalized = null;
        try {
            try {
                sQLiteCursorQueryFinalized = this.database.queryFinalized(String.format(Locale.US, "SELECT data FROM profile_stories_albums WHERE dialog_id = %d ORDER BY order_index ASC", Long.valueOf(j)), new Object[0]);
                while (sQLiteCursorQueryFinalized.next()) {
                    NativeByteBuffer nativeByteBufferByteBufferValue = sQLiteCursorQueryFinalized.byteBufferValue(0);
                    if (nativeByteBufferByteBufferValue != null) {
                        TL_stories.TL_storyAlbum tL_storyAlbumTLdeserialize = TL_stories.TL_storyAlbum.TLdeserialize(nativeByteBufferByteBufferValue, nativeByteBufferByteBufferValue.readInt32(false), false);
                        nativeByteBufferByteBufferValue.reuse();
                        if (tL_storyAlbumTLdeserialize != null) {
                            arrayList.add(StoriesController.StoryAlbum.from(tL_storyAlbumTLdeserialize));
                        }
                    }
                }
            } catch (Exception e) {
                checkSQLException(e);
                if (sQLiteCursorQueryFinalized != null) {
                }
                consumer.accept(arrayList);
            }
            sQLiteCursorQueryFinalized.dispose();
            consumer.accept(arrayList);
        } catch (Throwable th) {
            if (sQLiteCursorQueryFinalized != null) {
                sQLiteCursorQueryFinalized.dispose();
            }
            throw th;
        }
    }

    public SQLiteCursor createLoadStoriesCursor(long j, int i, int i2) {
        return this.database.queryFinalized(String.format(Locale.US, "SELECT data, seen, pin FROM profile_stories JOIN profile_stories_albums_links ON profile_stories.story_id = profile_stories_albums_links.story_id WHERE profile_stories.dialog_id = %d AND profile_stories_albums_links.dialog_id = %d  AND profile_stories_albums_links.album_id = %d AND profile_stories.type = %d ORDER BY profile_stories_albums_links.order_index ASC;", Long.valueOf(j), Long.valueOf(j), Integer.valueOf(i), Integer.valueOf(i2)), new Object[0]);
    }

    public boolean isMonoForum(long j) {
        return isForum(j, 4);
    }

    public int getForumTypeFlags(long j) {
        int i = this.dialogIsForumTyped.get(j, -1);
        if (i != -1) {
            return i;
        }
        int i2 = 0;
        if (j < 0) {
            TLRPC.Chat chat = getChat(-j);
            if (chat != null && chat.forum) {
                i2 = chat.forum_tabs ? 3 : 1;
            }
            if (chat != null && chat.monoforum) {
                i2 |= 4;
            }
        } else {
            TLRPC.User user = getUser(j);
            if (user != null && user.bot_forum_view) {
                i2 = 8;
            }
        }
        this.dialogIsForumTyped.put(j, i2);
        return i2;
    }

    private static boolean isMessageActionTypeWithReply(TLRPC.MessageAction messageAction) {
        return (messageAction instanceof TLRPC.TL_messageActionPinMessage) || (messageAction instanceof TLRPC.TL_messageActionPaymentSent) || (messageAction instanceof TLRPC.TL_messageActionGameScore) || (messageAction instanceof TLRPC.TL_messageActionSuggestedPostApproval) || (messageAction instanceof TLRPC.TL_messageActionPollAppendAnswer) || (messageAction instanceof TLRPC.TL_messageActionPollDeleteAnswer);
    }

    public boolean isForum(long j, int i) {
        return (getForumTypeFlags(j) & i) != 0;
    }

    private void isForumCacheInvalidate(long j) {
        this.dialogIsForumTyped.delete(j);
    }

    public static class TopicKey {
        public long dialogId;
        public long topicId;

        public static TopicKey of(long j, long j2) {
            TopicKey topicKey = new TopicKey();
            topicKey.dialogId = j;
            topicKey.topicId = j2;
            return topicKey;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && getClass() == obj.getClass()) {
                TopicKey topicKey = (TopicKey) obj;
                if (this.dialogId == topicKey.dialogId && this.topicId == topicKey.topicId) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(Long.valueOf(this.dialogId), Long.valueOf(this.topicId));
        }

        public String toString() {
            return "TopicKey{dialogId=" + this.dialogId + ", topicId=" + this.topicId + '}';
        }
    }
}
