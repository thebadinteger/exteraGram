package org.telegram.ui.Components.blur3.drawable;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.GlassOutlineStyle;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.blur3.Blur3HashImpl;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundProvider;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSource;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceBitmap;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceColor;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceWrapped;
import org.telegram.ui.Components.blur3.utils.NinePatchBuilder;

public abstract class BlurredBackgroundDrawable extends Drawable {
    protected int alpha;
    private final Paint backgroundBitmapFill;
    private final Paint backgroundBitmapPaint;
    protected int backgroundColor;
    private final Paint backgroundColorPaint;
    private final WeakReference<Bitmap> bitmapInShader;
    private BitmapShader bitmapShader;
    private final Matrix bitmapShaderMatrix;
    protected final Props boundProps;
    private final RectF cmpRectF1;
    private final RectF cmpRectF2;
    protected BlurredBackgroundColorProvider colorProvider;
    protected boolean inAppKeyboardOptimization;
    private NinePatchDrawable ninePatchDrawable;
    private long ninePatchDrawableHash;
    private final Rect ninePatchDrawablePadding;
    private final Blur3HashImpl ninePatchHashBuilder;
    private Bitmap[] ninePatchRef;
    private final Paint paintStrokeFill;
    protected float shadowAlpha;
    protected int shadowColor;
    protected float shadowLayerDx;
    protected float shadowLayerDy;
    protected float shadowLayerRadius;
    private final Paint shadowPaint;
    protected float sourceOffsetX;
    protected float sourceOffsetY;
    protected int strokeColorBottom;
    protected int strokeColorFull;
    protected int strokeColorTop;
    private ViewOutlineProvider viewOutlineProvider;
    private static final float[] tmpRadii = new float[8];
    private static Path tmpPath = new Path();

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    public abstract BlurredBackgroundSource getSource();

    public void onSourceRelativePositionChanged(RectF rectF) {
    }

    public BlurredBackgroundDrawable setClipToOutline(boolean z) {
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public BlurredBackgroundDrawable() {
        Props props = new Props();
        this.boundProps = props;
        this.alpha = 255;
        this.shadowAlpha = 1.0f;
        this.backgroundColorPaint = new Paint(1);
        this.paintStrokeFill = new Paint(1);
        Paint paint = new Paint(1);
        this.backgroundBitmapPaint = paint;
        this.backgroundBitmapFill = new Paint(1);
        Paint paint2 = new Paint(1);
        this.shadowPaint = paint2;
        this.bitmapShaderMatrix = new Matrix();
        this.bitmapInShader = new WeakReference<>(null);
        paint2.setColor(0);
        paint.setFilterBitmap(true);
        this.cmpRectF1 = new RectF();
        this.cmpRectF2 = new RectF();
        this.ninePatchHashBuilder = new Blur3HashImpl();
        this.ninePatchDrawablePadding = new Rect();
        props.strokeWidthTop = AndroidUtilities.dpf2(1.0f);
        props.strokeWidthBottom = AndroidUtilities.dpf2(0.6666667f);
        props.strokeWidthFull = 1.0f;
        this.shadowLayerRadius = AndroidUtilities.dpf2(1.0f);
        this.shadowLayerDx = 0.0f;
        this.shadowLayerDy = AndroidUtilities.dpf2(0.33333334f);
    }

    public void setSourceOffset(float f, float f2) {
        if (this.sourceOffsetX == f && this.sourceOffsetY == f2) {
            return;
        }
        this.sourceOffsetX = f;
        this.sourceOffsetY = f2;
        onSourceOffsetChange(f, f2);
    }

    public float getSourceOffsetX() {
        return this.sourceOffsetX;
    }

    public float getSourceOffsetY() {
        return this.sourceOffsetY;
    }

    public BlurredBackgroundDrawable setPadding(int i) {
        Props props = this.boundProps;
        if (props.padding != i) {
            props.padding = i;
            props.build();
            onBoundPropsChanged();
        }
        return this;
    }

    public BlurredBackgroundDrawable setHasPadding(boolean z) {
        this.boundProps.hasPadding = z;
        return this;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect rect) {
        int i = this.boundProps.padding;
        rect.set(i, i, i, i);
        return this.boundProps.hasPadding;
    }

    public BlurredBackgroundDrawable setRadius(float f) {
        Arrays.fill(this.boundProps.radii, f);
        Arrays.fill(this.boundProps.shaderRadii, f);
        this.boundProps.build();
        onBoundPropsChanged();
        return this;
    }

    public BlurredBackgroundDrawable setRadius(float f, float f2, float f3, float f4) {
        Props props = this.boundProps;
        float[] fArr = props.radii;
        fArr[1] = f;
        fArr[0] = f;
        fArr[3] = f2;
        fArr[2] = f2;
        fArr[5] = f3;
        fArr[4] = f3;
        fArr[7] = f4;
        fArr[6] = f4;
        props.build();
        onBoundPropsChanged();
        return this;
    }

    public void setRadius(float f, float f2, float f3, float f4, boolean z) {
        Props props = this.boundProps;
        float[] fArr = props.radii;
        fArr[1] = f;
        fArr[0] = f;
        fArr[3] = f2;
        fArr[2] = f2;
        float f5 = z ? 0.0f : f3;
        fArr[5] = f5;
        fArr[4] = f5;
        float f6 = z ? 0.0f : f4;
        fArr[7] = f6;
        fArr[6] = f6;
        float[] fArr2 = props.shaderRadii;
        fArr2[1] = f;
        fArr2[0] = f;
        fArr2[3] = f2;
        fArr2[2] = f2;
        fArr2[5] = f3;
        fArr2[4] = f3;
        fArr2[7] = f4;
        fArr2[6] = f4;
        props.build();
        onBoundPropsChanged();
    }

    public BlurredBackgroundDrawable setThickness(int i) {
        this.boundProps.liquidThickness = i;
        onBoundPropsChanged();
        return this;
    }

    public void setIntensity(float f) {
        this.boundProps.liquidIntensity = f;
        onBoundPropsChanged();
    }

    public Rect getPaddedBounds() {
        return this.boundProps.boundsWithPadding;
    }

    public Path getPath() {
        return this.boundProps.path;
    }

    @Override // android.graphics.drawable.Drawable
    public final void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.boundProps.bounds.set(rect);
        this.boundProps.build();
        onBoundPropsChanged();
    }

