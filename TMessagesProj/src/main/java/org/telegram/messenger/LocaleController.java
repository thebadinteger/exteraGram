package org.telegram.messenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.icu.text.RelativeDateTimeFormatter;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Xml;
import com.android.dx.rop.code.RegisterSpec;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.google.android.gms.cast.HlsSegmentFormat;
import com.google.android.material.timepicker.TimeModel;
import j$.util.DesugarTimeZone;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import kotlin.text.Typography;
import kotlin.time.DurationKt;
import okhttp3.internal.url._UrlKt;
import org.mvel2.MVEL;
import org.mvel2.asm.signature.SignatureVisitor;
import org.telegram.messenger.time.FastDateFormat;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.RestrictedLanguagesSelectActivity;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class LocaleController {
    static final int QUANTITY_FEW = 8;
    static final int QUANTITY_MANY = 16;
    static final int QUANTITY_ONE = 2;
    static final int QUANTITY_OTHER = 0;
    static final int QUANTITY_TWO = 4;
    static final int QUANTITY_ZERO = 1;
    public static boolean is24HourFormat = false;
    public static boolean isRTL = false;
    public static int nameDisplayOrder = 1;
    private static Boolean useImperialSystemType;
    private volatile FastDateFormat chatDate;
    private volatile FastDateFormat chatDateShort;
    private volatile FastDateFormat chatFullDate;
    private boolean checkingUpdateForCurrentRemoteLocale;
    private HashMap<String, String> currencyValues;
    private Locale currentLocale;
    private LocaleInfo currentLocaleInfo;
    private PluralRules currentPluralRules;
    private String currentSystemLocale;
    private volatile FastDateFormat exportFileFormatter;
    private volatile FastDateFormat exportFullDateFormatter;
    private volatile FastDateFormat formatterBannedUntil;
    private volatile FastDateFormat formatterBannedUntilThisYear;
    private volatile FastDateFormat formatterBoostExpired;
    private volatile FastDateFormat formatterConstDay;
    private volatile FastDateFormat formatterDay;
    private volatile FastDateFormat formatterDayMonth;
    private volatile FastDateFormat formatterDayWithSeconds;
    private volatile FastDateFormat formatterGiveawayCard;
    private volatile FastDateFormat formatterGiveawayMonthDay;
    private volatile FastDateFormat formatterGiveawayMonthDayYear;
    private volatile FastDateFormat formatterMonthOnly;
    private volatile FastDateFormat formatterMonthYear;
    private volatile FastDateFormat formatterScheduleDay;
    private volatile FastDateFormat formatterScheduleYear;
    private volatile FastDateFormat formatterStats;
    private volatile FastDateFormat formatterWeek;
    private volatile FastDateFormat formatterWeekLong;
    private volatile FastDateFormat formatterYear;
    private volatile FastDateFormat formatterYearMax;
    private String languageOverride;
    private boolean loadingRemoteLanguages;
    private boolean reloadLastFile;
    private HashMap<String, String> ruTranslitChars;
    private Locale systemDefaultLocale;
    private HashMap<String, String> translitChars;
    private static HashMap<Integer, String> resourcesCacheMap = new HashMap<>();
    private static volatile LocaleController Instance = null;
    private static char[] defaultNumbers = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static char[][] otherNumbers = {new char[]{1632, 1633, 1634, 1635, 1636, 1637, 1638, 1639, 1640, 1641}, new char[]{1776, 1777, 1778, 1779, 1780, 1781, 1782, 1783, 1784, 1785}, new char[]{2406, 2407, 2408, 2409, 2410, 2411, 2412, 2413, 2414, 2415}, new char[]{2790, 2791, 2792, 2793, 2794, 2795, 2796, 2797, 2798, 2799}, new char[]{2662, 2663, 2664, 2665, 2666, 2667, 2668, 2669, 2670, 2671}, new char[]{2534, 2535, 2536, 2537, 2538, 2539, 2540, 2541, 2542, 2543}, new char[]{3302, 3303, 3304, 3305, 3306, 3307, 3308, 3309, 3310, 3311}, new char[]{2918, 2919, 2920, 2921, 2922, 2923, 2924, 2925, 2926, 2927}, new char[]{3430, 3431, 3432, 3433, 3434, 3435, 3436, 3437, 3438, 3439}, new char[]{3046, 3047, 3048, 3049, 3050, 3051, 3052, 3053, 3054, 3055}, new char[]{3174, 3175, 3176, 3177, 3178, 3179, 3180, 3181, 3182, 3183}, new char[]{4160, 4161, 4162, 4163, 4164, 4165, 4166, 4167, 4168, 4169}, new char[]{3872, 3873, 3874, 3875, 3876, 3877, 3878, 3879, 3880, 3881}, new char[]{6160, 6161, 6162, 6163, 6164, 6165, 6166, 6167, 6168, 6169}, new char[]{6112, 6113, 6114, 6115, 6116, 6117, 6118, 6119, 6120, 6121}, new char[]{3664, 3665, 3666, 3667, 3668, 3669, 3670, 3671, 3672, 3673}, new char[]{3792, 3793, 3794, 3795, 3796, 3797, 3798, 3799, 3800, 3801}, new char[]{43472, 43473, 43474, 43475, 43476, 43477, 43478, 43479, 43480, 43481}};
    private final FastDateFormat[] formatterScheduleSend = new FastDateFormat[18];
    private HashMap<String, PluralRules> allRules = new HashMap<>();
    private HashMap<String, String> localeValues = new HashMap<>();
    private boolean changingConfiguration = false;
    public ArrayList<LocaleInfo> languages = new ArrayList<>();
    public ArrayList<LocaleInfo> unofficialLanguages = new ArrayList<>();
    public ArrayList<LocaleInfo> remoteLanguages = new ArrayList<>();
    public HashMap<String, LocaleInfo> remoteLanguagesDict = new HashMap<>();
    public HashMap<String, LocaleInfo> languagesDict = new HashMap<>();
    private ArrayList<LocaleInfo> otherLanguages = new ArrayList<>();
    private boolean patching = false;

    public static abstract class PluralRules {
        public abstract int quantityForNumber(int i);
    }

    public static class PluralRules_Breton extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 4;
            }
            if (i == 3) {
                return 8;
            }
            return i == 6 ? 16 : 0;
        }
    }

    public static class PluralRules_Czech extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            if (i == 1) {
                return 2;
            }
            return (i < 2 || i > 4) ? 0 : 8;
        }
    }

    public static class PluralRules_French extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            return (i < 0 || i >= 2) ? 0 : 2;
        }
    }

    public static class PluralRules_Langi extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            return i == 1 ? 2 : 0;
        }
    }

    public static class PluralRules_None extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            return 0;
        }
    }

    public static class PluralRules_One extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            return i == 1 ? 2 : 0;
        }
    }

    public static class PluralRules_Tachelhit extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            if (i < 0 || i > 1) {
                return (i < 2 || i > 10) ? 0 : 8;
            }
            return 2;
        }
    }

    public static class PluralRules_Two extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            if (i == 1) {
                return 2;
            }
            return i == 2 ? 4 : 0;
        }
    }

    public static class PluralRules_Welsh extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 4;
            }
            if (i == 3) {
                return 8;
            }
            return i == 6 ? 16 : 0;
        }
    }

    public static class PluralRules_Zero extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            return (i == 0 || i == 1) ? 2 : 0;
        }
    }

    public FastDateFormat getFormatterDay() {
        String str;
        int i;
        String str2;
        if (this.formatterDay == null) {
            synchronized (this) {
                try {
                    if (this.formatterDay == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        String lowerCase = locale.getLanguage().toLowerCase();
                        if (!lowerCase.equalsIgnoreCase("ar") && !lowerCase.equalsIgnoreCase("ko")) {
                            locale = Locale.US;
                        }
                        if (is24HourFormat) {
                            if (ExteraConfig.getFormatTimeWithSeconds()) {
                                str = "formatterDay24HSec";
                                i = R.string.formatterDay24HSec;
                            } else {
                                str = "formatterDay24H";
                                i = R.string.formatterDay24H;
                            }
                        } else if (ExteraConfig.getFormatTimeWithSeconds()) {
                            str = "formatterDay12HSec";
                            i = R.string.formatterDay12HSec;
                        } else {
                            str = "formatterDay12H";
                            i = R.string.formatterDay12H;
                        }
                        String stringInternal = getStringInternal(str, i);
                        if (is24HourFormat) {
                            str2 = ExteraConfig.getFormatTimeWithSeconds() ? "HH:mm:ss" : "HH:mm";
                        } else {
                            str2 = ExteraConfig.getFormatTimeWithSeconds() ? "h:mm:ss a" : "h:mm a";
                        }
                        this.formatterDay = createFormatter(locale, stringInternal, str2);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterDay;
    }

    public FastDateFormat getFormatterDayWithSeconds() {
        String str;
        int i;
        if (this.formatterDayWithSeconds == null) {
            synchronized (this) {
                try {
                    if (this.formatterDayWithSeconds == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        String lowerCase = locale.getLanguage().toLowerCase();
                        if (!lowerCase.equalsIgnoreCase("ar") && !lowerCase.equalsIgnoreCase("ko")) {
                            locale = Locale.US;
                        }
                        if (is24HourFormat) {
                            str = "formatterDayWithSeconds24H";
                            i = R.string.formatterDayWithSeconds24H;
                        } else {
                            str = "formatterDayWithSeconds12H";
                            i = R.string.formatterDayWithSeconds12H;
                        }
                        this.formatterDayWithSeconds = createFormatter(locale, getStringInternal(str, i), is24HourFormat ? "HH:mm:ss" : "h:mm:ss a");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterDayWithSeconds;
    }

    public FastDateFormat getExportFullDateFormatter() {
        if (this.exportFullDateFormatter == null) {
            synchronized (this) {
                try {
                    if (this.exportFullDateFormatter == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.exportFullDateFormatter = createFormatter(locale, null, "dd MMM yyyy HH:mm:ss");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.exportFullDateFormatter;
    }

    public FastDateFormat getExportFileFormatter() {
        if (this.exportFileFormatter == null) {
            synchronized (this) {
                try {
                    if (this.exportFileFormatter == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.exportFileFormatter = createFormatter(locale, null, "dd-MM-yyyy_HH-mm");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.exportFileFormatter;
    }

    public FastDateFormat getFormatterConstDay() {
        if (this.formatterConstDay == null) {
            synchronized (this) {
                try {
                    if (this.formatterConstDay == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        String language = locale.getLanguage();
                        if (language == null) {
                            language = "en";
                        }
                        String lowerCase = language.toLowerCase();
                        if (!lowerCase.toLowerCase().equals("ar") && !lowerCase.toLowerCase().equals("ko")) {
                            locale = Locale.US;
                        }
                        boolean z = is24HourFormat;
                        this.formatterConstDay = createFormatter(locale, z ? "HH:mm" : "h:mm a", z ? "HH:mm" : "h:mm a");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterConstDay;
    }

    public FastDateFormat getFormatterWeek() {
        if (this.formatterWeek == null) {
            synchronized (this) {
                try {
                    if (this.formatterWeek == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterWeek = createFormatter(locale, getStringInternal("formatterWeek", R.string.formatterWeek), "EEE");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterWeek;
    }

    public FastDateFormat getFormatterWeekLong() {
        if (this.formatterWeekLong == null) {
            synchronized (this) {
                try {
                    if (this.formatterWeekLong == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterWeekLong = createFormatter(locale, getStringInternal("formatterWeekLong", R.string.formatterWeekLong), "EEEE");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterWeekLong;
    }

    public FastDateFormat getFormatterDayMonth() {
        if (this.formatterDayMonth == null) {
            synchronized (this) {
                try {
                    if (this.formatterDayMonth == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterDayMonth = createFormatter(locale, getStringInternal("formatterMonth", R.string.formatterMonth), "dd MMM");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterDayMonth;
    }

    public FastDateFormat getFormatterYear() {
        if (this.formatterYear == null) {
            synchronized (this) {
                try {
                    if (this.formatterYear == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterYear = createFormatter(locale, getStringInternal("formatterYear", R.string.formatterYear), "dd.MM.yy");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterYear;
    }

    public FastDateFormat getFormatterYearMax() {
        if (this.formatterYearMax == null) {
            synchronized (this) {
                try {
                    if (this.formatterYearMax == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterYearMax = createFormatter(locale, getStringInternal("formatterYearMax", R.string.formatterYearMax), "dd.MM.yyyy");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterYearMax;
    }

    public FastDateFormat getFormatterStats() {
        String str;
        int i;
        if (this.formatterStats == null) {
            synchronized (this) {
                try {
                    if (this.formatterStats == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        if (is24HourFormat) {
                            str = "formatterStats24H";
                            i = R.string.formatterStats24H;
                        } else {
                            str = "formatterStats12H";
                            i = R.string.formatterStats12H;
                        }
                        this.formatterStats = createFormatter(locale, getStringInternal(str, i), is24HourFormat ? "MMM dd yyyy, HH:mm" : "MMM dd yyyy, h:mm a");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterStats;
    }

    public FastDateFormat getFormatterBannedUntil() {
        String str;
        int i;
        if (this.formatterBannedUntil == null) {
            synchronized (this) {
                try {
                    if (this.formatterBannedUntil == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        if (is24HourFormat) {
                            str = "formatterBannedUntil24H";
                            i = R.string.formatterBannedUntil24H;
                        } else {
                            str = "formatterBannedUntil12H";
                            i = R.string.formatterBannedUntil12H;
                        }
                        this.formatterBannedUntil = createFormatter(locale, getStringInternal(str, i), is24HourFormat ? "MMM dd yyyy, HH:mm" : "MMM dd yyyy, h:mm a");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterBannedUntil;
    }

    public FastDateFormat getFormatterBannedUntilThisYear() {
        String str;
        int i;
        if (this.formatterBannedUntilThisYear == null) {
            synchronized (this) {
                try {
                    if (this.formatterBannedUntilThisYear == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        if (is24HourFormat) {
                            str = "formatterBannedUntilThisYear24H";
                            i = R.string.formatterBannedUntilThisYear24H;
                        } else {
                            str = "formatterBannedUntilThisYear12H";
                            i = R.string.formatterBannedUntilThisYear12H;
                        }
                        this.formatterBannedUntilThisYear = createFormatter(locale, getStringInternal(str, i), is24HourFormat ? "MMM dd, HH:mm" : "MMM dd, h:mm a");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterBannedUntilThisYear;
    }

    public FastDateFormat getChatDate() {
        if (this.chatDate == null) {
            synchronized (this) {
                try {
                    if (this.chatDate == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.chatDate = createFormatter(locale, getStringInternal("chatDate", R.string.chatDate), "d MMMM");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.chatDate;
    }

    public FastDateFormat getChatDateShort() {
        if (this.chatDateShort == null) {
            synchronized (this) {
                try {
                    if (this.chatDateShort == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.chatDateShort = createFormatter(locale, getStringInternal("chatDateShort", R.string.chatDateShort), "d MMM");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.chatDateShort;
    }

    public FastDateFormat getChatFullDate() {
        if (this.chatFullDate == null) {
            synchronized (this) {
                try {
                    if (this.chatFullDate == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.chatFullDate = createFormatter(locale, getStringInternal("chatFullDate", R.string.chatFullDate), "d MMMM yyyy");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.chatFullDate;
    }

    public FastDateFormat getFormatterScheduleDay() {
        if (this.formatterScheduleDay == null) {
            synchronized (this) {
                try {
                    if (this.formatterScheduleDay == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterScheduleDay = createFormatter(locale, getStringInternal("formatDateSchedule", R.string.formatDateSchedule), "MMM d");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterScheduleDay;
    }

    public FastDateFormat getFormatterScheduleYear() {
        if (this.formatterScheduleYear == null) {
            synchronized (this) {
                try {
                    if (this.formatterScheduleYear == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterScheduleYear = createFormatter(locale, getStringInternal("formatDateScheduleYear", R.string.formatDateScheduleYear), "MMM d yyyy");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterScheduleYear;
    }

    public FastDateFormat getFormatterMonthYear() {
        if (this.formatterMonthYear == null) {
            synchronized (this) {
                try {
                    if (this.formatterMonthYear == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterMonthYear = createFormatter(locale, getStringInternal("formatterMonthYear", R.string.formatterMonthYear), "MMM yyyy");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterMonthYear;
    }

    public FastDateFormat getFormatterMonthOnly() {
        if (this.formatterMonthOnly == null) {
            synchronized (this) {
                try {
                    if (this.formatterMonthOnly == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterMonthOnly = createFormatter(locale, getStringInternal("formatterMonthOnly", R.string.formatterMonthOnly), "MMMM");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterMonthOnly;
    }

    public FastDateFormat getFormatterGiveawayCard() {
        if (this.formatterGiveawayCard == null) {
            synchronized (this) {
                try {
                    if (this.formatterGiveawayCard == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterGiveawayCard = createFormatter(locale, getStringInternal("formatterGiveawayCard", R.string.formatterGiveawayCard), "dd MMM yyyy");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterGiveawayCard;
    }

    public FastDateFormat getFormatterBoostExpired() {
        if (this.formatterBoostExpired == null) {
            synchronized (this) {
                try {
                    if (this.formatterBoostExpired == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterBoostExpired = createFormatter(locale, getStringInternal("formatterBoostExpired", R.string.formatterBoostExpired), "MMM dd, yyyy");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterBoostExpired;
    }

    public FastDateFormat getFormatterGiveawayMonthDay() {
        if (this.formatterGiveawayMonthDay == null) {
            synchronized (this) {
                try {
                    if (this.formatterGiveawayMonthDay == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterGiveawayMonthDay = createFormatter(locale, getStringInternal("formatterGiveawayMonthDay", R.string.formatterGiveawayMonthDay), "MMMM dd");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterGiveawayMonthDay;
    }

    public FastDateFormat getFormatterGiveawayMonthDayYear() {
        if (this.formatterGiveawayMonthDayYear == null) {
            synchronized (this) {
                try {
                    if (this.formatterGiveawayMonthDayYear == null) {
                        Locale locale = this.currentLocale;
                        if (locale == null) {
                            locale = Locale.getDefault();
                        }
                        this.formatterGiveawayMonthDayYear = createFormatter(locale, getStringInternal("formatterGiveawayMonthDayYear", R.string.formatterGiveawayMonthDayYear), "MMMM dd, yyyy");
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return this.formatterGiveawayMonthDayYear;
    }

    public FastDateFormat getFormatterScheduleSend(int i) {
        if (i < 0) {
            return null;
        }
        FastDateFormat[] fastDateFormatArr = this.formatterScheduleSend;
        if (i >= fastDateFormatArr.length) {
            return null;
        }
        if (fastDateFormatArr[i] == null) {
            Locale locale = this.currentLocale;
            if (locale == null) {
                locale = Locale.getDefault();
            }
            switch (i) {
                case 0:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("SendTodayAt", R.string.SendTodayAt), "'Send today at' HH:mm");
                    break;
                case 1:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("SendDayAt", R.string.SendDayAt), "'Send on' MMM d 'at' HH:mm");
                    break;
                case 2:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("SendDayYearAt", R.string.SendDayYearAt), "'Send on' MMM d yyyy 'at' HH:mm");
                    break;
                case 3:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("RemindTodayAt", R.string.RemindTodayAt), "'Remind today at' HH:mm");
                    break;
                case 4:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("RemindDayAt", R.string.RemindDayAt), "'Remind on' MMM d 'at' HH:mm");
                    break;
                case 5:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("RemindDayYearAt", R.string.RemindDayYearAt), "'Remind on' MMM d yyyy 'at' HH:mm");
                    break;
                case 6:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartTodayAt", R.string.StartTodayAt), "'Start today at' HH:mm");
                    break;
                case 7:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartDayAt", R.string.StartDayAt), "'Start on' MMM d 'at' HH:mm");
                    break;
                case 8:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartDayYearAt", R.string.StartDayYearAt), "'Start on' MMM d yyyy 'at' HH:mm");
                    break;
                case 9:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartShortTodayAt", R.string.StartShortTodayAt), "'Today,' HH:mm");
                    break;
                case 10:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartShortDayAt", R.string.StartShortDayAt), "MMM d',' HH:mm");
                    break;
                case 11:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartShortDayYearAt", R.string.StartShortDayYearAt), "MMM d yyyy, HH:mm");
                    break;
                case 12:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartsTodayAt", R.string.StartsTodayAt), "'Starts today at' HH:mm");
                    break;
                case 13:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartsDayAt", R.string.StartsDayAt), "'Starts on' MMM d 'at' HH:mm");
                    break;
                case 14:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("StartsDayYearAt", R.string.StartsDayYearAt), "'Starts on' MMM d yyyy 'at' HH:mm");
                    break;
                case 15:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("PublishTodayAt", R.string.PublishTodayAt), "'Publish today at' HH:mm");
                    break;
                case 16:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("PublishDayAt", R.string.PublishDayAt), "'Publish on' MMM d 'at' HH:mm");
                    break;
                case 17:
                    this.formatterScheduleSend[i] = createFormatter(locale, getStringInternal("PublishDayYearAt", R.string.PublishDayYearAt), "'Publish on' MMM d yyyy 'at' HH:mm");
                    break;
            }
        }
        return this.formatterScheduleSend[i];
    }

    public class TimeZoneChangedReceiver extends BroadcastReceiver {
        private TimeZoneChangedReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            ApplicationLoader.applicationHandler.post(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onReceive$0();
                }
            });
        }

        public void lambda$new$0() {
        loadRemoteLanguages(UserConfig.selectedAccount);
    }

    public void lambda$checkUpdateForCurrentRemoteLocale$2(int i) {
        this.checkingUpdateForCurrentRemoteLocale = false;
        checkPatchLangpack(i);
    }

    public void lambda$checkPatchLangpack$4(int i) {
        reloadCurrentRemoteLocale(i, null, true, null);
    }

    public void checkForcePatchLangpack(int i, final Runnable runnable) {
        final String currentLanguageName = getCurrentLanguageName();
        if (MessagesController.getInstance(i).checkResetLangpack > 0) {
            if (MessagesController.getGlobalMainSettings().getBoolean("langpack_patched" + currentLanguageName, false) || this.patching) {
                return;
            }
            this.patching = true;
            reloadCurrentRemoteLocale(i, null, true, new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$checkForcePatchLangpack$6(currentLanguageName, runnable);
                }
            });
        }
    }

    public void lambda$checkForcePatchLangpack$5(String str, Runnable runnable) {
        MessagesController.getGlobalMainSettings().edit().putBoolean("langpack_patched" + str, true).apply();
        if (runnable != null) {
            runnable.run();
        }
        this.patching = false;
    }

    private String getLocaleString(Locale locale) {
        if (locale == null) {
            return "en";
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder sb = new StringBuilder(11);
        sb.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            sb.append('_');
        }
        sb.append(country);
        if (variant.length() > 0) {
            sb.append('_');
        }
        sb.append(variant);
        return sb.toString();
    }

    public static String getSystemLocaleStringIso639() {
        Locale systemDefaultLocale = getInstance().getSystemDefaultLocale();
        if (systemDefaultLocale == null) {
            return "en";
        }
        String language = systemDefaultLocale.getLanguage();
        String country = systemDefaultLocale.getCountry();
        String variant = systemDefaultLocale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder sb = new StringBuilder(11);
        sb.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            sb.append(SignatureVisitor.SUPER);
        }
        sb.append(country);
        if (variant.length() > 0) {
            sb.append('_');
        }
        sb.append(variant);
        return sb.toString();
    }

    public static String getLocaleStringIso639() {
        LocaleInfo localeInfo = getInstance().currentLocaleInfo;
        if (localeInfo != null) {
            return localeInfo.getLangCode();
        }
        Locale locale = getInstance().currentLocale;
        if (locale == null) {
            return "en";
        }
        String language = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();
        if (language.length() == 0 && country.length() == 0) {
            return "en";
        }
        StringBuilder sb = new StringBuilder(11);
        sb.append(language);
        if (country.length() > 0 || variant.length() > 0) {
            sb.append(SignatureVisitor.SUPER);
        }
        sb.append(country);
        if (variant.length() > 0) {
            sb.append('_');
        }
        sb.append(variant);
        return sb.toString();
    }

    public static String getLocaleAlias(String str) {
        if (str == null) {
            return null;
        }
        switch (str) {
            case "he":
                return "iw";
            case "id":
                return "in";
            case "in":
                return "id";
            case "iw":
                return "he";
            case "ji":
                return "yi";
            case "jv":
                return "jw";
            case "jw":
                return "jv";
            case "nb":
                return "no";
            case "no":
                return "nb";
            case "tl":
                return "fil";
            case "yi":
                return "ji";
            case "fil":
                return "tl";
            default:
                return null;
        }
    }

    public boolean applyLanguageFile(File file, int i) {
        try {
            HashMap<String, String> localeFileStrings = getLocaleFileStrings(file);
            String str = localeFileStrings.get("LanguageName");
            String str2 = localeFileStrings.get("LanguageNameInEnglish");
            String str3 = localeFileStrings.get("LanguageCode");
            if (str != null && str.length() > 0 && str2 != null && str2.length() > 0 && str3 != null && str3.length() > 0 && !str.contains("&") && !str.contains("|") && !str2.contains("&") && !str2.contains("|") && !str3.contains("&") && !str3.contains("|") && !str3.contains("/") && !str3.contains("\\")) {
                File file2 = new File(ApplicationLoader.getFilesDirFixed(), str3.concat(".xml"));
                if (!AndroidUtilities.copyFile(file, file2)) {
                    return false;
                }
                LocaleInfo languageFromDict = getLanguageFromDict("local_" + str3.toLowerCase());
                if (languageFromDict == null) {
                    languageFromDict = new LocaleInfo();
                    languageFromDict.name = str;
                    languageFromDict.nameEnglish = str2;
                    String lowerCase = str3.toLowerCase();
                    languageFromDict.shortName = lowerCase;
                    languageFromDict.pluralLangCode = lowerCase;
                    languageFromDict.pathToFile = file2.getAbsolutePath();
                    this.languages.add(languageFromDict);
                    this.languagesDict.put(languageFromDict.getKey(), languageFromDict);
                    this.otherLanguages.add(languageFromDict);
                    saveOtherLanguages();
                }
                LocaleInfo localeInfo = languageFromDict;
                this.localeValues = localeFileStrings;
                applyLanguage(localeInfo, true, false, true, false, i, null);
                return true;
                return false;
            }
            return false;
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void saveOtherLanguages() {
        SharedPreferences.Editor editorEdit = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0).edit();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < this.otherLanguages.size(); i++) {
            String saveString = this.otherLanguages.get(i).getSaveString();
            if (saveString != null) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append(saveString);
            }
        }
        editorEdit.putString("locales", sb.toString());
        sb.setLength(0);
        for (int i2 = 0; i2 < this.remoteLanguages.size(); i2++) {
            String saveString2 = this.remoteLanguages.get(i2).getSaveString();
            if (saveString2 != null) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append(saveString2);
            }
        }
        editorEdit.putString("remote", sb.toString());
        sb.setLength(0);
        for (int i3 = 0; i3 < this.unofficialLanguages.size(); i3++) {
            String saveString3 = this.unofficialLanguages.get(i3).getSaveString();
            if (saveString3 != null) {
                if (sb.length() != 0) {
                    sb.append("&");
                }
                sb.append(saveString3);
            }
        }
        editorEdit.putString("unofficial", sb.toString());
        editorEdit.apply();
    }

    public boolean deleteLanguage(LocaleInfo localeInfo, int i) {
        if (localeInfo.pathToFile == null || (localeInfo.isRemote() && localeInfo.serverIndex != Integer.MAX_VALUE)) {
            return false;
        }
        if (this.currentLocaleInfo == localeInfo) {
            LocaleInfo languageFromDict = this.systemDefaultLocale.getLanguage() != null ? getLanguageFromDict(this.systemDefaultLocale.getLanguage()) : null;
            if (languageFromDict == null) {
                languageFromDict = getLanguageFromDict(getLocaleString(this.systemDefaultLocale));
            }
            if (languageFromDict == null) {
                languageFromDict = getLanguageFromDict("en");
            }
            applyLanguage(languageFromDict, true, false, i);
        }
        this.unofficialLanguages.remove(localeInfo);
        this.remoteLanguages.remove(localeInfo);
        this.remoteLanguagesDict.remove(localeInfo.getKey());
        this.otherLanguages.remove(localeInfo);
        this.languages.remove(localeInfo);
        this.languagesDict.remove(localeInfo.getKey());
        new File(localeInfo.pathToFile).delete();
        saveOtherLanguages();
        return true;
    }

    private void loadOtherLanguages() {
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("langconfig", 0);
        String string = sharedPreferences.getString("locales", null);
        if (!TextUtils.isEmpty(string)) {
            for (String str : string.split("&")) {
                LocaleInfo localeInfoCreateWithString = LocaleInfo.createWithString(str);
                if (localeInfoCreateWithString != null) {
                    this.otherLanguages.add(localeInfoCreateWithString);
                }
            }
        }
        String string2 = sharedPreferences.getString("remote", null);
        if (!TextUtils.isEmpty(string2)) {
            for (String str2 : string2.split("&")) {
                LocaleInfo localeInfoCreateWithString2 = LocaleInfo.createWithString(str2);
                localeInfoCreateWithString2.shortName = localeInfoCreateWithString2.shortName.replace("-", "_");
                if (!this.remoteLanguagesDict.containsKey(localeInfoCreateWithString2.getKey())) {
                    this.remoteLanguages.add(localeInfoCreateWithString2);
                    this.remoteLanguagesDict.put(localeInfoCreateWithString2.getKey(), localeInfoCreateWithString2);
                }
            }
        }
        String string3 = sharedPreferences.getString("unofficial", null);
        if (TextUtils.isEmpty(string3)) {
            return;
        }
        for (String str3 : string3.split("&")) {
            LocaleInfo localeInfoCreateWithString3 = LocaleInfo.createWithString(str3);
            if (localeInfoCreateWithString3 != null) {
                localeInfoCreateWithString3.shortName = localeInfoCreateWithString3.shortName.replace("-", "_");
                this.unofficialLanguages.add(localeInfoCreateWithString3);
            }
        }
    }

    private HashMap<String, String> getLocaleFileStrings(File file) {
        return getLocaleFileStrings(file, false);
    }

    private HashMap<String, String> getLocaleFileStrings(File file, boolean z) {
        this.reloadLastFile = false;
        FileLog.d("getLocaleFileStrings: reloadLastFile = false");
        try {
            if (!file.exists()) {
                return new HashMap<>();
            }
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                HashMap<String, String> localeStrings = parseLocaleStrings(fileInputStream, z);
                fileInputStream.close();
                return localeStrings;
            } catch (Throwable th) {
                try {
                    fileInputStream.close();
                } catch (Throwable th2) {
                    th.addSuppressed(th2);
                }
                throw th;
            }
        } catch (Exception e) {
            try {
                File file2 = new File(ApplicationLoader.getFilesDirFixed(), "malformed_locales/");
                file2.mkdirs();
                AndroidUtilities.copyFile(file, new File(file2, file.getName()));
            } catch (Exception unused) {
            }
            FileLog.e(e);
            FileLog.d("getLocaleFileStrings: error, reloadLastFile = true;");
            this.reloadLastFile = true;
            return new HashMap<>();
        }
    }

    private HashMap<String, String> getLocaleAssetStrings(String str) {
        try {
            InputStream inputStreamOpen = ApplicationLoader.applicationContext.getAssets().open(str);
            try {
                HashMap<String, String> localeStrings = parseLocaleStrings(inputStreamOpen, false);
                if (inputStreamOpen != null) {
                    inputStreamOpen.close();
                }
                return localeStrings;
            } catch (Throwable th) {
                if (inputStreamOpen != null) {
                    try {
                        inputStreamOpen.close();
                    } catch (Throwable th2) {
                        th.addSuppressed(th2);
                    }
                }
                throw th;
            }
        } catch (FileNotFoundException unused) {
            return new HashMap<>();
        } catch (Exception e) {
            FileLog.e(e);
            return new HashMap<>();
        }
    }

    private HashMap<String, String> parseLocaleStrings(InputStream inputStream, boolean z) throws XmlPullParserException, IOException {
        HashMap<String, String> map = new HashMap<>(10000);
        XmlPullParser xmlPullParserNewPullParser = Xml.newPullParser();
        xmlPullParserNewPullParser.setInput(inputStream, "UTF-8");
        String attributeValue = null;
        String name = null;
        String text = null;
        for (int eventType = xmlPullParserNewPullParser.getEventType(); eventType != 1; eventType = xmlPullParserNewPullParser.next()) {
            if (eventType == 2) {
                name = xmlPullParserNewPullParser.getName();
                if (xmlPullParserNewPullParser.getAttributeCount() > 0) {
                    attributeValue = xmlPullParserNewPullParser.getAttributeValue(0);
                }
            } else if (eventType == 4) {
                if (attributeValue != null && (text = xmlPullParserNewPullParser.getText()) != null) {
                    String strTrim = text.trim();
                    if (z) {
                        text = strTrim.replace("<", "&lt;").replace(">", "&gt;").replace("'", "\\'").replace("& ", "&amp; ");
                    } else {
                        String strReplace = strTrim.replace("\\n", "\n").replace("\\", _UrlKt.FRAGMENT_ENCODE_SET);
                        text = strReplace.replace("&lt;", "<");
                        if (!this.reloadLastFile && !text.equals(strReplace)) {
                            this.reloadLastFile = true;
                            FileLog.d("getLocaleFileStrings: value != old, reloadLastFile = true;");
                        }
                    }
                }
            } else if (eventType == 3) {
                attributeValue = null;
                name = null;
                text = null;
            }
            if (name != null && name.equals("string") && text != null && attributeValue != null && text.length() != 0 && attributeValue.length() != 0) {
                map.put(attributeValue, text);
                attributeValue = null;
                name = null;
                text = null;
            }
        }
        return map;
    }

    private void mergeExteraLocaleValues(HashMap<String, String> map, LocaleInfo localeInfo) {
        Iterator<String> it = getExteraLocaleAssetPaths(localeInfo).iterator();
        while (it.hasNext()) {
            map.putAll(getLocaleAssetStrings(it.next()));
        }
    }

    private LinkedHashSet<String> getExteraLocaleAssetPaths(LocaleInfo localeInfo) {
        LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();
        linkedHashSet.add("extera_locales/values/extera.xml");
        if (localeInfo != null) {
            addExteraLocaleAssetPaths(linkedHashSet, localeInfo.pluralLangCode);
            addExteraLocaleAssetPaths(linkedHashSet, localeInfo.baseLangCode);
            addExteraLocaleAssetPaths(linkedHashSet, localeInfo.shortName);
            return linkedHashSet;
        }
        Locale locale = this.currentLocale;
        if (locale != null) {
            addExteraLocaleAssetPaths(linkedHashSet, locale);
        }
        Locale locale2 = this.systemDefaultLocale;
        if (locale2 != null) {
            addExteraLocaleAssetPaths(linkedHashSet, locale2);
        }
        return linkedHashSet;
    }

    private void addExteraLocaleAssetPaths(LinkedHashSet<String> linkedHashSet, Locale locale) {
        if (locale == null) {
            return;
        }
        addExteraLocaleAssetPaths(linkedHashSet, locale.getLanguage());
        if (TextUtils.isEmpty(locale.getCountry())) {
            return;
        }
        addExteraLocaleAssetPaths(linkedHashSet, locale.getLanguage() + "_" + locale.getCountry());
    }

    private void addExteraLocaleAssetPaths(LinkedHashSet<String> linkedHashSet, String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        String[] strArrSplit = str.replace(SignatureVisitor.SUPER, '_').split("_");
        if (strArrSplit.length == 0 || TextUtils.isEmpty(strArrSplit[0])) {
            return;
        }
        String strNormalizeResourceLanguage = LocaleUtils.normalizeResourceLanguage(strArrSplit[0]);
        if (TextUtils.isEmpty(strNormalizeResourceLanguage)) {
            return;
        }
        linkedHashSet.add("extera_locales/values-" + strNormalizeResourceLanguage + "/extera.xml");
        if (strArrSplit.length <= 1 || TextUtils.isEmpty(strArrSplit[1])) {
            return;
        }
        linkedHashSet.add("extera_locales/values-" + strNormalizeResourceLanguage + "-r" + LocaleUtils.normalizeResourceRegion(strNormalizeResourceLanguage, strArrSplit[1]) + "/extera.xml");
    }

    public int applyLanguage(LocaleInfo localeInfo, boolean z, boolean z2, int i) {
        return applyLanguage(localeInfo, z, z2, false, false, i, null);
    }

    void lambda$applyLanguage$7(LocaleInfo localeInfo, int i, Runnable runnable) {
        applyRemoteLanguage(localeInfo, null, true, i, runnable);
    }

    public void lambda$saveRemoteLocaleStrings$10(int i, LocaleInfo localeInfo, TLRPC.TL_langPackDifference tL_langPackDifference, HashMap map, Runnable runnable) {
        String[] strArrSplit;
        Locale locale;
        if (i == 0) {
            localeInfo.version = tL_langPackDifference.version;
        } else {
            localeInfo.baseVersion = tL_langPackDifference.version;
        }
        saveOtherLanguages();
        try {
            if (this.currentLocaleInfo == localeInfo) {
                if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                    strArrSplit = localeInfo.pluralLangCode.split("_");
                } else if (!TextUtils.isEmpty(localeInfo.baseLangCode)) {
                    strArrSplit = localeInfo.baseLangCode.split("_");
                } else {
                    strArrSplit = localeInfo.shortName.split("_");
                }
                if (strArrSplit.length == 1) {
                    locale = new Locale(strArrSplit[0]);
                } else {
                    locale = new Locale(strArrSplit[0], strArrSplit[1]);
                }
                this.languageOverride = localeInfo.shortName;
                SharedPreferences.Editor editorEdit = MessagesController.getGlobalMainSettings().edit();
                editorEdit.putString("language", localeInfo.getKey());
                editorEdit.apply();
                this.localeValues = map;
                this.currentLocale = locale;
                this.currentLocaleInfo = localeInfo;
                if (!TextUtils.isEmpty(localeInfo.pluralLangCode)) {
                    this.currentPluralRules = this.allRules.get(this.currentLocaleInfo.pluralLangCode);
                }
                if (this.currentPluralRules == null) {
                    PluralRules pluralRules = this.allRules.get(this.currentLocale.getLanguage());
                    this.currentPluralRules = pluralRules;
                    if (pluralRules == null) {
                        this.currentPluralRules = this.allRules.get("en");
                    }
                }
                this.changingConfiguration = true;
                Locale.setDefault(this.currentLocale);
                Configuration configuration = new Configuration();
                configuration.locale = this.currentLocale;
                ApplicationLoader.applicationContext.getResources().updateConfiguration(configuration, ApplicationLoader.applicationContext.getResources().getDisplayMetrics());
                this.changingConfiguration = false;
                RestrictedLanguagesSelectActivity.invalidateRestrictedLanguages();
            } else {
                FileLog.d("saveRemoteLocaleStrings: currentLocaleInfo != localeInfo, do nothing");
            }
        } catch (Exception e) {
            FileLog.e(e);
            this.changingConfiguration = false;
        }
        recreateFormatters();
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.reloadInterface, new Object[0]);
        if (runnable != null) {
            runnable.run();
        }
    }

    public void loadRemoteLanguages(int i) {
        loadRemoteLanguages(i, true);
    }

    public void loadRemoteLanguages(final int i, final boolean z) {
        if (this.loadingRemoteLanguages) {
            return;
        }
        this.loadingRemoteLanguages = true;
        ConnectionsManager.getInstance(i).sendRequest(new TLRPC.TL_langpack_getLanguages(), new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$loadRemoteLanguages$12(z, i, tLObject, tL_error);
            }
        }, 8);
    }

    public void lambda$loadRemoteLanguages$11(TLObject tLObject, boolean z, int i) {
        this.loadingRemoteLanguages = false;
        Vector vector = (Vector) tLObject;
        int size = this.remoteLanguages.size();
        for (int i2 = 0; i2 < size; i2++) {
            this.remoteLanguages.get(i2).serverIndex = Integer.MAX_VALUE;
        }
        int size2 = vector.objects.size();
        for (int i3 = 0; i3 < size2; i3++) {
            TLRPC.TL_langPackLanguage tL_langPackLanguage = (TLRPC.TL_langPackLanguage) vector.objects.get(i3);
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("loaded lang " + tL_langPackLanguage.name);
            }
            LocaleInfo localeInfo = new LocaleInfo();
            localeInfo.nameEnglish = tL_langPackLanguage.name;
            localeInfo.name = tL_langPackLanguage.native_name;
            localeInfo.shortName = tL_langPackLanguage.lang_code.replace(SignatureVisitor.SUPER, '_').toLowerCase();
            String str = tL_langPackLanguage.base_lang_code;
            if (str != null) {
                localeInfo.baseLangCode = str.replace(SignatureVisitor.SUPER, '_').toLowerCase();
            } else {
                localeInfo.baseLangCode = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            localeInfo.pluralLangCode = tL_langPackLanguage.plural_code.replace(SignatureVisitor.SUPER, '_').toLowerCase();
            localeInfo.isRtl = tL_langPackLanguage.rtl;
            localeInfo.pathToFile = "remote";
            localeInfo.serverIndex = i3;
            LocaleInfo languageFromDict = getLanguageFromDict(localeInfo.getKey());
            if (languageFromDict == null) {
                this.languages.add(localeInfo);
                this.languagesDict.put(localeInfo.getKey(), localeInfo);
            } else {
                languageFromDict.nameEnglish = localeInfo.nameEnglish;
                languageFromDict.name = localeInfo.name;
                languageFromDict.baseLangCode = localeInfo.baseLangCode;
                languageFromDict.pluralLangCode = localeInfo.pluralLangCode;
                languageFromDict.pathToFile = localeInfo.pathToFile;
                languageFromDict.serverIndex = localeInfo.serverIndex;
                localeInfo = languageFromDict;
            }
            if (!this.remoteLanguagesDict.containsKey(localeInfo.getKey())) {
                this.remoteLanguages.add(localeInfo);
                this.remoteLanguagesDict.put(localeInfo.getKey(), localeInfo);
            }
        }
        int i4 = 0;
        while (i4 < this.remoteLanguages.size()) {
            LocaleInfo localeInfo2 = this.remoteLanguages.get(i4);
            if (localeInfo2.serverIndex == Integer.MAX_VALUE && localeInfo2 != this.currentLocaleInfo) {
                if (BuildVars.LOGS_ENABLED) {
                    FileLog.d("remove lang " + localeInfo2.getKey());
                }
                this.remoteLanguages.remove(i4);
                this.remoteLanguagesDict.remove(localeInfo2.getKey());
                this.languages.remove(localeInfo2);
                this.languagesDict.remove(localeInfo2.getKey());
                i4--;
            }
            i4++;
        }
        saveOtherLanguages();
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.suggestedLangpack, new Object[0]);
        if (z) {
            applyLanguage(this.currentLocaleInfo, true, false, i);
        }
    }

    private int applyRemoteLanguage(final LocaleInfo localeInfo, String str, boolean z, final int i, final Runnable runnable) {
        if (localeInfo == null || !(localeInfo.isRemote() || localeInfo.isUnofficial())) {
            return 0;
        }
        FileLog.d("applyRemoteLanguage " + str + " force=" + z + " currentAccount=" + i);
        final int[] iArr = {0};
        final int[] iArr2 = {0};
        final Runnable runnable2 = new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                LocaleController.m4440$r8$lambda$M8xnqQcNw5oaI4ZxKjhICarw5Y(iArr, iArr2, runnable);
            }
        };
        if (z) {
            patched(localeInfo.shortName);
        }
        if (localeInfo.hasBaseLang() && (str == null || str.equals(localeInfo.baseLangCode))) {
            if (localeInfo.baseVersion != 0 && !z) {
                if (localeInfo.hasBaseLang()) {
                    FileLog.d("applyRemoteLanguage getDifference of base");
                    TLRPC.TL_langpack_getDifference tL_langpack_getDifference = new TLRPC.TL_langpack_getDifference();
                    tL_langpack_getDifference.from_version = localeInfo.baseVersion;
                    tL_langpack_getDifference.lang_code = localeInfo.getBaseLangCode();
                    tL_langpack_getDifference.lang_pack = _UrlKt.FRAGMENT_ENCODE_SET;
                    iArr2[0] = iArr2[0] + 1;
                    ConnectionsManager.getInstance(i).sendRequest(tL_langpack_getDifference, new RequestDelegate() { 
                        @Override // org.telegram.tgnet.RequestDelegate
                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                            this.f$0.lambda$applyRemoteLanguage$15(localeInfo, i, runnable2, tLObject, tL_error);
                        }
                    }, 8);
                }
            } else {
                FileLog.d("applyRemoteLanguage getLangPack of base");
                TLRPC.TL_langpack_getLangPack tL_langpack_getLangPack = new TLRPC.TL_langpack_getLangPack();
                tL_langpack_getLangPack.lang_code = localeInfo.getBaseLangCode();
                iArr2[0] = iArr2[0] + 1;
                ConnectionsManager.getInstance(i).sendRequest(tL_langpack_getLangPack, new RequestDelegate() { 
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        this.f$0.lambda$applyRemoteLanguage$17(localeInfo, i, runnable2, tLObject, tL_error);
                    }
                }, 8);
            }
        }
        if (str != null && !str.equals(localeInfo.shortName)) {
            return 0;
        }
        if (localeInfo.version != 0 && !z) {
            FileLog.d("applyRemoteLanguage getDifference");
            TLRPC.TL_langpack_getDifference tL_langpack_getDifference2 = new TLRPC.TL_langpack_getDifference();
            tL_langpack_getDifference2.from_version = localeInfo.version;
            tL_langpack_getDifference2.lang_code = localeInfo.getLangCode();
            tL_langpack_getDifference2.lang_pack = _UrlKt.FRAGMENT_ENCODE_SET;
            iArr2[0] = iArr2[0] + 1;
            return ConnectionsManager.getInstance(i).sendRequest(tL_langpack_getDifference2, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$applyRemoteLanguage$19(localeInfo, i, runnable2, tLObject, tL_error);
                }
            }, 8);
        }
        for (int i2 = 0; i2 < 16; i2++) {
            ConnectionsManager.setLangCode(localeInfo.getLangCode());
        }
        FileLog.d("applyRemoteLanguage getLangPack");
        TLRPC.TL_langpack_getLangPack tL_langpack_getLangPack2 = new TLRPC.TL_langpack_getLangPack();
        tL_langpack_getLangPack2.lang_code = localeInfo.getLangCode();
        iArr2[0] = iArr2[0] + 1;
        return ConnectionsManager.getInstance(i).sendRequest(tL_langpack_getLangPack2, new RequestDelegate() { 
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$applyRemoteLanguage$21(localeInfo, i, runnable2, tLObject, tL_error);
            }
        }, 8);
    }

    public static void lambda$applyRemoteLanguage$17(final LocaleInfo localeInfo, final int i, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$applyRemoteLanguage$16(localeInfo, tLObject, i, runnable);
                }
            });
        }
    }

    public void lambda$applyRemoteLanguage$21(final LocaleInfo localeInfo, final int i, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$applyRemoteLanguage$20(localeInfo, tLObject, i, runnable);
                }
            });
        }
    }

    public String getTranslitString(String str) {
        return getTranslitString(str, true, false);
    }

    public String getTranslitString(String str, boolean z) {
        return getTranslitString(str, true, z);
    }

    public String getTranslitString(String str, boolean z, boolean z2) {
        String str2;
        String str3;
        String str4;
        String str5;
        String str6;
        if (str == null) {
            return null;
        }
        if (this.ruTranslitChars == null) {
            HashMap<String, String> map = new HashMap<>(33);
            this.ruTranslitChars = map;
            map.put("а", "a");
            this.ruTranslitChars.put("б", "b");
            this.ruTranslitChars.put("в", RegisterSpec.PREFIX);
            this.ruTranslitChars.put("г", ImageLoader.AUTOPLAY_FILTER);
            this.ruTranslitChars.put("д", "d");
            this.ruTranslitChars.put("е", "e");
            this.ruTranslitChars.put("ё", "yo");
            this.ruTranslitChars.put("ж", "zh");
            this.ruTranslitChars.put("з", "z");
            this.ruTranslitChars.put("и", "i");
            this.ruTranslitChars.put("й", "i");
            this.ruTranslitChars.put("к", "k");
            this.ruTranslitChars.put("л", "l");
            this.ruTranslitChars.put("м", "m");
            this.ruTranslitChars.put("н", "n");
            this.ruTranslitChars.put("о", "o");
            this.ruTranslitChars.put("п", "p");
            str4 = "r";
            this.ruTranslitChars.put("р", str4);
            this.ruTranslitChars.put("с", "s");
            this.ruTranslitChars.put("т", "t");
            str2 = "u";
            this.ruTranslitChars.put("у", str2);
            str3 = "s";
            this.ruTranslitChars.put("ф", "f");
            str5 = "h";
            this.ruTranslitChars.put("х", str5);
            str6 = "p";
            this.ruTranslitChars.put("ц", HlsSegmentFormat.TS);
            this.ruTranslitChars.put("ч", "ch");
            this.ruTranslitChars.put("ш", "sh");
            this.ruTranslitChars.put("щ", "sch");
            this.ruTranslitChars.put("ы", "i");
            this.ruTranslitChars.put("ь", _UrlKt.FRAGMENT_ENCODE_SET);
            this.ruTranslitChars.put("ъ", _UrlKt.FRAGMENT_ENCODE_SET);
            this.ruTranslitChars.put("э", "e");
            this.ruTranslitChars.put("ю", "yu");
            this.ruTranslitChars.put("я", "ya");
        } else {
            str2 = "u";
            str3 = "s";
            str4 = "r";
            str5 = "h";
            str6 = "p";
        }
        if (this.translitChars == null) {
            HashMap<String, String> map2 = new HashMap<>(488);
            this.translitChars = map2;
            map2.put("ȼ", "c");
            this.translitChars.put("ᶇ", "n");
            this.translitChars.put("ɖ", "d");
            this.translitChars.put("ỿ", "y");
            this.translitChars.put("ᴓ", "o");
            this.translitChars.put("ø", "o");
            this.translitChars.put("ḁ", "a");
            this.translitChars.put("ʯ", str5);
            this.translitChars.put("ŷ", "y");
            this.translitChars.put("ʞ", "k");
            this.translitChars.put("ừ", str2);
            String str7 = str2;
            this.translitChars.put("ꜳ", "aa");
            this.translitChars.put("ĳ", "ij");
            this.translitChars.put("ḽ", "l");
            this.translitChars.put("ɪ", "i");
            this.translitChars.put("ḇ", "b");
            this.translitChars.put("ʀ", str4);
            this.translitChars.put("ě", "e");
            this.translitChars.put("ﬃ", "ffi");
            this.translitChars.put("ơ", "o");
            this.translitChars.put("ⱹ", str4);
            this.translitChars.put("ồ", "o");
            this.translitChars.put("ǐ", "i");
            String str8 = str6;
            this.translitChars.put("ꝕ", str8);
            this.translitChars.put("ý", "y");
            this.translitChars.put("ḝ", "e");
            this.translitChars.put("ₒ", "o");
            this.translitChars.put("ⱥ", "a");
            this.translitChars.put("ʙ", "b");
            this.translitChars.put("ḛ", "e");
            this.translitChars.put("ƈ", "c");
            this.translitChars.put("ɦ", str5);
            this.translitChars.put("ᵬ", "b");
            String str9 = str5;
            String str10 = str3;
            this.translitChars.put("ṣ", str10);
            this.translitChars.put("đ", "d");
            this.translitChars.put("ỗ", "o");
            this.translitChars.put("ɟ", "j");
            this.translitChars.put("ẚ", "a");
            this.translitChars.put("ɏ", "y");
            this.translitChars.put("ʌ", RegisterSpec.PREFIX);
            this.translitChars.put("ꝓ", str8);
            this.translitChars.put("ﬁ", "fi");
            this.translitChars.put("ᶄ", "k");
            this.translitChars.put("ḏ", "d");
            this.translitChars.put("ᴌ", "l");
            this.translitChars.put("ė", "e");
            this.translitChars.put("ᴋ", "k");
            this.translitChars.put("ċ", "c");
            this.translitChars.put("ʁ", str4);
            this.translitChars.put("ƕ", "hv");
            this.translitChars.put("ƀ", "b");
            this.translitChars.put("ṍ", "o");
            this.translitChars.put("ȣ", "ou");
            this.translitChars.put("ǰ", "j");
            this.translitChars.put("ᶃ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ṋ", "n");
            this.translitChars.put("ɉ", "j");
            this.translitChars.put("ǧ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ǳ", "dz");
            this.translitChars.put("ź", "z");
            this.translitChars.put("ꜷ", "au");
            this.translitChars.put("ǖ", str7);
            this.translitChars.put("ᵹ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ȯ", "o");
            this.translitChars.put("ɐ", "a");
            this.translitChars.put("ą", "a");
            this.translitChars.put("õ", "o");
            this.translitChars.put("ɻ", str4);
            this.translitChars.put("ꝍ", "o");
            this.translitChars.put("ǟ", "a");
            this.translitChars.put("ȴ", "l");
            this.translitChars.put("ʂ", str10);
            this.translitChars.put("ﬂ", "fl");
            this.translitChars.put("ȉ", "i");
            this.translitChars.put("ⱻ", "e");
            this.translitChars.put("ṉ", "n");
            this.translitChars.put("ï", "i");
            this.translitChars.put("ñ", "n");
            this.translitChars.put("ᴉ", "i");
            this.translitChars.put("ʇ", "t");
            this.translitChars.put("ẓ", "z");
            this.translitChars.put("ỷ", "y");
            this.translitChars.put("ȳ", "y");
            this.translitChars.put("ṩ", str10);
            this.translitChars.put("ɽ", str4);
            this.translitChars.put("ĝ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ᴝ", str7);
            this.translitChars.put("ḳ", "k");
            this.translitChars.put("ꝫ", "et");
            this.translitChars.put("ī", "i");
            this.translitChars.put("ť", "t");
            this.translitChars.put("ꜿ", "c");
            this.translitChars.put("ʟ", "l");
            this.translitChars.put("ꜹ", "av");
            this.translitChars.put("û", str7);
            this.translitChars.put("æ", "ae");
            this.translitChars.put("ă", "a");
            this.translitChars.put("ǘ", str7);
            this.translitChars.put("ꞅ", str10);
            this.translitChars.put("ᵣ", str4);
            this.translitChars.put("ᴀ", "a");
            this.translitChars.put("ƃ", "b");
            this.translitChars.put("ḩ", str9);
            this.translitChars.put("ṧ", str10);
            this.translitChars.put("ₑ", "e");
            this.translitChars.put("ʜ", str9);
            this.translitChars.put("ẋ", "x");
            this.translitChars.put("ꝅ", "k");
            this.translitChars.put("ḋ", "d");
            this.translitChars.put("ƣ", "oi");
            this.translitChars.put("ꝑ", str8);
            this.translitChars.put("ħ", str9);
            this.translitChars.put("ⱴ", RegisterSpec.PREFIX);
            this.translitChars.put("ẇ", "w");
            this.translitChars.put("ǹ", "n");
            this.translitChars.put("ɯ", "m");
            this.translitChars.put("ɡ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ɴ", "n");
            this.translitChars.put("ᴘ", str8);
            this.translitChars.put("ᵥ", RegisterSpec.PREFIX);
            this.translitChars.put("ū", str7);
            this.translitChars.put("ḃ", "b");
            this.translitChars.put("ṗ", str8);
            this.translitChars.put("å", "a");
            this.translitChars.put("ɕ", "c");
            this.translitChars.put("ọ", "o");
            this.translitChars.put("ắ", "a");
            this.translitChars.put("ƒ", "f");
            this.translitChars.put("ǣ", "ae");
            this.translitChars.put("ꝡ", "vy");
            this.translitChars.put("ﬀ", "ff");
            this.translitChars.put("ᶉ", str4);
            this.translitChars.put("ô", "o");
            this.translitChars.put("ǿ", "o");
            this.translitChars.put("ṳ", str7);
            this.translitChars.put("ȥ", "z");
            this.translitChars.put("ḟ", "f");
            this.translitChars.put("ḓ", "d");
            this.translitChars.put("ȇ", "e");
            this.translitChars.put("ȕ", str7);
            this.translitChars.put("ȵ", "n");
            this.translitChars.put("ʠ", "q");
            this.translitChars.put("ấ", "a");
            this.translitChars.put("ǩ", "k");
            this.translitChars.put("ĩ", "i");
            this.translitChars.put("ṵ", str7);
            this.translitChars.put("ŧ", "t");
            this.translitChars.put("ɾ", str4);
            this.translitChars.put("ƙ", "k");
            this.translitChars.put("ṫ", "t");
            this.translitChars.put("ꝗ", "q");
            this.translitChars.put("ậ", "a");
            this.translitChars.put("ʄ", "j");
            this.translitChars.put("ƚ", "l");
            this.translitChars.put("ᶂ", "f");
            this.translitChars.put("ᵴ", str10);
            this.translitChars.put("ꞃ", str4);
            this.translitChars.put("ᶌ", RegisterSpec.PREFIX);
            this.translitChars.put("ɵ", "o");
            this.translitChars.put("ḉ", "c");
            this.translitChars.put("ᵤ", str7);
            this.translitChars.put("ẑ", "z");
            this.translitChars.put("ṹ", str7);
            this.translitChars.put("ň", "n");
            this.translitChars.put("ʍ", "w");
            this.translitChars.put("ầ", "a");
            this.translitChars.put("ǉ", "lj");
            this.translitChars.put("ɓ", "b");
            this.translitChars.put("ɼ", str4);
            this.translitChars.put("ò", "o");
            this.translitChars.put("ẘ", "w");
            this.translitChars.put("ɗ", "d");
            this.translitChars.put("ꜽ", "ay");
            this.translitChars.put("ư", str7);
            this.translitChars.put("ᶀ", "b");
            this.translitChars.put("ǜ", str7);
            this.translitChars.put("ẹ", "e");
            this.translitChars.put("ǡ", "a");
            this.translitChars.put("ɥ", str9);
            this.translitChars.put("ṏ", "o");
            this.translitChars.put("ǔ", str7);
            this.translitChars.put("ʎ", "y");
            this.translitChars.put("ȱ", "o");
            this.translitChars.put("ệ", "e");
            this.translitChars.put("ế", "e");
            this.translitChars.put("ĭ", "i");
            this.translitChars.put("ⱸ", "e");
            this.translitChars.put("ṯ", "t");
            this.translitChars.put("ᶑ", "d");
            this.translitChars.put("ḧ", str9);
            this.translitChars.put("ṥ", str10);
            this.translitChars.put("ë", "e");
            this.translitChars.put("ᴍ", "m");
            this.translitChars.put("ö", "o");
            this.translitChars.put("é", "e");
            this.translitChars.put("ı", "i");
            this.translitChars.put("ď", "d");
            this.translitChars.put("ᵯ", "m");
            this.translitChars.put("ỵ", "y");
            this.translitChars.put("ŵ", "w");
            this.translitChars.put("ề", "e");
            this.translitChars.put("ứ", str7);
            this.translitChars.put("ƶ", "z");
            this.translitChars.put("ĵ", "j");
            this.translitChars.put("ḍ", "d");
            this.translitChars.put("ŭ", str7);
            this.translitChars.put("ʝ", "j");
            this.translitChars.put("ê", "e");
            this.translitChars.put("ǚ", str7);
            this.translitChars.put("ġ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ṙ", str4);
            this.translitChars.put("ƞ", "n");
            this.translitChars.put("ḗ", "e");
            this.translitChars.put("ẝ", str10);
            this.translitChars.put("ᶁ", "d");
            this.translitChars.put("ķ", "k");
            this.translitChars.put("ᴂ", "ae");
            this.translitChars.put("ɘ", "e");
            this.translitChars.put("ợ", "o");
            this.translitChars.put("ḿ", "m");
            this.translitChars.put("ꜰ", "f");
            this.translitChars.put("ẵ", "a");
            this.translitChars.put("ꝏ", "oo");
            this.translitChars.put("ᶆ", "m");
            this.translitChars.put("ᵽ", str8);
            this.translitChars.put("ữ", str7);
            this.translitChars.put("ⱪ", "k");
            this.translitChars.put("ḥ", str9);
            this.translitChars.put("ţ", "t");
            this.translitChars.put("ᵱ", str8);
            this.translitChars.put("ṁ", "m");
            this.translitChars.put("á", "a");
            this.translitChars.put("ᴎ", "n");
            this.translitChars.put("ꝟ", RegisterSpec.PREFIX);
            this.translitChars.put("è", "e");
            this.translitChars.put("ᶎ", "z");
            this.translitChars.put("ꝺ", "d");
            this.translitChars.put("ᶈ", str8);
            this.translitChars.put("ɫ", "l");
            this.translitChars.put("ᴢ", "z");
            this.translitChars.put("ɱ", "m");
            this.translitChars.put("ṝ", str4);
            this.translitChars.put("ṽ", RegisterSpec.PREFIX);
            this.translitChars.put("ũ", str7);
            this.translitChars.put("ß", "ss");
            this.translitChars.put("ĥ", str9);
            this.translitChars.put("ᵵ", "t");
            this.translitChars.put("ʐ", "z");
            this.translitChars.put("ṟ", str4);
            this.translitChars.put("ɲ", "n");
            this.translitChars.put("à", "a");
            this.translitChars.put("ẙ", "y");
            this.translitChars.put("ỳ", "y");
            this.translitChars.put("ᴔ", "oe");
            this.translitChars.put("ₓ", "x");
            this.translitChars.put("ȗ", str7);
            this.translitChars.put("ⱼ", "j");
            this.translitChars.put("ẫ", "a");
            this.translitChars.put("ʑ", "z");
            this.translitChars.put("ẛ", str10);
            this.translitChars.put("ḭ", "i");
            String str11 = str4;
            this.translitChars.put("ꜵ", "ao");
            this.translitChars.put("ɀ", "z");
            this.translitChars.put("ÿ", "y");
            this.translitChars.put("ǝ", "e");
            this.translitChars.put("ǭ", "o");
            this.translitChars.put("ᴅ", "d");
            this.translitChars.put("ᶅ", "l");
            this.translitChars.put("ù", str7);
            this.translitChars.put("ạ", "a");
            this.translitChars.put("ḅ", "b");
            this.translitChars.put("ụ", str7);
            this.translitChars.put("ằ", "a");
            this.translitChars.put("ᴛ", "t");
            this.translitChars.put("ƴ", "y");
            this.translitChars.put("ⱦ", "t");
            this.translitChars.put("ⱡ", "l");
            this.translitChars.put("ȷ", "j");
            this.translitChars.put("ᵶ", "z");
            this.translitChars.put("ḫ", str9);
            this.translitChars.put("ⱳ", "w");
            this.translitChars.put("ḵ", "k");
            this.translitChars.put("ờ", "o");
            this.translitChars.put("î", "i");
            this.translitChars.put("ģ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ȅ", "e");
            this.translitChars.put("ȧ", "a");
            this.translitChars.put("ẳ", "a");
            this.translitChars.put("ɋ", "q");
            this.translitChars.put("ṭ", "t");
            this.translitChars.put("ꝸ", "um");
            this.translitChars.put("ᴄ", "c");
            this.translitChars.put("ẍ", "x");
            this.translitChars.put("ủ", str7);
            this.translitChars.put("ỉ", "i");
            this.translitChars.put("ᴚ", str11);
            this.translitChars.put("ś", str10);
            this.translitChars.put("ꝋ", "o");
            this.translitChars.put("ỹ", "y");
            this.translitChars.put("ṡ", str10);
            this.translitChars.put("ǌ", "nj");
            this.translitChars.put("ȁ", "a");
            this.translitChars.put("ẗ", "t");
            this.translitChars.put("ĺ", "l");
            this.translitChars.put("ž", "z");
            this.translitChars.put("ᵺ", "th");
            this.translitChars.put("ƌ", "d");
            this.translitChars.put("ș", str10);
            this.translitChars.put("š", str10);
            this.translitChars.put("ᶙ", str7);
            this.translitChars.put("ẽ", "e");
            this.translitChars.put("ẜ", str10);
            this.translitChars.put("ɇ", "e");
            this.translitChars.put("ṷ", str7);
            this.translitChars.put("ố", "o");
            this.translitChars.put("ȿ", str10);
            this.translitChars.put("ᴠ", RegisterSpec.PREFIX);
            this.translitChars.put("ꝭ", "is");
            this.translitChars.put("ᴏ", "o");
            this.translitChars.put("ɛ", "e");
            this.translitChars.put("ǻ", "a");
            this.translitChars.put("ﬄ", "ffl");
            this.translitChars.put("ⱺ", "o");
            this.translitChars.put("ȋ", "i");
            this.translitChars.put("ᵫ", "ue");
            this.translitChars.put("ȡ", "d");
            this.translitChars.put("ⱬ", "z");
            this.translitChars.put("ẁ", "w");
            this.translitChars.put("ᶏ", "a");
            this.translitChars.put("ꞇ", "t");
            this.translitChars.put("ğ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ɳ", "n");
            this.translitChars.put("ʛ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ᴜ", str7);
            this.translitChars.put("ẩ", "a");
            this.translitChars.put("ṅ", "n");
            this.translitChars.put("ɨ", "i");
            this.translitChars.put("ᴙ", str11);
            this.translitChars.put("ǎ", "a");
            this.translitChars.put("ſ", str10);
            this.translitChars.put("ȫ", "o");
            this.translitChars.put("ɿ", str11);
            this.translitChars.put("ƭ", "t");
            this.translitChars.put("ḯ", "i");
            this.translitChars.put("ǽ", "ae");
            this.translitChars.put("ⱱ", RegisterSpec.PREFIX);
            this.translitChars.put("ɶ", "oe");
            this.translitChars.put("ṃ", "m");
            this.translitChars.put("ż", "z");
            this.translitChars.put("ĕ", "e");
            this.translitChars.put("ꜻ", "av");
            this.translitChars.put("ở", "o");
            this.translitChars.put("ễ", "e");
            this.translitChars.put("ɬ", "l");
            this.translitChars.put("ị", "i");
            this.translitChars.put("ᵭ", "d");
            this.translitChars.put("ﬆ", "st");
            this.translitChars.put("ḷ", "l");
            this.translitChars.put("ŕ", str11);
            this.translitChars.put("ᴕ", "ou");
            this.translitChars.put("ʈ", "t");
            this.translitChars.put("ā", "a");
            this.translitChars.put("ḙ", "e");
            this.translitChars.put("ᴑ", "o");
            this.translitChars.put("ç", "c");
            this.translitChars.put("ᶊ", str10);
            this.translitChars.put("ặ", "a");
            this.translitChars.put("ų", str7);
            this.translitChars.put("ả", "a");
            this.translitChars.put("ǥ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ꝁ", "k");
            this.translitChars.put("ẕ", "z");
            this.translitChars.put("ŝ", str10);
            this.translitChars.put("ḕ", "e");
            this.translitChars.put("ɠ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ꝉ", "l");
            this.translitChars.put("ꝼ", "f");
            this.translitChars.put("ᶍ", "x");
            this.translitChars.put("ǒ", "o");
            this.translitChars.put("ę", "e");
            this.translitChars.put("ổ", "o");
            this.translitChars.put("ƫ", "t");
            this.translitChars.put("ǫ", "o");
            this.translitChars.put("i̇", "i");
            this.translitChars.put("ṇ", "n");
            this.translitChars.put("ć", "c");
            this.translitChars.put("ᵷ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ẅ", "w");
            this.translitChars.put("ḑ", "d");
            this.translitChars.put("ḹ", "l");
            this.translitChars.put("œ", "oe");
            this.translitChars.put("ᵳ", str11);
            this.translitChars.put("ļ", "l");
            this.translitChars.put("ȑ", str11);
            this.translitChars.put("ȭ", "o");
            this.translitChars.put("ᵰ", "n");
            this.translitChars.put("ᴁ", "ae");
            this.translitChars.put("ŀ", "l");
            this.translitChars.put("ä", "a");
            this.translitChars.put("ƥ", str8);
            this.translitChars.put("ỏ", "o");
            this.translitChars.put("į", "i");
            this.translitChars.put("ȓ", str11);
            this.translitChars.put("ǆ", "dz");
            this.translitChars.put("ḡ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ṻ", str7);
            this.translitChars.put("ō", "o");
            this.translitChars.put("ľ", "l");
            this.translitChars.put("ẃ", "w");
            this.translitChars.put("ț", "t");
            this.translitChars.put("ń", "n");
            this.translitChars.put("ɍ", str11);
            this.translitChars.put("ȃ", "a");
            this.translitChars.put("ü", str7);
            this.translitChars.put("ꞁ", "l");
            this.translitChars.put("ᴐ", "o");
            this.translitChars.put("ớ", "o");
            this.translitChars.put("ᴃ", "b");
            this.translitChars.put("ɹ", str11);
            this.translitChars.put("ᵲ", str11);
            this.translitChars.put("ʏ", "y");
            this.translitChars.put("ᵮ", "f");
            this.translitChars.put("ⱨ", str9);
            this.translitChars.put("ŏ", "o");
            this.translitChars.put("ú", str7);
            this.translitChars.put("ṛ", str11);
            this.translitChars.put("ʮ", str9);
            this.translitChars.put("ó", "o");
            this.translitChars.put("ů", str7);
            this.translitChars.put("ỡ", "o");
            this.translitChars.put("ṕ", str8);
            this.translitChars.put("ᶖ", "i");
            this.translitChars.put("ự", str7);
            this.translitChars.put("ã", "a");
            this.translitChars.put("ᵢ", "i");
            this.translitChars.put("ṱ", "t");
            this.translitChars.put("ể", "e");
            this.translitChars.put("ử", str7);
            this.translitChars.put("í", "i");
            this.translitChars.put("ɔ", "o");
            this.translitChars.put("ɺ", str11);
            this.translitChars.put("ɢ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ř", str11);
            this.translitChars.put("ẖ", str9);
            this.translitChars.put("ű", str7);
            this.translitChars.put("ȍ", "o");
            this.translitChars.put("ḻ", "l");
            this.translitChars.put("ḣ", str9);
            this.translitChars.put("ȶ", "t");
            this.translitChars.put("ņ", "n");
            this.translitChars.put("ᶒ", "e");
            this.translitChars.put("ì", "i");
            this.translitChars.put("ẉ", "w");
            this.translitChars.put("ē", "e");
            this.translitChars.put("ᴇ", "e");
            this.translitChars.put("ł", "l");
            this.translitChars.put("ộ", "o");
            this.translitChars.put("ɭ", "l");
            this.translitChars.put("ẏ", "y");
            this.translitChars.put("ᴊ", "j");
            this.translitChars.put("ḱ", "k");
            this.translitChars.put("ṿ", RegisterSpec.PREFIX);
            this.translitChars.put("ȩ", "e");
            this.translitChars.put("â", "a");
            this.translitChars.put("ş", str10);
            this.translitChars.put("ŗ", str11);
            this.translitChars.put("ʋ", RegisterSpec.PREFIX);
            this.translitChars.put("ₐ", "a");
            this.translitChars.put("ↄ", "c");
            this.translitChars.put("ᶓ", "e");
            this.translitChars.put("ɰ", "m");
            this.translitChars.put("ᴡ", "w");
            this.translitChars.put("ȏ", "o");
            this.translitChars.put("č", "c");
            this.translitChars.put("ǵ", ImageLoader.AUTOPLAY_FILTER);
            this.translitChars.put("ĉ", "c");
            this.translitChars.put("ᶗ", "o");
            this.translitChars.put("ꝃ", "k");
            this.translitChars.put("ꝙ", "q");
            this.translitChars.put("ṑ", "o");
            this.translitChars.put("ꜱ", str10);
            this.translitChars.put("ṓ", "o");
            this.translitChars.put("ȟ", str9);
            this.translitChars.put("ő", "o");
            this.translitChars.put("ꜩ", "tz");
            this.translitChars.put("ẻ", "e");
            this.translitChars.put("і", "i");
            this.translitChars.put("ї", "i");
        }
        StringBuilder sb = new StringBuilder(str.length());
        int length = str.length();
        int i = 0;
        boolean z3 = false;
        while (i < length) {
            int i2 = i + 1;
            String strSubstring = str.substring(i, i2);
            if (z2) {
                String lowerCase = strSubstring.toLowerCase();
                z3 = !strSubstring.equals(lowerCase);
                strSubstring = lowerCase;
            }
            String upperCase = this.translitChars.get(strSubstring);
            if (upperCase == null && z) {
                upperCase = this.ruTranslitChars.get(strSubstring);
            }
            if (upperCase != null) {
                if (z2 && z3) {
                    if (upperCase.length() > 1) {
                        upperCase = upperCase.substring(0, 1).toUpperCase() + upperCase.substring(1);
                    } else {
                        upperCase = upperCase.toUpperCase();
                    }
                }
                sb.append(upperCase);
            } else {
                if (z2) {
                    char cCharAt = strSubstring.charAt(0);
                    if ((cCharAt < 'a' || cCharAt > 'z' || cCharAt < '0' || cCharAt > '9') && cCharAt != ' ' && cCharAt != '\'' && cCharAt != ',' && cCharAt != '.' && cCharAt != '&' && cCharAt != '-' && cCharAt != '/') {
                        return null;
                    }
                    if (z3) {
                        strSubstring = strSubstring.toUpperCase();
                    }
                }
                sb.append(strSubstring);
            }
            i = i2;
        }
        return sb.toString();
    }

    public static class PluralRules_Slovenian extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i2 == 1) {
                return 2;
            }
            if (i2 == 2) {
                return 4;
            }
            return (i2 < 3 || i2 > 4) ? 0 : 8;
        }
    }

    public static class PluralRules_Romanian extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 1) {
                return 2;
            }
            if (i != 0) {
                return (i2 < 1 || i2 > 19) ? 0 : 8;
            }
            return 8;
        }
    }

    public static class PluralRules_Polish extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i == 1) {
                return 2;
            }
            if (i3 >= 2 && i3 <= 4 && (i2 < 12 || i2 > 14)) {
                return 8;
            }
            if (i3 >= 0 && i3 <= 1) {
                return 16;
            }
            if (i3 < 5 || i3 > 9) {
                return (i2 < 12 || i2 > 14) ? 0 : 16;
            }
            return 16;
        }
    }

    public static class PluralRules_Maltese extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 1) {
                return 2;
            }
            if (i == 0) {
                return 8;
            }
            if (i2 < 2 || i2 > 10) {
                return (i2 < 11 || i2 > 19) ? 0 : 16;
            }
            return 8;
        }
    }

    public static class PluralRules_Macedonian extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            return (i % 10 != 1 || i == 11) ? 0 : 2;
        }
    }

    public static class PluralRules_Lithuanian extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i3 == 1 && (i2 < 11 || i2 > 19)) {
                return 2;
            }
            if (i3 < 2 || i3 > 9) {
                return 0;
            }
            return (i2 < 11 || i2 > 19) ? 8 : 0;
        }
    }

    public static class PluralRules_Latvian extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            if (i == 0) {
                return 1;
            }
            return (i % 10 != 1 || i % 100 == 11) ? 0 : 2;
        }
    }

    public static class PluralRules_Balkan extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i3 == 1 && i2 != 11) {
                return 2;
            }
            if (i3 >= 2 && i3 <= 4 && (i2 < 12 || i2 > 14)) {
                return 8;
            }
            if (i3 == 0) {
                return 16;
            }
            if (i3 < 5 || i3 > 9) {
                return (i2 < 11 || i2 > 14) ? 0 : 16;
            }
            return 16;
        }
    }

    public static class PluralRules_Serbian extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            int i3 = i % 10;
            if (i3 == 1 && i2 != 11) {
                return 2;
            }
            if (i3 < 2 || i3 > 4) {
                return 0;
            }
            return (i2 < 12 || i2 > 14) ? 8 : 0;
        }
    }

    public static class PluralRules_Arabic extends PluralRules {
        @Override 
        public int quantityForNumber(int i) {
            int i2 = i % 100;
            if (i == 0) {
                return 1;
            }
            if (i == 1) {
                return 2;
            }
            if (i == 2) {
                return 4;
            }
            if (i2 < 3 || i2 > 10) {
                return (i2 < 11 || i2 > 99) ? 0 : 16;
            }
            return 8;
        }
    }

    public static String addNbsp(String str) {
        return str.replace(' ', Typography.nbsp);
    }

    public static void resetImperialSystemType() {
        useImperialSystemType = null;
    }

    public static boolean getUseImperialSystemType() {
        ensureImperialSystemInit();
        return useImperialSystemType.booleanValue();
    }

    public static void ensureImperialSystemInit() {
        if (useImperialSystemType != null) {
            return;
        }
        int i = SharedConfig.distanceSystemType;
        boolean z = true;
        if (i == 0) {
            try {
                TelephonyManager telephonyManager = (TelephonyManager) ApplicationLoader.applicationContext.getSystemService("phone");
                if (telephonyManager != null) {
                    String upperCase = telephonyManager.getSimCountryIso().toUpperCase();
                    if (!"US".equals(upperCase) && !"GB".equals(upperCase) && !"MM".equals(upperCase) && !"LR".equals(upperCase)) {
                        z = false;
                    }
                    useImperialSystemType = Boolean.valueOf(z);
                    return;
                }
                return;
            } catch (Exception e) {
                useImperialSystemType = Boolean.FALSE;
                FileLog.e(e);
                return;
            }
        }
        useImperialSystemType = Boolean.valueOf(i == 2);
    }

    public static String formatDistance(float f, int i) {
        return formatDistance(f, i, null);
    }

    private boolean shouldReinstallLangpack(String str) {
        int iCalculateTranslatedCount;
        int i = MessagesController.getInstance(UserConfig.selectedAccount).checkResetLangpack;
        if (i <= 0) {
            return false;
        }
        if (MessagesController.getGlobalMainSettings().getBoolean("lngpack_patched_" + str, false) || (iCalculateTranslatedCount = calculateTranslatedCount(this.localeValues)) >= i) {
            return false;
        }
        FileLog.e("reinstalling " + str + " langpack because of patch (" + iCalculateTranslatedCount + " keys, must be at least " + i + ")");
        patched(str);
        return true;
    }

    public static String formatDistance(float f, int i, Boolean bool) {
        String str;
        String str2;
        ensureImperialSystemInit();
        if ((bool == null || !bool.booleanValue()) && !(bool == null && useImperialSystemType.booleanValue())) {
            if (f < 1000.0f) {
                if (i == 0) {
                    return formatString("MetersAway2", R.string.MetersAway2, String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf((int) Math.max(1.0f, f))));
                }
                if (i == 1) {
                    return formatString("MetersFromYou2", R.string.MetersFromYou2, String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf((int) Math.max(1.0f, f))));
                }
                return formatString("MetersShort", R.string.MetersShort, String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf((int) Math.max(1.0f, f))));
            }
            if (f % 1000.0f != 0.0f) {
                str = String.format("%.2f", Float.valueOf(f / 1000.0f));
            } else {
                str = String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf((int) (f / 1000.0f)));
            }
            if (i == 0) {
                return formatString("KMetersAway2", R.string.KMetersAway2, str);
            }
            if (i == 1) {
                return formatString("KMetersFromYou2", R.string.KMetersFromYou2, str);
            }
            return formatString("KMetersShort", R.string.KMetersShort, str);
        }
        float f2 = f * 3.28084f;
        if (f2 < 1000.0f) {
            if (i == 0) {
                return formatString("FootsAway", R.string.FootsAway, String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf((int) Math.max(1.0f, f2))));
            }
            if (i == 1) {
                return formatString("FootsFromYou", R.string.FootsFromYou, String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf((int) Math.max(1.0f, f2))));
            }
            return formatString("FootsShort", R.string.FootsShort, String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf((int) Math.max(1.0f, f2))));
        }
        if (f2 % 5280.0f != 0.0f) {
            str2 = String.format("%.2f", Float.valueOf(f2 / 5280.0f));
        } else {
            str2 = String.format(TimeModel.NUMBER_FORMAT, Integer.valueOf((int) (f2 / 5280.0f)));
        }
        if (i == 0) {
            return formatString("MilesAway", R.string.MilesAway, str2);
        }
        if (i == 1) {
            return formatString("MilesFromYou", R.string.MilesFromYou, str2);
        }
        return formatString("MilesShort", R.string.MilesShort, str2);
    }

    private void patched(String str) {
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("set as patched " + str + " langpack");
        }
        MessagesController.getGlobalMainSettings().edit().putBoolean("lngpack_patched_" + str, true).apply();
    }

    public static String getTimeZoneName(String str, boolean z) {
        TimeZone timeZone = DesugarTimeZone.getTimeZone(str);
        if (timeZone == null) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        String displayName = timeZone.getDisplayName(true, 0, getInstance().getCurrentLocale());
        if (z) {
            String displayName2 = timeZone.getDisplayName(true, 1, getInstance().getCurrentLocale());
            if (!TextUtils.equals(displayName2, displayName)) {
                return displayName2 + ", " + displayName;
            }
        }
        return displayName;
    }

    public static CharSequence getCountryWithFlag(String str, int i) {
        return getCountryWithFlag(str, i, R.string.Fragment);
    }

    public static CharSequence getCountryWithFlag(String str, int i, int i2) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        String languageFlag = getLanguageFlag(str);
        if (!TextUtils.isEmpty(languageFlag)) {
            spannableStringBuilder.append((CharSequence) languageFlag).append((CharSequence) " ");
        }
        String countryName = getCountryName(str, i2);
        if (!TextUtils.isEmpty(countryName)) {
            spannableStringBuilder.append((CharSequence) countryName);
        } else {
            spannableStringBuilder.append((CharSequence) str);
        }
        TextPaint textPaint = new TextPaint();
        textPaint.setTextSize(AndroidUtilities.dp(i));
        return Emoji.replaceEmoji(spannableStringBuilder, textPaint.getFontMetricsInt(), true);
    }

    public static String getCountryName(String str) {
        return getCountryName(str, R.string.Fragment);
    }

    public static String getCountryName(String str, int i) {
        if (str != null && str.equalsIgnoreCase("ft")) {
            return getString(i);
        }
        try {
            return new Locale(_UrlKt.FRAGMENT_ENCODE_SET, str).getDisplayCountry(getInstance().getCurrentLocale());
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static String formatEntityFormattedDate(TLRPC.TL_messageEntityFormattedDate tL_messageEntityFormattedDate) {
        return formatEntityFormattedDate(tL_messageEntityFormattedDate, System.currentTimeMillis(), Locale.getDefault(), false);
    }

    public static String formatEntityFormattedDate(TLRPC.TL_messageEntityFormattedDate tL_messageEntityFormattedDate, boolean z) {
        return formatEntityFormattedDate(tL_messageEntityFormattedDate, System.currentTimeMillis(), Locale.getDefault(), z);
    }

    private static String formatEntityFormattedDate(TLRPC.TL_messageEntityFormattedDate tL_messageEntityFormattedDate, long j, Locale locale, boolean z) {
        String str;
        String string;
        String str2;
        long j2 = ((long) tL_messageEntityFormattedDate.date) * 1000;
        if (tL_messageEntityFormattedDate.relative && !z) {
            return formatEntityFormattedDateRelative(j2, j, locale);
        }
        boolean z2 = (tL_messageEntityFormattedDate.flags == 0) | z;
        boolean z3 = tL_messageEntityFormattedDate.day_of_week;
        String str3 = _UrlKt.FRAGMENT_ENCODE_SET;
        String str4 = z3 ? getInstance().getFormatterWeekLong().format(j2) : _UrlKt.FRAGMENT_ENCODE_SET;
        if (z2) {
            str = getInstance().getFormatterGiveawayCard().format(j2);
        } else if (tL_messageEntityFormattedDate.long_date) {
            str = getInstance().getChatFullDate().format(j2);
        } else {
            str = tL_messageEntityFormattedDate.short_date ? getInstance().getFormatterYear().format(j2) : _UrlKt.FRAGMENT_ENCODE_SET;
        }
        if (z2) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(j2);
            if (calendar.get(13) != 0) {
                str2 = getInstance().getFormatterDayWithSeconds().format(j2);
            } else {
                str2 = getInstance().getFormatterDay().format(j2);
            }
            str3 = str2;
        } else if (tL_messageEntityFormattedDate.long_time) {
            str3 = getInstance().getFormatterDayWithSeconds().format(j2);
        } else if (tL_messageEntityFormattedDate.short_time) {
            str3 = getInstance().getFormatterDay().format(j2);
        }
        boolean zIsEmpty = TextUtils.isEmpty(str4);
        boolean zIsEmpty2 = TextUtils.isEmpty(str);
        boolean zIsEmpty3 = TextUtils.isEmpty(str3);
        if (zIsEmpty2 && zIsEmpty3) {
            return str4;
        }
        if (zIsEmpty2 || zIsEmpty3) {
            if (zIsEmpty2) {
                str = str3;
            }
            string = str;
        } else {
            string = formatString(R.string.formatDateAtTime, str, str3);
        }
        return !zIsEmpty ? formatString(R.string.RelativeDateFormatterWeek2, str4, string) : string;
    }

    private static String formatEntityFormattedDateRelative(long j, long j2, Locale locale) {
        return RelativeIcu.format(j - j2, locale);
    }

    public static final class RelativeIcu {
        private RelativeIcu() {
        }

        public static String format(long j, Locale locale) {
            RelativeDateTimeFormatter.RelativeUnit relativeUnit;
            RelativeDateTimeFormatter.Direction direction;
            RelativeDateTimeFormatter relativeDateTimeFormatter = RelativeDateTimeFormatter.getInstance(locale);
            boolean z = j > 0;
            long jMax = Math.max(1L, Math.round(Math.abs(j) / 1000.0d));
            if (jMax < 60) {
                relativeUnit = RelativeDateTimeFormatter.RelativeUnit.SECONDS;
            } else if (jMax < 3600) {
                relativeUnit = RelativeDateTimeFormatter.RelativeUnit.MINUTES;
                jMax = Math.round(jMax / 60.0d);
            } else if (jMax < 86400) {
                relativeUnit = RelativeDateTimeFormatter.RelativeUnit.HOURS;
                jMax = Math.round(jMax / 3600.0d);
            } else if (jMax < 2592000) {
                relativeUnit = RelativeDateTimeFormatter.RelativeUnit.DAYS;
                jMax = Math.round(jMax / 86400.0d);
            } else if (jMax < 31536000) {
                relativeUnit = RelativeDateTimeFormatter.RelativeUnit.MONTHS;
                jMax = Math.round(jMax / 2592000.0d);
            } else {
                relativeUnit = RelativeDateTimeFormatter.RelativeUnit.YEARS;
                jMax = Math.round(jMax / 3.1536E7d);
            }
            if (z) {
                direction = RelativeDateTimeFormatter.Direction.NEXT;
            } else {
                direction = RelativeDateTimeFormatter.Direction.LAST;
            }
            return relativeDateTimeFormatter.format(jMax, direction, relativeUnit);
        }
    }
}
