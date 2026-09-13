package com.exteragram.messenger.feed;

import java.util.ArrayList;
import java.util.function.Consumer;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.ItemOptions;

public abstract class FeedChannelActions {
    public static boolean canLeave(TLRPC.Chat chat) {
        return (chat == null || chat.creator || ChatObject.isNotInChat(chat)) ? false : true;
    }

    public static void showAvatarMenu(final ChatActivity chatActivity, ChatMessageCell chatMessageCell, final TLRPC.Chat chat, Runnable runnable, final Runnable runnable2, final Consumer<ArrayList<Integer>> consumer) {
        if (chatActivity == null || chatMessageCell == null || chat == null) {
            return;
        }
        ItemOptions itemOptionsMakeOptions = ItemOptions.makeOptions(chatActivity, chatMessageCell);
        boolean z = chat.broadcast;
        itemOptionsMakeOptions.add(z ? R.drawable.msg_channel : R.drawable.msg_discussion, LocaleController.getString(z ? R.string.OpenChannel2 : R.string.OpenGroup2), runnable).add(R.drawable.menu_hide_gift, LocaleController.getString(R.string.FeedHideChannel), new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                final long dialogId = -chat.id;
                final int currentAccount = chatActivity.getCurrentAccount();
                FeedConfig.getInstance(currentAccount).setExcluded(dialogId, true);
                FeedController.getInstance(currentAccount).markConfigApplied();
                FeedController.getInstance(currentAccount).getStore().setHidden(dialogId, true);
                org.telegram.ui.Components.BulletinFactory.of(chatActivity).createUndoBulletin(org.telegram.messenger.AndroidUtilities.replaceTags(LocaleController.formatString(R.string.FeedChannelHidden, chat.title)), new Runnable() {
                    @Override
                    public void run() {
                        FeedConfig.getInstance(currentAccount).setExcluded(dialogId, false);
                        FeedController.getInstance(currentAccount).markConfigApplied();
                        FeedController.getInstance(currentAccount).getStore().setHidden(dialogId, false);
                    }
                }, null).show();
            }
        }).addIf(canLeave(chat), R.drawable.msg_leave, (CharSequence) LocaleController.getString(chat.broadcast ? R.string.LeaveChannelMenu : R.string.LeaveMegaMenu), true, new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                FeedChannelActions.leaveChannel(chatActivity, chat, runnable2, consumer);
            }
        }).setDrawScrim(false).setGravity(3).forceBottom(true).show();
    }

    public static void leaveChannel(final BaseFragment baseFragment, final TLRPC.Chat chat, final Runnable runnable, final Consumer<ArrayList<Integer>> consumer) {
        if (baseFragment == null || chat == null || baseFragment.getParentActivity() == null) {
            return;
        }
        AlertsCreator.createClearOrDeleteDialogAlert(baseFragment, false, chat, null, false, true, false, false, new MessagesStorage.BooleanCallback() { 
            @Override 
            public final void run(boolean z) {
                FeedChannelActions.m1147$r8$lambda$93MDwBmj6nCXiUj2TGXsm0wccI(chat, baseFragment, consumer, runnable, z);
            }
        });
    }

    public static /* synthetic */ void m1147$r8$lambda$93MDwBmj6nCXiUj2TGXsm0wccI(TLRPC.Chat chat, BaseFragment baseFragment, Consumer consumer, Runnable runnable, boolean z) {
        long j = -chat.id;
        if (ChatObject.isNotInChat(chat)) {
            baseFragment.getMessagesController().deleteDialog(j, 0, z);
        } else {
            baseFragment.getMessagesController().deleteParticipantFromChat(chat.id, baseFragment.getMessagesController().getUser(Long.valueOf(baseFragment.getUserConfig().getClientUserId())), (TLRPC.Chat) null, z, z);
        }
        deleteFeedRows(baseFragment, j, consumer);
        if (runnable != null) {
            runnable.run();
        }
    }

    private static void deleteFeedRows(BaseFragment baseFragment, long j, Consumer<ArrayList<Integer>> consumer) {
        ArrayList<Integer> arrayListDeleteHistory = FeedController.getInstance(baseFragment.getCurrentAccount()).deleteHistory(j, Integer.MAX_VALUE);
        if (consumer != null) {
            consumer.accept(arrayListDeleteHistory);
        }
    }
}
