package com.exteragram.messenger;

import android.content.SharedPreferences;
import android.util.Log;
import android.util.Pair;
import com.chaquo.python.internal.Common;
import com.exteragram.messenger.adblock.backend.AdBlockManager;
import com.exteragram.messenger.api.ApiController;
import com.exteragram.messenger.backup.PreferencesUtils;
import com.exteragram.messenger.config.BasePref;
import com.exteragram.messenger.config.BooleanPref;
import com.exteragram.messenger.config.BottomNavigationBar;
import com.exteragram.messenger.config.EnumPref;
import com.exteragram.messenger.config.FloatPref;
import com.exteragram.messenger.config.IntegerPref;
import com.exteragram.messenger.config.LongPref;
import com.exteragram.messenger.config.NullableStringPref;
import com.exteragram.messenger.config.PrefClassesKt;
import com.exteragram.messenger.config.SanitizedIntegerPref;
import com.exteragram.messenger.config.StringPref;
import com.exteragram.messenger.config.StringSetPref;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.exteragram.messenger.utils.network.RemoteUtils;
import com.exteragram.messenger.translator.TranslatorUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Predicate;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.NoWhenBranchMatchedException;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmName;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.MutablePropertyReference0Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.math.MathKt;
import kotlin.ranges.RangesKt;
import kotlin.reflect.KProperty;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.web.SearchEngine;

