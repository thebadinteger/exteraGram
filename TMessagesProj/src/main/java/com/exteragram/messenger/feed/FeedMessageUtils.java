package com.exteragram.messenger.feed;

import java.util.ArrayList;
import java.util.Calendar;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.BulletinFactory;

public abstract class FeedMessageUtils {
    public static boolean isAllowedDoubleTapAction(int i) {
        return i == 2 || i == 3 || i == 4 || i == 6 || i == 9;
    }

    public static boolean isAllowedFeedOption(int i) {
        return i == 2 || i == 3 || i == 4 || i == 6 || i == 7 || i == 8 || i == 10 || i == 16 || i == 22 || i == 29 || i == 36 || i == 200 || i == 203 || i == 206;
    }

    public static boolean isPostRow(MessageObject messageObject) {
        return (messageObject == null || messageObject.isDateObject || messageObject.type == 6 || messageObject.isSponsored()) ? false : true;
    }

    public static MessageObject createUnreadDivider(int i, int i2) {
        TLRPC.TL_message tL_message = new TLRPC.TL_message();
        tL_message.message = _UrlKt.FRAGMENT_ENCODE_SET;
        tL_message.id = 0;
        MessageObject messageObject = new MessageObject(i, tL_message, false, false);
        messageObject.type = 6;
        messageObject.contentType = 2;
        messageObject.stableId = i2;
        return messageObject;
    }

    public static MessageObject createDateHeader(int i, MessageObject messageObject, int i2) {
        TLRPC.TL_message tL_message = new TLRPC.TL_message();
        tL_message.message = LocaleController.formatDateChat(messageObject.messageOwner.date);
        tL_message.id = 0;
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(((long) messageObject.messageOwner.date) * 1000);
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
        tL_message.date = (int) (calendar.getTimeInMillis() / 1000);
        MessageObject messageObject2 = new MessageObject(i, tL_message, false, false);
        messageObject2.type = 10;
        messageObject2.contentType = 1;
        messageObject2.isDateObject = true;
        messageObject2.stableId = i2;
        return messageObject2;
    }

    public static TLRPC.InputPeer getInputPeerForMessageRequest(MessagesController messagesController, long j, boolean z, MessageObject messageObject) {
        if (z && messageObject != null) {
            j = messageObject.getDialogId();
        }
        return messagesController.getInputPeer(j);
    }

    public static boolean matchesPlaybackNotification(int i, MessageObject messageObject, int i2) {
        if (messageObject == null) {
            return false;
        }
        if (messageObject.getId() == i2) {
            return true;
        }
        FeedController feedControllerPeekInstance = FeedController.peekInstance(i);
        if (feedControllerPeekInstance == null) {
            return false;
        }
        long jResolveRealDialogId = feedControllerPeekInstance.resolveRealDialogId(i2);
        return jResolveRealDialogId != 0 && jResolveRealDialogId == messageObject.getDialogId() && feedControllerPeekInstance.resolveRealMessageId(jResolveRealDialogId, i2) == getFeedRealId(messageObject);
    }

    public static int getFeedRealId(MessageObject messageObject) {
        if (messageObject == null) {
            return 0;
        }
        return messageObject.searchType == 4 ? messageObject.getRealId() : messageObject.getId();
    }

    public static int getPlaybackScrollMessageId(boolean z, long j, MessageObject messageObject) {
        if (messageObject != null && messageObject.searchType == 4 && !z && messageObject.getDialogId() == j) {
            return messageObject.getRealId();
        }
        if (messageObject != null) {
            return messageObject.getId();
        }
        return 0;
    }

    public static MessageObject getForwardingMessageObject(int i, boolean z, MessageObject messageObject) {
        if (!z || messageObject == null || messageObject.getId() == messageObject.getRealId()) {
            return messageObject;
        }
        TLRPC.TL_message tL_messageCopyMessage = copyMessage(messageObject.messageOwner);
        tL_messageCopyMessage.id = messageObject.getRealId();
        tL_messageCopyMessage.realId = 0;
        tL_messageCopyMessage.dialog_id = messageObject.getDialogId();
        MessageObject messageObject2 = new MessageObject(i, tL_messageCopyMessage, messageObject.replyMessageObject, null, null, null, null, false, true, 0L, false, false, false);
        messageObject2.isPrimaryGroupMessage = messageObject.isPrimaryGroupMessage;
        messageObject2.localGroupId = messageObject.localGroupId;
        messageObject2.copyStableParams(messageObject);
        return messageObject2;
    }

