package com.exteragram.messenger.utils.chats;

import android.graphics.Point;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.feed.FeedMessageUtils;
import java.util.ArrayList;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BooleanSupplier;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;

/* JADX INFO: loaded from: classes4.dex */
public abstract class WidePosts {
    private static final Map<MessageObject.GroupedMessagePosition, GroupPositionState> GROUP_POSITION_STATES = new WeakHashMap();
    private static final Map<MessageObject, CommentsPostContext> COMMENTS_POST_CONTEXTS = new WeakHashMap();

    public static void registerCommentsPostContext(MessageObject messageObject, TLRPC.Chat chat, MessageObject messageObject2) {
        TLRPC.Peer peer;
        if (messageObject == null || messageObject.messageOwner == null || !ChatObject.isChannelAndNotMegaGroup(chat)) {
            return;
        }
        TLRPC.Message message = messageObject.messageOwner;
        TLRPC.MessageFwdHeader messageFwdHeader = message.fwd_from;
        TLRPC.Peer peer2 = message.from_id;
        long j = peer2 != null ? peer2.channel_id : 0L;
        TLRPC.Peer peer3 = message.peer_id;
        long j2 = peer3 != null ? peer3.channel_id : 0L;
        long j3 = (messageFwdHeader == null || (peer = messageFwdHeader.saved_from_peer) == null) ? 0L : peer.channel_id;
        long j4 = chat.id;
        boolean z = false;
        boolean z2 = (j != j4 || j2 == 0 || j2 == j4) ? false : true;
        if (message.reply_to == null && messageFwdHeader != null && (message.isThreadMessage || j3 == j4 || messageFwdHeader.channel_post != 0 || message.replies != null)) {
            z = true;
        }
        if (z2 && z) {
            String str = (messageObject2 == null || messageObject2.messageOwner == null || messageObject2.getDialogId() != (-chat.id)) ? null : messageObject2.messageOwner.post_author;
            Map<MessageObject, CommentsPostContext> map = COMMENTS_POST_CONTEXTS;
            CommentsPostContext commentsPostContext = map.get(messageObject);
            if (commentsPostContext != null && commentsPostContext.channelId == chat.id) {
                commentsPostContext.updatePostAuthor(str);
            } else {
                map.put(messageObject, new CommentsPostContext(chat.id, str));
            }
        }
    }

    public static boolean isCommentsChannelPost(MessageObject messageObject) {
        return getCommentsChannelId(messageObject) != 0;
    }

    public static void copyCommentsPostContext(MessageObject messageObject, MessageObject messageObject2) {
        Map<MessageObject, CommentsPostContext> map;
        CommentsPostContext commentsPostContext;
        if (messageObject == null || messageObject2 == null || (commentsPostContext = (map = COMMENTS_POST_CONTEXTS).get(messageObject)) == null) {
            return;
        }
        map.put(messageObject2, commentsPostContext);
    }

    public static String getCommentsPostAuthor(MessageObject messageObject) {
        CommentsPostContext commentsPostContext = getCommentsPostContext(messageObject);
        if (commentsPostContext != null) {
            return commentsPostContext.postAuthor;
        }
        return null;
    }

    public static boolean isEnabledFor(boolean z, boolean z2) {
        if (!z2) {
            return false;
        }
        if (z) {
            return ExteraConfig.getWidePostsInFeed();
        }
        return ExteraConfig.getWidePostsInChannels();
    }

    public static boolean isEnabledFor(MessageObject messageObject) {
        if (messageObject == null || !(ExteraConfig.getWidePostsInFeed() || ExteraConfig.getWidePostsInChannels())) {
            return false;
        }
        return isEnabledFor(messageObject.searchType == 4, isChannelPostOrSponsored(messageObject));
    }

    public static int getBubbleRight(int i) {
        return Math.max(AndroidUtilities.dp(120.0f), i - AndroidUtilities.dp(9.0f));
    }

