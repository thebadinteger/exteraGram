package org.telegram.ui.web;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.PermissionRequest;
import android.webkit.RenderProcessGoneDetail;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.Consumer;
import androidx.mediarouter.media.MediaRouteProviderProtocol;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.adblock.AdBlockClient;
import com.exteragram.messenger.adblock.SelectorsObserver;
import com.exteragram.messenger.adblock.data.BlockResult;
import com.google.android.gms.cast.MediaError;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.IDN;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.io.encoding.Base64;
import okhttp3.internal.url._UrlKt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.messenger.AiTonesController$$ExternalSyntheticLambda0;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarLayout;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.BottomSheetTabs;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.CameraScanActivity;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CreateBotAlert;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.Views.LinkPreview;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.voip.CellFlickerDrawable;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.MultiContactsSelectorBottomSheet;
import org.telegram.ui.OAuthSheet;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stories.recorder.StoryEntry;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.TopicsFragment;
import org.telegram.ui.WrappedResourceProvider;
import org.telegram.ui.bots.BotBiometry;
import org.telegram.ui.bots.BotDownloads;
import org.telegram.ui.bots.BotLocation;
import org.telegram.ui.bots.BotSensors;
import org.telegram.ui.bots.BotShareSheet;
import org.telegram.ui.bots.BotStorage;
import org.telegram.ui.bots.BotWebViewSheet;
import org.telegram.ui.bots.ChatAttachAlertBotWebViewLayout;
import org.telegram.ui.bots.SetupEmojiStatusSheet;
import org.telegram.ui.bots.WebViewRequestProps;

