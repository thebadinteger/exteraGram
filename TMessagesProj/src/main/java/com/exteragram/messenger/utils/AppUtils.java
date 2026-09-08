package com.exteragram.messenger.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import androidx.annotation.Keep;
import c.f$$ExternalSyntheticBUOutline1;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.util.Calendar;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildConfig;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.Theme;

public class AppUtils {
    private static Gson gson;

    public static Gson getGson() {
        if (gson == null) {
            gson = new GsonBuilder().setPrettyPrinting().serializeSpecialFloatingPointValues().addSerializationExclusionStrategy(new ExclusionStrategy() { 
                @Override // com.google.gson.ExclusionStrategy
                public boolean shouldSkipField(FieldAttributes fieldAttributes) {
                    if (fieldAttributes.getDeclaringClass().getPackage() == null) {
                        return false;
                    }
                    String name = fieldAttributes.getDeclaringClass().getPackage().getName();
                    return name.startsWith("android.") || name.startsWith("androidx.");
                }

                @Override // com.google.gson.ExclusionStrategy
                public boolean shouldSkipClass(Class<?> cls) {
                    if (cls.getPackage() == null) {
                        return false;
                    }
                    String name = cls.getPackage().getName();
                    return name.startsWith("android.") || name.startsWith("androidx.");
                }
            }).create();
        }
        return gson;
    }

    public static void ensureRunningOnUi(Runnable runnable) {
        if (Thread.currentThread() != Looper.getMainLooper().getThread()) {
            AndroidUtilities.runOnUIThread(runnable);
        } else {
            runnable.run();
        }
    }

    public static int getNotificationColor() {
        int accentColor = Theme.getActiveTheme().hasAccentColors() ? Theme.getActiveTheme().getAccentColor(Theme.getActiveTheme().currentAccentId) : 0;
        if (accentColor == 0) {
            accentColor = Theme.getColor(Theme.key_actionBarDefault) | (-16777216);
        }
        float fComputePerceivedBrightness = AndroidUtilities.computePerceivedBrightness(accentColor);
        return (fComputePerceivedBrightness >= 0.721f || fComputePerceivedBrightness <= 0.279f) ? Theme.getColor(Theme.key_windowBackgroundWhiteBlueHeader) | (-16777216) : accentColor;
    }

    public static boolean isWinter() {
        int i = Calendar.getInstance().get(2);
        return i == 11 || i == 0 || i == 1;
    }

    public static int getSwipeVelocity() {
        Point point = AndroidUtilities.displaySize;
        return point.x > point.y ? 1250 : 850;
    }

