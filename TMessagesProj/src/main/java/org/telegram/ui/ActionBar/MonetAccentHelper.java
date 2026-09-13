package org.telegram.ui.ActionBar;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.text.TextUtils;
import android.util.SparseArray;
import com.exteragram.messenger.utils.ui.MonetUtils;
import java.io.File;
import java.util.ArrayList;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.SvgHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;

/* JADX INFO: loaded from: C:\Users\badinteger\Files\.mess\extera\dex_files\classes3.dex */
public abstract class MonetAccentHelper {
    private static final int[] ACCENT_IDS = {91, 90, 89};
    private static Bitmap fallbackPatternBitmap;
    private static int fallbackPatternHeight;
    private static int fallbackPatternWidth;

    public static void appendAccentOptions(Theme.ThemeInfo themeInfo) {
        if (!isSupported() || themeInfo == null || themeInfo.themeAccentsMap == null || themeInfo.themeAccents == null) {
            return;
        }
        int i = 0;
        while (true) {
            int[] iArr = ACCENT_IDS;
            if (i >= iArr.length) {
                return;
            }
            ensureAccent(themeInfo, iArr[i], i);
            i++;
        }
    }

    public static boolean refresh(Theme.ThemeInfo themeInfo) {
        if (themeInfo == null) {
            return false;
        }
        boolean zRefreshAccents = refreshAccents(themeInfo);
        refreshPreviewColors(themeInfo);
        return zRefreshAccents;
    }

    public static boolean isMonetAccent(Theme.ThemeAccent themeAccent) {
        if (themeAccent == null) {
            return false;
        }
        int i = themeAccent.id;
        return i == 91 || i == 90 || i == 89;
    }

    public static boolean canEditAccent(Theme.ThemeAccent themeAccent) {
        return (themeAccent == null || themeAccent.id < 100 || themeAccent.isDefault || isMonetAccent(themeAccent)) ? false : true;
    }

    public static boolean hasRemotePatternWallpaper(Theme.ThemeAccent themeAccent) {
        return (themeAccent == null || TextUtils.isEmpty(themeAccent.patternSlug) || isFallbackPattern(themeAccent)) ? false : true;
    }

    public static boolean isFallbackPattern(Theme.ThemeAccent themeAccent) {
        return themeAccent != null && "__monet_default_pattern__".equals(themeAccent.patternSlug);
    }

    public static MotionBackgroundDrawable createFallbackPatternDrawable(int i, int i2, int i3, int i4, int i5, int i6, boolean z, int i7) {
        MotionBackgroundDrawable drawable;
        if (i2 == 0) {
            drawable = new MotionBackgroundDrawable(i, i, i, i, i5, z);
        } else if (i3 != 0 || i4 != 0) {
            drawable = new MotionBackgroundDrawable(i, i2, i3, i4, i5, z);
        } else {
            drawable = new MotionBackgroundDrawable(i, i2, i, i2, i5, z);
        }
        drawable.setPhase(i7);
        drawable.setPatternBitmap(i6, getFallbackPatternBitmap());
        drawable.setPatternColorFilter(drawable.getPatternColor());
        return drawable;
    }

    public static int countLeadingMonetAccents(ArrayList<Theme.ThemeAccent> arrayList) {
        if (arrayList == null) {
            return 0;
        }
        int size = arrayList.size();
        int i = 0;
        for (int i2 = 0; i2 < size && isMonetAccent(arrayList.get(i2)); i2++) {
            i++;
        }
        return i;
    }

    private static boolean isSupported() {
        return MonetUtils.isSupported();
    }

    private static void ensureAccent(Theme.ThemeInfo themeInfo, int i, int i2) {
        Theme.ThemeAccent themeAccent = themeInfo.themeAccentsMap.get(i);
        if (themeAccent == null) {
            themeAccent = new Theme.ThemeAccent();
            themeAccent.id = i;
            themeAccent.parentTheme = themeInfo;
            themeInfo.themeAccentsMap.put(i, themeAccent);
            themeInfo.themeAccents.add(themeAccent);
            themeInfo.defaultAccentCount++;
        }
        fillAccentValues(themeInfo, themeAccent, i2, false);
    }

