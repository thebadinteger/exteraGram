package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.config.BottomNavigationBar;
import com.exteragram.messenger.utils.ui.MainTabsUiHelper;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.TopicsController;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.TopicSearchCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatActivityInterface;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.DialogsActivityTopPanelLayout;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.Forum.ForumBubbleDrawable;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.FragmentFloatingButton;
import org.telegram.ui.Components.InviteMembersBottomSheet;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchDownloadsContainer;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.UnreadCounterTextView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.DownscaleScrollableNoiseSuppressor;
import org.telegram.ui.Components.blur3.ViewGroupPartRenderer;
import org.telegram.ui.Components.blur3.capture.IBlur3Capture;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceColor;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.Delegates.ChatActivityMemberRequestsDelegate;
import org.webrtc.GlShader$$ExternalSyntheticBUOutline1;

public class TopicsFragment extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, ChatActivityInterface, RightSlidingDialogContainer.BaseFragmentWithFullscreen, MainTabsActivity.TabFragmentDelegate {
    private static HashSet<Long> settingsPreloaded = new HashSet<>();
    Adapter adapter;
    private ActionBarMenuSubItem addMemberSubMenu;
    private int additionFloatingButtonOffset;
    private int additionNavigationBarHeight;
    private boolean allowSwipeDuringCurrentTouch;
    boolean animateSearchWithScale;
    boolean animatedUpdateEnabled;
    ChatAvatarContainer avatarContainer;
    private View blurredView;
    private ActionBarMenuSubItem boostGroupSubmenu;
    private TL_stories.TL_premium_boostsStatus boostsStatus;
    private int bottomButtonType;
    private UnreadCounterTextView bottomOverlayChatText;
    private FrameLayout bottomOverlayContainer;
    private RadialProgressView bottomOverlayProgress;
    private boolean bottomPannelVisible;
    boolean canShowCreateTopic;
    private boolean canShowHiddenArchive;
    private boolean canShowProgress;
    TLRPC.ChatFull chatFull;
    final long chatId;
    private ImageView closeReportSpam;
    private ActionBarMenuSubItem closeTopic;
    SizeNotifierFrameLayout contentView;
    private boolean createGroupCall;
    private ActionBarMenuSubItem createTopicSubmenu;
    private ActionBarMenuSubItem deleteChatSubmenu;
    private ActionBarMenuItem deleteItem;
    private int dialogChangeFinished;
    private int dialogInsertFinished;
    private int dialogRemoveFinished;
    DialogsActivity dialogsActivity;
    private boolean disableActionBarScrolling;
    private View emptyView;
    private EmptyViewContainer emptyViewContainer;
    HashSet<Integer> excludeTopics;
    private boolean finishDialogRightSlidingPreviewOnTransitionEnd;
    FragmentFloatingButton floatingButton;
    ArrayList<Item> forumTopics;
    private boolean forumTopicsListFrozen;
    FragmentContextView fragmentContextView;
    private FrameLayout fragmentContextViewWrapper;
    private ArrayList<Item> frozenForumTopicsList;
    FrameLayout fullscreenView;
    private View generalTopicViewMoving;
    private ChatObject.Call groupCall;
    private int hiddenCount;
    private boolean hiddenShown;
    private ActionBarMenuItem hideItem;
    private IBlur3Capture iBlur3Capture;
    private final BlurredBackgroundDrawableViewFactory iBlur3FactoryLiquidGlass;
    private boolean iBlur3Invalidated;
    private final RectF iBlur3PositionActionBar;
    private final RectF iBlur3PositionMainTabs;
    private final ArrayList<RectF> iBlur3Positions;
    private final BlurredBackgroundSourceColor iBlur3SourceColor;
    private final BlurredBackgroundSourceRenderNode iBlur3SourceGlass;
    private final BlurredBackgroundSourceRenderNode iBlur3SourceGlassFrosted;
    private boolean ignoreDiffUtil;
    boolean isSlideBackTransition;
    private DefaultItemAnimator itemAnimator;
    private ItemTouchHelper itemTouchHelper;
    private TouchHelperCallback itemTouchHelperCallback;
    RecyclerItemsEnterAnimator itemsEnterAnimator;
    private boolean joinRequested;
    private boolean lastCallCheckFromServer;
    private int lastItemsCount;
    LinearLayoutManager layoutManager;
    private boolean loadingTopics;
    private boolean mainTabsHiddenByScroll;
    private ArrayList<MessagesController.DialogFilter> movingDialogFilters;
    private boolean mute;
    private ActionBarMenuItem muteItem;
    private int navigationBarHeight;
    private final AnimationNotificationsLocker notificationsLocker;
    OnTopicSelectedListener onTopicSelectedListener;
    private boolean openAnimationEnded;
    private boolean openVideoChat;
    private final boolean openedForBotShare;
    private final boolean openedForForward;
    private final boolean openedForQuote;
    private final boolean openedForReply;
    private final boolean openedForSelect;
    private ActionBarMenuItem other;
    ActionBarMenuItem otherItem;
    public DialogsActivity parentDialogsActivity;
    private ChatActivityMemberRequestsDelegate pendingRequestsDelegate;
    private ActionBarMenuItem pinItem;
    private PullForegroundDrawable pullForegroundDrawable;
    private int pullViewState;
    private ActionBarMenuSubItem readItem;
    private TopicsRecyclerView recyclerListView;
    private boolean removeFragmentOnTransitionEnd;
    private boolean reordering;
    private ActionBarMenuSubItem reportSubmenu;
    private ActionBarMenuSubItem restartTopic;
    private RecyclerAnimationScrollHelper scrollHelper;
    private boolean scrollToTop;
    private final DownscaleScrollableNoiseSuppressor scrollableViewNoiseSuppressor;
    private float searchAnimationProgress;
    ValueAnimator searchAnimator;
    private MessagesSearchContainer searchContainer;
    private ActionBarMenuItem searchItem;
    private ViewPagerFixed.TabsView searchTabsView;
    public boolean searching;
    private NumberTextView selectedDialogsCountTextView;
    private long selectedTopicForTablet;
    HashSet<Integer> selectedTopics;
    private ActionBarMenuItem showItem;
    ValueAnimator slideBackTransitionAnimator;
    float slideFragmentProgress;
    private long startArchivePullingTime;
    ChatActivity.ThemeDelegate themeDelegate;
    private DialogsActivityTopPanelLayout topPanelLayout;
    private final TopicsController topicsController;
    StickerEmptyView topicsEmptyView;
    float transitionPadding;
    private ActionBarMenuItem unpinItem;
    private boolean updateAnimated;
    private String voiceChatHash;
    private boolean waitingForScrollFinished;

