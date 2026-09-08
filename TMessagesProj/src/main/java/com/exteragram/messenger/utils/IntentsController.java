package com.exteragram.messenger.utils;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.arch.core.util.Function;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.backup.BackupBottomSheet;
import com.exteragram.messenger.backup.PreferencesUtils;
import com.exteragram.messenger.components.SupporterBottomSheet;
import com.exteragram.messenger.feed.ui.FeedActivity;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.preferences.MainPreferencesActivity;
import com.exteragram.messenger.preferences.OtherPreferencesActivity;
import com.exteragram.messenger.preferences.utils.SettingsRegistry;
import com.exteragram.messenger.utils.chats.ChatUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.MapsKt;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;

@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\u0010\u0002\n\u0002\b\u0005\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0015\u0010\u0007\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0004¢\u0006\u0004\b\u0007\u0010\bJ\u0015\u0010\f\u001a\u00020\u000b2\u0006\u0010\n\u001a\u00020\t¢\u0006\u0004\b\f\u0010\rR,\u0010\u0012\u001a\u001a\u0012\u0004\u0012\u00020\u000f\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00110\u00100\u000e8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0012\u0010\u0013R,\u0010\u0014\u001a\u001a\u0012\u0004\u0012\u00020\u000f\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00110\u00100\u000e8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0014\u0010\u0013R,\u0010\u0015\u001a\u001a\u0012\u0004\u0012\u00020\u000f\u0012\u0010\u0012\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u000b0\u00100\u000e8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0015\u0010\u0013¨\u0006\u0016"}, d2 = {"Lcom/exteragram/messenger/utils/IntentsController;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "Landroid/net/Uri;", "uri", "Ljava/io/File;", "getTempFileFromIntent", "(Landroid/net/Uri;)Ljava/io/File;", "Landroid/content/Intent;", "intent", _UrlKt.FRAGMENT_ENCODE_SET, "handleIntent", "(Landroid/content/Intent;)Z", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlin/Function1;", _UrlKt.FRAGMENT_ENCODE_SET, "deeplinkCallbacks", "Ljava/util/Map;", "callbacks", "actionCallbacks", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nIntentsController.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IntentsController.kt\ncom/exteragram/messenger/utils/IntentsController\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,266:1\n1#2:267\n*E\n"})
public final class IntentsController {
    public static final IntentsController INSTANCE = new IntentsController();
    private static final Map<String, Function1<Uri, Unit>> deeplinkCallbacks = MapsKt.mapOf(TuplesKt.to("extera", new Function1() { 
        @Override // kotlin.jvm.functions.Function1
        public final Object invoke(Object obj) {
            return IntentsController.m1483$r8$lambda$WdTKTpzhigC61xIeBWpH_WTkyc((Uri) obj);
        }
    }), TuplesKt.to("export", new Function1() { 
        @Override // kotlin.jvm.functions.Function1
        public final Object invoke(Object obj) {
            return IntentsController.m1479$r8$lambda$9XjeTv5CAxJc0C67EC8OKii4c0((Uri) obj);
        }
    }), TuplesKt.to("feed", new Function1() { 
        @Override // kotlin.jvm.functions.Function1
        public final Object invoke(Object obj) {
            return IntentsController.$r8$lambda$L2RxthlVNJpl5iMxDDpn7NHtzy0((Uri) obj);
        }
    }), TuplesKt.to("support", new Function1() { 
        @Override // kotlin.jvm.functions.Function1
        public final Object invoke(Object obj) {
            return IntentsController.$r8$lambda$It8J4Jzm7OhL1Vqi8KopB5SXXqg((Uri) obj);
        }
    }), TuplesKt.to("donate", new Function1() { 
        @Override // kotlin.jvm.functions.Function1
        public final Object invoke(Object obj) {
            return IntentsController.$r8$lambda$b8B8zH141hhzJ54sxiCZj1b2KUg((Uri) obj);
        }
    }), TuplesKt.to("emoji", new Function1() { 
        @Override // kotlin.jvm.functions.Function1
        public final Object invoke(Object obj) {
            return IntentsController.$r8$lambda$4nHeLUQ09Gyn3NZM8J_qAzCpDyA((Uri) obj);
        }
    }), TuplesKt.to("user", new Function1() { 
        @Override // kotlin.jvm.functions.Function1
        public final Object invoke(Object obj) {
            return IntentsController.m1486$r8$lambda$u3rHULPL9MN5BHaU8W8fgkJiyE((Uri) obj);
        }
    }), TuplesKt.to("chat", new Function1() { 
        @Override // kotlin.jvm.functions.Function1
        public final Object invoke(Object obj) {
            return IntentsController.$r8$lambda$IW9cVW1_Ls85TWGd_6MsusR_AB8((Uri) obj);
        }
    }));
    private static final Map<String, Function1<Uri, Unit>> callbacks = MapsKt.mapOf(TuplesKt.to("exteraSettings", new Function1() { 
        @Override // kotlin.jvm.functions.Function1
        public final Object invoke(Object obj) {
            return IntentsController.$r8$lambda$sZPDRQSzV6QOqGXDv1JwbZbiGWg((Uri) obj);
        }
    }));
    private static final Map<String, Function1<Intent, Boolean>> actionCallbacks = MapsKt.mapOf(TuplesKt.to("com.exteragram.plugins.safemode", new Function1() { 
        @Override // kotlin.jvm.functions.Function1
        public final Object invoke(Object obj) {
            return Boolean.valueOf(IntentsController.$r8$lambda$t_HCOUeylOGoGDt_jk1guvYhKgg((Intent) obj));
        }
    }), TuplesKt.to("android.intent.action.VIEW", new Function1() { 
        @Override // kotlin.jvm.functions.Function1
        public final Object invoke(Object obj) {
            return Boolean.valueOf(IntentsController.$r8$lambda$bACeRfEFydNv6BTd7X5lA6iME_8((Intent) obj));
        }
    }));