    private static boolean refreshAccents(Theme.ThemeInfo themeInfo) {
        int i = 0;
        if (!isSupported() || themeInfo.themeAccentsMap == null) {
            return false;
        }
        boolean zFillAccentValues = false;
        while (true) {
            int[] iArr = ACCENT_IDS;
            if (i >= iArr.length) {
                return zFillAccentValues;
            }
            Theme.ThemeAccent themeAccent = themeInfo.themeAccentsMap.get(iArr[i]);
            if (themeAccent != null) {
                zFillAccentValues |= fillAccentValues(themeInfo, themeAccent, i, true);
            }
            i++;
        }
    }

    private static boolean fillAccentValues(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent, int i, boolean z) {
        File pathToWallpaper = themeAccent.getPathToWallpaper();
        int systemAccentColor = MonetUtils.getSystemAccentColor(i, themeInfo.isDark());
        Theme.ThemeAccent sourceAccent = getSourceAccent(themeInfo, themeAccent.id);
        themeAccent.accentColor = systemAccentColor;
        themeAccent.accentColor2 = 0;
        themeAccent.myMessagesAccentColor = systemAccentColor;
        themeAccent.myMessagesGradientAccentColor1 = 0;
        themeAccent.myMessagesGradientAccentColor2 = 0;
        themeAccent.myMessagesGradientAccentColor3 = 0;
        themeAccent.myMessagesAnimated = false;
        fillWallpaperValues(themeInfo, themeAccent, sourceAccent, systemAccentColor);
        if (sourceAccent != null && !TextUtils.isEmpty(sourceAccent.patternSlug)) {
            themeAccent.patternSlug = sourceAccent.patternSlug;
            themeAccent.patternIntensity = sourceAccent.patternIntensity;
            themeAccent.patternMotion = sourceAccent.patternMotion;
        } else if (themeInfo.firstAccentIsDefault) {
            themeAccent.patternSlug = "__monet_default_pattern__";
            themeAccent.patternIntensity = 0.34f;
            themeAccent.patternMotion = false;
        } else {
            themeAccent.patternSlug = _UrlKt.FRAGMENT_ENCODE_SET;
            themeAccent.patternIntensity = 0.0f;
            themeAccent.patternMotion = false;
        }
        if (z) {
            deleteCachedWallpaper(pathToWallpaper);
            deleteCachedWallpaper(themeAccent.getPathToWallpaper());
        }
        return z && hasRemotePatternWallpaper(themeAccent);
    }

    private static void fillWallpaperValues(Theme.ThemeInfo themeInfo, Theme.ThemeAccent themeAccent, Theme.ThemeAccent themeAccent2, int i) {
        if (themeAccent2 == null || !hasWallpaperColors(themeAccent2)) {
            themeAccent.backgroundOverrideColor = 0L;
            themeAccent.backgroundGradientOverrideColor1 = 0L;
            themeAccent.backgroundGradientOverrideColor2 = 0L;
            themeAccent.backgroundGradientOverrideColor3 = 0L;
            themeAccent.backgroundRotation = 45;
            return;
        }
        themeAccent.backgroundOverrideColor = changeWallpaperColor(themeInfo, i, themeAccent2.backgroundOverrideColor);
        themeAccent.backgroundGradientOverrideColor1 = changeWallpaperColor(themeInfo, i, themeAccent2.backgroundGradientOverrideColor1);
        themeAccent.backgroundGradientOverrideColor2 = changeWallpaperColor(themeInfo, i, themeAccent2.backgroundGradientOverrideColor2);
        themeAccent.backgroundGradientOverrideColor3 = changeWallpaperColor(themeInfo, i, themeAccent2.backgroundGradientOverrideColor3);
        themeAccent.backgroundRotation = themeAccent2.backgroundRotation;
    }

