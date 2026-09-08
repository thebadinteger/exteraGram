package org.telegram.ui.Cells;

import android.R;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.URLSpan;
import android.util.Pair;
import android.util.Property;
import android.util.SparseArray;
import android.util.StateSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewStructure;
import android.view.Window;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityNodeProvider;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import com.chaquo.python.internal.Common;
import com.exteragram.messenger.DividerStyle;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.badges.BadgesController;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.speech.VoiceRecognitionController;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.google.android.gms.cast.MediaError;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicReference;
import kotlin.jvm.internal.LongCompanionObject;
import kotlin.time.DurationKt;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import me.vkryl.core.BitwiseUtils;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AppGlobalConfig;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BotForumHelper;
import org.telegram.messenger.BotInlineKeyboard;
import org.telegram.messenger.ChatMessageSharedResources;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.CodeHighlighting;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FlagSecureReason;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.RichMessageLayout;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.TranslateController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.utils.Choreographer60FpsContent;
import org.telegram.messenger.utils.CountdownTimer;
import org.telegram.messenger.utils.DrawableUtils;
import org.telegram.messenger.utils.tlutils.TlUtils;
import org.telegram.messenger.video.OldVideoPlayerRewinder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_iv;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.AvatarSpan;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedNumberLayout;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AudioVisualizerDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.AvatarsDrawable;
import org.telegram.ui.Components.AvatarsListDrawable;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmptyStubSpan;
import org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.ForegroundColorSpanThemable;
import org.telegram.ui.Components.FormattedDateSpan;
import org.telegram.ui.Components.ForwardBackground;
import org.telegram.ui.Components.InfiniteProgress;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.LoadingDrawable;
import org.telegram.ui.Components.MediaActionDrawable;
import org.telegram.ui.Components.MessageBackgroundDrawable;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.MsgClockDrawable;
import org.telegram.ui.Components.PostRunnableHolder;
import org.telegram.ui.Components.Premium.boosts.BoostCounterSpan;
import org.telegram.ui.Components.Premium.boosts.cells.msg.GiveawayMessageCell;
import org.telegram.ui.Components.Premium.boosts.cells.msg.GiveawayResultsMessageCell;
import org.telegram.ui.Components.QuoteHighlight;
import org.telegram.ui.Components.QuoteSpan;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ReplyMessageLine;
import org.telegram.ui.Components.RoundVideoPlayingDrawable;
import org.telegram.ui.Components.SeekBar;
import org.telegram.ui.Components.SeekBarAccessibilityDelegate;
import org.telegram.ui.Components.SeekBarWaveform;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.SlotsDrawable;
import org.telegram.ui.Components.StaticLayoutEx;
import org.telegram.ui.Components.StickerSetLinkIcon;
import org.telegram.ui.Components.SuggestionOffer;
import org.telegram.ui.Components.SummaryIcon;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TimerParticles;
import org.telegram.ui.Components.TopicSeparator;
import org.telegram.ui.Components.TranscribeButton;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanBotCommand;
import org.telegram.ui.Components.URLSpanBrowser;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.chat.ChatActivityDraftMessageMeasureController;
import org.telegram.ui.Components.poll.PollAttachedMediaPack;
import org.telegram.ui.Components.poll.PollContentDrawable;
import org.telegram.ui.Components.poll.PollUtils;
import org.telegram.ui.Components.poll.buttons.PollAddButtonDrawable;
import org.telegram.ui.Components.poll.buttons.PollButtonDrawable;
import org.telegram.ui.Components.poll.buttons.PollInstantButtonDrawable;
import org.telegram.ui.Components.spoilers.SpoilerEffect;
import org.telegram.ui.Components.spoilers.SpoilerEffect2;
import org.telegram.ui.GradientClip;
import org.telegram.ui.MultiLayoutTypingAnimator;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PinchToZoomHelper;
import org.telegram.ui.SecretMediaViewer;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stars.StarsReactionsSheet;
import org.telegram.ui.Stories.StoriesUtilities;
import org.telegram.ui.Stories.StoryViewer;
import org.telegram.ui.Stories.recorder.CaptionContainerView;

public class ChatMessageCell extends BaseCell implements SeekBar.SeekBarDelegate, ImageReceiver.ImageReceiverDelegate, DownloadController.FileDownloadProgressListener, TextSelectionHelper.SelectableView, NotificationCenter.NotificationCenterDelegate, FactorAnimator.Target, IMessageCell {
    private static Paint avatarOnlineClearPaint;
    private static float[] radii = new float[8];
    private final boolean ALPHA_PROPERTY_WORKAROUND;
    public Property<ChatMessageCell, Float> ANIMATION_OFFSET_X;
    private int TAG;
    CharSequence accessibilityText;
    private boolean accessibilityTextContentUnread;
    private long accessibilityTextFileSize;
    private boolean accessibilityTextUnread;
    private SparseArray<Rect> accessibilityVirtualViewBounds;
    private float actionAlpha;
    private int[] adaptiveEmojiColor;
    private ColorFilter[] adaptiveEmojiColorFilter;
    private int addedCaptionHeight;
    private boolean addedForTest;
    private int additionalPaddingHeight;
    private int additionalTimeOffsetY;
    private StaticLayout adminLayout;
    private ButtonBounce adminLayoutBounce;
    private boolean adminLayoutIsAdmin;
    private boolean adminLayoutIsOwner;
    private RectF adminLayoutRect;
    private boolean allowAssistant;
    private float alphaInternal;
    private int animateFromStatusDrawableParams;
    private boolean animatePollAnswer;
    private boolean animatePollAnswerAlpha;
    private boolean animatePollAvatars;
    private int animateToStatusDrawableParams;
    public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiDescriptionStack;
    public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiPollExplanation;
    public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiPollQuestion;
    public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiReplyStack;
    public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmojiStack;
    private AnimatedTextView.AnimatedTextDrawable animatedInfoLayout;
    private AnimatedTextView.AnimatedTextDrawable animatedInfoLayout2;
    private int animatingDrawVideoImageButton;
    private float animatingDrawVideoImageButtonProgress;
    private float animatingLoadingProgressProgress;
    private int animatingNoSound;
    private boolean animatingNoSoundPlaying;
    private float animatingNoSoundProgress;
    private float animationOffsetX;
    private boolean animationRunning;
    public int askBotForumBottomPadding;
    private BotAskCellDrawable askBotForumBubble;
    private TopicSeparator askBotForumSeparator;
    private boolean attachedToWindow;
    private StaticLayout authorLayout;
    private int authorLayoutLeft;
    private int authorLayoutWidth;
    private boolean autoPlayingMedia;
    private int availableTimeWidth;
    protected AvatarDrawable avatarDrawable;
    private ImageReceiver avatarImage;
    private boolean avatarOnlineLastOnline;
    private float avatarOnlineProgress;
    private long avatarOnlineUserId;
    private boolean avatarPressed;
    private Theme.MessageDrawable.PathDrawParams backgroundCacheParams;
    private MessageBackgroundDrawable backgroundDrawable;
    private int backgroundDrawableBottom;
    private int backgroundDrawableLeft;
    private int backgroundDrawableRight;
    private int backgroundDrawableTop;
    public int backgroundHeight;
    public int backgroundWidth;
    private ImageReceiver blurredPhotoImage;
    public int blurredViewBottomOffset;
    public int blurredViewTopOffset;
    private RectF boostCounterBounds;
    private Drawable boostCounterLayoutSelector;
    private boolean boostCounterPressed;
    private int boostCounterSelectorColor;
    private BoostCounterSpan boostCounterSpan;
    private final AnimatedEmojiSpan.InvalidateHolder botButtonEmojiInvalidateHolder;
    private Path botButtonPath;
    private float[] botButtonRadii;
    private ArrayList<BotButton> botButtons;
    private HashMap<String, BotButton> botButtonsByData;
    private HashMap<String, BotButton> botButtonsByPosition;
    private String botButtonsLayout;
    public MultiLayoutTypingAnimator botDraftTypingAnimator;
    public int bottomActionPadding;
    private Text bottomActionText;
    private LinkPath bottomActionTextPath;
    private CornerPathEffect bottomActionTextPathEffect;
    private boolean bottomNearToSet;
    private int buttonPressed;
    private int buttonState;
    private int buttonX;
    private int buttonY;
    private final boolean canDrawBackgroundInParent;
    private boolean canStreamVideo;
    public boolean captionAbove;
    private int captionFullWidth;
    private int captionHeight;
    public MessageObject.TextLayoutBlocks captionLayout;
    private int captionOffsetX;
    private int captionWidth;
    private float captionX;
    public float captionY;
    public ChannelRecommendationsCell channelRecommendationsCell;
    private CheckBoxBase checkBox;
    private boolean checkBoxAnimationInProgress;
    private float checkBoxAnimationProgress;
    public int checkBoxTranslation;
    private boolean checkBoxVisible;
    private boolean checkOnlyButtonPressed;
    public int childPosition;
    public int childPosition2;
    private GradientClip clip;
    private Paint clipPaint;
    public boolean clipToGroupBounds;
    private Drawable closeExplanationDrawable;
    private int closeExplanationDrawableColor;
    private boolean closeExplanationPressed;
    private int closeExplanationX;
    private int closeExplanationY;
    private ButtonBounce closeSponsoredBounce;
    private RectF closeSponsoredBounds;
    private String closeTimeText;
    private int closeTimeWidth;
    private int commentArrowX;
    private AvatarDrawable[] commentAvatarDrawables;
    private ImageReceiver[] commentAvatarImages;
    private boolean[] commentAvatarImagesVisible;
    private boolean commentButtonPressed;
    private Rect commentButtonRect;
    private boolean commentDrawUnread;
    private StaticLayout commentLayout;
    private LoadingDrawable commentLoading;
    private AnimatedNumberLayout commentNumberLayout;
    private int commentNumberWidth;
    private InfiniteProgress commentProgress;
    private float commentProgressAlpha;
    private long commentProgressLastUpadteTime;
    private int commentUnreadX;
    private int commentWidth;
    private int commentX;
    public MessageObject.TextLayoutBlocks computedCaptionLayout;
    public int computedGroupCaptionY;
    private AvatarDrawable contactAvatarDrawable;
    public ButtonBounce contactBounce;
    private ArrayList<InstantViewButton> contactButtons;
    public ReplyMessageLine contactLine;
    private boolean contactPressed;
    private RectF contactRect;
    private float controlsAlpha;
    public final int currentAccount;
    public Theme.MessageDrawable currentBackgroundDrawable;
    private Theme.MessageDrawable currentBackgroundSelectedDrawable;
    private CharSequence currentCaption;
    private TLRPC.Chat currentChat;
    private CharSequence currentExplanation;
    private int currentFocusedVirtualView;
    private TLRPC.Chat currentForwardChannel;
    private String currentForwardName;
    private String currentForwardNameString;
    private TLRPC.User currentForwardUser;
    private int currentMapProvider;
    private MessageObject currentMessageObject;
    private MessageObject.GroupedMessages currentMessagesGroup;
    private long currentNameBotVerificationId;
    private boolean currentNameBotVerificationIsBadge;
    public AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable currentNameEmojiStatusDrawable;
    private Object currentNameStatus;
    public AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable currentNameStatusDrawable;
    private String currentNameString;
    private TLRPC.FileLocation currentPhoto;
    private String currentPhotoFilter;
    private String currentPhotoFilterThumb;
    private ImageLocation currentPhotoLocation;
    private TLRPC.PhotoSize currentPhotoObject;
    private TLRPC.PhotoSize currentPhotoObjectThumb;
    private BitmapDrawable currentPhotoObjectThumbStripped;
    private ImageLocation currentPhotoThumbLocation;
    private MessageObject.GroupedMessagePosition currentPosition;
    private String currentRepliesString;
    private TLRPC.PhotoSize currentReplyPhoto;
    private float currentSelectedBackgroundAlpha;
    private SpannableStringBuilder currentTimeString;
    private String currentUnlockString;
    private String currentUrl;
    private TLRPC.User currentUser;
    private TLRPC.User currentViaBotUser;
    private String currentViewsString;
    private WebFile currentWebFile;
    private ChatMessageCellDelegate delegate;
    private RectF deleteProgressRect;
    private StaticLayout descriptionLayout;
    private int descriptionLayoutLeft;
    private int descriptionLayoutWidth;
    private int descriptionX;
    private int descriptionY;
    private Runnable diceFinishCallback;
    private long diceStakeOutcome;
    private boolean disallowLongPress;
    public byte[] doNotDrawPollId;
    public int doNotDrawTaskId;
    private final Runnable doUpdateRelativeDatesRunnable;
    private StaticLayout docTitleLayout;
    private int docTitleOffsetX;
    private int docTitleWidth;
    private TLRPC.Document documentAttach;
    private int documentAttachType;
    public BotForumHelper.BotDraftAnimationsPool draftAnimationsPool;
    private boolean drawBackground;
    private boolean drawCommentButton;
    private boolean drawCommentNumber;
    private boolean drawContact;
    private boolean drawContactAdd;
    private boolean drawContactSendMessage;
    private boolean drawContactView;
    private boolean drawContinueBotTopic;
    public boolean drawForBlur;
    private boolean drawForwardedName;
    public boolean drawFromPinchToZoom;
    private boolean drawImageButton;
    private boolean drawInstantView;
    public int drawInstantViewType;
    private boolean drawMediaCheckBox;
    private boolean drawName;
    private boolean drawNameAvatar;
    private boolean drawNameLayout;
    public byte[] drawOnlyPollId;
    public boolean drawPhotoImage;
    public boolean drawPinnedBottom;
    public boolean drawPinnedTop;
    private boolean drawRadialCheckBackground;
    private boolean drawSelectionBackground;
    private int drawSideButton;
    private int drawSideButton2;
    private boolean drawStartBotTopic;
    private boolean drawSummarizeButton;
    public boolean drawSummaryReply;
    private boolean drawTime;
    private float drawTimeX;
    private float drawTimeY;
    private boolean drawTopic;
    private boolean drawVideoImageButton;
    private boolean drawVideoSize;
    public boolean drawingToBitmap;
    private int drawnContactButtonsFlag;
    private Paint drillHolePaint;
    private Path drillHolePath;
    private StaticLayout durationLayout;
    private int durationWidth;
    private boolean edited;
    private AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable effectDrawable;
    private ButtonBounce effectDrawableBounce;
    private long effectId;
    private int effectMessageId;
    boolean enterTransitionInProgress;
    private StaticLayout ephemeralLayout;
    private int ephemeralWidth;
    private float ephemeralX;
    private float ephemeralY;
    public ExpiredStoryView expiredStoryView;
    private int explanationContentHeight;
    private int explanationContentWidth;
    private int explanationHeight;
    public MessageObject.TextLayoutBlocks explanationLayout;
    public StaticLayout explanationTitleLayout;
    private Drawable factCheckArrow;
    private int factCheckArrowColor;
    private ButtonBounce factCheckBounce;
    private int factCheckHeight;
    private boolean factCheckLarge;
    public ReplyMessageLine factCheckLine;
    private LinkSpanDrawable.LinkCollector factCheckLinks;
    private boolean factCheckPressed;
    private StaticLayout factCheckText2Layout;
    private int factCheckText2LayoutLeft;
    private StaticLayout factCheckTextLayout;
    private int factCheckTextLayoutHeight;
    private boolean factCheckTextLayoutLastLineEnd;
    private int factCheckTextLayoutLeft;
    private Text factCheckTitle;
    private Text factCheckWhat;
    private ButtonBounce factCheckWhatBounce;
    private boolean factCheckWhatPressed;
    private int factCheckWidth;
    private int factCheckY;
    private ColorMatrixColorFilter fancyBlurFilter;
    private boolean firstCircleLength;
    public boolean firstInChat;
    private boolean firstInChatToSet;
    private int firstVisibleBlockNum;
    public int firstVisiblePollButton;
    private int firstVisibleRichBlock;
    private boolean fitPhotoImage;
    private FlagSecureReason flagSecure;
    private boolean flipImage;
    private boolean forceNotDrawTime;
    private boolean forcedLayout;
    private Drawable foreverDrawable;
    private int foreverDrawableColor;
    private AvatarSpan forwardAvatar;
    private ForwardBackground forwardBg;
    private boolean forwardBotPressed;
    private int forwardHeight;
    private int forwardNameCenterX;
    private final float[] forwardNameOffsetX;
    private boolean forwardNamePressed;
    private float forwardNameX;
    private int forwardNameY;
    private final StaticLayout[] forwardedNameLayout;
    private int forwardedNameWidth;
    private boolean frozen;
    private boolean fullyDraw;
    private boolean gamePreviewPressed;
    public final GiveawayMessageCell giveawayMessageCell;
    public final GiveawayResultsMessageCell giveawayResultsMessageCell;
    private Drawable gradientDrawable;
    private LinearGradient gradientShader;
    private Drawable groupCallDrawable;
    private int groupCallDrawableColor;
    private AvatarsDrawable groupCallParticipantsAvatars;
    private Text groupCallParticipantsText;
    public GroupMedia groupMedia;
    private boolean groupPhotoInvisible;
    private MessageObject.GroupedMessages groupedMessagesToSet;
    private boolean hadLongPress;
    public boolean hasDiscussion;
    private boolean hasEmbed;
    private boolean hasFactCheck;
    private boolean hasGamePreview;
    private boolean hasInvoicePreview;
    private boolean hasInvoicePrice;
    private boolean hasLinkPreview;
    private int hasMiniProgress;
    private boolean hasNewLineForTime;
    private boolean hasOldCaptionPreview;
    private boolean hasPsaHint;
    public boolean hasReplyQuote;
    private boolean hideSideButtonByQuickShare;
    private int highlightCaptionToSetEnd;
    private int highlightCaptionToSetStart;
    private LinkPath highlightPath;
    private long highlightPathStart;
    private int highlightProgress;
    private boolean highlightedQuote;
    private float hintButtonProgress;
    private boolean hintButtonVisible;
    private int imageBackgroundColor;
    private int imageBackgroundGradientColor1;
    private int imageBackgroundGradientColor2;
    private int imageBackgroundGradientColor3;
    private int imageBackgroundGradientRotation;
    private float imageBackgroundIntensity;
    private int imageBackgroundSideColor;
    private int imageBackgroundSideWidth;
    private boolean imageDrawn;
    private boolean imagePressed;
    boolean imageReceiversAttachState;
    boolean imageReceiversVisibleState;
    private boolean inLayout;
    private boolean inQuickShareMode;
    private StaticLayout infoLayout;
    private int infoWidth;
    private ButtonBounce instantButtonBounce;
    private LoadingDrawable instantButtonLoading;
    private boolean instantButtonPressed;
    private RectF instantButtonRect;
    public Drawable instantDrawable;
    public int instantDrawableColor;
    private Paint instantLinkArrowPaint;
    private Path instantLinkArrowPath;
    private boolean instantPressed;
    private int instantTextLeftX;
    private boolean instantTextNewLine;
    private int instantTextX;
    public CharSequence instantViewButtonText;
    private StaticLayout instantViewLayout;
    private float instantViewLayoutLeft;
    private float instantViewLayoutWidth;
    private TL_stars.StarGift instantViewTypeIsGiftAuction;
    private int instantWidth;
    private Runnable invalidateListener;
    private final Runnable invalidateOutboundsRunnable;
    private Runnable invalidateRunnable;
    private boolean invalidateSpoilersParent;
    private boolean invalidatesParent;
    public boolean isAllChats;
    public boolean isAvatarVisible;
    public boolean isBlurred;
    public boolean isBot;
    public boolean isBotForum;
    public boolean isChat;
    private boolean isCheckPressed;
    public boolean isForum;
    public boolean isForumGeneral;
    private boolean isHighlighted;
    private boolean isHighlightedAnimated;
    private boolean isMedia;
    public boolean isMegagroup;
    public boolean isMonoForum;
    public boolean isPinned;
    public boolean isPinnedChat;
    private boolean isPlayingRound;
    private boolean isPressed;
    public boolean isRepliesChat;
    public boolean isReplyQuote;
    public boolean isReplyTaskOrPollOption;
    public boolean isReportChat;
    private boolean isRoundVideo;
    public boolean isSavedChat;
    public boolean isSavedPreviewChat;
    public boolean isSideMenuEnabled;
    public boolean isSideMenued;
    private boolean isSmallImage;
    private boolean isSpoilerRevealing;
    private final BoolAnimator isSponsoredMessageHidden;
    public boolean isThreadChat;
    private boolean isThreadPost;
    private boolean isTitleLabelPressed;
    private boolean isUpdating;
    private int keyboardHeight;
    private long lastAnimationTime;
    private long lastCheckBoxAnimationTime;
    private long lastControlsAlphaChangeTime;
    private int lastDeleteDate;
    private float lastDrawExplanationX;
    private float lastDrawExplanationY;
    private float lastDrawingAudioProgress;
    private int lastHeight;
    private long lastHighlightProgressTime;
    public boolean lastInChatList;
    private boolean lastInChatListToSet;
    private long lastLoadingSizeTotal;
    private long lastNamesAnimationTime;
    private TLRPC.Poll lastPoll;
    private long lastPollCloseTime;
    private ArrayList<TLRPC.PollAnswerVoters> lastPollResults;
    private TLRPC.PollResults lastPollResultsObj;
    private int lastPollResultsVoters;
    private String lastPostAuthor;
    private TLRPC.TL_messageReactions lastReactions;
    private int lastRepliesCount;
    private TLRPC.Message lastReplyMessage;
    private long lastSeekUpdateTime;
    private int lastSendState;
    int lastSize;
    private double lastTime;
    private float lastTouchX;
    private float lastTouchY;
    private boolean lastTranslated;
    private int lastViewsCount;
    private int lastVisibleBlockNum;
    public int lastVisiblePollButton;
    private int lastVisibleRichBlock;
    private WebFile lastWebFile;
    private int lastWidth;
    public int layoutHeight;
    public int layoutWidth;
    public int linkBlockNum;
    public int linkExplanationBlockNum;
    public ReplyMessageLine linkLine;
    public boolean linkPreviewAbove;
    private ButtonBounce linkPreviewBounce;
    public int linkPreviewHeight;
    private boolean linkPreviewPressed;
    private Drawable linkPreviewSelector;
    public int linkPreviewSelectorColor;
    private int linkPreviewY;
    private int linkSelectionBlockNum;
    public long linkedChatId;
    public LinkSpanDrawable.LinkCollector links;
    private StaticLayout loadingProgressLayout;
    private long loadingProgressLayoutHash;
    private boolean locationExpired;
    private ImageReceiver locationImageReceiver;
    private Drawable locationLoadingThumb;
    public boolean makeVisibleAfterChange;
    private boolean mediaBackground;
    private CheckBoxBase mediaCheckBox;
    private int mediaOffsetY;
    private SpoilerEffect mediaSpoilerEffect;
    private SpoilerEffect2 mediaSpoilerEffect2;
    private Integer mediaSpoilerEffect2Index;
    private Path mediaSpoilerPath;
    private float[] mediaSpoilerRadii;
    private float mediaSpoilerRevealMaxRadius;
    private float mediaSpoilerRevealProgress;
    private float mediaSpoilerRevealX;
    private float mediaSpoilerRevealY;
    private boolean mediaWasInvisible;
    private MessageObject messageObjectToSet;
    private int miniButtonPressed;
    private int miniButtonState;
    private MotionBackgroundDrawable motionBackgroundDrawable;
    private boolean nameBadgePressed;
    private Drawable nameBadgeSelector;
    private int nameBadgeSelectorColor;
    private StaticLayout nameLayout;
    private boolean nameLayoutPressed;
    private Drawable nameLayoutSelector;
    private int nameLayoutSelectorColor;
    private int nameLayoutWidth;
    private float nameOffsetX;
    private boolean namePressed;
    private boolean nameStatusPressed;
    private Drawable nameStatusSelector;
    private int nameStatusSelectorColor;
    private String nameStatusSlug;
    private int nameWidth;
    private float nameX;
    private float nameY;
    public int namesOffset;
    private boolean needNewVisiblePart;
    public boolean needReplyImage;
    private int noSoundCenterX;
    private final ArrayList<PollButton> oldPollButtons;
    private Paint onceClearPaint;
    private RLottieDrawable onceFire;
    private CaptionContainerView.PeriodDrawable oncePeriod;
    private Paint onceRadialCutPaint;
    private Paint onceRadialPaint;
    private Paint onceRadialStrokePaint;
    private boolean otherPressed;
    private int otherX;
    private int otherY;
    private int overideShouldDrawTimeOnMedia;
    private Runnable overrideInvalidate;
    private AudioVisualizerDrawable overridenAudioVisualizer;
    private long overridenDuration;
    public int parentBoundsBottom;
    public float parentBoundsTop;
    public int parentHeight;
    public float parentViewTopOffset;
    public int parentWidth;
    private StaticLayout performerLayout;
    private int performerX;
    private ImageReceiver photoImage;
    private Path photoImageClipPath;
    private float[] photoImageClipPathRadii;
    private boolean photoImageOutOfBounds;
    private boolean photoNotSet;
    private TLObject photoParentObject;
    private StaticLayout photosCountLayout;
    private int photosCountWidth;
    public boolean pinnedBottom;
    public boolean pinnedTop;
    private boolean playedDice;
    private PollAddButtonDrawable pollAddButtonDrawable;
    private int pollAddButtonHeight;
    private int pollAddButtonPressed;
    private boolean pollAllowAdding;
    private float pollAnimationProgress;
    private float pollAnimationProgressTime;
    public final ArrayList<PollButton> pollButtons;
    private CheckBoxBase[] pollCheckBox;
    private Paint pollCheckPaint;
    private Path pollCheckPath;
    private boolean pollClosed;
    private PollContentDrawable pollContentDrawable;
    private int pollContentHeight;
    private int pollContentHeightWithOffset;
    private int pollContentWidth;
    private CountdownTimer pollCountDownTimer;
    private Paint pollCutAvatarPaint;
    private PollContentDrawable pollExplanationDrawable;
    private boolean pollHasResults;
    private boolean pollHasVoteRestrictions;
    private boolean pollHideResults;
    private boolean pollHintPressed;
    private int pollHintX;
    private int pollHintY;
    private boolean pollInInputNewOption;
    private PollInstantButtonDrawable pollInstantButtonDrawable;
    private boolean pollInstantViewTouchesBottom;
    private int pollMediaPressedIndex;
    private int[] pollPhotoImageRadius;
    private AvatarsListDrawable pollRecentVotersDrawable;
    private boolean pollResultsPreview;
    private boolean pollUnvoteInProgress;
    private boolean pollVoteInProgress;
    private int pollVoteInProgressNum;
    private boolean pollVoted;
    private boolean pollWithMediaInAnswers;
    private final PostRunnableHolder postRunnableHolder;
    private int pressedBlock;
    private int pressedBotButton;
    private MessageObject.TextLayoutBlock pressedCopyCode;
    private boolean pressedEffect;
    private AnimatedEmojiSpan pressedEmoji;
    private LinkSpanDrawable pressedFactCheckLink;
    private LinkSpanDrawable pressedLink;
    private int pressedLinkType;
    private int pressedSideButton;
    private final int[] pressedState;
    private int pressedVoteButton;
    private boolean pressedVoteIsMedia;
    public MessageObject.TextLayoutBlocks prevCaptionLayout;
    private CharacterStyle progressLoadingLink;
    private LoadingDrawable progressLoadingLinkCurrentDrawable;
    private ArrayList<LoadingDrawableLocation> progressLoadingLinkDrawables;
    private float psaButtonProgress;
    private boolean psaButtonVisible;
    private int psaHelpX;
    private int psaHelpY;
    private boolean psaHintPressed;
    public Drawable quoteArrow;
    public int quoteArrowColor;
    public Drawable[] quoteDrawable;
    public int[] quoteDrawableColor;
    public QuoteHighlight quoteHighlight;
    public ReplyMessageLine quoteLine;
    private RadialProgress2 radialProgress;
    protected float radialProgressAlpha;
    public final ReactionsLayoutInBubble reactionsLayoutInBubble;
    private boolean reactionsVisible;
    private RectF rect;
    private Path rectPath;
    private StaticLayout repliesLayout;
    private int repliesTextWidth;
    public ButtonBounce replyBounce;
    public float replyBounceX;
    public float replyBounceY;
    public float replyHeight;
    public ImageReceiver replyImageReceiver;
    public ReplyMessageLine replyLine;
    public StaticLayout replyNameLayout;
    private int replyNameOffset;
    private int replyNameWidth;
    private boolean replyPanelIsForward;
    private boolean replyPressed;
    private AnimatedFloat replyPressedFloat;
    public Drawable replyQuoteDrawable;
    public int replyQuoteDrawableColor;
    private Path replyRoundRectPath;
    public Drawable replySelector;
    private boolean replySelectorCanBePressed;
    public int replySelectorColor;
    private boolean replySelectorPressed;
    public float replySelectorRadLeft;
    public float replySelectorRadRight;
    public RectF replySelectorRect;
    public List<SpoilerEffect> replySpoilers;
    private final Stack<SpoilerEffect> replySpoilersPool;
    public int replyStartX;
    public int replyStartY;
    public CheckBoxBase replyTaskCheckbox;
    private int replyTextHeight;
    public StaticLayout replyTextLayout;
    public int replyTextOffset;
    public boolean replyTextRTL;
    private int replyTextWidth;
    private float replyTouchX;
    private float replyTouchY;
    private Theme.ResourcesProvider resourcesProvider;
    public float resultsPollButtonOffset;
    private float roundPlayingDrawableProgress;
    private float roundProgressAlpha;
    float roundSeekbarOutAlpha;
    float roundSeekbarOutProgress;
    int roundSeekbarTouched;
    private float roundToPauseProgress;
    private float roundToPauseProgress2;
    private AnimatedFloat roundVideoPlayPipFloat;
    private RoundVideoPlayingDrawable roundVideoPlayingDrawable;
    private final Path sPath;
    private final Runnable scheduleUpdateRelativeDatesRunnable;
    private boolean scheduledInvalidate;
    private Rect scrollRect;
    private SeekBar seekBar;
    private SeekBarAccessibilityDelegate seekBarAccessibilityDelegate;
    private int seekBarTranslateX;
    private SeekBarWaveform seekBarWaveform;
    private int seekBarWaveformTranslateX;
    private int seekBarX;
    private int seekBarY;
    float seekbarRoundX;
    float seekbarRoundY;
    private float selectedBackgroundProgress;
    private Paint selectionOverlayPaint;
    private final Drawable[] selectorDrawable;
    private int selectorDrawableColor;
    private int[] selectorDrawableMaskType;
    private final MaskDrawable[] selectorMaskDrawable;
    private Text sensitiveText;
    private Text sensitiveTextShort;
    private Text sensitiveTextShort2;
    private AnimatorSet shakeAnimation;
    private ChatMessageSharedResources sharedResources;
    public boolean shouldCheckVisibleOnScreen;
    public boolean showTopicSeparator;
    private Path sideButtonPath1;
    private Path sideButtonPath2;
    private float[] sideButtonPathCorners1;
    private float[] sideButtonPathCorners2;
    private boolean sideButtonPressed;
    private boolean sideButtonVisible;
    private ImageReceiver sideImage;
    public float sideMenuAlpha;
    public int sideMenuWidth;
    private int sideNameWidth;
    private float sideStartX;
    private float sideStartY;
    public int signWidth;
    private StaticLayout siteNameLayout;
    private float siteNameLayoutWidth;
    private float siteNameLeft;
    private int siteNameWidth;
    private boolean skipFrameUpdate;
    private float slidingOffsetX;
    private StaticLayout songLayout;
    private int songX;
    private SpoilerEffect spoilerPressed;
    private AtomicReference<Layout> spoilersPatchedReplyTextLayout;
    private Paint srcOutPaint;
    private long starsPrice;
    private Text starsPriceText;
    private LinkPath starsPriceTextPath;
    private CornerPathEffect starsPriceTextPathEffect;
    public int starsPriceTopPadding;
    private boolean statusDrawableAnimationInProgress;
    private ValueAnimator statusDrawableAnimator;
    private float statusDrawableProgress;
    private StickerSetLinkIcon stickerSetIcons;
    private int substractBackgroundHeight;
    public SuggestionOffer suggestionOffer;
    private int suggestionOfferTopPadding;
    private boolean summarizeButtonPressed;
    private float summarizeButtonX;
    private float summarizeButtonY;
    private SummaryIcon summarizeIcon;
    public ButtonBounce summaryBounce;
    public ReplyMessageLine summaryLine;
    public StarsReactionsSheet.Particles summaryParticles;
    public Drawable summaryReplySelector;
    public int summarySelectorColor;
    public RectF summarySelectorRect;
    public int summaryStartX;
    public int summaryStartY;
    public Text summarySubtitle;
    public Text summaryTitle;
    public int textX;
    public int textY;
    private float timeAlpha;
    private int timeAudioX;
    public StaticLayout timeLayout;
    private boolean timePressed;
    private int timeTextWidth;
    private boolean timeWasInvisible;
    public int timeWidth;
    private int timeWidthAudio;
    public int timeX;
    private TimerParticles timerParticles;
    private AnimatedFloat timerParticlesAlpha;
    private float timerTransitionProgress;
    private ButtonBounce titleLabelBounce;
    private StaticLayout titleLabelLayout;
    private float titleLabelLayoutHeight;
    private float titleLabelLayoutWidth;
    private float titleLabelX;
    private float titleLabelY;
    private StaticLayout titleLayout;
    private int titleLayoutLeft;
    private int titleLayoutWidth;
    private int titleX;
    private float toSeekBarProgress;
    private boolean topNearToSet;
    public TopicSeparator topicSeparator;
    public int topicSeparatorTopPadding;
    private long totalChangeTime;
    private int totalCommentWidth;
    public int totalHeight;
    private int totalVisibleBlocksCount;
    private boolean touchedByCaption;
    private boolean touchedByExpanation;
    public TranscribeButton transcribeButton;
    private float transcribeX;
    private float transcribeY;
    public final TransitionParams transitionParams;
    public float transitionYOffsetForDrawables;
    private LoadingDrawable translationLoadingDrawable;
    private ArrayList<MessageObject.TextLayoutBlock> translationLoadingDrawableText;
    private AnimatedFloat translationLoadingFloat;
    private LinkPath translationLoadingPath;
    private float unlockAlpha;
    private StaticLayout unlockLayout;
    private SpoilerEffect unlockSpoilerEffect;
    private Path unlockSpoilerPath;
    private float[] unlockSpoilerRadii;
    private int unlockTextWidth;
    private float unlockX;
    private float unlockY;
    private int unmovedTextX;
    private ArrayList<LinkPath> urlPathCache;
    private ArrayList<LinkPath> urlPathSelection;
    private boolean useSeekBarWaveform;
    private boolean useTranscribeButton;
    private int viaNameWidth;
    private boolean viaOnly;
    private TypefaceSpan viaSpan1;
    private TypefaceSpan viaSpan2;
    private int viaWidth;
    private boolean vibrateOnPollVote;
    private int videoButtonPressed;
    private int videoButtonX;
    private int videoButtonY;
    VideoForwardDrawable videoForwardDrawable;
    private StaticLayout videoInfoLayout;
    OldVideoPlayerRewinder videoPlayerRewinder;
    private RadialProgress2 videoRadialProgress;
    public float viewTop;
    private StaticLayout viewsLayout;
    private int viewsTextWidth;
    public int visibleHeight;
    private boolean visibleOnScreen;
    public int visibleParent;
    public float visibleParentOffset;
    public float visibleTop;
    private float voteCurrentCircleLength;
    private float voteCurrentProgressTime;
    private long voteLastUpdateTime;
    private float voteRadOffset;
    private boolean voteRisingCircleLength;
    private boolean wasAllChats;
    private boolean wasLayout;
    private boolean wasPinned;
    private boolean wasSending;
    private boolean wasTranscriptionOpen;
    private int widthBeforeNewTimeLine;
    private int widthForButtons;
    private boolean willRemoved;
    private boolean wouldBeInPip;

