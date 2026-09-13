package com.exteragram.messenger.pillstack.ui.pills.crypto.utils;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

public class ColoredBackground extends Drawable {
    private final Paint paint;
    private final Paint strokePaint;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -2;
    }

    public ColoredBackground() {
        this(-14965523, -15431455);
    }

    public ColoredBackground(int i, int i2) {
        Paint paint = new Paint(1);
        this.paint = paint;
        Paint paint2 = new Paint(1);
        this.strokePaint = paint2;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        paint.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(28.0f), new int[]{i, i2}, new float[]{0.0f, 1.0f}, tileMode));
        paint2.setStyle(Paint.Style.STROKE);
        paint2.setStrokeWidth(AndroidUtilities.dp(1.0f));
        paint2.setShader(new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(28.0f), new int[]{1308622847, 0, 452984831}, new float[]{0.0f, 0.5f, 1.0f}, tileMode));
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        float fDp = AndroidUtilities.dp(14.0f);
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(bounds);
        canvas.drawRoundRect(rectF, fDp, fDp, this.paint);
        if (!Theme.isCurrentThemeDark()) {
            return;
        }
        float fDp2 = AndroidUtilities.dp(1.0f);
        this.strokePaint.setStrokeWidth(fDp2);
        float f = fDp2 / 2.0f;
        rectF.inset(f, f);
        canvas.drawRoundRect(rectF, fDp, fDp, this.strokePaint);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.paint.setAlpha(i);
        this.strokePaint.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.paint.setColorFilter(colorFilter);
        this.strokePaint.setColorFilter(colorFilter);
    }
}
