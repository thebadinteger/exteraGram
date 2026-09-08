package org.telegram.messenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.app.PictureInPictureParams;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.Process;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.CalendarContract;
import android.provider.CallLog;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.media.session.MediaSessionCompat$$ExternalSyntheticThrowCCEIfNotNull0;
import android.system.ErrnoException;
import android.system.OsConstants;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.text.util.Linkify;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.util.StateSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.Surface;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import android.view.inspector.WindowInspector;
import android.webkit.MimeTypeMap;
import android.widget.EdgeEffect;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.core.view.WindowCompat;
import androidx.core.widget.NestedScrollView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.exifinterface.media.ExifInterface;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import c.f$$ExternalSyntheticBUOutline2;
import com.android.dx.util.IntList$$ExternalSyntheticBUOutline0;
import com.exteragram.messenger.DividerStyle;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.proxy.ProxyController;
import com.exteragram.messenger.utils.ui.FontUtils;
import com.google.android.exoplayer2.util.Consumer;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.cast.MediaError;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.tasks.OnSuccessListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.IDN;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPOutputStream;
import kotlin.text.Typography;
import kotlin.time.DurationKt;
import me.vkryl.core.BitwiseUtils;
import okhttp3.internal.url._UrlKt;
import org.mvel2.MVEL;
import org.mvel2.asm.Opcodes;
import org.mvel2.asm.signature.SignatureVisitor;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.utils.CustomHtml;
import org.telegram.messenger.utils.DebugRecordingCanvas;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestTimeDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatBackgroundDrawable;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.ButtonSpan;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ForegroundColorSpanThemable;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.Components.HideViewAfterAnimation;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.TableView;
import org.telegram.ui.Components.TextHelper;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.DebugRecordingCanvasReplayFragment;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stories.PeerStoriesView;
import org.telegram.ui.Stories.StoryMediaAreasView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;
import org.telegram.ui.ThemePreviewActivity;
import org.telegram.ui.WallpapersListActivity;
import org.webrtc.MediaStreamTrack;

public class AndroidUtilities {
    public static Pattern BAD_CHARS_MESSAGE_LONG_PATTERN = null;
    public static Pattern BAD_CHARS_MESSAGE_PATTERN = null;
    public static Pattern BAD_CHARS_PATTERN = null;
    public static final int FLAG_TAG_ALL = 11;
    public static final int FLAG_TAG_BOLD = 2;
    public static final int FLAG_TAG_BR = 1;
    public static final int FLAG_TAG_COLOR = 4;
    public static final int FLAG_TAG_URL = 8;
    private static final ExecutorService LINK_EXECUTOR;
    public static Pattern LONG_BAD_CHARS_PATTERN = null;
    public static Pattern REMOVE_MULTIPLE_DIACRITICS = null;
    public static final int REPLACING_TAG_TYPE_BOLD = 1;
    public static final int REPLACING_TAG_TYPE_LINK = 0;
    public static final int REPLACING_TAG_TYPE_LINKBOLD = 2;
    public static final int REPLACING_TAG_TYPE_LINK_NBSP = 3;
    public static final int REPLACING_TAG_TYPE_UNDERLINE = 4;
    public static final String STICKERS_PLACEHOLDER_PACK_NAME = "tg_placeholders_android";
    public static final String STICKERS_PLACEHOLDER_PACK_NAME_2 = "tg_superplaceholders_android_2";
    public static final String TYPEFACE_MERRIWEATHER_BOLD = "fonts/mw_bold.ttf";
    public static final String TYPEFACE_NUNITO_EXTRABOLD = "fonts/nunito_extrabold.ttf";
    public static final String TYPEFACE_ROBOTO_CONDENSED_BOLD = "fonts/rcondensedbold.ttf";
    public static final String TYPEFACE_ROBOTO_EXTRA_BOLD = "fonts/rextrabold.ttf";
    public static final String TYPEFACE_ROBOTO_ITALIC = "fonts/ritalic.ttf";
    public static final String TYPEFACE_ROBOTO_MEDIUM = "fonts/rmedium.ttf";
    public static final String TYPEFACE_ROBOTO_MEDIUM_ITALIC = "fonts/rmediumitalic.ttf";
    public static final String TYPEFACE_ROBOTO_MONO = "fonts/rmono.ttf";
    public static final String TYPEFACE_ROBOTO_REGULAR = "fonts/rregular.ttf";
    public static Pattern WEB_URL;
    private static AccessibilityManager accessibilityManager;
    private static CallReceiver callReceiver;
    private static final char[] characters;
    private static HashSet<Character> charactersMap;
    private static final int[] documentIcons;
    private static final int[] documentMediaIcons;
    public static boolean firstConfigurationWas;
    private static SimpleDateFormat generatingVideoPathFormat;
    private static final boolean hasCallPermissions;
    public static boolean incorrectDisplaySizeFix;
    private static Boolean isHonor;
    public static boolean isInMultiwindow;
    public static int leftBaseline;
    private static Pattern linksPattern;
    private static Field mAttachInfoField;
    private static Field mStableInsetsField;
    public static boolean makingGlobalBlurBitmap;
    public static Typeface mediumTypeface;
    private static final Paint navbarProtactionPaint;
    private static HashMap<Window, ValueAnimator> navigationBarColorAnimators;
    public static final String[] numbersSignatureArray;
    public static int roundMessageInset;
    public static int roundMessageSize;
    public static int roundPlayingMessageSize;
    public static int roundSidePlayingMessageSize;
    public static final Linkify.MatchFilter sUrlMatchFilter;
    private static final float[] tempFloats;
    private static final float[] tempFloats2;
    public static float touchSlop;
    private static Pattern uriParse;
    public static boolean usingHardwareInput;
    private static Vibrator vibrator;
    public static ThreadLocal<byte[]> readBufferLocal = new ThreadLocal<>();
    public static ThreadLocal<byte[]> bufferLocal = new ThreadLocal<>();
    private static final ConcurrentHashMap<String, Typeface> typefaceCache = new ConcurrentHashMap<>();
    private static int prevOrientation = -10;
    private static boolean waitingForSms = false;
    private static boolean waitingForCall = false;
    private static final Object smsLock = new Object();
    private static final Object callLock = new Object();

    @Deprecated
    public static int statusBarHeight = 0;

    @Deprecated
    public static int navigationBarHeight = 0;
    public static float density = 1.0f;
    public static Point displaySize = new Point();
    public static float screenRefreshRate = 60.0f;
    public static float screenMaxRefreshRate = 60.0f;
    public static float screenRefreshTime = 1000.0f / 60.0f;
    public static Integer photoSize = null;
    public static Integer highQualityPhotoSize = null;
    public static DisplayMetrics displayMetrics = new DisplayMetrics();
    public static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    public static AccelerateInterpolator accelerateInterpolator = new AccelerateInterpolator();
    public static OvershootInterpolator overshootInterpolator = new OvershootInterpolator();
    private static Boolean isTablet = null;
    private static Boolean wasTablet = null;
    private static Boolean isSmallScreen = null;
    private static int adjustOwnerClassGuid = 0;
    private static int altFocusableClassGuid = 0;
    public static final RectF rectTmp = new RectF();
    public static final Rect rectTmp2 = new Rect();
    public static final int[] pointTmp2 = new int[2];
    public static Pattern REMOVE_RTL = null;
    private static Pattern singleTagPatter = null;
    public static final Paint strokeTop = new Paint(1);
    public static final Paint strokeBottom = new Paint(1);

    public interface IntColorCallback {
        void run(int i);
    }

    public static int compare(int i, int i2) {
        if (i == i2) {
            return 0;
        }
        return i > i2 ? 1 : -1;
    }

    public static int compare(long j, long j2) {
        if (j == j2) {
            return 0;
        }
        return j > j2 ? 1 : -1;
    }

    public static double fixLocationCoord(double d) {
        return ((long) (d * 1000000.0d)) / 1000000.0d;
    }

    public static int getMyLayerVersion(int i) {
        return i & 65535;
    }

    public static int getWallpaperRotation(int i, boolean z) {
        int i2 = z ? i + 180 : i - 180;
        while (i2 >= 360) {
            i2 -= 360;
        }
        while (i2 < 0) {
            i2 += 360;
        }
        return i2;
    }

    public static float ilerp(float f, float f2, float f3) {
        return (f - f2) / (f3 - f2);
    }

    public static float ilerp(int i, int i2, int i3) {
        return (i - i2) / (i3 - i2);
    }

    public static boolean isValidWallChar(char c2) {
        return c2 == '-' || c2 == '~';
    }

    public static double lerp(double d, double d2, float f) {
        return d + (((double) f) * (d2 - d));
    }

    public static float lerp(float f, float f2, float f3) {
        return f + (f3 * (f2 - f));
    }

    public static float lerp(boolean z, boolean z2, float f) {
        return (z ? 1.0f : 0.0f) + (f * ((z2 ? 1.0f : 0.0f) - (z ? 1.0f : 0.0f)));
    }

    public static int lerp(int i, int i2, float f) {
        return (int) (i + (f * (i2 - i)));
    }

    public static float lerpAngle(float f, float f2, float f3) {
        return ((f + ((((((f2 - f) + 360.0f) + 180.0f) % 360.0f) - 180.0f) * f3)) + 360.0f) % 360.0f;
    }

    public static void logFlagSecure() {
    }

    public static long pack(int i, int i2) {
        return (((long) i2) & 4294967295L) | (((long) i) << 32);
    }

    public static int setMyLayerVersion(int i, int i2) {
        return (i & Opcodes.V_PREVIEW) | i2;
    }

    public static int setPeerLayerVersion(int i, int i2) {
        return (i & 65535) | (i2 << 16);
    }

    public static int unpackA(long j) {
        return (int) (j >> 32);
    }

    public static int unpackB(long j) {
        return (int) j;
    }

