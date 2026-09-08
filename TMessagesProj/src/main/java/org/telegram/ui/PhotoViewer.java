package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
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
import android.graphics.PorterDuffXfermode;
import android.graphics.RecordingCanvas;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.RenderEffect;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaFormat;
import android.net.Uri;
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
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineHeightSpan;
import android.text.style.URLSpan;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.util.FloatProperty;
import android.util.Pair;
import android.util.Property;
import android.util.Range;
import android.util.SparseArray;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.OverScroller;
import android.widget.Scroller;
import android.widget.Space;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.window.OnBackInvokedCallback;
import android.window.OnBackInvokedDispatcher;
import androidx.annotation.Keep;
import androidx.collection.LongSparseArray;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.widget.NestedScrollView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerEnd;
import androidx.recyclerview.widget.RecyclerView;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.components.ChooseSubtitlesLayout;
import com.exteragram.messenger.components.ReverseImageSearchSheet;
import com.exteragram.messenger.components.SearchPhotoPopupWrapper;
import com.exteragram.messenger.utils.AppUtils;
import com.exteragram.messenger.utils.VideoSubtitlesHelper;
import com.exteragram.messenger.utils.system.SystemUtils;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import com.exteragram.messenger.utils.ui.PopupUtils;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.analytics.AnalyticsListener;
import com.google.android.exoplayer2.text.Cue;
import com.google.android.exoplayer2.text.CueGroup;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.video.VideoFrameMetadataListener;
import com.google.android.exoplayer2.video.VideoSize;
import com.google.android.gms.cast.MediaError;
import com.google.android.gms.cast.MediaStatus;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.material.timepicker.TimeModel;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import me.vkryl.core.reference.ReferenceList;
import okhttp3.internal.url._UrlKt;
import org.mvel2.MVEL;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Bitmaps;
import org.telegram.messenger.BotFullscreenButtons$$ExternalSyntheticApiModelOutline0;
import org.telegram.messenger.BotFullscreenButtons$$ExternalSyntheticApiModelOutline1;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BringAppForegroundService;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileStreamLoadOperation;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessageSuggestionParams;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsSettingsFacade;
import org.telegram.messenger.R;
import org.telegram.messenger.SecureDocument;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.TranslateController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.WebFile;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.camera.Size;
import org.telegram.messenger.chromecast.ChromecastController;
import org.telegram.messenger.chromecast.ChromecastMedia;
import org.telegram.messenger.chromecast.ChromecastMediaVariations;
import org.telegram.messenger.pip.PipSource;
import org.telegram.messenger.pip.source.IPipSourceDelegate;
import org.telegram.messenger.pip.source.PipSourceSnapshot$$ExternalSyntheticApiModelOutline0;
import org.telegram.messenger.pip.utils.PipUtils;
import org.telegram.messenger.utils.WindowVisibilityManager;
import org.telegram.messenger.video.OldVideoPlayerRewinder;
import org.telegram.messenger.video.VideoAds;
import org.telegram.messenger.video.VideoFramesRewinder;
import org.telegram.messenger.video.VideoPlayerRewinder;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_iv;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSlider;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Cells.PhotoPickerPhotoCell;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AnimationProperties;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlurringShader;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CaptionPhotoViewer;
import org.telegram.ui.Components.CastMediaRouteButton;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.CheckBox;
import org.telegram.ui.Components.ClippingImageView;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.Crop.CropAreaView;
import org.telegram.ui.Components.Crop.CropTransform;
import org.telegram.ui.Components.Crop.CropView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EditCoverButton;
import org.telegram.ui.Components.EditTextEmoji;
import org.telegram.ui.Components.FilterGLThread;
import org.telegram.ui.Components.FilterShaders;
import org.telegram.ui.Components.FloatSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.GestureDetector2;
import org.telegram.ui.Components.GroupedPhotosListView;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.IntSeekBarAccessibilityDelegate;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.LivePhotoButton;
import org.telegram.ui.Components.LoadingDrawable;
import org.telegram.ui.Components.MediaActivity;
import org.telegram.ui.Components.MentionsContainerView;
import org.telegram.ui.Components.MuteDrawable;
import org.telegram.ui.Components.OtherDocumentPlaceholderDrawable;
import org.telegram.ui.Components.Paint.Views.LPhotoPaintView;
import org.telegram.ui.Components.Paint.Views.MaskPaintView;
import org.telegram.ui.Components.Paint.Views.PaintCancelView;
import org.telegram.ui.Components.Paint.Views.PaintDoneView;
import org.telegram.ui.Components.Paint.Views.PaintWeightChooserView;
import org.telegram.ui.Components.Paint.Views.StickerCutOutBtn;
import org.telegram.ui.Components.Paint.Views.StickerMakerBackgroundView;
import org.telegram.ui.Components.Paint.Views.StickerMakerView;
import org.telegram.ui.Components.PaintingOverlay;
import org.telegram.ui.Components.PhotoCropView;
import org.telegram.ui.Components.PhotoFilterBlurControl;
import org.telegram.ui.Components.PhotoFilterCurvesControl;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.PhotoViewerCoverEditor;
import org.telegram.ui.Components.PhotoViewerPollAttachButtons;
import org.telegram.ui.Components.PhotoViewerWebView;
import org.telegram.ui.Components.PickerBottomLayoutViewer;
import org.telegram.ui.Components.PipVideoOverlay;
import org.telegram.ui.Components.PlayPauseDrawable;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.QuoteSpan;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.RectOld;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.SeekSpeedDrawable;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayoutPhoto;
import org.telegram.ui.Components.SpeedIconDrawable;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TextViewSwitcher;
import org.telegram.ui.Components.ThanosEffect;
import org.telegram.ui.Components.Tooltip;
import org.telegram.ui.Components.TranslateAlert2;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.VideoCompressButton;
import org.telegram.ui.Components.VideoEditTextureView;
import org.telegram.ui.Components.VideoForwardDrawable;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.Components.VideoPlayerSeekBar;
import org.telegram.ui.Components.VideoSeekPreviewImage;
import org.telegram.ui.Components.VideoTimelinePlayView;
import org.telegram.ui.Components.ViewHelper;
import org.telegram.ui.Components.blur3.Blur3HashImpl;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.DownscaleScrollableNoiseSuppressor;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawableRenderNode;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawableSource;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSource;
import org.telegram.ui.Components.blur3.utils.Blur3Utils;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stories.DarkThemeResourceProvider;
import org.telegram.ui.Stories.recorder.CaptionContainerView;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.Stories.recorder.KeyboardNotifier;
import org.webrtc.MediaStreamTrack;