public abstract class BotWebViewContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    public static boolean firstWebView = true;
    private static HashMap<String, String> rotatedTONHosts;
    private static int tags;
    private BotBiometry biometry;
    private long blockedDialogsUntil;
    public final boolean bot;
    private TLRPC.User botUser;
    private BotWebViewProxy botWebViewProxy;
    private String buttonData;
    private BottomSheet cameraBottomSheet;
    private int currentAccount;
    private AlertDialog currentDialog;
    private String currentPaymentSlug;
    private Delegate delegate;
    private int dialogSequentialOpenTimes;
    private BotDownloads downloads;
    private final CellFlickerDrawable flickerDrawable;
    private BackupImageView flickerView;
    private int flickerViewColor;
    private boolean flickerViewColorOverriden;
    private SvgHelper.SvgDrawable flickerViewDrawable;
    private int forceHeight;
    private boolean hasQRPending;
    private boolean hasUserPermissions;
    private boolean isBackButtonVisible;
    private boolean isFlickeringCenter;
    private boolean isPageLoaded;
    private boolean isRequestingPageOpen;
    private boolean isSettingsButtonVisible;
    private boolean isViewPortByMeasureSuppressed;
    private boolean keyboardFocusable;
    private int lastButtonColor;
    private String lastButtonText;
    private int lastButtonTextColor;
    private long lastClickMs;
    private long lastDialogClosed;
    private long lastDialogCooldownTime;
    private int lastDialogType;
    private boolean lastExpanded;
    private final Rect lastInsets;
    private int lastInsetsTopMargin;
    private long lastPostStoryMs;
    private String lastQrText;
    private int lastSecondaryButtonColor;
    private String lastSecondaryButtonPosition;
    private String lastSecondaryButtonText;
    private int lastSecondaryButtonTextColor;
    private int lastViewportHeightReported;
    private boolean lastViewportIsExpanded;
    private boolean lastViewportStateStable;
    private BotLocation location;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mUrl;
    private final Runnable notifyLocationChecked;
    private Runnable onCloseListener;
    private Runnable onPermissionsRequestResultCallback;
    private Utilities.Callback4<Boolean, Double, String, Double> onVerifiedAge;
    private MyWebView opener;
    private Activity parentActivity;
    private boolean preserving;
    private Theme.ResourcesProvider resourcesProvider;
    private String secondaryButtonData;
    private BotStorage secureStorage;
    private BotSensors sensors;
    private int shownDialogsCount;
    private BotStorage storage;
    private final int tag;
    private float viewPortHeightOffset;
    private boolean wasFocusable;
    private WebViewRequestProps wasOpenedByBot;
    private boolean wasOpenedByLinkIntent;
    private MyWebView webView;
    private boolean webViewNotAvailable;
    private TextView webViewNotAvailableText;
    private Consumer<Float> webViewProgressListener;
    private WebViewProxy webViewProxy;
    private WebViewScrollListener webViewScrollListener;

    public interface WebViewScrollListener {
        void onWebViewScrolled(WebView webView, int i, int i2);
    }

    public void onErrorShown(boolean z, int i, String str) {
    }

    public void onFaviconChanged(Bitmap bitmap) {
    }

    public void onTitleChanged(String str) {
    }

    public void onURLChanged(String str, boolean z, boolean z2) {
    }

    public void onWebViewCreated(MyWebView myWebView) {
    }

    public void onWebViewDestroyed(MyWebView myWebView) {
    }

    public void showLinkCopiedBulletin() {
        BulletinFactory.of(this, this.resourcesProvider).createCopyLinkBulletin().show(true);
    }

    public BotWebViewContainer(Context context, Theme.ResourcesProvider resourcesProvider, int i, boolean z) {
        super(context);
        CellFlickerDrawable cellFlickerDrawable = new CellFlickerDrawable();
        this.flickerDrawable = cellFlickerDrawable;
        int i2 = Theme.key_featuredStickers_addButton;
        this.lastButtonColor = getColor(i2);
        int i3 = Theme.key_featuredStickers_buttonText;
        this.lastButtonTextColor = getColor(i3);
        this.lastButtonText = _UrlKt.FRAGMENT_ENCODE_SET;
        this.lastSecondaryButtonColor = getColor(i2);
        this.lastSecondaryButtonTextColor = getColor(i3);
        this.lastSecondaryButtonText = _UrlKt.FRAGMENT_ENCODE_SET;
        this.lastSecondaryButtonPosition = _UrlKt.FRAGMENT_ENCODE_SET;
        this.currentAccount = UserConfig.selectedAccount;
        this.forceHeight = -1;
        this.lastInsets = new Rect(0, 0, 0, 0);
        this.lastInsetsTopMargin = 0;
        this.notifyLocationChecked = new Runnable() { // from class: org.telegram.ui.web.BotWebViewContainer$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$59();
            }
        };
        this.lastDialogType = -1;
        this.shownDialogsCount = 0;
        int i4 = tags;
        tags = i4 + 1;
        this.tag = i4;
        this.bot = z;
        this.resourcesProvider = resourcesProvider;
        d("created new webview container");
        if (context instanceof Activity) {
            this.parentActivity = (Activity) context;
        }
        cellFlickerDrawable.drawFrame = false;
        cellFlickerDrawable.setColors(i, 153, Opcodes.SUB_DOUBLE_2ADDR);
        BackupImageView backupImageView = new BackupImageView(context) { // from class: org.telegram.ui.web.BotWebViewContainer.1
            {
                this.imageReceiver = new C00691(this);
            }

            public class C00691 extends ImageReceiver {
                public C00691(View view) {
                    super(view);
                }

                @Override 
                public boolean setImageBitmapByKey(Drawable drawable, String str, int i, boolean z, int i2) {
                    boolean imageBitmapByKey = super.setImageBitmapByKey(drawable, str, i, z, i2);
                    ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(300L);
                    duration.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.web.BotWebViewContainer$1$1$$ExternalSyntheticLambda0
                        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                        public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                            this.f$0.lambda$setImageBitmapByKey$0(valueAnimator);
                        }
                    });
                    duration.start();
                    return imageBitmapByKey;
                }

                public void lambda$onWebEventReceived$6(final TLRPC.TL_messages_requestUrlAuth tL_messages_requestUrlAuth, final String str, final String str2, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.web.BotWebViewContainer$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$onWebEventReceived$5(tLObject, tL_messages_requestUrlAuth, str, tL_error, str2);
            }
        });
    }

    public void lambda$onEventReceived$7(PopupButton popupButton, AtomicBoolean atomicBoolean, AlertDialog alertDialog, int i) {
        alertDialog.dismiss();
        try {
            this.lastClickMs = System.currentTimeMillis();
            notifyEvent("popup_closed", new JSONObject().put("button_id", popupButton.id));
            atomicBoolean.set(true);
        } catch (JSONException e) {
            FileLog.e(e);
        }
    }

    public void lambda$onEventReceived$27(Boolean bool, String str) {
        if (bool.booleanValue()) {
            BotBiometry botBiometry = this.biometry;
            botBiometry.access_granted = true;
            botBiometry.save();
        }
        notifyBiometryReceived();
    }

    public void lambda$onEventReceived$40(JSONObject jSONObject) {
        notifyEvent("location_requested", jSONObject);
    }

    public void lambda$onEventReceived$43(String str, String str2, Boolean bool) {
        if (!bool.booleanValue()) {
            notifyEvent("file_download_requested", obj("status", "cancelled"));
        } else {
            this.downloads.download(str, str2);
            notifyEvent("file_download_requested", obj("status", "downloading"));
        }
    }

    public void lambda$onEventReceived$51(final String str, TLRPC.TL_keyboardButtonRequestPeer tL_keyboardButtonRequestPeer, final TLRPC.User user) {
        if (user == null) {
            notifyEvent("requested_chat_failed", obj("req_id", str));
            return;
        }
        TLRPC.TL_messages_sendBotRequestedPeer tL_messages_sendBotRequestedPeer = new TLRPC.TL_messages_sendBotRequestedPeer();
        tL_messages_sendBotRequestedPeer.peer = MessagesController.getInputPeer(this.botUser);
        tL_messages_sendBotRequestedPeer.webapp_req_id = str;
        tL_messages_sendBotRequestedPeer.button_id = tL_keyboardButtonRequestPeer.button_id;
        tL_messages_sendBotRequestedPeer.requested_peers.add(MessagesController.getInputPeer(user));
        ConnectionsManager.getInstance(this.currentAccount).sendRequestTyped(tL_messages_sendBotRequestedPeer, new AiTonesController$$ExternalSyntheticLambda0(), new Utilities.Callback2() { // from class: org.telegram.ui.web.BotWebViewContainer$$ExternalSyntheticLambda58
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$onEventReceived$50(str, user, (TLRPC.Updates) obj, (TLRPC.TL_error) obj2);
            }
        });
        notifyEvent("requested_chat_sent", obj("req_id", str));
    }

    public void lambda$onEventReceived$52(String str, TLRPC.Updates updates, TLRPC.TL_error tL_error) {
        if (updates != null) {
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
            notifyEvent("requested_chat_sent", obj("req_id", str));
            return;
        }
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (tL_error != null) {
            BulletinFactory.of(this, resourcesProvider).showForError(tL_error);
            notifyEvent("requested_chat_failed", obj("req_id", str));
        } else {
            BulletinFactory.of(this, resourcesProvider).showForError("UNKNOWN_BUTTON");
            notifyEvent("requested_chat_failed", obj("req_id", str));
        }
    }

    public void lambda$onEventReceived$55(String str, TLRPC.Updates updates, TLRPC.TL_error tL_error) {
        if (updates != null) {
            MessagesController.getInstance(this.currentAccount).processUpdates(updates, false);
            notifyEvent("requested_chat_sent", obj("req_id", str));
            return;
        }
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        if (tL_error != null) {
            BulletinFactory.of(this, resourcesProvider).showForError(tL_error);
            notifyEvent("requested_chat_failed", obj("req_id", str));
        } else {
            BulletinFactory.of(this, resourcesProvider).showForError("UNKNOWN_BUTTON");
            notifyEvent("requested_chat_failed", obj("req_id", str));
        }
    }

    private void setStorageKey(BotStorage botStorage, String str, String str2, String str3) {
        if (botStorage == null || this.botUser == null) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            String string = jSONObject.getString("req_id");
            try {
                String strOptString = jSONObject.optString("key");
                if (strOptString == null) {
                    notifyEvent(str3, obj("req_id", string, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "KEY_INVALID"));
                    return;
                }
                try {
                    try {
                        botStorage.setKey(strOptString, jSONObject.optString("value"));
                        notifyEvent(str2, obj("req_id", string));
                    } catch (RuntimeException e) {
                        notifyEvent(str3, obj("req_id", string, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, e.getMessage()));
                    }
                } catch (Exception unused) {
                    notifyEvent(str3, obj("req_id", string, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "VALUE_INVALID"));
                }
            } catch (Exception unused2) {
                notifyEvent(str3, obj("req_id", string, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "KEY_INVALID"));
            }
        } catch (Exception e2) {
            FileLog.e(e2);
            if (TextUtils.isEmpty(_UrlKt.FRAGMENT_ENCODE_SET)) {
                return;
            }
            notifyEvent(str3, obj("req_id", _UrlKt.FRAGMENT_ENCODE_SET, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "UNKNOWN_ERROR"));
        }
    }

    private void getStorageKey(BotStorage botStorage, String str, String str2, String str3) {
        Object obj;
        if (botStorage == null || this.botUser == null) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            String string = jSONObject.getString("req_id");
            try {
                String strOptString = jSONObject.optString("key");
                if (strOptString == null) {
                    notifyEvent(str3, obj("req_id", string, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "KEY_INVALID"));
                    return;
                }
                try {
                    Pair<String, Boolean> key = botStorage.getKey(strOptString);
                    if (botStorage.secured && (obj = key.first) == null) {
                        notifyEvent(str2, obj("req_id", string, "value", obj, "can_restore", key.second));
                    } else {
                        notifyEvent(str2, obj("req_id", string, "value", key.first));
                    }
                } catch (RuntimeException e) {
                    notifyEvent(str3, obj("req_id", string, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, e.getMessage()));
                }
            } catch (Exception unused) {
                notifyEvent(str3, obj("req_id", string, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "KEY_INVALID"));
            }
        } catch (Exception e2) {
            FileLog.e(e2);
            if (TextUtils.isEmpty(_UrlKt.FRAGMENT_ENCODE_SET)) {
                return;
            }
            notifyEvent(str3, obj("req_id", _UrlKt.FRAGMENT_ENCODE_SET, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "UNKNOWN_ERROR"));
        }
    }

    private void restoreStorageKey(final BotStorage botStorage, String str, final String str2, final String str3) {
        if (botStorage == null || this.botUser == null) {
            return;
        }
        try {
            JSONObject jSONObject = new JSONObject(str);
            final String string = jSONObject.getString("req_id");
            try {
                final String strOptString = jSONObject.optString("key");
                if (strOptString == null) {
                    notifyEvent(str3, obj("req_id", string, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "KEY_INVALID"));
                    return;
                }
                try {
                    List<BotStorage.StorageConfig> storagesWithKey = botStorage.getStoragesWithKey(strOptString);
                    if (storagesWithKey.isEmpty()) {
                        notifyEvent(str3, obj("req_id", string, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "RESTORE_UNAVAILABLE"));
                    } else {
                        botStorage.showChooseStorage(getContext(), storagesWithKey, new Utilities.Callback() { // from class: org.telegram.ui.web.BotWebViewContainer$$ExternalSyntheticLambda47
                            @Override 
                            public final void run(Object obj) {
                                this.f$0.lambda$restoreStorageKey$58(str3, string, botStorage, strOptString, str2, (String) obj);
                            }
                        });
                    }
                } catch (Exception e) {
                    notifyEvent(str3, obj("req_id", string, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, e.getMessage()));
                }
            } catch (Exception unused) {
                notifyEvent(str3, obj("req_id", string, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "KEY_INVALID"));
            }
        } catch (Exception e2) {
            FileLog.e(e2);
            if (TextUtils.isEmpty(_UrlKt.FRAGMENT_ENCODE_SET)) {
                return;
            }
            notifyEvent(str3, obj("req_id", _UrlKt.FRAGMENT_ENCODE_SET, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, "UNKNOWN_ERROR"));
        }
    }

    public void lambda$resolveShare$1(Boolean bool) {
            MyWebView myWebView = this.webView;
            StringBuilder sb = new StringBuilder("window.navigator.__share__receive(");
            sb.append(bool.booleanValue() ? _UrlKt.FRAGMENT_ENCODE_SET : "'abort'");
            sb.append(")");
            myWebView.evaluateJS(sb.toString());
        }
    }

    public interface Delegate {
        default BotSensors getBotSensors() {
            return null;
        }

        default boolean isClipboardAvailable() {
            return false;
        }

        void onCloseRequested(Runnable runnable);

        default void onEmojiStatusGranted(boolean z) {
        }

        default void onEmojiStatusSet(TLRPC.Document document) {
        }

        default void onLocationGranted(boolean z) {
        }

        default void onOpenBackFromTabs() {
        }

        default void onOrientationLockChanged(boolean z) {
        }

        default void onSendWebViewData(String str) {
        }

        void onSetBackButtonVisible(boolean z);

        void onSetSettingsButtonVisible(boolean z);

        void onSetupMainButton(boolean z, boolean z2, String str, long j, int i, int i2, boolean z3, boolean z4);

        void onSetupSecondaryButton(boolean z, boolean z2, String str, long j, int i, int i2, boolean z3, boolean z4, String str2);

        default void onSharedTo(ArrayList<Long> arrayList) {
        }

        default void onWebAppBackgroundChanged(boolean z, int i) {
        }

        void onWebAppExpand();

        void onWebAppOpenInvoice(TLRPC.InputInvoice inputInvoice, String str, TLObject tLObject);

        default void onWebAppReady() {
        }

        void onWebAppSetActionBarColor(int i, int i2, boolean z);

        void onWebAppSetBackgroundColor(int i);

        default void onWebAppSetNavigationBarColor(int i) {
        }

        void onWebAppSetupClosingBehavior(boolean z);

        void onWebAppSwipingBehavior(boolean z);

        void onWebAppSwitchInlineQuery(TLRPC.User user, String str, List<String> list);

        default void onInstantClose() {
            onCloseRequested(null);
        }

        default void onCloseToTabs() {
            onCloseRequested(null);
        }

        default String onFullscreenRequested(boolean z, boolean z2) {
            return "UNSUPPORTED";
        }
    }

    public static final class PopupButton {
        public String id;
        public String text;
        public int textColorKey;

        public PopupButton(JSONObject jSONObject) throws JSONException {
            this.textColorKey = -1;
            this.id = jSONObject.getString("id");
            String string = jSONObject.getString(TeXSymbolParser.TYPE_ATTR);
            switch (string.hashCode()) {
                case -1829997182:
                    if (string.equals("destructive")) {
                        this.textColorKey = Theme.key_text_RedBold;
                    }
                    break;
                case -1367724422:
                    if (string.equals("cancel")) {
                        this.text = LocaleController.getString(R.string.Cancel);
                        return;
                    }
                    break;
                case 3548:
                    if (string.equals("ok")) {
                        this.text = LocaleController.getString(R.string.OK);
                        return;
                    }
                    break;
                case 94756344:
                    if (string.equals("close")) {
                        this.text = LocaleController.getString(R.string.Close);
                        return;
                    }
                    break;
                case 1544803905:
                    string.equals("default");
                    break;
            }
            this.text = jSONObject.getString("text");
        }
    }

    public static boolean isTonsite(String str) {
        return str != null && isTonsite(Uri.parse(str));
    }

    public static boolean isTonsite(Uri uri) {
        if ("tonsite".equals(uri.getScheme())) {
            return true;
        }
        String authority = uri.getAuthority();
        if (authority == null && uri.getScheme() == null) {
            authority = Uri.parse("http://" + uri.toString()).getAuthority();
        }
        if (authority != null) {
            return authority.endsWith(".ton") || authority.endsWith(".adnl");
        }
        return false;
    }

    public static WebResourceResponse proxyTON(WebResourceRequest webResourceRequest) {
        return proxyTON(webResourceRequest.getMethod(), webResourceRequest.getUrl().toString(), webResourceRequest.getRequestHeaders());
    }

    public static String rotateTONHost(String str) {
        try {
            str = IDN.toASCII(str, 1);
        } catch (Exception e) {
            FileLog.e(e);
        }
        String[] strArrSplit = str.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strArrSplit.length; i++) {
            if (i > 0) {
                sb.append("-d");
            }
            sb.append(strArrSplit[i].replaceAll("\\-", "-h"));
        }
        sb.append(".");
        sb.append(MessagesController.getInstance(UserConfig.selectedAccount).tonProxyAddress);
        return sb.toString();
    }

    public static WebResourceResponse proxyTON(String str, String str2, Map<String, String> map) {
        try {
            HttpURLConnection httpURLConnection = (HttpURLConnection) new URL(Browser.replaceHostname(Uri.parse(str2), rotateTONHost(AndroidUtilities.getHostAuthority(str2)), "https")).openConnection();
            httpURLConnection.setRequestMethod(str);
            if (map != null) {
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    httpURLConnection.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            httpURLConnection.connect();
            return new WebResourceResponse(httpURLConnection.getContentType().split(";", 2)[0], httpURLConnection.getContentEncoding(), httpURLConnection.getInputStream());
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static class MyWebView extends WebView {
        public boolean allowBlockedPageLoad;
        public final boolean bot;
        private BotWebViewContainer botWebViewContainer;
        private BrowserHistory.Entry currentHistoryEntry;
        private boolean currentPageWasBlocked;
        private BottomSheet currentSheet;
        private String currentUrl;
        public boolean dangerousUrl;
        public boolean errorShown;
        public String errorShownAt;
        public boolean injectedJS;
        private boolean isPageLoaded;
        public int lastActionBarColor;
        public boolean lastActionBarColorGot;
        public int lastBackgroundColor;
        public boolean lastBackgroundColorGot;
        public Bitmap lastFavicon;
        public boolean lastFaviconGot;
        private String lastFaviconUrl;
        private HashMap<String, Bitmap> lastFavicons;
        public String lastSiteName;
        public String lastTitle;
        public boolean lastTitleGot;
        private String lastUrl;
        private Runnable onCloseListener;
        private String openedByUrl;
        public MyWebView opener;
        private int prevScrollX;
        private int prevScrollY;
        private int searchCount;
        private int searchIndex;
        private Runnable searchListener;
        private boolean searchLoading;
        private SelectorsObserver selectorsObserver;
        private final int tag;
        public String urlFallback;
        private WebViewScrollListener webViewScrollListener;
        private Runnable whenPageLoaded;

        public static void m21998$r8$lambda$LMHOtTZ1WIWSZO19WLo8NoNkI(boolean[] zArr, JsPromptResult jsPromptResult, AlertDialog alertDialog, int i) {
                if (zArr[0]) {
                    return;
                }
                zArr[0] = true;
                jsPromptResult.cancel();
            }

            public static /* synthetic */ void $r8$lambda$FlsajW65O1qximWu9YktPhKFj1I(boolean[] zArr, JsPromptResult jsPromptResult, EditTextCaption editTextCaption, AlertDialog alertDialog, int i) {
                if (zArr[0]) {
                    return;
                }
                zArr[0] = true;
                jsPromptResult.confirm(editTextCaption.getText().toString());
            }

            public static /* synthetic */ void $r8$lambda$46yWweFpbnX7LDYh2i48Aea3vk4(boolean[] zArr, JsPromptResult jsPromptResult, DialogInterface dialogInterface) {
                if (zArr[0]) {
                    return;
                }
                zArr[0] = true;
                jsPromptResult.cancel();
            }

            public static /* synthetic */ void $r8$lambda$LbrLC539usybksXm7pXYylqEpbM(EditTextCaption editTextCaption, Runnable runnable) {
                AndroidUtilities.hideKeyboard(editTextCaption);
                AndroidUtilities.runOnUIThread(runnable, 80L);
            }

            @Override // android.webkit.WebChromeClient
            public void onReceivedIcon(WebView webView, Bitmap bitmap) {
                String str;
                MyWebView myWebView = MyWebView.this;
                if (bitmap == null) {
                    str = "null";
                } else {
                    str = bitmap.getWidth() + "x" + bitmap.getHeight();
                }
                myWebView.d("onReceivedIcon favicon=".concat(str));
                if (bitmap != null && (!TextUtils.equals(MyWebView.this.getUrl(), MyWebView.this.lastFaviconUrl) || MyWebView.this.lastFavicon == null || bitmap.getWidth() > MyWebView.this.lastFavicon.getWidth())) {
                    MyWebView myWebView2 = MyWebView.this;
                    myWebView2.lastFavicon = bitmap;
                    myWebView2.lastFaviconUrl = myWebView2.getUrl();
                    MyWebView myWebView3 = MyWebView.this;
                    myWebView3.lastFaviconGot = true;
                    myWebView3.saveHistory();
                }
                Bitmap bitmap2 = (Bitmap) MyWebView.this.lastFavicons.get(MyWebView.this.getUrl());
                if (bitmap != null && (bitmap2 == null || bitmap2.getWidth() < bitmap.getWidth())) {
                    MyWebView.this.lastFavicons.put(MyWebView.this.getUrl(), bitmap);
                }
                if (MyWebView.this.botWebViewContainer != null) {
                    MyWebView.this.botWebViewContainer.onFaviconChanged(bitmap);
                }
                super.onReceivedIcon(webView, bitmap);
            }

            @Override // android.webkit.WebChromeClient
            public void onReceivedTitle(WebView webView, String str) {
                MyWebView.this.d("onReceivedTitle title=" + str);
                MyWebView myWebView = MyWebView.this;
                if (!myWebView.errorShown) {
                    myWebView.lastTitleGot = true;
                    myWebView.lastTitle = str;
                }
                if (myWebView.botWebViewContainer != null) {
                    MyWebView.this.botWebViewContainer.onTitleChanged(str);
                }
                super.onReceivedTitle(webView, str);
            }

            @Override // android.webkit.WebChromeClient
            public void onReceivedTouchIconUrl(WebView webView, String str, boolean z) {
                MyWebView.this.d("onReceivedTouchIconUrl url=" + str + " precomposed=" + z);
                super.onReceivedTouchIconUrl(webView, str, z);
            }

            @Override // android.webkit.WebChromeClient
            public boolean onCreateWindow(WebView webView, boolean z, boolean z2, Message message) {
                BaseFragment safeLastFragment;
                MyWebView.this.d("onCreateWindow isDialog=" + z + " isUserGesture=" + z2 + " resultMsg=" + message);
                String url = MyWebView.this.getUrl();
                if (MessagesController.getInstance(UserConfig.selectedAccount).isWebBrowserInAppEnabled()) {
                    if (MyWebView.this.botWebViewContainer == null || (safeLastFragment = LaunchActivity.getSafeLastFragment()) == null) {
                        return false;
                    }
                    if (safeLastFragment.getParentLayout() instanceof ActionBarLayout) {
                        safeLastFragment = ((ActionBarLayout) safeLastFragment.getParentLayout()).getSheetFragment();
                    }
                    ArticleViewer articleViewerCreateArticleViewer = safeLastFragment.createArticleViewer(true);
                    articleViewerCreateArticleViewer.setOpener(MyWebView.this);
                    articleViewerCreateArticleViewer.open((String) null);
                    MyWebView lastWebView = articleViewerCreateArticleViewer.getLastWebView();
                    if (!TextUtils.isEmpty(url)) {
                        lastWebView.urlFallback = url;
                    }
                    MyWebView.this.d("onCreateWindow: newWebView=" + lastWebView);
                    if (lastWebView != null) {
                        ((WebView.WebViewTransport) message.obj).setWebView(lastWebView);
                        message.sendToTarget();
                        return true;
                    }
                    articleViewerCreateArticleViewer.close(true, true);
                    return false;
                }
                WebView webView2 = new WebView(webView.getContext());
                webView2.setWebViewClient(new AnonymousClass2(webView2));
                ((WebView.WebViewTransport) message.obj).setWebView(webView2);
                message.sendToTarget();
                return true;
            }

            public class AnonymousClass2 extends WebViewClient {
                final /* synthetic */ WebView val$newWebView;

                public AnonymousClass2(WebView webView) {
                    this.val$newWebView = webView;
                }

                @Override // android.webkit.WebViewClient
                public boolean onRenderProcessGone(WebView webView, RenderProcessGoneDetail renderProcessGoneDetail) {
                    int i = Build.VERSION.SDK_INT;
                    AnonymousClass3 anonymousClass3 = AnonymousClass3.this;
                    if (i >= 26) {
                        MyWebView myWebView = MyWebView.this;
                        StringBuilder sb = new StringBuilder("newWebView.onRenderProcessGone priority=");
                        sb.append(renderProcessGoneDetail == null ? null : Integer.valueOf(renderProcessGoneDetail.rendererPriorityAtExit()));
                        sb.append(" didCrash=");
                        sb.append(renderProcessGoneDetail == null ? null : Boolean.valueOf(renderProcessGoneDetail.didCrash()));
                        myWebView.d(sb.toString());
                    } else {
                        MyWebView.this.d("newWebView.onRenderProcessGone");
                    }
                    try {
                        if (!AndroidUtilities.isSafeToShow(MyWebView.this.getContext())) {
                            return true;
                        }
                        new AlertDialog.Builder(MyWebView.this.getContext(), MyWebView.this.botWebViewContainer == null ? null : MyWebView.this.botWebViewContainer.resourcesProvider).setTitle(LocaleController.getString(R.string.ChromeCrashTitle)).setMessage(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.ChromeCrashMessage), new Runnable() { // from class: org.telegram.ui.web.BotWebViewContainer$MyWebView$3$2$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$onRenderProcessGone$0();
                            }
                        })).setPositiveButton(LocaleController.getString(R.string.OK), null).setOnDismissListener(new DialogInterface.OnDismissListener() { // from class: org.telegram.ui.web.BotWebViewContainer$MyWebView$3$2$$ExternalSyntheticLambda1
                            @Override // android.content.DialogInterface.OnDismissListener
                            public final void onDismiss(DialogInterface dialogInterface) {
                                this.f$0.lambda$onRenderProcessGone$1(dialogInterface);
                            }
                        }).show();
                        return true;
                    } catch (Exception e) {
                        FileLog.e(e);
                        return false;
                    }
                }

                public /* synthetic */ void lambda$onRenderProcessGone$0() {
                    Browser.openUrl(MyWebView.this.getContext(), "https://play.google.com/store/apps/details?id=com.google.android.webview");
                }

                public /* synthetic */ void lambda$onRenderProcessGone$1(DialogInterface dialogInterface) {
                    if (MyWebView.this.botWebViewContainer.delegate != null) {
                        MyWebView.this.botWebViewContainer.delegate.onCloseRequested(null);
                    }
                }

                @Override // android.webkit.WebViewClient
                public boolean shouldOverrideUrlLoading(WebView webView, String str) {
                    if (MyWebView.this.botWebViewContainer == null) {
                        return true;
                    }
                    MyWebView.this.botWebViewContainer.onOpenUri(Uri.parse(str));
                    this.val$newWebView.destroy();
                    return true;
                }
            }

            @Override // android.webkit.WebChromeClient
            public void onCloseWindow(WebView webView) {
                MyWebView.this.d("onCloseWindow " + webView);
                if (MyWebView.this.botWebViewContainer != null && MyWebView.this.botWebViewContainer.delegate != null) {
                    MyWebView.this.botWebViewContainer.delegate.onCloseRequested(null);
                } else if (MyWebView.this.onCloseListener != null) {
                    MyWebView.this.onCloseListener.run();
                    MyWebView.this.onCloseListener = null;
                }
                super.onCloseWindow(webView);
            }

            @Override // android.webkit.WebChromeClient
            public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> valueCallback, WebChromeClient.FileChooserParams fileChooserParams) {
                Activity activityFindActivity = AndroidUtilities.findActivity(MyWebView.this.getContext());
                MyWebView myWebView = MyWebView.this;
                if (activityFindActivity == null) {
                    myWebView.d("onShowFileChooser: no activity, false");
                    return false;
                }
                BotWebViewContainer botWebViewContainer = myWebView.botWebViewContainer;
                MyWebView myWebView2 = MyWebView.this;
                if (botWebViewContainer == null) {
                    myWebView2.d("onShowFileChooser: no container, false");
                    return false;
                }
                if (myWebView2.botWebViewContainer.mFilePathCallback != null) {
                    MyWebView.this.botWebViewContainer.mFilePathCallback.onReceiveValue(null);
                }
                MyWebView.this.botWebViewContainer.mFilePathCallback = valueCallback;
                boolean z = fileChooserParams.getMode() == 1;
                Intent intentCreateIntent = fileChooserParams.createIntent();
                if (z) {
                    intentCreateIntent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
                }
                activityFindActivity.startActivityForResult(intentCreateIntent, 3000);
                MyWebView.this.d("onShowFileChooser: true");
                return true;
            }

            @Override // android.webkit.WebChromeClient
            public void onProgressChanged(WebView webView, int i) {
                if (MyWebView.this.botWebViewContainer != null && MyWebView.this.botWebViewContainer.webViewProgressListener != null) {
                    MyWebView.this.d("onProgressChanged " + i + "%");
                    MyWebView.this.botWebViewContainer.webViewProgressListener.accept(Float.valueOf(((float) i) / 100.0f));
                    return;
                }
                MyWebView.this.d("onProgressChanged " + i + "%: no container");
            }

            @Override // android.webkit.WebChromeClient
            public void onGeolocationPermissionsShowPrompt(final String str, final GeolocationPermissions.Callback callback) {
                if (MyWebView.this.botWebViewContainer == null || MyWebView.this.botWebViewContainer.parentActivity == null) {
                    MyWebView.this.d("onGeolocationPermissionsShowPrompt: no container");
                    callback.invoke(str, false, false);
                    return;
                }
                MyWebView.this.d("onGeolocationPermissionsShowPrompt " + str);
                boolean z = this.val$bot;
                MyWebView myWebView = MyWebView.this;
                String userName = z ? UserObject.getUserName(myWebView.botWebViewContainer.botUser) : AndroidUtilities.getHostAuthority(myWebView.getUrl());
                Dialog dialogCreateWebViewPermissionsRequestDialog = AlertsCreator.createWebViewPermissionsRequestDialog(MyWebView.this.botWebViewContainer.parentActivity, MyWebView.this.botWebViewContainer.resourcesProvider, new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, R.raw.permission_request_location, LocaleController.formatString(this.val$bot ? R.string.BotWebViewRequestGeolocationPermission : R.string.WebViewRequestGeolocationPermission, userName), LocaleController.formatString(this.val$bot ? R.string.BotWebViewRequestGeolocationPermissionWithHint : R.string.WebViewRequestGeolocationPermissionWithHint, userName), new Consumer() { // from class: org.telegram.ui.web.BotWebViewContainer$MyWebView$3$$ExternalSyntheticLambda13
                    @Override // androidx.core.util.Consumer
                    public final void accept(Object obj) {
                        this.f$0.lambda$onGeolocationPermissionsShowPrompt$11(callback, str, (Boolean) obj);
                    }
                });
                this.lastPermissionsDialog = dialogCreateWebViewPermissionsRequestDialog;
                dialogCreateWebViewPermissionsRequestDialog.show();
            }

            public /* synthetic */ void lambda$onGeolocationPermissionsShowPrompt$11(final GeolocationPermissions.Callback callback, final String str, Boolean bool) {
                if (this.lastPermissionsDialog != null) {
                    this.lastPermissionsDialog = null;
                    if (bool.booleanValue()) {
                        MyWebView.this.botWebViewContainer.runWithPermissions(new String[]{"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"}, new Consumer() { // from class: org.telegram.ui.web.BotWebViewContainer$MyWebView$3$$ExternalSyntheticLambda16
                            @Override // androidx.core.util.Consumer
                            public final void accept(Object obj) {
                                this.f$0.lambda$onGeolocationPermissionsShowPrompt$10(callback, str, (Boolean) obj);
                            }
                        });
                    } else {
                        callback.invoke(str, false, false);
                    }
                }
            }

            public /* synthetic */ void lambda$onGeolocationPermissionsShowPrompt$10(GeolocationPermissions.Callback callback, String str, Boolean bool) {
                callback.invoke(str, bool.booleanValue(), false);
                if (bool.booleanValue()) {
                    MyWebView.this.botWebViewContainer.hasUserPermissions = true;
                }
            }

            @Override // android.webkit.WebChromeClient
            public void onGeolocationPermissionsHidePrompt() {
                Dialog dialog = this.lastPermissionsDialog;
                MyWebView myWebView = MyWebView.this;
                if (dialog != null) {
                    myWebView.d("onGeolocationPermissionsHidePrompt: dialog.dismiss");
                    this.lastPermissionsDialog.dismiss();
                    this.lastPermissionsDialog = null;
                    return;
                }
                myWebView.d("onGeolocationPermissionsHidePrompt: no dialog");
            }

            @Override // android.webkit.WebChromeClient
            public void onPermissionRequest(final PermissionRequest permissionRequest) {
                Dialog dialog = this.lastPermissionsDialog;
                if (dialog != null) {
                    dialog.dismiss();
                    this.lastPermissionsDialog = null;
                }
                BotWebViewContainer botWebViewContainer = MyWebView.this.botWebViewContainer;
                MyWebView myWebView = MyWebView.this;
                if (botWebViewContainer == null) {
                    myWebView.d("onPermissionRequest: no container");
                    permissionRequest.deny();
                    return;
                }
                myWebView.d("onPermissionRequest " + permissionRequest);
                boolean z = this.val$bot;
                MyWebView myWebView2 = MyWebView.this;
                String userName = z ? UserObject.getUserName(myWebView2.botWebViewContainer.botUser) : AndroidUtilities.getHostAuthority(myWebView2.getUrl());
                final String[] resources = permissionRequest.getResources();
                if (resources.length == 1) {
                    final String str = resources[0];
                    if (MyWebView.this.botWebViewContainer.parentActivity == null) {
                        permissionRequest.deny();
                        return;
                    }
                    if (MyWebView.this.botWebViewContainer.isVerifyingAge()) {
                        permissionRequest.grant(resources);
                        return;
                    }
                    str.getClass();
                    if (str.equals("android.webkit.resource.VIDEO_CAPTURE")) {
                        Dialog dialogCreateWebViewPermissionsRequestDialog = AlertsCreator.createWebViewPermissionsRequestDialog(MyWebView.this.botWebViewContainer.parentActivity, MyWebView.this.botWebViewContainer.resourcesProvider, new String[]{"android.permission.CAMERA"}, R.raw.permission_request_camera, LocaleController.formatString(this.val$bot ? R.string.BotWebViewRequestCameraPermission : R.string.WebViewRequestCameraPermission, userName), LocaleController.formatString(this.val$bot ? R.string.BotWebViewRequestCameraPermissionWithHint : R.string.WebViewRequestCameraPermissionWithHint, userName), new Consumer() { // from class: org.telegram.ui.web.BotWebViewContainer$MyWebView$3$$ExternalSyntheticLambda11
                            @Override // androidx.core.util.Consumer
                            public final void accept(Object obj) {
                                this.f$0.lambda$onPermissionRequest$15(permissionRequest, str, (Boolean) obj);
                            }
                        });
                        this.lastPermissionsDialog = dialogCreateWebViewPermissionsRequestDialog;
                        dialogCreateWebViewPermissionsRequestDialog.show();
                        return;
                    } else {
                        if (str.equals("android.webkit.resource.AUDIO_CAPTURE")) {
                            Dialog dialogCreateWebViewPermissionsRequestDialog2 = AlertsCreator.createWebViewPermissionsRequestDialog(MyWebView.this.botWebViewContainer.parentActivity, MyWebView.this.botWebViewContainer.resourcesProvider, new String[]{"android.permission.RECORD_AUDIO"}, R.raw.permission_request_microphone, LocaleController.formatString(this.val$bot ? R.string.BotWebViewRequestMicrophonePermission : R.string.WebViewRequestMicrophonePermission, userName), LocaleController.formatString(this.val$bot ? R.string.BotWebViewRequestMicrophonePermissionWithHint : R.string.WebViewRequestMicrophonePermissionWithHint, userName), new Consumer() { // from class: org.telegram.ui.web.BotWebViewContainer$MyWebView$3$$ExternalSyntheticLambda10
                                @Override // androidx.core.util.Consumer
                                public final void accept(Object obj) {
                                    this.f$0.lambda$onPermissionRequest$13(permissionRequest, str, (Boolean) obj);
                                }
                            });
                            this.lastPermissionsDialog = dialogCreateWebViewPermissionsRequestDialog2;
                            dialogCreateWebViewPermissionsRequestDialog2.show();
                            return;
                        }
                        return;
                    }
                }
                if (resources.length == 2) {
                    if ("android.webkit.resource.AUDIO_CAPTURE".equals(resources[0]) || "android.webkit.resource.VIDEO_CAPTURE".equals(resources[0])) {
                        if ("android.webkit.resource.AUDIO_CAPTURE".equals(resources[1]) || "android.webkit.resource.VIDEO_CAPTURE".equals(resources[1])) {
                            Dialog dialogCreateWebViewPermissionsRequestDialog3 = AlertsCreator.createWebViewPermissionsRequestDialog(MyWebView.this.botWebViewContainer.parentActivity, MyWebView.this.botWebViewContainer.resourcesProvider, new String[]{"android.permission.CAMERA", "android.permission.RECORD_AUDIO"}, R.raw.permission_request_camera, LocaleController.formatString(this.val$bot ? R.string.BotWebViewRequestCameraMicPermission : R.string.WebViewRequestCameraMicPermission, userName), LocaleController.formatString(this.val$bot ? R.string.BotWebViewRequestCameraMicPermissionWithHint : R.string.WebViewRequestCameraMicPermissionWithHint, userName), new Consumer() { // from class: org.telegram.ui.web.BotWebViewContainer$MyWebView$3$$ExternalSyntheticLambda12
                                @Override // androidx.core.util.Consumer
                                public final void accept(Object obj) {
                                    this.f$0.lambda$onPermissionRequest$17(permissionRequest, resources, (Boolean) obj);
                                }
                            });
                            this.lastPermissionsDialog = dialogCreateWebViewPermissionsRequestDialog3;
                            dialogCreateWebViewPermissionsRequestDialog3.show();
                        }
                    }
                }
            }

            public /* synthetic */ void lambda$onPermissionRequest$13(final PermissionRequest permissionRequest, final String str, Boolean bool) {
                if (this.lastPermissionsDialog != null) {
                    this.lastPermissionsDialog = null;
                    if (bool.booleanValue()) {
                        MyWebView.this.botWebViewContainer.runWithPermissions(new String[]{"android.permission.RECORD_AUDIO"}, new Consumer() { // from class: org.telegram.ui.web.BotWebViewContainer$MyWebView$3$$ExternalSyntheticLambda15
                            @Override // androidx.core.util.Consumer
                            public final void accept(Object obj) {
                                this.f$0.lambda$onPermissionRequest$12(permissionRequest, str, (Boolean) obj);
                            }
                        });
                    } else {
                        permissionRequest.deny();
                    }
                }
            }

            public /* synthetic */ void lambda$onPermissionRequest$12(PermissionRequest permissionRequest, String str, Boolean bool) {
                if (bool.booleanValue()) {
                    permissionRequest.grant(new String[]{str});
                    MyWebView.this.botWebViewContainer.hasUserPermissions = true;
                } else {
                    permissionRequest.deny();
                }
            }

            public /* synthetic */ void lambda$onPermissionRequest$15(final PermissionRequest permissionRequest, final String str, Boolean bool) {
                if (this.lastPermissionsDialog != null) {
                    this.lastPermissionsDialog = null;
                    if (bool.booleanValue()) {
                        MyWebView.this.botWebViewContainer.runWithPermissions(new String[]{"android.permission.CAMERA"}, new Consumer() { // from class: org.telegram.ui.web.BotWebViewContainer$MyWebView$3$$ExternalSyntheticLambda17
                            @Override // androidx.core.util.Consumer
                            public final void accept(Object obj) {
                                this.f$0.lambda$onPermissionRequest$14(permissionRequest, str, (Boolean) obj);
                            }
                        });
                    } else {
                        permissionRequest.deny();
                    }
                }
            }

            public /* synthetic */ void lambda$onPermissionRequest$14(PermissionRequest permissionRequest, String str, Boolean bool) {
                if (bool.booleanValue()) {
                    permissionRequest.grant(new String[]{str});
                    MyWebView.this.botWebViewContainer.hasUserPermissions = true;
                } else {
                    permissionRequest.deny();
                }
            }

            public /* synthetic */ void lambda$onPermissionRequest$17(final PermissionRequest permissionRequest, final String[] strArr, Boolean bool) {
                if (this.lastPermissionsDialog != null) {
                    this.lastPermissionsDialog = null;
                    if (bool.booleanValue()) {
                        MyWebView.this.botWebViewContainer.runWithPermissions(new String[]{"android.permission.CAMERA", "android.permission.RECORD_AUDIO"}, new Consumer() { // from class: org.telegram.ui.web.BotWebViewContainer$MyWebView$3$$ExternalSyntheticLambda14
                            @Override // androidx.core.util.Consumer
                            public final void accept(Object obj) {
                                this.f$0.lambda$onPermissionRequest$16(permissionRequest, strArr, (Boolean) obj);
                            }
                        });
                    } else {
                        permissionRequest.deny();
                    }
                }
            }

            public /* synthetic */ void lambda$onPermissionRequest$16(PermissionRequest permissionRequest, String[] strArr, Boolean bool) {
                if (bool.booleanValue()) {
                    permissionRequest.grant(new String[]{strArr[0], strArr[1]});
                    MyWebView.this.botWebViewContainer.hasUserPermissions = true;
                } else {
                    permissionRequest.deny();
                }
            }

            @Override // android.webkit.WebChromeClient
            public void onPermissionRequestCanceled(PermissionRequest permissionRequest) {
                Dialog dialog = this.lastPermissionsDialog;
                MyWebView myWebView = MyWebView.this;
                if (dialog != null) {
                    myWebView.d("onPermissionRequestCanceled: dialog.dismiss");
                    this.lastPermissionsDialog.dismiss();
                    this.lastPermissionsDialog = null;
                    return;
                }
                myWebView.d("onPermissionRequestCanceled: no dialog");
            }

            @Override // android.webkit.WebChromeClient
            public Bitmap getDefaultVideoPoster() {
                return Bitmap.createBitmap(10, 10, Bitmap.Config.ARGB_8888);
            }
        }

        public class AnonymousClass5 implements DownloadListener {
            public AnonymousClass5() {
            }

            private String getFilename(String str, String str2, String str3) {
                try {
                    List<String> pathSegments = Uri.parse(str).getPathSegments();
                    String str4 = pathSegments.get(pathSegments.size() - 1);
                    int iLastIndexOf = str4.lastIndexOf(".");
                    if (iLastIndexOf > 0 && !TextUtils.isEmpty(str4.substring(iLastIndexOf + 1))) {
                        return str4;
                    }
                } catch (Exception unused) {
                }
                return URLUtil.guessFileName(str, str2, str3);
            }

            @Override // android.webkit.DownloadListener
            public void onDownloadStart(final String str, final String str2, String str3, final String str4, long j) {
                MyWebView.this.d("onDownloadStart " + str + " " + str2 + " " + str3 + " " + str4 + " " + j);
                try {
                    if (str.startsWith("blob:")) {
                        return;
                    }
                    final String strEscape = AndroidUtilities.escape(getFilename(str, str3, str4));
                    final Runnable runnable = new Runnable() { // from class: org.telegram.ui.web.BotWebViewContainer$MyWebView$5$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.lambda$onDownloadStart$0(str, str4, str2, strEscape);
                        }
                    };
                    if (!DownloadController.getInstance(UserConfig.selectedAccount).canDownloadMedia(8, j)) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MyWebView.this.getContext());
                        builder.setTitle(LocaleController.getString(R.string.WebDownloadAlertTitle));
                        builder.setMessage(AndroidUtilities.replaceTags(j > 0 ? LocaleController.formatString(R.string.WebDownloadAlertInfoWithSize, strEscape, AndroidUtilities.formatFileSize(j)) : LocaleController.formatString(R.string.WebDownloadAlertInfo, strEscape)));
                        builder.setPositiveButton(LocaleController.getString(R.string.WebDownloadAlertYes), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.web.BotWebViewContainer$MyWebView$5$$ExternalSyntheticLambda1
                            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                            public final void onClick(AlertDialog alertDialog, int i) {
                                runnable.run();
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                        TextView textView = (TextView) builder.show().getButton(-2);
                        if (textView != null) {
                            textView.setTextColor(Theme.getColor(Theme.key_text_RedBold));
                            return;
                        }
                        return;
                    }
                    runnable.run();
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }

            public /* synthetic */ void lambda$onDownloadStart$0(String str, String str2, String str3, String str4) {
                try {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(str));
                    request.setMimeType(str2);
                    request.addRequestHeader("User-Agent", str3);
                    request.setDescription(LocaleController.getString(R.string.WebDownloading));
                    request.setTitle(str4);
                    request.setNotificationVisibility(1);
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, str4);
                    DownloadManager downloadManager = (DownloadManager) MyWebView.this.getContext().getSystemService("download");
                    if (downloadManager != null) {
                        downloadManager.enqueue(request);
                    }
                    if (MyWebView.this.botWebViewContainer != null) {
                        BulletinFactory.of(MyWebView.this.botWebViewContainer, MyWebView.this.botWebViewContainer.resourcesProvider).createSimpleBulletin(R.raw.ic_download, AndroidUtilities.replaceTags(LocaleController.formatString(R.string.WebDownloadingFile, str4))).show(true);
                    }
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
        }

        public void saveHistory() {
            if (this.bot) {
                return;
            }
            WebMetadataCache.WebMetadata webMetadataFrom = WebMetadataCache.WebMetadata.from(this);
            WebMetadataCache.getInstance().save(webMetadataFrom);
            BrowserHistory.Entry entry = this.currentHistoryEntry;
            if (entry == null || webMetadataFrom == null) {
                return;
            }
            entry.meta = webMetadataFrom;
            BrowserHistory.pushHistory(entry);
        }

        public void search(String str, Runnable runnable) {
            this.searchLoading = true;
            this.searchListener = runnable;
            findAllAsync(str);
        }

        public int getSearchIndex() {
            return this.searchIndex;
        }

        public int getSearchCount() {
            return this.searchCount;
        }

        @Override // android.webkit.WebView
        public String getTitle() {
            return this.lastTitle;
        }

        public void setTitle(String str) {
            this.lastTitle = str;
        }

        public String getOpenURL() {
            return this.openedByUrl;
        }

        @Override // android.webkit.WebView
        public String getUrl() {
            if (this.dangerousUrl) {
                return this.urlFallback;
            }
            String url = super.getUrl();
            this.lastUrl = url;
            return url;
        }

        public boolean isUrlDangerous() {
            return this.dangerousUrl;
        }

        @Override // android.webkit.WebView
        public Bitmap getFavicon() {
            if (this.errorShown) {
                return null;
            }
            return this.lastFavicon;
        }

        public Bitmap getFavicon(String str) {
            return this.lastFavicons.get(str);
        }

        public void setContainers(BotWebViewContainer botWebViewContainer, WebViewScrollListener webViewScrollListener) {
            d("setContainers(" + botWebViewContainer + ", " + webViewScrollListener + ")");
            boolean z = this.botWebViewContainer == null && botWebViewContainer != null;
            this.botWebViewContainer = botWebViewContainer;
            this.webViewScrollListener = webViewScrollListener;
            if (z) {
                evaluateJS("window.__tg__postBackgroundChange()");
            }
        }

        public void setCloseListener(Runnable runnable) {
            this.onCloseListener = runnable;
        }

        public void evaluateJS(String str) {
            evaluateJavascript(str, new ValueCallback() { // from class: org.telegram.ui.web.BotWebViewContainer$MyWebView$$ExternalSyntheticLambda0
                @Override // android.webkit.ValueCallback
                public final void onReceiveValue(Object obj) {
                    BotWebViewContainer.MyWebView.m21969$r8$lambda$n4G84VuBJd3zHi0noWbm8zvFCg((String) obj);
                }
            });
        }

        @Override // android.webkit.WebView, android.view.View
        public void onScrollChanged(int i, int i2, int i3, int i4) {
            super.onScrollChanged(i, i2, i3, i4);
            WebViewScrollListener webViewScrollListener = this.webViewScrollListener;
            if (webViewScrollListener != null) {
                webViewScrollListener.onWebViewScrolled(this, getScrollX() - this.prevScrollX, getScrollY() - this.prevScrollY);
            }
            this.prevScrollX = getScrollX();
            this.prevScrollY = getScrollY();
        }

        public float getScrollProgress() {
            float fMax = Math.max(1, computeVerticalScrollRange() - computeVerticalScrollExtent());
            if (fMax <= getHeight()) {
                return 0.0f;
            }
            return Utilities.clamp01(getScrollY() / fMax);
        }

        public void setScrollProgress(float f) {
            setScrollY((int) (f * Math.max(1, computeVerticalScrollRange() - computeVerticalScrollExtent())));
        }

        @Override // android.view.View
        public void setScrollX(int i) {
            super.setScrollX(i);
            this.prevScrollX = i;
        }

        @Override // android.view.View
        public void setScrollY(int i) {
            super.setScrollY(i);
            this.prevScrollY = i;
        }

        @Override // android.webkit.WebView, android.view.View
        public boolean onCheckIsTextEditor() {
            BotWebViewContainer botWebViewContainer = this.botWebViewContainer;
            if (botWebViewContainer == null) {
                d("onCheckIsTextEditor: no container");
                return false;
            }
            boolean zIsFocusable = botWebViewContainer.isFocusable();
            d("onCheckIsTextEditor: " + zIsFocusable);
            return zIsFocusable;
        }

        @Override // android.webkit.WebView, android.widget.AbsoluteLayout, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), TLObject.FLAG_30));
        }

        @Override // android.webkit.WebView, android.view.View
        @SuppressLint({"ClickableViewAccessibility"})
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                this.botWebViewContainer.lastClickMs = System.currentTimeMillis();
                if (!this.botWebViewContainer.isVerifyingAge()) {
                    getSettings().setMediaPlaybackRequiresUserGesture(false);
                }
            }
            return super.onTouchEvent(motionEvent);
        }

        @Override // android.webkit.WebView, android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            d("attached");
            AndroidUtilities.checkAndroidTheme(getContext(), true);
            super.onAttachedToWindow();
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            d("detached");
            AndroidUtilities.checkAndroidTheme(getContext(), false);
            super.onDetachedFromWindow();
        }

        @Override // android.webkit.WebView
        public void destroy() {
            d("destroy");
            super.destroy();
        }

        @Override // android.webkit.WebView
        public void loadUrl(String str) {
            BottomSheet bottomSheet = this.currentSheet;
            if (bottomSheet != null) {
                bottomSheet.dismiss();
                this.currentSheet = null;
            }
            checkCachedMetaProperties(str);
            this.openedByUrl = str;
            String str2 = BotWebViewContainer.tonsite2magic(str);
            this.currentUrl = str2;
            d("loadUrl " + str2);
            super.loadUrl(str2);
            BotWebViewContainer botWebViewContainer = this.botWebViewContainer;
            if (botWebViewContainer != null) {
                if (this.dangerousUrl) {
                    str2 = this.urlFallback;
                }
                botWebViewContainer.onURLChanged(str2, !canGoBack(), !canGoForward());
            }
        }

        @Override // android.webkit.WebView
        public void loadUrl(String str, Map<String, String> map) {
            BottomSheet bottomSheet = this.currentSheet;
            if (bottomSheet != null) {
                bottomSheet.dismiss();
                this.currentSheet = null;
            }
            checkCachedMetaProperties(str);
            this.openedByUrl = str;
            String str2 = BotWebViewContainer.tonsite2magic(str);
            this.currentUrl = str2;
            d("loadUrl " + str2 + " " + map);
            super.loadUrl(str2, map);
            BotWebViewContainer botWebViewContainer = this.botWebViewContainer;
            if (botWebViewContainer != null) {
                if (this.dangerousUrl) {
                    str2 = this.urlFallback;
                }
                botWebViewContainer.onURLChanged(str2, !canGoBack(), !canGoForward());
            }
        }

        public void loadUrl(String str, WebMetadataCache.WebMetadata webMetadata) {
            BottomSheet bottomSheet = this.currentSheet;
            if (bottomSheet != null) {
                bottomSheet.dismiss();
                this.currentSheet = null;
            }
            applyCachedMeta(webMetadata);
            this.openedByUrl = str;
            String str2 = BotWebViewContainer.tonsite2magic(str);
            this.currentUrl = str2;
            d("loadUrl " + str2 + " with cached meta");
            super.loadUrl(str2);
            BotWebViewContainer botWebViewContainer = this.botWebViewContainer;
            if (botWebViewContainer != null) {
                if (this.dangerousUrl) {
                    str2 = this.urlFallback;
                }
                botWebViewContainer.onURLChanged(str2, !canGoBack(), !canGoForward());
            }
        }

        public void checkCachedMetaProperties(String str) {
            if (this.bot) {
                return;
            }
            applyCachedMeta(WebMetadataCache.getInstance().get(AndroidUtilities.getHostAuthority(str, true)));
        }

        public boolean applyCachedMeta(WebMetadataCache.WebMetadata webMetadata) {
            boolean z = false;
            if (webMetadata == null) {
                return false;
            }
            BotWebViewContainer botWebViewContainer = this.botWebViewContainer;
            if (botWebViewContainer != null && botWebViewContainer.delegate != null) {
                if (webMetadata.actionBarColor != 0) {
                    this.botWebViewContainer.delegate.onWebAppBackgroundChanged(true, webMetadata.actionBarColor);
                    this.lastActionBarColorGot = true;
                }
                int i = webMetadata.backgroundColor;
                if (i != 0) {
                    this.botWebViewContainer.delegate.onWebAppBackgroundChanged(false, webMetadata.backgroundColor);
                    this.lastBackgroundColorGot = true;
                } else {
                    i = -1;
                }
                Bitmap bitmap = webMetadata.favicon;
                if (bitmap != null) {
                    BotWebViewContainer botWebViewContainer2 = this.botWebViewContainer;
                    this.lastFavicon = bitmap;
                    botWebViewContainer2.onFaviconChanged(bitmap);
                    this.lastFaviconGot = true;
                }
                if (!TextUtils.isEmpty(webMetadata.sitename)) {
                    String str = webMetadata.sitename;
                    this.lastSiteName = str;
                    BotWebViewContainer botWebViewContainer3 = this.botWebViewContainer;
                    this.lastTitle = str;
                    botWebViewContainer3.onTitleChanged(str);
                    z = true;
                }
                if (SharedConfig.adaptableColorInBrowser) {
                    setBackgroundColor(i);
                }
            }
            if (!z) {
                setTitle(null);
                BotWebViewContainer botWebViewContainer4 = this.botWebViewContainer;
                if (botWebViewContainer4 != null) {
                    botWebViewContainer4.onTitleChanged(null);
                }
            }
            return true;
        }

        @Override // android.webkit.WebView
        public void reload() {
            CookieManager.getInstance().flush();
            d("reload");
            super.reload();
        }

        @Override // android.webkit.WebView
        public void loadData(String str, String str2, String str3) {
            this.openedByUrl = null;
            d("loadData " + str + " " + str2 + " " + str3);
            super.loadData(str, str2, str3);
        }

        @Override // android.webkit.WebView
        public void loadDataWithBaseURL(String str, String str2, String str3, String str4, String str5) {
            this.openedByUrl = null;
            d("loadDataWithBaseURL " + str + " " + str2 + " " + str3 + " " + str4 + " " + str5);
            super.loadDataWithBaseURL(str, str2, str3, str4, str5);
        }

        @Override // android.webkit.WebView
        public void stopLoading() {
            d("stopLoading");
            super.stopLoading();
        }

        @Override // android.view.View
        public void stopNestedScroll() {
            d("stopNestedScroll");
            super.stopNestedScroll();
        }

        @Override // android.webkit.WebView
        public void postUrl(String str, byte[] bArr) {
            d("postUrl " + str + " " + bArr);
            super.postUrl(str, bArr);
        }

        @Override // android.webkit.WebView
        public void onPause() {
            d("onPause");
            super.onPause();
        }

        @Override // android.webkit.WebView
        public void onResume() {
            d("onResume");
            super.onResume();
        }

        @Override // android.webkit.WebView
        public void pauseTimers() {
            d("pauseTimers");
            super.pauseTimers();
        }

        @Override // android.webkit.WebView
        public void resumeTimers() {
            d("resumeTimers");
            super.resumeTimers();
        }

        @Override // android.webkit.WebView
        public boolean canGoBack() {
            return super.canGoBack();
        }

        @Override // android.webkit.WebView
        public void goBack() {
            d("goBack");
            super.goBack();
        }

        @Override // android.webkit.WebView
        public void goForward() {
            d("goForward");
            super.goForward();
        }

        @Override // android.webkit.WebView
        public void clearHistory() {
            d("clearHistory");
            super.clearHistory();
        }

        @Override // android.view.View
        public void setFocusable(int i) {
            d("setFocusable " + i);
            super.setFocusable(i);
        }

        @Override // android.view.View
        public void setFocusable(boolean z) {
            d("setFocusable " + z);
            super.setFocusable(z);
        }

        @Override // android.view.View
        public void setFocusableInTouchMode(boolean z) {
            d("setFocusableInTouchMode " + z);
            super.setFocusableInTouchMode(z);
        }

        @Override // android.view.View
        public void setFocusedByDefault(boolean z) {
            d("setFocusedByDefault " + z);
            super.setFocusedByDefault(z);
        }

        @Override // android.view.ViewGroup
        public boolean drawChild(Canvas canvas, View view, long j) {
            return super.drawChild(canvas, view, j);
        }

        @Override // android.webkit.WebView, android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            super.dispatchDraw(canvas);
        }

        @Override // android.webkit.WebView, android.view.View
        public void onDraw(Canvas canvas) {
            super.onDraw(canvas);
        }

        @Override // android.view.View
        public void draw(Canvas canvas) {
            super.draw(canvas);
        }
    }

    public void d(String str) {
        FileLog.d("[webviewcontainer] #" + this.tag + " " + str);
    }

    public static String tonsite2magic(String str) {
        if (str == null || !isTonsite(Uri.parse(str))) {
            return str;
        }
        String hostAuthority = AndroidUtilities.getHostAuthority(str);
        try {
            hostAuthority = IDN.toASCII(hostAuthority, 1);
        } catch (Exception unused) {
        }
        String strRotateTONHost = rotateTONHost(hostAuthority);
        if (rotatedTONHosts == null) {
            rotatedTONHosts = new HashMap<>();
        }
        rotatedTONHosts.put(strRotateTONHost, hostAuthority);
        return Browser.replaceHostname(Uri.parse(str), strRotateTONHost, "https");
    }

    public static String magic2tonsite(String str) {
        String hostAuthority;
        String str2;
        if (rotatedTONHosts == null || str == null || (hostAuthority = AndroidUtilities.getHostAuthority(str)) == null) {
            return str;
        }
        StringBuilder sb = new StringBuilder(".");
        sb.append(MessagesController.getInstance(UserConfig.selectedAccount).tonProxyAddress);
        return (hostAuthority.endsWith(sb.toString()) && (str2 = rotatedTONHosts.get(hostAuthority)) != null) ? Browser.replace(Uri.parse(str), "tonsite", null, str2, null) : str;
    }

    public static JSONObject obj() {
        try {
            return new JSONObject();
        } catch (Exception unused) {
            return null;
        }
    }

    public static JSONObject obj(String str, Object obj) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(str, obj);
            return jSONObject;
        } catch (Exception unused) {
            return null;
        }
    }

    public static JSONObject obj(String str, Object obj, String str2, Object obj2) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(str, obj);
            jSONObject.put(str2, obj2);
            return jSONObject;
        } catch (Exception unused) {
            return null;
        }
    }

    public static JSONObject obj(String str, Object obj, String str2, Object obj2, String str3, Object obj3) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(str, obj);
            jSONObject.put(str2, obj2);
            jSONObject.put(str3, obj3);
            return jSONObject;
        } catch (Exception unused) {
            return null;
        }
    }

    public static JSONObject obj(String str, Object obj, String str2, Object obj2, String str3, Object obj3, String str4, Object obj4) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put(str, obj);
            jSONObject.put(str2, obj2);
            jSONObject.put(str3, obj3);
            jSONObject.put(str4, obj4);
            return jSONObject;
        } catch (Exception unused) {
            return null;
        }
    }

    public boolean isVerifyingAge() {
        return this.onVerifiedAge != null;
    }

    public void setOnVerifiedAge(Utilities.Callback4<Boolean, Double, String, Double> callback4) {
        this.onVerifiedAge = callback4;
    }
}
