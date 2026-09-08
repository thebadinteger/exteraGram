package com.exteragram.messenger.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import com.exteragram.messenger.MainMenuItem;
import com.exteragram.messenger.feed.FeedController;
import com.exteragram.messenger.utils.ui.UIUtil;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.MessagesStorage;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class DrawerMenuItemView extends FrameLayout {
    private final ImageView iconView;
    private int layoutButtonId;
    private final TextView textView;
    private final DrawerUnreadBadge unreadBadge;
    private static final int COLOR_KEY_SELECTOR = Theme.key_listSelector;
    private static final int COLOR_KEY_ICON = Theme.key_windowBackgroundWhiteGrayIcon;
    private static final int COLOR_KEY_TEXT = Theme.key_windowBackgroundWhiteBlackText;

    public DrawerMenuItemView(Context context) {
        super(context);
        this.layoutButtonId = Integer.MIN_VALUE;
        setWillNotDraw(false);
        setLayoutParams(new FrameLayout.LayoutParams(-1, AndroidUtilities.dp(48.0f)));
        setBackground(createSelectorDrawable());
        UIUtil.applyScaleStateListAnimator(this, 16.0f, false, false, 2, 0.04f, 1.5f);
        ImageView imageView = new ImageView(context);
        this.iconView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        imageView.setColorFilter(createIconColorFilter());
        addView(imageView, LayoutHelper.createFrame(24, 24.0f, 19, 20.0f, 0.0f, 0.0f, 0.0f));
        TextView textView = new TextView(context);
        this.textView = textView;
        textView.setTextSize(1, 15.0f);
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextColor(Theme.getColor(COLOR_KEY_TEXT));
        textView.setGravity(19);
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        addView(textView, LayoutHelper.createFrame(-1, -1.0f, 19, 68.0f, 0.0f, 16.0f, 0.0f));
        this.unreadBadge = new DrawerUnreadBadge();
    }

    public void setMenuItem(int i, int i2, int i3, CharSequence charSequence) {
        this.layoutButtonId = i;
        this.iconView.setImageResource(i3);
        this.textView.setText(charSequence);
        updateUnreadCounter(i2);
    }

    public void updateUnreadCounter(int i) {
        this.unreadBadge.bind(resolveUnreadCounter(i), this.textView);
        invalidate();
    }

    private int resolveUnreadCounter(int i) {
        if (this.layoutButtonId == MainMenuItem.ARCHIVE.getId()) {
            return MessagesStorage.getInstance(i).getArchiveUnreadCount();
        }
        if (this.layoutButtonId == MainMenuItem.FEED.getId()) {
            return FeedController.getInstance(i).getUnreadCount();
        }
        return 0;
    }

    public void updateColors() {
        setBackground(createSelectorDrawable());
        this.iconView.setColorFilter(createIconColorFilter());
        this.textView.setTextColor(Theme.getColor(COLOR_KEY_TEXT));
        invalidate();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        this.unreadBadge.draw(this, canvas);
    }

    private static Drawable createSelectorDrawable() {
        return Theme.createRadSelectorDrawable(Theme.getColor(COLOR_KEY_SELECTOR), 12, 12);
    }

    private static PorterDuffColorFilter createIconColorFilter() {
        return new PorterDuffColorFilter(Theme.getColor(COLOR_KEY_ICON), PorterDuff.Mode.SRC_IN);
    }
}
