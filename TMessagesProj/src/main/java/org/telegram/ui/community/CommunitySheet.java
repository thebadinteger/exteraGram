package org.telegram.ui.community;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.AvatarCornerType;
import com.exteragram.messenger.ExteraConfig;
import java.util.ArrayList;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.utils.FBool;
import org.telegram.messenger.utils.GradientProtectionDrawable;
import org.telegram.messenger.utils.TextWatcherImpl;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.FragmentSearchField;
import org.telegram.ui.Components.IconBackgroundColors;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.Components.chat.layouts.ChatActivityFadeView;
import org.telegram.ui.FilteredSearchView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.TopicsFragment;
import org.telegram.ui.community.cells.CommunityPendingRequestCell;
import org.telegram.ui.community.cells.CommunityRequestsCell;
import org.telegram.ui.community.sheet.CommunityAddOptionsSheet;
import org.telegram.ui.community.sheet.CommunityInviteOnlySheet;

public class CommunitySheet extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, FactorAnimator.Target {
    private ButtonWithCounterView addChatToCommunityButton;
    private final BoolAnimator animatorSearchChatsVisible;
    private final BoolAnimator animatorSearchMessagesVisible;
    private final Paint backgroundPaint;
    private final ChatsToAddListPage chatsPage;
    private final FadeView chatsPageFadeView;
    private final FragmentSearchField chatsSearchView;
    private final Utilities.Callback<TLRPC.Chat> chatsToAddCallback;
    private ArrayList<TLRPC.Chat> chatsToAddToCommunity;
    private ButtonWithCounterView closeChatToCommunityButton;
    private boolean collapsedInDialogs;
    private final long communityId;
    private TLRPC.ChatFull communityInfo;
    private final CommunityPage communityPage;
    private final FadeView communityPageFadeView;
    private TLRPC.Chat currentCommunity;
    private final View fakeAnchorView;
    private final FilteredSearchView filteredSearchView;
    private final UniversalRecyclerView foundChatsView;
    private final GradientProtectionDrawable gradientProtectionDrawableBottom;
    private final GradientProtectionDrawable gradientProtectionDrawableTop;
    private String lastSearchChatsString;
    private String lastSearchString;
    private final FragmentSearchField messagesSearchView;
    private final boolean onlyChatsMode;
    private final BaseFragment parentFragment;
    private CommunityUtils.PendingRequests pendingRequestsList;
    private final PendingRequestsPage requestsPage;
    private Insets systemAndImeInsets;
    private Insets systemInsets;
    private ViewPagerFixed viewPager;

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean canSwipeToBack(MotionEvent motionEvent) {
        return false;
    }

    public CommunitySheet(BaseFragment baseFragment, long j) {
        this(baseFragment, j, null, null);
    }

