package org.telegram.ui.community;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.exteragram.messenger.AvatarCornerType;
import com.exteragram.messenger.ExteraConfig;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.utils.DrawableUtils;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_communities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceColor;
import org.telegram.ui.community.sheet.CommunityAddOptionsSheet;

public class CommunityCreateActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private CommunityHeaderView communityHeaderView;
    private FrameLayout containerView;
    private TLRPC.Chat currentChat;
    private TLRPC.User currentUser;
    private long dialogId;
    private ArrayList<TLRPC.Chat> joinedCommunities;
    private UniversalRecyclerView listView;
    private NotificationCenter.ObserversGroup observersGroup;

    public boolean onLongClick(UItem uItem, View view, int i, float f, float f2) {
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    public CommunityCreateActivity(Bundle bundle) {
        super(bundle);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        this.dialogId = this.arguments.getLong("dialog_id", 0L);
        this.currentChat = getMessagesController().getChat(Long.valueOf(-this.dialogId));
        this.currentUser = getMessagesController().getUser(Long.valueOf(this.dialogId));
        this.joinedCommunities = getMessagesController().getJoinedCommunities();
        getMessagesController().fetchJoinedCommunities(new Utilities.Callback() { // from class: org.telegram.ui.community.CommunityCreateActivity$$ExternalSyntheticLambda3
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$onFragmentCreate$0((ArrayList) obj);
            }
        }, this.classGuid);
        this.observersGroup = getNotificationCenter().createObserversGroup(this).add(NotificationCenter.chatInfoDidLoad);
        return super.onFragmentCreate();
    }

    public void lambda$onClick$2(final String str) {
        getMessagesController().getChat(Long.valueOf(-this.dialogId));
        showDialog(new CommunityAddOptionsSheet(getContext(), null, this.dialogId, new Utilities.Callback() { // from class: org.telegram.ui.community.CommunityCreateActivity$$ExternalSyntheticLambda6
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$onClick$1(str, (Boolean) obj);
            }
        }));
    }

    public void lambda$createNewCommunity$4(AlertDialog alertDialog, String str, boolean z, long j) {
        alertDialog.dismiss();
        if (j == 0) {
            return;
        }
        this.dialogId = -j;
        this.currentChat = getMessagesController().getChat(Long.valueOf(j));
        createNewCommunity(str, z);
    }

    public void lambda$linkToCommunity$6(AlertDialog alertDialog, long j, boolean z, long j2) {
        alertDialog.dismiss();
        if (j2 == 0) {
            return;
        }
        this.dialogId = -j2;
        this.currentChat = getMessagesController().getChat(Long.valueOf(j2));
        linkToCommunity(j, z);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onInsets(int i, int i2, int i3, int i4) {
        super.onInsets(i, i2, i3, i4);
        this.listView.setPadding(0, i2, 0, i4);
    }

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.chatInfoDidLoad) {
            TLRPC.ChatFull chatFull = (TLRPC.ChatFull) objArr[0];
            View viewFindViewByItemId = this.listView.findViewByItemId(Long.hashCode(chatFull.id));
            if (viewFindViewByItemId instanceof ProfileSearchCell) {
                ProfileSearchCell profileSearchCell = (ProfileSearchCell) viewFindViewByItemId;
                ArrayList<TL_communities.CommunityPeer> arrayList = chatFull.linked_peers;
                profileSearchCell.setSubLabel(LocaleController.formatPluralString("Chats", arrayList != null ? arrayList.size() : 0, new Object[0]));
                return;
            }
            this.listView.adapter.update(false);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        NotificationCenter.ObserversGroup observersGroup = this.observersGroup;
        if (observersGroup != null) {
            observersGroup.removeAllObservers();
            this.observersGroup = null;
        }
        super.onFragmentDestroy();
    }

    @SuppressLint({"ViewConstructor"})
    public static class CommunityHeaderView extends FrameLayout implements Theme.Colorable {
        public final BackupImageView avatarView;
        private final Theme.ResourcesProvider resourcesProvider;
        private final TextView subtitleView;
        private final TextView titleView;

        @Override // org.telegram.ui.ActionBar.Theme.Colorable
        public /* bridge */ /* synthetic */ int[] getColorKeys() {
            return super.getColorKeys();
        }

        public CommunityHeaderView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            BackupImageView backupImageView = new BackupImageView(context);
            this.avatarView = backupImageView;
            backupImageView.setRoundRadius(ExteraConfig.getAvatarCorners(72.0f, false, AvatarCornerType.COMMUNITY));
            addView(backupImageView, LayoutHelper.createFrame(72, 72.0f, 49, 0.0f, 36.0f, 0.0f, 0.0f));
            TextView textView = new TextView(context);
            this.titleView = textView;
            textView.setTypeface(AndroidUtilities.bold());
            textView.setTextSize(1, 20.0f);
            textView.setGravity(17);
            addView(textView, LayoutHelper.createFrame(-1, -2.0f, 49, 24.0f, 123.0f, 24.0f, 0.0f));
            TextView textView2 = new TextView(context);
            this.subtitleView = textView2;
            textView2.setTextSize(1, 14.0f);
            textView2.setGravity(17);
            textView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(textView2, LayoutHelper.createFrame(-1, -2.0f, 49, 32.0f, 157.0f, 32.0f, 0.0f));
            updateColors();
        }

        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            DrawableUtils.drawCommunityCardDrawable(canvas, Theme.dialogs_communityCardsDrawable, this.avatarView.getLeft() + (this.avatarView.getWidth() / 2.0f), this.avatarView.getTop() + (this.avatarView.getHeight() / 2.0f), this.avatarView.getHeight());
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(218.0f), TLObject.FLAG_30));
        }

        @Override // org.telegram.ui.ActionBar.Theme.Colorable
        public void updateColors() {
            this.titleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, this.resourcesProvider));
            this.subtitleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText2, this.resourcesProvider));
        }

        public void setTitle(CharSequence charSequence) {
            this.titleView.setText(charSequence);
        }

        public void setSubtitle(CharSequence charSequence) {
            this.subtitleView.setText(charSequence);
        }
    }
}
