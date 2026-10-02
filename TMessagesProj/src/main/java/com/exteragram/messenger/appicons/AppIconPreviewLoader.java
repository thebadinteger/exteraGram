package com.exteragram.messenger.appicons;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.LruCache;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.Theme;

/* JADX INFO: loaded from: classes4.dex */
public abstract class AppIconPreviewLoader {
    private static LruCache<String, Bitmap> cache;
    private static final Paint paint = new Paint(3);
    private static final Map<String, List<Callback>> pending = new HashMap();
    private static final List<Integer> knownSizes = new ArrayList();
    private static final Map<String, Integer> accents = new HashMap();

    public interface Callback {
        void onPreviewReady(AppIcon appIcon, int i, Bitmap bitmap);
    }

    private static LruCache<String, Bitmap> getCache() {
        if (cache == null) {
            cache = new LruCache<String, Bitmap>((int) Math.min(12582912L, Runtime.getRuntime().maxMemory() / 16)) { // from class: com.exteragram.messenger.appicons.AppIconPreviewLoader.1
                @Override // android.util.LruCache
                public int sizeOf(String str, Bitmap bitmap) {
                    return bitmap.getByteCount();
                }
            };
        }
        return cache;
    }

    private static String key(AppIcon appIcon, int i) {
        return appIcon.id + "@" + i;
    }

    public static Bitmap getCached(AppIcon appIcon, int i) {
        return getCache().get(key(appIcon, i));
    }

    public static Bitmap getAnyCached(AppIcon appIcon) {
        Bitmap bitmap = null;
        int i = 0;
        while (true) {
            List<Integer> list = knownSizes;
            if (i >= list.size()) {
                return bitmap;
            }
            Bitmap bitmap2 = getCache().get(key(appIcon, list.get(i).intValue()));
            if (bitmap2 != null && (bitmap == null || bitmap2.getWidth() > bitmap.getWidth())) {
                bitmap = bitmap2;
            }
            i++;
        }
    }

    public static void trimAbove(int i) {
        for (Map.Entry<String, Bitmap> entry : getCache().snapshot().entrySet()) {
            if (entry.getValue().getWidth() >= i) {
                getCache().remove(entry.getKey());
            }
        }
    }

    public static int getAccent(AppIcon appIcon) {
        Integer numValueOf;
        int i = appIcon.color;
        if (i != 0) {
            return i;
        }
        Integer num = accents.get(appIcon.id);
        if (num != null) {
            return num.intValue();
        }
        if (!appIcon.isBackgroundColor()) {
            return 0;
        }
        try {
            numValueOf = Integer.valueOf(ContextCompat.getColor(ApplicationLoader.applicationContext, appIcon.getBackground()));
        } catch (Throwable th) {
            FileLog.e(th);
            numValueOf = 0;
        }
        accents.put(appIcon.id, numValueOf);
        return numValueOf.intValue();
    }

    public static int getAccentTextColor(AppIcon appIcon, int i, Theme.ResourcesProvider resourcesProvider) {
        float fMin;
        int accent = appIcon == null ? 0 : getAccent(appIcon);
        if (accent != 0) {
            float[] fArr = new float[3];
            ColorUtils.colorToHSL(accent, fArr);
            if (fArr[1] >= 0.15f) {
                if (Theme.isCurrentThemeDark()) {
                    fMin = Math.max(fArr[2], 0.72f);
                } else {
                    fMin = Math.min(fArr[2], 0.42f);
                }
                fArr[2] = fMin;
                return ColorUtils.HSLToColor(fArr);
            }
        }
        return Theme.getColor(i, resourcesProvider);
    }

