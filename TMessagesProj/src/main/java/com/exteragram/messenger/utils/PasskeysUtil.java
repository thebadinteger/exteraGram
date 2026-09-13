package com.exteragram.messenger.utils;

import android.app.Activity;
import android.os.Build;
import androidx.credentials.CredentialManager;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.jvm.JvmStatic;
import okhttp3.internal.url._UrlKt;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;

@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fH\u0007J\u0010\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0005H\u0007J$\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u00052\b\u0010\u0014\u001a\u0004\u0018\u00010\u0005H\u0007J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0086T¢\u0006\u0002\n\u0000¨\u0006\u0019"}, d2 = {"Lcom/exteragram/messenger/utils/PasskeysUtil;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "TYPE_GET", _UrlKt.FRAGMENT_ENCODE_SET, "TYPE_CREATE", "BITWARDEN_URL", "KEEPASSDX_URL", "showUnsupportedBulletin", "Lorg/telegram/ui/Components/Bulletin;", "factory", "Lorg/telegram/ui/Components/BulletinFactory;", "computeClientDataHash", _UrlKt.FRAGMENT_ENCODE_SET, "clientDataJSON", "generateClientDataJSONRaw", "get", _UrlKt.FRAGMENT_ENCODE_SET, "challenge", "rpId", "openSettings", _UrlKt.FRAGMENT_ENCODE_SET, "activity", "Landroid/app/Activity;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class PasskeysUtil {
    private static final String TYPE_GET = "webauthn.get";
    private static final String TYPE_CREATE = "webauthn.create";
    public static final String BITWARDEN_URL = "https://github.com/bitwarden/android";
    public static final String KEEPASSDX_URL = "https://github.com/Kunzisoft/KeePassDX";
    public static final PasskeysUtil INSTANCE = new PasskeysUtil();

    private PasskeysUtil() {
    }

    @JvmStatic
    public static final Bulletin showUnsupportedBulletin(BulletinFactory factory) {
        Bulletin bulletinShow = factory.createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.PasskeyUnsupportedTitle), AndroidUtilities.replaceMultipleTags(LocaleController.getString(R.string.PasskeyUnsupportedMessage), new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                Browser.openUrl(ApplicationLoader.applicationContext, "https://github.com/bitwarden/android");
            }
        }, new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                Browser.openUrl(ApplicationLoader.applicationContext, "https://github.com/Kunzisoft/KeePassDX");
            }
        })).show();
        return bulletinShow;
    }

    @JvmStatic
    public static final byte[] computeClientDataHash(String clientDataJSON) throws NoSuchAlgorithmException {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        Charset charset = StandardCharsets.UTF_8;
        byte[] bytes = clientDataJSON.getBytes(charset);
        byte[] bArrDigest = messageDigest.digest(bytes);
        return bArrDigest;
    }

    @JvmStatic
    public static final String generateClientDataJSONRaw(boolean get, String challenge, String rpId) {
        try {
            return new JSONObject().put("type", get ? "webauthn.get" : "webauthn.create").put("challenge", challenge).put("origin", rpId).toString();
        } catch (org.json.JSONException e) {
            org.telegram.messenger.FileLog.e(e);
            return "";
        }
    }

    @JvmStatic
    public static final void openSettings(Activity activity) {
        if (Build.VERSION.SDK_INT < 34) {
            return;
        }
        try {
            CredentialManager.create(activity).createSettingsPendingIntent().send();
        } catch (Throwable th) {
            org.telegram.messenger.FileLog.e(th);
        }
    }
}
