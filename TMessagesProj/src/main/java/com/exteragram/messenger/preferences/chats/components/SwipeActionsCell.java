package com.exteragram.messenger.preferences.chats.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.preferences.components.CustomPreferenceCell;
import com.exteragram.messenger.utils.chats.SwipeAction;
import com.exteragram.messenger.utils.chats.SwipeActionsHelper;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;

/* JADX INFO: loaded from: classes4.dex */
public class SwipeActionsCell extends FrameLayout implements CustomPreferenceCell {
    private final List<SwipeAction> actions;
    private Drawable backgroundDrawable;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    private ValueAnimator cycle;
    private final SwipeActionsHelper helper;
    private boolean looped;
    private final ChatMessageCell messageCell;
    private final Drawable monetBackgroundDrawable;
    private List<SwipeAction> pending;
    private int selected;
    private final Drawable shadowDrawable;
    private float slide;

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchSetPressed(boolean z) {
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        return false;
    }

    public SwipeActionsCell(Context context) {
        super(context);
        this.actions = new ArrayList();
        setWillNotDraw(false);
        this.monetBackgroundDrawable = new ColorDrawable(Theme.getColor(Theme.key_windowBackgroundGray));
        this.shadowDrawable = Theme.getThemedDrawable(context, R.drawable.greydivider_bottom, Theme.key_windowBackgroundGrayShadow);
        this.helper = new SwipeActionsHelper(this, null);
        TLRPC.TL_message tL_message = new TLRPC.TL_message();
        tL_message.message = LocaleController.getString(R.string.SwipeActionsPreviewMessage);
        tL_message.date = ((int) (System.currentTimeMillis() / 1000)) - 3600;
        tL_message.dialog_id = 1L;
        tL_message.flags = 259;
        TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
        tL_message.from_id = tL_peerUser;
        tL_peerUser.user_id = UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId();
        tL_message.id = 1;
        tL_message.out = false;
        TLRPC.TL_peerUser tL_peerUser2 = new TLRPC.TL_peerUser();
        tL_message.peer_id = tL_peerUser2;
        tL_peerUser2.user_id = 0L;
        TLRPC.TL_message tL_message2 = new TLRPC.TL_message();
        tL_message2.message = LocaleController.getString(R.string.SwipeActionsPreviewReply);
        tL_message2.date = tL_message.date;
        tL_message2.dialog_id = 1L;
        tL_message2.flags = 259;
        TLRPC.TL_peerUser tL_peerUser3 = new TLRPC.TL_peerUser();
        tL_message2.from_id = tL_peerUser3;
        tL_peerUser3.user_id = tL_message.from_id.user_id;
        tL_message2.id = 2;
        tL_message2.media = new TLRPC.TL_messageMediaEmpty();
        tL_message2.out = true;
        TLRPC.TL_peerUser tL_peerUser4 = new TLRPC.TL_peerUser();
        tL_message2.peer_id = tL_peerUser4;
        tL_peerUser4.user_id = 1L;
        TLRPC.TL_messageReplyHeader tL_messageReplyHeader = new TLRPC.TL_messageReplyHeader();
        tL_message.reply_to = tL_messageReplyHeader;
        tL_messageReplyHeader.flags |= 16;
        tL_messageReplyHeader.reply_to_msg_id = tL_message2.id;
        tL_message.flags |= 8;
        MessageObject messageObject = new MessageObject(UserConfig.selectedAccount, tL_message, true, false);
        messageObject.replyMessageObject = new MessageObject(UserConfig.selectedAccount, tL_message2, true, false);
        messageObject.customReplyName = LocaleController.getString(R.string.FromYou);
        messageObject.resetLayout();
        messageObject.eventId = 1L;
        ChatMessageCell chatMessageCell = new ChatMessageCell(context, UserConfig.selectedAccount);
        this.messageCell = chatMessageCell;
        chatMessageCell.isChat = false;
        chatMessageCell.setFullyDraw(true);
        chatMessageCell.setMessageObject(messageObject, null, false, false, false);
        addView(chatMessageCell, LayoutHelper.createFrame(-1, -2, 16));
        updateActions();
    }

