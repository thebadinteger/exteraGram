package com.exteragram.messenger.utils.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import androidx.core.graphics.ColorUtils;
import com.google.android.material.color.MaterialColors;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;

@Metadata(d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\bÇ\u0002\u0018\u00002\u00020\u0001:\u0001&B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0016\u001a\u00020\u00072\u0006\u0010\u0017\u001a\u00020\u0006H\u0007J\b\u0010\u0018\u001a\u00020\u0019H\u0007J\u0018\u0010\u001a\u001a\u00020\u00072\u0006\u0010\u001b\u001a\u00020\u00072\u0006\u0010\u001c\u001a\u00020\u0019H\u0007J\u0012\u0010\u001d\u001a\u00020\u00072\b\b\u0001\u0010\u001e\u001a\u00020\u0007H\u0007J\b\u0010\u001f\u001a\u00020\u0007H\u0007J\u0010\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0007J\u0010\u0010$\u001a\u00020!2\u0006\u0010\"\u001a\u00020#H\u0007J\b\u0010%\u001a\u00020!H\u0003R*\u0010\u0004\u001a\u001e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00070\u0005j\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u0007`\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u0016\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00060\nX\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u000bR\u0016\u0010\f\u001a\n \u000e*\u0004\u0018\u00010\r0\rX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u0006X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0007X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006'"}, d2 = {"Lcom/exteragram/messenger/utils/ui/MonetUtils;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "COLOR_MAP", "Ljava/util/HashMap;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlin/collections/HashMap;", "ACCENT_PREFIXES", _UrlKt.FRAGMENT_ENCODE_SET, "[Ljava/lang/String;", "PARAM_PATTERN", "Ljava/util/regex/Pattern;", "kotlin.jvm.PlatformType", "ACTION_OVERLAY_CHANGED", "overlayChangeReceiver", "Lcom/exteragram/messenger/utils/ui/MonetUtils$OverlayChangeReceiver;", "KEY_ALPHA", "KEY_SATURATION", "KEY_LIGHTNESS", "harmonizeContextColor", "getColor", "colorString", "isSupported", _UrlKt.FRAGMENT_ENCODE_SET, "getSystemAccentColor", "index", "isDark", "harmonize", "color", "getHarmonizeContextColor", "registerReceiver", _UrlKt.FRAGMENT_ENCODE_SET, "context", "Landroid/content/Context;", "unregisterReceiver", "initSystemColors", "OverlayChangeReceiver", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class MonetUtils {
    private static final String[] ACCENT_PREFIXES;
    private static final HashMap<String, Integer> COLOR_MAP;
    public static final MonetUtils INSTANCE;
    private static final Pattern PARAM_PATTERN;
    private static volatile int harmonizeContextColor;
    private static final OverlayChangeReceiver overlayChangeReceiver;
    private static final String ACTION_OVERLAY_CHANGED = "android.intent.action.OVERLAY_CHANGED";
    private static final String KEY_ALPHA = "a";
    private static final String KEY_SATURATION = "s";
    private static final String KEY_LIGHTNESS = "l";

    private MonetUtils() {
    }

    static {
        MonetUtils monetUtils = new MonetUtils();
        INSTANCE = monetUtils;
        HashMap<String, Integer> map = new HashMap<>();
        COLOR_MAP = map;
        ACCENT_PREFIXES = new String[]{"a1_", "a2_", "a3_"};
        PARAM_PATTERN = Pattern.compile("^([^(]+)\\(([^)]+)\\)?$");
        overlayChangeReceiver = new OverlayChangeReceiver();
        map.put("mBlack", Integer.valueOf(R.color.black));
        map.put("mWhite", Integer.valueOf(R.color.white));
        map.put("mRed200", Integer.valueOf(R.color.mRed200));
        map.put("mRed500", Integer.valueOf(R.color.mRed500));
        map.put("mRed800", Integer.valueOf(R.color.mRed800));
        map.put("mGreen200", Integer.valueOf(R.color.mGreen200));
        map.put("mGreen500", Integer.valueOf(R.color.mGreen500));
        map.put("mGreen800", Integer.valueOf(R.color.mGreen800));
        if (isSupported()) {
            monetUtils.initSystemColors();
        }
    }

    @JvmStatic
    public static final int getColor(String colorString) {
        int i;
        int i2;
        int i3;
        String string;
        if (colorString.length() == 0) {
            return 0;
        }
        try {
            Matcher matcher = PARAM_PATTERN.matcher(colorString);
            if (matcher.find()) {
                String strGroup = matcher.group(1);
                if (strGroup != null && (string = StringsKt.trim((CharSequence) strGroup).toString()) != null) {
                    colorString = string;
                }
                String strGroup2 = matcher.group(2);
                if (strGroup2 != null) {
                    i = 100;
                    i2 = 100;
                    i3 = 100;
                    for (String part : strGroup2.split(",")) {
                        String[] pair = part.split("=");
                        if (pair.length == 2) {
                            try {
                                String string2 = pair[0].trim();
                                int i4 = Integer.parseInt(pair[1].trim());
                                int iHashCode = string2.hashCode();
                                if (iHashCode != 97) {
                                    if (iHashCode != 108) {
                                        if (iHashCode == 115 && string2.equals("s")) {
                                            i2 = i4;
                                        }
                                    } else if (string2.equals("l")) {
                                        i3 = i4;
                                    }
                                } else if (string2.equals("a")) {
                                    i = i4;
                                }
                            } catch (NumberFormatException unused) {
                            }
                        }
                    }
                } else {
                    i = 100;
                    i2 = 100;
                    i3 = 100;
                }
            } else {
                i = 100;
                i2 = 100;
                i3 = 100;
            }
            Integer num = COLOR_MAP.get(colorString);
            if (num != null && num.intValue() != 0) {
                int color = ApplicationLoader.applicationContext.getColor(num.intValue());
                if (i2 != 100) {
                    color = ColorUtils.blendARGB(-1, color, i2 / 100.0f);
                }
                if (i3 != 100) {
                    color = ColorUtils.blendARGB(-16777216, color, i3 / 100.0f);
                }
                if (i != 100) {
                    color = ColorUtils.setAlphaComponent(color, (int) (i * 2.55f));
                }
                return (colorString.startsWith("mR") || colorString.startsWith("mG")) ? harmonize(color) : color;
            }
            return 0;
        } catch (Exception e) {
            FileLog.e(e);
            return 0;
        }
    }

    @JvmStatic
    public static final boolean isSupported() {
        return Build.VERSION.SDK_INT >= 31;
    }

    @JvmStatic
    public static final int getSystemAccentColor(int index, boolean isDark) {
        if (!isSupported() || index < 0) {
            return 0;
        }
        String[] strArr = ACCENT_PREFIXES;
        if (index >= strArr.length) {
            return 0;
        }
        return getColor(strArr[index] + (isDark ? 200 : 600));
    }

    @JvmStatic
    public static final int harmonize(int color) {
        int harmonizeContextColor2 = getHarmonizeContextColor();
        return harmonizeContextColor2 == 0 ? color : MaterialColors.harmonize(color, harmonizeContextColor2);
    }

    @JvmStatic
    public static final int getHarmonizeContextColor() {
        if (!isSupported()) {
            return 0;
        }
        int i = harmonizeContextColor;
        if (i != 0) {
            return i;
        }
        int color = ApplicationLoader.applicationContext.getColor(android.R.color.system_accent1_600);
        harmonizeContextColor = color;
        return color;
    }

    @JvmStatic
    public static final void registerReceiver(Context context) {
        try {
            harmonizeContextColor = 0;
            overlayChangeReceiver.register(context);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @JvmStatic
    public static final void unregisterReceiver(Context context) {
        try {
            overlayChangeReceiver.unregister(context);
        } catch (Exception unused) {
        }
    }

    @Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u000e\u0010\n\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tJ\u0018\u0010\u000b\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\f\u001a\u00020\rH\u0016R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u000e"}, d2 = {"Lcom/exteragram/messenger/utils/ui/MonetUtils$OverlayChangeReceiver;", "Landroid/content/BroadcastReceiver;", "<init>", "()V", "isRegistered", _UrlKt.FRAGMENT_ENCODE_SET, "register", _UrlKt.FRAGMENT_ENCODE_SET, "context", "Landroid/content/Context;", "unregister", "onReceive", "intent", "Landroid/content/Intent;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class OverlayChangeReceiver extends BroadcastReceiver {
        private boolean isRegistered;

        public final void register(Context context) {
            if (this.isRegistered) {
                return;
            }
            IntentFilter intentFilter = new IntentFilter("android.intent.action.OVERLAY_CHANGED");
            intentFilter.addDataScheme("package");
            intentFilter.addDataSchemeSpecificPart("android", 0);
            context.registerReceiver(this, intentFilter);
            this.isRegistered = true;
        }

        public final void unregister(Context context) {
            if (this.isRegistered) {
                context.unregisterReceiver(this);
                this.isRegistered = false;
            }
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if (Intrinsics.areEqual("android.intent.action.OVERLAY_CHANGED", intent.getAction())) {
                MonetUtils.harmonizeContextColor = 0;
                Theme.refreshMonetColors();
                if (Theme.isCurrentThemeMonet() || Theme.isCurrentAccentMonet()) {
                    Theme.applyTheme(Theme.getActiveTheme(), Theme.isCurrentThemeNight());
                }
            }
        }
    }

    private final void initSystemColors() {
        HashMap<String, Integer> map = COLOR_MAP;
        map.put("a1_10", Integer.valueOf(android.R.color.system_accent1_10));
        map.put("a1_50", Integer.valueOf(android.R.color.system_accent1_50));
        map.put("a1_100", Integer.valueOf(android.R.color.system_accent1_100));
        map.put("a1_200", Integer.valueOf(android.R.color.system_accent1_200));
        map.put("a1_300", Integer.valueOf(android.R.color.system_accent1_300));
        map.put("a1_400", Integer.valueOf(android.R.color.system_accent1_400));
        map.put("a1_500", Integer.valueOf(android.R.color.system_accent1_500));
        map.put("a1_600", Integer.valueOf(android.R.color.system_accent1_600));
        map.put("a1_700", Integer.valueOf(android.R.color.system_accent1_700));
        map.put("a1_800", Integer.valueOf(android.R.color.system_accent1_800));
        map.put("a1_900", Integer.valueOf(android.R.color.system_accent1_900));
        map.put("a2_10", Integer.valueOf(android.R.color.system_accent2_10));
        map.put("a2_50", Integer.valueOf(android.R.color.system_accent2_50));
        map.put("a2_100", Integer.valueOf(android.R.color.system_accent2_100));
        map.put("a2_200", Integer.valueOf(android.R.color.system_accent2_200));
        map.put("a2_300", Integer.valueOf(android.R.color.system_accent2_300));
        map.put("a2_400", Integer.valueOf(android.R.color.system_accent2_400));
        map.put("a2_500", Integer.valueOf(android.R.color.system_accent2_500));
        map.put("a2_600", Integer.valueOf(android.R.color.system_accent2_600));
        map.put("a2_700", Integer.valueOf(android.R.color.system_accent2_700));
        map.put("a2_800", Integer.valueOf(android.R.color.system_accent2_800));
        map.put("a2_900", Integer.valueOf(android.R.color.system_accent2_900));
        map.put("a3_10", Integer.valueOf(android.R.color.system_accent3_10));
        map.put("a3_50", Integer.valueOf(android.R.color.system_accent3_50));
        map.put("a3_100", Integer.valueOf(android.R.color.system_accent3_100));
        map.put("a3_200", Integer.valueOf(android.R.color.system_accent3_200));
        map.put("a3_300", Integer.valueOf(android.R.color.system_accent3_300));
        map.put("a3_400", Integer.valueOf(android.R.color.system_accent3_400));
        map.put("a3_500", Integer.valueOf(android.R.color.system_accent3_500));
        map.put("a3_600", Integer.valueOf(android.R.color.system_accent3_600));
        map.put("a3_700", Integer.valueOf(android.R.color.system_accent3_700));
        map.put("a3_800", Integer.valueOf(android.R.color.system_accent3_800));
        map.put("a3_900", Integer.valueOf(android.R.color.system_accent3_900));
        map.put("n1_10", Integer.valueOf(android.R.color.system_neutral1_10));
        map.put("n1_50", Integer.valueOf(android.R.color.system_neutral1_50));
        map.put("n1_100", Integer.valueOf(android.R.color.system_neutral1_100));
        map.put("n1_200", Integer.valueOf(android.R.color.system_neutral1_200));
        map.put("n1_300", Integer.valueOf(android.R.color.system_neutral1_300));
        map.put("n1_400", Integer.valueOf(android.R.color.system_neutral1_400));
        map.put("n1_500", Integer.valueOf(android.R.color.system_neutral1_500));
        map.put("n1_600", Integer.valueOf(android.R.color.system_neutral1_600));
        map.put("n1_700", Integer.valueOf(android.R.color.system_neutral1_700));
        map.put("n1_800", Integer.valueOf(android.R.color.system_neutral1_800));
        map.put("n1_900", Integer.valueOf(android.R.color.system_neutral1_900));
        map.put("n2_10", Integer.valueOf(android.R.color.system_neutral2_10));
        map.put("n2_50", Integer.valueOf(android.R.color.system_neutral2_50));
        map.put("n2_100", Integer.valueOf(android.R.color.system_neutral2_100));
        map.put("n2_200", Integer.valueOf(android.R.color.system_neutral2_200));
        map.put("n2_300", Integer.valueOf(android.R.color.system_neutral2_300));
        map.put("n2_400", Integer.valueOf(android.R.color.system_neutral2_400));
        map.put("n2_500", Integer.valueOf(android.R.color.system_neutral2_500));
        map.put("n2_600", Integer.valueOf(android.R.color.system_neutral2_600));
        map.put("n2_700", Integer.valueOf(android.R.color.system_neutral2_700));
        map.put("n2_800", Integer.valueOf(android.R.color.system_neutral2_800));
        map.put("n2_900", Integer.valueOf(android.R.color.system_neutral2_900));
    }
}
