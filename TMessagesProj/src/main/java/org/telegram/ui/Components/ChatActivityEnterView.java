package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.util.Pair;
import android.util.Property;
import android.view.ActionMode;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.util.Predicate;
import androidx.core.view.ContentInfoCompat;
import androidx.core.view.OnReceiveContentListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.customview.widget.ExploreByTouchHelper;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.ChatListItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.IconPackType;
import com.exteragram.messenger.VideoMessagesCamera;
import com.exteragram.messenger.ai.AiConfig;
import com.exteragram.messenger.ai.AiController;
import com.exteragram.messenger.ai.network.Client;
import com.exteragram.messenger.ai.ui.AiResponseAlert;
import com.exteragram.messenger.ai.ui.activities.AiPreferencesActivity;
import com.exteragram.messenger.ai.ui.activities.EditServiceActivity;
import com.exteragram.messenger.components.ActionRow;
import com.exteragram.messenger.components.ChatActivityEnterViewStaticIconView;
import com.exteragram.messenger.components.TranslateBeforeSendWrapper;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.utils.system.SystemUtils;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.BotForumHelper;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
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
import org.telegram.messenger.NotificationsSettingsFacade;
import org.telegram.messenger.R;
import org.telegram.messenger.RichMessageLayout;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SharedPrefsHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.utils.EphemeralMessagesHelper;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.tgnet.tl.TL_iv;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Business.BusinessLinksController;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Premium.boosts.BoostRepository;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProviderThemed;
import org.telegram.ui.Components.chat.SendButtonBlockedByTypingView;
import org.telegram.ui.Components.chat.layouts.ChatActivitySideControlsButtonsLayout;
import org.telegram.ui.Components.inset.WindowInsetsInAppController;
import org.telegram.ui.ContentPreviewViewer;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.GroupStickersActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.LinkManager;
import org.telegram.ui.MessageSendPreview;
import org.telegram.ui.MultiContactsSelectorBottomSheet;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.StickersActivity;
import org.telegram.ui.Stories.HighlightMessageSheet;
import org.telegram.ui.Stories.recorder.CaptionContainerView;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.TopicsFragment;
import org.telegram.ui.bots.BotCommandsMenuContainer;
import org.telegram.ui.bots.BotCommandsMenuView;
import org.telegram.ui.bots.BotKeyboardView;
import org.telegram.ui.bots.BotWebViewSheet;
import org.telegram.ui.bots.ChatActivityBotWebViewButton;
import org.telegram.ui.bots.WebViewRequestProps;
import org.telegram.ui.iv.BlockRow;
import org.telegram.ui.iv.RichEditor;
import org.telegram.ui.iv.RichHtml;
import org.telegram.ui.iv.RichMessageConvert;

public class ChatActivityEnterView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate, StickersAlert.StickersAlertDelegate, SuggestEmojiView.AnchorViewDelegate, FactorAnimator.Target, Theme.Colorable {
    private final Property<? super View, Float> ATTACH_LAYOUT_ALPHA;
    private final Property<? super View, Float> ATTACH_LAYOUT_TRANSLATION_X;
    private final Property<? super View, Float> EMOJI_BUTTON_ALPHA;
    private final Property<? super View, Float> EMOJI_BUTTON_SCALE;
    private final Property<? super View, Float> MESSAGE_TEXT_TRANSLATION_X;
    private AccountInstance accountInstance;
    private ActionBarMenuSubItem actionScheduleButton;
    private AdjustPanLayoutHelper adjustPanLayoutHelper;
    private ImageView aiButton;
    private AiButtonDrawable aiButtonIcon;
    public HintView2 aiHint;
    private boolean allowAnimatedEmoji;
    public boolean allowBlur;
    private boolean allowGifs;
    private boolean allowShowTopView;
    private boolean allowStickers;
    protected int animatedTop;
    private int animatingContentType;
    private Runnable animationEndRunnable;
    private HashMap<View, Float> animationParamsX;
    private final BoolAnimator animatorEphemeralMessageVisibility;
    private final FactorAnimator animatorInputFieldHeight;
    private final BoolAnimator animatorIsBlockedByStreaming;
    private final BoolAnimator animatorTopViewVisibility;
    private ImageView attachButton;
    private float attachButtonAlpha;
    private ViewPropertyAnimator attachButtonAnimator;
    private LinearLayout attachLayout;
    private float attachLayoutAlpha;
    private float attachLayoutPaddingAlpha;
    protected float attachLayoutPaddingTranslationX;
    private float attachLayoutTranslationX;
    protected RecordedAudioPlayerView audioTimelineView;
    private TLRPC.TL_document audioToSend;
    private MessageObject audioToSendMessageObject;
    private String audioToSendPath;
    private FrameLayout audioVideoButtonContainer;
    private boolean audioVideoButtonContainerForbidden;
    private View audioVideoSendButton;
    Paint backgroundPaint;
    public HintView2 birthdayHint;
    private Rect blurBounds;
    private ImageView botButton;
    private ReplaceableIconDrawable botButtonDrawable;
    private MessageObject botButtonsMessageObject;
    int botCommandLastPosition;
    int botCommandLastTop;
    private BotCommandsMenuView.BotCommandsAdapter botCommandsAdapter;
    private BotCommandsMenuView botCommandsMenuButton;
    public BotCommandsMenuContainer botCommandsMenuContainer;
    private int botCount;
    private BotKeyboardView botKeyboardView;
    private boolean botKeyboardViewVisible;
    private BotMenuButtonType botMenuButtonType;
    private String botMenuWebViewTitle;
    private String botMenuWebViewUrl;
    private MessageObject botMessageObject;
    private TLRPC.TL_replyKeyboardMarkup botReplyMarkup;
    private ChatActivityBotWebViewButton botWebViewButton;
    private final AnimatedFloat bottomGradientAlpha;
    private boolean calledRecordRunnable;
    private ItemOptions cameraChooserItemOptions;
    private Drawable cameraDrawable;
    private Drawable cameraOutline;
    private boolean canWriteToChannel;
    private ImageView cancelBotButton;
    private boolean canceledByGesture;
    private boolean captionAbove;
    private boolean captionLimitBulletinShown;
    public NumberTextView captionLimitView;
    private boolean clearBotButtonsOnKeyboardOpen;
    private final LinearGradient clipGradient;
    private final Matrix clipMatrix;
    private boolean closeAnimationInProgress;
    private int codePointCount;
    private int commonInputType;
    private float composeShadowAlpha;
    private float controlsScale;
    public ControlsView controlsView;
    boolean ctrlPressed;
    private int currentAccount;
    private float currentIslandTotalHeight;
    private float currentIslandTotalHeightTarget;
    private int currentLimit;
    private int currentPopupContentType;
    private ChatActivityEnterViewDelegate delegate;
    private ImageView deleteRichDraftButton;
    private boolean destroyed;
    private long dialog_id;
    private final Runnable dismissSendPreview;
    private boolean dismissSendPreviewSent;
    private float distCanMove;
    private SendButton doneButton;
    private AnimatorSet doneButtonAnimation;
    boolean doneButtonEnabled;
    private float doneButtonEnabledProgress;
    private Drawable doneCheckDrawable;
    private Paint dotPaint;
    private CharSequence draftMessage;
    private boolean draftSearchWebpage;
    private TL_account.TL_businessChatLink editingBusinessLink;
    private boolean editingCaption;
    private MessageObject editingMessageObject;
    private long effectId;
    private View emojiButton;
    float emojiButtonAlpha;
    float emojiButtonPaddingAlpha;
    float emojiButtonPaddingScale;
    private boolean emojiButtonRestricted;
    float emojiButtonScale;
    private int emojiPadding;
    private boolean emojiTabOpen;
    private EmojiView emojiView;
    private boolean emojiViewFrozen;
    public boolean emojiViewVisible;
    private float exitTransition;
    private ImageView expandStickersButton;
    private Runnable focusRunnable;
    private boolean forceShowSendButton;
    private ImageView giftButton;
    private final Paint gradientPaint;
    private boolean hasBotCommands;
    private boolean hasQuickReplies;
    private boolean hasRecordVideo;
    private Runnable hideKeyboardRunnable;
    private float horizontalPadding;
    float idleProgress;
    private boolean ignoreSendAsButtonUpdates;
    private boolean ignoreTextChange;
    private TLRPC.ChatFull info;
    private int innerTextChange;
    private final boolean isChat;
    private boolean isInVideoMode;
    private boolean isInitLineCount;
    private boolean isLiveComment;
    private boolean isPaste;
    private boolean isPaused;
    public boolean isStories;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean keyboardVisible;
    private int lastAttachVisible;
    private LongSparseArray<TL_bots.BotInfo> lastBotInfo;
    private int lastRecordState;
    private BusinessLinkPresetMessage lastSavedBusinessLinkMessage;
    private int lastSizeChangeValue1;
    private boolean lastSizeChangeValue2;
    private long lastTypingTimeSend;
    private int lineCount;
    private int[] location;
    private float lockAnimatedTranslation;
    private Drawable lockShadowDrawable;
    private EditTextBoldCursor mOverrideEditTextView;
    private View.AccessibilityDelegate mediaMessageButtonsDelegate;
    public EditTextCaption messageEditText;
    public FrameLayout messageEditTextContainer;
    private boolean messageEditTextEnabled;
    private ArrayList<TextWatcher> messageEditTextWatchers;
    public MessageSendPreview messageSendPreview;
    private float messageTextPaddingTranslationX;
    private float messageTextTranslationX;
    boolean messageTransitionIsRunning;
    private TLRPC.WebPage messageWebPage;
    private boolean messageWebPageSearch;
    private Drawable micDrawable;
    private Drawable micOutline;
    private long millisecondsRecorded;
    private Runnable moveToSendStateRunnable;
    private boolean needShowTopView;
    private AnimationNotificationsLocker notificationsLocker;
    private ImageView notifyButton;
    private CrossOutDrawable notifySilentDrawable;
    private Runnable onEmojiSearchClosed;
    private final Runnable onFinishInitCameraRunnable;
    private Runnable onKeyboardClosed;
    public boolean onceVisible;
    private Runnable openKeyboardRunnable;
    private int originalViewHeight;
    private CharSequence overrideHint;
    private CharSequence overrideHint2;
    private boolean overrideKeyboardAnimation;
    private long paidMessagesPrice;
    private Paint paint;
    private AnimatorSet panelAnimation;
    private Activity parentActivity;
    private ChatActivity parentFragment;
    private RectF pauseRect;
    private TLRPC.KeyboardButton pendingLocationButton;
    private MessageObject pendingMessageObject;
    private int popupX;
    private int popupY;
    public boolean preventInput;
    private CloseProgressDrawable2 progressDrawable;
    private ImageView reactionsButton;
    private Runnable recordAudioVideoRunnable;
    private boolean recordAudioVideoRunnableStarted;
    private RecordCircle recordCircle;
    private Property<RecordCircle, Float> recordCircleScale;
    private Property<RecordCircle, Float> recordControlsCircleScale;
    private RLottieImageView recordDeleteImageView;
    private RecordDot recordDot;
    private int recordInterfaceState;
    private boolean recordIsCanceled;
    private FrameLayout recordPanel;
    private AnimatorSet recordPannelAnimation;
    private LinearLayout recordTimeContainer;
    private TimerView recordTimerView;
    public FrameLayout recordedAudioPanel;
    private boolean recordingAudioVideo;
    public int recordingGuid;
    private Rect rect;
    private Paint redDotPaint;
    private boolean removeEmojiViewAfterAnimation;
    private MessageObject replyingMessageObject;
    private ChatActivity.ReplyQuote replyingQuote;
    private MessageObject replyingTopMessage;
    private int resizeForTopViewLastMinHeight;
    private boolean resizeForTopViewLastShow;
    private int resizeForTopViewLastTopMargin;
    private final Theme.ResourcesProvider resourcesProvider;
    private ImageView richButton;
    private boolean richDraftActive;
    private TL_iv.RichMessage richDraftMessage;
    public RichMessageLayout.PreviewView richDraftPreview;
    private Property<View, Integer> roundedTranslationYProperty;
    private Runnable runEmojiPanelAnimation;
    private AnimatorSet runningAnimation;
    private AnimatorSet runningAnimation2;
    private AnimatorSet runningAnimationAudio;
    private int runningAnimationType;
    private float scale;
    private boolean scheduleButtonHidden;
    private ImageView scheduledButton;
    private AnimatorSet scheduledButtonAnimation;
    private ValueAnimator searchAnimator;
    private float searchToOpenProgress;
    private int searchingType;
    private SendButton sendButton;
    private int sendButtonBackgroundColor;
    private SendButtonBlockedByTypingView sendButtonBlockedByTypingView;
    public FrameLayout sendButtonContainer;
    private boolean sendButtonEnabled;
    private ItemOptions sendButtonItemOptions;
    private boolean sendButtonVisible;
    private boolean sendByEnter;
    private Drawable sendDrawable;
    private ImageView sendOutlineView;
    public boolean sendPlainEnabled;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout sendPopupLayout;
    private ActionBarPopupWindow sendPopupWindow;
    private Rect sendRect;
    private boolean sendRoundEnabled;
    private HintView2 sendSuggestHintView;
    private boolean sendVoiceEnabled;
    private ActionBarMenuSubItem sendWhenOnlineButton;
    private SenderSelectPopup senderSelectPopupWindow;
    public SenderSelectView senderSelectView;
    private long sentFromPreview;
    private Runnable setTextFieldRunnable;
    boolean shiftPressed;
    protected boolean shouldAnimateEditTextWithBounds;
    public boolean shouldDrawBackground;
    public boolean shouldDrawRecordedAudioPanelInParent;
    private boolean showKeyboardOnResume;
    private boolean showTooltip;
    private long showTooltipStartTime;
    private Runnable showTopViewRunnable;
    private boolean shownAiButton;
    private boolean shownRichButton;
    private ChatActivitySideControlsButtonsLayout sideButtons;
    private boolean silent;
    private SizeNotifierFrameLayout sizeNotifierLayout;
    private int slideDelta;
    private SlideTextView slideText;
    private float slideToCancelLockProgress;
    private float slideToCancelProgress;
    private SlowModeBtn slowModeButton;
    private int slowModeTimer;
    private boolean smoothKeyboard;
    private float snapAnimationProgress;
    public final ColoredImageSpan[] spans;
    private float startTranslation;
    private float startedDraggingX;
    private AnimatedArrowDrawable stickersArrow;
    private boolean stickersDragging;
    private boolean stickersEnabled;
    private boolean stickersExpanded;
    private int stickersExpandedHeight;
    private Animator stickersExpansionAnim;
    private float stickersExpansionProgress;
    private boolean stickersTabOpen;
    private ImageView suggestButton;
    private ValueAnimator suggestButtonAppear;
    private boolean suggestButtonVisible;
    public FrameLayout textFieldContainer;
    boolean textTransitionIsRunning;
    private float tooltipAlpha;
    private final AnimatedFloat topGradientAlpha;
    protected View topView;
    protected boolean topViewShowed;
    private float transformToSeekbar;
    private TrendingStickersAlert trendingStickersAlert;
    private Runnable updateExpandabilityRunnable;
    private Runnable updateSlowModeRunnable;
    private TLRPC.UserFull userInfo;
    protected VideoTimelineView videoTimelineView;
    private VideoEditedInfo videoToSendMessageObject;
    private ViewGroup viewParentForEmojiView;
    public boolean voiceOnce;
    private boolean waitingForKeyboardOpen;
    private boolean waitingForKeyboardOpenAfterAnimation;
    private PowerManager.WakeLock wakeLock;
    private boolean wasSendTyping;
    private WindowInsetsInAppController windowInsetsInAppController;

    public enum BotMenuButtonType {
        NO_BUTTON,
        COMMANDS,
        WEB_VIEW
    }

    public interface ChatActivityEnterViewDelegate {
        default void bottomPanelTranslationYChanged(float f) {
        }

        default boolean checkCanRemoveRestrictionsByBoosts() {
            return false;
        }

        void didPressAttachButton();

        default void didPressSuggestionButton() {
        }

        default int getContentViewHeight() {
            return 0;
        }

        default TLRPC.Peer getDefaultSendAs() {
            return null;
        }

        default ChatActivity.ReplyQuote getReplyQuote() {
            return null;
        }

        default TL_stories.StoryItem getReplyToStory() {
            return null;
        }

        default TLRPC.TL_channels_sendAsPeers getSendAsPeers() {
            return null;
        }

        default boolean hasForwardingMessages() {
            return false;
        }

        default boolean hasScheduledMessages() {
            return true;
        }

        boolean isVideoRecordingPaused();

        default int measureKeyboardHeight() {
            return 0;
        }

        void needChangeVideoPreviewState(int i, float f);

        void needSendTyping();

        void needShowMediaBanHint();

        void needStartRecordAudio(int i);

        void needStartRecordVideo(int i, boolean z, int i2, int i3, int i4, long j, long j2);

        void onAttachButtonHidden();

        void onAttachButtonShow();

        void onAudioVideoInterfaceUpdated();

        default void onContextMenuClose() {
        }

        default void onContextMenuOpen() {
        }

        default void onEditTextScroll() {
        }

        default void onEmojiViewTabChanged() {
        }

        default void onKeyboardRequested() {
        }

        void onMessageEditEnd(boolean z);

        void onMessageSend(CharSequence charSequence, boolean z, int i, int i2, long j);

        void onPreAudioVideoRecord();

        void onSendLongClick();

        void onStickersExpandedChange();

        void onStickersTab(boolean z);

        void onSwitchRecordMode(boolean z);

        void onTextChanged(CharSequence charSequence, boolean z, boolean z2);

        void onTextSelectionChanged(int i, int i2);

        void onTextSpansChanged(CharSequence charSequence);

        default void onTrendingStickersShowed(boolean z) {
        }

        void onUpdateSlowModeButton(View view, boolean z, CharSequence charSequence);

        void onWindowSizeChanged(int i);

        default boolean onceVoiceAvailable() {
            return false;
        }

        default void openScheduledMessages() {
        }

        default void prepareMessageSending() {
        }

        default void scrollToSendingMessage() {
        }

        default boolean setDefaultSendAs(long j, long j2) {
            return false;
        }

        void setFrontface(boolean z);

        void toggleVideoRecordingPause();
    }

    public static void lambda$showPauseHint$0(HintView2 hintView2) {
            removeView(hintView2);
            if (this.pauseHint == hintView2) {
                this.pauseHint = null;
            }
        }

