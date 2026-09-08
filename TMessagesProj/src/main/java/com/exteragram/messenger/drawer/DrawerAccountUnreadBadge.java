package com.exteragram.messenger.drawer;

import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.View;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;

final class DrawerAccountUnreadBadge {
    private int badgeWidth;
    private int countWidth;
    private String text;
    private int textWidth;
    private boolean visible;
    private final RectF rect = new RectF();
    private int account = -1;

    public void bind(int i, SimpleTextView simpleTextView) {
        this.account = i;
        update(simpleTextView);
    }

    public void update(SimpleTextView simpleTextView) {
        this.visible = false;
        this.text = null;
        this.textWidth = 0;
        this.countWidth = 0;
        this.badgeWidth = 0;
        if (this.account < 0 || UserConfig.getActivatedAccountsCount() <= 1 || !NotificationsController.getInstance(this.account).showBadgeNumber) {
            simpleTextView.setRightPadding(0);
            return;
        }
        int mainUnreadCount = MessagesStorage.getInstance(this.account).getMainUnreadCount();
        if (mainUnreadCount <= 0) {
            simpleTextView.setRightPadding(0);
            return;
        }
        this.visible = true;
        String string = Integer.toString(mainUnreadCount);
        this.text = string;
        this.textWidth = (int) Math.ceil(Theme.dialogs_countTextPaint.measureText(string));
        int iMax = Math.max(AndroidUtilities.dp(10.0f), this.textWidth);
        this.countWidth = iMax;
        int iDp = iMax + AndroidUtilities.dp(14.0f);
        this.badgeWidth = iDp;
        simpleTextView.setRightPadding(iDp + AndroidUtilities.dp(12.0f));
    }

    public void draw(View view, Canvas canvas) {
        if (this.visible) {
            float fDp = AndroidUtilities.dp(23.0f);
            float measuredHeight = (view.getMeasuredHeight() - fDp) / 2.0f;
            int measuredWidth = view.getMeasuredWidth() - AndroidUtilities.dp(12.5f);
            int i = this.badgeWidth;
            float f = measuredWidth - i;
            this.rect.set(f, measuredHeight, i + f, fDp + measuredHeight);
            canvas.drawRoundRect(this.rect, AndroidUtilities.dp(11.5f), AndroidUtilities.dp(11.5f), Theme.dialogs_countPaint);
            float fCenterY = this.rect.centerY() - ((Theme.dialogs_countTextPaint.descent() + Theme.dialogs_countTextPaint.ascent()) / 2.0f);
            String str = this.text;
            RectF rectF = this.rect;
            canvas.drawText(str, rectF.left + ((rectF.width() - this.textWidth) / 2.0f), fCenterY, Theme.dialogs_countTextPaint);
        }
    }
}
