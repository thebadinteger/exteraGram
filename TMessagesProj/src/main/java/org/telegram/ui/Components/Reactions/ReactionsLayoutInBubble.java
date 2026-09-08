package org.telegram.ui.Components.Reactions;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.ChatListItemAnimator;
import com.exteragram.messenger.ExteraConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Objects;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.IMessageCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFileDrawable;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AvatarsDrawable;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CounterView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Stars.StarsReactionsSheet;
import org.webrtc.GlShader$$ExternalSyntheticBUOutline1;

public class ReactionsLayoutInBubble {
    private static int animationUniq;
    private int animateFromTotalHeight;
    public boolean animateHeight;
    private boolean animateMove;
    private boolean animateWidth;
    boolean attached;
    int availableWidth;
    public float drawServiceShaderBackground;
    public int fromWidth;
    private float fromX;
    private float fromY;
    public boolean hasPaidReaction;
    public boolean hasUnreadReactions;
    public int height;
    public boolean isEmpty;
    public boolean isSmall;
    private int lastDrawTotalHeight;
    private int lastDrawnWidth;
    private float lastDrawnX;
    private float lastDrawnY;
    public int lastLineX;
    ReactionButton lastSelectedButton;
    float lastX;
    float lastY;
    Runnable longPressRunnable;
    MessageObject messageObject;
    View parentView;
    public int positionOffsetY;
    boolean pressed;
    Theme.ResourcesProvider resourcesProvider;
    private boolean scrimDirection;
    private float scrimProgress;
    private Integer scrimViewReaction;
    public boolean tags;
    public int totalHeight;
    private float touchSlop;
    private boolean wasDrawn;
    public int width;
    public int x;
    public int y;
    private static final Paint paint = new Paint(1);
    private static final Paint tagPaint = new Paint(1);
    private static final Paint cutTagPaint = new Paint(1);
    private static final TextPaint textPaint = new TextPaint(1);
    private static final ButtonsComparator comparator = new ButtonsComparator();
    private static int pointer = 1;
    private static final Comparator<TLObject> usersComparator = new Comparator() { // from class: org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble$$ExternalSyntheticLambda0
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return ReactionsLayoutInBubble.$r8$lambda$PUvINze9IkR1wVS4VbZduDR1Brs((TLObject) obj, (TLObject) obj2);
        }
    };
    public ArrayList<ReactionButton> reactionButtons = new ArrayList<>();
    ArrayList<ReactionButton> outButtons = new ArrayList<>();
    HashMap<String, ReactionButton> lastDrawingReactionButtons = new HashMap<>();
    HashMap<String, ReactionButton> lastDrawingReactionButtonsTmp = new HashMap<>();
    final HashMap<VisibleReaction, ImageReceiver> animatedReactions = new HashMap<>();
    private final ArrayList<Integer> reactionLineWidths = new ArrayList<>();
    private final RectF scrimRect = new RectF();
    private final Rect scrimRect2 = new Rect();
    int currentAccount = UserConfig.selectedAccount;

    public static public boolean drawOverlay(Canvas canvas, float f) {
        float f2;
        float f3;
        Canvas canvas2 = canvas;
        if (this.isEmpty && this.outButtons.isEmpty()) {
            return false;
        }
        float f4 = this.x;
        float f5 = this.y;
        float f6 = 1.0f;
        if (this.isEmpty) {
            f4 = this.lastDrawnX;
            f5 = this.lastDrawnY;
        } else if (this.animateMove) {
            float f7 = 1.0f - f;
            f4 = (f4 * f) + (this.fromX * f7);
            f5 = (f5 * f) + (this.fromY * f7);
        }
        float f8 = f4;
        float f9 = f5;
        boolean z = false;
        int i = 0;
        while (i < this.reactionButtons.size()) {
            ReactionButton reactionButton = this.reactionButtons.get(i);
            if (reactionButton.paid) {
                canvas2.save();
                int i2 = reactionButton.x;
                float f10 = i2;
                int i3 = reactionButton.y;
                float f11 = i3;
                f2 = f6;
                if (f != f6 && reactionButton.animationType == 3) {
                    float f12 = f2 - f;
                    f10 = (reactionButton.animateFromX * f12) + (i2 * f);
                    f11 = (i3 * f) + (reactionButton.animateFromY * f12);
                }
                if (f == f6 || reactionButton.animationType != 1) {
                    f3 = f2;
                } else {
                    float f13 = (f * 0.5f) + 0.5f;
                    canvas2.scale(f13, f13, f8 + f10 + (reactionButton.width / 2.0f), f9 + f11 + (reactionButton.height / 2.0f));
                    f3 = f;
                }
                if (z) {
                    z = true;
                } else {
                    if (reactionButton.drawOverlay(canvas2, f10 + f8, f9 + f11, reactionButton.animationType == 3 ? f : f2, f3, false)) {
                        z = true;
                    } else {
                        z = false;
                    }
                }
                canvas2.restore();
            } else {
                f2 = f6;
            }
            i++;
            f6 = f2;
        }
        float f14 = f6;
        int i4 = 0;
        while (i4 < this.outButtons.size()) {
            ReactionButton reactionButton2 = this.outButtons.get(i4);
            if (reactionButton2.paid) {
                float f15 = f14 - f;
                float f16 = (f15 * 0.5f) + 0.5f;
                canvas2.save();
                canvas2.scale(f16, f16, reactionButton2.x + f8 + (reactionButton2.width / 2.0f), reactionButton2.y + f9 + (reactionButton2.height / 2.0f));
                z = z || this.outButtons.get(i4).drawOverlay(canvas2, ((float) reactionButton2.x) + f8, ((float) reactionButton2.y) + f9, 1.0f, f15, false);
                canvas.restore();
            }
            i4++;
            canvas2 = canvas;
        }
        return z;
    }

    public void drawPreview(View view, Canvas canvas, int i, Integer num) {
        if (this.isEmpty && this.outButtons.isEmpty()) {
            return;
        }
        for (int i2 = 0; i2 < this.reactionButtons.size(); i2++) {
            ReactionButton reactionButton = this.reactionButtons.get(i2);
            if ((num == null || reactionButton.reaction.hashCode() == num.intValue()) && num != null) {
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set(reactionButton.drawingImageRect);
                float fDp = AndroidUtilities.dp(140.0f);
                float fDp2 = AndroidUtilities.dp(14.0f);
                float fClamp = Utilities.clamp(rectF.left - AndroidUtilities.dp(12.0f), (getParentWidth() - fDp) - AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f));
                RectF rectF2 = this.scrimRect;
                float f = rectF.top;
                float f2 = i;
                rectF2.set(fClamp, ((f - fDp2) - fDp) + f2, fDp + fClamp, (f - fDp2) + f2);
                float interpolation = CubicBezierInterpolator.EASE_OUT_QUINT.getInterpolation(this.scrimProgress);
                RectF rectF3 = this.scrimRect;
                AndroidUtilities.lerp(rectF, rectF3, interpolation, rectF3);
                reactionButton.attachPreview(view);
                Rect rect = this.scrimRect2;
                RectF rectF4 = this.scrimRect;
                rect.set((int) rectF4.left, (int) rectF4.top, (int) rectF4.right, (int) rectF4.bottom);
                reactionButton.drawPreview(view, canvas, this.scrimRect, interpolation);
            }
        }
    }

    private int getParentWidth() {
        View view = this.parentView;
        if (view instanceof ChatMessageCell) {
            return ((ChatMessageCell) view).getParentWidth();
        }
        return AndroidUtilities.displaySize.x;
    }

    private void didPressReaction(TLRPC.ReactionCount reactionCount, boolean z, float f, float f2) {
        KeyEvent.Callback callback = this.parentView;
        if (callback instanceof IMessageCell) {
            ((IMessageCell) callback).didPressReactionFromLayout(reactionCount, z, f, f2);
        }
    }

    public void recordDrawingState() {
        this.lastDrawingReactionButtons.clear();
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.lastDrawingReactionButtons.put(this.reactionButtons.get(i).key, this.reactionButtons.get(i));
        }
        this.wasDrawn = !this.isEmpty;
        this.lastDrawnX = this.x;
        this.lastDrawnY = this.y;
        this.lastDrawnWidth = this.width;
        this.lastDrawTotalHeight = this.totalHeight;
    }

    public boolean animateChange() {
        ArrayList<ReactionButton> arrayList;
        AvatarsDrawable avatarsDrawable;
        CounterView.CounterDrawable counterDrawable;
        if (this.messageObject == null) {
            return false;
        }
        this.lastDrawingReactionButtonsTmp.clear();
        int i = 0;
        while (true) {
            int size = this.outButtons.size();
            arrayList = this.outButtons;
            if (i >= size) {
                break;
            }
            arrayList.get(i).detach();
            i++;
        }
        arrayList.clear();
        this.lastDrawingReactionButtonsTmp.putAll(this.lastDrawingReactionButtons);
        boolean z = false;
        for (int i2 = 0; i2 < this.reactionButtons.size(); i2++) {
            ReactionButton reactionButton = this.reactionButtons.get(i2);
            ReactionButton reactionButton2 = this.lastDrawingReactionButtonsTmp.get(reactionButton.key);
            if (reactionButton2 != null && reactionButton.isSmall != reactionButton2.isSmall) {
                reactionButton2 = null;
            }
            if (reactionButton2 != null) {
                this.lastDrawingReactionButtonsTmp.remove(reactionButton.key);
                int i3 = reactionButton.x;
                int i4 = reactionButton2.x;
                if (i3 != i4 || reactionButton.y != reactionButton2.y || reactionButton.width != reactionButton2.width || reactionButton.count != reactionButton2.count || reactionButton.choosen != reactionButton2.choosen || reactionButton.avatarsDrawable != null || reactionButton2.avatarsDrawable != null) {
                    reactionButton.animateFromX = i4;
                    reactionButton.animateFromY = reactionButton2.y;
                    reactionButton.animateFromWidth = reactionButton2.width;
                    reactionButton.fromTextColor = reactionButton2.lastDrawnTextColor;
                    reactionButton.fromBackgroundColor = reactionButton2.lastDrawnBackgroundColor;
                    reactionButton.fromTagDotColor = reactionButton2.lastDrawnTagDotColor;
                    reactionButton.animationType = 3;
                    int i5 = reactionButton.count;
                    int i6 = reactionButton2.count;
                    if (i5 != i6 && (counterDrawable = reactionButton.counterDrawable) != null) {
                        counterDrawable.setCount(i6, false);
                        reactionButton.counterDrawable.setCount(reactionButton.count, true);
                    }
                    AvatarsDrawable avatarsDrawable2 = reactionButton.avatarsDrawable;
                    if (avatarsDrawable2 != null || reactionButton2.avatarsDrawable != null) {
                        if (avatarsDrawable2 == null) {
                            reactionButton.setUsers(new ArrayList<>());
                        }
                        if (reactionButton2.avatarsDrawable == null) {
                            reactionButton2.setUsers(new ArrayList<>());
                        }
                        if (!equalsUsersList(reactionButton2.users, reactionButton.users) && (avatarsDrawable = reactionButton.avatarsDrawable) != null) {
                            avatarsDrawable.animateFromState(reactionButton2.avatarsDrawable, this.currentAccount, false);
                        }
                    }
                } else {
                    reactionButton.animationType = 0;
                }
            } else {
                reactionButton.animationType = 1;
            }
            z = true;
        }
        if (!this.lastDrawingReactionButtonsTmp.isEmpty()) {
            this.outButtons.addAll(this.lastDrawingReactionButtonsTmp.values());
            for (int i7 = 0; i7 < this.outButtons.size(); i7++) {
                this.outButtons.get(i7).drawImage = this.outButtons.get(i7).lastImageDrawn;
                this.outButtons.get(i7).attach();
            }
            z = true;
        }
        if (this.wasDrawn) {
            float f = this.lastDrawnX;
            if (f != this.x || this.lastDrawnY != this.y) {
                this.animateMove = true;
                this.fromX = f;
                this.fromY = this.lastDrawnY;
                z = true;
            }
        }
        int i8 = this.lastDrawnWidth;
        if (i8 != this.width) {
            this.animateWidth = true;
            this.fromWidth = i8;
            z = true;
        }
        int i9 = this.lastDrawTotalHeight;
        if (i9 == this.totalHeight) {
            return z;
        }
        this.animateHeight = true;
        this.animateFromTotalHeight = i9;
        return true;
    }

    private boolean equalsUsersList(ArrayList<TLObject> arrayList, ArrayList<TLObject> arrayList2) {
        if (arrayList == null || arrayList2 == null || arrayList.size() != arrayList2.size()) {
            return false;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            TLObject tLObject = arrayList.get(i);
            TLObject tLObject2 = arrayList2.get(i);
            if (tLObject == null || tLObject2 == null || getPeerId(tLObject) != getPeerId(tLObject2)) {
                return false;
            }
        }
        return true;
    }

    public void resetAnimation() {
        ArrayList<ReactionButton> arrayList;
        int i = 0;
        while (true) {
            int size = this.outButtons.size();
            arrayList = this.outButtons;
            if (i >= size) {
                break;
            }
            arrayList.get(i).detach();
            i++;
        }
        arrayList.clear();
        this.animateMove = false;
        this.animateWidth = false;
        this.animateHeight = false;
        for (int i2 = 0; i2 < this.reactionButtons.size(); i2++) {
            this.reactionButtons.get(i2).animationType = 0;
        }
    }

    public ReactionButton getReactionButton(VisibleReaction visibleReaction) {
        String string;
        if (visibleReaction.isStar) {
            string = "stars";
        } else {
            String str = visibleReaction.emojicon;
            string = str != null ? str : Long.toString(visibleReaction.documentId);
        }
        return getReactionButton(string);
    }

    public ReactionButton getReactionButton(String str) {
        if (this.isSmall) {
            ReactionButton reactionButton = this.lastDrawingReactionButtons.get(str + "_");
            if (reactionButton != null) {
                return reactionButton;
            }
        }
        return this.lastDrawingReactionButtons.get(str);
    }

    public void setScrimReaction(Integer num) {
        this.scrimViewReaction = num;
    }

    public void setScrimProgress(float f) {
        this.scrimProgress = f;
    }

    public void setScrimProgress(float f, boolean z) {
        this.scrimProgress = f;
        this.scrimDirection = z;
    }

    public class ReactionLayoutButton extends ReactionButton {
        public ReactionLayoutButton(ReactionButton reactionButton, TLRPC.ReactionCount reactionCount, boolean z, boolean z2) {
            super(reactionButton, ReactionsLayoutInBubble.this.currentAccount, ReactionsLayoutInBubble.this.parentView, reactionCount, z, z2, ReactionsLayoutInBubble.this.resourcesProvider);
        }

        @Override // org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.ReactionButton
        public boolean isPlaying() {
            return ReactionsEffectOverlay.isPlaying(ReactionsLayoutInBubble.this.messageObject.getId(), ReactionsLayoutInBubble.this.messageObject.getGroupId(), this.visibleReaction);
        }

        @Override // org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.ReactionButton
        public boolean isOutOwner() {
            return ReactionsLayoutInBubble.this.messageObject.isOutOwner();
        }

        @Override // org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.ReactionButton
        public float getDrawServiceShaderBackground() {
            return ReactionsLayoutInBubble.this.drawServiceShaderBackground;
        }

        @Override // org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.ReactionButton
        public ImageReceiver getImageReceiver() {
            return ReactionsLayoutInBubble.this.animatedReactions.get(this.visibleReaction);
        }

        @Override // org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.ReactionButton
        public void removeImageReceiver() {
            ReactionsLayoutInBubble.this.animatedReactions.remove(this.visibleReaction);
        }
    }

    public boolean verifyDrawable(Drawable drawable) {
        return drawable instanceof AnimatedTextView.AnimatedTextDrawable;
    }

    public static class ReactionButton {
        public int animateFromWidth;
        public int animateFromX;
        public int animateFromY;
        public AnimatedEmojiDrawable animatedEmojiDrawable;
        int animatedEmojiDrawableColor;
        public int animationType;
        public boolean attached;
        AvatarsDrawable avatarsDrawable;
        int backgroundColor;
        public final ButtonBounce bounce;
        public boolean choosen;
        public int choosenOrder;
        public int count;
        public String countText;
        public CounterView.CounterDrawable counterDrawable;
        private final int currentAccount;
        public boolean drawBgOnlyIfChosen;
        public int fromBackgroundColor;
        public int fromTagDotColor;
        public int fromTextColor;
        public boolean hasName;
        public int height;
        public ImageReceiver imageReceiver;
        public boolean inGroup;
        boolean isSelected;
        private final boolean isSmall;
        public boolean isTag;
        public String key;
        public int lastDrawnBackgroundColor;
        public int lastDrawnTagDotColor;
        public int lastDrawnTextColor;
        public boolean lastImageDrawn;
        private boolean lastScrimProgressDirection;
        public String name;
        public boolean paid;
        private final View parentView;
        private StarsReactionsSheet.Particles particles;
        public AnimatedEmojiDrawable previewAnimatedEmojiDrawable;
        public ImageReceiver previewImageReceiver;
        public TLRPC.Reaction reaction;
        private final TLRPC.ReactionCount reactionCount;
        public int realCount;
        private final Theme.ResourcesProvider resourcesProvider;
        public AnimatedTextView.AnimatedTextDrawable scrimPreviewCounterDrawable;
        int serviceBackgroundColor;
        int serviceTextColor;
        private RLottieDrawable starDrawable;
        private final Drawable.Callback supercallback;
        int textColor;
        public AnimatedTextView.AnimatedTextDrawable textDrawable;
        public int top;
        ArrayList<TLObject> users;
        VisibleReaction visibleReaction;
        public boolean wasDrawn;
        public int width;
        public int x;
        public int y;
        public boolean drawImage = true;
        Rect drawingImageRect = new Rect();
        private final RectF bounds = new RectF();
        private final RectF rect2 = new RectF();
        private final Path tagPath = new Path();

        public boolean drawTagDot() {
            return true;
        }

        public boolean drawTextWithCounter() {
            return false;
        }

        public float getDrawServiceShaderBackground() {
            return 0.0f;
        }

        public ImageReceiver getImageReceiver() {
            return null;
        }

        public boolean isOutOwner() {
            return false;
        }

        public boolean isPlaying() {
            return false;
        }

        public void removeImageReceiver() {
        }

        public int getCacheType() {
            return this.isTag ? 18 : 3;
        }

        public ReactionButton(ReactionButton reactionButton, int i, View view, TLRPC.ReactionCount reactionCount, boolean z, boolean z2, Theme.ResourcesProvider resourcesProvider) {
            StarsReactionsSheet.Particles particles;
            RLottieDrawable rLottieDrawable;
            Drawable.Callback callback = new Drawable.Callback() { // from class: org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble.ReactionButton.1
                @Override // android.graphics.drawable.Drawable.Callback
                public void invalidateDrawable(Drawable drawable) {
                    if (ReactionButton.this.parentView != null) {
                        ReactionButton.this.parentView.invalidate();
                        ReactionButton reactionButton2 = ReactionButton.this;
                        if (reactionButton2.inGroup && reactionButton2.parentView.getParent() != null && (ReactionButton.this.parentView.getParent().getParent() instanceof View)) {
                            ((View) ReactionButton.this.parentView.getParent().getParent()).invalidate();
                        }
                    }
                }

                @Override // android.graphics.drawable.Drawable.Callback
                public void scheduleDrawable(Drawable drawable, Runnable runnable, long j) {
                    if (ReactionButton.this.parentView != null) {
                        ReactionButton.this.parentView.scheduleDrawable(drawable, runnable, j);
                    }
                }

                @Override // android.graphics.drawable.Drawable.Callback
                public void unscheduleDrawable(Drawable drawable, Runnable runnable) {
                    if (ReactionButton.this.parentView != null) {
                        ReactionButton.this.parentView.unscheduleDrawable(drawable, runnable);
                    }
                }
            };
            this.supercallback = callback;
            this.currentAccount = i;
            this.parentView = view;
            this.bounce = new ButtonBounce(view);
            this.resourcesProvider = resourcesProvider;
            this.isTag = z2;
            if (reactionButton != null) {
                this.counterDrawable = reactionButton.counterDrawable;
            }
            if (this.imageReceiver == null) {
                this.imageReceiver = new ImageReceiver();
            }
            if (this.counterDrawable == null) {
                this.counterDrawable = new CounterView.CounterDrawable(view, false, null);
            }
            if (this.textDrawable == null) {
                AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(true, true, true);
                this.textDrawable = animatedTextDrawable;
                animatedTextDrawable.ignoreRTL = true;
                animatedTextDrawable.setAnimationProperties(0.4f, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
                this.textDrawable.setTextSize(AndroidUtilities.dp(13.0f));
                this.textDrawable.setCallback(callback);
                this.textDrawable.setTypeface(AndroidUtilities.bold());
                this.textDrawable.setOverrideFullWidth(AndroidUtilities.displaySize.x);
            }
            if (this.scrimPreviewCounterDrawable == null) {
                AnimatedTextView.AnimatedTextDrawable animatedTextDrawable2 = new AnimatedTextView.AnimatedTextDrawable(false, false, false, true);
                this.scrimPreviewCounterDrawable = animatedTextDrawable2;
                animatedTextDrawable2.setTextSize(AndroidUtilities.dp(12.0f));
                this.scrimPreviewCounterDrawable.setCallback(callback);
                this.scrimPreviewCounterDrawable.setTypeface(AndroidUtilities.bold());
                this.scrimPreviewCounterDrawable.setOverrideFullWidth(AndroidUtilities.displaySize.x);
                this.scrimPreviewCounterDrawable.setScaleProperty(0.35f);
            }
            this.reactionCount = reactionCount;
            TLRPC.Reaction reaction = reactionCount.reaction;
            this.reaction = reaction;
            this.visibleReaction = VisibleReaction.fromTL(reaction);
            int i2 = reactionCount.count;
            this.count = i2;
            this.choosen = reactionCount.chosen;
            this.realCount = i2;
            this.choosenOrder = reactionCount.chosen_order;
            this.isSmall = z;
            TLRPC.Reaction reaction2 = this.reaction;
            if (reaction2 instanceof TLRPC.TL_reactionPaid) {
                this.key = "stars";
            } else if (reaction2 instanceof TLRPC.TL_reactionEmoji) {
                this.key = ((TLRPC.TL_reactionEmoji) reaction2).emoticon;
            } else if (reaction2 instanceof TLRPC.TL_reactionCustomEmoji) {
                this.key = Long.toString(((TLRPC.TL_reactionCustomEmoji) reaction2).document_id);
            } else {
                GlShader$$ExternalSyntheticBUOutline1.m("unsupported");
                throw null;
            }
            this.imageReceiver.setParentView(view);
            this.isSelected = reactionCount.chosen;
            CounterView.CounterDrawable counterDrawable = this.counterDrawable;
            counterDrawable.updateVisibility = false;
            counterDrawable.shortFormat = true;
            if (this.reaction != null) {
                VisibleReaction visibleReaction = this.visibleReaction;
                if (visibleReaction.isStar) {
                    this.paid = true;
                    if (LiteMode.isEnabled(LiteMode.FLAG_ANIMATED_EMOJI_REACTIONS)) {
                        if (reactionButton != null && (rLottieDrawable = reactionButton.starDrawable) != null) {
                            this.starDrawable = rLottieDrawable;
                        } else {
                            this.starDrawable = new RLottieDrawable(R.raw.star_reaction_click, "star_reaction_click", AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
                        }
                        this.imageReceiver.setImageBitmap(this.starDrawable);
                    } else {
                        this.imageReceiver.setImageBitmap(ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.star_reaction).mutate());
                    }
                    if (reactionButton == null || (particles = reactionButton.particles) == null) {
                        particles = new StarsReactionsSheet.Particles(1, SharedConfig.getDevicePerformanceClass() == 2 ? 18 : 8);
                    }
                    this.particles = particles;
                } else if (visibleReaction.emojicon != null) {
                    TLRPC.TL_availableReaction tL_availableReaction = MediaDataController.getInstance(i).getReactionsMap().get(this.visibleReaction.emojicon);
                    if (tL_availableReaction != null) {
                        this.imageReceiver.setImage(ImageLocation.getForDocument(tL_availableReaction.center_icon), "40_40_lastreactframe", DocumentObject.getSvgThumb(tL_availableReaction.static_icon, Theme.key_windowBackgroundGray, 1.0f), "webp", tL_availableReaction, 1);
                    }
                } else if (visibleReaction.documentId != 0) {
                    this.animatedEmojiDrawable = new AnimatedEmojiDrawable(getCacheType(), i, this.visibleReaction.documentId);
                }
            }
            this.counterDrawable.setSize(AndroidUtilities.dp(26.0f), AndroidUtilities.dp(100.0f));
            this.counterDrawable.textPaint = ReactionsLayoutInBubble.textPaint;
            if (z2) {
                String savedTagName = MessagesController.getInstance(i).getSavedTagName(this.reaction);
                this.name = savedTagName;
                this.hasName = !TextUtils.isEmpty(savedTagName);
            }
            boolean z3 = this.hasName;
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable3 = this.textDrawable;
            if (z3) {
                animatedTextDrawable3.setText(Emoji.replaceEmoji(this.name, animatedTextDrawable3.getPaint().getFontMetricsInt(), false), !LocaleController.isRTL);
                if (drawTextWithCounter()) {
                    this.countText = Integer.toString(reactionCount.count);
                    this.counterDrawable.setCount(this.count, false);
                } else {
                    this.countText = _UrlKt.FRAGMENT_ENCODE_SET;
                    this.counterDrawable.setCount(0, false);
                }
            } else {
                if (animatedTextDrawable3 != null) {
                    animatedTextDrawable3.setText(_UrlKt.FRAGMENT_ENCODE_SET, false);
                }
                this.countText = Integer.toString(reactionCount.count);
                this.counterDrawable.setCount(this.count, false);
            }
            this.counterDrawable.setType(2);
            this.counterDrawable.gravity = 3;
        }

        private void drawRoundRect(Canvas canvas, RectF rectF, float f, Paint paint) {
            if (this.isTag) {
                RectF rectF2 = this.bounds;
                if (rectF2.left != rectF.left || rectF2.top != rectF.top || rectF2.right != rectF.right || rectF2.bottom != rectF.bottom) {
                    rectF2.set(rectF);
                    ReactionsLayoutInBubble.fillTagPath(this.bounds, this.rect2, this.tagPath);
                }
                canvas.drawPath(this.tagPath, paint);
                return;
            }
            canvas.drawRoundRect(rectF, f, f, paint);
        }

        public boolean drawCounter() {
            int i = this.count;
            return ((i == 0 || (this.isTag && !this.hasName && i == 1)) && this.counterDrawable.countChangeProgress == 1.0f) ? false : true;
        }

        public boolean drawOverlay(Canvas canvas, float f, float f2, float f3, float f4, boolean z) {
            if (this.particles == null || !LiteMode.isEnabled(LiteMode.FLAG_ANIMATED_EMOJI_REACTIONS) || !LiteMode.isEnabled(131072)) {
                return false;
            }
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(f, f2, this.width + f, this.height + f2);
            float f5 = this.height / 2.0f;
            this.particles.bounds.set(rectF);
            this.particles.bounds.inset(-AndroidUtilities.dp(4.0f), -AndroidUtilities.dp(4.0f));
            StarsReactionsSheet.Particles particles = this.particles;
            particles.setBounds(particles.bounds);
            boolean zProcess = this.particles.process();
            this.particles.draw(canvas, ColorUtils.blendARGB(ColorUtils.setAlphaComponent(this.backgroundColor, 255), ColorUtils.blendARGB(this.serviceTextColor, ColorUtils.setAlphaComponent(this.backgroundColor, 255), 0.4f), getDrawServiceShaderBackground()));
            if (this.isSelected) {
                this.tagPath.rewind();
                this.tagPath.addRoundRect(rectF, f5, f5, Path.Direction.CW);
                canvas.save();
                canvas.clipPath(this.tagPath);
                this.particles.draw(canvas, this.textColor);
                canvas.restore();
            }
            return zProcess;
        }

        void lambda$checkTouchEvent$1(ReactionButton reactionButton) {
        didPressReaction(reactionButton.reactionCount, true, 0.0f, 0.0f);
        reactionButton.bounce.setPressed(false);
        this.lastSelectedButton = null;
        this.pressed = false;
        this.longPressRunnable = null;
    }

    public float getCurrentWidth(float f) {
        if (this.animateWidth) {
            return (this.fromWidth * (1.0f - f)) + (this.width * f);
        }
        return this.width;
    }

    public float getCurrentTotalHeight(float f) {
        if (this.animateHeight) {
            return (this.animateFromTotalHeight * (1.0f - f)) + (this.totalHeight * f);
        }
        return this.totalHeight;
    }

    public static class ButtonsComparator implements Comparator<ReactionButton> {
        int currentAccount;
        long dialogId;

        private ButtonsComparator() {
        }

        @Override // java.util.Comparator
        public int compare(ReactionButton reactionButton, ReactionButton reactionButton2) {
            int i;
            int i2;
            int i3;
            int i4;
            if (this.dialogId >= 0) {
                boolean z = reactionButton.paid;
                if (z != reactionButton2.paid) {
                    return z ? -1 : 1;
                }
                boolean z2 = reactionButton.isSelected;
                if (z2 != reactionButton2.isSelected) {
                    return z2 ? -1 : 1;
                }
                if (z2 && (i3 = reactionButton.choosenOrder) != (i4 = reactionButton2.choosenOrder)) {
                    return i3 - i4;
                }
                i = reactionButton.reactionCount.lastDrawnPosition;
                i2 = reactionButton2.reactionCount.lastDrawnPosition;
            } else {
                boolean z3 = reactionButton.paid;
                if (z3 != reactionButton2.paid) {
                    return z3 ? -1 : 1;
                }
                int i5 = reactionButton.realCount;
                int i6 = reactionButton2.realCount;
                if (i5 != i6) {
                    return i6 - i5;
                }
                i = reactionButton.reactionCount.lastDrawnPosition;
                i2 = reactionButton2.reactionCount.lastDrawnPosition;
            }
            return i - i2;
        }
    }

    public void onAttachToWindow() {
        this.attached = true;
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.reactionButtons.get(i).attach();
        }
    }

    public void onDetachFromWindow() {
        this.attached = false;
        for (int i = 0; i < this.reactionButtons.size(); i++) {
            this.reactionButtons.get(i).detach();
        }
        if (!this.animatedReactions.isEmpty()) {
            Iterator<ImageReceiver> it = this.animatedReactions.values().iterator();
            while (it.hasNext()) {
                it.next().onDetachedFromWindow();
            }
        }
        this.animatedReactions.clear();
    }

    public void animateReaction(VisibleReaction visibleReaction) {
        if (visibleReaction.documentId == 0 && this.animatedReactions.get(visibleReaction) == null) {
            ImageReceiver imageReceiver = new ImageReceiver();
            imageReceiver.setParentView(this.parentView);
            int i = animationUniq;
            animationUniq = i + 1;
            imageReceiver.setUniqKeyPrefix(Integer.toString(i));
            TLRPC.TL_availableReaction tL_availableReaction = MediaDataController.getInstance(this.currentAccount).getReactionsMap().get(visibleReaction.emojicon);
            if (tL_availableReaction != null) {
                imageReceiver.setImage(ImageLocation.getForDocument(tL_availableReaction.center_icon), "40_40_nolimit", null, "tgs", tL_availableReaction, 1);
            }
            imageReceiver.setAutoRepeat(0);
            imageReceiver.onAttachedToWindow();
            this.animatedReactions.put(visibleReaction, imageReceiver);
            return;
        }
        if (!this.tags || visibleReaction.documentId == 0) {
            return;
        }
        for (int i2 = 0; i2 < this.reactionButtons.size(); i2++) {
            if (visibleReaction.isSame(this.reactionButtons.get(i2).reaction)) {
                this.reactionButtons.get(i2).startAnimation();
                return;
            }
        }
    }

    public static class VisibleReaction {
        public long documentId;
        public long effectId;
        public String emojicon;
        public long hash;
        public boolean isEffect;
        public boolean isStar;
        public boolean premium;
        public boolean sticker;

        public static VisibleReaction asStar() {
            VisibleReaction visibleReaction = new VisibleReaction();
            visibleReaction.isStar = true;
            return visibleReaction;
        }

        public static VisibleReaction fromTL(TLRPC.Reaction reaction) {
            VisibleReaction visibleReaction = new VisibleReaction();
            if (reaction instanceof TLRPC.TL_reactionPaid) {
                visibleReaction.isStar = true;
                return visibleReaction;
            }
            if (reaction instanceof TLRPC.TL_reactionEmoji) {
                String str = ((TLRPC.TL_reactionEmoji) reaction).emoticon;
                visibleReaction.emojicon = str;
                visibleReaction.hash = str.hashCode();
                return visibleReaction;
            }
            if (reaction instanceof TLRPC.TL_reactionCustomEmoji) {
                long j = ((TLRPC.TL_reactionCustomEmoji) reaction).document_id;
                visibleReaction.documentId = j;
                visibleReaction.hash = j;
            }
            return visibleReaction;
        }

        public static VisibleReaction fromTL(TLRPC.TL_availableEffect tL_availableEffect) {
            VisibleReaction visibleReaction = new VisibleReaction();
            visibleReaction.isEffect = true;
            long j = tL_availableEffect.id;
            visibleReaction.effectId = j;
            visibleReaction.sticker = tL_availableEffect.effect_animation_id == 0;
            visibleReaction.documentId = tL_availableEffect.effect_sticker_id;
            visibleReaction.hash = j;
            visibleReaction.premium = tL_availableEffect.premium_required;
            visibleReaction.emojicon = tL_availableEffect.emoticon;
            return visibleReaction;
        }

        public TLRPC.Reaction toTLReaction() {
            if (this.isStar) {
                return new TLRPC.TL_reactionPaid();
            }
            if (this.emojicon != null) {
                TLRPC.TL_reactionEmoji tL_reactionEmoji = new TLRPC.TL_reactionEmoji();
                tL_reactionEmoji.emoticon = this.emojicon;
                return tL_reactionEmoji;
            }
            TLRPC.TL_reactionCustomEmoji tL_reactionCustomEmoji = new TLRPC.TL_reactionCustomEmoji();
            tL_reactionCustomEmoji.document_id = this.documentId;
            return tL_reactionCustomEmoji;
        }

        public static VisibleReaction fromEmojicon(TLRPC.TL_availableReaction tL_availableReaction) {
            VisibleReaction visibleReaction = new VisibleReaction();
            String str = tL_availableReaction.reaction;
            visibleReaction.emojicon = str;
            visibleReaction.hash = str.hashCode();
            return visibleReaction;
        }

        public static VisibleReaction fromEmojicon(String str) {
            if (str == null) {
                str = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            VisibleReaction visibleReaction = new VisibleReaction();
            if (str.startsWith("animated_")) {
                try {
                    long j = Long.parseLong(str.substring(9));
                    visibleReaction.documentId = j;
                    visibleReaction.hash = j;
                    return visibleReaction;
                } catch (Exception unused) {
                    visibleReaction.emojicon = str;
                    visibleReaction.hash = str.hashCode();
                    return visibleReaction;
                }
            }
            visibleReaction.emojicon = str;
            visibleReaction.hash = str.hashCode();
            return visibleReaction;
        }

        public static VisibleReaction fromCustomEmoji(Long l) {
            VisibleReaction visibleReaction = new VisibleReaction();
            long jLongValue = l.longValue();
            visibleReaction.documentId = jLongValue;
            visibleReaction.hash = jLongValue;
            return visibleReaction;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj != null && getClass() == obj.getClass()) {
                VisibleReaction visibleReaction = (VisibleReaction) obj;
                if (this.documentId == visibleReaction.documentId && Objects.equals(this.emojicon, visibleReaction.emojicon)) {
                    return true;
                }
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.emojicon, Long.valueOf(this.documentId));
        }

        public boolean isSame(TLRPC.Reaction reaction) {
            if (reaction instanceof TLRPC.TL_reactionEmoji) {
                return TextUtils.equals(((TLRPC.TL_reactionEmoji) reaction).emoticon, this.emojicon);
            }
            return (reaction instanceof TLRPC.TL_reactionCustomEmoji) && ((TLRPC.TL_reactionCustomEmoji) reaction).document_id == this.documentId;
        }

        public VisibleReaction flatten() {
            String strFindAnimatedEmojiEmoticon;
            long j = this.documentId;
            return (j == 0 || (strFindAnimatedEmojiEmoticon = MessageObject.findAnimatedEmojiEmoticon(AnimatedEmojiDrawable.findDocument(UserConfig.selectedAccount, j), null)) == null) ? this : fromEmojicon(strFindAnimatedEmojiEmoticon);
        }

        public String toString() {
            TLRPC.Document documentFindDocument;
            if (!TextUtils.isEmpty(this.emojicon)) {
                return this.emojicon;
            }
            long j = this.documentId;
            if (j != 0 && (documentFindDocument = AnimatedEmojiDrawable.findDocument(UserConfig.selectedAccount, j)) != null) {
                return MessageObject.findAnimatedEmojiEmoticon(documentFindDocument, null);
            }
            return "VisibleReaction{" + this.documentId + ", " + this.emojicon + "}";
        }

        public CharSequence toCharSequence(Paint.FontMetricsInt fontMetricsInt) {
            if (!TextUtils.isEmpty(this.emojicon)) {
                return this.emojicon;
            }
            SpannableString spannableString = new SpannableString("😀");
            spannableString.setSpan(new AnimatedEmojiSpan(this.documentId, fontMetricsInt), 0, spannableString.length(), 17);
            return spannableString;
        }

        public CharSequence toCharSequence(int i) {
            TextPaint textPaint = new TextPaint();
            textPaint.setTextSize(AndroidUtilities.dp(i));
            if (!TextUtils.isEmpty(this.emojicon)) {
                return Emoji.replaceEmoji(this.emojicon, textPaint.getFontMetricsInt(), false);
            }
            SpannableString spannableString = new SpannableString("😀");
            spannableString.setSpan(new AnimatedEmojiSpan(this.documentId, textPaint.getFontMetricsInt()), 0, spannableString.length(), 17);
            return spannableString;
        }
    }

    public static boolean reactionsEqual(TLRPC.Reaction reaction, TLRPC.Reaction reaction2) {
        if (!(reaction instanceof TLRPC.TL_reactionEmoji)) {
            return (reaction instanceof TLRPC.TL_reactionCustomEmoji) && (reaction2 instanceof TLRPC.TL_reactionCustomEmoji) && ((TLRPC.TL_reactionCustomEmoji) reaction).document_id == ((TLRPC.TL_reactionCustomEmoji) reaction2).document_id;
        }
        if (reaction2 instanceof TLRPC.TL_reactionEmoji) {
            return TextUtils.equals(((TLRPC.TL_reactionEmoji) reaction).emoticon, ((TLRPC.TL_reactionEmoji) reaction2).emoticon);
        }
        return false;
    }

    public static void fillTagPath(RectF rectF, Path path) {
        fillTagPath(rectF, AndroidUtilities.rectTmp, path);
    }

    public static void fillTagPath(RectF rectF, RectF rectF2, Path path) {
        path.rewind();
        float f = rectF.left;
        rectF2.set(f, rectF.top, AndroidUtilities.dp(12.0f) + f, rectF.top + AndroidUtilities.dp(12.0f));
        path.arcTo(rectF2, -90.0f, -90.0f, false);
        rectF2.set(rectF.left, rectF.bottom - AndroidUtilities.dp(12.0f), rectF.left + AndroidUtilities.dp(12.0f), rectF.bottom);
        path.arcTo(rectF2, -180.0f, -90.0f, false);
        float f2 = rectF.height() > ((float) AndroidUtilities.dp(26.0f)) ? 1.4f : 0.0f;
        float fDpf2 = rectF.right - AndroidUtilities.dpf2(9.09f);
        float fDpf3 = fDpf2 - AndroidUtilities.dpf2(0.056f);
        float fDpf4 = AndroidUtilities.dpf2(1.22f) + fDpf2;
        float fDpf5 = fDpf2 + AndroidUtilities.dpf2(3.07f);
        float fDpf6 = AndroidUtilities.dpf2(2.406f) + fDpf2;
        float fDpf7 = fDpf2 + AndroidUtilities.dpf2(8.27f + f2);
        float fDpf8 = fDpf2 + AndroidUtilities.dpf2(8.923f + f2);
        float fDpf9 = AndroidUtilities.dpf2(1.753f) + rectF.top;
        float fDpf10 = rectF.bottom - AndroidUtilities.dpf2(1.753f);
        float fDpf11 = AndroidUtilities.dpf2(0.663f) + rectF.top;
        float fDpf12 = rectF.bottom - AndroidUtilities.dpf2(0.663f);
        float f3 = 10.263f + f2;
        float fDpf13 = rectF.top + AndroidUtilities.dpf2(f3);
        float fDpf14 = rectF.bottom - AndroidUtilities.dpf2(f3);
        float f4 = f2 + 11.333f;
        float fDpf15 = rectF.top + AndroidUtilities.dpf2(f4);
        float fDpf16 = rectF.bottom - AndroidUtilities.dpf2(f4);
        path.lineTo(fDpf3, rectF.bottom);
        path.cubicTo(fDpf4, rectF.bottom, fDpf6, fDpf12, fDpf5, fDpf10);
        path.lineTo(fDpf7, fDpf14);
        path.cubicTo(fDpf8, fDpf16, fDpf8, fDpf15, fDpf7, fDpf13);
        path.lineTo(fDpf5, fDpf9);
        float f5 = rectF.top;
        path.cubicTo(fDpf6, fDpf11, fDpf4, f5, fDpf3, f5);
        path.close();
    }
}
