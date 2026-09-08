package com.exteragram.messenger.preferences.utils;

import android.content.res.Resources;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.TextUtils;
import androidx.core.graphics.PathParser;
import com.exteragram.messenger.ExteraConfig;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;
import org.lsposed.lsparanoid.Deobfuscator$exteraGramDev$TMessagesProj;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

@Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0004\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J \u0010\u000f\u001a\u00020\u00072\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00112\b\b\u0002\u0010\u0013\u001a\u00020\u0011J\"\u0010\u0014\u001a\u00020\u00072\b\u0010\u0015\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u0011H\u0002J\b\u0010\u0016\u001a\u00020\u0007H\u0002J\b\u0010\u0017\u001a\u00020\u0018H\u0003J\u0018\u0010\u0019\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\fH\u0002J\u0018\u0010\u001b\u001a\u00020\t2\u0006\u0010\u0015\u001a\u00020\u00072\u0006\u0010\u001a\u001a\u00020\fH\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u0004\u0018\u00010\u0007X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001c"}, d2 = {"Lcom/exteragram/messenger/preferences/utils/IconShapeHelper;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "DEFAULT_CIRCLE_PATH", _UrlKt.FRAGMENT_ENCODE_SET, "cachedSystemPath", "Landroid/graphics/Path;", "isSystemPathSquare", _UrlKt.FRAGMENT_ENCODE_SET, "cacheInitialized", "scratchRect", "Landroid/graphics/RectF;", "scratchMatrix", "Landroid/graphics/Matrix;", "getFinalIconShapePath", "width", _UrlKt.FRAGMENT_ENCODE_SET, "height", "cornerRadius", "resizePath", "path", "getDefaultPath", "initSystemPathCache", _UrlKt.FRAGMENT_ENCODE_SET, "calculateIfShouldUseRoundedRect", "bounds", "hasSharpCorners", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class IconShapeHelper {
    private static boolean cacheInitialized;
    private static Path cachedSystemPath;
    private static boolean isSystemPathSquare;
    private static final String DEFAULT_CIRCLE_PATH = "M50,0A50,50,0,0,1,50,100A50,50,0,0,1,50,0";
    public static final IconShapeHelper INSTANCE = new IconShapeHelper();
    private static final RectF scratchRect = new RectF();
    private static final Matrix scratchMatrix = new Matrix();

    private IconShapeHelper() {
    }

    public final Path getFinalIconShapePath(float width, float height, float cornerRadius) {
        Path defaultPath;
        boolean z = Build.VERSION.SDK_INT >= 26 && ExteraConfig.getUseSystemIconShape();
        if (z && !cacheInitialized) {
            initSystemPathCache();
        }
        if (!z || (defaultPath = cachedSystemPath) == null) {
            defaultPath = getDefaultPath();
        }
        float fDpf2 = AndroidUtilities.dpf2(width);
        float fDpf3 = AndroidUtilities.dpf2(height);
        float fDpf4 = AndroidUtilities.dpf2(cornerRadius);
        if (fDpf4 > 0.0f && z && isSystemPathSquare) {
            Path path = new Path();
            RectF rectF = scratchRect;
            rectF.set(0.0f, 0.0f, fDpf2, fDpf3);
            path.addRoundRect(rectF, fDpf4, fDpf4, Path.Direction.CW);
            return path;
        }
        return resizePath(defaultPath, fDpf2, fDpf3);
    }

    private final Path resizePath(Path path, float width, float height) {
        Path path2 = new Path();
        if (path != null && !path.isEmpty() && width > 0.0f && height > 0.0f) {
            RectF rectF = scratchRect;
            path.computeBounds(rectF, true);
            if (rectF.width() > 0.0f && rectF.height() > 0.0f) {
                Matrix matrix = scratchMatrix;
                matrix.reset();
                matrix.setRectToRect(rectF, new RectF(0.0f, 0.0f, width, height), Matrix.ScaleToFit.FILL);
                path.transform(matrix, path2);
            }
        }
        return path2;
    }

    private final Path getDefaultPath() {
        Path pathCreatePathFromPathData = PathParser.createPathFromPathData("M50,0A50,50,0,0,1,50,100A50,50,0,0,1,50,0");
        "createPathFromPathData(...)";
        return pathCreatePathFromPathData;
    }

    private final void initSystemPathCache() {
        Resources system;
        int identifier;
        try {
            ColorDrawable colorDrawable = new ColorDrawable(0);
            IconShapeHelper$$ExternalSyntheticApiModelOutline1.m();
            Path iconMask = IconShapeHelper$$ExternalSyntheticApiModelOutline0.m(colorDrawable, colorDrawable).getIconMask();
            Path path = (iconMask == null || iconMask.isEmpty()) ? null : new Path(iconMask);
            if (path == null && (identifier = (system = Resources.getSystem()).getIdentifier("config_icon_mask", "string", "android")) != 0) {
                String string = system.getString(identifier);
                "getString(...)";
                if (!TextUtils.isEmpty(string)) {
                    path = PathParser.createPathFromPathData(string);
                }
            }
            if (path != null && !path.isEmpty()) {
                cachedSystemPath = path;
                RectF rectF = new RectF();
                path.computeBounds(rectF, true);
                isSystemPathSquare = calculateIfShouldUseRoundedRect(path, rectF);
            } else {
                cachedSystemPath = null;
                isSystemPathSquare = false;
            }
        } catch (Exception e) {
            FileLog.e(e);
            cachedSystemPath = null;
            isSystemPathSquare = false;
        } finally {
            cacheInitialized = true;
        }
    }

    private final boolean calculateIfShouldUseRoundedRect(Path path, RectF bounds) {
        if (path.isEmpty()) {
            return false;
        }
        Region region = new Region((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom);
        Region region2 = new Region();
        region2.setPath(path, region);
        if (region2.isRect()) {
            return true;
        }
        return hasSharpCorners(path, bounds);
    }

    private final boolean hasSharpCorners(Path path, RectF bounds) {
        if (bounds.width() <= 0.0f || bounds.height() <= 0.0f) {
            return false;
        }
        int iMax = Math.max(3, (int) (Math.min(bounds.width(), bounds.height()) * 0.1f));
        Region region = new Region();
        region.setPath(path, new Region((int) bounds.left, (int) bounds.top, (int) bounds.right, (int) bounds.bottom));
        float f = bounds.left;
        float f2 = bounds.top;
        Rect rect = new Rect((int) f, (int) f2, ((int) f) + iMax, ((int) f2) + iMax);
        float f3 = bounds.right;
        float f4 = bounds.top;
        Rect rect2 = new Rect(((int) f3) - iMax, (int) f4, (int) f3, ((int) f4) + iMax);
        float f5 = bounds.right;
        float f6 = bounds.bottom;
        Rect rect3 = new Rect(((int) f5) - iMax, ((int) f6) - iMax, (int) f5, (int) f6);
        float f7 = bounds.left;
        float f8 = bounds.bottom;
        Rect[] rectArr = {rect, rect2, rect3, new Rect((int) f7, ((int) f8) - iMax, ((int) f7) + iMax, (int) f8)};
        Region region2 = new Region();
        for (int i = 0; i < 4; i++) {
            Rect rect4 = rectArr[i];
            region2.set(region);
            if (!region2.op(rect4, Region.Op.INTERSECT)) {
                return false;
            }
        }
        return true;
    }
}
