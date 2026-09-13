package com.exteragram.messenger.preferences.components;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.core.graphics.ColorUtils;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

@Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\n\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002¢\u0006\u0004\b\u0004\u0010\u0005J\u0017\u0010\t\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006H\u0016¢\u0006\u0004\b\t\u0010\nJ\u0017\u0010\r\u001a\u00020\b2\u0006\u0010\f\u001a\u00020\u000bH\u0016¢\u0006\u0004\b\r\u0010\u000eJ\u0019\u0010\u0011\u001a\u00020\b2\b\u0010\u0010\u001a\u0004\u0018\u00010\u000fH\u0016¢\u0006\u0004\b\u0011\u0010\u0012J\u000f\u0010\u0013\u001a\u00020\u000bH\u0017¢\u0006\u0004\b\u0013\u0010\u0014R\u0014\u0010\u0016\u001a\u00020\u00158\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0018\u001a\u00020\u00158\u0006¢\u0006\f\n\u0004\b\u0018\u0010\u0017\u001a\u0004\b\u0019\u0010\u001aR\u0014\u0010\u001c\u001a\u00020\u001b8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u001c\u0010\u001dR\u0014\u0010\u001e\u001a\u00020\u00028\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u001e\u0010\u001fR*\u0010!\u001a\u00020\u00022\u0006\u0010 \u001a\u00020\u00028\u0006@FX\u0086\u000e¢\u0006\u0012\n\u0004\b!\u0010\u001f\u001a\u0004\b\"\u0010#\"\u0004\b$\u0010\u0005¨\u0006%"}, d2 = {"Lcom/exteragram/messenger/preferences/components/PreviewBackgroundDrawable;", "Landroid/graphics/drawable/Drawable;", _UrlKt.FRAGMENT_ENCODE_SET, "cornerRadiusDp", "<init>", "(F)V", "Landroid/graphics/Canvas;", "canvas", _UrlKt.FRAGMENT_ENCODE_SET, "draw", "(Landroid/graphics/Canvas;)V", _UrlKt.FRAGMENT_ENCODE_SET, "alpha", "setAlpha", "(I)V", "Landroid/graphics/ColorFilter;", "colorFilter", "setColorFilter", "(Landroid/graphics/ColorFilter;)V", "getOpacity", "()I", "Landroid/graphics/Paint;", "backgroundPaint", "Landroid/graphics/Paint;", "strokePaint", "getStrokePaint", "()Landroid/graphics/Paint;", "Landroid/graphics/RectF;", "rectF", "Landroid/graphics/RectF;", "radius", "F", "value", "selectionProgress", "getSelectionProgress", "()F", "setSelectionProgress", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class PreviewBackgroundDrawable extends Drawable {
    private final Paint backgroundPaint;
    private final float radius;
    private final RectF rectF;
    private float selectionProgress;
    private final Paint strokePaint;

    public PreviewBackgroundDrawable() {
        this(0.0f, 1, null);
    }

    @Override // android.graphics.drawable.Drawable
    @Deprecated
    public int getOpacity() {
        return -3;
    }

    public PreviewBackgroundDrawable(float f) {
        this.backgroundPaint = new Paint(1);
        Paint paint = new Paint(1);
        paint.setStyle(Paint.Style.STROKE);
        this.strokePaint = paint;
        this.rectF = new RectF();
        this.radius = AndroidUtilities.dp(f);
    }

    public /* synthetic */ PreviewBackgroundDrawable(float f, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((i & 1) != 0 ? 12.0f : f);
    }

    public final void setSelectionProgress(float f) {
        if (this.selectionProgress == f) {
            return;
        }
        this.selectionProgress = f;
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this.backgroundPaint.setColor(PreviewColors.getBackgroundColor());
        this.strokePaint.setColor(ColorUtils.blendARGB(PreviewColors.getOutlineColor(), Theme.getColor(Theme.key_windowBackgroundWhiteValueText), this.selectionProgress));
        this.strokePaint.setStrokeWidth(AndroidUtilities.dp(AndroidUtilities.lerp(0.5f, 2.0f, this.selectionProgress)));
        float strokeWidth = this.strokePaint.getStrokeWidth() / 2.0f;
        this.rectF.set(getBounds().left + strokeWidth, getBounds().top + strokeWidth, getBounds().right - strokeWidth, getBounds().bottom - strokeWidth);
        RectF rectF = this.rectF;
        float f = this.radius;
        canvas.drawRoundRect(rectF, f, f, this.backgroundPaint);
        RectF rectF2 = this.rectF;
        float f2 = this.radius;
        canvas.drawRoundRect(rectF2, f2, f2, this.strokePaint);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        this.backgroundPaint.setAlpha(alpha);
        this.strokePaint.setAlpha(alpha);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.backgroundPaint.setColorFilter(colorFilter);
        this.strokePaint.setColorFilter(colorFilter);
        invalidateSelf();
    }
}