    static {
        WEB_URL = null;
        BAD_CHARS_PATTERN = null;
        LONG_BAD_CHARS_PATTERN = null;
        BAD_CHARS_MESSAGE_PATTERN = null;
        BAD_CHARS_MESSAGE_LONG_PATTERN = null;
        REMOVE_MULTIPLE_DIACRITICS = null;
        try {
            BAD_CHARS_PATTERN = Pattern.compile("[─-◿]");
            LONG_BAD_CHARS_PATTERN = Pattern.compile("[一-鿿]");
            BAD_CHARS_MESSAGE_LONG_PATTERN = Pattern.compile("[̀-ͯ\u2066-\u2067]");
            BAD_CHARS_MESSAGE_PATTERN = Pattern.compile("[\u2066-\u2067]+");
            REMOVE_MULTIPLE_DIACRITICS = Pattern.compile("([\\u0300-\\u036f]{1,2})[\\u0300-\\u036f]+");
            WEB_URL = Pattern.compile("((?:(http|https|Http|Https|ton|tg|tonsite):\\/\\/(?:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,64}(?:\\:(?:[a-zA-Z0-9\\$\\-\\_\\.\\+\\!\\*\\'\\(\\)\\,\\;\\?\\&\\=]|(?:\\%[a-fA-F0-9]{2})){1,25})?\\@)?)?(?:" + Pattern.compile("(([a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef]([a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef\\-]{0,61}[a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef]){0,1}\\.)+[a-zA-Z -\ud7ff豈-﷏ﷰ-\uffef]{2,63}|" + Pattern.compile("((25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9])\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[1-9]|0)\\.(25[0-5]|2[0-4][0-9]|[0-1][0-9]{2}|[1-9][0-9]|[0-9]))") + ")") + ")(?:\\:\\d{1,5})?)(\\/(?:(?:[a-zA-Z0-9 -\ud7ff豈-﷏ﷰ-\uffef\\;\\/\\?\\:\\@\\&\\=\\#\\~\\-\\.\\+\\!\\*\\'\\(\\)\\,\\_])|(?:\\%[a-fA-F0-9]{2}))*)?(?:\\b|$)");
        } catch (Exception e) {
            FileLog.e(e);
        }
        leftBaseline = isTablet() ? 80 : 72;
        checkDisplaySize(ApplicationLoader.applicationContext, null);
        documentIcons = new int[]{R.drawable.media_doc_blue, R.drawable.media_doc_green, R.drawable.media_doc_red, R.drawable.media_doc_yellow};
        documentMediaIcons = new int[]{R.drawable.media_doc_blue_b, R.drawable.media_doc_green_b, R.drawable.media_doc_red_b, R.drawable.media_doc_yellow_b};
        sUrlMatchFilter = new Linkify.MatchFilter() { 
            @Override // android.text.util.Linkify.MatchFilter
            public final boolean acceptMatch(CharSequence charSequence, int i, int i2) {
                return AndroidUtilities.m4076$r8$lambda$TqylQ7mqSnaj2Z10Afg6KQFADI(charSequence, i, i2);
            }
        };
        int i = ConnectionsManager.CPU_COUNT;
        LINK_EXECUTOR = new ThreadPoolExecutor(i, i, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(100), new ThreadPoolExecutor.DiscardPolicy());
        hasCallPermissions = true;
        numbersSignatureArray = new String[]{_UrlKt.FRAGMENT_ENCODE_SET, "K", "M", "B", "T", "P"};
        tempFloats = new float[9];
        tempFloats2 = new float[9];
        characters = new char[]{Typography.nbsp, ' ', '!', Typography.quote, '#', '%', Typography.amp, '\'', '(', ')', '*', ',', SignatureVisitor.SUPER, '.', '/', ':', ';', '?', '@', '[', '\\', ']', '_', '{', '}', 161, Typography.section, 171, Typography.paragraph, Typography.middleDot, 187, 191, 894, 903, 1370, 1371, 1372, 1373, 1374, 1375, 1417, 1418, 1470, 1472, 1475, 1478, 1523, 1524, 1545, 1546, 1548, 1549, 1563, 1566, 1567, 1642, 1643, 1644, 1645, 1748, 1792, 1793, 1794, 1795, 1796, 1797, 1798, 1799, 1800, 1801, 1802, 1803, 1804, 1805, 2039, 2040, 2041, 2096, 2097, 2098, 2099, 2100, 2101, 2102, 2103, 2104, 2105, 2106, 2107, 2108, 2109, 2110, 2142, 2404, 2405, 2416, 2557, 2678, 2800, 3191, 3204, 3572, 3663, 3674, 3675, 3844, 3845, 3846, 3847, 3848, 3849, 3850, 3851, 3852, 3853, 3854, 3855, 3856, 3857, 3858, 3860, 3898, 3899, 3900, 3901, 3973, 4048, 4049, 4050, 4051, 4052, 4057, 4058, 4170, 4171, 4172, 4173, 4174, 4175, 4347, 4960, 4961, 4962, 4963, 4964, 4965, 4966, 4967, 4968, 5120, 5742, 5787, 5788, 5867, 5868, 5869, 5941, 5942, 6100, 6101, 6102, 6104, 6105, 6106, 6144, 6145, 6146, 6147, 6148, 6149, 6150, 6151, 6152, 6153, 6154, 6468, 6469, 6686, 6687, 6816, 6817, 6818, 6819, 6820, 6821, 6822, 6824, 6825, 6826, 6827, 6828, 6829, 7002, 7003, 7004, 7005, 7006, 7007, 7008, 7164, 7165, 7166, 7167, 7227, 7228, 7229, 7230, 7231, 7294, 7295, 7360, 7361, 7362, 7363, 7364, 7365, 7366, 7367, 7379, 8208, 8209, 8210, Typography.ndash, Typography.mdash, 8213, 8214, 8215, Typography.leftSingleQuote, Typography.rightSingleQuote, Typography.lowSingleQuote, 8219, Typography.leftDoubleQuote, Typography.rightDoubleQuote, Typography.lowDoubleQuote, 8223, Typography.dagger, Typography.doubleDagger, Typography.bullet, 8227, 8228, 8229, Typography.ellipsis, 8231, 8240, 8241, Typography.prime, Typography.doublePrime, 8244, 8245, 8246, 8247, 8248, 8249, 8250, 8251, 8252, 8253, 8254, 8255, 8256, 8257, 8258, 8259, 8261, 8262, 8263, 8264, 8265, 8266, 8267, 8268, 8269, 8270, 8271, 8272, 8273, 8275, 8276, 8277, 8278, 8279, 8280, 8281, 8282, 8283, 8284, 8285, 8286, 8317, 8318, 8333, 8334, 8968, 8969, 8970, 8971, 9001, 9002, 10088, 10089, 10090, 10091, 10092, 10093, 10094, 10095, 10096, 10097, 10098, 10099, 10100, 10101, 10181, 10182, 10214, 10215, 10216, 10217, 10218, 10219, 10220, 10221, 10222, 10223, 10627, 10628, 10629, 10630, 10631, 10632, 10633, 10634, 10635, 10636, 10637, 10638, 10639, 10640, 10641, 10642, 10643, 10644, 10645, 10646, 10647, 10648, 10712, 10713, 10714, 10715, 10748, 10749, 11513, 11514, 11515, 11516, 11518, 11519, 11632, 11776, 11777, 11778, 11779, 11780, 11781, 11782, 11783, 11784, 11785, 11786, 11787, 11788, 11789, 11790, 11791, 11792, 11793, 11794, 11795, 11796, 11797, 11798, 11799, 11800, 11801, 11802, 11803, 11804, 11805, 11806, 11807, 11808, 11809, 11810, 11811, 11812, 11813, 11814, 11815, 11816, 11817, 11818, 11819, 11820, 11821, 11822, 11824, 11825, 11826, 11827, 11828, 11829, 11830, 11831, 11832, 11833, 11834, 11835, 11836, 11837, 11838, 11839, 11840, 11841, 11842, 11843, 11844, 11845, 11846, 11847, 11848, 11849, 11850, 11851, 11852, 11853, 11854, 11855, 12289, 12290, 12291, 12296, 12297, 12298, 12299, 12300, 12301, 12302, 12303, 12304, 12305, 12308, 12309, 12310, 12311, 12312, 12313, 12314, 12315, 12316, 12317, 12318, 12319, 12336, 12349, 12448, 12539, 42238, 42239, 42509, 42510, 42511, 42611, 42622, 42738, 42739, 42740, 42741, 42742, 42743, 43124, 43125, 43126, 43127, 43214, 43215, 43256, 43257, 43258, 43260, 43310, 43311, 43359, 43457, 43458, 43459, 43460, 43461, 43462, 43463, 43464, 43465, 43466, 43467, 43468, 43469, 43486, 43487, 43612, 43613, 43614, 43615, 43742, 43743, 43760, 43761, 44011, 64830, 64831, 65040, 65041, 65042, 65043, 65044, 65045, 65046, 65047, 65048, 65049, 65072, 65073, 65074, 65075, 65076, 65077, 65078, 65079, 65080, 65081, 65082, 65083, 65084, 65085, 65086, 65087, 65088, 65089, 65090, 65091, 65092, 65093, 65094, 65095, 65096, 65097, 65098, 65099, 65100, 65101, 65102, 65103, 65104, 65105, 65106, 65108, 65109, 65110, 65111, 65112, 65113, 65114, 65115, 65116, 65117, 65118, 65119, 65120, 65121, 65123, 65128, 65130, 65131, 65281, 65282, 65283, 65285, 65286, 65287, 65288, 65289, 65290, 65292, 65293, 65294, 65295, 65306, 65307, 65311, 65312, 65339, 65340, 65341, 65343, 65371, 65373, 65375, 65376, 65377, 65378, 65379, 65380, 65381};
        navbarProtactionPaint = new Paint(1);
    }

    public static Typeface bold() {
        if (mediumTypeface == null) {
            if (SharedConfig.useSystemBoldFont && Build.VERSION.SDK_INT >= 28) {
                mediumTypeface = FontUtils.getSystemTypeface(TYPEFACE_ROBOTO_MEDIUM);
            } else {
                mediumTypeface = getTypeface(TYPEFACE_ROBOTO_MEDIUM);
            }
        }
        return mediumTypeface;
    }

    public static Typeface regular() {
        return getTypeface(TYPEFACE_ROBOTO_REGULAR);
    }

    public static int roundPlayingMessageSize(boolean z) {
        return z ? roundSidePlayingMessageSize : roundPlayingMessageSize;
    }

    public static String removeDiacritics(String str) {
        Matcher matcher;
        if (str == null) {
            return null;
        }
        Pattern pattern = REMOVE_MULTIPLE_DIACRITICS;
        return (pattern == null || (matcher = pattern.matcher(str)) == null) ? str : matcher.replaceAll("$1");
    }

    public static String removeRTL(String str) {
        if (str == null) {
            return null;
        }
        if (REMOVE_RTL == null) {
            REMOVE_RTL = Pattern.compile("[\\u200E\\u200F\\u202A-\\u202E]");
        }
        Matcher matcher = REMOVE_RTL.matcher(str);
        return matcher == null ? str : matcher.replaceAll(_UrlKt.FRAGMENT_ENCODE_SET);
    }

    public static String escape(String str) {
        return removeRTL(removeDiacritics(str));
    }

    private static boolean containsUnsupportedCharacters(String str) {
        if (str.contains("\u202c") || str.contains("\u202d") || str.contains("\u202e")) {
            return true;
        }
        try {
            return BAD_CHARS_PATTERN.matcher(str).find();
        } catch (Throwable unused) {
            return true;
        }
    }

    public static String getSafeString(String str) {
        try {
            return BAD_CHARS_MESSAGE_PATTERN.matcher(str).replaceAll("\u200c");
        } catch (Throwable unused) {
            return str;
        }
    }

    public static CharSequence ellipsizeCenterEnd(CharSequence charSequence, String str, int i, TextPaint textPaint, int i2) {
        Exception exc;
        CharSequence charSequenceSubSequence;
        try {
            int length = charSequence.length();
            int iIndexOf = charSequence.toString().toLowerCase().indexOf(str);
            if (length > i2) {
                charSequence = charSequence.subSequence(Math.max(0, iIndexOf - (i2 / 2)), Math.min(length, (i2 / 2) + iIndexOf));
                iIndexOf -= Math.max(0, iIndexOf - (i2 / 2));
                charSequence.length();
            }
            CharSequence charSequence2 = charSequence;
            try {
                StaticLayout staticLayout = new StaticLayout(charSequence2, textPaint, Integer.MAX_VALUE, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
                float lineWidth = staticLayout.getLineWidth(0);
                float f = i;
                if (textPaint.measureText("...") + lineWidth >= f) {
                    int i3 = iIndexOf + 1;
                    int i4 = i3;
                    while (i4 < charSequence2.length() - 1 && !Character.isWhitespace(charSequence2.charAt(i4))) {
                        i4++;
                    }
                    float primaryHorizontal = staticLayout.getPrimaryHorizontal(i4);
                    if (staticLayout.isRtlCharAt(i4)) {
                        primaryHorizontal = lineWidth - primaryHorizontal;
                    }
                    if (primaryHorizontal >= f) {
                        float f2 = 0.1f * f;
                        float fMeasureText = (primaryHorizontal - f) + (textPaint.measureText("...") * 2.0f) + f2;
                        if (charSequence2.length() - i4 > 20) {
                            fMeasureText += f2;
                        }
                        if (fMeasureText > 0.0f) {
                            int offsetForHorizontal = staticLayout.getOffsetForHorizontal(0, fMeasureText);
                            if (offsetForHorizontal > charSequence2.length() - 1) {
                                offsetForHorizontal = charSequence2.length() - 1;
                            }
                            int i5 = 0;
                            while (!Character.isWhitespace(charSequence2.charAt(offsetForHorizontal)) && i5 < 10) {
                                i5++;
                                offsetForHorizontal++;
                                if (offsetForHorizontal > charSequence2.length() - 1) {
                                    offsetForHorizontal = staticLayout.getOffsetForHorizontal(0, fMeasureText);
                                    break;
                                }
                            }
                            if (i5 >= 10) {
                                charSequenceSubSequence = charSequence2.subSequence(staticLayout.getOffsetForHorizontal(0, staticLayout.getPrimaryHorizontal(i3) - (f * 0.3f)), charSequence2.length());
                            } else {
                                if (offsetForHorizontal > 0 && offsetForHorizontal < charSequence2.length() - 2 && Character.isWhitespace(charSequence2.charAt(offsetForHorizontal))) {
                                    offsetForHorizontal++;
                                }
                                charSequenceSubSequence = charSequence2.subSequence(offsetForHorizontal, charSequence2.length());
                            }
                            return SpannableStringBuilder.valueOf("...").append(charSequenceSubSequence);
                        }
                    }
                }
                return charSequence2;
            } catch (Exception e) {
                exc = e;
                charSequence = charSequence2;
                FileLog.e(exc);
                return charSequence;
            }
        } catch (Exception e2) {
            exc = e2;
        }
    }

    public static CharSequence highlightText(CharSequence charSequence, ArrayList<String> arrayList, Theme.ResourcesProvider resourcesProvider) {
        if (arrayList == null) {
            return null;
        }
        int i = 0;
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            CharSequence charSequenceHighlightText = highlightText(charSequence, arrayList.get(i2), resourcesProvider);
            if (charSequenceHighlightText != null) {
                charSequence = charSequenceHighlightText;
            } else {
                i++;
            }
        }
        if (i == arrayList.size()) {
            return null;
        }
        return charSequence;
    }

