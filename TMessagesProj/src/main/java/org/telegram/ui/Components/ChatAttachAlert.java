package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.LongSparseArray;
import android.util.Property;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.RoundedCorner;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.components.TranslateBeforeSendWrapper;
import com.exteragram.messenger.utils.system.SystemUtils;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import com.google.android.exoplayer2.util.Consumer;
import com.google.android.material.timepicker.TimeModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import kotlin.time.DurationKt;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import me.vkryl.android.animator.ListAnimator;
import me.vkryl.android.animator.ReplaceAnimator;
import me.vkryl.core.BitwiseUtils;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
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
import org.telegram.messenger.NotificationsSettingsFacade;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.utils.EphemeralMessagesHelper;
import org.telegram.messenger.utils.FBool;
import org.telegram.messenger.utils.GradientProtectionDrawable;
import org.telegram.messenger.utils.RectFMergeBounding;
import org.telegram.messenger.utils.ViewOutlineProviderImpl;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Business.ChatAttachAlertQuickRepliesLayout;
import org.telegram.ui.Business.QuickRepliesController;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
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
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Components.chat.layouts.ChatActivityFadeView;
import org.telegram.ui.Components.glass.GlassTabView;
import org.telegram.ui.Components.poll.PollAttachedMediaPack;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GradientClip;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.MessageSendPreview;
import org.telegram.ui.PassportActivity;
import org.telegram.ui.PaymentFormActivity;
import org.telegram.ui.PhotoPickerActivity;
import org.telegram.ui.PhotoPickerSearchActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.Stars.MessageSuggestionOfferSheet;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.Stories.recorder.StoryEntry;
import org.telegram.ui.TopicsFragment;
import org.telegram.ui.WebAppDisclaimerAlert;
import org.telegram.ui.bots.BotWebViewMenuContainer$ActionBarColorsAnimating;
import org.telegram.ui.bots.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.iv.ChatAttachAlertRichLayout;
import org.telegram.ui.web.BotWebViewContainer;

public class ChatAttachAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, BottomSheet.BottomSheetDelegateInterface, FactorAnimator.Target {
    public final Property<AttachAlertLayout, Float> ATTACH_ALERT_LAYOUT_TRANSLATION;
    private final Property<ChatAttachAlert, Float> ATTACH_ALERT_PROGRESS;
    public ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private final ImageView aiButton;
    private final AiButtonDrawable aiButtonIcon;
    private boolean allowDrawContent;
    public boolean allowEnterCaption;
    public boolean allowLivePhotos;
    protected boolean allowOrder;
    protected boolean allowPassConfirmationAlert;
    private final BoolAnimator animatorActionBarVisible;
    private final BoolAnimator animatorCaptionAbove;
    private final BoolAnimator animatorCaptionNotEmpty;
    private final BoolAnimator animatorCaptionVisible;
    private final ReplaceAnimator<Long> animatorCurrentVisibleLayout;
    private final BoolAnimator animatorEphemeralMessageVisibility;
    private final BoolAnimator animatorToggleCaptionSupported;
    private SpringAnimation appearSpringAnimation;
    private final Paint attachButtonPaint;
    private int attachItemSize;
    private ChatAttachAlertAudioLayout audioLayout;
    private ChatAttachAlertAudioLayout.AudioSelectDelegate audioSelectDelegate;
    protected int avatarPicker;
    protected boolean avatarSearch;
    protected Utilities.Callback0Return<PhotoViewer.PlaceProviderObject> avatarWithBulletin;
    public final BaseFragment baseFragment;
    private float baseSelectedTextViewTranslationY;
    private LongSparseArray<ChatAttachAlertBotWebViewLayout> botAttachLayouts;
    private boolean botButtonProgressWasVisible;
    private boolean botButtonWasVisible;
    private float botMainButtonOffsetY;
    private AnimatedTextView botMainButtonTextView;
    private RadialProgressView botProgressView;
    private BlurredBackgroundWithFadeDrawable bottomFadeDrawable;
    private View bottomFadeView;
    private float bottomPannelTranslation;
    private boolean buttonPressed;
    private ButtonsAdapter buttonsAdapter;
    private AnimatorSet buttonsAnimation;
    private LinearLayoutManager buttonsLayoutManager;
    protected RecyclerListView buttonsRecyclerView;
    protected FrameLayout buttonsRecyclerViewWrapper;
    private final FragmentFloatingButton cameraFloatingButton;
    public boolean canOpenPreview;
    public boolean captionAbove;
    private FrameLayout captionContainer;
    private BlurredBackgroundDrawable captionContainerBg;
    private float captionEditTextTopOffset;
    protected boolean captionLimitBulletinShown;
    private final AnimatedTextView captionLimitView;
    private float chatActivityEnterViewAnimateFromTop;
    private int codepointCount;
    public ChatAttachAlertColorsLayout colorsLayout;
    public EditTextEmoji commentTextView;
    private int[] commentTextViewLocation;
    private AnimatorSet commentsAnimator;
    private boolean confirmationAlertShown;
    private ChatAttachAlertContactsLayout contactsLayout;
    protected float cornerRadius;
    public final int currentAccount;
    private AttachAlertLayout currentAttachLayout;
    private int currentLimit;
    public float currentPanTranslationY;
    public Utilities.Callback2<String, TLRPC.InputDocument> customStickerHandler;
    private DecelerateInterpolator decelerateInterpolator;
    protected ChatAttachViewDelegate delegate;
    public boolean destroyed;
    public long dialogId;
    private ChatAttachAlertDocumentLayout documentLayout;
    private ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate documentsDelegate;
    private boolean documentsEnabled;
    protected TextView doneItem;
    private float doneItemAlphaByEnabled;
    private float doneItemAlphaByLayout;
    private int editType;
    protected MessageObject editingMessageObject;
    private long effectId;
    private ChatAttachAlertEmojiLayout emojiLayout;
    private BlurredBackgroundDrawable emojiViewChildBg;
    private EmojiView.EmojiViewDelegate emojiViewDelegate;
    private boolean enterCommentEventSent;
    private ArrayList<Rect> exclusionRects;
    private Rect exclustionRect;
    private ChatActivityFadeView fadeView;
    public boolean forUser;
    private final boolean forceDarkTheme;
    private FrameLayout frameLayout2;
    private float fromScrollY;
    protected FrameLayout headerView;
    private final IBlur3Capture iBlur3Capture;
    private final BlurredBackgroundDrawableViewFactory iBlur3FactoryFade;
    private final BlurredBackgroundDrawableViewFactory iBlur3FactoryFrostedLiquidGlass;
    private final BlurredBackgroundDrawableViewFactory iBlur3FactoryLiquidGlass;
    private final RectF iBlur3PositionActionBar;
    private final RectF iBlur3PositionFastScroll;
    private final RectF iBlur3PositionMainTabs;
    private final ArrayList<RectF> iBlur3Positions;
    private final ArrayList<RectF> iBlur3PositionsMerged;
    private final BlurredBackgroundSourceColor iBlur3SourceColor;
    private final BlurredBackgroundSourceRenderNode iBlur3SourceGlass;
    private final BlurredBackgroundSourceRenderNode iBlur3SourceGlassFrosted;
    public boolean inBubbleMode;
    public boolean isBizLocationPicker;
    public boolean isLocationPicker;
    public boolean isPhotoPicker;
    public boolean isPollAttach;
    private boolean isSoundPicker;
    public boolean isStickerMode;
    public boolean isStoryAudioPicker;
    public boolean isStoryLocationPicker;
    private AttachAlertLayout[] layouts;
    private ChatAttachAlertLocationLayout.LocationActivityDelegate locationActivityDelegate;
    private ChatAttachAlertLocationLayout locationLayout;
    protected int maxSelectedPhotos;
    protected TextView mediaPreviewTextView;
    protected LinearLayout mediaPreviewView;
    public MentionsContainerView mentionContainer;
    private MentionsContainerView.Delegate mentionsDelegate;
    private AnimatorSet menuAnimator;
    private boolean menuShowed;
    private MessageSendPreview messageSendPreview;
    private HintView2 motionHint;
    private MotionPhotoDrawable motionIcon;
    protected ActionBarMenuItem motionItem;
    public ImageView moveCaptionButton;
    private boolean musicEnabled;
    private AttachAlertLayout nextAttachLayout;
    private boolean openTransitionFinished;
    protected boolean openWithFrontFaceCamera;
    protected ActionBarMenuItem optionsItem;
    private boolean overrideBackgroundColor;
    private Paint paint;
    public ImageUpdater parentImageUpdater;
    public ChatActivity.ThemeDelegate parentThemeDelegate;
    private PasscodeView passcodeView;
    protected boolean paused;
    private ChatAttachAlertPhotoLayout photoLayout;
    private ChatAttachAlertPhotoLayoutPreview photoPreviewLayout;
    private boolean photosEnabled;
    public boolean pinnedToTop;
    private boolean plainTextEnabled;
    private int pollAllowedLayouts;
    private ChatAttachAlertPollLayout pollLayout;
    private boolean pollsEnabled;
    private int previousScrollOffsetY;
    private ChatAttachAlertQuickRepliesLayout quickRepliesLayout;
    private RectF rect;
    public boolean restrictEphemeralMessageTypes;
    private ChatAttachRestrictedLayout restrictedLayout;
    private ChatAttachAlertRichLayout richLayout;
    public int[] scrollOffsetY;
    private final DownscaleScrollableNoiseSuppressor scrollableViewNoiseSuppressor;
    protected ActionBarMenuItem searchItem;
    protected ImageView selectedArrowImageView;
    private View selectedCountView;
    private long selectedId;
    protected ActionBarMenuItem selectedMenuItem;
    protected TextView selectedTextView;
    protected LinearLayout selectedView;
    boolean sendButtonEnabled;
    private float sendButtonEnabledProgress;
    private ItemOptions sendButtonItemOptions;
    public boolean sent;
    private ImageUpdater.AvatarFor setAvatarFor;
    private final boolean showingFromDialog;
    private boolean shownAiButton;
    public SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private ChatAttachAlertEmojiLayout stickersLayout;
    public boolean storyLocationPickerFileIsVideo;
    public double[] storyLocationPickerLatLong;
    public File storyLocationPickerPhotoFile;
    public boolean storyMediaPicker;
    private TextPaint textPaint;
    private float toScrollY;
    private boolean todoEnabled;
    private ChatAttachAlertPollLayout todoLayout;
    private final ImageView topAiButton;
    private final AiButtonDrawable topAiButtonIcon;
    private ValueAnimator topBackgroundAnimator;
    private final AnimatedTextView topCaptionLimitView;
    public FrameLayout topCommentContainer;
    public ImageView topCommentMoveButton;
    public EditTextEmoji topCommentTextView;
    public float translationProgress;
    protected boolean typeButtonsAvailable;
    private boolean typeButtonsHidden;
    private boolean videosEnabled;
    private Object viewChangeAnimator;
    private ViewPositionWatcher viewPositionWatcher;
    private ChatActivityEnterView.SendButton writeButton;
    private FrameLayout writeButtonContainer;

    public static long val$id;
        final void $r8$lambda$PmxU41FRyzgAm9jTxK4Sg8RKKow(OverlayActionBarLayoutDialog overlayActionBarLayoutDialog, ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout, String str, PaymentFormActivity.InvoiceStatus invoiceStatus) {
            if (invoiceStatus != PaymentFormActivity.InvoiceStatus.PENDING) {
                overlayActionBarLayoutDialog.dismiss();
            }
            chatAttachAlertBotWebViewLayout.getWebViewContainer().onInvoiceStatusUpdate(str, invoiceStatus.name().toLowerCase(Locale.ROOT));
        }

        @Override // org.telegram.ui.web.BotWebViewContainer.Delegate
        public void onWebAppExpand() {
            AttachAlertLayout attachAlertLayout = ChatAttachAlert.this.currentAttachLayout;
            ChatAttachAlertBotWebViewLayout chatAttachAlertBotWebViewLayout = this.val$webViewLayout;
            if (attachAlertLayout == chatAttachAlertBotWebViewLayout && chatAttachAlertBotWebViewLayout.canExpandByRequest()) {
                this.val$webViewLayout.scrollToTop();
            }
        }

