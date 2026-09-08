package org.telegram.ui.ActionBar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.InsetDrawable;
import android.graphics.drawable.NinePatchDrawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.RectShape;
import android.graphics.drawable.shapes.RoundRectShape;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.util.StateSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.DividerStyle;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.utils.ui.MonetUtils;
import com.exteragram.messenger.utils.ui.TextPaint;
import com.google.android.gms.cast.CastStatusCodes;
import com.google.android.gms.cast.MediaError;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import kotlin.UByte;
import okhttp3.HttpUrl$$ExternalSyntheticBUOutline0;
import okhttp3.internal.url._UrlKt;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesController$$ExternalSyntheticLambda90;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.time.SunDate;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.BlurSettingsBottomSheet;
import org.telegram.ui.Cells.BaseCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AudioVisualizerDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChoosingStickerStatusDrawable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.FragmentContextViewWavesDrawable;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.MsgClockDrawable;
import org.telegram.ui.Components.PathAnimator;
import org.telegram.ui.Components.PlayingGameDrawable;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RecordStatusDrawable;
import org.telegram.ui.Components.RoundStatusDrawable;
import org.telegram.ui.Components.ScamDrawable;
import org.telegram.ui.Components.SendingFileDrawable;
import org.telegram.ui.Components.StatusDrawable;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.Components.TypingDotsDrawable;
import org.telegram.ui.Components.blur3.utils.NinePatchBuilder;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.RoundVideoProgressShadow;
import org.telegram.ui.ThemeActivity;
import org.telegram.ui.ThemePreviewActivity;