    private boolean intersect(float f, float f2, float f3, float f4) {
        if (f <= f3) {
            return f2 >= f3;
        }
        return f <= f4;
    }

    public boolean isWidthAdaptive() {
        return false;
    }

    public RadialProgress2 getRadialProgress() {
        return this.radialProgress;
    }

    public void setEnterTransitionInProgress(boolean z) {
        this.enterTransitionInProgress = z;
        invalidate();
    }

    public ReactionsLayoutInBubble.ReactionButton getReactionButton(ReactionsLayoutInBubble.VisibleReaction visibleReaction) {
        return this.reactionsLayoutInBubble.getReactionButton(visibleReaction);
    }

    public MessageObject getPrimaryMessageObject() {
        MessageObject messageObject = this.currentMessageObject;
        MessageObject messageObjectFindPrimaryMessageObject = (messageObject == null || this.currentMessagesGroup == null || !messageObject.hasValidGroupId()) ? null : this.currentMessagesGroup.findPrimaryMessageObject();
        return messageObjectFindPrimaryMessageObject != null ? messageObjectFindPrimaryMessageObject : this.currentMessageObject;
    }

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        MessageObject messageObject;
        if (i == NotificationCenter.startSpoilers) {
            setSpoilersSuppressed(false);
            return;
        }
        if (i == NotificationCenter.stopSpoilers) {
            setSpoilersSuppressed(true);
            return;
        }
        if (i == NotificationCenter.userInfoDidLoad) {
            TLRPC.User user = this.currentUser;
            if (user != null) {
                if (user.id == ((Long) objArr[0]).longValue()) {
                    setAvatar(this.currentMessageObject);
                    return;
                }
                return;
            }
            return;
        }
        if (i == NotificationCenter.emojiLoaded) {
            invalidate();
            return;
        }
        if (i == NotificationCenter.updateInterfaces) {
            if (i2 == this.currentAccount && (((Integer) objArr[0]).intValue() & MessagesController.UPDATE_MASK_STATUS) != 0 && this.isAvatarVisible && ExteraConfig.getShowOnlineStatus()) {
                postInvalidateParentOnAnimation();
                return;
            }
            return;
        }
        if (i != NotificationCenter.didUpdatePremiumGiftStickers || (messageObject = this.currentMessageObject) == null) {
            return;
        }
        TLRPC.MessageMedia messageMedia = messageObject.messageOwner.media;
        if ((messageMedia instanceof TLRPC.TL_messageMediaGiveaway) || (messageMedia instanceof TLRPC.TL_messageMediaGiveawayResults)) {
            setMessageObject(messageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop, this.firstInChat);
        }
    }

    public void setAvatar(MessageObject messageObject) {
        if (messageObject == null) {
            return;
        }
        if (this.isAvatarVisible) {
            Drawable drawable = messageObject.customAvatarDrawable;
            if (drawable != null) {
                this.avatarImage.setImageBitmap(drawable);
                return;
            }
            TLRPC.User user = this.currentUser;
            if (user != null) {
                TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
                if (userProfilePhoto != null) {
                    this.currentPhoto = userProfilePhoto.photo_small;
                } else {
                    this.currentPhoto = null;
                }
                this.avatarDrawable.setInfo(this.currentAccount, user);
                this.avatarImage.setForUserOrChat(this.currentUser, this.avatarDrawable, null, LiteMode.isEnabled(LiteMode.FLAGS_CHAT), 1, false);
                return;
            }
            TLRPC.Chat chat = this.currentChat;
            if (chat != null) {
                TLRPC.ChatPhoto chatPhoto = chat.photo;
                if (chatPhoto != null) {
                    this.currentPhoto = chatPhoto.photo_small;
                } else {
                    this.currentPhoto = null;
                }
                if (chat.signature_profiles && messageObject.getDialogId() != UserObject.REPLY_BOT) {
                    long peerDialogId = DialogObject.getPeerDialogId(messageObject.messageOwner.from_id);
                    int i = messageObject.currentAccount;
                    if (peerDialogId >= 0) {
                        TLRPC.User user2 = MessagesController.getInstance(i).getUser(Long.valueOf(peerDialogId));
                        this.avatarDrawable.setInfo(this.currentAccount, user2);
                        this.avatarImage.setForUserOrChat(user2, this.avatarDrawable);
                        return;
                    } else {
                        TLRPC.Chat chat2 = MessagesController.getInstance(i).getChat(Long.valueOf(-peerDialogId));
                        this.avatarDrawable.setInfo(this.currentAccount, chat2);
                        this.avatarImage.setForUserOrChat(chat2, this.avatarDrawable);
                        return;
                    }
                }
                this.avatarDrawable.setInfo(this.currentAccount, this.currentChat);
                this.avatarImage.setForUserOrChat(this.currentChat, this.avatarDrawable);
                return;
            }
            if (messageObject.isSponsored()) {
                this.currentPhoto = null;
                if (messageObject.searchType == 4) {
                    this.avatarDrawable.setAvatarType(1);
                    this.avatarDrawable.setCustomIcon(Theme.avatarDrawables[5]);
                    this.avatarImage.setImage(null, null, this.avatarDrawable, null, null, 0);
                    return;
                } else {
                    TLRPC.Photo photo = messageObject.sponsoredPhoto;
                    if (photo != null) {
                        this.avatarImage.setImage(ImageLocation.getForPhoto(FileLoader.getClosestPhotoSizeWithSize(photo.sizes, AndroidUtilities.dp(50.0f), false, null, true), messageObject.sponsoredPhoto), "50_50", this.avatarDrawable, null, null, 0);
                        return;
                    }
                    return;
                }
            }
            this.currentPhoto = null;
            this.avatarDrawable.setInfo(messageObject.getFromChatId(), null, null);
            this.avatarImage.setImage(null, null, this.avatarDrawable, null, null, 0);
            return;
        }
        this.currentPhoto = null;
    }

    public void setSpoilersSuppressed(boolean z) {
        for (int i = 0; i < this.replySpoilers.size(); i++) {
            this.replySpoilers.get(i).setSuppressUpdates(z);
        }
        MessageObject.TextLayoutBlocks textLayoutBlocks = this.captionLayout;
        if (textLayoutBlocks != null && textLayoutBlocks.textLayoutBlocks != null) {
            for (int i2 = 0; i2 < this.captionLayout.textLayoutBlocks.size(); i2++) {
                MessageObject.TextLayoutBlock textLayoutBlock = this.captionLayout.textLayoutBlocks.get(i2);
                for (int i3 = 0; i3 < textLayoutBlock.spoilers.size(); i3++) {
                    textLayoutBlock.spoilers.get(i3).setSuppressUpdates(z);
                }
            }
        }
        if (getMessageObject() == null || getMessageObject().textLayoutBlocks == null) {
            return;
        }
        for (int i4 = 0; i4 < getMessageObject().textLayoutBlocks.size(); i4++) {
            MessageObject.TextLayoutBlock textLayoutBlock2 = getMessageObject().textLayoutBlocks.get(i4);
            for (int i5 = 0; i5 < textLayoutBlock2.spoilers.size(); i5++) {
                textLayoutBlock2.spoilers.get(i5).setSuppressUpdates(z);
            }
        }
    }

    public boolean hasSpoilers() {
        ArrayList<MessageObject.TextLayoutBlock> arrayList;
        MessageObject.TextLayoutBlocks textLayoutBlocks = this.captionLayout;
        if (textLayoutBlocks != null && (arrayList = textLayoutBlocks.textLayoutBlocks) != null) {
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                MessageObject.TextLayoutBlock textLayoutBlock = arrayList.get(i);
                i++;
                if (!textLayoutBlock.spoilers.isEmpty()) {
                    return true;
                }
            }
        }
        if (getMessageObject() != null && getMessageObject().textLayoutBlocks != null) {
            ArrayList<MessageObject.TextLayoutBlock> arrayList2 = getMessageObject().textLayoutBlocks;
            int size2 = arrayList2.size();
            int i2 = 0;
            while (i2 < size2) {
                MessageObject.TextLayoutBlock textLayoutBlock2 = arrayList2.get(i2);
                i2++;
                if (!textLayoutBlock2.spoilers.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    private void updateSpoilersVisiblePart(int i, int i2) {
        ArrayList<MessageObject.TextLayoutBlock> arrayList;
        MessageObject.TextLayoutBlocks textLayoutBlocks = this.captionLayout;
        int i3 = 0;
        if (textLayoutBlocks != null && (arrayList = textLayoutBlocks.textLayoutBlocks) != null) {
            int size = arrayList.size();
            int i4 = 0;
            while (i4 < size) {
                MessageObject.TextLayoutBlock textLayoutBlock = arrayList.get(i4);
                i4++;
                MessageObject.TextLayoutBlock textLayoutBlock2 = textLayoutBlock;
                Iterator<SpoilerEffect> it = textLayoutBlock2.spoilers.iterator();
                while (it.hasNext()) {
                    it.next().setVisibleBounds(0.0f, (i - textLayoutBlock2.textYOffset(this.captionLayout.textLayoutBlocks, this.transitionParams)) - this.captionX, getWidth(), (i2 - textLayoutBlock2.textYOffset(this.captionLayout.textLayoutBlocks, this.transitionParams)) - this.captionY);
                }
            }
        }
        StaticLayout staticLayout = this.replyTextLayout;
        if (staticLayout != null) {
            float height = (-this.replyStartY) - staticLayout.getHeight();
            Iterator<SpoilerEffect> it2 = this.replySpoilers.iterator();
            while (it2.hasNext()) {
                it2.next().setVisibleBounds(0.0f, i + height, getWidth(), i2 + height);
            }
        }
        if (getMessageObject() == null || getMessageObject().textLayoutBlocks == null) {
            return;
        }
        ArrayList<MessageObject.TextLayoutBlock> arrayList2 = getMessageObject().textLayoutBlocks;
        int size2 = arrayList2.size();
        while (i3 < size2) {
            MessageObject.TextLayoutBlock textLayoutBlock3 = arrayList2.get(i3);
            i3++;
            MessageObject.TextLayoutBlock textLayoutBlock4 = textLayoutBlock3;
            Iterator<SpoilerEffect> it3 = textLayoutBlock4.spoilers.iterator();
            while (it3.hasNext()) {
                it3.next().setVisibleBounds(0.0f, (i - textLayoutBlock4.textYOffset(getMessageObject().textLayoutBlocks, this.transitionParams)) - this.textY, getWidth(), (i2 - textLayoutBlock4.textYOffset(getMessageObject().textLayoutBlocks, this.transitionParams)) - this.textY);
            }
        }
    }

    public void setScrimReaction(Integer num) {
        this.reactionsLayoutInBubble.setScrimReaction(num);
    }

    public void drawScrimReaction(Canvas canvas, Integer num, float f, boolean z) {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null) {
            int i = groupedMessagePosition.flags;
            if ((i & 8) == 0 || (i & 1) == 0) {
                return;
            }
        }
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        if (reactionsLayoutInBubble.isSmall) {
            return;
        }
        reactionsLayoutInBubble.setScrimProgress(f, z);
        this.reactionsLayoutInBubble.draw(canvas, this.transitionParams.animateChangeProgress, num);
    }

    public void drawScrimReactionPreview(View view, Canvas canvas, int i, Integer num, float f) {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        if (groupedMessagePosition != null) {
            int i2 = groupedMessagePosition.flags;
            if ((i2 & 8) == 0 || (i2 & 1) == 0) {
                return;
            }
        }
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        if (reactionsLayoutInBubble.isSmall) {
            return;
        }
        reactionsLayoutInBubble.setScrimProgress(f);
        this.reactionsLayoutInBubble.drawPreview(view, canvas, i, num);
    }

    public boolean checkUnreadPollVotes() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.type == 17) {
            TLRPC.MessageMedia media = MessageObject.getMedia(messageObject.messageOwner);
            if (media instanceof TLRPC.TL_messageMediaPoll) {
                return ((TLRPC.TL_messageMediaPoll) media).results.has_unread_votes;
            }
        }
        return false;
    }

    public boolean checkUnreadReactions(float f, int i) {
        if (!this.reactionsLayoutInBubble.hasUnreadReactions) {
            return false;
        }
        float y = getY();
        ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
        float f2 = y + reactionsLayoutInBubble.y;
        return f2 > f && (f2 + ((float) reactionsLayoutInBubble.height)) - ((float) AndroidUtilities.dp(16.0f)) < ((float) i);
    }

    public void markReactionsAsRead() {
        this.reactionsLayoutInBubble.hasUnreadReactions = false;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.markReactionsAsRead();
    }

    public void markPollVotesAsRead() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        messageObject.markPollVotesAsRead();
    }

    public void setVisibleOnScreen(boolean z, float f, float f2) {
        if (this.visibleOnScreen != z) {
            this.visibleOnScreen = z;
            checkImageReceiversAttachState();
            if (z) {
                invalidate();
            }
        }
        float imageY = f - this.photoImage.getImageY();
        float measuredHeight = f2 - (getMeasuredHeight() - this.photoImage.getImageY2());
        float imageHeight = this.photoImage.getImageHeight();
        if (imageY > 0.0f) {
            imageHeight -= imageY;
        }
        if (measuredHeight > 0.0f) {
            imageHeight -= measuredHeight;
        }
        ImageReceiver imageReceiver = this.photoImage;
        boolean z2 = imageHeight / imageReceiver.getImageHeight() < 0.25f;
        this.skipFrameUpdate = z2;
        imageReceiver.setSkipUpdateFrame(z2);
    }

    public void setParentBounds(float f, int i) {
        this.parentBoundsTop = f;
        this.parentBoundsBottom = i;
        if (this.photoImageOutOfBounds) {
            float y = getY() + getPaddingTop() + this.photoImage.getImageY();
            if (this.photoImage.getImageHeight() + y < this.parentBoundsTop || y > this.parentBoundsBottom) {
                return;
            }
            invalidate();
        }
    }

    public void setSponsoredMessageVisible(boolean z, boolean z2) {
        this.isSponsoredMessageHidden.setValue(!z, z2);
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        if (i == 0) {
            invalidate();
            invalidateOutbounds();
        }
    }

    public interface ChatMessageCellDelegate {
        default boolean allowAddPollOptions() {
            return false;
        }

        default boolean canDrawOutboundsContent() {
            return true;
        }

        default boolean canPerformActions() {
            return false;
        }

        default boolean canToggleRichMessageCheckbox(ChatMessageCell chatMessageCell) {
            return false;
        }

        default void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
        }

        default void didLongPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
        }

        default boolean didLongPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2) {
            return false;
        }

        default void didLongPressCustomBotButton(ChatMessageCell chatMessageCell, BotInlineKeyboard.ButtonCustom buttonCustom) {
        }

        default boolean didLongPressPollOption(ChatMessageCell chatMessageCell, TLRPC.PollAnswer pollAnswer) {
            return false;
        }

        default boolean didLongPressToDoButton(ChatMessageCell chatMessageCell, TLRPC.TodoItem todoItem) {
            return false;
        }

        default boolean didLongPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2) {
            return false;
        }

        default void didPressAboutRevenueSharingAds() {
        }

        default void didPressAddPollOptionButton(ChatMessageCell chatMessageCell) {
        }

        default void didPressAdmin(ChatMessageCell chatMessageCell) {
        }

        default boolean didPressAnimatedEmoji(ChatMessageCell chatMessageCell, AnimatedEmojiSpan animatedEmojiSpan) {
            return false;
        }

        default void didPressBoostCounter(ChatMessageCell chatMessageCell) {
        }

        default void didPressBotButton(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
        }

        default void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
        }

        default void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2, boolean z) {
        }

        default void didPressChannelRecommendation(ChatMessageCell chatMessageCell, TLObject tLObject, boolean z) {
        }

        default void didPressChannelRecommendationsClose(ChatMessageCell chatMessageCell) {
        }

        default void didPressCodeCopy(ChatMessageCell chatMessageCell, MessageObject.TextLayoutBlock textLayoutBlock) {
        }

        default void didPressCommentButton(ChatMessageCell chatMessageCell) {
        }

        default void didPressCustomBotButton(ChatMessageCell chatMessageCell, BotInlineKeyboard.ButtonCustom buttonCustom) {
        }

        default void didPressEffect(ChatMessageCell chatMessageCell) {
        }

        default void didPressExtendedMediaPreview(ChatMessageCell chatMessageCell, TLRPC.KeyboardButton keyboardButton) {
        }

        default void didPressFactCheck(ChatMessageCell chatMessageCell) {
        }

        default void didPressFactCheckWhat(ChatMessageCell chatMessageCell, int i, int i2) {
        }

        default void didPressGiveawayChatButton(ChatMessageCell chatMessageCell, int i) {
        }

        default void didPressGroupImage(ChatMessageCell chatMessageCell, ImageReceiver imageReceiver, TLRPC.MessageExtendedMedia messageExtendedMedia, float f, float f2) {
        }

        default void didPressHiddenForward(ChatMessageCell chatMessageCell) {
        }

        default void didPressHint(ChatMessageCell chatMessageCell, int i) {
        }

        default void didPressImage(ChatMessageCell chatMessageCell, float f, float f2, boolean z) {
        }

        default void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
        }

        default void didPressMoreChannelRecommendations(ChatMessageCell chatMessageCell) {
        }

        default void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
        }

        default void didPressPollMedia(ChatMessageCell chatMessageCell, ImageReceiver imageReceiver, TLRPC.PollAnswer pollAnswer, TLRPC.MessageMedia messageMedia, float f, float f2, int i) {
        }

        default void didPressReaction(ChatMessageCell chatMessageCell, TLRPC.ReactionCount reactionCount, boolean z, float f, float f2) {
        }

        default void didPressReplyMessage(ChatMessageCell chatMessageCell, int i, float f, float f2, boolean z) {
        }

        default void didPressRevealSensitiveContent(ChatMessageCell chatMessageCell) {
        }

        default void didPressShowMore(ChatMessageCell chatMessageCell) {
        }

        default void didPressSideButton(ChatMessageCell chatMessageCell) {
        }

        default void didPressSponsoredClose(ChatMessageCell chatMessageCell) {
        }

        default void didPressSponsoredInfo(ChatMessageCell chatMessageCell, float f, float f2) {
        }

        default void didPressSummarize(ChatMessageCell chatMessageCell, boolean z) {
        }

        default void didPressTime(ChatMessageCell chatMessageCell) {
        }

        default boolean didPressToDoButton(ChatMessageCell chatMessageCell, TLRPC.TodoItem todoItem, boolean z) {
            return false;
        }

        default void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
        }

        default void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2, boolean z) {
        }

        default void didPressUserBadge(ChatMessageCell chatMessageCell, TLRPC.User user, BadgeDTO badgeDTO) {
        }

        default void didPressUserStatus(ChatMessageCell chatMessageCell, TLRPC.User user, TLRPC.Document document, String str) {
        }

        default void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
        }

        default void didPressViaBotNotInline(ChatMessageCell chatMessageCell, long j) {
        }

        default void didPressVoteButtons(ChatMessageCell chatMessageCell, ArrayList<TLRPC.PollAnswer> arrayList, int i, int i2, int i3) {
        }

        default void didQuickShareEnd(ChatMessageCell chatMessageCell, float f, float f2) {
        }

        default void didQuickShareMove(ChatMessageCell chatMessageCell, float f, float f2) {
        }

        default void didQuickShareStart(ChatMessageCell chatMessageCell, float f, float f2) {
        }

        default void didStartVideoStream(MessageObject messageObject) {
        }

        default void didTogglePollPreview(ChatMessageCell chatMessageCell) {
        }

        default void didToggleRichMessageCheckbox(ChatMessageCell chatMessageCell, boolean z, Runnable runnable) {
        }

        default void drawPollMode(Canvas canvas, ChatMessageCell chatMessageCell) {
        }

        default void forceUpdate(ChatMessageCell chatMessageCell, boolean z) {
        }

        default void forceUpdateNoAnimation(ChatMessageCell chatMessageCell, boolean z) {
        }

        default int getAddPollOptionInputFieldHeight(ChatMessageCell chatMessageCell) {
            return 0;
        }

        default String getAdminRank(long j) {
            return null;
        }

        default ChatActivityDraftMessageMeasureController getDraftMessageMeasureController() {
            return null;
        }

        default PinchToZoomHelper getPinchToZoomHelper() {
            return null;
        }

        default String getProgressLoadingBotButtonUrl(ChatMessageCell chatMessageCell) {
            return null;
        }

        default CharacterStyle getProgressLoadingLink(ChatMessageCell chatMessageCell) {
            return null;
        }

        default TextSelectionHelper.ChatListTextSelectionHelper getTextSelectionHelper() {
            return null;
        }

        default boolean hasSelectedMessages() {
            return false;
        }

        default void invalidateBlur() {
        }

        default boolean isAdmin(long j) {
            return false;
        }

        default boolean isLandscape() {
            return false;
        }

        default boolean isOwner(long j) {
            return false;
        }

        default boolean isProgressLoading(ChatMessageCell chatMessageCell, int i) {
            return false;
        }

        default boolean isReplyOrSelf() {
            return false;
        }

        default boolean keyboardIsOpened() {
            return false;
        }

        default void needOpenWebView(MessageObject messageObject, String str, String str2, String str3, String str4, int i, int i2) {
        }

        default boolean needPlayMessage(ChatMessageCell chatMessageCell, MessageObject messageObject, boolean z) {
            return false;
        }

        default void needReloadPolls() {
        }

        default void needShowPremiumBulletin(int i) {
        }

        default boolean onAccessibilityAction(int i, Bundle bundle) {
            return false;
        }

        default void onDiceFinished() {
        }

        default boolean openArticlePhoto(ChatMessageCell chatMessageCell, TL_iv.PageBlock pageBlock) {
            return false;
        }

        default void setShouldNotRepeatSticker(MessageObject messageObject) {
        }

        default boolean shouldDrawAvatarOnlineStatus(ChatMessageCell chatMessageCell) {
            return true;
        }

        default boolean shouldDrawThreadProgress(ChatMessageCell chatMessageCell, boolean z) {
            return false;
        }

        default boolean shouldRepeatSticker(MessageObject messageObject) {
            return true;
        }

        default void videoTimerReached() {
        }

        default void didPressWebPage(ChatMessageCell chatMessageCell, TLRPC.WebPage webPage, String str, boolean z) {
            Browser.openUrl(chatMessageCell.getContext(), str);
        }

        default boolean canPerformReply() {
            return canPerformActions();
        }

        default boolean doNotShowLoadingReply(MessageObject messageObject) {
            return messageObject != null && messageObject.getDialogId() == UserObject.REPLY_BOT;
        }
    }

    private ChatActivityDraftMessageMeasureController getDraftMessageMeasureController() {
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        if (chatMessageCellDelegate != null) {
            return chatMessageCellDelegate.getDraftMessageMeasureController();
        }
        return null;
    }

    public class PollButton {
        public int animateHeight;
        private StaticLayout animateTitle;
        private AnimatedEmojiSpan.EmojiGroupedSpans animateTitleEmoji;
        public int animateY;
        public AnimatedEmojiSpan.EmojiGroupedSpans animatedEmoji;
        private TLRPC.PollAnswer answer;
        private Text author;
        private AvatarDrawable avatarDrawable;
        private ImageReceiver avatarImageReceiver;
        private boolean chosen;
        private boolean correct;
        private int count;
        private float decimal;
        public int height;
        public boolean moveTitleByCounter;
        private int percent;
        private float percentProgress;
        public PollButtonDrawable pollButtonDrawable;
        private boolean prevChosen;
        private int prevPercent;
        private float prevPercentProgress;
        public Drawable selectorDrawable;
        public int selectorDrawableColor;
        private TLRPC.TodoItem task;
        public StaticLayout title;
        public float titleX;
        public float titleY;
        private boolean translated;
        public int x;
        public int y;

        public PollButton() {
        }

        public void attach() {
            ImageReceiver imageReceiver = this.avatarImageReceiver;
            if (imageReceiver != null) {
                imageReceiver.onAttachedToWindow();
            }
            PollButtonDrawable pollButtonDrawable = this.pollButtonDrawable;
            if (pollButtonDrawable != null) {
                pollButtonDrawable.attach();
            }
        }

        public void detach() {
            ImageReceiver imageReceiver = this.avatarImageReceiver;
            if (imageReceiver != null) {
                imageReceiver.onDetachedFromWindow();
            }
            PollButtonDrawable pollButtonDrawable = this.pollButtonDrawable;
            if (pollButtonDrawable != null) {
                pollButtonDrawable.detach();
            }
            AnimatedEmojiSpan.release(ChatMessageCell.this, this.animatedEmoji);
            AnimatedEmojiSpan.release(ChatMessageCell.this, this.animateTitleEmoji);
        }

        public void destroy() {
            detach();
            this.animatedEmoji = null;
            this.animateTitleEmoji = null;
        }
    }

    public static class InstantViewButton {
        private ButtonBounce buttonBounce;
        private float buttonWidth;
        private StaticLayout layout;
        private final RectF rect;
        private Drawable selectorDrawable;
        private float textX;
        private int type;

        private InstantViewButton() {
            this.rect = new RectF();
        }
    }

    public boolean isCellAttachedToWindow() {
        return this.attachedToWindow;
    }

    public float getLastTouchX() {
        return this.lastTouchX;
    }

    public float getLastTouchY() {
        return this.lastTouchY;
    }

    @Override // android.view.View
    public boolean canScrollHorizontally(int i) {
        RichMessageLayout richMessageLayout;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || (richMessageLayout = messageObject.richLayout) == null || !richMessageLayout.canScrollHorizontallyAt(this.lastTouchY - this.textY)) {
            return super.canScrollHorizontally(i);
        }
        return true;
    }

    public class LoadingDrawableLocation {
        int blockNum;
        LoadingDrawable drawable;

        public LoadingDrawableLocation() {
        }
    }

    public ChatMessageCell(Context context, int i) {
        this(context, i, false, null, null);
    }

    public ChatMessageCell(Context context, int i, boolean z, ChatMessageSharedResources chatMessageSharedResources, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.visibleOnScreen = true;
        this.postRunnableHolder = new PostRunnableHolder();
        this.isSponsoredMessageHidden = new BoolAnimator(0, this, CubicBezierInterpolator.EASE_OUT_QUINT, 380L);
        this.reactionsLayoutInBubble = new ReactionsLayoutInBubble(this);
        this.giveawayMessageCell = new GiveawayMessageCell(this);
        this.giveawayResultsMessageCell = new GiveawayResultsMessageCell(this);
        this.scrollRect = new Rect();
        this.firstVisibleRichBlock = -1;
        this.lastVisibleRichBlock = -1;
        this.pollPhotoImageRadius = new int[4];
        this.drawnContactButtonsFlag = 0;
        this.imageBackgroundGradientRotation = 45;
        this.selectorDrawable = new Drawable[2];
        this.selectorMaskDrawable = new MaskDrawable[2];
        this.selectorDrawableMaskType = new int[2];
        this.instantButtonRect = new RectF();
        this.pressedState = new int[]{R.attr.state_enabled, R.attr.state_pressed};
        this.highlightCaptionToSetStart = -1;
        this.highlightCaptionToSetEnd = -1;
        this.deleteProgressRect = new RectF();
        this.rect = new RectF();
        this.foreverDrawableColor = -1;
        this.timeAlpha = 1.0f;
        this.actionAlpha = 1.0f;
        this.controlsAlpha = 1.0f;
        this.pressedBlock = -1;
        this.links = new LinkSpanDrawable.LinkCollector(this);
        this.urlPathCache = new ArrayList<>();
        this.urlPathSelection = new ArrayList<>();
        this.rectPath = new Path();
        this.oldPollButtons = new ArrayList<>();
        this.pollButtons = new ArrayList<>();
        this.firstVisiblePollButton = -1;
        this.lastVisiblePollButton = -1;
        this.reactionsVisible = true;
        this.pollMediaPressedIndex = -1;
        this.botButtons = new ArrayList<>();
        this.botButtonEmojiInvalidateHolder = new AnimatedEmojiSpan.InvalidateHolder() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda8
            @Override // org.telegram.ui.Components.AnimatedEmojiSpan.InvalidateHolder
            public final void invalidate() {
                this.f$0.invalidateOutbounds();
            }
        };
        this.botButtonPath = new Path();
        this.botButtonRadii = new float[8];
        this.botButtonsByData = new HashMap<>();
        this.botButtonsByPosition = new HashMap<>();
        this.doNotDrawTaskId = -1;
        this.isCheckPressed = true;
        this.drawBackground = true;
        this.backgroundWidth = 100;
        this.commentButtonRect = new Rect();
        this.spoilersPatchedReplyTextLayout = new AtomicReference<>();
        this.adminLayoutRect = new RectF();
        this.forwardedNameLayout = new StaticLayout[2];
        this.forwardNameOffsetX = new float[2];
        this.drawTime = true;
        this.mediaSpoilerPath = new Path();
        this.mediaSpoilerRadii = new float[8];
        this.unlockAlpha = 1.0f;
        this.unlockSpoilerPath = new Path();
        this.unlockSpoilerRadii = new float[8];
        this.replySelectorRect = new RectF();
        this.summarySelectorRect = new RectF();
        this.ALPHA_PROPERTY_WORKAROUND = Build.VERSION.SDK_INT == 28;
        this.alphaInternal = 1.0f;
        this.transitionParams = new TransitionParams();
        this.roundVideoPlayPipFloat = new AnimatedFloat(this, 200L, CubicBezierInterpolator.EASE_OUT);
        this.diceFinishCallback = new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell.1
            @Override // java.lang.Runnable
            public void run() {
                if (ChatMessageCell.this.delegate != null) {
                    ChatMessageCell.this.delegate.onDiceFinished();
                }
            }
        };
        this.invalidateRunnable = new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell.2
            @Override // java.lang.Runnable
            public void run() {
                ChatMessageCell.this.checkLocationExpired();
                boolean z2 = ChatMessageCell.this.locationExpired;
                ChatMessageCell chatMessageCell = ChatMessageCell.this;
                if (z2) {
                    chatMessageCell.invalidate();
                    ChatMessageCell.this.scheduledInvalidate = false;
                } else {
                    chatMessageCell.invalidate(((int) chatMessageCell.rect.left) - 5, ((int) ChatMessageCell.this.rect.top) - 5, ((int) ChatMessageCell.this.rect.right) + 5, ((int) ChatMessageCell.this.rect.bottom) + 5);
                    if (ChatMessageCell.this.scheduledInvalidate) {
                        AndroidUtilities.runOnUIThread(ChatMessageCell.this.invalidateRunnable, 1000L);
                    }
                }
            }
        };
        this.accessibilityVirtualViewBounds = new SparseArray<>();
        this.currentFocusedVirtualView = -1;
        this.backgroundCacheParams = new Theme.MessageDrawable.PathDrawParams();
        this.replySpoilers = new ArrayList();
        this.replySpoilersPool = new Stack<>();
        this.sPath = new Path();
        this.scheduleUpdateRelativeDatesRunnable = new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.scheduleUpdateRelativeDates();
            }
        };
        this.doUpdateRelativeDatesRunnable = new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.doUpdateRelativeDates();
            }
        };
        this.pressedEffect = false;
        this.overridenDuration = -1L;
        this.hadLongPress = false;
        this.invalidateOutboundsRunnable = new ChatMessageCell$$ExternalSyntheticLambda4(this);
        this.showTopicSeparator = true;
        this.radialProgressAlpha = 1.0f;
        this.ANIMATION_OFFSET_X = new Property<ChatMessageCell, Float>(Float.class, "animationOffsetX") { // from class: org.telegram.ui.Cells.ChatMessageCell.11
            @Override // android.util.Property
            public Float get(ChatMessageCell chatMessageCell) {
                return Float.valueOf(chatMessageCell.animationOffsetX);
            }

            @Override // android.util.Property
            public void set(ChatMessageCell chatMessageCell, Float f) {
                chatMessageCell.setAnimationOffsetX(f.floatValue());
            }
        };
        this.currentAccount = i;
        this.resourcesProvider = resourcesProvider;
        this.canDrawBackgroundInParent = z;
        this.sharedResources = chatMessageSharedResources;
        if (chatMessageSharedResources == null) {
            this.sharedResources = new ChatMessageSharedResources(context);
        }
        setClipChildren(false);
        setClipToPadding(false);
        this.backgroundDrawable = new MessageBackgroundDrawable(this);
        ImageReceiver imageReceiver = new ImageReceiver();
        this.avatarImage = imageReceiver;
        imageReceiver.setAllowLoadingOnAttachedOnly(true);
        this.avatarImage.setRoundRadius(ExteraConfig.getAvatarCorners(42.0f));
        this.avatarDrawable = new AvatarDrawable();
        ImageReceiver imageReceiver2 = new ImageReceiver(this);
        this.replyImageReceiver = imageReceiver2;
        imageReceiver2.setAllowLoadingOnAttachedOnly(true);
        this.replyImageReceiver.setRoundRadius(AndroidUtilities.dp(6.0f));
        ImageReceiver imageReceiver3 = new ImageReceiver(this);
        this.locationImageReceiver = imageReceiver3;
        imageReceiver3.setAllowLoadingOnAttachedOnly(true);
        this.locationImageReceiver.setRoundRadius(AndroidUtilities.dp(26.1f));
        this.TAG = DownloadController.getInstance(i).generateObserverTag();
        this.contactAvatarDrawable = new AvatarDrawable();
        ImageReceiver imageReceiver4 = new ImageReceiver(this) { // from class: org.telegram.ui.Cells.ChatMessageCell.3
            @Override 
            public void setRoundRadius(int[] iArr) {
                super.setRoundRadius(iArr);
                ChatMessageCell.this.pollPhotoImageRadius[0] = iArr[0];
                ChatMessageCell.this.pollPhotoImageRadius[1] = iArr[1];
                int[] iArr2 = ChatMessageCell.this.pollPhotoImageRadius;
                int[] iArr3 = ChatMessageCell.this.pollPhotoImageRadius;
                int iDp = AndroidUtilities.dp(6.0f);
                iArr3[3] = iDp;
                iArr2[2] = iDp;
                if (ChatMessageCell.this.pollContentDrawable != null) {
                    ChatMessageCell.this.pollContentDrawable.imageReceiver.setRoundRadius(ChatMessageCell.this.pollPhotoImageRadius);
                }
            }
        };
        this.photoImage = imageReceiver4;
        imageReceiver4.setAllowLoadingOnAttachedOnly(true);
        this.photoImage.setUseRoundForThumbDrawable(true);
        this.photoImage.setDelegate(this);
        ImageReceiver imageReceiver5 = new ImageReceiver(this);
        this.blurredPhotoImage = imageReceiver5;
        imageReceiver5.setAllowLoadingOnAttachedOnly(true);
        this.blurredPhotoImage.setUseRoundForThumbDrawable(true);
        RadialProgress2 radialProgress2 = new RadialProgress2(this, resourcesProvider);
        this.radialProgress = radialProgress2;
        radialProgress2.setStyle(1);
        RadialProgress2 radialProgress3 = new RadialProgress2(this, resourcesProvider);
        this.videoRadialProgress = radialProgress3;
        radialProgress3.setStyle(1);
        this.videoRadialProgress.mediaActionDrawable.setDownloadIconScale(0.8f);
        this.videoRadialProgress.setInvertColors(false);
        this.videoRadialProgress.setDrawBackground(false);
        this.videoRadialProgress.setCircleRadius(AndroidUtilities.dp(15.0f));
        SeekBar seekBar = new SeekBar(this) { // from class: org.telegram.ui.Cells.ChatMessageCell.4
            @Override // org.telegram.ui.Components.SeekBar
            public void onTimestampUpdate(URLSpanNoUnderline uRLSpanNoUnderline) {
                ChatMessageCell.this.setHighlightedSpan(uRLSpanNoUnderline);
            }
        };
        this.seekBar = seekBar;
        seekBar.setDelegate(this);
        SeekBarWaveform seekBarWaveform = new SeekBarWaveform(context);
        this.seekBarWaveform = seekBarWaveform;
        seekBarWaveform.setDelegate(this);
        this.seekBarWaveform.setParentView(this);
        this.seekBarAccessibilityDelegate = new FloatSeekBarAccessibilityDelegate() { // from class: org.telegram.ui.Cells.ChatMessageCell.5
            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public float getProgress() {
                boolean zIsMusic = ChatMessageCell.this.currentMessageObject.isMusic();
                ChatMessageCell chatMessageCell = ChatMessageCell.this;
                if (zIsMusic) {
                    return chatMessageCell.seekBar.getProgress();
                }
                boolean zIsVoice = chatMessageCell.currentMessageObject.isVoice();
                ChatMessageCell chatMessageCell2 = ChatMessageCell.this;
                if (zIsVoice) {
                    boolean z2 = chatMessageCell2.useSeekBarWaveform;
                    ChatMessageCell chatMessageCell3 = ChatMessageCell.this;
                    if (z2) {
                        return chatMessageCell3.seekBarWaveform.getProgress();
                    }
                    return chatMessageCell3.seekBar.getProgress();
                }
                if (chatMessageCell2.currentMessageObject.isRoundVideo()) {
                    return ChatMessageCell.this.currentMessageObject.audioProgress;
                }
                return 0.0f;
            }

            @Override // org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate
            public void setProgress(float f) {
                boolean zIsMusic = ChatMessageCell.this.currentMessageObject.isMusic();
                ChatMessageCell chatMessageCell = ChatMessageCell.this;
                if (zIsMusic) {
                    chatMessageCell.seekBar.setProgress(f);
                } else {
                    boolean zIsVoice = chatMessageCell.currentMessageObject.isVoice();
                    ChatMessageCell chatMessageCell2 = ChatMessageCell.this;
                    if (zIsVoice) {
                        boolean z2 = chatMessageCell2.useSeekBarWaveform;
                        ChatMessageCell chatMessageCell3 = ChatMessageCell.this;
                        if (z2) {
                            chatMessageCell3.seekBarWaveform.setProgress(f);
                        } else {
                            chatMessageCell3.seekBar.setProgress(f);
                        }
                    } else {
                        if (!chatMessageCell2.currentMessageObject.isRoundVideo()) {
                            return;
                        }
                        boolean z3 = ChatMessageCell.this.useSeekBarWaveform;
                        ChatMessageCell chatMessageCell4 = ChatMessageCell.this;
                        if (z3) {
                            if (chatMessageCell4.seekBarWaveform != null) {
                                ChatMessageCell.this.seekBarWaveform.setProgress(f);
                            }
                        } else if (chatMessageCell4.seekBar != null) {
                            ChatMessageCell.this.seekBar.setProgress(f);
                        }
                        ChatMessageCell.this.currentMessageObject.audioProgress = f;
                    }
                }
                ChatMessageCell.this.onSeekBarDrag(f);
                ChatMessageCell.this.invalidate();
            }
        };
        this.roundVideoPlayingDrawable = new RoundVideoPlayingDrawable(this, resourcesProvider);
        setImportantForAccessibility(1);
    }

    public void setResourcesProvider(Theme.ResourcesProvider resourcesProvider) {
        this.resourcesProvider = resourcesProvider;
        RadialProgress2 radialProgress2 = this.radialProgress;
        if (radialProgress2 != null) {
            radialProgress2.setResourcesProvider(resourcesProvider);
        }
        RadialProgress2 radialProgress3 = this.videoRadialProgress;
        if (radialProgress3 != null) {
            radialProgress3.setResourcesProvider(resourcesProvider);
        }
        RoundVideoPlayingDrawable roundVideoPlayingDrawable = this.roundVideoPlayingDrawable;
        if (roundVideoPlayingDrawable != null) {
            roundVideoPlayingDrawable.setResourcesProvider(resourcesProvider);
        }
    }

    public Theme.ResourcesProvider getResourcesProvider() {
        return this.resourcesProvider;
    }

    private void createPollUI(int i) {
        CheckBoxBase[] checkBoxBaseArr = this.pollCheckBox;
        if (checkBoxBaseArr == null || checkBoxBaseArr.length != i) {
            if (checkBoxBaseArr != null) {
                int i2 = 0;
                while (true) {
                    CheckBoxBase[] checkBoxBaseArr2 = this.pollCheckBox;
                    if (i2 >= checkBoxBaseArr2.length) {
                        break;
                    }
                    checkBoxBaseArr2[i2].onDetachedFromWindow();
                    i2++;
                }
            }
            this.pollCheckBox = new CheckBoxBase[i];
            int i3 = 0;
            while (true) {
                CheckBoxBase[] checkBoxBaseArr3 = this.pollCheckBox;
                if (i3 >= checkBoxBaseArr3.length) {
                    break;
                }
                checkBoxBaseArr3[i3] = new CheckBoxBase(this, 20, this.resourcesProvider);
                this.pollCheckBox[i3].setDrawUnchecked(false);
                this.pollCheckBox[i3].setCuttingCheck(true);
                this.pollCheckBox[i3].setBackgroundType(9);
                i3++;
            }
        }
        if (this.pollRecentVotersDrawable == null) {
            this.pollRecentVotersDrawable = new AvatarsListDrawable(this.currentAccount, this, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dpf2(1.0f));
        }
    }

    private void createCommentUI() {
        if (this.commentAvatarImages != null) {
            return;
        }
        this.commentAvatarImages = new ImageReceiver[3];
        this.commentAvatarDrawables = new AvatarDrawable[3];
        this.commentAvatarImagesVisible = new boolean[3];
        int i = 0;
        while (true) {
            ImageReceiver[] imageReceiverArr = this.commentAvatarImages;
            if (i >= imageReceiverArr.length) {
                return;
            }
            imageReceiverArr[i] = new ImageReceiver(this);
            this.commentAvatarImages[i].setRoundRadius(ExteraConfig.getAvatarCorners(24.0f));
            this.commentAvatarDrawables[i] = new AvatarDrawable();
            this.commentAvatarDrawables[i].setTextSize(AndroidUtilities.dp(18.0f));
            i++;
        }
    }

    public void resetPressedLink(int i) {
        LinkSpanDrawable.LinkCollector linkCollector = this.links;
        if (i != -1) {
            linkCollector.removeLinks(Integer.valueOf(i));
        } else {
            linkCollector.clear();
        }
        LinkSpanDrawable.LinkCollector linkCollector2 = this.factCheckLinks;
        if (linkCollector2 != null) {
            linkCollector2.clear();
        }
        this.pressedEmoji = null;
        this.pressedFactCheckLink = null;
        if (this.pressedLink != null) {
            if (this.pressedLinkType == i || i == -1) {
                this.pressedLink = null;
                this.pressedLinkType = -1;
                this.touchedByExpanation = false;
                this.touchedByCaption = false;
                invalidate();
            }
        }
    }

    private void resetUrlPaths() {
        if (this.quoteHighlight != null) {
            this.quoteHighlight = null;
        }
        if (this.urlPathSelection.isEmpty()) {
            return;
        }
        this.urlPathCache.addAll(this.urlPathSelection);
        this.urlPathSelection.clear();
    }

    private LinkPath obtainNewUrlPath() {
        LinkPath linkPath;
        if (!this.urlPathCache.isEmpty()) {
            linkPath = this.urlPathCache.get(0);
            this.urlPathCache.remove(0);
        } else {
            linkPath = new LinkPath(true);
        }
        linkPath.reset();
        this.urlPathSelection.add(linkPath);
        return linkPath;
    }

    public int[] getRealSpanStartAndEnd(Spannable spannable, CharacterStyle characterStyle) {
        int spanStart;
        int spanEnd;
        boolean z;
        TextStyleSpan.TextStyleRun style;
        TLRPC.MessageEntity messageEntity;
        if (!(characterStyle instanceof URLSpanBrowser) || (style = ((URLSpanBrowser) characterStyle).getStyle()) == null || (messageEntity = style.urlEntity) == null) {
            spanStart = 0;
            spanEnd = 0;
            z = false;
        } else {
            spanStart = messageEntity.offset;
            spanEnd = messageEntity.length + spanStart;
            z = true;
        }
        if (!z) {
            spanStart = spannable.getSpanStart(characterStyle);
            spanEnd = spannable.getSpanEnd(characterStyle);
        }
        return new int[]{spanStart, spanEnd};
    }

    private boolean checkQuickShareMotionEvent(MotionEvent motionEvent) {
        ChatMessageCellDelegate chatMessageCellDelegate;
        if (!this.inQuickShareMode) {
            return false;
        }
        int action = motionEvent.getAction();
        if (action != 1 && action != 3) {
            if (action == 2 && (chatMessageCellDelegate = this.delegate) != null) {
                chatMessageCellDelegate.didQuickShareMove(this, getEventX(motionEvent), getEventY(motionEvent));
            }
            return true;
        }
        this.inQuickShareMode = false;
        ChatMessageCellDelegate chatMessageCellDelegate2 = this.delegate;
        if (chatMessageCellDelegate2 != null) {
            chatMessageCellDelegate2.didQuickShareEnd(this, getEventX(motionEvent), getEventY(motionEvent));
        }
        requestDisallowInterceptTouchEvent(false);
        return true;
    }

    private boolean checkAdminMotionEvent(MotionEvent motionEvent) {
        RectF rectF;
        ChatMessageCellDelegate chatMessageCellDelegate;
        Drawable drawable;
        boolean z = false;
        if (this.adminLayout == null || (rectF = this.boostCounterBounds) == null || (this.currentUser == null && this.currentChat == null)) {
            this.boostCounterPressed = false;
            return false;
        }
        boolean zContains = rectF.contains((int) getEventX(motionEvent), (int) getEventY(motionEvent));
        if (motionEvent.getAction() == 0) {
            SpannableString spannableString = new SpannableString(this.adminLayout.getText());
            BoostCounterSpan[] boostCounterSpanArr = (BoostCounterSpan[]) spannableString.getSpans(0, spannableString.length(), BoostCounterSpan.class);
            if (zContains && boostCounterSpanArr != null && boostCounterSpanArr.length > 0) {
                z = true;
            }
            this.boostCounterPressed = z;
            if (z && (drawable = this.boostCounterLayoutSelector) != null) {
                drawable.setHotspot((int) getEventX(motionEvent), (int) getEventY(motionEvent));
                this.boostCounterLayoutSelector.setState(this.pressedState);
            }
        } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            if (motionEvent.getAction() == 1 && this.boostCounterPressed && (chatMessageCellDelegate = this.delegate) != null) {
                chatMessageCellDelegate.didPressBoostCounter(this);
            }
            Drawable drawable2 = this.boostCounterLayoutSelector;
            if (drawable2 != null) {
                drawable2.setState(StateSet.NOTHING);
            }
            this.boostCounterPressed = false;
        }
        return this.boostCounterPressed;
    }

    private boolean checkRoundSeekbar(MotionEvent motionEvent) {
        float f;
        if (!MediaController.getInstance().isPlayingMessage(this.currentMessageObject) || !MediaController.getInstance().isMessagePaused()) {
            return false;
        }
        int eventX = (int) getEventX(motionEvent);
        int eventY = (int) getEventY(motionEvent);
        if (motionEvent.getAction() == 0) {
            float f2 = eventX;
            if (f2 < this.seekbarRoundX - AndroidUtilities.dp(20.0f) || f2 > this.seekbarRoundX + AndroidUtilities.dp(20.0f)) {
                float centerX = f2 - this.photoImage.getCenterX();
                float centerY = eventY - this.photoImage.getCenterY();
                float imageWidth = (this.photoImage.getImageWidth() - AndroidUtilities.dp(64.0f)) / 2.0f;
                f = (centerX * centerX) + (centerY * centerY);
                if (f < ((this.photoImage.getImageWidth() / 2.0f) * this.photoImage.getImageWidth()) / 2.0f && f > imageWidth * imageWidth) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    cancelCheckLongPress();
                    this.roundSeekbarTouched = 1;
                    invalidate();
                }
            } else {
                float f3 = eventY;
                if (f3 >= this.seekbarRoundY - AndroidUtilities.dp(20.0f) && f3 <= this.seekbarRoundY + AndroidUtilities.dp(20.0f)) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    cancelCheckLongPress();
                    this.roundSeekbarTouched = 1;
                    invalidate();
                } else {
                    float centerX2 = f2 - this.photoImage.getCenterX();
                    float centerY2 = eventY - this.photoImage.getCenterY();
                    float imageWidth2 = (this.photoImage.getImageWidth() - AndroidUtilities.dp(64.0f)) / 2.0f;
                    f = (centerX2 * centerX2) + (centerY2 * centerY2);
                    if (f < ((this.photoImage.getImageWidth() / 2.0f) * this.photoImage.getImageWidth()) / 2.0f) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                        cancelCheckLongPress();
                        this.roundSeekbarTouched = 1;
                        invalidate();
                    }
                }
            }
        } else if (this.roundSeekbarTouched == 1 && motionEvent.getAction() == 2) {
            float degrees = ((float) Math.toDegrees(Math.atan2(eventY - this.photoImage.getCenterY(), eventX - this.photoImage.getCenterX()))) + 90.0f;
            if (degrees < 0.0f) {
                degrees += 360.0f;
            }
            float f4 = degrees / 360.0f;
            if (Math.abs(this.currentMessageObject.audioProgress - f4) > 0.9f) {
                if (this.roundSeekbarOutAlpha == 0.0f) {
                    try {
                        performHapticFeedback(3, 2);
                    } catch (Exception unused) {
                    }
                }
                this.roundSeekbarOutAlpha = 1.0f;
                this.roundSeekbarOutProgress = this.currentMessageObject.audioProgress;
            }
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (jCurrentTimeMillis - this.lastSeekUpdateTime > 100) {
                MediaController.getInstance().seekToProgress(this.currentMessageObject, f4);
                this.lastSeekUpdateTime = jCurrentTimeMillis;
            }
            this.currentMessageObject.audioProgress = f4;
            updatePlayingMessageProgress();
        }
        if ((motionEvent.getAction() == 1 || motionEvent.getAction() == 3) && this.roundSeekbarTouched != 0) {
            if (motionEvent.getAction() == 1) {
                float degrees2 = ((float) Math.toDegrees(Math.atan2(eventY - this.photoImage.getCenterY(), eventX - this.photoImage.getCenterX()))) + 90.0f;
                if (degrees2 < 0.0f) {
                    degrees2 += 360.0f;
                }
                float f5 = degrees2 / 360.0f;
                this.currentMessageObject.audioProgress = f5;
                MediaController.getInstance().seekToProgress(this.currentMessageObject, f5);
                updatePlayingMessageProgress();
            }
            MediaController.getInstance().playMessage(this.currentMessageObject);
            this.roundSeekbarTouched = 0;
            getParent().requestDisallowInterceptTouchEvent(false);
        }
        return this.roundSeekbarTouched != 0;
    }

    void lambda$checkBotButtonMotionEvent$4(int i) {
        int i2 = this.pressedBotButton;
        if (i == i2) {
            BotButton botButton = this.botButtons.get(i2);
            if (botButton != null) {
                Drawable drawable = botButton.selectorDrawable;
                if (drawable != null) {
                    drawable.setState(StateSet.NOTHING);
                }
                botButton.setPressed(false);
                if (!this.currentMessageObject.scheduled) {
                    if (botButton.buttonCustom != null) {
                        cancelCheckLongPress();
                        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                        if (chatMessageCellDelegate != null) {
                            chatMessageCellDelegate.didLongPressCustomBotButton(this, botButton.buttonCustom);
                        }
                    } else if (botButton.button != null) {
                        cancelCheckLongPress();
                        ChatMessageCellDelegate chatMessageCellDelegate2 = this.delegate;
                        if (chatMessageCellDelegate2 != null) {
                            chatMessageCellDelegate2.didLongPressBotButton(this, botButton.button);
                        }
                    }
                }
            }
            this.pressedBotButton = -1;
            invalidateOutbounds();
        }
    }

    private boolean checkSummaryTouchEvent(MotionEvent motionEvent) {
        if (!this.drawSummaryReply || this.summaryTitle == null || this.summaryBounce == null || this.delegate == null) {
            return false;
        }
        float eventX = getEventX(motionEvent);
        float eventY = getEventY(motionEvent);
        if (motionEvent.getAction() == 0) {
            if (this.summarySelectorRect.contains(eventX, eventY)) {
                this.summaryBounce.setPressed(true);
                Drawable drawable = this.summaryReplySelector;
                if (drawable != null) {
                    drawable.setHotspot(eventX, eventY);
                    this.summaryReplySelector.setState(new int[]{R.attr.state_pressed, R.attr.state_enabled});
                }
            }
        } else if (motionEvent.getAction() == 2) {
            if (this.summaryBounce.isPressed() && !this.summarySelectorRect.contains(eventX, eventY)) {
                this.summaryBounce.setPressed(false);
                Drawable drawable2 = this.summaryReplySelector;
                if (drawable2 != null) {
                    drawable2.setState(new int[0]);
                }
            }
        } else if (motionEvent.getAction() == 1) {
            if (this.summaryBounce.isPressed()) {
                this.delegate.didPressSummarize(this, true);
            }
            this.summaryBounce.setPressed(false);
            Drawable drawable3 = this.summaryReplySelector;
            if (drawable3 != null) {
                drawable3.setState(new int[0]);
            }
        } else if (motionEvent.getAction() == 3) {
            this.summaryBounce.setPressed(false);
            Drawable drawable4 = this.summaryReplySelector;
            if (drawable4 != null) {
                drawable4.setState(new int[0]);
            }
        }
        return this.summaryBounce.isPressed();
    }

    private boolean checkAdminTouchEvent(MotionEvent motionEvent) {
        ChatMessageCellDelegate chatMessageCellDelegate;
        if (this.adminLayout == null || (chatMessageCellDelegate = this.delegate) == null || !chatMessageCellDelegate.canPerformActions()) {
            return false;
        }
        boolean zContains = this.adminLayoutRect.contains(motionEvent.getX(), motionEvent.getY());
        if (this.adminLayoutBounce == null) {
            this.adminLayoutBounce = new ButtonBounce(this);
        }
        if (motionEvent.getAction() == 0) {
            this.adminLayoutBounce.setPressed(zContains);
        } else if (motionEvent.getAction() == 2) {
            if (!zContains) {
                this.adminLayoutBounce.setPressed(false);
            }
        } else if (motionEvent.getAction() == 1) {
            if (this.adminLayoutBounce.isPressed()) {
                this.delegate.didPressAdmin(this);
            }
            this.adminLayoutBounce.setPressed(false);
        } else if (motionEvent.getAction() == 3) {
            this.adminLayoutBounce.setPressed(false);
        }
        return this.adminLayoutBounce.isPressed();
    }

    void lambda$checkReplyTouchEvent$5() {
        if (this.replyPressed && !this.replySelectorPressed && this.replySelectorCanBePressed) {
            this.replySelectorPressed = true;
            this.replySelector.setState(new int[]{R.attr.state_pressed, R.attr.state_enabled});
        }
    }

    public private boolean isPhotoDataChanged(MessageObject messageObject) {
        int i;
        String strFormapMapUrl;
        int i2;
        int i3 = messageObject.type;
        if (i3 == 0 || i3 == 14) {
            return false;
        }
        if (i3 == 4) {
            if (this.currentUrl == null) {
                return true;
            }
            TLRPC.GeoPoint geoPoint = messageObject.messageOwner.media.geo;
            double d = geoPoint.lat;
            double d2 = geoPoint._long;
            if (((int) messageObject.getDialogId()) != 0 || (i2 = SharedConfig.mapPreviewType) == 0) {
                i = -1;
            } else if (i2 == 1) {
                i = 4;
            } else if (i2 == 3) {
                i = 1;
            } else {
                i = -1;
            }
            TLRPC.MessageMedia messageMedia = messageObject.messageOwner.media;
            if (messageMedia instanceof TLRPC.TL_messageMediaGeoLive) {
                int iDp = this.backgroundWidth - AndroidUtilities.dp(21.0f);
                int iDp2 = AndroidUtilities.dp(195.0f);
                double d3 = (d * 3.141592653589793d) / 180.0d;
                double dAtan = ((1.5707963267948966d - (Math.atan(Math.exp(((Math.round(2.68435456E8d - ((Math.log((Math.sin(d3) + 1.0d) / (1.0d - Math.sin(d3))) * 8.544565944705395E7d) / 2.0d)) - ((long) (AndroidUtilities.dp(10.3f) << 6))) - 2.68435456E8d) / 8.544565944705395E7d)) * 2.0d)) * 180.0d) / 3.141592653589793d;
                int i4 = this.currentAccount;
                float f = AndroidUtilities.density;
                strFormapMapUrl = AndroidUtilities.formapMapUrl(i4, dAtan, d2, (int) (iDp / f), (int) (iDp2 / f), false, 15, i);
            } else {
                boolean zIsEmpty = TextUtils.isEmpty(messageMedia.title);
                int i5 = this.backgroundWidth;
                if (!zIsEmpty) {
                    int iDp3 = i5 - AndroidUtilities.dp(21.0f);
                    int iDp4 = AndroidUtilities.dp(195.0f);
                    int i6 = this.currentAccount;
                    float f2 = AndroidUtilities.density;
                    strFormapMapUrl = AndroidUtilities.formapMapUrl(i6, d, d2, (int) (iDp3 / f2), (int) (iDp4 / f2), true, 15, i);
                } else {
                    int iDp5 = i5 - AndroidUtilities.dp(12.0f);
                    int iDp6 = AndroidUtilities.dp(195.0f);
                    int i7 = this.currentAccount;
                    float f3 = AndroidUtilities.density;
                    strFormapMapUrl = AndroidUtilities.formapMapUrl(i7, d, d2, (int) (iDp5 / f3), (int) (iDp6 / f3), true, 15, i);
                }
            }
            return !strFormapMapUrl.equals(this.currentUrl);
        }
        TLRPC.PhotoSize photoSize = this.currentPhotoObject;
        if (photoSize == null || (photoSize.location instanceof TLRPC.TL_fileLocationUnavailable)) {
            return i3 == 1 || i3 == 20 || i3 == 5 || i3 == 3 || i3 == 8 || messageObject.isAnyKindOfSticker();
        }
        if (this.currentMessageObject == null || !this.photoNotSet) {
            return false;
        }
        return FileLoader.getInstance(this.currentAccount).getPathToMessage(this.currentMessageObject.messageOwner).exists();
    }

    public int getRepliesCount() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.messages.isEmpty()) {
            return this.currentMessagesGroup.messages.get(0).getRepliesCount();
        }
        return this.currentMessageObject.getRepliesCount();
    }

    private ArrayList<TLRPC.Peer> getRecentRepliers() {
        TLRPC.MessageReplies messageReplies;
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.messages.isEmpty() && (messageReplies = this.currentMessagesGroup.messages.get(0).messageOwner.replies) != null) {
            return messageReplies.recent_repliers;
        }
        TLRPC.MessageReplies messageReplies2 = this.currentMessageObject.messageOwner.replies;
        if (messageReplies2 != null) {
            return messageReplies2.recent_repliers;
        }
        return null;
    }

    public void updateAnimatedEmojis() {
        MessageObject messageObject;
        RichMessageLayout richMessageLayout;
        ArrayList<MessageObject.TextLayoutBlock> arrayList;
        if (!this.imageReceiversAttachState || (messageObject = this.currentMessageObject) == null) {
            return;
        }
        int cacheTypeForEnterView = messageObject.wasJustSent ? AnimatedEmojiDrawable.getCacheTypeForEnterView() : 0;
        MessageObject.TextLayoutBlocks textLayoutBlocks = this.captionLayout;
        if (textLayoutBlocks != null && (arrayList = textLayoutBlocks.textLayoutBlocks) != null) {
            this.animatedEmojiStack = AnimatedEmojiSpan.update(cacheTypeForEnterView, (View) this, false, this.animatedEmojiStack, arrayList);
        } else {
            ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
            this.animatedEmojiStack = AnimatedEmojiSpan.update(cacheTypeForEnterView, this, chatMessageCellDelegate == null || !chatMessageCellDelegate.canDrawOutboundsContent(), this.animatedEmojiStack, this.currentMessageObject.textLayoutBlocks);
        }
        MessageObject messageObject2 = this.currentMessageObject;
        if (messageObject2.type != 36 || (richMessageLayout = messageObject2.richLayout) == null) {
            return;
        }
        ChatMessageCellDelegate chatMessageCellDelegate2 = this.delegate;
        richMessageLayout.invalidateAnimatedEmojiInParent = chatMessageCellDelegate2 == null || !chatMessageCellDelegate2.canDrawOutboundsContent();
        this.currentMessageObject.richLayout.updateAnimatedEmojis(cacheTypeForEnterView);
    }

    private void setMessageContent(org.telegram.messenger.MessageObject r94, org.telegram.messenger.MessageObject.GroupedMessages r95, boolean r96, boolean r97, boolean r98, boolean r99) {
        void lambda$setMessageContent$7(long j, int i) {
        BotForumHelper.BotDraftAnimationsPool botDraftAnimationsPool = this.draftAnimationsPool;
        if (botDraftAnimationsPool != null) {
            botDraftAnimationsPool.removeAnimator(j, i);
        }
    }

    public void lambda$setMessageContentIfPoll$10(boolean z, long j) {
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = this.animatedInfoLayout2;
        if (animatedTextDrawable != null) {
            animatedTextDrawable.setText(LocaleController.formatPollEndTime((int) j, z), true);
        }
    }

    private void checkInstantButtonForPoll(boolean z) {
        boolean z2;
        CharSequence string;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        TLRPC.MessageMedia media = MessageObject.getMedia(messageObject);
        if (media instanceof TLRPC.TL_messageMediaPoll) {
            TLRPC.TL_messageMediaPoll tL_messageMediaPoll = (TLRPC.TL_messageMediaPoll) media;
            int i = 0;
            if (this.pollCheckBox != null) {
                int iMin = Math.min(tL_messageMediaPoll.poll.answers.size(), this.pollCheckBox.length);
                z2 = false;
                for (int i2 = 0; i2 < iMin; i2++) {
                    CheckBoxBase checkBoxBase = this.pollCheckBox[i2];
                    if (checkBoxBase != null && checkBoxBase.isChecked()) {
                        z2 = true;
                    }
                }
            } else {
                z2 = false;
            }
            TLRPC.PollResults pollResults = tL_messageMediaPoll.results;
            boolean z3 = (this.pollResultsPreview || this.pollVoted || this.pollClosed || !this.pollHasResults || pollResults == null || pollResults.total_voters == 0 || !tL_messageMediaPoll.poll.creator) ? false : true;
            boolean z4 = this.pollVoted;
            boolean z5 = (z4 || this.pollClosed || this.pollHasVoteRestrictions) ? false : true;
            TLRPC.Poll poll = tL_messageMediaPoll.poll;
            boolean z6 = poll.public_voters;
            if (this.pollInInputNewOption) {
                string = LocaleController.getString(org.telegram.messenger.R.string.PollButtonSave);
                i = 84;
            } else if (!z3 || (poll.multiple_choice && z5 && z2)) {
                if (poll.multiple_choice && z5) {
                    string = LocaleController.getString(org.telegram.messenger.R.string.PollSubmitVotesNoCaps);
                    i = 83;
                } else if (this.isBot || !z6 || ((!z4 || (this.pollHideResults && !this.pollHasResults)) && (!this.pollClosed || pollResults.total_voters == 0))) {
                    string = null;
                } else {
                    string = LocaleController.formatString(org.telegram.messenger.R.string.PollViewVotesX, Integer.valueOf(pollResults.total_voters));
                    i = 80;
                }
            } else if (!z6) {
                if (messageObject.forceShowPollResults) {
                    string = AndroidUtilities.replaceArrows(LocaleController.getString(org.telegram.messenger.R.string.PollViewBack), false, -AndroidUtilities.dp(2.6666667f), 0.0f, 1.0f);
                    i = 82;
                } else {
                    string = AndroidUtilities.replaceArrows(LocaleController.formatPluralString("PollViewVotesAsAdmin", pollResults.total_voters, new Object[0]), false);
                    i = 81;
                }
            } else {
                string = LocaleController.formatString(org.telegram.messenger.R.string.PollViewVotesX, Integer.valueOf(pollResults.total_voters));
                i = 80;
            }
            if (i != 0) {
                if (this.pollInstantButtonDrawable == null) {
                    PollInstantButtonDrawable pollInstantButtonDrawable = new PollInstantButtonDrawable(this, this.resourcesProvider);
                    this.pollInstantButtonDrawable = pollInstantButtonDrawable;
                    pollInstantButtonDrawable.setupCallbacks(this);
                }
                this.instantViewButtonText = string;
                this.pollInstantButtonDrawable.setButtonText(string, z);
                this.drawInstantView = true;
                this.drawInstantViewType = i;
                createInstantViewButton();
            }
            checkPollVoteSendingStatus(z);
        }
    }

    public void checkPollVoteSendingStatus(boolean z) {
        MessageObject messageObject;
        if (this.pollInstantButtonDrawable == null || (messageObject = this.currentMessageObject) == null || !messageObject.isPoll()) {
            return;
        }
        TLRPC.MessageMedia media = MessageObject.getMedia(this.currentMessageObject);
        boolean z2 = false;
        boolean z3 = media instanceof TLRPC.TL_messageMediaPoll ? ((TLRPC.TL_messageMediaPoll) media).poll.multiple_choice : false;
        PollInstantButtonDrawable pollInstantButtonDrawable = this.pollInstantButtonDrawable;
        if (z3 && SendMessagesHelper.getInstance(this.currentAccount).isSendingVote(this.currentMessageObject) != null) {
            z2 = true;
        }
        pollInstantButtonDrawable.setLoading(z2, z);
    }

    private boolean loopStickers() {
        return LiteMode.isEnabled(2);
    }

    private void calculateUnlockXY() {
        if (this.currentMessageObject.type != 20 || this.unlockLayout == null) {
            return;
        }
        this.unlockX = this.backgroundDrawableLeft + ((this.photoImage.getImageWidth() - this.unlockLayout.getWidth()) / 2.0f);
        this.unlockY = this.backgroundDrawableTop + this.photoImage.getImageY() + ((this.photoImage.getImageHeight() - this.unlockLayout.getHeight()) / 2.0f);
    }

    private void updateFlagSecure() {
        if (this.flagSecure == null) {
            Activity activityFindActivity = AndroidUtilities.findActivity(getContext());
            Window window = activityFindActivity == null ? null : activityFindActivity.getWindow();
            if (window != null) {
                FlagSecureReason flagSecureReason = new FlagSecureReason(window, new FlagSecureReason.FlagSecureCondition() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda18
                    @Override 
                    public final boolean run() {
                        return this.f$0.lambda$updateFlagSecure$11();
                    }
                });
                this.flagSecure = flagSecureReason;
                if (this.attachedToWindow) {
                    flagSecureReason.attach();
                }
            }
        }
        FlagSecureReason flagSecureReason2 = this.flagSecure;
        if (flagSecureReason2 != null) {
            flagSecureReason2.invalidate();
        }
    }

    public @Override // org.telegram.ui.Cells.BaseCell
    public boolean onLongPress() {
        int i;
        boolean zDidLongPressChannelAvatar;
        int i2;
        int i3;
        TLRPC.Message message;
        TLRPC.MessageReplyHeader messageReplyHeader;
        BotButton botButton;
        if (this.isRoundVideo && this.isPlayingRound && MediaController.getInstance().isPlayingMessage(this.currentMessageObject) && ((this.lastTouchX - this.photoImage.getCenterX()) * (this.lastTouchX - this.photoImage.getCenterX())) + ((this.lastTouchY - this.photoImage.getCenterY()) * (this.lastTouchY - this.photoImage.getCenterY())) < (this.photoImage.getImageWidth() / 2.0f) * (this.photoImage.getImageWidth() / 2.0f) && (this.lastTouchX > this.photoImage.getCenterX() + (this.photoImage.getImageWidth() / 4.0f) || this.lastTouchX < this.photoImage.getCenterX() - (this.photoImage.getImageWidth() / 4.0f))) {
            boolean z = this.lastTouchX > this.photoImage.getCenterX();
            if (this.videoPlayerRewinder == null) {
                this.videoForwardDrawable = new VideoForwardDrawable(true);
                this.videoPlayerRewinder = new OldVideoPlayerRewinder() { // from class: org.telegram.ui.Cells.ChatMessageCell.6
                    @Override 
                    public void onRewindCanceled() {
                        ChatMessageCell.this.onTouchEvent(MotionEvent.obtain(0L, 0L, 3, 0.0f, 0.0f, 0));
                        ChatMessageCell.this.videoForwardDrawable.setShowing(false);
                    }

                    @Override 
                    public void updateRewindProgressUi(long j, float f, boolean z2) {
                        ChatMessageCell.this.videoForwardDrawable.setTime(Math.abs(j));
                        if (z2) {
                            ChatMessageCell.this.currentMessageObject.audioProgress = f;
                            ChatMessageCell.this.updatePlayingMessageProgress();
                        }
                    }

                    @Override 
                    public void onRewindStart(boolean z2) {
                        ChatMessageCell.this.videoForwardDrawable.setDelegate(new VideoForwardDrawable.VideoForwardDrawableDelegate() { // from class: org.telegram.ui.Cells.ChatMessageCell.6.1
                            @Override // org.telegram.ui.Components.VideoForwardDrawable.VideoForwardDrawableDelegate
                            public void onAnimationEnd() {
                            }

                            @Override // org.telegram.ui.Components.VideoForwardDrawable.VideoForwardDrawableDelegate
                            public void invalidate() {
                                ChatMessageCell.this.invalidate();
                            }
                        });
                        ChatMessageCell.this.videoForwardDrawable.setOneShootAnimation(false);
                        ChatMessageCell.this.videoForwardDrawable.setLeftSide(!z2);
                        ChatMessageCell.this.videoForwardDrawable.setShowing(true);
                        ChatMessageCell.this.invalidate();
                    }
                };
                getParent().requestDisallowInterceptTouchEvent(true);
            }
            this.videoPlayerRewinder.startRewind(MediaController.getInstance().getVideoPlayer(), z, MediaController.getInstance().getPlaybackSpeed(false));
            return false;
        }
        Drawable drawable = this.replySelector;
        if (drawable != null) {
            this.replySelectorPressed = false;
            drawable.setState(StateSet.NOTHING);
            invalidate();
        }
        ButtonBounce buttonBounce = this.summaryBounce;
        if (buttonBounce != null) {
            buttonBounce.setPressed(false);
        }
        Drawable drawable2 = this.summaryReplySelector;
        if (drawable2 != null) {
            drawable2.setState(StateSet.NOTHING);
        }
        Drawable drawable3 = this.nameStatusSelector;
        if (drawable3 != null) {
            drawable3.setState(StateSet.NOTHING);
        }
        Drawable drawable4 = this.nameBadgeSelector;
        if (drawable4 != null) {
            drawable4.setState(StateSet.NOTHING);
        }
        Drawable drawable5 = this.nameLayoutSelector;
        if (drawable5 != null) {
            drawable5.setState(StateSet.NOTHING);
        }
        Drawable drawable6 = this.boostCounterLayoutSelector;
        if (drawable6 != null) {
            drawable6.setState(StateSet.NOTHING);
        }
        resetCodeSelectors();
        ButtonBounce buttonBounce2 = this.replyBounce;
        if (buttonBounce2 != null) {
            buttonBounce2.setPressed(false);
        }
        ButtonBounce buttonBounce3 = this.adminLayoutBounce;
        if (buttonBounce3 != null) {
            buttonBounce3.setPressed(false);
        }
        ButtonBounce buttonBounce4 = this.titleLabelBounce;
        if (buttonBounce4 != null) {
            buttonBounce4.setPressed(false);
        }
        ButtonBounce buttonBounce5 = this.factCheckWhatBounce;
        if (buttonBounce5 != null) {
            buttonBounce5.setPressed(false);
        }
        this.forwardNamePressed = false;
        ForwardBackground forwardBackground = this.forwardBg;
        if (forwardBackground != null) {
            forwardBackground.setPressed(false);
        }
        this.pressedEffect = false;
        ButtonBounce buttonBounce6 = this.effectDrawableBounce;
        if (buttonBounce6 != null) {
            buttonBounce6.setPressed(false);
        }
        if (this.pressedEmoji != null) {
            this.pressedEmoji = null;
        }
        int i4 = this.pressedVoteButton;
        if (i4 != -1) {
            PollButton pollButton = this.pollButtons.get(i4);
            if (this.delegate != null && pollButton != null) {
                if (pollButton.task != null && this.delegate.didLongPressToDoButton(this, pollButton.task)) {
                    return true;
                }
                if (pollButton.answer != null && this.delegate.didLongPressPollOption(this, pollButton.answer)) {
                    return true;
                }
            }
        }
        LinkSpanDrawable linkSpanDrawable = this.pressedFactCheckLink;
        if (linkSpanDrawable != null) {
            if (linkSpanDrawable.getSpan() instanceof URLSpanMono) {
                this.hadLongPress = true;
                this.delegate.didPressUrl(this, this.pressedFactCheckLink.getSpan(), true);
                return true;
            }
            boolean z2 = this.pressedFactCheckLink.getSpan() instanceof URLSpanNoUnderline;
            LinkSpanDrawable linkSpanDrawable2 = this.pressedFactCheckLink;
            if (z2) {
                URLSpanNoUnderline uRLSpanNoUnderline = (URLSpanNoUnderline) linkSpanDrawable2.getSpan();
                if (ChatActivity.isClickableLink(uRLSpanNoUnderline.getURL()) || uRLSpanNoUnderline.getURL().startsWith("/")) {
                    this.hadLongPress = true;
                    this.delegate.didPressUrl(this, this.pressedFactCheckLink.getSpan(), true);
                    return true;
                }
            } else if (linkSpanDrawable2.getSpan() instanceof URLSpan) {
                this.hadLongPress = true;
                this.delegate.didPressUrl(this, this.pressedFactCheckLink.getSpan(), true);
                return true;
            }
        }
        LinkSpanDrawable linkSpanDrawable3 = this.pressedLink;
        if (linkSpanDrawable3 != null) {
            if (linkSpanDrawable3.getSpan() instanceof URLSpanMono) {
                this.hadLongPress = true;
                this.delegate.didPressUrl(this, this.pressedLink.getSpan(), true);
                return true;
            }
            boolean z3 = this.pressedLink.getSpan() instanceof URLSpanNoUnderline;
            LinkSpanDrawable linkSpanDrawable4 = this.pressedLink;
            if (z3) {
                URLSpanNoUnderline uRLSpanNoUnderline2 = (URLSpanNoUnderline) linkSpanDrawable4.getSpan();
                if (ChatActivity.isClickableLink(uRLSpanNoUnderline2.getURL()) || uRLSpanNoUnderline2.getURL().startsWith("/") || uRLSpanNoUnderline2.getURL().startsWith("tel:")) {
                    this.hadLongPress = true;
                    this.delegate.didPressUrl(this, this.pressedLink.getSpan(), true);
                    return true;
                }
            } else if (linkSpanDrawable4.getSpan() instanceof URLSpan) {
                this.hadLongPress = true;
                this.delegate.didPressUrl(this, this.pressedLink.getSpan(), true);
                return true;
            }
        }
        resetPressedLink(-1);
        int i5 = this.pressedBotButton;
        if (i5 != -1 && (botButton = this.botButtons.get(i5)) != null && botButton.button != null) {
            Drawable drawable7 = botButton.selectorDrawable;
            if (drawable7 != null) {
                drawable7.setState(StateSet.NOTHING);
            }
            botButton.setPressed(false);
            this.delegate.didLongPressBotButton(this, botButton.button);
            return true;
        }
        if (this.buttonPressed != 0 || this.miniButtonPressed != 0 || this.videoButtonPressed != 0 || this.pressedBotButton != -1) {
            this.buttonPressed = 0;
            this.miniButtonPressed = 0;
            this.videoButtonPressed = 0;
            this.pressedBotButton = -1;
            invalidate();
        }
        if (this.replyPressed && !this.replyPanelIsForward) {
            this.hadLongPress = true;
            this.replyPressed = false;
            Drawable drawable8 = this.replySelector;
            if (drawable8 != null) {
                if (!this.replySelectorPressed) {
                    drawable8.setState(new int[]{R.attr.state_pressed, R.attr.state_enabled});
                    post(new Runnable() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda21
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onLongPress$12();
                        }
                    });
                } else {
                    drawable8.setState(new int[0]);
                }
                this.replySelectorPressed = false;
                this.replySelectorCanBePressed = false;
                invalidate();
            }
            ButtonBounce buttonBounce7 = this.replyBounce;
            if (buttonBounce7 != null) {
                buttonBounce7.setPressed(false);
            }
            playSoundEffect(0);
            if (this.delegate != null && (this.currentMessageObject.hasValidReplyMessageObject() || this.currentMessageObject.isReplyToStory() || this.hasReplyQuote || ((message = this.currentMessageObject.messageOwner) != null && (messageReplyHeader = message.reply_to) != null && messageReplyHeader.reply_from != null))) {
                this.delegate.didPressReplyMessage(this, this.currentMessageObject.getReplyMsgId(), this.replyTouchX, this.replyTouchY, true);
            }
            return true;
        }
        this.linkPreviewPressed = false;
        this.imagePressed = false;
        this.timePressed = false;
        this.gamePreviewPressed = false;
        this.giveawayMessageCell.setButtonPressed(false);
        this.giveawayResultsMessageCell.setButtonPressed(false);
        resetContactButtonsPressedState();
        if (this.pressedVoteButton != -1 || this.pollHintPressed || this.psaHintPressed || this.instantPressed || this.otherPressed || this.commentButtonPressed) {
            this.commentButtonPressed = false;
            this.instantPressed = false;
            setInstantButtonPressed(false);
            this.pressedVoteButton = -1;
            this.pollHintPressed = false;
            this.pollAddButtonPressed = -1;
            this.closeExplanationPressed = false;
            this.psaHintPressed = false;
            this.otherPressed = false;
            int i6 = 0;
            while (true) {
                Drawable[] drawableArr = this.selectorDrawable;
                if (i6 >= drawableArr.length) {
                    break;
                }
                Drawable drawable9 = drawableArr[i6];
                if (drawable9 != null) {
                    drawable9.setState(StateSet.NOTHING);
                }
                i6++;
            }
            Drawable drawable10 = this.linkPreviewSelector;
            if (drawable10 != null) {
                drawable10.setState(StateSet.NOTHING);
            }
            ButtonBounce buttonBounce8 = this.linkPreviewBounce;
            if (buttonBounce8 != null) {
                buttonBounce8.setPressed(false);
            }
            invalidate();
        }
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        if (chatMessageCellDelegate != null) {
            if (this.avatarPressed) {
                TLRPC.User user = this.currentUser;
                if (user == null) {
                    TLRPC.Chat chat = this.currentChat;
                    if (chat != null) {
                        TLRPC.MessageFwdHeader messageFwdHeader = this.currentMessageObject.messageOwner.fwd_from;
                        if (messageFwdHeader != null) {
                            if ((messageFwdHeader.flags & 16) != 0) {
                                i3 = messageFwdHeader.saved_from_msg_id;
                            } else {
                                i3 = messageFwdHeader.channel_post;
                            }
                            i2 = i3;
                        } else {
                            i2 = 0;
                        }
                        zDidLongPressChannelAvatar = chatMessageCellDelegate.didLongPressChannelAvatar(this, chat, i2, this.lastTouchX, this.lastTouchY);
                    }
                } else if (user.id != 0) {
                    zDidLongPressChannelAvatar = chatMessageCellDelegate.didLongPressUserAvatar(this, user, this.lastTouchX, this.lastTouchY);
                }
                if (!zDidLongPressChannelAvatar) {
                    this.delegate.didLongPress(this, this.lastTouchX, this.lastTouchY);
                }
            } else if (this.sideButtonPressed && (i = this.pressedSideButton) != 4 && i != 5 && i != 3 && i != 2) {
                chatMessageCellDelegate.didQuickShareStart(this, this.lastTouchX, this.lastTouchY);
                this.sideButtonPressed = false;
                this.pressedSideButton = 0;
                this.inQuickShareMode = true;
                requestDisallowInterceptTouchEvent(true);
                return false;
            }
            zDidLongPressChannelAvatar = false;
            if (!zDidLongPressChannelAvatar) {
                this.delegate.didLongPress(this, this.lastTouchX, this.lastTouchY);
            }
        }
        this.sideButtonPressed = false;
        this.pressedSideButton = 0;
        return true;
    }

    public {
                    super();
                }

                void lambda$drawContent$13(Canvas canvas) {
        this.radialProgress.draw(canvas);
    }

    private void checkStakedDice() {
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || !messageObject.isStakedDice() || this.playedDice) {
            return;
        }
        Drawable drawable = this.photoImage.getDrawable();
        if (drawable instanceof RLottieDrawable) {
            RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
            if (rLottieDrawable.isDice() && rLottieDrawable.hasBaseDice() && !this.playedDice && rLottieDrawable.isDiceRevealed()) {
                this.playedDice = true;
                ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
                if (chatMessageCellDelegate != null) {
                    chatMessageCellDelegate.forceUpdate(this, false);
                }
            }
        }
    }

    public void startRevealMedia() {
        startRevealMedia(this.photoImage.getImageX() + (this.photoImage.getImageWidth() / 2.0f), this.photoImage.getImageY() + (this.photoImage.getImageHeight() / 2.0f));
    }

    public void startRevealMedia(float f, float f2) {
        float fSqrt = (float) Math.sqrt(Math.pow(this.photoImage.getImageWidth(), 2.0d) + Math.pow(this.photoImage.getImageHeight(), 2.0d));
        this.mediaSpoilerRevealMaxRadius = fSqrt;
        startRevealMedia(f, f2, fSqrt);
    }

    private void startRevealMedia(float f, float f2, float f3) {
        final ChatMessageCell chatMessageCell;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject.isMediaSpoilersRevealed || this.mediaSpoilerRevealProgress != 0.0f) {
            return;
        }
        if (messageObject.type == 3) {
            messageObject.forceUpdate = true;
            messageObject.revealingMediaSpoilers = true;
            chatMessageCell = this;
            chatMessageCell.setMessageContent(messageObject, this.currentMessagesGroup, this.pinnedBottom, this.pinnedTop, this.firstInChat, this.lastInChatList);
            MessageObject messageObject2 = chatMessageCell.currentMessageObject;
            messageObject2.revealingMediaSpoilers = false;
            messageObject2.forceUpdate = false;
            if (chatMessageCell.currentMessagesGroup != null) {
                chatMessageCell.radialProgress.setProgress(0.0f, false);
            }
        } else {
            chatMessageCell = this;
        }
        chatMessageCell.mediaSpoilerRevealX = f;
        chatMessageCell.mediaSpoilerRevealY = f2;
        ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration((long) MathUtils.clamp(chatMessageCell.mediaSpoilerRevealMaxRadius * 0.3f, 250.0f, 550.0f));
        duration.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
        duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda13
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$startRevealMedia$14(valueAnimator);
            }
        });
        duration.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.ChatMessageCell.9
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ChatMessageCell.this.currentMessageObject.isMediaSpoilersRevealed = true;
                ChatMessageCell.this.invalidate();
            }
        });
        duration.start();
    }

    public private void drawBotButtons(Canvas canvas, ArrayList<BotButton> arrayList, int i) {
        int iDp;
        boolean z;
        boolean zIsSendingSuggestedMessageApproval;
        ChatMessageCellDelegate chatMessageCellDelegate;
        Canvas canvas2 = canvas;
        if (SizeNotifierFrameLayout.drawingBlur) {
            return;
        }
        int widthForButtons = getWidthForButtons();
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject != null && messageObject.isOutOwner()) {
            iDp = (getMeasuredWidth() - widthForButtons) - AndroidUtilities.dp(10.0f);
        } else {
            iDp = this.backgroundDrawableLeft + AndroidUtilities.dp((this.mediaBackground || this.drawPinnedBottom) ? 1.0f : 7.0f);
        }
        TransitionParams transitionParams = this.transitionParams;
        if (transitionParams.animateBackgroundBoundsInner) {
            iDp = (int) (iDp + transitionParams.deltaLeft);
        }
        int i2 = iDp;
        float f = 2.0f;
        float fDp = (this.layoutHeight - AndroidUtilities.dp(2.0f)) + this.transitionParams.deltaBottom;
        float f2 = 0.0f;
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            BotButton botButton = arrayList.get(i3);
            float f3 = botButton.y + botButton.height;
            if (f3 > f2) {
                f2 = f3;
            }
        }
        this.rect.set(0.0f, fDp, getMeasuredWidth(), f2 + fDp);
        if (i != 255) {
            canvas2.saveLayerAlpha(this.rect, i, 31);
        } else {
            canvas2.save();
        }
        int i4 = 0;
        boolean z2 = false;
        while (i4 < arrayList.size()) {
            BotButton botButton2 = arrayList.get(i4);
            float fDp2 = ((botButton2.y + this.layoutHeight) - AndroidUtilities.dp(f)) + this.transitionParams.deltaBottom;
            botButton2.getPressScale();
            if (botButton2.isSeparator) {
                float f4 = botButton2.x;
                float f5 = widthForButtons;
                float f6 = i2;
                float f7 = (f4 * f5) + f6;
                float f8 = (((f4 * f5) + f6) + (botButton2.width * f5)) - f7;
                float fDp3 = AndroidUtilities.dp(3.66f);
                float f9 = f8 - fDp3;
                int iRound = Math.round(f9 / AndroidUtilities.dp(8.66f));
                float f10 = f9 / iRound;
                applyServiceShaderMatrix();
                float fDp4 = AndroidUtilities.dp(1.0f);
                int i5 = 0;
                while (i5 < iRound + 1) {
                    float f11 = f10;
                    float f12 = (i5 * f11) + f7;
                    float f13 = fDp3;
                    float f14 = f7;
                    this.rect.set(f12, fDp2, f12 + f13, botButton2.height + fDp2);
                    canvas2.drawRoundRect(this.rect, fDp4, fDp4, getThemedPaint("paintChatActionBackground"));
                    if (hasGradientService()) {
                        canvas2.drawRoundRect(this.rect, fDp4, fDp4, Theme.chat_actionBackgroundGradientDarkenPaint);
                    }
                    i5++;
                    f10 = f11;
                    fDp3 = f13;
                    f7 = f14;
                }
            } else {
                RectF rectF = this.rect;
                float f15 = botButton2.x;
                float f16 = widthForButtons;
                float f17 = i2;
                rectF.set((f15 * f16) + f17, fDp2, (f15 * f16) + f17 + (botButton2.width * f16), botButton2.height + fDp2);
                applyServiceShaderMatrix();
                TLRPC.KeyboardButton keyboardButton = botButton2.button;
                boolean zIsSendingSuggestedMessageApproval2 = (((keyboardButton instanceof TLRPC.TL_keyboardButtonCallback) || (keyboardButton instanceof TLRPC.TL_keyboardButtonGame) || (keyboardButton instanceof TLRPC.TL_keyboardButtonBuy) || (keyboardButton instanceof TLRPC.TL_keyboardButtonUrlAuth)) && SendMessagesHelper.getInstance(this.currentAccount).isSendingCallback(this.currentMessageObject, botButton2.button)) || ((botButton2.button instanceof TLRPC.TL_keyboardButtonRequestGeoLocation) && SendMessagesHelper.getInstance(this.currentAccount).isSendingCurrentLocation(this.currentMessageObject, botButton2.button)) || ((botButton2.button instanceof TLRPC.TL_keyboardButtonUrl) && (chatMessageCellDelegate = this.delegate) != null && chatMessageCellDelegate.isProgressLoading(this, 3) && this.delegate.getProgressLoadingBotButtonUrl(this) == botButton2.button.url);
                BotInlineKeyboard.ButtonCustom buttonCustom = botButton2.buttonCustom;
                if (buttonCustom == null || this.currentMessageObject == null) {
                    z = false;
                    zIsSendingSuggestedMessageApproval = zIsSendingSuggestedMessageApproval2;
                } else {
                    int i6 = buttonCustom.id;
                    if (i6 == 2) {
                        zIsSendingSuggestedMessageApproval = zIsSendingSuggestedMessageApproval2 | MessagesController.getInstance(this.currentAccount).isSendingSuggestedMessageApproval(this.currentMessageObject.getDialogId(), this.currentMessageObject.getId(), true);
                        z = false;
                    } else {
                        if (i6 == 1) {
                            z = false;
                            zIsSendingSuggestedMessageApproval2 |= MessagesController.getInstance(this.currentAccount).isSendingSuggestedMessageApproval(this.currentMessageObject.getDialogId(), this.currentMessageObject.getId(), false);
                        } else {
                            z = false;
                        }
                        zIsSendingSuggestedMessageApproval = zIsSendingSuggestedMessageApproval2;
                    }
                }
                if (botButton2.draw(canvas2, this.rect, zIsSendingSuggestedMessageApproval, (this.hasInvoicePreview && this.hasInvoicePrice) ? true : z, this.resourcesProvider)) {
                    z2 = true;
                }
            }
            i4++;
            canvas2 = canvas;
            f = 2.0f;
        }
        canvas.restore();
        if (z2) {
            invalidateOutbounds();
        }
    }

    private boolean allowDrawPhotoImage() {
        return !this.currentMessageObject.hasMediaSpoilers() || this.currentMessageObject.isMediaSpoilersRevealed || this.mediaSpoilerRevealProgress != 0.0f || this.blurredPhotoImage.getBitmap() == null;
    }

    public void layoutTextXY(boolean z) {
        int iDp;
        int i;
        int iDp2;
        int iDp3;
        if (this.currentMessageObject.isOutOwner()) {
            this.textX = (z ? (int) (this.backgroundDrawableLeft + this.transitionParams.deltaLeft) : getCurrentBackgroundLeft()) + AndroidUtilities.dp(11.0f) + getExtraTextX();
        } else {
            int currentBackgroundLeft = z ? (int) (this.backgroundDrawableLeft + this.transitionParams.deltaLeft) : getCurrentBackgroundLeft();
            if (this.currentMessageObject.type == 19) {
                iDp = 0;
            } else {
                iDp = AndroidUtilities.dp((this.mediaBackground || !this.drawPinnedBottom) ? 17.0f : 11.0f);
            }
            this.textX = currentBackgroundLeft + iDp + getExtraTextX();
        }
        if (this.hasGamePreview) {
            this.textX += AndroidUtilities.dp(11.0f);
            int iDp4 = AndroidUtilities.dp(14.0f) + this.namesOffset;
            this.textY = iDp4;
            StaticLayout staticLayout = this.siteNameLayout;
            if (staticLayout != null) {
                this.textY = iDp4 + staticLayout.getLineBottom(staticLayout.getLineCount() - 1);
            }
        } else if (this.hasInvoicePreview) {
            int iDp5 = AndroidUtilities.dp(14.0f) + this.namesOffset;
            this.textY = iDp5;
            StaticLayout staticLayout2 = this.siteNameLayout;
            if (staticLayout2 != null) {
                this.textY = iDp5 + staticLayout2.getLineBottom(staticLayout2.getLineCount() - 1);
            }
        } else if (this.currentMessageObject.type == 19) {
            this.textY = AndroidUtilities.dp(6.0f) + this.namesOffset;
            if (!this.currentMessageObject.isOut()) {
                this.textX = getCurrentBackgroundLeft();
            } else {
                this.textX -= AndroidUtilities.dp(4.0f);
            }
        } else {
            int iDp6 = AndroidUtilities.dp(8.0f) + this.namesOffset;
            this.textY = iDp6;
            MessageObject messageObject = this.currentMessageObject;
            if (messageObject.type == 36) {
                RichMessageLayout richMessageLayout = messageObject.richLayout;
                if (richMessageLayout != null && richMessageLayout.startsWithMedia()) {
                    this.textY -= AndroidUtilities.dp(3.0f);
                }
            } else {
                if (messageObject.hasCodeAtTop && (i = SharedConfig.bubbleRadius) > 10) {
                    this.textY = iDp6 + AndroidUtilities.dp(i < 15 ? 1.0f : 2.0f);
                }
                if (this.currentMessageObject.hasCodeAtTop && this.namesOffset > 0) {
                    this.textY += AndroidUtilities.dp(5.0f);
                }
            }
        }
        if (this.currentMessageObject.isSponsored()) {
            this.linkPreviewY = this.textY + AndroidUtilities.dp(14.0f);
        } else {
            boolean z2 = this.linkPreviewAbove;
            int i2 = this.textY;
            if (z2) {
                this.linkPreviewY = i2 + AndroidUtilities.dp(10.0f);
                this.textY += this.linkPreviewHeight + AndroidUtilities.dp(13.0f);
                if (this.drawInstantView && !this.hasInvoicePreview && !this.currentMessageObject.isGiveawayOrGiveawayResults()) {
                    this.textY += AndroidUtilities.dp(44.0f);
                }
            } else {
                this.linkPreviewY = i2 + this.currentMessageObject.textHeight(this.transitionParams) + AndroidUtilities.dp(10.0f);
            }
        }
        if (this.linkPreviewAbove) {
            iDp2 = this.textY + this.currentMessageObject.textHeight(this.transitionParams);
            iDp3 = AndroidUtilities.dp(10.0f);
        } else {
            iDp2 = this.linkPreviewY + this.linkPreviewHeight + AndroidUtilities.dp(this.drawInstantView ? 46.0f : 0.0f);
            iDp3 = AndroidUtilities.dp(this.linkPreviewHeight <= 0 ? -8.0f : 4.0f);
        }
        this.factCheckY = iDp2 + iDp3;
        this.unmovedTextX = this.textX;
        if (this.currentMessageObject.textXOffset == 0.0f || this.replyNameLayout == null) {
            return;
        }
        int iDp7 = this.backgroundWidth - AndroidUtilities.dp(31.0f);
        MessageObject messageObject2 = this.currentMessageObject;
        int iDp8 = iDp7 - messageObject2.textWidth;
        if (!this.hasNewLineForTime) {
            iDp8 -= this.timeWidth + AndroidUtilities.dp((messageObject2.isOutOwner() ? 20 : 0) + 4);
        }
        if (iDp8 > 0) {
            this.textX += iDp8 - getExtraTimeX();
        }
    }

    public void drawMessageText(Canvas canvas) {
        float f;
        Canvas canvas2;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.isSponsored()) {
            return;
        }
        int i = this.textY;
        float fTextHeight = i;
        TransitionParams transitionParams = this.transitionParams;
        if (transitionParams.animateTextY) {
            float f2 = transitionParams.animateFromTextY;
            float f3 = transitionParams.animateChangeProgress;
            fTextHeight = (f2 * (1.0f - f3)) + (i * f3);
        }
        if (transitionParams.animateChangeProgress != 1.0f && transitionParams.animateMessageText) {
            canvas.save();
            Theme.MessageDrawable messageDrawable = this.currentBackgroundDrawable;
            if (messageDrawable != null) {
                Rect bounds = messageDrawable.getBounds();
                if (this.currentMessageObject.isOutOwner() && !this.mediaBackground && !this.pinnedBottom) {
                    canvas.clipRect(bounds.left + AndroidUtilities.dp(4.0f), bounds.top + AndroidUtilities.dp(4.0f), bounds.right - AndroidUtilities.dp(10.0f), bounds.bottom - AndroidUtilities.dp(4.0f));
                } else {
                    canvas.clipRect(bounds.left + AndroidUtilities.dp(4.0f), bounds.top + AndroidUtilities.dp(4.0f), bounds.right - AndroidUtilities.dp(4.0f), bounds.bottom - AndroidUtilities.dp(4.0f));
                }
            }
            MultiLayoutTypingAnimator multiLayoutTypingAnimator = this.botDraftTypingAnimator;
            if (multiLayoutTypingAnimator != null && multiLayoutTypingAnimator.isRunning()) {
                float f4 = this.textX;
                MessageObject messageObject2 = this.currentMessageObject;
                drawMessageText(f4, fTextHeight, canvas, messageObject2.textLayoutBlocks, messageObject2.textXOffset, true, 1.0f, true, false, false);
                canvas2 = canvas;
            } else {
                drawMessageText(this.textX, fTextHeight, canvas, this.transitionParams.animateOutTextBlocks, this.transitionParams.animateOutTextXOffset, false, 1.0f - this.transitionParams.animateChangeProgress, true, false, false);
                float f5 = this.textX;
                MessageObject messageObject3 = this.currentMessageObject;
                canvas2 = canvas;
                drawMessageText(f5, fTextHeight, canvas2, messageObject3.textLayoutBlocks, messageObject3.textXOffset, true, this.transitionParams.animateChangeProgress, true, false, false);
            }
            canvas2.restore();
            return;
        }
        boolean z = transitionParams.animateLinkAbove;
        if (z && this.currentBackgroundDrawable != null) {
            if (z) {
                float f6 = i;
                float fTextHeight2 = (this.linkPreviewAbove ? 1 : -1) * this.currentMessageObject.textHeight(transitionParams);
                TransitionParams transitionParams2 = this.transitionParams;
                f = (fTextHeight2 * (1.0f - transitionParams2.animateChangeProgress)) + f6;
                fTextHeight = transitionParams2.animateFromTextY - (((this.linkPreviewAbove ? 1 : -1) * this.currentMessageObject.textHeight(transitionParams2)) * this.transitionParams.animateChangeProgress);
            } else {
                f = fTextHeight;
            }
            canvas.save();
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(this.currentBackgroundDrawable.getBounds());
            if (this.currentMessageObject.isOutOwner() && !this.mediaBackground && !this.pinnedBottom) {
                rectF.left += AndroidUtilities.dp(4.0f);
                rectF.right -= AndroidUtilities.dp(10.0f);
            } else {
                rectF.left += AndroidUtilities.dp(4.0f);
                rectF.right -= AndroidUtilities.dp(4.0f);
            }
            float f7 = rectF.left;
            TransitionParams transitionParams3 = this.transitionParams;
            float f8 = transitionParams3.animateFromTextY;
            canvas.clipRect(f7, f8, rectF.right, this.currentMessageObject.textHeight(transitionParams3) + f8 + AndroidUtilities.dp(4.0f));
            float f9 = this.textX;
            MessageObject messageObject4 = this.currentMessageObject;
            drawMessageText(f9, fTextHeight, canvas, messageObject4.textLayoutBlocks, messageObject4.textXOffset, false, 1.0f - this.transitionParams.animateChangeProgress, true, false, false);
            canvas.restore();
            canvas.save();
            rectF.set(this.currentBackgroundDrawable.getBounds());
            if (this.currentMessageObject.isOutOwner() && !this.mediaBackground && !this.pinnedBottom) {
                rectF.left += AndroidUtilities.dp(4.0f);
                rectF.right -= AndroidUtilities.dp(10.0f);
            } else {
                rectF.left += AndroidUtilities.dp(4.0f);
                rectF.right -= AndroidUtilities.dp(4.0f);
            }
            float f10 = rectF.left;
            int i2 = this.textY;
            canvas.clipRect(f10, i2, rectF.right, i2 + this.currentMessageObject.textHeight(this.transitionParams) + AndroidUtilities.dp(4.0f));
            float f11 = this.textX;
            MessageObject messageObject5 = this.currentMessageObject;
            drawMessageText(f11, f, canvas, messageObject5.textLayoutBlocks, messageObject5.textXOffset, true, 1.0f, true, false, false);
            canvas.restore();
            return;
        }
        float f12 = this.textX;
        MessageObject messageObject6 = this.currentMessageObject;
        drawMessageText(f12, fTextHeight, canvas, messageObject6.textLayoutBlocks, messageObject6.textXOffset, true, 1.0f, true, false, false);
    }

    public void drawMessageText(Canvas canvas, ArrayList<MessageObject.TextLayoutBlock> arrayList, boolean z, float f, boolean z2) {
        int i = this.textY;
        float f2 = i;
        TransitionParams transitionParams = this.transitionParams;
        if (transitionParams.animateTextY) {
            float f3 = transitionParams.animateFromTextY;
            float f4 = transitionParams.animateChangeProgress;
            f2 = (f3 * (1.0f - f4)) + (i * f4);
        }
        float f5 = this.textX;
        MessageObject messageObject = this.currentMessageObject;
        drawMessageText(f5, f2, canvas, arrayList, messageObject == null ? 0.0f : messageObject.textXOffset, z, f, false, z2, false);
    }

    @SuppressLint({"Range"})
    public void drawMessageText(float f, float f2, Canvas canvas, ArrayList<MessageObject.TextLayoutBlock> arrayList, float f3, boolean z, float f4, boolean z2, boolean z3, boolean z4) {
        drawMessageText(f, f2, canvas, arrayList, f3, z, f4, z2, z3, z4, false);
    }

    public boolean checkNeedDrawShareButton(MessageObject messageObject) {
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        boolean z;
        if (this.isReportChat || messageObject == null) {
            return false;
        }
        if ((messageObject.deleted && !messageObject.deletedByThanos) || messageObject.isSponsored()) {
            return false;
        }
        if (this.currentMessagesGroup != null && (groupedMessagePosition = this.currentPosition) != null) {
            int i = groupedMessagePosition.flags;
            if ((i & 8) != 0) {
                z = (i & (messageObject.isOutOwner() ? 1 : 2)) != 0;
            }
            if (!this.currentMessagesGroup.isDocuments && !z) {
                return false;
            }
        }
        return messageObject.needDrawShareButton();
    }

    public boolean shouldHideShareButton(MessageObject messageObject, boolean z) {
        return ExteraConfig.getHideShareButton() || z;
    }

    public boolean isInsideBackground(float f, float f2) {
        if (this.currentBackgroundDrawable == null) {
            return false;
        }
        int i = this.backgroundDrawableLeft;
        return f >= ((float) i) && f <= ((float) (i + this.backgroundDrawableRight));
    }

    private void updateCurrentUserAndChat() {
        TLRPC.Peer peer;
        if (this.currentMessageObject == null) {
            return;
        }
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        TLRPC.MessageFwdHeader messageFwdHeader = this.currentMessageObject.messageOwner.fwd_from;
        long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        if (messageFwdHeader != null && (messageFwdHeader.from_id instanceof TLRPC.TL_peerChannel) && (this.currentMessageObject.getDialogId() == clientUserId || this.currentMessageObject.getDialogId() == UserObject.REPLY_BOT)) {
            this.currentChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(messageFwdHeader.from_id.channel_id));
            return;
        }
        if (messageFwdHeader != null && this.currentMessageObject.getDialogId() == UserObject.VERIFY) {
            long peerDialogId = DialogObject.getPeerDialogId(messageFwdHeader.from_id);
            if (peerDialogId >= 0) {
                this.currentUser = messagesController.getUser(Long.valueOf(peerDialogId));
                return;
            } else {
                this.currentChat = messagesController.getChat(Long.valueOf(-peerDialogId));
                return;
            }
        }
        if (messageFwdHeader != null && (peer = messageFwdHeader.saved_from_peer) != null) {
            long j = peer.user_id;
            if (j != 0) {
                if (!this.isSavedChat) {
                    TLRPC.Peer peer2 = messageFwdHeader.from_id;
                    if (peer2 instanceof TLRPC.TL_peerUser) {
                        this.currentUser = messagesController.getUser(Long.valueOf(peer2.user_id));
                        return;
                    }
                }
                this.currentUser = messagesController.getUser(Long.valueOf(j));
                return;
            }
            if (peer.channel_id != 0) {
                if (this.currentMessageObject.isSavedFromMegagroup()) {
                    TLRPC.Peer peer3 = messageFwdHeader.from_id;
                    if (peer3 instanceof TLRPC.TL_peerUser) {
                        this.currentUser = messagesController.getUser(Long.valueOf(peer3.user_id));
                        return;
                    }
                }
                this.currentChat = messagesController.getChat(Long.valueOf(messageFwdHeader.saved_from_peer.channel_id));
                return;
            }
            long j2 = peer.chat_id;
            if (j2 != 0) {
                TLRPC.Peer peer4 = messageFwdHeader.from_id;
                if (peer4 instanceof TLRPC.TL_peerUser) {
                    this.currentUser = messagesController.getUser(Long.valueOf(peer4.user_id));
                    return;
                } else {
                    this.currentChat = messagesController.getChat(Long.valueOf(j2));
                    return;
                }
            }
            return;
        }
        if (messageFwdHeader != null && (messageFwdHeader.from_id instanceof TLRPC.TL_peerUser) && (messageFwdHeader.imported || this.currentMessageObject.getDialogId() == clientUserId)) {
            this.currentUser = messagesController.getUser(Long.valueOf(messageFwdHeader.from_id.user_id));
            return;
        }
        if (messageFwdHeader != null && !TextUtils.isEmpty(messageFwdHeader.saved_from_name) && (messageFwdHeader.imported || this.currentMessageObject.getDialogId() == clientUserId)) {
            TLRPC.TL_user tL_user = new TLRPC.TL_user();
            this.currentUser = tL_user;
            tL_user.first_name = messageFwdHeader.saved_from_name;
            return;
        }
        if (messageFwdHeader != null && !TextUtils.isEmpty(messageFwdHeader.from_name) && (messageFwdHeader.imported || this.currentMessageObject.getDialogId() == clientUserId)) {
            TLRPC.TL_user tL_user2 = new TLRPC.TL_user();
            this.currentUser = tL_user2;
            tL_user2.first_name = messageFwdHeader.from_name;
            return;
        }
        long dialogId = this.currentMessageObject.getDialogId();
        long fromChatId = this.currentMessageObject.getFromChatId();
        TLRPC.Chat chat = DialogObject.isChatDialog(fromChatId) ? messagesController.getChat(Long.valueOf(-fromChatId)) : null;
        TLRPC.Chat chat2 = DialogObject.isChatDialog(dialogId) ? messagesController.getChat(Long.valueOf(-dialogId)) : null;
        if (DialogObject.isEncryptedDialog(this.currentMessageObject.getDialogId())) {
            if (this.currentMessageObject.isOutOwner()) {
                this.currentUser = UserConfig.getInstance(this.currentAccount).getCurrentUser();
                return;
            }
            TLRPC.EncryptedChat encryptedChat = messagesController.getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(this.currentMessageObject.getDialogId())));
            if (encryptedChat != null) {
                this.currentUser = messagesController.getUser(Long.valueOf(encryptedChat.user_id));
                return;
            }
            return;
        }
        if (DialogObject.isUserDialog(fromChatId) && (!this.currentMessageObject.messageOwner.post || (chat != null && chat.signature_profiles))) {
            this.currentUser = messagesController.getUser(Long.valueOf(fromChatId));
            return;
        }
        if (this.currentMessageObject.messageOwner.post && chat2 != null && !chat2.signature_profiles) {
            this.currentChat = chat2;
            return;
        }
        if (DialogObject.isChatDialog(fromChatId)) {
            this.currentChat = chat;
            return;
        }
        TLRPC.Message message = this.currentMessageObject.messageOwner;
        if (message.post) {
            this.currentChat = messagesController.getChat(Long.valueOf(message.peer_id.channel_id));
        }
    }

    public void setBackgroundTopY(boolean z) {
        int i;
        int measuredHeight;
        int i2 = 0;
        while (i2 < 2) {
            if (i2 == 1 && !z) {
                return;
            }
            Theme.MessageDrawable messageDrawable = i2 == 0 ? this.currentBackgroundDrawable : this.currentBackgroundSelectedDrawable;
            if (messageDrawable != null) {
                int parentWidth = this.parentWidth;
                int i3 = this.parentHeight;
                if (i3 == 0) {
                    parentWidth = getParentWidth();
                    i3 = AndroidUtilities.displaySize.y;
                    if (getParent() instanceof View) {
                        View view = (View) getParent();
                        int measuredWidth = view.getMeasuredWidth();
                        measuredHeight = view.getMeasuredHeight();
                        i = measuredWidth;
                    } else {
                        i = parentWidth;
                        measuredHeight = i3;
                    }
                } else {
                    i = parentWidth;
                    measuredHeight = i3;
                }
                float y = z ? getY() : getTop();
                float f = this.parentViewTopOffset;
                messageDrawable.setTop((int) (y + f), i, measuredHeight, (int) f, this.blurredViewTopOffset, this.blurredViewBottomOffset, this.pinnedTop, this.pinnedBottom || this.transitionParams.changePinnedBottomProgress != 1.0f);
                messageDrawable.setBotButtonsBottom(hasInlineBotButtons());
            }
            i2++;
        }
    }

    private void drawAnimatedEmojiMessageText(float f, float f2, Canvas canvas, ArrayList<MessageObject.TextLayoutBlock> arrayList, AnimatedEmojiSpan.EmojiGroupedSpans emojiGroupedSpans, boolean z, float f3, float f4, boolean z2) {
        int size;
        int i;
        float backgroundDrawableRight;
        int i2;
        int i3;
        float fCollapsed;
        float f5;
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        int parentWidth;
        int iDp;
        Canvas canvas2 = canvas;
        ArrayList<MessageObject.TextLayoutBlock> arrayList2 = arrayList;
        if (this.currentMessageObject == null || arrayList2 == null || arrayList2.isEmpty()) {
            return;
        }
        float f6 = 0.0f;
        if (f3 == 0.0f) {
            return;
        }
        int i4 = 0;
        if (z && !z2) {
            if (this.fullyDraw) {
                this.firstVisibleBlockNum = 0;
                this.lastVisibleBlockNum = arrayList2.size();
            }
            i = this.firstVisibleBlockNum;
            size = this.lastVisibleBlockNum;
        } else {
            size = arrayList2.size();
            i = 0;
        }
        int i5 = size;
        if (this.currentMessagesGroup == null || (groupedMessagePosition = this.currentPosition) == null) {
            backgroundDrawableRight = getBackgroundDrawableRight() + this.transitionParams.deltaRight;
        } else {
            int i6 = groupedMessagePosition.flags;
            if ((i6 & 1) != 0 && (i6 & 2) != 0) {
                backgroundDrawableRight = getBackgroundDrawableRight() + this.transitionParams.deltaRight;
            } else {
                if (AndroidUtilities.isTablet()) {
                    parentWidth = AndroidUtilities.getMinTabletSide();
                } else {
                    parentWidth = getParentWidth();
                }
                int iCeil = 0;
                for (int i7 = 0; i7 < this.currentMessagesGroup.posArray.size(); i7++) {
                    MessageObject.GroupedMessagePosition groupedMessagePosition2 = this.currentMessagesGroup.posArray.get(i7);
                    if (groupedMessagePosition2.minY != 0) {
                        break;
                    }
                    iCeil = (int) (((double) iCeil) + Math.ceil(((groupedMessagePosition2.pw + groupedMessagePosition2.leftSpanOffset) / 1000.0f) * parentWidth));
                }
                if (!this.mediaBackground && this.currentMessageObject.isOutOwner()) {
                    iDp = (this.backgroundDrawableLeft + iCeil) - AndroidUtilities.dp(6.0f);
                } else {
                    iDp = this.backgroundDrawableLeft + iCeil;
                }
                backgroundDrawableRight = iDp - (getExtraTextX() + AndroidUtilities.dp((this.isAvatarVisible ? 48 : 0) + 8));
            }
        }
        float fDp = (backgroundDrawableRight - (AndroidUtilities.dp(10 + ((!this.currentMessageObject.isOutOwner() || this.mediaBackground || this.drawPinnedBottom) ? 0 : 6)) + getExtraTextX())) - f;
        int i8 = i;
        while (i8 <= i5 && i8 < arrayList2.size()) {
            if (i8 < 0) {
                f5 = f6;
            } else {
                MessageObject.TextLayoutBlock textLayoutBlock = arrayList2.get(i8);
                int saveCount = canvas2.getSaveCount();
                canvas2.save();
                canvas2.translate(f - (textLayoutBlock.isRtl() ? (int) Math.ceil(f4) : i4), f2 + textLayoutBlock.padTop + textLayoutBlock.textYOffset(arrayList2, this.transitionParams) + this.transitionYOffsetForDrawables);
                float fTextYOffset = f2 + textLayoutBlock.textYOffset(arrayList2, this.transitionParams) + this.transitionYOffsetForDrawables;
                boolean z3 = this.transitionParams.messageEntering;
                int i9 = this.currentMessageObject.isOutOwner() ? Theme.key_chat_messageTextOut : Theme.key_chat_messageTextIn;
                if (this.currentMessageObject.shouldDrawWithoutBackground()) {
                    i9 = Theme.key_windowBackgroundWhiteBlackText;
                }
                int iDp2 = (int) (textLayoutBlock.maxRight + ((float) AndroidUtilities.dp(24.0f)) > 0.7f * fDp ? fDp : textLayoutBlock.maxRight + AndroidUtilities.dp(24.0f));
                if (textLayoutBlock.quoteCollapse) {
                    RectF rectF = AndroidUtilities.rectTmp;
                    rectF.set(f6, (-textLayoutBlock.padTop) + AndroidUtilities.dp(textLayoutBlock.first ? 4.66f : 3.0f), iDp2, textLayoutBlock.height(this.transitionParams) + AndroidUtilities.dp(4.0f));
                    rectF.offset(textLayoutBlock.isRtl() ? f4 - AndroidUtilities.dp(10.0f) : f6, f6);
                    ButtonBounce buttonBounce = textLayoutBlock.collapsedBounce;
                    if (buttonBounce != null) {
                        float scale = buttonBounce.getScale(0.01f);
                        canvas2.scale(scale, scale, rectF.centerX(), rectF.centerY());
                    }
                }
                if (!textLayoutBlock.quoteCollapse || textLayoutBlock.height <= textLayoutBlock.collapsedHeight) {
                    i2 = i9;
                    i3 = iDp2;
                    fCollapsed = 1.0f;
                } else {
                    fCollapsed = textLayoutBlock.collapsed(this.transitionParams);
                    i2 = i9;
                    i3 = iDp2;
                    canvas2.saveLayerAlpha(0.0f, 0.0f, iDp2, textLayoutBlock.height(this.transitionParams) - 1, 255, 31);
                }
                float f7 = fCollapsed;
                canvas2 = canvas;
                AnimatedEmojiSpan.drawAnimatedEmojis(canvas2, textLayoutBlock.textLayout, emojiGroupedSpans, 0.0f, textLayoutBlock.spoilers, 0.0f, 0.0f, fTextYOffset, f3, getAdaptiveEmojiColorFilter(i4, getThemedColor(i2)));
                if (!textLayoutBlock.quoteCollapse || textLayoutBlock.height <= textLayoutBlock.collapsedHeight) {
                    f5 = 0.0f;
                } else {
                    if (this.clip == null) {
                        this.clip = new GradientClip();
                    }
                    canvas2.save();
                    RectF rectF2 = AndroidUtilities.rectTmp;
                    f5 = 0.0f;
                    rectF2.set(0.0f, textLayoutBlock.height(this.transitionParams) - AndroidUtilities.dp(24.0f), i3, textLayoutBlock.height(this.transitionParams));
                    this.clip.draw(canvas2, rectF2, 3, f7);
                    canvas2.restore();
                }
                canvas2.restoreToCount(saveCount);
            }
            i8++;
            arrayList2 = arrayList;
            f6 = f5;
            i4 = 0;
        }
    }

    public void drawAnimatedEmojiCaption(Canvas canvas, float f) {
        float f2;
        float f3;
        float f4;
        if (this.captionLayout == null) {
            return;
        }
        float translationY = this.captionY;
        float f5 = this.captionX;
        TransitionParams transitionParams = this.transitionParams;
        if (transitionParams.animateBackgroundBoundsInner) {
            if (transitionParams.transformGroupToSingleMessage) {
                translationY -= getTranslationY();
                f4 = this.transitionParams.deltaLeft;
            } else if (transitionParams.moveCaption) {
                float f6 = this.captionX;
                TransitionParams transitionParams2 = this.transitionParams;
                float f7 = transitionParams2.animateChangeProgress;
                f3 = (f6 * f7) + (transitionParams2.captionFromX * (1.0f - f7));
                f2 = (transitionParams2.captionFromY * (1.0f - f7)) + (this.captionY * f7);
            } else {
                if (!this.currentMessageObject.isVoice() || !TextUtils.isEmpty(this.currentMessageObject.caption)) {
                    f4 = this.transitionParams.deltaLeft;
                }
                float f8 = f5;
                f2 = translationY;
                f3 = f8;
            }
            f5 += f4;
            float f9 = f5;
            f2 = translationY;
            f3 = f9;
        } else {
            float f10 = f5;
            f2 = translationY;
            f3 = f10;
        }
        TransitionParams transitionParams3 = this.transitionParams;
        if (transitionParams3.animateReplaceCaptionLayout && transitionParams3.animateChangeProgress != 1.0f) {
            ArrayList<MessageObject.TextLayoutBlock> arrayList = transitionParams3.animateOutCaptionLayout != null ? this.transitionParams.animateOutCaptionLayout.textLayoutBlocks : null;
            AnimatedEmojiSpan.EmojiGroupedSpans emojiGroupedSpans = this.transitionParams.animateOutAnimateEmoji;
            TransitionParams transitionParams4 = this.transitionParams;
            drawAnimatedEmojiMessageText(f3, f2, canvas, arrayList, emojiGroupedSpans, false, f * (1.0f - transitionParams4.animateChangeProgress), transitionParams4.animateOutCaptionLayout != null ? this.transitionParams.animateOutCaptionLayout.textXOffset : 0.0f, true);
            MessageObject.TextLayoutBlocks textLayoutBlocks = this.captionLayout;
            drawAnimatedEmojiMessageText(f3, f2, canvas, textLayoutBlocks != null ? textLayoutBlocks.textLayoutBlocks : null, this.animatedEmojiStack, true, f * this.transitionParams.animateChangeProgress, textLayoutBlocks != null ? textLayoutBlocks.textXOffset : 0.0f, true);
            return;
        }
        MessageObject.TextLayoutBlocks textLayoutBlocks2 = this.captionLayout;
        drawAnimatedEmojiMessageText(f3, f2, canvas, textLayoutBlocks2 != null ? textLayoutBlocks2.textLayoutBlocks : null, this.animatedEmojiStack, true, f, textLayoutBlocks2 != null ? textLayoutBlocks2.textXOffset : 0.0f, true);
    }

    public void setHideSideButtonByQuickShare(boolean z) {
        if (this.hideSideButtonByQuickShare != z) {
            this.hideSideButtonByQuickShare = z;
            boolean z2 = this.invalidatesParent;
            this.invalidatesParent = true;
            invalidate();
            this.invalidatesParent = z2;
        }
    }

    public void drawSideButton(Canvas canvas) {
        drawSideButton(canvas, false);
    }

    public void drawSideButton(Canvas canvas, boolean z) {
        int iSaveLayerAlpha;
        float f;
        MessageObject.GroupedMessages groupedMessages;
        this.sideButtonVisible = false;
        if (!this.hideSideButtonByQuickShare || z) {
            if (this.drawSideButton != 0 || this.drawSummarizeButton) {
                MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
                if (groupedMessagePosition == null || (groupedMessages = this.currentMessagesGroup) == null || !groupedMessages.isDocuments || groupedMessagePosition.last) {
                    boolean zIsOutOwner = this.currentMessageObject.isOutOwner();
                    TransitionParams transitionParams = this.transitionParams;
                    if (zIsOutOwner) {
                        float fDp = transitionParams.lastBackgroundLeft - AndroidUtilities.dp(40.0f);
                        this.sideStartX = fDp;
                        MessageObject.GroupedMessages groupedMessages2 = this.currentMessagesGroup;
                        if (groupedMessages2 != null) {
                            this.sideStartX = fDp + (groupedMessages2.transitionParams.offsetLeft - this.animationOffsetX);
                        }
                    } else {
                        float fDp2 = transitionParams.lastBackgroundRight + AndroidUtilities.dp(8.0f);
                        this.sideStartX = fDp2;
                        MessageObject.GroupedMessages groupedMessages3 = this.currentMessagesGroup;
                        if (groupedMessages3 != null) {
                            this.sideStartX = fDp2 + (groupedMessages3.transitionParams.offsetRight - this.animationOffsetX);
                        }
                    }
                    if (this.drawSideButton == 4) {
                        this.sideStartY = AndroidUtilities.dp(6.0f);
                    } else {
                        float fDp3 = (this.layoutHeight + this.transitionParams.deltaBottom) - AndroidUtilities.dp(41.0f);
                        this.sideStartY = fDp3;
                        MessageObject messageObject = this.currentMessageObject;
                        if (messageObject.type == 19 && messageObject.textWidth < this.timeTextWidth) {
                            this.sideStartY = fDp3 - AndroidUtilities.dp(22.0f);
                        }
                        MessageObject.GroupedMessages groupedMessages4 = this.currentMessagesGroup;
                        if (groupedMessages4 != null) {
                            float f2 = this.sideStartY;
                            MessageObject.GroupedMessages.TransitionParams transitionParams2 = groupedMessages4.transitionParams;
                            float f3 = f2 + transitionParams2.offsetBottom;
                            this.sideStartY = f3;
                            if (transitionParams2.backgroundChangeBounds) {
                                this.sideStartY = f3 - getTranslationY();
                            }
                        }
                        if (this.currentMessageObject.shouldDrawReactions()) {
                            ReactionsLayoutInBubble reactionsLayoutInBubble = this.reactionsLayoutInBubble;
                            if (!reactionsLayoutInBubble.isSmall) {
                                if (this.isRoundVideo) {
                                    this.sideStartY -= reactionsLayoutInBubble.getCurrentTotalHeight(this.transitionParams.animateChangeProgress) * (1.0f - getVideoTranscriptionProgress());
                                } else if (reactionsLayoutInBubble.drawServiceShaderBackground > 0.0f) {
                                    this.sideStartY -= reactionsLayoutInBubble.getCurrentTotalHeight(this.transitionParams.animateChangeProgress);
                                }
                            }
                        }
                    }
                    if (this.drawSideButton != 4) {
                        float fDp4 = ((this.layoutHeight + this.transitionParams.deltaBottom) - AndroidUtilities.dp(32.0f)) / 2.0f;
                        if (this.sideStartY < fDp4) {
                            this.sideStartY = fDp4;
                        }
                    }
                    if (this.currentMessageObject.type == 19) {
                        if (this.drawSideButton == 3 && this.commentLayout != null) {
                            this.sideStartY = AndroidUtilities.dp(18.0f);
                        } else {
                            this.sideStartY = 0.0f;
                        }
                    }
                    if (!this.currentMessageObject.isOutOwner() && this.isRoundVideo && !this.hasLinkPreview) {
                        float fRoundPlayingMessageSize = this.isAvatarVisible ? (AndroidUtilities.roundPlayingMessageSize(this.isSideMenued) - AndroidUtilities.roundMessageSize) * 0.7f : AndroidUtilities.dp(50.0f);
                        float videoTranscriptionProgress = this.isPlayingRound ? (1.0f - getVideoTranscriptionProgress()) * fRoundPlayingMessageSize : 0.0f;
                        float fDp5 = this.isPlayingRound ? AndroidUtilities.dp(28.0f) * (1.0f - getVideoTranscriptionProgress()) : 0.0f;
                        TransitionParams transitionParams3 = this.transitionParams;
                        if (transitionParams3.animatePlayingRound) {
                            videoTranscriptionProgress = (this.isPlayingRound ? transitionParams3.animateChangeProgress : 1.0f - transitionParams3.animateChangeProgress) * (1.0f - getVideoTranscriptionProgress()) * fRoundPlayingMessageSize;
                            boolean z2 = this.isPlayingRound;
                            TransitionParams transitionParams4 = this.transitionParams;
                            fDp5 = AndroidUtilities.dp(28.0f) * (z2 ? transitionParams4.animateChangeProgress : 1.0f - transitionParams4.animateChangeProgress) * (1.0f - getVideoTranscriptionProgress());
                        }
                        this.sideStartX -= videoTranscriptionProgress;
                        this.sideStartY -= fDp5;
                    }
                    this.sideButtonVisible = true;
                    if (this.drawSideButton == 3) {
                        if (!this.enterTransitionInProgress || this.currentMessageObject.isVoice()) {
                            drawCommentButton(canvas, 1.0f);
                            return;
                        }
                        return;
                    }
                    if (SizeNotifierFrameLayout.drawingBlur) {
                        return;
                    }
                    RectF rectF = this.rect;
                    float f4 = this.sideStartX;
                    rectF.set(f4, this.sideStartY, AndroidUtilities.dp(32.0f) + f4, this.sideStartY + AndroidUtilities.dp(this.drawSideButton2 == 5 ? 64.0f : 32.0f));
                    if (this.rect.right >= getMeasuredWidth()) {
                        this.sideButtonVisible = false;
                        return;
                    }
                    if (this.drawSideButton == 0) {
                        return;
                    }
                    int floatValue = (int) ((1.0f - this.isSponsoredMessageHidden.getFloatValue()) * 255.0f);
                    if (floatValue != 255) {
                        float f5 = this.sideStartX;
                        iSaveLayerAlpha = canvas.saveLayerAlpha(f5, this.sideStartY, AndroidUtilities.dp(32.0f) + f5, this.sideStartY + AndroidUtilities.dp(64.0f), floatValue);
                    } else {
                        iSaveLayerAlpha = -1;
                    }
                    applyServiceShaderMatrix();
                    if (this.drawSideButton == 4 && this.drawSideButton2 == 5 && this.sideButtonPressed) {
                        Path path = this.sideButtonPath1;
                        if (path == null) {
                            this.sideButtonPath1 = new Path();
                        } else {
                            path.rewind();
                        }
                        Path path2 = this.sideButtonPath2;
                        if (path2 == null) {
                            this.sideButtonPath2 = new Path();
                        } else {
                            path2.rewind();
                        }
                        if (this.sideButtonPathCorners1 == null) {
                            this.sideButtonPathCorners1 = new float[]{fDp, fDp, fDp, fDp, 0.0f, 0.0f, 0.0f, 0.0f};
                            float fDp6 = AndroidUtilities.dp(16.0f);
                        }
                        if (this.sideButtonPathCorners2 == null) {
                            this.sideButtonPathCorners2 = new float[]{0.0f, 0.0f, 0.0f, 0.0f, fDp, fDp, fDp, fDp};
                            float fDp7 = AndroidUtilities.dp(16.0f);
                        }
                        RectF rectF2 = AndroidUtilities.rectTmp;
                        float f6 = this.sideStartX;
                        f = 16.0f;
                        rectF2.set(f6, this.sideStartY, AndroidUtilities.dp(r10) + f6, this.sideStartY + AndroidUtilities.dp(r10));
                        Path path3 = this.sideButtonPath1;
                        float[] fArr = this.sideButtonPathCorners1;
                        Path.Direction direction = Path.Direction.CW;
                        path3.addRoundRect(rectF2, fArr, direction);
                        rectF2.set(this.sideStartX, this.sideStartY + AndroidUtilities.dp(r10), this.sideStartX + AndroidUtilities.dp(32.0f), this.sideStartY + AndroidUtilities.dp(64.0f));
                        this.sideButtonPath2.addRoundRect(rectF2, this.sideButtonPathCorners2, direction);
                        int i = this.pressedSideButton;
                        Path path4 = this.sideButtonPath1;
                        if (i == 4) {
                            canvas.drawPath(path4, getThemedPaint("paintChatActionBackgroundSelected"));
                            canvas.drawPath(this.sideButtonPath2, getThemedPaint("paintChatActionBackground"));
                        } else {
                            canvas.drawPath(path4, getThemedPaint("paintChatActionBackground"));
                            canvas.drawPath(this.sideButtonPath2, getThemedPaint("paintChatActionBackgroundSelected"));
                        }
                    } else {
                        f = 16.0f;
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), getThemedPaint(this.sideButtonPressed ? "paintChatActionBackgroundSelected" : "paintChatActionBackground"));
                    }
                    if (hasGradientService()) {
                        canvas.drawRoundRect(this.rect, AndroidUtilities.dp(f), AndroidUtilities.dp(f), Theme.chat_actionBackgroundGradientDarkenPaint);
                    }
                    int i2 = this.drawSideButton;
                    if (i2 == 2) {
                        Drawable themedDrawable = getThemedDrawable("drawableGoIcon");
                        BaseCell.setDrawableBounds(themedDrawable, (this.sideStartX + AndroidUtilities.dp(f)) - (themedDrawable.getIntrinsicWidth() / 2.0f), (this.sideStartY + AndroidUtilities.dp(f)) - (themedDrawable.getIntrinsicHeight() / 2.0f));
                        themedDrawable.draw(canvas);
                    } else {
                        float f7 = this.sideStartX;
                        if (i2 == 4) {
                            int iDp = (int) (f7 + AndroidUtilities.dp(f));
                            int iDp2 = (int) (this.sideStartY + AndroidUtilities.dp(f));
                            Drawable themedDrawable2 = getThemedDrawable("drawableCloseIcon");
                            int intrinsicWidth = themedDrawable2.getIntrinsicWidth() / 2;
                            int intrinsicHeight = themedDrawable2.getIntrinsicHeight() / 2;
                            themedDrawable2.setBounds(iDp - intrinsicWidth, iDp2 - intrinsicHeight, intrinsicWidth + iDp, intrinsicHeight + iDp2);
                            BaseCell.setDrawableBounds(themedDrawable2, this.sideStartX + AndroidUtilities.dp(4.0f), this.sideStartY + AndroidUtilities.dp(4.0f));
                            canvas.save();
                            canvas.scale(0.65f, 0.65f, themedDrawable2.getBounds().centerX(), themedDrawable2.getBounds().centerY());
                            themedDrawable2.draw(canvas);
                            canvas.restore();
                            if (this.drawSideButton2 == 5) {
                                Drawable themedDrawable3 = getThemedDrawable("drawableMoreIcon");
                                int intrinsicWidth2 = themedDrawable3.getIntrinsicWidth() / 2;
                                int intrinsicHeight2 = themedDrawable3.getIntrinsicHeight() / 2;
                                themedDrawable3.setBounds(iDp - intrinsicWidth2, iDp2 - intrinsicHeight2, iDp + intrinsicWidth2, iDp2 + intrinsicHeight2);
                                BaseCell.setDrawableBounds(themedDrawable3, this.sideStartX + AndroidUtilities.dp(4.0f), this.sideStartY + AndroidUtilities.dp(34.0f));
                                themedDrawable3.draw(canvas);
                            }
                        } else {
                            int iDp3 = (int) (f7 + AndroidUtilities.dp(f));
                            int iDp4 = (int) (this.sideStartY + AndroidUtilities.dp(f));
                            Drawable themedDrawable4 = getThemedDrawable("drawableShareIcon");
                            int intrinsicWidth3 = themedDrawable4.getIntrinsicWidth() / 2;
                            int intrinsicHeight3 = themedDrawable4.getIntrinsicHeight() / 2;
                            themedDrawable4.setBounds(iDp3 - intrinsicWidth3, iDp4 - intrinsicHeight3, iDp3 + intrinsicWidth3, iDp4 + intrinsicHeight3);
                            BaseCell.setDrawableBounds(themedDrawable4, this.sideStartX + AndroidUtilities.dp(4.0f), this.sideStartY + AndroidUtilities.dp(4.0f));
                            themedDrawable4.draw(canvas);
                        }
                    }
                    if (iSaveLayerAlpha != -1) {
                        canvas.restoreToCount(iSaveLayerAlpha);
                    }
                }
            }
        }
    }

    public void drawSummarizeButton(Canvas canvas) {
        TLRPC.Message message;
        this.summarizeButtonX = this.sideStartX;
        this.summarizeButtonY = getPaddingTop() + AndroidUtilities.dp(8.0f);
        float fMax = Math.max(Math.min((-this.childPosition2) + AndroidUtilities.dp(4.0f), this.sideStartY - AndroidUtilities.dp(42.0f)), this.summarizeButtonY);
        this.summarizeButtonY = fMax;
        if (this.drawSummarizeButton && this.sideButtonVisible) {
            RectF rectF = this.rect;
            float f = this.summarizeButtonX;
            rectF.set(f, fMax, AndroidUtilities.dp(32.0f) + f, this.summarizeButtonY + AndroidUtilities.dp(32.0f));
            applyServiceShaderMatrix();
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), getThemedPaint(this.summarizeButtonPressed ? "paintChatActionBackgroundSelected" : "paintChatActionBackground"));
            if (hasGradientService()) {
                canvas.drawRoundRect(this.rect, AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), Theme.chat_actionBackgroundGradientDarkenPaint);
            }
            if (this.summarizeIcon == null) {
                this.summarizeIcon = new SummaryIcon(this);
            }
            MessageObject messageObject = getMessageObject();
            this.summarizeIcon.set((messageObject == null || (message = messageObject.messageOwner) == null || !message.summarizedOpen) ? false : true);
            BaseCell.setDrawableBounds(this.summarizeIcon, (this.summarizeButtonX + AndroidUtilities.dp(16.0f)) - (this.summarizeIcon.getIntrinsicWidth() / 2.0f), (this.summarizeButtonY + AndroidUtilities.dp(16.0f)) - (this.summarizeIcon.getIntrinsicHeight() / 2.0f));
            this.summarizeIcon.draw(canvas);
        }
    }

    public float getSideButtonStartX() {
        return this.sideStartX;
    }

    public float getSideButtonStartY() {
        return this.sideStartY;
    }

    public void setTimeAlpha(float f) {
        this.timeAlpha = f;
    }

    public float getTimeAlpha() {
        return this.timeAlpha;
    }

    private boolean isSideMenuPossibleLeftMargin() {
        MessageObject messageObject;
        return this.isSideMenued && (messageObject = this.currentMessageObject) != null && !messageObject.isOutOwner() && this.currentPosition == null;
    }

    private boolean isSideMenuLeftMargin() {
        MessageObject messageObject;
        return this.isSideMenuEnabled && (messageObject = this.currentMessageObject) != null && !messageObject.isOutOwner() && this.currentPosition == null;
    }

    public int getBackgroundDrawableLeft() {
        int iDp;
        int iDp2;
        int iDp3;
        int i;
        MessageObject messageObject = getMessageObject();
        int i2 = 0;
        if (messageObject != null && messageObject.isOutOwner()) {
            boolean z = this.isRoundVideo;
            int i3 = this.layoutWidth;
            if (z) {
                return (i3 - this.backgroundWidth) - ((int) ((1.0f - getVideoTranscriptionProgress()) * AndroidUtilities.dp(9.0f)));
            }
            return (i3 - this.backgroundWidth) - (this.mediaBackground ? AndroidUtilities.dp(9.0f) : 0);
        }
        float f = 71.0f;
        if (this.isRoundVideo) {
            if (!isSideMenuLeftMargin()) {
                if ((this.isChat || ((messageObject != null && (messageObject.isRepostPreview || messageObject.forceAvatar || messageObject.messageOwner.guestchat_via_from != null)) || messageObject.getDialogId() == UserObject.VERIFY)) && this.isAvatarVisible) {
                    i2 = 48;
                }
                f = i2 + 3;
            }
            iDp = AndroidUtilities.dp(f);
            iDp2 = (int) (AndroidUtilities.dp(6.0f) * (1.0f - getVideoTranscriptionProgress()));
        } else {
            if (!isSideMenuLeftMargin()) {
                if ((this.isChat || ((messageObject != null && (messageObject.isRepostPreview || messageObject.forceAvatar || messageObject.messageOwner.guestchat_via_from != null)) || messageObject.getDialogId() == UserObject.VERIFY)) && this.isAvatarVisible) {
                    i2 = 48;
                }
                f = i2;
            }
            iDp = AndroidUtilities.dp(f);
            iDp2 = AndroidUtilities.dp(this.mediaBackground ? 9.0f : 3.0f);
        }
        int iCeil = iDp + iDp2;
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && !groupedMessages.isDocuments && (i = this.currentPosition.leftSpanOffset) != 0) {
            iCeil += (int) Math.ceil((i / 1000.0f) * getGroupPhotosWidth());
        }
        if (this.isRoundVideo) {
            if (this.drawPinnedBottom) {
                iDp3 = (int) (AndroidUtilities.dp(6.0f) * (1.0f - getVideoTranscriptionProgress()));
                return iCeil + iDp3;
            }
            return iCeil;
        }
        if (!this.mediaBackground && this.drawPinnedBottom) {
            iDp3 = AndroidUtilities.dp(6.0f);
            return iCeil + iDp3;
        }
        return iCeil;
    }

    public int getBackgroundDrawableRight() {
        int iDp;
        int backgroundDrawableLeft;
        MessageObject messageObject;
        MessageObject messageObject2;
        int i = this.backgroundWidth;
        if (this.isRoundVideo) {
            iDp = i - ((int) (getVideoTranscriptionProgress() * AndroidUtilities.dp(3.0f)));
            if (this.drawPinnedBottom && (messageObject2 = this.currentMessageObject) != null && messageObject2.isOutOwner()) {
                iDp = (int) (iDp - (AndroidUtilities.dp(6.0f) * (1.0f - getVideoTranscriptionProgress())));
            }
            if (this.drawPinnedBottom && ((messageObject = this.currentMessageObject) == null || !messageObject.isOutOwner())) {
                iDp = (int) (iDp - (AndroidUtilities.dp(6.0f) * (1.0f - getVideoTranscriptionProgress())));
            }
            backgroundDrawableLeft = getBackgroundDrawableLeft();
        } else {
            iDp = i - (this.mediaBackground ? 0 : AndroidUtilities.dp(3.0f));
            if (!this.mediaBackground && this.drawPinnedBottom) {
                iDp -= AndroidUtilities.dp(6.0f);
            }
            backgroundDrawableLeft = getBackgroundDrawableLeft();
        }
        return backgroundDrawableLeft + iDp;
    }

    public int getBackgroundDrawableTop() {
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        int iDp = ((groupedMessagePosition == null || (groupedMessagePosition.flags & 4) != 0) ? 0 : 0 - AndroidUtilities.dp(3.0f)) + (this.drawPinnedTop ? 0 : AndroidUtilities.dp(1.0f));
        return (this.mediaBackground || !this.drawPinnedTop) ? iDp : iDp - AndroidUtilities.dp(1.0f);
    }

    public int getBackgroundDrawableBottom() {
        int iDp;
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        int iDp2 = 0;
        if (groupedMessagePosition != null) {
            int i = 4;
            iDp = (groupedMessagePosition.flags & 4) == 0 ? AndroidUtilities.dp(3.0f) : 0;
            if ((this.currentPosition.flags & 8) == 0) {
                MessageObject messageObject = this.currentMessageObject;
                if (messageObject != null && messageObject.isOutOwner()) {
                    i = 3;
                }
                iDp += AndroidUtilities.dp(i);
            }
        } else {
            iDp = 0;
        }
        boolean z = this.drawPinnedBottom;
        if (!z || !this.drawPinnedTop) {
            if (z) {
                iDp2 = AndroidUtilities.dp(1.0f);
            } else {
                iDp2 = AndroidUtilities.dp(2.0f);
            }
        }
        int backgroundDrawableTop = ((getBackgroundDrawableTop() + this.layoutHeight) - iDp2) + iDp;
        if (this.mediaBackground) {
            return backgroundDrawableTop;
        }
        if (this.drawPinnedTop) {
            backgroundDrawableTop += AndroidUtilities.dp(1.0f);
        }
        return this.drawPinnedBottom ? backgroundDrawableTop + AndroidUtilities.dp(1.0f) : backgroundDrawableTop;
    }

    public boolean hasNameLayout() {
        MessageObject.GroupedMessagePosition groupedMessagePosition;
        if (!this.drawNameLayout || this.nameLayout == null) {
            if (this.drawForwardedName) {
                StaticLayout[] staticLayoutArr = this.forwardedNameLayout;
                if (staticLayoutArr[0] == null || staticLayoutArr[1] == null || ((groupedMessagePosition = this.currentPosition) != null && (groupedMessagePosition.minY != 0 || groupedMessagePosition.minX != 0))) {
                    if (this.drawSummaryReply && !this.transitionParams.animateSummaryReply && this.replyNameLayout == null) {
                        return false;
                    }
                }
            } else if (this.drawSummaryReply) {
            }
        }
        return true;
    }

    public boolean isDrawNameLayout() {
        return this.drawNameLayout && this.nameLayout != null;
    }

    public boolean isAdminLayoutChanged() {
        return !TextUtils.equals(this.lastPostAuthor, this.currentMessageObject.messageOwner.post_author);
    }

    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Cells.ChatMessageCell.drawCaptionLayout(android.graphics.Canvas, org.telegram.messenger.MessageObject$TextLayoutBlocks, boolean, boolean, float):void");
    }

    public void drawProgressLoadingLink(Canvas canvas, int i) {
        updateProgressLoadingLink();
        ArrayList<LoadingDrawableLocation> arrayList = this.progressLoadingLinkDrawables;
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        MessageObject messageObject = this.currentMessageObject;
        int themedColor = getThemedColor((messageObject == null || !messageObject.isOutOwner()) ? Theme.key_chat_linkSelectBackground : Theme.key_chat_outLinkSelectBackground);
        int i2 = 0;
        while (i2 < this.progressLoadingLinkDrawables.size()) {
            LoadingDrawableLocation loadingDrawableLocation = this.progressLoadingLinkDrawables.get(i2);
            if (loadingDrawableLocation.blockNum == i) {
                LoadingDrawable loadingDrawable = loadingDrawableLocation.drawable;
                loadingDrawable.setColors(Theme.multAlpha(themedColor, 0.85f), Theme.multAlpha(themedColor, 2.0f), Theme.multAlpha(themedColor, 3.5f), Theme.multAlpha(themedColor, 6.0f));
                loadingDrawable.draw(canvas);
                invalidate();
                if (loadingDrawable.isDisappeared()) {
                    this.progressLoadingLinkDrawables.remove(i2);
                    i2--;
                }
            }
            i2++;
        }
    }

    public void updateProgressLoadingLink() {
        MessageObject messageObject;
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        if (chatMessageCellDelegate == null) {
            return;
        }
        if (!chatMessageCellDelegate.isProgressLoading(this, 1)) {
            this.progressLoadingLink = null;
            ArrayList<LoadingDrawableLocation> arrayList = this.progressLoadingLinkDrawables;
            if (arrayList == null || arrayList.isEmpty()) {
                return;
            }
            for (int i = 0; i < this.progressLoadingLinkDrawables.size(); i++) {
                LoadingDrawableLocation loadingDrawableLocation = this.progressLoadingLinkDrawables.get(i);
                if (!loadingDrawableLocation.drawable.isDisappearing()) {
                    loadingDrawableLocation.drawable.disappear();
                }
            }
            return;
        }
        CharacterStyle progressLoadingLink = this.delegate.getProgressLoadingLink(this);
        if (progressLoadingLink == this.progressLoadingLink) {
            return;
        }
        this.progressLoadingLink = progressLoadingLink;
        LoadingDrawable loadingDrawable = this.progressLoadingLinkCurrentDrawable;
        if (loadingDrawable != null) {
            loadingDrawable.disappear();
            this.progressLoadingLinkCurrentDrawable = null;
        }
        LoadingDrawable loadingDrawable2 = new LoadingDrawable();
        this.progressLoadingLinkCurrentDrawable = loadingDrawable2;
        loadingDrawable2.setAppearByGradient(true);
        LinkPath linkPath = new LinkPath(true);
        this.progressLoadingLinkCurrentDrawable.usePath(linkPath);
        this.progressLoadingLinkCurrentDrawable.setRadiiDp(5.0f);
        LoadingDrawableLocation loadingDrawableLocation2 = new LoadingDrawableLocation();
        loadingDrawableLocation2.drawable = this.progressLoadingLinkCurrentDrawable;
        loadingDrawableLocation2.blockNum = -3;
        if (this.progressLoadingLinkDrawables == null) {
            this.progressLoadingLinkDrawables = new ArrayList<>();
        }
        this.progressLoadingLinkDrawables.add(loadingDrawableLocation2);
        if (this.progressLoadingLink == null || findProgressLoadingLink(loadingDrawableLocation2, linkPath, this.descriptionLayout, 0.0f, -2)) {
            return;
        }
        MessageObject.TextLayoutBlocks textLayoutBlocks = this.captionLayout;
        if ((textLayoutBlocks == null || !findProgressLoadingLink(loadingDrawableLocation2, linkPath, textLayoutBlocks.textLayoutBlocks)) && (messageObject = this.currentMessageObject) != null) {
            findProgressLoadingLink(loadingDrawableLocation2, linkPath, messageObject.textLayoutBlocks);
        }
    }

    private boolean findProgressLoadingLink(LoadingDrawableLocation loadingDrawableLocation, LinkPath linkPath, ArrayList<MessageObject.TextLayoutBlock> arrayList) {
        if (arrayList == null) {
            return false;
        }
        int i = 0;
        while (i < arrayList.size()) {
            ChatMessageCell chatMessageCell = this;
            LoadingDrawableLocation loadingDrawableLocation2 = loadingDrawableLocation;
            LinkPath linkPath2 = linkPath;
            if (chatMessageCell.findProgressLoadingLink(loadingDrawableLocation2, linkPath2, arrayList.get(i).textLayout, 0.0f, i)) {
                return true;
            }
            i++;
            this = chatMessageCell;
            loadingDrawableLocation = loadingDrawableLocation2;
            linkPath = linkPath2;
        }
        return false;
    }

    private boolean findProgressLoadingLink(LoadingDrawableLocation loadingDrawableLocation, LinkPath linkPath, Layout layout, float f, int i) {
        if (layout == null || !(layout.getText() instanceof Spanned)) {
            return false;
        }
        Spanned spanned = (Spanned) layout.getText();
        CharacterStyle[] characterStyleArr = (CharacterStyle[]) spanned.getSpans(0, spanned.length(), CharacterStyle.class);
        if (characterStyleArr != null) {
            for (CharacterStyle characterStyle : characterStyleArr) {
                if (characterStyle == this.progressLoadingLink) {
                    loadingDrawableLocation.blockNum = i;
                    break;
                }
            }
        }
        if (loadingDrawableLocation.blockNum != i) {
            return false;
        }
        linkPath.rewind();
        int spanStart = spanned.getSpanStart(this.progressLoadingLink);
        int spanEnd = spanned.getSpanEnd(this.progressLoadingLink);
        linkPath.setUseCornerPathImplementation(true);
        linkPath.setCurrentLayout(layout, spanStart, f);
        layout.getSelectionPath(spanStart, spanEnd, linkPath);
        linkPath.closeRects();
        this.progressLoadingLinkCurrentDrawable.updateBounds();
        return true;
    }

    public boolean needDrawTime() {
        if (this.forceNotDrawTime) {
            return false;
        }
        MessageObject messageObject = this.currentMessageObject;
        return messageObject == null || messageObject.type != 27;
    }

    void lambda$createStatusDrawableAnimator$15(boolean z, ValueAnimator valueAnimator) {
        this.statusDrawableProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
        if (!z || getParent() == null) {
            return;
        }
        ((View) getParent()).invalidate();
    }

    public void drawRadialProgress(Canvas canvas) {
        MessageObject messageObject;
        boolean zIsRoundOnce = this.currentMessageObject.isRoundOnce();
        if (zIsRoundOnce) {
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(this.radialProgress.getProgressRect());
            rectF.inset(-AndroidUtilities.dp(15.0f), -AndroidUtilities.dp(15.0f));
            canvas.saveLayerAlpha(rectF, (int) (this.radialProgressAlpha * 255.0f), 31);
        } else {
            canvas.save();
            int i = this.currentMessageObject.type;
            if (this.drawPhotoImage && (i == 3 || i == 1 || i == 8)) {
                canvas.clipRect(this.photoImage.getImageX(), this.photoImage.getImageY(), this.photoImage.getImageX2(), this.photoImage.getImageY2());
            }
        }
        if (this.currentMessageObject.needDrawBluredPreview()) {
            drawPhotoBlurRect(canvas, this.radialProgress.getProgressRect());
        }
        this.radialProgress.iconScale = 1.0f;
        if (this.drawPhotoImage && (messageObject = this.currentMessageObject) != null && messageObject.hasMediaSpoilers() && this.currentMessageObject.isSensitive()) {
            if (this.currentMessageObject.isMediaSpoilersRevealed) {
                float f = this.mediaSpoilerRevealProgress;
                if (f != 0.0f && f < 1.0f) {
                    this.radialProgress.iconScale *= this.mediaSpoilerRevealProgress;
                }
            } else {
                this.radialProgress.iconScale *= this.mediaSpoilerRevealProgress;
            }
        }
        this.radialProgress.drawScale = (this.documentAttachType == 1 && this.drawPhotoImage && this.photoImage.getImageWidth() > 0.0f) ? (this.photoImage.getImageWidth() - (this.photoImage.getSideClip() * 2.0f)) / this.photoImage.getImageWidth() : 1.0f;
        this.radialProgress.draw(canvas);
        if (zIsRoundOnce) {
            canvas.save();
            drawPhotoBlurRect(canvas, getRadialProgress().getProgressRect());
            getRadialProgress().draw(canvas);
            RectF progressRect = getRadialProgress().getProgressRect();
            float fCenterX = progressRect.centerX() + AndroidUtilities.dp(18.0f);
            float fCenterY = progressRect.centerY() + AndroidUtilities.dp(18.0f);
            float fDp = AndroidUtilities.dp(10.0f);
            float fDp2 = AndroidUtilities.dp(1.33f) + fDp;
            if (this.clipPaint == null) {
                Paint paint = new Paint(1);
                this.clipPaint = paint;
                paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            }
            canvas.drawCircle(fCenterX, fCenterY, fDp2, this.clipPaint);
            RectF rectF2 = AndroidUtilities.rectTmp;
            rectF2.set(fCenterX - fDp, fCenterY - fDp, fCenterX + fDp, fDp + fCenterY);
            drawPhotoBlurRect(canvas, rectF2);
            if (this.oncePeriod == null) {
                CaptionContainerView.PeriodDrawable periodDrawable = new CaptionContainerView.PeriodDrawable(3);
                this.oncePeriod = periodDrawable;
                periodDrawable.updateColors(-1, 0, 0);
                CaptionContainerView.PeriodDrawable periodDrawable2 = this.oncePeriod;
                periodDrawable2.diameterDp = 14.0f;
                periodDrawable2.setTextSize(10.0f);
                this.oncePeriod.strokePaint.setStrokeWidth(AndroidUtilities.dpf2(1.5f));
                this.oncePeriod.setValue(1, false, false);
                this.oncePeriod.textOffsetX = -AndroidUtilities.dpf2(0.33f);
                this.oncePeriod.textOffsetY = AndroidUtilities.dpf2(0.33f);
            }
            CaptionContainerView.PeriodDrawable periodDrawable3 = this.oncePeriod;
            periodDrawable3.diameterDp = 14.0f;
            periodDrawable3.setTextSize(10.0f);
            this.oncePeriod.setClear(false);
            this.oncePeriod.setCenterXY(fCenterX, fCenterY);
            this.oncePeriod.draw(canvas, 1.0f);
            canvas.restore();
        }
        canvas.restore();
    }

    public void drawPhotoBlurRect(Canvas canvas, RectF rectF) {
        this.rectPath.rewind();
        this.rectPath.addRoundRect(rectF, rectF.width() / 2.0f, rectF.height() / 2.0f, Path.Direction.CW);
        canvas.save();
        canvas.clipPath(this.rectPath);
        float alpha = this.photoImage.getAlpha();
        this.photoImage.setAlpha((this.currentMessageObject.isRoundOnce() ? 1.0f : 0.5f) * alpha);
        this.photoImage.draw(canvas);
        this.photoImage.setAlpha(alpha);
        canvas.restore();
        Paint themedPaint = getThemedPaint("paintChatTimeBackground");
        int alpha2 = themedPaint.getAlpha();
        themedPaint.setAlpha((int) (alpha2 * this.controlsAlpha * 0.4f));
        canvas.drawRoundRect(rectF, rectF.width() / 2.0f, rectF.height() / 2.0f, themedPaint);
        themedPaint.setAlpha(alpha2);
    }

    @Override 
    public int getObserverTag() {
        return this.TAG;
    }

    @Override // org.telegram.ui.Cells.IMessageCell
    public MessageObject getMessageObject() {
        MessageObject messageObject = this.messageObjectToSet;
        return messageObject != null ? messageObject : this.currentMessageObject;
    }

    public ReactionsLayoutInBubble getReactionsLayout() {
        return this.reactionsLayoutInBubble;
    }

    @Override // org.telegram.ui.Cells.IMessageCell
    public void didPressReactionFromLayout(TLRPC.ReactionCount reactionCount, boolean z, float f, float f2) {
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        if (chatMessageCellDelegate != null) {
            chatMessageCellDelegate.didPressReaction(this, reactionCount, z, f, f2);
        }
    }

    public TLRPC.Document getStreamingMedia() {
        int i = this.documentAttachType;
        if (i == 4 || i == 7 || i == 2) {
            return this.documentAttach;
        }
        return null;
    }

    @Override // org.telegram.ui.Cells.IMessageCell
    public boolean drawPinnedBottom() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && groupedMessages.isDocuments) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            if (groupedMessagePosition == null || (groupedMessagePosition.flags & 8) == 0) {
                return true;
            }
            return this.pinnedBottom;
        }
        return this.pinnedBottom;
    }

    public float getVideoTranscriptionProgress() {
        MessageObject messageObject;
        if (this.transitionParams == null || (messageObject = this.currentMessageObject) == null || !messageObject.isRoundVideo()) {
            return 1.0f;
        }
        TransitionParams transitionParams = this.transitionParams;
        boolean z = transitionParams.animateDrawBackground;
        boolean z2 = this.drawBackground;
        if (!z) {
            return z2 ? 1.0f : 0.0f;
        }
        if (z2) {
            return transitionParams.animateChangeProgress;
        }
        return 1.0f - transitionParams.animateChangeProgress;
    }

    @Override // org.telegram.ui.Cells.IMessageCell
    public boolean drawPinnedTop() {
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null && groupedMessages.isDocuments) {
            MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
            if (groupedMessagePosition == null || (groupedMessagePosition.flags & 4) == 0) {
                return true;
            }
            return this.pinnedTop;
        }
        return this.pinnedTop;
    }

    public boolean isPinnedBottom() {
        if (this.messageObjectToSet != null) {
            return this.bottomNearToSet;
        }
        return this.pinnedBottom;
    }

    public boolean isPinnedTop() {
        if (this.messageObjectToSet != null) {
            return this.topNearToSet;
        }
        return this.pinnedTop;
    }

    public boolean isFirstInChat() {
        if (this.messageObjectToSet != null) {
            return this.firstInChatToSet;
        }
        return this.firstInChat;
    }

    public boolean isLastInChatList() {
        if (this.messageObjectToSet != null) {
            return this.lastInChatListToSet;
        }
        return this.lastInChatList;
    }

    public MessageObject.GroupedMessages getCurrentMessagesGroup() {
        return this.currentMessagesGroup;
    }

    @Override // org.telegram.ui.Cells.IMessageCell
    public MessageObject.GroupedMessagePosition getCurrentPosition() {
        return this.currentPosition;
    }

    @Override // org.telegram.ui.Cells.IMessageCell
    public int getLayoutHeight() {
        return this.layoutHeight;
    }

    @Override // android.view.View
    public boolean performAccessibilityAction(int i, Bundle bundle) {
        ChatMessageCell chatMessageCell;
        ArrayList<MessageObject.TextLayoutBlock> arrayList;
        ChatMessageCellDelegate chatMessageCellDelegate = this.delegate;
        int i2 = 0;
        if (chatMessageCellDelegate != null && chatMessageCellDelegate.onAccessibilityAction(i, bundle)) {
            return false;
        }
        if (i == 16) {
            int iconForCurrentState = getIconForCurrentState();
            if (iconForCurrentState != 4 && iconForCurrentState != 5 && !MediaActionDrawable.isCustomFileIcon(iconForCurrentState)) {
                didPressButton(true, false);
            } else if (this.currentMessageObject.type == 16) {
                this.delegate.didPressOther(this, this.otherX, this.otherY);
            } else {
                didClickedImage();
            }
            return true;
        }
        if (i == org.telegram.messenger.R.id.acc_action_small_button) {
            didPressMiniButton(true);
        } else if (i == org.telegram.messenger.R.id.acc_action_msg_options) {
            ChatMessageCellDelegate chatMessageCellDelegate2 = this.delegate;
            if (chatMessageCellDelegate2 != null) {
                if (this.currentMessageObject.type == 16) {
                    chatMessageCellDelegate2.didLongPress(this, 0.0f, 0.0f);
                } else {
                    chatMessageCellDelegate2.didPressOther(this, this.otherX, this.otherY);
                }
            }
        } else {
            if (i == org.telegram.messenger.R.id.acc_action_open_forwarded_origin) {
                ChatMessageCellDelegate chatMessageCellDelegate3 = this.delegate;
                if (chatMessageCellDelegate3 != null) {
                    TLRPC.Chat chat = this.currentForwardChannel;
                    if (chat != null) {
                        chatMessageCell = this;
                        chatMessageCellDelegate3.didPressChannelAvatar(chatMessageCell, chat, this.currentMessageObject.messageOwner.fwd_from.channel_post, this.lastTouchX, this.lastTouchY, false);
                    } else {
                        chatMessageCell = this;
                        TLRPC.User user = chatMessageCell.currentForwardUser;
                        if (user != null) {
                            chatMessageCellDelegate3.didPressUserAvatar(chatMessageCell, user, chatMessageCell.lastTouchX, chatMessageCell.lastTouchY, false);
                        } else if (chatMessageCell.currentForwardName != null) {
                            chatMessageCellDelegate3.didPressHiddenForward(chatMessageCell);
                        }
                    }
                }
            } else {
                chatMessageCell = this;
                if (i == org.telegram.messenger.R.id.acc_action_summarize) {
                    ChatMessageCellDelegate chatMessageCellDelegate4 = chatMessageCell.delegate;
                    if (chatMessageCellDelegate4 != null) {
                        chatMessageCellDelegate4.didPressSummarize(chatMessageCell, chatMessageCell.drawSummaryReply);
                    }
                } else if (i == org.telegram.messenger.R.id.acc_action_copy_code && chatMessageCell.delegate != null && (arrayList = chatMessageCell.currentMessageObject.textLayoutBlocks) != null) {
                    int size = arrayList.size();
                    while (i2 < size) {
                        MessageObject.TextLayoutBlock textLayoutBlock = arrayList.get(i2);
                        i2++;
                        MessageObject.TextLayoutBlock textLayoutBlock2 = textLayoutBlock;
                        if (textLayoutBlock2.hasCodeCopyButton) {
                            chatMessageCell.delegate.didPressCodeCopy(chatMessageCell, textLayoutBlock2);
                            break;
                        }
                    }
                }
            }
            if ((chatMessageCell.currentMessageObject.isVoice() && !chatMessageCell.currentMessageObject.isRoundVideo() && (!chatMessageCell.currentMessageObject.isMusic() || !MediaController.getInstance().isPlayingMessage(chatMessageCell.currentMessageObject))) || !chatMessageCell.seekBarAccessibilityDelegate.performAccessibilityActionInternal(i, bundle)) {
                return super.performAccessibilityAction(i, bundle);
            }
        }
        chatMessageCell = this;
        return chatMessageCell.currentMessageObject.isVoice() ? true : true;
        return super.performAccessibilityAction(i, bundle);
    }

    @Override // org.telegram.ui.Cells.IMessageCell
    public void setAnimationRunning(boolean z, boolean z2) {
        this.animationRunning = z;
        if (z) {
            this.willRemoved = z2;
        } else {
            this.willRemoved = false;
        }
    }

    public float getEventX(MotionEvent motionEvent) {
        return motionEvent.getX();
    }

    public float getEventY(MotionEvent motionEvent) {
        return ((motionEvent.getY() - this.starsPriceTopPadding) - this.topicSeparatorTopPadding) - this.suggestionOfferTopPadding;
    }

    @Override // android.view.View
    public boolean onHoverEvent(MotionEvent motionEvent) {
        int eventX = (int) getEventX(motionEvent);
        int eventY = (int) getEventY(motionEvent);
        if (motionEvent.getAction() == 9 || motionEvent.getAction() == 7) {
            for (int i = 0; i < this.accessibilityVirtualViewBounds.size(); i++) {
                if (this.accessibilityVirtualViewBounds.valueAt(i).contains(eventX, eventY)) {
                    int iKeyAt = this.accessibilityVirtualViewBounds.keyAt(i);
                    if (iKeyAt == this.currentFocusedVirtualView) {
                        return true;
                    }
                    this.currentFocusedVirtualView = iKeyAt;
                    sendAccessibilityEventForVirtualView(iKeyAt, 32768);
                    return true;
                }
            }
        } else if (motionEvent.getAction() == 10) {
            this.currentFocusedVirtualView = 0;
        }
        return super.onHoverEvent(motionEvent);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
    }

    @Override // android.view.View
    public AccessibilityNodeProvider getAccessibilityNodeProvider() {
        return new MessageAccessibilityNodeProvider();
    }

    public void sendAccessibilityEventForVirtualView(int i, int i2) {
        sendAccessibilityEventForVirtualView(i, i2, null);
    }

    private void sendAccessibilityEventForVirtualView(int i, int i2, String str) {
        if (((AccessibilityManager) getContext().getSystemService("accessibility")).isTouchExplorationEnabled()) {
            AccessibilityEvent accessibilityEventObtain = AccessibilityEvent.obtain(i2);
            accessibilityEventObtain.setPackageName(getContext().getPackageName());
            accessibilityEventObtain.setSource(this, i);
            if (str != null) {
                accessibilityEventObtain.getText().add(str);
            }
            if (getParent() != null) {
                getParent().requestSendAccessibilityEvent(this, accessibilityEventObtain);
            }
        }
    }

    public static PointF getMessageSize(int i, int i2) {
        return getMessageSize(i, i2, 0, 0);
    }

    int $r8$lambda$ys4Wv4_xsobPzyL8iEli8AfAHao(Spanned spanned, CodeHighlighting.Span span, CodeHighlighting.Span span2) {
            return spanned.getSpanStart(span2) - spanned.getSpanStart(span);
        }

        private int layoutFactCheck(int i) {
        int iDp;
        String displayCountry;
        int i2;
        int iMax;
        int i3;
        int i4;
        int iMax2;
        int i5;
        boolean z;
        MessageObject primaryMessageObject = getPrimaryMessageObject();
        this.factCheckHeight = 0;
        this.factCheckWidth = 0;
        MessageObject.GroupedMessagePosition groupedMessagePosition = this.currentPosition;
        boolean z2 = ((groupedMessagePosition != null && (groupedMessagePosition.flags & 8) == 0) || primaryMessageObject == null || primaryMessageObject.getFactCheck() == null || primaryMessageObject.isRepostPreview) ? false : true;
        this.hasFactCheck = z2;
        if (z2) {
            TLRPC.TL_factCheck factCheck = primaryMessageObject.getFactCheck();
            CharSequence factCheckText = primaryMessageObject.getFactCheckText();
            if (factCheck.need_check || factCheckText == null) {
                this.hasFactCheck = false;
                iDp = 0;
            } else {
                this.factCheckHeight += AndroidUtilities.dp(4.66f);
                this.factCheckTitle = new Text(LocaleController.getString(org.telegram.messenger.R.string.FactCheck), 14.0f, AndroidUtilities.bold());
                this.factCheckWhat = new Text(LocaleController.getString(org.telegram.messenger.R.string.FactCheckWhat), 11.0f);
                this.factCheckHeight += AndroidUtilities.dp(17.33f);
                this.factCheckWidth = (int) (AndroidUtilities.dp(20.0f) + this.factCheckTitle.getCurrentWidth() + this.factCheckWhat.getCurrentWidth() + AndroidUtilities.dp(18.0f));
                try {
                    displayCountry = new Locale(_UrlKt.FRAGMENT_ENCODE_SET, factCheck.country).getDisplayCountry(LocaleController.getInstance().getCurrentLocale());
                    while (true) {
                        int lineCount = this.factCheckTextLayout.getLineCount();
                        i3 = this.factCheckTextLayoutLeft;
                        if (i2 >= lineCount) {
                            break;
                        }
                        this.factCheckTextLayoutLeft = (int) Math.min(i3, this.factCheckTextLayout.getLineLeft(i2));
                        iMax = (int) Math.max(iMax, this.factCheckTextLayout.getLineRight(i2));
                        i2++;
                    }
                    while (true) {
                        int lineCount2 = this.factCheckText2Layout.getLineCount();
                        i5 = this.factCheckText2LayoutLeft;
                        if (i4 >= lineCount2) {
                            break;
                        }
                        this.factCheckText2LayoutLeft = (int) Math.min(i5, this.factCheckText2Layout.getLineLeft(i4));
                        iMax2 = (int) Math.max(iMax2, this.factCheckText2Layout.getLineRight(i4));
                        i4++;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                    displayCountry = factCheck.country;
                }
                com.exteragram.messenger.utils.ui.TextPaint textPaint = Theme.chat_replyTextPaint;
                Layout.Alignment alignment = Layout.Alignment.ALIGN_NORMAL;
                float fDp = AndroidUtilities.dp(1.0f);
                TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
                this.factCheckTextLayout = StaticLayoutEx.createStaticLayout(factCheckText, textPaint, i, alignment, 1.0f, fDp, false, truncateAt, i, 99999);
                this.factCheckText2Layout = StaticLayoutEx.createStaticLayout(LocaleController.formatString(org.telegram.messenger.R.string.FactCheckFooter, displayCountry), Theme.chat_titleLabelTextPaint, i, alignment, 1.0f, AndroidUtilities.dp(1.0f), false, truncateAt, i, 99999);
                this.factCheckTextLayoutLeft = this.factCheckTextLayout.getWidth();
                i2 = 0;
                iMax = 0;
                this.factCheckWidth = Math.max(this.factCheckWidth, Math.abs(iMax - i3) + AndroidUtilities.dp(20.0f));
                this.factCheckText2LayoutLeft = this.factCheckText2Layout.getWidth();
                i4 = 0;
                iMax2 = 0;
                this.factCheckWidth = Math.max(this.factCheckWidth, Math.abs(iMax2 - i5) + AndroidUtilities.dp(20.0f));
                StaticLayout staticLayout = this.factCheckTextLayout;
                int lineBottom = staticLayout.getLineBottom(staticLayout.getLineCount() - 1);
                this.factCheckTextLayoutHeight = lineBottom;
                int iDp2 = lineBottom + AndroidUtilities.dp(12.66f);
                StaticLayout staticLayout2 = this.factCheckText2Layout;
                int lineBottom2 = iDp2 + staticLayout2.getLineBottom(staticLayout2.getLineCount() - 1);
                int textSize = (int) (Theme.chat_replyTextPaint.getTextSize() * 3.5f * 1.4f);
                boolean z3 = this.factCheckTextLayout.getLineCount() > 3 && AndroidUtilities.dp(10.0f) + lineBottom2 > textSize;
                this.factCheckLarge = z3;
                if (z3) {
                    StaticLayout staticLayout3 = this.factCheckText2Layout;
                    z = staticLayout3.getLineRight(staticLayout3.getLineCount() - 1) - ((float) this.factCheckText2LayoutLeft) > ((float) (this.factCheckWidth - AndroidUtilities.dp(50.0f)));
                }
                this.factCheckTextLayoutLastLineEnd = z;
                if (this.factCheckLarge && !primaryMessageObject.factCheckExpanded) {
                    if (lineBottom2 < textSize) {
                        this.factCheckLarge = false;
                    }
                    lineBottom2 = Math.min(textSize, lineBottom2);
                }
                if (this.factCheckTextLayoutLastLineEnd) {
                    lineBottom2 = (int) (lineBottom2 + (Theme.chat_replyTextPaint.getTextSize() * 1.3f));
                }
                int i6 = this.factCheckHeight + lineBottom2;
                this.factCheckHeight = i6;
                this.factCheckHeight = i6 + AndroidUtilities.dp(6.66f);
                iDp = AndroidUtilities.dp(2.0f);
            }
        } else {
            iDp = 0;
        }
        if (this.hasFactCheck) {
            return this.factCheckHeight + iDp;
        }
        return 0;
    }

    public int getWidthForButtons() {
        TransitionParams transitionParams = this.transitionParams;
        if (transitionParams.animateWidthForButton) {
            return AndroidUtilities.lerp(transitionParams.animateFromWidthForButton, this.widthForButtons, transitionParams.animateChangeProgress);
        }
        return this.widthForButtons;
    }

    public void drawVideoTimestamps(Canvas canvas, int i) {
        float videoSavedProgress;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null || messageObject.isLivePhoto() || this.controlsAlpha <= 0.0f || !this.photoImage.getVisible()) {
            return;
        }
        MessageObject messageObject2 = this.currentMessageObject;
        if (!messageObject2.openedInViewer && messageObject2.getVideoStartsTimestamp() != -1) {
            videoSavedProgress = this.currentMessageObject.getVideoStartsTimestamp() / ((float) this.currentMessageObject.getDuration());
        } else {
            videoSavedProgress = this.currentMessageObject.getVideoSavedProgress();
        }
        float fClamp01 = Utilities.clamp01(videoSavedProgress);
        if (fClamp01 > 0.0f) {
            int[] roundRadius = this.photoImage.getRoundRadius();
            canvas.save();
            if (roundRadius[0] <= 0 && roundRadius[1] <= 0 && roundRadius[2] <= 0 && roundRadius[3] <= 0) {
                canvas.clipRect(this.photoImage.getImageX(), this.photoImage.getImageY(), this.photoImage.getImageX2(), this.photoImage.getImageY2());
            } else {
                if (this.photoImageClipPath == null) {
                    this.photoImageClipPath = new Path();
                    this.photoImageClipPathRadii = new float[8];
                }
                float[] fArr = this.photoImageClipPathRadii;
                float fMax = Math.max(0, roundRadius[0]);
                fArr[1] = fMax;
                fArr[0] = fMax;
                float[] fArr2 = this.photoImageClipPathRadii;
                float fMax2 = Math.max(0, roundRadius[1]);
                fArr2[3] = fMax2;
                fArr2[2] = fMax2;
                float[] fArr3 = this.photoImageClipPathRadii;
                float fMax3 = Math.max(0, roundRadius[2]);
                fArr3[5] = fMax3;
                fArr3[4] = fMax3;
                float[] fArr4 = this.photoImageClipPathRadii;
                float fMax4 = Math.max(0, roundRadius[3]);
                fArr4[7] = fMax4;
                fArr4[6] = fMax4;
                this.photoImageClipPath.rewind();
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(this.photoImage.getImageX(), this.photoImage.getImageY(), this.photoImage.getImageX2(), this.photoImage.getImageY2());
                this.photoImageClipPath.addRoundRect(rectF, this.photoImageClipPathRadii, Path.Direction.CW);
                canvas.clipPath(this.photoImageClipPath);
            }
            Theme.chat_videoProgressPaint.setColor(Theme.multAlpha(-1, this.controlsAlpha * 0.35f));
            canvas.drawRect(this.photoImage.getImageX(), this.photoImage.getImageY2() - AndroidUtilities.dp(3.0f), this.photoImage.getImageX2(), this.photoImage.getImageY2(), Theme.chat_videoProgressPaint);
            Theme.chat_videoProgressPaint.setColor(Theme.multAlpha(i, this.controlsAlpha));
            RectF rectF2 = AndroidUtilities.rectTmp;
            rectF2.set(this.photoImage.getImageX() - AndroidUtilities.dp(2.0f), this.photoImage.getImageY2() - AndroidUtilities.dp(3.0f), this.photoImage.getImageX() + (this.photoImage.getImageWidth() * fClamp01), this.photoImage.getImageY2());
            canvas.drawRoundRect(rectF2, AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), Theme.chat_videoProgressPaint);
            canvas.restore();
        }
    }

    public long getStarsPrice() {
        TLRPC.Message message;
        TLRPC.Message message2;
        MessageObject.GroupedMessages groupedMessages = this.currentMessagesGroup;
        if (groupedMessages != null) {
            ArrayList<MessageObject> arrayList = groupedMessages.messages;
            int size = arrayList.size();
            int i = 0;
            long j = 0;
            while (i < size) {
                MessageObject messageObject = arrayList.get(i);
                i++;
                MessageObject messageObject2 = messageObject;
                j += (messageObject2 == null || (message2 = messageObject2.messageOwner) == null) ? 0L : message2.paid_message_stars;
            }
            return j;
        }
        MessageObject messageObject3 = this.currentMessageObject;
        if (messageObject3 == null || (message = messageObject3.messageOwner) == null) {
            return 0L;
        }
        return message.paid_message_stars;
    }

    private int getNameHeight() {
        if (this.drawNameAvatar) {
            if (this.adminLayout == null) {
                return AndroidUtilities.dp(31.0f);
            }
            return AndroidUtilities.dp(37.66f);
        }
        return (int) (AndroidUtilities.dp(5.0f) + Theme.chat_namePaint.getTextSize());
    }

    private float getNameHeightAnimated() {
        float fLerp;
        TransitionParams transitionParams = this.transitionParams;
        boolean z = transitionParams.animateDrawAvatar;
        boolean z2 = this.drawNameAvatar;
        if (z) {
            fLerp = AndroidUtilities.lerp(!z2, z2, transitionParams.animateChangeProgress);
        } else {
            fLerp = z2 ? 1.0f : 0.0f;
        }
        return AndroidUtilities.lerp(AndroidUtilities.dp(5.0f) + Theme.chat_namePaint.getTextSize(), AndroidUtilities.dp(this.adminLayout == null ? 31.0f : 35.0f), fLerp);
    }

    private static boolean isSmallImageLinkPreviewType(String str) {
        return Common.ASSET_APP.equals(str) || "profile".equals(str) || "article".equals(str) || "telegram_bot".equals(str) || "telegram_user".equals(str) || "telegram_channel".equals(str) || "telegram_channel_direct".equals(str) || "telegram_megagroup".equals(str) || "telegram_voicechat".equals(str) || "telegram_videochat".equals(str) || "telegram_livestream".equals(str) || "telegram_channel_boost".equals(str) || "telegram_group_boost".equals(str) || "telegram_aicomposetone".equals(str);
    }

    private static void normalizePollPercents(boolean z, int i, ArrayList<PollButton> arrayList) {
        if (!z || i == 0 || arrayList == null || arrayList.isEmpty()) {
            return;
        }
        Collections.sort(arrayList, new Comparator() { // from class: org.telegram.ui.Cells.ChatMessageCell$$ExternalSyntheticLambda19
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return ChatMessageCell.m6792$r8$lambda$Jc420Fgm4D5BG3Pi1Z_AoCTIpU((ChatMessageCell.PollButton) obj, (ChatMessageCell.PollButton) obj2);
            }
        });
        int size = arrayList.size();
        for (int i2 = 0; i2 < size && i > 0; i2++) {
            PollButton pollButton = arrayList.get(i2);
            if (pollButton.percent > 0) {
                pollButton.percent++;
                i--;
            }
        }
    }

    public static /* synthetic */ int m6792$r8$lambda$Jc420Fgm4D5BG3Pi1Z_AoCTIpU(PollButton pollButton, PollButton pollButton2) {
        if (pollButton.decimal > pollButton2.decimal) {
            return -1;
        }
        if (pollButton.decimal >= pollButton2.decimal && pollButton.percent <= pollButton2.percent) {
            return pollButton.percent < pollButton2.percent ? -1 : 0;
        }
        return 1;
    }

    public boolean getPollAddButtonBounds(Rect rect) {
        PollAddButtonDrawable pollAddButtonDrawable = this.pollAddButtonDrawable;
        if (pollAddButtonDrawable == null || !this.pollAllowAdding) {
            return false;
        }
        rect.set(pollAddButtonDrawable.getBounds());
        return true;
    }

    private static void clearBlurredImage(ImageReceiver imageReceiver) {
        Bitmap bitmap;
        if (imageReceiver == null || (bitmap = imageReceiver.getBitmap()) == null) {
            return;
        }
        bitmap.recycle();
        imageReceiver.setImageBitmap((Bitmap) null);
    }

    private static String getCallMessageText(MessageObject messageObject, boolean z, boolean z2, boolean z3) {
        if (messageObject.messageOwner.action instanceof TLRPC.TL_messageActionConferenceCall) {
            if (messageObject.isOutOwner()) {
                return LocaleController.getString(org.telegram.messenger.R.string.ConferenceCallOutgoing);
            }
            if (z) {
                return LocaleController.getString(org.telegram.messenger.R.string.ConferenceCallMissed);
            }
            return LocaleController.getString(org.telegram.messenger.R.string.ConferenceCallIncoming);
        }
        if (messageObject.isOutOwner()) {
            if (z) {
                if (z3) {
                    return LocaleController.getString(org.telegram.messenger.R.string.CallMessageVideoOutgoingMissed);
                }
                return LocaleController.getString(org.telegram.messenger.R.string.CallMessageOutgoingMissed);
            }
            if (z3) {
                return LocaleController.getString(org.telegram.messenger.R.string.CallMessageVideoOutgoing);
            }
            return LocaleController.getString(org.telegram.messenger.R.string.CallMessageOutgoing);
        }
        if (z) {
            if (z3) {
                return LocaleController.getString(org.telegram.messenger.R.string.CallMessageVideoIncomingMissed);
            }
            return LocaleController.getString(org.telegram.messenger.R.string.CallMessageIncomingMissed);
        }
        if (z2) {
            if (z3) {
                return LocaleController.getString(org.telegram.messenger.R.string.CallMessageVideoIncomingDeclined);
            }
            return LocaleController.getString(org.telegram.messenger.R.string.CallMessageIncomingDeclined);
        }
        if (z3) {
            return LocaleController.getString(org.telegram.messenger.R.string.CallMessageVideoIncoming);
        }
        return LocaleController.getString(org.telegram.messenger.R.string.CallMessageIncoming);
    }
}