public abstract class ExteraConfig {
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties;
    private static final Gson GSON;
    private static SharedPreferences.OnSharedPreferenceChangeListener apiBotListener;
    private static boolean configLoaded;
    private static volatile Pair<Long, String> currentApiBot;
    private static ArrayList<String> doNotMarkAsNew;
    private static final SharedPreferences.Editor editor;
    private static ArrayList<String> iconPacksHidden;
    private static ArrayList<String> iconPacksLayout;
    private static ArrayList<Integer> mainMenuHiddenItems;
    private static ArrayList<Integer> mainMenuLayout;
    private static HashMap<String, Long> newFeaturesShowedAt;
    private static boolean pluginsEngine;
    private static final SharedPreferences preferences;
    private static final AtomicBoolean servicesStarted;
    private static final Object sync;
    private static final SearchEngine yandexSearchEngine;
    private static final BasePref translationProvider$delegate;
    private static final BasePref disableNumberRounding$delegate;
    private static final BasePref formatTimeWithSeconds$delegate;
    private static final BasePref relativeLastSeen$delegate;
    private static final BasePref inAppVibration$delegate;
    private static final BasePref disableNotificationDelay$delegate;
    private static final BasePref filterZalgo$delegate;
    private static final BasePref useYandexMaps$delegate;
    private static final BasePref downloadSpeedBoost$delegate;
    private static final BasePref uploadSpeedBoost$delegate;
    private static final BasePref hidePhoneNumber$delegate;
    private static final BasePref showIdAndDc$delegate;
    private static final BasePref hideArchiveFolder$delegate;
    private static final BasePref archiveOnPull$delegate;
    private static final BasePref disableUnarchiveSwipe$delegate;
    private static final BasePref doNotUseProxy$delegate;
    private static final BasePref customSavePath$delegate;
    private static final BasePref iconPack$delegate;
    private static final BasePref editingIconPackId$delegate;
    private static final BasePref avatarCorners$delegate;
    private static final BasePref singleCornerRadius$delegate;
    private static final BasePref dividerStyle$delegate;
    private static final BasePref forceSnow$delegate;
    private static final BasePref hideActionBarStatus$delegate;
    private static final BasePref centerTitle$delegate;
    private static final BasePref hideStories$delegate;
    private static final BasePref hideFloatingButton$delegate;
    private static final BasePref hideDialogsSearchBar$delegate;
    private static final BasePref senderMiniAvatars$delegate;
    private static final BasePref titleText$delegate;
    private static final BasePref tabIcons$delegate;
    private static final BasePref tabCounterMode$delegate;
    private static final BasePref hideAllChats$delegate;
    private static final BasePref squareFab$delegate;
    private static final BasePref sectionRadius$delegate;
    private static final BasePref sectionsSeparatedHeadersPreference$delegate;
    private static final BasePref newLoadingStyle$delegate;
    private static final BasePref newSliderStyle$delegate;
    private static final BasePref newSwitchStyle$delegate;
    private static final BasePref newChatHeaderStyle$delegate;
    private static final BasePref newNavigationBarStyle$delegate;
    private static final BasePref newFabStyle$delegate;
    private static final BasePref tabletMode$delegate;
    private static final BasePref useSystemFonts$delegate;
    private static final BasePref gooeyAvatarAnimation$delegate;
    private static final BasePref customThemes$delegate;
    private static final BasePref predictiveBackIntensity$delegate;
    private static final BasePref transitionAnimation$delegate;
    private static final BasePref glassOutlineStyle$delegate;
    private static final BasePref glassMessageMenu$delegate;
    private static final BasePref forceBlur$delegate;
    private static final BasePref eventType$delegate;
    private static final BasePref navigationDrawer$delegate;
    private static final BasePref immersiveDrawerAnimation$delegate;
    private static final BasePref showFeedTab$delegate;
    private static final BasePref showFeedUnreadCounter$delegate;
    private static final BasePref stickerSize$delegate;
    private static final BasePref stickerTimeMode$delegate;
    private static final BasePref replyColors$delegate;
    private static final BasePref replyEmoji$delegate;
    private static final BasePref replyBackground$delegate;
    private static final BasePref stickerShape$delegate;
    private static final BasePref unlimitedRecentStickers$delegate;
    private static final BasePref hideReactionsInPrivateChats$delegate;
    private static final BasePref hideReactionsInChannels$delegate;
    private static final BasePref hideReactionsInGroups$delegate;
    private static final BasePref doubleTapAction$delegate;
    private static final BasePref doubleTapActionOutOwner$delegate;
    private static final BasePref swipeActions$delegate;
    private static final BasePref swipeActionsLoop$delegate;
    private static final BasePref swipeActionsReversed$delegate;
    private static final BasePref bottomButton$delegate;
    private static final BasePref widePostsInFeed$delegate;
    private static final BasePref widePostsInChannels$delegate;
    private static final BasePref telegramAiEditor$delegate;
    private static final BasePref telegramAiSummaries$delegate;
    private static final BasePref telegramAiInstantViewSummaries$delegate;
    private static final BasePref quickAdminShortcuts$delegate;
    private static final BasePref quickTransitionForChannels$delegate;
    private static final BasePref quickTransitionForTopics$delegate;
    private static final BasePref disableGreetingSticker$delegate;
    private static final BasePref hideKeyboardOnScroll$delegate;
    private static final BasePref addCommaAfterMention$delegate;
    private static final BasePref inlineMathResult$delegate;
    private static final BasePref disableMarkdown$delegate;
    private static final BasePref hideSendAsPeer$delegate;
    private static final BasePref removeMessageTail$delegate;
    private static final BasePref replaceEditedWithIcon$delegate;
    private static final BasePref showOnlineStatus$delegate;
    private static final BasePref showForwardsCount$delegate;
    private static final BasePref hideShareButton$delegate;
    private static final BasePref showResultsBeforeVoting$delegate;
    private static final BasePref showCopyPhotoButton$delegate;
    private static final BasePref showSaveMessageButton$delegate;
    private static final BasePref showRepeatMessageButton$delegate;
    private static final BasePref showClearButton$delegate;
    private static final BasePref showHistoryButton$delegate;
    private static final BasePref showReportButton$delegate;
    private static final BasePref showGenerateButton$delegate;
    private static final BasePref showDetailsButton$delegate;
    private static final BasePref groupMessageMenu$delegate;
    private static final BasePref recognitionLanguage$delegate;
    private static final BasePref postprocessingWithAi$delegate;
    private static final BasePref cameraType$delegate;
    private static final BasePref extendedFramesPerSecond$delegate;
    private static final BasePref cameraStabilization$delegate;
    private static final BasePref cameraMirrorMode$delegate;
    private static final BasePref videoMessagesCamera$delegate;
    private static final BasePref rememberLastUsedCamera$delegate;
    private static final BasePref startWithWideAngleCamera$delegate;
    private static final BasePref zoomSlider$delegate;
    private static final BasePref staticZoom$delegate;
    private static final BasePref alwaysSendInHD$delegate;
    private static final BasePref hideCameraTile$delegate;
    private static final BasePref doubleTapSeekDuration$delegate;
    private static final BasePref preferOriginalQuality$delegate;
    private static final BasePref swipeToPip$delegate;
    private static final BasePref unmuteWithVolumeButtons$delegate;
    private static final BasePref pauseOnMinimizeVideo$delegate;
    private static final BasePref pauseOnMinimizeVoice$delegate;
    private static final BasePref pauseOnMinimizeRound$delegate;
    private static final BasePref useGoogleCrashlytics$delegate;
    private static final BasePref useGoogleAnalytics$delegate;
    private static final BasePref enableAdBlock$delegate;
    private static final BasePref updateScheduleTimestamp$delegate;
    private static final BasePref sdkUpdateScheduleTimestamp$delegate;
    private static final BasePref targetLang$delegate;
    private static final BasePref flashWarmth$delegate;
    private static final BasePref flashIntensity$delegate;
    private static final BasePref pluginsDevMode$delegate;
    private static final BasePref pluginsSafeMode$delegate;
    private static final BasePref pluginsCompactView$delegate;
    private static final BasePref pluginsPySdkAutoUpdate$delegate;
    private static final BasePref pluginsPySdkBetaVersions$delegate;
    private static final BasePref pluginsDisableArtOpts$delegate;
    private static final BasePref pluginsUnknownSources$delegate;
    private static final BasePref pinnedPlugins$delegate;
    private static final BasePref useSystemIconShape$delegate;

    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    public static final /* synthetic */ class WhenMappings {
        public static final /* synthetic */ int[] $EnumSwitchMapping$0;

        static {
            int[] iArr = new int[AvatarCornerType.values().length];
            try {
                iArr[AvatarCornerType.DEFAULT.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                iArr[AvatarCornerType.FORUM.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                iArr[AvatarCornerType.COMMUNITY.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            $EnumSwitchMapping$0 = iArr;
        }
    }

    static {
        int i = 2;
        KProperty<?>[] kPropertyArr = {Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "translationProvider", "getTranslationProvider()I", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "disableNumberRounding", "getDisableNumberRounding()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "formatTimeWithSeconds", "getFormatTimeWithSeconds()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "relativeLastSeen", "getRelativeLastSeen()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "inAppVibration", "getInAppVibration()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "disableNotificationDelay", "getDisableNotificationDelay()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "filterZalgo", "getFilterZalgo()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "useYandexMaps", "getUseYandexMaps()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "downloadSpeedBoost", "getDownloadSpeedBoost()I", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "uploadSpeedBoost", "getUploadSpeedBoost()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "hidePhoneNumber", "getHidePhoneNumber()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "showIdAndDc", "getShowIdAndDc()I", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "hideArchiveFolder", "getHideArchiveFolder()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "archiveOnPull", "getArchiveOnPull()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "disableUnarchiveSwipe", "getDisableUnarchiveSwipe()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "doNotUseProxy", "getDoNotUseProxy()I", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "customSavePath", "getCustomSavePath()Ljava/lang/String;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "iconPack", "getIconPack()Lcom/exteragram/messenger/IconPackType;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "editingIconPackId", "getEditingIconPackId()Ljava/lang/String;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "avatarCorners", "getAvatarCorners()F", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "singleCornerRadius", "getSingleCornerRadius()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "dividerStyle", "getDividerStyle()Lcom/exteragram/messenger/DividerStyle;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "forceSnow", "getForceSnow()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "hideActionBarStatus", "getHideActionBarStatus()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "centerTitle", "getCenterTitle()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "hideStories", "getHideStories()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "hideFloatingButton", "getHideFloatingButton()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "hideDialogsSearchBar", "getHideDialogsSearchBar()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "senderMiniAvatars", "getSenderMiniAvatars()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "titleText", "getTitleText()I", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "tabIcons", "getTabIcons()Lcom/exteragram/messenger/TabIconsMode;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "tabCounterMode", "getTabCounterMode()Lcom/exteragram/messenger/TabCounterMode;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "hideAllChats", "getHideAllChats()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "squareFab", "getSquareFab()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "sectionRadius", "getSectionRadius()F", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "sectionsSeparatedHeadersPreference", "getSectionsSeparatedHeadersPreference()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "newLoadingStyle", "getNewLoadingStyle()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "newSliderStyle", "getNewSliderStyle()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "newSwitchStyle", "getNewSwitchStyle()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "newChatHeaderStyle", "getNewChatHeaderStyle()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "newNavigationBarStyle", "getNewNavigationBarStyle()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "newFabStyle", "getNewFabStyle()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "tabletMode", "getTabletMode()I", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "useSystemFonts", "getUseSystemFonts()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "gooeyAvatarAnimation", "getGooeyAvatarAnimation()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "customThemes", "getCustomThemes()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "predictiveBackIntensity", "getPredictiveBackIntensity()F", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "transitionAnimation", "getTransitionAnimation()Lcom/exteragram/messenger/TransitionAnimation;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "glassOutlineStyle", "getGlassOutlineStyle()Lcom/exteragram/messenger/GlassOutlineStyle;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "glassMessageMenu", "getGlassMessageMenu()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "forceBlur", "getForceBlur()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "eventType", "getEventType()I", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "navigationDrawer", "getNavigationDrawer()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "immersiveDrawerAnimation", "getImmersiveDrawerAnimation()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "showFeedTab", "getShowFeedTab()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "showFeedUnreadCounter", "getShowFeedUnreadCounter()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "stickerSize", "getStickerSize()F", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "stickerTimeMode", "getStickerTimeMode()Lcom/exteragram/messenger/StickerTimeMode;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "replyColors", "getReplyColors()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "replyEmoji", "getReplyEmoji()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "replyBackground", "getReplyBackground()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "stickerShape", "getStickerShape()I", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "unlimitedRecentStickers", "getUnlimitedRecentStickers()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "hideReactionsInPrivateChats", "getHideReactionsInPrivateChats()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "hideReactionsInChannels", "getHideReactionsInChannels()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "hideReactionsInGroups", "getHideReactionsInGroups()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "doubleTapAction", "getDoubleTapAction()I", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "doubleTapActionOutOwner", "getDoubleTapActionOutOwner()I", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "swipeActions", "getSwipeActions()Ljava/lang/String;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "swipeActionsLoop", "getSwipeActionsLoop()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "swipeActionsReversed", "getSwipeActionsReversed()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "bottomButton", "getBottomButton()I", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "widePostsInFeed", "getWidePostsInFeed()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "widePostsInChannels", "getWidePostsInChannels()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "telegramAiEditor", "getTelegramAiEditor()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "telegramAiSummaries", "getTelegramAiSummaries()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "telegramAiInstantViewSummaries", "getTelegramAiInstantViewSummaries()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "quickAdminShortcuts", "getQuickAdminShortcuts()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "quickTransitionForChannels", "getQuickTransitionForChannels()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "quickTransitionForTopics", "getQuickTransitionForTopics()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "disableGreetingSticker", "getDisableGreetingSticker()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "hideKeyboardOnScroll", "getHideKeyboardOnScroll()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "addCommaAfterMention", "getAddCommaAfterMention()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "inlineMathResult", "getInlineMathResult()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "disableMarkdown", "getDisableMarkdown()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "hideSendAsPeer", "getHideSendAsPeer()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "removeMessageTail", "getRemoveMessageTail()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "replaceEditedWithIcon", "getReplaceEditedWithIcon()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "showOnlineStatus", "getShowOnlineStatus()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "showForwardsCount", "getShowForwardsCount()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "hideShareButton", "getHideShareButton()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "showResultsBeforeVoting", "getShowResultsBeforeVoting()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "showCopyPhotoButton", "getShowCopyPhotoButton()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "showSaveMessageButton", "getShowSaveMessageButton()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "showRepeatMessageButton", "getShowRepeatMessageButton()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "showClearButton", "getShowClearButton()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "showHistoryButton", "getShowHistoryButton()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "showReportButton", "getShowReportButton()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "showGenerateButton", "getShowGenerateButton()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "showDetailsButton", "getShowDetailsButton()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "groupMessageMenu", "getGroupMessageMenu()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "recognitionLanguage", "getRecognitionLanguage()Ljava/lang/String;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "postprocessingWithAi", "getPostprocessingWithAi()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "cameraType", "getCameraType()Lcom/exteragram/messenger/CameraType;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "extendedFramesPerSecond", "getExtendedFramesPerSecond()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "cameraStabilization", "getCameraStabilization()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "cameraMirrorMode", "getCameraMirrorMode()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "videoMessagesCamera", "getVideoMessagesCamera()Lcom/exteragram/messenger/VideoMessagesCamera;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "rememberLastUsedCamera", "getRememberLastUsedCamera()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "startWithWideAngleCamera", "getStartWithWideAngleCamera()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "zoomSlider", "getZoomSlider()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "staticZoom", "getStaticZoom()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "alwaysSendInHD", "getAlwaysSendInHD()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "hideCameraTile", "getHideCameraTile()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "doubleTapSeekDuration", "getDoubleTapSeekDuration()I", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "preferOriginalQuality", "getPreferOriginalQuality()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "swipeToPip", "getSwipeToPip()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "unmuteWithVolumeButtons", "getUnmuteWithVolumeButtons()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "pauseOnMinimizeVideo", "getPauseOnMinimizeVideo()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "pauseOnMinimizeVoice", "getPauseOnMinimizeVoice()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "pauseOnMinimizeRound", "getPauseOnMinimizeRound()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "useGoogleCrashlytics", "getUseGoogleCrashlytics()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "useGoogleAnalytics", "getUseGoogleAnalytics()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "enableAdBlock", "getEnableAdBlock()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "updateScheduleTimestamp", "getUpdateScheduleTimestamp()J", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "sdkUpdateScheduleTimestamp", "getSdkUpdateScheduleTimestamp()J", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "targetLang", "getTargetLang()Ljava/lang/String;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "flashWarmth", "getFlashWarmth()F", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "flashIntensity", "getFlashIntensity()F", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "pluginsDevMode", "getPluginsDevMode()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "pluginsSafeMode", "getPluginsSafeMode()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "pluginsCompactView", "getPluginsCompactView()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "pluginsPySdkAutoUpdate", "getPluginsPySdkAutoUpdate()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "pluginsPySdkBetaVersions", "getPluginsPySdkBetaVersions()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "pluginsDisableArtOpts", "getPluginsDisableArtOpts()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "pluginsUnknownSources", "getPluginsUnknownSources()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "pinnedPlugins", "getPinnedPlugins()Ljava/util/Set;", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(ExteraConfig.class, "useSystemIconShape", "getUseSystemIconShape()Z", 1))};
        $$delegatedProperties = (KProperty<Object>[]) (Object) kPropertyArr;
        GSON = new Gson();
        SharedPreferences preferences2 = PreferencesUtils.getPreferences("exteraconfig");
        preferences = preferences2;
        editor = preferences2.edit();
        sync = new Object();
        servicesStarted = new AtomicBoolean(false);
        translationProvider$delegate = new IntegerPref(0, (String) null).provideDelegate(null, kPropertyArr[0]);
        disableNumberRounding$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[1]);
        formatTimeWithSeconds$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[2]);
        relativeLastSeen$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[3]);
        inAppVibration$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[4]);
        disableNotificationDelay$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[5]);
        filterZalgo$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[6]);
        useYandexMaps$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[7]);
        downloadSpeedBoost$delegate = new IntegerPref(0, (String) null).provideDelegate(null, kPropertyArr[8]);
        uploadSpeedBoost$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[9]);
        hidePhoneNumber$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[10]);
        showIdAndDc$delegate = new IntegerPref(1, (String) null).provideDelegate(null, kPropertyArr[11]);
        hideArchiveFolder$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[12]);
        archiveOnPull$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[13]);
        disableUnarchiveSwipe$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[14]);
        doNotUseProxy$delegate = new IntegerPref(0, (String) null).provideDelegate(null, kPropertyArr[15]);
        customSavePath$delegate = new StringPref("exteraGram", (String) null).provideDelegate(null, kPropertyArr[16]);
        iconPack$delegate = new EnumPref(IconPackType.DEFAULT, (String) null).provideDelegate(null, kPropertyArr[17]);
        editingIconPackId$delegate = new NullableStringPref((String) null, (String) null).provideDelegate(null, kPropertyArr[18]);
        avatarCorners$delegate = new FloatPref(28.0f, (String) null).provideDelegate(null, kPropertyArr[19]);
        singleCornerRadius$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[20]);
        dividerStyle$delegate = new EnumPref(DividerStyle.LINE, (String) null).provideDelegate(null, kPropertyArr[21]);
        forceSnow$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[22]);
        hideActionBarStatus$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[23]);
        centerTitle$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[24]);
        hideStories$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[25]);
        hideFloatingButton$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[26]);
        hideDialogsSearchBar$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[27]);
        senderMiniAvatars$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[28]);
        titleText$delegate = new IntegerPref(0, (String) null).provideDelegate(null, kPropertyArr[29]);
        tabIcons$delegate = new EnumPref(TabIconsMode.TITLES_ONLY, (String) null).provideDelegate(null, kPropertyArr[30]);
        tabCounterMode$delegate = new EnumPref(TabCounterMode.ALL, (String) null).provideDelegate(null, kPropertyArr[31]);
        hideAllChats$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[32]);
        squareFab$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[33]);
        sectionRadius$delegate = new FloatPref(20.0f, (String) null).provideDelegate(null, kPropertyArr[34]);
        sectionsSeparatedHeadersPreference$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[35]);
        newLoadingStyle$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[36]);
        newSliderStyle$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[37]);
        newSwitchStyle$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[38]);
        newChatHeaderStyle$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[39]);
        newNavigationBarStyle$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[40]);
        newFabStyle$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[41]);
        tabletMode$delegate = new IntegerPref(0, (String) null).provideDelegate(null, kPropertyArr[42]);
        useSystemFonts$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[43]);
        gooeyAvatarAnimation$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[44]);
        customThemes$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[45]);
        predictiveBackIntensity$delegate = new FloatPref(1.0f, (String) null).provideDelegate(null, kPropertyArr[46]);
        transitionAnimation$delegate = new EnumPref(TransitionAnimation.SPRING, (String) null).provideDelegate(null, kPropertyArr[47]);
        glassOutlineStyle$delegate = new EnumPref(GlassOutlineStyle.GLARE, (String) null).provideDelegate(null, kPropertyArr[48]);
        glassMessageMenu$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[49]);
        forceBlur$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[50]);
        eventType$delegate = new IntegerPref(0, (String) null).provideDelegate(null, kPropertyArr[51]);
        navigationDrawer$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[52]);
        immersiveDrawerAnimation$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[53]);
        showFeedTab$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[54]);
        showFeedUnreadCounter$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[55]);
        stickerSize$delegate = new FloatPref(12.0f, (String) null).provideDelegate(null, kPropertyArr[56]);
        stickerTimeMode$delegate = new EnumPref(StickerTimeMode.DEFAULT, (String) null).provideDelegate(null, kPropertyArr[57]);
        replyColors$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[58]);
        replyEmoji$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[59]);
        replyBackground$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[60]);
        stickerShape$delegate = new IntegerPref(1, (String) null).provideDelegate(null, kPropertyArr[61]);
        unlimitedRecentStickers$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[62]);
        hideReactionsInPrivateChats$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[63]);
        hideReactionsInChannels$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[64]);
        hideReactionsInGroups$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[65]);
        doubleTapAction$delegate = new SanitizedIntegerPref(1, (String) null).provideDelegate(null, kPropertyArr[66]);
        doubleTapActionOutOwner$delegate = new SanitizedIntegerPref(1, (String) null).provideDelegate(null, kPropertyArr[67]);
        swipeActions$delegate = new StringPref("2", (String) null).provideDelegate(null, kPropertyArr[68]);
        swipeActionsLoop$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[69]);
        swipeActionsReversed$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[70]);
        bottomButton$delegate = new IntegerPref(2, (String) null).provideDelegate(null, kPropertyArr[71]);
        widePostsInFeed$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[72]);
        widePostsInChannels$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[73]);
        telegramAiEditor$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[74]);
        telegramAiSummaries$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[75]);
        telegramAiInstantViewSummaries$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[76]);
        quickAdminShortcuts$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[77]);
        quickTransitionForChannels$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[78]);
        quickTransitionForTopics$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[79]);
        disableGreetingSticker$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[80]);
        hideKeyboardOnScroll$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[81]);
        addCommaAfterMention$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[82]);
        inlineMathResult$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[83]);
        disableMarkdown$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[84]);
        hideSendAsPeer$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[85]);
        removeMessageTail$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[86]);
        replaceEditedWithIcon$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[87]);
        showOnlineStatus$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[88]);
        showForwardsCount$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[89]);
        hideShareButton$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[90]);
        showResultsBeforeVoting$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[91]);
        showCopyPhotoButton$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[92]);
        showSaveMessageButton$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[93]);
        showRepeatMessageButton$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[94]);
        showClearButton$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[95]);
        showHistoryButton$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[96]);
        showReportButton$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[97]);
        showGenerateButton$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[98]);
        showDetailsButton$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[99]);
        groupMessageMenu$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[100]);
        recognitionLanguage$delegate = new StringPref("none", (String) null).provideDelegate(null, kPropertyArr[101]);
        postprocessingWithAi$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[102]);
        cameraType$delegate = new EnumPref(SharedConfig.getDevicePerformanceClass() == 2 ? CameraType.CAMERA_X : CameraType.CAMERA_1, (String) null).provideDelegate(null, kPropertyArr[103]);
        extendedFramesPerSecond$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[104]);
        cameraStabilization$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[105]);
        cameraMirrorMode$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[106]);
        videoMessagesCamera$delegate = new EnumPref(VideoMessagesCamera.FRONT, (String) null).provideDelegate(null, kPropertyArr[107]);
        rememberLastUsedCamera$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[108]);
        startWithWideAngleCamera$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[109]);
        zoomSlider$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[110]);
        staticZoom$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[111]);
        alwaysSendInHD$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[112]);
        hideCameraTile$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[113]);
        doubleTapSeekDuration$delegate = new IntegerPref(1, (String) null).provideDelegate(null, kPropertyArr[114]);
        preferOriginalQuality$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[115]);
        swipeToPip$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[116]);
        unmuteWithVolumeButtons$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[117]);
        pauseOnMinimizeVideo$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[118]);
        pauseOnMinimizeVoice$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[119]);
        pauseOnMinimizeRound$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[120]);
        useGoogleCrashlytics$delegate = new BooleanPref(BuildVars.isBetaApp(), (String) null).provideDelegate(null, kPropertyArr[121]);
        useGoogleAnalytics$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[122]);
        enableAdBlock$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[123]);
        updateScheduleTimestamp$delegate = new LongPref(0L, (String) null).provideDelegate(null, kPropertyArr[124]);
        sdkUpdateScheduleTimestamp$delegate = new LongPref(0L, (String) null).provideDelegate(null, kPropertyArr[125]);
        targetLang$delegate = new StringPref(Common.ASSET_APP, (String) null).provideDelegate(null, kPropertyArr[126]);
        flashWarmth$delegate = new FloatPref(0.5f, (String) null).provideDelegate(null, kPropertyArr[127]);
        flashIntensity$delegate = new FloatPref(1.0f, (String) null).provideDelegate(null, kPropertyArr[128]);
        pluginsDevMode$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[129]);
        pluginsSafeMode$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[130]);
        pluginsCompactView$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[131]);
        pluginsPySdkAutoUpdate$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[132]);
        pluginsPySdkBetaVersions$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[133]);
        pluginsDisableArtOpts$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[134]);
        pluginsUnknownSources$delegate = new BooleanPref(false, (String) null).provideDelegate(null, kPropertyArr[135]);
        pinnedPlugins$delegate = new StringSetPref(SetsKt.emptySet(), (String) null).provideDelegate(null, kPropertyArr[136]);
        useSystemIconShape$delegate = new BooleanPref(true, (String) null).provideDelegate(null, kPropertyArr[137]);
        doNotMarkAsNew = new ArrayList<>();
        newFeaturesShowedAt = new HashMap<>();
        iconPacksLayout = new ArrayList<>();
        iconPacksHidden = new ArrayList<>();
        mainMenuLayout = new ArrayList<>();
        mainMenuHiddenItems = new ArrayList<>();
        yandexSearchEngine = new SearchEngine("Yandex", "https://mini.ya.ru/", "https://ya.ru/search/?text=", "https://suggestqueries.google.com/complete/search?client=chrome&q=", "https://yandex.ru/legal/confidential");
    }

    public static final Gson getGSON() {
        return GSON;
    }

    private static final void ensureApiBotListener() {
        if (apiBotListener != null) {
            return;
        }
        try {
            RemoteUtils.initCached();
            SharedPreferences sharedPreferences = RemoteUtils.sharedPreferences;
            if (sharedPreferences == null) {
                return;
            }
            SharedPreferences.OnSharedPreferenceChangeListener onSharedPreferenceChangeListener = (sharedPreferences2, str) -> {
                if (Intrinsics.areEqual("extera_api_bot", str)) {
                    currentApiBot = null;
                }
            };
            sharedPreferences.registerOnSharedPreferenceChangeListener(onSharedPreferenceChangeListener);
            apiBotListener = onSharedPreferenceChangeListener;
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static final Pair<Long, String> getApiBotInfo() {
        Pair<Long, String> pair = currentApiBot;
        if (pair != null) {
            return pair;
        }
        synchronized (sync) {
            Pair<Long, String> pair2 = currentApiBot;
            if (pair2 != null) {
                return pair2;
            }
            ensureApiBotListener();
            String stringConfigValue = RemoteUtils.getStringConfigValue("extera_api_bot", "8083294286:exteraAuthBot");
            if (stringConfigValue != null) {
                try {
                    String[] split = stringConfigValue.split(":", 2);
                    if (split.length == 2) {
                        Pair<Long, String> pair3 = new Pair<>(Long.parseLong(split[0]), split[1]);
                        currentApiBot = pair3;
                        return pair3;
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            Pair<Long, String> pair4 = new Pair<>(8083294286L, "exteraAuthBot");
            currentApiBot = pair4;
            return pair4;
        }
    }

    public static final SharedPreferences getPreferences() {
        return preferences;
    }

    public static final SharedPreferences.Editor getEditor() {
        return editor;
    }

    public static final PreferencesUtils.BackupItem[] getBackupKeys() {
        return (PreferencesUtils.BackupItem[]) PrefClassesKt.getRegisteredKeys().toArray(new PreferencesUtils.BackupItem[0]);
    }

    public static final int getAvatarCorners(float f) {
        return getAvatarCorners(f, false, false, false);
    }

    @JvmOverloads
    public static final int getAvatarCorners(float f, boolean z) {
        return getAvatarCorners(f, z, false, false);
    }

    @JvmOverloads
    public static final int getAvatarCorners(float f, boolean z, AvatarCornerType avatarCornerType) {
        return getAvatarCorners(f, z, avatarCornerType, false);
    }

    @JvmOverloads
    public static final int getAvatarCorners(float f, boolean z, boolean z2) {
        return getAvatarCorners(f, z, z2, false);
    }

    public static final int getAvatarCorners(int f) {
        return getAvatarCorners((float) f);
    }

    public static final int getAvatarCorners(int f, boolean z) {
        return getAvatarCorners((float) f, z);
    }

    public static final int getAvatarCorners(int f, boolean z, boolean z2) {
        return getAvatarCorners((float) f, z, z2);
    }

    public static final int getTranslationProvider() {
        return ((Number) translationProvider$delegate.getValue(null, $$delegatedProperties[0])).intValue();
    }

    public static final void setTranslationProvider(int i) {
        translationProvider$delegate.setValue(null, $$delegatedProperties[0], Integer.valueOf(i));
    }

    public static final boolean getDisableNumberRounding() {
        return ((Boolean) disableNumberRounding$delegate.getValue(null, $$delegatedProperties[1])).booleanValue();
    }

    public static final void setDisableNumberRounding(boolean z) {
        disableNumberRounding$delegate.setValue(null, $$delegatedProperties[1], Boolean.valueOf(z));
    }

    public static final boolean getFormatTimeWithSeconds() {
        return ((Boolean) formatTimeWithSeconds$delegate.getValue(null, $$delegatedProperties[2])).booleanValue();
    }

    public static final void setFormatTimeWithSeconds(boolean z) {
        formatTimeWithSeconds$delegate.setValue(null, $$delegatedProperties[2], Boolean.valueOf(z));
    }

    public static final boolean getRelativeLastSeen() {
        return ((Boolean) relativeLastSeen$delegate.getValue(null, $$delegatedProperties[3])).booleanValue();
    }

    public static final void setRelativeLastSeen(boolean z) {
        relativeLastSeen$delegate.setValue(null, $$delegatedProperties[3], Boolean.valueOf(z));
    }

    public static final boolean getInAppVibration() {
        return ((Boolean) inAppVibration$delegate.getValue(null, $$delegatedProperties[4])).booleanValue();
    }

    public static final void setInAppVibration(boolean z) {
        inAppVibration$delegate.setValue(null, $$delegatedProperties[4], Boolean.valueOf(z));
    }

    public static final boolean getDisableNotificationDelay() {
        return ((Boolean) disableNotificationDelay$delegate.getValue(null, $$delegatedProperties[5])).booleanValue();
    }

    public static final void setDisableNotificationDelay(boolean z) {
        disableNotificationDelay$delegate.setValue(null, $$delegatedProperties[5], Boolean.valueOf(z));
    }

    public static final boolean getFilterZalgo() {
        return ((Boolean) filterZalgo$delegate.getValue(null, $$delegatedProperties[6])).booleanValue();
    }

    public static final void setFilterZalgo(boolean z) {
        filterZalgo$delegate.setValue(null, $$delegatedProperties[6], Boolean.valueOf(z));
    }

    public static final boolean getUseYandexMaps() {
        return ((Boolean) useYandexMaps$delegate.getValue(null, $$delegatedProperties[7])).booleanValue();
    }

    public static final void setUseYandexMaps(boolean z) {
        useYandexMaps$delegate.setValue(null, $$delegatedProperties[7], Boolean.valueOf(z));
    }

    public static final int getDownloadSpeedBoost() {
        return ((Number) downloadSpeedBoost$delegate.getValue(null, $$delegatedProperties[8])).intValue();
    }

    public static final void setDownloadSpeedBoost(int i) {
        downloadSpeedBoost$delegate.setValue(null, $$delegatedProperties[8], Integer.valueOf(i));
    }

    public static final boolean getUploadSpeedBoost() {
        return ((Boolean) uploadSpeedBoost$delegate.getValue(null, $$delegatedProperties[9])).booleanValue();
    }

    public static final void setUploadSpeedBoost(boolean z) {
        uploadSpeedBoost$delegate.setValue(null, $$delegatedProperties[9], Boolean.valueOf(z));
    }

    public static final boolean getHidePhoneNumber() {
        return ((Boolean) hidePhoneNumber$delegate.getValue(null, $$delegatedProperties[10])).booleanValue();
    }

    public static final void setHidePhoneNumber(boolean z) {
        hidePhoneNumber$delegate.setValue(null, $$delegatedProperties[10], Boolean.valueOf(z));
    }

    public static final int getShowIdAndDc() {
        return ((Number) showIdAndDc$delegate.getValue(null, $$delegatedProperties[11])).intValue();
    }

    public static final void setShowIdAndDc(int i) {
        showIdAndDc$delegate.setValue(null, $$delegatedProperties[11], Integer.valueOf(i));
    }

    public static final boolean getHideArchiveFolder() {
        return ((Boolean) hideArchiveFolder$delegate.getValue(null, $$delegatedProperties[12])).booleanValue();
    }

    public static final void setHideArchiveFolder(boolean z) {
        hideArchiveFolder$delegate.setValue(null, $$delegatedProperties[12], Boolean.valueOf(z));
    }

    public static final boolean getArchiveOnPull() {
        return ((Boolean) archiveOnPull$delegate.getValue(null, $$delegatedProperties[13])).booleanValue();
    }

    public static final void setArchiveOnPull(boolean z) {
        archiveOnPull$delegate.setValue(null, $$delegatedProperties[13], Boolean.valueOf(z));
    }

    public static final boolean getDisableUnarchiveSwipe() {
        return ((Boolean) disableUnarchiveSwipe$delegate.getValue(null, $$delegatedProperties[14])).booleanValue();
    }

    public static final void setDisableUnarchiveSwipe(boolean z) {
        disableUnarchiveSwipe$delegate.setValue(null, $$delegatedProperties[14], Boolean.valueOf(z));
    }

    public static final int getDoNotUseProxy() {
        return ((Number) doNotUseProxy$delegate.getValue(null, $$delegatedProperties[15])).intValue();
    }

    public static final void setDoNotUseProxy(int i) {
        doNotUseProxy$delegate.setValue(null, $$delegatedProperties[15], Integer.valueOf(i));
    }

    public static final String getCustomSavePath() {
        return (String) customSavePath$delegate.getValue(null, $$delegatedProperties[16]);
    }

    public static final void setCustomSavePath(String str) {
        Intrinsics.checkNotNullParameter(str, "value");
        customSavePath$delegate.setValue(null, $$delegatedProperties[16], str);
    }

    public static final IconPackType getIconPack() {
        return (IconPackType) iconPack$delegate.getValue(null, $$delegatedProperties[17]);
    }

    public static final void setIconPack(IconPackType value) {
        Intrinsics.checkNotNullParameter(value, "value");
        iconPack$delegate.setValue(null, $$delegatedProperties[17], value);
    }

    public static final String getEditingIconPackId() {
        return (String) editingIconPackId$delegate.getValue(null, $$delegatedProperties[18]);
    }

    public static final void setEditingIconPackId(String str) {
        Intrinsics.checkNotNullParameter(str, "value");
        editingIconPackId$delegate.setValue(null, $$delegatedProperties[18], str);
    }

    public static final float getAvatarCorners() {
        return ((Number) avatarCorners$delegate.getValue(null, $$delegatedProperties[19])).floatValue();
    }

    public static final void setAvatarCorners(float f) {
        avatarCorners$delegate.setValue(null, $$delegatedProperties[19], Float.valueOf(f));
    }

    public static final boolean getSingleCornerRadius() {
        return ((Boolean) singleCornerRadius$delegate.getValue(null, $$delegatedProperties[20])).booleanValue();
    }

    public static final void setSingleCornerRadius(boolean z) {
        singleCornerRadius$delegate.setValue(null, $$delegatedProperties[20], Boolean.valueOf(z));
    }

    public static final DividerStyle getDividerStyle() {
        return (DividerStyle) dividerStyle$delegate.getValue(null, $$delegatedProperties[21]);
    }

    public static final void setDividerStyle(DividerStyle value) {
        Intrinsics.checkNotNullParameter(value, "value");
        dividerStyle$delegate.setValue(null, $$delegatedProperties[21], value);
    }

    public static final boolean getForceSnow() {
        return ((Boolean) forceSnow$delegate.getValue(null, $$delegatedProperties[22])).booleanValue();
    }

    public static final void setForceSnow(boolean z) {
        forceSnow$delegate.setValue(null, $$delegatedProperties[22], Boolean.valueOf(z));
    }

    public static final boolean getHideActionBarStatus() {
        return ((Boolean) hideActionBarStatus$delegate.getValue(null, $$delegatedProperties[23])).booleanValue();
    }

    public static final void setHideActionBarStatus(boolean z) {
        hideActionBarStatus$delegate.setValue(null, $$delegatedProperties[23], Boolean.valueOf(z));
    }

    public static final boolean getCenterTitle() {
        return ((Boolean) centerTitle$delegate.getValue(null, $$delegatedProperties[24])).booleanValue();
    }

    public static final void setCenterTitle(boolean z) {
        centerTitle$delegate.setValue(null, $$delegatedProperties[24], Boolean.valueOf(z));
    }

    public static final boolean getHideStories() {
        return ((Boolean) hideStories$delegate.getValue(null, $$delegatedProperties[25])).booleanValue();
    }

    public static final void setHideStories(boolean z) {
        hideStories$delegate.setValue(null, $$delegatedProperties[25], Boolean.valueOf(z));
    }

    public static final boolean getHideFloatingButton() {
        return ((Boolean) hideFloatingButton$delegate.getValue(null, $$delegatedProperties[26])).booleanValue();
    }

    public static final void setHideFloatingButton(boolean z) {
        hideFloatingButton$delegate.setValue(null, $$delegatedProperties[26], Boolean.valueOf(z));
    }

    public static final boolean getHideDialogsSearchBar() {
        return ((Boolean) hideDialogsSearchBar$delegate.getValue(null, $$delegatedProperties[27])).booleanValue();
    }

    public static final void setHideDialogsSearchBar(boolean z) {
        hideDialogsSearchBar$delegate.setValue(null, $$delegatedProperties[27], Boolean.valueOf(z));
    }

    public static final boolean getSenderMiniAvatars() {
        return ((Boolean) senderMiniAvatars$delegate.getValue(null, $$delegatedProperties[28])).booleanValue();
    }

    public static final void setSenderMiniAvatars(boolean z) {
        senderMiniAvatars$delegate.setValue(null, $$delegatedProperties[28], Boolean.valueOf(z));
    }

    public static final int getTitleText() {
        return ((Number) titleText$delegate.getValue(null, $$delegatedProperties[29])).intValue();
    }

    public static final void setTitleText(int i) {
        titleText$delegate.setValue(null, $$delegatedProperties[29], Integer.valueOf(i));
    }

    public static final TabIconsMode getTabIcons() {
        return (TabIconsMode) tabIcons$delegate.getValue(null, $$delegatedProperties[30]);
    }

    public static final void setTabIcons(TabIconsMode value) {
        Intrinsics.checkNotNullParameter(value, "value");
        tabIcons$delegate.setValue(null, $$delegatedProperties[30], value);
    }

    public static final TabCounterMode getTabCounterMode() {
        return (TabCounterMode) tabCounterMode$delegate.getValue(null, $$delegatedProperties[31]);
    }

    public static final void setTabCounterMode(TabCounterMode tabCounterMode) {
        tabCounterMode$delegate.setValue(null, $$delegatedProperties[31], tabCounterMode);
    }

    public static final boolean getHideAllChats() {
        return ((Boolean) hideAllChats$delegate.getValue(null, $$delegatedProperties[32])).booleanValue();
    }

    public static final void setHideAllChats(boolean z) {
        hideAllChats$delegate.setValue(null, $$delegatedProperties[32], Boolean.valueOf(z));
    }

    public static final boolean getSquareFab() {
        return ((Boolean) squareFab$delegate.getValue(null, $$delegatedProperties[33])).booleanValue();
    }

    public static final void setSquareFab(boolean z) {
        squareFab$delegate.setValue(null, $$delegatedProperties[33], Boolean.valueOf(z));
    }

    public static final float getSectionRadius() {
        return ((Number) sectionRadius$delegate.getValue(null, $$delegatedProperties[34])).floatValue();
    }

    public static final void setSectionRadius(float f) {
        sectionRadius$delegate.setValue(null, $$delegatedProperties[34], Float.valueOf(f));
    }

    public static final boolean getSectionsSeparatedHeadersPreference() {
        return ((Boolean) sectionsSeparatedHeadersPreference$delegate.getValue(null, $$delegatedProperties[35])).booleanValue();
    }

    public static final void setSectionsSeparatedHeadersPreference(boolean z) {
        sectionsSeparatedHeadersPreference$delegate.setValue(null, $$delegatedProperties[35], Boolean.valueOf(z));
    }

    public static final boolean getNewLoadingStyle() {
        return ((Boolean) newLoadingStyle$delegate.getValue(null, $$delegatedProperties[36])).booleanValue();
    }

    public static final void setNewLoadingStyle(boolean z) {
        newLoadingStyle$delegate.setValue(null, $$delegatedProperties[36], Boolean.valueOf(z));
    }

    public static final boolean getNewSliderStyle() {
        return ((Boolean) newSliderStyle$delegate.getValue(null, $$delegatedProperties[37])).booleanValue();
    }

    public static final void setNewSliderStyle(boolean z) {
        newSliderStyle$delegate.setValue(null, $$delegatedProperties[37], Boolean.valueOf(z));
    }

    public static final boolean getNewSwitchStyle() {
        return ((Boolean) newSwitchStyle$delegate.getValue(null, $$delegatedProperties[38])).booleanValue();
    }

    public static final void setNewSwitchStyle(boolean z) {
        newSwitchStyle$delegate.setValue(null, $$delegatedProperties[38], Boolean.valueOf(z));
    }

    public static final boolean getNewChatHeaderStyle() {
        return ((Boolean) newChatHeaderStyle$delegate.getValue(null, $$delegatedProperties[39])).booleanValue();
    }

    public static final void setNewChatHeaderStyle(boolean z) {
        newChatHeaderStyle$delegate.setValue(null, $$delegatedProperties[39], Boolean.valueOf(z));
    }

    public static final boolean getNewNavigationBarStyle() {
        return ((Boolean) newNavigationBarStyle$delegate.getValue(null, $$delegatedProperties[40])).booleanValue();
    }

    public static final void setNewNavigationBarStyle(boolean z) {
        newNavigationBarStyle$delegate.setValue(null, $$delegatedProperties[40], Boolean.valueOf(z));
    }

    public static final boolean getNewFabStyle() {
        return ((Boolean) newFabStyle$delegate.getValue(null, $$delegatedProperties[41])).booleanValue();
    }

    public static final void setNewFabStyle(boolean z) {
        newFabStyle$delegate.setValue(null, $$delegatedProperties[41], Boolean.valueOf(z));
    }

    public static final int getTabletMode() {
        return ((Number) tabletMode$delegate.getValue(null, $$delegatedProperties[42])).intValue();
    }

    public static final void setTabletMode(int i) {
        tabletMode$delegate.setValue(null, $$delegatedProperties[42], Integer.valueOf(i));
    }

    public static final boolean getUseSystemFonts() {
        return ((Boolean) useSystemFonts$delegate.getValue(null, $$delegatedProperties[43])).booleanValue();
    }

    public static final void setUseSystemFonts(boolean z) {
        useSystemFonts$delegate.setValue(null, $$delegatedProperties[43], Boolean.valueOf(z));
    }

    public static final boolean getGooeyAvatarAnimation() {
        return ((Boolean) gooeyAvatarAnimation$delegate.getValue(null, $$delegatedProperties[44])).booleanValue();
    }

    public static final void setGooeyAvatarAnimation(boolean z) {
        gooeyAvatarAnimation$delegate.setValue(null, $$delegatedProperties[44], Boolean.valueOf(z));
    }

    public static final boolean getCustomThemes() {
        return ((Boolean) customThemes$delegate.getValue(null, $$delegatedProperties[45])).booleanValue();
    }

    public static final void setCustomThemes(boolean z) {
        customThemes$delegate.setValue(null, $$delegatedProperties[45], Boolean.valueOf(z));
    }

    public static final float getPredictiveBackIntensity() {
        return ((Number) predictiveBackIntensity$delegate.getValue(null, $$delegatedProperties[46])).floatValue();
    }

    public static final void setPredictiveBackIntensity(float f) {
        predictiveBackIntensity$delegate.setValue(null, $$delegatedProperties[46], Float.valueOf(f));
    }

    public static final TransitionAnimation getTransitionAnimation() {
        return (TransitionAnimation) transitionAnimation$delegate.getValue(null, $$delegatedProperties[47]);
    }

    public static final void setTransitionAnimation(TransitionAnimation value) {
        Intrinsics.checkNotNullParameter(value, "value");
        transitionAnimation$delegate.setValue(null, $$delegatedProperties[47], value);
    }

    public static final GlassOutlineStyle getGlassOutlineStyle() {
        return (GlassOutlineStyle) glassOutlineStyle$delegate.getValue(null, $$delegatedProperties[48]);
    }

    public static final void setGlassOutlineStyle(GlassOutlineStyle value) {
        Intrinsics.checkNotNullParameter(value, "value");
        glassOutlineStyle$delegate.setValue(null, $$delegatedProperties[48], value);
    }

    public static final boolean getGlassMessageMenu() {
        return ((Boolean) glassMessageMenu$delegate.getValue(null, $$delegatedProperties[49])).booleanValue();
    }

    public static final void setGlassMessageMenu(boolean z) {
        glassMessageMenu$delegate.setValue(null, $$delegatedProperties[49], Boolean.valueOf(z));
    }

    public static final boolean getForceBlur() {
        return ((Boolean) forceBlur$delegate.getValue(null, $$delegatedProperties[50])).booleanValue();
    }

    public static final void setForceBlur(boolean z) {
        forceBlur$delegate.setValue(null, $$delegatedProperties[50], Boolean.valueOf(z));
    }

    public static final int getEventType() {
        return ((Number) eventType$delegate.getValue(null, $$delegatedProperties[51])).intValue();
    }

    public static final void setEventType(int i) {
        eventType$delegate.setValue(null, $$delegatedProperties[51], Integer.valueOf(i));
    }

    public static final boolean getNavigationDrawer() {
        return ((Boolean) navigationDrawer$delegate.getValue(null, $$delegatedProperties[52])).booleanValue();
    }

    public static final void setNavigationDrawer(boolean z) {
        navigationDrawer$delegate.setValue(null, $$delegatedProperties[52], Boolean.valueOf(z));
    }

    public static final boolean getImmersiveDrawerAnimation() {
        return ((Boolean) immersiveDrawerAnimation$delegate.getValue(null, $$delegatedProperties[53])).booleanValue();
    }

    public static final void setImmersiveDrawerAnimation(boolean z) {
        immersiveDrawerAnimation$delegate.setValue(null, $$delegatedProperties[53], Boolean.valueOf(z));
    }

    public static final boolean getShowFeedTab() {
        return ((Boolean) showFeedTab$delegate.getValue(null, $$delegatedProperties[54])).booleanValue();
    }

    public static final void setShowFeedTab(boolean z) {
        showFeedTab$delegate.setValue(null, $$delegatedProperties[54], Boolean.valueOf(z));
    }

    public static final boolean getShowFeedUnreadCounter() {
        return ((Boolean) showFeedUnreadCounter$delegate.getValue(null, $$delegatedProperties[55])).booleanValue();
    }

    public static final void setShowFeedUnreadCounter(boolean z) {
        showFeedUnreadCounter$delegate.setValue(null, $$delegatedProperties[55], Boolean.valueOf(z));
    }

    public static final float getStickerSize() {
        return ((Number) stickerSize$delegate.getValue(null, $$delegatedProperties[56])).floatValue();
    }

    public static final void setStickerSize(float f) {
        stickerSize$delegate.setValue(null, $$delegatedProperties[56], Float.valueOf(f));
    }

    public static final StickerTimeMode getStickerTimeMode() {
        return (StickerTimeMode) stickerTimeMode$delegate.getValue(null, $$delegatedProperties[57]);
    }

    public static final void setStickerTimeMode(StickerTimeMode value) {
        Intrinsics.checkNotNullParameter(value, "value");
        stickerTimeMode$delegate.setValue(null, $$delegatedProperties[57], value);
    }

    public static final boolean getReplyColors() {
        return ((Boolean) replyColors$delegate.getValue(null, $$delegatedProperties[58])).booleanValue();
    }

    public static final void setReplyColors(boolean z) {
        replyColors$delegate.setValue(null, $$delegatedProperties[58], Boolean.valueOf(z));
    }

    public static final boolean getReplyEmoji() {
        return ((Boolean) replyEmoji$delegate.getValue(null, $$delegatedProperties[59])).booleanValue();
    }

    public static final void setReplyEmoji(boolean z) {
        replyEmoji$delegate.setValue(null, $$delegatedProperties[59], Boolean.valueOf(z));
    }

    public static final boolean getReplyBackground() {
        return ((Boolean) replyBackground$delegate.getValue(null, $$delegatedProperties[60])).booleanValue();
    }

    public static final void setReplyBackground(boolean z) {
        replyBackground$delegate.setValue(null, $$delegatedProperties[60], Boolean.valueOf(z));
    }

    public static final int getStickerShape() {
        return ((Number) stickerShape$delegate.getValue(null, $$delegatedProperties[61])).intValue();
    }

    public static final void setStickerShape(int i) {
        stickerShape$delegate.setValue(null, $$delegatedProperties[61], Integer.valueOf(i));
    }

    public static final boolean getUnlimitedRecentStickers() {
        return ((Boolean) unlimitedRecentStickers$delegate.getValue(null, $$delegatedProperties[62])).booleanValue();
    }

    public static final void setUnlimitedRecentStickers(boolean z) {
        unlimitedRecentStickers$delegate.setValue(null, $$delegatedProperties[62], Boolean.valueOf(z));
    }

    public static final boolean getHideReactionsInPrivateChats() {
        return ((Boolean) hideReactionsInPrivateChats$delegate.getValue(null, $$delegatedProperties[63])).booleanValue();
    }

    public static final void setHideReactionsInPrivateChats(boolean z) {
        hideReactionsInPrivateChats$delegate.setValue(null, $$delegatedProperties[63], Boolean.valueOf(z));
    }

    public static final boolean getHideReactionsInChannels() {
        return ((Boolean) hideReactionsInChannels$delegate.getValue(null, $$delegatedProperties[64])).booleanValue();
    }

    public static final void setHideReactionsInChannels(boolean z) {
        hideReactionsInChannels$delegate.setValue(null, $$delegatedProperties[64], Boolean.valueOf(z));
    }

    public static final boolean getHideReactionsInGroups() {
        return ((Boolean) hideReactionsInGroups$delegate.getValue(null, $$delegatedProperties[65])).booleanValue();
    }

    public static final void setHideReactionsInGroups(boolean z) {
        hideReactionsInGroups$delegate.setValue(null, $$delegatedProperties[65], Boolean.valueOf(z));
    }

    public static final int getDoubleTapAction() {
        return ((Number) doubleTapAction$delegate.getValue(null, $$delegatedProperties[66])).intValue();
    }

    public static final void setDoubleTapAction(int i) {
        doubleTapAction$delegate.setValue(null, $$delegatedProperties[66], Integer.valueOf(i));
    }

    public static final int getDoubleTapActionOutOwner() {
        return ((Number) doubleTapActionOutOwner$delegate.getValue(null, $$delegatedProperties[67])).intValue();
    }

    public static final void setDoubleTapActionOutOwner(int i) {
        doubleTapActionOutOwner$delegate.setValue(null, $$delegatedProperties[67], Integer.valueOf(i));
    }

    public static final String getSwipeActions() {
        return (String) swipeActions$delegate.getValue(null, $$delegatedProperties[68]);
    }

    public static final void setSwipeActions(String str) {
        Intrinsics.checkNotNullParameter(str, "value");
        swipeActions$delegate.setValue(null, $$delegatedProperties[68], str);
    }

    public static final boolean getSwipeActionsLoop() {
        return ((Boolean) swipeActionsLoop$delegate.getValue(null, $$delegatedProperties[69])).booleanValue();
    }

    public static final void setSwipeActionsLoop(boolean z) {
        swipeActionsLoop$delegate.setValue(null, $$delegatedProperties[69], Boolean.valueOf(z));
    }

    public static final boolean getSwipeActionsReversed() {
        return ((Boolean) swipeActionsReversed$delegate.getValue(null, $$delegatedProperties[70])).booleanValue();
    }

    public static final void setSwipeActionsReversed(boolean z) {
        swipeActionsReversed$delegate.setValue(null, $$delegatedProperties[70], Boolean.valueOf(z));
    }

    public static final int getBottomButton() {
        return ((Number) bottomButton$delegate.getValue(null, $$delegatedProperties[71])).intValue();
    }

    public static final void setBottomButton(int i) {
        bottomButton$delegate.setValue(null, $$delegatedProperties[71], Integer.valueOf(i));
    }

    public static final boolean getWidePostsInFeed() {
        return ((Boolean) widePostsInFeed$delegate.getValue(null, $$delegatedProperties[72])).booleanValue();
    }

    public static final void setWidePostsInFeed(boolean z) {
        widePostsInFeed$delegate.setValue(null, $$delegatedProperties[72], Boolean.valueOf(z));
    }

    public static final boolean getWidePostsInChannels() {
        return ((Boolean) widePostsInChannels$delegate.getValue(null, $$delegatedProperties[73])).booleanValue();
    }

    public static final void setWidePostsInChannels(boolean z) {
        widePostsInChannels$delegate.setValue(null, $$delegatedProperties[73], Boolean.valueOf(z));
    }

    public static final boolean getTelegramAiEditor() {
        return ((Boolean) telegramAiEditor$delegate.getValue(null, $$delegatedProperties[74])).booleanValue();
    }

    public static final void setTelegramAiEditor(boolean z) {
        telegramAiEditor$delegate.setValue(null, $$delegatedProperties[74], Boolean.valueOf(z));
    }

    public static final boolean getTelegramAiSummaries() {
        return ((Boolean) telegramAiSummaries$delegate.getValue(null, $$delegatedProperties[75])).booleanValue();
    }

    public static final void setTelegramAiSummaries(boolean z) {
        telegramAiSummaries$delegate.setValue(null, $$delegatedProperties[75], Boolean.valueOf(z));
    }

    public static final boolean getTelegramAiInstantViewSummaries() {
        return ((Boolean) telegramAiInstantViewSummaries$delegate.getValue(null, $$delegatedProperties[76])).booleanValue();
    }

    public static final void setTelegramAiInstantViewSummaries(boolean z) {
        telegramAiInstantViewSummaries$delegate.setValue(null, $$delegatedProperties[76], Boolean.valueOf(z));
    }

    public static final boolean getQuickAdminShortcuts() {
        return ((Boolean) quickAdminShortcuts$delegate.getValue(null, $$delegatedProperties[77])).booleanValue();
    }

    public static final void setQuickAdminShortcuts(boolean z) {
        quickAdminShortcuts$delegate.setValue(null, $$delegatedProperties[77], Boolean.valueOf(z));
    }

    public static final boolean getQuickTransitionForChannels() {
        return ((Boolean) quickTransitionForChannels$delegate.getValue(null, $$delegatedProperties[78])).booleanValue();
    }

    public static final void setQuickTransitionForChannels(boolean z) {
        quickTransitionForChannels$delegate.setValue(null, $$delegatedProperties[78], Boolean.valueOf(z));
    }

    public static final boolean getQuickTransitionForTopics() {
        return ((Boolean) quickTransitionForTopics$delegate.getValue(null, $$delegatedProperties[79])).booleanValue();
    }

    public static final void setQuickTransitionForTopics(boolean z) {
        quickTransitionForTopics$delegate.setValue(null, $$delegatedProperties[79], Boolean.valueOf(z));
    }

    public static final boolean getDisableGreetingSticker() {
        return ((Boolean) disableGreetingSticker$delegate.getValue(null, $$delegatedProperties[80])).booleanValue();
    }

    public static final void setDisableGreetingSticker(boolean z) {
        disableGreetingSticker$delegate.setValue(null, $$delegatedProperties[80], Boolean.valueOf(z));
    }

    public static final boolean getHideKeyboardOnScroll() {
        return ((Boolean) hideKeyboardOnScroll$delegate.getValue(null, $$delegatedProperties[81])).booleanValue();
    }

    public static final void setHideKeyboardOnScroll(boolean z) {
        hideKeyboardOnScroll$delegate.setValue(null, $$delegatedProperties[81], Boolean.valueOf(z));
    }

    public static final boolean getAddCommaAfterMention() {
        return ((Boolean) addCommaAfterMention$delegate.getValue(null, $$delegatedProperties[82])).booleanValue();
    }

    public static final void setAddCommaAfterMention(boolean z) {
        addCommaAfterMention$delegate.setValue(null, $$delegatedProperties[82], Boolean.valueOf(z));
    }

    public static final boolean getInlineMathResult() {
        return ((Boolean) inlineMathResult$delegate.getValue(null, $$delegatedProperties[83])).booleanValue();
    }

    public static final void setInlineMathResult(boolean z) {
        inlineMathResult$delegate.setValue(null, $$delegatedProperties[83], Boolean.valueOf(z));
    }

    public static final boolean getDisableMarkdown() {
        return ((Boolean) disableMarkdown$delegate.getValue(null, $$delegatedProperties[84])).booleanValue();
    }

    public static final void setDisableMarkdown(boolean z) {
        disableMarkdown$delegate.setValue(null, $$delegatedProperties[84], Boolean.valueOf(z));
    }

    public static final boolean getHideSendAsPeer() {
        return ((Boolean) hideSendAsPeer$delegate.getValue(null, $$delegatedProperties[85])).booleanValue();
    }

    public static final void setHideSendAsPeer(boolean z) {
        hideSendAsPeer$delegate.setValue(null, $$delegatedProperties[85], Boolean.valueOf(z));
    }

    public static final boolean getRemoveMessageTail() {
        return ((Boolean) removeMessageTail$delegate.getValue(null, $$delegatedProperties[86])).booleanValue();
    }

    public static final void setRemoveMessageTail(boolean z) {
        removeMessageTail$delegate.setValue(null, $$delegatedProperties[86], Boolean.valueOf(z));
    }

    public static final boolean getReplaceEditedWithIcon() {
        return ((Boolean) replaceEditedWithIcon$delegate.getValue(null, $$delegatedProperties[87])).booleanValue();
    }

    public static final void setReplaceEditedWithIcon(boolean z) {
        replaceEditedWithIcon$delegate.setValue(null, $$delegatedProperties[87], Boolean.valueOf(z));
    }

    public static final boolean getShowOnlineStatus() {
        return ((Boolean) showOnlineStatus$delegate.getValue(null, $$delegatedProperties[88])).booleanValue();
    }

    public static final void setShowOnlineStatus(boolean z) {
        showOnlineStatus$delegate.setValue(null, $$delegatedProperties[88], Boolean.valueOf(z));
    }

    public static final boolean getShowForwardsCount() {
        return ((Boolean) showForwardsCount$delegate.getValue(null, $$delegatedProperties[89])).booleanValue();
    }

    public static final void setShowForwardsCount(boolean z) {
        showForwardsCount$delegate.setValue(null, $$delegatedProperties[89], Boolean.valueOf(z));
    }

    public static final boolean getHideShareButton() {
        return ((Boolean) hideShareButton$delegate.getValue(null, $$delegatedProperties[90])).booleanValue();
    }

    public static final void setHideShareButton(boolean z) {
        hideShareButton$delegate.setValue(null, $$delegatedProperties[90], Boolean.valueOf(z));
    }

    public static final boolean getShowResultsBeforeVoting() {
        return ((Boolean) showResultsBeforeVoting$delegate.getValue(null, $$delegatedProperties[91])).booleanValue();
    }

    public static final void setShowResultsBeforeVoting(boolean z) {
        showResultsBeforeVoting$delegate.setValue(null, $$delegatedProperties[91], Boolean.valueOf(z));
    }

    public static final boolean getShowCopyPhotoButton() {
        return ((Boolean) showCopyPhotoButton$delegate.getValue(null, $$delegatedProperties[92])).booleanValue();
    }

    public static final void setShowCopyPhotoButton(boolean z) {
        showCopyPhotoButton$delegate.setValue(null, $$delegatedProperties[92], Boolean.valueOf(z));
    }

    public static final boolean getShowSaveMessageButton() {
        return ((Boolean) showSaveMessageButton$delegate.getValue(null, $$delegatedProperties[93])).booleanValue();
    }

    public static final void setShowSaveMessageButton(boolean z) {
        showSaveMessageButton$delegate.setValue(null, $$delegatedProperties[93], Boolean.valueOf(z));
    }

    public static final boolean getShowRepeatMessageButton() {
        return ((Boolean) showRepeatMessageButton$delegate.getValue(null, $$delegatedProperties[94])).booleanValue();
    }

    public static final void setShowRepeatMessageButton(boolean z) {
        showRepeatMessageButton$delegate.setValue(null, $$delegatedProperties[94], Boolean.valueOf(z));
    }

    public static final boolean getShowClearButton() {
        return ((Boolean) showClearButton$delegate.getValue(null, $$delegatedProperties[95])).booleanValue();
    }

    public static final void setShowClearButton(boolean z) {
        showClearButton$delegate.setValue(null, $$delegatedProperties[95], Boolean.valueOf(z));
    }

    public static final boolean getShowHistoryButton() {
        return ((Boolean) showHistoryButton$delegate.getValue(null, $$delegatedProperties[96])).booleanValue();
    }

    public static final void setShowHistoryButton(boolean z) {
        showHistoryButton$delegate.setValue(null, $$delegatedProperties[96], Boolean.valueOf(z));
    }

    public static final boolean getShowReportButton() {
        return ((Boolean) showReportButton$delegate.getValue(null, $$delegatedProperties[97])).booleanValue();
    }

    public static final void setShowReportButton(boolean z) {
        showReportButton$delegate.setValue(null, $$delegatedProperties[97], Boolean.valueOf(z));
    }

    public static final boolean getShowGenerateButton() {
        return ((Boolean) showGenerateButton$delegate.getValue(null, $$delegatedProperties[98])).booleanValue();
    }

    public static final void setShowGenerateButton(boolean z) {
        showGenerateButton$delegate.setValue(null, $$delegatedProperties[98], Boolean.valueOf(z));
    }

    public static final boolean getShowDetailsButton() {
        return ((Boolean) showDetailsButton$delegate.getValue(null, $$delegatedProperties[99])).booleanValue();
    }

    public static final void setShowDetailsButton(boolean z) {
        showDetailsButton$delegate.setValue(null, $$delegatedProperties[99], Boolean.valueOf(z));
    }

    public static final boolean getGroupMessageMenu() {
        return ((Boolean) groupMessageMenu$delegate.getValue(null, $$delegatedProperties[100])).booleanValue();
    }

    public static final void setGroupMessageMenu(boolean z) {
        groupMessageMenu$delegate.setValue(null, $$delegatedProperties[100], Boolean.valueOf(z));
    }

    public static final String getRecognitionLanguage() {
        return (String) recognitionLanguage$delegate.getValue(null, $$delegatedProperties[101]);
    }

    public static final void setRecognitionLanguage(String str) {
        Intrinsics.checkNotNullParameter(str, "value");
        recognitionLanguage$delegate.setValue(null, $$delegatedProperties[101], str);
    }

    public static final boolean getPostprocessingWithAi() {
        return ((Boolean) postprocessingWithAi$delegate.getValue(null, $$delegatedProperties[102])).booleanValue();
    }

    public static final void setPostprocessingWithAi(boolean z) {
        postprocessingWithAi$delegate.setValue(null, $$delegatedProperties[102], Boolean.valueOf(z));
    }

    public static final CameraType getCameraType() {
        return (CameraType) cameraType$delegate.getValue(null, $$delegatedProperties[103]);
    }

    public static final void setCameraType(CameraType value) {
        Intrinsics.checkNotNullParameter(value, "value");
        cameraType$delegate.setValue(null, $$delegatedProperties[103], value);
    }

    public static final boolean getExtendedFramesPerSecond() {
        return ((Boolean) extendedFramesPerSecond$delegate.getValue(null, $$delegatedProperties[104])).booleanValue();
    }

    public static final void setExtendedFramesPerSecond(boolean z) {
        extendedFramesPerSecond$delegate.setValue(null, $$delegatedProperties[104], Boolean.valueOf(z));
    }

    public static final boolean getCameraStabilization() {
        return ((Boolean) cameraStabilization$delegate.getValue(null, $$delegatedProperties[105])).booleanValue();
    }

    public static final void setCameraStabilization(boolean z) {
        cameraStabilization$delegate.setValue(null, $$delegatedProperties[105], Boolean.valueOf(z));
    }

    public static final boolean getCameraMirrorMode() {
        return ((Boolean) cameraMirrorMode$delegate.getValue(null, $$delegatedProperties[106])).booleanValue();
    }

    public static final void setCameraMirrorMode(boolean z) {
        cameraMirrorMode$delegate.setValue(null, $$delegatedProperties[106], Boolean.valueOf(z));
    }

    public static final VideoMessagesCamera getVideoMessagesCamera() {
        return (VideoMessagesCamera) videoMessagesCamera$delegate.getValue(null, $$delegatedProperties[107]);
    }

    public static final void setVideoMessagesCamera(VideoMessagesCamera value) {
        Intrinsics.checkNotNullParameter(value, "value");
        videoMessagesCamera$delegate.setValue(null, $$delegatedProperties[107], value);
    }

    public static final boolean getRememberLastUsedCamera() {
        return ((Boolean) rememberLastUsedCamera$delegate.getValue(null, $$delegatedProperties[108])).booleanValue();
    }

    public static final void setRememberLastUsedCamera(boolean z) {
        rememberLastUsedCamera$delegate.setValue(null, $$delegatedProperties[108], Boolean.valueOf(z));
    }

    public static final boolean getStartWithWideAngleCamera() {
        return ((Boolean) startWithWideAngleCamera$delegate.getValue(null, $$delegatedProperties[109])).booleanValue();
    }

    public static final void setStartWithWideAngleCamera(boolean z) {
        startWithWideAngleCamera$delegate.setValue(null, $$delegatedProperties[109], Boolean.valueOf(z));
    }

    public static final boolean getZoomSlider() {
        return ((Boolean) zoomSlider$delegate.getValue(null, $$delegatedProperties[110])).booleanValue();
    }

    public static final void setZoomSlider(boolean z) {
        zoomSlider$delegate.setValue(null, $$delegatedProperties[110], Boolean.valueOf(z));
    }

    public static final boolean getStaticZoom() {
        return ((Boolean) staticZoom$delegate.getValue(null, $$delegatedProperties[111])).booleanValue();
    }

    public static final void setStaticZoom(boolean z) {
        staticZoom$delegate.setValue(null, $$delegatedProperties[111], Boolean.valueOf(z));
    }

    public static final boolean getAlwaysSendInHD() {
        return ((Boolean) alwaysSendInHD$delegate.getValue(null, $$delegatedProperties[112])).booleanValue();
    }

    public static final void setAlwaysSendInHD(boolean z) {
        alwaysSendInHD$delegate.setValue(null, $$delegatedProperties[112], Boolean.valueOf(z));
    }

    public static final boolean getHideCameraTile() {
        return ((Boolean) hideCameraTile$delegate.getValue(null, $$delegatedProperties[113])).booleanValue();
    }

    public static final void setHideCameraTile(boolean z) {
        hideCameraTile$delegate.setValue(null, $$delegatedProperties[113], Boolean.valueOf(z));
    }

    public static final int getDoubleTapSeekDuration() {
        return ((Number) doubleTapSeekDuration$delegate.getValue(null, $$delegatedProperties[114])).intValue();
    }

    public static final void setDoubleTapSeekDuration(int i) {
        doubleTapSeekDuration$delegate.setValue(null, $$delegatedProperties[114], Integer.valueOf(i));
    }

    public static final boolean getPreferOriginalQuality() {
        return ((Boolean) preferOriginalQuality$delegate.getValue(null, $$delegatedProperties[115])).booleanValue();
    }

    public static final void setPreferOriginalQuality(boolean z) {
        preferOriginalQuality$delegate.setValue(null, $$delegatedProperties[115], Boolean.valueOf(z));
    }

    public static final boolean getSwipeToPip() {
        return ((Boolean) swipeToPip$delegate.getValue(null, $$delegatedProperties[116])).booleanValue();
    }

    public static final void setSwipeToPip(boolean z) {
        swipeToPip$delegate.setValue(null, $$delegatedProperties[116], Boolean.valueOf(z));
    }

    public static final boolean getUnmuteWithVolumeButtons() {
        return ((Boolean) unmuteWithVolumeButtons$delegate.getValue(null, $$delegatedProperties[117])).booleanValue();
    }

    public static final void setUnmuteWithVolumeButtons(boolean z) {
        unmuteWithVolumeButtons$delegate.setValue(null, $$delegatedProperties[117], Boolean.valueOf(z));
    }

    public static final boolean getPauseOnMinimizeVideo() {
        return ((Boolean) pauseOnMinimizeVideo$delegate.getValue(null, $$delegatedProperties[118])).booleanValue();
    }

    public static final void setPauseOnMinimizeVideo(boolean z) {
        pauseOnMinimizeVideo$delegate.setValue(null, $$delegatedProperties[118], Boolean.valueOf(z));
    }

    public static final boolean getPauseOnMinimizeVoice() {
        return ((Boolean) pauseOnMinimizeVoice$delegate.getValue(null, $$delegatedProperties[119])).booleanValue();
    }

    public static final void setPauseOnMinimizeVoice(boolean z) {
        pauseOnMinimizeVoice$delegate.setValue(null, $$delegatedProperties[119], Boolean.valueOf(z));
    }

    public static final boolean getPauseOnMinimizeRound() {
        return ((Boolean) pauseOnMinimizeRound$delegate.getValue(null, $$delegatedProperties[120])).booleanValue();
    }

    public static final void setPauseOnMinimizeRound(boolean z) {
        pauseOnMinimizeRound$delegate.setValue(null, $$delegatedProperties[120], Boolean.valueOf(z));
    }

    public static final boolean getUseGoogleCrashlytics() {
        return ((Boolean) useGoogleCrashlytics$delegate.getValue(null, $$delegatedProperties[121])).booleanValue();
    }

    public static final void setUseGoogleCrashlytics(boolean z) {
        useGoogleCrashlytics$delegate.setValue(null, $$delegatedProperties[121], Boolean.valueOf(z));
    }

    public static final boolean getUseGoogleAnalytics() {
        return ((Boolean) useGoogleAnalytics$delegate.getValue(null, $$delegatedProperties[122])).booleanValue();
    }

    public static final void setUseGoogleAnalytics(boolean z) {
        useGoogleAnalytics$delegate.setValue(null, $$delegatedProperties[122], Boolean.valueOf(z));
    }

    public static final boolean getEnableAdBlock() {
        return ((Boolean) enableAdBlock$delegate.getValue(null, $$delegatedProperties[123])).booleanValue();
    }

    public static final void setEnableAdBlock(boolean z) {
        enableAdBlock$delegate.setValue(null, $$delegatedProperties[123], Boolean.valueOf(z));
    }

    public static final long getUpdateScheduleTimestamp() {
        return ((Number) updateScheduleTimestamp$delegate.getValue(null, $$delegatedProperties[124])).longValue();
    }

    public static final void setUpdateScheduleTimestamp(long j) {
        updateScheduleTimestamp$delegate.setValue(null, $$delegatedProperties[124], Long.valueOf(j));
    }

    public static final long getSdkUpdateScheduleTimestamp() {
        return ((Number) sdkUpdateScheduleTimestamp$delegate.getValue(null, $$delegatedProperties[125])).longValue();
    }

    public static final void setSdkUpdateScheduleTimestamp(long j) {
        sdkUpdateScheduleTimestamp$delegate.setValue(null, $$delegatedProperties[125], Long.valueOf(j));
    }

    public static final String getTargetLang() {
        return (String) targetLang$delegate.getValue(null, $$delegatedProperties[126]);
    }

    public static final void setTargetLang(String str) {
        Intrinsics.checkNotNullParameter(str, "value");
        targetLang$delegate.setValue(null, $$delegatedProperties[126], str);
    }

    public static final float getFlashWarmth() {
        return ((Number) flashWarmth$delegate.getValue(null, $$delegatedProperties[127])).floatValue();
    }

    public static final void setFlashWarmth(float f) {
        flashWarmth$delegate.setValue(null, $$delegatedProperties[127], Float.valueOf(f));
    }

    public static final float getFlashIntensity() {
        return ((Number) flashIntensity$delegate.getValue(null, $$delegatedProperties[128])).floatValue();
    }

    public static final void setFlashIntensity(float f) {
        flashIntensity$delegate.setValue(null, $$delegatedProperties[128], Float.valueOf(f));
    }

    public static final boolean getPluginsDevMode() {
        return ((Boolean) pluginsDevMode$delegate.getValue(null, $$delegatedProperties[129])).booleanValue();
    }

    public static final void setPluginsDevMode(boolean z) {
        pluginsDevMode$delegate.setValue(null, $$delegatedProperties[129], Boolean.valueOf(z));
    }

    public static final boolean getPluginsSafeMode() {
        return ((Boolean) pluginsSafeMode$delegate.getValue(null, $$delegatedProperties[130])).booleanValue();
    }

    public static final void setPluginsSafeMode(boolean z) {
        pluginsSafeMode$delegate.setValue(null, $$delegatedProperties[130], Boolean.valueOf(z));
    }

    public static final boolean getPluginsCompactView() {
        return ((Boolean) pluginsCompactView$delegate.getValue(null, $$delegatedProperties[131])).booleanValue();
    }

    public static final void setPluginsCompactView(boolean z) {
        pluginsCompactView$delegate.setValue(null, $$delegatedProperties[131], Boolean.valueOf(z));
    }

    public static final boolean getPluginsPySdkAutoUpdate() {
        return ((Boolean) pluginsPySdkAutoUpdate$delegate.getValue(null, $$delegatedProperties[132])).booleanValue();
    }

    public static final void setPluginsPySdkAutoUpdate(boolean z) {
        pluginsPySdkAutoUpdate$delegate.setValue(null, $$delegatedProperties[132], Boolean.valueOf(z));
    }

    public static final boolean getPluginsPySdkBetaVersions() {
        return ((Boolean) pluginsPySdkBetaVersions$delegate.getValue(null, $$delegatedProperties[133])).booleanValue();
    }

    public static final void setPluginsPySdkBetaVersions(boolean z) {
        pluginsPySdkBetaVersions$delegate.setValue(null, $$delegatedProperties[133], Boolean.valueOf(z));
    }

    public static final boolean getPluginsDisableArtOpts() {
        return ((Boolean) pluginsDisableArtOpts$delegate.getValue(null, $$delegatedProperties[134])).booleanValue();
    }

    public static final void setPluginsDisableArtOpts(boolean z) {
        pluginsDisableArtOpts$delegate.setValue(null, $$delegatedProperties[134], Boolean.valueOf(z));
    }

    public static final boolean getPluginsUnknownSources() {
        return ((Boolean) pluginsUnknownSources$delegate.getValue(null, $$delegatedProperties[135])).booleanValue();
    }

    public static final void setPluginsUnknownSources(boolean z) {
        pluginsUnknownSources$delegate.setValue(null, $$delegatedProperties[135], Boolean.valueOf(z));
    }

    public static final Set<String> getPinnedPlugins() {
        return (Set) pinnedPlugins$delegate.getValue(null, $$delegatedProperties[136]);
    }

    public static final void setPinnedPlugins(Set<String> set) {
        Intrinsics.checkNotNullParameter(set, "value");
        pinnedPlugins$delegate.setValue(null, $$delegatedProperties[136], set);
    }

    public static final boolean getUseSystemIconShape() {
        return ((Boolean) useSystemIconShape$delegate.getValue(null, $$delegatedProperties[137])).booleanValue();
    }

    public static final void setUseSystemIconShape(boolean z) {
        useSystemIconShape$delegate.setValue(null, $$delegatedProperties[137], Boolean.valueOf(z));
    }

    public static final boolean getAospTransitions() {
        return getTransitionAnimation() == TransitionAnimation.AOSP;
    }

    public static final boolean getSpringSwipeback() {
        return getTransitionAnimation() != TransitionAnimation.DEFAULT;
    }

    public static final boolean getSpringAnimations() {
        return getTransitionAnimation() == TransitionAnimation.SPRING;
    }

    public static final boolean isProxyDisabledOn(ProxyDisableCondition proxyDisableCondition) {
        return (proxyDisableCondition.getFlag() & getDoNotUseProxy()) != 0;
    }

    public static final void setProxyDisabledOn(ProxyDisableCondition proxyDisableCondition, boolean z) {
        int doNotUseProxy;
        if (z) {
            doNotUseProxy = proxyDisableCondition.getFlag() | getDoNotUseProxy();
        } else {
            doNotUseProxy = (~proxyDisableCondition.getFlag()) & getDoNotUseProxy();
        }
        setDoNotUseProxy(doNotUseProxy);
    }

    public static final ArrayList<String> getDoNotMarkAsNew() {
        return doNotMarkAsNew;
    }

    public static final HashMap<String, Long> getNewFeaturesShowedAt() {
        return newFeaturesShowedAt;
    }

    public static final ArrayList<String> getIconPacksLayout() {
        return iconPacksLayout;
    }

    public static final ArrayList<String> getIconPacksHidden() {
        return iconPacksHidden;
    }

    public static final boolean getSectionsSeparatedHeaders() {
        return getDividerStyle() == DividerStyle.SEGMENTS || getSectionsSeparatedHeadersPreference();
    }

    public static final void setSectionsSeparatedHeaders(boolean z) {
        if (getDividerStyle() == DividerStyle.SEGMENTS) {
            z = true;
        }
        setSectionsSeparatedHeadersPreference(z);
    }

    public static final ArrayList<Integer> getMainMenuLayout() {
        return mainMenuLayout;
    }

    public static final ArrayList<Integer> getMainMenuHiddenItems() {
        return mainMenuHiddenItems;
    }

    public static final SearchEngine getYandexSearchEngine() {
        return yandexSearchEngine;
    }

    public static final boolean getPluginsEngine() {
        return pluginsEngine;
    }

    public static final void setPluginsEngine(boolean z) {
        pluginsEngine = z;
    }
public static final void init() {
        loadConfig();
        if (servicesStarted.compareAndSet(false, true)) {
            ApiController.init();
            RemoteUtils.init();
            PluginsController.INSTANCE.getInstance().init(getPluginsSafeMode(), new Runnable() { // from class: com.exteragram.messenger.ExteraConfig$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    ExteraConfig.m829$r8$lambda$HHqU03vj9BDWuJAcACYOf1spE();
                }
            });
            ChatUtils.utilsQueue.postRunnable(new Runnable() { // from class: com.exteragram.messenger.ExteraConfig$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    AdBlockManager.initialize();
                }
            });
            IconManager.INSTANCE.prefetchCustomPacks();
        }
    }

    /* JADX INFO: renamed from: $r8$lambda$HHqU03vj9B-DWu-JAcACYOf1spE, reason: not valid java name */
    public static void m829$r8$lambda$HHqU03vj9BDWuJAcACYOf1spE() {
        PluginsController.Companion companion = PluginsController.INSTANCE;
        companion.getInstance().executeOnAppEvent("app_start");
        if (ApplicationLoader.mainInterfacePaused) {
            return;
        }
        companion.getInstance().executeOnAppEvent("app_resume");
    }

    private static final void migrateProxyConditions() {
        SharedPreferences sharedPreferences = preferences;
        if (sharedPreferences.contains("doNotUseProxyWithVpn")) {
            if (sharedPreferences.getBoolean("doNotUseProxyWithVpn", false)) {
                setProxyDisabledOn(ProxyDisableCondition.VPN, true);
            }
            editor.remove("doNotUseProxyWithVpn").apply();
        }
    }

    private static final void migrateTabCounterMode() {
        SharedPreferences sharedPreferences = preferences;
        if (sharedPreferences.contains("tabCounter")) {
            if (!sharedPreferences.getBoolean("tabCounter", true)) {
                setTabCounterMode(TabCounterMode.HIDDEN);
            }
            editor.remove("tabCounter").apply();
        }
    }

    public static final void loadConfig() {
        ArrayList<String> arrayList;
        HashMap<String, Long> map;
        ArrayList<Integer> arrayList2;
        ArrayList<String> arrayList3;
        synchronized (sync) {
            try {
                if (configLoaded) {
                    return;
                }
                SharedPreferences sharedPreferences = preferences;
                BottomNavigationBar.setMode(sharedPreferences.getInt("bottomNavigationBarMode", 0));
                String string = sharedPreferences.getString("iconPacksLayout", null);
                String string2 = sharedPreferences.getString("iconPacksHidden", null);
                if (string != null) {
                    Gson gson = GSON;
                    iconPacksLayout = (ArrayList) gson.fromJson(string, new TypeToken<ArrayList<String>>() { // from class: com.exteragram.messenger.ExteraConfig$loadConfig$1$1
                    }.getType());
                    if (string2 != null) {
                        arrayList3 = (ArrayList) gson.fromJson(string2, new TypeToken<ArrayList<String>>() { // from class: com.exteragram.messenger.ExteraConfig$loadConfig$1$2
                        }.getType());
                    } else {
                        arrayList3 = new ArrayList<>();
                    }
                    iconPacksHidden = arrayList3;
                    Iterator<String> it = iconPacksLayout.iterator();
                    boolean z = false;
                    String str = null;
                    while (it.hasNext()) {
                        String next = it.next();
                        if (next.startsWith("base.")) {
                            if (str == null) {
                                str = next;
                            } else {
                                it.remove();
                                z = true;
                            }
                        }
                    }
                    if (str == null) {
                        iconPacksLayout.add("base.default");
                        z = true;
                    }
                    if (z) {
                        saveIconPacksLayout();
                    }
                } else {
                    iconPacksLayout = new ArrayList<>();
                    iconPacksHidden = new ArrayList<>();
                    String[] strArr = {"base.default", "base.solar", "base.remix"};
                    int i = 0;
                    int i2 = 0;
                    while (i < 3) {
                        int i3 = i2 + 1;
                        (i2 == getIconPack().ordinal() ? iconPacksLayout : iconPacksHidden).add(strArr[i]);
                        i++;
                        i2 = i3;
                    }
                    saveIconPacksLayout();
                }
                String[] strArr2 = {"base.default", "base.solar", "base.remix"};
                boolean z2 = false;
                for (int i4 = 0; i4 < 3; i4++) {
                    String str2 = strArr2[i4];
                    if (!iconPacksLayout.contains(str2) && !iconPacksHidden.contains(str2)) {
                        iconPacksHidden.add(str2);
                        z2 = true;
                    }
                }
                if (z2) {
                    saveIconPacksLayout();
                }
                SharedPreferences sharedPreferences2 = preferences;
                String string3 = sharedPreferences2.getString("doNotMarkAsNew", null);
                if (string3 == null || (arrayList = (ArrayList) GSON.fromJson(string3, new TypeToken<ArrayList<String>>() { // from class: com.exteragram.messenger.ExteraConfig$loadConfig$1$4$1
                }.getType())) == null) {
                    arrayList = new ArrayList<>();
                }
                doNotMarkAsNew = arrayList;
                String string4 = sharedPreferences2.getString("newFeaturesShowedAt", null);
                if (string4 == null || (map = (HashMap) GSON.fromJson(string4, new TypeToken<HashMap<String, Long>>() { // from class: com.exteragram.messenger.ExteraConfig$loadConfig$1$5$1
                }.getType())) == null) {
                    map = new HashMap<>();
                }
                newFeaturesShowedAt = map;
                String string5 = sharedPreferences2.getString("mainMenuLayout", null);
                String string6 = sharedPreferences2.getString("mainMenuHiddenItems", null);
                Log.i("ZWY", "read layoutJson: " + string5);
                Log.i("ZWY", "read hiddenJson: " + string6);
                if (string5 != null) {
                    Gson gson2 = GSON;
                    mainMenuLayout = (ArrayList) gson2.fromJson(string5, new TypeToken<ArrayList<Integer>>() { // from class: com.exteragram.messenger.ExteraConfig$loadConfig$1$6
                    }.getType());
                    if (string6 != null) {
                        arrayList2 = (ArrayList) gson2.fromJson(string6, new TypeToken<ArrayList<Integer>>() { // from class: com.exteragram.messenger.ExteraConfig$loadConfig$1$7
                        }.getType());
                    } else {
                        arrayList2 = new ArrayList<>();
                    }
                    mainMenuHiddenItems = arrayList2;
                } else {
                    mainMenuLayout = new ArrayList<>();
                    mainMenuHiddenItems = new ArrayList<>();
                    mainMenuLayout.addAll(getDefaultMainMenuLayout());
                    for (MainMenuItem mainMenuItem : MainMenuItem.getEntries()) {
                        if (mainMenuItem != MainMenuItem.DIVIDER && !mainMenuLayout.contains(Integer.valueOf(mainMenuItem.getId())) && (mainMenuItem != MainMenuItem.PLUGINS || PluginsController.INSTANCE.isPluginEngineSupported())) {
                            mainMenuHiddenItems.add(Integer.valueOf(mainMenuItem.getId()));
                        }
                    }
                    saveMainMenuLayout();
                }
                if (!PluginsController.INSTANCE.isPluginEngineSupported()) {
                    pluginsEngine = false;
                    if (preferences.getBoolean("pluginsEngine", false)) {
                        editor.putBoolean("pluginsEngine", false).apply();
                    }
                    int id = MainMenuItem.PLUGINS.getId();
                    mainMenuLayout.remove(Integer.valueOf(id));
                    mainMenuHiddenItems.remove(Integer.valueOf(id));
                } else {
                    pluginsEngine = preferences.getBoolean("pluginsEngine", false);
                    int id2 = MainMenuItem.PLUGINS.getId();
                    if (!mainMenuLayout.contains(Integer.valueOf(id2)) && !mainMenuHiddenItems.contains(Integer.valueOf(id2))) {
                        mainMenuHiddenItems.add(Integer.valueOf(id2));
                    }
                }
                int id3 = MainMenuItem.FEED.getId();
                if (!mainMenuLayout.contains(Integer.valueOf(id3)) && !mainMenuHiddenItems.contains(Integer.valueOf(id3))) {
                    mainMenuLayout.add(Integer.valueOf(id3));
                    saveMainMenuLayout();
                }
                mainMenuLayout.removeAll(CollectionsKt.toSet(mainMenuHiddenItems));
                ensureSettingsVisibility();
                sanitizeMenu();
                migrateProxyConditions();
                TranslatorUtils.ensureTargetLanguageCompatibleWithProvider();
                configLoaded = true;
                Log.i("ZWY", "end mainMenuLayout: " + mainMenuLayout);
                Log.i("ZWY", "end mainMenuHiddenItems: " + mainMenuHiddenItems);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static final void reloadConfig() {
        synchronized (sync) {
            try {
                configLoaded = false;
                Iterator<BasePref<?>> it = PrefClassesKt.getAllDelegates().iterator();
                while (it.hasNext()) {
                    it.next().invalidate();
                }
                loadConfig();
                Unit unit = Unit.INSTANCE;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static final void saveMainMenuLayout() {
        StringBuilder sb = new StringBuilder("save mainMenuLayout: ");
        Gson gson = GSON;
        sb.append(gson.toJson(mainMenuLayout));
        Log.i("ZWY", sb.toString());
        Log.i("ZWY", "save mainMenuHiddenItems: " + gson.toJson(mainMenuHiddenItems));
        editor.putString("mainMenuLayout", gson.toJson(mainMenuLayout)).putString("mainMenuHiddenItems", gson.toJson(mainMenuHiddenItems)).apply();
    }

    public static final void saveIconPacksLayout() {
        String str;
        IconPackType iconPackType;
        ArrayList<String> arrayList = iconPacksLayout;
        int size = arrayList.size();
        int i = 0;
        while (true) {
            str = null;
            if (i >= size) {
                break;
            }
            String str2 = arrayList.get(i);
            i++;
            if (str2.startsWith("base.")) {
                str = str2;
                break;
            }
        }
        String str3 = str;
        if (Intrinsics.areEqual(str3, "base.solar")) {
            iconPackType = IconPackType.SOLAR;
        } else {
            iconPackType = Intrinsics.areEqual(str3, "base.remix") ? IconPackType.REMIX : IconPackType.DEFAULT;
        }
        setIconPack(iconPackType);
        SharedPreferences.Editor editor2 = editor;
        Gson gson = GSON;
        editor2.putString("iconPacksLayout", gson.toJson(iconPacksLayout)).putString("iconPacksHidden", gson.toJson(iconPacksHidden)).apply();
    }

    public static final ArrayList<Integer> getDefaultMainMenuLayout() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(Integer.valueOf(MainMenuItem.ARCHIVE.getId()));
        if (BottomNavigationBar.hidden()) {
            arrayList.add(Integer.valueOf(MainMenuItem.PROFILE.getId()));
        }
        arrayList.add(Integer.valueOf(MainMenuItem.NEW_GROUP.getId()));
        if (BottomNavigationBar.hidden()) {
            arrayList.add(Integer.valueOf(MainMenuItem.CONTACTS.getId()));
        }
        arrayList.add(Integer.valueOf(MainMenuItem.SAVED.getId()));
        arrayList.add(Integer.valueOf(MainMenuItem.FEED.getId()));
        arrayList.add(Integer.valueOf(MainMenuItem.BOTS.getId()));
        if (BottomNavigationBar.hidden()) {
            arrayList.add(Integer.valueOf(MainMenuItem.SETTINGS.getId()));
        }
        return arrayList;
    }

    public static final void ensureSettingsVisibility() {
        if (BottomNavigationBar.hidden()) {
            int id = MainMenuItem.SETTINGS.getId();
            if (mainMenuLayout.contains(Integer.valueOf(id))) {
                return;
            }
            mainMenuHiddenItems.remove(Integer.valueOf(id));
            mainMenuLayout.add(Integer.valueOf(id));
            saveMainMenuLayout();
        }
    }

    public static boolean $r8$lambda$aHGs9QcNJ9KNU5_blrF7OsgQuJQ(Integer num) {
        return num.intValue() != MainMenuItem.DIVIDER.getId() && MainMenuItem.INSTANCE.getById(num.intValue()) == null;
    }

    public static final void sanitizeMenu() {
        ArrayList<Integer> arrayList = mainMenuLayout;
        final Function1 function1 = new Function1() { // from class: com.exteragram.messenger.ExteraConfig$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(ExteraConfig.$r8$lambda$aHGs9QcNJ9KNU5_blrF7OsgQuJQ((Integer) obj));
            }
        };
        boolean zRemoveIf = arrayList.removeIf(new Predicate() { // from class: com.exteragram.messenger.ExteraConfig$$ExternalSyntheticLambda1
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((Boolean) function1.invoke(obj)).booleanValue();
            }
        });
        ArrayList<Integer> arrayList2 = mainMenuHiddenItems;
        final Function1 function2 = new Function1() { // from class: com.exteragram.messenger.ExteraConfig$$ExternalSyntheticLambda2
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(ExteraConfig.$r8$lambda$xGdkDF9LgOMemIpLy3UVJgzFBuQ((Integer) obj));
            }
        };
        boolean zRemoveIf2 = zRemoveIf | arrayList2.removeIf(new Predicate() { // from class: com.exteragram.messenger.ExteraConfig$$ExternalSyntheticLambda3
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ((Boolean) function2.invoke(obj)).booleanValue();
            }
        });
        for (MainMenuItem mainMenuItem : MainMenuItem.getEntries()) {
            if (mainMenuItem != MainMenuItem.DIVIDER && (mainMenuItem != MainMenuItem.PLUGINS || PluginsController.INSTANCE.isPluginEngineSupported())) {
                if (!mainMenuLayout.contains(Integer.valueOf(mainMenuItem.getId())) && !mainMenuHiddenItems.contains(Integer.valueOf(mainMenuItem.getId()))) {
                    mainMenuHiddenItems.add(Integer.valueOf(mainMenuItem.getId()));
                    zRemoveIf2 = true;
                }
            }
        }
        if (zRemoveIf2) {
            saveMainMenuLayout();
        }
    }

    public static boolean $r8$lambda$xGdkDF9LgOMemIpLy3UVJgzFBuQ(Integer num) {
        return num.intValue() != MainMenuItem.DIVIDER.getId() && MainMenuItem.INSTANCE.getById(num.intValue()) == null;
    }

    public static /* synthetic */ int getAvatarCorners$default(float f, boolean z, boolean z2, boolean z3, int i, Object obj) {
        if ((i & 2) != 0) {
            z = false;
        }
        if ((i & 4) != 0) {
            z2 = false;
        }
        if ((i & 8) != 0) {
            z3 = false;
        }
        return getAvatarCorners(f, z, z2, z3);
    }

    @JvmOverloads
    public static final int getAvatarCorners(float f, boolean z, boolean z2, boolean z3) {
        return getAvatarCorners(f, z, z2 ? AvatarCornerType.FORUM : AvatarCornerType.DEFAULT, z3);
    }

    public static /* synthetic */ int getAvatarCorners$default(float f, boolean z, AvatarCornerType avatarCornerType, boolean z2, int i, Object obj) {
        if ((i & 8) != 0) {
            z2 = false;
        }
        return getAvatarCorners(f, z, avatarCornerType, z2);
    }

    @JvmOverloads
    public static final int getAvatarCorners(float f, boolean z, AvatarCornerType avatarCornerType, boolean z2) {
        int i;
        if (getAvatarCorners() == 0.0f) {
            return 0;
        }
        float avatarCorners = (getAvatarCorners() * f) / 56.0f;
        if (z2) {
            avatarCorners -= z ? AndroidUtilities.dpf2(2.5f) : 2.5f;
        }
        if (!z) {
            avatarCorners = AndroidUtilities.dp(avatarCorners);
        }
        if (!getSingleCornerRadius() && (i = WhenMappings.$EnumSwitchMapping$0[avatarCornerType.ordinal()]) != 1) {
            if (i == 2) {
                avatarCorners = (((int) avatarCorners) * 42) >> 6;
            } else {
                if (i != 3) {
                    throw new NoWhenBranchMatchedException();
                }
                avatarCorners = (avatarCorners * 40.0f) / 72.0f;
            }
        }
        return (int) Math.ceil(avatarCorners);
    }

    public static final float getAvatarSquareness() {
        return RangesKt.coerceIn(1.0f - (getAvatarCorners() / 28.0f), 0.0f, 1.0f);
    }

    public static final int getOnlineDotOuterRadius() {
        return AndroidUtilities.dp((getAvatarSquareness() * 2.0f) + 7.0f);
    }

    public static final int getOnlineDotInnerRadius() {
        return AndroidUtilities.dp(getAvatarSquareness() + 5.0f);
    }

    public static final float getOnlineDotOffset(float f, float f2) {
        return f + ((((float) (((double) f2) / Math.sqrt(2.0d))) - f) * getAvatarSquareness());
    }

    public static final int getSectionRadiusDp() {
        return MathKt.roundToInt(sanitizeSectionRadius(getSectionRadius()));
    }

    private static final float sanitizeSectionRadius(float f) {
        if (Float.isFinite(f)) {
            return RangesKt.coerceIn(f, 0.0f, 28.0f);
        }
        return 20.0f;
    }

    private static final SharedPreferences systemConfigPrefs() {
        return ApplicationLoader.applicationContext.getSharedPreferences("systemConfig", 0);
    }

    public static final void toggleLogging() {
        BuildVars.LOGS_ENABLED = !BuildVars.LOGS_ENABLED;
        SharedPreferences.Editor editorEdit = systemConfigPrefs().edit();
        editorEdit.putBoolean("logsEnabled", BuildVars.LOGS_ENABLED);
        editorEdit.apply();
        if (BuildVars.LOGS_ENABLED) {
            return;
        }
        FileLog.cleanupLogs();
    }

    public static final boolean getLogging() {
        return systemConfigPrefs().getBoolean("logsEnabled", false);
    }

    public static final void setLogging(boolean z) {
        BuildVars.LOGS_ENABLED = z;
        SharedPreferences.Editor editorEdit = systemConfigPrefs().edit();
        editorEdit.putBoolean("logsEnabled", z);
        editorEdit.apply();
        if (z) {
            return;
        }
        FileLog.cleanupLogs();
    }

    public static final String getCurrentLangName() {
        return TranslatorUtils.getTargetLanguageTitle();
    }

    public static final int getDoubleTapSeekDurationMillis() {
        int doubleTapSeekDuration = getDoubleTapSeekDuration();
        if (doubleTapSeekDuration == 0 || doubleTapSeekDuration == 1 || doubleTapSeekDuration == 2) {
            return (getDoubleTapSeekDuration() + 1) * 5000;
        }
        return 30000;
    }

    public static final boolean canUseYandexMaps() {
        return getUseYandexMaps() && ApplicationLoader.applicationLoaderInstance != null && ApplicationLoader.applicationLoaderInstance.allowToUseYandexMaps();
    }

    public static void setSpringAnimations(boolean value) {
        setTransitionAnimation(value ? TransitionAnimation.SPRING : TransitionAnimation.DEFAULT);
    }

    public static boolean getHideStickerTime() {
        return getStickerTimeMode() == StickerTimeMode.HIDDEN;
    }

    public static void setHideStickerTime(boolean value) {
        setStickerTimeMode(value ? StickerTimeMode.HIDDEN : StickerTimeMode.DEFAULT);
    }

    private static TranslationFormality translationFormality = TranslationFormality.NONE;

    public static TranslationFormality getTranslationFormality() {
        return translationFormality != null ? translationFormality : TranslationFormality.NONE;
    }

    public static void setTranslationFormality(TranslationFormality value) {
        translationFormality = value;
    }
}
