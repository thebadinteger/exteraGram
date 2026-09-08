package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.telephony.PhoneNumberUtils;
import android.telephony.SignalStrength;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.ReplacementSpan;
import android.util.Base64;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Space;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.util.Consumer;
import androidx.dynamicanimation.animation.DynamicAnimation;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.components.QrCodeLoginView;
import com.exteragram.messenger.utils.ui.UIUtil;
import com.google.android.gms.auth.api.R$drawable;
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest;
import com.google.android.gms.auth.api.identity.Identity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.timepicker.TimeModel;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import okhttp3.internal.url._UrlKt;
import org.json.JSONObject;
import org.mvel2.MVEL;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.AuthTokensHelper;
import org.telegram.messenger.BillingController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.CallReceiver;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.GenericProvider;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.PasskeysController;
import org.telegram.messenger.PushListenerController;
import org.telegram.messenger.R;
import org.telegram.messenger.SRPHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.utils.ViewOutlineProviderImpl;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_update;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.CheckBoxCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedPhoneNumberEditText;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.CustomPhoneKeyboardView;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.FragmentFloatingButton;
import org.telegram.ui.Components.ImageUpdater;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkPath;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.LoadingDrawable;
import org.telegram.ui.Components.LoginOrView;
import org.telegram.ui.Components.OutlineTextContainerView;
import org.telegram.ui.Components.Premium.GLIcon.GLIconRenderer;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Premium.StarParticlesView;
import org.telegram.ui.Components.ProxyDrawable;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.SimpleThemeDescription;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.SlideView;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.TextViewSwitcher;
import org.telegram.ui.Components.TransformableLoginButtonView;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.VerticalPositionAutoAnimator;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.Stars.ExplainStarsSheet;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.bots.BotWebViewSheet;

