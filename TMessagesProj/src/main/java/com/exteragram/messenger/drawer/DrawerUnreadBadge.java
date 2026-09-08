package com.exteragram.messenger.drawer;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.View;
import android.widget.TextView;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.Theme;

final class DrawerUnreadBadge {
    private int badgeWidth;
    private int counter;
    private String text;
    private int textWidth;
    private boolean visible;
    private final RectF rect = new RectF();
    private int defaultTextPaddingEnd = Integer.MIN_VALUE;

    public void bind(int i, TextView textView) {
        this.counter = i;
        update(textView);
    }

    public void update(TextView textView) {
        rememberDefaultTextPadding(textView);
        this.visible = false;
        this.text = null;
        this.textWidth = 0;
        this.badgeWidth = 0;
        int i = this.counter;
        if (i <= 0) {
            restoreTextPadding(textView);
            return;
        }
        this.visible = true;
        String string = Integer.toString(i);
        this.text = string;
        this.textWidth = (int) Math.ceil(Theme.dialogs_countTextPaint.measureText(string));
        int iMax = Math.max(AndroidUtilities.dp(10.0f), this.textWidth) + AndroidUtilities.dp(14.0f);
        this.badgeWidth = iMax;
        applyTextPadding(textView, this.defaultTextPaddingEnd + iMax + AndroidUtilities.dp(12.0f));
    }

    public void draw(View view, Canvas canvas) {
        if (this.visible) {
            float fDp = AndroidUtilities.dp(12.5f);
            int measuredWidth = view.getMeasuredWidth() - AndroidUtilities.dp(16.5f);
            int i = this.badgeWidth;
            float f = measuredWidth - i;
            this.rect.set(f, fDp, i + f, AndroidUtilities.dp(23.0f) + fDp);
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(11.5f), AndroidUtilities.dp(11.5f), Theme.dialogs_countGrayPaint);
            String str = this.text;
            RectF rectF = this.rect;
            canvas.drawText(str, rectF.left + ((rectF.width() - this.textWidth) / 2.0f), fDp + AndroidUtilities.dp(16.0f), Theme.dialogs_countTextPaint);
        }
    }

    private void rememberDefaultTextPadding(TextView textView) {
        if (this.defaultTextPaddingEnd != Integer.MIN_VALUE) {
            return;
        }
        this.defaultTextPaddingEnd = textView.getPaddingEnd();
    }

    private void restoreTextPadding(TextView textView) {
        int i = this.defaultTextPaddingEnd;
        if (i == Integer.MIN_VALUE) {
            i = 0;
        }
        applyTextPadding(textView, i);
    }

    private void applyTextPadding(TextView textView, int i) {
        if (textView.getPaddingEnd() == i) {
            return;
        }
        textView.setPaddingRelative(textView.getPaddingStart(), textView.getPaddingTop(), i, textView.getPaddingBottom());
    }
}
