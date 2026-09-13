package com.exteragram.messenger.utils.ui;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.fonts.Font;
import android.graphics.fonts.SystemFonts;
import android.os.Build;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Locale;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;

public abstract class FontUtils {
    private static final int CANVAS_SIZE;
    private static final Paint PAINT;
    private static final String TEST_TEXT;
    private static volatile Boolean italicSupported;
    public static boolean loadSystemEmojiFailed;
    private static volatile Boolean mediumWeightSupported;
    private static Typeface systemEmojiTypeface;
    private static Typeface systemGoogleSans;
    private static boolean systemGoogleSansLoaded;
    private static Typeface systemGoogleSansMedium;
    private static boolean systemGoogleSansMediumLoaded;
    private static volatile Boolean usePixelGoogleSans;

    static {
        int iDp = AndroidUtilities.dp(20.0f);
        CANVAS_SIZE = iDp;
        loadSystemEmojiFailed = false;
        mediumWeightSupported = null;
        italicSupported = null;
        usePixelGoogleSans = null;
        Paint paint = new Paint();
        PAINT = paint;
        paint.setTextSize(iDp);
        paint.setAntiAlias(true);
        paint.setSubpixelText(false);
        paint.setFakeBoldText(false);
        String language = "en";
        try {
            if (LocaleController.getInstance() != null && LocaleController.getInstance().getCurrentLocale() != null) {
                language = LocaleController.getInstance().getCurrentLocale().getLanguage();
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (java.util.Arrays.asList("zh", "ja", "ko").contains(language)) {
            TEST_TEXT = "你好";
        } else if (java.util.Arrays.asList("ar", "fa").contains(language)) {
            TEST_TEXT = "مرحبا";
        } else if ("iw".equals(language)) {
            TEST_TEXT = "שלום";
        } else if ("th".equals(language)) {
            TEST_TEXT = "สวัสดี";
        } else if ("hi".equals(language)) {
            TEST_TEXT = "नमस्ते";
        } else if (java.util.Arrays.asList("ru", "uk", "ky", "be", "sr").contains(language)) {
            TEST_TEXT = "Привет";
        } else {
            TEST_TEXT = "R";
        }
        systemGoogleSans = null;
        systemGoogleSansLoaded = false;
        systemGoogleSansMedium = null;
        systemGoogleSansMediumLoaded = false;
    }

    public static boolean isMediumWeightSupported() {
        if (mediumWeightSupported == null) {
            synchronized (FontUtils.class) {
                try {
                    if (mediumWeightSupported == null) {
                        mediumWeightSupported = Boolean.valueOf(supportsMediumWeight());
                        FileLog.d("mediumWeightSupported = " + mediumWeightSupported);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return mediumWeightSupported.booleanValue();
    }

    public static boolean isItalicSupported() {
        if (italicSupported == null) {
            synchronized (FontUtils.class) {
                try {
                    if (italicSupported == null) {
                        italicSupported = Boolean.valueOf(rendersDifferently(createWeightedSansTypeface(400, false), createWeightedSansTypeface(400, true)));
                        FileLog.d("italicSupported = " + italicSupported);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return italicSupported.booleanValue();
    }

    private static boolean rendersDifferently(Typeface typeface, Typeface typeface2) {
        Canvas canvas = new Canvas();
        int i = CANVAS_SIZE;
        Bitmap.Config config = Bitmap.Config.ARGB_8888;
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i * 2, i, config);
        Bitmap bitmapCreateBitmap2 = Bitmap.createBitmap(i * 2, i, config);
        Paint paint = PAINT;
        synchronized (paint) {
            canvas.setBitmap(bitmapCreateBitmap);
            paint.setTypeface(typeface);
            String str = TEST_TEXT;
            canvas.drawText(str, 0.0f, i, paint);
            canvas.setBitmap(bitmapCreateBitmap2);
            paint.setTypeface(typeface2);
            canvas.drawText(str, 0.0f, i, paint);
            paint.setTypeface(null);
        }
        boolean z = !bitmapCreateBitmap.sameAs(bitmapCreateBitmap2);
        AndroidUtilities.recycleBitmaps(java.util.Arrays.asList(bitmapCreateBitmap, bitmapCreateBitmap2));
        return z;
    }

    private static boolean differsFromRegular(Typeface typeface, boolean z) {
        return rendersDifferently(createWeightedSansTypeface(400, z), typeface);
    }

    private static boolean supportsMediumWeight() {
        if (Build.VERSION.SDK_INT < 28 || !differsFromRegular(createWeightedSansTypeface(500, false), false)) {
            return differsFromRegular(Typeface.create("sans-serif-medium", 0), false);
        }
        return true;
    }

    private static boolean isGooglePixelDevice() {
        String str;
        return "google".equalsIgnoreCase(Build.MANUFACTURER) && (str = Build.MODEL) != null && str.toLowerCase(Locale.US).startsWith("pixel");
    }

    private static boolean shouldUsePixelGoogleSans() {
        if (!isGooglePixelDevice()) {
            return false;
        }
        if (usePixelGoogleSans == null) {
            synchronized (FontUtils.class) {
                try {
                    if (usePixelGoogleSans == null) {
                        usePixelGoogleSans = Boolean.valueOf(hasSimilarMetrics(Typeface.create("sans-serif", 0), getFontFromAssets(AndroidUtilities.TYPEFACE_ROBOTO_REGULAR)));
                        FileLog.d("usePixelGoogleSans = " + usePixelGoogleSans);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
        return usePixelGoogleSans.booleanValue();
    }

    private static boolean hasSimilarMetrics(Typeface typeface, Typeface typeface2) {
        Paint paint = new Paint();
        paint.setTextSize(100.0f);
        float[] fArr = new float[25];
        float[] fArr2 = new float[25];
        paint.setTypeface(typeface);
        paint.getTextWidths("Hamburgefontsiv0123456789", fArr);
        paint.setTypeface(typeface2);
        paint.getTextWidths("Hamburgefontsiv0123456789", fArr2);
        for (int i = 0; i < 25; i++) {
            float f = fArr2[i];
            if (f == 0.0f || Math.abs(fArr[i] - f) / fArr2[i] > 0.01f) {
                return false;
            }
        }
        return true;
    }

    private static Typeface getBaseSystemTypeface() {
        File googleSansFromSystemApi;
        if (!shouldUsePixelGoogleSans()) {
            return Typeface.create("sans-serif", 0);
        }
        if (!systemGoogleSansLoaded) {
            systemGoogleSansLoaded = true;
            String[] strArr = {"google-sans-text", "google-sans"};
            for (int i = 0; i < 2; i++) {
                String str = strArr[i];
                Typeface typefaceCreate = Typeface.create(str, 0);
                if (rendersDifferently(typefaceCreate, Typeface.DEFAULT)) {
                    systemGoogleSans = typefaceCreate;
                    FileLog.d("system google sans alias = " + str);
                    return systemGoogleSans;
                }
            }
            if (Build.VERSION.SDK_INT >= 29 && (googleSansFromSystemApi = getGoogleSansFromSystemApi()) != null) {
                systemGoogleSans = Typeface.createFromFile(googleSansFromSystemApi);
                FileLog.d("system google sans file = " + googleSansFromSystemApi.getAbsolutePath());
            }
        }
        Typeface typeface = systemGoogleSans;
        return typeface != null ? typeface : Typeface.create("sans-serif", 0);
    }

    private static Typeface getSystemGoogleSansMedium() {
        if (!shouldUsePixelGoogleSans()) {
            return null;
        }
        if (!systemGoogleSansMediumLoaded) {
            systemGoogleSansMediumLoaded = true;
            String[] strArr = {"variable-title-medium-emphasized", "variable-title-medium"};
            for (int i = 0; i < 2; i++) {
                String str = strArr[i];
                Typeface typefaceCreate = Typeface.create(str, 0);
                if (rendersDifferently(typefaceCreate, Typeface.DEFAULT)) {
                    systemGoogleSansMedium = typefaceCreate;
                    FileLog.d("system google sans medium alias = " + str);
                    return systemGoogleSansMedium;
                }
            }
        }
        return systemGoogleSansMedium;
    }

    private static Typeface createWeightedSansTypeface(int i, boolean z) {
        Typeface systemGoogleSansMedium2;
        if (i >= 500 && i < 700 && (systemGoogleSansMedium2 = getSystemGoogleSansMedium()) != null) {
            if (!z) {
                return Build.VERSION.SDK_INT >= 28 ? Typeface.create(systemGoogleSansMedium2, i, false) : systemGoogleSansMedium2;
            }
            if (Build.VERSION.SDK_INT >= 28) {
                return Typeface.create(systemGoogleSansMedium2, i, true);
            }
            return Typeface.create(systemGoogleSansMedium2, 2);
        }
        Typeface baseSystemTypeface = getBaseSystemTypeface();
        if (Build.VERSION.SDK_INT >= 28) {
            return Typeface.create(baseSystemTypeface, i, z);
        }
        if (i >= 700) {
            return Typeface.create(baseSystemTypeface, z ? 3 : 1);
        }
        return z ? Typeface.create(baseSystemTypeface, 2) : baseSystemTypeface;
    }

    private static Typeface resolveSansTypeface(int i, boolean z) {
        Typeface systemGoogleSansMedium2;
        if (Build.VERSION.SDK_INT >= 28) {
            Typeface typefaceCreateWeightedSansTypeface = createWeightedSansTypeface(i, z);
            if (i == 400 || differsFromRegular(typefaceCreateWeightedSansTypeface, z)) {
                return typefaceCreateWeightedSansTypeface;
            }
        }
        Typeface baseSystemTypeface = getBaseSystemTypeface();
        if (i >= 800) {
            Typeface typefaceCreate = Typeface.create("sans-serif-black", z ? 2 : 0);
            if (differsFromRegular(typefaceCreate, z)) {
                return typefaceCreate;
            }
            return Typeface.create(baseSystemTypeface, z ? 3 : 1);
        }
        if (i >= 500) {
            if (i < 700 && (systemGoogleSansMedium2 = getSystemGoogleSansMedium()) != null && differsFromRegular(systemGoogleSansMedium2, z)) {
                return systemGoogleSansMedium2;
            }
            Typeface typefaceCreate2 = Typeface.create("sans-serif-medium", z ? 2 : 0);
            if (differsFromRegular(typefaceCreate2, z)) {
                return typefaceCreate2;
            }
            return Typeface.create(baseSystemTypeface, z ? 3 : 1);
        }
        return Typeface.create(baseSystemTypeface, z ? 2 : 0);
    }

    public static Typeface getSystemTypeface(String str) {
        str.getClass();
        switch (str) {
            case "fonts/ritalic.ttf":
                return resolveSansTypeface(400, true);
            case "fonts/rmediumitalic.ttf":
                return resolveSansTypeface(500, true);
            case "fonts/rcondensedbold.ttf":
                return Typeface.create("sans-serif-condensed", 1);
            case "fonts/rmedium.ttf":
                return resolveSansTypeface(500, false);
            case "fonts/rmono.ttf":
                return Typeface.MONOSPACE;
            case "fonts/rregular.ttf":
                return resolveSansTypeface(400, false);
            case "fonts/rextrabold.ttf":
                return resolveSansTypeface(800, false);
            default:
                return null;
        }
    }

    public static File getSystemEmojiFontPath() {
        File fontFromSystemApi;
        if (Build.VERSION.SDK_INT >= 29 && (fontFromSystemApi = getFontFromSystemApi()) != null) {
            return fontFromSystemApi;
        }
        File fontFallback = getFontFallback();
        return fontFallback != null ? fontFallback : getFontFromFontsXml();
    }

    private static File getGoogleSansFromSystemApi() {
        try {
            Iterator<Font> it = SystemFonts.getAvailableFonts().iterator();
            while (it.hasNext()) {
                File file = it.next().getFile();
                if (file != null) {
                    String lowerCase = file.getName().toLowerCase();
                    if (lowerCase.contains("googlesanstext") || lowerCase.contains("google-sans-text") || lowerCase.contains("googlesans") || lowerCase.contains("google-sans")) {
                        if (!lowerCase.contains("medium") && !lowerCase.contains("bold") && !lowerCase.contains("italic") && !lowerCase.contains("condensed")) {
                            return file;
                        }
                    }
                }
            }
            return null;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    private static File getFontFromSystemApi() {
        try {
            Iterator<Font> it = SystemFonts.getAvailableFonts().iterator();
            File file = null;
            while (it.hasNext()) {
                File file2 = it.next().getFile();
                if (file2 != null) {
                    String lowerCase = file2.getName().toLowerCase();
                    if (lowerCase.contains("samsungcoloremoji")) {
                        return file2;
                    }
                    if (file == null && lowerCase.contains("emoji")) {
                        file = file2;
                    }
                }
            }
            return file;
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    private static File getFontFallback() {
        String[] strArr = {"/system/fonts/SamsungColorEmoji.ttf", "/system/fonts/NotoColorEmoji.ttf", "/system/fonts/AndroidEmoji.ttf"};
        for (int i = 0; i < 3; i++) {
            String str = strArr[i];
            File file = new File(str);
            if (file.exists()) {
                FileLog.d("emoji font file fallback = " + str);
                return file;
            }
        }
        return null;
    }

    private static File getFontFromFontsXml() {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader("/system/etc/fonts.xml"))) {
            while (true) {
                boolean z = false;
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null) {
                        return null;
                    }
                    String strTrim = line.trim();
                    if (strTrim.startsWith("<family") && strTrim.contains("ignore=\"true\"")) {
                        z = true;
                    } else {
                        if (strTrim.startsWith("</family>")) {
                            break;
                        }
                        if (strTrim.startsWith("<font") && !z) {
                            int iIndexOf = strTrim.indexOf(">");
                            int iIndexOf2 = strTrim.indexOf("<", 1);
                            if (iIndexOf > 0 && iIndexOf2 > 0) {
                                String strSubstring = strTrim.substring(iIndexOf + 1, iIndexOf2);
                                if (strSubstring.toLowerCase().contains("emoji")) {
                                    File file = new File("/system/fonts/" + strSubstring);
                                    if (file.exists()) {
                                        FileLog.d("emoji font file fonts.xml = " + strSubstring);
                                        return file;
                                    }
                                } else {
                                    continue;
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    public static Typeface getSystemEmojiTypeface() {
        if (!loadSystemEmojiFailed && systemEmojiTypeface == null) {
            File systemEmojiFontPath = getSystemEmojiFontPath();
            if (systemEmojiFontPath != null) {
                systemEmojiTypeface = Typeface.createFromFile(systemEmojiFontPath);
            }
            if (systemEmojiTypeface == null) {
                loadSystemEmojiFailed = true;
            }
        }
        return systemEmojiTypeface;
    }

    public static Typeface getFontFromAssets(String str) {
        if (Build.VERSION.SDK_INT >= 26) {
            Typeface.Builder builderM = new Typeface.Builder(ApplicationLoader.applicationContext.getAssets(), str);
            if (str.contains("medium")) {
                builderM.setWeight(700);
            }
            if (str.contains("italic")) {
                builderM.setItalic(true);
            }
            return builderM.build();
        }
        return Typeface.createFromAsset(ApplicationLoader.applicationContext.getAssets(), str);
    }
}
