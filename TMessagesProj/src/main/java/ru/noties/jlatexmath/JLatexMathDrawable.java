package ru.noties.jlatexmath;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

public class JLatexMathDrawable extends Drawable {
    public static Builder builder(String latex) {
        return new Builder(latex);
    }

    public static class Builder {
        public Builder(String latex) {}
        public Builder textSize(float size) { return this; }
        public Builder textColor(int color) { return this; }
        public Builder background(Drawable drawable) { return this; }
        public Builder padding(int padding) { return this; }
        public Builder align(int align) { return this; }
        public JLatexMathDrawable build() { return new JLatexMathDrawable(); }
    }

    public static class Icon {
        public int getIconDepth() { return 0; }
        public int getIconHeight() { return 0; }
        public int getIconWidth() { return 0; }
    }

    public Icon icon() { return new Icon(); }

    @Override
    public int getIntrinsicWidth() { return 0; }

    @Override
    public int getIntrinsicHeight() { return 0; }

    @Override
    public void draw(Canvas canvas) {}

    public void draw(Canvas canvas, float x, float y) {}

    @Override
    public void setAlpha(int alpha) {}

    @Override
    public void setColorFilter(ColorFilter colorFilter) {}

    @Override
    public int getOpacity() { return PixelFormat.TRANSLUCENT; }
}
