package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.PictureInPictureParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StatFs;
import android.os.StrictMode;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.Base64;
import android.util.SparseIntArray;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.window.BackEvent;
import android.window.OnBackAnimationCallback;
import android.window.OnBackInvokedCallback;
import androidx.appcompat.app.AppCompatDelegateImpl$Api33Impl$$ExternalSyntheticApiModelOutline0;
import androidx.arch.core.util.Function;
import androidx.collection.LongSparseArray;
import androidx.core.app.ActivityCompat;
import androidx.core.content.pm.ShortcutInfoCompat;
import androidx.core.content.pm.ShortcutManagerCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.drawer.DrawerContainer;
import com.exteragram.messenger.icons.ExteraResources;
import com.exteragram.messenger.icons.ui.picker.IconPickerController;
import com.exteragram.messenger.pillstack.core.PillStackConfig;
import com.exteragram.messenger.pillstack.core.PillType;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.updater.UpdaterUtils;
import com.exteragram.messenger.utils.IntentsController;
import com.exteragram.messenger.utils.network.RemoteUtils;
import com.exteragram.messenger.utils.ui.MonetUtils;
import com.google.android.gms.cast.framework.media.NotificationOptions;
import com.google.android.gms.common.api.Status;
import com.google.android.material.navigation.NavigationBarView;
import com.google.common.primitives.Longs;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.AssistActionBuilder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import kotlin.jvm.internal.LongCompanionObject;
import kotlin.time.DurationKt;
import okhttp3.internal.url._UrlKt;
import org.mvel2.MVEL;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.AutoDeleteMediaTask;
import org.telegram.messenger.BackupAgent;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.BotGuardHelper;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChannelBoostsController;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.ContactsLoadingObserver;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FingerprintController;
import org.telegram.messenger.FlagSecureReason;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.GiftAuctionController;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.LocationController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.OpenAttachedMenuBotReceiver;
import org.telegram.messenger.PushListenerController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SharedPrefsHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.pip.PipActivityController;
import org.telegram.messenger.pip.activity.IPipActivity;
import org.telegram.messenger.pip.activity.IPipActivityHandler;
import org.telegram.messenger.pip.activity.IPipActivityListener;
import org.telegram.messenger.utils.Choreographer60FpsContent;
import org.telegram.messenger.utils.FrameMetricsOverlayView;
import org.telegram.messenger.utils.LeakDetector;
import org.telegram.messenger.utils.WindowVisibilityManager;
import org.telegram.messenger.video.VideoAds;
import org.telegram.messenger.voip.VideoCapturerDevice;
import org.telegram.messenger.voip.VoIPGroupNotification;
import org.telegram.messenger.voip.VoIPPendingCall;
import org.telegram.messenger.voip.VoIPPreNotificationService;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_chatlists;
import org.telegram.tgnet.tl.TL_forum;
import org.telegram.tgnet.tl.TL_payments;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheetTabs;
import org.telegram.ui.ActionBar.BottomSheetTabsOverlay;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.LanguageCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AppIconBulletinLayout;
import org.telegram.ui.Components.AttachBotIntroTopView;
import org.telegram.ui.Components.AudioPlayerAlert;
import org.telegram.ui.Components.BatteryDrawable;
import org.telegram.ui.Components.BlockingUpdateView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatAttachAlertContactsLayout;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.FireworksOverlay;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.FolderBottomSheet;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.JoinGroupAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PasscodeView;
import org.telegram.ui.Components.PasscodeViewDialog;
import org.telegram.ui.Components.PhonebookShareAlert;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.PipVideoOverlay;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.boosts.BoostPagerBottomSheet;
import org.telegram.ui.Components.Premium.boosts.GiftInfoBottomSheet;
import org.telegram.ui.Components.Premium.boosts.UserSelectorBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.SearchTagsList;
import org.telegram.ui.Components.ShareTopView;
import org.telegram.ui.Components.SharingLocationsAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickerSetBulletinLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.TermsOfServiceView;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.ThemeEditorView;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.inset.WindowAnimatedInsetsProvider;
import org.telegram.ui.Components.poll.PollAttachedMediaPack;
import org.telegram.ui.Components.spoilers.SpoilerEffect2;
import org.telegram.ui.Components.voip.RTMPStreamPipOverlay;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.Gifts.AuctionJoinSheet;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.Stars.ISuperRipple;
import org.telegram.ui.Stars.StarGiftPreviewSheet;
import org.telegram.ui.Stars.StarGiftSheet;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stars.SuperRipple;
import org.telegram.ui.Stories.LiveStoryPipOverlay;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoriesListPlaceProvider;
import org.telegram.ui.Stories.StoryViewer;
import org.telegram.ui.Stories.recorder.StoryEntry;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.TON.TONIntroActivity;
import org.telegram.ui.bots.BotWebViewSheet;
import org.telegram.ui.bots.WebViewRequestProps;
import org.webrtc.MediaStreamTrack;
import org.webrtc.voiceengine.WebRtcAudioTrack;

public class LaunchActivity extends BasePermissionsActivity implements INavigationLayout.INavigationLayoutDelegate, NotificationCenter.NotificationCenterDelegate, DialogsActivity.DialogsActivityDelegate, IPipActivity {
    public static final Pattern PREFIX_T_ME_PATTERN = Pattern.compile("^(?:http(?:s|)://|)([A-z0-9-]+?)\\.t\\.me");
    private static int activeInstanceCount;
    public static LaunchActivity instance;
    public static boolean isActive;
    public static boolean isResumed;
    public static Runnable onResumeStaticCallback;
    private static LaunchActivity staticInstanceForAlerts;
    public static boolean systemBlurEnabled;
    private static Pattern timestampPattern;
    public static Runnable whenResumed;
    public ActionBarLayout actionBarLayout;
    private long alreadyShownFreeDiscSpaceAlertForced;
    private SizeNotifierFrameLayout backgroundTablet;
    private final LiteMode.BatteryReceiver batteryReceiver;
    private BlockingUpdateView blockingUpdateView;
    private Consumer<Boolean> blurListener;
    private BottomSheetTabsOverlay bottomSheetTabsOverlay;
    private boolean checkFreeDiscSpaceShown;
    private ArrayList<TLRPC.User> contactsToSend;
    private Uri contactsToSendUri;
    private int currentConnectionState;
    private ISuperRipple currentRipple;
    private String documentsMimeType;
    private ArrayList<String> documentsOriginalPathsArray;
    private ArrayList<String> documentsPathsArray;
    private ArrayList<Uri> documentsUrisArray;
    public DrawerLayoutContainer drawerLayoutContainer;
    private HashMap<String, String> englishLocaleStrings;
    private Uri exportingChatUri;
    View feedbackView;
    private boolean finished;
    private FireworksOverlay fireworksOverlay;
    private FlagSecureReason flagSecureReason;
    public FrameLayout frameLayout;
    private FrameMetricsOverlayView frameMetricsOverlayView;
    private NotificationCenter.ObserversGroup globalObserversGroup;
    private ArrayList<Parcelable> importingStickers;
    private ArrayList<String> importingStickersEmoji;
    private String importingStickersSoftware;
    private final int instanceId;
    private boolean isInPictureInPictureMode;
    private boolean isNavigationBarColorFrozen;
    private boolean isStarted;
    private RelativeLayout launchLayout;
    private ActionBarLayout layersActionBarLayout;
    private boolean loadingLocaleDialog;
    private TLRPC.TL_theme loadingTheme;
    private boolean loadingThemeAccent;
    private String loadingThemeFileName;
    private Theme.ThemeInfo loadingThemeInfo;
    private AlertDialog loadingThemeProgressDialog;
    private TLRPC.TL_wallPaper loadingThemeWallpaper;
    private String loadingThemeWallpaperName;
    private Dialog localeDialog;
    private Runnable lockRunnable;
    private AlertDialog memoryLeakErrorAlertDialog;
    private ValueAnimator navBarAnimator;
    private boolean navigateToPremiumBot;
    public Runnable navigateToPremiumGiftCallback;
    private NotificationCenter.ObserversGroup observersGroup;
    private Object onBackAnimationCallback;
    private Object onBackInvokedCallback;
    private ViewTreeObserver.OnGlobalLayoutListener onGlobalLayoutListener;
    private Utilities.Callback<Boolean> onPowerSaverCallback;
    private final List<Runnable> onUserLeaveHintListeners;
    private List<PasscodeView> overlayPasscodeViews;
    private PasscodeViewDialog passcodeDialog;
    private Intent passcodeSaveIntent;
    private boolean passcodeSaveIntentIsNew;
    private boolean passcodeSaveIntentIsRestore;
    private ArrayList<SendMessagesHelper.SendingMediaInfo> photoPathsArray;
    private final PipActivityController pipActivityController;
    private final IPipActivityHandler pipActivityHandler;
    private Dialog proxyErrorDialog;
    private int reasonsToHideDecorView;
    private int reasonsToHideMainContent;
    private final SparseIntArray requestedPermissions;
    private int requsetPermissionsPointer;
    public ActionBarLayout rightActionBarLayout;
    private View rippleAbove;
    private WindowAnimatedInsetsProvider rootAnimatedInsetsListener;
    private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;
    private CharSequence sendingText;
    private FrameLayout shadowTablet;
    private FrameLayout shadowTabletSide;
    private boolean switchingAccount;
    private HashMap<String, String> systemLocaleStrings;
    private boolean tabletFullSize;
    private int[] tempLocation;
    private TermsOfServiceView termsOfServiceView;
    private ImageView themeSwitchImageView;
    private ImageView themeSwitchSunView;
    private String videoPath;
    private ActionMode visibleActionMode;
    public final ArrayList<Dialog> visibleDialogs;
    private String voicePath;
    public boolean voipLaunchedInBackground;
    private boolean wasMutedByAdminRaisedHand;
    private Utilities.Callback<Boolean> webviewShareAPIDoneListener;
    private ExteraResources res = null;
    private AssetManager assetManager = null;
    public ArrayList<INavigationLayout> sheetFragmentsStack = new ArrayList<>();
    private final ArrayList<BaseFragment> mainFragmentsStack = new ArrayList<>();
    private final ArrayList<BaseFragment> layerFragmentsStack = new ArrayList<>();
    private final ArrayList<BaseFragment> rightFragmentsStack = new ArrayList<>();

    public static boolean $r8$lambda$zgDmHDYwCMzcay6OxRkyPCmZOe8() {
        return SharedConfig.passcodeHash.length() > 0 && !SharedConfig.allowScreenCapture;
    }

    public class AnonymousClass4 extends DrawerLayoutContainer {
        private boolean wasPortrait;

        public AnonymousClass4(Context context) {
            super(context);
        }

