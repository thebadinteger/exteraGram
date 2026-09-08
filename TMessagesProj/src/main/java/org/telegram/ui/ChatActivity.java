package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.Parcelable;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.URLSpan;
import android.util.Pair;
import android.util.Property;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Space;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.math.MathUtils;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.ChatListItemAnimator;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.GridLayoutManagerFixed;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.ai.AiConfig;
import com.exteragram.messenger.ai.AiController;
import com.exteragram.messenger.ai.network.Client;
import com.exteragram.messenger.ai.ui.AiResponseAlert;
import com.exteragram.messenger.ai.ui.GenerateFromMessageBottomSheet;
import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.backup.BackupBottomSheet;
import com.exteragram.messenger.backup.PreferencesUtils;
import com.exteragram.messenger.badges.BadgesController;
import com.exteragram.messenger.camera.InstantCameraZoomSlider;
import com.exteragram.messenger.components.ActionRow;
import com.exteragram.messenger.components.MessageDetailsPopupWrapper;
import com.exteragram.messenger.config.BottomNavigationBar;
import com.exteragram.messenger.export.ui.ExportMapper$$ExternalSyntheticLambda2;
import com.exteragram.messenger.feed.FeedChannelActions;
import com.exteragram.messenger.feed.FeedChatIntegration;
import com.exteragram.messenger.feed.FeedController;
import com.exteragram.messenger.feed.FeedMessageUtils;
import com.exteragram.messenger.forward.ForwardContext;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.pillstack.ui.pills.crypto.RatePill$$ExternalSyntheticLambda1;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.plugins.hooks.MenuItemRecord;
import com.exteragram.messenger.plugins.ui.PluginsActivity;
import com.exteragram.messenger.plugins.ui.components.PluginsMenuWrapper;
import com.exteragram.messenger.plugins.utils.MenuContextBuilder;
import com.exteragram.messenger.proxy.IpAddressInfoController;
import com.exteragram.messenger.speech.VoiceRecognitionController;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.exteragram.messenger.utils.chats.DoubleTapUtils;
import com.exteragram.messenger.utils.chats.GlassMenuHelper;
import com.exteragram.messenger.utils.system.SystemUtils;
import com.exteragram.messenger.utils.system.VibratorUtils;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import com.exteragram.messenger.utils.ui.ChatHeaderUiHelper;
import com.exteragram.messenger.utils.ui.MainTabsUiHelper;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.util.Consumer;
import com.google.android.gms.cast.MediaError;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.material.timepicker.TimeModel;
import de.robv.android.xposed.callbacks.XCallback;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Stack;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import kotlin.jvm.internal.LongCompanionObject;
import kotlin.time.DurationKt;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import me.vkryl.core.BitwiseUtils;
import me.vkryl.core.reference.ReferenceList;
import okhttp3.Call;
import okhttp3.internal.url._UrlKt;
import org.mvel2.MVEL;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AiTonesController$$ExternalSyntheticLambda0;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BotForumHelper;
import org.telegram.messenger.BotInlineKeyboard;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChannelBoostsController;
import org.telegram.messenger.ChatMessageSharedResources;
import org.telegram.messenger.ChatMessagesMetadataController;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ChatThemeController;
import org.telegram.messenger.CodeHighlighting;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FactCheckController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FlagSecureReason;
import org.telegram.messenger.HashtagSearchController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LanguageDetector;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagePreviewParams;
import org.telegram.messenger.MessageSuggestionParams;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.RichMessageLayout;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.Timer;
import org.telegram.messenger.TopicsController;
import org.telegram.messenger.TranslateController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.utils.FBool;
import org.telegram.messenger.utils.OnPostDrawView;
import org.telegram.messenger.utils.PhotoUtilities;
import org.telegram.messenger.utils.RectFMergeBounding;
import org.telegram.messenger.utils.ViewOutlineProviderImpl;
import org.telegram.messenger.utils.tlutils.AmountUtils$Amount;
import org.telegram.messenger.utils.tlutils.AmountUtils$Currency;
import org.telegram.messenger.utils.tlutils.TlUtils;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.messenger.wallpaper.WallpaperBitmapHolder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.tgnet.tl.TL_iv;
import org.telegram.tgnet.tl.TL_phone;
import org.telegram.tgnet.tl.TL_stats;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.ActionBar.theme.ThemeKey;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Adapters.MentionsAdapter;
import org.telegram.ui.Adapters.MessagesSearchAdapter;
import org.telegram.ui.Business.BusinessBotButton;
import org.telegram.ui.Business.BusinessLinksActivity;
import org.telegram.ui.Business.BusinessLinksController;
import org.telegram.ui.Business.BusinessLinksEmptyView;
import org.telegram.ui.Business.QuickRepliesActivity;
import org.telegram.ui.Business.QuickRepliesController;
import org.telegram.ui.Business.QuickRepliesEmptyView;
import org.telegram.ui.Cells.BaseCell;
import org.telegram.ui.Cells.BotAskCell;
import org.telegram.ui.Cells.BotHelpCell;
import org.telegram.ui.Cells.BotSwitchCell;
import org.telegram.ui.Cells.ChannelRecommendationsCell;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.DialogCell;
import org.telegram.ui.Cells.IMessageCell;
import org.telegram.ui.Cells.MentionCell;
import org.telegram.ui.Cells.ProfileChannelCell;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Cells.UserInfoCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AttachBotIntroTopView;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.AutoDeletePopupWrapper;
import org.telegram.ui.Components.BackButtonMenu;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BluredView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatActivityEnterTopView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatActivityInterface;
import org.telegram.ui.Components.ChatActivityTopPanelLayout;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertDocumentLayout;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ChatBigEmptyView;
import org.telegram.ui.Components.ChatGreetingsView;
import org.telegram.ui.Components.ChatNotificationsPopupWrapper;
import org.telegram.ui.Components.ChatReplyContainer;
import org.telegram.ui.Components.ChatScrimPopupContainerLayout;
import org.telegram.ui.Components.ChatSearchTabs;
import org.telegram.ui.Components.ChatThemeBottomSheet;
import org.telegram.ui.Components.ChecksHintView;
import org.telegram.ui.Components.CircularProgressDrawable;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CrossfadeDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.DeleteMessagesBottomSheet;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.FireworksOverlay;
import org.telegram.ui.Components.FlickerLoadingView;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugProvider;
import org.telegram.ui.Components.FormattedDateSpan;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.FragmentContextView;
import org.telegram.ui.Components.GigagroupConvertAlert;
import org.telegram.ui.Components.HashtagActivity;
import org.telegram.ui.Components.HashtagHistoryView;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.HintsController;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.ImportingAlert;
import org.telegram.ui.Components.InstantCameraView;
import org.telegram.ui.Components.InviteMembersBottomSheet;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.MarkdownParser;
import org.telegram.ui.Components.MediaActivity;
import org.telegram.ui.Components.MentionsContainerView;
import org.telegram.ui.Components.MessageBackgroundDrawable;
import org.telegram.ui.Components.MessageContainsEmojiButton;
import org.telegram.ui.Components.MessagePreviewView;
import org.telegram.ui.Components.MessagePrivateSeenView;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.NumberTextView;
import org.telegram.ui.Components.PhonebookShareAlert;
import org.telegram.ui.Components.PinnedLineView;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.PollVotesAlert;
import org.telegram.ui.Components.PopupSwipeBackLayout;
import org.telegram.ui.Components.Premium.GiftPremiumBottomSheet$GiftTier;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.PremiumPreviewBottomSheet;
import org.telegram.ui.Components.Premium.boosts.BoostDialogs;
import org.telegram.ui.Components.Premium.boosts.GiftInfoBottomSheet;
import org.telegram.ui.Components.Premium.boosts.PremiumPreviewGiftLinkBottomSheet;
import org.telegram.ui.Components.QuoteSpan;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.ReactedHeaderView;
import org.telegram.ui.Components.ReactedUsersListView;
import org.telegram.ui.Components.ReactionTabHolderView;
import org.telegram.ui.Components.Reactions.ChatSelectionReactionMenuOverlay;
import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.ReactionsContainerLayout;
import org.telegram.ui.Components.RectOld;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.ScrimOptions;
import org.telegram.ui.Components.SearchTagsList;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickerEmptyView;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.SuggestEmojiView;
import org.telegram.ui.Components.TagEditCell;
import org.telegram.ui.Components.TextSelectionHint;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.ThanosEffect;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.Components.TopicSeparator;
import org.telegram.ui.Components.TopicsTabsView;
import org.telegram.ui.Components.TranscribeButton;
import org.telegram.ui.Components.TranslateAlert2;
import org.telegram.ui.Components.TranslateButton;
import org.telegram.ui.Components.TrendingStickersAlert;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.UnreadCounterTextView;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.ViewHelper;
import org.telegram.ui.Components.ViewPagerFixed;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.DownscaleScrollableNoiseSuppressor;
import org.telegram.ui.Components.blur3.capture.IBlur3Capture;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProviderThemed;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSource;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceBitmap;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceWrapped;
import org.telegram.ui.Components.blur3.utils.Blur3Utils;
import org.telegram.ui.Components.chat.ChatActivityBottomViewsVisibilityController;
import org.telegram.ui.Components.chat.ChatActivityDraftMessageMeasureController;
import org.telegram.ui.Components.chat.ChatActivityMessageMetricsView;
import org.telegram.ui.Components.chat.ChatActivitySearchContainer;
import org.telegram.ui.Components.chat.ChatInputViewsContainer;
import org.telegram.ui.Components.chat.ChatListViewPaddingsAnimator;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Components.chat.WallpaperBitmapProvider;
import org.telegram.ui.Components.chat.layouts.ButtonOnClickListener;
import org.telegram.ui.Components.chat.layouts.ButtonOnLongClickListener;
import org.telegram.ui.Components.chat.layouts.ChatActivityActionsButtonsLayout;
import org.telegram.ui.Components.chat.layouts.ChatActivityChannelButtonsLayout;
import org.telegram.ui.Components.chat.layouts.ChatActivityFadeView;
import org.telegram.ui.Components.chat.layouts.ChatActivitySideControlsButtonsLayout;
import org.telegram.ui.Components.inset.WindowInsetsStateHolder;
import org.telegram.ui.Components.poll.FileState;
import org.telegram.ui.Components.poll.PollAddOptionFieldLayout;
import org.telegram.ui.Components.poll.PollAttachedMediaPack;
import org.telegram.ui.Components.poll.PollSendParams;
import org.telegram.ui.Components.poll.PollUtils;
import org.telegram.ui.Components.poll.sheets.PollStatisticsBottomSheet;
import org.telegram.ui.Components.quickforward.QuickShareSelectorOverlayLayout;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.Delegates.ChatActivityMemberRequestsDelegate;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.Stars.MessageSuggestionOfferSheet;
import org.telegram.ui.Stars.StarReactionsOverlay;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stars.StarsReactionsSheet;
import org.telegram.ui.Stories.StoriesListPlaceProvider;
import org.telegram.ui.Stories.StoriesUtilities;
import org.telegram.ui.Stories.StoryViewer;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.Stories.recorder.PreviewView;
import org.telegram.ui.Stories.recorder.StoryEntry;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.TON.TONIntroActivity;
import org.telegram.ui.bots.AffiliateProgramFragment;
import org.telegram.ui.bots.BotAdView;
import org.telegram.ui.bots.BotCommandsMenuContainer;
import org.telegram.ui.bots.BotCommandsMenuView;
import org.telegram.ui.bots.BotWebViewSheet;
import org.telegram.ui.bots.WebViewRequestProps;
import org.telegram.ui.community.CommunitySheet;
import org.telegram.ui.iv.BlockRow;
import org.telegram.ui.iv.ChatAttachAlertRichLayout;
import org.telegram.ui.iv.RichEditor;
import org.telegram.ui.iv.RichEditorListView;
import org.telegram.ui.iv.RichHtml;
import org.telegram.ui.iv.RichMediaClipboard;
import org.webrtc.MediaStreamTrack;

public class ChatActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate, LocationActivity.LocationActivityDelegate, ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate, ChatActivityInterface, FloatingDebugProvider, InstantCameraView.Delegate, FactorAnimator.Target, ForwardContext {
    private static int SKELETON_LIGHT_OVERLAY_ALPHA = 22;
    private static float SKELETON_SATURATION = 1.4f;
    public static int lastStableId = 10;
    public static Pattern privateMsgUrlPattern;
    public static Pattern publicMsgUrlPattern;
    public static boolean scrolling;
    public static Pattern voiceChatUrlPattern;
    private static long wallpaperRandomSeed;
    private final Paint actionBarBackgroundPaint;
    public SearchTagsList actionBarSearchTags;
    private final ArrayList<View> actionModeViews;
    private ChatActivityActionsButtonsLayout actionsButtonsLayout;
    private long activityResumeTime;
    private ActionBarMenuItem.Item addContactItem;
    private TextView addProfilePictureButton;
    private TextView addToContactsButton;
    private boolean addToContactsButtonArchive;
    private ActionBarMenuItem.Item adminItemsGap;
    private TextView alertNameTextView;
    private TextView alertTextView;
    private FrameLayout alertView;
    private boolean allowContextBotPanel;
    private boolean allowContextBotPanelSecond;
    public boolean allowExpandPreviewByClick;
    private boolean allowStickersPanel;
    private final HashMap<MessageObject, Boolean> alreadyPlayedStickers;
    boolean animateProgressViewTo;
    private final ArrayList<ChatMessageCell> animateSendingViews;
    private final HashMap<TLRPC.Document, Integer> animatingDocuments;
    private ClippingImageView animatingImageView;
    public ArrayList<MessageObject> animatingMessageObjects;
    private final BoolAnimator animatorHideTopPanelByEmojiKeyboardExpanded;
    private final BoolAnimator animatorPollAddAnswerVisibility;
    private final BoolAnimator animatorPullingDownContainerVisibility;
    private final BoolAnimator animatorRoundMessageCameraVisibility;
    private final BoolAnimator animatorSearchFieldVisibility;
    private final BoolAnimator animatorSearchHashtagHistoryVisibility;
    private final BoolAnimator animatorSearchResultAsListVisibility;
    private int appliedDraftDate;
    private boolean approved;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private ActionBarMenu.LazyItem attachItem;
    private String attachMenuBotStartCommand;
    private String attachMenuBotToOpen;
    private ActionBarMenu.LazyItem audioCallIconItem;
    public ChatAvatarContainer avatarContainer;
    private ChatActivity backToPreviousFragment;
    private ChatBigEmptyView bigEmptyView;
    public ProfileBirthdayEffect.BirthdayEffectFetcher birthdayAssetsFetcher;
    private BusinessBotButton bizBotButton;
    private boolean bizbothint;
    private BlurredBackgroundColorProviderThemed blurredBackgroundColorProvider;
    private BlurredBackgroundColorProviderThemed blurredBackgroundColorProviderWhite;
    private BluredView blurredView;
    public int blurredViewBottomOffset;
    public int blurredViewTopOffset;
    private TL_stories.TL_premium_boostsStatus boostsStatus;
    private BotAdView botAdView;
    private MessageObject botButtons;
    private final PhotoViewer.PhotoViewerProvider botContextProvider;
    private ArrayList<Object> botContextResults;
    private final BotForumHelper.BotDraftAnimationsPool botDraftAnimationsPool;
    private final ChatActivityDraftMessageMeasureController botDraftHeightController;
    public final LongSparseArray<TL_bots.BotInfo> botInfo;
    private HintView2 botMessageHint;
    private MessageObject botReplyButtons;
    private MessageObject botSponsoredMessage;
    private String botUser;
    private int botsCount;
    private ChatActivityChannelButtonsLayout bottomChannelButtonsLayout;
    private HintView2 bottomGiftHintView;
    private FrameLayout bottomOverlay;
    private AnimatorSet bottomOverlayAnimation;
    private UnreadCounterTextView bottomOverlayChatText;
    private boolean bottomOverlayChatWaitsReply;
    private boolean bottomOverlayLinks;
    private LinkSpanDrawable.LinksTextView bottomOverlayLinksText;
    private RadialProgressView bottomOverlayProgress;
    private TextView bottomOverlayStartButton;
    private TextView bottomOverlayText;
    private HintView2 bottomSuggestHintView;
    private final ChatActivityBottomViewsVisibilityController bottomViewsVisibilityController;
    Bulletin.Delegate bulletinDelegate;
    public TL_account.TL_businessChatLink businessLink;
    private BusinessLinksEmptyView businessLinksEmptyView;
    private boolean[] cacheEndReached;
    private ChannelBoostsController.CanApplyBoost canApplyBoosts;
    private int canEditMessagesCount;
    private int canForwardMessagesCount;
    private int canSaveDocumentsCount;
    private int canSaveMusicCount;
    private boolean canShowPagedownButton;
    private Runnable cancelFixedPositionRunnable;
    private int cantDeleteMessagesCount;
    private int cantForwardMessagesCount;
    private int cantSaveMessagesCount;
    private ValueAnimator changeBoundAnimator;
    private ChatActivityDelegate chatActivityDelegate;
    private ChatActivityEnterTopView chatActivityEnterTopView;
    protected ChatActivityEnterView chatActivityEnterView;
    private boolean chatActivityEnterViewAnimateBeforeSending;
    private int chatActivityEnterViewAnimateFromTop;
    private ChatActivityFadeView chatActivityFadeView;
    private ChatActivityAdapter chatAdapter;
    public ChatAttachAlert chatAttachAlert;
    private int chatEmojiViewPadding;
    private long chatEnterTime;
    protected TLRPC.ChatFull chatInfo;
    private FrameLayout chatInputBubbleContainer;
    private FrameLayout chatInputInAppContainer;
    public ChatInputViewsContainer chatInputViewsContainer;
    private TLRPC.ChatInvite chatInvite;
    private Runnable chatInviteRunnable;
    private long chatInviterId;
    private GridLayoutManagerFixed chatLayoutManager;
    private long chatLeaveTime;
    private ChatListItemAnimator chatListItemAnimator;
    private ThanosEffect chatListThanosEffect;
    private ChatListRecyclerView chatListView;
    public float chatListViewPaddingTop;
    public int chatListViewPaddingVisibleOffset;
    private ChatListViewPaddingsAnimator chatListViewPaddingsAnimator;
    private MenuContextBuilder chatMenuContextData;
    private ChatMessageCellDelegate chatMessageCellDelegate;
    private final ChatMessagesMetadataController chatMessagesMetadataController;
    private int chatMode;
    private ChatNotificationsPopupWrapper chatNotificationsPopupWrapper;
    private RecyclerAnimationScrollHelper chatScrollHelper;
    private final ChatScrollCallback chatScrollHelperCallback;
    private ChatThemeBottomSheet chatThemeBottomSheet;
    private boolean chatWasReset;
    private TextView chatWithAdminTextView;
    private Runnable checkPaddingsRunnable;
    private boolean checkTextureViewPosition;
    private Runnable checkTranslationRunnable;
    private boolean checkedSavedMessagesHint;
    private ChecksHintView checksHintView;
    private ActionBarMenuItem.Item clearHistoryItem;
    private boolean clearOnLoad;
    private int clearOnLoadAndScrollMessageId;
    private int clearOnLoadAndScrollOffset;
    private boolean clearOnLoadButIsNewTopic;
    private LongSparseIntArray clearingHistoryArr;
    private final Client client;
    private final Rect clipBoundsTmp;
    private Dialog closeChatDialog;
    private Runnable closeInstantCameraAnimation;
    private ImageView closePinned;
    private ImageView closeReportSpam;
    private ActionBarMenuItem.Item closeTopicItem;
    private int commentLoadingGuid;
    private int commentLoadingMessageId;
    private long commentLoadingStartedAt;
    private int commentMessagesLoadingGuid;
    private int commentMessagesRequestId;
    private int commentRequestId;
    private int contentPaddingTop;
    private float contentPanTranslation;
    private float contentPanTranslationT;
    ContentPreviewViewer.ContentPreviewViewerDelegate contentPreviewViewerDelegate;
    public ChatActivityFragmentView contentView;
    private LongSparseArray<MessageObject> conversionMessages;
    SparseIntArray conversionObjectsStableIds;
    private boolean convertingToast;
    private int convertingToastMessageId;
    private boolean convertingToastShown;
    private boolean createGroupCall;
    private int createUnreadMessageAfterId;
    private boolean createUnreadMessageAfterIdLoading;
    protected TLRPC.Chat currentChat;
    protected TLRPC.EncryptedChat currentEncryptedChat;
    private boolean currentFloatingDateOnScreen;
    private boolean currentFloatingTopIsNotMessage;
    private boolean currentFloatingTopicOnScreen;
    private String currentPicturePath;
    private int currentPinnedMessageId;
    private final int[] currentPinnedMessageIndex;
    protected TLRPC.User currentUser;
    SparseIntArray dateObjectsStableIds;
    private int defaultSearchPage;
    private Runnable delayedReadRunnable;
    private final Runnable destroyTextureViewRunnable;
    int dialogFilterId;
    int dialogFolderId;
    private long dialog_id;
    private Long dialog_id_Long;
    private ChatMessageCell drawLaterRoundProgressCell;
    public float drawingChatListViewYoffset;
    private ChatMessageCell dummyMessageCell;
    private int editTextEnd;
    private ActionBarMenu.LazyItem editTextItem;
    private int editTextStart;
    public MessageObject editingMessageObject;
    private int editingMessageObjectReqId;
    public EmojiAnimationsOverlay emojiAnimationsOverlay;
    private View emojiButtonRed;
    private HintView emojiHintTextView;
    private LinkSpanDrawable.LinksTextView emojiStatusSpamHint;
    private TextView emptyView;
    private FrameLayout emptyViewContainer;
    private LinearLayout emptyViewContent;
    private boolean[] endReached;
    private HintView2 factCheckHint;
    private boolean fakePostponedScroll;
    private ActionBarMenuItem.Item feeItemGap;
    private ActionBarMenuItem.Item feeItemText;
    private FeedChatIntegration feedIntegration;
    private int feedLoadRetryCount;
    private final RectF feedTabBlurPosition;
    private int fieldPanelShown;
    private boolean filledEditTextItemMenu;
    private LongSparseArray<ArrayList<MessageObject>> filteredMessagesByDays;
    private LongSparseArray<MessageObject> filteredMessagesDict;
    protected FireworksOverlay fireworksOverlay;
    private boolean first;
    private boolean firstLoading;
    private boolean firstMessagesLoaded;
    boolean firstOpen;
    private boolean firstUnreadSent;
    private int first_unread_id;
    private boolean fixPaddingsInLayout;
    private int fixedKeyboardHeight;
    private FlagSecureReason flagSecure;
    private AnimatorSet floatingDateAnimation;
    private ChatActionCell floatingDateView;
    private float floatingDateViewOffset;
    private ValueAnimator floatingTopicAnimation;
    private TopicSeparator.Cell floatingTopicSeparator;
    private float floatingTopicViewAlpha;
    private float floatingTopicViewOffset;
    private boolean forbidForwardingWithDismiss;
    public boolean forceDisallowApplyWallpeper;
    public boolean forceDisallowRedrawThemeDescriptions;
    private boolean forceHistoryEmpty;
    private int forceNextPinnedMessageId;
    private boolean forceScrollToFirst;
    private boolean forceScrollToMessageBottom;
    private boolean forceScrollToTop;
    private CharSequence formwardingNameText;
    private TLRPC.TL_forumTopic forumTopic;
    private AnimatorSet forwardButtonAnimation;
    private boolean[] forwardEndReached;
    private HintView forwardHintView;
    private final ForwardContext.ForwardParams forwardParams;
    private MessageObject forwardingMessage;
    private MessageObject.GroupedMessages forwardingMessageGroup;
    MessagePreviewView forwardingPreviewView;
    private ArrayList<CharSequence> foundUrls;
    public TLRPC.WebPage foundWebPage;
    private FragmentContextView fragmentContextView;
    private FrameLayout fragmentContextViewWrapper;
    private FragmentContextView fragmentLocationContextView;
    private FrameLayout fragmentLocationContextViewWrapper;
    public boolean fragmentOpened;
    private AnimatorSet fragmentTransition;
    private final Runnable fragmentTransitionRunnable;
    private boolean fromPullingDownTransition;
    private HintView fwdRestrictedBottomHint;
    private HintView fwdRestrictedTopHint;
    private HintView gifHintTextView;
    private final ReferenceList<BlurredBackgroundDrawable> glassAttachedDrawables;
    private final ReferenceList<View> glassAttachedViews;
    private final BlurredBackgroundDrawableViewFactory glassBackgroundDrawableFactory;
    private final BlurredBackgroundDrawableViewFactory glassBackgroundDrawableFactoryFrosted;
    private final BlurredBackgroundSourceRenderNode glassBackgroundSourceFrostedRenderNode;
    private final BlurredBackgroundSourceRenderNode glassBackgroundSourceRenderNode;
    private final ArrayList<RectF> glassDrawablesPositions;
    private int glassDrawablesPositionsCount;
    private final ArrayList<RectF> glassDrawablesPositionsMerged;
    private Runnable glassSourceInvalidationCallback;
    private boolean globalIgnoreLayout;
    private NotificationCenter.ObserversGroup globalObserversGroup;
    private ChatActionCell greetingsInfo;
    private ChatGreetingsView greetingsViewContainer;
    private ChatObject.Call groupCall;
    private HintView2 groupEmojiPackHint;
    private LongSparseArray<MessageObject.GroupedMessages> groupedMessagesMap;
    private HintView2 guestBotHintView;
    private boolean hasAllMentionsLocal;
    private boolean hasBotWebView;
    private boolean hasBotsCommands;
    public boolean hasMainTabs;
    private boolean hasQuickReplies;
    private boolean hasSendingMessagesInBotForum;
    private boolean hasUnfavedSelected;
    private HashtagHistoryView hashtagHistoryView;
    private FlickerLoadingView hashtagLoadingView;
    private StickerEmptyView hashtagSearchEmptyView;
    private int hashtagSearchSelectedIndex;
    public ChatSearchTabs hashtagSearchTabs;
    private ActionBarMenuItem headerItem;
    private Runnable hideAlertViewRunnable;
    private boolean hideCommentLoading;
    private int hideDateDelay;
    private boolean hideForwardEndReached;
    public int highlightMessageId;
    public String highlightMessageQuote;
    public boolean highlightMessageQuoteFirst;
    private long highlightMessageQuoteFirstTime;
    public int highlightMessageQuoteOffset;
    public byte[] highlightPollOptionId;
    public Integer highlightTaskId;
    private MessageObject hint2MessageObject;
    private MessageObject hintMessageObject;
    private int hintMessageType;
    private boolean historyPreloaded;
    private boolean ignoreAttachOnPause;
    private boolean ignoreDraft;
    private boolean ignoreItemAnimation;
    private ChatActionCell infoTopView;
    private Animator infoTopViewAnimator;
    private int initialMessagesSize;
    private long inlineReturn;
    private float inputIslandHeightCurrent;
    private float inputIslandHeightTarget;
    public InstantCameraView instantCameraView;
    private OnPostDrawView invalidateBlurredSourcesView;
    private boolean invalidateChatListViewTopPadding;
    private boolean invalidateMessagesVisiblePart;
    public boolean isComments;
    public boolean isFullyVisible;
    public boolean isInsideContainer;
    private boolean isPauseOnThemePreview;
    public boolean isSubscriberSuggestions;
    public boolean isTopic;
    public boolean justCreatedChat;
    public boolean justCreatedTopic;
    private boolean keyboardWasVisible;
    private boolean lastCallCheckFromServer;
    private boolean lastImeVisible;
    private boolean lastInAppInputVisible;
    private HashMap<String, TLRPC.WebPage> lastLinkPreviewResults;
    private int lastLoadIndex;
    private long lastScrollTime;
    private int lastSkeletonCount;
    private int lastSkeletonMessageCount;
    private long lastSwitchTopicTime;
    private float lastTouchY;
    private long lastTranslationCheck;
    private int last_message_id;
    private int linkSearchRequestId;
    private Boolean liteModeChat;
    private boolean livestream;
    private boolean loadInfo;
    private final Runnable loadNextNewerFeedPage;
    private int loadedPinnedMessagesCount;
    private boolean loading;
    private boolean loadingForward;
    private boolean loadingFromOldPosition;

    @SuppressLint({"UseSparseArrays"})
    private final SparseArray<Boolean> loadingPinnedMessages;
    public boolean loadingPinnedMessagesList;
    private int loadsCount;
    private boolean locationAlertShown;
    private int[] maxDate;
    private int[] maxMessageId;
    private int maxPinnedMessageId;
    private boolean maybeStartTrackingSlidingView;
    private HintView mediaBanTooltip;
    private MentionsContainerView mentionContainer;
    private AnimatorSet mentionListAnimation;
    private RecyclerListView.OnItemClickListener mentionsOnItemClickListener;
    private ActionBarMenuSubItem menuDeleteItem;
    private long mergeDialogId;
    private Animator messageEditTextAnimator;
    public MessageEnterTransitionContainer messageEnterTransitionContainer;
    private ChatActivityMessageMetricsView messageMetricsView;
    public MessagePreviewParams messagePreviewParams;
    public Bulletin messageSeenPrivacyBulletin;
    private List<MessageSkeleton> messageSkeletons;
    public MessageSuggestionParams messageSuggestionParams;
    public ArrayList<MessageObject> messages;
    private HashMap<String, ArrayList<MessageObject>> messagesByDays;
    private SparseArray<ArrayList<MessageObject>> messagesByDaysSorted;
    private SparseArray<MessageObject>[] messagesDict;
    private MessagesSearchAdapter messagesSearchAdapter;
    private ChatActivitySearchContainer messagesSearchListContainer;
    public RecyclerListView messagesSearchListView;
    private int migrated_to;
    private int[] minDate;
    private int[] minMessageId;
    private ActionBarMenuItem.Item muteItem;
    private ActionBarMenuItem.Item muteItemGap;
    private final BlurredBackgroundDrawableViewFactory navbarContentDrawableFactory;
    private final BlurredBackgroundSourceWrapped navbarContentSourceWallpaper;
    private MessageObject needAnimateToMessage;
    private boolean needRemovePreviousSameChatActivity;
    private boolean needSelectFromMessageId;
    private int newMentionsCount;
    private int newUnreadMessageCount;
    private ArrayList<TLRPC.Chat> nextChannels;
    private boolean nextScrollForce;
    private int nextScrollForcePinnedMessageId;
    private int nextScrollFromMessageId;
    private int nextScrollLoadIndex;
    private boolean nextScrollSelect;
    private int nextScrollToMessageId;
    private HintView noSoundHintView;
    private ArrayList<MessageObject> notPushedSponsoredMessages;
    private NotificationCenter.ObserversGroup observersGroup;
    private Runnable onChatMessagesLoaded;
    private Runnable onHideFieldPanelRunnable;
    RecyclerListView.OnItemClickListenerExtended onItemClickListener;
    RecyclerListView.OnItemLongClickListenerExtended onItemLongClickListener;
    public Runnable onThemeChange;
    public boolean openAnimationEnded;
    private long openAnimationStartTime;
    private ActionBarMenuItem.Item openForumItem;
    private boolean openImport;
    protected boolean openKeyboardOnAttachMenuClose;
    private boolean openSearchKeyboard;
    private boolean openVideoChat;
    private ComposeDrawable otherIcon;
    private View overlayView;
    public float paddingTopHeight;
    private boolean pagedownButtonShowedByScroll;
    private ChatActivity parentChatActivity;
    private ThemeDelegate parentThemeDelegate;
    private boolean paused;
    private boolean pausedOnLastMessage;
    private String pendingLinkSearchString;
    private Runnable pendingOnOpenAction;
    private Runnable pendingReplyPanel;
    private ChatActivityMemberRequestsDelegate pendingRequestsDelegate;
    private final ArrayList<MessageObject> pendingSendMessages;
    private final SparseArray<MessageObject> pendingSendMessagesDict;
    private PhotoViewer.PhotoViewerProvider photoViewerPaidMediaProvider;
    private final PhotoViewer.PhotoViewerProvider photoViewerProvider;
    private int pinBullerinTag;
    private Bulletin pinBulletin;
    private PinchToZoomHelper pinchToZoomHelper;
    private NumberTextView pinnedCounterTextView;
    private int pinnedCounterTextViewX;
    private boolean pinnedEndReached;
    private int pinnedImageCacheType;
    private boolean pinnedImageHasBlur;
    private TLRPC.PhotoSize pinnedImageLocation;
    private TLObject pinnedImageLocationObject;
    private int pinnedImageSize;
    private TLRPC.PhotoSize pinnedImageThumbLocation;
    private PinnedLineView pinnedLineView;
    private AnimatorSet pinnedListAnimator;
    private ImageView pinnedListButton;
    private final PinnedMessageButton[] pinnedMessageButton;
    private boolean pinnedMessageButtonShown;
    private ArrayList<Integer> pinnedMessageIds;
    private final BackupImageView[] pinnedMessageImageView;
    private HashMap<Integer, MessageObject> pinnedMessageObjects;
    private final SimpleTextView[] pinnedMessageTextView;
    private FrameLayout pinnedMessageView;
    private final TrackingWidthSimpleTextView[] pinnedNameTextView;
    private final AnimatorSet[] pinnedNextAnimation;
    private RadialProgressView pinnedProgress;
    private boolean pinnedProgressIsShowing;
    private List<MenuItemRecord> pluginsContextMenuItems;
    private ActionBarMenuItem.Item pluginsMenuItem;
    private PluginsMenuWrapper pluginsMenuWrapper;
    private PollAddOptionFieldLayout pollAddOptionFieldLayout;
    private ChatMessageCell pollHintCell;
    private HintView pollHintView;
    private int pollHintX;
    private int pollHintY;
    private int pollVotesMentionCount;
    private LongSparseArray<ArrayList<MessageObject>> polls;
    private final ArrayList<MessageObject> pollsToCheck;
    private int popupAnimationIndex;
    private final NotificationCenter.PostponeNotificationCallback postponeNotificationsWhileLoadingCallback;
    private final DialogInterface.OnCancelListener postponedScrollCancelListener;
    private boolean postponedScrollIsCanceled;
    private int postponedScrollMessageId;
    private int postponedScrollMinMessageId;
    private int postponedScrollToLastMessageQueryIndex;
    private TLRPC.Document preloadedGreetingsSticker;
    private boolean premiumInvoiceBot;
    private int prevSetUnreadCount;
    boolean preventReopenSearchWithText;
    public ProfileChannelCell.ChannelMessageFetcher profileChannelMessageFetcher;
    private RadialProgressView progressBar;
    private AlertDialog progressDialog;
    private int progressDialogAtMessageId;
    private int progressDialogAtMessageType;
    private String progressDialogBotButtonUrl;
    private Browser.Progress progressDialogCurrent;
    private CharacterStyle progressDialogLinkSpan;
    private FrameLayout progressView;
    private View progressView2;
    boolean pulled;
    private float pullingBottomOffset;
    private float pullingDownAnimateProgress;
    private ChatActivity pullingDownAnimateToActivity;
    private Animator pullingDownBackAnimator;
    private ChatPullingDownDrawable pullingDownDrawable;
    private float pullingDownOffset;
    private QuickRepliesEmptyView quickRepliesEmptyView;
    private MessageObject quickReplyMessage;
    public String quickReplyShortcut;
    private QuickShareSelectorOverlayLayout quickShareSelectorOverlay;
    private AlertDialog quoteMessageUpdateAlert;
    private int reactionsMentionCount;
    private final int recommendedAdditionalSizeY;
    private boolean removingFromParent;
    private SparseArray<MessageObject> repliesMessagesDict;
    private ImageView replyCloseImageView;
    private int replyImageCacheType;
    private TLRPC.PhotoSize replyImageLocation;
    private TLObject replyImageLocationObject;
    private int replyImageSize;
    private TLRPC.PhotoSize replyImageThumbLocation;
    private ChatReplyContainer replyLayout;
    private int replyMaxReadId;
    private MessageObject replyMessageHeaderObject;
    private SparseArray<ArrayList<Integer>> replyMessageOwners;
    public TLRPC.Chat replyOriginalChat;
    private int replyOriginalMessageId;
    private MessageObject replyingMessageObject;
    private ReplyQuote replyingQuote;
    private MessageObject.GroupedMessages replyingQuoteGroup;
    public MessageObject replyingTopMessage;
    private String reportMessage;
    private byte[] reportOption;
    private TextView reportSpamButton;
    private String reportTitle;
    private boolean requestClearSearchPages;
    private TL_account.resolvedBusinessChatLinks resolvedChatLink;
    private TextView restartTopicButton;
    private boolean restoringFirstViewPageVisibility;
    private final Runnable retryFailedFeedLoad;
    private int returnToLoadIndex;
    private final Stack<Integer> returnToMessageIdStack;
    public boolean reversed;
    private View roundVideoRecordBackground;
    private ActionBarMenuItem.Item savedChatsGap;
    private ActionBarMenuItem.Item savedChatsItem;
    private TLRPC.TL_messages_discussionMessage savedDiscussionMessage;
    private TLRPC.messages_Messages savedHistory;
    private HintView2 savedMessagesHint;
    private boolean savedMessagesHintShown;
    private HintView2 savedMessagesSearchHint;
    private HintView2 savedMessagesTagHint;
    private boolean savedMessagesTagHintShown;
    private boolean savedNoDiscussion;
    private boolean savedNoHistory;
    private AlertDialog scheduleNowDialog;
    private HintView scheduledHint;
    private boolean scheduledHintShown;
    private int scheduledMessagesCount;
    private HintView scheduledOrNoSoundHint;
    private boolean scheduledOrNoSoundHintShown;
    private AnimatorSet scrimAnimatorSet;
    private final BlurredBackgroundDrawableViewFactory scrimBlur3Factory;
    private final BlurredBackgroundSourceBitmap scrimBlur3SourceBitmap;
    private Bitmap scrimBlurBitmap;
    private Paint scrimBlurBitmapPaint;
    private BitmapShader scrimBlurBitmapShader;
    private Matrix scrimBlurMatrix;
    private Paint scrimPaint;
    private float scrimPaintAlpha;
    public ActionBarPopupWindow scrimPopupWindow;
    private boolean scrimPopupWindowHideDimOnDismiss;
    private ActionBarMenuSubItem[] scrimPopupWindowItems;
    private boolean scrimProgressDirection;
    private View scrimView;
    private float scrimViewAlpha;
    private ValueAnimator scrimViewAlphaAnimator;
    private float scrimViewProgress;
    private Integer scrimViewReaction;
    private boolean scrimViewReactionAnimated;
    private int scrimViewReactionOffset;
    private Integer scrimViewTask;
    private int scrollAnimationIndex;
    private boolean scrollByTouch;
    private int scrollCallbackAnimationIndex;
    private MessageObject scrollToMessage;
    private int scrollToMessagePosition;
    private int scrollToOffsetOnRecreate;
    private int scrollToPositionOnRecreate;
    private boolean scrollToThreadMessage;
    private boolean scrollToTopOnResume;
    private boolean scrollToTopUnReadOnResume;
    private boolean scrollToVideo;
    private final DownscaleScrollableNoiseSuppressor scrollableViewNoiseSuppressor;
    private boolean scrollingChatListView;
    private boolean scrollingFloatingDate;
    private boolean scrollingFloatingTopic;
    private ImageView searchCalendarButton;
    private FrameLayout searchContainer;
    private final int searchContainerHeight;
    private AnimatedTextView searchCountText;
    private ValueAnimator searchExpandAnimator;
    private AnimatedTextView searchExpandList;
    private float searchExpandProgress;
    private ActionBarMenuItem searchIconItem;
    protected ActionBarMenuItem searchItem;
    private SearchItemListener searchItemListener;
    private boolean searchItemVisible;
    private int searchLastCount;
    private int searchLastIndex;
    private AnimatedTextView searchOtherButton;
    private int searchType;
    private ImageView searchUserButton;
    private ViewPagerFixed searchViewPager;
    boolean searchWas;
    private boolean searching;
    private TLRPC.Chat searchingChatMessages;
    private boolean searchingFiltered;
    private boolean searchingForUser;
    private String searchingHashtag;
    private String searchingQuery;
    public ReactionsLayoutInBubble.VisibleReaction searchingReaction;
    private TLRPC.User searchingUserMessages;
    private SecretVoicePlayer secretVoicePlayer;
    private final SparseArray<MessageObject>[] selectedMessagesCanCopyIds;
    private final SparseArray<MessageObject>[] selectedMessagesCanStarIds;
    private AnimatedTextView selectedMessagesCountTextView;
    private final SparseArrayWithTouch<MessageObject>[] selectedMessagesIds;
    private MessageObject selectedObject;
    private MessageObject.GroupedMessages selectedObjectGroup;
    private MessageObject selectedObjectToEditCaption;
    private ChatSelectionReactionMenuOverlay selectionReactionsOverlay;
    private TLRPC.TL_channels_sendAsPeers sendAsPeersObj;
    private boolean sentBotStart;
    private boolean setPinnedTextTranslationX;
    public int shareAlertDebugMode;
    public boolean shareAlertDebugTopicsSlowMotion;
    public ChatMessageSharedResources sharedResources;
    private boolean shouldHaveLightNavigationBarIcons;
    private boolean shouldHaveLightStatusBarIcons;
    private boolean showAudioCallAsIcon;
    private boolean showCloseChatDialogLater;
    public boolean showNoQuoteAlert;
    private boolean showPinBulletin;
    private final Runnable showScheduledHintRunnable;
    private final Runnable showScheduledOrNoSoundRunnable;
    private boolean showScrollToMessageError;
    private boolean showSearchAsIcon;
    private boolean showTapForForwardingOptionsHit;
    private boolean shownBotVerification;
    private boolean shownConversionDateTimeToast;
    private boolean shownRestartTopic;
    private boolean shownTranslateTopic;
    private ChatActivitySideControlsButtonsLayout sideControlsButtonsLayout;
    private Theme.MessageDrawable.PathDrawParams skeletonBackgroundCacheParams;
    private Theme.MessageDrawable skeletonBackgroundDrawable;
    private int skeletonColor0;
    private int skeletonColor1;
    private ColorMatrix skeletonColorMatrix;
    private LinearGradient skeletonGradient;
    private int skeletonGradientWidth;
    private long skeletonLastUpdateTime;
    private Matrix skeletonMatrix;
    private LinearGradient skeletonOutlineGradient;
    private Matrix skeletonOutlineMatrix;
    private Paint skeletonOutlinePaint;
    private Paint skeletonPaint;
    private Paint skeletonServicePaint;
    private int skeletonTotalTranslation;
    private View slidingView;
    private HintView slowModeHint;
    private boolean sponsoredMessagesAdded;
    private int sponsoredMessagesPostsBetween;
    private Pattern sponsoredUrlPattern;
    private StarReactionsOverlay starReactionsOverlay;
    private int startFromVideoMessageId;
    private int startFromVideoTimestamp;
    private int startLoadFromDate;
    private int startLoadFromMessageId;
    private int startLoadFromMessageIdSaved;
    private int startLoadFromMessageOffset;
    private long startMessageAppearTransitionMs;
    long startMs;
    private int startReplyTo;
    private String startVideoEdit;
    private boolean startedTrackingSlidingView;
    private SuggestEmojiView suggestEmojiPanel;
    private boolean swipeBackEnabled;
    private boolean switchFromTopics;
    private boolean switchingFromTopics;
    private float switchingFromTopicsProgress;
    private ReactionsContainerLayout tagSelector;
    private Runnable tapForForwardingOptionsHitRunnable;
    private ChatActivityTextSelectionHelper textSelectionHelper;
    private TextSelectionHint textSelectionHint;
    private boolean textSelectionHintWasShowed;
    private String textToSet;
    public ThemeDelegate themeDelegate;
    private int threadMaxInboxReadId;
    private int threadMaxOutboxReadId;
    private boolean threadMessageAdded;
    private long threadMessageId;
    private MessageObject threadMessageObject;
    private ArrayList<MessageObject> threadMessageObjects;
    private boolean threadMessageVisible;
    private int threadUnreadMessagesCount;
    private ActionBarMenuItem.Item timeItem2;
    private HintView timerHintView;
    private final float[] tmpOverlayPos;
    private final RectF tmpViewRectF;
    private boolean toPullingDownTransition;
    private FrameLayout topChatPanelView;
    private ChatActivityTopPanelLayout topPanelLayout;
    private UndoView topUndoView;
    private float topViewOffset;
    private int topViewWasVisible;
    private boolean topicChangedFromMessage;
    protected ActionBarMenuItem topicCreateItem;
    private MessageObject topicStarterMessageObject;
    public TopicsTabsView topicsTabs;
    private int totalPinnedMessagesCount;
    private int transitionAnimationGlobalIndex;
    private int transitionAnimationIndex;
    private TranslateButton translateButton;
    private ActionBarMenuItem.Item translateItem;
    private UndoView undoView;
    private MessageObject unreadMessageObject;
    private Runnable unselectRunnable;
    private final Runnable updateDeleteItemRunnable;
    Runnable updatePinnedProgressRunnable;
    Runnable updateReactionRunnable;
    private Runnable updateStreamingTopic;
    private boolean userBlocked;
    protected TLRPC.UserFull userInfo;
    private HintView2 videoConversionTimeHint;
    private float videoConversionTimeHintY;
    private FrameLayout videoPlayerContainer;
    private TextureView videoTextureView;
    private ActionBarMenuItem.Item viewAsTopics;
    private ViewPositionWatcher viewPositionWatcher;
    private String voiceChatHash;
    private HintView voiceHintTextView;
    private Runnable waitingForCharaterEnterRunnable;
    private boolean waitingForGetDifference;
    private final ArrayList<Integer> waitingForLoad;
    private SparseArray<MessageObject> waitingForReplies;
    private boolean waitingForReplyMessageLoad;
    private boolean waitingForSendingMessageLoad;
    int waitingForWebpageId;
    private WallpaperBitmapProvider wallpaperBitmapProvider;
    private boolean wasManualScroll;
    private boolean wasPaused;
    TextView webBotTitle;
    private final WindowInsetsStateHolder windowInsetsStateHolder;
    private static final int[] allowedNotificationsDuringChatListAnimations = {NotificationCenter.messagesRead, NotificationCenter.threadMessagesRead, NotificationCenter.monoForumMessagesRead, NotificationCenter.commentsRead, NotificationCenter.messagesReadEncrypted, NotificationCenter.messagesReadContent, NotificationCenter.didLoadPinnedMessages, NotificationCenter.newDraftReceived, NotificationCenter.updateMentionsCount, NotificationCenter.didUpdateConnectionState, NotificationCenter.updateDefaultSendAsPeer, NotificationCenter.closeChats, NotificationCenter.chatInfoCantLoad, NotificationCenter.userInfoDidLoad, NotificationCenter.pinnedInfoDidLoad, NotificationCenter.didSetNewWallpapper, NotificationCenter.savedMessagesDialogsUpdate, NotificationCenter.didApplyNewTheme, NotificationCenter.messageReceivedByServer2};
    private static boolean replacingChatActivity = false;
    private static final Rect clipBoundsRect = new Rect();

    public interface ChatActivityDelegate {
        default void onUnpin(boolean z, boolean z2) {
        }

        void openHashtagSearch(String str);

        default void openReplyMessage(int i) {
        }
    }

    public static void lambda$deleteHistory$4(int i, int i2, boolean z) {
        getMessagesController().deleteMessagesRange(this.dialog_id, ChatObject.isChannel(this.currentChat) ? this.dialog_id : 0L, i, i2, z, new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda541
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$deleteHistory$3();
            }
        });
    }

    public void lambda$onItemClick$0(int i) {
            ChatActivity.this.scrollToMessageId(i, 0, true, 0, true, 0);
        }

        void lambda$onMessageEditEnd$2() {
            ChatActivity.this.hideFieldPanel(true);
        }

        @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
        public void onWindowSizeChanged(int i) {
            int iDp = AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight();
            ChatActivity chatActivity = ChatActivity.this;
            if (i < iDp) {
                chatActivity.allowStickersPanel = false;
                if (ChatActivity.this.suggestEmojiPanel.getVisibility() == 0) {
                    ChatActivity.this.suggestEmojiPanel.setVisibility(4);
                }
            } else {
                chatActivity.allowStickersPanel = true;
                if (ChatActivity.this.suggestEmojiPanel.getVisibility() == 4 && !ChatActivity.this.isInPreviewMode()) {
                    ChatActivity.this.suggestEmojiPanel.setVisibility(0);
                }
            }
            ChatActivity chatActivity2 = ChatActivity.this;
            chatActivity2.allowContextBotPanel = !chatActivity2.chatActivityEnterView.isPopupShowing();
            int i2 = i + (ChatActivity.this.chatActivityEnterView.isPopupShowing() ? 65536 : 0);
            if (this.lastSize != i2) {
                ChatActivity.this.chatActivityEnterViewAnimateFromTop = 0;
                ChatActivity.this.chatActivityEnterViewAnimateBeforeSending = false;
            }
            this.lastSize = i2;
        }

        @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
        public void onStickersTab(boolean z) {
            if (ChatActivity.this.emojiButtonRed != null) {
                ChatActivity.this.emojiButtonRed.setVisibility(8);
            }
            ChatActivity.this.allowContextBotPanelSecond = !z;
        }

        @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
        public void didPressAttachButton() {
            ChatAttachAlert chatAttachAlert = ChatActivity.this.chatAttachAlert;
            if (chatAttachAlert != null) {
                chatAttachAlert.setEditingMessageObject(0, null);
            }
            ChatActivity.this.openAttachMenu();
        }

        @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
        public void setFrontface(boolean z) {
            ChatActivity.this.checkInstantCameraView();
            InstantCameraView instantCameraView = ChatActivity.this.instantCameraView;
            if (instantCameraView != null) {
                instantCameraView.setFrontface(z);
            }
        }

        @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
        public void didPressSuggestionButton() {
            Context context = ChatActivity.this.getContext();
            int i = ((BaseFragment) ChatActivity.this).currentAccount;
            long j = ChatActivity.this.dialog_id;
            MessageSuggestionParams messageSuggestionParamsEmpty = ChatActivity.this.messageSuggestionParams;
            if (messageSuggestionParamsEmpty == null) {
                messageSuggestionParamsEmpty = MessageSuggestionParams.empty();
            }
            ChatActivity chatActivity = ChatActivity.this;
            new MessageSuggestionOfferSheet(context, i, j, messageSuggestionParamsEmpty, chatActivity, chatActivity.getResourceProvider(), 0, new ChatActivity$$ExternalSyntheticLambda179(ChatActivity.this)).show();
        }

        @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
        public void toggleVideoRecordingPause() {
            InstantCameraView instantCameraView = ChatActivity.this.instantCameraView;
            if (instantCameraView != null) {
                instantCameraView.togglePause();
            }
        }

        @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
        public boolean isVideoRecordingPaused() {
            InstantCameraView instantCameraView = ChatActivity.this.instantCameraView;
            return instantCameraView != null && instantCameraView.isPaused();
        }

        @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
        public void needStartRecordVideo(int i, boolean z, int i2, int i3, int i4, long j, long j2) {
            ChatActivity.this.checkInstantCameraView();
            InstantCameraView instantCameraView = ChatActivity.this.instantCameraView;
            if (instantCameraView != null) {
                if (i == 0) {
                    instantCameraView.showCamera(false);
                    ChatActivity.this.chatListView.stopScroll();
                    ChatActivity.this.chatAdapter.updateRowsSafe();
                } else if (i == 1 || i == 3 || i == 4) {
                    instantCameraView.send(i, z, i2, 0, i4, j, j2);
                } else if (i == 2 || i == 5) {
                    instantCameraView.cancel(i == 2);
                }
            }
        }

        @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
        public void needChangeVideoPreviewState(int i, float f) {
            InstantCameraView instantCameraView = ChatActivity.this.instantCameraView;
            if (instantCameraView != null) {
                instantCameraView.changeVideoPreviewState(i, f);
            }
        }

        @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
        public void needStartRecordAudio(int i) {
            int i2 = i == 0 ? 8 : 0;
            if (ChatActivity.this.overlayView.getVisibility() != i2) {
                ChatActivity.this.overlayView.setVisibility(i2);
            }
        }

        @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
        public void needShowMediaBanHint() {
            ChatActivity.this.showMediaBannedHint();
        }

        @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
        public void onEmojiViewTabChanged() {
            ChatActivity.this.animatorHideTopPanelByEmojiKeyboardExpanded.setValue(ChatActivity.this.chatActivityEnterView.isStickersExpanded() && !ChatActivity.this.chatActivityEnterView.isCurrentPageEmoji(), true);
        }

        @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEnterViewDelegate
        public void onStickersExpandedChange() {
            ChatActivity.this.checkRaiseSensors();
            final boolean zIsStickersExpanded = ChatActivity.this.chatActivityEnterView.isStickersExpanded();
            ChatActivity.this.animatorHideTopPanelByEmojiKeyboardExpanded.setValue(zIsStickersExpanded && !ChatActivity.this.chatActivityEnterView.isCurrentPageEmoji(), true);
            ChatActivity chatActivity = ChatActivity.this;
            if (zIsStickersExpanded) {
                AndroidUtilities.setAdjustResizeToNothing(chatActivity.getParentActivity(), ((BaseFragment) ChatActivity.this).classGuid);
                if (Bulletin.getVisibleBulletin() != null && Bulletin.getVisibleBulletin().isShowing()) {
                    Bulletin.getVisibleBulletin().hide();
                }
            } else {
                AndroidUtilities.requestAdjustResize(chatActivity.getParentActivity(), ((BaseFragment) ChatActivity.this).classGuid);
            }
            float f = 0.0f;
            if (ChatActivity.this.mentionContainer != null) {
                ChatActivity.this.mentionContainer.animate().alpha((zIsStickersExpanded || ChatActivity.this.isInPreviewMode()) ? 0.0f : 1.0f).setInterpolator(CubicBezierInterpolator.DEFAULT).start();
            }
            if (ChatActivity.this.suggestEmojiPanel != null) {
                ChatActivity.this.suggestEmojiPanel.setVisibility(0);
                ViewPropertyAnimator viewPropertyAnimatorAnimate = ChatActivity.this.suggestEmojiPanel.animate();
                if (!zIsStickersExpanded && !ChatActivity.this.isInPreviewMode()) {
                    f = 1.0f;
                }
                viewPropertyAnimatorAnimate.alpha(f).setInterpolator(CubicBezierInterpolator.DEFAULT).withEndAction(new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatActivityEnterViewDelegate$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onStickersExpandedChange$3(zIsStickersExpanded);
                    }
                }).start();
            }
        }

        public void lambda$new$8() {
        ChatActivityEnterView chatActivityEnterView;
        View sendButton;
        if (getParentActivity() == null || this.fragmentView == null || (chatActivityEnterView = this.chatActivityEnterView) == null || (sendButton = chatActivityEnterView.getSendButton()) == null || this.chatActivityEnterView.getEditField() == null || this.chatActivityEnterView.getEditField().getText().length() < 5) {
            return;
        }
        SharedConfig.increaseScheduledOrNoSoundHintShowed();
        if (this.scheduledOrNoSoundHint == null) {
            HintView hintView = new HintView(getParentActivity(), 4, this.themeDelegate) { // from class: org.telegram.ui.ChatActivity.13
                @Override // org.telegram.ui.Components.HintView
                public int offsetCx() {
                    return AndroidUtilities.dp(56.0f) / 2;
                }
            };
            this.scheduledOrNoSoundHint = hintView;
            hintView.createCloseButton();
            this.scheduledOrNoSoundHint.setAlpha(0.0f);
            this.scheduledOrNoSoundHint.setVisibility(4);
            this.scheduledOrNoSoundHint.setText(LocaleController.getString(R.string.ScheduledOrNoSoundHint));
            this.contentView.addView(this.scheduledOrNoSoundHint, LayoutHelper.createFrame(-2, -2.0f, 51, 10.0f, 0.0f, 10.0f, 0.0f));
        }
        this.scheduledOrNoSoundHint.showForView(sendButton, true);
        this.scheduledOrNoSoundHintShown = true;
    }

    public @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z, boolean z2) {
                ImageReceiver photoImage;
                if (i >= 0 && i < ChatActivity.this.botContextResults.size() && ChatActivity.this.mentionContainer != null && ChatActivity.this.mentionContainer.getListView() != null) {
                    int childCount = ChatActivity.this.mentionContainer.getListView().getChildCount();
                    Object obj = ChatActivity.this.botContextResults.get(i);
                    for (int i2 = 0; i2 < childCount; i2++) {
                        View childAt = ChatActivity.this.mentionContainer.getListView().getChildAt(i2);
                        if (childAt instanceof ContextLinkCell) {
                            ContextLinkCell contextLinkCell = (ContextLinkCell) childAt;
                            if (contextLinkCell.getResult() == obj) {
                                photoImage = contextLinkCell.getPhotoImage();
                            } else {
                                photoImage = null;
                            }
                        } else {
                            photoImage = null;
                        }
                        if (photoImage != null) {
                            int[] iArr = new int[2];
                            childAt.getLocationInWindow(iArr);
                            PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                            placeProviderObject.viewX = iArr[0];
                            placeProviderObject.viewY = iArr[1];
                            placeProviderObject.parentView = ChatActivity.this.mentionContainer.getListView();
                            placeProviderObject.imageReceiver = photoImage;
                            placeProviderObject.thumb = photoImage.getBitmapSafe();
                            placeProviderObject.radius = photoImage.getRoundRadius(true);
                            return placeProviderObject;
                        }
                    }
                }
                return null;
            }

            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, int i3, boolean z2) {
                if (i < 0 || i >= ChatActivity.this.botContextResults.size()) {
                    return;
                }
                ChatActivity chatActivity = ChatActivity.this;
                chatActivity.sendBotInlineResult((TLRPC.BotInlineResult) chatActivity.botContextResults.get(i), z, i2, 0L);
            }
        };
        this.onItemLongClickListener = new RecyclerListView.OnItemLongClickListenerExtended() { // from class: org.telegram.ui.ChatActivity.10
            void lambda$onFragmentCreate$10(MessagesStorage messagesStorage, long j, CountDownLatch countDownLatch) {
        this.currentChat = messagesStorage.getChat(j);
        countDownLatch.countDown();
    }

    public void lambda$firstLoadMessages$19() {
        this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
        if (this.chatMode == 7) {
            loadMoreSearchResults();
        } else if (this.startLoadFromDate != 0) {
            MessagesController messagesController = getMessagesController();
            long j = this.dialog_id;
            long j2 = this.mergeDialogId;
            int i = this.startLoadFromDate;
            int i2 = this.classGuid;
            int i3 = this.chatMode;
            long j3 = this.threadMessageId;
            int i4 = this.replyMaxReadId;
            int i5 = this.lastLoadIndex;
            this.lastLoadIndex = i5 + 1;
            messagesController.loadMessages(j, j2, false, 30, 0, i, true, 0, i2, 4, 0, i3, j3, i4, i5, this.isTopic);
        } else if (this.startLoadFromMessageId != 0 && (!isThreadChat() || this.startLoadFromMessageId == this.highlightMessageId || this.isTopic)) {
            this.startLoadFromMessageIdSaved = this.startLoadFromMessageId;
            int i6 = this.migrated_to;
            if (i6 != 0) {
                this.mergeDialogId = i6;
                MessagesController messagesController2 = getMessagesController();
                long j4 = this.mergeDialogId;
                boolean z = this.loadInfo;
                int i7 = this.initialMessagesSize;
                int i8 = this.startLoadFromMessageId;
                int i9 = this.classGuid;
                int i10 = this.chatMode;
                long j5 = this.threadMessageId;
                int i11 = this.replyMaxReadId;
                int i12 = this.lastLoadIndex;
                this.lastLoadIndex = i12 + 1;
                messagesController2.loadMessages(j4, 0L, z, i7, i8, 0, true, 0, i9, 3, 0, i10, j5, i11, i12, this.isTopic);
            } else {
                MessagesController messagesController3 = getMessagesController();
                long j6 = this.dialog_id;
                long j7 = this.mergeDialogId;
                boolean z2 = this.loadInfo;
                int i13 = this.initialMessagesSize;
                int i14 = this.startLoadFromMessageId;
                int i15 = this.classGuid;
                int i16 = this.chatMode;
                long j8 = this.threadMessageId;
                int i17 = this.replyMaxReadId;
                int i18 = this.lastLoadIndex;
                this.lastLoadIndex = i18 + 1;
                messagesController3.loadMessages(j6, j7, z2, i13, i14, 0, true, 0, i15, 3, 0, i16, j8, i17, i18, this.isTopic);
            }
        } else if (this.historyPreloaded) {
            this.lastLoadIndex++;
        } else {
            MessagesController messagesController4 = getMessagesController();
            long j9 = this.dialog_id;
            long j10 = this.mergeDialogId;
            boolean z3 = this.loadInfo;
            int i19 = this.initialMessagesSize;
            int i20 = this.startLoadFromMessageId;
            int i21 = this.classGuid;
            int i22 = this.chatMode;
            long j11 = this.threadMessageId;
            int i23 = this.replyMaxReadId;
            int i24 = this.lastLoadIndex;
            this.lastLoadIndex = i24 + 1;
            messagesController4.loadMessages(j9, j10, z3, i19, i20, 0, true, 0, i21, 2, 0, i22, j11, i23, i24, this.isTopic);
        }
        int i25 = this.chatMode;
        if (i25 == 0 || (i25 == 3 && getSavedDialogId() == getUserConfig().getClientUserId())) {
            if (!isThreadChat() || this.isTopic) {
                this.waitingForLoad.add(Integer.valueOf(this.lastLoadIndex));
                MessagesController messagesController5 = getMessagesController();
                long j12 = this.dialog_id;
                long j13 = this.mergeDialogId;
                int i26 = this.classGuid;
                long j14 = this.chatMode == 3 ? 0L : this.threadMessageId;
                int i27 = this.replyMaxReadId;
                int i28 = this.lastLoadIndex;
                this.lastLoadIndex = i28 + 1;
                messagesController5.loadMessages(j12, j13, false, 1, 0, 0, true, 0, i26, 2, 0, 1, j14, i27, i28, this.isTopic);
            }
        }
    }

    private void fillInviterId(boolean z) {
        TLRPC.Chat chat = this.currentChat;
        if (chat == null || this.chatInfo == null || ChatObject.isNotInChat(chat) || this.currentChat.creator) {
            return;
        }
        TLRPC.ChatFull chatFull = this.chatInfo;
        long j = chatFull.inviterId;
        if (j != 0) {
            this.chatInviterId = j;
            return;
        }
        TLRPC.ChatParticipants chatParticipants = chatFull.participants;
        if (chatParticipants != null) {
            TLRPC.ChatParticipant chatParticipant = chatParticipants.self_participant;
            if (chatParticipant != null) {
                this.chatInviterId = chatParticipant.inviter_id;
                return;
            }
            long clientUserId = getUserConfig().getClientUserId();
            int size = this.chatInfo.participants.participants.size();
            for (int i = 0; i < size; i++) {
                TLRPC.ChatParticipant chatParticipant2 = this.chatInfo.participants.participants.get(i);
                if (chatParticipant2.user_id == clientUserId) {
                    this.chatInviterId = chatParticipant2.inviter_id;
                    return;
                }
            }
        }
        if (z && this.chatInviterId == 0) {
            getMessagesController().checkChatInviter(this.currentChat.id, false);
        }
    }

    private void hideUndoViews() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        Bulletin bulletin = this.pinBulletin;
        if (bulletin != null) {
            bulletin.hide(false, 0L);
        }
        UndoView undoView2 = this.topUndoView;
        if (undoView2 != null) {
            undoView2.hide(true, 0);
        }
    }

    public int getOtherSameChatsDiff() {
        INavigationLayout iNavigationLayout = this.parentLayout;
        int i = 0;
        if (iNavigationLayout == null || iNavigationLayout.getFragmentStack() == null) {
            return 0;
        }
        int iIndexOf = this.parentLayout.getFragmentStack().indexOf(this);
        if (iIndexOf == -1) {
            iIndexOf = this.parentLayout.getFragmentStack().size();
        }
        while (i < this.parentLayout.getFragmentStack().size()) {
            BaseFragment baseFragment = this.parentLayout.getFragmentStack().get(i);
            if (baseFragment != this && (baseFragment instanceof ChatActivity) && ((ChatActivity) baseFragment).dialog_id == this.dialog_id) {
                return i - iIndexOf;
            }
            i++;
        }
        i = iIndexOf;
        return i - iIndexOf;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBeginSlide() {
        super.onBeginSlide();
        ChatSelectionReactionMenuOverlay chatSelectionReactionMenuOverlay = this.selectionReactionsOverlay;
        if (chatSelectionReactionMenuOverlay == null || !chatSelectionReactionMenuOverlay.isVisible()) {
            return;
        }
        this.selectionReactionsOverlay.setHiddenByScroll(true);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        AndroidUtilities.cancelRunOnUIThread(this.loadNextNewerFeedPage);
        AndroidUtilities.cancelRunOnUIThread(this.retryFailedFeedLoad);
        FeedChatIntegration feedChatIntegration = this.feedIntegration;
        if (feedChatIntegration != null) {
            feedChatIntegration.destroy();
        }
        ViewPositionWatcher viewPositionWatcher = this.viewPositionWatcher;
        if (viewPositionWatcher != null) {
            viewPositionWatcher.shutdown();
            this.viewPositionWatcher = null;
        }
        super.onFragmentDestroy();
        ChatActivityMessageMetricsView chatActivityMessageMetricsView = this.messageMetricsView;
        if (chatActivityMessageMetricsView != null) {
            chatActivityMessageMetricsView.finish();
        }
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
        if (chatAvatarContainer != null) {
            chatAvatarContainer.onDestroy();
        }
        MentionsContainerView mentionsContainerView = this.mentionContainer;
        if (mentionsContainerView != null && mentionsContainerView.getAdapter() != null) {
            this.mentionContainer.getAdapter().onDestroy();
        }
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            chatAttachAlert.dismissInternal();
        }
        ContentPreviewViewer.getInstance().clearDelegate(this.contentPreviewViewerDelegate);
        getNotificationCenter().onAnimationFinish(this.transitionAnimationIndex);
        NotificationCenter.getGlobalInstance().onAnimationFinish(this.transitionAnimationGlobalIndex);
        getNotificationCenter().onAnimationFinish(this.scrollAnimationIndex);
        getNotificationCenter().onAnimationFinish(this.scrollCallbackAnimationIndex);
        hideUndoViews();
        Runnable runnable = this.chatInviteRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.chatInviteRunnable = null;
        }
        getNotificationCenter().removePostponeNotificationsCallback(this.postponeNotificationsWhileLoadingCallback);
        getMessagesController().setLastCreatedDialogId(this.dialog_id, this.chatMode == 1, false);
        NotificationCenter.ObserversGroup observersGroup = this.observersGroup;
        if (observersGroup != null) {
            observersGroup.removeAllObservers();
            this.observersGroup = null;
        }
        NotificationCenter.ObserversGroup observersGroup2 = this.globalObserversGroup;
        if (observersGroup2 != null) {
            observersGroup2.removeAllObservers();
            this.globalObserversGroup = null;
        }
        getNotificationCenter().removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pluginMenuItemsUpdated);
        if (this.chatMode == 0 && AndroidUtilities.isTablet()) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.openedChatChanged, Long.valueOf(this.dialog_id), Long.valueOf(getTopicId()), Boolean.TRUE);
        }
        if (this.currentUser != null) {
            MediaController.getInstance().stopMediaObserver();
        }
        FlagSecureReason flagSecureReason = this.flagSecure;
        if (flagSecureReason != null) {
            flagSecureReason.detach();
        }
        if (this.currentUser != null) {
            getMessagesController().cancelLoadFullUser(this.currentUser.id);
        }
        AndroidUtilities.removeAdjustResize(getParentActivity(), this.classGuid);
        ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
        if (chatAttachAlert2 != null) {
            chatAttachAlert2.onDestroy();
        }
        AndroidUtilities.unlockOrientation(getParentActivity());
        if (ChatObject.isChannel(this.currentChat)) {
            getMessagesController().startShortPoll(this.currentChat, this.classGuid, true);
            TLRPC.ChatFull chatFull = this.chatInfo;
            if (chatFull != null && chatFull.linked_chat_id != 0) {
                getMessagesController().startShortPoll(getMessagesController().getChat(Long.valueOf(this.chatInfo.linked_chat_id)), this.classGuid, true);
            }
        }
        ChatActivityTextSelectionHelper chatActivityTextSelectionHelper = this.textSelectionHelper;
        if (chatActivityTextSelectionHelper != null) {
            chatActivityTextSelectionHelper.clear();
        }
        ChatListItemAnimator chatListItemAnimator = this.chatListItemAnimator;
        if (chatListItemAnimator != null) {
            chatListItemAnimator.onDestroy();
        }
        PinchToZoomHelper pinchToZoomHelper = this.pinchToZoomHelper;
        if (pinchToZoomHelper != null) {
            pinchToZoomHelper.clear();
        }
        this.chatThemeBottomSheet = null;
        INavigationLayout parentLayout = getParentLayout();
        if (parentLayout != null && parentLayout.getFragmentStack() != null) {
            BackButtonMenu.clearPulledDialogs(this, parentLayout.getFragmentStack().indexOf(this) - (!replacingChatActivity ? 1 : 0));
        }
        replacingChatActivity = false;
        Browser.Progress progress = this.progressDialogCurrent;
        if (progress != null) {
            progress.cancel();
            this.progressDialogCurrent = null;
        }
        this.chatMessagesMetadataController.onFragmentDestroy();
        ProfileBirthdayEffect.BirthdayEffectFetcher birthdayEffectFetcher = this.birthdayAssetsFetcher;
        if (birthdayEffectFetcher != null) {
            birthdayEffectFetcher.detach(true);
            this.birthdayAssetsFetcher = null;
        }
        StarReactionsOverlay starReactionsOverlay = this.starReactionsOverlay;
        if (starReactionsOverlay != null) {
            starReactionsOverlay.setMessageCell(null);
            AndroidUtilities.removeFromParent(this.starReactionsOverlay);
            this.starReactionsOverlay = null;
        }
    }

    public static class ChatActivityTextSelectionHelper extends TextSelectionHelper.ChatListTextSelectionHelper {
        ChatActivity chatActivity;

        private ChatActivityTextSelectionHelper() {
        }

        public void setChatActivity(ChatActivity chatActivity) {
            cancelAllAnimators();
            clear();
            this.textSelectionOverlay = null;
            this.chatActivity = chatActivity;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        public int getParentTopPadding() {
            ChatActivity chatActivity = this.chatActivity;
            if (chatActivity == null) {
                return 0;
            }
            return (int) chatActivity.chatListViewPaddingTop;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        public int getParentBottomPadding() {
            ChatActivity chatActivity = this.chatActivity;
            if (chatActivity == null) {
                return 0;
            }
            return chatActivity.blurredViewBottomOffset;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        public int getThemedColor(int i) {
            return Theme.getColor(i, this.chatActivity.themeDelegate);
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        public Theme.ResourcesProvider getResourcesProvider() {
            ChatActivity chatActivity = this.chatActivity;
            if (chatActivity != null) {
                return chatActivity.themeDelegate;
            }
            return null;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        public boolean canShowQuote() {
            Cell cell;
            ChatActivity chatActivity;
            Cell cell2;
            ChatActivityEnterView chatActivityEnterView;
            ChatActivity chatActivity2 = this.chatActivity;
            if (chatActivity2 != null && chatActivity2.getDialogId() == UserObject.VERIFY) {
                return false;
            }
            ChatActivity chatActivity3 = this.chatActivity;
            return !this.isFactCheck && (chatActivity = this.chatActivity) != null && chatActivity.getCurrentEncryptedChat() == null && ((cell2 = this.selectedView) == 0 || ((!((ChatMessageCell) cell2).getMessageObject().isVoiceTranscriptionOpen() || ((chatActivityEnterView = this.chatActivity.chatActivityEnterView) != null && chatActivityEnterView.getVisibility() == 0)) && !((ChatMessageCell) this.selectedView).getMessageObject().isInvoice() && ((ChatMessageCell) this.selectedView).getMessageObject().richLayout == null && !this.chatActivity.textSelectionHelper.isDescription)) && !this.chatActivity.getMessagesController().getTranslateController().isTranslatingDialog(this.chatActivity.dialog_id) && !UserObject.isService(this.chatActivity.dialog_id) && (!((chatActivity3 != null && chatActivity3.isPeerNoForwards()) || ((cell = this.selectedView) != 0 && ((ChatMessageCell) cell).getMessageObject() != null && ((ChatMessageCell) this.selectedView).getMessageObject().messageOwner != null && ((ChatMessageCell) this.selectedView).getMessageObject().messageOwner.noforwards)) || this.chatActivity.getCurrentChat() == null || ChatObject.canWriteToChat(this.chatActivity.getCurrentChat()));
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        public boolean canCopy() {
            ChatActivity chatActivity;
            ChatActivity chatActivity2 = this.chatActivity;
            if ((chatActivity2 == null || chatActivity2.getDialogId() != UserObject.VERIFY) && (chatActivity = this.chatActivity) != null) {
                if (chatActivity.getDialogId() < 0 && this.chatActivity.getMessagesController().isPeerNoForwards(this.chatActivity.getDialogId())) {
                    return false;
                }
                Cell cell = this.selectedView;
                if (cell != 0 && ((ChatMessageCell) cell).getMessageObject() != null && ((ChatMessageCell) this.selectedView).getMessageObject().messageOwner != null && ((ChatMessageCell) this.selectedView).getMessageObject().messageOwner.noforwards) {
                    return false;
                }
            }
            return true;
        }

        @Override // org.telegram.ui.Cells.TextSelectionHelper
        public void onQuoteClick(MessageObject messageObject, int i, int i2, CharSequence charSequence) {
            ChatActivity chatActivity;
            CharSequence voiceTranscription;
            MessageObject.GroupedMessages group;
            if (messageObject == null || (chatActivity = this.chatActivity) == null) {
                return;
            }
            int iMin = Math.min(i2, chatActivity.getMessagesController().quoteLengthMax + i);
            if (messageObject.getGroupId() != 0 && (group = this.chatActivity.getGroup(messageObject.getGroupId())) != null && !group.isDocuments) {
                messageObject = group.captionMessage;
            }
            if (messageObject == null) {
                return;
            }
            ChatActivityEnterView chatActivityEnterView = this.chatActivity.chatActivityEnterView;
            if (chatActivityEnterView == null || chatActivityEnterView.getVisibility() != 0 || !messageObject.isVoiceTranscriptionOpen()) {
                MessageObject forwardingMessageObject = FeedMessageUtils.getForwardingMessageObject(((BaseFragment) this.chatActivity).currentAccount, this.chatActivity.isFeedSearch(), messageObject);
                ReplyQuote replyQuoteFrom = ReplyQuote.from(forwardingMessageObject, i, iMin);
                if (replyQuoteFrom.getText() == null) {
                    return;
                }
                ChatActivityEnterView chatActivityEnterView2 = this.chatActivity.chatActivityEnterView;
                if (chatActivityEnterView2 != null && chatActivityEnterView2.getVisibility() == 0) {
                    if (((BaseFragment) this.chatActivity).actionBar != null && ((BaseFragment) this.chatActivity).actionBar.isActionModeShowed()) {
                        this.chatActivity.clearSelectionMode();
                    }
                    this.chatActivity.showFieldPanelForReplyQuote(forwardingMessageObject, replyQuoteFrom);
                    ChatActivityEnterView chatActivityEnterView3 = this.chatActivity.chatActivityEnterView;
                    if (chatActivityEnterView3 != null) {
                        chatActivityEnterView3.openKeyboard();
                        return;
                    }
                    return;
                }
                this.chatActivity.replyingQuote = replyQuoteFrom;
                this.chatActivity.replyingMessageObject = forwardingMessageObject;
                this.chatActivity.forbidForwardingWithDismiss = false;
                ChatActivity chatActivity2 = this.chatActivity;
                chatActivity2.messagePreviewParams = new MessagePreviewParams(chatActivity2.currentEncryptedChat != null, chatActivity2.isPeerNoForwards(), ChatObject.isMonoForum(this.chatActivity.currentChat));
                ChatActivity chatActivity3 = this.chatActivity;
                chatActivity3.messagePreviewParams.updateReply(chatActivity3.replyingMessageObject, this.chatActivity.getGroup(forwardingMessageObject.getGroupId()), this.chatActivity.getDialogId(), this.chatActivity.replyingQuote);
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlySelect", true);
                bundle.putInt("dialogsType", 3);
                bundle.putBoolean("quote", true);
                bundle.putInt("messagesCount", 1);
                bundle.putBoolean("canSelectTopics", true);
                DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                dialogsActivity.setDelegate(this.chatActivity);
                this.chatActivity.presentFragment(dialogsActivity);
                return;
            }
            if (((BaseFragment) this.chatActivity).actionBar != null && ((BaseFragment) this.chatActivity).actionBar.isActionModeShowed()) {
                this.chatActivity.clearSelectionMode();
            }
            ChatActivityEnterView chatActivityEnterView4 = this.chatActivity.chatActivityEnterView;
            EditTextCaption editField = chatActivityEnterView4.getEditField();
            if (editField == null || (voiceTranscription = messageObject.getVoiceTranscription()) == null) {
                return;
            }
            if (iMin > voiceTranscription.length()) {
                iMin = voiceTranscription.length();
            }
            if (i > iMin) {
                i = iMin;
            }
            CharSequence charSequenceSubSequence = voiceTranscription.subSequence(i, iMin);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
            spannableStringBuilder.append(charSequenceSubSequence);
            QuoteSpan.QuoteStyleSpan quoteStyleSpan = new QuoteSpan.QuoteStyleSpan();
            QuoteSpan quoteSpan = new QuoteSpan(true, true, quoteStyleSpan);
            quoteStyleSpan.span = quoteSpan;
            quoteSpan.start = 0;
            quoteSpan.end = charSequenceSubSequence.length();
            spannableStringBuilder.setSpan(quoteSpan, 0, charSequenceSubSequence.length(), 33);
            spannableStringBuilder.setSpan(quoteStyleSpan, 0, charSequenceSubSequence.length(), 33);
            spannableStringBuilder.append((CharSequence) "\n");
            CharSequence fieldText = chatActivityEnterView4.getFieldText();
            if (fieldText != null) {
                spannableStringBuilder.append(fieldText);
            }
            editField.setText(spannableStringBuilder);
            this.chatActivity.showFieldPanelForReply(messageObject);
            chatActivityEnterView4.openKeyboard();
            editField.setSelection(editField.length());
        }
    }

    Context val$context;

        public AnonymousClass16(Context context) {
            this.val$context = context;
        }

        void lambda$onItemClick$0() {
            ChatActivity.this.finishFragment();
        }

        public void lambda$onItemClick$1(long j, long j2, Long l, Boolean bool) {
            StarsController.getInstance(((BaseFragment) ChatActivity.this).currentAccount).stopPaidMessages(j, j2, l.longValue() > 0 && bool.booleanValue(), true);
        }

        public void lambda$run$0(boolean z, boolean z2) {
                ChatActivity.this.performHistoryClear(true, z);
            }

            public void lambda$onItemClick$5(int i, boolean z, boolean z2) {
            if (i == 15 && ChatObject.isChannel(ChatActivity.this.currentChat)) {
                TLRPC.Chat chat = ChatActivity.this.currentChat;
                if (!chat.megagroup || ChatObject.isPublic(chat)) {
                    ChatActivity.this.getMessagesController().deleteDialog(ChatActivity.this.dialog_id, 2, z2);
                    return;
                }
            }
            ChatActivity chatActivity = ChatActivity.this;
            if (i != 15) {
                NotificationCenter notificationCenter = chatActivity.getNotificationCenter();
                ChatActivity chatActivity2 = ChatActivity.this;
                int i2 = NotificationCenter.closeChats;
                notificationCenter.removeObserver(chatActivity2, i2);
                ChatActivity.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i2, new Object[0]);
                ChatActivity.this.finishFragment();
                NotificationCenter notificationCenter2 = ChatActivity.this.getNotificationCenter();
                int i3 = NotificationCenter.needDeleteDialog;
                Long lValueOf = Long.valueOf(ChatActivity.this.dialog_id);
                ChatActivity chatActivity3 = ChatActivity.this;
                notificationCenter2.lambda$postNotificationNameOnUIThread$1(i3, lValueOf, chatActivity3.currentUser, chatActivity3.currentChat, Boolean.valueOf(z2));
                return;
            }
            chatActivity.performHistoryClear(z2, z);
        }

        public void lambda$createView$25(TLObject tLObject, TLObject tLObject2, TLRPC.TL_error tL_error) {
        final int i;
        if (tLObject2 instanceof TLRPC.messages_Messages) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject2;
            if (!messages_messages.messages.isEmpty()) {
                i = ((TLRPC.messages_Messages) tLObject).offset_id_offset - messages_messages.offset_id_offset;
            } else {
                i = ((TLRPC.messages_Messages) tLObject).offset_id_offset;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda322
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$createView$24(i);
                }
            });
        }
    }

    public @Override // org.telegram.ui.ChatActivity.ChatListRecyclerView
        public void drawChatForegroundElements(Canvas canvas, RectF rectF) {
            float f;
            ArrayList<ChatMessageCell> arrayList;
            ArrayList<ChatMessageCell> arrayList2;
            ArrayList<ChatMessageCell> arrayList3;
            int size = this.drawTimeAfter.size();
            boolean z = 1;
            boolean z2 = false;
            if (size > 0) {
                int i = 0;
                while (true) {
                    arrayList3 = this.drawTimeAfter;
                    if (i >= size) {
                        break;
                    }
                    ChatMessageCell chatMessageCell = arrayList3.get(i);
                    if (!ChatActivity.this.quickRejectChild(chatMessageCell, rectF)) {
                        canvas.save();
                        canvas.translate(chatMessageCell.getLeft() + chatMessageCell.getNonAnimationTranslationX(false), chatMessageCell.getY() + chatMessageCell.getPaddingTop());
                        chatMessageCell.drawTime(canvas, chatMessageCell.shouldDrawAlphaLayer() ? chatMessageCell.getAlpha() : 1.0f, true);
                        canvas.restore();
                    }
                    i++;
                }
                arrayList3.clear();
            }
            int size2 = this.drawNamesAfter.size();
            if (size2 > 0) {
                int i2 = 0;
                while (true) {
                    arrayList2 = this.drawNamesAfter;
                    if (i2 >= size2) {
                        break;
                    }
                    ChatMessageCell chatMessageCell2 = arrayList2.get(i2);
                    if (!ChatActivity.this.quickRejectChild(chatMessageCell2, rectF)) {
                        float left = chatMessageCell2.getLeft() + chatMessageCell2.getNonAnimationTranslationX(false);
                        float y = chatMessageCell2.getY() + chatMessageCell2.getPaddingTop();
                        float alpha = chatMessageCell2.shouldDrawAlphaLayer() ? chatMessageCell2.getAlpha() : 1.0f;
                        canvas.save();
                        canvas.translate(left, y);
                        chatMessageCell2.setInvalidatesParent(true);
                        chatMessageCell2.drawNamesLayout(canvas, alpha);
                        chatMessageCell2.setInvalidatesParent(false);
                        canvas.restore();
                    }
                    i2++;
                }
                arrayList2.clear();
            }
            int size3 = this.drawCaptionAfter.size();
            if (size3 > 0) {
                int i3 = 0;
                while (true) {
                    arrayList = this.drawCaptionAfter;
                    if (i3 >= size3) {
                        break;
                    }
                    ChatMessageCell chatMessageCell3 = arrayList.get(i3);
                    if (!ChatActivity.this.quickRejectChild(chatMessageCell3, rectF)) {
                        boolean z3 = (chatMessageCell3.getCurrentPosition() == null || (chatMessageCell3.getCurrentPosition().flags & z) != 0) ? z2 : z;
                        float alpha2 = chatMessageCell3.shouldDrawAlphaLayer() ? chatMessageCell3.getAlpha() : 1.0f;
                        float left2 = chatMessageCell3.getLeft() + chatMessageCell3.getNonAnimationTranslationX(z2);
                        float y2 = chatMessageCell3.getY() + chatMessageCell3.getPaddingTop();
                        canvas.save();
                        MessageObject.GroupedMessages currentMessagesGroup = chatMessageCell3.getCurrentMessagesGroup();
                        if (currentMessagesGroup != null && currentMessagesGroup.transitionParams.backgroundChangeBounds) {
                            float nonAnimationTranslationX = chatMessageCell3.getNonAnimationTranslationX(z);
                            MessageObject.GroupedMessages.TransitionParams transitionParams = currentMessagesGroup.transitionParams;
                            float f2 = transitionParams.left + nonAnimationTranslationX + transitionParams.offsetLeft;
                            float translationY = transitionParams.top + transitionParams.offsetTop;
                            float f3 = transitionParams.right + nonAnimationTranslationX + transitionParams.offsetRight;
                            float translationY2 = transitionParams.bottom + transitionParams.offsetBottom;
                            if (!transitionParams.backgroundChangeBounds) {
                                translationY += chatMessageCell3.getTranslationY();
                                translationY2 += chatMessageCell3.getTranslationY();
                            }
                            canvas.clipRect(f2 + AndroidUtilities.dp(8.0f), translationY + AndroidUtilities.dp(8.0f), f3 - AndroidUtilities.dp(8.0f), translationY2 - AndroidUtilities.dp(8.0f));
                        }
                        if (chatMessageCell3.getTransitionParams().wasDraw) {
                            canvas.translate(left2, y2);
                            chatMessageCell3.setInvalidatesParent(true);
                            chatMessageCell3.drawCaptionLayout(canvas, z3, alpha2);
                            chatMessageCell3.setInvalidatesParent(false);
                        }
                        canvas.restore();
                    }
                    i3++;
                    z = 1;
                    z2 = false;
                }
                f = 8.0f;
                arrayList.clear();
            } else {
                f = 8.0f;
            }
            int size4 = this.drawReactionsAfter.size();
            if (size4 <= 0) {
                return;
            }
            int i4 = 0;
            while (true) {
                ArrayList<ChatMessageCell> arrayList4 = this.drawReactionsAfter;
                if (i4 < size4) {
                    ChatMessageCell chatMessageCell4 = arrayList4.get(i4);
                    if (!ChatActivity.this.quickRejectChild(chatMessageCell4, rectF)) {
                        boolean z4 = chatMessageCell4.getCurrentPosition() != null && (chatMessageCell4.getCurrentPosition().flags & 1) == 0;
                        float alpha3 = chatMessageCell4.shouldDrawAlphaLayer() ? chatMessageCell4.getAlpha() : 1.0f;
                        float left3 = chatMessageCell4.getLeft() + chatMessageCell4.getNonAnimationTranslationX(false);
                        float y3 = chatMessageCell4.getY() + chatMessageCell4.getPaddingTop();
                        canvas.save();
                        MessageObject.GroupedMessages currentMessagesGroup2 = chatMessageCell4.getCurrentMessagesGroup();
                        if (currentMessagesGroup2 != null && currentMessagesGroup2.transitionParams.backgroundChangeBounds) {
                            float nonAnimationTranslationX2 = chatMessageCell4.getNonAnimationTranslationX(true);
                            MessageObject.GroupedMessages.TransitionParams transitionParams2 = currentMessagesGroup2.transitionParams;
                            float f4 = transitionParams2.left + nonAnimationTranslationX2 + transitionParams2.offsetLeft;
                            float translationY3 = transitionParams2.top + transitionParams2.offsetTop;
                            float f5 = transitionParams2.right + nonAnimationTranslationX2 + transitionParams2.offsetRight;
                            float translationY4 = transitionParams2.bottom + transitionParams2.offsetBottom;
                            if (!transitionParams2.backgroundChangeBounds) {
                                translationY3 += chatMessageCell4.getTranslationY();
                                translationY4 += chatMessageCell4.getTranslationY();
                            }
                            canvas.clipRect(f4 + AndroidUtilities.dp(f), translationY3 + AndroidUtilities.dp(f), f5 - AndroidUtilities.dp(f), translationY4 - AndroidUtilities.dp(f));
                        }
                        if (!z4 && chatMessageCell4.getTransitionParams().wasDraw) {
                            canvas.translate(left3, y3);
                            chatMessageCell4.setInvalidatesParent(true);
                            chatMessageCell4.drawReactionsLayout(canvas, alpha3, null);
                            chatMessageCell4.drawCommentLayout(canvas, alpha3);
                            chatMessageCell4.setInvalidatesParent(false);
                        }
                        canvas.restore();
                    }
                    i4++;
                } else {
                    arrayList4.clear();
                    return;
                }
            }
        }

        void lambda$onLayoutChildren$0() {
            ChatActivity.this.chatAdapter.notifyDataSetChanged(false);
        }

        void lambda$createView$41(boolean z, boolean z2) {
        this.topPanelLayout.setViewVisible(this.pendingRequestsDelegate.getView(), z, z2);
    }

    public void lambda$createView$43(Object obj, Long l) {
        TLRPC.TL_messages_sendQuickReplyMessages tL_messages_sendQuickReplyMessages = new TLRPC.TL_messages_sendQuickReplyMessages();
        tL_messages_sendQuickReplyMessages.peer = getMessagesController().getInputPeer(this.dialog_id);
        tL_messages_sendQuickReplyMessages.shortcut_id = ((QuickRepliesController.QuickReply) obj).id;
        getConnectionsManager().sendRequest(tL_messages_sendQuickReplyMessages, null);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.setFieldText(null);
        }
    }

    public void lambda$createView$49(final TLRPC.BotInlineResult botInlineResult, final Long l) {
        if (this.chatMode == 1) {
            AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.dialog_id, new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda295
                @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                public final void didSelectDate(boolean z, int i, int i2) {
                    this.f$0.lambda$createView$48(botInlineResult, l, z, i, i2);
                }
            }, this.themeDelegate);
        } else {
            sendBotInlineResult(botInlineResult, true, 0, l.longValue());
        }
    }

    public void lambda$createView$55(MessageObject messageObject) {
        scrollToMessageId(messageObject.getId(), 0, true, 0, true, 0, null, new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda248
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$createView$54();
            }
        });
        if (this.waitingForLoad.isEmpty()) {
            showMessagesSearchListView(false);
        }
    }

    public void lambda$checkAnimation$0(ValueAnimator valueAnimator) {
            setAnimatedTop((int) ((Float) valueAnimator.getAnimatedValue()).floatValue());
            ChatActivity.this.invalidateChatListViewTopPadding();
            ChatActivity.this.invalidateMessagesVisiblePart();
            this.messageEditTextContainer.invalidate();
            invalidate();
        }

        public void lambda$createView$60(View view) {
        setForwardParams(false);
        openForward(false);
    }

    public void lambda$createView$63(View view) {
        ArrayList arrayList = new ArrayList();
        int i = 1;
        while (true) {
            if (i < 0) {
                break;
            }
            for (int i2 = 0; i2 < this.selectedMessagesIds[i].size(); i2++) {
                arrayList.add(Integer.valueOf(this.selectedMessagesIds[i].keyAt(i2)));
            }
            i--;
        }
        if (arrayList.isEmpty()) {
            return;
        }
        Collections.sort(arrayList);
        Integer num = (Integer) arrayList.get(0);
        Integer num2 = (Integer) arrayList.get(arrayList.size() - 1);
        for (int i3 = 0; i3 < this.messages.size(); i3++) {
            int id = this.messages.get(i3).getId();
            MessageObject messageObject = this.messages.get(i3);
            if (messageObject != null && id > num.intValue() && id < num2.intValue() && this.selectedMessagesIds[0].indexOfKey(id) < 0 && messageObject.contentType == 0) {
                addToSelectedMessages(messageObject, false);
            }
        }
        updateActionModeTitle();
        updateVisibleRows();
    }

    public void lambda$createView$64(Integer num) {
        if (this.chatAttachAlert == null) {
            createChatAttachView();
        }
        this.chatAttachAlert.setEditingMessageObject(num.intValue(), this.editingMessageObject);
        openAttachMenu();
    }

    public void lambda$createView$67(View view) {
        MessageObject messageObject;
        this.messageSuggestionParams = null;
        int i = this.fieldPanelShown;
        if (i == 2) {
            this.replyingQuote = null;
            this.replyingMessageObject = null;
            MessagePreviewParams messagePreviewParams = this.messagePreviewParams;
            if (messagePreviewParams != null) {
                messagePreviewParams.updateReply(null, null, this.dialog_id, null);
            }
            fallbackFieldPanel();
            return;
        }
        if (i == 3) {
            openAnotherForward();
            return;
        }
        if (i == 4) {
            this.foundWebPage = null;
            MessagePreviewParams messagePreviewParams2 = this.messagePreviewParams;
            if (messagePreviewParams2 != null) {
                int i2 = this.currentAccount;
                MessageObject messageObject2 = this.replyingMessageObject;
                messagePreviewParams2.updateLink(i2, null, null, messageObject2 == this.threadMessageObject ? null : messageObject2, this.replyingQuote, this.editingMessageObject);
            }
            this.chatActivityEnterView.setWebPage(null, false);
            editResetMediaManual();
            fallbackFieldPanel();
            return;
        }
        if (ChatObject.isForum(this.currentChat) && !this.isTopic && (messageObject = this.replyingMessageObject) != null) {
            long topicId = MessageObject.getTopicId(this.currentAccount, messageObject.messageOwner, true);
            if (topicId != 0) {
                getMediaDataController().cleanDraft(this.dialog_id, topicId, false);
            }
        }
        showFieldPanel(false, null, null, null, null, true, 0, null, true, 0L, true);
    }

    public void lambda$createView$84(Boolean bool) {
        showBottomOverlayProgress(true, false);
        if (bool.booleanValue()) {
            finishFragment();
        }
    }

    public Boolean lambda$createView$96(URLSpan uRLSpan) {
        didPressMessageUrl(uRLSpan, false, null, null);
        return Boolean.TRUE;
    }

    public void lambda$showBotMessageHint$102(HintView2 hintView2) {
        this.contentView.removeView(hintView2);
    }

    public int $r8$lambda$ieCZZH7R8vIxFrxjcsJH2OxfELM(MessageObject messageObject, MessageObject messageObject2) {
        return messageObject.getId() - messageObject2.getId();
    }

    public static void lambda$createTopPanel$106(int i) {
        if (i == 0) {
            updateTopPanel(true);
        } else {
            finishFragment();
        }
    }

    public void lambda$createTopicsTabs$115() {
        invalidateChatListViewTopPadding();
        AndroidUtilities.forEachViews((RecyclerView) this.chatListView, (Consumer<View>) new Consumer() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda268
            @Override // com.google.android.exoplayer2.util.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$createTopicsTabs$114((View) obj);
            }
        });
        checkUi_topPanelLayoutWidth();
        ChatActionCell chatActionCell = this.floatingDateView;
        if (chatActionCell != null) {
            chatActionCell.setTranslationX(getSideMenuWidth() / 2.0f);
        }
        TopicSeparator.Cell cell = this.floatingTopicSeparator;
        if (cell != null) {
            cell.setTranslationX(getSideMenuWidth() / 2.0f);
        }
        FrameLayout frameLayout = this.emptyViewContainer;
        if (frameLayout != null) {
            frameLayout.setTranslationX(getSideMenuWidth() / 2.0f);
        }
        checkInsets();
        checkUi_topFade();
    }

    public void lambda$createTopicsTabs$118(Long l, Boolean bool) {
        if (l.longValue() == getTopicId()) {
            return;
        }
        this.lastSwitchTopicTime = SystemClock.uptimeMillis();
        this.topicChangedFromMessage = bool.booleanValue();
        if (l.longValue() == 0) {
            savePositionForTopicChange(getTopicId());
        } else if (getTopicId() == 0) {
            savePositionForTopicChange(l.longValue());
        } else {
            this.clearOnLoadAndScrollMessageId = -1;
        }
        getConnectionsManager().cancelRequestsForGuid(this.classGuid);
        getMessagesStorage().cancelTasksForGuid(this.classGuid);
        this.classGuid = ConnectionsManager.generateClassGuid();
        saveDraft();
        this.messagePreviewParams = null;
        this.startLoadFromMessageId = 0;
        this.firstMessagesLoaded = false;
        this.clearOnLoad = true;
        this.waitingForLoad.clear();
        setSavedDialog(l.longValue());
        TLRPC.TL_forumTopic tL_forumTopicFindTopic = getMessagesController().getTopicsController().findTopic(-getDialogId(), l.longValue());
        if (l.longValue() != 0 && tL_forumTopicFindTopic != null) {
            int i = tL_forumTopicFindTopic.read_inbox_max_id;
            this.threadMaxInboxReadId = i;
            this.threadMaxOutboxReadId = tL_forumTopicFindTopic.read_outbox_max_id;
            this.replyMaxReadId = Math.max(1, i);
            getMessagesController().getTopicsController().getTopicRepliesCount(this.dialog_id, DialogObject.getPeerDialogId(tL_forumTopicFindTopic.from_id));
        } else {
            this.forumTopic = null;
            this.threadMessageObjects = null;
            this.threadMessageObject = null;
            this.replyingMessageObject = null;
            this.threadMaxInboxReadId = 0;
            this.threadMaxOutboxReadId = 0;
            this.replyMaxReadId = 0;
            this.threadMessageId = 0L;
            this.replyOriginalMessageId = 0;
            this.replyOriginalChat = null;
            this.isTopic = false;
            this.isComments = false;
        }
        firstLoadMessages();
        updateTitle(true);
        this.avatarContainer.updateSubtitle(true);
        this.avatarContainer.checkAndUpdateAvatar();
        this.topicsTabs.setCurrentTopic(l.longValue());
        updateTopPanel(true);
        updateBottomOverlay(true);
        getMessagesController().setForumLastTopicId(-getDialogId(), getTopicId());
        hideFloatingTopicView(true);
        hideFieldPanel(true);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.hidePopup(false);
            this.chatActivityEnterView.updateFieldHint(true);
        }
        applyDraftMaybe(true, true);
        this.reactionsMentionCount = tL_forumTopicFindTopic != null ? tL_forumTopicFindTopic.unread_reactions_count : 0;
        this.pollVotesMentionCount = tL_forumTopicFindTopic != null ? tL_forumTopicFindTopic.unread_poll_votes_count : 0;
        updateReactionsMentionButton(false);
        updatePollVotesMentionButton(false);
        updateTopicButtons();
        if (this.searchItemListener == null || !this.actionBar.isSearchFieldVisible()) {
            return;
        }
        this.searchItemListener.onSearchPressed(null);
    }

    private void toggleIsAllChats() {
        final boolean zIsAllChats = isAllChats();
        AndroidUtilities.forEachViews((RecyclerView) this.chatListView, (Consumer<View>) new Consumer() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda156
            @Override // com.google.android.exoplayer2.util.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$toggleIsAllChats$119(zIsAllChats, (View) obj);
            }
        });
    }

    public void lambda$updateBubbleOffset$0(ValueAnimator valueAnimator) {
            setBubbleOffset(((Float) valueAnimator.getAnimatedValue()).floatValue());
            invalidate();
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ActionBarMenuItem item = ((BaseFragment) ChatActivity.this).actionBar.createActionMode().getItem(28);
            if (item != null) {
                getLocationInWindow(this.loc);
                float x = getX();
                float width = getWidth() + x;
                item.getLocationInWindow(this.loc);
                float width2 = this.loc[0] + (item.getWidth() / 2.0f);
                int iDp = AndroidUtilities.dp(20.0f);
                boolean z2 = LocaleController.isRTL;
                float f = width2 + (iDp * (z2 ? -1 : 1));
                boolean z3 = this.firstLayout;
                if (z2) {
                    updateBubbleOffset(f - x, !z3);
                } else {
                    updateBubbleOffset(f - width, !z3);
                }
                this.firstLayout = false;
            }
        }
    }

    public void createSearchContainer() {
        if (this.searchContainer != null || getContext() == null) {
            return;
        }
        FrameLayout frameLayout = new FrameLayout(getContext()) { // from class: org.telegram.ui.ChatActivity.61
            @Override // android.view.ViewGroup
            public void measureChildWithMargins(View view, int i, int i2, int i3, int i4) {
                if (view == ChatActivity.this.searchCountText) {
                    int i5 = (ChatActivity.this.searchCalendarButton == null || ChatActivity.this.searchCalendarButton.getVisibility() == 8) ? 18 : 66;
                    if (ChatActivity.this.searchUserButton != null && ChatActivity.this.searchUserButton.getVisibility() != 8) {
                        i5 += 48;
                    }
                    ((ViewGroup.MarginLayoutParams) view.getLayoutParams()).leftMargin = AndroidUtilities.dp(i5);
                }
                super.measureChildWithMargins(view, i, i2, i3, i4);
            }
        };
        this.searchContainer = frameLayout;
        frameLayout.setClickable(false);
        this.searchContainer.setWillNotDraw(false);
        this.bottomViewsVisibilityController.setViewVisible(4, false, false);
        this.searchContainer.setClipToPadding(false);
        AnimatedTextView animatedTextView = new AnimatedTextView(getContext(), true, true, true);
        this.searchCountText = animatedTextView;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        animatedTextView.setAnimationProperties(0.25f, 0L, 280L, cubicBezierInterpolator);
        this.searchCountText.setTextSize(AndroidUtilities.dp(15.0f));
        this.searchCountText.setTypeface(AndroidUtilities.bold());
        this.searchCountText.setTextColor(getThemedColor(Theme.key_chat_searchPanelText));
        this.searchCountText.setGravity(3);
        this.searchContainer.addView(this.searchCountText, LayoutHelper.createFrame(-2, 30.0f, 16, 0.0f, -1.0f, 97.33f, 0.0f));
        this.chatInputBubbleContainer.addView(this.searchContainer, LayoutHelper.createFrame(-1, 48.0f, 80, 7.0f, 0.0f, 7.0f, 0.0f));
        AnimatedTextView animatedTextView2 = new AnimatedTextView(getContext(), true, false, true);
        this.searchExpandList = animatedTextView2;
        animatedTextView2.setAnimationProperties(0.0f, 0L, 420L, cubicBezierInterpolator);
        this.searchExpandList.setScaleProperty(0.7f);
        this.searchExpandList.setTextSize(AndroidUtilities.dp(15.0f));
        this.searchExpandList.setGravity(5);
        this.searchExpandList.setTypeface(AndroidUtilities.bold());
        this.searchExpandList.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteBlueText2));
        this.searchExpandList.setText(LocaleController.getString(isMessagesSearchListVisible() ? R.string.SearchAsChat : R.string.SearchAsList));
        AnimatedTextView animatedTextView3 = this.searchExpandList;
        animatedTextView3.adaptWidth = false;
        animatedTextView3.setPadding(AndroidUtilities.dp(15.33f), 0, AndroidUtilities.dp(15.33f), 0);
        this.searchExpandList.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda202
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$createSearchContainer$123(view);
            }
        });
        this.searchExpandList.setAlpha(0.5f);
        this.searchExpandList.setClickable(false);
        this.searchContainer.addView(this.searchExpandList, LayoutHelper.createFrame(-2, -1, 117));
        AnimatedTextView animatedTextView4 = new AnimatedTextView(getContext(), true, true, true);
        this.searchOtherButton = animatedTextView4;
        animatedTextView4.setGravity(17);
        this.searchOtherButton.setTypeface(AndroidUtilities.bold());
        this.searchOtherButton.setTextColor(getThemedColor(Theme.key_chat_fieldOverlayText));
        this.searchOtherButton.setTextSize(AndroidUtilities.dp(15.0f));
        AnimatedTextView animatedTextView5 = this.searchOtherButton;
        int i = Theme.key_windowBackgroundWhite;
        animatedTextView5.setBackground(Theme.createSelectorWithBackgroundDrawable(getThemedColor(i), Theme.blendOver(getThemedColor(i), getThemedColor(Theme.key_listSelector))));
        this.searchOtherButton.setText(LocaleController.getString(this.chatAdapter.isFiltered ? R.string.SavedTagHideOtherMessages : R.string.SavedTagShowOtherMessages));
        this.searchOtherButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda203
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$createSearchContainer$124(view);
            }
        });
        this.searchOtherButton.setVisibility(8);
        this.searchOtherButton.setAlpha(0.0f);
        this.searchContainer.addView(this.searchOtherButton, LayoutHelper.createFrame(-1, -1, 119));
        TLRPC.Chat chat = this.currentChat;
        if (chat != null && ((!ChatObject.isChannel(chat) || this.currentChat.megagroup) && this.chatMode != 7 && !ChatObject.isMonoForum(this.currentChat))) {
            ImageView imageView = new ImageView(getContext());
            this.searchUserButton = imageView;
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            this.searchUserButton.setImageResource(R.drawable.msg_usersearch);
            this.searchUserButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_searchPanelIcons), PorterDuff.Mode.MULTIPLY));
            this.searchUserButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_actionBarActionModeDefaultSelector), 1));
            this.searchContainer.addView(this.searchUserButton, LayoutHelper.createFrame(48, 48.0f, 51, 48.0f, 0.0f, 0.0f, 0.0f));
            this.searchUserButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda204
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$createSearchContainer$125(view);
                }
            });
            this.searchUserButton.setContentDescription(LocaleController.getString(R.string.AccDescrSearchByUser));
        }
        if (this.chatMode != 7) {
            ImageView imageView2 = new ImageView(getContext());
            this.searchCalendarButton = imageView2;
            imageView2.setScaleType(ImageView.ScaleType.CENTER);
            this.searchCalendarButton.setImageResource(R.drawable.msg_calendar);
            this.searchCalendarButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_searchPanelIcons), PorterDuff.Mode.MULTIPLY));
            this.searchCalendarButton.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_actionBarActionModeDefaultSelector), 1));
            this.searchContainer.addView(this.searchCalendarButton, LayoutHelper.createFrame(48, 48.0f, 51, 2.66f, 0.0f, 0.0f, 0.0f));
            this.searchCalendarButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda205
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$createSearchContainer$126(view);
                }
            });
            this.searchCalendarButton.setContentDescription(LocaleController.getString(R.string.JumpToDate));
        }
    }

    public void lambda$onPageDownClicked$128() {
        this.sideControlsButtonsLayout.setButtonLoading(1, true, true);
    }

    private void playReactionAnimation(Integer num) {
        if (this.fragmentView == null) {
            return;
        }
        BaseCell baseCellFindMessageCell = findMessageCell(num.intValue(), false);
        if (baseCellFindMessageCell instanceof ChatMessageCell) {
            ChatMessageCell chatMessageCell = (ChatMessageCell) baseCellFindMessageCell;
            TLRPC.MessagePeerReaction randomUnreadReaction = chatMessageCell.getMessageObject().getRandomUnreadReaction();
            if (randomUnreadReaction != null && (chatMessageCell.reactionsLayoutInBubble.hasUnreadReactions || randomUnreadReaction.big)) {
                ReactionsEffectOverlay.show(this, null, baseCellFindMessageCell, null, 0.0f, 0.0f, ReactionsLayoutInBubble.VisibleReaction.fromTL(randomUnreadReaction.reaction), this.currentAccount, !randomUnreadReaction.big ? 1 : 0);
                ReactionsEffectOverlay.startAnimation();
            }
            chatMessageCell.markReactionsAsRead();
            return;
        }
        if (baseCellFindMessageCell instanceof ChatActionCell) {
            ChatActionCell chatActionCell = (ChatActionCell) baseCellFindMessageCell;
            TLRPC.MessagePeerReaction randomUnreadReaction2 = chatActionCell.getMessageObject().getRandomUnreadReaction();
            if (randomUnreadReaction2 != null && (chatActionCell.reactionsLayoutInBubble.hasUnreadReactions || randomUnreadReaction2.big)) {
                ReactionsEffectOverlay.show(this, null, baseCellFindMessageCell, null, 0.0f, 0.0f, ReactionsLayoutInBubble.VisibleReaction.fromTL(randomUnreadReaction2.reaction), this.currentAccount, !randomUnreadReaction2.big ? 1 : 0);
                ReactionsEffectOverlay.startAnimation();
            }
            chatActionCell.markReactionsAsRead();
        }
    }

    private void dimBehindView(View view, boolean z) {
        dimBehindView(view, false, z);
    }

    private void dimBehindView(View view, boolean z, boolean z2) {
        setScrimView(view);
        dimBehindView(z2 ? 0.2f : 0.0f, z, view != this.sideControlsButtonsLayout);
    }

    public void dimBehindView(View view, float f) {
        setScrimView(view);
        dimBehindView(f, false, view != this.sideControlsButtonsLayout);
    }

    public void setScrimView(View view) {
        View view2 = this.scrimView;
        if (view2 == view) {
            return;
        }
        if (view2 != null && (view2 instanceof ChatActionCell)) {
            ((ChatActionCell) view2).setInvalidateWithParent(null);
        }
        this.scrimView = view;
        if (view instanceof ChatActionCell) {
            ((ChatActionCell) view).setInvalidateWithParent(this.fragmentView);
        }
    }

    public void dimBehindView(boolean z) {
        dimBehindView(z ? 0.2f : 0.0f, false, true);
    }

    public void checkInstantCameraView() {
        if (this.instantCameraView == null && CameraView.isCameraAllowed() && getContext() != null) {
            InstantCameraView instantCameraView = new InstantCameraView(getContext(), this, this.themeDelegate, true) { // from class: org.telegram.ui.ChatActivity.63
                @Override // org.telegram.ui.Components.InstantCameraView
                public void startAnimation(boolean z, boolean z2) {
                    super.startAnimation(z, z2);
                    ChatActivity.this.animatorRoundMessageCameraVisibility.setValue(z, true);
                }
            };
            this.instantCameraView = instantCameraView;
            instantCameraView.setClipToPadding(false);
            this.instantCameraView.setButtonsBackground(this.glassBackgroundDrawableFactory, this.blurredBackgroundColorProvider);
            int iIndexOfChild = this.contentView.indexOfChild(this.chatInputViewsContainer);
            if (iIndexOfChild < 0) {
                iIndexOfChild = this.contentView.getChildCount();
            }
            this.contentView.addView(this.instantCameraView, Math.min(iIndexOfChild + 1, this.contentView.getChildCount()), LayoutHelper.createFrame(-1, -1, 51));
        }
    }

    private void dimBehindView(float f, boolean z, boolean z2) {
        ValueAnimator valueAnimatorOfFloat;
        ChatActivitySideControlsButtonsLayout chatActivitySideControlsButtonsLayout;
        final boolean z3 = f > 0.0f;
        View view = this.scrimView;
        if (view instanceof ChatMessageCell) {
            ChatMessageCell chatMessageCell = (ChatMessageCell) view;
            chatMessageCell.setInvalidatesParent(z3);
            if (z3) {
                restartSticker(chatMessageCell);
            }
        }
        this.contentView.invalidate();
        this.chatListView.invalidate();
        AnimatorSet animatorSet = this.scrimAnimatorSet;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.scrimAnimatorSet.cancel();
        }
        this.scrimAnimatorSet = new AnimatorSet();
        ArrayList arrayList = new ArrayList();
        final float fMax = Math.max(this.scrimPaintAlpha, f);
        if (z3) {
            this.scrimViewAlpha = 1.0f;
            this.scrimViewProgress = 0.0f;
            ValueAnimator valueAnimator = this.scrimViewAlphaAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.scrimProgressDirection = true;
            valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, f);
            arrayList.add(valueAnimatorOfFloat);
            if (z) {
                ScrimOptions.makeGlobalBlurBitmaps(new Utilities.Callback2() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda289
                    @Override 
                    public final void run(Object obj, Object obj2) {
                        this.f$0.lambda$dimBehindView$129((Bitmap) obj, (Bitmap) obj2);
                    }
                });
            }
        } else {
            float f2 = this.scrimPaintAlpha;
            this.scrimViewProgress = f2 / fMax;
            this.scrimProgressDirection = false;
            valueAnimatorOfFloat = ValueAnimator.ofFloat(f2, 0.0f);
            arrayList.add(valueAnimatorOfFloat);
        }
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda290
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.lambda$dimBehindView$130(fMax, valueAnimator2);
            }
        });
        if ((!z3 || z2) && (chatActivitySideControlsButtonsLayout = this.sideControlsButtonsLayout) != null) {
            arrayList.add(ObjectAnimator.ofFloat(chatActivitySideControlsButtonsLayout, (Property<ChatActivitySideControlsButtonsLayout, Float>) View.ALPHA, z3 ? 0.0f : 1.0f));
        }
        this.scrimAnimatorSet.playTogether(arrayList);
        this.scrimAnimatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
        this.scrimAnimatorSet.setDuration(z3 ? 150L : 220L);
        View view2 = this.scrimView;
        final ChatMessageCell chatMessageCell2 = view2 instanceof ChatMessageCell ? (ChatMessageCell) view2 : null;
        this.scrimAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatActivity.64
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (z3) {
                    return;
                }
                if (ChatActivity.this.scrimBlurBitmap != null) {
                    ChatActivity.this.scrimBlurBitmapShader = null;
                    ChatActivity.this.scrimBlurBitmapPaint = null;
                    ChatActivity.this.scrimBlurBitmap.recycle();
                    ChatActivity.this.scrimBlurBitmap = null;
                }
                ChatMessageCell chatMessageCell3 = chatMessageCell2;
                if (chatMessageCell3 != null) {
                    chatMessageCell3.invalidate();
                }
                ChatActivity.this.setScrimView(null);
                ChatActivity.this.scrimViewTask = null;
                ChatActivity.this.scrimViewReaction = null;
                ChatActivity.this.contentView.invalidate();
                ChatActivity.this.chatListView.invalidate();
            }
        });
        if (this.scrimView != null && this.scrimViewAlpha <= 0.0f) {
            setScrimView(null);
        }
        this.scrimAnimatorSet.start();
    }

    public MessagePreviewParams val$previewParams;

        void lambda$onFullDismiss$0() {
            ChatActivityEnterView chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
            if (chatActivityEnterView != null) {
                chatActivityEnterView.freezeEmojiView(false);
            }
        }

        @Override 
        public void onQuoteSelectedPart() {
            if (ChatActivity.this.replyingQuote != null && ChatActivity.this.replyingQuote.message != null) {
                ChatActivity chatActivity = ChatActivity.this;
                ReplyQuote replyQuote = chatActivity.messagePreviewParams.quote;
                if (replyQuote == null || replyQuote.message == null || chatActivity.replyingQuote.message.getId() == ChatActivity.this.messagePreviewParams.quote.message.getId()) {
                    return;
                }
            }
            ChatActivity chatActivity2 = ChatActivity.this;
            chatActivity2.replyingQuote = chatActivity2.messagePreviewParams.quote;
        }

        @Override 
        public void onDismiss(boolean z) {
            ChatActivity chatActivity = ChatActivity.this;
            chatActivity.forwardingPreviewView = null;
            chatActivity.checkShowBlur(true);
            ChatActivity chatActivity2 = ChatActivity.this;
            if (chatActivity2.messagePreviewParams != null) {
                if (chatActivity2.replyingQuote == null) {
                    ChatActivity chatActivity3 = ChatActivity.this;
                    chatActivity3.replyingQuote = chatActivity3.messagePreviewParams.quote;
                }
                ChatActivity chatActivity4 = ChatActivity.this;
                if (chatActivity4.messagePreviewParams.quote == null) {
                    chatActivity4.replyingQuote = null;
                }
                if (ChatActivity.this.replyingQuote != null) {
                    ChatActivity.this.replyingQuote.outdated = false;
                    ReplyQuote replyQuote = ChatActivity.this.replyingQuote;
                    ChatActivity chatActivity5 = ChatActivity.this;
                    replyQuote.start = chatActivity5.messagePreviewParams.quoteStart;
                    ReplyQuote replyQuote2 = chatActivity5.replyingQuote;
                    ChatActivity chatActivity6 = ChatActivity.this;
                    replyQuote2.end = chatActivity6.messagePreviewParams.quoteEnd;
                    chatActivity6.replyingQuote.update();
                    if (ChatActivity.this.fieldPanelShown == 2) {
                        ChatActivity chatActivity7 = ChatActivity.this;
                        chatActivity7.showFieldPanelForReplyQuote(chatActivity7.replyingMessageObject, ChatActivity.this.replyingQuote);
                    }
                } else {
                    ArrayList<MessageObject> arrayList = new ArrayList<>();
                    MessagePreviewParams.Messages messages = ChatActivity.this.messagePreviewParams.forwardMessages;
                    if (messages != null) {
                        messages.getSelectedMessages(arrayList);
                    }
                    ChatActivity.this.fallbackFieldPanel();
                }
            }
            ChatActivity.this.forbidForwardingWithDismiss = false;
            if (ChatActivity.this.keyboardWasVisible && z) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$65$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onDismiss$1();
                    }
                }, 50L);
                ChatActivity.this.keyboardWasVisible = false;
            }
            AndroidUtilities.requestAdjustResize(ChatActivity.this.getParentActivity(), ((BaseFragment) ChatActivity.this).classGuid);
        }

        public @Override 
        public void selectAnotherChat(boolean z) {
            MessagePreviewParams.Messages messages;
            dismiss(false);
            ChatActivity chatActivity = ChatActivity.this;
            if (chatActivity.messagePreviewParams != null) {
                if (!z) {
                    chatActivity.ignoreDraft = true;
                }
                ChatActivity chatActivity2 = ChatActivity.this;
                chatActivity2.setForwardParams(chatActivity2.messagePreviewParams.hideForwardSendersName);
                ArrayList<MessageObject> arrayList = new ArrayList<>();
                MessagePreviewParams.Messages messages2 = ChatActivity.this.messagePreviewParams.forwardMessages;
                if (messages2 != null) {
                    messages2.getSelectedMessages(arrayList);
                }
                ChatActivity.this.selectedMessagesIds[0].clear();
                ChatActivity.this.selectedMessagesIds[1].clear();
                int size = arrayList.size();
                int i = 0;
                int i2 = 0;
                boolean z2 = false;
                while (true) {
                    int i3 = 3;
                    if (i >= size) {
                        break;
                    }
                    MessageObject messageObject = arrayList.get(i);
                    if (messageObject.isTodo()) {
                        i2 = i3;
                    } else if (messageObject.isPoll()) {
                        i3 = 2;
                        if (i2 < 2) {
                            if (messageObject.isPublicPoll()) {
                                i2 = i3;
                            } else {
                                i2 = 1;
                            }
                        }
                    } else if (messageObject.isInvoice()) {
                        z2 = true;
                    }
                    ChatActivity.this.selectedMessagesIds[messageObject.getDialogId() == ChatActivity.this.dialog_id ? (char) 0 : (char) 1].put(messageObject.getId(), messageObject);
                    i++;
                }
                Bundle bundle = new Bundle();
                bundle.putBoolean("onlySelect", true);
                bundle.putInt("dialogsType", 3);
                bundle.putBoolean("quote", !z);
                boolean z3 = (z || (messages = ChatActivity.this.messagePreviewParams.replyMessage) == null || messages.messages.isEmpty() || ChatActivity.this.messagePreviewParams.quote != null) ? false : true;
                bundle.putBoolean("reply_to", z3);
                if (z3) {
                    long peerDialogId = DialogObject.getPeerDialogId(ChatActivity.this.messagePreviewParams.replyMessage.messages.get(0).getFromPeer());
                    if (peerDialogId != 0 && peerDialogId != ChatActivity.this.getDialogId() && peerDialogId != ChatActivity.this.getUserConfig().getClientUserId() && peerDialogId > 0) {
                        bundle.putLong("reply_to_author", peerDialogId);
                    }
                }
                bundle.putInt("hasPoll", i2);
                bundle.putBoolean("hasInvoice", z2);
                bundle.putInt("messagesCount", arrayList.size());
                bundle.putBoolean("canSelectTopics", true);
                DialogsActivity dialogsActivity = new DialogsActivity(bundle);
                dialogsActivity.setDelegate(ChatActivity.this);
                ChatActivity.this.presentFragment(dialogsActivity);
            }
        }

        @Override 
        public void didSendPressed() {
            super.didSendPressed();
            dismiss(true);
            ChatActivity.this.chatActivityEnterView.getSendButton().callOnClick();
        }
    }

    public void animateToNextChat() {
        ChatPullingDownDrawable chatPullingDownDrawable = this.pullingDownDrawable;
        if (chatPullingDownDrawable == null) {
            return;
        }
        if (this.isTopic) {
            if (chatPullingDownDrawable.getTopic() != null) {
                addToPulledTopicsMyself();
                addToPulledDialogs(this.currentChat, this.pullingDownDrawable.nextTopic, this.dialog_id, this.dialogFolderId, this.dialogFilterId);
                Bundle bundle = new Bundle();
                bundle.putInt("dialog_folder_id", this.pullingDownDrawable.dialogFolderId);
                bundle.putInt("dialog_filter_id", this.pullingDownDrawable.dialogFilterId);
                bundle.putBoolean("pulled", true);
                ChatActivity chatActivityForTopic = ForumUtilities.getChatActivityForTopic(this, -this.dialog_id, this.pullingDownDrawable.getTopic(), 0, bundle);
                chatActivityForTopic.setPullingDownTransition(true);
                replacingChatActivity = true;
                presentFragment(chatActivityForTopic, true);
                return;
            }
            return;
        }
        if (chatPullingDownDrawable.getChatId() != 0) {
            addToPulledDialogsMyself();
            ChatPullingDownDrawable chatPullingDownDrawable2 = this.pullingDownDrawable;
            addToPulledDialogs(chatPullingDownDrawable2.nextChat, null, chatPullingDownDrawable2.nextDialogId, chatPullingDownDrawable2.dialogFolderId, chatPullingDownDrawable2.dialogFilterId);
            Bundle bundle2 = new Bundle();
            bundle2.putLong("chat_id", this.pullingDownDrawable.getChatId());
            bundle2.putInt("dialog_folder_id", this.pullingDownDrawable.dialogFolderId);
            bundle2.putInt("dialog_filter_id", this.pullingDownDrawable.dialogFilterId);
            bundle2.putBoolean("pulled", true);
            MessagesController.getNotificationsSettings(this.currentAccount).edit().remove("diditem" + this.pullingDownDrawable.nextDialogId).apply();
            ChatActivity chatActivity = new ChatActivity(bundle2);
            ArrayList<TLRPC.Chat> arrayList = this.nextChannels;
            if (arrayList != null && arrayList.size() > 1) {
                ArrayList<TLRPC.Chat> arrayList2 = this.nextChannels;
                chatActivity.setNextChannels(new ArrayList<>(arrayList2.subList(1, arrayList2.size())));
            }
            chatActivity.setPullingDownTransition(true);
            replacingChatActivity = true;
            presentFragment(chatActivity, true);
        }
    }

    public void setNextChannels(ArrayList<TLRPC.Chat> arrayList) {
        this.nextChannels = arrayList;
    }

    private void addToPulledDialogsMyself() {
        if (getParentLayout() == null) {
            return;
        }
        BackButtonMenu.addToPulledDialogs(this, getParentLayout().getFragmentStack().indexOf(this), this.currentChat, this.currentUser, null, this.dialog_id, this.dialogFilterId, this.dialogFolderId);
    }

    private void addToPulledDialogs(TLRPC.Chat chat, TLRPC.TL_forumTopic tL_forumTopic, long j, int i, int i2) {
        if (getParentLayout() == null) {
            return;
        }
        BackButtonMenu.addToPulledDialogs(this, getParentLayout().getFragmentStack().indexOf(this), chat, null, tL_forumTopic, j, i, i2);
    }

    private void addToPulledTopicsMyself() {
        if (getParentLayout() == null) {
            return;
        }
        BackButtonMenu.addToPulledDialogs(this, getParentLayout().getFragmentStack().indexOf(this), this.currentChat, this.currentUser, this.forumTopic, this.dialog_id, this.dialogFilterId, this.dialogFolderId);
    }

    private void setPullingDownTransition(boolean z) {
        this.fromPullingDownTransition = z;
    }

    public void setSwitchFromTopics(boolean z) {
        this.switchFromTopics = z;
    }

    public void updateBulletinLayout() {
        Bulletin visibleBulletin = Bulletin.getVisibleBulletin();
        if (visibleBulletin == null || this.bulletinDelegate == null) {
            return;
        }
        visibleBulletin.updatePosition();
    }

    private void searchUserMessages(TLRPC.User user, TLRPC.Chat chat) {
        String strSubstring;
        this.searchingUserMessages = user;
        this.searchingChatMessages = chat;
        if (this.searchItem == null || this.mentionContainer == null) {
            return;
        }
        if (user == null && chat == null) {
            return;
        }
        if (user != null) {
            strSubstring = user.first_name;
            if (TextUtils.isEmpty(strSubstring)) {
                strSubstring = this.searchingUserMessages.last_name;
            }
        } else {
            strSubstring = chat.title;
        }
        if (strSubstring == null) {
            return;
        }
        if (strSubstring.length() > 10) {
            strSubstring = strSubstring.substring(0, 10);
        }
        this.searchingForUser = false;
        String string = LocaleController.getString(R.string.SearchFrom);
        SpannableString spannableString = new SpannableString(string + " " + strSubstring);
        spannableString.setSpan(new ForegroundColorSpan(getThemedColor(Theme.key_actionBarDefaultSubtitle)), string.length() + 1, spannableString.length(), 33);
        this.searchItem.setSearchFieldCaption(spannableString);
        this.mentionContainer.getAdapter().lambda$searchUsernameOrHashtag$8(null, 0, null, false, true);
        this.searchItem.setSearchFieldHint(null);
        this.searchItem.clearSearchText();
        MediaDataController mediaDataController = getMediaDataController();
        this.searchingQuery = _UrlKt.FRAGMENT_ENCODE_SET;
        mediaDataController.searchMessagesInChat(_UrlKt.FRAGMENT_ENCODE_SET, this.dialog_id, this.mergeDialogId, this.classGuid, 0, this.threadMessageId, this.searchingUserMessages, this.searchingChatMessages, this.searchingReaction);
    }

    private void updateTranslateItemVisibility() {
        ActionBarMenuItem.Item item = this.translateItem;
        if (item == null) {
            return;
        }
        item.setVisibility((getMessagesController().getTranslateController().isTranslateDialogHidden(getDialogId()) && getMessagesController().getTranslateController().isDialogTranslatable(getDialogId())) ? 0 : 8);
    }

    public boolean lambda$createPinnedMessageView$138(View view) {
        setPinVisibility(false);
        return true;
    }

    private void openAnotherForward() {
        MessagePreviewParams.Messages messages;
        ArrayList<MessageObject> arrayList;
        String string;
        MessagePreviewParams messagePreviewParams = this.messagePreviewParams;
        if (messagePreviewParams == null || messagePreviewParams.isEmpty() || (messages = this.messagePreviewParams.forwardMessages) == null || (arrayList = messages.messages) == null) {
            return;
        }
        int size = arrayList.size();
        long fromChatId = 0;
        long dialogId = 0;
        for (int i = 0; i < size; i++) {
            MessageObject messageObject = this.messagePreviewParams.forwardMessages.messages.get(i);
            if (fromChatId != 0) {
                if (fromChatId != messageObject.getFromChatId()) {
                    break;
                }
            } else {
                dialogId = messageObject.getDialogId();
                fromChatId = messageObject.getFromChatId();
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.themeDelegate);
        builder.setButtonsVertical(true);
        if (dialogId > 0) {
            TLRPC.User user = getMessagesController().getUser(Long.valueOf(dialogId));
            if (user == null) {
                return;
            } else {
                string = LocaleController.formatString("CancelForwardPrivate", R.string.CancelForwardPrivate, LocaleController.formatPluralString("MessagesBold", this.messagePreviewParams.forwardMessages.messages.size(), new Object[0]), ContactsController.formatName(user.first_name, user.last_name));
            }
        } else {
            TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(-dialogId));
            if (chat == null) {
                return;
            } else {
                string = LocaleController.formatString("CancelForwardChat", R.string.CancelForwardChat, LocaleController.formatPluralString("MessagesBold", this.messagePreviewParams.forwardMessages.messages.size(), new Object[0]), chat.title);
            }
        }
        builder.setMessage(AndroidUtilities.replaceTags(string));
        builder.setTitle(LocaleController.formatPluralString("messages", this.messagePreviewParams.forwardMessages.messages.size(), new Object[0]));
        builder.setPositiveButton(LocaleController.getString(R.string.CancelForwarding), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda158
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i2) {
                this.f$0.lambda$openAnotherForward$139(alertDialog, i2);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.ShowForwardingOptions), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda159
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i2) {
                this.f$0.lambda$openAnotherForward$140(alertDialog, i2);
            }
        });
        AlertDialog alertDialogCreate = builder.create();
        showDialog(alertDialogCreate);
        TextView textView = (TextView) alertDialogCreate.getButton(-1);
        if (textView != null) {
            textView.setTextColor(getThemedColor(Theme.key_text_RedBold));
        }
    }

    public void lambda$openAnotherForward$140(AlertDialog alertDialog, int i) {
        openForwardingPreview(1);
    }

    public void openPinnedMessagesList(boolean z) {
        INavigationLayout iNavigationLayout;
        if (getParentActivity() == null || (iNavigationLayout = this.parentLayout) == null || iNavigationLayout.getLastFragment() != this || this.pinnedMessageIds.isEmpty()) {
            return;
        }
        Bundle bundle = new Bundle();
        TLRPC.Chat chat = this.currentChat;
        if (chat != null) {
            bundle.putLong("chat_id", chat.id);
        } else {
            bundle.putLong("user_id", this.currentUser.id);
        }
        bundle.putInt("chatMode", 2);
        ChatActivity chatActivity = new ChatActivity(bundle);
        chatActivity.pinnedMessageIds = new ArrayList<>(this.pinnedMessageIds);
        chatActivity.pinnedMessageObjects = new HashMap<>(this.pinnedMessageObjects);
        int size = this.pinnedMessageIds.size();
        for (int i = 0; i < size; i++) {
            Integer num = this.pinnedMessageIds.get(i);
            MessageObject messageObject = this.pinnedMessageObjects.get(num);
            MessageObject messageObject2 = this.messagesDict[0].get(num.intValue());
            if (messageObject == null) {
                messageObject = messageObject2;
            } else if (messageObject2 != null) {
                messageObject.mediaExists = messageObject2.mediaExists;
                messageObject.attachPathExists = messageObject2.attachPathExists;
            }
            if (messageObject != null) {
                chatActivity.pinnedMessageObjects.put(num, messageObject);
                chatActivity.updatePinnedTopicStarterMessage();
            }
        }
        chatActivity.loadedPinnedMessagesCount = this.loadedPinnedMessagesCount;
        chatActivity.totalPinnedMessagesCount = this.isTopic ? this.pinnedMessageIds.size() : this.totalPinnedMessagesCount;
        chatActivity.pinnedEndReached = this.pinnedEndReached;
        chatActivity.maxPinnedMessageId = this.maxPinnedMessageId;
        chatActivity.userInfo = this.userInfo;
        chatActivity.chatInfo = this.chatInfo;
        chatActivity.chatActivityDelegate = new AnonymousClass71(chatActivity);
        if (z) {
            presentFragmentAsPreview(chatActivity);
            checkShowBlur(true);
        } else {
            presentFragment(chatActivity, false);
        }
    }

    public class AnonymousClass71 implements ChatActivityDelegate {
        final void lambda$onUnpin$0(boolean z, ArrayList arrayList, ArrayList arrayList2, int i, int i2) {
            ChatActivity chatActivity = ChatActivity.this;
            if (z) {
                MessagesController.getNotificationsSettings(((BaseFragment) chatActivity).currentAccount).edit().remove("pin_" + ChatActivity.this.dialog_id).apply();
                ChatActivity.this.updatePinnedMessageView(true);
            } else {
                chatActivity.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didLoadPinnedMessages, Long.valueOf(ChatActivity.this.dialog_id), arrayList, Boolean.TRUE, arrayList2, null, 0, Integer.valueOf(i), Boolean.valueOf(ChatActivity.this.pinnedEndReached));
            }
            if (i2 == ChatActivity.this.pinBullerinTag) {
                ChatActivity.this.pinBulletin = null;
            }
        }

        public void lambda$checkShowBlur$141(View view) {
        finishPreviewFragment();
    }

    public void lambda$hideInfoView$143(View view, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.topViewOffset = AndroidUtilities.dp(30.0f) * fFloatValue;
        invalidateChatListViewTopPadding();
        invalidateMessagesVisiblePart();
        view.setAlpha(fFloatValue);
    }

    public void updateChatListViewTopPadding() {
        TranslateButton translateButton;
        ChatActivityEnterView chatActivityEnterView;
        if (!this.invalidateChatListViewTopPadding || this.chatListView == null) {
            return;
        }
        float measuredHeight = 0.0f;
        if (this.fixedKeyboardHeight <= 0 || this.searchExpandProgress != 0.0f) {
            float topPanelHeightWithPadding = getTopPanelHeightWithPadding(AndroidUtilities.dp(7.0f));
            ChatActivity chatActivity = this.parentChatActivity;
            float finalTopPanelHeight = ChatHeaderUiHelper.getFinalTopPanelHeight(topPanelHeightWithPadding, chatActivity != null ? chatActivity.topPanelLayout : this.topPanelLayout);
            SearchTagsList searchTagsList = this.actionBarSearchTags;
            float fDp = finalTopPanelHeight + (searchTagsList != null ? AndroidUtilities.dp(searchTagsList.shownT * 35.0f) : 0) + (AndroidUtilities.dp(43.0f) * getHashtagTabsShownT());
            float f = this.chatListViewPaddingTop;
            float fDp2 = AndroidUtilities.dp(4.0f) + this.contentPaddingTop;
            this.paddingTopHeight = fDp;
            this.chatListViewPaddingTop = fDp2 + fDp + getTopicTabsSideSize(TopicsTabsView.Position.TOP);
            if (this.actionBar.getVisibility() == 0 || this.parentChatActivity != null) {
                this.chatListViewPaddingTop += this.actionBar.getMeasuredHeight();
            }
            float f2 = this.chatListViewPaddingTop + this.blurredViewTopOffset;
            this.chatListViewPaddingVisibleOffset = 0;
            this.chatListViewPaddingTop = f2 + this.contentPanTranslation;
            if (this.searchExpandProgress != 0.0f && (chatActivityEnterView = this.chatActivityEnterView) != null && chatActivityEnterView.getVisibility() == 0) {
                float f3 = this.chatListViewPaddingTop;
                measuredHeight = this.searchExpandProgress * (this.chatActivityEnterView.getMeasuredHeight() - AndroidUtilities.dp(48.0f));
                this.chatListViewPaddingTop = f3 - measuredHeight;
            }
            ChatActionCell chatActionCell = this.infoTopView;
            if (chatActionCell != null) {
                chatActionCell.setTranslationY(((this.chatListView.getTranslationY() + this.chatListViewPaddingTop) + this.topViewOffset) - AndroidUtilities.dp(30.0f));
                float f4 = this.chatListViewPaddingTop;
                float f5 = this.topViewOffset;
                this.chatListViewPaddingTop = f4 + f5;
                this.chatListViewPaddingVisibleOffset = (int) (this.chatListViewPaddingVisibleOffset + f5);
            }
            ChatActionCell chatActionCell2 = this.floatingDateView;
            if (chatActionCell2 != null) {
                chatActionCell2.setTranslationY((((this.chatListView.getTranslationY() - measuredHeight) + this.chatListViewPaddingTop) + this.floatingDateViewOffset) - AndroidUtilities.dp(4.0f));
            }
            updateFloatingTopicView();
            ChatListRecyclerView chatListRecyclerView = this.chatListView;
            if (chatListRecyclerView != null && this.chatLayoutManager != null && this.chatAdapter != null) {
                int paddingTop = chatListRecyclerView.getPaddingTop();
                int paddingBottom = this.chatListView.getPaddingBottom();
                checkUi_chatListViewPaddings();
                if (this.chatListView.getPaddingTop() != paddingTop || this.chatListView.getPaddingBottom() != paddingBottom) {
                    invalidateMessagesVisiblePart();
                    invalidateMergedVisibleBlurredPositionsAndSourcesPositions();
                }
                this.chatListView.setTopGlowOffset((int) ((this.chatListViewPaddingTop - this.chatListViewPaddingVisibleOffset) - AndroidUtilities.dp(4.0f)));
                if (f != this.chatListViewPaddingTop) {
                    int childCount = this.chatListView.getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View childAt = this.chatListView.getChildAt(i);
                        if (this.chatListView.getChildAdapterPosition(childAt) == this.chatAdapter.getItemCount() - 1) {
                            float f6 = this.chatListViewPaddingTop;
                            if (childAt.getTop() <= f6) {
                                break;
                            }
                            this.chatListView.scrollBy(0, (int) (childAt.getTop() - f6));
                            break;
                        }
                    }
                }
                if (!isThreadChat() && !this.wasManualScroll && this.unreadMessageObject != null && this.chatListView != null && ((translateButton = this.translateButton) == null || translateButton.getVisibility() != 0)) {
                    this.chatListView.scrollBy(0, (int) (f - this.chatListViewPaddingTop));
                }
            }
            this.invalidateChatListViewTopPadding = false;
            Bulletin.updateCurrentPosition();
            checkUi_chatListViewPaddings();
        }
    }

    private void checkUi_chatListViewPaddings() {
        float fDp;
        if (this.chatListView == null) {
            return;
        }
        if (this.isInsideContainer && this.parentChatActivity == null) {
            fDp = AndroidUtilities.navigationBarHeight;
        } else {
            fDp = this.blurredViewBottomOffset + AndroidUtilities.dp(16.0f) + this.inputIslandHeightCurrent + getTopicTabsSideSize(TopicsTabsView.Position.BOTTOM) + this.windowInsetsStateHolder.getAnimatedMaxBottomInset();
        }
        int i = (int) this.chatListViewPaddingTop;
        TopicsTabsView topicsTabsView = this.topicsTabs;
        if (topicsTabsView != null) {
            topicsTabsView.setSideMenuBackgroundMarginTop(0.0f);
        }
        this.chatListViewPaddingsAnimator.setPaddings(i, fDp, !this.chatListView.fastScrollAnimationRunning);
        ChatActivityMessageMetricsView chatActivityMessageMetricsView = this.messageMetricsView;
        if (chatActivityMessageMetricsView != null) {
            chatActivityMessageMetricsView.setViewportPadding(getTopicTabsSideSize(TopicsTabsView.Position.LEFT), i - this.recommendedAdditionalSizeY, 0.0f, fDp - this.blurredViewBottomOffset);
        }
    }

    public void invalidateChatListViewTopPadding() {
        if (!this.invalidateChatListViewTopPadding) {
            this.invalidateChatListViewTopPadding = true;
            ChatActivityFragmentView chatActivityFragmentView = this.contentView;
            if (chatActivityFragmentView != null) {
                chatActivityFragmentView.invalidate();
            }
            ChatListRecyclerView chatListRecyclerView = this.chatListView;
            if (chatListRecyclerView != null) {
                chatListRecyclerView.invalidate();
            }
        }
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.setAdditionalTranslationY(this.windowInsetsStateHolder.getAnimatedMaxBottomInset() + AndroidUtilities.dp(16.0f) + this.chatInputViewsContainer.getInputBubbleHeight() + getTopicTabsSideSize(TopicsTabsView.Position.BOTTOM));
        }
        checkUi_topPanelPositions();
    }

    public void checkUi_topPanelPositions() {
        float fDp = this.contentPanTranslation;
        SearchTagsList searchTagsList = this.actionBarSearchTags;
        if (searchTagsList != null) {
            searchTagsList.setTranslationY(fDp);
            fDp += AndroidUtilities.dp(35.0f) * this.actionBarSearchTags.shownT;
        }
        ChatSearchTabs chatSearchTabs = this.hashtagSearchTabs;
        if (chatSearchTabs != null) {
            chatSearchTabs.setTranslationY(fDp);
        }
        float fDp2 = fDp + (AndroidUtilities.dp(43.0f) * getHashtagTabsShownT());
        TopicsTabsView topicsTabsView = this.topicsTabs;
        if (topicsTabsView != null) {
            float topPanelHeightWithPadding = getTopPanelHeightWithPadding(AndroidUtilities.dp(7.0f));
            ChatActivity chatActivity = this.parentChatActivity;
            topicsTabsView.setSideMenuBackgroundMarginTop((ChatHeaderUiHelper.getFinalTopPanelHeight(topPanelHeightWithPadding, chatActivity != null ? chatActivity.topPanelLayout : this.topPanelLayout) * getHashtagTabsShownT()) + fDp2);
            fDp2 += getTopicTabsSideSize(TopicsTabsView.Position.TOP) * FBool.or(FBool.not(this.animatorSearchResultAsListVisibility.getFloatValue()), getHashtagTabsShownT());
        }
        ChatActivityTopPanelLayout chatActivityTopPanelLayout = this.topPanelLayout;
        if (chatActivityTopPanelLayout != null) {
            chatActivityTopPanelLayout.setTranslationY(ChatHeaderUiHelper.getTopPanelTranslationY(fDp2, getTopicTabsSideSize(TopicsTabsView.Position.TOP), getHashtagTabsShownT()));
        }
    }

    public TextureView createTextureView(boolean z) {
        if (this.parentLayout == null) {
            return null;
        }
        AndroidUtilities.cancelRunOnUIThread(this.destroyTextureViewRunnable);
        if (this.videoPlayerContainer == null) {
            FrameLayout frameLayout = new FrameLayout(getParentActivity()) { // from class: org.telegram.ui.ChatActivity.76
                @Override // android.view.View
                public void setTranslationY(float f) {
                    super.setTranslationY(f);
                    ChatActivity.this.contentView.invalidate();
                }
            };
            this.videoPlayerContainer = frameLayout;
            frameLayout.setOutlineProvider(new ViewOutlineProvider() { // from class: org.telegram.ui.ChatActivity.77
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    ImageReceiver imageReceiver = (ImageReceiver) view.getTag(R.id.parent_tag);
                    if (imageReceiver != null) {
                        int[] roundRadius = imageReceiver.getRoundRadius();
                        int iMax = 0;
                        for (int i = 0; i < 4; i++) {
                            iMax = Math.max(iMax, roundRadius[i]);
                        }
                        outline.setRoundRect(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight(), iMax);
                        return;
                    }
                    outline.setOval(0, 0, AndroidUtilities.roundPlayingMessageSize(ChatActivity.this.isSideMenued()), AndroidUtilities.roundPlayingMessageSize(ChatActivity.this.isSideMenued()));
                }
            });
            this.videoPlayerContainer.setClipToOutline(true);
            this.videoPlayerContainer.setWillNotDraw(false);
            AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(getParentActivity());
            this.aspectRatioFrameLayout = aspectRatioFrameLayout;
            aspectRatioFrameLayout.setBackgroundColor(0);
            if (z) {
                this.videoPlayerContainer.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1, 17));
            }
            TextureView textureView = new TextureView(getParentActivity());
            this.videoTextureView = textureView;
            textureView.setOpaque(false);
            this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1.0f));
        }
        ViewGroup viewGroup = (ViewGroup) this.videoPlayerContainer.getParent();
        if (viewGroup != null && viewGroup != this.contentView) {
            viewGroup.removeView(this.videoPlayerContainer);
            viewGroup = null;
        }
        if (viewGroup == null) {
            this.contentView.addView(this.videoPlayerContainer, 1, new FrameLayout.LayoutParams(AndroidUtilities.roundPlayingMessageSize(isSideMenued()), AndroidUtilities.roundPlayingMessageSize(isSideMenued())));
        }
        this.videoPlayerContainer.setTag(null);
        this.aspectRatioFrameLayout.setDrawingReady(false);
        return this.videoTextureView;
    }

    public void destroyTextureView() {
        FrameLayout frameLayout = this.videoPlayerContainer;
        if (frameLayout == null || frameLayout.getParent() == null) {
            return;
        }
        this.chatListView.invalidateViews();
        this.aspectRatioFrameLayout.setDrawingReady(false);
        this.videoPlayerContainer.setTag(null);
        this.contentView.removeView(this.videoPlayerContainer);
    }

    public boolean hasSelectedMessages() {
        return this.selectedMessagesIds[0].size() > 0 || this.selectedMessagesIds[1].size() > 0;
    }

    private boolean hasSelectedNoforwardsMessage() {
        TLRPC.Message message;
        try {
            for (SparseArrayWithTouch<MessageObject> sparseArrayWithTouch : this.selectedMessagesIds) {
                for (int i = 0; i < sparseArrayWithTouch.size(); i++) {
                    MessageObject messageObjectValueAt = sparseArrayWithTouch.valueAt(i);
                    if (messageObjectValueAt != null && (message = messageObjectValueAt.messageOwner) != null && message.noforwards) {
                        return true;
                    }
                }
            }
        } catch (Exception unused) {
        }
        return false;
    }

    public void share() {
        MessageObject messageObject = null;
        for (int i = 1; i >= 0; i--) {
            if (messageObject == null && this.selectedMessagesIds[i].size() != 0) {
                messageObject = this.messagesDict[i].get(this.selectedMessagesIds[i].keyAt(0));
            }
            this.selectedMessagesIds[i].clear();
            this.selectedMessagesCanCopyIds[i].clear();
            this.selectedMessagesCanStarIds[i].clear();
        }
        if (getParentActivity() != null && messageObject != null && ((messageObject.isVoice() || messageObject.isRoundVideo()) && messageObject.getDocument() != null)) {
            File pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(messageObject.getDocument(), null, false, true);
            TLRPC.Message message = messageObject.messageOwner;
            if (message != null && message.attachPath != null && (pathToAttach == null || !pathToAttach.exists())) {
                pathToAttach = new File(messageObject.messageOwner.attachPath);
            }
            if (pathToAttach != null && pathToAttach.exists()) {
                Intent intent = new Intent("android.intent.action.SEND");
                if (messageObject.isVideo() || messageObject.isRoundVideo()) {
                    intent.setType("video/mp4");
                } else {
                    intent.setType(messageObject.getMimeType());
                }
                try {
                    intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(getParentActivity(), ApplicationLoader.getApplicationId() + ".provider", pathToAttach));
                    intent.setFlags(1);
                } catch (Exception unused) {
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(pathToAttach));
                }
                getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString(R.string.ShareFile)), MediaError.DetailedErrorCode.SEGMENT_UNKNOWN);
            }
        }
        hideActionMode();
        updatePinnedMessageView(true);
        updateVisibleRows();
        updateSelectedMessageReactions();
    }

    @Override 
    public ForwardContext.ForwardParams getForwardParams() {
        return this.forwardParams;
    }

    @Override 
    public void setForwardParams(boolean z) {
        super.setForwardParams(z);
    }

    @Override 
    public boolean isForwardNoQuote() {
        return getForwardParams().noQuote;
    }

    public ArrayList<MessageObject> getForwardingMessages() {
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        if (this.forwardingMessage != null || this.selectedMessagesIds[0].size() != 0 || this.selectedMessagesIds[1].size() != 0) {
            MessageObject messageObject = this.forwardingMessage;
            if (messageObject == null) {
                for (int i = 1; i >= 0; i--) {
                    ArrayList arrayList2 = new ArrayList();
                    for (int i2 = 0; i2 < this.selectedMessagesIds[i].size(); i2++) {
                        arrayList2.add(Integer.valueOf(this.selectedMessagesIds[i].keyAt(i2)));
                    }
                    Collections.sort(arrayList2);
                    for (int i3 = 0; i3 < arrayList2.size(); i3++) {
                        addForwardingMessageObject(arrayList, this.selectedMessagesIds[i].get(((Integer) arrayList2.get(i3)).intValue()));
                    }
                }
            } else if (this.forwardingMessageGroup != null) {
                for (int i4 = 0; i4 < this.forwardingMessageGroup.messages.size(); i4++) {
                    addForwardingMessageObject(arrayList, this.forwardingMessageGroup.messages.get(i4));
                }
            } else {
                addForwardingMessageObject(arrayList, messageObject);
                return arrayList;
            }
        }
        return arrayList;
    }

    private boolean startContextForward(boolean z) {
        if (this.selectedObject == null || getMessagesController().isFrozen()) {
            if (getMessagesController().isFrozen()) {
                AccountFrozenAlert.show(this.currentAccount);
            }
            return true;
        }
        setForwardParams(z);
        this.forwardingMessage = this.selectedObject;
        this.forwardingMessageGroup = this.selectedObjectGroup;
        Bundle bundle = new Bundle();
        bundle.putBoolean("onlySelect", true);
        int i = 3;
        bundle.putInt("dialogsType", 3);
        MessageObject.GroupedMessages groupedMessages = this.forwardingMessageGroup;
        bundle.putInt("messagesCount", groupedMessages != null ? groupedMessages.messages.size() : 1);
        if (!this.forwardingMessage.isTodo()) {
            if (this.forwardingMessage.isPoll()) {
                i = this.forwardingMessage.isPublicPoll() ? 2 : 1;
            } else {
                i = 0;
            }
        }
        bundle.putInt("hasPoll", i);
        if (ChatObject.isMonoForum(this.currentChat) && ChatObject.canManageMonoForum(this.currentAccount, this.currentChat)) {
            long j = this.currentChat.linked_monoforum_id;
            if (j != 0) {
                bundle.putLong("forward_into_channel", -j);
            }
        }
        bundle.putBoolean("hasInvoice", this.forwardingMessage.isInvoice());
        bundle.putBoolean("canSelectTopics", true);
        writeForwardParams(bundle);
        DialogsActivity dialogsActivity = new DialogsActivity(bundle);
        dialogsActivity.setDelegate(this);
        presentFragment(dialogsActivity);
        return true;
    }

    void lambda$processInlineBotWebView$144(TLRPC.TL_inlineBotWebView tL_inlineBotWebView) {
        TLRPC.User foundContextBot = this.mentionContainer.getAdapter().getFoundContextBot();
        int i = this.currentAccount;
        TLRPC.User user = this.currentUser;
        WebViewRequestProps webViewRequestPropsOf = WebViewRequestProps.of(i, user != null ? user.id : this.currentChat.id, foundContextBot.id, tL_inlineBotWebView.text, tL_inlineBotWebView.url, 1, 0, getSendMonoForumPeerId(), false, null, false, null, null, 1, false, false);
        LaunchActivity launchActivity = LaunchActivity.instance;
        if (launchActivity == null || launchActivity.getBottomSheetTabs() == null || LaunchActivity.instance.getBottomSheetTabs().tryReopenTab(webViewRequestPropsOf) == null) {
            String restrictionReason = MessagesController.getInstance(this.currentAccount).getRestrictionReason(foundContextBot.restriction_reason);
            if (!TextUtils.isEmpty(restrictionReason)) {
                MessagesController.getInstance(this.currentAccount);
                MessagesController.showCantOpenAlert(this, restrictionReason);
                return;
            }
            BotWebViewSheet botWebViewSheet = new BotWebViewSheet(getContext(), getResourceProvider());
            botWebViewSheet.setDefaultFullsize(false);
            botWebViewSheet.setNeedsContext(true);
            botWebViewSheet.setParentActivity(getParentActivity());
            botWebViewSheet.requestWebView(this, webViewRequestPropsOf);
            botWebViewSheet.show();
        }
    }

    public void lambda$onEditTextDialogClose$146() {
        this.chatActivityEnterView.openKeyboard();
    }

    public void lambda$openDiscussionMessageChat$429(Runnable runnable) {
        NotificationCenter.getInstance(this.currentAccount).doOnIdle(runnable);
    }

    public void performHistoryClear(boolean z, boolean z2) {
        performHistoryClear(getThreadId(), z, z2);
    }

    public void performHistoryClear(final long j, final boolean z, boolean z2) {
        if (this.topicsTabs != null && UserObject.isBotForum(this.currentUser)) {
            this.topicsTabs.setAllTopicsHidden(true);
        }
        setClearingHistory(j, true);
        createUndoView();
        UndoView undoView = this.undoView;
        if (undoView == null) {
            return;
        }
        undoView.showWithAction(this.dialog_id, 0, new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda292
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$performHistoryClear$147(j, z);
            }
        }, new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda293
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$performHistoryClear$148(j);
            }
        });
        this.chatAdapter.notifyDataSetChanged();
    }

    public void lambda$shareMyContact$150(int i, MessageObject messageObject, AlertDialog alertDialog, int i2) {
        if (i == 1) {
            TLRPC.TL_contacts_acceptContact tL_contacts_acceptContact = new TLRPC.TL_contacts_acceptContact();
            tL_contacts_acceptContact.id = getMessagesController().getInputUser(this.currentUser);
            getConnectionsManager().sendRequest(tL_contacts_acceptContact, new RequestDelegate() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda403
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$shareMyContact$149(tLObject, tL_error);
                }
            });
            return;
        }
        SendMessagesHelper.SendMessageParams sendMessageParamsOf = SendMessagesHelper.SendMessageParams.of(getUserConfig().getCurrentUser(), this.dialog_id, messageObject, getThreadMessage(), (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, true, 0, 0);
        sendMessageParamsOf.quick_reply_shortcut_id = getQuickReplyId();
        sendMessageParamsOf.quick_reply_shortcut = this.quickReplyShortcut;
        SendMessagesHelper.getInstance(this.currentAccount).sendMessage(sendMessageParamsOf);
        if (this.chatMode == 0) {
            moveScrollToLastMessage(false);
        }
        hideFieldPanel(false);
    }

    public void lambda$onAnimationEnd$0() {
            if (ChatActivity.this.gifHintTextView == null) {
                return;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(ObjectAnimator.ofFloat(ChatActivity.this.gifHintTextView, (Property<HintView, Float>) View.ALPHA, 0.0f));
            animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatActivity.85.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (ChatActivity.this.gifHintTextView != null) {
                        ChatActivity.this.gifHintTextView.setVisibility(8);
                    }
                }
            });
            animatorSet.setDuration(300L);
            animatorSet.start();
        }
    }

    public void openAttachMenu() {
        if (getParentActivity() != null) {
            ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
            if (chatActivityEnterView == null || TextUtils.isEmpty(chatActivityEnterView.getSlowModeTimer())) {
                ChatActivityEnterView chatActivityEnterView2 = this.chatActivityEnterView;
                boolean z = chatActivityEnterView2 != null && chatActivityEnterView2.isEphemeralMessageVisible();
                createChatAttachView();
                ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
                chatAttachAlert.restrictEphemeralMessageTypes = z;
                chatAttachAlert.getPhotoLayout().loadGalleryPhotos();
                TLRPC.Chat chat = this.currentChat;
                if ((chat != null && this.messageSuggestionParams != null) || z) {
                    this.chatAttachAlert.setMaxSelectedPhotos(1, true);
                } else if (chat != null && !ChatObject.hasAdminRights(chat) && this.currentChat.slowmode_enabled) {
                    this.chatAttachAlert.setMaxSelectedPhotos(10, true);
                } else {
                    this.chatAttachAlert.setMaxSelectedPhotos(-1, true);
                }
                this.chatAttachAlert.enableDefaultMode();
                this.chatAttachAlert.init();
                this.chatAttachAlert.getCommentView().setText(this.chatActivityEnterView.getFieldText());
                ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
                chatAttachAlert2.parentThemeDelegate = this.themeDelegate;
                showDialog(chatAttachAlert2);
            }
        }
    }

    public void openAttachMenuForCreatingSticker() {
        ContentPreviewViewer.getInstance().setStickerSetForCustomSticker(null);
        if (getParentActivity() == null) {
            return;
        }
        createChatAttachView();
        this.chatAttachAlert.getPhotoLayout().loadGalleryPhotos();
        this.chatAttachAlert.setMaxSelectedPhotos(1, false);
        this.chatAttachAlert.setOpenWithFrontFaceCamera(true);
        this.chatAttachAlert.enableStickerMode(null);
        this.chatAttachAlert.init();
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        chatAttachAlert.parentThemeDelegate = this.themeDelegate;
        if (this.visibleDialog != null) {
            chatAttachAlert.show();
        } else {
            showDialog(chatAttachAlert);
        }
    }

    public void showFloatingDateView(boolean z) {
        ChatActionCell chatActionCell = this.floatingDateView;
        if (chatActionCell == null || this.chatMode == 5) {
            return;
        }
        if (chatActionCell.getTag() == null) {
            AnimatorSet animatorSet = this.floatingDateAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            this.floatingDateView.setTag(1);
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.floatingDateAnimation = animatorSet2;
            animatorSet2.setDuration(150L);
            this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(this.floatingDateView, (Property<ChatActionCell, Float>) View.ALPHA, 1.0f));
            this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatActivity.86
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ChatActivity.this.floatingDateAnimation)) {
                        ChatActivity.this.floatingDateAnimation = null;
                    }
                }
            });
            this.floatingDateAnimation.start();
        }
        if (z) {
            return;
        }
        invalidateMessagesVisiblePart();
        this.hideDateDelay = 1000;
    }

    public void hideFloatingDateView(boolean z) {
        if (this.floatingDateView.getTag() == null || this.currentFloatingDateOnScreen) {
            return;
        }
        if (!this.scrollingFloatingDate || this.currentFloatingTopIsNotMessage) {
            this.floatingDateView.setTag(null);
            if (z) {
                AnimatorSet animatorSet = new AnimatorSet();
                this.floatingDateAnimation = animatorSet;
                animatorSet.setDuration(150L);
                this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(this.floatingDateView, (Property<ChatActionCell, Float>) View.ALPHA, 0.0f));
                this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatActivity.87
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(ChatActivity.this.floatingDateAnimation)) {
                            ChatActivity.this.floatingDateAnimation = null;
                        }
                    }
                });
                this.floatingDateAnimation.setStartDelay(this.hideDateDelay);
                this.floatingDateAnimation.start();
            } else {
                AnimatorSet animatorSet2 = this.floatingDateAnimation;
                if (animatorSet2 != null) {
                    animatorSet2.cancel();
                    this.floatingDateAnimation = null;
                }
                this.floatingDateView.setAlpha(0.0f);
            }
            this.hideDateDelay = MediaError.DetailedErrorCode.SEGMENT_UNKNOWN;
        }
    }

    public void showFloatingTopicView(boolean z) {
        TopicSeparator.Cell cell = this.floatingTopicSeparator;
        if (cell == null || this.chatMode == 5) {
            return;
        }
        if (cell.getTag() == null) {
            ValueAnimator valueAnimator = this.floatingTopicAnimation;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            this.floatingTopicSeparator.setTag(1);
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.floatingTopicViewAlpha, 1.0f);
            this.floatingTopicAnimation = valueAnimatorOfFloat;
            valueAnimatorOfFloat.setDuration(150L);
            this.floatingTopicAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda222
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    this.f$0.lambda$showFloatingTopicView$151(valueAnimator2);
                }
            });
            this.floatingTopicAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatActivity.88
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ChatActivity.this.floatingTopicAnimation)) {
                        ChatActivity.this.floatingTopicViewAlpha = 1.0f;
                        ChatActivity.this.updateFloatingTopicView();
                        ChatActivity.this.floatingTopicAnimation = null;
                    }
                }
            });
            this.floatingTopicAnimation.start();
        }
        if (z) {
            return;
        }
        invalidateMessagesVisiblePart();
        this.hideDateDelay = 1000;
    }

    public *");
            startActivityForResult(intent, 21);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean dismissDialogOnPause(Dialog dialog) {
        return (dialog == this.chatAttachAlert || dialog == this.chatThemeBottomSheet || (dialog instanceof BotWebViewSheet) || !super.dismissDialogOnPause(dialog)) ? false : true;
    }

    private void cancelSearchLinks() {
        if (this.linkSearchRequestId != 0) {
            getConnectionsManager().cancelRequest(this.linkSearchRequestId, true);
        }
    }

    private void editResetMediaManual() {
        TLRPC.Message message;
        TLRPC.MessageMedia messageMedia;
        MessageObject messageObject = this.editingMessageObject;
        if (messageObject == null || (message = messageObject.messageOwner) == null || (messageMedia = message.media) == null) {
            return;
        }
        messageMedia.manual = false;
    }

    private void clearLinkPreview(boolean z) {
        this.pendingLinkSearchString = null;
        this.foundUrls = null;
        this.foundWebPage = null;
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.setWebPage(null, z);
        }
        MessagePreviewParams messagePreviewParams = this.messagePreviewParams;
        if (messagePreviewParams != null) {
            int i = this.currentAccount;
            MessageObject messageObject = this.replyingMessageObject;
            messagePreviewParams.updateLink(i, null, null, messageObject == this.threadMessageObject ? null : messageObject, this.replyingQuote, this.editingMessageObject);
        }
    }

    public void checkEditLinkRemoved(CharSequence charSequence) {
        TLRPC.WebPage webPage;
        TLRPC.Message message;
        TLRPC.MessageMedia messageMedia;
        TLRPC.WebPage webPage2;
        MessageObject messageObject = this.editingMessageObject;
        boolean z = (messageObject == null || (message = messageObject.messageOwner) == null || (messageMedia = message.media) == null || (webPage2 = messageMedia.webpage) == null || (webPage2 instanceof TLRPC.TL_webPageEmpty) || !messageMedia.manual) ? false : true;
        MessagePreviewParams messagePreviewParams = this.messagePreviewParams;
        if (messagePreviewParams == null || messageObject == null) {
            return;
        }
        int i = messageObject.type;
        if ((i == 0 || i == 19) && (webPage = this.foundWebPage) != null && !messagePreviewParams.hasLink(charSequence, webPage.url) && z) {
            clearLinkPreview(true);
            editResetMediaManual();
            fallbackFieldPanel();
        }
    }

    void lambda$searchLinks$160(final CharSequence charSequence, final MessagesController messagesController, final boolean z) {
        boolean z2;
        CharSequence charSequenceJoin;
        URLSpanReplacement[] uRLSpanReplacementArr;
        if (this.linkSearchRequestId != 0) {
            getConnectionsManager().cancelRequest(this.linkSearchRequestId, true);
            this.linkSearchRequestId = 0;
            z2 = true;
        } else {
            z2 = false;
        }
        ArrayList<CharSequence> arrayList = null;
        try {
            Matcher matcher = AndroidUtilities.WEB_URL.matcher(charSequence);
            while (matcher.find()) {
                if (matcher.start() <= 0 || charSequence.charAt(matcher.start() - 1) != '@') {
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    arrayList.add(charSequence.subSequence(matcher.start(), matcher.end()));
                }
            }
            if ((charSequence instanceof Spannable) && (uRLSpanReplacementArr = (URLSpanReplacement[]) ((Spannable) charSequence).getSpans(0, charSequence.length(), URLSpanReplacement.class)) != null && uRLSpanReplacementArr.length > 0) {
                if (arrayList == null) {
                    arrayList = new ArrayList<>();
                }
                for (URLSpanReplacement uRLSpanReplacement : uRLSpanReplacementArr) {
                    arrayList.add(uRLSpanReplacement.getURL());
                }
            }
            if (arrayList != null && this.foundUrls != null && arrayList.size() == this.foundUrls.size()) {
                boolean z3 = true;
                for (int i = 0; i < arrayList.size(); i++) {
                    if (!TextUtils.equals(arrayList.get(i), this.foundUrls.get(i))) {
                        z3 = false;
                    }
                }
                if (z3 && !z2) {
                    return;
                }
            }
            this.foundUrls = arrayList;
            if (arrayList == null) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda409
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$searchLinks$154();
                    }
                });
                return;
            }
            charSequenceJoin = TextUtils.join(" ", arrayList);
            if (arrayList != null && !arrayList.isEmpty()) {
                arrayList.get(0).toString();
            }
            if (this.currentEncryptedChat != null && messagesController.secretWebpagePreview == 2) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda411
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$searchLinks$157(messagesController, charSequence, z);
                    }
                });
                return;
            }
            final TL_account.getWebPagePreview getwebpagepreview = new TL_account.getWebPagePreview();
            if (charSequenceJoin instanceof String) {
                getwebpagepreview.message = (String) charSequenceJoin;
            } else {
                getwebpagepreview.message = charSequenceJoin.toString();
            }
            TLRPC.WebPage webPage = this.foundWebPage;
            if (webPage == null || !getwebpagepreview.message.equals(webPage.displayedText)) {
                final int i2 = this.waitingForWebpageId + 1;
                this.waitingForWebpageId = i2;
                requestLinkPreviewCached(getwebpagepreview, new Utilities.Callback2() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda412
                    @Override 
                    public final void run(Object obj, Object obj2) {
                        this.f$0.lambda$searchLinks$159(i2, getwebpagepreview, (Boolean) obj, (TLRPC.WebPage) obj2);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e(e);
            String lowerCase = charSequence.toString().toLowerCase();
            if (charSequence.length() < 13 || !(lowerCase.contains("http://") || lowerCase.contains("https://"))) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda410
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$searchLinks$155();
                    }
                });
                return;
            }
            charSequenceJoin = charSequence;
        }
    }

    public void lambda$requestLinkPreviewCached$161(TL_account.getWebPagePreview getwebpagepreview, Utilities.Callback2 callback2, Boolean bool, TLRPC.WebPage webPage) {
        if (bool.booleanValue() && !(webPage instanceof TLRPC.TL_webPagePending)) {
            Iterator<String> it = this.lastLinkPreviewResults.keySet().iterator();
            while (it.hasNext() && this.lastLinkPreviewResults.size() > 5) {
                it.next();
                it.remove();
            }
            this.lastLinkPreviewResults.put(getwebpagepreview.message, webPage);
        }
        callback2.run(bool, webPage);
    }

    private void requestLinkPreview(TL_account.getWebPagePreview getwebpagepreview, final Utilities.Callback2<Boolean, TLRPC.WebPage> callback2) {
        cancelSearchLinks();
        this.linkSearchRequestId = getConnectionsManager().sendRequestTyped(getwebpagepreview, new AiTonesController$$ExternalSyntheticLambda0(), new Utilities.Callback2() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda528
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$requestLinkPreview$166(callback2, (TL_account.webPagePreview) obj, (TLRPC.TL_error) obj2);
            }
        });
        getConnectionsManager().bindRequestToGuid(this.linkSearchRequestId, this.classGuid);
    }

    void lambda$requestLinkPreview$165(TLRPC.TL_messageMediaWebPage tL_messageMediaWebPage, TLRPC.TL_webPageAttributeStory tL_webPageAttributeStory, final Utilities.Callback2 callback2) {
        try {
            final LongSparseArray<ArrayList<MessageObject>> longSparseArray = new LongSparseArray<>();
            TLRPC.TL_message tL_message = new TLRPC.TL_message();
            tL_message.message = _UrlKt.FRAGMENT_ENCODE_SET;
            tL_message.id = 0;
            tL_message.media = tL_messageMediaWebPage;
            ArrayList<MessageObject> arrayList = new ArrayList<>();
            arrayList.add(new MessageObject(this.currentAccount, tL_message, false, false));
            longSparseArray.put(DialogObject.getPeerDialogId(tL_webPageAttributeStory.peer), arrayList);
            getMessagesController().getStoriesController().getStoriesStorage().fillMessagesWithStories(longSparseArray, new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda550
                @Override // java.lang.Runnable
                public final void run() {
                    ChatActivity.m7550$r8$lambda$9Bw1M8lfpM83nVyvaUQ43HUqfI(longSparseArray, callback2);
                }
            }, this.classGuid, false, null);
        } catch (Exception unused) {
        }
    }

    public static void lambda$forwardMessages$167() {
        this.waitingForSendingMessageLoad = false;
        hideFieldPanel(true);
    }

    @Override // org.telegram.ui.Components.ChatActivityInterface
    public boolean shouldShowImport() {
        return this.openImport;
    }

    public void setOpenImport() {
        this.openImport = true;
    }

    private void checkBotKeyboard() {
        StringBuilder sb;
        if (this.chatActivityEnterView == null || this.botButtons == null || this.userBlocked) {
            return;
        }
        boolean z = SystemClock.uptimeMillis() - this.lastSwitchTopicTime > 700;
        if (this.botButtons.messageOwner.reply_markup instanceof TLRPC.TL_replyKeyboardForceReply) {
            SharedPreferences mainSettings = MessagesController.getMainSettings(this.currentAccount);
            boolean z2 = this.isTopic;
            long topicId = this.dialog_id;
            if (z2) {
                sb = new StringBuilder();
                sb.append(topicId);
                sb.append("_");
                topicId = getTopicId();
            } else {
                sb = new StringBuilder(_UrlKt.FRAGMENT_ENCODE_SET);
            }
            sb.append(topicId);
            if (mainSettings.getInt("answered_".concat(sb.toString()), 0) != this.botButtons.getId()) {
                if (this.replyingMessageObject == null || this.chatActivityEnterView.getFieldText() == null) {
                    MessageObject messageObject = this.botButtons;
                    this.botReplyButtons = messageObject;
                    this.chatActivityEnterView.setButtons(messageObject, z, true);
                    showFieldPanelForReply(this.botButtons);
                    return;
                }
                return;
            }
            return;
        }
        MessageObject messageObject2 = this.replyingMessageObject;
        if (messageObject2 != null && this.botReplyButtons == messageObject2) {
            this.botReplyButtons = null;
            hideFieldPanel(true);
        }
        this.chatActivityEnterView.setButtons(this.botButtons, z, true);
    }

    public static class ReplyQuote {
        public TLRPC.PollAnswer answer;
        public int end;
        public ArrayList<TLRPC.MessageEntity> entities;
        public int length;
        public MessageObject message;
        public int offset;
        public byte[] option_id;
        public boolean outdated;
        public final long peerId;
        public boolean poll;
        public int start;
        public TLRPC.TodoItem task;
        public int task_id;
        public String text;
        public boolean todo;

        private ReplyQuote(long j, MessageObject messageObject, int i, int i2) {
            this.peerId = j;
            this.message = messageObject;
            this.start = i;
            this.end = i2;
            this.todo = false;
            this.task_id = -1;
            update();
        }

        private ReplyQuote(long j, MessageObject messageObject, int i) {
            this.peerId = j;
            this.message = messageObject;
            this.start = -1;
            this.end = -1;
            this.todo = true;
            this.task_id = i;
            update();
        }

        private ReplyQuote(long j, MessageObject messageObject, byte[] bArr) {
            this.peerId = j;
            this.message = messageObject;
            this.start = -1;
            this.end = -1;
            this.poll = true;
            this.option_id = bArr;
            update();
        }

        public static ReplyQuote from(MessageObject messageObject, String str, int i) {
            TLRPC.Message message;
            String str2;
            int iFindQuoteStart;
            if (messageObject == null || (message = messageObject.messageOwner) == null || (str2 = message.message) == null || str == null || (iFindQuoteStart = MessageObject.findQuoteStart(str2, str, i)) < 0) {
                return null;
            }
            return new ReplyQuote(messageObject.getDialogId(), messageObject, iFindQuoteStart, iFindQuoteStart + str.length());
        }

        public static ReplyQuote from(MessageObject messageObject, int i) {
            TLRPC.Message message;
            if (messageObject == null || (message = messageObject.messageOwner) == null || !(message.media instanceof TLRPC.TL_messageMediaToDo)) {
                return null;
            }
            return new ReplyQuote(messageObject.getDialogId(), messageObject, i);
        }

        public static ReplyQuote fromPollOption(MessageObject messageObject, byte[] bArr) {
            TLRPC.Message message;
            if (messageObject == null || (message = messageObject.messageOwner) == null || !(message.media instanceof TLRPC.TL_messageMediaPoll)) {
                return null;
            }
            return new ReplyQuote(messageObject.getDialogId(), messageObject, bArr);
        }

        public static ReplyQuote from(MessageObject messageObject) {
            TLRPC.Message message;
            if (messageObject == null || (message = messageObject.messageOwner) == null || message.message == null) {
                return null;
            }
            return from(messageObject, 0, Math.min(MessagesController.getInstance(messageObject.currentAccount).quoteLengthMax, messageObject.messageOwner.message.length()));
        }

        public static ReplyQuote from(MessageObject messageObject, int i, int i2) {
            if (messageObject == null) {
                return null;
            }
            return new ReplyQuote(messageObject.getDialogId(), messageObject, i, i2);
        }

        void lambda$showFieldPanel$168() {
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView == null || this.fieldPanelShown == 5) {
            return;
        }
        chatActivityEnterView.openKeyboard();
    }

    public void lambda$sendSecretMessageRead$170(MessageObject messageObject) {
        TLRPC.Message message = messageObject.messageOwner;
        int i = message.ttl;
        boolean z = i != Integer.MAX_VALUE;
        int i2 = i == Integer.MAX_VALUE ? 0 : i;
        message.destroyTime = getConnectionsManager().getCurrentTime() + i2;
        messageObject.messageOwner.destroyTimeMillis = (((long) i2) * 1000) + getConnectionsManager().getCurrentTimeMillis();
        if (this.currentEncryptedChat != null) {
            getMessagesController().markMessageAsRead(this.dialog_id, messageObject.messageOwner.random_id, i2);
        } else {
            getMessagesController().markMessageAsRead2(this.dialog_id, messageObject.getId(), null, i2, 0L, z);
        }
    }

    public Runnable sendSecretMediaDelete(final MessageObject messageObject) {
        if (messageObject == null || messageObject.isOut() || !messageObject.isSecretMedia() || messageObject.messageOwner.ttl != Integer.MAX_VALUE) {
            return null;
        }
        final long jCreateDeleteShowOnceTask = getMessagesController().createDeleteShowOnceTask(this.dialog_id, messageObject.getId());
        messageObject.forceExpired = true;
        if (messageObject.isOutOwner() || (!messageObject.isRoundOnce() && !messageObject.isVoiceOnce())) {
            ArrayList<MessageObject> arrayList = new ArrayList<>();
            arrayList.add(messageObject);
            updateMessages(arrayList, true);
        }
        return new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda497
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$sendSecretMediaDelete$171(jCreateDeleteShowOnceTask, messageObject);
            }
        };
    }

    public void lambda$scrollToLastMessage$172(int i, boolean z) {
        RecyclerAnimationScrollHelper recyclerAnimationScrollHelper = this.chatScrollHelper;
        this.chatScrollHelperCallback.position = i;
        this.chatScrollHelperCallback.offset = 0;
        boolean z2 = !z;
        this.chatScrollHelperCallback.bottom = z2;
        recyclerAnimationScrollHelper.scrollToPosition(i, 0, z2, true, true);
        this.canShowPagedownButton = false;
        updatePagedownButtonVisibility(true);
    }

    public void lambda$updateMessagesVisiblePart$178() {
        updatePinnedMessageView(this.openAnimationStartTime != 0 && SystemClock.elapsedRealtime() >= this.openAnimationStartTime + 150);
    }

    public void lambda$startMessageUnselect$181() {
        this.highlightMessageId = Integer.MAX_VALUE;
        this.highlightMessageQuoteFirst = false;
        this.highlightMessageQuoteFirstTime = 0L;
        this.highlightMessageQuote = null;
        this.highlightTaskId = null;
        this.highlightPollOptionId = null;
        this.highlightMessageQuoteOffset = -1;
        this.showNoQuoteAlert = false;
        updateVisibleRows();
        this.unselectRunnable = null;
    }

    public void removeSelectedMessageHighlight() {
        if (this.highlightMessageQuote == null && this.highlightTaskId == null && this.highlightPollOptionId == null) {
            Runnable runnable = this.unselectRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.unselectRunnable = null;
            }
            this.highlightMessageId = Integer.MAX_VALUE;
            this.highlightMessageQuoteFirst = false;
            this.highlightMessageQuoteFirstTime = 0L;
            this.highlightMessageQuote = null;
            this.highlightTaskId = null;
            this.highlightPollOptionId = null;
        }
    }

    public void resetProgressDialogLoading() {
        this.progressDialogLinkSpan = null;
        this.progressDialogAtMessageId = 0;
        this.progressDialogAtMessageType = -1;
        this.progressDialogBotButtonUrl = null;
        this.progressDialogCurrent = null;
        this.sideControlsButtonsLayout.setButtonLoading(1, false, true);
    }

    @Override // org.telegram.ui.Components.ChatActivityInterface
    public void scrollToMessageId(int i, int i2, boolean z, int i3, boolean z2, int i4) {
        scrollToMessageId(i, i2, z, i3, z2, i4, null, null);
    }

    public void scrollToMessageId(int i, int i2, boolean z, int i3, boolean z2, int i4, Integer num, Runnable runnable) {
        scrollToMessageId(i, i2, z, i3, z2, i4, num, null, runnable);
    }

    void lambda$scrollToMessageId$182() {
        int i = this.nextScrollToMessageId;
        if (i != 0) {
            scrollToMessageId(i, this.nextScrollFromMessageId, this.nextScrollSelect, this.nextScrollLoadIndex, this.nextScrollForce, this.nextScrollForcePinnedMessageId);
            this.nextScrollToMessageId = 0;
        }
    }

    public @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            float y;
            ChatActivityEnterView chatActivityEnterView;
            boolean zOnTouchEventInternal;
            boolean z;
            boolean z2;
            int i;
            if (ChatActivity.this.messageMetricsView != null) {
                ChatActivity.this.messageMetricsView.setIsUserActive();
            }
            if (AndroidUtilities.isInMultiwindow || ChatActivity.this.isInBubbleMode()) {
                EmojiView emojiView = ChatActivity.this.chatActivityEnterView.getEmojiView();
                ChatActivity chatActivity = ChatActivity.this;
                y = (emojiView != null ? chatActivity.chatActivityEnterView.getEmojiView() : chatActivity.chatActivityEnterView).getY();
            } else {
                y = ChatActivity.this.chatActivityEnterView.getY();
            }
            if ((ChatActivity.this.scrimView != null && ChatActivity.this.scrimView != ((BaseFragment) ChatActivity.this).actionBar.getBackButton()) || ((chatActivityEnterView = ChatActivity.this.chatActivityEnterView) != null && chatActivityEnterView.isStickersExpanded() && motionEvent.getY() < y)) {
                return false;
            }
            ChatActivity.this.lastTouchY = motionEvent.getY();
            TextSelectionHelper<Cell>.TextSelectionOverlay overlayView = ChatActivity.this.textSelectionHelper.getOverlayView(getContext());
            motionEvent.offsetLocation(-overlayView.getX(), -overlayView.getY());
            if (ChatActivity.this.textSelectionHelper.isInSelectionMode() && ChatActivity.this.textSelectionHelper.getOverlayView(getContext()).onTouchEvent(motionEvent)) {
                return true;
            }
            motionEvent.offsetLocation(overlayView.getX(), overlayView.getY());
            if (overlayView.checkOnTap(motionEvent)) {
                motionEvent.setAction(3);
            }
            if (ChatActivity.this.searchViewPager != null) {
                String str = ChatActivity.this.searchingHashtag;
                ChatActivity chatActivity2 = ChatActivity.this;
                if (str != null) {
                    zOnTouchEventInternal = chatActivity2.searchViewPager.onTouchEventInternal(motionEvent);
                    if (ChatActivity.this.searchViewPager.isTouch()) {
                        motionEvent.setAction(3);
                    }
                } else {
                    chatActivity2.searchViewPager.resetTouch();
                    zOnTouchEventInternal = false;
                }
            } else {
                zOnTouchEventInternal = false;
            }
            if (motionEvent.getAction() == 0 && ChatActivity.this.textSelectionHelper.isInSelectionMode() && (motionEvent.getY() < ChatActivity.this.chatListView.getTop() || motionEvent.getY() > ChatActivity.this.chatListView.getBottom())) {
                motionEvent.offsetLocation(-overlayView.getX(), -overlayView.getY());
                if (!ChatActivity.this.textSelectionHelper.getOverlayView(getContext()).onTouchEvent(motionEvent)) {
                    return true;
                }
                motionEvent.offsetLocation(overlayView.getX(), overlayView.getY());
                return super.dispatchTouchEvent(motionEvent);
            }
            if (ChatActivity.this.pinchToZoomHelper.isInOverlayMode()) {
                return ChatActivity.this.pinchToZoomHelper.onTouchEvent(motionEvent);
            }
            if (AvatarPreviewer.hasVisibleInstance()) {
                AvatarPreviewer.getInstance().onTouchEvent(motionEvent);
                return true;
            }
            if (ChatActivity.this.isInPreviewMode() && ChatActivity.this.allowExpandPreviewByClick) {
                if (motionEvent.getAction() == 0) {
                    int i2 = 2;
                    int[] iArr = new int[2];
                    getLocationInWindow(iArr);
                    int[] iArr2 = new int[2];
                    if (ChatActivity.this.sideControlsButtonsLayout == null) {
                        z = false;
                        break;
                    }
                    int i3 = 0;
                    while (true) {
                        if (i3 >= 3) {
                            z = false;
                            break;
                        }
                        if (i3 == 0) {
                            i = 1;
                        } else {
                            i = i3 == 1 ? i2 : 3;
                        }
                        if (ChatActivity.this.sideControlsButtonsLayout.getButtonLocationInWindow(i, iArr2)) {
                            Rect rect = AndroidUtilities.rectTmp2;
                            int i4 = iArr2[0];
                            int i5 = iArr[0];
                            rect.set(i4 - i5, iArr2[1] - iArr[1], (i4 - i5) + AndroidUtilities.dp(56.0f), (iArr2[1] - iArr[1]) + AndroidUtilities.dp(61.0f));
                            if (rect.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                                z = true;
                                break;
                            }
                        }
                        i3++;
                        i2 = 2;
                    }
                    ChatAvatarContainer chatAvatarContainer = ChatActivity.this.avatarContainer;
                    if (chatAvatarContainer != null) {
                        chatAvatarContainer.getLocationInWindow(iArr2);
                        Rect rect2 = AndroidUtilities.rectTmp2;
                        int i6 = iArr2[0];
                        int i7 = iArr[0];
                        rect2.set(i6 - i7, iArr2[1] - iArr[1], (i6 - i7) + ChatActivity.this.avatarContainer.getMeasuredWidth(), (iArr2[1] - iArr[1]) + ChatActivity.this.avatarContainer.getMeasuredHeight());
                        if (rect2.contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
                            z2 = true;
                        } else {
                            z2 = false;
                        }
                    } else {
                        z2 = false;
                    }
                    if (!z) {
                        this.x = motionEvent.getX();
                        this.y = motionEvent.getY();
                        this.pressTime = SystemClock.elapsedRealtime();
                        this.pressActionBar = z2;
                        ChatAvatarContainer chatAvatarContainer2 = ChatActivity.this.avatarContainer;
                        if (chatAvatarContainer2 != null) {
                            chatAvatarContainer2.bounce.setPressed(z2);
                        }
                        zOnTouchEventInternal = true;
                    } else {
                        this.pressTime = -1L;
                    }
                } else if (motionEvent.getAction() == 1) {
                    ChatAvatarContainer chatAvatarContainer3 = ChatActivity.this.avatarContainer;
                    if (chatAvatarContainer3 != null) {
                        chatAvatarContainer3.bounce.setPressed(false);
                    }
                    if (this.pressActionBar || (com.google.zxing.common.detector.MathUtils.distance(this.x, this.y, motionEvent.getX(), motionEvent.getY()) < AndroidUtilities.dp(6.0f) && SystemClock.elapsedRealtime() - this.pressTime <= ViewConfiguration.getTapTimeout())) {
                        boolean z3 = this.pressActionBar;
                        ChatActivity chatActivity3 = ChatActivity.this;
                        if (z3) {
                            INavigationLayout iNavigationLayout = ((BaseFragment) chatActivity3).parentLayout;
                            iNavigationLayout.closeLastFragment(false, true);
                            iNavigationLayout.presentFragment(ProfileActivity.of(ChatActivity.this.dialog_id));
                        } else {
                            ((BaseFragment) chatActivity3).parentLayout.expandPreviewFragment();
                        }
                        motionEvent.setAction(3);
                    }
                    this.pressTime = -1L;
                } else if (motionEvent.getAction() == 3) {
                    this.pressTime = -1L;
                }
            }
            return super.dispatchTouchEvent(motionEvent) || zOnTouchEventInternal;
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            if (getTag(67108867) != null) {
                return;
            }
            if (getTag(67108867) != null || ChatActivity.this.blurredView == null || !ChatActivity.this.blurredView.fullyDrawing() || ChatActivity.this.blurredView.getTag() == null) {
                super.onDraw(canvas);
            }
        }

        private void updateSecretStatus() {
        TLRPC.EncryptedChat encryptedChat;
        ChatActivityEnterView chatActivityEnterView;
        if (this.bottomOverlay == null) {
            return;
        }
        this.bottomOverlayText.setBackground(null);
        this.bottomOverlayText.setOnClickListener(null);
        boolean z = true;
        if (this.chatMode == 3 && getSavedDialogId() == UserObject.ANONYMOUS) {
            this.bottomOverlayText.setText(LocaleController.getString(R.string.AuthorHiddenDescription));
            this.bottomOverlay.setVisibility(0);
            AnimatorSet animatorSet = this.mentionListAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
                this.mentionListAnimation = null;
            }
            this.mentionContainer.setVisibility(8);
            this.mentionContainer.setTag(null);
            updateMessageListAccessibilityVisibility();
            SuggestEmojiView suggestEmojiView = this.suggestEmojiPanel;
            if (suggestEmojiView != null) {
                suggestEmojiView.forceClose();
            }
        } else {
            TLRPC.Chat chat = this.currentChat;
            if (chat == null || ChatObject.canSendMessages(chat) || ChatObject.canSendAnyMedia(this.currentChat)) {
                createEmptyView(false);
                encryptedChat = this.currentEncryptedChat;
                if (encryptedChat != null || this.bigEmptyView == null) {
                    this.bottomOverlay.setVisibility(4);
                    if (this.suggestEmojiPanel == null && (chatActivityEnterView = this.chatActivityEnterView) != null && chatActivityEnterView.hasText()) {
                        this.suggestEmojiPanel.fireUpdate();
                        return;
                    }
                    return;
                }
                if (encryptedChat instanceof TLRPC.TL_encryptedChatRequested) {
                    this.bottomOverlayText.setText(LocaleController.getString(R.string.EncryptionProcessing));
                    this.bottomOverlay.setVisibility(0);
                    this.chatActivityEnterView.setVisibility(4);
                } else if (encryptedChat instanceof TLRPC.TL_encryptedChatWaiting) {
                    this.bottomOverlayText.setText(AndroidUtilities.replaceTags(LocaleController.formatString("AwaitingEncryption", R.string.AwaitingEncryption, "<b>" + this.currentUser.first_name + "</b>")));
                    this.bottomOverlay.setVisibility(0);
                    this.chatActivityEnterView.setVisibility(4);
                } else if (encryptedChat instanceof TLRPC.TL_encryptedChatDiscarded) {
                    this.bottomOverlayText.setText(LocaleController.getString(R.string.EncryptionRejected));
                    this.bottomOverlay.setVisibility(0);
                    this.chatActivityEnterView.setVisibility(4);
                    this.chatActivityEnterView.setFieldText(_UrlKt.FRAGMENT_ENCODE_SET);
                    getMediaDataController().cleanDraft(this.dialog_id, this.threadMessageId, false);
                } else {
                    if (encryptedChat instanceof TLRPC.TL_encryptedChat) {
                        this.bottomOverlay.setVisibility(4);
                        if (!this.inPreviewMode && !this.isInsideContainer && this.chatMode != 3) {
                            this.chatActivityEnterView.setVisibility(0);
                        }
                    }
                    z = false;
                }
                checkRaiseSensors();
                checkActionBarMenu(false);
            } else {
                TLRPC.Chat chat2 = this.currentChat;
                if (!chat2.gigagroup && (!ChatObject.isChannel(chat2) || this.currentChat.megagroup)) {
                    TLRPC.Chat chat3 = this.currentChat;
                    TLRPC.TL_chatBannedRights tL_chatBannedRights = chat3.default_banned_rights;
                    if (tL_chatBannedRights != null && tL_chatBannedRights.send_messages) {
                        if (ChatObject.isPossibleRemoveChatRestrictionsByBoosts(chat3)) {
                            Drawable drawableMutate = ContextCompat.getDrawable(getContext(), R.drawable.filled_limit_boost).mutate();
                            int i = Theme.key_featuredStickers_addButton;
                            DrawableCompat.setTint(drawableMutate, getThemedColor(i));
                            drawableMutate.setBounds(0, 0, AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f));
                            CombinedDrawable combinedDrawable = new CombinedDrawable(null, drawableMutate, AndroidUtilities.dp(-6.0f), AndroidUtilities.dp(-6.0f));
                            combinedDrawable.setIconSize(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f));
                            combinedDrawable.setCustomSize(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f));
                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("d " + LocaleController.getString(R.string.BoostingBoostToSendMessages));
                            spannableStringBuilder.setSpan(new ForegroundColorSpan(getThemedColor(i)), 0, spannableStringBuilder.length(), 33);
                            spannableStringBuilder.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, spannableStringBuilder.length(), 33);
                            spannableStringBuilder.setSpan(new ImageSpan(combinedDrawable, 1), 0, 1, 33);
                            this.bottomOverlayText.setBackground(Theme.createSelectorWithBackgroundDrawable(0, Theme.getColor(Theme.key_listSelector)));
                            this.bottomOverlayText.setText(spannableStringBuilder);
                            this.bottomOverlayText.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda11
                                @Override // android.view.View.OnClickListener
                                public final void onClick(View view) {
                                    this.f$0.lambda$updateSecretStatus$185(view);
                                }
                            });
                        } else {
                            this.bottomOverlayText.setText(LocaleController.getString(R.string.GlobalSendMessageRestricted));
                        }
                    } else {
                        boolean zIsBannedForever = AndroidUtilities.isBannedForever(chat3.banned_rights);
                        TextView textView = this.bottomOverlayText;
                        if (zIsBannedForever) {
                            textView.setText(LocaleController.getString(R.string.SendMessageRestrictedForever));
                        } else {
                            textView.setText(LocaleController.formatString("SendMessageRestricted", R.string.SendMessageRestricted, LocaleController.formatDateForBan(this.currentChat.banned_rights.until_date)));
                        }
                    }
                    this.bottomOverlay.setVisibility(0);
                    AnimatorSet animatorSet2 = this.mentionListAnimation;
                    if (animatorSet2 != null) {
                        animatorSet2.cancel();
                        this.mentionListAnimation = null;
                    }
                    this.mentionContainer.setVisibility(8);
                    this.mentionContainer.setTag(null);
                    updateMessageListAccessibilityVisibility();
                    SuggestEmojiView suggestEmojiView2 = this.suggestEmojiPanel;
                    if (suggestEmojiView2 != null) {
                        suggestEmojiView2.forceClose();
                    }
                } else {
                    createEmptyView(false);
                    encryptedChat = this.currentEncryptedChat;
                    if (encryptedChat != null) {
                    }
                    this.bottomOverlay.setVisibility(4);
                    if (this.suggestEmojiPanel == null) {
                        return;
                    } else {
                        return;
                    }
                }
            }
        }
        if (this.inPreviewMode) {
            this.bottomOverlay.setVisibility(4);
        }
        if (z) {
            this.chatActivityEnterView.hidePopup(false);
            if (getParentActivity() != null) {
                AndroidUtilities.hideKeyboard(getParentActivity().getCurrentFocus());
            }
        }
    }

    public private void updateSelectedMessageReactions() {
        boolean z;
        if (getDialogId() == getUserConfig().getClientUserId()) {
            ArrayList<MessageObject> arrayList = new ArrayList<>();
            for (int i = 0; i < this.selectedMessagesIds.length; i++) {
                for (int i2 = 0; i2 < this.selectedMessagesIds[i].size(); i2++) {
                    MessageObject messageObjectValueAt = this.selectedMessagesIds[i].valueAt(i2);
                    if (messageObjectValueAt.hasValidGroupId()) {
                        MessageObject.GroupedMessages validGroupedMessage = getValidGroupedMessage(messageObjectValueAt);
                        MessageObject.GroupedMessagePosition position = validGroupedMessage != null ? validGroupedMessage.getPosition(messageObjectValueAt) : null;
                        if (position != null && position.last) {
                            arrayList.add(messageObjectValueAt);
                        }
                    } else {
                        arrayList.add(messageObjectValueAt);
                    }
                }
            }
            ReactionsContainerLayout reactionsContainerLayout = this.tagSelector;
            if (reactionsContainerLayout != null) {
                reactionsContainerLayout.setSelectedReactionsInclusive(arrayList);
                boolean zIsEmpty = this.tagSelector.getSelectedReactions().isEmpty();
                z = !zIsEmpty;
                this.tagSelector.setHint(LocaleController.getString(zIsEmpty ? R.string.SavedTagReactionsSelectedAddHint : R.string.SavedTagReactionsSelectedEditHint));
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda155
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$updateSelectedMessageReactions$187();
                    }
                }, 120L);
            } else {
                z = !ReactionsContainerLayout.getInclusiveReactions(arrayList).isEmpty();
            }
            ActionBarMenuItem item = this.actionBar.createActionMode().getItem(28);
            if (item != null) {
                item.setIcon(z ? R.drawable.menu_tag_edit : R.drawable.menu_tag_plus, true);
            }
        }
        if (disabledReactions() || this.selectionReactionsOverlay == null) {
            return;
        }
        ArrayList arrayList2 = new ArrayList();
        SparseArrayWithTouch<MessageObject> sparseArrayWithTouch = this.selectedMessagesIds[0];
        for (int i3 = 0; i3 < sparseArrayWithTouch.size(); i3++) {
            arrayList2.add(sparseArrayWithTouch.valueAt(i3));
        }
        SparseArrayWithTouch<MessageObject> sparseArrayWithTouch2 = this.selectedMessagesIds[1];
        for (int i4 = 0; i4 < sparseArrayWithTouch2.size(); i4++) {
            arrayList2.add(sparseArrayWithTouch2.valueAt(i4));
        }
        this.selectionReactionsOverlay.setSelectedMessages(arrayList2);
    }

    public void lambda$refreshFeedUnreadDivider$188() {
        if (isFeedSearch()) {
            feedIntegration().onReadStateRefreshed();
        }
    }

    public void onFeedChannelsChanged(boolean z) {
        if (isFeedSearch()) {
            if (z) {
                reloadFeed();
            } else {
                reconcileFeedList();
            }
        }
    }

    public void applyFeedConfigChange() {
        if (isFeedSearch()) {
            FeedController.getInstance(this.currentAccount).applyConfigChange(new Utilities.Callback() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda210
                @Override 
                public final void run(Object obj) {
                    this.f$0.lambda$applyFeedConfigChange$189((Boolean) obj);
                }
            });
        }
    }

    public public static ArrayList<MediaController.PhotoEntry> createEntriesFromMedia(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, boolean z, CharSequence charSequence) {
        ArrayList<MediaController.PhotoEntry> arrayList2 = new ArrayList<>();
        for (int i = 0; i < arrayList.size(); i++) {
            SendMessagesHelper.SendingMediaInfo sendingMediaInfo = arrayList.get(i);
            String absolutePath = sendingMediaInfo.path;
            if (absolutePath == null) {
                if (sendingMediaInfo.uri != null) {
                    try {
                        File fileGeneratePicturePath = AndroidUtilities.generatePicturePath(z, _UrlKt.FRAGMENT_ENCODE_SET);
                        InputStream inputStreamOpenInputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(sendingMediaInfo.uri);
                        FileOutputStream fileOutputStream = new FileOutputStream(fileGeneratePicturePath);
                        byte[] bArr = new byte[8192];
                        while (true) {
                            int i2 = inputStreamOpenInputStream.read(bArr);
                            if (i2 <= 0) {
                                break;
                            }
                            fileOutputStream.write(bArr, 0, i2);
                            fileOutputStream.flush();
                        }
                        inputStreamOpenInputStream.close();
                        fileOutputStream.close();
                        absolutePath = fileGeneratePicturePath.getAbsolutePath();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                } else {
                    absolutePath = null;
                }
            }
            String str = absolutePath;
            if (str != null) {
                Pair<Integer, Integer> imageOrientation = AndroidUtilities.getImageOrientation(str);
                MediaController.PhotoEntry orientation = new MediaController.PhotoEntry(0, 0, 0L, str, ((Integer) imageOrientation.first).intValue(), sendingMediaInfo.isVideo, 0, 0, 0L).setOrientation(imageOrientation);
                if (i == arrayList.size() - 1 && charSequence != null) {
                    orientation.caption = charSequence;
                }
                arrayList2.add(orientation);
            }
        }
        return arrayList2;
    }

    public boolean openPhotosEditor(ArrayList<SendMessagesHelper.SendingMediaInfo> arrayList, CharSequence charSequence) {
        final ArrayList<MediaController.PhotoEntry> arrayListCreateEntriesFromMedia = createEntriesFromMedia(arrayList, isSecretChat(), charSequence);
        if (arrayListCreateEntriesFromMedia.isEmpty()) {
            return false;
        }
        if (getParentActivity() != null) {
            final boolean[] zArr = new boolean[arrayListCreateEntriesFromMedia.size()];
            Arrays.fill(zArr, true);
            PhotoViewer.getInstance().setParentActivity(this, this.themeDelegate);
            PhotoViewer.getInstance().openPhotoForSelect(new ArrayList<>(arrayListCreateEntriesFromMedia), arrayListCreateEntriesFromMedia.size() - 1, 0, false, new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.ChatActivity.94
                @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
                public boolean canScrollAway() {
                    return false;
                }

                @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
                public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i) {
                    return null;
                }

                @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
                public int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo) {
                    return i;
                }

                @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
                public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, int i3, boolean z2) {
                    for (int size = arrayListCreateEntriesFromMedia.size() - 1; size >= 0; size--) {
                        if (!zArr[size]) {
                            arrayListCreateEntriesFromMedia.remove(size);
                        }
                    }
                    ChatActivity.this.sendPhotosGroup(arrayListCreateEntriesFromMedia, z, i2, z2);
                }

                @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
                public boolean isPhotoChecked(int i) {
                    return zArr[i];
                }
            }, this);
        } else {
            fillEditingMediaWithCaption(charSequence, null);
            sendPhotosGroup(arrayListCreateEntriesFromMedia, false, 0, false);
            afterMessageSend();
        }
        return true;
    }

    public void sendPhotosGroup(ArrayList<MediaController.PhotoEntry> arrayList, boolean z, int i, boolean z2) {
        String str;
        if (!arrayList.isEmpty()) {
            ArrayList arrayList2 = new ArrayList();
            int size = arrayList.size();
            int i2 = 0;
            while (i2 < size) {
                MediaController.PhotoEntry photoEntry = arrayList.get(i2);
                i2++;
                MediaController.PhotoEntry photoEntry2 = photoEntry;
                SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                boolean zIsHighQuality = photoEntry2.isHighQuality();
                if (!photoEntry2.isVideo && (str = photoEntry2.imagePath) != null) {
                    sendingMediaInfo.path = str;
                    if (zIsHighQuality) {
                        sendingMediaInfo.originalPhotoEntry = photoEntry2.clone();
                    }
                } else {
                    String str2 = photoEntry2.path;
                    if (str2 != null) {
                        sendingMediaInfo.path = str2;
                    }
                }
                sendingMediaInfo.thumbPath = photoEntry2.thumbPath;
                sendingMediaInfo.coverPath = photoEntry2.coverPath;
                sendingMediaInfo.isLivePhoto = photoEntry2.isLivePhoto();
                sendingMediaInfo.isVideo = photoEntry2.isVideo;
                sendingMediaInfo.discardLivePhoto = photoEntry2.isUnalivePhoto();
                sendingMediaInfo.livePhotoVideoOffset = photoEntry2.livePhotoVideoOffset;
                sendingMediaInfo.livePhotoTimestampUs = photoEntry2.livePhotoTimestampUs;
                CharSequence charSequence = photoEntry2.caption;
                sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                sendingMediaInfo.entities = photoEntry2.entities;
                sendingMediaInfo.masks = photoEntry2.stickers;
                sendingMediaInfo.ttl = photoEntry2.ttl;
                sendingMediaInfo.videoEditedInfo = photoEntry2.editedInfo;
                sendingMediaInfo.canDeleteAfter = photoEntry2.canDeleteAfter;
                sendingMediaInfo.hasMediaSpoilers = photoEntry2.hasSpoiler;
                sendingMediaInfo.highQuality = photoEntry2.isHighQuality();
                arrayList2.add(sendingMediaInfo);
                photoEntry2.reset();
            }
            fillEditingMediaWithCaption(((SendMessagesHelper.SendingMediaInfo) arrayList2.get(0)).caption, ((SendMessagesHelper.SendingMediaInfo) arrayList2.get(0)).entities);
            SendMessagesHelper.prepareSendingMedia(getAccountInstance(), arrayList2, this.dialog_id, this.replyingMessageObject, getThreadMessage(), null, this.replyingQuote, z2, true, null, z, i, 0, this.chatMode, ((SendMessagesHelper.SendingMediaInfo) arrayList2.get(0)).updateStickersOrder, null, this.quickReplyShortcut, getQuickReplyId(), 0L, false, 0L, getSendMonoForumPeerId(), this.messageSuggestionParams);
            afterMessageSend();
            ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
            if (chatActivityEnterView != null) {
                chatActivityEnterView.setFieldText(_UrlKt.FRAGMENT_ENCODE_SET);
            }
        }
        if (i != 0) {
            if (this.scheduledMessagesCount == -1) {
                this.scheduledMessagesCount = 0;
            }
            this.scheduledMessagesCount += arrayList.size();
            updateScheduledInterface(true);
        }
    }

    public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z, boolean z2) {
        ChatActionCell chatActionCell;
        MessageObject messageObject2;
        ImageReceiver photoImage;
        ImageReceiver photoImage2;
        TLRPC.Message message;
        TLRPC.MessageAction messageAction;
        TLRPC.Photo photo;
        ArrayList<TLRPC.VideoSize> arrayList;
        TLRPC.FileLocation fileLocation2;
        TLRPC.Chat chat;
        ChatMessageCell chatMessageCell;
        MessageObject messageObject3;
        int childCount = this.chatListView.getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = this.chatListView.getChildAt(i2);
            if (childAt instanceof ChatMessageCell) {
                if (messageObject == null || (messageObject3 = (chatMessageCell = (ChatMessageCell) childAt).getMessageObject()) == null || messageObject3.getFeedRealId() != messageObject.getFeedRealId() || messageObject3.getDialogId() != messageObject.getDialogId()) {
                    photoImage2 = null;
                } else {
                    ArrayList<Integer> arrayList2 = messageObject3.pollMediaMapping;
                    if (arrayList2 != null && i >= 0 && i < arrayList2.size()) {
                        photoImage2 = chatMessageCell.getPhotoImage(messageObject3.pollMediaMapping.get(i).intValue());
                    } else {
                        photoImage2 = chatMessageCell.getPhotoImage(i);
                    }
                }
            } else if (!(childAt instanceof ChatActionCell) || (messageObject2 = (chatActionCell = (ChatActionCell) childAt).getMessageObject()) == null) {
                photoImage2 = null;
            } else if (messageObject != null) {
                if (messageObject2.getFeedRealId() == messageObject.getFeedRealId() && messageObject2.getDialogId() == messageObject.getDialogId()) {
                    photoImage2 = chatActionCell.getPhotoImage();
                } else {
                    photoImage2 = null;
                }
            } else if (fileLocation == null || messageObject2.photoThumbs == null) {
                photoImage2 = null;
            } else {
                int i3 = 0;
                while (true) {
                    if (i3 >= messageObject2.photoThumbs.size()) {
                        photoImage = null;
                        break;
                    }
                    TLRPC.FileLocation fileLocation3 = messageObject2.photoThumbs.get(i3).location;
                    if (fileLocation3 != null && fileLocation3.local_id == fileLocation.local_id) {
                        photoImage = chatActionCell.getPhotoImage();
                        break;
                    }
                    i3++;
                }
                photoImage2 = (photoImage != null || (message = messageObject2.messageOwner) == null || (messageAction = message.action) == null || (photo = messageAction.photo) == null || (arrayList = photo.video_sizes) == null || arrayList.isEmpty() || (fileLocation2 = messageObject2.messageOwner.action.photo.video_sizes.get(0).location) == null || fileLocation2.volume_id != fileLocation.volume_id || fileLocation2.local_id != fileLocation.local_id) ? photoImage : chatActionCell.getPhotoImage();
            }
            if (photoImage2 != null) {
                if (z2 && childAt.getY() + photoImage2.getImageY2() < this.chatListViewPaddingTop - AndroidUtilities.dp(4.0f)) {
                    return null;
                }
                int[] iArr = new int[2];
                childAt.getLocationInWindow(iArr);
                PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                placeProviderObject.viewX = iArr[0];
                placeProviderObject.viewY = iArr[1] + childAt.getPaddingTop();
                placeProviderObject.parentView = this.chatListView;
                placeProviderObject.animatingImageView = null;
                placeProviderObject.imageReceiver = photoImage2;
                if (z) {
                    placeProviderObject.thumb = photoImage2.getBitmapSafe();
                }
                placeProviderObject.radius = photoImage2.getRoundRadius(true);
                if ((childAt instanceof ChatActionCell) && (chat = this.currentChat) != null) {
                    placeProviderObject.dialogId = -chat.id;
                }
                placeProviderObject.clipTopAddition = (int) ((this.chatListViewPaddingTop - this.chatListViewPaddingVisibleOffset) - AndroidUtilities.dp(4.0f));
                placeProviderObject.clipBottomAddition = (int) (this.blurredViewBottomOffset + AndroidUtilities.dp(9.0f) + this.windowInsetsStateHolder.getAnimatedMaxBottomInset() + getTopicTabsSideSize(TopicsTabsView.Position.BOTTOM) + this.inputIslandHeightCurrent);
                return placeProviderObject;
            }
        }
        return null;
    }

    public boolean openArticlePhoto(ChatMessageCell chatMessageCell, TL_iv.PageBlock pageBlock) {
        MessageObject messageObject;
        TLRPC.Message message;
        TL_iv.RichMessage richMessage;
        if (chatMessageCell == null || pageBlock == null || getParentActivity() == null || (messageObject = chatMessageCell.getMessageObject()) == null || (message = messageObject.messageOwner) == null || messageObject.richLayout == null || (richMessage = message.rich_message) == null) {
            return false;
        }
        ArrayList arrayList = new ArrayList();
        messageObject.richLayout.collectMediaBlocks(arrayList);
        int iIndexOf = arrayList.indexOf(pageBlock);
        if (iIndexOf < 0) {
            return false;
        }
        PhotoViewer photoViewer = PhotoViewer.getInstance();
        photoViewer.setParentActivity(this);
        return photoViewer.openPhoto(iIndexOf, new ChatArticlePageBlocksAdapter(richMessage, arrayList), new ChatArticlePhotoViewerProvider(arrayList));
    }

    public static class ChatArticlePageBlocksAdapter implements PhotoViewer.PageBlocksAdapter {
        private final List<TL_iv.PageBlock> blocks;
        private final TL_iv.RichMessage page;

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public CharSequence getCaption(int i) {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public boolean isHardwarePlayer(int i) {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public void updateSlideshowCell(TL_iv.PageBlock pageBlock) {
        }

        public ChatArticlePageBlocksAdapter(TL_iv.RichMessage richMessage, List<TL_iv.PageBlock> list) {
            this.page = richMessage;
            this.blocks = list;
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public int getItemsCount() {
            return this.blocks.size();
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public TL_iv.PageBlock get(int i) {
            return this.blocks.get(i);
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public List<TL_iv.PageBlock> getAll() {
            return this.blocks;
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public boolean isVideo(int i) {
            if (i < 0 || i >= this.blocks.size()) {
                return false;
            }
            return ArticleViewer.WebPageUtils.isVideo(this.page, this.blocks.get(i));
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public TLObject getMedia(int i) {
            if (i < 0 || i >= this.blocks.size()) {
                return null;
            }
            return ArticleViewer.WebPageUtils.getMedia(this.page, this.blocks.get(i));
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public File getFile(int i) {
            if (i < 0 || i >= this.blocks.size()) {
                return null;
            }
            return ArticleViewer.WebPageUtils.getMediaFile(this.page, this.blocks.get(i));
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public String getFileName(int i) {
            TLObject media = getMedia(i);
            if (media instanceof TLRPC.Photo) {
                media = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo) media).sizes, AndroidUtilities.getPhotoSize());
            }
            return FileLoader.getAttachFileName(media);
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public TLRPC.PhotoSize getFileLocation(TLObject tLObject, int[] iArr) {
            if (tLObject instanceof TLRPC.Photo) {
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(((TLRPC.Photo) tLObject).sizes, AndroidUtilities.getPhotoSize());
                if (closestPhotoSizeWithSize != null) {
                    int i = closestPhotoSizeWithSize.size;
                    iArr[0] = i;
                    if (i == 0) {
                        iArr[0] = -1;
                    }
                    return closestPhotoSizeWithSize;
                }
                iArr[0] = -1;
            } else if (tLObject instanceof TLRPC.Document) {
                TLRPC.Document document = (TLRPC.Document) tLObject;
                TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 320, false, null, true);
                if (closestPhotoSizeWithSize2 == null) {
                    closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                }
                if (closestPhotoSizeWithSize2 != null) {
                    int i2 = closestPhotoSizeWithSize2.size;
                    iArr[0] = i2;
                    if (i2 == 0) {
                        iArr[0] = -1;
                    }
                    return closestPhotoSizeWithSize2;
                }
            }
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PageBlocksAdapter
        public Object getParentObject() {
            return this.page;
        }
    }

    public class ChatArticlePhotoViewerProvider extends PhotoViewer.EmptyPhotoViewerProvider {
        private final List<TL_iv.PageBlock> blocks;
        private final int[] tempCoords = new int[2];

        public ChatArticlePhotoViewerProvider(List<TL_iv.PageBlock> list) {
            this.blocks = list;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z, boolean z2) {
            ImageReceiver imageReceiverFindMediaImageReceiver;
            ChatMessageCell chatMessageCell;
            MessageObject messageObject2;
            RichMessageLayout richMessageLayout;
            if (ChatActivity.this.chatListView != null && i >= 0 && i < this.blocks.size()) {
                TL_iv.PageBlock pageBlock = this.blocks.get(i);
                int childCount = ChatActivity.this.chatListView.getChildCount();
                for (int i2 = 0; i2 < childCount; i2++) {
                    View childAt = ChatActivity.this.chatListView.getChildAt(i2);
                    if (!(childAt instanceof ChatMessageCell) || (messageObject2 = (chatMessageCell = (ChatMessageCell) childAt).getMessageObject()) == null || (richMessageLayout = messageObject2.richLayout) == null) {
                        imageReceiverFindMediaImageReceiver = null;
                    } else {
                        int[] iArr = new int[2];
                        imageReceiverFindMediaImageReceiver = richMessageLayout.findMediaImageReceiver(pageBlock, iArr);
                        if (imageReceiverFindMediaImageReceiver != null) {
                            childAt.getLocationInWindow(this.tempCoords);
                            int[] iArr2 = this.tempCoords;
                            iArr2[0] = iArr2[0] + chatMessageCell.getTextX() + iArr[0];
                            int[] iArr3 = this.tempCoords;
                            iArr3[1] = iArr3[1] + chatMessageCell.getTextY() + iArr[1];
                        }
                    }
                    if (imageReceiverFindMediaImageReceiver != null) {
                        PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                        int[] iArr4 = this.tempCoords;
                        placeProviderObject.viewX = iArr4[0];
                        placeProviderObject.viewY = iArr4[1];
                        placeProviderObject.parentView = ChatActivity.this.chatListView;
                        placeProviderObject.imageReceiver = imageReceiverFindMediaImageReceiver;
                        placeProviderObject.thumb = imageReceiverFindMediaImageReceiver.getBitmapSafe();
                        placeProviderObject.radius = imageReceiverFindMediaImageReceiver.getRoundRadius(true);
                        ChatActivity chatActivity = ChatActivity.this;
                        placeProviderObject.clipTopAddition = (int) ((chatActivity.chatListViewPaddingTop - chatActivity.chatListViewPaddingVisibleOffset) - AndroidUtilities.dp(4.0f));
                        placeProviderObject.clipBottomAddition = (int) (ChatActivity.this.blurredViewBottomOffset + AndroidUtilities.dp(9.0f) + ChatActivity.this.windowInsetsStateHolder.getAnimatedMaxBottomInset() + ChatActivity.this.getTopicTabsSideSize(TopicsTabsView.Position.BOTTOM) + ChatActivity.this.inputIslandHeightCurrent);
                        return placeProviderObject;
                    }
                }
            }
            return null;
        }
    }

    private void showAttachmentError() {
        if (getParentActivity() == null) {
            return;
        }
        BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.UnsupportedAttachment), this.themeDelegate).show();
    }

    public void fillEditingMediaWithCaption(CharSequence charSequence, ArrayList<TLRPC.MessageEntity> arrayList) {
        if (this.editingMessageObject == null) {
            return;
        }
        boolean zIsEmpty = TextUtils.isEmpty(charSequence);
        MessageObject messageObject = this.editingMessageObject;
        if (!zIsEmpty) {
            messageObject.editingMessage = charSequence;
            messageObject.editingMessageEntities = arrayList;
            return;
        }
        if (messageObject.isMediaEmpty()) {
            MessageObject messageObject2 = this.editingMessageObject;
            messageObject2.editingMessage = _UrlKt.FRAGMENT_ENCODE_SET;
            messageObject2.editingMessageEntities = new ArrayList<>();
            return;
        }
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            this.editingMessageObject.editingMessage = chatActivityEnterView.getFieldText();
            MessageObject messageObject3 = this.editingMessageObject;
            if (messageObject3.editingMessage != null || TextUtils.isEmpty(messageObject3.messageOwner.message)) {
                return;
            }
            this.editingMessageObject.editingMessage = _UrlKt.FRAGMENT_ENCODE_SET;
        }
    }

    private void sendUriAsDocument(Uri uri) {
        sendUriAsDocument(uri, true, 0);
    }

    private void sendUriAsDocument(Uri uri, boolean z, int i) {
        Uri uri2;
        String str;
        String str2;
        if (uri == null) {
            return;
        }
        String string = uri.toString();
        boolean z2 = true;
        if (string.contains("com.google.android.apps.photos.contentprovider")) {
            try {
                String str3 = string.split("/1/")[1];
                int iIndexOf = str3.indexOf("/ACTUAL");
                uri2 = iIndexOf != -1 ? Uri.parse(URLDecoder.decode(str3.substring(0, iIndexOf), "UTF-8")) : uri;
            } catch (Exception e) {
                FileLog.e(e);
                uri2 = uri;
            }
        } else {
            uri2 = uri;
        }
        String path = AndroidUtilities.getPath(uri2);
        if (BuildVars.NO_SCOPED_STORAGE) {
            if (path == null) {
                String string2 = uri2.toString();
                String strCopyFileToCache = MediaController.copyFileToCache(uri2, "file");
                if (strCopyFileToCache == null) {
                    showAttachmentError();
                    return;
                } else {
                    str2 = string2;
                    str = strCopyFileToCache;
                }
            } else {
                str = path;
                str2 = str;
            }
            z2 = false;
        } else {
            str = path;
            str2 = str;
        }
        fillEditingMediaWithCaption(null, null);
        if (z2) {
            SendMessagesHelper.prepareSendingDocument(getAccountInstance(), null, null, uri2, null, null, this.dialog_id, this.replyingMessageObject, getThreadMessage(), null, this.replyingQuote, this.editingMessageObject, z, i, null, this.quickReplyShortcut, getQuickReplyId(), false);
        } else {
            SendMessagesHelper.prepareSendingDocument(getAccountInstance(), str, str2, null, null, null, this.dialog_id, this.replyingMessageObject, getThreadMessage(), null, this.replyingQuote, this.editingMessageObject, z, i, null, this.quickReplyShortcut, getQuickReplyId(), false);
        }
        hideFieldPanel(false);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int i, int i2, final Intent intent) {
        ChatAttachAlert chatAttachAlert;
        String path;
        super.onActivityResultFragment(i, i2, intent);
        if (i2 == -1) {
            if (i == 0 || i == 2) {
                createChatAttachView();
                ChatAttachAlert chatAttachAlert2 = this.chatAttachAlert;
                if (chatAttachAlert2 != null) {
                    chatAttachAlert2.getPhotoLayout().onActivityResultFragment(i, intent, this.currentPicturePath);
                }
                this.currentPicturePath = null;
                return;
            }
            if (i == 1) {
                if (intent == null || intent.getData() == null) {
                    showAttachmentError();
                    return;
                }
                ChatAttachAlert chatAttachAlert3 = this.chatAttachAlert;
                if (chatAttachAlert3 != null && (chatAttachAlert3.getCurrentAttachLayout() instanceof ChatAttachAlertRichLayout)) {
                    ((ChatAttachAlertRichLayout) this.chatAttachAlert.getCurrentAttachLayout()).onExternalMediaPicked(intent);
                    return;
                }
                final Uri data = intent.getData();
                if (data.toString().contains(MediaStreamTrack.VIDEO_TRACK_KIND)) {
                    try {
                        path = AndroidUtilities.getPath(data);
                    } catch (Exception e) {
                        FileLog.e(e);
                        path = null;
                    }
                    if (path == null) {
                        showAttachmentError();
                    }
                    if (this.paused) {
                        this.startVideoEdit = path;
                    } else {
                        openVideoEditor(path, null);
                    }
                } else if (this.editingMessageObject == null && this.chatMode == 1) {
                    AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.dialog_id, new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda121
                        @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                        public final void didSelectDate(boolean z, int i3, int i4) {
                            this.f$0.lambda$onActivityResultFragment$190(data, z, i3, i4);
                        }
                    }, this.themeDelegate);
                } else {
                    fillEditingMediaWithCaption(null, null);
                    SendMessagesHelper.prepareSendingPhoto(getAccountInstance(), null, data, this.dialog_id, this.replyingMessageObject, getThreadMessage(), this.replyingQuote, null, null, null, null, 0, this.editingMessageObject, true, 0, this.chatMode, this.quickReplyShortcut, getQuickReplyId());
                }
                afterMessageSend();
                return;
            }
            if (i != 21) {
                if (i != 28 || (chatAttachAlert = this.chatAttachAlert) == null) {
                    return;
                }
                chatAttachAlert.onPollAttachFilePicker(intent);
                return;
            }
            if (intent == null) {
                showAttachmentError();
                return;
            }
            if (this.editingMessageObject == null && this.chatMode == 1) {
                if (intent.getData() != null) {
                    ChatAttachAlert chatAttachAlert4 = this.chatAttachAlert;
                    if (chatAttachAlert4 != null) {
                        chatAttachAlert4.lambda$new$0();
                    }
                    AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.dialog_id, new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda122
                        @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                        public final void didSelectDate(boolean z, int i3, int i4) {
                            this.f$0.lambda$onActivityResultFragment$191(intent, z, i3, i4);
                        }
                    }, this.themeDelegate);
                    return;
                }
                if (intent.getClipData() != null) {
                    ChatAttachAlert chatAttachAlert5 = this.chatAttachAlert;
                    if (chatAttachAlert5 != null) {
                        chatAttachAlert5.lambda$new$0();
                    }
                    AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), this.dialog_id, new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda123
                        @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                        public final void didSelectDate(boolean z, int i3, int i4) {
                            this.f$0.lambda$onActivityResultFragment$192(intent, z, i3, i4);
                        }
                    }, this.themeDelegate);
                    return;
                }
                showAttachmentError();
            } else {
                fillEditingMediaWithCaption(null, null);
                if (intent.getData() != null) {
                    sendUriAsDocument(intent.getData());
                } else if (intent.getClipData() != null) {
                    ClipData clipData = intent.getClipData();
                    for (int i3 = 0; i3 < clipData.getItemCount(); i3++) {
                        sendUriAsDocument(clipData.getItemAt(i3).getUri());
                    }
                } else {
                    showAttachmentError();
                }
            }
            ChatAttachAlert chatAttachAlert6 = this.chatAttachAlert;
            if (chatAttachAlert6 != null) {
                chatAttachAlert6.lambda$new$0();
            }
            afterMessageSend();
        }
    }

    public void lambda$didReceivedNotification2$196(Object[] objArr, StickersAlert stickersAlert, boolean z, TLRPC.StickerSet stickerSet, DialogInterface dialogInterface) {
        if (objArr.length > 2) {
            Object obj = objArr[2];
            if (obj instanceof TLRPC.Document) {
                TLRPC.Document document = (TLRPC.Document) obj;
                if (objArr.length > 3) {
                    Object obj2 = objArr[3];
                    if (obj2 instanceof String) {
                        document.localThumbPath = (String) obj2;
                    }
                }
                BulletinFactory.of(stickersAlert.container, this.resourceProvider).createEmojiBulletin(document, LocaleController.formatString(z ? R.string.StickersStickerEditedInSetToast : R.string.StickersStickerAddedToSetToast, stickerSet.title)).setDuration(2750).show(true);
            }
        }
    }

    public void lambda$didReceivedNotification3$198(int i) {
        INavigationLayout iNavigationLayout = this.parentLayout;
        if (iNavigationLayout == null) {
            return;
        }
        BaseFragment backgroundFragment = iNavigationLayout.getBackgroundFragment();
        if (backgroundFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) backgroundFragment;
            if (chatActivity.getDialogId() == this.dialog_id) {
                finishFragment();
                chatActivity.scrollToMessageId(i, 0, true, 0, true, 0);
                return;
            }
        }
        presentFragment(of(this.dialog_id, i));
    }

    void lambda$didReceivedNotification4$199() {
        BulletinFactory.of(this).createSimpleBulletin(R.raw.chats_infotip, LocaleController.getString(R.string.BoostingRemoveRestrictionsSuccessTitle), LocaleController.getString(R.string.BoostingRemoveRestrictionsSuccessSubTitle)).show();
    }

    public void lambda$didReceivedNotification5$202(Object[] objArr, long j) {
        TLRPC.MessageMedia messageMedia;
        MessageObject messageObjectForUpdate = getMessageObjectForUpdate(j, ((Integer) objArr[1]).intValue());
        if (messageObjectForUpdate == null || (messageMedia = messageObjectForUpdate.messageOwner.media) == null) {
            return;
        }
        messageMedia.extended_media = (ArrayList) objArr[2];
        messageObjectForUpdate.forceUpdate = true;
        messageObjectForUpdate.setType();
        updateChangedMessageObject(messageObjectForUpdate, false);
    }

    public void lambda$didReceivedNotification5$205() {
        AndroidUtilities.forEachViews((RecyclerView) this.chatListView, (Consumer<View>) new Consumer() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda184
            @Override // com.google.android.exoplayer2.util.Consumer
            public final void accept(Object obj) {
                ChatActivity.m7575$r8$lambda$L2DNVwHj3WHGcsv3r_4Nze2dsM((View) obj);
            }
        });
        ChatActivityAdapter chatActivityAdapter = this.chatAdapter;
        if (chatActivityAdapter != null) {
            chatActivityAdapter.notifyDataSetChanged(true);
        }
    }

    public static void lambda$didReceivedNotification5$210(ArrayList arrayList, int i) {
        replaceMessageObjects(arrayList, i, false);
    }

    void lambda$didReceivedNotification6$211() {
        checkScrollForLoad(false);
    }

    public static void lambda$didReceivedNotification7$213(int i) {
        playReactionAnimation(Integer.valueOf(i));
    }

    public static void lambda$didReceivedNotification7$217(long j, boolean z, boolean z2) {
        if (AndroidUtilities.isContextSafe(getContext())) {
            TagEditCell.showSheet(getContext(), this.currentAccount, -j, getUserConfig().getCurrentUser(), null, z, z2, getResourceProvider());
        }
    }

    private int calculateHeightUntilOutOwner(int i) {
        MessageObject messageObject;
        int additionalPaddingHeight;
        int height = this.chatListView.getHeight();
        int iMax = 0;
        long j = 0;
        while (true) {
            View viewFindViewByPosition = this.chatListView.findViewByPosition(i);
            if (viewFindViewByPosition == null) {
                break;
            }
            if (viewFindViewByPosition instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) viewFindViewByPosition;
                messageObject = chatMessageCell.getMessageObject();
                additionalPaddingHeight = chatMessageCell.getAdditionalPaddingHeight();
            } else if (viewFindViewByPosition instanceof ChatActionCell) {
                messageObject = ((ChatActionCell) viewFindViewByPosition).getMessageObject();
                additionalPaddingHeight = 0;
            } else {
                continue;
            }
            if (messageObject != null) {
                long groupId = messageObject.getGroupId();
                boolean zIsOut = messageObject.isOut();
                if (j != 0 && j != groupId) {
                    break;
                }
                height = Math.min(height, viewFindViewByPosition.getTop());
                iMax = Math.max(iMax, viewFindViewByPosition.getBottom() - additionalPaddingHeight);
                if (zIsOut) {
                    if (groupId == 0) {
                        break;
                    }
                    j = groupId;
                }
                i++;
            } else {
                continue;
            }
        }
        return Math.max(0, iMax - height);
    }

    public void showQuoteMessageUpdate() {
        if (this.quoteMessageUpdateAlert != null) {
            return;
        }
        this.quoteMessageUpdateAlert = new AlertDialog.Builder(getContext(), getResourceProvider()).setTitle(LocaleController.getString(R.string.UpdatedQuoteTitle)).setMessage(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.UpdatedQuoteMessage, this.replyingMessageObject != null ? getMessagesController().getFullName(this.replyingMessageObject.getSenderId()) : _UrlKt.FRAGMENT_ENCODE_SET))).setPositiveButton(LocaleController.getString(R.string.Edit), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda372
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                this.f$0.lambda$showQuoteMessageUpdate$218(alertDialog, i);
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda373
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                this.f$0.lambda$showQuoteMessageUpdate$219(alertDialog, i);
            }
        }).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda374
            @Override // android.content.DialogInterface.OnDismissListener
            public final void onDismiss(DialogInterface dialogInterface) {
                this.f$0.lambda$showQuoteMessageUpdate$220(dialogInterface);
            }
        }).show();
    }

    public void lambda$showQuoteMessageUpdate$219(AlertDialog alertDialog, int i) {
        hideFieldPanel(true);
    }

    public void lambda$new$221() {
        this.lastTranslationCheck = System.currentTimeMillis();
        if (this.chatListView != null && this.chatAdapter != null) {
            int iMin = Integer.MAX_VALUE;
            int iMax = Integer.MIN_VALUE;
            for (int i = 0; i < this.chatListView.getChildCount(); i++) {
                View childAt = this.chatListView.getChildAt(i);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    if (chatMessageCell.getCurrentMessagesGroup() != null) {
                        for (int i2 = 0; i2 < chatMessageCell.getCurrentMessagesGroup().messages.size(); i2++) {
                            int id = chatMessageCell.getCurrentMessagesGroup().messages.get(i2).getId();
                            iMin = Math.min(iMin, id);
                            iMax = Math.max(iMax, id);
                        }
                    } else if (chatMessageCell.getMessageObject() != null) {
                        int id2 = chatMessageCell.getMessageObject().getId();
                        iMin = Math.min(iMin, id2);
                        iMax = Math.max(iMax, id2);
                    }
                }
            }
            if (iMin <= iMax) {
                ArrayList arrayList = new ArrayList();
                for (int i3 = 0; i3 < this.messages.size(); i3++) {
                    MessageObject messageObject = this.messages.get(i3);
                    MessageObject.GroupedMessages groupedMessages = this.groupedMessagesMap.get(messageObject.getGroupId());
                    if (groupedMessages != null) {
                        if (!arrayList.contains(Long.valueOf(groupedMessages.groupId))) {
                            for (int i4 = 0; i4 < groupedMessages.messages.size(); i4++) {
                                MessageObject messageObject2 = groupedMessages.messages.get(i4);
                                if (messageObject2 != null) {
                                    int id3 = messageObject2.getId();
                                    getMessagesController().getTranslateController().checkTranslation(messageObject2, id3 >= iMin + (-7) && id3 <= iMax + 7);
                                }
                            }
                            arrayList.add(Long.valueOf(groupedMessages.groupId));
                        }
                    } else {
                        int id4 = messageObject.getId();
                        getMessagesController().getTranslateController().checkTranslation(messageObject, id4 >= iMin + (-7) && id4 <= iMax + 7);
                    }
                }
            }
        }
        if (this.currentPinnedMessageId > 0 && this.pinnedMessageObjects != null) {
            getMessagesController().getTranslateController().checkTranslation(this.pinnedMessageObjects.get(Integer.valueOf(this.currentPinnedMessageId)), true);
        }
        updateTranslateItemVisibility();
    }

    private void checkSecretMessageForLocation(MessageObject messageObject) {
        if (messageObject.type != 4 || this.locationAlertShown || SharedConfig.isSecretMapPreviewSet()) {
            return;
        }
        this.locationAlertShown = true;
        AlertsCreator.showSecretLocationAlert(getParentActivity(), this.currentAccount, new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda152
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$checkSecretMessageForLocation$222();
            }
        }, true, this.themeDelegate);
    }

    public void lambda$showGigagroupConvertAlert$225() {
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (chatFull == null || this.paused) {
            return;
        }
        TLRPC.Chat chat = this.currentChat;
        if (chat.creator && chat.megagroup && !chat.gigagroup && chatFull.pending_suggestions.contains("CONVERT_GIGAGROUP") && this.visibleDialog == null) {
            SharedPreferences notificationsSettings = MessagesController.getNotificationsSettings(this.currentAccount);
            int i = notificationsSettings.getInt("group_convert_time", 0);
            int i2 = BuildVars.DEBUG_PRIVATE_VERSION ? 120 : 604800;
            int currentTime = getConnectionsManager().getCurrentTime();
            if (Math.abs(currentTime - i) < i2 || this.visibleDialog != null || getParentActivity() == null) {
                return;
            }
            notificationsSettings.edit().putInt("group_convert_time", currentTime).apply();
            showDialog(AlertsCreator.createGigagroupConvertAlert(getParentActivity(), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda306
                @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                public final void onClick(AlertDialog alertDialog, int i3) {
                    this.f$0.lambda$showGigagroupConvertAlert$223(alertDialog, i3);
                }
            }, new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda307
                @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                public final void onClick(AlertDialog alertDialog, int i3) {
                    this.f$0.lambda$showGigagroupConvertAlert$224(alertDialog, i3);
                }
            }).create());
        }
    }

    public class AnonymousClass96 extends GigagroupConvertAlert {
        public AnonymousClass96(Context context, BaseFragment baseFragment) {
            super(context, baseFragment);
        }

        @Override // org.telegram.ui.Components.GigagroupConvertAlert
        public void onCovert() {
            MessagesController messagesController = ChatActivity.this.getMessagesController();
            Activity parentActivity = ChatActivity.this.getParentActivity();
            ChatActivity chatActivity = ChatActivity.this;
            messagesController.convertToGigaGroup(parentActivity, chatActivity.currentChat, chatActivity, new MessagesStorage.BooleanCallback() { // from class: org.telegram.ui.ChatActivity$96$$ExternalSyntheticLambda0
                @Override 
                public final void run(boolean z) {
                    this.f$0.lambda$onCovert$0(z);
                }
            });
        }

        public void lambda$processNewMessages$226(long j, long j2) {
        BotForumHelper.getInstance(this.currentAccount).saveIsStreamingTopic(j, j2, false);
        this.updateStreamingTopic = null;
    }

    public public void lambda$migrateToNewChat$229(long j) {
        getMessagesController().loadFullChat(j, 0, true);
    }

    private void addToPolls(MessageObject messageObject, MessageObject messageObject2) {
        long pollId = messageObject.getPollId();
        if (pollId != 0) {
            ArrayList<MessageObject> arrayList = this.polls.get(pollId);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                this.polls.put(pollId, arrayList);
            }
            arrayList.add(messageObject);
            if (messageObject2 != null) {
                arrayList.remove(messageObject2);
            }
        }
    }

    public void showInfoHint(MessageObject messageObject, CharSequence charSequence, final int i) {
        BulletinFactory.of(this).createSimpleBulletin(R.raw.chats_infotip, charSequence, 9999).setDuration(Math.max(4000, Math.min(((charSequence == null ? 0 : charSequence.length()) / 50) * 1600, 10000))).setOnHideListener(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda219
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showInfoHint$230(i);
            }
        }).show(true);
        this.hintMessageObject = messageObject;
        this.hintMessageType = i;
    }

    public public void checkConversionDateTimeToast() {
        ChatMessageCell chatMessageCell;
        float timeY;
        if (this.shownConversionDateTimeToast || !this.isFullyVisible || !this.chatListView.isAttachedToWindow() || getContext() == null) {
            return;
        }
        int[] iArr = new int[2];
        int childCount = this.chatListView.getChildCount() - 1;
        while (true) {
            if (childCount < 0) {
                chatMessageCell = null;
                break;
            }
            View childAt = this.chatListView.getChildAt(childCount);
            if (childAt instanceof ChatMessageCell) {
                chatMessageCell = (ChatMessageCell) childAt;
                if (chatMessageCell.getMessageObject() != null && chatMessageCell.getMessageObject().messageOwner != null && chatMessageCell.getMessageObject().messageOwner.video_processing_pending) {
                    if (chatMessageCell.getCurrentPosition() == null) {
                        chatMessageCell.getLocationInWindow(iArr);
                        timeY = iArr[1] + chatMessageCell.getTimeY();
                        if (timeY >= AndroidUtilities.dp(240.0f) && timeY <= (AndroidUtilities.displaySize.y - AndroidUtilities.dp(25.0f)) - AndroidUtilities.navigationBarHeight) {
                            break;
                        }
                    } else if (chatMessageCell.getMessageObject() != null && (chatMessageCell.getCurrentPosition().flags & 8) != 0) {
                        if ((chatMessageCell.getCurrentPosition().flags & (chatMessageCell.getMessageObject().isOutOwner() ? 1 : 2)) != 0) {
                            chatMessageCell.getLocationInWindow(iArr);
                            timeY = iArr[1] + chatMessageCell.getTimeY();
                            if (timeY >= AndroidUtilities.dp(240.0f)) {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    }
                }
            }
            childCount--;
        }
        if (chatMessageCell != null) {
            this.shownConversionDateTimeToast = true;
            HintView2 rounding = new HintView2(getContext(), 3) { // from class: org.telegram.ui.ChatActivity.97
                @Override // android.view.View
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                    super.onLayout(z, i, i2, i3, i4);
                    setTranslationY(((-getTop()) - AndroidUtilities.dp(120.0f)) + ChatActivity.this.videoConversionTimeHintY);
                }
            }.setMultilineText(true).setTextAlign(Layout.Alignment.ALIGN_CENTER).setDuration(3500L).setHideByTouch(true).useScale(true).setMaxWidth(150.0f).setRounding(8.0f);
            this.videoConversionTimeHint = rounding;
            rounding.setText(LocaleController.getString(R.string.VideoConversionTimeInfo));
            this.contentView.addView(this.videoConversionTimeHint, LayoutHelper.createFrame(-1, 120.0f, 55, 16.0f, 0.0f, 16.0f, 0.0f));
            chatMessageCell.getLocationInWindow(iArr);
            this.videoConversionTimeHintY = iArr[1] + chatMessageCell.getTimeY();
            HintView2 hintView2 = this.videoConversionTimeHint;
            hintView2.setTranslationY(((-hintView2.getTop()) - AndroidUtilities.dp(120.0f)) + this.videoConversionTimeHintY);
            this.videoConversionTimeHint.setJointPx(0.0f, (-AndroidUtilities.dp(16.0f)) + iArr[0] + chatMessageCell.timeX + (chatMessageCell.timeWidth / 2.0f));
            this.videoConversionTimeHint.show();
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(new ChatActivity$$ExternalSyntheticLambda127(this));
        AndroidUtilities.runOnUIThread(new ChatActivity$$ExternalSyntheticLambda127(this), 2000L);
    }

    public void checkSavedMessagesHint() {
        if (this.checkedSavedMessagesHint) {
            return;
        }
        this.checkedSavedMessagesHint = true;
        if (this.savedMessagesHintShown || this.chatMode != 0 || getMessagesController().getSavedMessagesController().unsupported || getMessagesController().getSavedMessagesController().getAllCount() <= 2) {
            return;
        }
        if (this.savedMessagesHint != null && MessagesController.getGlobalMainSettings().getInt("savedhint", 0) < 1) {
            this.savedMessagesHint.show();
            this.savedMessagesHintShown = true;
            MessagesController.getGlobalMainSettings().edit().putInt("savedhint", MessagesController.getGlobalMainSettings().getInt("savedhint", 0) + 1).putInt("savedsearchhint", MessagesController.getGlobalMainSettings().getInt("savedsearchhint", 0) + 1).apply();
        } else {
            if (this.savedMessagesSearchHint != null && MessagesController.getGlobalMainSettings().getInt("savedsearchhint", 0) < 1) {
                this.savedMessagesSearchHint.show();
                this.savedMessagesHintShown = true;
                MessagesController.getGlobalMainSettings().edit().putInt("savedsearchhint", MessagesController.getGlobalMainSettings().getInt("savedsearchhint", 0) + 1).apply();
                return;
            }
            checkSavedMessagesTagHint();
        }
    }

    public void checkSavedMessagesTagHint() {
        ChatMessageCell chatMessageCell;
        HintView2 hintView2 = this.savedMessagesTagHint;
        if (hintView2 == null || hintView2.shown() || this.savedMessagesTagHintShown || !this.checkedSavedMessagesHint || this.savedMessagesTagHint == null || isMessagesSearchListVisible() || System.currentTimeMillis() - this.lastScrollTime <= 1800 || MessagesController.getGlobalMainSettings().getInt("savedsearchtaghint", 0) >= 1) {
            return;
        }
        int[] iArr = new int[2];
        int childCount = this.chatListView.getChildCount() - 1;
        while (true) {
            if (childCount < 0) {
                chatMessageCell = null;
                break;
            }
            View childAt = this.chatListView.getChildAt(childCount);
            if (childAt instanceof ChatMessageCell) {
                chatMessageCell = (ChatMessageCell) childAt;
                if (chatMessageCell.areTags() && !chatMessageCell.reactionsLayoutInBubble.reactionButtons.isEmpty()) {
                    chatMessageCell.getLocationInWindow(iArr);
                    float f = iArr[1] + chatMessageCell.reactionsLayoutInBubble.y;
                    if (f >= AndroidUtilities.dp(240.0f) && f <= (AndroidUtilities.displaySize.y - AndroidUtilities.dp(25.0f)) - AndroidUtilities.navigationBarHeight) {
                        break;
                    }
                }
            }
            childCount--;
        }
        if (chatMessageCell != null) {
            this.savedMessagesTagHintShown = true;
            ReactionsLayoutInBubble.ReactionButton reactionButton = chatMessageCell.reactionsLayoutInBubble.reactionButtons.get(0);
            chatMessageCell.getLocationInWindow(iArr);
            HintView2 hintView3 = this.savedMessagesTagHint;
            hintView3.setTranslationY(((iArr[1] - hintView3.getTop()) - AndroidUtilities.dp(120.0f)) + chatMessageCell.reactionsLayoutInBubble.y);
            this.savedMessagesTagHint.setJointPx(0.0f, (-AndroidUtilities.dp(16.0f)) + iArr[0] + chatMessageCell.reactionsLayoutInBubble.x + (reactionButton.width / 2.0f));
            this.savedMessagesTagHint.show();
            MessagesController.getGlobalMainSettings().edit().putInt("savedsearchtaghint", 1).apply();
            return;
        }
        AndroidUtilities.cancelRunOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda193
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.checkSavedMessagesTagHint();
            }
        });
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda193
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.checkSavedMessagesTagHint();
            }
        }, 2000L);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        hideTagSelector();
        if (!getMessagesController().premiumFeaturesBlocked() && getMessagesController().transcribeAudioTrialWeeklyNumber <= 0 && !getMessagesController().didPressTranscribeButtonEnough() && !getUserConfig().isPremium() && this.messages != null && !VoiceRecognitionController.isCustomRecognitionEnabled()) {
            for (int i = 0; i < this.messages.size(); i++) {
                MessageObject messageObject = this.messages.get(i);
                if (messageObject != null && !messageObject.isOutOwner() && ((messageObject.isVoice() || messageObject.isRoundVideo()) && !messageObject.isUnread() && (messageObject.isContentUnread() || ChatObject.isChannelAndNotMegaGroup(this.currentChat)))) {
                    TranscribeButton.showOffTranscribe(messageObject, false);
                }
            }
        }
        this.isFullyVisible = false;
        hideUndoViews();
        TranscribeButton.resetVideoTranscriptionsOpen();
        Browser.Progress progress = this.progressDialogCurrent;
        if (progress != null) {
            progress.cancel();
            this.progressDialogCurrent = null;
        }
        this.flagSecure.detach();
        super.onBecomeFullyHidden();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void saveKeyboardPositionBeforeTransition() {
        Runnable runnable = this.cancelFixedPositionRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null && this.contentView != null && chatActivityEnterView.getAdjustPanLayoutHelper() != null && !this.chatActivityEnterView.getAdjustPanLayoutHelper().animationInProgress()) {
            this.fixedKeyboardHeight = this.contentView.getKeyboardHeight();
        } else {
            this.fixedKeyboardHeight = -1;
        }
    }

    public void removeKeyboardPositionBeforeTransition() {
        if (this.fixedKeyboardHeight > 0) {
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda369
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$removeKeyboardPositionBeforeTransition$231();
                }
            };
            this.cancelFixedPositionRunnable = runnable;
            AndroidUtilities.runOnUIThread(runnable, 200L);
        }
    }

    public @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        String string;
        MessageObject messageObject;
        INavigationLayout iNavigationLayout;
        Bulletin bulletin;
        super.onTransitionAnimationEnd(z, z2);
        if (z && z2 && this.showPinBulletin && (bulletin = this.pinBulletin) != null) {
            bulletin.show();
            this.showPinBulletin = false;
        }
        Runnable runnable = this.cancelFixedPositionRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        this.fixedKeyboardHeight = -1;
        if (z) {
            checkShowBlur(false);
            this.openAnimationEnded = true;
            getNotificationCenter().onAnimationFinish(this.transitionAnimationIndex);
            NotificationCenter.getGlobalInstance().onAnimationFinish(this.transitionAnimationGlobalIndex);
            checkGroupCallJoin(this.lastCallCheckFromServer);
            Runnable runnable2 = this.pendingOnOpenAction;
            if (runnable2 != null) {
                this.pendingOnOpenAction = null;
                runnable2.run();
            }
            if (!((!ChatObject.isMonoForum(this.currentChat) || ChatObject.canManageMonoForum(this.currentAccount, this.currentChat)) ? false : this.chatActivityEnterView.showSendSuggestionHint()) && this.chatActivityEnterView.hasRecordVideo() && !this.chatActivityEnterView.isSendButtonVisible()) {
                showVoiceHint(false, this.chatActivityEnterView.isInVideoMode());
            }
            if (!z2 && (iNavigationLayout = this.parentLayout) != null && this.needRemovePreviousSameChatActivity) {
                int size = iNavigationLayout.getFragmentStack().size() - 1;
                for (int i = 0; i < size; i++) {
                    BaseFragment baseFragment = this.parentLayout.getFragmentStack().get(i);
                    if (baseFragment != this && (baseFragment instanceof ChatActivity)) {
                        ChatActivity chatActivity = (ChatActivity) baseFragment;
                        if (chatActivity.needRemovePreviousSameChatActivity && chatActivity.dialog_id == this.dialog_id && chatActivity.getTopicId() == getTopicId() && chatActivity.getChatMode() == getChatMode() && chatActivity.threadMessageId == this.threadMessageId && chatActivity.isReport() == isReport()) {
                            baseFragment.removeSelfFromStack();
                            break;
                        }
                    }
                }
            }
            showScheduledOrNoSoundHint();
            if (!z2 && this.firstOpen) {
                if (this.chatActivityEnterView != null && (((messageObject = this.threadMessageObject) != null && messageObject.getRepliesCount() == 0 && ChatObject.canSendMessages(this.currentChat) && !this.isTopic) || this.chatMode == 6)) {
                    this.chatActivityEnterView.setFieldFocused();
                    this.chatActivityEnterView.openKeyboard();
                }
                if (getMessagesController().isPromoDialog(this.dialog_id, true)) {
                    int i2 = getMessagesController().promoDialogType;
                    SharedPreferences globalNotificationsSettings = MessagesController.getGlobalNotificationsSettings();
                    if (i2 == MessagesController.PROMO_TYPE_PROXY) {
                        if (AndroidUtilities.getPrefIntOrLong(globalNotificationsSettings, "proxychannel", 0L) != this.dialog_id) {
                            string = LocaleController.getString(R.string.UseProxySponsorInfo);
                        } else {
                            string = null;
                        }
                    } else if (i2 == MessagesController.PROMO_TYPE_PSA) {
                        String str = getMessagesController().promoPsaType;
                        if (globalNotificationsSettings.getBoolean(str + "_shown", false)) {
                            string = null;
                        } else {
                            string = LocaleController.getString("PsaInfo_" + str);
                            if (TextUtils.isEmpty(string)) {
                                string = LocaleController.getString(R.string.PsaInfoDefault);
                            }
                        }
                    } else {
                        string = null;
                    }
                    if (!TextUtils.isEmpty(string)) {
                        checkTopUndoView();
                        if (this.topUndoView != null) {
                            if (i2 == MessagesController.PROMO_TYPE_PROXY) {
                                globalNotificationsSettings.edit().putLong("proxychannel", this.dialog_id).apply();
                            } else if (i2 == MessagesController.PROMO_TYPE_PSA) {
                                String str2 = getMessagesController().promoPsaType;
                                globalNotificationsSettings.edit().putBoolean(str2 + "_shown", true).apply();
                            }
                            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(string);
                            MessageObject.addLinks(false, spannableStringBuilder);
                            this.topUndoView.showWithAction(0L, 18, spannableStringBuilder, null, null);
                        }
                    }
                }
                this.firstOpen = false;
            }
            ChatActivityMemberRequestsDelegate chatActivityMemberRequestsDelegate = this.pendingRequestsDelegate;
            if (chatActivityMemberRequestsDelegate != null && z2) {
                chatActivityMemberRequestsDelegate.onBackToScreen();
            }
            updateMessagesVisiblePart(false);
        } else {
            getNotificationCenter().onAnimationFinish(this.transitionAnimationIndex);
            NotificationCenter.getGlobalInstance().onAnimationFinish(this.transitionAnimationGlobalIndex);
        }
        this.contentView.invalidate();
        if (!TextUtils.isEmpty(this.attachMenuBotToOpen)) {
            openAttachBotLayout(this.attachMenuBotToOpen);
            this.attachMenuBotToOpen = null;
        }
        checkGroupEmojiPackHint();
    }

    private void checkGroupEmojiPackHint() {
        TLRPC.ChatFull chatFull;
        TLRPC.StickerSet stickerSet;
        TLRPC.TL_messages_stickerSet groupStickerSetById;
        if (this.groupEmojiPackHint != null || !ChatObject.isMegagroup(this.currentChat) || (chatFull = getMessagesController().getChatFull(this.currentChat.id)) == null || this.chatActivityEnterView == null || getContext() == null || (stickerSet = chatFull.emojiset) == null) {
            return;
        }
        ChatActivityChannelButtonsLayout chatActivityChannelButtonsLayout = this.bottomChannelButtonsLayout;
        if (chatActivityChannelButtonsLayout == null || chatActivityChannelButtonsLayout.getVisibility() != 0) {
            HintsController.Hint hint = HintsController.Hint.GroupEmojiPackHintShown;
            if (hint.show()) {
                long j = stickerSet.thumb_document_id;
                if (j == 0 && (groupStickerSetById = getMediaDataController().getGroupStickerSetById(stickerSet)) != null && !groupStickerSetById.documents.isEmpty()) {
                    j = groupStickerSetById.documents.get(0).id;
                }
                if (j == 0 || getContext() == null) {
                    return;
                }
                hint.increment();
                HintView2 hintView2 = new HintView2(getContext(), 3);
                this.groupEmojiPackHint = hintView2;
                hintView2.setCloseButton(true);
                this.groupEmojiPackHint.setMultilineText(true);
                this.groupEmojiPackHint.setTextAlign(Layout.Alignment.ALIGN_CENTER);
                this.groupEmojiPackHint.setRounding(12.0f);
                SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("d");
                spannableStringBuilder.setSpan(new AnimatedEmojiSpan(j, this.groupEmojiPackHint.getTextPaint().getFontMetricsInt()), 0, spannableStringBuilder.length(), 33);
                SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(stickerSet.title);
                spannableStringBuilder2.setSpan(new TypefaceSpan(AndroidUtilities.bold()), 0, spannableStringBuilder2.length(), 34);
                spannableStringBuilder.append((CharSequence) " ");
                spannableStringBuilder.append((CharSequence) spannableStringBuilder2);
                this.groupEmojiPackHint.setText(AndroidUtilities.replaceCharSequence("%s", LocaleController.getString(R.string.GroupEmojiPackHint), spannableStringBuilder));
                HintView2 hintView3 = this.groupEmojiPackHint;
                hintView3.setMaxWidthPx(HintView2.cutInFancyHalf(hintView3.getText(), this.groupEmojiPackHint.getTextPaint()));
                this.groupEmojiPackHint.setDuration(-1L);
                this.groupEmojiPackHint.setPadding(AndroidUtilities.dp(6.0f), 0, AndroidUtilities.dp(6.0f), 0);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda8
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$checkGroupEmojiPackHint$232();
                    }
                }, 300L);
            }
        }
    }

    public void lambda$updateBottomOverlay$244() {
        ChatGreetingsView.showPremiumSheet(getContext(), this.currentAccount, this.dialog_id, this.themeDelegate);
    }

    public void lambda$updatePinnedMessageView$248(TLRPC.KeyboardButton keyboardButton, MessageObject messageObject, View view) {
        if (getParentActivity() != null) {
            if (this.bottomChannelButtonsLayout.getVisibility() != 0 || (keyboardButton instanceof TLRPC.TL_keyboardButtonSwitchInline) || (keyboardButton instanceof TLRPC.TL_keyboardButtonCallback) || (keyboardButton instanceof TLRPC.TL_keyboardButtonGame) || (keyboardButton instanceof TLRPC.TL_keyboardButtonUrl) || (keyboardButton instanceof TLRPC.TL_keyboardButtonBuy) || (keyboardButton instanceof TLRPC.TL_keyboardButtonUrlAuth) || (keyboardButton instanceof TLRPC.TL_keyboardButtonUserProfile)) {
                this.chatActivityEnterView.didPressedBotButton(keyboardButton, messageObject, messageObject);
            }
        }
    }

    public void lambda$updateTopPanel$266(final long j, final long j2) {
        StarsController.getInstance(this.currentAccount).getPaidRevenue(j, j2, new Utilities.Callback() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda299
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$updateTopPanel$265(j, j2, (Long) obj);
            }
        });
    }

    public void lambda$updateTopPanel$264(long j, long j2, Long l, Boolean bool) {
        StarsController.getInstance(this.currentAccount).stopPaidMessages(j, j2, l.longValue() > 0 && bool.booleanValue(), true);
    }

    private void checkListViewPaddings() {
        MessageObject messageObject;
        if (!this.wasManualScroll && (messageObject = this.unreadMessageObject) != null) {
            if (this.messages.indexOf(messageObject) >= 0) {
                this.fixPaddingsInLayout = true;
                View view = this.fragmentView;
                if (view != null) {
                    view.requestLayout();
                    return;
                }
                return;
            }
            return;
        }
        if (this.checkPaddingsRunnable != null) {
            return;
        }
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda291
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$checkListViewPaddings$267();
            }
        };
        this.checkPaddingsRunnable = runnable;
        AndroidUtilities.runOnUIThread(runnable);
    }

    public void lambda$onResume$268(DialogInterface dialogInterface) {
        FragmentContextView fragmentContextView = this.fragmentContextView;
        if (fragmentContextView != null) {
            fragmentContextView.checkImport(false);
        }
    }

    public @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        int i;
        boolean z;
        RecyclerListView.Holder holder;
        MessageObject messageObject;
        MessageObject messageObject2;
        ChatActivityChannelButtonsLayout chatActivityChannelButtonsLayout;
        super.onPause();
        int i2 = 0;
        scrolling = false;
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.setPauseNotifications(false);
            closeMenu();
        }
        getMessagesController().markDialogAsReadNow(this.dialog_id, this.threadMessageId);
        MediaController.getInstance().stopRaiseToEarSensors(this, true, true);
        this.paused = true;
        this.wasPaused = true;
        if (this.chatMode == 0) {
            getNotificationsController().setOpenedDialogId(0L, 0L);
        }
        Bulletin.removeDelegate(this);
        getMessagesController().setLastVisibleDialogId(this.dialog_id, this.chatMode == 1, false);
        if (!this.ignoreAttachOnPause && this.chatActivityEnterView != null && (chatActivityChannelButtonsLayout = this.bottomChannelButtonsLayout) != null && chatActivityChannelButtonsLayout.getVisibility() != 0) {
            this.chatActivityEnterView.onPause();
            this.chatActivityEnterView.setFieldFocused(false);
        }
        ChatAttachAlert chatAttachAlert = this.chatAttachAlert;
        if (chatAttachAlert != null) {
            if (!this.ignoreAttachOnPause) {
                chatAttachAlert.onPause();
            } else {
                this.ignoreAttachOnPause = false;
            }
        }
        ChatActivityFragmentView chatActivityFragmentView = this.contentView;
        if (chatActivityFragmentView != null) {
            chatActivityFragmentView.onPause();
        }
        int i3 = this.chatMode;
        if (i3 == 0 || ((i3 == 3 && getUserConfig().getClientUserId() == getSavedDialogId()) || (this.chatMode == 8 && ChatObject.isMonoForum(this.currentChat)))) {
            saveDraft();
            getMessagesController().lambda$sendTyping$172(0, this.dialog_id, this.threadMessageId);
        }
        int i4 = this.chatMode;
        if (i4 == 0 || i4 == 3) {
            if (!this.pausedOnLastMessage && !this.firstLoading && (!isThreadChat() || this.isTopic)) {
                SharedPreferences.Editor editorEdit = MessagesController.getNotificationsSettings(this.currentAccount).edit();
                if (this.chatLayoutManager != null) {
                    int i5 = 0;
                    while (true) {
                        if (i5 >= this.chatListView.getChildCount()) {
                            z = false;
                            break;
                        } else {
                            if ((this.chatListView.getChildAt(i5) instanceof ChatMessageCell) && ((ChatMessageCell) this.chatListView.getChildAt(i5)).getMessageObject().isSponsored()) {
                                z = true;
                                break;
                            }
                            i5++;
                        }
                    }
                    int iFindFirstVisibleItemPosition = this.chatLayoutManager.findFirstVisibleItemPosition();
                    if (iFindFirstVisibleItemPosition == 0 || z || (holder = (RecyclerListView.Holder) this.chatListView.findViewHolderForAdapterPosition(iFindFirstVisibleItemPosition)) == null) {
                        i = 0;
                    } else {
                        View view = holder.itemView;
                        if (view instanceof ChatMessageCell) {
                            messageObject = ((ChatMessageCell) view).getMessageObject();
                        } else {
                            messageObject = view instanceof ChatActionCell ? ((ChatActionCell) view).getMessageObject() : null;
                        }
                        if ((messageObject != null ? messageObject.getId() : 0) == 0) {
                            holder = (RecyclerListView.Holder) this.chatListView.findViewHolderForAdapterPosition(iFindFirstVisibleItemPosition + 1);
                        }
                        int i6 = iFindFirstVisibleItemPosition - 1;
                        boolean z2 = false;
                        int i7 = 0;
                        while (true) {
                            int i8 = this.chatAdapter.messagesStartRow;
                            if (i6 < i8) {
                                break;
                            }
                            int i9 = i6 - i8;
                            if (i9 >= 0 && i9 < this.messages.size() && (messageObject2 = this.messages.get(i9)) != null && messageObject2.getId() != 0) {
                                if ((!messageObject2.isOut() || messageObject2.messageOwner.from_scheduled) && messageObject != null && messageObject.isUnread()) {
                                    z2 = true;
                                }
                                if (i7 > 2) {
                                    break;
                                } else {
                                    i7++;
                                }
                            }
                            i6--;
                        }
                        if (holder == null || z2) {
                            i = 0;
                        } else {
                            int id = messageObject != null ? messageObject.getId() : 0;
                            if ((id <= 0 || this.currentEncryptedChat != null) && (id >= 0 || this.currentEncryptedChat == null)) {
                                i = 0;
                            } else {
                                int bottom = holder.itemView.getBottom() - this.chatListView.getMeasuredHeight();
                                if (BuildVars.LOGS_ENABLED) {
                                    FileLog.d("save offset = " + bottom + " for mid " + id);
                                }
                                i = bottom;
                                i2 = id;
                            }
                        }
                    }
                } else {
                    i = 0;
                }
                if (i2 != 0) {
                    editorEdit.putInt("diditem" + NotificationsController.getSharedPrefKey(this.dialog_id, getTopicId()), i2);
                    editorEdit.putInt("diditemo" + NotificationsController.getSharedPrefKey(this.dialog_id, getTopicId()), i);
                } else {
                    this.pausedOnLastMessage = true;
                    editorEdit.remove("diditem" + NotificationsController.getSharedPrefKey(this.dialog_id, getTopicId()));
                    editorEdit.remove("diditemo" + NotificationsController.getSharedPrefKey(this.dialog_id, getTopicId()));
                }
                editorEdit.apply();
            }
            if (this.currentUser != null) {
                this.chatLeaveTime = System.currentTimeMillis();
                updateInformationForScreenshotDetector();
            }
            hideUndoViews();
        }
        ChatListItemAnimator chatListItemAnimator = this.chatListItemAnimator;
        if (chatListItemAnimator != null) {
            chatListItemAnimator.endAnimations();
        }
        RecyclerAnimationScrollHelper recyclerAnimationScrollHelper = this.chatScrollHelper;
        if (recyclerAnimationScrollHelper != null) {
            recyclerAnimationScrollHelper.cancel();
        }
        if (AvatarPreviewer.hasVisibleInstance()) {
            AvatarPreviewer.getInstance().close();
        }
    }

    public void setResolvedChatLink(TL_account.resolvedBusinessChatLinks resolvedbusinesschatlinks) {
        this.resolvedChatLink = resolvedbusinesschatlinks;
    }

    public void applyChatLinkMessageMaybe() {
        ArrayList<TLRPC.MessageEntity> arrayList;
        ArrayList<TLRPC.MessageEntity> arrayList2;
        if (this.chatActivityEnterView == null || this.chatMode != 0 || this.resolvedChatLink == null) {
            return;
        }
        if (!UserConfig.getInstance(this.currentAccount).isPremium() && UserConfig.getInstance(this.currentAccount).getClientUserId() != this.dialog_id && !LocaleUtils.canUseLocalPremiumEmojis(this.currentAccount) && (arrayList2 = this.resolvedChatLink.entities) != null) {
            arrayList = (ArrayList) arrayList2.stream().filter(new Predicate() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda229
                @Override // java.util.function.Predicate
                public final boolean test(Object obj) {
                    return this.f$0.lambda$applyChatLinkMessageMaybe$270((TLRPC.MessageEntity) obj);
                }
            }).collect(Collectors.toCollection(new ExportMapper$$ExternalSyntheticLambda2()));
        } else {
            arrayList = this.resolvedChatLink.entities;
        }
        CharSequence charSequenceApplyMessageEntities = ChatActivityEnterView.applyMessageEntities(arrayList, this.resolvedChatLink.message, this.chatActivityEnterView.getEditField().getPaint().getFontMetricsInt());
        if (charSequenceApplyMessageEntities != null && charSequenceApplyMessageEntities.length() > 0 && charSequenceApplyMessageEntities.charAt(0) == '@') {
            charSequenceApplyMessageEntities = TextUtils.concat(" ", charSequenceApplyMessageEntities);
        }
        this.chatActivityEnterView.setFieldText(charSequenceApplyMessageEntities, true, true);
        this.resolvedChatLink = null;
    }

    void lambda$applyDraftMaybe$271() {
        ChatActivityEnterView chatActivityEnterView;
        if (BaseFragment.hasSheets(this) || (chatActivityEnterView = this.chatActivityEnterView) == null) {
            return;
        }
        chatActivityEnterView.setFieldFocused(true);
        this.chatActivityEnterView.openKeyboard();
    }

    private void checkNewMessagesOnQuoteEdit(boolean z) {
        if (this.replyingMessageObject == null) {
            return;
        }
        for (int i = 0; i < this.messages.size(); i++) {
            MessageObject messageObject = this.messages.get(i);
            if (messageObject != null && messageObject.getId() == this.replyingMessageObject.getId() && messageObject.getDialogId() == this.replyingMessageObject.getDialogId()) {
                this.replyingMessageObject = messageObject;
                ReplyQuote replyQuote = this.replyingQuote;
                if (replyQuote != null) {
                    replyQuote.checkEdit(messageObject);
                }
                if (z) {
                    MessagePreviewParams messagePreviewParams = this.messagePreviewParams;
                    if (messagePreviewParams != null) {
                        MessageObject messageObject2 = this.replyingMessageObject;
                        MessageObject.GroupedMessages group = this.replyingQuoteGroup;
                        if (group == null) {
                            group = getGroup(messageObject2.getGroupId());
                        }
                        messagePreviewParams.updateReply(messageObject2, group, this.dialog_id, this.replyingQuote);
                    }
                    fallbackFieldPanel();
                    return;
                }
                return;
            }
        }
    }

    private void updateInformationForScreenshotDetector() {
        if (this.currentUser == null) {
            return;
        }
        if (this.currentEncryptedChat != null) {
            ArrayList<Long> arrayList = new ArrayList<>();
            ChatListRecyclerView chatListRecyclerView = this.chatListView;
            if (chatListRecyclerView != null) {
                int childCount = chatListRecyclerView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View childAt = this.chatListView.getChildAt(i);
                    MessageObject messageObject = childAt instanceof ChatMessageCell ? ((ChatMessageCell) childAt).getMessageObject() : null;
                    if (messageObject != null && messageObject.getId() < 0) {
                        long j = messageObject.messageOwner.random_id;
                        if (j != 0) {
                            arrayList.add(Long.valueOf(j));
                        }
                    }
                }
            }
            MediaController.getInstance().setLastVisibleMessageIds(this.currentAccount, this.chatEnterTime, this.chatLeaveTime, this.currentUser, this.currentEncryptedChat, arrayList, 0);
            return;
        }
        SecretMediaViewer secretMediaViewer = SecretMediaViewer.getInstance();
        MessageObject currentMessageObject = secretMediaViewer.getCurrentMessageObject();
        if (currentMessageObject == null || currentMessageObject.isOut()) {
            return;
        }
        MediaController.getInstance().setLastVisibleMessageIds(this.currentAccount, secretMediaViewer.getOpenTime(), secretMediaViewer.getCloseTime(), this.currentUser, null, null, currentMessageObject.getId());
    }

    public boolean fixLayoutInternal() {
        boolean z;
        MessageObject.GroupedMessages currentMessagesGroup;
        int childCount = this.chatListView.getChildCount();
        HashMap map = null;
        int i = 0;
        while (true) {
            z = true;
            if (i >= childCount) {
                break;
            }
            View childAt = this.chatListView.getChildAt(i);
            if ((childAt instanceof ChatMessageCell) && (currentMessagesGroup = ((ChatMessageCell) childAt).getCurrentMessagesGroup()) != null && currentMessagesGroup.hasSibling && !currentMessagesGroup.messages.isEmpty()) {
                if (map == null) {
                    map = new HashMap();
                }
                if (!map.containsKey(Long.valueOf(currentMessagesGroup.groupId))) {
                    map.put(Long.valueOf(currentMessagesGroup.groupId), currentMessagesGroup);
                    ArrayList<MessageObject> arrayList = currentMessagesGroup.messages;
                    int iIndexOf = this.messages.indexOf(arrayList.get(arrayList.size() - 1));
                    if (iIndexOf >= 0) {
                        ChatActivityAdapter chatActivityAdapter = this.chatAdapter;
                        chatActivityAdapter.notifyItemRangeChanged(iIndexOf + chatActivityAdapter.messagesStartRow, currentMessagesGroup.messages.size());
                        this.chatListView.setItemAnimator(null);
                    }
                }
            }
            i++;
        }
        if (!AndroidUtilities.isTablet()) {
            return true;
        }
        if (AndroidUtilities.isSmallTablet() && ApplicationLoader.applicationContext.getResources().getConfiguration().orientation == 1) {
            this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        } else {
            ActionBar actionBar = this.actionBar;
            INavigationLayout iNavigationLayout = this.parentLayout;
            if (iNavigationLayout != null && !iNavigationLayout.getFragmentStack().isEmpty() && this.parentLayout.getFragmentStack().get(0) != this && this.parentLayout.getFragmentStack().size() != 1) {
                z = false;
            }
            actionBar.setBackButtonDrawable(new BackDrawable(z));
        }
        updateFeedTabBackButton();
        return false;
    }

    private void fixLayout() {
        ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
        if (chatAvatarContainer != null) {
            chatAvatarContainer.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.ChatActivity.107
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    ChatAvatarContainer chatAvatarContainer2 = ChatActivity.this.avatarContainer;
                    if (chatAvatarContainer2 != null) {
                        chatAvatarContainer2.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    return ChatActivity.this.fixLayoutInternal();
                }
            });
        }
    }

    public boolean maybePlayVisibleVideo() {
        AnimatedFileDrawable animation;
        MessageObject messageObject;
        ImageReceiver photoImage;
        AnimatedFileDrawable animation2;
        ChatMessageCell messageCell;
        if (this.chatListView == null) {
            return false;
        }
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (playingMessageObject != null && !playingMessageObject.isVideo()) {
            return false;
        }
        HintView hintView = this.noSoundHintView;
        if (hintView == null || hintView.getTag() == null || (messageCell = this.noSoundHintView.getMessageCell()) == null) {
            animation = null;
            messageObject = null;
        } else {
            ImageReceiver photoImage2 = messageCell.getPhotoImage();
            animation = photoImage2.getAnimation();
            if (animation != null) {
                messageObject = messageCell.getMessageObject();
                this.scrollToVideo = ((float) messageCell.getTop()) + photoImage2.getImageY2() > ((float) this.chatListView.getMeasuredHeight());
            } else {
                messageObject = null;
            }
        }
        if (messageObject == null) {
            int childCount = this.chatListView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.chatListView.getChildAt(i);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    MessageObject messageObject2 = chatMessageCell.getMessageObject();
                    boolean zIsRoundVideo = messageObject2.isRoundVideo();
                    if (!messageObject2.isRoundOnce() && !messageObject2.isVoiceOnce() && ((messageObject2.isVideo() || zIsRoundVideo) && messageObject2.videoEditedInfo == null && (animation2 = (photoImage = chatMessageCell.getPhotoImage()).getAnimation()) != null)) {
                        float top = childAt.getTop() + photoImage.getImageY();
                        float imageHeight = photoImage.getImageHeight() + top;
                        if (imageHeight >= 0.0f && top <= this.chatListView.getMeasuredHeight()) {
                            if (messageObject != null && top < 0.0f) {
                                break;
                            }
                            this.scrollToVideo = top < 0.0f || imageHeight > ((float) this.chatListView.getMeasuredHeight());
                            if (top >= 0.0f && imageHeight <= this.chatListView.getMeasuredHeight()) {
                                messageObject = messageObject2;
                                animation = animation2;
                                break;
                            }
                            messageObject = messageObject2;
                            animation = animation2;
                        }
                    }
                }
            }
        }
        if (messageObject == null || MediaController.getInstance().isPlayingMessage(messageObject)) {
            return false;
        }
        hideHints(true);
        if (messageObject.isRoundVideo()) {
            boolean zPlayMessage = MediaController.getInstance().playMessage(messageObject);
            MediaController.getInstance().setVoiceMessagesPlaylist(zPlayMessage ? createVoiceMessagesPlaylist(messageObject, false) : null, false);
            return zPlayMessage;
        }
        SharedConfig.setNoSoundHintShowed(true);
        messageObject.audioProgress = animation.getCurrentProgress();
        messageObject.audioProgressMs = animation.getCurrentProgressMs();
        animation.stop();
        if (PhotoViewer.isPlayingMessageInPip(messageObject)) {
            PhotoViewer.getPipInstance().destroyPhotoViewer();
        }
        return MediaController.getInstance().playMessage(messageObject);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration configuration) {
        MessageObject playingMessageObject;
        fixLayout();
        Dialog dialog = this.visibleDialog;
        if (dialog instanceof DatePickerDialog) {
            dialog.dismiss();
        }
        closeMenu();
        if (AndroidUtilities.isTablet()) {
            return;
        }
        if (configuration.orientation == 2) {
            if ((PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) || (playingMessageObject = MediaController.getInstance().getPlayingMessageObject()) == null || !playingMessageObject.isVideo()) {
                return;
            }
            PhotoViewer.getInstance().setParentActivity(this, this.themeDelegate);
            getFileLoader().setLoadingVideoForPlayer(playingMessageObject.getDocument(), false);
            MediaController.getInstance().cleanupPlayer(true, true, false, true);
            PhotoViewer photoViewer = PhotoViewer.getInstance();
            int i = playingMessageObject.type;
            if (photoViewer.openPhoto(playingMessageObject, i != 0 ? this.dialog_id : 0L, i != 0 ? this.mergeDialogId : 0L, i != 0 ? getTopicId() : 0L, this.photoViewerProvider, false)) {
                PhotoViewer.getInstance().setParentChatActivity(this);
            }
            hideHints(false);
            MediaController.getInstance().resetGoingToShowMessageObject();
            return;
        }
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isOpenedFullScreenVideo()) {
            PhotoViewer.getInstance().injectVideoPlayerToMediaController();
            PhotoViewer.getInstance().closePhoto(false, true);
        }
    }

    public void createDeleteMessagesAlert(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages) {
        createDeleteMessagesAlert(messageObject, groupedMessages, false);
    }

    private void createDeleteMessagesAlert(MessageObject messageObject, MessageObject.GroupedMessages groupedMessages, boolean z) {
        if (messageObject == null && this.selectedMessagesIds[0].size() + this.selectedMessagesIds[1].size() == 0) {
            return;
        }
        AlertsCreator.createDeleteMessagesAlert(this, this.currentUser, this.currentChat, this.currentEncryptedChat, this.chatInfo, this.mergeDialogId, messageObject, this.selectedMessagesIds, groupedMessages, (int) getTopicId(), this.chatMode, null, new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda320
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$createDeleteMessagesAlert$272();
            }
        }, z ? new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda321
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$createDeleteMessagesAlert$273();
            }
        } : null, this.themeDelegate);
    }

    public void lambda$createMenu$274(MessageObject messageObject) {
        scrollToMessageId(messageObject.getReplyMsgId(), messageObject.messageOwner.id, true, messageObject.getDialogId() == this.mergeDialogId ? 1 : 0, false, 0, null, ((TLRPC.TL_messageActionPollAppendAnswer) messageObject.messageOwner.action).answer.option, null);
    }

    public void lambda$createMenu$276(MessageObject messageObject) {
        scrollToMessageId(messageObject.getReplyMsgId(), messageObject.messageOwner.id, true, messageObject.getDialogId() == this.mergeDialogId ? 1 : 0, false, 0);
    }

    public void lambda$instantiateItem$0(ReactedUsersListView reactedUsersListView, ArrayList arrayList) {
            if (ChatActivity.this.getParentActivity() == null || ChatActivity.this.getContext() == null) {
                return;
            }
            ChatActivity chatActivity = ChatActivity.this;
            EmojiPacksAlert emojiPacksAlert = new EmojiPacksAlert(chatActivity, chatActivity.getParentActivity(), ChatActivity.this.themeDelegate, arrayList) { // from class: org.telegram.ui.ChatActivity.109.1
                @Override // org.telegram.ui.Components.EmojiPacksAlert, org.telegram.ui.ActionBar.BottomSheet, android.app.Dialog, android.content.DialogInterface, org.telegram.ui.ActionBar.BaseFragment.AttachedSheet
                public void lambda$new$0() {
                    super.lambda$new$0();
                    ChatActivity.this.dimBehindView(false);
                }
            };
            emojiPacksAlert.setCalcMandatoryInsets(ChatActivity.this.isKeyboardVisible());
            emojiPacksAlert.setDimBehind(false);
            ChatActivity.this.closeMenu(false);
            ChatActivity.this.showDialog(emojiPacksAlert);
        }

        public void m8267$r8$lambda$4JPcVwtpnSQ_mQTtr6rPf5hYmc(SparseIntArray sparseIntArray, int i, int i2, ViewPager viewPager, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, int[] iArr, ReactedUsersListView reactedUsersListView, int i3) {
            int i4 = i2 + i3;
            sparseIntArray.put(i, i4);
            if (viewPager.getCurrentItem() == i) {
                actionBarPopupWindowLayout.getSwipeBack().setNewForegroundHeight(iArr[0], i4, true);
            }
        }

        @Override // androidx.viewpager.widget.PagerAdapter
        public void destroyItem(ViewGroup viewGroup, int i, Object obj) {
            viewGroup.removeView((View) obj);
        }
    }

    public void lambda$createMenu$286(boolean z, MessageObject messageObject, ReactedUsersListView reactedUsersListView, long j, TLRPC.MessagePeerReaction messagePeerReaction) {
        if (messagePeerReaction == null || messagePeerReaction.reaction == null || j == getUserConfig().getClientUserId() || !z) {
            return;
        }
        final ArrayList arrayList = new ArrayList(1);
        arrayList.add(messageObject);
        TLObject userOrChat = getMessagesController().getUserOrChat(j);
        final ArrayList arrayList2 = new ArrayList(1);
        arrayList2.add(userOrChat);
        final TLRPC.ChannelParticipant[] channelParticipantArr = new TLRPC.ChannelParticipant[1];
        TLRPC.TL_channels_getParticipant tL_channels_getParticipant = new TLRPC.TL_channels_getParticipant();
        tL_channels_getParticipant.channel = MessagesController.getInputChannel(this.currentChat);
        tL_channels_getParticipant.participant = MessagesController.getInputPeer(userOrChat);
        getConnectionsManager().sendRequestTyped(tL_channels_getParticipant, new AiTonesController$$ExternalSyntheticLambda0(), new Utilities.Callback2() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda519
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$createMenu$285(channelParticipantArr, arrayList, arrayList2, (TLRPC.TL_channels_channelParticipant) obj, (TLRPC.TL_error) obj2);
            }
        });
        closeMenu();
    }

    public void $r8$lambda$jrC_yOFEmB44L0en_W_kNbXN1QM(long[] jArr, boolean[] zArr, ImageView imageView, ImageView imageView2) {
        jArr[0] = SystemClock.elapsedRealtime();
        if (!zArr[0]) {
            imageView = imageView2;
        }
        final CrossfadeDrawable crossfadeDrawable = (CrossfadeDrawable) imageView.getDrawable();
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda538
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                crossfadeDrawable.setProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
            }
        });
        valueAnimatorOfFloat.setDuration(150L);
        valueAnimatorOfFloat.setInterpolator(CubicBezierInterpolator.DEFAULT);
        valueAnimatorOfFloat.start();
    }

    public void lambda$createMenu$297() {
        closeMenu();
        BulletinFactory.of(this).createSimpleBulletin(R.raw.chats_infotip, LocaleController.getString(R.string.TranscriptionReportSent)).show();
    }

    public static void lambda$createMenu$309(View view) {
        processSelectedOption(8);
    }

    public void lambda$createMenu$324() {
        dimBehindView(false);
    }

    public void $r8$lambda$cos6dWb2lWogQ_LJpBOxDLv_c2U(AtomicBoolean atomicBoolean, AtomicReference atomicReference, Exception exc) {
        FileLog.e("mlkit: failed to detect language in message");
        atomicBoolean.set(false);
        if (atomicReference.get() != null) {
            ((Runnable) atomicReference.get()).run();
            atomicReference.set(null);
        }
    }

    public void lambda$createMenu$328() {
        dimBehindView(false);
    }

    public static void lambda$updateGreetingLock$343(long j, View view) {
        if (StarsController.getInstance(this.currentAccount).getBalance().amount < j) {
            new StarsIntroActivity.StarsNeededSheet(getContext(), getResourceProvider(), j, 13, DialogObject.getShortName(getDialogId()), new ChatActivity$$ExternalSyntheticLambda288(this), getDialogId()).show();
        } else {
            new StarsIntroActivity.StarsOptionsSheet(getContext(), this.resourceProvider).show();
        }
    }

    public static boolean val$added;
        final void lambda$startEditingMessageObject$350(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda395
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$startEditingMessageObject$349(tLObject);
            }
        });
    }

    public void lambda$unpinMessage$351(ArrayList arrayList, ArrayList arrayList2, int i) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didLoadPinnedMessages, Long.valueOf(this.dialog_id), arrayList, Boolean.TRUE, arrayList2, null, 0, Integer.valueOf(i), Boolean.valueOf(this.pinnedEndReached));
        this.pinBulletin = null;
    }

    public :
                                                            TLRPC.Peer peer2 = r1.selectedObject.messageOwner.from_id;
                                                            if ((r1.threadMessageId == 0 || r1.isTopic) && !UserObject.isReplyUser(r1.currentUser)) {
                                                                r1.openSearchWithText();
                                                            } else {
                                                                r1.searchItem.openSearch(false);
                                                            }
                                                            if (peer2.user_id != 0) {
                                                                r1.searchUserMessages(r1.getMessagesController().getUser(Long.valueOf(peer2.user_id)), null);
                                                            } else if (peer2.chat_id != 0) {
                                                                r1.searchUserMessages(null, r1.getMessagesController().getChat(Long.valueOf(peer2.chat_id)));
                                                            } else if (peer2.channel_id != 0) {
                                                                r1.searchUserMessages(null, r1.getMessagesController().getChat(Long.valueOf(peer2.channel_id)));
                                                            }
                                                            r1.showMessagesSearchListView(true);
                                                            break;
                                                        case Opcodes.DIV_DOUBLE_2ADDR :
                                                            new BackupBottomSheet((BaseFragment) r1, r1.selectedObject).showIfPossible();
                                                            break;
                                                    }
                                                    break;
                                            }
                                            break;
                                    }
                                    break;
                            }
                            break;
                    }
                    break;
            }
            return;
        }
        r1.closeMenu(true);
        final Runnable runnable = new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda348
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$processSelectedOption$393();
            }
        };
        MessageObject messageObject19 = r1.selectedObject;
        if (messageObject19 != null && (message = messageObject19.messageOwner) != null && message.video_processing_pending) {
            r1.scheduleNowDialog = new AlertDialog.Builder(r1.getContext(), r1.getResourceProvider()).setTitle(LocaleController.getString(R.string.VideoConversionNowTitle)).setMessage(LocaleController.getString(R.string.VideoConversionNowText)).setPositiveButton(LocaleController.getString(R.string.VideoConversionNowSend), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda349
                @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                public final void onClick(AlertDialog alertDialog, int i8) {
                    runnable.run();
                }
            }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).show();
            return;
        }
        runnable.run();
        r1.selectedObject = null;
        r1.selectedObjectGroup = null;
        r1.selectedObjectToEditCaption = null;
        r1.closeMenu(!z4);
    }

    public void lambda$processSelectedOption$357(GenerateFromMessageBottomSheet.GenerationData generationData) {
        MessageObject messageObject;
        TLRPC.Message message;
        AiResponseAlert.showAlert(this, getClient(), generationData.prompt(), generationData.imagePath(), generationData.useHistory(), getMessagesController().isPeerNoForwards(getDialogId()) || !((messageObject = this.selectedObject) == null || (message = messageObject.messageOwner) == null || !message.noforwards), new Utilities.CallbackReturn() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda475
            @Override 
            public final Object run(Object obj) {
                return this.f$0.lambda$processSelectedOption$354((URLSpan) obj);
            }
        }, new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda476
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$processSelectedOption$355();
            }
        }, canSendMessage() ? new Utilities.Callback2() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda477
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$processSelectedOption$356((String) obj, (CharSequence) obj2);
            }
        } : null).setDimBehind(true);
    }

    public void lambda$processSelectedOption$383(DialogInterface dialogInterface) {
        dimBehindView(false);
    }

    public void lambda$processSelectedOption$386(final AlertDialog[] alertDialogArr, final TLRPC.TL_messages_editMessage tL_messages_editMessage, TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda495
            @Override // java.lang.Runnable
            public final void run() {
                ChatActivity.$r8$lambda$M3fhyuc6pkaBEOhxF2vxA93BXpc(alertDialogArr);
            }
        });
        if (tL_error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda496
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$processSelectedOption$385(tL_error, tL_messages_editMessage);
                }
            });
        }
    }

    public static void lambda$processSelectedOption$392(final TLRPC.TL_messages_sendScheduledMessages tL_messages_sendScheduledMessages, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda492
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$processSelectedOption$390(tL_messages_sendScheduledMessages);
                }
            });
        } else if (tL_error.text != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda493
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$processSelectedOption$391(tL_error);
                }
            });
        }
    }

    public void lambda$processSelectedOption$391(TLRPC.TL_error tL_error) {
        if (tL_error.text.startsWith("SLOWMODE_WAIT_")) {
            AlertsCreator.showSimpleToast(this, LocaleController.getString(R.string.SlowmodeSendError));
        } else if (tL_error.text.equals("CHAT_SEND_MEDIA_FORBIDDEN")) {
            AlertsCreator.showSimpleToast(this, LocaleController.getString(R.string.AttachMediaRestrictedForever));
        } else {
            AlertsCreator.showSimpleToast(this, tL_error.text);
        }
    }

    public void lambda$processSelectedOption$396(DialogInterface dialogInterface) {
        dimBehindView(false);
    }

    public void lambda$processSelectedOption$399(final TLRPC.SuggestedPost suggestedPost, final MessageObject messageObject) {
        AlertsCreator.createSuggestedMessageDatePickerDialog(getContext(), suggestedPost != null ? suggestedPost.schedule_date : 0L, new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda407
            @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
            public final void didSelectDate(boolean z, int i, int i2) {
                this.f$0.lambda$processSelectedOption$398(suggestedPost, messageObject, z, i, i2);
            }
        }, getResourceProvider(), 0).show();
    }

    public void $r8$lambda$ggHUi1bNt8Yj9965re29rjVgTr8(final ChatActivity chatActivity, final CharSequence charSequence, final MessageObject.GroupedMessages groupedMessages, final MessageObject messageObject) {
        ChatActivityEnterView chatActivityEnterView = chatActivity.chatActivityEnterView;
        if (chatActivityEnterView == null) {
            return;
        }
        chatActivityEnterView.post(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda525
            @Override // java.lang.Runnable
            public final void run() {
                ChatActivity.$r8$lambda$Dpszr1sWE5sMhaFZ5YZj4WExZkY(this.f$0, charSequence, groupedMessages, messageObject);
            }
        });
    }

    public static void lambda$showSuggestionOfferForEditMessage$403(MessageSuggestionParams messageSuggestionParams) {
        this.messageSuggestionParams = messageSuggestionParams;
        this.editingMessageObject.messageOwner.suggested_post = messageSuggestionParams.toTl();
        showFieldPanelForEdit(true, this.editingMessageObject);
    }

    public void checkStarsNeedSheet(final Runnable runnable, final AmountUtils$Amount amountUtils$Amount, boolean z) {
        if (amountUtils$Amount == null || !z) {
            runnable.run();
            return;
        }
        Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda382
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$checkStarsNeedSheet$404(amountUtils$Amount, runnable);
            }
        };
        StarsController starsController = StarsController.getInstance(this.currentAccount, amountUtils$Amount.currency);
        if (!starsController.balanceAvailable()) {
            starsController.getBalance(true, runnable2, true);
        } else {
            runnable2.run();
        }
    }

    public void lambda$didSelectDialogs$405(DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, int i, int i2, ArrayList arrayList2, boolean z2, HashMap map) {
        if (dialogsActivity.resetDelegate) {
            dialogsActivity.setDelegate(null);
        }
        if (this.forwardingMessage != null) {
            this.forwardingMessage = null;
            this.forwardingMessageGroup = null;
        } else {
            for (int i3 = 1; i3 >= 0; i3--) {
                this.selectedMessagesCanCopyIds[i3].clear();
                this.selectedMessagesCanStarIds[i3].clear();
                this.selectedMessagesIds[i3].clear();
            }
            hideActionMode();
            updatePinnedMessageView(true);
            updateVisibleRows();
        }
        this.messagePreviewParams = null;
        hideFieldPanel(false);
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            long j = ((MessagesStorage.TopicKey) arrayList.get(i4)).dialogId;
            Long l = map == null ? 0L : (Long) map.get(Long.valueOf(j));
            if (charSequence != null) {
                SendMessagesHelper.SendMessageParams sendMessageParamsOf = SendMessagesHelper.SendMessageParams.of(charSequence.toString(), j, null, null, null, true, null, null, null, z, i, i2, null, false);
                sendMessageParamsOf.quick_reply_shortcut = this.quickReplyShortcut;
                sendMessageParamsOf.quick_reply_shortcut_id = getQuickReplyId();
                sendMessageParamsOf.payStars = l == null ? 0L : l.longValue();
                sendMessageParamsOf.monoForumPeer = getSendMonoForumPeerId();
                sendMessageParamsOf.suggestionParams = this.messageSuggestionParams;
                getSendMessagesHelper().sendMessage(sendMessageParamsOf);
            }
            getSendMessagesHelper().sendMessage(arrayList2, j, z2, false, z, i, i2, null, -1, l != null ? l.longValue() : 0L, getSendMonoForumPeerId(), getSendMessageSuggestionParams());
        }
        dialogsActivity.finishFragment();
        createUndoView();
        if (this.undoView != null) {
            if (arrayList.size() == 1) {
                if (BulletinFactory.of(this).showForwardedBulletinWithTag(((MessagesStorage.TopicKey) arrayList.get(0)).dialogId, arrayList2.size())) {
                    return;
                }
                this.undoView.showWithAction(((MessagesStorage.TopicKey) arrayList.get(0)).dialogId, 53, Integer.valueOf(arrayList2.size()));
                return;
            }
            this.undoView.showWithAction(0L, 53, Integer.valueOf(arrayList2.size()), Integer.valueOf(arrayList.size()), (Runnable) null, (Runnable) null);
        }
    }

    public void lambda$showQuickRepliesRemoveAlert$408(AlertDialog alertDialog, int i) {
        finishFragment();
    }

    public void showBusinessLinksDiscardAlert(final Runnable runnable) {
        AlertDialog alertDialogCreate = new AlertDialog.Builder(getContext(), getResourceProvider()).setTitle(LocaleController.getString(R.string.BusinessLinkDiscardChangesTitle)).setMessage(LocaleController.getString(R.string.BusinessLinkDiscardChangesMessage)).setPositiveButton(LocaleController.getString(R.string.Discard), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda183
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                runnable.run();
            }
        }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
        showDialog(alertDialogCreate);
        TextView textView = (TextView) alertDialogCreate.getButton(-1);
        if (textView != null) {
            textView.setTextColor(getThemedColor(Theme.key_text_RedBold));
        }
    }

    public void clearSelectionMode() {
        clearSelectionMode(false);
    }

    public void clearSelectionMode(boolean z) {
        for (int i = 1; i >= 0; i--) {
            this.selectedMessagesIds[i].clear();
            this.selectedMessagesCanCopyIds[i].clear();
            this.selectedMessagesCanStarIds[i].clear();
        }
        hideActionMode();
        updatePinnedMessageView(true);
        updateVisibleRows(z);
        updateSelectedMessageReactions();
    }

    public void onListItemAnimatorTick() {
        invalidateMessagesVisiblePart();
        if (this.scrimView != null) {
            this.fragmentView.invalidate();
        }
    }

    public void setSavedDialog(long j) {
        this.threadMessageId = j;
    }

    public void setQuickReplyId(long j) {
        this.threadMessageId = j;
        TLRPC.TL_message tL_message = new TLRPC.TL_message();
        tL_message.id = (int) j;
        this.quickReplyMessage = new MessageObject(this.currentAccount, tL_message, false, false);
    }

    public void setMonoForumThreadMessages(int i, int i2, TLRPC.TL_forumTopic tL_forumTopic) {
        this.threadMaxInboxReadId = i;
        this.threadMaxOutboxReadId = i2;
        this.replyMaxReadId = Math.max(1, i);
        this.threadMessageId = tL_forumTopic != null ? DialogObject.getPeerDialogId(tL_forumTopic.from_id) : 0L;
        updatePinnedTopicStarterMessage();
        updateTopPanel(false);
        updateBottomOverlay();
    }

    public void setThreadMessages(ArrayList<MessageObject> arrayList, TLRPC.Chat chat, int i, int i2, int i3, TLRPC.TL_forumTopic tL_forumTopic) {
        this.forumTopic = tL_forumTopic;
        this.threadMessageObjects = arrayList;
        MessageObject messageObject = arrayList.get(arrayList.size() - 1);
        this.threadMessageObject = messageObject;
        this.replyingMessageObject = messageObject;
        this.threadMaxInboxReadId = i2;
        this.threadMaxOutboxReadId = i3;
        this.replyMaxReadId = Math.max(1, i2);
        this.threadMessageId = this.threadMessageObject.getId();
        this.replyOriginalMessageId = i;
        this.replyOriginalChat = chat;
        boolean z = tL_forumTopic != null;
        this.isTopic = z;
        MessageObject messageObject2 = this.replyingMessageObject;
        TLRPC.MessageFwdHeader messageFwdHeader = messageObject2.messageOwner.fwd_from;
        this.isComments = (messageFwdHeader == null || messageFwdHeader.channel_post == 0 || z) ? false : true;
        if (z) {
            messageObject2.isTopicMainMessage = true;
        }
        updatePinnedTopicStarterMessage();
        updateTopPanel(false);
        updateBottomOverlay();
    }

    public void updatePinnedTopicStarterMessage() {
        TLRPC.Message message;
        TLRPC.TL_forumTopic tL_forumTopic;
        MessageObject messageObject = (this.isTopic && !this.pinnedMessageObjects.isEmpty() && this.pinnedMessageIds.size() == 1 && ((long) this.pinnedMessageIds.get(0).intValue()) == getTopicId() + 1) ? this.pinnedMessageObjects.get(this.pinnedMessageIds.get(0)) : null;
        this.topicStarterMessageObject = messageObject;
        if (!this.isTopic || messageObject == null || (message = messageObject.messageOwner) == null || (tL_forumTopic = this.forumTopic) == null || MessageObject.peersEqual(tL_forumTopic.from_id, message.from_id) || MessageObject.peersEqual(this.currentChat, this.topicStarterMessageObject.messageOwner.from_id)) {
            return;
        }
        this.topicStarterMessageObject = null;
    }

    public void setHighlightMessageId(int i) {
        this.highlightMessageId = i;
    }

    public void setHighlightQuote(int i, String str, int i2) {
        this.highlightMessageId = i;
        this.highlightMessageQuoteFirst = true;
        this.highlightMessageQuoteFirstTime = 0L;
        this.highlightMessageQuote = str;
        this.highlightMessageQuoteOffset = i2;
        this.showNoQuoteAlert = true;
    }

    public void showNoQuoteFound() {
        BulletinFactory.of(this).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.QuoteNotFound)).show(true);
    }

    public boolean isThreadChat() {
        return this.threadMessageObject != null;
    }

    public boolean isReplyChatComment() {
        return this.threadMessageObject != null && this.isComments;
    }

    public void updateVisibleRows() {
        updateVisibleRows(false);
    }

    private void updateVisibleRows(boolean z) {
        int scrollingOffsetForView;
        int iIndexOf;
        boolean z2;
        boolean z3;
        boolean z4;
        String str;
        String str2;
        byte[] bArr;
        Integer num;
        ChatListRecyclerView chatListRecyclerView = this.chatListView;
        if (chatListRecyclerView == null) {
            return;
        }
        boolean z5 = false;
        if (!this.wasManualScroll && this.unreadMessageObject != null) {
            int childCount = chatListRecyclerView.getChildCount();
            int i = 0;
            while (true) {
                if (i < childCount) {
                    View childAt = this.chatListView.getChildAt(i);
                    if (childAt instanceof ChatMessageCell) {
                        MessageObject messageObject = ((ChatMessageCell) childAt).getMessageObject();
                        MessageObject messageObject2 = this.unreadMessageObject;
                        if (messageObject == messageObject2) {
                            if (this.messages.indexOf(messageObject2) >= 0) {
                                iIndexOf = this.chatAdapter.messagesStartRow + this.messages.indexOf(this.unreadMessageObject);
                                scrollingOffsetForView = getScrollingOffsetForView(childAt);
                                break;
                            }
                        }
                    }
                    i++;
                }
                scrollingOffsetForView = 0;
                iIndexOf = -1;
                break;
            }
        } else {
            scrollingOffsetForView = 0;
            iIndexOf = -1;
            break;
        }
        int childCount2 = this.chatListView.getChildCount();
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.getEditingMessageObject();
        }
        TLRPC.ChatFull chatFull = this.chatInfo;
        long j = 0;
        long j2 = chatFull != null ? chatFull.linked_chat_id : 0L;
        int i2 = 0;
        while (i2 < childCount2) {
            View childAt2 = this.chatListView.getChildAt(i2);
            if (childAt2 instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) childAt2;
                MessageObject messageObject3 = chatMessageCell.getMessageObject();
                if (this.actionBar.isActionModeShowed() || isReport()) {
                    this.highlightMessageQuoteFirst = z5;
                    this.highlightMessageQuoteFirstTime = j;
                    this.highlightMessageQuote = null;
                    this.highlightTaskId = null;
                    this.highlightPollOptionId = null;
                    ArrayList<MessageObject> arrayList = this.threadMessageObjects;
                    chatMessageCell.setCheckBoxVisible((arrayList == null || messageObject3 == null || !arrayList.contains(messageObject3)) ? true : z5, true);
                    int i3 = (messageObject3 == null || messageObject3.getDialogId() == this.dialog_id) ? z5 : 1;
                    if (messageObject3 != null && this.selectedMessagesIds[i3].indexOfKey(messageObject3.getId()) >= 0) {
                        setCellSelectionBackground(messageObject3, chatMessageCell, i3, true);
                        z3 = true;
                    } else {
                        chatMessageCell.setDrawSelectionBackground(z5);
                        chatMessageCell.setChecked(z5, z5, true);
                        z3 = z5;
                    }
                    z4 = true;
                } else {
                    chatMessageCell.setDrawSelectionBackground(z5);
                    chatMessageCell.setCheckBoxVisible(z5, true);
                    chatMessageCell.setChecked(z5, z5, true);
                    z3 = z5;
                    z4 = z3;
                }
                if (messageObject3 != null && ((!messageObject3.deleted || chatMessageCell.linkedChatId != j2) && !z)) {
                    chatMessageCell.setIsUpdating(true);
                    if (isFeedSearch()) {
                        TLRPC.MessageReplies messageReplies = messageObject3.messageOwner.replies;
                        chatMessageCell.linkedChatId = messageReplies != null ? messageReplies.channel_id : 0L;
                    } else {
                        TLRPC.ChatFull chatFull2 = this.chatInfo;
                        chatMessageCell.linkedChatId = chatFull2 != null ? chatFull2.linked_chat_id : 0L;
                    }
                    chatMessageCell.setMessageObject(messageObject3, chatMessageCell.getCurrentMessagesGroup(), chatMessageCell.isPinnedBottom(), chatMessageCell.isPinnedTop(), chatMessageCell.isFirstInChat(), chatMessageCell.isLastInChatList());
                    chatMessageCell.setIsUpdating(false);
                }
                if (chatMessageCell != this.scrimView) {
                    chatMessageCell.setCheckPressed(!z4, z4 && z3);
                }
                chatMessageCell.setHighlighted((this.highlightMessageId == Integer.MAX_VALUE || messageObject3 == null || messageObject3.getId() != this.highlightMessageId) ? false : true);
                if (this.highlightMessageId != Integer.MAX_VALUE) {
                    startMessageUnselect();
                }
                if (chatMessageCell.isHighlighted() && this.highlightMessageQuote != null) {
                    long jCurrentTimeMillis = System.currentTimeMillis();
                    if (!chatMessageCell.setHighlightedText(this.highlightMessageQuote, true, this.highlightMessageQuoteOffset, this.highlightMessageQuoteFirst || jCurrentTimeMillis - this.highlightMessageQuoteFirstTime < 200) && this.showNoQuoteAlert) {
                        showNoQuoteFound();
                    }
                    if (this.highlightMessageQuoteFirst) {
                        this.highlightMessageQuoteFirstTime = jCurrentTimeMillis;
                    }
                    z2 = false;
                    this.highlightMessageQuoteFirst = false;
                    this.showNoQuoteAlert = false;
                } else {
                    z2 = false;
                    if (chatMessageCell.isHighlighted() && (num = this.highlightTaskId) != null) {
                        chatMessageCell.setHighlightedTask(num.intValue());
                    } else if (chatMessageCell.isHighlighted() && (bArr = this.highlightPollOptionId) != null) {
                        chatMessageCell.setHighlightedPoll(bArr);
                    } else if (this.chatMode == 7 && this.searchingHashtag != null && (str2 = this.searchingQuery) != null) {
                        chatMessageCell.setHighlightedText(str2);
                    } else {
                        if (this.searchItem == null || !this.searchItemVisible || messageObject3 == null) {
                            str = null;
                        } else {
                            if (!getMediaDataController().isMessageFound(messageObject3.getId(), messageObject3.getDialogId() == this.mergeDialogId) || getMediaDataController().getLastSearchQuery() == null) {
                                str = null;
                            } else {
                                chatMessageCell.setHighlightedText(getMediaDataController().getLastSearchQuery());
                            }
                        }
                        chatMessageCell.setHighlightedText(str);
                    }
                }
                chatMessageCell.setSpoilersSuppressed(this.chatListView.getScrollState() != 0 ? true : z2);
            } else {
                z2 = z5;
                if (childAt2 instanceof ChatActionCell) {
                    ChatActionCell chatActionCell = (ChatActionCell) childAt2;
                    if (!z) {
                        chatActionCell.setMessageObject(chatActionCell.getMessageObject());
                    }
                    chatActionCell.setSpoilersSuppressed(this.chatListView.getScrollState() != 0 ? true : z2);
                }
            }
            i2++;
            z5 = z2;
            j = 0;
        }
        if (iIndexOf != -1) {
            this.chatLayoutManager.scrollToPositionWithOffset(iIndexOf, scrollingOffsetForView);
        }
    }

    private void updateVisibleRows(Utilities.CallbackReturn<MessageObject, Boolean> callbackReturn) {
        int iIndexOf;
        int scrollingOffsetForView;
        ChatListRecyclerView chatListRecyclerView = this.chatListView;
        if (chatListRecyclerView == null) {
            return;
        }
        if (!this.wasManualScroll && this.unreadMessageObject != null) {
            int childCount = chatListRecyclerView.getChildCount();
            int i = 0;
            while (true) {
                if (i < childCount) {
                    View childAt = this.chatListView.getChildAt(i);
                    if (childAt instanceof ChatMessageCell) {
                        MessageObject messageObject = ((ChatMessageCell) childAt).getMessageObject();
                        MessageObject messageObject2 = this.unreadMessageObject;
                        if (messageObject == messageObject2) {
                            if (this.messages.indexOf(messageObject2) >= 0) {
                                iIndexOf = this.chatAdapter.messagesStartRow + this.messages.indexOf(this.unreadMessageObject);
                                scrollingOffsetForView = getScrollingOffsetForView(childAt);
                                break;
                            }
                        }
                    }
                    i++;
                }
                iIndexOf = -1;
                scrollingOffsetForView = 0;
                break;
            }
        } else {
            iIndexOf = -1;
            scrollingOffsetForView = 0;
            break;
        }
        int childCount2 = this.chatListView.getChildCount();
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.getEditingMessageObject();
        }
        TLRPC.ChatFull chatFull = this.chatInfo;
        if (chatFull != null) {
            long j = chatFull.linked_chat_id;
        }
        for (int i2 = 0; i2 < childCount2; i2++) {
            View childAt2 = this.chatListView.getChildAt(i2);
            if (childAt2 instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) childAt2;
                MessageObject messageObject3 = chatMessageCell.getMessageObject();
                if (callbackReturn.run(messageObject3).booleanValue()) {
                    messageObject3.forceUpdate = true;
                    chatMessageCell.setMessageObject(messageObject3, chatMessageCell.getCurrentMessagesGroup(), chatMessageCell.isPinnedBottom(), chatMessageCell.isPinnedTop(), chatMessageCell.isFirstInChat(), chatMessageCell.isLastInChatList());
                    this.chatAdapter.updateRowAtPosition(this.chatListView.getChildAdapterPosition(chatMessageCell));
                }
            }
        }
        if (iIndexOf != -1) {
            this.chatLayoutManager.scrollToPositionWithOffset(iIndexOf, scrollingOffsetForView);
        }
    }

    public void checkEditTimer() {
        MessageObject editingMessageObject;
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView == null || (editingMessageObject = chatActivityEnterView.getEditingMessageObject()) == null || editingMessageObject.scheduled) {
            return;
        }
        TLRPC.User user = this.currentUser;
        if (user == null || !user.self) {
            SimpleTextView simpleTextView = this.replyLayout.current().obj;
            int iAbs = editingMessageObject.canEditMessageAnytime(this.currentChat) ? 360 : (getMessagesController().maxEditTime + 300) - Math.abs(getConnectionsManager().getCurrentTime() - editingMessageObject.messageOwner.date);
            if (iAbs > 0) {
                if (iAbs <= 300) {
                    simpleTextView.setText(LocaleController.formatString("TimeToEdit", R.string.TimeToEdit, AndroidUtilities.formatShortDuration(iAbs)));
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda385
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.checkEditTimer();
                    }
                }, 1000L);
            } else {
                this.chatActivityEnterView.onEditTimeExpired();
                simpleTextView.setText(LocaleController.formatString("TimeToEditExpired", R.string.TimeToEditExpired, new Object[0]));
            }
        }
    }

    public ArrayList<MessageObject> createVoiceMessagesPlaylist(MessageObject messageObject, boolean z) {
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        arrayList.add(messageObject);
        int id = messageObject.getId();
        messageObject.getDialogId();
        if (id != 0) {
            for (int size = this.messages.size() - 1; size >= 0; size--) {
                MessageObject messageObject2 = this.messages.get(size);
                if ((messageObject2.getDialogId() != this.mergeDialogId || messageObject.getDialogId() == this.mergeDialogId) && (((this.currentEncryptedChat == null && messageObject2.getId() > id) || (this.currentEncryptedChat != null && messageObject2.getId() < id)) && ((messageObject2.isVoice() || messageObject2.isRoundVideo()) && !messageObject2.isVoiceOnce() && !messageObject2.isRoundOnce() && (!z || (messageObject2.isContentUnread() && !messageObject2.isOut()))))) {
                    arrayList.add(messageObject2);
                }
            }
        }
        return arrayList;
    }

    public void alertUserOpenError(MessageObject messageObject) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), this.themeDelegate);
        builder.setTitle(LocaleController.getString(R.string.AppName));
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        if (messageObject.type == 3) {
            builder.setMessage(LocaleController.getString(R.string.NoPlayerInstalled));
        } else {
            builder.setMessage(LocaleController.formatString("NoHandleAppInstalled", R.string.NoHandleAppInstalled, messageObject.getDocument().mime_type));
        }
        showDialog(builder.create());
    }

    public void openSearchWithText() {
        lambda$openSearchWithText$413(_UrlKt.FRAGMENT_ENCODE_SET);
    }

    public void parseMarkdownAsync(final MessageObject messageObject) {
        if (getParentActivity() == null) {
            return;
        }
        final AlertDialog alertDialog = new AlertDialog(getParentActivity(), 3, this.themeDelegate);
        alertDialog.setCanceledOnTouchOutside(false);
        final boolean[] zArr = {false};
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda484
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                ChatActivity.$r8$lambda$sjLf2Zm27PA4fbHcZXOtHGl9RGs(zArr, dialogInterface);
            }
        });
        alertDialog.showDelayed(150L);
        new Thread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda485
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$parseMarkdownAsync$412(messageObject, alertDialog, zArr);
            }
        }).start();
    }

    public static void lambda$sendMedia$418() {
        this.closeInstantCameraAnimation = null;
        runCloseInstantCameraAnimation();
    }

    public void lambda$showOpenGameAlert$420(TLRPC.TL_game tL_game, MessageObject messageObject, String str, long j, AlertDialog alertDialog, int i) {
        showOpenGameAlert(tL_game, messageObject, str, false, j);
        MessagesController.getNotificationsSettings(this.currentAccount).edit().putBoolean("askgame_" + j, false).commit();
    }

    void lambda$processLoadedDiscussionMessage$424(final ArrayList arrayList, TLRPC.TL_messages_discussionMessage tL_messages_discussionMessage, final TLRPC.messages_Messages messages_messages, TLRPC.Chat chat, TLRPC.TL_messages_getDiscussionMessage tL_messages_getDiscussionMessage, final int i, MessageObject messageObject, final int i2, int i3, MessageObject messageObject2) {
        int i4;
        TLRPC.TL_messageReactions tL_messageReactions;
        TLRPC.MessageReplies messageReplies;
        TLRPC.MessageReplies messageReplies2;
        int i5 = 0;
        if (!arrayList.isEmpty() && tL_messages_discussionMessage != null) {
            this.hideCommentLoading = true;
            this.chatListView.invalidateViews();
            Bundle bundle = new Bundle();
            final long dialogId = ((MessageObject) arrayList.get(0)).getDialogId();
            bundle.putLong("chat_id", -dialogId);
            bundle.putInt("message_id", Math.max(1, tL_messages_discussionMessage.read_inbox_max_id));
            bundle.putInt("unread_count", tL_messages_discussionMessage.unread_count);
            bundle.putBoolean("historyPreloaded", messages_messages != null);
            final ChatActivity chatActivity = new ChatActivity(bundle);
            chatActivity.setThreadMessages(arrayList, chat, tL_messages_getDiscussionMessage.msg_id, tL_messages_discussionMessage.read_inbox_max_id, tL_messages_discussionMessage.read_outbox_max_id, null);
            if (i != 0) {
                chatActivity.highlightMessageId = i;
            }
            if (messageObject != null && (messageReplies = messageObject.messageOwner.replies) != null && (messageReplies2 = chatActivity.threadMessageObject.messageOwner.replies) != null) {
                messageReplies.replies = messageReplies2.replies;
            }
            if (messageObject != null && (tL_messageReactions = messageObject.messageOwner.reactions) != null) {
                chatActivity.threadMessageObject.messageOwner.reactions = tL_messageReactions;
            }
            final boolean[] zArr = {false};
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda544
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$processLoadedDiscussionMessage$422(zArr, i2, chatActivity);
                }
            };
            if (messages_messages != null) {
                if (messages_messages.messages.isEmpty()) {
                    i4 = i3;
                } else {
                    for (int size = messages_messages.messages.size() - 1; size >= 0; size--) {
                        TLRPC.Message message = messages_messages.messages.get(size);
                        int i6 = message.id;
                        i4 = i3;
                        if (i6 > i4 && !message.out) {
                            i5 = i6;
                        }
                    }
                    i4 = i3;
                }
                final int classGuid = chatActivity.getClassGuid();
                NotificationCenter.getInstance(this.currentAccount).addObserver(new AnonymousClass131(classGuid, runnable, chatActivity), NotificationCenter.messagesDidLoad);
                final int i7 = i5;
                final int i8 = i4;
                Utilities.stageQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda545
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$processLoadedDiscussionMessage$423(messages_messages, dialogId, i, i8, classGuid, i7, arrayList);
                    }
                });
                return;
            }
            runnable.run();
            return;
        }
        this.commentLoadingMessageId = 0;
        this.hideCommentLoading = false;
        this.chatListView.invalidateViews();
        if (messageObject2 != null && !isFeedSearch()) {
            openOriginalReplyChat(messageObject2);
        } else if (getParentActivity() != null) {
            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.ChannelPostDeleted), this.themeDelegate).show();
        }
    }

    public void lambda$processLoadedDiscussionMessage$423(TLRPC.messages_Messages messages_messages, long j, int i, int i2, int i3, int i4, ArrayList arrayList) {
        getMessagesController().processLoadedMessages(messages_messages, messages_messages.messages.size(), j, 0L, 30, i > 0 ? i : i2, 0, false, i3, i4, 0, 0, 0, i > 0 ? 3 : 2, true, 0, ((MessageObject) arrayList.get(arrayList.size() - 1)).getId(), 1, false, 0, true, this.isTopic, null);
    }

    public void openDiscussionMessageChat(long j, final MessageObject messageObject, int i, final long j2, final int i2, final int i3, final MessageObject messageObject2) {
        int id = i;
        final TLRPC.Chat chat = getMessagesController().getChat(Long.valueOf(j));
        final TLRPC.TL_messages_getDiscussionMessage tL_messages_getDiscussionMessage = new TLRPC.TL_messages_getDiscussionMessage();
        tL_messages_getDiscussionMessage.peer = MessagesController.getInputPeer(chat);
        tL_messages_getDiscussionMessage.msg_id = id;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("getDiscussionMessage chat = " + chat.id + " msg_id = " + id);
        }
        this.commentLoadingMessageId = 0;
        this.hideCommentLoading = false;
        this.savedDiscussionMessage = null;
        this.savedNoDiscussion = false;
        this.savedNoHistory = false;
        this.savedHistory = null;
        ChatListRecyclerView chatListRecyclerView = this.chatListView;
        if (chatListRecyclerView != null) {
            chatListRecyclerView.invalidateViews();
        }
        if (this.commentMessagesRequestId != -1) {
            getConnectionsManager().cancelRequest(this.commentMessagesRequestId, false);
        }
        if (this.commentRequestId != -1) {
            getConnectionsManager().cancelRequest(this.commentRequestId, false);
        }
        if (messageObject2 != null) {
            id = messageObject2.getId();
        }
        this.commentLoadingMessageId = id;
        this.hideCommentLoading = false;
        this.commentLoadingStartedAt = System.currentTimeMillis();
        ChatListRecyclerView chatListRecyclerView2 = this.chatListView;
        if (chatListRecyclerView2 != null) {
            chatListRecyclerView2.invalidateViews();
        }
        final int i4 = this.commentLoadingGuid + 1;
        this.commentLoadingGuid = i4;
        this.commentRequestId = getConnectionsManager().sendRequest(tL_messages_getDiscussionMessage, new RequestDelegate() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda362
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$openDiscussionMessageChat$430(i4, i2, j2, i3, messageObject2, tL_messages_getDiscussionMessage, chat, messageObject, tLObject, tL_error);
            }
        });
        getConnectionsManager().bindRequestToGuid(this.commentRequestId, this.classGuid);
    }

    public void lambda$openDiscussionMessageChat$426(final int i, final TLObject tLObject, final TLRPC.TL_error tL_error, final int i2, final MessageObject messageObject, final TLRPC.TL_messages_getDiscussionMessage tL_messages_getDiscussionMessage, final TLRPC.Chat chat, final int i3, final MessageObject messageObject2) {
        lambda$openDiscussionMessageChat$429(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda542
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openDiscussionMessageChat$425(i, tLObject, tL_error, i2, messageObject, tL_messages_getDiscussionMessage, chat, i3, messageObject2);
            }
        });
    }

    public public void openClickableLink(final CharacterStyle characterStyle, final String str, boolean z, final ChatMessageCell chatMessageCell, final MessageObject messageObject, boolean z2) {
        String strReplaceHostname;
        String strDecode;
        CharSequence[] charSequenceArr;
        TLRPC.Message message;
        if (z) {
            if (str.startsWith("@")) {
                if (chatMessageCell != null) {
                    chatMessageCell.resetPressedLink(-1);
                }
                didLongPressUsername(chatMessageCell, characterStyle, str.substring(1));
                return;
            }
            if (characterStyle == null) {
                BottomSheet.Builder builder = new BottomSheet.Builder(getParentActivity(), false, this.themeDelegate);
                final int iIntValue = str.startsWith("video?") ? Utilities.parseInt((CharSequence) str).intValue() : -1;
                if (iIntValue >= 0) {
                    builder.setTitle(AndroidUtilities.formatDuration(iIntValue, false));
                } else {
                    try {
                        Uri uri = Uri.parse(str);
                        strReplaceHostname = Browser.replaceHostname(uri, Browser.IDN_toUnicode(uri.getHost()), null);
                    } catch (Exception e) {
                        try {
                            FileLog.e(e);
                            strReplaceHostname = str;
                        } catch (Exception e2) {
                            e = e2;
                            strReplaceHostname = str;
                            FileLog.e(e);
                            strDecode = strReplaceHostname;
                            builder.setTitle(strDecode);
                            builder.setTitleMultipleLines(true);
                            if (isPeerNoForwards()) {
                                charSequenceArr = new CharSequence[]{LocaleController.getString(R.string.Open)};
                            } else {
                                charSequenceArr = new CharSequence[]{LocaleController.getString(R.string.Open)};
                            }
                            builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda216
                                @Override // android.content.DialogInterface.OnClickListener
                                public final void onClick(DialogInterface dialogInterface, int i) {
                                    this.f$0.lambda$openClickableLink$437(str, characterStyle, messageObject, chatMessageCell, iIntValue, dialogInterface, i);
                                }
                            });
                            builder.setOnPreDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda217
                                @Override // android.content.DialogInterface.OnDismissListener
                                public final void onDismiss(DialogInterface dialogInterface) {
                                    ChatActivity.$r8$lambda$ztA_QHmy8KyHJGz2Y4a7LSA3NiY(chatMessageCell, dialogInterface);
                                }
                            });
                            showDialog(builder.create());
                            return;
                        }
                    }
                    try {
                        strDecode = URLDecoder.decode(strReplaceHostname.replaceAll("\\+", "%2b"), "UTF-8");
                    } catch (Exception e3) {
                        e = e3;
                        FileLog.e(e);
                        strDecode = strReplaceHostname;
                    }
                    builder.setTitle(strDecode);
                    builder.setTitleMultipleLines(true);
                }
                if (isPeerNoForwards() || !(messageObject == null || (message = messageObject.messageOwner) == null || !message.noforwards)) {
                    charSequenceArr = new CharSequence[]{LocaleController.getString(R.string.Open)};
                } else {
                    charSequenceArr = new CharSequence[]{LocaleController.getString(R.string.Open), LocaleController.getString(R.string.Copy)};
                }
                builder.setItems(charSequenceArr, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda216
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        this.f$0.lambda$openClickableLink$437(str, characterStyle, messageObject, chatMessageCell, iIntValue, dialogInterface, i);
                    }
                });
                builder.setOnPreDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda217
                    @Override // android.content.DialogInterface.OnDismissListener
                    public final void onDismiss(DialogInterface dialogInterface) {
                        ChatActivity.$r8$lambda$ztA_QHmy8KyHJGz2Y4a7LSA3NiY(chatMessageCell, dialogInterface);
                    }
                });
                showDialog(builder.create());
                return;
            }
            if (chatMessageCell != null) {
                chatMessageCell.resetPressedLink(-1);
            }
            didLongPressLink(chatMessageCell, messageObject, characterStyle, str);
            return;
        }
        logSponsoredClicked(messageObject, false, false);
        String strExtractUsername = Browser.extractUsername(str);
        if (strExtractUsername != null) {
            String lowerCase = strExtractUsername.toLowerCase();
            if (ChatObject.hasPublicLink(this.currentChat, lowerCase) || UserObject.hasPublicUsername(this.currentUser, lowerCase)) {
                ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
                if (chatAvatarContainer != null) {
                    chatAvatarContainer.openProfile(false);
                    return;
                } else {
                    shakeContent();
                    return;
                }
            }
            if (str.startsWith("@")) {
                getMessagesController().openByUserName(lowerCase, this, 0, makeProgressForLink(chatMessageCell, characterStyle));
                return;
            } else {
                processExternalUrl(0, str, characterStyle, chatMessageCell, false, false);
                return;
            }
        }
        if (str.startsWith("#") || str.startsWith("$")) {
            if (isFeedSearch()) {
                return;
            }
            int i = this.chatMode;
            if (i == 1 || i == 2 || i == 7) {
                ChatActivityDelegate chatActivityDelegate = this.chatActivityDelegate;
                if (chatActivityDelegate != null) {
                    chatActivityDelegate.openHashtagSearch(str);
                } else {
                    presentFragment(new HashtagActivity(str, this.resourceProvider));
                }
                if (this.chatMode != 7) {
                    finishFragment();
                    return;
                }
                return;
            }
            if (str.contains("@")) {
                presentFragment(new HashtagActivity(str, this.resourceProvider));
                return;
            } else {
                lambda$openHashtagSearch$414(str);
                return;
            }
        }
        processExternalUrl(0, str, characterStyle, chatMessageCell, false, false);
    }

    void $r8$lambda$ztA_QHmy8KyHJGz2Y4a7LSA3NiY(ChatMessageCell chatMessageCell, DialogInterface dialogInterface) {
        if (chatMessageCell != null) {
            chatMessageCell.resetPressedLink(-1);
        }
    }

    public void shakeContent() {
        AndroidUtilities.shakeViewSpring(getChatListView(), 5.0f);
        BotWebViewVibrationEffect.APP_ERROR.vibrate();
        ChatActivityEnterView chatActivityEnterView = getChatActivityEnterView();
        for (int i = 0; i < chatActivityEnterView.getChildCount(); i++) {
            AndroidUtilities.shakeViewSpring(chatActivityEnterView.getChildAt(i), 5.0f);
        }
        ActionBar actionBar = getActionBar();
        for (int i2 = 0; i2 < actionBar.getChildCount(); i2++) {
            AndroidUtilities.shakeViewSpring(actionBar.getChildAt(i2), 5.0f);
        }
    }

    private Browser.Progress makeProgressForLink(ChatMessageCell chatMessageCell, CharacterStyle characterStyle) {
        Browser.Progress progress = this.progressDialogCurrent;
        if (progress != null) {
            progress.cancel(true);
            this.progressDialogCurrent = null;
        }
        if (characterStyle == null || chatMessageCell == null || chatMessageCell.getMessageObject() == null) {
            this.progressDialogCurrent = null;
            return null;
        }
        AnonymousClass132 anonymousClass132 = new AnonymousClass132(chatMessageCell.getMessageObject().getId(), characterStyle, chatMessageCell);
        this.progressDialogCurrent = anonymousClass132;
        return anonymousClass132;
    }

    public class AnonymousClass132 extends Browser.Progress {
        final void lambda$end$0(int i) {
            if (ChatActivity.this.progressDialogAtMessageId == i) {
                ChatActivity.this.resetProgressDialogLoading();
            }
        }
    }

    public Browser.Progress makeProgressForForward(ChatMessageCell chatMessageCell) {
        Browser.Progress progress = this.progressDialogCurrent;
        if (progress != null) {
            progress.cancel(true);
            this.progressDialogCurrent = null;
        }
        if (chatMessageCell == null || chatMessageCell.getMessageObject() == null) {
            this.progressDialogCurrent = null;
            return null;
        }
        AnonymousClass133 anonymousClass133 = new AnonymousClass133(chatMessageCell.getMessageObject().getId(), chatMessageCell);
        this.progressDialogCurrent = anonymousClass133;
        return anonymousClass133;
    }

    public class AnonymousClass133 extends Browser.Progress {
        final void lambda$end$0(int i) {
            if (ChatActivity.this.progressDialogAtMessageId == i) {
                ChatActivity.this.resetProgressDialogLoading();
            }
        }
    }

    public Browser.Progress makeProgressForBotButton(ChatMessageCell chatMessageCell, String str) {
        Browser.Progress progress = this.progressDialogCurrent;
        if (progress != null) {
            progress.cancel(true);
            this.progressDialogCurrent = null;
        }
        if (str == null || chatMessageCell == null || chatMessageCell.getMessageObject() == null) {
            this.progressDialogCurrent = null;
            return null;
        }
        AnonymousClass134 anonymousClass134 = new AnonymousClass134(chatMessageCell.getMessageObject().getId(), str, chatMessageCell);
        this.progressDialogCurrent = anonymousClass134;
        return anonymousClass134;
    }

    public class AnonymousClass134 extends Browser.Progress {
        final void lambda$end$0(int i) {
            if (ChatActivity.this.progressDialogAtMessageId == i) {
                ChatActivity.this.resetProgressDialogLoading();
            }
        }
    }

    public Browser.Progress makeProgressForPaidMedia(ChatMessageCell chatMessageCell) {
        Browser.Progress progress = this.progressDialogCurrent;
        if (progress != null) {
            progress.cancel(true);
            this.progressDialogCurrent = null;
        }
        if (chatMessageCell == null || chatMessageCell.getMessageObject() == null) {
            this.progressDialogCurrent = null;
            return null;
        }
        AnonymousClass135 anonymousClass135 = new AnonymousClass135(chatMessageCell.getMessageObject().getId(), chatMessageCell);
        this.progressDialogCurrent = anonymousClass135;
        return anonymousClass135;
    }

    public class AnonymousClass135 extends Browser.Progress {
        final void lambda$end$0(int i) {
            if (ChatActivity.this.progressDialogAtMessageId == i) {
                ChatActivity.this.resetProgressDialogLoading();
            }
        }
    }

    public void loadFullRichMessage(final ChatMessageCell chatMessageCell) {
        final MessageObject messageObject;
        if (chatMessageCell == null || (messageObject = chatMessageCell.getMessageObject()) == null || messageObject.messageOwner == null) {
            return;
        }
        int id = messageObject.getId();
        if (this.progressDialogAtMessageId == id && this.progressDialogAtMessageType == 7) {
            return;
        }
        TLRPC.InputPeer inputPeer = getMessagesController().getInputPeer(messageObject.getDialogId());
        if (inputPeer == null) {
            return;
        }
        TL_iv.getRichMessage getrichmessage = new TL_iv.getRichMessage();
        getrichmessage.peer = inputPeer;
        getrichmessage.id = id;
        Browser.Progress progress = this.progressDialogCurrent;
        if (progress != null) {
            progress.cancel(true);
            this.progressDialogCurrent = null;
        }
        final int[] iArr = new int[1];
        final AnonymousClass136 anonymousClass136 = new AnonymousClass136(id, chatMessageCell);
        anonymousClass136.onCancel(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda520
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadFullRichMessage$439(iArr);
            }
        });
        this.progressDialogCurrent = anonymousClass136;
        anonymousClass136.init();
        iArr[0] = getConnectionsManager().sendRequestTyped(getrichmessage, new AiTonesController$$ExternalSyntheticLambda0(), new Utilities.Callback2() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda521
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$loadFullRichMessage$440(anonymousClass136, iArr, chatMessageCell, messageObject, (TLRPC.messages_Messages) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public class AnonymousClass136 extends Browser.Progress {
        final void lambda$end$0(int i) {
            if (ChatActivity.this.progressDialogAtMessageId == i) {
                ChatActivity.this.resetProgressDialogLoading();
            }
        }
    }

    public void $r8$lambda$I7SdminFf1flZZIJ4_3Pd7YRK78() {
        }

        public static @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ChatActivity.ChatActivityAdapter.getItemCount():int");
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            ArrayList<MessageObject> arrayList;
            if (ChatActivity.this.isClearingHistory() && i == this.botInfoEmptyRow) {
                return 1L;
            }
            if (this.isFrozen) {
                arrayList = this.frozenMessages;
            } else if (this.isFiltered) {
                arrayList = this.filteredMessages;
            } else {
                arrayList = ChatActivity.this.messages;
            }
            int i2 = this.messagesStartRow;
            if (i >= i2 && i < this.messagesEndRow) {
                return arrayList.get(i - i2).stableId;
            }
            if (i == this.botInfoRow || i == this.botInfoEmptyRow) {
                return 1L;
            }
            if (i == this.loadingUpRow) {
                return 2L;
            }
            if (i == this.loadingDownRow) {
                return 3L;
            }
            if (i == this.hintRow) {
                return 4L;
            }
            if (i == this.userInfoRow) {
                return 6L;
            }
            if (i == this.userPhotoTimeRow) {
                return 7L;
            }
            if (i == this.userNameTimeRow) {
                return 8L;
            }
            return i == this.botForumStartThreadRow ? 9L : 5L;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View chatActionCell;
            ChatMessageCell chatMessageCell;
            boolean z = true;
            if (i == 0) {
                Context context = this.mContext;
                int i2 = ((BaseFragment) ChatActivity.this).currentAccount;
                ChatActivity chatActivity = ChatActivity.this;
                chatMessageCell = new ChatMessageCell(context, i2, true, chatActivity.sharedResources, chatActivity.themeDelegate);
                chatMessageCell.setResourcesProvider(ChatActivity.this.themeDelegate);
                chatMessageCell.shouldCheckVisibleOnScreen = false;
                chatMessageCell.setDelegate(ChatActivity.this.getChatMessageCellDelegate());
                chatMessageCell.draftAnimationsPool = ChatActivity.this.botDraftAnimationsPool;
                if (ChatActivity.this.currentEncryptedChat == null) {
                    chatActionCell = chatMessageCell;
                    chatMessageCell.setAllowAssistant(true);
                    chatActionCell = chatMessageCell;
                }
            } else if (i == 1) {
                ChatActionCell chatActionCell2 = new ChatActionCell(this.mContext, z, ChatActivity.this.themeDelegate) { // from class: org.telegram.ui.ChatActivity.ChatActivityAdapter.1
                    @Override // org.telegram.ui.Cells.ChatActionCell, android.view.View
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                        accessibilityNodeInfo.setVisibleToUser(true);
                    }
                };
                chatActionCell2.setInvalidateColors(true);
                chatActionCell2.setDelegate(new AnonymousClass2());
                chatActionCell = chatActionCell2;
            } else if (i == 2) {
                chatActionCell = new ChatUnreadCell(this.mContext, ChatActivity.this.themeDelegate);
            } else if (i == 3) {
                BotHelpCell botHelpCell = new BotHelpCell(this.mContext, ((BaseFragment) ChatActivity.this).currentAccount, ChatActivity.this.themeDelegate) { // from class: org.telegram.ui.ChatActivity.ChatActivityAdapter.3
                    @Override // org.telegram.ui.Cells.BotHelpCell
                    public int getSideMenuWidth() {
                        return ChatActivity.this.getSideMenuWidth();
                    }
                };
                botHelpCell.setDelegate(new BotHelpCell.BotHelpCellDelegate() { // from class: org.telegram.ui.ChatActivity$ChatActivityAdapter$$ExternalSyntheticLambda1
                    @Override // org.telegram.ui.Cells.BotHelpCell.BotHelpCellDelegate
                    public final void didPressUrl(String str) {
                        this.f$0.lambda$onCreateViewHolder$0(str);
                    }
                });
                chatActionCell = botHelpCell;
            } else if (i == 4) {
                Context context2 = this.mContext;
                ChatActivity chatActivity2 = ChatActivity.this;
                chatActionCell = new ChatLoadingCell(context2, chatActivity2.contentView, chatActivity2.themeDelegate);
            } else if (i == 6) {
                chatActionCell = new UserInfoCell(this.mContext, ((BaseFragment) ChatActivity.this).currentAccount, ChatActivity.this.themeDelegate);
            } else if (i == 7) {
                chatActionCell = new ChatActionCell(this.mContext, false, ChatActivity.this.themeDelegate);
            } else {
                chatActionCell = i == 8 ? new BotAskCell(this.mContext, ((BaseFragment) ChatActivity.this).currentAccount, ChatActivity.this.themeDelegate) { // from class: org.telegram.ui.ChatActivity.ChatActivityAdapter.4
                    @Override // org.telegram.ui.Cells.BotAskCell
                    public int getSideMenuWidth() {
                        return ChatActivity.this.getSideMenuWidth();
                    }
                } : null;
            }
            chatActionCell = chatMessageCell;
            chatActionCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(chatActionCell);
        }

        public class AnonymousClass2 implements ChatActionCell.ChatActionCellDelegate {
            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public boolean canDrawOutboundsContent() {
                return false;
            }

            public AnonymousClass2() {
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void didOpenPremiumGift(ChatActionCell chatActionCell, TLRPC.TL_premiumGiftOption tL_premiumGiftOption, String str, boolean z) {
                if (str != null) {
                    initGiftProgressDialog(chatActionCell);
                    PremiumPreviewGiftLinkBottomSheet.show(str, ChatActivity.this.progressDialogCurrent);
                } else {
                    ChatActivity chatActivity = ChatActivity.this;
                    ChatActivity chatActivity2 = ChatActivity.this;
                    chatActivity.showDialog(new PremiumPreviewBottomSheet(chatActivity2, ((BaseFragment) chatActivity2).currentAccount, ChatActivity.this.getCurrentUser(), new GiftPremiumBottomSheet$GiftTier(tL_premiumGiftOption, (Object) null), null, ChatActivity.this.themeDelegate).setAnimateConfetti(z).setOutboundGift(chatActionCell.getMessageObject().isOut()));
                }
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void didPressReaction(ChatActionCell chatActionCell, TLRPC.ReactionCount reactionCount, boolean z, float f, float f2) {
                ChatActivity.this.didPressReaction(chatActionCell, reactionCount, z, f, f2);
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void forceUpdate(ChatActionCell chatActionCell, boolean z) {
                MessageObject messageObject;
                int childAdapterPosition;
                int scrollingOffsetForView;
                if (chatActionCell == null || (messageObject = chatActionCell.getMessageObject()) == null) {
                    return;
                }
                messageObject.forceUpdate = true;
                if (ChatActivity.this.chatListView == null || ChatActivity.this.chatLayoutManager == null || ChatActivity.this.chatLayoutManager.hasPendingScrollPosition()) {
                    childAdapterPosition = -1;
                    scrollingOffsetForView = 0;
                    break;
                }
                int childCount = ChatActivity.this.chatListView.getChildCount() - 1;
                while (true) {
                    if (childCount < 0) {
                        childAdapterPosition = -1;
                        scrollingOffsetForView = 0;
                        break;
                    }
                    View childAt = ChatActivity.this.chatListView.getChildAt(childCount);
                    childAdapterPosition = ChatActivity.this.chatListView.getChildAdapterPosition(childAt);
                    if (childAdapterPosition >= 0) {
                        if (childAt instanceof ChatMessageCell) {
                            if (((ChatMessageCell) childAt).getCurrentMessagesGroup() == null) {
                                scrollingOffsetForView = ChatActivity.this.getScrollingOffsetForView(childAt);
                                break;
                            }
                        } else if (childAt instanceof ChatActionCell) {
                            scrollingOffsetForView = ChatActivity.this.getScrollingOffsetForView(childAt);
                            break;
                        }
                    }
                    childCount--;
                }
                ChatActivity.this.lambda$updateMessageAnimated$348(messageObject, false);
                if (!z || childAdapterPosition < 0) {
                    return;
                }
                ChatActivity.this.chatLayoutManager.scrollToPositionWithOffset(childAdapterPosition, scrollingOffsetForView);
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void didOpenPremiumGiftChannel(ChatActionCell chatActionCell, String str, boolean z) {
                initGiftProgressDialog(chatActionCell);
                GiftInfoBottomSheet.show(getBaseFragment(), str, ChatActivity.this.progressDialogCurrent);
            }

            private void initGiftProgressDialog(ChatActionCell chatActionCell) {
                if (ChatActivity.this.progressDialogCurrent != null) {
                    ChatActivity.this.progressDialogCurrent.cancel(true);
                }
                ChatActivity.this.progressDialogCurrent = (chatActionCell == null || chatActionCell.getMessageObject() == null) ? null : new AnonymousClass1(chatActionCell);
            }

            public class AnonymousClass1 extends Browser.Progress {
                final void lambda$end$0(ChatActionCell chatActionCell) {
                    ChatActivity.this.resetProgressDialogLoading();
                    chatActionCell.getMessageObject().flickerLoading = false;
                    chatActionCell.invalidate();
                }
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void needShowEffectOverlay(ChatActionCell chatActionCell, TLRPC.Document document, TLRPC.VideoSize videoSize) {
                ChatActivity.this.emojiAnimationsOverlay.showAnimationForActionCell(chatActionCell, document, videoSize);
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void didClickButton(ChatActionCell chatActionCell) {
                MessageObject messageObject;
                if (chatActionCell != null && (messageObject = chatActionCell.getMessageObject()) != null && messageObject.type == 22 && !chatActionCell.getMessageObject().isOutOwner() && chatActionCell.getMessageObject().isWallpaperForBoth() && chatActionCell.getMessageObject().isCurrentWallpaper()) {
                    AlertDialog alertDialogCreate = new AlertDialog.Builder(ChatActivity.this.getContext(), ChatActivity.this.getResourceProvider()).setTitle(LocaleController.getString(R.string.RemoveWallpaperTitle)).setMessage(LocaleController.getString(R.string.RemoveWallpaperMessage)).setPositiveButton(LocaleController.getString(R.string.Remove), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.ChatActivity$ChatActivityAdapter$2$$ExternalSyntheticLambda1
                        @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                        public final void onClick(AlertDialog alertDialog, int i) {
                            this.f$0.lambda$didClickButton$0(alertDialog, i);
                        }
                    }).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
                    ChatActivity.this.showDialog(alertDialogCreate);
                    TextView textView = (TextView) alertDialogCreate.getButton(-1);
                    if (textView != null) {
                        textView.setTextColor(ChatActivity.this.getThemedColor(Theme.key_text_RedBold));
                    }
                }
            }

            public MediaController.PhotoEntry val$entry;
                final void lambda$sendButtonPressed$3(final MessageObject messageObject, final TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatActivityAdapter$2$2$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$sendButtonPressed$2(tLObject, messageObject);
                        }
                    });
                }

                public void lambda$sendButtonPressed$1() {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", UserConfig.getInstance(((BaseFragment) ChatActivity.this).currentAccount).clientUserId);
                    ChatActivity.this.presentFragment(new ProfileActivity(bundle));
                }
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public BaseFragment getBaseFragment() {
                return ChatActivity.this;
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public long getTopicId() {
                return ChatActivity.this.getTopicId();
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public boolean didLongPress(ChatActionCell chatActionCell, float f, float f2) {
                if (((BaseFragment) ChatActivity.this).inPreviewMode) {
                    return false;
                }
                return ChatActivity.this.createMenu(chatActionCell, false, false, f, f2, true);
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void onTopicClick(ChatActionCell chatActionCell) {
                MessageObject messageObject;
                if (chatActionCell == null || (messageObject = chatActionCell.getMessageObject()) == null) {
                    return;
                }
                ChatActivity chatActivity = ChatActivity.this;
                if (chatActivity.topicsTabs == null) {
                    return;
                }
                boolean zIsMonoForum = ChatObject.isMonoForum(chatActivity.currentChat);
                ChatActivityAdapter chatActivityAdapter = ChatActivityAdapter.this;
                if (zIsMonoForum) {
                    ChatActivity.this.topicsTabs.selectTopic(messageObject.getMonoForumTopicId(), true);
                } else {
                    ChatActivity.this.topicsTabs.selectTopic(messageObject.getTopicId(), true);
                }
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void needOpenUserProfile(long j) {
                ChatActivity.this.openUserProfile(j);
            }

            @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
            public void didPressReplyMessage(final ChatActionCell chatActionCell, final int i) {
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatActivityAdapter$2$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$didPressReplyMessage$1(chatActionCell, i);
                    }
                };
                if (ChatActivity.this.chatAdapter.isFiltered) {
                    ChatActivity.this.setFilterMessages(false, true, true);
                    AndroidUtilities.runOnUIThread(runnable, 80L);
                } else {
                    runnable.run();
                }
            }

            public ChatMessageCell val$messageCell;

            public AnonymousClass5(ChatMessageCell chatMessageCell) {
                this.val$messageCell = chatMessageCell;
            }

            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                PipRoundVideoView pipRoundVideoView = PipRoundVideoView.getInstance();
                if (pipRoundVideoView != null) {
                    pipRoundVideoView.showTemporary(true);
                }
                this.val$messageCell.getViewTreeObserver().removeOnPreDrawListener(this);
                ImageReceiver photoImage = this.val$messageCell.getPhotoImage();
                float imageWidth = photoImage.getImageWidth();
                RectOld cameraRect = ChatActivity.this.instantCameraView.getCameraRect();
                float f = imageWidth / cameraRect.width;
                int[] iArr = new int[2];
                this.val$messageCell.getTransitionParams().ignoreAlpha = true;
                this.val$messageCell.setAlpha(0.0f);
                this.val$messageCell.setTimeAlpha(0.0f);
                this.val$messageCell.getLocationOnScreen(iArr);
                iArr[0] = (int) (iArr[0] + (photoImage.getImageX() - this.val$messageCell.getAnimationOffsetX()));
                iArr[1] = (int) (iArr[1] + ((photoImage.getImageY() + this.val$messageCell.getPaddingTop()) - this.val$messageCell.getTranslationY()));
                final InstantCameraView.InstantViewCameraContainer cameraContainer = ChatActivity.this.instantCameraView.getCameraContainer();
                cameraContainer.setPivotX(0.0f);
                cameraContainer.setPivotY(0.0f);
                AnimatorSet animatorSet = new AnimatorSet();
                cameraContainer.setImageReceiver(photoImage);
                AnimatorSet animatorSet2 = new AnimatorSet();
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(cameraContainer, (Property<InstantCameraView.InstantViewCameraContainer, Float>) View.SCALE_X, f);
                ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(cameraContainer, (Property<InstantCameraView.InstantViewCameraContainer, Float>) View.SCALE_Y, f);
                ObjectAnimator objectAnimatorOfFloat3 = ObjectAnimator.ofFloat(cameraContainer, (Property<InstantCameraView.InstantViewCameraContainer, Float>) View.TRANSLATION_Y, iArr[1] - cameraRect.y);
                View buttonsLayout = ChatActivity.this.instantCameraView.getButtonsLayout();
                Property property = View.ALPHA;
                animatorSet.playTogether(objectAnimatorOfFloat, objectAnimatorOfFloat2, objectAnimatorOfFloat3, ObjectAnimator.ofFloat(buttonsLayout, (Property<View, Float>) property, 0.0f), ObjectAnimator.ofFloat(ChatActivity.this.instantCameraView.getZoomSlider(), InstantCameraZoomSlider.OPEN_ALPHA, 0.0f), ObjectAnimator.ofInt(ChatActivity.this.instantCameraView.getPaint(), AnimationProperties.PAINT_ALPHA, 0), ObjectAnimator.ofFloat(ChatActivity.this.instantCameraView.getMuteImageView(), (Property<View, Float>) property, 0.0f));
                animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                ObjectAnimator objectAnimatorOfFloat4 = ObjectAnimator.ofFloat(cameraContainer, (Property<InstantCameraView.InstantViewCameraContainer, Float>) View.TRANSLATION_X, iArr[0] - cameraRect.x);
                objectAnimatorOfFloat4.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animatorSet2.playTogether(objectAnimatorOfFloat4, animatorSet);
                animatorSet2.setDuration(300L);
                InstantCameraView instantCameraView = ChatActivity.this.instantCameraView;
                if (instantCameraView != null) {
                    instantCameraView.setIsMessageTransition(true);
                }
                animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatActivity.ChatActivityAdapter.5.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        AnonymousClass5.this.val$messageCell.setAlpha(1.0f);
                        AnonymousClass5.this.val$messageCell.getTransitionParams().ignoreAlpha = false;
                        AnimationProperties.FloatProperty<ChatMessageCell> floatProperty = new AnimationProperties.FloatProperty<ChatMessageCell>("alpha") { // from class: org.telegram.ui.ChatActivity.ChatActivityAdapter.5.1.1
                            @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
                            public void setValue(ChatMessageCell chatMessageCell, float f2) {
                                chatMessageCell.setTimeAlpha(f2);
                            }

                            @Override // android.util.Property
                            public Float get(ChatMessageCell chatMessageCell) {
                                return Float.valueOf(chatMessageCell.getTimeAlpha());
                            }
                        };
                        AnimatorSet animatorSet3 = new AnimatorSet();
                        animatorSet3.playTogether(ObjectAnimator.ofFloat(cameraContainer, (Property<InstantCameraView.InstantViewCameraContainer, Float>) View.ALPHA, 0.0f), ObjectAnimator.ofFloat(AnonymousClass5.this.val$messageCell, floatProperty, 1.0f));
                        animatorSet3.setDuration(100L);
                        animatorSet3.setInterpolator(new DecelerateInterpolator());
                        animatorSet3.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatActivity.ChatActivityAdapter.5.1.2
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator2) {
                                InstantCameraView instantCameraView2 = ChatActivity.this.instantCameraView;
                                if (instantCameraView2 != null) {
                                    instantCameraView2.setIsMessageTransition(false);
                                    ChatActivity.this.instantCameraView.hideCamera(true);
                                    ChatActivity.this.instantCameraView.setVisibility(4);
                                }
                            }
                        });
                        animatorSet3.start();
                    }
                });
                animatorSet2.start();
                return true;
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            ArrayList<MessageObject> arrayList;
            if (ChatActivity.this.isClearingHistory() && i == this.botInfoEmptyRow) {
                return 3;
            }
            if (i == this.hintRow) {
                return 1;
            }
            int i2 = this.messagesStartRow;
            if (i >= i2 && i < this.messagesEndRow) {
                if (this.isFrozen) {
                    arrayList = this.frozenMessages;
                } else if (this.isFiltered) {
                    arrayList = this.filteredMessages;
                } else {
                    arrayList = ChatActivity.this.messages;
                }
                return arrayList.get(i - i2).contentType;
            }
            if (i == this.botInfoRow) {
                return 3;
            }
            if (i == this.userInfoRow) {
                return 6;
            }
            if (i == this.userNameTimeRow || i == this.userPhotoTimeRow) {
                return 7;
            }
            return i == this.botForumStartThreadRow ? 8 : 4;
        }

        public void notifyDataSetChanged(boolean z) {
            int size;
            boolean z2;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("notify data set changed fragmentOpened=" + ChatActivity.this.fragmentOpened);
            }
            if (z) {
                ChatActivity chatActivity = ChatActivity.this;
                if (chatActivity.fragmentOpened) {
                    if (chatActivity.chatListView.getItemAnimator() != ChatActivity.this.chatListItemAnimator) {
                        ChatActivity.this.chatListView.setItemAnimator(ChatActivity.this.chatListItemAnimator);
                    }
                } else {
                    ChatActivity.this.chatListView.setItemAnimator(null);
                }
            } else {
                ChatActivity.this.chatListView.setItemAnimator(null);
            }
            updateRowsInternal();
            try {
                super.notifyDataSetChanged();
                while (true) {
                    if (size >= 0) {
                        MessageObject messageObject = ChatActivity.this.messages.get(size);
                        if (!messageObject.isDateObject) {
                            TLRPC.Message message = messageObject.messageOwner;
                            if (message != null) {
                                TLRPC.MessageAction messageAction = message.action;
                                if ((messageAction instanceof TLRPC.TL_messageActionTopicCreate) || (messageAction instanceof TLRPC.TL_messageActionChannelCreate)) {
                                    z2 = true;
                                    break;
                                }
                            }
                        } else {
                            size--;
                        }
                    }
                    z2 = false;
                    break;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            size = ChatActivity.this.messages.size() - 1;
            if ((ChatActivity.this.endReached[0] && (ChatActivity.this.mergeDialogId == 0 || ChatActivity.this.endReached[1])) || z2) {
                ChatActivity chatActivity2 = ChatActivity.this;
                chatActivity2.checkDispatchHideSkeletons(((BaseFragment) chatActivity2).fragmentBeginToShow);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimatableAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            notifyDataSetChanged(false);
        }

        @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimatableAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemChanged(int i) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("notify item changed " + i);
            }
            if (!((BaseFragment) ChatActivity.this).fragmentBeginToShow || ChatActivity.this.ignoreItemAnimation) {
                ChatActivity.this.chatListView.setItemAnimator(null);
            } else if (ChatActivity.this.chatListView.getItemAnimator() != ChatActivity.this.chatListItemAnimator) {
                ChatActivity.this.chatListView.setItemAnimator(ChatActivity.this.chatListItemAnimator);
            }
            updateRowsInternal();
            try {
                super.notifyItemChanged(i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimatableAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int i, int i2) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("notify item range changed " + i + ":" + i2);
            }
            boolean z = ((BaseFragment) ChatActivity.this).fragmentBeginToShow;
            ChatActivity chatActivity = ChatActivity.this;
            if (!z) {
                chatActivity.chatListView.setItemAnimator(null);
            } else if (chatActivity.chatListView.getItemAnimator() != ChatActivity.this.chatListItemAnimator) {
                ChatActivity.this.chatListView.setItemAnimator(ChatActivity.this.chatListItemAnimator);
            }
            updateRowsInternal();
            try {
                super.notifyItemRangeChanged(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimatableAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemInserted(int i) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("notify item inserted " + i);
            }
            boolean z = ((BaseFragment) ChatActivity.this).fragmentBeginToShow;
            ChatActivity chatActivity = ChatActivity.this;
            if (!z) {
                chatActivity.chatListView.setItemAnimator(null);
            } else if (chatActivity.chatListView.getItemAnimator() != ChatActivity.this.chatListItemAnimator) {
                ChatActivity.this.chatListView.setItemAnimator(ChatActivity.this.chatListItemAnimator);
            }
            updateRowsInternal();
            try {
                super.notifyItemInserted(i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemMoved(int i, int i2) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("notify item moved" + i + ":" + i2);
            }
            boolean z = ((BaseFragment) ChatActivity.this).fragmentBeginToShow;
            ChatActivity chatActivity = ChatActivity.this;
            if (!z) {
                chatActivity.chatListView.setItemAnimator(null);
            } else if (chatActivity.chatListView.getItemAnimator() != ChatActivity.this.chatListItemAnimator) {
                ChatActivity.this.chatListView.setItemAnimator(ChatActivity.this.chatListItemAnimator);
            }
            updateRowsInternal();
            try {
                super.notifyItemMoved(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimatableAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeInserted(int i, int i2) {
            int i3;
            int i4;
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("notify item range inserted " + i + ":" + i2);
            }
            boolean z = ((BaseFragment) ChatActivity.this).fragmentBeginToShow;
            ChatActivity chatActivity = ChatActivity.this;
            if (!z) {
                chatActivity.chatListView.setItemAnimator(null);
            } else if (chatActivity.chatListView.getItemAnimator() != ChatActivity.this.chatListItemAnimator) {
                ChatActivity.this.chatListView.setItemAnimator(ChatActivity.this.chatListItemAnimator);
            }
            updateRowsInternal();
            if ((i == 1 || (ChatActivity.this.isFeedSearch() && i == this.messagesStartRow)) && i2 > 0 && (i3 = i + i2) >= (i4 = this.messagesStartRow) && i3 < this.messagesEndRow) {
                MessageObject messageObject = ChatActivity.this.messages.get(i3 - i4);
                MessageObject messageObject2 = ChatActivity.this.messages.get((i3 - this.messagesStartRow) - 1);
                if ((ChatActivity.this.isFeedSearch() && MessageObject.getPeerId(messageObject.messageOwner.peer_id) == MessageObject.getPeerId(messageObject2.messageOwner.peer_id)) || ((ChatActivity.this.currentChat != null && messageObject.getFromChatId() == messageObject2.getFromChatId()) || (ChatActivity.this.currentUser != null && messageObject.isOutOwner() == messageObject2.isOutOwner()))) {
                    notifyItemChanged(i);
                }
            }
            try {
                super.notifyItemRangeInserted(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimatableAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRemoved(int i) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("notify item removed " + i);
            }
            boolean z = ((BaseFragment) ChatActivity.this).fragmentBeginToShow;
            ChatActivity chatActivity = ChatActivity.this;
            if (!z) {
                chatActivity.chatListView.setItemAnimator(null);
            } else if (chatActivity.chatListView.getItemAnimator() != ChatActivity.this.chatListItemAnimator) {
                ChatActivity.this.chatListView.setItemAnimator(ChatActivity.this.chatListItemAnimator);
            }
            updateRowsInternal();
            try {
                super.notifyItemRemoved(i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void notifyItemRemoved(int i, boolean z) {
            if (BuildVars.LOGS_ENABLED) {
                StringBuilder sb = new StringBuilder("notify item removed ");
                sb.append(i);
                sb.append(z ? " with thanos effect" : _UrlKt.FRAGMENT_ENCODE_SET);
                FileLog.d(sb.toString());
            }
            boolean z2 = ((BaseFragment) ChatActivity.this).fragmentBeginToShow;
            ChatActivity chatActivity = ChatActivity.this;
            if (!z2) {
                chatActivity.chatListView.setItemAnimator(null);
            } else if (chatActivity.chatListView.getItemAnimator() != ChatActivity.this.chatListItemAnimator) {
                ChatActivity.this.chatListView.setItemAnimator(ChatActivity.this.chatListItemAnimator);
            }
            if (z && ChatActivity.this.chatListItemAnimator != null && ChatActivity.this.chatListView.getItemAnimator() == ChatActivity.this.chatListItemAnimator) {
                ChatActivity.this.chatListItemAnimator.prepareThanos(ChatActivity.this.chatListView.findViewHolderForAdapterPosition(i));
            }
            updateRowsInternal();
            try {
                super.notifyItemRemoved(i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimatableAdapter, androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeRemoved(int i, int i2) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("notify item range removed" + i + ":" + i2);
            }
            boolean z = ((BaseFragment) ChatActivity.this).fragmentBeginToShow;
            ChatActivity chatActivity = ChatActivity.this;
            if (!z) {
                chatActivity.chatListView.setItemAnimator(null);
            } else if (chatActivity.chatListView.getItemAnimator() != ChatActivity.this.chatListItemAnimator) {
                ChatActivity.this.chatListView.setItemAnimator(ChatActivity.this.chatListItemAnimator);
            }
            updateRowsInternal();
            try {
                super.notifyItemRangeRemoved(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public class SearchItemListener extends ActionBarMenuItem.ActionBarMenuItemSearchListener {
        private float searchAnimationProgress;

        private SearchItemListener() {
        }

        void lambda$onSearchCollapse$0(ValueAnimator valueAnimator) {
            setSearchAnimationProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
        }

        @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
        public void onSearchExpand() {
            ChatSearchTabs chatSearchTabs;
            ChatActivity.this.searching = true;
            ChatActivity.this.updatePagedownButtonVisibility(true);
            ChatActivity.this.updateSearchUpDownButtonVisibility(true);
            if ((ChatActivity.this.threadMessageId != 0 && ChatActivity.this.chatMode != 3) || UserObject.isReplyUser(ChatActivity.this.currentUser)) {
                ChatActivity chatActivity = ChatActivity.this;
                if (!chatActivity.preventReopenSearchWithText) {
                    chatActivity.lambda$openSearchWithText$413(null);
                }
            }
            if (ChatActivity.this.openSearchKeyboard) {
                ChatActivity.this.saveKeyboardPositionBeforeTransition();
                ChatActivity chatActivity2 = ChatActivity.this;
                if (!chatActivity2.isInsideContainer) {
                    AndroidUtilities.requestAdjustResize(chatActivity2.getParentActivity(), ((BaseFragment) ChatActivity.this).classGuid);
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$SearchItemListener$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onSearchExpand$1();
                    }
                }, 500L);
                ChatActivity.this.hideSendButtonHints();
            }
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.searchAnimationProgress, 1.0f);
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatActivity$SearchItemListener$$ExternalSyntheticLambda2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$onSearchExpand$2(valueAnimator);
                }
            });
            valueAnimatorOfFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            valueAnimatorOfFloat.setDuration(320L);
            valueAnimatorOfFloat.start();
            ChatActivity chatActivity3 = ChatActivity.this;
            SearchTagsList searchTagsList = chatActivity3.actionBarSearchTags;
            if (searchTagsList != null) {
                searchTagsList.show(!chatActivity3.isInsideContainer && searchTagsList.hasFilters() && ChatActivity.this.searchingHashtag == null);
            }
            if (ChatActivity.this.searchingHashtag == null || (chatSearchTabs = ChatActivity.this.hashtagSearchTabs) == null || chatSearchTabs.tabs.getCurrentPosition() == ChatActivity.this.defaultSearchPage) {
                return;
            }
            ChatActivity chatActivity4 = ChatActivity.this;
            chatActivity4.hashtagSearchTabs.tabs.scrollToTab(chatActivity4.defaultSearchPage, ChatActivity.this.defaultSearchPage);
        }

        public void lambda$didPressFactCheckWhat$0(HintView2 hintView2) {
            ChatActivity.this.contentView.removeView(hintView2);
            if (hintView2 == ChatActivity.this.factCheckHint) {
                ChatActivity.this.factCheckHint = null;
            }
        }

        public void lambda$sendMessageFromQuickShare$2(ArrayList arrayList, long j) {
            AlertsCreator.showSendMediaAlert(SendMessagesHelper.getInstance(((BaseFragment) ChatActivity.this).currentAccount).sendMessage(arrayList, j, false, false, true, 0, 0, null, -1, 0L, ChatActivity.this.getSendMonoForumPeerId(), ChatActivity.this.getSendMessageSuggestionParams()), ChatActivity.this, null);
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void didPressSideButton(ChatMessageCell chatMessageCell) {
            TLRPC.Message message;
            TLRPC.MessageReplyHeader messageReplyHeader;
            int i;
            TLRPC.MessageFwdHeader messageFwdHeader;
            MessageObject.GroupedMessages groupedMessages;
            if (ChatActivity.this.getParentActivity() == null) {
                return;
            }
            ChatActivity chatActivity = ChatActivity.this;
            if (chatActivity.topicsTabs != null && chatActivity.threadMessageId == 0 && (chatMessageCell.isForum || chatMessageCell.isMonoForum)) {
                ChatActivity.this.topicsTabs.selectTopic(chatMessageCell.getMessageObject().getTopicId(), true);
                return;
            }
            boolean zIsFrozen = ChatActivity.this.getMessagesController().isFrozen();
            ChatActivity chatActivity2 = ChatActivity.this;
            if (zIsFrozen) {
                AccountFrozenAlert.show(((BaseFragment) chatActivity2).currentAccount);
                return;
            }
            ChatActivityEnterView chatActivityEnterView = chatActivity2.chatActivityEnterView;
            if (chatActivityEnterView != null) {
                chatActivityEnterView.closeKeyboard();
            }
            MessageObject messageObject = chatMessageCell.getMessageObject();
            int i2 = ChatActivity.this.chatMode;
            ChatActivity chatActivity3 = ChatActivity.this;
            if (i2 == 2) {
                chatActivity3.chatActivityDelegate.openReplyMessage(messageObject.getId());
                ChatActivity.this.finishFragment();
                return;
            }
            if (chatActivity3.chatMode == 3 || ((ChatActivity.this.chatMode == 7 && ChatActivity.this.searchType == 2) || ((UserObject.isReplyUser(ChatActivity.this.currentUser) || UserObject.isUserSelf(ChatActivity.this.currentUser)) && (messageFwdHeader = messageObject.messageOwner.fwd_from) != null && messageFwdHeader.saved_from_peer != null))) {
                if (UserObject.isReplyUser(ChatActivity.this.currentUser) && (messageReplyHeader = (message = messageObject.messageOwner).reply_to) != null && (i = messageReplyHeader.reply_to_top_id) != 0) {
                    ChatActivity.this.openDiscussionMessageChat(messageReplyHeader.reply_to_peer_id.channel_id, null, i, 0L, -1, message.fwd_from.saved_from_msg_id, messageObject);
                    return;
                } else if (ChatActivity.this.chatMode == 7 && ChatActivity.this.searchType == 2) {
                    ChatActivity.this.openMessageInOriginalDialog(messageObject);
                    return;
                } else {
                    ChatActivity.this.openOriginalReplyChat(messageObject);
                    return;
                }
            }
            ArrayList<MessageObject> arrayList = (messageObject.getGroupId() == 0 || (groupedMessages = (MessageObject.GroupedMessages) ChatActivity.this.groupedMessagesMap.get(messageObject.getGroupId())) == null) ? null : groupedMessages.messages;
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                arrayList.add(messageObject);
            }
            boolean z = ChatActivity.this.getMessagesController().storiesEnabled() && StoryEntry.canRepostMessage(messageObject);
            ChatActivity chatActivity4 = ChatActivity.this;
            Context context = ChatActivity.this.getContext();
            ChatActivity chatActivity5 = ChatActivity.this;
            chatActivity4.showDialog(new AnonymousClass1(context, chatActivity5, arrayList, null, null, null, ChatObject.isChannel(chatActivity5.currentChat), null, null, false, false, z, null, ChatActivity.this.themeDelegate, z, messageObject));
            AndroidUtilities.setAdjustResizeToNothing(ChatActivity.this.getParentActivity(), ((BaseFragment) ChatActivity.this).classGuid);
            ChatActivity.this.fragmentView.requestLayout();
        }

        public class AnonymousClass1 extends ShareAlert {
            final void lambda$onShareStory$1(StoryRecorder storyRecorder, View view, Long l, Runnable runnable, Boolean bool, final Long l2) {
                boolean zBooleanValue = bool.booleanValue();
                StoryRecorder.SourceView sourceViewFromShareCell = null;
                if (zBooleanValue) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$1$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onShareStory$0(l2);
                        }
                    });
                    lambda$new$0();
                    storyRecorder.replaceSourceView(null);
                } else {
                    if ((view instanceof ShareDialogCell) && view.isAttachedToWindow()) {
                        sourceViewFromShareCell = StoryRecorder.SourceView.fromShareCell((ShareDialogCell) view);
                    }
                    storyRecorder.replaceSourceView(sourceViewFromShareCell);
                }
                AndroidUtilities.runOnUIThread(runnable);
            }

            public void lambda$didPressOther$4(final AlertDialog alertDialog, final HashSet hashSet, final TLRPC.TL_inputGroupCallInviteMessage tL_inputGroupCallInviteMessage, final MessageObject messageObject, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda51
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didPressOther$3(alertDialog, tLObject, hashSet, tL_inputGroupCallInviteMessage, messageObject, tL_error);
                }
            });
        }

        public void lambda$didPressOther$5(int i, DialogInterface dialogInterface) {
            ChatActivity.this.getConnectionsManager().cancelRequest(i, true);
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void didPressSponsoredClose(ChatMessageCell chatMessageCell) {
            ChatActivity.this.selectedObject = chatMessageCell.getMessageObject();
            ChatActivity.this.hideAds();
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void didPressSummarize(ChatMessageCell chatMessageCell, boolean z) {
            MessageObject messageObject = chatMessageCell.getMessageObject();
            TLRPC.Message message = messageObject.messageOwner;
            message.summarizedOpen = !message.summarizedOpen;
            messageObject.updateTranslation(true);
            ChatActivity.this.getMessagesStorage().updateMessageCustomParams(messageObject.getDialogId(), messageObject.messageOwner);
            ChatActivity.this.getMessagesController().getTranslateController().checkTranslation(messageObject, true);
            forceUpdate(chatMessageCell, true, true);
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void didPressSponsoredInfo(ChatMessageCell chatMessageCell, float f, float f2) {
            ChatActivity.this.createMenu(chatMessageCell, true, false, f, f2, false, false);
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void didPressUserStatus(ChatMessageCell chatMessageCell, TLRPC.User user, TLRPC.Document document, String str) {
            ImageLocation forDocument;
            String str2;
            if (chatMessageCell == null) {
                return;
            }
            if (!TextUtils.isEmpty(str)) {
                Browser.openUrl(ChatActivity.this.getContext(), "https://" + ChatActivity.this.getMessagesController().linkPrefix + "/nft/" + str);
                return;
            }
            if (!user.premium || DialogObject.getEmojiStatusDocumentId(user.emoji_status) == 0) {
                BadgesController badgesController = BadgesController.INSTANCE;
                BadgeDTO badge = badgesController.getBadge(user);
                if (badge != null) {
                    ChatActivity chatActivity = ChatActivity.this;
                    badgesController.showBadgeBulletin(chatActivity, badge, user, chatActivity.getResourceProvider(), ((BaseFragment) ChatActivity.this).currentAccount);
                    return;
                }
                return;
            }
            ChatActivity chatActivity2 = ChatActivity.this;
            PremiumPreviewBottomSheet premiumPreviewBottomSheet = new PremiumPreviewBottomSheet(chatActivity2, ((BaseFragment) chatActivity2).currentAccount, user, ChatActivity.this.themeDelegate);
            chatMessageCell.getLocationOnScreen(new int[2]);
            premiumPreviewBottomSheet.startEnterFromX = chatMessageCell.getNameStatusX();
            premiumPreviewBottomSheet.startEnterFromY = chatMessageCell.getNameStatusY();
            premiumPreviewBottomSheet.startEnterFromScale = chatMessageCell.getScaleX();
            premiumPreviewBottomSheet.startEnterFromX1 = chatMessageCell.getLeft();
            premiumPreviewBottomSheet.startEnterFromY1 = chatMessageCell.getTop();
            premiumPreviewBottomSheet.startEnterFromView = chatMessageCell;
            int colorId = UserObject.getColorId(user);
            ChatActivity chatActivity3 = ChatActivity.this;
            if (colorId >= 7) {
                MessagesController.PeerColors peerColors = MessagesController.getInstance(((BaseFragment) chatActivity3).currentAccount).peerColors;
                MessagesController.PeerColor color = peerColors != null ? peerColors.getColor(colorId) : null;
                premiumPreviewBottomSheet.accentColor = color != null ? Integer.valueOf(color.getColor1()) : null;
            } else {
                premiumPreviewBottomSheet.accentColor = Integer.valueOf(chatActivity3.getThemedColor(Theme.keys_avatar_nameInMessage[colorId]));
            }
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = chatMessageCell.currentNameStatusDrawable;
            if (swapAnimatedEmojiDrawable != null && (swapAnimatedEmojiDrawable.getDrawable() instanceof AnimatedEmojiDrawable)) {
                premiumPreviewBottomSheet.startEnterFromScale *= 0.95f;
                if (document != null) {
                    BackupImageView backupImageView = new BackupImageView(ChatActivity.this.getContext());
                    SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(document.thumbs, Theme.key_windowBackgroundWhiteGrayIcon, 0.2f);
                    TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                    if ("video/webm".equals(document.mime_type)) {
                        forDocument = ImageLocation.getForDocument(document);
                        if (svgThumb != null) {
                            svgThumb.overrideWidthAndHeight(512, 512);
                        }
                        str2 = "160_160_g";
                    } else {
                        if (svgThumb != null && MessageObject.isAnimatedStickerDocument(document, false)) {
                            svgThumb.overrideWidthAndHeight(512, 512);
                        }
                        forDocument = ImageLocation.getForDocument(document);
                        str2 = "160_160";
                    }
                    ImageLocation imageLocation = forDocument;
                    String str3 = str2;
                    backupImageView.setLayerNum(7);
                    backupImageView.setRoundRadius(AndroidUtilities.dp(4.0f));
                    backupImageView.setImage(imageLocation, str3, ImageLocation.getForDocument(closestPhotoSizeWithSize, document), "140_140", svgThumb, document);
                    if (MessageObject.isTextColorEmoji(document)) {
                        Integer num = premiumPreviewBottomSheet.accentColor;
                        backupImageView.setColorFilter(new PorterDuffColorFilter(num != null ? num.intValue() : ChatActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlueIcon), PorterDuff.Mode.SRC_IN));
                        premiumPreviewBottomSheet.statusStickerSet = MessageObject.getInputStickerSet(document);
                    } else {
                        premiumPreviewBottomSheet.statusStickerSet = MessageObject.getInputStickerSet(document);
                    }
                    premiumPreviewBottomSheet.overrideTitleIcon = backupImageView;
                    premiumPreviewBottomSheet.isEmojiStatus = true;
                }
            }
            ChatActivity.this.showDialog(premiumPreviewBottomSheet);
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void didPressAddPollOptionButton(ChatMessageCell chatMessageCell) {
            ChatActivity.this.pollAddOptionModeStart(chatMessageCell);
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public boolean allowAddPollOptions() {
            return ChatActivity.this.chatMode == 0;
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public int getAddPollOptionInputFieldHeight(ChatMessageCell chatMessageCell) {
            if (!ChatActivity.this.isInPollAddOptionMode() || ChatActivity.this.pollAddOptionFieldLayout == null || ChatActivity.this.pollAddOptionFieldLayout.cellToWatch != chatMessageCell || ChatActivity.this.pollAddOptionFieldLayout.textView.getWidth() <= 0) {
                return 0;
            }
            return ChatActivity.this.pollAddOptionFieldLayout.textView.getHeight();
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void drawPollMode(Canvas canvas, ChatMessageCell chatMessageCell) {
            if (ChatActivity.this.pollAddOptionFieldLayout == null || ChatActivity.this.pollAddOptionFieldLayout.cellToWatch != chatMessageCell) {
                return;
            }
            ChatActivity.this.pollAddOptionFieldLayout.drawInCell(canvas);
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void didPressUserBadge(ChatMessageCell chatMessageCell, TLRPC.User user, BadgeDTO badgeDTO) {
            if (chatMessageCell == null || user == null || badgeDTO == null) {
                return;
            }
            BadgesController badgesController = BadgesController.INSTANCE;
            ChatActivity chatActivity = ChatActivity.this;
            badgesController.showBadgeBulletin(chatActivity, badgeDTO, user, chatActivity.getResourceProvider(), ((BaseFragment) ChatActivity.this).currentAccount);
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public ChatActivityDraftMessageMeasureController getDraftMessageMeasureController() {
            return ChatActivity.this.botDraftHeightController;
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2, boolean z) {
            boolean z2 = true;
            if (((BaseFragment) ChatActivity.this).actionBar.isActionModeShowed() || ChatActivity.this.isReport()) {
                ChatActivity.this.processRowSelect(chatMessageCell, true, f, f2);
                return;
            }
            if (chatMessageCell != null && chatMessageCell.getMessageObject() != null && chatMessageCell.getMessageObject().isSponsored()) {
                didPressInstantButton(chatMessageCell, 10);
                return;
            }
            if (!ChatObject.isForum(ChatActivity.this.currentChat) && !ChatActivity.this.isThreadChat()) {
                z2 = false;
            }
            openProfile(user, z2);
        }

        void lambda$didPressCustomBotButton$22(TLRPC.Message message, String str) {
            ChatActivity.this.getMessagesController().rejectSuggestedMessage(DialogObject.getPeerDialogId(message.peer_id), message.id, str);
        }

        public void $r8$lambda$9XoCYDGAOcuypx9gpZ7znNQL_hU(Bulletin[] bulletinArr, DialogInterface dialogInterface) {
            Bulletin bulletin = bulletinArr[0];
            if (bulletin != null) {
                bulletin.hide();
            }
        }

        public void lambda$didPressCustomBotButton$24(MessageObject messageObject, boolean z, int i, int i2) {
            if (z) {
                ChatActivity.this.getMessagesController().approveSuggestedMessage(DialogObject.getPeerDialogId(messageObject.messageOwner.peer_id), messageObject.messageOwner.id, i);
            }
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void needShowPremiumBulletin(int i) {
            CharSequence charSequenceReplaceSingleTag;
            try {
                if (i == 0) {
                    ChatActivity.this.checkTopUndoView();
                    if (ChatActivity.this.topUndoView == null) {
                        return;
                    }
                    ChatActivity.this.topUndoView.showWithAction(0L, 84, null, new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda32
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$needShowPremiumBulletin$27();
                        }
                    });
                    ChatActivity.this.topUndoView.performHapticFeedback(3, 2);
                    return;
                }
                if (i == 1) {
                    String dateTime = LocaleController.formatDateTime(ChatActivity.this.getMessagesController().transcribeAudioTrialCooldownUntil, true);
                    int i2 = ChatActivity.this.getMessagesController().transcribeAudioTrialCooldownUntil;
                    ChatActivity chatActivity = ChatActivity.this;
                    BulletinFactory.of(ChatActivity.this).createSimpleBulletin(R.raw.transcribe, i2 > 0 ? AndroidUtilities.replaceTags(LocaleController.formatPluralString("TranscriptionTrialLeftUntil", TranscribeButton.getTranscribeTrialCount(((BaseFragment) chatActivity).currentAccount), dateTime)) : AndroidUtilities.replaceTags(LocaleController.formatPluralString("TranscriptionTrialLeft", TranscribeButton.getTranscribeTrialCount(((BaseFragment) chatActivity).currentAccount), new Object[0])), 6).show(true);
                    ChatActivity.this.fragmentView.performHapticFeedback(3, 2);
                    return;
                }
                if (i == 2 || i == 3) {
                    String dateTime2 = LocaleController.formatDateTime(ChatActivity.this.getMessagesController().transcribeAudioTrialCooldownUntil, true);
                    BulletinFactory bulletinFactoryOf = BulletinFactory.of(ChatActivity.this);
                    int i3 = R.raw.transcribe;
                    SpannableStringBuilder spannableStringBuilderAppend = new SpannableStringBuilder().append((CharSequence) AndroidUtilities.replaceTags(LocaleController.formatPluralString("TranscriptionTrialEnd", ChatActivity.this.getMessagesController().transcribeAudioTrialWeeklyNumber, new Object[0]))).append((CharSequence) " ");
                    if (i == 2) {
                        charSequenceReplaceSingleTag = AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.TranscriptionTrialEndBuy), new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda33
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$needShowPremiumBulletin$28();
                            }
                        });
                    } else {
                        charSequenceReplaceSingleTag = ChatActivity.this.getMessagesController().transcribeAudioTrialCooldownUntil <= 0 ? _UrlKt.FRAGMENT_ENCODE_SET : AndroidUtilities.replaceSingleTag(LocaleController.formatString(R.string.TranscriptionTrialEndWaitOrBuy, dateTime2), new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda34
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$needShowPremiumBulletin$29();
                            }
                        });
                    }
                    bulletinFactoryOf.createSimpleBulletin(i3, spannableStringBuilderAppend.append(charSequenceReplaceSingleTag), 6, 7000).show(true);
                    BotWebViewVibrationEffect.APP_ERROR.vibrate();
                }
            } catch (Exception unused) {
            }
        }

        public void lambda$didLongPressToDoButton$31() {
            ChatActivity.this.selectedObject = null;
            ChatActivity.this.selectedObjectGroup = null;
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public boolean didLongPressPollOption(ChatMessageCell chatMessageCell, TLRPC.PollAnswer pollAnswer) {
            if (ChatActivity.this.getParentActivity() == null || ChatActivity.this.getContext() == null) {
                return false;
            }
            if (ChatActivity.this.savedMessagesTagHint != null && ChatActivity.this.savedMessagesTagHint.shown()) {
                ChatActivity.this.savedMessagesTagHint.hide();
            }
            if (ChatActivity.this.videoConversionTimeHint != null && ChatActivity.this.videoConversionTimeHint.shown()) {
                ChatActivity.this.videoConversionTimeHint.hide();
            }
            MessageObject primaryMessageObject = chatMessageCell.getPrimaryMessageObject();
            TLRPC.MessageMedia media = MessageObject.getMedia(primaryMessageObject);
            if (primaryMessageObject == null || !(media instanceof TLRPC.TL_messageMediaPoll)) {
                return false;
            }
            ChatActivity.this.selectedObject = primaryMessageObject;
            ChatActivity.this.selectedObjectGroup = null;
            PollItemMenu pollItemMenu = new PollItemMenu(ChatActivity.this.getContext(), ChatActivity.this.getResourceProvider());
            pollItemMenu.setCell(ChatActivity.this, chatMessageCell, pollAnswer.option);
            ArrayList<Integer> arrayList = new ArrayList<>();
            ArrayList<CharSequence> arrayList2 = new ArrayList<>();
            ArrayList<Integer> arrayList3 = new ArrayList<>();
            ChatActivity.this.fillMessageMenu(primaryMessageObject, arrayList, arrayList2, arrayList3);
            ChatActivity chatActivity = ChatActivity.this;
            pollItemMenu.setupMessageOptions(chatActivity, arrayList, arrayList2, arrayList3, new ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda11(chatActivity));
            pollItemMenu.setOnDismissListener(new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didLongPressPollOption$37();
                }
            });
            pollItemMenu.show();
            return true;
        }

        public void lambda$didPressMoreChannelRecommendations$39() {
            ChatActivity.this.presentFragment(new PremiumPreviewFragment("similar_channels"));
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void didPressChannelRecommendation(final ChatMessageCell chatMessageCell, TLObject tLObject, boolean z) {
            if (ChatActivity.this.getContext() == null || tLObject == null) {
                return;
            }
            if (((BaseFragment) ChatActivity.this).parentLayout == null || !((BaseFragment) ChatActivity.this).parentLayout.isInPreviewMode()) {
                Bundle bundle = new Bundle();
                boolean z2 = tLObject instanceof TLRPC.Chat;
                if (z2) {
                    bundle.putLong("chat_id", ((TLRPC.Chat) tLObject).id);
                } else if (!(tLObject instanceof TLRPC.User)) {
                    return;
                } else {
                    bundle.putLong("user_id", ((TLRPC.User) tLObject).id);
                }
                if (z && z2) {
                    final TLRPC.Chat chat = (TLRPC.Chat) tLObject;
                    ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(ChatActivity.this.getContext(), R.drawable.popup_fixed_alert4, ChatActivity.this.getResourceProvider(), 2);
                    actionBarPopupWindowLayout.setBackgroundColor(ChatActivity.this.getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
                    ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(ChatActivity.this.getParentActivity(), false, false);
                    actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(R.string.OpenChannel2), R.drawable.msg_channel);
                    actionBarMenuSubItem.setMinimumWidth(160);
                    actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda3
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            this.f$0.lambda$didPressChannelRecommendation$40(view);
                        }
                    });
                    actionBarPopupWindowLayout.addView(actionBarMenuSubItem);
                    ActionBarMenuSubItem actionBarMenuSubItem2 = new ActionBarMenuSubItem(ChatActivity.this.getParentActivity(), false, false);
                    actionBarMenuSubItem2.setTextAndIcon(LocaleController.getString(R.string.ProfileJoinChannel), R.drawable.msg_addbot);
                    actionBarMenuSubItem2.setMinimumWidth(160);
                    actionBarMenuSubItem2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda4
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            this.f$0.lambda$didPressChannelRecommendation$42(chat, chatMessageCell, view);
                        }
                    });
                    actionBarPopupWindowLayout.addView(actionBarMenuSubItem2);
                    ChatActivity chatActivity = new ChatActivity(bundle);
                    chatActivity.allowExpandPreviewByClick = true;
                    ChatActivity.this.presentFragmentAsPreviewWithMenu(chatActivity, actionBarPopupWindowLayout);
                    ChatActivity.this.checkShowBlur(true);
                    return;
                }
                ChatActivity.this.presentFragment(new ChatActivity(bundle));
            }
        }

        public void lambda$didPressReplyMessage$43(AtomicReference atomicReference, ChatMessageCell chatMessageCell, float f, float f2, View view) {
            ((ActionBarPopupWindow) atomicReference.get()).dismiss();
            didLongPress(chatMessageCell, f, f2);
        }

        public void lambda$didPressReplyMessage$46(int i, final MessageObject messageObject, Integer num, byte[] bArr) {
            ChatActivity.this.scrollToMessageId(i, messageObject.getId(), true, messageObject.getDialogId() == ChatActivity.this.mergeDialogId ? 1 : 0, true, 0, num, bArr, new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda30
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didPressReplyMessage$45(messageObject);
                }
            });
        }

        public @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void didPressImage(ChatMessageCell chatMessageCell, float f, float f2, boolean z) {
            int i;
            int i2;
            boolean z2;
            File file;
            TLRPC.Chat chat;
            TLRPC.Message message;
            TLRPC.MessageMedia messageMedia;
            TLRPC.WebPage webPage;
            MessageObject messageObject = chatMessageCell.getMessageObject();
            int i3 = 2;
            if (messageObject.type == 23) {
                TLRPC.MessageMedia messageMedia2 = messageObject.messageOwner.media;
                TL_stories.StoryItem storyItem = messageMedia2.storyItem;
                if (storyItem == null || (storyItem instanceof TL_stories.TL_storyItemDeleted)) {
                    return;
                }
                storyItem.dialogId = DialogObject.getPeerDialogId(messageMedia2.peer);
                storyItem.messageId = messageObject.getId();
                storyItem.messageType = 2;
                StoriesUtilities.applyViewedUser(storyItem, ChatActivity.this.currentUser);
                ChatActivity.this.getOrCreateStoryViewer().open(ChatActivity.this.getContext(), messageObject.messageOwner.media.storyItem, StoriesListPlaceProvider.of(ChatActivity.this.chatListView));
                return;
            }
            boolean z3 = true;
            if (!messageObject.isVideo() || DownloadController.getInstance(((BaseFragment) ChatActivity.this).currentAccount).canDownloadMedia(messageObject.messageOwner) == 1) {
                messageObject.putInDownloadsStore = true;
            }
            if (messageObject.isSendError()) {
                ChatActivity.this.createMenu(chatMessageCell, false, false, f, f2, false);
                return;
            }
            if (messageObject.isSending()) {
                return;
            }
            chatActivityEnterView = null;
            ChatActivityEnterView chatActivityEnterView = null;
            pathToMessage = null;
            File pathToMessage = null;
            if (z && (message = messageObject.messageOwner) != null && (messageMedia = message.media) != null && (webPage = messageMedia.webpage) != null && !TextUtils.isEmpty(webPage.url)) {
                String str = messageObject.messageOwner.media.webpage.url;
                AndroidUtilities.getHostAuthority(str);
                if (ChatActivity.this.openLinkInternally(str, chatMessageCell, null, messageObject.getId(), 2)) {
                    return;
                }
                if (ChatActivity.this.progressDialogCurrent != null) {
                    ChatActivity.this.progressDialogCurrent.cancel(true);
                }
                ChatActivity.this.progressDialogCurrent = chatMessageCell.getMessageObject() != null ? new AnonymousClass15(chatMessageCell) : null;
                Browser.openUrl(ChatActivity.this.getParentActivity(), Uri.parse(str), true, false, false, ChatActivity.this.progressDialogCurrent, null, false, true, false);
                return;
            }
            final MessageObject messageObject2 = messageObject;
            if (messageObject2.isDice()) {
                final Runnable runnable = new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$didPressImage$48(messageObject2);
                    }
                };
                if (messageObject2.isStakeableDice()) {
                    ChatActivity.this.getMessagesController().loadStakeDiceInfo(new Utilities.Callback() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda2
                        @Override 
                        public final void run(Object obj) {
                            this.f$0.lambda$didPressImage$50(messageObject2, runnable, (Boolean) obj);
                        }
                    });
                    return;
                } else {
                    runnable.run();
                    return;
                }
            }
            int i4 = 0;
            if ((messageObject2.isAnimatedEmoji() && (!messageObject2.isAnimatedAnimatedEmoji() || (ChatActivity.this.emojiAnimationsOverlay.supports(MessageObject.findAnimatedEmojiEmoticon(messageObject2.getDocument())) && ChatActivity.this.currentUser != null))) || messageObject2.isPremiumSticker()) {
                ChatActivity.this.restartSticker(chatMessageCell);
                ChatActivity chatActivity = ChatActivity.this;
                chatActivity.emojiAnimationsOverlay.onTapItem(chatMessageCell, chatActivity, true);
                ChatActivity.this.chatListView.cancelClickRunnables(false);
                return;
            }
            if (messageObject2.needDrawBluredPreview()) {
                Runnable runnableSendSecretMessageRead = ChatActivity.this.sendSecretMessageRead(messageObject2, false);
                Runnable runnableSendSecretMediaDelete = ChatActivity.this.sendSecretMediaDelete(messageObject2);
                chatMessageCell.invalidate();
                SecretMediaViewer.getInstance().setParentActivity(ChatActivity.this.getParentActivity());
                SecretMediaViewer.getInstance().openMedia(messageObject2, ChatActivity.this.photoViewerProvider, runnableSendSecretMessageRead, runnableSendSecretMediaDelete);
                return;
            }
            if (MessageObject.isAnimatedEmoji(messageObject2.getDocument()) && MessageObject.getInputStickerSet(messageObject2.getDocument()) != null) {
                ArrayList arrayList = new ArrayList(1);
                arrayList.add(MessageObject.getInputStickerSet(messageObject2.getDocument()));
                ChatActivity chatActivity2 = ChatActivity.this;
                EmojiPacksAlert emojiPacksAlert = new EmojiPacksAlert(chatActivity2, chatActivity2.getParentActivity(), ChatActivity.this.themeDelegate, arrayList);
                emojiPacksAlert.setCalcMandatoryInsets(ChatActivity.this.isKeyboardVisible());
                ChatActivity.this.showDialog(emojiPacksAlert);
                return;
            }
            if (messageObject2.getInputStickerSet() != null) {
                Activity parentActivity = ChatActivity.this.getParentActivity();
                ChatActivity chatActivity3 = ChatActivity.this;
                TLRPC.InputStickerSet inputStickerSet = messageObject2.getInputStickerSet();
                if (ChatActivity.this.bottomChannelButtonsLayout.getVisibility() != 0 && ((chat = ChatActivity.this.currentChat) == null || ChatObject.canSendStickers(chat))) {
                    chatActivityEnterView = ChatActivity.this.chatActivityEnterView;
                }
                StickersAlert stickersAlert = new StickersAlert(parentActivity, chatActivity3, inputStickerSet, null, chatActivityEnterView, ChatActivity.this.themeDelegate, false);
                stickersAlert.setCalcMandatoryInsets(ChatActivity.this.isKeyboardVisible());
                ChatActivity.this.showDialog(stickersAlert);
                return;
            }
            int i5 = messageObject2.type;
            if (i5 == 13 || i5 == 15) {
                TLRPC.Document document = messageObject2.getDocument();
                int size = document.attributes.size();
                int i6 = 0;
                while (true) {
                    if (i6 >= size) {
                        i = 0;
                        break;
                    }
                    TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i6);
                    if (documentAttribute instanceof TLRPC.TL_documentAttributeImageSize) {
                        i4 = documentAttribute.w;
                        i = documentAttribute.h;
                        break;
                    }
                    i6++;
                }
                if (i4 > 512 || i > 512) {
                    ChatActivity.this.openPhotoViewerForMessage(chatMessageCell, messageObject2);
                    return;
                }
                return;
            }
            if (messageObject2.isVideo() || (i2 = messageObject2.type) == 1 || ((i2 == 0 && !messageObject2.isWebpageDocument()) || messageObject2.isGif())) {
                if (messageObject2.isSponsored()) {
                    if (messageObject2.isGif() || messageObject2.isPhoto()) {
                        ChatActivity.this.logSponsoredClicked(messageObject2, true, false);
                        if (messageObject2.sponsoredUrl != null) {
                            if (ChatActivity.this.progressDialogCurrent != null) {
                                ChatActivity.this.progressDialogCurrent.cancel(true);
                            }
                            ChatActivity.this.progressDialogCurrent = chatMessageCell.getMessageObject() != null ? new AnonymousClass16(chatMessageCell) : null;
                            Browser.openUrl(ChatActivity.this.getContext(), Uri.parse(messageObject2.sponsoredUrl), true, false, false, ChatActivity.this.progressDialogCurrent, null, false, ChatActivity.this.getMessagesController().sponsoredLinksInappAllow, false);
                            return;
                        }
                        return;
                    }
                    if (messageObject2.isVideo()) {
                        ChatActivity.this.logSponsoredClicked(messageObject2, true, false);
                    }
                }
                if (messageObject2.getDuration() > 0.0d && messageObject2.getVideoStartsTimestamp() > 0 && !messageObject2.openedInViewer) {
                    messageObject2.forceSeekTo = (float) (((double) messageObject2.getVideoStartsTimestamp()) / messageObject2.getDuration());
                }
                ChatActivity.this.openPhotoViewerForMessage(chatMessageCell, messageObject2);
                return;
            }
            int i7 = messageObject2.type;
            if (i7 == 3) {
                ChatActivity.this.sendSecretMessageRead(messageObject2, true);
                try {
                    String str2 = messageObject2.messageOwner.attachPath;
                    if (str2 != null && str2.length() != 0) {
                        pathToMessage = new File(messageObject2.messageOwner.attachPath);
                    }
                    if (pathToMessage == null || !pathToMessage.exists()) {
                        pathToMessage = ChatActivity.this.getFileLoader().getPathToMessage(messageObject2.messageOwner);
                    }
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.setFlags(1);
                    intent.setDataAndType(FileProvider.getUriForFile(ChatActivity.this.getParentActivity(), ApplicationLoader.getApplicationId() + ".provider", pathToMessage), "video/mp4");
                    ChatActivity.this.getParentActivity().startActivityForResult(intent, MediaError.DetailedErrorCode.SEGMENT_UNKNOWN);
                    return;
                } catch (Exception e) {
                    FileLog.e(e);
                    ChatActivity.this.alertUserOpenError(messageObject2);
                    return;
                }
            }
            if (i7 == 4) {
                if (AndroidUtilities.isMapsInstalled(ChatActivity.this)) {
                    boolean zIsLiveLocation = messageObject2.isLiveLocation();
                    ChatActivity chatActivity4 = ChatActivity.this;
                    if (zIsLiveLocation) {
                        TLRPC.Chat chat2 = chatActivity4.currentChat;
                        if (chat2 != null && !ChatObject.canSendMessages(chat2) && !ChatActivity.this.currentChat.megagroup) {
                            i3 = 6;
                        }
                        LocationActivity locationActivity = new LocationActivity(i3);
                        locationActivity.setDelegate(ChatActivity.this);
                        locationActivity.setMessageObject(messageObject2);
                        ChatActivity.this.presentFragment(locationActivity);
                        return;
                    }
                    LocationActivity locationActivity2 = new LocationActivity(chatActivity4.currentEncryptedChat == null ? 3 : 0);
                    locationActivity2.setDelegate(ChatActivity.this);
                    locationActivity2.setMessageObject(messageObject2);
                    ChatActivity.this.presentFragment(locationActivity2);
                    return;
                }
                return;
            }
            if (i7 == 9 || i7 == 0) {
                if (PreferencesUtils.getInstance().isBackup(messageObject2)) {
                    new BackupBottomSheet(ChatActivity.this, messageObject2).showIfPossible();
                    return;
                }
                if (PluginsController.isPlugin(messageObject2)) {
                    PluginsController.getInstance().showInstallDialog(ChatActivity.this, messageObject2);
                    return;
                }
                IconManager iconManager = IconManager.INSTANCE;
                if (iconManager.isIconPack(messageObject2)) {
                    iconManager.handleIconPack(ChatActivity.this, messageObject2);
                    return;
                }
                if (messageObject2.getDocumentName().toLowerCase().endsWith("attheme")) {
                    String str3 = messageObject2.messageOwner.attachPath;
                    if (str3 == null || str3.length() == 0) {
                        file = null;
                    } else {
                        file = new File(messageObject2.messageOwner.attachPath);
                        if (!file.exists()) {
                            file = null;
                        }
                    }
                    if (file == null) {
                        File pathToMessage2 = ChatActivity.this.getFileLoader().getPathToMessage(messageObject2.messageOwner);
                        if (pathToMessage2.exists()) {
                            file = pathToMessage2;
                        }
                    }
                    Theme.ThemeInfo themeInfoApplyThemeFile = Theme.applyThemeFile(file, messageObject2.getDocumentName(), null, true);
                    ChatActivity chatActivity5 = ChatActivity.this;
                    if (themeInfoApplyThemeFile != null) {
                        chatActivity5.presentFragment(new ThemePreviewActivity(themeInfoApplyThemeFile));
                        return;
                    }
                    chatActivity5.scrollToPositionOnRecreate = -1;
                }
                if (messageObject2.canPreviewDocument()) {
                    PhotoViewer photoViewer = PhotoViewer.getInstance();
                    ChatActivity chatActivity6 = ChatActivity.this;
                    photoViewer.setParentActivity(chatActivity6, chatActivity6.themeDelegate);
                    PhotoViewer photoViewer2 = PhotoViewer.getInstance();
                    ChatActivity chatActivity7 = ChatActivity.this;
                    photoViewer2.openPhoto(messageObject2, chatActivity7, messageObject2.type != 0 ? chatActivity7.dialog_id : 0L, messageObject2.type != 0 ? ChatActivity.this.mergeDialogId : 0L, messageObject2.type != 0 ? ChatActivity.this.getTopicId() : 0L, ChatActivity.this.photoViewerProvider);
                    messageObject2 = messageObject2;
                    z2 = true;
                } else {
                    z2 = false;
                }
                if (MarkdownParser.isMarkdown(messageObject2)) {
                    ChatActivity.this.parseMarkdownAsync(messageObject2);
                } else {
                    z3 = z2;
                }
                if (z3) {
                    return;
                }
                try {
                    AndroidUtilities.openForView(messageObject2, ChatActivity.this.getParentActivity(), ChatActivity.this.themeDelegate, false);
                } catch (Exception e2) {
                    FileLog.e(e2);
                    ChatActivity.this.alertUserOpenError(messageObject2);
                }
            }
        }

        public class AnonymousClass15 extends Browser.Progress {
            final void lambda$didPressImage$48(final MessageObject messageObject) {
            ChatActivity.this.createUndoView();
            if (ChatActivity.this.undoView == null) {
                return;
            }
            ChatActivity.this.undoView.showWithAction(0L, (ChatActivity.this.chatActivityEnterView.getVisibility() != 0 || ChatActivity.this.bottomOverlay.getVisibility() == 0) ? 17 : 16, messageObject.getDiceEmoji(), null, new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda31
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didPressImage$47(messageObject);
                }
            });
        }

        public void lambda$didPressImage$49(MessageObject messageObject, Long l) {
            if (ChatActivity.this.checkSlowModeAlert()) {
                SendMessagesHelper.SendMessageParams sendMessageParamsOf = SendMessagesHelper.SendMessageParams.of(messageObject.getDiceEmoji(), ChatActivity.this.dialog_id, ChatActivity.this.replyingMessageObject, ChatActivity.this.getThreadMessage(), null, false, null, null, null, true, 0, 0, null, false);
                sendMessageParamsOf.quick_reply_shortcut_id = ChatActivity.this.getQuickReplyId();
                sendMessageParamsOf.quick_reply_shortcut = ChatActivity.this.quickReplyShortcut;
                sendMessageParamsOf.dice_stake = l.longValue();
                ChatActivity.this.getSendMessagesHelper().sendMessage(sendMessageParamsOf);
            }
        }

        public class AnonymousClass16 extends Browser.Progress {
            final @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
            TLRPC.MessageMedia messageMedia;
            TLRPC.TL_webPageAttributeStory tL_webPageAttributeStory;
            TL_stories.StoryItem storyItem;
            TLRPC.Message message;
            TLRPC.MessageMedia messageMedia2;
            String string;
            TLRPC.WebPage webPage;
            MessageObject messageObject = chatMessageCell.getMessageObject();
            if (i == 19) {
                if (ChatActivity.this.progressDialogCurrent != null) {
                    ChatActivity.this.progressDialogCurrent.cancel(true);
                }
                ChatActivity.this.progressDialogCurrent = chatMessageCell.getMessageObject() != null ? new AnonymousClass17(chatMessageCell) : null;
                BoostDialogs.openGiveAwayStatusDialog(messageObject, ChatActivity.this.progressDialogCurrent, ChatActivity.this.getContext(), ChatActivity.this.getResourceProvider());
                return;
            }
            if (i == 21) {
                if (LaunchActivity.instance != null) {
                    if (ChatActivity.this.progressDialogCurrent != null) {
                        ChatActivity.this.progressDialogCurrent.cancel(true);
                    }
                    ChatActivity.this.progressDialogCurrent = chatMessageCell.getMessageObject() != null ? new AnonymousClass18(chatMessageCell) : null;
                    LaunchActivity.instance.checkAppUpdate(true, ChatActivity.this.progressDialogCurrent);
                    return;
                }
                return;
            }
            if (i == 84) {
                ChatActivity.this.pollAddOptionModeComplete(chatMessageCell);
                return;
            }
            if (i == 80) {
                PollVotesAlert.showForPoll(ChatActivity.this, messageObject);
                return;
            }
            if (i == 0) {
                TLRPC.MessageMedia messageMedia3 = messageObject.messageOwner.media;
                if (messageMedia3 == null || (webPage = messageMedia3.webpage) == null || webPage.cached_page == null) {
                    return;
                }
                LaunchActivity launchActivity = LaunchActivity.instance;
                if (launchActivity == null || launchActivity.getBottomSheetTabs() == null || LaunchActivity.instance.getBottomSheetTabs().tryReopenTab(messageObject) == null) {
                    ChatActivity.this.createArticleViewer(false).open(messageObject);
                    return;
                }
                return;
            }
            if (i == 5) {
                long j = messageObject.messageOwner.media.user_id;
                TLRPC.User user = j != 0 ? MessagesController.getInstance(((BaseFragment) ChatActivity.this).currentAccount).getUser(Long.valueOf(j)) : null;
                ChatActivity chatActivity = ChatActivity.this;
                TLRPC.MessageMedia messageMedia4 = messageObject.messageOwner.media;
                chatActivity.openVCard(user, messageMedia4.phone_number, messageMedia4.vcard, messageMedia4.first_name, messageMedia4.last_name);
                return;
            }
            if (i == 30) {
                long j2 = messageObject.messageOwner.media.user_id;
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", j2);
                ChatActivity.this.presentFragment(new ChatActivity(bundle));
                return;
            }
            if (i == 31) {
                long j3 = messageObject.messageOwner.media.user_id;
                TLRPC.User user2 = j3 != 0 ? MessagesController.getInstance(((BaseFragment) ChatActivity.this).currentAccount).getUser(Long.valueOf(j3)) : null;
                if (user2 != null) {
                    if (!TextUtils.isEmpty(messageObject.vCardData)) {
                        string = messageObject.vCardData.toString();
                    } else if (!TextUtils.isEmpty(user2.phone)) {
                        string = PhoneFormat.getInstance().format("+" + user2.phone);
                    } else {
                        String str = MessageObject.getMedia(messageObject.messageOwner).phone_number;
                        if (!TextUtils.isEmpty(str)) {
                            string = PhoneFormat.getInstance().format(str);
                        } else {
                            string = LocaleController.getString(R.string.NumberUnknown);
                        }
                    }
                    Bundle bundle2 = new Bundle();
                    bundle2.putLong("user_id", user2.id);
                    bundle2.putString("phone", string);
                    bundle2.putBoolean("addContact", true);
                    ChatActivity.this.presentFragment(new ContactAddActivity(bundle2));
                    return;
                }
                return;
            }
            if (i == 23 || i == 24) {
                final boolean z = i == 24;
                TLRPC.Message message2 = messageObject.messageOwner;
                TLRPC.WebPage webPage2 = (message2 == null || (messageMedia = message2.media) == null) ? null : messageMedia.webpage;
                if (webPage2 == null || webPage2.url == null) {
                    return;
                }
                Matcher matcher = Pattern.compile("^https?\\:\\/\\/t\\.me\\/add(?:emoji|stickers)\\/(.+)$").matcher(webPage2.url);
                if (ChatActivity.this.progressDialogCurrent != null) {
                    ChatActivity.this.progressDialogCurrent.cancel(true);
                }
                ChatActivity.this.progressDialogCurrent = chatMessageCell.getMessageObject() != null ? new AnonymousClass19(chatMessageCell) : null;
                if (matcher.matches() && matcher.groupCount() > 1 && matcher.group(1) != null) {
                    String strGroup = matcher.group(1);
                    if (MediaDataController.getInstance(((BaseFragment) ChatActivity.this).currentAccount).getStickerSetByName(strGroup) == null) {
                        ChatActivity.this.progressDialogCurrent.init();
                        TLRPC.TL_messages_getStickerSet tL_messages_getStickerSet = new TLRPC.TL_messages_getStickerSet();
                        TLRPC.TL_inputStickerSetShortName tL_inputStickerSetShortName = new TLRPC.TL_inputStickerSetShortName();
                        tL_inputStickerSetShortName.short_name = strGroup;
                        tL_messages_getStickerSet.stickerset = tL_inputStickerSetShortName;
                        final int iSendRequest = ConnectionsManager.getInstance(((BaseFragment) ChatActivity.this).currentAccount).sendRequest(tL_messages_getStickerSet, new RequestDelegate() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda21
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                this.f$0.lambda$didPressInstantButton$52(z, tLObject, tL_error);
                            }
                        });
                        ChatActivity.this.progressDialogCurrent.onCancel(new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda22
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$didPressInstantButton$53(iSendRequest);
                            }
                        });
                        return;
                    }
                }
                Browser.openUrl(ChatActivity.this.getParentActivity(), Uri.parse(webPage2.url), true, true, false, ChatActivity.this.progressDialogCurrent, null, false, true, false);
                return;
            }
            if (messageObject.isSponsored()) {
                ChatActivity.this.logSponsoredClicked(messageObject, false, false);
                if (messageObject.sponsoredUrl != null) {
                    if (ChatActivity.this.progressDialogCurrent != null) {
                        ChatActivity.this.progressDialogCurrent.cancel(true);
                    }
                    ChatActivity.this.progressDialogCurrent = chatMessageCell.getMessageObject() != null ? new AnonymousClass20(chatMessageCell) : null;
                    Browser.openUrl(ChatActivity.this.getContext(), Uri.parse(messageObject.sponsoredUrl), true, false, false, ChatActivity.this.progressDialogCurrent, null, false, ChatActivity.this.getMessagesController().sponsoredLinksInappAllow, false);
                    return;
                }
                return;
            }
            TLRPC.WebPage storyMentionWebpage = messageObject.getStoryMentionWebpage();
            if (storyMentionWebpage == null && (message = messageObject.messageOwner) != null && (messageMedia2 = message.media) != null) {
                storyMentionWebpage = messageMedia2.webpage;
            }
            if (storyMentionWebpage == null) {
                return;
            }
            if (storyMentionWebpage.attributes != null) {
                for (int i2 = 0; i2 < storyMentionWebpage.attributes.size(); i2++) {
                    if ((storyMentionWebpage.attributes.get(i2) instanceof TLRPC.TL_webPageAttributeStory) && (storyItem = (tL_webPageAttributeStory = (TLRPC.TL_webPageAttributeStory) storyMentionWebpage.attributes.get(i2)).storyItem) != null) {
                        storyItem.dialogId = DialogObject.getPeerDialogId(tL_webPageAttributeStory.peer);
                        tL_webPageAttributeStory.storyItem.messageId = messageObject.getId();
                        tL_webPageAttributeStory.storyItem.messageType = 1;
                        ChatActivity.this.getOrCreateStoryViewer().open(ChatActivity.this.getContext(), tL_webPageAttributeStory.storyItem, StoriesListPlaceProvider.of(ChatActivity.this.chatListView));
                        return;
                    }
                }
            }
            if (ChatActivity.this.openLinkInternally(storyMentionWebpage.url, chatMessageCell, null, messageObject.getId(), 2)) {
                return;
            }
            if (ChatActivity.this.progressDialogCurrent != null) {
                ChatActivity.this.progressDialogCurrent.cancel(true);
            }
            ChatActivity.this.progressDialogCurrent = chatMessageCell.getMessageObject() != null ? new AnonymousClass21(chatMessageCell) : null;
            Browser.openUrl(ChatActivity.this.getParentActivity(), Uri.parse(storyMentionWebpage.url), true, true, false, ChatActivity.this.progressDialogCurrent, null, false, true, false);
        }

        public class AnonymousClass17 extends Browser.Progress {
            final ChatMessageCell val$cell;

            public AnonymousClass18(ChatMessageCell chatMessageCell) {
                this.val$cell = chatMessageCell;
            }

            @Override 
            public void init() {
                ChatActivity.this.progressDialogAtMessageId = this.val$cell.getMessageObject().getId();
                ChatActivity.this.progressDialogAtMessageType = 2;
                ChatActivity.this.progressDialogLinkSpan = null;
                this.val$cell.invalidate();
            }

            @Override 
            public void end(boolean z) {
                if (z) {
                    return;
                }
                final ChatActivity chatActivity = ChatActivity.this;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$18$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        chatActivity.resetProgressDialogLoading();
                    }
                }, 250L);
            }
        }

        public class AnonymousClass19 extends Browser.Progress {
            final void lambda$didPressInstantButton$52(final boolean z, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda48
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didPressInstantButton$51(tLObject, z);
                }
            });
        }

        public ChatMessageCell val$cell;

            public AnonymousClass21(ChatMessageCell chatMessageCell) {
                this.val$cell = chatMessageCell;
            }

            @Override 
            public void init() {
                ChatActivity.this.progressDialogAtMessageId = this.val$cell.getMessageObject().getId();
                ChatActivity.this.progressDialogAtMessageType = 2;
                ChatActivity.this.progressDialogLinkSpan = null;
                this.val$cell.invalidate();
            }

            @Override 
            public void end(boolean z) {
                if (z) {
                    return;
                }
                final ChatActivity chatActivity = ChatActivity.this;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$21$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        chatActivity.resetProgressDialogLoading();
                    }
                }, 250L);
            }
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void didPressGiveawayChatButton(ChatMessageCell chatMessageCell, int i) {
            if (chatMessageCell.getMessageObject().messageOwner.media instanceof TLRPC.TL_messageMediaGiveaway) {
                long jLongValue = ((TLRPC.TL_messageMediaGiveaway) chatMessageCell.getMessageObject().messageOwner.media).channels.get(i).longValue();
                long j = ChatActivity.this.dialog_id;
                long j2 = -jLongValue;
                ChatActivity chatActivity = ChatActivity.this;
                if (j != j2) {
                    chatActivity.presentFragment(ChatActivity.of(j2));
                } else {
                    chatActivity.avatarContainer.openProfile(false);
                }
            }
            if (chatMessageCell.getMessageObject().messageOwner.media instanceof TLRPC.TL_messageMediaGiveawayResults) {
                ChatActivity.this.presentFragment(ProfileActivity.of(((TLRPC.TL_messageMediaGiveawayResults) chatMessageCell.getMessageObject().messageOwner.media).winners.get(i).longValue()));
            }
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public void didPressCommentButton(ChatMessageCell chatMessageCell) {
            MessageObject messageObject;
            int i;
            long j;
            MessageObject.GroupedMessages currentMessagesGroup = chatMessageCell.getCurrentMessagesGroup();
            if (currentMessagesGroup != null && !currentMessagesGroup.messages.isEmpty()) {
                messageObject = currentMessagesGroup.messages.get(0);
            } else {
                messageObject = chatMessageCell.getMessageObject();
            }
            MessageObject messageObject2 = messageObject;
            TLRPC.MessageReplies messageReplies = messageObject2.messageOwner.replies;
            if (messageReplies != null) {
                i = messageReplies.read_max_id;
                j = messageReplies.channel_id;
            } else {
                i = -1;
                j = 0;
            }
            int i2 = i;
            long j2 = j;
            boolean zIsFeedSearch = ChatActivity.this.isFeedSearch();
            ChatActivity chatActivity = ChatActivity.this;
            if (zIsFeedSearch) {
                ChatActivity.this.openDiscussionMessageChat(-messageObject2.getDialogId(), FeedMessageUtils.getForwardingMessageObject(((BaseFragment) chatActivity).currentAccount, true, messageObject2), messageObject2.getRealId(), j2, i2, 0, messageObject2);
            } else {
                chatActivity.openDiscussionMessageChat(chatActivity.currentChat.id, messageObject2, messageObject2.getId(), j2, i2, 0, null);
            }
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public boolean isAdmin(long j) {
            TLRPC.Chat chat = ChatActivity.this.currentChat;
            if (chat == null || ChatObject.isChannelAndNotMegaGroup(chat)) {
                return false;
            }
            return ChatActivity.this.getMessagesController().isAdmin(ChatActivity.this.currentChat.id, j);
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public boolean isOwner(long j) {
            TLRPC.Chat chat = ChatActivity.this.currentChat;
            if (chat == null || ChatObject.isChannelAndNotMegaGroup(chat)) {
                return false;
            }
            return ChatActivity.this.getMessagesController().isOwner(ChatActivity.this.currentChat.id, j);
        }

        @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
        public String getAdminRank(long j) {
            String adminRank;
            if (UserObject.isBotForum(ChatActivity.this.currentUser)) {
                return null;
            }
            TLRPC.Chat chat = ChatActivity.this.currentChat;
            if (chat != null && !ChatObject.isChannelAndNotMegaGroup(chat) && (adminRank = ChatActivity.this.getMessagesController().getAdminRank(ChatActivity.this.currentChat.id, j)) != null) {
                return adminRank;
            }
            if (ChatActivity.this.forumTopic == null || ChatActivity.this.forumTopic.from_id == null || !(ChatActivity.this.forumTopic.from_id.user_id == j || ChatActivity.this.forumTopic.from_id.channel_id == j || ChatActivity.this.forumTopic.from_id.chat_id == j)) {
                return null;
            }
            return LocaleController.getString(R.string.TopicCreator);
        }

        private void didPressAdmin(final ChatMessageCell chatMessageCell, TLObject tLObject) {
            TLRPC.Chat chat;
            boolean z;
            String str;
            boolean z2;
            String str2;
            boolean z3;
            TLRPC.User currentUser = chatMessageCell.getCurrentUser();
            ChatActivity.this.getUserConfig().getCurrentUser();
            if (!AndroidUtilities.isContextSafe(ChatActivity.this.getContext()) || (chat = ChatActivity.this.currentChat) == null || currentUser == null || ChatObject.isChannelAndNotMegaGroup(chat)) {
                return;
            }
            boolean z4 = true;
            boolean z5 = false;
            if (tLObject instanceof TLRPC.ChannelParticipant) {
                TLRPC.ChannelParticipant channelParticipant = (TLRPC.ChannelParticipant) tLObject;
                if (channelParticipant instanceof TLRPC.TL_channelParticipantCreator) {
                    z = false;
                    z5 = true;
                } else if (channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) {
                    z = channelParticipant.promoted_by == ChatActivity.this.getUserConfig().getClientUserId();
                } else {
                    z = false;
                    z4 = false;
                }
                str = channelParticipant.rank;
            } else {
                if (tLObject instanceof TLRPC.TL_chatChannelParticipant) {
                    TLRPC.ChannelParticipant channelParticipant2 = ((TLRPC.TL_chatChannelParticipant) tLObject).channelParticipant;
                    if (channelParticipant2 instanceof TLRPC.TL_channelParticipantCreator) {
                        z2 = false;
                        z5 = true;
                    } else if (channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) {
                        z2 = channelParticipant2.promoted_by == ChatActivity.this.getUserConfig().getClientUserId();
                    } else {
                        z2 = false;
                        z4 = false;
                    }
                    str2 = channelParticipant2.rank;
                    z3 = z2;
                } else if (tLObject instanceof TLRPC.ChatParticipant) {
                    if (tLObject instanceof TLRPC.TL_chatParticipantCreator) {
                        z = false;
                        z5 = true;
                    } else if (tLObject instanceof TLRPC.TL_chatParticipantAdmin) {
                        z = ((TLRPC.TL_chatParticipantAdmin) tLObject).inviter_id == ChatActivity.this.getUserConfig().getClientUserId();
                    } else {
                        z = false;
                        z4 = false;
                    }
                    str = ((TLRPC.ChatParticipant) tLObject).rank;
                } else {
                    if (ChatObject.isChannel(ChatActivity.this.currentChat)) {
                        TLRPC.TL_channels_getParticipant tL_channels_getParticipant = new TLRPC.TL_channels_getParticipant();
                        ChatActivity.this.getMessagesController();
                        tL_channels_getParticipant.channel = MessagesController.getInputChannel(ChatActivity.this.currentChat);
                        tL_channels_getParticipant.participant = ChatActivity.this.getMessagesController().getInputPeer(currentUser.id);
                        ChatActivity.this.getConnectionsManager().sendRequestTyped(tL_channels_getParticipant, new AiTonesController$$ExternalSyntheticLambda0(), new Utilities.Callback2() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda0
                            @Override 
                            public final void run(Object obj, Object obj2) {
                                this.f$0.lambda$didPressAdmin$54(chatMessageCell, (TLRPC.TL_channels_channelParticipant) obj, (TLRPC.TL_error) obj2);
                            }
                        });
                        return;
                    }
                    return;
                }
                boolean z6 = z4;
                boolean z7 = z5;
                Context context = ChatActivity.this.getContext();
                int i = ((BaseFragment) ChatActivity.this).currentAccount;
                ChatActivity chatActivity = ChatActivity.this;
                TagEditCell.showInfoSheet(context, i, -chatActivity.currentChat.id, currentUser, str2, z6, z7, z3, ((BaseFragment) chatActivity).resourceProvider);
            }
            z3 = z;
            str2 = str;
            boolean z8 = z4;
            boolean z9 = z5;
            Context context2 = ChatActivity.this.getContext();
            int i2 = ((BaseFragment) ChatActivity.this).currentAccount;
            ChatActivity chatActivity2 = ChatActivity.this;
            TagEditCell.showInfoSheet(context2, i2, -chatActivity2.currentChat.id, currentUser, str2, z8, z9, z3, ((BaseFragment) chatActivity2).resourceProvider);
        }

        public void lambda$didPressRevealSensitiveContent$60(AlertDialog alertDialog, final ChatMessageCell chatMessageCell, final TL_account.contentSettings contentsettings) {
            alertDialog.dismissUnless(200L);
            boolean z = ChatActivity.this.getMessagesController().config.needAgeVideoVerification.get() && !TextUtils.isEmpty(ChatActivity.this.getMessagesController().verifyAgeBotUsername);
            boolean z2 = (contentsettings == null || !contentsettings.sensitive_can_change) && z;
            final boolean[] zArr = new boolean[1];
            Context context = alertDialog.getContext();
            FrameLayout frameLayout = new FrameLayout(context);
            if (z) {
                zArr[0] = true;
            } else if (contentsettings != null && contentsettings.sensitive_can_change) {
                CheckBoxCell checkBoxCell = new CheckBoxCell(context, 1, ChatActivity.this.getResourceProvider());
                checkBoxCell.setBackground(Theme.getSelectorDrawable(false));
                checkBoxCell.setText(LocaleController.getString(R.string.MessageShowSensitiveContentAlways), _UrlKt.FRAGMENT_ENCODE_SET, zArr[0], false);
                checkBoxCell.setPadding(LocaleController.isRTL ? AndroidUtilities.dp(16.0f) : AndroidUtilities.dp(8.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(8.0f) : AndroidUtilities.dp(16.0f), 0);
                frameLayout.addView(checkBoxCell, LayoutHelper.createFrame(-1, 48.0f, 51, 0.0f, 0.0f, 0.0f, 0.0f));
                checkBoxCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda27
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        ChatActivity.ChatMessageCellDelegate.$r8$lambda$pJE1rJF9boPUg_6ddW1r6itKsV0(zArr, view);
                    }
                });
            }
            AlertDialog.Builder negativeButton = new AlertDialog.Builder(context, ChatActivity.this.getResourceProvider()).setTitle(LocaleController.getString(R.string.MessageShowSensitiveContentMediaTitle)).setMessage(LocaleController.getString(z2 ? R.string.MessageShowSensitiveContentMediaTextClosed : R.string.MessageShowSensitiveContentMediaText)).setView(frameLayout).setCustomViewOffset(9).setNegativeButton(LocaleController.getString(z2 ? R.string.MessageShowSensitiveContentMediaTextClosedButton : R.string.Cancel), null);
            if (!z2) {
                final boolean z3 = z;
                negativeButton.setPositiveButton(LocaleController.getString(R.string.MessageShowSensitiveContentButton), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.ChatActivity$ChatMessageCellDelegate$$ExternalSyntheticLambda28
                    @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                    public final void onClick(AlertDialog alertDialog2, int i) {
                        this.f$0.lambda$didPressRevealSensitiveContent$59(chatMessageCell, zArr, z3, contentsettings, alertDialog2, i);
                    }
                });
            }
            ChatActivity.this.showDialog(negativeButton.create());
        }

        public static void lambda$didPressRevealSensitiveContent$56(ChatMessageCell chatMessageCell, Boolean bool) {
            if (bool.booleanValue()) {
                for (int i = 0; i < ChatActivity.this.chatListView.getChildCount(); i++) {
                    View childAt = ChatActivity.this.chatListView.getChildAt(i);
                    if (childAt instanceof ChatMessageCell) {
                        ChatMessageCell chatMessageCell2 = (ChatMessageCell) childAt;
                        if (chatMessageCell2.getMessageObject() != null && chatMessageCell2.getMessageObject().isSensitive()) {
                            chatMessageCell2.startRevealMedia();
                        }
                    }
                }
                return;
            }
            if (chatMessageCell.getMessageObject() != null) {
                chatMessageCell.getMessageObject().isSensitiveCached = Boolean.FALSE;
            }
            chatMessageCell.startRevealMedia();
        }

        public void lambda$onEndAnimation$0() {
            ChatActivity.this.getNotificationCenter().onAnimationFinish(ChatActivity.this.scrollCallbackAnimationIndex);
        }

        @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimationCallback
        public void recycleView(View view) {
            if (view instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                chatMessageCell.setDelegate(null);
                chatMessageCell.setResourcesProvider(null);
            }
        }
    }

    public static boolean isClickableLink(String str) {
        return str.startsWith("https://") || str.startsWith("@") || str.startsWith("#") || str.startsWith("$") || str.startsWith("video?");
    }

    public SimpleTextView getReplyNameTextView() {
        return this.replyLayout.current().name;
    }

    public SimpleTextView getReplyObjectTextView() {
        return this.replyLayout.current().obj;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        int i;
        if (this.forceDisallowRedrawThemeDescriptions) {
            return null;
        }
        if (this.isPauseOnThemePreview) {
            this.isPauseOnThemePreview = false;
            return null;
        }
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = new ThemeDescription.ThemeDescriptionDelegate() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda104
            @Override // org.telegram.ui.ActionBar.ThemeDescription.ThemeDescriptionDelegate
            public final void didSetColor() {
                this.f$0.lambda$getThemeDescriptions$442();
            }
        };
        ArrayList<ThemeDescription> arrayList = new ArrayList<>();
        arrayList.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, Theme.key_chat_wallpaper));
        arrayList.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, Theme.key_chat_wallpaper_gradient_to1));
        arrayList.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, Theme.key_chat_wallpaper_gradient_to2));
        arrayList.add(new ThemeDescription(this.fragmentView, 0, null, null, null, null, Theme.key_chat_wallpaper_gradient_to3));
        if (!isReport()) {
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_ITEMSCOLOR, null, null, null, null, Theme.key_actionBarDefaultIcon));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, Theme.key_actionBarDefaultSubtitle));
        } else {
            ActionBar actionBar = this.actionBar;
            int i2 = ThemeDescription.FLAG_AB_ITEMSCOLOR;
            int i3 = Theme.key_actionBarActionModeDefaultIcon;
            arrayList.add(new ThemeDescription(actionBar, i2, null, null, null, null, i3));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_TITLECOLOR, null, null, null, null, i3));
            arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBTITLECOLOR, null, null, null, null, i3));
        }
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUBACKGROUND, null, null, null, themeDescriptionDelegate, Theme.key_actionBarDefaultSubmenuBackground));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM, null, null, null, themeDescriptionDelegate, Theme.key_actionBarDefaultSubmenuItem));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SUBMENUITEM | ThemeDescription.FLAG_IMAGECOLOR, null, null, null, themeDescriptionDelegate, Theme.key_actionBarDefaultSubmenuItemIcon));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LISTGLOWCOLOR, null, null, null, null, Theme.key_actionBarDefault));
        ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
        arrayList.add(new ThemeDescription(chatAvatarContainer != null ? chatAvatarContainer.getTitleTextView() : null, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_actionBarDefaultTitle));
        ChatAvatarContainer chatAvatarContainer2 = this.avatarContainer;
        SimpleTextView titleTextView = chatAvatarContainer2 != null ? chatAvatarContainer2.getTitleTextView() : null;
        int i4 = ThemeDescription.FLAG_IMAGECOLOR;
        int i5 = Theme.key_actionBarDefaultSubtitle;
        arrayList.add(new ThemeDescription(titleTextView, i4, null, null, null, null, i5));
        ChatAvatarContainer chatAvatarContainer3 = this.avatarContainer;
        arrayList.add(new ThemeDescription(chatAvatarContainer3 != null ? chatAvatarContainer3.getSubtitleTextView() : null, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, new Paint[]{Theme.chat_statusPaint, Theme.chat_statusRecordPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_status, (Object) null));
        ChatAvatarContainer chatAvatarContainer4 = this.avatarContainer;
        arrayList.add(new ThemeDescription(chatAvatarContainer4 != null ? chatAvatarContainer4.getSubtitleTextView() : null, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, (Class[]) null, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i5, (Object) null));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarDefaultSelector));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCH, null, null, null, null, Theme.key_actionBarDefaultSearch));
        ActionBar actionBar2 = this.actionBar;
        int i6 = ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER;
        int i7 = Theme.key_actionBarDefaultSearchPlaceholder;
        arrayList.add(new ThemeDescription(actionBar2, i6, null, null, null, null, i7));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_SEARCHPLACEHOLDER, null, null, null, null, i7));
        ActionBar actionBar3 = this.actionBar;
        int i8 = ThemeDescription.FLAG_AB_AM_ITEMSCOLOR;
        int i9 = Theme.key_actionBarActionModeDefaultIcon;
        arrayList.add(new ThemeDescription(actionBar3, i8, null, null, null, null, i9));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_BACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefault));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_TOPBACKGROUND, null, null, null, null, Theme.key_actionBarActionModeDefaultTop));
        arrayList.add(new ThemeDescription(this.actionBar, ThemeDescription.FLAG_AB_AM_SELECTORCOLOR, null, null, null, null, Theme.key_actionBarActionModeDefaultSelector));
        arrayList.add(new ThemeDescription(this.selectedMessagesCountTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i9));
        ChatAvatarContainer chatAvatarContainer5 = this.avatarContainer;
        arrayList.add(new ThemeDescription(chatAvatarContainer5 != null ? chatAvatarContainer5.getTitleTextView() : null, 0, null, null, new Drawable[]{Theme.chat_muteIconDrawable}, null, Theme.key_chat_muteIcon));
        ChatAvatarContainer chatAvatarContainer6 = this.avatarContainer;
        arrayList.add(new ThemeDescription(chatAvatarContainer6 != null ? chatAvatarContainer6.getTitleTextView() : null, 0, null, null, new Drawable[]{Theme.chat_lockIconDrawable}, null, Theme.key_chat_lockIcon));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundRed));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundOrange));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundViolet));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundGreen));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundCyan));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundBlue));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_backgroundPink));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageRed));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageOrange));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageViolet));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageGreen));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageCyan));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessageBlue));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_avatar_nameInMessagePink));
        Theme.MessageDrawable messageDrawable = (Theme.MessageDrawable) getThemedDrawable("drawableMsgIn");
        Theme.MessageDrawable messageDrawable2 = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInMedia");
        Theme.MessageDrawable messageDrawable3 = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInSelected");
        Theme.MessageDrawable messageDrawable4 = (Theme.MessageDrawable) getThemedDrawable("drawableMsgInMediaSelected");
        Theme.MessageDrawable messageDrawable5 = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOut");
        Theme.MessageDrawable messageDrawable6 = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOutMedia");
        Theme.MessageDrawable messageDrawable7 = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOutSelected");
        Theme.MessageDrawable messageDrawable8 = (Theme.MessageDrawable) getThemedDrawable("drawableMsgOutMediaSelected");
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, BotHelpCell.class}, null, new Drawable[]{messageDrawable, messageDrawable2}, null, Theme.key_chat_inBubble));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{messageDrawable3, messageDrawable4}, null, Theme.key_chat_inBubbleSelected));
        if (messageDrawable != null) {
            Drawable[] shadowDrawables = messageDrawable.getShadowDrawables();
            int i10 = Theme.key_chat_inBubbleShadow;
            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, shadowDrawables, null, i10));
            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, messageDrawable2.getShadowDrawables(), null, i10));
            Drawable[] shadowDrawables2 = messageDrawable5.getShadowDrawables();
            int i11 = Theme.key_chat_outBubbleShadow;
            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, shadowDrawables2, null, i11));
            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, messageDrawable6.getShadowDrawables(), null, i11));
        }
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{messageDrawable5, messageDrawable6}, null, Theme.key_chat_outBubble));
        if (!this.themeDelegate.isThemeChangeAvailable(false)) {
            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{messageDrawable5, messageDrawable6}, null, Theme.key_chat_outBubbleGradient1));
            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{messageDrawable5, messageDrawable6}, null, Theme.key_chat_outBubbleGradient2));
            arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{messageDrawable5, messageDrawable6}, null, Theme.key_chat_outBubbleGradient3));
        }
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{messageDrawable7, messageDrawable8}, null, Theme.key_chat_outBubbleSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{messageDrawable7, messageDrawable8}, null, Theme.key_chat_outBubbleGradientSelectedOverlay));
        Paint themedPaint = getThemedPaint("paintChatActionText");
        int i12 = Theme.key_chat_serviceText;
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActionCell.class}, themedPaint, null, null, i12));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatActionCell.class}, getThemedPaint("paintChatActionText"), null, null, Theme.key_chat_serviceLink));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_botCardDrawable, getThemedDrawable("drawableShareIcon"), getThemedDrawable("drawableReplyIcon"), getThemedDrawable("drawableBotInline"), getThemedDrawable("drawableBotLink"), getThemedDrawable("drawableBotLock"), getThemedDrawable("drawable_botInvite"), getThemedDrawable("drawableGoIcon"), getThemedDrawable("drawableCommentSticker")}, null, Theme.key_chat_serviceIcon));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, Theme.key_chat_serviceBackground));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, ChatActionCell.class}, null, null, null, Theme.key_chat_serviceBackgroundSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class, BotHelpCell.class}, null, null, null, Theme.key_chat_messageTextIn));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_messageTextOut));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class, BotHelpCell.class}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageLinkIn, (Object) null));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{ChatMessageCell.class}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messageLinkOut, (Object) null));
        Drawable[] drawableArr = {Theme.chat_msgNoSoundDrawable};
        int i13 = Theme.key_chat_mediaTimeText;
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, drawableArr, null, i13));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable("drawableMsgOutCheck")}, null, Theme.key_chat_outSentCheck));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable("drawableMsgOutCheckSelected")}, null, Theme.key_chat_outSentCheckSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable("drawableMsgOutCheckRead"), getThemedDrawable("drawableMsgOutHalfCheck")}, null, Theme.key_chat_outSentCheckRead));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable("drawableMsgOutCheckReadSelected"), getThemedDrawable("drawableMsgOutHalfCheckSelected")}, null, Theme.key_chat_outSentCheckReadSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outSentClock));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outSentClockSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inSentClock));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inSentClockSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaCheckDrawable, Theme.chat_msgMediaHalfCheckDrawable}, null, Theme.key_chat_mediaSentCheck));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable("drawableMsgStickerHalfCheck"), getThemedDrawable("drawableMsgStickerCheck"), getThemedDrawable("drawableMsgStickerClock"), getThemedDrawable("drawableMsgStickerViews"), getThemedDrawable("drawableMsgStickerReplies"), getThemedDrawable("drawableMsgStickerPinned")}, null, i12));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaSentClock));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable("drawableMsgOutViews"), getThemedDrawable("drawableMsgOutReplies"), getThemedDrawable("drawableMsgOutPinned")}, null, Theme.key_chat_outViews));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable("drawableMsgOutViewsSelected"), getThemedDrawable("drawableMsgOutReplies"), getThemedDrawable("drawableMsgOutPinnedSelected")}, null, Theme.key_chat_outViewsSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsDrawable, Theme.chat_msgInRepliesDrawable, Theme.chat_msgInPinnedDrawable}, null, Theme.key_chat_inViews));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInViewsSelectedDrawable, Theme.chat_msgInRepliesSelectedDrawable, Theme.chat_msgInPinnedSelectedDrawable}, null, Theme.key_chat_inViewsSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaViewsDrawable, Theme.chat_msgMediaRepliesDrawable, Theme.chat_msgMediaPinnedDrawable}, null, Theme.key_chat_mediaViews));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable("drawableMsgOutMenu")}, null, Theme.key_chat_outMenu));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable("drawableMsgOutMenuSelected")}, null, Theme.key_chat_outMenuSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuDrawable}, null, Theme.key_chat_inMenu));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgInMenuSelectedDrawable}, null, Theme.key_chat_inMenuSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgMediaMenuDrawable}, null, Theme.key_chat_mediaMenu));
        Drawable[] drawableArr2 = {getThemedDrawable("drawableMsgOutInstant")};
        int i14 = Theme.key_chat_outInstant;
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, drawableArr2, null, i14));
        Drawable[] drawableArr3 = {Theme.chat_msgInInstantDrawable, Theme.chat_commentDrawable, Theme.chat_commentArrowDrawable};
        int i15 = Theme.key_chat_inInstant;
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, drawableArr3, null, i15));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable("drawableMsgOutCallAudio"), getThemedDrawable("drawableMsgOutCallVideo")}, null, i14));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{getThemedDrawable("drawableMsgOutCallAudioSelected"), getThemedDrawable("drawableMsgOutCallVideo")}, null, i14));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInCallDrawable, null, i15));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, Theme.chat_msgInCallSelectedDrawable, null, Theme.key_chat_inInstantSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallUpGreenDrawable}, null, Theme.key_chat_outGreenCall));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallDownRedDrawable}, null, Theme.key_fill_RedNormal));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgCallDownGreenDrawable}, null, Theme.key_chat_inGreenCall));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_msgErrorPaint, null, null, Theme.key_chat_sentError));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_msgErrorDrawable}, null, Theme.key_chat_sentErrorIcon));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, themeDescriptionDelegate, Theme.key_chat_selectedBackground));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_durationPaint, null, null, Theme.key_chat_previewDurationText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_gamePaint, null, null, Theme.key_chat_previewGameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewInstantText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewInstantText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_deleteProgressPaint, null, null, Theme.key_chat_secretTimeText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, getThemedPaint("paintChatBotButton"), null, null, Theme.key_chat_botButtonText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, getThemedPaint("paintChatTimeBackground"), null, null, Theme.key_chat_mediaTimeBackground));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inForwardedNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outForwardedNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPsaNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPsaNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inViaBotNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outViaBotNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerViaBotNameText));
        int i16 = Theme.key_chat_inReplyLine;
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, i16));
        int i17 = Theme.key_chat_outReplyLine;
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, i17));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyLine));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMessageText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMessageText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inReplyMediaMessageSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outReplyMediaMessageSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_stickerReplyMessageText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPreviewLine));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPreviewLine));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inSiteNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outSiteNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactPhoneText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inContactPhoneSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactPhoneText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outContactPhoneSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaProgress));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioProgress));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioProgress));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSelectedProgress));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSelectedProgress));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, i13));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inTimeSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAdminText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAdminSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAdminText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAdminSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outTimeSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioPerformerText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioPerformerSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioPerformerText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioPerformerSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioTitleText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioTitleText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioDurationText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioDurationText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioDurationSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioDurationSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbar));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbar));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbarSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbarSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioSeekbarFill));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inAudioCacheSeekbar));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioSeekbarFill));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outAudioCacheSeekbar));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbar));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbar));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbarSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbarSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVoiceSeekbarFill));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVoiceSeekbarFill));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileProgress));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileProgress));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileProgressSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileProgressSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileNameText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileInfoText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileInfoText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileInfoSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileInfoSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileBackground));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileBackground));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inFileBackgroundSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outFileBackgroundSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueInfoText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueInfoText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inVenueInfoSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outVenueInfoSelectedText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_mediaInfoText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_urlPaint, null, null, Theme.key_chat_linkSelectBackground));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_outUrlPaint, null, null, i17));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, Theme.chat_textSearchSelectionPaint, null, null, i16));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outLoader));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outMediaIcon));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outLoaderSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outMediaIconSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inLoader));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inMediaIcon));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inLoaderSelected));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inMediaIconSelected));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, Theme.key_chat_inContactBackground));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[0]}, null, Theme.key_chat_inContactIcon));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, Theme.key_chat_outContactBackground));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_contactDrawable[1]}, null, Theme.key_chat_outContactIcon));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inLocationBackground));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[0]}, null, Theme.key_chat_inLocationIcon));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_locationDrawable[1]}, null, Theme.key_chat_outLocationIcon));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPollCorrectAnswer));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPollCorrectAnswer));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_inPollWrongAnswer));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, null, null, Theme.key_chat_outPollWrongAnswer));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_pollHintDrawable[0]}, null, Theme.key_chat_inPreviewInstantText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_pollHintDrawable[1]}, null, Theme.key_chat_outPreviewInstantText));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_psaHelpDrawable[0]}, null, Theme.key_chat_inViews));
        arrayList.add(new ThemeDescription(this.chatListView, 0, new Class[]{ChatMessageCell.class}, null, new Drawable[]{Theme.chat_psaHelpDrawable[1]}, null, Theme.key_chat_outViews));
        if (this.themeDelegate.isThemeChangeAvailable(false)) {
            i = 0;
        } else {
            arrayList.add(new ThemeDescription(this.messagesSearchListView, 0, new Class[]{DialogCell.class}, null, Theme.avatarDrawables, null, Theme.key_avatar_text));
            arrayList.add(new ThemeDescription(this.messagesSearchListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_countPaint, null, null, Theme.key_chats_unreadCounter));
            arrayList.add(new ThemeDescription(this.messagesSearchListView, 0, new Class[]{DialogCell.class}, (String[]) null, new Paint[]{Theme.dialogs_namePaint[0], Theme.dialogs_namePaint[1], Theme.dialogs_searchNamePaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_name));
            arrayList.add(new ThemeDescription(this.messagesSearchListView, 0, new Class[]{DialogCell.class}, (String[]) null, new Paint[]{Theme.dialogs_nameEncryptedPaint[0], Theme.dialogs_nameEncryptedPaint[1], Theme.dialogs_searchNameEncryptedPaint}, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_secretName));
            arrayList.add(new ThemeDescription(this.messagesSearchListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_lockDrawable}, null, Theme.key_chats_secretIcon));
            i = 0;
            arrayList.add(new ThemeDescription(this.messagesSearchListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_scamDrawable, Theme.dialogs_fakeDrawable}, null, Theme.key_chats_draft));
            arrayList.add(new ThemeDescription(this.messagesSearchListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messagePaint[1], null, null, Theme.key_chats_message_threeLines));
            arrayList.add(new ThemeDescription(this.messagesSearchListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_messageNamePaint, null, null, Theme.key_chats_nameMessage_threeLines));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_chats_nameMessage));
            arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_chats_attachMessage));
            arrayList.add(new ThemeDescription(this.messagesSearchListView, 0, new Class[]{DialogCell.class}, (String[]) null, Theme.dialogs_messagePrintingPaint, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chats_actionMessage));
            arrayList.add(new ThemeDescription(this.messagesSearchListView, 0, new Class[]{DialogCell.class}, Theme.dialogs_timePaint, null, null, Theme.key_chats_date));
            arrayList.add(new ThemeDescription(this.messagesSearchListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkDrawable}, null, Theme.key_chats_sentCheck));
            arrayList.add(new ThemeDescription(this.messagesSearchListView, 0, new Class[]{DialogCell.class}, null, new Drawable[]{Theme.dialogs_checkReadDrawable, Theme.dialogs_halfCheckDrawable}, null, Theme.key_chats_sentReadCheck));
        }
        MentionsContainerView mentionsContainerView = this.mentionContainer;
        Paint themedPaint2 = getThemedPaint("paintChatComposeBackground");
        int i18 = Theme.key_chat_messagePanelBackground;
        arrayList.add(new ThemeDescription(mentionsContainerView, 0, null, themedPaint2, null, null, i18));
        MentionsContainerView mentionsContainerView2 = this.mentionContainer;
        Drawable[] drawableArr4 = {Theme.chat_composeShadowDrawable};
        int i19 = Theme.key_chat_messagePanelShadow;
        arrayList.add(new ThemeDescription(mentionsContainerView2, 0, null, null, drawableArr4, null, i19));
        arrayList.add(new ThemeDescription(this.mentionContainer, 0, null, null, new Drawable[]{Theme.chat_composeShadowRoundDrawable}, null, i18));
        arrayList.add(new ThemeDescription(this.searchContainer, 0, null, getThemedPaint("paintChatComposeBackground"), null, null, i18));
        arrayList.add(new ThemeDescription(this.searchContainer, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, i19));
        arrayList.add(new ThemeDescription(this.bottomOverlay, 0, null, getThemedPaint("paintChatComposeBackground"), null, null, i18));
        arrayList.add(new ThemeDescription(this.bottomOverlay, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, i19));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, null, getThemedPaint("paintChatComposeBackground"), null, null, i18));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, null, null, new Drawable[]{Theme.chat_composeShadowDrawable}, null, i19));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelText));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_CURSORCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelCursor));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_HINTTEXTCOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"messageEditText"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_glass_defaultText));
        int i20 = Theme.key_chat_messagePanelSend;
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"sendButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i20));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"sendButton"}, null, null, 24, null, i20));
        int i21 = Theme.key_glass_defaultIcon;
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"botButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i21));
        int i22 = Theme.key_listSelector;
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"botButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i22));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"notifyButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i21));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR | ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatActivityEnterView.class}, new String[]{"scheduledButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i21));
        int i23 = Theme.key_chat_recordedVoiceDot;
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"scheduledButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i23));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"scheduledButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i22));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"attachButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i21));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"attachButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i22));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"suggestButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i21));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"suggestButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i22));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"notifyButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i22));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"videoTimelineView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i20));
        int i24 = Theme.key_chat_messagePanelVoicePressed;
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"micDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i24));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"cameraDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i24));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"sendDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i24));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, null, null, null, null, Theme.key_chat_messagePanelVoiceLock));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, null, null, null, Theme.key_chat_messagePanelVoiceLockBackground));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"lockShadowDrawable"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelVoiceLockShadow));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"recordDeleteImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i22));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_BACKGROUNDFILTER, new Class[]{ChatActivityEnterView.class}, new String[]{"recordedAudioBackground"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_recordedVoiceBackground));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, null, null, null, null, Theme.key_chat_recordTime));
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        int i25 = Theme.key_chat_recordVoiceCancel;
        arrayList.add(new ThemeDescription(chatActivityEnterView, 0, null, null, null, null, i25));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, null, null, null, null, i25));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatActivityEnterView.class}, new String[]{"cancelBotButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelCancelInlineBot));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, new Class[]{ChatActivityEnterView.class}, new String[]{"cancelBotButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i22));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"redDotPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i23));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"paint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_messagePanelVoiceBackground));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, new String[]{"dotPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_emojiPanelNewTrending));
        arrayList.add(new ThemeDescription(this.chatActivityEnterView, 0, new Class[]{ChatActivityEnterView.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_chat_recordedVoicePlayPause));
        ChatActivityEnterView chatActivityEnterView2 = this.chatActivityEnterView;
        arrayList.add(new ThemeDescription(chatActivityEnterView2 != null ? chatActivityEnterView2.getEmojiView() : null, 0, new Class[]{EmojiView.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_chat_emojiPanelBackground));
        ChatActivityEnterView chatActivityEnterView3 = this.chatActivityEnterView;
        arrayList.add(new ThemeDescription(chatActivityEnterView3 != null ? chatActivityEnterView3.getEmojiView() : null, 0, new Class[]{EmojiView.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_chat_emojiPanelShadowLine));
        ChatActivityEnterView chatActivityEnterView4 = this.chatActivityEnterView;
        arrayList.add(new ThemeDescription(chatActivityEnterView4 != null ? chatActivityEnterView4.getEmojiView() : null, 0, new Class[]{EmojiView.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_chat_emojiPanelEmptyText));
        ChatActivityEnterView chatActivityEnterView5 = this.chatActivityEnterView;
        arrayList.add(new ThemeDescription(chatActivityEnterView5 != null ? chatActivityEnterView5.getEmojiView() : null, 0, new Class[]{EmojiView.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_chat_emojiPanelIcon));
        ChatActivityEnterView chatActivityEnterView6 = this.chatActivityEnterView;
        arrayList.add(new ThemeDescription(chatActivityEnterView6 != null ? chatActivityEnterView6.getEmojiView() : null, 0, new Class[]{EmojiView.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_chat_emojiPanelIconSelected));
        ChatActivityEnterView chatActivityEnterView7 = this.chatActivityEnterView;
        arrayList.add(new ThemeDescription(chatActivityEnterView7 != null ? chatActivityEnterView7.getEmojiView() : null, 0, new Class[]{EmojiView.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_chat_emojiPanelStickerPackSelector));
        ChatActivityEnterView chatActivityEnterView8 = this.chatActivityEnterView;
        arrayList.add(new ThemeDescription(chatActivityEnterView8 != null ? chatActivityEnterView8.getEmojiView() : null, 0, new Class[]{EmojiView.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_chat_emojiPanelBackspace));
        ChatActivityEnterView chatActivityEnterView9 = this.chatActivityEnterView;
        arrayList.add(new ThemeDescription(chatActivityEnterView9 != null ? chatActivityEnterView9.getEmojiView() : null, 0, new Class[]{EmojiView.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_chat_emojiPanelTrendingTitle));
        ChatActivityEnterView chatActivityEnterView10 = this.chatActivityEnterView;
        arrayList.add(new ThemeDescription(chatActivityEnterView10 != null ? chatActivityEnterView10.getEmojiView() : null, 0, new Class[]{EmojiView.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_chat_emojiPanelTrendingDescription));
        ChatActivityEnterView chatActivityEnterView11 = this.chatActivityEnterView;
        arrayList.add(new ThemeDescription(chatActivityEnterView11 != null ? chatActivityEnterView11.getEmojiView() : null, 0, new Class[]{EmojiView.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_chat_emojiBottomPanelIcon));
        ChatActivityEnterView chatActivityEnterView12 = this.chatActivityEnterView;
        arrayList.add(new ThemeDescription(chatActivityEnterView12 != null ? chatActivityEnterView12.getEmojiView() : null, 0, new Class[]{EmojiView.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_chat_emojiSearchIcon));
        ChatActivityEnterView chatActivityEnterView13 = this.chatActivityEnterView;
        arrayList.add(new ThemeDescription(chatActivityEnterView13 != null ? chatActivityEnterView13.getEmojiView() : null, 0, new Class[]{EmojiView.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_chat_emojiPanelStickerSetNameHighlight));
        ChatActivityEnterView chatActivityEnterView14 = this.chatActivityEnterView;
        arrayList.add(new ThemeDescription(chatActivityEnterView14 != null ? chatActivityEnterView14.getEmojiView() : null, 0, new Class[]{EmojiView.class}, (String[]) null, (Paint[]) null, (Drawable[]) null, themeDescriptionDelegate, Theme.key_chat_emojiPanelStickerPackSelectorLine));
        ChatActivityEnterView chatActivityEnterView15 = this.chatActivityEnterView;
        if (chatActivityEnterView15 != null) {
            TrendingStickersAlert trendingStickersAlert = chatActivityEnterView15.getTrendingStickersAlert();
            if (trendingStickersAlert != null) {
                arrayList.addAll(trendingStickersAlert.getThemeDescriptions());
            }
            arrayList.add(new ThemeDescription((View) null, 0, (Class[]) null, (String[]) null, (Paint[]) null, new Drawable[]{this.chatActivityEnterView.getStickersArrowDrawable()}, (ThemeDescription.ThemeDescriptionDelegate) null, i21));
        }
        int i26 = i;
        while (i26 < 2) {
            UndoView undoView = i26 == 0 ? this.undoView : this.topUndoView;
            arrayList.add(new ThemeDescription(undoView, ThemeDescription.FLAG_BACKGROUNDFILTER, null, null, null, null, Theme.key_undo_background));
            int i27 = Theme.key_undo_cancelColor;
            arrayList.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"undoImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i27));
            arrayList.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"undoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i27));
            int i28 = Theme.key_undo_infoColor;
            arrayList.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"infoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i28));
            arrayList.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i28));
            arrayList.add(new ThemeDescription(undoView, ThemeDescription.FLAG_LINKCOLOR, new Class[]{UndoView.class}, new String[]{"subinfoTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i27));
            arrayList.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"textPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i28));
            arrayList.add(new ThemeDescription(undoView, 0, new Class[]{UndoView.class}, new String[]{"progressPaint"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i28));
            arrayList.add(new ThemeDescription(undoView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{UndoView.class}, new String[]{"leftImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i28));
            i26++;
        }
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonText));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_botKeyboardButtonBackgroundPressed));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerBackground));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"playButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerPlayPause));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerTitle));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerPerformer));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"titleTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_returnToCallText));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{FragmentContextView.class}, new String[]{"closeButton"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_inappPlayerClose));
        arrayList.add(new ThemeDescription(this.fragmentView, ThemeDescription.FLAG_BACKGROUND | ThemeDescription.FLAG_CHECKTAG, new Class[]{FragmentContextView.class}, new String[]{"frameLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_returnToCallBackground));
        arrayList.add(new ThemeDescription(this.pinnedLineView, 0, null, null, null, themeDescriptionDelegate, Theme.key_chat_topPanelLine));
        arrayList.add(new ThemeDescription(this.pinnedLineView, 0, null, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhite));
        arrayList.add(new ThemeDescription(this.pinnedCounterTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelTitle));
        for (int i29 = i; i29 < 2; i29++) {
            arrayList.add(new ThemeDescription(this.pinnedNameTextView[i29], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelTitle));
            arrayList.add(new ThemeDescription(this.pinnedMessageTextView[i29], ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelMessage));
        }
        arrayList.add(new ThemeDescription(this.alertNameTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelTitle));
        arrayList.add(new ThemeDescription(this.alertTextView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_topPanelMessage));
        ImageView imageView = this.closePinned;
        int i30 = ThemeDescription.FLAG_IMAGECOLOR;
        int i31 = Theme.key_chat_topPanelClose;
        arrayList.add(new ThemeDescription(imageView, i30, null, null, null, null, i31));
        arrayList.add(new ThemeDescription(this.pinnedListButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, i31));
        arrayList.add(new ThemeDescription(this.closeReportSpam, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, i31));
        TextView textView = this.addToContactsButton;
        int i32 = ThemeDescription.FLAG_TEXTCOLOR;
        int i33 = Theme.key_chat_addContact;
        arrayList.add(new ThemeDescription(textView, i32, null, null, null, null, i33));
        arrayList.add(new ThemeDescription(this.reportSpamButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, Theme.key_text_RedBold));
        arrayList.add(new ThemeDescription(this.reportSpamButton, ThemeDescription.FLAG_TEXTCOLOR | ThemeDescription.FLAG_CHECKTAG, null, null, null, null, i33));
        arrayList.add(new ThemeDescription(this.replyCloseImageView, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, Theme.key_glass_defaultIcon));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_chat_replyPanelName));
        ImageView imageView2 = this.searchCalendarButton;
        int i34 = ThemeDescription.FLAG_IMAGECOLOR;
        int i35 = Theme.key_chat_searchPanelIcons;
        arrayList.add(new ThemeDescription(imageView2, i34, null, null, null, null, i35));
        ImageView imageView3 = this.searchCalendarButton;
        int i36 = ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE;
        int i37 = Theme.key_actionBarActionModeDefaultSelector;
        arrayList.add(new ThemeDescription(imageView3, i36, null, null, null, null, i37));
        arrayList.add(new ThemeDescription(this.searchUserButton, ThemeDescription.FLAG_IMAGECOLOR, null, null, null, null, i35));
        arrayList.add(new ThemeDescription(this.searchUserButton, ThemeDescription.FLAG_BACKGROUNDFILTER | ThemeDescription.FLAG_DRAWABLESELECTEDSTATE, null, null, null, null, i37));
        arrayList.add(new ThemeDescription(this.bottomOverlayText, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, Theme.key_chat_secretChatStatusText));
        arrayList.add(new ThemeDescription(this.bottomOverlayChatText, 0, null, null, null, null, Theme.key_glass_defaultText));
        arrayList.add(new ThemeDescription(this.bottomOverlayChatText, 0, null, null, null, null, Theme.key_chat_goDownButtonCounterBackground));
        arrayList.add(new ThemeDescription(this.bottomOverlayChatText, 0, null, null, null, null, Theme.key_chat_messagePanelBackground));
        arrayList.add(new ThemeDescription(this.bottomOverlayProgress, 0, null, null, null, null, Theme.key_featuredStickers_buttonText));
        ChatBigEmptyView chatBigEmptyView = this.bigEmptyView;
        int i38 = ThemeDescription.FLAG_TEXTCOLOR;
        int i39 = Theme.key_chat_serviceText;
        arrayList.add(new ThemeDescription(chatBigEmptyView, i38, null, null, null, null, i39));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_TEXTCOLOR, null, null, null, null, i39));
        arrayList.add(new ThemeDescription(this.progressBar, ThemeDescription.FLAG_PROGRESSBAR, null, null, null, null, i39));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_USEBACKGROUNDDRAWABLE, new Class[]{ChatUnreadCell.class}, new String[]{"backgroundLayout"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_unreadMessagesStartBackground));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_unreadMessagesStartArrowIcon));
        arrayList.add(new ThemeDescription(this.chatListView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{ChatUnreadCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_unreadMessagesStartText));
        View view = this.progressView2;
        int i40 = ThemeDescription.FLAG_SERVICEBACKGROUND;
        int i41 = Theme.key_chat_serviceBackground;
        arrayList.add(new ThemeDescription(view, i40, null, null, null, null, i41));
        arrayList.add(new ThemeDescription(this.emptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, i41));
        arrayList.add(new ThemeDescription(this.bigEmptyView, ThemeDescription.FLAG_SERVICEBACKGROUND, null, null, null, null, i41));
        if (this.mentionContainer != null) {
            arrayList.add(new ThemeDescription(this.mentionContainer.getListView(), ThemeDescription.FLAG_TEXTCOLOR, new Class[]{BotSwitchCell.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_chat_botSwitchToInlineText));
            int i42 = Theme.key_windowBackgroundWhiteBlackText;
            arrayList.add(new ThemeDescription(this.mentionContainer.getListView(), ThemeDescription.FLAG_TEXTCOLOR, new Class[]{MentionCell.class}, new String[]{"nameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i42));
            arrayList.add(new ThemeDescription(this.mentionContainer.getListView(), ThemeDescription.FLAG_TEXTCOLOR, new Class[]{MentionCell.class}, new String[]{"usernameTextView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText3));
            arrayList.add(new ThemeDescription(this.mentionContainer.getListView(), 0, new Class[]{ContextLinkCell.class}, null, new Drawable[]{Theme.chat_inlineResultFile, Theme.chat_inlineResultAudio, Theme.chat_inlineResultLocation}, null, Theme.key_chat_inlineResultIcon));
            arrayList.add(new ThemeDescription(this.mentionContainer.getListView(), 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteGrayText2));
            arrayList.add(new ThemeDescription(this.mentionContainer.getListView(), 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_windowBackgroundWhiteLinkText));
            arrayList.add(new ThemeDescription(this.mentionContainer.getListView(), 0, new Class[]{ContextLinkCell.class}, null, null, null, i42));
            arrayList.add(new ThemeDescription(this.mentionContainer.getListView(), 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_chat_inAudioProgress));
            arrayList.add(new ThemeDescription(this.mentionContainer.getListView(), 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_chat_inAudioSelectedProgress));
            arrayList.add(new ThemeDescription(this.mentionContainer.getListView(), 0, new Class[]{ContextLinkCell.class}, null, null, null, Theme.key_divider));
        }
        HintView hintView = this.gifHintTextView;
        int i43 = ThemeDescription.FLAG_BACKGROUNDFILTER;
        int i44 = Theme.key_chat_gifSaveHintBackground;
        arrayList.add(new ThemeDescription(hintView, i43, null, null, null, null, i44));
        HintView hintView2 = this.gifHintTextView;
        int i45 = ThemeDescription.FLAG_TEXTCOLOR;
        int i46 = Theme.key_chat_gifSaveHintText;
        arrayList.add(new ThemeDescription(hintView2, i45, null, null, null, null, i46));
        arrayList.add(new ThemeDescription(this.noSoundHintView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HintView.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i46));
        arrayList.add(new ThemeDescription(this.noSoundHintView, ThemeDescription.FLAG_IMAGECOLOR, new Class[]{HintView.class}, new String[]{"imageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i46));
        arrayList.add(new ThemeDescription(this.noSoundHintView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HintView.class}, new String[]{"arrowImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i44));
        arrayList.add(new ThemeDescription(this.forwardHintView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HintView.class}, new String[]{"textView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i46));
        arrayList.add(new ThemeDescription(this.forwardHintView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{HintView.class}, new String[]{"arrowImageView"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, i44));
        arrayList.add(new ThemeDescription(this.floatingDateView, 0, null, null, null, null, i39));
        arrayList.add(new ThemeDescription(this.floatingDateView, 0, null, null, null, null, i41));
        arrayList.add(new ThemeDescription(this.infoTopView, 0, null, null, null, null, i39));
        arrayList.add(new ThemeDescription(this.infoTopView, 0, null, null, null, null, i41));
        int i47 = Theme.key_chat_attachIcon;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, i47));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_chat_attachGalleryBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, i47));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_chat_attachAudioBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, i47));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, i47));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_chat_attachContactBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_chat_attachContactText));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, i47));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_chat_attachLocationBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, i47));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_chat_attachPollBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, new Drawable[]{Theme.chat_attachEmptyDrawable}, null, Theme.key_chat_attachEmptyImage));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_chat_attachPhotoBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_dialogBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_dialogBackgroundGray));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_dialogTextGray2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_dialogScrollGlow));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_dialogGrayLine));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_dialogButtonSelector));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_windowBackgroundWhiteLinkSelection));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_windowBackgroundWhiteInputField));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_outTextSelectionHighlight));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_inTextSelectionHighlight));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_TextSelectionCursor));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayGreen1));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayGreen2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayBlue1));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayBlue2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelGreen1));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelGreen2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelBlue1));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelBlue2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_topPanelGray));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertGradientMuted));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertGradientMuted2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertGradientUnmuted));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertGradientUnmuted2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_mutedByAdminGradient));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_mutedByAdminGradient2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_mutedByAdminGradient3));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertMutedByAdmin));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_voipgroup_overlayAlertMutedByAdmin2));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_windowBackgroundGray));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_outReactionButtonBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_inReactionButtonBackground));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_inReactionButtonText));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, Theme.key_chat_outReactionButtonText));
        int i48 = Theme.key_chat_inReactionButtonTextSelected;
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, i48));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, null, i48));
        arrayList.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_chat_BlurAlpha));
        ChatActivityEnterView chatActivityEnterView16 = this.chatActivityEnterView;
        if (chatActivityEnterView16 != null && chatActivityEnterView16.botCommandsMenuContainer != null) {
            arrayList.add(new ThemeDescription(this.chatActivityEnterView.botCommandsMenuContainer.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{BotCommandsMenuView.BotCommandView.class}, new String[]{MediaTrack.ROLE_DESCRIPTION}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteBlackText));
            arrayList.add(new ThemeDescription(this.chatActivityEnterView.botCommandsMenuContainer.listView, ThemeDescription.FLAG_TEXTCOLOR, new Class[]{BotCommandsMenuView.BotCommandView.class}, new String[]{"command"}, (Paint[]) null, (Drawable[]) null, (ThemeDescription.ThemeDescriptionDelegate) null, Theme.key_windowBackgroundWhiteGrayText));
        }
        ChatActivityMemberRequestsDelegate chatActivityMemberRequestsDelegate = this.pendingRequestsDelegate;
        if (chatActivityMemberRequestsDelegate != null) {
            chatActivityMemberRequestsDelegate.fillThemeDescriptions(arrayList);
        }
        int size = arrayList.size();
        int i49 = i;
        while (i49 < size) {
            ThemeDescription themeDescription = arrayList.get(i49);
            i49++;
            themeDescription.resourcesProvider = this.themeDelegate;
        }
        return arrayList;
    }

    public void lambda$onCustomTransitionAnimation$443(ChatActivity chatActivity, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        chatActivity.setTransitionToChatProgress(fFloatValue);
        float f = 1.0f - fFloatValue;
        float fDp = AndroidUtilities.dp(8.0f) * f;
        this.avatarContainer.setTranslationY(fDp);
        this.avatarContainer.getAvatarImageView().setTranslationY(-fDp);
        float f2 = (-AndroidUtilities.dp(8.0f)) * fFloatValue;
        chatActivity.avatarContainer.setTranslationY(f2);
        chatActivity.avatarContainer.getAvatarImageView().setTranslationY(-f2);
        float f3 = (fFloatValue * 0.2f) + 0.8f;
        this.avatarContainer.getAvatarImageView().setScaleX(f3);
        this.avatarContainer.getAvatarImageView().setScaleY(f3);
        this.avatarContainer.getAvatarImageView().setAlpha(fFloatValue);
        float f4 = (0.2f * f) + 0.8f;
        chatActivity.avatarContainer.getAvatarImageView().setScaleX(f4);
        chatActivity.avatarContainer.getAvatarImageView().setScaleY(f4);
        chatActivity.avatarContainer.getAvatarImageView().setAlpha(f);
        ChatActivityTopPanelLayout chatActivityTopPanelLayout = chatActivity.topPanelLayout;
        if (chatActivityTopPanelLayout != null) {
            chatActivityTopPanelLayout.setAlpha(f);
        }
    }

    public class AnonymousClass137 extends AnimatorListenerAdapter {
        int index;
        final void lambda$setCurrentTheme$4() {
            this.animatingMessageDrawable.crossfadeFromDrawable = null;
            this.animatingMessageMediaDrawable.crossfadeFromDrawable = null;
            this.animatingColors = null;
            updateServiceMessageColor(1.0f);
        }

        private void setupChatTheme(EmojiThemes emojiThemes, TLRPC.WallPaper wallPaper, boolean z, boolean z2) {
            Theme.ThemeInfo theme;
            Theme.ThemeInfo theme2;
            if (ExteraConfig.getCustomThemes()) {
                ChatActivity chatActivity = this.activity.get();
                if (chatActivity == null || chatActivity.parentThemeDelegate == null) {
                    this.chatTheme = emojiThemes;
                    this.wallpaper = wallPaper;
                    Drawable backgroundImage = (chatActivity == null || chatActivity.fragmentView == null) ? null : chatActivity.contentView.getBackgroundImage();
                    final MotionBackgroundDrawable motionBackgroundDrawable = backgroundImage instanceof MotionBackgroundDrawable ? (MotionBackgroundDrawable) backgroundImage : null;
                    int phase = motionBackgroundDrawable != null ? motionBackgroundDrawable.getPhase() : 0;
                    if ((emojiThemes == null || emojiThemes.showAsDefaultStub) && wallPaper == null) {
                        this.currentColor = Theme.getServiceMessageColor();
                    }
                    String str = "Dark Blue";
                    String str2 = "Blue";
                    if (emojiThemes == null && wallPaper == null) {
                        this.currentColors = new SparseIntArray();
                        this.currentPaints.clear();
                        this.currentDrawables.clear();
                        Drawable cachedWallpaperNonBlocking = Theme.getCachedWallpaperNonBlocking();
                        if (cachedWallpaperNonBlocking instanceof MotionBackgroundDrawable) {
                            ((MotionBackgroundDrawable) cachedWallpaperNonBlocking).setPhase(phase);
                        }
                        this.backgroundDrawable = null;
                        if (Theme.getActiveTheme().isDark() == this.isDark) {
                            theme2 = Theme.getActiveTheme();
                        } else {
                            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
                            String string = sharedPreferences.getString("lastDayTheme", "Blue");
                            if (Theme.getTheme(string) != null && !Theme.getTheme(string).isDark()) {
                                str2 = string;
                            }
                            String string2 = sharedPreferences.getString("lastDarkTheme", "Dark Blue");
                            if (Theme.getTheme(string2) != null && Theme.getTheme(string2).isDark()) {
                                str = string2;
                            }
                            theme2 = this.isDark ? Theme.getTheme(str) : Theme.getTheme(str2);
                        }
                        Theme.applyTheme(theme2, false, this.isDark);
                        initServiceMessageColors(this.backgroundDrawable);
                        return;
                    }
                    if (ApplicationLoader.applicationContext != null) {
                        Theme.createChatResources(ApplicationLoader.applicationContext, false);
                    }
                    if (emojiThemes == null) {
                        this.currentColors = new SparseIntArray();
                    } else {
                        this.currentColors = emojiThemes.createColors(this.currentAccount, this.isDark ? 1 : 0);
                        pinMediaOverlayColors();
                    }
                    if (!TextUtils.isEmpty(ChatThemeController.getWallpaperEmoticon(this.wallpaper))) {
                        this.backgroundDrawable = PreviewView.getBackgroundDrawable(this.backgroundDrawable, this.currentAccount, this.wallpaper, this.isDark);
                    } else if (wallPaper != null) {
                        this.backgroundDrawable = ChatBackgroundDrawable.getOrCreate(this.backgroundDrawable, wallPaper, this.isDark);
                    } else {
                        this.backgroundDrawable = getBackgroundDrawableFromTheme(emojiThemes, phase);
                    }
                    AnimatorSet animatorSet = this.patternAlphaAnimator;
                    if (animatorSet != null) {
                        animatorSet.cancel();
                    }
                    if (z) {
                        this.patternAlphaAnimator = new AnimatorSet();
                        if (motionBackgroundDrawable != null) {
                            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(1.0f, 0.0f);
                            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatActivity$ThemeDelegate$$ExternalSyntheticLambda5
                                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    motionBackgroundDrawable.setPatternAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
                                }
                            });
                            valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatActivity.ThemeDelegate.1
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animator) {
                                    super.onAnimationEnd(animator);
                                    motionBackgroundDrawable.setPatternAlpha(1.0f);
                                }
                            });
                            valueAnimatorOfFloat.setDuration(200L);
                            this.patternAlphaAnimator.playTogether(valueAnimatorOfFloat);
                        }
                        Drawable drawable = this.backgroundDrawable;
                        if (drawable instanceof MotionBackgroundDrawable) {
                            final MotionBackgroundDrawable motionBackgroundDrawable2 = (MotionBackgroundDrawable) drawable;
                            motionBackgroundDrawable2.setPatternAlpha(0.0f);
                            ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
                            valueAnimatorOfFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatActivity$ThemeDelegate$$ExternalSyntheticLambda6
                                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    motionBackgroundDrawable2.setPatternAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
                                }
                            });
                            valueAnimatorOfFloat2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatActivity.ThemeDelegate.2
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animator) {
                                    super.onAnimationEnd(animator);
                                    motionBackgroundDrawable2.setPatternAlpha(1.0f);
                                }
                            });
                            valueAnimatorOfFloat2.setDuration(250L);
                            this.patternAlphaAnimator.playTogether(valueAnimatorOfFloat2);
                        }
                        this.patternAlphaAnimator.start();
                    }
                    if (emojiThemes == null && this.dialog_id >= 0) {
                        if (Theme.getActiveTheme().isDark() == this.isDark) {
                            theme = Theme.getActiveTheme();
                        } else {
                            SharedPreferences sharedPreferences2 = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
                            String string3 = sharedPreferences2.getString("lastDayTheme", "Blue");
                            if (Theme.getTheme(string3) != null && !Theme.getTheme(string3).isDark()) {
                                str2 = string3;
                            }
                            String string4 = sharedPreferences2.getString("lastDarkTheme", "Dark Blue");
                            if (Theme.getTheme(string4) != null && Theme.getTheme(string4).isDark()) {
                                str = string4;
                            }
                            theme = this.isDark ? Theme.getTheme(str) : Theme.getTheme(str2);
                        }
                        Theme.applyTheme(theme, false, this.isDark);
                    }
                    if (z2) {
                        this.currentColor = AndroidUtilities.calcDrawableColor(this.backgroundDrawable)[0];
                        initDrawables();
                        initPaints();
                        initServiceMessageColors(this.backgroundDrawable);
                        updateServiceMessageColor(1.0f);
                    }
                }
            }
        }

        private void initDrawables() {
            Drawable messageDrawable;
            int themeDrawableColorKey;
            for (Map.Entry<String, Drawable> entry : Theme.getThemeDrawablesMap().entrySet()) {
                String key = entry.getKey();
                key.getClass();
                switch (key) {
                    case "drawableMsgIn":
                        messageDrawable = new Theme.MessageDrawable(0, false, false, this);
                        break;
                    case "drawableMsgInMedia":
                        messageDrawable = new Theme.MessageDrawable(1, false, false, this);
                        break;
                    case "drawableMsgInMediaSelected":
                        messageDrawable = new Theme.MessageDrawable(1, false, true, this);
                        break;
                    case "drawableMsgOutMedia":
                        messageDrawable = new Theme.MessageDrawable(1, true, false, this);
                        break;
                    case "drawableMsgOutSelected":
                        messageDrawable = new Theme.MessageDrawable(0, true, true, this);
                        break;
                    case "drawableMsgOutMediaSelected":
                        messageDrawable = new Theme.MessageDrawable(1, true, true, this);
                        break;
                    case "drawableMsgInSelected":
                        messageDrawable = new Theme.MessageDrawable(0, false, true, this);
                        break;
                    case "drawableMsgOut":
                        messageDrawable = new Theme.MessageDrawable(0, true, false, this);
                        break;
                    default:
                        Drawable.ConstantState constantState = entry.getValue().getConstantState();
                        messageDrawable = constantState != null ? constantState.newDrawable().mutate() : null;
                        if (messageDrawable != null && (themeDrawableColorKey = Theme.getThemeDrawableColorKey(entry.getKey())) >= 0) {
                            Theme.setDrawableColor(messageDrawable, getColor(themeDrawableColorKey));
                            break;
                        }
                        break;
                }
                if (messageDrawable != null) {
                    this.currentDrawables.put(entry.getKey(), messageDrawable);
                }
            }
        }

        private void initPaints() {
            Paint paint;
            for (Map.Entry<String, Paint> entry : Theme.getThemePaintsMap().entrySet()) {
                Paint value = entry.getValue();
                if (value instanceof TextPaint) {
                    paint = new TextPaint();
                    paint.setTextSize(value.getTextSize());
                    paint.setTypeface(value.getTypeface());
                } else {
                    paint = new Paint();
                }
                if ((value.getFlags() & 1) != 0) {
                    paint.setFlags(1);
                }
                int themePaintColorKey = Theme.getThemePaintColorKey(entry.getKey());
                if (themePaintColorKey >= 0 && !"paintChatActionBackgroundDarken".equals(entry.getKey())) {
                    paint.setColor(getColor(themePaintColorKey));
                }
                this.currentPaints.put(entry.getKey(), paint);
            }
        }

        private void initServiceMessageColors(Drawable drawable) {
            float dimAmount;
            float f;
            int width;
            int height;
            Drawable drawable2 = drawable;
            ChatActivity chatActivity = this.activity.get();
            if (chatActivity == null || chatActivity.parentThemeDelegate == null) {
                int i = AndroidUtilities.calcDrawableColor(drawable2)[0];
                int currentColor = getCurrentColor(Theme.key_chat_serviceBackground);
                int i2 = Theme.key_chat_selectedBackground;
                int currentColor2 = getCurrentColor(i2);
                if (currentColor != 0 && this.wallpaper == null) {
                    i = currentColor;
                }
                this.currentServiceColor = i;
                if (drawable2 instanceof ChatBackgroundDrawable) {
                    ChatBackgroundDrawable chatBackgroundDrawable = (ChatBackgroundDrawable) drawable2;
                    dimAmount = chatBackgroundDrawable.getDimAmount();
                    drawable2 = chatBackgroundDrawable.getDrawable(false);
                } else {
                    dimAmount = 0.0f;
                }
                boolean z = drawable2 instanceof MotionBackgroundDrawable;
                this.drawServiceGradient = (z || (drawable2 instanceof BitmapDrawable)) && SharedConfig.getDevicePerformanceClass() != 0;
                boolean z2 = isGiftTheme() && this.isDark;
                boolean z3 = this.drawServiceGradient;
                this.drawSelectedGradient = z3;
                if (z3) {
                    if (drawable2 instanceof BitmapDrawable) {
                        Bitmap bitmap = ((BitmapDrawable) drawable2).getBitmap();
                        f = 0.0f;
                        if (bitmap.getWidth() > bitmap.getHeight()) {
                            height = (int) (bitmap.getHeight() * (40.0f / bitmap.getWidth()));
                            width = 40;
                        } else {
                            width = (int) (bitmap.getWidth() * (40.0f / bitmap.getHeight()));
                            height = 40;
                        }
                        this.serviceBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                        this.serviceCanvas = new Canvas(this.serviceBitmap);
                        this.src.set(0, 0, bitmap.getWidth(), bitmap.getHeight());
                        this.dst.set(0, 0, this.serviceBitmap.getWidth(), this.serviceBitmap.getHeight());
                        this.serviceCanvas.drawBitmap(bitmap, this.src, this.dst, (Paint) null);
                        Bitmap bitmap2 = this.serviceBitmap;
                        Utilities.blurBitmap(bitmap2, 3, 1, bitmap2.getWidth(), this.serviceBitmap.getHeight(), this.serviceBitmap.getRowBytes());
                        this.serviceCanvas.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) (dimAmount * 255.0f)));
                        Bitmap bitmap3 = this.serviceBitmap;
                        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                        this.serviceShader = new BitmapShader(bitmap3, tileMode, tileMode);
                        this.serviceBitmapSource = Bitmap.createBitmap(this.serviceBitmap);
                        this.serviceShaderSource = new BitmapShader(this.serviceBitmapSource, tileMode, tileMode);
                        if (Build.VERSION.SDK_INT >= 33) {
                            this.serviceShader.setFilterMode(2);
                            this.serviceShaderSource.setFilterMode(2);
                        }
                        this.useSourceShader = true;
                    } else {
                        f = 0.0f;
                        this.serviceBitmap = Bitmap.createBitmap(60, 80, Bitmap.Config.ARGB_8888);
                        Bitmap bitmap4 = ((MotionBackgroundDrawable) drawable2).getBitmap();
                        this.serviceBitmapSource = bitmap4;
                        if (z2) {
                            this.serviceBitmapSource = Bitmap.createBitmap(bitmap4);
                            new Canvas(this.serviceBitmapSource).drawColor(-870178270);
                        }
                        this.serviceCanvas = new Canvas(this.serviceBitmap);
                        this.src.set(0, 0, this.serviceBitmapSource.getWidth(), this.serviceBitmapSource.getHeight());
                        this.dst.set(0, 0, this.serviceBitmap.getWidth(), this.serviceBitmap.getHeight());
                        this.serviceCanvas.drawBitmap(this.serviceBitmapSource, this.src, this.dst, (Paint) null);
                        this.serviceCanvas.drawColor(ColorUtils.setAlphaComponent(-16777216, (int) (dimAmount * 255.0f)));
                        if (z2) {
                            this.serviceCanvas.drawColor(-870178270);
                        }
                        Bitmap bitmap5 = this.serviceBitmap;
                        Shader.TileMode tileMode2 = Shader.TileMode.CLAMP;
                        this.serviceShader = new BitmapShader(bitmap5, tileMode2, tileMode2);
                        this.serviceShaderSource = new BitmapShader(this.serviceBitmapSource, tileMode2, tileMode2);
                        if (Build.VERSION.SDK_INT >= 33) {
                            this.serviceShader.setFilterMode(2);
                            this.serviceShaderSource.setFilterMode(2);
                        }
                        this.useSourceShader = true;
                    }
                } else {
                    f = 0.0f;
                    this.serviceBitmap = null;
                    this.serviceShader = null;
                    this.serviceBitmapSource = null;
                    this.serviceCanvas = null;
                    this.useSourceShader = false;
                }
                Paint paint = getPaint("paintChatActionBackground");
                Paint paint2 = getPaint("paintChatActionBackgroundSelected");
                Paint paint3 = getPaint("paintChatMessageBackgroundSelected");
                if (paint != null) {
                    Paint paint4 = this.currentPaints.get("paintChatActionBackgroundDarken");
                    if (paint4 == null) {
                        HashMap<String, Paint> map = this.currentPaints;
                        Paint paint5 = new Paint(1);
                        map.put("paintChatActionBackgroundDarken", paint5);
                        paint5.setColor(0);
                        paint4 = paint5;
                    }
                    if (this.drawServiceGradient) {
                        ColorMatrix colorMatrix = new ColorMatrix();
                        if (z) {
                            if (((MotionBackgroundDrawable) drawable2).getIntensity() >= f) {
                                colorMatrix.setSaturation(1.6f);
                                AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, this.isDark ? 0.97f : 0.92f);
                                AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, this.isDark ? 0.12f : -0.06f);
                            } else {
                                colorMatrix.setSaturation(1.1f);
                                AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, this.isDark ? 0.4f : 0.8f);
                                AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, this.isDark ? 0.08f : -0.06f);
                            }
                        } else {
                            colorMatrix.setSaturation(1.6f);
                            AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, this.isDark ? 0.9f : 0.84f);
                            AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, this.isDark ? 0.04f : 0.06f);
                        }
                        paint.setAlpha(255);
                        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
                        paint.setShader(this.serviceShaderSource);
                        paint.setFilterBitmap(true);
                        paint2.setAlpha(255);
                        ColorMatrix colorMatrix2 = new ColorMatrix(colorMatrix);
                        AndroidUtilities.adjustSaturationColorMatrix(colorMatrix2, 0.26f);
                        AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix2, 0.92f);
                        paint2.setColorFilter(new ColorMatrixColorFilter(colorMatrix2));
                        paint2.setShader(this.serviceShaderSource);
                        paint2.setFilterBitmap(true);
                        paint4.setAlpha(0);
                    } else {
                        paint.setColorFilter(null);
                        paint.setShader(null);
                        paint2.setColorFilter(null);
                        paint2.setShader(null);
                        paint4.setAlpha(21);
                    }
                }
                if (paint3 == null) {
                    paint3 = new Paint(1);
                    this.currentPaints.put("paintChatMessageBackgroundSelected", paint3);
                }
                if (this.drawSelectedGradient) {
                    ColorMatrix colorMatrix3 = new ColorMatrix();
                    AndroidUtilities.adjustSaturationColorMatrix(colorMatrix3, 2.5f);
                    AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix3, 0.75f);
                    paint3.setAlpha(64);
                    paint3.setColorFilter(new ColorMatrixColorFilter(colorMatrix3));
                    paint3.setShader(this.serviceShaderSource);
                    paint3.setFilterBitmap(true);
                    return;
                }
                if (currentColor2 == 0) {
                    currentColor2 = getColor(i2);
                }
                paint3.setColor(currentColor2);
                paint3.setColorFilter(null);
                paint3.setShader(null);
            }
        }

        private void updateServiceMessageColor(float f) {
            Bitmap bitmap;
            Bitmap bitmap2;
            if (this.currentPaints.isEmpty()) {
                return;
            }
            Paint paint = getPaint("paintChatActionBackground");
            Paint paint2 = getPaint("paintChatActionBackgroundSelected");
            Paint paint3 = getPaint("paintChatMessageBackgroundSelected");
            int iBlendARGB = this.currentServiceColor;
            int currentColor = this.drawServiceGradient ? -1 : getCurrentColor(Theme.key_chat_serviceText, true);
            int currentColor2 = this.drawServiceGradient ? -1 : getCurrentColor(Theme.key_chat_serviceLink, true);
            int currentColor3 = this.drawServiceGradient ? -1 : getCurrentColor(Theme.key_chat_serviceLink, true);
            int currentColor4 = this.drawServiceGradient ? -1 : getCurrentColor(Theme.key_chat_serviceIcon, true);
            if (f != 1.0f) {
                iBlendARGB = ColorUtils.blendARGB(this.startServiceColor, iBlendARGB, f);
                currentColor = ColorUtils.blendARGB(this.startServiceTextColor, currentColor, f);
                currentColor2 = ColorUtils.blendARGB(this.startServiceLinkColor, currentColor2, f);
                currentColor3 = ColorUtils.blendARGB(this.startServiceButtonColor, currentColor3, f);
                currentColor4 = ColorUtils.blendARGB(this.startServiceIconColor, currentColor4, f);
            }
            if (paint != null && !this.drawServiceGradient) {
                paint.setColor(iBlendARGB);
                paint2.setColor(iBlendARGB);
            }
            this.currentColor = iBlendARGB;
            Paint paint4 = getPaint("paintChatActionText");
            if (paint4 != null) {
                ((TextPaint) paint4).linkColor = currentColor2;
                getPaint("paintChatActionText").setColor(currentColor);
                getPaint("paintChatBotButton").setColor(currentColor3);
            }
            Theme.setDrawableColor(getDrawable("drawableMsgStickerCheck"), currentColor);
            Theme.setDrawableColor(getDrawable("drawableMsgStickerClock"), currentColor);
            Theme.setDrawableColor(getDrawable("drawableMsgStickerHalfCheck"), currentColor);
            Theme.setDrawableColor(getDrawable("drawableMsgStickerPinned"), currentColor);
            Theme.setDrawableColor(getDrawable("drawableMsgStickerReplies"), currentColor);
            Theme.setDrawableColor(getDrawable("drawableMsgStickerViews"), currentColor);
            Theme.setDrawableColor(getDrawable("drawableBotInline"), currentColor4);
            Theme.setDrawableColor(getDrawable("drawableBotLink"), currentColor4);
            Theme.setDrawableColor(getDrawable("drawableBotLock"), currentColor4);
            Theme.setDrawableColor(getDrawable("drawable_botInvite"), currentColor4);
            Theme.setDrawableColor(getDrawable("drawableCommentSticker"), currentColor4);
            Theme.setDrawableColor(getDrawable("drawableGoIcon"), currentColor4);
            Theme.setDrawableColor(getDrawable("drawableReplyIcon"), currentColor4);
            Theme.setDrawableColor(getDrawable("drawableShareIcon"), currentColor4);
            if (this.serviceCanvas == null || (bitmap = this.serviceBitmapSource) == null) {
                return;
            }
            if (f != 1.0f && (bitmap2 = this.startServiceBitmap) != null) {
                this.useSourceShader = false;
                this.src.set(0, 0, bitmap2.getWidth(), this.startServiceBitmap.getHeight());
                this.dst.set(0, 0, this.serviceBitmap.getWidth(), this.serviceBitmap.getHeight());
                this.serviceCanvas.drawBitmap(this.startServiceBitmap, this.src, this.dst, (Paint) null);
                this.paint.setAlpha((int) (f * 255.0f));
                this.src.set(0, 0, this.serviceBitmapSource.getWidth(), this.serviceBitmapSource.getHeight());
                this.dst.set(0, 0, this.serviceBitmap.getWidth(), this.serviceBitmap.getHeight());
                this.serviceCanvas.drawBitmap(this.serviceBitmapSource, this.src, this.dst, this.paint);
                if (paint != null) {
                    paint.setShader(this.serviceShader);
                    paint2.setShader(this.serviceShader);
                }
                if (paint3 != null) {
                    paint3.setShader(this.serviceShader);
                    return;
                }
                return;
            }
            this.useSourceShader = true;
            this.src.set(0, 0, bitmap.getWidth(), this.serviceBitmapSource.getHeight());
            this.dst.set(0, 0, this.serviceBitmap.getWidth(), this.serviceBitmap.getHeight());
            this.serviceCanvas.drawBitmap(this.serviceBitmapSource, this.src, this.dst, (Paint) null);
            if (paint != null) {
                paint.setShader(this.serviceShaderSource);
                paint2.setShader(this.serviceShaderSource);
            }
            if (paint3 != null) {
                paint3.setShader(this.serviceShaderSource);
            }
        }

        private Drawable getBackgroundDrawableFromTheme(final EmojiThemes emojiThemes, int i) {
            if (emojiThemes.showAsDefaultStub) {
                Drawable drawable = Theme.createBackgroundDrawable(EmojiThemes.getDefaultThemeInfo(this.isDark), emojiThemes.getPreviewColors(this.currentAccount, this.isDark ? 1 : 0), emojiThemes.getWallpaperLink(this.isDark ? 1 : 0), i, false).wallpaper;
                return new ColorDrawable(-16777216);
            }
            int color = getColor(Theme.key_chat_wallpaper);
            int color2 = getColor(Theme.key_chat_wallpaper_gradient_to1);
            int color3 = getColor(Theme.key_chat_wallpaper_gradient_to2);
            int color4 = getColor(Theme.key_chat_wallpaper_gradient_to3);
            final MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable();
            motionBackgroundDrawable.setPatternBitmap(emojiThemes.getWallpaper(this.isDark ? 1 : 0).settings.intensity);
            motionBackgroundDrawable.setGiftDrawable(emojiThemes.getEmojiAnimatedSticker());
            motionBackgroundDrawable.setColors(color, color2, color3, color4, 0, true);
            motionBackgroundDrawable.setPhase(i);
            final int patternColor = motionBackgroundDrawable.getPatternColor();
            final boolean z = this.isDark;
            emojiThemes.loadWallpaperGiftPattern(z ? 1 : 0, new ResultCallback() { // from class: org.telegram.ui.ChatActivity$ThemeDelegate$$ExternalSyntheticLambda7
                @Override // org.telegram.tgnet.ResultCallback
                public final void onComplete(Object obj) {
                    this.f$0.lambda$getBackgroundDrawableFromTheme$7(motionBackgroundDrawable, (Pair) obj);
                }
            });
            boolean z2 = this.isDark;
            emojiThemes.loadWallpaper(z2 ? 1 : 0, new ResultCallback() { // from class: org.telegram.ui.ChatActivity$ThemeDelegate$$ExternalSyntheticLambda8
                @Override // org.telegram.tgnet.ResultCallback
                public final void onComplete(Object obj) {
                    this.f$0.lambda$getBackgroundDrawableFromTheme$9(emojiThemes, z, motionBackgroundDrawable, patternColor, (Pair) obj);
                }
            });
            return motionBackgroundDrawable;
        }

        void lambda$updateBotHelpCellClick$452(final BotHelpCell botHelpCell, final CharSequence charSequence, final String str) {
        final String toLanguage = TranslateAlert2.getToLanguage();
        if (str != null && ((!TextUtils.equals(TranslatorUtils.primaryLanguageOf(str), TranslatorUtils.primaryLanguageOf(toLanguage)) || str.equals(TranslateController.UNKNOWN_LANGUAGE)) && !TranslatorUtils.isRestrictedLanguage(str))) {
            botHelpCell.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda402
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$updateBotHelpCellClick$451(str, toLanguage, charSequence, botHelpCell, view);
                }
            });
        } else {
            botHelpCell.setClickable(false);
        }
    }

    public void lambda$getChatThanosEffect$454(ThanosEffect[] thanosEffectArr) {
        ThanosEffect thanosEffect;
        if (this.removingFromParent || (thanosEffect = thanosEffectArr[0]) == null) {
            return;
        }
        thanosEffectArr[0] = null;
        if (this.chatListThanosEffect == thanosEffect) {
            this.chatListThanosEffect = null;
        }
        AndroidUtilities.removeFromParent(thanosEffect);
    }

    public StarReactionsOverlay getStarReactionsOverlay() {
        if (this.starReactionsOverlay == null) {
            this.starReactionsOverlay = new StarReactionsOverlay(this);
        }
        FrameLayout layoutContainer = getLayoutContainer();
        if (layoutContainer == null) {
            return null;
        }
        ViewParent parent = this.starReactionsOverlay.getParent();
        StarReactionsOverlay starReactionsOverlay = this.starReactionsOverlay;
        if (parent != layoutContainer) {
            AndroidUtilities.removeFromParent(starReactionsOverlay);
            layoutContainer.addView(this.starReactionsOverlay, LayoutHelper.createFrame(-1, -1.0f));
        } else if (layoutContainer.indexOfChild(starReactionsOverlay) < layoutContainer.indexOfChild(this.fragmentView)) {
            this.starReactionsOverlay.bringToFront();
        }
        return this.starReactionsOverlay;
    }

    private void checkGroupMessagesOrder() {
        int i;
        if (!this.reversed) {
            return;
        }
        int i2 = 0;
        int i3 = -1;
        long j = 0;
        for (int i4 = 0; i4 < this.messages.size(); i4++) {
            long groupIdForUse = this.messages.get(i4).getGroupIdForUse();
            if (j != groupIdForUse) {
                if (i3 >= 0 && j != 0 && (i = i4 - i3) > 1) {
                    ArrayList arrayList = new ArrayList();
                    for (int i5 = 0; i5 < i; i5++) {
                        arrayList.add(this.messages.remove(i3));
                    }
                    Collections.sort(arrayList, new Comparator() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda228
                        @Override // java.util.Comparator
                        public final int compare(Object obj, Object obj2) {
                            return ChatActivity.m7541$r8$lambda$6cT7Xa7XbFDwHhkiKWi14HGJ8U((MessageObject) obj, (MessageObject) obj2);
                        }
                    });
                    this.messages.addAll(i3, arrayList);
                }
                i3 = i4;
                j = groupIdForUse;
            }
        }
        if (i3 < 0 || j == 0 || this.messages.size() - i3 <= 1) {
            return;
        }
        int size = this.messages.size() - i3;
        ArrayList arrayList2 = new ArrayList();
        while (true) {
            ArrayList<MessageObject> arrayList3 = this.messages;
            if (i2 < size) {
                arrayList2.add(arrayList3.remove(i3));
                i2++;
            } else {
                arrayList3.addAll(i3, arrayList2);
                return;
            }
        }
    }

    public static void lambda$showPremiumFloodWaitBulletin$456(boolean z) {
        presentFragment(new PremiumPreviewFragment(z ? "upload_speed" : "download_speed"));
    }

    public void didLongPressLink(final ChatMessageCell chatMessageCell, MessageObject messageObject, CharacterStyle characterStyle, final String str) {
        final MessageObject messageObject2;
        final CharacterStyle characterStyle2;
        int i;
        String strConcat;
        int i2;
        TLRPC.WebPage webPage;
        ItemOptions itemOptionsMakeOptions = ItemOptions.makeOptions((BaseFragment) this, (View) chatMessageCell, true);
        ScrimOptions scrimOptions = new ScrimOptions(getContext(), this.themeDelegate);
        itemOptionsMakeOptions.setOnDismiss(new ChatActivity$$ExternalSyntheticLambda128(scrimOptions));
        boolean z = (str.startsWith("video?") || Browser.isInternalUri(Uri.parse(str), null)) ? false : true;
        boolean zIsWebBrowserOpenInApp = getMessagesController().isWebBrowserOpenInApp(str);
        final boolean z2 = zIsWebBrowserOpenInApp && z;
        final boolean z3 = str.startsWith("#") || str.startsWith("$");
        final boolean zStartsWith = str.startsWith("mailto:");
        if (zStartsWith) {
            messageObject2 = messageObject;
            characterStyle2 = characterStyle;
        } else {
            int i3 = (!z2 || z3) ? R.drawable.msg_openin : R.drawable.menu_website;
            messageObject2 = messageObject;
            characterStyle2 = characterStyle;
            itemOptionsMakeOptions.add(i3, LocaleController.getString((!z2 || z3) ? R.string.Open : R.string.OpenInTelegramBrowser2), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda129
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didLongPressLink$457(str, characterStyle2, messageObject2, chatMessageCell, z2, z3);
                }
            });
        }
        if ((z2 && !z3) || zStartsWith) {
            itemOptionsMakeOptions.add(R.drawable.msg_openin, LocaleController.getString(R.string.OpenInSystemBrowser2), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda130
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didLongPressLink$459(str);
                }
            });
        } else if (!zStartsWith && !z3 && !z2 && z && !zIsWebBrowserOpenInApp) {
            itemOptionsMakeOptions.add(R.drawable.menu_website, LocaleController.getString(R.string.OpenInTelegramBrowser2), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda131
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didLongPressLink$461(str);
                }
            });
        }
        TLRPC.MessageMedia media = MessageObject.getMedia(messageObject2);
        if ((media instanceof TLRPC.TL_messageMediaWebPage) && (webPage = media.webpage) != null && webPage.cached_page != null && TextUtils.equals(webPage.url, str)) {
            itemOptionsMakeOptions.add(R.drawable.menu_instant_view, LocaleController.getString(R.string.OpenInstantView), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda132
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didLongPressLink$462(messageObject2);
                }
            });
        }
        int i4 = R.drawable.msg_copy;
        if (z3) {
            i = R.string.CopyHashtag;
        } else {
            i = zStartsWith ? R.string.CopyMail : R.string.CopyLink;
        }
        itemOptionsMakeOptions.add(i4, LocaleController.getString(i), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda133
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$didLongPressLink$463(str, messageObject2, zStartsWith);
            }
        });
        if (zIsWebBrowserOpenInApp && !z3 && !zStartsWith && !str.startsWith("tg:")) {
            itemOptionsMakeOptions.add(R.drawable.outline_saved_24, LocaleController.getString(R.string.WebBookmarkAdd), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda134
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didLongPressLink$464(str);
                }
            });
        }
        scrimOptions.setItemOptions(itemOptionsMakeOptions);
        if (str.startsWith("mailto:")) {
            SpannableString spannableString = new SpannableString(str.substring(7));
            spannableString.setSpan(characterStyle2, 0, spannableString.length(), 33);
            scrimOptions.setScrim(chatMessageCell, characterStyle2, spannableString);
        } else if (characterStyle2 instanceof URLSpanReplacement) {
            String url = ((URLSpanReplacement) characterStyle2).getURL();
            try {
                try {
                    Uri uri = Uri.parse(url);
                    url = Browser.replaceHostname(uri, Browser.IDN_toUnicode(uri.getHost()), null);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                strConcat = URLDecoder.decode(url.replaceAll("\\+", "%2b"), "UTF-8");
            } catch (Exception e2) {
                FileLog.e(e2);
                strConcat = url;
            }
            if (strConcat.length() > 204) {
                i2 = 0;
                strConcat = strConcat.substring(0, Opcodes.SUB_DOUBLE_2ADDR).concat("…");
            } else {
                i2 = 0;
            }
            SpannableString spannableString2 = new SpannableString(strConcat);
            spannableString2.setSpan(characterStyle2, i2, spannableString2.length(), 33);
            scrimOptions.setScrim(chatMessageCell, characterStyle2, spannableString2);
        } else {
            scrimOptions.setScrim(chatMessageCell, characterStyle2, null);
        }
        showDialog(scrimOptions);
    }

    public void lambda$didLongPressLink$458(String str, Boolean bool, Boolean bool2) {
        if (bool.booleanValue()) {
            if (bool2.booleanValue()) {
                getMessagesController().addWebBrowserException(str, true);
            }
            Browser.openInExternalBrowser(getParentActivity(), str, false);
        }
    }

    public void lambda$didLongPressLink$460(String str, Boolean bool, Boolean bool2) {
        if (bool.booleanValue()) {
            if (bool2.booleanValue()) {
                getMessagesController().addWebBrowserException(str, false);
            }
            Browser.openInTelegramBrowser(getParentActivity(), str, null);
        }
    }

    public public void lambda$didLongPressLink$464(String str) {
        ArticleViewer.addBookmark(str, this.currentAccount, this.contentView, null, this.themeDelegate);
    }

    public void didLongPressFormattedDate(final ChatMessageCell chatMessageCell, CharacterStyle characterStyle, String str, final TLRPC.TL_messageEntityFormattedDate tL_messageEntityFormattedDate) {
        final MessageObject messageObject;
        MessageObject.GroupedMessages groupedMessages;
        if (tL_messageEntityFormattedDate == null || (messageObject = chatMessageCell.getMessageObject()) == null) {
            return;
        }
        ArrayList<MessageObject> arrayList = (messageObject.getGroupId() == 0 || (groupedMessages = this.groupedMessagesMap.get(messageObject.getGroupId())) == null) ? null : groupedMessages.messages;
        if (arrayList == null) {
            arrayList = new ArrayList<>();
            arrayList.add(messageObject);
        }
        final ArrayList<MessageObject> arrayList2 = arrayList;
        final long clientUserId = getUserConfig().getClientUserId();
        final String entityFormattedDate = LocaleController.formatEntityFormattedDate(tL_messageEntityFormattedDate, true);
        final ItemOptions itemOptionsMakeOptions = ItemOptions.makeOptions((BaseFragment) this, (View) chatMessageCell, true);
        final ScrimOptions scrimOptions = new ScrimOptions(getContext(), this.themeDelegate);
        itemOptionsMakeOptions.setOnDismiss(new ChatActivity$$ExternalSyntheticLambda128(scrimOptions));
        itemOptionsMakeOptions.add(R.drawable.msg_copy, LocaleController.getString(R.string.RelativeDateMenuCopy), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda138
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$didLongPressFormattedDate$465(scrimOptions, entityFormattedDate);
            }
        });
        itemOptionsMakeOptions.add(R.drawable.msg_calendar2, LocaleController.getString(R.string.RelativeDateMenuAddToACalendar), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda139
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$didLongPressFormattedDate$466(itemOptionsMakeOptions, messageObject, chatMessageCell, entityFormattedDate, tL_messageEntityFormattedDate, scrimOptions);
            }
        });
        itemOptionsMakeOptions.add(R.drawable.msg_notifications, LocaleController.getString(R.string.RelativeDateMenuSetAReminder), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda140
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$didLongPressFormattedDate$469(itemOptionsMakeOptions, clientUserId, tL_messageEntityFormattedDate, arrayList2, scrimOptions);
            }
        });
        SpannableString spannableString = new SpannableString(entityFormattedDate);
        spannableString.setSpan(characterStyle, 0, spannableString.length(), 33);
        scrimOptions.setItemOptions(itemOptionsMakeOptions);
        scrimOptions.setScrim(chatMessageCell, characterStyle, spannableString);
        showDialog(scrimOptions);
    }

    public void lambda$didLongPressFormattedDate$469(ItemOptions itemOptions, final long j, TLRPC.TL_messageEntityFormattedDate tL_messageEntityFormattedDate, final ArrayList arrayList, final ScrimOptions scrimOptions) {
        itemOptions.dontDismiss();
        Context context = getContext();
        long j2 = tL_messageEntityFormattedDate.date;
        AlertsCreator.ScheduleDatePickerDelegate scheduleDatePickerDelegate = new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda311
            @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
            public final void didSelectDate(boolean z, int i, int i2) {
                this.f$0.lambda$didLongPressFormattedDate$468(arrayList, j, scrimOptions, z, i, i2);
            }
        };
        Objects.requireNonNull(scrimOptions);
        AlertsCreator.createScheduleDatePickerDialog(context, (String) null, j, j2, true, scheduleDatePickerDelegate, (Runnable) new ChatActivity$$ExternalSyntheticLambda269(scrimOptions));
    }

    public void lambda$didLongPressIpAddress$477(final Browser.Progress progress, final ChatMessageCell chatMessageCell, final String str, final CharacterStyle characterStyle, final IpAddressInfoController.IpAddressInfo ipAddressInfo) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda310
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$didLongPressIpAddress$476(progress, chatMessageCell, str, ipAddressInfo, characterStyle);
            }
        });
    }

    public void lambda$didLongPressUsername$483(Browser.Progress progress, ChatMessageCell chatMessageCell, final String str, CharacterStyle characterStyle, TLObject tLObject, Boolean bool) {
        final long j;
        boolean z;
        int i;
        progress.end();
        boolean zIsChannelAndNotMegaGroup = false;
        if (tLObject instanceof TLRPC.User) {
            j = ((TLRPC.User) tLObject).id;
            z = true;
        } else if (tLObject instanceof TLRPC.Chat) {
            TLRPC.Chat chat = (TLRPC.Chat) tLObject;
            j = -chat.id;
            zIsChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(chat);
            z = false;
        } else {
            j = 0;
            z = false;
        }
        ItemOptions itemOptionsMakeOptions = ItemOptions.makeOptions((BaseFragment) this, (View) chatMessageCell, true);
        final ScrimOptions scrimOptions = new ScrimOptions(getContext(), this.themeDelegate);
        itemOptionsMakeOptions.setOnDismiss(new ChatActivity$$ExternalSyntheticLambda128(scrimOptions));
        if (j != 0) {
            itemOptionsMakeOptions.add(zIsChannelAndNotMegaGroup ? R.drawable.msg_channel : R.drawable.msg_discussion, LocaleController.getString(zIsChannelAndNotMegaGroup ? R.string.ViewChannel : R.string.SendMessage), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda378
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didLongPressUsername$479(j);
                }
            });
        }
        itemOptionsMakeOptions.add(R.drawable.msg_copy, LocaleController.getString(R.string.ProfileCopyUsername), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda379
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$didLongPressUsername$480(scrimOptions, str);
            }
        });
        if (bool.booleanValue()) {
            itemOptionsMakeOptions.add(R.drawable.outline_gram_24, LocaleController.getString(R.string.BuyUsernameOnFragment), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda380
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didLongPressUsername$481(str);
                }
            });
        }
        itemOptionsMakeOptions.addGap();
        if (j != 0) {
            if (z) {
                i = R.string.ViewProfile;
            } else {
                i = zIsChannelAndNotMegaGroup ? R.string.ViewChannelProfile : R.string.ViewGroupProfile;
            }
            itemOptionsMakeOptions.addProfile(tLObject, LocaleController.getString(i), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda381
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didLongPressUsername$482(j);
                }
            });
        } else {
            itemOptionsMakeOptions.addText(LocaleController.getString(R.string.NoUsernameFound2), 13, AndroidUtilities.dp(200.0f));
        }
        scrimOptions.setItemOptions(itemOptionsMakeOptions);
        scrimOptions.setScrim(chatMessageCell, characterStyle, null);
        showDialog(scrimOptions);
    }

    public void lambda$didPressPhoneNumber$507(ChatMessageCell chatMessageCell, final String str, TLRPC.TL_contact tL_contact, CharacterStyle characterStyle, final TLRPC.User user) {
        final TLRPC.UserFull userFull = user != null ? getMessagesController().getUserFull(user.id) : null;
        final ItemOptions itemOptionsMakeOptions = ItemOptions.makeOptions((BaseFragment) this, (View) chatMessageCell, true);
        final ScrimOptions scrimOptions = new ScrimOptions(getContext(), this.themeDelegate);
        itemOptionsMakeOptions.setOnDismiss(new ChatActivity$$ExternalSyntheticLambda269(scrimOptions));
        final Utilities.Callback callback = new Utilities.Callback() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda275
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$didPressPhoneNumber$494(user, str, (Boolean) obj);
            }
        };
        final ItemOptions itemOptionsMakeSwipeback = itemOptionsMakeOptions.makeSwipeback();
        itemOptionsMakeSwipeback.add(R.drawable.ic_ab_back, LocaleController.getString(R.string.Back), new RatePill$$ExternalSyntheticLambda1(itemOptionsMakeOptions));
        itemOptionsMakeSwipeback.addGap();
        itemOptionsMakeSwipeback.add(R.drawable.msg_addbot, LocaleController.getString(R.string.CreateNewContact), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda276
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$didPressPhoneNumber$495(itemOptionsMakeOptions, str);
            }
        });
        itemOptionsMakeSwipeback.add(R.drawable.menu_contact_existing, LocaleController.getString(R.string.AddToExistingContact), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda277
            @Override // java.lang.Runnable
            public final void run() {
                callback.run(Boolean.FALSE);
            }
        });
        if (tL_contact == null && (user == null || !getContactsController().contactsDict.containsKey(Long.valueOf(user.id)))) {
            itemOptionsMakeOptions.add(R.drawable.msg_contact_add, LocaleController.getString(R.string.AddToContacts), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda278
                @Override // java.lang.Runnable
                public final void run() {
                    itemOptionsMakeOptions.openSwipeback(itemOptionsMakeSwipeback);
                }
            });
            itemOptionsMakeOptions.addGap();
        }
        if (user == null) {
            itemOptionsMakeOptions.add(R.drawable.menu_invit_telegram, LocaleController.getString(R.string.InviteToTelegramShort), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda279
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didPressPhoneNumber$498(str);
                }
            });
            itemOptionsMakeOptions.add(R.drawable.msg_calls_regular, LocaleController.getString(R.string.VoiceCallViaCarrier), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda280
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didPressPhoneNumber$499(str);
                }
            });
            itemOptionsMakeOptions.add(R.drawable.msg_copy, LocaleController.getString(R.string.CopyNumber), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda281
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didPressPhoneNumber$500(str);
                }
            });
            itemOptionsMakeOptions.addGap();
            itemOptionsMakeOptions.addText(LocaleController.getString(R.string.NumberNotOnTelegram), 13);
        } else {
            itemOptionsMakeOptions.add(R.drawable.msg_discussion, LocaleController.getString(R.string.SendMessage), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda282
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didPressPhoneNumber$501(user);
                }
            });
            if (!UserObject.isUserSelf(user)) {
                itemOptionsMakeOptions.add(R.drawable.msg_calls, LocaleController.getString(R.string.VoiceCallViaTelegram), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda270
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$didPressPhoneNumber$502(user, userFull);
                    }
                });
                itemOptionsMakeOptions.add(R.drawable.msg_videocall, LocaleController.getString(R.string.VideoCallViaTelegram), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda271
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$didPressPhoneNumber$503(user, userFull);
                    }
                });
            }
            itemOptionsMakeOptions.add(R.drawable.msg_calls_regular, LocaleController.getString(R.string.VoiceCallViaCarrier), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda272
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didPressPhoneNumber$504(str);
                }
            });
            itemOptionsMakeOptions.add(R.drawable.msg_copy, LocaleController.getString(R.string.CopyNumber), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda273
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didPressPhoneNumber$505(str);
                }
            });
            itemOptionsMakeOptions.addGap();
            itemOptionsMakeOptions.addProfile(user, LocaleController.getString(R.string.ViewProfile), new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda274
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$didPressPhoneNumber$506(scrimOptions, user);
                }
            });
        }
        scrimOptions.setItemOptions(itemOptionsMakeOptions);
        if (characterStyle instanceof URLSpanReplacement) {
            String url = ((URLSpanReplacement) characterStyle).getURL();
            if (url == null) {
                url = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            String strTrim = url.trim();
            if (strTrim.startsWith("tel:")) {
                strTrim = strTrim.substring(4);
            }
            if (strTrim.length() > 204) {
                strTrim = strTrim.substring(0, Opcodes.SUB_DOUBLE_2ADDR).concat("…");
            }
            SpannableString spannableString = new SpannableString(strTrim);
            spannableString.setSpan(characterStyle, 0, spannableString.length(), 33);
            scrimOptions.setScrim(chatMessageCell, characterStyle, spannableString);
        } else {
            scrimOptions.setScrim(chatMessageCell, characterStyle, null);
        }
        showDialog(scrimOptions);
    }

    public void m7674$r8$lambda$yiTT60iDp7zcW41TBEbcZVp00s(Browser.Progress progress, Utilities.Callback callback, TLRPC.User user) {
        progress.end();
        callback.run(user);
    }

    public void lambda$didPressReaction$514(TLRPC.ReactionCount reactionCount, View view) {
        closeMenu();
        SearchTagsList.openRenameTagAlert(getContext(), this.currentAccount, reactionCount.reaction, this.themeDelegate, false);
    }

    public void lambda$didPressReaction$521(boolean z, MessageObject messageObject, ReactedUsersListView reactedUsersListView, long j, TLRPC.MessagePeerReaction messagePeerReaction) {
        if (messagePeerReaction == null || messagePeerReaction.reaction == null || j == getUserConfig().getClientUserId() || !z) {
            return;
        }
        final ArrayList arrayList = new ArrayList(1);
        arrayList.add(messageObject);
        TLObject userOrChat = getMessagesController().getUserOrChat(j);
        final ArrayList arrayList2 = new ArrayList(1);
        arrayList2.add(userOrChat);
        final TLRPC.ChannelParticipant[] channelParticipantArr = new TLRPC.ChannelParticipant[1];
        TLRPC.TL_channels_getParticipant tL_channels_getParticipant = new TLRPC.TL_channels_getParticipant();
        tL_channels_getParticipant.channel = MessagesController.getInputChannel(this.currentChat);
        tL_channels_getParticipant.participant = MessagesController.getInputPeer(userOrChat);
        getConnectionsManager().sendRequestTyped(tL_channels_getParticipant, new AiTonesController$$ExternalSyntheticLambda0(), new Utilities.Callback2() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda499
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$didPressReaction$520(channelParticipantArr, arrayList, arrayList2, (TLRPC.TL_channels_channelParticipant) obj, (TLRPC.TL_error) obj2);
            }
        });
        closeMenu();
    }

    public void lambda$onSideControlButtonOnClick$523(Integer num) {
        if (num.intValue() == 0) {
            this.reactionsMentionCount = 0;
            updateReactionsMentionButton(true);
            getMessagesController().markReactionsAsRead(this.dialog_id, getTopicId());
        } else {
            updateReactionsMentionButton(true);
            scrollToMessageId(num.intValue(), 0, false, 0, true, 0);
        }
    }

    public void lambda$loadLastUnreadMention$525(int i) {
        if (i == 0) {
            this.hasAllMentionsLocal = false;
            loadLastUnreadMention();
        } else {
            scrollToMessageId(i, 0, false, 0, true, 0);
        }
    }

    public void lambda$onSideControlButtonOnLongClick$528() {
        for (int i = 0; i < this.messages.size(); i++) {
            MessageObject messageObject = this.messages.get(i);
            if (messageObject.messageOwner.mentioned && !messageObject.isContentUnread()) {
                messageObject.setContentIsRead();
            }
        }
        this.newMentionsCount = 0;
        getMessagesController().markMentionsAsRead(this.dialog_id, getTopicId());
        this.hasAllMentionsLocal = true;
        showMentionDownButton(false, true);
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
    }

    public void lambda$onSideControlButtonOnLongClick$530() {
        for (int i = 0; i < this.messages.size(); i++) {
            this.messages.get(i).markPollVotesAsRead();
        }
        this.pollVotesMentionCount = 0;
        updatePollVotesMentionButton(true);
        getMessagesController().markPollVotesAsRead(this.dialog_id, getTopicId());
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
    }

    private static View createMenuTextOption(Context context, Theme.ResourcesProvider resourcesProvider, CharSequence charSequence) {
        return createMenuTextOption(context, resourcesProvider, charSequence, 14);
    }

    private static View createMenuTextOption(Context context, Theme.ResourcesProvider resourcesProvider, CharSequence charSequence, int i) {
        FrameLayout frameLayout = new FrameLayout(context);
        frameLayout.setMinimumHeight(AndroidUtilities.dp(48.0f));
        frameLayout.setPadding(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(4.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(4.0f));
        TextView textView = new TextView(context) { // from class: org.telegram.ui.ChatActivity.142
            @Override // android.widget.TextView, android.view.View
            public void onMeasure(int i2, int i3) {
                if (View.MeasureSpec.getMode(i2) == Integer.MIN_VALUE && getLayout() != null) {
                    Layout layout = getLayout();
                    int iMax = 0;
                    for (int i4 = 0; i4 < layout.getLineCount(); i4++) {
                        iMax = Math.max(iMax, (int) Math.ceil(layout.getLineWidth(i4)));
                    }
                    i2 = View.MeasureSpec.makeMeasureSpec(getPaddingLeft() + iMax + getPaddingRight(), TLObject.FLAG_30);
                }
                super.onMeasure(i2, i3);
            }
        };
        textView.setMaxLines(3);
        textView.setGravity(3);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider));
        textView.setTextSize(1, i);
        textView.setMaxWidth(AndroidUtilities.dp(170.0f));
        textView.setText(charSequence);
        frameLayout.addView(textView, LayoutHelper.createFrame(-1, -2, (LocaleController.isRTL ? 5 : 3) | 16));
        return frameLayout;
    }

    public boolean isPeerNoForwards() {
        if (this.currentChat != null) {
            return getMessagesController().isChatNoForwards(this.currentChat);
        }
        return getMessagesController().isUserNoForwards(this.userInfo);
    }

    public void pollAddOptionModeStart(ChatMessageCell chatMessageCell) {
        int themedColor;
        if (chatMessageCell == null || this.chatActivityEnterView == null || isInPollAddOptionMode() || chatMessageCell.getMessageObject() == null) {
            return;
        }
        ChatActivityFragmentView chatActivityFragmentView = this.contentView;
        RectF rectF = AndroidUtilities.rectTmp;
        ViewPositionWatcher.computeRectInParent(chatMessageCell, chatActivityFragmentView, rectF);
        if (chatMessageCell.getPollAddButtonBounds(AndroidUtilities.rectTmp2)) {
            final int measuredHeight = (int) (rectF.bottom - (((this.contentView.getMeasuredHeight() - this.chatListView.getPaddingBottom()) + this.inputIslandHeightCurrent) - AndroidUtilities.dp(2.0f)));
            MessageObject messageObject = chatMessageCell.getMessageObject();
            if (this.pollAddOptionFieldLayout == null) {
                this.pollAddOptionFieldLayout = new PollAddOptionFieldLayout(this, getContext(), this.resourceProvider);
                int iIndexOfChild = this.contentView.indexOfChild(this.chatListView);
                ChatActivityFragmentView chatActivityFragmentView2 = this.contentView;
                if (iIndexOfChild >= 0) {
                    chatActivityFragmentView2.addView(this.pollAddOptionFieldLayout, iIndexOfChild + 1, LayoutHelper.createFrameMatchParent());
                } else {
                    chatActivityFragmentView2.addView(this.pollAddOptionFieldLayout, LayoutHelper.createFrameMatchParent());
                }
            }
            if (messageObject.isOutOwner()) {
                themedColor = getThemedColor(chatMessageCell.isDrawSelectionBackground() ? Theme.key_chat_outTimeSelectedText : Theme.key_chat_outTimeText);
            } else {
                themedColor = getThemedColor(chatMessageCell.isDrawSelectionBackground() ? Theme.key_chat_inTimeSelectedText : Theme.key_chat_inTimeText);
            }
            this.pollAddOptionFieldLayout.setColor(themedColor);
            this.pollAddOptionFieldLayout.setCellToWatch(chatMessageCell);
            this.pollAddOptionFieldLayout.setEmojiKeyboardVisible(false, false);
            this.pollAddOptionFieldLayout.setAnimatedVisibility(this.animatorPollAddAnswerVisibility.getFloatValue());
            this.pollAddOptionFieldLayout.doOnEmojiClick(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda509
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.pollAddOptionModeToggleEmoji();
                }
            });
            this.pollAddOptionFieldLayout.doOnCancel(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda510
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.pollAddOptionModeClose();
                }
            });
            final EditTextBoldCursor editTextBoldCursor = this.pollAddOptionFieldLayout.textView;
            editTextBoldCursor.setOnKeyListener(new View.OnKeyListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda511
                @Override // android.view.View.OnKeyListener
                public final boolean onKey(View view, int i, KeyEvent keyEvent) {
                    return this.f$0.lambda$pollAddOptionModeStart$531(view, i, keyEvent);
                }
            });
            editTextBoldCursor.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda512
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                    return this.f$0.lambda$pollAddOptionModeStart$532(textView, i, keyEvent);
                }
            });
            this.chatActivityEnterView.overrideEditTextView(editTextBoldCursor);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda513
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$pollAddOptionModeStart$534(measuredHeight, editTextBoldCursor);
                }
            }, 100L);
            this.animatorPollAddAnswerVisibility.setValue(true, true);
        }
    }

    public /* synthetic */ boolean lambda$pollAddOptionModeStart$531(View view, int i, KeyEvent keyEvent) {
        EditTextBoldCursor editTextBoldCursor = (EditTextBoldCursor) view;
        if (i != 67 || keyEvent.getAction() != 0 || editTextBoldCursor.length() != 0) {
            return false;
        }
        pollAddOptionModeClose();
        return true;
    }

    public /* synthetic */ boolean lambda$pollAddOptionModeStart$532(TextView textView, int i, KeyEvent keyEvent) {
        PollAddOptionFieldLayout pollAddOptionFieldLayout;
        ChatMessageCell chatMessageCell;
        if (i != 6 || (pollAddOptionFieldLayout = this.pollAddOptionFieldLayout) == null || (chatMessageCell = pollAddOptionFieldLayout.cellToWatch) == null) {
            return false;
        }
        pollAddOptionModeComplete(chatMessageCell);
        return true;
    }

    public /* synthetic */ void lambda$pollAddOptionModeStart$534(int i, final EditTextBoldCursor editTextBoldCursor) {
        this.chatListView.smoothScrollBy(0, i);
        if (!AndroidUtilities.showKeyboard(editTextBoldCursor)) {
            editTextBoldCursor.clearFocus();
            editTextBoldCursor.requestFocus();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChatActivity$$ExternalSyntheticLambda532
            @Override // java.lang.Runnable
            public final void run() {
                AndroidUtilities.showKeyboard(editTextBoldCursor);
            }
        }, 100L);
    }

    public void pollAddOptionModeToggleEmoji() {
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView == null || this.pollAddOptionFieldLayout == null) {
            return;
        }
        boolean zIsPopupShowing = chatActivityEnterView.isPopupShowing();
        ChatActivityEnterView chatActivityEnterView2 = this.chatActivityEnterView;
        if (zIsPopupShowing) {
            chatActivityEnterView2.hidePopup(false);
            AndroidUtilities.showKeyboard(this.pollAddOptionFieldLayout.textView);
            this.pollAddOptionFieldLayout.setEmojiKeyboardVisible(false, true);
        } else {
            chatActivityEnterView2.setAllowStickersAndGifs(true, false, false);
            this.chatActivityEnterView.showEmojiView();
            this.pollAddOptionFieldLayout.setEmojiKeyboardVisible(true, true);
        }
    }

    public void pollAddOptionModeComplete(ChatMessageCell chatMessageCell) {
        PollAddOptionFieldLayout pollAddOptionFieldLayout;
        if (isInPollAddOptionMode() && (pollAddOptionFieldLayout = this.pollAddOptionFieldLayout) != null && pollAddOptionFieldLayout.cellToWatch == chatMessageCell) {
            Editable text = pollAddOptionFieldLayout.textView.getText();
            int length = text.length();
            if (length == 0 || length > getMessagesController().config.pollAnswerLengthMax.get()) {
                AndroidUtilities.shakeView(this.pollAddOptionFieldLayout.textView);
            } else {
                SendMessagesHelper.getInstance(this.currentAccount).addPollOption(chatMessageCell.getMessageObject(), text, this.pollAddOptionFieldLayout.getAttachedMedia());
                pollAddOptionModeClose();
            }
        }
    }

    public void pollAddOptionModeClose() {
        this.animatorPollAddAnswerVisibility.setValue(false, true);
        PollAddOptionFieldLayout pollAddOptionFieldLayout = this.pollAddOptionFieldLayout;
        if (pollAddOptionFieldLayout != null) {
            pollAddOptionFieldLayout.updateCell();
        }
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.setAllowStickersAndGifs(true, true, true, false);
            this.chatActivityEnterView.overrideEditTextView(null);
        }
    }

    private void pollAddOptionModeDestroy() {
        PollAddOptionFieldLayout pollAddOptionFieldLayout = this.pollAddOptionFieldLayout;
        if (pollAddOptionFieldLayout != null) {
            this.contentView.removeView(pollAddOptionFieldLayout);
            this.pollAddOptionFieldLayout = null;
        }
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.openKeyboard();
        }
    }

    public boolean isInPollAddOptionMode() {
        return this.animatorPollAddAnswerVisibility.getValue();
    }

    public boolean isBottomButtonHidden() {
        if (isFeedSearch()) {
            return true;
        }
        if (ExteraConfig.getBottomButton() == 0 && this.chatMode == 0 && !isReport() && ChatObject.isChannel(this.currentChat)) {
            TLRPC.Chat chat = this.currentChat;
            if (!chat.megagroup && !ChatObject.isNotInChat(chat) && !ChatObject.canWriteToChat(this.currentChat)) {
                return !isThreadChat();
            }
        }
        return false;
    }

    private boolean shouldCollapseInputIsland(boolean z) {
        if (isBottomButtonHidden()) {
            return (((this.bottomViewsVisibilityController.getVisibility(5) > 0.0f ? 1 : (this.bottomViewsVisibilityController.getVisibility(5) == 0.0f ? 0 : -1)) > 0 || (z && this.bottomViewsVisibilityController.getCurrentPriorityContainerId() == 5)) || ((this.bottomViewsVisibilityController.getVisibility(4) > 0.0f ? 1 : (this.bottomViewsVisibilityController.getVisibility(4) == 0.0f ? 0 : -1)) > 0 || (z && this.bottomViewsVisibilityController.getCurrentPriorityContainerId() == 4))) ? false : true;
        }
        return false;
    }

    private boolean shouldHideInputIslandBackground() {
        return isBottomButtonHidden() && this.bottomViewsVisibilityController.getVisibility(4) <= 0.0f;
    }

    public void startFireworks() {
        FireworksOverlay fireworksOverlay = this.fireworksOverlay;
        if (fireworksOverlay == null || fireworksOverlay.isStarted()) {
            return;
        }
        this.fireworksOverlay.start();
        try {
            this.fireworksOverlay.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
    }

    public float getTopicTabsSideSize(TopicsTabsView.Position position) {
        TopicsTabsView topicsTabsView = this.topicsTabs;
        if (topicsTabsView != null) {
            return topicsTabsView.getTabsVisibleSpaceWithPadding(position, AndroidUtilities.dp(7.0f));
        }
        return 0.0f;
    }

    public void invalidateMergedVisibleBlurredPositionsAndSourcesPositions() {
        invalidateMergedVisibleBlurredPositionsAndSources(2);
    }

    public void invalidateMergedVisibleBlurredPositionsAndSources(int i) {
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity != null) {
            chatActivity.invalidateMergedVisibleBlurredPositionsAndSources(i);
        }
        if (Build.VERSION.SDK_INT < 31 || this.scrollableViewNoiseSuppressor == null) {
            return;
        }
        this.invalidateBlurredSourcesView.invalidate(i);
    }

    public void invalidateMergedVisibleBlurredPositionsAndSourcesImpl(int i) {
        if (Build.VERSION.SDK_INT < 31 || this.scrollableViewNoiseSuppressor == null) {
            return;
        }
        if (BitwiseUtils.hasFlag(i, 4)) {
            invalidateClipRectForBackgroundAndChatList();
        }
        if (BitwiseUtils.hasFlag(i, 2)) {
            int mergedVisibleBlurredPositions = getMergedVisibleBlurredPositions(this.glassDrawablesPositionsMerged);
            this.glassDrawablesPositionsCount = mergedVisibleBlurredPositions;
            this.scrollableViewNoiseSuppressor.setupRenderNodes(this.glassDrawablesPositionsMerged, mergedVisibleBlurredPositions);
        }
        DownscaleScrollableNoiseSuppressor downscaleScrollableNoiseSuppressor = this.scrollableViewNoiseSuppressor;
        ChatActivityFragmentView chatActivityFragmentView = this.contentView;
        Objects.requireNonNull(chatActivityFragmentView);
        if (downscaleScrollableNoiseSuppressor.invalidateResultRenderNodes(new ChatActivity$$ExternalSyntheticLambda231(chatActivityFragmentView), this.contentView.getWidth(), this.contentView.getHeight())) {
            BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode = this.glassBackgroundSourceRenderNode;
            if (blurredBackgroundSourceRenderNode != null) {
                blurredBackgroundSourceRenderNode.invalidateDisplayListForDrawables();
            }
            BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode2 = this.glassBackgroundSourceFrostedRenderNode;
            if (blurredBackgroundSourceRenderNode2 != null) {
                blurredBackgroundSourceRenderNode2.invalidateDisplayListForDrawables();
            }
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.invalidate();
            }
            invalidateAllGlassAttachedViews();
            Runnable runnable = this.glassSourceInvalidationCallback;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    private int getMergedVisibleBlurredPositions(List<RectF> list) {
        RectF rectF;
        int visibleBlurredPositions = getVisibleBlurredPositions(this.glassDrawablesPositions);
        if (this.hasMainTabs && BottomNavigationBar.visible() && this.contentView.getMeasuredWidth() > 0 && this.contentView.getMeasuredHeight() > 0) {
            MainTabsUiHelper.setBlurBounds(this.feedTabBlurPosition, this.contentView, AndroidUtilities.navigationBarHeight);
            this.feedTabBlurPosition.inset(0.0f, LiteMode.isEnabled(262144) ? 0.0f : -AndroidUtilities.dp(48.0f));
            int size = this.glassDrawablesPositions.size();
            ArrayList<RectF> arrayList = this.glassDrawablesPositions;
            if (visibleBlurredPositions < size) {
                rectF = arrayList.get(visibleBlurredPositions);
            } else {
                rectF = new RectF();
                arrayList.add(rectF);
            }
            rectF.set(this.feedTabBlurPosition);
            visibleBlurredPositions++;
        }
        int iMergeOverlapping = RectFMergeBounding.mergeOverlapping(this.glassDrawablesPositions, visibleBlurredPositions, list);
        int measuredWidth = this.contentView.getMeasuredWidth();
        for (int i = 0; i < iMergeOverlapping; i++) {
            RectF rectF2 = list.get(i);
            float f = measuredWidth;
            rectF2.left = MathUtils.clamp(rectF2.left, 0.0f, f);
            rectF2.top = Math.max(this.chatListView.getY(), rectF2.top);
            rectF2.right = MathUtils.clamp(rectF2.right, 0.0f, f);
            rectF2.bottom = Math.min(this.chatListView.getY() + this.chatListView.getMeasuredHeight(), rectF2.bottom);
        }
        return iMergeOverlapping;
    }

    private int getVisibleBlurredPositions(List<RectF> list) {
        RectF rectF;
        int visiblePositions = 0;
        if (Build.VERSION.SDK_INT < 29) {
            return 0;
        }
        if (this.glassBackgroundSourceFrostedRenderNode != null) {
            if (list.isEmpty()) {
                rectF = new RectF();
                list.add(rectF);
            } else {
                rectF = list.get(0);
            }
            rectF.set(0.0f, 0.0f, this.contentView.getMeasuredWidth(), this.chatListView.getPaddingTop() + this.chatListView.getY());
            rectF.inset(0.0f, -AndroidUtilities.dp(45.0f));
            visiblePositions = 1 + this.glassBackgroundSourceFrostedRenderNode.getVisiblePositions(list, 1, AndroidUtilities.dp(48.0f));
        }
        BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode = this.glassBackgroundSourceRenderNode;
        return blurredBackgroundSourceRenderNode != null ? visiblePositions + blurredBackgroundSourceRenderNode.getVisiblePositions(list, visiblePositions, AndroidUtilities.dp(8.0f)) : visiblePositions;
    }

    public void invalidateClipRectForBackgroundAndChatList() {
        if (this.contentView == null) {
            return;
        }
        int iMax = (int) Math.max(0.0f, (this.windowInsetsStateHolder.getAnimatedImeBottomInset() * this.windowInsetsStateHolder.getAnimatedKeyboardVisibility()) - AndroidUtilities.dp(29.0f));
        ChatActivityFragmentView chatActivityFragmentView = this.contentView;
        if (chatActivityFragmentView.backgroundView != null) {
            this.clipBoundsTmp.set(0, 0, chatActivityFragmentView.getMeasuredWidth(), this.contentView.getMeasuredHeight() - iMax);
            this.contentView.backgroundView.setClipBounds(this.clipBoundsTmp);
        }
        if (this.chatListView != null) {
            this.clipBoundsTmp.set(0, 0, this.contentView.getMeasuredWidth(), this.contentView.getMeasuredHeight() - iMax);
            this.clipBoundsTmp.offset(0, -this.chatListView.getTop());
            ChatListRecyclerView chatListRecyclerView = this.chatListView;
            chatListRecyclerView.setClipBounds(chatListRecyclerView.hasActiveEdgeEffects() ? null : this.clipBoundsTmp);
        }
        if (this.chatActivityFadeView != null) {
            this.clipBoundsTmp.set(0, 0, this.contentView.getMeasuredWidth(), this.contentView.getMeasuredHeight() - ((int) Math.max(0.0f, Math.min(this.windowInsetsStateHolder.getInAppKeyboardHeight(), this.windowInsetsStateHolder.getAnimatedImeBottomInset() * this.windowInsetsStateHolder.getAnimatedKeyboardVisibility()) - AndroidUtilities.dp(29.0f))));
            this.chatActivityFadeView.setClipBounds(this.clipBoundsTmp);
        }
    }

    public void sendDebugRichMessage() {
        TLRPC.WebPage webPage = ArticleViewer.debugCopiedRichMessageWebPage;
        if (webPage == null || webPage.cached_page == null) {
            BulletinFactory.of(this).createErrorBulletin("No rich message copied").show();
        } else {
            SendMessagesHelper.prepareSendingArticle(getAccountInstance(), new ArrayList(webPage.cached_page.blocks), webPage.cached_page.rtl, this.dialog_id, this.replyingMessageObject, getThreadMessage(), true, 0, 0, null, 0, 0L, getSendMonoForumPeerId(), 0L);
        }
    }

    public abstract class ChatListRecyclerView extends RecyclerListViewInternal {
        public abstract void drawChatBackgroundElements(Canvas canvas, RectF rectF);

        public abstract void drawChatForegroundElements(Canvas canvas, RectF rectF);

        public ChatListRecyclerView(Context context, ThemeDelegate themeDelegate) {
            super(context, themeDelegate);
        }

        public void drawChatBackgroundElements(Canvas canvas) {
            drawChatBackgroundElements(canvas, null);
        }

        public void drawChatForegroundElements(Canvas canvas) {
            drawChatForegroundElements(canvas, null);
        }
    }
}