        @Override // org.telegram.ui.web.BotWebViewContainer.Delegate
        public void onWebAppSwitchInlineQuery(final TLRPC.User user, final String str, List<String> list) {
            if (list.isEmpty()) {
                BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
                if (baseFragment instanceof ChatActivity) {
                    ((ChatActivity) baseFragment).getChatActivityEnterView().setFieldText("@" + UserObject.getPublicUsername(user) + " " + str);
                }
                ChatAttachAlert.this.dismiss(true);
                return;
            }
            Bundle bundle = new Bundle();
            bundle.putInt("dialogsType", 14);
            bundle.putBoolean("onlySelect", true);
            bundle.putBoolean("allowGroups", list.contains("groups"));
            bundle.putBoolean("allowLegacyGroups", list.contains("groups"));
            bundle.putBoolean("allowMegagroups", list.contains("groups"));
            bundle.putBoolean("allowUsers", list.contains("users"));
            bundle.putBoolean("allowChannels", list.contains("channels"));
            bundle.putBoolean("allowBots", list.contains("bots"));
            DialogsActivity dialogsActivity = new DialogsActivity(bundle);
            final OverlayActionBarLayoutDialog overlayActionBarLayoutDialog = new OverlayActionBarLayoutDialog(ChatAttachAlert.this.getContext(), ((BottomSheet) ChatAttachAlert.this).resourcesProvider);
            dialogsActivity.setDelegate(new DialogsActivity.DialogsActivityDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$1$$ExternalSyntheticLambda0
                @Override // org.telegram.ui.DialogsActivity.DialogsActivityDelegate
                public final boolean didSelectDialogs(DialogsActivity dialogsActivity2, ArrayList arrayList, CharSequence charSequence, boolean z, boolean z2, int i, int i2, TopicsFragment topicsFragment) {
                    return this.f$0.lambda$onWebAppSwitchInlineQuery$5(user, str, overlayActionBarLayoutDialog, dialogsActivity2, arrayList, charSequence, z, z2, i, i2, topicsFragment);
                }
            });
            overlayActionBarLayoutDialog.show();
            overlayActionBarLayoutDialog.addFragment(dialogsActivity);
        }

        public void m9691$r8$lambda$Q4NR2NsLg9EoXirNGvADBmdOEU(ImageReceiver imageReceiver, boolean z, boolean z2, boolean z3) {
            Drawable drawable = imageReceiver.getDrawable();
            if (drawable instanceof RLottieDrawable) {
                RLottieDrawable rLottieDrawable = (RLottieDrawable) drawable;
                rLottieDrawable.setCustomEndFrame(0);
                rLottieDrawable.stop();
                rLottieDrawable.setProgress(0.0f, false);
            }
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            updateCheckedState(false);
        }

        public void updateCheckedState(boolean z) {
            boolean z2 = this.attachMenuBot != null && (-this.currentUser.id) == ChatAttachAlert.this.selectedId;
            this.glassTabView.setSelected(z2, z);
            RLottieDrawable lottieAnimation = this.glassTabView.getBackupImageView().getImageReceiver().getLottieAnimation();
            if (!z) {
                if (lottieAnimation != null) {
                    lottieAnimation.stop();
                    lottieAnimation.setProgress(0.0f, false);
                    return;
                }
                return;
            }
            if (!z2 || lottieAnimation == null) {
                return;
            }
            lottieAnimation.setAutoRepeat(0);
            lottieAnimation.setCustomEndFrame(-1);
            lottieAnimation.setProgress(0.0f, false);
            lottieAnimation.start();
        }

        public void setUser(TLRPC.User user) {
            if (user == null) {
                return;
            }
            this.glassTabView.setAttachBotUser(user, ChatAttachAlert.this.currentAccount);
            this.currentUser = user;
            this.attachMenuBot = null;
            this.glassTabView.setSelected(false, false);
            invalidate();
        }

        public void setAttachBot(TLRPC.User user, TLRPC.TL_attachMenuBot tL_attachMenuBot) {
            if (user == null || tL_attachMenuBot == null) {
                return;
            }
            this.glassTabView.setAttachBot(user, tL_attachMenuBot, ChatAttachAlert.this.currentAccount);
            this.currentUser = user;
            this.attachMenuBot = tL_attachMenuBot;
            this.glassTabView.setSelected(false, false);
            invalidate();
        }
    }

    public ChatAttachAlert(Context context, BaseFragment baseFragment, boolean z, boolean z2) {
        this(context, baseFragment, z, z2, true, null);
    }

    @SuppressLint({"ClickableViewAccessibility"})
    public ChatAttachAlert(final Context context, final BaseFragment baseFragment, boolean z, final boolean z2, boolean z3, final Theme.ResourcesProvider resourcesProvider) {
        TextView textView;
        int i;
        float f;
        int i2;
        super(context, false, resourcesProvider);
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.animatorCaptionAbove = new BoolAnimator(0, this, cubicBezierInterpolator, 380L);
        this.animatorCaptionVisible = new BoolAnimator(1, this, cubicBezierInterpolator, 380L);
        this.animatorActionBarVisible = new BoolAnimator(2, this, cubicBezierInterpolator, 380L);
        this.animatorCaptionNotEmpty = new BoolAnimator(3, this, cubicBezierInterpolator, 380L);
        this.animatorToggleCaptionSupported = new BoolAnimator(4, this, cubicBezierInterpolator, 380L, true);
        this.animatorEphemeralMessageVisibility = new BoolAnimator(5, this, cubicBezierInterpolator, 320L);
        ReplaceAnimator<Long> replaceAnimator = new ReplaceAnimator<>(new ReplaceAnimator.Callback() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda18
            @Override // me.vkryl.android.animator.ReplaceAnimator.Callback
            public final void onItemChanged(ReplaceAnimator replaceAnimator2) {
                this.f$0.onCurrentLayoutAnimatorChanged(replaceAnimator2);
            }
        }, cubicBezierInterpolator, 380L);
        this.animatorCurrentVisibleLayout = replaceAnimator;
        this.canOpenPreview = false;
        this.isSoundPicker = false;
        this.isStoryLocationPicker = false;
        this.isBizLocationPicker = false;
        this.isLocationPicker = false;
        this.isStoryAudioPicker = false;
        this.translationProgress = 0.0f;
        this.ATTACH_ALERT_LAYOUT_TRANSLATION = new AnimationProperties.FloatProperty<AttachAlertLayout>("translation") { // from class: org.telegram.ui.Components.ChatAttachAlert.2
            @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
            public void setValue(AttachAlertLayout attachAlertLayout, float f2) {
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                chatAttachAlert.translationProgress = f2;
                if (chatAttachAlert.nextAttachLayout == null) {
                    return;
                }
                if ((ChatAttachAlert.this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview) || (ChatAttachAlert.this.currentAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview)) {
                    int iMax = Math.max(ChatAttachAlert.this.nextAttachLayout.getWidth(), ChatAttachAlert.this.currentAttachLayout.getWidth());
                    boolean z4 = ChatAttachAlert.this.nextAttachLayout instanceof ChatAttachAlertPhotoLayoutPreview;
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                    if (z4) {
                        chatAttachAlert2.currentAttachLayout.setTranslationX((-iMax) * f2);
                        ChatAttachAlert.this.nextAttachLayout.setTranslationX((1.0f - f2) * iMax);
                    } else {
                        chatAttachAlert2.currentAttachLayout.setTranslationX(iMax * f2);
                        ChatAttachAlert.this.nextAttachLayout.setTranslationX((-iMax) * (1.0f - f2));
                    }
                } else {
                    ChatAttachAlert.this.nextAttachLayout.setAlpha(f2);
                    ChatAttachAlert.this.nextAttachLayout.onHideShowProgress(f2);
                    if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.pollLayout || ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.pollLayout) {
                        ChatAttachAlert chatAttachAlert3 = ChatAttachAlert.this;
                        chatAttachAlert3.updateSelectedPosition(chatAttachAlert3.nextAttachLayout == ChatAttachAlert.this.pollLayout ? 1 : 0);
                    }
                    if (ChatAttachAlert.this.nextAttachLayout == ChatAttachAlert.this.todoLayout || ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.todoLayout) {
                        ChatAttachAlert chatAttachAlert4 = ChatAttachAlert.this;
                        chatAttachAlert4.updateSelectedPosition(chatAttachAlert4.nextAttachLayout == ChatAttachAlert.this.todoLayout ? 1 : 0);
                    }
                    ChatAttachAlert.this.nextAttachLayout.setTranslationY(AndroidUtilities.dp(78.0f) * f2);
                    ChatAttachAlert.this.currentAttachLayout.onHideShowProgress(1.0f - Math.min(1.0f, f2 / 0.7f));
                    ChatAttachAlert.this.currentAttachLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
                }
                if (ChatAttachAlert.this.viewChangeAnimator != null) {
                    ChatAttachAlert.this.updateSelectedPosition(1);
                }
                ChatAttachAlert.this.blur3_InvalidateBlur();
                ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
            }

            @Override // android.util.Property
            public Float get(AttachAlertLayout attachAlertLayout) {
                return Float.valueOf(ChatAttachAlert.this.translationProgress);
            }
        };
        this.allowLivePhotos = false;
        this.layouts = new AttachAlertLayout[11];
        this.botAttachLayouts = new LongSparseArray<>();
        this.commentTextViewLocation = new int[2];
        this.textPaint = new TextPaint(1);
        this.rect = new RectF();
        this.paint = new Paint(1);
        this.sendButtonEnabled = true;
        this.sendButtonEnabledProgress = 1.0f;
        this.cornerRadius = 1.0f;
        this.botButtonProgressWasVisible = false;
        this.botButtonWasVisible = false;
        int i3 = UserConfig.selectedAccount;
        this.currentAccount = i3;
        this.documentsEnabled = true;
        this.photosEnabled = true;
        this.videosEnabled = true;
        this.musicEnabled = true;
        this.pollsEnabled = true;
        this.todoEnabled = true;
        this.plainTextEnabled = true;
        this.maxSelectedPhotos = -1;
        this.allowOrder = true;
        this.attachItemSize = AndroidUtilities.dp(85.0f);
        this.decelerateInterpolator = new DecelerateInterpolator();
        this.scrollOffsetY = new int[2];
        this.attachButtonPaint = new Paint(1);
        this.captionLimitBulletinShown = false;
        this.exclusionRects = new ArrayList<>();
        this.exclustionRect = new Rect();
        this.ATTACH_ALERT_PROGRESS = new AnimationProperties.FloatProperty<ChatAttachAlert>("openProgress") { // from class: org.telegram.ui.Components.ChatAttachAlert.33
            private float openProgress;

            @Override // org.telegram.ui.Components.AnimationProperties.FloatProperty
            public void setValue(ChatAttachAlert chatAttachAlert, float f2) {
                float interpolation;
                int childCount = ChatAttachAlert.this.buttonsRecyclerView.getChildCount();
                for (int i4 = 0; i4 < childCount; i4++) {
                    float f3 = (3 - i4) * 32.0f;
                    View childAt = ChatAttachAlert.this.buttonsRecyclerView.getChildAt(i4);
                    if (f2 > f3) {
                        float f4 = f2 - f3;
                        if (f4 <= 200.0f) {
                            float f5 = f4 / 200.0f;
                            interpolation = CubicBezierInterpolator.EASE_OUT.getInterpolation(f5) * 1.1f;
                            childAt.setAlpha(CubicBezierInterpolator.EASE_BOTH.getInterpolation(f5));
                        } else {
                            childAt.setAlpha(1.0f);
                            float f6 = f4 - 200.0f;
                            interpolation = f6 <= 100.0f ? 1.1f - (CubicBezierInterpolator.EASE_IN.getInterpolation(f6 / 100.0f) * 0.1f) : 1.0f;
                        }
                    } else {
                        interpolation = 0.0f;
                    }
                    if (childAt instanceof AttachButtonBase) {
                        ((AttachButtonBase) childAt).glassTabView.setAttachScale(interpolation);
                    }
                }
            }

            @Override // android.util.Property
            public Float get(ChatAttachAlert chatAttachAlert) {
                return Float.valueOf(this.openProgress);
            }
        };
        this.allowDrawContent = true;
        this.sent = false;
        this.confirmationAlertShown = false;
        this.allowPassConfirmationAlert = false;
        ArrayList<RectF> arrayList = new ArrayList<>();
        this.iBlur3Positions = arrayList;
        RectF rectF = new RectF();
        this.iBlur3PositionActionBar = rectF;
        RectF rectF2 = new RectF();
        this.iBlur3PositionMainTabs = rectF2;
        RectF rectF3 = new RectF();
        this.iBlur3PositionFastScroll = rectF3;
        arrayList.add(rectF);
        arrayList.add(rectF2);
        arrayList.add(rectF3);
        this.iBlur3PositionsMerged = new ArrayList<>();
        this.occupyNavigationBarWithoutKeyboard = true;
        BlurredBackgroundSourceColor blurredBackgroundSourceColor = new BlurredBackgroundSourceColor();
        this.iBlur3SourceColor = blurredBackgroundSourceColor;
        blurredBackgroundSourceColor.setColor(getThemedColor(Theme.key_windowBackgroundWhite));
        if (Build.VERSION.SDK_INT >= 31) {
            this.scrollableViewNoiseSuppressor = new DownscaleScrollableNoiseSuppressor();
            BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode = new BlurredBackgroundSourceRenderNode(null);
            this.iBlur3SourceGlass = blurredBackgroundSourceRenderNode;
            blurredBackgroundSourceRenderNode.setupRenderer(new RenderNodeWithHash.Renderer() { // from class: org.telegram.ui.Components.ChatAttachAlert.3
                @Override // org.telegram.ui.Components.blur3.RenderNodeWithHash.Renderer
                public void renderNodeCalculateHash(IBlur3Hash iBlur3Hash) {
                    iBlur3Hash.add(ChatAttachAlert.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    iBlur3Hash.add(SharedConfig.chatBlurEnabled());
                }

                @Override // org.telegram.ui.Components.blur3.RenderNodeWithHash.Renderer
                public void renderNodeUpdateDisplayList(Canvas canvas) {
                    canvas.drawColor(ChatAttachAlert.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    if (SharedConfig.chatBlurEnabled()) {
                        ChatAttachAlert.this.scrollableViewNoiseSuppressor.draw(canvas, -2);
                    }
                }
            });
            BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode2 = new BlurredBackgroundSourceRenderNode(null);
            this.iBlur3SourceGlassFrosted = blurredBackgroundSourceRenderNode2;
            blurredBackgroundSourceRenderNode2.setupRenderer(new RenderNodeWithHash.Renderer() { // from class: org.telegram.ui.Components.ChatAttachAlert.4
                @Override // org.telegram.ui.Components.blur3.RenderNodeWithHash.Renderer
                public void renderNodeCalculateHash(IBlur3Hash iBlur3Hash) {
                    iBlur3Hash.add(ChatAttachAlert.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    iBlur3Hash.add(SharedConfig.chatBlurEnabled());
                }

                @Override // org.telegram.ui.Components.blur3.RenderNodeWithHash.Renderer
                public void renderNodeUpdateDisplayList(Canvas canvas) {
                    canvas.drawColor(ChatAttachAlert.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    if (SharedConfig.chatBlurEnabled()) {
                        ChatAttachAlert.this.scrollableViewNoiseSuppressor.draw(canvas, -3);
                    }
                }
            });
            BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSourceRenderNode);
            this.iBlur3FactoryLiquidGlass = blurredBackgroundDrawableViewFactory;
            blurredBackgroundDrawableViewFactory.setLiquidGlassEffectAllowed(LiteMode.isEnabled(262144));
            BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory2 = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSourceRenderNode2);
            this.iBlur3FactoryFrostedLiquidGlass = blurredBackgroundDrawableViewFactory2;
            blurredBackgroundDrawableViewFactory2.setLiquidGlassEffectAllowed(LiteMode.isEnabled(262144));
        } else {
            this.scrollableViewNoiseSuppressor = null;
            this.iBlur3SourceGlassFrosted = null;
            this.iBlur3SourceGlass = null;
            this.iBlur3FactoryLiquidGlass = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSourceColor);
            this.iBlur3FactoryFrostedLiquidGlass = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSourceColor);
        }
        BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory3 = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSourceColor);
        this.iBlur3FactoryFade = blurredBackgroundDrawableViewFactory3;
        this.iBlur3Capture = new IBlur3Capture() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda29
            @Override // org.telegram.ui.Components.blur3.capture.IBlur3Capture
            public final void capture(Canvas canvas, RectF rectF4) {
                this.f$0.lambda$new$0(canvas, rectF4);
            }
        };
        this.forceDarkTheme = z;
        this.showingFromDialog = z2;
        this.inBubbleMode = (baseFragment instanceof ChatActivity) && baseFragment.isInBubbleMode();
        this.openInterpolator = new OvershootInterpolator(0.7f);
        this.baseFragment = baseFragment;
        this.useSmoothKeyboard = true;
        setDelegate(this);
        NotificationCenter.getInstance(i3).addObserver(this, NotificationCenter.reloadInlineHints);
        NotificationCenter.getInstance(i3).addObserver(this, NotificationCenter.attachMenuBotsDidLoad);
        NotificationCenter.getInstance(i3).addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        NotificationCenter.getInstance(i3).addObserver(this, NotificationCenter.quickRepliesUpdated);
        this.exclusionRects.add(this.exclustionRect);
        AnonymousClass5 anonymousClass5 = new AnonymousClass5(context);
        this.sizeNotifierFrameLayout = anonymousClass5;
        anonymousClass5.setDelegate(new SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert.6
            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate
            public void onSizeChanged(int i4, boolean z4) {
                if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoPreviewLayout) {
                    ChatAttachAlert.this.currentAttachLayout.invalidate();
                }
            }
        });
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.sizeNotifierFrameLayout;
        this.containerView = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setWillNotDraw(false);
        this.containerView.setClipChildren(false);
        this.containerView.setClipToPadding(false);
        ViewGroup viewGroup = this.containerView;
        int i4 = this.backgroundPaddingLeft;
        viewGroup.setPadding(i4, 0, i4, 0);
        ActionBar actionBar = new ActionBar(context, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlert.7
            @Override // android.view.View
            public void setVisibility(int i5) {
                super.setVisibility(i5);
                ChatAttachAlert.this.checkUi_fadeTopAlpha();
            }

            @Override // android.view.View
            public void setAlpha(float f2) {
                float alpha = getAlpha();
                super.setAlpha(f2);
                if (alpha != f2) {
                    TextView textView2 = ChatAttachAlert.this.selectedTextView;
                    if (textView2 != null) {
                        float f3 = 1.0f - f2;
                        textView2.setAlpha(f3);
                        ChatAttachAlert.this.selectedTextView.setVisibility(f3 > 0.0f ? 0 : 8);
                    }
                    ChatAttachAlert.this.checkUi_fadeTopAlpha();
                    ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
                    if (ChatAttachAlert.this.frameLayout2 != null) {
                        ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                        if (chatAttachAlert.buttonsRecyclerViewWrapper != null) {
                            Object tag = chatAttachAlert.frameLayout2.getTag();
                            ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                            if (tag == null) {
                                if (chatAttachAlert2.currentAttachLayout == null || ChatAttachAlert.this.currentAttachLayout.shouldHideBottomButtons()) {
                                    ChatAttachAlert.this.buttonsRecyclerViewWrapper.setAlpha(1.0f - f2);
                                    ChatAttachAlert.this.buttonsRecyclerViewWrapper.setTranslationY(AndroidUtilities.dp(44.0f) * f2);
                                }
                                ChatAttachAlert.this.frameLayout2.setTranslationY(AndroidUtilities.dp(48.0f) * f2);
                                return;
                            }
                            if (chatAttachAlert2.currentAttachLayout == null) {
                                float f4 = f2 == 0.0f ? 1.0f : 0.0f;
                                if (ChatAttachAlert.this.buttonsRecyclerViewWrapper.getAlpha() != f4) {
                                    ChatAttachAlert.this.buttonsRecyclerViewWrapper.setAlpha(f4);
                                }
                            }
                        }
                    }
                }
            }
        };
        this.actionBar = actionBar;
        actionBar.setForcedMenuWidth(AndroidUtilities.dp(46.0f));
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        ActionBar actionBar2 = this.actionBar;
        int i5 = Theme.key_dialogTextBlack;
        actionBar2.setItemsColor(getThemedColor(i5), false);
        ActionBar actionBar3 = this.actionBar;
        int i6 = Theme.key_dialogButtonSelector;
        actionBar3.setItemsBackgroundColor(getThemedColor(i6), false);
        this.actionBar.setTitleColor(getThemedColor(i5));
        this.actionBar.setOccupyStatusBar(true);
        this.actionBar.setAlpha(0.0f);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: org.telegram.ui.Components.ChatAttachAlert.8
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i7) {
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                if (i7 == -1) {
                    if (chatAttachAlert.currentAttachLayout.onBackPressed()) {
                        return;
                    }
                    ChatAttachAlert.this.lambda$new$0();
                    return;
                }
                chatAttachAlert.currentAttachLayout.onMenuItemClick(i7);
            }
        });
        ActionBarMenuItem actionBarMenuItem = new ActionBarMenuItem(context, null, 0, getThemedColor(i5), false, resourcesProvider);
        this.selectedMenuItem = actionBarMenuItem;
        actionBarMenuItem.setLongClickEnabled(false);
        this.selectedMenuItem.setIcon(R.drawable.ic_ab_other);
        this.selectedMenuItem.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
        this.selectedMenuItem.setVisibility(4);
        this.selectedMenuItem.setAlpha(0.0f);
        this.selectedMenuItem.setScaleX(0.6f);
        this.selectedMenuItem.setScaleY(0.6f);
        this.selectedMenuItem.setSubMenuOpenSide(2);
        this.selectedMenuItem.setDelegate(new ActionBarMenuItem.ActionBarMenuItemDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda33
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemDelegate
            public final void onItemClick(int i7) {
                this.f$0.lambda$new$1(i7);
            }
        });
        this.selectedMenuItem.setAdditionalYOffset(AndroidUtilities.dp(72.0f));
        this.selectedMenuItem.setTranslationX(AndroidUtilities.dp(1.0f));
        this.selectedMenuItem.setBackground(Theme.createSelectorDrawable(getThemedColor(i6), 6));
        this.selectedMenuItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda34
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$2(view);
            }
        });
        ActionBarMenuItem actionBarMenuItem2 = new ActionBarMenuItem(context, null, 0, getThemedColor(i5), false, resourcesProvider);
        this.motionItem = actionBarMenuItem2;
        actionBarMenuItem2.setLongClickEnabled(false);
        ActionBarMenuItem actionBarMenuItem3 = this.motionItem;
        MotionPhotoDrawable motionPhotoDrawable = new MotionPhotoDrawable();
        this.motionIcon = motionPhotoDrawable;
        actionBarMenuItem3.setIcon(motionPhotoDrawable);
        this.motionItem.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
        this.motionItem.setVisibility(8);
        this.motionItem.setAlpha(0.0f);
        this.motionItem.setScaleX(0.6f);
        this.motionItem.setScaleY(0.6f);
        this.motionItem.setAdditionalYOffset(AndroidUtilities.dp(72.0f));
        this.motionItem.setTranslationX(-AndroidUtilities.dp(3.0f));
        this.motionItem.setBackground(Theme.createSelectorDrawable(getThemedColor(i6), 6));
        this.motionItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda35
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$3(view);
            }
        });
        TextView textView2 = new TextView(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.9
            Paint p = new Paint(1);

            @Override // android.widget.TextView, android.view.View
            public void onDraw(Canvas canvas) {
                this.p.setColor(ChatAttachAlert.this.getThemedColor(Theme.key_featuredStickers_addButton));
                canvas.drawRoundRect(0.0f, (getHeight() / 2.0f) - AndroidUtilities.dp(14.0f), getWidth(), (getHeight() / 2.0f) + AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(14.0f), this.p);
                super.onDraw(canvas);
            }
        };
        textView2.setTextColor(getThemedColor(Theme.key_featuredStickers_buttonText));
        textView2.setText(LocaleController.getString(R.string.Create));
        textView2.setTypeface(AndroidUtilities.bold());
        textView2.setTextSize(1, 14.0f);
        textView2.setVisibility(4);
        textView2.setAlpha(0.0f);
        textView2.setGravity(17);
        textView2.setPadding(AndroidUtilities.dp(12.0f), 0, AndroidUtilities.dp(12.0f), 0);
        textView2.setTranslationX(-AndroidUtilities.dp(12.0f));
        textView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda36
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$4(view);
            }
        });
        ScaleStateListAnimator.apply(textView2);
        this.doneItem = textView2;
        updateDoneItemEnabled();
        if (baseFragment != null) {
            f = 14.0f;
            textView = textView2;
            i = i6;
            ActionBarMenuItem actionBarMenuItem4 = new ActionBarMenuItem(context, null, 0, getThemedColor(i5), false, resourcesProvider);
            this.searchItem = actionBarMenuItem4;
            actionBarMenuItem4.setLongClickEnabled(false);
            this.searchItem.setIcon(R.drawable.outline_header_search);
            this.searchItem.setContentDescription(LocaleController.getString(R.string.Search));
            i2 = 4;
            this.searchItem.setVisibility(4);
            this.searchItem.setAlpha(0.0f);
            this.searchItem.setTranslationX(-AndroidUtilities.dp(42.0f));
            this.searchItem.setBackgroundDrawable(Theme.createSelectorDrawable(getThemedColor(i), 6));
            this.searchItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda37
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$new$5(z2, view);
                }
            });
        } else {
            textView = textView2;
            i = i6;
            f = 14.0f;
            i2 = 4;
        }
        ActionBarMenuItem actionBarMenuItem5 = new ActionBarMenuItem(context, null, 0, getThemedColor(i5), false, resourcesProvider);
        this.optionsItem = actionBarMenuItem5;
        actionBarMenuItem5.setLongClickEnabled(false);
        this.optionsItem.setIcon(R.drawable.ic_ab_other);
        this.optionsItem.setContentDescription(LocaleController.getString(R.string.AccDescrMoreOptions));
        this.optionsItem.setVisibility(8);
        this.optionsItem.setBackground(Theme.createSelectorDrawable(getThemedColor(i), 3));
        this.optionsItem.addSubItem(1, R.drawable.msg_addbot, LocaleController.getString(R.string.StickerCreateEmpty)).setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda38
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$6(resourcesProvider, view);
            }
        });
        this.optionsItem.setMenuYOffset(AndroidUtilities.dp(-12.0f));
        this.optionsItem.setAdditionalXOffset(AndroidUtilities.dp(12.0f));
        this.optionsItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda39
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$7(view);
            }
        });
        FrameLayout frameLayout = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.12
            @Override // android.view.View
            public void setAlpha(float f2) {
                super.setAlpha(f2);
                ChatAttachAlert.this.updateSelectedPosition(0);
                ((BottomSheet) ChatAttachAlert.this).containerView.invalidate();
            }

            @Override // android.view.View
            public boolean onTouchEvent(MotionEvent motionEvent) {
                if (ChatAttachAlert.this.headerView.getVisibility() != 0) {
                    return false;
                }
                return super.onTouchEvent(motionEvent);
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (ChatAttachAlert.this.headerView.getVisibility() != 0) {
                    return false;
                }
                return super.onInterceptTouchEvent(motionEvent);
            }
        };
        this.headerView = frameLayout;
        frameLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda40
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$8(view);
            }
        });
        this.headerView.setAlpha(0.0f);
        this.headerView.setVisibility(i2);
        LinearLayout linearLayout = new LinearLayout(context);
        this.selectedView = linearLayout;
        linearLayout.setOrientation(0);
        this.selectedView.setGravity(16);
        TextView textView3 = new TextView(context);
        this.selectedTextView = textView3;
        textView3.setTextColor(getThemedColor(i5));
        this.selectedTextView.setTextSize(1, 16.0f);
        this.selectedTextView.setTypeface(AndroidUtilities.bold());
        this.selectedTextView.setGravity(19);
        this.selectedTextView.setMaxLines(1);
        this.selectedTextView.setEllipsize(TextUtils.TruncateAt.END);
        this.selectedView.addView(this.selectedTextView, LayoutHelper.createLinear(-2, -2, 16));
        this.selectedArrowImageView = new ImageView(context);
        Drawable drawableMutate = getContext().getResources().getDrawable(R.drawable.attach_arrow_right).mutate();
        int themedColor = getThemedColor(i5);
        PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
        drawableMutate.setColorFilter(new PorterDuffColorFilter(themedColor, mode));
        this.selectedArrowImageView.setImageDrawable(drawableMutate);
        this.selectedArrowImageView.setVisibility(8);
        this.selectedView.addView(this.selectedArrowImageView, LayoutHelper.createLinear(-2, -2, 16, 4, 1, 0, 0));
        this.selectedView.setAlpha(1.0f);
        this.headerView.addView(this.selectedView, LayoutHelper.createFrame(-2, -1.0f));
        LinearLayout linearLayout2 = new LinearLayout(context);
        this.mediaPreviewView = linearLayout2;
        linearLayout2.setOrientation(0);
        this.mediaPreviewView.setGravity(16);
        ImageView imageView = new ImageView(context);
        Drawable drawableMutate2 = getContext().getResources().getDrawable(R.drawable.attach_arrow_left).mutate();
        drawableMutate2.setColorFilter(new PorterDuffColorFilter(getThemedColor(i5), mode));
        imageView.setImageDrawable(drawableMutate2);
        this.mediaPreviewView.addView(imageView, LayoutHelper.createLinear(-2, -2, 16, 0, 1, 4, 0));
        TextView textView4 = new TextView(context);
        this.mediaPreviewTextView = textView4;
        textView4.setTextColor(getThemedColor(i5));
        this.mediaPreviewTextView.setTextSize(1, 16.0f);
        this.mediaPreviewTextView.setTypeface(AndroidUtilities.bold());
        this.mediaPreviewTextView.setGravity(19);
        this.mediaPreviewTextView.setText(LocaleController.getString("AttachMediaPreview", R.string.AttachMediaPreview));
        this.mediaPreviewView.setAlpha(0.0f);
        this.mediaPreviewView.addView(this.mediaPreviewTextView, LayoutHelper.createLinear(-2, -2, 16));
        this.headerView.addView(this.mediaPreviewView, LayoutHelper.createFrame(-2, -1.0f));
        AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = new ChatAttachAlertPhotoLayout(this, context, z, z3, resourcesProvider);
        this.photoLayout = chatAttachAlertPhotoLayout;
        attachAlertLayoutArr[0] = chatAttachAlertPhotoLayout;
        chatAttachAlertPhotoLayout.setTranslationX(0.0f);
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2 = this.photoLayout;
        this.currentAttachLayout = chatAttachAlertPhotoLayout2;
        this.selectedId = 1L;
        this.containerView.addView(chatAttachAlertPhotoLayout2, LayoutHelper.createFrame(-1, -1.0f));
        ChatActivityFadeView chatActivityFadeView = new ChatActivityFadeView(context);
        this.fadeView = chatActivityFadeView;
        chatActivityFadeView.setup(blurredBackgroundDrawableViewFactory3);
        this.fadeView.setFadeTopAlpha(0);
        this.fadeView.setFadeHeightTop(AndroidUtilities.dp(48.0f));
        this.fadeView.setFadeHeightBottom(AndroidUtilities.dp(48.0f));
        this.fadeView.setFadeZoneTop(AndroidUtilities.statusBarHeight + ActionBar.getCurrentActionBarHeight() + AndroidUtilities.dp(5.0f));
        this.containerView.addView(this.fadeView, LayoutHelper.createFrameMatchParent());
        this.containerView.addView(this.headerView, LayoutHelper.createFrame(-1, -2.0f, 51, 23.0f, 0.0f, 21.0f, 0.0f));
        FrameLayout frameLayout2 = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.13
            private final Path path = new Path();
            private final GradientClip clip = new GradientClip();

            @Override // android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                float fDp = AndroidUtilities.dp(20.0f);
                RectF rectF4 = AndroidUtilities.rectTmp;
                rectF4.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
                this.path.rewind();
                this.path.addRoundRect(rectF4, fDp, fDp, Path.Direction.CW);
                canvas.save();
                canvas.clipRect(0.0f, 0.0f, getWidth(), getHeight() * getAlpha());
                canvas.clipPath(this.path);
                canvas.saveLayerAlpha(rectF4, 255, 31);
                super.dispatchDraw(canvas);
                rectF4.set(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getPaddingTop() + AndroidUtilities.dp(6.0f));
                this.clip.draw(canvas, rectF4, 1, 1.0f);
                rectF4.set(getPaddingLeft(), (getHeight() - getPaddingBottom()) - AndroidUtilities.dp(6.0f), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
                this.clip.draw(canvas, rectF4, 3, 1.0f);
                canvas.restore();
                canvas.restore();
            }
        };
        this.topCommentContainer = frameLayout2;
        this.containerView.addView(frameLayout2, LayoutHelper.createFrame(-1, -2, 55));
        this.containerView.addView(this.actionBar, LayoutHelper.createFrame(-1, -2.0f));
        this.containerView.addView(this.selectedMenuItem, LayoutHelper.createFrame(48, 48, 53));
        this.containerView.addView(this.motionItem, LayoutHelper.createFrame(48, 48.0f, 53, 0.0f, 0.0f, 48.0f, 0.0f));
        ActionBarMenuItem actionBarMenuItem6 = this.searchItem;
        if (actionBarMenuItem6 != null) {
            this.containerView.addView(actionBarMenuItem6, LayoutHelper.createFrame(48, 48, 53));
        }
        ActionBarMenuItem actionBarMenuItem7 = this.optionsItem;
        if (actionBarMenuItem7 != null) {
            this.headerView.addView(actionBarMenuItem7, LayoutHelper.createFrame(32, 32.0f, 21, 0.0f, 0.0f, 0.0f, 8.0f));
        }
        this.containerView.addView(textView, LayoutHelper.createFrame(-2, 48, 53));
        this.buttonsRecyclerViewWrapper = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.14
            @Override // android.widget.FrameLayout, android.view.View
            public void onMeasure(int i7, int i8) {
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                if (chatAttachAlert.isPollAttach && chatAttachAlert.pollAllowedLayouts != 0) {
                    super.onMeasure(View.MeasureSpec.makeMeasureSpec(Math.min(View.MeasureSpec.getSize(i7), (Integer.bitCount(ChatAttachAlert.this.pollAllowedLayouts) * AndroidUtilities.dp(80.0f)) + AndroidUtilities.dp(36.0f)), TLObject.FLAG_30), i8);
                } else {
                    super.onMeasure(i7, i8);
                }
            }

            @Override // android.view.View
            public void setTranslationY(float f2) {
                super.setTranslationY(f2);
                ChatAttachAlert.this.currentAttachLayout.onButtonsTranslationYUpdated();
                ChatAttachAlert.this.updateCameraButtonVisibility();
            }

            @Override // android.view.View
            public void setAlpha(float f2) {
                super.setAlpha(f2);
                ChatAttachAlert.this.updateCameraButtonVisibility();
            }
        };
        RecyclerListView recyclerListView = new RecyclerListView(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.15
            private final BoolAnimator hasFadeLeft;
            private final BoolAnimator hasFadeRight;
            private boolean mHasFadeLeft;
            private boolean mHasFadeRight;
            private final Paint paintLeft;
            private final Paint paintRight;
            private final Shader shaderLeft;
            private final Shader shaderRight;

            {
                CubicBezierInterpolator cubicBezierInterpolator2 = CubicBezierInterpolator.EASE_OUT_QUINT;
                this.hasFadeLeft = new BoolAnimator(this, cubicBezierInterpolator2, 320L);
                this.hasFadeRight = new BoolAnimator(this, cubicBezierInterpolator2, 320L);
                Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(8.0f), 0.0f, new int[]{0, -16777216}, (float[]) null, tileMode);
                this.shaderLeft = linearGradient;
                LinearGradient linearGradient2 = new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(8.0f), 0.0f, new int[]{-16777216, 0}, (float[]) null, tileMode);
                this.shaderRight = linearGradient2;
                Paint paint = new Paint(1);
                this.paintLeft = paint;
                Paint paint2 = new Paint(1);
                this.paintRight = paint2;
                paint.setShader(linearGradient);
                PorterDuff.Mode mode2 = PorterDuff.Mode.DST_IN;
                paint.setXfermode(new PorterDuffXfermode(mode2));
                paint2.setShader(linearGradient2);
                paint2.setXfermode(new PorterDuffXfermode(mode2));
            }

            @Override // org.telegram.ui.Components.RecyclerListView, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                this.mHasFadeRight = false;
                this.mHasFadeLeft = false;
                super.dispatchDraw(canvas);
                this.hasFadeLeft.setValue(this.mHasFadeLeft, true);
                this.hasFadeRight.setValue(this.mHasFadeRight, true);
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View view, long j) {
                float x = view.getX();
                float width = view.getWidth() + x;
                boolean z4 = true;
                boolean z5 = x < ((float) AndroidUtilities.dp(10.0f));
                boolean z6 = width > ((float) (getMeasuredWidth() - AndroidUtilities.dp(10.0f)));
                if (!z5 && !z6) {
                    z4 = false;
                }
                this.mHasFadeLeft |= z5;
                this.mHasFadeRight |= z6;
                canvas.save();
                if (z4) {
                    canvas.clipRect(AndroidUtilities.dp(19.0f), 0, getMeasuredWidth() - AndroidUtilities.dp(19.0f), getMeasuredHeight());
                }
                boolean zDrawChild = super.drawChild(canvas, view, j);
                canvas.restore();
                if (z5) {
                    float fDp = AndroidUtilities.dp(11.0f);
                    canvas.saveLayer(fDp, getPaddingTop(), AndroidUtilities.dp(19.0f), getMeasuredHeight() - getPaddingBottom(), null);
                    super.drawChild(canvas, view, j);
                    canvas.save();
                    canvas.translate(fDp - (AndroidUtilities.dp(8.0f) * (1.0f - this.hasFadeLeft.getFloatValue())), 0.0f);
                    canvas.drawPaint(this.paintLeft);
                    canvas.restore();
                    canvas.restore();
                }
                if (z6) {
                    float measuredWidth = getMeasuredWidth() - AndroidUtilities.dp(19.0f);
                    canvas.saveLayer(measuredWidth, getPaddingTop(), getMeasuredWidth() - AndroidUtilities.dp(11.0f), getMeasuredHeight() - getPaddingBottom(), null);
                    super.drawChild(canvas, view, j);
                    canvas.save();
                    canvas.translate(measuredWidth + (AndroidUtilities.dp(8.0f) * (1.0f - this.hasFadeRight.getFloatValue())), 0.0f);
                    canvas.drawPaint(this.paintRight);
                    canvas.restore();
                    canvas.restore();
                }
                return zDrawChild;
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public void onMeasure(int i7, int i8) {
                int childCount = getChildCount();
                int size = (View.MeasureSpec.getSize(i7) - getPaddingLeft()) - getPaddingRight();
                float fMeasureAttachTabWidth = 0.0f;
                for (int i9 = 0; i9 < childCount; i9++) {
                    View childAt = getChildAt(i9);
                    if (childAt instanceof AttachButtonBase) {
                        fMeasureAttachTabWidth += ((AttachButtonBase) childAt).glassTabView.measureAttachTabWidth();
                    }
                }
                float f2 = size;
                int iFloor = (f2 <= fMeasureAttachTabWidth || childCount <= 0) ? 0 : (int) Math.floor((f2 - fMeasureAttachTabWidth) / childCount);
                for (int i10 = 0; i10 < childCount; i10++) {
                    View childAt2 = getChildAt(i10);
                    if (childAt2 instanceof AttachButtonBase) {
                        ((AttachButtonBase) childAt2).glassTabView.setAdditionalWidth(iFloor);
                    }
                }
                super.onMeasure(i7, i8);
            }
        };
        this.buttonsRecyclerView = recyclerListView;
        recyclerListView.setClipChildren(true);
        this.buttonsRecyclerView.setClipToPadding(false);
        RecyclerListView recyclerListView2 = this.buttonsRecyclerView;
        ButtonsAdapter buttonsAdapter = new ButtonsAdapter(context);
        this.buttonsAdapter = buttonsAdapter;
        recyclerListView2.setAdapter(buttonsAdapter);
        RecyclerListView recyclerListView3 = this.buttonsRecyclerView;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, 0, false);
        this.buttonsLayoutManager = linearLayoutManager;
        recyclerListView3.setLayoutManager(linearLayoutManager);
        this.buttonsRecyclerView.setVerticalScrollBarEnabled(false);
        this.buttonsRecyclerView.setHorizontalScrollBarEnabled(false);
        this.buttonsRecyclerView.setItemAnimator(null);
        this.buttonsRecyclerView.setLayoutAnimation(null);
        this.buttonsRecyclerView.setGlowColor(getThemedColor(Theme.key_dialogScrollGlow));
        this.buttonsRecyclerView.setAdaptiveOverScroll();
        ViewPositionWatcher viewPositionWatcher = this.viewPositionWatcher;
        if (viewPositionWatcher != null) {
            viewPositionWatcher.shutdown();
        }
        ViewPositionWatcher viewPositionWatcher2 = new ViewPositionWatcher(this.containerView);
        this.viewPositionWatcher = viewPositionWatcher2;
        this.iBlur3FactoryLiquidGlass.setSourceRootView(viewPositionWatcher2, this.containerView);
        this.iBlur3FactoryFrostedLiquidGlass.setSourceRootView(this.viewPositionWatcher, this.containerView);
        blurredBackgroundDrawableViewFactory3.setSourceRootView(this.viewPositionWatcher, this.containerView);
        this.bottomFadeView = new View(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.16
            @Override // android.view.View
            public void onSizeChanged(int i7, int i8, int i9, int i10) {
                super.onSizeChanged(i7, i8, i9, i10);
                ChatAttachAlert.this.bottomFadeDrawable.setBounds(0, (i8 - AndroidUtilities.navigationBarHeight) - AndroidUtilities.dp(48.0f), i7, i8);
            }

            @Override // android.view.View
            public void draw(Canvas canvas) {
                super.draw(canvas);
                ChatAttachAlert.this.bottomFadeDrawable.draw(canvas);
            }
        };
        this.bottomFadeDrawable = new BlurredBackgroundWithFadeDrawable(blurredBackgroundDrawableViewFactory3.create(this.bottomFadeView, (BlurredBackgroundColorProvider) null));
        if (SharedConfig.chatBlurEnabled()) {
            LiteMode.isEnabled(262144);
        }
        this.bottomFadeDrawable.setFadeHeight(AndroidUtilities.dp(72.0f), true);
        this.containerView.addView(this.bottomFadeView, LayoutHelper.createFrameMatchParent());
        BlurredBackgroundDrawable blurredBackgroundDrawableCreate = this.iBlur3FactoryLiquidGlass.create(this.buttonsRecyclerViewWrapper, BlurredBackgroundProviderImpl.mainTabs(resourcesProvider));
        blurredBackgroundDrawableCreate.setRadius(AndroidUtilities.dp(28.0f));
        blurredBackgroundDrawableCreate.setPadding(AndroidUtilities.dp(7.0f));
        this.buttonsRecyclerViewWrapper.setBackground(blurredBackgroundDrawableCreate);
        this.buttonsRecyclerView.setPadding(AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(11.0f));
        this.buttonsRecyclerView.setClipToOutline(true);
        this.buttonsRecyclerView.setOutlineProvider(ViewOutlineProviderImpl.boundsWithPaddingRoundRect(AndroidUtilities.dp(11.0f), AndroidUtilities.dp(28.0f)));
        this.buttonsRecyclerView.setImportantForAccessibility(1);
        this.buttonsRecyclerView.setOverScrollMode(2);
        this.buttonsRecyclerViewWrapper.addView(this.buttonsRecyclerView, LayoutHelper.createFrameMatchParent());
        this.containerView.addView(this.buttonsRecyclerViewWrapper, LayoutHelper.createFrame(-1, 70, 81));
        this.buttonsRecyclerView.setOnItemClickListener(new RecyclerListView.OnItemClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda19
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListener
            public final void onItemClick(View view, int i7) {
                this.f$0.lambda$new$14(resourcesProvider, view, i7);
            }
        });
        this.buttonsRecyclerView.setOnItemLongClickListener(new RecyclerListView.OnItemLongClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda20
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemLongClickListener
            public final boolean onItemClick(View view, int i7) {
                return this.f$0.lambda$new$15(view, i7);
            }
        });
        AnimatedTextView animatedTextView = new AnimatedTextView(context, true, false, true);
        this.botMainButtonTextView = animatedTextView;
        animatedTextView.setVisibility(8);
        this.botMainButtonTextView.setAlpha(0.0f);
        this.botMainButtonTextView.setGravity(17);
        this.botMainButtonTextView.setTypeface(AndroidUtilities.bold());
        int iDp = AndroidUtilities.dp(16.0f);
        this.botMainButtonTextView.setPadding(iDp, 0, iDp, 0);
        this.botMainButtonTextView.setTextSize(AndroidUtilities.dp(f));
        this.botMainButtonTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda21
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$16(view);
            }
        });
        this.containerView.addView(this.botMainButtonTextView, LayoutHelper.createFrame(-1, 48, 83));
        RadialProgressView radialProgressView = new RadialProgressView(context);
        this.botProgressView = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(18.0f));
        this.botProgressView.setAlpha(0.0f);
        this.botProgressView.setScaleX(0.1f);
        this.botProgressView.setScaleY(0.1f);
        this.botProgressView.setVisibility(8);
        this.containerView.addView(this.botProgressView, LayoutHelper.createFrame(28, 28.0f, 85, 0.0f, 0.0f, 10.0f, 10.0f));
        FragmentFloatingButton fragmentFloatingButton = new FragmentFloatingButton(context, resourcesProvider);
        this.cameraFloatingButton = fragmentFloatingButton;
        fragmentFloatingButton.setImageResource(R.drawable.camera);
        fragmentFloatingButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda22
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$17(view);
            }
        });
        fragmentFloatingButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda23
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return this.f$0.lambda$new$18(view);
            }
        });
        FrameLayout.LayoutParams layoutParamsCreateDefaultLayoutParams = FragmentFloatingButton.createDefaultLayoutParams();
        layoutParamsCreateDefaultLayoutParams.bottomMargin += AndroidUtilities.dp(70.0f);
        this.containerView.addView(fragmentFloatingButton, layoutParamsCreateDefaultLayoutParams);
        ImageView imageView2 = new ImageView(context);
        this.moveCaptionButton = imageView2;
        ImageView.ScaleType scaleType = ImageView.ScaleType.CENTER;
        imageView2.setScaleType(scaleType);
        ImageView imageView3 = this.moveCaptionButton;
        int themedColor2 = getThemedColor(Theme.key_windowBackgroundWhiteGrayText2);
        PorterDuff.Mode mode2 = PorterDuff.Mode.SRC_IN;
        imageView3.setColorFilter(new PorterDuffColorFilter(themedColor2, mode2));
        this.moveCaptionButton.setImageResource(R.drawable.menu_link_above);
        this.moveCaptionButton.setVisibility(8);
        this.moveCaptionButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda24
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$19(view);
            }
        });
        this.frameLayout2 = new AnonymousClass17(context);
        FrameLayout frameLayout3 = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.18
            private int lastHeight;
            private final Path path = new Path();
            private final GradientClip clip = new GradientClip();

            @Override // android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                if (ChatAttachAlert.this.captionContainerBg != null) {
                    ChatAttachAlert.this.captionContainerBg.setBounds(0, (int) ChatAttachAlert.this.captionEditTextTopOffset, getMeasuredWidth(), getMeasuredHeight());
                    ChatAttachAlert.this.captionContainerBg.draw(canvas);
                }
                float fDp = AndroidUtilities.dp(20.0f);
                int iDp2 = AndroidUtilities.dp(7.0f);
                int iDp3 = AndroidUtilities.dp(7.0f);
                RectF rectF4 = AndroidUtilities.rectTmp;
                float f2 = iDp2;
                rectF4.set(getPaddingLeft(), f2, getWidth() - getPaddingRight(), getHeight() - iDp3);
                this.path.rewind();
                this.path.addRoundRect(rectF4, fDp, fDp, Path.Direction.CW);
                canvas.save();
                canvas.clipPath(this.path);
                canvas.saveLayerAlpha(rectF4, 255, 31);
                super.dispatchDraw(canvas);
                rectF4.set(getPaddingLeft(), f2, getWidth() - getPaddingRight(), iDp2 + AndroidUtilities.dp(6.0f));
                this.clip.draw(canvas, rectF4, 1, 1.0f);
                rectF4.set(getPaddingLeft(), (getHeight() - iDp3) - AndroidUtilities.dp(6.0f), getWidth() - getPaddingRight(), getHeight() - iDp3);
                this.clip.draw(canvas, rectF4, 3, 1.0f);
                canvas.restore();
                canvas.restore();
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean z4, int i7, int i8, int i9, int i10) {
                int top = this.lastHeight - ChatAttachAlert.this.aiButton.getTop();
                super.onLayout(z4, i7, i8, i9, i10);
                this.lastHeight = getHeight();
                if (ChatAttachAlert.this.aiButton.getVisibility() != 0 || getHeight() - ChatAttachAlert.this.aiButton.getTop() == top) {
                    return;
                }
                ChatAttachAlert.this.aiButton.setTranslationY(((getHeight() - ChatAttachAlert.this.aiButton.getTop()) - top) + ChatAttachAlert.this.aiButton.getTranslationY());
                ChatAttachAlert.this.aiButton.animate().translationY(0.0f).setDuration(320L).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).start();
            }
        };
        this.captionContainer = frameLayout3;
        this.frameLayout2.addView(frameLayout3, LayoutHelper.createFrame(-1, -1, 119));
        BlurredBackgroundDrawable blurredBackgroundDrawableCreate2 = this.iBlur3FactoryFrostedLiquidGlass.create(this.sizeNotifierFrameLayout, BlurredBackgroundProviderImpl.inputFieldDialogActivity(resourcesProvider));
        this.emojiViewChildBg = blurredBackgroundDrawableCreate2;
        blurredBackgroundDrawableCreate2.enableInAppKeyboardOptimization();
        this.emojiViewChildBg.setRadius(AndroidUtilities.dp(29.0f), AndroidUtilities.dp(29.0f), 0.0f, 0.0f);
        this.emojiViewChildBg.setThickness(AndroidUtilities.dp(32.0f));
        this.emojiViewChildBg.setIntensity(0.4f);
        BlurredBackgroundDrawable blurredBackgroundDrawableCreate3 = this.iBlur3FactoryLiquidGlass.create(this.captionContainer, BlurredBackgroundProviderImpl.inputFieldDialogActivity(resourcesProvider));
        this.captionContainerBg = blurredBackgroundDrawableCreate3;
        blurredBackgroundDrawableCreate3.setRadius(AndroidUtilities.dp(22.0f));
        this.captionContainerBg.setPadding(AndroidUtilities.dp(7.0f));
        this.captionContainer.setPadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(7.0f), AndroidUtilities.dp(5.0f));
        this.frameLayout2.setWillNotDraw(false);
        this.frameLayout2.setVisibility(4);
        this.frameLayout2.setAlpha(0.0f);
        this.containerView.addView(this.frameLayout2, LayoutHelper.createFrame(-1, -2, 83));
        this.frameLayout2.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda25
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return ChatAttachAlert.$r8$lambda$951lLyzH1kuqRnkXoJk3xhIZ1h0(view, motionEvent);
            }
        });
        AnimatedTextView animatedTextView2 = new AnimatedTextView(context);
        this.captionLimitView = animatedTextView2;
        animatedTextView2.setAllowCancel(true);
        animatedTextView2.setScaleProperty(0.6f);
        animatedTextView2.setVisibility(8);
        animatedTextView2.setTextSize(AndroidUtilities.dp(15.0f));
        int i7 = Theme.key_windowBackgroundWhiteGrayText;
        animatedTextView2.setTextColor(getThemedColor(i7));
        animatedTextView2.setTypeface(AndroidUtilities.bold());
        animatedTextView2.setGravity(17);
        this.captionContainer.addView(animatedTextView2, LayoutHelper.createFrame(56, 20.0f, 85, 3.0f, 0.0f, 3.0f, 50.0f));
        ImageView imageView4 = new ImageView(context);
        this.aiButton = imageView4;
        AiButtonDrawable aiButtonDrawable = new AiButtonDrawable(context);
        this.aiButtonIcon = aiButtonDrawable;
        imageView4.setImageDrawable(aiButtonDrawable);
        imageView4.setScaleType(scaleType);
        int i8 = Theme.key_glass_defaultIcon;
        imageView4.setColorFilter(new PorterDuffColorFilter(getThemedColor(i8), mode));
        int i9 = Theme.key_listSelector;
        imageView4.setBackground(Theme.createSelectorDrawable(getThemedColor(i9), 1, AndroidUtilities.dp(16.0f)));
        this.captionContainer.addView(imageView4, LayoutHelper.createFrame(44, 44.0f, 53, 0.0f, 1.0f, 0.0f, 0.0f));
        imageView4.setContentDescription(LocaleController.getString(R.string.AIEditor));
        ScaleStateListAnimator.apply(imageView4);
        imageView4.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda26
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$23(resourcesProvider, view);
            }
        });
        imageView4.setVisibility(8);
        imageView4.setAlpha(0.0f);
        imageView4.setScaleX(0.6f);
        imageView4.setScaleY(0.6f);
        this.currentLimit = MessagesController.getInstance(UserConfig.selectedAccount).getCaptionMaxLengthLimit();
        boolean z4 = UserConfig.getInstance(i3).isPremium() || LocaleUtils.canUseLocalPremiumEmojis(i3);
        AnonymousClass19 anonymousClass19 = new AnonymousClass19(context, this.sizeNotifierFrameLayout, null, 1, z4, resourcesProvider);
        this.commentTextView = anonymousClass19;
        anonymousClass19.includeNavigationBar = true;
        anonymousClass19.allowEmojisForNonPremium(LocaleUtils.canUseLocalPremiumEmojis(i3));
        this.commentTextView.setHint(LocaleController.getString("AddCaption", R.string.AddCaption));
        this.commentTextView.onResume();
        this.commentTextView.getEditText().setLayoutParams(LayoutHelper.createFrame(-1, -1.0f, 19, 48.0f, 0.0f, 36.0f, 0.0f));
        this.commentTextView.getEditText().addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.ChatAttachAlert.20
            private boolean processChange;
            private boolean wasEmpty;

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i10, int i11, int i12) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i10, int i11, int i12) {
                if (i12 - i11 >= 1) {
                    this.processChange = true;
                }
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                if (chatAttachAlert.mentionContainer == null) {
                    chatAttachAlert.createMentionsContainer();
                }
                if (ChatAttachAlert.this.mentionContainer.getAdapter() != null) {
                    ChatAttachAlert.this.mentionContainer.setReversed(false);
                    ChatAttachAlert.this.mentionContainer.getAdapter().lambda$searchUsernameOrHashtag$8(charSequence, ChatAttachAlert.this.commentTextView.getEditText().getSelectionStart(), null, false, false);
                    ChatAttachAlert.this.updateCommentTextViewPosition();
                }
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                boolean z5;
                int i10;
                if (this.wasEmpty != TextUtils.isEmpty(editable)) {
                    if (ChatAttachAlert.this.currentAttachLayout != null) {
                        ChatAttachAlert.this.currentAttachLayout.onSelectedItemsCountChanged(ChatAttachAlert.this.currentAttachLayout.getSelectedItemsCount());
                    }
                    this.wasEmpty = !this.wasEmpty;
                }
                boolean z6 = false;
                if (this.processChange) {
                    for (ImageSpan imageSpan : (ImageSpan[]) editable.getSpans(0, editable.length(), ImageSpan.class)) {
                        editable.removeSpan(imageSpan);
                    }
                    Emoji.replaceEmoji(editable, ChatAttachAlert.this.commentTextView.getEditText().getPaint().getFontMetricsInt(), false);
                    this.processChange = false;
                }
                ChatAttachAlert.this.codepointCount = Character.codePointCount(editable, 0, editable.length());
                ChatAttachAlert.this.animatorCaptionNotEmpty.setValue(ChatAttachAlert.this.codepointCount > 0, true);
                if (ChatAttachAlert.this.currentLimit > 0 && (i10 = ChatAttachAlert.this.currentLimit - ChatAttachAlert.this.codepointCount) <= 100) {
                    if (i10 < -9999) {
                        i10 = -9999;
                    }
                    long j = i10;
                    ChatAttachAlert.this.captionLimitView.setText(LocaleController.formatNumber(j, ','), ChatAttachAlert.this.captionLimitView.getVisibility() == 0);
                    if (ChatAttachAlert.this.captionLimitView.getVisibility() != 0) {
                        ChatAttachAlert.this.captionLimitView.setVisibility(0);
                        ChatAttachAlert.this.captionLimitView.setAlpha(0.0f);
                        ChatAttachAlert.this.captionLimitView.setScaleX(0.5f);
                        ChatAttachAlert.this.captionLimitView.setScaleY(0.5f);
                    }
                    ChatAttachAlert.this.captionLimitView.animate().setListener(null).cancel();
                    ChatAttachAlert.this.captionLimitView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(100L).start();
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    if (i10 < 0) {
                        chatAttachAlert.captionLimitView.setTextColor(ChatAttachAlert.this.getThemedColor(Theme.key_text_RedRegular));
                        z5 = false;
                    } else {
                        chatAttachAlert.captionLimitView.setTextColor(ChatAttachAlert.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
                        z5 = true;
                    }
                    ChatAttachAlert.this.topCaptionLimitView.setText(LocaleController.formatNumber(j, ','), false);
                    ChatAttachAlert.this.topCaptionLimitView.setAlpha(1.0f);
                } else {
                    ChatAttachAlert.this.captionLimitView.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setDuration(100L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlert.20.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            ChatAttachAlert.this.captionLimitView.setVisibility(8);
                        }
                    });
                    ChatAttachAlert.this.topCaptionLimitView.setAlpha(0.0f);
                    z5 = true;
                }
                ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                if (chatAttachAlert2.sendButtonEnabled != z5) {
                    chatAttachAlert2.sendButtonEnabled = z5;
                    chatAttachAlert2.writeButton.invalidate();
                }
                ChatAttachAlert chatAttachAlert3 = ChatAttachAlert.this;
                if (!chatAttachAlert3.captionAbove) {
                    if (chatAttachAlert3.commentTextView.getEditText().getLineCount() > 2 && !TextUtils.isEmpty(ChatAttachAlert.this.commentTextView.getText().toString().trim())) {
                        z6 = true;
                    }
                    chatAttachAlert3.showAiButton(z6);
                }
                ChatAttachAlert.this.checkIsEphemeralMessage(true);
            }
        });
        this.captionContainer.addView(this.commentTextView, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 84.0f, 0.0f));
        this.captionContainer.setClipChildren(false);
        this.frameLayout2.setClipChildren(false);
        this.commentTextView.setClipChildren(false);
        this.topCommentContainer.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
        this.topCommentContainer.setWillNotDraw(false);
        EditTextEmoji editTextEmoji = new EditTextEmoji(context, this.sizeNotifierFrameLayout, null, 1, z4, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlert.21
            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (!ChatAttachAlert.this.enterCommentEventSent) {
                    if (motionEvent.getX() > ChatAttachAlert.this.topCommentTextView.getEditText().getLeft() && motionEvent.getX() < ChatAttachAlert.this.topCommentTextView.getEditText().getRight() && motionEvent.getY() > ChatAttachAlert.this.topCommentTextView.getEditText().getTop() && motionEvent.getY() < ChatAttachAlert.this.topCommentTextView.getEditText().getBottom()) {
                        ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                        chatAttachAlert.makeFocusable(chatAttachAlert.topCommentTextView.getEditText(), true);
                    } else {
                        ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                        chatAttachAlert2.makeFocusable(chatAttachAlert2.topCommentTextView.getEditText(), false);
                    }
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            @Override // org.telegram.ui.Components.EditTextEmoji
            public void onLineCountChanged(int i10, int i11) {
                super.onLineCountChanged(i10, i11);
                ChatAttachAlert.this.updatedTopCaptionHeight();
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                if (chatAttachAlert.captionAbove) {
                    chatAttachAlert.showAiButton(i11 > 2 && !TextUtils.isEmpty(getEditText().getText().toString().trim()));
                }
            }

            @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean z5, int i10, int i11, int i12, int i13) {
                super.onLayout(z5, i10, i11, i12, i13);
                ChatAttachAlert.this.updatedTopCaptionHeight();
            }

            @Override // org.telegram.ui.Components.EditTextEmoji
            public void extendActionMode(ActionMode actionMode, Menu menu) {
                BaseFragment baseFragment2 = ChatAttachAlert.this.baseFragment;
                if (baseFragment2 instanceof ChatActivity) {
                    ChatActivity.fillActionModeMenu(menu, ((ChatActivity) baseFragment2).getCurrentEncryptedChat(), true, true);
                }
                super.extendActionMode(actionMode, menu);
            }

            @Override // org.telegram.ui.Components.EditTextEmoji
            public void createEmojiView() {
                super.createEmojiView();
                EmojiView emojiView = getEmojiView();
                if (emojiView != null) {
                    emojiView.shouldLightenBackground = false;
                    emojiView.fixBottomTabContainerTranslation = false;
                    emojiView.setShouldDrawBackground(false);
                    emojiView.setBottomInset(AndroidUtilities.navigationBarHeight);
                }
            }
        };
        this.topCommentTextView = editTextEmoji;
        editTextEmoji.includeNavigationBar = true;
        editTextEmoji.getEditText().addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.Components.ChatAttachAlert.22
            private boolean processChange;
            private boolean wasEmpty;

            @Override // android.text.TextWatcher
            public void beforeTextChanged(CharSequence charSequence, int i10, int i11, int i12) {
            }

            @Override // android.text.TextWatcher
            public void onTextChanged(CharSequence charSequence, int i10, int i11, int i12) {
                if (i12 - i11 >= 1) {
                    this.processChange = true;
                }
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                if (chatAttachAlert.mentionContainer == null) {
                    chatAttachAlert.createMentionsContainer();
                }
                if (ChatAttachAlert.this.mentionContainer.getAdapter() != null) {
                    ChatAttachAlert.this.mentionContainer.setReversed(true);
                    ChatAttachAlert.this.mentionContainer.getAdapter().lambda$searchUsernameOrHashtag$8(charSequence, ChatAttachAlert.this.topCommentTextView.getEditText().getSelectionStart(), null, false, false);
                    ChatAttachAlert.this.updateCommentTextViewPosition();
                }
            }

            @Override // android.text.TextWatcher
            public void afterTextChanged(Editable editable) {
                boolean z5;
                int i10;
                if (this.wasEmpty != TextUtils.isEmpty(editable)) {
                    if (ChatAttachAlert.this.currentAttachLayout != null) {
                        ChatAttachAlert.this.currentAttachLayout.onSelectedItemsCountChanged(ChatAttachAlert.this.currentAttachLayout.getSelectedItemsCount());
                    }
                    this.wasEmpty = !this.wasEmpty;
                }
                boolean z6 = false;
                if (this.processChange) {
                    for (ImageSpan imageSpan : (ImageSpan[]) editable.getSpans(0, editable.length(), ImageSpan.class)) {
                        editable.removeSpan(imageSpan);
                    }
                    Emoji.replaceEmoji(editable, ChatAttachAlert.this.topCommentTextView.getEditText().getPaint().getFontMetricsInt(), false);
                    this.processChange = false;
                }
                ChatAttachAlert.this.codepointCount = Character.codePointCount(editable, 0, editable.length());
                ChatAttachAlert.this.animatorCaptionNotEmpty.setValue(ChatAttachAlert.this.codepointCount > 0, true);
                if (ChatAttachAlert.this.currentLimit > 0 && (i10 = ChatAttachAlert.this.currentLimit - ChatAttachAlert.this.codepointCount) <= 100) {
                    if (i10 < -9999) {
                        i10 = -9999;
                    }
                    long j = i10;
                    ChatAttachAlert.this.topCaptionLimitView.setText(LocaleController.formatNumber(j, ','), ChatAttachAlert.this.topCaptionLimitView.getVisibility() == 0);
                    if (ChatAttachAlert.this.topCaptionLimitView.getVisibility() != 0) {
                        ChatAttachAlert.this.topCaptionLimitView.setVisibility(0);
                        ChatAttachAlert.this.topCaptionLimitView.setAlpha(0.0f);
                        ChatAttachAlert.this.topCaptionLimitView.setScaleX(0.5f);
                        ChatAttachAlert.this.topCaptionLimitView.setScaleY(0.5f);
                    }
                    ChatAttachAlert.this.topCaptionLimitView.animate().setListener(null).cancel();
                    ChatAttachAlert.this.topCaptionLimitView.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(100L).start();
                    ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                    if (i10 < 0) {
                        chatAttachAlert.topCaptionLimitView.setTextColor(ChatAttachAlert.this.getThemedColor(Theme.key_text_RedRegular));
                        z5 = false;
                    } else {
                        chatAttachAlert.topCaptionLimitView.setTextColor(ChatAttachAlert.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
                        z5 = true;
                    }
                    ChatAttachAlert.this.captionLimitView.setText(LocaleController.formatNumber(j, ','), false);
                    ChatAttachAlert.this.captionLimitView.setAlpha(1.0f);
                } else {
                    ChatAttachAlert.this.topCaptionLimitView.animate().alpha(0.0f).scaleX(0.5f).scaleY(0.5f).setDuration(100L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlert.22.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            ChatAttachAlert.this.topCaptionLimitView.setVisibility(8);
                        }
                    });
                    ChatAttachAlert.this.captionLimitView.setAlpha(0.0f);
                    z5 = true;
                }
                ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                if (chatAttachAlert2.sendButtonEnabled != z5) {
                    chatAttachAlert2.sendButtonEnabled = z5;
                    chatAttachAlert2.writeButton.invalidate();
                }
                ChatAttachAlert chatAttachAlert3 = ChatAttachAlert.this;
                if (!chatAttachAlert3.captionLimitBulletinShown && !MessagesController.getInstance(chatAttachAlert3.currentAccount).premiumFeaturesBlocked() && !UserConfig.getInstance(ChatAttachAlert.this.currentAccount).isPremium() && ChatAttachAlert.this.codepointCount > MessagesController.getInstance(ChatAttachAlert.this.currentAccount).captionLengthLimitDefault && ChatAttachAlert.this.codepointCount < MessagesController.getInstance(ChatAttachAlert.this.currentAccount).captionLengthLimitPremium) {
                    ChatAttachAlert chatAttachAlert4 = ChatAttachAlert.this;
                    chatAttachAlert4.captionLimitBulletinShown = true;
                    chatAttachAlert4.showCaptionLimitBulletin(baseFragment);
                }
                ChatAttachAlert chatAttachAlert5 = ChatAttachAlert.this;
                if (chatAttachAlert5.captionAbove) {
                    if (chatAttachAlert5.topCommentTextView.getEditText().getLineCount() > 2 && !TextUtils.isEmpty(ChatAttachAlert.this.topCommentTextView.getText().toString().trim())) {
                        z6 = true;
                    }
                    chatAttachAlert5.showAiButton(z6);
                }
                ChatAttachAlert.this.checkIsEphemeralMessage(true);
            }
        });
        this.topCommentTextView.allowEmojisForNonPremium(LocaleUtils.canUseLocalPremiumEmojis(i3));
        this.topCommentTextView.getEditText().setPadding(0, AndroidUtilities.dp(9.0f), 0, AndroidUtilities.dp(9.0f));
        this.topCommentTextView.getEditText().setLayoutParams(LayoutHelper.createFrame(-1, -1.0f, 19, 48.0f, 0.0f, 96.0f, 0.0f));
        this.topCommentTextView.getEditText().setTextSize(1, 17.0f);
        this.topCommentTextView.getEmojiButton().setLayoutParams(LayoutHelper.createFrame(40, 40.0f, 83, 0.0f, 0.0f, 0.0f, 0.0f));
        this.topCommentTextView.setHint(LocaleController.getString("AddCaption", R.string.AddCaption));
        this.topCommentContainer.addView(this.topCommentTextView, LayoutHelper.createFrame(-1, -2, 119));
        this.topCommentContainer.setAlpha(0.0f);
        this.topCommentContainer.setVisibility(8);
        this.commentTextView.addView(this.moveCaptionButton, LayoutHelper.createFrame(40, 40.0f, 85, 0.0f, 0.0f, 0.0f, 4.0f));
        BlurredBackgroundDrawable blurredBackgroundDrawableCreate4 = this.iBlur3FactoryLiquidGlass.create(this.topCommentContainer, BlurredBackgroundProviderImpl.inputFieldDialogActivity(resourcesProvider));
        blurredBackgroundDrawableCreate4.setRadius(AndroidUtilities.dp(22.0f));
        blurredBackgroundDrawableCreate4.setPadding(AndroidUtilities.dp(7.0f));
        this.topCommentContainer.setBackground(blurredBackgroundDrawableCreate4);
        this.topCommentContainer.setPadding(AndroidUtilities.dp(11.0f), AndroidUtilities.dp(9.0f), AndroidUtilities.dp(11.0f), AndroidUtilities.dp(9.0f));
        AnimatedTextView animatedTextView3 = new AnimatedTextView(context);
        this.topCaptionLimitView = animatedTextView3;
        animatedTextView3.setScaleProperty(0.6f);
        animatedTextView3.setVisibility(8);
        animatedTextView3.setTextSize(AndroidUtilities.dp(15.0f));
        animatedTextView3.setTextColor(getThemedColor(i7));
        animatedTextView3.setTypeface(AndroidUtilities.bold());
        animatedTextView3.setGravity(17);
        animatedTextView3.setAllowCancel(true);
        this.topCommentContainer.addView(animatedTextView3, LayoutHelper.createFrame(56, 20.0f, 53, 3.0f, 45.0f, 3.0f, 0.0f));
        ImageView imageView5 = new ImageView(context);
        this.topCommentMoveButton = imageView5;
        imageView5.setScaleType(scaleType);
        this.topCommentMoveButton.setImageResource(R.drawable.menu_link_below);
        this.topCommentMoveButton.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_messagePanelIcons), mode2));
        this.topCommentTextView.addView(this.topCommentMoveButton, LayoutHelper.createFrame(40, 40.0f, 85, 0.0f, 0.0f, 60.0f, 0.0f));
        this.topCommentMoveButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda27
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$24(view);
            }
        });
        ImageView imageView6 = new ImageView(context);
        this.topAiButton = imageView6;
        AiButtonDrawable aiButtonDrawable2 = new AiButtonDrawable(context);
        this.topAiButtonIcon = aiButtonDrawable2;
        imageView6.setImageDrawable(aiButtonDrawable2);
        imageView6.setScaleType(scaleType);
        imageView6.setColorFilter(new PorterDuffColorFilter(getThemedColor(i8), mode));
        imageView6.setBackground(Theme.createSelectorDrawable(getThemedColor(i9), 1, AndroidUtilities.dp(16.0f)));
        this.topCommentContainer.addView(imageView6, LayoutHelper.createFrame(44, 44.0f, 85, 0.0f, 1.0f, 0.0f, 0.0f));
        imageView6.setContentDescription(LocaleController.getString(R.string.AIEditor));
        ScaleStateListAnimator.apply(imageView6);
        imageView6.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda28
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$27(resourcesProvider, view);
            }
        });
        imageView6.setVisibility(8);
        imageView6.setAlpha(0.0f);
        imageView6.setScaleX(0.6f);
        imageView6.setScaleY(0.6f);
        FrameLayout frameLayout4 = new FrameLayout(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.23
            @Override // android.view.View
            public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                AttachAlertLayout attachAlertLayout = ChatAttachAlert.this.currentAttachLayout;
                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout3 = ChatAttachAlert.this.photoLayout;
                ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
                if (attachAlertLayout == chatAttachAlertPhotoLayout3) {
                    accessibilityNodeInfo.setText(LocaleController.formatPluralString("AccDescrSendPhotos", chatAttachAlert.photoLayout.getSelectedItemsCount(), new Object[0]));
                } else {
                    AttachAlertLayout attachAlertLayout2 = chatAttachAlert.currentAttachLayout;
                    ChatAttachAlertDocumentLayout chatAttachAlertDocumentLayout = ChatAttachAlert.this.documentLayout;
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
                    if (attachAlertLayout2 == chatAttachAlertDocumentLayout) {
                        accessibilityNodeInfo.setText(LocaleController.formatPluralString("AccDescrSendFiles", chatAttachAlert2.documentLayout.getSelectedItemsCount(), new Object[0]));
                    } else if (chatAttachAlert2.currentAttachLayout == ChatAttachAlert.this.audioLayout) {
                        accessibilityNodeInfo.setText(LocaleController.formatPluralString("AccDescrSendAudio", ChatAttachAlert.this.audioLayout.getSelectedItemsCount(), new Object[0]));
                    }
                }
                accessibilityNodeInfo.setClassName(Button.class.getName());
                accessibilityNodeInfo.setLongClickable(true);
                accessibilityNodeInfo.setClickable(true);
            }
        };
        this.writeButtonContainer = frameLayout4;
        frameLayout4.setFocusable(true);
        this.writeButtonContainer.setFocusableInTouchMode(true);
        this.writeButtonContainer.setVisibility(4);
        this.writeButtonContainer.setScaleX(0.2f);
        this.writeButtonContainer.setScaleY(0.2f);
        this.writeButtonContainer.setAlpha(0.0f);
        this.writeButtonContainer.setClipChildren(false);
        this.writeButtonContainer.setClipToPadding(false);
        this.containerView.addView(this.writeButtonContainer, LayoutHelper.createFrame(110, 50, 85));
        ChatActivityEnterView.SendButton sendButton = new ChatActivityEnterView.SendButton(context, R.drawable.send_extera_24, resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlert.24
            @Override // org.telegram.ui.Components.ChatActivityEnterView.SendButton
            public boolean isOpen() {
                return true;
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.SendButton
            public boolean shouldDrawBackground() {
                return true;
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.SendButton
            public boolean isInScheduleMode() {
                return super.isInScheduleMode();
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.SendButton
            public boolean isInactive() {
                return !ChatAttachAlert.this.sendButtonEnabled;
            }

            @Override // org.telegram.ui.Components.ChatActivityEnterView.SendButton
            public int getFillColor() {
                return ChatAttachAlert.this.getThemedColor(Theme.key_dialogFloatingButton);
            }
        };
        this.writeButton = sendButton;
        sendButton.setImportantForAccessibility(2);
        this.writeButtonContainer.addView(this.writeButton, LayoutHelper.createFrame(-1, -1.0f, 119, 0.0f, 0.0f, 0.0f, 1.0f));
        this.writeButton.setTranslationX(this.backgroundPaddingLeft);
        this.writeButton.setCircleSize(AndroidUtilities.dp(52.0f), AndroidUtilities.dp(38.0f));
        this.writeButton.setCirclePadding(AndroidUtilities.dp(7.0f), AndroidUtilities.dp(6.0f));
        ChatActivityEnterView.SendButton sendButton2 = this.writeButton;
        sendButton2.newCounterPos = true;
        sendButton2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda30
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$28(view);
            }
        });
        this.writeButton.setOnTouchListener(new View.OnTouchListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda31
            @Override // android.view.View.OnTouchListener
            public final boolean onTouch(View view, MotionEvent motionEvent) {
                return this.f$0.lambda$new$29(view, motionEvent);
            }
        });
        this.writeButton.setOnLongClickListener(new View.OnLongClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda32
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return this.f$0.lambda$new$42(context, resourcesProvider, baseFragment, view);
            }
        });
        this.textPaint.setTextSize(AndroidUtilities.dp(12.0f));
        this.textPaint.setTypeface(AndroidUtilities.bold());
        View view = new View(context) { // from class: org.telegram.ui.Components.ChatAttachAlert.27
            @Override // android.view.View
            public void onDraw(Canvas canvas) {
                String str = String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf(Math.max(1, ChatAttachAlert.this.currentAttachLayout.getSelectedItemsCount())));
                int iCeil = (int) Math.ceil(ChatAttachAlert.this.textPaint.measureText(str));
                int iMax = Math.max(AndroidUtilities.dp(16.0f) + iCeil, AndroidUtilities.dp(24.0f));
                int measuredWidth = getMeasuredWidth() / 2;
                int themedColor3 = ChatAttachAlert.this.getThemedColor(Theme.key_dialogRoundCheckBoxCheck);
                ChatAttachAlert.this.textPaint.setColor(ColorUtils.setAlphaComponent(themedColor3, (int) (((double) Color.alpha(themedColor3)) * ((((double) ChatAttachAlert.this.sendButtonEnabledProgress) * 0.42d) + 0.58d))));
                ChatAttachAlert.this.paint.setColor(ChatAttachAlert.this.getThemedColor(Theme.key_dialogBackground));
                int i10 = iMax / 2;
                int i11 = measuredWidth - i10;
                int i12 = i10 + measuredWidth;
                ChatAttachAlert.this.rect.set(i11, 0.0f, i12, getMeasuredHeight());
                canvas.drawRoundRect(ChatAttachAlert.this.rect, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), ChatAttachAlert.this.paint);
                ChatAttachAlert.this.paint.setColor(ChatAttachAlert.this.getThemedColor(Theme.key_chat_attachCheckBoxBackground));
                ChatAttachAlert.this.rect.set(i11 + AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f), i12 - AndroidUtilities.dp(2.0f), getMeasuredHeight() - AndroidUtilities.dp(2.0f));
                canvas.drawRoundRect(ChatAttachAlert.this.rect, AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), ChatAttachAlert.this.paint);
                canvas.drawText(str, measuredWidth - (iCeil / 2), AndroidUtilities.dp(16.2f), ChatAttachAlert.this.textPaint);
            }
        };
        this.selectedCountView = view;
        view.setAlpha(0.0f);
        this.selectedCountView.setScaleX(0.2f);
        this.selectedCountView.setScaleY(0.2f);
        if (z) {
            checkColors();
            this.navBarColorKey = -1;
        }
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout3 = this.photoLayout;
        if (chatAttachAlertPhotoLayout3 != null) {
            chatAttachAlertPhotoLayout3.gridView.getFastScroll().applyBlurDrawables(this.iBlur3FactoryLiquidGlass, BlurredBackgroundProviderImpl.topPanel(resourcesProvider));
        }
        PasscodeView passcodeView = new PasscodeView(context);
        this.passcodeView = passcodeView;
        this.containerView.addView(passcodeView, LayoutHelper.createFrame(-1, -1.0f));
        this.actionBar.setupGlass(this.iBlur3FactoryLiquidGlass, BlurredBackgroundProviderImpl.attachMenuActionBar(resourcesProvider));
        replaceAnimator.replace(1L, false);
    }

    public void lambda$sendButtonPressed$0(MediaController.PhotoEntry photoEntry, boolean z, int i, boolean z2, Long l) {
            ChatAttachAlertPhotoLayout.selectedPhotosOrder.clear();
            ChatAttachAlertPhotoLayout.selectedPhotos.clear();
            ChatAttachAlertPhotoLayout.selectedPhotosOrder.add(0);
            ChatAttachAlertPhotoLayout.selectedPhotos.put(0, photoEntry);
            ChatAttachAlert.this.delegate.didPressedButton(7, true, z, i, 0, 0L, isCaptionAbove(), z2, l.longValue());
        }
    }

    public void lambda$new$9(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2, long j) {
        ((ChatActivity) this.baseFragment).didSelectLocation(messageMedia, i, z, i2, j);
    }

    public void lambda$new$13(final AttachBotButton attachBotButton, Boolean bool) {
        TLRPC.TL_messages_toggleBotInAttachMenu tL_messages_toggleBotInAttachMenu = new TLRPC.TL_messages_toggleBotInAttachMenu();
        tL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(this.currentAccount).getInputUser(attachBotButton.attachMenuBot.bot_id);
        tL_messages_toggleBotInAttachMenu.enabled = true;
        tL_messages_toggleBotInAttachMenu.write_allowed = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_toggleBotInAttachMenu, new RequestDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda66
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$new$12(attachBotButton, tLObject, tL_error);
            }
        }, 66);
    }

    public void lambda$new$21(CharSequence charSequence) {
        this.commentTextView.setText(charSequence);
        this.commentTextView.setSelection(charSequence.length(), charSequence.length());
    }

    public void lambda$dispatchDraw$0(EditTextCaption editTextCaption, ValueAnimator valueAnimator) {
            editTextCaption.setOffsetY(((Float) valueAnimator.getAnimatedValue()).floatValue());
            ChatAttachAlert.this.updateCommentTextViewPosition();
            if (ChatAttachAlert.this.currentAttachLayout == ChatAttachAlert.this.photoLayout) {
                ChatAttachAlert.this.photoLayout.onContainerTranslationUpdated(ChatAttachAlert.this.currentPanTranslationY);
            }
        }

        @Override // org.telegram.ui.Components.EditTextEmoji
        public void onLineCountChanged(int i, int i2) {
            boolean z = false;
            if (!TextUtils.isEmpty(getEditText().getText())) {
                this.shouldAnimateEditTextWithBounds = true;
                this.messageEditTextPredrawHeigth = getEditText().getMeasuredHeight();
                this.messageEditTextPredrawScrollY = getEditText().getScrollY();
                invalidate();
            } else {
                getEditText().animate().cancel();
                getEditText().setOffsetY(0.0f);
                this.shouldAnimateEditTextWithBounds = false;
            }
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            if (!chatAttachAlert.captionAbove) {
                if (i2 > 2 && !TextUtils.isEmpty(getEditText().getText().toString().trim())) {
                    z = true;
                }
                chatAttachAlert.showAiButton(z);
            }
            ChatAttachAlert chatAttachAlert2 = ChatAttachAlert.this;
            chatAttachAlert2.chatActivityEnterViewAnimateFromTop = chatAttachAlert2.frameLayout2.getTop() + ChatAttachAlert.this.captionEditTextTopOffset;
            ChatAttachAlert.this.frameLayout2.invalidate();
            ChatAttachAlert.this.updateCommentTextViewPosition();
        }

        @Override // org.telegram.ui.Components.EditTextEmoji
        public void bottomPanelTranslationY(float f) {
            ChatAttachAlert.this.bottomPannelTranslation = f;
            ChatAttachAlert.this.frameLayout2.setTranslationY(f);
            ChatAttachAlert.this.frameLayout2.invalidate();
            ChatAttachAlert.this.checkUi_writeButtonContainerY();
            ChatAttachAlert chatAttachAlert = ChatAttachAlert.this;
            chatAttachAlert.updateLayout(chatAttachAlert.currentAttachLayout, true, 0);
        }

        @Override // org.telegram.ui.Components.EditTextEmoji
        public void closeParent() {
            ChatAttachAlert.super.lambda$new$0();
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            ChatAttachAlert.this.updateCommentTextViewPosition();
        }

        @Override // org.telegram.ui.Components.EditTextEmoji
        public void extendActionMode(ActionMode actionMode, Menu menu) {
            BaseFragment baseFragment = ChatAttachAlert.this.baseFragment;
            if (baseFragment instanceof ChatActivity) {
                ChatActivity.fillActionModeMenu(menu, ((ChatActivity) baseFragment).getCurrentEncryptedChat(), true, true);
            }
            super.extendActionMode(actionMode, menu);
        }

        @Override // org.telegram.ui.Components.EditTextEmoji
        public void createEmojiView() {
            super.createEmojiView();
            EmojiView emojiView = getEmojiView();
            if (emojiView != null) {
                emojiView.shouldLightenBackground = false;
                emojiView.fixBottomTabContainerTranslation = false;
                emojiView.setShouldDrawBackground(false);
                emojiView.setBottomInset(AndroidUtilities.navigationBarHeight);
            }
        }
    }

    public void lambda$new$25(CharSequence charSequence) {
        this.topCommentTextView.setText(charSequence);
        this.topCommentTextView.setSelection(charSequence.length(), charSequence.length());
    }

    public void lambda$new$30(DialogInterface dialogInterface) {
        this.sendButtonItemOptions = null;
        this.messageSendPreview = null;
    }

    void lambda$new$31(long j, boolean z, int i, int i2) {
        ChatAttachAlert chatAttachAlert;
        boolean zSendPressed;
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            chatAttachAlert = this;
            zSendPressed = chatAttachAlert.sendPressed(z, i, i2, j, isCaptionAbove());
        } else {
            if (!attachAlertLayout.sendSelectedItems(z, i, i2, j, isCaptionAbove())) {
                this.allowPassConfirmationAlert = true;
                lambda$new$0();
            }
            zSendPressed = false;
            chatAttachAlert = this;
        }
        MessageSendPreview messageSendPreview = chatAttachAlert.messageSendPreview;
        if (messageSendPreview != null) {
            messageSendPreview.dismiss(!zSendPressed);
            chatAttachAlert.messageSendPreview = null;
        }
    }

    public void lambda$new$35(long j, Theme.ResourcesProvider resourcesProvider) {
        AlertsCreator.createScheduleDatePickerDialog(getContext(), j, new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda72
            @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
            public final void didSelectDate(boolean z, int i, int i2) {
                this.f$0.lambda$new$34(z, i, i2);
            }
        }, resourcesProvider);
    }

    public void lambda$new$37(long j, final ChatActivity chatActivity, Theme.ResourcesProvider resourcesProvider) {
        Context context = getContext();
        int i = this.currentAccount;
        MessageSuggestionParams messageSuggestionParamsEmpty = chatActivity.messageSuggestionParams;
        if (messageSuggestionParamsEmpty == null) {
            messageSuggestionParamsEmpty = MessageSuggestionParams.empty();
        }
        new MessageSuggestionOfferSheet(context, i, j, messageSuggestionParamsEmpty, chatActivity, resourcesProvider, 0, new Utilities.Callback() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda68
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$new$36(chatActivity, (MessageSuggestionParams) obj);
            }
        }).show();
    }

    public void lambda$new$38() {
        ChatAttachAlert chatAttachAlert;
        boolean zSendPressed;
        MessageSendPreview messageSendPreview = this.messageSendPreview;
        long selectedEffect = messageSendPreview != null ? messageSendPreview.getSelectedEffect() : 0L;
        ChatActivityEnterView.SendButton sendButton = this.writeButton;
        this.effectId = selectedEffect;
        sendButton.setEffect(selectedEffect);
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            chatAttachAlert = this;
            zSendPressed = chatAttachAlert.sendPressed(false, 0, 0, selectedEffect, isCaptionAbove());
        } else {
            if (!attachAlertLayout.sendSelectedItems(false, 0, 0, selectedEffect, isCaptionAbove())) {
                lambda$new$0();
            }
            zSendPressed = false;
            chatAttachAlert = this;
        }
        MessageSendPreview messageSendPreview2 = chatAttachAlert.messageSendPreview;
        if (messageSendPreview2 != null) {
            messageSendPreview2.dismiss(!zSendPressed);
            chatAttachAlert.messageSendPreview = null;
        }
    }

    public void lambda$new$41(Context context, final ActionBarMenuSubItem actionBarMenuSubItem, Theme.ResourcesProvider resourcesProvider, View view) {
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        if (chatAttachAlertPhotoLayout == null) {
            return;
        }
        StarsIntroActivity.showMediaPriceSheet(context, chatAttachAlertPhotoLayout.getStarsPrice(), true, new Utilities.Callback2() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda69
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$new$40(actionBarMenuSubItem, (Long) obj, (Runnable) obj2);
            }
        }, resourcesProvider);
    }

    public void lambda$onWriteButtonPressed$43(boolean z, int i, int i2) {
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        if (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) {
            sendPressed(z, i, 0, this.effectId, isCaptionAbove());
        } else {
            if (attachAlertLayout.sendSelectedItems(z, i, i2, 0L, isCaptionAbove())) {
                return;
            }
            this.allowPassConfirmationAlert = true;
            lambda$new$0();
        }
    }

    public void updateCommentTextViewPosition() {
        float y;
        this.commentTextView.getLocationOnScreen(this.commentTextViewLocation);
        if (this.mentionContainer != null) {
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            if ((attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout) && this.captionAbove) {
                y = (this.topCommentContainer.getY() - this.mentionContainer.getTop()) + (this.topCommentContainer.getMeasuredHeight() * this.topCommentContainer.getAlpha());
            } else {
                y = -this.commentTextView.getHeight();
            }
            if (Math.abs(this.mentionContainer.getTranslationY() - y) > 0.5f) {
                this.mentionContainer.setTranslationY(y);
                this.mentionContainer.invalidate();
                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
                if (chatAttachAlertPhotoLayout != null) {
                    chatAttachAlertPhotoLayout.checkCameraViewPosition();
                }
            }
        }
        checkUi_writeButtonContainerY();
    }

    public int getCommentTextViewTop() {
        return this.commentTextViewLocation[1];
    }

    public void showCaptionLimitBulletin(final BaseFragment baseFragment) {
        if ((baseFragment instanceof ChatActivity) && ChatObject.isChannelAndNotMegaGroup(((ChatActivity) baseFragment).getCurrentChat())) {
            BulletinFactory.of(this.sizeNotifierFrameLayout, this.resourcesProvider).createCaptionLimitBulletin(MessagesController.getInstance(this.currentAccount).captionLengthLimitPremium, new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda71
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$showCaptionLimitBulletin$44(baseFragment);
                }
            }).show();
        }
    }

    public void lambda$sendPressed$48(boolean z, int i, int i2, long j, boolean z2, Long l) {
        setButtonPressed(true);
        this.delegate.didPressedButton(7, true, z, i, i2, j, z2, false, l.longValue());
    }

    public void setButtonPressed(boolean z) {
        this.buttonPressed = z;
    }

    public void openAttachLayoutForType(int i) {
        if (i == 3) {
            if (this.musicEnabled || !checkCanRemoveRestrictionsByBoosts()) {
                BaseFragment baseFragment = this.baseFragment;
                Activity parentActivity = baseFragment != null ? baseFragment.getParentActivity() : null;
                if (parentActivity != null) {
                    if (Build.VERSION.SDK_INT >= 33) {
                        if (parentActivity.checkSelfPermission("android.permission.READ_MEDIA_AUDIO") != 0) {
                            parentActivity.requestPermissions(new String[]{"android.permission.READ_MEDIA_AUDIO"}, 4);
                            return;
                        }
                    } else if (parentActivity.checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE") != 0) {
                        parentActivity.requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 4);
                        return;
                    }
                }
                openAudioLayout(true);
                return;
            }
            return;
        }
        if (i == 6 && AndroidUtilities.isMapsInstalled(this.baseFragment)) {
            if (this.locationLayout == null) {
                AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
                ChatAttachAlertLocationLayout chatAttachAlertLocationLayout = new ChatAttachAlertLocationLayout(this, getContext(), this.resourcesProvider, true ^ this.isPollAttach);
                this.locationLayout = chatAttachAlertLocationLayout;
                attachAlertLayoutArr[5] = chatAttachAlertLocationLayout;
                ChatAttachAlertLocationLayout.LocationActivityDelegate locationActivityDelegate = this.locationActivityDelegate;
                if (locationActivityDelegate != null) {
                    chatAttachAlertLocationLayout.setDelegate(locationActivityDelegate);
                } else if (this.baseFragment instanceof ChatActivity) {
                    chatAttachAlertLocationLayout.setDelegate(new ChatAttachAlertLocationLayout.LocationActivityDelegate() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda78
                        @Override // org.telegram.ui.Components.ChatAttachAlertLocationLayout.LocationActivityDelegate
                        public final void didSelectLocation(TLRPC.MessageMedia messageMedia, int i2, boolean z, int i3, long j) {
                            this.f$0.lambda$openAttachLayoutForType$49(messageMedia, i2, z, i3, j);
                        }
                    });
                }
            }
            showLayout(this.locationLayout);
        }
    }

    public void lambda$showPollLayout$50(TLRPC.MessageMedia messageMedia, CharSequence charSequence, PollAttachedMediaPack pollAttachedMediaPack, ArrayList arrayList, boolean z, int i, long j) {
        ((ChatActivity) this.baseFragment).sendPoll((TLRPC.TL_messageMediaPoll) messageMedia, charSequence, pollAttachedMediaPack, arrayList, z, i, j);
    }

    public void setupPoll(Boolean bool) {
        this.typeButtonsAvailable = false;
        this.buttonsRecyclerViewWrapper.setVisibility(8);
        showPollLayout(false, bool);
    }

    private void showLayout(AttachAlertLayout attachAlertLayout, long j) {
        showLayout(attachAlertLayout, j, true);
    }

    public void lambda$showCommentTextView$57(ValueAnimator valueAnimator) {
        updatedTopCaptionHeight();
    }

    public int getAdditionalMessagesCount() {
        MessagePreviewParams messagePreviewParams;
        BaseFragment baseFragment = this.baseFragment;
        if (!(baseFragment instanceof ChatActivity) || (messagePreviewParams = ((ChatActivity) baseFragment).messagePreviewParams) == null) {
            return 0;
        }
        return messagePreviewParams.getForwardedMessagesCount();
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void cancelSheetAnimation() {
        AnimatorSet animatorSet = this.currentSheetAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            SpringAnimation springAnimation = this.appearSpringAnimation;
            if (springAnimation != null) {
                springAnimation.cancel();
            }
            AnimatorSet animatorSet2 = this.buttonsAnimation;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
            }
            this.currentSheetAnimation = null;
            this.currentSheetAnimationType = 0;
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean onCustomOpenAnimation() {
        this.photoLayout.setTranslationX(0.0f);
        this.mediaPreviewView.setAlpha(0.0f);
        this.selectedView.setAlpha(1.0f);
        this.containerView.setTranslationY(this.containerView.getMeasuredHeight());
        AnimatorSet animatorSet = new AnimatorSet();
        this.buttonsAnimation = animatorSet;
        animatorSet.playTogether(ObjectAnimator.ofFloat(this, this.ATTACH_ALERT_PROGRESS, 0.0f, 400.0f));
        this.buttonsAnimation.setDuration(400L);
        this.buttonsAnimation.setStartDelay(20L);
        this.ATTACH_ALERT_PROGRESS.set(this, Float.valueOf(0.0f));
        this.buttonsAnimation.start();
        ValueAnimator valueAnimator = this.navigationBarAnimation;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.navigationBarAlpha, 1.0f);
        this.navigationBarAnimation = valueAnimatorOfFloat;
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda14
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.lambda$onCustomOpenAnimation$58(valueAnimator2);
            }
        });
        SpringAnimation springAnimation = this.appearSpringAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        SpringAnimation springAnimation2 = new SpringAnimation(this.containerView, DynamicAnimation.TRANSLATION_Y, 0.0f);
        this.appearSpringAnimation = springAnimation2;
        if (this.editingMessageObject != null) {
            springAnimation2.getSpring().setDampingRatio(0.75f);
            this.appearSpringAnimation.getSpring().setStiffness(350.0f);
        } else {
            springAnimation2.getSpring().setDampingRatio(0.75f);
            this.appearSpringAnimation.getSpring().setStiffness(350.0f);
        }
        this.appearSpringAnimation.start();
        if (this.useHardwareLayer) {
            this.container.setLayerType(2, null);
        }
        this.currentSheetAnimationType = 1;
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.currentSheetAnimation = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofInt(this.backDrawable, (Property<BottomSheet.SheetBackDrawable, Integer>) AnimationProperties.COLOR_DRAWABLE_ALPHA, this.dimBehind ? this.dimBehindAlpha : 0));
        this.currentSheetAnimation.setDuration(400L);
        this.currentSheetAnimation.setStartDelay(20L);
        this.currentSheetAnimation.setInterpolator(this.openInterpolator);
        final AnimationNotificationsLocker animationNotificationsLocker = new AnimationNotificationsLocker();
        final BottomSheet.BottomSheetDelegateInterface bottomSheetDelegateInterface = super.delegate;
        final Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onCustomOpenAnimation$59(animationNotificationsLocker, bottomSheetDelegateInterface);
            }
        };
        this.appearSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda16
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
                this.f$0.lambda$onCustomOpenAnimation$60(runnable, dynamicAnimation, z, f, f2);
            }
        });
        this.currentSheetAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlert.34
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimation == null || !((BottomSheet) ChatAttachAlert.this).currentSheetAnimation.equals(animator) || ChatAttachAlert.this.appearSpringAnimation == null || ChatAttachAlert.this.appearSpringAnimation.isRunning()) {
                    return;
                }
                runnable.run();
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                if (((BottomSheet) ChatAttachAlert.this).currentSheetAnimation == null || !((BottomSheet) ChatAttachAlert.this).currentSheetAnimation.equals(animator)) {
                    return;
                }
                runnable.run();
                ((BottomSheet) ChatAttachAlert.this).currentSheetAnimation = null;
                ((BottomSheet) ChatAttachAlert.this).currentSheetAnimationType = 0;
            }
        });
        animationNotificationsLocker.lock();
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.stopAllHeavyOperations, 512);
        this.currentSheetAnimation.start();
        ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
        setNavBarAlpha(0.0f);
        valueAnimatorOfFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda17
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.lambda$onCustomOpenAnimation$61(valueAnimator2);
            }
        });
        valueAnimatorOfFloat2.setStartDelay(25L);
        valueAnimatorOfFloat2.setDuration(200L);
        valueAnimatorOfFloat2.setInterpolator(CubicBezierInterpolator.DEFAULT);
        valueAnimatorOfFloat2.start();
        return true;
    }

    public void lambda$init$66(TLRPC.MessageMedia messageMedia, int i, boolean z, int i2, long j) {
        ((ChatActivity) this.baseFragment).didSelectLocation(messageMedia, i, z, i2, 0L);
    }

    public void onDestroy() {
        ViewPositionWatcher viewPositionWatcher = this.viewPositionWatcher;
        if (viewPositionWatcher != null) {
            viewPositionWatcher.shutdown();
            this.viewPositionWatcher = null;
        }
        int i = 0;
        while (true) {
            AttachAlertLayout[] attachAlertLayoutArr = this.layouts;
            if (i >= attachAlertLayoutArr.length) {
                break;
            }
            AttachAlertLayout attachAlertLayout = attachAlertLayoutArr[i];
            if (attachAlertLayout != null) {
                attachAlertLayout.onDestroy();
            }
            i++;
        }
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.reloadInlineHints);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.attachMenuBotsDidLoad);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.quickRepliesUpdated);
        this.destroyed = true;
        EditTextEmoji editTextEmoji = this.commentTextView;
        if (editTextEmoji != null) {
            editTextEmoji.onDestroy();
        }
        EditTextEmoji editTextEmoji2 = this.topCommentTextView;
        if (editTextEmoji2 != null) {
            editTextEmoji2.onDestroy();
        }
        MentionsContainerView mentionsContainerView = this.mentionContainer;
        if (mentionsContainerView != null) {
            if (mentionsContainerView.getAdapter() != null) {
                this.mentionContainer.getAdapter().onDestroy();
            }
            this.mentionContainer.onDetachedFromWindow();
            this.mentionContainer = null;
        }
        PhotoViewer.getInstance().nullifyParentAlert(this);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void onOpenAnimationEnd() {
        if (this.baseFragment instanceof ChatActivity) {
            int i = MediaController.VIDEO_BITRATE_1080;
        } else {
            int i2 = MediaController.VIDEO_BITRATE_1080;
        }
        this.currentAttachLayout.onOpenAnimationEnd();
        AndroidUtilities.makeAccessibilityAnnouncement(LocaleController.getString("AccDescrAttachButton", R.string.AccDescrAttachButton));
        this.openTransitionFinished = true;
        if (this.videosEnabled || this.photosEnabled) {
            return;
        }
        checkCanRemoveRestrictionsByBoosts();
    }

    public void setAllowDrawContent(boolean z) {
        this.currentAttachLayout.onContainerTranslationUpdated(this.currentPanTranslationY);
        if (this.allowDrawContent != z) {
            this.allowDrawContent = z;
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
            if (attachAlertLayout != chatAttachAlertPhotoLayout || chatAttachAlertPhotoLayout == null || chatAttachAlertPhotoLayout.cameraExpanded) {
                return;
            }
            chatAttachAlertPhotoLayout.pauseCamera(!z || this.sent);
        }
    }

    public void setAvatarPicker(int i, boolean z, Utilities.Callback0Return<PhotoViewer.PlaceProviderObject> callback0Return) {
        this.avatarPicker = i;
        this.avatarSearch = z;
        this.avatarWithBulletin = callback0Return;
        if (i != 0) {
            this.typeButtonsAvailable = false;
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            if (attachAlertLayout == null || attachAlertLayout == this.photoLayout) {
                this.buttonsRecyclerViewWrapper.setVisibility(8);
            }
            int i2 = this.avatarPicker;
            TextView textView = this.selectedTextView;
            if (i2 == 2) {
                textView.setText(LocaleController.getString(R.string.ChoosePhotoOrVideo));
            } else {
                textView.setText(LocaleController.getString(R.string.ChoosePhoto));
            }
        } else {
            this.typeButtonsAvailable = true;
        }
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        if (chatAttachAlertPhotoLayout != null) {
            chatAttachAlertPhotoLayout.updateAvatarPicker();
        }
    }

    public void setStoryMediaPicker() {
        this.storyMediaPicker = true;
        this.typeButtonsAvailable = false;
        this.selectedTextView.setText(LocaleController.getString(R.string.ChoosePhotoOrVideo));
    }

    public void enableStickerMode(Utilities.Callback2<String, TLRPC.InputDocument> callback2) {
        this.selectedTextView.setText(LocaleController.getString(R.string.ChoosePhotoForSticker));
        this.typeButtonsAvailable = false;
        this.buttonsRecyclerViewWrapper.setVisibility(8);
        this.avatarPicker = 1;
        this.isPhotoPicker = true;
        this.isStickerMode = true;
        this.allowLivePhotos = false;
        this.customStickerHandler = callback2;
        if (this.optionsItem != null) {
            this.selectedTextView.setTranslationY(-AndroidUtilities.dp(8.0f));
            this.optionsItem.setVisibility(0);
            this.optionsItem.setClickable(true);
            this.optionsItem.setAlpha(1.0f);
            this.optionsItem.setScaleX(1.0f);
            this.optionsItem.setScaleY(1.0f);
        }
    }

    public void enablePollAttachMode(int i) {
        this.typeButtonsAvailable = true;
        this.buttonsRecyclerViewWrapper.setVisibility(0);
        this.isPollAttach = true;
        this.pollAllowedLayouts = i;
        this.avatarPicker = 0;
        this.isPhotoPicker = false;
        this.isStickerMode = false;
        this.customStickerHandler = null;
        if (this.optionsItem != null) {
            this.selectedTextView.setTranslationY(0.0f);
            this.optionsItem.setVisibility(8);
        }
    }

    public void setLocationActivityDelegate(ChatAttachAlertLocationLayout.LocationActivityDelegate locationActivityDelegate) {
        this.locationActivityDelegate = locationActivityDelegate;
    }

    public void enableDefaultMode() {
        this.typeButtonsAvailable = true;
        this.buttonsRecyclerViewWrapper.setVisibility(0);
        this.avatarPicker = 0;
        this.isPhotoPicker = false;
        this.isStickerMode = false;
        this.allowLivePhotos = true;
        this.customStickerHandler = null;
        if (this.optionsItem != null) {
            this.selectedTextView.setTranslationY(0.0f);
            this.optionsItem.setVisibility(8);
        }
    }

    public TextView getSelectedTextView() {
        return this.selectedTextView;
    }

    public int getTypeButtonsHeight() {
        if (this.typeButtonsAvailable) {
            return AndroidUtilities.dp(62.0f);
        }
        return 0;
    }

    public void setTypeButtonsHidden(final boolean z, boolean z2) {
        if (this.typeButtonsHidden == z) {
            return;
        }
        this.typeButtonsHidden = z;
        if (this.typeButtonsAvailable) {
            this.buttonsRecyclerViewWrapper.animate().cancel();
            if (!z) {
                this.buttonsRecyclerViewWrapper.setVisibility(0);
            }
            FrameLayout frameLayout = this.buttonsRecyclerViewWrapper;
            if (z2) {
                frameLayout.animate().alpha(z ? 0.0f : 1.0f).translationY(z ? AndroidUtilities.dp(48.0f) : 0.0f).setDuration(180L).withEndAction(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda67
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$setTypeButtonsHidden$67(z);
                    }
                }).start();
                return;
            }
            frameLayout.setAlpha(z ? 0.0f : 1.0f);
            this.buttonsRecyclerViewWrapper.setTranslationY(z ? AndroidUtilities.dp(48.0f) : 0.0f);
            this.buttonsRecyclerViewWrapper.setVisibility(z ? 4 : 0);
        }
    }

    public void lambda$dismiss$70(AlertDialog alertDialog, int i) {
        this.allowPassConfirmationAlert = true;
        lambda$new$0();
    }

    public /* synthetic */ void lambda$dismiss$71(DialogInterface dialogInterface) {
        SpringAnimation springAnimation = this.appearSpringAnimation;
        if (springAnimation != null) {
            springAnimation.cancel();
        }
        SpringAnimation springAnimation2 = new SpringAnimation(this.containerView, DynamicAnimation.TRANSLATION_Y, 0.0f);
        this.appearSpringAnimation = springAnimation2;
        springAnimation2.getSpring().setDampingRatio(1.5f);
        this.appearSpringAnimation.getSpring().setStiffness(1500.0f);
        this.appearSpringAnimation.start();
    }

    public /* synthetic */ void lambda$dismiss$72(DialogInterface dialogInterface) {
        this.confirmationAlertShown = false;
    }

    public /* synthetic */ void lambda$dismiss$73(int i) {
        this.navBarColorKey = -1;
        this.navBarColor = i;
        this.containerView.invalidate();
    }

    @Override // android.app.Dialog, android.view.KeyEvent.Callback
    public boolean onKeyDown(int i, KeyEvent keyEvent) {
        if (this.currentAttachLayout.onSheetKeyDown(i, keyEvent)) {
            return true;
        }
        return super.onKeyDown(i, keyEvent);
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void setAllowNestedScroll(boolean z) {
        this.allowNestedScroll = z;
    }

    public BaseFragment getBaseFragment() {
        return this.baseFragment;
    }

    public EditTextEmoji getCommentTextView() {
        return this.commentTextView;
    }

    public ChatAttachAlertDocumentLayout getDocumentLayout() {
        return this.documentLayout;
    }

    public void setAllowEnterCaption(boolean z) {
        this.allowEnterCaption = z;
    }

    public void setAudioSelectDelegate(ChatAttachAlertAudioLayout.AudioSelectDelegate audioSelectDelegate) {
        this.audioSelectDelegate = audioSelectDelegate;
    }

    public void setDocumentsDelegate(ChatAttachAlertDocumentLayout.DocumentSelectActivityDelegate documentSelectActivityDelegate) {
        this.documentsDelegate = documentSelectActivityDelegate;
    }

    public void replaceWithText(int i, int i2, CharSequence charSequence, boolean z) {
        if (getCommentView() == null) {
            return;
        }
        try {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(getCommentView().getText());
            spannableStringBuilder.replace(i, i2 + i, charSequence);
            if (z) {
                Emoji.replaceEmoji(spannableStringBuilder, getCommentView().getEditText().getPaint().getFontMetricsInt(), false);
            }
            getCommentView().setText(spannableStringBuilder);
            getCommentView().setSelection(i + charSequence.length());
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void createMentionsContainer() {
        MentionsContainerView mentionsContainerView = new MentionsContainerView(getContext(), this.dialogId, 0L, LaunchActivity.getLastFragment(), this.resourcesProvider) { // from class: org.telegram.ui.Components.ChatAttachAlert.37
            @Override // org.telegram.ui.Components.MentionsContainerView
            public void onScrolled(boolean z, boolean z2) {
                if (ChatAttachAlert.this.photoLayout != null) {
                    ChatAttachAlert.this.photoLayout.checkCameraViewPosition();
                }
            }

            @Override // org.telegram.ui.Components.MentionsContainerView
            public void onAnimationScroll() {
                if (ChatAttachAlert.this.photoLayout != null) {
                    ChatAttachAlert.this.photoLayout.checkCameraViewPosition();
                }
            }
        };
        this.mentionContainer = mentionsContainerView;
        setupMentionContainer(mentionsContainerView);
        MentionsContainerView.Delegate delegate = new MentionsContainerView.Delegate() { // from class: org.telegram.ui.Components.ChatAttachAlert.38
            @Override // org.telegram.ui.Components.MentionsContainerView.Delegate
            public void replaceText(int i, int i2, CharSequence charSequence, boolean z) {
                ChatAttachAlert.this.replaceWithText(i, i2, charSequence, z);
            }

            @Override // org.telegram.ui.Components.MentionsContainerView.Delegate
            public Paint.FontMetricsInt getFontMetrics() {
                return ChatAttachAlert.this.commentTextView.getEditText().getPaint().getFontMetricsInt();
            }
        };
        this.mentionsDelegate = delegate;
        this.mentionContainer.withDelegate(delegate);
        ViewGroup viewGroup = this.containerView;
        viewGroup.addView(this.mentionContainer, viewGroup.indexOfChild(this.frameLayout2), LayoutHelper.createFrame(-1, -1, 83));
        setupMentionContainer(this.mentionContainer);
        updateCommentTextViewPosition();
    }

    public void setupMentionContainer(MentionsContainerView mentionsContainerView) {
        mentionsContainerView.getAdapter().setAllowStickers(false);
        mentionsContainerView.getAdapter().setAllowBots(false);
        mentionsContainerView.getAdapter().setAllowChats(false);
        if (this.baseFragment instanceof ChatActivity) {
            mentionsContainerView.getAdapter().setSearchInDialogs(false);
            ChatActivity chatActivity = (ChatActivity) this.baseFragment;
            mentionsContainerView.getAdapter().setUserOrChat(chatActivity.getCurrentUser(), chatActivity.getCurrentChat());
            mentionsContainerView.getAdapter().setChatInfo(chatActivity.getCurrentChatInfo());
            mentionsContainerView.getAdapter().setNeedUsernames(chatActivity.getCurrentChat() != null);
        } else {
            mentionsContainerView.getAdapter().setSearchInDialogs(true);
            mentionsContainerView.getAdapter().setChatInfo(null);
            mentionsContainerView.getAdapter().setNeedUsernames(false);
        }
        mentionsContainerView.getAdapter().setNeedBotContext(false);
    }

    public void setCaptionAbove(boolean z) {
        setCaptionAbove(z, true);
    }

    public void setCaptionAbove(boolean z, boolean z2) {
        this.animatorCaptionAbove.setValue(z, z2);
        EditTextEmoji commentView = getCommentView();
        this.captionAbove = z;
        EditTextEmoji commentView2 = getCommentView();
        final boolean z3 = this.frameLayout2.getTag() != null;
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        final boolean z4 = this.captionAbove && (attachAlertLayout == this.photoLayout || attachAlertLayout == this.photoPreviewLayout);
        FrameLayout frameLayout = this.topCommentContainer;
        if (z2) {
            frameLayout.setVisibility(z3 ? 0 : 8);
            ViewPropertyAnimator duration = this.topCommentContainer.animate().alpha((z4 && z3) ? 1.0f : 0.0f).setDuration(320L);
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            duration.setInterpolator(cubicBezierInterpolator).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda9
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$setCaptionAbove$74(valueAnimator);
                }
            }).withEndAction(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda10
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setCaptionAbove$75(z4, z3);
                }
            }).start();
            this.captionContainer.setVisibility(0);
            this.captionContainer.animate().translationY((z4 || !z3) ? this.captionContainer.getMeasuredHeight() : 0.0f).alpha((z4 || !z3) ? 0.0f : 1.0f).setDuration(320L).setInterpolator(cubicBezierInterpolator).setUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda11
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$setCaptionAbove$76(valueAnimator);
                }
            }).withEndAction(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda12
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setCaptionAbove$77(z4, z3);
                }
            }).start();
        } else {
            frameLayout.setVisibility((z4 && z3) ? 0 : 8);
            this.topCommentContainer.setAlpha((z4 && z3) ? 1.0f : 0.0f);
            updatedTopCaptionHeight();
            this.captionContainer.setAlpha((z4 || !z3) ? 0.0f : 1.0f);
            FrameLayout frameLayout2 = this.captionContainer;
            frameLayout2.setTranslationY((z4 || !z3) ? frameLayout2.getMeasuredHeight() : 0.0f);
            this.captionContainer.setVisibility((z4 || !z3) ? 8 : 0);
        }
        if (commentView != commentView2) {
            commentView.hidePopup(true);
            commentView2.setText(AnimatedEmojiSpan.cloneSpans(commentView.getText()));
            commentView2.getEditText().setAllowTextEntitiesIntersection(commentView.getEditText().getAllowTextEntitiesIntersection());
            if (commentView.getEditText().isFocused()) {
                commentView2.getEditText().requestFocus();
                commentView2.getEditText().setSelection(commentView.getEditText().getSelectionStart(), commentView.getEditText().getSelectionEnd());
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlert$$ExternalSyntheticLambda13
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setCaptionAbove$78();
            }
        });
    }

    public /* synthetic */ void lambda$setCaptionAbove$74(ValueAnimator valueAnimator) {
        updatedTopCaptionHeight();
    }

    public /* synthetic */ void lambda$setCaptionAbove$75(boolean z, boolean z2) {
        if (!z || !z2) {
            this.topCommentContainer.setVisibility(8);
        }
        updatedTopCaptionHeight();
    }

    public /* synthetic */ void lambda$setCaptionAbove$76(ValueAnimator valueAnimator) {
        this.frameLayout2.invalidate();
    }

    public /* synthetic */ void lambda$setCaptionAbove$77(boolean z, boolean z2) {
        if (z || !z2) {
            this.captionContainer.setVisibility(8);
        }
    }

    public /* synthetic */ void lambda$setCaptionAbove$78() {
        EditTextEmoji editTextEmoji = this.captionAbove ? this.topCommentTextView : this.commentTextView;
        showAiButton(editTextEmoji.getEditText().getLineCount() > 2 && !TextUtils.isEmpty(editTextEmoji.getText().toString().trim()));
    }

    public void updatedTopCaptionHeight() {
        updateSelectedPosition(0);
        this.sizeNotifierFrameLayout.invalidate();
        this.topCommentContainer.invalidate();
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        if (chatAttachAlertPhotoLayout != null) {
            chatAttachAlertPhotoLayout.checkCameraViewPosition();
            RecyclerListView recyclerListView = this.photoLayout.gridView;
            if (recyclerListView != null && recyclerListView.getFastScroll() != null) {
                this.photoLayout.gridView.getFastScroll().topOffset = ActionBar.getCurrentActionBarHeight() + this.photoLayout.listAdditionalH + (this.captionAbove ? (int) (this.topCommentContainer.getMeasuredHeight() * this.topCommentContainer.getAlpha()) : 0);
                this.photoLayout.gridView.getFastScroll().invalidate();
            }
        }
        updateCommentTextViewPosition();
        checkUi_writeButtonContainerY();
    }

    private void toggleCaptionAbove() {
        setCaptionAbove(!this.captionAbove);
    }

    /* JADX WARN: Code duplicated, block: B:27:0x004f  */
    public void checkIsEphemeralMessage(boolean z) {
        boolean z2;
        BaseFragment baseFragment = this.baseFragment;
        if (baseFragment == null || !(baseFragment instanceof ChatActivity)) {
            return;
        }
        ChatActivity chatActivity = (ChatActivity) baseFragment;
        EditTextEmoji editTextEmoji = this.captionAbove ? this.topCommentTextView : this.commentTextView;
        String string = editTextEmoji != null ? editTextEmoji.getText().toString() : null;
        if (this.editingMessageObject == null) {
            AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
            z2 = true;
            if ((attachAlertLayout != null && attachAlertLayout.getSelectedItemsCount() > 1) || (!EphemeralMessagesHelper.getInstance(this.currentAccount).isEphemeralCommand(string, chatActivity.botInfo) && (chatActivity.getReplyMessage() == null || !chatActivity.getReplyMessage().isEphemeral()))) {
                z2 = false;
            }
        } else {
            z2 = false;
        }
        this.animatorEphemeralMessageVisibility.setValue(z2, z);
        if (z2 && isCaptionAbove()) {
            setCaptionAbove(false, z);
        }
    }

    public void blur3_InvalidateBlur() {
        boolean z;
        if (Build.VERSION.SDK_INT < 31 || this.scrollableViewNoiseSuppressor == null) {
            return;
        }
        ViewPositionWatcher.computeRectInParent(this.buttonsRecyclerViewWrapper, this.containerView, this.iBlur3PositionMainTabs);
        this.iBlur3PositionActionBar.set(0.0f, 0.0f, this.containerView.getMeasuredWidth(), this.actionBar.getMeasuredHeight());
        this.iBlur3PositionActionBar.inset(0.0f, -AndroidUtilities.dp(48.0f));
        this.iBlur3PositionMainTabs.set(0.0f, this.containerView.getMeasuredHeight() - (Math.max(AndroidUtilities.navigationBarHeight, getEmojiPadding()) + AndroidUtilities.dp(180.0f)), this.containerView.getMeasuredWidth(), this.containerView.getMeasuredHeight());
        this.iBlur3PositionMainTabs.inset(0.0f, LiteMode.isEnabled(262144) ? 0.0f : -AndroidUtilities.dp(48.0f));
        AttachAlertLayout attachAlertLayout = this.currentAttachLayout;
        ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = this.photoLayout;
        if (attachAlertLayout != chatAttachAlertPhotoLayout || chatAttachAlertPhotoLayout == null || chatAttachAlertPhotoLayout.gridView.getFastScroll() == null) {
            z = false;
        } else {
            this.photoLayout.gridView.getFastScroll().fillDrawablesRect(this.iBlur3PositionFastScroll);
            RecyclerListView.FastScroll fastScroll = this.photoLayout.gridView.getFastScroll();
            ViewGroup viewGroup = this.containerView;
            RectF rectF = AndroidUtilities.rectTmp;
            ViewPositionWatcher.computeRectInParent(fastScroll, viewGroup, rectF);
            this.iBlur3PositionFastScroll.offset(rectF.left, rectF.top);
            this.iBlur3PositionFastScroll.inset(-AndroidUtilities.dp(48.0f), -AndroidUtilities.dp(48.0f));
            RectF rectF2 = this.iBlur3PositionFastScroll;
            rectF2.left = Math.max(0.0f, rectF2.left);
            this.iBlur3PositionFastScroll.right = Math.min(this.containerView.getMeasuredWidth(), this.iBlur3PositionFastScroll.right);
            z = true;
        }
        this.scrollableViewNoiseSuppressor.setupRenderNodes(this.iBlur3PositionsMerged, RectFMergeBounding.mergeOverlapping(this.iBlur3Positions, z ? 3 : 2, this.iBlur3PositionsMerged));
        this.scrollableViewNoiseSuppressor.invalidateResultRenderNodes(this.iBlur3Capture, this.containerView.getMeasuredWidth(), this.containerView.getMeasuredHeight());
    }

    @SuppressLint({"ViewConstructor"})
    public static class SearchFadeView extends View {
        private final int bgKeyColor;
        private final GradientProtectionDrawable gradientProtectionDrawable;
        private final GradientProtectionDrawable gradientProtectionDrawable2;
        private final Theme.ResourcesProvider resourcesProvider;

        public SearchFadeView(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.gradientProtectionDrawable = new GradientProtectionDrawable(2);
            this.gradientProtectionDrawable2 = new GradientProtectionDrawable(2);
            this.resourcesProvider = resourcesProvider;
            this.bgKeyColor = i;
        }

        @Override // android.view.View
        public void onSizeChanged(int i, int i2, int i3, int i4) {
            super.onSizeChanged(i, i2, i3, i4);
            int i5 = AndroidUtilities.statusBarHeight;
            this.gradientProtectionDrawable.setInsets(0, AndroidUtilities.dp(12.0f) + i5, 0, 0);
            this.gradientProtectionDrawable.setBounds(0, 0, i, AndroidUtilities.dp(52.0f) + i5);
            this.gradientProtectionDrawable2.setInsets(0, i5 / 3, 0, 0);
            this.gradientProtectionDrawable2.setBounds(0, 0, i, i5);
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            this.gradientProtectionDrawable.setColor(Theme.multAlpha(Theme.getColor(this.bgKeyColor, this.resourcesProvider), 0.5f));
            this.gradientProtectionDrawable.draw(canvas);
            this.gradientProtectionDrawable2.setColor(Theme.multAlpha(Theme.getColor(this.bgKeyColor, this.resourcesProvider), 0.95f));
            this.gradientProtectionDrawable2.draw(canvas);
        }
    }

    public void onPollAttachFilePicker(Intent intent) {
        ChatAttachAlertPollLayout chatAttachAlertPollLayout = this.pollLayout;
        if (chatAttachAlertPollLayout != null) {
            chatAttachAlertPollLayout.onPollAttachFilePicker(intent);
        }
    }

    public void checkUi_fadeTopAlpha() {
        int alpha;
        if (this.fadeView == null || this.actionBar == null) {
            return;
        }
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        boolean zIsDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
        ChatActivityFadeView chatActivityFadeView = this.fadeView;
        if (this.actionBar.getVisibility() == 0) {
            alpha = (int) ((zIsDark ? 255 : 160) * this.actionBar.getAlpha());
        } else {
            alpha = 0;
        }
        chatActivityFadeView.setFadeTopAlpha(alpha);
    }

    @SuppressLint({"ViewConstructor"})
    public static class AttachSearchField extends FragmentSearchField {
        private final ChatAttachAlert parentAlert;

        @Override // org.telegram.ui.Components.FragmentSearchField, org.telegram.ui.ActionBar.Theme.Colorable
        public /* bridge */ /* synthetic */ int[] getColorKeys() {
            return super.getColorKeys();
        }

        public AttachSearchField(Context context, ChatAttachAlert chatAttachAlert, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
            this.parentAlert = chatAttachAlert;
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            this.parentAlert.makeFocusable(this.editText, true);
            return super.onInterceptTouchEvent(motionEvent);
        }
    }
}