    public static boolean isAppModified() {
        try {
            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 64);
            return (BuildConfig.APPLICATION_ID.equals(packageInfo.packageName) && "VdBS+IkXbbu+mQuHS4vyXw==".equals(Base64.encodeToString(MessageDigest.getInstance("MD5").digest(packageInfo.signatures[0].toByteArray()), 0).trim())) ? false : true;
        } catch (Exception e) {
            FileLog.e(e);
            return true;
        }
    }

    @Keep
    public static void log(String str) {
        logInternal(str, null, 5);
    }

    @Keep
    public static void log(Throwable th) {
        logInternal(_UrlKt.FRAGMENT_ENCODE_SET, th, 5);
    }

    @Keep
    public static void log(String str, Throwable th) {
        logInternal(str, th, 5);
    }

    private static void logInternal(String str, Throwable th, int i) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement stackTraceElement = stackTrace[Math.max(3, Math.min(i, stackTrace.length - 1))];
        String className = stackTraceElement.getClassName();
        if (className.contains(".")) {
            className = className.substring(className.lastIndexOf(46) + 1);
        }
        if (className.contains("$")) {
            className = className.substring(className.lastIndexOf(36) + 1);
        }
        String str2 = "[" + className + "]";
        String str3 = String.format("[%s] %s", stackTraceElement.getMethodName(), str);
        if (th != null) {
            Log.e(str2, str3, th);
        } else {
            Log.d(str2, str3);
        }
    }

    @Keep
    public static void printObjectDetails(Object obj) {
        if (obj == null) {
            return;
        }
        try {
            logInternal(obj.getClass().getName() + ": " + getGson().toJson(obj), null, 6);
        } catch (Exception e) {
            logInternal(obj.getClass().getName(), e, 6);
        }
    }

    @Keep
    public static Object getPrivateField(Object obj, String str) {
        try {
            Class<?> superclass = obj.getClass();
            Field declaredField = null;
            while (superclass != null) {
                try {
                    declaredField = superclass.getDeclaredField(str);
                } catch (NoSuchFieldException unused) {
                    superclass = superclass.getSuperclass();
                }
            }
            if (declaredField != null) {
                declaredField.setAccessible(true);
                return declaredField.get(obj);
            }
        } catch (Exception e) {
            logInternal(obj.getClass().getName(), e, 6);
        }
        return null;
    }

    @Keep
    public static void setPrivateField(Object obj, String str, Object obj2) {
        try {
            Class<?> superclass = obj.getClass();
            Field declaredField = null;
            while (superclass != null) {
                try {
                    declaredField = superclass.getDeclaredField(str);
                } catch (NoSuchFieldException unused) {
                    superclass = superclass.getSuperclass();
                }
            }
            if (declaredField != null) {
                declaredField.setAccessible(true);
                declaredField.set(obj, obj2);
            }
        } catch (Exception e) {
            logInternal(obj.getClass().getName(), e, 6);
        }
    }

    @Keep
    public static Object getPrivateStaticField(Class<?> cls, String str) {
        Class<?> superclass = cls;
        Field declaredField = null;
        while (superclass != null) {
            try {
                try {
                    declaredField = superclass.getDeclaredField(str);
                } catch (NoSuchFieldException unused) {
                    superclass = superclass.getSuperclass();
                }
            } catch (Exception e) {
                logInternal(cls.getName(), e, 6);
            }
        }
        if (declaredField != null) {
            declaredField.setAccessible(true);
            return declaredField.get(null);
        }
        return null;
    }

    @Keep
    public static void setPrivateStaticField(Class<?> cls, String str, Object obj) {
        Class<?> superclass = cls;
        Field declaredField = null;
        while (superclass != null) {
            try {
                try {
                    declaredField = superclass.getDeclaredField(str);
                } catch (NoSuchFieldException unused) {
                    superclass = superclass.getSuperclass();
                }
            } catch (Exception e) {
                logInternal(cls.getName(), e, 6);
                return;
            }
        }
        if (declaredField != null) {
            declaredField.setAccessible(true);
            declaredField.set(null, obj);
        }
    }

    public static String stackTraceToString(Throwable th) {
        StringWriter stringWriter = new StringWriter();
        th.printStackTrace(new PrintWriter(stringWriter));
        return stringWriter.toString();
    }

    public static String getVersionText() {
        StringBuilder sb = new StringBuilder();
        sb.append(LocaleUtils.getAppName());
        sb.append(" ");
        if (BuildVars.IS_LITE_VERSION) {
            sb.append("Lite ");
        }
        sb.append(BuildVars.BUILD_VERSION_STRING);
        try {
            PackageInfo packageInfo = ApplicationLoader.applicationContext.getPackageManager().getPackageInfo(ApplicationLoader.applicationContext.getPackageName(), 0);
            sb.append(" (");
            sb.append(packageInfo.versionCode);
            sb.append(")");
        } catch (PackageManager.NameNotFoundException e) {
            FileLog.e(e);
        }
        if (isAppModified()) {
            sb.append("\nbased on @exteraGram");
        }
        return sb.toString();
    }

    public static boolean compareVersions(String str, String str2, String str3) {
        int iCompareVersionValues = compareVersionValues(str2, str3);
        str.getClass();
        switch (str) {
            case "<":
                return iCompareVersionValues < 0;
            case ">":
                return iCompareVersionValues > 0;
            case "<=":
                return iCompareVersionValues <= 0;
            case "==":
                return iCompareVersionValues == 0;
            case ">=":
                return iCompareVersionValues >= 0;
            default:
                f$$ExternalSyntheticBUOutline1.m("Unsupported operator: ".concat(str));
                return false;
        }
    }

    public static boolean compareVersions(String str, int i, int i2) {
        int iCompare = Integer.compare(i, i2);
        str.getClass();
        switch (str) {
            case "<":
                return iCompare < 0;
            case ">":
                return iCompare > 0;
            case "<=":
                return iCompare <= 0;
            case "==":
                return iCompare == 0;
            case ">=":
                return iCompare >= 0;
            default:
                f$$ExternalSyntheticBUOutline1.m("Unsupported operator: ".concat(str));
                return false;
        }
    }

    public static int compareVersionValues(String str, String str2) {
        String[] strArrSplit = str.split("\\.");
        String[] strArrSplit2 = str2.split("\\.");
        int iMax = Math.max(strArrSplit.length, strArrSplit2.length);
        int i = 0;
        while (i < iMax) {
            int i2 = i < strArrSplit.length ? Integer.parseInt(strArrSplit[i]) : 0;
            int i3 = i < strArrSplit2.length ? Integer.parseInt(strArrSplit2[i]) : 0;
            if (i2 != i3) {
                return Integer.compare(i2, i3);
            }
            i++;
        }
        return 0;
    }
}