    public void onBoundPropsChanged() {
        dispatchSourceRelativePositionChange();
    }

    public void onSourceOffsetChange(float f, float f2) {
        dispatchSourceRelativePositionChange();
    }

    public BlurredBackgroundSource getUnwrappedSource() {
        BlurredBackgroundSource source = getSource();
        while (source instanceof BlurredBackgroundSourceWrapped) {
            source = ((BlurredBackgroundSourceWrapped) source).getSource();
        }
        return source;
    }

    public BlurredBackgroundDrawable setColorProvider(BlurredBackgroundColorProvider blurredBackgroundColorProvider) {
        this.colorProvider = blurredBackgroundColorProvider;
        updateColors();
        if (blurredBackgroundColorProvider instanceof BlurredBackgroundProvider) {
            BlurredBackgroundProvider blurredBackgroundProvider = (BlurredBackgroundProvider) blurredBackgroundColorProvider;
            setStrokeWidth(blurredBackgroundProvider.getStrokeWidthTop(), blurredBackgroundProvider.getStrokeWidthBottom());
        }
        return this;
    }

    public void updateColors() {
        if (this.colorProvider == null) {
            return;
        }
        GlassOutlineStyle glassOutlineStyle = ExteraConfig.getGlassOutlineStyle();
        boolean z = glassOutlineStyle == GlassOutlineStyle.GLARE;
        boolean z2 = glassOutlineStyle == GlassOutlineStyle.SOLID;
        Props props = this.boundProps;
        if (props.useFullStroke != z2) {
            props.useFullStroke = z2;
            props.build();
        }
        this.backgroundColor = this.colorProvider.getBackgroundColor();
        this.shadowColor = z ? this.colorProvider.getShadowColor() : 0;
        this.strokeColorTop = z ? this.colorProvider.getStrokeColorTop() : 0;
        this.strokeColorBottom = z ? this.colorProvider.getStrokeColorBottom() : 0;
        this.strokeColorFull = z2 ? this.colorProvider.getStrokeColorFull() : 0;
        BlurredBackgroundColorProvider blurredBackgroundColorProvider = this.colorProvider;
        if (blurredBackgroundColorProvider instanceof BlurredBackgroundProvider) {
            BlurredBackgroundProvider blurredBackgroundProvider = (BlurredBackgroundProvider) blurredBackgroundColorProvider;
            if (z2 || glassOutlineStyle == GlassOutlineStyle.HIDDEN) {
                setShadowParams(0.0f, 0.0f, 0.0f);
            } else {
                setShadowParams(blurredBackgroundProvider.getShadowRadius(), blurredBackgroundProvider.getShadowDx(), blurredBackgroundProvider.getShadowDy());
            }
        }
        invalidateSelf();
    }

    public static class Props {
        public boolean hasPadding;
        public int liquidThickness;
        public int padding;
        public float strokeWidthBottom;
        public float strokeWidthFull;
        public float strokeWidthTop;
        public final Rect bounds = new Rect();
        public final float[] radii = new float[8];
        public final float[] shaderRadii = new float[8];
        public float liquidIntensity = 0.75f;
        public float liquidIndex = 1.5f;
        public final Path path = new Path();
        public boolean radiiAreSame = true;
        public final Rect boundsWithPadding = new Rect();
        public final Path strokePathTop = new Path();
        public final Path strokePathBottom = new Path();
        public boolean useFullStroke = false;