    private IntentsController() {
    }

    public static Unit m1483$r8$lambda$WdTKTpzhigC61xIeBWpH_WTkyc(Uri uri) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                IntentsController.deeplinkCallbacks$lambda$0$0();
            }
        });
        return Unit.INSTANCE;
    }

    public static final void deeplinkCallbacks$lambda$0$0() {
        LaunchActivity.instance.lambda$runLinkRequest$101(new MainPreferencesActivity());
    }

    public static Unit m1479$r8$lambda$9XjeTv5CAxJc0C67EC8OKii4c0(Uri uri) {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            PreferencesUtils.getInstance().exportSettings(safeLastFragment);
        }
        return Unit.INSTANCE;
    }

    public static Unit $r8$lambda$L2RxthlVNJpl5iMxDDpn7NHtzy0(Uri uri) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                IntentsController.deeplinkCallbacks$lambda$2$0();
            }
        });
        return Unit.INSTANCE;
    }

    public static final void deeplinkCallbacks$lambda$2$0() {
        FeedActivity.presentFeed(LaunchActivity.getSafeLastFragment());
    }

    public static Unit $r8$lambda$It8J4Jzm7OhL1Vqi8KopB5SXXqg(Uri uri) {
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment != null) {
            SupporterBottomSheet.showAlert(safeLastFragment);
        }
        return Unit.INSTANCE;
    }

    public static Unit $r8$lambda$b8B8zH141hhzJ54sxiCZj1b2KUg(Uri uri) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                IntentsController.deeplinkCallbacks$lambda$4$0();
            }
        });
        return Unit.INSTANCE;
    }

    public static final void deeplinkCallbacks$lambda$4$0() {
        LaunchActivity.instance.lambda$runLinkRequest$101(new OtherPreferencesActivity());
    }

    public static Unit $r8$lambda$4nHeLUQ09Gyn3NZM8J_qAzCpDyA(Uri uri) {
        Long longOrNull;
        TLRPC.Document documentFindDocument;
        TLRPC.InputStickerSet inputStickerSet;
        String queryParameter = uri.getQueryParameter("id");
        if (queryParameter != null && (longOrNull = StringsKt.toLongOrNull(queryParameter)) != null) {
            long jLongValue = longOrNull.longValue();
            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
            if (safeLastFragment != null && (documentFindDocument = AnimatedEmojiDrawable.findDocument(UserConfig.selectedAccount, jLongValue)) != null && (inputStickerSet = MessageObject.getInputStickerSet(documentFindDocument)) != null) {
                EmojiPacksAlert emojiPacksAlert = new EmojiPacksAlert(safeLastFragment, safeLastFragment.getParentActivity(), safeLastFragment.getResourceProvider(), new ArrayList(Collections.singletonList(inputStickerSet)));
                emojiPacksAlert.setPreviewEmoji(documentFindDocument);
                safeLastFragment.showDialog(emojiPacksAlert);
            }
        }
        return Unit.INSTANCE;
    }

    public static Unit m1486$r8$lambda$u3rHULPL9MN5BHaU8W8fgkJiyE(Uri uri) {
        Long longOrNull;
        String queryParameter = uri.getQueryParameter("id");
        if (queryParameter != null && (longOrNull = StringsKt.toLongOrNull(queryParameter)) != null) {
            long jLongValue = longOrNull.longValue();
            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
            if (safeLastFragment != null) {
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", jLongValue);
                final ProfileActivity profileActivity = new ProfileActivity(bundle);
                final AlertDialog alertDialog = new AlertDialog(safeLastFragment.getParentActivity(), 3);
                alertDialog.setCanCancel(false);
                alertDialog.show();
                ChatUtils.getInstance().searchUserById(Long.valueOf(jLongValue), new Utilities.Callback() { 
                    @Override 
                    public final void run(Object obj) {
                        IntentsController.deeplinkCallbacks$lambda$6$0$0$2(alertDialog, profileActivity, (TLRPC.User) obj);
                    }
                });
            }
        }
        return Unit.INSTANCE;
    }

    public static final void deeplinkCallbacks$lambda$6$0$0$2(AlertDialog alertDialog, final ProfileActivity profileActivity, TLRPC.User user) {
        alertDialog.dismiss();
        if (user != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    IntentsController.deeplinkCallbacks$lambda$6$0$0$2$0(profileActivity);
                }
            });
            if (AndroidUtilities.isTablet()) {
                LaunchActivity.instance.actionBarLayout.showLastFragment();
                LaunchActivity.instance.rightActionBarLayout.showLastFragment();
                return;
            }
            return;
        }
        LaunchActivity.instance.showBulletin(new Function() { 
            @Override // androidx.arch.core.util.Function
            public final Object apply(Object obj) {
                return IntentsController.deeplinkCallbacks$lambda$6$0$0$2$1((BulletinFactory) obj);
            }
        });
    }

    public static final void deeplinkCallbacks$lambda$6$0$0$2$0(ProfileActivity profileActivity) {
        LaunchActivity.instance.presentFragment(profileActivity, false, false);
    }

    public static final Bulletin deeplinkCallbacks$lambda$6$0$0$2$1(BulletinFactory bulletinFactory) {
        return bulletinFactory.createErrorBulletin(LocaleController.getString(R.string.UserNotFound));
    }

    public static Unit $r8$lambda$IW9cVW1_Ls85TWGd_6MsusR_AB8(Uri uri) {
        Long longOrNull;
        String queryParameter = uri.getQueryParameter("id");
        if (queryParameter != null && (longOrNull = StringsKt.toLongOrNull(queryParameter)) != null) {
            long jLongValue = longOrNull.longValue();
            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
            if (safeLastFragment != null) {
                Bundle bundle = new Bundle();
                bundle.putLong("chat_id", jLongValue);
                final ChatActivity chatActivity = new ChatActivity(bundle);
                final AlertDialog alertDialog = new AlertDialog(safeLastFragment.getParentActivity(), 3);
                alertDialog.setCanCancel(false);
                alertDialog.show();
                ChatUtils.getInstance().searchChatById(jLongValue, new Utilities.Callback() { 
                    @Override 
                    public final void run(Object obj) {
                        IntentsController.deeplinkCallbacks$lambda$7$0$0$2(alertDialog, chatActivity, (TLRPC.Chat) obj);
                    }
                });
            }
        }
        return Unit.INSTANCE;
    }

    public static final void deeplinkCallbacks$lambda$7$0$0$2(AlertDialog alertDialog, final ChatActivity chatActivity, TLRPC.Chat chat) {
        alertDialog.dismiss();
        if (chat != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    IntentsController.deeplinkCallbacks$lambda$7$0$0$2$0(chatActivity);
                }
            });
            if (AndroidUtilities.isTablet()) {
                LaunchActivity.instance.actionBarLayout.showLastFragment();
                LaunchActivity.instance.rightActionBarLayout.showLastFragment();
                return;
            }
            return;
        }
        LaunchActivity.instance.showBulletin(new Function() { 
            @Override // androidx.arch.core.util.Function
            public final Object apply(Object obj) {
                return IntentsController.deeplinkCallbacks$lambda$7$0$0$2$1((BulletinFactory) obj);
            }
        });
    }

    public static final void deeplinkCallbacks$lambda$7$0$0$2$0(ChatActivity chatActivity) {
        LaunchActivity.instance.presentFragment(chatActivity, false, false);
    }

    public static final Bulletin deeplinkCallbacks$lambda$7$0$0$2$1(BulletinFactory bulletinFactory) {
        return bulletinFactory.createErrorBulletin(LocaleController.getString(R.string.ChatNotFound));
    }

    public static Unit $r8$lambda$sZPDRQSzV6QOqGXDv1JwbZbiGWg(Uri uri) {
        SettingsRegistry.getInstance().handleLink(uri.getQueryParameter("s"), uri.getQueryParameter("p"));
        return Unit.INSTANCE;
    }

    public static boolean $r8$lambda$t_HCOUeylOGoGDt_jk1guvYhKgg(Intent intent) {
        ExteraConfig.setPluginsSafeMode(true);
        if (ExteraConfig.getPluginsEngine()) {
            PluginsController.Companion companion = PluginsController.INSTANCE;
            if (companion.getInstance().getInitialized()) {
                companion.getInstance().restart(true);
            }
        }
        return true;
    }

    public static boolean $r8$lambda$bACeRfEFydNv6BTd7X5lA6iME_8(Intent intent) {
        String strSubstringAfterLast;
        BaseFragment safeLastFragment;
        Uri data = intent.getData();
        boolean z = false;
        if (data == null) {
            return false;
        }
        String path = data.getPath();
        Boolean boolValueOf = null;
        if (path != null && (strSubstringAfterLast = StringsKt.substringAfterLast(path, '.', _UrlKt.FRAGMENT_ENCODE_SET)) != null && (safeLastFragment = LaunchActivity.getSafeLastFragment()) != null) {
            int iHashCode = strSubstringAfterLast.hashCode();
            if (iHashCode != -1289044077) {
                if (iHashCode != -985174221) {
                    if (iHashCode == 100029210 && strSubstringAfterLast.equals("icons")) {
                        IconManager.INSTANCE.handleIconPack(safeLastFragment, INSTANCE.getTempFileFromIntent(data).getAbsolutePath());
                        z = true;
                    }
                } else if (strSubstringAfterLast.equals("plugin")) {
                    PluginsController.INSTANCE.getInstance().showInstallDialog(safeLastFragment, INSTANCE.getTempFileFromIntent(data).getAbsolutePath(), false);
                    z = true;
                }
            } else if (strSubstringAfterLast.equals("extera")) {
                new BackupBottomSheet(safeLastFragment, INSTANCE.getTempFileFromIntent(data)).showIfPossible();
                z = true;
            }
            boolValueOf = Boolean.valueOf(z);
        }
        return Intrinsics.areEqual(boolValueOf, Boolean.TRUE);
    }

    public final File getTempFileFromIntent(Uri uri) throws FileNotFoundException {
        File file = new File(ApplicationLoader.getFilesDirFixed(), "temp");
        if (!file.exists()) {
            file.mkdirs();
        }
        File file2 = new File(file, "temp_file_" + System.currentTimeMillis() + ".plugin");
        InputStream inputStreamOpenInputStream = ApplicationLoader.applicationContext.getContentResolver().openInputStream(uri);
        if (inputStreamOpenInputStream != null) {
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(file2);
                try {
                    ByteStreamsKt.copyTo$default(inputStreamOpenInputStream, fileOutputStream, 0, 2, null);
                    CloseableKt.closeFinally(fileOutputStream, null);
                    CloseableKt.closeFinally(inputStreamOpenInputStream, null);
                } catch (Throwable th) {
                    try {
                        throw th;
                    } catch (Throwable th2) {
                        CloseableKt.closeFinally(fileOutputStream, th);
                        throw th2;
                    }
                }
            } catch (Throwable th3) {
                try {
                    throw th3;
                } catch (Throwable th4) {
                    CloseableKt.closeFinally(inputStreamOpenInputStream, th3);
                    throw th4;
                }
            }
        }
        file2.deleteOnExit();
        return file2;
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /* JADX WARN: Code duplicated, block: B:34:0x007c  */
    /* JADX WARN: Code duplicated, block: B:37:0x0085  */
    /* JADX WARN: Code duplicated, block: B:40:0x008f  */
    /* JADX WARN: Code duplicated, block: B:43:0x0099  */
    /* JADX WARN: Code duplicated, block: B:46:0x00a3  */
    /* JADX WARN: Code duplicated, block: B:48:0x00ac  */
    /* JADX WARN: Code duplicated, block: B:50:0x00b8  */
    /* JADX WARN: Code duplicated, block: B:52:0x00ca  */
    /* JADX WARN: Failed to restore switch over string. Please report as a decompilation issue */
    public final boolean handleIntent(Intent intent) {
        String scheme;
        String host;
        String str;
        Map<String, Function1<Uri, Unit>> map;
        String action = intent.getAction();
        if (action != null) {
            Map<String, Function1<Intent, Boolean>> map2 = actionCallbacks;
            if (map2.containsKey(action) && map2.get(action).invoke(intent).booleanValue()) {
                return true;
            }
        }
        Uri data = intent.getData();
        if ((intent.getFlags() & 1048576) == 0 && data != null && data.getScheme() != null && Intrinsics.areEqual(intent.getAction(), "android.intent.action.VIEW") && (scheme = data.getScheme()) != null) {
            int iHashCode = scheme.hashCode();
            if (iHashCode != 3699) {
                if (iHashCode != 3213448) {
                    host = data.getHost();
                    if (host != null) {
                        switch (host.hashCode()) {
                            case -831459633:
                                if (host.equals("telegram.dog")) {
                                    if (!data.getPathSegments().isEmpty()) {
                                        str = data.getPathSegments().get(0);
                                        map = callbacks;
                                        if (map.containsKey(str)) {
                                            map.get(str).invoke(data);
                                            return true;
                                        }
                                    }
                                }
                                break;
                            case -831448969:
                                if (host.equals("telegram.org")) {
                                    if (!data.getPathSegments().isEmpty()) {
                                        str = data.getPathSegments().get(0);
                                        map = callbacks;
                                        if (map.containsKey(str)) {
                                            map.get(str).invoke(data);
                                            return true;
                                        }
                                    }
                                }
                                break;
                            case 3503442:
                                if (host.equals("t.me")) {
                                    if (!data.getPathSegments().isEmpty()) {
                                        str = data.getPathSegments().get(0);
                                        map = callbacks;
                                        if (map.containsKey(str)) {
                                            map.get(str).invoke(data);
                                            return true;
                                        }
                                    }
                                }
                                break;
                            case 1912841637:
                                if (host.equals("telegram.me")) {
                                    if (!data.getPathSegments().isEmpty()) {
                                        str = data.getPathSegments().get(0);
                                        map = callbacks;
                                        if (map.containsKey(str)) {
                                            map.get(str).invoke(data);
                                            return true;
                                        }
                                    }
                                }
                                break;
                        }
                    }
                } else {
                    host = data.getHost();
                    if (host != null) {
                        switch (host.hashCode()) {
                            case -831459633:
                                if (host.equals("telegram.dog")) {
                                    if (!data.getPathSegments().isEmpty()) {
                                        str = data.getPathSegments().get(0);
                                        map = callbacks;
                                        if (map.containsKey(str)) {
                                            map.get(str).invoke(data);
                                            return true;
                                        }
                                    }
                                }
                                break;
                            case -831448969:
                                if (host.equals("telegram.org")) {
                                    if (!data.getPathSegments().isEmpty()) {
                                        str = data.getPathSegments().get(0);
                                        map = callbacks;
                                        if (map.containsKey(str)) {
                                            map.get(str).invoke(data);
                                            return true;
                                        }
                                    }
                                }
                                break;
                            case 3503442:
                                if (host.equals("t.me")) {
                                    if (!data.getPathSegments().isEmpty()) {
                                        str = data.getPathSegments().get(0);
                                        map = callbacks;
                                        if (map.containsKey(str)) {
                                            map.get(str).invoke(data);
                                            return true;
                                        }
                                    }
                                }
                                break;
                            case 1912841637:
                                if (host.equals("telegram.me")) {
                                    if (!data.getPathSegments().isEmpty()) {
                                        str = data.getPathSegments().get(0);
                                        map = callbacks;
                                        if (map.containsKey(str)) {
                                            map.get(str).invoke(data);
                                            return true;
                                        }
                                    }
                                }
                                break;
                        }
                    }
                }
            } else if (scheme.equals("tg")) {
                String host2 = data.getHost();
                Map<String, Function1<Uri, Unit>> map3 = deeplinkCallbacks;
                if (map3.containsKey(host2)) {
                    map3.get(data.getHost()).invoke(data);
                    return true;
                }
            }
        }
        return false;
    }
}