    public static CharSequence highlightText(CharSequence charSequence, String str, Theme.ResourcesProvider resourcesProvider) {
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(charSequence)) {
            return null;
        }
        String lowerCase = charSequence.toString().toLowerCase();
        SpannableStringBuilder spannableStringBuilderValueOf = SpannableStringBuilder.valueOf(charSequence);
        int iIndexOf = lowerCase.indexOf(str);
        while (iIndexOf >= 0) {
            try {
                spannableStringBuilderValueOf.setSpan(new ForegroundColorSpanThemable(Theme.key_windowBackgroundWhiteBlueText4, resourcesProvider), iIndexOf, Math.min(str.length() + iIndexOf, charSequence.length()), 0);
            } catch (Exception e) {
                FileLog.e(e);
            }
            iIndexOf = lowerCase.indexOf(str, iIndexOf + 1);
        }
        return spannableStringBuilderValueOf;
    }

    public static Activity getActivity() {
        return getActivity(null);
    }

    public static Activity getActivity(Context context) {
        Activity activityFindActivity = findActivity(context);
        if (activityFindActivity == null || activityFindActivity.isFinishing()) {
            activityFindActivity = LaunchActivity.instance;
        }
        return (activityFindActivity == null || activityFindActivity.isFinishing()) ? findActivity(ApplicationLoader.applicationContext) : activityFindActivity;
    }

    public static Activity findActivity(Context context) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        if (context instanceof ContextWrapper) {
            return findActivity(((ContextWrapper) context).getBaseContext());
        }
        return null;
    }

    public static SpannableStringBuilder premiumText(String str, Runnable runnable) {
        return replaceSingleTag(str, -1, 2, runnable);
    }

    public static SpannableStringBuilder replaceSingleTag(String str, Runnable runnable) {
        return replaceSingleTag(str, -1, 0, runnable);
    }

    public static SpannableStringBuilder replaceSingleTag(String str, int i, int i2, Runnable runnable) {
        return replaceSingleTag(str, i, i2, runnable, null);
    }

    public static SpannableStringBuilder replaceSingleTag(String str, final int i, final int i2, final Runnable runnable, final Theme.ResourcesProvider resourcesProvider) {
        int i3;
        int i4;
        int iIndexOf = str.indexOf("**");
        int iIndexOf2 = str.indexOf("**", iIndexOf + 1);
        String strReplace = str.replace("**", _UrlKt.FRAGMENT_ENCODE_SET);
        if (iIndexOf < 0 || iIndexOf2 < 0 || (i4 = iIndexOf2 - iIndexOf) <= 2) {
            iIndexOf = -1;
            i3 = 0;
        } else {
            i3 = i4 - 2;
        }
        if (iIndexOf < 0) {
            int iIndexOf3 = strReplace.indexOf("[");
            int i5 = iIndexOf3 + 1;
            int iIndexOf4 = strReplace.indexOf("]()", i5);
            if (iIndexOf3 >= 0 && iIndexOf4 > iIndexOf3) {
                i3 = (iIndexOf4 - iIndexOf3) - 1;
                strReplace = strReplace.substring(0, iIndexOf3) + strReplace.substring(i5, iIndexOf4) + strReplace.substring(iIndexOf4 + 3);
                iIndexOf = iIndexOf3;
            }
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(strReplace);
        if (iIndexOf >= 0) {
            if (i2 == 3) {
                int i6 = iIndexOf + i3;
                spannableStringBuilder.replace(iIndexOf, i6, replaceMultipleCharSequence(" ", spannableStringBuilder.subSequence(iIndexOf, i6), " "));
            }
            if (i2 == 0 || i2 == 3 || i2 == 2 || i2 == 4) {
                spannableStringBuilder.setSpan(new ClickableSpan() { 
                    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                    public void updateDrawState(TextPaint textPaint) {
                        super.updateDrawState(textPaint);
                        textPaint.setUnderlineText(i2 == 4);
                        int i7 = i;
                        if (i7 >= 0) {
                            textPaint.setColor(Theme.getColor(i7, resourcesProvider));
                        }
                        if (i2 == 2) {
                            textPaint.setTypeface(AndroidUtilities.bold());
                        }
                    }

                    @Override // android.text.style.ClickableSpan
                    public void onClick(View view) {
                        Runnable runnable2 = runnable;
                        if (runnable2 != null) {
                            runnable2.run();
                        }
                    }
                }, iIndexOf, i3 + iIndexOf, 0);
            } else {
                spannableStringBuilder.setSpan(new CharacterStyle() { 
                    @Override // android.text.style.CharacterStyle
                    public void updateDrawState(TextPaint textPaint) {
                        textPaint.setTypeface(AndroidUtilities.bold());
                        int alpha = textPaint.getAlpha();
                        textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, resourcesProvider));
                        textPaint.setAlpha(alpha);
                    }
                }, iIndexOf, i3 + iIndexOf, 0);
                return spannableStringBuilder;
            }
        }
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder makeClickable(CharSequence charSequence, final int i, final Runnable runnable, final Theme.ResourcesProvider resourcesProvider) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        if (i == 0 || i == 3 || i == 2 || i == 4) {
            spannableStringBuilder.setSpan(new ClickableSpan() { 
                @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                public void updateDrawState(TextPaint textPaint) {
                    super.updateDrawState(textPaint);
                    textPaint.setUnderlineText(i == 4);
                    if (i == 2) {
                        textPaint.setTypeface(AndroidUtilities.bold());
                    }
                }

                @Override // android.text.style.ClickableSpan
                public void onClick(View view) {
                    Runnable runnable2 = runnable;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                }
            }, 0, spannableStringBuilder.length(), 0);
            return spannableStringBuilder;
        }
        spannableStringBuilder.setSpan(new CharacterStyle() { 
            @Override // android.text.style.CharacterStyle
            public void updateDrawState(TextPaint textPaint) {
                textPaint.setTypeface(AndroidUtilities.bold());
                int alpha = textPaint.getAlpha();
                textPaint.setColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlueText, resourcesProvider));
                textPaint.setAlpha(alpha);
            }
        }, 0, spannableStringBuilder.length(), 0);
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder makeClickable(CharSequence charSequence, Runnable runnable) {
        return makeClickable(charSequence, 0, runnable, null);
    }

    public static SpannableStringBuilder replaceMultipleTags(String str, Runnable... runnableArr) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        for (final Runnable runnable : runnableArr) {
            int iCharSequenceIndexOf = charSequenceIndexOf(spannableStringBuilder, "**");
            int i = iCharSequenceIndexOf + 2;
            int iCharSequenceIndexOf2 = charSequenceIndexOf(spannableStringBuilder, "**", i);
            if (iCharSequenceIndexOf < 0 || iCharSequenceIndexOf2 < 0) {
                break;
            }
            spannableStringBuilder.delete(iCharSequenceIndexOf, i);
            int i2 = iCharSequenceIndexOf2 - 2;
            spannableStringBuilder.delete(i2, iCharSequenceIndexOf2);
            spannableStringBuilder.setSpan(new ClickableSpan() { 
                @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                public void updateDrawState(TextPaint textPaint) {
                    super.updateDrawState(textPaint);
                    textPaint.setUnderlineText(false);
                }

                @Override // android.text.style.ClickableSpan
                public void onClick(View view) {
                    Runnable runnable2 = runnable;
                    if (runnable2 != null) {
                        runnable2.run();
                    }
                }
            }, iCharSequenceIndexOf, i2, 33);
        }
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder replaceSingleLink(String str, int i) {
        return replaceSingleLink(str, i, null);
    }

    public static SpannableStringBuilder replaceSingleLink(String str, final int i, final Runnable runnable) {
        int i2;
        int i3;
        int iIndexOf = str.indexOf("**");
        int iIndexOf2 = str.indexOf("**", iIndexOf + 1);
        String strReplace = str.replace("**", _UrlKt.FRAGMENT_ENCODE_SET);
        if (iIndexOf < 0 || iIndexOf2 < 0 || (i3 = iIndexOf2 - iIndexOf) <= 2) {
            iIndexOf = -1;
            i2 = 0;
        } else {
            i2 = i3 - 2;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(strReplace);
        if (iIndexOf >= 0) {
            if (runnable != null) {
                spannableStringBuilder.setSpan(new ClickableSpan() { 
                    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                    public void updateDrawState(TextPaint textPaint) {
                        super.updateDrawState(textPaint);
                        textPaint.setUnderlineText(false);
                        textPaint.setColor(i);
                    }

                    @Override // android.text.style.ClickableSpan
                    public void onClick(View view) {
                        Runnable runnable2 = runnable;
                        if (runnable2 != null) {
                            runnable2.run();
                        }
                    }
                }, iIndexOf, i2 + iIndexOf, 0);
                return spannableStringBuilder;
            }
            spannableStringBuilder.setSpan(new CharacterStyle() { 
                @Override // android.text.style.CharacterStyle
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setUnderlineText(false);
                    textPaint.setColor(i);
                }
            }, iIndexOf, i2 + iIndexOf, 0);
        }
        return spannableStringBuilder;
    }

    public static SpannableStringBuilder replaceSingleLinkBold(String str, int i) {
        return replaceSingleLinkBold(str, i, null);
    }

    public static SpannableStringBuilder replaceSingleLinkBold(String str, final int i, final Runnable runnable) {
        int i2;
        int i3;
        int iIndexOf = str.indexOf("**");
        int iIndexOf2 = str.indexOf("**", iIndexOf + 1);
        String strReplace = str.replace("**", _UrlKt.FRAGMENT_ENCODE_SET);
        if (iIndexOf < 0 || iIndexOf2 < 0 || (i3 = iIndexOf2 - iIndexOf) <= 2) {
            iIndexOf = -1;
            i2 = 0;
        } else {
            i2 = i3 - 2;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(strReplace);
        if (iIndexOf >= 0) {
            if (runnable != null) {
                spannableStringBuilder.setSpan(new ClickableSpan() { 
                    @Override // android.text.style.ClickableSpan, android.text.style.CharacterStyle
                    public void updateDrawState(TextPaint textPaint) {
                        super.updateDrawState(textPaint);
                        textPaint.setUnderlineText(false);
                        textPaint.setTypeface(AndroidUtilities.bold());
                        textPaint.setColor(i);
                    }

                    @Override // android.text.style.ClickableSpan
                    public void onClick(View view) {
                        Runnable runnable2 = runnable;
                        if (runnable2 != null) {
                            runnable2.run();
                        }
                    }
                }, iIndexOf, i2 + iIndexOf, 0);
                return spannableStringBuilder;
            }
            spannableStringBuilder.setSpan(new CharacterStyle() { 
                @Override // android.text.style.CharacterStyle
                public void updateDrawState(TextPaint textPaint) {
                    textPaint.setUnderlineText(false);
                    textPaint.setTypeface(AndroidUtilities.bold());
                    textPaint.setColor(i);
                }
            }, iIndexOf, i2 + iIndexOf, 0);
        }
        return spannableStringBuilder;
    }

    public static CharSequence replaceArrows(CharSequence charSequence, boolean z) {
        return replaceArrows(charSequence, z, dp(2.6666667f), 0.0f, 1.0f);
    }

    public static CharSequence replaceArrows(CharSequence charSequence, boolean z, float f, float f2) {
        return replaceArrows(charSequence, z, f, f2, 1.0f);
    }

    public static CharSequence replaceArrows(CharSequence charSequence, boolean z, float f, float f2, float f3) {
        return replaceArrows(charSequence, z, f, f2, f3, R.drawable.msg_mini_forumarrow);
    }

    public static CharSequence replaceArrows(CharSequence charSequence, boolean z, float f, float f2, float f3, int i) {
        ColoredImageSpan coloredImageSpan = new ColoredImageSpan(i, 0);
        float f4 = f3 * 0.88f;
        coloredImageSpan.setScale(f4, f4);
        coloredImageSpan.translate(-f, f2);
        coloredImageSpan.spaceScaleX = 0.8f;
        if (z) {
            coloredImageSpan.useLinkPaintColor = z;
        }
        SpannableString spannableString = new SpannableString(" >");
        spannableString.setSpan(coloredImageSpan, spannableString.length() - 1, spannableString.length(), 33);
        CharSequence charSequenceReplaceMultipleCharSequence = replaceMultipleCharSequence(" >", charSequence, spannableString);
        SpannableString spannableString2 = new SpannableString(">");
        spannableString2.setSpan(coloredImageSpan, 0, 1, 33);
        CharSequence charSequenceReplaceMultipleCharSequence2 = replaceMultipleCharSequence(">", charSequenceReplaceMultipleCharSequence, spannableString2);
        ColoredImageSpan coloredImageSpan2 = new ColoredImageSpan(i, 0);
        coloredImageSpan2.setScale(f4, f4);
        coloredImageSpan2.translate(f, f2);
        coloredImageSpan2.rotate(180.0f);
        coloredImageSpan2.spaceScaleX = 0.8f;
        if (z) {
            coloredImageSpan2.useLinkPaintColor = z;
        }
        SpannableString spannableString3 = new SpannableString("<");
        spannableString3.setSpan(coloredImageSpan2, 0, 1, 33);
        return replaceMultipleCharSequence("<", charSequenceReplaceMultipleCharSequence2, spannableString3);
    }

    public static void recycleBitmaps(List<Bitmap> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        final ArrayList arrayList = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            Bitmap bitmap = list.get(i);
            if (bitmap != null && !bitmap.isRecycled()) {
                arrayList.add(new WeakReference(bitmap));
            }
        }
        runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                Utilities.globalQueue.postRunnable(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        AndroidUtilities.$r8$lambda$zrWNCtpqYtQ17xrk0FlGhXkHpvI(arrayList);
                    }
                });
            }
        }, 36L);
    }

    public static void $r8$lambda$xUpIG0xWvQrEWNwHNYj7d5nwyYU(Intent intent) {
        try {
            int i = UserConfig.selectedAccount;
            ApplicationLoader.postInitApplication();
            if (!needShowPasscode() && !SharedConfig.isWaitingForPasscodeEnter) {
                String stringExtra = intent.getStringExtra("android.intent.extra.TEXT");
                if (TextUtils.isEmpty(stringExtra)) {
                    return;
                }
                String stringExtra2 = intent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_URI");
                long j = Long.parseLong(intent.getStringExtra("com.google.android.voicesearch.extra.RECIPIENT_CONTACT_CHAT_ID"));
                TLRPC.User user = MessagesController.getInstance(i).getUser(Long.valueOf(j));
                if (user == null && (user = MessagesStorage.getInstance(i).getUserSync(j)) != null) {
                    MessagesController.getInstance(i).putUser(user, true);
                }
                if (user != null) {
                    ContactsController.getInstance(i).markAsContacted(stringExtra2);
                    SendMessagesHelper.getInstance(i).sendMessage(SendMessagesHelper.SendMessageParams.of(stringExtra, user.id, null, null, null, true, null, null, null, true, 0, 0, null, false));
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static void recycleBitmap(Bitmap bitmap) {
        recycleBitmaps(Collections.singletonList(bitmap));
    }

    public static boolean findClickableView(ViewGroup viewGroup, float f, float f2) {
        return findClickableView(viewGroup, f, f2, null);
    }

    void m4077$r8$lambda$U1mjz0c6pNXEGrZtp2rsOmuR0(boolean[] zArr, CountDownLatch countDownLatch, int i) {
        zArr[0] = i == 0;
        countDownLatch.countDown();
    }

    public static float[] getCoordinateInParent(ViewGroup viewGroup, View view) {
        float y;
        float f = 0.0f;
        if (view != null && viewGroup != null) {
            y = 0.0f;
            float x = 0.0f;
            while (true) {
                if (view == viewGroup) {
                    f = x;
                    break;
                }
                if (view != null) {
                    y += view.getY();
                    x += view.getX();
                    if (view instanceof NestedScrollView) {
                        y -= view.getScrollY();
                        x -= view.getScrollX();
                    }
                    if (view.getParent() instanceof View) {
                        view = (View) view.getParent();
                    }
                }
                y = 0.0f;
                break;
            }
        }
        y = 0.0f;
        break;
        return new float[]{f, y};
    }

    public static void doOnLayout(final View view, final Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (view == null) {
            runnable.run();
        } else {
            view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { 
                @Override // android.view.View.OnLayoutChangeListener
                public void onLayoutChange(View view2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                    view.removeOnLayoutChangeListener(this);
                    runnable.run();
                }
            });
        }
    }

    public static String readRes(int i) {
        return readRes(null, i);
    }

    public static String readRes(File file) {
        return readRes(file, 0);
    }

    public static String readRes(File file, int i) {
        InputStream inputStreamOpenRawResource;
        byte[] bArr = readBufferLocal.get();
        if (bArr == null) {
            bArr = new byte[65536];
            readBufferLocal.set(bArr);
        }
        try {
            if (file != null) {
                inputStreamOpenRawResource = new FileInputStream(file);
            } else {
                inputStreamOpenRawResource = ApplicationLoader.applicationContext.getResources().openRawResource(i);
            }
            try {
                byte[] bArr2 = bufferLocal.get();
                if (bArr2 == null) {
                    bArr2 = new byte[4096];
                    bufferLocal.set(bArr2);
                }
                int i2 = 0;
                while (true) {
                    int i3 = inputStreamOpenRawResource.read(bArr2, 0, bArr2.length);
                    if (i3 >= 0) {
                        int i4 = i2 + i3;
                        if (bArr.length < i4) {
                            byte[] bArr3 = new byte[bArr.length * 2];
                            System.arraycopy(bArr, 0, bArr3, 0, i2);
                            readBufferLocal.set(bArr3);
                            bArr = bArr3;
                        }
                        if (i3 > 0) {
                            System.arraycopy(bArr2, 0, bArr, i2, i3);
                            i2 = i4;
                        }
                    } else {
                        try {
                            break;
                        } catch (Throwable unused) {
                        }
                    }
                }
                inputStreamOpenRawResource.close();
                return new String(bArr, 0, i2);
            } catch (Throwable unused2) {
                if (inputStreamOpenRawResource != null) {
                    try {
                        inputStreamOpenRawResource.close();
                    } catch (Throwable unused3) {
                    }
                }
                return null;
            }
        } catch (Throwable unused4) {
            inputStreamOpenRawResource = null;
        }
    }

    public static Bitmap getBitmapFromRaw(int i) {
        InputStream inputStreamOpenRawResource;
        Bitmap bitmapDecodeStream = null;
        try {
            inputStreamOpenRawResource = ApplicationLoader.applicationContext.getResources().openRawResource(i);
            try {
                bitmapDecodeStream = BitmapFactory.decodeStream(inputStreamOpenRawResource);
            } catch (Throwable th) {
                th = th;
                try {
                    FileLog.e(th);
                } finally {
                    try {
                        inputStreamOpenRawResource.close();
                    } catch (IOException unused) {
                    }
                }
            }
        } catch (Throwable th2) {
            th = th2;
            inputStreamOpenRawResource = null;
        }
        return bitmapDecodeStream;
    }

    public static class LinkSpec {
        int end;
        int start;
        String url;

        private LinkSpec() {
        }
    }

    private static String makeUrl(String str, String[] strArr) {
        boolean z;
        int length = strArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                z = false;
                break;
            }
            String str2 = strArr[i];
            String str3 = str;
            if (str3.regionMatches(true, 0, str2, 0, str2.length())) {
                z = true;
                if (!str3.regionMatches(false, 0, str2, 0, str2.length())) {
                    str = str2.concat(str3.substring(str2.length()));
                    break;
                }
                str = str3;
                break;
            }
            i++;
            str = str3;
        }
        if (z || strArr.length <= 0) {
            return str;
        }
        return strArr[0] + str;
    }

    private static String getPlainText(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        return charSequence.toString();
    }

    private static void gatherLinks(ArrayList<LinkSpec> arrayList, CharSequence charSequence, Pattern pattern, String[] strArr, Linkify.MatchFilter matchFilter, boolean z) {
        if (TextUtils.indexOf(charSequence, (char) 9472) >= 0) {
            charSequence = charSequence.toString().replace((char) 9472, ' ');
        }
        if (!TextUtils.isEmpty(charSequence) && TextUtils.lastIndexOf(charSequence, '_') == charSequence.length() - 1) {
            charSequence = charSequence.subSequence(0, charSequence.length() - 1).toString() + "a";
        }
        Matcher matcher = pattern.matcher(charSequence);
        while (matcher.find()) {
            int iStart = matcher.start();
            int iEnd = matcher.end();
            if (matchFilter == null || matchFilter.acceptMatch(charSequence, iStart, iEnd)) {
                LinkSpec linkSpec = new LinkSpec();
                String strMakeUrl = makeUrl(matcher.group(0), strArr);
                if (!z || Browser.isInternalUrl(strMakeUrl, true, null)) {
                    linkSpec.url = strMakeUrl;
                    linkSpec.start = iStart;
                    linkSpec.end = iEnd;
                    arrayList.add(linkSpec);
                }
            }
        }
    }

    public static Boolean $r8$lambda$CEcQZmOn3RwbTaUHT0SBERSJNjU(Utilities.Callback0Return callback0Return) {
        try {
            return (Boolean) callback0Return.run();
        } catch (Exception e) {
            FileLog.e(e);
            return Boolean.FALSE;
        }
    }

    @Deprecated
    public static boolean addLinks(Spannable spannable, int i, boolean z, boolean z2) {
        return addLinks(spannable, getPlainText(spannable), i, z, z2);
    }

    private static boolean addLinks(Spannable spannable, String str, int i, boolean z, boolean z2) {
        if (spannable == null || str == null || containsUnsupportedCharacters(str) || i == 0) {
            return false;
        }
        URLSpan[] uRLSpanArr = (URLSpan[]) spannable.getSpans(0, spannable.length(), URLSpan.class);
        for (int length = uRLSpanArr.length - 1; length >= 0; length--) {
            URLSpan uRLSpan = uRLSpanArr[length];
            if (!(uRLSpan instanceof URLSpanReplacement) || z2) {
                spannable.removeSpan(uRLSpan);
            }
        }
        ArrayList arrayList = new ArrayList();
        if (!z && (i & 4) != 0) {
            Linkify.addLinks(spannable, 4);
        }
        if ((i & 1) != 0) {
            gatherLinks(arrayList, str, LinkifyPort.WEB_URL, new String[]{"http://", "https://", "tg://", "tonsite://"}, sUrlMatchFilter, z);
        }
        pruneOverlaps(arrayList);
        if (arrayList.size() == 0) {
            return false;
        }
        int size = arrayList.size();
        for (int i2 = 0; i2 < size; i2++) {
            LinkSpec linkSpec = (LinkSpec) arrayList.get(i2);
            URLSpan[] uRLSpanArr2 = (URLSpan[]) spannable.getSpans(linkSpec.start, linkSpec.end, URLSpan.class);
            if (uRLSpanArr2 != null && uRLSpanArr2.length > 0) {
                for (URLSpan uRLSpan2 : uRLSpanArr2) {
                    spannable.removeSpan(uRLSpan2);
                    if (!(uRLSpan2 instanceof URLSpanReplacement) || z2) {
                        spannable.removeSpan(uRLSpan2);
                    }
                }
            }
            String strReplaceAll = linkSpec.url;
            if (strReplaceAll != null) {
                strReplaceAll = strReplaceAll.replaceAll("∕|⁄|%E2%81%84|%E2%88%95", "/");
            }
            if (!Browser.isTonsitePunycode(strReplaceAll)) {
                spannable.setSpan(new URLSpan(strReplaceAll), linkSpec.start, linkSpec.end, 33);
            }
        }
        return true;
    }

    private static void pruneOverlaps(ArrayList<LinkSpec> arrayList) {
        int i;
        int i2;
        Collections.sort(arrayList, new Comparator() { 
            @Override // java.util.Comparator
            public final int compare(Object obj, Object obj2) {
                return AndroidUtilities.m4068$r8$lambda$KhNaetepuZm8ZT4fOcBgykfoI((AndroidUtilities.LinkSpec) obj, (AndroidUtilities.LinkSpec) obj2);
            }
        });
        int size = arrayList.size();
        int i3 = 0;
        while (i3 < size - 1) {
            LinkSpec linkSpec = arrayList.get(i3);
            int i4 = i3 + 1;
            LinkSpec linkSpec2 = arrayList.get(i4);
            int i5 = linkSpec.start;
            int i6 = linkSpec2.start;
            if (i5 <= i6 && (i = linkSpec.end) > i6) {
                int i7 = linkSpec2.end;
                if (i7 > i && i - i5 <= i7 - i6) {
                    i2 = i - i5 < i7 - i6 ? i3 : -1;
                } else {
                    i2 = i4;
                }
                if (i2 != -1) {
                    arrayList.remove(i2);
                    size--;
                }
            }
            i3 = i4;
        }
    }

    public static void m4069$r8$lambda$0F4KjwNtH7eKBB5ZrPUMWlqPs(String str, BaseFragment baseFragment, AlertDialog alertDialog, int i) {
        try {
            baseFragment.getParentActivity().startActivityForResult(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=" + str)), MediaError.DetailedErrorCode.SEGMENT_UNKNOWN);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static int[] toIntArray(List<Integer> list) {
        int size = list.size();
        int[] iArr = new int[size];
        for (int i = 0; i < size; i++) {
            iArr[i] = list.get(i).intValue();
        }
        return iArr;
    }

    public static boolean isInternalUri(Uri uri) {
        return isInternalUri(uri, 0);
    }

    public static boolean isInternalUri(int i) {
        return isInternalUri(null, i);
    }

    private static boolean isInternalUri(Uri uri, int i) {
        String str;
        String str2;
        String canonicalPath;
        String str3;
        if (uri != null) {
            String path = uri.getPath();
            if (path == null) {
                return false;
            }
            if (path.matches(Pattern.quote(new File(ApplicationLoader.applicationContext.getCacheDir(), "voip_logs").getAbsolutePath()) + "/\\d+\\.log")) {
                return false;
            }
            int i2 = 0;
            str3 = path;
            while (str3.length() <= 4096) {
                try {
                    String str4 = Utilities.readlink(str3);
                    str2 = str3;
                    if (str4 != null && !str4.equals(str3)) {
                        i2++;
                        if (i2 >= 10) {
                            str2 = str3;
                            return true;
                        }
                        str2 = str3;
                        str3 = str4;
                    }
                } catch (Throwable unused) {
                    return true;
                }
            }
            return true;
        }
        int i3 = 0;
        str = _UrlKt.FRAGMENT_ENCODE_SET;
        while (str.length() <= 4096) {
            try {
                String str5 = Utilities.readlinkFd(i);
                str2 = str;
                if (str5 != null && !str5.equals(str)) {
                    i3++;
                    if (i3 >= 10) {
                        str2 = str;
                        return true;
                    }
                    str2 = str;
                    str = str5;
                }
            } catch (Throwable unused2) {
                return true;
            }
        }
        return true;
        try {
            str2 = str;
            str2 = str3;
            canonicalPath = new File(str2).getCanonicalPath();
        } catch (Exception unused3) {
            str2.replace("/./", "/");
            canonicalPath = str2;
        }
        if (canonicalPath.endsWith(".attheme")) {
            return false;
        }
        return canonicalPath.toLowerCase().contains("/data/data/" + ApplicationLoader.applicationContext.getPackageName());
    }

    @SuppressLint({"WrongConstant"})
    public static void lockOrientation(Activity activity) {
        if (activity == null || prevOrientation != -10) {
            return;
        }
        try {
            prevOrientation = activity.getRequestedOrientation();
            WindowManager windowManager = (WindowManager) activity.getSystemService("window");
            if (windowManager == null || windowManager.getDefaultDisplay() == null) {
                return;
            }
            int rotation = windowManager.getDefaultDisplay().getRotation();
            int i = activity.getResources().getConfiguration().orientation;
            if (rotation == 3) {
                if (i == 1) {
                    activity.setRequestedOrientation(1);
                    return;
                } else {
                    activity.setRequestedOrientation(8);
                    return;
                }
            }
            if (rotation == 1) {
                if (i == 1) {
                    activity.setRequestedOrientation(9);
                    return;
                } else {
                    activity.setRequestedOrientation(0);
                    return;
                }
            }
            if (rotation == 0) {
                if (i == 2) {
                    activity.setRequestedOrientation(0);
                    return;
                } else {
                    activity.setRequestedOrientation(1);
                    return;
                }
            }
            if (i == 2) {
                activity.setRequestedOrientation(8);
            } else {
                activity.setRequestedOrientation(9);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @SuppressLint({"WrongConstant"})
    public static void lockOrientation(Activity activity, int i) {
        if (activity == null) {
            return;
        }
        try {
            prevOrientation = activity.getRequestedOrientation();
            activity.setRequestedOrientation(i);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @SuppressLint({"WrongConstant"})
    public static void unlockOrientation(Activity activity) {
        if (activity == null) {
            return;
        }
        try {
            int i = prevOrientation;
            if (i != -10) {
                activity.setRequestedOrientation(i);
                prevOrientation = -10;
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static class VcardData {
        String name;
        ArrayList<String> phones;
        StringBuilder vcard;

        private VcardData() {
            this.phones = new ArrayList<>();
            this.vcard = new StringBuilder();
        }
    }

    public static class VcardItem {
        public int type;
        public ArrayList<String> vcardData = new ArrayList<>();
        public String fullData = _UrlKt.FRAGMENT_ENCODE_SET;
        public boolean checked = true;

        public String[] getRawValue() {
            byte[] bArrDecodeQuotedPrintable;
            int iIndexOf = this.fullData.indexOf(58);
            if (iIndexOf < 0) {
                return new String[0];
            }
            String strSubstring = this.fullData.substring(0, iIndexOf);
            String strSubstring2 = this.fullData.substring(iIndexOf + 1);
            String str = null;
            String str2 = "UTF-8";
            for (String str3 : strSubstring.split(";")) {
                String[] strArrSplit = str3.split("=");
                if (strArrSplit.length == 2) {
                    if (strArrSplit[0].equals("CHARSET")) {
                        str2 = strArrSplit[1];
                    } else if (strArrSplit[0].equals("ENCODING")) {
                        str = strArrSplit[1];
                    }
                }
            }
            String[] strArrSplit2 = strSubstring2.split(";");
            for (int i = 0; i < strArrSplit2.length; i++) {
                if (!TextUtils.isEmpty(strArrSplit2[i]) && str != null && str.equalsIgnoreCase("QUOTED-PRINTABLE") && (bArrDecodeQuotedPrintable = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(strArrSplit2[i]))) != null && bArrDecodeQuotedPrintable.length != 0) {
                    try {
                        strArrSplit2[i] = new String(bArrDecodeQuotedPrintable, str2);
                    } catch (Exception unused) {
                    }
                }
            }
            return strArrSplit2;
        }

        public String getValue(boolean z) {
            byte[] bArrDecodeQuotedPrintable;
            StringBuilder sb = new StringBuilder();
            int iIndexOf = this.fullData.indexOf(58);
            if (iIndexOf < 0) {
                return _UrlKt.FRAGMENT_ENCODE_SET;
            }
            if (sb.length() > 0) {
                sb.append(", ");
            }
            String strSubstring = this.fullData.substring(0, iIndexOf);
            String strSubstring2 = this.fullData.substring(iIndexOf + 1);
            String str = null;
            String str2 = "UTF-8";
            for (String str3 : strSubstring.split(";")) {
                String[] strArrSplit = str3.split("=");
                if (strArrSplit.length == 2) {
                    if (strArrSplit[0].equals("CHARSET")) {
                        str2 = strArrSplit[1];
                    } else if (strArrSplit[0].equals("ENCODING")) {
                        str = strArrSplit[1];
                    }
                }
            }
            String[] strArrSplit2 = strSubstring2.split(";");
            boolean z2 = false;
            for (int i = 0; i < strArrSplit2.length; i++) {
                if (!TextUtils.isEmpty(strArrSplit2[i])) {
                    if (str != null && str.equalsIgnoreCase("QUOTED-PRINTABLE") && (bArrDecodeQuotedPrintable = AndroidUtilities.decodeQuotedPrintable(AndroidUtilities.getStringBytes(strArrSplit2[i]))) != null && bArrDecodeQuotedPrintable.length != 0) {
                        try {
                            strArrSplit2[i] = new String(bArrDecodeQuotedPrintable, str2);
                        } catch (Exception unused) {
                        }
                    }
                    if (z2 && sb.length() > 0) {
                        sb.append(" ");
                    }
                    sb.append(strArrSplit2[i]);
                    if (!z2) {
                        z2 = strArrSplit2[i].length() > 0;
                    }
                }
            }
            if (z) {
                int i2 = this.type;
                if (i2 == 0) {
                    return PhoneFormat.getInstance().format(sb.toString());
                }
                if (i2 == 5) {
                    String[] strArrSplit3 = sb.toString().split("T");
                    if (strArrSplit3.length > 0) {
                        String[] strArrSplit4 = strArrSplit3[0].split("-");
                        if (strArrSplit4.length == 3) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(1, Utilities.parseInt((CharSequence) strArrSplit4[0]).intValue());
                            calendar.set(2, Utilities.parseInt((CharSequence) strArrSplit4[1]).intValue() - 1);
                            calendar.set(5, Utilities.parseInt((CharSequence) strArrSplit4[2]).intValue());
                            return LocaleController.getInstance().getFormatterYearMax().format(calendar.getTime());
                        }
                    }
                }
            }
            return sb.toString();
        }

        public String getRawType(boolean z) {
            int iIndexOf = this.fullData.indexOf(58);
            if (iIndexOf < 0) {
                return _UrlKt.FRAGMENT_ENCODE_SET;
            }
            String strSubstring = this.fullData.substring(0, iIndexOf);
            if (this.type == 20) {
                String[] strArrSplit = strSubstring.substring(2).split(";");
                if (z) {
                    return strArrSplit[0];
                }
                if (strArrSplit.length <= 1) {
                    return _UrlKt.FRAGMENT_ENCODE_SET;
                }
                return strArrSplit[strArrSplit.length - 1];
            }
            for (String str : strSubstring.split(";")) {
                if (str.indexOf(61) < 0) {
                    strSubstring = str;
                }
            }
            return strSubstring;
        }

        public String getType() {
            String strSubstring;
            int i = this.type;
            if (i == 4) {
                return LocaleController.getString(R.string.ContactNote);
            }
            if (i == 3) {
                return LocaleController.getString(R.string.ContactUrl);
            }
            if (i == 5) {
                return LocaleController.getString(R.string.ContactBirthday);
            }
            if (i == 6) {
                if ("ORG".equalsIgnoreCase(getRawType(true))) {
                    return LocaleController.getString(R.string.ContactJob);
                }
                return LocaleController.getString(R.string.ContactJobTitle);
            }
            int iIndexOf = this.fullData.indexOf(58);
            if (iIndexOf < 0) {
                return _UrlKt.FRAGMENT_ENCODE_SET;
            }
            String strSubstring2 = this.fullData.substring(0, iIndexOf);
            if (this.type == 20) {
                strSubstring = strSubstring2.substring(2).split(";")[0];
            } else {
                for (String str : strSubstring2.split(";")) {
                    if (str.indexOf(61) < 0) {
                        strSubstring2 = str;
                    }
                }
                strSubstring = strSubstring2.startsWith("X-") ? strSubstring2.substring(2) : strSubstring2;
                switch (strSubstring) {
                    case "MOBILE":
                    case "CELL":
                        strSubstring = LocaleController.getString(R.string.PhoneMobile);
                        break;
                    case "HOME":
                        strSubstring = LocaleController.getString(R.string.PhoneHome);
                        break;
                    case "PREF":
                        strSubstring = LocaleController.getString(R.string.PhoneMain);
                        break;
                    case "WORK":
                        strSubstring = LocaleController.getString(R.string.PhoneWork);
                        break;
                    case "OTHER":
                        strSubstring = LocaleController.getString(R.string.PhoneOther);
                        break;
                }
            }
            return strSubstring.substring(0, 1).toUpperCase() + strSubstring.substring(1).toLowerCase();
        }
    }

    public static byte[] getStringBytes(String str) {
        try {
            return str.getBytes(StandardCharsets.UTF_8);
        } catch (Exception unused) {
            return new byte[0];
        }
    }

    Typeface $r8$lambda$U1asOTL621l4z8g5RIfymaYl5pg(String str) {
        try {
            if (!ExteraConfig.getUseSystemFonts()) {
                return FontUtils.getFontFromAssets(str);
            }
            Typeface systemTypeface = FontUtils.getSystemTypeface(str);
            return systemTypeface != null ? systemTypeface : FontUtils.getFontFromAssets(str);
        } catch (Exception e) {
            FileLog.e("Could not get typeface '" + str + "' because " + e.getMessage());
            return null;
        }
    }

    public static void clearTypefaceCache() {
        ConcurrentHashMap<String, Typeface> concurrentHashMap = typefaceCache;
        synchronized (concurrentHashMap) {
            concurrentHashMap.clear();
            mediumTypeface = null;
        }
    }

    public static boolean isWaitingForSms() {
        boolean z;
        synchronized (smsLock) {
            z = waitingForSms;
        }
        return z;
    }

    public static void setWaitingForSms(boolean z) {
        synchronized (smsLock) {
            try {
                waitingForSms = z;
                if (z) {
                    try {
                        SmsRetriever.getClient(ApplicationLoader.applicationContext).startSmsRetriever().addOnSuccessListener(new OnSuccessListener() { 
                            @Override // com.google.android.gms.tasks.OnSuccessListener
                            public final void onSuccess(Object obj) {
                                AndroidUtilities.$r8$lambda$J4uGjwnOr5qIdWYhIX4Og12v1ts((Void) obj);
                            }
                        });
                    } catch (Throwable th) {
                        FileLog.e(th);
                    }
                }
            } catch (Throwable th2) {
                throw th2;
            }
        }
    }

    public static void $r8$lambda$114_tsQ3Yj3FepwwLiuAukKWoYc(View view, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        view.setTranslationX((float) (((double) (fFloatValue * 4.0f * (1.0f - fFloatValue))) * Math.sin(((double) fFloatValue) * 3.141592653589793d * 4.0d) * ((double) dp(4.0f))));
    }

    public static void shakeViewSpring(View view) {
        shakeViewSpring(view, 10.0f, null);
    }

    public static void shakeViewSpring(View view, float f) {
        shakeViewSpring(view, f, null);
    }

    public static void shakeViewSpring(View view, Runnable runnable) {
        shakeViewSpring(view, 10.0f, runnable);
    }

    public static void shakeViewSpring(final View view, float f, final Runnable runnable) {
        if (view == null) {
            return;
        }
        int iDp = dp(f);
        if (view.getTag(R.id.spring_tag) != null) {
            ((SpringAnimation) view.getTag(R.id.spring_tag)).cancel();
        }
        Float f2 = (Float) view.getTag(R.id.spring_was_translation_x_tag);
        if (f2 != null) {
            view.setTranslationX(f2.floatValue());
        }
        view.setTag(R.id.spring_was_translation_x_tag, Float.valueOf(view.getTranslationX()));
        final float translationX = view.getTranslationX();
        SpringAnimation springAnimationAddEndListener = new SpringAnimation(view, DynamicAnimation.TRANSLATION_X, translationX).setSpring(new SpringForce(translationX).setStiffness(600.0f)).setStartVelocity((-iDp) * 100).addEndListener(new DynamicAnimation.OnAnimationEndListener() { 
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z, float f3, float f4) {
                AndroidUtilities.$r8$lambda$qgAShxesFu3Jqe_DzjHU5D6poXE(runnable, view, translationX, dynamicAnimation, z, f3, f4);
            }
        });
        view.setTag(R.id.spring_tag, springAnimationAddEndListener);
        springAnimationAddEndListener.start();
    }

    public static public static boolean openForView(File file, String str, String str2, Activity activity, Theme.ResourcesProvider resourcesProvider, boolean z) {
        if (file == null || !file.exists()) {
            return false;
        }
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setFlags(1);
        MimeTypeMap singleton = MimeTypeMap.getSingleton();
        int iLastIndexOf = str == null ? -1 : str.lastIndexOf(46);
        if (iLastIndexOf != -1) {
            String strSubstring = str.substring(iLastIndexOf + 1);
            if (z && MessageObject.isV(strSubstring)) {
                return true;
            }
            String mimeTypeFromExtension = singleton.getMimeTypeFromExtension(strSubstring.toLowerCase());
            if (mimeTypeFromExtension != null) {
                str2 = mimeTypeFromExtension;
            } else if (str2 == null || str2.length() == 0) {
                str2 = null;
            }
        } else {
            str2 = null;
        }
        if (str2 != null && str2.equals("application/vnd.android.package-archive")) {
            if (z) {
                return true;
            }
            if (Build.VERSION.SDK_INT >= 26 && !ApplicationLoader.applicationContext.getPackageManager().canRequestPackageInstalls()) {
                AlertsCreator.createApkRestrictedDialog(activity, resourcesProvider).show();
                return true;
            }
        }
        intent.setDataAndType(FileProvider.getUriForFile(activity, ApplicationLoader.getApplicationId() + ".provider", file), str2 != null ? str2 : "text/plain");
        if (str2 != null) {
            try {
                activity.startActivityForResult(intent, MediaError.DetailedErrorCode.SEGMENT_UNKNOWN);
            } catch (Exception unused) {
                intent.setDataAndType(FileProvider.getUriForFile(activity, ApplicationLoader.getApplicationId() + ".provider", file), "text/plain");
                activity.startActivityForResult(intent, MediaError.DetailedErrorCode.SEGMENT_UNKNOWN);
            }
        } else {
            activity.startActivityForResult(intent, MediaError.DetailedErrorCode.SEGMENT_UNKNOWN);
        }
        return true;
    }

    public static boolean openForView(MessageObject messageObject, Activity activity, Theme.ResourcesProvider resourcesProvider, boolean z) {
        String str = messageObject.messageOwner.attachPath;
        File file = (str == null || str.length() == 0) ? null : new File(messageObject.messageOwner.attachPath);
        if (file == null || !file.exists()) {
            file = FileLoader.getInstance(messageObject.currentAccount).getPathToMessage(messageObject.messageOwner);
        }
        File file2 = file;
        int i = messageObject.type;
        return openForView(file2, messageObject.getFileName(), (i == 9 || i == 0) ? messageObject.getMimeType() : null, activity, resourcesProvider, z);
    }

    public static boolean openForView(TLRPC.Document document, Activity activity) {
        return openForView(FileLoader.getInstance(UserConfig.selectedAccount).getPathToAttach(document, true), FileLoader.getAttachFileName(document), document.mime_type, activity, null, false);
    }

    public static String $r8$lambda$tZkoabMKM1212nONUi1T0va_1wc(Integer num) {
        return "%" + (num.intValue() + 1) + "$s";
    }

    public static SpannableStringBuilder formatSpannable(CharSequence charSequence, GenericProvider<Integer, String> genericProvider, CharSequence... charSequenceArr) {
        String string = charSequence.toString();
        SpannableStringBuilder spannableStringBuilderValueOf = SpannableStringBuilder.valueOf(charSequence);
        for (int i = 0; i < charSequenceArr.length; i++) {
            String strProvide = genericProvider.provide(Integer.valueOf(i));
            int iIndexOf = string.indexOf(strProvide);
            if (iIndexOf != -1) {
                spannableStringBuilderValueOf.replace(iIndexOf, strProvide.length() + iIndexOf, charSequenceArr[i]);
                string = string.substring(0, iIndexOf) + charSequenceArr[i].toString() + string.substring(iIndexOf + strProvide.length());
            }
        }
        return spannableStringBuilderValueOf;
    }

    public static CharSequence replaceTwoNewLinesToOne(CharSequence charSequence) {
        char[] cArr = new char[2];
        if (charSequence instanceof StringBuilder) {
            StringBuilder sbReplace = (StringBuilder) charSequence;
            int length = charSequence.length();
            int i = 0;
            while (i < length - 2) {
                int i2 = i + 2;
                sbReplace.getChars(i, i2, cArr, 0);
                if (cArr[0] == '\n' && cArr[1] == '\n') {
                    sbReplace = sbReplace.replace(i, i2, "\n");
                    i--;
                    length--;
                }
                i++;
            }
            return charSequence;
        }
        if (charSequence instanceof SpannableStringBuilder) {
            SpannableStringBuilder spannableStringBuilderReplace = (SpannableStringBuilder) charSequence;
            int length2 = charSequence.length();
            int i3 = 0;
            while (i3 < length2 - 2) {
                int i4 = i3 + 2;
                spannableStringBuilderReplace.getChars(i3, i4, cArr, 0);
                if (cArr[0] == '\n' && cArr[1] == '\n') {
                    spannableStringBuilderReplace = spannableStringBuilderReplace.replace(i3, i4, (CharSequence) "\n");
                    i3--;
                    length2--;
                }
                i3++;
            }
            return charSequence;
        }
        if (charSequence instanceof SpannableString) {
            if (TextUtils.indexOf(charSequence, "\n\n") < 0) {
                return charSequence;
            }
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
            int length3 = charSequence.length();
            int i5 = 0;
            while (i5 < length3 - 2) {
                int i6 = i5 + 2;
                spannableStringBuilder.getChars(i5, i6, cArr, 0);
                if (cArr[0] == '\n' && cArr[1] == '\n') {
                    spannableStringBuilder = spannableStringBuilder.replace(i5, i6, (CharSequence) "\n");
                    i5--;
                    length3--;
                }
                i5++;
            }
            return spannableStringBuilder;
        }
        return charSequence.toString().replace("\n\n", "\n");
    }

    public static CharSequence replaceNewLines(CharSequence charSequence) {
        int i = 0;
        if (charSequence instanceof StringBuilder) {
            StringBuilder sb = (StringBuilder) charSequence;
            int length = charSequence.length();
            while (i < length) {
                if (charSequence.charAt(i) == '\n') {
                    sb.setCharAt(i, ' ');
                }
                i++;
            }
            return charSequence;
        }
        if (charSequence instanceof SpannableStringBuilder) {
            SpannableStringBuilder spannableStringBuilder = (SpannableStringBuilder) charSequence;
            int length2 = charSequence.length();
            while (i < length2) {
                if (charSequence.charAt(i) == '\n') {
                    spannableStringBuilder.replace(i, i + 1, (CharSequence) " ");
                }
                i++;
            }
            return spannableStringBuilder;
        }
        if (!(charSequence instanceof SpannableString)) {
            if (charSequence == null) {
                return null;
            }
            return charSequence.toString().replace('\n', ' ');
        }
        if (TextUtils.indexOf(charSequence, '\n') < 0) {
            return charSequence;
        }
        SpannableStringBuilder spannableStringBuilder2 = new SpannableStringBuilder(charSequence);
        int length3 = charSequence.length();
        while (i < length3) {
            if (charSequence.charAt(i) == '\n') {
                spannableStringBuilder2.replace(i, i + 1, (CharSequence) " ");
            }
            i++;
        }
        return spannableStringBuilder2;
    }

    void $r8$lambda$Un_lfiA_8Kna1apqsY3goAMo8pY(WeakReference weakReference, String str) {
        ButtonSpan.TextViewButtons textViewButtons = (ButtonSpan.TextViewButtons) weakReference.get();
        if (textViewButtons == null) {
            return;
        }
        if (TextUtils.isEmpty(str)) {
            str = LocaleController.getString(R.string.Unknown);
        }
        textViewButtons.setText(str);
    }

    public static void m4073$r8$lambda$KAzEYEDaMH0r8TgZzhW2mRChrk(boolean[] zArr, final ButtonSpan.TextViewButtons[] textViewButtonsArr, String str, String str2, String str3, String str4, String str5) {
        if (zArr[0]) {
            return;
        }
        zArr[0] = true;
        textViewButtonsArr[0].setText(LocaleController.getString(R.string.ProxyBottomSheetChecking) + "...");
        textViewButtonsArr[0].clear();
        try {
            ConnectionsManager.getInstance(UserConfig.selectedAccount).checkProxy(str, Integer.parseInt(str2), str3, str4, str5, new RequestTimeDelegate() { 
                @Override // org.telegram.tgnet.RequestTimeDelegate
                public final void run(long j) {
                    AndroidUtilities.runOnUIThread(new Runnable() { 
                        @Override // java.lang.Runnable
                        public final void run() {
                            AndroidUtilities.$r8$lambda$IIjZ836PhDCfSTbVEHxs4p2jVug(j, textViewButtonsArr);
                        }
                    });
                }
            });
        } catch (NumberFormatException unused) {
            textViewButtonsArr[0].setText(LocaleController.getString(R.string.Unavailable));
            textViewButtonsArr[0].setTextColor(Theme.getColor(Theme.key_text_RedRegular));
        }
    }

    public static void $r8$lambda$_VMM147bROfktHG8u4U1NzwtHHA(IntColorCallback intColorCallback, Window window, ValueAnimator valueAnimator) {
        int iIntValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        if (intColorCallback != null) {
            intColorCallback.run(iIntValue);
        }
        try {
            window.setNavigationBarColor(iIntValue);
        } catch (Exception unused) {
        }
    }

    public static boolean checkHostForPunycode(String str) {
        boolean z;
        boolean z2;
        if (str == null) {
            return false;
        }
        try {
            int length = str.length();
            z = false;
            z2 = false;
            for (int i = 0; i < length; i++) {
                try {
                    char cCharAt = str.charAt(i);
                    if (cCharAt != '.' && cCharAt != '-' && cCharAt != '/' && cCharAt != '+' && (cCharAt < '0' || cCharAt > '9')) {
                        if ((cCharAt < 'a' || cCharAt > 'z') && (cCharAt < 'A' || cCharAt > 'Z')) {
                            z2 = true;
                        } else {
                            z = true;
                        }
                        if (z && z2) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e = e;
                    FileLog.e(e);
                }
            }
        } catch (Exception e2) {
            e = e2;
            z = false;
            z2 = false;
        }
        return z && z2;
    }

    public static boolean shouldShowUrlInAlert(String str) {
        try {
            return checkHostForPunycode(Uri.parse(str).getHost());
        } catch (Exception e) {
            FileLog.e(e);
            return false;
        }
    }

    public static void scrollToFragmentRow(INavigationLayout iNavigationLayout, final String str) {
        if (iNavigationLayout == null || str == null) {
            return;
        }
        final BaseFragment baseFragment = iNavigationLayout.getFragmentStack().get(iNavigationLayout.getFragmentStack().size() - 1);
        try {
            Field declaredField = baseFragment.getClass().getDeclaredField("listView");
            declaredField.setAccessible(true);
            final RecyclerListView recyclerListView = (RecyclerListView) declaredField.get(baseFragment);
            recyclerListView.highlightRow(new RecyclerListView.IntReturnCallback() { 
                @Override // org.telegram.ui.Components.RecyclerListView.IntReturnCallback
                public final int run() {
                    return AndroidUtilities.$r8$lambda$QzwQ09NlPLTme8f5WjwZ82sY_uo(baseFragment, str, recyclerListView);
                }
            });
            declaredField.setAccessible(false);
        } catch (Throwable unused) {
        }
    }

    public static void $r8$lambda$Sr4WUKQaqOOGUhXSOVowHnvGzhc(ImageView imageView, AtomicBoolean atomicBoolean, Drawable drawable, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float fAbs = Math.abs(fFloatValue - 0.5f) + 0.5f;
        imageView.setScaleX(fAbs);
        imageView.setScaleY(fAbs);
        if (fFloatValue < 0.5f || atomicBoolean.get()) {
            return;
        }
        atomicBoolean.set(true);
        imageView.setImageDrawable(drawable);
    }

    public static void updateViewVisibilityAnimated(View view, boolean z) {
        updateViewVisibilityAnimated(view, z, 1.0f, true, true);
    }

    public static void updateViewVisibilityAnimated(View view, boolean z, float f, boolean z2) {
        updateViewVisibilityAnimated(view, z, f, true, z2);
    }

    public static void updateViewVisibilityAnimated(View view, boolean z, float f, boolean z2, boolean z3) {
        updateViewVisibilityAnimated(view, z, f, z2, 1.0f, z3, null);
    }

    public static void updateViewVisibilityAnimated(View view, boolean z, float f, boolean z2, float f2, boolean z3, ValueAnimator.AnimatorUpdateListener animatorUpdateListener) {
        Integer num = 1;
        if (view == null) {
            return;
        }
        int i = 0;
        if (view.getParent() == null) {
            z3 = false;
        }
        if (!z3) {
            view.animate().setListener(null).cancel();
            if (!z) {
                i = z2 ? 8 : 4;
            }
            view.setVisibility(i);
            view.setTag(z ? 1 : null);
            view.setAlpha(f2);
            view.setScaleX(1.0f);
            view.setScaleY(1.0f);
            return;
        }
        if (z && view.getTag() == null) {
            view.animate().setListener(null).cancel();
            if (view.getVisibility() != 0) {
                view.setVisibility(0);
                view.setAlpha(0.0f);
                view.setScaleX(f);
                view.setScaleY(f);
            }
            view.animate().alpha(f2).scaleY(1.0f).scaleX(1.0f).setDuration(150L).setUpdateListener(animatorUpdateListener).start();
            view.setTag(num);
            return;
        }
        if (z || view.getTag() == null) {
            return;
        }
        view.animate().setListener(null).cancel();
        view.animate().alpha(0.0f).scaleY(f).scaleX(f).setListener(new HideViewAfterAnimation(view, z2)).setDuration(150L).setUpdateListener(animatorUpdateListener).start();
        view.setTag(null);
    }

    public static void updateViewShow(View view, boolean z) {
        updateViewShow(view, z, true, true);
    }

    public static void updateViewShow(View view, boolean z, boolean z2, boolean z3) {
        updateViewShow(view, z, z2, 0.0f, z3, null);
    }

    public static void updateViewShow(View view, boolean z, boolean z2, boolean z3, Runnable runnable) {
        updateViewShow(view, z, z2, 0.0f, z3, runnable);
    }

    public static void updateViewShow(View view, boolean z, boolean z2, float f, boolean z3, Runnable runnable) {
        if (view == null) {
            return;
        }
        if (view.getParent() == null) {
            z3 = false;
        }
        view.animate().setListener(null).cancel();
        if (!z3) {
            view.setVisibility(z ? 0 : 8);
            view.setTag(z ? 1 : null);
            view.setAlpha(1.0f);
            view.setScaleX((!z2 || z) ? 1.0f : 0.5f);
            view.setScaleY((!z2 || z) ? 1.0f : 0.5f);
            if (f != 0.0f) {
                view.setTranslationY(z ? 0.0f : dp(-16.0f) * f);
            }
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        if (z) {
            if (view.getVisibility() != 0) {
                view.setVisibility(0);
                view.setAlpha(0.0f);
                view.setScaleX(z2 ? 0.5f : 1.0f);
                view.setScaleY(z2 ? 0.5f : 1.0f);
                if (f != 0.0f) {
                    view.setTranslationY(dp(-16.0f) * f);
                }
            }
            ViewPropertyAnimator viewPropertyAnimatorWithEndAction = view.animate().alpha(1.0f).scaleY(1.0f).scaleX(1.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(340L).withEndAction(runnable);
            if (f != 0.0f) {
                viewPropertyAnimatorWithEndAction.translationY(0.0f);
            }
            viewPropertyAnimatorWithEndAction.start();
            return;
        }
        ViewPropertyAnimator viewPropertyAnimatorWithEndAction2 = view.animate().alpha(0.0f).scaleY(z2 ? 0.5f : 1.0f).scaleX(z2 ? 0.5f : 1.0f).setListener(new HideViewAfterAnimation(view)).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(340L).withEndAction(runnable);
        if (f != 0.0f) {
            viewPropertyAnimatorWithEndAction2.translationY(dp(-16.0f) * f);
        }
        viewPropertyAnimatorWithEndAction2.start();
    }

    public static long getPrefIntOrLong(SharedPreferences sharedPreferences, String str, long j) {
        try {
            return sharedPreferences.getLong(str, j);
        } catch (Exception unused) {
            return sharedPreferences.getInt(str, (int) j);
        }
    }

    void m4078$r8$lambda$w8KvFFT73lKIckzNxkPpw0ffM(RecyclerView recyclerView) {
        if (recyclerView.getAdapter() != null) {
            recyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public static void doOnPreDraw(View view, final Runnable runnable) {
        final ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        final boolean[] zArr = new boolean[1];
        final ViewTreeObserver.OnPreDrawListener[] onPreDrawListenerArr = {onPreDrawListener};
        ViewTreeObserver.OnPreDrawListener onPreDrawListener = new ViewTreeObserver.OnPreDrawListener() { 
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public final boolean onPreDraw() {
                return AndroidUtilities.m4070$r8$lambda$2DgNRwQUSz8aGxkiCxJbTaM93E(viewTreeObserver, onPreDrawListenerArr, zArr, runnable);
            }
        };
        viewTreeObserver.addOnPreDrawListener(onPreDrawListener);
    }

    public static /* synthetic */ boolean m4070$r8$lambda$2DgNRwQUSz8aGxkiCxJbTaM93E(ViewTreeObserver viewTreeObserver, ViewTreeObserver.OnPreDrawListener[] onPreDrawListenerArr, boolean[] zArr, Runnable runnable) {
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.removeOnPreDrawListener(onPreDrawListenerArr[0]);
        }
        if (!zArr[0]) {
            zArr[0] = true;
            runnable.run();
        }
        return true;
    }

    public static boolean isInAirplaneMode(Context context) {
        try {
            return Settings.Global.getInt(context.getContentResolver(), "airplane_mode_on", 0) != 0;
        } catch (Exception unused) {
        }
    }

    public static boolean isWifiEnabled(Context context) {
        try {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService("wifi");
            return wifiManager != null && wifiManager.isWifiEnabled();
        } catch (Exception unused) {
        }
    }

    public static boolean gzip(File file, File file2) {
        try {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            try {
                GZIPOutputStream gZIPOutputStream = new GZIPOutputStream(new BufferedOutputStream(new FileOutputStream(file2)));
                try {
                    byte[] bArr = new byte[8192];
                    while (true) {
                        int i = bufferedInputStream.read(bArr);
                        if (i != -1) {
                            gZIPOutputStream.write(bArr, 0, i);
                        } else {
                            gZIPOutputStream.close();
                            bufferedInputStream.close();
                            return true;
                        }
                        try {
                            bufferedInputStream.close();
                        } catch (Throwable th) {
                            th.addSuppressed(th);
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    try {
                        gZIPOutputStream.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                    throw th2;
                }
            } catch (Throwable th4) {
                bufferedInputStream.close();
                throw th4;
            }
        } catch (FileNotFoundException e) {
            FileLog.e(e);
            return false;
        } catch (IOException e2) {
            FileLog.e(e2);
            return false;
        }
    }

    public static String getBuildVersionInfo() {
        String str;
        try {
            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            int i = packageInfo.versionCode;
            int i2 = i / 10;
            int i3 = i % 10;
            if (i3 == 1 || i3 == 2) {
                str = "store bundled " + Build.CPU_ABI + " " + Build.CPU_ABI2;
            } else {
                str = "universal " + Build.CPU_ABI + " " + Build.CPU_ABI2;
            }
            return LocaleController.formatString("TelegramVersion", R.string.TelegramVersion, String.format(Locale.US, "v%s (%d) %s", packageInfo.versionName, Integer.valueOf(i2), str));
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static void printStackTrace(String str) {
        if (BuildVars.DEBUG_PRIVATE_VERSION) {
            String str2 = "[" + str + "]";
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            int iMin = Math.min(stackTrace.length, 14);
            for (int i = 3; i < iMin; i++) {
                FileLog.d(str2 + " " + stackTrace[i]);
            }
            FileLog.d(str2);
        }
    }

    public static void printLayoutRequestedChain(View view) {
        if (view == null) {
            FileLog.d("LayoutCheck view == null");
            return;
        }
        int i = 0;
        while (view != null) {
            Object parent = view.getParent();
            FileLog.d("LayoutCheck level=" + i + ", view=" + view.getClass().getSimpleName() + "@" + Integer.toHexString(System.identityHashCode(view)) + ", isLayoutRequested=" + view.isLayoutRequested());
            if (!(parent instanceof View)) {
                if (parent == null) {
                    break;
                }
                FileLog.d("LayoutCheck level=" + (i + 1) + ", parent=" + parent.getClass().getSimpleName() + " (not a View)");
                break;
            }
            view = (View) parent;
            i++;
        }
        FileLog.d("LayoutCheck");
    }

    public static <T> T randomOf(ArrayList<T> arrayList) {
        if (arrayList.isEmpty()) {
            return null;
        }
        return arrayList.get(Math.abs(Utilities.fastRandom.nextInt() % arrayList.size()));
    }

    public static float getNavigationBarThirdButtonsFactor(int i) {
        return Utilities.clamp01((i - dp(32.0f)) / dp(16.0f));
    }

    public static float getNavigationBarThirdButtonsFactor(float f, float f2, int i) {
        return lerp(f, f2, getNavigationBarThirdButtonsFactor(i));
    }

    public static void drawNavigationBarProtection(Canvas canvas, View view, int i, int i2) {
        drawNavigationBarProtection(canvas, view, i, i2, 1.0f);
    }

    public static void drawNavigationBarProtection(Canvas canvas, View view, int i, int i2, float f) {
        Paint paint = navbarProtactionPaint;
        paint.setColor(Theme.multAlpha(i, f * getNavigationBarThirdButtonsFactor(0.0f, 0.75f, i2)));
        canvas.drawRect(0.0f, (view.getY() + view.getMeasuredHeight()) - i2, view.getMeasuredWidth(), view.getY() + view.getMeasuredHeight(), paint);
    }

    public static WindowInsets fixedDispatchApplyWindowInsets(WindowInsets windowInsets, ViewGroup viewGroup) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            viewGroup.getChildAt(i).dispatchApplyWindowInsets(windowInsets);
        }
        return windowInsets;
    }

    public static void enableEdgeToEdge(Activity activity) {
        Window window = activity.getWindow();
        try {
            enableEdgeToEdge(window);
            if (Build.VERSION.SDK_INT >= 28) {
                window.setNavigationBarDividerColor(0);
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public static void enableEdgeToEdge(Window window) {
        Objects.requireNonNull(window);
        window.getDecorView();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        window.setStatusBarColor(0);
        window.setNavigationBarColor(0);
        int i = Build.VERSION.SDK_INT;
        if (i >= 28) {
            int i2 = i >= 30 ? 3 : 1;
            WindowManager.LayoutParams attributes = window.getAttributes();
            if (attributes.layoutInDisplayCutoutMode != i2) {
                attributes.layoutInDisplayCutoutMode = i2;
                window.setAttributes(attributes);
            }
        }
        if (i >= 29) {
            window.setStatusBarContrastEnforced(false);
            window.setNavigationBarContrastEnforced(false);
        }
    }

    public static void drawStroke(Canvas canvas, RectF rectF, float f) {
        drawStroke(canvas, rectF, f, 1.0f);
    }

    public static void drawStroke(Canvas canvas, RectF rectF, float f, float f2) {
        if (f2 <= 0.0f) {
            return;
        }
        float fMin = Math.min(f, Math.min(rectF.width(), rectF.height()) / 2.0f);
        float fDpf2 = dpf2(1.0f);
        float f3 = fDpf2 / 2.0f;
        float fMax = Math.max(0.0f, fMin - fDpf2);
        Paint paint = strokeTop;
        Paint.Style style = Paint.Style.STROKE;
        paint.setStyle(style);
        paint.setStrokeWidth(fDpf2);
        paint.setColor(Theme.multAlpha(570425343, f2));
        canvas.save();
        float f4 = rectF.left - f3;
        float f5 = rectF.top;
        if (canvas.clipRect(f4, f5, rectF.right + f3, MathUtils.clamp(f5 + fMax, f5, rectF.bottom))) {
            canvas.drawRoundRect(rectF.left, rectF.top + f3, rectF.right, f3 + rectF.bottom, fMax, fMax, paint);
        }
        canvas.restore();
        float fDpf3 = dpf2(0.6666667f);
        float f6 = fDpf3 / 2.0f;
        float fMax2 = Math.max(0.0f, fMin - fDpf3);
        Paint paint2 = strokeBottom;
        paint2.setStyle(style);
        paint2.setStrokeWidth(fDpf3);
        paint2.setColor(Theme.multAlpha(385875967, f2));
        canvas.save();
        float f7 = rectF.left - f6;
        float f8 = rectF.bottom;
        if (canvas.clipRect(f7, MathUtils.clamp(f8 - fMax2, rectF.top, f8), rectF.right + f6, rectF.bottom)) {
            canvas.drawRoundRect(rectF.left, rectF.top - f6, rectF.right, rectF.bottom - f6, fMax2, fMax2, paint2);
        }
        canvas.restore();
    }

    public static boolean isContextSafe(Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            Activity activity = (Activity) context;
            return (activity.isFinishing() || activity.isDestroyed()) ? false : true;
        }
        if (context instanceof ContextWrapper) {
            return isContextSafe(((ContextWrapper) context).getBaseContext());
        }
        return true;
    }

    public static Bitmap applyColorMatrix(Bitmap bitmap, ColorMatrix colorMatrix) {
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        new Canvas(bitmapCreateBitmap).drawBitmap(bitmap, 0.0f, 0.0f, paint);
        return bitmapCreateBitmap;
    }

    public static int applyColorMatrix(int i, ColorMatrix colorMatrix) {
        float[] array = colorMatrix.getArray();
        int iAlpha = Color.alpha(i);
        int iRed = Color.red(i);
        float f = iRed;
        float fGreen = Color.green(i);
        float fBlue = Color.blue(i);
        float f2 = iAlpha;
        float f3 = (array[0] * f) + (array[1] * fGreen) + (array[2] * fBlue) + (array[3] * f2) + array[4];
        float f4 = (array[5] * f) + (array[6] * fGreen) + (array[7] * fBlue) + (array[8] * f2) + array[9];
        float f5 = (array[10] * f) + (array[11] * fGreen) + (array[12] * fBlue) + (array[13] * f2) + array[14];
        return Color.argb(MathUtils.clamp(Math.round((array[15] * f) + (array[16] * fGreen) + (array[17] * fBlue) + (array[18] * f2) + array[19]), 0, 255), MathUtils.clamp(Math.round(f3), 0, 255), MathUtils.clamp(Math.round(f4), 0, 255), MathUtils.clamp(Math.round(f5), 0, 255));
    }

    public static void createCalendarEvent(Activity activity, long j, String str, String str2, boolean z) {
        long j2;
        if (z) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(j);
            calendar.set(11, 0);
            calendar.set(12, 0);
            calendar.set(13, 0);
            calendar.set(14, 0);
            j = calendar.getTimeInMillis();
            j2 = DurationKt.MILLIS_IN_DAY;
        } else {
            j2 = 600000;
        }
        Intent intentPutExtra = new Intent("android.intent.action.INSERT").setData(CalendarContract.Events.CONTENT_URI).putExtra("beginTime", j).putExtra("endTime", j2 + j).putExtra("allDay", z);
        if (!TextUtils.isEmpty(str)) {
            intentPutExtra.putExtra("title", str);
        }
        if (!TextUtils.isEmpty(str2)) {
            intentPutExtra.putExtra(MediaTrack.ROLE_DESCRIPTION, str2);
        }
        try {
            activity.startActivity(intentPutExtra);
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public static void dumpCanvas(View view) {
        if (BuildVars.DEBUG_PRIVATE_VERSION) {
            DebugRecordingCanvas debugRecordingCanvas = new DebugRecordingCanvas(Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888));
            view.draw(debugRecordingCanvas);
            debugRecordingCanvas.logCommands();
            LaunchActivity.instance.lambda$runLinkRequest$101(new DebugRecordingCanvasReplayFragment(debugRecordingCanvas));
        }
    }

    public static <A, B> B find(List<A> list, Class<B> cls) {
        if (list == null) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            A a2 = list.get(i);
            if (cls.isInstance(a2)) {
                return cls.cast(a2);
            }
        }
        return null;
    }

    public static <A, B> B findLast(List<A> list, Class<B> cls) {
        if (list == null) {
            return null;
        }
        for (int size = list.size() - 1; size >= 0; size--) {
            A a2 = list.get(size);
            if (cls.isInstance(a2)) {
                return cls.cast(a2);
            }
        }
        return null;
    }

    public static TLRPC.Photo findPhoto(List<TLRPC.Photo> list, long j) {
        if (list == null) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            TLRPC.Photo photo = list.get(i);
            if (photo != null && photo.id == j) {
                return photo;
            }
        }
        return null;
    }

    public static TLRPC.Document findDocument(List<TLRPC.Document> list, long j) {
        if (list == null) {
            return null;
        }
        for (int i = 0; i < list.size(); i++) {
            TLRPC.Document document = list.get(i);
            if (document != null && document.id == j) {
                return document;
            }
        }
        return null;
    }
}
