package org.telegram.ui.Cells;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import androidx.mediarouter.media.GlobalMediaRouter;
import com.exteragram.messenger.ExteraConfig;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatBackgroundDrawable;
import org.telegram.ui.Components.AnimatedColor;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackgroundGradientDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.Reactions.ReactionsEffectOverlay;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Stories.recorder.StoryEntry;

public class ThemePreviewMessagesCell extends LinearLayout {
    private Drawable backgroundDrawable;
    private BackgroundGradientDrawable.Disposable backgroundGradientDisposable;
    private final Runnable cancelProgress;
    private ChatMessageCell[] cells;
    public boolean customAnimation;
    public BaseFragment fragment;
    private final Runnable invalidateRunnable;
    private Drawable oldBackgroundDrawable;
    private BackgroundGradientDrawable.Disposable oldBackgroundGradientDisposable;
    private Drawable overrideDrawable;
    private final AnimatedFloat overrideDrawableUpdate;
    private INavigationLayout parentLayout;
    private int progress;
    private Drawable shadowDrawable;
    private final int type;

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchSetPressed(boolean z) {
    }

    public Context val$context;
                    final /* synthetic */ int val$type;

                    public class C00401 extends GestureDetector.SimpleOnGestureListener {
                        public C00401() {
                        }

                        @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
                        public boolean onDoubleTap(MotionEvent motionEvent) {
                            AnonymousClass1 anonymousClass1 = AnonymousClass1.this;
                            if (anonymousClass1.val$type != 2 || MediaDataController.getInstance(anonymousClass1.currentAccount).getDoubleTapReaction() == null) {
                                return false;
                            }
                            boolean zSelectReaction = getMessageObject().selectReaction(ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(MediaDataController.getInstance(AnonymousClass1.this.currentAccount).getDoubleTapReaction()), false, false);
                            AnonymousClass1 anonymousClass2 = AnonymousClass1.this;
                            anonymousClass2.setMessageObject(anonymousClass2.getMessageObject(), null, false, false, false);
                            requestLayout();
                            ReactionsEffectOverlay.removeCurrent(false);
                            if (zSelectReaction) {
                                ThemePreviewMessagesCell themePreviewMessagesCell = ThemePreviewMessagesCell.this;
                                ReactionsEffectOverlay.show(themePreviewMessagesCell.fragment, null, themePreviewMessagesCell.cells[1], null, motionEvent.getX(), motionEvent.getY(), ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(MediaDataController.getInstance(AnonymousClass1.this.currentAccount).getDoubleTapReaction()), AnonymousClass1.this.currentAccount, 0);
                                ReactionsEffectOverlay.startAnimation();
                            }
                            getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserverOnPreDrawListenerC00411());
                            return true;
                        }

                        public class ViewTreeObserverOnPreDrawListenerC00411 implements ViewTreeObserver.OnPreDrawListener {
                            public ViewTreeObserverOnPreDrawListenerC00411() {
                            }