    private static boolean shouldExpand(MessageObject messageObject) {
        return isEnabledFor(messageObject) && !messageObject.shouldDrawWithoutBackground();
    }

    public static boolean shouldDrawBackground(MessageObject messageObject) {
        if (messageObject == null) {
            return false;
        }
        if (messageObject.shouldDrawWithoutBackground()) {
            return messageObject.isRoundVideo() && messageObject.isVoiceTranscriptionOpen();
        }
        return true;
    }

    public static boolean isTextMessage(MessageObject messageObject) {
        int i = messageObject.type;
        return i == 0 || i == 36 || i == 24;
    }

    public static boolean isExpandableMedia(MessageObject messageObject) {
        int i = messageObject.type;
        return i == 1 || i == 20 || i == 3 || i == 8;
    }

    public static int getImageHeight(int i, int i2, int i3, int i4) {
        return Math.max(AndroidUtilities.dp(120.0f), Math.min(Math.round(i4 * (i2 / i)), Math.max(i3, Math.min(AndroidUtilities.getPhotoSize(), i4 + AndroidUtilities.dp(100.0f)))));
    }

    public static boolean isProfileResolved(MessageObject messageObject) {
        if (messageObject == null || !shouldEmbedProfileAvatar(messageObject)) {
            return true;
        }
        return isPeerResolved(messageObject.currentAccount, getEmbeddedProfileDialogId(messageObject)) && isPeerResolved(messageObject.currentAccount, getFeedSignatureProfileDialogId(messageObject));
    }

    private static boolean isPeerResolved(int i, long j) {
        if (j > 0) {
            return MessagesController.getInstance(i).getUser(Long.valueOf(j)) != null;
        }
        return j >= 0 || MessagesController.getInstance(i).getChat(Long.valueOf(-j)) != null;
    }

    public static boolean shouldEmbedProfileAvatar(MessageObject messageObject) {
        return shouldEmbedFeedChannelProfileAvatar(messageObject) || shouldEmbedCommentsChannelProfileAvatar(messageObject) || shouldEmbedAuthorProfileAvatar(messageObject);
    }

    public static boolean shouldSuppressExternalAvatar(MessageObject messageObject) {
        return isEnabledFor(messageObject) && messageObject.searchType == 4 && messageObject.isSponsored();
    }

    private static boolean shouldEmbedFeedChannelProfileAvatar(MessageObject messageObject) {
        return isEmbeddedProfileAvatarMessage(messageObject) && FeedMessageUtils.resolveDisplayChannel(messageObject) != null;
    }

    public static boolean shouldEmbedAuthorProfileAvatar(MessageObject messageObject) {
        TLRPC.Chat broadcastChannel;
        return isEmbeddedProfileAvatarMessage(messageObject) && messageObject.searchType != 4 && (broadcastChannel = getBroadcastChannel(messageObject)) != null && (broadcastChannel.signature_profiles || messageObject.currentEvent != null) && ChatObject.isChannelAndNotMegaGroup(broadcastChannel);
    }

    private static boolean shouldEmbedCommentsChannelProfileAvatar(MessageObject messageObject) {
        boolean zIsEmbeddedProfileAvatarMessage = isEmbeddedProfileAvatarMessage(messageObject);
        long commentsChannelId = getCommentsChannelId(messageObject);
        CommentsPostContext commentsPostContext = getCommentsPostContext(messageObject);
        TLRPC.Chat chat = (messageObject == null || commentsChannelId == 0) ? null : MessagesController.getInstance(messageObject.currentAccount).getChat(Long.valueOf(commentsChannelId));
        if (!zIsEmbeddedProfileAvatarMessage || commentsChannelId == 0) {
            return false;
        }
        return commentsPostContext != null || ChatObject.isChannelAndNotMegaGroup(chat);
    }

