package com.exteragram.messenger.utils.chats;

import androidx.collection.LongSparseArray;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.TabCounterMode;
import com.exteragram.messenger.plugins.PluginsConstants;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationBadge;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.TLRPC;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 %2\u00020\u0001:\u0002$%B\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\f\u001a\u00020\u0003J\u000e\u0010\r\u001a\u00020\u00032\u0006\u0010\u000e\u001a\u00020\u000fJp\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u000f0\u00132\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u00152\f\u0010\u0017\u001a\b\u0012\u0004\u0012\u00020\u00160\u00152\u0006\u0010\u0018\u001a\u00020\u00192\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u001b0\u00152\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00152\f\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u000b0\u00152\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u00030\u0015J\u0006\u0010\u001f\u001a\u00020\u0011J(\u0010 \u001a\u00020\u00032\f\u0010!\u001a\b\u0012\u0004\u0012\u00020\"0\u00132\u0006\u0010#\u001a\u00020\u00032\b\u0010\u000e\u001a\u0004\u0018\u00010\u000fH\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R*\u0010\u0007\u001a\u001e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u00030\bj\u000e\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u0003`\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006&"}, d2 = {"Lcom/exteragram/messenger/utils/chats/FolderCounters;", _UrlKt.FRAGMENT_ENCODE_SET, "account", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(I)V", "mainCount", "filterCounts", "Ljava/util/HashMap;", "Lkotlin/collections/HashMap;", "recountScheduled", _UrlKt.FRAGMENT_ENCODE_SET, "getMainUnreadCount", "getUnreadCount", "filter", "Lorg/telegram/messenger/MessagesController$DialogFilter;", PluginsConstants.UPDATE, _UrlKt.FRAGMENT_ENCODE_SET, "filters", _UrlKt.FRAGMENT_ENCODE_SET, "usersDict", "Landroidx/collection/LongSparseArray;", "Lorg/telegram/tgnet/TLRPC$User;", "encUsersDict", "encryptedChatsByUsersCount", "Lorg/telegram/messenger/support/LongSparseIntArray;", "chatsDict", "Lorg/telegram/tgnet/TLRPC$Chat;", "mutedDialogs", "archivedDialogs", "dialogsWithMentions", "scheduleRecount", NotificationBadge.NewHtcHomeBadger.COUNT, "dialogs", "Lcom/exteragram/messenger/utils/chats/FolderCounters$UnreadDialog;", "flags", "UnreadDialog", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class FolderCounters {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final FolderCounters[] instances = new FolderCounters[16];
    private static final Object[] lockObjects;
    private final int account;
    private HashMap<Integer, Integer> filterCounts;
    private int mainCount;
    private boolean recountScheduled;

    public /* synthetic */ FolderCounters(int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(i);
    }

    @JvmStatic
    public static final FolderCounters getInstance(int i) {
        return INSTANCE.getInstance(i);
    }

    private FolderCounters(int i) {
        this.account = i;
        this.filterCounts = new HashMap<>();
    }

    @Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\n\b\u0002\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011¨\u0006\u0012"}, d2 = {"Lcom/exteragram/messenger/utils/chats/FolderCounters$UnreadDialog;", _UrlKt.FRAGMENT_ENCODE_SET, "id", _UrlKt.FRAGMENT_ENCODE_SET, "typeFlags", _UrlKt.FRAGMENT_ENCODE_SET, "weight", "archived", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(JIIZ)V", "getId", "()J", "getTypeFlags", "()I", "getWeight", "getArchived", "()Z", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class UnreadDialog {
        private final boolean archived;
        private final long id;
        private final int typeFlags;
        private final int weight;

        public UnreadDialog(long j, int i, int i2, boolean z) {
            this.id = j;
            this.typeFlags = i;
            this.weight = i2;
            this.archived = z;
        }

        public final long getId() {
            return this.id;
        }

        public final int getTypeFlags() {
            return this.typeFlags;
        }

        public final int getWeight() {
            return this.weight;
        }

        public final boolean getArchived() {
            return this.archived;
        }
    }

    public final int getMainUnreadCount() {
        return INSTANCE.isUnmutedOnly() ? this.mainCount : MessagesStorage.getInstance(this.account).getMainUnreadCount();
    }

    public final int getUnreadCount(MessagesController.DialogFilter filter) {
        if (!INSTANCE.isUnmutedOnly()) {
            return filter.unreadCount;
        }
        Integer num = this.filterCounts.get(Integer.valueOf(filter.id));
        if (num != null) {
            return num.intValue();
        }
        return 0;
    }

    public final void update(List<? extends MessagesController.DialogFilter> filters, LongSparseArray<TLRPC.User> usersDict, LongSparseArray<TLRPC.User> encUsersDict, LongSparseIntArray encryptedChatsByUsersCount, LongSparseArray<TLRPC.Chat> chatsDict, LongSparseArray<Boolean> mutedDialogs, LongSparseArray<Boolean> archivedDialogs, LongSparseArray<Integer> dialogsWithMentions) {
        int i;
        int i2;
        if (INSTANCE.isUnmutedOnly()) {
            ArrayList arrayList = new ArrayList();
            int size = usersDict.size();
            for (int i3 = 0; i3 < size; i3++) {
                TLRPC.User userValueAt = usersDict.valueAt(i3);
                if (!mutedDialogs.containsKey(userValueAt.id)) {
                    if (ChatObject.isUserCollapsedInCommunity(chatsDict, userValueAt)) {
                        i2 = MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS;
                    } else if (userValueAt.bot) {
                        i2 = MessagesController.DIALOG_FILTER_FLAG_BOTS;
                    } else if (userValueAt.self || userValueAt.contact) {
                        i2 = MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
                    } else {
                        i2 = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                    }
                    int i4 = i2;
                    long j = userValueAt.id;
                    arrayList.add(new UnreadDialog(j, i4, 1, archivedDialogs.containsKey(j)));
                }
            }
            int size2 = encUsersDict.size();
            for (int i5 = 0; i5 < size2; i5++) {
                TLRPC.User userValueAt2 = encUsersDict.valueAt(i5);
                int i6 = encryptedChatsByUsersCount.get(userValueAt2.id, 0);
                if (i6 != 0 && !mutedDialogs.containsKey(userValueAt2.id)) {
                    int i7 = (userValueAt2.self || userValueAt2.contact) ? MessagesController.DIALOG_FILTER_FLAG_CONTACTS : MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                    long j2 = userValueAt2.id;
                    arrayList.add(new UnreadDialog(j2, i7, i6, archivedDialogs.containsKey(j2)));
                }
            }
            int size3 = chatsDict.size();
            for (int i8 = 0; i8 < size3; i8++) {
                TLRPC.Chat chatValueAt = chatsDict.valueAt(i8);
                if (chatValueAt != null && !ChatObject.isCommunity(chatValueAt)) {
                    long j3 = -chatValueAt.id;
                    if (!mutedDialogs.containsKey(j3) || dialogsWithMentions.containsKey(j3)) {
                        if (ChatObject.isChatCollapsedInCommunity(chatsDict, chatValueAt)) {
                            i = MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS;
                        } else {
                            i = (!ChatObject.isChannel(chatValueAt) || chatValueAt.megagroup) ? MessagesController.DIALOG_FILTER_FLAG_GROUPS : MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
                        }
                        arrayList.add(new UnreadDialog(j3, i, 1, archivedDialogs.containsKey(j3)));
                    }
                }
            }
            final int iCount = count(arrayList, MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS | MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED, null);
            final HashMap map = new HashMap();
            for (MessagesController.DialogFilter dialogFilter : filters) {
                map.put(Integer.valueOf(dialogFilter.id), Integer.valueOf(count(arrayList, dialogFilter.flags, dialogFilter)));
            }
            AndroidUtilities.runOnUIThread(() -> {
                FolderCounters.this.mainCount = iCount;
                FolderCounters.this.filterCounts = map;
            });
        }
    }

    public final void scheduleRecount() {
        if (!INSTANCE.isUnmutedOnly() || this.recountScheduled) {
            return;
        }
        this.recountScheduled = true;
        final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.account);
        messagesStorage.getStorageQueue().postRunnable(() -> {
            FolderCounters.this.recountScheduled = false;
            messagesStorage.resetAllUnreadCounters(false);
        }, 500L);
    }

    private final int count(List<UnreadDialog> dialogs, int flags, MessagesController.DialogFilter filter) {
        ArrayList<Long> arrayList;
        ArrayList<Long> arrayList2;
        HashSet hashSet = null;
        HashSet hashSet2 = (filter == null || (arrayList2 = filter.alwaysShow) == null) ? null : CollectionsKt.toHashSet(arrayList2);
        if (filter != null && (arrayList = filter.neverShow) != null) {
            hashSet = CollectionsKt.toHashSet(arrayList);
        }
        int weight = 0;
        for (UnreadDialog unreadDialog : dialogs) {
            if (hashSet == null || !hashSet.contains(Long.valueOf(unreadDialog.getId()))) {
                if (hashSet2 == null || !hashSet2.contains(Long.valueOf(unreadDialog.getId()))) {
                    if (!unreadDialog.getArchived() || (MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED & flags) == 0) {
                        if ((unreadDialog.getTypeFlags() & flags) == unreadDialog.getTypeFlags()) {
                        }
                    }
                }
                weight += unreadDialog.getWeight();
            }
        }
        return weight;
    }

    @Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\f\u001a\u00020\b2\u0006\u0010\r\u001a\u00020\u000eH\u0007J\b\u0010\u000f\u001a\u00020\u0010H\u0007J\b\u0010\u0011\u001a\u00020\u0012H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0018\u0010\u0006\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\b0\u0007X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\tR\u0016\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\u0007X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000b¨\u0006\u0013"}, d2 = {"Lcom/exteragram/messenger/utils/chats/FolderCounters$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "RECOUNT_DELAY_MS", _UrlKt.FRAGMENT_ENCODE_SET, "instances", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/utils/chats/FolderCounters;", "[Lcom/exteragram/messenger/utils/chats/FolderCounters;", "lockObjects", "[Ljava/lang/Object;", "getInstance", "num", _UrlKt.FRAGMENT_ENCODE_SET, "recountAll", _UrlKt.FRAGMENT_ENCODE_SET, "isUnmutedOnly", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final FolderCounters getInstance(int num) {
            FolderCounters folderCounters;
            FolderCounters folderCounters2 = FolderCounters.instances[num];
            if (folderCounters2 != null) {
                return folderCounters2;
            }
            synchronized (FolderCounters.lockObjects[num]) {
                try {
                    folderCounters = FolderCounters.instances[num];
                    if (folderCounters == null) {
                        folderCounters = new FolderCounters(num, null);
                        FolderCounters.instances[num] = folderCounters;
                    }
                    Unit unit = Unit.INSTANCE;
                } catch (Throwable th) {
                    throw th;
                }
            }
            return folderCounters;
        }

        @JvmStatic
        public final void recountAll() {
            if (isUnmutedOnly()) {
                for (int i = 0; i < 16; i++) {
                    if (UserConfig.getInstance(i).isClientActivated()) {
                        final MessagesStorage messagesStorage = MessagesStorage.getInstance(i);
                        messagesStorage.getStorageQueue().postRunnable(new Runnable() { // from class: com.exteragram.messenger.utils.chats.FolderCounters$Companion$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                messagesStorage.resetAllUnreadCounters(false);
                            }
                        });
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public final boolean isUnmutedOnly() {
            return ExteraConfig.getTabCounterMode() == TabCounterMode.UNMUTED;
        }
    }

    static {
        Object[] objArr = new Object[16];
        for (int i = 0; i < 16; i++) {
            objArr[i] = new Object();
        }
        lockObjects = objArr;
    }
}