    private static boolean hasWallpaperColors(Theme.ThemeAccent themeAccent) {
        return (themeAccent.backgroundOverrideColor == 0 && themeAccent.backgroundGradientOverrideColor1 == 0 && themeAccent.backgroundGradientOverrideColor2 == 0 && themeAccent.backgroundGradientOverrideColor3 == 0) ? false : true;
    }

    private static long changeWallpaperColor(Theme.ThemeInfo themeInfo, int i, long j) {
        int i2;
        if (j == 0 || j == 4294967296L) {
            return 0L;
        }
        if (i == 0 || (i2 = themeInfo.accentBaseColor) == 0 || i == i2) {
            return j;
        }
        float[] fArr = new float[3];
        float[] fArr2 = new float[3];
        Color.colorToHSV(i2, fArr);
        Color.colorToHSV(i, fArr2);
        int i3 = (int) j;
        return Theme.changeColorAccent(fArr, fArr2, i3, themeInfo.isDark(), i3);
    }

    private static void deleteCachedWallpaper(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        file.delete();
    }

    private static Theme.ThemeAccent getSourceAccent(Theme.ThemeInfo themeInfo, int i) {
        SparseArray<Theme.ThemeAccent> sparseArray = themeInfo.themeAccentsMap;
        if (sparseArray != null && themeInfo.themeAccents != null) {
            Theme.ThemeAccent themeAccent = sparseArray.get(themeInfo.firstAccentIsDefault ? Theme.DEFALT_THEME_ACCENT_ID : 0);
            if (themeAccent != null && themeAccent.id != i) {
                return themeAccent;
            }
            int size = themeInfo.themeAccents.size();
            for (int i2 = 0; i2 < size; i2++) {
                Theme.ThemeAccent themeAccent2 = themeInfo.themeAccents.get(i2);
                if (!isMonetAccent(themeAccent2) && themeAccent2.id != i) {
                    return themeAccent2;
                }
            }
        }
        return null;
    }

    private static void refreshPreviewColors(Theme.ThemeInfo themeInfo) {
        if (isSupported() && themeInfo.isMonet()) {
            if ("Monet Light".equals(themeInfo.name)) {
                themeInfo.setPreviewBackgroundColor(MonetUtils.getColor("n1_10"));
                themeInfo.setPreviewInColor(MonetUtils.getColor("n1_50"));
                themeInfo.setPreviewOutColor(MonetUtils.getColor("a1_600"));
            } else if ("Monet Dark".equals(themeInfo.name)) {
                themeInfo.setPreviewBackgroundColor(MonetUtils.getColor("n1_900"));
                themeInfo.setPreviewInColor(MonetUtils.getColor("n1_800"));
                themeInfo.setPreviewOutColor(MonetUtils.getColor("a1_200"));
            } else if ("Monet Black".equals(themeInfo.name)) {
                themeInfo.setPreviewBackgroundColor(MonetUtils.getColor("mBlack"));
                themeInfo.setPreviewInColor(MonetUtils.getColor("n1_800"));
                themeInfo.setPreviewOutColor(MonetUtils.getColor("a1_200"));
            }
        }
    }

    private static Bitmap getFallbackPatternBitmap() {
        Bitmap bitmap;
        Point point = AndroidUtilities.displaySize;
        int iMax = Math.max(1, Math.min(point.x, point.y));
        Point point2 = AndroidUtilities.displaySize;
        int iMax2 = Math.max(1, Math.max(point2.x, point2.y));
        synchronized (MonetAccentHelper.class) {
            try {
                Bitmap bitmap2 = fallbackPatternBitmap;
                if (bitmap2 == null || bitmap2.isRecycled() || fallbackPatternWidth != iMax || fallbackPatternHeight != iMax2) {
                    fallbackPatternBitmap = SvgHelper.getBitmap(R.raw.default_pattern, iMax, iMax2, -16777216);
                    fallbackPatternWidth = iMax;
                    fallbackPatternHeight = iMax2;
                }
                bitmap = fallbackPatternBitmap;
            } catch (Throwable th) {
                throw th;
            }
        }
        return bitmap;
    }
}
