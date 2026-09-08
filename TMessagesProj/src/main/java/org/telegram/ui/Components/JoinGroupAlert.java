package org.telegram.ui.Components;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import androidx.mediarouter.media.PlatformMediaRouter1RouteProvider;
import com.exteragram.messenger.ExteraConfig;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotGuardHelper;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;

public class JoinGroupAlert extends BottomSheet {
    private BulletinFactory bulletinFactory;
    private TLRPC.ChatInvite chatInvite;
    private TLRPC.Chat currentChat;
    private final BaseFragment fragment;
    private final String hash;
    private RadialProgressView requestProgressView;
    private TextView requestTextView;

    public JoinGroupAlert(Context context, TLObject tLObject, String str, BaseFragment baseFragment, Theme.ResourcesProvider resourcesProvider) {
        this(context, tLObject, str, baseFragment, resourcesProvider, -1);
    }

    void lambda$new$1() {
        if (isDismissed()) {
            return;
        }
        this.requestTextView.setVisibility(4);
        this.requestProgressView.setVisibility(0);
    }

    public void lambda$new$11(final long j, final int i, final TLRPC.TL_messages_importChatInvite tL_messages_importChatInvite, TLRPC.ChatInviteJoinResult chatInviteJoinResult, final TLRPC.TL_error tL_error) {
        final TLRPC.Updates updates;
        if (chatInviteJoinResult instanceof TLRPC.TL_chatInviteJoinResultOk) {
            TLRPC.Updates updates2 = ((TLRPC.TL_chatInviteJoinResultOk) chatInviteJoinResult).updates;
            MessagesController.getInstance(this.currentAccount).processUpdates(updates2, false);
            updates = updates2;
        } else {
            if (chatInviteJoinResult instanceof TLRPC.TL_chatInviteJoinResultWebView) {
                final TLRPC.TL_chatInviteJoinResultWebView tL_chatInviteJoinResultWebView = (TLRPC.TL_chatInviteJoinResultWebView) chatInviteJoinResult;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda8
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$new$9(tL_chatInviteJoinResultWebView, j);
                    }
                });
            }
            updates = null;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.JoinGroupAlert$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$10(tL_error, updates, i, tL_messages_importChatInvite);
            }
        });
    }

    public /* synthetic */ void lambda$new$9(TLRPC.TL_chatInviteJoinResultWebView tL_chatInviteJoinResultWebView, long j) {
        MessagesController.getInstance(this.currentAccount).putUsers(tL_chatInviteJoinResultWebView.users, false);
        BotGuardHelper.getInstance(this.currentAccount).openGuardBotWebApp(j, tL_chatInviteJoinResultWebView.bot_id, tL_chatInviteJoinResultWebView.query_id);
    }

    public /* synthetic */ void lambda$new$10(TLRPC.TL_error tL_error, TLRPC.Updates updates, int i, TLRPC.TL_messages_importChatInvite tL_messages_importChatInvite) {
        TLRPC.ChatInvite chatInvite;
        TLRPC.Chat chat;
        BaseFragment baseFragment = this.fragment;
        if (baseFragment == null || baseFragment.getParentActivity() == null) {
            return;
        }
        if (tL_error == null) {
            if (updates == null || updates.chats.isEmpty()) {
                return;
            }
            TLRPC.Chat chat2 = updates.chats.get(0);
            chat2.left = false;
            chat2.kicked = false;
            MessagesController.getInstance(this.currentAccount).putUsers(updates.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(updates.chats, false);
            openChat(chat2.id, !ChatObject.isChannelAndNotMegaGroup(chat2));
            return;
        }
        if ("USER_ALREADY_PARTICIPANT".equals(tL_error.text) && i == 0 && (chatInvite = this.chatInvite) != null && (chat = chatInvite.chat) != null) {
            openChat(chat.id, false);
        } else {
            AlertsCreator.processError(this.currentAccount, tL_error, this.fragment, tL_messages_importChatInvite, new Object[0]);
        }
    }

    public JoinGroupAlert setBulletinFactory(BulletinFactory bulletinFactory) {
        this.bulletinFactory = bulletinFactory;
        return this;
    }

    private Drawable getVerifiedCrossfadeDrawable() {
        return new CombinedDrawable(Theme.dialogs_verifiedDrawable, Theme.dialogs_verifiedCheckDrawable);
    }

    public static void showBulletin(Context context, BaseFragment baseFragment, boolean z) {
        showBulletin(context, baseFragment, BulletinFactory.of(baseFragment), z);
    }

    public static void showBulletin(Context context, BaseFragment baseFragment, BulletinFactory bulletinFactory, boolean z) {
        String string;
        if (context == null) {
            if (baseFragment != null) {
                baseFragment.getContext();
                return;
            }
            return;
        }
        if (bulletinFactory == null) {
            bulletinFactory = BulletinFactory.of(baseFragment);
        }
        Bulletin.TwoLineLottieLayout twoLineLottieLayout = new Bulletin.TwoLineLottieLayout(context, baseFragment.getResourceProvider());
        twoLineLottieLayout.imageView.setAnimation(R.raw.timer_3, 28, 28);
        twoLineLottieLayout.titleTextView.setText(LocaleController.getString(R.string.RequestToJoinSent));
        if (z) {
            string = LocaleController.getString(R.string.RequestToJoinChannelSentDescription);
        } else {
            string = LocaleController.getString(R.string.RequestToJoinGroupSentDescription);
        }
        twoLineLottieLayout.subtitleTextView.setText(string);
        bulletinFactory.create(twoLineLottieLayout, 2750).show();
    }

    private CharSequence ellipsize(TextView textView, TLRPC.ChatInvite chatInvite, int i) {
        String str = chatInvite.participants.get(i).first_name;
        if (str == null) {
            str = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        return TextUtils.ellipsize(str.trim(), textView.getPaint(), AndroidUtilities.dp(120.0f), TextUtils.TruncateAt.END);
    }

    private Drawable getScamDrawable(int i) {
        return i == 0 ? Theme.dialogs_scamDrawable : Theme.dialogs_fakeDrawable;
    }

    private void openChat(long j, boolean z) {
        Bundle bundle = new Bundle();
        bundle.putLong("chat_id", j);
        if (MessagesController.getInstance(this.currentAccount).checkCanOpenChat(bundle, this.fragment)) {
            AnonymousClass1 anonymousClass1 = new AnonymousClass1(bundle, z, j);
            BaseFragment baseFragment = this.fragment;
            baseFragment.presentFragment(anonymousClass1, baseFragment instanceof ChatActivity);
        }
    }

    public class AnonymousClass1 extends ChatActivity {
        private boolean shownToast;
        final /* synthetic */ long val$chatId;
        final /* synthetic */ boolean val$showJoined;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AnonymousClass1(Bundle bundle, boolean z, long j) {
            super(bundle);
            this.val$showJoined = z;
            this.val$chatId = j;
            this.shownToast = false;
        }

        @Override // org.telegram.ui.ChatActivity, org.telegram.ui.ActionBar.BaseFragment
        public void onBecomeFullyVisible() {
            super.onBecomeFullyVisible();
            if (this.shownToast || !this.val$showJoined) {
                return;
            }
            this.shownToast = true;
            final TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(this.val$chatId));
            if (ChatObject.canManageMyTag(chat)) {
                BulletinFactory bulletinFactoryOf = BulletinFactory.of(this);
                int i = R.raw.contact_check;
                String string = LocaleController.getString(R.string.JoinedGroup);
                String string2 = LocaleController.getString(R.string.JoinedGroupAddTag);
                final long j = this.val$chatId;
                bulletinFactoryOf.createSimpleBulletin(i, string, string2, new Runnable() { // from class: org.telegram.ui.Components.JoinGroupAlert$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onBecomeFullyVisible$0(j, chat);
                    }
                }).hideAfterBottomSheet(false).show(true);
                return;
            }
            BulletinFactory.of(this).createSimpleBulletin(R.raw.contact_check, LocaleController.getString(R.string.JoinedGroup)).hideAfterBottomSheet(false).show(true);
        }

        public /* synthetic */ void lambda$onBecomeFullyVisible$0(long j, TLRPC.Chat chat) {
            if (AndroidUtilities.isContextSafe(getContext())) {
                TagEditCell.showSheet(getContext(), this.currentAccount, -j, getUserConfig().getCurrentUser(), null, chat.admin_rights != null, chat.creator, ((BottomSheet) JoinGroupAlert.this).resourcesProvider);
            }
        }
    }
}
