package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.LongSparseArray;
import android.util.Property;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.AvatarCornerType;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.TabIconsMode;
import com.exteragram.messenger.components.TranslateBeforeSendWrapper;
import com.exteragram.messenger.config.BottomNavigationBar;
import com.exteragram.messenger.drawer.DrawerContainer;
import com.exteragram.messenger.utils.AppUtils;
import com.exteragram.messenger.utils.chats.MainMenuHelper;
import com.exteragram.messenger.utils.system.VibratorUtils;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import com.exteragram.messenger.utils.ui.MainTabsUiHelper;
import com.google.android.gms.cast.MediaError;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import me.vkryl.android.util.ClickHelper;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FilesMigrationService;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.NotificationsSettingsFacade;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.utils.FBool;
import org.telegram.messenger.utils.GradientProtectionDrawable;
import org.telegram.messenger.utils.SearchTextWatcher;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_chatlists;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.MenuDrawable;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Adapters.DialogsAdapter;
import org.telegram.ui.Adapters.DialogsSearchAdapter;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Cells.ActiveGiftAuctionsHintCell;
import org.telegram.ui.Cells.AnimatedStatusView;
import org.telegram.ui.Cells.ArchiveHintInnerCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.DialogsEmptyCell;
import org.telegram.ui.Cells.DialogsHintCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HashtagSearchCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.HintDialogCell;
import org.telegram.ui.Cells.LoadingCell;
import org.telegram.ui.Cells.ProfileSearchCell;
import org.telegram.ui.Cells.RequestPeerRequirementsCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UnconfirmedAuthHintCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.ArchiveHelp;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlurredRecyclerView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.DialogsActivityStatusLayout;
import org.telegram.ui.Components.DialogsActivityTopBubblesFadeView;
import org.telegram.ui.Components.DialogsActivityTopPanelLayout;
import org.telegram.ui.Components.DialogsItemAnimator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FilterTabsView;
import org.telegram.ui.Components.FiltersListBottomSheet;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugProvider;
import org.telegram.ui.Components.FolderBottomSheet;
import org.telegram.ui.Components.FolderDrawable;
import org.telegram.ui.Components.ForegroundColorSpanThemable;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.FragmentFloatingButton;
import org.telegram.ui.Components.FragmentSearchField;
import org.telegram.ui.Components.IconBackgroundColors;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MediaActivity;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.PacmanAnimation;
import org.telegram.ui.Components.PermissionRequest;
import org.telegram.ui.Components.PopupSwipeBackLayout;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.boosts.UserSelectorBottomSheet;
import org.telegram.ui.Components.ProxyDrawable;
import org.telegram.ui.Components.PullForegroundDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerItemsEnterAnimator;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SearchViewPager;
import org.telegram.ui.Components.ShareTopView;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.BlurredBackgroundWithFadeDrawable;
import org.telegram.ui.Components.blur3.DownscaleScrollableNoiseSuppressor;
import org.telegram.ui.Components.blur3.RenderNodeWithHash;
import org.telegram.ui.Components.blur3.capture.IBlur3Capture;
import org.telegram.ui.Components.blur3.capture.IBlur3Hash;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceColor;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode;
import org.telegram.ui.Components.blur3.utils.Blur3Utils;
import org.telegram.ui.Components.chat.ChatInputViewsContainer;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Components.chat.layouts.ChatActivityFadeView;
import org.telegram.ui.Components.inset.WindowInsetsStateHolder;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.Stars.StarGiftSheet;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stories.DialogStoriesCell;
import org.telegram.ui.Stories.StealthModeAlert;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesListPlaceProvider;
import org.telegram.ui.Stories.UserListPoller;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.bots.BotWebViewSheet;
import org.telegram.ui.community.CommunityChatType;
import org.telegram.ui.community.CommunityEditActivity;
import org.telegram.ui.community.CommunityPendingRequestsActivity;
import org.telegram.ui.community.CommunityUtils;
import org.telegram.ui.community.cells.CommunityRequestsCell;

