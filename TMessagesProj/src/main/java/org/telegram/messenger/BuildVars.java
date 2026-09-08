package org.telegram.messenger;

import android.os.Build;
import com.exteragram.messenger.ExteraConfig;
import okhttp3.internal.url._UrlKt;

public class BuildVars {
    public static int BUILD_VERSION = 0;
    public static String BUILD_VERSION_STRING = null;
    public static boolean DEBUG_VERSION = false;
    private static final String EXTERA_APP_HASH;
    private static final int EXTERA_APP_ID;
    public static String GITHUB_APP_URL;
    public static String GOOGLE_AUTH_CLIENT_ID;
    public static boolean IS_BILLING_UNAVAILABLE;
    public static boolean IS_LITE_VERSION;
    public static boolean NO_SCOPED_STORAGE;
    public static boolean PM_BUILD;
    public static String RELEASES_URL;
    public static String SAFETYNET_KEY;
    public static boolean SUPPORTS_PASSKEYS;
    public static boolean USE_LEGACY_SYSTEM_INSETS;
    public static boolean LOGS_ENABLED = ExteraConfig.getLogging();
    public static boolean DEBUG_PRIVATE_VERSION = false;
    public static boolean USE_CLOUD_STRINGS = true;
    public static boolean CHECK_UPDATES = false;

    private static boolean hasDirectCurrency() {
        return false;
    }

    public static boolean useInvoiceBilling() {
        return true;
    }

    static {
        int i = Build.VERSION.SDK_INT;
        NO_SCOPED_STORAGE = i <= 29;
        PM_BUILD = false;
        IS_LITE_VERSION = false;
        SAFETYNET_KEY = _UrlKt.FRAGMENT_ENCODE_SET;
        GITHUB_APP_URL = "https://github.com/exteraSquad/exteraGram/releases/latest";
        RELEASES_URL = "https://t.me/exteraReleases";
        GOOGLE_AUTH_CLIENT_ID = "760348033671-81kmi3pi84p11ub8hp9a1funsv0rn2p9.apps.googleusercontent.com";
        IS_BILLING_UNAVAILABLE = false;
        SUPPORTS_PASSKEYS = i >= 34;
        USE_LEGACY_SYSTEM_INSETS = false;
        BUILD_VERSION = BuildConfig.VERSION_CODE;
        BUILD_VERSION_STRING = "12.9.0";
        EXTERA_APP_ID = BuildConfig.APP_ID;
        EXTERA_APP_HASH = BuildConfig.APP_HASH;
    }

    public static int getExteraAppId() {
        return EXTERA_APP_ID;
    }

    public static String getExteraAppHash() {
        return EXTERA_APP_HASH;
    }

    public static boolean isBetaApp() {
        return DEBUG_VERSION;
    }

    public static String getSmsHash() {
        return DEBUG_VERSION ? "O2P2z+/jBpJ" : "oLeq9AcOZkT";
    }
}