    public static MessageObject createReplacement(int i, long j, MessageObject messageObject) {
        FeedController feedController;
        MessageObject message;
        if (messageObject == null || (message = (feedController = FeedController.getInstance(i)).getMessage(j, messageObject.getRealId())) == null) {
            return null;
        }
        TLRPC.TL_message tL_messageCopyMessage = copyMessage(messageObject.messageOwner);
        tL_messageCopyMessage.id = message.getId();
        tL_messageCopyMessage.realId = message.getRealId();
        tL_messageCopyMessage.dialog_id = message.getDialogId();
        MessageObject messageObject2 = new MessageObject(i, tL_messageCopyMessage, message.replyMessageObject, null, null, null, null, true, true, 0L, false, false, false, 4);
        messageObject2.isPrimaryGroupMessage = message.isPrimaryGroupMessage;
        messageObject2.localGroupId = message.localGroupId;
        messageObject2.copyStableParams(message);
        feedController.replaceMessage(message, messageObject2);
        return messageObject2;
    }

    public static ArrayList<MessageObject> createReplacements(int i, long j, ArrayList<MessageObject> arrayList) {
        ArrayList<MessageObject> arrayList2 = new ArrayList<>();
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            MessageObject messageObjectCreateReplacement = createReplacement(i, j, arrayList.get(i2));
            if (messageObjectCreateReplacement != null) {
                arrayList2.add(messageObjectCreateReplacement);
            }
        }
        return arrayList2;
    }

    public static void filterAllowedOptions(ArrayList<CharSequence> arrayList, ArrayList<Integer> arrayList2, ArrayList<Integer> arrayList3) {
        for (int size = arrayList2.size() - 1; size >= 0; size--) {
            if (!isAllowedFeedOption(arrayList2.get(size).intValue())) {
                arrayList3.remove(size);
                arrayList.remove(size);
                arrayList2.remove(size);
            }
        }
    }

    public static void copyFeedPostLink(final ChatActivity chatActivity, MessageObject messageObject) {
        if (chatActivity == null || messageObject == null) {
            return;
        }
        TLRPC.Chat chat = chatActivity.getMessagesController().getChat(Long.valueOf(-messageObject.getDialogId()));
        if (ChatObject.isChannel(chat)) {
            TLRPC.TL_channels_exportMessageLink tL_channels_exportMessageLink = new TLRPC.TL_channels_exportMessageLink();
            tL_channels_exportMessageLink.id = messageObject.getRealId();
            tL_channels_exportMessageLink.channel = MessagesController.getInputChannel(chat);
            chatActivity.getConnectionsManager().sendRequest(tL_channels_exportMessageLink, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() { 
                        @Override // java.lang.Runnable
                        public final void run() {
                            FeedMessageUtils.$r8$lambda$IXZLP3pS1__oSfuf53vB5B1rwvc(tLObject, chatActivity);
                        }
                    });
                }
            });
        }
    }

    public static /* synthetic */ void $r8$lambda$IXZLP3pS1__oSfuf53vB5B1rwvc(TLObject tLObject, ChatActivity chatActivity) {
        if (tLObject instanceof TLRPC.TL_exportedMessageLink) {
            String str = ((TLRPC.TL_exportedMessageLink) tLObject).link;
            if (AndroidUtilities.addToClipboard(str) && BulletinFactory.canShowBulletin(chatActivity)) {
                BulletinFactory.of(chatActivity).createCopyLinkBulletin(str.contains("/c/")).show();
            }
        }
    }

    public static void copyTranslationState(MessageObject messageObject, MessageObject messageObject2) {
        TLRPC.Message message;
        TLRPC.Message message2;
        if (messageObject == null || messageObject2 == null || messageObject == messageObject2 || (message = messageObject.messageOwner) == null || (message2 = messageObject2.messageOwner) == null) {
            return;
        }
        message2.translatedText = message.translatedText;
        message2.translatedToLanguage = message.translatedToLanguage;
        message2.translatedVoiceTranscription = message.translatedVoiceTranscription;
        message2.translatedPoll = message.translatedPoll;
        message2.summaryText = message.summaryText;
        message2.summarizedOpen = message.summarizedOpen;
        message2.translatedSummaryText = message.translatedSummaryText;
        message2.translatedSummaryLanguage = message.translatedSummaryLanguage;
    }

    private static TLRPC.TL_message copyMessage(TLRPC.Message message) {
        TLRPC.TL_message tL_message = new TLRPC.TL_message();
        tL_message.id = message.id;
        tL_message.from_id = message.from_id;
        tL_message.from_boosts_applied = message.from_boosts_applied;
        tL_message.peer_id = message.peer_id;
        tL_message.saved_peer_id = message.saved_peer_id;
        tL_message.date = message.date;
        tL_message.expire_date = message.expire_date;
        tL_message.action = message.action;
        tL_message.message = message.message;
        tL_message.media = message.media;
        tL_message.flags = message.flags;
        tL_message.flags2 = message.flags2;
        tL_message.mentioned = message.mentioned;
        tL_message.media_unread = message.media_unread;
        tL_message.out = message.out;
        tL_message.unread = message.unread;
        tL_message.entities = message.entities;
        tL_message.via_bot_name = message.via_bot_name;
        tL_message.reply_markup = message.reply_markup;
        tL_message.views = message.views;
        tL_message.forwards = message.forwards;
        tL_message.replies = message.replies;
        tL_message.edit_date = message.edit_date;
        tL_message.silent = message.silent;
        tL_message.post = message.post;
        tL_message.from_scheduled = message.from_scheduled;
        tL_message.legacy = message.legacy;
        tL_message.edit_hide = message.edit_hide;
        tL_message.pinned = message.pinned;
        tL_message.fwd_from = message.fwd_from;
        tL_message.via_bot_id = message.via_bot_id;
        tL_message.via_business_bot_id = message.via_business_bot_id;
        tL_message.reply_to = message.reply_to;
        tL_message.post_author = message.post_author;
        tL_message.grouped_id = message.grouped_id;
        tL_message.reactions = message.reactions;
        tL_message.restriction_reason = message.restriction_reason;
        tL_message.ttl_period = message.ttl_period;
        tL_message.quick_reply_shortcut_id = message.quick_reply_shortcut_id;
        tL_message.effect = message.effect;
        tL_message.noforwards = message.noforwards;
        tL_message.invert_media = message.invert_media;
        tL_message.offline = message.offline;
        tL_message.factcheck = message.factcheck;
        tL_message.send_state = message.send_state;
        tL_message.fwd_msg_id = message.fwd_msg_id;
        tL_message.params = message.params;
        tL_message.random_id = message.random_id;
        tL_message.local_id = message.local_id;
        tL_message.attachPath = message.attachPath;
        tL_message.dialog_id = message.dialog_id;
        tL_message.ttl = message.ttl;
        tL_message.destroyTime = message.destroyTime;
        tL_message.destroyTimeMillis = message.destroyTimeMillis;
        tL_message.layer = message.layer;
        tL_message.seq_in = message.seq_in;
        tL_message.seq_out = message.seq_out;
        tL_message.with_my_score = message.with_my_score;
        tL_message.replyMessage = message.replyMessage;
        tL_message.reqId = message.reqId;
        tL_message.realId = message.realId;
        tL_message.stickerVerified = message.stickerVerified;
        tL_message.isThreadMessage = message.isThreadMessage;
        tL_message.voiceTranscription = message.voiceTranscription;
        tL_message.voiceTranscriptionOpen = message.voiceTranscriptionOpen;
        tL_message.voiceTranscriptionRated = message.voiceTranscriptionRated;
        tL_message.voiceTranscriptionFinal = message.voiceTranscriptionFinal;
        tL_message.voiceTranscriptionForce = message.voiceTranscriptionForce;
        tL_message.voiceTranscriptionId = message.voiceTranscriptionId;
        tL_message.premiumEffectWasPlayed = message.premiumEffectWasPlayed;
        tL_message.originalLanguage = message.originalLanguage;
        tL_message.translatedToLanguage = message.translatedToLanguage;
        tL_message.translatedText = message.translatedText;
        tL_message.replyStory = message.replyStory;
        tL_message.quick_reply_shortcut = message.quick_reply_shortcut;
        return tL_message;
    }
}
