package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ConfigurationInfo;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.URLSpan;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.webkit.CookieManager;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Keep;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.mediarouter.media.PlatformMediaRouter1RouteProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.api.ApiController;
import com.exteragram.messenger.api.db.ExteraDatabase;
import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.api.dto.NowPlayingDTO;
import com.exteragram.messenger.api.dto.RegDateDTO;
import com.exteragram.messenger.badges.BadgesController;
import com.exteragram.messenger.config.BottomNavigationBar;
import com.exteragram.messenger.nowplaying.NowPlayingController;
import com.exteragram.messenger.nowplaying.ui.components.NowPlayingCard;
import com.exteragram.messenger.nowplaying.ui.components.NowPlayingCardData;
import com.exteragram.messenger.pillstack.ui.pills.crypto.RatePill$$ExternalSyntheticLambda1;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.plugins.hooks.MenuItemRecord;
import com.exteragram.messenger.plugins.ui.PluginsActivity;
import com.exteragram.messenger.plugins.ui.components.PluginsMenuWrapper;
import com.exteragram.messenger.plugins.utils.MenuContextBuilder;
import com.exteragram.messenger.preferences.MainPreferencesActivity;
import com.exteragram.messenger.preferences.utils.SettingsRegistry;
import com.exteragram.messenger.regdate.RegDateController;
import com.exteragram.messenger.speech.ui.RecognitionModelDialogs$1$$ExternalSyntheticLambda0;
import com.exteragram.messenger.utils.AppUtils;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.exteragram.messenger.utils.network.RemoteUtils;
import com.exteragram.messenger.utils.system.SystemUtils;
import com.exteragram.messenger.utils.system.VibratorUtils;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import com.exteragram.messenger.utils.ui.ChatHeaderUiHelper;
import com.exteragram.messenger.utils.ui.MainTabsUiHelper;
import com.exteragram.messenger.utils.ui.UIUtil;
import com.google.android.gms.cast.MediaError;
import com.google.android.material.timepicker.TimeModel;
import j$.time.Duration;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import kotlin.time.DurationKt;
import kotlinx.coroutines.Job;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import okhttp3.internal.http.HttpStatusCodesKt;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.AuthTokensHelper;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FlagSecureReason;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LanguageDetector;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotchInfoUtils;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.NotificationsSettingsFacade;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.TranslateController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.tgnet.tl.TL_fragment;
import org.telegram.tgnet.tl.TL_iv;
import org.telegram.tgnet.tl.TL_payments;
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
import org.telegram.ui.ActionBar.OKLCH;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Business.OpeningHoursActivity;
import org.telegram.ui.Business.ProfileHoursCell;
import org.telegram.ui.Business.ProfileLocationCell;
import org.telegram.ui.Cells.AboutLinkCell;
import org.telegram.ui.Cells.AnimatedStatusView;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.DividerCell;
import org.telegram.ui.Cells.GraySectionCell;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.ProfileChannelCell;
import org.telegram.ui.Cells.SettingsSearchCell;
import org.telegram.ui.Cells.SettingsSuggestionCell;
import org.telegram.ui.Cells.ShadowSectionCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextDetailCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Cells.UserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.AutoDeletePopupWrapper;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackButtonMenu;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CanvasButton;
import org.telegram.ui.Components.ChatActivityInterface;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.DotDividerSpan;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.EmptyStubSpan;
import org.telegram.ui.Components.FiltersListBottomSheet;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.HintsController;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.InstantCameraView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.MediaActivity;
import org.telegram.ui.Components.MessageContainsEmojiButton;
import org.telegram.ui.Components.MessagePrivateSeenView;
import org.telegram.ui.Components.Paint.PersistColorPalette;
import org.telegram.ui.Components.Premium.LimitPreviewView;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet;
import org.telegram.ui.Components.Premium.ProfilePremiumCell;
import org.telegram.ui.Components.Premium.boosts.UserSelectorBottomSheet;
import org.telegram.ui.Components.ProfileActionsView;
import org.telegram.ui.Components.ProfileGalleryBlurView;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.ProfileGooeyView;
import org.telegram.ui.Components.ProfileMusicView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ReplaceableIconDrawable;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StarRatingView;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.TagEditCell;
import org.telegram.ui.Components.TimerDrawable;
import org.telegram.ui.Components.TranslateAlert2;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.VectorAvatarThumbDrawable;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.DownscaleScrollableNoiseSuppressor;
import org.telegram.ui.Components.blur3.ViewGroupPartRenderer;
import org.telegram.ui.Components.blur3.capture.IBlur3Capture;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProviderThemed;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceColor;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.Gifts.ProfileGiftsContainer;
import org.telegram.ui.Stars.BotStarsActivity;
import org.telegram.ui.Stars.BotStarsController;
import org.telegram.ui.Stars.ProfileGiftsView;
import org.telegram.ui.Stars.StarGiftPatterns;
import org.telegram.ui.Stars.StarGiftSheet;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.ProfileStoriesView;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesListPlaceProvider;
import org.telegram.ui.Stories.StoryViewer;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.Stories.recorder.DualCameraView;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.TON.TONIntroActivity;
import org.telegram.ui.bots.AffiliateProgramFragment;
import org.telegram.ui.bots.BotBiometry;
import org.telegram.ui.bots.BotDownloads;
import org.telegram.ui.bots.BotLocation;
import org.telegram.ui.bots.BotWebViewAttachedSheet;
import org.telegram.ui.bots.ChannelAffiliateProgramsFragment;
import org.telegram.ui.bots.SetupEmojiStatusSheet;
import org.telegram.ui.community.CommunityArrowDrawable;
import org.telegram.ui.community.CommunitySheet;
import org.telegram.ui.community.cells.CommunityLinkView;

