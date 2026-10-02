package com.exteragram.messenger.utils.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.TextPaint;
import com.exteragram.messenger.ExteraConfig;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.BaseCell;
import org.telegram.ui.Cells.ChatMessageCell;

/* JADX INFO: loaded from: classes4.dex */
public final class ForwardsCountUiHelper {
    private float animateFromX;
    private StaticLayout animateLayout;
    private boolean animating;
    private int count;
    private long dialogId;
    private Drawable drawable;
    private StaticLayout lastLayout;
    private float lastX;
    private StaticLayout layout;
    private int messageId;
    private float textSize;
    private Typeface typeface;
    private int width;
    private final PorterDuffColorFilter[] colorFilters = new PorterDuffColorFilter[2];
    private final int[] colors = new int[2];
    private boolean allowed = true;
    private int account = -1;

    private static int getCount(MessageObject messageObject) {
        TLRPC.Message message;
        if (!ExteraConfig.getShowForwardsCount() || messageObject == null || (message = messageObject.messageOwner) == null || (message.flags & 1024) == 0 || messageObject.scheduled || messageObject.notime || messageObject.isSponsored() || messageObject.isQuickReply() || messageObject.isWelcomeMessage()) {
            return 0;
        }
        return Math.max(0, messageObject.messageOwner.forwards);
    }

    public boolean isChanged(MessageObject messageObject) {
        return this.count != (this.allowed ? getCount(messageObject) : 0);
    }

    public void bind(MessageObject messageObject) {
        if (messageObject != null) {
            if (this.account == messageObject.currentAccount && this.dialogId == messageObject.getDialogId() && this.messageId == messageObject.getId()) {
                return;
            }
            this.account = messageObject.currentAccount;
            this.dialogId = messageObject.getDialogId();
            this.messageId = messageObject.getId();
            this.layout = null;
            this.lastLayout = null;
            this.count = 0;
            this.width = 0;
            this.lastX = 0.0f;
            this.allowed = true;
            resetAnimation();
        }
    }