                            @Override // android.view.ViewTreeObserver.OnPreDrawListener
                            public boolean onPreDraw() {
                                getViewTreeObserver().removeOnPreDrawListener(this);
                                getTransitionParams().resetAnimation();
                                getTransitionParams().animateChange();
                                getTransitionParams().animateChange = true;
                                getTransitionParams().animateChangeProgress = 0.0f;
                                ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                                valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.ThemePreviewMessagesCell$1$1$1$$ExternalSyntheticLambda0
                                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                        this.f$0.lambda$onPreDraw$0(valueAnimator);
                                    }
                                });
                                valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.ThemePreviewMessagesCell.1.1.1.1
                                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                    public void onAnimationEnd(Animator animator) {
                                        super.onAnimationEnd(animator);
                                        getTransitionParams().resetAnimation();
                                        getTransitionParams().animateChange = false;
                                        getTransitionParams().animateChangeProgress = 1.0f;
                                    }
                                });
                                valueAnimatorOfFloat.start();
                                return false;
                            }

                            public /* synthetic */ void lambda$onPreDraw$0(ValueAnimator valueAnimator) {
                                getTransitionParams().animateChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                                invalidate();
                            }
                        }
                    }

                    {
                        this.val$context = context;
                        this.val$type = i;
                        this.gestureDetector = new GestureDetector(context, new C00401());
                        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT;
                        this.color1 = new AnimatedColor(this, 0L, 180L, cubicBezierInterpolator);
                        this.color2 = new AnimatedColor(this, 0L, 180L, cubicBezierInterpolator);
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell, android.view.View
                    public boolean onTouchEvent(MotionEvent motionEvent) {
                        if (ThemePreviewMessagesCell.this.allowLoadingOnTouch()) {
                            return super.onTouchEvent(motionEvent);
                        }
                        this.gestureDetector.onTouchEvent(motionEvent);
                        return true;
                    }

                    @Override // android.view.ViewGroup, android.view.View
                    public void dispatchDraw(Canvas canvas) {
                        int themedColor;
                        int themedColor2;
                        if (getMessageObject() != null && getMessageObject().overrideLinkColor >= 0) {
                            int i6 = getMessageObject().overrideLinkColor;
                            if (i6 >= 14) {
                                MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
                                MessagesController.PeerColors peerColors = messagesController != null ? messagesController.peerColors : null;
                                MessagesController.PeerColor color = peerColors != null ? peerColors.getColor(i6) : null;
                                if (color != null) {
                                    int color1 = color.getColor1();
                                    themedColor = getThemedColor(Theme.keys_avatar_background[AvatarDrawable.getPeerColorIndex(color1)]);
                                    themedColor2 = getThemedColor(Theme.keys_avatar_background2[AvatarDrawable.getPeerColorIndex(color1)]);
                                } else {
                                    long j3 = i6;
                                    themedColor = getThemedColor(Theme.keys_avatar_background[AvatarDrawable.getColorIndex(j3)]);
                                    themedColor2 = getThemedColor(Theme.keys_avatar_background2[AvatarDrawable.getColorIndex(j3)]);
                                }
                            } else {
                                long j4 = i6;
                                themedColor = getThemedColor(Theme.keys_avatar_background[AvatarDrawable.getColorIndex(j4)]);
                                themedColor2 = getThemedColor(Theme.keys_avatar_background2[AvatarDrawable.getColorIndex(j4)]);
                            }
                            this.avatarDrawable.setColor(this.color1.set(themedColor), this.color2.set(themedColor2));
                        } else {
                            this.color1.set(this.avatarDrawable.getColor());
                            this.color2.set(this.avatarDrawable.getColor2());
                        }
                        if (getAvatarImage() != null && getAvatarImage().getImageHeight() != 0.0f) {
                            getAvatarImage().setImageCoords(getAvatarImage().getImageX(), (getMeasuredHeight() - getAvatarImage().getImageHeight()) - AndroidUtilities.dp(4.0f), getAvatarImage().getImageWidth(), getAvatarImage().getImageHeight());
                            getAvatarImage().setRoundRadius(ExteraConfig.getAvatarCorners(getAvatarImage().getImageHeight(), true));
                            getAvatarImage().draw(canvas);
                        } else if (this.val$type == 2) {
                            invalidate();
                        }
                        super.dispatchDraw(canvas);
                    }
                };
                this.cells[i3].setDelegate(new ChatMessageCell.ChatMessageCellDelegate() { // from class: org.telegram.ui.Cells.ThemePreviewMessagesCell.2
                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public boolean canPerformActions() {
                        return ThemePreviewMessagesCell.this.allowLoadingOnTouch();
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public void didPressReplyMessage(ChatMessageCell chatMessageCell, int i6, float f, float f2, boolean z3) {
                        if (ThemePreviewMessagesCell.this.allowLoadingOnTouch()) {
                            ThemePreviewMessagesCell.this.progress = 0;
                            chatMessageCell.invalidate();
                            AndroidUtilities.cancelRunOnUIThread(ThemePreviewMessagesCell.this.cancelProgress);
                            AndroidUtilities.runOnUIThread(ThemePreviewMessagesCell.this.cancelProgress, 5000L);
                        }
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public void needOpenWebView(MessageObject messageObject9, String str, String str2, String str3, String str4, int i6, int i7) {
                        if (ThemePreviewMessagesCell.this.allowLoadingOnTouch()) {
                            ThemePreviewMessagesCell.this.progress = 2;
                            AndroidUtilities.cancelRunOnUIThread(ThemePreviewMessagesCell.this.cancelProgress);
                            AndroidUtilities.runOnUIThread(ThemePreviewMessagesCell.this.cancelProgress, 5000L);
                        }
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public void didPressInstantButton(ChatMessageCell chatMessageCell, int i6) {
                        if (ThemePreviewMessagesCell.this.allowLoadingOnTouch()) {
                            ThemePreviewMessagesCell.this.progress = 2;
                            chatMessageCell.invalidate();
                            AndroidUtilities.cancelRunOnUIThread(ThemePreviewMessagesCell.this.cancelProgress);
                            AndroidUtilities.runOnUIThread(ThemePreviewMessagesCell.this.cancelProgress, 5000L);
                        }
                    }

                    @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                    public boolean isProgressLoading(ChatMessageCell chatMessageCell, int i6) {
                        return i6 == ThemePreviewMessagesCell.this.progress;
                    }
                });
                ChatMessageCell chatMessageCell = this.cells[i3];
                if (i != 2 || i == 4) {
                    z = 1;
                } else {
                    z = i2;
                }
                chatMessageCell.isChat = z;
                chatMessageCell.setFullyDraw(true);
                if (i3 == 0) {
                    messageObject3 = messageObject2;
                } else {
                    messageObject3 = messageObject;
                }
                if (messageObject3 == null) {
                    this.cells[i3].setMessageObject(messageObject3, null, false, false, false);
                    addView(this.cells[i3], LayoutHelper.createLinear(-1, -2));
                }
                i3++;
                resourcesProvider2 = resourcesProvider;
            }
        }
        messageObject2 = null;
        i2 = 0;
        i3 = i2;
        while (true) {
            chatMessageCellArr = this.cells;
            if (i3 < chatMessageCellArr.length) {
                return;
            }
            chatMessageCellArr[i3] = new ChatMessageCell(context, i4, false, null, resourcesProvider2, context, i) { // from class: org.telegram.ui.Cells.ThemePreviewMessagesCell.1
                private final AnimatedColor color1;
                private final AnimatedColor color2;
                private GestureDetector gestureDetector;
                final /* synthetic */ Context val$context;
                final /* synthetic */ int val$type;

                public class C00401 extends GestureDetector.SimpleOnGestureListener {
                    public C00401() {
                    }

                    @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnDoubleTapListener
                    public boolean onDoubleTap(MotionEvent motionEvent) {
                        AnonymousClass1 anonymousClass1 = AnonymousClass1.this;
                        if (anonymousClass1.val$type != 2 || MediaDataController.getInstance(anonymousClass1.currentAccount).getDoubleTapReaction() == null) {
                            return false;
                        }
                        boolean zSelectReaction = getMessageObject().selectReaction(ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(MediaDataController.getInstance(AnonymousClass1.this.currentAccount).getDoubleTapReaction()), false, false);
                        AnonymousClass1 anonymousClass2 = AnonymousClass1.this;
                        anonymousClass2.setMessageObject(anonymousClass2.getMessageObject(), null, false, false, false);
                        requestLayout();
                        ReactionsEffectOverlay.removeCurrent(false);
                        if (zSelectReaction) {
                            ThemePreviewMessagesCell themePreviewMessagesCell = ThemePreviewMessagesCell.this;
                            ReactionsEffectOverlay.show(themePreviewMessagesCell.fragment, null, themePreviewMessagesCell.cells[1], null, motionEvent.getX(), motionEvent.getY(), ReactionsLayoutInBubble.VisibleReaction.fromEmojicon(MediaDataController.getInstance(AnonymousClass1.this.currentAccount).getDoubleTapReaction()), AnonymousClass1.this.currentAccount, 0);
                            ReactionsEffectOverlay.startAnimation();
                        }
                        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserverOnPreDrawListenerC00411());
                        return true;
                    }

                    public class ViewTreeObserverOnPreDrawListenerC00411 implements ViewTreeObserver.OnPreDrawListener {
                        public ViewTreeObserverOnPreDrawListenerC00411() {
                        }

                        @Override // android.view.ViewTreeObserver.OnPreDrawListener
                        public boolean onPreDraw() {
                            getViewTreeObserver().removeOnPreDrawListener(this);
                            getTransitionParams().resetAnimation();
                            getTransitionParams().animateChange();
                            getTransitionParams().animateChange = true;
                            getTransitionParams().animateChangeProgress = 0.0f;
                            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Cells.ThemePreviewMessagesCell$1$1$1$$ExternalSyntheticLambda0
                                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    this.f$0.lambda$onPreDraw$0(valueAnimator);
                                }
                            });
                            valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Cells.ThemePreviewMessagesCell.1.1.1.1
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animator) {
                                    super.onAnimationEnd(animator);
                                    getTransitionParams().resetAnimation();
                                    getTransitionParams().animateChange = false;
                                    getTransitionParams().animateChangeProgress = 1.0f;
                                }
                            });
                            valueAnimatorOfFloat.start();
                            return false;
                        }

                        public /* synthetic */ void lambda$onPreDraw$0(ValueAnimator valueAnimator) {
                            getTransitionParams().animateChangeProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
                            invalidate();
                        }
                    }
                }

                {
                    this.val$context = context;
                    this.val$type = i;
                    this.gestureDetector = new GestureDetector(context, new C00401());
                    CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT;
                    this.color1 = new AnimatedColor(this, 0L, 180L, cubicBezierInterpolator);
                    this.color2 = new AnimatedColor(this, 0L, 180L, cubicBezierInterpolator);
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell, android.view.View
                public boolean onTouchEvent(MotionEvent motionEvent) {
                    if (ThemePreviewMessagesCell.this.allowLoadingOnTouch()) {
                        return super.onTouchEvent(motionEvent);
                    }
                    this.gestureDetector.onTouchEvent(motionEvent);
                    return true;
                }

                @Override // android.view.ViewGroup, android.view.View
                public void dispatchDraw(Canvas canvas) {
                    int themedColor;
                    int themedColor2;
                    if (getMessageObject() != null && getMessageObject().overrideLinkColor >= 0) {
                        int i6 = getMessageObject().overrideLinkColor;
                        if (i6 >= 14) {
                            MessagesController messagesController = MessagesController.getInstance(UserConfig.selectedAccount);
                            MessagesController.PeerColors peerColors = messagesController != null ? messagesController.peerColors : null;
                            MessagesController.PeerColor color = peerColors != null ? peerColors.getColor(i6) : null;
                            if (color != null) {
                                int color1 = color.getColor1();
                                themedColor = getThemedColor(Theme.keys_avatar_background[AvatarDrawable.getPeerColorIndex(color1)]);
                                themedColor2 = getThemedColor(Theme.keys_avatar_background2[AvatarDrawable.getPeerColorIndex(color1)]);
                            } else {
                                long j3 = i6;
                                themedColor = getThemedColor(Theme.keys_avatar_background[AvatarDrawable.getColorIndex(j3)]);
                                themedColor2 = getThemedColor(Theme.keys_avatar_background2[AvatarDrawable.getColorIndex(j3)]);
                            }
                        } else {
                            long j4 = i6;
                            themedColor = getThemedColor(Theme.keys_avatar_background[AvatarDrawable.getColorIndex(j4)]);
                            themedColor2 = getThemedColor(Theme.keys_avatar_background2[AvatarDrawable.getColorIndex(j4)]);
                        }
                        this.avatarDrawable.setColor(this.color1.set(themedColor), this.color2.set(themedColor2));
                    } else {
                        this.color1.set(this.avatarDrawable.getColor());
                        this.color2.set(this.avatarDrawable.getColor2());
                    }
                    if (getAvatarImage() != null && getAvatarImage().getImageHeight() != 0.0f) {
                        getAvatarImage().setImageCoords(getAvatarImage().getImageX(), (getMeasuredHeight() - getAvatarImage().getImageHeight()) - AndroidUtilities.dp(4.0f), getAvatarImage().getImageWidth(), getAvatarImage().getImageHeight());
                        getAvatarImage().setRoundRadius(ExteraConfig.getAvatarCorners(getAvatarImage().getImageHeight(), true));
                        getAvatarImage().draw(canvas);
                    } else if (this.val$type == 2) {
                        invalidate();
                    }
                    super.dispatchDraw(canvas);
                }
            };
            this.cells[i3].setDelegate(new ChatMessageCell.ChatMessageCellDelegate() { // from class: org.telegram.ui.Cells.ThemePreviewMessagesCell.2
                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public boolean canPerformActions() {
                    return ThemePreviewMessagesCell.this.allowLoadingOnTouch();
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public void didPressReplyMessage(ChatMessageCell chatMessageCell2, int i6, float f, float f2, boolean z3) {
                    if (ThemePreviewMessagesCell.this.allowLoadingOnTouch()) {
                        ThemePreviewMessagesCell.this.progress = 0;
                        chatMessageCell2.invalidate();
                        AndroidUtilities.cancelRunOnUIThread(ThemePreviewMessagesCell.this.cancelProgress);
                        AndroidUtilities.runOnUIThread(ThemePreviewMessagesCell.this.cancelProgress, 5000L);
                    }
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public void needOpenWebView(MessageObject messageObject9, String str, String str2, String str3, String str4, int i6, int i7) {
                    if (ThemePreviewMessagesCell.this.allowLoadingOnTouch()) {
                        ThemePreviewMessagesCell.this.progress = 2;
                        AndroidUtilities.cancelRunOnUIThread(ThemePreviewMessagesCell.this.cancelProgress);
                        AndroidUtilities.runOnUIThread(ThemePreviewMessagesCell.this.cancelProgress, 5000L);
                    }
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public void didPressInstantButton(ChatMessageCell chatMessageCell2, int i6) {
                    if (ThemePreviewMessagesCell.this.allowLoadingOnTouch()) {
                        ThemePreviewMessagesCell.this.progress = 2;
                        chatMessageCell2.invalidate();
                        AndroidUtilities.cancelRunOnUIThread(ThemePreviewMessagesCell.this.cancelProgress);
                        AndroidUtilities.runOnUIThread(ThemePreviewMessagesCell.this.cancelProgress, 5000L);
                    }
                }

                @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
                public boolean isProgressLoading(ChatMessageCell chatMessageCell2, int i6) {
                    return i6 == ThemePreviewMessagesCell.this.progress;
                }
            });
            ChatMessageCell chatMessageCell2 = this.cells[i3];
            if (i != 2) {
                z = 1;
            } else {
                z = 1;
            }
            chatMessageCell2.isChat = z;
            chatMessageCell2.setFullyDraw(true);
            if (i3 == 0) {
                messageObject3 = messageObject2;
            } else {
                messageObject3 = messageObject;
            }
            if (messageObject3 == null) {
                this.cells[i3].setMessageObject(messageObject3, null, false, false, false);
                addView(this.cells[i3], LayoutHelper.createLinear(-1, -2));
            }
            i3++;
            resourcesProvider2 = resourcesProvider;
        }
    }

    public ChatMessageCell[] getCells() {
        return this.cells;
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        int i = 0;
        while (true) {
            ChatMessageCell[] chatMessageCellArr = this.cells;
            if (i >= chatMessageCellArr.length) {
                return;
            }
            chatMessageCellArr[i].invalidate();
            i++;
        }
    }

    public void setOverrideBackground(Drawable drawable) {
        this.overrideDrawable = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
        }
        if ((this.overrideDrawable instanceof ChatBackgroundDrawable) && isAttachedToWindow()) {
            ((ChatBackgroundDrawable) this.overrideDrawable).onAttachedToWindow(this);
        }
        invalidate();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Drawable drawable = this.overrideDrawable;
        if (drawable instanceof ChatBackgroundDrawable) {
            ((ChatBackgroundDrawable) drawable).onAttachedToWindow(this);
        }
    }

    @Override // android.view.View
    public boolean verifyDrawable(Drawable drawable) {
        return drawable == this.overrideDrawable || drawable == this.oldBackgroundDrawable || super.verifyDrawable(drawable);
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onDraw(Canvas canvas) {
        Drawable cachedWallpaperNonBlocking = this.overrideDrawable;
        if (cachedWallpaperNonBlocking == null) {
            cachedWallpaperNonBlocking = Theme.getCachedWallpaperNonBlocking();
        }
        if (Theme.wallpaperLoadTask != null) {
            invalidate();
        }
        if (cachedWallpaperNonBlocking != this.backgroundDrawable && cachedWallpaperNonBlocking != null) {
            if (Theme.isAnimatingColor() || this.customAnimation) {
                this.oldBackgroundDrawable = this.backgroundDrawable;
                this.oldBackgroundGradientDisposable = this.backgroundGradientDisposable;
            } else {
                BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
                if (disposable != null) {
                    disposable.dispose();
                    this.backgroundGradientDisposable = null;
                }
            }
            this.backgroundDrawable = cachedWallpaperNonBlocking;
            this.overrideDrawableUpdate.set(0.0f, true);
        }
        float themeAnimationValue = this.customAnimation ? this.overrideDrawableUpdate.set(1.0f) : this.parentLayout.getThemeAnimationValue();
        int i = 0;
        while (i < 2) {
            Drawable drawable = i == 0 ? this.oldBackgroundDrawable : this.backgroundDrawable;
            if (drawable != null) {
                int i2 = (i != 1 || this.oldBackgroundDrawable == null || (this.parentLayout == null && !this.customAnimation)) ? 255 : (int) (255.0f * themeAnimationValue);
                if (i2 > 0) {
                    drawable.setAlpha(i2);
                    if ((drawable instanceof ColorDrawable) || (drawable instanceof GradientDrawable) || (drawable instanceof MotionBackgroundDrawable)) {
                        drawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
                        if (drawable instanceof BackgroundGradientDrawable) {
                            this.backgroundGradientDisposable = ((BackgroundGradientDrawable) drawable).drawExactBoundsSize(canvas, this);
                        } else {
                            drawable.draw(canvas);
                        }
                    } else if (drawable instanceof BitmapDrawable) {
                        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                        bitmapDrawable.setFilterBitmap(true);
                        if (bitmapDrawable.getTileModeX() == Shader.TileMode.REPEAT) {
                            canvas.save();
                            float f = 2.0f / AndroidUtilities.density;
                            canvas.scale(f, f);
                            drawable.setBounds(0, 0, (int) Math.ceil(getMeasuredWidth() / f), (int) Math.ceil(getMeasuredHeight() / f));
                        } else {
                            int measuredHeight = getMeasuredHeight();
                            float fMax = Math.max(getMeasuredWidth() / drawable.getIntrinsicWidth(), measuredHeight / drawable.getIntrinsicHeight());
                            int iCeil = (int) Math.ceil(drawable.getIntrinsicWidth() * fMax);
                            int iCeil2 = (int) Math.ceil(drawable.getIntrinsicHeight() * fMax);
                            int measuredWidth = (getMeasuredWidth() - iCeil) / 2;
                            int i3 = (measuredHeight - iCeil2) / 2;
                            canvas.save();
                            canvas.clipRect(0, 0, iCeil, getMeasuredHeight());
                            drawable.setBounds(measuredWidth, i3, iCeil + measuredWidth, iCeil2 + i3);
                        }
                        drawable.draw(canvas);
                        canvas.restore();
                    } else {
                        StoryEntry.drawBackgroundDrawable(canvas, drawable, getWidth(), getHeight());
                    }
                    if (i == 0 && this.oldBackgroundDrawable != null && themeAnimationValue >= 1.0f) {
                        BackgroundGradientDrawable.Disposable disposable2 = this.oldBackgroundGradientDisposable;
                        if (disposable2 != null) {
                            disposable2.dispose();
                            this.oldBackgroundGradientDisposable = null;
                        }
                        this.oldBackgroundDrawable = null;
                        invalidate();
                    }
                }
            }
            i++;
        }
        this.shadowDrawable.setBounds(0, 0, getMeasuredWidth(), getMeasuredHeight());
        this.shadowDrawable.draw(canvas);
    }

    public boolean allowLoadingOnTouch() {
        int i = this.type;
        return i == 3 || i == 0;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        BackgroundGradientDrawable.Disposable disposable = this.backgroundGradientDisposable;
        if (disposable != null) {
            disposable.dispose();
            this.backgroundGradientDisposable = null;
        }
        BackgroundGradientDrawable.Disposable disposable2 = this.oldBackgroundGradientDisposable;
        if (disposable2 != null) {
            disposable2.dispose();
            this.oldBackgroundGradientDisposable = null;
        }
        Drawable drawable = this.overrideDrawable;
        if (drawable instanceof ChatBackgroundDrawable) {
            ((ChatBackgroundDrawable) drawable).onDetachedFromWindow(this);
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.type == 2 || allowLoadingOnTouch()) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        return false;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.type == 2 || allowLoadingOnTouch()) {
            return super.dispatchTouchEvent(motionEvent);
        }
        return false;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.type == 2 || allowLoadingOnTouch()) {
            return super.onTouchEvent(motionEvent);
        }
        return false;
    }
}
