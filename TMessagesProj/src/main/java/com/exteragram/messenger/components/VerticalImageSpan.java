package com.exteragram.messenger.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import java.util.ArrayList;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.ActionBar.Theme;

public class VerticalImageSpan extends ImageSpan {
    public VerticalImageSpan(Drawable drawable) {
        super(drawable);
    }

    public static SpannableStringBuilder createSpan(Context context, int i, String str, String str2, int i2, Theme.ResourcesProvider resourcesProvider) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        ArrayList arrayList = new ArrayList();
        int iIndexOf = str.indexOf(str2);
        while (iIndexOf >= 0) {
            arrayList.add(Integer.valueOf(iIndexOf));
            iIndexOf = str.indexOf(str2, iIndexOf + 1);
        }
        Drawable drawable = context.getDrawable(i);
        int i3 = 0;
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.setColorFilter(new PorterDuffColorFilter(Theme.getColor(i2, resourcesProvider), PorterDuff.Mode.MULTIPLY));
        if (!arrayList.isEmpty()) {
            int size = arrayList.size();
            while (i3 < size) {
                Object obj = arrayList.get(i3);
                i3++;
                int iIntValue = ((Integer) obj).intValue();
                spannableStringBuilder.setSpan(new VerticalImageSpan(drawable), iIntValue, str2.length() + iIntValue, 33);
            }
        }
        return spannableStringBuilder;
    }

    @Override // android.text.style.DynamicDrawableSpan, android.text.style.ReplacementSpan
    public int getSize(Paint paint, CharSequence charSequence, int i, int i2, Paint.FontMetricsInt fontMetricsInt) {
        Rect bounds = getDrawable().getBounds();
        if (fontMetricsInt != null) {
            Paint.FontMetricsInt fontMetricsInt2 = paint.getFontMetricsInt();
            int i3 = fontMetricsInt2.descent;
            int i4 = fontMetricsInt2.ascent;
            int i5 = i4 + ((i3 - i4) / 2);
            int i6 = (bounds.bottom - bounds.top) / 2;
            int i7 = i5 - i6;
            fontMetricsInt.ascent = i7;
            fontMetricsInt.top = i7;
            int i8 = i5 + i6;
            fontMetricsInt.bottom = i8;
            fontMetricsInt.descent = i8;
        }
        return bounds.right;
    }

    @Override // android.text.style.DynamicDrawableSpan, android.text.style.ReplacementSpan
    public void draw(Canvas canvas, CharSequence charSequence, int i, int i2, float f, int i3, int i4, int i5, Paint paint) {
        Drawable drawable = getDrawable();
        canvas.save();
        Paint.FontMetricsInt fontMetricsInt = paint.getFontMetricsInt();
        int i6 = fontMetricsInt.descent;
        canvas.translate(f, ((i4 + i6) - ((i6 - fontMetricsInt.ascent) / 2)) - ((drawable.getBounds().bottom - drawable.getBounds().top) / 2));
        if (LocaleController.isRTL) {
            canvas.scale(-1.0f, 1.0f, drawable.getIntrinsicWidth() >> 1, drawable.getIntrinsicHeight() >> 1);
        }
        drawable.draw(canvas);
        canvas.restore();
    }
}
