package com.exteragram.messenger.appicons.ui.components;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.os.SystemClock;
import android.view.View;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.appicons.AppIcon;
import com.exteragram.messenger.appicons.AppIconPreviewLoader;
import com.exteragram.messenger.preferences.utils.IconShapeHelper;
import java.util.HashMap;
import java.util.Map;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

/* JADX INFO: loaded from: classes4.dex */
public class AppIconPreviewView extends View implements AppIconPreviewLoader.Callback {
    private static final Map<String, Path> shapeCache = new HashMap();
    private Bitmap bitmap;
    private float bitmapAlpha;
    private boolean bitmapIsExact;
    private final Paint bitmapPaint;
    private BitmapShader bitmapShader;
    private boolean crossfade;
    private AppIcon icon;
    private final Rect iconBounds;
    private final Path iconPath;
    private final Paint placeholderPaint;
    private Runnable previewReadyListener;
    private int requestedSize;
    private final Theme.ResourcesProvider resourcesProvider;
    private float scale;
    private final Matrix shaderMatrix;
    private Bitmap underlayBitmap;
    private final Paint underlayPaint;
    private BitmapShader underlayShader;

    public AppIconPreviewView(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.bitmapPaint = new Paint(3);
        this.underlayPaint = new Paint(3);
        this.placeholderPaint = new Paint(1);
        this.iconPath = new Path();
        this.iconBounds = new Rect();
        this.shaderMatrix = new Matrix();
        this.scale = 1.0f;
        this.resourcesProvider = resourcesProvider;
    }

    public void setOnPreviewReady(Runnable runnable) {
        this.previewReadyListener = runnable;
    }

    public void setCrossfade(boolean z) {
        this.crossfade = z;
    }

    public void setIcon(AppIcon appIcon) {
        Bitmap bitmap;
        if (this.icon == appIcon) {
            return;
        }
        this.icon = appIcon;
        if (this.crossfade && (bitmap = this.bitmap) != null && this.bitmapAlpha >= 1.0f) {
            this.underlayBitmap = bitmap;
            this.underlayShader = null;
        }
        this.bitmap = null;
        this.bitmapShader = null;
        this.bitmapIsExact = false;
        this.bitmapAlpha = 0.0f;
        requestPreview();
        invalidate();
    }

    public void setPreviewScale(float f) {
        if (this.scale == f) {
            return;
        }
        this.scale = f;
        invalidate();
    }

    private void requestPreview() {
        int width;
        if (this.icon == null || getWidth() <= 0 || (width = getWidth() - (AndroidUtilities.dp(5.0f) * 2)) <= 0) {
            return;
        }
        this.requestedSize = width;
        Bitmap cached = AppIconPreviewLoader.getCached(this.icon, width);
        if (cached != null) {
            setContent(cached, true);
            Runnable runnable = this.previewReadyListener;
            if (runnable != null) {
                runnable.run();
                return;
            }
            return;
        }
        Bitmap anyCached = AppIconPreviewLoader.getAnyCached(this.icon);
        if (anyCached != null) {
            setContent(anyCached, false);
        }
        AppIconPreviewLoader.load(this.icon, width, this);
    }

    private void setContent(Bitmap bitmap, boolean z) {
        Bitmap bitmap2 = this.bitmap;
        if (bitmap2 == bitmap) {
            this.bitmapIsExact |= z;
            return;
        }
        boolean z2 = (bitmap2 == null || this.bitmapIsExact || !z) ? false : true;
        this.bitmap = bitmap;
        this.bitmapShader = null;
        this.bitmapIsExact = z;
        if (z2) {
            return;
        }
        this.bitmapAlpha = (!this.crossfade && z && this.underlayBitmap == null) ? 1.0f : 0.0f;
    }

    @Override // com.exteragram.messenger.appicons.AppIconPreviewLoader.Callback
    public void onPreviewReady(AppIcon appIcon, int i, Bitmap bitmap) {
        if (this.icon == appIcon && this.requestedSize == i) {
            setContent(bitmap, true);
            invalidate();
            Runnable runnable = this.previewReadyListener;
            if (runnable != null) {
                runnable.run();
            }
        }
    }