        public void build() {
            this.radiiAreSame = BlurredBackgroundDrawable.radiiAreSame(this.radii);
            this.boundsWithPadding.set(this.bounds);
            Rect rect = this.boundsWithPadding;
            int i = this.padding;
            rect.inset(i, i);
            this.path.rewind();
            Path path = this.path;
            Rect rect2 = this.boundsWithPadding;
            float f = rect2.left;
            float f2 = rect2.top;
            float f3 = rect2.right;
            float f4 = rect2.bottom;
            float[] fArr = this.radii;
            Path.Direction direction = Path.Direction.CW;
            path.addRoundRect(f, f2, f3, f4, fArr, direction);
            this.path.close();
            float fMin = Math.min(this.boundsWithPadding.width(), this.boundsWithPadding.height()) / 2.0f;
            if (this.useFullStroke) {
                System.arraycopy(this.radii, 0, BlurredBackgroundDrawable.tmpRadii, 0, 8);
                if (this.radiiAreSame && this.radii[0] > fMin) {
                    Arrays.fill(BlurredBackgroundDrawable.tmpRadii, fMin);
                }
                this.strokePathTop.rewind();
                Path path2 = this.strokePathTop;
                Rect rect3 = this.boundsWithPadding;
                path2.addRoundRect(rect3.left, rect3.top, rect3.right, rect3.bottom, BlurredBackgroundDrawable.tmpRadii, direction);
                Path path3 = this.strokePathTop;
                Rect rect4 = this.boundsWithPadding;
                float f5 = rect4.left;
                float f6 = this.strokeWidthFull;
                path3.addRoundRect(f5 + f6, rect4.top + f6, rect4.right - f6, rect4.bottom - f6, BlurredBackgroundDrawable.tmpRadii, Path.Direction.CCW);
                this.strokePathTop.close();
                this.strokePathBottom.rewind();
                this.strokePathBottom.close();
                return;
            }
            Arrays.fill(BlurredBackgroundDrawable.tmpRadii, 0.0f);
            BlurredBackgroundDrawable.tmpRadii[0] = this.radii[0];
            BlurredBackgroundDrawable.tmpRadii[1] = this.radii[1];
            BlurredBackgroundDrawable.tmpRadii[2] = this.radii[2];
            float[] fArr2 = BlurredBackgroundDrawable.tmpRadii;
            float[] fArr3 = this.radii;
            fArr2[3] = fArr3[3];
            if (this.radiiAreSame && fArr3[0] > fMin) {
                float[] fArr4 = BlurredBackgroundDrawable.tmpRadii;
                float[] fArr5 = BlurredBackgroundDrawable.tmpRadii;
                float[] fArr6 = BlurredBackgroundDrawable.tmpRadii;
                BlurredBackgroundDrawable.tmpRadii[3] = fMin;
                fArr6[2] = fMin;
                fArr5[1] = fMin;
                fArr4[0] = fMin;
            }
            this.strokePathTop.rewind();
            Path path4 = this.strokePathTop;
            Rect rect5 = this.boundsWithPadding;
            float f7 = rect5.left;
            int i2 = rect5.top;
            path4.addRoundRect(f7, i2, rect5.right, Math.min(i2 + this.radii[0], rect5.bottom), BlurredBackgroundDrawable.tmpRadii, direction);
            Path path5 = this.strokePathTop;
            Rect rect6 = this.boundsWithPadding;
            float f8 = rect6.left;
            int i3 = rect6.top;
            float f9 = i3 + this.strokeWidthTop;
            float f10 = rect6.right;
            float fMin2 = Math.min(i3 + this.radii[0], rect6.bottom);
            float[] fArr7 = BlurredBackgroundDrawable.tmpRadii;
            Path.Direction direction2 = Path.Direction.CCW;
            path5.addRoundRect(f8, f9, f10, fMin2, fArr7, direction2);
            this.strokePathTop.close();
            Arrays.fill(BlurredBackgroundDrawable.tmpRadii, 0.0f);
            BlurredBackgroundDrawable.tmpRadii[4] = this.radii[4];
            BlurredBackgroundDrawable.tmpRadii[5] = this.radii[5];
            BlurredBackgroundDrawable.tmpRadii[6] = this.radii[6];
            float[] fArr8 = BlurredBackgroundDrawable.tmpRadii;
            float[] fArr9 = this.radii;
            fArr8[7] = fArr9[7];
            if (this.radiiAreSame && fArr9[0] > fMin) {
                float[] fArr10 = BlurredBackgroundDrawable.tmpRadii;
                float[] fArr11 = BlurredBackgroundDrawable.tmpRadii;
                float[] fArr12 = BlurredBackgroundDrawable.tmpRadii;
                BlurredBackgroundDrawable.tmpRadii[7] = fMin;
                fArr12[6] = fMin;
                fArr11[5] = fMin;
                fArr10[4] = fMin;
            }
            this.strokePathBottom.rewind();
            Path path6 = this.strokePathBottom;
            Rect rect7 = this.boundsWithPadding;
            float f11 = rect7.left;
            float fMax = Math.max(rect7.bottom - this.radii[4], rect7.top);
            Rect rect8 = this.boundsWithPadding;
            path6.addRoundRect(f11, fMax, rect8.right, rect8.bottom, BlurredBackgroundDrawable.tmpRadii, direction);
            Path path7 = this.strokePathBottom;
            Rect rect9 = this.boundsWithPadding;
            float f12 = rect9.left;
            float fMax2 = Math.max(rect9.bottom - this.radii[4], rect9.top);
            Rect rect10 = this.boundsWithPadding;
            path7.addRoundRect(f12, fMax2, rect10.right, rect10.bottom - this.strokeWidthBottom, BlurredBackgroundDrawable.tmpRadii, direction2);
            this.strokePathBottom.close();
        }

