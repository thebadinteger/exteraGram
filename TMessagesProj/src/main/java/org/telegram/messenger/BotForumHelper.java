package org.telegram.messenger;

import static org.telegram.messenger.AndroidUtilities.dp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.DynamicDrawableSpan;
import android.util.LongSparseArray;
import android.util.SparseIntArray;

import androidx.annotation.Nullable;

import org.telegram.messenger.utils.tlutils.TlUtils;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_forum;
import org.telegram.tgnet.tl.TL_iv;
import org.telegram.tgnet.tl.TL_update;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.TypingDotsDrawable;
import org.telegram.ui.MultiLayoutTypingAnimator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BotForumHelper extends BaseController {

    private MessageObject createDraftMessage(long userId, int topicId, long randomId, int messageId, TLRPC.TL_textWithEntities text) {
        TLRPC.Message message = new TLRPC.TL_message();
        message.dialog_id = userId;
        message.peer_id = getMessagesController().getPeer(userId);
        message.from_id = getMessagesController().getPeer(userId);
        message.id = message.local_id = messageId;
        message.random_id = randomId;

        message.message = text.text;

        message.entities = text.entities;
        message.flags |= 128;

        message.date = getConnectionsManager().getCurrentTime();

        message.reply_to = new TLRPC.TL_messageReplyHeader();
        message.flags |= 16;

        message.reply_to.forum_topic = true;
        message.reply_to.reply_to_top_id = topicId;
        message.reply_to.flags |= 2;

        message.media = new TLRPC.TL_messageMediaEmpty();
        message.flags |= 512;

        MessageObject messageObject = new MessageObject(currentAccount, message, false, true);
        messageObject.isBotPendingDraft = true;
        messageObject.resetLayout();

        return messageObject;
    }

    private MessageObject createDraftMessage(long userId, int topicId, long randomId, int messageId, TL_iv.RichMessage rich_message) {
        TLRPC.Message message = new TLRPC.TL_message();
        message.dialog_id = userId;
        message.peer_id = getMessagesController().getPeer(userId);
        message.from_id = getMessagesController().getPeer(userId);
        message.id = message.local_id = messageId;
        message.random_id = randomId;

        message.message = "";

        message.flags |= TLObject.FLAG_13;
        message.rich_message = rich_message;

        message.date = getConnectionsManager().getCurrentTime();

        message.reply_to = new TLRPC.TL_messageReplyHeader();
        message.flags |= 16;

        message.reply_to.forum_topic = true;
        message.reply_to.reply_to_top_id = topicId;
        message.reply_to.flags |= 2;

        message.media = new TLRPC.TL_messageMediaEmpty();
        message.flags |= 512;

        MessageObject messageObject = new MessageObject(currentAccount, message, false, true);
        messageObject.isBotPendingDraft = true;
        messageObject.resetLayout();

        return messageObject;
    }

    // user_id > topic_id -> random_id - message
    private final DialogTopicIdKeyMap<BotDraftMessage> botTextDraftsByRandomIds = new DialogTopicIdKeyMap<>();

    public void onBotForumDraftUpdate(long userId, int topicId, long randomId, TLRPC.TL_textWithEntities text) {
        FileLog.d("[BotForum] onDraftNewDraft " + userId + " " + topicId + " " + randomId);

        LongSparseArray<BotDraftMessage> drafts = botTextDraftsByRandomIds.get(userId, topicId);
        long[] toRemove = null;
        if (drafts != null && drafts.size() > 0) {
            toRemove = new long[drafts.size()];
            for (int a = 0, N = drafts.size(); a < N; a++) {
                toRemove[a] = drafts.keyAt(a);
            }
        }

        BotDraftMessage draftMessage = botTextDraftsByRandomIds.get(userId, topicId, randomId);
        if (draftMessage == null) {
            draftMessage = new BotDraftMessage(userId, topicId, randomId, getUserConfig().getNewMessageId());
            botTextDraftsByRandomIds.put(userId, topicId, randomId, draftMessage);
        }

        if (toRemove != null) {
            for (long id: toRemove) {
                if (id == randomId) {
                    continue;
                }
                BotDraftMessage deletedMessage = drafts.get(id);
                if (deletedMessage.selfDestruct != null) {
                    AndroidUtilities.cancelRunOnUIThread(deletedMessage.selfDestruct);
                }
                onBotForumDraftTimeout(userId, topicId, id);
            }
        }

        final boolean isNew = draftMessage.messageObject == null;
        if (draftMessage.selfDestruct != null) {
            AndroidUtilities.cancelRunOnUIThread(draftMessage.selfDestruct);
        }

        draftMessage.selfDestruct = () -> onBotForumDraftTimeout(userId, topicId, randomId);
        draftMessage.text = text;
        draftMessage.messageObject = createDraftMessage(userId, topicId, randomId, draftMessage.localMessageId, text);

        AndroidUtilities.runOnUIThread(draftMessage.selfDestruct, getAppGlobalConfig().messageTypingDraftTtl.get(TimeUnit.MILLISECONDS));

        getNotificationCenter().postNotificationName(NotificationCenter.botForumDraftUpdate,
            new BotForumTextDraftUpdateNotification(userId, topicId, draftMessage.messageObject, isNew));
    }

    public void onBotForumDraftUpdate(long userId, int topicId, long randomId, TL_iv.RichMessage text) {
        FileLog.d("[BotForum] onDraftNewDraft (rich_message) " + userId + " " + topicId + " " + randomId);

        LongSparseArray<BotDraftMessage> drafts = botTextDraftsByRandomIds.get(userId, topicId);
        long[] toRemove = null;
        if (drafts != null && drafts.size() > 0) {
            toRemove = new long[drafts.size()];
            for (int a = 0, N = drafts.size(); a < N; a++) {
                toRemove[a] = drafts.keyAt(a);
            }
        }

        BotDraftMessage draftMessage = botTextDraftsByRandomIds.get(userId, topicId, randomId);
        if (draftMessage == null) {
            draftMessage = new BotDraftMessage(userId, topicId, randomId, getUserConfig().getNewMessageId());
            botTextDraftsByRandomIds.put(userId, topicId, randomId, draftMessage);
        }

        if (toRemove != null) {
            for (long id: toRemove) {
                if (id == randomId) {
                    continue;
                }
                BotDraftMessage deletedMessage = drafts.get(id);
                if (deletedMessage.selfDestruct != null) {
                    AndroidUtilities.cancelRunOnUIThread(deletedMessage.selfDestruct);
                }
                onBotForumDraftTimeout(userId, topicId, id);
            }
        }

        final boolean isNew = draftMessage.messageObject == null;
        if (draftMessage.selfDestruct != null) {
            AndroidUtilities.cancelRunOnUIThread(draftMessage.selfDestruct);
        }

        draftMessage.selfDestruct = () -> onBotForumDraftTimeout(userId, topicId, randomId);
        draftMessage.richMessage = text;
        draftMessage.messageObject = createDraftMessage(userId, topicId, randomId, draftMessage.localMessageId, text);

        AndroidUtilities.runOnUIThread(draftMessage.selfDestruct, getAppGlobalConfig().messageTypingDraftTtl.get(TimeUnit.MILLISECONDS));

        getNotificationCenter().postNotificationName(NotificationCenter.botForumDraftUpdate,
                new BotForumTextDraftUpdateNotification(userId, topicId, draftMessage.messageObject, isNew));
    }

    public boolean hasBotForumDrafts(long userId, int topicId) {
        LongSparseArray<BotDraftMessage> messages = botTextDraftsByRandomIds.get(userId, topicId);
        return messages != null && messages.size() > 0;
    }

    public MessageObject onBotForumDraftCheckNewMessages(long userId, int topicId, int messageId, String message) {
        LongSparseArray<BotDraftMessage> messages = botTextDraftsByRandomIds.get(userId, topicId);
        if (messages == null) {
            return null;
        }

        BotDraftMessage bestDraftMessage = null;
        for (int i = 0; i < messages.size(); i++) {
            final BotDraftMessage draftMessage = messages.valueAt(i);
            if (bestDraftMessage == null) {
                bestDraftMessage = draftMessage;
            }

            if (message != null && draftMessage.text != null && message.startsWith(draftMessage.text.text)) {
                bestDraftMessage = draftMessage;
                break;
            }
        }

        if (bestDraftMessage != null) {
            if (bestDraftMessage.selfDestruct != null) {
                AndroidUtilities.cancelRunOnUIThread(bestDraftMessage.selfDestruct);
            }
            botTextDraftsByRandomIds.remove(userId, topicId, bestDraftMessage.randomId);

            FileLog.d("[BotForum] onDraftNewMessage " + userId + " " + topicId);
            return bestDraftMessage.messageObject;
        }

        return null;
    }

    private void onBotForumDraftTimeout(long userId, int topicId, long randomId) {
        BotDraftMessage draftMessage = botTextDraftsByRandomIds.remove(userId, topicId, randomId);
        if (draftMessage == null) {
            return;
        }

        getNotificationCenter().postNotificationName(NotificationCenter.botForumDraftDelete,
                new BotForumTextDraftDeleteNotification(userId, topicId, draftMessage.localMessageId));
    }





    private static class BotDraftMessage {
        public final long userId;
        public final int topicId;
        public final long randomId;
        public final int localMessageId;

        private Runnable selfDestruct;
        private TLRPC.TL_textWithEntities text;
        private TL_iv.RichMessage richMessage;
        private MessageObject messageObject;

        private BotDraftMessage(long userId, int topicId, long randomId, int localMessageId) {
            this.userId = userId;
            this.topicId = topicId;
            this.randomId = randomId;
            this.localMessageId = localMessageId;
        }
    }


    public static boolean isBotForum(int currentAccount, long dialogId) {
        if (dialogId > 0) {
            return UserObject.isBotForum(MessagesController.getInstance(currentAccount).getUser(dialogId));
        } else {
            MessagesController.getInstance(currentAccount).getChat(-dialogId);
            return false;
        }
    }

    private final SharedPreferences preferences;

    public void saveIsStreamingTopic(long dialogId, long topicId, boolean isStreaming) {
        preferences.edit().putBoolean(dialogId + "_" + topicId, isStreaming).apply();
    }

    public boolean isStreamingTopic(long dialogId, long topicId) {
        return preferences.getBoolean(dialogId + "_" + topicId, false);
    }


    /** Instance **/

    private BotForumHelper(int currentAccount) {
        super(currentAccount);
        preferences = ApplicationLoader.applicationContext.getSharedPreferences("bot_drafts" + currentAccount, Activity.MODE_PRIVATE);
    }

    private static volatile BotForumHelper[] Instance = new BotForumHelper[UserConfig.MAX_ACCOUNT_COUNT];
    public static BotForumHelper getInstance(final int num) {
        BotForumHelper localInstance = Instance[num];
        if (localInstance == null) {
            synchronized (BotForumHelper.class) {
                localInstance = Instance[num];
                if (localInstance == null) {
                    Instance[num] = localInstance = new BotForumHelper(num);
                }
            }
        }
        return localInstance;
    }



    public static class BotDraftAnimationsPool {
        private final DialogTopicIdKeyMap<MultiLayoutTypingAnimator> animators = new DialogTopicIdKeyMap<>();
        private final SparseIntArray ids = new SparseIntArray();   // messageId -> pendingId;

        @Nullable
        public MultiLayoutTypingAnimator getAnimator(long dialogId, int messageId, boolean allowCreate) {
            final int animatorId = messageId > 0 ? ids.get(messageId, 0) : messageId;
            if (animatorId == 0) {
                return null;
            }

            MultiLayoutTypingAnimator animator = animators.get(dialogId, 0, animatorId);
            if (animator == null && allowCreate) {
                animator = new MultiLayoutTypingAnimator();
                animators.put(dialogId, 0, animatorId, animator);
            }

            return animator;
        }

        public void bind(int pendingMessageId, int messageId) {
            ids.put(messageId, pendingMessageId);
        }

        public void removeAnimator(long dialogId, int messageId) {
            final int animatorId = messageId > 0 ? ids.get(messageId, 0) : messageId;
            if (animatorId == 0) {
                return;
            }

            animators.remove(dialogId, 0, animatorId);
        }
    }

    public static class DialogTopicIdKeyMap<T> {
        private final LongSparseArray<LongSparseArray<LongSparseArray<T>>> map = new LongSparseArray<>();

        public LongSparseArray<T> get(long dialogId, long topicId) {
            LongSparseArray<LongSparseArray<T>> topics = map.get(dialogId);
            if (topics == null) {
                return null;
            }

            return topics.get(topicId);
        }

        public T get(long dialogId, long topicId, long messageId) {
            LongSparseArray<T> messages = get(dialogId, topicId);
            if (messages == null) {
                return null;
            }
            return messages.get(messageId);
        }

        public T put(long dialogId, long topicId, long messageId, T value) {
            LongSparseArray<LongSparseArray<T>> topics = map.get(dialogId);
            if (topics == null) {
                topics = new LongSparseArray<>();
                map.put(dialogId, topics);
            }

            LongSparseArray<T> messages = topics.get(topicId);
            if (messages == null) {
                messages = new LongSparseArray<>();
                topics.put(topicId, messages);
            }

            T oldValue = messages.get(messageId);
            messages.put(messageId, value);

            return oldValue;
        }

        public T remove(long dialogId, long topicId, long messageId) {
            LongSparseArray<LongSparseArray<T>> topics = map.get(dialogId);
            if (topics == null) {
                return null;
            }

            LongSparseArray<T> messages = topics.get(topicId);
            if (messages == null) {
                return null;
            }

            T oldValue = messages.get(messageId);
            messages.remove(messageId);

            return oldValue;
        }
    }

    public static CharSequence applyTypingAnimationSpan(CharSequence text) {
        if (text instanceof Spannable) {
            TypingBotSpan[] spans = ((Spannable) text).getSpans(0, text.length(), TypingBotSpan.class);
            if (spans != null && spans.length > 0) {
                return text;
            }
        }

        final SpannableStringBuilder ssb;
        if (text instanceof SpannableStringBuilder) {
            ssb = (SpannableStringBuilder) text;
        } else {
            ssb = new SpannableStringBuilder(text);
        }

        final TypingDotsDrawable typingDotsDrawable = new TypingDotsDrawable(true);
        typingDotsDrawable.setColor(Color.WHITE);
        typingDotsDrawable.start();

        final ColoredImageSpan coloredImageSpan = new TypingBotSpan(typingDotsDrawable, DynamicDrawableSpan.ALIGN_BASELINE);
        coloredImageSpan.setColorKey(Theme.key_chat_messageTextIn);
        coloredImageSpan.setTopOffset(-dp(10));

        ssb.append(" _");
        ssb.setSpan(coloredImageSpan, ssb.length() - 1, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ssb;
    }

    private static class TypingBotSpan extends ColoredImageSpan {
        public TypingBotSpan(TypingDotsDrawable drawable, int align) {
            super(drawable, align);
        }
    }
}