public abstract class Theme {
    public static Paint DEBUG_BLUE;
    public static Paint DEBUG_GREEN_40;
    public static Paint DEBUG_GREEN_B0;
    public static Paint DEBUG_GREEN_STROKE;
    public static Paint DEBUG_RED;
    public static Paint DEBUG_RED_STROKE;
    public static final Paint PAINT_CLEAR;
    private static final Paint PAINT_FILLING;
    private static Method StateListDrawable_getStateDrawableMethod;
    private static SensorEventListener ambientSensorListener;
    private static HashMap<MessageObject, AudioVisualizerDrawable> animatedOutVisualizerDrawables;
    private static SparseIntArray animatingColors;
    public static float autoNightBrighnessThreshold;
    public static String autoNightCityName;
    public static int autoNightDayEndTime;
    public static int autoNightDayStartTime;
    public static int autoNightLastSunCheckDay;
    public static double autoNightLocationLatitude;
    public static double autoNightLocationLongitude;
    public static boolean autoNightScheduleByLocation;
    public static int autoNightSunriseTime;
    public static int autoNightSunsetTime;
    public static Paint avatar_backgroundPaint;
    private static BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    private static Bitmap blurredBitmap;
    public static Drawable calllog_msgCallDownGreenDrawable;
    public static Drawable calllog_msgCallDownRedDrawable;
    public static Drawable calllog_msgCallUpGreenDrawable;
    public static Drawable calllog_msgCallUpRedDrawable;
    private static boolean canStartHolidayAnimation;
    private static boolean changingWallpaper;
    public static Paint chat_actionBackgroundGradientDarkenPaint;
    public static Paint chat_actionBackgroundPaint;
    public static Paint chat_actionBackgroundSelectedPaint;
    public static TextPaint chat_actionTextPaint;
    public static TextPaint chat_actionTextPaint2;
    public static TextPaint chat_actionTextPaint3;
    public static TextPaint chat_adminPaint;
    public static PorterDuffColorFilter chat_animatedEmojiTextColorFilter;
    public static Drawable chat_attachEmptyDrawable;
    public static TextPaint chat_audioPerformerPaint;
    public static TextPaint chat_audioTimePaint;
    public static TextPaint chat_audioTitlePaint;
    public static TextPaint chat_botButtonPaint;
    public static Drawable chat_botCardDrawable;
    public static Drawable chat_botInlineDrawable;
    public static Drawable chat_botInviteDrawable;
    public static Drawable chat_botLinkDrawable;
    public static Drawable chat_botLockDrawable;
    public static Drawable chat_botWebViewDrawable;
    public static Drawable chat_channelIconDrawable;
    public static Drawable chat_closeIconDrawable;
    public static Drawable chat_commentArrowDrawable;
    public static Drawable chat_commentDrawable;
    public static Drawable chat_commentStickerDrawable;
    public static TextPaint chat_commentTextPaint;
    public static Paint chat_composeBackgroundPaint;
    public static Drawable chat_composeShadowDrawable;
    public static Drawable chat_composeShadowRoundDrawable;
    public static TextPaint chat_contactNamePaint;
    public static TextPaint chat_contactPhonePaint;
    public static TextPaint chat_contextResult_descriptionTextPaint;
    public static Drawable chat_contextResult_shadowUnderSwitchDrawable;
    public static TextPaint chat_contextResult_titleTextPaint;
    public static Paint chat_deleteProgressPaint;
    public static Paint chat_docBackPaint;
    public static TextPaint chat_docNamePaint;
    public static TextPaint chat_durationPaint;
    public static TextPaint chat_ephemeralPaint;
    public static TextPaint chat_explanationTextPaint;
    public static Drawable chat_flameIcon;
    public static TextPaint chat_forwardNamePaint;
    public static TextPaint chat_gamePaint;
    public static Drawable chat_gifIcon;
    public static Drawable chat_goIconDrawable;
    public static Drawable chat_gradientLeftDrawable;
    public static Drawable chat_gradientRightDrawable;
    public static TextPaint chat_infoBoldPaint;
    public static TextPaint chat_infoPaint;
    public static Drawable chat_inlineResultAudio;
    public static Drawable chat_inlineResultFile;
    public static Drawable chat_inlineResultLocation;
    public static Paint chat_instantViewButtonPaint;
    public static TextPaint chat_instantViewPaint;
    public static Paint chat_instantViewRectPaint;
    public static TextPaint chat_livePaint;
    public static Drawable chat_livePhoto;
    public static TextPaint chat_locationAddressPaint;
    public static TextPaint chat_locationTitlePaint;
    public static Drawable chat_lockIconDrawable;
    public static Paint chat_messageBackgroundSelectedPaint;
    public static Drawable chat_moreIconDrawable;
    private static AudioVisualizerDrawable chat_msgAudioVisualizeDrawable;
    public static TextPaint chat_msgBotButtonPaint;
    public static Drawable chat_msgCallDownGreenDrawable;
    public static Drawable chat_msgCallDownRedDrawable;
    public static Drawable chat_msgCallUpGreenDrawable;
    public static MsgClockDrawable chat_msgClockDrawable;
    public static TextPaint chat_msgCodeBgPaint;
    public static Drawable chat_msgErrorDrawable;
    public static Paint chat_msgErrorPaint;
    public static TextPaint chat_msgGameTextPaint;
    public static MessageDrawable chat_msgInDrawable;
    public static Drawable chat_msgInInstantDrawable;
    public static MessageDrawable chat_msgInMediaDrawable;
    public static MessageDrawable chat_msgInMediaSelectedDrawable;
    public static Drawable chat_msgInMenuDrawable;
    public static Drawable chat_msgInMenuSelectedDrawable;
    public static Drawable chat_msgInPinnedDrawable;
    public static Drawable chat_msgInPinnedSelectedDrawable;
    public static Drawable chat_msgInRepliesDrawable;
    public static Drawable chat_msgInRepliesSelectedDrawable;
    public static MessageDrawable chat_msgInSelectedDrawable;
    public static Drawable chat_msgInViewsDrawable;
    public static Drawable chat_msgInViewsSelectedDrawable;
    public static Drawable chat_msgMediaCheckDrawable;
    public static Drawable chat_msgMediaHalfCheckDrawable;
    public static Drawable chat_msgMediaMenuDrawable;
    public static Drawable chat_msgMediaPinnedDrawable;
    public static Drawable chat_msgMediaRepliesDrawable;
    public static Drawable chat_msgMediaViewsDrawable;
    public static Drawable chat_msgNoSoundDrawable;
    public static Drawable chat_msgOutCheckDrawable;
    public static Drawable chat_msgOutCheckReadDrawable;
    public static Drawable chat_msgOutCheckReadSelectedDrawable;
    public static Drawable chat_msgOutCheckSelectedDrawable;
    public static MessageDrawable chat_msgOutDrawable;
    public static Drawable chat_msgOutHalfCheckDrawable;
    public static Drawable chat_msgOutHalfCheckSelectedDrawable;
    public static Drawable chat_msgOutInstantDrawable;
    public static MessageDrawable chat_msgOutMediaDrawable;
    public static MessageDrawable chat_msgOutMediaSelectedDrawable;
    public static Drawable chat_msgOutMenuDrawable;
    public static Drawable chat_msgOutMenuSelectedDrawable;
    public static Drawable chat_msgOutPinnedDrawable;
    public static Drawable chat_msgOutPinnedSelectedDrawable;
    public static Drawable chat_msgOutRepliesDrawable;
    public static Drawable chat_msgOutRepliesSelectedDrawable;
    public static MessageDrawable chat_msgOutSelectedDrawable;
    public static Drawable chat_msgOutViewsDrawable;
    public static Drawable chat_msgOutViewsSelectedDrawable;
    public static Drawable chat_msgStickerCheckDrawable;
    public static Drawable chat_msgStickerHalfCheckDrawable;
    public static Drawable chat_msgStickerPinnedDrawable;
    public static Drawable chat_msgStickerRepliesDrawable;
    public static Drawable chat_msgStickerViewsDrawable;
    public static TextPaint chat_msgTextCode2Paint;
    public static TextPaint chat_msgTextCode3Paint;
    public static TextPaint chat_msgTextCodePaint;
    public static TextPaint chat_msgTextPaint;
    public static TextPaint[] chat_msgTextPaintEmoji;
    public static TextPaint chat_msgTextPaintOneEmoji;
    public static TextPaint chat_msgTextPaintThreeEmoji;
    public static TextPaint chat_msgTextPaintTwoEmoji;
    public static Drawable chat_msgUnlockDrawable;
    public static Drawable chat_muteIconDrawable;
    public static TextPaint chat_namePaint;
    public static PorterDuffColorFilter chat_outAnimatedEmojiTextColorFilter;
    public static Paint chat_outUrlPaint;
    public static Drawable chat_pencilIconDrawable;
    public static Drawable chat_pluginIcon;
    public static Paint chat_pollTimerPaint;
    public static TextPaint chat_quoteTextPaint;
    public static Paint chat_radialProgress2Paint;
    public static Paint chat_radialProgressPaint;
    public static Paint chat_radialProgressPausedPaint;
    public static Paint chat_radialProgressPausedSeekbarPaint;
    public static Drawable chat_replyIconDrawable;
    public static Paint chat_replyLinePaint;
    public static TextPaint chat_replyNamePaint;
    public static TextPaint chat_replyTextPaint;
    public static Drawable chat_roundVideoShadow;
    public static Drawable chat_settingsIcon;
    public static Drawable chat_shareIconDrawable;
    public static TextPaint chat_shipmentPaint;
    public static Paint chat_statusPaint;
    public static Paint chat_statusRecordPaint;
    public static TextPaint chat_stickerCommentCountPaint;
    public static Drawable chat_stickersIcon;
    public static Paint chat_textSearchSelectionPaint;
    public static Paint chat_timeBackgroundPaint;
    public static TextPaint chat_timePaint;
    public static TextPaint chat_titleLabelTextPaint;
    public static TextPaint chat_topicTextPaint;
    public static TextPaint chat_unlockExtendedMediaTextPaint;
    public static Paint chat_urlPaint;
    public static Paint chat_videoProgressPaint;
    public static Paint checkboxSquare_backgroundPaint;
    public static Paint checkboxSquare_checkPaint;
    public static Paint checkboxSquare_eraserPaint;
    public static int colorsCount;
    public static int currentColor;
    private static SparseIntArray currentColors;
    private static SparseIntArray currentColorsNoAccent;
    private static ThemeInfo currentDayTheme;
    private static ThemeInfo currentNightTheme;
    private static ThemeInfo currentTheme;
    private static final HashMap<String, Integer> defaultChatDrawableColorKeys;
    private static final HashMap<String, Drawable> defaultChatDrawables;
    private static final HashMap<String, Integer> defaultChatPaintColors;
    private static final HashMap<String, Paint> defaultChatPaints;
    private static int[] defaultColors;
    private static ThemeInfo defaultTheme;
    public static Paint dialogs_actionMessagePaint;
    public static RLottieDrawable dialogs_archiveAvatarDrawable;
    public static boolean dialogs_archiveAvatarDrawableRecolored;
    public static RLottieDrawable dialogs_archiveDrawable;
    public static boolean dialogs_archiveDrawableRecolored;
    public static TextPaint dialogs_archiveTextPaint;
    public static TextPaint dialogs_archiveTextPaintSmall;
    public static Drawable dialogs_checkDrawable;
    public static Drawable dialogs_checkReadDrawable;
    public static Drawable dialogs_clockDrawable;
    public static Drawable dialogs_communityCardsDrawable;
    public static Paint dialogs_countGrayPaint;
    public static Paint dialogs_countPaint;
    public static TextPaint dialogs_countTextPaint;
    public static TextPaint dialogs_countTextPaint2;
    public static Drawable dialogs_errorDrawable;
    public static Paint dialogs_errorPaint;
    public static ScamDrawable dialogs_fakeDrawable;
    public static Drawable dialogs_forum_arrowDrawable;
    public static Drawable dialogs_halfCheckDrawable;
    public static Drawable dialogs_hiddenDrawable;
    public static RLottieDrawable dialogs_hidePsaDrawable;
    public static boolean dialogs_hidePsaDrawableRecolored;
    public static Drawable dialogs_holidayDrawable;
    private static int dialogs_holidayDrawableOffsetX;
    private static int dialogs_holidayDrawableOffsetY;
    public static Drawable dialogs_lock2Drawable;
    public static Drawable dialogs_lockDrawable;
    public static Drawable dialogs_mentionDrawable;
    public static Drawable dialogs_mentionDrawableMuted;
    public static TextPaint dialogs_messageNamePaint;
    public static TextPaint[] dialogs_messagePaint;
    public static TextPaint[] dialogs_messagePrintingPaint;
    public static Drawable dialogs_muteDrawable;
    public static TextPaint[] dialogs_nameEncryptedPaint;
    public static TextPaint[] dialogs_namePaint;
    public static TextPaint dialogs_offlinePaint;
    public static Paint dialogs_onlineCirclePaint;
    public static TextPaint dialogs_onlinePaint;
    public static RLottieDrawable dialogs_pinArchiveDrawable;
    public static Drawable dialogs_pinnedDrawable;
    public static Drawable dialogs_pinnedDrawable2;
    public static Drawable dialogs_pinnedDrawable2Accent;
    public static Paint dialogs_pinnedPaint;
    public static Drawable dialogs_playDrawable;
    public static Drawable dialogs_pollMentionDrawable;
    public static Drawable dialogs_pollMentionDrawableMuted;
    public static Drawable dialogs_reactionsMentionDrawable;
    public static Drawable dialogs_reactionsMentionDrawableMuted;
    public static Drawable dialogs_reorderDrawable;
    public static ScamDrawable dialogs_scamDrawable;
    public static TextPaint dialogs_searchNameEncryptedPaint;
    public static TextPaint dialogs_searchNamePaint;
    public static RLottieDrawable dialogs_swipeCommunityUngroup;
    public static RLottieDrawable dialogs_swipeDeleteDrawable;
    public static RLottieDrawable dialogs_swipeMuteDrawable;
    public static RLottieDrawable dialogs_swipePinDrawable;
    public static RLottieDrawable dialogs_swipeReadDrawable;
    public static RLottieDrawable dialogs_swipeUnmuteDrawable;
    public static RLottieDrawable dialogs_swipeUnpinDrawable;
    public static RLottieDrawable dialogs_swipeUnreadDrawable;
    public static Paint dialogs_tabletSeletedPaint;
    public static Paint dialogs_tagPaint;
    public static TextPaint dialogs_tagTextPaint;
    public static TextPaint dialogs_timePaint;
    public static TextPaint dialogs_timePaintBold;
    public static TextPaint dialogs_timePaintBoldAccent;
    public static RLottieDrawable dialogs_unarchiveDrawable;
    public static Drawable dialogs_unmuteDrawable;
    public static RLottieDrawable dialogs_unpinArchiveDrawable;
    public static Drawable dialogs_verifiedCheckDrawable;
    public static Drawable dialogs_verifiedDrawable;
    public static boolean disallowChangeServiceMessageColor;
    public static Paint dividerExtraPaint;
    public static Paint dividerPaint;
    private static SparseIntArray fallbackKeys;
    public static Paint forcedDividerPaint;
    private static FragmentContextViewWavesDrawable fragmentContextViewWavesDrawable;
    private static boolean hasPreviousTheme;
    private static final ThreadLocal<float[]> hsvTemp1Local;
    private static final ThreadLocal<float[]> hsvTemp2Local;
    private static final ThreadLocal<float[]> hsvTemp3Local;
    private static final ThreadLocal<float[]> hsvTemp4Local;
    private static final ThreadLocal<float[]> hsvTemp5Local;
    private static boolean isApplyingAccent;
    private static boolean isCustomTheme;
    private static boolean isInNigthMode;
    private static boolean isPatternWallpaper;
    private static boolean isWallpaperMotion;
    public static final int key_actionBarActionModeDefault;
    public static final int key_actionBarActionModeDefaultIcon;
    public static final int key_actionBarActionModeDefaultSelector;
    public static final int key_actionBarActionModeDefaultTop;
    public static final int key_actionBarActionModeReaction;
    public static final int key_actionBarActionModeReactionDot;
    public static final int key_actionBarActionModeReactionText;
    public static final int key_actionBarBrowser;
    public static final int key_actionBarDefault;
    public static final int key_actionBarDefaultArchived;
    public static final int key_actionBarDefaultArchivedIcon;
    public static final int key_actionBarDefaultArchivedSearch;
    public static final int key_actionBarDefaultArchivedSearchPlaceholder;
    public static final int key_actionBarDefaultArchivedSelector;
    public static final int key_actionBarDefaultArchivedTitle;
    public static final int key_actionBarDefaultIcon;
    public static final int key_actionBarDefaultSearch;
    public static final int key_actionBarDefaultSearchPlaceholder;
    public static final int key_actionBarDefaultSelector;
    public static final int key_actionBarDefaultSubmenuBackground;
    public static final int key_actionBarDefaultSubmenuItem;
    public static final int key_actionBarDefaultSubmenuItemIcon;
    public static final int key_actionBarDefaultSubmenuSeparator;
    public static final int key_actionBarDefaultSubtitle;
    public static final int key_actionBarDefaultTitle;
    public static final int key_actionBarTabActiveText;
    public static final int key_actionBarTabLine;
    public static final int key_actionBarTabSelector;
    public static final int key_actionBarTabUnactiveText;
    public static final int key_actionBarWhiteSelector;
    public static final int key_avatar_actionBarIconBlue;
    public static final int key_avatar_actionBarSelectorBlue;
    public static final int key_avatar_background2Blue;
    public static final int key_avatar_background2Cyan;
    public static final int key_avatar_background2Green;
    public static final int key_avatar_background2Orange;
    public static final int key_avatar_background2Pink;
    public static final int key_avatar_background2Red;
    public static final int key_avatar_background2Saved;
    public static final int key_avatar_background2Violet;
    public static final int key_avatar_backgroundActionBarBlue;
    public static final int key_avatar_backgroundArchived;
    public static final int key_avatar_backgroundArchivedHidden;
    public static final int key_avatar_backgroundBlue;
    public static final int key_avatar_backgroundCyan;
    public static final int key_avatar_backgroundGray;
    public static final int key_avatar_backgroundGreen;
    public static final int key_avatar_backgroundInProfileBlue;
    public static final int key_avatar_backgroundOrange;
    public static final int key_avatar_backgroundPink;
    public static final int key_avatar_backgroundRed;
    public static final int key_avatar_backgroundSaved;
    public static final int key_avatar_backgroundViolet;
    public static final int key_avatar_nameInMessageBlue;
    public static final int key_avatar_nameInMessageCyan;
    public static final int key_avatar_nameInMessageGreen;
    public static final int key_avatar_nameInMessageOrange;
    public static final int key_avatar_nameInMessagePink;
    public static final int key_avatar_nameInMessageRed;
    public static final int key_avatar_nameInMessageViolet;
    public static final int key_avatar_subtitleInProfileBlue;
    public static final int key_avatar_text;
    public static final int key_botKeyboard_button_danger;
    public static final int key_botKeyboard_button_primary;
    public static final int key_botKeyboard_button_success;
    public static final int key_bot_loadingIcon;
    public static final int key_buttonNeutral;
    public static final int key_buttonNeutralText;
    public static final int key_calls_callReceivedGreenIcon;
    public static final int key_calls_callReceivedRedIcon;
    public static final int key_changephoneinfo_image2;
    public static final int key_chat_BlurAlpha;
    public static final int key_chat_BlurAlphaSlow;
    public static final int key_chat_TextSelectionCursor;
    public static final int key_chat_addContact;
    public static final int key_chat_attachActiveTab;
    public static final int key_chat_attachAudioBackground;
    public static final int key_chat_attachCheckBoxBackground;
    public static final int key_chat_attachCheckBoxCheck;
    public static final int key_chat_attachContactBackground;
    public static final int key_chat_attachContactText;
    public static final int key_chat_attachEmptyImage;
    public static final int key_chat_attachGalleryBackground;
    public static final int key_chat_attachIcon;
    public static final int key_chat_attachLocationBackground;
    public static final int key_chat_attachPermissionImage;
    public static final int key_chat_attachPermissionMark;
    public static final int key_chat_attachPermissionText;
    public static final int key_chat_attachPhotoBackground;
    public static final int key_chat_attachPollBackground;
    public static final int key_chat_attachUnactiveTab;
    public static final int key_chat_botButtonText;
    public static final int key_chat_botKeyboardButtonBackground;
    public static final int key_chat_botKeyboardButtonBackgroundPressed;
    public static final int key_chat_botKeyboardButtonText;
    public static final int key_chat_botSwitchToInlineText;
    public static final int key_chat_editMediaButton;
    public static final int key_chat_emojiBottomPanelIcon;
    public static final int key_chat_emojiPanelBackground;
    public static final int key_chat_emojiPanelBackspace;
    public static final int key_chat_emojiPanelEmptyText;
    public static final int key_chat_emojiPanelIcon;
    public static final int key_chat_emojiPanelIconSelected;
    public static final int key_chat_emojiPanelNewTrending;
    public static final int key_chat_emojiPanelShadowLine;
    public static final int key_chat_emojiPanelStickerPackSelector;
    public static final int key_chat_emojiPanelStickerPackSelectorLine;
    public static final int key_chat_emojiPanelStickerSetName;
    public static final int key_chat_emojiPanelStickerSetNameHighlight;
    public static final int key_chat_emojiPanelStickerSetNameIcon;
    public static final int key_chat_emojiPanelTrendingDescription;
    public static final int key_chat_emojiPanelTrendingTitle;
    public static final int key_chat_emojiSearchBackground;
    public static final int key_chat_emojiSearchIcon;
    public static final int key_chat_fieldOverlayText;
    public static final int key_chat_gifSaveHintBackground;
    public static final int key_chat_gifSaveHintText;
    public static final int key_chat_goDownButton;
    public static final int key_chat_goDownButtonCounter;
    public static final int key_chat_goDownButtonCounterBackground;
    public static final int key_chat_inAdminSelectedText;
    public static final int key_chat_inAdminText;
    public static final int key_chat_inAudioCacheSeekbar;
    public static final int key_chat_inAudioDurationSelectedText;
    public static final int key_chat_inAudioDurationText;
    public static final int key_chat_inAudioPerformerSelectedText;
    public static final int key_chat_inAudioPerformerText;
    public static final int key_chat_inAudioProgress;
    public static final int key_chat_inAudioSeekbar;
    public static final int key_chat_inAudioSeekbarFill;
    public static final int key_chat_inAudioSeekbarSelected;
    public static final int key_chat_inAudioSelectedProgress;
    public static final int key_chat_inAudioTitleText;
    public static final int key_chat_inBubble;
    public static final int key_chat_inBubbleLocationPlaceholder;
    public static final int key_chat_inBubbleSelected;
    public static final int key_chat_inBubbleSelectedOverlay;
    public static final int key_chat_inBubbleShadow;
    public static final int key_chat_inCodeBackground;
    public static final int key_chat_inContactBackground;
    public static final int key_chat_inContactIcon;
    public static final int key_chat_inContactNameText;
    public static final int key_chat_inContactPhoneSelectedText;
    public static final int key_chat_inContactPhoneText;
    public static final int key_chat_inFileBackground;
    public static final int key_chat_inFileBackgroundSelected;
    public static final int key_chat_inFileInfoSelectedText;
    public static final int key_chat_inFileInfoText;
    public static final int key_chat_inFileNameText;
    public static final int key_chat_inFileProgress;
    public static final int key_chat_inFileProgressSelected;
    public static final int key_chat_inForwardedNameText;
    public static final int key_chat_inGreenCall;
    public static final int key_chat_inInstant;
    public static final int key_chat_inInstantSelected;
    public static final int key_chat_inLoader;
    public static final int key_chat_inLoaderPhoto;
    public static final int key_chat_inLoaderSelected;
    public static final int key_chat_inLocationBackground;
    public static final int key_chat_inLocationIcon;
    public static final int key_chat_inMediaIcon;
    public static final int key_chat_inMediaIconSelected;
    public static final int key_chat_inMenu;
    public static final int key_chat_inMenuSelected;
    public static final int key_chat_inPollCorrectAnswer;
    public static final int key_chat_inPollWrongAnswer;
    public static final int key_chat_inPreviewInstantText;
    public static final int key_chat_inPreviewLine;
    public static final int key_chat_inPsaNameText;
    public static final int key_chat_inQuote;
    public static final int key_chat_inReactionButtonBackground;
    public static final int key_chat_inReactionButtonText;
    public static final int key_chat_inReactionButtonTextSelected;
    public static final int key_chat_inReplyLine;
    public static final int key_chat_inReplyMediaMessageSelectedText;
    public static final int key_chat_inReplyMediaMessageText;
    public static final int key_chat_inReplyMessageText;
    public static final int key_chat_inReplyNameText;
    public static final int key_chat_inSentClock;
    public static final int key_chat_inSentClockSelected;
    public static final int key_chat_inSiteNameText;
    public static final int key_chat_inTextSelectionHighlight;
    public static final int key_chat_inTimeSelectedText;
    public static final int key_chat_inTimeText;
    public static final int key_chat_inVenueInfoSelectedText;
    public static final int key_chat_inVenueInfoText;
    public static final int key_chat_inViaBotNameText;
    public static final int key_chat_inViews;
    public static final int key_chat_inViewsSelected;
    public static final int key_chat_inVoiceSeekbar;
    public static final int key_chat_inVoiceSeekbarFill;
    public static final int key_chat_inVoiceSeekbarSelected;
    public static final int key_chat_inlineResultIcon;
    public static final int key_chat_linkSelectBackground;
    public static final int key_chat_lockIcon;
    public static final int key_chat_mediaInfoText;
    public static final int key_chat_mediaLoaderPhoto;
    public static final int key_chat_mediaLoaderPhotoIcon;
    public static final int key_chat_mediaLoaderPhotoIconSelected;
    public static final int key_chat_mediaLoaderPhotoSelected;
    public static final int key_chat_mediaMenu;
    public static final int key_chat_mediaProgress;
    public static final int key_chat_mediaSentCheck;
    public static final int key_chat_mediaSentClock;
    public static final int key_chat_mediaTimeBackground;
    public static final int key_chat_mediaTimeText;
    public static final int key_chat_mediaViews;
    public static final int key_chat_messageLinkIn;
    public static final int key_chat_messageLinkOut;
    public static final int key_chat_messagePanelBackground;
    public static final int key_chat_messagePanelCancelInlineBot;
    public static final int key_chat_messagePanelCursor;
    public static final int key_chat_messagePanelHint;
    public static final int key_chat_messagePanelIcons;
    public static final int key_chat_messagePanelSend;
    public static final int key_chat_messagePanelShadow;
    public static final int key_chat_messagePanelText;
    public static final int key_chat_messagePanelVoiceBackground;
    public static final int key_chat_messagePanelVoiceDelete;
    public static final int key_chat_messagePanelVoiceDuration;
    public static final int key_chat_messagePanelVoiceLock;
    public static final int key_chat_messagePanelVoiceLockBackground;
    public static final int key_chat_messagePanelVoiceLockShadow;
    public static final int key_chat_messagePanelVoicePressed;
    public static final int key_chat_messageTextIn;
    public static final int key_chat_messageTextOut;
    public static final int key_chat_muteIcon;
    public static final int key_chat_outAdminSelectedText;
    public static final int key_chat_outAdminText;
    public static final int key_chat_outAudioCacheSeekbar;
    public static final int key_chat_outAudioDurationSelectedText;
    public static final int key_chat_outAudioDurationText;
    public static final int key_chat_outAudioPerformerSelectedText;
    public static final int key_chat_outAudioPerformerText;
    public static final int key_chat_outAudioProgress;
    public static final int key_chat_outAudioSeekbar;
    public static final int key_chat_outAudioSeekbarFill;
    public static final int key_chat_outAudioSeekbarSelected;
    public static final int key_chat_outAudioSelectedProgress;
    public static final int key_chat_outAudioTitleText;
    public static final int key_chat_outBubble;
    public static final int key_chat_outBubbleGradient1;
    public static final int key_chat_outBubbleGradient2;
    public static final int key_chat_outBubbleGradient3;
    public static final int key_chat_outBubbleGradientAnimated;
    public static final int key_chat_outBubbleGradientSelectedOverlay;
    public static final int key_chat_outBubbleLocationPlaceholder;
    public static final int key_chat_outBubbleSelected;
    public static final int key_chat_outBubbleSelectedOverlay;
    public static final int key_chat_outBubbleShadow;
    public static final int key_chat_outCodeBackground;
    public static final int key_chat_outContactBackground;
    public static final int key_chat_outContactIcon;
    public static final int key_chat_outContactNameText;
    public static final int key_chat_outContactPhoneSelectedText;
    public static final int key_chat_outContactPhoneText;
    public static final int key_chat_outFileBackground;
    public static final int key_chat_outFileBackgroundSelected;
    public static final int key_chat_outFileInfoSelectedText;
    public static final int key_chat_outFileInfoText;
    public static final int key_chat_outFileNameText;
    public static final int key_chat_outFileProgress;
    public static final int key_chat_outFileProgressSelected;
    public static final int key_chat_outForwardedNameText;
    public static final int key_chat_outGreenCall;
    public static final int key_chat_outInstant;
    public static final int key_chat_outInstantSelected;
    public static final int key_chat_outLinkSelectBackground;
    public static final int key_chat_outLoader;
    public static final int key_chat_outLoaderSelected;
    public static final int key_chat_outLocationIcon;
    public static final int key_chat_outMediaIcon;
    public static final int key_chat_outMediaIconSelected;
    public static final int key_chat_outMenu;
    public static final int key_chat_outMenuSelected;
    public static final int key_chat_outPollCorrectAnswer;
    public static final int key_chat_outPollWrongAnswer;
    public static final int key_chat_outPreviewInstantText;
    public static final int key_chat_outPreviewLine;
    public static final int key_chat_outPsaNameText;
    public static final int key_chat_outQuote;
    public static final int key_chat_outReactionButtonBackground;
    public static final int key_chat_outReactionButtonText;
    public static final int key_chat_outReactionButtonTextSelected;
    public static final int key_chat_outReplyLine;
    public static final int key_chat_outReplyLine2;
    public static final int key_chat_outReplyMediaMessageSelectedText;
    public static final int key_chat_outReplyMediaMessageText;
    public static final int key_chat_outReplyMessageText;
    public static final int key_chat_outReplyNameText;
    public static final int key_chat_outSentCheck;
    public static final int key_chat_outSentCheckRead;
    public static final int key_chat_outSentCheckReadSelected;
    public static final int key_chat_outSentCheckSelected;
    public static final int key_chat_outSentClock;
    public static final int key_chat_outSentClockSelected;
    public static final int key_chat_outSiteNameText;
    public static final int key_chat_outTextSelectionCursor;
    public static final int key_chat_outTextSelectionHighlight;
    public static final int key_chat_outTimeSelectedText;
    public static final int key_chat_outTimeText;
    public static final int key_chat_outVenueInfoSelectedText;
    public static final int key_chat_outVenueInfoText;
    public static final int key_chat_outViaBotNameText;
    public static final int key_chat_outViews;
    public static final int key_chat_outViewsSelected;
    public static final int key_chat_outVoiceSeekbar;
    public static final int key_chat_outVoiceSeekbarFill;
    public static final int key_chat_outVoiceSeekbarSelected;
    public static final int key_chat_previewDurationText;
    public static final int key_chat_previewGameText;
    public static final int key_chat_reactionServiceButtonBackgroundSelected;
    public static final int key_chat_reactionServiceButtonTextSelected;
    public static final int key_chat_recordTime;
    public static final int key_chat_recordVoiceCancel;
    public static final int key_chat_recordedVoiceBackground;
    public static final int key_chat_recordedVoiceDarkerBackground;
    public static final int key_chat_recordedVoiceDot;
    public static final int key_chat_recordedVoicePlayPause;
    public static final int key_chat_recordedVoiceProgress;
    public static final int key_chat_recordedVoiceProgressInner;
    public static final int key_chat_replyPanelClose;
    public static final int key_chat_replyPanelIcons;
    public static final int key_chat_replyPanelLine;
    public static final int key_chat_replyPanelName;
    public static final int key_chat_searchPanelIcons;
    public static final int key_chat_searchPanelText;
    public static final int key_chat_secretChatStatusText;
    public static final int key_chat_secretTimeText;
    public static final int key_chat_selectedBackground;
    public static final int key_chat_sentError;
    public static final int key_chat_sentErrorIcon;
    public static final int key_chat_serviceBackground;
    public static final int key_chat_serviceBackgroundSelected;
    public static final int key_chat_serviceBackgroundSelector;
    public static final int key_chat_serviceIcon;
    public static final int key_chat_serviceLink;
    public static final int key_chat_serviceText;
    public static final int key_chat_status;
    public static final int key_chat_stickerNameText;
    public static final int key_chat_stickerReplyLine;
    public static final int key_chat_stickerReplyMessageText;
    public static final int key_chat_stickerReplyNameText;
    public static final int key_chat_stickerViaBotNameText;
    public static final int key_chat_stickersHintPanel;
    public static final int key_chat_tagAdmin;
    public static final int key_chat_tagCreator;
    public static final int key_chat_textSelectBackground;
    public static final int key_chat_topPanelBackground;
    public static final int key_chat_topPanelClose;
    public static final int key_chat_topPanelLine;
    public static final int key_chat_topPanelMessage;
    public static final int key_chat_topPanelTitle;
    public static final int key_chat_unreadMessagesStartArrowIcon;
    public static final int key_chat_unreadMessagesStartBackground;
    public static final int key_chat_unreadMessagesStartText;
    public static final int key_chat_wallpaper;
    public static final int key_chat_wallpaper_gradient_rotation;
    public static final int key_chat_wallpaper_gradient_to1;
    public static final int key_chat_wallpaper_gradient_to2;
    public static final int key_chat_wallpaper_gradient_to3;
    public static final int key_chats_actionBackground;
    public static final int key_chats_actionIcon;
    public static final int key_chats_actionMessage;
    public static final int key_chats_actionPressedBackground;
    public static final int key_chats_archiveBackground;
    public static final int key_chats_archiveIcon;
    public static final int key_chats_archivePinBackground;
    public static final int key_chats_archivePullDownBackground;
    public static final int key_chats_archivePullDownBackgroundActive;
    public static final int key_chats_archiveText;
    public static final int key_chats_attachMessage;
    public static final int key_chats_date;
    public static final int key_chats_date_bold;
    public static final int key_chats_draft;
    public static final int key_chats_mentionIcon;
    public static final int key_chats_menuBackground;
    public static final int key_chats_menuItemCheck;
    public static final int key_chats_menuItemIcon;
    public static final int key_chats_menuItemText;
    public static final int key_chats_menuName;
    public static final int key_chats_menuPhone;
    public static final int key_chats_menuPhoneCats;
    public static final int key_chats_menuTopBackground;
    public static final int key_chats_menuTopBackgroundCats;
    public static final int key_chats_menuTopShadow;
    public static final int key_chats_menuTopShadowCats;
    public static final int key_chats_message;
    public static final int key_chats_messageArchived;
    public static final int key_chats_message_threeLines;
    public static final int key_chats_muteIcon;
    public static final int key_chats_name;
    public static final int key_chats_nameArchived;
    public static final int key_chats_nameMessage;
    public static final int key_chats_nameMessageArchived;
    public static final int key_chats_nameMessageArchived_threeLines;
    public static final int key_chats_nameMessage_threeLines;
    public static final int key_chats_onlineCircle;
    public static final int key_chats_pinnedIcon;
    public static final int key_chats_pinnedOverlay;
    public static final int key_chats_secretIcon;
    public static final int key_chats_secretName;
    public static final int key_chats_sentCheck;
    public static final int key_chats_sentClock;
    public static final int key_chats_sentError;
    public static final int key_chats_sentErrorIcon;
    public static final int key_chats_sentReadCheck;
    public static final int key_chats_tabUnreadActiveBackground;
    public static final int key_chats_tabUnreadUnactiveBackground;
    public static final int key_chats_tabletSelectedOverlay;
    public static final int key_chats_unreadCounter;
    public static final int key_chats_unreadCounterMuted;
    public static final int key_chats_unreadCounterText;
    public static final int key_chats_verifiedBackground;
    public static final int key_chats_verifiedCheck;
    public static final int key_checkbox;
    public static final int key_checkboxCheck;
    public static final int key_checkboxDisabled;
    public static final int key_checkboxSquareBackground;
    public static final int key_checkboxSquareCheck;
    public static final int key_checkboxSquareDisabled;
    public static final int key_checkboxSquareUnchecked;
    public static final int key_code_comment;
    public static final int key_code_constant;
    public static final int key_code_function;
    public static final int key_code_keyword;
    public static final int key_code_number;
    public static final int key_code_operator;
    public static final int key_code_string;
    public static final int key_color_blue;
    public static final int key_color_cyan;
    public static final int key_color_green;
    public static final int key_color_lightblue;
    public static final int key_color_lightgreen;
    public static final int key_color_orange;
    public static final int key_color_purple;
    public static final int key_color_red;
    public static final int key_color_yellow;
    public static final int key_contacts_inviteBackground;
    public static final int key_contacts_inviteText;
    public static final int key_contextProgressInner1;
    public static final int key_contextProgressInner2;
    public static final int key_contextProgressInner3;
    public static final int key_contextProgressInner4;
    public static final int key_contextProgressOuter1;
    public static final int key_contextProgressOuter2;
    public static final int key_contextProgressOuter3;
    public static final int key_contextProgressOuter4;
    public static final int key_dialogBackground;
    public static final int key_dialogBackgroundGray;
    public static final int key_dialogButton;
    public static final int key_dialogButtonSelector;
    public static final int key_dialogCardShadow;
    public static final int key_dialogCheckboxSquareBackground;
    public static final int key_dialogCheckboxSquareCheck;
    public static final int key_dialogCheckboxSquareDisabled;
    public static final int key_dialogCheckboxSquareUnchecked;
    public static final int key_dialogEmptyImage;
    public static final int key_dialogEmptyText;
    public static final int key_dialogFloatingButton;
    public static final int key_dialogFloatingButtonPressed;
    public static final int key_dialogFloatingIcon;
    public static final int key_dialogGiftsBackground;
    public static final int key_dialogGiftsTabText;
    public static final int key_dialogGrayLine;
    public static final int key_dialogIcon;
    public static final int key_dialogInputField;
    public static final int key_dialogInputFieldActivated;
    public static final int key_dialogLineProgress;
    public static final int key_dialogLineProgressBackground;
    public static final int key_dialogLinkSelection;
    public static final int key_dialogRadioBackground;
    public static final int key_dialogRadioBackgroundChecked;
    public static final int key_dialogReactionMentionBackground;
    public static final int key_dialogRoundCheckBox;
    public static final int key_dialogRoundCheckBoxCheck;
    public static final int key_dialogScrollGlow;
    public static final int key_dialogSearchBackground;
    public static final int key_dialogSearchHint;
    public static final int key_dialogSearchIcon;
    public static final int key_dialogSearchText;
    public static final int key_dialogShadowLine;
    public static final int key_dialogSwipeRemove;
    public static final int key_dialogTextBlack;
    public static final int key_dialogTextBlue;
    public static final int key_dialogTextBlue2;
    public static final int key_dialogTextBlue4;
    public static final int key_dialogTextGray;
    public static final int key_dialogTextGray2;
    public static final int key_dialogTextGray3;
    public static final int key_dialogTextGray4;
    public static final int key_dialogTextHint;
    public static final int key_dialogTextLink;
    public static final int key_dialogTopBackground;
    public static final int key_dialog_inlineProgress;
    public static final int key_dialog_inlineProgressBackground;
    public static final int key_dialog_liveLocationProgress;
    public static final int key_divider;
    public static final int key_emptyListPlaceholder;
    public static final int key_fastScrollActive;
    public static final int key_fastScrollInactive;
    public static final int key_fastScrollText;
    public static final int key_featuredStickers_addButton;
    public static final int key_featuredStickers_addButton2;
    public static final int key_featuredStickers_addButtonPressed;
    public static final int key_featuredStickers_addedIcon;
    public static final int key_featuredStickers_buttonProgress;
    public static final int key_featuredStickers_buttonText;
    public static final int key_featuredStickers_removeButtonText;
    public static final int key_featuredStickers_unread;
    public static final int key_files_folderIcon;
    public static final int key_files_folderIconBackground;
    public static final int key_files_iconText;
    public static final int key_fill_RedDark;
    public static final int key_fill_RedNormal;
    public static final int key_gift_ribbon;
    public static final int key_gift_ribbon_soldout;
    public static final int key_glass_defaultIcon;
    public static final int key_glass_defaultText;
    public static final int key_glass_tabSelected;
    public static final int key_glass_tabSelectedText;
    public static final int key_glass_tabUnselected;
    public static final int key_glass_targetMainTabs;
    public static final int key_glass_targetMainTopPanel;
    public static final int key_graySection;
    public static final int key_graySectionText;
    public static final int key_groupcreate_cursor;
    public static final int key_groupcreate_hintText;
    public static final int key_groupcreate_sectionShadow;
    public static final int key_groupcreate_sectionText;
    public static final int key_groupcreate_spanBackground;
    public static final int key_groupcreate_spanDelete;
    public static final int key_groupcreate_spanText;
    public static final int key_inappPlayerBackground;
    public static final int key_inappPlayerClose;
    public static final int key_inappPlayerPerformer;
    public static final int key_inappPlayerPlayPause;
    public static final int key_inappPlayerTitle;
    public static final int key_iv_ab_progress;
    public static final int key_iv_background;
    public static final int key_iv_backgroundGray;
    public static final int key_iv_navigationBackground;
    public static final int key_listSelector;
    public static final int key_location_actionActiveIcon;
    public static final int key_location_actionBackground;
    public static final int key_location_actionIcon;
    public static final int key_location_actionPressedBackground;
    public static final int key_location_liveLocationProgress;
    public static final int key_location_placeLocationBackground;
    public static final int key_location_sendLiveLocationBackground;
    public static final int key_location_sendLiveLocationIcon;
    public static final int key_location_sendLiveLocationText;
    public static final int key_location_sendLocationBackground;
    public static final int key_location_sendLocationIcon;
    public static final int key_location_sendLocationText;
    public static final int key_login_progressInner;
    public static final int key_login_progressOuter;
    public static final int key_passport_authorizeBackground;
    public static final int key_passport_authorizeBackgroundSelected;
    public static final int key_passport_authorizeText;
    public static final int key_picker_badge;
    public static final int key_picker_badgeText;
    public static final int key_picker_disabledButton;
    public static final int key_picker_enabledButton;
    public static final int key_player_actionBarItems;
    public static final int key_player_actionBarSelector;
    public static final int key_player_actionBarSubtitle;
    public static final int key_player_actionBarTitle;
    public static final int key_player_background;
    public static final int key_player_button;
    public static final int key_player_buttonActive;
    public static final int key_player_progress;
    public static final int key_player_progressBackground;
    public static final int key_player_progressCachedBackground;
    public static final int key_player_time;
    public static final int key_pollCreateIcons;
    public static final int key_premiumCoinGradient1;
    public static final int key_premiumCoinGradient2;
    public static final int key_premiumGradient0;
    public static final int key_premiumGradient1;
    public static final int key_premiumGradient2;
    public static final int key_premiumGradient3;
    public static final int key_premiumGradient4;
    public static final int key_premiumGradientBackground1;
    public static final int key_premiumGradientBackground2;
    public static final int key_premiumGradientBackground3;
    public static final int key_premiumGradientBackground4;
    public static final int key_premiumGradientBackgroundOverlay;
    public static final int key_premiumGradientBottomSheet1;
    public static final int key_premiumGradientBottomSheet2;
    public static final int key_premiumGradientBottomSheet3;
    public static final int key_premiumStarGradient1;
    public static final int key_premiumStarGradient2;
    public static final int key_premiumStartSmallStarsColor;
    public static final int key_premiumStartSmallStarsColor2;
    public static final int key_profile_actionBackground;
    public static final int key_profile_actionIcon;
    public static final int key_profile_actionPressedBackground;
    public static final int key_profile_creatorIcon;
    public static final int key_profile_status;
    public static final int key_profile_tabSelectedLine;
    public static final int key_profile_tabSelectedText;
    public static final int key_profile_tabSelector;
    public static final int key_profile_tabText;
    public static final int key_profile_title;
    public static final int key_profile_verifiedBackground;
    public static final int key_profile_verifiedCheck;
    public static final int key_progressCircle;
    public static final int key_radioBackground;
    public static final int key_radioBackgroundChecked;
    public static final int key_reactionStarSelector;
    public static final int key_returnToCallBackground;
    public static final int key_returnToCallMutedBackground;
    public static final int key_returnToCallText;
    public static final int key_sessions_devicesImage;
    public static final int key_settings_listSelector;
    public static final int key_share_icon;
    public static final int key_share_linkBackground;
    public static final int key_share_linkText;
    public static final int key_sharedMedia_linkPlaceholder;
    public static final int key_sharedMedia_linkPlaceholderText;
    public static final int key_sharedMedia_photoPlaceholder;
    public static final int key_sharedMedia_startStopLoadIcon;
    public static final int key_sheet_other;
    public static final int key_sheet_scrollUp;
    public static final int key_starsGradient1;
    public static final int key_starsGradient2;
    public static final int key_statisticChartActiveLine;
    public static final int key_statisticChartActivePickerChart;
    public static final int key_statisticChartBackZoomColor;
    public static final int key_statisticChartChevronColor;
    public static final int key_statisticChartHintLine;
    public static final int key_statisticChartInactivePickerChart;
    public static final int key_statisticChartLineEmpty;
    public static final int key_statisticChartLine_blue;
    public static final int key_statisticChartLine_cyan;
    public static final int key_statisticChartLine_golden;
    public static final int key_statisticChartLine_green;
    public static final int key_statisticChartLine_indigo;
    public static final int key_statisticChartLine_lightblue;
    public static final int key_statisticChartLine_lightgreen;
    public static final int key_statisticChartLine_orange;
    public static final int key_statisticChartLine_purple;
    public static final int key_statisticChartLine_red;
    public static final int key_statisticChartRipple;
    public static final int key_statisticChartSignature;
    public static final int key_statisticChartSignatureAlpha;
    public static final int key_stickers_menu;
    public static final int key_stickers_menuSelector;
    public static final int key_stories_circle1;
    public static final int key_stories_circle2;
    public static final int key_stories_circle_closeFriends1;
    public static final int key_stories_circle_closeFriends2;
    public static final int key_stories_circle_dialog1;
    public static final int key_stories_circle_dialog2;
    public static final int key_stories_circle_live1;
    public static final int key_stories_circle_live2;
    public static final int key_switch2Track;
    public static final int key_switch2TrackChecked;
    public static final int key_switchTrack;
    public static final int key_switchTrackBlue;
    public static final int key_switchTrackBlueChecked;
    public static final int key_switchTrackBlueSelector;
    public static final int key_switchTrackBlueSelectorChecked;
    public static final int key_switchTrackBlueThumb;
    public static final int key_switchTrackBlueThumbChecked;
    public static final int key_switchTrackChecked;
    public static final int key_table_background;
    public static final int key_table_border;
    public static final int key_telegram_color;
    public static final int key_telegram_color_dialogsLogo;
    public static final int key_telegram_color_text;
    public static final int key_text_RedBold;
    public static final int key_text_RedRegular;
    public static final int key_topics_unreadCounter;
    public static final int key_topics_unreadCounterMuted;
    public static final int key_undo_background;
    public static final int key_undo_cancelColor;
    public static final int key_undo_infoColor;
    public static final int key_voipgroup_actionBar;
    public static final int key_voipgroup_actionBarItems;
    public static final int key_voipgroup_actionBarItemsSelector;
    public static final int key_voipgroup_actionBarUnscrolled;
    public static final int key_voipgroup_checkMenu;
    public static final int key_voipgroup_connectingProgress;
    public static final int key_voipgroup_dialogBackground;
    public static final int key_voipgroup_disabledButton;
    public static final int key_voipgroup_disabledButtonActive;
    public static final int key_voipgroup_disabledButtonActiveScrolled;
    public static final int key_voipgroup_inviteMembersBackground;
    public static final int key_voipgroup_lastSeenText;
    public static final int key_voipgroup_lastSeenTextUnscrolled;
    public static final int key_voipgroup_leaveButton;
    public static final int key_voipgroup_leaveButtonScrolled;
    public static final int key_voipgroup_leaveCallMenu;
    public static final int key_voipgroup_listSelector;
    public static final int key_voipgroup_listViewBackground;
    public static final int key_voipgroup_listViewBackgroundUnscrolled;
    public static final int key_voipgroup_listeningText;
    public static final int key_voipgroup_muteButton;
    public static final int key_voipgroup_muteButton2;
    public static final int key_voipgroup_muteButton3;
    public static final int key_voipgroup_mutedByAdminGradient;
    public static final int key_voipgroup_mutedByAdminGradient2;
    public static final int key_voipgroup_mutedByAdminGradient3;
    public static final int key_voipgroup_mutedByAdminIcon;
    public static final int key_voipgroup_mutedByAdminMuteButton;
    public static final int key_voipgroup_mutedByAdminMuteButtonDisabled;
    public static final int key_voipgroup_mutedIcon;
    public static final int key_voipgroup_mutedIconUnscrolled;
    public static final int key_voipgroup_nameText;
    public static final int key_voipgroup_overlayAlertGradientMuted;
    public static final int key_voipgroup_overlayAlertGradientMuted2;
    public static final int key_voipgroup_overlayAlertGradientUnmuted;
    public static final int key_voipgroup_overlayAlertGradientUnmuted2;
    public static final int key_voipgroup_overlayAlertMutedByAdmin;
    public static final int key_voipgroup_overlayAlertMutedByAdmin2;
    public static final int key_voipgroup_overlayBlue1;
    public static final int key_voipgroup_overlayBlue2;
    public static final int key_voipgroup_overlayGreen1;
    public static final int key_voipgroup_overlayGreen2;
    public static final int key_voipgroup_rtmpButton;
    public static final int key_voipgroup_scrollUp;
    public static final int key_voipgroup_searchBackground;
    public static final int key_voipgroup_searchPlaceholder;
    public static final int key_voipgroup_searchText;
    public static final int key_voipgroup_soundButton;
    public static final int key_voipgroup_soundButton2;
    public static final int key_voipgroup_soundButtonActive;
    public static final int key_voipgroup_soundButtonActive2;
    public static final int key_voipgroup_soundButtonActive2Scrolled;
    public static final int key_voipgroup_soundButtonActiveScrolled;
    public static final int key_voipgroup_speakingText;
    public static final int key_voipgroup_topPanelBlue1;
    public static final int key_voipgroup_topPanelBlue2;
    public static final int key_voipgroup_topPanelGray;
    public static final int key_voipgroup_topPanelGreen1;
    public static final int key_voipgroup_topPanelGreen2;
    public static final int key_voipgroup_unmuteButton;
    public static final int key_voipgroup_unmuteButton2;
    public static final int key_voipgroup_windowBackgroundWhiteInputField;
    public static final int key_voipgroup_windowBackgroundWhiteInputFieldActivated;
    public static final int key_wallpaperFileOffset;
    public static final int key_windowBackgroundCheckText;
    public static final int key_windowBackgroundChecked;
    public static final int key_windowBackgroundGray;
    public static final int key_windowBackgroundGrayShadow;
    public static final int key_windowBackgroundUnchecked;
    public static final int key_windowBackgroundWhite;
    public static final int key_windowBackgroundWhiteBlackText;
    public static final int key_windowBackgroundWhiteBlueButton;
    public static final int key_windowBackgroundWhiteBlueHeader;
    public static final int key_windowBackgroundWhiteBlueIcon;
    public static final int key_windowBackgroundWhiteBlueText;
    public static final int key_windowBackgroundWhiteBlueText2;
    public static final int key_windowBackgroundWhiteBlueText3;
    public static final int key_windowBackgroundWhiteBlueText4;
    public static final int key_windowBackgroundWhiteBlueText5;
    public static final int key_windowBackgroundWhiteBlueText6;
    public static final int key_windowBackgroundWhiteBlueText7;
    public static final int key_windowBackgroundWhiteGrayIcon;
    public static final int key_windowBackgroundWhiteGrayText;
    public static final int key_windowBackgroundWhiteGrayText2;
    public static final int key_windowBackgroundWhiteGrayText3;
    public static final int key_windowBackgroundWhiteGrayText4;
    public static final int key_windowBackgroundWhiteGrayText5;
    public static final int key_windowBackgroundWhiteGrayText6;
    public static final int key_windowBackgroundWhiteGrayText7;
    public static final int key_windowBackgroundWhiteGrayText8;
    public static final int key_windowBackgroundWhiteGreenText;
    public static final int key_windowBackgroundWhiteGreenText2;
    public static final int key_windowBackgroundWhiteHintText;
    public static final int key_windowBackgroundWhiteInputField;
    public static final int key_windowBackgroundWhiteInputFieldActivated;
    public static final int key_windowBackgroundWhiteLinkSelection;
    public static final int key_windowBackgroundWhiteLinkText;
    public static final int key_windowBackgroundWhiteValueText;
    public static int[] keys_avatar_background;
    public static int[] keys_avatar_background2;
    public static int[] keys_avatar_nameInMessage;
    public static final int[] keys_colors;
    private static long lastDelayUpdateTime;
    private static WeakReference<Drawable> lastDrawableToBlur;
    private static int lastLoadingCurrentThemeTime;
    private static long lastThemeSwitchTime;
    private static Sensor lightSensor;
    private static boolean lightSensorRegistered;
    public static Paint linkSelectionPaint;
    private static int loadingCurrentTheme;
    public static Drawable moveUpDrawable;
    public static final int myMessages2EndIndex;
    public static final int myMessages2StartIndex;
    public static final int myMessagesBubblesEndIndex;
    public static final int myMessagesBubblesStartIndex;
    public static final int myMessagesEndIndex;
    public static final int myMessagesStartIndex;
    private static ArrayList<ThemeInfo> otherThemes;
    private static int patternIntensity;
    public static PathAnimator playPauseAnimator;
    private static int previousPhase;
    private static ThemeInfo previousTheme;
    public static TextPaint profile_aboutTextPaint;
    public static Drawable profile_verifiedCheckDrawable;
    public static Drawable profile_verifiedDrawable;
    private static boolean resolvingDividerColor;
    private static RoundVideoProgressShadow roundPlayDrawable;
    public static int selectedAutoNightType;
    private static SensorManager sensorManager;
    private static Bitmap serviceBitmap;
    private static Matrix serviceBitmapMatrix;
    public static BitmapShader serviceBitmapShader;
    private static int serviceMessageColor;
    public static int serviceMessageColorBackup;
    private static int serviceSelectedMessageColor;
    public static int serviceSelectedMessageColorBackup;
    private static boolean shouldDrawGradientIcons;
    private static boolean switchDayRunnableScheduled;
    private static boolean switchNightRunnableScheduled;
    private static int switchNightThemeDelay;
    private static boolean switchingNightTheme;
    private static HashSet<Integer> themeAccentExclusionKeys;
    private static Drawable themedWallpaper;
    private static int themedWallpaperFileOffset;
    private static String themedWallpaperLink;
    public static ArrayList<ThemeInfo> themes;
    private static HashMap<String, ThemeInfo> themesDict;
    private static float[] tmpHSV5;
    private static int[] viewPos;
    private static Drawable wallpaper;
    public static Runnable wallpaperLoadTask;
    public static final int default_shadow_color = ColorUtils.setAlphaComponent(-16777216, 27);
    private static final Object sync = new Object();
    private static float lastBrightnessValue = 1.0f;
    private static Runnable switchDayBrightnessRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.Theme.1
        @Override // java.lang.Runnable
        public void run() {
            Theme.switchDayRunnableScheduled = false;
            Theme.applyDayNightThemeMaybe(false);
        }
    };
    private static Runnable switchNightBrightnessRunnable = new Runnable() { // from class: org.telegram.ui.ActionBar.Theme.2
        @Override // java.lang.Runnable
        public void run() {
            Theme.switchNightRunnableScheduled = false;
            Theme.applyDayNightThemeMaybe(true);
        }
    };
    public static int DEFALT_THEME_ACCENT_ID = 99;
    private static Paint maskPaint = new Paint(1);
    private static boolean[] loadingRemoteThemes = new boolean[16];
    private static int[] lastLoadingThemesTime = new int[16];
    private static long[] remoteThemesHash = new long[16];
    public static Drawable[] avatarDrawables = new Drawable[26];
    private static StatusDrawable[] chat_status_drawables = new StatusDrawable[6];
    public static Drawable[] chat_msgInCallDrawable = new Drawable[2];
    public static Drawable[] chat_msgInCallSelectedDrawable = new Drawable[2];
    public static Drawable[] chat_msgOutCallDrawable = new Drawable[2];
    public static Drawable[] chat_msgOutCallSelectedDrawable = new Drawable[2];
    public static Drawable[] chat_pollCheckDrawable = new Drawable[2];
    public static Drawable[] chat_pollCrossDrawable = new Drawable[2];
    public static Drawable[] chat_pollHintDrawable = new Drawable[2];
    public static Drawable[] chat_psaHelpDrawable = new Drawable[2];
    public static Drawable[] chat_locationDrawable = new Drawable[2];
    public static Drawable[] chat_contactDrawable = new Drawable[2];
    public static Drawable[][] chat_fileStatesDrawable = (Drawable[][]) Array.newInstance((Class<?>) Drawable.class, 5, 2);
    public static Path[] chat_filePath = new Path[2];
    public static Path[] chat_updatePath = new Path[3];

    public static class BackgroundDrawableSettings {
        public Boolean isCustomTheme;
        public Boolean isPatternWallpaper;
        public Boolean isWallpaperMotion;
        public Drawable themedWallpaper;
        public Drawable wallpaper;
    }

    public interface Colorable {
        default int[] getColorKeys() {
            return null;
        }

        void updateColors();
    }

    private static float abs(float f) {
        return f > 0.0f ? f : -f;
    }

    public static void destroyResources() {
    }

    public static Drawable getCurrentHolidayDrawable() {
        return null;
    }

    public static int getWallpaperColor(int i) {
        if (i == 0) {
            return 0;
        }
        return i | (-16777216);
    }

    void lambda$didReceivedNotification$3(LoadingPattern loadingPattern) {
            TLRPC.TL_wallPaper tL_wallPaper = loadingPattern.pattern;
            File pathToAttach = FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(tL_wallPaper.document, true);
            int size = loadingPattern.accents.size();
            Bitmap bitmapCreateWallpaperForAccent = null;
            ArrayList<ThemeAccent> arrayList = null;
            for (int i = 0; i < size; i++) {
                ThemeAccent themeAccent = loadingPattern.accents.get(i);
                if (themeAccent.patternSlug.equals(tL_wallPaper.slug)) {
                    bitmapCreateWallpaperForAccent = createWallpaperForAccent(bitmapCreateWallpaperForAccent, "application/x-tgwallpattern".equals(tL_wallPaper.document.mime_type), pathToAttach, themeAccent);
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                        arrayList.add(themeAccent);
                    }
                }
            }
            if (bitmapCreateWallpaperForAccent != null) {
                bitmapCreateWallpaperForAccent.recycle();
            }
            checkCurrentWallpaper(arrayList, false);
        }
    }

    public static class ThemeAccent {
        public int accentColor;
        public int accentColor2;
        public int account;
        public long backgroundGradientOverrideColor1;
        public long backgroundGradientOverrideColor2;
        public long backgroundGradientOverrideColor3;
        public long backgroundOverrideColor;
        public int id;
        public TLRPC.TL_theme info;
        public boolean isDefault;
        public int myMessagesAccentColor;
        public boolean myMessagesAnimated;
        public int myMessagesGradientAccentColor1;
        public int myMessagesGradientAccentColor2;
        public int myMessagesGradientAccentColor3;
        public OverrideWallpaperInfo overrideWallpaper;
        public ThemeInfo parentTheme;
        public TLRPC.TL_wallPaper pattern;
        public float patternIntensity;
        public boolean patternMotion;
        public TLRPC.InputFile uploadedFile;
        public TLRPC.InputFile uploadedThumb;
        public String uploadingFile;
        public String uploadingThumb;
        public int backgroundRotation = 45;
        public String patternSlug = _UrlKt.FRAGMENT_ENCODE_SET;
        private float[] tempHSV = new float[3];

        void lambda$didReceivedNotification$0(File file) {
            createBackground(file, this.newPathToWallpaper);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$ThemeInfo$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.onFinishLoadingRemoteTheme();
                }
            });
        }

        public void $r8$lambda$hXZO8feR59GGnPGakQxZrmdaYUg(String[] strArr, ThemeInfo themeInfo, boolean z, boolean z2, Runnable runnable) {
        try {
            themedWallpaperFileOffset = currentColorsNoAccent.get(key_wallpaperFileOffset, -1);
            if (!TextUtils.isEmpty(strArr[0])) {
                themedWallpaperLink = strArr[0];
                String absolutePath = new File(ApplicationLoader.getFilesDirFixed(), Utilities.MD5(themedWallpaperLink) + ".wp").getAbsolutePath();
                try {
                    String str = themeInfo.pathToWallpaper;
                    if (str != null && !str.equals(absolutePath)) {
                        new File(themeInfo.pathToWallpaper).delete();
                    }
                } catch (Exception unused) {
                }
                themeInfo.pathToWallpaper = absolutePath;
                try {
                    Uri uri = Uri.parse(themedWallpaperLink);
                    themeInfo.slug = uri.getQueryParameter("slug");
                    String queryParameter = uri.getQueryParameter("mode");
                    if (queryParameter != null) {
                        for (String str2 : queryParameter.toLowerCase().split(" ")) {
                            if ("blur".equals(str2)) {
                                themeInfo.isBlured = true;
                            } else if ("motion".equals(str2)) {
                                themeInfo.isMotion = true;
                            }
                        }
                    }
                    themeInfo.patternBgGradientRotation = 45;
                    try {
                        String queryParameter2 = uri.getQueryParameter("bg_color");
                        if (!TextUtils.isEmpty(queryParameter2)) {
                            themeInfo.patternBgColor = Integer.parseInt(queryParameter2.substring(0, 6), 16) | (-16777216);
                            if (queryParameter2.length() >= 13 && AndroidUtilities.isValidWallChar(queryParameter2.charAt(6))) {
                                themeInfo.patternBgGradientColor1 = Integer.parseInt(queryParameter2.substring(7, 13), 16) | (-16777216);
                            }
                            if (queryParameter2.length() >= 20 && AndroidUtilities.isValidWallChar(queryParameter2.charAt(13))) {
                                themeInfo.patternBgGradientColor2 = Integer.parseInt(queryParameter2.substring(14, 20), 16) | (-16777216);
                            }
                            if (queryParameter2.length() == 27 && AndroidUtilities.isValidWallChar(queryParameter2.charAt(20))) {
                                themeInfo.patternBgGradientColor3 = Integer.parseInt(queryParameter2.substring(21), 16) | (-16777216);
                            }
                        }
                    } catch (Exception unused2) {
                    }
                    try {
                        String queryParameter3 = uri.getQueryParameter("rotation");
                        if (!TextUtils.isEmpty(queryParameter3)) {
                            themeInfo.patternBgGradientRotation = Utilities.parseInt((CharSequence) queryParameter3).intValue();
                        }
                    } catch (Exception unused3) {
                    }
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            } else {
                try {
                    if (themeInfo.pathToWallpaper != null) {
                        new File(themeInfo.pathToWallpaper).delete();
                    }
                } catch (Exception unused4) {
                }
                themeInfo.pathToWallpaper = null;
                themedWallpaperLink = null;
            }
            if (!z && previousTheme == null) {
                currentDayTheme = themeInfo;
                if (isCurrentThemeNight()) {
                    switchNightThemeDelay = CastStatusCodes.AUTHENTICATION_FAILED;
                    lastDelayUpdateTime = SystemClock.elapsedRealtime();
                    AndroidUtilities.runOnUIThread(new MessagesController$$ExternalSyntheticLambda90(), 2100L);
                }
            }
            currentTheme = themeInfo;
            refreshThemeColors();
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (previousTheme == null && z2 && !switchingNightTheme) {
            MessagesController.getInstance(themeInfo.account).saveTheme(themeInfo, themeInfo.getAccent(false), z, false);
        }
        if (runnable != null) {
            runnable.run();
        }
    }

    public static /* synthetic */ void $r8$lambda$uoADhlIjZ0L8lu1XOfNoRRUgOGw(Runnable runnable, SparseIntArray sparseIntArray) {
        currentColorsNoAccent = sparseIntArray;
        runnable.run();
    }

    public static /* synthetic */ void $r8$lambda$Fw3ecpL5lIcIDhHXr1csyUDY9ms(Runnable runnable, SparseIntArray sparseIntArray) {
        currentColorsNoAccent = sparseIntArray;
        runnable.run();
    }

    public static boolean useBlackText(int i, int i2) {
        float fRed = Color.red(i) / 255.0f;
        float fRed2 = Color.red(i2) / 255.0f;
        float fGreen = Color.green(i) / 255.0f;
        float fGreen2 = Color.green(i2) / 255.0f;
        float fBlue = Color.blue(i) / 255.0f;
        return ((((fRed * 0.5f) + (fRed2 * 0.5f)) * 0.2126f) + (((fGreen * 0.5f) + (fGreen2 * 0.5f)) * 0.7152f)) + (((fBlue * 0.5f) + ((((float) Color.blue(i2)) / 255.0f) * 0.5f)) * 0.0722f) > 0.705f || ((fRed * 0.2126f) + (fGreen * 0.7152f)) + (fBlue * 0.0722f) > 0.705f;
    }

    public static void refreshThemeColors() {
        refreshThemeColors(false, false);
    }

    public static void refreshThemeColors(boolean z, boolean z2) {
        currentColors = currentColorsNoAccent.clone();
        shouldDrawGradientIcons = true;
        ThemeAccent accent = currentTheme.getAccent(false);
        if (accent != null) {
            shouldDrawGradientIcons = accent.fillAccentColors(currentColorsNoAccent, currentColors);
        }
        if (!z2) {
            reloadWallpaper(!(LaunchActivity.getLastFragment() instanceof ChatActivity));
        }
        applyCommonTheme();
        applyDialogsTheme();
        applyProfileTheme();
        applyChatTheme(false, z);
        final boolean z3 = !hasPreviousTheme;
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetNewTheme, Boolean.FALSE, Boolean.valueOf(z3), Boolean.TRUE);
            }
        });
    }

    public static boolean hasHue(int i) {
        float[] tempHsv = getTempHsv(3);
        Color.colorToHSV(i, tempHsv);
        float f = tempHsv[1];
        return f > 0.1f && f < 0.9f;
    }

    public static int changeColorAccent(int i, int i2, int i3, boolean z, int i4) {
        float[] tempHsv = getTempHsv(3);
        float[] tempHsv2 = getTempHsv(4);
        Color.colorToHSV(i, tempHsv);
        Color.colorToHSV(i2, tempHsv2);
        return changeColorAccent(tempHsv, tempHsv2, i3, z, i4);
    }

    public static int changeColorAccent(ThemeInfo themeInfo, int i, int i2) {
        int i3;
        if (i == 0 || (i3 = themeInfo.accentBaseColor) == 0 || i == i3 || (themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID)) {
            return i2;
        }
        float[] tempHsv = getTempHsv(3);
        float[] tempHsv2 = getTempHsv(4);
        Color.colorToHSV(themeInfo.accentBaseColor, tempHsv);
        Color.colorToHSV(i, tempHsv2);
        return changeColorAccent(tempHsv, tempHsv2, i2, themeInfo.isDark(), i2);
    }

    public static float[] getTempHsv(int i) {
        ThreadLocal<float[]> threadLocal;
        if (i == 1) {
            threadLocal = hsvTemp1Local;
        } else if (i == 2) {
            threadLocal = hsvTemp2Local;
        } else if (i == 3) {
            threadLocal = hsvTemp3Local;
        } else if (i == 4) {
            threadLocal = hsvTemp4Local;
        } else {
            threadLocal = hsvTemp5Local;
        }
        float[] fArr = threadLocal.get();
        if (fArr != null) {
            return fArr;
        }
        float[] fArr2 = new float[3];
        threadLocal.set(fArr2);
        return fArr2;
    }

    public static int getAccentColor(float[] fArr, int i, int i2) {
        float[] tempHsv = getTempHsv(3);
        float[] tempHsv2 = getTempHsv(4);
        Color.colorToHSV(i, tempHsv);
        Color.colorToHSV(i2, tempHsv2);
        float fMin = Math.min((tempHsv[1] * 1.5f) / fArr[1], 1.0f);
        tempHsv[0] = (tempHsv2[0] - tempHsv[0]) + fArr[0];
        tempHsv[1] = (tempHsv2[1] * fArr[1]) / tempHsv[1];
        float f = ((((tempHsv2[2] / tempHsv[2]) + fMin) - 1.0f) * fArr[2]) / fMin;
        tempHsv[2] = f;
        return f < 0.3f ? i2 : Color.HSVToColor(255, tempHsv);
    }

    public static int changeColorAccent(int i) {
        ThemeAccent accent = currentTheme.getAccent(false);
        return changeColorAccent(currentTheme, accent != null ? accent.accentColor : 0, i);
    }

    public static int changeColorAccent(float[] fArr, float[] fArr2, int i, boolean z, int i2) {
        if (tmpHSV5 == null) {
            tmpHSV5 = new float[3];
        }
        float[] fArr3 = tmpHSV5;
        Color.colorToHSV(i, fArr3);
        if (Math.min(abs(fArr3[0] - fArr[0]), abs((fArr3[0] - fArr[0]) - 360.0f)) > 30.0f) {
            return i2;
        }
        float fMin = Math.min((fArr3[1] * 1.5f) / fArr[1], 1.0f);
        fArr3[0] = (fArr3[0] + fArr2[0]) - fArr[0];
        fArr3[1] = (fArr3[1] * fArr2[1]) / fArr[1];
        fArr3[2] = fArr3[2] * ((1.0f - fMin) + ((fMin * fArr2[2]) / fArr[2]));
        int iHSVToColor = Color.HSVToColor(Color.alpha(i), fArr3);
        float fComputePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(i);
        float fComputePerceivedBrightness2 = AndroidUtilities.computePerceivedBrightness(iHSVToColor);
        if (z) {
            if (fComputePerceivedBrightness <= fComputePerceivedBrightness2) {
                return iHSVToColor;
            }
        } else if (fComputePerceivedBrightness >= fComputePerceivedBrightness2) {
            return iHSVToColor;
        }
        return changeBrightness(iHSVToColor, ((0.39999998f * fComputePerceivedBrightness) / fComputePerceivedBrightness2) + 0.6f);
    }

    private static int changeBrightness(int i, float f) {
        int iRed = (int) (Color.red(i) * f);
        int iGreen = (int) (Color.green(i) * f);
        int iBlue = (int) (Color.blue(i) * f);
        return Color.argb(Color.alpha(i), iRed < 0 ? 0 : Math.min(iRed, 255), iGreen < 0 ? 0 : Math.min(iGreen, 255), iBlue >= 0 ? Math.min(iBlue, 255) : 0);
    }

    public static boolean deleteThemeAccent(ThemeInfo themeInfo, ThemeAccent themeAccent, boolean z) {
        boolean z2 = false;
        if (themeAccent == null || themeInfo == null || themeInfo.themeAccents == null) {
            return false;
        }
        boolean z3 = themeAccent.id == themeInfo.currentAccentId;
        File pathToWallpaper = themeAccent.getPathToWallpaper();
        if (pathToWallpaper != null) {
            pathToWallpaper.delete();
        }
        themeInfo.themeAccentsMap.remove(themeAccent.id);
        themeInfo.themeAccents.remove(themeAccent);
        TLRPC.TL_theme tL_theme = themeAccent.info;
        if (tL_theme != null) {
            themeInfo.accentsByThemeId.remove(tL_theme.id);
        }
        OverrideWallpaperInfo overrideWallpaperInfo = themeAccent.overrideWallpaper;
        if (overrideWallpaperInfo != null) {
            overrideWallpaperInfo.delete();
        }
        if (z3) {
            themeInfo.setCurrentAccentId(themeInfo.themeAccents.get(0).id);
        }
        if (z) {
            saveThemeAccents(themeInfo, true, false, false, false);
            if (themeAccent.info != null) {
                MessagesController messagesController = MessagesController.getInstance(themeAccent.account);
                if (z3 && themeInfo == currentNightTheme) {
                    z2 = true;
                }
                messagesController.saveTheme(themeInfo, themeAccent, z2, true);
            }
        }
        return z3;
    }

    public static void saveThemeAccents(ThemeInfo themeInfo, boolean z, boolean z2, boolean z3, boolean z4) {
        saveThemeAccents(themeInfo, z, z2, z3, z4, false);
    }

    public static void saveThemeAccents(ThemeInfo themeInfo, boolean z, boolean z2, boolean z3, boolean z4, boolean z5) {
        if (z) {
            SharedPreferences.Editor editorEdit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
            if (!z3) {
                int size = themeInfo.themeAccents.size();
                int iMax = Math.max(0, size - themeInfo.defaultAccentCount);
                SerializedData serializedData = new SerializedData(((iMax * 16) + 2) * 4);
                serializedData.writeInt32(9);
                serializedData.writeInt32(iMax);
                for (int i = 0; i < size; i++) {
                    ThemeAccent themeAccent = themeInfo.themeAccents.get(i);
                    int i2 = themeAccent.id;
                    if (i2 >= 100) {
                        serializedData.writeInt32(i2);
                        serializedData.writeInt32(themeAccent.accentColor);
                        serializedData.writeInt32(themeAccent.accentColor2);
                        serializedData.writeInt32(themeAccent.myMessagesAccentColor);
                        serializedData.writeInt32(themeAccent.myMessagesGradientAccentColor1);
                        serializedData.writeInt32(themeAccent.myMessagesGradientAccentColor2);
                        serializedData.writeInt32(themeAccent.myMessagesGradientAccentColor3);
                        serializedData.writeBool(themeAccent.myMessagesAnimated);
                        serializedData.writeInt64(themeAccent.backgroundOverrideColor);
                        serializedData.writeInt64(themeAccent.backgroundGradientOverrideColor1);
                        serializedData.writeInt64(themeAccent.backgroundGradientOverrideColor2);
                        serializedData.writeInt64(themeAccent.backgroundGradientOverrideColor3);
                        serializedData.writeInt32(themeAccent.backgroundRotation);
                        serializedData.writeInt64(0L);
                        serializedData.writeDouble(themeAccent.patternIntensity);
                        serializedData.writeBool(themeAccent.patternMotion);
                        serializedData.writeString(themeAccent.patternSlug);
                        serializedData.writeBool(themeAccent.info != null);
                        if (themeAccent.info != null) {
                            serializedData.writeInt32(themeAccent.account);
                            themeAccent.info.serializeToStream(serializedData);
                        }
                    }
                }
                editorEdit.putString("accents_" + themeInfo.assetName, Base64.encodeToString(serializedData.toByteArray(), 3));
                if (!z5) {
                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeAccentListUpdated, new Object[0]);
                }
                if (z4) {
                    MessagesController.getInstance(UserConfig.selectedAccount).saveThemeToServer(themeInfo, themeInfo.getAccent(false));
                }
            }
            editorEdit.putInt("accent_current_" + themeInfo.assetName, themeInfo.currentAccentId);
            editorEdit.apply();
        } else {
            if (themeInfo.prevAccentId != -1) {
                if (z2) {
                    ThemeAccent themeAccent2 = themeInfo.themeAccentsMap.get(themeInfo.currentAccentId);
                    themeInfo.themeAccentsMap.remove(themeAccent2.id);
                    themeInfo.themeAccents.remove(themeAccent2);
                    TLRPC.TL_theme tL_theme = themeAccent2.info;
                    if (tL_theme != null) {
                        themeInfo.accentsByThemeId.remove(tL_theme.id);
                    }
                }
                themeInfo.currentAccentId = themeInfo.prevAccentId;
                ThemeAccent accent = themeInfo.getAccent(false);
                if (accent != null) {
                    themeInfo.overrideWallpaper = accent.overrideWallpaper;
                } else {
                    themeInfo.overrideWallpaper = null;
                }
            }
            if (currentTheme == themeInfo) {
                refreshThemeColors();
            }
        }
        themeInfo.prevAccentId = -1;
    }

    public static void saveOtherThemes(boolean z) {
        saveOtherThemes(z, false);
    }

    private static void saveOtherThemes(boolean z, boolean z2) {
        String str;
        boolean z3;
        ArrayList<ThemeAccent> arrayList;
        int i = 0;
        SharedPreferences.Editor editorEdit = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0).edit();
        if (z) {
            JSONArray jSONArray = new JSONArray();
            for (int i2 = 0; i2 < otherThemes.size(); i2++) {
                JSONObject saveJson = otherThemes.get(i2).getSaveJson();
                if (saveJson != null) {
                    jSONArray.put(saveJson);
                }
            }
            editorEdit.putString("themes2", jSONArray.toString());
        }
        int i3 = 0;
        while (i3 < 16) {
            StringBuilder sb = new StringBuilder("2remoteThemesHash");
            Object objValueOf = _UrlKt.FRAGMENT_ENCODE_SET;
            sb.append(i3 != 0 ? Integer.valueOf(i3) : _UrlKt.FRAGMENT_ENCODE_SET);
            editorEdit.putLong(sb.toString(), remoteThemesHash[i3]);
            StringBuilder sb2 = new StringBuilder("lastLoadingThemesTime");
            if (i3 != 0) {
                objValueOf = Integer.valueOf(i3);
            }
            sb2.append(objValueOf);
            editorEdit.putInt(sb2.toString(), lastLoadingThemesTime[i3]);
            i3++;
        }
        editorEdit.putInt("lastLoadingCurrentThemeTime", lastLoadingCurrentThemeTime);
        editorEdit.apply();
        if (z) {
            while (i < 5) {
                if (i == 0) {
                    str = "Blue";
                } else if (i == 1) {
                    str = "Dark Blue";
                } else if (i == 2) {
                    str = "Arctic Blue";
                } else if (i == 3) {
                    str = "Day";
                } else {
                    str = "Night";
                }
                ThemeInfo themeInfo = themesDict.get(str);
                if (themeInfo == null || (arrayList = themeInfo.themeAccents) == null || arrayList.isEmpty()) {
                    z3 = z2;
                } else {
                    z3 = z2;
                    saveThemeAccents(themeInfo, true, false, false, false, z3);
                }
                i++;
                z2 = z3;
            }
        }
    }

    public static int[] getDefaultColors() {
        return defaultColors;
    }

    public static ThemeInfo getPreviousTheme() {
        return previousTheme;
    }

    public static String getCurrentNightThemeName() {
        ThemeInfo themeInfo = currentNightTheme;
        if (themeInfo == null) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        String name = themeInfo.getName();
        return name.toLowerCase().endsWith(".attheme") ? name.substring(0, name.lastIndexOf(46)) : name;
    }

    public static ThemeInfo getCurrentTheme() {
        ThemeInfo themeInfo = currentDayTheme;
        return themeInfo != null ? themeInfo : defaultTheme;
    }

    public static ThemeInfo getCurrentNightTheme() {
        return currentNightTheme;
    }

    public static boolean isCurrentThemeNight() {
        return currentTheme == currentNightTheme;
    }

    public static boolean isCurrentThemeDark() {
        return currentTheme.isDark();
    }

    public static boolean isCurrentThemeMonet() {
        return currentTheme.isMonet();
    }

    public static boolean isCurrentThemeMonet(ResourcesProvider resourcesProvider) {
        return resourcesProvider != null ? resourcesProvider.isMonet() : isCurrentThemeMonet();
    }

    public static boolean isCurrentAccentMonet() {
        ThemeInfo themeInfo = currentTheme;
        return MonetAccentHelper.isMonetAccent(themeInfo != null ? themeInfo.getAccent(false) : null);
    }

    public static void refreshMonetColors() {
        ArrayList<ThemeInfo> arrayList;
        if (Build.VERSION.SDK_INT < 31 || (arrayList = themes) == null) {
            return;
        }
        int size = arrayList.size();
        boolean zRefresh = false;
        for (int i = 0; i < size; i++) {
            ThemeInfo themeInfo = themes.get(i);
            if (themeInfo != null) {
                zRefresh |= MonetAccentHelper.refresh(themeInfo);
            }
        }
        if (zRefresh) {
            PatternsLoader.createLoader(true);
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeListUpdated, new Object[0]);
    }

    public static ThemeInfo getActiveTheme() {
        return currentTheme;
    }

    public static long getAutoNightSwitchThemeDelay() {
        return Math.abs(lastThemeSwitchTime - SystemClock.elapsedRealtime()) >= 12000 ? 1800L : 12000L;
    }

    public static void setCurrentNightTheme(ThemeInfo themeInfo) {
        boolean z = currentTheme == currentNightTheme;
        currentNightTheme = themeInfo;
        if (z) {
            applyDayNightThemeMaybe(true);
        }
    }

    public static void checkAutoNightThemeConditions() {
        checkAutoNightThemeConditions(false);
    }

    public static void cancelAutoNightThemeCallbacks() {
        if (selectedAutoNightType != 2) {
            if (switchNightRunnableScheduled) {
                switchNightRunnableScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(switchNightBrightnessRunnable);
            }
            if (switchDayRunnableScheduled) {
                switchDayRunnableScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(switchDayBrightnessRunnable);
            }
            if (lightSensorRegistered) {
                lastBrightnessValue = 1.0f;
                sensorManager.unregisterListener(ambientSensorListener, lightSensor);
                lightSensorRegistered = false;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("light sensor unregistered");
                }
            }
        }
    }

    private static int needSwitchToTheme() {
        Sensor sensor;
        SensorEventListener sensorEventListener;
        int i;
        int i2;
        int i3 = selectedAutoNightType;
        if (i3 == 1) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            int i4 = (calendar.get(11) * 60) + calendar.get(12);
            if (autoNightScheduleByLocation) {
                int i5 = calendar.get(5);
                if (autoNightLastSunCheckDay != i5) {
                    double d = autoNightLocationLatitude;
                    if (d != 10000.0d) {
                        double d2 = autoNightLocationLongitude;
                        if (d2 != 10000.0d) {
                            int[] iArrCalculateSunriseSunset = SunDate.calculateSunriseSunset(d, d2);
                            autoNightSunriseTime = iArrCalculateSunriseSunset[0];
                            autoNightSunsetTime = iArrCalculateSunriseSunset[1];
                            autoNightLastSunCheckDay = i5;
                            saveAutoNightThemeConfig();
                        }
                    }
                }
                i = autoNightSunsetTime;
                i2 = autoNightSunriseTime;
            } else {
                i = autoNightDayStartTime;
                i2 = autoNightDayEndTime;
            }
            if (i < i2) {
                return (i > i4 || i4 > i2) ? 1 : 2;
            }
            return ((i > i4 || i4 > 1440) && (i4 < 0 || i4 > i2)) ? 1 : 2;
        }
        if (i3 == 2) {
            if (lightSensor == null) {
                SensorManager sensorManager2 = (SensorManager) ApplicationLoader.applicationContext.getSystemService("sensor");
                sensorManager = sensorManager2;
                lightSensor = sensorManager2.getDefaultSensor(5);
            }
            if (!lightSensorRegistered && (sensor = lightSensor) != null && (sensorEventListener = ambientSensorListener) != null) {
                sensorManager.registerListener(sensorEventListener, sensor, 500000);
                lightSensorRegistered = true;
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("light sensor registered");
                }
            }
            if (lastBrightnessValue <= autoNightBrighnessThreshold) {
                if (!switchNightRunnableScheduled) {
                    return 2;
                }
            } else if (!switchDayRunnableScheduled) {
                return 1;
            }
        } else if (i3 == 3) {
            int i6 = ApplicationLoader.applicationContext.getResources().getConfiguration().uiMode & 48;
            if (i6 == 0 || i6 == 16) {
                return 1;
            }
            if (i6 == 32) {
                return 2;
            }
        } else if (i3 == 0) {
            return 1;
        }
        return 0;
    }

    public static void setChangingWallpaper(boolean z) {
        changingWallpaper = z;
        if (z) {
            return;
        }
        checkAutoNightThemeConditions(false);
    }

    public static void checkAutoNightThemeConditions(boolean z) {
        if (previousTheme != null || changingWallpaper) {
            return;
        }
        if (!z && switchNightThemeDelay > 0) {
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            long j = jElapsedRealtime - lastDelayUpdateTime;
            lastDelayUpdateTime = jElapsedRealtime;
            int i = (int) (((long) switchNightThemeDelay) - j);
            switchNightThemeDelay = i;
            if (i > 0) {
                return;
            }
        }
        if (z) {
            if (switchNightRunnableScheduled) {
                switchNightRunnableScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(switchNightBrightnessRunnable);
            }
            if (switchDayRunnableScheduled) {
                switchDayRunnableScheduled = false;
                AndroidUtilities.cancelRunOnUIThread(switchDayBrightnessRunnable);
            }
        }
        cancelAutoNightThemeCallbacks();
        int iNeedSwitchToTheme = needSwitchToTheme();
        if (iNeedSwitchToTheme != 0) {
            applyDayNightThemeMaybe(iNeedSwitchToTheme == 2);
        }
        if (z) {
            lastThemeSwitchTime = 0L;
        }
    }

    public static void applyDayNightThemeMaybe(boolean z) {
        ThemeInfo themeInfo;
        if (previousTheme != null) {
            return;
        }
        if (z) {
            ThemeInfo themeInfo2 = currentTheme;
            ThemeInfo themeInfo3 = currentNightTheme;
            if (themeInfo2 != themeInfo3) {
                if (themeInfo2 == null || !(themeInfo3 == null || themeInfo2.isDark() == currentNightTheme.isDark())) {
                    isInNigthMode = true;
                    lastThemeSwitchTime = SystemClock.elapsedRealtime();
                    switchingNightTheme = true;
                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needSetDayNightTheme, currentNightTheme, Boolean.TRUE, null, -1);
                    switchingNightTheme = false;
                    return;
                }
                return;
            }
            return;
        }
        ThemeInfo themeInfo4 = currentDayTheme;
        if (themeInfo4 != null && themeInfo4.isDark() && selectedAutoNightType != 0 && (themeInfo = defaultTheme) != null) {
            themeInfo4 = themeInfo;
        }
        ThemeInfo themeInfo5 = currentTheme;
        if (themeInfo5 != themeInfo4) {
            if (themeInfo5 == null || !(themeInfo4 == null || themeInfo5.isDark() == themeInfo4.isDark())) {
                isInNigthMode = false;
                lastThemeSwitchTime = SystemClock.elapsedRealtime();
                switchingNightTheme = true;
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needSetDayNightTheme, themeInfo4, Boolean.TRUE, null, -1);
                switchingNightTheme = false;
            }
        }
    }

    public static boolean deleteTheme(ThemeInfo themeInfo) {
        boolean z = false;
        if (themeInfo.pathToFile == null) {
            return false;
        }
        if (currentTheme == themeInfo) {
            applyTheme(defaultTheme, true, false, false);
            z = true;
        }
        if (themeInfo == currentNightTheme) {
            currentNightTheme = themesDict.get("Dark Blue");
        }
        themeInfo.removeObservers();
        otherThemes.remove(themeInfo);
        themesDict.remove(themeInfo.name);
        OverrideWallpaperInfo overrideWallpaperInfo = themeInfo.overrideWallpaper;
        if (overrideWallpaperInfo != null) {
            overrideWallpaperInfo.delete();
        }
        themes.remove(themeInfo);
        new File(themeInfo.pathToFile).delete();
        saveOtherThemes(true);
        return z;
    }

    public static ThemeInfo createNewTheme(String str) throws Throwable {
        ThemeInfo themeInfo = new ThemeInfo();
        themeInfo.pathToFile = new File(ApplicationLoader.getFilesDirFixed(), "theme" + Utilities.random.nextLong() + ".attheme").getAbsolutePath();
        themeInfo.name = str;
        themedWallpaperLink = getWallpaperUrl(currentTheme.overrideWallpaper);
        themeInfo.account = UserConfig.selectedAccount;
        saveCurrentTheme(themeInfo, true, true, false);
        return themeInfo;
    }

    private static String getWallpaperUrl(OverrideWallpaperInfo overrideWallpaperInfo) {
        String str;
        if (overrideWallpaperInfo == null || TextUtils.isEmpty(overrideWallpaperInfo.slug) || overrideWallpaperInfo.slug.equals("d")) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        if (overrideWallpaperInfo.isBlurred) {
            sb.append("blur");
        }
        if (overrideWallpaperInfo.isMotion) {
            if (sb.length() > 0) {
                sb.append("+");
            }
            sb.append("motion");
        }
        int i = overrideWallpaperInfo.color;
        if (i == 0) {
            str = "https://attheme.org?slug=" + overrideWallpaperInfo.slug;
        } else {
            String lowerCase = String.format("%02x%02x%02x", Integer.valueOf(((byte) (i >> 16)) & UByte.MAX_VALUE), Integer.valueOf(((byte) (overrideWallpaperInfo.color >> 8)) & UByte.MAX_VALUE), Byte.valueOf((byte) (overrideWallpaperInfo.color & 255))).toLowerCase();
            int i2 = overrideWallpaperInfo.gradientColor1;
            String lowerCase2 = i2 != 0 ? String.format("%02x%02x%02x", Integer.valueOf(((byte) (i2 >> 16)) & UByte.MAX_VALUE), Integer.valueOf(((byte) (overrideWallpaperInfo.gradientColor1 >> 8)) & UByte.MAX_VALUE), Byte.valueOf((byte) (overrideWallpaperInfo.gradientColor1 & 255))).toLowerCase() : null;
            int i3 = overrideWallpaperInfo.gradientColor2;
            String lowerCase3 = i3 != 0 ? String.format("%02x%02x%02x", Integer.valueOf(((byte) (i3 >> 16)) & UByte.MAX_VALUE), Integer.valueOf(((byte) (overrideWallpaperInfo.gradientColor2 >> 8)) & UByte.MAX_VALUE), Byte.valueOf((byte) (overrideWallpaperInfo.gradientColor2 & 255))).toLowerCase() : null;
            int i4 = overrideWallpaperInfo.gradientColor3;
            String lowerCase4 = i4 != 0 ? String.format("%02x%02x%02x", Integer.valueOf(((byte) (i4 >> 16)) & UByte.MAX_VALUE), Integer.valueOf(((byte) (overrideWallpaperInfo.gradientColor3 >> 8)) & UByte.MAX_VALUE), Byte.valueOf((byte) (overrideWallpaperInfo.gradientColor3 & 255))).toLowerCase() : null;
            if (lowerCase2 == null || lowerCase3 == null) {
                if (lowerCase2 != null) {
                    lowerCase = (lowerCase + "-" + lowerCase2) + "&rotation=" + overrideWallpaperInfo.rotation;
                }
            } else if (lowerCase4 != null) {
                lowerCase = lowerCase + "~" + lowerCase2 + "~" + lowerCase3 + "~" + lowerCase4;
            } else {
                lowerCase = lowerCase + "~" + lowerCase2 + "~" + lowerCase3;
            }
            str = "https://attheme.org?slug=" + overrideWallpaperInfo.slug + "&intensity=" + ((int) (overrideWallpaperInfo.intensity * 100.0f)) + "&bg_color=" + lowerCase;
        }
        if (sb.length() <= 0) {
            return str;
        }
        return str + "&mode=" + ((Object) sb);
    }

    /* JADX WARN: Code duplicated, block: B:100:0x0178 A[Catch: all -> 0x0108, Exception -> 0x010d, TryCatch #7 {Exception -> 0x010d, all -> 0x0108, blocks: (B:81:0x00f2, B:83:0x00f8, B:85:0x00fc, B:87:0x0102, B:92:0x0111, B:94:0x0124, B:109:0x01a8, B:111:0x01b4, B:112:0x01cd, B:114:0x01d3, B:116:0x01d7, B:117:0x01df, B:99:0x0174, B:100:0x0178, B:102:0x017c, B:104:0x0185, B:107:0x01a0), top: B:146:0x00f2 }] */
    /* JADX WARN: Code duplicated, block: B:102:0x017c A[Catch: all -> 0x0108, Exception -> 0x010d, TryCatch #7 {Exception -> 0x010d, all -> 0x0108, blocks: (B:81:0x00f2, B:83:0x00f8, B:85:0x00fc, B:87:0x0102, B:92:0x0111, B:94:0x0124, B:109:0x01a8, B:111:0x01b4, B:112:0x01cd, B:114:0x01d3, B:116:0x01d7, B:117:0x01df, B:99:0x0174, B:100:0x0178, B:102:0x017c, B:104:0x0185, B:107:0x01a0), top: B:146:0x00f2 }] */
    /* JADX WARN: Code duplicated, block: B:104:0x0185 A[Catch: all -> 0x0108, Exception -> 0x010d, TryCatch #7 {Exception -> 0x010d, all -> 0x0108, blocks: (B:81:0x00f2, B:83:0x00f8, B:85:0x00fc, B:87:0x0102, B:92:0x0111, B:94:0x0124, B:109:0x01a8, B:111:0x01b4, B:112:0x01cd, B:114:0x01d3, B:116:0x01d7, B:117:0x01df, B:99:0x0174, B:100:0x0178, B:102:0x017c, B:104:0x0185, B:107:0x01a0), top: B:146:0x00f2 }] */
    /* JADX WARN: Code duplicated, block: B:109:0x01a8 A[Catch: all -> 0x0108, Exception -> 0x010d, TryCatch #7 {Exception -> 0x010d, all -> 0x0108, blocks: (B:81:0x00f2, B:83:0x00f8, B:85:0x00fc, B:87:0x0102, B:92:0x0111, B:94:0x0124, B:109:0x01a8, B:111:0x01b4, B:112:0x01cd, B:114:0x01d3, B:116:0x01d7, B:117:0x01df, B:99:0x0174, B:100:0x0178, B:102:0x017c, B:104:0x0185, B:107:0x01a0), top: B:146:0x00f2 }] */
    /* JADX WARN: Code duplicated, block: B:111:0x01b4 A[Catch: all -> 0x0108, Exception -> 0x010d, TryCatch #7 {Exception -> 0x010d, all -> 0x0108, blocks: (B:81:0x00f2, B:83:0x00f8, B:85:0x00fc, B:87:0x0102, B:92:0x0111, B:94:0x0124, B:109:0x01a8, B:111:0x01b4, B:112:0x01cd, B:114:0x01d3, B:116:0x01d7, B:117:0x01df, B:99:0x0174, B:100:0x0178, B:102:0x017c, B:104:0x0185, B:107:0x01a0), top: B:146:0x00f2 }] */
    /* JADX WARN: Code duplicated, block: B:114:0x01d3 A[Catch: all -> 0x0108, Exception -> 0x010d, TryCatch #7 {Exception -> 0x010d, all -> 0x0108, blocks: (B:81:0x00f2, B:83:0x00f8, B:85:0x00fc, B:87:0x0102, B:92:0x0111, B:94:0x0124, B:109:0x01a8, B:111:0x01b4, B:112:0x01cd, B:114:0x01d3, B:116:0x01d7, B:117:0x01df, B:99:0x0174, B:100:0x0178, B:102:0x017c, B:104:0x0185, B:107:0x01a0), top: B:146:0x00f2 }] */
    /* JADX WARN: Code duplicated, block: B:116:0x01d7 A[Catch: all -> 0x0108, Exception -> 0x010d, TryCatch #7 {Exception -> 0x010d, all -> 0x0108, blocks: (B:81:0x00f2, B:83:0x00f8, B:85:0x00fc, B:87:0x0102, B:92:0x0111, B:94:0x0124, B:109:0x01a8, B:111:0x01b4, B:112:0x01cd, B:114:0x01d3, B:116:0x01d7, B:117:0x01df, B:99:0x0174, B:100:0x0178, B:102:0x017c, B:104:0x0185, B:107:0x01a0), top: B:146:0x00f2 }] */
    /* JADX WARN: Code duplicated, block: B:129:0x020d  */
    /* JADX WARN: Code duplicated, block: B:137:0x0141 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:158:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:94:0x0124 A[Catch: all -> 0x0108, Exception -> 0x010d, TRY_LEAVE, TryCatch #7 {Exception -> 0x010d, all -> 0x0108, blocks: (B:81:0x00f2, B:83:0x00f8, B:85:0x00fc, B:87:0x0102, B:92:0x0111, B:94:0x0124, B:109:0x01a8, B:111:0x01b4, B:112:0x01cd, B:114:0x01d3, B:116:0x01d7, B:117:0x01df, B:99:0x0174, B:100:0x0178, B:102:0x017c, B:104:0x0185, B:107:0x01a0), top: B:146:0x00f2 }] */
    /* JADX WARN: Instruction removed from duplicated block: B:94:0x0124, please report this as an issue */
    /* JADX WARN: Multi-variable type inference failed */
    public static void saveCurrentTheme(ThemeInfo themeInfo, boolean z, boolean z2, boolean z3) throws Throwable {
        String wallpaperUrl;
        Throwable th;
        FileOutputStream fileOutputStream;
        Bitmap bitmap;
        OverrideWallpaperInfo overrideWallpaperInfo = themeInfo.overrideWallpaper;
        if (overrideWallpaperInfo != null) {
            wallpaperUrl = getWallpaperUrl(overrideWallpaperInfo);
        } else {
            wallpaperUrl = themedWallpaperLink;
        }
        Drawable drawable = z2 ? wallpaper : themedWallpaper;
        if (z2 && drawable != null) {
            themedWallpaper = wallpaper;
        }
        ThemeAccent accent = currentTheme.getAccent(false);
        Object[] objArr = currentTheme.firstAccentIsDefault && accent.id == DEFALT_THEME_ACCENT_ID;
        FileOutputStream fileOutputStream2 = null;
        SparseIntArray sparseIntArray = objArr != false ? null : currentColors;
        StringBuilder sb = new StringBuilder();
        if (objArr == false) {
            int i = accent != null ? accent.myMessagesAccentColor : 0;
            int i2 = accent != null ? accent.myMessagesGradientAccentColor1 : 0;
            int i3 = accent != null ? accent.myMessagesGradientAccentColor2 : 0;
            int i4 = accent != null ? accent.myMessagesGradientAccentColor3 : 0;
            if (i != 0 && i2 != 0) {
                sparseIntArray.put(key_chat_outBubble, i);
                sparseIntArray.put(key_chat_outBubbleGradient1, i2);
                if (i3 != 0) {
                    sparseIntArray.put(key_chat_outBubbleGradient2, i3);
                    if (i4 != 0) {
                        sparseIntArray.put(key_chat_outBubbleGradient3, i4);
                    }
                }
                sparseIntArray.put(key_chat_outBubbleGradientAnimated, accent.myMessagesAnimated ? 1 : 0);
            }
        }
        try {
            try {
                try {
                    try {
                        if (objArr == false) {
                            for (int i5 = 0; i5 < sparseIntArray.size(); i5++) {
                                int iKeyAt = sparseIntArray.keyAt(i5);
                                int iValueAt = sparseIntArray.valueAt(i5);
                                if ((!(drawable instanceof BitmapDrawable) && wallpaperUrl == null) || (key_chat_wallpaper != iKeyAt && key_chat_wallpaper_gradient_to1 != iKeyAt && key_chat_wallpaper_gradient_to2 != iKeyAt && key_chat_wallpaper_gradient_to3 != iKeyAt)) {
                                    sb.append(ThemeColors.getStringName(iKeyAt));
                                    sb.append("=");
                                    sb.append(iValueAt);
                                    sb.append("\n");
                                }
                            }
                        } else {
                            int i6 = 0;
                            while (true) {
                                int[] iArr = defaultColors;
                                if (i6 >= iArr.length) {
                                    break;
                                }
                                int i7 = iArr[i6];
                                if ((!(drawable instanceof BitmapDrawable) && wallpaperUrl == null) || (key_chat_wallpaper != i6 && key_chat_wallpaper_gradient_to1 != i6 && key_chat_wallpaper_gradient_to2 != i6 && key_chat_wallpaper_gradient_to3 != i6)) {
                                    sb.append(ThemeColors.getStringName(i6));
                                    sb.append("=");
                                    sb.append(i7);
                                    sb.append("\n");
                                }
                                i6++;
                            }
                            fileOutputStream = new FileOutputStream(themeInfo.pathToFile);
                            if (sb.length() == 0 && !(drawable instanceof BitmapDrawable) && TextUtils.isEmpty(wallpaperUrl)) {
                                sb.append(' ');
                            }
                            fileOutputStream.write(AndroidUtilities.getStringBytes(sb.toString()));
                            if (!TextUtils.isEmpty(wallpaperUrl)) {
                                fileOutputStream.write(AndroidUtilities.getStringBytes("WLS=" + wallpaperUrl + "\n"));
                                if (z2) {
                                    try {
                                        Bitmap bitmap2 = ((BitmapDrawable) drawable).getBitmap();
                                        FileOutputStream fileOutputStream3 = new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), Utilities.MD5(wallpaperUrl) + ".wp"));
                                        bitmap2.compress(Bitmap.CompressFormat.JPEG, 87, fileOutputStream3);
                                        fileOutputStream3.close();
                                    } catch (Throwable th2) {
                                        FileLog.e(th2);
                                    }
                                }
                            } else if (drawable instanceof BitmapDrawable) {
                                bitmap = ((BitmapDrawable) drawable).getBitmap();
                                if (bitmap != null) {
                                    fileOutputStream.write(new byte[]{87, 80, 83, 10});
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 87, fileOutputStream);
                                    fileOutputStream.write(new byte[]{10, 87, 80, 69, 10});
                                }
                                if (z && !z3) {
                                    wallpaper = drawable;
                                    calcBackgroundColor(drawable, 2);
                                }
                            }
                            if (!z3) {
                                if (themesDict.get(themeInfo.getKey()) == null) {
                                    themes.add(themeInfo);
                                    themesDict.put(themeInfo.getKey(), themeInfo);
                                    otherThemes.add(themeInfo);
                                    saveOtherThemes(true);
                                    sortThemes();
                                }
                                currentTheme = themeInfo;
                                if (themeInfo != currentNightTheme) {
                                    currentDayTheme = themeInfo;
                                }
                                if (objArr != false) {
                                    currentColorsNoAccent.clear();
                                    refreshThemeColors();
                                }
                                SharedPreferences.Editor editorEdit = MessagesController.getGlobalMainSettings().edit();
                                editorEdit.putString("theme", currentDayTheme.getKey());
                                editorEdit.apply();
                            }
                            fileOutputStream.close();
                            if (z) {
                                MessagesController.getInstance(themeInfo.account).saveThemeToServer(themeInfo, themeInfo.getAccent(false));
                            }
                        }
                        if (sb.length() == 0) {
                            sb.append(' ');
                        }
                        fileOutputStream.write(AndroidUtilities.getStringBytes(sb.toString()));
                        if (!TextUtils.isEmpty(wallpaperUrl)) {
                            fileOutputStream.write(AndroidUtilities.getStringBytes("WLS=" + wallpaperUrl + "\n"));
                            if (z2) {
                                Bitmap bitmap3 = ((BitmapDrawable) drawable).getBitmap();
                                FileOutputStream fileOutputStream4 = new FileOutputStream(new File(ApplicationLoader.getFilesDirFixed(), Utilities.MD5(wallpaperUrl) + ".wp"));
                                bitmap3.compress(Bitmap.CompressFormat.JPEG, 87, fileOutputStream4);
                                fileOutputStream4.close();
                            }
                        } else if (drawable instanceof BitmapDrawable) {
                            bitmap = ((BitmapDrawable) drawable).getBitmap();
                            if (bitmap != null) {
                                fileOutputStream.write(new byte[]{87, 80, 83, 10});
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 87, fileOutputStream);
                                fileOutputStream.write(new byte[]{10, 87, 80, 69, 10});
                            }
                            if (z) {
                                wallpaper = drawable;
                                calcBackgroundColor(drawable, 2);
                            }
                        }
                        if (!z3) {
                            if (themesDict.get(themeInfo.getKey()) == null) {
                                themes.add(themeInfo);
                                themesDict.put(themeInfo.getKey(), themeInfo);
                                otherThemes.add(themeInfo);
                                saveOtherThemes(true);
                                sortThemes();
                            }
                            currentTheme = themeInfo;
                            if (themeInfo != currentNightTheme) {
                                currentDayTheme = themeInfo;
                            }
                            if (objArr != false) {
                                currentColorsNoAccent.clear();
                                refreshThemeColors();
                            }
                            SharedPreferences.Editor editorEdit2 = MessagesController.getGlobalMainSettings().edit();
                            editorEdit2.putString("theme", currentDayTheme.getKey());
                            editorEdit2.apply();
                        }
                        fileOutputStream.close();
                    } catch (Exception e) {
                        e = e;
                        fileOutputStream2 = fileOutputStream;
                        FileLog.e(e);
                        if (fileOutputStream2 != null) {
                            fileOutputStream2.close();
                        }
                        if (z) {
                            MessagesController.getInstance(themeInfo.account).saveThemeToServer(themeInfo, themeInfo.getAccent(false));
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        fileOutputStream2 = fileOutputStream;
                        if (fileOutputStream2 != null) {
                            try {
                                fileOutputStream2.close();
                                throw th;
                            } catch (Exception e2) {
                                FileLog.e(e2);
                                throw th;
                            }
                        }
                        throw th;
                    }
                    fileOutputStream = new FileOutputStream(themeInfo.pathToFile);
                } catch (Exception e3) {
                    FileLog.e(e3);
                }
            } catch (Exception e4) {
                e = e4;
            }
            if (z) {
                MessagesController.getInstance(themeInfo.account).saveThemeToServer(themeInfo, themeInfo.getAccent(false));
            }
        } catch (Throwable th4) {
            th = th4;
        }
    }

    public static void checkCurrentRemoteTheme(boolean z) {
        int i;
        if (loadingCurrentTheme == 0) {
            if (z || Math.abs((System.currentTimeMillis() / 1000) - ((long) lastLoadingCurrentThemeTime)) >= 3600) {
                int i2 = 0;
                while (i2 < 2) {
                    final ThemeInfo themeInfo = i2 == 0 ? currentDayTheme : currentNightTheme;
                    if (themeInfo != null && UserConfig.getInstance(themeInfo.account).isClientActivated()) {
                        final ThemeAccent accent = themeInfo.getAccent(false);
                        final TLRPC.TL_theme tL_theme = themeInfo.info;
                        if (tL_theme != null) {
                            i = themeInfo.account;
                        } else if (accent != null && (tL_theme = accent.info) != null) {
                            i = UserConfig.selectedAccount;
                        }
                        if (tL_theme.document != null) {
                            loadingCurrentTheme++;
                            TL_account.getTheme gettheme = new TL_account.getTheme();
                            gettheme.document_id = tL_theme.document.id;
                            gettheme.format = "android";
                            TLRPC.TL_inputTheme tL_inputTheme = new TLRPC.TL_inputTheme();
                            tL_inputTheme.access_hash = tL_theme.access_hash;
                            tL_inputTheme.id = tL_theme.id;
                            gettheme.theme = tL_inputTheme;
                            ConnectionsManager.getInstance(i).sendRequest(gettheme, new RequestDelegate() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda5
                                @Override // org.telegram.tgnet.RequestDelegate
                                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda6
                                        @Override // java.lang.Runnable
                                        public final void run() {
                                            Theme.m5944$r8$lambda$YaQiMDK_ThvoQBl6AnzeXT7s8(tLObject, themeAccent, themeInfo, tL_theme);
                                        }
                                    });
                                }
                            });
                        }
                    }
                    i2++;
                }
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:42:0x0098  */
    public static /* synthetic */ void m5944$r8$lambda$YaQiMDK_ThvoQBl6AnzeXT7s8(TLObject tLObject, ThemeAccent themeAccent, ThemeInfo themeInfo, TLRPC.TL_theme tL_theme) {
        boolean z;
        TLRPC.WallPaperSettings wallPaperSettings;
        boolean z2 = true;
        loadingCurrentTheme--;
        if (tLObject instanceof TLRPC.TL_theme) {
            TLRPC.TL_theme tL_theme2 = (TLRPC.TL_theme) tLObject;
            TLRPC.ThemeSettings themeSettings = tL_theme2.settings.size() > 0 ? tL_theme2.settings.get(0) : null;
            if (themeAccent != null && themeSettings != null) {
                if (ThemeInfo.accentEquals(themeAccent, themeSettings)) {
                    z = false;
                } else {
                    File pathToWallpaper = themeAccent.getPathToWallpaper();
                    if (pathToWallpaper != null) {
                        pathToWallpaper.delete();
                    }
                    ThemeInfo.fillAccentValues(themeAccent, themeSettings);
                    ThemeInfo themeInfo2 = currentTheme;
                    if (themeInfo2 == themeInfo && themeInfo2.currentAccentId == themeAccent.id) {
                        refreshThemeColors();
                        createChatResources(ApplicationLoader.applicationContext, false);
                        NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                        int i = NotificationCenter.needSetDayNightTheme;
                        ThemeInfo themeInfo3 = currentTheme;
                        globalInstance.lambda$postNotificationNameOnUIThread$1(i, themeInfo3, Boolean.valueOf(currentNightTheme == themeInfo3), null, -1);
                    }
                    PatternsLoader.createLoader(true);
                    z = true;
                }
                TLRPC.WallPaper wallPaper = themeSettings.wallpaper;
                themeAccent.patternMotion = (wallPaper == null || (wallPaperSettings = wallPaper.settings) == null || !wallPaperSettings.motion) ? false : true;
                z2 = z;
            } else {
                TLRPC.Document document = tL_theme2.document;
                if (document == null || document.id == tL_theme.document.id) {
                    z2 = false;
                } else if (themeAccent != null) {
                    themeAccent.info = tL_theme2;
                } else {
                    themeInfo.info = tL_theme2;
                    themeInfo.loadThemeDocument();
                }
            }
        } else {
            z2 = false;
        }
        if (loadingCurrentTheme == 0) {
            lastLoadingCurrentThemeTime = (int) (System.currentTimeMillis() / 1000);
            saveOtherThemes(z2);
        }
    }

    public static void loadRemoteThemes(final int i, boolean z) {
        if (loadingRemoteThemes[i]) {
            return;
        }
        if ((z || Math.abs((System.currentTimeMillis() / 1000) - ((long) lastLoadingThemesTime[i])) >= 3600) && UserConfig.getInstance(i).isClientActivated()) {
            loadingRemoteThemes[i] = true;
            TL_account.getThemes getthemes = new TL_account.getThemes();
            getthemes.format = "android";
            if (!MediaDataController.getInstance(i).defaultEmojiThemes.isEmpty()) {
                getthemes.hash = remoteThemesHash[i];
            }
            if (BuildVars.LOGS_ENABLED) {
                Log.i("theme", "loading remote themes, hash " + getthemes.hash);
            }
            ConnectionsManager.getInstance(i).sendRequest(getthemes, new RequestDelegate() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda4
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda13
                        @Override // java.lang.Runnable
                        public final void run() {
                            Theme.$r8$lambda$QJ4M_NRBSxhPO8JEoHapVMMxNAI(i, tLObject);
                        }
                    });
                }
            });
        }
    }

    /* JADX WARN: Code duplicated, block: B:103:0x01f3 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:104:0x01f5  */
    /* JADX WARN: Code duplicated, block: B:106:0x01fa  */
    /* JADX WARN: Code duplicated, block: B:108:0x0201  */
    public static /* synthetic */ void $r8$lambda$QJ4M_NRBSxhPO8JEoHapVMMxNAI(int i, TLObject tLObject) {
        boolean z;
        ThemeInfo themeInfo;
        String baseThemeKey;
        ThemeInfo themeInfo2;
        TLRPC.WallPaperSettings wallPaperSettings;
        int i2 = 0;
        loadingRemoteThemes[i] = false;
        if (tLObject instanceof TL_account.TL_themes) {
            TL_account.TL_themes tL_themes = (TL_account.TL_themes) tLObject;
            remoteThemesHash[i] = tL_themes.hash;
            lastLoadingThemesTime[i] = (int) (System.currentTimeMillis() / 1000);
            ArrayList<TLRPC.TL_theme> arrayList = new ArrayList<>();
            ArrayList arrayList2 = new ArrayList();
            int size = themes.size();
            for (int i3 = 0; i3 < size; i3++) {
                ThemeInfo themeInfo3 = themes.get(i3);
                if (themeInfo3.info != null && themeInfo3.account == i) {
                    arrayList2.add(themeInfo3);
                } else if (themeInfo3.themeAccents != null) {
                    for (int i4 = 0; i4 < themeInfo3.themeAccents.size(); i4++) {
                        ThemeAccent themeAccent = themeInfo3.themeAccents.get(i4);
                        if (themeAccent.info != null && themeAccent.account == i) {
                            arrayList2.add(themeAccent);
                        }
                    }
                }
            }
            int size2 = tL_themes.themes.size();
            int i5 = 0;
            boolean z2 = false;
            boolean z3 = false;
            while (i5 < size2) {
                TLRPC.TL_theme tL_theme = tL_themes.themes.get(i5);
                if (tL_theme != null) {
                    if (tL_theme.isDefault) {
                        arrayList.add(tL_theme);
                    }
                    ArrayList<TLRPC.ThemeSettings> arrayList3 = tL_theme.settings;
                    if (arrayList3 != null && arrayList3.size() > 0) {
                        for (int i6 = i2; i6 < tL_theme.settings.size(); i6++) {
                            TLRPC.ThemeSettings themeSettings = tL_theme.settings.get(i6);
                            if (themeSettings != null && (baseThemeKey = getBaseThemeKey(themeSettings)) != null && (themeInfo2 = themesDict.get(baseThemeKey)) != null && themeInfo2.themeAccents != null) {
                                ArrayList arrayList4 = arrayList2;
                                ThemeAccent themeAccent2 = themeInfo2.accentsByThemeId.get(tL_theme.id);
                                if (themeAccent2 != null) {
                                    if (!ThemeInfo.accentEquals(themeAccent2, themeSettings)) {
                                        File pathToWallpaper = themeAccent2.getPathToWallpaper();
                                        if (pathToWallpaper != null) {
                                            pathToWallpaper.delete();
                                        }
                                        ThemeInfo.fillAccentValues(themeAccent2, themeSettings);
                                        ThemeInfo themeInfo4 = currentTheme;
                                        if (themeInfo4 == themeInfo2 && themeInfo4.currentAccentId == themeAccent2.id) {
                                            refreshThemeColors();
                                            NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                                            int i7 = NotificationCenter.needSetDayNightTheme;
                                            ThemeInfo themeInfo5 = currentTheme;
                                            globalInstance.lambda$postNotificationNameOnUIThread$1(i7, themeInfo5, Boolean.valueOf(currentNightTheme == themeInfo5), null, -1);
                                        }
                                        z2 = true;
                                        z3 = true;
                                    }
                                    TLRPC.WallPaper wallPaper = themeSettings.wallpaper;
                                    themeAccent2.patternMotion = (wallPaper == null || (wallPaperSettings = wallPaper.settings) == null || !wallPaperSettings.motion) ? false : true;
                                    arrayList2 = arrayList4;
                                    arrayList2.remove(themeAccent2);
                                } else {
                                    arrayList2 = arrayList4;
                                    ThemeAccent themeAccentCreateNewAccent = themeInfo2.createNewAccent(tL_theme, i, false, i6);
                                    if (TextUtils.isEmpty(themeAccentCreateNewAccent.patternSlug)) {
                                        themeAccent2 = themeAccentCreateNewAccent;
                                    } else {
                                        themeAccent2 = themeAccentCreateNewAccent;
                                        z2 = true;
                                    }
                                }
                                themeAccent2.isDefault = tL_theme.isDefault;
                            }
                        }
                    } else {
                        String str = "remote" + tL_theme.id;
                        ThemeInfo themeInfo6 = themesDict.get(str);
                        if (themeInfo6 == null) {
                            themeInfo6 = new ThemeInfo();
                            themeInfo6.account = i;
                            themeInfo6.pathToFile = new File(ApplicationLoader.getFilesDirFixed(), str.concat(".attheme")).getAbsolutePath();
                            themes.add(themeInfo6);
                            otherThemes.add(themeInfo6);
                            z3 = true;
                        } else {
                            arrayList2.remove(themeInfo6);
                        }
                        themeInfo6.name = tL_theme.title;
                        themeInfo6.info = tL_theme;
                        themesDict.put(themeInfo6.getKey(), themeInfo6);
                    }
                }
                i5++;
                i2 = 0;
            }
            int size3 = arrayList2.size();
            for (int i8 = 0; i8 < size3; i8++) {
                Object obj = arrayList2.get(i8);
                if (obj instanceof ThemeInfo) {
                    ThemeInfo themeInfo7 = (ThemeInfo) obj;
                    themeInfo7.removeObservers();
                    otherThemes.remove(themeInfo7);
                    themesDict.remove(themeInfo7.name);
                    OverrideWallpaperInfo overrideWallpaperInfo = themeInfo7.overrideWallpaper;
                    if (overrideWallpaperInfo != null) {
                        overrideWallpaperInfo.delete();
                    }
                    themes.remove(themeInfo7);
                    new File(themeInfo7.pathToFile).delete();
                    if (currentDayTheme == themeInfo7) {
                        currentDayTheme = defaultTheme;
                    } else {
                        if (currentNightTheme == themeInfo7) {
                            currentNightTheme = themesDict.get("Dark Blue");
                            z = true;
                        }
                        if (currentTheme != themeInfo7) {
                            if (z) {
                                themeInfo = currentNightTheme;
                            } else {
                                themeInfo = currentDayTheme;
                            }
                            applyTheme(themeInfo, true, false, z);
                        }
                    }
                    z = false;
                    if (currentTheme != themeInfo7) {
                        if (z) {
                            themeInfo = currentNightTheme;
                        } else {
                            themeInfo = currentDayTheme;
                        }
                        applyTheme(themeInfo, true, false, z);
                    }
                } else if (obj instanceof ThemeAccent) {
                    ThemeAccent themeAccent3 = (ThemeAccent) obj;
                    if (deleteThemeAccent(themeAccent3.parentTheme, themeAccent3, false) && currentTheme == themeAccent3.parentTheme) {
                        refreshThemeColors();
                        NotificationCenter globalInstance2 = NotificationCenter.getGlobalInstance();
                        int i9 = NotificationCenter.needSetDayNightTheme;
                        ThemeInfo themeInfo8 = currentTheme;
                        globalInstance2.lambda$postNotificationNameOnUIThread$1(i9, themeInfo8, Boolean.valueOf(currentNightTheme == themeInfo8), null, -1);
                    }
                }
            }
            saveOtherThemes(true);
            sortThemes();
            if (z3) {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeListUpdated, new Object[0]);
            }
            if (z2) {
                PatternsLoader.createLoader(true);
            }
            MediaDataController.getInstance(i).generateEmojiPreviewThemes(arrayList, i);
        }
    }

    public static String getBaseThemeKey(TLRPC.ThemeSettings themeSettings) {
        if (themeSettings == null) {
            return null;
        }
        TLRPC.BaseTheme baseTheme = themeSettings.base_theme;
        if (baseTheme instanceof TLRPC.TL_baseThemeClassic) {
            return "Blue";
        }
        if (baseTheme instanceof TLRPC.TL_baseThemeDay) {
            return "Day";
        }
        if (baseTheme instanceof TLRPC.TL_baseThemeTinted) {
            return "Dark Blue";
        }
        if (baseTheme instanceof TLRPC.TL_baseThemeArctic) {
            return "Arctic Blue";
        }
        if (baseTheme instanceof TLRPC.TL_baseThemeNight) {
            return "Night";
        }
        return null;
    }

    public static TLRPC.BaseTheme getBaseThemeByKey(String str) {
        if ("Blue".equals(str)) {
            return new TLRPC.TL_baseThemeClassic();
        }
        if ("Day".equals(str)) {
            return new TLRPC.TL_baseThemeDay();
        }
        if ("Dark Blue".equals(str)) {
            return new TLRPC.TL_baseThemeTinted();
        }
        if ("Arctic Blue".equals(str)) {
            return new TLRPC.TL_baseThemeArctic();
        }
        if ("Night".equals(str)) {
            return new TLRPC.TL_baseThemeNight();
        }
        return null;
    }

    public static void setThemeFileReference(TLRPC.TL_theme tL_theme) {
        TLRPC.Document document;
        int size = themes.size();
        for (int i = 0; i < size; i++) {
            TLRPC.TL_theme tL_theme2 = themes.get(i).info;
            if (tL_theme2 != null && tL_theme2.id == tL_theme.id) {
                TLRPC.Document document2 = tL_theme2.document;
                if (document2 == null || (document = tL_theme.document) == null) {
                    return;
                }
                document2.file_reference = document.file_reference;
                saveOtherThemes(true);
                return;
            }
        }
    }

    public static boolean isThemeInstalled(ThemeInfo themeInfo) {
        return (themeInfo == null || themesDict.get(themeInfo.getKey()) == null) ? false : true;
    }

    public static void setThemeUploadInfo(ThemeInfo themeInfo, ThemeAccent themeAccent, TLRPC.TL_theme tL_theme, int i, boolean z) {
        String key;
        TLRPC.WallPaperSettings wallPaperSettings;
        if (tL_theme == null) {
            return;
        }
        TLRPC.ThemeSettings themeSettings = tL_theme.settings.size() > 0 ? tL_theme.settings.get(0) : null;
        if (themeSettings != null) {
            if (themeInfo == null) {
                String baseThemeKey = getBaseThemeKey(themeSettings);
                if (baseThemeKey == null || (themeInfo = themesDict.get(baseThemeKey)) == null) {
                    return;
                } else {
                    themeAccent = themeInfo.accentsByThemeId.get(tL_theme.id);
                }
            }
            if (themeAccent == null) {
                return;
            }
            TLRPC.TL_theme tL_theme2 = themeAccent.info;
            if (tL_theme2 != null) {
                themeInfo.accentsByThemeId.remove(tL_theme2.id);
            }
            themeAccent.info = tL_theme;
            themeAccent.account = i;
            themeInfo.accentsByThemeId.put(tL_theme.id, themeAccent);
            if (!ThemeInfo.accentEquals(themeAccent, themeSettings)) {
                File pathToWallpaper = themeAccent.getPathToWallpaper();
                if (pathToWallpaper != null) {
                    pathToWallpaper.delete();
                }
                ThemeInfo.fillAccentValues(themeAccent, themeSettings);
                ThemeInfo themeInfo2 = currentTheme;
                if (themeInfo2 == themeInfo && themeInfo2.currentAccentId == themeAccent.id) {
                    refreshThemeColors();
                    NotificationCenter globalInstance = NotificationCenter.getGlobalInstance();
                    int i2 = NotificationCenter.needSetDayNightTheme;
                    ThemeInfo themeInfo3 = currentTheme;
                    globalInstance.lambda$postNotificationNameOnUIThread$1(i2, themeInfo3, Boolean.valueOf(currentNightTheme == themeInfo3), null, -1);
                }
                PatternsLoader.createLoader(true);
            }
            TLRPC.WallPaper wallPaper = themeSettings.wallpaper;
            themeAccent.patternMotion = (wallPaper == null || (wallPaperSettings = wallPaper.settings) == null || !wallPaperSettings.motion) ? false : true;
            themeInfo.previewParsed = false;
        } else {
            if (themeInfo != null) {
                HashMap<String, ThemeInfo> map = themesDict;
                key = themeInfo.getKey();
                map.remove(key);
            } else {
                HashMap<String, ThemeInfo> map2 = themesDict;
                key = "remote" + tL_theme.id;
                themeInfo = map2.get(key);
            }
            if (themeInfo == null) {
                return;
            }
            themeInfo.info = tL_theme;
            themeInfo.name = tL_theme.title;
            File file = new File(themeInfo.pathToFile);
            File file2 = new File(ApplicationLoader.getFilesDirFixed(), key + ".attheme");
            if (!file.equals(file2)) {
                try {
                    AndroidUtilities.copyFile(file, file2);
                    themeInfo.pathToFile = file2.getAbsolutePath();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            if (z) {
                themeInfo.loadThemeDocument();
            } else {
                themeInfo.previewParsed = false;
            }
            themesDict.put(themeInfo.getKey(), themeInfo);
        }
        saveOtherThemes(true);
    }

    public static File getAssetFile(String str) {
        long jAvailable;
        File file = new File(ApplicationLoader.getFilesDirFixed(), str);
        try {
            InputStream inputStreamOpen = ApplicationLoader.applicationContext.getAssets().open(str);
            jAvailable = inputStreamOpen.available();
            inputStreamOpen.close();
        } catch (Exception e) {
            FileLog.e(e);
            jAvailable = 0;
        }
        if (!file.exists() || (jAvailable != 0 && file.length() != jAvailable)) {
            try {
                InputStream inputStreamOpen2 = ApplicationLoader.applicationContext.getAssets().open(str);
                try {
                    AndroidUtilities.copyFile(inputStreamOpen2, file);
                    if (inputStreamOpen2 != null) {
                        inputStreamOpen2.close();
                    }
                } catch (Throwable th) {
                    if (inputStreamOpen2 != null) {
                        try {
                            inputStreamOpen2.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (Exception e2) {
                FileLog.e(e2);
            }
        }
        return file;
    }

    public static int getPreviewColor(SparseIntArray sparseIntArray, int i) {
        int iIndexOfKey = sparseIntArray.indexOfKey(i);
        if (iIndexOfKey >= 0) {
            return sparseIntArray.valueAt(iIndexOfKey);
        }
        return defaultColors[i];
    }

    /* JADX WARN: Failed to calculate best type for var: r10v12 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r10v12 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r10v13 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r10v13 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r8v11 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r8v11 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r8v12 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r8v12 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r9v10 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r9v10 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r9v9 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r9v9 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:159)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:136)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:241)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r9v9 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r9v9 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /*  JADX ERROR: Types fix failed
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r9v9 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryPossibleTypes(FixTypesVisitor.java:186)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:245)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
        Caused by: java.lang.NullPointerException
        */
    public static java.lang.String createThemePreviewImage(java.lang.String r35, java.lang.String r36, org.telegram.ui.ActionBar.Theme.ThemeAccent r37) {
        /*
            Method dump skipped, instruction units count: 1694
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createThemePreviewImage(java.lang.String, java.lang.String, org.telegram.ui.ActionBar.Theme$ThemeAccent):java.lang.String");
    }

    public static void checkIsDark(SparseIntArray sparseIntArray, ThemeInfo themeInfo) {
        if (themeInfo == null || sparseIntArray == null || themeInfo.isDark != -1) {
            return;
        }
        int i = key_windowBackgroundWhite;
        if (ColorUtils.calculateLuminance(ColorUtils.blendARGB(getPreviewColor(sparseIntArray, i), getPreviewColor(sparseIntArray, i), 0.5f)) < 0.5d) {
            themeInfo.isDark = 1;
        } else {
            themeInfo.isDark = 0;
        }
    }

    public static void getThemeFileValuesInBackground(final File file, final String str, final String[] strArr, final Utilities.Callback<SparseIntArray> callback) {
        Utilities.themeQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                callback.run(Theme.getThemeFileValues(file, str, strArr));
            }
        });
    }

    /* JADX WARN: Code duplicated, block: B:71:0x0115 A[Catch: Exception -> 0x010b, TRY_ENTER, TRY_LEAVE, TryCatch #3 {Exception -> 0x010b, blocks: (B:65:0x0107, B:71:0x0115), top: B:88:0x000a }] */
    public static SparseIntArray getThemeFileValues(File file, String str, String[] strArr) {
        int iIntValue;
        SparseIntArray sparseIntArray = new SparseIntArray();
        FileInputStream fileInputStream = null;
        try {
            try {
                byte[] bArr = new byte[1024];
                FileInputStream fileInputStream2 = new FileInputStream(str != null ? getAssetFile(str) : file);
                int i = -1;
                int i2 = 0;
                int i3 = -1;
                int i4 = 0;
                boolean z = false;
                while (true) {
                    try {
                        int i5 = fileInputStream2.read(bArr);
                        if (i5 == i) {
                            break;
                        }
                        int i6 = i2;
                        int i7 = i6;
                        int i8 = i4;
                        while (i6 < i5) {
                            if (bArr[i6] == 10) {
                                int i9 = i6 - i7;
                                int i10 = i9 + 1;
                                String str2 = new String(bArr, i7, i9);
                                if (str2.startsWith("WLS=")) {
                                    if (strArr != null && strArr.length > 0) {
                                        strArr[i2] = str2.substring(4);
                                    }
                                } else {
                                    if (str2.startsWith("WPS")) {
                                        i3 = i8 + i10;
                                        z = true;
                                        break;
                                    }
                                    int iIndexOf = str2.indexOf(61);
                                    if (iIndexOf != i) {
                                        String strSubstring = str2.substring(i2, iIndexOf);
                                        String strSubstring2 = str2.substring(iIndexOf + 1);
                                        boolean zEndsWith = strSubstring2.trim().endsWith("h");
                                        if (zEndsWith) {
                                            strSubstring2 = strSubstring2.substring(i2, strSubstring2.length() - 1);
                                        }
                                        if (strSubstring2.startsWith("#")) {
                                            try {
                                                iIntValue = Color.parseColor(strSubstring2);
                                            } catch (Exception unused) {
                                                iIntValue = Utilities.parseInt((CharSequence) strSubstring2).intValue();
                                            }
                                        } else if (Build.VERSION.SDK_INT >= 31 && (strSubstring2.startsWith("a") || strSubstring2.startsWith("n") || strSubstring2.startsWith("m"))) {
                                            iIntValue = MonetUtils.getColor(strSubstring2.trim());
                                        } else {
                                            iIntValue = Utilities.parseInt((CharSequence) strSubstring2).intValue();
                                        }
                                        if (Build.VERSION.SDK_INT >= 31 && zEndsWith) {
                                            iIntValue = MonetUtils.harmonize(iIntValue);
                                        }
                                        int iStringKeyToInt = ThemeColors.stringKeyToInt(strSubstring);
                                        if (iStringKeyToInt >= 0) {
                                            sparseIntArray.put(iStringKeyToInt, iIntValue);
                                        }
                                    }
                                    th = th;
                                    fileInputStream = fileInputStream2;
                                    try {
                                        FileLog.e(th);
                                        if (fileInputStream != null) {
                                            fileInputStream.close();
                                        }
                                        return sparseIntArray;
                                    } catch (Throwable th) {
                                        if (fileInputStream != null) {
                                            try {
                                                fileInputStream.close();
                                                throw th;
                                            } catch (Exception e) {
                                                FileLog.e(e);
                                                throw th;
                                            }
                                        }
                                        throw th;
                                    }
                                }
                                i7 += i10;
                                i8 += i10;
                            }
                            i6++;
                            i = -1;
                            i2 = 0;
                        }
                        if (i4 == i8) {
                            break;
                        }
                        fileInputStream2.getChannel().position(i8);
                        if (z) {
                            break;
                        }
                        i4 = i8;
                        i = -1;
                        i2 = 0;
                    } catch (Throwable th2) {
                        th = th2;
                        fileInputStream = fileInputStream2;
                        FileLog.e(th);
                        if (fileInputStream != null) {
                            fileInputStream.close();
                        }
                        return sparseIntArray;
                    }
                }
                sparseIntArray.put(key_wallpaperFileOffset, i3);
                fileInputStream2.close();
            } catch (Throwable th3) {
                th = th3;
            }
        } catch (Exception e2) {
            FileLog.e(e2);
        }
        return sparseIntArray;
    }

    public static void createCommonResources(Context context) {
        if (dividerPaint == null) {
            Paint paint = new Paint();
            dividerPaint = paint;
            paint.setStrokeWidth(1.0f);
            Paint paint2 = new Paint();
            dividerExtraPaint = paint2;
            paint2.setStrokeWidth(1.0f);
            Paint paint3 = new Paint();
            forcedDividerPaint = paint3;
            paint3.setStrokeWidth(1.0f);
            avatar_backgroundPaint = new Paint(1);
            Paint paint4 = new Paint(1);
            checkboxSquare_checkPaint = paint4;
            paint4.setStyle(Paint.Style.STROKE);
            checkboxSquare_checkPaint.setStrokeWidth(AndroidUtilities.dp(2.0f));
            checkboxSquare_checkPaint.setStrokeCap(Paint.Cap.ROUND);
            Paint paint5 = new Paint(1);
            checkboxSquare_eraserPaint = paint5;
            paint5.setColor(0);
            checkboxSquare_eraserPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            checkboxSquare_backgroundPaint = new Paint(1);
            Paint paint6 = new Paint();
            linkSelectionPaint = paint6;
            paint6.setPathEffect(LinkPath.getRoundedEffect());
            Resources resources = context.getResources();
            avatarDrawables[0] = resources.getDrawable(R.drawable.chats_saved);
            avatarDrawables[1] = resources.getDrawable(R.drawable.ghost);
            avatarDrawables[2] = resources.getDrawable(R.drawable.msg_folders_private);
            avatarDrawables[3] = resources.getDrawable(R.drawable.msg_folders_requests);
            avatarDrawables[4] = resources.getDrawable(R.drawable.msg_folders_groups);
            avatarDrawables[5] = resources.getDrawable(R.drawable.msg_folders_channels);
            avatarDrawables[6] = resources.getDrawable(R.drawable.msg_folders_bots);
            avatarDrawables[7] = resources.getDrawable(R.drawable.msg_folders_muted);
            avatarDrawables[8] = resources.getDrawable(R.drawable.msg_folders_read);
            avatarDrawables[9] = resources.getDrawable(R.drawable.msg_folders_archive);
            avatarDrawables[10] = resources.getDrawable(R.drawable.msg_folders_private);
            avatarDrawables[11] = resources.getDrawable(R.drawable.chats_replies);
            avatarDrawables[12] = resources.getDrawable(R.drawable.other_chats);
            avatarDrawables[13] = resources.getDrawable(R.drawable.msg_stories_closefriends);
            avatarDrawables[14] = resources.getDrawable(R.drawable.filled_gift_premium);
            avatarDrawables[15] = resources.getDrawable(R.drawable.filled_unknown);
            avatarDrawables[16] = resources.getDrawable(R.drawable.filled_unclaimed);
            avatarDrawables[17] = resources.getDrawable(R.drawable.large_repost_story);
            avatarDrawables[18] = resources.getDrawable(R.drawable.large_hidden);
            avatarDrawables[19] = resources.getDrawable(R.drawable.large_notes);
            avatarDrawables[20] = resources.getDrawable(R.drawable.filled_folder_new);
            avatarDrawables[21] = resources.getDrawable(R.drawable.filled_folder_existing);
            avatarDrawables[22] = resources.getDrawable(R.drawable.filled_giveaway_premium);
            avatarDrawables[23] = resources.getDrawable(R.drawable.filled_giveaway_stars);
            avatarDrawables[24] = resources.getDrawable(R.drawable.filled_suggest_chat_avatar);
            avatarDrawables[25] = resources.getDrawable(R.drawable.ic_feed_filled);
            RLottieDrawable rLottieDrawable = dialogs_archiveAvatarDrawable;
            if (rLottieDrawable != null) {
                rLottieDrawable.setCallback(null);
                dialogs_archiveAvatarDrawable.recycle(false);
            }
            RLottieDrawable rLottieDrawable2 = dialogs_archiveDrawable;
            if (rLottieDrawable2 != null) {
                rLottieDrawable2.recycle(false);
            }
            RLottieDrawable rLottieDrawable3 = dialogs_unarchiveDrawable;
            if (rLottieDrawable3 != null) {
                rLottieDrawable3.recycle(false);
            }
            RLottieDrawable rLottieDrawable4 = dialogs_pinArchiveDrawable;
            if (rLottieDrawable4 != null) {
                rLottieDrawable4.recycle(false);
            }
            RLottieDrawable rLottieDrawable5 = dialogs_unpinArchiveDrawable;
            if (rLottieDrawable5 != null) {
                rLottieDrawable5.recycle(false);
            }
            RLottieDrawable rLottieDrawable6 = dialogs_hidePsaDrawable;
            if (rLottieDrawable6 != null) {
                rLottieDrawable6.recycle(false);
            }
            dialogs_archiveAvatarDrawable = new RLottieDrawable(R.raw.chats_archiveavatar, "chats_archiveavatar", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, null);
            dialogs_archiveDrawable = new RLottieDrawable(R.raw.chats_archive, "chats_archive", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, null);
            dialogs_unarchiveDrawable = new RLottieDrawable(R.raw.chats_unarchive, "chats_unarchive", AndroidUtilities.dp(AndroidUtilities.dp(36.0f)), AndroidUtilities.dp(36.0f), false, null);
            dialogs_pinArchiveDrawable = new RLottieDrawable(R.raw.chats_hide, "chats_hide", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, null);
            dialogs_unpinArchiveDrawable = new RLottieDrawable(R.raw.chats_unhide, "chats_unhide", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, null);
            dialogs_hidePsaDrawable = new RLottieDrawable(R.raw.chat_audio_record_delete, "chats_psahide", AndroidUtilities.dp(30.0f), AndroidUtilities.dp(30.0f), false, null);
            dialogs_swipeMuteDrawable = new RLottieDrawable(R.raw.swipe_mute, "swipe_mute", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, null);
            dialogs_swipeUnmuteDrawable = new RLottieDrawable(R.raw.swipe_unmute, "swipe_unmute", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, null);
            dialogs_swipeReadDrawable = new RLottieDrawable(R.raw.swipe_read, "swipe_read", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, null);
            dialogs_swipeUnreadDrawable = new RLottieDrawable(R.raw.swipe_unread, "swipe_unread", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, null);
            dialogs_swipeDeleteDrawable = new RLottieDrawable(R.raw.swipe_delete, "swipe_delete", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, null);
            dialogs_swipeUnpinDrawable = new RLottieDrawable(R.raw.swipe_unpin, "swipe_unpin", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, null);
            dialogs_swipePinDrawable = new RLottieDrawable(R.raw.swipe_pin, "swipe_pin", AndroidUtilities.dp(36.0f), AndroidUtilities.dp(36.0f), false, null);
            dialogs_swipeCommunityUngroup = new RLottieDrawable(R.raw.swipe_community_ungroup, "swipe_community_ungroup", AndroidUtilities.dp(28.0f), AndroidUtilities.dp(28.0f), false, null);
            applyCommonTheme();
        }
    }

    public static void applyCommonTheme() {
        Paint paint = dividerPaint;
        if (paint == null) {
            return;
        }
        paint.setColor(getColor(key_divider));
        forcedDividerPaint.setColor(getDividerColor(null));
        linkSelectionPaint.setColor(getColor(key_windowBackgroundWhiteLinkSelection));
        for (Drawable drawable : avatarDrawables) {
            setDrawableColorByKey(drawable, key_avatar_text);
        }
        dialogs_archiveAvatarDrawable.beginApplyLayerColors();
        RLottieDrawable rLottieDrawable = dialogs_archiveAvatarDrawable;
        int i = key_avatar_backgroundArchived;
        rLottieDrawable.setLayerColor("Arrow1.**", getNonAnimatedColor(i));
        dialogs_archiveAvatarDrawable.setLayerColor("Arrow2.**", getNonAnimatedColor(i));
        RLottieDrawable rLottieDrawable2 = dialogs_archiveAvatarDrawable;
        int i2 = key_avatar_text;
        rLottieDrawable2.setLayerColor("Box2.**", getNonAnimatedColor(i2));
        dialogs_archiveAvatarDrawable.setLayerColor("Box1.**", getNonAnimatedColor(i2));
        dialogs_archiveAvatarDrawable.commitApplyLayerColors();
        dialogs_archiveAvatarDrawableRecolored = false;
        dialogs_archiveAvatarDrawable.setAllowDecodeSingleFrame(true);
        dialogs_pinArchiveDrawable.beginApplyLayerColors();
        RLottieDrawable rLottieDrawable3 = dialogs_pinArchiveDrawable;
        int i3 = key_chats_archiveIcon;
        rLottieDrawable3.setLayerColor("Arrow.**", getNonAnimatedColor(i3));
        dialogs_pinArchiveDrawable.setLayerColor("Line.**", getNonAnimatedColor(i3));
        dialogs_pinArchiveDrawable.commitApplyLayerColors();
        dialogs_unpinArchiveDrawable.beginApplyLayerColors();
        dialogs_unpinArchiveDrawable.setLayerColor("Arrow.**", getNonAnimatedColor(i3));
        dialogs_unpinArchiveDrawable.setLayerColor("Line.**", getNonAnimatedColor(i3));
        dialogs_unpinArchiveDrawable.commitApplyLayerColors();
        dialogs_hidePsaDrawable.beginApplyLayerColors();
        RLottieDrawable rLottieDrawable4 = dialogs_hidePsaDrawable;
        int i4 = key_chats_archiveBackground;
        rLottieDrawable4.setLayerColor("Line 1.**", getNonAnimatedColor(i4));
        dialogs_hidePsaDrawable.setLayerColor("Line 2.**", getNonAnimatedColor(i4));
        dialogs_hidePsaDrawable.setLayerColor("Line 3.**", getNonAnimatedColor(i4));
        dialogs_hidePsaDrawable.setLayerColor("Cup Red.**", getNonAnimatedColor(i3));
        dialogs_hidePsaDrawable.setLayerColor("Box.**", getNonAnimatedColor(i3));
        dialogs_hidePsaDrawable.commitApplyLayerColors();
        dialogs_hidePsaDrawableRecolored = false;
        dialogs_archiveDrawable.beginApplyLayerColors();
        dialogs_archiveDrawable.setLayerColor("Arrow.**", getNonAnimatedColor(i4));
        dialogs_archiveDrawable.setLayerColor("Box2.**", getNonAnimatedColor(i3));
        dialogs_archiveDrawable.setLayerColor("Box1.**", getNonAnimatedColor(i3));
        dialogs_archiveDrawable.commitApplyLayerColors();
        dialogs_archiveDrawableRecolored = false;
        dialogs_unarchiveDrawable.beginApplyLayerColors();
        dialogs_unarchiveDrawable.setLayerColor("Arrow1.**", getNonAnimatedColor(i3));
        dialogs_unarchiveDrawable.setLayerColor("Arrow2.**", getNonAnimatedColor(key_chats_archivePinBackground));
        dialogs_unarchiveDrawable.setLayerColor("Box2.**", getNonAnimatedColor(i3));
        dialogs_unarchiveDrawable.setLayerColor("Box1.**", getNonAnimatedColor(i3));
        dialogs_unarchiveDrawable.commitApplyLayerColors();
        int color = getColor(key_windowBackgroundWhiteBlackText);
        PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
        chat_animatedEmojiTextColorFilter = new PorterDuffColorFilter(color, mode);
        chat_outAnimatedEmojiTextColorFilter = new PorterDuffColorFilter(getColor(key_chat_messageTextOut), mode);
        PremiumGradient.getInstance().checkIconColors();
    }

    public static void createCommonDialogResources(Context context) {
        if (dialogs_countTextPaint == null) {
            TextPaint textPaint = new TextPaint(1);
            dialogs_countTextPaint = textPaint;
            textPaint.setTypeface(AndroidUtilities.bold());
            TextPaint textPaint2 = new TextPaint(1);
            dialogs_countTextPaint2 = textPaint2;
            textPaint2.setTypeface(AndroidUtilities.bold());
            dialogs_countPaint = new Paint(1);
            dialogs_onlineCirclePaint = new Paint(1);
            dialogs_tagPaint = new Paint(1);
        }
        dialogs_countTextPaint.setTextSize(AndroidUtilities.dp(12.0f));
        dialogs_countTextPaint2.setTextSize(AndroidUtilities.dp(13.0f));
    }

    public static void createDialogsResources(Context context) {
        createCommonResources(context);
        createCommonDialogResources(context);
        if (dialogs_namePaint == null) {
            Resources resources = context.getResources();
            dialogs_namePaint = new TextPaint[2];
            dialogs_nameEncryptedPaint = new TextPaint[2];
            dialogs_messagePaint = new TextPaint[2];
            dialogs_messagePrintingPaint = new TextPaint[2];
            for (int i = 0; i < 2; i++) {
                dialogs_namePaint[i] = new TextPaint(1);
                dialogs_namePaint[i].setTypeface(AndroidUtilities.bold());
                dialogs_nameEncryptedPaint[i] = new TextPaint(1);
                dialogs_nameEncryptedPaint[i].setTypeface(AndroidUtilities.bold());
                dialogs_messagePaint[i] = new TextPaint(1);
                dialogs_messagePrintingPaint[i] = new TextPaint(1);
            }
            TextPaint textPaint = new TextPaint(1);
            dialogs_searchNamePaint = textPaint;
            textPaint.setTypeface(AndroidUtilities.bold());
            TextPaint textPaint2 = new TextPaint(1);
            dialogs_searchNameEncryptedPaint = textPaint2;
            textPaint2.setTypeface(AndroidUtilities.bold());
            TextPaint textPaint3 = new TextPaint(1);
            dialogs_messageNamePaint = textPaint3;
            textPaint3.setTypeface(AndroidUtilities.bold());
            dialogs_timePaint = new TextPaint(1);
            dialogs_timePaintBold = new TextPaint(1);
            dialogs_timePaintBoldAccent = new TextPaint(1);
            TextPaint textPaint4 = new TextPaint(1);
            dialogs_archiveTextPaint = textPaint4;
            textPaint4.setTypeface(AndroidUtilities.bold());
            TextPaint textPaint5 = new TextPaint(1);
            dialogs_archiveTextPaintSmall = textPaint5;
            textPaint5.setTypeface(AndroidUtilities.bold());
            dialogs_onlinePaint = new TextPaint(1);
            dialogs_offlinePaint = new TextPaint(1);
            TextPaint textPaint6 = new TextPaint(1);
            dialogs_tagTextPaint = textPaint6;
            textPaint6.setTypeface(AndroidUtilities.bold());
            dialogs_tabletSeletedPaint = new Paint();
            dialogs_pinnedPaint = new Paint(1);
            dialogs_countGrayPaint = new Paint(1);
            dialogs_errorPaint = new Paint(1);
            dialogs_actionMessagePaint = new Paint(1);
            dialogs_lockDrawable = resources.getDrawable(R.drawable.list_secret);
            dialogs_lock2Drawable = resources.getDrawable(R.drawable.msg_mini_lock2);
            dialogs_checkDrawable = resources.getDrawable(R.drawable.list_check).mutate();
            dialogs_communityCardsDrawable = resources.getDrawable(R.drawable.community_cards).mutate();
            dialogs_playDrawable = resources.getDrawable(R.drawable.minithumb_play).mutate();
            dialogs_checkReadDrawable = resources.getDrawable(R.drawable.list_check).mutate();
            dialogs_halfCheckDrawable = resources.getDrawable(R.drawable.list_halfcheck);
            dialogs_clockDrawable = new MsgClockDrawable();
            dialogs_errorDrawable = resources.getDrawable(R.drawable.list_warning_sign);
            dialogs_reorderDrawable = resources.getDrawable(R.drawable.list_reorder).mutate();
            dialogs_muteDrawable = resources.getDrawable(R.drawable.list_mute).mutate();
            dialogs_unmuteDrawable = resources.getDrawable(R.drawable.list_unmute).mutate();
            dialogs_hiddenDrawable = resources.getDrawable(R.drawable.mini_ephemeral_hidden_16).mutate();
            dialogs_verifiedDrawable = resources.getDrawable(R.drawable.verified_area).mutate();
            dialogs_holidayDrawable = resources.getDrawable(R.drawable.newyear).mutate();
            dialogs_scamDrawable = new ScamDrawable(11, 0);
            dialogs_fakeDrawable = new ScamDrawable(11, 1);
            dialogs_verifiedCheckDrawable = resources.getDrawable(R.drawable.verified_check).mutate();
            dialogs_mentionDrawable = resources.getDrawable(R.drawable.filled_chatlist_mention).mutate();
            dialogs_reactionsMentionDrawable = resources.getDrawable(R.drawable.filled_chatlist_reaction).mutate();
            dialogs_pollMentionDrawable = resources.getDrawable(R.drawable.filled_chatlist_poll).mutate();
            dialogs_mentionDrawableMuted = resources.getDrawable(R.drawable.filled_chatlist_mention).mutate();
            dialogs_reactionsMentionDrawableMuted = resources.getDrawable(R.drawable.filled_chatlist_reaction).mutate();
            dialogs_pollMentionDrawableMuted = resources.getDrawable(R.drawable.filled_chatlist_poll).mutate();
            dialogs_pinnedDrawable = resources.getDrawable(R.drawable.list_pin);
            dialogs_pinnedDrawable2 = resources.getDrawable(R.drawable.msg_pin_mini).mutate();
            dialogs_pinnedDrawable2Accent = resources.getDrawable(R.drawable.msg_pin_mini).mutate();
            dialogs_forum_arrowDrawable = resources.getDrawable(R.drawable.msg_mini_forumarrow);
            moveUpDrawable = resources.getDrawable(R.drawable.preview_arrow);
            RectF rectF = new RectF();
            chat_updatePath[0] = new Path();
            chat_updatePath[2] = new Path();
            float fDp = AndroidUtilities.dp(12.0f);
            float fDp2 = AndroidUtilities.dp(12.0f);
            rectF.set(fDp - AndroidUtilities.dp(5.0f), fDp2 - AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f) + fDp, AndroidUtilities.dp(5.0f) + fDp2);
            chat_updatePath[2].arcTo(rectF, -160.0f, -110.0f, true);
            chat_updatePath[2].arcTo(rectF, 20.0f, -110.0f, true);
            chat_updatePath[0].moveTo(fDp, AndroidUtilities.dp(8.0f) + fDp2);
            chat_updatePath[0].lineTo(fDp, AndroidUtilities.dp(2.0f) + fDp2);
            chat_updatePath[0].lineTo(AndroidUtilities.dp(3.0f) + fDp, AndroidUtilities.dp(5.0f) + fDp2);
            chat_updatePath[0].close();
            chat_updatePath[0].moveTo(fDp, fDp2 - AndroidUtilities.dp(8.0f));
            chat_updatePath[0].lineTo(fDp, fDp2 - AndroidUtilities.dp(2.0f));
            chat_updatePath[0].lineTo(fDp - AndroidUtilities.dp(3.0f), fDp2 - AndroidUtilities.dp(5.0f));
            chat_updatePath[0].close();
            applyDialogsTheme();
        }
        dialogs_messageNamePaint.setTextSize(AndroidUtilities.dp(14.0f));
        dialogs_timePaint.setTextSize(AndroidUtilities.dp(12.0f));
        dialogs_timePaintBold.setTextSize(AndroidUtilities.dp(12.0f));
        dialogs_timePaintBold.setTypeface(AndroidUtilities.bold());
        dialogs_timePaintBoldAccent.setTextSize(AndroidUtilities.dp(12.0f));
        dialogs_timePaintBoldAccent.setTypeface(AndroidUtilities.bold());
        dialogs_archiveTextPaint.setTextSize(AndroidUtilities.dp(13.0f));
        dialogs_archiveTextPaintSmall.setTextSize(AndroidUtilities.dp(11.0f));
        dialogs_onlinePaint.setTextSize(AndroidUtilities.dp(15.0f));
        dialogs_offlinePaint.setTextSize(AndroidUtilities.dp(15.0f));
        dialogs_tagTextPaint.setTextSize(AndroidUtilities.dp(10.0f));
        dialogs_searchNamePaint.setTextSize(AndroidUtilities.dp(16.0f));
        dialogs_searchNameEncryptedPaint.setTextSize(AndroidUtilities.dp(16.0f));
    }

    public static void applyDialogsTheme() {
        if (dialogs_namePaint == null) {
            return;
        }
        for (int i = 0; i < 2; i++) {
            dialogs_namePaint[i].setColor(getColor(key_chats_name));
            dialogs_nameEncryptedPaint[i].setColor(getColor(key_chats_secretName));
            TextPaint textPaint = dialogs_messagePaint[i];
            int color = getColor(key_chats_message);
            ((android.text.TextPaint) textPaint).linkColor = color;
            textPaint.setColor(color);
            dialogs_messagePrintingPaint[i].setColor(getColor(key_chats_actionMessage));
        }
        dialogs_searchNamePaint.setColor(getColor(key_chats_name));
        dialogs_searchNameEncryptedPaint.setColor(getColor(key_chats_secretName));
        TextPaint textPaint2 = dialogs_messageNamePaint;
        int color2 = getColor(key_chats_nameMessage_threeLines);
        ((android.text.TextPaint) textPaint2).linkColor = color2;
        textPaint2.setColor(color2);
        dialogs_tabletSeletedPaint.setColor(getColor(key_chats_tabletSelectedOverlay));
        dialogs_pinnedPaint.setColor(getColor(key_chats_pinnedOverlay));
        dialogs_timePaint.setColor(getColor(key_chats_date));
        dialogs_timePaintBold.setColor(getColor(key_chats_date_bold));
        TextPaint textPaint3 = dialogs_timePaintBoldAccent;
        int i2 = key_telegram_color_text;
        textPaint3.setColor(getColor(i2));
        TextPaint textPaint4 = dialogs_countTextPaint;
        int i3 = key_chats_unreadCounterText;
        textPaint4.setColor(getColor(i3));
        dialogs_countTextPaint2.setColor(getColor(i3));
        TextPaint textPaint5 = dialogs_archiveTextPaint;
        int i4 = key_chats_archiveText;
        textPaint5.setColor(getColor(i4));
        dialogs_archiveTextPaintSmall.setColor(getColor(i4));
        Paint paint = dialogs_countPaint;
        int i5 = key_chats_unreadCounter;
        paint.setColor(getColor(i5));
        Paint paint2 = dialogs_countGrayPaint;
        int i6 = key_chats_unreadCounterMuted;
        paint2.setColor(getColor(i6));
        dialogs_actionMessagePaint.setColor(getColor(key_chats_actionMessage));
        dialogs_errorPaint.setColor(getColor(key_chats_sentError));
        dialogs_onlinePaint.setColor(getColor(key_windowBackgroundWhiteBlueText3));
        dialogs_offlinePaint.setColor(getColor(key_windowBackgroundWhiteGrayText3));
        setDrawableColorByKey(dialogs_lockDrawable, key_chats_secretIcon);
        Drawable drawable = dialogs_lock2Drawable;
        int i7 = key_chats_pinnedIcon;
        setDrawableColorByKey(drawable, i7);
        setDrawableColorByKey(dialogs_checkDrawable, key_chats_sentCheck);
        setDrawableColorByKey(dialogs_communityCardsDrawable, key_windowBackgroundWhiteBlackText);
        Drawable drawable2 = dialogs_checkReadDrawable;
        int i8 = key_chats_sentReadCheck;
        setDrawableColorByKey(drawable2, i8);
        setDrawableColorByKey(dialogs_halfCheckDrawable, i8);
        setDrawableColorByKey(dialogs_clockDrawable, key_chats_sentClock);
        setDrawableColorByKey(dialogs_errorDrawable, key_chats_sentErrorIcon);
        setDrawableColorByKey(dialogs_pinnedDrawable, i7);
        setDrawableColorByKey(dialogs_pinnedDrawable2, i7);
        setDrawableColorByKey(dialogs_pinnedDrawable2Accent, i2);
        setDrawableColorByKey(dialogs_reorderDrawable, i7);
        Drawable drawable3 = dialogs_muteDrawable;
        int i9 = key_chats_muteIcon;
        setDrawableColorByKey(drawable3, i9);
        setDrawableColorByKey(dialogs_unmuteDrawable, i9);
        setDrawableColorByKey(dialogs_hiddenDrawable, i9);
        setDrawableColorByKey(dialogs_mentionDrawable, i5);
        setDrawableColorByKey(dialogs_reactionsMentionDrawable, key_dialogReactionMentionBackground);
        setDrawableColorByKey(dialogs_pollMentionDrawable, key_color_purple);
        setDrawableColorByKey(dialogs_mentionDrawableMuted, i6);
        setDrawableColorByKey(dialogs_reactionsMentionDrawableMuted, i6);
        setDrawableColorByKey(dialogs_pollMentionDrawableMuted, i6);
        setDrawableColorByKey(dialogs_forum_arrowDrawable, key_chats_message);
        setDrawableColorByKey(dialogs_verifiedDrawable, key_chats_verifiedBackground);
        setDrawableColorByKey(dialogs_verifiedCheckDrawable, key_chats_verifiedCheck);
        setDrawableColorByKey(dialogs_holidayDrawable, key_actionBarDefaultTitle);
        ScamDrawable scamDrawable = dialogs_scamDrawable;
        int i10 = key_chats_draft;
        setDrawableColorByKey(scamDrawable, i10);
        setDrawableColorByKey(dialogs_fakeDrawable, i10);
    }

    public static void reloadAllResources(Context context) {
        destroyResources();
        if (chat_msgInDrawable != null) {
            chat_msgInDrawable = null;
            currentColor = 0;
            createChatResources(context, false);
        }
        if (dialogs_namePaint != null) {
            dialogs_namePaint = null;
            dividerPaint = null;
            createDialogsResources(context);
        }
        if (profile_verifiedDrawable != null) {
            profile_verifiedDrawable = null;
            createProfileResources(context);
        }
    }

    public static void createCommonMessageResources() {
        synchronized (sync) {
            try {
                if (chat_msgTextPaint == null) {
                    chat_msgTextPaint = new TextPaint(1);
                    chat_msgGameTextPaint = new TextPaint(1);
                    chat_msgTextPaintEmoji = new TextPaint[6];
                    chat_msgTextPaintOneEmoji = new TextPaint(1);
                    chat_msgTextPaintTwoEmoji = new TextPaint(1);
                    chat_msgTextPaintThreeEmoji = new TextPaint(1);
                    TextPaint textPaint = new TextPaint(1);
                    chat_msgBotButtonPaint = textPaint;
                    textPaint.setTypeface(AndroidUtilities.bold());
                    TextPaint textPaint2 = new TextPaint(1);
                    chat_namePaint = textPaint2;
                    textPaint2.setTypeface(AndroidUtilities.bold());
                    TextPaint textPaint3 = new TextPaint(1);
                    chat_replyNamePaint = textPaint3;
                    textPaint3.setTypeface(AndroidUtilities.bold());
                    chat_replyTextPaint = new TextPaint(1);
                    chat_quoteTextPaint = new TextPaint(1);
                    chat_explanationTextPaint = new TextPaint(1);
                    chat_titleLabelTextPaint = new TextPaint(1);
                    TextPaint textPaint4 = new TextPaint(1);
                    chat_topicTextPaint = textPaint4;
                    textPaint4.setTypeface(AndroidUtilities.bold());
                    chat_forwardNamePaint = new TextPaint(1);
                    chat_adminPaint = new TextPaint(1);
                    chat_timePaint = new TextPaint(1);
                    TextPaint textPaint5 = new TextPaint(1);
                    chat_msgTextCodePaint = textPaint5;
                    Typeface typeface = Typeface.MONOSPACE;
                    textPaint5.setTypeface(typeface);
                    TextPaint textPaint6 = new TextPaint(1);
                    chat_msgTextCode2Paint = textPaint6;
                    textPaint6.setTypeface(typeface);
                    TextPaint textPaint7 = new TextPaint(1);
                    chat_msgTextCode3Paint = textPaint7;
                    textPaint7.setTypeface(typeface);
                    chat_msgCodeBgPaint = new TextPaint(1);
                    chat_ephemeralPaint = new TextPaint(1);
                }
                float[] fArr = {0.68f, 0.46f, 0.34f, 0.28f, 0.22f, 0.19f};
                int i = 0;
                while (true) {
                    TextPaint[] textPaintArr = chat_msgTextPaintEmoji;
                    if (i < textPaintArr.length) {
                        textPaintArr[i] = new TextPaint(1);
                        chat_msgTextPaintEmoji[i].setTextSize(AndroidUtilities.dp(fArr[i] * 120.0f));
                        i++;
                    } else {
                        chat_msgTextPaintOneEmoji.setTextSize(AndroidUtilities.dp(46.0f));
                        chat_msgTextPaintTwoEmoji.setTextSize(AndroidUtilities.dp(38.0f));
                        chat_msgTextPaintThreeEmoji.setTextSize(AndroidUtilities.dp(30.0f));
                        chat_msgTextPaint.setTextSize(AndroidUtilities.dp(SharedConfig.fontSize));
                        chat_msgGameTextPaint.setTextSize(AndroidUtilities.dp(14.0f));
                        chat_msgBotButtonPaint.setTextSize(AndroidUtilities.dp(15.0f));
                        float f = ((SharedConfig.fontSize * 2) + 10) / 3.0f;
                        chat_namePaint.setTextSize(AndroidUtilities.dp(f));
                        chat_replyNamePaint.setTextSize(AndroidUtilities.dp(f));
                        chat_replyTextPaint.setTextSize(AndroidUtilities.dp(f));
                        float f2 = f - 1.0f;
                        chat_quoteTextPaint.setTextSize(AndroidUtilities.dp(f2));
                        chat_explanationTextPaint.setTextSize(AndroidUtilities.dp(f));
                        chat_ephemeralPaint.setTextSize(AndroidUtilities.dp(12.0f));
                        chat_topicTextPaint.setTextSize(AndroidUtilities.dp(f2));
                        chat_titleLabelTextPaint.setTextSize(AndroidUtilities.dp(f - 2.0f));
                        chat_forwardNamePaint.setTextSize(AndroidUtilities.dp(f));
                        chat_adminPaint.setTextSize(AndroidUtilities.dp(f2));
                        chat_msgTextCodePaint.setTextSize(AndroidUtilities.dp(Math.max(Math.min(10, SharedConfig.fontSize - 1), SharedConfig.fontSize - 2)));
                        chat_msgTextCode2Paint.setTextSize(AndroidUtilities.dp(Math.max(Math.min(10, SharedConfig.fontSize - 2), SharedConfig.fontSize - 3)));
                        chat_msgTextCode3Paint.setTextSize(AndroidUtilities.dp(Math.max(Math.min(10, SharedConfig.fontSize - 2), SharedConfig.fontSize - 5)));
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static void createCommonChatResources() {
        createCommonMessageResources();
        if (chat_infoPaint == null) {
            chat_infoPaint = new TextPaint(1);
            TextPaint textPaint = new TextPaint(1);
            chat_infoBoldPaint = textPaint;
            textPaint.setTypeface(AndroidUtilities.bold());
            TextPaint textPaint2 = new TextPaint(1);
            chat_stickerCommentCountPaint = textPaint2;
            textPaint2.setTypeface(AndroidUtilities.bold());
            TextPaint textPaint3 = new TextPaint(1);
            chat_docNamePaint = textPaint3;
            textPaint3.setTypeface(AndroidUtilities.bold());
            chat_docBackPaint = new Paint(1);
            Paint paint = new Paint(1);
            chat_deleteProgressPaint = paint;
            Paint.Style style = Paint.Style.STROKE;
            paint.setStyle(style);
            Paint paint2 = chat_deleteProgressPaint;
            Paint.Cap cap = Paint.Cap.ROUND;
            paint2.setStrokeCap(cap);
            TextPaint textPaint4 = new TextPaint(1);
            chat_locationTitlePaint = textPaint4;
            textPaint4.setTypeface(AndroidUtilities.bold());
            chat_locationAddressPaint = new TextPaint(1);
            Paint paint3 = new Paint();
            chat_urlPaint = paint3;
            paint3.setPathEffect(LinkPath.getRoundedEffect());
            Paint paint4 = new Paint();
            chat_outUrlPaint = paint4;
            paint4.setPathEffect(LinkPath.getRoundedEffect());
            Paint paint5 = new Paint();
            chat_textSearchSelectionPaint = paint5;
            paint5.setPathEffect(LinkPath.getRoundedEffect());
            Paint paint6 = new Paint(1);
            chat_radialProgressPaint = paint6;
            paint6.setStrokeCap(cap);
            chat_radialProgressPaint.setStyle(style);
            chat_radialProgressPaint.setColor(-1610612737);
            Paint paint7 = new Paint(1);
            chat_radialProgress2Paint = paint7;
            paint7.setStrokeCap(cap);
            chat_radialProgress2Paint.setStyle(style);
            chat_audioTimePaint = new TextPaint(1);
            TextPaint textPaint5 = new TextPaint(1);
            chat_livePaint = textPaint5;
            textPaint5.setTypeface(Typeface.DEFAULT_BOLD);
            TextPaint textPaint6 = new TextPaint(1);
            chat_audioTitlePaint = textPaint6;
            textPaint6.setTypeface(AndroidUtilities.bold());
            chat_audioPerformerPaint = new TextPaint(1);
            TextPaint textPaint7 = new TextPaint(1);
            chat_botButtonPaint = textPaint7;
            textPaint7.setTypeface(AndroidUtilities.bold());
            TextPaint textPaint8 = new TextPaint(1);
            chat_contactNamePaint = textPaint8;
            textPaint8.setTypeface(AndroidUtilities.bold());
            chat_contactPhonePaint = new TextPaint(1);
            chat_durationPaint = new TextPaint(1);
            TextPaint textPaint9 = new TextPaint(1);
            chat_gamePaint = textPaint9;
            textPaint9.setTypeface(AndroidUtilities.bold());
            chat_shipmentPaint = new TextPaint(1);
            chat_timePaint = new TextPaint(1);
            chat_adminPaint = new TextPaint(1);
            chat_ephemeralPaint = new TextPaint(1);
            TextPaint textPaint10 = new TextPaint(1);
            chat_namePaint = textPaint10;
            textPaint10.setTypeface(AndroidUtilities.bold());
            chat_forwardNamePaint = new TextPaint(1);
            TextPaint textPaint11 = new TextPaint(1);
            chat_replyNamePaint = textPaint11;
            textPaint11.setTypeface(AndroidUtilities.bold());
            chat_replyTextPaint = new TextPaint(1);
            TextPaint textPaint12 = new TextPaint(1);
            chat_topicTextPaint = textPaint12;
            textPaint12.setTypeface(AndroidUtilities.bold());
            chat_titleLabelTextPaint = new TextPaint(1);
            chat_commentTextPaint = new TextPaint(1);
            TextPaint textPaint13 = new TextPaint(1);
            chat_instantViewPaint = textPaint13;
            textPaint13.setTypeface(AndroidUtilities.bold());
            Paint paint8 = new Paint(1);
            chat_instantViewRectPaint = paint8;
            paint8.setStyle(style);
            chat_instantViewRectPaint.setStrokeCap(cap);
            chat_instantViewButtonPaint = new Paint(1);
            Paint paint9 = new Paint(1);
            chat_pollTimerPaint = paint9;
            paint9.setStyle(style);
            chat_pollTimerPaint.setStrokeCap(cap);
            chat_replyLinePaint = new Paint(1);
            chat_msgErrorPaint = new Paint(1);
            chat_statusPaint = new Paint(1);
            Paint paint10 = new Paint(1);
            chat_statusRecordPaint = paint10;
            paint10.setStyle(style);
            chat_statusRecordPaint.setStrokeCap(cap);
            chat_actionTextPaint = new TextPaint(1);
            chat_actionTextPaint2 = new TextPaint(1);
            chat_actionTextPaint3 = new TextPaint(1);
            chat_actionTextPaint.setTypeface(AndroidUtilities.bold());
            TextPaint textPaint14 = new TextPaint(1);
            chat_unlockExtendedMediaTextPaint = textPaint14;
            textPaint14.setTypeface(AndroidUtilities.bold());
            Paint paint11 = new Paint(1);
            chat_actionBackgroundGradientDarkenPaint = paint11;
            paint11.setColor(352321536);
            chat_timeBackgroundPaint = new Paint(1);
            TextPaint textPaint15 = new TextPaint(1);
            chat_contextResult_titleTextPaint = textPaint15;
            textPaint15.setTypeface(AndroidUtilities.bold());
            chat_contextResult_descriptionTextPaint = new TextPaint(1);
            chat_composeBackgroundPaint = new Paint();
            chat_radialProgressPausedPaint = new Paint(1);
            chat_radialProgressPausedSeekbarPaint = new Paint(1);
            chat_videoProgressPaint = new Paint(1);
            chat_messageBackgroundSelectedPaint = new Paint(1);
            chat_actionBackgroundPaint = new Paint(7);
            chat_actionBackgroundSelectedPaint = new Paint(7);
            addChatPaint("paintChatMessageBackgroundSelected", chat_messageBackgroundSelectedPaint, key_chat_selectedBackground);
            Paint paint12 = chat_actionBackgroundPaint;
            int i = key_chat_serviceBackground;
            addChatPaint("paintChatActionBackground", paint12, i);
            addChatPaint("paintChatActionBackgroundDarken", chat_actionBackgroundGradientDarkenPaint, i);
            addChatPaint("paintChatActionBackgroundSelected", chat_actionBackgroundSelectedPaint, key_chat_serviceBackgroundSelected);
            TextPaint textPaint16 = chat_actionTextPaint;
            int i2 = key_chat_serviceText;
            addChatPaint("paintChatActionText", textPaint16, i2);
            addChatPaint("paintChatActionText2", chat_actionTextPaint2, i2);
            addChatPaint("paintChatActionText3", chat_actionTextPaint3, i2);
            addChatPaint("paintChatBotButton", chat_botButtonPaint, key_chat_botButtonText);
            addChatPaint("paintChatComposeBackground", chat_composeBackgroundPaint, key_chat_messagePanelBackground);
            addChatPaint("paintChatTimeBackground", chat_timeBackgroundPaint, key_chat_mediaTimeBackground);
        }
    }

    /* JADX WARN: Failed to calculate best type for var: r0v18 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r0v18 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:159)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:136)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:241)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r0v18 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r0v18 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r0v19 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r0v19 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /* JADX WARN: Failed to calculate best type for var: r7v6 ??
    jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r7v6 ??, new type: float
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
    	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
    	at java.base/java.util.ArrayList.forEach(Unknown Source)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
    	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
    Caused by: java.lang.NullPointerException
     */
    /*  JADX ERROR: Types fix failed
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r0v18 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryPossibleTypes(FixTypesVisitor.java:186)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:245)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
        Caused by: java.lang.NullPointerException
        */
    public static void createChatResources(android.content.Context r20, boolean r21) {
        /*
            Method dump skipped, instruction units count: 2593
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.Theme.createChatResources(android.content.Context, boolean):void");
    }

    public static void applyChatTheme(boolean z, boolean z2) {
        if (chat_msgTextPaint == null || chat_msgInDrawable == null || z) {
            return;
        }
        chat_gamePaint.setColor(getColor(key_chat_previewGameText));
        chat_durationPaint.setColor(getColor(key_chat_previewDurationText));
        chat_botButtonPaint.setColor(getColor(key_chat_botButtonText));
        chat_urlPaint.setColor(getColor(key_chat_linkSelectBackground));
        chat_outUrlPaint.setColor(getColor(key_chat_outLinkSelectBackground));
        chat_textSearchSelectionPaint.setColor(getColor(key_chat_textSelectBackground));
        chat_msgErrorPaint.setColor(getColor(key_chat_sentError));
        Paint paint = chat_statusPaint;
        int i = key_chat_status;
        paint.setColor(getColor(i));
        chat_statusRecordPaint.setColor(getColor(i));
        TextPaint textPaint = chat_actionTextPaint;
        int i2 = key_chat_serviceText;
        textPaint.setColor(getColor(i2));
        chat_actionTextPaint2.setColor(getColor(i2));
        chat_actionTextPaint3.setColor(getColor(i2));
        ((android.text.TextPaint) chat_actionTextPaint).linkColor = getColor(key_chat_serviceLink);
        chat_unlockExtendedMediaTextPaint.setColor(getColor(i2));
        chat_contextResult_titleTextPaint.setColor(getColor(key_windowBackgroundWhiteBlackText));
        chat_composeBackgroundPaint.setColor(getColor(key_chat_messagePanelBackground));
        chat_timeBackgroundPaint.setColor(getColor(key_chat_mediaTimeBackground));
        setDrawableColorByKey(chat_msgNoSoundDrawable, key_chat_mediaTimeText);
        MessageDrawable messageDrawable = chat_msgInDrawable;
        int i3 = key_chat_inBubble;
        setDrawableColorByKey(messageDrawable, i3);
        MessageDrawable messageDrawable2 = chat_msgInSelectedDrawable;
        int i4 = key_chat_inBubbleSelected;
        setDrawableColorByKey(messageDrawable2, i4);
        setDrawableColorByKey(chat_msgInMediaDrawable, i3);
        setDrawableColorByKey(chat_msgInMediaSelectedDrawable, i4);
        setDrawableColorByKey(chat_msgOutCheckDrawable, key_chat_outSentCheck);
        setDrawableColorByKey(chat_msgOutCheckSelectedDrawable, key_chat_outSentCheckSelected);
        Drawable drawable = chat_msgOutCheckReadDrawable;
        int i5 = key_chat_outSentCheckRead;
        setDrawableColorByKey(drawable, i5);
        Drawable drawable2 = chat_msgOutCheckReadSelectedDrawable;
        int i6 = key_chat_outSentCheckReadSelected;
        setDrawableColorByKey(drawable2, i6);
        setDrawableColorByKey(chat_msgOutHalfCheckDrawable, i5);
        setDrawableColorByKey(chat_msgOutHalfCheckSelectedDrawable, i6);
        Drawable drawable3 = chat_msgMediaCheckDrawable;
        int i7 = key_chat_mediaSentCheck;
        setDrawableColorByKey(drawable3, i7);
        setDrawableColorByKey(chat_msgMediaHalfCheckDrawable, i7);
        setDrawableColorByKey(chat_msgStickerCheckDrawable, i2);
        setDrawableColorByKey(chat_msgStickerHalfCheckDrawable, i2);
        setDrawableColorByKey(chat_msgStickerViewsDrawable, i2);
        setDrawableColorByKey(chat_msgStickerRepliesDrawable, i2);
        setDrawableColorByKey(chat_msgUnlockDrawable, i2);
        Drawable drawable4 = chat_shareIconDrawable;
        int i8 = key_chat_serviceIcon;
        setDrawableColorByKey(drawable4, i8);
        setDrawableColorByKey(chat_replyIconDrawable, i8);
        setDrawableColorByKey(chat_goIconDrawable, i8);
        setDrawableColorByKey(chat_botInlineDrawable, i8);
        setDrawableColorByKey(chat_botWebViewDrawable, i8);
        Drawable drawable5 = chat_botLockDrawable;
        int i9 = key_chat_lockIcon;
        setDrawableColorByKey(drawable5, i9);
        setDrawableColorByKey(chat_botInviteDrawable, i8);
        setDrawableColorByKey(chat_botLinkDrawable, i8);
        Drawable drawable6 = chat_msgInViewsDrawable;
        int i10 = key_chat_inViews;
        setDrawableColorByKey(drawable6, i10);
        Drawable drawable7 = chat_msgInViewsSelectedDrawable;
        int i11 = key_chat_inViewsSelected;
        setDrawableColorByKey(drawable7, i11);
        Drawable drawable8 = chat_msgOutViewsDrawable;
        int i12 = key_chat_outViews;
        setDrawableColorByKey(drawable8, i12);
        Drawable drawable9 = chat_msgOutViewsSelectedDrawable;
        int i13 = key_chat_outViewsSelected;
        setDrawableColorByKey(drawable9, i13);
        setDrawableColorByKey(chat_msgInRepliesDrawable, i10);
        setDrawableColorByKey(chat_msgInRepliesSelectedDrawable, i11);
        setDrawableColorByKey(chat_msgOutRepliesDrawable, i12);
        setDrawableColorByKey(chat_msgOutRepliesSelectedDrawable, i13);
        setDrawableColorByKey(chat_msgInPinnedDrawable, i10);
        setDrawableColorByKey(chat_msgInPinnedSelectedDrawable, i11);
        setDrawableColorByKey(chat_msgOutPinnedDrawable, i12);
        setDrawableColorByKey(chat_msgOutPinnedSelectedDrawable, i13);
        Drawable drawable10 = chat_msgMediaPinnedDrawable;
        int i14 = key_chat_mediaViews;
        setDrawableColorByKey(drawable10, i14);
        setDrawableColorByKey(chat_msgStickerPinnedDrawable, i2);
        setDrawableColorByKey(chat_msgMediaViewsDrawable, i14);
        setDrawableColorByKey(chat_msgMediaRepliesDrawable, i14);
        setDrawableColorByKey(chat_msgInMenuDrawable, key_chat_inMenu);
        setDrawableColorByKey(chat_msgInMenuSelectedDrawable, key_chat_inMenuSelected);
        setDrawableColorByKey(chat_msgOutMenuDrawable, key_chat_outMenu);
        setDrawableColorByKey(chat_msgOutMenuSelectedDrawable, key_chat_outMenuSelected);
        setDrawableColorByKey(chat_msgMediaMenuDrawable, key_chat_mediaMenu);
        setDrawableColorByKey(chat_msgOutInstantDrawable, key_chat_outInstant);
        Drawable drawable11 = chat_msgInInstantDrawable;
        int i15 = key_chat_inInstant;
        setDrawableColorByKey(drawable11, i15);
        setDrawableColorByKey(chat_msgErrorDrawable, key_chat_sentErrorIcon);
        setDrawableColorByKey(chat_muteIconDrawable, key_chat_muteIcon);
        setDrawableColorByKey(chat_lockIconDrawable, i9);
        Drawable drawable12 = chat_inlineResultFile;
        int i16 = key_chat_inlineResultIcon;
        setDrawableColorByKey(drawable12, i16);
        setDrawableColorByKey(chat_inlineResultAudio, i16);
        setDrawableColorByKey(chat_inlineResultLocation, i16);
        setDrawableColorByKey(chat_commentDrawable, i15);
        setDrawableColorByKey(chat_commentStickerDrawable, i8);
        setDrawableColorByKey(chat_commentArrowDrawable, i15);
        Drawable drawable13 = chat_gradientLeftDrawable;
        int i17 = key_chat_stickersHintPanel;
        setDrawableColorByKey(drawable13, i17);
        setDrawableColorByKey(chat_gradientRightDrawable, i17);
        for (int i18 = 0; i18 < 2; i18++) {
            setDrawableColorByKey(chat_msgInCallDrawable[i18], key_chat_inInstant);
            setDrawableColorByKey(chat_msgInCallSelectedDrawable[i18], key_chat_inInstantSelected);
            setDrawableColorByKey(chat_msgOutCallDrawable[i18], key_chat_outInstant);
            setDrawableColorByKey(chat_msgOutCallSelectedDrawable[i18], key_chat_outInstantSelected);
        }
        setDrawableColorByKey(chat_msgCallUpGreenDrawable, key_chat_outGreenCall);
        Drawable drawable14 = chat_msgCallDownRedDrawable;
        int i19 = key_fill_RedNormal;
        setDrawableColorByKey(drawable14, i19);
        setDrawableColorByKey(chat_msgCallDownGreenDrawable, key_chat_inGreenCall);
        setDrawableColorByKey(calllog_msgCallUpRedDrawable, i19);
        Drawable drawable15 = calllog_msgCallUpGreenDrawable;
        int i20 = key_calls_callReceivedGreenIcon;
        setDrawableColorByKey(drawable15, i20);
        setDrawableColorByKey(calllog_msgCallDownRedDrawable, i19);
        setDrawableColorByKey(calllog_msgCallDownGreenDrawable, i20);
        for (StatusDrawable statusDrawable : chat_status_drawables) {
            setDrawableColorByKey(statusDrawable, key_chats_actionMessage);
        }
        for (int i21 = 0; i21 < 5; i21++) {
            setCombinedDrawableColor(chat_fileStatesDrawable[i21][0], getColor(key_chat_inLoader), false);
            setCombinedDrawableColor(chat_fileStatesDrawable[i21][0], getColor(key_chat_inMediaIcon), true);
            setCombinedDrawableColor(chat_fileStatesDrawable[i21][1], getColor(key_chat_inLoaderSelected), false);
            setCombinedDrawableColor(chat_fileStatesDrawable[i21][1], getColor(key_chat_inMediaIconSelected), true);
        }
        setCombinedDrawableColor(chat_contactDrawable[0], getColor(key_chat_inContactBackground), false);
        setCombinedDrawableColor(chat_contactDrawable[0], getColor(key_chat_inContactIcon), true);
        setCombinedDrawableColor(chat_contactDrawable[1], getColor(key_chat_outContactBackground), false);
        setCombinedDrawableColor(chat_contactDrawable[1], getColor(key_chat_outContactIcon), true);
        setDrawableColor(chat_locationDrawable[0], getColor(key_chat_inLocationIcon));
        setDrawableColor(chat_locationDrawable[1], getColor(key_chat_outLocationIcon));
        setDrawableColor(chat_pollHintDrawable[0], getColor(key_chat_inPreviewInstantText));
        setDrawableColor(chat_pollHintDrawable[1], getColor(key_chat_outPreviewInstantText));
        setDrawableColor(chat_psaHelpDrawable[0], getColor(key_chat_inViews));
        setDrawableColor(chat_psaHelpDrawable[1], getColor(key_chat_outViews));
        setDrawableColorByKey(chat_composeShadowDrawable, key_chat_messagePanelShadow);
        setDrawableColorByKey(chat_composeShadowRoundDrawable, key_chat_messagePanelBackground);
        int color = getColor(key_chat_outAudioSeekbarFill) == -1 ? getColor(key_chat_outBubble) : -1;
        setDrawableColor(chat_pollCheckDrawable[1], color);
        setDrawableColor(chat_pollCrossDrawable[1], color);
        setDrawableColor(chat_attachEmptyDrawable, getColor(key_chat_attachEmptyImage));
        if (z2 || disallowChangeServiceMessageColor) {
            return;
        }
        applyChatServiceMessageColor();
        applyChatMessageSelectedBackgroundColor();
    }

    public static void applyChatServiceMessageColor() {
        Drawable drawable = wallpaper;
        if (drawable != null) {
            applyChatServiceMessageColor(null, null, drawable);
        }
    }

    public static boolean hasGradientService() {
        return serviceBitmapShader != null;
    }

    public static void applyServiceShaderMatrixForView(View view, View view2) {
        applyServiceShaderMatrixForView(view, view2, null);
    }

    public static void applyServiceShaderMatrixForView(View view, View view2, ResourcesProvider resourcesProvider) {
        int measuredWidth;
        if (view == null || view2 == null) {
            return;
        }
        view.getLocationOnScreen(viewPos);
        int[] iArr = viewPos;
        int i = iArr[0];
        int i2 = iArr[1];
        view2.getLocationOnScreen(iArr);
        if (view2 instanceof ThemePreviewActivity.BackgroundView) {
            Bitmap bitmap = serviceBitmap;
            if (bitmap != null) {
                float width = bitmap.getWidth();
                measuredWidth = (int) (i + (((view2.getMeasuredWidth() - (width * Math.max(view2.getMeasuredWidth() / width, view2.getMeasuredHeight() / serviceBitmap.getHeight()))) / 2.0f) - ((ThemePreviewActivity.BackgroundView) view2).tx));
            } else {
                measuredWidth = (int) (i + (-((ThemePreviewActivity.BackgroundView) view2).tx));
            }
            i = measuredWidth;
            i2 = (int) (i2 + (-((ThemePreviewActivity.BackgroundView) view2).ty));
        }
        if (resourcesProvider != null) {
            resourcesProvider.applyServiceShaderMatrix(view2.getMeasuredWidth(), view2.getMeasuredHeight(), i, i2 - viewPos[1]);
        } else {
            applyServiceShaderMatrix(view2.getMeasuredWidth(), view2.getMeasuredHeight(), i, i2 - viewPos[1]);
        }
    }

    public static void applyServiceShaderMatrix(int i, int i2, float f, float f2) {
        applyServiceShaderMatrix(serviceBitmap, serviceBitmapShader, serviceBitmapMatrix, i, i2, f, f2);
    }

    public static void applyServiceShaderMatrix(Bitmap bitmap, BitmapShader bitmapShader, Matrix matrix, int i, int i2, float f, float f2) {
        if (bitmapShader == null || matrix == null) {
            return;
        }
        float width = bitmap.getWidth();
        float height = bitmap.getHeight();
        float f3 = i;
        float f4 = i2;
        float fMax = Math.max(f3 / width, f4 / height);
        matrix.reset();
        matrix.setTranslate(((f3 - (width * fMax)) / 2.0f) - f, ((f4 - (height * fMax)) / 2.0f) - f2);
        matrix.preScale(fMax, fMax);
        bitmapShader.setLocalMatrix(matrix);
    }

    public static void applyChatServiceMessageColor(int[] iArr, Drawable drawable, Drawable drawable2) {
        int iValueAt;
        int i;
        int iValueAt2;
        Bitmap bitmapCheckBlur;
        if (chat_actionBackgroundPaint == null) {
            return;
        }
        serviceMessageColor = serviceMessageColorBackup;
        serviceSelectedMessageColor = serviceSelectedMessageColorBackup;
        if (iArr != null && iArr.length >= 2) {
            i = iArr[0];
            iValueAt2 = iArr[1];
            serviceMessageColor = i;
            serviceSelectedMessageColor = iValueAt2;
        } else {
            int iIndexOfKey = currentColors.indexOfKey(key_chat_serviceBackground);
            if (iIndexOfKey >= 0) {
                iValueAt = currentColors.valueAt(iIndexOfKey);
            } else {
                iValueAt = serviceMessageColor;
            }
            i = iValueAt;
            int iIndexOfKey2 = currentColors.indexOfKey(key_chat_serviceBackgroundSelected);
            if (iIndexOfKey2 >= 0) {
                iValueAt2 = currentColors.valueAt(iIndexOfKey2);
            } else {
                iValueAt2 = serviceSelectedMessageColor;
            }
        }
        if (drawable == null) {
            drawable = drawable2;
        }
        boolean z = drawable instanceof MotionBackgroundDrawable;
        if ((z || (drawable instanceof BitmapDrawable)) && SharedConfig.getDevicePerformanceClass() != 0 && LiteMode.isEnabled(32)) {
            if (z) {
                bitmapCheckBlur = ((MotionBackgroundDrawable) drawable).getBitmap();
            } else {
                bitmapCheckBlur = drawable instanceof BitmapDrawable ? checkBlur(drawable) : null;
            }
            if (serviceBitmap != bitmapCheckBlur) {
                serviceBitmap = bitmapCheckBlur;
                Bitmap bitmap = serviceBitmap;
                Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                serviceBitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
                if (Build.VERSION.SDK_INT >= 33) {
                    serviceBitmapShader.setFilterMode(2);
                }
                if (serviceBitmapMatrix == null) {
                    serviceBitmapMatrix = new Matrix();
                }
            }
            setDrawableColor(chat_msgStickerPinnedDrawable, -1);
            setDrawableColor(chat_msgStickerCheckDrawable, -1);
            setDrawableColor(chat_msgStickerHalfCheckDrawable, -1);
            setDrawableColor(chat_msgStickerViewsDrawable, -1);
            setDrawableColor(chat_msgStickerRepliesDrawable, -1);
            chat_actionTextPaint.setColor(-1);
            chat_actionTextPaint2.setColor(-1);
            chat_actionTextPaint3.setColor(-1);
            ((android.text.TextPaint) chat_actionTextPaint).linkColor = -1;
            chat_unlockExtendedMediaTextPaint.setColor(-1);
            chat_botButtonPaint.setColor(-1);
            setDrawableColor(chat_commentStickerDrawable, -1);
            setDrawableColor(chat_shareIconDrawable, -1);
            setDrawableColor(chat_replyIconDrawable, -1);
            setDrawableColor(chat_goIconDrawable, -1);
            setDrawableColor(chat_botInlineDrawable, -1);
            setDrawableColor(chat_botWebViewDrawable, -1);
            setDrawableColor(chat_botLockDrawable, -1);
            setDrawableColor(chat_botInviteDrawable, -1);
            setDrawableColor(chat_botLinkDrawable, -1);
        } else {
            serviceBitmap = null;
            serviceBitmapShader = null;
            Drawable drawable3 = chat_msgStickerPinnedDrawable;
            int i2 = key_chat_serviceText;
            setDrawableColorByKey(drawable3, i2);
            setDrawableColorByKey(chat_msgStickerCheckDrawable, i2);
            setDrawableColorByKey(chat_msgStickerHalfCheckDrawable, i2);
            setDrawableColorByKey(chat_msgStickerViewsDrawable, i2);
            setDrawableColorByKey(chat_msgStickerRepliesDrawable, i2);
            chat_actionTextPaint.setColor(getColor(i2));
            chat_actionTextPaint2.setColor(getColor(i2));
            ((android.text.TextPaint) chat_actionTextPaint).linkColor = getColor(key_chat_serviceLink);
            chat_unlockExtendedMediaTextPaint.setColor(getColor(i2));
            Drawable drawable4 = chat_commentStickerDrawable;
            int i3 = key_chat_serviceIcon;
            setDrawableColorByKey(drawable4, i3);
            setDrawableColorByKey(chat_shareIconDrawable, i3);
            setDrawableColorByKey(chat_replyIconDrawable, i3);
            setDrawableColorByKey(chat_goIconDrawable, i3);
            setDrawableColorByKey(chat_botInlineDrawable, i3);
            setDrawableColorByKey(chat_botWebViewDrawable, i3);
            setDrawableColorByKey(chat_botLockDrawable, i3);
            setDrawableColorByKey(chat_botInviteDrawable, i3);
            setDrawableColorByKey(chat_botLinkDrawable, i3);
            chat_botButtonPaint.setColor(getColor(key_chat_botButtonText));
        }
        chat_actionBackgroundPaint.setColor(i);
        chat_actionBackgroundSelectedPaint.setColor(iValueAt2);
        currentColor = i;
        if (serviceBitmapShader != null && (currentColors.indexOfKey(key_chat_serviceBackground) < 0 || z || (drawable instanceof BitmapDrawable))) {
            ColorMatrix colorMatrix = new ColorMatrix();
            if (z) {
                if (((MotionBackgroundDrawable) drawable).getIntensity() >= 0.0f) {
                    colorMatrix.setSaturation(1.6f);
                    AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, isCurrentThemeDark() ? 0.97f : 0.92f);
                    AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, isCurrentThemeDark() ? 0.12f : -0.06f);
                } else {
                    colorMatrix.setSaturation(1.1f);
                    AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, isCurrentThemeDark() ? 0.4f : 0.8f);
                    AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, isCurrentThemeDark() ? 0.08f : -0.06f);
                }
            } else {
                colorMatrix.setSaturation(1.6f);
                AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, isCurrentThemeDark() ? 0.9f : 0.84f);
                AndroidUtilities.adjustBrightnessColorMatrix(colorMatrix, isCurrentThemeDark() ? -0.04f : 0.06f);
            }
            chat_actionBackgroundPaint.setFilterBitmap(true);
            chat_actionBackgroundPaint.setShader(serviceBitmapShader);
            chat_actionBackgroundPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            chat_actionBackgroundPaint.setAlpha(255);
            chat_actionBackgroundSelectedPaint.setFilterBitmap(true);
            chat_actionBackgroundSelectedPaint.setShader(serviceBitmapShader);
            ColorMatrix colorMatrix2 = new ColorMatrix(colorMatrix);
            AndroidUtilities.adjustSaturationColorMatrix(colorMatrix2, 0.26f);
            isCurrentThemeDark();
            AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix2, 0.92f);
            chat_actionBackgroundSelectedPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix2));
            chat_actionBackgroundSelectedPaint.setAlpha(255);
            chat_actionBackgroundGradientDarkenPaint.setAlpha(0);
            return;
        }
        chat_actionBackgroundPaint.setColorFilter(null);
        chat_actionBackgroundPaint.setShader(null);
        chat_actionBackgroundSelectedPaint.setColorFilter(null);
        chat_actionBackgroundSelectedPaint.setShader(null);
        chat_actionBackgroundGradientDarkenPaint.setAlpha(21);
    }

    private static Bitmap checkBlur(Drawable drawable) {
        WeakReference<Drawable> weakReference = lastDrawableToBlur;
        if (weakReference != null && weakReference.get() == drawable) {
            return blurredBitmap;
        }
        WeakReference<Drawable> weakReference2 = lastDrawableToBlur;
        if (weakReference2 != null) {
            weakReference2.clear();
        }
        lastDrawableToBlur = null;
        if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
            blurredBitmap = null;
            return null;
        }
        lastDrawableToBlur = new WeakReference<>(drawable);
        int intrinsicWidth = (int) ((drawable.getIntrinsicWidth() / drawable.getIntrinsicHeight()) * 24.0f);
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(intrinsicWidth, 24, Bitmap.Config.ARGB_8888);
        drawable.setBounds(0, 0, intrinsicWidth, 24);
        ColorFilter colorFilter = drawable.getColorFilter();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(1.3f);
        AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, 0.94f);
        drawable.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        drawable.draw(new Canvas(bitmapCreateBitmap));
        drawable.setColorFilter(colorFilter);
        Utilities.blurBitmap(bitmapCreateBitmap, 3, 1, bitmapCreateBitmap.getWidth(), bitmapCreateBitmap.getHeight(), bitmapCreateBitmap.getRowBytes());
        blurredBitmap = bitmapCreateBitmap;
        return bitmapCreateBitmap;
    }

    public static void applyChatMessageSelectedBackgroundColor() {
        applyChatMessageSelectedBackgroundColor(null, wallpaper);
    }

    public static void applyChatMessageSelectedBackgroundColor(Drawable drawable, Drawable drawable2) {
        Bitmap bitmap;
        if (chat_messageBackgroundSelectedPaint == null) {
            return;
        }
        int i = currentColors.get(key_chat_selectedBackground);
        if (drawable == null) {
            drawable = drawable2;
        }
        boolean z = (drawable instanceof MotionBackgroundDrawable) && SharedConfig.getDevicePerformanceClass() != 0 && i == 0;
        if (z && serviceBitmap != (bitmap = ((MotionBackgroundDrawable) drawable).getBitmap())) {
            serviceBitmap = bitmap;
            Bitmap bitmap2 = serviceBitmap;
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            serviceBitmapShader = new BitmapShader(bitmap2, tileMode, tileMode);
            if (serviceBitmapMatrix == null) {
                serviceBitmapMatrix = new Matrix();
            }
        }
        if (serviceBitmapShader != null && i == 0 && z) {
            ColorMatrix colorMatrix = new ColorMatrix();
            AndroidUtilities.adjustSaturationColorMatrix(colorMatrix, 2.5f);
            AndroidUtilities.multiplyBrightnessColorMatrix(colorMatrix, 0.75f);
            chat_messageBackgroundSelectedPaint.setShader(serviceBitmapShader);
            chat_messageBackgroundSelectedPaint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
            chat_messageBackgroundSelectedPaint.setAlpha(64);
            return;
        }
        Paint paint = chat_messageBackgroundSelectedPaint;
        if (i == 0) {
            i = TLObject.FLAG_30;
        }
        paint.setColor(i);
        chat_messageBackgroundSelectedPaint.setColorFilter(null);
        chat_messageBackgroundSelectedPaint.setShader(null);
    }

    public static void createProfileResources(Context context) {
        if (profile_verifiedDrawable == null) {
            profile_aboutTextPaint = new TextPaint(1);
            Resources resources = context.getResources();
            profile_verifiedDrawable = resources.getDrawable(R.drawable.verified_area).mutate();
            profile_verifiedCheckDrawable = resources.getDrawable(R.drawable.verified_check).mutate();
            applyProfileTheme();
        }
        profile_aboutTextPaint.setTextSize(AndroidUtilities.dp(16.0f));
    }

    public static void applyProfileTheme() {
        if (profile_verifiedDrawable == null) {
            return;
        }
        profile_aboutTextPaint.setColor(getColor(key_windowBackgroundWhiteBlackText));
        ((android.text.TextPaint) profile_aboutTextPaint).linkColor = getColor(key_windowBackgroundWhiteLinkText);
        setDrawableColorByKey(profile_verifiedDrawable, key_profile_verifiedBackground);
        setDrawableColorByKey(profile_verifiedCheckDrawable, key_profile_verifiedCheck);
    }

    public static Drawable getThemedDrawableByKey(Context context, int i, int i2, ResourcesProvider resourcesProvider) {
        return getThemedDrawable(context, i, getColor(i2, resourcesProvider));
    }

    public static Drawable getThemedDrawableByKey(Context context, int i, int i2) {
        return getThemedDrawable(context, i, getColor(i2));
    }

    public static Drawable getThemedDrawable(Context context, int i, int i2) {
        if (context == null) {
            return null;
        }
        Drawable drawableMutate = context.getResources().getDrawable(i).mutate();
        drawableMutate.setColorFilter(new PorterDuffColorFilter(i2, PorterDuff.Mode.MULTIPLY));
        return drawableMutate;
    }

    public static int getDefaultColor(int i) {
        int i2 = defaultColors[i];
        if (i2 != 0) {
            return i2;
        }
        if (isMyMessagesBubbles(i) || i == key_chats_menuTopShadow || i == key_chats_menuTopBackground || i == key_chats_menuTopShadowCats || i == key_chat_wallpaper_gradient_to2 || i == key_chat_wallpaper_gradient_to3) {
            return 0;
        }
        return org.mvel2.asm.Opcodes.V_PREVIEW;
    }

    public static boolean hasThemeKey(int i) {
        return currentColors.indexOfKey(i) >= 0;
    }

    public static void setAnimatingColor(boolean z) {
        animatingColors = z ? new SparseIntArray() : null;
    }

    public static boolean isAnimatingColor() {
        return animatingColors != null;
    }

    public static void setAnimatedColor(int i, int i2) {
        Paint paint;
        SparseIntArray sparseIntArray = animatingColors;
        if (sparseIntArray == null) {
            return;
        }
        sparseIntArray.put(i, i2);
        if (i != key_divider || (paint = forcedDividerPaint) == null) {
            return;
        }
        paint.setColor(getDividerColor(null));
    }

    public static int getDefaultAccentColor(int i) {
        int iIndexOfKey = currentColorsNoAccent.indexOfKey(i);
        if (iIndexOfKey < 0) {
            return 0;
        }
        int iValueAt = currentColorsNoAccent.valueAt(iIndexOfKey);
        ThemeAccent accent = currentTheme.getAccent(false);
        if (accent == null) {
            return 0;
        }
        float[] tempHsv = getTempHsv(1);
        float[] tempHsv2 = getTempHsv(2);
        Color.colorToHSV(currentTheme.accentBaseColor, tempHsv);
        Color.colorToHSV(accent.accentColor, tempHsv2);
        return changeColorAccent(tempHsv, tempHsv2, iValueAt, currentTheme.isDark(), iValueAt);
    }

    public static int getNonAnimatedColor(int i) {
        return getColor(i, null, true);
    }

    public static int getNonAnimatedColor(int i, ResourcesProvider resourcesProvider) {
        if (i == key_divider && !resolvingDividerColor && ExteraConfig.getDividerStyle() != DividerStyle.LINE) {
            return 16777215;
        }
        if (resourcesProvider != null) {
            return resourcesProvider.getColor(i);
        }
        return getColor(i, null, true);
    }

    public static int getColor(int i, ResourcesProvider resourcesProvider) {
        if (i == key_divider && !resolvingDividerColor && ExteraConfig.getDividerStyle() != DividerStyle.LINE) {
            return 16777215;
        }
        if (resourcesProvider != null) {
            return resourcesProvider.getColor(i);
        }
        return getColor(i);
    }

    public static int getColor(int i) {
        return getColor(i, null, false);
    }

    public static int getColor(int i, boolean[] zArr) {
        return getColor(i, zArr, false);
    }

    public static int getColor(int i, boolean[] zArr, boolean z) {
        if (i != key_divider || resolvingDividerColor || ExteraConfig.getDividerStyle() == DividerStyle.LINE) {
            return getRawColor(i, zArr, z);
        }
        return 16777215;
    }

    public static int getDividerColor(ResourcesProvider resourcesProvider) {
        DividerStyle dividerStyle = ExteraConfig.getDividerStyle();
        if (dividerStyle == DividerStyle.HIDDEN) {
            return 16777215;
        }
        boolean z = true;
        resolvingDividerColor = true;
        try {
            if (resourcesProvider != null) {
                int color = resourcesProvider.getColor(key_divider);
                resolvingDividerColor = false;
                return color;
            }
            int i = key_divider;
            if (dividerStyle == DividerStyle.LINE) {
                z = false;
            }
            int rawColor = getRawColor(i, null, z);
            resolvingDividerColor = false;
            return rawColor;
        } catch (Throwable th) {
            resolvingDividerColor = false;
            throw th;
        }
    }

    private static int getRawColor(int i, boolean[] zArr, boolean z) {
        int iIndexOfKey;
        boolean zIsDefaultMainAccent;
        SparseIntArray sparseIntArray;
        int iIndexOfKey2;
        if (!z && (sparseIntArray = animatingColors) != null && (iIndexOfKey2 = sparseIntArray.indexOfKey(i)) >= 0) {
            return animatingColors.valueAt(iIndexOfKey2);
        }
        if (serviceBitmapShader != null && (key_chat_serviceText == i || key_chat_serviceLink == i || key_chat_serviceIcon == i || key_chat_stickerReplyLine == i || key_chat_stickerReplyNameText == i || key_chat_stickerReplyMessageText == i)) {
            return -1;
        }
        if (currentTheme == defaultTheme) {
            if (isMyMessagesBubbles(i)) {
                zIsDefaultMainAccent = currentTheme.isDefaultMyMessagesBubbles();
            } else if (isMyMessages(i)) {
                zIsDefaultMainAccent = currentTheme.isDefaultMyMessages();
            } else {
                zIsDefaultMainAccent = (key_chat_wallpaper == i || key_chat_wallpaper_gradient_to1 == i || key_chat_wallpaper_gradient_to2 == i || key_chat_wallpaper_gradient_to3 == i) ? false : currentTheme.isDefaultMainAccent();
            }
            if (zIsDefaultMainAccent) {
                if (i == key_chat_serviceBackground) {
                    return serviceMessageColor;
                }
                if (i == key_chat_serviceBackgroundSelected) {
                    return serviceSelectedMessageColor;
                }
                return getDefaultColor(i);
            }
        }
        int iIndexOfKey3 = currentColors.indexOfKey(i);
        if (iIndexOfKey3 < 0) {
            int i2 = fallbackKeys.get(i, -1);
            if (i2 != -1 && (iIndexOfKey = currentColors.indexOfKey(i2)) >= 0) {
                return currentColors.valueAt(iIndexOfKey);
            }
            if (zArr != null) {
                zArr[0] = true;
            }
            if (i == key_chat_serviceBackground) {
                return serviceMessageColor;
            }
            if (i == key_chat_serviceBackgroundSelected) {
                return serviceSelectedMessageColor;
            }
            return getDefaultColor(i);
        }
        int iValueAt = currentColors.valueAt(iIndexOfKey3);
        return (key_windowBackgroundWhite == i || key_windowBackgroundGray == i || key_actionBarDefault == i || key_actionBarDefaultArchived == i) ? (-16777216) | iValueAt : iValueAt;
    }

    private static boolean isMyMessagesBubbles(int i) {
        return i >= myMessagesBubblesStartIndex && i < myMessagesBubblesEndIndex;
    }

    private static boolean isMyMessages(int i) {
        return i >= myMessagesStartIndex && i < myMessagesEndIndex;
    }

    public static void setColor(int i, int i2, boolean z) {
        int i3 = key_chat_wallpaper;
        if (i == i3 || i == key_chat_wallpaper_gradient_to1 || i == key_chat_wallpaper_gradient_to2 || i == key_chat_wallpaper_gradient_to3 || i == key_windowBackgroundWhite || i == key_windowBackgroundGray || i == key_actionBarDefault || i == key_actionBarDefaultArchived) {
            i2 |= -16777216;
        }
        if (z) {
            currentColors.delete(i);
        } else {
            currentColors.put(i, i2);
        }
        if (i == key_chat_selectedBackground) {
            applyChatMessageSelectedBackgroundColor();
            return;
        }
        if (i == key_chat_serviceBackground || i == key_chat_serviceBackgroundSelected) {
            applyChatServiceMessageColor();
            return;
        }
        if (i == i3 || i == key_chat_wallpaper_gradient_to1 || i == key_chat_wallpaper_gradient_to2 || i == key_chat_wallpaper_gradient_to3 || i == key_chat_wallpaper_gradient_rotation) {
            reloadWallpaper(true);
            return;
        }
        if (i == key_actionBarDefault) {
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
        } else {
            if (i != key_windowBackgroundGray || Build.VERSION.SDK_INT < 26) {
                return;
            }
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
        }
    }

    public static void setDefaultColor(int i, int i2) {
        defaultColors[i] = i2;
    }

    public static void setThemeWallpaper(ThemeInfo themeInfo, Bitmap bitmap, File file) throws Throwable {
        currentColors.delete(key_chat_wallpaper);
        currentColors.delete(key_chat_wallpaper_gradient_to1);
        currentColors.delete(key_chat_wallpaper_gradient_to2);
        currentColors.delete(key_chat_wallpaper_gradient_to3);
        currentColors.delete(key_chat_wallpaper_gradient_rotation);
        themedWallpaperLink = null;
        themeInfo.setOverrideWallpaper(null);
        if (bitmap != null) {
            themedWallpaper = new BitmapDrawable(bitmap);
            saveCurrentTheme(themeInfo, false, false, false);
            calcBackgroundColor(themedWallpaper, 0);
            applyChatServiceMessageColor();
            applyChatMessageSelectedBackgroundColor();
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetNewWallpapper, new Object[0]);
            return;
        }
        themedWallpaper = null;
        wallpaper = null;
        saveCurrentTheme(themeInfo, false, false, false);
        reloadWallpaper(true);
    }

    public static void setDrawableColor(Drawable drawable, int i) {
        if (drawable == null) {
            return;
        }
        if (drawable instanceof StatusDrawable) {
            ((StatusDrawable) drawable).setColor(i);
            return;
        }
        if (drawable instanceof MsgClockDrawable) {
            ((MsgClockDrawable) drawable).setColor(i);
            return;
        }
        if (drawable instanceof ShapeDrawable) {
            ((ShapeDrawable) drawable).getPaint().setColor(i);
        } else if (drawable instanceof ScamDrawable) {
            ((ScamDrawable) drawable).setColor(i);
        } else {
            drawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        }
    }

    public static void setDrawableColorByKey(Drawable drawable, int i) {
        setDrawableColor(drawable, getColor(i));
    }

    public static void setEmojiDrawableColor(Drawable drawable, int i, boolean z) {
        Drawable stateDrawable;
        if (drawable instanceof StateListDrawable) {
            try {
                if (z) {
                    stateDrawable = getStateDrawable(drawable, 0);
                } else {
                    stateDrawable = getStateDrawable(drawable, 1);
                }
                if (stateDrawable instanceof ShapeDrawable) {
                    ((ShapeDrawable) stateDrawable).getPaint().setColor(i);
                } else {
                    stateDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                }
            } catch (Throwable unused) {
            }
        }
    }

    @SuppressLint({"DiscouragedPrivateApi"})
    public static void setRippleDrawableForceSoftware(RippleDrawable rippleDrawable) {
        if (rippleDrawable == null) {
            return;
        }
        try {
            RippleDrawable.class.getDeclaredMethod("setForceSoftware", Boolean.TYPE).invoke(rippleDrawable, Boolean.TRUE);
        } catch (Throwable unused) {
        }
    }

    public static boolean setSelectorDrawableColor(Drawable drawable, int i, boolean z) {
        Drawable stateDrawable;
        boolean z2;
        boolean z3 = true;
        if (drawable instanceof StateListDrawable) {
            try {
                if (z) {
                    Drawable stateDrawable2 = getStateDrawable(drawable, 0);
                    if (stateDrawable2 instanceof ShapeDrawable) {
                        z2 = ((ShapeDrawable) stateDrawable2).getPaint().getColor() != i;
                        try {
                            ((ShapeDrawable) stateDrawable2).getPaint().setColor(i);
                        } catch (Throwable unused) {
                            return z2;
                        }
                    } else {
                        stateDrawable2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                        z2 = false;
                    }
                    stateDrawable = getStateDrawable(drawable, 1);
                } else {
                    stateDrawable = getStateDrawable(drawable, 2);
                    z2 = false;
                }
                if (stateDrawable instanceof ShapeDrawable) {
                    if (((ShapeDrawable) stateDrawable).getPaint().getColor() == i && !z2) {
                        z3 = false;
                    }
                    try {
                        ((ShapeDrawable) stateDrawable).getPaint().setColor(i);
                        return z3;
                    } catch (Throwable unused2) {
                        return z3;
                    }
                }
                stateDrawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
                return z2;
            } catch (Throwable unused3) {
                return false;
            }
        }
        if (drawable instanceof RippleDrawable) {
            RippleDrawable rippleDrawable = (RippleDrawable) drawable;
            if (z) {
                rippleDrawable.setColor(new ColorStateList(new int[][]{StateSet.WILD_CARD}, new int[]{i}));
                return false;
            }
            if (rippleDrawable.getNumberOfLayers() > 0) {
                Drawable drawable2 = rippleDrawable.getDrawable(0);
                if (drawable2 instanceof ShapeDrawable) {
                    ShapeDrawable shapeDrawable = (ShapeDrawable) drawable2;
                    z3 = shapeDrawable.getPaint().getColor() != i;
                    shapeDrawable.getPaint().setColor(i);
                    return z3;
                }
                drawable2.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
            }
        }
        return false;
    }

    public static boolean isThemeWallpaperPublic() {
        return !TextUtils.isEmpty(themedWallpaperLink);
    }

    public static boolean hasWallpaperFromTheme() {
        ThemeInfo themeInfo = currentTheme;
        if (themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID) {
            return false;
        }
        return currentColors.indexOfKey(key_chat_wallpaper) >= 0 || themedWallpaperFileOffset > 0 || !TextUtils.isEmpty(themedWallpaperLink);
    }

    public static boolean isCustomTheme() {
        return isCustomTheme;
    }

    public static void reloadWallpaper(boolean z) {
        BackgroundGradientDrawable.Disposable disposable = backgroundGradientDisposable;
        if (disposable != null) {
            disposable.dispose();
            backgroundGradientDisposable = null;
        }
        Drawable drawable = wallpaper;
        if (drawable instanceof MotionBackgroundDrawable) {
            previousPhase = ((MotionBackgroundDrawable) drawable).getPhase();
        } else {
            previousPhase = 0;
        }
        wallpaper = null;
        themedWallpaper = null;
        loadWallpaper(z);
    }

    private static void calcBackgroundColor(Drawable drawable, int i) {
        if (i != 2) {
            int[] iArrCalcDrawableColor = AndroidUtilities.calcDrawableColor(drawable);
            int i2 = iArrCalcDrawableColor[0];
            serviceMessageColorBackup = i2;
            serviceMessageColor = i2;
            int i3 = iArrCalcDrawableColor[1];
            serviceSelectedMessageColorBackup = i3;
            serviceSelectedMessageColor = i3;
        }
    }

    public static int getServiceMessageColor() {
        int iIndexOfKey = currentColors.indexOfKey(key_chat_serviceBackground);
        if (iIndexOfKey >= 0) {
            return currentColors.valueAt(iIndexOfKey);
        }
        return serviceMessageColor;
    }

    /* JADX WARN: Code duplicated, block: B:37:0x0069  */
    /* JADX WARN: Code duplicated, block: B:39:0x007a  */
    /* JADX WARN: Code duplicated, block: B:41:0x0088  */
    public static void loadWallpaper(boolean z) {
        File file;
        TLRPC.Document document;
        boolean z2;
        float f;
        float f2;
        final int i;
        Drawable drawableLoadWallpaperInternal;
        TLRPC.WallPaper wallPaper;
        if (wallpaper != null) {
            return;
        }
        ThemeInfo themeInfo = currentTheme;
        boolean z3 = themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID;
        ThemeAccent accent = themeInfo.getAccent(false);
        if (accent != null) {
            File pathToWallpaper = accent.getPathToWallpaper();
            boolean z4 = accent.patternMotion;
            TLRPC.TL_theme tL_theme = accent.info;
            TLRPC.ThemeSettings themeSettings = (tL_theme == null || tL_theme.settings.size() <= 0) ? null : accent.info.settings.get(0);
            z2 = z4;
            document = (accent.info == null || themeSettings == null || (wallPaper = themeSettings.wallpaper) == null) ? null : wallPaper.document;
            file = pathToWallpaper;
        } else {
            file = null;
            document = null;
            z2 = false;
        }
        ThemeInfo themeInfo2 = currentTheme;
        final File file2 = file;
        final OverrideWallpaperInfo overrideWallpaperInfo = themeInfo2.overrideWallpaper;
        if (overrideWallpaperInfo == null) {
            if (accent != null) {
                f2 = accent.patternIntensity;
            } else {
                f = themeInfo2.patternIntensity;
            }
            i = (int) f;
            if (z) {
                DispatchQueue dispatchQueue = Utilities.themeQueue;
                final boolean z5 = z3;
                final TLRPC.Document document2 = document;
                final boolean z6 = z2;
                Runnable runnable = new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        Theme.$r8$lambda$gvgZQHt_aksR0W7l4hMLnrufm7A(overrideWallpaperInfo, file2, i, z6, document2, z5);
                    }
                };
                wallpaperLoadTask = runnable;
                dispatchQueue.postRunnable(runnable);
                return;
            }
            drawableLoadWallpaperInternal = loadWallpaperInternal(overrideWallpaperInfo, file2, i, z2, document, z3);
            createCommonChatResources();
            if (!disallowChangeServiceMessageColor) {
                applyChatServiceMessageColor(null, null, drawableLoadWallpaperInternal);
                applyChatMessageSelectedBackgroundColor(null, drawableLoadWallpaperInternal);
            }
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetNewWallpapper, new Object[0]);
        }
        f2 = overrideWallpaperInfo.intensity;
        f = f2 * 100.0f;
        i = (int) f;
        if (z) {
            DispatchQueue dispatchQueue2 = Utilities.themeQueue;
            final boolean z7 = z3;
            final TLRPC.Document document3 = document;
            final boolean z8 = z2;
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    Theme.$r8$lambda$gvgZQHt_aksR0W7l4hMLnrufm7A(overrideWallpaperInfo, file2, i, z8, document3, z7);
                }
            };
            wallpaperLoadTask = runnable2;
            dispatchQueue2.postRunnable(runnable2);
            return;
        }
        drawableLoadWallpaperInternal = loadWallpaperInternal(overrideWallpaperInfo, file2, i, z2, document, z3);
        createCommonChatResources();
        if (!disallowChangeServiceMessageColor) {
            applyChatServiceMessageColor(null, null, drawableLoadWallpaperInternal);
            applyChatMessageSelectedBackgroundColor(null, drawableLoadWallpaperInternal);
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetNewWallpapper, new Object[0]);
    }

    public static /* synthetic */ void $r8$lambda$gvgZQHt_aksR0W7l4hMLnrufm7A(OverrideWallpaperInfo overrideWallpaperInfo, File file, int i, boolean z, TLRPC.Document document, boolean z2) {
        final Drawable drawableLoadWallpaperInternal = loadWallpaperInternal(overrideWallpaperInfo, file, i, z, document, z2);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda8
            @Override // java.lang.Runnable
            public final void run() {
                Theme.$r8$lambda$Mu0Qxx9lco6NYPpjphkArSoB7e8(drawableLoadWallpaperInternal);
            }
        });
    }

    public static /* synthetic */ void $r8$lambda$Mu0Qxx9lco6NYPpjphkArSoB7e8(Drawable drawable) {
        wallpaperLoadTask = null;
        createCommonChatResources();
        if (!disallowChangeServiceMessageColor) {
            applyChatServiceMessageColor(null, null, drawable);
            applyChatMessageSelectedBackgroundColor(null, drawable);
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetNewWallpapper, new Object[0]);
    }

    private static Drawable loadWallpaperInternal(OverrideWallpaperInfo overrideWallpaperInfo, File file, int i, boolean z, TLRPC.Document document, boolean z2) {
        BackgroundDrawableSettings backgroundDrawableSettingsCreateBackgroundDrawable = createBackgroundDrawable(currentTheme, overrideWallpaperInfo, currentColors, file, themedWallpaperLink, themedWallpaperFileOffset, i, previousPhase, z2, hasPreviousTheme, isApplyingAccent, z, document, false);
        Boolean bool = backgroundDrawableSettingsCreateBackgroundDrawable.isWallpaperMotion;
        isWallpaperMotion = bool != null ? bool.booleanValue() : isWallpaperMotion;
        Boolean bool2 = backgroundDrawableSettingsCreateBackgroundDrawable.isPatternWallpaper;
        isPatternWallpaper = bool2 != null ? bool2.booleanValue() : isPatternWallpaper;
        Boolean bool3 = backgroundDrawableSettingsCreateBackgroundDrawable.isCustomTheme;
        isCustomTheme = bool3 != null ? bool3.booleanValue() : isCustomTheme;
        patternIntensity = i;
        Drawable drawable = backgroundDrawableSettingsCreateBackgroundDrawable.wallpaper;
        wallpaper = drawable != null ? drawable : wallpaper;
        calcBackgroundColor(drawable, 1);
        applyChatServiceMessageColor();
        return drawable;
    }

    public static BackgroundDrawableSettings createBackgroundDrawable(ThemeInfo themeInfo, SparseIntArray sparseIntArray, String str, int i, boolean z) {
        float f;
        float f2;
        boolean z2 = themeInfo.firstAccentIsDefault && themeInfo.currentAccentId == DEFALT_THEME_ACCENT_ID;
        ThemeAccent accent = themeInfo.getAccent(false);
        File pathToWallpaper = accent != null ? accent.getPathToWallpaper() : null;
        boolean z3 = accent != null && accent.patternMotion;
        OverrideWallpaperInfo overrideWallpaperInfo = themeInfo.overrideWallpaper;
        if (overrideWallpaperInfo != null) {
            f2 = overrideWallpaperInfo.intensity;
        } else {
            if (accent != null) {
                f2 = accent.patternIntensity;
            } else {
                f = themeInfo.patternIntensity;
            }
            return createBackgroundDrawable(themeInfo, overrideWallpaperInfo, sparseIntArray, pathToWallpaper, str, currentColorsNoAccent.get(key_wallpaperFileOffset, -1), (int) f, i, z2, false, false, z3, null, z);
        }
        f = f2 * 100.0f;
        return createBackgroundDrawable(themeInfo, overrideWallpaperInfo, sparseIntArray, pathToWallpaper, str, currentColorsNoAccent.get(key_wallpaperFileOffset, -1), (int) f, i, z2, false, false, z3, null, z);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r11v10 */
    /* JADX WARN: Type inference failed for: r11v11 */
    /* JADX WARN: Type inference failed for: r11v12 */
    /* JADX WARN: Type inference failed for: r11v13 */
    /* JADX WARN: Type inference failed for: r11v14 */
    /* JADX WARN: Type inference failed for: r11v15 */
    /* JADX WARN: Type inference failed for: r11v16 */
    /* JADX WARN: Type inference failed for: r11v17 */
    /* JADX WARN: Type inference failed for: r11v18 */
    /* JADX WARN: Type inference failed for: r11v29 */
    /* JADX WARN: Type inference failed for: r11v30 */
    /* JADX WARN: Type inference failed for: r11v31 */
    /* JADX WARN: Type inference failed for: r11v32 */
    /* JADX WARN: Type inference failed for: r11v33 */
    /* JADX WARN: Type inference failed for: r11v7 */
    /* JADX WARN: Type inference failed for: r11v8 */
    /* JADX WARN: Type inference failed for: r11v9, types: [int] */
    /* JADX WARN: Type inference failed for: r3v7, types: [android.graphics.drawable.Drawable, org.telegram.ui.Components.MotionBackgroundDrawable] */
    public static BackgroundDrawableSettings createBackgroundDrawable(ThemeInfo themeInfo, OverrideWallpaperInfo overrideWallpaperInfo, SparseIntArray sparseIntArray, File file, String str, int i, int i2, int i3, boolean z, boolean z2, boolean z3, boolean z4, TLRPC.Document document, boolean z5) {
        ?? r11;
        ?? r12;
        int height;
        int iMin;
        Bitmap bitmapLoadScreenSizedBitmap;
        Bitmap bitmapLoadScreenSizedBitmap2;
        int i4;
        int i5;
        boolean z6;
        File file2;
        Bitmap bitmap;
        Bitmap bitmap2;
        BackgroundDrawableSettings backgroundDrawableSettings = new BackgroundDrawableSettings();
        backgroundDrawableSettings.wallpaper = z5 ? null : wallpaper;
        boolean z7 = (!z2 || z3) && overrideWallpaperInfo != null;
        if (overrideWallpaperInfo != null) {
            backgroundDrawableSettings.isWallpaperMotion = Boolean.valueOf(overrideWallpaperInfo.isMotion);
            Boolean boolValueOf = Boolean.valueOf((overrideWallpaperInfo.color == 0 || overrideWallpaperInfo.isDefault() || overrideWallpaperInfo.isColor()) ? false : true);
            backgroundDrawableSettings.isPatternWallpaper = boolValueOf;
            r11 = boolValueOf;
        } else {
            backgroundDrawableSettings.isWallpaperMotion = Boolean.valueOf(themeInfo.isMotion);
            Boolean boolValueOf2 = Boolean.valueOf(themeInfo.patternBgColor != 0);
            backgroundDrawableSettings.isPatternWallpaper = boolValueOf2;
            r11 = boolValueOf2;
        }
        if (z7) {
            r12 = i3;
        } else {
            int i6 = z ? 0 : sparseIntArray.get(key_chat_wallpaper);
            int i7 = sparseIntArray.get(key_chat_wallpaper_gradient_to3);
            int i8 = sparseIntArray.get(key_chat_wallpaper_gradient_to2);
            int i9 = sparseIntArray.get(key_chat_wallpaper_gradient_to1);
            ThemeAccent accent = themeInfo.getAccent(false);
            if (i6 != 0 && MonetAccentHelper.isFallbackPattern(accent)) {
                backgroundDrawableSettings.wallpaper = MonetAccentHelper.createFallbackPatternDrawable(i6, i9, i8, i7, sparseIntArray.get(key_chat_wallpaper_gradient_rotation, 45), i2, false, i3);
                backgroundDrawableSettings.isWallpaperMotion = Boolean.valueOf(z4);
                Boolean bool = Boolean.TRUE;
                backgroundDrawableSettings.isPatternWallpaper = bool;
                backgroundDrawableSettings.isCustomTheme = bool;
                return backgroundDrawableSettings;
            }
            if (file == null || !file.exists()) {
                i4 = i2;
                r12 = i3;
                i5 = 45;
                z6 = false;
            } else {
                try {
                    if (i6 != 0 && i9 != 0 && i8 != 0) {
                        try {
                            i5 = 45;
                            i4 = i2;
                            r11 = i3;
                            MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(i6, i9, i8, i7, false);
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            Bitmap.Config config = Bitmap.Config.ALPHA_8;
                            options.inPreferredConfig = config;
                            Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
                            if (bitmapDecodeFile != null && bitmapDecodeFile.getConfig() != config) {
                                Bitmap bitmapCopy = bitmapDecodeFile.copy(config, false);
                                bitmapDecodeFile.recycle();
                                bitmapDecodeFile = bitmapCopy;
                            }
                            z6 = bitmapDecodeFile != null;
                            try {
                                motionBackgroundDrawable.setPatternBitmap(i4, bitmapDecodeFile);
                                motionBackgroundDrawable.setPatternColorFilter(motionBackgroundDrawable.getPatternColor());
                                backgroundDrawableSettings.wallpaper = motionBackgroundDrawable;
                                r11 = r11;
                            } catch (Throwable th) {
                                th = th;
                                FileLog.e(th);
                                r12 = r11;
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            i4 = i2;
                            r11 = i3;
                            i5 = 45;
                            z6 = true;
                            FileLog.e(th);
                            r12 = r11;
                        }
                    } else {
                        i4 = i2;
                        r11 = i3;
                        i5 = 45;
                        backgroundDrawableSettings.wallpaper = Drawable.createFromPath(file.getAbsolutePath());
                        z6 = true;
                    }
                    backgroundDrawableSettings.isWallpaperMotion = Boolean.valueOf(z4);
                    Boolean bool2 = Boolean.TRUE;
                    backgroundDrawableSettings.isPatternWallpaper = bool2;
                    backgroundDrawableSettings.isCustomTheme = bool2;
                    r12 = r11;
                } catch (Throwable th3) {
                    th = th3;
                }
            }
            if (!z6) {
                if (i6 != 0) {
                    int i10 = sparseIntArray.get(key_chat_wallpaper_gradient_rotation, -1);
                    if (i10 == -1) {
                        i10 = i5;
                    }
                    if (i9 != 0 && i8 != 0) {
                        MotionBackgroundDrawable motionBackgroundDrawable2 = new MotionBackgroundDrawable(i6, i9, i8, i7, false);
                        if (file != null) {
                            Point point = AndroidUtilities.displaySize;
                            int iMin2 = Math.min(point.x, point.y);
                            Point point2 = AndroidUtilities.displaySize;
                            int iMax = Math.max(point2.x, point2.y);
                            if (document != null) {
                                bitmap2 = SvgHelper.getBitmap(FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document, true), iMin2, iMax, false, SvgHelper.ScaleMode.ByWidth);
                            } else {
                                bitmap2 = SvgHelper.getBitmap(R.raw.default_pattern, iMin2, iMax, -1, 1.0f, SvgHelper.ScaleMode.ByWidth);
                            }
                            bitmap = bitmap2;
                            if (bitmap != null) {
                                try {
                                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                                    Bitmap bitmapCopy2 = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                                    bitmapCopy2.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
                                    bitmapCopy2.recycle();
                                    fileOutputStream.close();
                                } catch (Exception e) {
                                    FileLog.e(e);
                                    e.printStackTrace();
                                }
                            }
                        } else {
                            bitmap = null;
                        }
                        motionBackgroundDrawable2.setPatternBitmap(i4, bitmap);
                        motionBackgroundDrawable2.setPhase(r12 == true ? 1 : 0);
                        backgroundDrawableSettings.wallpaper = motionBackgroundDrawable2;
                    } else if (i9 == 0 || i9 == i6) {
                        backgroundDrawableSettings.wallpaper = new ColorDrawable(i6);
                    } else {
                        BackgroundGradientDrawable backgroundGradientDrawable = new BackgroundGradientDrawable(BackgroundGradientDrawable.getGradientOrientation(i10), new int[]{i6, i9});
                        backgroundGradientDisposable = backgroundGradientDrawable.startDithering(BackgroundGradientDrawable.Sizes.ofDeviceScreen(), new BackgroundGradientDrawable.ListenerAdapter() { // from class: org.telegram.ui.ActionBar.Theme.13
                            @Override // org.telegram.ui.Components.BackgroundGradientDrawable.ListenerAdapter, org.telegram.ui.Components.BackgroundGradientDrawable.Listener
                            public void onSizeReady(int i11, int i12) {
                                Point point3 = AndroidUtilities.displaySize;
                                if ((point3.x <= point3.y) == (i11 <= i12)) {
                                    NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetNewWallpapper, new Object[0]);
                                }
                            }
                        }, 100L);
                        backgroundDrawableSettings.wallpaper = backgroundGradientDrawable;
                    }
                    backgroundDrawableSettings.isCustomTheme = Boolean.TRUE;
                } else if (str != null) {
                    try {
                        Bitmap bitmapLoadScreenSizedBitmap3 = loadScreenSizedBitmap(new FileInputStream(new File(ApplicationLoader.getFilesDirFixed(), Utilities.MD5(str) + ".wp")), 0);
                        if (bitmapLoadScreenSizedBitmap3 != null) {
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(bitmapLoadScreenSizedBitmap3);
                            backgroundDrawableSettings.wallpaper = bitmapDrawable;
                            backgroundDrawableSettings.themedWallpaper = bitmapDrawable;
                            backgroundDrawableSettings.isCustomTheme = Boolean.TRUE;
                        }
                    } catch (Exception e2) {
                        FileLog.e(e2);
                    }
                } else if (i > 0 && (themeInfo.pathToFile != null || themeInfo.assetName != null)) {
                    try {
                        String str2 = themeInfo.assetName;
                        if (str2 != null) {
                            file2 = getAssetFile(str2);
                        } else {
                            file2 = new File(themeInfo.pathToFile);
                        }
                        Bitmap bitmapLoadScreenSizedBitmap4 = loadScreenSizedBitmap(new FileInputStream(file2), i);
                        if (bitmapLoadScreenSizedBitmap4 != null) {
                            BitmapDrawable bitmapDrawable2 = new BitmapDrawable(bitmapLoadScreenSizedBitmap4);
                            wallpaper = bitmapDrawable2;
                            backgroundDrawableSettings.themedWallpaper = bitmapDrawable2;
                            backgroundDrawableSettings.wallpaper = bitmapDrawable2;
                            bitmapDrawable2.setFilterBitmap(true);
                            backgroundDrawableSettings.isCustomTheme = Boolean.TRUE;
                        }
                    } catch (Throwable th4) {
                        FileLog.e(th4);
                    }
                }
            }
        }
        if (backgroundDrawableSettings.wallpaper == null) {
            int i11 = overrideWallpaperInfo != null ? overrideWallpaperInfo.color : 0;
            if (overrideWallpaperInfo != null) {
                try {
                    if (overrideWallpaperInfo.isDefault()) {
                        backgroundDrawableSettings.wallpaper = createDefaultWallpaper();
                        backgroundDrawableSettings.isCustomTheme = Boolean.FALSE;
                    } else if (!overrideWallpaperInfo.isColor() || overrideWallpaperInfo.gradientColor1 != 0) {
                        if (i11 != 0 && (!isPatternWallpaper || overrideWallpaperInfo.gradientColor2 != 0)) {
                            if (overrideWallpaperInfo.gradientColor1 != 0 && overrideWallpaperInfo.gradientColor2 != 0) {
                                ?? motionBackgroundDrawable3 = new MotionBackgroundDrawable(overrideWallpaperInfo.color, overrideWallpaperInfo.gradientColor1, overrideWallpaperInfo.gradientColor2, overrideWallpaperInfo.gradientColor3, false);
                                motionBackgroundDrawable3.setPhase(r12);
                                if (backgroundDrawableSettings.isPatternWallpaper.booleanValue()) {
                                    File file3 = new File(ApplicationLoader.getFilesDirFixed(), overrideWallpaperInfo.fileName);
                                    if (file3.exists()) {
                                        motionBackgroundDrawable3.setPatternBitmap((int) (overrideWallpaperInfo.intensity * 100.0f), loadScreenSizedBitmap(new FileInputStream(file3), 0));
                                        backgroundDrawableSettings.isCustomTheme = Boolean.TRUE;
                                    }
                                }
                                backgroundDrawableSettings.wallpaper = motionBackgroundDrawable3;
                            } else if (backgroundDrawableSettings.isPatternWallpaper.booleanValue()) {
                                File file4 = new File(ApplicationLoader.getFilesDirFixed(), overrideWallpaperInfo.fileName);
                                if (file4.exists() && (bitmapLoadScreenSizedBitmap2 = loadScreenSizedBitmap(new FileInputStream(file4), 0)) != null) {
                                    BitmapDrawable bitmapDrawable3 = new BitmapDrawable(bitmapLoadScreenSizedBitmap2);
                                    backgroundDrawableSettings.wallpaper = bitmapDrawable3;
                                    bitmapDrawable3.setFilterBitmap(true);
                                    backgroundDrawableSettings.isCustomTheme = Boolean.TRUE;
                                }
                            } else {
                                int i12 = overrideWallpaperInfo.gradientColor1;
                                if (i12 != 0) {
                                    BackgroundGradientDrawable backgroundGradientDrawable2 = new BackgroundGradientDrawable(BackgroundGradientDrawable.getGradientOrientation(overrideWallpaperInfo.rotation), new int[]{i11, i12});
                                    backgroundGradientDisposable = backgroundGradientDrawable2.startDithering(BackgroundGradientDrawable.Sizes.ofDeviceScreen(), new BackgroundGradientDrawable.ListenerAdapter() { // from class: org.telegram.ui.ActionBar.Theme.14
                                        @Override // org.telegram.ui.Components.BackgroundGradientDrawable.ListenerAdapter, org.telegram.ui.Components.BackgroundGradientDrawable.Listener
                                        public void onSizeReady(int i13, int i14) {
                                            Point point3 = AndroidUtilities.displaySize;
                                            if ((point3.x <= point3.y) == (i13 <= i14)) {
                                                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didSetNewWallpapper, new Object[0]);
                                            }
                                        }
                                    }, 100L);
                                    backgroundDrawableSettings.wallpaper = backgroundGradientDrawable2;
                                } else {
                                    backgroundDrawableSettings.wallpaper = new ColorDrawable(i11);
                                }
                            }
                        } else {
                            File file5 = new File(ApplicationLoader.getFilesDirFixed(), overrideWallpaperInfo.fileName);
                            if (file5.exists() && (bitmapLoadScreenSizedBitmap = loadScreenSizedBitmap(new FileInputStream(file5), 0)) != null) {
                                BitmapDrawable bitmapDrawable4 = new BitmapDrawable(bitmapLoadScreenSizedBitmap);
                                backgroundDrawableSettings.wallpaper = bitmapDrawable4;
                                bitmapDrawable4.setFilterBitmap(true);
                                backgroundDrawableSettings.isCustomTheme = Boolean.TRUE;
                            }
                            if (backgroundDrawableSettings.wallpaper == null) {
                                backgroundDrawableSettings.wallpaper = createDefaultWallpaper();
                                backgroundDrawableSettings.isCustomTheme = Boolean.FALSE;
                            }
                        }
                    }
                } catch (Throwable unused) {
                }
            } else {
                backgroundDrawableSettings.wallpaper = createDefaultWallpaper();
                backgroundDrawableSettings.isCustomTheme = Boolean.FALSE;
            }
            if (backgroundDrawableSettings.wallpaper == null) {
                if (i11 == 0) {
                    i11 = -2693905;
                }
                backgroundDrawableSettings.wallpaper = new ColorDrawable(i11);
            }
        }
        if (!LiteMode.isEnabled(32)) {
            Drawable drawable = backgroundDrawableSettings.wallpaper;
            if (drawable instanceof MotionBackgroundDrawable) {
                MotionBackgroundDrawable motionBackgroundDrawable4 = (MotionBackgroundDrawable) drawable;
                if (motionBackgroundDrawable4.getPatternBitmap() == null) {
                    Point point3 = AndroidUtilities.displaySize;
                    iMin = Math.min(point3.x, point3.y);
                    Point point4 = AndroidUtilities.displaySize;
                    height = Math.max(point4.x, point4.y);
                } else {
                    int width = motionBackgroundDrawable4.getPatternBitmap().getWidth();
                    height = motionBackgroundDrawable4.getPatternBitmap().getHeight();
                    iMin = width;
                }
                Bitmap bitmapCreateBitmap = Bitmap.createBitmap(iMin, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmapCreateBitmap);
                backgroundDrawableSettings.wallpaper.setBounds(0, 0, bitmapCreateBitmap.getWidth(), bitmapCreateBitmap.getHeight());
                backgroundDrawableSettings.wallpaper.draw(canvas);
                backgroundDrawableSettings.wallpaper = new BitmapDrawable(bitmapCreateBitmap);
            }
        }
        return backgroundDrawableSettings;
    }

    public static Drawable createDefaultWallpaper() {
        return createDefaultWallpaper(0, 0);
    }

    public static Drawable createDefaultWallpaper(int i, int i2) {
        MotionBackgroundDrawable motionBackgroundDrawable = new MotionBackgroundDrawable(-2368069, -9722489, -2762611, -7817084, i != 0);
        if (i <= 0 || i2 <= 0) {
            Point point = AndroidUtilities.displaySize;
            i = Math.min(point.x, point.y);
            Point point2 = AndroidUtilities.displaySize;
            i2 = Math.max(point2.x, point2.y);
        }
        motionBackgroundDrawable.setPatternBitmap(34, SvgHelper.getBitmap(R.raw.default_pattern, i, i2, -16777216, 1.0f, SvgHelper.ScaleMode.ByWidth));
        motionBackgroundDrawable.setPatternColorFilter(motionBackgroundDrawable.getPatternColor());
        return motionBackgroundDrawable;
    }

    public static Bitmap loadScreenSizedBitmap(FileInputStream fileInputStream, int i) {
        float fMin;
        int i2;
        try {
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = true;
                long j = i;
                fileInputStream.getChannel().position(j);
                BitmapFactory.decodeStream(fileInputStream, null, options);
                float f = options.outWidth;
                float f2 = options.outHeight;
                Point point = AndroidUtilities.displaySize;
                int iMin = Math.min(point.x, point.y);
                Point point2 = AndroidUtilities.displaySize;
                int iMax = Math.max(point2.x, point2.y);
                if (iMin >= iMax && f > f2) {
                    fMin = Math.max(f / iMin, f2 / iMax);
                } else {
                    fMin = Math.min(f / iMin, f2 / iMax);
                }
                if (fMin < 1.2f) {
                    fMin = 1.0f;
                }
                options.inJustDecodeBounds = false;
                if (fMin > 1.0f && (f > iMin || f2 > iMax)) {
                    int i3 = 1;
                    while (true) {
                        i2 = i3 * 2;
                        if (i3 * 4 >= fMin) {
                            break;
                        }
                        i3 = i2;
                    }
                    options.inSampleSize = i2;
                } else {
                    options.inSampleSize = (int) fMin;
                }
                fileInputStream.getChannel().position(j);
                Bitmap bitmapDecodeStream = BitmapFactory.decodeStream(fileInputStream, null, options);
                if (bitmapDecodeStream.getWidth() < iMin || bitmapDecodeStream.getHeight() < iMax) {
                    float fMax = Math.max(iMin / bitmapDecodeStream.getWidth(), iMax / bitmapDecodeStream.getHeight());
                    if (fMax >= 1.02f) {
                        Bitmap bitmapCreateScaledBitmap = Bitmap.createScaledBitmap(bitmapDecodeStream, (int) (bitmapDecodeStream.getWidth() * fMax), (int) (bitmapDecodeStream.getHeight() * fMax), true);
                        bitmapDecodeStream.recycle();
                        try {
                            fileInputStream.close();
                        } catch (Exception unused) {
                        }
                        return bitmapCreateScaledBitmap;
                    }
                }
                try {
                    fileInputStream.close();
                } catch (Exception unused2) {
                }
                return bitmapDecodeStream;
            } catch (Throwable th) {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (Exception unused3) {
                    }
                }
                throw th;
            }
        } catch (Exception e) {
            FileLog.e(e);
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (Exception unused4) {
                }
            }
            return null;
        }
    }

    /* JADX WARN: Code duplicated, block: B:127:0x00f9 A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /* JADX WARN: Code duplicated, block: B:70:0x010e A[Catch: all -> 0x012a, TryCatch #5 {all -> 0x012a, blocks: (B:68:0x00fe, B:70:0x010e, B:71:0x011a, B:75:0x0123, B:78:0x012d, B:80:0x013b, B:82:0x0143, B:85:0x014c, B:87:0x0152, B:88:0x015a, B:95:0x0170), top: B:129:0x00fe }] */
    /* JADX WARN: Code duplicated, block: B:80:0x013b A[Catch: all -> 0x012a, TryCatch #5 {all -> 0x012a, blocks: (B:68:0x00fe, B:70:0x010e, B:71:0x011a, B:75:0x0123, B:78:0x012d, B:80:0x013b, B:82:0x0143, B:85:0x014c, B:87:0x0152, B:88:0x015a, B:95:0x0170), top: B:129:0x00fe }] */
    /* JADX WARN: Code duplicated, block: B:82:0x0143 A[Catch: all -> 0x012a, TryCatch #5 {all -> 0x012a, blocks: (B:68:0x00fe, B:70:0x010e, B:71:0x011a, B:75:0x0123, B:78:0x012d, B:80:0x013b, B:82:0x0143, B:85:0x014c, B:87:0x0152, B:88:0x015a, B:95:0x0170), top: B:129:0x00fe }] */
    /* JADX WARN: Code duplicated, block: B:83:0x0148  */
    /* JADX WARN: Code duplicated, block: B:94:0x016e A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:95:0x0170 A[Catch: all -> 0x012a, TRY_ENTER, TRY_LEAVE, TryCatch #5 {all -> 0x012a, blocks: (B:68:0x00fe, B:70:0x010e, B:71:0x011a, B:75:0x0123, B:78:0x012d, B:80:0x013b, B:82:0x0143, B:85:0x014c, B:87:0x0152, B:88:0x015a, B:95:0x0170), top: B:129:0x00fe }] */
    public static Drawable getThemedWallpaper(final boolean z, final View view) {
        MotionBackgroundDrawable motionBackgroundDrawable;
        File assetFile;
        int i;
        Throwable th;
        FileInputStream fileInputStream;
        BitmapFactory.Options options;
        int i2;
        Bitmap.Config config;
        Bitmap bitmapDecodeStream;
        BitmapDrawable bitmapDrawable;
        ThemeAccent accent;
        int i3;
        float f;
        float f2;
        int iDp;
        float f3;
        File pathToWallpaper;
        int i4 = currentColors.get(key_chat_wallpaper);
        if (i4 != 0) {
            int i5 = currentColors.get(key_chat_wallpaper_gradient_to1);
            int i6 = currentColors.get(key_chat_wallpaper_gradient_to2);
            int i7 = currentColors.get(key_chat_wallpaper_gradient_to3);
            int i8 = currentColors.get(key_chat_wallpaper_gradient_rotation, -1);
            if (i8 == -1) {
                i8 = 45;
            }
            ThemeAccent accent2 = currentTheme.getAccent(false);
            int i9 = accent2 != null ? (int) (accent2.patternIntensity * 100.0f) : 34;
            if (i5 == 0) {
                return (MonetAccentHelper.isFallbackPattern(accent2) && previousTheme == null) ? MonetAccentHelper.createFallbackPatternDrawable(i4, 0, 0, 0, i8, i9, true, 0) : new ColorDrawable(i4);
            }
            int i10 = i9;
            if (MonetAccentHelper.isFallbackPattern(accent2) && previousTheme == null) {
                return MonetAccentHelper.createFallbackPatternDrawable(i4, i5, i6, i7, i8, i10, true, 0);
            }
            assetFile = (accent2 == null || TextUtils.isEmpty(accent2.patternSlug) || previousTheme != null || (pathToWallpaper = accent2.getPathToWallpaper()) == null || !pathToWallpaper.exists()) ? null : pathToWallpaper;
            if (i6 != 0) {
                motionBackgroundDrawable = new MotionBackgroundDrawable(i4, i5, i6, i7, true);
                if (assetFile == null) {
                    return motionBackgroundDrawable;
                }
            } else {
                int i11 = i8;
                if (assetFile == null) {
                    BackgroundGradientDrawable backgroundGradientDrawable = new BackgroundGradientDrawable(BackgroundGradientDrawable.getGradientOrientation(i11), new int[]{i4, i5});
                    backgroundGradientDrawable.startDithering(!z ? BackgroundGradientDrawable.Sizes.ofDeviceScreen() : BackgroundGradientDrawable.Sizes.ofDeviceScreen(0.125f, BackgroundGradientDrawable.Sizes.Orientation.PORTRAIT), view != null ? new BackgroundGradientDrawable.ListenerAdapter() { // from class: org.telegram.ui.ActionBar.Theme.15
                        @Override // org.telegram.ui.Components.BackgroundGradientDrawable.ListenerAdapter, org.telegram.ui.Components.BackgroundGradientDrawable.Listener
                        public void onSizeReady(int i12, int i13) {
                            if (!z) {
                                Point point = AndroidUtilities.displaySize;
                                if ((point.x <= point.y) == (i12 <= i13)) {
                                    view.invalidate();
                                    return;
                                }
                                return;
                            }
                            view.invalidate();
                        }
                    } : null);
                    return backgroundGradientDrawable;
                }
                motionBackgroundDrawable = null;
            }
        } else {
            if (themedWallpaperFileOffset > 0) {
                ThemeInfo themeInfo = currentTheme;
                if (themeInfo.pathToFile != null || themeInfo.assetName != null) {
                    String str = themeInfo.assetName;
                    assetFile = str != null ? getAssetFile(str) : new File(currentTheme.pathToFile);
                    i = themedWallpaperFileOffset;
                    motionBackgroundDrawable = null;
                }
                if (assetFile != null) {
                    try {
                        fileInputStream = new FileInputStream(assetFile);
                        try {
                            fileInputStream.getChannel().position(i);
                            options = new BitmapFactory.Options();
                            i2 = 1;
                            if (z) {
                                options.inJustDecodeBounds = true;
                                f = options.outWidth;
                                f2 = options.outHeight;
                                iDp = AndroidUtilities.dp(100.0f);
                                while (true) {
                                    f3 = iDp;
                                    if (f > f3 && f2 <= f3) {
                                        break;
                                    }
                                    i2 *= 2;
                                    f /= 2.0f;
                                    f2 /= 2.0f;
                                }
                            }
                            config = Bitmap.Config.ALPHA_8;
                            options.inPreferredConfig = config;
                            options.inJustDecodeBounds = false;
                            options.inSampleSize = i2;
                            bitmapDecodeStream = BitmapFactory.decodeStream(fileInputStream, null, options);
                            if (motionBackgroundDrawable != null) {
                                accent = currentTheme.getAccent(false);
                                if (accent != null) {
                                    i3 = (int) (accent.patternIntensity * 100.0f);
                                } else {
                                    i3 = 100;
                                }
                                if (bitmapDecodeStream != null && bitmapDecodeStream.getConfig() != config) {
                                    Bitmap bitmapCopy = bitmapDecodeStream.copy(config, false);
                                    bitmapDecodeStream.recycle();
                                    bitmapDecodeStream = bitmapCopy;
                                }
                                motionBackgroundDrawable.setPatternBitmap(i3, bitmapDecodeStream);
                                motionBackgroundDrawable.setPatternColorFilter(motionBackgroundDrawable.getPatternColor());
                                try {
                                    fileInputStream.close();
                                    return motionBackgroundDrawable;
                                } catch (Exception e) {
                                    FileLog.e(e);
                                    return motionBackgroundDrawable;
                                }
                            }
                            if (bitmapDecodeStream != null) {
                                bitmapDrawable = new BitmapDrawable(bitmapDecodeStream);
                                try {
                                    fileInputStream.close();
                                    return bitmapDrawable;
                                } catch (Exception e2) {
                                    FileLog.e(e2);
                                    return bitmapDrawable;
                                }
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            try {
                                FileLog.e(th);
                                return null;
                            } finally {
                                if (fileInputStream != null) {
                                    try {
                                        fileInputStream.close();
                                    } catch (Exception e3) {
                                        FileLog.e(e3);
                                    }
                                }
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        fileInputStream = null;
                    }
                }
                return null;
            }
            motionBackgroundDrawable = null;
            assetFile = null;
        }
        i = 0;
        if (assetFile != null) {
            fileInputStream = new FileInputStream(assetFile);
            fileInputStream.getChannel().position(i);
            options = new BitmapFactory.Options();
            i2 = 1;
            if (z) {
                options.inJustDecodeBounds = true;
                f = options.outWidth;
                f2 = options.outHeight;
                iDp = AndroidUtilities.dp(100.0f);
                while (true) {
                    f3 = iDp;
                    if (f > f3) {
                    }
                    i2 *= 2;
                    f /= 2.0f;
                    f2 /= 2.0f;
                }
            }
            config = Bitmap.Config.ALPHA_8;
            options.inPreferredConfig = config;
            options.inJustDecodeBounds = false;
            options.inSampleSize = i2;
            bitmapDecodeStream = BitmapFactory.decodeStream(fileInputStream, null, options);
            if (motionBackgroundDrawable != null) {
                accent = currentTheme.getAccent(false);
                if (accent != null) {
                    i3 = (int) (accent.patternIntensity * 100.0f);
                } else {
                    i3 = 100;
                }
                if (bitmapDecodeStream != null) {
                    Bitmap bitmapCopy2 = bitmapDecodeStream.copy(config, false);
                    bitmapDecodeStream.recycle();
                    bitmapDecodeStream = bitmapCopy2;
                }
                motionBackgroundDrawable.setPatternBitmap(i3, bitmapDecodeStream);
                motionBackgroundDrawable.setPatternColorFilter(motionBackgroundDrawable.getPatternColor());
                fileInputStream.close();
                return motionBackgroundDrawable;
            }
            if (bitmapDecodeStream != null) {
                bitmapDrawable = new BitmapDrawable(bitmapDecodeStream);
                fileInputStream.close();
                return bitmapDrawable;
            }
        }
        return null;
    }

    public static String getSelectedBackgroundSlug() {
        OverrideWallpaperInfo overrideWallpaperInfo = currentTheme.overrideWallpaper;
        if (overrideWallpaperInfo != null) {
            return overrideWallpaperInfo.slug;
        }
        if (hasWallpaperFromTheme()) {
            return "t";
        }
        return "d";
    }

    public static Drawable getCachedWallpaper() {
        Drawable cachedWallpaperNonBlocking = getCachedWallpaperNonBlocking();
        if (cachedWallpaperNonBlocking != null || wallpaperLoadTask == null) {
            return cachedWallpaperNonBlocking;
        }
        CountDownLatch countDownLatch = new CountDownLatch(1);
        Utilities.themeQueue.postRunnable(new Theme$$ExternalSyntheticLambda9(countDownLatch));
        try {
            countDownLatch.await();
        } catch (Exception e) {
            FileLog.e(e);
        }
        return getCachedWallpaperNonBlocking();
    }

    public static Drawable getCachedWallpaperNonBlocking() {
        Drawable drawable = themedWallpaper;
        return drawable != null ? drawable : wallpaper;
    }

    public static boolean isWallpaperMotion() {
        return isWallpaperMotion;
    }

    public static boolean isPatternWallpaper() {
        String selectedBackgroundSlug = getSelectedBackgroundSlug();
        return isPatternWallpaper || "CJz3BZ6YGEYBAAAABboWp6SAv04".equals(selectedBackgroundSlug) || "qeZWES8rGVIEAAAARfWlK1lnfiI".equals(selectedBackgroundSlug);
    }

    public static BackgroundGradientDrawable getCurrentGradientWallpaper() {
        int i;
        int i2;
        OverrideWallpaperInfo overrideWallpaperInfo = currentTheme.overrideWallpaper;
        if (overrideWallpaperInfo == null || (i = overrideWallpaperInfo.color) == 0 || (i2 = overrideWallpaperInfo.gradientColor1) == 0) {
            return null;
        }
        return new BackgroundGradientDrawable(BackgroundGradientDrawable.getGradientOrientation(overrideWallpaperInfo.rotation), new int[]{i, i2});
    }

    public static AudioVisualizerDrawable getCurrentAudiVisualizerDrawable() {
        if (chat_msgAudioVisualizeDrawable == null) {
            chat_msgAudioVisualizeDrawable = new AudioVisualizerDrawable();
        }
        return chat_msgAudioVisualizeDrawable;
    }

    public static void unrefAudioVisualizeDrawable(final MessageObject messageObject) {
        AudioVisualizerDrawable audioVisualizerDrawable = chat_msgAudioVisualizeDrawable;
        if (audioVisualizerDrawable == null) {
            return;
        }
        if (audioVisualizerDrawable.getParentView() == null || messageObject == null) {
            chat_msgAudioVisualizeDrawable.setParentView(null);
            return;
        }
        if (animatedOutVisualizerDrawables == null) {
            animatedOutVisualizerDrawables = new HashMap<>();
        }
        animatedOutVisualizerDrawables.put(messageObject, chat_msgAudioVisualizeDrawable);
        chat_msgAudioVisualizeDrawable.setWaveform(false, true, null);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                Theme.m5942$r8$lambda$IfQcXDUUx0gKIvGuTBUT9VHuMI(messageObject);
            }
        }, 200L);
        chat_msgAudioVisualizeDrawable = null;
    }

    public static /* synthetic */ void m5942$r8$lambda$IfQcXDUUx0gKIvGuTBUT9VHuMI(MessageObject messageObject) {
        AudioVisualizerDrawable audioVisualizerDrawableRemove = animatedOutVisualizerDrawables.remove(messageObject);
        if (audioVisualizerDrawableRemove != null) {
            audioVisualizerDrawableRemove.setParentView(null);
        }
    }

    public static AudioVisualizerDrawable getAnimatedOutAudioVisualizerDrawable(MessageObject messageObject) {
        HashMap<MessageObject, AudioVisualizerDrawable> map = animatedOutVisualizerDrawables;
        if (map == null || messageObject == null) {
            return null;
        }
        return map.get(messageObject);
    }

    public static StatusDrawable getChatStatusDrawable(int i) {
        if (i < 0 || i > 5) {
            return null;
        }
        StatusDrawable[] statusDrawableArr = chat_status_drawables;
        StatusDrawable statusDrawable = statusDrawableArr[i];
        if (statusDrawable != null) {
            return statusDrawable;
        }
        if (i == 0) {
            statusDrawableArr[0] = new TypingDotsDrawable(true);
        } else if (i == 1) {
            statusDrawableArr[1] = new RecordStatusDrawable(true);
        } else if (i == 2) {
            statusDrawableArr[2] = new SendingFileDrawable(true);
        } else if (i == 3) {
            statusDrawableArr[3] = new PlayingGameDrawable(true, null);
        } else if (i == 4) {
            statusDrawableArr[4] = new RoundStatusDrawable(true);
        } else if (i == 5) {
            statusDrawableArr[5] = new ChoosingStickerStatusDrawable(true);
        }
        StatusDrawable statusDrawable2 = chat_status_drawables[i];
        statusDrawable2.start();
        statusDrawable2.setColor(getColor(key_chats_actionMessage));
        return statusDrawable2;
    }

    public static FragmentContextViewWavesDrawable getFragmentContextViewWavesDrawable() {
        if (fragmentContextViewWavesDrawable == null) {
            fragmentContextViewWavesDrawable = new FragmentContextViewWavesDrawable();
        }
        return fragmentContextViewWavesDrawable;
    }

    public static RoundVideoProgressShadow getRadialSeekbarShadowDrawable() {
        if (roundPlayDrawable == null) {
            roundPlayDrawable = new RoundVideoProgressShadow();
        }
        return roundPlayDrawable;
    }

    public static SparseIntArray getFallbackKeys() {
        return fallbackKeys;
    }

    public static int getFallbackKey(int i) {
        return fallbackKeys.get(i);
    }

    public static Map<String, Drawable> getThemeDrawablesMap() {
        return defaultChatDrawables;
    }

    public static Drawable getThemeDrawable(String str) {
        return defaultChatDrawables.get(str);
    }

    public static Drawable getThemeDrawable(String str, ResourcesProvider resourcesProvider) {
        Drawable drawable = resourcesProvider != null ? resourcesProvider.getDrawable(str) : null;
        return drawable != null ? drawable : defaultChatDrawables.get(str);
    }

    public static int getThemeDrawableColorKey(String str) {
        return defaultChatDrawableColorKeys.get(str).intValue();
    }

    public static Map<String, Paint> getThemePaintsMap() {
        return defaultChatPaints;
    }

    public static Paint getThemePaint(String str) {
        if (Objects.equals(str, "paintDivider")) {
            return dividerPaint;
        }
        return defaultChatPaints.get(str);
    }

    public static int getThemePaintColorKey(String str) {
        return defaultChatPaintColors.get(str).intValue();
    }

    private static void addChatDrawable(String str, Drawable drawable, int i) {
        defaultChatDrawables.put(str, drawable);
        defaultChatDrawableColorKeys.put(str, Integer.valueOf(i));
    }

    private static void addChatPaint(String str, Paint paint, int i) {
        defaultChatPaints.put(str, paint);
        defaultChatPaintColors.put(str, Integer.valueOf(i));
    }

    public static boolean isCurrentThemeDay() {
        return !getActiveTheme().isDark();
    }

    public static boolean isHome(ThemeAccent themeAccent) {
        ThemeInfo themeInfo = themeAccent.parentTheme;
        if (themeInfo != null) {
            if (themeInfo.getKey().equals("Blue") && themeAccent.id == 99) {
                return true;
            }
            if (themeAccent.parentTheme.getKey().equals("Day") && themeAccent.id == 9) {
                return true;
            }
            if ((themeAccent.parentTheme.getKey().equals("Night") || themeAccent.parentTheme.getKey().equals("Dark Blue")) && themeAccent.id == 0) {
                return true;
            }
        }
        return false;
    }

    public static void turnOffAutoNight(final BaseFragment baseFragment) {
        String string;
        if (selectedAutoNightType != 0) {
            if (baseFragment != null) {
                try {
                    BulletinFactory bulletinFactoryOf = BulletinFactory.of(baseFragment);
                    int i = R.raw.auto_night_off;
                    if (selectedAutoNightType == 3) {
                        string = LocaleController.getString("AutoNightSystemModeOff", R.string.AutoNightSystemModeOff);
                    } else {
                        string = LocaleController.getString("AutoNightModeOff", R.string.AutoNightModeOff);
                    }
                    bulletinFactoryOf.createSimpleBulletin(i, string, LocaleController.getString("Settings", R.string.Settings), 5000, new Runnable() { // from class: org.telegram.ui.ActionBar.Theme$$ExternalSyntheticLambda14
                        @Override // java.lang.Runnable
                        public final void run() {
                            baseFragment.presentFragment(new ThemeActivity(1));
                        }
                    }).show();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            selectedAutoNightType = 0;
            saveAutoNightThemeConfig();
            cancelAutoNightThemeCallbacks();
        }
    }

    public static void turnOffAutoNight(BulletinFactory bulletinFactory, Runnable runnable) {
        String string;
        int i = selectedAutoNightType;
        if (i != 0) {
            if (bulletinFactory != null && runnable != null) {
                try {
                    int i2 = R.raw.auto_night_off;
                    if (i == 3) {
                        string = LocaleController.getString("AutoNightSystemModeOff", R.string.AutoNightSystemModeOff);
                    } else {
                        string = LocaleController.getString("AutoNightModeOff", R.string.AutoNightModeOff);
                    }
                    bulletinFactory.createSimpleBulletin(i2, string, LocaleController.getString("Settings", R.string.Settings), 5000, runnable).show();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            selectedAutoNightType = 0;
            saveAutoNightThemeConfig();
            cancelAutoNightThemeCallbacks();
        }
    }

    public static Paint fillingPaint(int i) {
        Paint paint = PAINT_FILLING;
        paint.setColor(i);
        return paint;
    }
}