    public static long getEmbeddedProfileDialogId(MessageObject messageObject) {
        TLRPC.Peer peer;
        if (shouldEmbedFeedChannelProfileAvatar(messageObject)) {
            TLRPC.Chat chatResolveDisplayChannel = FeedMessageUtils.resolveDisplayChannel(messageObject);
            if (chatResolveDisplayChannel != null) {
                return -chatResolveDisplayChannel.id;
            }
            return 0L;
        }
        if (shouldEmbedCommentsChannelProfileAvatar(messageObject)) {
            long commentsChannelId = getCommentsChannelId(messageObject);
            if (commentsChannelId != 0) {
                return -commentsChannelId;
            }
            return 0L;
        }
        if (!shouldEmbedAuthorProfileAvatar(messageObject) || (peer = messageObject.messageOwner.from_id) == null) {
            return 0L;
        }
        return DialogObject.getPeerDialogId(peer);
    }

    public static long getFeedSignatureProfileDialogId(MessageObject messageObject) {
        TLRPC.Peer peer;
        if (FeedMessageUtils.hasProfileSignature(messageObject) && (peer = messageObject.messageOwner.from_id) != null) {
            long peerDialogId = DialogObject.getPeerDialogId(peer);
            if (peerDialogId != DialogObject.getPeerDialogId(messageObject.messageOwner.peer_id)) {
                return peerDialogId;
            }
        }
        return 0L;
    }

    public static boolean shouldDrawFeedAuthorSignature(MessageObject messageObject) {
        return FeedMessageUtils.hasProfileSignature(messageObject);
    }

    public static String getFeedPostAuthor(MessageObject messageObject) {
        if (FeedMessageUtils.hasProfileSignature(messageObject)) {
            return messageObject.messageOwner.post_author;
        }
        return null;
    }

    public static boolean hasSameEmbeddedProfileHeader(MessageObject messageObject, MessageObject messageObject2) {
        return shouldEmbedFeedChannelProfileAvatar(messageObject) == shouldEmbedFeedChannelProfileAvatar(messageObject2) && getEmbeddedProfileDialogId(messageObject) == getEmbeddedProfileDialogId(messageObject2);
    }

    private static boolean isEmbeddedProfileAvatarMessage(MessageObject messageObject) {
        TLRPC.MessageFwdHeader messageFwdHeader;
        String str;
        return (messageObject == null || messageObject.messageOwner == null || (!shouldExpand(messageObject) && (!isEnabledFor(messageObject) || !isEmbeddedProfileAvatarMessageWithoutBackground(messageObject))) || messageObject.isOutOwner() || messageObject.isSponsored() || messageObject.isExpiredStory() || !messageObject.needDrawAvatar() || (((messageFwdHeader = messageObject.messageOwner.fwd_from) != null && (str = messageFwdHeader.psa_type) != null && !str.isEmpty()) || getChannelId(messageObject) == 0)) ? false : true;
    }

    public static boolean isEmbeddedProfileAvatarMessageWithoutBackground(MessageObject messageObject) {
        if (messageObject == null) {
            return false;
        }
        int i = messageObject.type;
        return i == 5 || i == 13 || i == 15 || i == 19;
    }

    public static boolean shouldHideAuxiliaryActions(MessageObject messageObject) {
        return isEnabledFor(messageObject) && !messageObject.isSponsored();
    }

    public static boolean isWideGroupedMedia(MessageObject.GroupedMessages groupedMessages) {
        return isGroupedLayout(groupedMessages) && !groupedMessages.isDocuments;
    }

    public static int getScaledGroupWidth(MessageObject.GroupedMessages groupedMessages) {
        ArrayList<MessageObject.GroupedMessagePosition> arrayList;
        GroupPositionState groupPositionState;
        if (groupedMessages == null || (arrayList = groupedMessages.posArray) == null || arrayList.isEmpty() || (groupPositionState = GROUP_POSITION_STATES.get(groupedMessages.posArray.get(0))) == null) {
            return 0;
        }
        return groupPositionState.scaledForWidth;
    }

