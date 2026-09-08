package org.telegram.messenger;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.util.SparseIntArray;
import android.view.View;
import android.widget.FrameLayout;
import androidx.collection.LongSparseArray;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.Consumer;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.plugins.hooks.PluginsHooks;
import com.exteragram.messenger.utils.network.RemoteUtils;
import com.exteragram.messenger.utils.ui.MonetUtils;
import com.google.android.gms.cast.MediaError;
import java.io.File;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collectors;
import kotlin.jvm.internal.ByteCompanionObject;
import kotlin.jvm.internal.LongCompanionObject;
import kotlin.time.DurationKt;
import me.vkryl.core.BitwiseUtils;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLiteException;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.messenger.support.LongSparseLongArray;
import org.telegram.messenger.utils.EphemeralMessagesHelper;
import org.telegram.messenger.voip.ConferenceCall;
import org.telegram.messenger.voip.GroupCallMessagesController;
import org.telegram.messenger.voip.VoIPDebugToSend;
import org.telegram.messenger.voip.VoIPPreNotificationService;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.tgnet.tl.TL_chatlists;
import org.telegram.tgnet.tl.TL_communities;
import org.telegram.tgnet.tl.TL_forum;
import org.telegram.tgnet.tl.TL_phone;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.tgnet.tl.TL_update;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Business.QuickRepliesController;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatRightsEditActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.JoinCallAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.TranscribeButton;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.MainTabsActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.SecretMediaViewer;
import org.telegram.ui.Stars.BotStarsController;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stories.HighlightMessageSheet;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.ThemeActivity;
import org.telegram.ui.TopicsFragment;
import org.telegram.ui.bots.BotWebViewSheet;
import org.telegram.ui.bots.WebViewRequestProps;
import org.telegram.ui.community.CommunityChatType;
import org.telegram.ui.community.CommunityUtils;

public class MessagesController extends BaseController implements NotificationCenter.NotificationCenterDelegate {
    public static int DIALOG_FILTER_FLAG_BOTS = 16;
    public static int DIALOG_FILTER_FLAG_CHANNELS = 8;
    public static int DIALOG_FILTER_FLAG_CHATLIST = 512;
    public static int DIALOG_FILTER_FLAG_CHATLIST_ADMIN = 1024;
    public static int DIALOG_FILTER_FLAG_CONTACTS = 1;
    public static int DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED = 128;
    public static int DIALOG_FILTER_FLAG_EXCLUDE_MUTED = 32;
    public static int DIALOG_FILTER_FLAG_EXCLUDE_READ = 64;
    public static int DIALOG_FILTER_FLAG_GROUPS = 4;
    public static int DIALOG_FILTER_FLAG_NON_CONTACTS = 2;
    public static int DIALOG_FILTER_FLAG_ONLY_ARCHIVED = 256;
    public static final int EMAIL_SUGGESTION_FALSE = 0;
    public static final int EMAIL_SUGGESTION_TRUE = 1;
    public static final int EMAIL_SUGGESTION_TRUE_FORCED = 2;
    public static final int LOAD_AROUND_DATE = 4;
    public static final int LOAD_AROUND_MESSAGE = 3;
    public static final int LOAD_BACKWARD = 0;
    public static final int LOAD_FORWARD = 1;
    public static final int LOAD_FROM_UNREAD = 2;
    public static int PROMO_TYPE_OTHER = 2;
    public static int PROMO_TYPE_PROXY = 0;
    public static int PROMO_TYPE_PSA = 1;
    public static final int TOGGLE_NO_FORWARDS_RESULT_ERROR = 0;
    public static final int TOGGLE_NO_FORWARDS_RESULT_OK = 1;
    public static final int TOGGLE_NO_FORWARDS_RESULT_PENDING = 2;
    public static int UPDATE_MASK_AVATAR = 2;
    public static int UPDATE_MASK_CHAT = 8192;
    public static int UPDATE_MASK_CHAT_AVATAR = 8;
    public static int UPDATE_MASK_CHAT_MEMBERS = 32;
    public static int UPDATE_MASK_CHAT_NAME = 16;
    public static int UPDATE_MASK_CHECK = 65536;
    public static int UPDATE_MASK_EMOJI_INTERACTIONS = 262144;
    public static int UPDATE_MASK_EMOJI_STATUS = 524288;
    public static int UPDATE_MASK_MESSAGE_TEXT = 32768;
    public static int UPDATE_MASK_NAME = 1;
    public static int UPDATE_MASK_NEW_MESSAGE = 2048;
    public static int UPDATE_MASK_PHONE = 1024;
    public static int UPDATE_MASK_REACTIONS_READ = 1048576;
    public static int UPDATE_MASK_READ_DIALOG_MESSAGE = 256;
    public static int UPDATE_MASK_REORDER = 131072;
    public static int UPDATE_MASK_SELECT_DIALOG = 512;
    public static int UPDATE_MASK_SEND_STATE = 4096;
    public static int UPDATE_MASK_STATUS = 4;
    public static int UPDATE_MASK_USER_PHONE = 128;
    public static int UPDATE_MASK_USER_PRINT = 64;
    private static volatile long lastPasswordCheckTime = 0;
    private static volatile long lastThemeCheckTime = 0;
    public static int stableIdPointer = 100;
    private int DIALOGS_LOAD_TYPE_CACHE;
    private int DIALOGS_LOAD_TYPE_CHANNEL;
    private int DIALOGS_LOAD_TYPE_UNKNOWN;
    public int aboutLengthLimitDefault;
    public int aboutLengthLimitPremium;
    private final HashMap<Long, TLRPC.Chat> activeVoiceChatsMap;
    protected final ArrayList<TLRPC.Dialog> allDialogs;
    public boolean androidDisableRoundCamera2;
    public float animatedEmojisZoom;
    private final CacheFetcher<Integer, TLRPC.TL_help_appConfig> appConfigFetcher;
    public Set<String> authDomains;
    public int authorizationAutoconfirmPeriod;
    public boolean autoarchiveAvailable;
    public Set<String> autologinDomains;
    public String autologinToken;
    private TLRPC.messages_AvailableEffects availableEffects;
    public int availableMapProviders;
    public boolean backgroundConnection;
    public LongSparseIntArray blockePeers;
    public boolean blockedCountry;
    public boolean blockedEndReached;
    public int boostsChannelLevelMax;
    public long boostsPerSentGift;
    public int botPreviewMediasMax;
    public int botVerificationDescriptionLengthLimit;
    public int businessChatLinksLimit;
    public SparseIntArray businessFeaturesTypesToPosition;
    private CacheByChatsController cacheByChatsController;
    private HashMap<Long, ChannelRecommendations> cachedChannelRecommendations;
    private TLRPC.TL_exportedContactToken cachedContactToken;
    private final LongSparseArray<TL_account.RequirementToContact> cachedIsUserContactBlocked;
    public int callConnectTimeout;
    public int callPacketTimeout;
    public int callReceiveTimeout;
    public boolean callRequestsDisabled;
    public int callRingTimeout;
    public boolean canEditFactcheck;
    public boolean canRevokePmInbox;
    public int captionLengthLimitDefault;
    public int captionLengthLimitPremium;
    private LongSparseArray<LongSparseArray<TLRPC.ChannelParticipant>> channelAdmins;
    public int channelAutotranslationLevelMin;
    public int channelBgIconLevelMin;
    private ChannelBoostsController channelBoostsControler;
    public int channelCustomWallpaperLevelMin;
    public int channelEmojiStatusLevelMin;
    public int channelProfileIconLevelMin;
    public int channelRestrictSponsoredLevelMin;
    public boolean channelRevenueWithdrawalEnabled;
    private LongSparseArray<ArrayList<Integer>> channelViewsToSend;
    public int channelWallpaperLevelMin;
    public int channelsLimitDefault;
    public int channelsLimitPremium;
    private LongSparseIntArray channelsPts;
    public int chatReadMarkExpirePeriod;
    public int chatReadMarkSizeThreshold;
    private SparseArray<ChatlistUpdatesStat> chatlistFoldersUpdates;
    public int chatlistInvitesLimitDefault;
    public int chatlistInvitesLimitPremium;
    public int chatlistJoinedLimitDefault;
    public int chatlistJoinedLimitPremium;
    private int chatlistUpdatePeriod;
    private final ConcurrentHashMap<Long, TLRPC.Chat> chats;
    public int checkResetLangpack;
    private LongSparseArray<Boolean> checkingLastMessagesDialogs;
    private boolean checkingPromoInfo;
    private int checkingPromoInfoRequestId;
    private boolean checkingTosUpdate;
    private LongSparseArray<TLRPC.Dialog> clearingHistoryDialogs;
    public boolean collectDeviceStats;
    public final android.util.LongSparseArray<CommonChatsList> commonChats;
    private Comparator<CommunityPeerDialog> communityPeerDialogComparator;
    public int conferenceCallSizeLimit;
    public final AppGlobalConfig config;
    private TL_account.contentSettings contentSettings;
    private ArrayList<Utilities.Callback<TL_account.contentSettings>> contentSettingsCallbacks;
    private long contentSettingsLoadedTime;
    private boolean contentSettingsLoading;
    private ArrayList<Long> createdDialogIds;
    private ArrayList<Long> createdDialogMainThreadIds;
    private ArrayList<Long> createdScheduledDialogIds;
    private Runnable currentDeleteTaskRunnable;
    private LongSparseArray<ArrayList<Integer>> currentDeletingTaskMediaMids;
    private LongSparseArray<ArrayList<Integer>> currentDeletingTaskMids;
    private int currentDeletingTaskTime;
    public TLRPC.TL_pendingSuggestion customPendingSuggestion;
    public String dcDomainName;
    public LongSparseIntArray deletedHistory;
    private LongSparseArray<TLRPC.Dialog> deletingDialogs;
    private Comparator<TLRPC.Dialog> dialogComparator;
    private final Comparator<TLRPC.Dialog> dialogDateComparator;
    public ArrayList<DialogFilter> dialogFilters;
    public SparseArray<DialogFilter> dialogFiltersById;
    public int dialogFiltersChatsLimitDefault;
    public int dialogFiltersChatsLimitPremium;
    public int dialogFiltersLimitDefault;
    public int dialogFiltersLimitPremium;
    public boolean dialogFiltersLoaded;
    public int dialogFiltersPinnedLimitDefault;
    public int dialogFiltersPinnedLimitPremium;
    public LongSparseArray<ArrayList<MessageObject>> dialogMessage;
    public SparseArray<MessageObject> dialogMessagesByIds;
    public LongSparseArray<MessageObject> dialogMessagesByRandomIds;
    private LongSparseArray<DialogPhotos> dialogPhotos;
    private final LongSparseArray<ArrayList<TLRPC.Dialog>> dialogsByCommunity;
    public final SparseArray<ArrayList<TLRPC.Dialog>> dialogsByFolder;
    public ArrayList<TLRPC.Dialog> dialogsCanAddUsers;
    public ArrayList<TLRPC.Dialog> dialogsChannelsOnly;
    private final LongSparseArray<TLRPC.Dialog> dialogsCommunityFoundCommunities;
    private final LongSparseIntArray dialogsCommunityLastMessageDate;
    private final LongSparseIntArray dialogsCommunityUnreadCount;
    private final LongSparseIntArray dialogsCommunityUnreadMark;
    private SparseBooleanArray dialogsEndReached;
    public ArrayList<TLRPC.Dialog> dialogsForBlock;
    public ArrayList<TLRPC.Dialog> dialogsForward;
    public ArrayList<TLRPC.Dialog> dialogsGroupsOnly;
    private boolean dialogsInTransaction;
    public boolean dialogsLoaded;
    private int dialogsLoadedTillDate;
    public ArrayList<TLRPC.Dialog> dialogsMyChannels;
    public ArrayList<TLRPC.Dialog> dialogsMyGroups;
    public ArrayList<TLRPC.Dialog> dialogsServerOnly;
    public ArrayList<TLRPC.Dialog> dialogsUsersOnly;
    public LongSparseArray<TLRPC.Dialog> dialogs_dict;
    public ConcurrentHashMap<Long, Integer> dialogs_read_inbox_max;
    public ConcurrentHashMap<Long, Integer> dialogs_read_outbox_max;
    public HashSet<String> diceEmojies;
    public HashMap<String, DiceFrameSuccess> diceSuccess;
    public List<String> directPaymentsCurrency;
    public boolean disableBotFullscreenBlur;
    public Set<String> dismissedSuggestions;
    private final CacheFetcher<Integer, TLRPC.messages_AvailableEffects> effectsFetcher;
    private boolean emailSuggestionWasShown;
    public HashMap<Long, ArrayList<TLRPC.TL_sendMessageEmojiInteraction>> emojiInteractions;
    private final SharedPreferences emojiPreferences;
    public HashMap<String, EmojiSound> emojiSounds;
    private final ConcurrentHashMap<Long, Integer> emojiStatusUntilValues;
    public boolean enableGiftsInProfile;
    public boolean enableJoined;
    private final ConcurrentHashMap<Integer, TLRPC.EncryptedChat> encryptedChats;
    public Set<String> exportGroupUri;
    public Set<String> exportPrivateUri;
    public Set<String> exportUri;
    private final LongSparseArray<TLRPC.TL_chatInviteExported> exportedChats;
    public int factcheckLengthLimit;
    public ArrayList<FaqSearchResult> faqSearchArray;
    public TLRPC.WebPage faqWebPage;
    public boolean filtersEnabled;
    public boolean firstGettingTask;
    public boolean folderTags;
    public int forumUpgradeParticipantsMin;
    public String freezeAppealUrl;
    public long freezeSinceDate;
    public long freezeUntilDate;
    public ArrayList<DialogFilter> frozenDialogFilters;
    private final LongSparseArray<TLRPC.ChatFull> fullChats;
    private final LongSparseArray<TLRPC.UserFull> fullUsers;
    private boolean getDifferenceFirstSync;
    public boolean getfileExperimentalParams;
    private LongSparseArray<Boolean> gettingChatInviters;
    public boolean gettingDifference;
    private LongSparseArray<Boolean> gettingDifferenceChannels;
    private boolean gettingNewDeleteTask;
    private LongSparseArray<Boolean> gettingUnknownChannels;
    private LongSparseArray<Boolean> gettingUnknownDialogs;
    public String gifSearchBot;
    public ArrayList<String> gifSearchEmojies;
    public boolean giftAttachMenuIcon;
    public boolean giftTextFieldIcon;
    public long giveawayAddPeersMax;
    public long giveawayBoostsPerPremium;
    public long giveawayCountriesMax;
    public boolean giveawayGiftsPurchaseAvailable;
    public long giveawayPeriodMax;
    public int groupCallVideoMaxParticipants;
    private final LongSparseArray<ChatObject.Call> groupCalls;
    private final LongSparseArray<ChatObject.Call> groupCallsByChatId;
    public int groupCustomWallpaperLevelMin;
    public int groupEmojiStatusLevelMin;
    public int groupEmojiStickersLevelMin;
    public int groupProfileBgIconLevelMin;
    public int groupTranscribeLevelMin;
    public int groupWallpaperLevelMin;
    private boolean hasArchivedChats;
    private boolean hasStories;
    public int hiddenMembersGroupSizeMin;
    public ArrayList<Long> hiddenUndoChats;
    public ArrayList<TLRPC.RecentMeUrl> hintDialogs;
    private final PluginsHooks hooks;
    public Set<String> ignoreRestrictionReasons;
    public volatile boolean ignoreSetOnline;
    public String imageSearchBot;
    private String installReferer;
    public int introDescriptionLengthLimit;
    public int introTitleLengthLimit;
    private boolean isLeftPromoChannel;
    private ArrayList<TLRPC.Chat> joinedCommunities;
    private final ArrayList<Long> joiningToChannels;
    public boolean keepAliveService;
    public int largeQueueMaxActiveOperations;
    private int lastCheckPromoId;
    private long lastCheckPromoInfoTime;
    public int lastKnownSessionsCount;
    private int lastPrintingStringCount;
    private long lastPushRegisterSendTime;
    private LongSparseArray<Long> lastQuickReplyServerQueryTime;
    private LongSparseArray<Long> lastSavedServerQueryTime;
    private LongSparseArray<Long> lastScheduledServerQueryTime;
    private LongSparseArray<Long> lastServerQueryTime;
    private long lastStatusUpdateTime;
    private long lastViewsCheckTime;
    public String linkPrefix;
    private Runnable loadAppConfigRunnable;
    private final Runnable loadWebConfigRunnable;
    public LongSparseLongArray loadedFullChats;
    private HashSet<Long> loadedFullParticipants;
    private LongSparseLongArray loadedFullUsers;
    private boolean loadingArePaidReactionsAnonymous;
    private boolean loadingAvailableEffects;
    public boolean loadingBlockedPeers;
    private LongSparseIntArray loadingChannelAdmins;
    private SparseBooleanArray loadingDialogs;
    private HashSet<Long> loadingFullChats;
    private HashSet<Long> loadingFullParticipants;
    private HashSet<Long> loadingFullUsers;
    private HashSet<Long> loadingGroupCalls;
    private final HashSet<Long> loadingIsUserContactBlocked;
    private int loadingNotificationSettings;
    private boolean loadingNotificationSignUpSettings;
    private boolean loadingPeerColors;
    private LongSparseArray<Boolean> loadingPeerSettings;
    private SparseIntArray loadingPinnedDialogs;
    private boolean loadingProfilePeerColors;
    private HashSet<Long> loadingReactionTags;
    private boolean loadingRemoteFilters;
    private ArrayList<Utilities.Callback<Boolean>> loadingStakeDiceInfo;
    private boolean loadingSuggestedFilters;
    private boolean loadingUnreadDialogs;
    private boolean loggedDeviceStats;
    private final SharedPreferences mainPreferences;
    public String mapKey;
    public int mapProvider;
    public int maxBroadcastCount;
    public int maxCaptionLength;
    public int maxEditTime;
    public int maxFaveStickersCount;
    public int maxFolderPinnedDialogsCountDefault;
    public int maxFolderPinnedDialogsCountPremium;
    public int maxGroupCount;
    public int maxMegagroupCount;
    public int maxMessageLength;
    public int maxPinnedDialogsCountDefault;
    public int maxPinnedDialogsCountPremium;
    public int maxRecentGifsCount;
    public int maxRecentStickersCount;
    private SparseIntArray migratedChats;
    private boolean migratingDialogs;
    public int minGroupConvertSize;
    private final ConcurrentHashMap<Long, Long> monoForumLinkedChannels;
    private LongSparseArray<ArrayList<Integer>> needShortPollChannels;
    private LongSparseArray<ArrayList<Integer>> needShortPollOnlines;
    public NewMessageCallback newMessageCallback;
    public boolean newNoncontactPeersRequirePremiumWithoutOwnpremium;
    private SparseIntArray nextDialogsCacheOffset;
    private int nextPromoInfoCheckTime;
    private int nextTosCheckTime;
    private final SharedPreferences notificationsPreferences;
    private final Runnable notifyTranscriptionAudioCooldownUpdate;
    private final ConcurrentHashMap<String, TLObject> objectsByUsernames;
    private boolean offlineSent;
    private Utilities.Callback<Boolean> onLoadedRemoteFilters;
    public ConcurrentHashMap<Long, Integer> onlinePrivacy;
    public Long paidReactionsPrivacy;
    public long paidReactionsPrivacyTime;
    private Runnable passwordCheckRunnable;
    public PeerColors peerColors;
    private final long peerDialogRequestTimeout;
    private final LongSparseArray<Long> peerDialogsRequested;
    private final HashSet<Pair<StarsController.MessageId, AtomicBoolean>> pendingReportMessageDelivery;
    public Set<String> pendingSuggestions;
    private LongSparseIntArray pendingUnreadCounter;
    public SparseArray<ImageUpdater> photoSuggestion;
    public int pmReadDateExpirePeriod;
    private LongSparseArray<SparseArray<MessageObject>> pollsToCheck;
    private int pollsToCheckSize;
    public boolean preloadFeaturedStickers;
    public String premiumBotUsername;
    public SparseIntArray premiumFeaturesTypesToPosition;
    public String premiumInvoiceSlug;
    public boolean premiumLocked;
    public String premiumManageSubscriptionUrl;
    public LongSparseArray<LongSparseArray<CharSequence>> printingStrings;
    public LongSparseArray<LongSparseArray<Integer>> printingStringsTypes;
    public ConcurrentHashMap<Long, ConcurrentHashMap<Integer, ArrayList<PrintingUser>>> printingUsers;
    public PeerColors profilePeerColors;
    private TLRPC.Dialog promoDialog;
    private long promoDialogId;
    public int promoDialogType;
    public String promoPsaMessage;
    public String promoPsaType;
    private String proxyDialogAddress;
    public int publicLinksLimitDefault;
    public int publicLinksLimitPremium;
    public boolean qrLoginCamera;
    public int quickRepliesLimit;
    public int quickReplyMessagesLimit;
    public int quoteLengthMax;
    public int ratingDecay;
    private LongSparseArray<TLRPC.TL_messages_savedReactionsTags> reactionTags;
    public int reactionsInChatMax;
    public int reactionsUniqMax;
    public int reactionsUserMaxDefault;
    public int reactionsUserMaxPremium;
    private ArrayList<ReadTask> readTasks;
    private LongSparseArray<ReadTask> readTasksMap;
    private Runnable recentEmojiStatusUpdateRunnable;
    private long recentEmojiStatusUpdateRunnableTime;
    private long recentEmojiStatusUpdateRunnableTimeout;
    public int recommendedChannelsLimitDefault;
    public int recommendedChannelsLimitPremium;
    public boolean registeringForPush;
    private LongSparseArray<ArrayList<Integer>> reloadingMessages;
    private HashMap<String, ArrayList<MessageObject>> reloadingSavedWebpages;
    private LongSparseArray<ArrayList<MessageObject>> reloadingSavedWebpagesPending;
    private HashMap<String, ArrayList<MessageObject>> reloadingScheduledWebpages;
    private LongSparseArray<ArrayList<MessageObject>> reloadingScheduledWebpagesPending;
    private HashMap<String, ArrayList<MessageObject>> reloadingWebpages;
    private LongSparseArray<ArrayList<MessageObject>> reloadingWebpagesPending;
    public boolean remoteConfigLoaded;
    private ArrayList<ReadTask> repliesReadTasks;
    private final HashSet<StarsController.MessageId> reportedMessageDelivery;
    private final Runnable requestIsUserContactBlockedRunnable;
    private boolean requestingContactToken;
    private TLRPC.messages_Dialogs resetDialogsAll;
    private TLRPC.TL_messages_peerDialogs resetDialogsPinned;
    private boolean resetingDialogs;
    public int revokeTimeLimit;
    public int revokeTimePmLimit;
    public int ringtoneDurationMax;
    public int ringtoneSizeMax;
    public int roundAudioBitrate;
    public int roundVideoBitrate;
    public int roundVideoSize;
    public boolean saveGifsWithStickers;
    public int savedDialogsPinnedLimitDefault;
    public int savedDialogsPinnedLimitPremium;
    public int savedGifsLimitDefault;
    public int savedGifsLimitPremium;
    public SavedMessagesController savedMessagesController;
    private SavedMusicIds savedMusicIds;
    public boolean savedViewAsChats;
    public int secretWebpagePreview;
    public DialogFilter[] selectedDialogFilter;
    private LongSparseArray<SendAsPeersInfo> sendAsPeers;
    private LongSparseArray<SendAsPeersInfo> sendAsPeersLiveStories;
    private final Runnable sendReportMessageDeliver;
    private final HashMap<String, Boolean> sendingSuggestedMessageApprovalMap;
    public LongSparseArray<LongSparseArray<Boolean>>[] sendingTypings;
    private final HashSet<Long> sensitiveAgreed;
    private SparseBooleanArray serverDialogsEndReached;
    private LongSparseIntArray shortPollChannels;
    private LongSparseIntArray shortPollOnlines;
    public boolean showAnnualPerMonth;
    public boolean showFiltersTooltip;
    public int smallQueueMaxActiveOperations;
    public boolean smsjobsStickyNotificationEnabled;
    private DialogFilter sortingDialogFilter;
    public boolean sponsoredLinksInappAllow;
    private LongSparseArray<SponsoredMessagesInfo> sponsoredMessages;
    public TLRPC.EmojiGameInfo stakeDiceInfo;
    public boolean stargiftsBlocked;
    public int stargiftsConvertPeriodMax;
    public int[][] stargiftsCraftAttributesPermilles;
    public int stargiftsMessageLengthMax;
    public int stargiftsPinnedToTopLimit;
    public boolean starrefConnectAllowed;
    public int starrefMaxCommissionPermille;
    public int starrefMinCommissionPermille;
    public boolean starrefProgramAllowed;
    public Set<String> starrefStartParamPrefixes;
    public boolean starsGiftsEnabled;
    public int starsGroupcallMessageAmountMax;
    public int[] starsGroupcallMessageLimits;
    public boolean starsLocked;
    public long starsPaidMessageAmountMax;
    public int starsPaidMessageCommissionPermille;
    public boolean starsPaidMessagesAvailable;
    public long starsPaidPostAmountMax;
    public long starsPaidReactionAmountMax;
    public long starsRevenueWithdrawalMin;
    public long starsSubscriptionAmountMax;
    public float starsUsdSellRate1000;
    public float starsUsdWithdrawRate1000;
    private int statusRequest;
    private int statusSettingState;
    public int stealthModeCooldown;
    public int stealthModeFuture;
    public int stealthModePast;
    public int stickersFavedLimitDefault;
    public int stickersFavedLimitPremium;
    public long storiesChangelogUserId;
    public StoriesController storiesController;
    public String storiesEntities;
    public boolean storiesExportNopublicLink;
    public int storiesPinnedToTopCountMax;
    public String storiesPosting;
    public int storiesSentMonthlyLimitDefault;
    public int storiesSentMonthlyLimitPremium;
    public int storiesSentWeeklyLimitDefault;
    public int storiesSentWeeklyLimitPremium;
    public int storiesSuggestedReactionsLimitDefault;
    public int storiesSuggestedReactionsLimitPremium;
    public int storyCaptionLengthLimitDefault;
    public int storyCaptionLengthLimitPremium;
    public int storyExpiringLimitDefault;
    public int storyExpiringLimitPremium;
    public boolean storyQualityFull;
    public String storyVenueSearchBot;
    public boolean storyWeatherPreload;
    public boolean suggestContacts;
    public boolean suggestStickersApiOnly;
    public ArrayList<TLRPC.TL_dialogFilterSuggested> suggestedFilters;
    public String suggestedLangCode;
    public int telegramAntispamGroupSizeMin;
    public long telegramAntispamUserId;
    private Runnable themeCheckRunnable;
    private HashMap<String, ReadTask> threadsReadTasksMap;
    public int todoItemLengthMax;
    public int todoItemsMax;
    public int todoTitleLengthMax;
    public String tonBlockchainExplorerUrl;
    public String tonProxyAddress;
    public long tonStakeddiceStakeAmountMax;
    public long tonStakeddiceStakeAmountMin;
    public long[] tonStakediceStakeSuggestedAmounts;
    private AiTonesController tonesController;
    private TopicsController topicsController;
    public int topicsPinnedLimit;
    public int totalBlockedCount;
    public int transcribeAudioTrialCooldownUntil;
    public int transcribeAudioTrialCurrentNumber;
    public int transcribeAudioTrialDurationMax;
    public int transcribeAudioTrialWeeklyNumber;
    public int transcribeButtonPressed;
    private TranslateController translateController;
    public String translationsAutoEnabled;
    public String translationsManualEnabled;
    public UnconfirmedAuthController unconfirmedAuthController;
    public int unreadUnmutedDialogs;
    public int updateCheckDelay;
    private Comparator<TLRPC.Update> updatesComparator;
    private final LongSparseArray<ArrayList<TLRPC.Updates>> updatesQueueChannels;
    private ArrayList<TLRPC.Updates> updatesQueuePts;
    private ArrayList<TLRPC.Updates> updatesQueueQts;
    private ArrayList<TLRPC.Updates> updatesQueueSeq;
    private LongSparseLongArray updatesStartWaitTimeChannels;
    private long updatesStartWaitTimePts;
    private long updatesStartWaitTimeQts;
    private long updatesStartWaitTimeSeq;
    public boolean updatingState;
    public boolean uploadMarkupVideo;
    public int uploadMaxFileParts;
    public int uploadMaxFilePartsPremium;
    public float uploadPremiumSpeedupDownload;
    public int uploadPremiumSpeedupNotifyPeriod;
    public float uploadPremiumSpeedupUpload;
    private String uploadingAvatar;
    private HashMap<String, Object> uploadingThemes;
    public String uploadingWallpaper;
    public Theme.OverrideWallpaperInfo uploadingWallpaperInfo;
    private UserNameResolver userNameResolver;
    private final LongSparseArray<TLRPC.PeerSettings> userPeerSettings;
    private final ConcurrentHashMap<Long, TLRPC.User> users;
    public String venueSearchBot;
    public String verifyAgeBotUsername;
    public String verifyAgeCountry;
    public int verifyAgeMin;
    public boolean videoIgnoreAltDocuments;
    private ArrayList<Long> visibleDialogMainThreadIds;
    private ArrayList<Long> visibleScheduledDialogMainThreadIds;
    public VoIPDebugToSend voipDebug;
    public String weatherSearchUsername;
    public Set<String> webAppAllowedProtocols;
    private TL_account.TL_webBrowserSettings webBrowserSettings;
    private final CacheFetcher<Void, TL_account.TL_webBrowserSettings> webBrowserSettingsFetcher;
    public int webFileDatacenterId;
    public HashSet<Long> whitelistedBots;
    public String youtubePipType;
    public static int UPDATE_MASK_ALL = (2 | 4) | 1050105;
    public static int DIALOG_FILTER_FLAG_ALL_CHATS = (2 | 1) | 28;
    private static volatile MessagesController[] Instance = new MessagesController[16];
    private static final Object[] lockObjects = new Object[16];

    public interface ErrorDelegate {
        boolean run(TLRPC.TL_error tL_error);
    }

    public interface IsInChatCheckedCallback {
        void run(boolean z, TLRPC.TL_chatAdminRights tL_chatAdminRights, String str);
    }

    public interface MessagesLoadedCallback {
        void onError();

        void onMessagesLoaded(boolean z);
    }

    public interface NewMessageCallback {
        boolean onMessageReceived(TLRPC.Message message);
    }

    public static class PrintingUser {
        public TLRPC.SendMessageAction action;
        public long lastTime;
        public long userId;
    }

    public static class UnreadCounts {
        public boolean hasUnmutedUnreadDialogs;
        public int mentionCount;
        public int pollVotesMentionCount;
        public int reactionMentionCount;
        public int unreadCount;
    }

    public static void $r8$lambda$EgZ4yLxyqSRcWOusGv7r1UfA3yE(Utilities.Callback4 callback4, TLObject tLObject, TLRPC.TL_error tL_error) {
            String str;
            int i;
            if (tLObject instanceof TLRPC.TL_help_appConfigNotModified) {
                Boolean bool = Boolean.TRUE;
                callback4.run(bool, null, 0L, bool);
                return;
            }
            if (!(tLObject instanceof TLRPC.TL_help_appConfig)) {
                if (tL_error != null) {
                    str = tL_error.code + " " + tL_error.text;
                } else {
                    str = _UrlKt.FRAGMENT_ENCODE_SET;
                }
                FileLog.e("getting appconfig error ".concat(str));
                callback4.run(Boolean.FALSE, null, 0L, Boolean.valueOf(tL_error == null || !((i = tL_error.code) == -2000 || i == -2001)));
                return;
            }
            TLRPC.TL_help_appConfig tL_help_appConfig = (TLRPC.TL_help_appConfig) tLObject;
            callback4.run(Boolean.FALSE, tL_help_appConfig, Long.valueOf(tL_help_appConfig.hash), Boolean.TRUE);
        }

        @Override 
        public void getLocal(final int i, Integer num, final Utilities.Callback2<Long, TLRPC.TL_help_appConfig> callback2) {
            MessagesController.this.getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() throws Throwable {
                    MessagesController.AnonymousClass1.$r8$lambda$q4QNTPDmaRs1G40x3Kcs5sNxkv8(i, callback2);
                }
            });
        }

        void $r8$lambda$3C9uL85dfSsEYcOSo_LyIWzvBC4(int i, TLRPC.TL_help_appConfig tL_help_appConfig) {
            try {
                SQLiteDatabase database = MessagesStorage.getInstance(i).getDatabase();
                if (database != null) {
                    database.executeFast("DELETE FROM app_config").stepThis().dispose();
                    if (tL_help_appConfig != null) {
                        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = database.executeFast("INSERT INTO app_config VALUES(?)");
                        sQLitePreparedStatementExecuteFast.requery();
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_help_appConfig.getObjectSize());
                        tL_help_appConfig.serializeToStream(nativeByteBuffer);
                        sQLitePreparedStatementExecuteFast.bindByteBuffer(1, nativeByteBuffer);
                        sQLitePreparedStatementExecuteFast.step();
                        nativeByteBuffer.reuse();
                        sQLitePreparedStatementExecuteFast.dispose();
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public int getMaxMessageLength() {
        boolean zIsPremium = getUserConfig().isPremium();
        AppGlobalConfig appGlobalConfig = this.config;
        return (zIsPremium ? appGlobalConfig.messageLengthLimitPremium : appGlobalConfig.messageLengthLimitDefault).get();
    }

    public boolean starsPurchaseAvailable() {
        return !this.starsLocked;
    }

    public boolean premiumFeaturesBlocked() {
        return this.premiumLocked && !getUserConfig().isPremium();
    }

    public boolean premiumPurchaseBlocked() {
        return this.premiumLocked;
    }

    public boolean isTranslationsManualEnabled() {
        return !"disabled".equals(this.translationsManualEnabled);
    }

    public boolean isTranslationsAutoEnabled() {
        return !"disabled".equals(this.translationsAutoEnabled);
    }

    public void getNextReactionMention(long j, long j2, int i, Consumer<Integer> consumer) {
        getNextReactionMentionInternal(j, j2, i, true, consumer);
    }

    public void getNextPollVotesMention(long j, long j2, int i, Consumer<Integer> consumer) {
        getNextReactionMentionInternal(j, j2, i, false, consumer);
    }

    private void getNextReactionMentionInternal(final long j, final long j2, final int i, final boolean z, final Consumer<Integer> consumer) {
        final String str = z ? "reaction_mentions" : "poll_votes_mentions";
        final String str2 = z ? "reaction_mentions_topics" : "poll_votes_mentions_topics";
        final boolean z2 = !z;
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getNextReactionMentionInternal$4(j2, str2, j, str, z, z2, consumer, i);
            }
        });
    }

    public void m4690$r8$lambda$2b4BKB5veZSwxIwVOWodBEFhdo(final Consumer consumer, TLRPC.messages_Messages messages_messages, TLRPC.TL_error tL_error) {
        ArrayList<TLRPC.Message> arrayList;
        final int i = 0;
        if (tL_error == null && messages_messages != null && (arrayList = messages_messages.messages) != null && !arrayList.isEmpty()) {
            i = messages_messages.messages.get(0).id;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                consumer.accept(Integer.valueOf(i));
            }
        });
    }

    public void updatePremium(boolean z) {
        if (this.dialogFilters.isEmpty()) {
            return;
        }
        if (!z) {
            if (!this.dialogFilters.get(0).isDefault()) {
                for (int i = 1; i < this.dialogFilters.size(); i++) {
                    if (this.dialogFilters.get(i).isDefault()) {
                        this.dialogFilters.add(0, this.dialogFilters.remove(i));
                        break;
                    }
                }
            }
            lockFiltersInternal();
        } else {
            for (int i2 = 0; i2 < this.dialogFilters.size(); i2++) {
                this.dialogFilters.get(i2).locked = false;
            }
        }
        getMessagesStorage().saveDialogFiltersOrder();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        getStoriesController().onPremiumChanged();
    }

    public void lockFiltersInternal() {
        boolean z;
        if (getUserConfig().isPremium() || this.dialogFilters.size() - 1 <= this.dialogFiltersLimitDefault) {
            z = false;
        } else {
            int size = (this.dialogFilters.size() - 1) - this.dialogFiltersLimitDefault;
            ArrayList arrayList = new ArrayList(this.dialogFilters);
            Collections.reverse(arrayList);
            z = false;
            for (int i = 0; i < arrayList.size(); i++) {
                if (i < size) {
                    if (!((DialogFilter) arrayList.get(i)).locked) {
                        z = true;
                    }
                    ((DialogFilter) arrayList.get(i)).locked = true;
                } else {
                    if (((DialogFilter) arrayList.get(i)).locked) {
                        z = true;
                    }
                    ((DialogFilter) arrayList.get(i)).locked = false;
                }
            }
        }
        if (z) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        }
    }

    public int getCaptionMaxLengthLimit() {
        return getUserConfig().isPremium() ? this.captionLengthLimitPremium : this.captionLengthLimitDefault;
    }

    public int getAboutLimit() {
        return getUserConfig().isPremium() ? this.aboutLengthLimitPremium : this.aboutLengthLimitDefault;
    }

    public int getMaxUserReactionsCount() {
        return getUserConfig().isPremium() ? this.reactionsUserMaxPremium : this.reactionsUserMaxDefault;
    }

    public int getChatReactionsCount() {
        if (getUserConfig().isPremium()) {
            return this.reactionsInChatMax;
        }
        return 1;
    }

    public int getChatMaxUniqReactions(long j) {
        TLRPC.ChatFull chatFull = getInstance(this.currentAccount).getChatFull(-j);
        if (chatFull != null && (!(chatFull instanceof TLRPC.TL_chatFull) ? (chatFull.flags2 & 8192) != 0 : (chatFull.flags & 1048576) != 0)) {
            return chatFull.reactions_limit;
        }
        return this.reactionsUniqMax;
    }

    public boolean isPremiumUser(TLRPC.User user) {
        return (user == null || !user.premium || isSupportUser(user)) ? false : true;
    }

    public void pressTranscribeButton() {
        int i = this.transcribeButtonPressed;
        if (i < 2) {
            this.transcribeButtonPressed = i + 1;
            SharedPreferences sharedPreferences = this.mainPreferences;
            if (sharedPreferences != null) {
                sharedPreferences.edit().putInt("transcribeButtonPressed", this.transcribeButtonPressed).apply();
            }
        }
    }

    public void putLastGiftAuctionUpdate() {
        SharedPreferences sharedPreferences = this.mainPreferences;
        if (sharedPreferences != null) {
            sharedPreferences.edit().putLong("lastGiftAuctionTimeUpdate", System.currentTimeMillis()).apply();
        }
    }

    public boolean giftAuctionUpdateWasRecently() {
        SharedPreferences sharedPreferences = this.mainPreferences;
        return System.currentTimeMillis() - (sharedPreferences != null ? sharedPreferences.getLong("lastGiftAuctionTimeUpdate", 0L) : 0L) < DurationKt.MILLIS_IN_DAY;
    }

    public ArrayList<TLRPC.TL_messages_stickerSet> filterPremiumStickers(ArrayList<TLRPC.TL_messages_stickerSet> arrayList) {
        if (!premiumFeaturesBlocked()) {
            return arrayList;
        }
        int i = 0;
        while (i < arrayList.size()) {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSetFilterPremiumStickers = getInstance(this.currentAccount).filterPremiumStickers(arrayList.get(i));
            if (tL_messages_stickerSetFilterPremiumStickers == null) {
                arrayList.remove(i);
                i--;
            } else {
                arrayList.set(i, tL_messages_stickerSetFilterPremiumStickers);
            }
            i++;
        }
        return arrayList;
    }

    public TLRPC.TL_messages_stickerSet filterPremiumStickers(TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
        if (premiumFeaturesBlocked() && tL_messages_stickerSet != null) {
            int i = 0;
            for (int i2 = 0; i2 < tL_messages_stickerSet.documents.size(); i2++) {
                try {
                    if (MessageObject.isPremiumSticker(tL_messages_stickerSet.documents.get(i2))) {
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(tL_messages_stickerSet.getObjectSize());
                        tL_messages_stickerSet.serializeToStream(nativeByteBuffer);
                        nativeByteBuffer.position(0);
                        TLRPC.TL_messages_stickerSet tL_messages_stickerSet2 = new TLRPC.TL_messages_stickerSet();
                        nativeByteBuffer.readInt32(true);
                        tL_messages_stickerSet2.readParams(nativeByteBuffer, true);
                        nativeByteBuffer.reuse();
                        while (i < tL_messages_stickerSet2.documents.size()) {
                            try {
                                if (MessageObject.isPremiumSticker(tL_messages_stickerSet2.documents.get(i))) {
                                    tL_messages_stickerSet2.documents.remove(i);
                                    tL_messages_stickerSet2.packs.remove(i);
                                    i--;
                                    if (tL_messages_stickerSet2.documents.isEmpty()) {
                                        return null;
                                    }
                                }
                                i++;
                            } catch (Exception e) {
                                e = e;
                                tL_messages_stickerSet = tL_messages_stickerSet2;
                            }
                        }
                        return tL_messages_stickerSet2;
                    }
                } catch (Exception e2) {
                    e = e2;
                }
                e.printStackTrace();
            }
            return tL_messages_stickerSet;
        }
        return tL_messages_stickerSet;
    }

    public TopicsController getTopicsController() {
        return this.topicsController;
    }

    public TranslateController getTranslateController() {
        return this.translateController;
    }

    public AiTonesController getTonesController() {
        if (this.tonesController == null) {
            this.tonesController = new AiTonesController(this.currentAccount);
        }
        return this.tonesController;
    }

    public boolean isCommunity(long j) {
        if (j < 0) {
            return ChatObject.isCommunity(getChat(Long.valueOf(-j)));
        }
        return false;
    }

    public boolean isForum(long j) {
        if (j < 0) {
            TLRPC.Chat chat = getChat(Long.valueOf(-j));
            return chat != null && chat.forum;
        }
        return UserObject.isBotForum(getUser(Long.valueOf(j)));
    }

    public boolean isMonoForum(long j) {
        TLRPC.Chat chat = getChat(Long.valueOf(-j));
        return chat != null && chat.monoforum;
    }

    public boolean isMonoForumWithManageRights(long j) {
        TLRPC.Chat chat = getChat(Long.valueOf(-j));
        return ChatObject.isMonoForum(chat) && ChatObject.canManageMonoForum(this.currentAccount, chat);
    }

    public boolean isForum(MessageObject messageObject) {
        return messageObject != null && isForum(messageObject.getDialogId());
    }

    public boolean isForum(TLRPC.Message message) {
        return message != null && isForum(MessageObject.getDialogId(message));
    }

    public void markAllTopicsAsRead(final long j) {
        getMessagesStorage().loadTopics(j, new java.util.function.Consumer() { 
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$markAllTopicsAsRead$8(j, (ArrayList) obj);
            }
        });
    }

    public void lambda$markAllTopicsAsRead$7(ArrayList arrayList, long j) {
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC.TL_forumTopic tL_forumTopic = (TLRPC.TL_forumTopic) arrayList.get(i);
                MessagesController messagesController = getMessagesController();
                int i2 = tL_forumTopic.top_message;
                TLRPC.Message message = tL_forumTopic.topMessage;
                messagesController.markDialogAsRead(j, i2, 0, message != null ? message.date : 0, false, isMonoForum(j) ? DialogObject.getPeerDialogId(tL_forumTopic.from_id) : tL_forumTopic.id, 0, true, 0);
                getMessagesStorage().updateRepliesMaxReadId(-j, isMonoForum(j) ? DialogObject.getPeerDialogId(tL_forumTopic.from_id) : tL_forumTopic.id, tL_forumTopic.top_message, 0, true);
            }
        }
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$markAllTopicsAsRead$6();
            }
        });
    }

    public void lambda$markAllTopicsAsRead$5() {
        getMessagesController().sortDialogs(null);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    public String getFullName(long j) {
        if (j > 0) {
            TLRPC.User user = getUser(Long.valueOf(j));
            if (user != null) {
                return ContactsController.formatName(user.first_name, user.last_name);
            }
            return null;
        }
        TLRPC.Chat chat = getChat(Long.valueOf(-j));
        if (chat != null) {
            return chat.title;
        }
        return null;
    }

    public UserNameResolver getUserNameResolver() {
        if (this.userNameResolver == null) {
            this.userNameResolver = new UserNameResolver(this.currentAccount);
        }
        return this.userNameResolver;
    }

    public class SponsoredMessagesInfo {
        public long loadTime;
        public boolean loading;
        public ArrayList<MessageObject> messages;
        public Integer posts_between;

        public SponsoredMessagesInfo() {
        }
    }

    public class SendAsPeersInfo {
        private long loadTime;
        private boolean loading;
        private TLRPC.TL_channels_sendAsPeers sendAsPeers;

        private SendAsPeersInfo() {
        }
    }

    public static class FaqSearchResult {
        public int num;
        public String[] path;
        public String title;
        public String url;

        public FaqSearchResult(String str, String[] strArr, String str2) {
            this.title = str;
            this.path = strArr;
            this.url = str2;
        }

        public boolean equals(Object obj) {
            if (obj instanceof FaqSearchResult) {
                return this.title.equals(((FaqSearchResult) obj).title);
            }
            return false;
        }

        public String toString() {
            SerializedData serializedData = new SerializedData();
            serializedData.writeInt32(this.num);
            int i = 0;
            serializedData.writeInt32(0);
            serializedData.writeString(this.title);
            String[] strArr = this.path;
            serializedData.writeInt32(strArr != null ? strArr.length : 0);
            if (this.path != null) {
                while (true) {
                    String[] strArr2 = this.path;
                    if (i >= strArr2.length) {
                        break;
                    }
                    serializedData.writeString(strArr2[i]);
                    i++;
                }
            }
            serializedData.writeString(this.url);
            return Utilities.bytesToHex(serializedData.toByteArray());
        }
    }

    public static class EmojiSound {
        public long accessHash;
        public byte[] fileReference;
        public long id;

        public EmojiSound(long j, long j2, String str) {
            this.id = j;
            this.accessHash = j2;
            this.fileReference = Base64.decode(str, 8);
        }

        public EmojiSound(long j, long j2, byte[] bArr) {
            this.id = j;
            this.accessHash = j2;
            this.fileReference = bArr;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof EmojiSound)) {
                return false;
            }
            EmojiSound emojiSound = (EmojiSound) obj;
            return this.id == emojiSound.id && this.accessHash == emojiSound.accessHash && Arrays.equals(this.fileReference, emojiSound.fileReference);
        }
    }

    public void clearQueryTime() {
        this.lastServerQueryTime.clear();
        this.lastScheduledServerQueryTime.clear();
        this.lastQuickReplyServerQueryTime.clear();
        this.lastSavedServerQueryTime.clear();
    }

    public static class DiceFrameSuccess {
        public int frame;
        public int num;

        public DiceFrameSuccess(int i, int i2) {
            this.frame = i;
            this.num = i2;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof DiceFrameSuccess)) {
                return false;
            }
            DiceFrameSuccess diceFrameSuccess = (DiceFrameSuccess) obj;
            return this.frame == diceFrameSuccess.frame && this.num == diceFrameSuccess.num;
        }
    }

    public static class UserActionUpdatesSeq extends TLRPC.Updates {
        private UserActionUpdatesSeq() {
        }
    }

    public static class UserActionUpdatesPts extends TLRPC.Updates {
        private UserActionUpdatesPts() {
        }
    }

    static {
        for (int i = 0; i < 16; i++) {
            lockObjects[i] = new Object();
        }
    }

    public static class ReadTask {
        public long dialogId;
        public int maxDate;
        public int maxId;
        public long monoForumPeerId;
        public long replyId;
        public long sendRequestTime;

        private ReadTask() {
        }
    }

    public static class DialogFilter {
        private static int dialogFilterPointer = 10;
        public int color;
        public String emoticon;
        public int flags;
        public int id;
        public int localId;
        public boolean locked;
        public String name;
        public int order;
        public volatile int pendingUnreadCount;
        public boolean title_noanimate;
        public int unreadCount;
        public ArrayList<TLRPC.MessageEntity> entities = new ArrayList<>();
        public ArrayList<Long> alwaysShow = new ArrayList<>();
        public ArrayList<Long> neverShow = new ArrayList<>();
        public LongSparseIntArray pinnedDialogs = new LongSparseIntArray();
        public ArrayList<TLRPC.Dialog> dialogs = new ArrayList<>();
        public ArrayList<TLRPC.Dialog> dialogsForward = new ArrayList<>();
        public ArrayList<TL_chatlists.TL_exportedChatlistInvite> invites = null;

        public DialogFilter() {
            int i = dialogFilterPointer;
            dialogFilterPointer = i + 1;
            this.localId = i;
        }

        public boolean includesDialog(AccountInstance accountInstance, long j) {
            TLRPC.Dialog dialog = accountInstance.getMessagesController().dialogs_dict.get(j);
            if (dialog == null) {
                return false;
            }
            return includesDialog(accountInstance, j, dialog);
        }

        public boolean includesDialog(AccountInstance accountInstance, long j, TLRPC.Dialog dialog) {
            TLRPC.Chat chat;
            if (this.neverShow.contains(Long.valueOf(j))) {
                return false;
            }
            if (this.alwaysShow.contains(Long.valueOf(j))) {
                return true;
            }
            if (dialog.folder_id != 0 && (this.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_ARCHIVED) != 0) {
                return false;
            }
            MessagesController messagesController = accountInstance.getMessagesController();
            ContactsController contactsController = accountInstance.getContactsController();
            if (((this.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0 && messagesController.isDialogMuted(dialog.id, 0L) && dialog.unread_mentions_count == 0) || ((this.flags & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0 && messagesController.getDialogUnreadCount(dialog) == 0 && !dialog.unread_mark && dialog.unread_mentions_count == 0)) {
                return false;
            }
            if (j > 0) {
                TLRPC.User user = messagesController.getUser(Long.valueOf(j));
                if (user != null) {
                    if (!user.bot) {
                        if (user.self || user.contact || contactsController.isContact(j)) {
                            if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_CONTACTS) != 0) {
                                return true;
                            }
                        } else if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS) != 0) {
                            return true;
                        }
                    } else if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_BOTS) != 0) {
                        return true;
                    }
                }
            } else if (j < 0 && (chat = messagesController.getChat(Long.valueOf(-j))) != null) {
                if (ChatObject.isCommunity(chat)) {
                    int i = this.flags;
                    int i2 = MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS;
                    return (i & i2) == i2;
                }
                if (ChatObject.isChatCollapsedInCommunity(accountInstance.getCurrentAccount(), chat)) {
                    return false;
                }
                if (ChatObject.isChannel(chat) && !chat.megagroup) {
                    if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_CHANNELS) != 0) {
                        return true;
                    }
                } else if ((this.flags & MessagesController.DIALOG_FILTER_FLAG_GROUPS) != 0) {
                    return true;
                }
            }
            return false;
        }

        public boolean alwaysShow(int i, TLRPC.Dialog dialog) {
            TLRPC.EncryptedChat encryptedChat;
            if (dialog == null) {
                return false;
            }
            long j = dialog.id;
            if (DialogObject.isEncryptedDialog(j) && (encryptedChat = MessagesController.getInstance(i).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j)))) != null) {
                j = encryptedChat.user_id;
            }
            return this.alwaysShow.contains(Long.valueOf(j));
        }

        public boolean isDefault() {
            return this.id == 0;
        }

        public boolean isChatlist() {
            return (this.flags & MessagesController.DIALOG_FILTER_FLAG_CHATLIST) > 0;
        }

        public boolean isMyChatlist() {
            return isChatlist() && (this.flags & MessagesController.DIALOG_FILTER_FLAG_CHATLIST_ADMIN) > 0;
        }
    }

    public void lambda$new$13() {
        MessagesController messagesController = getMessagesController();
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileUploaded);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileUploadFailed);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileUploadProgressChanged);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileLoaded);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.fileLoadFailed);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.messageReceivedByServer);
        getNotificationCenter().addObserver(messagesController, NotificationCenter.updateMessageMedia);
    }

    public static void lambda$sendLoadPeersRequest$19(ArrayList arrayList, ArrayList arrayList2, TLRPC.messages_Dialogs messages_dialogs, TLRPC.messages_Dialogs messages_dialogs2, ArrayList arrayList3, TLObject tLObject, ArrayList arrayList4, SparseArray sparseArray, ArrayList arrayList5, HashMap map, HashSet hashSet, Runnable runnable, TLObject tLObject2, TLRPC.TL_error tL_error) {
        if (tLObject2 instanceof TLRPC.TL_messages_chats) {
            arrayList.addAll(((TLRPC.TL_messages_chats) tLObject2).chats);
        } else if (tLObject2 instanceof Vector) {
            Vector vector = (Vector) tLObject2;
            int size = vector.objects.size();
            for (int i = 0; i < size; i++) {
                arrayList2.add((TLRPC.User) vector.objects.get(i));
            }
        } else if (tLObject2 instanceof TLRPC.TL_messages_peerDialogs) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject2;
            messages_dialogs.dialogs.addAll(tL_messages_peerDialogs.dialogs);
            messages_dialogs.messages.addAll(tL_messages_peerDialogs.messages);
            messages_dialogs2.dialogs.addAll(tL_messages_peerDialogs.dialogs);
            messages_dialogs2.messages.addAll(tL_messages_peerDialogs.messages);
            arrayList2.addAll(tL_messages_peerDialogs.users);
            arrayList.addAll(tL_messages_peerDialogs.chats);
        }
        arrayList3.remove(tLObject);
        if (arrayList3.isEmpty()) {
            getMessagesStorage().processLoadedFilterPeers(messages_dialogs, messages_dialogs2, arrayList2, arrayList, arrayList4, sparseArray, arrayList5, map, hashSet, runnable);
        }
    }

    public void loadFilterPeers(final HashMap<Long, TLRPC.InputPeer> map, final HashMap<Long, TLRPC.InputPeer> map2, final HashMap<Long, TLRPC.InputPeer> map3, final TLRPC.messages_Dialogs messages_dialogs, final TLRPC.messages_Dialogs messages_dialogs2, final ArrayList<TLRPC.User> arrayList, final ArrayList<TLRPC.Chat> arrayList2, final ArrayList<DialogFilter> arrayList3, final SparseArray<DialogFilter> sparseArray, final ArrayList<Integer> arrayList4, final HashMap<Integer, HashSet<Long>> map4, final HashSet<Integer> hashSet, final Runnable runnable) {
        Utilities.stageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadFilterPeers$20(map2, messages_dialogs, messages_dialogs2, arrayList, arrayList2, arrayList3, sparseArray, arrayList4, map4, hashSet, runnable, map3, map);
            }
        });
    }

    public int m4712$r8$lambda$B_YMwDrP4SKcNFIQ2Bb_FRDz2Y(DialogFilter dialogFilter, DialogFilter dialogFilter2) {
        int i = dialogFilter.order;
        int i2 = dialogFilter2.order;
        if (i > i2) {
            return 1;
        }
        return i < i2 ? -1 : 0;
    }

    public void loadSuggestedFilters() {
        if (this.loadingSuggestedFilters) {
            return;
        }
        this.loadingSuggestedFilters = true;
        getConnectionsManager().sendRequest(new TLRPC.TL_messages_getSuggestedDialogFilters(), new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$loadSuggestedFilters$25(tLObject, tL_error);
            }
        });
    }

    public void lambda$loadSuggestedFilters$24(TLObject tLObject) {
        this.loadingSuggestedFilters = false;
        this.suggestedFilters.clear();
        if (tLObject instanceof Vector) {
            this.suggestedFilters.addAll(((Vector) tLObject).objects);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.suggestedFiltersLoaded, new Object[0]);
    }

    public void loadRemoteFilters(boolean z) {
        loadRemoteFilters(z, null);
    }

    public void loadRemoteFilters(boolean z, Utilities.Callback<Boolean> callback) {
        if (callback != null) {
            this.onLoadedRemoteFilters = callback;
        }
        if (this.loadingRemoteFilters || !getUserConfig().isClientActivated()) {
            return;
        }
        if (z || !getUserConfig().filtersLoaded) {
            if (z) {
                getUserConfig().filtersLoaded = false;
                getUserConfig().saveConfig(false);
            }
            getConnectionsManager().sendRequest(new TLRPC.TL_messages_getDialogFilters(), new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$loadRemoteFilters$30(tLObject, tL_error);
                }
            });
        }
    }

    public void lambda$loadRemoteFilters$26() {
        Utilities.Callback<Boolean> callback = this.onLoadedRemoteFilters;
        if (callback != null) {
            callback.run(Boolean.TRUE);
            this.onLoadedRemoteFilters = null;
        }
    }

    public void lambda$loadAppConfig$33(final TLRPC.TL_help_appConfig tL_help_appConfig) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadAppConfig$32(tL_help_appConfig);
            }
        });
    }

    public String $r8$lambda$tbjzJQKbfJh6joMCVEsW3w2B9PA(int[] iArr) {
        return (String) Arrays.stream(iArr).mapToObj(new MessagesController$$ExternalSyntheticLambda48()).collect(Collectors.joining(","));
    }

    public void lambda$scheduleTranscriptionUpdate$38() {
        AndroidUtilities.cancelRunOnUIThread(this.notifyTranscriptionAudioCooldownUpdate);
        long currentTime = this.transcribeAudioTrialCooldownUntil - getConnectionsManager().getCurrentTime();
        if (currentTime > 0) {
            AndroidUtilities.runOnUIThread(this.notifyTranscriptionAudioCooldownUpdate, currentTime);
        }
    }

    public public static org.telegram.messenger.MessagesController.PeerColors fromJSON(int r12, org.telegram.tgnet.TLRPC.TL_jsonObject r13, org.telegram.tgnet.TLRPC.TL_jsonObject r14, org.telegram.tgnet.TLRPC.TL_jsonArray r15) {
            void lambda$updateConfig$41(TLRPC.TL_config tL_config) {
        getDownloadController().loadAutoDownloadConfig(false);
        loadAppConfig(true);
        checkPeerColors(true);
        this.remoteConfigLoaded = true;
        this.maxMegagroupCount = tL_config.megagroup_size_max;
        this.maxGroupCount = tL_config.chat_size_max;
        this.maxEditTime = tL_config.edit_time_limit;
        this.ratingDecay = tL_config.rating_e_decay;
        this.maxRecentStickersCount = tL_config.stickers_recent_limit;
        this.revokeTimeLimit = tL_config.revoke_time_limit;
        this.revokeTimePmLimit = tL_config.revoke_pm_time_limit;
        this.canRevokePmInbox = tL_config.revoke_pm_inbox;
        String str = tL_config.me_url_prefix;
        this.linkPrefix = str;
        boolean z = tL_config.force_try_ipv6;
        if (str.endsWith("/")) {
            String str2 = this.linkPrefix;
            this.linkPrefix = str2.substring(0, str2.length() - 1);
        }
        boolean zStartsWith = this.linkPrefix.startsWith("https://");
        String str3 = this.linkPrefix;
        if (zStartsWith) {
            this.linkPrefix = str3.substring(8);
        } else if (str3.startsWith("http://")) {
            this.linkPrefix = this.linkPrefix.substring(7);
        }
        this.callReceiveTimeout = tL_config.call_receive_timeout_ms;
        this.callRingTimeout = tL_config.call_ring_timeout_ms;
        this.callConnectTimeout = tL_config.call_connect_timeout_ms;
        this.callPacketTimeout = tL_config.call_packet_timeout_ms;
        this.maxMessageLength = tL_config.message_length_max;
        this.maxCaptionLength = tL_config.caption_length_max;
        this.preloadFeaturedStickers = tL_config.preload_featured_stickers;
        String str4 = tL_config.venue_search_username;
        if (str4 != null) {
            this.venueSearchBot = str4;
        }
        String str5 = tL_config.gif_search_username;
        if (str5 != null) {
            this.gifSearchBot = str5;
        }
        if (this.imageSearchBot != null) {
            this.imageSearchBot = tL_config.img_search_username;
        }
        this.blockedCountry = tL_config.blocked_mode;
        this.dcDomainName = tL_config.dc_txt_domain_name;
        this.webFileDatacenterId = tL_config.webfile_dc_id;
        String str6 = tL_config.suggested_lang_code;
        if (str6 != null) {
            String str7 = this.suggestedLangCode;
            boolean z2 = str7 == null || !str7.equals(str6);
            this.suggestedLangCode = tL_config.suggested_lang_code;
            if (z2) {
                LocaleController.getInstance().loadRemoteLanguages(this.currentAccount);
            }
        }
        Theme.loadRemoteThemes(this.currentAccount, false);
        Theme.checkCurrentRemoteTheme(false);
        if (tL_config.static_maps_provider == null) {
            tL_config.static_maps_provider = "telegram";
        }
        this.mapKey = null;
        this.mapProvider = 2;
        this.availableMapProviders = 0;
        FileLog.d("map providers = " + tL_config.static_maps_provider);
        String[] strArrSplit = tL_config.static_maps_provider.split(",");
        for (int i = 0; i < strArrSplit.length; i++) {
            String[] strArrSplit2 = strArrSplit[i].split("\\+");
            if (strArrSplit2.length > 0) {
                String[] strArrSplit3 = strArrSplit2[0].split(":");
                if (strArrSplit3.length > 0) {
                    if ("yandex".equals(strArrSplit3[0])) {
                        if (i == 0) {
                            if (strArrSplit2.length > 1) {
                                this.mapProvider = 3;
                            } else {
                                this.mapProvider = 1;
                            }
                        }
                        this.availableMapProviders |= 4;
                    } else if ("google".equals(strArrSplit3[0])) {
                        if (i == 0 && strArrSplit2.length > 1) {
                            this.mapProvider = 4;
                        }
                        this.availableMapProviders |= 1;
                    } else if ("telegram".equals(strArrSplit3[0])) {
                        if (i == 0) {
                            this.mapProvider = 2;
                        }
                        this.availableMapProviders |= 2;
                    }
                    if (strArrSplit3.length > 1) {
                        this.mapKey = strArrSplit3[1];
                    }
                }
            }
        }
        SharedPreferences.Editor editorEdit = this.mainPreferences.edit();
        editorEdit.putBoolean("remoteConfigLoaded", this.remoteConfigLoaded);
        editorEdit.putInt("maxGroupCount", this.maxGroupCount);
        editorEdit.putInt("maxMegagroupCount", this.maxMegagroupCount);
        editorEdit.putInt("maxEditTime", this.maxEditTime);
        editorEdit.putInt("ratingDecay", this.ratingDecay);
        editorEdit.putInt("maxRecentGifsCount", this.maxRecentGifsCount);
        editorEdit.putInt("maxRecentStickersCount", this.maxRecentStickersCount);
        editorEdit.putInt("maxFaveStickersCount", this.maxFaveStickersCount);
        editorEdit.putInt("callReceiveTimeout", this.callReceiveTimeout);
        editorEdit.putInt("callRingTimeout", this.callRingTimeout);
        editorEdit.putInt("callConnectTimeout", this.callConnectTimeout);
        editorEdit.putInt("callPacketTimeout", this.callPacketTimeout);
        editorEdit.putString("linkPrefix", this.linkPrefix);
        editorEdit.putInt("maxFolderPinnedDialogsCountDefault", this.maxFolderPinnedDialogsCountDefault);
        editorEdit.putInt("maxFolderPinnedDialogsCountPremium", this.maxFolderPinnedDialogsCountPremium);
        editorEdit.putInt("maxMessageLength", this.maxMessageLength);
        editorEdit.putInt("maxCaptionLength", this.maxCaptionLength);
        editorEdit.putBoolean("preloadFeaturedStickers", this.preloadFeaturedStickers);
        editorEdit.putInt("revokeTimeLimit", this.revokeTimeLimit);
        editorEdit.putInt("revokeTimePmLimit", this.revokeTimePmLimit);
        editorEdit.putInt("mapProvider", this.mapProvider);
        String str8 = this.mapKey;
        if (str8 != null) {
            editorEdit.putString("pk", str8);
        } else {
            editorEdit.remove("pk");
        }
        editorEdit.putBoolean("canRevokePmInbox", this.canRevokePmInbox);
        editorEdit.putBoolean("blockedCountry", this.blockedCountry);
        editorEdit.putString("venueSearchBot", this.venueSearchBot);
        editorEdit.putString("gifSearchBot", this.gifSearchBot);
        editorEdit.putString("imageSearchBot", this.imageSearchBot);
        editorEdit.putString("dcDomainName2", this.dcDomainName);
        editorEdit.putInt("webFileDatacenterId", this.webFileDatacenterId);
        editorEdit.putString("suggestedLangCode", this.suggestedLangCode);
        editorEdit.putBoolean("forceTryIpV6", z);
        String str9 = tL_config.autologin_token;
        this.autologinToken = str9;
        editorEdit.putString("autologinToken", str9);
        editorEdit.apply();
        getConnectionsManager().setForceTryIpV6(z);
        LocaleController.getInstance().checkUpdateForCurrentRemoteLocale(this.currentAccount, tL_config.lang_pack_version, tL_config.base_lang_pack_version);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.configLoaded, new Object[0]);
    }

    public void addSupportUser() {
        TLRPC.TL_userForeign_old2 tL_userForeign_old2 = new TLRPC.TL_userForeign_old2();
        tL_userForeign_old2.phone = "333";
        tL_userForeign_old2.id = 333000L;
        tL_userForeign_old2.first_name = "Telegram";
        tL_userForeign_old2.last_name = _UrlKt.FRAGMENT_ENCODE_SET;
        tL_userForeign_old2.status = null;
        tL_userForeign_old2.photo = new TLRPC.TL_userProfilePhotoEmpty();
        putUser(tL_userForeign_old2, true);
        TLRPC.TL_userForeign_old2 tL_userForeign_old3 = new TLRPC.TL_userForeign_old2();
        tL_userForeign_old3.phone = "42777";
        tL_userForeign_old3.id = 777000L;
        tL_userForeign_old3.verified = true;
        tL_userForeign_old3.first_name = "Telegram";
        tL_userForeign_old3.last_name = "Notifications";
        tL_userForeign_old3.status = null;
        tL_userForeign_old3.photo = new TLRPC.TL_userProfilePhotoEmpty();
        putUser(tL_userForeign_old3, true);
    }

    public TLRPC.InputUser getInputUser(TLRPC.User user) {
        if (user == null) {
            return new TLRPC.TL_inputUserEmpty();
        }
        if (user.id == getUserConfig().getClientUserId()) {
            return new TLRPC.TL_inputUserSelf();
        }
        if (user.access_hash == 0 && user.fromMessageDialogId != 0 && user.fromMessageId != 0) {
            TLRPC.TL_inputUserFromMessage tL_inputUserFromMessage = new TLRPC.TL_inputUserFromMessage();
            tL_inputUserFromMessage.user_id = user.id;
            tL_inputUserFromMessage.peer = getInputPeer(user.fromMessageDialogId);
            tL_inputUserFromMessage.msg_id = user.fromMessageId;
            return tL_inputUserFromMessage;
        }
        TLRPC.TL_inputUser tL_inputUser = new TLRPC.TL_inputUser();
        tL_inputUser.user_id = user.id;
        tL_inputUser.access_hash = user.access_hash;
        return tL_inputUser;
    }

    public TLRPC.InputUser getInputUser(TLRPC.InputPeer inputPeer) {
        if (inputPeer == null) {
            return new TLRPC.TL_inputUserEmpty();
        }
        if (inputPeer instanceof TLRPC.TL_inputPeerSelf) {
            return new TLRPC.TL_inputUserSelf();
        }
        if (inputPeer.access_hash == 0) {
            TLRPC.User user = getUser(Long.valueOf(inputPeer.user_id));
            if (user.access_hash == 0 && user.fromMessageDialogId != 0 && user.fromMessageId != 0) {
                TLRPC.TL_inputUserFromMessage tL_inputUserFromMessage = new TLRPC.TL_inputUserFromMessage();
                tL_inputUserFromMessage.user_id = inputPeer.user_id;
                tL_inputUserFromMessage.peer = getInputPeer(user.fromMessageDialogId);
                tL_inputUserFromMessage.msg_id = user.fromMessageId;
                return tL_inputUserFromMessage;
            }
        }
        TLRPC.TL_inputUser tL_inputUser = new TLRPC.TL_inputUser();
        tL_inputUser.user_id = inputPeer.user_id;
        tL_inputUser.access_hash = inputPeer.access_hash;
        return tL_inputUser;
    }

    public TLRPC.InputUser getInputUser(long j) {
        return getInputUser(getUser(Long.valueOf(j)));
    }

    public static TLRPC.InputChannel getInputChannel(TLRPC.Chat chat) {
        if (!ChatObject.isChannel(chat)) {
            return new TLRPC.TL_inputChannelEmpty();
        }
        if (chat.access_hash == 0 && chat.fromMessageDialogId != 0 && chat.fromMessageId != 0) {
            TLRPC.TL_inputChannelFromMessage tL_inputChannelFromMessage = new TLRPC.TL_inputChannelFromMessage();
            tL_inputChannelFromMessage.channel_id = chat.id;
            tL_inputChannelFromMessage.peer = getInstance(UserConfig.selectedAccount).getInputPeer(chat.fromMessageDialogId);
            tL_inputChannelFromMessage.msg_id = chat.fromMessageId;
            return tL_inputChannelFromMessage;
        }
        TLRPC.TL_inputChannel tL_inputChannel = new TLRPC.TL_inputChannel();
        tL_inputChannel.channel_id = chat.id;
        tL_inputChannel.access_hash = chat.access_hash;
        return tL_inputChannel;
    }

    public static TLRPC.InputChannel getInputChannel(TLRPC.InputPeer inputPeer) {
        TLRPC.Chat chat;
        if (inputPeer.access_hash == 0 && (chat = getInstance(UserConfig.selectedAccount).getChat(Long.valueOf(inputPeer.channel_id))) != null && chat.access_hash == 0 && chat.fromMessageId != 0 && chat.fromMessageDialogId != 0) {
            TLRPC.TL_inputChannelFromMessage tL_inputChannelFromMessage = new TLRPC.TL_inputChannelFromMessage();
            tL_inputChannelFromMessage.channel_id = inputPeer.channel_id;
            tL_inputChannelFromMessage.peer = getInstance(UserConfig.selectedAccount).getInputPeer(chat.fromMessageDialogId);
            tL_inputChannelFromMessage.msg_id = chat.fromMessageId;
            return tL_inputChannelFromMessage;
        }
        TLRPC.TL_inputChannel tL_inputChannel = new TLRPC.TL_inputChannel();
        tL_inputChannel.channel_id = inputPeer.channel_id;
        tL_inputChannel.access_hash = inputPeer.access_hash;
        return tL_inputChannel;
    }

    public TLRPC.InputChannel getInputChannel(long j) {
        return getInputChannel(getChat(Long.valueOf(j)));
    }

    public TLRPC.InputPeer getInputPeer(TLRPC.Peer peer) {
        if (peer instanceof TLRPC.TL_peerChat) {
            TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
            tL_inputPeerChat.chat_id = peer.chat_id;
            return tL_inputPeerChat;
        }
        if (peer instanceof TLRPC.TL_peerChannel) {
            TLRPC.Chat chat = getChat(Long.valueOf(peer.channel_id));
            if (chat != null && chat.access_hash == 0) {
                long j = chat.fromMessageDialogId;
                if (j != 0 && j != peer.channel_id && chat.fromMessageId != 0) {
                    TLRPC.TL_inputPeerChannelFromMessage tL_inputPeerChannelFromMessage = new TLRPC.TL_inputPeerChannelFromMessage();
                    tL_inputPeerChannelFromMessage.channel_id = peer.channel_id;
                    tL_inputPeerChannelFromMessage.peer = getInputPeer(chat.fromMessageDialogId);
                    tL_inputPeerChannelFromMessage.msg_id = chat.fromMessageId;
                    return tL_inputPeerChannelFromMessage;
                }
            }
            TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
            tL_inputPeerChannel.channel_id = peer.channel_id;
            if (chat != null) {
                tL_inputPeerChannel.access_hash = chat.access_hash;
            }
            return tL_inputPeerChannel;
        }
        TLRPC.User user = getUser(Long.valueOf(peer.user_id));
        if (user != null && user.access_hash == 0 && user.fromMessageDialogId != 0 && user.fromMessageId != 0) {
            TLRPC.TL_inputPeerUserFromMessage tL_inputPeerUserFromMessage = new TLRPC.TL_inputPeerUserFromMessage();
            tL_inputPeerUserFromMessage.user_id = peer.user_id;
            tL_inputPeerUserFromMessage.peer = getInputPeer(user.fromMessageDialogId);
            tL_inputPeerUserFromMessage.msg_id = user.fromMessageId;
            return tL_inputPeerUserFromMessage;
        }
        TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
        tL_inputPeerUser.user_id = peer.user_id;
        if (user != null) {
            tL_inputPeerUser.access_hash = user.access_hash;
        }
        return tL_inputPeerUser;
    }

    public TLRPC.InputPeer getInputPeer(long j) {
        if (j == getUserConfig().getClientUserId()) {
            return new TLRPC.TL_inputPeerSelf();
        }
        if (j < 0) {
            long j2 = -j;
            TLRPC.Chat chat = getChat(Long.valueOf(j2));
            if (ChatObject.isChannel(chat)) {
                if (chat != null && chat.access_hash == 0) {
                    long j3 = chat.fromMessageDialogId;
                    if (j3 != 0 && j3 != j && chat.fromMessageId != 0) {
                        TLRPC.TL_inputPeerChannelFromMessage tL_inputPeerChannelFromMessage = new TLRPC.TL_inputPeerChannelFromMessage();
                        tL_inputPeerChannelFromMessage.channel_id = j2;
                        tL_inputPeerChannelFromMessage.peer = getInputPeer(chat.fromMessageDialogId);
                        tL_inputPeerChannelFromMessage.msg_id = chat.fromMessageId;
                        return tL_inputPeerChannelFromMessage;
                    }
                }
                TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
                tL_inputPeerChannel.channel_id = j2;
                tL_inputPeerChannel.access_hash = chat.access_hash;
                return tL_inputPeerChannel;
            }
            TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
            tL_inputPeerChat.chat_id = j2;
            return tL_inputPeerChat;
        }
        TLRPC.User user = getUser(Long.valueOf(j));
        if (user != null && user.access_hash == 0) {
            long j4 = user.fromMessageDialogId;
            if (j4 != 0 && j4 != j && user.fromMessageId != 0) {
                TLRPC.TL_inputPeerUserFromMessage tL_inputPeerUserFromMessage = new TLRPC.TL_inputPeerUserFromMessage();
                tL_inputPeerUserFromMessage.user_id = j;
                tL_inputPeerUserFromMessage.peer = getInputPeer(user.fromMessageDialogId);
                tL_inputPeerUserFromMessage.msg_id = user.fromMessageId;
                return tL_inputPeerUserFromMessage;
            }
        }
        TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
        tL_inputPeerUser.user_id = j;
        if (user != null) {
            tL_inputPeerUser.access_hash = user.access_hash;
        }
        return tL_inputPeerUser;
    }

    public static TLRPC.InputPeer getInputPeer(TLRPC.Chat chat) {
        if (!ChatObject.isChannel(chat)) {
            TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
            tL_inputPeerChat.chat_id = chat.id;
            return tL_inputPeerChat;
        }
        if (chat != null && chat.access_hash == 0 && chat.fromMessageDialogId != 0 && chat.fromMessageId != 0) {
            TLRPC.TL_inputPeerChannelFromMessage tL_inputPeerChannelFromMessage = new TLRPC.TL_inputPeerChannelFromMessage();
            tL_inputPeerChannelFromMessage.channel_id = chat.id;
            tL_inputPeerChannelFromMessage.peer = getInstance(UserConfig.selectedAccount).getInputPeer(chat.fromMessageDialogId);
            tL_inputPeerChannelFromMessage.msg_id = chat.fromMessageId;
            return tL_inputPeerChannelFromMessage;
        }
        TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
        tL_inputPeerChannel.channel_id = chat.id;
        tL_inputPeerChannel.access_hash = chat.access_hash;
        return tL_inputPeerChannel;
    }

    public static TLRPC.InputPeer getInputPeer(TLRPC.User user) {
        if (user != null && user.access_hash == 0 && user.fromMessageDialogId != 0 && user.fromMessageId != 0) {
            TLRPC.TL_inputPeerUserFromMessage tL_inputPeerUserFromMessage = new TLRPC.TL_inputPeerUserFromMessage();
            tL_inputPeerUserFromMessage.user_id = user.id;
            tL_inputPeerUserFromMessage.peer = getInstance(UserConfig.selectedAccount).getInputPeer(user.fromMessageDialogId);
            tL_inputPeerUserFromMessage.msg_id = user.fromMessageId;
            return tL_inputPeerUserFromMessage;
        }
        TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
        tL_inputPeerUser.user_id = user.id;
        tL_inputPeerUser.access_hash = user.access_hash;
        return tL_inputPeerUser;
    }

    public static TLRPC.InputPeer getInputPeer(TLObject tLObject) {
        if (tLObject instanceof TLRPC.User) {
            return getInputPeer((TLRPC.User) tLObject);
        }
        if (tLObject instanceof TLRPC.Chat) {
            return getInputPeer((TLRPC.Chat) tLObject);
        }
        return null;
    }

    public TLRPC.Peer getPeer(long j) {
        if (j < 0) {
            long j2 = -j;
            if (ChatObject.isChannel(getChat(Long.valueOf(j2)))) {
                TLRPC.TL_peerChannel tL_peerChannel = new TLRPC.TL_peerChannel();
                tL_peerChannel.channel_id = j2;
                return tL_peerChannel;
            }
            TLRPC.TL_peerChat tL_peerChat = new TLRPC.TL_peerChat();
            tL_peerChat.chat_id = j2;
            return tL_peerChat;
        }
        TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
        tL_peerUser.user_id = j;
        return tL_peerUser;
    }

    public TLRPC.InputDocument getInputDocument(TLRPC.Document document) {
        if (document == null) {
            return null;
        }
        TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
        tL_inputDocument.id = document.id;
        tL_inputDocument.access_hash = document.access_hash;
        byte[] bArr = document.file_reference;
        tL_inputDocument.file_reference = bArr;
        if (bArr == null) {
            tL_inputDocument.file_reference = new byte[0];
        }
        return tL_inputDocument;
    }

    public String getPeerName(long j) {
        return getPeerName(j, false);
    }

    public String getPeerName(long j, boolean z) {
        if (j >= 0) {
            TLRPC.User user = getUser(Long.valueOf(j));
            if (z) {
                return AndroidUtilities.removeRTL(AndroidUtilities.removeDiacritics(UserObject.getFirstName(user, true)));
            }
            return AndroidUtilities.removeRTL(AndroidUtilities.removeDiacritics(UserObject.getUserName(user)));
        }
        TLRPC.Chat chat = getChat(Long.valueOf(-j));
        return AndroidUtilities.removeRTL(AndroidUtilities.removeDiacritics(chat == null ? _UrlKt.FRAGMENT_ENCODE_SET : chat.title));
    }

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        MessageObject messageObject;
        TLRPC.InputFile inputFile;
        Theme.ThemeInfo themeInfo;
        final Theme.ThemeAccent themeAccent;
        TLRPC.InputFile inputFile2;
        TLRPC.TL_theme tL_theme;
        TLRPC.TL_inputThemeSettings tL_inputThemeSettings = null;
        if (i == NotificationCenter.fileUploaded) {
            String str = (String) objArr[0];
            TLRPC.InputFile inputFile3 = (TLRPC.InputFile) objArr[1];
            String str2 = this.uploadingAvatar;
            if (str2 != null && str2.equals(str)) {
                TLRPC.TL_photos_uploadProfilePhoto tL_photos_uploadProfilePhoto = new TLRPC.TL_photos_uploadProfilePhoto();
                tL_photos_uploadProfilePhoto.file = inputFile3;
                tL_photos_uploadProfilePhoto.flags |= 1;
                getConnectionsManager().sendRequest(tL_photos_uploadProfilePhoto, new RequestDelegate() { 
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        this.f$0.lambda$didReceivedNotification$43(tLObject, tL_error);
                    }
                });
            } else {
                String str3 = this.uploadingWallpaper;
                if (str3 != null && str3.equals(str)) {
                    TL_account.uploadWallPaper uploadwallpaper = new TL_account.uploadWallPaper();
                    uploadwallpaper.file = inputFile3;
                    uploadwallpaper.mime_type = "image/jpeg";
                    final Theme.OverrideWallpaperInfo overrideWallpaperInfo = this.uploadingWallpaperInfo;
                    final String str4 = this.uploadingWallpaper;
                    final TLRPC.TL_wallPaperSettings tL_wallPaperSettings = new TLRPC.TL_wallPaperSettings();
                    tL_wallPaperSettings.blur = overrideWallpaperInfo.isBlurred;
                    tL_wallPaperSettings.motion = overrideWallpaperInfo.isMotion;
                    uploadwallpaper.settings = tL_wallPaperSettings;
                    Theme.OverrideWallpaperInfo overrideWallpaperInfo2 = this.uploadingWallpaperInfo;
                    overrideWallpaperInfo2.uploadingProgress = 1.0f;
                    overrideWallpaperInfo2.requestIds = new ArrayList<>();
                    this.uploadingWallpaperInfo.requestIds.add(Integer.valueOf(getConnectionsManager().sendRequest(uploadwallpaper, new RequestDelegate() { 
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            this.f$0.lambda$didReceivedNotification$45(overrideWallpaperInfo, tL_wallPaperSettings, str4, tLObject, tL_error);
                        }
                    })));
                } else {
                    Object obj = this.uploadingThemes.get(str);
                    if (obj instanceof Theme.ThemeInfo) {
                        themeInfo = (Theme.ThemeInfo) obj;
                        if (str.equals(themeInfo.uploadingThumb)) {
                            themeInfo.uploadedThumb = inputFile3;
                            themeInfo.uploadingThumb = null;
                        } else if (str.equals(themeInfo.uploadingFile)) {
                            themeInfo.uploadedFile = inputFile3;
                            themeInfo.uploadingFile = null;
                        }
                        inputFile = themeInfo.uploadedThumb;
                        inputFile2 = themeInfo.uploadedFile;
                        themeAccent = null;
                    } else if (obj instanceof Theme.ThemeAccent) {
                        Theme.ThemeAccent themeAccent2 = (Theme.ThemeAccent) obj;
                        if (str.equals(themeAccent2.uploadingThumb)) {
                            themeAccent2.uploadedThumb = inputFile3;
                            themeAccent2.uploadingThumb = null;
                        } else if (str.equals(themeAccent2.uploadingFile)) {
                            themeAccent2.uploadedFile = inputFile3;
                            themeAccent2.uploadingFile = null;
                        }
                        Theme.ThemeInfo themeInfo2 = themeAccent2.parentTheme;
                        TLRPC.InputFile inputFile4 = themeAccent2.uploadedThumb;
                        inputFile2 = themeAccent2.uploadedFile;
                        themeInfo = themeInfo2;
                        inputFile = inputFile4;
                        themeAccent = themeAccent2;
                    } else {
                        inputFile = null;
                        themeInfo = null;
                        themeAccent = null;
                        inputFile2 = null;
                    }
                    this.uploadingThemes.remove(str);
                    if (inputFile2 != null && inputFile != null) {
                        new File(str);
                        TL_account.uploadTheme uploadtheme = new TL_account.uploadTheme();
                        uploadtheme.mime_type = "application/x-tgtheme-android";
                        uploadtheme.file_name = "theme.attheme";
                        uploadtheme.file = inputFile2;
                        inputFile2.name = "theme.attheme";
                        uploadtheme.thumb = inputFile;
                        inputFile.name = "theme-preview.jpg";
                        uploadtheme.flags |= 1;
                        if (themeAccent != null) {
                            themeAccent.uploadedFile = null;
                            themeAccent.uploadedThumb = null;
                            tL_theme = themeAccent.info;
                            tL_inputThemeSettings = new TLRPC.TL_inputThemeSettings();
                            tL_inputThemeSettings.base_theme = Theme.getBaseThemeByKey(themeInfo.name);
                            tL_inputThemeSettings.accent_color = themeAccent.accentColor;
                            int i3 = themeAccent.accentColor2;
                            if (i3 != 0) {
                                tL_inputThemeSettings.flags |= 8;
                                tL_inputThemeSettings.outbox_accent_color = i3;
                            }
                            int i4 = themeAccent.myMessagesAccentColor;
                            if (i4 != 0) {
                                tL_inputThemeSettings.message_colors.add(Integer.valueOf(i4));
                                tL_inputThemeSettings.flags |= 1;
                                int i5 = themeAccent.myMessagesGradientAccentColor1;
                                if (i5 != 0) {
                                    tL_inputThemeSettings.message_colors.add(Integer.valueOf(i5));
                                    int i6 = themeAccent.myMessagesGradientAccentColor2;
                                    if (i6 != 0) {
                                        tL_inputThemeSettings.message_colors.add(Integer.valueOf(i6));
                                        int i7 = themeAccent.myMessagesGradientAccentColor3;
                                        if (i7 != 0) {
                                            tL_inputThemeSettings.message_colors.add(Integer.valueOf(i7));
                                        }
                                    }
                                }
                                tL_inputThemeSettings.message_colors_animated = themeAccent.myMessagesAnimated;
                            }
                            tL_inputThemeSettings.flags |= 2;
                            tL_inputThemeSettings.wallpaper_settings = new TLRPC.TL_wallPaperSettings();
                            if (!TextUtils.isEmpty(themeAccent.patternSlug)) {
                                TLRPC.TL_inputWallPaperSlug tL_inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
                                tL_inputWallPaperSlug.slug = themeAccent.patternSlug;
                                tL_inputThemeSettings.wallpaper = tL_inputWallPaperSlug;
                                TLRPC.WallPaperSettings wallPaperSettings = tL_inputThemeSettings.wallpaper_settings;
                                wallPaperSettings.intensity = (int) (themeAccent.patternIntensity * 100.0f);
                                wallPaperSettings.flags |= 8;
                            } else {
                                TLRPC.TL_inputWallPaperNoFile tL_inputWallPaperNoFile = new TLRPC.TL_inputWallPaperNoFile();
                                tL_inputWallPaperNoFile.id = 0L;
                                tL_inputThemeSettings.wallpaper = tL_inputWallPaperNoFile;
                            }
                            TLRPC.WallPaperSettings wallPaperSettings2 = tL_inputThemeSettings.wallpaper_settings;
                            wallPaperSettings2.motion = themeAccent.patternMotion;
                            long j = themeAccent.backgroundOverrideColor;
                            if (j != 0) {
                                wallPaperSettings2.background_color = (int) j;
                                wallPaperSettings2.flags |= 1;
                            }
                            long j2 = themeAccent.backgroundGradientOverrideColor1;
                            if (j2 != 0) {
                                wallPaperSettings2.second_background_color = (int) j2;
                                wallPaperSettings2.flags |= 16;
                                wallPaperSettings2.rotation = AndroidUtilities.getWallpaperRotation(themeAccent.backgroundRotation, true);
                            }
                            long j3 = themeAccent.backgroundGradientOverrideColor2;
                            if (j3 != 0) {
                                TLRPC.WallPaperSettings wallPaperSettings3 = tL_inputThemeSettings.wallpaper_settings;
                                wallPaperSettings3.third_background_color = (int) j3;
                                wallPaperSettings3.flags |= 32;
                            }
                            long j4 = themeAccent.backgroundGradientOverrideColor3;
                            if (j4 != 0) {
                                TLRPC.WallPaperSettings wallPaperSettings4 = tL_inputThemeSettings.wallpaper_settings;
                                wallPaperSettings4.fourth_background_color = (int) j4;
                                wallPaperSettings4.flags |= 64;
                            }
                        } else {
                            themeInfo.uploadedFile = null;
                            themeInfo.uploadedThumb = null;
                            tL_theme = themeInfo.info;
                        }
                        final Theme.ThemeInfo themeInfo3 = themeInfo;
                        final TLRPC.TL_inputThemeSettings tL_inputThemeSettings2 = tL_inputThemeSettings;
                        final TLRPC.TL_theme tL_theme2 = tL_theme;
                        getConnectionsManager().sendRequest(uploadtheme, new RequestDelegate() { 
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                this.f$0.lambda$didReceivedNotification$51(tL_theme2, themeInfo3, tL_inputThemeSettings2, themeAccent, tLObject, tL_error);
                            }
                        });
                    }
                }
            }
        } else if (i == NotificationCenter.fileUploadFailed) {
            String str5 = (String) objArr[0];
            String str6 = this.uploadingAvatar;
            if (str6 != null && str6.equals(str5)) {
                this.uploadingAvatar = null;
            } else {
                String str7 = this.uploadingWallpaper;
                if (str7 != null && str7.equals(str5)) {
                    this.uploadingWallpaper = null;
                    this.uploadingWallpaperInfo = null;
                } else {
                    Object objRemove = this.uploadingThemes.remove(str5);
                    if (objRemove instanceof Theme.ThemeInfo) {
                        Theme.ThemeInfo themeInfo4 = (Theme.ThemeInfo) objRemove;
                        themeInfo4.uploadedFile = null;
                        themeInfo4.uploadedThumb = null;
                        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeUploadError, themeInfo4, null);
                    } else if (objRemove instanceof Theme.ThemeAccent) {
                        Theme.ThemeAccent themeAccent3 = (Theme.ThemeAccent) objRemove;
                        themeAccent3.uploadingThumb = null;
                        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeUploadError, themeAccent3.parentTheme, themeAccent3);
                    }
                }
            }
        }
        if (i == NotificationCenter.fileUploadProgressChanged) {
            String str8 = (String) objArr[0];
            String str9 = this.uploadingWallpaper;
            if (str9 == null || !str9.equals(str8)) {
                return;
            }
            this.uploadingWallpaperInfo.uploadingProgress = ((Long) objArr[1]).longValue() / ((Long) objArr[2]).longValue();
            return;
        }
        if (i == NotificationCenter.messageReceivedByServer) {
            if (((Boolean) objArr[6]).booleanValue()) {
                return;
            }
            Integer num = (Integer) objArr[0];
            Integer num2 = (Integer) objArr[1];
            Long l = (Long) objArr[3];
            ArrayList<MessageObject> arrayList = this.dialogMessage.get(l.longValue());
            for (int i8 = 0; arrayList != null && i8 < arrayList.size(); i8++) {
                MessageObject messageObject2 = arrayList.get(i8);
                if (messageObject2 != null && (messageObject2.getId() == num.intValue() || messageObject2.messageOwner.local_id == num.intValue())) {
                    messageObject2.messageOwner.id = num2.intValue();
                    messageObject2.messageOwner.send_state = 0;
                }
                MessageObject messageObject3 = this.dialogMessagesByIds.get(num.intValue());
                if (messageObject3 != null) {
                    this.dialogMessagesByIds.remove(num.intValue());
                    this.dialogMessagesByIds.put(num2.intValue(), messageObject3);
                }
            }
            TLRPC.Dialog dialog = this.dialogs_dict.get(l.longValue());
            if (dialog != null && dialog.top_message == num.intValue()) {
                dialog.top_message = num2.intValue();
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            if (DialogObject.isChatDialog(l.longValue())) {
                TLRPC.ChatFull chatFull = this.fullChats.get(-l.longValue());
                TLRPC.Chat chat = getChat(Long.valueOf(-l.longValue()));
                if (chat == null || ChatObject.hasAdminRights(chat) || MessageObject.isEphemeralMessageId(num2.intValue()) || chatFull == null || chatFull.slowmode_seconds == 0) {
                    return;
                }
                chatFull.slowmode_next_send_date = getConnectionsManager().getCurrentTime() + chatFull.slowmode_seconds;
                chatFull.flags |= 262144;
                getMessagesStorage().updateChatInfo(chatFull, false);
                return;
            }
            return;
        }
        if (i == NotificationCenter.updateMessageMedia) {
            TLRPC.Message message = (TLRPC.Message) objArr[0];
            if (message.peer_id.channel_id != 0 || (messageObject = this.dialogMessagesByIds.get(message.id)) == null) {
                return;
            }
            messageObject.messageOwner.media = MessageObject.getMedia(message);
            if (MessageObject.getMedia(message).ttl_seconds != 0) {
                if ((MessageObject.getMedia(message).photo instanceof TLRPC.TL_photoEmpty) || (MessageObject.getMedia(message).document instanceof TLRPC.TL_documentEmpty)) {
                    messageObject.setType();
                    getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
                    return;
                }
                return;
            }
            return;
        }
        if (i == NotificationCenter.currentUserPremiumStatusChanged) {
            loadAppConfig(false);
            getContactsController().reloadContactsStatusesMaybe(true);
            if ((!this.storyQualityFull || getUserConfig().isPremium()) && !getUserConfig().isPremium()) {
                return;
            }
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.storyQualityUpdate, new Object[0]);
        }
    }

    public void lambda$didReceivedNotification$42() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_AVATAR));
        getUserConfig().saveConfig(true);
    }

    public void lambda$didReceivedNotification$44(TLRPC.WallPaper wallPaper, TLRPC.TL_wallPaperSettings tL_wallPaperSettings, Theme.OverrideWallpaperInfo overrideWallpaperInfo, File file, String str) {
        if (this.uploadingWallpaper == null || this.uploadingWallpaperInfo.requestIds == null || wallPaper == null) {
            return;
        }
        wallPaper.settings = tL_wallPaperSettings;
        wallPaper.flags |= 4;
        overrideWallpaperInfo.slug = wallPaper.slug;
        overrideWallpaperInfo.saveOverrideWallpaper();
        ArrayList<TLRPC.WallPaper> arrayList = new ArrayList<>();
        arrayList.add(wallPaper);
        getMessagesStorage().putWallpapers(arrayList, 2);
        TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(wallPaper.document.thumbs, 320);
        if (closestPhotoSizeWithSize != null) {
            ImageLoader.getInstance().replaceImageInCache(Utilities.MD5(file.getAbsolutePath()) + "@100_100", closestPhotoSizeWithSize.location.volume_id + "_" + closestPhotoSizeWithSize.location.local_id + "@100_100", ImageLocation.getForDocument(closestPhotoSizeWithSize, wallPaper.document), false);
        }
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.wallpapersNeedReload, wallPaper.slug);
        ArrayList<Integer> arrayList2 = this.uploadingWallpaperInfo.requestIds;
        if (arrayList2 == null || overrideWallpaperInfo.dialogId == 0) {
            return;
        }
        arrayList2.add(Integer.valueOf(ChatThemeController.getInstance(this.currentAccount).setWallpaperToPeer(overrideWallpaperInfo.dialogId, str, overrideWallpaperInfo, null, null)));
    }

    public void lambda$didReceivedNotification$47(final Theme.ThemeInfo themeInfo, final Theme.ThemeAccent themeAccent, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$didReceivedNotification$46(tLObject, themeInfo, themeAccent);
            }
        });
    }

    public void lambda$didReceivedNotification$48(TLObject tLObject, Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent) {
        if (tLObject instanceof TLRPC.TL_theme) {
            Theme.setThemeUploadInfo(themeInfo, themeAccent, (TLRPC.TL_theme) tLObject, this.currentAccount, false);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeUploadedToServer, themeInfo, themeAccent);
        } else {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.themeUploadError, themeInfo, themeAccent);
        }
    }

    public void lambda$cleanup$52() {
        this.readTasks.clear();
        this.readTasksMap.clear();
        this.repliesReadTasks.clear();
        this.threadsReadTasksMap.clear();
        this.updatesQueueSeq.clear();
        this.updatesQueuePts.clear();
        this.updatesQueueQts.clear();
        this.gettingUnknownChannels.clear();
        this.gettingUnknownDialogs.clear();
        this.updatesStartWaitTimeSeq = 0L;
        this.updatesStartWaitTimePts = 0L;
        this.updatesStartWaitTimeQts = 0L;
        this.createdDialogIds.clear();
        this.createdScheduledDialogIds.clear();
        this.gettingDifference = false;
        this.resetDialogsPinned = null;
        this.resetDialogsAll = null;
    }

    public void lambda$setLastCreatedDialogId$55(boolean z, boolean z2, long j) {
        ArrayList<Long> arrayList = z ? this.createdScheduledDialogIds : this.createdDialogIds;
        if (z2) {
            if (arrayList.contains(Long.valueOf(j))) {
                return;
            }
            arrayList.add(Long.valueOf(j));
            return;
        }
        arrayList.remove(Long.valueOf(j));
    }

    public TLRPC.TL_chatInviteExported getExportedInvite(long j) {
        return this.exportedChats.get(j);
    }

    public boolean putUser(TLRPC.User user, boolean z) {
        return putUser(user, z, false);
    }

    void lambda$reloadUser$56(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof Vector) {
            ArrayList<T> arrayList = ((Vector) tLObject).objects;
            ArrayList<TLRPC.User> arrayList2 = new ArrayList<>();
            for (int i = 0; i < arrayList.size(); i++) {
                if (arrayList.get(i) instanceof TLRPC.User) {
                    arrayList2.add((TLRPC.User) arrayList.get(i));
                }
            }
            getMessagesController().putUsers(arrayList2, false);
        }
    }

    public void putUsers(ArrayList<TLRPC.User> arrayList, boolean z) {
        if (arrayList == null || arrayList.isEmpty()) {
            return;
        }
        int size = arrayList.size();
        boolean z2 = false;
        for (int i = 0; i < size; i++) {
            if (putUser(arrayList.get(i), z)) {
                z2 = true;
            }
        }
        if (z2) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$putUsers$57();
                }
            });
        }
    }

    public void lambda$putChat$58(TLRPC.Chat chat) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.channelRightsUpdated, chat);
    }

    public void lambda$getGroupCall$63(final long j, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getGroupCall$62(tLObject, j, runnable);
            }
        });
    }

    public void lambda$reloadDialogsReadValue$64(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            ArrayList<TLRPC.Update> arrayList = new ArrayList<>();
            for (int i = 0; i < tL_messages_peerDialogs.dialogs.size(); i++) {
                TLRPC.Dialog dialog = tL_messages_peerDialogs.dialogs.get(i);
                DialogObject.initDialog(dialog);
                Integer num = this.dialogs_read_inbox_max.get(Long.valueOf(dialog.id));
                if (num == null) {
                    num = 0;
                }
                this.dialogs_read_inbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_inbox_max_id, num.intValue())));
                if (num.intValue() == 0) {
                    if (dialog.peer.channel_id != 0) {
                        TL_update.TL_updateReadChannelInbox tL_updateReadChannelInbox = new TL_update.TL_updateReadChannelInbox();
                        tL_updateReadChannelInbox.channel_id = dialog.peer.channel_id;
                        tL_updateReadChannelInbox.max_id = dialog.read_inbox_max_id;
                        tL_updateReadChannelInbox.still_unread_count = dialog.unread_count;
                        arrayList.add(tL_updateReadChannelInbox);
                    } else {
                        TL_update.TL_updateReadHistoryInbox tL_updateReadHistoryInbox = new TL_update.TL_updateReadHistoryInbox();
                        tL_updateReadHistoryInbox.peer = dialog.peer;
                        tL_updateReadHistoryInbox.max_id = dialog.read_inbox_max_id;
                        arrayList.add(tL_updateReadHistoryInbox);
                    }
                }
                Integer num2 = this.dialogs_read_outbox_max.get(Long.valueOf(dialog.id));
                if (num2 == null) {
                    num2 = 0;
                }
                this.dialogs_read_outbox_max.put(Long.valueOf(dialog.id), Integer.valueOf(Math.max(dialog.read_outbox_max_id, num2.intValue())));
                if (dialog.read_outbox_max_id > num2.intValue()) {
                    if (dialog.peer.channel_id != 0) {
                        TL_update.TL_updateReadChannelOutbox tL_updateReadChannelOutbox = new TL_update.TL_updateReadChannelOutbox();
                        tL_updateReadChannelOutbox.channel_id = dialog.peer.channel_id;
                        tL_updateReadChannelOutbox.max_id = dialog.read_outbox_max_id;
                        arrayList.add(tL_updateReadChannelOutbox);
                    } else {
                        TL_update.TL_updateReadHistoryOutbox tL_updateReadHistoryOutbox = new TL_update.TL_updateReadHistoryOutbox();
                        tL_updateReadHistoryOutbox.peer = dialog.peer;
                        tL_updateReadHistoryOutbox.max_id = dialog.read_outbox_max_id;
                        arrayList.add(tL_updateReadHistoryOutbox);
                    }
                }
            }
            if (arrayList.isEmpty()) {
                return;
            }
            processUpdateArray(arrayList, null, null, false, 0);
        }
    }

    public TLRPC.ChannelParticipant getAdminInChannel(long j, long j2) {
        LongSparseArray<TLRPC.ChannelParticipant> longSparseArray = this.channelAdmins.get(j2);
        if (longSparseArray == null) {
            return null;
        }
        return longSparseArray.get(j);
    }

    public String getAdminRank(long j, long j2) {
        TLRPC.ChannelParticipant channelParticipant;
        TLRPC.ChannelParticipant channelParticipant2;
        if (j == j2) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        LongSparseArray<TLRPC.ChannelParticipant> longSparseArray = this.channelAdmins.get(j);
        if (longSparseArray != null && (channelParticipant2 = longSparseArray.get(j2)) != null) {
            String str = channelParticipant2.rank;
            if (str != null) {
                return str;
            }
            if (channelParticipant2 instanceof TLRPC.TL_channelParticipantCreator) {
                return LocaleController.getString(R.string.ChatTagOwner);
            }
            if (channelParticipant2 instanceof TLRPC.TL_channelParticipantAdmin) {
                return LocaleController.getString(R.string.ChatTagAdmin);
            }
        }
        TLRPC.ChatFull chatFull = getChatFull(j);
        if (chatFull != null && chatFull.participants != null) {
            for (int i = 0; i < chatFull.participants.participants.size(); i++) {
                TLRPC.ChatParticipant chatParticipant = chatFull.participants.participants.get(i);
                if (chatParticipant.user_id == j2) {
                    String str2 = chatParticipant.rank;
                    if (str2 != null) {
                        return str2;
                    }
                    if ((chatParticipant instanceof TLRPC.TL_chatChannelParticipant) && (channelParticipant = ((TLRPC.TL_chatChannelParticipant) chatParticipant).channelParticipant) != null) {
                        return channelParticipant.rank;
                    }
                    if (chatParticipant instanceof TLRPC.TL_chatParticipantCreator) {
                        return LocaleController.getString(R.string.ChatTagOwner);
                    }
                    if (chatParticipant instanceof TLRPC.TL_chatParticipantAdmin) {
                        return LocaleController.getString(R.string.ChatTagAdmin);
                    }
                    return null;
                }
            }
        }
        return null;
    }

    public void updateRank(long j, long j2, String str) {
        TLRPC.ChannelParticipant channelParticipant;
        if (TextUtils.isEmpty(str)) {
            str = null;
        }
        String str2 = str;
        getChat(Long.valueOf(j));
        LongSparseArray<TLRPC.ChannelParticipant> longSparseArray = this.channelAdmins.get(j);
        if (longSparseArray != null && (channelParticipant = longSparseArray.get(j2)) != null) {
            channelParticipant.rank = str2;
        }
        TLRPC.ChatFull chatFull = getChatFull(j);
        if (chatFull != null && chatFull.participants != null) {
            for (int i = 0; i < chatFull.participants.participants.size(); i++) {
                TLRPC.ChatParticipant chatParticipant = chatFull.participants.participants.get(i);
                chatParticipant.setRank(j2, str2);
                if (chatParticipant.user_id == j2) {
                    if (chatParticipant instanceof TLRPC.TL_chatChannelParticipant) {
                        TLRPC.ChannelParticipant channelParticipant2 = ((TLRPC.TL_chatChannelParticipant) chatParticipant).channelParticipant;
                        if (channelParticipant2 != null) {
                            channelParticipant2.rank = str2;
                        }
                    } else {
                        chatParticipant.rank = str2;
                    }
                }
            }
        }
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, 0);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.updatedChatRanks, Long.valueOf(j), Long.valueOf(j2), str2);
        MessagesStorage.getInstance(this.currentAccount).updateRanksInLastMessages(-j, j2, str2);
    }

    public TLObject getParticipant(long j, long j2) {
        LongSparseArray<TLRPC.ChannelParticipant> longSparseArray = this.channelAdmins.get(j);
        if (longSparseArray == null) {
            TLRPC.ChatFull chatFull = getChatFull(j);
            if (chatFull == null || chatFull.participants == null) {
                return null;
            }
            for (int i = 0; i < chatFull.participants.participants.size(); i++) {
                TLRPC.ChatParticipant chatParticipant = chatFull.participants.participants.get(i);
                if (chatParticipant.user_id == j2) {
                    return chatParticipant;
                }
            }
            return null;
        }
        return longSparseArray.get(j2);
    }

    public boolean isAdmin(long j, long j2) {
        if (j == j2) {
            return true;
        }
        LongSparseArray<TLRPC.ChannelParticipant> longSparseArray = this.channelAdmins.get(j);
        if (longSparseArray == null) {
            TLRPC.ChatFull chatFull = getChatFull(j);
            if (chatFull != null && chatFull.participants != null) {
                for (int i = 0; i < chatFull.participants.participants.size(); i++) {
                    TLRPC.ChatParticipant chatParticipant = chatFull.participants.participants.get(i);
                    if (chatParticipant.user_id == j2) {
                        return (chatParticipant instanceof TLRPC.TL_chatParticipantAdmin) || (chatParticipant instanceof TLRPC.TL_chatParticipantCreator);
                    }
                }
            }
            return false;
        }
        TLRPC.ChannelParticipant channelParticipant = longSparseArray.get(j2);
        return (channelParticipant instanceof TLRPC.TL_channelParticipantAdmin) || (channelParticipant instanceof TLRPC.TL_channelParticipantCreator);
    }

    public boolean isOwner(long j, long j2) {
        if (j == j2) {
            return true;
        }
        TLRPC.Chat chat = getChat(Long.valueOf(j));
        if (getUserConfig().getClientUserId() == j2 && chat != null && chat.creator) {
            return true;
        }
        LongSparseArray<TLRPC.ChannelParticipant> longSparseArray = this.channelAdmins.get(j);
        if (longSparseArray == null) {
            TLRPC.ChatFull chatFull = getChatFull(j);
            if (chatFull != null && chatFull.participants != null) {
                for (int i = 0; i < chatFull.participants.participants.size(); i++) {
                    TLRPC.ChatParticipant chatParticipant = chatFull.participants.participants.get(i);
                    if (chatParticipant.user_id == j2) {
                        return chatParticipant instanceof TLRPC.TL_chatParticipantCreator;
                    }
                }
            }
            return false;
        }
        return longSparseArray.get(j2) instanceof TLRPC.TL_channelParticipantCreator;
    }

    public boolean isChannelAdminsLoaded(long j) {
        return this.channelAdmins.get(j) != null;
    }

    public void loadChannelAdmins(final long j, boolean z) {
        if ((SystemClock.elapsedRealtime() / 1000) - ((long) this.loadingChannelAdmins.get(j)) < 60) {
            return;
        }
        this.loadingChannelAdmins.put(j, (int) (SystemClock.elapsedRealtime() / 1000));
        if (z) {
            getMessagesStorage().loadChannelAdmins(j);
            return;
        }
        TLRPC.TL_channels_getParticipants tL_channels_getParticipants = new TLRPC.TL_channels_getParticipants();
        tL_channels_getParticipants.channel = getInputChannel(j);
        tL_channels_getParticipants.limit = 100;
        tL_channels_getParticipants.filter = new TLRPC.TL_channelParticipantsAdmins();
        getConnectionsManager().sendRequest(tL_channels_getParticipants, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$loadChannelAdmins$65(j, tLObject, tL_error);
            }
        });
    }

    public void lambda$processLoadedChannelAdmins$66(long j, LongSparseArray longSparseArray, boolean z) {
        this.channelAdmins.put(j, longSparseArray);
        if (z) {
            this.loadingChannelAdmins.delete(j);
            loadChannelAdmins(j, false);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didLoadChatAdmins, Long.valueOf(j));
        }
    }

    public void loadFullChat(final long j, final int i, boolean z) {
        TLRPC.TL_messages_getFullChat tL_messages_getFullChat;
        TLObject tLObject;
        boolean z2 = this.loadedFullChats.get(j, 0L) > 0;
        if (this.loadingFullChats.contains(Long.valueOf(j))) {
            return;
        }
        if (z || !z2) {
            this.loadingFullChats.add(Long.valueOf(j));
            final long j2 = -j;
            final TLRPC.Chat chat = getChat(Long.valueOf(j));
            if (ChatObject.isChannel(chat)) {
                TLRPC.TL_channels_getFullChannel tL_channels_getFullChannel = new TLRPC.TL_channels_getFullChannel();
                tL_channels_getFullChannel.channel = getInputChannel(chat);
                loadChannelAdmins(j, !z2);
                tLObject = tL_channels_getFullChannel;
            } else {
                tL_messages_getFullChat = new TLRPC.TL_messages_getFullChat();
                tL_messages_getFullChat.chat_id = j;
                if (this.dialogs_read_inbox_max.get(Long.valueOf(j2)) == null || this.dialogs_read_outbox_max.get(Long.valueOf(j2)) == null) {
                    tLObject = tL_messages_getFullChat;
                    reloadDialogsReadValue(null, j2);
                    tLObject = tL_messages_getFullChat;
                }
            }
            tLObject = tL_messages_getFullChat;
            int iSendRequest = getConnectionsManager().sendRequest(tLObject, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$loadFullChat$69(j2, j, chat, i, tLObject2, tL_error);
                }
            });
            if (i != 0) {
                getConnectionsManager().bindRequestToGuid(iSendRequest, i);
            }
        }
    }

    public void lambda$loadFullChat$67(long j, TLRPC.TL_messages_chatFull tL_messages_chatFull, int i, long j2) {
        TLRPC.ChatFull chatFull = this.fullChats.get(j);
        if (chatFull != null) {
            tL_messages_chatFull.full_chat.inviterId = chatFull.inviterId;
        }
        this.fullChats.put(j, tL_messages_chatFull.full_chat);
        long j3 = -j;
        getTranslateController().updateDialogFull(j3);
        applyDialogNotificationsSettings(j3, 0L, tL_messages_chatFull.full_chat.notify_settings);
        for (int i2 = 0; i2 < tL_messages_chatFull.full_chat.bot_info.size(); i2++) {
            getMediaDataController().putBotInfo(j3, tL_messages_chatFull.full_chat.bot_info.get(i2));
        }
        int iIndexOfKey = this.blockePeers.indexOfKey(j3);
        if (tL_messages_chatFull.full_chat.blocked) {
            if (iIndexOfKey < 0) {
                this.blockePeers.put(j3, 1);
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.blockedUsersDidLoad, new Object[0]);
            }
        } else if (iIndexOfKey >= 0) {
            this.blockePeers.removeAt(iIndexOfKey);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
        this.exportedChats.put(j, tL_messages_chatFull.full_chat.exported_invite);
        this.loadingFullChats.remove(Long.valueOf(j));
        this.loadedFullChats.put(j, System.currentTimeMillis());
        putUsers(tL_messages_chatFull.users, false);
        putChats(tL_messages_chatFull.chats, false);
        if (tL_messages_chatFull.full_chat.stickerset != null) {
            getMediaDataController().getGroupStickerSetById(tL_messages_chatFull.full_chat.stickerset);
        }
        if (tL_messages_chatFull.full_chat.emojiset != null) {
            getMediaDataController().getGroupStickerSetById(tL_messages_chatFull.full_chat.emojiset);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatInfoDidLoad, tL_messages_chatFull.full_chat, Integer.valueOf(i), Boolean.FALSE, Boolean.TRUE);
        TLRPC.Dialog dialog = this.dialogs_dict.get(j3);
        if (dialog != null) {
            TLRPC.ChatFull chatFull2 = tL_messages_chatFull.full_chat;
            if ((chatFull2.flags & 2048) != 0) {
                int i3 = dialog.folder_id;
                int i4 = chatFull2.folder_id;
                if (i3 != i4) {
                    dialog.folder_id = i4;
                    sortDialogs(null);
                    getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
                }
            }
            int i5 = dialog.ttl_period;
            int i6 = tL_messages_chatFull.full_chat.ttl_period;
            if (i5 != i6) {
                dialog.ttl_period = i6;
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
            }
            boolean z = dialog.view_forum_as_messages;
            boolean z2 = tL_messages_chatFull.full_chat.view_forum_as_messages;
            if (z != z2) {
                dialog.view_forum_as_messages = z2;
                getMessagesStorage().setDialogViewThreadAsMessages(j2, tL_messages_chatFull.full_chat.view_forum_as_messages);
            }
        }
    }

    public void lambda$loadFullUser$72(long j, Utilities.Callback callback, final TLRPC.User user, final int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_users_userFull tL_users_userFull = (TLRPC.TL_users_userFull) tLObject;
            final TLRPC.UserFull userFull = tL_users_userFull.full_user;
            putUsers(tL_users_userFull.users, false);
            putChats(tL_users_userFull.chats, false);
            TLRPC.UserFull userFull2 = tL_users_userFull.full_user;
            userFull2.user = getUser(Long.valueOf(userFull2.id));
            getMessagesStorage().updateUserInfo(userFull, false);
            getStoriesController().updateStoriesFromFullPeer(j, userFull.stories);
            ChatThemeController chatThemeController = ChatThemeController.getInstance(this.currentAccount);
            TLRPC.UserFull userFull3 = tL_users_userFull.full_user;
            chatThemeController.saveChatWallpaper(userFull3.id, userFull3.wallpaper);
            if (callback != null) {
                callback.run(userFull);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$loadFullUser$70(userFull, user, i);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadFullUser$71(user);
            }
        });
    }

    public void lambda$reloadMessages$74(final long j, boolean z, int i, final ArrayList arrayList, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            return;
        }
        TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
        LongSparseArray longSparseArray = new LongSparseArray();
        for (int i2 = 0; i2 < messages_messages.users.size(); i2++) {
            TLRPC.User user = messages_messages.users.get(i2);
            longSparseArray.put(user.id, user);
        }
        LongSparseArray longSparseArray2 = new LongSparseArray();
        for (int i3 = 0; i3 < messages_messages.chats.size(); i3++) {
            TLRPC.Chat chat = messages_messages.chats.get(i3);
            longSparseArray2.put(chat.id, chat);
        }
        Integer numValueOf = this.dialogs_read_inbox_max.get(Long.valueOf(j));
        if (numValueOf == null) {
            numValueOf = Integer.valueOf(getMessagesStorage().getDialogReadMax(false, j));
            this.dialogs_read_inbox_max.put(Long.valueOf(j), numValueOf);
        }
        Integer num = numValueOf;
        Integer numValueOf2 = this.dialogs_read_outbox_max.get(Long.valueOf(j));
        if (numValueOf2 == null) {
            numValueOf2 = Integer.valueOf(getMessagesStorage().getDialogReadMax(true, j));
            this.dialogs_read_outbox_max.put(Long.valueOf(j), numValueOf2);
        }
        Integer num2 = numValueOf2;
        final ArrayList arrayList2 = new ArrayList();
        int i4 = 0;
        while (true) {
            int size = messages_messages.messages.size();
            ArrayList<TLRPC.Message> arrayList3 = messages_messages.messages;
            if (i4 < size) {
                TLRPC.Message message = arrayList3.get(i4);
                message.dialog_id = j;
                if (!z) {
                    message.unread = (message.out ? num2 : num).intValue() < message.id;
                }
                arrayList2.add(new MessageObject(this.currentAccount, message, (LongSparseArray<TLRPC.User>) longSparseArray, (LongSparseArray<TLRPC.Chat>) longSparseArray2, true, true));
                i4++;
            } else {
                ImageLoader.saveMessagesThumbs(arrayList3);
                getMessagesStorage().putMessages(messages_messages, j, -1, 0, false, i, 0L);
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$reloadMessages$73(j, arrayList, arrayList2);
                    }
                });
                return;
            }
        }
    }

    public void lambda$loadPeerSettings$80(final long j, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadPeerSettings$79(j, tLObject);
            }
        });
    }

    public void lambda$didAddedNewTask$81(int i) {
        int i2;
        if (!(this.currentDeletingTaskMids == null && this.currentDeletingTaskMediaMids == null && !this.gettingNewDeleteTask) && ((i2 = this.currentDeletingTaskTime) == 0 || i >= i2)) {
            return;
        }
        getNewDeleteTask(null, null);
    }

    public void lambda$getNewDeleteTask$83(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.gettingNewDeleteTask = true;
        getMessagesStorage().getNewTask(longSparseArray, longSparseArray2);
    }

    private boolean checkDeletingTask(boolean z) {
        int i;
        int currentTime = getConnectionsManager().getCurrentTime();
        if ((this.currentDeletingTaskMids == null && this.currentDeletingTaskMediaMids == null) || (!z && ((i = this.currentDeletingTaskTime) == 0 || i > currentTime))) {
            return false;
        }
        this.currentDeletingTaskTime = 0;
        if (this.currentDeleteTaskRunnable != null && !z) {
            Utilities.stageQueue.cancelRunnable(this.currentDeleteTaskRunnable);
        }
        this.currentDeleteTaskRunnable = null;
        LongSparseArray<ArrayList<Integer>> longSparseArray = this.currentDeletingTaskMids;
        final LongSparseArray<ArrayList<Integer>> longSparseArrayClone = longSparseArray != null ? longSparseArray.m658clone() : null;
        LongSparseArray<ArrayList<Integer>> longSparseArray2 = this.currentDeletingTaskMediaMids;
        final LongSparseArray<ArrayList<Integer>> longSparseArrayClone2 = longSparseArray2 != null ? longSparseArray2.m658clone() : null;
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$checkDeletingTask$86(longSparseArrayClone, longSparseArrayClone2);
            }
        });
        return true;
    }

    public void lambda$checkDeletingTask$85(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        getNewDeleteTask(longSparseArray, longSparseArray2);
        this.currentDeletingTaskTime = 0;
        this.currentDeletingTaskMids = null;
        this.currentDeletingTaskMediaMids = null;
    }

    public void processLoadedDeleteTask(final int i, final LongSparseArray<ArrayList<Integer>> longSparseArray, final LongSparseArray<ArrayList<Integer>> longSparseArray2) {
        Utilities.stageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$processLoadedDeleteTask$88(longSparseArray, longSparseArray2, i);
            }
        });
    }

    public void lambda$processLoadedDeleteTask$87() {
        checkDeletingTask(true);
    }

    public DialogPhotos getDialogPhotos(long j) {
        DialogPhotos dialogPhotos = this.dialogPhotos.get(j);
        if (dialogPhotos != null) {
            return dialogPhotos;
        }
        LongSparseArray<DialogPhotos> longSparseArray = this.dialogPhotos;
        DialogPhotos dialogPhotos2 = new DialogPhotos(j);
        longSparseArray.put(j, dialogPhotos2);
        return dialogPhotos2;
    }

    public class DialogPhotos {
        public static final int STEP = 80;
        public final long dialogId;
        private boolean loading;
        public final ArrayList<TLRPC.Photo> photos = new ArrayList<>();
        public boolean fromCache = true;
        public boolean loaded = false;
        private int lastLoadOffset = -1;
        private int lastLoadCount = -1;

        public void addPhotoAtStart(TLRPC.Photo photo) {
        }

        public DialogPhotos(long j) {
            this.dialogId = j;
        }

        public void loadAfter(int i, boolean z) {
            int i2 = 0;
            if (this.photos.isEmpty()) {
                load(0, 80);
                return;
            }
            if (i < 0) {
                i += this.photos.size();
            }
            if (i >= this.photos.size()) {
                i -= this.photos.size();
            }
            if (i < 0 || i >= this.photos.size()) {
                return;
            }
            for (int i3 = 0; i3 < this.photos.size(); i3++) {
                if (this.photos.get(i3) == null) {
                    if (z) {
                        while (this.photos.get(i) != null) {
                            i++;
                            if (i >= this.photos.size()) {
                                i = 0;
                            }
                        }
                        while (i2 <= 80) {
                            int i4 = i + i2;
                            if (i4 >= this.photos.size() || this.photos.get(i4) != null) {
                                break;
                            } else {
                                i2++;
                            }
                        }
                        if (i2 > 0) {
                            load(i, i2);
                            return;
                        }
                        return;
                    }
                    while (this.photos.get(i) != null) {
                        i--;
                        if (i < 0) {
                            i = this.photos.size() - 1;
                        }
                    }
                    while (i2 <= 80) {
                        int i5 = i - i2;
                        if (i5 < 0 || this.photos.get(i5) != null) {
                            break;
                        } else {
                            i2++;
                        }
                    }
                    if (i2 > 0) {
                        load(i - i2, i2);
                        return;
                    }
                    return;
                }
            }
        }

        public void load(final int i, final int i2) {
            if (this.loading || i2 <= 0 || i < 0) {
                return;
            }
            if (i2 == this.lastLoadCount && i == this.lastLoadOffset) {
                return;
            }
            this.loading = true;
            this.lastLoadOffset = i;
            this.lastLoadCount = i2;
            long j = this.dialogId;
            if (j >= 0) {
                TLRPC.User user = MessagesController.this.getUser(Long.valueOf(j));
                if (user == null) {
                    this.loading = false;
                    return;
                }
                TLRPC.TL_photos_getUserPhotos tL_photos_getUserPhotos = new TLRPC.TL_photos_getUserPhotos();
                tL_photos_getUserPhotos.offset = i;
                tL_photos_getUserPhotos.limit = i2;
                tL_photos_getUserPhotos.max_id = 0L;
                tL_photos_getUserPhotos.user_id = MessagesController.this.getInputUser(user);
                MessagesController.this.getConnectionsManager().sendRequest(tL_photos_getUserPhotos, new RequestDelegate() { 
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        this.f$0.lambda$load$1(i, i2, tLObject, tL_error);
                    }
                });
                return;
            }
            TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
            tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterChatPhotos();
            tL_messages_search.add_offset = i;
            tL_messages_search.limit = i2;
            tL_messages_search.offset_id = 0;
            tL_messages_search.q = _UrlKt.FRAGMENT_ENCODE_SET;
            tL_messages_search.peer = MessagesController.this.getInputPeer(this.dialogId);
            MessagesController.this.getConnectionsManager().sendRequest(tL_messages_search, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$load$3(i, i2, tLObject, tL_error);
                }
            });
        }

        public void lambda$load$0(TLRPC.photos_Photos photos_photos, int i, int i2) {
            MessagesController.this.putUsers(photos_photos.users, false);
            onLoaded(i, i2, photos_photos);
        }

        public void lambda$load$2(TLRPC.messages_Messages messages_messages, int i, int i2) {
            TLRPC.Photo photo;
            MessagesController.this.putUsers(messages_messages.users, false);
            MessagesController.this.putChats(messages_messages.chats, false);
            TLRPC.TL_photos_photos tL_photos_photos = new TLRPC.TL_photos_photos();
            tL_photos_photos.count = messages_messages.count;
            for (int i3 = 0; i3 < messages_messages.messages.size(); i3++) {
                TLRPC.MessageAction messageAction = messages_messages.messages.get(i3).action;
                if (messageAction != null && (photo = messageAction.photo) != null) {
                    tL_photos_photos.photos.add(photo);
                }
            }
            onLoaded(i, i2, tL_photos_photos);
        }

        private void onLoaded(int i, int i2, TLRPC.photos_Photos photos_photos) {
            boolean z = this.loaded;
            int i3 = 0;
            this.loading = false;
            boolean z2 = true;
            this.loaded = true;
            this.fromCache = false;
            int iMax = Math.max(photos_photos.count, photos_photos.photos.size());
            photos_photos.count = iMax;
            boolean z3 = iMax != this.photos.size() || i + i2 > this.photos.size();
            if (!z3) {
                int i4 = 0;
                while (true) {
                    if (i4 >= photos_photos.photos.size()) {
                        z2 = z3;
                        break;
                    }
                    int i5 = i + i4;
                    if (this.photos.get(i5) != null && this.photos.get(i5).id != photos_photos.photos.get(i4).id) {
                        break;
                    } else {
                        i4++;
                    }
                }
            } else {
                z2 = z3;
                break;
            }
            if (z2) {
                this.photos.clear();
                while (i3 < photos_photos.count) {
                    int i6 = i3 - i;
                    this.photos.add((i6 < 0 || i6 >= photos_photos.photos.size()) ? null : photos_photos.photos.get(i6));
                    i3++;
                }
            } else {
                while (i3 < photos_photos.photos.size()) {
                    this.photos.set(i + i3, photos_photos.photos.get(i3));
                    i3++;
                }
            }
            saveCache();
            MessagesController.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogPhotosUpdate, this);
            if (z || i != 0 || i2 >= this.photos.size() || this.photos.size() - i2 <= 80) {
                return;
            }
            load(this.photos.size() - 80, 80);
        }

        public void removePhoto(long j) {
            if (removePhotoInternal(j)) {
                saveCache();
                MessagesController.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogPhotosUpdate, this);
            }
        }

        public void moveToStart(int i) {
            if (i < 0 || i >= this.photos.size()) {
                return;
            }
            ArrayList<TLRPC.Photo> arrayList = this.photos;
            arrayList.add(0, arrayList.remove(i));
            saveCache();
            MessagesController.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogPhotosUpdate, this);
        }

        private boolean removePhotoInternal(long j) {
            int i = 0;
            boolean z = false;
            while (i < this.photos.size()) {
                TLRPC.Photo photo = this.photos.get(i);
                if (photo != null && photo.id == j) {
                    this.photos.remove(i);
                    i--;
                    z = true;
                }
                i++;
            }
            return z;
        }

        public int getCount() {
            return this.photos.size();
        }

        public void loadCache() {
            MessagesController.this.getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() throws Throwable {
                    this.f$0.lambda$loadCache$5();
                }
            });
        }

        void lambda$loadCache$4(int i, HashMap map) {
            this.photos.clear();
            this.lastLoadOffset = -1;
            this.lastLoadCount = -1;
            for (int i2 = 0; i2 < i; i2++) {
                this.photos.add(null);
            }
            for (Map.Entry entry : map.entrySet()) {
                this.photos.set(((Integer) entry.getKey()).intValue(), (TLRPC.Photo) entry.getValue());
            }
            MessagesController.this.getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogPhotosUpdate, this);
            load(0, 80);
        }

        private void saveCache() {
            MessagesController.this.getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$saveCache$6();
                }
            });
        }

        public void lambda$setParticipantBannedRole$92(final long j, Runnable runnable, final BaseFragment baseFragment, final TLRPC.TL_channels_editBanned tL_channels_editBanned, final boolean z, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setParticipantBannedRole$90(j);
                }
            }, 1000L);
            if (runnable != null) {
                AndroidUtilities.runOnUIThread(runnable);
                return;
            }
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setParticipantBannedRole$91(tL_error, baseFragment, tL_channels_editBanned, z);
            }
        });
    }

    public void lambda$setChannelSlowMode$94(final long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setChannelSlowMode$93(j);
                }
            }, 1000L);
        }
    }

    public void lambda$setBoostsToUnblockRestrictions$96(final long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            getMessagesController().processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setBoostsToUnblockRestrictions$95(j);
                }
            }, 1000L);
        }
    }

    public void lambda$setDefaultBannedRole$97(long j) {
        loadFullChat(j, 0, true);
    }

    public void lambda$setUserAdminRole$103(final long j, final Runnable runnable, final TLRPC.Chat chat, final TLRPC.User user, final ErrorDelegate errorDelegate, final BaseFragment baseFragment, final TLRPC.TL_channels_editAdmin tL_channels_editAdmin, final boolean z, final boolean z2, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setUserAdminRole$99(j, runnable);
                }
            }, 1000L);
        } else {
            if ("USER_PRIVACY_RESTRICTED".equals(tL_error.text) && !ChatObject.isCommunity(chat) && ChatObject.canUserDoAdminAction(chat, 3)) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$setUserAdminRole$100(user, chat, errorDelegate, tL_error);
                    }
                });
                return;
            }
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setUserAdminRole$101(tL_error, baseFragment, tL_channels_editAdmin, z, z2);
                }
            });
            if (errorDelegate != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        errorDelegate.run(tL_error);
                    }
                });
            }
        }
    }

    public void lambda$setUserAdminRole$105(long j, Runnable runnable) {
        loadFullChat(j, 0, true);
        if (runnable != null) {
            runnable.run();
        }
    }

    public void $r8$lambda$lGSoFzoISKHIgPPUjycTUeIHlM8(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public void getBlockedPeers(final boolean z) {
        if (!getUserConfig().isClientActivated() || this.loadingBlockedPeers) {
            return;
        }
        this.loadingBlockedPeers = true;
        final TLRPC.TL_contacts_getBlocked tL_contacts_getBlocked = new TLRPC.TL_contacts_getBlocked();
        tL_contacts_getBlocked.offset = z ? 0 : this.blockePeers.size();
        tL_contacts_getBlocked.limit = z ? 20 : 100;
        getConnectionsManager().sendRequest(tL_contacts_getBlocked, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$getBlockedPeers$113(z, tL_contacts_getBlocked, tLObject, tL_error);
            }
        });
    }

    public void lambda$getBlockedPeers$112(TLObject tLObject, boolean z, TLRPC.TL_contacts_getBlocked tL_contacts_getBlocked) {
        if (tLObject != null) {
            TLRPC.contacts_Blocked contacts_blocked = (TLRPC.contacts_Blocked) tLObject;
            putUsers(contacts_blocked.users, false);
            putChats(contacts_blocked.chats, false);
            getMessagesStorage().putUsersAndChats(contacts_blocked.users, contacts_blocked.chats, true, true);
            if (z) {
                this.blockePeers.clear();
            }
            this.totalBlockedCount = Math.max(contacts_blocked.count, contacts_blocked.blocked.size());
            this.blockedEndReached = contacts_blocked.blocked.size() < tL_contacts_getBlocked.limit;
            int size = contacts_blocked.blocked.size();
            for (int i = 0; i < size; i++) {
                this.blockePeers.put(MessageObject.getPeerId(contacts_blocked.blocked.get(i).peer_id), 1);
            }
            this.loadingBlockedPeers = false;
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        }
    }

    public void deleteUserPhoto(TLRPC.InputPhoto inputPhoto) {
        TLRPC.Photo photo;
        final long clientUserId = getUserConfig().getClientUserId();
        if (inputPhoto == null) {
            DialogPhotos dialogPhotos = getDialogPhotos(clientUserId);
            if (dialogPhotos != null && dialogPhotos.photos.size() > 0 && (photo = dialogPhotos.photos.get(0)) != null) {
                dialogPhotos.removePhoto(photo.id);
            }
            TLRPC.TL_photos_updateProfilePhoto tL_photos_updateProfilePhoto = new TLRPC.TL_photos_updateProfilePhoto();
            tL_photos_updateProfilePhoto.id = new TLRPC.TL_inputPhotoEmpty();
            TLRPC.User user = getUser(Long.valueOf(getUserConfig().getClientUserId()));
            if (user == null) {
                user = getUserConfig().getCurrentUser();
            }
            if (user == null) {
                return;
            }
            if (user.photo != null) {
                getMessagesStorage().clearUserPhoto(user.id, user.photo.photo_id);
            }
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_ALL));
            getConnectionsManager().sendRequest(tL_photos_updateProfilePhoto, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$deleteUserPhoto$115(clientUserId, tLObject, tL_error);
                }
            });
            return;
        }
        TLRPC.TL_photos_deletePhotos tL_photos_deletePhotos = new TLRPC.TL_photos_deletePhotos();
        tL_photos_deletePhotos.id.add(inputPhoto);
        getDialogPhotos(clientUserId).removePhoto(inputPhoto.id);
        getConnectionsManager().sendRequest(tL_photos_deletePhotos, null);
    }

    public void lambda$deleteUserPhoto$114(TLObject tLObject, long j) {
        TLRPC.TL_photos_photo tL_photos_photo = (TLRPC.TL_photos_photo) tLObject;
        TLRPC.User user = getUser(Long.valueOf(getUserConfig().getClientUserId()));
        if (user == null) {
            user = getUserConfig().getCurrentUser();
            putUser(user, false);
        } else {
            getUserConfig().setCurrentUser(user);
        }
        if (user == null) {
            return;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(user);
        getMessagesStorage().putUsersAndChats(arrayList, null, false, true);
        if (tL_photos_photo.photo instanceof TLRPC.TL_photo) {
            TLRPC.TL_userProfilePhoto tL_userProfilePhoto = new TLRPC.TL_userProfilePhoto();
            user.photo = tL_userProfilePhoto;
            tL_userProfilePhoto.has_video = !tL_photos_photo.photo.video_sizes.isEmpty();
            TLRPC.UserProfilePhoto userProfilePhoto = user.photo;
            TLRPC.Photo photo = tL_photos_photo.photo;
            userProfilePhoto.photo_id = photo.id;
            userProfilePhoto.photo_small = FileLoader.getClosestPhotoSizeWithSize(photo.sizes, 150).location;
            user.photo.photo_big = FileLoader.getClosestPhotoSizeWithSize(tL_photos_photo.photo.sizes, 800).location;
            user.photo.dc_id = tL_photos_photo.photo.dc_id;
        } else {
            user.photo = new TLRPC.TL_userProfilePhotoEmpty();
        }
        TLRPC.UserFull userFull = getUserFull(j);
        if (userFull != null) {
            userFull.profile_photo = tL_photos_photo.photo;
            getMessagesStorage().updateUserInfo(userFull, false);
        }
        getUserConfig().getCurrentUser().photo = user.photo;
        putUser(user, false);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
        NotificationCenter notificationCenter = getNotificationCenter();
        int i = NotificationCenter.updateInterfaces;
        notificationCenter.lambda$postNotificationNameOnUIThread$1(i, Integer.valueOf(UPDATE_MASK_ALL));
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(i, Integer.valueOf(UPDATE_MASK_AVATAR));
        getUserConfig().saveConfig(true);
    }

    public void uploadAndApplyUserAvatar(TLRPC.FileLocation fileLocation) {
        if (fileLocation == null) {
            return;
        }
        this.uploadingAvatar = FileLoader.getDirectory(4) + "/" + fileLocation.volume_id + "_" + fileLocation.local_id + ".jpg";
        getFileLoader().uploadFile(this.uploadingAvatar, false, true, 16777216);
    }

    public void saveTheme(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent, boolean z, boolean z2) {
        TLRPC.TL_theme tL_theme = themeAccent != null ? themeAccent.info : themeInfo.info;
        if (tL_theme != null) {
            TL_account.saveTheme savetheme = new TL_account.saveTheme();
            TLRPC.TL_inputTheme tL_inputTheme = new TLRPC.TL_inputTheme();
            tL_inputTheme.id = tL_theme.id;
            tL_inputTheme.access_hash = tL_theme.access_hash;
            savetheme.theme = tL_inputTheme;
            savetheme.unsave = z2;
            getConnectionsManager().sendRequest(savetheme, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.$r8$lambda$NndgRHEFW3bbic5nhBZOPe4LOVw(tLObject, tL_error);
                }
            });
            getConnectionsManager().resumeNetworkMaybe();
        }
        if (z2) {
            return;
        }
        installTheme(themeInfo, themeAccent, z);
    }

    public void installTheme(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent, boolean z) {
        TLRPC.TL_theme tL_theme = themeAccent != null ? themeAccent.info : themeInfo.info;
        String str = themeAccent != null ? themeAccent.patternSlug : themeInfo.slug;
        boolean z2 = themeAccent == null && themeInfo.isBlured;
        boolean z3 = themeAccent != null ? themeAccent.patternMotion : themeInfo.isMotion;
        TL_account.installTheme installtheme = new TL_account.installTheme();
        installtheme.dark = z;
        if (tL_theme != null) {
            installtheme.format = "android";
            TLRPC.TL_inputTheme tL_inputTheme = new TLRPC.TL_inputTheme();
            tL_inputTheme.id = tL_theme.id;
            tL_inputTheme.access_hash = tL_theme.access_hash;
            installtheme.theme = tL_inputTheme;
            installtheme.flags |= 2;
        }
        getConnectionsManager().sendRequest(installtheme, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.m4857$r8$lambda$ztpka7uPSvIGLX8dVX34VFSHhI(tLObject, tL_error);
            }
        });
        if (TextUtils.isEmpty(str)) {
            return;
        }
        TL_account.installWallPaper installwallpaper = new TL_account.installWallPaper();
        TLRPC.TL_inputWallPaperSlug tL_inputWallPaperSlug = new TLRPC.TL_inputWallPaperSlug();
        tL_inputWallPaperSlug.slug = str;
        installwallpaper.wallpaper = tL_inputWallPaperSlug;
        TLRPC.TL_wallPaperSettings tL_wallPaperSettings = new TLRPC.TL_wallPaperSettings();
        installwallpaper.settings = tL_wallPaperSettings;
        tL_wallPaperSettings.blur = z2;
        tL_wallPaperSettings.motion = z3;
        getConnectionsManager().sendRequest(installwallpaper, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.m4715$r8$lambda$DSvROXaS7fupWjTUxFgGgWfKsY(tLObject, tL_error);
            }
        });
    }

    public void saveThemeToServer(final Theme.ThemeInfo themeInfo, final Theme.ThemeAccent themeAccent) {
        String absolutePath;
        File pathToWallpaper;
        if (themeInfo == null) {
            return;
        }
        if (themeAccent != null) {
            absolutePath = themeAccent.saveToFile().getAbsolutePath();
            pathToWallpaper = themeAccent.getPathToWallpaper();
        } else {
            absolutePath = themeInfo.pathToFile;
            pathToWallpaper = null;
        }
        final String str = absolutePath;
        final File file = pathToWallpaper;
        if (str == null || this.uploadingThemes.containsKey(str)) {
            return;
        }
        this.uploadingThemes.put(str, themeAccent != null ? themeAccent : themeInfo);
        Utilities.globalQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$saveThemeToServer$120(str, file, themeAccent, themeInfo);
            }
        });
    }

    public void lambda$saveThemeToServer$119(String str, String str2, Theme.ThemeAccent themeAccent, Theme.ThemeInfo themeInfo) {
        HashMap<String, Object> map = this.uploadingThemes;
        if (str == null) {
            map.remove(str2);
            return;
        }
        map.put(str, themeAccent != null ? themeAccent : themeInfo);
        if (themeAccent == null) {
            themeInfo.uploadingFile = str2;
            themeInfo.uploadingThumb = str;
        } else {
            themeAccent.uploadingFile = str2;
            themeAccent.uploadingThumb = str;
        }
        getFileLoader().uploadFile(str2, false, true, 67108864);
        getFileLoader().uploadFile(str, false, true, 16777216);
    }

    public void saveWallpaperToServer(File file, Theme.OverrideWallpaperInfo overrideWallpaperInfo, boolean z, final long j) {
        TLObject tLObject;
        NativeByteBuffer nativeByteBuffer;
        TLRPC.WallPaper tL_wallPaper;
        NativeByteBuffer nativeByteBuffer2 = null;
        if (this.uploadingWallpaper != null) {
            File file2 = new File(ApplicationLoader.getFilesDirFixed(), overrideWallpaperInfo.originalFileName);
            if (file != null && (file.getAbsolutePath().equals(this.uploadingWallpaper) || file.equals(file2))) {
                this.uploadingWallpaperInfo = overrideWallpaperInfo;
                return;
            } else {
                getFileLoader().cancelFileUpload(this.uploadingWallpaper, false);
                this.uploadingWallpaper = null;
                this.uploadingWallpaperInfo = null;
            }
        }
        if (file != null) {
            this.uploadingWallpaper = file.getAbsolutePath();
            this.uploadingWallpaperInfo = overrideWallpaperInfo;
            getFileLoader().uploadFile(this.uploadingWallpaper, false, true, 16777216);
        } else if (!overrideWallpaperInfo.isDefault() && !overrideWallpaperInfo.isColor() && overrideWallpaperInfo.wallpaperId > 0 && !overrideWallpaperInfo.isTheme()) {
            TLRPC.InputWallPaper inputWallpaper = getInputWallpaper(overrideWallpaperInfo);
            TLRPC.TL_wallPaperSettings wallpaperSetting = getWallpaperSetting(overrideWallpaperInfo);
            if (z) {
                TL_account.installWallPaper installwallpaper = new TL_account.installWallPaper();
                installwallpaper.wallpaper = inputWallpaper;
                installwallpaper.settings = wallpaperSetting;
                tLObject = installwallpaper;
            } else {
                TL_account.saveWallPaper savewallpaper = new TL_account.saveWallPaper();
                savewallpaper.wallpaper = inputWallpaper;
                savewallpaper.settings = wallpaperSetting;
                tLObject = savewallpaper;
            }
            if (j == 0) {
                try {
                    nativeByteBuffer = new NativeByteBuffer(1024);
                    try {
                        nativeByteBuffer.writeInt32(21);
                        nativeByteBuffer.writeBool(overrideWallpaperInfo.isBlurred);
                        nativeByteBuffer.writeBool(overrideWallpaperInfo.isMotion);
                        nativeByteBuffer.writeInt32(overrideWallpaperInfo.color);
                        nativeByteBuffer.writeInt32(overrideWallpaperInfo.gradientColor1);
                        nativeByteBuffer.writeInt32(overrideWallpaperInfo.rotation);
                        nativeByteBuffer.writeDouble(overrideWallpaperInfo.intensity);
                        nativeByteBuffer.writeBool(z);
                        nativeByteBuffer.writeString(overrideWallpaperInfo.slug);
                        nativeByteBuffer.writeString(overrideWallpaperInfo.originalFileName);
                        nativeByteBuffer.limit(nativeByteBuffer.position());
                    } catch (Exception e) {
                        e = e;
                        nativeByteBuffer2 = nativeByteBuffer;
                        FileLog.e(e);
                        nativeByteBuffer = nativeByteBuffer2;
                    }
                } catch (Exception e2) {
                    e = e2;
                }
                j = getMessagesStorage().createPendingTask(nativeByteBuffer);
            }
            getConnectionsManager().sendRequest(tLObject, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$saveWallpaperToServer$121(j, tLObject2, tL_error);
                }
            });
        }
        if ((overrideWallpaperInfo.isColor() || overrideWallpaperInfo.gradientColor2 != 0) && overrideWallpaperInfo.wallpaperId <= 0) {
            if (overrideWallpaperInfo.isColor()) {
                tL_wallPaper = new TLRPC.TL_wallPaperNoFile();
            } else {
                tL_wallPaper = new TLRPC.TL_wallPaper();
                tL_wallPaper.slug = overrideWallpaperInfo.slug;
                tL_wallPaper.document = new TLRPC.TL_documentEmpty();
            }
            long j2 = overrideWallpaperInfo.wallpaperId;
            if (j2 == 0) {
                long jNextLong = Utilities.random.nextLong();
                tL_wallPaper.id = jNextLong;
                if (jNextLong > 0) {
                    tL_wallPaper.id = -jNextLong;
                }
            } else {
                tL_wallPaper.id = j2;
            }
            tL_wallPaper.dark = MotionBackgroundDrawable.isDark(overrideWallpaperInfo.color, overrideWallpaperInfo.gradientColor1, overrideWallpaperInfo.gradientColor2, overrideWallpaperInfo.gradientColor3);
            tL_wallPaper.flags |= 4;
            TLRPC.TL_wallPaperSettings tL_wallPaperSettings = new TLRPC.TL_wallPaperSettings();
            tL_wallPaper.settings = tL_wallPaperSettings;
            tL_wallPaperSettings.blur = overrideWallpaperInfo.isBlurred;
            tL_wallPaperSettings.motion = overrideWallpaperInfo.isMotion;
            int i = overrideWallpaperInfo.color;
            if (i != 0) {
                tL_wallPaperSettings.background_color = i;
                int i2 = tL_wallPaperSettings.flags;
                tL_wallPaperSettings.intensity = (int) (overrideWallpaperInfo.intensity * 100.0f);
                tL_wallPaperSettings.flags = i2 | 9;
            }
            int i3 = overrideWallpaperInfo.gradientColor1;
            if (i3 != 0) {
                tL_wallPaperSettings.second_background_color = i3;
                tL_wallPaperSettings.rotation = AndroidUtilities.getWallpaperRotation(overrideWallpaperInfo.rotation, true);
                tL_wallPaper.settings.flags |= 16;
            }
            int i4 = overrideWallpaperInfo.gradientColor2;
            if (i4 != 0) {
                TLRPC.WallPaperSettings wallPaperSettings = tL_wallPaper.settings;
                wallPaperSettings.third_background_color = i4;
                wallPaperSettings.flags |= 32;
            }
            int i5 = overrideWallpaperInfo.gradientColor3;
            if (i5 != 0) {
                TLRPC.WallPaperSettings wallPaperSettings2 = tL_wallPaper.settings;
                wallPaperSettings2.fourth_background_color = i5;
                wallPaperSettings2.flags |= 64;
            }
            ArrayList<TLRPC.WallPaper> arrayList = new ArrayList<>();
            arrayList.add(tL_wallPaper);
            getMessagesStorage().putWallpapers(arrayList, -3);
            getMessagesStorage().getWallpapers();
        }
    }

    public void lambda$deleteMessages$122(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void lambda$unpinAllMessages$127(TLRPC.Chat chat, TLRPC.User user, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_affectedHistory tL_messages_affectedHistory = (TLRPC.TL_messages_affectedHistory) tLObject;
            boolean zIsChannel = ChatObject.isChannel(chat);
            int i = tL_messages_affectedHistory.pts;
            if (zIsChannel) {
                processNewChannelDifferenceParams(i, tL_messages_affectedHistory.pts_count, chat.id);
            } else {
                processNewDifferenceParams(-1, i, -1, tL_messages_affectedHistory.pts_count);
            }
            new ArrayList();
            getMessagesStorage().updatePinnedMessages(chat != null ? -chat.id : user.id, null, false, 0, 0, false, null);
        }
    }

    public void pinMessage(final TLRPC.Chat chat, final TLRPC.User user, final int i, final boolean z, boolean z2, boolean z3) {
        if (chat == null && user == null) {
            return;
        }
        TLRPC.TL_messages_updatePinnedMessage tL_messages_updatePinnedMessage = new TLRPC.TL_messages_updatePinnedMessage();
        tL_messages_updatePinnedMessage.peer = getInputPeer(chat != null ? -chat.id : user.id);
        tL_messages_updatePinnedMessage.id = i;
        tL_messages_updatePinnedMessage.unpin = z;
        tL_messages_updatePinnedMessage.silent = !z3;
        tL_messages_updatePinnedMessage.pm_oneside = z2;
        getConnectionsManager().sendRequest(tL_messages_updatePinnedMessage, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$pinMessage$128(i, chat, user, z, tLObject, tL_error);
            }
        });
    }

    public void lambda$deleteReactionsFromMessage$130(TLRPC.Updates updates, TLRPC.TL_error tL_error) {
        if (updates != null) {
            processUpdates(updates, false);
        }
    }

    public void deleteUserChannelAllReactions(TLRPC.Chat chat, TLRPC.User user, TLRPC.Chat chat2) {
        long j;
        if (user != null) {
            j = user.id;
        } else {
            j = chat2 != null ? chat2.id : 0L;
        }
        deleteAllReactionsFrom(-chat.id, j);
    }

    public void deleteUserChannelHistory(final TLRPC.Chat chat, final TLRPC.User user, final TLRPC.Chat chat2, int i) {
        long j;
        if (user != null) {
            j = user.id;
        } else {
            j = chat2 != null ? chat2.id : 0L;
        }
        if (i == 0) {
            getMessagesStorage().deleteUserChatHistory(-chat.id, j);
        }
        TLRPC.TL_channels_deleteParticipantHistory tL_channels_deleteParticipantHistory = new TLRPC.TL_channels_deleteParticipantHistory();
        tL_channels_deleteParticipantHistory.channel = getInputChannel(chat);
        tL_channels_deleteParticipantHistory.participant = user != null ? getInputPeer(user) : getInputPeer(chat2);
        getConnectionsManager().sendRequest(tL_channels_deleteParticipantHistory, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$deleteUserChannelHistory$131(chat, user, chat2, tLObject, tL_error);
            }
        });
    }

    public void lambda$removeDialog$132(long j) {
        long j2 = -j;
        this.channelsPts.delete(j2);
        this.shortPollChannels.delete(j2);
        this.needShortPollChannels.delete(j2);
        this.shortPollOnlines.delete(j2);
        this.needShortPollOnlines.delete(j2);
    }

    private void removeDialogFromCommunityMap(TLRPC.Dialog dialog) {
        ArrayList<TLRPC.Dialog> arrayList;
        ArrayList<TLRPC.Dialog> arrayList2;
        long j = dialog.id;
        if (j > 0) {
            TLRPC.User user = getUser(Long.valueOf(j));
            if (user != null) {
                long j2 = user.linked_community_id;
                if (j2 == 0 || (arrayList2 = this.dialogsByCommunity.get(j2)) == null) {
                    return;
                }
                arrayList2.remove(dialog);
                return;
            }
            return;
        }
        TLRPC.Chat chat = getChat(Long.valueOf(-j));
        if (chat != null) {
            long j3 = chat.linked_community_id;
            if (j3 == 0 || (arrayList = this.dialogsByCommunity.get(j3)) == null) {
                return;
            }
            arrayList.remove(dialog);
        }
    }

    public void hidePromoDialog() {
        if (this.promoDialog == null) {
            return;
        }
        TLRPC.TL_help_hidePromoData tL_help_hidePromoData = new TLRPC.TL_help_hidePromoData();
        tL_help_hidePromoData.peer = getInputPeer(this.promoDialog.id);
        getConnectionsManager().sendRequest(tL_help_hidePromoData, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                MessagesController.$r8$lambda$kW36Jj6DG9zVsjaGmE5Iu7uiZBE(tLObject, tL_error);
            }
        });
        Utilities.stageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$hidePromoDialog$134();
            }
        });
        removePromoDialog();
    }

    public void lambda$setDialogHistoryTTL$135(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public void setDialogsInTransaction(boolean z) {
        this.dialogsInTransaction = z;
        if (z) {
            return;
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    void lambda$deleteDialog$136(long j, int i, boolean z, TLRPC.InputPeer inputPeer, long j2, int i2) {
        if (j == getUserConfig().getClientUserId()) {
            getSavedMessagesController().deleteAllDialogs();
        }
        deleteDialog(j, 2, i, Math.max(0, i2), z, inputPeer, j2);
        checkIfFolderEmpty(1);
    }

    public void lambda$deleteDialog$139(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public void lambda$deleteSavedDialog$142(long j, final long j2, final TLRPC.InputPeer inputPeer, int i, final int[] iArr) {
        SavedMessagesController.SavedDialog savedDialog;
        if (j == 0) {
            getMessagesStorage().deleteSavedDialog(j2);
        }
        TLRPC.TL_messages_deleteSavedHistory tL_messages_deleteSavedHistory = new TLRPC.TL_messages_deleteSavedHistory();
        tL_messages_deleteSavedHistory.peer = getInputPeer(j2);
        tL_messages_deleteSavedHistory.parent_peer = inputPeer;
        if (i == 0 && j == 0) {
            int i2 = 0;
            while (true) {
                if (i2 >= getSavedMessagesController().allDialogs.size()) {
                    savedDialog = null;
                    break;
                } else {
                    if (getSavedMessagesController().allDialogs.get(i2).dialogId == j2) {
                        savedDialog = getSavedMessagesController().allDialogs.get(i2);
                        break;
                    }
                    i2++;
                }
            }
            if (savedDialog != null) {
                iArr[0] = Math.max(iArr[0], savedDialog.top_message_id);
                getSavedMessagesController().deleteDialog(j2);
            }
            int i3 = iArr[0];
            if (i3 <= 0) {
                i3 = Integer.MAX_VALUE;
            }
            tL_messages_deleteSavedHistory.max_id = i3;
        }
        getConnectionsManager().sendRequest(tL_messages_deleteSavedHistory, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$deleteSavedDialog$141(j2, iArr, inputPeer, tLObject, tL_error);
            }
        }, 64);
    }

    public void lambda$saveGif$144(Object obj, TLRPC.TL_messages_saveGif tL_messages_saveGif, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null || !FileRefController.isFileRefError(tL_error.text)) {
            return;
        }
        getFileRefController().requestReference(obj, tL_messages_saveGif);
    }

    public void saveRecentSticker(final Object obj, TLRPC.Document document, boolean z) {
        if (obj == null || document == null) {
            return;
        }
        final TLRPC.TL_messages_saveRecentSticker tL_messages_saveRecentSticker = new TLRPC.TL_messages_saveRecentSticker();
        TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
        tL_messages_saveRecentSticker.id = tL_inputDocument;
        tL_inputDocument.id = document.id;
        tL_inputDocument.access_hash = document.access_hash;
        byte[] bArr = document.file_reference;
        tL_inputDocument.file_reference = bArr;
        if (bArr == null) {
            tL_inputDocument.file_reference = new byte[0];
        }
        tL_messages_saveRecentSticker.unsave = false;
        tL_messages_saveRecentSticker.attached = z;
        getConnectionsManager().sendRequest(tL_messages_saveRecentSticker, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$saveRecentSticker$145(obj, tL_messages_saveRecentSticker, tLObject, tL_error);
            }
        });
    }

    public void lambda$loadChannelParticipants$147(final Long l, final Utilities.Callback callback, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadChannelParticipants$146(tL_error, tLObject, l, callback);
            }
        });
    }

    public void lambda$processChatInfo$148(boolean z, long j, boolean z2, boolean z3, TLRPC.ChatFull chatFull, ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, HashMap map, int i, boolean z4) {
        if (z && j > 0 && !z2) {
            if (System.currentTimeMillis() - this.loadedFullChats.get(j, 0L) > 60000) {
                loadFullChat(j, 0, z3);
            }
        }
        if (chatFull != null) {
            if (this.fullChats.get(j) == null) {
                this.fullChats.put(j, chatFull);
                getTranslateController().updateDialogFull(-j);
            }
            putUsers(arrayList, z);
            putChats(arrayList2, z);
            if (chatFull.stickerset != null) {
                getMediaDataController().getGroupStickerSetById(chatFull.stickerset);
            }
            if (chatFull.emojiset != null) {
                getMediaDataController().getGroupStickerSetById(chatFull.emojiset);
            }
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatInfoDidLoad, chatFull, 0, Boolean.valueOf(z2), Boolean.FALSE);
        }
        if (arrayList3 != null) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pinnedInfoDidLoad, Long.valueOf(-j), arrayList3, map, Integer.valueOf(i), Boolean.valueOf(z4));
        }
    }

    public void loadUserInfo(TLRPC.User user, boolean z, int i) {
        loadUserInfo(user, z, i, 0);
    }

    public void loadUserInfo(TLRPC.User user, boolean z, int i, int i2) {
        getMessagesStorage().loadUserInfo(user, z, i, i2);
    }

    public void updateUsernameActiveness(TLObject tLObject, String str, boolean z) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        this.objectsByUsernames.remove(str);
        if (z) {
            this.objectsByUsernames.put(str.toLowerCase(), tLObject);
        }
    }

    public void processUserInfo(final TLRPC.User user, final TLRPC.UserFull userFull, final boolean z, final boolean z2, final int i, final ArrayList<Integer> arrayList, final HashMap<Integer, MessageObject> map, final int i2, final boolean z3) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$processUserInfo$149(z, user, i, z2, userFull, arrayList, map, i2, z3);
            }
        });
    }

    public void lambda$updateTimerProc$150(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            this.lastStatusUpdateTime = System.currentTimeMillis();
            this.offlineSent = false;
            this.statusSettingState = 0;
        } else {
            long j = this.lastStatusUpdateTime;
            if (j != 0) {
                this.lastStatusUpdateTime = j + 5000;
            }
        }
        this.statusRequest = 0;
    }

    public void lambda$updateTimerProc$152(TLRPC.TL_messages_messageViews tL_messages_messageViews, LongSparseArray longSparseArray, LongSparseArray longSparseArray2, LongSparseArray longSparseArray3) {
        putUsers(tL_messages_messageViews.users, false);
        putChats(tL_messages_messageViews.chats, false);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didUpdateMessagesViews, longSparseArray, longSparseArray2, longSparseArray3, Boolean.FALSE);
    }

    void lambda$updateTimerProc$155(boolean z, boolean z2, final TLRPC.TL_messageMediaPoll tL_messageMediaPoll, final MessageObject messageObject, TLObject tLObject, TLRPC.TL_error tL_error) {
        TLRPC.PollResults pollResults;
        TLRPC.Poll poll;
        if (tL_error == null) {
            TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            if (z) {
                for (int i = 0; i < updates.updates.size(); i++) {
                    TLRPC.Update update = updates.updates.get(i);
                    if ((update instanceof TL_update.TL_updateMessagePoll) && (poll = ((TL_update.TL_updateMessagePoll) update).poll) != null && !poll.closed) {
                        this.lastViewsCheckTime = System.currentTimeMillis() - 4000;
                    }
                }
            }
            processUpdates(updates, false);
            if (z2) {
                for (int i2 = 0; i2 < updates.updates.size(); i2++) {
                    TLRPC.Update update2 = updates.updates.get(i2);
                    if (update2 instanceof TL_update.TL_updateMessagePoll) {
                        final TL_update.TL_updateMessagePoll tL_updateMessagePoll = (TL_update.TL_updateMessagePoll) update2;
                        if (tL_updateMessagePoll.poll_id == tL_messageMediaPoll.poll.id && (pollResults = tL_updateMessagePoll.results) != null && pollResults.results.isEmpty() && tL_updateMessagePoll.results.total_voters > 0) {
                            AndroidUtilities.runOnUIThread(new Runnable() { 
                                @Override // java.lang.Runnable
                                public final void run() {
                                    this.f$0.lambda$updateTimerProc$154(messageObject, tL_messageMediaPoll, tL_updateMessagePoll);
                                }
                            });
                            return;
                        }
                    }
                }
            }
        }
    }

    public void lambda$updateTimerProc$158(long j, TLRPC.TL_chatOnlines tL_chatOnlines) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatOnlineCountDidLoad, Long.valueOf(j), Integer.valueOf(tL_chatOnlines.onlines));
    }

    public void lambda$checkTosUpdate$162(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.checkingTosUpdate = false;
        if (tLObject instanceof TLRPC.TL_help_termsOfServiceUpdateEmpty) {
            this.nextTosCheckTime = ((TLRPC.TL_help_termsOfServiceUpdateEmpty) tLObject).expires;
        } else if (tLObject instanceof TLRPC.TL_help_termsOfServiceUpdate) {
            final TLRPC.TL_help_termsOfServiceUpdate tL_help_termsOfServiceUpdate = (TLRPC.TL_help_termsOfServiceUpdate) tLObject;
            this.nextTosCheckTime = tL_help_termsOfServiceUpdate.expires;
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$checkTosUpdate$161(tL_help_termsOfServiceUpdate);
                }
            });
        } else {
            this.nextTosCheckTime = getConnectionsManager().getCurrentTime() + 3600;
        }
        this.notificationsPreferences.edit().putInt("nextTosCheckTime", this.nextTosCheckTime).apply();
    }

    public void lambda$checkPromoInfoInternal$167(final long j, final TLRPC.TL_help_promoData tL_help_promoData, final int i) {
        this.lastCheckPromoInfoTime = getConnectionsManager().getCurrentTime();
        TLRPC.Dialog dialog = this.promoDialog;
        if (dialog != null && j != dialog.id) {
            removePromoDialog();
        }
        this.promoDialog = this.dialogs_dict.get(j);
        this.pendingSuggestions = new HashSet(tL_help_promoData.pending_suggestions);
        this.dismissedSuggestions = new HashSet(tL_help_promoData.dismissed_suggestions);
        this.customPendingSuggestion = tL_help_promoData.custom_pending_suggestion;
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.newSuggestionsAvailable, new Object[0]);
        SharedPreferences.Editor editorEdit = this.mainPreferences.edit();
        editorEdit.putStringSet("pendingSuggestions", this.pendingSuggestions);
        editorEdit.putStringSet("dismissedSuggestions", this.dismissedSuggestions);
        editorEdit.commit();
        if (this.promoDialog != null) {
            this.checkingPromoInfo = false;
            sortDialogs(null);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
            return;
        }
        LongSparseArray longSparseArray = new LongSparseArray();
        LongSparseArray longSparseArray2 = new LongSparseArray();
        for (int i2 = 0; i2 < tL_help_promoData.users.size(); i2++) {
            TLRPC.User user = tL_help_promoData.users.get(i2);
            longSparseArray.put(user.id, user);
        }
        for (int i3 = 0; i3 < tL_help_promoData.chats.size(); i3++) {
            TLRPC.Chat chat = tL_help_promoData.chats.get(i3);
            longSparseArray2.put(chat.id, chat);
        }
        TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
        TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
        TLRPC.Peer peer = tL_help_promoData.peer;
        if (peer == null) {
            TLRPC.Dialog dialog2 = this.promoDialog;
            if (dialog2 != null) {
                long j2 = dialog2.id;
                if (j2 < 0) {
                    TLRPC.Chat chat2 = getChat(Long.valueOf(-j2));
                    if (ChatObject.isNotInChat(chat2) || chat2.restricted) {
                        removeDialog(this.promoDialog);
                    }
                } else {
                    removeDialog(dialog2);
                }
                this.promoDialog = null;
                sortDialogs(null);
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
                return;
            }
            return;
        }
        if (peer.user_id != 0) {
            TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
            tL_inputDialogPeer.peer = tL_inputPeerUser;
            long j3 = tL_help_promoData.peer.user_id;
            tL_inputPeerUser.user_id = j3;
            TLRPC.User user2 = (TLRPC.User) longSparseArray.get(j3);
            if (user2 != null) {
                tL_inputDialogPeer.peer.access_hash = user2.access_hash;
            }
        } else if (peer.chat_id != 0) {
            TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
            tL_inputDialogPeer.peer = tL_inputPeerChat;
            long j4 = tL_help_promoData.peer.chat_id;
            tL_inputPeerChat.chat_id = j4;
            TLRPC.Chat chat3 = (TLRPC.Chat) longSparseArray2.get(j4);
            if (chat3 != null) {
                tL_inputDialogPeer.peer.access_hash = chat3.access_hash;
            }
        } else {
            TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
            tL_inputDialogPeer.peer = tL_inputPeerChannel;
            long j5 = tL_help_promoData.peer.channel_id;
            tL_inputPeerChannel.channel_id = j5;
            TLRPC.Chat chat4 = (TLRPC.Chat) longSparseArray2.get(j5);
            if (chat4 != null) {
                tL_inputDialogPeer.peer.access_hash = chat4.access_hash;
            }
        }
        tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
        this.checkingPromoInfoRequestId = getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$checkPromoInfoInternal$166(i, tL_help_promoData, j, tLObject, tL_error);
            }
        });
    }

    public void lambda$checkPromoInfoInternal$164(TLRPC.TL_help_promoData tL_help_promoData, TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs, long j) {
        Integer num = 0;
        putUsers(tL_help_promoData.users, false);
        putChats(tL_help_promoData.chats, false);
        putUsers(tL_messages_peerDialogs.users, false);
        putChats(tL_messages_peerDialogs.chats, false);
        TLRPC.Dialog dialog = this.promoDialog;
        if (dialog != null) {
            long j2 = dialog.id;
            if (j2 < 0) {
                TLRPC.Chat chat = getChat(Long.valueOf(-j2));
                if (ChatObject.isNotInChat(chat) || chat.restricted) {
                    removeDialog(this.promoDialog);
                }
            } else {
                removeDialog(dialog);
            }
        }
        TLRPC.Dialog dialog2 = tL_messages_peerDialogs.dialogs.get(0);
        this.promoDialog = dialog2;
        dialog2.id = j;
        dialog2.folder_id = 0;
        if (DialogObject.isChannel(dialog2)) {
            LongSparseIntArray longSparseIntArray = this.channelsPts;
            TLRPC.Dialog dialog3 = this.promoDialog;
            longSparseIntArray.put(-dialog3.id, dialog3.pts);
        }
        Integer num2 = this.dialogs_read_inbox_max.get(Long.valueOf(this.promoDialog.id));
        if (num2 == null) {
            num2 = num;
        }
        this.dialogs_read_inbox_max.put(Long.valueOf(this.promoDialog.id), Integer.valueOf(Math.max(num2.intValue(), this.promoDialog.read_inbox_max_id)));
        Integer num3 = this.dialogs_read_outbox_max.get(Long.valueOf(this.promoDialog.id));
        this.dialogs_read_outbox_max.put(Long.valueOf(this.promoDialog.id), Integer.valueOf(Math.max((num3 != null ? num3 : 0).intValue(), this.promoDialog.read_outbox_max_id)));
        this.dialogs_dict.put(j, this.promoDialog);
        if (!tL_messages_peerDialogs.messages.isEmpty()) {
            LongSparseArray longSparseArray = new LongSparseArray();
            LongSparseArray longSparseArray2 = new LongSparseArray();
            for (int i = 0; i < tL_messages_peerDialogs.users.size(); i++) {
                TLRPC.User user = tL_messages_peerDialogs.users.get(i);
                longSparseArray.put(user.id, user);
            }
            for (int i2 = 0; i2 < tL_messages_peerDialogs.chats.size(); i2++) {
                TLRPC.Chat chat2 = tL_messages_peerDialogs.chats.get(i2);
                longSparseArray2.put(chat2.id, chat2);
            }
            MessageObject messageObject = new MessageObject(this.currentAccount, tL_messages_peerDialogs.messages.get(0), (LongSparseArray<TLRPC.User>) longSparseArray, (LongSparseArray<TLRPC.Chat>) longSparseArray2, false, true);
            ArrayList<MessageObject> arrayList = this.dialogMessage.get(j);
            if (arrayList == null) {
                arrayList = new ArrayList<>(1);
            }
            if (arrayList.size() > 0 && arrayList.get(0) != null && arrayList.get(0).hasValidGroupId() && arrayList.get(0).getGroupIdForUse() != messageObject.getGroupIdForUse()) {
                arrayList.clear();
            }
            arrayList.add(messageObject);
            this.dialogMessage.put(j, arrayList);
            TLRPC.Dialog dialog4 = this.promoDialog;
            if (dialog4.last_message_date == 0) {
                dialog4.last_message_date = messageObject.messageOwner.date;
            }
            getTranslateController().checkDialogMessage(j);
        }
        sortDialogs(null);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    public void lambda$updatePrintingStrings$169(LongSparseArray longSparseArray, LongSparseArray longSparseArray2) {
        this.printingStrings = longSparseArray;
        this.printingStringsTypes = longSparseArray2;
    }

    public void lambda$sendTyping$172(int i, long j, long j2) {
        LongSparseArray<LongSparseArray<Boolean>> longSparseArray;
        LongSparseArray<Boolean> longSparseArray2;
        if (i >= 0) {
            LongSparseArray<LongSparseArray<Boolean>>[] longSparseArrayArr = this.sendingTypings;
            if (i >= longSparseArrayArr.length || (longSparseArray = longSparseArrayArr[i]) == null || (longSparseArray2 = longSparseArray.get(j)) == null) {
                return;
            }
            longSparseArray2.remove(j2);
            if (longSparseArray2.size() == 0) {
                longSparseArray.remove(j);
            }
        }
    }

    public boolean sendTyping(long j, long j2, int i, int i2) {
        return sendTyping(j, j2, i, null, i2);
    }

    public boolean sendTyping(final long j, final long j2, final int i, String str, int i2) {
        TLRPC.Chat chat;
        if (i < 0 || i >= this.sendingTypings.length || j == 0) {
            return false;
        }
        long clientUserId = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        if (j == clientUserId) {
            return false;
        }
        if (j < 0) {
            long j3 = -j;
            if (ChatObject.getSendAsPeerId(getChat(Long.valueOf(j3)), getChatFull(j3)) != clientUserId) {
                return false;
            }
        } else {
            TLRPC.User user = getUser(Long.valueOf(j));
            if (user != null) {
                if (user.id == getUserConfig().getClientUserId()) {
                    return false;
                }
                TLRPC.UserStatus userStatus = user.status;
                if (userStatus != null && userStatus.expires != -100 && !this.onlinePrivacy.containsKey(Long.valueOf(user.id))) {
                    if (user.status.expires <= getConnectionsManager().getCurrentTime() - 30) {
                        return false;
                    }
                }
            }
        }
        LongSparseArray<LongSparseArray<Boolean>>[] longSparseArrayArr = this.sendingTypings;
        LongSparseArray<LongSparseArray<Boolean>> longSparseArray = longSparseArrayArr[i];
        if (longSparseArray == null) {
            longSparseArray = new LongSparseArray<>();
            longSparseArrayArr[i] = longSparseArray;
        }
        LongSparseArray<Boolean> longSparseArray2 = longSparseArray.get(j);
        if (longSparseArray2 == null) {
            longSparseArray2 = new LongSparseArray<>();
            longSparseArray.put(j, longSparseArray2);
        }
        if (longSparseArray2.get(j2) != null) {
            return false;
        }
        if (!DialogObject.isEncryptedDialog(j)) {
            TLRPC.TL_messages_setTyping tL_messages_setTyping = new TLRPC.TL_messages_setTyping();
            if (j2 != 0) {
                tL_messages_setTyping.top_msg_id = (int) j2;
                tL_messages_setTyping.flags |= 1;
            }
            TLRPC.InputPeer inputPeer = getInputPeer(j);
            tL_messages_setTyping.peer = inputPeer;
            if (((inputPeer instanceof TLRPC.TL_inputPeerChannel) && ((chat = getChat(Long.valueOf(inputPeer.channel_id))) == null || !chat.megagroup)) || tL_messages_setTyping.peer == null) {
                return false;
            }
            if (i == 0) {
                tL_messages_setTyping.action = new TLRPC.TL_sendMessageTypingAction();
            } else if (i == 1) {
                tL_messages_setTyping.action = new TLRPC.TL_sendMessageRecordAudioAction();
            } else if (i == 2) {
                tL_messages_setTyping.action = new TLRPC.TL_sendMessageCancelAction();
            } else if (i == 3) {
                tL_messages_setTyping.action = new TLRPC.TL_sendMessageUploadDocumentAction();
            } else if (i == 4) {
                tL_messages_setTyping.action = new TLRPC.TL_sendMessageUploadPhotoAction();
            } else if (i == 5) {
                tL_messages_setTyping.action = new TLRPC.TL_sendMessageUploadVideoAction();
            } else if (i == 6) {
                tL_messages_setTyping.action = new TLRPC.TL_sendMessageGamePlayAction();
            } else if (i == 7) {
                tL_messages_setTyping.action = new TLRPC.TL_sendMessageRecordRoundAction();
            } else if (i == 8) {
                tL_messages_setTyping.action = new TLRPC.TL_sendMessageUploadRoundAction();
            } else if (i == 9) {
                tL_messages_setTyping.action = new TLRPC.TL_sendMessageUploadAudioAction();
            } else if (i == 10) {
                tL_messages_setTyping.action = new TLRPC.TL_sendMessageChooseStickerAction();
            } else if (i == 11) {
                TLRPC.TL_sendMessageEmojiInteractionSeen tL_sendMessageEmojiInteractionSeen = new TLRPC.TL_sendMessageEmojiInteractionSeen();
                tL_sendMessageEmojiInteractionSeen.emoticon = str;
                tL_messages_setTyping.action = tL_sendMessageEmojiInteractionSeen;
            }
            longSparseArray2.put(j2, Boolean.TRUE);
            int iSendRequest = getConnectionsManager().sendRequest(tL_messages_setTyping, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$sendTyping$171(i, j, j2, tLObject, tL_error);
                }
            }, 2);
            if (i2 != 0) {
                getConnectionsManager().bindRequestToGuid(iSendRequest, i2);
            }
        } else {
            if (i != 0) {
                return false;
            }
            TLRPC.EncryptedChat encryptedChat = getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j)));
            byte[] bArr = encryptedChat.auth_key;
            if (bArr != null && bArr.length > 1 && (encryptedChat instanceof TLRPC.TL_encryptedChat)) {
                TLRPC.TL_messages_setEncryptedTyping tL_messages_setEncryptedTyping = new TLRPC.TL_messages_setEncryptedTyping();
                TLRPC.TL_inputEncryptedChat tL_inputEncryptedChat = new TLRPC.TL_inputEncryptedChat();
                tL_messages_setEncryptedTyping.peer = tL_inputEncryptedChat;
                tL_inputEncryptedChat.chat_id = encryptedChat.id;
                tL_inputEncryptedChat.access_hash = encryptedChat.access_hash;
                tL_messages_setEncryptedTyping.typing = true;
                longSparseArray2.put(j2, Boolean.TRUE);
                int iSendRequest2 = getConnectionsManager().sendRequest(tL_messages_setEncryptedTyping, new RequestDelegate() { 
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        this.f$0.lambda$sendTyping$173(i, j, j2, tLObject, tL_error);
                    }
                }, 2);
                if (i2 != 0) {
                    getConnectionsManager().bindRequestToGuid(iSendRequest2, i2);
                }
            }
        }
        return true;
    }

    public void lambda$sendTyping$173(final int i, final long j, final long j2, TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$sendTyping$172(i, j, j2);
            }
        });
    }

    public void removeDeletedMessagesFromArray(long j, ArrayList<TLRPC.Message> arrayList) {
        int i = 0;
        int i2 = this.deletedHistory.get(j, 0);
        if (i2 == 0) {
            return;
        }
        int size = arrayList.size();
        while (i < size) {
            if (arrayList.get(i).id <= i2) {
                arrayList.remove(i);
                i--;
                size--;
            }
            i++;
        }
    }

    public void loadMessages(long j, long j2, boolean z, int i, int i2, int i3, boolean z2, int i4, int i5, int i6, int i7, int i8, long j3, int i9, int i10, boolean z3) {
        loadMessages(j, j2, z, i, i2, i3, z2, i4, i5, i6, i7, i8, j3, i10, j3 != 0 ? i9 : 0, 0, 0, false, 0, z3);
    }

    public void loadMessages(long j, long j2, boolean z, int i, int i2, int i3, boolean z2, int i4, int i5, int i6, int i7, int i8, long j3, int i9, int i10, int i11, int i12, boolean z3, int i13, boolean z4) {
        loadMessagesInternal(j, j2, z, i, i2, i3, z2, i4, i5, i6, i7, i8, j3, i9, i10, i11, i12, z3, i13, true, true, z4, null, 0L);
    }

    public void loadMessagesInternal(final long j, final long j2, final boolean z, final int i, final int i2, final int i3, boolean z2, final int i4, final int i5, final int i6, final int i7, final int i8, final long j3, final int i9, final int i10, final int i11, final int i12, final boolean z3, final int i13, boolean z4, final boolean z5, final boolean z6, Timer timer, long j4) {
        int i14;
        Timer timer2;
        if (BuildVars.LOGS_ENABLED) {
            StringBuilder sb = new StringBuilder("load messages in chat ");
            sb.append(j);
            sb.append(" topic_id ");
            sb.append(j3);
            sb.append(" count ");
            sb.append(i);
            sb.append(" max_id ");
            sb.append(i2);
            sb.append(" cache ");
            sb.append(z2);
            sb.append(" mindate = ");
            i14 = i4;
            sb.append(i14);
            sb.append(" guid ");
            sb.append(i5);
            sb.append(" load_type ");
            sb.append(i6);
            sb.append(" last_message_id ");
            sb.append(i7);
            sb.append(" mode ");
            sb.append(i8);
            sb.append(" index ");
            sb.append(i9);
            sb.append(" firstUnread ");
            sb.append(i10);
            sb.append(" unread_count ");
            sb.append(i11);
            sb.append(" last_date ");
            sb.append(i12);
            sb.append(" queryFromServer ");
            sb.append(z3);
            sb.append(" isTopic ");
            sb.append(z6);
            FileLog.d(sb.toString());
        } else {
            i14 = i4;
        }
        if (BuildVars.LOGS_ENABLED && timer == null && i8 == 0) {
            timer2 = new Timer("MessageLoaderLogger dialogId=" + j + " index=" + i9 + " count=" + i);
        } else {
            timer2 = timer;
        }
        if ((j3 == 0 || z6 || i8 == 8 || i8 == 3 || i8 == 5) && i8 != 2 && (z2 || DialogObject.isEncryptedDialog(j))) {
            getMessagesStorage().getMessages(j, j2, z, i, i2, i3, i14, i5, i6, i8, j3, i9, z5, z6, timer2);
            return;
        }
        TLRPC.Chat chat = j < 0 ? getChat(Long.valueOf(-j)) : null;
        if (i8 == 5) {
            TLRPC.TL_messages_getQuickReplyMessages tL_messages_getQuickReplyMessages = new TLRPC.TL_messages_getQuickReplyMessages();
            tL_messages_getQuickReplyMessages.shortcut_id = (int) j3;
            tL_messages_getQuickReplyMessages.hash = j4;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_getQuickReplyMessages, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$loadMessagesInternal$174(j, j2, i, i2, i3, i5, i10, i7, i11, i12, i6, i8, j3, i9, z3, i13, z5, z6, tLObject, tL_error);
                }
            }), i5);
            return;
        }
        final int i15 = i6;
        if (j3 == 0) {
            if (i8 == 2) {
                return;
            }
            if (i8 == 1) {
                TLRPC.TL_messages_getScheduledHistory tL_messages_getScheduledHistory = new TLRPC.TL_messages_getScheduledHistory();
                tL_messages_getScheduledHistory.peer = getInputPeer(j);
                tL_messages_getScheduledHistory.hash = j4;
                getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_getScheduledHistory, new RequestDelegate() { 
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        this.f$0.lambda$loadMessagesInternal$179(i2, i3, j, j2, i, i5, i10, i7, i11, i12, i6, i8, j3, i9, z3, i13, z5, z6, tLObject, tL_error);
                    }
                }), i5);
                return;
            }
            if (!ChatObject.isMonoForum(chat) && z4 && ((i6 == 3 || i6 == 2) && i7 == 0)) {
                final TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
                TLRPC.InputPeer inputPeer = getInputPeer(j);
                TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
                tL_inputDialogPeer.peer = inputPeer;
                tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
                getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate() { 
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        this.f$0.lambda$loadMessagesInternal$181(j, j2, z, i, i2, i3, i4, i5, i6, j3, i9, i10, i12, z3, z5, z6, tL_messages_getPeerDialogs, tLObject, tL_error);
                    }
                });
                return;
            }
            final TLRPC.TL_messages_getHistory tL_messages_getHistory = new TLRPC.TL_messages_getHistory();
            tL_messages_getHistory.peer = getInputPeer(j);
            if (i6 == 4) {
                tL_messages_getHistory.add_offset = (-i) + 5;
            } else if (i6 == 3) {
                tL_messages_getHistory.add_offset = (-i) / 2;
            } else if (i6 == 1) {
                tL_messages_getHistory.add_offset = (-i) - 1;
            } else if (i6 == 2 && i2 != 0) {
                tL_messages_getHistory.add_offset = (-i) + 6;
            } else if (j < 0 && i2 != 0 && ChatObject.isChannel(chat)) {
                tL_messages_getHistory.add_offset = -1;
                tL_messages_getHistory.limit++;
            }
            tL_messages_getHistory.limit = i;
            tL_messages_getHistory.offset_id = i2;
            tL_messages_getHistory.offset_date = i3;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_getHistory, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$loadMessagesInternal$183(j, i, i2, i3, j2, i5, i10, i7, i11, i12, i6, i8, j3, i9, z3, i13, z5, z6, tL_messages_getHistory, tLObject, tL_error);
                }
            }), i5);
            return;
        }
        if (i8 == 3 || ChatObject.isMonoForum(chat)) {
            final TLRPC.TL_messages_getSavedHistory tL_messages_getSavedHistory = new TLRPC.TL_messages_getSavedHistory();
            tL_messages_getSavedHistory.peer = getInputPeer(j3);
            if (ChatObject.isMonoForum(chat)) {
                tL_messages_getSavedHistory.parent_peer = getInputPeer(j);
            }
            if (i15 == 4) {
                tL_messages_getSavedHistory.add_offset = (-i) + 5;
            } else if (i15 == 3) {
                tL_messages_getSavedHistory.add_offset = (-i) / 2;
            } else if (i15 == 1) {
                tL_messages_getSavedHistory.add_offset = (-i) - 1;
            } else if (i15 == 2 && i2 != 0) {
                tL_messages_getSavedHistory.add_offset = (-i) + 6;
            } else if (j < 0 && i2 != 0 && ChatObject.isChannel(chat)) {
                tL_messages_getSavedHistory.add_offset = -1;
                tL_messages_getSavedHistory.limit++;
            }
            tL_messages_getSavedHistory.limit = i;
            tL_messages_getSavedHistory.offset_id = i2;
            tL_messages_getSavedHistory.offset_date = i3;
            getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_getSavedHistory, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$loadMessagesInternal$176(j, i, i2, i3, j2, i5, i10, i7, i11, i12, i15, i8, j3, i9, z3, i13, z5, z6, tL_messages_getSavedHistory, tLObject, tL_error);
                }
            }), i5);
            return;
        }
        if (z4 && z6 && i15 == 2 && i7 == 0) {
            TLRPC.TL_forumTopic tL_forumTopicFindTopic = this.topicsController.findTopic(-j, j3);
            if (tL_forumTopicFindTopic != null) {
                loadMessagesInternal(j, j2, z, i, i2, i3, false, i4, i5, i6, tL_forumTopicFindTopic.top_message, i8, j3, i9, i10, tL_forumTopicFindTopic.unread_count, i12, z3, tL_forumTopicFindTopic.unread_mentions_count, false, z5, z6, timer2, 0L);
                return;
            }
            i15 = i6;
        }
        if (i8 != 0) {
            return;
        }
        final TLRPC.TL_messages_getReplies tL_messages_getReplies = new TLRPC.TL_messages_getReplies();
        tL_messages_getReplies.peer = getInputPeer(j);
        tL_messages_getReplies.msg_id = (int) j3;
        tL_messages_getReplies.offset_date = i3;
        if (i15 == 4) {
            tL_messages_getReplies.add_offset = (-i) + 5;
        } else if (i15 == 3) {
            tL_messages_getReplies.add_offset = (-i) / 2;
        } else if (i15 == 1) {
            tL_messages_getReplies.add_offset = (-i) - 1;
        } else if (i15 == 2 && i2 != 0) {
            tL_messages_getReplies.add_offset = (-i) + 10;
        } else if (j < 0 && i2 != 0 && ChatObject.isChannel(chat)) {
            tL_messages_getReplies.add_offset = -1;
            tL_messages_getReplies.limit++;
        }
        tL_messages_getReplies.limit = i;
        tL_messages_getReplies.offset_id = i2;
        tL_messages_getReplies.hash = j4;
        System.currentTimeMillis();
        final int i16 = i15;
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequest(tL_messages_getReplies, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$loadMessagesInternal$178(i, i2, i3, i10, i16, j, j2, i5, i7, i11, i12, i8, j3, i9, z3, i13, z5, z6, tL_messages_getReplies, tLObject, tL_error);
            }
        }), i5);
    }

    public void lambda$loadMessagesInternal$176(long j, int i, int i2, int i3, long j2, final int i4, int i5, int i6, int i7, int i8, int i9, int i10, long j3, int i11, boolean z, int i12, boolean z2, boolean z3, final TLRPC.TL_messages_getSavedHistory tL_messages_getSavedHistory, TLObject tLObject, final TLRPC.TL_error tL_error) {
        int i13;
        if (tLObject != null) {
            TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
            removeDeletedMessagesFromArray(j, messages_messages.messages);
            if (messages_messages.messages.size() > i) {
                messages_messages.messages.remove(0);
            }
            if (i3 == 0 || messages_messages.messages.isEmpty()) {
                i13 = i2;
            } else {
                ArrayList<TLRPC.Message> arrayList = messages_messages.messages;
                int i14 = arrayList.get(arrayList.size() - 1).id;
                for (int size = messages_messages.messages.size() - 1; size >= 0; size--) {
                    TLRPC.Message message = messages_messages.messages.get(size);
                    if (message.date > i3) {
                        i14 = message.id;
                        break;
                    }
                }
                i13 = i14;
            }
            processLoadedMessages(messages_messages, messages_messages.messages.size(), j, j2, i, i13, i3, false, i4, i5, i6, i7, i8, i9, false, i10, j3, i11, z, i12, z2, z3, null);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadMessagesInternal$175(i4, tL_messages_getSavedHistory, tL_error);
            }
        });
    }

    public void lambda$loadMessagesInternal$177(int i, TLRPC.TL_messages_getReplies tL_messages_getReplies, TLRPC.TL_error tL_error) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.loadingMessagesFailed, Integer.valueOf(i), tL_messages_getReplies, tL_error);
    }

    public void lambda$loadMessagesInternal$181(long j, long j2, boolean z, int i, int i2, int i3, int i4, final int i5, int i6, long j3, int i7, int i8, int i9, boolean z2, boolean z3, boolean z4, final TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            if (tL_messages_peerDialogs.dialogs.isEmpty()) {
                return;
            }
            TLRPC.Dialog dialog = tL_messages_peerDialogs.dialogs.get(0);
            if (dialog.top_message != 0) {
                TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
                tL_messages_dialogs.chats = tL_messages_peerDialogs.chats;
                tL_messages_dialogs.users = tL_messages_peerDialogs.users;
                tL_messages_dialogs.dialogs = tL_messages_peerDialogs.dialogs;
                tL_messages_dialogs.messages = tL_messages_peerDialogs.messages;
                getMessagesStorage().putDialogs(tL_messages_dialogs, 2);
            }
            loadMessagesInternal(j, j2, z, i, i2, i3, false, i4, i5, i6, dialog.top_message, 0, j3, i7, i8, dialog.unread_count, i9, z2, dialog.unread_mentions_count, false, z3, z4, null, 0L);
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadMessagesInternal$180(i5, tL_messages_getPeerDialogs, tL_error);
            }
        });
    }

    public void lambda$loadMessagesInternal$182(int i, TLRPC.TL_messages_getHistory tL_messages_getHistory, TLRPC.TL_error tL_error) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.loadingMessagesFailed, Integer.valueOf(i), tL_messages_getHistory, tL_error);
    }

    public void reloadWebPages(final long j, HashMap<String, ArrayList<MessageObject>> map, final int i) {
        HashMap<String, ArrayList<MessageObject>> map2;
        LongSparseArray<ArrayList<MessageObject>> longSparseArray;
        boolean z = i == 1;
        boolean z2 = i == 3;
        if (z) {
            map2 = this.reloadingScheduledWebpages;
            longSparseArray = this.reloadingScheduledWebpagesPending;
        } else if (z2) {
            map2 = this.reloadingSavedWebpages;
            longSparseArray = this.reloadingSavedWebpagesPending;
        } else {
            map2 = this.reloadingWebpages;
            longSparseArray = this.reloadingWebpagesPending;
        }
        final HashMap<String, ArrayList<MessageObject>> map3 = map2;
        final LongSparseArray<ArrayList<MessageObject>> longSparseArray2 = longSparseArray;
        for (Map.Entry<String, ArrayList<MessageObject>> entry : map.entrySet()) {
            final String key = entry.getKey();
            ArrayList<MessageObject> value = entry.getValue();
            ArrayList<MessageObject> arrayList = map3.get(key);
            if (arrayList == null) {
                arrayList = new ArrayList<>();
                map3.put(key, arrayList);
            }
            arrayList.addAll(value);
            TL_account.getWebPagePreview getwebpagepreview = new TL_account.getWebPagePreview();
            getwebpagepreview.message = key;
            getConnectionsManager().sendRequest(getwebpagepreview, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$reloadWebPages$185(map3, key, longSparseArray2, j, i, tLObject, tL_error);
                }
            });
        }
    }

    public public void lambda$processLoadedMessages$186(long j, long j2, int i, int i2, boolean z, int i3, int i4, int i5, int i6, int i7, int i8, long j3, int i9, int i10, int i11, int i12, boolean z2, boolean z3, Timer timer, long j4) {
        loadMessagesInternal(j, j2, false, i, (i2 == 2 && z) ? i3 : i4, i5, false, 0, i6, i2, i7, i8, j3, i9, i3, i10, i11, z, i12, true, z2, z3, timer, j4);
    }

    public int $r8$lambda$Rp4CdnuKkxa3D3oXjjhZBW3Tneo(MessageObject messageObject, MessageObject messageObject2) {
        int id;
        int id2;
        if (messageObject.messageOwner.date == messageObject2.messageOwner.date && messageObject.getId() >= 0 && messageObject2.getId() >= 0) {
            id = messageObject2.getId();
            id2 = messageObject.getId();
        } else {
            id = messageObject2.messageOwner.date;
            id2 = messageObject.messageOwner.date;
        }
        return id - id2;
    }

    public static void lambda$processLoadedMessages$190(Timer.Task task, boolean z, int i, int i2, boolean z2, boolean z3, int i3, long j, int i4, ArrayList arrayList, int i5, int i6, int i7, int i8, int i9, int i10, int i11, int i12) {
        Timer.done(task);
        if (!z) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDidLoadWithoutProcess, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z2), Boolean.valueOf(z3), Integer.valueOf(i3));
        } else {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDidLoad, Long.valueOf(j), Integer.valueOf(i4), arrayList, Boolean.valueOf(z2), Integer.valueOf(i5), Integer.valueOf(i3), Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i8), Boolean.valueOf(z3), Integer.valueOf(i), Integer.valueOf(i9), Integer.valueOf(i10), Integer.valueOf(i11), Integer.valueOf(i12));
        }
    }

    public void forceNoReload(long j, int i) {
        if (i == 1) {
            this.lastScheduledServerQueryTime.put(j, Long.valueOf(SystemClock.elapsedRealtime()));
        } else if (i == 0) {
            this.lastServerQueryTime.put(j, Long.valueOf(SystemClock.elapsedRealtime()));
        }
    }

    public void loadHintDialogs() {
        if (!this.hintDialogs.isEmpty() || TextUtils.isEmpty(this.installReferer)) {
            return;
        }
        TLRPC.TL_help_getRecentMeUrls tL_help_getRecentMeUrls = new TLRPC.TL_help_getRecentMeUrls();
        tL_help_getRecentMeUrls.referer = this.installReferer;
        getConnectionsManager().sendRequest(tL_help_getRecentMeUrls, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$loadHintDialogs$193(tLObject, tL_error);
            }
        });
    }

    public void lambda$loadHintDialogs$192(TLObject tLObject) {
        TLRPC.TL_help_recentMeUrls tL_help_recentMeUrls = (TLRPC.TL_help_recentMeUrls) tLObject;
        putUsers(tL_help_recentMeUrls.users, false);
        putChats(tL_help_recentMeUrls.chats, false);
        this.hintDialogs.clear();
        this.hintDialogs.addAll(tL_help_recentMeUrls.urls);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public TLRPC.TL_dialogFolder ensureFolderDialogExists(int i, boolean[] zArr) {
        if (i == 0) {
            return null;
        }
        long jMakeFolderDialogId = DialogObject.makeFolderDialogId(i);
        TLRPC.Dialog dialog = this.dialogs_dict.get(jMakeFolderDialogId);
        if (dialog instanceof TLRPC.TL_dialogFolder) {
            if (zArr != null) {
                zArr[0] = false;
            }
            return (TLRPC.TL_dialogFolder) dialog;
        }
        if (zArr != null) {
            zArr[0] = true;
        }
        TLRPC.TL_dialogFolder tL_dialogFolder = new TLRPC.TL_dialogFolder();
        tL_dialogFolder.id = jMakeFolderDialogId;
        tL_dialogFolder.peer = new TLRPC.TL_peerUser();
        TLRPC.TL_folder tL_folder = new TLRPC.TL_folder();
        tL_dialogFolder.folder = tL_folder;
        tL_folder.id = i;
        tL_folder.title = LocaleController.getString(R.string.ArchivedChats);
        tL_dialogFolder.pinned = true;
        int iMax = 0;
        for (int i2 = 0; i2 < this.allDialogs.size(); i2++) {
            TLRPC.Dialog dialog2 = this.allDialogs.get(i2);
            if (!dialog2.pinned) {
                if (dialog2.id != this.promoDialogId) {
                    break;
                }
            } else {
                iMax = Math.max(dialog2.pinnedNum, iMax);
            }
        }
        tL_dialogFolder.pinnedNum = iMax + 1;
        TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
        tL_messages_dialogs.dialogs.add(tL_dialogFolder);
        getMessagesStorage().putDialogs(tL_messages_dialogs, 1);
        this.dialogs_dict.put(jMakeFolderDialogId, tL_dialogFolder);
        this.allDialogs.add(0, tL_dialogFolder);
        return tL_dialogFolder;
    }

    private void removeFolder(int i) {
        long jMakeFolderDialogId = DialogObject.makeFolderDialogId(i);
        TLRPC.Dialog dialog = this.dialogs_dict.get(jMakeFolderDialogId);
        if (dialog == null) {
            return;
        }
        this.dialogs_dict.remove(jMakeFolderDialogId);
        this.allDialogs.remove(dialog);
        sortDialogs(null);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.folderBecomeEmpty, Integer.valueOf(i));
    }

    public void onFolderEmpty(final int i) {
        if (getUserConfig().getDialogLoadOffsets(i)[0] != 2147483647L) {
            loadDialogs(i, 0, 10, false, new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onFolderEmpty$194(i);
                }
            });
        } else if (i == 1) {
            this.hasArchivedChats = false;
            checkArchiveFolder();
        } else {
            removeFolder(i);
        }
    }

    public void lambda$addDialogToFolder$195(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void loadDialogs(int i, int i2, int i3, boolean z) {
        loadDialogs(i, i2, i3, z, null);
    }

    void lambda$loadDialogs$196(int i, int i2, Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.messages_Dialogs messages_dialogs = (TLRPC.messages_Dialogs) tLObject;
            processLoadedDialogs(messages_dialogs, null, null, i, 0, i2, 0, false, false, false);
            if (runnable == null || !messages_dialogs.dialogs.isEmpty()) {
                return;
            }
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public void loadGlobalNotificationsSettings() {
        SharedPreferences.Editor editorEdit;
        if (this.loadingNotificationSettings == 0 && !getUserConfig().notificationsSettingsLoaded) {
            SharedPreferences notificationsSettings = getNotificationsSettings(this.currentAccount);
            if (notificationsSettings.contains("EnableGroup")) {
                boolean z = notificationsSettings.getBoolean("EnableGroup", true);
                editorEdit = notificationsSettings.edit();
                if (!z) {
                    editorEdit.putInt("EnableGroup2", Integer.MAX_VALUE);
                    editorEdit.putInt("EnableChannel2", Integer.MAX_VALUE);
                }
                editorEdit.remove("EnableGroup").apply();
            } else {
                editorEdit = null;
            }
            if (notificationsSettings.contains("EnableAll")) {
                boolean z2 = notificationsSettings.getBoolean("EnableAll", true);
                if (editorEdit == null) {
                    editorEdit = notificationsSettings.edit();
                }
                if (!z2) {
                    editorEdit.putInt("EnableAll2", Integer.MAX_VALUE);
                }
                editorEdit.remove("EnableAll").apply();
            }
            if (editorEdit != null) {
                editorEdit.apply();
            }
            this.loadingNotificationSettings = 4;
            for (final int i = 0; i < 3; i++) {
                TL_account.getNotifySettings getnotifysettings = new TL_account.getNotifySettings();
                if (i == 0) {
                    getnotifysettings.peer = new TLRPC.TL_inputNotifyChats();
                } else if (i == 1) {
                    getnotifysettings.peer = new TLRPC.TL_inputNotifyUsers();
                } else {
                    getnotifysettings.peer = new TLRPC.TL_inputNotifyBroadcasts();
                }
                getConnectionsManager().sendRequest(getnotifysettings, new RequestDelegate() { 
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        this.f$0.lambda$loadGlobalNotificationsSettings$198(i, tLObject, tL_error);
                    }
                });
            }
            getConnectionsManager().sendRequest(new TL_account.getReactionsNotifySettings(), new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$loadGlobalNotificationsSettings$200(tLObject, tL_error);
                }
            });
        }
        if (getUserConfig().notificationsSignUpSettingsLoaded) {
            return;
        }
        loadSignUpNotificationsSettings();
    }

    public void lambda$loadGlobalNotificationsSettings$197(TLObject tLObject, int i) {
        if (tLObject != null) {
            this.loadingNotificationSettings--;
            TLRPC.TL_peerNotifySettings tL_peerNotifySettings = (TLRPC.TL_peerNotifySettings) tLObject;
            SharedPreferences.Editor editorEdit = this.notificationsPreferences.edit();
            if (i == 0) {
                if ((tL_peerNotifySettings.flags & 1) != 0) {
                    editorEdit.putBoolean("EnablePreviewGroup", tL_peerNotifySettings.show_previews);
                }
                if ((tL_peerNotifySettings.flags & 4) != 0) {
                    editorEdit.putInt("EnableGroup2", tL_peerNotifySettings.mute_until);
                }
            } else {
                int i2 = tL_peerNotifySettings.flags;
                if (i == 1) {
                    if ((i2 & 1) != 0) {
                        editorEdit.putBoolean("EnablePreviewAll", tL_peerNotifySettings.show_previews);
                    }
                    if ((tL_peerNotifySettings.flags & 64) != 0) {
                        editorEdit.putBoolean("EnableAllStories", !tL_peerNotifySettings.stories_muted);
                    }
                    if ((tL_peerNotifySettings.flags & 128) != 0) {
                        editorEdit.putBoolean("EnableHideStoriesSenders", tL_peerNotifySettings.stories_hide_sender);
                    }
                    if ((tL_peerNotifySettings.flags & 4) != 0) {
                        editorEdit.putInt("EnableAll2", tL_peerNotifySettings.mute_until);
                    }
                    if ((tL_peerNotifySettings.flags & 64) != 0) {
                        editorEdit.putBoolean("EnableAllStories", !tL_peerNotifySettings.stories_muted);
                    }
                } else {
                    if ((i2 & 1) != 0) {
                        editorEdit.putBoolean("EnablePreviewChannel", tL_peerNotifySettings.show_previews);
                    }
                    if ((tL_peerNotifySettings.flags & 4) != 0) {
                        editorEdit.putInt("EnableChannel2", tL_peerNotifySettings.mute_until);
                    }
                }
            }
            getNotificationsController().getNotificationsSettingsFacade().applySoundSettings(tL_peerNotifySettings.android_sound, editorEdit, 0L, 0L, i, false);
            editorEdit.apply();
            if (this.loadingNotificationSettings == 0) {
                getUserConfig().notificationsSettingsLoaded = true;
                getUserConfig().saveConfig(false);
            }
        }
    }

    public void lambda$loadGlobalNotificationsSettings$199(TLObject tLObject) {
        this.loadingNotificationSettings--;
        if (tLObject instanceof TL_account.TL_reactionsNotifySettings) {
            TL_account.TL_reactionsNotifySettings tL_reactionsNotifySettings = (TL_account.TL_reactionsNotifySettings) tLObject;
            SharedPreferences.Editor editorEdit = this.notificationsPreferences.edit();
            editorEdit.putBoolean("EnableReactionsMessages", tL_reactionsNotifySettings.messages_notify_from != null);
            TL_account.ReactionNotificationsFrom reactionNotificationsFrom = tL_reactionsNotifySettings.messages_notify_from;
            if (reactionNotificationsFrom != null) {
                editorEdit.putBoolean("EnableReactionsMessagesContacts", reactionNotificationsFrom instanceof TL_account.TL_reactionNotificationsFromContacts);
            }
            editorEdit.putBoolean("EnableReactionsStories", tL_reactionsNotifySettings.stories_notify_from != null);
            TL_account.ReactionNotificationsFrom reactionNotificationsFrom2 = tL_reactionsNotifySettings.stories_notify_from;
            if (reactionNotificationsFrom2 != null) {
                editorEdit.putBoolean("EnableReactionsStoriesContacts", reactionNotificationsFrom2 instanceof TL_account.TL_reactionNotificationsFromContacts);
            }
            editorEdit.putBoolean("EnableReactionsPreview", tL_reactionsNotifySettings.show_previews);
            getNotificationsController().getNotificationsSettingsFacade().applySoundSettings(tL_reactionsNotifySettings.sound, editorEdit, 0L, 0L, 4, false);
            editorEdit.apply();
        }
        if (this.loadingNotificationSettings == 0) {
            getUserConfig().notificationsSettingsLoaded = true;
            getUserConfig().saveConfig(false);
        }
    }

    public void lambda$reloadReactionsNotifySettings$201(TLObject tLObject) {
        if (tLObject instanceof TL_account.TL_reactionsNotifySettings) {
            TL_account.TL_reactionsNotifySettings tL_reactionsNotifySettings = (TL_account.TL_reactionsNotifySettings) tLObject;
            SharedPreferences.Editor editorEdit = this.notificationsPreferences.edit();
            editorEdit.putBoolean("EnableReactionsMessages", tL_reactionsNotifySettings.messages_notify_from != null);
            TL_account.ReactionNotificationsFrom reactionNotificationsFrom = tL_reactionsNotifySettings.messages_notify_from;
            if (reactionNotificationsFrom != null) {
                editorEdit.putBoolean("EnableReactionsMessagesContacts", reactionNotificationsFrom instanceof TL_account.TL_reactionNotificationsFromContacts);
            }
            editorEdit.putBoolean("EnableReactionsStories", tL_reactionsNotifySettings.stories_notify_from != null);
            TL_account.ReactionNotificationsFrom reactionNotificationsFrom2 = tL_reactionsNotifySettings.stories_notify_from;
            if (reactionNotificationsFrom2 != null) {
                editorEdit.putBoolean("EnableReactionsStoriesContacts", reactionNotificationsFrom2 instanceof TL_account.TL_reactionNotificationsFromContacts);
            }
            editorEdit.putBoolean("EnableReactionsPreview", tL_reactionsNotifySettings.show_previews);
            getNotificationsController().getNotificationsSettingsFacade().applySoundSettings(tL_reactionsNotifySettings.sound, editorEdit, 0L, 0L, 4, false);
            editorEdit.apply();
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.notificationsSettingsUpdated, new Object[0]);
        }
    }

    public void loadSignUpNotificationsSettings() {
        if (this.loadingNotificationSignUpSettings) {
            return;
        }
        this.loadingNotificationSignUpSettings = true;
        getConnectionsManager().sendRequest(new TL_account.getContactSignUpNotification(), new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$loadSignUpNotificationsSettings$204(tLObject, tL_error);
            }
        });
    }

    public void lambda$loadSignUpNotificationsSettings$203(TLObject tLObject) {
        this.loadingNotificationSignUpSettings = false;
        SharedPreferences.Editor editorEdit = this.notificationsPreferences.edit();
        boolean z = tLObject instanceof TLRPC.TL_boolFalse;
        this.enableJoined = z;
        editorEdit.putBoolean("EnableContactJoined", z);
        editorEdit.apply();
        getUserConfig().notificationsSignUpSettingsLoaded = true;
        getUserConfig().saveConfig(false);
    }

    public void forceResetDialogs() {
        resetDialogs(true, getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
        getNotificationsController().deleteAllNotificationChannels();
    }

    public void loadUnknownDialog(TLRPC.InputPeer inputPeer, long j) {
        Exception exc;
        NativeByteBuffer nativeByteBuffer;
        if (inputPeer == null) {
            return;
        }
        final long peerDialogId = DialogObject.getPeerDialogId(inputPeer);
        if (this.gettingUnknownDialogs.indexOfKey(peerDialogId) >= 0) {
            return;
        }
        this.gettingUnknownDialogs.put(peerDialogId, Boolean.TRUE);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("load unknown dialog " + peerDialogId);
        }
        TLRPC.TL_messages_getPeerDialogs tL_messages_getPeerDialogs = new TLRPC.TL_messages_getPeerDialogs();
        TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
        tL_inputDialogPeer.peer = inputPeer;
        tL_messages_getPeerDialogs.peers.add(tL_inputDialogPeer);
        if (j == 0) {
            NativeByteBuffer nativeByteBuffer2 = null;
            try {
                nativeByteBuffer = new NativeByteBuffer(inputPeer.getObjectSize() + 4);
                try {
                    nativeByteBuffer.writeInt32(15);
                    inputPeer.serializeToStream(nativeByteBuffer);
                } catch (Exception e) {
                    exc = e;
                    nativeByteBuffer2 = nativeByteBuffer;
                    FileLog.e(exc);
                    nativeByteBuffer = nativeByteBuffer2;
                }
            } catch (Exception e2) {
                exc = e2;
            }
            j = getMessagesStorage().createPendingTask(nativeByteBuffer);
        }
        final long j2 = j;
        getConnectionsManager().sendRequest(tL_messages_getPeerDialogs, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$loadUnknownDialog$205(j2, peerDialogId, tLObject, tL_error);
            }
        });
    }

    public void lambda$resetDialogs$206(int i, int i2, int i3, int i4, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            this.resetDialogsPinned = (TLRPC.TL_messages_peerDialogs) tLObject;
            for (int i5 = 0; i5 < this.resetDialogsPinned.dialogs.size(); i5++) {
                this.resetDialogsPinned.dialogs.get(i5).pinned = true;
            }
            resetDialogs(false, i, i2, i3, i4);
        }
    }

    public void lambda$completeDialogsReset$209(int i, int i2, int i3, final TLRPC.messages_Dialogs messages_dialogs, final LongSparseArray longSparseArray, final LongSparseArray longSparseArray2) {
        this.gettingDifference = false;
        getMessagesStorage().setLastPtsValue(i);
        getMessagesStorage().setLastDateValue(i2);
        getMessagesStorage().setLastQtsValue(i3);
        getDifference();
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$completeDialogsReset$208(messages_dialogs, longSparseArray, longSparseArray2);
            }
        });
    }

    public void lambda$migrateDialogs$213(final int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            final TLRPC.messages_Dialogs messages_dialogs = (TLRPC.messages_Dialogs) tLObject;
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$migrateDialogs$211(messages_dialogs, i);
                }
            });
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$migrateDialogs$212();
                }
            });
        }
    }

    void lambda$migrateDialogs$210() {
        this.migratingDialogs = false;
    }

    public void lambda$processLoadedDialogs$214(TLRPC.messages_Dialogs messages_dialogs, ArrayList arrayList, int i, boolean z, long[] jArr, int i2) {
        putUsers(messages_dialogs.users, true);
        if (arrayList != null) {
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                long j = ((TLRPC.UserFull) arrayList.get(i3)).id;
                this.fullUsers.put(j, (TLRPC.UserFull) arrayList.get(i3));
                getTranslateController().updateDialogFull(j);
            }
        }
        this.loadingDialogs.put(i, false);
        if (z) {
            this.dialogsEndReached.put(i, false);
            this.serverDialogsEndReached.put(i, false);
        } else if (jArr[0] == 2147483647L) {
            this.dialogsEndReached.put(i, true);
            this.serverDialogsEndReached.put(i, true);
        } else {
            loadDialogs(i, 0, i2, false);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    void lambda$processLoadedDialogs$215(TLRPC.Chat chat) {
        checkChatInviter(chat.id, true);
    }

    void lambda$reloadMentionsCountForChannel$218(TLRPC.InputPeer inputPeer, long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        TLRPC.messages_Messages messages_messages = (TLRPC.messages_Messages) tLObject;
        if (messages_messages != null) {
            int size = messages_messages.count;
            if (size == 0) {
                size = messages_messages.messages.size();
            }
            getMessagesStorage().resetMentionsCount(-inputPeer.channel_id, 0L, size);
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void reloadMentionsCountForChannels(final ArrayList<Long> arrayList) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$reloadMentionsCountForChannels$219(arrayList);
            }
        });
    }

    public void lambda$processDialogsUpdateRead$220(LongSparseIntArray longSparseIntArray, LongSparseIntArray longSparseIntArray2) {
        boolean z;
        if (longSparseIntArray != null) {
            z = false;
            for (int i = 0; i < longSparseIntArray.size(); i++) {
                long jKeyAt = longSparseIntArray.keyAt(i);
                TLRPC.Dialog dialog = this.dialogs_dict.get(jKeyAt);
                if (dialog == null) {
                    for (int i2 = 0; i2 < this.allDialogs.size(); i2++) {
                        if (this.allDialogs.get(i2).id == jKeyAt) {
                            this.dialogs_dict.put(jKeyAt, this.allDialogs.get(i2));
                            dialog = this.allDialogs.get(i2);
                            break;
                        }
                    }
                }
                if (dialog == null) {
                    if (BuildVars.DEBUG_PRIVATE_VERSION) {
                        FileLog.d("can't update dialog " + jKeyAt + " with new unread " + longSparseIntArray.valueAt(i));
                    }
                    this.pendingUnreadCounter.put(jKeyAt, longSparseIntArray.valueAt(i));
                }
                if (dialog != null) {
                    int i3 = dialog.unread_count;
                    dialog.unread_count = longSparseIntArray.valueAt(i);
                    if (BuildVars.DEBUG_PRIVATE_VERSION) {
                        FileLog.d("update dialog " + jKeyAt + " with new unread " + dialog.unread_count);
                    }
                    if (i3 != 0 && dialog.unread_count == 0) {
                        if (!isDialogMuted(jKeyAt, 0L)) {
                            this.unreadUnmutedDialogs--;
                        }
                        if (!z) {
                            int i4 = 0;
                            while (true) {
                                DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
                                if (i4 >= dialogFilterArr.length) {
                                    break;
                                }
                                DialogFilter dialogFilter = dialogFilterArr[i4];
                                if (dialogFilter != null && (dialogFilter.flags & DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                                    z = true;
                                    break;
                                    break;
                                }
                                i4++;
                            }
                        }
                    } else if (i3 == 0 && !dialog.unread_mark && dialog.unread_count != 0) {
                        if (!isDialogMuted(jKeyAt, 0L)) {
                            this.unreadUnmutedDialogs++;
                        }
                        if (!z) {
                            int i5 = 0;
                            while (true) {
                                DialogFilter[] dialogFilterArr2 = this.selectedDialogFilter;
                                if (i5 >= dialogFilterArr2.length) {
                                    break;
                                }
                                DialogFilter dialogFilter2 = dialogFilterArr2[i5];
                                if (dialogFilter2 != null && (dialogFilter2.flags & DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                                    z = true;
                                    break;
                                }
                                i5++;
                            }
                        }
                    }
                }
            }
        } else {
            z = false;
        }
        if (longSparseIntArray2 != null) {
            for (int i6 = 0; i6 < longSparseIntArray2.size(); i6++) {
                TLRPC.Dialog dialog2 = this.dialogs_dict.get(longSparseIntArray2.keyAt(i6));
                if (dialog2 != null) {
                    dialog2.unread_mentions_count = longSparseIntArray2.valueAt(i6);
                    if (this.createdDialogMainThreadIds.contains(Long.valueOf(dialog2.id))) {
                        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateMentionsCount, Long.valueOf(dialog2.id), 0L, Integer.valueOf(dialog2.unread_mentions_count));
                    }
                    if (!z) {
                        int i7 = 0;
                        while (true) {
                            DialogFilter[] dialogFilterArr3 = this.selectedDialogFilter;
                            if (i7 >= dialogFilterArr3.length) {
                                break;
                            }
                            DialogFilter dialogFilter3 = dialogFilterArr3[i7];
                            if (dialogFilter3 != null) {
                                int i8 = dialogFilter3.flags;
                                if ((DIALOG_FILTER_FLAG_EXCLUDE_MUTED & i8) != 0 || (i8 & DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                                    z = true;
                                    break;
                                }
                            }
                            i7++;
                        }
                    }
                }
            }
        }
        if (z) {
            sortDialogs(null);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
        if (longSparseIntArray != null) {
            getNotificationsController().processDialogsUpdateRead(longSparseIntArray);
        }
    }

    public void checkLastDialogMessage(final TLRPC.Dialog dialog, TLRPC.InputPeer inputPeer, final long j) {
        NativeByteBuffer nativeByteBuffer;
        Exception e;
        if (DialogObject.isEncryptedDialog(dialog.id) || this.checkingLastMessagesDialogs.indexOfKey(dialog.id) >= 0) {
            return;
        }
        TLRPC.TL_messages_getHistory tL_messages_getHistory = new TLRPC.TL_messages_getHistory();
        if (inputPeer == null) {
            inputPeer = getInputPeer(dialog.id);
        }
        tL_messages_getHistory.peer = inputPeer;
        if (inputPeer == null) {
            return;
        }
        tL_messages_getHistory.limit = 1;
        this.checkingLastMessagesDialogs.put(dialog.id, Boolean.TRUE);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("checkLastDialogMessage for " + dialog.id);
        }
        if (j == 0) {
            try {
                nativeByteBuffer = new NativeByteBuffer(tL_messages_getHistory.peer.getObjectSize() + 60);
                try {
                    nativeByteBuffer.writeInt32(14);
                    nativeByteBuffer.writeInt64(dialog.id);
                    nativeByteBuffer.writeInt32(dialog.top_message);
                    nativeByteBuffer.writeInt32(dialog.read_inbox_max_id);
                    nativeByteBuffer.writeInt32(dialog.read_outbox_max_id);
                    nativeByteBuffer.writeInt32(dialog.unread_count);
                    nativeByteBuffer.writeInt32(dialog.last_message_date);
                    nativeByteBuffer.writeInt32(dialog.pts);
                    nativeByteBuffer.writeInt32(dialog.flags);
                    nativeByteBuffer.writeBool(dialog.pinned);
                    nativeByteBuffer.writeInt32(dialog.pinnedNum);
                    nativeByteBuffer.writeInt32(dialog.unread_mentions_count);
                    nativeByteBuffer.writeBool(dialog.unread_mark);
                    nativeByteBuffer.writeInt32(dialog.folder_id);
                    tL_messages_getHistory.peer.serializeToStream(nativeByteBuffer);
                } catch (Exception e2) {
                    e = e2;
                    FileLog.e(e);
                }
            } catch (Exception e3) {
                nativeByteBuffer = null;
                e = e3;
            }
            j = getMessagesStorage().createPendingTask(nativeByteBuffer);
        }
        getConnectionsManager().sendRequest(tL_messages_getHistory, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$checkLastDialogMessage$224(dialog, j, tLObject, tL_error);
            }
        });
    }

    public void lambda$checkLastDialogMessage$222(final TLRPC.Dialog dialog) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("checkLastDialogMessage for " + dialog.id + " has not message");
        }
        if (getMediaDataController().getDraft(dialog.id, 0L) == null) {
            TLRPC.Dialog dialog2 = this.dialogs_dict.get(dialog.id);
            if (dialog2 == null) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("checkLastDialogMessage for " + dialog.id + " current dialog not found");
                }
                getMessagesStorage().isDialogHasTopMessage(dialog.id, new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$checkLastDialogMessage$221(dialog);
                    }
                });
                return;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("checkLastDialogMessage for " + dialog.id + " current dialog top message " + dialog2.top_message);
            }
            if (dialog2.top_message == 0) {
                deleteDialog(dialog.id, 3);
            }
        }
    }

    public throw new UnsupportedOperationException("Method not decompiled: org.telegram.messenger.MessagesController.lambda$processDialogsUpdate$225(org.telegram.tgnet.TLRPC$messages_Dialogs, androidx.collection.LongSparseArray, androidx.collection.LongSparseArray, boolean, org.telegram.messenger.support.LongSparseIntArray):void");
    }

    private int messagesMaxDate(ArrayList<MessageObject> arrayList) {
        TLRPC.Message message;
        int i;
        int i2 = Integer.MIN_VALUE;
        for (int i3 = 0; arrayList != null && i3 < arrayList.size(); i3++) {
            MessageObject messageObject = arrayList.get(i3);
            if (messageObject != null && (message = messageObject.messageOwner) != null && (i = message.date) > i2) {
                i2 = i;
            }
        }
        return i2;
    }

    public void addToViewsQueue(MessageObject messageObject) {
        if (messageObject == null) {
            return;
        }
        addToViewsQueue(messageObject.getDialogId(), messageObject.getId());
    }

    public void addToViewsQueue(final long j, final int i) {
        if (j == 0 || i <= 0) {
            return;
        }
        Utilities.stageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$addToViewsQueue$227(j, i);
            }
        });
    }

    public void lambda$markMessageContentAsRead$230(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
    }

    public void markMentionMessageAsRead(int i, long j, long j2) {
        getMessagesStorage().markMentionMessageAsRead(j2, i, j2);
        if (j != 0) {
            TLRPC.TL_channels_readMessageContents tL_channels_readMessageContents = new TLRPC.TL_channels_readMessageContents();
            TLRPC.InputChannel inputChannel = getInputChannel(j);
            tL_channels_readMessageContents.channel = inputChannel;
            if (inputChannel == null) {
                return;
            }
            tL_channels_readMessageContents.id.add(Integer.valueOf(i));
            getConnectionsManager().sendRequest(tL_channels_readMessageContents, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.$r8$lambda$kuvPOwfXA9T3WVt9pMfYwhvbUXs(tLObject, tL_error);
                }
            });
            return;
        }
        TLRPC.TL_messages_readMessageContents tL_messages_readMessageContents = new TLRPC.TL_messages_readMessageContents();
        tL_messages_readMessageContents.id.add(Integer.valueOf(i));
        getConnectionsManager().sendRequest(tL_messages_readMessageContents, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$markMentionMessageAsRead$232(tLObject, tL_error);
            }
        });
    }

    public void lambda$markMessageAsRead2$233(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public void lambda$completeReadTask$236(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null && (tLObject instanceof TLRPC.TL_messages_affectedMessages)) {
            TLRPC.TL_messages_affectedMessages tL_messages_affectedMessages = (TLRPC.TL_messages_affectedMessages) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedMessages.pts, -1, tL_messages_affectedMessages.pts_count);
        }
    }

    private void checkReadTasks() {
        long jElapsedRealtime = SystemClock.elapsedRealtime();
        int size = this.readTasks.size();
        int i = 0;
        int i2 = 0;
        while (i2 < size) {
            ReadTask readTask = this.readTasks.get(i2);
            if (readTask.sendRequestTime <= jElapsedRealtime) {
                completeReadTask(readTask);
                this.readTasks.remove(i2);
                this.readTasksMap.remove(readTask.dialogId);
                i2--;
                size--;
            }
            i2++;
        }
        int size2 = this.repliesReadTasks.size();
        while (i < size2) {
            ReadTask readTask2 = this.repliesReadTasks.get(i);
            if (readTask2.sendRequestTime <= jElapsedRealtime) {
                completeReadTask(readTask2);
                this.repliesReadTasks.remove(i);
                this.threadsReadTasksMap.remove(readTask2.dialogId + "_" + readTask2.replyId);
                i += -1;
                size2 += -1;
            }
            i++;
        }
    }

    public void markDialogAsReadNow(final long j, final long j2) {
        Utilities.stageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$markDialogAsReadNow$238(j2, j);
            }
        });
    }

    public void lambda$markDialogAsRead$240(final long j, final int i, final int i2, final boolean z) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$markDialogAsRead$239(j, i, i2, z);
            }
        });
    }

    public void lambda$markDialogAsRead$241(long j, int i, boolean z, int i2, int i3) {
        getNotificationsController().processReadMessages(null, j, i, 0, z);
        TLRPC.Dialog dialog = this.dialogs_dict.get(j);
        if (dialog != null) {
            int i4 = dialog.unread_count;
            if (i2 == 0 || i3 <= dialog.top_message) {
                dialog.unread_count = 0;
            } else {
                int iMax = Math.max(i4 - i2, 0);
                dialog.unread_count = iMax;
                if (i3 != Integer.MAX_VALUE) {
                    int i5 = dialog.top_message;
                    if (iMax > i3 - i5) {
                        dialog.unread_count = i3 - i5;
                    }
                }
            }
            boolean z2 = dialog.unread_mark;
            if (z2) {
                dialog.unread_mark = false;
                getMessagesStorage().setDialogUnread(dialog.id, false);
            }
            if ((i4 != 0 || z2) && dialog.unread_count == 0) {
                if (!isDialogMuted(j, 0L)) {
                    this.unreadUnmutedDialogs--;
                }
                int i6 = 0;
                while (true) {
                    DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
                    if (i6 < dialogFilterArr.length) {
                        DialogFilter dialogFilter = dialogFilterArr[i6];
                        if (dialogFilter != null && (dialogFilter.flags & DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                            sortDialogs(null);
                            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
                            break;
                        }
                        i6++;
                    } else {
                        break;
                    }
                }
            }
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
        }
        LongSparseIntArray longSparseIntArray = new LongSparseIntArray(1);
        longSparseIntArray.put(j, 0);
        getNotificationsController().processDialogsUpdateRead(longSparseIntArray);
    }

    public void lambda$fetchCommunityPendingJoinRequests$244(Utilities.Callback2 callback2, TL_communities.PeerLinkRequests peerLinkRequests, TLRPC.TL_error tL_error) {
        if (peerLinkRequests != null) {
            putChats(peerLinkRequests.chats, false);
            putUsers(peerLinkRequests.users, false);
            ArrayList<TL_communities.CommunityPeerRequest> arrayList = peerLinkRequests.requests;
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                TL_communities.CommunityPeerRequest communityPeerRequest = arrayList.get(i);
                i++;
                long j = -DialogObject.getPeerDialogId(communityPeerRequest.peer);
                if (getChatFull(j) == null) {
                    loadFullChat(j, 0, true);
                }
            }
            callback2.run(peerLinkRequests, null);
            return;
        }
        callback2.run(null, tL_error);
    }

    public int fetchCommunityJoinedChats(long j, long j2, final Utilities.Callback2<TL_communities.ParticipantJoinedChats, TLRPC.TL_error> callback2) {
        TL_communities.TL_communities_getParticipantJoinedChats tL_communities_getParticipantJoinedChats = new TL_communities.TL_communities_getParticipantJoinedChats();
        tL_communities_getParticipantJoinedChats.community = getInputChannel(j);
        tL_communities_getParticipantJoinedChats.participant = getInputPeer(j2);
        return getConnectionsManager().sendRequestTyped(tL_communities_getParticipantJoinedChats, new AiTonesController$$ExternalSyntheticLambda0(), new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$fetchCommunityJoinedChats$245(callback2, (TL_communities.ParticipantJoinedChats) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$toggleCommunityParticipantBanned$246(long j, Utilities.Callback2 callback2, TLRPC.Bool bool, TLRPC.TL_error tL_error) {
        if (bool != null) {
            loadFullChat(j, 0, true);
        }
        if (callback2 != null) {
            callback2.run(bool, tL_error);
        }
    }

    public int resolveCommunityJoinPendingRequest(final long j, long j2, boolean z, final Utilities.Callback2<TLRPC.Bool, TLRPC.TL_error> callback2) {
        TL_communities.TL_communities_togglePeerLinkRequestApproval tL_communities_togglePeerLinkRequestApproval = new TL_communities.TL_communities_togglePeerLinkRequestApproval();
        tL_communities_togglePeerLinkRequestApproval.community = getInputChannel(j);
        tL_communities_togglePeerLinkRequestApproval.peer = getInputPeer(j2);
        tL_communities_togglePeerLinkRequestApproval.reject = z;
        return getConnectionsManager().sendRequestTyped(tL_communities_togglePeerLinkRequestApproval, new AiTonesController$$ExternalSyntheticLambda0(), new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$resolveCommunityJoinPendingRequest$247(j, callback2, (TLRPC.Bool) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$resolveCommunityAllJoinPendingRequests$248(long j, Utilities.Callback2 callback2, TLRPC.Bool bool, TLRPC.TL_error tL_error) {
        if (bool != null) {
            loadFullChat(j, 0, true);
        }
        if (callback2 != null) {
            callback2.run(bool, tL_error);
        }
    }

    public ArrayList<TLRPC.Chat> getJoinedCommunities() {
        return this.joinedCommunities;
    }

    public void fetchJoinedCommunities(final Utilities.Callback<ArrayList<TLRPC.Chat>> callback, int i) {
        getConnectionsManager().bindRequestToGuid(getConnectionsManager().sendRequestTyped(new TL_communities.TL_communities_getJoinedCommunities(), new AiTonesController$$ExternalSyntheticLambda0(), new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$fetchJoinedCommunities$249(callback, (TLRPC.messages_Chats) obj, (TLRPC.TL_error) obj2);
            }
        }), i);
    }

    public void lambda$fetchChatsToAddToCommunity$250(Utilities.Callback2 callback2, TLRPC.messages_Chats messages_chats, TLRPC.TL_error tL_error) {
        if (messages_chats != null) {
            ArrayList arrayList = new ArrayList(messages_chats.chats.size());
            putChats(messages_chats.chats, true);
            ArrayList<TLRPC.Chat> arrayList2 = messages_chats.chats;
            int size = arrayList2.size();
            int i = 0;
            while (i < size) {
                TLRPC.Chat chat = arrayList2.get(i);
                i++;
                TLRPC.Chat chat2 = chat;
                if (chat2.creator && !ChatObject.isMonoForum(chat2) && !ChatObject.isCommunity(chat2)) {
                    arrayList.add(chat2);
                }
            }
            callback2.run(arrayList, null);
            return;
        }
        callback2.run(null, tL_error);
    }

    public int linkCommunity(long j, long j2, boolean z, Utilities.Callback2<TLRPC.Bool, TLRPC.TL_error> callback2) {
        return linkCommunityInternal(j, j2, false, z, callback2);
    }

    public int unlinkCommunity(long j, long j2, Utilities.Callback2<TLRPC.Bool, TLRPC.TL_error> callback2) {
        return linkCommunityInternal(j, j2, true, false, callback2);
    }

    private int linkCommunityInternal(final long j, final long j2, final boolean z, boolean z2, final Utilities.Callback2<TLRPC.Bool, TLRPC.TL_error> callback2) {
        TL_communities.TL_communities_togglePeerLink tL_communities_togglePeerLink = new TL_communities.TL_communities_togglePeerLink();
        tL_communities_togglePeerLink.community = getInputChannel(j2);
        tL_communities_togglePeerLink.peer = getInputPeer(j);
        tL_communities_togglePeerLink.deleted = z;
        tL_communities_togglePeerLink.visible = !z2;
        tL_communities_togglePeerLink.hidden = z2;
        return getConnectionsManager().sendRequestTyped(tL_communities_togglePeerLink, new AiTonesController$$ExternalSyntheticLambda0(), new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$linkCommunityInternal$251(j, callback2, z, j2, (TLRPC.Bool) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void m4713$r8$lambda$CS2auyn4pvVA26K_GW0vrLQeAY(Utilities.Callback2 callback2, TLRPC.Updates updates, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            callback2.run(null, tL_error);
        } else {
            callback2.run(new TLRPC.TL_boolTrue(), null);
        }
    }

    public int createChat(String str, ArrayList<Long> arrayList, String str2, int i, boolean z, Location location, String str3, int i2, final BaseFragment baseFragment) {
        if (i == 0 && !z) {
            final TLRPC.TL_messages_createChat tL_messages_createChat = new TLRPC.TL_messages_createChat();
            tL_messages_createChat.title = str;
            if (i2 >= 0) {
                tL_messages_createChat.ttl_period = i2;
                tL_messages_createChat.flags |= 1;
            }
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                TLRPC.User user = getUser(arrayList.get(i3));
                if (user != null) {
                    tL_messages_createChat.users.add(getInputUser(user));
                }
            }
            return getConnectionsManager().sendRequest(tL_messages_createChat, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$createChat$256(baseFragment, tL_messages_createChat, tLObject, tL_error);
                }
            }, 2);
        }
        if (!z && i != 2 && i != 4 && i != 5) {
            return 0;
        }
        final TLRPC.TL_channels_createChannel tL_channels_createChannel = new TLRPC.TL_channels_createChannel();
        tL_channels_createChannel.title = str;
        if (str2 == null) {
            str2 = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        tL_channels_createChannel.about = str2;
        tL_channels_createChannel.for_import = z;
        if (z || i == 4 || i == 5) {
            tL_channels_createChannel.megagroup = true;
        } else {
            tL_channels_createChannel.broadcast = true;
        }
        tL_channels_createChannel.forum = i == 5;
        if (location != null) {
            TLRPC.TL_inputGeoPoint tL_inputGeoPoint = new TLRPC.TL_inputGeoPoint();
            tL_channels_createChannel.geo_point = tL_inputGeoPoint;
            tL_inputGeoPoint.lat = location.getLatitude();
            tL_channels_createChannel.geo_point._long = location.getLongitude();
            tL_channels_createChannel.address = str3;
            tL_channels_createChannel.flags |= 4;
        }
        return getConnectionsManager().sendRequest(tL_channels_createChannel, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$createChat$259(baseFragment, tL_channels_createChannel, tLObject, tL_error);
            }
        }, 2);
    }

    public void lambda$createChat$254(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_messages_createChat tL_messages_createChat) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_messages_createChat, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    public void lambda$createChat$257(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_channels_createChannel tL_channels_createChannel) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_createChannel, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatDidFailCreate, new Object[0]);
    }

    public void lambda$convertToMegaGroup$263(final Context context, final AlertDialog alertDialog, final MessagesStorage.LongCallback longCallback, final long j, Runnable runnable, final BaseFragment baseFragment, final TLRPC.TL_messages_migrateChat tL_messages_migrateChat, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            if (context != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.$r8$lambda$NXkI3y4ym0Z9q2fsur1lnYym4a4(context, alertDialog);
                    }
                });
            }
            final TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            processUpdates(updates, false);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.$r8$lambda$82VlSIyXpFd4U2Ue3kFjdqO89CQ(longCallback, updates, j);
                }
            });
            return;
        }
        if (runnable != null) {
            runnable.run();
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$convertToMegaGroup$262(longCallback, context, alertDialog, tL_error, baseFragment, tL_messages_migrateChat);
            }
        });
    }

    public static void lambda$convertToGigaGroup$268(final Context context, final AlertDialog alertDialog, final MessagesStorage.BooleanCallback booleanCallback, final BaseFragment baseFragment, final TLRPC.TL_channels_convertToGigagroup tL_channels_convertToGigagroup, TLObject tLObject, final TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            if (context != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.$r8$lambda$onEFsPaZ2oqMorWhf348R_vIoMI(context, alertDialog);
                    }
                });
            }
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.$r8$lambda$HFW_HSoGcJaXhFKhfRSxN6dhjFI(booleanCallback);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$convertToGigaGroup$267(booleanCallback, context, alertDialog, tL_error, baseFragment, tL_channels_convertToGigagroup);
            }
        });
    }

    public static void lambda$addUsersToChannel$270(TLRPC.TL_error tL_error, BaseFragment baseFragment, TLRPC.TL_channels_inviteToChannel tL_channels_inviteToChannel) {
        AlertsCreator.processError(this.currentAccount, tL_error, baseFragment, tL_channels_inviteToChannel, Boolean.TRUE);
    }

    public void lambda$addUsersToChannel$271(TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers, long j) {
        putUsers(tL_messages_invitedUsers.updates.users, false);
        putChats(tL_messages_invitedUsers.updates.chats, false);
        AlertsCreator.checkRestrictedInviteUsers(this.currentAccount, getChat(Long.valueOf(j)), tL_messages_invitedUsers);
    }

    public void setDefaultSendAs(final long j, long j2) {
        TLRPC.ChatFull chatFull = getChatFull(-j);
        if (chatFull != null) {
            chatFull.default_send_as = getPeer(j2);
            getMessagesStorage().updateChatInfo(chatFull, false);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateDefaultSendAsPeer, Long.valueOf(j), chatFull.default_send_as);
        }
        TLRPC.TL_messages_saveDefaultSendAs tL_messages_saveDefaultSendAs = new TLRPC.TL_messages_saveDefaultSendAs();
        tL_messages_saveDefaultSendAs.peer = getInputPeer(j);
        tL_messages_saveDefaultSendAs.send_as = getInputPeer(j2);
        getConnectionsManager().sendRequest(tL_messages_saveDefaultSendAs, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$setDefaultSendAs$273(j, tLObject, tL_error);
            }
        }, 64);
    }

    public void lambda$toggleChatNoForwards$276(final Utilities.Callback2 callback2, TLRPC.Updates updates, final TLRPC.TL_error tL_error) {
        final int i;
        if (updates == null) {
            if (callback2 != null) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        callback2.run(0, tL_error);
                    }
                });
                return;
            }
            return;
        }
        ArrayList<TLRPC.Update> arrayList = updates.updates;
        int size = arrayList.size();
        int i2 = 0;
        while (i2 < size) {
            TLRPC.Update update = arrayList.get(i2);
            i2++;
            TLRPC.Update update2 = update;
            if (update2 instanceof TL_update.TL_updateNewMessage) {
                TLRPC.MessageAction messageAction = ((TL_update.TL_updateNewMessage) update2).message.action;
                if (messageAction instanceof TLRPC.TL_messageActionNoForwardsRequest) {
                    i = 2;
                } else if (messageAction instanceof TLRPC.TL_messageActionNoForwardsToggle) {
                    i = 1;
                }
                processUpdates(updates, false);
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$toggleChatNoForwards$274(callback2, i);
                    }
                });
            }
        }
        i = 0;
        processUpdates(updates, false);
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$toggleChatNoForwards$274(callback2, i);
            }
        });
    }

    public void lambda$toggleChatJoinToSend$278(Runnable runnable, Runnable runnable2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$toggleChatJoinToSend$277();
                }
            });
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        if (tL_error == null || "CHAT_NOT_MODIFIED".equals(tL_error.text)) {
            if (runnable != null) {
                runnable.run();
            }
        } else if (runnable2 != null) {
            runnable2.run();
        }
    }

    public void lambda$toggleChatJoinRequest$280(final boolean z, final long j, final long j2, Runnable runnable, Runnable runnable2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$toggleChatJoinRequest$279(z, j, j2);
                }
            }, 100L);
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        if (tL_error == null || "CHAT_NOT_MODIFIED".equals(tL_error.text)) {
            if (runnable != null) {
                runnable.run();
            }
        } else if (runnable2 != null) {
            runnable2.run();
        }
    }

    public void lambda$toggleChannelSignatures$282(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$toggleChannelSignatures$281();
                }
            });
        }
    }

    public void lambda$toggleChannelForum$284(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$toggleChannelForum$283();
                }
            });
        }
    }

    public void lambda$toggleChannelInvitesHistory$286(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$toggleChannelInvitesHistory$285();
                }
            });
        }
    }

    public void lambda$updateChatAbout$288(final TLRPC.ChatFull chatFull, final String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (!(tLObject instanceof TLRPC.TL_boolTrue) || chatFull == null) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$updateChatAbout$287(chatFull, str);
            }
        });
    }

    public void lambda$updateChannelUserName$290(final long j, final String str, final Runnable runnable, BaseFragment baseFragment, TLRPC.TL_channels_updateUsername tL_channels_updateUsername, Runnable runnable2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if ((tLObject instanceof TLRPC.TL_boolTrue) || (tL_error != null && "USERNAME_NOT_MODIFIED".equals(tL_error.text))) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$updateChannelUserName$289(j, str, runnable);
                }
            });
            return;
        }
        AlertsCreator.processError(UserConfig.selectedAccount, tL_error, baseFragment, tL_channels_updateUsername, new Object[0]);
        if (runnable2 != null) {
            runnable2.run();
        }
    }

    void lambda$sendBotStart$291(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            return;
        }
        processUpdates((TLRPC.Updates) tLObject, false);
    }

    public boolean isJoiningChannel(long j) {
        return this.joiningToChannels.contains(Long.valueOf(j));
    }

    public void addUserToChat(long j, TLRPC.User user, int i, String str, BaseFragment baseFragment, Runnable runnable) {
        addUserToChat(j, user, i, str, baseFragment, false, runnable, null);
    }

    public void addUsersToChat(TLRPC.Chat chat, BaseFragment baseFragment, ArrayList<TLRPC.User> arrayList, int i, final Consumer<TLRPC.User> consumer, final Consumer<TLRPC.User> consumer2, final Runnable runnable) {
        final int size = arrayList.size();
        final int[] iArr = {0};
        final TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers = new TLRPC.TL_messages_invitedUsers();
        tL_messages_invitedUsers.updates = new TLRPC.TL_updates();
        final TLRPC.Chat chat2 = chat;
        long j = chat2.id;
        for (int i2 = 0; i2 < size; i2++) {
            final TLRPC.User user = arrayList.get(i2);
            addUserToChat(j, user, i, null, baseFragment, false, new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.$r8$lambda$ib66aqX1oHr64AVI88D6jWCsoY8(consumer, user);
                }
            }, new ErrorDelegate() { 
                @Override 
                public final boolean run(TLRPC.TL_error tL_error) {
                    return MessagesController.$r8$lambda$pUOdXTEZ_b1KgPsz2GjfQVpuEqo(consumer2, user, tL_error);
                }
            }, new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.lambda$addUsersToChat$295(tL_messages_invitedUsers, iArr, size, chat2, runnable, (TLRPC.TL_messages_invitedUsers) obj);
                }
            });
            putUser(user, false);
            chat2 = chat;
        }
    }

    public static void lambda$addUsersToChat$294(TLRPC.Chat chat, TLRPC.TL_messages_invitedUsers tL_messages_invitedUsers) {
        AlertsCreator.checkRestrictedInviteUsers(this.currentAccount, chat, tL_messages_invitedUsers);
    }

    public void addUserToChat(long j, TLRPC.User user, int i, String str, BaseFragment baseFragment, boolean z, Runnable runnable, ErrorDelegate errorDelegate) {
        addUserToChat(j, user, i, str, baseFragment, z, runnable, errorDelegate, null);
    }

    public void addUserToChat(final long j, final TLRPC.User user, int i, String str, final BaseFragment baseFragment, final boolean z, final Runnable runnable, final ErrorDelegate errorDelegate, final Utilities.Callback<TLRPC.TL_messages_invitedUsers> callback) {
        TLObject tLObject;
        if (user == null) {
            if (errorDelegate != null) {
                errorDelegate.run(null);
            }
            if (callback != null) {
                callback.run(null);
                return;
            }
            return;
        }
        final TLRPC.Chat chat = getChat(Long.valueOf(j));
        final boolean zIsChannel = ChatObject.isChannel(chat);
        final boolean z2 = zIsChannel && chat.megagroup;
        final TLRPC.InputUser inputUser = getInputUser(user);
        if (str != null && (!zIsChannel || z2)) {
            TLRPC.TL_messages_startBot tL_messages_startBot = new TLRPC.TL_messages_startBot();
            tL_messages_startBot.bot = inputUser;
            if (zIsChannel) {
                tL_messages_startBot.peer = getInputPeer(-j);
            } else {
                TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
                tL_messages_startBot.peer = tL_inputPeerChat;
                tL_inputPeerChat.chat_id = j;
            }
            tL_messages_startBot.start_param = str;
            tL_messages_startBot.random_id = Utilities.random.nextLong();
            tLObject = tL_messages_startBot;
        } else if (zIsChannel) {
            if (inputUser instanceof TLRPC.TL_inputUserSelf) {
                if (this.joiningToChannels.contains(Long.valueOf(j))) {
                    if (errorDelegate != null) {
                        errorDelegate.run(null);
                        return;
                    }
                    return;
                } else {
                    TLRPC.TL_channels_joinChannel tL_channels_joinChannel = new TLRPC.TL_channels_joinChannel();
                    tL_channels_joinChannel.channel = getInputChannel(j);
                    this.joiningToChannels.add(Long.valueOf(j));
                    tLObject = tL_channels_joinChannel;
                }
            } else {
                TLRPC.TL_channels_inviteToChannel tL_channels_inviteToChannel = new TLRPC.TL_channels_inviteToChannel();
                tL_channels_inviteToChannel.channel = getInputChannel(j);
                tL_channels_inviteToChannel.users.add(inputUser);
                tLObject = tL_channels_inviteToChannel;
            }
        } else {
            TLRPC.TL_messages_addChatUser tL_messages_addChatUser = new TLRPC.TL_messages_addChatUser();
            tL_messages_addChatUser.chat_id = j;
            tL_messages_addChatUser.fwd_limit = i;
            tL_messages_addChatUser.user_id = inputUser;
            tLObject = tL_messages_addChatUser;
        }
        final TLObject tLObject2 = tLObject;
        getConnectionsManager().sendRequest(tLObject2, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject3, TLRPC.TL_error tL_error) {
                this.f$0.lambda$addUserToChat$308(zIsChannel, inputUser, j, callback, runnable, user, z, errorDelegate, baseFragment, tLObject2, z2, chat, tLObject3, tL_error);
            }
        });
    }

    public void lambda$addUserToChat$296(long j) {
        this.joiningToChannels.remove(Long.valueOf(j));
    }

    public static void lambda$deleteParticipantFromChat$311(boolean z, boolean z2, final long j, Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            return;
        }
        processUpdates((TLRPC.Updates) tLObject, false);
        if (z && !z2) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$deleteParticipantFromChat$310(j);
                }
            }, 1000L);
        }
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public void lambda$deleteParticipantFromChat$314(boolean z, TLRPC.User user, final long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            return;
        }
        if (tLObject instanceof TLRPC.Updates) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
        if (!z || UserObject.isUserSelf(user)) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$deleteParticipantFromChat$313(j);
            }
        }, 1000L);
    }

    public void lambda$changeChatTitle$315(Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            return;
        }
        processUpdates((TLRPC.Updates) tLObject, false);
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }

    public void changeChatAvatar(final long j, final TLRPC.TL_inputChatPhoto tL_inputChatPhoto, TLRPC.InputFile inputFile, TLRPC.InputFile inputFile2, TLRPC.VideoSize videoSize, double d, final String str, final TLRPC.FileLocation fileLocation, final TLRPC.FileLocation fileLocation2, final Runnable runnable) {
        TLRPC.InputChatPhoto tL_inputChatPhotoEmpty;
        TLObject tLObject;
        if (tL_inputChatPhoto != null) {
            tL_inputChatPhotoEmpty = tL_inputChatPhoto;
        } else if (inputFile != null || inputFile2 != null || videoSize != null) {
            TLRPC.TL_inputChatUploadedPhoto tL_inputChatUploadedPhoto = new TLRPC.TL_inputChatUploadedPhoto();
            if (inputFile != null) {
                tL_inputChatUploadedPhoto.file = inputFile;
                tL_inputChatUploadedPhoto.flags |= 1;
            }
            if (inputFile2 != null) {
                tL_inputChatUploadedPhoto.video = inputFile2;
                int i = tL_inputChatUploadedPhoto.flags;
                tL_inputChatUploadedPhoto.video_start_ts = d;
                tL_inputChatUploadedPhoto.flags = i | 6;
            }
            if (videoSize != null) {
                tL_inputChatUploadedPhoto.video_emoji_markup = videoSize;
                tL_inputChatUploadedPhoto.flags |= 8;
            }
            tL_inputChatPhotoEmpty = tL_inputChatUploadedPhoto;
        } else {
            tL_inputChatPhotoEmpty = new TLRPC.TL_inputChatPhotoEmpty();
        }
        if (ChatObject.isChannel(j, this.currentAccount)) {
            TLRPC.TL_channels_editPhoto tL_channels_editPhoto = new TLRPC.TL_channels_editPhoto();
            tL_channels_editPhoto.channel = getInputChannel(j);
            tL_channels_editPhoto.photo = tL_inputChatPhotoEmpty;
            tLObject = tL_channels_editPhoto;
        } else {
            TLRPC.TL_messages_editChatPhoto tL_messages_editChatPhoto = new TLRPC.TL_messages_editChatPhoto();
            tL_messages_editChatPhoto.chat_id = j;
            tL_messages_editChatPhoto.photo = tL_inputChatPhotoEmpty;
            tLObject = tL_messages_editChatPhoto;
        }
        getConnectionsManager().sendRequest(tLObject, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                this.f$0.lambda$changeChatAvatar$317(tL_inputChatPhoto, fileLocation, fileLocation2, str, j, runnable, tLObject2, tL_error);
            }
        }, 64);
    }

    public void lambda$changeChatAvatar$316(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_AVATAR));
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.reloadDialogPhotos, new Object[0]);
    }

    public void unregistedPush() {
        if (getUserConfig().registeredForPush && SharedConfig.pushString.length() == 0) {
            TL_account.unregisterDevice unregisterdevice = new TL_account.unregisterDevice();
            unregisterdevice.token = SharedConfig.pushString;
            unregisterdevice.token_type = SharedConfig.pushType;
            for (int i = 0; i < 16; i++) {
                UserConfig userConfig = UserConfig.getInstance(i);
                if (i != this.currentAccount && userConfig.isClientActivated()) {
                    unregisterdevice.other_uids.add(Long.valueOf(userConfig.getClientUserId()));
                }
            }
            getConnectionsManager().sendRequest(unregisterdevice, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.m4714$r8$lambda$CU4LiY8xZULxFD63RwvkR1nCLI(tLObject, tL_error);
                }
            });
        }
    }

    void lambda$performLogout$320(final TLObject tLObject, TLRPC.TL_error tL_error) {
        getConnectionsManager().cleanup(false);
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                MessagesController.$r8$lambda$Nyjt44YRgorr6dNlpNcCr0WgWzQ(tLObject);
            }
        });
    }

    public static void lambda$registerForPush$322(int i, String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("account " + this.currentAccount + " registered for push, push type: " + i);
            }
            getUserConfig().registeredForPush = true;
            SharedConfig.pushString = str;
            SharedConfig.pushType = i;
            getUserConfig().saveConfig(false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$registerForPush$321();
            }
        });
    }

    public void lambda$loadCurrentState$323(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.updatingState = false;
        if (tL_error == null) {
            TLRPC.TL_updates_state tL_updates_state = (TLRPC.TL_updates_state) tLObject;
            getMessagesStorage().setLastDateValue(tL_updates_state.date);
            getMessagesStorage().setLastPtsValue(tL_updates_state.pts);
            getMessagesStorage().setLastSeqValue(tL_updates_state.seq);
            getMessagesStorage().setLastQtsValue(tL_updates_state.qts);
            for (int i = 0; i < 3; i++) {
                processUpdatesQueue(i, 2);
            }
            getMessagesStorage().saveDiffParams(getMessagesStorage().getLastSeqValue(), getMessagesStorage().getLastPtsValue(), getMessagesStorage().getLastDateValue(), getMessagesStorage().getLastQtsValue());
            return;
        }
        if (tL_error.code != 401) {
            loadCurrentState();
        }
    }

    private int getUpdateSeq(TLRPC.Updates updates) {
        if (updates instanceof TLRPC.TL_updatesCombined) {
            return updates.seq_start;
        }
        return updates.seq;
    }

    private void setUpdatesStartTime(int i, long j) {
        if (i == 0) {
            this.updatesStartWaitTimeSeq = j;
        } else if (i == 1) {
            this.updatesStartWaitTimePts = j;
        } else if (i == 2) {
            this.updatesStartWaitTimeQts = j;
        }
    }

    public long getUpdatesStartTime(int i) {
        if (i == 0) {
            return this.updatesStartWaitTimeSeq;
        }
        if (i == 1) {
            return this.updatesStartWaitTimePts;
        }
        if (i == 2) {
            return this.updatesStartWaitTimeQts;
        }
        return 0L;
    }

    private int isValidUpdate(TLRPC.Updates updates, int i) {
        if (i == 0) {
            int updateSeq = getUpdateSeq(updates);
            if (getMessagesStorage().getLastSeqValue() + 1 == updateSeq || getMessagesStorage().getLastSeqValue() == updateSeq) {
                return 0;
            }
            return getMessagesStorage().getLastSeqValue() < updateSeq ? 1 : 2;
        }
        if (i == 1) {
            if (updates.pts <= getMessagesStorage().getLastPtsValue()) {
                return 2;
            }
            return getMessagesStorage().getLastPtsValue() + updates.pts_count == updates.pts ? 0 : 1;
        }
        if (i != 2) {
            return 0;
        }
        if (updates.pts <= getMessagesStorage().getLastQtsValue()) {
            return 2;
        }
        return getMessagesStorage().getLastQtsValue() + updates.updates.size() == updates.pts ? 0 : 1;
    }

    private void processChannelsUpdatesQueue(long j, int i) {
        char c2;
        ArrayList<TLRPC.Updates> arrayList = this.updatesQueueChannels.get(j);
        if (arrayList == null) {
            return;
        }
        int i2 = this.channelsPts.get(j);
        if (arrayList.isEmpty() || i2 == 0) {
            this.updatesQueueChannels.remove(j);
            return;
        }
        Collections.sort(arrayList, new Comparator() { 
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return AndroidUtilities.compare(((TLRPC.Updates) obj).pts, ((TLRPC.Updates) obj2).pts);
            }
        });
        if (i == 2) {
            this.channelsPts.put(j, arrayList.get(0).pts);
        }
        boolean z = false;
        while (arrayList.size() > 0) {
            TLRPC.Updates updates = arrayList.get(0);
            int i3 = updates.pts;
            if (i3 <= i2) {
                c2 = 2;
            } else {
                c2 = updates.pts_count + i2 == i3 ? (char) 0 : (char) 1;
            }
            if (c2 == 0) {
                processUpdates(updates, true);
                if (arrayList.size() <= 0) {
                    break;
                }
                arrayList.remove(0);
                z = true;
            } else {
                if (c2 == 1) {
                    long j2 = this.updatesStartWaitTimeChannels.get(j);
                    if (j2 != 0 && (z || Math.abs(System.currentTimeMillis() - j2) <= 1500)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("HOLE IN CHANNEL " + j + " UPDATES QUEUE - will wait more time");
                        }
                        if (z) {
                            this.updatesStartWaitTimeChannels.put(j, System.currentTimeMillis());
                            return;
                        }
                        return;
                    }
                    if (BuildVars.LOGS_ENABLED) {
                        FileLog.d("HOLE IN CHANNEL " + j + " UPDATES QUEUE - getChannelDifference ");
                    }
                    this.updatesStartWaitTimeChannels.delete(j);
                    this.updatesQueueChannels.remove(j);
                    getChannelDifference(j);
                    return;
                }
                if (arrayList.size() <= 0) {
                    break;
                } else {
                    arrayList.remove(0);
                }
            }
        }
        this.updatesQueueChannels.remove(j);
        this.updatesStartWaitTimeChannels.delete(j);
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("UPDATES CHANNEL " + j + " QUEUE PROCEED - OK");
        }
    }

    private void processUpdatesQueue(int i, int i2) {
        ArrayList<TLRPC.Updates> arrayList;
        if (i == 0) {
            arrayList = this.updatesQueueSeq;
            Collections.sort(arrayList, new Comparator() { 
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return this.f$0.lambda$processUpdatesQueue$325((TLRPC.Updates) obj, (TLRPC.Updates) obj2);
                }
            });
        } else if (i == 1) {
            arrayList = this.updatesQueuePts;
            Collections.sort(arrayList, new Comparator() { 
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return AndroidUtilities.compare(((TLRPC.Updates) obj).pts, ((TLRPC.Updates) obj2).pts);
                }
            });
        } else if (i == 2) {
            arrayList = this.updatesQueueQts;
            Collections.sort(arrayList, new Comparator() { 
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return AndroidUtilities.compare(((TLRPC.Updates) obj).pts, ((TLRPC.Updates) obj2).pts);
                }
            });
        } else {
            arrayList = null;
        }
        if (arrayList != null && !arrayList.isEmpty()) {
            if (i2 == 2) {
                TLRPC.Updates updates = arrayList.get(0);
                if (i == 0) {
                    getMessagesStorage().setLastSeqValue(getUpdateSeq(updates));
                } else if (i == 1) {
                    getMessagesStorage().setLastPtsValue(updates.pts);
                } else {
                    getMessagesStorage().setLastQtsValue(updates.pts);
                }
            }
            boolean z = false;
            while (arrayList.size() > 0) {
                TLRPC.Updates updates2 = arrayList.get(0);
                int iIsValidUpdate = isValidUpdate(updates2, i);
                if (iIsValidUpdate == 0) {
                    processUpdates(updates2, true);
                    arrayList.remove(0);
                    z = true;
                } else {
                    if (iIsValidUpdate == 1) {
                        if (getUpdatesStartTime(i) != 0 && (z || Math.abs(System.currentTimeMillis() - getUpdatesStartTime(i)) <= 1500)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("HOLE IN UPDATES QUEUE - will wait more time");
                            }
                            if (z) {
                                setUpdatesStartTime(i, System.currentTimeMillis());
                                return;
                            }
                            return;
                        }
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("HOLE IN UPDATES QUEUE - getDifference");
                        }
                        setUpdatesStartTime(i, 0L);
                        arrayList.clear();
                        getDifference();
                        return;
                    }
                    arrayList.remove(0);
                }
            }
            arrayList.clear();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("UPDATES QUEUE PROCEED - OK");
            }
        }
        setUpdatesStartTime(i, 0L);
    }

    public void lambda$loadUnknownChannel$328(long j, TLRPC.Chat chat, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            if (!tL_messages_peerDialogs.dialogs.isEmpty() && !tL_messages_peerDialogs.chats.isEmpty()) {
                TLRPC.TL_dialog tL_dialog = (TLRPC.TL_dialog) tL_messages_peerDialogs.dialogs.get(0);
                TLRPC.TL_messages_dialogs tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
                tL_messages_dialogs.dialogs.addAll(tL_messages_peerDialogs.dialogs);
                tL_messages_dialogs.messages.addAll(tL_messages_peerDialogs.messages);
                tL_messages_dialogs.users.addAll(tL_messages_peerDialogs.users);
                tL_messages_dialogs.chats.addAll(tL_messages_peerDialogs.chats);
                processLoadedDialogs(tL_messages_dialogs, null, null, tL_dialog.folder_id, 0, 1, this.DIALOGS_LOAD_TYPE_CHANNEL, false, false, false);
            }
        }
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
        this.gettingUnknownChannels.delete(chat.id);
    }

    public void startShortPoll(TLRPC.Chat chat, int i, boolean z) {
        startShortPoll(chat, i, z, null);
    }

    public void startShortPoll(final TLRPC.Chat chat, final int i, final boolean z, final Consumer<Boolean> consumer) {
        if (chat == null) {
            return;
        }
        Utilities.stageQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$startShortPoll$331(chat, z, i, consumer);
            }
        });
    }

    public void lambda$getChannelDifference$332(long j) {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onReceivedChannelDifference, Long.valueOf(j));
    }

    public void lambda$getChannelDifference$336(TLRPC.updates_ChannelDifference updates_channeldifference) {
        putUsers(updates_channeldifference.users, false);
        putChats(updates_channeldifference.chats, false);
    }

    void lambda$getChannelDifference$337(SparseArray sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int iKeyAt = sparseArray.keyAt(i);
            long[] jArr = (long[]) sparseArray.valueAt(i);
            getSendMessagesHelper().processSentMessage((int) jArr[1]);
            NotificationCenter notificationCenter = getNotificationCenter();
            int i2 = NotificationCenter.messageReceivedByServer;
            Integer numValueOf = Integer.valueOf((int) jArr[1]);
            Integer numValueOf2 = Integer.valueOf(iKeyAt);
            Long lValueOf = Long.valueOf(jArr[0]);
            Boolean bool = Boolean.FALSE;
            notificationCenter.lambda$postNotificationNameOnUIThread$1(i2, numValueOf, numValueOf2, null, lValueOf, 0L, -1, bool);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messageReceivedByServer2, Integer.valueOf((int) jArr[1]), Integer.valueOf(iKeyAt), null, Long.valueOf(jArr[0]), 0L, -1, bool);
        }
    }

    void lambda$getChannelDifference$338(LongSparseArray longSparseArray) {
        for (int i = 0; i < longSparseArray.size(); i++) {
            updateInterfaceWithMessages(longSparseArray.keyAt(i), (ArrayList) longSparseArray.valueAt(i), 0);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void lambda$getChannelDifference$339(ArrayList arrayList) {
        getNotificationsController().processNewMessages(arrayList, true, false, null);
    }

    public void lambda$getChannelDifference$341(long j) {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.onReceivedChannelDifference, Long.valueOf(j));
    }

    public void lambda$getDifference$357(final int i, final int i2, TLObject tLObject, TLRPC.TL_error tL_error) {
        int i3 = 0;
        if (tL_error == null) {
            final TLRPC.updates_Difference updates_difference = (TLRPC.updates_Difference) tLObject;
            if (updates_difference instanceof TLRPC.TL_updates_differenceTooLong) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$getDifference$348(updates_difference, i, i2);
                    }
                });
                return;
            }
            if (updates_difference instanceof TLRPC.TL_updates_differenceSlice) {
                TLRPC.TL_updates_state tL_updates_state = updates_difference.intermediate_state;
                getDifference(tL_updates_state.pts, tL_updates_state.date, tL_updates_state.qts, true);
            }
            final LongSparseArray longSparseArray = new LongSparseArray();
            final LongSparseArray longSparseArray2 = new LongSparseArray();
            for (int i4 = 0; i4 < updates_difference.users.size(); i4++) {
                TLRPC.User user = updates_difference.users.get(i4);
                longSparseArray.put(user.id, user);
            }
            for (int i5 = 0; i5 < updates_difference.chats.size(); i5++) {
                TLRPC.Chat chat = updates_difference.chats.get(i5);
                longSparseArray2.put(chat.id, chat);
            }
            final ArrayList arrayList = new ArrayList();
            if (!updates_difference.other_updates.isEmpty()) {
                while (i3 < updates_difference.other_updates.size()) {
                    TLRPC.Update update = updates_difference.other_updates.get(i3);
                    if (update instanceof TL_update.TL_updateMessageID) {
                        arrayList.add((TL_update.TL_updateMessageID) update);
                        updates_difference.other_updates.remove(i3);
                    } else {
                        if (getUpdateType(update) == 2) {
                            long updateChannelId = getUpdateChannelId(update);
                            int channelPtsSync = this.channelsPts.get(updateChannelId);
                            if (channelPtsSync == 0 && (channelPtsSync = getMessagesStorage().getChannelPtsSync(updateChannelId)) != 0) {
                                this.channelsPts.put(updateChannelId, channelPtsSync);
                            }
                            if (channelPtsSync != 0 && getUpdatePts(update) <= channelPtsSync) {
                                updates_difference.other_updates.remove(i3);
                            }
                        }
                        i3++;
                    }
                    i3--;
                    i3++;
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getDifference$349(updates_difference);
                }
            });
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getDifference$356(updates_difference, arrayList, longSparseArray, longSparseArray2);
                }
            });
            return;
        }
        this.gettingDifference = false;
        getConnectionsManager().setIsUpdating(false);
        FileLog.d("received: isUpdating = false");
    }

    public void lambda$getDifference$350(SparseArray sparseArray) {
        for (int i = 0; i < sparseArray.size(); i++) {
            int iKeyAt = sparseArray.keyAt(i);
            long[] jArr = (long[]) sparseArray.valueAt(i);
            getSendMessagesHelper().processSentMessage((int) jArr[1]);
            NotificationCenter notificationCenter = getNotificationCenter();
            int i2 = NotificationCenter.messageReceivedByServer;
            Integer numValueOf = Integer.valueOf((int) jArr[1]);
            Integer numValueOf2 = Integer.valueOf(iKeyAt);
            Long lValueOf = Long.valueOf(jArr[0]);
            Boolean bool = Boolean.FALSE;
            notificationCenter.lambda$postNotificationNameOnUIThread$1(i2, numValueOf, numValueOf2, null, lValueOf, 0L, -1, bool);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messageReceivedByServer2, Integer.valueOf((int) jArr[1]), Integer.valueOf(iKeyAt), null, Long.valueOf(jArr[0]), 0L, -1, bool);
        }
    }

    public void lambda$getDifference$354(final ArrayList arrayList, final TLRPC.updates_Difference updates_difference, LongSparseArray longSparseArray) {
        if (!arrayList.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getDifference$351(arrayList, updates_difference);
                }
            });
        }
        getMessagesStorage().putMessages(updates_difference.new_messages, true, false, false, getDownloadController().getAutodownloadMask(), 0, 0L);
        for (int i = 0; i < longSparseArray.size(); i++) {
            final long jKeyAt = longSparseArray.keyAt(i);
            final ArrayList<MessageObject> arrayList2 = (ArrayList) longSparseArray.valueAt(i);
            getMediaDataController().loadReplyMessagesForMessages(arrayList2, jKeyAt, 0, 0L, new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$getDifference$353(jKeyAt, arrayList2);
                }
            }, 0, null);
        }
    }

    public void lambda$getDifference$352(long j, ArrayList arrayList) {
        updateInterfaceWithMessages(j, arrayList, 0);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void markDialogAsUnread(long j, TLRPC.InputPeer inputPeer, final long j2) {
        NativeByteBuffer nativeByteBuffer;
        TLRPC.Dialog dialog = this.dialogs_dict.get(j);
        NativeByteBuffer nativeByteBuffer2 = null;
        if (dialog != null) {
            dialog.unread_mark = true;
            if (dialog.unread_count == 0 && !isDialogMuted(j, 0L)) {
                this.unreadUnmutedDialogs++;
            }
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_READ_DIALOG_MESSAGE));
            getMessagesStorage().setDialogUnread(j, true);
            int i = 0;
            while (true) {
                DialogFilter[] dialogFilterArr = this.selectedDialogFilter;
                if (i < dialogFilterArr.length) {
                    DialogFilter dialogFilter = dialogFilterArr[i];
                    if (dialogFilter != null && (dialogFilter.flags & DIALOG_FILTER_FLAG_EXCLUDE_READ) != 0) {
                        sortDialogs(null);
                        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
                        break;
                    }
                    i++;
                } else {
                    break;
                }
            }
        }
        if (DialogObject.isEncryptedDialog(j)) {
            return;
        }
        TLRPC.TL_messages_markDialogUnread tL_messages_markDialogUnread = new TLRPC.TL_messages_markDialogUnread();
        tL_messages_markDialogUnread.unread = true;
        if (inputPeer == null) {
            inputPeer = getInputPeer(j);
        }
        if (inputPeer instanceof TLRPC.TL_inputPeerEmpty) {
            return;
        }
        TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
        tL_inputDialogPeer.peer = inputPeer;
        tL_messages_markDialogUnread.peer = tL_inputDialogPeer;
        if (j2 == 0) {
            try {
                nativeByteBuffer = new NativeByteBuffer(inputPeer.getObjectSize() + 12);
                try {
                    nativeByteBuffer.writeInt32(9);
                    nativeByteBuffer.writeInt64(j);
                    inputPeer.serializeToStream(nativeByteBuffer);
                } catch (Exception e) {
                    e = e;
                    nativeByteBuffer2 = nativeByteBuffer;
                    FileLog.e(e);
                    nativeByteBuffer = nativeByteBuffer2;
                }
            } catch (Exception e2) {
                e = e2;
            }
            j2 = getMessagesStorage().createPendingTask(nativeByteBuffer);
        }
        getConnectionsManager().sendRequest(tL_messages_markDialogUnread, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$markDialogAsUnread$358(j2, tLObject, tL_error);
            }
        });
    }

    public void lambda$loadUnreadDialogs$360(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadUnreadDialogs$359(tLObject);
            }
        });
    }

    public void lambda$reorderPinnedDialogs$361(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (j != 0) {
            getMessagesStorage().removePendingTask(j);
        }
    }

    public boolean pinDialog(long j, boolean z, TLRPC.InputPeer inputPeer, long j2) {
        NativeByteBuffer nativeByteBuffer;
        final long jCreatePendingTask;
        TLRPC.Dialog dialog = this.dialogs_dict.get(j);
        if (dialog == null || dialog.pinned == z) {
            return dialog != null;
        }
        int i = dialog.folder_id;
        ArrayList<TLRPC.Dialog> dialogs = getDialogs(i);
        dialog.pinned = z;
        if (z) {
            int iMax = 0;
            for (int i2 = 0; i2 < dialogs.size(); i2++) {
                TLRPC.Dialog dialog2 = dialogs.get(i2);
                if (!(dialog2 instanceof TLRPC.TL_dialogFolder)) {
                    if (!dialog2.pinned) {
                        if (dialog2.id != this.promoDialogId) {
                            break;
                        }
                    } else {
                        iMax = Math.max(dialog2.pinnedNum, iMax);
                    }
                }
            }
            dialog.pinnedNum = iMax + 1;
        } else {
            dialog.pinnedNum = 0;
        }
        NativeByteBuffer nativeByteBuffer2 = null;
        sortDialogs(null);
        if (!z && !dialogs.isEmpty() && dialogs.get(dialogs.size() - 1) == dialog && !this.dialogsEndReached.get(i)) {
            dialogs.remove(dialogs.size() - 1);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        if (!DialogObject.isEncryptedDialog(j) && j2 != -1) {
            TLRPC.TL_messages_toggleDialogPin tL_messages_toggleDialogPin = new TLRPC.TL_messages_toggleDialogPin();
            tL_messages_toggleDialogPin.pinned = z;
            TLRPC.InputPeer inputPeer2 = inputPeer == null ? getInputPeer(j) : inputPeer;
            if (inputPeer2 instanceof TLRPC.TL_inputPeerEmpty) {
                return false;
            }
            TLRPC.TL_inputDialogPeer tL_inputDialogPeer = new TLRPC.TL_inputDialogPeer();
            tL_inputDialogPeer.peer = inputPeer2;
            tL_messages_toggleDialogPin.peer = tL_inputDialogPeer;
            if (j2 == 0) {
                try {
                    nativeByteBuffer = new NativeByteBuffer(inputPeer2.getObjectSize() + 16);
                    try {
                        nativeByteBuffer.writeInt32(4);
                        nativeByteBuffer.writeInt64(j);
                        nativeByteBuffer.writeBool(z);
                        inputPeer2.serializeToStream(nativeByteBuffer);
                    } catch (Exception e) {
                        e = e;
                        nativeByteBuffer2 = nativeByteBuffer;
                        FileLog.e(e);
                        nativeByteBuffer = nativeByteBuffer2;
                    }
                } catch (Exception e2) {
                    e = e2;
                }
                jCreatePendingTask = getMessagesStorage().createPendingTask(nativeByteBuffer);
            } else {
                jCreatePendingTask = j2;
            }
            getConnectionsManager().sendRequest(tL_messages_toggleDialogPin, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$pinDialog$362(jCreatePendingTask, tLObject, tL_error);
                }
            });
        }
        getMessagesStorage().setDialogPinned(j, dialog.pinnedNum);
        return true;
    }

    public void lambda$loadPinnedDialogs$364(final int i, final ArrayList arrayList, final boolean z, final TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs, final LongSparseArray longSparseArray, final TLRPC.TL_messages_dialogs tL_messages_dialogs) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadPinnedDialogs$363(i, arrayList, z, tL_messages_peerDialogs, longSparseArray, tL_messages_dialogs);
            }
        });
    }

    void lambda$generateJoinMessage$366(long j, ArrayList arrayList) {
        updateInterfaceWithMessages(-j, arrayList, 0);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void deleteMessagesByPush(final long j, final ArrayList<Integer> arrayList, final long j2) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$deleteMessagesByPush$368(arrayList, j2, j);
            }
        });
    }

    public void lambda$deleteMessagesByPush$367(ArrayList arrayList, long j) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDeleted, arrayList, Long.valueOf(j), Boolean.FALSE);
        if (j == 0) {
            int size = arrayList.size();
            for (int i = 0; i < size; i++) {
                MessageObject messageObject = this.dialogMessagesByIds.get(((Integer) arrayList.get(i)).intValue());
                if (messageObject != null) {
                    messageObject.deleted = true;
                }
            }
            return;
        }
        ArrayList<MessageObject> arrayList2 = this.dialogMessage.get(-j);
        if (arrayList2 != null) {
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                MessageObject messageObject2 = arrayList2.get(i2);
                int size2 = arrayList.size();
                for (int i3 = 0; i3 < size2; i3++) {
                    if (messageObject2.getId() == ((Integer) arrayList.get(i3)).intValue()) {
                        messageObject2.deleted = true;
                        break;
                    }
                }
            }
        }
    }

    public void checkChatInviter(final long j, boolean z) {
        final TLRPC.Chat chat = getChat(Long.valueOf(j));
        if (!ChatObject.isChannel(chat) || chat.creator || this.gettingChatInviters.indexOfKey(j) >= 0) {
            return;
        }
        if (ChatObject.isMonoForum(chat)) {
            z = false;
        }
        final boolean z2 = z;
        this.gettingChatInviters.put(j, Boolean.TRUE);
        TLRPC.TL_channels_getParticipant tL_channels_getParticipant = new TLRPC.TL_channels_getParticipant();
        tL_channels_getParticipant.channel = getInputChannel(j);
        tL_channels_getParticipant.participant = getInputPeer(getUserConfig().getClientUserId());
        getConnectionsManager().sendRequest(tL_channels_getParticipant, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$checkChatInviter$373(chat, z2, j, tLObject, tL_error);
            }
        });
    }

    public void lambda$checkChatInviter$369(TLRPC.TL_channels_channelParticipant tL_channels_channelParticipant) {
        putUsers(tL_channels_channelParticipant.users, false);
        putChats(tL_channels_channelParticipant.chats, false);
    }

    public void lambda$checkChatInviter$372(long j, ArrayList arrayList, TLRPC.TL_channels_channelParticipant tL_channels_channelParticipant) {
        this.gettingChatInviters.delete(j);
        if (arrayList != null) {
            updateInterfaceWithMessages(-j, arrayList, 0);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didLoadChatInviter, Long.valueOf(j), Long.valueOf(tL_channels_channelParticipant.participant.inviter_id));
    }

    private int getUpdateType(TLRPC.Update update) {
        if ((update instanceof TL_update.TL_updateNewMessage) || (update instanceof TL_update.TL_updateReadMessagesContents) || (update instanceof TL_update.TL_updateReadHistoryInbox) || (update instanceof TL_update.TL_updateReadHistoryOutbox) || (update instanceof TL_update.TL_updateDeleteMessages) || (update instanceof TL_update.TL_updateWebPage) || (update instanceof TL_update.TL_updateEditMessage) || (update instanceof TL_update.TL_updateFolderPeers) || (update instanceof TL_update.TL_updatePinnedMessages)) {
            return 0;
        }
        if (update instanceof TL_update.TL_updateNewEncryptedMessage) {
            return 1;
        }
        return ((update instanceof TL_update.TL_updateNewChannelMessage) || (update instanceof TL_update.TL_updateDeleteChannelMessages) || (update instanceof TL_update.TL_updateEditChannelMessage) || (update instanceof TL_update.TL_updateChannelWebPage) || (update instanceof TL_update.TL_updatePinnedChannelMessages)) ? 2 : 3;
    }

    private static int getUpdatePts(TLRPC.Update update) {
        if (update instanceof TL_update.TL_updateDeleteMessages) {
            return ((TL_update.TL_updateDeleteMessages) update).pts;
        }
        if (update instanceof TL_update.TL_updateNewChannelMessage) {
            return ((TL_update.TL_updateNewChannelMessage) update).pts;
        }
        if (update instanceof TL_update.TL_updateReadHistoryOutbox) {
            return ((TL_update.TL_updateReadHistoryOutbox) update).pts;
        }
        if (update instanceof TL_update.TL_updateNewMessage) {
            return ((TL_update.TL_updateNewMessage) update).pts;
        }
        if (update instanceof TL_update.TL_updateEditMessage) {
            return ((TL_update.TL_updateEditMessage) update).pts;
        }
        if (update instanceof TL_update.TL_updateWebPage) {
            return ((TL_update.TL_updateWebPage) update).pts;
        }
        if (update instanceof TL_update.TL_updateReadHistoryInbox) {
            return ((TL_update.TL_updateReadHistoryInbox) update).pts;
        }
        if (update instanceof TL_update.TL_updateChannelWebPage) {
            return ((TL_update.TL_updateChannelWebPage) update).pts;
        }
        if (update instanceof TL_update.TL_updateDeleteChannelMessages) {
            return ((TL_update.TL_updateDeleteChannelMessages) update).pts;
        }
        if (update instanceof TL_update.TL_updateEditChannelMessage) {
            return ((TL_update.TL_updateEditChannelMessage) update).pts;
        }
        if (update instanceof TL_update.TL_updateReadMessagesContents) {
            return ((TL_update.TL_updateReadMessagesContents) update).pts;
        }
        if (update instanceof TL_update.TL_updateChannelTooLong) {
            return ((TL_update.TL_updateChannelTooLong) update).pts;
        }
        if (update instanceof TL_update.TL_updateFolderPeers) {
            return ((TL_update.TL_updateFolderPeers) update).pts;
        }
        if (update instanceof TL_update.TL_updatePinnedChannelMessages) {
            return ((TL_update.TL_updatePinnedChannelMessages) update).pts;
        }
        if (update instanceof TL_update.TL_updatePinnedMessages) {
            return ((TL_update.TL_updatePinnedMessages) update).pts;
        }
        return 0;
    }

    private static int getUpdatePtsCount(TLRPC.Update update) {
        if (update instanceof TL_update.TL_updateDeleteMessages) {
            return ((TL_update.TL_updateDeleteMessages) update).pts_count;
        }
        if (update instanceof TL_update.TL_updateNewChannelMessage) {
            return ((TL_update.TL_updateNewChannelMessage) update).pts_count;
        }
        if (update instanceof TL_update.TL_updateReadHistoryOutbox) {
            return ((TL_update.TL_updateReadHistoryOutbox) update).pts_count;
        }
        if (update instanceof TL_update.TL_updateNewMessage) {
            return ((TL_update.TL_updateNewMessage) update).pts_count;
        }
        if (update instanceof TL_update.TL_updateEditMessage) {
            return ((TL_update.TL_updateEditMessage) update).pts_count;
        }
        if (update instanceof TL_update.TL_updateWebPage) {
            return ((TL_update.TL_updateWebPage) update).pts_count;
        }
        if (update instanceof TL_update.TL_updateReadHistoryInbox) {
            return ((TL_update.TL_updateReadHistoryInbox) update).pts_count;
        }
        if (update instanceof TL_update.TL_updateChannelWebPage) {
            return ((TL_update.TL_updateChannelWebPage) update).pts_count;
        }
        if (update instanceof TL_update.TL_updateDeleteChannelMessages) {
            return ((TL_update.TL_updateDeleteChannelMessages) update).pts_count;
        }
        if (update instanceof TL_update.TL_updateEditChannelMessage) {
            return ((TL_update.TL_updateEditChannelMessage) update).pts_count;
        }
        if (update instanceof TL_update.TL_updateReadMessagesContents) {
            return ((TL_update.TL_updateReadMessagesContents) update).pts_count;
        }
        if (update instanceof TL_update.TL_updateFolderPeers) {
            return ((TL_update.TL_updateFolderPeers) update).pts_count;
        }
        if (update instanceof TL_update.TL_updatePinnedChannelMessages) {
            return ((TL_update.TL_updatePinnedChannelMessages) update).pts_count;
        }
        if (update instanceof TL_update.TL_updatePinnedMessages) {
            return ((TL_update.TL_updatePinnedMessages) update).pts_count;
        }
        return 0;
    }

    private static int getUpdateQts(TLRPC.Update update) {
        if (update instanceof TL_update.TL_updateNewEncryptedMessage) {
            return ((TL_update.TL_updateNewEncryptedMessage) update).qts;
        }
        return 0;
    }

    public static long getUpdateChannelId(TLRPC.Update update) {
        if (update instanceof TL_update.TL_updateNewChannelMessage) {
            return ((TL_update.TL_updateNewChannelMessage) update).message.peer_id.channel_id;
        }
        if (update instanceof TL_update.TL_updateEditChannelMessage) {
            return ((TL_update.TL_updateEditChannelMessage) update).message.peer_id.channel_id;
        }
        if (update instanceof TL_update.TL_updateReadChannelOutbox) {
            return ((TL_update.TL_updateReadChannelOutbox) update).channel_id;
        }
        if (update instanceof TL_update.TL_updateChannelMessageViews) {
            return ((TL_update.TL_updateChannelMessageViews) update).channel_id;
        }
        if (update instanceof TL_update.TL_updateChannelMessageForwards) {
            return ((TL_update.TL_updateChannelMessageForwards) update).channel_id;
        }
        if (update instanceof TL_update.TL_updateChannelTooLong) {
            return ((TL_update.TL_updateChannelTooLong) update).channel_id;
        }
        if (update instanceof TL_update.TL_updateChannelReadMessagesContents) {
            return ((TL_update.TL_updateChannelReadMessagesContents) update).channel_id;
        }
        if (update instanceof TL_update.TL_updateChannelAvailableMessages) {
            return ((TL_update.TL_updateChannelAvailableMessages) update).channel_id;
        }
        if (update instanceof TL_update.TL_updateChannel) {
            return ((TL_update.TL_updateChannel) update).channel_id;
        }
        if (update instanceof TL_update.TL_updateChannelWebPage) {
            return ((TL_update.TL_updateChannelWebPage) update).channel_id;
        }
        if (update instanceof TL_update.TL_updateDeleteChannelMessages) {
            return ((TL_update.TL_updateDeleteChannelMessages) update).channel_id;
        }
        if (update instanceof TL_update.TL_updateReadChannelInbox) {
            return ((TL_update.TL_updateReadChannelInbox) update).channel_id;
        }
        if (update instanceof TL_update.TL_updatePinnedForumTopic) {
            return ((TL_update.TL_updatePinnedForumTopic) update).peer.channel_id;
        }
        if (update instanceof TL_update.TL_updatePinnedForumTopics) {
            return ((TL_update.TL_updatePinnedForumTopics) update).peer.channel_id;
        }
        if (update instanceof TL_update.TL_updateReadChannelDiscussionInbox) {
            return ((TL_update.TL_updateReadChannelDiscussionInbox) update).channel_id;
        }
        if (update instanceof TL_update.TL_updateReadChannelDiscussionOutbox) {
            return ((TL_update.TL_updateReadChannelDiscussionOutbox) update).channel_id;
        }
        if (update instanceof TL_update.TL_updateChannelUserTyping) {
            return ((TL_update.TL_updateChannelUserTyping) update).channel_id;
        }
        if (update instanceof TL_update.TL_updatePinnedChannelMessages) {
            return ((TL_update.TL_updatePinnedChannelMessages) update).channel_id;
        }
        if (update instanceof TL_update.TL_updateChannelViewForumAsMessages) {
            return ((TL_update.TL_updateChannelViewForumAsMessages) update).channel_id;
        }
        if (!BuildVars.LOGS_ENABLED) {
            return 0L;
        }
        FileLog.e("trying to get unknown update channel_id for " + update);
        return 0L;
    }

    void lambda$processUpdates$374(boolean z, long j, ArrayList arrayList) {
        if (z) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_USER_PRINT));
        }
        updateInterfaceWithMessages(j, arrayList, 0);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
    }

    public void lambda$processUpdates$378() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_STATUS));
    }

    private boolean applyFoldersUpdates(ArrayList<TL_update.TL_updateFolderPeers> arrayList) {
        if (arrayList == null) {
            return false;
        }
        int size = arrayList.size();
        int i = 0;
        boolean z = false;
        while (i < size) {
            TL_update.TL_updateFolderPeers tL_updateFolderPeers = arrayList.get(i);
            int size2 = tL_updateFolderPeers.folder_peers.size();
            for (int i2 = 0; i2 < size2; i2++) {
                TLRPC.TL_folderPeer tL_folderPeer = tL_updateFolderPeers.folder_peers.get(i2);
                TLRPC.Dialog dialog = this.dialogs_dict.get(DialogObject.getPeerDialogId(tL_folderPeer.peer));
                if (dialog != null) {
                    int i3 = dialog.folder_id;
                    int i4 = tL_folderPeer.folder_id;
                    if (i3 != i4) {
                        dialog.pinned = false;
                        dialog.pinnedNum = 0;
                        dialog.folder_id = i4;
                        this.hasArchivedChats = true;
                        checkArchiveFolder();
                    }
                }
            }
            getMessagesStorage().setDialogsFolderId(arrayList.get(i).folder_peers, null, 0L, 0);
            i++;
            z = true;
        }
        return z;
    }

    public void lambda$processUpdateArray$384(TL_update.TL_updateUserTyping tL_updateUserTyping) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.onEmojiInteractionsReceived, Long.valueOf(tL_updateUserTyping.user_id), tL_updateUserTyping.action);
    }

    public void lambda$processUpdateArray$388(TL_update.TL_updatePeerBlocked tL_updatePeerBlocked) {
        long peerId = MessageObject.getPeerId(tL_updatePeerBlocked.peer_id);
        boolean z = tL_updatePeerBlocked.blocked;
        LongSparseIntArray longSparseIntArray = this.blockePeers;
        if (z) {
            if (longSparseIntArray.indexOfKey(peerId) < 0) {
                this.blockePeers.put(peerId, 1);
            }
        } else {
            longSparseIntArray.delete(peerId);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.blockedUsersDidLoad, new Object[0]);
        getStoriesController().updateBlockUser(peerId, tL_updatePeerBlocked.blocked_my_stories_from, false);
    }

    public void lambda$processUpdateArray$391(TLRPC.Message message) {
        getSendMessagesHelper().onMessageEdited(message);
    }

    public void lambda$processUpdateArray$396(final LongSparseArray longSparseArray) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$processUpdateArray$395(longSparseArray);
            }
        });
    }

    public void lambda$processUpdateArray$404(LongSparseArray longSparseArray) {
        if (longSparseArray != null) {
            int size = longSparseArray.size();
            for (int i = 0; i < size; i++) {
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.didReceiveNewMessages, Long.valueOf(longSparseArray.keyAt(i)), (ArrayList) longSparseArray.valueAt(i), Boolean.FALSE, 0);
            }
        }
    }

    public void lambda$processUpdateArray$408(long j, ArrayList arrayList) {
        getMessagesStorage().updateDialogsWithDeletedMessages(j, -j, arrayList, getMessagesStorage().markMessagesAsDeleted(j, arrayList, false, true, 0, 0));
    }

    public void lambda$checkUnreadReactionsInternal2$413(final long j, final long j2, final ArrayList arrayList, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            final int i = tL_messages_peerDialogs.dialogs.size() != 0 ? tL_messages_peerDialogs.dialogs.get(0).unread_reactions_count : 0;
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$checkUnreadReactionsInternal2$412(j, i, j2, arrayList);
                }
            });
        }
    }

    public void lambda$checkUnreadReactionsInternal2$414(long j, long j2, int i, ArrayList arrayList) {
        getMessagesController().getTopicsController().updateReactionsUnread(j, j2, i, false);
        getMessagesStorage().updateUnreadReactionsCount(j, j2, i);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsUnreadReactionsCounterChanged, Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(i), arrayList);
    }

    public void lambda$checkUnreadReactionsInternal2$416(long j, long j2, int i, ArrayList arrayList) {
        getMessagesController().getTopicsController().updateReactionsUnread(j, j2, i, false);
        getMessagesStorage().updateUnreadReactionsCount(j, j2, i);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsUnreadReactionsCounterChanged, Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(i), arrayList);
    }

    public void lambda$checkUnreadPollVotesInternal2$420(final long j, final long j2, final ArrayList arrayList, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            TLRPC.TL_messages_peerDialogs tL_messages_peerDialogs = (TLRPC.TL_messages_peerDialogs) tLObject;
            final int i = tL_messages_peerDialogs.dialogs.size() != 0 ? tL_messages_peerDialogs.dialogs.get(0).unread_poll_votes_count : 0;
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$checkUnreadPollVotesInternal2$419(j, i, j2, arrayList);
                }
            });
        }
    }

    public void lambda$checkUnreadPollVotesInternal2$421(long j, long j2, int i, ArrayList arrayList) {
        getMessagesController().getTopicsController().updatePollVotesUnread(j, j2, i, false);
        getMessagesStorage().updateUnreadPollVotesCount(j, j2, i);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsUnreadPollVotesCounterChanged, Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(i), arrayList);
    }

    public void lambda$checkUnreadPollVotesInternal2$423(long j, long j2, int i, ArrayList arrayList) {
        getMessagesController().getTopicsController().updatePollVotesUnread(j, j2, i, false);
        getMessagesStorage().updateUnreadPollVotesCount(j, j2, i);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsUnreadPollVotesCounterChanged, Long.valueOf(j), Long.valueOf(j2), Integer.valueOf(i), arrayList);
    }

    public void lambda$getSponsoredMessages$428(TLRPC.messages_SponsoredMessages messages_sponsoredmessages) {
        putUsers(messages_sponsoredmessages.users, false);
        putChats(messages_sponsoredmessages.chats, false);
    }

    public void lambda$getSendAsPeers$433(final LongSparseArray longSparseArray, final long j, final SendAsPeersInfo sendAsPeersInfo, final boolean z, TLObject tLObject, TLRPC.TL_error tL_error) {
        TLRPC.TL_channels_sendAsPeers tL_channels_sendAsPeers = null;
        if (tLObject != null) {
            final TLRPC.TL_channels_sendAsPeers tL_channels_sendAsPeers2 = (TLRPC.TL_channels_sendAsPeers) tLObject;
            if (!tL_channels_sendAsPeers2.peers.isEmpty()) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$getSendAsPeers$431(tL_channels_sendAsPeers2);
                    }
                });
                tL_channels_sendAsPeers = tL_channels_sendAsPeers2;
            }
        }
        final TLRPC.TL_channels_sendAsPeers tL_channels_sendAsPeers3 = tL_channels_sendAsPeers;
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getSendAsPeers$432(tL_channels_sendAsPeers3, longSparseArray, j, sendAsPeersInfo, z);
            }
        });
    }

    public void lambda$updateInterfaceWithMessages$434(TLRPC.Dialog dialog, int i, long j, int i2) {
        if (i2 == -1) {
            if (i <= 0 || DialogObject.isEncryptedDialog(j)) {
                return;
            }
            loadUnknownDialog(getInputPeer(j), 0L);
            return;
        }
        if (i2 != 0) {
            dialog.folder_id = i2;
            sortDialogs(null);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
        }
    }

    public TLRPC.Dialog getDialog(long j) {
        return this.dialogs_dict.get(j);
    }

    public void addDialogAction(long j, boolean z) {
        TLRPC.Dialog dialog = this.dialogs_dict.get(j);
        if (dialog == null) {
            return;
        }
        if (z) {
            this.clearingHistoryDialogs.put(j, dialog);
        } else {
            this.deletingDialogs.put(j, dialog);
            this.allDialogs.remove(dialog);
            sortDialogs(null);
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    public void removeDialogAction(long j, boolean z, boolean z2) {
        TLRPC.Dialog dialog = this.dialogs_dict.get(j);
        if (dialog == null) {
            return;
        }
        if (z) {
            this.clearingHistoryDialogs.remove(j);
        } else {
            this.deletingDialogs.remove(j);
            if (!z2) {
                this.allDialogs.add(dialog);
                sortDialogs(null);
            }
        }
        if (z2) {
            return;
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, Boolean.TRUE);
    }

    public boolean isClearingDialog(long j) {
        return this.clearingHistoryDialogs.get(j) != null;
    }

    public void updateFilterDialogs(DialogFilter dialogFilter) {
        int size;
        int i;
        TLRPC.EncryptedChat encryptedChat;
        TLRPC.Chat chat;
        if (dialogFilter == null) {
            return;
        }
        ArrayList<TLRPC.Dialog> arrayList = dialogFilter.dialogs;
        ArrayList<TLRPC.Dialog> arrayList2 = dialogFilter.dialogsForward;
        arrayList.clear();
        arrayList2.clear();
        this.sortingDialogFilter = dialogFilter;
        try {
            Collections.sort(this.allDialogs, this.dialogDateComparator);
            while (true) {
                ArrayList<TLRPC.Dialog> arrayList3 = this.allDialogs;
                if (i < size) {
                    TLRPC.Dialog dialog = arrayList3.get(i);
                    boolean z = dialog instanceof TLRPC.TL_dialogCommunity;
                    if ((dialog instanceof TLRPC.TL_dialog) || z) {
                        long j = dialog.id;
                        if (!z || (chat = getChat(Long.valueOf(-j))) == null || chat.collapsed_in_dialogs) {
                            if (DialogObject.isEncryptedDialog(j) && (encryptedChat = getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(j)))) != null) {
                                j = encryptedChat.user_id;
                            }
                            if (dialogFilter.includesDialog(getAccountInstance(), j, dialog)) {
                                if (canAddToForward(dialog)) {
                                    arrayList2.add(dialog);
                                }
                                arrayList.add(dialog);
                            }
                        }
                    }
                    i++;
                } else {
                    try {
                        Collections.sort(arrayList3, this.dialogComparator);
                        return;
                    } catch (Exception unused) {
                        return;
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        size = this.allDialogs.size();
        i = 0;
    }

    public boolean canAddToForward(TLRPC.Dialog dialog) {
        if (dialog == null) {
            return false;
        }
        if (DialogObject.isEncryptedDialog(dialog.id)) {
            return true;
        }
        boolean zIsChannel = DialogObject.isChannel(dialog);
        long j = dialog.id;
        if (!zIsChannel) {
            if (j > 0) {
                if (ChatObject.isUserCollapsedInCommunity(this.currentAccount, getUser(Long.valueOf(j)))) {
                    return false;
                }
            }
            return true;
        }
        TLRPC.Chat chat = getChat(Long.valueOf(-j));
        if (ChatObject.isChatCollapsedInCommunity(this.currentAccount, chat)) {
            return false;
        }
        if (chat == null || !chat.megagroup) {
            return ChatObject.hasAdminRights(chat) && ChatObject.canPost(chat);
        }
        return !chat.gigagroup || ChatObject.hasAdminRights(chat);
    }

    private void checkCollapsedDialogsInCommunity() {
        this.dialogsCommunityLastMessageDate.clear();
        this.dialogsCommunityUnreadCount.clear();
        this.dialogsCommunityFoundCommunities.clear();
        this.dialogsCommunityUnreadMark.clear();
        int size = this.allDialogs.size();
        int i = 0;
        while (i < size) {
            TLRPC.Dialog dialog = this.allDialogs.get(i);
            if (dialog instanceof TLRPC.TL_dialogCommunity) {
                long j = dialog.community_id;
                TLRPC.Chat chat = getChat(Long.valueOf(j));
                if ((chat != null && !chat.collapsed_in_dialogs) || this.dialogsCommunityFoundCommunities.get(j) != null) {
                    this.allDialogs.remove(i);
                    i--;
                    size--;
                } else {
                    this.dialogsCommunityFoundCommunities.put(j, dialog);
                }
            } else {
                long j2 = dialog.id;
                if (j2 < 0) {
                    TLRPC.Chat chat2 = getChat(Long.valueOf(-j2));
                    if (ChatObject.isCommunity(chat2)) {
                        this.allDialogs.remove(i);
                        i--;
                        size--;
                    } else if (ChatObject.isChatCollapsedInCommunity(this.currentAccount, chat2)) {
                        long j3 = chat2.linked_community_id;
                        LongSparseIntArray longSparseIntArray = this.dialogsCommunityLastMessageDate;
                        longSparseIntArray.put(j3, Math.max(longSparseIntArray.get(j3), dialog.last_message_date));
                        LongSparseIntArray longSparseIntArray2 = this.dialogsCommunityUnreadCount;
                        longSparseIntArray2.put(j3, longSparseIntArray2.get(j3) + getDialogUnreadCount(dialog));
                        if (dialog.unread_mark) {
                            this.dialogsCommunityUnreadMark.put(j3, 1);
                        }
                    }
                } else if (j2 > 0) {
                    TLRPC.User user = getUser(Long.valueOf(j2));
                    if (ChatObject.isUserCollapsedInCommunity(this.currentAccount, user)) {
                        long j4 = user.linked_community_id;
                        LongSparseIntArray longSparseIntArray3 = this.dialogsCommunityLastMessageDate;
                        longSparseIntArray3.put(j4, Math.max(longSparseIntArray3.get(j4), dialog.last_message_date));
                        LongSparseIntArray longSparseIntArray4 = this.dialogsCommunityUnreadCount;
                        longSparseIntArray4.put(j4, longSparseIntArray4.get(j4) + getDialogUnreadCount(dialog));
                        if (dialog.unread_mark) {
                            this.dialogsCommunityUnreadMark.put(j4, 1);
                        }
                    }
                }
            }
            i++;
        }
        int size2 = this.dialogsCommunityLastMessageDate.size();
        TLRPC.TL_messages_dialogs tL_messages_dialogs = null;
        for (int i2 = 0; i2 < size2; i2++) {
            long jKeyAt = this.dialogsCommunityLastMessageDate.keyAt(i2);
            int iValueAt = this.dialogsCommunityLastMessageDate.valueAt(i2);
            int i3 = this.dialogsCommunityUnreadCount.get(jKeyAt);
            boolean z = this.dialogsCommunityUnreadMark.get(jKeyAt) > 0;
            TLRPC.Dialog dialog2 = this.dialogsCommunityFoundCommunities.get(jKeyAt);
            if (dialog2 != null) {
                dialog2.unread_count = i3;
                dialog2.last_message_date = iValueAt;
                dialog2.unread_mark = z;
            } else {
                long j5 = -jKeyAt;
                TLRPC.Dialog dialog3 = this.dialogs_dict.get(j5);
                if (dialog3 instanceof TLRPC.TL_dialogCommunity) {
                    dialog3.unread_count = i3;
                    dialog3.last_message_date = iValueAt;
                    dialog3.unread_mark = z;
                    this.allDialogs.add(dialog3);
                } else {
                    TLRPC.TL_dialogCommunity tL_dialogCommunity = new TLRPC.TL_dialogCommunity();
                    tL_dialogCommunity.community_id = jKeyAt;
                    if (dialog3 != null) {
                        tL_dialogCommunity.pinned = dialog3.pinned;
                        tL_dialogCommunity.pinnedNum = dialog3.pinnedNum;
                        tL_dialogCommunity.notify_settings = dialog3.notify_settings;
                    } else {
                        tL_dialogCommunity.notify_settings = new TLRPC.TL_peerNotifySettings();
                    }
                    tL_dialogCommunity.unread_mark = z;
                    tL_dialogCommunity.last_message_date = iValueAt;
                    tL_dialogCommunity.unread_count = i3;
                    DialogObject.initDialog(tL_dialogCommunity);
                    this.dialogs_dict.put(j5, tL_dialogCommunity);
                    this.allDialogs.add(tL_dialogCommunity);
                    if (dialog3 == null) {
                        if (tL_messages_dialogs == null) {
                            tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
                        }
                        tL_messages_dialogs.dialogs.add(tL_dialogCommunity);
                    }
                }
            }
        }
        if (tL_messages_dialogs != null) {
            getMessagesStorage().putDialogs(tL_messages_dialogs, 2);
        }
    }

    public void checkSensitive(final BaseFragment baseFragment, final long j, final Runnable runnable, final Runnable runnable2) {
        TLRPC.User user;
        ArrayList<TLRPC.RestrictionReason> arrayList;
        if (j < 0) {
            TLRPC.Chat chat = getChat(Long.valueOf(-j));
            if (chat != null) {
                arrayList = chat.restriction_reason;
            } else {
                arrayList = null;
            }
        } else if (j < 0 || (user = getUser(Long.valueOf(j))) == null) {
            arrayList = null;
        } else {
            arrayList = user.restriction_reason;
        }
        if (!isSensitive(arrayList) || this.sensitiveAgreed.contains(Long.valueOf(j))) {
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        Context context = baseFragment.getContext();
        if (context == null) {
            context = AndroidUtilities.findActivity(LaunchActivity.instance);
        }
        if (context == null) {
            context = LaunchActivity.instance;
        }
        if (context == null) {
            context = ApplicationLoader.applicationContext;
        }
        final AlertDialog alertDialog = context != null ? new AlertDialog(context, 3) : null;
        if (alertDialog != null) {
            alertDialog.showDelayed(200L);
        }
        getContentSettings(new Utilities.Callback() { 
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$checkSensitive$440(alertDialog, baseFragment, runnable2, j, runnable, (TL_account.contentSettings) obj);
            }
        });
    }

    public void $r8$lambda$sefvbCzRI1fr36ijdzWmRRm7xfk(boolean[] zArr, View view) {
        boolean z = !zArr[0];
        zArr[0] = z;
        ((CheckBoxCell) view).setChecked(z, true);
    }

    public static void lambda$checkSensitive$438(long j, boolean[] zArr, Runnable runnable, Boolean bool) {
        final BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (!bool.booleanValue()) {
            if (safeLastFragment != null) {
                BulletinFactory.of(safeLastFragment).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.AgeVerificationFailedTitle), LocaleController.getString(R.string.AgeVerificationFailedText)).show();
                return;
            }
            return;
        }
        this.sensitiveAgreed.add(Long.valueOf(j));
        setContentSettings(true);
        if (safeLastFragment != null) {
            BulletinFactory.of(safeLastFragment).createSimpleBulletinDetail(R.raw.chats_infotip, AndroidUtilities.replaceArrows(AndroidUtilities.premiumText(LocaleController.getString(R.string.SensitiveContentSettingsToast), new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.m4826$r8$lambda$oEXNTRR9csKTpSsMcqpaQmhldk(safeLastFragment);
                }
            }), true)).show(true);
        }
        zArr[0] = true;
        if (runnable != null) {
            runnable.run();
        }
    }

    public static void lambda$checkCanOpenChat$442(final AlertDialog alertDialog, final Browser.Progress progress, final BaseFragment baseFragment, final Bundle bundle, final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$checkCanOpenChat$441(alertDialog, progress, tLObject, baseFragment, bundle);
                }
            });
        }
    }

    void lambda$checkCanOpenChat$443(int i, BaseFragment baseFragment, DialogInterface dialogInterface) {
        getConnectionsManager().cancelRequest(i, true);
        baseFragment.setVisibleDialog(null);
    }

    public void lambda$openByUserName$445(Browser.Progress progress, AlertDialog[] alertDialogArr, BaseFragment baseFragment, boolean[] zArr, int i, Long l) {
        try {
            if (progress != null) {
                progress.end();
            } else {
                alertDialogArr[0].dismiss();
            }
        } catch (Exception unused) {
        }
        alertDialogArr[0] = null;
        baseFragment.setVisibleDialog(null);
        if (zArr[0]) {
            return;
        }
        if (l != null) {
            if (l.longValue() < 0) {
                openChatOrProfileWith(null, getChat(Long.valueOf(-l.longValue())), baseFragment, 1, false);
                return;
            } else {
                openChatOrProfileWith(getUser(l), null, baseFragment, i, false);
                return;
            }
        }
        if (baseFragment.getParentActivity() != null) {
            try {
                if (baseFragment instanceof ChatActivity) {
                    ((ChatActivity) baseFragment).shakeContent();
                }
                BulletinFactory.of(baseFragment).createErrorBulletin(LocaleController.getString(R.string.NoUsernameFound)).show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public static void $r8$lambda$_Go08XldHBuCuop5XwQwZYocAfw(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    public Runnable ensureMessagesLoaded(final long j, int i, final MessagesLoadedCallback messagesLoadedCallback) {
        int i2;
        final MessagesController messagesController;
        SharedPreferences notificationsSettings = getNotificationsSettings(this.currentAccount);
        if (i == 0) {
            i2 = notificationsSettings.getInt("diditem" + j, 0);
        } else {
            i2 = i;
        }
        final int iGenerateClassGuid = ConnectionsManager.generateClassGuid();
        long j2 = DialogObject.isChatDialog(j) ? -j : 0L;
        if (j2 != 0 && getMessagesController().getChat(Long.valueOf(j2)) == null) {
            final boolean[] zArr = new boolean[1];
            final Runnable[] runnableArr = new Runnable[1];
            final MessagesStorage messagesStorage = getMessagesStorage();
            final long j3 = j2;
            final int i3 = i2;
            messagesStorage.getStorageQueue().postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$ensureMessagesLoaded$450(zArr, messagesStorage, j3, runnableArr, j, i3, messagesLoadedCallback);
                }
            });
            return new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.$r8$lambda$SfYJMtxIMxU3oX4Luhj1RczYXv8(zArr, runnableArr);
                }
            };
        }
        final int i4 = AndroidUtilities.isTablet() ? 30 : 20;
        final int i5 = i2;
        NotificationCenter.NotificationCenterDelegate notificationCenterDelegate = new NotificationCenter.NotificationCenterDelegate() { 
            @Override 
            public void didReceivedNotification(int i6, int i7, Object... objArr) {
                int i8 = NotificationCenter.messagesDidLoadWithoutProcess;
                if (i6 == i8 && ((Integer) objArr[0]).intValue() == iGenerateClassGuid) {
                    int iIntValue = ((Integer) objArr[1]).intValue();
                    boolean zBooleanValue = ((Boolean) objArr[2]).booleanValue();
                    boolean zBooleanValue2 = ((Boolean) objArr[3]).booleanValue();
                    int iIntValue2 = ((Integer) objArr[4]).intValue();
                    int i9 = i4;
                    if (iIntValue < i9 / 2 && !zBooleanValue2 && zBooleanValue) {
                        int i10 = i5;
                        MessagesController messagesController2 = MessagesController.this;
                        if (i10 != 0) {
                            messagesController2.loadMessagesInternal(j, 0L, false, i9, i10, 0, false, 0, iGenerateClassGuid, 3, iIntValue2, 0, 0L, -1, 0, 0, 0, false, 0, true, false, false, null, 0L);
                            return;
                        } else {
                            messagesController2.loadMessagesInternal(j, 0L, false, i9, i10, 0, false, 0, iGenerateClassGuid, 2, iIntValue2, 0, 0L, -1, 0, 0, 0, false, 0, true, false, false, null, 0L);
                            return;
                        }
                    }
                    MessagesController.this.getNotificationCenter().removeObserver(this, i8);
                    MessagesController.this.getNotificationCenter().removeObserver(this, NotificationCenter.loadingMessagesFailed);
                    MessagesLoadedCallback messagesLoadedCallback2 = messagesLoadedCallback;
                    if (messagesLoadedCallback2 != null) {
                        messagesLoadedCallback2.onMessagesLoaded(zBooleanValue);
                        return;
                    }
                    return;
                }
                int i11 = NotificationCenter.loadingMessagesFailed;
                if (i6 == i11 && ((Integer) objArr[0]).intValue() == iGenerateClassGuid) {
                    MessagesController.this.getNotificationCenter().removeObserver(this, i8);
                    MessagesController.this.getNotificationCenter().removeObserver(this, i11);
                    MessagesLoadedCallback messagesLoadedCallback3 = messagesLoadedCallback;
                    if (messagesLoadedCallback3 != null) {
                        messagesLoadedCallback3.onError();
                    }
                }
            }
        };
        getNotificationCenter().addObserver(notificationCenterDelegate, NotificationCenter.messagesDidLoadWithoutProcess);
        getNotificationCenter().addObserver(notificationCenterDelegate, NotificationCenter.loadingMessagesFailed);
        if (i5 != 0) {
            messagesController = this;
            messagesController.loadMessagesInternal(j, 0L, true, i4, i5, 0, true, 0, iGenerateClassGuid, 3, 0, 0, 0L, -1, 0, 0, 0, false, 0, true, false, false, null, 0L);
        } else {
            messagesController = this;
            messagesController.loadMessagesInternal(j, 0L, true, i4, i5, 0, true, 0, iGenerateClassGuid, 2, 0, 0, 0L, -1, 0, 0, 0, false, 0, true, false, false, null, 0L);
        }
        return new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$ensureMessagesLoaded$452(iGenerateClassGuid);
            }
        };
    }

    public void lambda$ensureMessagesLoaded$449(boolean[] zArr, TLRPC.Chat chat, Runnable[] runnableArr, long j, int i, MessagesLoadedCallback messagesLoadedCallback) {
        if (zArr[0]) {
            return;
        }
        if (chat != null) {
            getMessagesController().putChat(chat, true);
            runnableArr[0] = ensureMessagesLoaded(j, i, messagesLoadedCallback);
        } else if (messagesLoadedCallback != null) {
            messagesLoadedCallback.onError();
        }
    }

    public static void lambda$deleteMessagesRange$456(final long j, final int i, final int i2, final long j2, final boolean z, final Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error == null) {
            final TLRPC.TL_messages_affectedHistory tL_messages_affectedHistory = (TLRPC.TL_messages_affectedHistory) tLObject;
            processNewDifferenceParams(-1, tL_messages_affectedHistory.pts, -1, tL_messages_affectedHistory.pts_count);
            getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$deleteMessagesRange$454(j, i, i2, j2, tL_messages_affectedHistory, z, runnable);
                }
            });
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                runnable.run();
            }
        });
    }

    public void lambda$deleteMessagesRange$453(ArrayList arrayList, long j, TLRPC.TL_messages_affectedHistory tL_messages_affectedHistory, long j2, int i, int i2, boolean z, Runnable runnable) {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.messagesDeleted, arrayList, Long.valueOf(j), Boolean.FALSE);
        if (tL_messages_affectedHistory.offset > 0) {
            deleteMessagesRange(j2, j, i, i2, z, runnable);
        } else {
            runnable.run();
        }
    }

    public void setCustomChatReactions(final long j, int i, List<TLRPC.Reaction> list, int i2, Boolean bool, final Utilities.Callback<TLRPC.TL_error> callback, final Runnable runnable) {
        final TLRPC.TL_messages_setChatAvailableReactions tL_messages_setChatAvailableReactions = new TLRPC.TL_messages_setChatAvailableReactions();
        tL_messages_setChatAvailableReactions.peer = getInputPeer(-j);
        if (i == 2 || list.isEmpty()) {
            tL_messages_setChatAvailableReactions.available_reactions = new TLRPC.TL_chatReactionsNone();
        } else if (i == 0) {
            tL_messages_setChatAvailableReactions.available_reactions = new TLRPC.TL_chatReactionsAll();
        } else {
            TLRPC.TL_chatReactionsSome tL_chatReactionsSome = new TLRPC.TL_chatReactionsSome();
            tL_messages_setChatAvailableReactions.available_reactions = tL_chatReactionsSome;
            tL_chatReactionsSome.reactions.addAll(list);
        }
        if (bool != null) {
            tL_messages_setChatAvailableReactions.flags |= 2;
            tL_messages_setChatAvailableReactions.paid_enabled = bool.booleanValue();
        }
        tL_messages_setChatAvailableReactions.flags |= 1;
        tL_messages_setChatAvailableReactions.reactions_limit = i2;
        getConnectionsManager().sendRequest(tL_messages_setChatAvailableReactions, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$setCustomChatReactions$459(j, tL_messages_setChatAvailableReactions, runnable, callback, tLObject, tL_error);
            }
        });
        TLRPC.ChatFull chatFull = getChatFull(j);
        if (chatFull != null) {
            if (chatFull instanceof TLRPC.TL_channelFull) {
                chatFull.flags2 |= 8192;
            } else {
                chatFull.flags |= 1048576;
            }
            chatFull.reactions_limit = i2;
            if (bool != null) {
                chatFull.paid_reactions_available = bool.booleanValue();
            }
            getMessagesStorage().updateChatInfo(chatFull, false);
        }
    }

    public void lambda$setCustomChatReactions$457(Runnable runnable, long j) {
        if (runnable != null) {
            runnable.run();
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatAvailableReactionsUpdated, Long.valueOf(j), 0L);
    }

    public static void lambda$setChatReactions$461(final long j, TLRPC.TL_messages_setChatAvailableReactions tL_messages_setChatAvailableReactions, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
            TLRPC.ChatFull chatFull = getChatFull(j);
            if (chatFull != null) {
                if (chatFull instanceof TLRPC.TL_chatFull) {
                    chatFull.flags |= 262144;
                }
                if (chatFull instanceof TLRPC.TL_channelFull) {
                    chatFull.flags |= TLObject.FLAG_30;
                }
                chatFull.available_reactions = tL_messages_setChatAvailableReactions.available_reactions;
                getMessagesStorage().updateChatInfo(chatFull, false);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setChatReactions$460(j);
                }
            });
        }
    }

    public void $r8$lambda$gNUDyNCB99LXROqi0r_rvVhxmhc(Utilities.Callback callback, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (callback != null) {
            callback.run(tLObject instanceof TLRPC.TL_channels_channelParticipant ? ((TLRPC.TL_channels_channelParticipant) tLObject).participant : null);
        }
    }

    public void checkIsInChat(boolean z, TLRPC.Chat chat, TLRPC.User user, final IsInChatCheckedCallback isInChatCheckedCallback) {
        TLRPC.ChatFull chatFull;
        TLRPC.ChatParticipant chatParticipant;
        ArrayList<TLRPC.ChatParticipant> arrayList;
        TLRPC.ChatParticipant chatParticipant2;
        ArrayList<TLRPC.ChatParticipant> arrayList2;
        int i = 0;
        if (chat == null || user == null) {
            if (isInChatCheckedCallback != null) {
                isInChatCheckedCallback.run(false, null, null);
                return;
            }
            return;
        }
        if (chat.megagroup || ChatObject.isChannel(chat)) {
            if (z && (chatFull = getChatFull(chat.id)) != null) {
                TLRPC.ChatParticipants chatParticipants = chatFull.participants;
                if (chatParticipants != null && (arrayList = chatParticipants.participants) != null) {
                    int size = arrayList.size();
                    while (true) {
                        if (i < size) {
                            chatParticipant = chatFull.participants.participants.get(i);
                            if (chatParticipant != null && chatParticipant.user_id == user.id) {
                                break;
                            } else {
                                i++;
                            }
                        } else {
                            chatParticipant = null;
                            break;
                        }
                    }
                } else {
                    chatParticipant = null;
                    break;
                }
                if (isInChatCheckedCallback != null && chatParticipant != null) {
                    TLRPC.ChatParticipants chatParticipants2 = chatFull.participants;
                    isInChatCheckedCallback.run(true, (chatParticipants2 == null || chatParticipants2.admin_id != user.id) ? null : ChatRightsEditActivity.emptyAdminRights(true), null);
                    return;
                }
            }
            TLRPC.TL_channels_getParticipant tL_channels_getParticipant = new TLRPC.TL_channels_getParticipant();
            tL_channels_getParticipant.channel = getInputChannel(chat.id);
            tL_channels_getParticipant.participant = getInputPeer(user);
            getConnectionsManager().sendRequest(tL_channels_getParticipant, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.$r8$lambda$SPnplTTrAsVcbT2w1tetcQ1rJwc(isInChatCheckedCallback, tLObject, tL_error);
                }
            });
            return;
        }
        TLRPC.ChatFull chatFull2 = getChatFull(chat.id);
        if (chatFull2 == null) {
            if (isInChatCheckedCallback != null) {
                isInChatCheckedCallback.run(false, null, null);
                return;
            }
            return;
        }
        TLRPC.ChatParticipants chatParticipants3 = chatFull2.participants;
        if (chatParticipants3 != null && (arrayList2 = chatParticipants3.participants) != null) {
            int size2 = arrayList2.size();
            int i2 = 0;
            while (true) {
                if (i2 < size2) {
                    chatParticipant2 = chatFull2.participants.participants.get(i2);
                    if (chatParticipant2 != null && chatParticipant2.user_id == user.id) {
                        break;
                    } else {
                        i2++;
                    }
                } else {
                    chatParticipant2 = null;
                    break;
                }
            }
        } else {
            chatParticipant2 = null;
            break;
        }
        if (isInChatCheckedCallback != null) {
            boolean z2 = chatParticipant2 != null;
            TLRPC.ChatParticipants chatParticipants4 = chatFull2.participants;
            isInChatCheckedCallback.run(z2, (chatParticipants4 == null || chatParticipants4.admin_id != user.id) ? null : ChatRightsEditActivity.emptyAdminRights(true), null);
        }
    }

    public static void lambda$updateEmojiStatusUntil$464() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.updateInterfaces, Integer.valueOf(UPDATE_MASK_EMOJI_STATUS));
        updateEmojiStatusUntil();
    }

    public String getMutedString(long j, long j2) {
        if (getMessagesController().isDialogMuted(j, j2)) {
            int i = this.notificationsPreferences.getInt(NotificationsSettingsFacade.PROPERTY_NOTIFY_UNTIL + NotificationsController.getSharedPrefKey(j, j2), 0);
            if (i >= getConnectionsManager().getCurrentTime()) {
                return LocaleController.formatString("NotificationsMutedForHint", R.string.NotificationsMutedForHint, LocaleController.formatTTLString(i));
            }
            return LocaleController.getString(R.string.NotificationsMuted);
        }
        return LocaleController.getString(R.string.NotificationsUnmuted);
    }

    public int getDialogUnreadCount(TLRPC.Dialog dialog) {
        if (dialog == null) {
            return 0;
        }
        int i = dialog.unread_count;
        TLRPC.Chat chat = getChat(Long.valueOf(-dialog.id));
        return (chat == null || !(chat.forum || chat.monoforum)) ? i : this.topicsController.getForumUnreadCount(-dialog.id)[0];
    }

    public TLRPC.TL_exportedContactToken getCachedContactToken() {
        TLRPC.TL_exportedContactToken tL_exportedContactToken = this.cachedContactToken;
        if (tL_exportedContactToken == null || tL_exportedContactToken.expires <= System.currentTimeMillis() / 1000) {
            return null;
        }
        return this.cachedContactToken;
    }

    public void requestContactToken(Utilities.Callback<TLRPC.TL_exportedContactToken> callback) {
        requestContactToken(0L, callback);
    }

    public void requestContactToken(final long j, final Utilities.Callback<TLRPC.TL_exportedContactToken> callback) {
        if (callback == null || this.requestingContactToken) {
            return;
        }
        TLRPC.TL_exportedContactToken tL_exportedContactToken = this.cachedContactToken;
        if (tL_exportedContactToken != null && tL_exportedContactToken.expires > System.currentTimeMillis() / 1000) {
            callback.run(this.cachedContactToken);
            return;
        }
        this.requestingContactToken = true;
        final long jCurrentTimeMillis = System.currentTimeMillis();
        getConnectionsManager().sendRequest(new TLRPC.TL_contacts_exportContactToken(), new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$requestContactToken$466(callback, j, jCurrentTimeMillis, tLObject, tL_error);
            }
        });
    }

    public void lambda$requestContactToken$465(Utilities.Callback callback) {
        callback.run(this.cachedContactToken);
        this.requestingContactToken = false;
    }

    public CacheByChatsController getCacheByChatsController() {
        return this.cacheByChatsController;
    }

    public int getFilterIdByDialogsType(int i) {
        if (i != 7 && i != 8) {
            return 0;
        }
        DialogFilter dialogFilter = this.selectedDialogFilter[i - 7];
        if (dialogFilter == null) {
            return -1;
        }
        return dialogFilter.id;
    }

    public void invalidateChatlistFolderUpdate(int i) {
        this.chatlistFoldersUpdates.remove(i);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatlistFolderUpdate, Integer.valueOf(i));
    }

    public void checkChatlistFolderUpdate(final int i, boolean z) {
        if (i < 0) {
            return;
        }
        final ChatlistUpdatesStat chatlistUpdatesStat = this.chatlistFoldersUpdates.get(i);
        if (chatlistUpdatesStat != null) {
            if (chatlistUpdatesStat.loading) {
                return;
            }
            if (System.currentTimeMillis() - chatlistUpdatesStat.lastRequestTime <= ((long) this.chatlistUpdatePeriod) * 1000 && !z) {
                return;
            }
        }
        if (chatlistUpdatesStat == null) {
            chatlistUpdatesStat = new ChatlistUpdatesStat();
            this.chatlistFoldersUpdates.put(i, chatlistUpdatesStat);
        }
        chatlistUpdatesStat.loading = false;
        TL_chatlists.TL_chatlists_getChatlistUpdates tL_chatlists_getChatlistUpdates = new TL_chatlists.TL_chatlists_getChatlistUpdates();
        TL_chatlists.TL_inputChatlistDialogFilter tL_inputChatlistDialogFilter = new TL_chatlists.TL_inputChatlistDialogFilter();
        tL_chatlists_getChatlistUpdates.chatlist = tL_inputChatlistDialogFilter;
        tL_inputChatlistDialogFilter.filter_id = i;
        getConnectionsManager().sendRequest(tL_chatlists_getChatlistUpdates, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$checkChatlistFolderUpdate$468(i, chatlistUpdatesStat, tLObject, tL_error);
            }
        });
    }

    public void lambda$checkChatlistFolderUpdate$467(TLObject tLObject, int i, ChatlistUpdatesStat chatlistUpdatesStat) {
        if (tLObject instanceof TL_chatlists.TL_chatlists_chatlistUpdates) {
            TL_chatlists.TL_chatlists_chatlistUpdates tL_chatlists_chatlistUpdates = (TL_chatlists.TL_chatlists_chatlistUpdates) tLObject;
            putChats(tL_chatlists_chatlistUpdates.chats, false);
            putUsers(tL_chatlists_chatlistUpdates.users, false);
            this.chatlistFoldersUpdates.put(i, new ChatlistUpdatesStat(tL_chatlists_chatlistUpdates));
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.chatlistFolderUpdate, Integer.valueOf(i));
            return;
        }
        chatlistUpdatesStat.loading = false;
    }

    public TL_chatlists.TL_chatlists_chatlistUpdates getChatlistFolderUpdates(int i) {
        ChatlistUpdatesStat chatlistUpdatesStat = this.chatlistFoldersUpdates.get(i);
        if (chatlistUpdatesStat == null) {
            return null;
        }
        return chatlistUpdatesStat.lastValue;
    }

    public Pair<Runnable, Runnable> removeFolderTemporarily(final int i, ArrayList<Long> arrayList) {
        this.frozenDialogFilters = new ArrayList<>(this.dialogFilters);
        int i2 = 0;
        while (i2 < this.frozenDialogFilters.size()) {
            if (this.frozenDialogFilters.get(i2).id == i) {
                this.frozenDialogFilters.remove(i2);
                i2--;
            }
            i2++;
        }
        this.hiddenUndoChats.clear();
        if (arrayList != null) {
            this.hiddenUndoChats.addAll(arrayList);
        }
        boolean zIsEmpty = this.hiddenUndoChats.isEmpty();
        final boolean z = !zIsEmpty;
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        if (!zIsEmpty) {
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogsNeedReload, new Object[0]);
        }
        return new Pair<>(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$removeFolderTemporarily$469(i, z);
            }
        }, new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$removeFolderTemporarily$470(z);
            }
        });
    }

    public void lambda$getChannelRecommendations$472(final boolean z, final long j, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getChannelRecommendations$471(tLObject, z, j);
            }
        });
    }

    public public boolean updateSavedReactionTags(long j, ReactionsLayoutInBubble.VisibleReaction visibleReaction, boolean z, boolean z2) {
        if (this.reactionTags == null) {
            return false;
        }
        int i = 0;
        boolean z3 = false;
        while (i < 2) {
            long j2 = i == 0 ? 0L : j;
            boolean z4 = true;
            if (i != 1 || j2 != 0) {
                TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags = this.reactionTags.get(j2);
                if (tL_messages_savedReactionsTags == null) {
                    if (j2 != 0) {
                        LongSparseArray<TLRPC.TL_messages_savedReactionsTags> longSparseArray = this.reactionTags;
                        tL_messages_savedReactionsTags = new TLRPC.TL_messages_savedReactionsTags();
                        longSparseArray.put(j2, tL_messages_savedReactionsTags);
                    }
                }
                int i2 = 0;
                boolean z5 = false;
                boolean z6 = false;
                while (i2 < tL_messages_savedReactionsTags.tags.size()) {
                    TLRPC.TL_savedReactionTag tL_savedReactionTag = tL_messages_savedReactionsTags.tags.get(i2);
                    if (visibleReaction.isSame(tL_savedReactionTag.reaction)) {
                        int i3 = tL_savedReactionTag.count;
                        int iMax = Math.max(0, (z ? 1 : -1) + i3);
                        tL_savedReactionTag.count = iMax;
                        if (iMax <= 0) {
                            tL_messages_savedReactionsTags.tags.remove(i2);
                            i2--;
                        } else if (iMax == i3) {
                            z5 = true;
                        }
                        z3 = true;
                        z5 = true;
                        z6 = true;
                    }
                    i2++;
                }
                if (z5 || !z) {
                    z4 = z6;
                } else {
                    TLRPC.TL_savedReactionTag tL_savedReactionTag2 = new TLRPC.TL_savedReactionTag();
                    tL_savedReactionTag2.reaction = visibleReaction.toTLReaction();
                    tL_savedReactionTag2.count = 1;
                    tL_messages_savedReactionsTags.tags.add(tL_savedReactionTag2);
                    z3 = true;
                }
                if (z2 && z4) {
                    updateSavedReactionTags(j2);
                }
            }
            i++;
        }
        return z3;
    }

    public void updateSavedReactionTags(HashSet<Long> hashSet) {
        updateSavedReactionTags(0L);
        Iterator<Long> it = hashSet.iterator();
        while (it.hasNext()) {
            updateSavedReactionTags(it.next().longValue());
        }
    }

    public void updateSavedReactionTags(long j) {
        String str;
        LongSparseArray<TLRPC.TL_messages_savedReactionsTags> longSparseArray = this.reactionTags;
        if (longSparseArray == null) {
            return;
        }
        TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags = longSparseArray.get(j);
        if (tL_messages_savedReactionsTags == null) {
            if (j == 0) {
                return;
            }
            LongSparseArray<TLRPC.TL_messages_savedReactionsTags> longSparseArray2 = this.reactionTags;
            TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags2 = new TLRPC.TL_messages_savedReactionsTags();
            longSparseArray2.put(j, tL_messages_savedReactionsTags2);
            tL_messages_savedReactionsTags = tL_messages_savedReactionsTags2;
        }
        Collections.sort(tL_messages_savedReactionsTags.tags, new Comparator() { 
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return this.f$0.lambda$updateSavedReactionTags$473((TLRPC.TL_savedReactionTag) obj, (TLRPC.TL_savedReactionTag) obj2);
            }
        });
        long jCalcHash = 0;
        for (int i = 0; i < tL_messages_savedReactionsTags.tags.size(); i++) {
            TLRPC.TL_savedReactionTag tL_savedReactionTag = tL_messages_savedReactionsTags.tags.get(i);
            if (tL_savedReactionTag.count > 0) {
                TLRPC.Reaction reaction = tL_savedReactionTag.reaction;
                if (reaction instanceof TLRPC.TL_reactionEmoji) {
                    String strSubstring = Utilities.MD5(((TLRPC.TL_reactionEmoji) reaction).emoticon).substring(0, 16);
                    jCalcHash = MediaDataController.calcHash(jCalcHash, MessagesController$$ExternalSyntheticBackport5.m(strSubstring, 0, strSubstring.length(), 16));
                } else if (reaction instanceof TLRPC.TL_reactionCustomEmoji) {
                    jCalcHash = MediaDataController.calcHash(jCalcHash, ((TLRPC.TL_reactionCustomEmoji) reaction).document_id);
                }
                if (j == 0 && (tL_savedReactionTag.flags & 1) != 0 && (str = tL_savedReactionTag.title) != null) {
                    String strSubstring2 = Utilities.MD5(str).substring(0, 16);
                    jCalcHash = MediaDataController.calcHash(jCalcHash, MessagesController$$ExternalSyntheticBackport5.m(strSubstring2, 0, strSubstring2.length(), 16));
                }
                jCalcHash = MediaDataController.calcHash(jCalcHash, tL_savedReactionTag.count);
            }
        }
        tL_messages_savedReactionsTags.hash = jCalcHash;
        saveSavedReactionsTags(j, tL_messages_savedReactionsTags);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.savedReactionTagsUpdate, Long.valueOf(j));
    }

    public int lambda$renameSavedReactionTag$474(TLRPC.TL_savedReactionTag tL_savedReactionTag, TLRPC.TL_savedReactionTag tL_savedReactionTag2) {
        int i = tL_savedReactionTag.count;
        int i2 = tL_savedReactionTag2.count;
        return i == i2 ? Long.compare(getTagLongId(tL_savedReactionTag2.reaction) ^ Long.MIN_VALUE, getTagLongId(tL_savedReactionTag.reaction) ^ Long.MIN_VALUE) : i2 - i;
    }

    private long getTagLongId(TLRPC.Reaction reaction) {
        if (reaction == null) {
            return 0L;
        }
        long j = reaction.tag_long_id;
        if (j != 0) {
            return j;
        }
        if (reaction instanceof TLRPC.TL_reactionEmoji) {
            String strSubstring = Utilities.MD5(((TLRPC.TL_reactionEmoji) reaction).emoticon).substring(0, 16);
            long jM = MessagesController$$ExternalSyntheticBackport5.m(strSubstring, 0, strSubstring.length(), 16);
            reaction.tag_long_id = jM;
            return jM;
        }
        if (!(reaction instanceof TLRPC.TL_reactionCustomEmoji)) {
            return 0L;
        }
        long j2 = ((TLRPC.TL_reactionCustomEmoji) reaction).document_id;
        reaction.tag_long_id = j2;
        return j2;
    }

    public TLRPC.TL_messages_savedReactionsTags getSavedReactionTags(long j) {
        return getSavedReactionTags(j, false);
    }

    public TLRPC.TL_messages_savedReactionsTags getSavedReactionTags(final long j, boolean z) {
        HashSet<Long> hashSet = this.loadingReactionTags;
        if (hashSet != null && hashSet.contains(Long.valueOf(j)) && !z) {
            LongSparseArray<TLRPC.TL_messages_savedReactionsTags> longSparseArray = this.reactionTags;
            if (longSparseArray == null) {
                return null;
            }
            return longSparseArray.get(j);
        }
        if (this.loadingReactionTags == null) {
            this.loadingReactionTags = new HashSet<>();
        }
        this.loadingReactionTags.add(Long.valueOf(j));
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$getSavedReactionTags$478(j);
            }
        });
        return null;
    }

    void lambda$getSavedReactionTags$477(final TLRPC.messages_SavedReactionTags messages_savedreactiontags, final long j) {
        if (this.reactionTags == null) {
            this.reactionTags = new LongSparseArray<>();
        }
        boolean z = messages_savedreactiontags instanceof TLRPC.TL_messages_savedReactionsTags;
        if (z) {
            this.reactionTags.put(j, (TLRPC.TL_messages_savedReactionsTags) messages_savedreactiontags);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.savedReactionTagsUpdate, Long.valueOf(j));
        }
        final TLRPC.TL_messages_getSavedReactionTags tL_messages_getSavedReactionTags = new TLRPC.TL_messages_getSavedReactionTags();
        if (z) {
            tL_messages_getSavedReactionTags.hash = messages_savedreactiontags.hash;
        }
        if (j != 0) {
            tL_messages_getSavedReactionTags.flags |= 1;
            tL_messages_getSavedReactionTags.peer = getInputPeer(j);
        }
        getConnectionsManager().sendRequest(tL_messages_getSavedReactionTags, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$getSavedReactionTags$476(j, messages_savedreactiontags, tL_messages_getSavedReactionTags, tLObject, tL_error);
            }
        });
    }

    public void lambda$getSavedReactionTags$475(TLObject tLObject, long j, TLRPC.messages_SavedReactionTags messages_savedreactiontags, TLRPC.TL_messages_getSavedReactionTags tL_messages_getSavedReactionTags) {
        if (tLObject instanceof TLRPC.TL_messages_savedReactionsTags) {
            TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags = (TLRPC.TL_messages_savedReactionsTags) tLObject;
            this.reactionTags.put(j, tL_messages_savedReactionsTags);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.savedReactionTagsUpdate, Long.valueOf(j));
            saveSavedReactionsTags(j, tL_messages_savedReactionsTags);
            return;
        }
        if ((tLObject instanceof TLRPC.TL_messages_savedReactionsTagsNotModified) && messages_savedreactiontags == null && tL_messages_getSavedReactionTags.hash == 0) {
            TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags2 = new TLRPC.TL_messages_savedReactionsTags();
            this.reactionTags.put(j, tL_messages_savedReactionsTags2);
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.savedReactionTagsUpdate, Long.valueOf(j));
            saveSavedReactionsTags(j, tL_messages_savedReactionsTags2);
        }
    }

    private void saveSavedReactionsTags(final long j, final TLRPC.TL_messages_savedReactionsTags tL_messages_savedReactionsTags) {
        getMessagesStorage().getStorageQueue().postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                this.f$0.lambda$saveSavedReactionsTags$479(j, tL_messages_savedReactionsTags);
            }
        });
    }

    void lambda$checkPeerColors$481(final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_help_peerColors) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$checkPeerColors$480(tLObject);
                }
            });
        }
    }

    public void lambda$checkPeerColors$482(TLObject tLObject) {
        this.loadingProfilePeerColors = false;
        this.profilePeerColors = PeerColors.fromTL(1, (TLRPC.TL_help_peerColors) tLObject);
        this.mainPreferences.edit().putString("profilePeerColors", this.profilePeerColors.toString()).apply();
    }

    public void setStoryQuality(boolean z) {
        if (this.storyQualityFull != z) {
            SharedPreferences.Editor editorEdit = this.mainPreferences.edit();
            this.storyQualityFull = z;
            editorEdit.putBoolean("storyQualityFull", z).apply();
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.storyQualityUpdate, new Object[0]);
        }
    }

    public void setSavedViewAs(boolean z) {
        if (this.savedViewAsChats != z) {
            SharedPreferences.Editor editorEdit = this.mainPreferences.edit();
            this.savedViewAsChats = z;
            editorEdit.putBoolean("savedViewAsChats", z).apply();
        }
    }

    public void setFolderTags(boolean z) {
        if (this.folderTags != z) {
            SharedPreferences.Editor editorEdit = this.mainPreferences.edit();
            this.folderTags = z;
            editorEdit.putBoolean("folderTags", z).apply();
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setFolderTags$484();
                }
            });
        }
    }

    public void lambda$requestIsUserContactBlocked$486(final ArrayList arrayList, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$requestIsUserContactBlocked$485(tLObject, arrayList);
            }
        });
    }

    public void lambda$getAvailableEffects$487(TLRPC.messages_AvailableEffects messages_availableeffects) {
        if (this.availableEffects != messages_availableeffects) {
            this.availableEffects = messages_availableeffects;
            if (messages_availableeffects != null) {
                AnimatedEmojiDrawable.getDocumentFetcher(this.currentAccount).putDocuments(this.availableEffects.documents);
            }
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.availableEffectsUpdate, new Object[0]);
        }
        this.loadingAvailableEffects = false;
    }

    public boolean hasAvailableEffects() {
        TLRPC.messages_AvailableEffects messages_availableeffects = this.availableEffects;
        return (messages_availableeffects == null || messages_availableeffects.effects.isEmpty()) ? false : true;
    }

    public TLRPC.TL_availableEffect getEffect(long j) {
        getAvailableEffects();
        if (this.availableEffects == null) {
            return null;
        }
        for (int i = 0; i < this.availableEffects.effects.size(); i++) {
            if (this.availableEffects.effects.get(i).id == j) {
                return this.availableEffects.effects.get(i);
            }
        }
        return null;
    }

    public TLRPC.Document getEffectDocument(long j) {
        if (this.availableEffects == null) {
            return null;
        }
        for (int i = 0; i < this.availableEffects.documents.size(); i++) {
            if (this.availableEffects.documents.get(i).id == j) {
                return this.availableEffects.documents.get(i);
            }
        }
        return null;
    }

    public class AnonymousClass4 extends CacheFetcher<Integer, TLRPC.messages_AvailableEffects> {
        @Override 
        public boolean emitLocal(Integer num) {
            return true;
        }

        @Override 
        public boolean saveLastTimeRequested() {
            return true;
        }

        public AnonymousClass4() {
        }

        @Override 
        public void getRemote(int i, Integer num, long j, final Utilities.Callback4<Boolean, TLRPC.messages_AvailableEffects, Long, Boolean> callback4) {
            TLRPC.TL_messages_getAvailableEffects tL_messages_getAvailableEffects = new TLRPC.TL_messages_getAvailableEffects();
            tL_messages_getAvailableEffects.hash = (int) j;
            MessagesController.this.getConnectionsManager().sendRequest(tL_messages_getAvailableEffects, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    MessagesController.AnonymousClass4.$r8$lambda$PYTsfFCelfPN1oIWiKkj850qgKU(callback4, tLObject, tL_error);
                }
            });
        }

        public static void m4860$r8$lambda$lPMUGMTQ2PorSpUbpqFTXhYm6M(int i, TLRPC.messages_AvailableEffects messages_availableeffects) {
            try {
                SQLiteDatabase database = MessagesStorage.getInstance(i).getDatabase();
                if (database != null) {
                    database.executeFast("DELETE FROM effects").stepThis().dispose();
                    if (messages_availableeffects != null) {
                        SQLitePreparedStatement sQLitePreparedStatementExecuteFast = database.executeFast("INSERT INTO effects VALUES(?)");
                        sQLitePreparedStatementExecuteFast.requery();
                        NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(messages_availableeffects.getObjectSize());
                        messages_availableeffects.serializeToStream(nativeByteBuffer);
                        sQLitePreparedStatementExecuteFast.bindByteBuffer(1, nativeByteBuffer);
                        sQLitePreparedStatementExecuteFast.step();
                        nativeByteBuffer.reuse();
                        sQLitePreparedStatementExecuteFast.dispose();
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override 
        public long getSavedLastTimeRequested(int i) {
            return MessagesController.this.mainPreferences.getLong("effects_last_" + i, 0L);
        }

        @Override 
        public void setSavedLastTimeRequested(int i, long j) {
            MessagesController.this.mainPreferences.edit().putLong("effects_last_" + i, j).apply();
        }
    }

    public static boolean equals(TLRPC.MessageMedia messageMedia, TLRPC.MessageMedia messageMedia2) {
        if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
            return (messageMedia2 instanceof TLRPC.TL_messageMediaDocument) && messageMedia.document.id == messageMedia2.document.id;
        }
        return (messageMedia2 instanceof TLRPC.TL_messageMediaPhoto) && messageMedia.photo.id == messageMedia2.photo.id;
    }

    public static TLRPC.InputDocument toInputDocument(TLRPC.Document document) {
        TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
        tL_inputDocument.id = document.id;
        tL_inputDocument.access_hash = document.access_hash;
        tL_inputDocument.file_reference = document.file_reference;
        return tL_inputDocument;
    }

    public static TLRPC.InputMedia toInputMedia(TLRPC.MessageMedia messageMedia) {
        if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
            TLRPC.TL_inputMediaDocument tL_inputMediaDocument = new TLRPC.TL_inputMediaDocument();
            TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
            tL_inputMediaDocument.id = tL_inputDocument;
            TLRPC.Document document = messageMedia.document;
            tL_inputDocument.id = document.id;
            tL_inputDocument.access_hash = document.access_hash;
            tL_inputDocument.file_reference = document.file_reference;
            return tL_inputMediaDocument;
        }
        if (!(messageMedia instanceof TLRPC.TL_messageMediaPhoto)) {
            return null;
        }
        TLRPC.TL_inputMediaPhoto tL_inputMediaPhoto = new TLRPC.TL_inputMediaPhoto();
        TLRPC.TL_inputPhoto tL_inputPhoto = new TLRPC.TL_inputPhoto();
        tL_inputMediaPhoto.id = tL_inputPhoto;
        TLRPC.Photo photo = messageMedia.photo;
        tL_inputPhoto.id = photo.id;
        tL_inputPhoto.access_hash = photo.access_hash;
        tL_inputPhoto.file_reference = photo.file_reference;
        return tL_inputMediaPhoto;
    }

    public void openApp(TLRPC.User user, int i) {
        openApp(null, user, null, i, null);
    }

    public void openApp(BaseFragment baseFragment, TLRPC.User user, String str, int i, Browser.Progress progress) {
        openApp(baseFragment, user, str, i, progress, false, false);
    }

    public void openApp(final BaseFragment baseFragment, final TLRPC.User user, final String str, final int i, final Browser.Progress progress, final boolean z, final boolean z2) {
        if (user == null) {
            return;
        }
        final boolean[] zArr = {false};
        if (progress != null) {
            progress.onCancel(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.m4841$r8$lambda$sOOIYfeMNh1fykNXjOFnga14Mo(zArr);
                }
            });
            progress.init();
        }
        final TL_bots.BotInfo[] botInfoArr = {botInfoCached};
        final Runnable runnable = new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openApp$489(baseFragment, progress, zArr, user, str, z, z2, botInfoArr);
            }
        };
        MediaDataController mediaDataController = getMediaDataController();
        long j = user.id;
        TL_bots.BotInfo botInfoCached = mediaDataController.getBotInfoCached(j, j);
        if (user.bot_has_main_app) {
            runnable.run();
        } else {
            if (botInfoCached == null) {
                MediaDataController mediaDataController2 = getMediaDataController();
                long j2 = user.id;
                mediaDataController2.loadBotInfo(j2, j2, false, i, new Utilities.Callback() { 
                    @Override 
                    public final void run(Object obj) {
                        this.f$0.lambda$openApp$491(zArr, botInfoArr, user, i, runnable, (TL_bots.BotInfo) obj);
                    }
                });
                return;
            }
            runnable.run();
        }
    }

    public static void m4794$r8$lambda$ezFXq0Luff8jifnePt9v0kfgzs(boolean[] zArr, TL_bots.BotInfo[] botInfoArr, Runnable runnable, TLRPC.UserFull userFull) {
        if (zArr[0]) {
            return;
        }
        if (userFull != null) {
            botInfoArr[0] = userFull.bot_info;
        }
        AndroidUtilities.runOnUIThread(runnable);
    }

    public TL_account.contentSettings getContentSettings() {
        return this.contentSettings;
    }

    public void getContentSettings(Utilities.Callback<TL_account.contentSettings> callback) {
        if (this.contentSettings != null && System.currentTimeMillis() - this.contentSettingsLoadedTime < DurationKt.MILLIS_IN_HOUR) {
            if (callback != null) {
                callback.run(this.contentSettings);
                return;
            }
            return;
        }
        if (this.contentSettingsCallbacks == null) {
            this.contentSettingsCallbacks = new ArrayList<>();
        }
        if (callback != null) {
            this.contentSettingsCallbacks.add(callback);
        }
        if (this.contentSettingsLoading) {
            return;
        }
        this.contentSettingsLoading = true;
        getConnectionsManager().sendRequest(new TL_account.getContentSettings(), new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$getContentSettings$493(tLObject, tL_error);
            }
        });
    }

    public void lambda$getContentSettings$492(TLObject tLObject) {
        Set<String> set;
        if (tLObject instanceof TL_account.contentSettings) {
            this.contentSettings = (TL_account.contentSettings) tLObject;
            this.contentSettingsLoadedTime = System.currentTimeMillis();
        }
        this.contentSettingsLoading = false;
        TL_account.contentSettings contentsettings = this.contentSettings;
        if (contentsettings != null && (set = this.ignoreRestrictionReasons) != null) {
            if (contentsettings.sensitive_enabled) {
                set.add("sensitive");
            } else {
                set.remove("sensitive");
            }
            SharedPreferences sharedPreferences = this.mainPreferences;
            if (sharedPreferences != null) {
                sharedPreferences.edit().putStringSet("ignoreRestrictionReasons", this.ignoreRestrictionReasons).apply();
            }
        }
        ArrayList<Utilities.Callback<TL_account.contentSettings>> arrayList = this.contentSettingsCallbacks;
        if (arrayList != null) {
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Utilities.Callback<TL_account.contentSettings> callback = arrayList.get(i);
                i++;
                callback.run(this.contentSettings);
            }
            this.contentSettingsCallbacks.clear();
            this.contentSettingsCallbacks = null;
        }
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.contentSettingsLoaded, new Object[0]);
    }

    public void invalidateContentSettings() {
        this.contentSettings = null;
        this.contentSettingsLoadedTime = 0L;
    }

    public void setContentSettings(boolean z) {
        TL_account.contentSettings contentsettings = this.contentSettings;
        if (contentsettings != null) {
            if (!contentsettings.sensitive_can_change) {
                return;
            } else {
                contentsettings.sensitive_enabled = z;
            }
        }
        if (this.ignoreRestrictionReasons == null) {
            this.ignoreRestrictionReasons = new HashSet();
        }
        Set<String> set = this.ignoreRestrictionReasons;
        if (z) {
            set.add("sensitive");
        } else {
            set.remove("sensitive");
        }
        SharedPreferences sharedPreferences = this.mainPreferences;
        if (sharedPreferences != null) {
            sharedPreferences.edit().putStringSet("ignoreRestrictionReasons", this.ignoreRestrictionReasons).apply();
        }
        TL_account.setContentSettings setcontentsettings = new TL_account.setContentSettings();
        setcontentsettings.sensitive_enabled = z;
        getConnectionsManager().sendRequest(setcontentsettings, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        MessagesController.$r8$lambda$YV7YJ7iOEOMINEDC3haiXeFD5KY(tL_error);
                    }
                });
            }
        });
    }

    public static void lambda$load$1(final int[] iArr, final boolean z, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$load$0(iArr, tLObject, z);
                }
            });
        }

        public void lambda$approveOrRejectSuggestedMessageImpl$497(long j, int i, TLObject tLObject, TLRPC.TL_error tL_error) {
        this.sendingSuggestedMessageApprovalMap.remove(j + "_" + i);
        if (tL_error == null && tLObject != null) {
            processUpdates((TLRPC.Updates) tLObject, false);
        }
    }

    public static class SavedMusicList {
        public final int currentAccount;
        public final long dialogId;
        public boolean endReached;
        public final ArrayList<MessageObject> list = new ArrayList<>();
        public boolean loading;
        public int totalCount;

        public SavedMusicList(int i, long j) {
            this.currentAccount = i;
            this.dialogId = j;
        }

        public void setup(TLRPC.Document document) {
            this.list.clear();
            load();
            this.list.add(0, toMessageObject(document));
        }

        public MessageObject toMessageObject(TLRPC.Document document) {
            TLRPC.TL_message tL_message = new TLRPC.TL_message();
            tL_message.id = SharedConfig.getLastLocalId();
            tL_message.peer_id = MessagesController.getInstance(this.currentAccount).getPeer(this.dialogId);
            tL_message.from_id = MessagesController.getInstance(this.currentAccount).getPeer(this.dialogId);
            TLRPC.TL_messageMediaDocument tL_messageMediaDocument = new TLRPC.TL_messageMediaDocument();
            tL_message.media = tL_messageMediaDocument;
            tL_messageMediaDocument.document = document;
            MessageObject messageObject = new MessageObject(this.currentAccount, tL_message, false, false);
            messageObject.checkMediaExistance();
            return messageObject;
        }

        public void load() {
            if (this.loading || this.endReached) {
                return;
            }
            this.loading = true;
            TLRPC.TL_getSavedMusic tL_getSavedMusic = new TLRPC.TL_getSavedMusic();
            tL_getSavedMusic.id = MessagesController.getInstance(this.currentAccount).getInputUser(this.dialogId);
            tL_getSavedMusic.offset = this.list.size();
            tL_getSavedMusic.limit = 30;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_getSavedMusic, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$load$1(tLObject, tL_error);
                }
            });
        }

        public void lambda$load$0(TLObject tLObject, ArrayList arrayList) {
            if (tLObject instanceof TLRPC.TL_savedMusic) {
                TLRPC.TL_savedMusic tL_savedMusic = (TLRPC.TL_savedMusic) tLObject;
                if (this.totalCount <= 0 && this.list.size() == 1) {
                    if (this.list.get(0).getDocument() != null && !arrayList.isEmpty() && ((MessageObject) arrayList.get(0)).getDocument() != null && this.list.get(0).getDocument().id == ((MessageObject) arrayList.get(0)).getDocument().id) {
                        arrayList.remove(0);
                    } else {
                        this.list.clear();
                    }
                }
                this.totalCount = tL_savedMusic.count;
                this.list.addAll(arrayList);
                this.endReached = this.list.size() >= this.totalCount;
            }
            this.loading = false;
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.musicListLoaded, this);
        }

        public TLRPC.Document getFirstDocument() {
            MessageObject messageObject;
            if (this.list.isEmpty() || (messageObject = this.list.get(0)) == null) {
                return null;
            }
            return messageObject.getDocument();
        }

        public void remove(MessageObject messageObject) {
            TLRPC.Document firstDocument = getFirstDocument();
            this.list.remove(messageObject);
            if (getFirstDocument() != firstDocument) {
                updateFirstMusic();
            }
        }

        public void add(TLRPC.Document document) {
            this.list.add(0, toMessageObject(document));
            updateFirstMusic();
        }

        public void move(int i, int i2) {
            TLRPC.Document firstDocument = getFirstDocument();
            MessageObject messageObject = this.list.get(i2);
            MessageObject messageObject2 = this.list.get(i);
            this.list.set(i, messageObject);
            this.list.set(i2, messageObject2);
            if (getFirstDocument() != firstDocument) {
                updateFirstMusic();
            }
            MessageObject messageObject3 = i2 == 0 ? null : this.list.get(i2 - 1);
            TLRPC.Document document = messageObject2.getDocument();
            TLRPC.Document document2 = messageObject3 == null ? null : messageObject3.getDocument();
            TLRPC.TL_account_saveMusic tL_account_saveMusic = new TLRPC.TL_account_saveMusic();
            if (document == null) {
                return;
            }
            TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
            tL_account_saveMusic.id = tL_inputDocument;
            tL_inputDocument.id = document.id;
            tL_inputDocument.access_hash = document.access_hash;
            byte[] bArr = document.file_reference;
            tL_inputDocument.file_reference = bArr;
            if (bArr == null) {
                tL_inputDocument.file_reference = new byte[0];
            }
            if (document2 != null) {
                tL_account_saveMusic.flags |= 2;
                TLRPC.TL_inputDocument tL_inputDocument2 = new TLRPC.TL_inputDocument();
                tL_account_saveMusic.after_id = tL_inputDocument2;
                tL_inputDocument2.id = document2.id;
                tL_inputDocument2.access_hash = document2.access_hash;
                byte[] bArr2 = document2.file_reference;
                tL_inputDocument2.file_reference = bArr2;
                if (bArr2 == null) {
                    tL_inputDocument2.file_reference = new byte[0];
                }
            }
            tL_account_saveMusic.unsave = false;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_account_saveMusic, null);
        }

        public void updateFirstMusic() {
            TLRPC.Document firstDocument = getFirstDocument();
            TLRPC.UserFull userFull = MessagesController.getInstance(this.currentAccount).getUserFull(this.dialogId);
            if (userFull == null) {
                return;
            }
            int i = userFull.flags2;
            if (firstDocument == null) {
                userFull.flags2 = (-2097153) & i;
                userFull.saved_music = null;
            } else {
                userFull.flags2 = i | TLObject.FLAG_21;
                userFull.saved_music = firstDocument;
            }
            MessagesStorage.getInstance(this.currentAccount).updateUserInfo(userFull, false);
            UserConfig.getInstance(this.currentAccount).saveConfig(true);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.profileMusicUpdated, Long.valueOf(this.dialogId));
        }
    }

    public SavedMusicIds getSavedMusicIds() {
        if (this.savedMusicIds == null) {
            this.savedMusicIds = new SavedMusicIds(this.currentAccount);
        }
        return this.savedMusicIds;
    }

    public static class SavedMusicIds {
        public final int currentAccount;
        public final HashSet<Long> ids = new HashSet<>();
        public boolean loaded;
        public boolean loading;

        public SavedMusicIds(int i) {
            this.currentAccount = i;
            load();
        }

        public void load() {
            if (this.loading || this.loaded) {
                return;
            }
            this.loading = true;
            TL_account.getSavedMusicIds getsavedmusicids = new TL_account.getSavedMusicIds();
            getsavedmusicids.hash = 0L;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(getsavedmusicids, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$load$1(tLObject, tL_error);
                }
            });
        }

        public void lambda$load$0(TLObject tLObject) {
            this.loading = false;
            this.loaded = true;
            if (tLObject instanceof TL_account.TL_savedMusicIds) {
                this.ids.addAll(((TL_account.TL_savedMusicIds) tLObject).ids);
            }
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.musicIdsLoaded, new Object[0]);
        }

        public void update(long j, boolean z) {
            HashSet<Long> hashSet = this.ids;
            if (z) {
                hashSet.add(Long.valueOf(j));
            } else {
                hashSet.remove(Long.valueOf(j));
            }
        }
    }

    public boolean hasSetupEmailSuggestion() {
        return this.pendingSuggestions.contains("SETUP_LOGIN_EMAIL") || this.pendingSuggestions.contains("SETUP_LOGIN_EMAIL_NOSKIP");
    }

    public int checkEmailSuggestion() {
        if (this.emailSuggestionWasShown || this.lastCheckPromoInfoTime + 10 < getConnectionsManager().getCurrentTime()) {
            return 0;
        }
        boolean zContains = this.pendingSuggestions.contains("SETUP_LOGIN_EMAIL");
        if (this.pendingSuggestions.contains("SETUP_LOGIN_EMAIL_NOSKIP")) {
            return 2;
        }
        return zContains ? 1 : 0;
    }

    public void markEmailSuggestionAsShown() {
        this.emailSuggestionWasShown = true;
    }

    public void loadStakeDiceInfo(Utilities.Callback<Boolean> callback) {
        TLRPC.EmojiGameInfo emojiGameInfo = this.stakeDiceInfo;
        if (emojiGameInfo != null) {
            callback.run(Boolean.valueOf(emojiGameInfo instanceof TLRPC.TL_emojiGameDiceInfo));
            return;
        }
        ArrayList<Utilities.Callback<Boolean>> arrayList = this.loadingStakeDiceInfo;
        if (arrayList != null) {
            arrayList.add(callback);
            return;
        }
        ArrayList<Utilities.Callback<Boolean>> arrayList2 = new ArrayList<>();
        this.loadingStakeDiceInfo = arrayList2;
        arrayList2.add(callback);
        getConnectionsManager().sendRequestTyped(new TLRPC.TL_messages_getEmojiGameInfo(), new AiTonesController$$ExternalSyntheticLambda0(), new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$loadStakeDiceInfo$498((TLRPC.EmojiGameInfo) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$loadWebBrowserConfig$500(Long l, final TL_account.TL_webBrowserSettings tL_webBrowserSettings) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadWebBrowserConfig$499(tL_webBrowserSettings);
            }
        });
    }

    public void lambda$loadWebBrowserConfig$501(TL_account.TL_webBrowserSettings tL_webBrowserSettings) {
        if (tL_webBrowserSettings != null) {
            this.webBrowserSettings = tL_webBrowserSettings;
            getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.webBrowserSettingsUpdate, new Object[0]);
        }
        AndroidUtilities.cancelRunOnUIThread(this.loadWebConfigRunnable);
        AndroidUtilities.runOnUIThread(this.loadWebConfigRunnable, 3600010L);
    }

    public void addWebBrowserException(String str, boolean z) {
        TL_account.toggleWebBrowserSettingsException togglewebbrowsersettingsexception = new TL_account.toggleWebBrowserSettingsException();
        togglewebbrowsersettingsexception.flags |= 1;
        togglewebbrowsersettingsexception.open_external_browser = z;
        togglewebbrowsersettingsexception.url = str;
        getConnectionsManager().sendRequestTyped(togglewebbrowsersettingsexception, new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$addWebBrowserException$504((TLRPC.Updates) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$removeWebBrowserException$506(TLRPC.Updates updates, TLRPC.TL_error tL_error) {
        if (updates != null) {
            processUpdates(updates, false);
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$removeWebBrowserException$505();
            }
        });
    }

    private void setWebBrowserSettings(TL_account.TL_webBrowserSettings tL_webBrowserSettings) {
        this.webBrowserSettings = tL_webBrowserSettings;
        this.webBrowserSettingsFetcher.setLocal(this.currentAccount, null, tL_webBrowserSettings, tL_webBrowserSettings.hash);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.webBrowserSettingsUpdate, new Object[0]);
    }

    public void clearAllWebBrowserExceptions() {
        TL_account.TL_webBrowserSettings tL_webBrowserSettings = this.webBrowserSettings;
        if (tL_webBrowserSettings != null) {
            tL_webBrowserSettings.open_external_browser = true;
            tL_webBrowserSettings.display_close_button = true;
            tL_webBrowserSettings.external_exceptions.clear();
            this.webBrowserSettings.inapp_exceptions.clear();
            setWebBrowserSettings(this.webBrowserSettings);
        }
        getConnectionsManager().sendRequestTyped(new TL_account.deleteWebBrowserSettingsExceptions(), new AiTonesController$$ExternalSyntheticLambda0(), new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$clearAllWebBrowserExceptions$507((TL_account.WebBrowserSettings) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void m4861$r8$lambda$Rn_K7xAG5gCcmoYQuVhI7o4QWM(Utilities.Callback4 callback4, TL_account.WebBrowserSettings webBrowserSettings, TLRPC.TL_error tL_error) {
            String str;
            int i;
            if (webBrowserSettings instanceof TL_account.TL_webBrowserSettings) {
                TL_account.TL_webBrowserSettings tL_webBrowserSettings = (TL_account.TL_webBrowserSettings) webBrowserSettings;
                callback4.run(Boolean.FALSE, tL_webBrowserSettings, Long.valueOf(tL_webBrowserSettings.hash), Boolean.TRUE);
                return;
            }
            if (webBrowserSettings instanceof TL_account.TL_webBrowserSettingsNotModified) {
                Boolean bool = Boolean.TRUE;
                callback4.run(bool, null, 0L, bool);
                return;
            }
            if (tL_error != null) {
                str = tL_error.code + " " + tL_error.text;
            } else {
                str = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            FileLog.e("getting web settings error ".concat(str));
            callback4.run(Boolean.FALSE, null, 0L, Boolean.valueOf(tL_error == null || !((i = tL_error.code) == -2000 || i == -2001)));
        }

        @Override 
        public void setLocal(final int i, Void r2, final TL_account.TL_webBrowserSettings tL_webBrowserSettings, long j) {
            MessagesStorage.getInstance(i).getStorageQueue().postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    MessagesController.AnonymousClass5.$r8$lambda$AGK0qPcYtlvbDoLcIUg1yAlmRhk(i, tL_webBrowserSettings);
                }
            });
        }

        public static void lambda$updateWebBrowserSettings$508(TL_account.WebBrowserSettings webBrowserSettings, TLRPC.TL_error tL_error) {
        if (webBrowserSettings instanceof TL_account.TL_webBrowserSettings) {
            setWebBrowserSettings((TL_account.TL_webBrowserSettings) webBrowserSettings);
        }
    }

    private void applyWebBrowserUpdate(TL_update.TL_updateWebBrowserSettings tL_updateWebBrowserSettings) {
        TL_account.TL_webBrowserSettings tL_webBrowserSettings = this.webBrowserSettings;
        if (tL_webBrowserSettings == null) {
            return;
        }
        tL_webBrowserSettings.open_external_browser = tL_updateWebBrowserSettings.open_external_browser;
        tL_webBrowserSettings.display_close_button = tL_updateWebBrowserSettings.display_close_button;
        setWebBrowserSettings(tL_webBrowserSettings);
    }

    private void applyWebBrowserUpdate(TL_update.TL_updateWebBrowserException tL_updateWebBrowserException) {
        TL_account.TL_webBrowserSettings tL_webBrowserSettings = this.webBrowserSettings;
        if (tL_webBrowserSettings == null) {
            return;
        }
        if (tL_updateWebBrowserException.delete) {
            if (BitwiseUtils.hasFlag(tL_updateWebBrowserException.flags, 1)) {
                boolean z = tL_updateWebBrowserException.open_external_browser;
                TL_account.TL_webBrowserSettings tL_webBrowserSettings2 = this.webBrowserSettings;
                if (z) {
                    deleteWebBrowserExceptionFromList(tL_webBrowserSettings2.external_exceptions, tL_updateWebBrowserException.exception);
                } else {
                    deleteWebBrowserExceptionFromList(tL_webBrowserSettings2.inapp_exceptions, tL_updateWebBrowserException.exception);
                }
            } else {
                deleteWebBrowserExceptionFromList(this.webBrowserSettings.external_exceptions, tL_updateWebBrowserException.exception);
                deleteWebBrowserExceptionFromList(this.webBrowserSettings.inapp_exceptions, tL_updateWebBrowserException.exception);
            }
        } else if (tL_updateWebBrowserException.open_external_browser) {
            addWebBrowserExceptionFromList(tL_webBrowserSettings.external_exceptions, tL_updateWebBrowserException.exception);
            deleteWebBrowserExceptionFromList(this.webBrowserSettings.inapp_exceptions, tL_updateWebBrowserException.exception);
        } else {
            addWebBrowserExceptionFromList(tL_webBrowserSettings.inapp_exceptions, tL_updateWebBrowserException.exception);
            deleteWebBrowserExceptionFromList(this.webBrowserSettings.external_exceptions, tL_updateWebBrowserException.exception);
        }
        setWebBrowserSettings(this.webBrowserSettings);
    }
}