        public void showOnceHint() {
            int i;
            hideHintView();
            HintView2 hintView2 = new HintView2(getContext(), 2);
            this.onceHint = hintView2;
            hintView2.setJoint(1.0f, 0.0f);
            this.onceHint.setMultilineText(true);
            boolean z = ChatActivityEnterView.this.isInVideoMode;
            ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
            if (z) {
                i = chatActivityEnterView.voiceOnce ? R.string.VideoSetOnceHintEnabled : R.string.VideoSetOnceHint;
            } else {
                i = chatActivityEnterView.voiceOnce ? R.string.VoiceSetOnceHintEnabled : R.string.VoiceSetOnceHint;
            }
            this.onceHint.setText(AndroidUtilities.replaceTags(LocaleController.getString(i)));
            HintView2 hintView3 = this.onceHint;
            hintView3.setMaxWidthPx(HintView2.cutInFancyHalf(hintView3.getText(), this.onceHint.getTextPaint()));
            if (ChatActivityEnterView.this.voiceOnce) {
                this.onceHint.setIcon(R.raw.fire_on);
            } else {
                MessagesController.getGlobalMainSettings().edit().putInt("voiceoncehint", MessagesController.getGlobalMainSettings().getInt("voiceoncehint", 0) + 1).apply();
            }
            addView(this.onceHint, LayoutHelper.createFrame(-1, -1.0f, 119, 0.0f, 0.0f, 54.0f, 58.0f));
            final HintView2 hintView4 = this.onceHint;
            hintView4.setOnHiddenListener(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$ControlsView$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$showOnceHint$1(hintView4);
                }
            });
            this.onceHint.show();
        }

        public Boolean lambda$drawChild$0(Canvas canvas, View view, long j) {
            return Boolean.valueOf(super.drawChild(canvas, view, j));
        }
    }

    public void lambda$new$0() {
        this.waitingForKeyboardOpenAfterAnimation = false;
        openKeyboardInternal();
    }

    public void lambda$new$5(View view) {
        AdjustPanLayoutHelper adjustPanLayoutHelper = this.adjustPanLayoutHelper;
        if ((adjustPanLayoutHelper == null || !adjustPanLayoutHelper.animationInProgress()) && this.attachLayoutPaddingAlpha != 0.0f) {
            this.delegate.didPressAttachButton();
        }
    }

    public void lambda$new$6(long j, Theme.ResourcesProvider resourcesProvider, TL_iv.RichMessage richMessage, Integer num, Integer num2, Boolean bool) {
        saveRichDraft(richMessage);
        if (isInScheduleMode() && num.intValue() == 0) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, j, new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView.21
                @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                public void didSelectDate(boolean z, int i, int i2) {
                    ChatActivityEnterView.this.sendMessageInternal(z, i, i2, 0L, true);
                }
            }, resourcesProvider);
        } else {
            sendMessageInternal(bool.booleanValue(), num.intValue(), num2.intValue(), 0L, true);
        }
    }

    public void lambda$new$8(long j, Theme.ResourcesProvider resourcesProvider, CharSequence charSequence, Integer num, Integer num2, Boolean bool) {
        this.messageEditText.setText(charSequence);
        if (this.editingMessageObject != null) {
            doneEditingMessage();
        } else if (isInScheduleMode() && num.intValue() == 0) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, j, new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView.22
                @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                public void didSelectDate(boolean z, int i, int i2) {
                    boolean zSendMessageInternal = ChatActivityEnterView.this.sendMessageInternal(z, i, i2, 0L, true);
                    MessageSendPreview messageSendPreview = ChatActivityEnterView.this.messageSendPreview;
                    if (messageSendPreview != null) {
                        messageSendPreview.dismiss(!zSendMessageInternal);
                        ChatActivityEnterView.this.messageSendPreview = null;
                    }
                }
            }, resourcesProvider);
        } else {
            sendMessageInternal(bool.booleanValue(), num.intValue(), num2.intValue(), 0L, true);
        }
    }

    public void lambda$onTouchEvent$0(Long l) {
            ChatActivityEnterView.this.sendMessageInternal(true, 0, 0, l.longValue(), false);
        }

        public void lambda$new$12(View view) {
        MessageSendPreview messageSendPreview = this.messageSendPreview;
        if (messageSendPreview == null || !messageSendPreview.isShowing()) {
            AnimatorSet animatorSet = this.runningAnimationAudio;
            if ((animatorSet == null || !animatorSet.isRunning()) && this.moveToSendStateRunnable == null) {
                sendMessage();
            }
        }
    }

    public public int getMessagesCount() {
        MessagePreviewParams messagePreviewParams;
        ChatActivity chatActivity = this.parentFragment;
        int forwardedMessagesCount = (chatActivity == null || (messagePreviewParams = chatActivity.messagePreviewParams) == null) ? 0 : messagePreviewParams.getForwardedMessagesCount();
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null && !TextUtils.isEmpty(editTextCaption.getText())) {
            String trimmedString = SendMessagesHelper.getTrimmedString(this.messageEditText.getText().toString());
            int maxMessageLength = this.accountInstance.getMessagesController().getMaxMessageLength();
            if (trimmedString.length() != 0) {
                forwardedMessagesCount += (int) Math.ceil(trimmedString.length() / maxMessageLength);
            } else {
                forwardedMessagesCount++;
            }
        } else if (hasAudioToSend()) {
            forwardedMessagesCount++;
        }
        return Math.max(1, forwardedMessagesCount);
    }

    public void createCaptionLimitView() {
        if (this.captionLimitView != null) {
            return;
        }
        NumberTextView numberTextView = new NumberTextView(getContext());
        this.captionLimitView = numberTextView;
        numberTextView.setVisibility(8);
        this.captionLimitView.setTextSize(15);
        this.captionLimitView.setTextColor(getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
        this.captionLimitView.setTypeface(AndroidUtilities.bold());
        this.captionLimitView.setCenterAlign(true);
        addView(this.captionLimitView, Math.min(2, getChildCount()), LayoutHelper.createFrame(48, 20.0f, 85, 3.0f, 0.0f, 0.0f, 48.0f));
    }

    private void createScheduledButton() {
        if (this.scheduledButton != null || this.parentFragment == null) {
            return;
        }
        Drawable drawableMutate = getContext().getResources().getDrawable(R.drawable.input_calendar1).mutate();
        Drawable drawableMutate2 = getContext().getResources().getDrawable(R.drawable.input_calendar2).mutate();
        int themedColor = getThemedColor(Theme.key_glass_defaultIcon);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        drawableMutate.setColorFilter(new PorterDuffColorFilter(themedColor, mode));
        drawableMutate2.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_recordedVoiceDot), mode));
        CombinedDrawable combinedDrawable = new CombinedDrawable(drawableMutate, drawableMutate2);
        ImageView imageView = new ImageView(getContext()) { // from class: org.telegram.ui.Components.ChatActivityEnterView.30
            private float innerTranslationX;

            @Override // android.view.View
            public float getTranslationX() {
                return this.innerTranslationX;
            }

            @Override // android.view.View
            public void setTranslationX(float f) {
                this.innerTranslationX = f;
                float fDp = AndroidUtilities.dp(-48.0f) + this.innerTranslationX;
                ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                super.setTranslationX(fDp + chatActivityEnterView.attachLayoutPaddingTranslationX + chatActivityEnterView.attachLayoutTranslationX + (AndroidUtilities.dp((ChatActivityEnterView.this.giftButton == null || ChatActivityEnterView.this.giftButton.getVisibility() != 0) ? 0.0f : -48.0f) * (ChatActivityEnterView.this.giftButton == null ? 0.0f : ChatActivityEnterView.this.giftButton.getAlpha())) + (AndroidUtilities.dp((ChatActivityEnterView.this.botButton == null || ChatActivityEnterView.this.botButton.getVisibility() != 0) ? 0.0f : -48.0f) * (ChatActivityEnterView.this.botButton != null ? ChatActivityEnterView.this.botButton.getAlpha() : 0.0f)));
            }
        };
        this.scheduledButton = imageView;
        imageView.setImageDrawable(combinedDrawable);
        this.scheduledButton.setVisibility(8);
        this.scheduledButton.setContentDescription(LocaleController.getString(R.string.ScheduledMessages));
        this.scheduledButton.setScaleType(ImageView.ScaleType.CENTER);
        this.scheduledButton.setBackground(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector)));
        this.messageEditTextContainer.addView(this.scheduledButton, 2, LayoutHelper.createFrame(48, 48, 85));
        this.scheduledButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda27
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$createScheduledButton$15(view);
            }
        });
        this.scheduledButton.setTranslationX(0.0f);
    }

    public void lambda$createGiftButton$17(AlertDialog alertDialog, TLRPC.User user, boolean z, List list) {
        alertDialog.dismiss();
        new GiftSheet(getContext(), this.currentAccount, user.id, BoostRepository.filterGiftOptionsByBilling(BoostRepository.filterGiftOptions(list, 1)), null).setBirthday(z).show();
    }

    public void lambda$createExpandStickersButton$23(View view) {
        EmojiView emojiView;
        EditTextCaption editTextCaption;
        if (this.expandStickersButton.getVisibility() == 0 && this.expandStickersButton.getAlpha() == 1.0f && !this.waitingForKeyboardOpen) {
            if (this.keyboardVisible && (editTextCaption = this.messageEditText) != null && editTextCaption.isFocused()) {
                return;
            }
            if (this.stickersExpanded) {
                if (this.searchingType != 0) {
                    setSearchingTypeInternal(0, true);
                    this.emojiView.closeSearch(true);
                    this.emojiView.hideSearchKeyboard();
                    if (this.emojiTabOpen) {
                        checkSendButton(true);
                    }
                } else if (!this.stickersDragging && (emojiView = this.emojiView) != null) {
                    emojiView.showSearchField(false);
                }
            } else if (!this.stickersDragging) {
                this.emojiView.showSearchField(true);
            }
            if (this.stickersDragging) {
                return;
            }
            setStickersExpanded(!this.stickersExpanded, true, false);
        }
    }

    private void createRecordAudioPanel() {
        if (this.recordedAudioPanel != null) {
            return;
        }
        FrameLayout frameLayout = new FrameLayout(getContext()) { // from class: org.telegram.ui.Components.ChatActivityEnterView.36
            @Override // android.view.View
            public void setVisibility(int i) {
                super.setVisibility(i);
                ChatActivityEnterView.this.updateSendAsButton();
            }
        };
        this.recordedAudioPanel = frameLayout;
        frameLayout.setVisibility(this.audioToSend == null ? 8 : 0);
        this.recordedAudioPanel.setFocusable(true);
        this.recordedAudioPanel.setFocusableInTouchMode(true);
        this.recordedAudioPanel.setClickable(true);
        this.messageEditTextContainer.addView(this.recordedAudioPanel, LayoutHelper.createFrame(-1, 48, 80));
        RLottieImageView rLottieImageView = new RLottieImageView(getContext());
        this.recordDeleteImageView = rLottieImageView;
        rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
        this.recordDeleteImageView.setAnimation(R.raw.chat_audio_record_delete_2, 28, 28);
        this.recordDeleteImageView.getAnimatedDrawable().setInvalidateOnProgressSet(true);
        updateRecordedDeleteIconColors();
        this.recordDeleteImageView.setContentDescription(LocaleController.getString("Delete", R.string.Delete));
        this.recordDeleteImageView.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(Theme.key_listSelector)));
        this.recordedAudioPanel.addView(this.recordDeleteImageView, LayoutHelper.createFrame(48, 48.0f));
        this.recordDeleteImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda80
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$createRecordAudioPanel$24(view);
            }
        });
        VideoTimelineView videoTimelineView = new VideoTimelineView(getContext());
        this.videoTimelineView = videoTimelineView;
        videoTimelineView.setVisibility(4);
        VideoTimelineView videoTimelineView2 = this.videoTimelineView;
        videoTimelineView2.useClip = !this.shouldDrawBackground;
        videoTimelineView2.setRoundFrames(true);
        this.videoTimelineView.setDelegate(new VideoTimelineView.VideoTimelineViewDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView.37
            @Override // org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate
            public void onLeftProgressChanged(float f) {
                if (ChatActivityEnterView.this.videoToSendMessageObject == null) {
                    return;
                }
                ChatActivityEnterView.this.videoToSendMessageObject.startTime = (long) (ChatActivityEnterView.this.videoToSendMessageObject.estimatedDuration * f);
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(2, f);
            }

            @Override // org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate
            public void onRightProgressChanged(float f) {
                if (ChatActivityEnterView.this.videoToSendMessageObject == null) {
                    return;
                }
                ChatActivityEnterView.this.videoToSendMessageObject.endTime = (long) (ChatActivityEnterView.this.videoToSendMessageObject.estimatedDuration * f);
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(2, f);
            }

            @Override // org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate
            public void didStartDragging() {
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(1, 0.0f);
            }

            @Override // org.telegram.ui.Components.VideoTimelineView.VideoTimelineViewDelegate
            public void didStopDragging() {
                ChatActivityEnterView.this.delegate.needChangeVideoPreviewState(0, 0.0f);
            }
        });
        this.recordedAudioPanel.addView(this.videoTimelineView, LayoutHelper.createFrame(-1, -1.0f, 19, 56.0f, 0.0f, 8.0f, 0.0f));
        VideoTimelineView.TimeHintView timeHintView = new VideoTimelineView.TimeHintView(getContext());
        this.videoTimelineView.setTimeHintView(timeHintView);
        this.sizeNotifierLayout.addView(timeHintView, LayoutHelper.createFrame(-1, -2.0f, 80, 0.0f, 0.0f, 0.0f, 52.0f));
        RecordedAudioPlayerView recordedAudioPlayerView = new RecordedAudioPlayerView(getContext(), this.resourcesProvider);
        this.audioTimelineView = recordedAudioPlayerView;
        this.recordedAudioPanel.addView(recordedAudioPlayerView, LayoutHelper.createFrame(-1, 32.0f, 19, 48.0f, 0.0f, 4.0f, 0.0f));
        updateFieldRight(this.lastAttachVisible);
    }

    public void lambda$createSenderSelectView$32(View view) {
        TLRPC.Peer defaultSendAs;
        int i;
        int iDp;
        ChatActivity chatActivity;
        if (!this.isLiveComment ? getTranslationY() != 0.0f : isPopupShowing()) {
            this.onEmojiSearchClosed = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda93
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$createSenderSelectView$25();
                }
            };
            if (this.isLiveComment) {
                hidePopup(true, false);
                return;
            } else {
                hidePopup(true, true);
                return;
            }
        }
        if (this.delegate.measureKeyboardHeight() > AndroidUtilities.dp(20.0f)) {
            int contentViewHeight = this.delegate.getContentViewHeight();
            int iMeasureKeyboardHeight = this.delegate.measureKeyboardHeight();
            if (iMeasureKeyboardHeight <= AndroidUtilities.dp(20.0f)) {
                contentViewHeight += iMeasureKeyboardHeight;
            }
            if (this.emojiViewVisible) {
                contentViewHeight -= getEmojiPadding();
            }
            if (contentViewHeight < AndroidUtilities.dp(200.0f)) {
                this.onKeyboardClosed = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda94
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$createSenderSelectView$26();
                    }
                };
                closeKeyboard();
                return;
            }
        }
        if (this.delegate.getSendAsPeers() != null) {
            try {
                view.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
            if (senderSelectPopup != null) {
                senderSelectPopup.setPauseNotifications(false);
                this.senderSelectPopupWindow.startDismissAnimation(new SpringAnimation[0]);
                return;
            }
            final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            final TLRPC.ChatFull chatFull = null;
            if (this.isLiveComment) {
                defaultSendAs = this.delegate.getDefaultSendAs();
            } else {
                MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-this.dialog_id));
                TLRPC.ChatFull chatFull2 = MessagesController.getInstance(this.currentAccount).getChatFull(-this.dialog_id);
                chatFull = chatFull2;
                defaultSendAs = chatFull2 != null ? chatFull2.default_send_as : null;
            }
            if (defaultSendAs == null && this.delegate.getSendAsPeers() != null && !this.delegate.getSendAsPeers().peers.isEmpty()) {
                defaultSendAs = this.delegate.getSendAsPeers().peers.get(0).peer;
            }
            TLRPC.Peer peer = defaultSendAs;
            boolean zIsChannelAndNotMegaGroup = ChatObject.isChannelAndNotMegaGroup(messagesController.getChat(Long.valueOf(-this.dialog_id)));
            if (this.isLiveComment) {
            } else {
                this.parentFragment.getParentLayout().getOverlayContainerView();
            }
            AnonymousClass38 anonymousClass38 = new AnonymousClass38(getContext(), this.parentFragment, messagesController, zIsChannelAndNotMegaGroup, peer, this.delegate.getSendAsPeers(), new SenderSelectPopup.OnSelectCallback() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda95
                @Override // org.telegram.ui.Components.SenderSelectPopup.OnSelectCallback
                public final void onPeerSelected(RecyclerView recyclerView, SenderSelectPopup.SenderView senderView, TLRPC.Peer peer2) {
                    this.f$0.lambda$createSenderSelectView$31(chatFull, messagesController, recyclerView, senderView, peer2);
                }
            }, this.resourcesProvider);
            this.senderSelectPopupWindow = anonymousClass38;
            anonymousClass38.setPauseNotifications(true);
            this.senderSelectPopupWindow.setDismissAnimationDuration(Opcodes.REM_INT_LIT8);
            this.senderSelectPopupWindow.setOutsideTouchable(true);
            this.senderSelectPopupWindow.setClippingEnabled(true);
            this.senderSelectPopupWindow.setFocusable(true);
            this.senderSelectPopupWindow.getContentView().measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
            this.senderSelectPopupWindow.setInputMethodMode(2);
            this.senderSelectPopupWindow.setSoftInputMode(0);
            this.senderSelectPopupWindow.getContentView().setFocusableInTouchMode(true);
            this.senderSelectPopupWindow.setAnimationEnabled(false);
            int i2 = -AndroidUtilities.dp(4.0f);
            int[] iArr = new int[2];
            if (!AndroidUtilities.isTablet() || (chatActivity = this.parentFragment) == null) {
                i = i2;
            } else {
                chatActivity.getFragmentView().getLocationInWindow(iArr);
                i = iArr[0] + i2;
            }
            int contentViewHeight2 = this.delegate.getContentViewHeight();
            int measuredHeight = this.senderSelectPopupWindow.getContentView().getMeasuredHeight();
            int iMeasureKeyboardHeight2 = this.delegate.measureKeyboardHeight();
            if (iMeasureKeyboardHeight2 <= AndroidUtilities.dp(20.0f)) {
                contentViewHeight2 += iMeasureKeyboardHeight2;
            }
            if (this.emojiViewVisible) {
                contentViewHeight2 -= getEmojiPadding();
            }
            AndroidUtilities.dp(1.0f);
            int i3 = (i2 * 2) + contentViewHeight2;
            ChatActivity chatActivity2 = this.parentFragment;
            if (measuredHeight < (i3 - ((chatActivity2 == null || !chatActivity2.isInBubbleMode()) ? AndroidUtilities.statusBarHeight : 0)) - this.senderSelectPopupWindow.headerText.getMeasuredHeight()) {
                getLocationInWindow(iArr);
                iDp = ((iArr[1] - measuredHeight) - i2) - AndroidUtilities.dp(2.0f);
            } else {
                ChatActivity chatActivity3 = this.parentFragment;
                int i4 = (chatActivity3 == null || !chatActivity3.isInBubbleMode()) ? AndroidUtilities.statusBarHeight : 0;
                this.senderSelectPopupWindow.recyclerContainer.getLayoutParams().height = ((contentViewHeight2 - i4) - AndroidUtilities.dp(14.0f)) - getHeightWithTopView();
                iDp = i4;
            }
            this.senderSelectPopupWindow.startShowAnimation();
            SenderSelectPopup senderSelectPopup2 = this.senderSelectPopupWindow;
            this.popupX = i;
            this.popupY = iDp;
            senderSelectPopup2.showAtLocation(view, 51, i, iDp);
            this.senderSelectView.setProgress(1.0f);
        }
    }

    public void lambda$createBotCommandsMenuButton$33(View view) {
        boolean zIsOpened = this.botCommandsMenuButton.isOpened();
        this.botCommandsMenuButton.setOpened(!zIsOpened);
        try {
            performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        if (hasBotWebView()) {
            if (zIsOpened) {
                return;
            }
            if (this.emojiViewVisible || this.botKeyboardViewVisible) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda40
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.openWebViewMenu();
                    }
                }, 275L);
                hidePopup(false);
                return;
            } else {
                openWebViewMenu();
                return;
            }
        }
        if (!zIsOpened) {
            createBotCommandsMenuContainer();
            this.botCommandsMenuContainer.show();
        } else {
            BotCommandsMenuContainer botCommandsMenuContainer = this.botCommandsMenuContainer;
            if (botCommandsMenuContainer != null) {
                botCommandsMenuContainer.dismiss();
            }
        }
    }

    private void createBotWebViewButton() {
        if (this.botWebViewButton != null) {
            return;
        }
        ChatActivityBotWebViewButton chatActivityBotWebViewButton = new ChatActivityBotWebViewButton(getContext());
        this.botWebViewButton = chatActivityBotWebViewButton;
        chatActivityBotWebViewButton.setVisibility(8);
        createBotCommandsMenuButton();
        this.botWebViewButton.setBotMenuButton(this.botCommandsMenuButton);
        this.messageEditTextContainer.addView(this.botWebViewButton, LayoutHelper.createFrame(-1, -1, 80));
    }

    public void createRecordCircle() {
        createControlsView();
        if (this.recordCircle != null) {
            return;
        }
        RecordCircle recordCircle = new RecordCircle(getContext());
        this.recordCircle = recordCircle;
        recordCircle.setVisibility(8);
        this.sizeNotifierLayout.addView(this.recordCircle, LayoutHelper.createFrame(-1, -2, 80));
    }

    private void createControlsView() {
        if (this.controlsView != null) {
            return;
        }
        ControlsView controlsView = new ControlsView(getContext());
        this.controlsView = controlsView;
        controlsView.setVisibility(8);
        this.sizeNotifierLayout.addView(this.controlsView, LayoutHelper.createFrame(-1, -2, 80));
    }

    public boolean isRecordCircleOrControlsView(View view) {
        if (view != null) {
            return view == this.controlsView || view == this.recordCircle;
        }
        return false;
    }

    public void showRestrictedHint() {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if ((chatActivityEnterViewDelegate == null || !chatActivityEnterViewDelegate.checkCanRemoveRestrictionsByBoosts()) && DialogObject.isChatDialog(this.dialog_id)) {
            BulletinFactory.of(this.parentFragment).createSimpleBulletin(R.raw.passcode_lock_close, LocaleController.formatString("SendPlainTextRestrictionHint", R.string.SendPlainTextRestrictionHint, ChatObject.getAllowedSendString(this.accountInstance.getMessagesController().getChat(Long.valueOf(-this.dialog_id)))), 3).show();
        }
    }

    public void openWebViewMenu() {
        createBotWebViewMenuContainer();
        final Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda114
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openWebViewMenu$36();
            }
        };
        if (SharedPrefsHelper.isWebViewConfirmShown(this.currentAccount, this.dialog_id) || MessagesController.getInstance(this.currentAccount).whitelistedBots.contains(Long.valueOf(this.dialog_id))) {
            runnable.run();
        } else {
            AlertsCreator.createBotLaunchAlert(this.parentFragment, MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(this.dialog_id)), new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda115
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$openWebViewMenu$37(runnable);
                }
            }, new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda116
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$openWebViewMenu$38();
                }
            });
        }
    }

    public void lambda$new$39() {
        MessageSendPreview messageSendPreview = this.messageSendPreview;
        if (messageSendPreview != null) {
            messageSendPreview.dismiss(this.dismissSendPreviewSent);
            this.messageSendPreview = null;
        }
    }

    public boolean onSendOptionsTouch(View view, MotionEvent motionEvent) {
        MessageSendPreview messageSendPreview;
        if (this.sendButtonItemOptions == null || (messageSendPreview = this.messageSendPreview) == null || !messageSendPreview.isShowing()) {
            return false;
        }
        if (view.getParent() != null) {
            view.getParent().requestDisallowInterceptTouchEvent(true);
        }
        this.messageSendPreview.dispatchCapturedTouchEvent(motionEvent, this.sendButtonItemOptions);
        return false;
    }

    public void lambda$onSendLongClick$45(Client client) {
        if (this.keyboardVisible) {
            closeKeyboard();
        } else if (isPopupShowing()) {
            hidePopup(false);
        }
        AiResponseAlert.showAlert(this.parentFragment, client, getEditField().getText().toString(), AiConfig.getSaveHistory(), false, null, new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda125
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onSendLongClick$43();
            }
        }, new Utilities.Callback2() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda126
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$onSendLongClick$44((String) obj, (CharSequence) obj2);
            }
        }).setDimBehind(true);
    }

    public void lambda$onSendLongClick$47() {
        this.parentFragment.presentFragment(new AiPreferencesActivity());
        this.sendPopupLayout = null;
    }

    public void lambda$onSendLongClick$50(View view) {
        ActionBarPopupWindow actionBarPopupWindow = this.sendPopupWindow;
        if (actionBarPopupWindow != null && actionBarPopupWindow.isShowing()) {
            this.sendPopupWindow.dismiss();
        }
        AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView.45
            @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
            public void didSelectDate(boolean z, int i, int i2) {
                ChatActivityEnterView.this.sendMessageInternal(z, i, 0, 0L, true);
            }
        }, this.resourcesProvider);
    }

    public void lambda$onSendLongClick$54(Canvas canvas) {
        drawBackground(canvas, false);
    }

    public void lambda$onSendLongClick$56(View view) {
        MessageSendPreview messageSendPreview = this.messageSendPreview;
        if (messageSendPreview != null) {
            messageSendPreview.dismiss(false);
        }
        this.parentFragment.presentFragment(new EditServiceActivity());
    }

    public void lambda$onSendLongClick$61(final Client client, View view) {
        MessageSendPreview messageSendPreview = this.messageSendPreview;
        if (messageSendPreview != null) {
            messageSendPreview.dismiss(false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda117
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onSendLongClick$60(client);
            }
        }, 150L);
    }

    public void lambda$onSendLongClick$58() {
        this.parentFragment.dimBehindView(false);
    }

    public void lambda$onSendLongClick$62() {
        this.parentFragment.presentFragment(new AiPreferencesActivity());
        this.sendPopupLayout = null;
    }

    public void lambda$onSendLongClick$65() {
        AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView.49
            @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
            public void didSelectDate(boolean z, int i, int i2) {
                ChatActivityEnterView.this.sendMessageInternal(z, i, i2, 0L, true);
                MessageSendPreview messageSendPreview = ChatActivityEnterView.this.messageSendPreview;
                if (messageSendPreview != null) {
                    messageSendPreview.dismissInstant();
                    ChatActivityEnterView.this.messageSendPreview = null;
                }
            }
        }, this.resourcesProvider);
    }

    public void lambda$onSendLongClick$68() {
        MessageSendPreview messageSendPreview = this.messageSendPreview;
        if (messageSendPreview != null) {
            messageSendPreview.dismiss(false);
            this.messageSendPreview = null;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda122
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onSendLongClick$67();
            }
        }, 600L);
    }

    public void lambda$onItemClick$0(String str, boolean z, int i, int i2) {
            SendMessagesHelper.SendMessageParams sendMessageParamsOf = SendMessagesHelper.SendMessageParams.of(str, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), null, false, null, null, null, z, i, i2, null, false);
            sendMessageParamsOf.quick_reply_shortcut = ChatActivityEnterView.this.parentFragment != null ? ChatActivityEnterView.this.parentFragment.quickReplyShortcut : null;
            sendMessageParamsOf.quick_reply_shortcut_id = ChatActivityEnterView.this.parentFragment != null ? ChatActivityEnterView.this.parentFragment.getQuickReplyId() : 0;
            sendMessageParamsOf.effect_id = ChatActivityEnterView.this.effectId;
            SendMessagesHelper.getInstance(ChatActivityEnterView.this.currentAccount).sendMessage(sendMessageParamsOf);
            ChatActivityEnterView.this.setFieldText(_UrlKt.FRAGMENT_ENCODE_SET);
            ChatActivityEnterView.this.botCommandsMenuContainer.dismiss();
            SendButton sendButton = ChatActivityEnterView.this.sendButton;
            ChatActivityEnterView.this.effectId = 0L;
            sendButton.setEffect(0L);
        }

        public void lambda$onTouchEvent$0() {
            ChatActivityEnterView.this.showRestrictedHint();
        }

        public void lambda$editPhoto$3(Uri uri, final File file, String str) {
            try {
                InputStream inputStreamOpenInputStream = getContext().getContentResolver().openInputStream(uri);
                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    try {
                        if (inputStreamOpenInputStream == null) {
                            FileLog.e("InputStream is null for URI: " + uri);
                            fileOutputStream.close();
                            if (inputStreamOpenInputStream == null) {
                                return;
                            }
                        } else {
                            byte[] bArr = new byte[1024];
                            while (true) {
                                int i = inputStreamOpenInputStream.read(bArr);
                                if (i <= 0) {
                                    break;
                                } else {
                                    fileOutputStream.write(bArr, 0, i);
                                }
                            }
                            MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, -1, 0L, file.getAbsolutePath(), 0, "video/mp4".equals(str), 0, 0, 0L);
                            final ArrayList arrayList = new ArrayList();
                            arrayList.add(photoEntry);
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$ChatActivityEditTextCaption$$ExternalSyntheticLambda3
                                @Override // java.lang.Runnable
                                public final void run() {
                                    this.f$0.lambda$editPhoto$2(arrayList, file);
                                }
                            });
                            fileOutputStream.close();
                        }
                        inputStreamOpenInputStream.close();
                    } catch (Throwable th) {
                        try {
                            fileOutputStream.close();
                            throw th;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            throw th;
                        }
                    }
                } catch (Throwable th3) {
                    if (inputStreamOpenInputStream == null) {
                        throw th3;
                    }
                    try {
                        inputStreamOpenInputStream.close();
                        throw th3;
                    } catch (Throwable th4) {
                        th3.addSuppressed(th4);
                        throw th3;
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        public void lambda$editPhoto$2(final ArrayList<Object> arrayList, final File file) {
            if (ChatActivityEnterView.this.parentFragment == null || ChatActivityEnterView.this.parentFragment.getParentActivity() == null) {
                return;
            }
            final MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) arrayList.get(0);
            if (ChatActivityEnterView.this.keyboardVisible) {
                AndroidUtilities.hideKeyboard(this);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEditTextCaption.1
                    @Override // java.lang.Runnable
                    public void run() {
                        ChatActivityEditTextCaption.this.lambda$editPhoto$2(arrayList, file);
                    }
                }, 100L);
            } else {
                PhotoViewer.getInstance().setParentActivity(ChatActivityEnterView.this.parentFragment, ChatActivityEnterView.this.resourcesProvider);
                PhotoViewer.getInstance().openPhotoForSelect(arrayList, 0, 2, false, new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEditTextCaption.2
                    boolean sending;

                    @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
                    public boolean canCaptureMorePhotos() {
                        return false;
                    }

                    @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
                    public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, int i3, boolean z2) {
                        String str;
                        if (ChatActivityEnterView.this.replyingQuote != null && ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.replyingQuote.outdated) {
                            ChatActivityEnterView.this.parentFragment.showQuoteMessageUpdate();
                            return;
                        }
                        ArrayList arrayList2 = new ArrayList();
                        SendMessagesHelper.SendingMediaInfo sendingMediaInfo = new SendMessagesHelper.SendingMediaInfo();
                        MediaController.PhotoEntry photoEntry2 = photoEntry;
                        if (!photoEntry2.isVideo && (str = photoEntry2.imagePath) != null) {
                            sendingMediaInfo.path = str;
                            if (photoEntry2.isHighQuality()) {
                                sendingMediaInfo.originalPhotoEntry = photoEntry.clone();
                            }
                        } else {
                            String str2 = photoEntry2.path;
                            if (str2 != null) {
                                sendingMediaInfo.path = str2;
                            }
                        }
                        MediaController.PhotoEntry photoEntry3 = photoEntry;
                        sendingMediaInfo.thumbPath = photoEntry3.thumbPath;
                        sendingMediaInfo.isLivePhoto = photoEntry3.isLivePhoto();
                        MediaController.PhotoEntry photoEntry4 = photoEntry;
                        sendingMediaInfo.isVideo = photoEntry4.isVideo;
                        sendingMediaInfo.discardLivePhoto = photoEntry4.isUnalivePhoto();
                        MediaController.PhotoEntry photoEntry5 = photoEntry;
                        sendingMediaInfo.livePhotoVideoOffset = photoEntry5.livePhotoVideoOffset;
                        sendingMediaInfo.livePhotoTimestampUs = photoEntry5.livePhotoTimestampUs;
                        CharSequence charSequence = photoEntry5.caption;
                        sendingMediaInfo.caption = charSequence != null ? charSequence.toString() : null;
                        MediaController.PhotoEntry photoEntry6 = photoEntry;
                        sendingMediaInfo.entities = photoEntry6.entities;
                        sendingMediaInfo.masks = photoEntry6.stickers;
                        sendingMediaInfo.ttl = photoEntry6.ttl;
                        sendingMediaInfo.videoEditedInfo = videoEditedInfo;
                        sendingMediaInfo.canDeleteAfter = true;
                        sendingMediaInfo.highQuality = photoEntry6.isHighQuality();
                        arrayList2.add(sendingMediaInfo);
                        photoEntry.reset();
                        this.sending = true;
                        SendMessagesHelper.prepareSendingMedia(ChatActivityEnterView.this.accountInstance, arrayList2, ChatActivityEnterView.this.dialog_id, ChatActivityEnterView.this.replyingMessageObject, ChatActivityEnterView.this.getThreadMessage(), null, ChatActivityEnterView.this.replyingQuote, false, false, ChatActivityEnterView.this.editingMessageObject, z, i2, i3, ChatActivityEnterView.this.parentFragment == null ? 0 : ChatActivityEnterView.this.parentFragment.getChatMode(), SendMessagesHelper.checkUpdateStickersOrder(sendingMediaInfo.caption), null, ChatActivityEnterView.this.parentFragment != null ? ChatActivityEnterView.this.parentFragment.quickReplyShortcut : null, ChatActivityEnterView.this.parentFragment != null ? ChatActivityEnterView.this.parentFragment.getQuickReplyId() : 0, 0L, false, 0L, ChatActivityEnterView.this.getSendMonoForumPeerId(), ChatActivityEnterView.this.parentFragment != null ? ChatActivityEnterView.this.parentFragment.messageSuggestionParams : null);
                        if (ChatActivityEnterView.this.delegate != null) {
                            ChatActivityEnterView.this.delegate.onMessageSend(null, true, i2, i3, 0L);
                        }
                    }

                    @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
                    public void willHidePhotoViewer() {
                        if (this.sending) {
                            return;
                        }
                        try {
                            file.delete();
                        } catch (Throwable unused) {
                        }
                    }
                }, ChatActivityEnterView.this.parentFragment);
            }
        }

        @Override // org.telegram.ui.Components.EditTextBoldCursor
        public Theme.ResourcesProvider getResourcesProvider() {
            return ChatActivityEnterView.this.resourcesProvider;
        }

        @Override // android.view.View
        public boolean requestFocus(int i, Rect rect) {
            ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
            if (!chatActivityEnterView.sendPlainEnabled && !chatActivityEnterView.isEditingMessage()) {
                return false;
            }
            ChatActivityEnterView.this.onKeyboardShown();
            return super.requestFocus(i, rect);
        }

        @Override // org.telegram.ui.Components.EditTextEffects
        public void setOffsetY(float f) {
            super.setOffsetY(f);
            if (ChatActivityEnterView.this.sizeNotifierLayout.getForeground() != null) {
                ChatActivityEnterView.this.sizeNotifierLayout.invalidateDrawable(ChatActivityEnterView.this.sizeNotifierLayout.getForeground());
            }
        }
    }

    private boolean isKeyboardSupportIncognitoMode() {
        String string = Settings.Secure.getString(getContext().getContentResolver(), "default_input_method");
        return string == null || !string.startsWith("com.samsung");
    }

    private void createMessageEditText() {
        if (this.messageEditText != null) {
            return;
        }
        this.messageEditText = new ChatActivityEditTextCaption(getContext(), this.resourcesProvider) { // from class: org.telegram.ui.Components.ChatActivityEnterView.53
            boolean clickMaybe;
            private boolean firstDraw = true;
            float touchX;
            float touchY;

            @Override // org.telegram.ui.Components.EditTextCaption, org.telegram.ui.Components.EditTextBoldCursor, org.telegram.ui.Components.EditTextEffects, android.widget.TextView, android.view.View
            public void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (getLayout() == null || !this.firstDraw) {
                    return;
                }
                this.firstDraw = false;
                ChatActivityEnterView.this.checkSendButton(true);
            }

            @Override 
            public boolean onTextContextMenuItem(int i) {
                if (i == 16908322 && ChatActivityEnterView.this.handleRichHtmlPaste()) {
                    return true;
                }
                return super.onTextContextMenuItem(i);
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEditTextCaption, org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (ChatActivityEnterView.this.botCommandsMenuIsShowing()) {
                    if (motionEvent.getAction() == 0) {
                        this.touchX = motionEvent.getX();
                        this.touchY = motionEvent.getY();
                        this.clickMaybe = true;
                    } else if (this.clickMaybe && motionEvent.getAction() == 2) {
                        if (Math.abs(motionEvent.getX() - this.touchX) > AndroidUtilities.touchSlop || Math.abs(motionEvent.getY() - this.touchY) > AndroidUtilities.touchSlop) {
                            this.clickMaybe = false;
                        }
                    } else if (this.clickMaybe) {
                        if (ChatActivityEnterView.this.delegate != null) {
                            fixHandlesColor();
                            ChatActivityEnterView.this.delegate.onKeyboardRequested();
                        }
                        EditTextCaption editTextCaption = ChatActivityEnterView.this.messageEditText;
                        if (editTextCaption != null && !AndroidUtilities.showKeyboard(editTextCaption)) {
                            ChatActivityEnterView.this.messageEditText.clearFocus();
                            ChatActivityEnterView.this.messageEditText.requestFocus();
                        }
                    }
                    return this.clickMaybe;
                }
                if (motionEvent.getAction() == 0 && ChatActivityEnterView.this.delegate != null) {
                    fixHandlesColor();
                    ChatActivityEnterView.this.delegate.onKeyboardRequested();
                }
                return super.onTouchEvent(motionEvent);
            }

            private void fixHandlesColor() {
                setHandlesColor(ChatActivityEnterView.this.getThemedColor(Theme.key_chat_TextSelectionCursor));
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEditTextCaption, org.telegram.ui.Components.EditTextEffects
            public void setOffsetY(float f) {
                super.setOffsetY(f);
                ChatActivityEnterView.this.messageEditTextContainer.invalidate();
            }

            @Override // org.telegram.ui.Components.EditTextBoldCursor, org.telegram.ui.Components.EditTextEffects, android.widget.TextView, android.view.View
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                if (ChatActivityEnterView.this.parentFragment != null && ChatActivityEnterView.this.parentFragment.getParentLayout() != null && ChatActivityEnterView.this.parentFragment.getParentLayout().isSheet()) {
                    setWindowView(ChatActivityEnterView.this.parentFragment.getParentLayout().getWindow().getDecorView());
                } else {
                    setWindowView(ChatActivityEnterView.this.parentActivity.getWindow().getDecorView());
                }
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.ChatActivityEditTextCaption, org.telegram.ui.Components.EditTextCaption, org.telegram.ui.Components.EditTextBoldCursor, android.widget.TextView, android.view.View
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
                if (ChatActivityEnterView.this.lineCount != ChatActivityEnterView.this.messageEditText.getLineCount()) {
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    boolean z = false;
                    chatActivityEnterView.showAiButton((chatActivityEnterView.messageEditText.getLineCount() <= 2 || ChatActivityEnterView.this.messageEditText.getText() == null || TextUtils.isEmpty(ChatActivityEnterView.this.messageEditText.getText().toString().trim())) ? false : true);
                    ChatActivityEnterView chatActivityEnterView2 = ChatActivityEnterView.this;
                    if (chatActivityEnterView2.messageEditText.getLineCount() > 2 && ChatActivityEnterView.this.messageEditText.getText() != null && !TextUtils.isEmpty(ChatActivityEnterView.this.messageEditText.getText().toString().trim())) {
                        z = true;
                    }
                    chatActivityEnterView2.showRichButton(z);
                }
            }
        };
        if (this.parentFragment != null && !isEditingBusinessLink() && !this.isLiveComment) {
            ViewCompat.setOnReceiveContentListener(this.messageEditText, new String[]{"image/gif", "imageboolean m9275$r8$lambda$CT634F_Uonc5BOyXwzUf2wEUS8(ClipData.Item item) {
        return item.getUri() != null;
    }

    public void lambda$createMessageEditText$74(View view) {
        openRichEditor();
    }

    public class AnonymousClass56 implements TextWatcher {
        boolean heightShouldBeChanged;
        private boolean ignorePrevTextChange;
        private boolean nextChangeIsSend;
        private CharSequence prevText;
        private boolean processChange;

        public AnonymousClass56() {
        }

        @Override // android.text.TextWatcher
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (!this.ignorePrevTextChange && ChatActivityEnterView.this.recordingAudioVideo) {
                this.prevText = charSequence.toString();
            }
        }

        @Override // android.text.TextWatcher
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            int currentPage;
            if (this.ignorePrevTextChange) {
                return;
            }
            if (ChatActivityEnterView.this.emojiView == null) {
                currentPage = MessagesController.getGlobalEmojiSettings().getInt("selected_page", 0);
            } else {
                currentPage = ChatActivityEnterView.this.emojiView.getCurrentPage();
            }
            boolean z = currentPage != 0 && (ChatActivityEnterView.this.allowStickers || ChatActivityEnterView.this.allowGifs);
            if (((i2 == 0 && !TextUtils.isEmpty(charSequence)) || (i2 != 0 && TextUtils.isEmpty(charSequence))) && z) {
                ChatActivityEnterView.this.setEmojiButtonImage(false, true);
            }
            if (ChatActivityEnterView.this.lineCount != ChatActivityEnterView.this.messageEditText.getLineCount()) {
                this.heightShouldBeChanged = (ChatActivityEnterView.this.messageEditText.getLineCount() >= 4) != (ChatActivityEnterView.this.lineCount >= 4);
                if (!ChatActivityEnterView.this.isInitLineCount && ChatActivityEnterView.this.messageEditText.getMeasuredWidth() > 0) {
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    chatActivityEnterView.onLineCountChanged(chatActivityEnterView.lineCount, ChatActivityEnterView.this.messageEditText.getLineCount());
                }
                ChatActivityEnterView chatActivityEnterView2 = ChatActivityEnterView.this;
                chatActivityEnterView2.lineCount = chatActivityEnterView2.messageEditText.getLineCount();
                ChatActivityEnterView chatActivityEnterView3 = ChatActivityEnterView.this;
                chatActivityEnterView3.showAiButton((chatActivityEnterView3.lineCount <= 2 || charSequence == null || TextUtils.isEmpty(charSequence.toString().trim())) ? false : true);
                ChatActivityEnterView chatActivityEnterView4 = ChatActivityEnterView.this;
                chatActivityEnterView4.showRichButton((chatActivityEnterView4.lineCount <= 2 || charSequence == null || TextUtils.isEmpty(charSequence.toString().trim())) ? false : true);
            } else {
                this.heightShouldBeChanged = false;
            }
            if (ChatActivityEnterView.this.innerTextChange == 1) {
                return;
            }
            if (ChatActivityEnterView.this.sendByEnter) {
                ChatActivityEnterView chatActivityEnterView5 = ChatActivityEnterView.this;
                if (!chatActivityEnterView5.ctrlPressed && !chatActivityEnterView5.shiftPressed && !chatActivityEnterView5.ignoreTextChange && !ChatActivityEnterView.this.isPaste && ChatActivityEnterView.this.editingMessageObject == null && i3 > i2 && charSequence.length() > 0 && charSequence.length() == i + i3 && charSequence.charAt(charSequence.length() - 1) == '\n') {
                    this.nextChangeIsSend = true;
                }
            }
            ChatActivityEnterView.this.isPaste = false;
            ChatActivityEnterView.this.checkSendButton(true);
            CharSequence trimmedString = AndroidUtilities.getTrimmedString(charSequence.toString());
            if (ChatActivityEnterView.this.delegate != null && !ChatActivityEnterView.this.ignoreTextChange) {
                boolean z2 = i2 > i3 + 1 || i3 - i2 > 2;
                if (TextUtils.isEmpty(charSequence) || (z2 && (ChatActivityEnterView.this.messageWebPage != null || ChatActivityEnterView.this.messageWebPageSearch))) {
                    ChatActivityEnterView.this.messageWebPageSearch = true;
                }
                ChatActivityEnterView.this.delegate.onTextChanged(charSequence, z2, false);
            }
            if (ChatActivityEnterView.this.innerTextChange != 2 && i3 - i2 > 1) {
                this.processChange = true;
            }
            if (ChatActivityEnterView.this.editingMessageObject == null && !ChatActivityEnterView.this.canWriteToChannel && trimmedString.length() != 0 && ChatActivityEnterView.this.lastTypingTimeSend < System.currentTimeMillis() - 5000 && !ChatActivityEnterView.this.ignoreTextChange) {
                ChatActivityEnterView.this.lastTypingTimeSend = System.currentTimeMillis();
                if (ChatActivityEnterView.this.delegate != null) {
                    ChatActivityEnterView.this.delegate.needSendTyping();
                }
            }
            ChatActivityEnterView.this.updateSendButtonPaid();
        }

        void lambda$afterTextChanged$0() {
            ChatActivityEnterView.this.showCaptionLimitBulletin();
        }
    }

    public void showAiButton(boolean z) {
        ChatActivity chatActivity;
        final boolean z2 = ExteraConfig.getTelegramAiEditor() && !((!z && !this.richDraftActive) || (chatActivity = this.parentFragment) == null || chatActivity.isSecretChat());
        if (this.shownAiButton == z2) {
            return;
        }
        if (z2) {
            MessagesController.getInstance(this.currentAccount).getTonesController().load();
        }
        this.shownAiButton = z2;
        this.aiButton.setVisibility(0);
        this.aiButton.animate().alpha(z2 ? 1.0f : 0.0f).scaleX(z2 ? 1.0f : 0.6f).scaleY(z2 ? 1.0f : 0.6f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(420L).withEndAction(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showAiButton$75(z2);
            }
        }).start();
        if (z2) {
            ImageView imageView = this.aiButton;
            AiButtonDrawable aiButtonDrawable = this.aiButtonIcon;
            Objects.requireNonNull(aiButtonDrawable);
            imageView.postDelayed(new CaptionPhotoViewer$$ExternalSyntheticLambda9(aiButtonDrawable), 220L);
            HintView2 hintView2 = this.aiHint;
            if (hintView2 != null) {
                hintView2.hide();
                this.aiHint = null;
            }
            if (MessagesController.getGlobalMainSettings().getInt("aihintshown", 0) < 3) {
                final HintView2 hintView3 = new HintView2(getContext(), 3);
                this.aiHint = hintView3;
                hintView3.setMultilineText(true);
                this.aiHint.setText(LocaleController.getString(R.string.AIEditorHint));
                this.aiHint.setJointPx(0.0f, (this.aiButton.getWidth() / 2.0f) + AndroidUtilities.dp(4.0f));
                addView(this.aiHint, LayoutHelper.createFrame(-1, 200.0f, 48, 0.0f, -196.0f, 0.0f, 0.0f));
                this.aiHint.setOnHiddenListener(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda19
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$showAiButton$76(hintView3);
                    }
                });
                this.aiHint.setDuration(4000L);
                this.aiHint.show();
                MessagesController.getGlobalMainSettings().edit().putInt("aihintshown", MessagesController.getGlobalMainSettings().getInt("aihintshown", 0) + 1).apply();
                return;
            }
            return;
        }
        HintView2 hintView4 = this.aiHint;
        if (hintView4 != null) {
            hintView4.hide();
            this.aiHint = null;
        }
    }

    public void lambda$showTopView$78() {
        showTopView(true, false, true);
        this.showTopViewRunnable = null;
    }

    public void onEditTimeExpired() {
        SendButton sendButton = this.doneButton;
        if (sendButton != null) {
            sendButton.setVisibility(8);
        }
    }

    public void showEditDoneProgress(boolean z, boolean z2) {
        if (this.doneButton == null) {
            return;
        }
        AnimatorSet animatorSet = this.doneButtonAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        SendButton sendButton = this.doneButton;
        if (z) {
            sendButton.setEnabled(false);
            this.doneButton.setLoading(true, -3.0f);
        } else {
            sendButton.setEnabled(true);
            this.doneButton.setLoading(false, -3.0f);
        }
    }

    public void hideTopView(boolean z) {
        if (this.topView == null || !this.topViewShowed) {
            return;
        }
        Runnable runnable = this.showTopViewRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        this.topViewShowed = false;
        this.needShowTopView = false;
        if (this.allowShowTopView) {
            this.animatorTopViewVisibility.setValue(false, z);
        }
    }

    public boolean isTopViewVisible() {
        View view = this.topView;
        return view != null && view.getVisibility() == 0;
    }

    public float topViewVisible() {
        return getTopViewEnterProgress();
    }

    public void onAdjustPanTransitionEnd() {
        Runnable runnable = this.onKeyboardClosed;
        if (runnable != null) {
            runnable.run();
            this.onKeyboardClosed = null;
        }
    }

    public void onAdjustPanTransitionStart(boolean z, int i) {
        Runnable runnable;
        if (z && (runnable = this.showTopViewRunnable) != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.showTopViewRunnable.run();
        }
        Runnable runnable2 = this.setTextFieldRunnable;
        if (runnable2 != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable2);
            this.setTextFieldRunnable.run();
        }
    }

    private void onWindowSizeChanged() {
        int height = this.sizeNotifierLayout.getHeight();
        if (!this.keyboardVisible) {
            height -= this.emojiPadding;
        }
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onWindowSizeChanged(height);
        }
        if (this.topView != null) {
            int iDp = AndroidUtilities.dp(72.0f) + ActionBar.getCurrentActionBarHeight();
            boolean z = this.allowShowTopView;
            if (height < iDp) {
                if (z) {
                    this.allowShowTopView = false;
                    if (this.needShowTopView) {
                        this.animatorTopViewVisibility.setValue(false, false);
                        return;
                    }
                    return;
                }
                return;
            }
            if (z) {
                return;
            }
            this.allowShowTopView = true;
            if (this.needShowTopView) {
                this.animatorTopViewVisibility.setValue(true, false);
            }
        }
    }

    private void resizeForTopView(float f) {
        if (this.topView == null) {
            return;
        }
        float fClamp = MathUtils.clamp(f, 0.0f, 1.0f);
        float topViewHeightForAnimation = getTopViewHeightForAnimation() * fClamp;
        int iRound = Math.round(topViewHeightForAnimation) + AndroidUtilities.dp(9.0f);
        if (this.resizeForTopViewLastTopMargin != iRound) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.textFieldContainer.getLayoutParams();
            layoutParams.topMargin = iRound;
            this.textFieldContainer.setLayoutParams(layoutParams);
            this.resizeForTopViewLastTopMargin = iRound;
        }
        int iDp = AndroidUtilities.dp(48.0f) + Math.round(topViewHeightForAnimation);
        if (this.resizeForTopViewLastMinHeight != iDp) {
            setMinimumHeight(iDp);
            this.resizeForTopViewLastMinHeight = iDp;
        }
        boolean z = fClamp > 0.0f;
        if (this.resizeForTopViewLastShow != z) {
            this.resizeForTopViewLastShow = z;
            if (this.stickersExpanded) {
                if (this.searchingType == 0) {
                    setStickersExpanded(false, true, false);
                } else {
                    checkStickresExpandHeight();
                }
            }
        }
    }

    public void onDestroy() {
        RecordedAudioPlayerView recordedAudioPlayerView = this.audioTimelineView;
        if (recordedAudioPlayerView != null) {
            recordedAudioPlayerView.destroy();
        }
        if (this.audioTimelineView != null && this.audioToSend != null) {
            MediaDataController mediaDataController = MediaDataController.getInstance(this.currentAccount);
            long j = this.dialog_id;
            ChatActivity chatActivity = this.parentFragment;
            long topicId = (chatActivity == null || !chatActivity.isTopic) ? 0L : chatActivity.getTopicId();
            RecordedAudioPlayerView recordedAudioPlayerView2 = this.audioTimelineView;
            float audioLeft = recordedAudioPlayerView2 == null ? 0.0f : recordedAudioPlayerView2.getAudioLeft();
            RecordedAudioPlayerView recordedAudioPlayerView3 = this.audioTimelineView;
            mediaDataController.setDraftVoiceRegion(j, topicId, audioLeft, recordedAudioPlayerView3 == null ? 1.0f : recordedAudioPlayerView3.getAudioRight());
        }
        this.destroyed = true;
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStarted);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordPaused);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordResumed);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStartError);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordStopped);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.recordProgressChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.closeChats);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioDidSent);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioRouteChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.featuredStickersDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messageReceivedByServer2);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.sendingMessagesChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.audioRecordTooShort);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.updateBotMenuButton);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.didUpdatePremiumGiftFieldIcon);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.onDestroy();
        }
        Runnable runnable = this.updateSlowModeRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.updateSlowModeRunnable = null;
        }
        PowerManager.WakeLock wakeLock = this.wakeLock;
        if (wakeLock != null) {
            try {
                wakeLock.release();
                this.wakeLock = null;
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.setDelegate(null);
        }
        SenderSelectPopup senderSelectPopup = this.senderSelectPopupWindow;
        if (senderSelectPopup != null) {
            senderSelectPopup.setPauseNotifications(false);
            this.senderSelectPopupWindow.dismiss();
        }
    }

    public void checkChannelRights() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity == null) {
            return;
        }
        updateRecordButton(chatActivity.getCurrentChat(), this.parentFragment.getCurrentUserInfo());
    }

    void lambda$offerRichDraftConversion$80() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            chatActivity.showDialog(new PremiumFeatureBottomSheet(this.parentFragment, 43, true));
        }
    }

    public boolean sendMessage() {
        if (offerRichDraftConversion()) {
            return true;
        }
        if (isInScheduleMode()) {
            AlertsCreator.createScheduleDatePickerDialog(this.parentActivity, this.parentFragment.getDialogId(), new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatActivityEnterView.59
                @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
                public void didSelectDate(boolean z, int i, int i2) {
                    boolean zSendMessageInternal = ChatActivityEnterView.this.sendMessageInternal(z, i, i2, 0L, true);
                    MessageSendPreview messageSendPreview = ChatActivityEnterView.this.messageSendPreview;
                    if (messageSendPreview != null) {
                        messageSendPreview.dismiss(!zSendMessageInternal);
                        ChatActivityEnterView.this.messageSendPreview = null;
                    }
                }
            }, this.resourcesProvider);
            return true;
        }
        return sendMessageInternal(true, 0, 0, 0L, true);
    }

    public boolean sendMessageInternal(final boolean z, final int i, final int i2, final long j, boolean z2) {
        if (offerRichDraftConversion()) {
            return true;
        }
        final boolean z3 = z2 && !this.animatorEphemeralMessageVisibility.getValue();
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda43
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$sendMessageInternal$85(z, z3, i, i2, j);
            }
        };
        if (z3) {
            boolean zEnsurePaidMessageConfirmation = AlertsCreator.ensurePaidMessageConfirmation(this.currentAccount, this.dialog_id, getMessagesCount(), new Utilities.Callback() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda44
                @Override 
                public final void run(Object obj) {
                    this.f$0.lambda$sendMessageInternal$86(z, i, i2, (Long) obj);
                }
            }, j);
            if (zEnsurePaidMessageConfirmation && this.sendButtonVisible) {
                if (isInVideoMode()) {
                    if (!this.delegate.isVideoRecordingPaused()) {
                        SlideTextView slideTextView = this.slideText;
                        if (slideTextView != null) {
                            slideTextView.setEnabled(false);
                        }
                        this.delegate.toggleVideoRecordingPause();
                        return zEnsurePaidMessageConfirmation;
                    }
                } else if (!MediaController.getInstance().isRecordingPaused()) {
                    if (this.sendButtonVisible) {
                        this.calledRecordRunnable = true;
                    }
                    MediaController.getInstance().toggleRecordingPause(this.voiceOnce);
                    this.delegate.needStartRecordAudio(0);
                    SlideTextView slideTextView2 = this.slideText;
                    if (slideTextView2 != null) {
                        slideTextView2.setEnabled(false);
                    }
                }
            }
            return zEnsurePaidMessageConfirmation;
        }
        runnable.run();
        return false;
    }

    public void lambda$sendMessageInternal$81(boolean z, int i, int i2, long j) {
        sendMessageInternal(z, i, i2, j, false);
    }

    public void lambda$sendMessageInternal$86(boolean z, int i, int i2, Long l) {
        sendMessageInternal(z, i, i2, l.longValue(), false);
    }

    void lambda$saveBusinessLink$89() {
        BulletinFactory.of(this.parentFragment).createSuccessBulletin(LocaleController.getString(R.string.BusinessLinkSaved)).show();
    }

    public void doneEditingMessage() {
        MessagePreviewParams messagePreviewParams;
        MessageSuggestionParams messageSuggestionParamsOf;
        int i;
        MessageSuggestionParams messageSuggestionParamsOf2;
        MessageObject messageObject = this.editingMessageObject;
        if (messageObject == null) {
            return;
        }
        if (messageObject.needResendWhenEdit() && !ChatObject.canManageMonoForum(this.currentAccount, this.editingMessageObject.getDialogId())) {
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity == null || (messageSuggestionParamsOf2 = chatActivity.messageSuggestionParams) == null) {
                messageSuggestionParamsOf2 = MessageSuggestionParams.of(this.editingMessageObject.messageOwner.suggested_post);
            }
            if (!StarsController.isEnoughAmount(this.currentAccount, messageSuggestionParamsOf2.amount)) {
                ChatActivity chatActivity2 = this.parentFragment;
                if (chatActivity2 != null) {
                    chatActivity2.showSuggestionOfferForEditMessage(messageSuggestionParamsOf2);
                    return;
                }
                return;
            }
        }
        if (this.currentLimit - this.codePointCount < 0) {
            NumberTextView numberTextView = this.captionLimitView;
            if (numberTextView != null) {
                AndroidUtilities.shakeViewSpring(numberTextView, 3.5f);
                try {
                    this.captionLimitView.performHapticFeedback(3, 2);
                } catch (Exception unused) {
                }
            }
            if (MessagesController.getInstance(this.currentAccount).premiumFeaturesBlocked() || MessagesController.getInstance(this.currentAccount).captionLengthLimitPremium <= this.codePointCount) {
                return;
            }
            showCaptionLimitBulletin();
            return;
        }
        if (this.searchingType != 0) {
            setSearchingTypeInternal(0, true);
            this.emojiView.closeSearch(false);
            if (this.stickersExpanded) {
                setStickersExpanded(false, true, false);
                this.waitingForKeyboardOpenAfterAnimation = true;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda105
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$doneEditingMessage$90();
                    }
                }, 200L);
            }
        }
        EditTextCaption editTextCaption = this.messageEditText;
        CharSequence textToUse = editTextCaption == null ? _UrlKt.FRAGMENT_ENCODE_SET : editTextCaption.getTextToUse();
        MessageObject messageObject2 = this.editingMessageObject;
        if (messageObject2 == null || messageObject2.type != 19) {
            textToUse = AndroidUtilities.getTrimmedString(textToUse);
        }
        CharSequence[] charSequenceArr = {textToUse};
        if (TextUtils.isEmpty(charSequenceArr[0])) {
            TLRPC.MessageMedia messageMedia = this.editingMessageObject.messageOwner.media;
            if ((messageMedia instanceof TLRPC.TL_messageMediaWebPage) || (messageMedia instanceof TLRPC.TL_messageMediaEmpty) || messageMedia == null) {
                AndroidUtilities.shakeViewSpring(this.messageEditText, -3.0f);
                BotWebViewVibrationEffect.APP_ERROR.vibrate();
                return;
            }
        }
        ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, supportsSendingNewEntities());
        if (!TextUtils.equals(charSequenceArr[0], this.editingMessageObject.messageText) || ((entities != null && !entities.isEmpty()) || !this.editingMessageObject.messageOwner.entities.isEmpty() || (this.editingMessageObject.messageOwner.media instanceof TLRPC.TL_messageMediaWebPage))) {
            MessageObject messageObject3 = this.editingMessageObject;
            messageObject3.editingMessage = charSequenceArr[0];
            messageObject3.editingMessageEntities = entities;
            messageObject3.editingMessageSearchWebPage = this.messageWebPageSearch;
            ChatActivity chatActivity3 = this.parentFragment;
            if (chatActivity3 != null && chatActivity3.getCurrentChat() != null && (((i = this.editingMessageObject.type) == 0 || i == 19) && !ChatObject.canSendEmbed(this.parentFragment.getCurrentChat()))) {
                MessageObject messageObject4 = this.editingMessageObject;
                messageObject4.editingMessageSearchWebPage = false;
                TLRPC.Message message = messageObject4.messageOwner;
                message.flags &= -513;
                message.media = null;
            } else {
                ChatActivity chatActivity4 = this.parentFragment;
                if (chatActivity4 != null && (messagePreviewParams = chatActivity4.messagePreviewParams) != null) {
                    if (chatActivity4.foundWebPage instanceof TLRPC.TL_webPagePending) {
                        MessageObject messageObject5 = this.editingMessageObject;
                        messageObject5.editingMessageSearchWebPage = false;
                        int i2 = messageObject5.type;
                        if (i2 == 0 || i2 == 19) {
                            messageObject5.messageOwner.media = new TLRPC.TL_messageMediaEmpty();
                            this.editingMessageObject.messageOwner.flags |= 512;
                        }
                    } else {
                        TLRPC.WebPage webPage = messagePreviewParams.webpage;
                        MessageObject messageObject6 = this.editingMessageObject;
                        if (webPage != null) {
                            messageObject6.editingMessageSearchWebPage = false;
                            TLRPC.Message message2 = messageObject6.messageOwner;
                            message2.flags |= 512;
                            message2.media = new TLRPC.TL_messageMediaWebPage();
                            this.editingMessageObject.messageOwner.media.webpage = this.parentFragment.messagePreviewParams.webpage;
                        } else {
                            messageObject6.editingMessageSearchWebPage = false;
                            int i3 = messageObject6.type;
                            if (i3 == 0 || i3 == 19) {
                                TLRPC.Message message3 = messageObject6.messageOwner;
                                message3.flags |= 512;
                                message3.media = new TLRPC.TL_messageMediaEmpty();
                            }
                        }
                    }
                    TLRPC.Message message4 = this.editingMessageObject.messageOwner;
                    MessagePreviewParams messagePreviewParams2 = this.parentFragment.messagePreviewParams;
                    message4.invert_media = messagePreviewParams2.webpageTop;
                    if (messagePreviewParams2.hasMedia) {
                        TLRPC.MessageMedia messageMedia2 = message4.media;
                        if (messageMedia2 instanceof TLRPC.TL_messageMediaWebPage) {
                            boolean z = messagePreviewParams2.webpageSmall;
                            messageMedia2.force_small_media = z;
                            messageMedia2.force_large_media = true ^ z;
                        }
                    }
                } else {
                    MessageObject messageObject7 = this.editingMessageObject;
                    messageObject7.editingMessageSearchWebPage = false;
                    int i4 = messageObject7.type;
                    if (i4 == 0 || i4 == 19) {
                        TLRPC.Message message5 = messageObject7.messageOwner;
                        message5.flags |= 512;
                        message5.media = new TLRPC.TL_messageMediaEmpty();
                    }
                }
            }
            if (this.editingMessageObject.needResendWhenEdit()) {
                SendMessagesHelper.SendMessageParams sendMessageParamsOf = SendMessagesHelper.SendMessageParams.of(this.editingMessageObject.editingMessage.toString(), this.editingMessageObject.getDialogId());
                ChatActivity chatActivity5 = this.parentFragment;
                if (chatActivity5 == null || (messageSuggestionParamsOf = chatActivity5.messageSuggestionParams) == null) {
                    messageSuggestionParamsOf = MessageSuggestionParams.of(this.editingMessageObject.messageOwner.suggested_post);
                }
                sendMessageParamsOf.suggestionParams = messageSuggestionParamsOf;
                sendMessageParamsOf.monoForumPeer = DialogObject.getPeerDialogId(this.editingMessageObject.messageOwner.saved_peer_id);
                sendMessageParamsOf.hasMediaSpoilers = this.editingMessageObject.hasMediaSpoilers();
                MessageObject messageObject8 = this.editingMessageObject;
                sendMessageParamsOf.replyToMsg = messageObject8;
                sendMessageParamsOf.parentObject = messageObject8;
                boolean z2 = messageObject8.getDocument() instanceof TLRPC.TL_document;
                MessageObject messageObject9 = this.editingMessageObject;
                if (z2) {
                    sendMessageParamsOf.document = (TLRPC.TL_document) messageObject9.getDocument();
                    sendMessageParamsOf.caption = sendMessageParamsOf.message;
                    sendMessageParamsOf.message = null;
                } else {
                    TLRPC.MessageMedia messageMedia3 = messageObject9.messageOwner.media;
                    if (messageMedia3 != null && !(messageMedia3 instanceof TLRPC.TL_messageMediaEmpty)) {
                        TLRPC.Photo photo = messageMedia3.photo;
                        if (photo instanceof TLRPC.TL_photo) {
                            sendMessageParamsOf.photo = (TLRPC.TL_photo) photo;
                        } else {
                            sendMessageParamsOf.location = messageMedia3;
                        }
                        sendMessageParamsOf.caption = sendMessageParamsOf.message;
                        sendMessageParamsOf.message = null;
                    }
                }
                SendMessagesHelper.getInstance(this.currentAccount).sendMessage(sendMessageParamsOf);
            } else {
                SendMessagesHelper sendMessagesHelper = SendMessagesHelper.getInstance(this.currentAccount);
                MessageObject messageObject10 = this.editingMessageObject;
                sendMessagesHelper.editMessage(messageObject10, null, null, null, null, null, null, false, messageObject10.hasMediaSpoilers(), null);
            }
        }
        setEditingMessageObject(null, null, false);
    }

    public void lambda$updateRecordInterface$91(ValueAnimator valueAnimator) {
        this.recordCircle.setTransformToSeekbar(((Float) valueAnimator.getAnimatedValue()).floatValue());
        if (!isInVideoMode()) {
            this.audioTimelineView.setAlpha(this.recordCircle.getTransformToSeekbarProgressStep3());
            this.audioTimelineView.invalidate();
        }
        isRecordingStateChanged();
    }

    public void cancelRecordInterfaceInternal() {
        FrameLayout frameLayout = this.recordPanel;
        if (frameLayout != null) {
            frameLayout.setVisibility(8);
        }
        RecordCircle recordCircle = this.recordCircle;
        if (recordCircle != null) {
            recordCircle.setVisibility(8);
        }
        this.runningAnimationAudio = null;
        isRecordingStateChanged();
        if (this.attachLayout != null) {
            this.attachLayoutTranslationX = 0.0f;
            updateAttachLayoutParams();
        }
        SlideTextView slideTextView = this.slideText;
        if (slideTextView != null) {
            slideTextView.setCancelToProgress(0.0f);
        }
        this.delegate.onAudioVideoInterfaceUpdated();
        updateSendAsButton();
    }

    private void createRecordPanel() {
        if (this.recordPanel != null || getContext() == null) {
            return;
        }
        FrameLayout frameLayout = new FrameLayout(getContext()) { // from class: org.telegram.ui.Components.ChatActivityEnterView.74
            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return super.onTouchEvent(motionEvent);
            }
        };
        this.recordPanel = frameLayout;
        frameLayout.setClipChildren(false);
        this.recordPanel.setVisibility(8);
        this.messageEditTextContainer.addView(this.recordPanel, LayoutHelper.createFrame(-1, 48.0f));
        this.recordPanel.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda79
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return ChatActivityEnterView.$r8$lambda$geUFytAXHquo9901JcHgGzXBA04(view, motionEvent);
            }
        });
        FrameLayout frameLayout2 = this.recordPanel;
        SlideTextView slideTextView = new SlideTextView(getContext());
        this.slideText = slideTextView;
        frameLayout2.addView(slideTextView, LayoutHelper.createFrame(-1, -1.0f, 0, 45.0f, 0.0f, 0.0f, 0.0f));
        LinearLayout linearLayout = new LinearLayout(getContext());
        this.recordTimeContainer = linearLayout;
        linearLayout.setOrientation(0);
        this.recordTimeContainer.setPadding(AndroidUtilities.dp(13.0f), 0, 0, 0);
        this.recordTimeContainer.setFocusable(false);
        LinearLayout linearLayout2 = this.recordTimeContainer;
        RecordDot recordDot = new RecordDot(getContext());
        this.recordDot = recordDot;
        linearLayout2.addView(recordDot, LayoutHelper.createLinear(28, 28, 16, 0, 0, 0, 0));
        LinearLayout linearLayout3 = this.recordTimeContainer;
        TimerView timerView = new TimerView(getContext());
        this.recordTimerView = timerView;
        linearLayout3.addView(timerView, LayoutHelper.createLinear(-1, -1, 16, 6, 0, 0, 0));
        this.recordPanel.addView(this.recordTimeContainer, LayoutHelper.createFrame(-1, -1, 16));
    }

    public static void lambda$setEditingBusinessLink$93(View view) {
        saveBusinessLink();
    }

    public void setEffectId(long j) {
        this.effectId = j;
        SendButton sendButton = this.sendButton;
        if (sendButton != null) {
            sendButton.setEffect(j);
        }
    }

    public long getEffectId() {
        return this.effectId;
    }

    private MessageObject editingMessageObjectPreview(MessageObject messageObject, boolean z) {
        MessageObject messageObject2 = new MessageObject(messageObject.currentAccount, messageObject.messageOwner, true, true) { // from class: org.telegram.ui.Components.ChatActivityEnterView.75
            @Override 
            public boolean isOutOwner() {
                return true;
            }

            @Override 
            public boolean needDrawShareButton() {
                return false;
            }
        };
        if (z) {
            EditTextCaption editTextCaption = this.messageEditText;
            CharSequence[] charSequenceArr = {editTextCaption == null ? _UrlKt.FRAGMENT_ENCODE_SET : editTextCaption.getTextToUse()};
            ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, true);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequenceArr[0].toString());
            MessageObject.addEntitiesToText(spannableStringBuilder, entities, true, true, false, true);
            messageObject2.caption = MessageObject.replaceAnimatedEmoji(Emoji.replaceEmoji((CharSequence) spannableStringBuilder, Theme.chat_msgTextPaint.getFontMetricsInt(), false, (int[]) null), entities, Theme.chat_msgTextPaint.getFontMetricsInt());
        }
        return messageObject2;
    }

    void lambda$setEditingMessageObject$94(View view) {
        doneEditingMessage();
    }

    public void lambda$setEditingMessageObject$95(ArrayList arrayList, MessagePreviewView.ToggleButton toggleButton, MessageSendPreview messageSendPreview, View view) {
        this.captionAbove = !this.captionAbove;
        for (int i = 0; i < arrayList.size(); i++) {
            ((MessageObject) arrayList.get(i)).messageOwner.invert_media = this.captionAbove;
        }
        toggleButton.setState(!this.captionAbove, true);
        if (!arrayList.isEmpty()) {
            messageSendPreview.changeMessage((MessageObject) arrayList.get(0));
        }
        messageSendPreview.scrollTo(!this.captionAbove);
    }

    public void lambda$setEditingMessageObject$98(CharSequence charSequence) {
        setFieldText(charSequence);
        this.setTextFieldRunnable = null;
    }

    public static CharSequence applyMessageEntities(ArrayList<TLRPC.MessageEntity> arrayList, CharSequence charSequence, Paint.FontMetricsInt fontMetricsInt) {
        AnimatedEmojiSpan animatedEmojiSpan;
        MediaDataController.sortEntities(arrayList);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(FormattedDateSpan.restoreFormatedDateEntities(charSequence));
        Object[] spans = spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), Object.class);
        if (spans != null && spans.length > 0) {
            for (Object obj : spans) {
                spannableStringBuilder.removeSpan(obj);
            }
        }
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                try {
                    TLRPC.MessageEntity messageEntity = arrayList.get(i);
                    if (messageEntity.offset + messageEntity.length <= spannableStringBuilder.length()) {
                        if (messageEntity instanceof TLRPC.TL_inputMessageEntityMentionName) {
                            if (messageEntity.offset + messageEntity.length < spannableStringBuilder.length() && spannableStringBuilder.charAt(messageEntity.offset + messageEntity.length) == ' ') {
                                messageEntity.length++;
                            }
                            URLSpanUserMention uRLSpanUserMention = new URLSpanUserMention(_UrlKt.FRAGMENT_ENCODE_SET + ((TLRPC.TL_inputMessageEntityMentionName) messageEntity).user_id.user_id, 3);
                            int i2 = messageEntity.offset;
                            spannableStringBuilder.setSpan(uRLSpanUserMention, i2, messageEntity.length + i2, 33);
                        } else if (messageEntity instanceof TLRPC.TL_messageEntityMentionName) {
                            if (messageEntity.offset + messageEntity.length < spannableStringBuilder.length() && spannableStringBuilder.charAt(messageEntity.offset + messageEntity.length) == ' ') {
                                messageEntity.length++;
                            }
                            URLSpanUserMention uRLSpanUserMention2 = new URLSpanUserMention(_UrlKt.FRAGMENT_ENCODE_SET + ((TLRPC.TL_messageEntityMentionName) messageEntity).user_id, 3);
                            int i3 = messageEntity.offset;
                            spannableStringBuilder.setSpan(uRLSpanUserMention2, i3, messageEntity.length + i3, 33);
                        } else if (messageEntity instanceof TLRPC.TL_messageEntityCode) {
                            TextStyleSpan.TextStyleRun textStyleRun = new TextStyleSpan.TextStyleRun();
                            textStyleRun.flags |= 4;
                            TextStyleSpan textStyleSpan = new TextStyleSpan(textStyleRun);
                            int i4 = messageEntity.offset;
                            MediaDataController.addStyleToText(textStyleSpan, i4, messageEntity.length + i4, spannableStringBuilder, true);
                        } else if (!(messageEntity instanceof TLRPC.TL_messageEntityPre)) {
                            if (messageEntity instanceof TLRPC.TL_messageEntityBold) {
                                TextStyleSpan.TextStyleRun textStyleRun2 = new TextStyleSpan.TextStyleRun();
                                textStyleRun2.flags |= 1;
                                TextStyleSpan textStyleSpan2 = new TextStyleSpan(textStyleRun2);
                                int i5 = messageEntity.offset;
                                MediaDataController.addStyleToText(textStyleSpan2, i5, messageEntity.length + i5, spannableStringBuilder, true);
                            } else if (messageEntity instanceof TLRPC.TL_messageEntityItalic) {
                                TextStyleSpan.TextStyleRun textStyleRun3 = new TextStyleSpan.TextStyleRun();
                                textStyleRun3.flags |= 2;
                                TextStyleSpan textStyleSpan3 = new TextStyleSpan(textStyleRun3);
                                int i6 = messageEntity.offset;
                                MediaDataController.addStyleToText(textStyleSpan3, i6, messageEntity.length + i6, spannableStringBuilder, true);
                            } else if (messageEntity instanceof TLRPC.TL_messageEntityStrike) {
                                TextStyleSpan.TextStyleRun textStyleRun4 = new TextStyleSpan.TextStyleRun();
                                textStyleRun4.flags |= 8;
                                TextStyleSpan textStyleSpan4 = new TextStyleSpan(textStyleRun4);
                                int i7 = messageEntity.offset;
                                MediaDataController.addStyleToText(textStyleSpan4, i7, messageEntity.length + i7, spannableStringBuilder, true);
                            } else if (messageEntity instanceof TLRPC.TL_messageEntityUnderline) {
                                TextStyleSpan.TextStyleRun textStyleRun5 = new TextStyleSpan.TextStyleRun();
                                textStyleRun5.flags |= 16;
                                TextStyleSpan textStyleSpan5 = new TextStyleSpan(textStyleRun5);
                                int i8 = messageEntity.offset;
                                MediaDataController.addStyleToText(textStyleSpan5, i8, messageEntity.length + i8, spannableStringBuilder, true);
                            } else if (messageEntity instanceof TLRPC.TL_messageEntityTextUrl) {
                                URLSpanReplacement uRLSpanReplacement = new URLSpanReplacement(messageEntity.url);
                                int i9 = messageEntity.offset;
                                spannableStringBuilder.setSpan(uRLSpanReplacement, i9, messageEntity.length + i9, 33);
                            } else if (messageEntity instanceof TLRPC.TL_messageEntityFormattedDate) {
                                TextStyleSpan.TextStyleRun textStyleRun6 = new TextStyleSpan.TextStyleRun();
                                textStyleRun6.flags |= 128;
                                int i10 = messageEntity.offset;
                                textStyleRun6.start = i10;
                                textStyleRun6.end = i10 + messageEntity.length;
                                textStyleRun6.urlEntity = messageEntity;
                                int i11 = messageEntity.offset;
                                FormattedDateSpan formattedDateSpan = new FormattedDateSpan(spannableStringBuilder.subSequence(i11, messageEntity.length + i11).toString(), textStyleRun6, (TLRPC.TL_messageEntityFormattedDate) messageEntity);
                                int i12 = messageEntity.offset;
                                spannableStringBuilder.setSpan(formattedDateSpan, i12, messageEntity.length + i12, 33);
                            } else if (messageEntity instanceof TLRPC.TL_messageEntitySpoiler) {
                                TextStyleSpan.TextStyleRun textStyleRun7 = new TextStyleSpan.TextStyleRun();
                                textStyleRun7.flags |= 256;
                                TextStyleSpan textStyleSpan6 = new TextStyleSpan(textStyleRun7);
                                int i13 = messageEntity.offset;
                                MediaDataController.addStyleToText(textStyleSpan6, i13, messageEntity.length + i13, spannableStringBuilder, true);
                            } else if (messageEntity instanceof TLRPC.TL_messageEntityCustomEmoji) {
                                TLRPC.TL_messageEntityCustomEmoji tL_messageEntityCustomEmoji = (TLRPC.TL_messageEntityCustomEmoji) messageEntity;
                                if (tL_messageEntityCustomEmoji.document != null) {
                                    animatedEmojiSpan = new AnimatedEmojiSpan(tL_messageEntityCustomEmoji.document, fontMetricsInt);
                                } else {
                                    animatedEmojiSpan = new AnimatedEmojiSpan(tL_messageEntityCustomEmoji.document_id, fontMetricsInt);
                                }
                                animatedEmojiSpan.local = tL_messageEntityCustomEmoji.local;
                                int i14 = messageEntity.offset;
                                spannableStringBuilder.setSpan(animatedEmojiSpan, i14, messageEntity.length + i14, 33);
                            }
                        }
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }
        QuoteSpan.mergeQuotes(spannableStringBuilder, arrayList);
        CharSequence charSequenceReplaceEmoji = Emoji.replaceEmoji((CharSequence) new SpannableStringBuilder(spannableStringBuilder), fontMetricsInt, false, (int[]) null);
        if (arrayList != null) {
            try {
                for (int size = arrayList.size() - 1; size >= 0; size--) {
                    TLRPC.MessageEntity messageEntity2 = arrayList.get(size);
                    if ((messageEntity2 instanceof TLRPC.TL_messageEntityPre) && messageEntity2.offset + messageEntity2.length <= charSequenceReplaceEmoji.length()) {
                        if (!(charSequenceReplaceEmoji instanceof Spannable)) {
                            charSequenceReplaceEmoji = new SpannableStringBuilder(charSequenceReplaceEmoji);
                        }
                        ((SpannableStringBuilder) charSequenceReplaceEmoji).insert(messageEntity2.offset + messageEntity2.length, (CharSequence) "```\n");
                        SpannableStringBuilder spannableStringBuilder2 = (SpannableStringBuilder) charSequenceReplaceEmoji;
                        int i15 = messageEntity2.offset;
                        StringBuilder sb = new StringBuilder();
                        sb.append("```");
                        String str = messageEntity2.language;
                        if (str == null) {
                            str = _UrlKt.FRAGMENT_ENCODE_SET;
                        }
                        sb.append(str);
                        sb.append("\n");
                        spannableStringBuilder2.insert(i15, (CharSequence) sb.toString());
                    }
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        return charSequenceReplaceEmoji;
    }

    public ImageView getAttachButton() {
        return this.attachButton;
    }

    public ImageView getSuggestButton() {
        return this.suggestButton;
    }

    public View getSendButton() {
        return getSendButtonInternal().getVisibility() == 0 ? getSendButtonInternal() : this.audioVideoButtonContainer;
    }

    public View getSendButtonInternal() {
        return this.sendButton;
    }

    public void setBlockedByStreaming(boolean z, boolean z2) {
        boolean z3 = this.animatorIsBlockedByStreaming.getValue() != z;
        this.animatorIsBlockedByStreaming.setValue(z, z2);
        if (z3) {
            checkSendButton(z2);
        }
    }

    public ValueAnimator animateSendButton(boolean z) {
        final float alpha = getSendButtonInternal().getAlpha();
        final float f = z ? 1.0f : 0.0f;
        final float scaleX = getSendButtonInternal().getScaleX();
        final float f2 = z ? 1.0f : 0.1f;
        final float scaleY = getSendButtonInternal().getScaleY();
        final float f3 = z ? 1.0f : 0.1f;
        if (z && alpha < 0.25f && (getSendButtonInternal() instanceof SendButton)) {
            ((SendButton) getSendButtonInternal()).appear();
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda35
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$animateSendButton$99(alpha, f, scaleX, f2, scaleY, f3, valueAnimator);
            }
        });
        return valueAnimatorOfFloat;
    }

    public void lambda$setFieldFocused$100() {
        EditTextCaption editTextCaption;
        ViewGroup view = null;
        this.focusRunnable = null;
        boolean z = true;
        if (AndroidUtilities.isTablet()) {
            Activity activity = this.parentActivity;
            if (activity instanceof LaunchActivity) {
                LaunchActivity launchActivity = (LaunchActivity) activity;
                if (launchActivity != null && launchActivity.getLayersActionBarLayout() != null) {
                    view = launchActivity.getLayersActionBarLayout().getView();
                }
                if (view != null && view.getVisibility() == 0) {
                    z = false;
                }
            }
        }
        if (this.isPaused || !z || (editTextCaption = this.messageEditText) == null) {
            return;
        }
        try {
            editTextCaption.requestFocus();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean hasText() {
        EditTextCaption editTextCaption = this.messageEditText;
        return editTextCaption != null && editTextCaption.length() > 0;
    }

    @Override // org.telegram.ui.Components.SuggestEmojiView.AnchorViewDelegate
    public EditTextCaption getEditField() {
        return this.messageEditText;
    }

    public SenderSelectView getSenderSelectView() {
        return this.senderSelectView;
    }

    @Override // org.telegram.ui.Components.SuggestEmojiView.AnchorViewDelegate
    public Editable getEditText() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null) {
            return null;
        }
        return editTextCaption.getText();
    }

    public CharSequence getDraftMessage() {
        if (this.editingMessageObject != null) {
            if (TextUtils.isEmpty(this.draftMessage)) {
                return null;
            }
            return this.draftMessage;
        }
        if (this.messageEditText == null || !hasText()) {
            return null;
        }
        return this.messageEditText.getText();
    }

    @Override // org.telegram.ui.Components.SuggestEmojiView.AnchorViewDelegate
    public CharSequence getFieldText() {
        if (this.messageEditText == null || !hasText()) {
            return null;
        }
        return this.messageEditText.getText();
    }

    public void openRichEditor() {
        RichEditor richEditor;
        if (this.messageEditText == null || this.parentFragment == null || !MessagesController.getInstance(this.currentAccount).richEditorAvailable()) {
            return;
        }
        TL_iv.RichMessage richMessage = this.richDraftMessage;
        if (richMessage != null) {
            richEditor = new RichEditor(richMessage);
        } else {
            Editable text = this.messageEditText.getText();
            CharSequence fieldMarkdown = parseFieldMarkdown(text);
            RichEditor richEditor2 = new RichEditor(fieldMarkdown);
            if (fieldMarkdown == text) {
                int selectionStart = this.messageEditText.getSelectionStart();
                int selectionEnd = this.messageEditText.getSelectionEnd();
                if (selectionStart < 0) {
                    selectionStart = this.messageEditText.length();
                }
                if (selectionEnd < 0) {
                    selectionEnd = selectionStart;
                }
                richEditor2.setInitialSelection(selectionStart, selectionEnd);
            }
            richEditor2.setOnCleared(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda51
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$openRichEditor$101();
                }
            });
            richEditor = richEditor2;
        }
        richEditor.setResourceProvider(this.resourcesProvider);
        richEditor.setChatActivity(this.parentFragment);
        richEditor.animateFrom(this.parentFragment);
        richEditor.setOnSent(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda52
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openRichEditor$102();
            }
        });
        this.parentFragment.presentFragment(richEditor);
    }

    public void lambda$openRichEditor$102() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            editTextCaption.setText(_UrlKt.FRAGMENT_ENCODE_SET);
        }
        checkSendButton(true);
    }

    public void openRichEditorWithHtml(CharSequence charSequence, String str, CharSequence charSequence2) {
        if (this.messageEditText == null || this.parentFragment == null || !MessagesController.getInstance(this.currentAccount).richEditorAvailable()) {
            return;
        }
        RichEditor htmlSurrounding = new RichEditor(str, true).setHtmlSurrounding(charSequence, charSequence2);
        htmlSurrounding.setResourceProvider(this.resourcesProvider);
        htmlSurrounding.setChatActivity(this.parentFragment);
        htmlSurrounding.animateFrom(this.parentFragment);
        htmlSurrounding.setOnCleared(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda132
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openRichEditorWithHtml$103();
            }
        });
        htmlSurrounding.setOnSent(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda133
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openRichEditorWithHtml$104();
            }
        });
        this.parentFragment.presentFragment(htmlSurrounding);
    }

    public void lambda$openRichEditorWithHtml$104() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            editTextCaption.setText(_UrlKt.FRAGMENT_ENCODE_SET);
        }
        checkSendButton(true);
    }

    public boolean handleRichHtmlPaste() {
        HashMap map;
        List<BlockRow> list;
        if (this.messageEditText == null) {
            return false;
        }
        try {
            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService("clipboard");
            ClipData primaryClip = clipboardManager == null ? null : clipboardManager.getPrimaryClip();
            if (primaryClip != null && primaryClip.getItemCount() >= 1 && primaryClip.getDescription() != null && primaryClip.getDescription().hasMimeType("text/html")) {
                String htmlText = primaryClip.getItemAt(0).getHtmlText();
                if (!TextUtils.isEmpty(htmlText) && (list = RichHtml.parse(htmlText, (map = new HashMap()))) != null && !list.isEmpty()) {
                    if (RichMessageConvert.isLossy(list, map)) {
                        if (!MessagesController.getInstance(this.currentAccount).richEditorAvailable()) {
                            return false;
                        }
                        int iMax = Math.max(0, this.messageEditText.getSelectionStart());
                        int iMin = Math.min(this.messageEditText.getText().length(), this.messageEditText.getSelectionEnd());
                        openRichEditorWithHtml(this.messageEditText.getText().subSequence(0, Math.min(iMax, iMin)), htmlText, this.messageEditText.getText().subSequence(Math.max(iMax, iMin), this.messageEditText.getText().length()));
                        return true;
                    }
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(RichMessageConvert.rowsToCharSequence(list));
                    Emoji.replaceEmoji((CharSequence) spannableStringBuilder, this.messageEditText.getPaint().getFontMetricsInt(), false, (int[]) null);
                    AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), AnimatedEmojiSpan.class);
                    if (animatedEmojiSpanArr != null) {
                        for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr) {
                            animatedEmojiSpan.applyFontMetrics(this.messageEditText.getPaint().getFontMetricsInt(), AnimatedEmojiDrawable.getCacheTypeForEnterView());
                        }
                    }
                    int iMax2 = Math.max(0, this.messageEditText.getSelectionStart());
                    int iMin2 = Math.min(this.messageEditText.getText().length(), this.messageEditText.getSelectionEnd());
                    QuoteSpan.QuoteStyleSpan[] quoteStyleSpanArr = (QuoteSpan.QuoteStyleSpan[]) this.messageEditText.getText().getSpans(iMax2, iMin2, QuoteSpan.QuoteStyleSpan.class);
                    if (quoteStyleSpanArr != null && quoteStyleSpanArr.length > 0) {
                        QuoteSpan.QuoteStyleSpan[] quoteStyleSpanArr2 = (QuoteSpan.QuoteStyleSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), QuoteSpan.QuoteStyleSpan.class);
                        for (int i = 0; i < quoteStyleSpanArr2.length; i++) {
                            spannableStringBuilder.removeSpan(quoteStyleSpanArr2[i]);
                            spannableStringBuilder.removeSpan(quoteStyleSpanArr2[i].span);
                        }
                    } else {
                        QuoteSpan.normalizeQuotes(spannableStringBuilder);
                    }
                    EditTextCaption editTextCaption = this.messageEditText;
                    editTextCaption.setText(editTextCaption.getText().replace(iMax2, iMin2, spannableStringBuilder));
                    this.messageEditText.setSelection(Math.min(iMax2 + spannableStringBuilder.length(), this.messageEditText.getText().length()));
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    private CharSequence parseFieldMarkdown(CharSequence charSequence) {
        if (!TextUtils.isEmpty(charSequence) && TextUtils.indexOf(charSequence, '`') >= 0) {
            try {
                CharSequence[] charSequenceArr = {new SpannableStringBuilder(charSequence)};
                ArrayList<TLRPC.MessageEntity> entities = MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, true);
                if (entities != null && !entities.isEmpty()) {
                    SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequenceArr[0]);
                    MessageObject.addEntitiesToText(spannableStringBuilder, entities, false, false, false, false);
                    return spannableStringBuilder;
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        return charSequence;
    }

    public boolean isRichDraftActive() {
        return this.richDraftActive;
    }

    public void setRichDraftPreview(TL_iv.RichMessage richMessage) {
        if (this.richDraftPreview == null) {
            return;
        }
        if (!MessagesController.getInstance(this.currentAccount).richEditorAvailable()) {
            richMessage = null;
        }
        this.richDraftMessage = richMessage;
        updateRichDraftPreview();
    }

    private void updateRichDraftPreview() {
        RichMessageLayout.PreviewView previewView = this.richDraftPreview;
        if (previewView == null) {
            return;
        }
        boolean z = this.richDraftActive;
        boolean z2 = this.richDraftMessage != null && this.editingMessageObject == null;
        this.richDraftActive = z2;
        if (z2) {
            previewView.setResourcesProvider(this.resourcesProvider);
            this.richDraftPreview.set(this.richDraftMessage);
            this.richDraftPreview.setVisibility(0);
            EditTextCaption editTextCaption = this.messageEditText;
            if (editTextCaption != null) {
                editTextCaption.setVisibility(8);
            }
            this.emojiButton.setVisibility(8);
            this.deleteRichDraftButton.setVisibility(0);
            this.sendButton.setLocked(!UserConfig.getInstance(this.currentAccount).isPremium());
        } else {
            previewView.setVisibility(8);
            EditTextCaption editTextCaption2 = this.messageEditText;
            if (editTextCaption2 != null) {
                editTextCaption2.setVisibility(0);
            }
            this.emojiButton.setVisibility(0);
            this.deleteRichDraftButton.setVisibility(8);
            this.sendButton.setLocked(false);
        }
        updateButtons();
        if (z != this.richDraftActive) {
            checkSendButton(true);
        }
    }

    private void updateButtons() {
        EditTextCaption editTextCaption = this.messageEditText;
        boolean z = false;
        showAiButton((editTextCaption == null || editTextCaption.getLineCount() <= 2 || this.messageEditText.getText() == null || TextUtils.isEmpty(this.messageEditText.getText().toString().trim())) ? false : true);
        EditTextCaption editTextCaption2 = this.messageEditText;
        if (editTextCaption2 != null && editTextCaption2.getLineCount() > 2 && this.messageEditText.getText() != null && !TextUtils.isEmpty(this.messageEditText.getText().toString().trim())) {
            z = true;
        }
        showRichButton(z);
    }

    private void sendRichDraftAsSimpleMessage() {
        TL_iv.RichMessage richMessage = this.richDraftMessage;
        if (richMessage == null || this.messageEditText == null) {
            return;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(RichMessageConvert.toCharSequence(richMessage));
        Emoji.replaceEmoji((CharSequence) spannableStringBuilder, this.messageEditText.getPaint().getFontMetricsInt(), false, (int[]) null);
        AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), AnimatedEmojiSpan.class);
        if (animatedEmojiSpanArr != null) {
            for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr) {
                animatedEmojiSpan.applyFontMetrics(this.messageEditText.getPaint().getFontMetricsInt(), AnimatedEmojiDrawable.getCacheTypeForEnterView());
            }
        }
        QuoteSpan.normalizeQuotes(spannableStringBuilder);
        clearRichDraft();
        setFieldText(spannableStringBuilder);
        sendMessage();
    }

    public void sendConvertedRichAsSimple(CharSequence charSequence, boolean z, int i, int i2) {
        if (this.messageEditText == null) {
            return;
        }
        if (charSequence == null) {
            charSequence = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        Emoji.replaceEmoji((CharSequence) spannableStringBuilder, this.messageEditText.getPaint().getFontMetricsInt(), false, (int[]) null);
        AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), AnimatedEmojiSpan.class);
        if (animatedEmojiSpanArr != null) {
            for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr) {
                animatedEmojiSpan.applyFontMetrics(this.messageEditText.getPaint().getFontMetricsInt(), AnimatedEmojiDrawable.getCacheTypeForEnterView());
            }
        }
        QuoteSpan.normalizeQuotes(spannableStringBuilder);
        clearRichDraft();
        setFieldText(spannableStringBuilder);
        sendMessageInternal(z, i, i2, 0L, true);
    }

    public void openRichEditorWithoutFormatting() {
        TL_iv.RichMessage richMessage = this.richDraftMessage;
        if (richMessage == null || this.messageEditText == null || this.parentFragment == null) {
            return;
        }
        if (!MessagesController.getInstance(this.currentAccount).richEditorAvailable()) {
            sendRichDraftAsSimpleMessage();
            return;
        }
        RichEditor richEditorConvertToSimpleOnOpen = new RichEditor(richMessage).convertToSimpleOnOpen();
        richEditorConvertToSimpleOnOpen.setResourceProvider(this.resourcesProvider);
        richEditorConvertToSimpleOnOpen.setChatActivity(this.parentFragment);
        richEditorConvertToSimpleOnOpen.animateFrom(this.parentFragment);
        richEditorConvertToSimpleOnOpen.setOnCleared(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda96
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openRichEditorWithoutFormatting$105();
            }
        });
        richEditorConvertToSimpleOnOpen.setOnSent(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda97
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openRichEditorWithoutFormatting$106();
            }
        });
        this.parentFragment.presentFragment(richEditorConvertToSimpleOnOpen);
    }

    public void lambda$openRichEditorWithoutFormatting$106() {
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            editTextCaption.setText(_UrlKt.FRAGMENT_ENCODE_SET);
        }
        checkSendButton(true);
    }

    private void sendRichDraft(boolean z, int i, int i2, long j) {
        TL_iv.RichMessage richMessage = this.richDraftMessage;
        if (richMessage == null) {
            return;
        }
        AccountInstance accountInstance = this.accountInstance;
        ArrayList<TL_iv.PageBlock> arrayList = richMessage.blocks;
        ArrayList<TLRPC.Photo> arrayList2 = richMessage.photos;
        ArrayList<TLRPC.Document> arrayList3 = richMessage.documents;
        long j2 = this.dialog_id;
        MessageObject messageObject = this.replyingMessageObject;
        MessageObject threadMessage = getThreadMessage();
        ChatActivity chatActivity = this.parentFragment;
        SendMessagesHelper.prepareSendingArticle(accountInstance, arrayList, arrayList2, arrayList3, null, false, j2, messageObject, threadMessage, z, i, i2, chatActivity != null ? chatActivity.quickReplyShortcut : null, chatActivity != null ? chatActivity.getQuickReplyId() : 0, this.effectId, getSendMonoForumPeerId(), j);
        SendButton sendButton = this.sendButton;
        this.effectId = 0L;
        sendButton.setEffect(0L);
        this.messageEditText.setText(_UrlKt.FRAGMENT_ENCODE_SET);
        clearRichDraft();
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onMessageSend(null, z, i, i2, j);
        }
        checkSendButton(true);
    }

    public void clearRichDraft() {
        if (this.parentFragment != null) {
            MediaDataController.getInstance(this.currentAccount).saveDraft(this.parentFragment.getDialogId(), this.parentFragment.getDraftThreadId(), _UrlKt.FRAGMENT_ENCODE_SET, null, null, null, null, 0L, false, true, null);
        }
        setRichDraftPreview(null);
    }

    public void saveRichDraft(TL_iv.RichMessage richMessage) {
        if (this.parentFragment != null) {
            MediaDataController.getInstance(this.currentAccount).saveDraft(this.parentFragment.getDialogId(), this.parentFragment.getDraftThreadId(), _UrlKt.FRAGMENT_ENCODE_SET, null, null, null, null, 0L, false, false, richMessage);
        }
        setRichDraftPreview(richMessage);
    }

    public void applyConvertedSimpleDraft(CharSequence charSequence) {
        if (this.messageEditText == null) {
            return;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence == null ? _UrlKt.FRAGMENT_ENCODE_SET : charSequence);
        Emoji.replaceEmoji((CharSequence) spannableStringBuilder, this.messageEditText.getPaint().getFontMetricsInt(), false, (int[]) null);
        AnimatedEmojiSpan[] animatedEmojiSpanArr = (AnimatedEmojiSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), AnimatedEmojiSpan.class);
        if (animatedEmojiSpanArr != null) {
            for (AnimatedEmojiSpan animatedEmojiSpan : animatedEmojiSpanArr) {
                animatedEmojiSpan.applyFontMetrics(this.messageEditText.getPaint().getFontMetricsInt(), AnimatedEmojiDrawable.getCacheTypeForEnterView());
            }
        }
        QuoteSpan.normalizeQuotes(spannableStringBuilder);
        if (this.parentFragment != null) {
            CharSequence[] charSequenceArr = {new SpannableStringBuilder(spannableStringBuilder)};
            MediaDataController.getInstance(this.currentAccount).saveDraft(this.parentFragment.getDialogId(), this.parentFragment.getDraftThreadId(), charSequenceArr[0], MediaDataController.getInstance(this.currentAccount).getEntities(charSequenceArr, true, false), null, null, null, 0L, false, false, null);
        }
        setRichDraftPreview(null);
        setFieldText(spannableStringBuilder);
    }

    void lambda$checkBirthdayHint$108() {
        removeView(this.birthdayHint);
    }

    public void setBirthdayHintText() {
        HintView2 hintView2 = this.birthdayHint;
        if (hintView2 == null) {
            return;
        }
        hintView2.setText(Emoji.replaceWithRestrictedEmoji(AndroidUtilities.replaceTags(LocaleController.formatString(R.string.UserBirthdayHint, UserObject.getFirstName(this.parentFragment.getCurrentUser()))), this.birthdayHint.getTextPaint().getFontMetricsInt(), new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda98
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.setBirthdayHintText();
            }
        }));
        HintView2 hintView3 = this.birthdayHint;
        hintView3.setMaxWidthPx(HintView2.cutInFancyHalf(hintView3.getText(), this.birthdayHint.getTextPaint()));
    }

    public boolean showSendSuggestionHint() {
        ImageView imageView;
        if (this.sendSuggestHintView != null || (imageView = this.suggestButton) == null || imageView.getVisibility() != 0 || MessagesController.getGlobalMainSettings().getInt("channelsuggesthint2", 0) >= 2) {
            return false;
        }
        HintView2 hintView2 = new HintView2(getContext(), 3);
        this.sendSuggestHintView = hintView2;
        hintView2.setRounding(13.0f);
        this.sendSuggestHintView.setMultilineText(true);
        this.sendSuggestHintView.setText(LocaleController.formatString(R.string.SuggestAPostBelowHint, ForumUtilities.getMonoForumTitle(this.currentAccount, this.dialog_id, true)));
        this.sendSuggestHintView.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        this.sendSuggestHintView.setJointPx(1.0f, -((getWidth() - AndroidUtilities.dp(12.0f)) - (((this.messageEditTextContainer.getX() + this.attachLayout.getX()) + this.suggestButton.getX()) + (this.suggestButton.getMeasuredWidth() / 2.0f))));
        addView(this.sendSuggestHintView, LayoutHelper.createFrame(-1, 200.0f, 48, 0.0f, -192.0f, 0.0f, 0.0f));
        this.sendSuggestHintView.setOnHiddenListener(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showSendSuggestionHint$109();
            }
        });
        this.sendSuggestHintView.setDuration(8000L);
        this.sendSuggestHintView.show();
        MessagesController.getGlobalMainSettings().edit().putInt("channelsuggesthint2", MessagesController.getGlobalMainSettings().getInt("channelsuggesthint2", 0) + 1).apply();
        return true;
    }

    public void lambda$updateSendAsButton$110(float f, float f2, float f3, float f4, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f5 = f + ((f2 - f) * fFloatValue);
        SenderSelectView senderSelectView = this.senderSelectView;
        if (senderSelectView != null) {
            senderSelectView.setAlpha(f3 + ((f4 - f3) * fFloatValue));
            this.senderSelectView.setTranslationX(f5);
        }
        this.emojiButton.setTranslationX(f5);
        this.messageTextTranslationX = f5;
        updateMessageTextParams();
    }

    public boolean hasBotWebView() {
        return this.botMenuButtonType == BotMenuButtonType.WEB_VIEW;
    }

    void lambda$updateBotButton$111(ValueAnimator valueAnimator) {
        ImageView imageView = this.scheduledButton;
        if (imageView != null) {
            imageView.setTranslationX(imageView.getTranslationX());
        }
    }

    public void updateBotWebView(boolean z) {
        if (this.botMenuButtonType != BotMenuButtonType.NO_BUTTON && this.dialog_id > 0) {
            createBotCommandsMenuButton();
        }
        BotCommandsMenuView botCommandsMenuView = this.botCommandsMenuButton;
        if (botCommandsMenuView != null) {
            botCommandsMenuView.setWebView(hasBotWebView());
        }
        updateBotButton(z);
    }

    public void setBotsCount(int i, boolean z, boolean z2, boolean z3) {
        this.botCount = i;
        if (this.hasBotCommands == z && this.hasQuickReplies == z2) {
            return;
        }
        this.hasBotCommands = z;
        this.hasQuickReplies = z2;
        updateBotButton(z3);
    }

    public void setButtons(MessageObject messageObject) {
        setButtons(messageObject, true);
    }

    public void setButtons(MessageObject messageObject, boolean z) {
        setButtons(messageObject, true, z);
    }

    void lambda$setButtons$112(TLRPC.KeyboardButton keyboardButton) {
        MessageObject messageObject;
        ChatActivity chatActivity;
        boolean z = this.replyingMessageObject != null && (chatActivity = this.parentFragment) != null && chatActivity.isTopic && chatActivity.getTopicId() == ((long) this.replyingMessageObject.getId());
        if ((this.replyingMessageObject == null || z) && !BotForumHelper.isBotForum(this.currentAccount, this.dialog_id)) {
            messageObject = DialogObject.isChatDialog(this.dialog_id) ? this.botButtonsMessageObject : null;
        } else {
            messageObject = this.replyingMessageObject;
        }
        MessageObject messageObject2 = this.replyingMessageObject;
        if (messageObject2 == null || z) {
            messageObject2 = this.botButtonsMessageObject;
        }
        boolean zDidPressedBotButton = didPressedBotButton(keyboardButton, messageObject, messageObject2);
        if (this.replyingMessageObject != null && !z) {
            openKeyboardInternal();
            setButtons(this.botMessageObject, false);
        } else {
            MessageObject messageObject3 = this.botButtonsMessageObject;
            if (messageObject3 != null && messageObject3.messageOwner.reply_markup.single_use) {
                if (zDidPressedBotButton) {
                    openKeyboardInternal();
                } else {
                    showPopup(0, 0);
                }
                MessagesController.getMainSettings(this.currentAccount).edit().putInt("answered_" + getTopicKeyString(), this.botButtonsMessageObject.getId()).apply();
            }
        }
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onMessageSend(null, true, 0, 0, 0L);
        }
    }

    public boolean didPressedBotButton(TLRPC.KeyboardButton keyboardButton, MessageObject messageObject, MessageObject messageObject2) {
        return didPressedBotButton(keyboardButton, messageObject, messageObject2, null);
    }

    void lambda$didPressedBotButton$113(Runnable runnable, long j) {
        runnable.run();
        SharedPrefsHelper.setWebViewConfirmShown(this.currentAccount, j, true);
    }

    public void lambda$didPressedBotButton$116(MessageObject messageObject, TLRPC.TL_keyboardButtonRequestPeer tL_keyboardButtonRequestPeer, TLRPC.User user, TLRPC.User user2) {
        if (user2 != null) {
            TLRPC.TL_messages_sendBotRequestedPeer tL_messages_sendBotRequestedPeer = new TLRPC.TL_messages_sendBotRequestedPeer();
            tL_messages_sendBotRequestedPeer.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(messageObject.messageOwner.peer_id);
            tL_messages_sendBotRequestedPeer.flags |= 1;
            tL_messages_sendBotRequestedPeer.msg_id = messageObject.getId();
            tL_messages_sendBotRequestedPeer.button_id = tL_keyboardButtonRequestPeer.button_id;
            tL_messages_sendBotRequestedPeer.requested_peers.add(MessagesController.getInputPeer(user2));
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_sendBotRequestedPeer, null);
            long j = user.id;
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", user2.id);
            AnonymousClass80 anonymousClass80 = new AnonymousClass80(bundle, user2, user, j);
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null) {
                chatActivity.presentFragment(anonymousClass80);
            }
        }
    }

    public class AnonymousClass80 extends ChatActivity {
        private boolean shownToast;
        final boolean lambda$didPressedBotButton$118(MessageObject messageObject, TLRPC.TL_keyboardButtonRequestPeer tL_keyboardButtonRequestPeer, DialogsActivity dialogsActivity, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, int i2, TopicsFragment topicsFragment) {
        if (arrayList != null && !arrayList.isEmpty()) {
            TLRPC.TL_messages_sendBotRequestedPeer tL_messages_sendBotRequestedPeer = new TLRPC.TL_messages_sendBotRequestedPeer();
            tL_messages_sendBotRequestedPeer.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(messageObject.messageOwner.peer_id);
            tL_messages_sendBotRequestedPeer.flags |= 1;
            tL_messages_sendBotRequestedPeer.msg_id = messageObject.getId();
            tL_messages_sendBotRequestedPeer.button_id = tL_keyboardButtonRequestPeer.button_id;
            HashSet hashSet = new HashSet();
            int size = arrayList.size();
            int i3 = 0;
            while (i3 < size) {
                Object obj = arrayList.get(i3);
                i3++;
                hashSet.add(Long.valueOf(((MessagesStorage.TopicKey) obj).dialogId));
            }
            Iterator it = hashSet.iterator();
            while (it.hasNext()) {
                tL_messages_sendBotRequestedPeer.requested_peers.add(MessagesController.getInstance(this.currentAccount).getInputPeer(((Long) it.next()).longValue()));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_sendBotRequestedPeer, null);
        }
        dialogsActivity.finishFragment();
        return true;
    }

    public boolean isPopupView(View view) {
        return view == this.botKeyboardView || view == this.emojiView;
    }

    public int getPopupViewHeight(View view) {
        BotKeyboardView botKeyboardView = this.botKeyboardView;
        if (view != botKeyboardView || botKeyboardView == null) {
            return -1;
        }
        return botKeyboardView.getKeyboardHeight();
    }

    public boolean isRecordCircle(View view) {
        return view == this.recordCircle;
    }

    public SizeNotifierFrameLayout getSizeNotifierLayout() {
        return this.sizeNotifierLayout;
    }

    public void overrideEditTextView(EditTextBoldCursor editTextBoldCursor) {
        this.mOverrideEditTextView = editTextBoldCursor;
    }

    public void createEmojiView() {
        EmojiView emojiView = this.emojiView;
        if (emojiView != null && emojiView.currentAccount != UserConfig.selectedAccount) {
            this.viewParentForEmojiView.removeView(emojiView);
            this.emojiView = null;
        }
        if (this.emojiView != null) {
            return;
        }
        TLRPC.UserFull currentUserInfo = getParentFragment() != null ? getParentFragment().getCurrentUserInfo() : null;
        boolean z = currentUserInfo != null && currentUserInfo.id == UserConfig.getInstance(this.currentAccount).getClientUserId();
        boolean zIsPremium = UserConfig.getInstance(this.currentAccount).isPremium();
        TLRPC.ChatFull chatFull = this.info;
        boolean z2 = (chatFull == null || chatFull.emojiset == null) ? false : true;
        EmojiView emojiView2 = new EmojiView(this.parentFragment, this.allowAnimatedEmoji && (z || z2 || (LocaleUtils.canUseLocalPremiumEmojis(this.currentAccount) && !z && !z2) || zIsPremium), true, true, getContext(), true, this.info, this.sizeNotifierLayout, this.shouldDrawBackground, this.resourcesProvider, this.emojiViewFrozen, this.windowInsetsInAppController != null) { // from class: org.telegram.ui.Components.ChatActivityEnterView.81
            @Override // org.telegram.ui.Components.EmojiView, android.view.View
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                if (ChatActivityEnterView.this.panelAnimation == null || ChatActivityEnterView.this.animatingContentType != 0) {
                    return;
                }
                ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(f);
            }
        };
        this.emojiView = emojiView2;
        emojiView2.shouldDrawStickerSettings = true;
        if (!this.shouldDrawBackground) {
            emojiView2.updateColors();
        }
        this.emojiView.allowEmojisForNonPremium(LocaleUtils.canUseLocalPremiumEmojis(this.currentAccount));
        this.emojiView.setAllow(this.allowStickers, this.allowGifs, true);
        this.emojiView.setVisibility(8);
        this.emojiView.setShowing(false);
        if (this.windowInsetsInAppController != null) {
            EmojiView emojiView3 = this.emojiView;
            emojiView3.shouldLightenBackground = false;
            emojiView3.setShouldDrawBackground(false);
            this.emojiView.isNewHeightControl = true;
        }
        this.emojiView.setDelegate(new AnonymousClass82());
        this.emojiView.setDragListener(new EmojiView.DragListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView.83
            int initialOffset;
            boolean wasExpanded;

            @Override // org.telegram.ui.Components.EmojiView.DragListener
            public void onDragStart() {
                if (allowDragging()) {
                    if (ChatActivityEnterView.this.stickersExpansionAnim != null) {
                        ChatActivityEnterView.this.stickersExpansionAnim.cancel();
                    }
                    ChatActivityEnterView.this.stickersDragging = true;
                    this.wasExpanded = ChatActivityEnterView.this.stickersExpanded;
                    ChatActivityEnterView.this.stickersExpanded = true;
                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 1);
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    chatActivityEnterView.stickersExpandedHeight = ((((chatActivityEnterView.sizeNotifierLayout.getHeight() - AndroidUtilities.statusBarHeight) - AndroidUtilities.navigationBarHeight) - AndroidUtilities.dp(6.0f)) - ActionBar.getCurrentActionBarHeight()) - ChatActivityEnterView.this.getHeight();
                    if (ChatActivityEnterView.this.searchingType == 2) {
                        ChatActivityEnterView chatActivityEnterView2 = ChatActivityEnterView.this;
                        int i = chatActivityEnterView2.stickersExpandedHeight;
                        int iDp = AndroidUtilities.dp(175.0f);
                        Point point = AndroidUtilities.displaySize;
                        int i2 = point.x;
                        int i3 = point.y;
                        ChatActivityEnterView chatActivityEnterView3 = ChatActivityEnterView.this;
                        chatActivityEnterView2.stickersExpandedHeight = Math.min(i, iDp + (i2 > i3 ? chatActivityEnterView3.keyboardHeightLand : chatActivityEnterView3.keyboardHeight));
                    }
                    if (ChatActivityEnterView.this.windowInsetsInAppController == null) {
                        ChatActivityEnterView.this.emojiView.getLayoutParams().height = ChatActivityEnterView.this.stickersExpandedHeight;
                    }
                    ChatActivityEnterView.this.emojiView.setLayerType(2, null);
                    ChatActivityEnterView.this.sizeNotifierLayout.requestLayout();
                    ChatActivityEnterView chatActivityEnterView4 = ChatActivityEnterView.this;
                    if (chatActivityEnterView4.shouldDrawBackground) {
                        chatActivityEnterView4.sizeNotifierLayout.setForeground(ChatActivityEnterView.this.new ScrimDrawable());
                    }
                    this.initialOffset = (int) ChatActivityEnterView.this.getTranslationY();
                    if (ChatActivityEnterView.this.delegate != null) {
                        ChatActivityEnterView.this.delegate.onStickersExpandedChange();
                    }
                }
            }

            @Override // org.telegram.ui.Components.EmojiView.DragListener
            public void onDragEnd(float f) {
                if (allowDragging()) {
                    ChatActivityEnterView.this.stickersDragging = false;
                    if ((this.wasExpanded && f >= AndroidUtilities.dp(200.0f)) || ((!this.wasExpanded && f <= AndroidUtilities.dp(-200.0f)) || ((this.wasExpanded && ChatActivityEnterView.this.stickersExpansionProgress <= 0.6f) || (!this.wasExpanded && ChatActivityEnterView.this.stickersExpansionProgress >= 0.4f)))) {
                        ChatActivityEnterView.this.setStickersExpanded(!this.wasExpanded, true, true);
                    } else {
                        ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded, true, true);
                    }
                }
            }

            @Override // org.telegram.ui.Components.EmojiView.DragListener
            public void onDragCancel() {
                if (ChatActivityEnterView.this.stickersTabOpen) {
                    ChatActivityEnterView.this.stickersDragging = false;
                    ChatActivityEnterView.this.setStickersExpanded(this.wasExpanded, true, false);
                }
            }

            @Override // org.telegram.ui.Components.EmojiView.DragListener
            public void onDrag(int i) {
                if (allowDragging()) {
                    Point point = AndroidUtilities.displaySize;
                    int i2 = point.x;
                    int i3 = point.y;
                    ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
                    int i4 = i2 > i3 ? chatActivityEnterView.keyboardHeightLand : chatActivityEnterView.keyboardHeight;
                    int iMax = Math.max(Math.min(i + this.initialOffset, 0), -(ChatActivityEnterView.this.stickersExpandedHeight - i4));
                    if (ChatActivityEnterView.this.windowInsetsInAppController == null) {
                        float f = iMax;
                        ChatActivityEnterView.this.emojiView.setTranslationY(f);
                        ChatActivityEnterView.this.setTranslationY(f);
                    }
                    ChatActivityEnterView chatActivityEnterView2 = ChatActivityEnterView.this;
                    chatActivityEnterView2.stickersExpansionProgress = iMax / (-(chatActivityEnterView2.stickersExpandedHeight - i4));
                    ChatActivityEnterView.this.sizeNotifierLayout.invalidate();
                }
            }

            private boolean allowDragging() {
                EditTextCaption editTextCaption;
                if (ChatActivityEnterView.this.stickersTabOpen) {
                    return (ChatActivityEnterView.this.stickersExpanded || (editTextCaption = ChatActivityEnterView.this.messageEditText) == null || editTextCaption.length() <= 0) && ChatActivityEnterView.this.emojiView.areThereAnyStickers() && !ChatActivityEnterView.this.waitingForKeyboardOpen;
                }
                return false;
            }
        });
        EmojiView emojiView4 = this.emojiView;
        if (emojiView4 != null) {
            emojiView4.setStickersBanned(!this.sendPlainEnabled, !this.stickersEnabled, -this.dialog_id);
        }
        attachEmojiView();
        checkChannelRights();
    }

    public class AnonymousClass82 implements EmojiView.EmojiViewDelegate {
        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public boolean canAddCaptionToGif(TLRPC.Document document) {
            return true;
        }

        public AnonymousClass82() {
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public boolean isUserSelf() {
            return ChatActivityEnterView.this.dialog_id == UserConfig.getInstance(ChatActivityEnterView.this.currentAccount).getClientUserId();
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public boolean onBackspace() {
            EditTextBoldCursor editTextBoldCursor = ChatActivityEnterView.this.mOverrideEditTextView;
            ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
            TextView textView = editTextBoldCursor != null ? chatActivityEnterView.mOverrideEditTextView : chatActivityEnterView.messageEditText;
            if (textView == null || textView.length() == 0) {
                return false;
            }
            textView.dispatchKeyEvent(new KeyEvent(0, 67));
            return true;
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onEmojiSelected(String str) {
            EditTextBoldCursor editTextBoldCursor = ChatActivityEnterView.this.mOverrideEditTextView;
            ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
            EditText editText = editTextBoldCursor != null ? chatActivityEnterView.mOverrideEditTextView : chatActivityEnterView.messageEditText;
            if (editText == null) {
                return;
            }
            int selectionEnd = editText.getSelectionEnd();
            if (selectionEnd < 0) {
                selectionEnd = 0;
            }
            try {
                try {
                    ChatActivityEnterView.this.innerTextChange = 2;
                    CharSequence charSequenceReplaceEmoji = Emoji.replaceEmoji((CharSequence) str, editText.getPaint().getFontMetricsInt(), false, (int[]) null);
                    editText.setText(editText.getText().insert(selectionEnd, charSequenceReplaceEmoji));
                    int length = selectionEnd + charSequenceReplaceEmoji.length();
                    editText.setSelection(length, length);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            } finally {
                ChatActivityEnterView.this.innerTextChange = 0;
            }
        }

        @Override // org.telegram.ui.Components.EmojiView.EmojiViewDelegate
        public void onCustomEmojiSelected(final long j, final TLRPC.Document document, final String str, final boolean z) {
            EditTextBoldCursor editTextBoldCursor = ChatActivityEnterView.this.mOverrideEditTextView;
            ChatActivityEnterView chatActivityEnterView = ChatActivityEnterView.this;
            final EditTextBoldCursor editTextBoldCursor2 = editTextBoldCursor != null ? chatActivityEnterView.mOverrideEditTextView : chatActivityEnterView.messageEditText;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$82$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onCustomEmojiSelected$0(editTextBoldCursor2, str, document, j, z);
                }
            });
        }

        public void lambda$onGifSelected$3(final Object obj, final MediaController.PhotoEntry photoEntry, final boolean z, final int i, final int i2, final boolean z2, final String str, final Object obj2, final Long l) {
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$82$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onGifSelected$2(obj, photoEntry, z, i, i2, z2, l, str, obj2);
                }
            };
            if (ChatActivityEnterView.this.showConfirmAlert(runnable)) {
                return;
            }
            runnable.run();
        }

        public void lambda$onStickerSelected$121(final TLRPC.Document document, final String str, final MessageObject.SendAnimationData sendAnimationData, final boolean z, final int i, final int i2, final Object obj, final boolean z2, final Long l) {
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda136
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onStickerSelected$120(document, str, sendAnimationData, z, i, i2, obj, l, z2);
            }
        };
        if (showConfirmAlert(runnable)) {
            return;
        }
        runnable.run();
    }

    public void lambda$showPopup$122() {
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.bottomPanelTranslationYChanged(0.0f);
        }
        requestLayout();
    }

    public void lambda$setSearchingTypeInternal$124(ValueAnimator valueAnimator) {
        this.searchToOpenProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.searchProgressChanged();
        }
    }

    public boolean isCurrentPageEmoji() {
        EmojiView emojiView = this.emojiView;
        return emojiView != null && emojiView.getCurrentPage() == 0;
    }

    public void openKeyboardInternal() {
        ChatActivity chatActivity;
        if ((hasBotWebView() && botCommandsMenuIsShowing()) || BaseFragment.hasSheets(this.parentFragment)) {
            return;
        }
        showPopup((AndroidUtilities.usingHardwareInput || AndroidUtilities.isInMultiwindow || ((chatActivity = this.parentFragment) != null && chatActivity.isInBubbleMode()) || this.isPaused) ? 0 : 2, 0);
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onKeyboardRequested();
        }
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption != null) {
            editTextCaption.requestFocus();
        }
        AndroidUtilities.showKeyboard(this.messageEditText);
        if (this.isPaused) {
            this.showKeyboardOnResume = true;
            return;
        }
        if (AndroidUtilities.usingHardwareInput || this.keyboardVisible || AndroidUtilities.isInMultiwindow) {
            return;
        }
        ChatActivity chatActivity2 = this.parentFragment;
        if (chatActivity2 == null || !chatActivity2.isInBubbleMode()) {
            this.waitingForKeyboardOpen = true;
            EmojiView emojiView = this.emojiView;
            if (emojiView != null) {
                emojiView.onTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), 3, 0.0f, 0.0f, 0));
            }
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
            AndroidUtilities.runOnUIThread(this.openKeyboardRunnable, 100L);
        }
    }

    public boolean isEditingMessage() {
        return this.editingMessageObject != null;
    }

    public MessageObject getEditingMessageObject() {
        return this.editingMessageObject;
    }

    public boolean isEditingCaption() {
        return this.editingCaption;
    }

    public boolean hasAudioToSend() {
        return (this.audioToSendMessageObject == null && this.videoToSendMessageObject == null) ? false : true;
    }

    public void openKeyboard() {
        if ((hasBotWebView() && botCommandsMenuIsShowing()) || BaseFragment.hasSheets(this.parentFragment)) {
            return;
        }
        ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
        if (chatActivityEnterViewDelegate != null) {
            chatActivityEnterViewDelegate.onKeyboardRequested();
        }
        EditTextCaption editTextCaption = this.messageEditText;
        if (editTextCaption == null || AndroidUtilities.showKeyboard(editTextCaption)) {
            return;
        }
        this.messageEditText.clearFocus();
        this.messageEditText.requestFocus();
    }

    public void closeKeyboard() {
        AndroidUtilities.hideKeyboard(this.messageEditText);
    }

    public boolean isPopupShowing() {
        return this.emojiViewVisible || this.botKeyboardViewVisible;
    }

    public boolean closeCreationLinkDialog(boolean z) {
        EditTextCaption editTextCaption = this.messageEditText;
        return editTextCaption != null && editTextCaption.closeCreationLinkDialog(z);
    }

    public boolean isKeyboardVisible() {
        return this.keyboardVisible;
    }

    public boolean isWaitingForKeyboard() {
        return this.waitingForKeyboardOpen;
    }

    public void addRecentGif(TLRPC.Document document) {
        MediaDataController.getInstance(this.currentAccount).addRecentGif(document, (int) (System.currentTimeMillis() / 1000), true);
        EmojiView emojiView = this.emojiView;
        if (emojiView != null) {
            emojiView.addRecentGif(document);
        }
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        if (i != i3 && this.stickersExpanded) {
            setSearchingTypeInternal(0, false);
            this.emojiView.closeSearch(false);
            setStickersExpanded(false, false, false);
        }
        VideoTimelineView videoTimelineView = this.videoTimelineView;
        if (videoTimelineView != null) {
            videoTimelineView.clearFrames();
        }
    }

    public boolean isStickersExpanded() {
        return this.stickersExpanded;
    }

    @Override // org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate
    public void onSizeChanged(int i, boolean z) {
        MessageObject messageObject;
        EditTextCaption editTextCaption;
        TLRPC.TL_replyKeyboardMarkup tL_replyKeyboardMarkup;
        boolean z2;
        View view;
        int i2;
        int i3;
        WindowInsetsInAppController windowInsetsInAppController;
        if (this.searchingType != 0) {
            this.lastSizeChangeValue1 = i;
            this.lastSizeChangeValue2 = z;
            this.keyboardVisible = i > 0;
            checkBotMenu();
            return;
        }
        if (i > AndroidUtilities.dp(50.0f) && this.keyboardVisible && !AndroidUtilities.isInMultiwindow) {
            if (z) {
                this.keyboardHeightLand = i;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height_land3", this.keyboardHeightLand).apply();
            } else {
                this.keyboardHeight = i;
                MessagesController.getGlobalEmojiSettings().edit().putInt("kbd_height", this.keyboardHeight).apply();
            }
        }
        if (this.keyboardVisible && this.emojiViewVisible && this.emojiView == null) {
            this.emojiViewVisible = false;
        }
        if (isPopupShowing()) {
            int iMin = z ? this.keyboardHeightLand : this.keyboardHeight;
            ChatActivity chatActivity = this.parentFragment;
            if (chatActivity != null && chatActivity.getParentLayout() != null) {
                iMin -= this.parentFragment.getParentLayout().getBottomTabsHeight(false);
            }
            if (this.currentPopupContentType == 1 && !this.botKeyboardView.isFullSize()) {
                iMin = Math.min(this.botKeyboardView.getKeyboardHeight(), iMin);
            }
            int i4 = this.currentPopupContentType;
            if (i4 == 0) {
                view = this.emojiView;
            } else {
                view = i4 == 1 ? this.botKeyboardView : null;
            }
            BotKeyboardView botKeyboardView = this.botKeyboardView;
            if (botKeyboardView != null && i4 == 1) {
                botKeyboardView.setPanelHeight(iMin);
                WindowInsetsInAppController windowInsetsInAppController2 = this.windowInsetsInAppController;
                if (windowInsetsInAppController2 != null && iMin > 0 && this.currentPopupContentType == 1) {
                    windowInsetsInAppController2.requestInAppKeyboardHeightIncludeNavbar(iMin);
                }
            }
            if (view != null) {
                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) view.getLayoutParams();
                if (!this.closeAnimationInProgress && !this.stickersExpanded && (((i2 = layoutParams.width) != (i3 = AndroidUtilities.displaySize.x) || layoutParams.height != iMin) && ((windowInsetsInAppController = this.windowInsetsInAppController) == null || i2 != -1 || layoutParams.height != -1))) {
                    if (windowInsetsInAppController == null) {
                        layoutParams.width = i3;
                        layoutParams.height = iMin;
                        view.setLayoutParams(layoutParams);
                    }
                    SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierLayout;
                    if (sizeNotifierFrameLayout != null) {
                        int i5 = this.emojiPadding;
                        this.emojiPadding = layoutParams.height;
                        sizeNotifierFrameLayout.requestLayout();
                        onWindowSizeChanged();
                        if (this.smoothKeyboard && !this.keyboardVisible && i5 != this.emojiPadding && pannelAnimationEnabled()) {
                            AnimatorSet animatorSet = new AnimatorSet();
                            this.panelAnimation = animatorSet;
                            WindowInsetsInAppController windowInsetsInAppController3 = this.windowInsetsInAppController;
                            int i6 = this.emojiPadding;
                            if (windowInsetsInAppController3 != null) {
                                animatorSet.playTogether(ValueAnimator.ofFloat(i6 - i5, 0.0f));
                            } else {
                                animatorSet.playTogether(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.TRANSLATION_Y, i6 - i5, 0.0f));
                            }
                            this.panelAnimation.setInterpolator(AdjustPanLayoutHelper.keyboardInterpolator);
                            this.panelAnimation.setDuration(250L);
                            this.panelAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.88
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animator) {
                                    ChatActivityEnterView.this.panelAnimation = null;
                                    if (ChatActivityEnterView.this.delegate != null) {
                                        ChatActivityEnterView.this.delegate.bottomPanelTranslationYChanged(0.0f);
                                    }
                                    ChatActivityEnterView.this.requestLayout();
                                    ChatActivityEnterView.this.notificationsLocker.unlock();
                                }
                            });
                            AndroidUtilities.runOnUIThread(this.runEmojiPanelAnimation, 50L);
                            this.notificationsLocker.lock();
                            requestLayout();
                        }
                    }
                }
            }
        }
        if (this.lastSizeChangeValue1 == i && this.lastSizeChangeValue2 == z) {
            onWindowSizeChanged();
            return;
        }
        this.lastSizeChangeValue1 = i;
        this.lastSizeChangeValue2 = z;
        boolean z3 = this.keyboardVisible;
        this.keyboardVisible = i > 0;
        checkBotMenu();
        if (this.keyboardVisible && isPopupShowing() && this.stickersExpansionAnim == null) {
            showPopup(0, this.currentPopupContentType);
        } else if (!this.keyboardVisible && !isPopupShowing() && (messageObject = this.botButtonsMessageObject) != null && this.replyingMessageObject != messageObject && !hasBotWebView() && !botCommandsMenuIsShowing() && !BaseFragment.hasSheets(this.parentFragment) && (((editTextCaption = this.messageEditText) == null || TextUtils.isEmpty(editTextCaption.getText())) && (tL_replyKeyboardMarkup = this.botReplyMarkup) != null && !tL_replyKeyboardMarkup.rows.isEmpty())) {
            boolean zAnimationInProgress = this.sizeNotifierLayout.adjustPanLayoutHelper.animationInProgress();
            SizeNotifierFrameLayout sizeNotifierFrameLayout2 = this.sizeNotifierLayout;
            if (zAnimationInProgress) {
                sizeNotifierFrameLayout2.adjustPanLayoutHelper.stopTransition();
            } else {
                sizeNotifierFrameLayout2.adjustPanLayoutHelper.ignoreOnce();
            }
            showPopup(1, 1, false);
        }
        if (this.emojiPadding != 0 && !(z2 = this.keyboardVisible) && z2 != z3 && !isPopupShowing()) {
            this.emojiPadding = 0;
            this.sizeNotifierLayout.requestLayout();
        }
        if (this.keyboardVisible && this.waitingForKeyboardOpen) {
            this.waitingForKeyboardOpen = false;
            if (this.clearBotButtonsOnKeyboardOpen) {
                this.clearBotButtonsOnKeyboardOpen = false;
                this.botKeyboardView.setButtons(this.botReplyMarkup);
            }
            AndroidUtilities.cancelRunOnUIThread(this.openKeyboardRunnable);
        }
        onWindowSizeChanged();
    }

    public void checkReactionsButton(boolean z) {
        AndroidUtilities.updateViewVisibilityAnimated(this.reactionsButton, z, 0.1f, true);
    }

    public int getEmojiPadding() {
        return this.emojiPadding;
    }

    public int getVisibleEmojiPadding() {
        if (this.emojiViewVisible) {
            return this.emojiPadding;
        }
        return 0;
    }

    public MessageObject getThreadMessage() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity != null) {
            return chatActivity.getThreadMessage();
        }
        return null;
    }

    public int getThreadMessageId() {
        ChatActivity chatActivity = this.parentFragment;
        if (chatActivity == null || chatActivity.getThreadMessage() == null) {
            return 0;
        }
        return this.parentFragment.getThreadMessage().getId();
    }

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        SendButton sendButton;
        TLRPC.ChatFull chatFull;
        TLRPC.Chat chat;
        double d;
        ImageView imageView;
        if (i == NotificationCenter.emojiLoaded) {
            EmojiView emojiView = this.emojiView;
            if (emojiView != null) {
                emojiView.invalidateViews();
            }
            BotKeyboardView botKeyboardView = this.botKeyboardView;
            if (botKeyboardView != null) {
                botKeyboardView.invalidateViews();
            }
            EditTextCaption editTextCaption = this.messageEditText;
            if (editTextCaption != null) {
                editTextCaption.postInvalidate();
                this.messageEditText.invalidateForce();
                return;
            }
            return;
        }
        if (i == NotificationCenter.recordProgressChanged) {
            if (((Integer) objArr[0]).intValue() != this.recordingGuid) {
                return;
            }
            if (this.recordInterfaceState != 0 && !this.wasSendTyping && !isInScheduleMode()) {
                this.wasSendTyping = true;
                this.accountInstance.getMessagesController().sendTyping(this.dialog_id, getThreadMessageId(), isInVideoMode() ? 7 : 1, 0);
            }
            RecordCircle recordCircle = this.recordCircle;
            if (recordCircle != null) {
                recordCircle.setAmplitude(((Double) objArr[1]).doubleValue());
                return;
            }
            return;
        }
        if (i == NotificationCenter.closeChats) {
            EditTextCaption editTextCaption2 = this.messageEditText;
            if (editTextCaption2 == null || !editTextCaption2.isFocused()) {
                return;
            }
            AndroidUtilities.hideKeyboard(this.messageEditText);
            return;
        }
        int i3 = 5;
        if (i == NotificationCenter.recordStartError || i == NotificationCenter.recordStopped) {
            if (((Integer) objArr[0]).intValue() == this.recordingGuid && this.recordingAudioVideo) {
                this.recordingAudioVideo = false;
                if (i == NotificationCenter.recordStopped) {
                    Integer num = (Integer) objArr[1];
                    if (num.intValue() == 4) {
                        i3 = 4;
                    } else if (isInVideoMode() && num.intValue() == 5) {
                        i3 = 1;
                    } else if (num.intValue() != 0) {
                        i3 = num.intValue() == 6 ? 2 : 3;
                    }
                    if (i3 != 3) {
                        updateRecordInterface(i3, true);
                        return;
                    }
                    return;
                }
                updateRecordInterface(2, true);
                return;
            }
            return;
        }
        if (i == NotificationCenter.recordStarted) {
            if (((Integer) objArr[0]).intValue() != this.recordingGuid) {
                return;
            }
            boolean zBooleanValue = ((Boolean) objArr[1]).booleanValue();
            this.isInVideoMode = !zBooleanValue;
            View view = this.audioVideoSendButton;
            if (view != null) {
                if (view instanceof ChatActivityEnterViewAnimatedIconView) {
                    ((ChatActivityEnterViewAnimatedIconView) view).setState(zBooleanValue ? ChatActivityEnterViewAnimatedIconView.State.VOICE : ChatActivityEnterViewAnimatedIconView.State.VIDEO, true);
                } else if (view instanceof ChatActivityEnterViewStaticIconView) {
                    ((ChatActivityEnterViewStaticIconView) view).setState(zBooleanValue ? ChatActivityEnterViewStaticIconView.State.VOICE : ChatActivityEnterViewStaticIconView.State.VIDEO, true);
                }
            }
            if (!this.recordingAudioVideo) {
                this.recordingAudioVideo = true;
                updateRecordInterface(0, true);
            } else {
                RecordCircle recordCircle2 = this.recordCircle;
                if (recordCircle2 != null) {
                    recordCircle2.showWaves(true, true);
                }
            }
            TimerView timerView = this.recordTimerView;
            if (timerView != null) {
                timerView.start(this.millisecondsRecorded);
            }
            RecordDot recordDot = this.recordDot;
            if (recordDot != null) {
                recordDot.enterAnimation = false;
                return;
            }
            return;
        }
        byte[] bArr = null;
        if (i == NotificationCenter.recordPaused) {
            this.recordingAudioVideo = false;
            this.audioToSend = null;
            this.videoToSendMessageObject = null;
            return;
        }
        if (i == NotificationCenter.recordResumed) {
            this.audioToSend = null;
            this.videoToSendMessageObject = null;
            TimerView timerView2 = this.recordTimerView;
            if (timerView2 != null) {
                timerView2.start(this.millisecondsRecorded);
            }
            checkSendButton(true);
            this.recordingAudioVideo = true;
            updateRecordInterface(0, true);
            return;
        }
        if (i == NotificationCenter.audioDidSent) {
            if (((Integer) objArr[0]).intValue() != this.recordingGuid) {
                return;
            }
            this.millisecondsRecorded = 0L;
            Object obj = objArr[1];
            if (obj instanceof VideoEditedInfo) {
                VideoEditedInfo videoEditedInfo = (VideoEditedInfo) obj;
                this.videoToSendMessageObject = videoEditedInfo;
                String str = (String) objArr[2];
                this.audioToSendPath = str;
                ArrayList<Bitmap> arrayList = (ArrayList) objArr[3];
                this.millisecondsRecorded = videoEditedInfo.estimatedDuration;
                VideoTimelineView videoTimelineView = this.videoTimelineView;
                if (videoTimelineView != null) {
                    videoTimelineView.setVideoPath(str);
                    this.videoTimelineView.setKeyframes(arrayList);
                    this.videoTimelineView.setVisibility(0);
                    this.videoTimelineView.setMinProgressDiff(1000.0f / this.videoToSendMessageObject.estimatedDuration);
                    isRecordingStateChanged();
                }
                updateRecordInterface(3, true);
                checkSendButton(false);
                return;
            }
            this.audioToSend = (TLRPC.TL_document) obj;
            this.audioToSendPath = (String) objArr[2];
            boolean z = objArr.length >= 4 && ((Boolean) objArr[3]).booleanValue();
            float fFloatValue = objArr.length >= 5 ? ((Float) objArr[4]).floatValue() : 0.0f;
            float fFloatValue2 = objArr.length >= 6 ? ((Float) objArr[5]).floatValue() : 1.0f;
            if (this.audioToSend != null) {
                createRecordAudioPanel();
                if (this.recordedAudioPanel == null) {
                    return;
                }
                TLRPC.TL_message tL_message = new TLRPC.TL_message();
                tL_message.out = true;
                tL_message.id = 0;
                tL_message.peer_id = new TLRPC.TL_peerUser();
                TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
                tL_message.from_id = tL_peerUser;
                TLRPC.Peer peer = tL_message.peer_id;
                long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
                tL_peerUser.user_id = clientUserId;
                peer.user_id = clientUserId;
                tL_message.date = (int) (System.currentTimeMillis() / 1000);
                tL_message.message = _UrlKt.FRAGMENT_ENCODE_SET;
                tL_message.attachPath = this.audioToSendPath;
                TLRPC.TL_messageMediaDocument tL_messageMediaDocument = new TLRPC.TL_messageMediaDocument();
                tL_message.media = tL_messageMediaDocument;
                tL_messageMediaDocument.flags |= 3;
                tL_messageMediaDocument.document = this.audioToSend;
                tL_message.flags |= 768;
                this.audioToSendMessageObject = new MessageObject(UserConfig.selectedAccount, tL_message, false, true);
                this.recordedAudioPanel.setAlpha(1.0f);
                this.recordedAudioPanel.setVisibility(0);
                this.recordDeleteImageView.setVisibility(0);
                this.recordDeleteImageView.setAlpha(0.0f);
                this.recordDeleteImageView.setScaleY(0.0f);
                this.recordDeleteImageView.setScaleX(0.0f);
                int i4 = 0;
                while (true) {
                    if (i4 >= this.audioToSend.attributes.size()) {
                        d = 0.0d;
                        break;
                    }
                    TLRPC.DocumentAttribute documentAttribute = this.audioToSend.attributes.get(i4);
                    if (documentAttribute instanceof TLRPC.TL_documentAttributeAudio) {
                        d = documentAttribute.duration;
                        break;
                    }
                    i4++;
                }
                double d2 = d;
                for (int i5 = 0; i5 < this.audioToSend.attributes.size(); i5++) {
                    TLRPC.DocumentAttribute documentAttribute2 = this.audioToSend.attributes.get(i5);
                    if (documentAttribute2 instanceof TLRPC.TL_documentAttributeAudio) {
                        byte[] bArr2 = documentAttribute2.waveform;
                        if (bArr2 == null || bArr2.length == 0) {
                            documentAttribute2.waveform = MediaController.getWaveform(this.audioToSendPath);
                        }
                        bArr = documentAttribute2.waveform;
                        break;
                    }
                }
                byte[] bArr3 = bArr;
                if (z && (imageView = this.attachButton) != null) {
                    this.attachButtonAlpha = 0.0f;
                    imageView.setAlpha(0.0f);
                    this.attachButton.setScaleX(0.0f);
                    this.attachButton.setScaleY(0.0f);
                }
                this.millisecondsRecorded = (long) (1000.0d * d2);
                this.audioTimelineView.init(this.audioToSendPath, d2, bArr3, fFloatValue, fFloatValue2);
                checkSendButton(false);
                if (z) {
                    createRecordCircle();
                    createRecordPanel();
                    createRecordAudioPanel();
                    this.recordInterfaceState = 1;
                    this.recordCircle.resetLockTranslation(false);
                    this.recordControlsCircleScale.set(this.recordCircle, Float.valueOf(1.0f));
                    ControlsView controlsView = this.controlsView;
                    if (controlsView != null) {
                        controlsView.setVisibility(0);
                        this.controlsView.setAlpha(1.0f);
                    }
                }
                updateRecordInterface(3, !z);
                return;
            }
            ChatActivityEnterViewDelegate chatActivityEnterViewDelegate = this.delegate;
            if (chatActivityEnterViewDelegate != null) {
                chatActivityEnterViewDelegate.onMessageSend(null, true, 0, 0, 0L);
                return;
            }
            return;
        }
        if (i == NotificationCenter.audioRouteChanged) {
            if (this.parentActivity != null) {
                this.parentActivity.setVolumeControlStream(((Boolean) objArr[0]).booleanValue() ? 0 : Integer.MIN_VALUE);
                return;
            }
            return;
        }
        if (i == NotificationCenter.messagePlayingProgressDidChanged) {
            if (this.audioToSendMessageObject == null || !MediaController.getInstance().isPlayingMessage(this.audioToSendMessageObject)) {
                return;
            }
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            MessageObject messageObject = this.audioToSendMessageObject;
            messageObject.audioProgress = playingMessageObject.audioProgress;
            messageObject.audioProgressSec = playingMessageObject.audioProgressSec;
            return;
        }
        if (i == NotificationCenter.featuredStickersDidLoad) {
            View view2 = this.emojiButton;
            if (view2 != null) {
                view2.invalidate();
                return;
            }
            return;
        }
        if (i == NotificationCenter.messageReceivedByServer2) {
            if (((Boolean) objArr[6]).booleanValue()) {
                return;
            }
            long jLongValue = ((Long) objArr[3]).longValue();
            Integer num2 = (Integer) objArr[1];
            if (jLongValue != this.dialog_id || (chatFull = this.info) == null || chatFull.slowmode_seconds == 0 || MessageObject.isEphemeralMessageId(num2.intValue()) || (chat = this.accountInstance.getMessagesController().getChat(Long.valueOf(this.info.id))) == null || ChatObject.hasAdminRights(chat) || ChatObject.isIgnoredChatRestrictionsForBoosters(chat)) {
                return;
            }
            TLRPC.ChatFull chatFull2 = this.info;
            int currentTime = ConnectionsManager.getInstance(this.currentAccount).getCurrentTime();
            TLRPC.ChatFull chatFull3 = this.info;
            chatFull2.slowmode_next_send_date = currentTime + chatFull3.slowmode_seconds;
            chatFull3.flags |= 262144;
            setSlowModeTimer(chatFull3.slowmode_next_send_date);
            return;
        }
        if (i == NotificationCenter.sendingMessagesChanged) {
            if (this.info != null) {
                updateSlowModeText();
                return;
            }
            return;
        }
        if (i == NotificationCenter.audioRecordTooShort) {
            this.audioToSend = null;
            this.videoToSendMessageObject = null;
            updateRecordInterface(4, true);
            return;
        }
        if (i == NotificationCenter.updateBotMenuButton) {
            long jLongValue2 = ((Long) objArr[0]).longValue();
            TL_bots.BotMenuButton botMenuButton = (TL_bots.BotMenuButton) objArr[1];
            if (jLongValue2 == this.dialog_id) {
                if (botMenuButton instanceof TL_bots.TL_botMenuButton) {
                    TL_bots.TL_botMenuButton tL_botMenuButton = (TL_bots.TL_botMenuButton) botMenuButton;
                    this.botMenuWebViewTitle = tL_botMenuButton.text;
                    this.botMenuWebViewUrl = tL_botMenuButton.url;
                    this.botMenuButtonType = BotMenuButtonType.WEB_VIEW;
                } else if (this.hasBotCommands || this.hasQuickReplies) {
                    this.botMenuButtonType = BotMenuButtonType.COMMANDS;
                } else {
                    this.botMenuButtonType = BotMenuButtonType.NO_BUTTON;
                }
                updateBotButton(false);
                return;
            }
            return;
        }
        if (i == NotificationCenter.didUpdatePremiumGiftFieldIcon) {
            updateGiftButton(true);
        } else if (i == NotificationCenter.currentUserPremiumStatusChanged && this.richDraftActive && (sendButton = this.sendButton) != null) {
            sendButton.setLocked(!UserConfig.getInstance(this.currentAccount).isPremium());
        }
    }

    public void onRequestPermissionsResultFragment(int i, String[] strArr, int[] iArr) {
        if (i != 2 || this.pendingLocationButton == null) {
            return;
        }
        if (iArr.length > 0 && iArr[0] == 0) {
            SendMessagesHelper.getInstance(this.currentAccount).sendCurrentLocation(this.pendingMessageObject, this.pendingLocationButton);
        }
        this.pendingLocationButton = null;
        this.pendingMessageObject = null;
    }

    public void checkStickresExpandHeight() {
        if (this.emojiView == null) {
            return;
        }
        Point point = AndroidUtilities.displaySize;
        int i = point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight;
        int iDp = ((((this.originalViewHeight - AndroidUtilities.statusBarHeight) - AndroidUtilities.navigationBarHeight) - AndroidUtilities.dp(6.0f)) - ActionBar.getCurrentActionBarHeight()) - getHeight();
        if (this.searchingType == 2) {
            iDp = Math.min(iDp, AndroidUtilities.dp(175.0f) + i);
        }
        int i2 = this.emojiView.getLayoutParams().height;
        if (i2 == iDp) {
            return;
        }
        Animator animator = this.stickersExpansionAnim;
        if (animator != null) {
            animator.cancel();
            this.stickersExpansionAnim = null;
        }
        this.stickersExpandedHeight = iDp;
        if (i2 > iDp) {
            final Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda21
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$checkStickresExpandHeight$125();
                }
            };
            this.emojiView.setLayerType(2, null);
            if (this.overrideKeyboardAnimation) {
                this.animationEndRunnable = runnable;
            } else {
                AnimatorSet animatorSet = new AnimatorSet();
                if (this.windowInsetsInAppController != null) {
                    animatorSet.playTogether(ValueAnimator.ofInt(-(this.stickersExpandedHeight - i)), ValueAnimator.ofInt(-(this.stickersExpandedHeight - i)));
                } else {
                    animatorSet.playTogether(ObjectAnimator.ofInt(this, (Property<ChatActivityEnterView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - i)), ObjectAnimator.ofInt(this.emojiView, (Property<EmojiView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - i)));
                    ((ObjectAnimator) animatorSet.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda22
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            this.f$0.lambda$checkStickresExpandHeight$126(valueAnimator);
                        }
                    });
                }
                animatorSet.setDuration(300L);
                animatorSet.setInterpolator(CubicBezierInterpolator.DEFAULT);
                animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.89
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator2) {
                        ChatActivityEnterView.this.stickersExpansionAnim = null;
                        runnable.run();
                    }
                });
                this.stickersExpansionAnim = animatorSet;
                animatorSet.start();
            }
        } else {
            if (this.windowInsetsInAppController == null) {
                this.emojiView.getLayoutParams().height = this.stickersExpandedHeight;
            }
            this.sizeNotifierLayout.requestLayout();
            EditTextCaption editTextCaption = this.messageEditText;
            if (editTextCaption != null) {
                int selectionStart = editTextCaption.getSelectionStart();
                int selectionEnd = this.messageEditText.getSelectionEnd();
                EditTextCaption editTextCaption2 = this.messageEditText;
                editTextCaption2.setText(editTextCaption2.getText());
                this.messageEditText.setSelection(selectionStart, selectionEnd);
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            if (this.windowInsetsInAppController != null) {
                animatorSet2.playTogether(ValueAnimator.ofInt(-(this.stickersExpandedHeight - i)), ValueAnimator.ofInt(-(this.stickersExpandedHeight - i)));
            } else {
                animatorSet2.playTogether(ObjectAnimator.ofInt(this, (Property<ChatActivityEnterView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - i)), ObjectAnimator.ofInt(this.emojiView, (Property<EmojiView, Integer>) this.roundedTranslationYProperty, -(this.stickersExpandedHeight - i)));
                ((ObjectAnimator) animatorSet2.getChildAnimations().get(0)).addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatActivityEnterView$$ExternalSyntheticLambda23
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        this.f$0.lambda$checkStickresExpandHeight$127(valueAnimator);
                    }
                });
            }
            animatorSet2.setDuration(300L);
            animatorSet2.setInterpolator(CubicBezierInterpolator.DEFAULT);
            animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatActivityEnterView.90
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator2) {
                    ChatActivityEnterView.this.stickersExpansionAnim = null;
                    ChatActivityEnterView.this.emojiView.setLayerType(0, null);
                }
            });
            this.stickersExpansionAnim = animatorSet2;
            this.emojiView.setLayerType(2, null);
            animatorSet2.start();
        }
        WindowInsetsInAppController windowInsetsInAppController = this.windowInsetsInAppController;
        if (windowInsetsInAppController != null) {
            windowInsetsInAppController.requestInAppKeyboardHeightIncludeNavbar(iDp);
        }
    }

    public void lambda$setStickersExpanded$128(int i, ValueAnimator valueAnimator) {
        this.stickersExpansionProgress = Math.abs(getTranslationY() / (-(this.stickersExpandedHeight - i)));
        this.sizeNotifierLayout.invalidate();
    }

    public void lambda$onAudioLongClick$130() {
        openCamera(true);
    }

    public void lambda$bounceCount$0(ValueAnimator valueAnimator) {
            this.countBounceScale = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        }

        private void checkBackgroundRect() {
            float fDpf2 = AndroidUtilities.dpf2(40.0f);
            float fLerp = AndroidUtilities.lerp(Math.max(fDpf2, AndroidUtilities.dpf2(20.0f) + this.priceText.getCurrentWidth()), fDpf2, this.sameWidthFactor);
            float measuredHeight = (getMeasuredHeight() - fDpf2) / 2.0f;
            float measuredWidth = this.centeredBackground ? (getMeasuredWidth() + fLerp) / 2.0f : getMeasuredWidth();
            this.backgroundRect.set(measuredWidth - fLerp, measuredHeight, measuredWidth, fDpf2 + measuredHeight);
        }
    }

    public boolean drawMessageEditText(Canvas canvas, Utilities.Callback0Return<Boolean> callback0Return) {
        float f;
        float f2 = this.topGradientAlpha.set(this.messageEditText.canScrollVertically(-1));
        float f3 = this.bottomGradientAlpha.set(this.messageEditText.canScrollVertically(1));
        if (f2 <= 0.0f && f3 <= 0.0f) {
            return callback0Return.run().booleanValue();
        }
        canvas.saveLayerAlpha(0.0f, 0.0f, this.messageEditText.getX() + this.messageEditText.getMeasuredWidth() + AndroidUtilities.dp(5.0f), this.messageEditText.getY() + this.messageEditText.getMeasuredHeight() + AndroidUtilities.dp(2.0f), 255, 31);
        boolean zBooleanValue = callback0Return.run().booleanValue();
        canvas.save();
        if (f2 > 0.0f) {
            RectF rectF = AndroidUtilities.rectTmp;
            f = 255.0f;
            rectF.set(this.messageEditText.getX() - AndroidUtilities.dp(5.0f), (this.messageEditText.getY() + this.animatedTop) - 1.0f, this.messageEditText.getX() + this.messageEditText.getMeasuredWidth() + AndroidUtilities.dp(5.0f), this.messageEditText.getY() + this.animatedTop + AndroidUtilities.dp(13.0f));
            this.clipMatrix.reset();
            this.clipMatrix.postScale(1.0f, rectF.height() / 16.0f);
            this.clipMatrix.postTranslate(rectF.left, rectF.top);
            this.clipGradient.setLocalMatrix(this.clipMatrix);
            this.gradientPaint.setAlpha((int) (f2 * 255.0f));
            canvas.drawRect(rectF, this.gradientPaint);
        } else {
            f = 255.0f;
        }
        if (f3 > 0.0f) {
            RectF rectF2 = AndroidUtilities.rectTmp;
            rectF2.set(this.messageEditText.getX() - AndroidUtilities.dp(5.0f), (this.messageEditText.getY() + this.messageEditText.getMeasuredHeight()) - AndroidUtilities.dp(15.0f), this.messageEditText.getX() + this.messageEditText.getMeasuredWidth() + AndroidUtilities.dp(5.0f), this.messageEditText.getY() + this.messageEditText.getMeasuredHeight() + AndroidUtilities.dp(2.0f) + 1.0f);
            this.clipMatrix.reset();
            this.clipMatrix.postScale(1.0f, rectF2.height() / 16.0f);
            this.clipMatrix.postRotate(180.0f);
            this.clipMatrix.postTranslate(rectF2.left, rectF2.bottom);
            this.clipGradient.setLocalMatrix(this.clipMatrix);
            this.gradientPaint.setAlpha((int) (f3 * f));
            canvas.drawRect(rectF2, this.gradientPaint);
        }
        canvas.restore();
        canvas.restore();
        return zBooleanValue;
    }

    public TLRPC.TL_textWithEntities getTextWithEntities() {
        TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
        CharSequence[] charSequenceArr = {new SpannableStringBuilder(getEditText())};
        tL_textWithEntities.entities = MediaDataController.getInstance(UserConfig.selectedAccount).getEntities(charSequenceArr, true);
        tL_textWithEntities.text = charSequenceArr[0].toString();
        return tL_textWithEntities;
    }

    public void setInAppInsetsController(WindowInsetsInAppController windowInsetsInAppController) {
        this.windowInsetsInAppController = windowInsetsInAppController;
    }

    public void setSideButtonsForAttach(ChatActivitySideControlsButtonsLayout chatActivitySideControlsButtonsLayout) {
        this.sideButtons = chatActivitySideControlsButtonsLayout;
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        if (i == 0 || i == 1) {
            checkUi_IslandTotalHeight();
            checkUi_TopViewVisibility();
        } else {
            if (i == 2) {
                this.sendButtonBlockedByTypingView.setAlpha(f);
                this.sendButtonBlockedByTypingView.setScaleX(AndroidUtilities.lerp(0.5f, 1.0f, f));
                this.sendButtonBlockedByTypingView.setScaleY(AndroidUtilities.lerp(0.5f, 1.0f, f));
                this.sendButtonBlockedByTypingView.setVisibility(f <= 0.0f ? 4 : 0);
            } else if (i == 3) {
                this.sendButtonContainer.setScaleX(AndroidUtilities.lerp(1.0f, 0.79f, f));
                this.sendButtonContainer.setScaleY(AndroidUtilities.lerp(1.0f, 0.79f, f));
                this.sendOutlineView.setScaleX(AndroidUtilities.lerp(0.79f, 1.0f, f));
                this.sendOutlineView.setScaleY(AndroidUtilities.lerp(0.79f, 1.0f, f));
                this.sendOutlineView.setVisibility(f <= 0.0f ? 8 : 0);
                this.sendOutlineView.setAlpha(f);
                SendButton sendButton = this.sendButton;
                if (sendButton != null) {
                    sendButton.setSameWidthFactor(f);
                }
            }
        }
        invalidate();
    }

    public void checkUi_TopViewVisibility() {
        float floatValue = this.animatorTopViewVisibility.getFloatValue();
        if (this.topView != null) {
            this.topView.setTranslationY(((getMeasuredHeight() - this.animatorInputFieldHeight.getFactor()) - (getTopViewHeightForAnimation() * floatValue)) + this.animatedTop);
            this.topView.setVisibility(floatValue > 0.0f ? 0 : 4);
        }
        resizeForTopView(floatValue);
    }

    private float calculateIslandTotalHeight(boolean z) {
        float factor;
        float floatValue;
        FactorAnimator factorAnimator = this.animatorInputFieldHeight;
        if (z) {
            factor = factorAnimator.getToFactor();
        } else {
            factor = factorAnimator.getFactor();
        }
        BoolAnimator boolAnimator = this.animatorTopViewVisibility;
        if (z) {
            floatValue = boolAnimator.getValue() ? 1.0f : 0.0f;
        } else {
            floatValue = boolAnimator.getFloatValue();
        }
        return factor + (getTopViewHeightForAnimation() * floatValue);
    }

    private int getTopViewHeightForAnimation() {
        int i;
        View view = this.topView;
        if (view == null) {
            return 0;
        }
        int measuredHeight = view.getMeasuredHeight();
        if (measuredHeight > 0) {
            return measuredHeight;
        }
        ViewGroup.LayoutParams layoutParams = this.topView.getLayoutParams();
        if (layoutParams == null || (i = layoutParams.height) <= 0) {
            return 0;
        }
        return i;
    }

    private void checkUi_IslandTotalHeight() {
        this.currentIslandTotalHeightTarget = calculateIslandTotalHeight(true);
        float fCalculateIslandTotalHeight = calculateIslandTotalHeight(false);
        if (this.currentIslandTotalHeight != fCalculateIslandTotalHeight) {
            this.currentIslandTotalHeight = fCalculateIslandTotalHeight;
            onChangedIslandTotalHeight(fCalculateIslandTotalHeight);
        }
    }

    public float getIslandTotalHeight(boolean z) {
        if (z) {
            return this.currentIslandTotalHeightTarget;
        }
        return this.currentIslandTotalHeight;
    }

    public void setLiveComment(boolean z, boolean z2) {
        if (this.isLiveComment == z) {
            return;
        }
        this.isLiveComment = z;
        this.attachButton.setVisibility(z ? 8 : 0);
        if (z) {
            AndroidUtilities.removeFromParent(this.notifyButton);
        }
        if (z) {
            this.audioVideoSendButton.setVisibility(8);
        } else {
            reset();
        }
        if (!z) {
            this.currentLimit = -1;
            NumberTextView numberTextView = this.captionLimitView;
            if (numberTextView != null) {
                numberTextView.setVisibility(8);
            }
        }
        updateFieldRight(this.lastAttachVisible);
        checkSendButton(false);
    }
}
