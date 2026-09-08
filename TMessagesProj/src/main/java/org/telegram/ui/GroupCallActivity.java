package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RadialGradient;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.collection.LongSparseArray;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListUpdateCallback;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.IconPackType;
import com.exteragram.messenger.icons.IconManager;
import com.google.android.gms.cast.CastStatusCodes;
import com.google.android.gms.cast.MediaError;
import com.google.android.material.timepicker.TimeModel;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import kotlin.jvm.internal.CharCompanionObject;
import kotlin.time.DurationKt;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotFullscreenButtons$$ExternalSyntheticApiModelOutline0;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ChatThemeController$$ExternalSyntheticLambda6;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmojiData;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.pip.PipSource;
import org.telegram.messenger.pip.utils.PipUtils;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.voip.ConferenceCall;
import org.telegram.messenger.voip.GroupCallMessage;
import org.telegram.messenger.voip.GroupCallMessagesController;
import org.telegram.messenger.voip.NativeInstance;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.messenger.voip.VoipAudioManager;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_phone;
import org.telegram.tgnet.tl.TL_update;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.AccountSelectCell;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.GroupCallInvitedCell;
import org.telegram.ui.Cells.GroupCallTextCell;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlobDrawable;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CheckBoxSquare;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.FillLastGridLayoutManager;
import org.telegram.ui.Components.GroupCallFullscreenAdapter;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.GroupCallRecordAlert;
import org.telegram.ui.Components.GroupVoipInviteAlert;
import org.telegram.ui.Components.HintView;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.NumberPicker;
import org.telegram.ui.Components.PermissionRequest;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.boosts.UserSelectorBottomSheet;
import org.telegram.ui.Components.ProfileGalleryView;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow;
import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.ReactionsContainerLayout;
import org.telegram.ui.Components.RecordStatusDrawable;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.conference.GroupCallActivityButtonsLayout;
import org.telegram.ui.Components.conference.message.GroupCallMessageCell;
import org.telegram.ui.Components.conference.message.GroupCallMessagesListView;
import org.telegram.ui.Components.inset.WindowInsetsStateHolder;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.Components.voip.GroupCallGridCell;
import org.telegram.ui.Components.voip.GroupCallMiniTextureView;
import org.telegram.ui.Components.voip.GroupCallRenderersContainer;
import org.telegram.ui.Components.voip.GroupCallStatusIcon;
import org.telegram.ui.Components.voip.PrivateVideoPreviewDialog;
import org.telegram.ui.Components.voip.RTMPStreamPipOverlay;
import org.telegram.ui.Components.voip.VoIPTextureView;
import org.telegram.ui.Components.voip.VoIPToggleButton;
import org.telegram.ui.Stories.recorder.DominantColors;
import org.webrtc.MediaStreamTrack;
import org.webrtc.voiceengine.WebRtcAudioTrack;

public class GroupCallActivity extends BottomSheet implements NotificationCenter.NotificationCenterDelegate, VoIPService.StateListener, FactorAnimator.Target {
    public static GroupCallActivity groupCallInstance;
    public static boolean groupCallUiVisible;
    public static boolean isLandscapeMode;
    public static boolean isTabletMode;
    public static boolean paused;
    public static volatile DispatchQueue updateTextureLightningQueue = new DispatchQueue("updateTextureLightningQueue");
    private final AccountInstance accountInstance;
    private final AccountSelectCell accountSelectCell;
    private final ActionBar actionBar;
    private AnimatorSet actionBarAnimation;
    private final View actionBarBackground;
    private final View actionBarShadow;
    ObjectAnimator additionalSubtitleYAnimator;
    private ActionBarMenuSubItem adminItem;
    private float amplitude;
    private float animateAmplitudeDiff;
    private float animateToAmplitude;
    private boolean animatingToFullscreenExpand;
    private final BoolAnimator animatorHasVideo;
    private final BoolAnimator animatorHideButtons;
    private final FactorAnimator animatorMessageInputHeight;
    private final BoolAnimator animatorMessageIsEmpty;
    private boolean anyEnterEventSent;
    private final ArrayList<GroupCallMiniTextureView> attachedRenderers;
    private final ArrayList<GroupCallMiniTextureView> attachedRenderersTmp;
    private final AvatarPreviewPagerIndicator avatarPagerIndicator;
    private final FrameLayout avatarPreviewContainer;
    private boolean avatarPriviewTransitionInProgress;
    AvatarUpdaterDelegate avatarUpdaterDelegate;
    private boolean avatarsPreviewShowed;
    private final ProfileGalleryView avatarsViewPager;
    private int backgroundColor;
    private final RLottieDrawable bigMicDrawable;
    private final BlobDrawable bigWaveDrawable;
    private final View blurredView;
    private final FrameLayout bulletinContainer;
    private GradientDrawable buttonsBackgroundGradient;
    private final View buttonsBackgroundGradientView;
    private final View buttonsBackgroundGradientView2;
    private final GroupCallActivityButtonsLayout buttonsContainer;
    private int buttonsVisibility;
    private Integer cacheAudioOutputValue;
    public ChatObject.Call call;
    private boolean callInitied;
    private final FrameLayout callMessageEnterContainer;
    private final Paint callMessageEnterContainerBgPaint;
    private final FrameLayout callMessageEnterUnderContainer;
    private final EditTextEmoji callMessageEnterView;
    private final ImageView callMessageHideButton;
    private final ImageView callMessageSendButton;
    private final VoIPToggleButton cameraButton;
    public CellFlickerDrawable cellFlickerDrawable;
    private boolean changingPermissions;
    private float colorProgress;
    private final int[] colorsTmp;
    private boolean contentFullyOverlayed;
    private long creatingServiceTime;
    ImageUpdater currentAvatarUpdater;
    private int currentCallState;
    public TLRPC.Chat currentChat;
    private ViewGroup currentOptionsLayout;
    private WeavingState currentState;
    private boolean delayedGroupCallUpdated;
    private DiffUtil.Callback diffUtilsCallback;
    private final ActionBarMenuSubItem disableComments;
    private boolean drawSpeakingSubtitle;
    public boolean drawingForBlur;
    private final ActionBarMenuSubItem editTitleItem;
    private final ActionBarMenuSubItem enableComments;
    private CallEncryptionCellDrawable encryptionDrawable;
    private boolean enterEventSent;
    private ActionBarMenuSubItem everyoneItem;
    private final ImageView expandOrMinimizeButton;
    private final VoIPToggleButton flipButton;
    private final RLottieDrawable flipIcon;
    private int flipIconCurrentEndFrame;
    GroupCallFullscreenAdapter fullscreenAdapter;
    private final DefaultItemAnimator fullscreenListItemAnimator;
    RecyclerListView fullscreenUsersListView;
    private final int[] gradientColors;
    private final GroupCallMessagesListView groupCallMessagesListView;
    private GroupVoipInviteAlert groupVoipInviteAlert;
    private final RLottieDrawable handDrawables;
    private boolean hasScrimAnchorView;
    private boolean hasVideo;
    private boolean invalidateColors;
    private final ActionBarMenuSubItem inviteItem;
    private String[] invites;
    private boolean isInDrawRenderNodeBlur;
    private boolean isInFullscreen;
    private final GroupCallItemAnimator itemAnimator;
    private long lastUpdateTime;
    private final FillLastGridLayoutManager layoutManager;
    private final VoIPToggleButton leaveButton;
    private final ActionBarMenuSubItem leaveItem;
    private final AnimatedTextView limitTextView;
    private final ListAdapter listAdapter;
    private RecyclerListView listView;
    private final Paint listViewBackgroundPaint;
    private boolean listViewVideoVisibility;
    private ValueAnimator liveLabelBgColorAnimator;
    private final Paint liveLabelPaint;
    private final TextView liveLabelTextView;
    private final int maxGroupCallMessageLength;
    private final LinearLayout menuItemsContainer;
    private final VoIPToggleButton messageButton;
    private final VoIPToggleButton muteButton;
    private final RLottieImageView muteButtonIcon;
    private int muteButtonState;
    private boolean needTextureLightning;
    private final ActionBarMenuSubItem noiseItem;
    private int oldAddMemberRow;
    private int oldConferenceAddPeopleRow;
    private int oldConferenceShareLinkRow;
    private int oldCount;
    private int oldEncryptionRow;
    private final ArrayList<Long> oldInvited;
    private int oldInvitedEndRow;
    private int oldInvitedStartRow;
    private final ArrayList<TLRPC.GroupCallParticipant> oldParticipants;
    private final ArrayList<Long> oldShadyJoin;
    private int oldShadyJoinEndRow;
    private int oldShadyJoinStartRow;
    private final ArrayList<Long> oldShadyLeft;
    private int oldShadyLeftEndRow;
    private int oldShadyLeftStartRow;
    private int oldUsersEndRow;
    private int oldUsersStartRow;
    private int oldUsersVideoEndRow;
    private int oldUsersVideoStartRow;
    private int oldVideoDividerRow;
    private int oldVideoNotAvailableRow;
    private final ArrayList<ChatObject.VideoParticipant> oldVideoParticipants;
    private final Runnable onUserLeaveHintListener;
    private final ActionBarMenuItem otherItem;
    private Paint paint;
    private Paint paintTmp;
    private LaunchActivity parentActivity;
    private Boolean pendingCommentsEnabled;
    private final ActionBarMenuSubItem permissionItem;
    PinchToZoomHelper pinchToZoomHelper;
    private final ActionBarMenuItem pipItem;
    private PipSource pipSource;
    private boolean playingHandAnimation;
    private int popupAnimationIndex;
    private final Runnable pressRunnable;
    private boolean pressed;
    private WeavingState prevState;
    PrivateVideoPreviewDialog previewDialog;
    private boolean previewTextureTransitionEnabled;
    private float progressToAvatarPreview;
    private RadialGradient radialGradient;
    private final Matrix radialMatrix;
    private final Paint radialPaint;
    private final RadialProgressView radialProgressView;
    private final ImageReceiver reactionEffectImageReceiver;
    private ReactionsContainerLayout reactionsContainerLayout;
    private RecordCallDrawable recordCallDrawable;
    private HintView recordHintView;
    private final ActionBarMenuSubItem recordItem;
    private final RectF rect;
    private HintView reminderHintView;
    private RenderNode renderNodeBlur;
    private float renderNodeBlurScale;
    private GroupCallRenderersContainer renderersContainer;
    ViewTreeObserver.OnPreDrawListener requestFullscreenListener;
    private ValueAnimator scheduleAnimator;
    private TextView scheduleButtonTextView;
    private final boolean scheduleHasFewPeers;
    private TextView scheduleInfoTextView;
    private TLRPC.InputPeer schedulePeer;
    private int scheduleStartAt;
    private SimpleTextView scheduleStartAtTextView;
    private SimpleTextView scheduleStartInTextView;
    private SimpleTextView scheduleTimeTextView;
    private LinearLayout scheduleTimerContainer;
    private boolean scheduled;
    private final String scheduledHash;
    private final ActionBarMenuSubItem screenItem;
    private final ActionBarMenuItem screenShareItem;
    private AnimatorSet scrimAnimatorSet;
    private GroupCallFullscreenAdapter.GroupCallUserCell scrimFullscreenView;
    private GroupCallGridCell scrimGridView;
    private Paint scrimPaint;
    private View scrimPopupLayout;
    private ActionBarPopupWindow scrimPopupWindow;
    private GroupCallMiniTextureView scrimRenderer;
    private GroupCallUserCell scrimView;
    private boolean scrimViewAttached;
    private float scrollOffsetY;
    private TLRPC.Peer selfPeer;
    private final Drawable shadowDrawable;
    private ShareAlert shareAlert;
    private float showLightingProgress;
    private float showWavesProgress;
    private final SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private final VoIPToggleButton soundButton;
    private ActionBarMenuSubItem soundItem;
    private final View soundItemDivider;
    private final GridLayoutManager.SpanSizeLookup spanSizeLookup;
    private final VoIPToggleButton speakerButton;
    private int speakerIcon;
    private final ImageView speakerImageView;
    private boolean startingGroupCall;
    private WeavingState[] states;
    public final ArrayList<GroupCallStatusIcon> statusIconPool;
    ObjectAnimator subtitleYAnimator;
    private float switchProgress;
    private float switchToButtonInt2;
    private float switchToButtonProgress;
    GroupCallTabletGridAdapter tabletGridAdapter;
    RecyclerListView tabletVideoGridView;
    private LightningView textureLightningView;
    private float textureLightningViewAnimatingAlpha;
    private final BlobDrawable tinyWaveDrawable;
    private final LinearLayout titleLayout;
    private final AudioPlayerAlert.ClippingTextViewSwitcher titleTextView;
    private final UndoView[] undoView;
    private final Runnable unmuteRunnable;
    private Runnable updateCallRecordRunnable;
    private final Runnable updateSchedeulRunnable;
    private final Runnable updateTextureLightningRunnable;
    private boolean useBlur;
    private TLObject userSwitchObject;
    LongSparseIntArray visiblePeerIds;
    public final ArrayList<ChatObject.VideoParticipant> visibleVideoParticipants;
    private Boolean wasExpandBigSize;
    private Boolean wasNotInLayoutFullscreen;
    private WatchersView watchersView;
    private final WindowInsetsStateHolder windowInsetsStateHolder;