    public void updateActions() {
        List<SwipeAction> listEnabled = SwipeAction.enabled();
        if (listEnabled.equals(this.actions) && this.looped == ExteraConfig.getSwipeActionsLoop()) {
            return;
        }
        if (this.cycle == null || this.actions.isEmpty() || listEnabled.isEmpty()) {
            applyActions(listEnabled);
        } else {
            this.pending = listEnabled;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyActions(List<SwipeAction> list) {
        String strQuickReactionEmoticon;
        this.actions.clear();
        this.actions.addAll(list);
        this.looped = ExteraConfig.getSwipeActionsLoop();
        this.selected = 0;
        this.helper.start(this.actions);
        this.helper.select(0);
        if (this.actions.contains(SwipeAction.REACTION) && (strQuickReactionEmoticon = SwipeAction.quickReactionEmoticon(UserConfig.selectedAccount)) != null) {
            ReactionsLayoutInBubble.VisibleReaction visibleReactionFromEmojicon = ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(strQuickReactionEmoticon);
            this.helper.setReaction(UserConfig.selectedAccount, visibleReactionFromEmojicon.emojicon, visibleReactionFromEmojicon.documentId);
        }
        restartCycle();
    }

    /* JADX WARN: Code duplicated, block: B:15:0x0035  */
    private void restartCycle() {
        int i = 0;
        ValueAnimator valueAnimator = this.cycle;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.cycle = null;
        }
        this.slide = 0.0f;
        this.messageCell.setSlidingOffset(0.0f);
        if (this.actions.isEmpty() || !isAttachedToWindow()) {
            invalidate();
            return;
        }
        int size = this.actions.size();
        if (this.looped) {
            i = this.actions.size() <= 1 ? 0 : 1;
        }
        final int i2 = size + i;
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.cycle = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(((long) (i2 + 2)) * 1100);
        this.cycle.setInterpolator(new LinearInterpolator());
        this.cycle.setRepeatCount(-1);
        this.cycle.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.exteragram.messenger.preferences.chats.components.SwipeActionsCell$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                SwipeActionsCell.this.lambda$restartCycle$0(i2, valueAnimator2);
            }
        });
        this.cycle.addListener(new AnonymousClass1());
        this.cycle.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$restartCycle$0(int i, ValueAnimator valueAnimator) {
        int iMax;
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        float f = 1.0f / (i + 2);
        if (fFloatValue < f) {
            this.slide = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(fFloatValue / f);
        } else {
            float f2 = 1.0f - f;
            if (fFloatValue > f2) {
                this.slide = 1.0f - CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation((fFloatValue - f2) / f);
            } else {
                this.slide = 1.0f;
            }
        }
        if (fFloatValue >= f && fFloatValue <= 1.0f - f && (iMax = Math.max(0, Math.min(i - 1, (int) ((fFloatValue - f) / f))) % this.actions.size()) != this.selected) {
            this.selected = iMax;
            this.helper.select(iMax);
        }
        this.messageCell.setSlidingOffset((-this.slide) * AndroidUtilities.dp(72.0f));
        invalidate();
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.preferences.chats.components.SwipeActionsCell$1, reason: invalid class name */
    public class AnonymousClass1 extends AnimatorListenerAdapter {
        public AnonymousClass1() {
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
            List list = SwipeActionsCell.this.pending;
            SwipeActionsCell swipeActionsCell = SwipeActionsCell.this;
            if (list != null) {
                final List list2 = swipeActionsCell.pending;
                SwipeActionsCell.this.pending = null;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.preferences.chats.components.SwipeActionsCell$1$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        SwipeActionsCell.this.applyActions(list2);
                    }
                });
            } else {
                swipeActionsCell.selected = 0;
                SwipeActionsCell.this.helper.start(SwipeActionsCell.this.actions);
                SwipeActionsCell.this.helper.select(0);
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAnimationRepeat$0(List list) {
            SwipeActionsCell.this.applyActions(list);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (this.actions.isEmpty()) {
            return;
        }
        float fDp = (-this.slide) * AndroidUtilities.dp(72.0f);
        this.helper.draw(canvas, fDp, false, this.messageCell.getTop() + (this.messageCell.getMeasuredHeight() / 2.0f), this.messageCell.getBackgroundDrawableRight() + fDp);
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        Drawable cachedWallpaperNonBlocking = Theme.isCurrentThemeMonet() ? this.monetBackgroundDrawable : Theme.getCachedWallpaperNonBlocking();
        if (cachedWallpaperNonBlocking != this.backgroundDrawable && cachedWallpaperNonBlocking != null) {
            BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
            if (disposable != null) {
                disposable.dispose();
                this.backgroundGradientDisposable = null;
            }
            this.backgroundDrawable = cachedWallpaperNonBlocking;
        }
        Drawable drawable = this.backgroundDrawable;
        if (drawable != null) {
            drawable.setAlpha(drawable == this.monetBackgroundDrawable ? 150 : 255);
            drawWallpaper(canvas, this.backgroundDrawable);
        }
        this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        this.shadowDrawable.draw(canvas);
    }

    private void drawWallpaper(Canvas canvas, Drawable drawable) {
        if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable) || (drawable instanceof MotionBackgroundDrawable)) {
            drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
            if (drawable instanceof BackgroundGradientDrawable) {
                this.backgroundGradientDisposable = ((BackgroundGradientDrawable) drawable).drawExactBoundsSize(canvas, this);
                return;
            } else {
                drawable.draw(canvas);
                return;
            }
        }
        if (drawable instanceof BitmapDrawable) {
            canvas.save();
            if (((BitmapDrawable) drawable).getTileModeX() == Shader.TileMode.REPEAT) {
                float f = 2.0f / AndroidUtilities.density;
                canvas.scale(f, f);
                drawable.setBounds(0, 0, (int) Math.ceil(getMeasuredWidth() / f), (int) Math.ceil(getMeasuredHeight() / f));
            } else {
                float fMax = Math.max(getMeasuredWidth() / drawable.getIntrinsicWidth(), getMeasuredHeight() / drawable.getIntrinsicHeight());
                int iCeil = (int) Math.ceil(drawable.getIntrinsicWidth() * fMax);
                int iCeil2 = (int) Math.ceil(drawable.getIntrinsicHeight() * fMax);
                canvas.clipRect(0, 0, iCeil, getMeasuredHeight());
                drawable.setBounds((getMeasuredWidth() - iCeil) / 2, (getMeasuredHeight() - iCeil2) / 2, ((getMeasuredWidth() - iCeil) / 2) + iCeil, ((getMeasuredHeight() - iCeil2) / 2) + iCeil2);
            }
            drawable.draw(canvas);
            canvas.restore();
        }
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(150.0f), TLObject.FLAG_30));
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (this.cycle == null) {
            restartCycle();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        ValueAnimator valueAnimator = this.cycle;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.cycle = null;
        }
        this.helper.detach();
        BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
        if (disposable != null) {
            disposable.dispose();
            this.backgroundGradientDisposable = null;
        }
    }

    @Override // com.exteragram.messenger.preferences.components.CustomPreferenceCell
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof SwipeActionsCell) {
            return this.actions.equals(((SwipeActionsCell) obj).actions);
        }
        return false;
    }
}