    public static void load(final AppIcon appIcon, final int i, Callback callback) {
        if (i <= 0) {
            return;
        }
        List<Integer> list = knownSizes;
        if (!list.contains(Integer.valueOf(i))) {
            list.add(Integer.valueOf(i));
        }
        final String strKey = key(appIcon, i);
        Bitmap bitmap = getCache().get(strKey);
        if (bitmap != null) {
            callback.onPreviewReady(appIcon, i, bitmap);
            return;
        }
        Map<String, List<Callback>> map = pending;
        List<Callback> list2 = map.get(strKey);
        if (list2 != null) {
            list2.add(callback);
            return;
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add(callback);
        map.put(strKey, arrayList);
        final boolean z = !accents.containsKey(appIcon.id) && getAccent(appIcon) == 0;
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: com.exteragram.messenger.appicons.AppIconPreviewLoader$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AppIconPreviewLoader.$r8$lambda$xCNjtrhQGHVyqdjOo4Qfmhh5oYI(appIcon, i, z, strKey);
            }
        });
    }

    public static /* synthetic */ void $r8$lambda$xCNjtrhQGHVyqdjOo4Qfmhh5oYI(final AppIcon appIcon, final int i, boolean z, final String str) {
        final Bitmap bitmapRender = render(appIcon, i);
        final int iExtractAccent = (bitmapRender == null || !z) ? 0 : extractAccent(bitmapRender);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.appicons.AppIconPreviewLoader$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                AppIconPreviewLoader.$r8$lambda$5pAaOXpJhOh8Ffl7ErwtmWPqaac(bitmapRender, str, iExtractAccent, appIcon, i);
            }
        });
    }

    public static /* synthetic */ void $r8$lambda$5pAaOXpJhOh8Ffl7ErwtmWPqaac(Bitmap bitmap, String str, int i, AppIcon appIcon, int i2) {
        if (bitmap != null) {
            getCache().put(str, bitmap);
        }
        if (i != 0) {
            Map<String, Integer> map = accents;
            if (!map.containsKey(appIcon.id)) {
                map.put(appIcon.id, Integer.valueOf(i));
            }
        }
        List<Callback> listRemove = pending.remove(str);
        if (listRemove == null || bitmap == null) {
            return;
        }
        Iterator<Callback> it = listRemove.iterator();
        while (it.hasNext()) {
            it.next().onPreviewReady(appIcon, i2, bitmap);
        }
    }

    private static Bitmap render(AppIcon appIcon, int i) {
        try {
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i, i, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmapCreateBitmap);
            int i2 = i / 4;
            int i3 = -i2;
            int i4 = i + i2;
            Rect rect = new Rect(i3, i3, i4, i4);
            if (appIcon.isBackgroundColor()) {
                canvas.drawColor(ContextCompat.getColor(ApplicationLoader.applicationContext, appIcon.getBackground()));
            } else {
                draw(canvas, appIcon.getBackground(), rect);
            }
            draw(canvas, appIcon.getForeground(), rect);
            return bitmapCreateBitmap;
        } catch (Throwable th) {
            FileLog.e(th);
            return null;
        }
    }

    /* JADX WARN: Code duplicated, block: B:18:0x00ac  */
    private static int extractAccent(Bitmap bitmap) {
        char c2;
        char c3 = 1;
        float width = (bitmap.getWidth() - 1) / 23.0f;
        float height = (bitmap.getHeight() - 1) / 23.0f;
        float[] fArr = new float[3];
        float[] fArr2 = new float[12];
        float[] fArr3 = new float[12];
        float[] fArr4 = new float[12];
        float[] fArr5 = new float[12];
        int i = 0;
        float f = 0.0f;
        float f2 = 0.0f;
        float f3 = 0.0f;
        while (true) {
            c2 = c3;
            if (i >= 24) {
                break;
            }
            int i2 = 0;
            for (int i3 = 24; i2 < i3; i3 = 24) {
                int pixel = bitmap.getPixel((int) (i * width), (int) (i2 * height));
                if (Color.alpha(pixel) >= 200) {
                    ColorUtils.colorToHSL(pixel, fArr);
                    if (fArr[c2] >= 0.15f) {
                        float f4 = fArr[2];
                        if (f4 < 0.06f || f4 > 0.94f) {
                            f += 1.0f;
                            f3 += fArr[2];
                        } else {
                            int iMin = Math.min(11, (int) ((fArr[0] / 360.0f) * 12.0f));
                            float fAbs = fArr[c2] * (1.0f - Math.abs((fArr[2] * 2.0f) - 1.0f));
                            fArr2[iMin] = fArr2[iMin] + fAbs;
                            fArr3[iMin] = fArr3[iMin] + (fArr[0] * fAbs);
                            fArr4[iMin] = fArr4[iMin] + (fArr[c2] * fAbs);
                            fArr5[iMin] = fArr5[iMin] + (fArr[2] * fAbs);
                            f2 += 1.0f;
                        }
                    } else {
                        f += 1.0f;
                        f3 += fArr[2];
                    }
                }
                i2++;
            }
            i++;
            c3 = c2;
        }
        int i4 = -1;
        for (int i5 = 0; i5 < 12; i5++) {
            if (i4 == -1 || fArr2[i5] > fArr2[i4]) {
                i4 = i5;
            }
        }
        if (i4 != -1) {
            float f5 = fArr2[i4];
            if (f5 > 0.0f && f2 >= f * 0.15f) {
                fArr[0] = fArr3[i4] / f5;
                fArr[c2] = fArr4[i4] / fArr2[i4];
                fArr[2] = fArr5[i4] / fArr2[i4];
                return ColorUtils.HSLToColor(fArr);
            }
        }
        if (f <= 0.0f) {
            return 0;
        }
        fArr[0] = 0.0f;
        fArr[c2] = 0.0f;
        fArr[2] = f3 / f;
        return ColorUtils.HSLToColor(fArr);
    }

    private static void draw(Canvas canvas, int i, Rect rect) {
        if (i == 0) {
            return;
        }
        Context context = ApplicationLoader.applicationContext;
        Resources resources = context.getResources();
        BitmapFactory.Options options = new BitmapFactory.Options();
        int i2 = 1;
        options.inJustDecodeBounds = true;
        options.inScaled = false;
        BitmapFactory.decodeResource(resources, i, options);
        if (options.outWidth > 0 && options.outHeight > 0) {
            while (true) {
                int i3 = i2 * 2;
                if (options.outWidth / i3 < rect.width()) {
                    break;
                } else {
                    i2 = i3;
                }
            }
            options.inJustDecodeBounds = false;
            options.inSampleSize = i2;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmapDecodeResource = BitmapFactory.decodeResource(resources, i, options);
            if (bitmapDecodeResource != null) {
                canvas.drawBitmap(bitmapDecodeResource, (Rect) null, rect, paint);
                return;
            }
        }
        Drawable drawable = ContextCompat.getDrawable(context, i);
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                canvas.drawBitmap(bitmapDrawable.getBitmap(), (Rect) null, rect, paint);
                return;
            }
        }
        if (drawable != null) {
            drawable.setBounds(rect);
            drawable.draw(canvas);
        }
    }
}