@SuppressLint({"WrongConstant"})
public class PhotoViewer implements NotificationCenter.NotificationCenterDelegate, GestureDetector2.OnGestureListener, GestureDetector2.OnDoubleTapListener, IPipSourceDelegate, FactorAnimator.Target, AudioManager.OnAudioFocusChangeListener {
    private static DecelerateInterpolator decelerateInterpolator;
    private static Drawable[] progressDrawables;
    private static Paint progressPaint;
    private boolean ALLOW_USE_SURFACE;
    public final Property<View, Float> FLASH_VIEW_VALUE;
    private int aboutToSwitchTo;
    private ActionBar actionBar;
    private AnimatorSet actionBarAnimator;
    private ImageView actionBarBackButton;
    private Drawable actionBarBackButtonDrawableDeafult;
    private Drawable actionBarBackButtonDrawableGlass;
    private PhotoViewerActionBarContainer actionBarContainer;
    private Map<View, Boolean> actionBarItemsVisibility;
    private boolean actionBarWasShownBeforeByEnd;
    private Context activityContext;
    private WindowVisibilityManager.Controller activityVisibilityController;
    private TextView adButtonTextView;
    private FrameLayout adButtonView;
    private VideoAds ads;
    private ActionBarMenuSubItem allMediaItem;
    private boolean allowOrder;
    private boolean allowShare;
    private boolean allowShowFullscreenButton;
    private float animateToMirror;
    private float animateToRotate;
    private float animateToScale;
    private float animateToX;
    private float animateToY;
    private ClippingImageView animatingImageView;
    private Runnable animationEndRunnable;
    private int animationInProgress;
    private long animationStartTime;
    private float animationValue;
    private float[][] animationValues;
    private final BoolAnimator animatorPollAttachButtonsVisibility = new BoolAnimator(0, this, CubicBezierInterpolator.EASE_OUT_QUINT, 380);
    private boolean applying;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private boolean attachedToWindow;
    private long audioFramesSize;
    private final AudioManager audioManager;
    private float avatarStartProgress;
    private long avatarStartTime;
    private final ArrayList<TLRPC.Photo> avatarsArr;
    private long avatarsDialogId;
    private BackgroundDrawable backgroundDrawable;
    private volatile int bitrate;
    private Paint blackPaint;
    private final AnimatedFloat blurAlpha;
    private RectF blurBounds;
    public BlurringShader.BlurManager blurManager;
    private BlurredBackgroundSource blurredBackgroundSource;
    private FrameLayout bottomBulletinUnderCaption;
    private LinearGradient bottomFancyShadow;
    private Matrix bottomFancyShadowMatrix;
    private Paint bottomFancyShadowPaint;
    private FrameLayout bottomLayout;
    private LinearLayout btnLayout;
    private boolean canDragDown;
    private boolean canEditAvatar;
    private boolean canZoom;
    private FrameLayout captionContainer;
    private String captionDetectedLanguage;
    private CaptionPhotoViewer captionEdit;
    private final float[] captionEditAlpha;
    private FrameLayout captionEditContainer;
    public CharSequence captionForAllMedia;
    private boolean captionHwLayerEnabled;
    private CaptionScrollView captionScrollView;
    private CaptionTextViewSwitcher captionTextViewSwitcher;
    private boolean captionTranslated;
    private long captureFrameAtTime;
    private long captureFrameReadyAtTime;
    private ActionBarMenuSubItem castItem;
    private CastMediaRouteButton castItemButton;
    private BlurringShader.ThumbBlurer centerBlur;
    public ImageReceiver centerImage;
    private AnimatedFloat[] centerImageInsideBlur;
    private boolean centerImageIsLivePhoto;
    private boolean centerImageIsVideo;
    private Matrix centerImageTransform;
    private boolean centerImageTransformLocked;
    private AnimatorSet changeModeAnimation;
    public TextureView changedTextureView;
    private boolean changingPage;
    private boolean changingTextureView;
    private CheckBox checkImageView;
    private ChooseDownloadQualityLayout chooseDownloadQualityLayout;
    private SpeedButtonsLayout chooseSpeedLayout;
    private ChooseSubtitlesLayout chooseSubtitlesLayout;
    private int classGuid;
    private Path clipFancyShadows;
    private float clippingImageProgress;
    public boolean closePhotoAfterSelect;
    public boolean closePhotoAfterSelectWithAnimation;
    private VideoCompressButton compressItem;
    private HintView2 compressPhotoHint;
    private volatile int compressionsCount;
    private FrameLayoutDrawer containerView;
    private PhotoCountView countView;
    public PhotoViewerCoverEditor coverEditor;
    private boolean cropInitied;
    private ImageView cropItem;
    private CropTransform cropTransform;
    private int currentAccount;
    private AnimatedFileDrawable currentAnimation;
    private ImageLocation currentAvatarLocation;
    private Bitmap currentBitmap;
    private TLRPC.BotInlineResult currentBotInlineResult;
    private float currentCropScale;
    private float currentCropX;
    private float currentCropY;
    private long currentDialogId;
    private int currentEditMode;
    private ImageLocation currentFileLocation;
    private ImageLocation currentFileLocationVideo;
    private String[] currentFileNames;
    private String currentFilterQuery;
    private ReactionsLayoutInBubble.VisibleReaction currentFilterTag;
    private boolean currentFiltered;
    private String currentImageFaceKey;
    private int currentImageHasFace;
    private String currentImagePath;
    private int currentIndex;
    private AnimatorSet currentListViewAnimation;
    private Runnable currentLoadingVideoRunnable;
    private MessageObject currentMessageObject;
    private TL_iv.PageBlock currentPageBlock;
    private float currentPanTranslationY;
    private String currentPathObject;
    private long currentPathVideoOffset;
    private PlaceProviderObject currentPlaceObject;
    private Uri currentPlayingVideoFile;
    private ArrayList<VideoPlayer.Quality> currentPlayingVideoQualityFiles;
    private SecureDocument currentSecureDocument;
    private String currentSubtitle;
    private VideoSubtitlesHelper.SubtitleState currentSubtitleState;
    private ImageReceiver.BitmapHolder currentThumb;
    private boolean currentVideoFinishedLoading;
    private float currentVideoSpeed;
    public Utilities.Callback2<String, TLRPC.InputDocument> customStickerHandler;
    private CharSequence customTitle;
    private BlurButton cutOutBtn;
    private int dateOverride;
    private ActionBarMenuItem deleteItem;
    private MessagesController.DialogPhotos dialogPhotos;
    private boolean disableSelection;
    private boolean disableShowCheck;
    private boolean discardTap;
    private TextView docInfoTextView;
    private TextView docNameTextView;
    private TextView doneButtonFullWidth;
    public boolean doneButtonPressed;
    private boolean dontAutoPlay;
    private boolean dontChangeCaptionPosition;
    private boolean dontResetZoomOnFirstLayout;
    private boolean doubleTap;
    private boolean doubleTapEnabled;
    private float dragY;
    private boolean draggingDown;
    private boolean[] drawPressedDrawable;
    private boolean edgeSwipe;
    private EditCoverButton editCoverButton;
    private ActionBarMenuItem editItem;
    private EditState editState;
    private boolean editing;
    private PickerBottomLayoutViewer editorDoneLayout;
    private boolean[] endReached;
    private long endTime;
    private BlurButton eraseBtn;
    private long estimatedDuration;
    private long estimatedSize;
    private ImageView exitFullscreenButton;
    private boolean fancyShadows;
    private boolean firstAnimationDelay;
    private boolean firstFrameRendered;
    private FirstFrameView firstFrameView;
    private AnimatorSet flashAnimator;
    private View flashView;
    public final VideoFramesRewinder framesRewinder;
    boolean fromCamera;
    private ImageView[] fullscreenButton;
    private int fullscreenedByButton;
    private ActionBarMenuSubItem galleryButton;
    private GestureDetector2 gestureDetector;
    private ReferenceList<View> glassAttachedViews;
    private GroupedPhotosListView groupedPhotosListView;
    private boolean hasAudioFocus;
    public boolean hasCaptionForAllMedia;
    private Runnable hideActionBarRunnable;
    private PlaceProviderObject hideAfterAnimation;
    private UndoView hintView;
    private Rect hitRect;
    private ReferenceList<BlurredBackgroundDrawableRenderNode> iBlur3BlurredDrawables;
    private BlurredBackgroundDrawableViewFactory iBlur3FactoryFrostedLiquidGlass;
    private boolean ignoreDidSetImage;
    private boolean ignorePlayerUpdate;
    private RectF imageBounds;
    private Matrix imageBoundsMatrix;
    private float[] imageBoundsPoints;
    private AnimatorSet imageMoveAnimation;
    private final ArrayList<MessageObject> imagesArr;
    private final ArrayList<Object> imagesArrLocals;
    private final ArrayList<ImageLocation> imagesArrLocations;
    private final ArrayList<Long> imagesArrLocationsSizes;
    private final ArrayList<ImageLocation> imagesArrLocationsVideo;
    private final ArrayList<TLRPC.Message> imagesArrMessages;
    private final ArrayList<MessageObject> imagesArrTemp;
    private final SparseArray<MessageObject>[] imagesByIds;
    private final SparseArray<MessageObject>[] imagesByIdsTemp;
    private boolean inBubbleMode;
    private boolean inPreview;
    private VideoPlayer injectingVideoPlayer;
    private SurfaceTexture injectingVideoPlayerSurface;
    private float inlineOutAnimationProgress;
    private final Rect insets;
    private DecelerateInterpolator interpolator;
    private boolean invalidCoords;
    private boolean isActionBarVisible;
    private boolean isCurrentVideo;
    private boolean isDocumentsPicker;
    private boolean isEmbedVideo;
    private boolean isEvent;
    private boolean isFirstLoading;
    private volatile boolean isH264Video;
    private boolean isInline;
    private boolean isPhotosListViewVisible;
    private boolean isPlaying;
    private boolean isStreaming;
    private boolean isVisible;
    private boolean isVisibleOrAnimating;
    private LinearLayout itemsLayout;
    private boolean keepScreenOnFlagSet;
    private int keyboardSize;
    private long lastBufferedPositionCheck;
    private boolean lastCaptionTranslating;
    private String lastControlFrameDuration;
    private Bitmap lastFrameBitmap;
    private ImageView lastFrameImageView;
    private int lastImageId;
    private long lastPhotoSetTime;
    private int lastQualityIndexSelected;
    private long lastSaveTime;
    private CueGroup lastSubtitleCueGroup;
    private CharSequence lastTitle;
    float lastX;
    private BlurringShader.ThumbBlurer leftBlur;
    private MediaController.CropState leftCropState;
    private CropTransform leftCropTransform;
    private ImageReceiver leftImage;
    private boolean leftImageIsVideo;
    private PaintingOverlay leftPaintingOverlay;
    private Bulletin limitBulletin;
    private LivePhotoButton livePhotoButton;
    private ArrayList<HintView2> livePhotoHints;
    private boolean loadInitialVideo;
    private boolean loadingMoreImages;
    Runnable longPressRunnable;
    float longPressX;
    OldVideoPlayerRewinder longVideoPlayerRewinder;
    private ActionBarMenuSubItem loopItem;
    private boolean manuallyPaused;
    private MaskPaintView maskPaintView;
    private boolean maskPaintViewEraser;
    private boolean maskPaintViewShuttingDown;
    private int maskPaintViewTouched;
    private StickersAlert masksAlert;
    private ActionBarMenuItem masksItem;
    private final LongSparseArray<RenderNode> matrixRenderNodes;
    private int maxSelectedPhotos;
    private float maxX;
    private float maxY;
    private ActionBarMenu menu;
    private ActionBarMenuItem menuItem;
    private long mergeDialogId;
    private float minX;
    private float minY;
    private AnimatorSet miniProgressAnimator;
    private final Runnable miniProgressShowRunnable;
    private RadialProgressView miniProgressView;
    private float mirror;
    private ImageView mirrorItem;
    private float moveStartX;
    private float moveStartY;
    private boolean moving;
    private ImageView muteButton;
    private MuteDrawable muteDrawable;
    private ArrayList<HintView2> muteHints;
    private boolean muteVideo;
    private String nameOverride;
    private View navigationBar;
    private int navigationBarHeight;
    private boolean needCaptionLayout;
    private long needCaptureFrameReadyAtTime;
    private boolean needSearchImageInArr;
    private boolean needShowOnReady;
    private Runnable onUserLeaveHintListener;
    private AnimatorSet openTransitionAnimation;
    private boolean openedFromProfile;
    private boolean openedFullScreenVideo;
    private boolean opennedFromMedia;
    private OrientationEventListener orientationEventListener;
    private volatile int originalBitrate;
    private volatile int originalHeight;
    private long originalSize;
    public TLRPC.Document originalSticker;
    private volatile int originalWidth;
    private BlurButton outlineBtn;
    private boolean padImageForHorizontalInsets;
    private PageBlocksAdapter pageBlocksAdapter;
    private ImageView paintItem;
    private AnimatorSet paintKeyboardAnimator;
    private KeyboardNotifier paintKeyboardNotifier;
    private int paintViewTouched;
    private PaintingOverlay paintingOverlay;
    private Activity parentActivity;
    private ChatAttachAlert parentAlert;
    private WindowVisibilityManager.Controller parentAlertWindowVisibilityController;
    private ChatActivity parentChatActivity;
    private BaseFragment parentFragment;
    private Runnable parentFragmentNullifier;
    private boolean pauseOnMinimize;
    private PhotoCropView photoCropView;
    private PhotoFilterView photoFilterView;
    private LPhotoPaintView photoPaintView;
    private PhotoProgressView[] photoProgressViews;
    private PhotoViewerWebView photoViewerWebView;
    private CounterView photosCounterView;
    private FrameLayout pickerView;
    private ChatActivityEnterView.SendButton pickerViewSendButton;
    private float pinchCenterX;
    private float pinchCenterY;
    private float pinchStartAngle;
    private float pinchStartDistance;
    private float pinchStartRotate;
    private float pinchStartScale;
    private float pinchStartX;
    private float pinchStartY;
    private boolean pipAnimationInProgress;
    private boolean pipAvailable;
    public Runnable pipFirstFrameCallback;
    private ActionBarMenuSubItem pipItem;
    private View pipPlaceholderView;
    private int[] pipPosition;
    private PipSource pipSource;
    private TextureView pipTextureView;
    private boolean pipVideoOverlayAnimateFlag;
    private PhotoViewerProvider placeProvider;
    private View playButtonAccessibilityOverlay;
    private boolean playerAutoStarted;
    private boolean playerInjected;
    private boolean playerLooping;
    private boolean playerWasPlaying;
    private boolean playerWasReady;
    private PhotoViewerPollAttachButtons pollAttachButtons;
    private GradientDrawable[] pressedDrawable;
    private float[] pressedDrawableAlpha;
    private int prevOrientation;
    private int previousCompression;
    private boolean previousCropMirrored;
    private int previousCropOrientation;
    private float previousCropPh;
    private float previousCropPw;
    private float previousCropPx;
    private float previousCropPy;
    private float previousCropRotation;
    private float previousCropScale;
    private boolean previousHasTransform;
    private RadialProgressView progressView;
    private QualityChooseView qualityChooseView;
    private AnimatorSet qualityChooseViewAnimation;
    private PickerBottomLayoutViewer qualityPicker;
    private RenderNode renderNode;
    private RenderNode renderNodeBlurred;
    private RenderNode renderNodeGlassed;
    private final Blur3HashImpl renderNodeHashBuilder;
    public TLRPC.Document replacedSticker;
    private boolean requestingPreview;
    private TextView resetButton;
    private Theme.ResourcesProvider resourcesProvider;
    private BlurButton restoreBtn;
    private volatile int resultHeight;
    private volatile int resultWidth;
    private BlurringShader.ThumbBlurer rightBlur;
    private MediaController.CropState rightCropState;
    private CropTransform rightCropTransform;
    private ImageReceiver rightImage;
    private boolean rightImageIsVideo;
    private PaintingOverlay rightPaintingOverlay;
    private float rotate;
    private ImageView rotateItem;
    private int rotationValue;
    private float savedRotation;
    private float savedScale;
    private SavedState savedState;
    private float savedTx;
    private float savedTy;
    private float scale;
    private Scroller scroller;
    private ActionBarPopupWindow.GapView searchGap;
    private ActionBarMenuSubItem searchItem;
    SearchPhotoPopupWrapper searchPhotoPopupWrapper;
    private final ArrayList<SecureDocument> secureDocuments;
    private SeekSpeedDrawable seekSpeedDrawable;
    private float seekToProgressPending;
    private float seekToProgressPending2;
    private volatile int selectedCompression;
    private ArrayList<String> selectedEmojis;
    private ListAdapter selectedPhotosAdapter;
    private SelectedPhotosListView selectedPhotosListView;
    private ActionBarMenuItem sendItem;
    private int sendPhotoType;
    private boolean sendPhotoTypeIsGif;
    private boolean sendPhotoTypeIsPollMedia;
    private boolean sendPhotoTypeIsPollMediaEdit;
    private ImageUpdater.AvatarFor setAvatarFor;
    private Runnable setLoadingRunnable;
    private BlurringShader.StoryBlurDrawer shadowBlurer;
    private int sharedMediaType;
    private float shiftDp;
    private String shouldSavePositionForCurrentVideo;
    private String shouldSavePositionForCurrentVideoShortTerm;
    private PlaceProviderObject showAfterAnimation;
    private boolean shownControlsByEnd;
    private ImageReceiver sideImage;
    private boolean skipFirstBufferingProgress;
    public boolean skipLastFrameDraw;
    private int slideshowMessageId;
    private ActionBarPopupWindow.GapView speedGap;
    private ActionBarMenuSlider.SpeedSlider speedItem;
    private int startOffset;
    private boolean startReached;
    private long startTime;
    private long startedPlayTime;
    public boolean stickerEmpty;
    public boolean stickerEmptySent;
    private StickerMakerBackgroundView stickerMakerBackgroundView;
    public StickerMakerView stickerMakerView;
    private ImageView stickerRoundItem;
    private boolean streamingAlertShown;
    private ActionBarMenuSubItem subtitlesItem;
    private Paint surfaceBlackoutPaint;
    private TextureView.SurfaceTextureListener surfaceTextureListener;
    private int switchImageAfterAnimation;
    private final Runnable switchToInlineRunnable;
    private boolean switchingInlineMode;
    private int switchingToIndex;
    private int switchingToMode;
    private int[] tempInt;
    private TextSelectionHelper.SimpleTextSelectionHelper textSelectionHelper;
    private ImageView textureImageView;
    private boolean textureUploaded;
    private boolean textureViewSkipRender;
    private Tooltip tooltip;
    private FrameLayout topBulletinUnderCaption;
    private CaptionPhotoViewer topCaptionEdit;
    private final float[] topCaptionEditAlpha;
    private FrameLayout topCaptionEditContainer;
    private LinearGradient topFancyShadow;
    private Matrix topFancyShadowMatrix;
    private Paint topFancyShadowPaint;
    private long topicId;
    private int totalImagesCount;
    private int totalImagesCountMerge;
    private int touchSlop;
    private long transitionAnimationStartTime;
    private AnimationNotificationsLocker transitionNotificationLocker;
    private float translateY;
    private ValueAnimator translateYAnimator;
    private float translationX;
    private float translationY;
    private boolean tryStartRequestPreviewOnFinish;
    private ImageView tuneItem;
    private BlurButton undoBtn;
    private final Runnable updateContainerFlagsRunnable;
    private Runnable updateProgressRunnable;
    private boolean usedSurfaceView;
    private VelocityTracker velocityTracker;
    private TextView videoAvatarTooltip;
    private volatile boolean videoConvertSupported;
    private float videoCrossfadeAlpha;
    private long videoCrossfadeAlphaLastTime;
    private boolean videoCrossfadeStarted;
    private float videoCutEnd;
    private float videoCutStart;
    private float videoDuration;
    private VideoForwardDrawable videoForwardDrawable;
    private Bitmap videoFrameBitmap;
    private Paint videoFrameBitmapPaint;
    private int videoFramerate;
    private long videoFramesSize;
    private int videoHeight;
    private ActionBarMenuItem videoItem;
    private ChooseQualityLayout$QualityIcon videoItemIcon;
    private Runnable videoPlayRunnable;
    private VideoPlayer videoPlayer;
    private Animator videoPlayerControlAnimator;
    private VideoPlayerControlFrameLayout videoPlayerControlFrameLayout;
    private boolean videoPlayerControlVisible;
    private int[] videoPlayerCurrentTime;
    private final VideoPlayerRewinder videoPlayerRewinder;
    private VideoPlayerSeekBar videoPlayerSeekbar;
    private View videoPlayerSeekbarView;
    private SimpleTextView videoPlayerTime;
    private int[] videoPlayerTotalTime;
    private VideoSeekPreviewImage videoPreviewFrame;
    private AnimatorSet videoPreviewFrameAnimation;
    private MessageObject videoPreviewMessageObject;
    private final ArrayList<ActionBarMenuSubItem> videoQualityItems;
    private LinearLayout videoQualityLayout;
    private boolean videoSizeSet;
    private TextView videoSubtitlesView;
    private SurfaceView videoSurfaceView;
    private TextureView videoTextureView;
    private ObjectAnimator videoTimelineAnimator;
    private VideoTimelinePlayView videoTimelineView;
    private FrameLayout videoTimelineViewContainer;
    private int videoWidth;
    private AlertDialog visibleDialog;
    private int waitingForDraw;
    private int waitingForFirstTextureUpload;
    private boolean wasCountViewShown;
    private boolean wasLayout;
    private boolean wasRotated;
    private WindowManager.LayoutParams windowLayoutParams;
    public FrameLayout windowView;
    private boolean windowViewSkipRender;
    private boolean zoomAnimation;
    private boolean zooming;
    public static Paint bitmapPaint = new Paint(2);
    private static final HashMap<String, SavedVideoPosition> savedVideoPositions = new HashMap<>();
    private static final Property<VideoPlayerControlFrameLayout, Float> VPC_PROGRESS = new FloatProperty<VideoPlayerControlFrameLayout>("progress") { // from class: org.telegram.ui.PhotoViewer.12
        @Override // android.util.FloatProperty
        public void setValue(VideoPlayerControlFrameLayout videoPlayerControlFrameLayout, float f) {
            videoPlayerControlFrameLayout.setProgress(f);
        }

        @Override // android.util.Property
        public Float get(VideoPlayerControlFrameLayout videoPlayerControlFrameLayout) {
            return Float.valueOf(videoPlayerControlFrameLayout.getProgress());
        }
    };

    @SuppressLint({"StaticFieldLeak"})
    private static volatile PhotoViewer Instance = null;
    private static volatile PhotoViewer PipInstance = null;
    private static volatile PhotoViewer Instance2 = null;