public class DialogsActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, FloatingDebugProvider, FactorAnimator.Target, MainTabsActivity.TabFragmentDelegate {
    public static boolean switchingTheme;
    private final String ACTION_MODE_SEARCH_DIALOGS_TAG;
    private final int ADDITIONAL_LIST_HEIGHT_DP;
    public final Property<DialogsActivity, Float> SCROLL_Y;
    public final Property<View, Float> SEARCH_TRANSLATION_Y;
    private ValueAnimator actionBarColorAnimator;
    private final Paint actionBarDefaultPaint;
    private String actionBarDefaultTitle;
    private int actionModeAdditionalHeight;
    private ImageView actionModeCloseView;
    private boolean actionModeFullyShowed;
    private final ArrayList<View> actionModeViews;
    private ActiveGiftAuctionsHintCell activeGiftAuctionsHintCell;
    private ButtonWithCounterView addChatsToCommunityButton;
    private ActionBarMenuSubItem addToFolderItem;
    private String addToGroupAlertString;
    private int additionFloatingButtonOffset;
    private int additionNavigationBarHeight;
    private float additionalFloatingTranslation;
    private float additionalOffset;
    private boolean afterSignup;
    public boolean allowBots;
    public boolean allowChannels;
    private boolean allowGlobalSearch;
    public boolean allowGroups;
    public boolean allowLegacyGroups;
    public boolean allowMegagroups;
    private boolean allowMoving;
    private boolean allowSwipeDuringCurrentTouch;
    private boolean allowSwitchAccount;
    public boolean allowUsers;
    private boolean animateToHasStories;
    private AnimatedStatusView animatedStatusView;
    private boolean animatingForward;
    private final BoolAnimator animatorActionModeVisible;
    private final BoolAnimator animatorBottomTabsOffset;
    private final BoolAnimator animatorDoneButtonVisible;
    private final BoolAnimator animatorFilterTabsVisible;
    private final BoolAnimator animatorForwardButtonVisible;
    private final BoolAnimator animatorSearchButtonVisible;
    private final BoolAnimator animatorSearchFilterTabsVisible;
    private final BoolAnimator animatorSearchVisible;
    private final BoolAnimator animatorShadowVisible;
    private final BoolAnimator animatorSpeedButtonVisible;
    private ActionBarMenuItem archive2Item;
    private ActionBarMenuSubItem archiveItem;
    private boolean askAboutContacts;
    private boolean askingForPermissions;
    private UnconfirmedAuthHintCell authHintCell;
    private TLRPC.FileLocation avatar;
    private TLRPC.FileLocation avatarBig;
    private ChatAvatarContainer avatarContainer;
    private int avatarUploadingRequest;
    private boolean backAnimation;
    private BackDrawable backDrawable;
    private ActionBarMenuSubItem blockItem;
    private View blurredView;
    private ArrayList<TLRPC.Dialog> botShareDialogs;
    private Long cacheSize;
    private int canClearCacheCount;
    private boolean canDeletePsaSelected;
    private int canMuteCount;
    private int canPinCount;
    private int canReadCount;
    private int canReportSpamCount;
    private boolean canSelectTopics;
    private boolean canShowFilterTabsView;
    private boolean canShowHiddenArchive;
    private boolean canShowStoryHint;
    private int canUnarchiveCount;
    private int canUnmuteCount;
    private boolean cantSendToChannels;
    private FrameLayout chatInputBubbleContainer;
    private FrameLayout chatInputInAppContainer;
    private ChatInputViewsContainer chatInputViewsContainer;
    private boolean checkCanWrite;
    private boolean checkPermission;
    private boolean checkingImportDialog;
    private ActionBarMenuSubItem clearItem;
    private boolean closeFragment;
    private boolean closeSearchFieldOnHide;
    private ChatActivityEnterView commentView;
    private TLRPC.Chat community;
    private AvatarDrawable communityAvatarDrawable;
    private BackupImageView communityAvatarImage;
    private ChatActivityFadeView communityBottomFadeView;
    private TLRPC.ChatFull communityFull;
    private long communityId;
    private CommunityRequestsCell communityPendingRequests;
    private float contactsAlpha;
    private ValueAnimator contactsAlphaAnimator;
    private int currentConnectionState;
    View databaseMigrationHint;
    private int debugLastUpdateAction;
    private DialogsActivityDelegate delegate;
    private ActionBarMenuItem deleteItem;
    private Long deviceSize;
    public DialogStoriesCell dialogStoriesCell;
    public boolean dialogStoriesCellVisible;
    private DialogsActivityStatusLayout dialogsActivityStatusLayout;
    private DialogsHintCell dialogsHintCell;
    private boolean dialogsListFrozen;
    private boolean disableActionBarScrolling;
    private ActionBarMenuItem doneItem;
    private AnimatorSet doneItemAnimator;
    private DownloadProgressIcon downloadProgressIcon;
    private ActionBarMenuItem downloadsItem;
    private boolean downloadsItemVisible;
    private ItemOptions filterOptions;
    private FilterTabsView filterTabsView;
    private FiltersView filtersView;
    private boolean fixScrollYAfterArchiveOpened;
    private FragmentFloatingButton floatingButton3;
    boolean floatingButtonHidden;
    private float floatingButtonPanOffset;
    private FragmentFloatingButton floatingButtonStories;
    private boolean floatingForceVisible;
    private int folderId;
    private int forumCount;
    public long forwardOriginalChannel;
    private FragmentContextView fragmentContextView;
    private FrameLayout fragmentContextViewWrapper;
    private FragmentContextView fragmentLocationContextView;
    private FrameLayout fragmentLocationContextViewWrapper;
    private FragmentSearchField fragmentSearchField;
    private SearchTextWatcher fragmentSearchFieldWatcher;
    private ArrayList<TLRPC.Dialog> frozenDialogsList;
    private NotificationCenter.ObserversGroup globalObserversGroup;
    private boolean hasInvoice;
    public boolean hasMainTabs;
    public boolean hasOnlySlefStories;
    private int hasPoll;
    public boolean hasStories;
    private IBlur3Capture iBlur3Capture;
    private final BlurredBackgroundDrawableViewFactory iBlur3FactoryBlur;
    private final BlurredBackgroundDrawableViewFactory iBlur3FactoryFade;
    private final BlurredBackgroundDrawableViewFactory iBlur3FactoryFrostedLiquidGlass;
    private final BlurredBackgroundDrawableViewFactory iBlur3FactoryLiquidGlass;
    private final RectF iBlur3PositionActionBar;
    private final RectF iBlur3PositionMainTabs;
    private final ArrayList<RectF> iBlur3Positions;
    private final BlurredBackgroundSourceColor iBlur3SourceColor;
    private final BlurredBackgroundSourceRenderNode iBlur3SourceGlass;
    private final BlurredBackgroundSourceRenderNode iBlur3SourceGlassFrosted;
    private ImageUpdater imageUpdater;
    private int imeInsetHeight;
    private int initialDialogsType;
    private String initialSearchString;
    private int initialSearchType;
    private boolean invalidateScrollY;
    private boolean isNextButton;
    public boolean isQuote;
    public boolean isReplyTo;
    boolean isSlideBackTransition;
    private Drawable logoDrawable;
    private MainTabsActivityController mainTabsActivityController;
    boolean mainTabsHiddenByScroll;
    private int maximumVelocity;
    private boolean maybeStartTracking;
    private MenuDrawable menuDrawable;
    private int messagesCount;
    private final ArrayList<MessagesController.DialogFilter> movingDialogFilters;
    private DialogCell movingView;
    private boolean movingWas;
    private ActionBarMenuItem muteItem;
    private int navigationBarHeight;
    private AnimationNotificationsLocker notificationsLocker;
    public boolean notify;
    private NotificationCenter.ObserversGroup observersGroup;
    private boolean onlySelect;
    private MessagesStorage.TopicKey openedDialogId;
    private ActionBarMenuItem optionsItem;
    private int otherwiseReloginDays;
    private PacmanAnimation pacmanAnimation;
    private final Paint paint;
    float panTranslationY;
    private DialogsActivity parentForwardDialogFragment;
    private ActionBarMenuItem passcodeItem;
    private CharSequence pendingSharedCaption;
    private AlertDialog permissionDialog;
    private ActionBarMenuSubItem pin2Item;
    private ActionBarMenuItem pinItem;
    private Drawable premiumStar;
    private int prevPosition;
    private int prevTop;
    private float progressToActionMode;
    public float progressToDialogStoriesCell;
    public float progressToShowStories;
    private ProxyDrawable proxyDrawable;
    private ActionBarMenuSubItem proxyMenuSubItem;
    private ActionBarMenuSubItem readItem;
    private final RectF rect;
    private ActionBarMenuSubItem removeFromFolderItem;
    public long replyMessageAuthor;
    private long requestPeerBotId;
    private TLRPC.RequestPeerType requestPeerType;
    public boolean resetDelegate;
    private boolean rightFragmentTransitionInProgress;
    private boolean rightFragmentTransitionIsOpen;
    public RightSlidingDialogContainer rightSlidingDialogContainer;
    public int scheduleDate;
    public int scheduleRepeatPeriod;
    private float scrollAdditionalOffset;
    private boolean scrollBarVisible;
    private boolean scrollUpdated;
    private float scrollYOffset;
    private final DownscaleScrollableNoiseSuppressor scrollableViewNoiseSuppressor;
    private boolean scrollingManually;
    private float searchAnimationProgress;
    private AnimatorSet searchAnimator;
    private long searchDialogId;
    private boolean searchFiltersWasShowed;
    private boolean searchIsShowed;
    public ActionBarMenuItem searchItem;
    private TLObject searchObject;
    private String searchString;
    private SearchTabsAndFiltersLayout searchTabsAndFiltersLayout;
    private ViewPagerFixed.TabsView searchTabsView;
    private SearchViewPager searchViewPager;
    private int searchViewPagerIndex;
    float searchViewPagerTranslationY;
    private boolean searchWas;
    private boolean searchWasFullyShowed;
    private boolean searching;
    private String selectAlertString;
    private String selectAlertStringGroup;
    private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;
    private ArrayList<Long> selectedDialogs;
    private NumberTextView selectedDialogsCountTextView;
    private boolean shareHintStarted;
    private Runnable shareLinkSearchRunnable;
    private ShareTopView shareTopView;
    private String sharedLink;
    private ArrayList<MediaController.PhotoEntry> sharedMediaEntries;
    private SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;
    private CharSequence sharedTextSeed;
    private int shiftDp;
    private boolean showSetPasswordConfirm;
    private String showingSuggestion;
    final int slideAmplitudeDp;
    ValueAnimator slideBackTransitionAnimator;
    boolean slideFragmentLite;
    float slideFragmentProgress;
    private DialogCell slidingView;
    private boolean slowedReloadAfterDialogClick;
    private ActionBarMenuItem speedItem;
    private long startArchivePullingTime;
    private boolean startedTracking;
    private int statusBarHeight;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable statusDrawable;
    private Long statusDrawableGiftId;
    private Bulletin storiesBulletin;
    public boolean storiesEnabled;
    private float storiesOverscroll;
    private boolean storiesOverscrollCalled;
    ValueAnimator storiesVisibilityAnimator;
    ValueAnimator storiesVisibilityAnimator2;
    private float storiesYOffset;
    private HintView2 storyHint;
    private boolean storyHintShown;
    private HintView2 storyPremiumHint;
    private ActionBarMenuItem switchItem;
    private AnimatorSet tabsAnimation;
    private boolean tabsAnimationInProgress;
    private float tabsYOffset;
    private final TextPaint textPaint;
    private DialogsActivityTopBubblesFadeView topBubblesFadeView;
    private Bulletin topBulletin;
    private DialogsActivityTopPanelLayout topPanelLayout;
    private UndoView[] undoView;
    private int undoViewIndex;
    private boolean updatePullAfterScroll;
    private Bulletin uploadingAvatarBulletin;
    private ViewPage[] viewPages;
    private ViewPositionWatcher viewPositionWatcher;
    private boolean waitingForScrollFinished;
    private boolean wasDrawn;
    private boolean wasSelectedDialogsEmpty;
    public boolean whiteActionBar;
    private final WindowInsetsStateHolder windowInsetsStateHolder;
    private ChatActivityEnterView.SendButton writeButton;
    public static boolean[] dialogsLoaded = new boolean[16];
    private static final Interpolator interpolator = new Interpolator() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda76
        @Override // android.animation.TimeInterpolator
        public final float getInterpolation(float f) {
            return DialogsActivity.$r8$lambda$1MKEGXpjH9HrtwN1ZClzWIcweWE(f);
        }
    };
    public static float viewOffset = 0.0f;
    private static boolean isFirstLoading = false;

    public interface DialogsActivityDelegate {
        default boolean canSelectStories() {
            return false;
        }

        boolean didSelectDialogs(DialogsActivity dialogsActivity, ArrayList<MessagesStorage.TopicKey> arrayList, CharSequence charSequence, boolean z, boolean z2, int i, int i2, TopicsFragment topicsFragment);

        default boolean didSelectStories(DialogsActivity dialogsActivity) {
            return false;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean drawEdgeNavigationBar() {
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    public boolean shouldShowNextButton(DialogsActivity dialogsActivity, ArrayList<Long> arrayList, CharSequence charSequence, boolean z) {
        return false;
    }

    public MessagesStorage.TopicKey getOpenedDialogId() {
        return this.openedDialogId;
    }

    public class ViewPage extends FrameLayout {
        public boolean animateStoriesView;
        private DialogsAdapter animationSupportDialogsAdapter;
        private RecyclerListView animationSupportListView;
        private int archivePullViewState;
        private DialogsAdapter dialogsAdapter;
        private DialogsItemAnimator dialogsItemAnimator;
        private int dialogsType;
        private boolean isLocked;
        private ItemTouchHelper itemTouchhelper;
        private int lastItemsCount;
        private LinearLayoutManager layoutManager;
        public DialogsRecyclerView listView;
        public int pageAdditionalOffset;
        private FlickerLoadingView progressView;
        private PullForegroundDrawable pullForegroundDrawable;
        private RecyclerItemsEnterAnimator recyclerItemsEnterAnimator;
        Runnable saveScrollPositionRunnable;
        private RecyclerAnimationScrollHelper scrollHelper;
        public RecyclerListViewScroller scroller;
        private int selectedType;
        private SwipeController swipeController;
        Runnable updateListRunnable;
        boolean updating;

        public ViewPage(Context context) {
            super(context);
            this.saveScrollPositionRunnable = new Runnable() { // from class: org.telegram.ui.DialogsActivity$ViewPage$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$new$0();
                }
            };
            this.updateListRunnable = new Runnable() { // from class: org.telegram.ui.DialogsActivity$ViewPage$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$new$1();
                }
            };
        }

        public boolean isDefaultDialogType() {
            int i = this.dialogsType;
            return i == 0 || i == 7 || i == 8;
        }

        public void lambda$updateStatus$2(View view) {
        DialogStoriesCell dialogStoriesCell;
        if (this.dialogStoriesCellVisible && (dialogStoriesCell = this.dialogStoriesCell) != null && !dialogStoriesCell.isExpanded()) {
            scrollToTop(true, true);
        } else {
            showSelectStatusDialog();
        }
    }

    public void lambda$createView$7(View view) {
        showSearch(true, false, true);
        this.fragmentSearchFieldWatcher.toggleSearch(true);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda137
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$createView$6();
            }
        }, 100L);
    }

    public void lambda$updateDialogsHint$35(View view) {
        AccountFrozenAlert.show(getContext(), this.currentAccount, getResourceProvider());
    }

    public void lambda$updateDialogsHint$49(TL_account.TL_birthday tL_birthday) {
        TL_account.updateBirthday updatebirthday = new TL_account.updateBirthday();
        updatebirthday.flags |= 1;
        updatebirthday.birthday = tL_birthday;
        final TLRPC.UserFull userFull = getMessagesController().getUserFull(getUserConfig().getClientUserId());
        final TL_account.TL_birthday tL_birthday2 = userFull != null ? userFull.birthday : null;
        if (userFull != null) {
            userFull.flags2 |= 32;
            userFull.birthday = tL_birthday;
        }
        getMessagesController().invalidateContentSettings();
        getConnectionsManager().sendRequest(updatebirthday, new RequestDelegate() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda140
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$updateDialogsHint$48(userFull, tL_birthday2, tLObject, tL_error);
            }
        }, 1024);
        MessagesController.getInstance(this.currentAccount).removeSuggestion(0L, "BIRTHDAY_SETUP");
        updateDialogsHint();
    }

    public void lambda$createGroupForThis$76(final ChannelCreateActivity channelCreateActivity, final AlertDialog alertDialog, final BaseFragment baseFragment, final Long l) {
        Utilities.doCallbacks(new Utilities.Callback() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda161
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$createGroupForThis$68(l, channelCreateActivity, baseFragment, (Runnable) obj);
            }
        }, new Utilities.Callback() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda162
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$createGroupForThis$70(alertDialog, l, (Runnable) obj);
            }
        }, new Utilities.Callback() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda163
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$createGroupForThis$72(l, (Runnable) obj);
            }
        }, new Utilities.Callback() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda164
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$createGroupForThis$74(l, (Runnable) obj);
            }
        }, new Utilities.Callback() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda165
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$createGroupForThis$75(alertDialog, l, channelCreateActivity, baseFragment, (Runnable) obj);
            }
        });
    }

    public boolean $r8$lambda$vD6T_cw5rdBb3YjShlHoRxZA0yo(Runnable runnable, TLRPC.TL_error tL_error) {
        runnable.run();
        return true;
    }

    public boolean $r8$lambda$Kcxx0VcADvxHqrqIKldlovS9Zsc(Runnable runnable, TLRPC.TL_error tL_error) {
        runnable.run();
        return true;
    }

    public boolean m14499$r8$lambda$ffslDi3c2P20GY_W_Ju4ZWIru4(Runnable runnable, TLRPC.TL_error tL_error) {
        runnable.run();
        return true;
    }

    public void lambda$didFinishChatCreation$1(long j, BaseFragment[] baseFragmentArr, final Runnable runnable) {
            if (DialogsActivity.this.requestPeerType.has_username != null && DialogsActivity.this.requestPeerType.has_username.booleanValue()) {
                Bundle bundle = new Bundle();
                bundle.putInt("step", 1);
                bundle.putLong("chat_id", j);
                bundle.putBoolean("forcePublic", DialogsActivity.this.requestPeerType.has_username.booleanValue());
                ChannelCreateActivity channelCreateActivity = new ChannelCreateActivity(bundle);
                channelCreateActivity.setOnFinishListener(new Utilities.Callback2() { // from class: org.telegram.ui.DialogsActivity$30$$ExternalSyntheticLambda7
                    @Override 
                    public final void run(Object obj, Object obj2) {
                        runnable.run();
                    }
                });
                DialogsActivity.this.presentFragment(channelCreateActivity);
                baseFragmentArr[1] = channelCreateActivity;
                return;
            }
            runnable.run();
        }

        public boolean m14741$r8$lambda$1l54IXmh8FSouQnx6WDFgWB0A(Runnable runnable, TLRPC.TL_error tL_error) {
            runnable.run();
            return true;
        }

        public boolean m14742$r8$lambda$6QAWGFYN_hifaceXl3AGN9LOoE(Runnable runnable, TLRPC.TL_error tL_error) {
            runnable.run();
            return true;
        }

        public boolean m14744$r8$lambda$BG3nU2Rg4XvSp527a8VlwJE8ZY(Runnable runnable, TLRPC.TL_error tL_error) {
            runnable.run();
            return true;
        }

        public void lambda$onResume$82(final Activity activity, Boolean bool) {
        if (bool.booleanValue()) {
            return;
        }
        showDialog(new NotificationPermissionDialog(activity, !PermissionRequest.canAskPermission("android.permission.POST_NOTIFICATIONS"), new Utilities.Callback() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda152
            @Override 
            public final void run(Object obj) {
                DialogsActivity.$r8$lambda$5xHUDQ7o9prrIpDSGi_ozR0wNIU(activity, (Boolean) obj);
            }
        }));
    }

    public static void lambda$showSearch$92(ValueAnimator valueAnimator) {
        setSearchAnimationProgress(((Float) valueAnimator.getAnimatedValue()).floatValue(), false);
    }

    public boolean onlyDialogsAdapter() {
        int totalDialogsCount = getMessagesController().getTotalDialogsCount();
        if (this.onlySelect) {
            return true;
        }
        return totalDialogsCount <= 10 && !this.hasStories;
    }

    private void updateFilterTabsVisibility(boolean z) {
        if (this.fragmentView == null) {
            return;
        }
        if (this.isPaused || this.databaseMigrationHint != null) {
            z = false;
        }
        if (this.searchIsShowed) {
            return;
        }
        this.animatorFilterTabsVisible.setValue(this.canShowFilterTabsView, z);
    }

    public void setSearchAnimationProgress(float f, boolean z) {
        this.searchAnimationProgress = f;
        boolean z2 = true;
        if (this.whiteActionBar && this.actionBar != null) {
            int themedColor = getThemedColor((this.folderId == 0 && this.communityId == 0) ? Theme.key_actionBarDefaultIcon : Theme.key_actionBarDefaultArchivedIcon);
            ActionBar actionBar = this.actionBar;
            int i = Theme.key_actionBarActionModeDefaultIcon;
            actionBar.setItemsColor(ColorUtils.blendARGB(themedColor, getThemedColor(i), this.searchAnimationProgress), false);
            this.actionBar.setItemsColor(ColorUtils.blendARGB(getThemedColor(i), getThemedColor(i), this.searchAnimationProgress), true);
            this.actionBar.setItemsBackgroundColor(ColorUtils.blendARGB(getThemedColor((this.folderId == 0 && this.communityId == 0) ? Theme.key_actionBarDefaultSelector : Theme.key_actionBarDefaultArchivedSelector), getThemedColor(Theme.key_actionBarActionModeDefaultSelector), this.searchAnimationProgress), false);
        }
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
        if (SharedConfig.getDevicePerformanceClass() != 0 && LiteMode.isEnabled(32768)) {
            z2 = false;
        }
        if (z) {
            ViewPage viewPage = this.viewPages[0];
            if (viewPage != null) {
                if (f < 1.0f) {
                    viewPage.setVisibility(0);
                }
                this.viewPages[0].setAlpha(1.0f - f);
                if (!z2) {
                    float f2 = (0.1f * f) + 0.9f;
                    this.viewPages[0].setScaleX(f2);
                    this.viewPages[0].setScaleY(f2);
                }
            }
            RightSlidingDialogContainer rightSlidingDialogContainer = this.rightSlidingDialogContainer;
            if (rightSlidingDialogContainer != null) {
                if (f >= 1.0f) {
                    rightSlidingDialogContainer.setVisibility(8);
                } else {
                    rightSlidingDialogContainer.setVisibility(0);
                    this.rightSlidingDialogContainer.setAlpha(1.0f - f);
                }
            }
            SearchViewPager searchViewPager = this.searchViewPager;
            if (searchViewPager != null) {
                searchViewPager.setAlpha(f);
                if (!z2) {
                    float f3 = ((1.0f - f) * 0.05f) + 1.0f;
                    this.searchViewPager.setScaleX(f3);
                    this.searchViewPager.setScaleY(f3);
                }
            }
        }
        updateContextViewPosition();
    }

    public void findAndUpdateCheckBox(long j, boolean z) {
        ViewPage[] viewPageArr = this.viewPages;
        if (viewPageArr == null) {
            return;
        }
        for (ViewPage viewPage : viewPageArr) {
            int childCount = viewPage.listView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = viewPage.listView.getChildAt(i);
                if (childAt instanceof DialogCell) {
                    DialogCell dialogCell = (DialogCell) childAt;
                    if (dialogCell.getDialogId() == j) {
                        dialogCell.setChecked(z, true);
                        break;
                    }
                }
            }
        }
    }

    public void checkListLoad(ViewPage viewPage) {
        checkListLoad(viewPage, viewPage.layoutManager.findFirstVisibleItemPosition(), viewPage.layoutManager.findLastVisibleItemPosition(), false);
    }

    void lambda$onItemLongClick$94(long j, AlertDialog alertDialog, int i) {
        this.searchViewPager.dialogsSearchAdapter.removeRecentSearch(j);
    }

    public void startMultiselect(final RecyclerListView recyclerListView, int i, final DialogsAdapter dialogsAdapter) {
        TLRPC.Dialog dialogForMultiselect = getDialogForMultiselect(i, dialogsAdapter);
        if (dialogForMultiselect == null) {
            return;
        }
        final boolean z = !this.selectedDialogs.contains(Long.valueOf(dialogForMultiselect.id));
        final HashSet hashSet = new HashSet(this.selectedDialogs);
        recyclerListView.startMultiselect(i, false, new RecyclerListView.onMultiSelectionChanged() { // from class: org.telegram.ui.DialogsActivity.34
            @Override // org.telegram.ui.Components.RecyclerListView.onMultiSelectionChanged
            public int checkPosition(int i2, boolean z2) {
                return i2;
            }

            @Override // org.telegram.ui.Components.RecyclerListView.onMultiSelectionChanged
            public boolean limitReached() {
                return false;
            }

            @Override // org.telegram.ui.Components.RecyclerListView.onMultiSelectionChanged
            public void onSelectionChanged(int i2, boolean z2, float f, float f2) {
                TLRPC.Dialog dialogForMultiselect2 = DialogsActivity.this.getDialogForMultiselect(i2, dialogsAdapter);
                if (dialogForMultiselect2 == null) {
                    return;
                }
                if (z) {
                    z2 = !z2;
                }
                if (z2 == DialogsActivity.this.selectedDialogs.contains(Long.valueOf(dialogForMultiselect2.id))) {
                    return;
                }
                RecyclerView.ViewHolder viewHolderFindViewHolderForAdapterPosition = recyclerListView.findViewHolderForAdapterPosition(i2);
                DialogsActivity.this.showOrUpdateActionMode(dialogForMultiselect2.id, viewHolderFindViewHolderForAdapterPosition != null ? viewHolderFindViewHolderForAdapterPosition.itemView : null);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.onMultiSelectionChanged
            public boolean canSelect(int i2) {
                TLRPC.Dialog dialogForMultiselect2 = DialogsActivity.this.getDialogForMultiselect(i2, dialogsAdapter);
                if (dialogForMultiselect2 == null) {
                    return false;
                }
                boolean z2 = z;
                HashSet hashSet2 = hashSet;
                if (z2) {
                    return hashSet2.contains(Long.valueOf(dialogForMultiselect2.id));
                }
                return !hashSet2.contains(Long.valueOf(dialogForMultiselect2.id));
            }

            @Override // org.telegram.ui.Components.RecyclerListView.onMultiSelectionChanged
            public void getPaddings(int[] iArr) {
                iArr[0] = recyclerListView.getPaddingTop();
                iArr[1] = recyclerListView.getPaddingBottom();
            }

            @Override // org.telegram.ui.Components.RecyclerListView.onMultiSelectionChanged
            public void scrollBy(int i2) {
                recyclerListView.scrollBy(0, i2);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.onMultiSelectionChanged
            public int getStartDragDistance() {
                return AndroidUtilities.dp(24.0f);
            }
        });
    }

    public TLRPC.Dialog getDialogForMultiselect(int i, DialogsAdapter dialogsAdapter) {
        Object item = dialogsAdapter.getItem(i);
        if (!(item instanceof TLRPC.Dialog) || (item instanceof TLRPC.TL_dialogFolder)) {
            return null;
        }
        return (TLRPC.Dialog) item;
    }

    private void onArchiveLongPress(View view) {
        try {
            view.performHapticFeedback(0, 2);
        } catch (Exception unused) {
        }
        BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity());
        boolean z = getMessagesStorage().getArchiveUnreadCount() != 0;
        builder.setItems(new CharSequence[]{z ? LocaleController.getString(R.string.MarkAllAsRead) : null, LocaleController.getString(SharedConfig.archiveHidden ? R.string.PinInTheList : R.string.HideAboveTheList)}, new int[]{z ? R.drawable.msg_markread : 0, SharedConfig.archiveHidden ? R.drawable.chats_pin : R.drawable.chats_unpin}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda111
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                this.f$0.lambda$onArchiveLongPress$95(dialogInterface, i);
            }
        });
        showDialog(builder.create());
    }

    public void lambda$performSelectedDialogsAction$107(ArrayList arrayList) {
        getMessagesController().addDialogToFolder(arrayList, (this.folderId == 0 && this.communityId == 0) ? 0 : 1, -1, null, 0L);
    }

    public void lambda$showOrUpdateActionMode$118(float f, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.progressToActionMode = fFloatValue;
        this.viewPages[0].setTranslationY((-f) * fFloatValue);
        for (int i = 0; i < this.actionBar.getChildCount(); i++) {
            if (this.actionBar.getChildAt(i).getVisibility() == 0 && this.actionBar.getChildAt(i) != this.actionBar.getActionMode() && this.actionBar.getChildAt(i) != this.actionBar.getBackButton()) {
                this.actionBar.getChildAt(i).setAlpha(1.0f - this.progressToActionMode);
            }
        }
        View view = this.fragmentView;
        if (view != null) {
            view.invalidate();
        }
        checkUi_searchFieldVisibility();
        checkUi_itemBackButtonVisibility();
    }

    public void closeSearch() {
        if (AndroidUtilities.isTablet()) {
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.closeSearchField();
            }
            TLObject tLObject = this.searchObject;
            if (tLObject != null) {
                SearchViewPager searchViewPager = this.searchViewPager;
                if (searchViewPager != null) {
                    searchViewPager.dialogsSearchAdapter.putRecentSearch(this.searchDialogId, tLObject);
                }
                this.searchObject = null;
                return;
            }
            return;
        }
        this.closeSearchFieldOnHide = true;
    }

    public RecyclerListView getListView() {
        return this.viewPages[0].listView;
    }

    public RecyclerListView getSearchListView() {
        createSearchViewPager();
        SearchViewPager searchViewPager = this.searchViewPager;
        if (searchViewPager != null) {
            return searchViewPager.searchListView;
        }
        return null;
    }

    public void createUndoView() {
        Context context;
        if (this.undoView[0] == null && (context = getContext()) != null) {
            for (int i = 0; i < 2; i++) {
                this.undoView[i] = new AnonymousClass38(context);
                FrameLayout.LayoutParams layoutParamsCreateFrame = LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f);
                layoutParamsCreateFrame.bottomMargin += this.navigationBarHeight + getCurrentUndoViewOffset();
                ContentView contentView = (ContentView) this.fragmentView;
                UndoView undoView = this.undoView[i];
                int i2 = this.undoViewIndex + 1;
                this.undoViewIndex = i2;
                contentView.addView(undoView, i2, layoutParamsCreateFrame);
            }
        }
    }

    public class AnonymousClass38 extends UndoView {
        public AnonymousClass38(Context context) {
            super(context);
        }

        @Override // android.view.View
        public void setTranslationY(float f) {
            super.setTranslationY(f);
            if (this == DialogsActivity.this.undoView[0]) {
                if (DialogsActivity.this.undoView[1] == null || DialogsActivity.this.undoView[1].getVisibility() != 0) {
                    DialogsActivity.this.additionalFloatingTranslation = Math.max(0.0f, (getMeasuredHeight() + AndroidUtilities.dp(8.0f)) - f);
                    DialogsActivity.this.updateFloatingButtonOffset();
                }
            }
        }

        @Override // org.telegram.ui.Components.UndoView
        public boolean canUndo() {
            for (int i = 0; i < DialogsActivity.this.viewPages.length; i++) {
                if (DialogsActivity.this.viewPages[i].dialogsItemAnimator.isRunning()) {
                    return false;
                }
            }
            return true;
        }

        @Override // org.telegram.ui.Components.UndoView
        public void onRemoveDialogAction(long j, int i) {
            if (i == 1 || i == 27) {
                DialogsActivity.this.debugLastUpdateAction = 1;
                DialogsActivity.this.setDialogsListFrozen(true);
                if (DialogsActivity.this.frozenDialogsList != null) {
                    final int i2 = 0;
                    while (true) {
                        if (i2 >= DialogsActivity.this.frozenDialogsList.size()) {
                            i2 = -1;
                            break;
                        } else if (((TLRPC.Dialog) DialogsActivity.this.frozenDialogsList.get(i2)).id == j) {
                            break;
                        } else {
                            i2++;
                        }
                    }
                    DialogsActivity dialogsActivity = DialogsActivity.this;
                    if (i2 >= 0) {
                        final TLRPC.Dialog dialog = (TLRPC.Dialog) dialogsActivity.frozenDialogsList.remove(i2);
                        DialogsActivity.this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$38$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$onRemoveDialogAction$0(i2, dialog);
                            }
                        });
                    } else {
                        dialogsActivity.setDialogsListFrozen(false);
                    }
                }
                DialogsActivity.this.checkAnimationFinished();
            }
        }

        public void m14511$r8$lambda$zVItCvp4gJVkCv3mlxLRNGrW6w(Activity activity, Boolean bool) {
        if (bool.booleanValue()) {
            if (!PermissionRequest.canAskPermission("android.permission.POST_NOTIFICATIONS")) {
                PermissionRequest.showPermissionSettings("android.permission.POST_NOTIFICATIONS");
            } else {
                activity.requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"}, 1);
            }
        }
    }

    public void lambda$didReceivedNotification$122(ViewPage viewPage, Object[] objArr) {
        reloadViewPageDialogs(viewPage, objArr.length > 0);
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView == null || filterTabsView.getVisibility() != 0) {
            return;
        }
        this.filterTabsView.checkTabsCounter();
    }

    public boolean $r8$lambda$B5Qy2nwztgDMMnVNlhDMmOmqjBQ(Runnable runnable, TLRPC.TL_error tL_error) {
        runnable.run();
        return true;
    }

    private void showSendToBotAlert(TLRPC.User user, final Runnable runnable, final Runnable runnable2) {
        TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(this.requestPeerBotId));
        showDialog(new AlertDialog.Builder(getContext()).setTitle(LocaleController.formatString(R.string.AreYouSureSendChatToBotTitle, UserObject.getFirstName(user), UserObject.getFirstName(user2))).setMessage(TextUtils.concat(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.AreYouSureSendChatToBotMessage, UserObject.getFirstName(user), UserObject.getFirstName(user2))))).setPositiveButton(LocaleController.formatString("Send", R.string.Send, new Object[0]), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda122
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                runnable.run();
            }
        }).setNegativeButton(LocaleController.formatString("Cancel", R.string.Cancel, new Object[0]), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda123
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                DialogsActivity.$r8$lambda$49xpN9BQFGftJduFG9vEfclcCIg(runnable2, alertDialog, i);
            }
        }).create());
    }

    public static void lambda$onSendLongClick$143() {
        this.notify = false;
        if (this.delegate == null || this.selectedDialogs.isEmpty()) {
            return;
        }
        ArrayList<MessagesStorage.TopicKey> arrayList = new ArrayList<>();
        for (int i = 0; i < this.selectedDialogs.size(); i++) {
            arrayList.add(MessagesStorage.TopicKey.of(this.selectedDialogs.get(i).longValue(), 0L));
        }
        this.delegate.didSelectDialogs(this, arrayList, this.commentView.getFieldText(), false, this.notify, this.scheduleDate, this.scheduleRepeatPeriod, null);
    }

    public /* JADX WARN: Code duplicated, block: B:128:0x0048 A[SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:19:0x0020  */
    /* JADX WARN: Code duplicated, block: B:21:0x0027  */
    /* JADX WARN: Code duplicated, block: B:23:0x002f  */
    /* JADX WARN: Code duplicated, block: B:24:0x0035  */
    /* JADX WARN: Code duplicated, block: B:26:0x0039  */
    /* JADX WARN: Code duplicated, block: B:27:0x003f  */
    /* JADX WARN: Code duplicated, block: B:29:0x0043  */
    public /* synthetic */ void lambda$getThemeDescriptions$145() {
        DialogsSearchAdapter dialogsSearchAdapter;
        RecyclerListView innerListView;
        ViewGroup viewGroup;
        int childCount;
        int i;
        View childAt;
        int i2 = 0;
        while (i2 < 3) {
            if (i2 == 2) {
                SearchViewPager searchViewPager = this.searchViewPager;
                if (searchViewPager != null) {
                    viewGroup = searchViewPager.searchListView;
                    if (viewGroup == null) {
                        childCount = viewGroup.getChildCount();
                        for (i = 0; i < childCount; i++) {
                            childAt = viewGroup.getChildAt(i);
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
            } else {
                ViewPage[] viewPageArr = this.viewPages;
                if (viewPageArr != null) {
                    viewGroup = i2 < viewPageArr.length ? viewPageArr[i2].listView : null;
                    if (viewGroup == null) {
                        childCount = viewGroup.getChildCount();
                        while (i < childCount) {
                            childAt = viewGroup.getChildAt(i);
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
            }
            i2++;
        }
        SearchViewPager searchViewPager2 = this.searchViewPager;
        if (searchViewPager2 != null && (dialogsSearchAdapter = searchViewPager2.dialogsSearchAdapter) != null && (innerListView = dialogsSearchAdapter.getInnerListView()) != null) {
            int childCount2 = innerListView.getChildCount();
            for (int i3 = 0; i3 < childCount2; i3++) {
                View childAt2 = innerListView.getChildAt(i3);
                if (childAt2 instanceof HintDialogCell) {
                    ((HintDialogCell) childAt2).update();
                }
            }
        }
        if (this.viewPages != null) {
            int i4 = 0;
            while (true) {
                ViewPage[] viewPageArr2 = this.viewPages;
                if (i4 >= viewPageArr2.length) {
                    break;
                }
                if (viewPageArr2[i4].pullForegroundDrawable != null) {
                    this.viewPages[i4].pullForegroundDrawable.updateColors();
                }
                i4++;
            }
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.setPopupBackgroundColor(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground), true);
            this.actionBar.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem), false, true);
            this.actionBar.setPopupItemsColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon), true, true);
            this.actionBar.setPopupItemsSelectorColor(getThemedColor(Theme.key_dialogButtonSelector), true);
            this.actionBar.updateColors();
        }
        if (this.statusDrawable != null) {
            updateStatus(UserConfig.getInstance(this.currentAccount).getCurrentUser(), false);
        }
        DialogsHintCell dialogsHintCell = this.dialogsHintCell;
        if (dialogsHintCell != null) {
            dialogsHintCell.setBackground(Theme.getSelectorDrawable(false));
        }
        ItemOptions itemOptions = this.filterOptions;
        if (itemOptions != null) {
            itemOptions.updateColors();
        }
        ActionBarMenuItem actionBarMenuItem = this.doneItem;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setIconColor(getThemedColor(Theme.key_actionBarDefaultIcon));
        }
        ChatActivityEnterView chatActivityEnterView = this.commentView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.updateColors();
        }
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null) {
            filterTabsView.updateColors();
        }
        FiltersView filtersView = this.filtersView;
        if (filtersView != null) {
            filtersView.updateColors();
        }
        SearchViewPager searchViewPager3 = this.searchViewPager;
        if (searchViewPager3 != null) {
            searchViewPager3.updateColors();
        }
        ViewPagerFixed.TabsView tabsView = this.searchTabsView;
        if (tabsView != null) {
            tabsView.updateColors();
        }
        SearchTabsAndFiltersLayout searchTabsAndFiltersLayout = this.searchTabsAndFiltersLayout;
        if (searchTabsAndFiltersLayout != null) {
            searchTabsAndFiltersLayout.updateColors();
        }
        View view = this.blurredView;
        if (view != null) {
            view.setForeground(new ColorDrawable(ColorUtils.setAlphaComponent(getThemedColor(Theme.key_windowBackgroundWhite), 100)));
        }
        FragmentFloatingButton fragmentFloatingButton = this.floatingButton3;
        if (fragmentFloatingButton != null) {
            fragmentFloatingButton.updateColors();
        }
        FragmentFloatingButton fragmentFloatingButton2 = this.floatingButtonStories;
        if (fragmentFloatingButton2 != null) {
            fragmentFloatingButton2.updateColors();
        }
        BlurredBackgroundSourceColor blurredBackgroundSourceColor = this.iBlur3SourceColor;
        int i5 = Theme.key_windowBackgroundWhite;
        blurredBackgroundSourceColor.setColor(getThemedColor(i5));
        DialogsActivityTopPanelLayout dialogsActivityTopPanelLayout = this.topPanelLayout;
        if (dialogsActivityTopPanelLayout != null) {
            dialogsActivityTopPanelLayout.updateColors();
        }
        DialogsActivityTopBubblesFadeView dialogsActivityTopBubblesFadeView = this.topBubblesFadeView;
        if (dialogsActivityTopBubblesFadeView != null) {
            dialogsActivityTopBubblesFadeView.setColor(Theme.getColor(i5));
        }
        FragmentContextView fragmentContextView = this.fragmentContextView;
        if (fragmentContextView != null) {
            fragmentContextView.updateColors();
        }
        FragmentContextView fragmentContextView2 = this.fragmentLocationContextView;
        if (fragmentContextView2 != null) {
            fragmentContextView2.updateColors();
        }
        setSearchAnimationProgress(this.searchAnimationProgress, false);
        DialogStoriesCell dialogStoriesCell = this.dialogStoriesCell;
        if (dialogStoriesCell != null) {
            dialogStoriesCell.updateColors();
        }
        Drawable drawable = this.logoDrawable;
        if (drawable != null) {
            drawable.setColorFilter(getThemedColor(Theme.key_telegram_color_dialogsLogo), PorterDuff.Mode.MULTIPLY);
        }
        ImageView imageView = this.actionModeCloseView;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_actionBarActionModeDefaultIcon), PorterDuff.Mode.MULTIPLY));
            this.actionModeCloseView.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_actionBarActionModeDefaultSelector)));
        }
        FragmentSearchField fragmentSearchField = this.fragmentSearchField;
        if (fragmentSearchField != null) {
            fragmentSearchField.updateColors();
        }
    }

    public /* synthetic */ void lambda$getThemeDescriptions$146() {
        SearchViewPager searchViewPager = this.searchViewPager;
        if (searchViewPager != null) {
            ActionBarMenu actionMode = searchViewPager.getActionMode();
            if (actionMode != null) {
                actionMode.setBackgroundColor(getThemedColor(Theme.key_actionBarActionModeDefault));
            }
            ActionBarMenuItem speedItem = this.searchViewPager.getSpeedItem();
            if (speedItem != null) {
                speedItem.getIconView().setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_actionBarActionModeDefaultIcon), PorterDuff.Mode.SRC_IN));
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public Animator getCustomSlideTransition(boolean z, boolean z2, float f) {
        if (z2) {
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.slideFragmentProgress, 1.0f);
            this.slideBackTransitionAnimator = valueAnimatorOfFloat;
            return valueAnimatorOfFloat;
        }
        int iClamp = (getLayoutContainer() == null || getLayoutContainer().getMeasuredWidth() <= 0) ? 150 : (int) Utilities.clamp((200.0f / getLayoutContainer().getMeasuredWidth()) * f, 200.0f, 80.0f);
        ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(this.slideFragmentProgress, 1.0f);
        this.slideBackTransitionAnimator = valueAnimatorOfFloat2;
        valueAnimatorOfFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda40
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$getCustomSlideTransition$147(valueAnimator);
            }
        });
        this.slideBackTransitionAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT);
        this.slideBackTransitionAnimator.setDuration(iClamp);
        this.slideBackTransitionAnimator.start();
        return this.slideBackTransitionAnimator;
    }

    public /* synthetic */ void lambda$getCustomSlideTransition$147(ValueAnimator valueAnimator) {
        setSlideTransitionProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void prepareFragmentToSlide(boolean z, boolean z2) {
        if (!z && z2) {
            this.isSlideBackTransition = true;
            setFragmentIsSliding(true);
        } else {
            this.slideBackTransitionAnimator = null;
            this.isSlideBackTransition = false;
            setFragmentIsSliding(false);
            setSlideTransitionProgress(1.0f);
        }
    }

    private void setFragmentIsSliding(boolean z) {
        ViewPage viewPage;
        if (SharedConfig.getDevicePerformanceClass() == 0 || !LiteMode.isEnabled(32768)) {
            return;
        }
        ViewPage[] viewPageArr = this.viewPages;
        if (z) {
            if (viewPageArr != null && (viewPage = viewPageArr[0]) != null) {
                viewPage.setLayerType(2, null);
                this.viewPages[0].setClipChildren(false);
                this.viewPages[0].setClipToPadding(false);
                this.viewPages[0].listView.setClipChildren(false);
            }
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.setLayerType(2, null);
            }
            View view = this.fragmentView;
            if (view != null) {
                ((ViewGroup) view).setClipChildren(false);
                this.fragmentView.requestLayout();
                return;
            }
            return;
        }
        if (viewPageArr != null) {
            for (ViewPage viewPage2 : viewPageArr) {
                if (viewPage2 != null) {
                    viewPage2.setLayerType(0, null);
                    viewPage2.setClipChildren(true);
                    viewPage2.setClipToPadding(true);
                    viewPage2.listView.setClipChildren(true);
                }
            }
        }
        ActionBar actionBar2 = this.actionBar;
        if (actionBar2 != null) {
            actionBar2.setLayerType(0, null);
        }
        DialogStoriesCell dialogStoriesCell = this.dialogStoriesCell;
        if (dialogStoriesCell != null) {
            dialogStoriesCell.setLayerType(0, null);
        }
        View view2 = this.fragmentView;
        if (view2 != null) {
            ((ViewGroup) view2).setClipChildren(true);
            this.fragmentView.requestLayout();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onSlideProgress(boolean z, float f) {
        if ((SharedConfig.getDevicePerformanceClass() > 0 || BuildVars.DEBUG_PRIVATE_VERSION) && this.isSlideBackTransition && this.slideBackTransitionAnimator == null) {
            setSlideTransitionProgress(f);
        }
    }

    private void setSlideTransitionProgress(float f) {
        if ((SharedConfig.getDevicePerformanceClass() > 0 || BuildVars.DEBUG_PRIVATE_VERSION) && this.slideFragmentProgress != f) {
            this.slideFragmentLite = !ExteraConfig.getSpringAnimations() && (SharedConfig.getDevicePerformanceClass() == 0 || !LiteMode.isEnabled(32768));
            this.slideFragmentProgress = f;
            View view = this.fragmentView;
            if (view != null) {
                view.invalidate();
            }
            if (this.slideFragmentLite) {
                float f2 = (-AndroidUtilities.dp(40.0f)) * (1.0f - this.slideFragmentProgress);
                DialogStoriesCell dialogStoriesCell = this.dialogStoriesCell;
                if (dialogStoriesCell != null) {
                    dialogStoriesCell.setTranslationX(f2);
                }
                FragmentSearchField fragmentSearchField = this.fragmentSearchField;
                if (fragmentSearchField != null) {
                    fragmentSearchField.setTranslationX(f2);
                }
                RightSlidingDialogContainer rightSlidingDialogContainer = this.rightSlidingDialogContainer;
                if (rightSlidingDialogContainer == null || rightSlidingDialogContainer.getFragmentView() == null || this.rightFragmentTransitionInProgress) {
                    return;
                }
                this.rightSlidingDialogContainer.getFragmentView().setTranslationX(f2);
                return;
            }
            float f3 = -AndroidUtilities.dp(4.0f);
            float f4 = this.slideFragmentProgress;
            float f5 = f3 * (1.0f - f4);
            float f6 = 1.0f - ((1.0f - f4) * 0.05f);
            DialogStoriesCell dialogStoriesCell2 = this.dialogStoriesCell;
            if (dialogStoriesCell2 != null) {
                dialogStoriesCell2.setScaleX(f6);
                this.dialogStoriesCell.setScaleY(f6);
                this.dialogStoriesCell.setTranslationX(f5);
                this.dialogStoriesCell.setPivotX(0.0f);
                this.dialogStoriesCell.setPivotY(0.0f);
            }
            FragmentSearchField fragmentSearchField2 = this.fragmentSearchField;
            if (fragmentSearchField2 != null) {
                fragmentSearchField2.setTranslationX(f5);
                this.fragmentSearchField.setScaleX(f6);
                this.fragmentSearchField.setScaleY(f6);
            }
            RightSlidingDialogContainer rightSlidingDialogContainer2 = this.rightSlidingDialogContainer;
            if (rightSlidingDialogContainer2 == null || rightSlidingDialogContainer2.getFragmentView() == null) {
                return;
            }
            if (!this.rightFragmentTransitionInProgress) {
                this.rightSlidingDialogContainer.getFragmentView().setScaleX(f6);
                this.rightSlidingDialogContainer.getFragmentView().setScaleY(f6);
                this.rightSlidingDialogContainer.getFragmentView().setTranslationX(f5);
            }
            this.rightSlidingDialogContainer.getFragmentView().setPivotX(0.0f);
            this.rightSlidingDialogContainer.getFragmentView().setPivotY(0.0f);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public INavigationLayout.BackButtonState getBackButtonState() {
        return (isArchive() || this.rightSlidingDialogContainer.isOpenned) ? INavigationLayout.BackButtonState.BACK : INavigationLayout.BackButtonState.MENU;
    }

    public void setShowSearch(String str, int i) {
        int positionForType;
        if (!this.searching) {
            this.initialSearchType = i;
            this.fragmentSearchField.editText.setText(str);
            this.fragmentSearchField.editText.setSelection(str.length());
            return;
        }
        this.fragmentSearchField.editText.setText(str);
        this.fragmentSearchField.editText.setSelection(str.length());
        SearchViewPager searchViewPager = this.searchViewPager;
        if (searchViewPager == null || (positionForType = searchViewPager.getPositionForType(i)) < 0 || this.searchViewPager.getTabsView().getCurrentTabId() == positionForType) {
            return;
        }
        this.searchViewPager.getTabsView().scrollToTab(positionForType, positionForType);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        RightSlidingDialogContainer rightSlidingDialogContainer;
        if (this.searching || (rightSlidingDialogContainer = this.rightSlidingDialogContainer) == null || rightSlidingDialogContainer.getFragment() == null) {
            return ColorUtils.calculateLuminance(getThemedColor(Theme.key_windowBackgroundWhite)) > 0.699999988079071d;
        }
        return this.rightSlidingDialogContainer.getFragment().isLightStatusBar();
    }

    @Override // org.telegram.ui.Components.FloatingDebug.FloatingDebugProvider
    public List<FloatingDebugController.DebugItem> onGetDebugItems() {
        return Arrays.asList(new FloatingDebugController.DebugItem(LocaleController.getString(R.string.DebugDialogsActivity)), new FloatingDebugController.DebugItem(LocaleController.getString(R.string.ClearLocalDatabase), new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda149
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onGetDebugItems$148();
            }
        }), new FloatingDebugController.DebugItem(LocaleController.getString(R.string.DebugClearSendMessageAsPeers), new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda150
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onGetDebugItems$149();
            }
        }));
    }

    public /* synthetic */ void lambda$onGetDebugItems$148() {
        getMessagesStorage().clearLocalDatabase();
        Toast.makeText(getContext(), LocaleController.getString(R.string.DebugClearLocalDatabaseSuccess), 0).show();
    }

    public /* synthetic */ void lambda$onGetDebugItems$149() {
        getMessagesController().clearSendAsPeers();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean closeLastFragment() {
        if (this.rightSlidingDialogContainer.hasFragment()) {
            this.rightSlidingDialogContainer.lambda$presentFragment$1();
            SearchViewPager searchViewPager = this.searchViewPager;
            if (searchViewPager == null) {
                return true;
            }
            searchViewPager.updateTabs();
            return true;
        }
        return super.closeLastFragment();
    }

    public boolean getAllowGlobalSearch() {
        return this.allowGlobalSearch;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean canBeginSlide() {
        FilterTabsView filterTabsView;
        if (this.rightSlidingDialogContainer.hasFragment()) {
            return false;
        }
        if (this.initialDialogsType == 3 && (filterTabsView = this.filterTabsView) != null && filterTabsView.getVisibility() == 0) {
            return this.filterTabsView.isFirstTab();
        }
        return true;
    }

    public int getSlideAmplitude() {
        return AndroidUtilities.dp(this.slideFragmentLite ? 40.0f : 20.0f) * (!this.slideFragmentLite ? -1 : 1);
    }

    public void updateStoriesVisibility(boolean z) {
        ActionBar actionBar;
        final boolean z2;
        if (this.dialogStoriesCell == null || this.storiesVisibilityAnimator != null) {
            return;
        }
        RightSlidingDialogContainer rightSlidingDialogContainer = this.rightSlidingDialogContainer;
        if ((rightSlidingDialogContainer != null && rightSlidingDialogContainer.hasFragment()) || this.searchIsShowed || (actionBar = this.actionBar) == null || actionBar.isActionModeShowed() || this.onlySelect) {
            return;
        }
        int i = 0;
        if (StoryRecorder.isVisible() || (getLastStoryViewer() != null && getLastStoryViewer().isFullyVisible())) {
            z = false;
        }
        boolean zHasOnlySelfStories = !isArchive() && getStoriesController().hasOnlySelfStories();
        if (this.communityId != 0) {
            z2 = false;
        } else if (isArchive()) {
            z2 = !getStoriesController().getHiddenList().isEmpty();
        } else {
            z2 = !zHasOnlySelfStories && getStoriesController().hasStories();
            zHasOnlySelfStories = getStoriesController().hasOnlySelfStories();
        }
        this.hasOnlySlefStories = zHasOnlySelfStories;
        boolean z3 = this.dialogStoriesCellVisible;
        boolean z4 = zHasOnlySelfStories || z2;
        this.dialogStoriesCellVisible = z4;
        if (z2 || z4) {
            this.dialogStoriesCell.updateItems(z, z4 != z3);
        }
        boolean z5 = this.dialogStoriesCellVisible;
        int i2 = 8;
        if (z5 != z3) {
            if (z) {
                ValueAnimator valueAnimator = this.storiesVisibilityAnimator2;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                if (this.dialogStoriesCellVisible && !isInPreviewMode()) {
                    this.dialogStoriesCell.setVisibility(0);
                }
                ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.progressToDialogStoriesCell, this.dialogStoriesCellVisible ? 1.0f : 0.0f);
                this.storiesVisibilityAnimator2 = valueAnimatorOfFloat;
                valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.DialogsActivity.44
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        DialogsActivity.this.progressToDialogStoriesCell = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                        View view = DialogsActivity.this.fragmentView;
                        if (view != null) {
                            view.invalidate();
                        }
                    }
                });
                this.storiesVisibilityAnimator2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.45
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        boolean z6 = dialogsActivity.dialogStoriesCellVisible;
                        dialogsActivity.progressToDialogStoriesCell = z6 ? 1.0f : 0.0f;
                        if (!z6) {
                            dialogsActivity.dialogStoriesCell.setVisibility(8);
                        }
                        View view = DialogsActivity.this.fragmentView;
                        if (view != null) {
                            view.invalidate();
                        }
                    }
                });
                this.storiesVisibilityAnimator2.setDuration(200L);
                this.storiesVisibilityAnimator2.setInterpolator(CubicBezierInterpolator.DEFAULT);
                this.storiesVisibilityAnimator2.start();
            } else {
                this.dialogStoriesCell.setVisibility((!z5 || isInPreviewMode()) ? 8 : 0);
                this.progressToDialogStoriesCell = this.dialogStoriesCellVisible ? 1.0f : 0.0f;
                View view = this.fragmentView;
                if (view != null) {
                    view.invalidate();
                }
            }
        }
        if (z2 == this.animateToHasStories) {
            return;
        }
        this.animateToHasStories = z2;
        if (z2) {
            this.dialogStoriesCell.setProgressToCollapse(1.0f, false);
        }
        if (z && !isInPreviewMode()) {
            this.dialogStoriesCell.setVisibility(0);
            float f = -this.scrollYOffset;
            float maxScrollYOffset = z2 ? 0.0f : getMaxScrollYOffset();
            ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.storiesVisibilityAnimator = valueAnimatorOfFloat2;
            valueAnimatorOfFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener(f, z2, maxScrollYOffset) { // from class: org.telegram.ui.DialogsActivity.46
                int currentValue;
                final /* synthetic */ float val$fromScrollY;
                final /* synthetic */ boolean val$newVisibility;
                final /* synthetic */ float val$toScrollY;

                {
                    this.val$fromScrollY = f;
                    this.val$newVisibility = z2;
                    this.val$toScrollY = maxScrollYOffset;
                    this.currentValue = (int) f;
                }

                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    DialogsActivity.this.progressToShowStories = ((Float) valueAnimator2.getAnimatedValue()).floatValue();
                    if (!this.val$newVisibility) {
                        DialogsActivity dialogsActivity = DialogsActivity.this;
                        dialogsActivity.progressToShowStories = 1.0f - dialogsActivity.progressToShowStories;
                    }
                    int iLerp = (int) AndroidUtilities.lerp(this.val$fromScrollY, this.val$toScrollY, ((Float) valueAnimator2.getAnimatedValue()).floatValue());
                    int i3 = iLerp - this.currentValue;
                    this.currentValue = iLerp;
                    DialogsActivity.this.viewPages[0].listView.scrollBy(0, i3);
                    View view2 = DialogsActivity.this.fragmentView;
                    if (view2 != null) {
                        view2.invalidate();
                    }
                }
            });
            this.storiesVisibilityAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.DialogsActivity.47
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    DialogsActivity dialogsActivity;
                    DialogsActivity dialogsActivity2 = DialogsActivity.this;
                    dialogsActivity2.storiesVisibilityAnimator = null;
                    boolean z6 = z2;
                    dialogsActivity2.hasStories = z6;
                    if (!z6 && !dialogsActivity2.hasOnlySlefStories) {
                        dialogsActivity2.dialogStoriesCell.setVisibility(8);
                    }
                    boolean z7 = z2;
                    DialogsActivity dialogsActivity3 = DialogsActivity.this;
                    if (!z7) {
                        dialogsActivity3.setScrollY(0.0f);
                        DialogsActivity.this.scrollAdditionalOffset = AndroidUtilities.dp(81.0f);
                    } else {
                        dialogsActivity3.scrollAdditionalOffset = -AndroidUtilities.dp(81.0f);
                        DialogsActivity dialogsActivity4 = DialogsActivity.this;
                        dialogsActivity4.setScrollY(-dialogsActivity4.getMaxScrollYOffsetWithoutSearch());
                    }
                    int i3 = 0;
                    while (true) {
                        int length = DialogsActivity.this.viewPages.length;
                        dialogsActivity = DialogsActivity.this;
                        if (i3 >= length) {
                            break;
                        }
                        if (dialogsActivity.viewPages[i3] != null) {
                            DialogsActivity.this.viewPages[i3].listView.requestLayout();
                        }
                        i3++;
                    }
                    View view2 = dialogsActivity.fragmentView;
                    if (view2 != null) {
                        view2.requestLayout();
                    }
                }
            });
            this.storiesVisibilityAnimator.setDuration(200L);
            this.storiesVisibilityAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.storiesVisibilityAnimator.start();
            return;
        }
        this.progressToShowStories = z2 ? 1.0f : 0.0f;
        this.hasStories = z2;
        DialogStoriesCell dialogStoriesCell = this.dialogStoriesCell;
        if ((z2 || this.hasOnlySlefStories) && !isInPreviewMode()) {
            i2 = 0;
        }
        dialogStoriesCell.setVisibility(i2);
        if (!z2) {
            setScrollY(0.0f);
        } else {
            this.scrollAdditionalOffset = -AndroidUtilities.dp(81.0f);
            setScrollY(-(isArchive() ? getMaxScrollYOffsetWithoutSearch() : getMaxScrollYOffset()));
            if (isArchive()) {
                this.viewPages[0].dialogsAdapter.notifyDataSetChanged();
            }
        }
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                break;
            }
            ViewPage viewPage = viewPageArr[i];
            if (viewPage != null) {
                viewPage.listView.requestLayout();
            }
            i++;
        }
        View view2 = this.fragmentView;
        if (view2 != null) {
            view2.requestLayout();
            this.fragmentView.invalidate();
        }
    }

    public void createSearchViewPager() {
        int i;
        SearchViewPager searchViewPager = this.searchViewPager;
        if ((searchViewPager != null && searchViewPager.getParent() == this.fragmentView) || this.fragmentView == null || getContext() == null) {
            return;
        }
        if (this.searchString != null) {
            i = 2;
        } else {
            i = !this.onlySelect ? 1 : 0;
        }
        SearchViewPager searchViewPager2 = new SearchViewPager(getContext(), this, i, this.initialDialogsType, this.folderId, this.communityId, new SearchViewPager.ChatPreviewDelegate() { // from class: org.telegram.ui.DialogsActivity.48
            @Override // org.telegram.ui.Components.SearchViewPager.ChatPreviewDelegate
            public void startChatPreview(RecyclerListView recyclerListView, DialogCell dialogCell) {
                DialogsActivity.this.showChatPreview(dialogCell);
            }

            @Override // org.telegram.ui.Components.SearchViewPager.ChatPreviewDelegate
            public void move(float f) {
                Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    DialogsActivity.this.movePreviewFragment(f);
                }
            }

            @Override // org.telegram.ui.Components.SearchViewPager.ChatPreviewDelegate
            public void finish() {
                Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    DialogsActivity.this.finishPreviewFragment();
                }
            }
        }) { // from class: org.telegram.ui.DialogsActivity.49
            final GradientProtectionDrawable gradientDrawable = new GradientProtectionDrawable(2);
            final GradientProtectionDrawable gradientDrawable2 = new GradientProtectionDrawable(8);

            @Override // org.telegram.ui.Components.ViewPagerFixed
            public boolean onBackProgress(float f) {
                return false;
            }

            @Override // org.telegram.ui.Components.ViewPagerFixed
            public void onTabPageSelected(int i2) {
                DialogsActivity.this.updateSpeedItem(isDownloadsTab(i2));
            }

            @Override // org.telegram.ui.Components.SearchViewPager
            public boolean includeDownloads() {
                RightSlidingDialogContainer rightSlidingDialogContainer = DialogsActivity.this.rightSlidingDialogContainer;
                return rightSlidingDialogContainer == null || !rightSlidingDialogContainer.hasFragment();
            }

            @Override // android.view.View
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                if (DialogsActivity.this.searchTabsAndFiltersLayout != null) {
                    DialogsActivity.this.searchTabsAndFiltersLayout.setTranslationY(f);
                }
            }

            @Override // android.view.View
            public void setAlpha(float f) {
                super.setAlpha(f);
                DialogsActivity.this.blur3_InvalidateBlur();
            }

            @Override // android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                if (DialogsActivity.this.searchTabsView != null || DialogsActivity.this.communityId != 0) {
                    int iDp = AndroidUtilities.dp(54.0f);
                    int measuredHeight = (((((BaseFragment) DialogsActivity.this).actionBar.getMeasuredHeight() + AndroidUtilities.dp(DialogsActivity.this.ADDITIONAL_LIST_HEIGHT_DP)) - AndroidUtilities.dp(2.0f)) - (DialogsActivity.this.communityId != 0 ? iDp : 0)) + (DialogsActivity.this.topPanelLayout != null ? (int) DialogsActivity.this.topPanelLayout.getAnimatedHeightWithPadding(AndroidUtilities.dp(7.0f)) : 0);
                    this.gradientDrawable.setColor(Theme.multAlpha(DialogsActivity.this.getThemedColor(Theme.key_windowBackgroundWhite), 0.7f));
                    this.gradientDrawable.setInsets(0, measuredHeight, 0, 0);
                    this.gradientDrawable.setBounds(0, 0, getMeasuredWidth(), measuredHeight + iDp);
                    this.gradientDrawable.draw(canvas);
                }
                if (DialogsActivity.this.navigationBarHeight > AndroidUtilities.dp(32.0f)) {
                    this.gradientDrawable2.setColor(Theme.multAlpha(DialogsActivity.this.getThemedColor(Theme.key_windowBackgroundWhite), 0.9f));
                    this.gradientDrawable2.setBounds(0, getMeasuredHeight() - DialogsActivity.this.navigationBarHeight, getMeasuredWidth(), getMeasuredHeight());
                    this.gradientDrawable2.draw(canvas);
                }
            }

            @Override // org.telegram.ui.Components.SearchViewPager
            public void onPageScrolled(int i2, int i3) {
                super.onPageScrolled(i2, i3);
                if (Build.VERSION.SDK_INT < 31 || DialogsActivity.this.scrollableViewNoiseSuppressor == null) {
                    return;
                }
                DialogsActivity.this.scrollableViewNoiseSuppressor.onScrolled(i2, i3);
                DialogsActivity.this.blur3_InvalidateBlur();
            }

            @Override // org.telegram.ui.Components.ViewPagerFixed
            public void onTabAnimationUpdate(boolean z) {
                super.onTabAnimationUpdate(z);
                if (Build.VERSION.SDK_INT < 31 || DialogsActivity.this.scrollableViewNoiseSuppressor == null) {
                    return;
                }
                DialogsActivity.this.blur3_InvalidateBlur();
            }
        };
        this.searchViewPager = searchViewPager2;
        ((ContentView) this.fragmentView).addView(searchViewPager2, this.searchViewPagerIndex);
        this.searchViewPager.dialogsSearchAdapter.setDelegate(new AnonymousClass50());
        this.searchViewPager.channelsSearchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda124
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i2, float f, float f2) {
                this.f$0.lambda$createSearchViewPager$150(view, i2, f, f2);
            }
        });
        this.searchViewPager.botsSearchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda125
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i2, float f, float f2) {
                this.f$0.lambda$createSearchViewPager$151(view, i2, f, f2);
            }
        });
        this.searchViewPager.hashtagSearchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda126
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i2) {
                this.f$0.lambda$createSearchViewPager$152(view, i2);
            }
        });
        this.searchViewPager.botsSearchListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda127
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i2) {
                return this.f$0.lambda$createSearchViewPager$154(view, i2);
            }
        });
        this.searchViewPager.searchListView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda128
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i2, float f, float f2) {
                this.f$0.lambda$createSearchViewPager$155(view, i2, f, f2);
            }
        });
        this.searchViewPager.searchListView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListenerExtended() { // from class: org.telegram.ui.DialogsActivity.51
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
            public boolean onItemClick(View view, int i2, float f, float f2) {
                if (view instanceof ProfileSearchCell) {
                    ProfileSearchCell profileSearchCell = (ProfileSearchCell) view;
                    if (profileSearchCell.isBlocked()) {
                        DialogsActivity.this.showPremiumBlockedToast(view, profileSearchCell.getDialogId());
                        return true;
                    }
                }
                DialogsActivity dialogsActivity = DialogsActivity.this;
                return dialogsActivity.onItemLongClick(dialogsActivity.searchViewPager.searchListView, view, i2, f, f2, -1, DialogsActivity.this.searchViewPager.dialogsSearchAdapter);
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
            public void onMove(float f, float f2) {
                Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    DialogsActivity.this.movePreviewFragment(f2);
                }
            }

            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListenerExtended
            public void onLongClickRelease() {
                Point point = AndroidUtilities.displaySize;
                if (point.x > point.y) {
                    DialogsActivity.this.finishPreviewFragment();
                }
            }
        });
        this.searchViewPager.setFilteredSearchViewDelegate(new FilteredSearchView.Delegate() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda129
            @Override // org.telegram.ui.FilteredSearchView.Delegate
            public final void updateFiltersView(boolean z, ArrayList arrayList, ArrayList arrayList2, boolean z2) {
                this.f$0.lambda$createSearchViewPager$156(z, arrayList, arrayList2, z2);
            }
        });
        this.searchViewPager.setAlpha(0.0f);
        this.searchViewPager.setScaleX(1.05f);
        this.searchViewPager.setScaleY(1.05f);
        this.searchViewPager.setVisibility(8);
        this.searchViewPager.setBlurredBackgroundDrawableFactory(this.iBlur3FactoryBlur);
    }

    public class AnonymousClass50 implements DialogsSearchAdapter.DialogsSearchAdapterDelegate {
        public AnonymousClass50() {
        }

        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate
        public void searchStateChanged(boolean z, boolean z2) {
            if (DialogsActivity.this.searchViewPager.emptyView.getVisibility() == 0) {
                z2 = true;
            }
            if (DialogsActivity.this.searching && DialogsActivity.this.searchWas && DialogsActivity.this.searchViewPager.emptyView != null) {
                if (z || DialogsActivity.this.searchViewPager.dialogsSearchAdapter.getItemCount() != 0) {
                    DialogsActivity.this.searchViewPager.emptyView.showProgress(true, z2);
                } else {
                    DialogsActivity.this.searchViewPager.emptyView.showProgress(false, z2);
                }
            }
            if (z && DialogsActivity.this.searchViewPager.dialogsSearchAdapter.getItemCount() == 0) {
                DialogsActivity.this.searchViewPager.cancelEnterAnimation();
            }
        }

        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate
        public void didPressedBlockedDialog(View view, long j) {
            DialogsActivity.this.showPremiumBlockedToast(view, j);
        }

        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate
        public void didPressedOnSubDialog(long j) {
            DialogsActivity dialogsActivity;
            if (DialogsActivity.this.onlySelect) {
                if (DialogsActivity.this.validateSlowModeDialog(j)) {
                    boolean zIsEmpty = DialogsActivity.this.selectedDialogs.isEmpty();
                    DialogsActivity dialogsActivity2 = DialogsActivity.this;
                    if (!zIsEmpty) {
                        DialogsActivity.this.findAndUpdateCheckBox(j, dialogsActivity2.addOrRemoveSelectedDialog(j, null));
                        DialogsActivity.this.updateSelectedCount();
                        ((BaseFragment) DialogsActivity.this).actionBar.closeSearchField();
                        return;
                    }
                    dialogsActivity2.didSelectResult(j, 0L, true, false);
                    return;
                }
                return;
            }
            Bundle bundle = new Bundle();
            if (DialogObject.isUserDialog(j)) {
                bundle.putLong("user_id", j);
            } else {
                bundle.putLong("chat_id", -j);
            }
            DialogsActivity.this.closeSearch();
            if (AndroidUtilities.isTablet() && DialogsActivity.this.viewPages != null) {
                int i = 0;
                while (true) {
                    int length = DialogsActivity.this.viewPages.length;
                    dialogsActivity = DialogsActivity.this;
                    if (i >= length) {
                        break;
                    }
                    DialogsAdapter dialogsAdapter = dialogsActivity.viewPages[i].dialogsAdapter;
                    DialogsActivity.this.openedDialogId.dialogId = j;
                    dialogsAdapter.setOpenedDialogId(j);
                    i++;
                }
                dialogsActivity.updateVisibleRows(MessagesController.UPDATE_MASK_SELECT_DIALOG);
            }
            String str = DialogsActivity.this.searchString;
            DialogsActivity dialogsActivity3 = DialogsActivity.this;
            if (str != null) {
                if (dialogsActivity3.getMessagesController().checkCanOpenChat(bundle, DialogsActivity.this)) {
                    DialogsActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
                    DialogsActivity.this.presentFragment(new ChatActivity(bundle));
                    return;
                }
                return;
            }
            if (dialogsActivity3.getMessagesController().checkCanOpenChat(bundle, DialogsActivity.this)) {
                DialogsActivity.this.presentFragment(new ChatActivity(bundle));
            }
        }

        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate
        public void needRemoveHint(final long j) {
            TLRPC.User user;
            if (DialogsActivity.this.getParentActivity() == null || (user = DialogsActivity.this.getMessagesController().getUser(Long.valueOf(j))) == null) {
                return;
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
            builder.setTitle(LocaleController.getString(R.string.ChatHintsDeleteAlertTitle));
            builder.setMessage(AndroidUtilities.replaceTags(LocaleController.formatString("ChatHintsDeleteAlert", R.string.ChatHintsDeleteAlert, ContactsController.formatName(user.first_name, user.last_name))));
            builder.setPositiveButton(LocaleController.getString(R.string.StickersRemove), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.DialogsActivity$50$$ExternalSyntheticLambda2
                @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                public final void onClick(AlertDialog alertDialog, int i) {
                    this.f$0.lambda$needRemoveHint$0(j, alertDialog, i);
                }
            });
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            AlertDialog alertDialogCreate = builder.create();
            DialogsActivity.this.showDialog(alertDialogCreate);
            TextView textView = (TextView) alertDialogCreate.getButton(-1);
            if (textView != null) {
                textView.setTextColor(DialogsActivity.this.getThemedColor(Theme.key_text_RedBold));
            }
        }

        public /* synthetic */ void lambda$needRemoveHint$0(long j, AlertDialog alertDialog, int i) {
            DialogsActivity.this.getMediaDataController().removePeer(j);
        }

        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate
        public void needClearList() {
            AlertDialog.Builder builder = new AlertDialog.Builder(DialogsActivity.this.getParentActivity());
            if (DialogsActivity.this.searchViewPager.dialogsSearchAdapter.isSearchWas() && DialogsActivity.this.searchViewPager.dialogsSearchAdapter.isRecentSearchDisplayed()) {
                builder.setTitle(LocaleController.getString(R.string.ClearSearchAlertPartialTitle));
                builder.setMessage(LocaleController.formatPluralString("ClearSearchAlertPartial", DialogsActivity.this.searchViewPager.dialogsSearchAdapter.getRecentResultsCount(), new Object[0]));
                builder.setPositiveButton(LocaleController.getString(R.string.Clear), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.DialogsActivity$50$$ExternalSyntheticLambda0
                    @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                    public final void onClick(AlertDialog alertDialog, int i) {
                        this.f$0.lambda$needClearList$1(alertDialog, i);
                    }
                });
            } else {
                builder.setTitle(LocaleController.getString(R.string.ClearSearchAlertTitle));
                builder.setMessage(LocaleController.getString(R.string.ClearSearchAlert));
                builder.setPositiveButton(LocaleController.getString(R.string.ClearButton), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.DialogsActivity$50$$ExternalSyntheticLambda1
                    @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                    public final void onClick(AlertDialog alertDialog, int i) {
                        this.f$0.lambda$needClearList$2(alertDialog, i);
                    }
                });
            }
            builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
            AlertDialog alertDialogCreate = builder.create();
            DialogsActivity.this.showDialog(alertDialogCreate);
            TextView textView = (TextView) alertDialogCreate.getButton(-1);
            if (textView != null) {
                textView.setTextColor(DialogsActivity.this.getThemedColor(Theme.key_text_RedBold));
            }
        }

        public /* synthetic */ void lambda$needClearList$1(AlertDialog alertDialog, int i) {
            DialogsActivity.this.searchViewPager.dialogsSearchAdapter.clearRecentSearch();
        }

        public /* synthetic */ void lambda$needClearList$2(AlertDialog alertDialog, int i) {
            boolean zIsRecentSearchDisplayed = DialogsActivity.this.searchViewPager.dialogsSearchAdapter.isRecentSearchDisplayed();
            DialogsActivity dialogsActivity = DialogsActivity.this;
            if (zIsRecentSearchDisplayed) {
                dialogsActivity.searchViewPager.dialogsSearchAdapter.clearRecentSearch();
            } else {
                dialogsActivity.searchViewPager.dialogsSearchAdapter.clearRecentHashtags();
            }
        }

        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate
        public void runResultsEnterAnimation() {
            if (DialogsActivity.this.searchViewPager != null) {
                DialogsActivity.this.searchViewPager.runResultsEnterAnimation();
            }
        }

        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate
        public boolean isSelected(long j) {
            return DialogsActivity.this.selectedDialogs.contains(Long.valueOf(j));
        }

        @Override // org.telegram.ui.Adapters.DialogsSearchAdapter.DialogsSearchAdapterDelegate
        public long getSearchForumDialogId() {
            RightSlidingDialogContainer rightSlidingDialogContainer = DialogsActivity.this.rightSlidingDialogContainer;
            if (rightSlidingDialogContainer == null || !(rightSlidingDialogContainer.getFragment() instanceof TopicsFragment)) {
                return 0L;
            }
            return ((TopicsFragment) DialogsActivity.this.rightSlidingDialogContainer.getFragment()).getDialogId();
        }
    }

    public /* synthetic */ void lambda$createSearchViewPager$150(View view, int i, float f, float f2) {
        Object object = this.searchViewPager.channelsSearchAdapter.getObject(i);
        if (object instanceof TLRPC.Chat) {
            Bundle bundle = new Bundle();
            bundle.putLong("chat_id", ((TLRPC.Chat) object).id);
            ChatActivity chatActivity = new ChatActivity(bundle);
            chatActivity.setNextChannels(this.searchViewPager.channelsSearchAdapter.getNextChannels(i));
            presentFragment(chatActivity);
            return;
        }
        if (object instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) object;
            Bundle bundle2 = new Bundle();
            if (messageObject.getDialogId() >= 0) {
                bundle2.putLong("user_id", messageObject.getDialogId());
            } else {
                bundle2.putLong("chat_id", -messageObject.getDialogId());
            }
            bundle2.putInt("message_id", messageObject.getId());
            presentFragment(highlightFoundQuote(new ChatActivity(bundle2), messageObject));
        }
    }

    public /* synthetic */ void lambda$createSearchViewPager$151(View view, int i, float f, float f2) {
        Object object = this.searchViewPager.botsSearchAdapter.getObject(i);
        if (object instanceof TLRPC.User) {
            presentFragment(ProfileActivity.of(((TLRPC.User) object).id));
            return;
        }
        if (object instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) object;
            Bundle bundle = new Bundle();
            if (messageObject.getDialogId() >= 0) {
                bundle.putLong("user_id", messageObject.getDialogId());
            } else {
                bundle.putLong("chat_id", -messageObject.getDialogId());
            }
            bundle.putInt("message_id", messageObject.getId());
            presentFragment(highlightFoundQuote(new ChatActivity(bundle), messageObject));
        }
    }

    public /* synthetic */ void lambda$createSearchViewPager$152(View view, int i) {
        Object obj = this.searchViewPager.hashtagSearchAdapter.getItem(i).object;
        if (obj instanceof MessageObject) {
            MessageObject messageObject = (MessageObject) obj;
            Bundle bundle = new Bundle();
            if (messageObject.getDialogId() >= 0) {
                bundle.putLong("user_id", messageObject.getDialogId());
            } else {
                bundle.putLong("chat_id", -messageObject.getDialogId());
            }
            bundle.putInt("message_id", messageObject.getId());
            presentFragment(highlightFoundQuote(new ChatActivity(bundle), messageObject));
            return;
        }
        if (obj instanceof StoriesController.SearchStoriesList) {
            StoriesController.SearchStoriesList searchStoriesList = (StoriesController.SearchStoriesList) obj;
            Bundle bundle2 = new Bundle();
            bundle2.putInt(TeXSymbolParser.TYPE_ATTR, 3);
            bundle2.putString("hashtag", searchStoriesList.query);
            bundle2.putInt("storiesCount", searchStoriesList.getCount());
            presentFragment(new MediaActivity(bundle2, null));
        }
    }

    public /* synthetic */ boolean lambda$createSearchViewPager$154(View view, int i) {
        Object topPeerObject = this.searchViewPager.botsSearchAdapter.getTopPeerObject(i);
        if (!(topPeerObject instanceof TLRPC.User)) {
            return false;
        }
        final TLRPC.User user = (TLRPC.User) topPeerObject;
        new AlertDialog.Builder(getContext(), this.resourceProvider).setTitle(LocaleController.getString(R.string.AppsClearSearch)).setMessage(LocaleController.formatString(R.string.AppsClearSearchAlert, "\"" + UserObject.getUserName(user) + "\"")).setNegativeButton(LocaleController.getString(R.string.Cancel), null).setPositiveButton(LocaleController.getString(R.string.Remove), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda153
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i2) {
                this.f$0.lambda$createSearchViewPager$153(user, alertDialog, i2);
            }
        }).makeRed(-1).show();
        return false;
    }

    public /* synthetic */ void lambda$createSearchViewPager$153(TLRPC.User user, AlertDialog alertDialog, int i) {
        getMediaDataController().removeWebapp(user.id);
    }

    public /* synthetic */ void lambda$createSearchViewPager$155(View view, int i, float f, float f2) {
        Object item = this.searchViewPager.dialogsSearchAdapter.getItem(i);
        if (item instanceof TLRPC.TL_sponsoredPeer) {
            TLRPC.TL_sponsoredPeer tL_sponsoredPeer = (TLRPC.TL_sponsoredPeer) item;
            presentFragment(ChatActivity.of(DialogObject.getPeerDialogId(tL_sponsoredPeer.peer)));
            this.searchViewPager.dialogsSearchAdapter.clickedSponsoredPeer(tL_sponsoredPeer);
            return;
        }
        if (view instanceof ProfileSearchCell) {
            ProfileSearchCell profileSearchCell = (ProfileSearchCell) view;
            if (profileSearchCell.isBlocked()) {
                showPremiumBlockedToast(view, profileSearchCell.getDialogId());
                return;
            }
        }
        int i2 = this.initialDialogsType;
        SearchViewPager searchViewPager = this.searchViewPager;
        if (i2 == 10) {
            onItemLongClick(searchViewPager.searchListView, view, i, f, f2, -1, searchViewPager.dialogsSearchAdapter);
        } else {
            onItemClick(view, i, searchViewPager.dialogsSearchAdapter, f, f2);
        }
    }

    public /* synthetic */ void lambda$createSearchViewPager$156(boolean z, ArrayList arrayList, ArrayList arrayList2, boolean z2) {
        updateFiltersView(z, arrayList, arrayList2, z2, true);
    }

    public boolean clickSelectsDialog() {
        return this.initialDialogsType == 10;
    }

    public void openSetAvatar() {
        try {
            ((RLottieDrawable) ((AvatarDrawable) this.dialogsHintCell.imageView.getImageReceiver().getStaticThumb()).getCustomIcon()).restart(true);
        } catch (Exception unused) {
        }
        if (this.imageUpdater == null) {
            ImageUpdater imageUpdater = new ImageUpdater(true, 0, true);
            this.imageUpdater = imageUpdater;
            imageUpdater.setOpenWithFrontfaceCamera(true);
            ImageUpdater imageUpdater2 = this.imageUpdater;
            imageUpdater2.parentFragment = this;
            imageUpdater2.setDelegate(new AnonymousClass52());
            getMediaDataController().checkFeaturedStickers();
            getMessagesController().loadSuggestedFilters();
            getMessagesController().loadUserInfo(getUserConfig().getCurrentUser(), true, this.classGuid);
        }
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(UserConfig.getInstance(this.currentAccount).getClientUserId()));
        if (user == null) {
            user = UserConfig.getInstance(this.currentAccount).getCurrentUser();
        }
        if (user == null) {
            return;
        }
        this.imageUpdater.updateColors();
        ImageUpdater imageUpdater3 = this.imageUpdater;
        TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
        imageUpdater3.openMenu((userProfilePhoto == null || userProfilePhoto.photo_big == null || (userProfilePhoto instanceof TLRPC.TL_userProfilePhotoEmpty)) ? false : true, new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda98
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openSetAvatar$157();
            }
        }, new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda99
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                this.f$0.lambda$openSetAvatar$158(dialogInterface);
            }
        }, 0);
    }

    public class AnonymousClass52 implements ImageUpdater.ImageUpdaterDelegate {
        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public boolean supportsBulletin() {
            return true;
        }

        public AnonymousClass52() {
        }

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public void didStartUpload(boolean z, boolean z2) {
            if (DialogsActivity.this.uploadingAvatarBulletin != null) {
                DialogsActivity.this.uploadingAvatarBulletin.hide();
                DialogsActivity.this.uploadingAvatarBulletin = null;
            }
            Bulletin.ProgressLayout progressLayout = new Bulletin.ProgressLayout(DialogsActivity.this.getContext(), ((BaseFragment) DialogsActivity.this).resourceProvider);
            BackupImageView backupImageView = progressLayout.imageView;
            if (z) {
                backupImageView.setImageBitmap(DialogsActivity.this.imageUpdater.getPreviewBitmap());
            } else {
                backupImageView.setImageBitmap(PhotoViewer.getInstance().centerImage.getBitmap());
            }
            progressLayout.setButton(new Bulletin.UndoButton(DialogsActivity.this.getContext(), true, ((BaseFragment) DialogsActivity.this).resourceProvider).setText(LocaleController.getString(R.string.ViewAction)).setUndoAction(new Runnable() { // from class: org.telegram.ui.DialogsActivity$52$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.openAvatarInProfile();
                }
            }));
            progressLayout.getButton().setVisibility(8);
            progressLayout.textView.setText(LocaleController.getString(z2 ? R.string.YourProfileVideoUploading : R.string.YourProfilePhotoUploading), true);
            DialogsActivity dialogsActivity = DialogsActivity.this;
            dialogsActivity.uploadingAvatarBulletin = BulletinFactory.of(dialogsActivity).create(progressLayout, -1);
            DialogsActivity.this.uploadingAvatarBulletin.hideAfterBottomSheet = false;
            DialogsActivity.this.uploadingAvatarBulletin.setCanHide(false);
            DialogsActivity.this.uploadingAvatarBulletin.skipShowAnimation();
            DialogsActivity.this.uploadingAvatarBulletin.show();
        }

        public void openAvatarInProfile() {
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", UserConfig.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).getClientUserId());
            bundle.putBoolean("my_profile", true);
            DialogsActivity.this.presentFragment(new ProfileActivity(bundle, null));
        }

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public PhotoViewer.PlaceProviderObject getCloseIntoObject() {
            if (DialogsActivity.this.uploadingAvatarBulletin == null) {
                return null;
            }
            Bulletin.ProgressLayout progressLayout = (Bulletin.ProgressLayout) DialogsActivity.this.uploadingAvatarBulletin.getLayout();
            PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
            int[] iArr = new int[2];
            progressLayout.imageView.getLocationInWindow(iArr);
            placeProviderObject.viewX = iArr[0];
            placeProviderObject.viewY = iArr[1];
            placeProviderObject.parentView = DialogsActivity.this.fragmentView;
            ImageReceiver imageReceiver = progressLayout.imageView.getImageReceiver();
            placeProviderObject.imageReceiver = imageReceiver;
            placeProviderObject.thumb = imageReceiver.getBitmapSafe();
            placeProviderObject.clipBottomAddition = 0;
            placeProviderObject.radius = placeProviderObject.imageReceiver.getRoundRadius();
            placeProviderObject.scale = progressLayout.imageView.getScaleX();
            return placeProviderObject;
        }

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public void onUploadProgressChanged(float f) {
            if (DialogsActivity.this.uploadingAvatarBulletin != null) {
                ((Bulletin.ProgressLayout) DialogsActivity.this.uploadingAvatarBulletin.getLayout()).setProgress(f * 0.9f);
            }
        }

        @Override // org.telegram.ui.Components.ImageUpdater.ImageUpdaterDelegate
        public void didUploadPhoto(final TLRPC.InputFile inputFile, final TLRPC.InputFile inputFile2, final double d, final String str, final TLRPC.PhotoSize photoSize, final TLRPC.PhotoSize photoSize2, final boolean z, final TLRPC.VideoSize videoSize) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$52$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didUploadPhoto$2(inputFile, inputFile2, videoSize, d, str, z, photoSize2, photoSize);
                }
            });
        }

        public /* synthetic */ void lambda$didUploadPhoto$1(final String str, final boolean z, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.DialogsActivity$52$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didUploadPhoto$0(tL_error, tLObject, str, z);
                }
            });
        }

        public /* synthetic */ void lambda$didUploadPhoto$0(TLRPC.TL_error tL_error, TLObject tLObject, String str, boolean z) {
            if (tL_error == null) {
                TLRPC.User user = DialogsActivity.this.getMessagesController().getUser(Long.valueOf(DialogsActivity.this.getUserConfig().getClientUserId()));
                DialogsActivity dialogsActivity = DialogsActivity.this;
                if (user == null) {
                    user = dialogsActivity.getUserConfig().getCurrentUser();
                    if (user == null) {
                        return;
                    } else {
                        DialogsActivity.this.getMessagesController().putUser(user, false);
                    }
                } else {
                    dialogsActivity.getUserConfig().setCurrentUser(user);
                }
                TLRPC.TL_photos_photo tL_photos_photo = (TLRPC.TL_photos_photo) tLObject;
                ArrayList<TLRPC.PhotoSize> arrayList = tL_photos_photo.photo.sizes;
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(arrayList, 150);
                TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(arrayList, 800);
                TLRPC.VideoSize closestVideoSizeWithSize = tL_photos_photo.photo.video_sizes.isEmpty() ? null : FileLoader.getClosestVideoSizeWithSize(tL_photos_photo.photo.video_sizes, 1000);
                TLRPC.TL_userProfilePhoto tL_userProfilePhoto = new TLRPC.TL_userProfilePhoto();
                user.photo = tL_userProfilePhoto;
                tL_userProfilePhoto.photo_id = tL_photos_photo.photo.id;
                if (closestPhotoSizeWithSize != null) {
                    tL_userProfilePhoto.photo_small = closestPhotoSizeWithSize.location;
                }
                if (closestPhotoSizeWithSize2 != null) {
                    tL_userProfilePhoto.photo_big = closestPhotoSizeWithSize2.location;
                }
                if (closestPhotoSizeWithSize != null && DialogsActivity.this.avatar != null) {
                    FileLoader.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).getPathToAttach(DialogsActivity.this.avatar, true).renameTo(FileLoader.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).getPathToAttach(closestPhotoSizeWithSize, true));
                    ImageLoader.getInstance().replaceImageInCache(DialogsActivity.this.avatar.volume_id + "_" + DialogsActivity.this.avatar.local_id + "@50_50", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@50_50", ImageLocation.getForUserOrChat(((BaseFragment) DialogsActivity.this).currentAccount, user, 1), false);
                }
                if (closestVideoSizeWithSize != null && str != null) {
                    new File(str).renameTo(FileLoader.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).getPathToAttach(closestVideoSizeWithSize, "mp4", true));
                } else if (closestPhotoSizeWithSize2 != null && DialogsActivity.this.avatarBig != null) {
                    FileLoader.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).getPathToAttach(DialogsActivity.this.avatarBig, true).renameTo(FileLoader.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).getPathToAttach(closestPhotoSizeWithSize2, true));
                }
                DialogsActivity.this.getMessagesController().getDialogPhotos(user.id).addPhotoAtStart(tL_photos_photo.photo);
                ArrayList arrayList2 = new ArrayList();
                arrayList2.add(user);
                DialogsActivity.this.getMessagesStorage().putUsersAndChats(arrayList2, null, false, true);
                TLRPC.UserFull userFull = DialogsActivity.this.getMessagesController().getUserFull(DialogsActivity.this.getUserConfig().getClientUserId());
                if (userFull != null) {
                    userFull.profile_photo = tL_photos_photo.photo;
                    DialogsActivity.this.getMessagesStorage().updateUserInfo(userFull, false);
                }
            }
            DialogsActivity.this.avatar = null;
            DialogsActivity.this.avatarBig = null;
            DialogsActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(MessagesController.UPDATE_MASK_ALL));
            DialogsActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
            DialogsActivity.this.getUserConfig().saveConfig(true);
            MessagesController.getInstance(((BaseFragment) DialogsActivity.this).currentAccount).removeSuggestion(0L, "USERPIC_SETUP");
            DialogsActivity.this.updateDialogsHint();
            if (DialogsActivity.this.uploadingAvatarBulletin != null) {
                Bulletin.ProgressLayout progressLayout = (Bulletin.ProgressLayout) DialogsActivity.this.uploadingAvatarBulletin.getLayout();
                progressLayout.textView.setText(LocaleController.getString(z ? R.string.YourProfileVideoDone : R.string.YourProfilePhotoDone), true);
                progressLayout.setProgress(1.0f);
                Bulletin.Button button = progressLayout.getButton();
                button.setScaleX(0.6f);
                button.setScaleY(0.6f);
                button.setAlpha(0.0f);
                button.setVisibility(0);
                button.animate().scaleX(1.0f).scaleY(1.0f).alpha(1.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(360L).start();
                DialogsActivity.this.uploadingAvatarBulletin.setDuration(5000);
                DialogsActivity.this.uploadingAvatarBulletin.setCanHide(false);
                DialogsActivity.this.uploadingAvatarBulletin.setCanHide(true);
            }
        }

        public /* synthetic */ void lambda$didUploadPhoto$2(TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, TLRPC.VideoSize videoSize, double d, final String str, final boolean z, TLRPC.PhotoSize photoSize, TLRPC.PhotoSize photoSize2) {
            if (inputFile != null || inputFile2 != null || videoSize != null) {
                if (DialogsActivity.this.avatar == null) {
                    return;
                }
                TLRPC.TL_photos_uploadProfilePhoto tL_photos_uploadProfilePhoto = new TLRPC.TL_photos_uploadProfilePhoto();
                if (inputFile != null) {
                    tL_photos_uploadProfilePhoto.file = inputFile;
                    tL_photos_uploadProfilePhoto.flags |= 1;
                }
                if (inputFile2 != null) {
                    tL_photos_uploadProfilePhoto.video = inputFile2;
                    int i = tL_photos_uploadProfilePhoto.flags;
                    tL_photos_uploadProfilePhoto.video_start_ts = d;
                    tL_photos_uploadProfilePhoto.flags = i | 6;
                }
                if (videoSize != null) {
                    tL_photos_uploadProfilePhoto.video_emoji_markup = videoSize;
                    tL_photos_uploadProfilePhoto.flags |= 16;
                }
                DialogsActivity dialogsActivity = DialogsActivity.this;
                dialogsActivity.avatarUploadingRequest = dialogsActivity.getConnectionsManager().sendRequest(tL_photos_uploadProfilePhoto, new RequestDelegate() { // from class: org.telegram.ui.DialogsActivity$52$$ExternalSyntheticLambda2
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        this.f$0.lambda$didUploadPhoto$1(str, z, tLObject, tL_error);
                    }
                });
            } else {
                DialogsActivity.this.avatar = photoSize.location;
                DialogsActivity.this.avatarBig = photoSize2.location;
            }
            ((BaseFragment) DialogsActivity.this).actionBar.createMenu().requestLayout();
        }
    }

    public /* synthetic */ void lambda$openSetAvatar$157() {
        MessagesController.getInstance(this.currentAccount).deleteUserPhoto(null);
    }

    public /* synthetic */ void lambda$openSetAvatar$158(DialogInterface dialogInterface) {
        if (this.imageUpdater.isUploadingImage()) {
            MessagesController.getInstance(this.currentAccount).removeSuggestion(0L, "USERPIC_SETUP");
            updateDialogsHint();
        }
    }

    private void openWriteContacts() {
        Bundle bundle = new Bundle();
        bundle.putBoolean("destroyAfterSelect", true);
        presentFragment(new ContactsActivity(bundle));
    }

    private void openStoriesRecorder() {
        if (!this.storiesEnabled) {
            HintView2 hintView2 = this.storyPremiumHint;
            if (hintView2 != null) {
                if (hintView2.shown()) {
                    return;
                } else {
                    AndroidUtilities.removeFromParent(this.storyPremiumHint);
                }
            }
            HintView2 bgColor = new HintView2(getContext(), 2).setRounding(8.0f).setDuration(8000L).setCloseButton(true).setMultilineText(true).setMaxWidthPx(AndroidUtilities.displaySize.x - AndroidUtilities.dp(148.0f)).setText(AndroidUtilities.replaceSingleTag(LocaleController.getString("StoriesPremiumHint2").replace('\n', ' '), Theme.key_undo_cancelColor, 0, new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda92
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$openStoriesRecorder$159();
                }
            })).setJoint(1.0f, -40.0f).setBgColor(getThemedColor(Theme.key_undo_background));
            this.storyPremiumHint = bgColor;
            bgColor.setTranslationY((-this.navigationBarHeight) - this.additionNavigationBarHeight);
            ((ViewGroup) this.fragmentView).addView(this.storyPremiumHint, LayoutHelper.createFrame(-1, 240.0f, 87, 12.0f, 0.0f, 68.0f, 40.0f));
            this.storyPremiumHint.show();
            return;
        }
        HintView2 hintView3 = this.storyHint;
        if (hintView3 != null) {
            hintView3.hide();
        }
        StoriesController.StoryLimit storyLimitCheckStoryLimit = MessagesController.getInstance(this.currentAccount).getStoriesController().checkStoryLimit();
        if (storyLimitCheckStoryLimit != null && storyLimitCheckStoryLimit.active(this.currentAccount, 1)) {
            showDialog(new LimitReachedBottomSheet(this, getContext(), storyLimitCheckStoryLimit.getLimitReachedType(), this.currentAccount, null));
        } else {
            StoryRecorder.getInstance(getParentActivity(), this.currentAccount).closeToWhenSent(new StoryRecorder.ClosingViewProvider() { // from class: org.telegram.ui.DialogsActivity.53
                @Override // org.telegram.ui.Stories.recorder.StoryRecorder.ClosingViewProvider
                public void preLayout(long j, final Runnable runnable) {
                    DialogsActivity dialogsActivity = DialogsActivity.this;
                    if (dialogsActivity.dialogStoriesCell != null) {
                        dialogsActivity.scrollToTop(false, true);
                        DialogsActivity.this.invalidateScrollY = true;
                        DialogsActivity.this.fragmentView.invalidate();
                        if (j == 0 || j == DialogsActivity.this.getUserConfig().getClientUserId()) {
                            DialogsActivity.this.dialogStoriesCell.scrollToFirstCell();
                        } else {
                            DialogsActivity.this.dialogStoriesCell.scrollTo(j);
                        }
                        DialogsActivity.this.viewPages[0].listView.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.DialogsActivity.53.1
                            @Override // android.view.ViewTreeObserver.OnPreDrawListener
                            public boolean onPreDraw() {
                                DialogsActivity.this.viewPages[0].listView.getViewTreeObserver().removeOnPreDrawListener(this);
                                AndroidUtilities.runOnUIThread(runnable, 100L);
                                return false;
                            }
                        });
                        return;
                    }
                    runnable.run();
                }

                @Override // org.telegram.ui.Stories.recorder.StoryRecorder.ClosingViewProvider
                public StoryRecorder.SourceView getView(long j) {
                    DialogStoriesCell dialogStoriesCell = DialogsActivity.this.dialogStoriesCell;
                    return StoryRecorder.SourceView.fromStoryCell(dialogStoriesCell != null ? dialogStoriesCell.findStoryCell(j) : null);
                }
            }).open(null, true);
        }
    }

    public /* synthetic */ void lambda$openStoriesRecorder$159() {
        HintView2 hintView2 = this.storyPremiumHint;
        if (hintView2 != null) {
            hintView2.hide();
        }
        presentFragment(new PremiumPreviewFragment("stories"));
    }

    private void checkEmailConfig() {
        int iCheckEmailSuggestion = getMessagesController().checkEmailSuggestion();
        if (iCheckEmailSuggestion != 0) {
            presentFragment(new LoginActivity().changeEmail(new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda90
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$checkEmailConfig$160();
                }
            }, new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda91
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$checkEmailConfig$161();
                }
            }, iCheckEmailSuggestion == 2));
            getMessagesController().markEmailSuggestionAsShown();
        }
    }

    public /* synthetic */ void lambda$checkEmailConfig$160() {
        Bulletin.LottieLayout lottieLayout = new Bulletin.LottieLayout(getContext(), this.resourceProvider);
        lottieLayout.setAnimation(R.raw.email_check_inbox, new String[0]);
        lottieLayout.textView.setText(LocaleController.getString(R.string.YourLoginEmailChangedSuccess));
        Bulletin.make(this, lottieLayout, 2750).show();
        try {
            this.fragmentView.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
    }

    public /* synthetic */ void lambda$checkEmailConfig$161() {
        getMessagesController().removeSuggestion(0L, "SETUP_LOGIN_EMAIL");
    }

    private boolean isTouchInsideCurrentDialogsList(MotionEvent motionEvent) {
        ViewPage[] viewPageArr;
        ViewPage viewPage;
        DialogsRecyclerView dialogsRecyclerView;
        if (motionEvent != null && (viewPageArr = this.viewPages) != null && viewPageArr.length != 0 && (viewPage = viewPageArr[0]) != null && (dialogsRecyclerView = viewPage.listView) != null) {
            Rect rect = AndroidUtilities.rectTmp2;
            if (dialogsRecyclerView.getVisibility() == 0 && dialogsRecyclerView.getGlobalVisibleRect(rect)) {
                return rect.contains((int) motionEvent.getRawX(), (int) motionEvent.getRawY());
            }
        }
        return false;
    }

    @Override // org.telegram.ui.MainTabsActivity.TabFragmentDelegate
    public boolean canParentTabsSlide(MotionEvent motionEvent, boolean z) {
        RightSlidingDialogContainer rightSlidingDialogContainer;
        if (isParentPreviewActive() || this.searchIsShowed || ((rightSlidingDialogContainer = this.rightSlidingDialogContainer) != null && rightSlidingDialogContainer.hasFragment())) {
            return false;
        }
        View view = this.blurredView;
        if (view != null && view.getVisibility() == 0) {
            return false;
        }
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null && filterTabsView.isEditing()) {
            return false;
        }
        if (motionEvent != null && motionEvent.getY() < this.actionBar.getMeasuredHeight()) {
            return true;
        }
        FilterTabsView filterTabsView2 = this.filterTabsView;
        boolean z2 = filterTabsView2 == null || filterTabsView2.getTabsCount() < 2 || this.filterTabsView.getCurrentTabId() == this.filterTabsView.getFirstTabId();
        FilterTabsView filterTabsView3 = this.filterTabsView;
        boolean z3 = filterTabsView3 == null || filterTabsView3.getTabsCount() < 2 || this.filterTabsView.getCurrentTabId() == this.filterTabsView.getLastTabId();
        FilterTabsView filterTabsView4 = this.filterTabsView;
        boolean z4 = filterTabsView4 != null && filterTabsView4.getTabsCount() > 1;
        int chatSwipeAction = SharedConfig.getChatSwipeAction(this.currentAccount);
        if (z && z4 && z3 && !z2 && chatSwipeAction != 5 && isTouchInsideCurrentDialogsList(motionEvent)) {
            return false;
        }
        if (z) {
            return z3 && !z2;
        }
        return z2;
    }

    public void openArchivedChatsFromMainMenu() {
        Bundle bundle = new Bundle();
        bundle.putInt("folderId", 1);
        bundle.putBoolean("onlySelect", this.onlySelect);
        DialogsActivity dialogsActivity = new DialogsActivity(bundle);
        dialogsActivity.setDelegate(this.delegate);
        presentFragment(dialogsActivity, this.onlySelect);
    }

    private void showItemOptions() {
        boolean zIsCurrentThemeDark;
        final ItemOptions itemOptionsMakeOptions = ItemOptions.makeOptions((BaseFragment) this, (View) this.optionsItem, true);
        itemOptionsMakeOptions.setDimAlpha(8);
        itemOptionsMakeOptions.setSwipebackGravity(true, false);
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            safeLastFragment = this;
        }
        MainMenuHelper.MenuContext menuContextCreateMenuContext = MainMenuHelper.createMenuContext(this.currentAccount, this, new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda103
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.openArchivedChatsFromMainMenu();
            }
        }, MainMenuHelper.createPluginContextData(this.currentAccount, safeLastFragment));
        if (this.communityId != 0) {
            if (ChatObject.hasAdminRights(this.community)) {
                itemOptionsMakeOptions.add(R.drawable.msg_customize, LocaleController.getString(R.string.CommunityMenuSettings), new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda104
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$showItemOptions$162();
                    }
                });
                itemOptionsMakeOptions.addGap();
            }
            itemOptionsMakeOptions.addChecked(this.community.collapsed_in_dialogs, LocaleController.getString(R.string.CommunityMenuShowAsOneChat), new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda105
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$showItemOptions$163();
                }
            });
            itemOptionsMakeOptions.addChecked(true ^ this.community.collapsed_in_dialogs, LocaleController.getString(R.string.CommunityMenuShowAsSeparateChats), new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda106
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$showItemOptions$164();
                }
            });
            itemOptionsMakeOptions.show();
            itemOptionsMakeOptions.setTranslationY(-AndroidUtilities.dp(64.0f));
            return;
        }
        if (isArchive()) {
            itemOptionsMakeOptions.add(R.drawable.msg_customize, LocaleController.getString(R.string.ArchiveSettings), new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda107
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$showItemOptions$165();
                }
            });
            itemOptionsMakeOptions.add(R.drawable.msg_help, LocaleController.getString(R.string.HowDoesItWork), new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda108
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.showArchiveHelp();
                }
            });
            itemOptionsMakeOptions.show();
            itemOptionsMakeOptions.setTranslationY(-AndroidUtilities.dp(64.0f));
            return;
        }
        Theme.ResourcesProvider resourcesProvider = this.resourceProvider;
        if (resourcesProvider != null) {
            zIsCurrentThemeDark = resourcesProvider.isDark();
        } else {
            zIsCurrentThemeDark = Theme.isCurrentThemeDark();
        }
        itemOptionsMakeOptions.add(zIsCurrentThemeDark ? R.drawable.menu_day_mode_24 : R.drawable.menu_night_mode_24, LocaleController.getString(zIsCurrentThemeDark ? R.string.SwitchThemeToDay : R.string.SwitchThemeToNight), new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda109
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showItemOptions$167();
            }
        });
        itemOptionsMakeOptions.addGap();
        MainMenuHelper.addConfiguredItemOptions(itemOptionsMakeOptions, menuContextCreateMenuContext);
        ApplicationLoader applicationLoader = ApplicationLoader.applicationLoaderInstance;
        if (applicationLoader != null) {
            applicationLoader.addItemOptions(itemOptionsMakeOptions);
        }
        ActionBarMenuSubItem actionBarMenuSubItem = this.proxyMenuSubItem;
        if (actionBarMenuSubItem != null) {
            actionBarMenuSubItem.subtextView.setTextColor(getThemedColor(Theme.key_groupcreate_sectionText));
            this.proxyMenuSubItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda110
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$showItemOptions$168(itemOptionsMakeOptions, view);
                }
            });
            if (!SharedConfig.proxyList.isEmpty()) {
                itemOptionsMakeOptions.addGap();
                itemOptionsMakeOptions.add(this.proxyMenuSubItem);
                this.proxyDrawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon), PorterDuff.Mode.SRC_IN));
            }
        }
        itemOptionsMakeOptions.show();
        itemOptionsMakeOptions.setTranslationY(-AndroidUtilities.dp(64.0f));
    }

    public /* synthetic */ void lambda$showItemOptions$162() {
        Bundle bundle = new Bundle();
        bundle.putLong("community_id", this.communityId);
        presentFragment(new CommunityEditActivity(bundle));
    }

    public /* synthetic */ void lambda$showItemOptions$163() {
        if (this.community.collapsed_in_dialogs) {
            return;
        }
        getMessagesController().toggleCommunityCollapsedInDialogs(this.communityId, true);
        finishFragment();
    }

    public /* synthetic */ void lambda$showItemOptions$164() {
        if (this.community.collapsed_in_dialogs) {
            getMessagesController().toggleCommunityCollapsedInDialogs(this.communityId, false);
            finishFragment();
        }
    }

    public /* synthetic */ void lambda$showItemOptions$165() {
        presentFragment(new ArchiveSettingsActivity());
    }

    /* JADX WARN: Code duplicated, block: B:29:0x0073  */
    /* JADX WARN: Code duplicated, block: B:30:0x0078  */
    public /* synthetic */ void lambda$showItemOptions$167() {
        boolean zEquals;
        Theme.ThemeInfo theme;
        if (switchingTheme) {
            return;
        }
        switchingTheme = true;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
        String str = "Blue";
        String string = sharedPreferences.getString("lastDayTheme", "Blue");
        if (Theme.getTheme(string) == null || Theme.getTheme(string).isDark()) {
            string = "Blue";
        }
        String str2 = "Dark Blue";
        String string2 = sharedPreferences.getString("lastDarkTheme", "Dark Blue");
        if (Theme.getTheme(string2) == null || !Theme.getTheme(string2).isDark()) {
            string2 = "Dark Blue";
        }
        Theme.ThemeInfo activeTheme = Theme.getActiveTheme();
        if (string.equals(string2)) {
            if (activeTheme.isDark() || string.equals("Dark Blue") || string.equals("Night")) {
                str2 = string2;
            }
            zEquals = str.equals(activeTheme.getKey());
            if (zEquals) {
                theme = Theme.getTheme(str2);
            } else {
                theme = Theme.getTheme(str);
            }
            switchTheme(theme, zEquals);
            Theme.turnOffAutoNight(BulletinFactory.of(this), new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda148
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$showItemOptions$166();
                }
            });
        }
        str2 = string2;
        str = string;
        zEquals = str.equals(activeTheme.getKey());
        if (zEquals) {
            theme = Theme.getTheme(str2);
        } else {
            theme = Theme.getTheme(str);
        }
        switchTheme(theme, zEquals);
        Theme.turnOffAutoNight(BulletinFactory.of(this), new Runnable() { // from class: org.telegram.ui.DialogsActivity$$ExternalSyntheticLambda148
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showItemOptions$166();
            }
        });
    }

    public /* synthetic */ void lambda$showItemOptions$166() {
        presentFragment(new ThemeActivity(1));
    }

    public /* synthetic */ void lambda$showItemOptions$168(ItemOptions itemOptions, View view) {
        itemOptions.dismiss();
        presentFragment(new ProxyListActivity());
    }

    public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
        this.windowInsetsStateHolder.setInsets(windowInsetsCompat);
        this.statusBarHeight = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars()).top;
        this.navigationBarHeight = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
        int i = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.ime()).bottom;
        if (this.imeInsetHeight != i) {
            this.imeInsetHeight = i;
            this.fragmentView.requestLayout();
        }
        this.dialogsActivityStatusLayout.setPadding(0, this.statusBarHeight, 0, 0);
        updateFloatingButtonOffset();
        for (UndoView undoView : this.undoView) {
            if (undoView != null) {
                int currentUndoViewOffset = this.navigationBarHeight + getCurrentUndoViewOffset();
                ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) undoView.getLayoutParams();
                if (marginLayoutParams != null && marginLayoutParams.bottomMargin != currentUndoViewOffset) {
                    marginLayoutParams.bottomMargin = currentUndoViewOffset;
                    undoView.setLayoutParams(marginLayoutParams);
                }
            }
        }
        RightSlidingDialogContainer rightSlidingDialogContainer = this.rightSlidingDialogContainer;
        if (rightSlidingDialogContainer != null) {
            ViewCompat.dispatchApplyWindowInsets(rightSlidingDialogContainer, windowInsetsCompat);
        }
        return WindowInsetsCompat.CONSUMED;
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        View view;
        if (i == 1) {
            checkUi_menuItems();
            checkUi_searchFieldVisibility();
            checkUi_topPanelVisible();
            checkUi_filterTabsVisible();
            checkUi_searchFiltersVisibility();
            checkUi_searchFieldStyle();
            if (ExteraConfig.getHideDialogsSearchBar() && (view = this.fragmentView) != null) {
                view.requestLayout();
            }
            checkUi_communityAvatarImageVisibility();
            return;
        }
        if (i == 2) {
            checkUi_menuItems();
            checkUi_searchFieldVisibility();
            return;
        }
        if (i == 3) {
            checkUi_itemSpeedVisibility();
            return;
        }
        if (i == 4) {
            View view2 = this.fragmentView;
            if (view2 != null) {
                view2.invalidate();
                return;
            }
            return;
        }
        if (i == 5) {
            checkUi_itemSearchVisibility();
            return;
        }
        if (i == 6) {
            checkUi_menuItems();
            checkUi_searchFieldVisibility();
        } else {
            if (i == 7) {
                checkUi_forwardCommentFieldVisible();
                return;
            }
            if (i == 8) {
                checkUi_filterTabsVisible();
                checkUi_searchFiltersVisibility();
            } else if (i == 9) {
                checkUi_searchFiltersVisibility();
            }
        }
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChangeFinished(int i, float f, FactorAnimator factorAnimator) {
        ActionBarMenuItem actionBarMenuItem;
        if (i != 3 || (actionBarMenuItem = this.speedItem) == null) {
            return;
        }
        AnimatedVectorDrawable animatedVectorDrawable = (AnimatedVectorDrawable) actionBarMenuItem.getIconView().getDrawable();
        if (this.animatorSpeedButtonVisible.getValue()) {
            animatedVectorDrawable.start();
            if (SharedConfig.getDevicePerformanceClass() != 0) {
                TLRPC.TL_help_premiumPromo premiumPromo = MediaDataController.getInstance(this.currentAccount).getPremiumPromo();
                String strFeatureTypeToServerString = PremiumPreviewFragment.featureTypeToServerString(2);
                if (premiumPromo != null) {
                    int i2 = 0;
                    while (true) {
                        if (i2 >= premiumPromo.video_sections.size()) {
                            i2 = -1;
                            break;
                        } else if (premiumPromo.video_sections.get(i2).equals(strFeatureTypeToServerString)) {
                            break;
                        } else {
                            i2++;
                        }
                    }
                    if (i2 != -1) {
                        FileLoader.getInstance(this.currentAccount).loadFile(premiumPromo.videos.get(i2), premiumPromo, 3, 0);
                        return;
                    }
                    return;
                }
                return;
            }
            return;
        }
        animatedVectorDrawable.reset();
    }

    private void checkUi_communityAvatarImageVisibility() {
        float fNot = FBool.not(this.animatorSearchVisible.getFloatValue());
        BackupImageView backupImageView = this.communityAvatarImage;
        if (backupImageView != null) {
            backupImageView.setScaleX(fNot);
            this.communityAvatarImage.setScaleY(fNot);
            this.communityAvatarImage.setAlpha(fNot);
            this.communityAvatarImage.setVisibility(fNot > 0.0f ? 0 : 8);
        }
        if (this.addChatsToCommunityButton != null) {
            float fLerp = AndroidUtilities.lerp(0.9f, 1.0f, fNot);
            this.addChatsToCommunityButton.setScaleX(fLerp);
            this.addChatsToCommunityButton.setScaleY(fLerp);
            this.addChatsToCommunityButton.setAlpha(fNot);
            this.addChatsToCommunityButton.setVisibility(fNot > 0.0f ? 0 : 8);
            this.communityBottomFadeView.setAlpha(fNot);
            this.communityBottomFadeView.setVisibility(fNot > 0.0f ? 0 : 8);
        }
    }

    public void checkUi_searchFiltersVisibility() {
        if (this.searchTabsAndFiltersLayout != null) {
            float floatValue = (this.searchTabsView != null ? 1.0f : 0.0f) * this.animatorSearchVisible.getFloatValue();
            float fLerp = AndroidUtilities.lerp(0.98f, 1.0f, floatValue);
            this.searchTabsAndFiltersLayout.setScaleX(fLerp);
            this.searchTabsAndFiltersLayout.setScaleY(fLerp);
            this.searchTabsAndFiltersLayout.setAlpha(floatValue);
            this.searchTabsAndFiltersLayout.setVisibility(floatValue > 0.0f ? 0 : 8);
        }
        if (this.searchTabsView != null) {
            float floatValue2 = 1.0f - this.animatorSearchFilterTabsVisible.getFloatValue();
            this.searchTabsView.setAlpha(floatValue2);
            this.searchTabsView.setVisibility(floatValue2 > 0.0f ? 0 : 8);
        }
        if (this.filtersView != null) {
            float floatValue3 = this.animatorSearchFilterTabsVisible.getFloatValue();
            this.filtersView.setAlpha(floatValue3);
            this.filtersView.setVisibility(floatValue3 > 0.0f ? 0 : 8);
        }
    }

    private void checkUi_forwardCommentFieldVisible() {
        float floatValue = this.animatorForwardButtonVisible.getFloatValue();
        float fLerp = AndroidUtilities.lerp(0.2f, 1.0f, floatValue);
        ChatActivityEnterView.SendButton sendButton = this.writeButton;
        if (sendButton != null) {
            sendButton.setScaleX(fLerp);
            this.writeButton.setScaleY(fLerp);
            this.writeButton.setAlpha(floatValue);
            this.writeButton.setVisibility(floatValue > 0.0f ? 0 : 8);
        }
        ChatInputViewsContainer chatInputViewsContainer = this.chatInputViewsContainer;
        if (chatInputViewsContainer != null) {
            chatInputViewsContainer.setAlpha(floatValue);
            this.chatInputViewsContainer.setVisibility(floatValue > 0.0f ? 0 : 8);
            this.chatInputViewsContainer.getFadeView().setAlpha(floatValue);
            this.chatInputViewsContainer.getFadeView().setVisibility(floatValue > 0.0f ? 0 : 8);
        }
    }

    public void checkUi_topPanelVisible() {
        if (this.topPanelLayout != null) {
            float fLerp = AndroidUtilities.lerp(0.98f, 1.0f, 1.0f);
            this.topPanelLayout.setAlpha(1.0f);
            this.topPanelLayout.setScaleX(fLerp);
            this.topPanelLayout.setScaleY(fLerp);
            this.topPanelLayout.setVisibility(0);
        }
    }

    public void checkUi_searchPagesPaddings(boolean z) {
        if (this.searchViewPager == null || this.actionBar == null) {
            return;
        }
        int i = AndroidUtilities.navigationBarHeight;
        int iDp = AndroidUtilities.dp(this.ADDITIONAL_LIST_HEIGHT_DP) + this.actionBar.getMeasuredHeight() + (this.searchTabsView != null ? AndroidUtilities.dp(50.0f) : 0);
        DialogsActivityTopPanelLayout dialogsActivityTopPanelLayout = this.topPanelLayout;
        this.searchViewPager.setPagesPadding(iDp + (dialogsActivityTopPanelLayout != null ? (int) dialogsActivityTopPanelLayout.getAnimatedHeightWithPadding(AndroidUtilities.dp(7.0f)) : 0), i, z);
    }

    public float getFilterTabsVisibilityFactor(boolean z) {
        return (z ? 1.0f - this.animatorSearchVisible.getFloatValue() : 1.0f) * (1.0f - getRightSlidingProgress()) * this.animatorFilterTabsVisible.getFloatValue();
    }

    public void checkUi_filterTabsVisible() {
        ViewPage viewPage;
        float filterTabsVisibilityFactor = getFilterTabsVisibilityFactor(true);
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null) {
            boolean z = filterTabsView.getAlpha() != filterTabsVisibilityFactor;
            float fLerp = AndroidUtilities.lerp(0.98f, 1.0f, filterTabsVisibilityFactor);
            this.filterTabsView.setAlpha(filterTabsVisibilityFactor);
            this.filterTabsView.setScaleX(fLerp);
            this.filterTabsView.setScaleY(fLerp);
            this.filterTabsView.setVisibility(filterTabsVisibilityFactor > 0.0f ? 0 : 8);
            if (z && (viewPage = this.viewPages[0]) != null) {
                viewPage.listView.requestLayout();
            }
        }
        updateContextViewPosition();
    }

    public void checkUi_mainTabsVisible() {
        View view;
        boolean z = (!BottomNavigationBar.visible() || this.searching || this.mainTabsHiddenByScroll || isParentPreviewActive() || ((view = this.blurredView) != null && view.getBackground() != null && this.blurredView.getVisibility() != 8)) ? false : true;
        boolean z2 = this.animatorBottomTabsOffset.getValue() != z;
        this.animatorBottomTabsOffset.setValue(z, true);
        if (z2 && BottomNavigationBar.floating()) {
            checkUi_chatListViewPaddingsBottom();
        }
        MainTabsActivityController mainTabsActivityController = this.mainTabsActivityController;
        if (mainTabsActivityController != null) {
            mainTabsActivityController.setTabsVisible(z);
        }
    }

    private boolean isParentPreviewActive() {
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout != null) {
            return iNavigationLayout.isInPreviewMode() || this.parentLayout.isPreviewOpenAnimationInProgress();
        }
        return false;
    }

    public void checkUi_searchFieldVisibility() {
        if (this.fragmentSearchField == null) {
            return;
        }
        float fClamp = 1.0f - MathUtils.clamp(((-this.scrollYOffset) - getMaxScrollYOffsetWithoutSearch()) / AndroidUtilities.dp(48.0f), 0.0f, 1.0f);
        float fMax = Math.max(this.progressToActionMode, this.animatorActionModeVisible.getFloatValue());
        float floatValue = this.animatorSearchVisible.getFloatValue();
        float floatValue2 = (isSupportSearch() ? 1.0f : 0.0f) * (1.0f - fMax) * (1.0f - this.animatorDoneButtonVisible.getFloatValue());
        float fMax2 = Math.max(floatValue, fClamp * (1.0f - getRightSlidingProgress())) * floatValue2;
        if (ExteraConfig.getHideDialogsSearchBar() && !this.searching) {
            fMax2 = floatValue2 * floatValue;
        }
        this.fragmentSearchField.setAlpha(fMax2);
        this.fragmentSearchField.setVisibility(fMax2 > 0.0f ? 0 : 8);
        this.animatorSearchButtonVisible.setValue(fMax2 <= 0.01f, true);
    }

    public boolean shouldPassSearchFieldTouchToActionBar(MotionEvent motionEvent) {
        if (motionEvent != null && this.fragmentSearchField != null && this.actionBar != null && !this.animatorSearchVisible.getValue() && this.fragmentSearchField.getVisibility() == 0 && this.fragmentSearchField.getAlpha() > 0.01f) {
            float y = (this.actionBar.getY() + this.actionBar.getMeasuredHeight()) - this.fragmentSearchField.getY();
            if (y > 0.0f && motionEvent.getY() <= y) {
                return true;
            }
        }
        return false;
    }

    private void checkUi_searchFieldStyle() {
        FragmentSearchField fragmentSearchField = this.fragmentSearchField;
        if (fragmentSearchField == null) {
            return;
        }
        fragmentSearchField.setBlurredBackgroundVisibility(this.animatorSearchVisible.getFloatValue());
    }

    public void checkUi_searchFieldHint() {
        String string = LocaleController.getString(getRightSlidingProgress() > 0.5f ? R.string.SearchTopics : R.string.SearchChats);
        this.fragmentSearchField.editText.setContentDescription(string);
        this.fragmentSearchField.editText.setHint(string);
    }

    public void checkUi_menuItems() {
        checkUi_itemBackButtonVisibility();
        checkUi_itemOptionsVisibility();
        checkUi_itemDownloadsVisibility();
        checkUi_itemSpeedVisibility();
        checkUi_itemPasscodeVisibility();
        checkUi_itemSearchVisibility();
    }

    private void checkUi_itemBackButtonVisibility() {
        float fMax;
        if (this.actionBar == null) {
            return;
        }
        float floatValue = (1.0f - this.animatorSearchVisible.getFloatValue()) * (1.0f - getRightSlidingProgress()) * (1.0f - this.animatorDoneButtonVisible.getFloatValue());
        if (this.hasMainTabs && !ExteraConfig.getNavigationDrawer()) {
            fMax = (1.0f - this.progressToActionMode) * floatValue;
        } else {
            fMax = Math.max(this.progressToActionMode, floatValue);
        }
        FragmentFloatingButton.setAnimatedVisibility(this.actionBar.getBackButton(), fMax);
    }

    private void checkUi_itemOptionsVisibility() {
        float floatValue = 1.0f - this.animatorSearchVisible.getFloatValue();
        float rightSlidingProgress = 1.0f - getRightSlidingProgress();
        float floatValue2 = 1.0f - this.animatorDoneButtonVisible.getFloatValue();
        FragmentFloatingButton.setAnimatedVisibility(this.optionsItem, floatValue * rightSlidingProgress * floatValue2 * (ExteraConfig.getNavigationDrawer() ? 0.0f : 1.0f));
    }

    public void checkUi_itemPasscodeVisibility() {
        float f = SharedConfig.passcodeHash.isEmpty() ? 0.0f : 1.0f;
        FragmentFloatingButton.setAnimatedVisibility(this.passcodeItem, f * (1.0f - this.animatorSearchVisible.getFloatValue()) * (1.0f - getRightSlidingProgress()) * (1.0f - this.animatorDoneButtonVisible.getFloatValue()));
    }

    private void checkUi_itemDownloadsVisibility() {
        float f = this.downloadsItemVisible ? 1.0f : 0.0f;
        FragmentFloatingButton.setAnimatedVisibility(this.downloadsItem, f * (1.0f - this.animatorSearchVisible.getFloatValue()) * (1.0f - getRightSlidingProgress()) * (1.0f - this.animatorDoneButtonVisible.getFloatValue()));
    }

    private void checkUi_itemSpeedVisibility() {
        float floatValue = this.animatorSearchVisible.getFloatValue();
        float rightSlidingProgress = 1.0f - getRightSlidingProgress();
        FragmentFloatingButton.setAnimatedVisibility(this.speedItem, floatValue * rightSlidingProgress * (1.0f - this.animatorDoneButtonVisible.getFloatValue()) * this.animatorSpeedButtonVisible.getFloatValue());
    }

    private void checkUi_itemSearchVisibility() {
        float f = isSupportSearch() ? 1.0f : 0.0f;
        FragmentFloatingButton.setAnimatedVisibility(this.searchItem, f * this.animatorSearchButtonVisible.getFloatValue() * (1.0f - getRightSlidingProgress()) * (1.0f - this.animatorDoneButtonVisible.getFloatValue()));
        DialogStoriesCell dialogStoriesCell = this.dialogStoriesCell;
        if (dialogStoriesCell != null) {
            dialogStoriesCell.invalidate();
        }
    }

    private boolean isSupportSearch() {
        return ((isDrawerAccountPreview() && this.inPreviewMode) || this.initialDialogsType == 2) ? false : true;
    }

    public long getCommunityId() {
        return this.communityId;
    }

    /* JADX WARN: Code duplicated, block: B:50:0x00f6  */
    /* JADX WARN: Code duplicated, block: B:53:0x0111  */
    /* JADX WARN: Code duplicated, block: B:56:0x0129  */
    /* JADX WARN: Code duplicated, block: B:61:? A[RETURN, SYNTHETIC] */
    public void blur3_InvalidateBlur() {
        BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode;
        BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode2;
        if (Build.VERSION.SDK_INT < 31 || this.scrollableViewNoiseSuppressor == null || this.fragmentView == null || this.actionBar == null) {
            return;
        }
        int iDp = AndroidUtilities.dp(48.0f);
        int measuredHeight = this.actionBar.getMeasuredHeight() + AndroidUtilities.dp(48.0f) + AndroidUtilities.dp(this.hasStories ? 81.0f : 0.0f);
        FilterTabsView filterTabsView = this.filterTabsView;
        boolean z = false;
        int measuredHeight2 = measuredHeight + ((filterTabsView == null || filterTabsView.getVisibility() != 0) ? 0 : this.filterTabsView.getMeasuredHeight());
        DialogsActivityTopPanelLayout dialogsActivityTopPanelLayout = this.topPanelLayout;
        int sumHeightOfAllVisibleChild = measuredHeight2 + ((dialogsActivityTopPanelLayout == null || dialogsActivityTopPanelLayout.getVisibility() != 0) ? 0 : this.topPanelLayout.getSumHeightOfAllVisibleChild()) + ((int) this.scrollYOffset);
        int measuredHeight3 = this.actionBar.getMeasuredHeight();
        SearchTabsAndFiltersLayout searchTabsAndFiltersLayout = this.searchTabsAndFiltersLayout;
        this.iBlur3PositionActionBar.set(0.0f, -iDp, this.fragmentView.getMeasuredWidth(), AndroidUtilities.lerp(sumHeightOfAllVisibleChild, measuredHeight3 + (searchTabsAndFiltersLayout != null ? searchTabsAndFiltersLayout.getMeasuredHeight() : 0) + AndroidUtilities.dp(30.0f), this.animatorSearchVisible.getFloatValue()) + iDp);
        if (this.hasMainTabs) {
            MainTabsUiHelper.setBlurBounds(this.iBlur3PositionMainTabs, this.fragmentView, this.navigationBarHeight);
            this.iBlur3PositionMainTabs.inset(0.0f, LiteMode.isEnabled(262144) ? 0.0f : -AndroidUtilities.dp(48.0f));
        } else {
            if (this.commentView != null && this.chatInputViewsContainer != null) {
                this.iBlur3PositionMainTabs.set(0.0f, this.fragmentView.getMeasuredHeight() - calculateListViewPaddingBottom(), this.fragmentView.getMeasuredWidth(), this.fragmentView.getMeasuredHeight());
                this.iBlur3PositionMainTabs.inset(0.0f, LiteMode.isEnabled(262144) ? 0.0f : -AndroidUtilities.dp(48.0f));
            }
            this.scrollableViewNoiseSuppressor.setupRenderNodes(this.iBlur3Positions, z ? 2 : 1);
            this.scrollableViewNoiseSuppressor.invalidateResultRenderNodes(this.iBlur3Capture, this.fragmentView.getMeasuredWidth(), this.fragmentView.getMeasuredHeight());
            blurredBackgroundSourceRenderNode = this.iBlur3SourceGlassFrosted;
            if (blurredBackgroundSourceRenderNode != null) {
                blurredBackgroundSourceRenderNode.setSize(this.fragmentView.getMeasuredWidth(), this.fragmentView.getMeasuredHeight());
                this.iBlur3SourceGlassFrosted.updateDisplayListIfNeeded();
            }
            blurredBackgroundSourceRenderNode2 = this.iBlur3SourceGlass;
            if (blurredBackgroundSourceRenderNode2 != null) {
                blurredBackgroundSourceRenderNode2.setSize(this.fragmentView.getMeasuredWidth(), this.fragmentView.getMeasuredHeight());
                this.iBlur3SourceGlass.updateDisplayListIfNeeded();
            }
        }
        z = true;
        this.scrollableViewNoiseSuppressor.setupRenderNodes(this.iBlur3Positions, z ? 2 : 1);
        this.scrollableViewNoiseSuppressor.invalidateResultRenderNodes(this.iBlur3Capture, this.fragmentView.getMeasuredWidth(), this.fragmentView.getMeasuredHeight());
        blurredBackgroundSourceRenderNode = this.iBlur3SourceGlassFrosted;
        if (blurredBackgroundSourceRenderNode != null) {
            blurredBackgroundSourceRenderNode.setSize(this.fragmentView.getMeasuredWidth(), this.fragmentView.getMeasuredHeight());
            this.iBlur3SourceGlassFrosted.updateDisplayListIfNeeded();
        }
        blurredBackgroundSourceRenderNode2 = this.iBlur3SourceGlass;
        if (blurredBackgroundSourceRenderNode2 != null) {
            blurredBackgroundSourceRenderNode2.setSize(this.fragmentView.getMeasuredWidth(), this.fragmentView.getMeasuredHeight());
            this.iBlur3SourceGlass.updateDisplayListIfNeeded();
        }
    }

    public int calculateListViewPaddingBottom() {
        int listViewFloatingTabsPadding;
        if (this.commentView != null) {
            return (int) (this.windowInsetsStateHolder.getAnimatedMaxBottomInset() + AndroidUtilities.dp(9.0f) + this.chatInputViewsContainer.getInputBubbleHeight() + AndroidUtilities.dp(7.0f) + AndroidUtilities.dp(2.0f));
        }
        long j = this.communityId;
        int i = this.navigationBarHeight;
        if (j != 0) {
            listViewFloatingTabsPadding = AndroidUtilities.dp(72.0f);
        } else {
            i += this.additionNavigationBarHeight;
            listViewFloatingTabsPadding = getListViewFloatingTabsPadding();
        }
        return i + listViewFloatingTabsPadding;
    }

    private int getListViewFloatingTabsPadding() {
        if (this.commentView == null) {
            return MainTabsUiHelper.getFloatingTabsPadding(this.hasMainTabs);
        }
        return 0;
    }

    public int calculateBulletinBottomOffset() {
        int iCalculateListViewPaddingBottom = calculateListViewPaddingBottom();
        return BottomNavigationBar.floating() ? iCalculateListViewPaddingBottom + (getCurrentUndoViewOffset() - getListViewFloatingTabsPadding()) : iCalculateListViewPaddingBottom;
    }

    @Override // org.telegram.ui.MainTabsActivity.TabFragmentDelegate
    public BlurredBackgroundSourceRenderNode getGlassSource() {
        return this.iBlur3SourceGlass;
    }

    @Override // org.telegram.ui.MainTabsActivity.TabFragmentDelegate
    public void onParentScrollToTop() {
        ViewPage viewPage;
        FilterTabsView filterTabsView;
        ViewPage[] viewPageArr = this.viewPages;
        if (viewPageArr != null && (viewPage = viewPageArr[0]) != null) {
            int i = (viewPage.dialogsType == 0 && hasHiddenArchive() && this.viewPages[0].archivePullViewState == 2) ? 1 : 0;
            if (this.viewPages[0].layoutManager != null && this.viewPages[0].layoutManager.findFirstVisibleItemPosition() <= i && (filterTabsView = this.filterTabsView) != null && filterTabsView.getVisibility() == 0 && !this.filterTabsView.isFirstTabSelected()) {
                this.filterTabsView.selectFirstTab();
                return;
            }
        }
        scrollToTop(true, true);
    }

    private void switchTheme(Theme.ThemeInfo themeInfo, boolean z) {
        ActionBarMenuItem actionBarMenuItem = this.optionsItem;
        if (actionBarMenuItem == null) {
            return;
        }
        int[] iArr = new int[2];
        actionBarMenuItem.getLocationInWindow(iArr);
        iArr[0] = iArr[0] + (this.optionsItem.getIconView().getMeasuredWidth() / 2);
        iArr[1] = iArr[1] + (this.optionsItem.getIconView().getMeasuredHeight() / 2);
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needSetDayNightTheme, themeInfo, Boolean.FALSE, iArr, -1, Boolean.valueOf(z), null, null, null, Boolean.TRUE);
    }

    private boolean shouldUseDrawerActionBarLayout() {
        return ExteraConfig.getNavigationDrawer() && !this.onlySelect && this.initialDialogsType == 0 && this.folderId == 0 && this.searchString == null;
    }

    private float getDialogStoriesMenuItemsOffset() {
        if (isArchive() || shouldUseDrawerActionBarLayout()) {
            return AndroidUtilities.dp(68.0f);
        }
        return AndroidUtilities.dpf2(16.66f);
    }

    private void updateDrawerButton() {
        DialogStoriesCell dialogStoriesCell = this.dialogStoriesCell;
        if (dialogStoriesCell != null) {
            dialogStoriesCell.setMenuItemsOffset(getDialogStoriesMenuItemsOffset());
        }
        if (this.actionBar != null && !this.onlySelect && this.initialDialogsType == 0 && this.folderId == 0 && this.searchString == null) {
            if (ExteraConfig.getNavigationDrawer()) {
                if (this.menuDrawable == null) {
                    MenuDrawable menuDrawable = new MenuDrawable();
                    this.menuDrawable = menuDrawable;
                    menuDrawable.setRotateToBack(false);
                }
                this.actionBar.setBackButtonDrawable(this.menuDrawable);
                this.actionBar.setBackButtonContentDescription(LocaleController.getString(R.string.AccDescrOpenMenu));
            } else {
                if (this.actionBar.getBackButton() != null && (this.actionBar.getBackButton().getDrawable() instanceof MenuDrawable)) {
                    this.actionBar.setBackButtonDrawable(null);
                }
                this.menuDrawable = null;
            }
            checkUi_itemOptionsVisibility();
            checkUi_itemBackButtonVisibility();
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.refreshTitlePosition(true);
            }
        }
    }

    public boolean canOpenDrawer() {
        ActionBar actionBar;
        RightSlidingDialogContainer rightSlidingDialogContainer;
        if (!ExteraConfig.getNavigationDrawer() || this.onlySelect || this.communityId != 0 || this.folderId != 0 || this.initialDialogsType != 0 || this.searching || (((actionBar = this.actionBar) != null && actionBar.isActionModeShowed()) || this.animatorSearchVisible.getValue() || this.searchAnimator != null || (((rightSlidingDialogContainer = this.rightSlidingDialogContainer) != null && (rightSlidingDialogContainer.hasFragment() || this.rightFragmentTransitionInProgress)) || this.tabsAnimationInProgress || this.startedTracking || this.maybeStartTracking))) {
            return false;
        }
        FilterTabsView filterTabsView = this.filterTabsView;
        if (filterTabsView != null && (filterTabsView.isEditing() || this.filterTabsView.isAnimatingIndicator())) {
            return false;
        }
        FilterTabsView filterTabsView2 = this.filterTabsView;
        if (filterTabsView2 == null) {
            return true;
        }
        filterTabsView2.getVisibility();
        return true;
    }

    public boolean canOpenDrawerBySwipe(MotionEvent motionEvent) {
        FilterTabsView filterTabsView;
        View view;
        if (motionEvent != null && this.hasMainTabs && BottomNavigationBar.visible() && !this.mainTabsHiddenByScroll && (view = this.fragmentView) != null) {
            Rect rect = AndroidUtilities.rectTmp2;
            if (view.getGlobalVisibleRect(rect)) {
                int rawY = ((int) motionEvent.getRawY()) - rect.top;
                int measuredHeight = this.fragmentView.getMeasuredHeight() - this.navigationBarHeight;
                if (rawY >= measuredHeight - AndroidUtilities.dp(MainTabsUiHelper.getTabsViewHeightDp()) && rawY <= measuredHeight) {
                    return false;
                }
            }
        }
        return canOpenDrawer() && ((filterTabsView = this.filterTabsView) == null || filterTabsView.getVisibility() != 0 || SharedConfig.getChatSwipeAction(this.currentAccount) != 5 || this.filterTabsView.getCurrentTabId() == this.filterTabsView.getFirstTabId());
    }

    public DrawerContainer drawerContainer() {
        if (getParentActivity() instanceof LaunchActivity) {
            return ((LaunchActivity) getParentActivity()).drawerLayoutContainer.getDrawerContainer();
        }
        return null;
    }

    public float getTopPanelAnimatedHeight() {
        return getTopPanelAnimatedHeight(AndroidUtilities.dp(14.0f));
    }

    public float getTopPanelAnimatedHeight(int i) {
        DialogsActivityTopPanelLayout dialogsActivityTopPanelLayout = this.topPanelLayout;
        if (dialogsActivityTopPanelLayout != null) {
            return dialogsActivityTopPanelLayout.getAnimatedHeightWithPadding(i);
        }
        return 0.0f;
    }

    public float getTopPanelVisibility() {
        DialogsActivityTopPanelLayout dialogsActivityTopPanelLayout = this.topPanelLayout;
        if (dialogsActivityTopPanelLayout != null) {
            return dialogsActivityTopPanelLayout.getMetadata().getTotalVisibility();
        }
        return 0.0f;
    }

    public void checkInsets() {
        ChatInputViewsContainer chatInputViewsContainer = this.chatInputViewsContainer;
        if (chatInputViewsContainer != null) {
            chatInputViewsContainer.checkInsets();
        }
        checkUi_chatListViewPaddingsBottom();
        blur3_InvalidateBlur();
        checkUi_fadeView();
        ChatActivityEnterView.SendButton sendButton = this.writeButton;
        if (sendButton != null) {
            sendButton.setTranslationY(-this.windowInsetsStateHolder.getAnimatedMaxBottomInset());
        }
    }

    public void checkUi_fadeView() {
        ChatInputViewsContainer chatInputViewsContainer = this.chatInputViewsContainer;
        if (chatInputViewsContainer != null) {
            chatInputViewsContainer.setBlurredBottomHeight(this.windowInsetsStateHolder.getAnimatedMaxBottomInset() + AndroidUtilities.dp(9.0f) + this.chatInputViewsContainer.getInputBubbleHeight() + AndroidUtilities.dp(7.0f));
        }
    }

    public void checkUi_chatListViewPaddingsBottom() {
        if (this.viewPages == null) {
            return;
        }
        int iCalculateListViewPaddingBottom = calculateListViewPaddingBottom();
        int i = 0;
        while (true) {
            ViewPage[] viewPageArr = this.viewPages;
            if (i >= viewPageArr.length) {
                return;
            }
            ViewPage viewPage = viewPageArr[i];
            if (viewPage != null) {
                DialogsRecyclerView dialogsRecyclerView = viewPage.listView;
                dialogsRecyclerView.setPadding(0, dialogsRecyclerView.topPadding, 0, iCalculateListViewPaddingBottom);
            }
            i++;
        }
    }

    public void drawHeaderShadow(Canvas canvas, int i) {
        INavigationLayout iNavigationLayout;
        if (this.parentLayout == null || this.actionBar == null) {
            return;
        }
        float fMax = Math.max(this.animatorShadowVisible.getFloatValue(), getRightSlidingProgress());
        float f = this.searchAnimationProgress;
        float f2 = 1.0f;
        float f3 = fMax * (1.0f - f) * (1.0f - f);
        if (f3 == 0.0f) {
            return;
        }
        if (-1 >= i) {
            f2 = 0.0f;
            i = -1;
        }
        if (f2 <= 0.0f || f3 <= 0.0f || i <= 0 || (iNavigationLayout = this.parentLayout) == null) {
            return;
        }
        iNavigationLayout.drawHeaderShadow(canvas, (int) (f2 * 255.0f * f3), i);
    }
}
