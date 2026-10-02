package com.exteragram.messenger.appicons;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import com.exteragram.messenger.preferences.utils.IconShapeHelper;
import org.telegram.messenger.AndroidUtilities;

/* JADX INFO: loaded from: classes4.dex */
public class AppIconPreviewDrawable extends Drawable implements AppIconPreviewLoader.Callback {
    private Bitmap bitmap;
    private final AppIcon icon;
    private final Path path;
    private BitmapShader shader;
    private final int size;
    private final Paint paint = new Paint(3);
    private final Matrix matrix = new Matrix();

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public AppIconPreviewDrawable(AppIcon appIcon, int i) {
        Path path = new Path();
        this.path = path;
        this.icon = appIcon;
        this.size = i;
        float f = i / AndroidUtilities.density;
        path.set(IconShapeHelper.INSTANCE.getFinalIconShapePath(f, f, 0.28f * f));
        Bitmap cached = AppIconPreviewLoader.getCached(appIcon, i);
        if (cached != null) {
            this.bitmap = cached;
        } else {
            AppIconPreviewLoader.load(appIcon, i, this);
        }
    }

    @Override // com.exteragram.messenger.appicons.AppIconPreviewLoader.Callback
    public void onPreviewReady(AppIcon appIcon, int i, Bitmap bitmap) {
        if (this.icon == appIcon && this.size == i) {
            this.bitmap = bitmap;
            this.shader = null;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.bitmap == null) {
            return;
        }
        if (this.shader == null) {
            Bitmap bitmap = this.bitmap;
            Shader.TileMode tileMode = Shader.TileMode.CLAMP;
            BitmapShader bitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
            this.shader = bitmapShader;
            this.paint.setShader(bitmapShader);
        }
        Rect bounds = getBounds();
        canvas.save();
        canvas.translate(bounds.left, bounds.top);
        this.matrix.setScale(this.size / this.bitmap.getWidth(), this.size / this.bitmap.getHeight());
        this.shader.setLocalMatrix(this.matrix);
        canvas.drawPath(this.path, this.paint);
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.size;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.size;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.paint.setAlpha(i);
        invalidateSelf();
    }
}