    public static class EmptyPhotoViewerProvider implements PhotoViewerProvider {
        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean allowCaption() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean allowSendingSubmenu() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canCaptureMorePhotos() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canEdit(int i) {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canReplace(int i) {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canScrollAway() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean cancelButtonPressed() {
            return true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean closeKeyboard() {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void deleteImageAtIndex(int i) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public String getDeleteMessageString() {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public MessageObject getEditingMessageObject() {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getPhotoIndex(int i) {
            return -1;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z, boolean z2) {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getSelectedCount() {
            return 0;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public HashMap<Object, Object> getSelectedPhotos() {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public ArrayList<Object> getSelectedPhotosOrder() {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public CharSequence getSubtitleFor(int i) {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i) {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public CharSequence getTitleFor(int i) {
            return null;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getTotalImageCount() {
            return -1;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean isPhotoChecked(int i) {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean loadMore() {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void needAddMorePhotos() {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onApplyCaption(CharSequence charSequence) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onClose() {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void onOpen() {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void openPhotoForEdit(String str, String str2, boolean z) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void replaceButtonPressed(int i, VideoEditedInfo videoEditedInfo) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean scaleToFill() {
            return false;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, int i3, boolean z2) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo) {
            return -1;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int setPhotoUnchecked(Object obj) {
            return -1;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void updatePhotoAtIndex(int i) {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void willHidePhotoViewer() {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i) {
        }
    }

    public interface PageBlocksAdapter {
        TL_iv.PageBlock get(int i);

        List<TL_iv.PageBlock> getAll();

        CharSequence getCaption(int i);

        File getFile(int i);

        TLRPC.PhotoSize getFileLocation(TLObject tLObject, int[] iArr);

        String getFileName(int i);

        int getItemsCount();

        TLObject getMedia(int i);

        Object getParentObject();

        boolean isHardwarePlayer(int i);

        boolean isVideo(int i);

        void updateSlideshowCell(TL_iv.PageBlock pageBlock);
    }

    public interface PhotoViewerProvider {
        boolean allowCaption();

        default boolean allowLivePhotos() {
            return false;
        }

        boolean allowSendingSubmenu();

        boolean canCaptureMorePhotos();

        boolean canEdit(int i);

        default boolean canLoadMoreAvatars() {
            return true;
        }

        default boolean canMoveCaptionAbove() {
            return false;
        }

        boolean canReplace(int i);

        default boolean canSchedule() {
            return false;
        }

        boolean canScrollAway();

        default boolean canSetTimer() {
            return false;
        }

        boolean cancelButtonPressed();

        boolean closeKeyboard();

        void deleteImageAtIndex(int i);

        default boolean forceAllInGroup() {
            return false;
        }

        String getDeleteMessageString();

        default long getDialogId() {
            return 0L;
        }

        MessageObject getEditingMessageObject();

        int getPhotoIndex(int i);

        PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z, boolean z2);

        int getSelectedCount();

        HashMap<Object, Object> getSelectedPhotos();

        ArrayList<Object> getSelectedPhotosOrder();

        CharSequence getSubtitleFor(int i);

        ImageReceiver.BitmapHolder getThumbForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i);

        CharSequence getTitleFor(int i);

        int getTotalImageCount();

        default boolean isCaptionAbove() {
            return false;
        }

        default boolean isEditingMessage() {
            return false;
        }

        default boolean isEditingMessageResend() {
            return false;
        }

        default boolean isEditingSticker() {
            return false;
        }

        boolean isPhotoChecked(int i);

        boolean loadMore();

        default void moveCaptionAbove(boolean z) {
        }

        void needAddMorePhotos();

        void onApplyCaption(CharSequence charSequence);

        void onClose();

        default boolean onDeletePhoto(int i) {
            return true;
        }

        default void onEditModeChanged(boolean z) {
        }

        void onOpen();

        default void onPollAttachDelete() {
        }

        default void onPollAttachReplace() {
        }

        default void onPreClose() {
        }

        default void onPreOpen() {
        }

        default void onReleasePlayerBeforeClose(int i) {
        }

        void openPhotoForEdit(String str, String str2, boolean z);

        void replaceButtonPressed(int i, VideoEditedInfo videoEditedInfo);

        boolean scaleToFill();

        void sendButtonPressed(int i, VideoEditedInfo videoEditedInfo, boolean z, int i2, int i3, boolean z2);

        int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo);

        int setPhotoUnchecked(Object obj);

        default void spoilerPressed() {
        }

        void updatePhotoAtIndex(int i);

        default void updatedLivePhotos() {
        }

        void willHidePhotoViewer();

        void willSwitchFromPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i);
    }

    public static class PlaceProviderObject {
        public ClippingImageView animatingImageView;
        public int animatingImageViewYOffset;
        public boolean canEdit;
        public int clipBottomAddition;
        public int clipTopAddition;
        public long dialogId;
        public boolean fadeIn;
        public ImageReceiver imageReceiver;
        public boolean isEvent;
        public boolean keepImageReceiverVisible;
        public View parentView;
        public int[] radius;
        public long size;
        public int starOffset;
        public ImageReceiver.BitmapHolder thumb;
        public int viewX;
        public int viewY;
        public float scale = 1.0f;
        public boolean allowTakeAnimation = true;
    }

    public static void lambda$new$0() {
            setTemporarilyShown(false, true);
        }

        public void updateShow(boolean z, boolean z2) {
            if (this.shown != z) {
                this.shown = z;
                if (!z) {
                    this.nextNotAnimate = true;
                    AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
                    this.temporarilyShown = false;
                    if (!z2) {
                        this.temporaryShowT.set(0.0f, true);
                    }
                } else {
                    showTemporarily(z2);
                }
                if (!z2) {
                    this.showT.set(z ? 1.0f : 0.0f, true);
                }
                invalidate();
            }
        }

        @Override // android.view.View
        public boolean isShown() {
            return this.shown;
        }

        private void showTemporarily(boolean z) {
            if (this.shown) {
                AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
                setTemporarilyShown(true, z);
                AndroidUtilities.runOnUIThread(this.hideRunnable, 2000L);
            }
        }

        private void setTemporarilyShown(boolean z, boolean z2) {
            if (this.temporarilyShown == z && z2) {
                return;
            }
            this.temporarilyShown = z;
            if (!z2) {
                this.temporaryShowT.set(z ? 1.0f : 0.0f, true);
            }
            invalidate();
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            float fMax = Math.max(0.0f, Math.min(1.0f, this.showT.set(this.shown ? 1.0f : 0.0f) * this.temporaryShowT.set(this.temporarilyShown ? 1.0f : 0.0f)));
            if (fMax <= 0.0f) {
                return;
            }
            int i = (int) (255.0f * fMax);
            float currentWidth = this.left.getCurrentWidth() + this.centerWidth + this.right.getCurrentWidth() + AndroidUtilities.dp(18.0f);
            float f = this.marginTop + ((1.0f - fMax) * (-AndroidUtilities.dp(8.0f)));
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set((getWidth() - currentWidth) / 2.0f, AndroidUtilities.dpf2(10.0f) + f, (getWidth() + currentWidth) / 2.0f, AndroidUtilities.dpf2(33.0f) + f);
            int alpha = this.backgroundPaint.getAlpha();
            this.backgroundPaint.setAlpha((int) (alpha * fMax));
            canvas.drawRoundRect(rectF, AndroidUtilities.dpf2(12.0f), AndroidUtilities.dpf2(12.0f), this.backgroundPaint);
            this.backgroundPaint.setAlpha(alpha);
            canvas.save();
            canvas.translate(((getWidth() - currentWidth) / 2.0f) + AndroidUtilities.dp(9.0f), f + AndroidUtilities.dp(9.5f));
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = this.left;
            animatedTextDrawable.setBounds(0, 0, (int) animatedTextDrawable.getCurrentWidth(), AndroidUtilities.dp(23.0f));
            this.left.setAlpha(i);
            this.left.draw(canvas);
            canvas.translate(this.left.getCurrentWidth(), 0.0f);
            canvas.save();
            canvas.translate((-(this.center.getWidth() - this.centerWidth)) / 2.0f, (AndroidUtilities.dp(23.0f) - this.center.getHeight()) / 2.0f);
            this.paint.setAlpha(i);
            this.center.draw(canvas);
            canvas.restore();
            canvas.translate(this.centerWidth, 0.0f);
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable2 = this.right;
            animatedTextDrawable2.setBounds(0, 0, (int) animatedTextDrawable2.getCurrentWidth(), AndroidUtilities.dp(23.0f));
            this.right.setAlpha(i);
            this.right.draw(canvas);
            canvas.restore();
            this.paint.setAlpha(255);
        }

        @Override // android.view.View
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            this.marginTop = ActionBar.getCurrentActionBarHeight() + AndroidUtilities.statusBarHeight;
            this.left.setOverrideFullWidth(size);
            this.right.setOverrideFullWidth(size);
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(size, TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(this.marginTop + AndroidUtilities.dp(43.0f), TLObject.FLAG_30));
        }

        @Override // android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            AndroidUtilities.cancelRunOnUIThread(this.hideRunnable);
        }
    }

    public void requestAudioFocus(boolean z) {
        MessageObject messageObject;
        if (z && !shouldManageAudioFocus()) {
            if (this.audioManager.abandonAudioFocus(this) == 1) {
                this.hasAudioFocus = false;
            }
        } else {
            if (z && (messageObject = this.currentMessageObject) != null && !messageObject.isGif()) {
                if (SharedConfig.pauseMusicOnMedia && this.audioManager.requestAudioFocus(this, 3, 2) == 1) {
                    this.hasAudioFocus = true;
                    return;
                }
                return;
            }
            if (this.audioManager.abandonAudioFocus(this) == 1) {
                this.hasAudioFocus = false;
            }
        }
    }

    @Override // android.media.AudioManager.OnAudioFocusChangeListener
    public void onAudioFocusChange(final int i) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda72
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onAudioFocusChange$1(i);
            }
        });
    }

    public void lambda$onLinkLongPress$2(ClickableSpan clickableSpan, TextView textView, String str, boolean z, DialogInterface dialogInterface, int i) {
        String string;
        if (i == 0) {
            onLinkClick(clickableSpan, textView);
            return;
        }
        if (i == 1) {
            AndroidUtilities.addToClipboard(str);
            if (z) {
                string = LocaleController.getString("PhoneCopied", R.string.PhoneCopied);
            } else if (str.startsWith("#")) {
                string = LocaleController.getString("HashtagCopied", R.string.HashtagCopied);
            } else if (str.startsWith("@")) {
                string = LocaleController.getString("UsernameCopied", R.string.UsernameCopied);
            } else {
                string = LocaleController.getString("LinkCopied", R.string.LinkCopied);
            }
            if (AndroidUtilities.shouldShowClipboardToast()) {
                BulletinFactory.of(this.containerView, this.resourcesProvider).createSimpleBulletin(R.raw.voip_invite, string).show();
            }
        }
    }

    public void cancelFlashAnimations() {
        View view = this.flashView;
        if (view != null) {
            view.animate().setListener(null).cancel();
            this.flashView.setAlpha(0.0f);
        }
        AnimatorSet animatorSet = this.flashAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.flashAnimator = null;
        }
        PhotoCropView photoCropView = this.photoCropView;
        if (photoCropView != null) {
            photoCropView.cancelThumbAnimation();
        }
    }

    public void cancelVideoPlayRunnable() {
        Runnable runnable = this.videoPlayRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.videoPlayRunnable = null;
        }
    }

    public long getCurrentVideoPosition() {
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
            return this.photoViewerWebView.getCurrentPosition();
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return 0L;
        }
        return videoPlayer.getCurrentPosition();
    }

    public long getVideoDuration() {
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
            return this.photoViewerWebView.getVideoDuration();
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return 0L;
        }
        return videoPlayer.getDuration();
    }

    public void seekVideoOrWebTo(long j) {
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
            this.photoViewerWebView.seekTo(j);
        } else {
            VideoPlayer videoPlayer = this.videoPlayer;
            if (videoPlayer != null) {
                videoPlayer.seekTo(j);
            }
        }
        updateVideoPlayerTime();
    }

    public boolean isVideoPlaying() {
        PhotoViewerWebView photoViewerWebView = this.photoViewerWebView;
        if (photoViewerWebView != null && photoViewerWebView.isControllable()) {
            return this.photoViewerWebView.isPlaying();
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        return videoPlayer != null && videoPlayer.isPlaying();
    }

    public class AnonymousClass6 implements Runnable {
        public AnonymousClass6() {
        }

        @Override // java.lang.Runnable
        public void run() {
            float bufferedProgressFromPosition;
            if (PhotoViewer.this.videoPlayer != null || (PhotoViewer.this.photoViewerWebView != null && PhotoViewer.this.photoViewerWebView.isControllable())) {
                boolean z = PhotoViewer.this.isCurrentVideo;
                PhotoViewer photoViewer = PhotoViewer.this;
                if (z) {
                    if (!photoViewer.videoTimelineView.isDragging()) {
                        float currentVideoPosition = (!PhotoViewer.this.shownControlsByEnd || PhotoViewer.this.actionBarWasShownBeforeByEnd) ? PhotoViewer.this.getCurrentVideoPosition() / PhotoViewer.this.getVideoDuration() : 0.0f;
                        if (!PhotoViewer.this.inPreview && (PhotoViewer.this.currentEditMode != 0 || PhotoViewer.this.videoTimelineViewContainer.getVisibility() == 0)) {
                            float rightProgress = PhotoViewer.this.videoTimelineView.getRightProgress();
                            PhotoViewer photoViewer2 = PhotoViewer.this;
                            if (currentVideoPosition >= rightProgress) {
                                photoViewer2.videoTimelineView.setProgress(PhotoViewer.this.videoTimelineView.getLeftProgress());
                                PhotoViewer.this.videoPlayer.seekTo((int) (PhotoViewer.this.videoTimelineView.getLeftProgress() * PhotoViewer.this.getVideoDuration()));
                                PhotoViewer.this.manuallyPaused = false;
                                PhotoViewer.this.cancelVideoPlayRunnable();
                                if (PhotoViewer.this.muteVideo || PhotoViewer.this.sendPhotoType == 1 || PhotoViewer.this.currentEditMode != 0 || PhotoViewer.this.switchingToMode > 0) {
                                    PhotoViewer.this.playVideoOrWeb();
                                } else {
                                    PhotoViewer.this.pauseVideoOrWeb();
                                }
                                PhotoViewer.this.containerView.invalidate();
                            } else {
                                photoViewer2.videoTimelineView.setProgress(currentVideoPosition);
                            }
                        } else if (PhotoViewer.this.sendPhotoType != 1) {
                            PhotoViewer.this.videoTimelineView.setProgress(currentVideoPosition);
                        }
                        PhotoViewer.this.updateVideoPlayerTime();
                    }
                } else {
                    final float currentVideoPosition2 = photoViewer.getCurrentVideoPosition() / PhotoViewer.this.getVideoDuration();
                    if (PhotoViewer.this.shownControlsByEnd && !PhotoViewer.this.actionBarWasShownBeforeByEnd) {
                        currentVideoPosition2 = 0.0f;
                    }
                    if (PhotoViewer.this.currentVideoFinishedLoading) {
                        bufferedProgressFromPosition = 1.0f;
                    } else {
                        long jElapsedRealtime = SystemClock.elapsedRealtime();
                        if (Math.abs(jElapsedRealtime - PhotoViewer.this.lastBufferedPositionCheck) >= 500) {
                            if (PhotoViewer.this.photoViewerWebView != null && PhotoViewer.this.photoViewerWebView.isControllable()) {
                                bufferedProgressFromPosition = PhotoViewer.this.photoViewerWebView.getBufferedPosition();
                            } else if (PhotoViewer.this.isStreaming) {
                                bufferedProgressFromPosition = FileLoader.getInstance(PhotoViewer.this.currentAccount).getBufferedProgressFromPosition(PhotoViewer.this.seekToProgressPending != 0.0f ? PhotoViewer.this.seekToProgressPending : currentVideoPosition2, PhotoViewer.this.currentFileNames[0]);
                            } else {
                                bufferedProgressFromPosition = 1.0f;
                            }
                            PhotoViewer.this.lastBufferedPositionCheck = jElapsedRealtime;
                        } else {
                            bufferedProgressFromPosition = -1.0f;
                        }
                    }
                    if (PhotoViewer.this.inPreview || PhotoViewer.this.videoTimelineViewContainer.getVisibility() != 0) {
                        if (PhotoViewer.this.seekToProgressPending == 0.0f) {
                            PhotoViewer photoViewer3 = PhotoViewer.this;
                            OldVideoPlayerRewinder oldVideoPlayerRewinder = photoViewer3.longVideoPlayerRewinder;
                            if (oldVideoPlayerRewinder.rewindCount == 0 || (!oldVideoPlayerRewinder.rewindByBackSeek && !photoViewer3.videoPlayerRewinder.rewindByBackSeek)) {
                                PhotoViewer.this.videoPlayerSeekbar.setProgress(currentVideoPosition2, false);
                            }
                        }
                        if (bufferedProgressFromPosition != -1.0f) {
                            PhotoViewer.this.videoPlayerSeekbar.setBufferedProgress(bufferedProgressFromPosition);
                            PipVideoOverlay.setBufferedProgress(bufferedProgressFromPosition);
                        }
                    } else {
                        float rightProgress2 = PhotoViewer.this.videoTimelineView.getRightProgress();
                        PhotoViewer photoViewer4 = PhotoViewer.this;
                        if (currentVideoPosition2 >= rightProgress2) {
                            photoViewer4.manuallyPaused = false;
                            PhotoViewer.this.pauseVideoOrWeb();
                            PhotoViewer.this.videoPlayerSeekbar.setProgress(0.0f);
                            PhotoViewer photoViewer5 = PhotoViewer.this;
                            photoViewer5.seekVideoOrWebTo((int) (photoViewer5.videoTimelineView.getLeftProgress() * PhotoViewer.this.getVideoDuration()));
                            PhotoViewer.this.containerView.invalidate();
                        } else {
                            float leftProgress = currentVideoPosition2 - photoViewer4.videoTimelineView.getLeftProgress();
                            if (leftProgress < 0.0f) {
                                leftProgress = 0.0f;
                            }
                            currentVideoPosition2 = leftProgress / (PhotoViewer.this.videoTimelineView.getRightProgress() - PhotoViewer.this.videoTimelineView.getLeftProgress());
                            if (currentVideoPosition2 > 1.0f) {
                                currentVideoPosition2 = 1.0f;
                            }
                            PhotoViewer.this.videoPlayerSeekbar.setProgress(currentVideoPosition2);
                        }
                    }
                    PhotoViewer.this.videoPlayerSeekbarView.invalidate();
                    if (PhotoViewer.this.shouldSavePositionForCurrentVideo != null && currentVideoPosition2 >= 0.0f && SystemClock.elapsedRealtime() - PhotoViewer.this.lastSaveTime >= 1000) {
                        final String str = PhotoViewer.this.shouldSavePositionForCurrentVideo;
                        PhotoViewer.this.lastSaveTime = SystemClock.elapsedRealtime();
                        if (PhotoViewer.this.currentMessageObject != null) {
                            PhotoViewer.this.currentMessageObject.cachedSavedTimestamp = Float.valueOf(currentVideoPosition2);
                        }
                        Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PhotoViewer$6$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                ApplicationLoader.applicationContext.getSharedPreferences("media_saved_pos", 0).edit().putFloat(str, currentVideoPosition2).apply();
                            }
                        });
                    }
                    PhotoViewer.this.updateVideoPlayerTime();
                }
            }
            if (PhotoViewer.this.firstFrameView != null) {
                PhotoViewer.this.firstFrameView.updateAlpha();
            }
            if (PhotoViewer.this.isPlaying) {
                AndroidUtilities.runOnUIThread(PhotoViewer.this.updateProgressRunnable, 17L);
            }
        }
    }

    public class AnonymousClass7 implements Runnable {
        public AnonymousClass7() {
        }

        @Override // java.lang.Runnable
        public void run() {
            if (PipVideoOverlay.isVisible()) {
                PipVideoOverlay.dismiss();
                AndroidUtilities.runOnUIThread(this, 250L);
                return;
            }
            PhotoViewer.this.switchingInlineMode = false;
            if (PhotoViewer.this.currentBitmap != null) {
                PhotoViewer.this.currentBitmap.recycle();
                PhotoViewer.this.currentBitmap = null;
            }
            PhotoViewer.this.changingTextureView = true;
            final TextureViewContainer textureViewContainer = new TextureViewContainer(PhotoViewer.this.parentActivity);
            try {
                boolean z = PhotoViewer.this.usedSurfaceView;
                PhotoViewer photoViewer = PhotoViewer.this;
                if (z) {
                    Drawable drawable = photoViewer.textureImageView.getDrawable();
                    boolean z2 = drawable instanceof BitmapDrawable;
                    PhotoViewer photoViewer2 = PhotoViewer.this;
                    if (z2) {
                        photoViewer2.currentBitmap = ((BitmapDrawable) drawable).getBitmap();
                        if (PhotoViewer.this.currentBitmap != null) {
                            if (PhotoViewer.this.textureImageView != null) {
                                PhotoViewer.this.textureImageView.setVisibility(0);
                                PhotoViewer.this.textureImageView.setImageBitmap(PhotoViewer.this.currentBitmap);
                            }
                            textureViewContainer.imageReceiver.setImageBitmap(PhotoViewer.this.currentBitmap);
                        }
                    } else {
                        photoViewer2.currentBitmap = Bitmaps.createBitmap(photoViewer2.videoSurfaceView.getWidth(), PhotoViewer.this.videoSurfaceView.getHeight(), Bitmap.Config.ARGB_8888);
                        AndroidUtilities.getBitmapFromSurface(PhotoViewer.this.videoSurfaceView, PhotoViewer.this.currentBitmap, new Runnable() { // from class: org.telegram.ui.PhotoViewer$7$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$run$0(textureViewContainer);
                            }
                        });
                    }
                } else {
                    photoViewer.currentBitmap = Bitmaps.createBitmap(photoViewer.videoTextureView.getWidth(), PhotoViewer.this.videoTextureView.getHeight(), Bitmap.Config.ARGB_8888);
                    PhotoViewer.this.videoTextureView.getBitmap(PhotoViewer.this.currentBitmap);
                    if (PhotoViewer.this.currentBitmap != null) {
                        if (PhotoViewer.this.textureImageView != null) {
                            PhotoViewer.this.textureImageView.setVisibility(0);
                            PhotoViewer.this.textureImageView.setImageBitmap(PhotoViewer.this.currentBitmap);
                        }
                        textureViewContainer.imageReceiver.setImageBitmap(PhotoViewer.this.currentBitmap);
                    }
                }
            } catch (Throwable th) {
                if (PhotoViewer.this.currentBitmap != null) {
                    PhotoViewer.this.currentBitmap.recycle();
                    PhotoViewer.this.currentBitmap = null;
                }
                FileLog.e(th);
            }
            PhotoViewer.this.isInline = true;
            PhotoViewer photoViewer3 = PhotoViewer.this;
            photoViewer3.changedTextureView = textureViewContainer.textureView;
            if (PipVideoOverlay.show(false, photoViewer3.parentActivity, textureViewContainer, PhotoViewer.this.videoWidth, PhotoViewer.this.videoHeight, PhotoViewer.this.pipVideoOverlayAnimateFlag)) {
                PipVideoOverlay.setPhotoViewer(PhotoViewer.this);
            }
            PhotoViewer.this.pipVideoOverlayAnimateFlag = true;
            boolean z3 = PhotoViewer.this.usedSurfaceView;
            PhotoViewer photoViewer4 = PhotoViewer.this;
            if (z3) {
                if (photoViewer4.aspectRatioFrameLayout != null) {
                    PhotoViewer.this.aspectRatioFrameLayout.removeView(PhotoViewer.this.videoTextureView);
                    PhotoViewer.this.aspectRatioFrameLayout.removeView(PhotoViewer.this.videoSurfaceView);
                }
                PhotoViewer.this.videoPlayer.setSurfaceView(null);
                PhotoViewer.this.videoPlayer.setTextureView(null);
                PhotoViewer.this.videoPlayer.play();
                PhotoViewer.this.videoPlayer.setTextureView(PhotoViewer.this.changedTextureView);
                PhotoViewer.this.checkChangedTextureView(true);
                PhotoViewer.this.changedTextureView.setVisibility(0);
                return;
            }
            photoViewer4.changedTextureView.setVisibility(4);
            if (PhotoViewer.this.aspectRatioFrameLayout != null) {
                PhotoViewer.this.aspectRatioFrameLayout.removeView(PhotoViewer.this.videoTextureView);
                PhotoViewer.this.aspectRatioFrameLayout.removeView(PhotoViewer.this.videoSurfaceView);
            }
        }

        public void lambda$onItemClick$2(Uri uri) {
            BulletinFactory.createSaveToGalleryBulletin(PhotoViewer.this.containerView, false, true, -115203550, -1).show();
        }

        public void lambda$onItemClick$5(int[] iArr, int[] iArr2, boolean z, boolean z2, boolean z3) {
            int i = iArr[0] + 1;
            iArr[0] = i;
            if (i == iArr2[0]) {
                BulletinFactory.createSaveMediaToGalleryBulletin(PhotoViewer.this.containerView, iArr2[0], z, z2, z3, -115203550, -1).show();
            }
        }

        public void lambda$onItemClick$17(Theme.ResourcesProvider resourcesProvider) {
            BulletinFactory.of(PhotoViewer.this.containerView, null).createCopyBulletin(LocaleController.getString(R.string.FrameCopied), resourcesProvider).show();
        }

        public void lambda$setParentActivity$15(Uri uri) {
        BulletinFactory.createSaveToGalleryBulletin((FrameLayout) this.containerView, true, -115203550, -1).show();
    }

    public void lambda$setParentActivity$21(View view) {
        this.selectedCompression = this.previousCompression;
        didChangedCompressionLevel(false);
        showQualityView(false);
        requestVideoPreview(2);
    }

    public void lambda$setParentActivity$29(final String str, final MediaController.PhotoEntry photoEntry, final long j, Bitmap bitmap) {
        if (bitmap == null) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda195
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setParentActivity$26();
                }
            });
            return;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(str));
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
            fileOutputStream.close();
            final Bitmap bitmapCreateBitmap = Bitmap.createBitmap(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(26.0f), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmapCreateBitmap);
            Paint paint = new Paint(3);
            canvas.translate(bitmapCreateBitmap.getWidth() / 2.0f, bitmapCreateBitmap.getHeight() / 2.0f);
            float fMax = Math.max(bitmapCreateBitmap.getWidth() / bitmap.getWidth(), bitmapCreateBitmap.getHeight() / bitmap.getHeight());
            canvas.scale(fMax, fMax);
            canvas.drawBitmap(bitmap, (-bitmap.getWidth()) / 2.0f, (-bitmap.getHeight()) / 2.0f, paint);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda197
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setParentActivity$28(photoEntry, j, str, bitmapCreateBitmap);
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda196
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setParentActivity$27();
                }
            });
        }
    }

    public boolean lambda$setParentActivity$74(final Theme.ResourcesProvider resourcesProvider, View view) {
        TLRPC.User user;
        boolean zCanSchedule;
        boolean z;
        ChatActivity chatActivity;
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        if ((photoViewerProvider != null && !photoViewerProvider.allowSendingSubmenu()) || this.sendPhotoType == 11) {
            return false;
        }
        BaseFragment baseFragment = this.parentFragment;
        boolean z2 = (baseFragment == null || baseFragment.getLastStoryViewer() == null) ? false : true;
        ChatActivity chatActivity2 = this.parentChatActivity;
        if (chatActivity2 != null && chatActivity2.isInScheduleMode()) {
            return false;
        }
        if ((this.parentChatActivity == null && !z2 && this.placeProvider == null) || this.captionEdit.isCaptionOverLimit()) {
            return false;
        }
        ChatActivity chatActivity3 = this.parentChatActivity;
        if (chatActivity3 != null) {
            user = chatActivity3.getCurrentUser();
            zCanSchedule = this.parentChatActivity.canScheduleMessage();
        } else {
            PhotoViewerProvider photoViewerProvider2 = this.placeProvider;
            if (photoViewerProvider2 == null) {
                return false;
            }
            long dialogId = photoViewerProvider2.getDialogId();
            user = dialogId != 0 ? MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(dialogId)) : null;
            zCanSchedule = this.placeProvider.canSchedule();
        }
        PhotoViewerProvider photoViewerProvider3 = this.placeProvider;
        boolean z3 = photoViewerProvider3 != null && photoViewerProvider3.canEdit(this.currentIndex);
        PhotoViewerProvider photoViewerProvider4 = this.placeProvider;
        boolean z4 = photoViewerProvider4 != null && photoViewerProvider4.canReplace(this.currentIndex);
        boolean zIsUserSelf = UserObject.isUserSelf(user);
        final Object obj = this.imagesArrLocals.get(this.currentIndex);
        boolean z5 = (obj instanceof MediaController.PhotoEntry) && ((chatActivity = this.parentChatActivity) == null || !chatActivity.isSecretChat());
        boolean z6 = z5 && ((MediaController.PhotoEntry) obj).hasSpoiler;
        boolean zIsEmpty = TextUtils.isEmpty(this.captionEdit.getText());
        boolean z7 = this.isCurrentVideo;
        PhotoViewerProvider photoViewerProvider5 = this.placeProvider;
        if (photoViewerProvider5 == null || photoViewerProvider5.getSelectedPhotos() == null) {
            z = false;
        } else {
            Iterator<Map.Entry<Object, Object>> it = this.placeProvider.getSelectedPhotos().entrySet().iterator();
            while (true) {
                if (it.hasNext()) {
                    Object value = it.next().getValue();
                    if (value instanceof MediaController.PhotoEntry) {
                        if (((MediaController.PhotoEntry) value).ttl != 0) {
                            z = true;
                        }
                    } else if ((value instanceof MediaController.SearchImage) && ((MediaController.SearchImage) value).ttl != 0) {
                        z = true;
                    }
                } else {
                    z = false;
                }
            }
        }
        boolean z8 = (z3 || this.isCurrentVideo || this.captionEdit.hasTimer() || z6) ? false : true;
        boolean z9 = (z3 || !zCanSchedule || z) ? false : true;
        boolean z10 = ((z3 && z4) || zIsUserSelf) ? false : true;
        PhotoViewerProvider photoViewerProvider6 = this.placeProvider;
        final ItemOptions itemOptionsAddIf = ItemOptions.makeOptions(this.containerView, new DarkThemeResourceProvider(), view).addIf(z8, R.drawable.msg_sendfile, LocaleController.getString(photoViewerProvider6 != null && photoViewerProvider6.getSelectedCount() > 1 ? R.string.SendAsFiles : R.string.SendAsFile), new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda124
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setParentActivity$65();
            }
        }).addIf(z4, R.drawable.msg_send, LocaleController.getString(R.string.SendAsNewPhoto), new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda125
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setParentActivity$66();
            }
        }).addIf(z4, R.drawable.msg_replace, LocaleController.getString(R.string.ReplacePhoto), new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda126
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.replacePressed();
            }
        }).addIf(z9, R.drawable.msg_calendar2, LocaleController.getString(zIsUserSelf ? R.string.SetReminder : R.string.ScheduleMessage), new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda127
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.showScheduleDatePickerDialog();
            }
        }).addIf(z10, R.drawable.input_notify_off, LocaleController.getString(R.string.SendWithoutSound), new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda128
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setParentActivity$67();
            }
        });
        if (z5) {
            itemOptionsAddIf.add(z6 ? R.drawable.msg_spoiler_off : R.drawable.msg_spoiler, LocaleController.getString(z6 ? R.string.DisablePhotoSpoiler : R.string.EnablePhotoSpoiler), new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda129
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setParentActivity$68(obj);
                }
            });
        }
        if (!zIsEmpty) {
            final ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem((Context) this.parentActivity, false, false, (Theme.ResourcesProvider) new DarkThemeResourceProvider());
            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(R.string.TranslateTo), R.drawable.msg_translate);
            actionBarMenuSubItem.setSubtext(TranslatorUtils.getSendTargetLanguageTitle());
            actionBarMenuSubItem.setItemHeight(56);
            actionBarMenuSubItem.setRightIcon(R.drawable.msg_arrowright);
            actionBarMenuSubItem.getRightIcon().setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda130
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    this.f$0.lambda$setParentActivity$70(actionBarMenuSubItem, view2);
                }
            });
            actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda131
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    this.f$0.lambda$setParentActivity$71(itemOptionsAddIf, resourcesProvider, view2);
                }
            });
            itemOptionsAddIf.add(actionBarMenuSubItem);
        }
        if (z7) {
            itemOptionsAddIf.add(R.drawable.msg_filehq, LocaleController.getString(R.string.SendVideoWithoutCompression), new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda132
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setParentActivity$73();
                }
            });
        }
        if (itemOptionsAddIf.getItemsCount() == 0) {
            return false;
        }
        itemOptionsAddIf.setDismissOnMoveOutside(true);
        itemOptionsAddIf.setGravity(5).show();
        return true;
    }

    public void $r8$lambda$Y0qt1sVYgHsp8KvrRNESuw7wbgY(ActionBarMenuSubItem actionBarMenuSubItem, int i) {
        TranslatorUtils.setSendTargetLanguage(TranslatorUtils.getTargetLanguageCodeByIndex(i));
        actionBarMenuSubItem.setSubtext(TranslatorUtils.getSendTargetLanguageTitle());
    }

    public void lambda$setParentActivity$73() {
        PhotoViewerProvider photoViewerProvider;
        this.bitrate = -2;
        this.selectedCompression = -2;
        this.muteVideo = false;
        this.editState.reset();
        this.cropTransform = new CropTransform();
        PaintingOverlay paintingOverlay = this.paintingOverlay;
        if (paintingOverlay != null) {
            paintingOverlay.reset();
            this.paintingOverlay.setVisibility(8);
        }
        updateWidthHeightBitrateForCompression();
        updateVideoInfo();
        Object obj = this.imagesArrLocals.get(this.currentIndex);
        if (obj instanceof MediaController.MediaEditState) {
            MediaController.MediaEditState mediaEditState = (MediaController.MediaEditState) obj;
            mediaEditState.resetEdit();
            mediaEditState.editedInfo = getCurrentVideoEditedInfo();
        }
        int i = this.sendPhotoType;
        if ((i == 0 || i == 4) && (photoViewerProvider = this.placeProvider) != null) {
            photoViewerProvider.updatePhotoAtIndex(this.currentIndex);
        }
        showQualityView(false);
        requestVideoPreview(2);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda173
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setParentActivity$72();
            }
        }, 200L);
    }

    public void lambda$onError$0(AlertDialog alertDialog, int i) {
            try {
                AndroidUtilities.openForView(PhotoViewer.this.currentMessageObject, PhotoViewer.this.parentActivity, PhotoViewer.this.resourcesProvider, true);
                PhotoViewer.this.closePhoto(false, false);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onVideoSizeChanged(int i, int i2, int i3, float f) {
            if (PhotoViewer.this.aspectRatioFrameLayout != null) {
                if (i3 == 90 || i3 == 270) {
                    i2 = i;
                    i = i2;
                }
                float f2 = i * f;
                int i4 = (int) f2;
                PhotoViewer.this.videoWidth = i4;
                float f3 = i2;
                PhotoViewer.this.videoHeight = (int) (f * f3);
                if (PhotoViewer.this.pipSource != null) {
                    PhotoViewer.this.pipSource.setContentRatio(PhotoViewer.this.videoWidth, PhotoViewer.this.videoHeight);
                }
                PhotoViewer.this.aspectRatioFrameLayout.setAspectRatio(i2 == 0 ? 1.0f : f2 / f3, i3);
                if (PhotoViewer.this.videoTextureView instanceof VideoEditTextureView) {
                    ((VideoEditTextureView) PhotoViewer.this.videoTextureView).setHDRInfo(PhotoViewer.this.videoPlayer.getHDRStaticInfo(null));
                    ((VideoEditTextureView) PhotoViewer.this.videoTextureView).setVideoSize(i4, i2);
                    if (PhotoViewer.this.sendPhotoType == 1) {
                        PhotoViewer.this.setCropBitmap();
                    }
                }
                PhotoViewer.this.videoSizeSet = true;
            }
        }

        @Override // org.telegram.ui.Components.VideoPlayer.VideoPlayerDelegate
        public void onRenderedFirstFrame() {
            if (!PhotoViewer.this.textureUploaded) {
                PhotoViewer.this.textureUploaded = true;
                PhotoViewer.this.containerView.invalidate();
            }
            if (PhotoViewer.this.firstFrameView != null) {
                if (PhotoViewer.this.videoPlayer == null || !PhotoViewer.this.videoPlayer.isLooping()) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$59$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onRenderedFirstFrame$1();
                        }
                    }, 64L);
                }
            }
        }

        public void lambda$setVideoPlayerControlVisible$115(ValueAnimator valueAnimator) {
        this.videoPlayerControlFrameLayout.setAlpha(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private void updateCaptionTextForCurrentPhoto(Object obj) {
        CharSequence charSequence;
        if (this.hasCaptionForAllMedia) {
            charSequence = this.captionForAllMedia;
        } else if (obj instanceof MediaController.PhotoEntry) {
            charSequence = ((MediaController.PhotoEntry) obj).caption;
        } else {
            charSequence = (!(obj instanceof TLRPC.BotInlineResult) && (obj instanceof MediaController.SearchImage)) ? ((MediaController.SearchImage) obj).caption : null;
        }
        if (TextUtils.isEmpty(charSequence)) {
            getCaptionView().setText(_UrlKt.FRAGMENT_ENCODE_SET);
        } else {
            getCaptionView().setText(AnimatedEmojiSpan.cloneSpans(charSequence, 3));
        }
        getCaptionView().editText.getEditText().setAllowTextEntitiesIntersection(supportsSendingNewEntities());
    }

    public void showAlertDialog(AlertDialog.Builder builder) {
        if (this.parentActivity == null) {
            return;
        }
        try {
            AlertDialog alertDialog = this.visibleDialog;
            if (alertDialog != null) {
                alertDialog.dismiss();
                this.visibleDialog = null;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        try {
            AlertDialog alertDialogShow = builder.show();
            this.visibleDialog = alertDialogShow;
            alertDialogShow.setCanceledOnTouchOutside(true);
            this.visibleDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda162
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    this.f$0.lambda$showAlertDialog$116(dialogInterface);
                }
            });
        } catch (Exception e2) {
            FileLog.e(e2);
        }
    }

    public void lambda$createPaintView$130(Integer num) {
        this.photoPaintView.keyboardVisible = this.paintKeyboardNotifier.keyboardVisible();
        this.containerView.invalidate();
        int iMax = Math.max(num.intValue(), this.photoPaintView.getEmojiPadding(false));
        translateY((!this.photoPaintView.isCurrentText() || iMax <= 0) ? 0.0f : ((AndroidUtilities.displaySize.y - iMax) - AndroidUtilities.dp(80.0f)) - this.photoPaintView.getSelectedEntityBottom());
        AnimatorSet animatorSet = this.paintKeyboardAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda188
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$createPaintView$129(valueAnimator);
            }
        });
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.paintKeyboardAnimator = animatorSet2;
        PaintWeightChooserView paintWeightChooserView = this.photoPaintView.weightChooserView;
        Property property = View.TRANSLATION_Y;
        int i = -iMax;
        ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(paintWeightChooserView, (Property<PaintWeightChooserView, Float>) property, i / 2.5f);
        ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(this.photoPaintView.bottomLayout, (Property<FrameLayout, Float>) property, Math.min(0, i + AndroidUtilities.dp(40.0f)));
        LinearLayout linearLayout = this.photoPaintView.tabsLayout;
        Property property2 = View.ALPHA;
        animatorSet2.playTogether(objectAnimatorOfFloat, objectAnimatorOfFloat2, ObjectAnimator.ofFloat(linearLayout, (Property<LinearLayout, Float>) property2, iMax > AndroidUtilities.dp(20.0f) ? 0.0f : 1.0f), ObjectAnimator.ofFloat(this.photoPaintView.cancelButton, (Property<PaintCancelView, Float>) property2, iMax > AndroidUtilities.dp(20.0f) ? 0.0f : 1.0f), ObjectAnimator.ofFloat(this.photoPaintView.doneButton, (Property<PaintDoneView, Float>) property2, iMax <= AndroidUtilities.dp(20.0f) ? 1.0f : 0.0f), valueAnimatorOfFloat);
        animatorSet2.setDuration(320L);
        animatorSet2.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        animatorSet2.start();
        this.photoPaintView.updatePlusEmojiKeyboardButton();
    }

    public void lambda$setItemVisible$140(ValueAnimator valueAnimator) {
        updateActionBarTitlePadding();
    }

    public int $r8$lambda$icy9YvG3UzGsB3Mt_mSi_Yu1KHI(MessageObject messageObject, MessageObject messageObject2) {
        return messageObject.getFeedRealId() - messageObject2.getFeedRealId();
    }

    private boolean canSendMediaToParentChatActivity() {
        ChatActivity chatActivity = this.parentChatActivity;
        if (chatActivity == null) {
            return false;
        }
        if (chatActivity.currentUser != null) {
            return true;
        }
        TLRPC.Chat chat = chatActivity.currentChat;
        if (chat == null || ChatObject.isNotInChat(chat)) {
            return false;
        }
        return ChatObject.canSendPhoto(this.parentChatActivity.currentChat) || ChatObject.canSendVideo(this.parentChatActivity.currentChat);
    }

    private void setDoubleTapEnabled(boolean z) {
        this.doubleTapEnabled = z;
        GestureDetector2 gestureDetector2 = this.gestureDetector;
        if (!z) {
            this = null;
        }
        gestureDetector2.setOnDoubleTapListener(this);
    }

    public void setImages() {
        if (this.animationInProgress == 0) {
            setIndexToImage(this.centerImage, this.currentIndex, null);
            setIndexToPaintingOverlay(this.currentIndex, this.paintingOverlay);
            setIndexToImage(this.rightImage, this.currentIndex + 1, this.rightCropTransform);
            setIndexToPaintingOverlay(this.currentIndex + 1, this.rightPaintingOverlay);
            setIndexToImage(this.leftImage, this.currentIndex - 1, this.leftCropTransform);
            setIndexToPaintingOverlay(this.currentIndex - 1, this.leftPaintingOverlay);
        }
    }

    private void setIsAboutToSwitchToIndex(int i, boolean z, boolean z2) {
        setIsAboutToSwitchToIndex(i, z, z2, false);
    }

    void lambda$setIsAboutToSwitchToIndex$143(int i, TranslateController translateController, MessageObject messageObject, String str) {
        if (i != this.switchingToIndex) {
            return;
        }
        this.captionDetectedLanguage = str;
        if (translateController.isContextTranslateEnabled() && translateController.canTranslatePhoto(messageObject, this.captionDetectedLanguage)) {
            boolean z = this.captionTranslated;
            ActionBarMenuItem actionBarMenuItem = this.menuItem;
            if (z) {
                actionBarMenuItem.showSubItem(20);
                this.menuItem.hideSubItem(19);
                return;
            } else {
                actionBarMenuItem.showSubItem(19);
                this.menuItem.hideSubItem(20);
                return;
            }
        }
        this.menuItem.hideSubItem(19);
        this.menuItem.hideSubItem(20);
    }

    private void checkActionBarStyle() {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setTranslationX(this.sendPhotoTypeIsPollMediaEdit ? -AndroidUtilities.dp(4.0f) : 0.0f);
        }
        ActionBarMenuItem actionBarMenuItem = this.deleteItem;
        if (actionBarMenuItem != null) {
            setItemVisible(actionBarMenuItem, this.sendPhotoTypeIsPollMediaEdit, false);
        }
        boolean z = this.sendPhotoTypeIsPollMedia;
        if (z && this.actionBarBackButtonDrawableGlass == null) {
            this.actionBarBackButtonDrawableGlass = Blur3Utils.wrapCenteredDrawable(this.iBlur3FactoryFrostedLiquidGlass.create(this.actionBarBackButton).setColorProvider(BlurredBackgroundProviderImpl.photoViewer(this.resourcesProvider)).setRadius(AndroidUtilities.dp(20.0f)).setPadding(AndroidUtilities.dp(7.0f)), AndroidUtilities.dp(54.0f), AndroidUtilities.dp(54.0f));
        }
        ImageView imageView = this.actionBarBackButton;
        if (z) {
            ScaleStateListAnimator.apply(imageView);
            this.actionBarBackButton.setBackground(this.actionBarBackButtonDrawableGlass);
        } else {
            imageView.setStateListAnimator(null);
            this.actionBarBackButton.setBackground(this.actionBarBackButtonDrawableDeafult);
        }
    }

    public void updateCaptionTranslated() {
        int i;
        MessageObject messageObject;
        TLRPC.Message message;
        if (this.imagesArr.isEmpty() || (i = this.switchingToIndex) < 0 || i >= this.imagesArr.size() || (messageObject = this.imagesArr.get(this.switchingToIndex)) == null) {
            return;
        }
        if (this.captionTranslated && (message = messageObject.messageOwner) != null && message.translatedText != null && TextUtils.equals(message.translatedToLanguage, TranslateAlert2.getToLanguage())) {
            setCurrentCaption(messageObject, postProcessTranslated(messageObject), false, true);
        } else {
            setCurrentCaption(messageObject, messageObject.caption, this.captionTranslated, true);
        }
    }

    private CharSequence postProcessTranslated(MessageObject messageObject) {
        if (messageObject == null || messageObject.messageOwner == null) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        Spannable spannableReplaceAnimatedEmoji = MessageObject.replaceAnimatedEmoji(Emoji.replaceEmoji(new SpannableStringBuilder(messageObject.messageOwner.translatedText.text), Theme.chat_msgTextPaint.getFontMetricsInt(), false), messageObject.messageOwner.translatedText.entities, Theme.chat_msgTextPaint.getFontMetricsInt(), false);
        if (MessageObject.containsUrls(spannableReplaceAnimatedEmoji)) {
            try {
                AndroidUtilities.addLinksSafe(spannableReplaceAnimatedEmoji, 5, false, true);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        MessageObject.addUrlsByPattern(messageObject.isOutOwner(), spannableReplaceAnimatedEmoji, true, 0, 0, true);
        MessageObject.addEntitiesToText(spannableReplaceAnimatedEmoji, messageObject.messageOwner.translatedText.entities, messageObject.isOutOwner(), true, true, true);
        if (messageObject.isVideo()) {
            MessageObject.addUrlsByPattern(messageObject.isOutOwner(), spannableReplaceAnimatedEmoji, true, 3, (int) messageObject.getDuration(), false);
        } else if (messageObject.isMusic() || messageObject.isVoice()) {
            MessageObject.addUrlsByPattern(messageObject.isOutOwner(), spannableReplaceAnimatedEmoji, true, 4, (int) messageObject.getDuration(), false);
        }
        return spannableReplaceAnimatedEmoji;
    }

    public TLRPC.Document getOriginalSticker() {
        int i;
        if (this.translationX == 0.0f && this.translationY == 0.0f && (i = this.currentIndex) >= 0 && i < this.imagesArrLocals.size()) {
            Object obj = this.imagesArrLocals.get(this.currentIndex);
            if (!(obj instanceof MediaController.MediaEditState)) {
                return null;
            }
            MediaController.MediaEditState mediaEditState = (MediaController.MediaEditState) obj;
            if (!mediaEditState.isPainted && !mediaEditState.isCropped && !mediaEditState.isFiltered) {
                return this.originalSticker;
            }
        }
        return null;
    }

    public void enableStickerMode(TLRPC.Document document, TLRPC.Document document2, boolean z, Utilities.Callback2<String, TLRPC.InputDocument> callback2) {
        this.originalSticker = document;
        this.replacedSticker = document2;
        this.stickerEmpty = z;
        this.stickerEmptySent = false;
        this.customStickerHandler = callback2;
        this.rotate = 0.0f;
        this.animateToRotate = 0.0f;
        if (this.stickerMakerView != null) {
            BlurButton blurButton = this.outlineBtn;
            if (blurButton != null) {
                blurButton.setActive(false, false);
            }
            this.stickerMakerView.clean();
            this.stickerMakerView.setStickerCornerRoundness(ExteraConfig.getPreferences().getInt("stickerCornerRoundness", 0));
            updateStickerRoundItemVisibility();
            ArrayList<String> arrayList = this.selectedEmojis;
            if (arrayList != null) {
                arrayList.clear();
            }
        }
        if (this.originalSticker != null) {
            ArrayList<String> arrayList2 = this.selectedEmojis;
            if (arrayList2 == null) {
                this.selectedEmojis = new ArrayList<>();
            } else {
                arrayList2.clear();
            }
            ArrayList<String> arrayListFindStickerEmoticons = MessageObject.findStickerEmoticons(this.originalSticker, Integer.valueOf(this.currentAccount));
            if (arrayListFindStickerEmoticons != null) {
                this.selectedEmojis.addAll(arrayListFindStickerEmoticons);
            }
        }
        BlurButton blurButton2 = this.cutOutBtn;
        if (blurButton2 != null) {
            blurButton2.clean();
        }
        showStickerMode(true, false);
        ImageView imageView = this.tuneItem;
        if (imageView != null) {
            imageView.setAlpha(this.stickerEmpty ? 0.4f : 1.0f);
        }
    }

    public void prepareSegmentImage() {
        StickerMakerView stickerMakerView = this.stickerMakerView;
        if (stickerMakerView == null || this.sendPhotoType != 11) {
            return;
        }
        if (this.stickerEmpty) {
            stickerMakerView.clean();
        } else {
            stickerMakerView.segmentImage(this.centerImage.getBitmap(), this.centerImage.getOrientation(), getContainerViewWidth(), getContainerViewHeight(), new Utilities.Callback() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda144
                @Override 
                public final void run(Object obj) {
                    this.f$0.lambda$prepareSegmentImage$144((StickerMakerView.SegmentedObject) obj);
                }
            });
        }
    }

    public void lambda$setImageIndex$152() {
        if (this.ads.isPopupShown()) {
            VideoAds videoAds = this.ads;
            VideoPlayer videoPlayer = this.videoPlayer;
            videoAds.videoWasPlaying = videoPlayer == null ? true : videoPlayer.isPlaying();
            VideoPlayer videoPlayer2 = this.videoPlayer;
            if (videoPlayer2 != null) {
                videoPlayer2.pause();
                return;
            }
            return;
        }
        VideoPlayer videoPlayer3 = this.videoPlayer;
        if (videoPlayer3 == null || !this.ads.videoWasPlaying) {
            return;
        }
        videoPlayer3.play();
    }

    private void resetIndexForDeferredImageLoading() {
        Object mark = this.centerImage.getMark();
        if (mark == null || !mark.equals(1)) {
            return;
        }
        setIndexToImage(this.centerImage, this.currentIndex, null);
    }

    private void setCurrentCaption(MessageObject messageObject, CharSequence charSequence, boolean z, boolean z2) {
        int i;
        boolean z3;
        TLRPC.Message message;
        CharSequence charSequenceCloneSpans = AnimatedEmojiSpan.cloneSpans(charSequence, 3);
        showEditCaption(this.editing, z2);
        if (!this.editing) {
            if (this.sendPhotoType != 1) {
                this.captionEdit.setVisibility(8);
                this.topCaptionEdit.setVisibility(8);
                if (this.needCaptionLayout) {
                    if (this.captionTextViewSwitcher.getParent() != this.pickerView) {
                        FrameLayout frameLayout = this.captionContainer;
                        if (frameLayout != null) {
                            frameLayout.removeView(this.captionTextViewSwitcher);
                        }
                        this.captionTextViewSwitcher.setMeasureAllChildren(false);
                        this.pickerView.addView(this.captionTextViewSwitcher, LayoutHelper.createFrame(-1, -2.0f, 83, 0.0f, 0.0f, 76.0f, 48.0f));
                    }
                } else {
                    if (this.captionScrollView == null) {
                        FrameLayout frameLayout2 = new FrameLayout(this.containerView.getContext());
                        this.captionContainer = frameLayout2;
                        this.captionTextViewSwitcher.setContainer(frameLayout2);
                        CaptionScrollView captionScrollView = new CaptionScrollView(this.containerView.getContext(), this.captionTextViewSwitcher, this.captionContainer) { // from class: org.telegram.ui.PhotoViewer.76
                            @Override // org.telegram.ui.PhotoViewer.CaptionScrollView
                            public boolean isStatusBarVisible() {
                                return !PhotoViewer.this.inBubbleMode;
                            }

                            @Override // android.view.View
                            public void invalidate() {
                                super.invalidate();
                                if (PhotoViewer.this.isActionBarVisible) {
                                    int scrollY = getScrollY();
                                    float translationY = PhotoViewer.this.captionTextViewSwitcher.getTranslationY();
                                    boolean z4 = scrollY == 0 && translationY == 0.0f;
                                    boolean z5 = scrollY == 0 && translationY == 0.0f;
                                    if (!z4) {
                                        int y = PhotoViewer.this.photoProgressViews[0].getY() + PhotoViewer.this.photoProgressViews[0].size;
                                        int top = (((PhotoViewer.this.captionContainer.getTop() + ((int) translationY)) - scrollY) + ((isStatusBarVisible() ? AndroidUtilities.statusBarHeight : 0) + ActionBar.getCurrentActionBarHeight())) - AndroidUtilities.dp(12.0f);
                                        z5 = top > ((int) PhotoViewer.this.fullscreenButton[0].getY()) + AndroidUtilities.dp(32.0f);
                                        z4 = top > y;
                                    }
                                    if (PhotoViewer.this.allowShowFullscreenButton) {
                                        if (PhotoViewer.this.fullscreenButton[0].getTag() != null && ((Integer) PhotoViewer.this.fullscreenButton[0].getTag()).intValue() == 3 && z5) {
                                            PhotoViewer.this.fullscreenButton[0].setTag(2);
                                            PhotoViewer.this.fullscreenButton[0].animate().alpha(1.0f).setDuration(150L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.PhotoViewer.76.1
                                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                                public void onAnimationEnd(Animator animator) {
                                                    PhotoViewer.this.fullscreenButton[0].setTag(null);
                                                }
                                            }).start();
                                        } else if (PhotoViewer.this.fullscreenButton[0].getTag() == null && !z5) {
                                            PhotoViewer.this.fullscreenButton[0].setTag(3);
                                            PhotoViewer.this.fullscreenButton[0].animate().alpha(0.0f).setListener(null).setDuration(150L).start();
                                        }
                                    }
                                    PhotoViewer.this.photoProgressViews[0].setIndexedAlpha(2, z4 ? 1.0f : 0.0f, true);
                                }
                            }
                        };
                        this.captionScrollView = captionScrollView;
                        this.captionTextViewSwitcher.setScrollView(captionScrollView);
                        this.captionContainer.setClipChildren(false);
                        this.captionScrollView.addView(this.captionContainer, new ViewGroup.LayoutParams(-1, -2));
                        this.containerView.addView(this.captionScrollView, LayoutHelper.createFrame(-1, -1, 80));
                    }
                    if (this.captionTextViewSwitcher.getParent() != this.captionContainer) {
                        this.pickerView.removeView(this.captionTextViewSwitcher);
                        this.captionTextViewSwitcher.setMeasureAllChildren(true);
                        this.captionContainer.addView(this.captionTextViewSwitcher, -1, -2);
                        this.videoPreviewFrame.bringToFront();
                    }
                    if (messageObject != null && messageObject.isSponsored()) {
                        createAdButtonView();
                        AndroidUtilities.removeFromParent(this.adButtonView);
                        this.adButtonTextView.setText(messageObject.sponsoredButtonText);
                        this.captionContainer.addView(this.adButtonView, LayoutHelper.createFrame(-1, 44.0f, 87, 16.0f, 0.0f, 16.0f, 12.0f));
                        this.captionTextViewSwitcher.setPadding(0, 0, 0, AndroidUtilities.dp(64.0f));
                        this.adButtonView.bringToFront();
                    } else {
                        FrameLayout frameLayout3 = this.adButtonView;
                        if (frameLayout3 != null) {
                            AndroidUtilities.removeFromParent(frameLayout3);
                            this.captionTextViewSwitcher.setPadding(0, 0, 0, 0);
                        }
                    }
                }
                boolean zIsEmpty = TextUtils.isEmpty(charSequenceCloneSpans);
                boolean zIsEmpty2 = TextUtils.isEmpty(this.captionTextViewSwitcher.getCurrentView().getText());
                CaptionTextViewSwitcher captionTextViewSwitcher = this.captionTextViewSwitcher;
                TextView nextView = z2 ? captionTextViewSwitcher.getNextView() : captionTextViewSwitcher.getCurrentView();
                if (this.isCurrentVideo) {
                    if (nextView.getMaxLines() != 1) {
                        this.captionTextViewSwitcher.getCurrentView().setMaxLines(1);
                        this.captionTextViewSwitcher.getNextView().setMaxLines(1);
                        this.captionTextViewSwitcher.getCurrentView().setSingleLine(true);
                        this.captionTextViewSwitcher.getNextView().setSingleLine(true);
                        TextView currentView = this.captionTextViewSwitcher.getCurrentView();
                        TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
                        currentView.setEllipsize(truncateAt);
                        this.captionTextViewSwitcher.getNextView().setEllipsize(truncateAt);
                    }
                } else {
                    int maxLines = nextView.getMaxLines();
                    if (maxLines == 1) {
                        this.captionTextViewSwitcher.getCurrentView().setSingleLine(false);
                        this.captionTextViewSwitcher.getNextView().setSingleLine(false);
                    }
                    if (this.needCaptionLayout) {
                        Point point = AndroidUtilities.displaySize;
                        i = point.x > point.y ? 5 : 10;
                    } else {
                        i = Integer.MAX_VALUE;
                    }
                    if (maxLines != i) {
                        this.captionTextViewSwitcher.getCurrentView().setMaxLines(i);
                        this.captionTextViewSwitcher.getNextView().setMaxLines(i);
                        this.captionTextViewSwitcher.getCurrentView().setEllipsize(null);
                        this.captionTextViewSwitcher.getNextView().setEllipsize(null);
                    }
                }
                nextView.setScrollX(0);
                boolean z4 = this.needCaptionLayout;
                this.dontChangeCaptionPosition = !z4 && z2 && zIsEmpty;
                if (!z4) {
                    this.captionScrollView.dontChangeTopMargin = false;
                }
                if (z2) {
                    TransitionManager.endTransitions(z4 ? this.pickerView : this.captionScrollView);
                    if (this.needCaptionLayout) {
                        TransitionSet transitionSet = new TransitionSet();
                        transitionSet.setOrdering(0);
                        transitionSet.addTransition(new ChangeBounds());
                        transitionSet.addTransition(new Fade(2));
                        transitionSet.addTransition(new Fade(1));
                        transitionSet.setDuration(200L);
                        TransitionManager.beginDelayedTransition(this.pickerView, transitionSet);
                    } else {
                        TransitionSet duration = new TransitionSet().addTransition(new AnonymousClass78(2, zIsEmpty2, zIsEmpty)).addTransition(new AnonymousClass77(1, zIsEmpty2, zIsEmpty)).setDuration(200L);
                        if (!zIsEmpty2) {
                            this.captionScrollView.dontChangeTopMargin = true;
                            duration.addTransition(new AnonymousClass79());
                        }
                        if (zIsEmpty2 && !zIsEmpty) {
                            duration.addTarget((View) this.captionTextViewSwitcher);
                        }
                        TransitionManager.beginDelayedTransition(this.captionScrollView, duration);
                    }
                    z3 = true;
                } else {
                    this.captionTextViewSwitcher.getCurrentView().setText((CharSequence) null);
                    CaptionScrollView captionScrollView2 = this.captionScrollView;
                    if (captionScrollView2 != null) {
                        captionScrollView2.scrollTo(0, 0);
                    }
                    z3 = false;
                }
                if (!zIsEmpty) {
                    Theme.createChatResources(null, true);
                    if (messageObject == null || !this.captionTranslated || (message = messageObject.messageOwner) == null || message.translatedText == null || !TextUtils.equals(message.translatedToLanguage, TranslateAlert2.getToLanguage())) {
                        if (messageObject != null && !messageObject.messageOwner.entities.isEmpty()) {
                            SpannableString spannableString = new SpannableString(charSequenceCloneSpans);
                            messageObject.addEntitiesToText(spannableString, true, false);
                            if (messageObject.isVideo()) {
                                MessageObject.addUrlsByPattern(messageObject.isOutOwner(), spannableString, false, 3, (int) messageObject.getDuration(), false);
                            }
                            charSequenceCloneSpans = Emoji.replaceEmoji(spannableString, nextView.getPaint().getFontMetricsInt(), false);
                        } else {
                            charSequenceCloneSpans = Emoji.replaceEmoji(new SpannableStringBuilder(charSequenceCloneSpans), nextView.getPaint().getFontMetricsInt(), false);
                        }
                    }
                    if (messageObject != null && messageObject.isSponsored()) {
                        charSequenceCloneSpans = sponsoredCaption(messageObject, charSequenceCloneSpans);
                    }
                    this.captionTextViewSwitcher.setTag(charSequenceCloneSpans);
                    try {
                        this.captionTextViewSwitcher.setText(charSequenceCloneSpans, z2, this.lastCaptionTranslating != z);
                        CaptionScrollView captionScrollView3 = this.captionScrollView;
                        if (captionScrollView3 != null) {
                            captionScrollView3.updateTopMargin();
                        }
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    nextView.setScrollY(0);
                    nextView.setTextColor(-1);
                    this.captionTextViewSwitcher.setVisibility(this.isActionBarVisible && (!this.isCurrentVideo || this.pickerView.getVisibility() == 0 || this.pageBlocksAdapter != null) ? 0 : 4);
                } else {
                    boolean z5 = this.needCaptionLayout;
                    CaptionTextViewSwitcher captionTextViewSwitcher2 = this.captionTextViewSwitcher;
                    if (z5) {
                        captionTextViewSwitcher2.setText(LocaleController.getString("AddCaption", R.string.AddCaption), z2);
                        this.captionTextViewSwitcher.getCurrentView().setTextColor(-1291845633);
                        this.captionTextViewSwitcher.setTag("empty");
                        this.captionTextViewSwitcher.setVisibility(0);
                    } else {
                        captionTextViewSwitcher2.setText(null, z2);
                        this.captionTextViewSwitcher.getCurrentView().setTextColor(-1);
                        this.captionTextViewSwitcher.setVisibility(4, !z3 || zIsEmpty2);
                        this.captionTextViewSwitcher.setTag(null);
                    }
                }
                if (this.captionTextViewSwitcher.getCurrentView() instanceof CaptionTextView) {
                    ((CaptionTextView) this.captionTextViewSwitcher.getCurrentView()).setLoading(z);
                }
                this.lastCaptionTranslating = !zIsEmpty && z;
                return;
            }
        }
        getCaptionView().setText(charSequenceCloneSpans);
        this.captionTextViewSwitcher.setVisibility(8);
    }

    public class AnonymousClass78 extends Fade {
        final public void checkProgress(final int i, boolean z, final boolean z2) {
        int i2;
        boolean zShouldIndexAutoPlayed;
        final boolean z3;
        final boolean z4;
        ?? r2;
        final File file;
        File file2;
        File pathToAttach;
        File pathToAttach2;
        boolean z5;
        FileLoader.FileResolver fileResolver;
        AnimatedFileDrawable animatedFileDrawable;
        int i3 = this.currentIndex;
        if (i == 1) {
            i2 = i3 + 1;
        } else {
            i2 = i == 2 ? i3 - 1 : i3;
        }
        if (this.currentFileNames[i] != null) {
            boolean zHasBitmap = (i == 0 && i3 == 0 && (animatedFileDrawable = this.currentAnimation) != null) ? animatedFileDrawable.hasBitmap() : false;
            File file3 = null;
            final FileLoader.FileResolver fileResolver2 = null;
            if (this.currentMessageObject != null) {
                if (i2 < 0 || i2 >= this.imagesArr.size()) {
                    this.photoProgressViews[i].setBackgroundState(-1, z2, true);
                    return;
                }
                MessageObject messageObject = this.imagesArr.get(i2);
                zShouldIndexAutoPlayed = shouldMessageObjectAutoPlayed(messageObject);
                if (this.sharedMediaType == 1 && !messageObject.canPreviewDocument()) {
                    this.photoProgressViews[i].setBackgroundState(-1, z2, true);
                    return;
                }
                File file4 = !TextUtils.isEmpty(messageObject.messageOwner.attachPath) ? new File(messageObject.messageOwner.attachPath) : null;
                if ((MessageObject.getMedia(messageObject.messageOwner) instanceof TLRPC.TL_messageMediaWebPage) && MessageObject.getMedia(messageObject.messageOwner).webpage != null && MessageObject.getMedia(messageObject.messageOwner).webpage.document == null) {
                    final TLObject fileLocation = getFileLocation(i2, null);
                    fileResolver = new FileLoader.FileResolver() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda69
                        @Override 
                        public final File getFile() {
                            return this.f$0.lambda$checkProgress$153(fileLocation);
                        }
                    };
                } else {
                    final TLRPC.Message message = messageObject.messageOwner;
                    fileResolver = new FileLoader.FileResolver() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda70
                        @Override 
                        public final File getFile() {
                            return this.f$0.lambda$checkProgress$154(message);
                        }
                    };
                }
                if (messageObject.isVideo()) {
                    FileLoader.FileResolver fileResolver3 = fileResolver;
                    z3 = (SharedConfig.streamMedia && messageObject.canStreamVideo() && !DialogObject.isEncryptedDialog(messageObject.getDialogId())) || messageObject.hasVideoQualities();
                    r2 = messageObject;
                    file = null;
                    file3 = file4;
                    fileResolver2 = fileResolver3;
                    z4 = true;
                } else {
                    z4 = false;
                    r2 = messageObject;
                    file = null;
                    file3 = file4;
                    fileResolver2 = fileResolver;
                    z3 = false;
                }
            } else {
                if (this.currentBotInlineResult != null) {
                    if (i2 < 0 || i2 >= this.imagesArrLocals.size()) {
                        this.photoProgressViews[i].setBackgroundState(-1, z2, true);
                        return;
                    }
                    TLRPC.BotInlineResult botInlineResult = (TLRPC.BotInlineResult) this.imagesArrLocals.get(i2);
                    if (botInlineResult.type.equals(MediaStreamTrack.VIDEO_TRACK_KIND) || MessageObject.isVideoDocument(botInlineResult.document)) {
                        if (botInlineResult.document != null) {
                            file2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(botInlineResult.document);
                        } else if (botInlineResult.content instanceof TLRPC.TL_webDocument) {
                            file2 = new File(FileLoader.getDirectory(4), Utilities.MD5(botInlineResult.content.url) + "." + ImageLoader.getHttpUrlExtension(botInlineResult.content.url, "mp4"));
                        } else {
                            file2 = null;
                        }
                        z5 = true;
                    } else {
                        if (botInlineResult.document != null) {
                            file2 = new File(FileLoader.getDirectory(3), this.currentFileNames[i]);
                        } else if (botInlineResult.photo != null) {
                            file2 = new File(FileLoader.getDirectory(0), this.currentFileNames[i]);
                        } else {
                            z5 = false;
                            file2 = null;
                        }
                        z5 = false;
                    }
                    zShouldIndexAutoPlayed = false;
                    z3 = false;
                    z4 = z5;
                    file = new File(FileLoader.getDirectory(4), this.currentFileNames[i]);
                } else {
                    if (this.currentFileLocation != null) {
                        if (i2 < 0 || i2 >= this.imagesArrLocationsVideo.size()) {
                            this.photoProgressViews[i].setBackgroundState(-1, z2, true);
                            return;
                        }
                        ImageLocation imageLocation = this.imagesArrLocationsVideo.get(i2);
                        if (imageLocation != null) {
                            pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(imageLocation.location, getFileLocationExt(imageLocation), false);
                            pathToAttach2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(imageLocation.location, getFileLocationExt(imageLocation), true);
                        } else {
                            pathToAttach2 = null;
                            pathToAttach = null;
                        }
                    } else if (this.currentSecureDocument != null) {
                        if (i2 < 0 || i2 >= this.secureDocuments.size()) {
                            this.photoProgressViews[i].setBackgroundState(-1, z2, true);
                            return;
                        } else {
                            SecureDocument secureDocument = this.secureDocuments.get(i2);
                            pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(secureDocument, true);
                            pathToAttach2 = FileLoader.getInstance(this.currentAccount).getPathToAttach(secureDocument, false);
                        }
                    } else if (this.currentPathObject != null) {
                        file2 = new File(FileLoader.getDirectory(3), this.currentFileNames[i]);
                        file = new File(FileLoader.getDirectory(4), this.currentFileNames[i]);
                        zShouldIndexAutoPlayed = false;
                        z3 = false;
                        z4 = false;
                    } else {
                        PageBlocksAdapter pageBlocksAdapter = this.pageBlocksAdapter;
                        if (pageBlocksAdapter != null) {
                            File file5 = pageBlocksAdapter.getFile(i2);
                            boolean z6 = this.pageBlocksAdapter.isVideo(i2) || this.pageBlocksAdapter.isHardwarePlayer(i2);
                            z4 = z6;
                            z3 = z6 && SharedConfig.streamMedia && (this.pageBlocksAdapter.getMedia(i2) instanceof TLRPC.Document);
                            fileResolver2 = null;
                            zShouldIndexAutoPlayed = shouldIndexAutoPlayed(i2);
                            file3 = file5;
                            r2 = 0;
                            file = null;
                        } else {
                            zShouldIndexAutoPlayed = false;
                            z3 = false;
                            z4 = false;
                            r2 = 0;
                            file = null;
                            fileResolver2 = null;
                        }
                    }
                    zShouldIndexAutoPlayed = false;
                    z3 = false;
                    z4 = false;
                    file3 = pathToAttach;
                    file = pathToAttach2;
                    r2 = file3;
                }
                file3 = file2;
                r2 = file3;
            }
            final boolean z7 = !(i == 0 && this.dontAutoPlay) && zShouldIndexAutoPlayed;
            final File file6 = file3;
            final ?? r7 = r2;
            final boolean z8 = zHasBitmap;
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda71
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$checkProgress$156(z8, file6, file, fileResolver2, i, r7, z3, z4, z7, z2);
                }
            });
            return;
        }
        if (!this.imagesArrLocals.isEmpty() && i2 >= 0 && i2 < this.imagesArrLocals.size()) {
            Object obj = this.imagesArrLocals.get(i2);
            if (obj instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) obj;
                if (photoEntry.isVideo && (!photoEntry.isLivePhoto() || (!photoEntry.isUnalivePhoto() && this.sendPhotoType != 11))) {
                    this.photoProgressViews[i].setBackgroundState(3, z2, true);
                    return;
                }
            }
        }
        this.photoProgressViews[i].setBackgroundState(-1, z2, true);
    }

    public void lambda$checkProgress$156(boolean z, final File file, File file2, FileLoader.FileResolver fileResolver, final int i, MessageObject messageObject, final boolean z2, final boolean z3, final boolean z4, final boolean z5) {
        final File file3;
        ChatActivity chatActivity;
        TLRPC.Document document;
        boolean zExists = (z || file == null) ? z : file.exists();
        File file4 = null;
        if (file2 == null && fileResolver != null) {
            file3 = fileResolver.getFile();
        } else {
            file4 = fileResolver != null ? fileResolver.getFile() : null;
            file3 = file2;
        }
        if (!zExists && file3 != null) {
            zExists = file3.exists();
        }
        if (!zExists && file4 != null) {
            zExists = file4.exists();
        }
        final boolean z6 = zExists;
        if (!z6 && i != 0 && messageObject != null && z2 && DownloadController.getInstance(this.currentAccount).canDownloadMedia(messageObject.messageOwner) != 0 && (((chatActivity = this.parentChatActivity) == null || chatActivity.getCurrentEncryptedChat() == null) && !messageObject.shouldEncryptPhotoOrVideo() && (document = messageObject.getDocument()) != null)) {
            FileLoader.getInstance(this.currentAccount).loadFile(document, messageObject, 0, 10);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda151
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$checkProgress$155(i, file, file3, z6, z2, z3, z4, z5);
            }
        });
    }

    public public static boolean isShowingImage(MessageObject messageObject) {
        boolean z;
        if (Instance == null || Instance.pipAnimationInProgress || !Instance.isVisible || Instance.disableShowCheck || messageObject == null) {
            z = false;
        } else {
            MessageObject editingMessageObject = Instance.currentMessageObject;
            if (editingMessageObject == null && Instance.placeProvider != null) {
                editingMessageObject = Instance.placeProvider.getEditingMessageObject();
            }
            if (editingMessageObject != null && editingMessageObject.getFeedRealId() == messageObject.getFeedRealId() && editingMessageObject.getDialogId() == messageObject.getDialogId()) {
                z = true;
            } else {
                z = false;
            }
        }
        if (z || PipInstance == null) {
            return z;
        }
        return PipInstance.isVisible && !PipInstance.disableShowCheck && messageObject != null && PipInstance.currentMessageObject != null && PipInstance.currentMessageObject.getFeedRealId() == messageObject.getFeedRealId() && PipInstance.currentMessageObject.getDialogId() == messageObject.getDialogId();
    }

    public static boolean isPlayingMessageInPip(MessageObject messageObject) {
        return (PipInstance == null || messageObject == null || PipInstance.currentMessageObject == null || PipInstance.currentMessageObject.getFeedRealId() != messageObject.getFeedRealId() || PipInstance.currentMessageObject.getDialogId() != messageObject.getDialogId()) ? false : true;
    }

    public static boolean isPlayingMessage(MessageObject messageObject) {
        return (Instance == null || Instance.pipAnimationInProgress || !Instance.isVisible || messageObject == null || Instance.currentMessageObject == null || Instance.currentMessageObject.getFeedRealId() != messageObject.getFeedRealId() || Instance.currentMessageObject.getDialogId() != messageObject.getDialogId()) ? false : true;
    }

    public static boolean isShowingImage(TLRPC.FileLocation fileLocation) {
        if (Instance != null && Instance.isVisible && !Instance.disableShowCheck && fileLocation != null) {
            if (Instance.currentFileLocation != null && fileLocation.local_id == Instance.currentFileLocation.location.local_id && fileLocation.volume_id == Instance.currentFileLocation.location.volume_id && fileLocation.dc_id == Instance.currentFileLocation.dc_id) {
                return true;
            }
            if (Instance.currentFileLocationVideo != null && fileLocation.local_id == Instance.currentFileLocationVideo.location.local_id && fileLocation.volume_id == Instance.currentFileLocationVideo.location.volume_id && fileLocation.dc_id == Instance.currentFileLocationVideo.dc_id) {
                return true;
            }
        }
        return false;
    }

    public static boolean isShowingImage(TLRPC.BotInlineResult botInlineResult) {
        return (Instance == null || !Instance.isVisible || Instance.disableShowCheck || botInlineResult == null || Instance.currentBotInlineResult == null || botInlineResult.id != Instance.currentBotInlineResult.id) ? false : true;
    }

    public static boolean isShowingImage(String str) {
        return (Instance == null || !Instance.isVisible || Instance.disableShowCheck || str == null || !str.equals(Instance.currentPathObject)) ? false : true;
    }

    public void setParentChatActivity(ChatActivity chatActivity) {
        this.parentChatActivity = chatActivity;
    }

    public void setMaxSelectedPhotos(int i, boolean z) {
        this.maxSelectedPhotos = i;
        this.allowOrder = z;
    }

    public void checkCurrentImageVisibility() {
        PlaceProviderObject placeProviderObject = this.currentPlaceObject;
        if (placeProviderObject != null) {
            placeProviderObject.imageReceiver.setVisible(true, true);
        }
        PhotoViewerProvider photoViewerProvider = this.placeProvider;
        PlaceProviderObject placeForPhoto = photoViewerProvider == null ? null : photoViewerProvider.getPlaceForPhoto(this.currentMessageObject, getFileLocation(this.currentFileLocation), this.currentIndex, false, false);
        this.currentPlaceObject = placeForPhoto;
        if (placeForPhoto == null || placeForPhoto.keepImageReceiverVisible) {
            return;
        }
        placeForPhoto.imageReceiver.setVisible(false, true);
    }

    public boolean openPhoto(MessageObject messageObject, ChatActivity chatActivity, long j, long j2, long j3, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(messageObject, null, null, null, null, null, null, 0, photoViewerProvider, chatActivity, j, j2, j3, true, null, null);
    }

    public boolean openPhoto(MessageObject messageObject, int i, ChatActivity chatActivity, long j, long j2, long j3, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(messageObject, null, null, null, null, null, null, 0, photoViewerProvider, chatActivity, j, j2, j3, true, null, Integer.valueOf(i));
    }

    public boolean openPhoto(MessageObject messageObject, long j, long j2, long j3, PhotoViewerProvider photoViewerProvider, boolean z) {
        return openPhoto(messageObject, null, null, null, null, null, null, 0, photoViewerProvider, null, j, j2, j3, z, null, null);
    }

    public boolean openPhoto(TLRPC.FileLocation fileLocation, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(null, fileLocation, null, null, null, null, null, 0, photoViewerProvider, null, 0L, 0L, 0L, true, null, null);
    }

    public boolean openPhotoWithVideo(TLRPC.FileLocation fileLocation, ImageLocation imageLocation, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(null, fileLocation, null, imageLocation, null, null, null, 0, photoViewerProvider, null, 0L, 0L, 0L, true, null, null);
    }

    public boolean openPhotoWithVideo(TLRPC.FileLocation fileLocation, ImageLocation imageLocation, ImageLocation imageLocation2, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(null, fileLocation, imageLocation, imageLocation2, null, null, null, 0, photoViewerProvider, null, 0L, 0L, 0L, true, null, null);
    }

    public boolean openPhoto(TLRPC.FileLocation fileLocation, ImageLocation imageLocation, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(null, fileLocation, imageLocation, null, null, null, null, 0, photoViewerProvider, null, 0L, 0L, 0L, true, null, null);
    }

    public boolean openPhoto(ArrayList<MessageObject> arrayList, int i, long j, long j2, long j3, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(arrayList.get(i), null, null, null, arrayList, null, null, i, photoViewerProvider, null, j, j2, j3, true, null, null);
    }

    public boolean openPhoto(ArrayList<SecureDocument> arrayList, int i, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(null, null, null, null, null, arrayList, null, i, photoViewerProvider, null, 0L, 0L, 0L, true, null, null);
    }

    public boolean openPhoto(int i, PageBlocksAdapter pageBlocksAdapter, PhotoViewerProvider photoViewerProvider) {
        return openPhoto(null, null, null, null, null, null, null, i, photoViewerProvider, null, 0L, 0L, 0L, true, pageBlocksAdapter, null);
    }

    private static String attrsToString(TLRPC.Document document) {
        if (document == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < document.attributes.size(); i++) {
            TLRPC.DocumentAttribute documentAttribute = document.attributes.get(i);
            if (sb.length() > 0) {
                sb.append(",");
            }
            sb.append(documentAttribute.getClass().getSimpleName());
            if (documentAttribute instanceof TLRPC.TL_documentAttributeVideo) {
                sb.append("(w=");
                sb.append(documentAttribute.w);
                sb.append(",h=");
                sb.append(documentAttribute.h);
                sb.append(",dur=");
                sb.append(documentAttribute.duration);
                sb.append(")");
            }
        }
        return sb.toString();
    }

    public boolean openPhotoForSelect(ArrayList<Object> arrayList, int i, int i2, boolean z, PhotoViewerProvider photoViewerProvider, ChatActivity chatActivity) {
        return openPhotoForSelect(null, null, arrayList, i, i2, z, photoViewerProvider, chatActivity);
    }

    public boolean openPhotoForSelect(TLRPC.FileLocation fileLocation, ImageLocation imageLocation, ArrayList<Object> arrayList, int i, int i2, boolean z, PhotoViewerProvider photoViewerProvider, ChatActivity chatActivity) {
        AnimatedTextView animatedTextView;
        this.isDocumentsPicker = z;
        ChatActivityEnterView.SendButton sendButton = this.pickerViewSendButton;
        if (sendButton != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) sendButton.getLayoutParams();
            if (i2 == 4 || i2 == 5) {
                this.pickerViewSendButton.setResourceId(R.drawable.send_extera_24);
                layoutParams.bottomMargin = AndroidUtilities.dp(1.0f);
            } else if (i2 == 13 || i2 == 14 || i2 == 1 || i2 == 3 || i2 == 10 || i2 == 11) {
                this.pickerViewSendButton.setResourceId(R.drawable.floating_check);
                this.pickerViewSendButton.setPadding(0, AndroidUtilities.dp(1.0f), 0, 0);
                layoutParams.bottomMargin = AndroidUtilities.dp(1.0f);
            } else {
                this.pickerViewSendButton.setResourceId(R.drawable.send_extera_24);
                layoutParams.bottomMargin = AndroidUtilities.dp(1.0f);
            }
            this.pickerViewSendButton.setLayoutParams(layoutParams);
        }
        if (i2 != 11 && this.stickerMakerView != null) {
            this.stickerEmpty = false;
            ImageView imageView = this.tuneItem;
            if (imageView != null) {
                imageView.setAlpha(1.0f);
            }
            BlurButton blurButton = this.outlineBtn;
            if (blurButton != null) {
                blurButton.setActive(false, false);
            }
            this.stickerMakerView.clean();
            ArrayList<String> arrayList2 = this.selectedEmojis;
            if (arrayList2 != null) {
                arrayList2.clear();
            }
        }
        boolean z2 = this.isVisible;
        if (z2 && this.sendPhotoType != i2 && i2 == 1) {
            this.sendPhotoType = i2;
            this.doneButtonPressed = false;
            this.actionBarContainer.setTitle(_UrlKt.FRAGMENT_ENCODE_SET);
            this.actionBarContainer.setSubtitle(_UrlKt.FRAGMENT_ENCODE_SET, false);
            this.placeProvider = photoViewerProvider;
            this.mergeDialogId = 0L;
            this.currentDialogId = 0L;
            this.selectedPhotosAdapter.notifyDataSetChanged();
            this.pageBlocksAdapter = null;
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.isVisible = true;
            this.isVisibleOrAnimating = true;
            togglePhotosListView(false, false);
            this.openedFullScreenVideo = false;
            createCropView();
            toggleActionBar(false, false);
            this.seekToProgressPending2 = 0.0f;
            this.skipFirstBufferingProgress = false;
            this.playerInjected = false;
            makeFocusable();
            this.backgroundDrawable.setAlpha(255);
            this.containerView.setAlpha(1.0f);
            onPhotoShow(null, fileLocation, imageLocation, null, null, null, arrayList, i, null);
            initCropView();
            setCropBitmap();
            return true;
        }
        if (z2 && this.sendPhotoType != i2 && i2 == 11) {
            this.sendPhotoType = i2;
            this.doneButtonPressed = false;
            this.actionBarContainer.setTitle(_UrlKt.FRAGMENT_ENCODE_SET);
            this.actionBarContainer.setSubtitle(_UrlKt.FRAGMENT_ENCODE_SET, false);
            this.placeProvider = photoViewerProvider;
            this.mergeDialogId = 0L;
            this.currentDialogId = 0L;
            this.selectedPhotosAdapter.notifyDataSetChanged();
            this.pageBlocksAdapter = null;
            if (this.velocityTracker == null) {
                this.velocityTracker = VelocityTracker.obtain();
            }
            this.isVisible = true;
            this.isVisibleOrAnimating = true;
            togglePhotosListView(false, false);
            this.seekToProgressPending2 = 0.0f;
            this.skipFirstBufferingProgress = false;
            this.playerInjected = false;
            makeFocusable();
            this.backgroundDrawable.setAlpha(255);
            this.containerView.setAlpha(1.0f);
            onPhotoShow(null, fileLocation, imageLocation, null, null, null, arrayList, i, null);
            return true;
        }
        this.sendPhotoType = i2;
        if (i2 == 12) {
            this.sendPhotoType = 0;
            this.sendPhotoTypeIsGif = true;
        }
        int i3 = this.sendPhotoType;
        boolean z3 = i3 == 14;
        this.sendPhotoTypeIsPollMediaEdit = z3;
        if (i3 == 13 || z3) {
            this.sendPhotoType = 0;
            this.sendPhotoTypeIsPollMedia = true;
        }
        this.animatorPollAttachButtonsVisibility.setValue(z3, false);
        if (this.sendPhotoType == 11) {
            this.navigationBar.setBackgroundColor(-16777216);
        }
        PhotoViewerActionBarContainer photoViewerActionBarContainer = this.actionBarContainer;
        if (photoViewerActionBarContainer != null && (animatedTextView = photoViewerActionBarContainer.subtitleTextView) != null) {
            animatedTextView.setVisibility(this.sendPhotoTypeIsPollMedia ? 8 : 0);
        }
        return openPhoto(null, fileLocation, imageLocation, null, null, null, arrayList, i, photoViewerProvider, chatActivity, 0L, 0L, 0L, true, null, null);
    }

    public void setTitle(CharSequence charSequence) {
        PhotoViewerActionBarContainer photoViewerActionBarContainer = this.actionBarContainer;
        this.customTitle = charSequence;
        photoViewerActionBarContainer.setTitle(charSequence);
        toggleActionBar(true, false);
    }

    public void openCurrentPhotoInPaintModeForSelect() {
        final File pathToAttach;
        final MessageObject messageObject;
        boolean zIsVideo;
        final boolean z;
        final boolean z2;
        final boolean z3;
        if (canSendMediaToParentChatActivity()) {
            MessageObject messageObject2 = this.currentMessageObject;
            File pathToMessage = null;
            if (messageObject2 != null) {
                boolean z4 = messageObject2.canEditMedia() && !this.currentMessageObject.isDocument();
                boolean z5 = z4 && this.currentMessageObject.isOutOwner();
                this.currentMessageObject.isVideo();
                if (!TextUtils.isEmpty(this.currentMessageObject.messageOwner.attachPath)) {
                    File file = new File(this.currentMessageObject.messageOwner.attachPath);
                    if (file.exists()) {
                        pathToMessage = file;
                    }
                }
                if (pathToMessage == null) {
                    pathToMessage = FileLoader.getInstance(this.currentAccount).getPathToMessage(this.currentMessageObject.messageOwner);
                }
                zIsVideo = this.currentMessageObject.isVideo();
                messageObject = messageObject2;
                pathToAttach = pathToMessage;
                z = z4;
                z2 = z5;
            } else {
                if (this.currentFileLocationVideo != null) {
                    pathToAttach = FileLoader.getInstance(this.currentAccount).getPathToAttach(getFileLocation(this.currentFileLocationVideo), getFileLocationExt(this.currentFileLocationVideo), !(Objects.equals(getFileLocationExt(this.currentFileLocationVideo), "mp4") || this.avatarsDialogId == 0) || this.isEvent);
                    messageObject = null;
                } else {
                    PageBlocksAdapter pageBlocksAdapter = this.pageBlocksAdapter;
                    if (pageBlocksAdapter != null) {
                        File file2 = pageBlocksAdapter.getFile(this.currentIndex);
                        zIsVideo = this.pageBlocksAdapter.isVideo(this.currentIndex);
                        pathToAttach = file2;
                        messageObject = null;
                        z = false;
                        z2 = false;
                    } else {
                        pathToAttach = null;
                        messageObject = null;
                    }
                }
                z3 = false;
                z = false;
                z2 = false;
                if (pathToAttach == null && pathToAttach.exists()) {
                    this.savedState = new SavedState(this.currentIndex, new ArrayList(this.imagesArr), this.placeProvider);
                    ActionBarToggleParams actionBarToggleParamsEnableStatusBarAnimation = new ActionBarToggleParams().enableStatusBarAnimation(false);
                    toggleActionBar(false, true, actionBarToggleParamsEnableStatusBarAnimation);
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda176
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$openCurrentPhotoInPaintModeForSelect$157(pathToAttach, z3, messageObject, z, z2);
                        }
                    }, actionBarToggleParamsEnableStatusBarAnimation.animationDuration);
                    return;
                }
                showDownloadAlert();
            }
            z3 = zIsVideo;
            if (pathToAttach == null) {
            }
            showDownloadAlert();
        }
    }

    public void lambda$openPhoto$158() {
        Activity activity = this.parentActivity;
        if (activity instanceof LaunchActivity) {
            ((LaunchActivity) activity).onBackPressed();
        } else if (isVisible()) {
            closePhoto(true, false);
        }
    }

    public class AnonymousClass81 implements ViewTreeObserver.OnPreDrawListener {
        final void lambda$onPhotoClosed$169(PlaceProviderObject placeProviderObject) {
        ClippingImageView clippingImageView;
        if (this.isVisible) {
            return;
        }
        this.animatingImageView.setImageBitmap(null);
        if (placeProviderObject != null && !AndroidUtilities.isTablet() && (clippingImageView = placeProviderObject.animatingImageView) != null) {
            clippingImageView.setImageBitmap(null);
        }
        try {
            if (this.windowView.getParent() != null) {
                ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
                onHideView();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void redraw(final int i) {
        FrameLayoutDrawer frameLayoutDrawer;
        if (i >= 6 || (frameLayoutDrawer = this.containerView) == null) {
            return;
        }
        frameLayoutDrawer.invalidate();
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda87
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$redraw$170(i);
            }
        }, 100L);
    }

    public int val$compressQuality;
        final public ChromecastMediaVariations getCurrentChromecastMedia() {
        String name;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return null;
        }
        TLRPC.Document document = messageObject.getDocument();
        String documentName = this.currentMessageObject.getDocumentName();
        if (TextUtils.isEmpty(documentName)) {
            documentName = this.currentMessageObject.getFileName();
        }
        long dialogId = this.currentMessageObject.getDialogId();
        if (DialogObject.isUserDialog(dialogId)) {
            TLRPC.User user = MessagesController.getInstance(UserConfig.selectedAccount).getUser(Long.valueOf(dialogId));
            if (user != null) {
                name = ContactsController.formatName(user.first_name, user.last_name);
            } else {
                name = null;
            }
        } else {
            TLRPC.Chat chat = MessagesController.getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(-dialogId));
            if (chat != null) {
                name = chat.title;
            } else {
                name = null;
            }
        }
        if (this.currentMessageObject.isPhoto()) {
            File pathToMessage = FileLoader.getInstance(this.currentMessageObject.currentAccount).getPathToMessage(this.currentMessageObject.messageOwner);
            if (pathToMessage == null || !pathToMessage.exists()) {
                return null;
            }
            return ChromecastMediaVariations.of(ChromecastMedia.Builder.fromUri(Uri.parse("file://" + pathToMessage.getAbsolutePath()), "/photo_" + this.currentMessageObject.getFeedRealId(), "image/jpeg").setTitle(name).setSubtitle(documentName).build());
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(document != null ? document.id : this.currentMessageObject.getFeedRealId());
        sb.append(_UrlKt.FRAGMENT_ENCODE_SET);
        return videoPlayer.getCurrentChromecastMedia(sb.toString(), name, documentName);
    }

    public void syncCastedPlayer() {
        if (isVisible()) {
            this.ignorePlayerUpdate = true;
            VideoPlayer videoPlayer = this.videoPlayer;
            if (videoPlayer != null) {
                videoPlayer.setMute(CastSync.isActive() || this.muteVideo);
            }
            if (this.videoPlayer != null && CastSync.isActive() && !CastSync.isUpdatePending()) {
                long position = CastSync.getPosition();
                if (position >= 0 && Math.abs(this.videoPlayer.getCurrentPosition() - position) > 1000) {
                    this.videoPlayer.seekTo(position);
                }
                boolean zIsPlaying = CastSync.isPlaying();
                VideoPlayer videoPlayer2 = this.videoPlayer;
                if (zIsPlaying) {
                    videoPlayer2.play();
                } else {
                    videoPlayer2.pause();
                }
                if (this.activityContext != null && Math.abs(CastSync.getDeviceVolume() - CastSync.getVolume()) > 0.05f) {
                    AudioManager audioManager = (AudioManager) this.activityContext.getSystemService(MediaStreamTrack.AUDIO_TRACK_KIND);
                    int streamMaxVolume = audioManager.getStreamMaxVolume(3);
                    int streamMinVolume = Build.VERSION.SDK_INT >= 28 ? audioManager.getStreamMinVolume(3) : 0;
                    int volume = streamMinVolume + ((int) ((streamMaxVolume - streamMinVolume) * CastSync.getVolume()));
                    if (volume != audioManager.getStreamVolume(3)) {
                        audioManager.setStreamVolume(3, volume, 1);
                    }
                }
                chooseSpeed(CastSync.getSpeed(), true, false);
            }
            ChooseQualityLayout$QualityIcon chooseQualityLayout$QualityIcon = this.videoItemIcon;
            if (chooseQualityLayout$QualityIcon != null) {
                chooseQualityLayout$QualityIcon.setCasting(CastSync.isActive(), true);
            }
            this.ignorePlayerUpdate = false;
        }
    }

    public long getCurrentPosition() {
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return -1L;
        }
        return videoPlayer.getCurrentPosition();
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        if (i == 0) {
            float f3 = 1.0f - f;
            this.pollAttachButtons.setTranslationY(AndroidUtilities.dp(36.0f) * f3);
            this.pollAttachButtons.setAlpha(f);
            this.pollAttachButtons.setVisibility(f > 0.0f ? 0 : 8);
            if (this.sendPhotoTypeIsPollMediaEdit) {
                this.pickerView.setVisibility(f < 1.0f ? 0 : 8);
                this.pickerView.setAlpha(f3);
                this.pickerView.setTranslationY(AndroidUtilities.dp(36.0f) * f);
                this.pickerViewSendButton.setVisibility(f < 1.0f ? 0 : 8);
                this.pickerViewSendButton.setAlpha(f3);
                this.pickerViewSendButton.setTranslationY(AndroidUtilities.dp(36.0f) * f);
            }
        }
    }

    public void pipInvalidateAvailability() {
        PipSource pipSource = this.pipSource;
        if (pipSource != null) {
            pipSource.invalidateAvailability();
        }
        if (PipVideoOverlay.getPipSource() != null) {
            PipVideoOverlay.getPipSource().invalidateAvailability();
        }
    }

    @Override 
    public boolean pipIsAvailable() {
        ActionBarMenuSubItem actionBarMenuSubItem = this.pipItem;
        return actionBarMenuSubItem != null && actionBarMenuSubItem.isEnabled() && this.isPlaying;
    }

    @Override 
    public Bitmap pipCreatePrimaryWindowViewBitmap() {
        TextureView textureView = this.videoTextureView;
        if (textureView != null) {
            return textureView.getBitmap();
        }
        if (!this.usedSurfaceView) {
            return null;
        }
        Bitmap bitmapCreateBitmap = Bitmaps.createBitmap(this.videoSurfaceView.getWidth(), this.videoSurfaceView.getHeight(), Bitmap.Config.ARGB_8888);
        AndroidUtilities.getBitmapFromSurface(this.videoSurfaceView, bitmapCreateBitmap);
        return bitmapCreateBitmap;
    }

    @Override 
    public void pipRenderBackground(Canvas canvas) {
        canvas.drawColor(-16777216);
    }

    @Override 
    public void pipRenderForeground(Canvas canvas) {
        this.textureViewSkipRender = true;
        this.windowView.draw(canvas);
        this.textureViewSkipRender = false;
    }

    @Override 
    public View pipCreatePictureInPictureView() {
        TextureView textureView = new TextureView(this.parentActivity);
        this.pipTextureView = textureView;
        textureView.setOpaque(false);
        View view = this.pipPlaceholderView;
        if (view != null) {
            view.bringToFront();
        }
        return this.pipTextureView;
    }

    @Override 
    public void pipHidePrimaryWindowView(Runnable runnable) {
        if (PipVideoOverlay.isVisible()) {
            PipVideoOverlay.dismiss(false);
        }
        this.pipFirstFrameCallback = runnable;
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer != null) {
            videoPlayer.setSurfaceView(null);
            this.videoPlayer.setTextureView(null);
            this.videoPlayer.play();
            this.videoPlayer.setTextureView(this.pipTextureView);
        }
        ((WindowManager) this.parentActivity.getSystemService("window")).removeView(this.windowView);
        this.windowViewSkipRender = true;
        this.windowView.invalidate();
    }

    @Override 
    public Bitmap pipCreatePictureInPictureViewBitmap() {
        TextureView textureView = this.pipTextureView;
        if (textureView == null || !textureView.isAvailable()) {
            return null;
        }
        return this.pipTextureView.getBitmap();
    }

    @Override 
    public void pipShowPrimaryWindowView(Runnable runnable) {
        CueGroup cueGroup;
        this.pipFirstFrameCallback = runnable;
        this.windowViewSkipRender = false;
        if (this.windowView != null) {
            ((WindowManager) this.parentActivity.getSystemService("window")).addView(this.windowView, this.windowLayoutParams);
            this.windowView.invalidate();
        }
        VideoPlayer videoPlayer = this.videoPlayer;
        if (videoPlayer == null) {
            return;
        }
        videoPlayer.setSurfaceView(null);
        this.videoPlayer.setTextureView(null);
        this.videoPlayer.play();
        TextureView textureView = this.videoTextureView;
        if (textureView != null) {
            this.videoPlayer.setTextureView(textureView);
        } else {
            SurfaceView surfaceView = this.videoSurfaceView;
            if (surfaceView != null) {
                this.videoPlayer.setSurfaceView(surfaceView);
            }
        }
        bringVideoOverlayViewsToFront();
        if (this.currentSubtitleState == null || (cueGroup = this.lastSubtitleCueGroup) == null) {
            return;
        }
        updateVideoSubtitles(cueGroup);
    }

    private void showPhotoQualityHint(boolean z) {
        HintView2 hintView2 = this.compressPhotoHint;
        if (hintView2 != null) {
            hintView2.hide();
            this.compressPhotoHint = null;
        }
        if (this.activityContext == null) {
            return;
        }
        this.compressPhotoHint = new HintView2(this.activityContext, 3);
        SpannableStringBuilder spannableStringBuilderAppend = new SpannableStringBuilder("x ").append((CharSequence) LocaleController.getString(z ? R.string.PhotoWillBeSentInHD : R.string.PhotoWillBeSentInSD));
        spannableStringBuilderAppend.setSpan(new ColoredImageSpan(z ? R.drawable.menu_quality_hd_filled : R.drawable.menu_quality_sd_filled), 0, 1, 33);
        this.compressPhotoHint.setText(spannableStringBuilderAppend);
        this.containerView.addView(this.compressPhotoHint, LayoutHelper.createFrame(-1, 100.0f, 87, 0.0f, 0.0f, 0.0f, 48.0f));
        this.compressPhotoHint.setTranslationY(this.pickerView.getTranslationY());
        this.compressPhotoHint.setJointPx(0.0f, this.itemsLayout.getX() + this.compressItem.getX() + (this.compressItem.getWidth() / 2.0f));
        final HintView2 hintView3 = this.compressPhotoHint;
        hintView3.setOnHiddenListener(new Runnable() { // from class: org.telegram.ui.PhotoViewer$$ExternalSyntheticLambda133
            @Override // java.lang.Runnable
            public final void run() {
                AndroidUtilities.removeFromParent(hintView3);
            }
        });
        this.compressPhotoHint.setDuration(3500L);
        this.compressPhotoHint.show();
    }
}