    public static int getDefaultGroupWidth() {
        Point point = AndroidUtilities.displaySize;
        int i = point.x;
        boolean z = i > point.y;
        if (AndroidUtilities.isInMultiwindow || !AndroidUtilities.isTablet()) {
            return i;
        }
        return (!AndroidUtilities.isSmallTablet() || z) ? i - Math.max((i * 35) / 100, AndroidUtilities.dp(320.0f)) : i;
    }

    /* JADX WARN: Code duplicated, block: B:70:0x010b  */
    /* JADX WARN: Code restructure failed: missing block: B:67:0x00fe, code lost:
    
        r7.spanSize = 1000;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public static void updateGroupedLayout(MessageObject.GroupedMessages groupedMessages, int i) {
        GroupPositionState groupPositionState;
        int i2;
        if (!isWideGroupedMedia(groupedMessages) || i <= 0) {
            restoreGroupedLayout(groupedMessages);
            return;
        }
        MessageObject primaryGroupMessage = getPrimaryGroupMessage(groupedMessages);
        if (!shouldExpand(primaryGroupMessage)) {
            restoreGroupedLayout(groupedMessages);
            return;
        }
        boolean zApply = false;
        boolean z = shouldEmbedProfileAvatar(primaryGroupMessage) || shouldSuppressExternalAvatar(primaryGroupMessage);
        ArrayList<MessageObject.GroupedMessagePosition> arrayList = groupedMessages.posArray;
        int size = arrayList.size();
        int i3 = 0;
        while (i3 < size) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = arrayList.get(i3);
            i3++;
            MessageObject.GroupedMessagePosition groupedMessagePosition2 = groupedMessagePosition;
            if (groupedMessagePosition2 != null) {
                Map<MessageObject.GroupedMessagePosition, GroupPositionState> map = GROUP_POSITION_STATES;
                GroupPositionState groupPositionState2 = map.get(groupedMessagePosition2);
                if (groupPositionState2 == null) {
                    groupPositionState2 = new GroupPositionState(groupedMessagePosition2);
                    map.put(groupedMessagePosition2, groupPositionState2);
                }
                groupPositionState2.restore(groupedMessagePosition2);
                if (z) {
                    removeGroupAvatarOffset(groupedMessagePosition2);
                }
            }
        }
        int baseGroupVisualWidth = getBaseGroupVisualWidth(groupedMessages);
        if (baseGroupVisualWidth <= 0) {
            restoreGroupedLayout(groupedMessages);
            return;
        }
        int iClamp = Utilities.clamp(Math.round((Math.max(AndroidUtilities.dp(120.0f), i - AndroidUtilities.dp(9.0f)) * 1000) / i), 1000, 1);
        float f = iClamp / baseGroupVisualWidth;
        ArrayList<MessageObject.GroupedMessagePosition> arrayList2 = groupedMessages.posArray;
        int size2 = arrayList2.size();
        int i4 = 0;
        while (i4 < size2) {
            MessageObject.GroupedMessagePosition groupedMessagePosition3 = arrayList2.get(i4);
            i4++;
            MessageObject.GroupedMessagePosition groupedMessagePosition4 = groupedMessagePosition3;
            if (groupedMessagePosition4 != null && GROUP_POSITION_STATES.get(groupedMessagePosition4) != null) {
                int i5 = groupedMessagePosition4.pw;
                boolean z2 = groupedMessagePosition4.leftSpanOffset != 0;
                int iMax = Math.max(1, Math.round(i5 * f));
                groupedMessagePosition4.pw = iMax;
                groupedMessagePosition4.leftSpanOffset = !z2 ? 0 : Math.max(0, iClamp - iMax);
            }
        }
        normalizeGroupRows(groupedMessages, iClamp, z);
        int i6 = 1000 - iClamp;
        ArrayList<MessageObject.GroupedMessagePosition> arrayList3 = groupedMessages.posArray;
        int size3 = arrayList3.size();
        int i7 = 0;
        while (i7 < size3) {
            MessageObject.GroupedMessagePosition groupedMessagePosition5 = arrayList3.get(i7);
            i7++;
            MessageObject.GroupedMessagePosition groupedMessagePosition6 = groupedMessagePosition5;
            if (groupedMessagePosition6 != null && (groupPositionState = GROUP_POSITION_STATES.get(groupedMessagePosition6)) != null) {
                if (z) {
                    i2 = groupedMessagePosition6.pw;
                    groupedMessagePosition6.spanSize = i2;
                    if ((groupedMessagePosition6.flags & 2) != 0) {
                        groupedMessagePosition6.spanSize = i2 + i6;
                    }
                } else {
                    i2 = groupedMessagePosition6.pw;
                    groupedMessagePosition6.spanSize = i2;
                    if ((groupedMessagePosition6.flags & 2) != 0) {
                        groupedMessagePosition6.spanSize = i2 + i6;
                    }
                }
                groupPositionState.scaledForWidth = i;
                zApply |= groupPositionState.apply(groupedMessagePosition6);
            }
        }
        if (zApply) {
            groupedMessages.cachedWidthForCaption = -1;
        }
    }

    private static boolean isChannelPostOrSponsored(MessageObject messageObject) {
        if (messageObject == null || messageObject.messageOwner == null || messageObject.preview || messageObject.type == 27) {
            return false;
        }
        if (messageObject.isSponsored()) {
            return true;
        }
        long commentsChannelId = getCommentsChannelId(messageObject);
        if (commentsChannelId == 0 && messageObject.messageOwner.peer_id == null) {
            return false;
        }
        if (commentsChannelId == 0) {
            commentsChannelId = getChannelId(messageObject);
        }
        if (commentsChannelId == 0) {
            return false;
        }
        TLRPC.Chat chat = MessagesController.getInstance(messageObject.currentAccount).getChat(Long.valueOf(commentsChannelId));
        if (chat != null) {
            return ChatObject.isChannelAndNotMegaGroup(chat);
        }
        return messageObject.messageOwner.post || commentsChannelId != 0;
    }

    private static long getChannelId(MessageObject messageObject) {
        if (messageObject == null || messageObject.messageOwner == null) {
            return 0L;
        }
        long commentsChannelId = getCommentsChannelId(messageObject);
        if (commentsChannelId != 0) {
            return commentsChannelId;
        }
        TLRPC.Peer peer = messageObject.messageOwner.peer_id;
        long j = peer != null ? peer.channel_id : 0L;
        if (j != 0) {
            return j;
        }
        long dialogId = messageObject.getDialogId();
        if (dialogId < 0) {
            return -dialogId;
        }
        return 0L;
    }

    private static long getCommentsChannelId(MessageObject messageObject) {
        CommentsPostContext commentsPostContext;
        if (messageObject == null || messageObject.messageOwner == null || (commentsPostContext = getCommentsPostContext(messageObject)) == null) {
            return 0L;
        }
        return commentsPostContext.channelId;
    }

    private static CommentsPostContext getCommentsPostContext(MessageObject messageObject) {
        if (messageObject == null) {
            return null;
        }
        return COMMENTS_POST_CONTEXTS.get(messageObject);
    }

    private static TLRPC.Chat getBroadcastChannel(MessageObject messageObject) {
        long channelId = getChannelId(messageObject);
        if (channelId != 0) {
            return MessagesController.getInstance(messageObject.currentAccount).getChat(Long.valueOf(channelId));
        }
        return null;
    }

    private static boolean isGroupedLayout(MessageObject.GroupedMessages groupedMessages) {
        ArrayList<MessageObject> arrayList;
        if (groupedMessages == null) {
            return false;
        }
        ArrayList<MessageObject.GroupedMessagePosition> arrayList2 = groupedMessages.posArray;
        return (arrayList2 != null && arrayList2.size() > 1) || ((arrayList = groupedMessages.messages) != null && arrayList.size() > 1);
    }

    private static MessageObject getPrimaryGroupMessage(MessageObject.GroupedMessages groupedMessages) {
        if (groupedMessages == null) {
            return null;
        }
        int i = groupedMessages.reversed ? 10 : 5;
        ArrayList<MessageObject> arrayList = groupedMessages.messages;
        if (arrayList != null) {
            int size = arrayList.size();
            int i2 = 0;
            while (i2 < size) {
                MessageObject messageObject = arrayList.get(i2);
                i2++;
                MessageObject messageObject2 = messageObject;
                MessageObject.GroupedMessagePosition position = groupedMessages.getPosition(messageObject2);
                if (position != null && (position.flags & i) == i) {
                    return messageObject2;
                }
            }
        }
        ArrayList<MessageObject> arrayList2 = groupedMessages.messages;
        if (arrayList2 == null || arrayList2.isEmpty()) {
            return null;
        }
        return groupedMessages.messages.get(0);
    }

    private static int getBaseGroupVisualWidth(MessageObject.GroupedMessages groupedMessages) {
        ArrayList<MessageObject.GroupedMessagePosition> arrayList = groupedMessages.posArray;
        int size = arrayList.size();
        int iMax = 0;
        int i = 0;
        while (i < size) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = arrayList.get(i);
            i++;
            MessageObject.GroupedMessagePosition groupedMessagePosition2 = groupedMessagePosition;
            if (groupedMessagePosition2 != null) {
                iMax = Math.max(iMax, (int) groupedMessagePosition2.maxY);
            }
        }
        int iMax2 = 0;
        for (int i2 = 0; i2 <= iMax; i2++) {
            ArrayList<MessageObject.GroupedMessagePosition> arrayList2 = groupedMessages.posArray;
            int size2 = arrayList2.size();
            int i3 = 0;
            int i4 = 0;
            while (i4 < size2) {
                MessageObject.GroupedMessagePosition groupedMessagePosition3 = arrayList2.get(i4);
                i4++;
                MessageObject.GroupedMessagePosition groupedMessagePosition4 = groupedMessagePosition3;
                if (groupedMessagePosition4 != null && groupedMessagePosition4.minY <= i2 && groupedMessagePosition4.maxY >= i2) {
                    i3 += groupedMessagePosition4.pw;
                }
            }
            iMax2 = Math.max(iMax2, i3);
        }
        return Math.min(1000, iMax2);
    }

    private static void removeGroupAvatarOffset(MessageObject.GroupedMessagePosition groupedMessagePosition) {
        if (groupedMessagePosition.edge) {
            groupedMessagePosition.pw = Math.max(1, groupedMessagePosition.pw - 108);
            int i = groupedMessagePosition.spanSize;
            if (i != 1000) {
                groupedMessagePosition.spanSize = Math.max(1, i - 108);
                return;
            }
            return;
        }
        if ((groupedMessagePosition.flags & 2) != 0) {
            int i2 = groupedMessagePosition.spanSize;
            if (i2 != 1000) {
                groupedMessagePosition.spanSize = i2 + 108;
                return;
            }
            int i3 = groupedMessagePosition.leftSpanOffset;
            if (i3 != 0) {
                groupedMessagePosition.leftSpanOffset = Math.max(0, i3 - 108);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:45:0x0086  */
    private static void normalizeGroupRows(MessageObject.GroupedMessages groupedMessages, int i, boolean z) {
        ArrayList<MessageObject.GroupedMessagePosition> arrayList = groupedMessages.posArray;
        int size = arrayList.size();
        int iMax = 0;
        int i2 = 0;
        while (i2 < size) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = arrayList.get(i2);
            i2++;
            MessageObject.GroupedMessagePosition groupedMessagePosition2 = groupedMessagePosition;
            if (groupedMessagePosition2 != null) {
                iMax = Math.max(iMax, (int) groupedMessagePosition2.maxY);
            }
        }
        for (int i3 = 0; i3 <= iMax; i3++) {
            ArrayList<MessageObject.GroupedMessagePosition> arrayList2 = groupedMessages.posArray;
            int size2 = arrayList2.size();
            MessageObject.GroupedMessagePosition groupedMessagePosition3 = null;
            int i4 = 0;
            int i5 = 0;
            while (true) {
                if (i5 >= size2) {
                    break;
                }
                MessageObject.GroupedMessagePosition groupedMessagePosition4 = arrayList2.get(i5);
                i5++;
                MessageObject.GroupedMessagePosition groupedMessagePosition5 = groupedMessagePosition4;
                if (groupedMessagePosition5 != null && groupedMessagePosition5.minY <= i3 && groupedMessagePosition5.maxY >= i3) {
                    i4 += groupedMessagePosition5.pw;
                    boolean z2 = (groupedMessagePosition5.flags & 2) != 0;
                    boolean z3 = (groupedMessagePosition3 == null || (groupedMessagePosition3.flags & 2) == 0) ? false : true;
                    if (groupedMessagePosition3 == null || ((z2 && !z3) || (z2 == z3 && groupedMessagePosition5.maxX > groupedMessagePosition3.maxX))) {
                        groupedMessagePosition3 = groupedMessagePosition5;
                    }
                }
            }
            if (groupedMessagePosition3 != null) {
                groupedMessagePosition3.pw = Math.max(1, (groupedMessagePosition3.pw + i) - i4);
                GroupPositionState groupPositionState = GROUP_POSITION_STATES.get(groupedMessagePosition3);
                if (z) {
                    if (groupedMessagePosition3.leftSpanOffset > 0) {
                        groupedMessagePosition3.leftSpanOffset = Math.max(0, i - groupedMessagePosition3.pw);
                    }
                } else if (groupPositionState != null && groupPositionState.leftSpanOffset > 0) {
                    groupedMessagePosition3.leftSpanOffset = Math.max(0, i - groupedMessagePosition3.pw);
                }
            }
        }
    }

    private static void restoreGroupedLayout(MessageObject.GroupedMessages groupedMessages) {
        ArrayList<MessageObject.GroupedMessagePosition> arrayList;
        GroupPositionState groupPositionStateRemove;
        if (groupedMessages == null || (arrayList = groupedMessages.posArray) == null || arrayList.isEmpty()) {
            return;
        }
        ArrayList<MessageObject.GroupedMessagePosition> arrayList2 = groupedMessages.posArray;
        int size = arrayList2.size();
        boolean z = false;
        int i = 0;
        while (i < size) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = arrayList2.get(i);
            i++;
            MessageObject.GroupedMessagePosition groupedMessagePosition2 = groupedMessagePosition;
            if (groupedMessagePosition2 != null && (groupPositionStateRemove = GROUP_POSITION_STATES.remove(groupedMessagePosition2)) != null) {
                groupPositionStateRemove.restore(groupedMessagePosition2);
                z = true;
            }
        }
        if (z) {
            groupedMessages.cachedWidthForCaption = -1;
        }
    }

    public static final class CommentsPostAuthorLoader {
        private final TLRPC.Chat channel;
        private ChatActivity chatActivity;
        private final int currentAccount;
        private final ArrayList<MessageObject> messages = new ArrayList<>();
        private MessageObject originalMessage;

        public CommentsPostAuthorLoader(int i, TLRPC.Chat chat) {
            this.currentAccount = i;
            this.channel = chat;
        }

        public MessageObject getOriginalMessage() {
            return this.originalMessage;
        }

        public int load(final int i, final BooleanSupplier booleanSupplier) {
            TLRPC.TL_channels_getMessages tL_channels_getMessages = new TLRPC.TL_channels_getMessages();
            tL_channels_getMessages.channel = MessagesController.getInputChannel(this.channel);
            tL_channels_getMessages.id.add(Integer.valueOf(i));
            return ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getMessages, new RequestDelegate() {
                @Override
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    CommentsPostAuthorLoader.this.lambda$load$2(booleanSupplier, i, tLObject, tL_error);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$load$1(final BooleanSupplier booleanSupplier, final TLObject tLObject, final int i) {
            NotificationCenter.getInstance(this.currentAccount).doOnIdle(new Runnable() {
                @Override
                public final void run() {
                    CommentsPostAuthorLoader.this.lambda$load$0(booleanSupplier, tLObject, i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$load$2(final BooleanSupplier booleanSupplier, final int i, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public final void run() {
                    CommentsPostAuthorLoader.this.lambda$load$1(booleanSupplier, tLObject, i);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$load$0(BooleanSupplier booleanSupplier, TLObject tLObject, int i) {
            if (booleanSupplier == null || !booleanSupplier.getAsBoolean()) {
                if (tLObject instanceof TLRPC.messages_Messages) {
                    TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
                    MessagesController.getInstance(this.currentAccount).putUsers(messages_messages.users, false);
                    MessagesController.getInstance(this.currentAccount).putChats(messages_messages.chats, false);
                    int size = messages_messages.messages.size();
                    for (int i2 = 0; i2 < size; i2++) {
                        TLRPC.Message message = messages_messages.messages.get(i2);
                        if (message != null && message.id == i) {
                            this.originalMessage = new MessageObject(this.currentAccount, message, true, true);
                            break;
                        }
                    }
                }
                apply();
            }
        }

        public void setMessages(ArrayList<MessageObject> arrayList, ChatActivity chatActivity) {
            this.messages.clear();
            if (arrayList != null) {
                this.messages.addAll(arrayList);
            }
            this.chatActivity = chatActivity;
            apply();
        }

        private void apply() {
            ChatActivity chatActivity;
            if (this.messages.isEmpty()) {
                return;
            }
            int size = this.messages.size();
            for (int i = 0; i < size; i++) {
                MessageObject messageObject = this.messages.get(i);
                WidePosts.registerCommentsPostContext(messageObject, this.channel, this.originalMessage);
                messageObject.forceUpdate = true;
            }
            if (this.originalMessage == null || (chatActivity = this.chatActivity) == null) {
                return;
            }
            chatActivity.updateWidePostsCommentsAuthor(this.messages);
        }
    }

    public static final class CommentsPostContext {
        final long channelId;
        String postAuthor;

        public CommentsPostContext(long j, String str) {
            this.channelId = j;
            this.postAuthor = str;
        }

        public void updatePostAuthor(String str) {
            if (str != null) {
                this.postAuthor = str;
            }
        }
    }

    public static final class GroupPositionState {
        private int appliedLeftSpanOffset;
        private int appliedSpanSize;
        private int appliedWidth;
        final int leftSpanOffset;
        private int scaledForWidth;
        final int spanSize;
        final int width;

        public GroupPositionState(MessageObject.GroupedMessagePosition groupedMessagePosition) {
            int i = groupedMessagePosition.pw;
            this.appliedWidth = i;
            this.width = i;
            int i2 = groupedMessagePosition.spanSize;
            this.appliedSpanSize = i2;
            this.spanSize = i2;
            int i3 = groupedMessagePosition.leftSpanOffset;
            this.appliedLeftSpanOffset = i3;
            this.leftSpanOffset = i3;
        }

        public boolean apply(MessageObject.GroupedMessagePosition groupedMessagePosition) {
            int i = this.appliedWidth;
            int i2 = groupedMessagePosition.pw;
            boolean z = (i == i2 && this.appliedSpanSize == groupedMessagePosition.spanSize && this.appliedLeftSpanOffset == groupedMessagePosition.leftSpanOffset) ? false : true;
            this.appliedWidth = i2;
            this.appliedSpanSize = groupedMessagePosition.spanSize;
            this.appliedLeftSpanOffset = groupedMessagePosition.leftSpanOffset;
            return z;
        }

        public void restore(MessageObject.GroupedMessagePosition groupedMessagePosition) {
            groupedMessagePosition.pw = this.width;
            groupedMessagePosition.spanSize = this.spanSize;
            groupedMessagePosition.leftSpanOffset = this.leftSpanOffset;
        }
    }
}