    public int measure(Context context, MessageObject messageObject, boolean z) {
        this.allowed = z;
        int count = z ? getCount(messageObject) : 0;
        this.count = count;
        if (count == 0) {
            this.layout = null;
            this.width = 0;
            return 0;
        }
        if (this.drawable == null) {
            this.drawable = context.getResources().getDrawable(R.drawable.msg_reply_small).mutate();
        }
        TextPaint textPaint = Theme.chat_timePaint;
        String shortNumber = LocaleController.formatShortNumber(this.count, null);
        StaticLayout staticLayout = this.layout;
        if (staticLayout == null || !TextUtils.equals(staticLayout.getText(), shortNumber) || this.textSize != textPaint.getTextSize() || this.typeface != textPaint.getTypeface()) {
            this.textSize = textPaint.getTextSize();
            this.typeface = textPaint.getTypeface();
            this.layout = new StaticLayout(shortNumber, textPaint, Math.max(1, (int) Math.ceil(textPaint.measureText(shortNumber))), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
        }
        int iCeil = (int) Math.ceil(this.layout.getWidth() + ((this.drawable.getIntrinsicWidth() * textPaint.getTextSize()) / this.drawable.getIntrinsicHeight()) + AndroidUtilities.dp(10.0f));
        this.width = iCeil;
        return iCeil;
    }

    public void recordDrawingState() {
        this.lastLayout = this.layout;
    }

    public boolean animateChange() {
        this.animateFromX = this.lastX;
        StaticLayout staticLayout = this.layout;
        StaticLayout staticLayout2 = this.lastLayout;
        boolean z = staticLayout != staticLayout2;
        this.animating = z;
        if (!z) {
            staticLayout2 = null;
        }
        this.animateLayout = staticLayout2;
        return z;
    }

    public void resetAnimation() {
        this.animating = false;
        this.animateLayout = null;
    }

    /* JADX WARN: Multi-variable type inference failed */
    public float draw(Canvas canvas, ChatMessageCell chatMessageCell, float f, float f2, float f3, float f4, float f5, boolean z) {
        int i;
        float f6;
        float f7;
        StaticLayout staticLayout = this.layout;
        if (staticLayout == null && this.animateLayout == null) {
            return 0.0f;
        }
        ChatMessageCell.TransitionParams transitionParams = chatMessageCell.transitionParams;
        float f8 = transitionParams.animateChangeProgress;
        boolean z2 = this.animating;
        boolean z3 = false;
        boolean zEntering = z2 && this.animateLayout == null;
        if (z2 && staticLayout == null) {
            z3 = true;
        }
        boolean z4 = transitionParams.shouldAnimateTimeX;
        float animationOffsetX = (z4 ? chatMessageCell.timeX : f) + f3;
        if (z4 && !zEntering && (!chatMessageCell.getMessageObject().isRoundVideo() || !transitionParams.animateDrawBackground)) {
            float fLerp = this.animateFromX;
            if (!z3) {
                fLerp = AndroidUtilities.lerp(fLerp, animationOffsetX, f8);
            }
            animationOffsetX = fLerp;
        }
        MessageObject.GroupedMessages currentMessagesGroup = chatMessageCell.getCurrentMessagesGroup();
        if (currentMessagesGroup != null) {
            MessageObject.GroupedMessages.TransitionParams transitionParams2 = currentMessagesGroup.transitionParams;
            if (transitionParams2.backgroundChangeBounds) {
                animationOffsetX += transitionParams2.offsetRight;
            }
        }
        if (transitionParams.animateBackgroundBoundsInner) {
            animationOffsetX += chatMessageCell.getAnimationOffsetX();
        }
        this.lastX = animationOffsetX;
        TextPaint textPaint = Theme.chat_timePaint;
        int alpha = textPaint.getAlpha();
        int i2 = (int) (alpha * f5);
        if (chatMessageCell.shouldDrawTimeOnMedia()) {
            i = chatMessageCell.getMessageObject().shouldDrawWithoutBackground() ? Theme.key_chat_serviceText : Theme.key_chat_mediaViews;
        } else if (chatMessageCell.getMessageObject().isOutOwner()) {
            i = z ? Theme.key_chat_outViewsSelected : Theme.key_chat_outViews;
        } else {
            i = z ? Theme.key_chat_inViewsSelected : Theme.key_chat_inViews;
        }
        int themedColor = chatMessageCell.getThemedColor(i);
        PorterDuffColorFilter[] porterDuffColorFilterArr = this.colorFilters;
        if (porterDuffColorFilterArr[z ? 1 : 0] == null || this.colors[z ? 1 : 0] != themedColor) {
            this.colors[z ? 1 : 0] = themedColor;
            porterDuffColorFilterArr[z ? 1 : 0] = new PorterDuffColorFilter(themedColor, PorterDuff.Mode.SRC_IN);
        }
        this.drawable.setColorFilter(this.colorFilters[z ? 1 : 0]);
        float drawableBounds = BaseCell.setDrawableBounds(this.drawable, animationOffsetX, f2, textPaint.getTextSize());
        canvas.save();
        if (f5 != 1.0f) {
            float f9 = (f5 * 0.5f) + 0.5f;
            StaticLayout staticLayout2 = this.layout;
            if (staticLayout2 == null) {
                staticLayout2 = this.animateLayout;
            }
            f6 = 3.0f;
            canvas.scale(f9, f9, (((AndroidUtilities.dp(3.0f) + drawableBounds) + staticLayout2.getWidth()) / 2.0f) + animationOffsetX, this.drawable.getBounds().centerY());
        } else {
            f6 = 3.0f;
        }
        if (zEntering) {
            f7 = f8;
        } else {
            f7 = z3 ? 1.0f - f8 : 1.0f;
        }
        this.drawable.setAlpha((int) (255.0f * f4 * f7));
        canvas.save();
        canvas.scale(-1.0f, 1.0f, this.drawable.getBounds().exactCenterX(), 0.0f);
        this.drawable.draw(canvas);
        canvas.restore();
        canvas.translate(animationOffsetX + drawableBounds + AndroidUtilities.dp(f6), f2);
        if (this.animateLayout != null) {
            textPaint.setAlpha((int) (i2 * (1.0f - f8)));
            this.animateLayout.draw(canvas);
        }
        if (this.layout != null) {
            float f10 = i2;
            if (!this.animating) {
                f8 = 1.0f;
            }
            textPaint.setAlpha((int) (f10 * f8));
            this.layout.draw(canvas);
        }
        canvas.restore();
        textPaint.setAlpha(alpha);
        return this.width;
    }

    public void appendAccessibilityText(SpannableStringBuilder spannableStringBuilder) {
        if (this.count > 0) {
            spannableStringBuilder.append("\n").append((CharSequence) String.format(LocaleController.getPluralString("Shares", this.count), AndroidUtilities.formatCount(this.count)));
        }
    }
}