        @Override // org.telegram.ui.ActionBar.DrawerLayoutContainer, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            super.onLayout(z, i, i2, i3, i4);
            boolean z2 = i4 - i2 > i3 - i;
            if (z2 != this.wasPortrait) {
                post(new Runnable() { // from class: org.telegram.ui.LaunchActivity$4$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$onLayout$0();
                    }
                });
                this.wasPortrait = z2;
            }
        }

        public void lambda$onAcceptTerms$0() {
            LaunchActivity.this.termsOfServiceView.setVisibility(8);
        }
    }

    public void showPasscodeActivity(boolean z, boolean z2, int i, int i2, final Runnable runnable, Runnable runnable2) {
        if (this.drawerLayoutContainer == null || isFinishing()) {
            return;
        }
        if (this.passcodeDialog == null) {
            this.passcodeDialog = new PasscodeViewDialog(this);
        }
        SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialogWindow = this.selectAnimatedEmojiDialog;
        if (selectAnimatedEmojiDialogWindow != null) {
            selectAnimatedEmojiDialogWindow.dismiss();
            this.selectAnimatedEmojiDialog = null;
        }
        SharedConfig.appLocked = true;
        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            SecretMediaViewer.getInstance().closePhoto(false, false);
        } else if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(false, true);
        } else if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        StoryRecorder.destroyInstance();
        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
        if (playingMessageObject != null && playingMessageObject.isRoundVideo()) {
            MediaController.getInstance().cleanupPlayer(true, true);
        }
        this.passcodeDialog.show();
        this.passcodeDialog.passcodeView.onShow(this.overlayPasscodeViews.isEmpty() && z, z2, i, i2, new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda59
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showPasscodeActivity$7(runnable);
            }
        }, runnable2);
        int i3 = 0;
        while (i3 < this.overlayPasscodeViews.size()) {
            this.overlayPasscodeViews.get(i3).onShow(z && i3 == this.overlayPasscodeViews.size() - 1, z2, i, i2, null, null);
            i3++;
        }
        SharedConfig.isWaitingForPasscodeEnter = true;
        PasscodeView.PasscodeViewDelegate passcodeViewDelegate = new PasscodeView.PasscodeViewDelegate() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda60
            @Override // org.telegram.ui.Components.PasscodeView.PasscodeViewDelegate
            public final void didAcceptedPassword(PasscodeView passcodeView) throws Throwable {
                this.f$0.lambda$showPasscodeActivity$8(passcodeView);
            }
        };
        this.passcodeDialog.passcodeView.setDelegate(passcodeViewDelegate);
        Iterator<PasscodeView> it = this.overlayPasscodeViews.iterator();
        while (it.hasNext()) {
            it.next().setDelegate(passcodeViewDelegate);
        }
        try {
            NotificationsController.getInstance(UserConfig.selectedAccount).showNotifications();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$handleIntent$9(String str) {
        if (this.actionBarLayout.getFragmentStack().isEmpty()) {
            return;
        }
        this.actionBarLayout.getFragmentStack().get(0).presentFragment(new PremiumPreviewFragment(Uri.parse(str).getQueryParameter("ref")));
    }

    public void lambda$handleIntent$19() {
        if (this.actionBarLayout.getFragmentStack().isEmpty()) {
            return;
        }
        this.actionBarLayout.getFragmentStack().get(0).showDialog(new StickersAlert(this, this.importingStickersSoftware, this.importingStickers, this.importingStickersEmoji, null));
    }

    public void lambda$openTopicRequest$34(final int i, final TLRPC.Chat chat, final int i2, final int i3, final Runnable runnable, final String str, final Integer num, final byte[] bArr, final int i4, final ArrayList arrayList, final int i5, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda167
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openTopicRequest$33(tL_error, tLObject, i, chat, i2, i3, runnable, str, num, bArr, i4, arrayList, i5);
            }
        });
    }

    public void lambda$runImportRequest$36(final Uri uri, final int i, final AlertDialog alertDialog, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda120
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$runImportRequest$35(tLObject, uri, i, alertDialog);
            }
        }, 2L);
    }

    public void lambda$runLinkRequest$46(int i, String str, String str2, String str3, String str4, String str5, String str6, String str7, String str8, String str9, String str10, String str11, String str12, boolean z, Integer num, Long l, Long l2, Integer num2, String str13, HashMap map, String str14, String str15, String str16, String str17, TLRPC.TL_wallPaper tL_wallPaper, String str18, String str19, String str20, String str21, boolean z2, String str22, int i2, String str23, String str24, String str25, String str26, String str27, Browser.Progress progress, boolean z3, int i3, boolean z4, int i4, int i5, String str28, String str29, boolean z5, String str30, boolean z6, boolean z7, boolean z8, boolean z9, boolean z10, String str31, Integer num3, boolean z11, byte[] bArr, int i6) {
        LaunchActivity launchActivity;
        if (i6 != i) {
            launchActivity = this;
            launchActivity.switchToAccount(i6, true);
        } else {
            launchActivity = this;
        }
        launchActivity.runLinkRequest(i6, str, str2, str3, str4, str5, str6, str7, str8, str9, str10, str11, str12, z, num, l, l2, num2, str13, map, str14, str15, str16, str17, tL_wallPaper, str18, str19, str20, str21, z2, str22, 1, i2, str23, str24, str25, str26, str27, progress, z3, i3, z4, i4, i5, str28, str29, z5, str30, z6, z7, z8, z9, z10, str31, num3, z11, bArr);
    }

    public static void $r8$lambda$3dI_5PWyLopdlltbTKWvfStY8j4(Runnable runnable) {
        try {
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void lambda$runLinkRequest$76(final String str, final String str2, final int i, final TLRPC.Chat chat, final DialogsActivity dialogsActivity, final TLRPC.User user, final long j, final boolean z, final TLRPC.TL_chatAdminRights tL_chatAdminRights, final String str3) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda153
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$runLinkRequest$75(str, tL_chatAdminRights, z, str2, i, chat, dialogsActivity, user, j, str3);
            }
        });
    }

    public Bundle val$args;
        final void lambda$onMessagesLoaded$2(String str, final long j, final BaseFragment baseFragment) {
            if (str != null) {
                final AccountInstance accountInstance = AccountInstance.getInstance(LaunchActivity.this.currentAccount);
                long j2 = -j;
                ChatObject.Call groupCall = accountInstance.getMessagesController().getGroupCall(j2, false);
                if (groupCall != null) {
                    VoIPHelper.startCall(accountInstance.getMessagesController().getChat(Long.valueOf(j2)), accountInstance.getMessagesController().getInputPeer(j), null, false, Boolean.valueOf(!groupCall.call.rtmp_stream), LaunchActivity.this, baseFragment, accountInstance);
                    return;
                }
                TLRPC.ChatFull chatFull = accountInstance.getMessagesController().getChatFull(j2);
                if (chatFull != null) {
                    if (chatFull.call == null) {
                        if (baseFragment.getParentActivity() != null) {
                            BulletinFactory.of(baseFragment).createSimpleBulletin(R.raw.linkbroken, LocaleController.getString(R.string.InviteExpired)).show();
                            return;
                        }
                        return;
                    }
                    accountInstance.getMessagesController().getGroupCall(j2, true, new Runnable() { // from class: org.telegram.ui.LaunchActivity$15$$ExternalSyntheticLambda1
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onMessagesLoaded$1(accountInstance, j, baseFragment);
                        }
                    });
                }
            }
        }

        public void lambda$runLinkRequest$88(final int i, final AlertDialog alertDialog, final Runnable runnable, final String str, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda92
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$runLinkRequest$87(tL_error, tLObject, i, alertDialog, runnable, str);
            }
        });
    }

    void $r8$lambda$nkxDupGtYG5kIp7hdSwwGK8Bi6I(boolean[] zArr, DialogInterface dialogInterface) {
        zArr[0] = true;
    }

    public void lambda$runLinkRequest$100(final Runnable runnable, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda105
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$runLinkRequest$99(runnable, tLObject, tL_error);
            }
        });
    }

    public void lambda$processAttachedMenuBotFromShortcut$122(TLRPC.User user) {
        MessagesController.getInstance(this.currentAccount).openApp(user, 0);
    }

    public private List<TLRPC.TL_contact> findContacts(String str, String str2, boolean z) {
        String str3;
        TLRPC.User user;
        MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
        ContactsController contactsController = ContactsController.getInstance(this.currentAccount);
        ArrayList arrayList = new ArrayList(contactsController.contacts);
        ArrayList arrayList2 = new ArrayList();
        String str4 = null;
        int i = 0;
        if (str2 == null) {
            str3 = str;
        } else {
            String strStripExceptNumbers = PhoneFormat.stripExceptNumbers(str2);
            TLRPC.TL_contact tL_contact = contactsController.contactsByPhone.get(strStripExceptNumbers);
            if (tL_contact == null) {
                tL_contact = contactsController.contactsByShortPhone.get(strStripExceptNumbers.substring(Math.max(0, strStripExceptNumbers.length() - 7)));
            }
            if (tL_contact == null) {
                str3 = str;
            } else {
                TLRPC.User user2 = messagesController.getUser(Long.valueOf(tL_contact.user_id));
                if (user2 == null || (user2.self && !z)) {
                    str3 = null;
                } else {
                    arrayList2.add(tL_contact);
                    str3 = str;
                }
            }
        }
        if (arrayList2.isEmpty() && str3 != null) {
            String lowerCase = str3.trim().toLowerCase();
            if (!TextUtils.isEmpty(lowerCase)) {
                String translitString = LocaleController.getInstance().getTranslitString(lowerCase);
                if (lowerCase.equals(translitString) || translitString.length() == 0) {
                    translitString = null;
                }
                String[] strArr = {lowerCase, translitString};
                int size = arrayList.size();
                int i2 = 0;
                while (i2 < size) {
                    TLRPC.TL_contact tL_contact2 = (TLRPC.TL_contact) arrayList.get(i2);
                    if (tL_contact2 != null && (user = messagesController.getUser(Long.valueOf(tL_contact2.user_id))) != null && (!user.self || z)) {
                        int i3 = 3;
                        String[] strArr2 = new String[3];
                        strArr2[i] = ContactsController.formatName(user.first_name, user.last_name).toLowerCase();
                        String translitString2 = LocaleController.getInstance().getTranslitString(strArr2[i]);
                        strArr2[1] = translitString2;
                        if (strArr2[i].equals(translitString2)) {
                            strArr2[1] = str4;
                        }
                        if (UserObject.isReplyUser(user)) {
                            strArr2[2] = LocaleController.getString(R.string.RepliesTitle).toLowerCase();
                        } else if (user.self) {
                            strArr2[2] = LocaleController.getString(R.string.SavedMessages).toLowerCase();
                        }
                        int i4 = i;
                        int i5 = i4;
                        while (i4 < 2) {
                            String str5 = strArr[i4];
                            if (str5 != null) {
                                while (i < i3) {
                                    String str6 = strArr2[i];
                                    if (str6 != null && (str6.startsWith(str5) || str6.contains(" ".concat(str5)))) {
                                        i5 = 1;
                                        break;
                                    }
                                    i++;
                                    i3 = 3;
                                }
                                String publicUsername = UserObject.getPublicUsername(user);
                                if (i5 == 0 && publicUsername != null && publicUsername.startsWith(str5)) {
                                    i5 = 1;
                                }
                                if (i5 != 0) {
                                    arrayList2.add(tL_contact2);
                                    break;
                                }
                            }
                            i4++;
                            i = 0;
                            i3 = 3;
                        }
                    }
                    i2++;
                    str4 = null;
                    i = 0;
                }
            }
        }
        return arrayList2;
    }

    public void checkAppUpdate(boolean z) {
        checkAppUpdate(z, null);
    }

    public void checkAppUpdate(final boolean z, final Browser.Progress progress) {
        if (!z) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            if (Math.abs(jCurrentTimeMillis - SharedConfig.lastUpdateCheckTime) < 1800000 || Math.abs(jCurrentTimeMillis - ExteraConfig.getUpdateScheduleTimestamp()) < DurationKt.MILLIS_IN_HOUR) {
                return;
            }
        }
        SharedConfig.lastUpdateCheckTime = System.currentTimeMillis();
        SharedConfig.saveConfig();
        final int i = this.currentAccount;
        UpdaterUtils.getAppUpdate(new Utilities.Callback2() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda42
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$checkAppUpdate$138(z, i, progress, (TLRPC.TL_help_appUpdate) obj, (TLRPC.TL_error) obj2);
            }
        });
        if (progress != null) {
            progress.init();
        }
    }

    public void lambda$didSelectDialogs$145(int i, DialogsActivity dialogsActivity, boolean z, ArrayList arrayList, Uri uri, AlertDialog alertDialog, long j) {
        if (j != 0) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("scrollToTopOnResume", true);
            if (!AndroidUtilities.isTablet()) {
                NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.closeChats, new Object[0]);
            }
            if (DialogObject.isUserDialog(j)) {
                bundle.putLong("user_id", j);
            } else {
                bundle.putLong("chat_id", -j);
            }
            ChatActivity chatActivity = new ChatActivity(bundle);
            chatActivity.setOpenImport();
            getActionBarLayout().presentFragment(chatActivity, dialogsActivity != null || z, dialogsActivity == null, true, false);
        } else {
            this.documentsUrisArray = arrayList;
            if (arrayList == null) {
                this.documentsUrisArray = new ArrayList<>();
            }
            this.documentsUrisArray.add(0, uri);
            openDialogsToSend(true);
        }
        try {
            alertDialog.dismiss();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void lambda$didReceivedNotification$152(int i, AlertDialog alertDialog, int i2) {
        if (this.mainFragmentsStack.isEmpty()) {
            return;
        }
        MessagesController messagesController = MessagesController.getInstance(i);
        ArrayList<BaseFragment> arrayList = this.mainFragmentsStack;
        messagesController.openByUserName("spambot", arrayList.get(arrayList.size() - 1), 1);
    }

    public void $r8$lambda$ETQeb5hoj5u8KkVyddSPclU9kFU(HashMap map, int i, TLRPC.MessageMedia messageMedia, int i2, boolean z, int i3, long j) {
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            MessageObject messageObject = (MessageObject) ((Map.Entry) it.next()).getValue();
            SendMessagesHelper.getInstance(i).sendMessage(SendMessagesHelper.SendMessageParams.of(messageMedia, messageObject.getDialogId(), messageObject, (MessageObject) null, (TLRPC.ReplyMarkup) null, (HashMap<String, String>) null, z, i3, 0));
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$159(ValueAnimator valueAnimator) {
        this.frameLayout.invalidate();
    }

    public /* synthetic */ void lambda$didReceivedNotification$160() {
        if (this.isNavigationBarColorFrozen) {
            this.isNavigationBarColorFrozen = false;
            checkSystemBarColors(false, true);
        }
    }

    public /* synthetic */ void lambda$didReceivedNotification$162(final Theme.ThemeInfo themeInfo, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda52
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$didReceivedNotification$161(tLObject, themeInfo);
            }
        });
    }

    public /* synthetic */ void lambda$didReceivedNotification$161(TLObject tLObject, Theme.ThemeInfo themeInfo) {
        if (tLObject instanceof TLRPC.TL_wallPaper) {
            TLRPC.TL_wallPaper tL_wallPaper = (TLRPC.TL_wallPaper) tLObject;
            this.loadingThemeInfo = themeInfo;
            this.loadingThemeWallpaperName = FileLoader.getAttachFileName(tL_wallPaper.document);
            this.loadingThemeWallpaper = tL_wallPaper;
            FileLoader.getInstance(themeInfo.account).loadFile(tL_wallPaper.document, tL_wallPaper, 1, 1);
            return;
        }
        onThemeLoadFinish();
    }

    public /* synthetic */ void lambda$didReceivedNotification$164(Theme.ThemeInfo themeInfo, File file) {
        themeInfo.createBackground(file, themeInfo.pathToWallpaper);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda50
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$didReceivedNotification$163();
            }
        });
    }

    public /* synthetic */ void lambda$didReceivedNotification$163() {
        if (this.loadingTheme == null) {
            return;
        }
        File file = new File(ApplicationLoader.getFilesDirFixed(), "remote" + this.loadingTheme.id + ".attheme");
        TLRPC.TL_theme tL_theme = this.loadingTheme;
        Theme.ThemeInfo themeInfoApplyThemeFile = Theme.applyThemeFile(file, tL_theme.title, tL_theme, true);
        if (themeInfoApplyThemeFile != null) {
            lambda$runLinkRequest$101(new ThemePreviewActivity(themeInfoApplyThemeFile, true, 0, false, false));
        }
        onThemeLoadFinish();
    }

    public /* synthetic */ void lambda$didReceivedNotification$169(DialogInterface dialogInterface) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda41
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$didReceivedNotification$168();
            }
        }, NotificationOptions.SKIP_STEP_THIRTY_SECONDS_IN_MS);
    }

    public /* synthetic */ void lambda$didReceivedNotification$168() {
        this.memoryLeakErrorAlertDialog = null;
    }

    public /* synthetic */ void lambda$didReceivedNotification$170(Bulletin bulletin) {
        if (this.finished || !isResumed) {
            return;
        }
        bulletin.show();
    }

    private void invalidateCachedViews(View view) {
        if (view.getLayerType() != 0) {
            view.invalidate();
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                invalidateCachedViews(viewGroup.getChildAt(i));
            }
        }
    }

    private void checkWasMutedByAdmin(boolean z) {
        ChatObject.Call call;
        long j;
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        boolean z2 = false;
        if (sharedInstance != null && (call = sharedInstance.groupCall) != null) {
            boolean z3 = this.wasMutedByAdminRaisedHand;
            TLRPC.InputPeer groupCallPeer = sharedInstance.getGroupCallPeer();
            if (groupCallPeer != null) {
                j = groupCallPeer.user_id;
                if (j == 0) {
                    long j2 = groupCallPeer.chat_id;
                    if (j2 == 0) {
                        j2 = groupCallPeer.channel_id;
                    }
                    j = -j2;
                }
            } else {
                j = UserConfig.getInstance(this.currentAccount).clientUserId;
            }
            TLRPC.GroupCallParticipant groupCallParticipant = call.participants.get(j);
            boolean z4 = (groupCallParticipant == null || groupCallParticipant.can_self_unmute || !groupCallParticipant.muted) ? false : true;
            if (z4 && groupCallParticipant.raise_hand_rating != 0) {
                z2 = true;
            }
            this.wasMutedByAdminRaisedHand = z2;
            if (z || !z3 || z2 || z4 || GroupCallActivity.groupCallInstance != null) {
                return;
            }
            showVoiceChatTooltip(38);
            return;
        }
        this.wasMutedByAdminRaisedHand = false;
    }

    private void showVoiceChatTooltip(int i) {
        VoIPService sharedInstance = VoIPService.getSharedInstance();
        if (sharedInstance == null || this.mainFragmentsStack.isEmpty() || sharedInstance.groupCall == null) {
            return;
        }
        TLRPC.Chat chat = sharedInstance.getChat();
        BaseFragment currentVisibleFragment = this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1);
        if (currentVisibleFragment instanceof MainTabsActivity) {
            currentVisibleFragment = ((MainTabsActivity) currentVisibleFragment).getCurrentVisibleFragment();
        }
        UndoView undoView = null;
        if (currentVisibleFragment instanceof ChatActivity) {
            ChatActivity chatActivity = (ChatActivity) currentVisibleFragment;
            if (chat != null && chatActivity.getDialogId() == (-chat.id)) {
                chat = null;
            }
            undoView = chatActivity.getUndoView();
        } else if (currentVisibleFragment instanceof DialogsActivity) {
            undoView = ((DialogsActivity) currentVisibleFragment).getUndoView();
        } else if (currentVisibleFragment instanceof ProfileActivity) {
            undoView = ((ProfileActivity) currentVisibleFragment).getUndoView();
        }
        if (undoView != null) {
            undoView.showWithAction(0L, i, chat);
        }
        if (i != 38 || VoIPService.getSharedInstance() == null) {
            return;
        }
        VoIPService.getSharedInstance().playAllowTalkSound();
    }

    private String getStringForLanguageAlert(HashMap<String, String> map, String str, int i) {
        String str2 = map.get(str);
        return str2 == null ? LocaleController.getString(str, i) : str2;
    }

    private void openThemeAccentPreview(TLRPC.TL_theme tL_theme, TLRPC.TL_wallPaper tL_wallPaper, Theme.ThemeInfo themeInfo) {
        int i = themeInfo.lastAccentId;
        Theme.ThemeAccent themeAccentCreateNewAccent = themeInfo.createNewAccent(tL_theme, this.currentAccount);
        themeInfo.prevAccentId = themeInfo.currentAccentId;
        themeInfo.setCurrentAccentId(themeAccentCreateNewAccent.id);
        themeAccentCreateNewAccent.pattern = tL_wallPaper;
        lambda$runLinkRequest$101(new ThemePreviewActivity(themeInfo, i != themeInfo.lastAccentId, 0, false, false));
    }

    private void onThemeLoadFinish() {
        AlertDialog alertDialog = this.loadingThemeProgressDialog;
        if (alertDialog != null) {
            try {
                alertDialog.dismiss();
                this.loadingThemeProgressDialog = null;
            } catch (Throwable th) {
                this.loadingThemeProgressDialog = null;
                throw th;
            }
        }
        this.loadingThemeWallpaperName = null;
        this.loadingThemeWallpaper = null;
        this.loadingThemeInfo = null;
        this.loadingThemeFileName = null;
        this.loadingTheme = null;
    }

    private void checkFreeDiscSpace(final int i) {
        staticInstanceForAlerts = this;
        AutoDeleteMediaTask.run();
        SharedConfig.checkLogsToDelete();
        if ((Build.VERSION.SDK_INT < 26 || i != 0) && !this.checkFreeDiscSpaceShown) {
            Utilities.globalQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$checkFreeDiscSpace$173(i);
                }
            }, 2000L);
        }
    }

    public /* synthetic */ void lambda$checkFreeDiscSpace$173(int i) {
        File directory;
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            try {
                SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
                if ((((i == 2 || i == 1) && Math.abs(this.alreadyShownFreeDiscSpaceAlertForced - System.currentTimeMillis()) > 240000) || Math.abs(globalMainSettings.getLong("last_space_check", 0L) - System.currentTimeMillis()) >= 259200000) && (directory = FileLoader.getDirectory(4)) != null) {
                    StatFs statFs = new StatFs(directory.getAbsolutePath());
                    long availableBlocksLong = statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong();
                    if (i > 0 || availableBlocksLong < 52428800) {
                        if (i > 0) {
                            this.alreadyShownFreeDiscSpaceAlertForced = System.currentTimeMillis();
                        }
                        globalMainSettings.edit().putLong("last_space_check", System.currentTimeMillis()).apply();
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda51
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$checkFreeDiscSpace$172();
                            }
                        });
                    }
                }
            } catch (Throwable unused) {
            }
        }
    }

    public /* synthetic */ void lambda$checkFreeDiscSpace$172() {
        if (this.checkFreeDiscSpaceShown) {
            return;
        }
        try {
            Dialog dialogCreateFreeSpaceDialog = AlertsCreator.createFreeSpaceDialog(this);
            dialogCreateFreeSpaceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda130
                @Override // android.content.DialogInterface.OnDismissListener
                public final void onDismiss(DialogInterface dialogInterface) {
                    this.f$0.lambda$checkFreeDiscSpace$171(dialogInterface);
                }
            });
            this.checkFreeDiscSpaceShown = true;
            dialogCreateFreeSpaceDialog.show();
        } catch (Throwable unused) {
        }
    }

    public /* synthetic */ void lambda$checkFreeDiscSpace$171(DialogInterface dialogInterface) {
        this.checkFreeDiscSpaceShown = false;
    }

    public static void checkFreeDiscSpaceStatic(int i) {
        LaunchActivity launchActivity = staticInstanceForAlerts;
        if (launchActivity != null) {
            launchActivity.checkFreeDiscSpace(i);
        }
    }

    private void showLanguageAlertInternal(LocaleController.LocaleInfo localeInfo, LocaleController.LocaleInfo localeInfo2, String str) {
        try {
            this.loadingLocaleDialog = false;
            LocaleController.LocaleInfo localeInfo3 = localeInfo;
            boolean z = localeInfo3.builtIn || LocaleController.getInstance().isCurrentLocalLocale();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getStringForLanguageAlert(this.systemLocaleStrings, "ChooseYourLanguage", R.string.ChooseYourLanguage));
            builder.setSubtitle(getStringForLanguageAlert(this.englishLocaleStrings, "ChooseYourLanguage", R.string.ChooseYourLanguage));
            LinearLayout linearLayout = new LinearLayout(this);
            linearLayout.setOrientation(1);
            final LanguageCell[] languageCellArr = new LanguageCell[2];
            String stringForLanguageAlert = getStringForLanguageAlert(this.systemLocaleStrings, "English", R.string.English);
            LocaleController.LocaleInfo[] localeInfoArr = {z ? localeInfo3 : localeInfo2, z ? localeInfo2 : localeInfo3};
            if (!z) {
                localeInfo3 = localeInfo2;
            }
            final LocaleController.LocaleInfo[] localeInfoArr2 = {localeInfo3};
            int i = 0;
            while (i < 2) {
                LanguageCell languageCell = new LanguageCell(this);
                languageCellArr[i] = languageCell;
                LocaleController.LocaleInfo localeInfo4 = localeInfoArr[i];
                languageCell.setLanguage(localeInfo4, localeInfo4 == localeInfo2 ? stringForLanguageAlert : null, true);
                languageCellArr[i].setTag(Integer.valueOf(i));
                languageCellArr[i].setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 2));
                languageCellArr[i].setLanguageSelected(i == 0, false);
                linearLayout.addView(languageCellArr[i], LayoutHelper.createLinear(-1, 50));
                languageCellArr[i].setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda147
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        LaunchActivity.$r8$lambda$pGF14W9lwDJF6Gvz7FDzKyY5F3I(localeInfoArr2, languageCellArr, view);
                    }
                });
                i++;
            }
            LanguageCell languageCell2 = new LanguageCell(this);
            languageCell2.setValue(getStringForLanguageAlert(this.systemLocaleStrings, "ChooseYourLanguageOther", R.string.ChooseYourLanguageOther), getStringForLanguageAlert(this.englishLocaleStrings, "ChooseYourLanguageOther", R.string.ChooseYourLanguageOther));
            languageCell2.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_dialogButtonSelector), 2));
            languageCell2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda148
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$showLanguageAlertInternal$175(view);
                }
            });
            linearLayout.addView(languageCell2, LayoutHelper.createLinear(-1, 50));
            builder.setView(linearLayout);
            builder.setNegativeButton(LocaleController.getString(R.string.OK), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda149
                @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                public final void onClick(AlertDialog alertDialog, int i2) {
                    this.f$0.lambda$showLanguageAlertInternal$176(localeInfoArr2, alertDialog, i2);
                }
            });
            this.localeDialog = showAlertDialog(builder);
            MessagesController.getGlobalMainSettings().edit().putString("language_showed2", str).apply();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static /* synthetic */ void $r8$lambda$pGF14W9lwDJF6Gvz7FDzKyY5F3I(LocaleController.LocaleInfo[] localeInfoArr, LanguageCell[] languageCellArr, View view) {
        Integer num = (Integer) view.getTag();
        localeInfoArr[0] = ((LanguageCell) view).getCurrentLocale();
        int i = 0;
        while (i < languageCellArr.length) {
            languageCellArr[i].setLanguageSelected(i == num.intValue(), true);
            i++;
        }
    }

    public /* synthetic */ void lambda$showLanguageAlertInternal$175(View view) {
        this.localeDialog = null;
        lambda$runLinkRequest$101(new LanguageSelectActivity());
        int i = 0;
        while (true) {
            int size = this.visibleDialogs.size();
            ArrayList<Dialog> arrayList = this.visibleDialogs;
            if (i < size) {
                if (arrayList.get(i).isShowing()) {
                    this.visibleDialogs.get(i).dismiss();
                }
                i++;
            } else {
                arrayList.clear();
                return;
            }
        }
    }

    public /* synthetic */ void lambda$showLanguageAlertInternal$176(LocaleController.LocaleInfo[] localeInfoArr, AlertDialog alertDialog, int i) {
        LocaleController.getInstance().applyLanguage(localeInfoArr[0], true, false, this.currentAccount);
        rebuildAllFragments(true);
    }

    public void drawRippleAbove(Canvas canvas, View view) {
        View view2;
        if (view == null || (view2 = this.rippleAbove) == null || view2.getBackground() == null) {
            return;
        }
        if (this.tempLocation == null) {
            this.tempLocation = new int[2];
        }
        this.rippleAbove.getLocationInWindow(this.tempLocation);
        int[] iArr = this.tempLocation;
        int i = iArr[0];
        int i2 = iArr[1];
        view.getLocationInWindow(iArr);
        int[] iArr2 = this.tempLocation;
        int i3 = i - iArr2[0];
        int i4 = i2 - iArr2[1];
        canvas.save();
        canvas.translate(i3, i4);
        this.rippleAbove.getBackground().draw(canvas);
        canvas.restore();
    }

    private void showLanguageAlert(boolean z) {
        String str;
        char c2;
        LocaleController.LocaleInfo localeInfo;
        if (UserConfig.getInstance(this.currentAccount).isClientActivated()) {
            try {
                if (!this.loadingLocaleDialog && !ApplicationLoader.mainInterfacePaused) {
                    String string = MessagesController.getGlobalMainSettings().getString("language_showed2", _UrlKt.FRAGMENT_ENCODE_SET);
                    final String str2 = MessagesController.getInstance(this.currentAccount).suggestedLangCode;
                    if (!z && string.equals(str2)) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("alert already showed for ".concat(string));
                            return;
                        }
                        return;
                    }
                    final LocaleController.LocaleInfo[] localeInfoArr = new LocaleController.LocaleInfo[2];
                    char c3 = 0;
                    String str3 = str2.contains("-") ? str2.split("-")[0] : str2;
                    if ("in".equals(str3)) {
                        str = "id";
                    } else if ("iw".equals(str3)) {
                        str = "he";
                    } else {
                        str = "jw".equals(str3) ? "jv" : null;
                    }
                    int i = 0;
                    while (true) {
                        if (i >= LocaleController.getInstance().languages.size()) {
                            c2 = c3;
                            break;
                        }
                        LocaleController.LocaleInfo localeInfo2 = LocaleController.getInstance().languages.get(i);
                        c2 = c3;
                        if (localeInfo2.shortName.equals("en")) {
                            localeInfoArr[c2] = localeInfo2;
                        }
                        if (localeInfo2.shortName.replace("_", "-").equals(str2) || localeInfo2.shortName.equals(str3) || localeInfo2.shortName.equals(str)) {
                            localeInfoArr[1] = localeInfo2;
                        }
                        if (localeInfoArr[c2] != null && localeInfoArr[1] != null) {
                            break;
                        }
                        i++;
                        c3 = c2;
                    }
                    LocaleController.LocaleInfo localeInfo3 = localeInfoArr[c2];
                    if (localeInfo3 != null && (localeInfo = localeInfoArr[1]) != null && localeInfo3 != localeInfo) {
                        if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("show lang alert for " + localeInfoArr[c2].getKey() + " and " + localeInfoArr[1].getKey());
                        }
                        this.systemLocaleStrings = null;
                        this.englishLocaleStrings = null;
                        this.loadingLocaleDialog = true;
                        TLRPC.TL_langpack_getStrings tL_langpack_getStrings = new TLRPC.TL_langpack_getStrings();
                        tL_langpack_getStrings.lang_code = localeInfoArr[1].getLangCode();
                        tL_langpack_getStrings.keys.add("English");
                        tL_langpack_getStrings.keys.add("ChooseYourLanguage");
                        tL_langpack_getStrings.keys.add("ChooseYourLanguageOther");
                        tL_langpack_getStrings.keys.add("ChangeLanguageLater");
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings, new RequestDelegate() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda43
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                this.f$0.lambda$showLanguageAlert$178(localeInfoArr, str2, tLObject, tL_error);
                            }
                        }, 8);
                        TLRPC.TL_langpack_getStrings tL_langpack_getStrings2 = new TLRPC.TL_langpack_getStrings();
                        tL_langpack_getStrings2.lang_code = localeInfoArr[c2].getLangCode();
                        tL_langpack_getStrings2.keys.add("English");
                        tL_langpack_getStrings2.keys.add("ChooseYourLanguage");
                        tL_langpack_getStrings2.keys.add("ChooseYourLanguageOther");
                        tL_langpack_getStrings2.keys.add("ChangeLanguageLater");
                        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_langpack_getStrings2, new RequestDelegate() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda44
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                this.f$0.lambda$showLanguageAlert$180(localeInfoArr, str2, tLObject, tL_error);
                            }
                        }, 8);
                    }
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    public /* synthetic */ void lambda$showLanguageAlert$178(final LocaleController.LocaleInfo[] localeInfoArr, final String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        final HashMap map = new HashMap();
        if (tLObject instanceof Vector) {
            Vector vector = (Vector) tLObject;
            for (int i = 0; i < vector.objects.size(); i++) {
                TLRPC.LangPackString langPackString = (TLRPC.LangPackString) vector.objects.get(i);
                map.put(langPackString.key, langPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda112
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showLanguageAlert$177(map, localeInfoArr, str);
            }
        });
    }

    public /* synthetic */ void lambda$showLanguageAlert$177(HashMap map, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.systemLocaleStrings = map;
        if (this.englishLocaleStrings != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    public /* synthetic */ void lambda$showLanguageAlert$180(final LocaleController.LocaleInfo[] localeInfoArr, final String str, TLObject tLObject, TLRPC.TL_error tL_error) {
        final HashMap map = new HashMap();
        if (tLObject instanceof Vector) {
            Vector vector = (Vector) tLObject;
            for (int i = 0; i < vector.objects.size(); i++) {
                TLRPC.LangPackString langPackString = (TLRPC.LangPackString) vector.objects.get(i);
                map.put(langPackString.key, langPackString.value);
            }
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda127
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showLanguageAlert$179(map, localeInfoArr, str);
            }
        });
    }

    public /* synthetic */ void lambda$showLanguageAlert$179(HashMap map, LocaleController.LocaleInfo[] localeInfoArr, String str) {
        this.englishLocaleStrings = map;
        if (this.systemLocaleStrings != null) {
            showLanguageAlertInternal(localeInfoArr[1], localeInfoArr[0], str);
        }
    }

    private void onPasscodePause() {
        int i;
        if (this.lockRunnable != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("cancel lockRunnable onPasscodePause");
            }
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (SharedConfig.passcodeHash.length() != 0) {
            SharedConfig.lastPauseTime = (int) (SystemClock.elapsedRealtime() / 1000);
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.LaunchActivity.19
                @Override // java.lang.Runnable
                public void run() {
                    if (LaunchActivity.this.lockRunnable == this) {
                        if (AndroidUtilities.needShowPasscode(true)) {
                            if (BuildVars.LOGS_ENABLED) {
                                FileLog.d("lock app");
                            }
                            LaunchActivity.this.showPasscodeActivity(true, false, -1, -1, null, null);
                            try {
                                NotificationsController.getInstance(UserConfig.selectedAccount).showNotifications();
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        } else if (BuildVars.LOGS_ENABLED) {
                            FileLog.d("didn't pass lock check");
                        }
                        LaunchActivity.this.lockRunnable = null;
                    }
                }
            };
            this.lockRunnable = runnable;
            if (SharedConfig.appLocked || (i = SharedConfig.autoLockIn) == Integer.MAX_VALUE) {
                AndroidUtilities.runOnUIThread(runnable, 1000L);
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("schedule app lock in 1000");
                }
            } else if (i != 0) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("schedule app lock in " + ((((long) SharedConfig.autoLockIn) * 1000) + 1000));
                }
                AndroidUtilities.runOnUIThread(this.lockRunnable, (((long) SharedConfig.autoLockIn) * 1000) + 1000);
            }
        } else {
            SharedConfig.lastPauseTime = 0;
        }
        SharedConfig.saveConfig();
    }

    public void addOverlayPasscodeView(PasscodeView passcodeView) {
        this.overlayPasscodeViews.add(passcodeView);
    }

    public void removeOverlayPasscodeView(PasscodeView passcodeView) {
        this.overlayPasscodeViews.remove(passcodeView);
    }

    private void onPasscodeResume() {
        if (this.lockRunnable != null) {
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("cancel lockRunnable onPasscodeResume");
            }
            AndroidUtilities.cancelRunOnUIThread(this.lockRunnable);
            this.lockRunnable = null;
        }
        if (AndroidUtilities.needShowPasscode(true)) {
            showPasscodeActivity(true, false, -1, -1, null, null);
        }
        if (SharedConfig.lastPauseTime != 0) {
            SharedConfig.lastPauseTime = 0;
            SharedConfig.saveConfig();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("reset lastPauseTime onPasscodeResume");
            }
        }
    }

    private void updateCurrentConnectionState() {
        int i;
        String str;
        if (this.actionBarLayout == null) {
            return;
        }
        int connectionState = ConnectionsManager.getInstance(this.currentAccount).getConnectionState();
        this.currentConnectionState = connectionState;
        if (connectionState == 2) {
            i = R.string.WaitingForNetwork;
            str = "WaitingForNetwork";
        } else if (connectionState == 5) {
            i = R.string.Updating;
            str = "Updating";
        } else if (connectionState == 4) {
            i = R.string.ConnectingToProxyWithDots;
            str = "ConnectingToProxyWithDots";
        } else if (connectionState == 1) {
            i = R.string.Connecting;
            str = "Connecting";
        } else {
            i = 0;
            str = null;
        }
        this.actionBarLayout.setTitleOverlayText(str, i, (connectionState == 1 || connectionState == 4) ? new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda39
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$updateCurrentConnectionState$181();
            }
        } : null);
    }

    /* JADX WARN: Code duplicated, block: B:10:0x0034  */
    public /* synthetic */ void lambda$updateCurrentConnectionState$181() {
        BaseFragment baseFragment;
        if (AndroidUtilities.isTablet()) {
            if (this.layerFragmentsStack.isEmpty()) {
                baseFragment = null;
            } else {
                ArrayList<BaseFragment> arrayList = this.layerFragmentsStack;
                baseFragment = arrayList.get(arrayList.size() - 1);
            }
        } else if (this.mainFragmentsStack.isEmpty()) {
            baseFragment = null;
        } else {
            ArrayList<BaseFragment> arrayList2 = this.mainFragmentsStack;
            baseFragment = arrayList2.get(arrayList2.size() - 1);
        }
        if ((baseFragment instanceof ProxyListActivity) || (baseFragment instanceof ProxySettingsActivity)) {
            return;
        }
        lambda$runLinkRequest$101(new ProxyListActivity());
    }

    public void hideVisibleActionMode() {
        ActionMode actionMode = this.visibleActionMode;
        if (actionMode == null) {
            return;
        }
        actionMode.finish();
    }

    /* JADX WARN: Code duplicated, block: B:20:0x00a1  */
    @Override // androidx.p002activity.ComponentActivity, androidx.core.app.ComponentActivity, android.app.Activity
    public void onSaveInstanceState(Bundle bundle) {
        BaseFragment baseFragment;
        try {
            super.onSaveInstanceState(bundle);
            if (AndroidUtilities.isTablet()) {
                ActionBarLayout actionBarLayout = this.layersActionBarLayout;
                if (actionBarLayout != null && !actionBarLayout.getFragmentStack().isEmpty()) {
                    baseFragment = this.layersActionBarLayout.getFragmentStack().get(this.layersActionBarLayout.getFragmentStack().size() - 1);
                } else {
                    ActionBarLayout actionBarLayout2 = this.rightActionBarLayout;
                    if (actionBarLayout2 != null && !actionBarLayout2.getFragmentStack().isEmpty()) {
                        baseFragment = this.rightActionBarLayout.getFragmentStack().get(this.rightActionBarLayout.getFragmentStack().size() - 1);
                    } else if (this.actionBarLayout.getFragmentStack().isEmpty()) {
                        baseFragment = null;
                    } else {
                        baseFragment = this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1);
                    }
                }
            } else if (this.actionBarLayout.getFragmentStack().isEmpty()) {
                baseFragment = null;
            } else {
                baseFragment = this.actionBarLayout.getFragmentStack().get(this.actionBarLayout.getFragmentStack().size() - 1);
            }
            if (baseFragment != null) {
                Bundle arguments = baseFragment.getArguments();
                if ((baseFragment instanceof ChatActivity) && arguments != null) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "chat");
                } else if ((baseFragment instanceof GroupCreateFinalActivity) && arguments != null) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "group");
                } else if (baseFragment instanceof WallpapersListActivity) {
                    bundle.putString("fragment", "wallpapers");
                } else if (baseFragment instanceof ProfileActivity) {
                    ProfileActivity profileActivity = (ProfileActivity) baseFragment;
                    if (profileActivity.isSettings()) {
                        bundle.putString("fragment", "settings");
                    } else if (profileActivity.isChat() && arguments != null) {
                        bundle.putBundle("args", arguments);
                        bundle.putString("fragment", "chat_profile");
                    }
                } else if ((baseFragment instanceof ChannelCreateActivity) && arguments != null && arguments.getInt("step") == 0) {
                    bundle.putBundle("args", arguments);
                    bundle.putString("fragment", "channel");
                } else if (baseFragment instanceof SettingsActivity) {
                    bundle.putString("fragment", "settings2");
                }
                baseFragment.saveSelfArgs(bundle);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override // androidx.p002activity.ComponentActivity, android.app.Activity
    public void onBackPressed() {
        if (onBackPressed(true)) {
            if (AndroidUtilities.isTablet()) {
                ActionBarLayout actionBarLayout = this.layersActionBarLayout;
                if (actionBarLayout != null && actionBarLayout.getView().getVisibility() == 0) {
                    this.layersActionBarLayout.onBackPressed();
                    return;
                }
                ActionBarLayout actionBarLayout2 = this.rightActionBarLayout;
                if (actionBarLayout2 != null && actionBarLayout2.getView().getVisibility() == 0 && !this.rightActionBarLayout.getFragmentStack().isEmpty()) {
                    BaseFragment baseFragment = this.rightActionBarLayout.getFragmentStack().get(this.rightActionBarLayout.getFragmentStack().size() - 1);
                    if (baseFragment.onBackPressed(true)) {
                        baseFragment.finishFragment();
                        return;
                    }
                    return;
                }
                this.actionBarLayout.onBackPressed();
                return;
            }
            this.actionBarLayout.onBackPressed();
        }
    }

    public boolean onBackPressed(boolean z) {
        if (FloatingDebugController.onBackPressed(z) || IconPickerController.onBackPressed(z)) {
            return false;
        }
        PasscodeViewDialog passcodeViewDialog = this.passcodeDialog;
        if (passcodeViewDialog != null && passcodeViewDialog.passcodeView.getVisibility() == 0) {
            if (z) {
                finish();
            }
            return false;
        }
        BottomSheetTabsOverlay bottomSheetTabsOverlay = this.bottomSheetTabsOverlay;
        if (bottomSheetTabsOverlay != null && bottomSheetTabsOverlay.isOpen) {
            if (z) {
                bottomSheetTabsOverlay.onBackPressed();
            }
            return false;
        }
        if (!SearchTagsList.onBackPressedRenameTagAlert(z)) {
            return false;
        }
        if (ContentPreviewViewer.hasInstance() && ContentPreviewViewer.getInstance().isVisible()) {
            if (z) {
                ContentPreviewViewer.getInstance().closeWithMenu();
            }
            return false;
        }
        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            if (z) {
                SecretMediaViewer.getInstance().closePhoto(true, false);
            }
            return false;
        }
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            if (z) {
                PhotoViewer.getInstance().closePhoto(true, false);
            }
            return false;
        }
        if (!ArticleViewer.hasInstance() || !ArticleViewer.getInstance().isVisible()) {
            return true;
        }
        if (z) {
            ArticleViewer.getInstance().close(true, false);
        }
        return false;
    }

    @Override // android.app.Activity, android.content.ComponentCallbacks
    public void onLowMemory() {
        super.onLowMemory();
        ActionBarLayout actionBarLayout = this.actionBarLayout;
        if (actionBarLayout != null) {
            actionBarLayout.onLowMemory();
            if (AndroidUtilities.isTablet()) {
                ActionBarLayout actionBarLayout2 = this.rightActionBarLayout;
                if (actionBarLayout2 != null) {
                    actionBarLayout2.onLowMemory();
                }
                ActionBarLayout actionBarLayout3 = this.layersActionBarLayout;
                if (actionBarLayout3 != null) {
                    actionBarLayout3.onLowMemory();
                }
            }
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onActionModeStarted(ActionMode actionMode) {
        super.onActionModeStarted(actionMode);
        this.visibleActionMode = actionMode;
        try {
            Menu menu = actionMode.getMenu();
            if (menu != null && !this.actionBarLayout.extendActionMode(menu) && AndroidUtilities.isTablet() && !this.rightActionBarLayout.extendActionMode(menu)) {
                this.layersActionBarLayout.extendActionMode(menu);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (actionMode.getType() == 1) {
            return;
        }
        this.actionBarLayout.onActionModeStarted(actionMode);
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onActionModeStarted(actionMode);
            this.layersActionBarLayout.onActionModeStarted(actionMode);
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public void onActionModeFinished(ActionMode actionMode) {
        super.onActionModeFinished(actionMode);
        if (this.visibleActionMode == actionMode) {
            this.visibleActionMode = null;
        }
        if (actionMode.getType() == 1) {
            return;
        }
        this.actionBarLayout.onActionModeFinished(actionMode);
        if (AndroidUtilities.isTablet()) {
            this.rightActionBarLayout.onActionModeFinished(actionMode);
            this.layersActionBarLayout.onActionModeFinished(actionMode);
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public boolean onPreIme() {
        if (SecretMediaViewer.hasInstance() && SecretMediaViewer.getInstance().isVisible()) {
            SecretMediaViewer.getInstance().closePhoto(true, false);
            return true;
        }
        if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
            PhotoViewer.getInstance().closePhoto(true, false);
            return true;
        }
        if (!ArticleViewer.hasInstance() || !ArticleViewer.getInstance().isVisible()) {
            return false;
        }
        ArticleViewer.getInstance().close(true, false);
        return true;
    }

    @Override // androidx.core.app.ComponentActivity, android.app.Activity, android.view.Window.Callback
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        BaseFragment lastFragment;
        int keyCode = keyEvent.getKeyCode();
        if ((keyEvent.getKeyCode() == 24 || keyEvent.getKeyCode() == 25) && (lastFragment = getLastFragment()) != null && lastFragment.getLastStoryViewer() != null) {
            lastFragment.getLastStoryViewer().dispatchKeyEvent(keyEvent);
            return true;
        }
        if (keyEvent.getKeyCode() == 24 || keyEvent.getKeyCode() == 25) {
            BaseFragment lastFragment2 = getLastFragment();
            if (lastFragment2 instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) lastFragment2;
                if (chatActivity.getInstantCameraView() != null && chatActivity.getInstantCameraView().isCameraReady()) {
                    chatActivity.getInstantCameraView().onKeyDown(keyCode, keyEvent);
                    return true;
                }
            }
        }
        if (keyEvent.getAction() == 0 && (keyEvent.getKeyCode() == 24 || keyEvent.getKeyCode() == 25)) {
            if (VoIPService.getSharedInstance() != null) {
                if (Build.VERSION.SDK_INT >= 32) {
                    boolean zIsSpeakerMuted = WebRtcAudioTrack.isSpeakerMuted();
                    AudioManager audioManager = (AudioManager) getSystemService(MediaStreamTrack.AUDIO_TRACK_KIND);
                    boolean z = audioManager.getStreamVolume(0) == audioManager.getStreamMinVolume(0) && keyEvent.getKeyCode() == 25;
                    WebRtcAudioTrack.setSpeakerMute(z);
                    if (zIsSpeakerMuted != WebRtcAudioTrack.isSpeakerMuted()) {
                        showVoiceChatTooltip(z ? 42 : 43);
                    }
                }
            } else if (ExteraConfig.getUnmuteWithVolumeButtons() && ((!PhotoViewer.hasInstance() || !PhotoViewer.getInstance().isVisible()) && keyEvent.getRepeatCount() == 0)) {
                ArrayList<BaseFragment> arrayList = this.mainFragmentsStack;
                BaseFragment baseFragment = arrayList.get(arrayList.size() - 1);
                if ((baseFragment instanceof ChatActivity) && !BaseFragment.hasSheets(baseFragment) && ((ChatActivity) baseFragment).maybePlayVisibleVideo()) {
                    return true;
                }
                if (AndroidUtilities.isTablet() && !this.rightFragmentsStack.isEmpty()) {
                    ArrayList<BaseFragment> arrayList2 = this.rightFragmentsStack;
                    BaseFragment baseFragment2 = arrayList2.get(arrayList2.size() - 1);
                    if ((baseFragment2 instanceof ChatActivity) && !BaseFragment.hasSheets(baseFragment2) && ((ChatActivity) baseFragment2).maybePlayVisibleVideo()) {
                        return true;
                    }
                }
            }
        }
        try {
            return super.dispatchKeyEvent(keyEvent);
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int i, KeyEvent keyEvent) {
        if (i == 82 && !SharedConfig.isWaitingForPasscodeEnter) {
            if (PhotoViewer.hasInstance() && PhotoViewer.getInstance().isVisible()) {
                return super.onKeyUp(i, keyEvent);
            }
            if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
                return super.onKeyUp(i, keyEvent);
            }
            if (AndroidUtilities.isTablet()) {
                if (this.layersActionBarLayout.getView().getVisibility() == 0 && !this.layersActionBarLayout.getFragmentStack().isEmpty()) {
                    this.layersActionBarLayout.getView().onKeyUp(i, keyEvent);
                } else if (this.rightActionBarLayout.getView().getVisibility() == 0 && !this.rightActionBarLayout.getFragmentStack().isEmpty()) {
                    this.rightActionBarLayout.getView().onKeyUp(i, keyEvent);
                } else {
                    this.actionBarLayout.getView().onKeyUp(i, keyEvent);
                }
            } else {
                this.actionBarLayout.getView().onKeyUp(i, keyEvent);
            }
        }
        return super.onKeyUp(i, keyEvent);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public boolean needPresentFragment(INavigationLayout iNavigationLayout, INavigationLayout.NavigationParams navigationParams) {
        ActionBarLayout actionBarLayout;
        ActionBarLayout actionBarLayout2;
        ActionBarLayout actionBarLayout3;
        ActionBarLayout actionBarLayout4;
        ActionBarLayout actionBarLayout5;
        ActionBarLayout actionBarLayout6;
        ActionBarLayout actionBarLayout7;
        ActionBarLayout actionBarLayout8;
        BaseFragment baseFragment = navigationParams.fragment;
        boolean z = navigationParams.removeLast;
        boolean z2 = navigationParams.noAnimation;
        if (ArticleViewer.hasInstance() && ArticleViewer.getInstance().isVisible()) {
            ArticleViewer.getInstance().close(false, true);
        }
        if (AndroidUtilities.isTablet()) {
            if (baseFragment instanceof MainTabsActivity) {
                ActionBarLayout actionBarLayout9 = this.actionBarLayout;
                if (iNavigationLayout != actionBarLayout9) {
                    actionBarLayout9.removeAllFragments();
                    getActionBarLayout().presentFragment(navigationParams.setRemoveLast(z).setNoAnimation(z2).setCheckPresentFromDelegate(false));
                    this.layersActionBarLayout.removeAllFragments();
                    this.layersActionBarLayout.getView().setVisibility(8);
                    if (!this.tabletFullSize) {
                        this.shadowTabletSide.setVisibility(0);
                        if (this.rightActionBarLayout.getFragmentStack().isEmpty()) {
                            this.backgroundTablet.setVisibility(0);
                        }
                    }
                    return false;
                }
            } else if (baseFragment instanceof DialogsActivity) {
                DialogsActivity dialogsActivity = (DialogsActivity) baseFragment;
                if (dialogsActivity.isMainDialogList() && iNavigationLayout != (actionBarLayout = this.actionBarLayout)) {
                    actionBarLayout.removeAllFragments();
                    getActionBarLayout().presentFragment(navigationParams.setRemoveLast(z).setNoAnimation(z2).setCheckPresentFromDelegate(false));
                    this.layersActionBarLayout.removeAllFragments();
                    this.layersActionBarLayout.getView().setVisibility(8);
                    if (!this.tabletFullSize) {
                        this.shadowTabletSide.setVisibility(0);
                        if (this.rightActionBarLayout.getFragmentStack().isEmpty()) {
                            this.backgroundTablet.setVisibility(0);
                        }
                    }
                    return false;
                }
                if (iNavigationLayout == this.actionBarLayout && dialogsActivity.getArguments() != null && dialogsActivity.getArguments().getInt("folderId", 0) == 1) {
                    return true;
                }
            }
            if (((baseFragment instanceof ChatActivity) && !((ChatActivity) baseFragment).isInScheduleMode()) || navigationParams.forceRightLayout || (iNavigationLayout == (actionBarLayout2 = this.rightActionBarLayout) && (actionBarLayout2.getFragmentStack().size() > 1 || !(this.rightActionBarLayout.getLastFragment() instanceof ChatActivity)))) {
                boolean z3 = this.tabletFullSize;
                if ((!z3 && iNavigationLayout == this.rightActionBarLayout) || (z3 && iNavigationLayout == this.actionBarLayout)) {
                    ActionBarLayout actionBarLayout10 = this.rightActionBarLayout;
                    if (iNavigationLayout == actionBarLayout10) {
                        if (actionBarLayout10.getView() != null) {
                            this.rightActionBarLayout.getView().setVisibility(0);
                        }
                        this.backgroundTablet.setVisibility(8);
                    }
                    boolean z4 = (this.tabletFullSize && iNavigationLayout == (actionBarLayout8 = this.actionBarLayout) && actionBarLayout8.getFragmentStack().size() == 1) ? false : true;
                    if (!this.layersActionBarLayout.getFragmentStack().isEmpty()) {
                        while (true) {
                            int size = this.layersActionBarLayout.getFragmentStack().size() - 1;
                            actionBarLayout7 = this.layersActionBarLayout;
                            if (size <= 0) {
                                break;
                            }
                            actionBarLayout7.removeFragmentFromStack(actionBarLayout7.getFragmentStack().get(0));
                        }
                        actionBarLayout7.closeLastFragment(!z2);
                    }
                    if (!z4) {
                        getActionBarLayout().presentFragment(navigationParams.setNoAnimation(z2).setCheckPresentFromDelegate(false));
                    }
                    return z4;
                }
                if (!z3 && iNavigationLayout != (actionBarLayout5 = this.rightActionBarLayout) && actionBarLayout5 != null) {
                    if (actionBarLayout5.getView() != null) {
                        this.rightActionBarLayout.getView().setVisibility(0);
                    }
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.presentFragment(navigationParams.setNoAnimation(true).setRemoveLast(z).setCheckPresentFromDelegate(false));
                    if (!this.layersActionBarLayout.getFragmentStack().isEmpty()) {
                        while (true) {
                            int size2 = this.layersActionBarLayout.getFragmentStack().size() - 1;
                            actionBarLayout6 = this.layersActionBarLayout;
                            if (size2 <= 0) {
                                break;
                            }
                            actionBarLayout6.removeFragmentFromStack(actionBarLayout6.getFragmentStack().get(0));
                        }
                        actionBarLayout6.closeLastFragment(!z2);
                    }
                    return false;
                }
                if (z3 && iNavigationLayout != this.actionBarLayout) {
                    getActionBarLayout().presentFragment(navigationParams.setRemoveLast(this.actionBarLayout.getFragmentStack().size() > 1).setNoAnimation(z2).setCheckPresentFromDelegate(false));
                    if (!this.layersActionBarLayout.getFragmentStack().isEmpty()) {
                        while (true) {
                            int size3 = this.layersActionBarLayout.getFragmentStack().size() - 1;
                            actionBarLayout4 = this.layersActionBarLayout;
                            if (size3 <= 0) {
                                break;
                            }
                            actionBarLayout4.removeFragmentFromStack(actionBarLayout4.getFragmentStack().get(0));
                        }
                        actionBarLayout4.closeLastFragment(!z2);
                    }
                    return false;
                }
                ActionBarLayout actionBarLayout11 = this.layersActionBarLayout;
                if (actionBarLayout11 != null && actionBarLayout11.getFragmentStack() != null && !this.layersActionBarLayout.getFragmentStack().isEmpty()) {
                    while (true) {
                        int size4 = this.layersActionBarLayout.getFragmentStack().size() - 1;
                        actionBarLayout3 = this.layersActionBarLayout;
                        if (size4 <= 0) {
                            break;
                        }
                        actionBarLayout3.removeFragmentFromStack(actionBarLayout3.getFragmentStack().get(0));
                    }
                    actionBarLayout3.closeLastFragment(!z2);
                }
                getActionBarLayout().presentFragment(navigationParams.setRemoveLast(this.actionBarLayout.getFragmentStack().size() > 1).setNoAnimation(z2).setCheckPresentFromDelegate(false));
                return false;
            }
            ActionBarLayout actionBarLayout12 = this.layersActionBarLayout;
            if (actionBarLayout12 != null && iNavigationLayout != actionBarLayout12) {
                actionBarLayout12.getView().setVisibility(0);
                int i = 0;
                while (true) {
                    if (i >= 16) {
                        i = -1;
                        break;
                    }
                    if (UserConfig.getInstance(i).isClientActivated()) {
                        break;
                    }
                    i++;
                }
                if ((baseFragment instanceof LoginActivity) && i == -1) {
                    this.backgroundTablet.setVisibility(0);
                    this.shadowTabletSide.setVisibility(8);
                    this.shadowTablet.setBackgroundColor(0);
                } else {
                    this.shadowTablet.setBackgroundColor(2130706432);
                }
                this.layersActionBarLayout.presentFragment(navigationParams.setRemoveLast(z).setNoAnimation(z2).setCheckPresentFromDelegate(false));
                return false;
            }
        }
        return true;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public boolean needAddFragmentToStack(BaseFragment baseFragment, INavigationLayout iNavigationLayout) {
        ActionBarLayout actionBarLayout;
        ActionBarLayout actionBarLayout2;
        ActionBarLayout actionBarLayout3;
        ActionBarLayout actionBarLayout4;
        if (AndroidUtilities.isTablet()) {
            boolean z = baseFragment instanceof DialogsActivity;
            if (z || (baseFragment instanceof MainTabsActivity)) {
                boolean z2 = iNavigationLayout != this.actionBarLayout;
                if (z2 && z && !((DialogsActivity) baseFragment).isMainDialogList()) {
                    z2 = false;
                }
                if (z2) {
                    this.actionBarLayout.removeAllFragments();
                    this.actionBarLayout.addFragmentToStack(baseFragment);
                    this.layersActionBarLayout.removeAllFragments();
                    this.layersActionBarLayout.getView().setVisibility(8);
                    if (!this.tabletFullSize) {
                        this.shadowTabletSide.setVisibility(0);
                        if (this.rightActionBarLayout.getFragmentStack().isEmpty()) {
                            this.backgroundTablet.setVisibility(0);
                        }
                    }
                    return false;
                }
            } else if ((baseFragment instanceof ChatActivity) && !((ChatActivity) baseFragment).isInScheduleMode()) {
                boolean z3 = this.tabletFullSize;
                if (!z3 && iNavigationLayout != (actionBarLayout3 = this.rightActionBarLayout)) {
                    actionBarLayout3.getView().setVisibility(0);
                    this.backgroundTablet.setVisibility(8);
                    this.rightActionBarLayout.removeAllFragments();
                    this.rightActionBarLayout.addFragmentToStack(baseFragment);
                    if (!this.layersActionBarLayout.getFragmentStack().isEmpty()) {
                        while (true) {
                            int size = this.layersActionBarLayout.getFragmentStack().size() - 1;
                            actionBarLayout4 = this.layersActionBarLayout;
                            if (size <= 0) {
                                break;
                            }
                            actionBarLayout4.removeFragmentFromStack(actionBarLayout4.getFragmentStack().get(0));
                        }
                        actionBarLayout4.closeLastFragment(true);
                    }
                    return false;
                }
                if (z3 && iNavigationLayout != (actionBarLayout = this.actionBarLayout)) {
                    actionBarLayout.addFragmentToStack(baseFragment);
                    if (!this.layersActionBarLayout.getFragmentStack().isEmpty()) {
                        while (true) {
                            int size2 = this.layersActionBarLayout.getFragmentStack().size() - 1;
                            actionBarLayout2 = this.layersActionBarLayout;
                            if (size2 <= 0) {
                                break;
                            }
                            actionBarLayout2.removeFragmentFromStack(actionBarLayout2.getFragmentStack().get(0));
                        }
                        actionBarLayout2.closeLastFragment(true);
                    }
                    return false;
                }
            } else {
                ActionBarLayout actionBarLayout5 = this.layersActionBarLayout;
                if (iNavigationLayout != actionBarLayout5) {
                    actionBarLayout5.getView().setVisibility(0);
                    int i = 0;
                    while (true) {
                        if (i >= 16) {
                            i = -1;
                            break;
                        }
                        if (UserConfig.getInstance(i).isClientActivated()) {
                            break;
                        }
                        i++;
                    }
                    if ((baseFragment instanceof LoginActivity) && i == -1) {
                        this.backgroundTablet.setVisibility(0);
                        this.shadowTabletSide.setVisibility(8);
                        this.shadowTablet.setBackgroundColor(0);
                    } else {
                        this.shadowTablet.setBackgroundColor(2130706432);
                    }
                    this.layersActionBarLayout.addFragmentToStack(baseFragment);
                    return false;
                }
            }
        }
        return true;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public boolean needCloseLastFragment(INavigationLayout iNavigationLayout) {
        if (AndroidUtilities.isTablet()) {
            if (iNavigationLayout == this.actionBarLayout && iNavigationLayout.getFragmentStack().size() <= 1 && !this.switchingAccount) {
                onFinish();
                finish();
                return false;
            }
            if (iNavigationLayout == this.rightActionBarLayout) {
                if (!this.tabletFullSize) {
                    this.backgroundTablet.setVisibility(0);
                }
            } else if (iNavigationLayout == this.layersActionBarLayout && this.actionBarLayout.getFragmentStack().isEmpty() && this.layersActionBarLayout.getFragmentStack().size() == 1) {
                onFinish();
                finish();
                return false;
            }
        } else if (iNavigationLayout.getFragmentStack().size() <= 1) {
            onFinish();
            finish();
            return false;
        }
        return true;
    }

    public void rebuildAllFragments(boolean z) {
        ActionBarLayout actionBarLayout = this.layersActionBarLayout;
        if (actionBarLayout != null) {
            actionBarLayout.rebuildAllFragmentViews(z, z);
        } else {
            this.actionBarLayout.rebuildAllFragmentViews(z, z);
        }
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout.INavigationLayoutDelegate
    public void onRebuildAllFragments(INavigationLayout iNavigationLayout, boolean z) {
        if (AndroidUtilities.isTablet() && iNavigationLayout == this.layersActionBarLayout) {
            this.rightActionBarLayout.rebuildAllFragmentViews(z, z);
            this.actionBarLayout.rebuildAllFragmentViews(z, z);
        }
    }

    public static BaseFragment getLastFragmentIncludeMainTabs() {
        BaseFragment lastFragment = getLastFragment();
        return lastFragment instanceof MainTabsActivity ? ((MainTabsActivity) lastFragment).getCurrentVisibleFragment() : lastFragment;
    }

    public static BaseFragment getLastFragment() {
        INavigationLayout iNavigationLayout;
        BubbleActivity bubbleActivity = BubbleActivity.instance;
        if (bubbleActivity != null && (iNavigationLayout = bubbleActivity.actionBarLayout) != null) {
            return iNavigationLayout.getLastFragment();
        }
        LaunchActivity launchActivity = instance;
        if (launchActivity != null && !launchActivity.sheetFragmentsStack.isEmpty()) {
            ArrayList<INavigationLayout> arrayList = instance.sheetFragmentsStack;
            return arrayList.get(arrayList.size() - 1).getLastFragment();
        }
        LaunchActivity launchActivity2 = instance;
        if (launchActivity2 == null || launchActivity2.getActionBarLayout() == null) {
            return null;
        }
        return instance.getActionBarLayout().getLastFragment();
    }

    public static <T extends BaseFragment> T findFragment(Class<T> cls) {
        INavigationLayout iNavigationLayout;
        BubbleActivity bubbleActivity = BubbleActivity.instance;
        if (bubbleActivity != null && (iNavigationLayout = bubbleActivity.actionBarLayout) != null) {
            return (T) iNavigationLayout.findFragment(cls);
        }
        LaunchActivity launchActivity = instance;
        if (launchActivity != null && !launchActivity.sheetFragmentsStack.isEmpty()) {
            ArrayList<INavigationLayout> arrayList = instance.sheetFragmentsStack;
            return (T) arrayList.get(arrayList.size() - 1).findFragment(cls);
        }
        LaunchActivity launchActivity2 = instance;
        if (launchActivity2 == null || launchActivity2.getActionBarLayout() == null) {
            return null;
        }
        return (T) instance.getActionBarLayout().findFragment(cls);
    }

    public static BaseFragment getSafeLastFragment() {
        INavigationLayout iNavigationLayout;
        BubbleActivity bubbleActivity = BubbleActivity.instance;
        if (bubbleActivity != null && (iNavigationLayout = bubbleActivity.actionBarLayout) != null) {
            return iNavigationLayout.getSafeLastFragment();
        }
        LaunchActivity launchActivity = instance;
        if (launchActivity != null && !launchActivity.sheetFragmentsStack.isEmpty()) {
            ArrayList<INavigationLayout> arrayList = instance.sheetFragmentsStack;
            return arrayList.get(arrayList.size() - 1).getSafeLastFragment();
        }
        LaunchActivity launchActivity2 = instance;
        if (launchActivity2 == null || launchActivity2.getActionBarLayout() == null) {
            return null;
        }
        return instance.getActionBarLayout().getSafeLastFragment();
    }

    public static List<BaseFragment> getVisibleFragments() {
        INavigationLayout iNavigationLayout;
        BaseFragment safeLastFragment;
        ArrayList arrayList = new ArrayList();
        BubbleActivity bubbleActivity = BubbleActivity.instance;
        if (bubbleActivity != null && (iNavigationLayout = bubbleActivity.actionBarLayout) != null && (safeLastFragment = iNavigationLayout.getSafeLastFragment()) != null) {
            arrayList.add(safeLastFragment);
        }
        LaunchActivity launchActivity = instance;
        if (launchActivity != null) {
            if (!launchActivity.sheetFragmentsStack.isEmpty()) {
                for (int i = 0; i < instance.sheetFragmentsStack.size(); i++) {
                    BaseFragment safeLastFragment2 = instance.sheetFragmentsStack.get(i).getSafeLastFragment();
                    if (safeLastFragment2 != null) {
                        arrayList.add(safeLastFragment2);
                    }
                }
            }
            if (!instance.layerFragmentsStack.isEmpty()) {
                ArrayList<BaseFragment> arrayList2 = instance.layerFragmentsStack;
                arrayList.add(arrayList2.get(arrayList2.size() - 1));
            }
            if (!instance.rightFragmentsStack.isEmpty()) {
                ArrayList<BaseFragment> arrayList3 = instance.rightFragmentsStack;
                arrayList.add(arrayList3.get(arrayList3.size() - 1));
            }
            if (!instance.mainFragmentsStack.isEmpty()) {
                ArrayList<BaseFragment> arrayList4 = instance.mainFragmentsStack;
                arrayList.add(arrayList4.get(arrayList4.size() - 1));
            }
        }
        return arrayList;
    }

    public int getNavigationBarColor() {
        if (Build.VERSION.SDK_INT >= 26) {
            return getWindow().getNavigationBarColor();
        }
        return 0;
    }

    public void setNavigationBarColor(int i) {
        this.drawerLayoutContainer.setInternalNavigationBarColor(i);
        BottomSheetTabs bottomSheetTabs = getBottomSheetTabs();
        if (bottomSheetTabs != null) {
            bottomSheetTabs.setNavigationBarColor(i);
        }
    }

    public BottomSheetTabs getBottomSheetTabs() {
        ActionBarLayout actionBarLayout = this.rightActionBarLayout;
        if (actionBarLayout != null && actionBarLayout.getBottomSheetTabs() != null) {
            return this.rightActionBarLayout.getBottomSheetTabs();
        }
        ActionBarLayout actionBarLayout2 = this.actionBarLayout;
        if (actionBarLayout2 == null || actionBarLayout2.getBottomSheetTabs() == null) {
            return null;
        }
        return this.actionBarLayout.getBottomSheetTabs();
    }

    @Deprecated
    public void animateNavigationBarColor(final int i) {
        if (Build.VERSION.SDK_INT < 26) {
            return;
        }
        ValueAnimator valueAnimator = this.navBarAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.navBarAnimator = null;
        }
        ValueAnimator valueAnimatorOfArgb = ValueAnimator.ofArgb(getNavigationBarColor(), i);
        this.navBarAnimator = valueAnimatorOfArgb;
        valueAnimatorOfArgb.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda54
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.lambda$animateNavigationBarColor$182(valueAnimator2);
            }
        });
        this.navBarAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.LaunchActivity.20
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                LaunchActivity.this.setNavigationBarColor(i);
            }
        });
        this.navBarAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.navBarAnimator.setDuration(320L);
        this.navBarAnimator.start();
    }

    public /* synthetic */ void lambda$animateNavigationBarColor$182(ValueAnimator valueAnimator) {
        setNavigationBarColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
    }

    public boolean isLightNavigationBar() {
        return AndroidUtilities.getLightNavigationBar(getWindow());
    }

    private void openStory(final long j, final int i, final boolean z) {
        TL_stories.StoryItem storyItem;
        StoriesController.StoriesList storiesList;
        StoriesController.StoriesList storiesList2;
        MessageObject messageObjectFindMessageObject;
        MessageObject messageObjectFindMessageObject2;
        StoriesController storiesController = MessagesController.getInstance(this.currentAccount).getStoriesController();
        TL_stories.PeerStories stories = storiesController.getStories(j);
        StoriesListPlaceProvider storiesListPlaceProviderOf = null;
        if (stories != null) {
            int i2 = 0;
            while (true) {
                if (i2 >= stories.stories.size()) {
                    storyItem = null;
                    break;
                } else {
                    if (stories.stories.get(i2).id == i) {
                        storyItem = stories.stories.get(i2);
                        break;
                    }
                    i2++;
                }
            }
            if (storyItem != null) {
                storyItem.dialogId = j;
                BaseFragment lastFragment = getLastFragment();
                if (lastFragment == null) {
                    return;
                }
                if (lastFragment instanceof DialogsActivity) {
                    try {
                        storiesListPlaceProviderOf = StoriesListPlaceProvider.of(((DialogsActivity) lastFragment).dialogStoriesCell.recyclerListView);
                    } catch (Exception unused) {
                    }
                }
                lastFragment.getOrCreateStoryViewer().instantClose();
                ArrayList<Long> arrayList = new ArrayList<>();
                arrayList.add(Long.valueOf(storyItem.dialogId));
                if (z) {
                    lastFragment.getOrCreateStoryViewer().showViewsAfterOpening();
                }
                lastFragment.getOrCreateStoryViewer().open(this, storyItem, arrayList, 0, null, stories, storiesListPlaceProviderOf, false);
                return;
            }
        } else {
            storyItem = null;
        }
        if (storyItem == null) {
            StoriesController.StoriesList storiesList3 = storiesController.getStoriesList(j, 0);
            if (storiesList3 == null || (messageObjectFindMessageObject2 = storiesList3.findMessageObject(i)) == null) {
                storiesList3 = null;
            } else {
                storyItem = messageObjectFindMessageObject2.storyItem;
            }
            if (storyItem != null || (storiesList2 = storiesController.getStoriesList(j, 1)) == null || (messageObjectFindMessageObject = storiesList2.findMessageObject(i)) == null) {
                storiesList = storiesList3;
            } else {
                storyItem = messageObjectFindMessageObject.storyItem;
                storiesList = storiesList2;
            }
            if (storyItem != null && storiesList != null) {
                storyItem.dialogId = j;
                BaseFragment lastFragment2 = getLastFragment();
                if (lastFragment2 == null) {
                    return;
                }
                if (lastFragment2 instanceof DialogsActivity) {
                    try {
                        storiesListPlaceProviderOf = StoriesListPlaceProvider.of(((DialogsActivity) lastFragment2).dialogStoriesCell.recyclerListView);
                    } catch (Exception unused2) {
                    }
                }
                lastFragment2.getOrCreateStoryViewer().instantClose();
                ArrayList<Long> arrayList2 = new ArrayList<>();
                arrayList2.add(Long.valueOf(storyItem.dialogId));
                if (z) {
                    lastFragment2.getOrCreateStoryViewer().showViewsAfterOpening();
                }
                lastFragment2.getOrCreateStoryViewer().open(this, storyItem, arrayList2, 0, storiesList, null, storiesListPlaceProviderOf, false);
                return;
            }
        }
        TL_stories.TL_stories_getStoriesByID tL_stories_getStoriesByID = new TL_stories.TL_stories_getStoriesByID();
        tL_stories_getStoriesByID.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
        tL_stories_getStoriesByID.id.add(Integer.valueOf(i));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_stories_getStoriesByID, new RequestDelegate() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda53
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$openStory$184(i, j, z, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$openStory$184(final int i, final long j, final boolean z, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda109
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openStory$183(tLObject, i, j, z);
            }
        });
    }

    public /* synthetic */ void lambda$openStory$183(TLObject tLObject, int i, long j, boolean z) {
        StoriesListPlaceProvider storiesListPlaceProviderOf;
        TL_stories.StoryItem storyItem;
        if (tLObject instanceof TL_stories.TL_stories_stories) {
            TL_stories.TL_stories_stories tL_stories_stories = (TL_stories.TL_stories_stories) tLObject;
            int i2 = 0;
            while (true) {
                storiesListPlaceProviderOf = null;
                if (i2 >= tL_stories_stories.stories.size()) {
                    storyItem = null;
                    break;
                } else {
                    if (tL_stories_stories.stories.get(i2).id == i) {
                        storyItem = tL_stories_stories.stories.get(i2);
                        break;
                    }
                    i2++;
                }
            }
            if (storyItem != null) {
                storyItem.dialogId = j;
                BaseFragment lastFragment = getLastFragment();
                if (lastFragment == null) {
                    return;
                }
                if (lastFragment instanceof DialogsActivity) {
                    try {
                        storiesListPlaceProviderOf = StoriesListPlaceProvider.of(((DialogsActivity) lastFragment).dialogStoriesCell.recyclerListView);
                    } catch (Exception unused) {
                    }
                }
                StoriesListPlaceProvider storiesListPlaceProvider = storiesListPlaceProviderOf;
                lastFragment.getOrCreateStoryViewer().instantClose();
                ArrayList<Long> arrayList = new ArrayList<>();
                arrayList.add(Long.valueOf(j));
                if (z) {
                    lastFragment.getOrCreateStoryViewer().showViewsAfterOpening();
                }
                lastFragment.getOrCreateStoryViewer().open(this, storyItem, arrayList, 0, null, null, storiesListPlaceProvider, false);
                return;
            }
        }
        BulletinFactory.global().createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.StoryNotFound)).show(false);
    }

    private void openStories(long[] jArr, boolean z) {
        boolean z2;
        final long[] array;
        StoriesListPlaceProvider storiesListPlaceProviderOf;
        int i = 0;
        while (true) {
            if (i >= jArr.length) {
                z2 = true;
                break;
            }
            TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(jArr[i]));
            if (user != null && !user.stories_hidden) {
                z2 = false;
                break;
            }
            i++;
        }
        BaseFragment lastFragment = getLastFragment();
        if (lastFragment == null) {
            return;
        }
        StoriesController storiesController = MessagesController.getInstance(this.currentAccount).getStoriesController();
        ArrayList arrayList = new ArrayList(z2 ? storiesController.getHiddenList() : storiesController.getDialogListStories());
        boolean z3 = z2;
        ArrayList<Long> arrayList2 = new ArrayList<>();
        ArrayList arrayList3 = new ArrayList();
        if (z3) {
            array = jArr;
        } else {
            ArrayList arrayList4 = new ArrayList();
            for (int i2 = 0; i2 < jArr.length; i2++) {
                TLRPC.User user2 = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(jArr[i2]));
                if (user2 == null || !user2.stories_hidden) {
                    arrayList4.add(Long.valueOf(jArr[i2]));
                }
            }
            array = Longs.toArray(arrayList4);
        }
        if (z) {
            for (long j : array) {
                arrayList3.add(Long.valueOf(j));
            }
        } else {
            for (long j2 : array) {
                arrayList2.add(Long.valueOf(j2));
            }
        }
        if (!arrayList3.isEmpty() && z) {
            final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
            final int[] iArr = {arrayList3.size()};
            final Runnable runnable = new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda48
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$openStories$185(iArr, array);
                }
            };
            for (int i3 = 0; i3 < arrayList3.size(); i3++) {
                final long jLongValue = ((Long) arrayList3.get(i3)).longValue();
                TL_stories.TL_stories_getPeerStories tL_stories_getPeerStories = new TL_stories.TL_stories_getPeerStories();
                TLRPC.InputPeer inputPeer = messagesController.getInputPeer(jLongValue);
                tL_stories_getPeerStories.peer = inputPeer;
                if (inputPeer instanceof TLRPC.TL_inputPeerEmpty) {
                    iArr[0] = iArr[0] - 1;
                } else if (inputPeer == null) {
                    iArr[0] = iArr[0] - 1;
                } else {
                    ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_stories_getPeerStories, new RequestDelegate() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda49
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LaunchActivity$$ExternalSyntheticLambda119
                                @Override // java.lang.Runnable
                                public final void run() {
                                    LaunchActivity.m15715$r8$lambda$g396DMT6FUNOZ_uEPabL1YbpE0(tLObject, messagesController, j, runnable);
                                }
                            });
                        }
                    });
                }
            }
            return;
        }
        long clientUserId = UserConfig.getInstance(this.currentAccount).getClientUserId();
        for (int i4 = 0; i4 < arrayList.size(); i4++) {
            long peerDialogId = DialogObject.getPeerDialogId(((TL_stories.PeerStories) arrayList.get(i4)).peer);
            if (peerDialogId != clientUserId && !arrayList2.contains(Long.valueOf(peerDialogId)) && storiesController.hasUnreadStories(peerDialogId)) {
                arrayList2.add(Long.valueOf(peerDialogId));
            }
        }
        if (arrayList2.isEmpty()) {
            return;
        }
        if (lastFragment instanceof DialogsActivity) {
            try {
                storiesListPlaceProviderOf = StoriesListPlaceProvider.of(((DialogsActivity) lastFragment).dialogStoriesCell.recyclerListView);
            } catch (Exception unused) {
                storiesListPlaceProviderOf = null;
            }
        } else {
            storiesListPlaceProviderOf = null;
        }
        StoriesListPlaceProvider storiesListPlaceProvider = storiesListPlaceProviderOf;
        lastFragment.getOrCreateStoryViewer().instantClose();
        lastFragment.getOrCreateStoryViewer().open(this, null, arrayList2, 0, null, null, storiesListPlaceProvider, false);
    }

    public /* synthetic */ void lambda$openStories$185(int[] iArr, long[] jArr) {
        int i = iArr[0] - 1;
        iArr[0] = i;
        if (i == 0) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesUpdated, new Object[0]);
            openStories(jArr, false);
        }
    }

    public static /* synthetic */ void m15715$r8$lambda$g396DMT6FUNOZ_uEPabL1YbpE0(TLObject tLObject, MessagesController messagesController, long j, Runnable runnable) {
        if (tLObject instanceof TL_stories.TL_stories_peerStories) {
            TL_stories.TL_stories_peerStories tL_stories_peerStories = (TL_stories.TL_stories_peerStories) tLObject;
            messagesController.putUsers(tL_stories_peerStories.users, false);
            messagesController.getStoriesController().putStories(j, tL_stories_peerStories.stories);
            runnable.run();
            return;
        }
        runnable.run();
    }

    public static void dismissAllWeb() {
        ArrayList<BaseFragment.AttachedSheet> arrayList;
        BaseFragment safeLastFragment = getSafeLastFragment();
        if (safeLastFragment == null) {
            return;
        }
        int i = 0;
        EmptyBaseFragment sheetFragment = safeLastFragment.getParentLayout() instanceof ActionBarLayout ? ((ActionBarLayout) safeLastFragment.getParentLayout()).getSheetFragment(false) : null;
        if (sheetFragment != null && (arrayList = sheetFragment.sheetsStack) != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                sheetFragment.sheetsStack.get(size).dismiss(true);
            }
        }
        ArrayList<BaseFragment.AttachedSheet> arrayList2 = safeLastFragment.sheetsStack;
        if (arrayList2 != null) {
            for (int size2 = arrayList2.size() - 1; size2 >= 0; size2--) {
                safeLastFragment.sheetsStack.get(size2).dismiss(true);
            }
        }
        ArrayList arrayList3 = new ArrayList();
        Iterator<BotWebViewSheet> it = BotWebViewSheet.activeSheets.iterator();
        while (it.hasNext()) {
            arrayList3.add(it.next());
        }
        int size3 = arrayList3.size();
        while (i < size3) {
            Object obj = arrayList3.get(i);
            i++;
            ((BotWebViewSheet) obj).dismiss(true);
        }
    }

    public static void makeRipple(float f, float f2, float f3) {
        LaunchActivity launchActivity = instance;
        if (launchActivity == null) {
            return;
        }
        launchActivity.makeRippleInternal(f, f2, f3);
    }

    private void makeRippleInternal(float f, float f2, float f3) {
        ISuperRipple iSuperRipple;
        View decorView = getWindow().getDecorView();
        if (decorView == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 33 && ((iSuperRipple = this.currentRipple) == null || iSuperRipple.view != decorView)) {
            this.currentRipple = new SuperRipple(decorView);
        }
        ISuperRipple iSuperRipple2 = this.currentRipple;
        if (iSuperRipple2 != null) {
            iSuperRipple2.animate(f, f2, f3);
        }
    }

    public int getMainFragmentsStackSize() {
        return this.mainFragmentsStack.size();
    }

    @Override 
    public PipActivityController getPipController() {
        return this.pipActivityController;
    }

    public void updateReasonsToHideMainContent(boolean z, boolean z2) {
        int i = this.reasonsToHideMainContent + (z ? 1 : -1);
        this.reasonsToHideMainContent = i;
        if (z2) {
            this.reasonsToHideDecorView += z ? 1 : -1;
        }
        FrameLayout frameLayout = this.frameLayout;
        if (frameLayout != null) {
            frameLayout.setVisibility(i > 0 ? 8 : 0);
        }
        checkDecorViewVisibility();
    }

    public void checkDecorViewVisibility() {
        getWindow();
    }

    public ActivityVisibilityController createActivityVisibilityController(boolean z) {
        return new ActivityVisibilityController(z);
    }

    public static WindowVisibilityManager.Controller obtainActivityVisibilityController() {
        LaunchActivity launchActivity = instance;
        if (launchActivity != null) {
            return launchActivity.createActivityVisibilityController(true);
        }
        return null;
    }

    public static class ActivityVisibilityController implements WindowVisibilityManager.Controller {
        private final WeakReference<LaunchActivity> activity;
        private boolean destroyed;
        private boolean hidden;
        private final boolean withDecorView;

        private ActivityVisibilityController(LaunchActivity launchActivity, boolean z) {
            this.activity = new WeakReference<>(launchActivity);
            this.withDecorView = z;
        }

        @Override 
        public void setHidden(boolean z) {
            if (this.hidden == z || this.destroyed) {
                return;
            }
            this.hidden = z;
            LaunchActivity launchActivity = this.activity.get();
            if (launchActivity != null) {
                launchActivity.updateReasonsToHideMainContent(z, this.withDecorView);
            }
        }

        @Override 
        public void destroy() {
            setHidden(false);
            this.destroyed = true;
        }

        public void show() {
            setHidden(false);
        }

        public void hide() {
            setHidden(true);
        }
    }
}
