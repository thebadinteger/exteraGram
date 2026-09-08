package org.telegram.ui;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.messenger.AiTonesController$$ExternalSyntheticLambda0;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BirthdayController;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_aicompose;
import org.telegram.tgnet.tl.TL_phone;
import org.telegram.tgnet.tl.TL_update;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.Components.AIEditorAlert;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CreateBotAlert;
import org.telegram.ui.Components.Premium.boosts.UserSelectorBottomSheet;
import org.telegram.ui.Components.SharedMediaLayout;
import org.telegram.ui.Components.voip.VoIPHelper;
import org.telegram.ui.Gifts.GiftSheet;
import org.telegram.ui.Stars.BotStarsActivity;
import org.telegram.ui.Stars.StarsController;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.TON.TONIntroActivity;
import org.telegram.ui.bots.ChannelAffiliateProgramsFragment;
import org.telegram.ui.web.WebBrowserSettings;
import org.webrtc.MediaStreamTrack;

public class LinkManager {
    private final LaunchActivity activity;
    private final int currentAccount;
    private int currentRequestId = -1;
    private boolean done;
    private boolean inited;
    private final boolean isExternalIntent;
    private final Browser.Progress progress;
    private AlertDialog progressDialog;

    public LinkManager(LaunchActivity launchActivity, int i, Browser.Progress progress, boolean z) {
        this.activity = launchActivity;
        this.currentAccount = i;
        this.progress = progress;
        this.isExternalIntent = z;
    }

    public boolean handle(Uri uri) {
        if (uri == null) {
            return false;
        }
        String scheme = uri.getScheme();
        if ("tonsite".equalsIgnoreCase(scheme)) {
            return handleTonsite(uri);
        }
        if ("http".equalsIgnoreCase(scheme) || "https".equalsIgnoreCase(scheme)) {
            return handleHttp(uri);
        }
        if ("tg".equalsIgnoreCase(scheme)) {
            return handleTg(uri);
        }
        return false;
    }

    private boolean handleTonsite(Uri uri) {
        Browser.openUrl(this.activity, uri);
        return true;
    }

    private boolean handleHttp(Uri uri) {
        String host = uri.getHost();
        if (host == null) {
            return false;
        }
        Matcher matcher = LaunchActivity.PREFIX_T_ME_PATTERN.matcher(host.toLowerCase());
        boolean zFind = matcher.find();
        if (!"telegram.me".equalsIgnoreCase(host) && !"t.me".equalsIgnoreCase(host) && !"telegram.dog".equalsIgnoreCase(host) && !zFind) {
            return false;
        }
        if (zFind) {
            StringBuilder sb = new StringBuilder("https://t.me/");
            sb.append(matcher.group(1));
            boolean zIsEmpty = TextUtils.isEmpty(uri.getPath());
            String str = _UrlKt.FRAGMENT_ENCODE_SET;
            sb.append(zIsEmpty ? _UrlKt.FRAGMENT_ENCODE_SET : uri.getPath());
            if (!TextUtils.isEmpty(uri.getQuery())) {
                str = "?" + uri.getQuery();
            }
            sb.append(str);
            uri = Uri.parse(sb.toString());
        }
        String path = uri.getPath();
        if (path != null && path.length() > 1) {
            String strSubstring = path.substring(1);
            List<String> pathSegments = uri.getPathSegments();
            if (pathSegments != null && !pathSegments.isEmpty()) {
                String str2 = pathSegments.get(0);
                String str3 = pathSegments.size() > 1 ? pathSegments.get(1) : null;
                if ("$".equalsIgnoreCase(str2)) {
                    return handleInvoiceSlug(strSubstring.substring(1));
                }
                if ("invoice".equalsIgnoreCase(str2)) {
                    return handleInvoiceSlug(str3);
                }
                if ("addstyle".equalsIgnoreCase(str2)) {
                    return handleAiStyle(str3);
                }
                if ("oauth".equalsIgnoreCase(str2)) {
                    return handleOAuth(uri, uri.getQueryParameter("startapp"));
                }
                if ("newbot".equalsIgnoreCase(str2)) {
                    if (pathSegments.size() < 2) {
                        return true;
                    }
                    return handleNewBot(str3, pathSegments.size() >= 3 ? pathSegments.get(2) : null, uri.getQueryParameter("name"));
                }
            }
        }
        return false;
    }