    public static void lambda$didReceivedNotification$1() {
        if (isStillConnecting()) {
            updateState(true, false);
        }
    }

    public void lambda$updateItems$3() {
        updateRecordCallText();
        AndroidUtilities.runOnUIThread(this.updateCallRecordRunnable, 1000L);
    }

    public void makeFocusable(final BottomSheet bottomSheet, final AlertDialog alertDialog, final EditTextBoldCursor editTextBoldCursor, final boolean z) {
        if (this.enterEventSent) {
            return;
        }
        BaseFragment baseFragment = this.parentActivity.getActionBarLayout().getFragmentStack().get(this.parentActivity.getActionBarLayout().getFragmentStack().size() - 1);
        if (baseFragment instanceof ChatActivity) {
            boolean zNeedEnterText = ((ChatActivity) baseFragment).needEnterText();
            this.enterEventSent = true;
            this.anyEnterEventSent = true;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda56
                @Override // java.lang.Runnable
                public final void run() {
                    GroupCallActivity.$r8$lambda$Fa3ZPhFXyZdUSkEgn5plm6EMoSA(bottomSheet, editTextBoldCursor, z, alertDialog);
                }
            }, zNeedEnterText ? 200L : 0L);
            return;
        }
        this.enterEventSent = true;
        this.anyEnterEventSent = true;
        if (bottomSheet != null) {
            bottomSheet.setFocusable(true);
        } else if (alertDialog != null) {
            alertDialog.setFocusable(true);
        }
        if (z) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda57
                @Override // java.lang.Runnable
                public final void run() {
                    GroupCallActivity.m15133$r8$lambda$93eSzpgS8XVzTm6hjP7Nm8Wk7U(editTextBoldCursor);
                }
            }, 100L);
        }
    }

    public static @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                GroupCallActivity groupCallActivity2;
                Canvas canvas2;
                Canvas canvas3;
                GroupCallUserCell groupCallUserCell;
                float y;
                float left;
                GroupCallUserCell groupCallUserCell2;
                float left2;
                float top;
                float left3;
                float top2;
                int i5 = 0;
                if (!GroupCallActivity.this.isInDrawRenderNodeBlur && !GroupCallActivity.this.drawingForBlur && Build.VERSION.SDK_INT >= 31 && canvas.isHardwareAccelerated() && !AndroidUtilities.makingGlobalBlurBitmap) {
                    if (GroupCallActivity.this.renderNodeBlur == null) {
                        GroupCallActivity.this.renderNodeBlur = BotFullscreenButtons$$ExternalSyntheticApiModelOutline0.m("CallActivity.Blur");
                        GroupCallActivity.this.renderNodeBlurScale = SizeNotifierFrameLayout.getRenderNodeScale();
                        ColorMatrix colorMatrix = new ColorMatrix(new float[]{0.5f, 0.0f, 0.0f, 0.0f, 8.5f, 0.0f, 0.5f, 0.0f, 0.0f, 8.5f, 0.0f, 0.0f, 0.5f, 0.0f, 8.5f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f});
                        float blurRadius = SizeNotifierFrameLayout.getBlurRadius();
                        GroupCallActivity.this.renderNodeBlur.setRenderEffect(RenderEffect.createChainEffect(RenderEffect.createBlurEffect(blurRadius, blurRadius, Shader.TileMode.DECAL), RenderEffect.createColorFilterEffect(new ColorMatrixColorFilter(colorMatrix))));
                        GroupCallActivity.this.groupCallMessagesListView.setBlurRoot(this);
                        GroupCallActivity.this.groupCallMessagesListView.setRenderNode(GroupCallActivity.this.renderNodeBlur, GroupCallActivity.this.renderNodeBlurScale);
                    }
                    int iRound = Math.round(getMeasuredWidth() / GroupCallActivity.this.renderNodeBlurScale);
                    int iRound2 = Math.round(getMeasuredHeight() / GroupCallActivity.this.renderNodeBlurScale);
                    GroupCallActivity.this.isInDrawRenderNodeBlur = true;
                    GroupCallActivity.this.renderNodeBlur.setPosition(0, 0, iRound, iRound2);
                    RecordingCanvas recordingCanvasBeginRecording = GroupCallActivity.this.renderNodeBlur.beginRecording();
                    recordingCanvasBeginRecording.scale(1.0f / GroupCallActivity.this.renderNodeBlurScale, 1.0f / GroupCallActivity.this.renderNodeBlurScale);
                    dispatchDraw(recordingCanvasBeginRecording);
                    GroupCallActivity.this.renderNodeBlur.endRecording();
                    GroupCallActivity.this.isInDrawRenderNodeBlur = false;
                }
                int i6 = 0;
                while (true) {
                    int childCount = GroupCallActivity.this.listView.getChildCount();
                    groupCallActivity2 = GroupCallActivity.this;
                    if (i6 >= childCount) {
                        break;
                    }
                    View childAt = groupCallActivity2.listView.getChildAt(i6);
                    if (childAt instanceof GroupCallUserCell) {
                        ((GroupCallUserCell) childAt).setDrawAvatar(true);
                    }
                    if (!(childAt instanceof GroupCallGridCell)) {
                        if (childAt.getMeasuredWidth() != GroupCallActivity.this.listView.getMeasuredWidth()) {
                            childAt.setTranslationX((GroupCallActivity.this.listView.getMeasuredWidth() - childAt.getMeasuredWidth()) >> 1);
                        } else {
                            childAt.setTranslationX(0.0f);
                        }
                    }
                    i6++;
                }
                if (groupCallActivity2.renderersContainer.isAnimating()) {
                    if (GroupCallActivity.this.fullscreenUsersListView.getVisibility() == 0) {
                        this.listCells.clear();
                        for (int i7 = 0; i7 < GroupCallActivity.this.listView.getChildCount(); i7++) {
                            View childAt2 = GroupCallActivity.this.listView.getChildAt(i7);
                            if (childAt2.isAttachedToWindow()) {
                                if ((childAt2 instanceof GroupCallGridCell) && GroupCallActivity.this.listView.getChildAdapterPosition(childAt2) >= 0) {
                                    GroupCallGridCell groupCallGridCell = (GroupCallGridCell) childAt2;
                                    if (groupCallGridCell.getRenderer() != GroupCallActivity.this.renderersContainer.fullscreenTextureView) {
                                        this.listCells.put(groupCallGridCell.getParticipant(), childAt2);
                                    }
                                } else if ((childAt2 instanceof GroupCallUserCell) && GroupCallActivity.this.listView.getChildAdapterPosition(childAt2) >= 0) {
                                    GroupCallUserCell groupCallUserCell3 = (GroupCallUserCell) childAt2;
                                    this.listCells.put(groupCallUserCell3.getParticipant(), groupCallUserCell3);
                                }
                            }
                        }
                        for (int i8 = 0; i8 < GroupCallActivity.this.fullscreenUsersListView.getChildCount(); i8++) {
                            GroupCallFullscreenAdapter.GroupCallUserCell groupCallUserCell4 = (GroupCallFullscreenAdapter.GroupCallUserCell) GroupCallActivity.this.fullscreenUsersListView.getChildAt(i8);
                            View view = this.listCells.get(groupCallUserCell4.getVideoParticipant());
                            if (view == null) {
                                view = this.listCells.get(groupCallUserCell4.getParticipant());
                            }
                            float f = GroupCallActivity.this.renderersContainer.progressToFullscreenMode;
                            if (!GroupCallActivity.this.fullscreenListItemAnimator.isRunning()) {
                                groupCallUserCell4.setAlpha(1.0f);
                            }
                            if (view != null) {
                                if (view instanceof GroupCallGridCell) {
                                    GroupCallGridCell groupCallGridCell2 = (GroupCallGridCell) view;
                                    left2 = (groupCallGridCell2.getLeft() + GroupCallActivity.this.listView.getX()) - GroupCallActivity.this.renderersContainer.getLeft();
                                    top = (groupCallGridCell2.getTop() + GroupCallActivity.this.listView.getY()) - GroupCallActivity.this.renderersContainer.getTop();
                                    left3 = groupCallUserCell4.getLeft() + GroupCallActivity.this.fullscreenUsersListView.getX();
                                    top2 = groupCallUserCell4.getTop() + GroupCallActivity.this.fullscreenUsersListView.getY();
                                } else {
                                    GroupCallUserCell groupCallUserCell5 = (GroupCallUserCell) view;
                                    left2 = ((groupCallUserCell5.getLeft() + GroupCallActivity.this.listView.getX()) - GroupCallActivity.this.renderersContainer.getLeft()) + groupCallUserCell5.getAvatarImageView().getLeft() + (groupCallUserCell5.getAvatarImageView().getMeasuredWidth() >> 1);
                                    float top3 = ((groupCallUserCell5.getTop() + GroupCallActivity.this.listView.getY()) - GroupCallActivity.this.renderersContainer.getTop()) + groupCallUserCell5.getAvatarImageView().getTop() + (groupCallUserCell5.getAvatarImageView().getMeasuredHeight() >> 1);
                                    float left4 = groupCallUserCell4.getLeft() + GroupCallActivity.this.fullscreenUsersListView.getX() + (groupCallUserCell4.getMeasuredWidth() >> 1);
                                    float top4 = groupCallUserCell4.getTop() + GroupCallActivity.this.fullscreenUsersListView.getY() + (groupCallUserCell4.getMeasuredHeight() >> 1);
                                    groupCallUserCell5.setDrawAvatar(false);
                                    top = top3;
                                    left3 = left4;
                                    top2 = top4;
                                }
                                float f2 = left2 - left3;
                                float f3 = 1.0f - f;
                                groupCallUserCell4.setTranslationX(f2 * f3);
                                groupCallUserCell4.setTranslationY((top - top2) * f3);
                                groupCallUserCell4.setScaleX(1.0f);
                                groupCallUserCell4.setScaleY(1.0f);
                                groupCallUserCell4.setProgressToFullscreen(f);
                            } else {
                                groupCallUserCell4.setScaleX(1.0f);
                                groupCallUserCell4.setScaleY(1.0f);
                                groupCallUserCell4.setTranslationX(0.0f);
                                groupCallUserCell4.setTranslationY(0.0f);
                                groupCallUserCell4.setProgressToFullscreen(1.0f);
                                if (groupCallUserCell4.getRenderer() == null) {
                                    groupCallUserCell4.setAlpha(f);
                                }
                            }
                        }
                    }
                } else {
                    for (int i9 = 0; i9 < GroupCallActivity.this.fullscreenUsersListView.getChildCount(); i9++) {
                        ((GroupCallFullscreenAdapter.GroupCallUserCell) GroupCallActivity.this.fullscreenUsersListView.getChildAt(i9)).setProgressToFullscreen(1.0f);
                    }
                }
                for (int i10 = 0; i10 < GroupCallActivity.this.attachedRenderers.size(); i10++) {
                    GroupCallMiniTextureView groupCallMiniTextureView = (GroupCallMiniTextureView) GroupCallActivity.this.attachedRenderers.get(i10);
                    RecyclerListView recyclerListView = GroupCallActivity.this.listView;
                    GroupCallActivity groupCallActivity3 = GroupCallActivity.this;
                    groupCallMiniTextureView.updatePosition(recyclerListView, groupCallActivity3.tabletVideoGridView, groupCallActivity3.fullscreenUsersListView, groupCallActivity3.renderersContainer);
                }
                boolean z2 = GroupCallActivity.isTabletMode;
                GroupCallActivity groupCallActivity4 = GroupCallActivity.this;
                if (!z2) {
                    groupCallActivity4.buttonsBackgroundGradientView.setAlpha(1.0f - GroupCallActivity.this.renderersContainer.progressToFullscreenMode);
                    GroupCallActivity.this.buttonsBackgroundGradientView2.setAlpha(1.0f - GroupCallActivity.this.renderersContainer.progressToFullscreenMode);
                } else {
                    groupCallActivity4.buttonsBackgroundGradientView.setAlpha(1.0f);
                    GroupCallActivity.this.buttonsBackgroundGradientView2.setAlpha(1.0f);
                }
                boolean z3 = GroupCallActivity.this.renderersContainer.swipedBack;
                GroupCallActivity groupCallActivity5 = GroupCallActivity.this;
                if (z3) {
                    groupCallActivity5.listView.setAlpha(1.0f - GroupCallActivity.this.renderersContainer.progressToFullscreenMode);
                } else {
                    groupCallActivity5.listView.setAlpha(1.0f);
                }
                if (GroupCallActivity.this.watchersView != null) {
                    GroupCallActivity.this.watchersView.setAlpha(1.0f - GroupCallActivity.this.renderersContainer.progressToFullscreenMode);
                    GroupCallActivity.this.watchersView.setTranslationY(GroupCallActivity.this.renderersContainer.progressToFullscreenMode * AndroidUtilities.dp(64.0f));
                }
                super.dispatchDraw(canvas);
                GroupCallActivity groupCallActivity6 = GroupCallActivity.this;
                if (groupCallActivity6.drawingForBlur) {
                    return;
                }
                boolean z4 = groupCallActivity6.avatarsPreviewShowed;
                GroupCallActivity groupCallActivity7 = GroupCallActivity.this;
                if (z4) {
                    if (groupCallActivity7.scrimView != null) {
                        if (GroupCallActivity.this.useBlur) {
                            canvas3 = canvas;
                        } else {
                            canvas3 = canvas;
                            canvas3.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), GroupCallActivity.this.scrimPaint);
                        }
                        float y2 = GroupCallActivity.this.listView.getY();
                        float[] fArr = new float[8];
                        Path path = new Path();
                        int childCount2 = GroupCallActivity.this.listView.getChildCount();
                        float y3 = GroupCallActivity.this.listView.getY() + GroupCallActivity.this.listView.getMeasuredHeight();
                        if (GroupCallActivity.this.hasScrimAnchorView) {
                            int i11 = 0;
                            while (true) {
                                if (i11 >= childCount2) {
                                    groupCallUserCell = null;
                                    break;
                                } else {
                                    if (GroupCallActivity.this.listView.getChildAt(i11) == GroupCallActivity.this.scrimView) {
                                        groupCallUserCell = GroupCallActivity.this.scrimView;
                                        break;
                                    }
                                    i11++;
                                }
                            }
                        } else {
                            groupCallUserCell = GroupCallActivity.this.scrimView;
                        }
                        if (groupCallUserCell != null && y2 < y3) {
                            canvas3.save();
                            if (GroupCallActivity.this.scrimFullscreenView == null) {
                                canvas3.clipRect(0.0f, (1.0f - GroupCallActivity.this.progressToAvatarPreview) * y2, getMeasuredWidth(), ((1.0f - GroupCallActivity.this.progressToAvatarPreview) * y3) + (getMeasuredHeight() * GroupCallActivity.this.progressToAvatarPreview));
                            }
                            boolean z5 = GroupCallActivity.this.hasScrimAnchorView;
                            GroupCallActivity groupCallActivity8 = GroupCallActivity.this;
                            if (!z5) {
                                y = groupCallActivity8.avatarPreviewContainer.getTop() + GroupCallActivity.this.avatarPreviewContainer.getMeasuredWidth();
                                left = GroupCallActivity.this.avatarPreviewContainer.getLeft();
                            } else {
                                y = ((groupCallActivity8.listView.getY() + groupCallUserCell.getY()) * (1.0f - GroupCallActivity.this.progressToAvatarPreview)) + ((GroupCallActivity.this.avatarPreviewContainer.getTop() + GroupCallActivity.this.avatarPreviewContainer.getMeasuredWidth()) * GroupCallActivity.this.progressToAvatarPreview);
                                left = ((GroupCallActivity.this.listView.getLeft() + groupCallUserCell.getX()) * (1.0f - GroupCallActivity.this.progressToAvatarPreview)) + (GroupCallActivity.this.avatarPreviewContainer.getLeft() * GroupCallActivity.this.progressToAvatarPreview);
                            }
                            canvas3.translate(left, y);
                            if (!GroupCallActivity.this.hasScrimAnchorView) {
                                groupCallUserCell2 = groupCallUserCell;
                                canvas3.saveLayerAlpha(0.0f, 0.0f, groupCallUserCell.getMeasuredWidth(), groupCallUserCell.getClipHeight(), (int) (GroupCallActivity.this.progressToAvatarPreview * 255.0f), 31);
                            } else {
                                groupCallUserCell2 = groupCallUserCell;
                                canvas3.save();
                            }
                            float measuredHeight = (int) (groupCallUserCell2.getMeasuredHeight() + ((groupCallUserCell2.getClipHeight() - groupCallUserCell2.getMeasuredHeight()) * (1.0f - CubicBezierInterpolator.EASE_OUT.getInterpolation(1.0f - GroupCallActivity.this.progressToAvatarPreview))));
                            this.rect.set(0.0f, 0.0f, groupCallUserCell2.getMeasuredWidth(), measuredHeight);
                            GroupCallUserCell groupCallUserCell6 = groupCallUserCell2;
                            groupCallUserCell6.setProgressToAvatarPreview(GroupCallActivity.this.hasScrimAnchorView ? GroupCallActivity.this.progressToAvatarPreview : 1.0f);
                            while (i5 < 4) {
                                fArr[i5] = AndroidUtilities.dp(13.0f) * (1.0f - GroupCallActivity.this.progressToAvatarPreview);
                                fArr[i5 + 4] = AndroidUtilities.dp(13.0f);
                                i5++;
                            }
                            path.reset();
                            path.addRoundRect(this.rect, fArr, Path.Direction.CW);
                            path.close();
                            canvas3.drawPath(path, GroupCallActivity.this.listViewBackgroundPaint);
                            groupCallUserCell6.draw(canvas3);
                            canvas3.restore();
                            canvas3.restore();
                            if (GroupCallActivity.this.scrimPopupLayout != null) {
                                float f4 = y + measuredHeight;
                                float measuredWidth = (getMeasuredWidth() - GroupCallActivity.this.scrimPopupLayout.getMeasuredWidth()) - AndroidUtilities.dp(14.0f);
                                if (GroupCallActivity.this.progressToAvatarPreview != 1.0f) {
                                    canvas3.saveLayerAlpha(measuredWidth, f4, GroupCallActivity.this.scrimPopupLayout.getMeasuredWidth() + measuredWidth, GroupCallActivity.this.scrimPopupLayout.getMeasuredHeight() + f4, (int) (GroupCallActivity.this.progressToAvatarPreview * 255.0f), 31);
                                } else {
                                    canvas3.save();
                                }
                                GroupCallActivity.this.scrimPopupLayout.setTranslationX(measuredWidth - GroupCallActivity.this.scrimPopupLayout.getLeft());
                                GroupCallActivity.this.scrimPopupLayout.setTranslationY(f4 - GroupCallActivity.this.scrimPopupLayout.getTop());
                                float f5 = (GroupCallActivity.this.progressToAvatarPreview * 0.2f) + 0.8f;
                                canvas3.scale(f5, f5, (GroupCallActivity.this.scrimPopupLayout.getMeasuredWidth() / 2.0f) + measuredWidth, f4);
                                canvas3.translate(measuredWidth, f4);
                                GroupCallActivity.this.scrimPopupLayout.draw(canvas3);
                                canvas3.restore();
                            }
                        }
                        if (GroupCallActivity.this.pinchToZoomHelper.isInOverlayMode()) {
                            return;
                        }
                        canvas3.save();
                        if (GroupCallActivity.this.hasScrimAnchorView && GroupCallActivity.this.scrimFullscreenView == null) {
                            canvas3.clipRect(0.0f, y2 * (1.0f - GroupCallActivity.this.progressToAvatarPreview), getMeasuredWidth(), (y3 * (1.0f - GroupCallActivity.this.progressToAvatarPreview)) + (getMeasuredHeight() * GroupCallActivity.this.progressToAvatarPreview));
                        }
                        canvas3.scale(GroupCallActivity.this.avatarPreviewContainer.getScaleX(), GroupCallActivity.this.avatarPreviewContainer.getScaleY(), GroupCallActivity.this.avatarPreviewContainer.getX(), GroupCallActivity.this.avatarPreviewContainer.getY());
                        canvas3.translate(GroupCallActivity.this.avatarPreviewContainer.getX(), GroupCallActivity.this.avatarPreviewContainer.getY());
                        GroupCallActivity.this.avatarPreviewContainer.draw(canvas3);
                        canvas3.restore();
                        return;
                    }
                    return;
                }
                if (groupCallActivity7.scrimView != null) {
                    canvas.drawRect(0.0f, 0.0f, getMeasuredWidth(), getMeasuredHeight(), GroupCallActivity.this.scrimPaint);
                    float y4 = GroupCallActivity.this.listView.getY();
                    GroupCallActivity.this.listView.getY();
                    GroupCallActivity.this.listView.getMeasuredHeight();
                    boolean z6 = GroupCallActivity.this.hasScrimAnchorView;
                    GroupCallActivity groupCallActivity9 = GroupCallActivity.this;
                    if (z6) {
                        int childCount3 = groupCallActivity9.listView.getChildCount();
                        while (i5 < childCount3) {
                            View childAt3 = GroupCallActivity.this.listView.getChildAt(i5);
                            if (childAt3 == GroupCallActivity.this.scrimView) {
                                float fMax = Math.max(GroupCallActivity.this.listView.getLeft(), GroupCallActivity.this.listView.getLeft() + childAt3.getX());
                                float fMax2 = Math.max(y4, GroupCallActivity.this.listView.getY() + childAt3.getY());
                                float fMin = Math.min(GroupCallActivity.this.listView.getRight(), GroupCallActivity.this.listView.getLeft() + childAt3.getX() + childAt3.getMeasuredWidth());
                                float fMin2 = Math.min(GroupCallActivity.this.listView.getY() + GroupCallActivity.this.listView.getMeasuredHeight(), GroupCallActivity.this.listView.getY() + childAt3.getY() + GroupCallActivity.this.scrimView.getClipHeight());
                                if (fMax2 < fMin2) {
                                    if (childAt3.getAlpha() != 1.0f) {
                                        canvas2 = canvas;
                                        canvas2.saveLayerAlpha(fMax, fMax2, fMin, fMin2, (int) (childAt3.getAlpha() * 255.0f), 31);
                                    } else {
                                        canvas2 = canvas;
                                        canvas2.save();
                                    }
                                    canvas2.clipRect(fMax, fMax2, fMin, getMeasuredHeight());
                                    canvas2.translate(GroupCallActivity.this.listView.getLeft() + childAt3.getX(), GroupCallActivity.this.listView.getY() + childAt3.getY());
                                    float alpha = GroupCallActivity.this.scrimPaint.getAlpha() / 100.0f;
                                    this.rect.set(0.0f, 0.0f, childAt3.getMeasuredWidth(), (int) (GroupCallActivity.this.scrimView.getMeasuredHeight() + ((GroupCallActivity.this.scrimView.getClipHeight() - GroupCallActivity.this.scrimView.getMeasuredHeight()) * (1.0f - CubicBezierInterpolator.EASE_OUT.getInterpolation(1.0f - alpha)))));
                                    GroupCallActivity.this.scrimView.setAboutVisibleProgress(GroupCallActivity.this.listViewBackgroundPaint.getColor(), alpha);
                                    canvas2.drawRoundRect(this.rect, AndroidUtilities.dp(13.0f), AndroidUtilities.dp(13.0f), GroupCallActivity.this.listViewBackgroundPaint);
                                    childAt3.draw(canvas2);
                                    canvas2.restore();
                                }
                            }
                            i5++;
                        }
                        return;
                    }
                    if (groupCallActivity9.scrimFullscreenView != null) {
                        canvas.save();
                        canvas.translate(GroupCallActivity.this.scrimFullscreenView.getX() + GroupCallActivity.this.fullscreenUsersListView.getX() + GroupCallActivity.this.renderersContainer.getX(), GroupCallActivity.this.scrimFullscreenView.getY() + GroupCallActivity.this.fullscreenUsersListView.getY() + GroupCallActivity.this.renderersContainer.getY());
                        if (GroupCallActivity.this.scrimFullscreenView.getRenderer() != null && GroupCallActivity.this.scrimFullscreenView.getRenderer().isAttached() && !GroupCallActivity.this.scrimFullscreenView.getRenderer().showingInFullscreen) {
                            GroupCallActivity.this.scrimFullscreenView.getRenderer().draw(canvas);
                        } else {
                            GroupCallActivity.this.scrimFullscreenView.draw(canvas);
                        }
                        GroupCallActivity.this.scrimFullscreenView.drawOverlays(canvas);
                        canvas.restore();
                        return;
                    }
                    if (GroupCallActivity.this.scrimRenderer == null || !GroupCallActivity.this.scrimRenderer.isAttached()) {
                        return;
                    }
                    canvas.save();
                    canvas.translate(GroupCallActivity.this.scrimRenderer.getX() + GroupCallActivity.this.renderersContainer.getX(), GroupCallActivity.this.scrimRenderer.getY() + GroupCallActivity.this.renderersContainer.getY());
                    GroupCallActivity.this.scrimRenderer.draw(canvas);
                    canvas.restore();
                }
            }

            @Override // android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (GroupCallActivity.this.isInDrawRenderNodeBlur) {
                    if (view == GroupCallActivity.this.listView) {
                        int childCount = GroupCallActivity.this.listView.getChildCount();
                        for (int i5 = 0; i5 < childCount; i5++) {
                            View childAt = GroupCallActivity.this.listView.getChildAt(i5);
                            if (childAt.getVisibility() == 0) {
                                canvas.save();
                                canvas.translate(childAt.getX(), childAt.getY());
                                childAt.draw(canvas);
                                canvas.restore();
                            }
                        }
                    }
                    if (view == GroupCallActivity.this.renderersContainer || view == GroupCallActivity.this.buttonsContainer) {
                        return super.drawChild(canvas, view, j);
                    }
                    return true;
                }
                if (!GroupCallActivity.isTabletMode && GroupCallActivity.this.renderersContainer.progressToFullscreenMode == 1.0f && (view == GroupCallActivity.this.actionBar || view == GroupCallActivity.this.actionBarShadow || view == GroupCallActivity.this.actionBarBackground || view == GroupCallActivity.this.titleTextView || view == GroupCallActivity.this.menuItemsContainer || view == GroupCallActivity.this.textureLightningView)) {
                    return true;
                }
                GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
                if (groupCallActivity2.drawingForBlur && view == groupCallActivity2.renderersContainer) {
                    canvas.save();
                    canvas.translate(GroupCallActivity.this.renderersContainer.getX() + GroupCallActivity.this.fullscreenUsersListView.getX(), GroupCallActivity.this.renderersContainer.getY() + GroupCallActivity.this.fullscreenUsersListView.getY());
                    GroupCallActivity.this.fullscreenUsersListView.draw(canvas);
                    canvas.restore();
                    return true;
                }
                if (view == GroupCallActivity.this.avatarPreviewContainer || view == GroupCallActivity.this.scrimPopupLayout || view == GroupCallActivity.this.scrimView) {
                    return true;
                }
                if (GroupCallActivity.this.contentFullyOverlayed && GroupCallActivity.this.useBlur && (view == GroupCallActivity.this.listView || view == GroupCallActivity.this.buttonsContainer || view == GroupCallActivity.this.groupCallMessagesListView)) {
                    return true;
                }
                return super.drawChild(canvas, view, j);
            }

            @Override // android.view.View, android.view.KeyEvent.Callback
            public boolean onKeyDown(int i5, KeyEvent keyEvent) {
                if (GroupCallActivity.this.scrimView != null && i5 == 4) {
                    GroupCallActivity.this.dismissAvatarPreview(true);
                    return true;
                }
                return super.onKeyDown(i5, keyEvent);
            }
        };
        groupCallActivity.sizeNotifierFrameLayout = sizeNotifierFrameLayout;
        groupCallActivity.containerView = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.setClipToPadding(false);
        groupCallActivity.containerView.setFocusable(true);
        groupCallActivity.containerView.setFocusableInTouchMode(true);
        groupCallActivity.containerView.setWillNotDraw(false);
        ViewGroup viewGroup = groupCallActivity.containerView;
        int i5 = groupCallActivity.backgroundPaddingLeft;
        viewGroup.setPadding(i5, 0, i5, 0);
        groupCallActivity.containerView.setKeepScreenOn(true);
        groupCallActivity.containerView.setClipChildren(false);
        groupCallActivity.reactionEffectImageReceiver = new ImageReceiver(groupCallActivity.containerView);
        if (inputPeer != null) {
            SimpleTextView simpleTextView = new SimpleTextView(activity);
            groupCallActivity.scheduleStartInTextView = simpleTextView;
            simpleTextView.setGravity(17);
            groupCallActivity.scheduleStartInTextView.setTextColor(-1);
            groupCallActivity.scheduleStartInTextView.setTypeface(AndroidUtilities.bold());
            groupCallActivity.scheduleStartInTextView.setTextSize(18);
            groupCallActivity.scheduleStartInTextView.setText(LocaleController.getString(R.string.VoipChatStartsIn));
            groupCallActivity.containerView.addView(groupCallActivity.scheduleStartInTextView, LayoutHelper.createFrame(-2, -2.0f, 49, 21.0f, 0.0f, 21.0f, 311.0f));
            SimpleTextView simpleTextView2 = new SimpleTextView(activity) { // from class: org.telegram.ui.GroupCallActivity.8
                private float duration;
                private float gradientWidth;
                private int lastTextWidth;
                private long lastUpdateTime;
                private LinearGradient linearGradient;
                private float startX;
                private float time;
                private Matrix matrix = new Matrix();
                private float targetX = -1.0f;

                private void setTarget() {
                    this.targetX = ((Utilities.random.nextInt(100) - 50) * 0.2f) / 50.0f;
                }

                @Override // org.telegram.ui.ActionBar.SimpleTextView
                public boolean createLayout(int i6) {
                    boolean zCreateLayout = super.createLayout(i6);
                    int textWidth = getTextWidth();
                    if (textWidth != this.lastTextWidth) {
                        float f = textWidth;
                        this.gradientWidth = 1.3f * f;
                        float textHeight = getTextHeight();
                        float f2 = f * 2.0f;
                        int color = Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient);
                        int color2 = Theme.getColor(Theme.key_voipgroup_mutedByAdminGradient3);
                        int i7 = Theme.key_voipgroup_mutedByAdminGradient2;
                        this.linearGradient = new LinearGradient(0.0f, textHeight, f2, 0.0f, new int[]{color, color2, Theme.getColor(i7), Theme.getColor(i7)}, new float[]{0.0f, 0.38f, 0.76f, 1.0f}, Shader.TileMode.CLAMP);
                        getPaint().setShader(this.linearGradient);
                        this.lastTextWidth = textWidth;
                    }
                    return zCreateLayout;
                }

                void lambda$new$9(DialogInterface dialogInterface) {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (this.anyEnterEventSent && (safeLastFragment instanceof ChatActivity)) {
            ((ChatActivity) safeLastFragment).onEditTextDialogClose(true, true);
        }
    }

    public class AnonymousClass6 extends ActionBar.ActionBarMenuOnItemClick {
        final void lambda$new$12(ChatObject.Call.InvitedUser invitedUser, Long l) {
        TL_phone.declineConferenceCallInvite declineconferencecallinvite = new TL_phone.declineConferenceCallInvite();
        declineconferencecallinvite.msg_id = invitedUser.msg_id;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(declineconferencecallinvite, new RequestDelegate() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda59
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$new$11(tLObject, tL_error);
            }
        });
        ChatObject.Call call = this.call;
        if (call != null) {
            invitedUser.calling = false;
            call.invitedUsersMessageIds.put(l, invitedUser);
            applyCallParticipantUpdates(true);
        }
    }

    public void lambda$new$21(HashSet hashSet, ChatObject.Call call, final String str) {
        TL_account.getRequirementsToContact getrequirementstocontact = new TL_account.getRequirementsToContact();
        final ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        final ArrayList arrayList3 = new ArrayList();
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            Long l = (Long) it.next();
            long jLongValue = l.longValue();
            if (call != null) {
                call.removeInvitedUser(jLongValue);
            }
            arrayList.add(MessagesController.getInstance(this.currentAccount).getUser(l));
            getrequirementstocontact.id.add(MessagesController.getInstance(this.currentAccount).getInputUser(jLongValue));
        }
        final Runnable runnable = new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda83
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$18(arrayList, arrayList2, arrayList3, str);
            }
        };
        if (UserConfig.getInstance(this.currentAccount).isPremium()) {
            runnable.run();
        } else {
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(getrequirementstocontact, new RequestDelegate() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda84
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda86
                        @Override // java.lang.Runnable
                        public final void run() {
                            GroupCallActivity.$r8$lambda$VySc520Ees6mNZtPHpJFl8XHJWY(tLObject, arrayList, arrayList, runnable);
                        }
                    });
                }
            });
        }
    }

    public void lambda$onClick$0() {
            GroupCallActivity.this.wasNotInLayoutFullscreen = null;
            GroupCallActivity groupCallActivity = GroupCallActivity.this;
            groupCallActivity.updateMuteButton(groupCallActivity.muteButtonState, true);
        }

        public void lambda$new$47(NumberPicker numberPicker, NumberPicker numberPicker2, NumberPicker numberPicker3, NumberPicker numberPicker4, int i, int i2) {
        try {
            this.container.performHapticFeedback(3, 2);
        } catch (Exception unused) {
        }
        AlertsCreator.checkScheduleDate(this.scheduleButtonTextView, this.scheduleInfoTextView, 604800L, 2, numberPicker, numberPicker2, numberPicker3);
    }

    public void lambda$updateTitle$53(View view) {
        ChatObject.Call call = this.call;
        if (call == null || !call.recording) {
            return;
        }
        showRecordHint(this.actionBar.getTitleTextView());
    }

    public void lambda$updateLiveLabel$66(int i, int i2, boolean z, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.liveLabelPaint.setColor(AndroidUtilities.getOffsetColor(i, i2, fFloatValue, 1.0f));
        this.liveLabelTextView.invalidate();
        if (!z) {
            fFloatValue = 1.0f - fFloatValue;
        }
        this.textureLightningViewAnimatingAlpha = fFloatValue;
        LightningView lightningView = this.textureLightningView;
        if (lightningView == null || !this.needTextureLightning) {
            return;
        }
        lightningView.invalidate();
    }

    @Override 
    public void onAudioSettingsChanged() {
        updateSpeakerPhoneIcon(true);
        if (VoIPService.getSharedInstance() == null || VoIPService.getSharedInstance().isMicMute()) {
            setMicAmplitude(0.0f);
        }
        if (this.listView.getVisibility() == 0) {
            AndroidUtilities.updateVisibleRows(this.listView);
        }
        if (this.fullscreenUsersListView.getVisibility() == 0) {
            AndroidUtilities.updateVisibleRows(this.fullscreenUsersListView);
        }
        this.attachedRenderersTmp.clear();
        this.attachedRenderersTmp.addAll(this.attachedRenderers);
        for (int i = 0; i < this.attachedRenderersTmp.size(); i++) {
            this.attachedRenderersTmp.get(i).updateAttachState(true);
        }
    }

    private void updateSpeakerPhoneIcon(boolean z) {
        VoIPToggleButton voIPToggleButton = this.soundButton;
        if (voIPToggleButton == null || voIPToggleButton.getVisibility() != 0) {
            return;
        }
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        boolean z2 = false;
        if (sharedInstance == null || isRtmpStream()) {
            this.soundButton.setData(R.drawable.msg_voiceshare, -1, 0, 0.3f, true, LocaleController.getString(R.string.VoipChatShare), false, z);
            this.buttonsContainer.setButtonEnabled(this.soundButton, ChatObject.isPublic(this.currentChat) || (ChatObject.hasAdminRights(this.currentChat) && ChatObject.canAddUsers(this.currentChat)), z);
            this.soundButton.setChecked(true, false);
            return;
        }
        this.buttonsContainer.setButtonEnabled(this.soundButton, true, z);
        boolean z3 = sharedInstance.isBluetoothOn() || sharedInstance.isBluetoothWillOn();
        if (!z3 && sharedInstance.isSpeakerphoneOn()) {
            z2 = true;
        }
        if (z3) {
            this.soundButton.setData(R.drawable.calls_bluetooth, -1, 0, 0.1f, true, LocaleController.getString(R.string.VoipAudioRoutingBluetooth), false, z);
        } else if (z2) {
            this.soundButton.setData(R.drawable.calls_speaker, -1, 0, 0.3f, true, LocaleController.getString(R.string.VoipSpeaker), false, z);
        } else {
            boolean zIsHeadsetPlugged = sharedInstance.isHeadsetPlugged();
            VoIPToggleButton voIPToggleButton2 = this.soundButton;
            if (zIsHeadsetPlugged) {
                voIPToggleButton2.setData(R.drawable.calls_headphones, -1, 0, 0.1f, true, LocaleController.getString(R.string.VoipAudioRoutingHeadset), false, z);
            } else {
                voIPToggleButton2.setData(R.drawable.calls_speaker, -1, 0, 0.1f, true, LocaleController.getString(R.string.VoipSpeaker), false, z);
            }
        }
        this.soundButton.setChecked(z2, z);
        int audioOutputIcon = getAudioOutputIcon(getAudioOutputValue());
        if (this.speakerIcon != audioOutputIcon) {
            ImageView imageView = this.speakerImageView;
            this.speakerIcon = audioOutputIcon;
            AndroidUtilities.updateImageViewImageAnimated(imageView, audioOutputIcon);
        }
        if (this.speakerButton.getVisibility() == 0) {
            this.speakerButton.setData(0, -1, 0, 1.0f, true, getAudioOutputName(getAudioOutputValue()), false, z);
            this.speakerButton.setChecked(getAudioOutputActive(getAudioOutputValue()), z);
        }
    }

    void lambda$runAvatarPreviewTransition$76(float f, float f2, float f3, int i, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.progressToAvatarPreview = fFloatValue;
        this.renderersContainer.progressToScrimView = fFloatValue;
        float f4 = (f * (1.0f - fFloatValue)) + (fFloatValue * 1.0f);
        this.avatarPreviewContainer.setScaleX(f4);
        this.avatarPreviewContainer.setScaleY(f4);
        this.avatarPreviewContainer.setTranslationX(f2 * (1.0f - this.progressToAvatarPreview));
        this.avatarPreviewContainer.setTranslationY(f3 * (1.0f - this.progressToAvatarPreview));
        if (!this.useBlur) {
            this.scrimPaint.setAlpha((int) (this.progressToAvatarPreview * 100.0f));
        }
        GroupCallMiniTextureView groupCallMiniTextureView = this.scrimRenderer;
        if (groupCallMiniTextureView != null) {
            groupCallMiniTextureView.textureView.setRoundCorners(AndroidUtilities.dp(8.0f) * (1.0f - this.progressToAvatarPreview));
        }
        this.avatarPreviewContainer.invalidate();
        this.containerView.invalidate();
        ProfileGalleryView profileGalleryView = this.avatarsViewPager;
        float f5 = i;
        float f6 = this.progressToAvatarPreview;
        profileGalleryView.setRoundRadius((int) ((1.0f - f6) * f5), (int) (f5 * (1.0f - f6)));
    }

    public void dismissAvatarPreview(boolean z) {
        if (this.avatarPriviewTransitionInProgress || !this.avatarsPreviewShowed) {
            return;
        }
        if (z) {
            this.avatarPriviewTransitionInProgress = true;
            runAvatarPreviewTransition(false, this.scrimView);
            return;
        }
        clearScrimView();
        this.containerView.removeView(this.scrimPopupLayout);
        this.scrimPopupLayout = null;
        this.avatarPreviewContainer.setVisibility(8);
        this.containerView.invalidate();
        this.avatarsPreviewShowed = false;
        this.layoutManager.setCanScrollVertically(true);
        this.listView.invalidate();
        this.blurredView.setVisibility(8);
        if (this.delayedGroupCallUpdated) {
            this.delayedGroupCallUpdated = false;
            applyCallParticipantUpdates(true);
        }
        checkContentOverlayed();
    }

    public class ListAdapter extends RecyclerListView.SelectionAdapter {
        private int addMemberRow;
        private int conferenceAddPeopleRow;
        private int conferenceShareLinkRow;
        private int encryptionRow;
        private boolean hasSelfUser;
        private int invitedEndRow;
        private int invitedStartRow;
        private int lastRow;
        private Context mContext;
        private int rowsCount;
        private int shadyJoinEndRow;
        private int shadyJoinStartRow;
        private int shadyLeftEndRow;
        private int shadyLeftStartRow;
        private int usersEndRow;
        private int usersStartRow;
        private int usersVideoGridEndRow;
        private int usersVideoGridStartRow;
        private int videoGridDividerRow;
        private int videoNotAvailableRow;

        public ListAdapter(Context context) {
            this.mContext = context;
        }

        public boolean addSelfToCounter() {
            if (GroupCallActivity.this.isRtmpStream() || this.hasSelfUser || VoIPService.getSharedInstance() == null) {
                return false;
            }
            return !VoIPService.getSharedInstance().isJoined();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.rowsCount;
        }

        public void updateRows() {
            TLRPC.Chat chat;
            TLRPC.Chat chat2;
            ChatObject.Call call = GroupCallActivity.this.call;
            if (call == null || call.isScheduled() || GroupCallActivity.this.delayedGroupCallUpdated) {
                return;
            }
            this.addMemberRow = -1;
            this.conferenceAddPeopleRow = -1;
            this.conferenceShareLinkRow = -1;
            this.videoGridDividerRow = -1;
            this.videoNotAvailableRow = -1;
            this.encryptionRow = -1;
            this.rowsCount = 0;
            GroupCallActivity groupCallActivity = GroupCallActivity.this;
            this.hasSelfUser = groupCallActivity.call.participants.indexOfKey(MessageObject.getPeerId(groupCallActivity.selfPeer)) >= 0;
            if (GroupCallActivity.this.isConference()) {
                int i = this.rowsCount;
                this.rowsCount = i + 1;
                this.encryptionRow = i;
            }
            int i2 = this.rowsCount;
            this.usersVideoGridStartRow = i2;
            int size = i2 + GroupCallActivity.this.visibleVideoParticipants.size();
            this.rowsCount = size;
            this.usersVideoGridEndRow = size;
            if (GroupCallActivity.this.visibleVideoParticipants.size() > 0) {
                int i3 = this.rowsCount;
                this.rowsCount = i3 + 1;
                this.videoGridDividerRow = i3;
            }
            if (!GroupCallActivity.this.visibleVideoParticipants.isEmpty() && GroupCallActivity.this.canManageCall()) {
                GroupCallActivity groupCallActivity2 = GroupCallActivity.this;
                if (groupCallActivity2.call.call.participants_count > groupCallActivity2.accountInstance.getMessagesController().groupCallVideoMaxParticipants) {
                    int i4 = this.rowsCount;
                    this.rowsCount = i4 + 1;
                    this.videoNotAvailableRow = i4;
                }
            }
            this.usersStartRow = this.rowsCount;
            if (!GroupCallActivity.this.isRtmpStream()) {
                this.rowsCount += GroupCallActivity.this.call.visibleParticipants.size();
            }
            this.usersEndRow = this.rowsCount;
            if (GroupCallActivity.this.call.invitedUsers.isEmpty() || GroupCallActivity.this.isRtmpStream()) {
                this.invitedStartRow = -1;
                this.invitedEndRow = -1;
            } else {
                int i5 = this.rowsCount;
                this.invitedStartRow = i5;
                int size2 = i5 + GroupCallActivity.this.call.invitedUsers.size();
                this.rowsCount = size2;
                this.invitedEndRow = size2;
            }
            if (GroupCallActivity.this.call.shadyJoinParticipants.isEmpty() || GroupCallActivity.this.isRtmpStream()) {
                this.shadyJoinStartRow = -1;
                this.shadyJoinEndRow = -1;
            } else {
                int i6 = this.rowsCount;
                this.shadyJoinStartRow = i6;
                int size3 = i6 + GroupCallActivity.this.call.shadyJoinParticipants.size();
                this.rowsCount = size3;
                this.shadyJoinEndRow = size3;
            }
            if (GroupCallActivity.this.call.shadyLeftParticipants.isEmpty() || GroupCallActivity.this.isRtmpStream()) {
                this.shadyLeftStartRow = -1;
                this.shadyLeftEndRow = -1;
            } else {
                int i7 = this.rowsCount;
                this.shadyLeftStartRow = i7;
                int size4 = i7 + GroupCallActivity.this.call.shadyLeftParticipants.size();
                this.rowsCount = size4;
                this.shadyLeftEndRow = size4;
            }
            if (GroupCallActivity.this.isConference()) {
                int i8 = this.rowsCount;
                this.conferenceAddPeopleRow = i8;
                this.rowsCount = i8 + 2;
                this.conferenceShareLinkRow = i8 + 1;
            } else if (!GroupCallActivity.this.isRtmpStream() && (((!ChatObject.isChannel(GroupCallActivity.this.currentChat) || ((chat2 = GroupCallActivity.this.currentChat) != null && chat2.megagroup)) && ChatObject.canWriteToChat(GroupCallActivity.this.currentChat)) || (ChatObject.isChannel(GroupCallActivity.this.currentChat) && (chat = GroupCallActivity.this.currentChat) != null && !chat.megagroup && ChatObject.isPublic(chat)))) {
                int i9 = this.rowsCount;
                this.rowsCount = i9 + 1;
                this.addMemberRow = i9;
            }
            int i10 = this.rowsCount;
            this.rowsCount = i10 + 1;
            this.lastRow = i10;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            updateRows();
            super.notifyDataSetChanged();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemChanged(int i) {
            updateRows();
            super.notifyItemChanged(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemChanged(int i, Object obj) {
            updateRows();
            super.notifyItemChanged(i, obj);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int i, int i2) {
            updateRows();
            super.notifyItemRangeChanged(i, i2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int i, int i2, Object obj) {
            updateRows();
            super.notifyItemRangeChanged(i, i2, obj);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemInserted(int i) {
            updateRows();
            super.notifyItemInserted(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemMoved(int i, int i2) {
            updateRows();
            super.notifyItemMoved(i, i2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeInserted(int i, int i2) {
            updateRows();
            super.notifyItemRangeInserted(i, i2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRemoved(int i) {
            updateRows();
            super.notifyItemRemoved(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeRemoved(int i, int i2) {
            updateRows();
            super.notifyItemRangeRemoved(i, i2);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType == 1) {
                GroupCallUserCell groupCallUserCell = (GroupCallUserCell) viewHolder.itemView;
                int i = GroupCallActivity.this.actionBar.getTag() != null ? Theme.key_voipgroup_mutedIcon : Theme.key_voipgroup_mutedIconUnscrolled;
                groupCallUserCell.setGrayIconColor(i, Theme.getColor(i));
                groupCallUserCell.setDrawDivider(viewHolder.getAdapterPosition() != getItemCount() - 2);
                return;
            }
            if (itemViewType == 2) {
                GroupCallInvitedCell groupCallInvitedCell = (GroupCallInvitedCell) viewHolder.itemView;
                int i2 = GroupCallActivity.this.actionBar.getTag() != null ? Theme.key_voipgroup_mutedIcon : Theme.key_voipgroup_mutedIconUnscrolled;
                groupCallInvitedCell.setGrayIconColor(i2, Theme.getColor(i2));
                groupCallInvitedCell.setDrawDivider(viewHolder.getAdapterPosition() != getItemCount() - 2);
            }
        }

        void lambda$toggleAdminSpeak$77(TLRPC.Updates updates, TLRPC.TL_error tL_error) {
        if (updates != null) {
            this.accountInstance.getMessagesController().processUpdates(updates, false);
        }
    }

    public void setCommentsEnabled(boolean z) {
        if (this.call == null) {
            return;
        }
        TL_phone.toggleGroupCallSettings togglegroupcallsettings = new TL_phone.toggleGroupCallSettings();
        togglegroupcallsettings.call = this.call.getInputGroupCall();
        togglegroupcallsettings.messages_enabled = Boolean.valueOf(z);
        this.pendingCommentsEnabled = Boolean.valueOf(z);
        updateButtonsVisibility(true);
        ConnectionsManager connectionsManager = this.accountInstance.getConnectionsManager();
        DispatchQueue dispatchQueue = Utilities.stageQueue;
        Objects.requireNonNull(dispatchQueue);
        connectionsManager.sendRequestTyped(togglegroupcallsettings, new ChatThemeController$$ExternalSyntheticLambda6(dispatchQueue), new Utilities.Callback2() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda64
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$setCommentsEnabled$79((TLRPC.Updates) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$runPendingAnimations$0(ValueAnimator valueAnimator) {
            this.animationProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            GroupCallActivity.this.listView.invalidate();
            GroupCallActivity.this.renderersContainer.invalidate();
            ((BottomSheet) GroupCallActivity.this).containerView.invalidate();
            GroupCallActivity.this.updateLayout(true);
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean canDismissWithTouchOutside() {
        return !this.renderersContainer.inFullscreenMode;
    }

    public void onUserLeaveHint() {
        if (isRtmpStream() && AndroidUtilities.checkInlinePermissions(this.parentActivity) && !RTMPStreamPipOverlay.isVisible()) {
            dismiss();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda49
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onUserLeaveHint$80();
                }
            }, 100L);
        }
    }

    public void lambda$updateEmoji$1(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            updateEmoji();
        }

        public void attach(View view) {
            this.parents.add(view);
            checkAttach();
        }

        public void detach(View view) {
            this.parents.remove(view);
            checkAttach();
        }

        private void checkAttach() {
            boolean zIsEmpty = this.parents.isEmpty();
            boolean z = !zIsEmpty;
            if (this.attached != z) {
                this.attached = z;
                if (!zIsEmpty) {
                    onAttached();
                } else {
                    onDetached();
                }
            }
        }

        private void onAttached() {
            AnimatedEmojiDrawable animatedEmojiDrawable = this.real;
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.addView(this.invalidate);
            }
        }

        private void onDetached() {
            AnimatedEmojiDrawable animatedEmojiDrawable = this.real;
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.removeView(this.invalidate);
            }
        }

        public boolean draw(Canvas canvas, RectF rectF, float f) {
            float f2;
            float fDp = AndroidUtilities.dp(6.0f);
            this.rectF.set(rectF);
            float f3 = -fDp;
            this.rectF.inset(f3, f3);
            RectF rectF2 = this.rectF;
            canvas.saveLayerAlpha(rectF2.left, rectF2.top, rectF2.right, rectF2.bottom, 255, 31);
            long jCurrentTimeMillis = System.currentTimeMillis() + (((long) this.offset) * 45);
            long j = jCurrentTimeMillis - this.startTime;
            float f4 = j / 180.0f;
            float fMin = Math.min(1.0f, f4);
            boolean z = false;
            if (this.loaded && this.real != null && this.realThumb != null && this.realAllowed) {
                this.rectF.set(rectF);
                this.rectF.offset(0.0f, (fMin - 1.0f) * (rectF.height() + fDp));
                if (f < 1.0f) {
                    canvas.save();
                    f2 = 255.0f;
                    this.realThumb.setBounds(0, 0, (int) this.rectF.width(), (int) this.rectF.height());
                    RectF rectF3 = this.rectF;
                    canvas.translate(rectF3.left, rectF3.top);
                    this.realThumb.setAlpha((int) ((1.0f - f) * 255.0f));
                    this.realThumb.draw(canvas);
                    this.realThumb.setAlpha(255);
                    canvas.restore();
                } else {
                    f2 = 255.0f;
                }
                if (f > 0) {
                    canvas.save();
                    this.rectF.inset(AndroidUtilities.dp(-4.0f), AndroidUtilities.dp(-4.0f));
                    this.real.setBounds(0, 0, (int) this.rectF.width(), (int) this.rectF.height());
                    RectF rectF4 = this.rectF;
                    canvas.translate(rectF4.left, rectF4.top);
                    this.real.setAlpha((int) (f * f2));
                    this.real.draw(canvas);
                    this.real.setAlpha(255);
                    canvas.restore();
                }
            } else {
                canvas.save();
                this.rectF.set(rectF);
                this.rectF.offset(0.0f, (fMin - 1.0f) * (rectF.height() + fDp));
                RectF rectF5 = this.rectF;
                canvas.translate(rectF5.left, rectF5.top);
                this.pool[1].setBounds(0, 0, (int) this.rectF.width(), (int) this.rectF.height());
                this.pool[1].setAlpha(127);
                this.pool[1].draw(canvas);
                this.pool[1].setAlpha(255);
                canvas.restore();
            }
            this.rectF.set(rectF);
            this.rectF.offset(0.0f, fMin * (rectF.height() + fDp));
            canvas.save();
            RectF rectF6 = this.rectF;
            canvas.translate(rectF6.left, rectF6.top);
            this.pool[0].setBounds(0, 0, (int) this.rectF.width(), (int) this.rectF.height());
            this.pool[0].setAlpha(127);
            this.pool[0].draw(canvas);
            this.pool[0].setAlpha(255);
            canvas.restore();
            if (f4 >= 1.0f) {
                if (this.loaded && this.realAllowed) {
                    z = true;
                } else {
                    this.startTime = jCurrentTimeMillis - (j % 180);
                    shiftPool();
                    if (this.loaded) {
                        this.realAllowed = true;
                    }
                }
            }
            this.rectF.set(rectF);
            float f5 = (int) f3;
            this.rectF.inset(f5, f5);
            RectF rectF7 = this.rectF;
            float f6 = rectF7.left;
            float f7 = rectF7.top;
            rectF7.set(f6, f7, rectF7.right, f7 + fDp);
            this.clip.draw(canvas, this.rectF, 1, 1.0f);
            this.rectF.set(rectF);
            this.rectF.inset(f5, f5);
            RectF rectF8 = this.rectF;
            float f8 = rectF8.left;
            float f9 = rectF8.bottom;
            rectF8.set(f8, f9 - fDp, rectF8.right, f9);
            this.clip.draw(canvas, this.rectF, 3, 1.0f);
            canvas.restore();
            return !z;
        }

        private void shiftPool() {
            int i = 0;
            while (true) {
                Drawable[] drawableArr = this.pool;
                if (i < drawableArr.length - 1) {
                    int i2 = i + 1;
                    drawableArr[i] = drawableArr[i2];
                    i = i2;
                } else {
                    drawableArr[drawableArr.length - 1] = Emoji.getEmojiDrawable(GroupCallActivity.getRandomEmoji());
                    return;
                }
            }
        }
    }

    public static final class CallEncryptionCellDrawable {
        private final Text closeText;
        private int listBackgroundColor;
        private View parentView;
        private final Text text1;
        private final Text text2;
        private final Paint backgroundPaint = new Paint(1);
        private final Paint dividerPaint = new Paint(1);
        private final EmojiSlot[] slots = new EmojiSlot[4];
        private boolean loading = true;
        private final AnimatedFloat loadingAlpha = new AnimatedFloat(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$CallEncryptionCellDrawable$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.invalidate();
            }
        }, 0, 320, CubicBezierInterpolator.EASE_OUT_QUINT);
        private final RectF fromRect = new RectF();
        private final RectF toRect = new RectF();
        private final Path clipPath = new Path();

        public CallEncryptionCellDrawable(Context context) {
            int i = 0;
            while (true) {
                EmojiSlot[] emojiSlotArr = this.slots;
                if (i < emojiSlotArr.length) {
                    emojiSlotArr[i] = new EmojiSlot(i);
                    i++;
                } else {
                    this.backgroundPaint.setColor(Theme.getColor(Theme.key_voipgroup_listViewBackground));
                    this.text1 = new Text(LocaleController.getString(R.string.ConferenceEncrypted), 12.0f, AndroidUtilities.bold());
                    this.text2 = new Text(LocaleController.getString(R.string.ConferenceEncryptedInfo), 11.0f).multiline(99).setMaxWidth(AndroidUtilities.dp(200.0f)).lineSpacing(AndroidUtilities.dp(2.66f));
                    this.closeText = new Text(LocaleController.getString(R.string.ConferenceEncryptedClose), 14.0f, AndroidUtilities.bold());
                    setEmojis(null);
                    return;
                }
            }
        }

        public void invalidate() {
            View view = this.parentView;
            if (view != null) {
                view.invalidate();
            }
        }

        public void setParentView(View view) {
            this.parentView = view;
            int i = 0;
            while (true) {
                EmojiSlot[] emojiSlotArr = this.slots;
                if (i >= emojiSlotArr.length) {
                    return;
                }
                emojiSlotArr[i].attach(view);
                i++;
            }
        }

        public void resetParentView(View view) {
            if (this.parentView == view) {
                return;
            }
            int i = 0;
            while (true) {
                EmojiSlot[] emojiSlotArr = this.slots;
                if (i < emojiSlotArr.length) {
                    emojiSlotArr[i].detach(view);
                    i++;
                } else {
                    this.parentView = null;
                    return;
                }
            }
        }

        public void setEmojis(String[] strArr) {
            this.loading = strArr == null;
            for (int i = 0; i < 4; i++) {
                this.slots[i].set(strArr == null ? null : strArr[i]);
            }
            invalidate();
        }

        public void setPaintBackgroundColor(int i) {
            this.listBackgroundColor = i;
            invalidate();
        }

        public boolean draw(Canvas canvas, float f, float f2) {
            canvas.save();
            this.text1.ellipsize(f - AndroidUtilities.dp(132.0f));
            this.backgroundPaint.setColor(ColorUtils.blendARGB(this.listBackgroundColor, Theme.getColor(Theme.key_voipgroup_listViewBackground), f2));
            float f3 = this.loadingAlpha.set(this.loading);
            float fDp = AndroidUtilities.dp(14.0f) + AndroidUtilities.dp(86.0f) + this.text1.getWidth();
            float fDp2 = AndroidUtilities.dp(28.0f);
            float fDp3 = AndroidUtilities.dp(232.0f);
            float fDp4 = AndroidUtilities.dp(50.0f) + AndroidUtilities.dp(54.0f) + this.text2.getHeight();
            float fLerp = AndroidUtilities.lerp(fDp, fDp3, f2);
            float fLerp2 = AndroidUtilities.lerp(fDp2, fDp4, f2);
            float fLerp3 = AndroidUtilities.lerp(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(16.0f), f2);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set((f - fLerp) / 2.0f, 0.0f, (f + fLerp) / 2.0f, fLerp2);
            canvas.drawRoundRect(rectF, fLerp3, fLerp3, this.backgroundPaint);
            this.clipPath.rewind();
            this.clipPath.addRoundRect(rectF, fLerp3, fLerp3, Path.Direction.CW);
            canvas.clipPath(this.clipPath);
            int iDp = AndroidUtilities.dp(18.0f);
            int iDp2 = (int) ((fDp3 - AndroidUtilities.dp(32.0f)) / 4.0f);
            int iDp3 = AndroidUtilities.dp(30.0f);
            int i = iDp / 2;
            float f4 = iDp / 2.0f;
            float fCenterY = ((int) rectF.centerY()) - i;
            float fCenterY2 = ((int) rectF.centerY()) + i;
            this.fromRect.set(((((int) rectF.left) + AndroidUtilities.dp(7.0f)) + AndroidUtilities.dp(10.0f)) - f4, fCenterY, ((int) rectF.left) + AndroidUtilities.dp(7.0f) + AndroidUtilities.dp(10.0f) + f4, fCenterY2);
            float f5 = f / 2.0f;
            float f6 = f5 - (iDp2 * 2);
            float f7 = iDp2;
            float f8 = (int) ((0.5f * f7) + f6);
            float f9 = iDp3 / 2.0f;
            this.toRect.set(f8 - f9, (int) ((rectF.top + AndroidUtilities.dp(27.33f)) - f9), f8 + f9, (int) (rectF.top + AndroidUtilities.dp(27.33f) + f9));
            RectF rectF2 = this.fromRect;
            RectF rectF3 = this.toRect;
            AndroidUtilities.lerpCentered(rectF2, rectF3, f2, rectF3);
            boolean zDraw = this.slots[0].draw(canvas, this.toRect, f2);
            this.fromRect.set(((((int) rectF.left) + AndroidUtilities.dp(27.0f)) + AndroidUtilities.dp(10.0f)) - f4, fCenterY, ((int) rectF.left) + AndroidUtilities.dp(27.0f) + AndroidUtilities.dp(10.0f) + f4, fCenterY2);
            float f10 = (int) ((1.5f * f7) + f6);
            boolean z = zDraw;
            this.toRect.set(f10 - f9, (int) ((rectF.top + AndroidUtilities.dp(27.33f)) - f9), f10 + f9, (int) (rectF.top + AndroidUtilities.dp(27.33f) + f9));
            RectF rectF4 = this.fromRect;
            RectF rectF5 = this.toRect;
            AndroidUtilities.lerpCentered(rectF4, rectF5, f2, rectF5);
            if (this.slots[1].draw(canvas, this.toRect, f2)) {
                z = true;
            }
            Text text = this.text1;
            text.draw(canvas, f5 - (text.getWidth() / 2.0f), fDp2 / 2.0f, -1, (1.0f - f2) * AndroidUtilities.lerp(1.0f, 0.75f, f3));
            this.fromRect.set(((((int) rectF.right) - AndroidUtilities.dp(47.0f)) + AndroidUtilities.dp(10.0f)) - f4, fCenterY, (((int) rectF.right) - AndroidUtilities.dp(47.0f)) + AndroidUtilities.dp(10.0f) + f4, fCenterY2);
            float f11 = (int) ((2.5f * f7) + f6);
            this.toRect.set(f11 - f9, (int) ((rectF.top + AndroidUtilities.dp(27.33f)) - f9), f11 + f9, (int) (rectF.top + AndroidUtilities.dp(27.33f) + f9));
            RectF rectF6 = this.fromRect;
            RectF rectF7 = this.toRect;
            AndroidUtilities.lerpCentered(rectF6, rectF7, f2, rectF7);
            if (this.slots[2].draw(canvas, this.toRect, f2)) {
                z = true;
            }
            this.fromRect.set(((((int) rectF.right) - AndroidUtilities.dp(27.0f)) + AndroidUtilities.dp(10.0f)) - f4, fCenterY, (((int) rectF.right) - AndroidUtilities.dp(27.0f)) + AndroidUtilities.dp(10.0f) + f4, fCenterY2);
            float f12 = (int) (f6 + (f7 * 3.5f));
            this.toRect.set(f12 - f9, (int) ((rectF.top + AndroidUtilities.dp(27.33f)) - f9), f12 + f9, (int) (rectF.top + AndroidUtilities.dp(27.33f) + f9));
            RectF rectF8 = this.fromRect;
            RectF rectF9 = this.toRect;
            AndroidUtilities.lerpCentered(rectF8, rectF9, f2, rectF9);
            boolean z2 = this.slots[3].draw(canvas, this.toRect, f2) ? true : z;
            if (f2 > 0.0f) {
                this.text2.draw(canvas, AndroidUtilities.dp(16.0f) + (rectF.centerX() - (fDp3 / 2.0f)), AndroidUtilities.dp(54.0f), -1, f2);
                this.dividerPaint.setColor(-16777216);
                this.dividerPaint.setAlpha((int) (255.0f * f2));
                canvas.drawRect(rectF.left, fDp4 - AndroidUtilities.dp(40.0f), rectF.right, AndroidUtilities.dp(0.66f) + (fDp4 - AndroidUtilities.dp(40.0f)), this.dividerPaint);
                this.closeText.draw(canvas, rectF.centerX() - (this.closeText.getWidth() / 2.0f), fDp4 - AndroidUtilities.dp(20.0f), -1, f2);
            }
            canvas.restore();
            return z2;
        }
    }

    public static final class CallEncryptionCell extends View {
        private final CallEncryptionCellDrawable drawable;

        public CallEncryptionCell(final Context context, CallEncryptionCellDrawable callEncryptionCellDrawable) {
            super(context);
            this.drawable = callEncryptionCellDrawable;
            NotificationCenter.listenEmojiLoading(this);
            setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.GroupCallActivity$CallEncryptionCell$$ExternalSyntheticLambda0
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$new$0(context, view);
                }
            });
        }

        public void lambda$new$0(CallEncryptionCell callEncryptionCell, Bitmap bitmap) {
                if (callEncryptionCell != null) {
                    callEncryptionCell.setVisibility(0);
                }
                this.blurBitmap = bitmap;
                Paint paint = new Paint(1);
                this.blurBitmapPaint = paint;
                Bitmap bitmap2 = this.blurBitmap;
                Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                BitmapShader bitmapShader = new BitmapShader(bitmap2, tileMode, tileMode);
                this.blurBitmapShader = bitmapShader;
                paint.setShader(bitmapShader);
                ColorMatrix colorMatrix = new ColorMatrix();
                AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, Theme.isCurrentThemeDark() ? 0.05f : 0.25f);
                AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, Theme.isCurrentThemeDark() ? -0.02f : -0.04f);
                this.blurBitmapPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            }

            public /* synthetic */ void lambda$new$1(View view) {
                dismiss();
            }

            @Override // android.app.Dialog
            public void onCreate(Bundle bundle) {
                super.onCreate(bundle);
                Window window = getWindow();
                window.setWindowAnimations(R.style.DialogNoAnimation);
                setContentView(this.windowView, new ViewGroup.LayoutParams(-1, -1));
                WindowManager.LayoutParams attributes = window.getAttributes();
                attributes.width = -1;
                attributes.height = -1;
                attributes.gravity = 119;
                attributes.dimAmount = 0.0f;
                int i = attributes.flags & (-3);
                attributes.softInputMode = 48;
                attributes.flags = (-2013069056) | i;
                if (!BuildVars.DEBUG_PRIVATE_VERSION) {
                    attributes.flags = i | (-2013060864);
                    AndroidUtilities.logFlagSecure();
                }
                attributes.flags |= 1152;
                if (Build.VERSION.SDK_INT >= 28) {
                    attributes.layoutInDisplayCutoutMode = 1;
                }
                window.setAttributes(attributes);
            }

            @Override // android.app.Dialog
            public void show() {
                super.show();
                animate(1.0f, null);
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$CallEncryptionCell$EncryptionCallDialog$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$show$2();
                    }
                }, 16L);
            }

            public /* synthetic */ void lambda$show$2() {
                CallEncryptionCell callEncryptionCell = this.cell;
                if (callEncryptionCell != null) {
                    callEncryptionCell.setVisibility(4);
                }
            }

            @Override // android.app.Dialog, android.content.DialogInterface
            public void dismiss() {
                if (this.dismissing) {
                    return;
                }
                this.dismissing = true;
                animate(0.0f, new Runnable() { // from class: org.telegram.ui.GroupCallActivity$CallEncryptionCell$EncryptionCallDialog$$ExternalSyntheticLambda2
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$dismiss$4();
                    }
                });
                try {
                    WindowManager.LayoutParams attributes = getWindow().getAttributes();
                    attributes.flags |= 16;
                    getWindow().setAttributes(attributes);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            public /* synthetic */ void lambda$dismiss$4() {
                CallEncryptionCell callEncryptionCell = this.cell;
                if (callEncryptionCell != null) {
                    callEncryptionCell.setVisibility(0);
                }
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$CallEncryptionCell$EncryptionCallDialog$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$dismiss$3();
                    }
                }, 16L);
            }

            public /* synthetic */ void lambda$dismiss$3() {
                super.dismiss();
            }

            private void animate(final float f, final Runnable runnable) {
                ValueAnimator valueAnimator = this.progressAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.progressAnimator = null;
                }
                ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.progress, f);
                this.progressAnimator = valueAnimatorOfFloat;
                valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.GroupCallActivity$CallEncryptionCell$EncryptionCallDialog$$ExternalSyntheticLambda5
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        this.f$0.lambda$animate$5(valueAnimator2);
                    }
                });
                this.progressAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.GroupCallActivity.CallEncryptionCell.EncryptionCallDialog.2
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        EncryptionCallDialog.this.progress = f;
                        EncryptionCallDialog.this.windowView.invalidate();
                        Runnable runnable2 = runnable;
                        if (runnable2 != null) {
                            runnable2.run();
                        }
                    }
                });
                this.progressAnimator.setDuration(420L);
                this.progressAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.progressAnimator.start();
            }

            public /* synthetic */ void lambda$animate$5(ValueAnimator valueAnimator) {
                this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                this.windowView.invalidate();
            }
        }
    }

    public void openShareConferenceLink() {
        ChatObject.Call call = this.call;
        if (call == null || call.call == null) {
            return;
        }
        final AlertDialog alertDialog = new AlertDialog(getContext(), 3);
        alertDialog.showDelayed(300L);
        final TL_phone.exportGroupCallInvite exportgroupcallinvite = new TL_phone.exportGroupCallInvite();
        TLRPC.TL_inputGroupCall tL_inputGroupCall = new TLRPC.TL_inputGroupCall();
        exportgroupcallinvite.call = tL_inputGroupCall;
        TLRPC.GroupCall groupCall = this.call.call;
        tL_inputGroupCall.id = groupCall.id;
        tL_inputGroupCall.access_hash = groupCall.access_hash;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(exportgroupcallinvite, new RequestDelegate() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda65
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$openShareConferenceLink$82(alertDialog, exportgroupcallinvite, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$openShareConferenceLink$82(final AlertDialog alertDialog, final TL_phone.exportGroupCallInvite exportgroupcallinvite, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.GroupCallActivity$$ExternalSyntheticLambda72
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openShareConferenceLink$81(alertDialog, tLObject, exportgroupcallinvite, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$openShareConferenceLink$81(AlertDialog alertDialog, TLObject tLObject, TL_phone.exportGroupCallInvite exportgroupcallinvite, TLRPC.TL_error tL_error) {
        TLRPC.GroupCall groupCall;
        alertDialog.dismiss();
        if (!(tLObject instanceof TL_phone.exportedGroupCallInvite)) {
            if (tL_error != null) {
                BulletinFactory.of(this.topBulletinContainer, new DarkBlueThemeResourcesProvider()).showForError(tL_error);
                return;
            }
            return;
        }
        Context context = getContext();
        int i = this.currentAccount;
        TLRPC.InputGroupCall inputGroupCall = exportgroupcallinvite.call;
        String str = ((TL_phone.exportedGroupCallInvite) tLObject).link;
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        ChatObject.Call call = this.call;
        CallLogActivity.showCallLinkSheet(context, i, inputGroupCall, str, resourcesProvider, false, (call == null || (groupCall = call.call) == null || !groupCall.creator) ? false : true);
    }

    public int getAudioOutputValue() {
        Integer num = this.cacheAudioOutputValue;
        if (num != null) {
            return num.intValue();
        }
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null) {
            return 0;
        }
        int currentAudioRoute = sharedInstance.getCurrentAudioRoute();
        if (currentAudioRoute != 0) {
            return currentAudioRoute != 1 ? 2 : 0;
        }
        return 1;
    }

    public void setAudioOutputValue(int i) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null) {
            return;
        }
        sharedInstance.setAudioOutput(i);
        this.cacheAudioOutputValue = Integer.valueOf(i);
    }

    public int getNextAudioOutputValue() {
        if (getAudioOutputActive(getAudioOutputValue())) {
            return 1;
        }
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        return (sharedInstance == null || !sharedInstance.isBluetoothHeadsetConnected()) ? 0 : 2;
    }

    public String getAudioOutputName(int i) {
        String str;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (i == 0) {
            return LocaleController.getString(R.string.VoipAudioRoutingSpeaker);
        }
        if (i == 1) {
            return (sharedInstance == null || !sharedInstance.isHeadsetPlugged()) ? LocaleController.getString(R.string.VoipAudioRoutingPhone) : LocaleController.getString(R.string.VoipAudioRoutingHeadset);
        }
        if (i != 2) {
            return null;
        }
        return (sharedInstance == null || (str = sharedInstance.currentBluetoothDeviceName) == null) ? LocaleController.getString(R.string.VoipAudioRoutingBluetooth) : str;
    }

    public int getAudioOutputIcon(int i) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance != null && sharedInstance.isBluetoothHeadsetConnected()) {
            return R.drawable.filled_calls_bluetooth_s;
        }
        return R.drawable.filled_sound_on;
    }

    public int getAudioOutputToastIcon(int i) {
        if (i == 2) {
            return R.drawable.msg_voice_bluetooth;
        }
        if (i == 0) {
            return R.drawable.msg_voice_speaker;
        }
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        return (sharedInstance == null || !sharedInstance.isHeadsetPlugged()) ? R.drawable.msg_voice_phone : R.drawable.msg_voice_headphones;
    }

    public String getAudioOutputToastText(int i) {
        String str;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (i == 2) {
            return (sharedInstance == null || (str = sharedInstance.currentBluetoothDeviceName) == null) ? LocaleController.getString(R.string.VoipAudioSwitchedToBluetooth) : LocaleController.formatString(R.string.VoipAudioSwitchedToBluetoothDevice, str);
        }
        if (i == 1) {
            return (sharedInstance == null || !sharedInstance.isHeadsetPlugged()) ? LocaleController.getString(R.string.VoipAudioSwitchedToPhone) : LocaleController.getString(R.string.VoipAudioSwitchedToHeadset);
        }
        return LocaleController.getString(R.string.VoipAudioSwitchedToSpeaker);
    }

    public boolean isBulletinTop() {
        float fMin;
        int iDp = AndroidUtilities.dp(74.0f);
        float f = this.scrollOffsetY - iDp;
        if (this.backgroundPaddingTop + f < ActionBar.getCurrentActionBarHeight()) {
            fMin = Math.min(1.0f, ((ActionBar.getCurrentActionBarHeight() - f) - this.backgroundPaddingTop) / ((iDp - this.backgroundPaddingTop) - AndroidUtilities.dp(14.0f)));
        } else {
            fMin = 0.0f;
        }
        return fMin > 0.5f;
    }

    public void updateTopBulletinY() {
        float fMin;
        if (this.topBulletinContainer == null) {
            return;
        }
        int iDp = AndroidUtilities.dp(74.0f);
        float currentActionBarHeight = this.scrollOffsetY - iDp;
        if (this.backgroundPaddingTop + currentActionBarHeight < ActionBar.getCurrentActionBarHeight()) {
            int iDp2 = (iDp - this.backgroundPaddingTop) - AndroidUtilities.dp(14.0f);
            fMin = Math.min(1.0f, ((ActionBar.getCurrentActionBarHeight() - currentActionBarHeight) - this.backgroundPaddingTop) / iDp2);
            currentActionBarHeight -= (int) ((ActionBar.getCurrentActionBarHeight() - iDp2) * fMin);
        } else {
            fMin = 0.0f;
        }
        float paddingTop = currentActionBarHeight + this.containerView.getPaddingTop();
        FrameLayout frameLayout = this.topBulletinContainer;
        frameLayout.setTranslationY(AndroidUtilities.lerp(((-frameLayout.getTop()) - this.topBulletinContainer.getHeight()) + paddingTop + AndroidUtilities.dp(10.0f), (-this.topBulletinContainer.getTop()) + this.actionBar.getY() + this.actionBar.getHeight(), fMin));
        Bulletin visibleBulletin = Bulletin.getVisibleBulletin();
        if (visibleBulletin == null || visibleBulletin.getLayout() == null || visibleBulletin.getLayout().getParent() == null || visibleBulletin.getLayout().getParent().getParent() != this.topBulletinContainer) {
            return;
        }
        visibleBulletin.getLayout().setTop(fMin > 0.5f);
    }

    private ReactionsContainerLayout createReactionsLayout() {
        ReactionsContainerLayout reactionsContainerLayout = this.reactionsContainerLayout;
        if (reactionsContainerLayout != null) {
            return reactionsContainerLayout;
        }
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment == null) {
            return null;
        }
        ReactionsContainerLayout reactionsContainerLayout2 = new ReactionsContainerLayout(1, lastFragment, getContext(), this.currentAccount, this.resourcesProvider);
        this.reactionsContainerLayout = reactionsContainerLayout2;
        reactionsContainerLayout2.setDelegate(new ReactionsContainerLayout.ReactionsContainerDelegate() { // from class: org.telegram.ui.GroupCallActivity.66
            private final Paint bgPaint;
            private final Path clipPath = new Path();

            @Override // org.telegram.ui.Components.ReactionsContainerLayout.ReactionsContainerDelegate
            public boolean allowLongPress() {
                return false;
            }

            @Override // org.telegram.ui.Components.ReactionsContainerLayout.ReactionsContainerDelegate
            public boolean drawBackground() {
                return true;
            }

            {
                Paint paint = new Paint(1);
                this.bgPaint = paint;
                paint.setColor(-14603467);
            }

            @Override // org.telegram.ui.Components.ReactionsContainerLayout.ReactionsContainerDelegate
            public void drawRoundRect(Canvas canvas, RectF rectF, float f, float f2, float f3, int i, boolean z) {
                Paint paint = this.bgPaint;
                if (f > 0.0f) {
                    canvas.drawRoundRect(rectF, f, f, paint);
                } else {
                    canvas.drawRect(rectF, paint);
                }
                if (Build.VERSION.SDK_INT < 29 || !canvas.isHardwareAccelerated() || GroupCallActivity.this.renderNodeBlur == null) {
                    return;
                }
                canvas.save();
                if (f > 0.0f) {
                    this.clipPath.rewind();
                    this.clipPath.addRoundRect(rectF, f, f, Path.Direction.CW);
                    this.clipPath.close();
                    canvas.clipPath(this.clipPath);
                } else {
                    canvas.clipRect(rectF);
                }
                canvas.translate(-GroupCallActivity.this.reactionsContainerLayout.getX(), -GroupCallActivity.this.reactionsContainerLayout.getY());
                canvas.scale(GroupCallActivity.this.renderNodeBlurScale, GroupCallActivity.this.renderNodeBlurScale);
                canvas.drawRenderNode(GroupCallActivity.this.renderNodeBlur);
                canvas.restore();
            }

            @Override // org.telegram.ui.Components.ReactionsContainerLayout.ReactionsContainerDelegate
            public void onReactionClicked(View view, ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z, boolean z2) {
                TLRPC.TL_messageEntityCustomEmoji tL_messageEntityCustomEmoji = new TLRPC.TL_messageEntityCustomEmoji();
                String str = visibleReaction.emojicon;
                if (str == null) {
                    str = "👍";
                }
                TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
                tL_textWithEntities.text = str;
                long j = visibleReaction.documentId;
                if (j != 0) {
                    tL_messageEntityCustomEmoji.document_id = j;
                    tL_messageEntityCustomEmoji.offset = 0;
                    tL_messageEntityCustomEmoji.length = str.length();
                    tL_textWithEntities.entities.add(tL_messageEntityCustomEmoji);
                }
                GroupCallActivity.this.sendGroupCallMessage(tL_textWithEntities);
                GroupCallActivity.this.hideKeyboardOrEmojiView();
                CustomEmojiReactionsWindow reactionsWindow = GroupCallActivity.this.reactionsContainerLayout.getReactionsWindow();
                if (reactionsWindow == null || !reactionsWindow.isShowing()) {
                    return;
                }
                GroupCallActivity.this.reactionsContainerLayout.getReactionsWindow().dismissWithAlpha();
                GroupCallActivity.this.reactionsContainerLayout.reset();
            }
        });
        this.containerView.addView(this.reactionsContainerLayout, LayoutHelper.createFrame(-2, 52, 81));
        this.reactionsContainerLayout.setMessage(null, null, false);
        this.callMessageEnterUnderContainer.bringToFront();
        this.callMessageEnterContainer.bringToFront();
        return this.reactionsContainerLayout;
    }

    public void hideKeyboardOrEmojiView() {
        boolean zIsPopupVisible = this.callMessageEnterView.isPopupVisible();
        EditTextEmoji editTextEmoji = this.callMessageEnterView;
        if (zIsPopupVisible) {
            editTextEmoji.hideEmojiView();
        } else {
            editTextEmoji.closeKeyboard();
        }
    }

    public void sendGroupCallMessage(TLRPC.TL_textWithEntities tL_textWithEntities) {
        TLRPC.InputGroupCall inputGroupCall;
        long peerDialogId;
        this.callMessageEnterView.setText(_UrlKt.FRAGMENT_ENCODE_SET);
        ChatObject.Call call = this.call;
        if (call == null || call.call == null || (inputGroupCall = call.getInputGroupCall()) == null) {
            return;
        }
        ChatObject.Call call2 = this.call;
        long j = call2.call.id;
        TLRPC.Peer peer = call2.selfPeer;
        if (peer != null) {
            peerDialogId = DialogObject.getPeerDialogId(peer);
        } else {
            peerDialogId = UserConfig.getInstance(this.currentAccount).clientUserId;
        }
        GroupCallMessagesController.getInstance(this.currentAccount).sendCallMessage(peerDialogId, tL_textWithEntities, j, inputGroupCall);
    }

    public void checkInsets() {
        checkGroupCallUiPositions_MessagesList();
        checkGroupCallUiAlpha_ReactionsLayout();
        checkGroupCallUiAlpha_EnterView();
        checkGroupCallUiPositions_EnterView();
        checkGroupCallUiPositions_ReactionsLayout();
        this.containerView.invalidate();
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        if (i == 2) {
            checkGroupCallUiPositions_MessagesList();
            checkGroupCallUiPositions_ButtonsList();
            checkGroupCallUiPositions_FullscreenUsersList();
            checkGroupCallUiAlpha_ButtonsList();
            this.renderersContainer.setProgressToHideUi(f);
            this.fullscreenUsersListView.invalidate();
            this.containerView.invalidate();
            this.buttonsContainer.invalidate();
        }
        if (i == 3) {
            checkGroupCallUiPositions_ReactionsLayout();
            checkGroupCallUiPositions_MessagesList();
            checkGroupCallUiPositions_SendButton();
            checkGroupCallUiAlpha_ReactionsLayout();
            this.containerView.invalidate();
        }
        if (i == 4) {
            checkGroupCallUiPositions_MessagesList();
            this.callMessageEnterUnderContainer.invalidate();
            this.callMessageEnterView.invalidate();
        }
        if (i == 5) {
            checkGroupCallUiPositions_MessagesList();
            this.containerView.invalidate();
        }
    }

    private void checkGroupCallUi() {
        checkGroupCallUiPositions_EnterView();
        checkGroupCallUiPositions_ReactionsLayout();
        checkGroupCallUiPositions_MessagesList();
        checkGroupCallUiPositions_ButtonsList();
        checkGroupCallUiPositions_FullscreenUsersList();
        checkGroupCallUiPositions_SendButton();
        checkGroupCallUiAlpha_ButtonsList();
        checkGroupCallUiAlpha_EnterView();
        checkGroupCallUiAlpha_ReactionsLayout();
    }

    private void checkGroupCallUiPositions_EnterView() {
        this.callMessageEnterContainer.setTranslationY((-this.windowInsetsStateHolder.getAnimatedMaxBottomInset()) + this.containerView.getPaddingBottom());
        this.callMessageEnterUnderContainer.invalidate();
    }

    private void checkGroupCallUiPositions_ReactionsLayout() {
        if (this.reactionsContainerLayout != null) {
            this.reactionsContainerLayout.setTranslationY((-this.windowInsetsStateHolder.getAnimatedMaxBottomInset()) + this.containerView.getPaddingBottom() + ((-this.animatorMessageIsEmpty.getFloatValue()) * AndroidUtilities.dp(64.0f)));
        }
    }

    public void checkGroupCallUiPositions_MessagesList() {
        float floatValue;
        float f = -((this.windowInsetsStateHolder.getAnimatedMaxBottomInset() - this.containerView.getPaddingBottom()) + this.animatorMessageInputHeight.getFactor() + (AndroidUtilities.dp(68.0f) * this.animatorMessageIsEmpty.getFloatValue()) + AndroidUtilities.dp(10.0f));
        if (isTabletMode) {
            floatValue = (1.0f - this.animatorHasVideo.getFloatValue()) * AndroidUtilities.dp(-91.0f);
        } else {
            floatValue = isLandscapeMode ? 0.0f : ((this.animatorHideButtons.getFloatValue() * AndroidUtilities.dp(94.0f)) - (AndroidUtilities.dp(104.0f) * this.renderersContainer.progressToFullscreenMode)) - AndroidUtilities.dp(91.0f);
        }
        float fLerp = AndroidUtilities.lerp(floatValue, f, this.windowInsetsStateHolder.getAnimatedKeyboardVisibility());
        float measuredHeight = ((this.containerView.getMeasuredHeight() - this.scrollOffsetY) + fLerp) - this.backgroundPaddingTop;
        float fMax = Math.max((measuredHeight / 3.0f) * 2.0f, measuredHeight - AndroidUtilities.dp(250.0f));
        this.groupCallMessagesListView.setTranslationY(fLerp);
        this.groupCallMessagesListView.setVisibleHeight((int) fMax);
    }

    private void checkGroupCallUiPositions_ButtonsList() {
        if (isTabletMode) {
            this.buttonsContainer.setTranslationX(0.0f);
            this.buttonsContainer.setTranslationY(0.0f);
            return;
        }
        boolean z = isLandscapeMode;
        GroupCallActivityButtonsLayout groupCallActivityButtonsLayout = this.buttonsContainer;
        if (z) {
            groupCallActivityButtonsLayout.setTranslationX(this.animatorHideButtons.getFloatValue() * AndroidUtilities.dp(94.0f));
            this.buttonsContainer.setTranslationY(0.0f);
        } else {
            groupCallActivityButtonsLayout.setTranslationX(0.0f);
            this.buttonsContainer.setTranslationY(this.animatorHideButtons.getFloatValue() * AndroidUtilities.dp(94.0f));
        }
    }

    private void checkGroupCallUiPositions_FullscreenUsersList() {
        if (isTabletMode) {
            this.fullscreenUsersListView.setTranslationX(0.0f);
            this.fullscreenUsersListView.setTranslationY(0.0f);
            return;
        }
        boolean z = isLandscapeMode;
        RecyclerListView recyclerListView = this.fullscreenUsersListView;
        if (z) {
            recyclerListView.setTranslationX(this.animatorHideButtons.getFloatValue() * AndroidUtilities.dp(94.0f));
            this.fullscreenUsersListView.setTranslationY(0.0f);
        } else {
            recyclerListView.setTranslationX(0.0f);
            this.fullscreenUsersListView.setTranslationY(this.animatorHideButtons.getFloatValue() * AndroidUtilities.dp(94.0f));
        }
    }

    private void checkGroupCallUiPositions_SendButton() {
        float floatValue = this.animatorMessageIsEmpty.getFloatValue();
        this.callMessageHideButton.setScaleX(AndroidUtilities.lerp(0.25f, 1.0f, floatValue));
        this.callMessageHideButton.setScaleY(AndroidUtilities.lerp(0.25f, 1.0f, floatValue));
        this.callMessageHideButton.setAlpha(floatValue);
        this.callMessageHideButton.setClickable(floatValue > 0.9f);
        float f = 1.0f - floatValue;
        this.callMessageSendButton.setScaleX(AndroidUtilities.lerp(0.25f, 1.0f, f));
        this.callMessageSendButton.setScaleY(AndroidUtilities.lerp(0.25f, 1.0f, f));
        this.callMessageSendButton.setAlpha(f);
        this.callMessageSendButton.setClickable(f > 0.9f);
    }

    private void checkGroupCallUiAlpha_ButtonsList() {
        this.buttonsContainer.setAlpha(1.0f - this.animatorHideButtons.getFloatValue());
    }

    private void checkGroupCallUiAlpha_EnterView() {
        float animatedKeyboardVisibility = this.windowInsetsStateHolder.getAnimatedKeyboardVisibility();
        int i = animatedKeyboardVisibility > 0.0f ? 0 : 8;
        this.callMessageEnterContainer.setAlpha(animatedKeyboardVisibility);
        this.callMessageEnterUnderContainer.setAlpha(animatedKeyboardVisibility);
        if (this.callMessageEnterContainer.getVisibility() != i) {
            this.callMessageEnterContainer.setVisibility(i);
            this.callMessageEnterUnderContainer.setVisibility(i);
            if (i == 8 && this.callMessageEnterView.isFocused()) {
                this.callMessageEnterView.clearFocus();
            }
        }
    }

    private void checkGroupCallUiAlpha_ReactionsLayout() {
        if (this.reactionsContainerLayout != null) {
            float animatedKeyboardVisibility = this.windowInsetsStateHolder.getAnimatedKeyboardVisibility() * this.animatorMessageIsEmpty.getFloatValue();
            this.reactionsContainerLayout.setAlpha(animatedKeyboardVisibility);
            int i = animatedKeyboardVisibility > 0.0f ? 0 : 8;
            if (this.reactionsContainerLayout.getVisibility() != i) {
                this.reactionsContainerLayout.setVisibility(i);
                if (i == 8) {
                    this.reactionsContainerLayout.reset();
                }
            }
            ReactionsContainerLayout reactionsContainerLayout = this.reactionsContainerLayout;
            if (reactionsContainerLayout.skipEnterAnimation || animatedKeyboardVisibility != 1.0f) {
                return;
            }
            reactionsContainerLayout.skipEnterAnimation = true;
        }
    }
}