    public interface OnTopicSelectedListener {
        void onTopicSelected(TLRPC.TL_forumTopic tL_forumTopic);
    }

    public static void lambda$onItemClick$2(final long j, final ArrayList arrayList, int i) {
            final int size = arrayList.size();
            final int[] iArr = new int[1];
            final TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers = new TLRPC.TL_messages_invitedUsers();
            tL_messages_invitedUsers.updates = new TLRPC.TL_updates();
            for (int i2 = 0; i2 < size; i2++) {
                TopicsFragment.this.getMessagesController().addUserToChat(j, (TLRPC.User) arrayList.get(i2), i, null, TopicsFragment.this, false, new Runnable() { // from class: org.telegram.ui.TopicsFragment$2$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        TopicsFragment.AnonymousClass2.$r8$lambda$1OkCq9MtFhXJWecrxhBTGxZpKV8();
                    }
                }, null, new Utilities.Callback() { // from class: org.telegram.ui.TopicsFragment$2$$ExternalSyntheticLambda5
                    @Override 
                    public final void run(Object obj) {
                        this.f$0.lambda$onItemClick$1(tL_messages_invitedUsers, iArr, size, arrayList, j, (TLRPC.TL_messages_invitedUsers) obj);
                    }
                });
            }
        }

        public void lambda$onClick$0(int i) {
            TopicsFragment topicsFragment = TopicsFragment.this;
            if (i == 0) {
                topicsFragment.updateChatInfo();
            } else {
                topicsFragment.finishFragment();
            }
        }
    }

    public void lambda$joinToGroup$21() {
        this.joinRequested = false;
        updateChatInfo(true);
    }

    public void lambda$updateHidden$0(ValueAnimator valueAnimator) {
            this.hiddenT = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            setHiddenT();
        }

        private void setHiddenT() {
            Drawable drawable = this.forumIcon;
            if (drawable instanceof ForumUtilities.GeneralTopicDrawable) {
                ((ForumUtilities.GeneralTopicDrawable) drawable).setColor(ColorUtils.blendARGB(TopicsFragment.this.getThemedColor(Theme.key_chats_archivePullDownBackground), TopicsFragment.this.getThemedColor(Theme.key_avatar_background2Saved), this.hiddenT));
            }
            Drawable[] drawableArr = this.topicIconInName;
            if (drawableArr != null) {
                Drawable drawable2 = drawableArr[0];
                if (drawable2 instanceof ForumUtilities.GeneralTopicDrawable) {
                    ((ForumUtilities.GeneralTopicDrawable) drawable2).setColor(ColorUtils.blendARGB(TopicsFragment.this.getThemedColor(Theme.key_chats_archivePullDownBackground), TopicsFragment.this.getThemedColor(Theme.key_avatar_background2Saved), this.hiddenT));
                }
            }
            invalidate();
        }

        @Override // org.telegram.ui.Cells.DialogCell
        public boolean drawLock2() {
            return this.closed;
        }
    }

    public void hideFloatingButton(boolean z, boolean z2) {
        this.floatingButton.setButtonVisible(!z, this.fragmentBeginToShow && z2);
        if (BottomNavigationBar.floating()) {
            return;
        }
        checkUi_mainTabsVisible();
    }

    private void updateFloatingButtonOffset() {
        this.floatingButton.setTranslationY(((-this.transitionPadding) - this.navigationBarHeight) - this.additionFloatingButtonOffset);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.closeSearchField();
        }
    }

    public class EmptyViewContainer extends FrameLayout {
        boolean increment;
        float progress;
        TextView textView;

        public EmptyViewContainer(Context context) {
            SpannableStringBuilder spannableStringBuilder;
            super(context);
            this.textView = new TextView(context);
            if (LocaleController.isRTL) {
                spannableStringBuilder = new SpannableStringBuilder("  ");
                spannableStringBuilder.setSpan(new ColoredImageSpan(R.drawable.attach_arrow_left), 0, 1, 0);
                spannableStringBuilder.append((CharSequence) LocaleController.getString(R.string.TapToCreateTopicHint));
            } else {
                spannableStringBuilder = new SpannableStringBuilder(LocaleController.getString(R.string.TapToCreateTopicHint));
                spannableStringBuilder.append((CharSequence) "  ");
                spannableStringBuilder.setSpan(new ColoredImageSpan(R.drawable.arrow_newchat), spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 0);
            }
            this.textView.setText(spannableStringBuilder);
            this.textView.setTextSize(1, 14.0f);
            this.textView.setLayerType(2, null);
            this.textView.setTextColor(TopicsFragment.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
            TextView textView = this.textView;
            boolean z = LocaleController.isRTL;
            addView(textView, LayoutHelper.createFrame(-2, -2.0f, 81, z ? 72.0f : 32.0f, 0.0f, z ? 32.0f : 72.0f, 32.0f));
        }

        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
            boolean z = this.increment;
            float f = this.progress;
            if (z) {
                float f2 = f + 0.013333334f;
                this.progress = f2;
                if (f2 > 1.0f) {
                    this.increment = false;
                    this.progress = 1.0f;
                }
            } else {
                float f3 = f - 0.013333334f;
                this.progress = f3;
                if (f3 < 0.0f) {
                    this.increment = true;
                    this.progress = 0.0f;
                }
            }
            this.textView.setTranslationX(CubicBezierInterpolator.DEFAULT.getInterpolation(this.progress) * AndroidUtilities.dp(8.0f) * (LocaleController.isRTL ? -1 : 1));
            invalidate();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        int themedColor = getThemedColor(this.searching ? Theme.key_windowBackgroundWhite : Theme.key_actionBarDefault);
        if (this.actionBar.isActionModeShowed()) {
            themedColor = getThemedColor(Theme.key_actionBarActionModeDefault);
        }
        return ColorUtils.calculateLuminance(themedColor) > 0.699999988079071d;
    }

    public class MessagesSearchContainer extends ViewPagerFixed implements FilteredSearchView.UiCallback {
        boolean canLoadMore;
        SearchViewPager.ChatPreviewDelegate chatPreviewDelegate;
        StickerEmptyView emptyView;
        FlickerLoadingView flickerLoadingView;
        boolean isLoading;
        RecyclerItemsEnterAnimator itemsEnterAnimator;
        private int keyboardSize;
        LinearLayoutManager layoutManager;
        int messagesEndRow;
        int messagesHeaderRow;
        boolean messagesIsLoading;
        int messagesStartRow;
        RecyclerListView recyclerView;
        int rowCount;
        SearchAdapter searchAdapter;
        FrameLayout searchContainer;
        ArrayList<MessageObject> searchResultMessages;
        ArrayList<TLRPC.TL_forumTopic> searchResultTopics;
        Runnable searchRunnable;
        String searchString;
        private ArrayList<MessageObject> selectedItems;
        int topicsEndRow;
        int topicsHeaderRow;
        int topicsStartRow;
        private ViewPagerAdapter viewPagerAdapter;

        public MessagesSearchContainer(Context context) {
            super(context);
            this.searchString = "empty";
            this.searchResultTopics = new ArrayList<>();
            this.searchResultMessages = new ArrayList<>();
            this.selectedItems = new ArrayList<>();
            this.searchContainer = new FrameLayout(context);
            this.chatPreviewDelegate = new SearchViewPager.ChatPreviewDelegate() { 
                @Override // org.telegram.ui.Components.SearchViewPager.ChatPreviewDelegate
                public void startChatPreview(RecyclerListView recyclerListView, DialogCell dialogCell) {
                    TopicsFragment.this.showChatPreview(dialogCell);
                }

                @Override // org.telegram.ui.Components.SearchViewPager.ChatPreviewDelegate
                public void move(float f) {
                    Point point = AndroidUtilities.displaySize;
                    if (point.x > point.y) {
                        TopicsFragment.this.movePreviewFragment(f);
                    }
                }

                @Override // org.telegram.ui.Components.SearchViewPager.ChatPreviewDelegate
                public void finish() {
                    Point point = AndroidUtilities.displaySize;
                    if (point.x > point.y) {
                        TopicsFragment.this.finishPreviewFragment();
                    }
                }
            };
            RecyclerListView recyclerListView = new RecyclerListView(context);
            this.recyclerView = recyclerListView;
            SearchAdapter searchAdapter = new SearchAdapter();
            this.searchAdapter = searchAdapter;
            recyclerListView.setAdapter(searchAdapter);
            RecyclerListView recyclerListView2 = this.recyclerView;
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            this.layoutManager = linearLayoutManager;
            recyclerListView2.setLayoutManager(linearLayoutManager);
            this.recyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.TopicsFragment$MessagesSearchContainer$$ExternalSyntheticLambda0
                @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
                public final void onItemClick(View view, int i) {
                    this.f$0.lambda$new$0(view, i);
                }
            });
            this.recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() { 
                @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
                public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                    super.onScrolled(recyclerView, i, i2);
                    MessagesSearchContainer messagesSearchContainer = MessagesSearchContainer.this;
                    if (messagesSearchContainer.canLoadMore) {
                        int iFindLastVisibleItemPosition = messagesSearchContainer.layoutManager.findLastVisibleItemPosition() + 5;
                        MessagesSearchContainer messagesSearchContainer2 = MessagesSearchContainer.this;
                        if (iFindLastVisibleItemPosition >= messagesSearchContainer2.rowCount) {
                            messagesSearchContainer2.loadMessages(messagesSearchContainer2.searchString);
                        }
                    }
                    TopicsFragment topicsFragment = TopicsFragment.this;
                    if (topicsFragment.searching) {
                        if (i == 0 && i2 == 0) {
                            return;
                        }
                        AndroidUtilities.hideKeyboard(topicsFragment.searchItem.getSearchField());
                    }
                }
            });
            FlickerLoadingView flickerLoadingView = new FlickerLoadingView(context);
            this.flickerLoadingView = flickerLoadingView;
            flickerLoadingView.setViewType(7);
            this.flickerLoadingView.showDate(false);
            this.flickerLoadingView.setUseHeaderOffset(true);
            StickerEmptyView stickerEmptyView = new StickerEmptyView(context, this.flickerLoadingView, 1);
            this.emptyView = stickerEmptyView;
            stickerEmptyView.title.setText(LocaleController.getString(R.string.NoResult));
            this.emptyView.subtitle.setVisibility(8);
            this.emptyView.setVisibility(8);
            this.emptyView.addView(this.flickerLoadingView, 0);
            this.emptyView.setAnimateLayoutChange(true);
            this.recyclerView.setEmptyView(this.emptyView);
            this.recyclerView.setAnimateEmptyView(true, 0);
            this.searchContainer.addView(this.emptyView);
            this.searchContainer.addView(this.recyclerView);
            updateRows();
            RecyclerItemsEnterAnimator recyclerItemsEnterAnimator = new RecyclerItemsEnterAnimator(this.recyclerView, true);
            this.itemsEnterAnimator = recyclerItemsEnterAnimator;
            this.recyclerView.setItemsEnterAnimator(recyclerItemsEnterAnimator);
            ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter();
            this.viewPagerAdapter = viewPagerAdapter;
            setAdapter(viewPagerAdapter);
        }

        public void lambda$searchMessages$1(String str) {
            String lowerCase = str.trim().toLowerCase();
            ArrayList arrayList = new ArrayList();
            for (int i = 0; i < TopicsFragment.this.forumTopics.size(); i++) {
                if (TopicsFragment.this.forumTopics.get(i).topic != null && TopicsFragment.this.forumTopics.get(i).topic.title.toLowerCase().contains(lowerCase)) {
                    arrayList.add(TopicsFragment.this.forumTopics.get(i).topic);
                    TopicsFragment.this.forumTopics.get(i).topic.searchQuery = lowerCase;
                }
            }
            this.searchResultTopics.clear();
            this.searchResultTopics.addAll(arrayList);
            updateRows();
            if (!this.searchResultTopics.isEmpty()) {
                this.isLoading = false;
                this.itemsEnterAnimator.showItemsAnimated(0);
            }
            loadMessages(str);
        }

        public void setSearchString(String str) {
            if (this.searchString.equals(str)) {
                return;
            }
            search(this.viewPages[0], getCurrentPosition(), str, false);
        }

        public void loadMessages(final String str) {
            if (this.messagesIsLoading) {
                return;
            }
            TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
            tL_messages_search.peer = TopicsFragment.this.getMessagesController().getInputPeer(-TopicsFragment.this.chatId);
            tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterEmpty();
            tL_messages_search.limit = 20;
            tL_messages_search.q = str;
            if (!this.searchResultMessages.isEmpty()) {
                ArrayList<MessageObject> arrayList = this.searchResultMessages;
                tL_messages_search.offset_id = arrayList.get(arrayList.size() - 1).getId();
            }
            this.messagesIsLoading = true;
            ConnectionsManager.getInstance(((BaseFragment) TopicsFragment.this).currentAccount).sendRequest(tL_messages_search, new RequestDelegate() { // from class: org.telegram.ui.TopicsFragment$MessagesSearchContainer$$ExternalSyntheticLambda2
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$loadMessages$3(str, tLObject, tL_error);
                }
            });
        }

        public void lambda$getThemeDescriptions$24() {
        ViewGroup viewGroup;
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                viewGroup = this.recyclerListView;
            } else {
                MessagesSearchContainer messagesSearchContainer = this.searchContainer;
                viewGroup = messagesSearchContainer != null ? messagesSearchContainer.recyclerView : null;
            }
            if (viewGroup != null) {
                int childCount = viewGroup.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = viewGroup.getChildAt(i2);
                    if (childAt instanceof ProfileSearchCell) {
                        ((ProfileSearchCell) childAt).update(0);
                    } else if (childAt instanceof DialogCell) {
                        ((DialogCell) childAt).update(0);
                    } else if (childAt instanceof UserCell) {
                        ((UserCell) childAt).update(0);
                    }
                }
            }
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.setPopupBackgroundColor(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground), true);
            this.actionBar.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem), false, true);
            this.actionBar.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon), true, true);
            this.actionBar.setPopupItemsSelectorColor(getThemedColor(Theme.key_dialogButtonSelector), true);
        }
        View view = this.blurredView;
        if (view != null) {
            view.setForeground(new ColorDrawable(ColorUtils.setAlphaComponent(getThemedColor(Theme.key_windowBackgroundWhite), 100)));
        }
        updateColors();
    }

    public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
        int i = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
        this.navigationBarHeight = i;
        MessagesSearchContainer messagesSearchContainer = this.searchContainer;
        if (messagesSearchContainer != null) {
            messagesSearchContainer.setPadding(0, 0, 0, i);
        }
        EmptyViewContainer emptyViewContainer = this.emptyViewContainer;
        if (emptyViewContainer != null) {
            emptyViewContainer.textView.setTranslationY((-this.navigationBarHeight) - this.additionFloatingButtonOffset);
        }
        updateFloatingButtonOffset();
        checkUi_listViewPadding();
        return WindowInsetsCompat.CONSUMED;
    }

    public void blur3_InvalidateBlur() {
        if (Build.VERSION.SDK_INT < 31 || this.scrollableViewNoiseSuppressor == null) {
            return;
        }
        int iDp = AndroidUtilities.dp(48.0f);
        int iDp2 = AndroidUtilities.dp(48.0f) + ((int) this.topPanelLayout.getAnimatedHeightWithPadding(AndroidUtilities.dp(14.0f)));
        DialogsActivity dialogsActivity = this.parentDialogsActivity;
        View view = dialogsActivity != null ? dialogsActivity.fragmentView : this.fragmentView;
        this.iBlur3PositionActionBar.set(0.0f, -iDp, view.getMeasuredWidth(), (dialogsActivity != null ? dialogsActivity.getActionBar() : this.actionBar).getMeasuredHeight() + iDp + iDp2);
        MainTabsUiHelper.setBlurBounds(this.iBlur3PositionMainTabs, view, this.navigationBarHeight);
        this.iBlur3PositionMainTabs.inset(0.0f, LiteMode.isEnabled(262144) ? 0.0f : -AndroidUtilities.dp(48.0f));
        this.scrollableViewNoiseSuppressor.setupRenderNodes(this.iBlur3Positions, this.parentDialogsActivity != null ? 2 : 1);
        this.scrollableViewNoiseSuppressor.invalidateResultRenderNodes(this.iBlur3Capture, view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    @Override // org.telegram.ui.MainTabsActivity.TabFragmentDelegate
    public BlurredBackgroundSourceRenderNode getGlassSource() {
        return this.iBlur3SourceGlass;
    }

    public BlurredBackgroundSourceRenderNode getFrostedGlassSource() {
        return this.iBlur3SourceGlassFrosted;
    }

    @Override // org.telegram.ui.MainTabsActivity.TabFragmentDelegate
    public void onParentScrollToTop() {
        this.recyclerListView.smoothScrollToPosition(0);
    }
}