    public CommunitySheet(final BaseFragment baseFragment, long j, ArrayList<TLRPC.Chat> arrayList, Utilities.Callback<TLRPC.Chat> callback) {
        super(baseFragment.getContext(), true, true, baseFragment.getResourceProvider());
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.animatorSearchMessagesVisible = new BoolAnimator(1, this, cubicBezierInterpolator, 350L);
        this.animatorSearchChatsVisible = new BoolAnimator(2, this, cubicBezierInterpolator, 350L);
        this.gradientProtectionDrawableTop = new GradientProtectionDrawable(2);
        this.gradientProtectionDrawableBottom = new GradientProtectionDrawable(8);
        this.backgroundPaint = new Paint(1);
        Insets insets = Insets.NONE;
        this.systemAndImeInsets = insets;
        this.systemInsets = insets;
        AndroidUtilities.enableEdgeToEdge(getWindow());
        this.parentFragment = baseFragment;
        this.onlyChatsMode = arrayList != null;
        this.chatsToAddToCommunity = arrayList;
        this.chatsToAddCallback = callback;
        Context context = baseFragment.getContext();
        init(context);
        this.communityPageFadeView = new FadeView(context);
        this.chatsPageFadeView = new FadeView(context);
        FragmentSearchField fragmentSearchField = new FragmentSearchField(context, this.resourcesProvider);
        this.messagesSearchView = fragmentSearchField;
        fragmentSearchField.setCloseButtonVisible(true);
        fragmentSearchField.setWhiteBackground();
        fragmentSearchField.editText.setHint(LocaleController.getString(R.string.Search));
        fragmentSearchField.editText.addTextChangedListener(new TextWatcherImpl() { // from class: org.telegram.ui.community.CommunitySheet.1
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                CommunitySheet.this.onMessagesSearchTextChanged(editable.toString());
            }
        });
        fragmentSearchField.setVisibility(8);
        FragmentSearchField fragmentSearchField2 = new FragmentSearchField(context, this.resourcesProvider);
        this.chatsSearchView = fragmentSearchField2;
        fragmentSearchField2.setCloseButtonVisible(true);
        fragmentSearchField2.setWhiteBackground();
        fragmentSearchField2.editText.setHint(LocaleController.getString(R.string.Search));
        fragmentSearchField2.editText.addTextChangedListener(new TextWatcherImpl() { // from class: org.telegram.ui.community.CommunitySheet.2
            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                CommunitySheet.this.onChatsSearchTextChanged(editable.toString());
            }
        });
        fragmentSearchField2.setVisibility(8);
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(context, this.currentAccount, 0, new Utilities.Callback2() { // from class: org.telegram.ui.community.CommunitySheet$$ExternalSyntheticLambda0
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.fillItemsChatsToAddSearch((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() { // from class: org.telegram.ui.community.CommunitySheet$$ExternalSyntheticLambda1
            @Override 
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                this.f$0.onClickChatToAdd((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, null, this.resourcesProvider);
        this.foundChatsView = universalRecyclerView;
        universalRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.community.CommunitySheet.3
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                super.onScrolled(recyclerView, i, i2);
                AndroidUtilities.hideKeyboard(CommunitySheet.this.chatsSearchView.editText);
            }
        });
        universalRecyclerView.setClipToPadding(false);
        universalRecyclerView.setVisibility(8);
        universalRecyclerView.setSections();
        universalRecyclerView.adapter.setApplyBackground(false);
        universalRecyclerView.setPadding(0, AndroidUtilities.statusBarHeight + AndroidUtilities.dp(52.0f), 0, AndroidUtilities.navigationBarHeight);
        FilteredSearchView filteredSearchView = new FilteredSearchView(baseFragment);
        this.filteredSearchView = filteredSearchView;
        filteredSearchView.setVisibility(8);
        filteredSearchView.setBackground(null);
        filteredSearchView.setChatPreviewDelegate(new SearchViewPager.ChatPreviewDelegate() { // from class: org.telegram.ui.community.CommunitySheet.4
            @Override // org.telegram.ui.Components.SearchViewPager.ChatPreviewDelegate
            public void finish() {
            }

            @Override // org.telegram.ui.Components.SearchViewPager.ChatPreviewDelegate
            public void move(float f) {
            }

            @Override // org.telegram.ui.Components.SearchViewPager.ChatPreviewDelegate
            public void startChatPreview(RecyclerListView recyclerListView, DialogCell dialogCell) {
            }
        });
        filteredSearchView.setUiCallback(new FilteredSearchView.UiCallback() { // from class: org.telegram.ui.community.CommunitySheet.5
            @Override // org.telegram.ui.FilteredSearchView.UiCallback
            public boolean actionModeShowing() {
                return false;
            }

            @Override // org.telegram.ui.FilteredSearchView.UiCallback
            public boolean isSelected(FilteredSearchView.MessageHashId messageHashId) {
                return false;
            }

            @Override // org.telegram.ui.FilteredSearchView.UiCallback
            public void showActionMode() {
            }

            @Override // org.telegram.ui.FilteredSearchView.UiCallback
            public void toggleItemSelection(MessageObject messageObject, View view, int i) {
            }

            @Override // org.telegram.ui.FilteredSearchView.UiCallback
            public void goToMessage(MessageObject messageObject) {
                CommunitySheet.this.parentFragment.presentFragment(SearchViewPager.createFragmentFromMessage(((BottomSheet) CommunitySheet.this).currentAccount, messageObject));
                CommunitySheet.this.lambda$new$0();
            }
        });
        filteredSearchView.recyclerListView.setClipToPadding(false);
        this.fakeAnchorView = new View(getContext());
        Context context2 = getContext();
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        CommunityUtils.PendingRequests pendingRequests = new CommunityUtils.PendingRequests(context2, resourcesProvider, BulletinFactory.of((FrameLayout) this.containerView, resourcesProvider), this.currentAccount, j);
        this.pendingRequestsList = pendingRequests;
        pendingRequests.setDelegate(new CommunityUtils.PendingRequests.Delegate() { // from class: org.telegram.ui.community.CommunitySheet.6
            @Override // org.telegram.ui.community.CommunityUtils.PendingRequests.Delegate
            public void updateAdapter() {
                CommunitySheet.this.requestsPage.listView.adapter.update(true);
                CommunitySheet.this.communityPage.listView.adapter.update(true);
            }

            @Override // org.telegram.ui.community.CommunityUtils.PendingRequests.Delegate
            public void close() {
                CommunitySheet.this.viewPager.scrollToPosition(0);
            }

            @Override // org.telegram.ui.community.CommunityUtils.PendingRequests.Delegate
            public void onClickGroupOwner(long j2) {
                baseFragment.presentFragment(ChatActivity.of(j2));
                CommunitySheet.this.lambda$new$0();
            }
        });
        this.communityId = j;
        this.currentCommunity = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j));
        this.communityInfo = MessagesController.getInstance(this.currentAccount).getChatFull(j);
        TLRPC.Chat chat = this.currentCommunity;
        this.collapsedInDialogs = chat != null && chat.collapsed_in_dialogs;
        FiltersView.MediaFilterData mediaFilterData = new FiltersView.MediaFilterData(R.drawable.search_users_filled, DialogObject.getShortName(chat), (TLRPC.MessagesFilter) null, 4);
        mediaFilterData.setUser(this.currentCommunity);
        mediaFilterData.removable = false;
        fragmentSearchField.addSearchFilter(mediaFilterData);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundGray, this.resourcesProvider));
        this.requestsPage = new PendingRequestsPage(context);
        this.communityPage = new CommunityPage(context);
        this.chatsPage = new ChatsToAddListPage(context);
        this.viewPager.setAdapter(new ViewPagerFixed.Adapter() { // from class: org.telegram.ui.community.CommunitySheet.7
            @Override // org.telegram.ui.Components.ViewPagerFixed.Adapter
            public int getItemCount() {
                return CommunitySheet.this.onlyChatsMode ? 1 : 3;
            }

            @Override // org.telegram.ui.Components.ViewPagerFixed.Adapter
            public View createView(int i) {
                CommunitySheet communitySheet = CommunitySheet.this;
                if (i == 2) {
                    return communitySheet.chatsPage;
                }
                return i == 0 ? communitySheet.communityPage : communitySheet.requestsPage;
            }

            @Override // org.telegram.ui.Components.ViewPagerFixed.Adapter
            public int getItemViewType(int i) {
                if (CommunitySheet.this.onlyChatsMode || i == 2) {
                    return 2;
                }
                return i == 0 ? 0 : 1;
            }

            @Override // org.telegram.ui.Components.ViewPagerFixed.Adapter
            public void bindView(View view, int i, int i2) {
                ((Page) view).bind(i2);
            }
        });
        fragmentSearchField.setCloseButtonOnClickListener(new Runnable() { // from class: org.telegram.ui.community.CommunitySheet$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$0();
            }
        });
        fragmentSearchField2.setCloseButtonOnClickListener(new Runnable() { // from class: org.telegram.ui.community.CommunitySheet$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$1();
            }
        });
        this.pendingRequestsList.loadNext();
        MessagesController.getInstance(this.currentAccount).loadFullChat(j, 0, true);
        Bulletin.addDelegate((FrameLayout) this.containerView, new Bulletin.Delegate() { // from class: org.telegram.ui.community.CommunitySheet.8
            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public int getBottomOffset(int i) {
                return AndroidUtilities.navigationBarHeight + AndroidUtilities.dp(60.0f);
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(this.containerView, new OnApplyWindowInsetsListener() { // from class: org.telegram.ui.community.CommunitySheet$$ExternalSyntheticLambda4
            @Override // androidx.core.view.OnApplyWindowInsetsListener
            public final WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
                return this.f$0.onApplyWindowInsets(view, windowInsetsCompat);
            }
        });
    }

    public void lambda$new$1() {
        this.chatsPage.listView.layoutManager.scrollToPositionWithOffset(1, this.systemInsets.top);
        this.animatorSearchChatsVisible.setValue(false, true);
        setAllowNestedScroll(true);
        AndroidUtilities.hideKeyboard(this.chatsSearchView.editText);
        this.chatsSearchView.editText.clearFocus();
    }

    public void fillItemsCommunity(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        String pluralString;
        arrayList.add(UItem.asSpace(99, Math.min(AndroidUtilities.statusBarHeight + AndroidUtilities.dp(176.0f), (int) (AndroidUtilities.displaySize.y * 0.25f))));
        arrayList.add(UItem.asSpace(0, AndroidUtilities.dp(56.0f)));
        arrayList.add(UItem.asSwitchNoIcon(101, LocaleController.getString(R.string.CommunityShowAsOneChat)).setChecked(this.collapsedInDialogs));
        arrayList.add(UItem.asShadow(2, LocaleController.getString(R.string.CommunityShowAsOneChatInfo)));
        if (this.pendingRequestsList.isSingle()) {
            arrayList.add(UItem.asHeader(3, LocaleController.getString(R.string.CommunityPendingRequest)));
            this.pendingRequestsList.fillItems(arrayList);
            arrayList.add(UItem.asSpace(5, AndroidUtilities.dp(14.33f)));
        } else if (this.pendingRequestsList.getTotalCount() > 0) {
            int totalCount = this.pendingRequestsList.getTotalCount();
            int unreadCount = this.pendingRequestsList.getUnreadCount();
            IconBackgroundColors iconBackgroundColors = IconBackgroundColors.BLUE_ALT;
            int i = R.drawable.filled_requests_24;
            if (totalCount == unreadCount) {
                pluralString = LocaleController.getString(R.string.CommunityPendingRequests);
            } else {
                pluralString = LocaleController.formatPluralString("CommunityPendingRequestsRow", totalCount, new Object[0]);
            }
            arrayList.add(CommunityRequestsCell.Factory.of(100, iconBackgroundColors, i, pluralString, unreadCount > 0 ? Integer.toString(unreadCount) : null, true));
            arrayList.add(UItem.asSpace(5, AndroidUtilities.dp(14.33f)));
        }
        CommunityUtils.fillLinkedPeers(this.currentAccount, arrayList, this.communityId, true);
    }

    public void fillItemsRequests(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asSpace(99, (int) (AndroidUtilities.displaySize.y * 0.35f)));
        arrayList.add(UItem.asSpace(0, AndroidUtilities.dp(48.0f)));
        if (ChatObject.canBlockUsers(this.currentCommunity)) {
            arrayList.add(UItem.asShadow(1, AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.CommunityPendingRequestsInfo), new Runnable() { // from class: org.telegram.ui.community.CommunitySheet$$ExternalSyntheticLambda8
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$fillItemsRequests$2();
                }
            }), true)));
        } else {
            arrayList.add(UItem.asShadow(1, LocaleController.getString(R.string.CommunityPendingRequestsInfoNoChange)));
        }
        arrayList.add(UItem.asCustom(2, this.fakeAnchorView));
        arrayList.add(UItem.asHeader(3, LocaleController.formatPluralString("CommunityPendingRequestsSuggestedHeader", this.pendingRequestsList.getTotalCount(), new Object[0])));
        this.pendingRequestsList.fillItems(arrayList);
    }

    public void lambda$onClickChatToAdd$3(TLRPC.Chat chat, Boolean bool) {
        linkToCommunity(chat, this.communityId, bool.booleanValue());
    }

    public void onClickCommunity(UItem uItem, View view, int i, float f, float f2) {
        long j;
        TLRPC.Chat chat;
        int i2;
        if (checkPendingRequestClick(uItem)) {
            return;
        }
        int i3 = uItem.id;
        boolean zIsChannelAndNotMegaGroup = false;
        if (i3 == 101) {
            this.collapsedInDialogs = !this.collapsedInDialogs;
            MessagesController.getInstance(this.currentAccount).toggleCommunityCollapsedInDialogs(this.communityId, this.collapsedInDialogs);
            if (view instanceof TextCheckCell2) {
                ((TextCheckCell2) view).getCheckBox().setChecked(this.collapsedInDialogs, true);
                return;
            } else {
                this.communityPage.listView.adapter.update(false);
                return;
            }
        }
        if (i3 == 100) {
            this.viewPager.scrollToPosition(1);
            this.pendingRequestsList.markAsViewed();
            return;
        }
        Object obj = uItem.object;
        if (obj instanceof TLRPC.Chat) {
            chat = (TLRPC.Chat) obj;
            zIsChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(chat);
            j = -chat.id;
        } else {
            if (!(obj instanceof TLRPC.User)) {
                return;
            }
            j = ((TLRPC.User) obj).id;
            chat = null;
        }
        TLRPC.Chat chat2 = chat;
        CommunityChatType communityChatType = CommunityUtils.getCommunityChatType(this.currentAccount, j);
        if (communityChatType == CommunityChatType.YouAreIn || communityChatType == CommunityChatType.YouCanView) {
            BaseFragment baseFragment = this.parentFragment;
            if (baseFragment instanceof ChatActivity) {
                TLRPC.Chat currentChat = ((ChatActivity) baseFragment).getCurrentChat();
                TLRPC.User currentUser = ((ChatActivity) this.parentFragment).getCurrentUser();
                if ((currentChat != null && currentChat.id == (-j)) || (currentUser != null && currentUser.id == j)) {
                    lambda$new$0();
                    return;
                }
            }
            Bundle bundle = new Bundle();
            if (j > 0) {
                bundle.putLong("user_id", j);
            } else {
                bundle.putLong("chat_id", -j);
            }
            if (ChatObject.isForum(chat2)) {
                if (ChatObject.areTabsEnabled(chat2)) {
                    ChatActivity chatActivity = new ChatActivity(bundle);
                    ForumUtilities.applyTopic(chatActivity, MessagesStorage.TopicKey.of(j, MessagesController.getInstance(this.currentAccount).getForumLastTopicId(chat2.id)));
                    this.parentFragment.presentFragment(chatActivity);
                } else {
                    this.parentFragment.presentFragment(new TopicsFragment(bundle));
                }
            } else {
                this.parentFragment.presentFragment(new ChatActivity(bundle));
            }
            lambda$new$0();
            return;
        }
        if (communityChatType == CommunityChatType.YouCanSendJoinRequest) {
            new JoinGroupAlert(getContext(), chat2, null, this.parentFragment, this.resourcesProvider).setBulletinFactory(BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider)).show();
            return;
        }
        if (communityChatType == CommunityChatType.HiddenUnavailable) {
            BulletinFactory bulletinFactoryOf = BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider);
            int i4 = R.raw.e_hand_2;
            if (zIsChannelAndNotMegaGroup) {
                i2 = R.string.CommunityHiddenChannelUnavailable;
            } else {
                i2 = R.string.CommunityHiddenGroupUnavailable;
            }
            bulletinFactoryOf.createSimpleBulletin(i4, LocaleController.getString(i2)).show();
        }
    }

    public boolean onLongClickCommunity(UItem uItem, View view, int i, float f, float f2) {
        long j;
        boolean zCanRemoveBotFromCommunity;
        final boolean z;
        final boolean z2;
        Object obj = uItem.object;
        if (obj instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) obj;
            j = -chat.id;
            boolean zIsChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(chat);
            zCanRemoveBotFromCommunity = ChatObject.canRemoveChatFromCommunity(chat, this.currentCommunity);
            z2 = zIsChannelAndNotMegaGroup;
            z = false;
        } else {
            if (!(obj instanceof TLRPC.User)) {
                return false;
            }
            TLRPC.User user = (TLRPC.User) obj;
            j = user.id;
            boolean zIsBot = UserObject.isBot(user);
            zCanRemoveBotFromCommunity = ChatObject.canRemoveBotFromCommunity(user, this.currentCommunity);
            z = zIsBot;
            z2 = false;
        }
        final long j2 = j;
        if (!zCanRemoveBotFromCommunity) {
            return false;
        }
        ItemOptions itemOptionsMakeOptions = ItemOptions.makeOptions(this.container, view);
        itemOptionsMakeOptions.add(R.drawable.msg_cancel, (CharSequence) LocaleController.getString(R.string.CommunityMenuRemoveFromCommunity), true, new Runnable() { // from class: org.telegram.ui.community.CommunitySheet$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onLongClickCommunity$6(z, z2, j2);
            }
        });
        itemOptionsMakeOptions.setScrimViewBackground(this.communityPage.listView.getClipBackground(view, true));
        itemOptionsMakeOptions.show();
        return true;
    }

    public void lambda$onLongClickCommunity$4(TLRPC.Bool bool, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).showForError(tL_error);
        }
    }

    public void onClickRequest(UItem uItem, View view, int i, float f, float f2) {
        checkPendingRequestClick(uItem);
    }

    private boolean checkPendingRequestClick(UItem uItem) {
        Object obj = uItem.object;
        if (!(obj instanceof CommunityPendingRequestCell.Data)) {
            return false;
        }
        final CommunityPendingRequestCell.Data data = (CommunityPendingRequestCell.Data) obj;
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-data.dialogToAdd));
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(data.dialogToAdd));
        if (user != null) {
            this.parentFragment.presentFragment(ChatActivity.of(user.id));
            return true;
        }
        if (ChatObject.isPublic(chat) || ChatObject.isInChat(chat)) {
            this.parentFragment.presentFragment(ChatActivity.of(-chat.id));
            return true;
        }
        new CommunityInviteOnlySheet(getContext(), chat, data.requestFromUser, new Runnable() { // from class: org.telegram.ui.community.CommunitySheet$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$checkPendingRequestClick$7(data);
            }
        }).show();
        return true;
    }

    public void lambda$new$0(View view) {
            CommunitySheet.this.onAddChatToCommunityButtonClick();
        }

        @Override // org.telegram.ui.community.CommunitySheet.Page
        public float top() {
            return super.top() * FBool.not(CommunitySheet.this.animatorSearchMessagesVisible.getFloatValue());
        }

        @Override // org.telegram.ui.community.CommunitySheet.Page
        public void updateTops() {
            super.updateTops();
            CommunitySheet.this.messagesSearchView.setTranslationY(Math.max(AndroidUtilities.statusBarHeight + AndroidUtilities.dp(8.0f), top() + AndroidUtilities.dp(4.0f)));
        }
    }

    public void onAddChatToCommunityButtonClick() {
        if (!ChatObject.canAddChatToCommunity(this.currentCommunity)) {
            lambda$new$0();
        } else {
            loadChatsToAddToCommunity();
        }
    }

    private void loadChatsToAddToCommunity() {
        if (this.addChatToCommunityButton.isLoading()) {
            return;
        }
        this.addChatToCommunityButton.setLoading(true);
        MessagesController.getInstance(this.currentAccount).fetchChatsToAddToCommunity(new Utilities.Callback2() { // from class: org.telegram.ui.community.CommunitySheet$$ExternalSyntheticLambda9
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$loadChatsToAddToCommunity$8((ArrayList) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$new$0(View view) {
            CommunitySheet.this.viewPager.scrollToPosition(0);
        }

        @Override // org.telegram.ui.community.CommunitySheet.Page
        public void updateTops() {
            super.updateTops();
            CommunitySheet.this.chatsSearchView.setTranslationY(Math.max(AndroidUtilities.statusBarHeight + AndroidUtilities.dp(8.0f), top() + AndroidUtilities.dp(4.0f)));
        }

        @Override // org.telegram.ui.community.CommunitySheet.Page
        public float top() {
            return super.top() * FBool.not(CommunitySheet.this.animatorSearchChatsVisible.getFloatValue());
        }
    }

    public class PendingRequestsPage extends Page {
        public PendingRequestsPage(Context context) {
            super(context);
            UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(context, ((BottomSheet) CommunitySheet.this).currentAccount, 0, new Utilities.Callback2() { // from class: org.telegram.ui.community.CommunitySheet$PendingRequestsPage$$ExternalSyntheticLambda0
                @Override 
                public final void run(Object obj, Object obj2) {
                    communitySheet.fillItemsRequests((ArrayList) obj, (UniversalAdapter) obj2);
                }
            }, new Utilities.Callback5() { // from class: org.telegram.ui.community.CommunitySheet$PendingRequestsPage$$ExternalSyntheticLambda1
                @Override 
                public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                    communitySheet.onClickRequest((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
                }
            }, null, ((BottomSheet) CommunitySheet.this).resourcesProvider);
            this.listView = universalRecyclerView;
            universalRecyclerView.setSections();
            this.listView.adapter.setApplyBackground(false);
            this.listView.setClipToPadding(false);
            this.listView.setPadding(0, 0, 0, AndroidUtilities.navigationBarHeight + AndroidUtilities.dp(60.0f));
            this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.community.CommunitySheet.PendingRequestsPage.1
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    super.onScrolled(recyclerView, i, i2);
                    CommunitySheet.this.pendingRequestsList.checkLoadNext(PendingRequestsPage.this.listView);
                }
            });
            this.contentView.addView(this.listView, 0, LayoutHelper.createFrame(-1, -1.0f));
            ActionBar actionBar = new ActionBar(context, ((BottomSheet) CommunitySheet.this).resourcesProvider);
            this.actionBar = actionBar;
            actionBar.setOccupyStatusBar(false);
            ActionBar actionBar2 = this.actionBar;
            int i = Theme.key_windowBackgroundWhiteBlackText;
            actionBar2.setTitleColor(CommunitySheet.this.getThemedColor(i));
            this.actionBar.setItemsBackgroundColor(CommunitySheet.this.getThemedColor(Theme.key_actionBarActionModeDefaultSelector), false);
            this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
            this.actionBar.setItemsColor(CommunitySheet.this.getThemedColor(Theme.key_actionBarActionModeDefaultIcon), false);
            this.actionBar.setTitle(LocaleController.getString(R.string.CommunityPendingRequestsTitle));
            this.actionBar.getTitleTextView().setTranslationX(-AndroidUtilities.dp(18.0f));
            this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.community.CommunitySheet.PendingRequestsPage.2
                @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
                public void onItemClick(int i2) {
                    if (i2 == -1) {
                        CommunitySheet.this.communityPage.listView.adapter.update(false);
                        CommunitySheet.this.viewPager.scrollToPosition(0);
                    }
                }
            });
            this.contentView.addView(this.actionBar, LayoutHelper.createFrame(-1, 56, 48));
            LinearLayout linearLayout = new LinearLayout(context);
            linearLayout.setOrientation(0);
            linearLayout.setPadding(AndroidUtilities.dp(7.0f), 0, AndroidUtilities.dp(7.0f), AndroidUtilities.dp(12.0f));
            ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, ((BottomSheet) CommunitySheet.this).resourcesProvider);
            buttonWithCounterView.setNeutral();
            buttonWithCounterView.setColor(ColorUtils.blendARGB(CommunitySheet.this.getThemedColor(Theme.key_windowBackgroundWhite), CommunitySheet.this.getThemedColor(i), 0.125f));
            buttonWithCounterView.setText(LocaleController.getString(R.string.CommunityPendingRequestDeclineAll));
            buttonWithCounterView.setRound();
            buttonWithCounterView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.community.CommunitySheet$PendingRequestsPage$$ExternalSyntheticLambda2
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$new$0(view);
                }
            });
            linearLayout.addView(buttonWithCounterView, LayoutHelper.createLinear(0, 48, 1.0f, 0, 4, 0, 4, 0));
            ButtonWithCounterView buttonWithCounterView2 = new ButtonWithCounterView(context, ((BottomSheet) CommunitySheet.this).resourcesProvider);
            buttonWithCounterView2.setText(LocaleController.getString(R.string.CommunityPendingRequestAddAll));
            buttonWithCounterView2.setRound();
            buttonWithCounterView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.community.CommunitySheet$PendingRequestsPage$$ExternalSyntheticLambda3
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$new$1(view);
                }
            });
            linearLayout.addView(buttonWithCounterView2, LayoutHelper.createLinear(0, 48, 1.0f, 0, 4, 0, 4, 0));
            this.contentView.addView(linearLayout, LayoutHelper.createFrameMarginPx(-1, -2.0f, 80, 0, 0, 0, AndroidUtilities.navigationBarHeight));
            afterInit();
        }

        public void lambda$linkToCommunity$9(AlertDialog alertDialog, long j, boolean z, long j2) {
        alertDialog.dismiss();
        if (j2 == 0) {
            return;
        }
        linkToCommunity(MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(j2)), j, z);
    }

    public /* synthetic */ void lambda$linkToCommunity$10(boolean z, TLRPC.Bool bool, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            if (TextUtils.equals("COMMUNITY_REQUEST_CREATED", tL_error.text)) {
                onLinkSuccess(2, z);
                return;
            } else {
                BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider).showForError(tL_error);
                return;
            }
        }
        onLinkSuccess(1, z);
    }

    private void onLinkSuccess(int i, boolean z) {
        CommunityUtils.showCommunityLinkSuccessToast(BulletinFactory.of((FrameLayout) this.containerView, this.resourcesProvider), i, z);
        this.viewPager.scrollToPosition(0);
    }

    public void onChatsSearchTextChanged(String str) {
        this.lastSearchChatsString = str;
        this.foundChatsView.adapter.update(true);
    }

    public void onMessagesSearchTextChanged(String str) {
        onMessagesSearchTextChanged(str, false);
    }

    public void onMessagesSearchTextChanged(String str, boolean z) {
        if (TextUtils.isEmpty(this.lastSearchString)) {
            z = true;
        }
        this.lastSearchString = str;
        this.filteredSearchView.search(0L, this.communityId, 0L, 0L, null, false, str, z);
    }

    public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
        this.systemAndImeInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime());
        this.systemInsets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars());
        this.filteredSearchView.setPagesPaddings(this.systemAndImeInsets.top + AndroidUtilities.dp(56.0f), this.systemAndImeInsets.bottom);
        this.communityPageFadeView.invalidate();
        return WindowInsetsCompat.CONSUMED;
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        if (i == 1) {
            float fNot = FBool.not(f);
            float fLerp = AndroidUtilities.lerp(0.9f, 1.0f, fNot);
            this.communityPage.actionBar.setAlpha(fNot);
            this.communityPage.actionBar.setScaleX(fLerp);
            this.communityPage.actionBar.setScaleY(fLerp);
            this.communityPage.actionBar.setVisibility(fNot > 0.0f ? 0 : 8);
            float fLerp2 = AndroidUtilities.lerp(0.9f, 1.0f, f);
            this.messagesSearchView.setAlpha(f);
            this.messagesSearchView.setScaleX(fLerp2);
            this.messagesSearchView.setScaleY(fLerp2);
            this.messagesSearchView.setVisibility(f > 0.0f ? 0 : 8);
            this.communityPage.listView.setAlpha(fNot);
            this.communityPage.listView.setVisibility(fNot > 0.0f ? 0 : 8);
            this.addChatToCommunityButton.setAlpha(fNot);
            this.addChatToCommunityButton.setScaleX(AndroidUtilities.lerp(0.95f, 1.0f, fNot));
            this.addChatToCommunityButton.setScaleY(AndroidUtilities.lerp(0.95f, 1.0f, fNot));
            this.addChatToCommunityButton.setVisibility(fNot > 0.0f ? 0 : 8);
            this.filteredSearchView.setAlpha(f);
            this.filteredSearchView.setVisibility(f > 0.0f ? 0 : 8);
            this.containerView.invalidate();
            this.communityPageFadeView.invalidate();
        }
        if (i == 2) {
            float fNot2 = FBool.not(f);
            float fLerp3 = AndroidUtilities.lerp(0.9f, 1.0f, fNot2);
            this.chatsPage.actionBar.setAlpha(fNot2);
            this.chatsPage.actionBar.setScaleX(fLerp3);
            this.chatsPage.actionBar.setScaleY(fLerp3);
            this.chatsPage.actionBar.setVisibility(fNot2 > 0.0f ? 0 : 8);
            float fLerp4 = AndroidUtilities.lerp(0.9f, 1.0f, f);
            this.chatsSearchView.setAlpha(f);
            this.chatsSearchView.setScaleX(fLerp4);
            this.chatsSearchView.setScaleY(fLerp4);
            this.chatsSearchView.setVisibility(f > 0.0f ? 0 : 8);
            this.chatsPage.listView.setAlpha(fNot2);
            this.chatsPage.listView.setVisibility(fNot2 > 0.0f ? 0 : 8);
            if (!this.onlyChatsMode) {
                this.closeChatToCommunityButton.setAlpha(fNot2);
                this.closeChatToCommunityButton.setScaleX(AndroidUtilities.lerp(0.95f, 1.0f, fNot2));
                this.closeChatToCommunityButton.setScaleY(AndroidUtilities.lerp(0.95f, 1.0f, fNot2));
                this.closeChatToCommunityButton.setVisibility(fNot2 > 0.0f ? 0 : 8);
            }
            this.foundChatsView.setAlpha(f);
            this.foundChatsView.setVisibility(f > 0.0f ? 0 : 8);
            this.containerView.invalidate();
            this.chatsPageFadeView.invalidate();
        }
    }

    public class FadeView extends View {
        public FadeView(Context context) {
            super(context);
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float fMax = Math.max(CommunitySheet.this.animatorSearchMessagesVisible.getFloatValue(), CommunitySheet.this.animatorSearchChatsVisible.getFloatValue());
            CommunitySheet.this.gradientProtectionDrawableTop.setInsets(0, CommunitySheet.this.systemInsets.top + AndroidUtilities.dp(42.0f), 0, 0);
            CommunitySheet.this.gradientProtectionDrawableTop.setBounds(0, 0, getWidth(), CommunitySheet.this.systemInsets.top + AndroidUtilities.dp(56.0f));
            GradientProtectionDrawable gradientProtectionDrawable = CommunitySheet.this.gradientProtectionDrawableTop;
            CommunitySheet communitySheet = CommunitySheet.this;
            int i = Theme.key_windowBackgroundGray;
            gradientProtectionDrawable.setColor(Theme.multAlpha(communitySheet.getThemedColor(i), AndroidUtilities.lerp(1.0f, 0.8f, fMax)));
            CommunitySheet.this.gradientProtectionDrawableTop.draw(canvas);
            if (CommunitySheet.this.onlyChatsMode) {
                fMax = 1.0f;
            }
            int iLerp = AndroidUtilities.lerp(CommunitySheet.this.systemInsets.bottom + AndroidUtilities.dp(48.0f), 0, fMax);
            int iLerp2 = CommunitySheet.this.systemInsets.bottom + AndroidUtilities.lerp(AndroidUtilities.dp(72.0f), 0, fMax);
            float fLerp = AndroidUtilities.lerp(0.8f, AndroidUtilities.getNavigationBarThirdButtonsFactor(CommunitySheet.this.systemInsets.bottom), fMax);
            CommunitySheet.this.gradientProtectionDrawableBottom.setInsets(0, 0, 0, iLerp);
            CommunitySheet.this.gradientProtectionDrawableBottom.setBounds(0, getHeight() - iLerp2, getWidth(), getHeight());
            CommunitySheet.this.gradientProtectionDrawableBottom.setColor(Theme.multAlpha(CommunitySheet.this.getThemedColor(i), fLerp));
            CommunitySheet.this.gradientProtectionDrawableBottom.draw(canvas);
        }
    }
}