@SuppressLint({"HardwareIds"})
public class LoginActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int SHOW_DELAY;
    private int activityMode;
    private Runnable animationFinishCallback;
    private ImageView backButtonView;
    private View cachedFragmentView;
    private AlertDialog cancelDeleteProgressDialog;
    private TLRPC.TL_auth_sentCode cancelDeletionCode;
    private Bundle cancelDeletionParams;
    private String cancelDeletionPhone;
    private boolean checkPermissions;
    private int currentConnectionState;
    private int currentDoneType;
    private TLRPC.TL_help_termsOfService currentTermsOfService;
    private int currentViewNum;
    private boolean customKeyboardWasVisible;
    private boolean[] doneButtonVisible;
    private AnimatorSet doneItemAnimation;
    private boolean[] doneProgressVisible;
    private Runnable[] editDoneCallback;
    private Runnable emailChangeFinishCallback;
    private boolean emailChangeIsSuggestion;
    private boolean emailChangeNonSkippable;
    private TextView emailChangeSkipButton;
    private Runnable emailChangeSkipCallback;
    private VerticalPositionAutoAnimator floatingAutoAnimator;
    private FragmentFloatingButton floatingButton;
    private TransformableLoginButtonView floatingButtonIcon;
    private boolean forceDisableSafetyNet;
    private View introView;
    private boolean isAnimatingIntro;
    private boolean isRequestingFirebaseSms;
    private ValueAnimator keyboardAnimator;
    private Runnable keyboardHideCallback;
    private LinearLayout keyboardLinearLayout;
    private CustomPhoneKeyboardView keyboardView;
    private ItemOptions loginOptionsMenu;
    private ProxyDrawable loginOptionsProxyDrawable;
    private ActionBarMenuSubItem loginOptionsProxyItem;
    private boolean newAccount;
    private boolean paid;
    private boolean pendingSwitchingAccount;
    private Dialog permissionsDialog;
    private ArrayList<String> permissionsItems;
    private PhoneNumberConfirmView phoneNumberConfirmView;
    private boolean[] postedEditDoneCallback;
    private int progressRequestId;
    private ImageView proxyButtonView;
    private boolean proxyButtonVisible;
    private ProxyDrawable proxyDrawable;
    private RadialProgressView radialProgressView;
    private boolean restoringState;
    private AnimatorSet[] showDoneAnimation;
    private Runnable showProxyButtonDelayed;
    private SizeNotifierFrameLayout sizeNotifierFrameLayout;
    private FrameLayout slideViewsContainer;
    private TextView startMessagingButton;
    private boolean syncContacts;
    private boolean testBackend;
    private final SlideView[] views;

    public static class ProgressView extends View {
    }

    public LoginActivity setIntroView(View view, TextView textView) {
        return this;
    }

    static {
        SHOW_DELAY = SharedConfig.getDevicePerformanceClass() <= 1 ? 150 : 100;
    }

    public LoginActivity() {
        this.views = new SlideView[20];
        this.permissionsItems = new ArrayList<>();
        this.checkPermissions = true;
        this.syncContacts = false;
        this.testBackend = false;
        this.activityMode = 0;
        this.showDoneAnimation = new AnimatorSet[2];
        this.doneButtonVisible = new boolean[]{true, false};
        this.customKeyboardWasVisible = false;
        this.doneProgressVisible = new boolean[2];
        this.editDoneCallback = new Runnable[2];
        this.postedEditDoneCallback = new boolean[2];
    }

    public LoginActivity(int i) {
        this.views = new SlideView[20];
        this.permissionsItems = new ArrayList<>();
        this.checkPermissions = true;
        this.syncContacts = false;
        this.testBackend = false;
        this.activityMode = 0;
        this.showDoneAnimation = new AnimatorSet[2];
        this.doneButtonVisible = new boolean[]{true, false};
        this.customKeyboardWasVisible = false;
        this.doneProgressVisible = new boolean[2];
        this.editDoneCallback = new Runnable[2];
        this.postedEditDoneCallback = new boolean[2];
        this.currentAccount = i;
        this.newAccount = true;
    }

    public LoginActivity changeEmail(Runnable runnable) {
        this.activityMode = 3;
        this.currentViewNum = 12;
        this.emailChangeFinishCallback = runnable;
        return this;
    }

    public LoginActivity changeEmail(Runnable runnable, Runnable runnable2, boolean z) {
        this.activityMode = 3;
        this.currentViewNum = 12;
        this.emailChangeFinishCallback = runnable;
        this.emailChangeSkipCallback = runnable2;
        this.emailChangeNonSkippable = z;
        this.emailChangeIsSuggestion = true;
        return this;
    }

    public LoginActivity cancelAccountDeletion(String str, Bundle bundle, TLRPC.TL_auth_sentCode tL_auth_sentCode) {
        this.cancelDeletionPhone = str;
        this.cancelDeletionParams = bundle;
        this.cancelDeletionCode = tL_auth_sentCode;
        this.activityMode = 1;
        return this;
    }

    public LoginActivity changePhoneNumber() {
        this.activityMode = 2;
        return this;
    }

    public boolean isInCancelAccountDeletionMode() {
        return this.activityMode == 1;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public int getNavigationBarColor() {
        return getThemedColor(Theme.key_windowBackgroundWhite);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void setNavigationBarColor(int i) {
        super.setNavigationBarColor(getNavigationBarColor());
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        int i = 0;
        while (true) {
            SlideView[] slideViewArr = this.views;
            if (i >= slideViewArr.length) {
                break;
            }
            SlideView slideView = slideViewArr[i];
            if (slideView != null) {
                slideView.onDestroyActivity();
            }
            i++;
        }
        AlertDialog alertDialog = this.cancelDeleteProgressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            this.cancelDeleteProgressDialog = null;
        }
        for (Runnable runnable : this.editDoneCallback) {
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
        }
        ItemOptions itemOptions = this.loginOptionsMenu;
        if (itemOptions != null) {
            itemOptions.dismiss();
        }
        clearLoginOptionsMenuReferences();
        getNotificationCenter().removeObserver(this, NotificationCenter.didUpdateConnectionState);
        getNotificationCenter().removeObserver(this, NotificationCenter.newSuggestionsAvailable);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        getNotificationCenter().addObserver(this, NotificationCenter.didUpdateConnectionState);
        getNotificationCenter().addObserver(this, NotificationCenter.newSuggestionsAvailable);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
        return super.onFragmentCreate();
    }

    MainTabsActivity $r8$lambda$fQjuG1p1jelJBTD3_0GqFA5uieM(boolean z, Void r2) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("afterSignup", z);
        MainTabsActivity mainTabsActivity = new MainTabsActivity();
        mainTabsActivity.prepareDialogsActivity(bundle);
        return mainTabsActivity;
    }

    public void onAuthSuccess(TLRPC.TL_auth_authorization tL_auth_authorization) {
        onAuthSuccess(tL_auth_authorization, false);
    }

    public void onAuthSuccess(TLRPC.TL_auth_authorization tL_auth_authorization, boolean z) {
        MessagesController.getInstance(this.currentAccount).cleanup();
        ConnectionsManager.getInstance(this.currentAccount).setUserId(tL_auth_authorization.user.id);
        UserConfig.getInstance(this.currentAccount).clearConfig();
        MessagesController.getInstance(this.currentAccount).cleanup();
        UserConfig.getInstance(this.currentAccount).syncContacts = this.syncContacts;
        UserConfig.getInstance(this.currentAccount).setCurrentUser(tL_auth_authorization.user);
        UserConfig.getInstance(this.currentAccount).saveConfig(true);
        MessagesStorage.getInstance(this.currentAccount).cleanup(true);
        ArrayList arrayList = new ArrayList();
        arrayList.add(tL_auth_authorization.user);
        MessagesStorage.getInstance(this.currentAccount).putUsersAndChats(arrayList, null, true, true);
        MessagesController.getInstance(this.currentAccount).putUser(tL_auth_authorization.user, false);
        ContactsController.getInstance(this.currentAccount).checkAppAccount();
        MessagesController.getInstance(this.currentAccount).checkPromoInfo(true);
        ConnectionsManager.getInstance(this.currentAccount).updateDcSettings();
        MessagesController.getInstance(this.currentAccount).loadAppConfig();
        MessagesController.getInstance(this.currentAccount).lambda$removeWebBrowserException$505();
        MessagesController.getInstance(this.currentAccount).checkPeerColors(false);
        if (tL_auth_authorization.future_auth_token != null) {
            AuthTokensHelper.saveLogInToken(tL_auth_authorization);
        } else {
            FileLog.d("onAuthSuccess future_auth_token is empty");
        }
        if (z) {
            MessagesController.getInstance(this.currentAccount).putDialogsEndReachedAfterRegistration();
        }
        MediaDataController.getInstance(this.currentAccount).loadStickersByEmojiOrName(AndroidUtilities.STICKERS_PLACEHOLDER_PACK_NAME, false, true);
        needFinishActivity(z, tL_auth_authorization.setup_password_required, tL_auth_authorization.otherwise_relogin_days);
    }

    public void fillNextCodeParams(Bundle bundle, TL_account.sentEmailCode sentemailcode) {
        bundle.putString("emailPattern", sentemailcode.email_pattern);
        bundle.putInt("length", sentemailcode.length);
        setPage(13, true, bundle, false);
    }

    public void fillNextCodeParams(Bundle bundle, TLRPC.auth_SentCode auth_sentcode) {
        fillNextCodeParams(bundle, auth_sentcode, true);
    }

    public void open(String str, TLRPC.auth_SentCode auth_sentcode) {
        this.paid = true;
        Bundle bundle = new Bundle();
        bundle.putString("phone", "+" + str);
        bundle.putString("ephone", "+" + str);
        bundle.putString("phoneFormated", str);
        fillNextCodeParams(bundle, auth_sentcode, true);
    }

    private void fillNextCodeParams(Bundle bundle, TLRPC.auth_SentCode auth_sentcode, boolean z) {
        if (auth_sentcode instanceof TLRPC.TL_auth_sentCodePaymentRequired) {
            TLRPC.TL_auth_sentCodePaymentRequired tL_auth_sentCodePaymentRequired = (TLRPC.TL_auth_sentCodePaymentRequired) auth_sentcode;
            bundle.putString("product", tL_auth_sentCodePaymentRequired.store_product);
            bundle.putString("phoneHash", tL_auth_sentCodePaymentRequired.phone_code_hash);
            bundle.putString("support_email_address", tL_auth_sentCodePaymentRequired.support_email_address);
            bundle.putString("support_email_subject", tL_auth_sentCodePaymentRequired.support_email_subject);
            bundle.putString("currency", tL_auth_sentCodePaymentRequired.currency);
            bundle.putInt("premium_days", tL_auth_sentCodePaymentRequired.premium_days);
            bundle.putLong("amount", tL_auth_sentCodePaymentRequired.amount);
            setPage(19, true, bundle, true);
            return;
        }
        bundle.putString("phoneHash", auth_sentcode.phone_code_hash);
        TLRPC.auth_CodeType auth_codetype = auth_sentcode.next_type;
        if (auth_codetype instanceof TLRPC.TL_auth_codeTypeCall) {
            bundle.putInt("nextType", 4);
        } else if (auth_codetype instanceof TLRPC.TL_auth_codeTypeFlashCall) {
            bundle.putInt("nextType", 3);
        } else if (auth_codetype instanceof TLRPC.TL_auth_codeTypeSms) {
            bundle.putInt("nextType", 2);
        } else if (auth_codetype instanceof TLRPC.TL_auth_codeTypeMissedCall) {
            bundle.putInt("nextType", 11);
        } else if (auth_codetype instanceof TLRPC.TL_auth_codeTypeFragmentSms) {
            bundle.putInt("nextType", 15);
        }
        if (auth_sentcode.type instanceof TLRPC.TL_auth_sentCodeTypeApp) {
            bundle.putInt(TeXSymbolParser.TYPE_ATTR, 1);
            bundle.putInt("length", auth_sentcode.type.length);
            setPage(1, z, bundle, false);
            return;
        }
        if (auth_sentcode.timeout == 0) {
            auth_sentcode.timeout = BuildVars.DEBUG_PRIVATE_VERSION ? 5 : 60;
        }
        bundle.putInt("timeout", auth_sentcode.timeout * 1000);
        TLRPC.auth_SentCodeType auth_sentcodetype = auth_sentcode.type;
        if (auth_sentcodetype instanceof TLRPC.TL_auth_sentCodeTypeCall) {
            bundle.putInt(TeXSymbolParser.TYPE_ATTR, 4);
            bundle.putInt("length", auth_sentcode.type.length);
            setPage(4, z, bundle, false);
            return;
        }
        if (auth_sentcodetype instanceof TLRPC.TL_auth_sentCodeTypeFlashCall) {
            bundle.putInt(TeXSymbolParser.TYPE_ATTR, 3);
            bundle.putString("pattern", auth_sentcode.type.pattern);
            setPage(3, z, bundle, false);
            return;
        }
        if ((auth_sentcodetype instanceof TLRPC.TL_auth_sentCodeTypeSms) || (auth_sentcodetype instanceof TLRPC.TL_auth_sentCodeTypeFirebaseSms)) {
            bundle.putInt(TeXSymbolParser.TYPE_ATTR, 2);
            bundle.putInt("length", auth_sentcode.type.length);
            bundle.putBoolean("firebase", auth_sentcode.type instanceof TLRPC.TL_auth_sentCodeTypeFirebaseSms);
            setPage(2, z, bundle, false);
            return;
        }
        if (auth_sentcodetype instanceof TLRPC.TL_auth_sentCodeTypeFragmentSms) {
            bundle.putInt(TeXSymbolParser.TYPE_ATTR, 15);
            bundle.putString("url", auth_sentcode.type.url);
            bundle.putInt("length", auth_sentcode.type.length);
            setPage(15, z, bundle, false);
            return;
        }
        if (auth_sentcodetype instanceof TLRPC.TL_auth_sentCodeTypeMissedCall) {
            bundle.putInt(TeXSymbolParser.TYPE_ATTR, 11);
            bundle.putInt("length", auth_sentcode.type.length);
            bundle.putString("prefix", auth_sentcode.type.prefix);
            setPage(11, z, bundle, false);
            return;
        }
        if (auth_sentcodetype instanceof TLRPC.TL_auth_sentCodeTypeSetUpEmailRequired) {
            bundle.putBoolean("googleSignInAllowed", auth_sentcodetype.google_signin_allowed);
            setPage(12, z, bundle, false);
            return;
        }
        if (auth_sentcodetype instanceof TLRPC.TL_auth_sentCodeTypeEmailCode) {
            bundle.putBoolean("googleSignInAllowed", auth_sentcodetype.google_signin_allowed);
            bundle.putString("emailPattern", auth_sentcode.type.email_pattern);
            bundle.putInt("length", auth_sentcode.type.length);
            bundle.putInt("nextPhoneLoginDate", auth_sentcode.type.next_phone_login_date);
            bundle.putInt("resetAvailablePeriod", auth_sentcode.type.reset_available_period);
            bundle.putInt("resetPendingDate", auth_sentcode.type.reset_pending_date);
            setPage(14, z, bundle, false);
            return;
        }
        if (auth_sentcodetype instanceof TLRPC.TL_auth_sentCodeTypeSmsWord) {
            String str = auth_sentcodetype.beginning;
            if (str != null) {
                bundle.putString("beginning", str);
            }
            setPage(16, z, bundle, false);
            return;
        }
        if (auth_sentcodetype instanceof TLRPC.TL_auth_sentCodeTypeSmsPhrase) {
            String str2 = auth_sentcodetype.beginning;
            if (str2 != null) {
                bundle.putString("beginning", str2);
            }
            setPage(17, z, bundle, false);
        }
    }

    public class PhoneView extends SlideView implements AdapterView.OnItemSelectedListener, NotificationCenter.NotificationCenterDelegate {
        private Runnable cancelRequestingPasskey;
        private ImageView chevronRight;
        private View codeDividerView;
        private AnimatedPhoneNumberEditText codeField;
        private HashMap<String, List<CountrySelectActivity.Country>> codesMap;
        private boolean confirmedNumber;
        private ArrayList<CountrySelectActivity.Country> countriesArray;
        private TextViewSwitcher countryButton;
        private String countryCodeForHint;
        private OutlineTextContainerView countryOutlineView;
        private int countryState;
        private CountrySelectActivity.Country currentCountry;
        private boolean ignoreOnPhoneChange;
        private boolean ignoreOnPhoneChangePaste;
        private boolean ignoreOnTextChange;
        private boolean ignoreSelection;
        private long lastTitleClick;
        private Toast lastTitleToast;
        private boolean nextPressed;
        private boolean numberFilled;
        private AnimatedPhoneNumberEditText phoneField;
        private HashMap<String, List<String>> phoneFormatMap;
        private final ImageView phoneNumberHintButton;
        private OutlineTextContainerView phoneOutlineView;
        private TextView plusTextView;
        private boolean requestedPasskey;
        private boolean requestingPasskey;
        private boolean requestingPhoneNumberHint;
        private LinkSpanDrawable.LinksTextView subtitleView;
        private CheckBoxCell syncContactsBox;
        private CheckBoxCell testBackendCheckBox;
        private int titleClickCount;
        private TextView titleView;
        private int wasCountryHintIndex;

        @Override // org.telegram.ui.Components.SlideView
        public boolean hasCustomKeyboard() {
            return true;
        }

        @Override // android.widget.AdapterView.OnItemSelectedListener
        public void onNothingSelected(AdapterView<?> adapterView) {
        }

        private void showDebugMenu() {
            new AlertDialog.Builder(getContext()).setTitle(LocaleController.getString(R.string.SettingsDebug)).setItems(new String[]{LocaleController.getString(BuildVars.LOGS_ENABLED ? R.string.DebugMenuDisableLogs : R.string.DebugMenuEnableLogs), LocaleController.getString(R.string.DebugSendLogs)}, new DialogInterface.OnClickListener() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda23
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    this.f$0.lambda$showDebugMenu$0(dialogInterface, i);
                }
            }).show();
        }

        public void lambda$requestPasskey$31(Long l, TLRPC.auth_Authorization auth_authorization, String str) {
            this.cancelRequestingPasskey = null;
            this.requestingPasskey = false;
            if (str != null && ("EMPTY".equals(str) || "CANCELLED".equals(str))) {
                if (this.subtitleView == null || !"CANCELLED".equals(str)) {
                    return;
                }
                this.subtitleView.setText(AndroidUtilities.replaceArrows(AndroidUtilities.replaceSingleTag(LocaleController.getString(R.string.StartTextPasskey), new Runnable() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda28
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$requestPasskey$28();
                    }
                }), true));
                return;
            }
            if (l.longValue() != 0 && (LoginActivity.this.getParentActivity() instanceof LaunchActivity)) {
                for (int i = 0; i < 16; i++) {
                    UserConfig userConfig = UserConfig.getInstance(i);
                    if (userConfig.isClientActivated() && userConfig.getClientUserId() == l.longValue() && ConnectionsManager.getInstance(i).isTestBackend() == LoginActivity.this.testBackend) {
                        if (UserConfig.selectedAccount != i) {
                            ((LaunchActivity) LoginActivity.this.getParentActivity()).switchToAccount(i, true);
                        }
                        LoginActivity.this.finishFragment();
                        LoginActivity.this.needHideProgress(false);
                        return;
                    }
                }
            }
            if (str != null && str.contains("SESSION_PASSWORD_NEEDED")) {
                ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(new TL_account.getPassword(), new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$PhoneView$$ExternalSyntheticLambda29
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        this.f$0.lambda$requestPasskey$30(tLObject, tL_error);
                    }
                }, 10);
            } else if (str != null) {
                if (BuildVars.DEBUG_VERSION) {
                    BulletinFactory.of(LoginActivity.this).showForError(str);
                    return;
                }
                return;
            }
            if (auth_authorization instanceof TLRPC.TL_auth_authorization) {
                LoginActivity.this.onAuthSuccess((TLRPC.TL_auth_authorization) auth_authorization);
            }
        }

        public void $r8$lambda$gKyVJVXs5FXqCJwgMxzY5MPqX6U(TLObject tLObject, TLRPC.TL_error tL_error) {
        }

        public static private void updateLoadingLayout() {
            CharSequence text;
            Layout layout = getLayout();
            if (layout == null || (text = layout.getText()) == null) {
                return;
            }
            LinkPath linkPath = new LinkPath(true);
            linkPath.setInset(AndroidUtilities.dp(3.0f), AndroidUtilities.dp(6.0f));
            int length = text.length();
            linkPath.setCurrentLayout(layout, 0, 0.0f);
            layout.getSelectionPath(0, length, linkPath);
            RectF rectF = AndroidUtilities.rectTmp;
            linkPath.getBounds(rectF);
            this.rippleDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
            this.loadingDrawable.usePath(linkPath);
            this.loadingDrawable.setRadiiDp(4.0f);
            int themedColor = LoginActivity.this.getThemedColor(Theme.key_chat_linkSelectBackground);
            this.loadingDrawable.setColors(Theme.multAlpha(themedColor, 0.85f), Theme.multAlpha(themedColor, 2.0f), Theme.multAlpha(themedColor, 3.5f), Theme.multAlpha(themedColor, 6.0f));
            this.loadingDrawable.updateBounds();
        }

        @Override // android.widget.TextView, android.view.View
        public void onDraw(Canvas canvas) {
            canvas.save();
            float paddingTop = ((getGravity() & 16) == 0 || getLayout() == null) ? getPaddingTop() : getPaddingTop() + ((((getHeight() - getPaddingTop()) - getPaddingBottom()) - getLayout().getHeight()) / 2.0f);
            canvas.translate(getPaddingLeft(), paddingTop);
            this.rippleDrawable.draw(canvas);
            canvas.restore();
            super.onDraw(canvas);
            if (isResendingCode() || this.loadingDrawable.isDisappearing()) {
                canvas.save();
                canvas.translate(getPaddingLeft(), paddingTop);
                this.loadingDrawable.draw(canvas);
                canvas.restore();
                invalidate();
            }
        }

        @Override // android.widget.TextView, android.view.View
        public boolean verifyDrawable(Drawable drawable) {
            return drawable == this.rippleDrawable || super.verifyDrawable(drawable);
        }

        @Override // android.widget.TextView, android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (isRippleEnabled() && motionEvent.getAction() == 0) {
                this.rippleDrawable.setHotspot(motionEvent.getX(), motionEvent.getY());
                this.rippleDrawable.setState(new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed});
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 1) {
                this.rippleDrawable.setState(new int[0]);
            }
            return super.onTouchEvent(motionEvent);
        }
    }

    public class LoginActivityPasswordView extends SlideView {
        private TextView cancelButton;
        private EditTextBoldCursor codeField;
        private TextView confirmTextView;
        private Bundle currentParams;
        private TL_account.Password currentPassword;
        private RLottieImageView lockImageView;
        private boolean nextPressed;
        private OutlineTextContainerView outlineCodeField;
        private String passwordString;
        private String phoneCode;
        private String phoneHash;
        private String requestPhone;
        private TextView titleView;

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return true;
        }

        void lambda$new$2(GoogleSignInClient googleSignInClient, Task task) {
            if (LoginActivity.this.getParentActivity() == null || LoginActivity.this.getParentActivity().isFinishing()) {
                return;
            }
            LoginActivity.this.getParentActivity().startActivityForResult(googleSignInClient.getSignInIntent(), 200);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void updateColors() {
            TextView textView = this.titleView;
            int i = Theme.key_windowBackgroundWhiteBlackText;
            textView.setTextColor(Theme.getColor(i));
            this.subtitleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteGrayText6));
            this.subtitleView.setLinkTextColor(Theme.getColor(Theme.key_chat_messageLinkIn));
            this.emailField.setTextColor(Theme.getColor(i));
            this.signInWithGoogleView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText4));
            this.loginOrView.updateColors();
            this.emailOutlineView.invalidate();
        }

        @Override // org.telegram.ui.Components.SlideView
        public boolean needBackButton() {
            return !LoginActivity.this.emailChangeIsSuggestion;
        }

        @Override // org.telegram.ui.Components.SlideView
        public String getHeaderName() {
            return LocaleController.getString("AddEmailTitle", R.string.AddEmailTitle);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void setParams(Bundle bundle, boolean z) {
            if (bundle == null) {
                return;
            }
            this.emailField.setText(_UrlKt.FRAGMENT_ENCODE_SET);
            this.currentParams = bundle;
            this.phone = bundle.getString("phone");
            this.emailPhone = this.currentParams.getString("ephone");
            this.requestPhone = this.currentParams.getString("phoneFormated");
            this.phoneHash = this.currentParams.getString("phoneHash");
            int i = (bundle.getBoolean("googleSignInAllowed") && PushListenerController.GooglePushListenerServiceProvider.INSTANCE.hasServices()) ? 0 : 8;
            this.loginOrView.setVisibility(i);
            this.signInWithGoogleView.setVisibility(i);
            LoginActivity.this.showKeyboard(this.emailField);
            this.emailField.requestFocus();
        }

        private void onPasscodeError(boolean z) {
            if (LoginActivity.this.getParentActivity() == null) {
                return;
            }
            try {
                this.emailOutlineView.performHapticFeedback(3, 2);
            } catch (Exception unused) {
            }
            if (z) {
                this.emailField.setText(_UrlKt.FRAGMENT_ENCODE_SET);
            }
            this.emailField.requestFocus();
            LoginActivity.this.onFieldError(this.emailOutlineView, true);
            postDelayed(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivitySetupEmail$$ExternalSyntheticLambda6
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onPasscodeError$4();
                }
            }, 300L);
        }

        public void lambda$new$2(GoogleSignInClient googleSignInClient, Task task) {
            if (LoginActivity.this.getParentActivity() == null) {
                return;
            }
            LoginActivity.this.getParentActivity().startActivityForResult(googleSignInClient.getSignInIntent(), 200);
        }

        public void lambda$showLoginOptionsMenu$23(ItemOptions itemOptions, View view) {
        itemOptions.dismiss();
        presentFragment(new ProxyListActivity());
    }

    public public LoginActivityPhraseView(Context context, int i) {
            boolean z;
            super(context);
            this.pasteShown = true;
            this.errorShown = false;
            this.pasting = false;
            this.pasted = false;
            this.timerSync = new Object();
            this.time = 60000;
            this.codeTime = 15000;
            this.lastError = _UrlKt.FRAGMENT_ENCODE_SET;
            this.checkPasteRunnable = new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityPhraseView$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$new$7();
                }
            };
            this.dismissField = new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginActivityPhraseView$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$new$8();
                }
            };
            this.shiftDp = -3.0f;
            this.currentType = i;
            boolean z2 = i != 16;
            setOrientation(1);
            RLottieImageView rLottieImageView = new RLottieImageView(context);
            this.imageView = rLottieImageView;
            rLottieImageView.setScaleType(ImageView.ScaleType.CENTER);
            rLottieImageView.setAnimation(R.raw.bubble, 95, 95);
            if (AndroidUtilities.isSmallScreen()) {
                z = true;
            } else {
                Point point = AndroidUtilities.displaySize;
                if (point.x <= point.y || AndroidUtilities.isTablet()) {
                    z = false;
                } else {
                    z = true;
                }
            }
            rLottieImageView.setVisibility(z ? 8 : 0);
            addView(rLottieImageView, LayoutHelper.createLinear(95, 95, 1, 0, 10, 0, 5));
            TextView textView = new TextView(context);
            this.titleTextView = textView;
            textView.setTextSize(1, 18.0f);
            textView.setTypeface(AndroidUtilities.bold());
            textView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            textView.setGravity(49);
            textView.setText(LocaleController.getString(!z2 ? R.string.SMSWordTitle : R.string.SMSPhraseTitle));
            addView(textView, LayoutHelper.createLinear(-2, -2, 1, 8, z ? 25 : 0, 8, 0));
            TextView textView2 = new TextView(context);
            this.confirmTextView = textView2;
            textView2.setTextSize(1, 14.0f);
            textView2.setGravity(1);
            textView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            addView(textView2, LayoutHelper.createLinear(-2, -2, 1, 8, 5, 8, 16));
            OutlineTextContainerView outlineTextContainerView = new OutlineTextContainerView(context);
            this.outlineField = outlineTextContainerView;
            outlineTextContainerView.setText(LocaleController.getString(!z2 ? R.string.SMSWord : R.string.SMSPhrase));
            AnonymousClass1 anonymousClass1 = new AnonymousClass1(context, LoginActivity.this);
            this.codeField = anonymousClass1;
            anonymousClass1.setSingleLine();
            anonymousClass1.setLines(1);
            anonymousClass1.setCursorSize(AndroidUtilities.dp(20.0f));
            anonymousClass1.setCursorWidth(1.5f);
            anonymousClass1.setImeOptions(268435461);
            anonymousClass1.setTextSize(1, 18.0f);
            anonymousClass1.setMaxLines(1);
            anonymousClass1.setBackground(null);
            anonymousClass1.setHint(LocaleController.getString(!z2 ? R.string.SMSWordHint : R.string.SMSPhraseHint));
            anonymousClass1.addTextChangedListener(new TextWatcher() { // from class: org.telegram.ui.LoginActivity.LoginActivityPhraseView.2
                private boolean ignoreTextChange;
                private int trimmedLength;

                @Override // android.text.TextWatcher
                public void onTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                }

                @Override // android.text.TextWatcher
                public void beforeTextChanged(CharSequence charSequence, int i2, int i3, int i4) {
                    if (this.ignoreTextChange || charSequence == null || LoginActivityPhraseView.this.beginning == null) {
                        return;
                    }
                    this.trimmedLength = LoginActivityPhraseView.this.trimLeft(charSequence.toString()).length();
                }

                @Override // android.text.TextWatcher
                public void afterTextChanged(Editable editable) {
                    if (this.ignoreTextChange) {
                        return;
                    }
                    LoginActivityPhraseView.this.checkPaste(true);
                    AndroidUtilities.cancelRunOnUIThread(LoginActivityPhraseView.this.dismissField);
                    LoginActivityPhraseView.this.animateError(false);
                    if (TextUtils.isEmpty(editable)) {
                        LoginActivityPhraseView.this.pasted = false;
                    }
                    if (LoginActivityPhraseView.this.beginsOk(editable.toString())) {
                        return;
                    }
                    LoginActivityPhraseView.this.onInputError(true);
                    this.ignoreTextChange = true;
                    boolean z3 = LoginActivityPhraseView.this.codeField.getSelectionEnd() >= LoginActivityPhraseView.this.codeField.getText().length();
                    if (!LoginActivityPhraseView.this.pasted) {
                        LoginActivityPhraseView.this.codeField.setText(LoginActivityPhraseView.this.beginning.substring(0, Utilities.clamp(this.trimmedLength, LoginActivityPhraseView.this.beginning.length(), 0)));
                        if (z3) {
                            LoginActivityPhraseView.this.codeField.setSelection(LoginActivityPhraseView.this.codeField.getText().length());
                        }
                    }
                    this.ignoreTextChange = false;
                }
            });
            anonymousClass1.setEllipsizeByGradient(true);
            anonymousClass1.setInputType(1);
            anonymousClass1.setTypeface(Typeface.DEFAULT);
            anonymousClass1.setGravity(LocaleController.isRTL ? 5 : 3);
            anonymousClass1.setOnFocusChangeListener(new View.OnFocusChangeListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityPhraseView$$ExternalSyntheticLambda3
                @Override // android.view.View.OnFocusChangeListener
                public final void onFocusChange(View view, boolean z3) {
                    this.f$0.lambda$new$0(view, z3);
                }
            });
            TextView textView3 = new TextView(context);
            this.pasteTextView = textView3;
            textView3.setTextSize(1, 12.0f);
            textView3.setTypeface(AndroidUtilities.bold());
            textView3.setText(LocaleController.getString(R.string.Paste));
            textView3.setPadding(AndroidUtilities.dp(10.0f), 0, AndroidUtilities.dp(10.0f), 0);
            textView3.setGravity(17);
            int color = Theme.getColor(Theme.key_windowBackgroundWhiteBlueText2, ((BaseFragment) LoginActivity.this).resourceProvider);
            textView3.setTextColor(color);
            textView3.setBackground(Theme.createSimpleSelectorRoundRectDrawable(AndroidUtilities.dp(6.0f), Theme.multAlpha(color, 0.12f), Theme.multAlpha(color, 0.15f)));
            ScaleStateListAnimator.apply(textView3, 0.1f, 1.5f);
            anonymousClass1.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(13.34f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(13.34f));
            textView3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityPhraseView$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$new$1(view);
                }
            });
            outlineTextContainerView.addView(anonymousClass1, LayoutHelper.createFrame(-1, -2.0f, 119, 0.0f, 0.0f, 0.0f, 0.0f));
            outlineTextContainerView.attachEditText(anonymousClass1);
            outlineTextContainerView.addView(textView3, LayoutHelper.createFrame(-2, 26.0f, 21, 0.0f, 0.0f, 10.0f, 0.0f));
            LinearLayout linearLayout = new LinearLayout(context);
            this.fieldContainer = linearLayout;
            linearLayout.setOrientation(1);
            linearLayout.addView(outlineTextContainerView, LayoutHelper.createLinear(-1, -2, 1));
            addView(linearLayout, LayoutHelper.createLinear(-1, -2, 1, 16, 3, 16, 0));
            anonymousClass1.setOnEditorActionListener(new TextView.OnEditorActionListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityPhraseView$$ExternalSyntheticLambda5
                @Override // android.widget.TextView.OnEditorActionListener
                public final boolean onEditorAction(TextView textView4, int i2, KeyEvent keyEvent) {
                    return this.f$0.lambda$new$2(textView4, i2, keyEvent);
                }
            });
            FrameLayout frameLayout = new FrameLayout(context);
            this.infoContainer = frameLayout;
            linearLayout.addView(frameLayout, LayoutHelper.createLinear(-1, -2));
            LoadingTextView loadingTextView = LoginActivity.this.new LoadingTextView(context);
            this.prevTypeTextView = loadingTextView;
            int i2 = Theme.key_windowBackgroundWhiteValueText;
            loadingTextView.setLinkTextColor(Theme.getColor(i2));
            loadingTextView.setTextColor(LoginActivity.this.getThemedColor(i2));
            loadingTextView.setTextSize(1, 14.0f);
            loadingTextView.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            loadingTextView.setPadding(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(16.0f));
            loadingTextView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityPhraseView$$ExternalSyntheticLambda6
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$new$3(view);
                }
            });
            addView(loadingTextView, LayoutHelper.createLinear(-2, -2, 1, 0, 18, 0, 0));
            loadingTextView.setVisibility(8);
            TextView textView4 = new TextView(context);
            this.errorTextView = textView4;
            textView4.setPivotX(0.0f);
            textView4.setPivotY(0.0f);
            textView4.setText(LocaleController.getString(!z2 ? R.string.SMSWordError : R.string.SMSPhraseError));
            textView4.setTextColor(LoginActivity.this.getThemedColor(Theme.key_text_RedRegular));
            textView4.setTextSize(1, 13.0f);
            frameLayout.addView(textView4, LayoutHelper.createFrame(-1, -2.0f, 119, 16.0f, 8.0f, 16.0f, 8.0f));
            textView4.setAlpha(0.0f);
            textView4.setScaleX(0.8f);
            textView4.setScaleY(0.8f);
            textView4.setTranslationY(-AndroidUtilities.dp(4.0f));
            TextView textView5 = new TextView(context);
            this.infoTextView = textView5;
            textView5.setPivotX(0.0f);
            textView5.setPivotY(0.0f);
            textView5.setText(LocaleController.getString(!z2 ? R.string.SMSWordPasteHint : R.string.SMSPhrasePasteHint));
            textView5.setTextColor(LoginActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteGrayText));
            textView5.setTextSize(1, 13.0f);
            frameLayout.addView(textView5, LayoutHelper.createFrame(-1, -2.0f, 119, 16.0f, 8.0f, 16.0f, 8.0f));
            LoadingTextView loadingTextView2 = new LoadingTextView(context) { // from class: org.telegram.ui.LoginActivity.LoginActivityPhraseView.3
                {
                    LoginActivity loginActivity = LoginActivity.this;
                }

                @Override // org.telegram.ui.LoginActivity.LoadingTextView
                public boolean isResendingCode() {
                    return LoginActivityPhraseView.this.isResendingCode;
                }

                @Override // org.telegram.ui.LoginActivity.LoadingTextView
                public boolean isRippleEnabled() {
                    if (getVisibility() == 0) {
                        return LoginActivityPhraseView.this.time <= 0 || LoginActivityPhraseView.this.timeTimer == null;
                    }
                    return false;
                }
            };
            this.timeText = loadingTextView2;
            loadingTextView2.setLinkTextColor(Theme.getColor(i2));
            loadingTextView2.setLineSpacing(AndroidUtilities.dp(2.0f), 1.0f);
            loadingTextView2.setPadding(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(14.0f), AndroidUtilities.dp(16.0f));
            loadingTextView2.setTextSize(1, 15.0f);
            loadingTextView2.setGravity(19);
            loadingTextView2.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.LoginActivity$LoginActivityPhraseView$$ExternalSyntheticLambda7
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$new$6(view);
                }
            });
            FrameLayout frameLayout2 = new FrameLayout(context);
            frameLayout2.addView(loadingTextView2, LayoutHelper.createFrame(-1, 56.0f, 80, 6.0f, 0.0f, 60.0f, 28.0f));
            addView(frameLayout2, LayoutHelper.createLinear(-1, -1, 80));
            VerticalPositionAutoAnimator.attach(loadingTextView2);
        }

        public class AnonymousClass1 extends EditTextBoldCursor {
            final void lambda$onTextContextMenuItem$0() {
                LoginActivityPhraseView.this.pasting = false;
            }
        }

        public void lambda$setParams$4(final TLRPC.TL_inputStorePaymentAuthCode tL_inputStorePaymentAuthCode, final TLRPC.PaymentForm paymentForm, TLRPC.TL_payments_paymentResult tL_payments_paymentResult) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginPayView$$ExternalSyntheticLambda20
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setParams$3(tL_inputStorePaymentAuthCode, paymentForm);
                }
            });
        }

        public void lambda$setParams$13(String str) {
            FileLog.d("LoginBilling purchased done " + str);
            if ("CANCELLED".equalsIgnoreCase(str)) {
                this.button.setLoading(false);
            }
        }

        public /* synthetic */ void lambda$setParams$18(ProductDetails productDetails, final Utilities.Callback callback, TLRPC.TL_inputStorePaymentAuthCode tL_inputStorePaymentAuthCode) {
            LoginActivity.this.paid = true;
            BillingController.getInstance().addResultListener(productDetails.getProductId(), new Consumer() { // from class: org.telegram.ui.LoginActivity$LoginPayView$$ExternalSyntheticLambda31
                @Override // androidx.core.util.Consumer
                public final void accept(Object obj) {
                    LoginActivity.LoginPayView.$r8$lambda$pAGp1uDnxfAJNNKcngfU5B1nZnE(callback, (BillingResult) obj);
                }
            });
            BillingController.getInstance().setOnCanceled(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginPayView$$ExternalSyntheticLambda32
                @Override // java.lang.Runnable
                public final void run() {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginPayView$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            callback.run("CANCELLED");
                        }
                    });
                }
            });
            BillingController.getInstance().launchBillingFlow(LoginActivity.this.getParentActivity(), AccountInstance.getInstance(((BaseFragment) LoginActivity.this).currentAccount), tL_inputStorePaymentAuthCode, Collections.singletonList(BillingFlowParams.ProductDetailsParams.newBuilder().setProductDetails(productDetails).build()));
        }

        public static /* synthetic */ void $r8$lambda$pAGp1uDnxfAJNNKcngfU5B1nZnE(final Utilities.Callback callback, BillingResult billingResult) {
            final String responseCodeString = billingResult.getResponseCode() == 0 ? null : BillingController.getResponseCodeString(billingResult.getResponseCode());
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginPayView$$ExternalSyntheticLambda1
                @Override // java.lang.Runnable
                public final void run() {
                    callback.run(responseCodeString);
                }
            });
        }

        public /* synthetic */ void lambda$setParams$24(final String str, final TLRPC.TL_inputStorePaymentAuthCode tL_inputStorePaymentAuthCode, final TLRPC.TL_payments_canPurchaseStore tL_payments_canPurchaseStore, final Runnable runnable, final BillingResult billingResult, final List list) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginPayView$$ExternalSyntheticLambda33
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setParams$23(billingResult, list, str, tL_inputStorePaymentAuthCode, tL_payments_canPurchaseStore, runnable);
                }
            });
        }

        public /* synthetic */ void lambda$setParams$23(BillingResult billingResult, List list, String str, final TLRPC.TL_inputStorePaymentAuthCode tL_inputStorePaymentAuthCode, final TLRPC.TL_payments_canPurchaseStore tL_payments_canPurchaseStore, final Runnable runnable) {
            if (billingResult.getResponseCode() == 0 && list != null && !list.isEmpty()) {
                Iterator it = list.iterator();
                while (it.hasNext()) {
                    final Purchase purchase = (Purchase) it.next();
                    if (purchase.getProducts().contains(str)) {
                        TLRPC.TL_payments_assignPlayMarketTransaction tL_payments_assignPlayMarketTransaction = new TLRPC.TL_payments_assignPlayMarketTransaction();
                        TLRPC.TL_dataJSON tL_dataJSON = new TLRPC.TL_dataJSON();
                        tL_payments_assignPlayMarketTransaction.receipt = tL_dataJSON;
                        tL_dataJSON.data = purchase.getOriginalJson();
                        tL_inputStorePaymentAuthCode.restore = true;
                        tL_payments_assignPlayMarketTransaction.purpose = tL_inputStorePaymentAuthCode;
                        LoginActivity.this.getConnectionsManager().sendRequest(tL_payments_assignPlayMarketTransaction, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginPayView$$ExternalSyntheticLambda0
                            @Override // org.telegram.tgnet.RequestDelegate
                            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                this.f$0.lambda$setParams$22(tL_inputStorePaymentAuthCode, purchase, tL_payments_canPurchaseStore, runnable, tLObject, tL_error);
                            }
                        }, 74);
                        return;
                    }
                }
            }
            runnable.run();
        }

        public /* synthetic */ void lambda$setParams$22(final TLRPC.TL_inputStorePaymentAuthCode tL_inputStorePaymentAuthCode, Purchase purchase, TLRPC.TL_payments_canPurchaseStore tL_payments_canPurchaseStore, final Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
            if (!(tLObject instanceof TLRPC.Updates)) {
                if (tL_error != null) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginPayView$$ExternalSyntheticLambda5
                        @Override // java.lang.Runnable
                        public final void run() {
                            runnable.run();
                        }
                    });
                    return;
                }
                return;
            }
            TLRPC.Updates updates = (TLRPC.Updates) tLObject;
            ArrayList arrayListFindUpdatesAndRemove = MessagesController.findUpdatesAndRemove(updates, TL_update.TL_updateSentPhoneCode.class);
            int size = arrayListFindUpdatesAndRemove.size();
            int i = 0;
            while (i < size) {
                Object obj = arrayListFindUpdatesAndRemove.get(i);
                i++;
                final TL_update.TL_updateSentPhoneCode tL_updateSentPhoneCode = (TL_update.TL_updateSentPhoneCode) obj;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginPayView$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$setParams$19(tL_inputStorePaymentAuthCode, tL_updateSentPhoneCode);
                    }
                });
            }
            LoginActivity.this.getMessagesController().processUpdates(updates, false);
            BillingController.getInstance().consumeGiftPurchase(purchase, tL_payments_canPurchaseStore.purpose, null);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginPayView$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setParams$20();
                }
            });
        }

        public /* synthetic */ void lambda$setParams$19(TLRPC.TL_inputStorePaymentAuthCode tL_inputStorePaymentAuthCode, TL_update.TL_updateSentPhoneCode tL_updateSentPhoneCode) {
            LoginActivity.this.paid = true;
            LoginActivity loginActivity = (LoginActivity) LaunchActivity.findFragment(LoginActivity.class);
            if (loginActivity == null) {
                loginActivity = new LoginActivity(((BaseFragment) LoginActivity.this).currentAccount);
                BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
                if (safeLastFragment != null) {
                    safeLastFragment.presentFragment(loginActivity);
                }
            }
            loginActivity.open(tL_inputStorePaymentAuthCode.phone_number, tL_updateSentPhoneCode.sent_code);
        }

        public /* synthetic */ void lambda$setParams$20() {
            this.button.setLoading(false);
        }

        private void closeAllPaymentFormActivities() {
            INavigationLayout parentLayout = LoginActivity.this.getParentLayout();
            if (parentLayout == null || parentLayout.getFragmentStack() == null) {
                return;
            }
            List<BaseFragment> fragmentStack = parentLayout.getFragmentStack();
            BaseFragment baseFragment = fragmentStack.isEmpty() ? null : fragmentStack.get(fragmentStack.size() - 1);
            ArrayList arrayList = new ArrayList(fragmentStack);
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Object obj = arrayList.get(i);
                i++;
                BaseFragment baseFragment2 = (BaseFragment) obj;
                if ((baseFragment2 instanceof PaymentFormActivity) && baseFragment2 != baseFragment) {
                    baseFragment2.removeSelfFromStack();
                }
            }
            if (baseFragment instanceof PaymentFormActivity) {
                parentLayout.closeLastFragment(true);
            }
        }

        private void startPoll(String str, String str2, long j) {
            if (this.polling) {
                return;
            }
            this.polling = true;
            this.pollingPhoneNumber = str;
            this.pollingPhoneCodeHash = str2;
            this.pollingFormId = j;
            this.button.setLoading(true);
            poll();
        }

        public void poll() {
            if (this.polling) {
                TLRPC.TL_checkPaidAuth tL_checkPaidAuth = new TLRPC.TL_checkPaidAuth();
                tL_checkPaidAuth.form_id = this.pollingFormId;
                tL_checkPaidAuth.phone_number = this.pollingPhoneNumber;
                tL_checkPaidAuth.phone_code_hash = this.pollingPhoneCodeHash;
                this.pollingRequestId = ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).sendRequest(tL_checkPaidAuth, new RequestDelegate() { // from class: org.telegram.ui.LoginActivity$LoginPayView$$ExternalSyntheticLambda24
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        this.f$0.lambda$poll$32(tLObject, tL_error);
                    }
                }, 1096);
            }
        }

        public /* synthetic */ void lambda$poll$32(final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginPayView$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$poll$31(tLObject, tL_error);
                }
            });
        }

        public /* synthetic */ void lambda$poll$31(TLObject tLObject, TLRPC.TL_error tL_error) {
            this.pollingRequestId = -1;
            if (tLObject instanceof TLRPC.auth_SentCode) {
                this.polling = false;
                this.button.setLoading(false);
                closeAllPaymentFormActivities();
                LoginActivity.this.fillNextCodeParams(this.params, (TLRPC.auth_SentCode) tLObject);
                return;
            }
            if (tL_error != null) {
                String str = tL_error.text;
                if (str != null && str.startsWith("FLOOD_WAIT_")) {
                    AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.LoginActivity$LoginPayView$$ExternalSyntheticLambda30
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.poll();
                        }
                    }, Integer.parseInt(tL_error.text.substring(11)) * 1000);
                    return;
                }
                String str2 = tL_error.text;
                if (str2 != null && "PHONE_CODE_EXPIRED".equalsIgnoreCase(str2)) {
                    onBackPressed(true);
                    LoginActivity.this.setPage(0, true, null, true);
                    LoginActivity.this.needShowAlert(LocaleController.getString(R.string.RestorePasswordNoEmailTitle), LocaleController.getString(R.string.CodeExpired));
                } else {
                    this.lastError = tL_error.text;
                    this.polling = false;
                    this.button.setLoading(false);
                    BulletinFactory.of(LoginActivity.this.slideViewsContainer, null).createSimpleBulletin(R.raw.error, LocaleController.formatString(R.string.UnknownErrorCode, tL_error.text));
                }
            }
        }

        private void stopPoll() {
            if (this.pollingRequestId >= 0) {
                ConnectionsManager.getInstance(((BaseFragment) LoginActivity.this).currentAccount).cancelRequest(this.pollingRequestId, true);
                this.pollingRequestId = -1;
            }
            this.polling = false;
            this.button.setLoading(false);
        }

        @Override // org.telegram.ui.Components.SlideView
        public void onHide() {
            super.onHide();
            stopPoll();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void clearViews() {
        View view = this.fragmentView;
        if (view != null) {
            ViewGroup viewGroup = (ViewGroup) view.getParent();
            if (viewGroup != null) {
                try {
                    onRemoveFromParent();
                    BaseFragment.removeViewFromParent(viewGroup, this.fragmentView);
                } catch (Exception e) {
                    FileLog.e(e);
                }
            }
            if (this.pendingSwitchingAccount) {
                this.cachedFragmentView = this.fragmentView;
            }
            this.fragmentView = null;
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null && !this.pendingSwitchingAccount) {
            ViewGroup viewGroup2 = (ViewGroup) actionBar.getParent();
            if (viewGroup2 != null) {
                try {
                    BaseFragment.removeViewFromParent(viewGroup2, this.actionBar);
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            this.actionBar = null;
        }
        clearSheets();
        this.parentLayout = null;
    }
}