        public void drawShadows(Canvas canvas, Paint paint, boolean z) {
            if (z) {
                Rect rect = this.boundsWithPadding;
                int i = rect.top;
                float fClamp = MathUtils.clamp(i + (this.radii[0] * 2.0f), i, rect.bottom);
                canvas.save();
                Rect rect2 = this.bounds;
                canvas.clipRect(rect2.left, rect2.top, rect2.right, fClamp);
                Rect rect3 = this.boundsWithPadding;
                float f = rect3.left;
                float f2 = rect3.top;
                float f3 = rect3.right;
                float f4 = this.radii[0];
                canvas.drawRoundRect(f, f2, f3, fClamp, f4, f4, paint);
                canvas.restore();
                return;
            }
            draw(canvas, paint);
        }

        public void draw(Canvas canvas, Paint paint) {
            if (this.radiiAreSame) {
                Rect rect = this.boundsWithPadding;
                float f = rect.left;
                float f2 = rect.top;
                float f3 = rect.right;
                float f4 = rect.bottom;
                float f5 = this.radii[0];
                canvas.drawRoundRect(f, f2, f3, f4, f5, f5, paint);
                return;
            }
            canvas.drawPath(this.path, paint);
        }
    }

    public ViewOutlineProvider getViewOutlineProvider() {
        if (this.viewOutlineProvider == null) {
            this.viewOutlineProvider = new ViewOutlineProvider() { // from class: org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable.1
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    Props props = BlurredBackgroundDrawable.this.boundProps;
                    BlurredBackgroundDrawable.getOutline(outline, props.boundsWithPadding, props.radii);
                }
            };
        }
        return this.viewOutlineProvider;
    }

    @Override // android.graphics.drawable.Drawable
    public void getOutline(Outline outline) {
        Props props = this.boundProps;
        getOutline(outline, props.boundsWithPadding, props.radii);
    }

    public static void getOutline(Outline outline, Rect rect, float[] fArr) {
        if (radiiAreSame(fArr)) {
            outline.setRoundRect(rect, Math.min(fArr[0], Math.min(rect.width(), rect.height()) / 2.0f));
            return;
        }
        Path path = tmpPath;
        if (path == null) {
            tmpPath = new Path();
        } else {
            path.rewind();
        }
        tmpPath.addRoundRect(rect.left, rect.top, rect.right, rect.bottom, fArr, Path.Direction.CW);
        outline.setConvexPath(tmpPath);
    }

    public static boolean radiiAreSame(float[] fArr) {
        float f = fArr[0];
        return f == fArr[1] && f == fArr[2] && f == fArr[3] && f == fArr[4] && f == fArr[5] && f == fArr[6] && f == fArr[7];
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.alpha = i;
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.alpha;
    }

    /* JADX WARN: Code duplicated, block: B:10:0x0029  */
    public static void drawStroke(Canvas canvas, float f, float f2, float f3, float f4, float[] fArr, float f5, boolean z, Paint paint) {
        boolean z2;
        if (z) {
            float f6 = fArr[0];
            float f7 = fArr[1];
            if (f6 == f7) {
                float f8 = fArr[2];
                if (f7 == f8 && f8 == fArr[3]) {
                    z2 = true;
                }
            }
            z2 = false;
        } else {
            float f9 = fArr[4];
            float f10 = fArr[5];
            if (f9 == f10) {
                float f11 = fArr[6];
                if (f10 == f11 && f11 == fArr[7]) {
                    z2 = true;
                }
            }
            z2 = false;
        }
        float f12 = f5 / 2.0f;
        if (z) {
            if (z2) {
                canvas.save();
                if (canvas.clipRect(f, f2, f3, MathUtils.clamp((fArr[0] * 2.0f) + f2, f2, f4))) {
                    float f13 = fArr[0];
                    canvas.drawRoundRect(f - f12, f2 + f12, f3 + f12, f4 + f12, f13, f13, paint);
                }
                canvas.restore();
                return;
            }
            float f14 = (f + f3) / 2.0f;
            canvas.save();
            if (canvas.clipRect(f, f2, f14, MathUtils.clamp((fArr[0] * 2.0f) + f2, f2, f4))) {
                canvas.drawRoundRect(f - f12, f2 + f12, f3 + f12, f4 + f12, fArr[0], fArr[1], paint);
            }
            canvas.restore();
            canvas.save();
            if (canvas.clipRect(f14, f2, f3, MathUtils.clamp((fArr[0] * 2.0f) + f2, f2, f4))) {
                canvas.drawRoundRect(f - f12, f2 + f12, f3 + f12, f4 + f12, fArr[2], fArr[3], paint);
            }
            canvas.restore();
            return;
        }
        if (z2) {
            canvas.save();
            if (canvas.clipRect(f, MathUtils.clamp(f4 - (fArr[4] * 2.0f), f2, f4), f3, f4)) {
                float f15 = fArr[4];
                canvas.drawRoundRect(f - f12, f2 - f12, f3 + f12, f4 - f12, f15, f15, paint);
            }
            canvas.restore();
            return;
        }
        float f16 = (f + f3) / 2.0f;
        canvas.save();
        if (canvas.clipRect(f, MathUtils.clamp(f4 - (fArr[4] * 2.0f), f2, f4), f16, f4)) {
            canvas.drawRoundRect(f - f12, f2 - f12, f3 + f12, f4 - f12, fArr[6], fArr[7], paint);
        }
        canvas.restore();
        canvas.save();
        if (canvas.clipRect(f16, MathUtils.clamp(f4 - (fArr[4] * 2.0f), f2, f4), f3, f4)) {
            canvas.drawRoundRect(f - f12, f2 - f12, f3 + f12, f4 - f12, fArr[4], fArr[5], paint);
        }
        canvas.restore();
    }

    public static void drawStroke(Canvas canvas, RectF rectF, float f, float f2, boolean z, Paint paint) {
        drawStroke(canvas, rectF.left, rectF.top, rectF.right, rectF.bottom, f, f2, z, paint);
    }

    public static void drawStroke(Canvas canvas, float f, float f2, float f3, float f4, float f5, float f6, boolean z, Paint paint) {
        float f7 = f6 / 2.0f;
        canvas.save();
        if (z) {
            float f8 = f - f7;
            float f9 = f3 + f7;
            if (canvas.clipRect(f8, f2, f9, MathUtils.clamp((2.0f * f5) + f2, f2, f4))) {
                canvas.drawRoundRect(f8, f2 + f7, f9, f7 + f4, f5, f5, paint);
            }
        } else {
            float f10 = f - f7;
            float f11 = f3 + f7;
            if (canvas.clipRect(f10, MathUtils.clamp(f4 - (f5 * 2.0f), f2, f4), f11, f4)) {
                canvas.drawRoundRect(f10, f2 - f7, f11, f4 - f7, f5, f5, paint);
            }
        }
        canvas.restore();
    }

    public void enableInAppKeyboardOptimization() {
        this.inAppKeyboardOptimization = true;
    }

    public void setShadowParams(float f, float f2, float f3) {
        this.shadowLayerRadius = f;
        this.shadowLayerDx = f2;
        this.shadowLayerDy = f3;
    }

    public void setShadowAlpha(float f) {
        if (this.shadowAlpha != f) {
            this.shadowAlpha = f;
            invalidateSelf();
        }
    }

    public void setStrokeWidth(float f, float f2) {
        Props props = this.boundProps;
        props.strokeWidthTop = f;
        props.strokeWidthBottom = f2;
    }

    public void drawSource(Canvas canvas, BlurredBackgroundSource blurredBackgroundSource) {
        if (this.boundProps.boundsWithPadding.isEmpty()) {
            return;
        }
        if (Color.alpha(this.backgroundColor) == 255) {
            drawSourceColorImpl(canvas, 0);
            return;
        }
        if (blurredBackgroundSource instanceof BlurredBackgroundSourceColor) {
            drawSourceColor(canvas, (BlurredBackgroundSourceColor) blurredBackgroundSource);
            return;
        }
        if (blurredBackgroundSource instanceof BlurredBackgroundSourceBitmap) {
            drawSourceBitmap(canvas, (BlurredBackgroundSourceBitmap) blurredBackgroundSource);
            return;
        }
        if (Build.VERSION.SDK_INT >= 29 && (blurredBackgroundSource instanceof BlurredBackgroundSourceRenderNode)) {
            drawSourceRenderNode(canvas, (BlurredBackgroundSourceRenderNode) blurredBackgroundSource);
        } else if (blurredBackgroundSource instanceof BlurredBackgroundSourceWrapped) {
            drawSource(canvas, ((BlurredBackgroundSourceWrapped) blurredBackgroundSource).getSource());
        } else if (blurredBackgroundSource != null) {
            drawSourceAny(canvas, blurredBackgroundSource);
        }
    }

    private void drawSourceAny(Canvas canvas, BlurredBackgroundSource blurredBackgroundSource) {
        int i = this.alpha;
        if (i == 0) {
            return;
        }
        int iMultAlpha = Theme.multAlpha(this.backgroundColor, i / 255.0f);
        if (Color.alpha(this.shadowColor) > 0 && this.alpha == 255) {
            float f = this.shadowAlpha;
            if (f > 0.0f) {
                this.shadowPaint.setShadowLayer(this.shadowLayerRadius, this.shadowLayerDx, this.shadowLayerDy, Theme.multAlpha(this.shadowColor, f));
                this.boundProps.drawShadows(canvas, this.shadowPaint, this.inAppKeyboardOptimization);
            }
        }
        float f2 = this.sourceOffsetX;
        float f3 = this.sourceOffsetY;
        Rect rect = this.boundProps.boundsWithPadding;
        int i2 = rect.left;
        float f4 = i2 + f2;
        int i3 = rect.top;
        float f5 = i3 + f3;
        int i4 = rect.right;
        float f6 = i4 + f2;
        int i5 = rect.bottom;
        float f7 = i5 + f3;
        int i6 = this.alpha;
        boolean z = i6 != 255;
        if (z) {
            canvas.saveLayerAlpha(i2, i3, i4, i5, i6);
        }
        canvas.save();
        canvas.clipPath(this.boundProps.path);
        Rect rect2 = this.boundProps.boundsWithPadding;
        canvas.translate(rect2.left, rect2.top);
        canvas.translate(-f4, -f5);
        blurredBackgroundSource.draw(canvas, f4, f5, f6, f7);
        canvas.restore();
        if (Color.alpha(iMultAlpha) > 0) {
            this.backgroundColorPaint.setColor(iMultAlpha);
            this.boundProps.draw(canvas, this.backgroundColorPaint);
        }
        drawStrokeInternalIfNeeded(canvas);
        if (z) {
            canvas.restore();
        }
    }

    private void drawSourceColor(Canvas canvas, BlurredBackgroundSourceColor blurredBackgroundSourceColor) {
        drawSourceColorImpl(canvas, blurredBackgroundSourceColor.getColor());
    }

    private void drawSourceColorImpl(Canvas canvas, int i) {
        int iCompositeColors = ColorUtils.compositeColors(this.backgroundColor, i);
        if (Color.alpha(iCompositeColors) == 0 && Color.alpha(this.shadowColor) == 0) {
            return;
        }
        NinePatchDrawable ninePatchDrawableCheckNinePatchDrawable = checkNinePatchDrawable(iCompositeColors, true);
        Rect rect = this.boundProps.boundsWithPadding;
        int i2 = rect.left;
        Rect rect2 = this.ninePatchDrawablePadding;
        ninePatchDrawableCheckNinePatchDrawable.setBounds(i2 - rect2.left, rect.top - rect2.top, rect.right + rect2.right, rect.bottom + rect2.bottom);
        ninePatchDrawableCheckNinePatchDrawable.setAlpha(this.alpha);
        ninePatchDrawableCheckNinePatchDrawable.draw(canvas);
    }

    private void drawSourceBitmap(Canvas canvas, BlurredBackgroundSourceBitmap blurredBackgroundSourceBitmap) {
        Bitmap bitmap = blurredBackgroundSourceBitmap.getBitmap();
        if (bitmap != this.bitmapInShader.get()) {
            if (bitmap != null && !bitmap.isRecycled()) {
                Shader.TileMode tileMode = Shader.TileMode.CLAMP;
                BitmapShader bitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
                this.bitmapShader = bitmapShader;
                this.backgroundBitmapPaint.setShader(bitmapShader);
            } else {
                this.bitmapShader = null;
                this.backgroundBitmapPaint.setShader(null);
            }
        }
        if (this.shadowLayerRadius > 0.0f && Color.alpha(this.shadowColor) > 0 && this.shadowAlpha > 0.0f) {
            NinePatchDrawable ninePatchDrawableCheckNinePatchDrawable = checkNinePatchDrawable(0, false);
            Rect rect = this.boundProps.boundsWithPadding;
            int i = rect.left;
            Rect rect2 = this.ninePatchDrawablePadding;
            ninePatchDrawableCheckNinePatchDrawable.setBounds(i - rect2.left, rect.top - rect2.top, rect.right + rect2.right, rect.bottom + rect2.bottom);
            ninePatchDrawableCheckNinePatchDrawable.setAlpha(this.alpha);
            ninePatchDrawableCheckNinePatchDrawable.draw(canvas);
        }
        if (this.bitmapShader != null && bitmap != null && !bitmap.isRecycled() && this.alpha > 0) {
            this.bitmapShaderMatrix.set(blurredBackgroundSourceBitmap.getMatrix());
            this.bitmapShaderMatrix.postTranslate(-this.sourceOffsetX, -this.sourceOffsetY);
            this.bitmapShader.setLocalMatrix(this.bitmapShaderMatrix);
            this.backgroundBitmapPaint.setAlpha(this.alpha);
            this.boundProps.draw(canvas, this.backgroundBitmapPaint);
        }
        int iMultAlpha = Theme.multAlpha(this.backgroundColor, this.alpha / 255.0f);
        if (Color.alpha(iMultAlpha) > 0) {
            this.backgroundBitmapFill.setColor(iMultAlpha);
            this.boundProps.draw(canvas, this.backgroundBitmapFill);
        }
        drawStrokeInternalIfNeeded(canvas);
    }

    private void drawStrokeInternalIfNeeded(Canvas canvas) {
        if (this.boundProps.useFullStroke) {
            int iMultAlpha = Theme.multAlpha(this.strokeColorFull, this.alpha / 255.0f);
            if (Color.alpha(iMultAlpha) > 0) {
                this.paintStrokeFill.setColor(iMultAlpha);
                canvas.drawPath(this.boundProps.strokePathTop, this.paintStrokeFill);
                return;
            }
            return;
        }
        int iMultAlpha2 = Theme.multAlpha(this.strokeColorTop, this.alpha / 255.0f);
        int iMultAlpha3 = Theme.multAlpha(this.strokeColorBottom, this.alpha / 255.0f);
        if (Color.alpha(iMultAlpha2) > 0) {
            this.paintStrokeFill.setColor(iMultAlpha2);
            canvas.drawPath(this.boundProps.strokePathTop, this.paintStrokeFill);
        }
        if (Color.alpha(iMultAlpha3) > 0) {
            this.paintStrokeFill.setColor(iMultAlpha3);
            canvas.drawPath(this.boundProps.strokePathBottom, this.paintStrokeFill);
        }
    }

    private void drawSourceRenderNode(Canvas canvas, BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode) {
        if (canvas.isHardwareAccelerated()) {
            return;
        }
        drawSource(canvas, blurredBackgroundSourceRenderNode.getFallbackSource());
    }

    private void dispatchSourceRelativePositionChange() {
        getPositionRelativeSource(this.cmpRectF1);
        if (this.cmpRectF1.equals(this.cmpRectF2)) {
            return;
        }
        this.cmpRectF2.set(this.cmpRectF1);
        onSourceRelativePositionChanged(this.cmpRectF1);
    }

    public void getPositionRelativeSource(RectF rectF) {
        rectF.set(this.boundProps.boundsWithPadding);
        rectF.offset(this.sourceOffsetX, this.sourceOffsetY);
    }

    private NinePatchDrawable checkNinePatchDrawable(final int i, final boolean z) {
        this.ninePatchHashBuilder.start();
        this.ninePatchHashBuilder.add(i);
        this.ninePatchHashBuilder.add(this.shadowColor);
        this.ninePatchHashBuilder.add(this.boundProps.radii);
        this.ninePatchHashBuilder.addF(this.shadowAlpha);
        this.ninePatchHashBuilder.addF(this.shadowLayerRadius);
        this.ninePatchHashBuilder.addF(this.shadowLayerDx);
        this.ninePatchHashBuilder.addF(this.shadowLayerDy);
        this.ninePatchHashBuilder.add(z);
        if (z) {
            this.ninePatchHashBuilder.add(this.boundProps.useFullStroke);
            boolean z2 = this.boundProps.useFullStroke;
            Blur3HashImpl blur3HashImpl = this.ninePatchHashBuilder;
            if (z2) {
                blur3HashImpl.add(this.strokeColorFull);
                this.ninePatchHashBuilder.addF(this.boundProps.strokeWidthFull);
            } else {
                blur3HashImpl.add(this.strokeColorTop);
                this.ninePatchHashBuilder.add(this.strokeColorBottom);
                this.ninePatchHashBuilder.addF(this.boundProps.strokeWidthTop);
                this.ninePatchHashBuilder.addF(this.boundProps.strokeWidthBottom);
            }
        }
        long j = this.ninePatchHashBuilder.get();
        if (this.ninePatchDrawable == null || this.ninePatchDrawableHash != j) {
            this.ninePatchDrawableHash = j;
            NinePatchDrawable ninePatchDrawableCreateNinePatch = NinePatchBuilder.createNinePatch(this.ninePatchRef, this.boundProps.radii, this.shadowLayerRadius, this.shadowLayerDx, this.shadowLayerDy, Color.alpha(i) == 255 ? i : 1, new NinePatchBuilder.NinePathRenderer() { // from class: org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable$$ExternalSyntheticLambda0
                @Override // org.telegram.ui.Components.blur3.utils.NinePatchBuilder.NinePathRenderer
                public final void draw(Canvas canvas, RectF rectF, float[] fArr) {
                    this.f$0.lambda$checkNinePatchDrawable$0(i, z, canvas, rectF, fArr);
                }
            });
            this.ninePatchDrawable = ninePatchDrawableCreateNinePatch;
            ninePatchDrawableCreateNinePatch.getPadding(this.ninePatchDrawablePadding);
        }
        return this.ninePatchDrawable;
    }

    public /* synthetic */ void lambda$checkNinePatchDrawable$0(int i, boolean z, Canvas canvas, RectF rectF, float[] fArr) {
        Path path = new Path();
        Path.Direction direction = Path.Direction.CW;
        path.addRoundRect(rectF, fArr, direction);
        Paint paint = new Paint(1);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(i);
        if (this.shadowLayerRadius > 0.0f) {
            paint.setShadowLayer(this.shadowLayerRadius, this.shadowLayerDx, this.shadowLayerDy, Theme.multAlpha(this.shadowColor, this.shadowAlpha));
        }
        canvas.drawPath(path, paint);
        if (this.shadowLayerRadius > 0.0f) {
            paint.clearShadowLayer();
            canvas.drawPath(path, paint);
        }
        if (z) {
            Paint paint2 = new Paint(1);
            float[] fArrCopyOf = Arrays.copyOf(fArr, 8);
            boolean zRadiiAreSame = radiiAreSame(fArrCopyOf);
            float fMin = Math.min(rectF.width(), rectF.height()) / 2.0f;
            if (this.boundProps.useFullStroke) {
                Path path2 = new Path();
                float[] fArr2 = tmpRadii;
                System.arraycopy(fArrCopyOf, 0, fArr2, 0, 8);
                if (zRadiiAreSame && fArrCopyOf[0] > fMin) {
                    Arrays.fill(fArr2, fMin);
                }
                path2.addRoundRect(rectF.left, rectF.top, rectF.right, rectF.bottom, fArr2, direction);
                float f = rectF.left;
                float f2 = this.boundProps.strokeWidthFull;
                path2.addRoundRect(f + f2, rectF.top + f2, rectF.right - f2, rectF.bottom - f2, fArr2, Path.Direction.CCW);
                path2.close();
                paint2.setColor(this.strokeColorFull);
                canvas.drawPath(path2, paint2);
                return;
            }
            if (Color.alpha(this.strokeColorTop) > 0 && (fArrCopyOf[0] > 0.0f || fArrCopyOf[2] > 0.0f)) {
                float[] fArr3 = tmpRadii;
                Arrays.fill(fArr3, 0.0f);
                fArr3[0] = fArrCopyOf[0];
                fArr3[1] = fArrCopyOf[1];
                fArr3[2] = fArrCopyOf[2];
                fArr3[3] = fArrCopyOf[3];
                if (zRadiiAreSame && fArrCopyOf[0] > fMin) {
                    fArr3[3] = fMin;
                    fArr3[2] = fMin;
                    fArr3[1] = fMin;
                    fArr3[0] = fMin;
                }
                Path path3 = new Path();
                float f3 = rectF.left;
                float f4 = rectF.top;
                path3.addRoundRect(f3, f4, rectF.right, Math.min(Math.max(fArrCopyOf[0], fArrCopyOf[2]) + f4, rectF.bottom), fArr3, direction);
                float f5 = rectF.left;
                float f6 = rectF.top;
                path3.addRoundRect(f5, f6 + this.boundProps.strokeWidthTop, rectF.right, Math.min(f6 + Math.max(fArrCopyOf[0], fArrCopyOf[2]), rectF.bottom), fArr3, Path.Direction.CCW);
                paint2.setColor(this.strokeColorTop);
                canvas.drawPath(path3, paint2);
            }
            if (Color.alpha(this.strokeColorBottom) > 0) {
                if (fArrCopyOf[4] > 0.0f || fArrCopyOf[6] > 0.0f) {
                    float[] fArr4 = tmpRadii;
                    Arrays.fill(fArr4, 0.0f);
                    fArr4[4] = fArrCopyOf[4];
                    fArr4[5] = fArrCopyOf[5];
                    fArr4[6] = fArrCopyOf[6];
                    fArr4[7] = fArrCopyOf[7];
                    if (zRadiiAreSame && fArrCopyOf[0] > fMin) {
                        fArr4[7] = fMin;
                        fArr4[6] = fMin;
                        fArr4[5] = fMin;
                        fArr4[4] = fMin;
                    }
                    Path path4 = new Path();
                    path4.addRoundRect(rectF.left, Math.max(rectF.bottom - Math.max(fArrCopyOf[4], fArrCopyOf[6]), rectF.top), rectF.right, rectF.bottom, fArr4, direction);
                    path4.addRoundRect(rectF.left, Math.max(rectF.bottom - Math.max(fArrCopyOf[4], fArrCopyOf[6]), rectF.top), rectF.right, rectF.bottom - this.boundProps.strokeWidthBottom, fArr4, Path.Direction.CCW);
                    paint2.setColor(this.strokeColorBottom);
                    canvas.drawPath(path4, paint2);
                }
            }
        }
    }
}
