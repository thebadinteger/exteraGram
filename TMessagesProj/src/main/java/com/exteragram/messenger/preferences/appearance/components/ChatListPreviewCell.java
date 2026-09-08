package com.exteragram.messenger.preferences.appearance.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.FrameLayout;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.preferences.components.CustomPreferenceCell;
import com.exteragram.messenger.preferences.components.PreviewBackgroundDrawable;
import com.exteragram.messenger.utils.text.LocaleUtils;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.LayoutHelper;

@SuppressLint({"ViewConstructor"})
public class ChatListPreviewCell extends FrameLayout implements CustomPreferenceCell {
    private final ActionBar actionBar;
    private Drawable premiumStar;
    private final AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable statusDrawable;

    public ChatListPreviewCell(Context context) {
        super(context);
        setWillNotDraw(false);
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = new AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable(null, AndroidUtilities.dp(26.0f));
        this.statusDrawable = swapAnimatedEmojiDrawable;
        swapAnimatedEmojiDrawable.center = true;
        ActionBar actionBar = new ActionBar(context);
        this.actionBar = actionBar;
        actionBar.setItemsColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText), false);
        actionBar.setOccupyStatusBar(false);
        actionBar.createMenu().addItem(0, R.drawable.ic_ab_other);
        actionBar.setBackground(new PreviewBackgroundDrawable());
        actionBar.setSupportsHolidayImage(true);
        addView(actionBar, LayoutHelper.createFrame(-1, -2.0f, 17, 21.0f, 21.0f, 21.0f, 21.0f));
        updateStatus(false);
    }

    public void updateCentered(boolean z) {
        this.actionBar.refreshTitlePosition(z);
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.invalidate();
        }
    }

    /* JADX WARN: Code duplicated, block: B:18:0x0076  */
    public void updateStatus(boolean z) {
        Drawable drawable;
        if (this.actionBar == null) {
            return;
        }
        TLRPC.User currentUser = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
        if (currentUser == null || ExteraConfig.getHideActionBarStatus()) {
            drawable = null;
        } else {
            Long emojiStatusDocumentId = UserObject.getEmojiStatusDocumentId(currentUser);
            if (emojiStatusDocumentId != null) {
                this.statusDrawable.set(emojiStatusDocumentId.longValue(), z);
                this.statusDrawable.setColor(Integer.valueOf(Theme.getColor(Theme.key_profile_verifiedBackground)));
                drawable = this.statusDrawable;
            } else if (currentUser.premium) {
                if (this.premiumStar == null) {
                    Drawable drawableMutate = getContext().getResources().getDrawable(R.drawable.msg_premium_liststar).mutate();
                    drawableMutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_profile_verifiedBackground), PorterDuff.Mode.MULTIPLY));
                    this.premiumStar = new AnimatedEmojiDrawable.WrapSizeDrawable(drawableMutate, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f)) { 
                        @Override // org.telegram.ui.Components.AnimatedEmojiDrawable.WrapSizeDrawable, android.graphics.drawable.Drawable
                        public void draw(Canvas canvas) {
                            canvas.save();
                            canvas.translate(0.0f, AndroidUtilities.dp(1.0f));
                            super.draw(canvas);
                            canvas.restore();
                        }
                    };
                }
                drawable = this.premiumStar;
            } else {
                drawable = null;
            }
        }
        ActionBar actionBar = this.actionBar;
        if (z) {
            actionBar.setTitleAnimatedX(LocaleUtils.getActionBarTitle(), drawable, true, 250);
        } else {
            actionBar.setTitle(LocaleUtils.getActionBarTitle(), drawable);
        }
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(0, 0));
        setMeasuredDimension(View.MeasureSpec.getSize(i), getMeasuredHeight());
    }

    @Override 
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ChatListPreviewCell) {
            return Objects.equals(this.actionBar, ((ChatListPreviewCell) obj).actionBar);
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.statusDrawable;
        if (swapAnimatedEmojiDrawable != null) {
            swapAnimatedEmojiDrawable.attach();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = this.statusDrawable;
        if (swapAnimatedEmojiDrawable != null) {
            swapAnimatedEmojiDrawable.detach();
        }
    }
}