public class ProfileActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate, SharedMediaLayout.SharedMediaPreloaderDelegate, ImageUpdater.ImageUpdaterDelegate, SharedMediaLayout.Delegate, MainTabsActivity.TabFragmentDelegate {
    private static final long NOW_PLAYING_UPDATE_INTERVAL = Duration.ofSeconds(15).toMillis();
    private Property<ActionBar, Float> ACTIONBAR_HEADER_PROGRESS;
    private final Property<ProfileActivity, Float> HEADER_SHADOW;
    private AboutLinkCell aboutLinkCell;
    private int actionBarAnimationColorFrom;
    private int actionBarBackgroundColor;
    private Paint actionBarBackgroundPaint;
    private ProfileActionsView actionsView;
    private final SparseIntArray adaptedColors;
    private int addMemberRow;
    private int addToContactsRow;
    private int addToGroupButtonRow;
    private int addToGroupInfoRow;
    private int additionFloatingButtonOffset;
    private int additionNavigationBarHeight;
    private int administratorsRow;
    private int affiliateRow;
    private boolean allowProfileAnimation;
    private boolean allowPullingDown;
    private AnimatedStatusView animatedStatusView;
    private ActionBarMenuItem animatingItem;
    private final BoolAnimator animatorBottomButtonVisibility;
    private final BoolAnimator animatorBottomTabsOffset;
    private Runnable applyBulletin;
    private ActionBarMenuSubItem autoDeleteItem;
    TimerDrawable autoDeleteItemDrawable;
    AutoDeletePopupWrapper autoDeletePopupWrapper;
    private TLRPC.FileLocation avatar;
    private AnimatorSet avatarAnimation;
    private float avatarAnimationProgress;
    private TLRPC.FileLocation avatarBig;
    private int avatarColor;
    private FrameLayout avatarContainer;
    private FrameLayout avatarContainer2;
    private AvatarDrawable avatarDrawable;
    private ProfileGooeyView avatarGooey;
    private AvatarImageView avatarImage;
    private RadialProgressView avatarProgressView;
    private float avatarScale;
    int avatarUploadingRequest;
    private float avatarX;
    private float avatarY;
    private ProfileGalleryBlurView avatarsBlurView;
    private ProfileGalleryView avatarsViewPager;
    private PagerIndicatorView avatarsViewPagerIndicatorView;
    private float[] backwardInitialValues;
    private float backwardTransitionFromExtraHeight;
    private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] badgeDrawable;
    private int balanceDividerRow;
    private long banFromGroup;
    private int bioRow;
    private ProfileBirthdayEffect birthdayEffect;
    public ProfileBirthdayEffect.BirthdayEffectFetcher birthdayFetcher;
    public int birthdayRow;
    private int bizHoursRow;
    private int bizLocationRow;
    private int blockedUsersRow;
    private View blurredView;
    private int botAppRow;
    private BotBiometry botBiometry;
    private TL_bots.BotInfo botInfo;
    private BotLocation botLocation;

    @Keep
    private int botPermissionBiometry;

    @Keep
    private int botPermissionEmojiStatus;
    private int botPermissionEmojiStatusReqId;

    @Keep
    private int botPermissionLocation;
    private int botPermissionsDivider;
    private int botPermissionsHeader;
    private int botStarsBalanceRow;
    private int botTonBalanceRow;
    private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] botVerificationDrawable;
    private ButtonWithCounterView[] bottomButton;
    private ButtonWithCounterView bottomButton2;
    private FrameLayout bottomButton2Container;
    private FrameLayout[] bottomButtonContainer;
    private SpannableStringBuilder bottomButtonPostText;
    private SpannableStringBuilder bottomButtonPostTextAlbum;
    private FrameLayout bottomButtonsContainer;
    private int bottomPaddingRow;
    private int businessRow;
    private ActionBarMenuItem callItem;
    private boolean callItemVisible;
    private ImageView callToActionItem;
    private RLottieDrawable cameraDrawable;
    private boolean canSearchMembers;
    private RLottieDrawable cellCameraDrawable;
    private int channelBalanceRow;
    private int channelBalanceSectionRow;
    private int channelDividerRow;
    private int channelInfoRow;
    private int channelRow;
    private long chatId;
    private TLRPC.ChatFull chatInfo;
    private int chatRow;
    private int clearLogsRow;
    private HintView2 collectibleHint;
    private int collectibleHintBackgroundColor;
    private Boolean collectibleHintVisible;
    private TLRPC.TL_emojiStatusCollectible collectibleStatus;
    private CommunityArrowDrawable communityArrowDrawable;
    private ImageView communityItem;
    private NestedFrameLayout contentView;
    public boolean createdBirthdayFetcher;
    private boolean creatingChat;
    private HintView2 creationDateHint;
    private ReplaceableIconDrawable creationDateIcon;
    private CharSequence currentBio;
    private TLRPC.ChannelParticipant currentChannelParticipant;
    private TLRPC.Chat currentChat;
    private TLRPC.EncryptedChat currentEncryptedChat;
    private float currentExpanAnimatorFracture;
    private float currentExpandAnimatorValue;
    private TL_account.TL_password currentPassword;
    private float customAvatarProgress;
    private float customPhotoOffset;
    private int dataRow;
    private int debugHeaderRow;
    private int deleteReactionRow;
    private int devicesRow;
    private int devicesSectionRow;
    private long dialogId;
    private boolean disableProfileAnimation;
    private boolean doNotSetForeground;
    private ActionBarMenuSubItem editColorItem;
    private ActionBarMenuItem editItem;
    private boolean editItemVisible;
    private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable[] emojiStatusDrawable;
    private Long emojiStatusGiftId;
    private int emptyRow;
    private int emptyRow2;
    private StickerEmptyView emptyView;
    private ValueAnimator expandAnimator;
    private float[] expandAnimatorValues;
    float expandNameYStartedFrom;
    float expandOnlineYStartedFrom;
    private boolean expandPhoto;
    private float expandProgress;
    private int exteraRow;
    private float extraHeight;
    private ImageReceiver fallbackImage;
    private int filtersRow;
    private boolean firstLayout;
    private FlagSecureReason flagSecure;
    private boolean fragmentOpened;
    private boolean fragmentViewAttached;
    private boolean fullyVisible;
    private HintView fwdRestrictedHint;
    public ProfileGiftsView giftsView;
    private int graceSuggestionRow;
    private int graceSuggestionSectionRow;
    private boolean hasCustomPhoto;
    private boolean hasFallbackPhoto;
    public boolean hasMainTabs;
    private boolean hasMusic;
    private boolean hasVoiceChatItem;
    private AnimatorSet headerAnimatorSet;
    protected float headerShadowAlpha;
    private AnimatorSet headerShadowAnimatorSet;
    private int helpHeaderRow;
    private int helpSectionCell;
    private boolean hoursExpanded;
    private boolean hoursShownMine;
    private IBlur3Capture iBlur3Capture;
    private final BlurredBackgroundDrawableViewFactory iBlur3FactoryLiquidGlass;
    private boolean iBlur3Invalidated;
    private final RectF iBlur3PositionActionBar;
    private final RectF iBlur3PositionMainTabs;
    private final ArrayList<RectF> iBlur3Positions;
    private final BlurredBackgroundSourceColor iBlur3SourceColor;
    private final BlurredBackgroundSourceRenderNode iBlur3SourceGlass;
    private int idDcRow;
    private boolean ignoreScrollOnFullExpand;
    private ImageUpdater imageUpdater;
    private int infoAffiliateRow;
    private int infoEndRow;
    private int infoEndRowEmpty;
    private int infoHeaderRow;
    private int infoHeaderRowEmpty;
    private int infoSectionRow;
    private int infoStartRow;
    private float initialAnimationExtraHeight;
    public int initialStoryAlbum;
    private boolean invalidateScroll;
    private boolean isBot;
    private boolean isCallAvailable;
    public boolean isFragmentOpened;
    private boolean isFragmentPhoneNumber;
    private boolean isInLandscapeMode;
    private boolean isLoadingIconSet;
    private boolean[] isOnline;
    private boolean isPulledDown;
    private boolean isStarRatingVisible1;
    private boolean isTopic;
    private int joinRow;
    private boolean justFullyExpanded;
    private int languageRow;
    private float lastEmojiStatusProgress;
    private int lastMeasuredContentHeight;
    private int lastMeasuredContentWidth;
    private float lastOnlineTextViewX;
    private float lastOnlineTextViewY;
    private float lastRatingViewTranslationXOffset;
    private float lastRatingViewTranslationYOffset;
    private int lastSectionRow;
    private boolean lastStoriesIsInAlbum;
    private int lastStoriesSelectedCount;
    private LinearLayoutManager layoutManager;
    private ActionBarMenuSubItem linkItem;
    private int linkedCommunityDividerRow;
    private int linkedCommunityRow;
    private ListAdapter listAdapter;
    private int listContentHeight;
    private RecyclerListView listView;
    private float listViewVelocityY;
    private int liteModeRow;
    private CircularProgressDrawable loadingDrawable;
    private CharacterStyle loadingSpan;
    private boolean loadingUsers;
    private int locationRow;
    private Drawable lockIconDrawable;
    private MainTabsActivityController mainTabsActivityController;
    private boolean mainTabsHiddenByScroll;
    private AudioPlayerAlert.ClippingTextViewSwitcher mediaCounterTextView;
    private float mediaHeaderAnimationProgress;
    private boolean mediaHeaderVisible;
    private int membersEndRow;
    private int membersHeaderRow;
    private int membersSectionRow;
    private int membersStartRow;
    private long mergeDialogId;
    private ProfileMusicView musicView;
    public boolean myProfile;
    private SimpleTextView[] nameTextView;
    private String nameTextViewRightDrawable2ContentDescription;
    private String nameTextViewRightDrawableContentDescription;
    private float nameX;
    private float nameY;
    private int navigationBarAnimationColorFrom;
    private int navigationBarHeight;
    private boolean needSendMessage;
    private boolean needStarImage;
    private boolean needTimerImage;
    private int noteRow;
    private int notificationRow;
    HashSet<Integer> notificationsExceptionTopics;
    private int notificationsRow;
    private int notificationsSimpleRow;
    private NowPlayingCardData nowPlayingCardData;
    private final Handler nowPlayingHandler;
    private Job nowPlayingJob;
    private boolean nowPlayingLoading;
    private int nowPlayingRow;
    private Runnable nowPlayingRunnable;
    private int nowPlayingSectionRow;
    private int numberRow;
    private int numberSectionRow;
    private int onlineCount;
    private SimpleTextView[] onlineTextView;
    private float onlineX;
    private float onlineY;
    private boolean openAnimationInProgress;
    public boolean openCommonChats;
    public boolean openGifts;
    public int openGiftsCollection;
    public boolean openGiftsUpgradable;
    private boolean openSimilar;
    private boolean openedGifts;
    private boolean openingAvatar;
    private ActionBarMenuItem otherItem;
    private int overlayCountVisible;
    private OverlaysView overlaysView;
    private LongSparseArray<TLRPC.ChatParticipant> participantsMap;
    private int passwordSuggestionRow;
    private int passwordSuggestionSectionRow;
    private MessagesController.PeerColor peerColor;
    private int phoneRow;
    private int phoneSuggestionRow;
    private int phoneSuggestionSectionRow;
    float photoDescriptionProgress;
    PinchToZoomHelper pinchToZoomHelper;
    private int playProfileAnimation;
    private ActionBarMenuSubItem pluginsMenuItem;
    private PluginsMenuWrapper pluginsMenuWrapper;
    private int policyRow;
    private HashMap<Integer, Integer> positionToOffset;
    private boolean preloadedChannelEmojiStatuses;
    private final CrossfadeDrawable[] premiumCrossfadeDrawable;
    private int premiumGiftingRow;
    private int premiumRow;
    private int premiumSectionsRow;
    private final Drawable[] premiumStarDrawable;
    private ImageLocation prevLoadedImageLocation;
    private final ChatHeaderUiHelper.ProfileTransitionState previousChatAvatarTransition;
    ChatActivityInterface previousTransitionFragment;
    BaseFragment previousTransitionMainFragment;
    private int privacyRow;
    public ProfileChannelCell.ChannelMessageFetcher profileChannelMessageFetcher;
    private MenuContextBuilder profileMenuContextData;
    boolean profileTransitionInProgress;
    private PhotoViewer.PhotoViewerProvider provider;
    private float pullUpProgress;
    private int questionRow;
    private StarRatingView ratingView;
    private boolean recreateMenuAfterAnimation;
    private Rect rect;
    private int reportDividerRow;
    private long reportReactionFromDialogId;
    private int reportReactionMessageId;
    private int reportReactionRow;
    private int reportRow;
    private boolean reportSpam;
    private Animator.AnimatorListener resetListener;
    private Theme.ResourcesProvider resourcesProvider;
    private int rowCount;
    public boolean saved;
    private MessagesController.SavedMusicList savedMusicList;
    int savedScrollOffset;
    int savedScrollPosition;
    boolean savedScrollToSharedMedia;
    private ScamDrawable scamDrawable;
    private AnimatorSet scrimAnimatorSet;
    private Paint scrimPaint;
    private View scrimView;
    private final DownscaleScrollableNoiseSuppressor scrollableViewNoiseSuppressor;
    private boolean scrolling;
    private SearchAdapter searchAdapter;
    private ActionBarMenuItem searchItem;
    private RecyclerListView searchListView;
    private boolean searchMode;
    private int searchTransitionOffset;
    private float searchTransitionProgress;
    private Animator searchViewTransition;
    private int secretSettingsSectionRow;
    private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;
    private long selectedUser;
    private int sendLastLogsRow;
    private int sendLogsRow;
    private int sendMessageRow;
    private TextCell setAvatarCell;
    private int setAvatarRow;
    private int setAvatarSectionRow;
    private final Runnable setLoadingIcon;
    private ActionBarMenuSubItem setUsernameItem;
    private int setUsernameRow;
    private int settingsKeyRow;
    private int settingsRow;
    private int settingsSectionRow;
    private int settingsSectionRow2;
    private int settingsTimerRow;
    public SharedMediaLayout sharedMediaLayout;
    private boolean sharedMediaLayoutAttached;
    private SharedMediaLayout.SharedMediaPreloader sharedMediaPreloader;
    private int sharedMediaRow;
    private boolean showAddToContacts;
    private ShowDrawable showStatusButton;
    private ArrayList<Integer> sortedUsers;
    private ImageView starBgItem;
    private ImageView starFgItem;
    private int starsRow;
    private int stickersRow;
    private ProfileStoriesView storyView;
    private int subscribersRequestsRow;
    private int subscribersRow;
    private int switchBackendRow;
    private ImageView timeItem;
    private TimerDrawable timerDrawable;
    private float titleAnimationsYDiff;
    private int tonRow;
    private TopView topView;
    private long topicId;
    private boolean transitionAnimationInProress;
    private int transitionIndex;
    private View transitionOnlineText;
    private ImageView ttlIconView;
    private int unblockRow;
    private UndoView undoView;
    private int unofficialSecurityRiskDividerRow;
    private int unofficialSecurityRiskRow;
    private int updateRemoteConfigRow;
    private ImageLocation uploadingImageLocation;
    private boolean userBlocked;
    private long userId;
    private TLRPC.UserFull userInfo;
    private int userInfoRow;
    private int usernameRow;
    private boolean usersEndReached;
    private int usersForceShowingIn;
    private String vcardFirstName;
    private String vcardLastName;
    private String vcardPhone;
    private final Drawable[] verifiedCheckDrawable;
    private final CrossfadeDrawable[] verifiedCrossfadeDrawable;
    private final Drawable[] verifiedDrawable;
    private int versionRow;
    private ActionBarMenuItem videoCallItem;
    private boolean videoCallItemVisible;
    private ViewPositionWatcher viewPositionWatcher;
    private final ArrayList<TLRPC.ChatParticipant> visibleChatParticipants;
    private final ArrayList<Integer> visibleSortedUsers;
    private final Paint whitePaint;
    private RLottieImageView writeButton;
    private AnimatorSet writeButtonAnimation;

    public static void lambda$checkNowPlaying$1() {
        if (getParentActivity() == null) {
            return;
        }
        this.nowPlayingCardData = null;
        updateListAnimated(false);
    }

    public boolean lambda$setParentLayout$11() {
        return this.currentEncryptedChat != null || isPeerNoForwards();
    }

    void lambda$onItemClick$0(TLRPC.User user, AlertDialog alertDialog, int i) {
            ArrayList<TLRPC.User> arrayList = new ArrayList<>();
            arrayList.add(user);
            ProfileActivity.this.getContactsController().deleteContact(arrayList, true);
            if (user != null) {
                user.contact = false;
                ProfileActivity.this.updateListAnimated(false);
            }
        }

        public void lambda$onItemClick$5(final long j, final DialogsActivity dialogsActivity, final boolean z, final TLRPC.TL_chatAdminRights tL_chatAdminRights, final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ProfileActivity$7$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onItemClick$4(j, tL_chatAdminRights, str, z, dialogsActivity);
                }
            });
        }

        public void lambda$createView$24(Context context, long j, TL_payments.connectedBotStarRef connectedbotstarref) {
        int i = this.currentAccount;
        if (connectedbotstarref == null) {
            ChannelAffiliateProgramsFragment.showConnectAffiliateAlert(context, i, this.userInfo.starref_program, getUserConfig().getClientUserId(), this.resourcesProvider, false);
        } else {
            ChannelAffiliateProgramsFragment.showShareAffiliateAlert(context, i, connectedbotstarref, j, this.resourcesProvider);
        }
    }

    public void lambda$onItemClick$1(TLObject tLObject, TLRPC.TL_error tL_error) {
            TLRPC.TL_help_dismissSuggestion tL_help_dismissSuggestion = new TLRPC.TL_help_dismissSuggestion();
            tL_help_dismissSuggestion.suggestion = "VALIDATE_PASSWORD";
            tL_help_dismissSuggestion.peer = new TLRPC.TL_inputPeerEmpty();
            ProfileActivity.this.getConnectionsManager().sendRequest(tL_help_dismissSuggestion, new RequestDelegate() { // from class: org.telegram.ui.ProfileActivity$15$$ExternalSyntheticLambda5
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error2) {
                    this.f$0.lambda$onItemClick$0(tLObject2, tL_error2);
                }
            });
        }

        public void lambda$createView$41(TLRPC.InputStickerSet inputStickerSet) {
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(inputStickerSet);
        showDialog(new EmojiPacksAlert(this, getParentActivity(), this.resourcesProvider, arrayList));
    }

    public void lambda$toggleNoForwards$50(boolean z, Integer num, TLRPC.TL_error tL_error) {
        if (finishFragmentIfPreviousIsChatActivity()) {
            return;
        }
        if (BulletinFactory.canShowBulletin(this)) {
            if (num.intValue() == 1) {
                BulletinFactory.createDissableSharingBulletin(this, null, z).show();
            } else if (num.intValue() == 2) {
                BulletinFactory.createDissableSharingBulletin(this, DialogObject.getShortName(this.userId), z).show();
            } else if (tL_error != null) {
                BulletinFactory.showError(tL_error);
            }
        }
        FlagSecureReason flagSecureReason = this.flagSecure;
        if (flagSecureReason != null) {
            flagSecureReason.invalidate();
        }
    }

    public void stopTabsReorder() {
        this.sharedMediaLayout.scrollSlidingTextTabStrip.setReordering(false);
        this.sharedMediaLayout.sendTabsOrder();
        this.sharedMediaLayout.updateTabs(true);
        this.bottomButton2Container.animate().translationY(AndroidUtilities.dp(69.0f)).setDuration(180L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).withEndAction(new Runnable() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda66
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$stopTabsReorder$51();
            }
        }).start();
        BulletinFactory.of(this).createSimpleBulletin(R.raw.contact_check, "Tab order changed.").show();
    }

    public void lambda$onBlockContactClicked$54(int i) {
        if (i == 1) {
            NotificationCenter notificationCenter = getNotificationCenter();
            int i2 = NotificationCenter.closeChats;
            notificationCenter.removeObserver(this, i2);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i2, new Object[0]);
            this.playProfileAnimation = 0;
            finishFragment();
            return;
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.peerSettingsDidLoad, Long.valueOf(this.userId));
    }

    public void lambda$onJoinClicked$59(boolean z, boolean[] zArr) {
        if (!z || this.joinRow != -1) {
            updateRowsIds();
            ListAdapter listAdapter = this.listAdapter;
            if (listAdapter != null) {
                listAdapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        ProfileActionsView profileActionsView = this.actionsView;
        if (profileActionsView != null) {
            profileActionsView.stopLoading(7);
            if (zArr[0]) {
                this.actionsView.beginApplyingActions();
                this.actionsView.set(7, false);
                this.actionsView.set(9, true);
                this.actionsView.commitActions();
            }
        }
    }

    public void lambda$onMemberClick$63(TLRPC.ChannelParticipant channelParticipant, TLRPC.User user, TLRPC.ChatParticipant chatParticipant, boolean z, String str, Integer num) {
        if (channelParticipant != null) {
            openRightsEdit(num.intValue(), user, chatParticipant, channelParticipant.admin_rights, channelParticipant.banned_rights, channelParticipant.rank, z);
        } else {
            openRightsEdit(num.intValue(), user, chatParticipant, null, null, str, z);
        }
    }

    public void lambda$onSend$0(LongSparseArray longSparseArray, int i) {
            BulletinFactory.createInviteSentBulletin(ProfileActivity.this.getParentActivity(), ProfileActivity.this.contentView, longSparseArray.size(), longSparseArray.size() == 1 ? ((TLRPC.Dialog) longSparseArray.valueAt(0)).id : 0L, i, getThemedColor(Theme.key_undo_background), getThemedColor(Theme.key_undo_infoColor)).show();
        }
    }

    public Boolean lambda$processOnClickOrPress$86(URLSpan uRLSpan) {
        if (uRLSpan != null) {
            openUrl(uRLSpan.getURL(), null);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }

    public void lambda$leaveChatPressed$92(boolean z) {
        this.playProfileAnimation = 0;
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i, new Object[0]);
        finishFragment();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needDeleteDialog, Long.valueOf(-this.currentChat.id), null, this.currentChat, Boolean.valueOf(z));
    }

    public void getChannelParticipants(boolean z) {
        LongSparseArray<TLRPC.ChatParticipant> longSparseArray;
        if (this.loadingUsers || (longSparseArray = this.participantsMap) == null || this.chatInfo == null) {
            return;
        }
        this.loadingUsers = true;
        final int i = (longSparseArray.size() == 0 || !z) ? 0 : 300;
        final TLRPC.TL_channels_getParticipants tL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
        tL_channels_getParticipants.channel = getMessagesController().getInputChannel(this.chatId);
        tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsRecent();
        tL_channels_getParticipants.offset = z ? 0 : this.participantsMap.size();
        tL_channels_getParticipants.limit = 200;
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_channels_getParticipants, new RequestDelegate() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda73
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$getChannelParticipants$95(tL_channels_getParticipants, i, tLObject, tL_error);
            }
        }), this.classGuid);
    }

    public void lambda$didReceivedNotification$101(Object[] objArr) {
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.closeChats;
        notificationCenter.removeObserver(this, i);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i, new Object[0]);
        TLRPC.EncryptedChat encryptedChat = (TLRPC.EncryptedChat) objArr[0];
        Bundle bundle = new Bundle();
        bundle.putInt("enc_id", encryptedChat.id);
        presentFragment(new ChatActivity(bundle), true);
    }

    public void lambda$createActionBarMenu$124(View view) {
        this.pluginsMenuItem.openSwipeBack();
    }

    public void lambda$onYesClick$0(int i) {
                NotificationCenter notificationCenter = ProfileActivity.this.getNotificationCenter();
                ProfileActivity profileActivity = ProfileActivity.this;
                int i2 = NotificationCenter.newSuggestionsAvailable;
                notificationCenter.removeObserver(profileActivity, i2);
                ListAdapter listAdapter = ListAdapter.this;
                if (i == 2) {
                    ProfileActivity.this.getMessagesController().removeSuggestion(0L, "PREMIUM_GRACE");
                    Browser.openUrl(getContext(), ProfileActivity.this.getMessagesController().premiumManageSubscriptionUrl);
                } else {
                    ProfileActivity.this.getMessagesController().removeSuggestion(0L, i == 0 ? "VALIDATE_PHONE_NUMBER" : "VALIDATE_PASSWORD");
                }
                ProfileActivity.this.getNotificationCenter().addObserver(ProfileActivity.this, i2);
                ProfileActivity.this.updateListAnimated(false);
            }

            @Override // org.telegram.ui.Cells.SettingsSuggestionCell
            public void onNoClick(int i) {
                ListAdapter listAdapter = ListAdapter.this;
                if (i == 0) {
                    ProfileActivity.this.presentFragment(new ActionIntroActivity(3));
                } else {
                    ProfileActivity.this.presentFragment(new TwoStepVerificationSetupActivity(8, null));
                }
            }
        }

        public void lambda$editRow$166(final TLRPC.UserFull userFull, TL_account.TL_birthday tL_birthday) {
        TL_account.updateBirthday updatebirthday = new TL_account.updateBirthday();
        updatebirthday.flags |= 1;
        updatebirthday.birthday = tL_birthday;
        final TL_account.TL_birthday tL_birthday2 = userFull != null ? userFull.birthday : null;
        if (userFull != null) {
            userFull.flags2 |= 32;
            userFull.birthday = tL_birthday;
        }
        getMessagesController().invalidateContentSettings();
        getConnectionsManager().sendRequest(updatebirthday, new RequestDelegate() { // from class: org.telegram.ui.ProfileActivity$$ExternalSyntheticLambda175
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$editRow$165(userFull, tL_birthday2, tLObject, tL_error);
            }
        }, 1024);
    }

    public void m17856$r8$lambda$9gCTTJt3GVHoB53JCVAaZmR4XU(LinkSpanDrawable.LinksTextView[] linksTextViewArr, Boolean bool) {
        ViewPropertyAnimator viewPropertyAnimatorScaleY = linksTextViewArr[0].animate().alpha(bool.booleanValue() ? 0.0f : 1.0f).scaleX(bool.booleanValue() ? 0.8f : 1.0f).scaleY(bool.booleanValue() ? 0.8f : 1.0f);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        viewPropertyAnimatorScaleY.setInterpolator(cubicBezierInterpolator).setDuration(600L).start();
        linksTextViewArr[1].animate().alpha(bool.booleanValue() ? 1.0f : 0.0f).scaleX(!bool.booleanValue() ? 0.8f : 1.0f).scaleY(bool.booleanValue() ? 1.0f : 0.8f).setInterpolator(cubicBezierInterpolator).setDuration(600L).start();
    }

    public static /* synthetic */ void $r8$lambda$1a95ly1AjU0HatPbPRNOnmhpa5Y(LimitPreviewView limitPreviewView, TLRPC.UserFull userFull, Utilities.Callback callback) {
        limitPreviewView.animateStarRating(userFull.stars_rating, userFull.stars_my_pending_rating);
        callback.run(Boolean.TRUE);
    }

    public static /* synthetic */ void $r8$lambda$CD6AUrgfcjJUSlEmlnEnQGD1Qgk(LimitPreviewView limitPreviewView, TLRPC.UserFull userFull, Utilities.Callback callback, View view) {
        limitPreviewView.animateStarRating(userFull.stars_rating, userFull.stars_my_pending_rating);
        callback.run(Boolean.TRUE);
    }

    public static /* synthetic */ void m17882$r8$lambda$gvlUpiLmGekveeA2dLkLdPJaCc(LimitPreviewView limitPreviewView, TLRPC.UserFull userFull, Utilities.Callback callback) {
        limitPreviewView.animateStarRating(userFull.stars_my_pending_rating, userFull.stars_rating);
        callback.run(Boolean.FALSE);
    }

    public static /* synthetic */ void m17860$r8$lambda$FTs9O_vaNwrapIkCsnIPKPro1k(LimitPreviewView limitPreviewView, TLRPC.UserFull userFull, Utilities.Callback callback, View view) {
        limitPreviewView.animateStarRating(userFull.stars_my_pending_rating, userFull.stars_rating);
        callback.run(Boolean.FALSE);
    }

    private static CharSequence createNewSpan(String str, int i) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        FilterCreateActivity.NewSpan newSpan = new FilterCreateActivity.NewSpan(false, 9) { // from class: org.telegram.ui.ProfileActivity.58
            @Override // org.telegram.ui.FilterCreateActivity.NewSpan, android.text.style.ReplacementSpan
            public void draw(Canvas canvas, CharSequence charSequence, int i2, int i3, float f, int i4, int i5, int i6, Paint paint) {
                canvas.save();
                canvas.translate(AndroidUtilities.dp(2.0f), 0.0f);
                super.draw(canvas, charSequence, i2, i3, f, i4, i5, i6, paint);
                canvas.restore();
            }
        };
        newSpan.setText(str);
        newSpan.setColor(i);
        spannableStringBuilder.setSpan(newSpan, 0, spannableStringBuilder.length(), 0);
        return spannableStringBuilder;
    }

    public boolean isPeerNoForwards() {
        if (this.currentChat != null) {
            return getMessagesController().isChatNoForwards(this.currentChat);
        }
        return getMessagesController().isUserNoForwards(this.userInfo);
    }

    public void onSavedMusicClick() {
        boolean z;
        TLRPC.Document document;
        if (this.savedMusicList == null) {
            if (MediaController.getInstance().currentSavedMusicList != null && MediaController.getInstance().currentSavedMusicList.currentAccount == this.currentAccount && MediaController.getInstance().currentSavedMusicList.dialogId == getDialogId()) {
                this.savedMusicList = MediaController.getInstance().currentSavedMusicList;
            } else {
                MessagesController.SavedMusicList savedMusicList = new MessagesController.SavedMusicList(this.currentAccount, getDialogId());
                this.savedMusicList = savedMusicList;
                TLRPC.UserFull userFull = this.userInfo;
                if (userFull != null && (document = userFull.saved_music) != null) {
                    savedMusicList.setup(document);
                }
            }
        }
        if (this.savedMusicList.list.isEmpty()) {
            return;
        }
        if (MediaController.getInstance().currentSavedMusicList == this.savedMusicList && MediaController.getInstance().isPlayingMessage(this.savedMusicList.list.get(0))) {
            z = true;
        } else {
            MediaController.getInstance().cleanup();
            z = false;
        }
        MediaController.getInstance().currentSavedMusicList = this.savedMusicList;
        MediaController.getInstance().getPlaylist().clear();
        MediaController.getInstance().getPlaylist().addAll(this.savedMusicList.list);
        if (!z) {
            MediaController.getInstance().playMessage(this.savedMusicList.list.get(0));
        }
        showDialog(new AudioPlayerAlert(getContext(), getResourceProvider()));
    }

    public void updateMainTabsVisibility() {
        boolean z = false;
        this.animatorBottomTabsOffset.setValue(BottomNavigationBar.visible() && !this.mainTabsHiddenByScroll, true);
        MainTabsActivityController mainTabsActivityController = this.mainTabsActivityController;
        if (mainTabsActivityController != null) {
            if (BottomNavigationBar.visible() && !this.mainTabsHiddenByScroll) {
                z = true;
            }
            mainTabsActivityController.setTabsVisible(z);
        }
    }

    public int getCurrentBottomTabsOffset() {
        return Math.round(this.additionFloatingButtonOffset * this.animatorBottomTabsOffset.getFloatValue());
    }

    private void applyBottomTabsOffset() {
        ProfileGiftsContainer profileGiftsContainer;
        FrameLayout frameLayout = this.bottomButtonsContainer;
        if (frameLayout != null) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
            int currentBottomTabsOffset = this.navigationBarHeight + getCurrentBottomTabsOffset();
            if (marginLayoutParams != null && marginLayoutParams.bottomMargin != currentBottomTabsOffset) {
                marginLayoutParams.bottomMargin = currentBottomTabsOffset;
                this.bottomButtonsContainer.setLayoutParams(marginLayoutParams);
            }
        }
        SharedMediaLayout sharedMediaLayout = this.sharedMediaLayout;
        if (sharedMediaLayout != null && (profileGiftsContainer = sharedMediaLayout.giftsContainer) != null) {
            profileGiftsContainer.setButtonOffset(this.navigationBarHeight + getCurrentBottomTabsOffset());
        }
        Bulletin visibleBulletin = Bulletin.getVisibleBulletin();
        if (visibleBulletin != null) {
            visibleBulletin.updatePosition();
        }
    }

    public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
        this.navigationBarHeight = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
        FrameLayout frameLayout = this.bottomButtonsContainer;
        if (frameLayout != null) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) frameLayout.getLayoutParams();
            int currentBottomTabsOffset = this.navigationBarHeight + getCurrentBottomTabsOffset();
            if (marginLayoutParams != null && marginLayoutParams.bottomMargin != currentBottomTabsOffset) {
                marginLayoutParams.bottomMargin = currentBottomTabsOffset;
                this.bottomButtonsContainer.setLayoutParams(marginLayoutParams);
            }
        }
        if (this.sharedMediaLayout != null) {
            this.sharedMediaLayout.setPagesPaddingBottom(this.navigationBarHeight + this.additionNavigationBarHeight + MainTabsUiHelper.getFloatingTabsPadding(this.hasMainTabs));
            ProfileGiftsContainer profileGiftsContainer = this.sharedMediaLayout.giftsContainer;
            if (profileGiftsContainer != null) {
                profileGiftsContainer.setButtonOffset(this.navigationBarHeight + getCurrentBottomTabsOffset());
            }
        }
        return WindowInsetsCompat.CONSUMED;
    }

    public static class Button2 extends FrameLayout {
        public Button2(Context context) {
            super(context);
            setPadding(AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f));
        }
    }

    public void blur3_InvalidateBlur() {
        if (Build.VERSION.SDK_INT < 31 || this.scrollableViewNoiseSuppressor == null) {
            return;
        }
        int iDp = AndroidUtilities.dp(48.0f);
        this.iBlur3PositionActionBar.set(0.0f, -iDp, this.fragmentView.getMeasuredWidth(), this.actionBar.getMeasuredHeight() + iDp);
        MainTabsUiHelper.setBlurBounds(this.iBlur3PositionMainTabs, this.fragmentView, this.navigationBarHeight);
        this.iBlur3PositionMainTabs.inset(0.0f, LiteMode.isEnabled(262144) ? 0.0f : -AndroidUtilities.dp(48.0f));
        this.scrollableViewNoiseSuppressor.setupRenderNodes(this.iBlur3Positions, 2);
        this.scrollableViewNoiseSuppressor.invalidateResultRenderNodes(this.iBlur3Capture, this.fragmentView.getMeasuredWidth(), this.fragmentView.getMeasuredHeight());
    }

    @Override // org.telegram.ui.MainTabsActivity.TabFragmentDelegate
    public BlurredBackgroundSourceRenderNode getGlassSource() {
        return this.iBlur3SourceGlass;
    }

    @Override // org.telegram.ui.MainTabsActivity.TabFragmentDelegate
    public void onParentScrollToTop() {
        this.listView.smoothScrollToPosition(0);
    }

    public final class TextView2 extends TextView implements Theme.Colorable {
        public TextView2(Context context) {
            super(context);
            updateColors();
        }

        @Override // android.widget.TextView, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), i2);
        }

        @Override // org.telegram.ui.ActionBar.Theme.Colorable
        public void updateColors() {
            setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, ProfileActivity.this.resourcesProvider));
        }
    }
}