    @Override // android.view.View
    public void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        updatePaths();
        requestPreview();
    }

    private void updatePaths() {
        this.iconPath.rewind();
        if (getWidth() <= 0) {
            return;
        }
        float width = (getWidth() / AndroidUtilities.density) - 10.0f;
        if (width <= 0.0f) {
            return;
        }
        Matrix matrix = new Matrix();
        matrix.setTranslate(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f));
        shape(width).transform(matrix, this.iconPath);
        this.iconBounds.set(AndroidUtilities.dp(5.0f), AndroidUtilities.dp(5.0f), getWidth() - AndroidUtilities.dp(5.0f), getHeight() - AndroidUtilities.dp(5.0f));
    }

    private Path shape(float f) {
        String str = f + "_" + AndroidUtilities.density + "_" + ExteraConfig.getUseSystemIconShape();
        Map<String, Path> map = shapeCache;
        Path path = map.get(str);
        if (path != null) {
            return path;
        }
        Path finalIconShapePath = IconShapeHelper.INSTANCE.getFinalIconShapePath(f, f, 0.28f * f);
        map.put(str, finalIconShapePath);
        return finalIconShapePath;
    }

    private void drawBitmap(Canvas canvas, Bitmap bitmap, BitmapShader bitmapShader, Paint paint, int i) {
        Matrix matrix = this.shaderMatrix;
        Rect rect = this.iconBounds;
        matrix.setTranslate(rect.left, rect.top);
        this.shaderMatrix.preScale(this.iconBounds.width() / bitmap.getWidth(), this.iconBounds.height() / bitmap.getHeight());
        bitmapShader.setLocalMatrix(this.shaderMatrix);
        paint.setAlpha(i);
        canvas.drawPath(this.iconPath, paint);
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        if (this.iconPath.isEmpty()) {
            return;
        }
        boolean z = this.scale != 1.0f;
        if (z) {
            canvas.save();
            float f = this.scale;
            canvas.scale(f, f, getWidth() / 2.0f, getHeight() / 2.0f);
        }
        if (this.bitmap != null) {
            float f2 = this.bitmapAlpha;
            if (f2 < 1.0f) {
                this.bitmapAlpha = Math.min(1.0f, f2 + 0.07272727f);
                invalidate();
            }
        }
        if (this.underlayBitmap != null && this.bitmapAlpha < 1.0f) {
            if (this.underlayShader == null) {
                Bitmap bitmap = this.underlayBitmap;
                Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                BitmapShader bitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
                this.underlayShader = bitmapShader;
                this.underlayPaint.setShader(bitmapShader);
            }
            drawBitmap(canvas, this.underlayBitmap, this.underlayShader, this.underlayPaint, 255);
        }
        if (this.bitmap != null) {
            if (this.bitmapShader == null) {
                Bitmap bitmap2 = this.bitmap;
                Shader.TileMode tileMode2 = Shader.TileMode.CLAMP;
                BitmapShader bitmapShader2 = new BitmapShader(bitmap2, tileMode2, tileMode2);
                this.bitmapShader = bitmapShader2;
                this.bitmapPaint.setShader(bitmapShader2);
            }
            drawBitmap(canvas, this.bitmap, this.bitmapShader, this.bitmapPaint, (int) (this.bitmapAlpha * 255.0f));
            if (this.bitmapAlpha >= 1.0f) {
                this.underlayBitmap = null;
                this.underlayShader = null;
            }
        } else if (this.underlayBitmap == null) {
            int i = Theme.key_switchTrack;
            this.placeholderPaint.setColor(ColorUtils.blendARGB(ColorUtils.setAlphaComponent(Theme.getColor(i, this.resourcesProvider), 56), ColorUtils.setAlphaComponent(Theme.getColor(i, this.resourcesProvider), 114), (((float) Math.sin(((double) ((SystemClock.uptimeMillis() % 900) / 900.0f)) * 3.141592653589793d * 2.0d)) * 0.5f) + 0.5f));
            canvas.drawPath(this.iconPath, this.placeholderPaint);
            postInvalidateOnAnimation();
        }
        if (z) {
            canvas.restore();
        }
    }
}