    private Uri normalizeTgUri(Uri uri) {
        String scheme;
        String schemeSpecificPart;
        if (uri == null || !uri.isOpaque() || (scheme = uri.getScheme()) == null || uri.getAuthority() != null || (schemeSpecificPart = uri.getSchemeSpecificPart()) == null) {
            return uri;
        }
        return Uri.parse(scheme + "://" + schemeSpecificPart);
    }

    void lambda$handleSettings$0(FiltersSetupActivity filtersSetupActivity) {
        filtersSetupActivity.createFolder(getParentLayout());
    }

    public void $r8$lambda$URxiwuOJG0fM2eKyA9oQo6oOclQ(Runnable runnable, String str) {
        if (runnable == null || !"paid".equals(str)) {
            return;
        }
        runnable.run();
    }

    public static void lambda$handleOAuth$18(TLRPC.TL_messages_requestUrlAuth tL_messages_requestUrlAuth, TLRPC.UrlAuthResult urlAuthResult, TLRPC.TL_error tL_error) {
        lambda$handleInvoiceSlug$13();
        if (tL_error != null) {
            if ("URL_EXPIRED".equalsIgnoreCase(tL_error.text)) {
                getBulletinFactory().createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.BotAuthLoggedInFailTitle), LocaleController.getString(R.string.BotAuthLoggedInFailNoDomain)).show();
                return;
            } else {
                getBulletinFactory().showForError(tL_error);
                return;
            }
        }
        OAuthSheet.handle(this.isExternalIntent, this.currentAccount, tL_messages_requestUrlAuth, urlAuthResult);
    }

    private boolean handleNewBot(String str, String str2, String str3) {
        final TLRPC.TL_requestPeerTypeCreateBot tL_requestPeerTypeCreateBot = new TLRPC.TL_requestPeerTypeCreateBot();
        tL_requestPeerTypeCreateBot.bot_managed = true;
        if (!TextUtils.isEmpty(str3)) {
            tL_requestPeerTypeCreateBot.flags |= 2;
            tL_requestPeerTypeCreateBot.suggested_name = str3;
        }
        if (!TextUtils.isEmpty(str2)) {
            tL_requestPeerTypeCreateBot.flags |= 4;
            tL_requestPeerTypeCreateBot.suggested_username = str2;
        }
        final BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null && safeLastFragment.getContext() != null) {
            init();
            final TLRPC.User[] userArr = {null};
            final Runnable runnable = new Runnable() { // from class: org.telegram.ui.LinkManager$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$handleNewBot$20(safeLastFragment, userArr, tL_requestPeerTypeCreateBot);
                }
            };
            MessagesController.getInstance(this.currentAccount).getUserNameResolver().resolve(str, new Consumer() { // from class: org.telegram.ui.LinkManager$$ExternalSyntheticLambda12
                @Override // com.google.android.exoplayer2.util.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$handleNewBot$21(userArr, runnable, (Long) obj);
                }
            });
        }
        return true;
    }

    public void lambda$handleNewBot$19(TLRPC.User[] userArr, TLRPC.User user) {
        lambda$handleInvoiceSlug$13();
        if (user == null) {
            return;
        }
        long j = userArr[0].id;
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", user.id);
        presentFragment(new AnonymousClass3(bundle, user, userArr, j));
    }

    public class AnonymousClass3 extends ChatActivity {
        private boolean shownToast;
        final void lambda$handleAiStyle$22(TL_aicompose.Tones tones, TLRPC.TL_error tL_error) {
        lambda$handleInvoiceSlug$13();
        if (!(tones instanceof TL_aicompose.TL_tones)) {
            if (tL_error != null) {
                if ("AICOMPOSE_TONE_SLUG_INVALID".equalsIgnoreCase(tL_error.text)) {
                    getBulletinFactory().createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.AIEditorStyleNotFound)).show();
                    return;
                } else {
                    getBulletinFactory().showForError(tL_error);
                    return;
                }
            }
            return;
        }
        TL_aicompose.TL_tones tL_tones = (TL_aicompose.TL_tones) tones;
        MessagesController.getInstance(this.currentAccount).putUsers(tL_tones.users, false);
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null || tL_tones.tones.isEmpty()) {
            return;
        }
        new AIEditorAlert.AiStyleAlert(safeLastFragment.getContext(), tL_tones.tones.get(0), safeLastFragment.getResourceProvider()).show();
    }

    private void setRequestId(int i) {
        this.currentRequestId = i;
    }

    private void presentFragment(BaseFragment baseFragment) {
        presentFragment(baseFragment, false);
    }

    private void presentFragment(BaseFragment baseFragment, boolean z) {
        this.activity.presentFragment(baseFragment, z, false);
        if (AndroidUtilities.isTablet()) {
            this.activity.actionBarLayout.rebuildFragments(1);
            this.activity.rightActionBarLayout.rebuildFragments(1);
        }
    }

    private INavigationLayout getParentLayout() {
        return this.activity.getActionBarLayout();
    }

    private void scrollTo(String str) {
        AndroidUtilities.scrollToFragmentRow(getParentLayout(), str);
    }

    private BaseFragment getLastFragment() {
        return LaunchActivity.getSafeLastFragment();
    }

    public BulletinFactory getBulletinFactory() {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            return BulletinFactory.global();
        }
        return BulletinFactory.of(safeLastFragment);
    }

    public UserConfig getUserConfig() {
        return UserConfig.getInstance(this.currentAccount);
    }

    public ConnectionsManager getConnectionsManager() {
        return ConnectionsManager.getInstance(this.currentAccount);
    }

    private void init() {
        if (this.inited || this.done) {
            return;
        }
        Browser.Progress progress = this.progress;
        if (progress == null) {
            if (this.progressDialog == null) {
                this.progressDialog = new AlertDialog(this.activity, 3);
            }
            this.progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.LinkManager$$ExternalSyntheticLambda16
                @Override // android.content.DialogInterface.OnCancelListener
                public final void onCancel(DialogInterface dialogInterface) {
                    this.f$0.lambda$init$23(dialogInterface);
                }
            });
            this.progressDialog.showDelayed(300L);
        } else {
            progress.onCancel(new Runnable() { // from class: org.telegram.ui.LinkManager$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.cancel();
                }
            });
            this.progress.init();
        }
        this.inited = true;
    }

    public /* synthetic */ void lambda$init$23(DialogInterface dialogInterface) {
        cancel();
    }

    public void cancel() {
        if (this.currentRequestId >= 0) {
            getConnectionsManager().cancelRequest(this.currentRequestId, true);
            this.currentRequestId = -1;
        }
    }

    public void lambda$handleInvoiceSlug$13() {
        if (this.done) {
            return;
        }
        AlertDialog alertDialog = this.progressDialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        Browser.Progress progress = this.progress;
        if (progress != null) {
            progress.end();
        }
        this.done = true;
    }

    private static boolean isEmpty(String str) {
        return TextUtils.isEmpty(str);
    }

    /* JADX WARN: Code duplicated, block: B:100:0x0153 A[ADDED_TO_REGION, RETURN] */
    /* JADX WARN: Code duplicated, block: B:101:0x0154 A[Catch: Exception -> 0x0034, TryCatch #0 {Exception -> 0x0034, blocks: (B:5:0x0004, B:8:0x000f, B:11:0x0016, B:18:0x002b, B:25:0x003f, B:28:0x0046, B:30:0x0060, B:32:0x0068, B:35:0x0072, B:38:0x0083, B:40:0x008f, B:41:0x0092, B:43:0x0098, B:45:0x009f, B:48:0x00aa, B:50:0x00b0, B:53:0x00bd, B:54:0x00c1, B:98:0x0147, B:101:0x0154, B:56:0x00c6, B:59:0x00d0, B:62:0x00da, B:65:0x00e4, B:68:0x00ed, B:71:0x00f6, B:74:0x00ff, B:77:0x0108, B:80:0x0111, B:83:0x011a, B:86:0x0123, B:89:0x012c, B:92:0x0135, B:95:0x013e, B:105:0x015e, B:107:0x0164, B:23:0x0037, B:109:0x0170, B:111:0x0178, B:113:0x0180, B:115:0x0188), top: B:120:0x0004 }] */
    /* JADX WARN: Code duplicated, block: B:27:0x0045 A[RETURN] */
    /* JADX WARN: Code duplicated, block: B:28:0x0046 A[Catch: Exception -> 0x0034, TryCatch #0 {Exception -> 0x0034, blocks: (B:5:0x0004, B:8:0x000f, B:11:0x0016, B:18:0x002b, B:25:0x003f, B:28:0x0046, B:30:0x0060, B:32:0x0068, B:35:0x0072, B:38:0x0083, B:40:0x008f, B:41:0x0092, B:43:0x0098, B:45:0x009f, B:48:0x00aa, B:50:0x00b0, B:53:0x00bd, B:54:0x00c1, B:98:0x0147, B:101:0x0154, B:56:0x00c6, B:59:0x00d0, B:62:0x00da, B:65:0x00e4, B:68:0x00ed, B:71:0x00f6, B:74:0x00ff, B:77:0x0108, B:80:0x0111, B:83:0x011a, B:86:0x0123, B:89:0x012c, B:92:0x0135, B:95:0x013e, B:105:0x015e, B:107:0x0164, B:23:0x0037, B:109:0x0170, B:111:0x0178, B:113:0x0180, B:115:0x0188), top: B:120:0x0004 }] */
    /* JADX WARN: Code duplicated, block: B:35:0x0072 A[Catch: Exception -> 0x0034, TRY_LEAVE, TryCatch #0 {Exception -> 0x0034, blocks: (B:5:0x0004, B:8:0x000f, B:11:0x0016, B:18:0x002b, B:25:0x003f, B:28:0x0046, B:30:0x0060, B:32:0x0068, B:35:0x0072, B:38:0x0083, B:40:0x008f, B:41:0x0092, B:43:0x0098, B:45:0x009f, B:48:0x00aa, B:50:0x00b0, B:53:0x00bd, B:54:0x00c1, B:98:0x0147, B:101:0x0154, B:56:0x00c6, B:59:0x00d0, B:62:0x00da, B:65:0x00e4, B:68:0x00ed, B:71:0x00f6, B:74:0x00ff, B:77:0x0108, B:80:0x0111, B:83:0x011a, B:86:0x0123, B:89:0x012c, B:92:0x0135, B:95:0x013e, B:105:0x015e, B:107:0x0164, B:23:0x0037, B:109:0x0170, B:111:0x0178, B:113:0x0180, B:115:0x0188), top: B:120:0x0004 }] */
    /* JADX WARN: Code duplicated, block: B:38:0x0083 A[Catch: Exception -> 0x0034, TRY_ENTER, TryCatch #0 {Exception -> 0x0034, blocks: (B:5:0x0004, B:8:0x000f, B:11:0x0016, B:18:0x002b, B:25:0x003f, B:28:0x0046, B:30:0x0060, B:32:0x0068, B:35:0x0072, B:38:0x0083, B:40:0x008f, B:41:0x0092, B:43:0x0098, B:45:0x009f, B:48:0x00aa, B:50:0x00b0, B:53:0x00bd, B:54:0x00c1, B:98:0x0147, B:101:0x0154, B:56:0x00c6, B:59:0x00d0, B:62:0x00da, B:65:0x00e4, B:68:0x00ed, B:71:0x00f6, B:74:0x00ff, B:77:0x0108, B:80:0x0111, B:83:0x011a, B:86:0x0123, B:89:0x012c, B:92:0x0135, B:95:0x013e, B:105:0x015e, B:107:0x0164, B:23:0x0037, B:109:0x0170, B:111:0x0178, B:113:0x0180, B:115:0x0188), top: B:120:0x0004 }] */
    /* JADX WARN: Code duplicated, block: B:43:0x0098 A[Catch: Exception -> 0x0034, TryCatch #0 {Exception -> 0x0034, blocks: (B:5:0x0004, B:8:0x000f, B:11:0x0016, B:18:0x002b, B:25:0x003f, B:28:0x0046, B:30:0x0060, B:32:0x0068, B:35:0x0072, B:38:0x0083, B:40:0x008f, B:41:0x0092, B:43:0x0098, B:45:0x009f, B:48:0x00aa, B:50:0x00b0, B:53:0x00bd, B:54:0x00c1, B:98:0x0147, B:101:0x0154, B:56:0x00c6, B:59:0x00d0, B:62:0x00da, B:65:0x00e4, B:68:0x00ed, B:71:0x00f6, B:74:0x00ff, B:77:0x0108, B:80:0x0111, B:83:0x011a, B:86:0x0123, B:89:0x012c, B:92:0x0135, B:95:0x013e, B:105:0x015e, B:107:0x0164, B:23:0x0037, B:109:0x0170, B:111:0x0178, B:113:0x0180, B:115:0x0188), top: B:120:0x0004 }] */
    /* JADX WARN: Code restructure failed: missing block: B:96:0x0144, code lost:
    
        if (r1.equals("joinchat") != false) goto L97;
     */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean isWebAppLink(java.lang.String r7) {
        /*
            Method dump skipped, instruction units count: 466
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.LinkManager.isWebAppLink(java.lang.String):boolean");
    }
}
