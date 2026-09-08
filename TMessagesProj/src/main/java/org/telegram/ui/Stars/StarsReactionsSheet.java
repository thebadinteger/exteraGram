package org.telegram.ui.Stars;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.TransitionManager;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.gms.cast.MediaError;
import com.google.zxing.common.detector.MathUtils;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import me.vkryl.android.animator.BoolAnimator;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.AccountFrozenAlert;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BatchParticlesDrawHelper;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.Premium.GLIcon.GLIconRenderer;
import org.telegram.ui.Components.Premium.GLIcon.GLIconTextureView;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.Text;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stories.HighlightMessageSheet;
import org.telegram.ui.Stories.LiveCommentsView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class StarsReactionsSheet extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private final BalanceCloud balanceCloud;
    private final ButtonWithCounterView buttonView;
    private ChatActivity chatActivity;
    private final CheckBox2 checkBox;
    private final LinearLayout checkLayout;
    private final View checkSeparatorView;
    private final TextView checkTextView;
    private boolean checkedVisiblity;
    private final ImageView closeView;
    private LiveCommentsView.Message commentMessage;
    private LiveCommentsView.LiveCommentView commentView;
    private LiveCommentsView commentsView;
    private final int currentAccount;
    private final BackupImageView dialogImageView;
    private final ImageView dialogSelectorIconView;
    private final FrameLayout dialogSelectorInnerLayout;
    private final FrameLayout dialogSelectorLayout;
    private final GLIconTextureView icon3dView;
    private ValueAnimator iconAnimator;
    public long lastSelectedPeer;
    private final LinearLayout layout;
    private final boolean liveStories;
    private View messageCell;
    private int messageId;
    private final MessageObject messageObject;
    private Utilities.Callback2Return<Long, Long, Integer> onSendListener;
    public long peer;
    private final ArrayList<TLRPC.MessageReactor> reactors;
    private final Theme.ResourcesProvider resourcesProvider;
    private final boolean sendEnabled;
    private boolean sending;
    private int sentMessageId;
    private final View separatorView;
    private final StarsSlider slider;
    private final ColoredImageSpan[] starRef;
    private final TextView statusView;
    private final TextView titleView;
    private final FrameLayout topLayout;
    private final TopSendersView topSendersView;
    private final LinearLayout toptopLayout;

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void appendOpenAnimator(boolean z, ArrayList<Animator> arrayList) {
        arrayList.add(ObjectAnimator.ofFloat(this.balanceCloud, (Property<BalanceCloud, Float>) View.ALPHA, z ? 1.0f : 0.0f));
        arrayList.add(ObjectAnimator.ofFloat(this.balanceCloud, (Property<BalanceCloud, Float>) View.SCALE_X, z ? 1.0f : 0.6f));
        arrayList.add(ObjectAnimator.ofFloat(this.balanceCloud, (Property<BalanceCloud, Float>) View.SCALE_Y, z ? 1.0f : 0.6f));
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean isTouchOutside(float f, float f2) {
        if (f < this.balanceCloud.getX() || f > this.balanceCloud.getX() + this.balanceCloud.getWidth() || f2 < this.balanceCloud.getY() || f2 > this.balanceCloud.getY() + this.balanceCloud.getHeight()) {
            return super.isTouchOutside(f, f2);
        }
        return false;
    }

    throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarsReactionsSheet.<init>(android.content.Context, int, long, org.telegram.ui.ChatActivity, org.telegram.messenger.MessageObject, java.util.ArrayList, boolean, boolean, long, org.telegram.ui.ActionBar.Theme$ResourcesProvider):void");
    }

    public int $r8$lambda$bCH_sQJJecgHsNrSQOp5eQYI_k8(SenderData senderData, SenderData senderData2) {
        return (int) (senderData2.stars - senderData.stars);
    }

    private void checkVisibility() {
        if (this.checkedVisiblity) {
            return;
        }
        this.checkedVisiblity = true;
        MessageObject messageObject = this.messageObject;
        if (messageObject == null) {
            return;
        }
        Long myPaidReactionPeer = messageObject.getMyPaidReactionPeer();
        if (myPaidReactionPeer == null || myPaidReactionPeer.longValue() != this.peer) {
            this.messageObject.setMyPaidReactionDialogId(this.peer);
            StarsController.MessageId messageIdFrom = StarsController.MessageId.from(this.messageObject);
            TLRPC.TL_messages_togglePaidReactionPrivacy tL_messages_togglePaidReactionPrivacy = new TLRPC.TL_messages_togglePaidReactionPrivacy();
            tL_messages_togglePaidReactionPrivacy.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(messageIdFrom.did);
            tL_messages_togglePaidReactionPrivacy.msg_id = messageIdFrom.mid;
            long j = this.peer;
            if (j == 0) {
                tL_messages_togglePaidReactionPrivacy.privacy = new TL_stars.paidReactionPrivacyDefault();
            } else if (j == UserObject.ANONYMOUS) {
                tL_messages_togglePaidReactionPrivacy.privacy = new TL_stars.paidReactionPrivacyAnonymous();
            } else {
                TL_stars.paidReactionPrivacyPeer paidreactionprivacypeer = new TL_stars.paidReactionPrivacyPeer();
                tL_messages_togglePaidReactionPrivacy.privacy = paidreactionprivacypeer;
                paidreactionprivacypeer.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.peer);
            }
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.starReactionAnonymousUpdate, Long.valueOf(messageIdFrom.did), Integer.valueOf(messageIdFrom.mid), Long.valueOf(this.peer));
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_togglePaidReactionPrivacy, new RequestDelegate() { // from class: org.telegram.ui.Stars.StarsReactionsSheet$$ExternalSyntheticLambda0
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$checkVisibility$12(tLObject, tL_error);
                }
            });
        }
    }

    public private void animate3dIcon(final Runnable runnable) {
        View view;
        final ReactionsLayoutInBubble reactionsLayoutInBubble;
        LiveCommentsView liveCommentsView;
        View view2;
        ReactionsLayoutInBubble reactionsLayoutInBubble2;
        ReactionsLayoutInBubble.ReactionButton reactionButton;
        ReactionsLayoutInBubble.ReactionButton reactionButton2 = null;
        MessageObject messageObject = null;
        if (this.messageObject == null || (view2 = this.chatActivity.fragmentView) == null || !view2.isAttachedToWindow()) {
            if (this.commentsView == null) {
                return;
            }
            view = null;
            reactionsLayoutInBubble = null;
        } else {
            View viewFindMessageCell = this.messageCell;
            if (viewFindMessageCell instanceof ChatMessageCell) {
                reactionsLayoutInBubble2 = ((ChatMessageCell) viewFindMessageCell).reactionsLayoutInBubble;
                reactionButton = reactionsLayoutInBubble2.getReactionButton(ReactionsLayoutInBubble.VisibleReaction.asStar());
            } else if (viewFindMessageCell instanceof ChatActionCell) {
                reactionsLayoutInBubble2 = ((ChatActionCell) viewFindMessageCell).reactionsLayoutInBubble;
                reactionButton = reactionsLayoutInBubble2.getReactionButton(ReactionsLayoutInBubble.VisibleReaction.asStar());
            } else {
                reactionsLayoutInBubble2 = null;
                reactionButton = null;
            }
            if (reactionButton != null || reactionsLayoutInBubble2 == null) {
                reactionButton2 = reactionButton;
            } else {
                MessageObject.GroupedMessages validGroupedMessage = this.chatActivity.getValidGroupedMessage(this.messageObject);
                if (validGroupedMessage != null && !validGroupedMessage.posArray.isEmpty()) {
                    ArrayList<MessageObject> arrayList = validGroupedMessage.messages;
                    int size = arrayList.size();
                    int i = 0;
                    while (i < size) {
                        MessageObject messageObject2 = arrayList.get(i);
                        i++;
                        MessageObject messageObject3 = messageObject2;
                        MessageObject.GroupedMessagePosition position = validGroupedMessage.getPosition(messageObject3);
                        if (position != null) {
                            int i2 = position.flags;
                            if ((i2 & 1) != 0 && (i2 & 8) != 0) {
                                messageObject = messageObject3;
                                break;
                            }
                        }
                    }
                    if (messageObject != null) {
                        viewFindMessageCell = this.chatActivity.findMessageCell(messageObject.getId(), false);
                    }
                }
                if (viewFindMessageCell == null) {
                    return;
                }
                if (viewFindMessageCell instanceof ChatMessageCell) {
                    ReactionsLayoutInBubble reactionsLayoutInBubble3 = ((ChatMessageCell) viewFindMessageCell).reactionsLayoutInBubble;
                    reactionsLayoutInBubble2 = reactionsLayoutInBubble3;
                    reactionButton2 = reactionsLayoutInBubble3.getReactionButton(ReactionsLayoutInBubble.VisibleReaction.asStar());
                } else {
                    reactionButton2 = reactionButton;
                }
            }
            if (reactionButton2 == null) {
                return;
            }
            reactionsLayoutInBubble = reactionsLayoutInBubble2;
            view = viewFindMessageCell;
        }
        final View view3 = view;
        final int[] iArr = new int[2];
        final RectF rectF = new RectF();
        this.slider.getLocationInWindow(iArr);
        rectF.set(this.slider.counterImage.getBounds());
        rectF.inset(-AndroidUtilities.dp(3.5f), -AndroidUtilities.dp(3.5f));
        rectF.offset(iArr[0], iArr[1]);
        this.icon3dView.whenReady(new Runnable() { // from class: org.telegram.ui.Stars.StarsReactionsSheet$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$animate3dIcon$13();
            }
        });
        if (reactionButton2 != null) {
            reactionButton2.drawImage = false;
        }
        if (view3 != null) {
            view3.invalidate();
        }
        final LiveCommentsView.LiveCommentView[] liveCommentViewArr = new LiveCommentsView.LiveCommentView[1];
        if (this.liveStories && (liveCommentsView = this.commentsView) != null) {
            liveCommentViewArr[0] = liveCommentsView.findComment(this.sentMessageId);
        }
        final ReactionsLayoutInBubble.ReactionButton reactionButton3 = reactionButton2;
        final RectF rectF2 = new RectF();
        final Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Stars.StarsReactionsSheet$$ExternalSyntheticLambda16
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$animate3dIcon$14(liveCommentViewArr, iArr, rectF2, view3, reactionsLayoutInBubble, reactionButton3);
            }
        };
        runnable2.run();
        this.icon3dView.setPaused(false);
        this.icon3dView.setVisibility(0);
        final RectF rectF3 = new RectF();
        rectF3.set(rectF);
        this.icon3dView.setTranslationX(rectF3.centerX() - (AndroidUtilities.dp(150.0f) / 2.0f));
        this.icon3dView.setTranslationY(rectF3.centerY() - (AndroidUtilities.dp(150.0f) / 2.0f));
        this.icon3dView.setScaleX(rectF3.width() / AndroidUtilities.dp(150.0f));
        this.icon3dView.setScaleY(rectF3.height() / AndroidUtilities.dp(150.0f));
        ValueAnimator valueAnimator = this.iconAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        final boolean[] zArr = new boolean[1];
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.iconAnimator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Stars.StarsReactionsSheet$$ExternalSyntheticLambda17
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.lambda$animate3dIcon$15(runnable2, rectF, rectF2, rectF3, zArr, runnable, valueAnimator2);
            }
        });
        this.iconAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Stars.StarsReactionsSheet.7
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                StarsReactionsSheet.this.icon3dView.setVisibility(4);
                StarsReactionsSheet.this.icon3dView.setPaused(true);
                ReactionsLayoutInBubble.ReactionButton reactionButton4 = reactionButton3;
                if (reactionButton4 != null) {
                    reactionButton4.drawImage = true;
                }
                View view4 = view3;
                if (view4 != null) {
                    view4.invalidate();
                }
                LiveCommentsView.LiveCommentView liveCommentView = liveCommentViewArr[0];
                if (liveCommentView != null) {
                    liveCommentView.setDrawStar(true);
                }
                StarsReactionsSheet.super.dismissInternal();
                boolean[] zArr2 = zArr;
                if (!zArr2[0]) {
                    zArr2[0] = true;
                    LaunchActivity.makeRipple(rectF2.centerX(), rectF2.centerY(), 1.5f);
                    try {
                        StarsReactionsSheet.this.container.performHapticFeedback(0, 1);
                    } catch (Exception unused) {
                    }
                    Runnable runnable3 = runnable;
                    if (runnable3 != null) {
                        runnable3.run();
                    }
                }
                LaunchActivity launchActivity = LaunchActivity.instance;
                if (launchActivity == null || launchActivity.getFireworksOverlay() == null) {
                    return;
                }
                LaunchActivity.instance.getFireworksOverlay().start(true);
            }
        });
        this.iconAnimator.setDuration(800L);
        this.iconAnimator.setInterpolator(new Interpolator() { // from class: org.telegram.ui.Stars.StarsReactionsSheet.8
            @Override // android.animation.TimeInterpolator
            public float getInterpolation(float f) {
                return (float) Math.pow(f, 2.0d);
            }
        });
        this.iconAnimator.start();
    }

    public /* synthetic */ void lambda$animate3dIcon$13() {
        StarsSlider starsSlider = this.slider;
        starsSlider.drawCounterImage = false;
        starsSlider.invalidate();
    }

    public /* synthetic */ void lambda$animate3dIcon$14(LiveCommentsView.LiveCommentView[] liveCommentViewArr, int[] iArr, RectF rectF, View view, ReactionsLayoutInBubble reactionsLayoutInBubble, ReactionsLayoutInBubble.ReactionButton reactionButton) {
        if (this.liveStories) {
            LiveCommentsView.LiveCommentView liveCommentViewFindComment = liveCommentViewArr[0];
            if (liveCommentViewFindComment == null) {
                liveCommentViewFindComment = this.commentsView.findComment(this.sentMessageId);
                liveCommentViewArr[0] = liveCommentViewFindComment;
            }
            if (liveCommentViewFindComment != null) {
                liveCommentViewFindComment.setDrawStar(false);
                liveCommentViewFindComment.getLocationInWindow(iArr);
                liveCommentViewFindComment.getStarLocation(rectF);
                rectF.offset(iArr[0], iArr[1]);
                return;
            }
            return;
        }
        view.getLocationInWindow(iArr);
        rectF.set(iArr[0] + reactionsLayoutInBubble.x + reactionButton.x + AndroidUtilities.dp(4.0f), iArr[1] + reactionsLayoutInBubble.y + reactionButton.y + ((reactionButton.height - AndroidUtilities.dp(22.0f)) / 2.0f), iArr[0] + reactionsLayoutInBubble.x + reactionButton.x + AndroidUtilities.dp(26.0f), iArr[1] + reactionsLayoutInBubble.y + reactionButton.y + ((reactionButton.height + AndroidUtilities.dp(22.0f)) / 2.0f));
    }

    public /* synthetic */ void lambda$animate3dIcon$15(Runnable runnable, RectF rectF, RectF rectF2, RectF rectF3, boolean[] zArr, Runnable runnable2, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        runnable.run();
        AndroidUtilities.lerp(rectF, rectF2, fFloatValue, rectF3);
        this.icon3dView.setTranslationX(rectF3.centerX() - (AndroidUtilities.dp(150.0f) / 2.0f));
        this.icon3dView.setTranslationY(rectF3.centerY() - (AndroidUtilities.dp(150.0f) / 2.0f));
        float fLerp = AndroidUtilities.lerp(Math.max(rectF3.width() / AndroidUtilities.dp(150.0f), rectF3.height() / AndroidUtilities.dp(150.0f)), 1.0f, (float) Math.sin(((double) fFloatValue) * 3.141592653589793d));
        this.icon3dView.setScaleX(fLerp);
        this.icon3dView.setScaleY(fLerp);
        GLIconRenderer gLIconRenderer = this.icon3dView.mRenderer;
        gLIconRenderer.angleX = 360.0f * fFloatValue;
        gLIconRenderer.white = Math.max(0.0f, 1.0f - (4.0f * fFloatValue));
        if (zArr[0] || fFloatValue <= 0.95f) {
            return;
        }
        zArr[0] = true;
        LaunchActivity.makeRipple(rectF2.centerX(), rectF2.centerY(), 1.5f);
        try {
            this.container.performHapticFeedback(0, 1);
        } catch (Exception unused) {
        }
        if (runnable2 != null) {
            runnable2.run();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public void dismissInternal() {
        ValueAnimator valueAnimator = this.iconAnimator;
        if (valueAnimator == null || !valueAnimator.isRunning()) {
            super.dismissInternal();
        }
    }

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean canDismissWithSwipe() {
        if (this.slider.tracking) {
            return false;
        }
        return super.canDismissWithSwipe();
    }

    public static class StarsSlider extends View {
        public float aprogress;
        private final RectF arc;
        private final Drawable counterImage;
        private final AnimatedTextView.AnimatedTextDrawable counterSubText;
        private final AnimatedTextView.AnimatedTextDrawable counterText;
        private long currentTop;
        public boolean drawCounterImage;
        public boolean drawPlus;
        private LinearGradient gradient;
        private ValueAnimator gradientAnimator;
        private int gradientColor1;
        private int gradientColor2;
        private final Matrix gradientMatrix;
        private float lastX;
        private float lastY;
        private final AnimatedFloat overTop;
        private final AnimatedFloat overTopText;
        private final Paint plusPaint;
        private final Path plusPath;
        private int pointerId;
        private long pressTime;
        public float progress;
        private ValueAnimator progressAnimator;
        private final Theme.ResourcesProvider resourcesProvider;
        private final Paint sliderCirclePaint;
        private final RectF sliderCircleRect;
        private final Paint sliderInnerPaint;
        private final Path sliderInnerPath;
        private final RectF sliderInnerRect;
        private final Paint sliderPaint;
        private final Particles sliderParticles;
        private final Path sliderPath;
        private final RectF sliderRect;
        private final ColoredImageSpan[] starRef;
        public int steps;
        public int[] stops;
        private final BoolAnimator subTextVisible;
        private final Paint textBackgroundPaint;
        private final Particles textParticles;
        private final Path textPath;
        private final RectF textRect;
        private int toGradientColor1;
        private int toGradientColor2;
        private final Paint topPaint;
        private final Text topText;
        private boolean tracking;

        public boolean onTapCustom(float f, float f2) {
            return false;
        }

        public void onValueChanged(int i) {
        }

        public StarsSlider(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.sliderInnerPaint = new Paint(1);
            this.sliderPaint = new Paint(1);
            this.plusPaint = new Paint(1);
            this.sliderCirclePaint = new Paint(1);
            this.textBackgroundPaint = new Paint(1);
            this.sliderParticles = new Particles(0, 300);
            this.textParticles = new Particles(2, 30);
            this.gradientColor1 = -1135603;
            this.gradientColor2 = -404714;
            this.toGradientColor1 = -1135603;
            this.toGradientColor2 = -404714;
            this.gradient = new LinearGradient(0.0f, 0.0f, 255.0f, 0.0f, new int[]{this.gradientColor1, this.gradientColor2}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            this.gradientMatrix = new Matrix();
            this.drawCounterImage = true;
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
            this.counterText = animatedTextDrawable;
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable2 = new AnimatedTextView.AnimatedTextDrawable(false, true, true);
            this.counterSubText = animatedTextDrawable2;
            this.starRef = new ColoredImageSpan[1];
            Paint paint = new Paint(1);
            this.topPaint = paint;
            this.topText = new Text(LocaleController.getString(R.string.StarsReactionTop), 14.0f, AndroidUtilities.getTypeface(AndroidUtilities.TYPEFACE_ROBOTO_CONDENSED_BOLD));
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
            this.overTop = new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator);
            this.overTopText = new AnimatedFloat(this, 0L, 320L, cubicBezierInterpolator);
            this.currentTop = -1L;
            this.sliderInnerRect = new RectF();
            this.sliderRect = new RectF();
            this.sliderCircleRect = new RectF();
            this.arc = new RectF();
            this.sliderInnerPath = new Path();
            this.sliderPath = new Path();
            this.plusPath = new Path();
            this.textRect = new RectF();
            this.textPath = new Path();
            this.progress = 0.0f;
            this.subTextVisible = new BoolAnimator(this, cubicBezierInterpolator, 320L);
            this.resourcesProvider = resourcesProvider;
            Drawable drawableMutate = context.getResources().getDrawable(R.drawable.msg_premium_liststar).mutate();
            this.counterImage = drawableMutate;
            drawableMutate.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
            animatedTextDrawable.setTextColor(-1);
            animatedTextDrawable.setTypeface(AndroidUtilities.getTypeface("fonts/num.otf"));
            animatedTextDrawable.setTextSize(AndroidUtilities.dp(21.0f));
            animatedTextDrawable.setCallback(this);
            animatedTextDrawable.setOverrideFullWidth(AndroidUtilities.displaySize.x);
            animatedTextDrawable.setGravity(17);
            animatedTextDrawable2.setTextColor(-570425345);
            animatedTextDrawable2.setTextSize(AndroidUtilities.dp(11.0f));
            animatedTextDrawable2.setCallback(this);
            animatedTextDrawable2.setOverrideFullWidth(AndroidUtilities.displaySize.x);
            animatedTextDrawable2.setGravity(17);
            paint.setColor(Theme.getColor(Theme.key_dialogBackground, resourcesProvider));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(AndroidUtilities.dp(1.0f));
        }

        public void setStarsTop(long j) {
            this.currentTop = j;
            invalidate();
        }

        public void setTopText(String str) {
            this.topText.setText(str);
        }

        @Override // android.view.View
        public boolean verifyDrawable(Drawable drawable) {
            return drawable == this.counterText || super.verifyDrawable(drawable);
        }

        public void setSteps(int i, int... iArr) {
            this.steps = i;
            this.stops = iArr;
        }

        public void setValue(int i) {
            setValue(i, false);
        }

        public void setValue(int i, boolean z) {
            float progress = getProgress(i);
            this.progress = progress;
            if (!z) {
                this.aprogress = progress;
            }
            updateText(true);
        }

        public int getValue() {
            return getValue(this.progress);
        }

        public float getProgress() {
            return this.progress;
        }

        public int getValue(float f) {
            if (f <= 0.0f) {
                return this.stops[0];
            }
            int[] iArr = this.stops;
            if (f >= 1.0f) {
                return iArr[iArr.length - 1];
            }
            float length = f * (iArr.length - 1);
            int i = (int) length;
            float f2 = length - i;
            int i2 = iArr[i];
            float f3 = i2;
            int i3 = i + 1;
            if (i3 < iArr.length) {
                i = i3;
            }
            return Math.round(f3 + (f2 * (iArr[i] - i2)));
        }

        public float getProgress(int i) {
            int i2 = 1;
            while (true) {
                int[] iArr = this.stops;
                if (i2 >= iArr.length) {
                    return 1.0f;
                }
                int i3 = iArr[i2];
                if (i <= i3) {
                    int i4 = i2 - 1;
                    int i5 = iArr[i4];
                    return (i4 + ((i - i5) / (i3 - i5))) / (iArr.length - 1);
                }
                i2++;
            }
        }

        public void setColor(final int i, final int i2, boolean z) {
            if (this.toGradientColor1 == i && this.toGradientColor2 == i2) {
                return;
            }
            ValueAnimator valueAnimator = this.gradientAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.gradientAnimator = null;
            }
            if (z) {
                final int i3 = this.gradientColor1;
                final int i4 = this.gradientColor2;
                this.toGradientColor1 = i;
                this.toGradientColor2 = i2;
                ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.gradientAnimator = valueAnimatorOfFloat;
                valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Stars.StarsReactionsSheet$StarsSlider$$ExternalSyntheticLambda1
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        this.f$0.lambda$setColor$0(i3, i, i4, i2, valueAnimator2);
                    }
                });
                this.gradientAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Stars.StarsReactionsSheet.StarsSlider.1
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        StarsSlider.this.gradientColor1 = ColorUtils.blendARGB(i3, i, 1.0f);
                        StarsSlider.this.gradientColor2 = ColorUtils.blendARGB(i4, i2, 1.0f);
                        StarsSlider.this.gradient = new LinearGradient(0.0f, 0.0f, 255.0f, 0.0f, new int[]{StarsSlider.this.gradientColor1, StarsSlider.this.gradientColor2}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                        StarsSlider.this.invalidate();
                    }
                });
                this.gradientAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                this.gradientAnimator.setDuration(420L);
                this.gradientAnimator.start();
                return;
            }
            this.toGradientColor1 = i;
            this.gradientColor1 = i;
            this.toGradientColor2 = i2;
            this.gradientColor2 = i2;
            this.gradient = new LinearGradient(0.0f, 0.0f, 255.0f, 0.0f, new int[]{this.gradientColor1, this.gradientColor2}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            invalidate();
        }

        public /* synthetic */ void lambda$setColor$0(int i, int i2, int i3, int i4, ValueAnimator valueAnimator) {
            float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            this.gradientColor1 = ColorUtils.blendARGB(i, i2, fFloatValue);
            this.gradientColor2 = ColorUtils.blendARGB(i3, i4, fFloatValue);
            this.gradient = new LinearGradient(0.0f, 0.0f, 255.0f, 0.0f, new int[]{this.gradientColor1, this.gradientColor2}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            invalidate();
        }

        public void updateText(boolean z) {
            this.counterText.cancelAnimation();
            this.counterText.setText(StarsIntroActivity.replaceStars(LocaleController.formatNumber(getValue(), ','), this.starRef), z);
        }

        @Override // android.view.View
        public void onMeasure(int i, int i2) {
            setMeasuredDimension(View.MeasureSpec.getSize(i), AndroidUtilities.dp(220.0f));
            int measuredWidth = getMeasuredWidth();
            getMeasuredHeight();
            int iDp = AndroidUtilities.dp(14.0f);
            int iDp2 = AndroidUtilities.dp(135.0f);
            this.sliderInnerRect.set(iDp, iDp2, measuredWidth - iDp, iDp2 + AndroidUtilities.dp(24.0f));
            this.sliderPaint.setColor(-1069811);
            this.sliderCirclePaint.setColor(-1);
        }

        /* JADX WARN: Failed to calculate best type for var: r0v1 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r0v1 ??, new type: org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r10v2 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r10v2 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r10v4 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r10v4 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r10v5 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r10v5 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r11v16 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r11v16 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r11v17 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r11v17 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r11v18 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r11v18 ??, new type: android.graphics.RectF
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r11v23 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r11v23 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r11v24 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r11v24 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r11v25 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r11v25 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r11v26 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r11v26 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r11v3 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r11v3 ??, new type: android.graphics.RectF
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:159)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:136)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:241)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r11v3 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r11v3 ??, new type: android.graphics.RectF
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r11v4 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r11v4 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:159)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:136)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:241)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r11v4 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r11v4 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r11v5 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r11v5 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r12v15 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r12v15 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r12v16 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r12v16 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r12v17 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r12v17 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r12v2 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r12v2 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r12v3 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r12v3 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r13v3 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v3 ??, new type: android.graphics.RectF
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r13v4 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v4 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r13v5 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v5 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r13v9 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r13v9 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r14v2 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r14v2 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r14v3 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r14v3 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r14v8 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r14v8 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r15v4 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r15v4 ??, new type: android.graphics.RectF
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r15v5 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r15v5 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r15v6 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r15v6 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r1v0 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r1v0 ??, new type: android.graphics.Canvas
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:159)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:136)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:241)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r1v0 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r1v0 ??, new type: android.graphics.Canvas
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r1v1 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r1v1 ??, new type: android.graphics.Canvas
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:159)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:136)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:241)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r1v1 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r1v1 ??, new type: android.graphics.Canvas
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r1v2 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r1v2 ??, new type: android.graphics.Canvas
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:159)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:136)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:241)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r1v2 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r1v2 ??, new type: android.graphics.Canvas
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r1v4 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r1v4 ??, new type: android.graphics.Canvas
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r1v6 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r1v6 ??, new type: android.graphics.Canvas
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r2v20 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v20 ??, new type: org.telegram.ui.Stars.StarsReactionsSheet$Particles
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:159)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:136)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:241)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r2v20 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v20 ??, new type: org.telegram.ui.Stars.StarsReactionsSheet$Particles
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r2v24 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v24 ??, new type: org.telegram.ui.Stars.StarsReactionsSheet$Particles
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:159)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:136)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:241)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r2v24 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v24 ??, new type: org.telegram.ui.Stars.StarsReactionsSheet$Particles
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r2v27 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v27 ??, new type: android.graphics.RectF
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.calculateFromBounds(FixTypesVisitor.java:159)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.setBestType(FixTypesVisitor.java:136)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:241)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r2v27 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v27 ??, new type: android.graphics.RectF
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r2v34 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v34 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r2v35 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v35 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r2v36 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v36 ??, new type: org.telegram.ui.Stars.StarsReactionsSheet$Particles
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r2v45 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v45 ??, new type: org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r2v48 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r2v48 ??, new type: android.graphics.drawable.Drawable
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r3v31 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v31 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r3v32 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v32 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r3v33 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v33 ??, new type: android.graphics.RectF
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r3v34 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v34 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r3v35 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v35 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r3v37 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v37 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r3v51 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v51 ??, new type: org.telegram.ui.Stars.StarsReactionsSheet$Particles
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r3v64 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v64 ??, new type: android.graphics.RectF
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r3v65 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v65 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r3v66 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v66 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r3v67 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r3v67 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r42v0 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r42v0 ??, new type: android.graphics.Canvas
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v18 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v18 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v20 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v20 ??, new type: android.graphics.RectF
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v22 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v22 ??, new type: android.graphics.RectF
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v23 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v23 ??, new type: android.graphics.RectF
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v24 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v24 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v25 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v25 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v26 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v26 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v30 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v30 ??, new type: android.graphics.Path
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v32 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v32 ??, new type: android.graphics.RectF
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v50 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v50 ??, new type: org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v51 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v51 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v52 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v52 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v55 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v55 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v68 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v68 ??, new type: android.graphics.Path
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r4v70 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v70 ??, new type: android.graphics.Path
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r5v27 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r5v27 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r5v35 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r5v35 ??, new type: android.graphics.RectF
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r5v36 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r5v36 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r5v37 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r5v37 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r5v38 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r5v38 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r6v10 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r6v10 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r6v11 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r6v11 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r6v12 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r6v12 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r6v13 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r6v13 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r6v16 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r6v16 ??, new type: org.telegram.ui.Components.AnimatedTextView$AnimatedTextDrawable
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r6v17 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r6v17 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r6v18 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r6v18 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r6v20 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r6v20 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r6v5 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r6v5 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r6v9 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r6v9 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r7v1 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r7v1 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r7v13 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r7v13 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r7v14 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r7v14 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r7v17 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r7v17 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r7v22 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r7v22 ??, new type: android.graphics.Path
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r7v25 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r7v25 ??, new type: android.graphics.Path
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r7v26 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r7v26 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r7v4 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r7v4 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r7v6 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r7v6 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r7v7 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r7v7 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r9v1 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r9v1 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r9v10 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r9v10 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r9v12 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r9v12 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r9v4 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r9v4 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r9v6 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r9v6 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r9v9 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r9v9 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to set immutable type for var: r42v0 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r42v0 ??, new type: android.graphics.Canvas
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.applyWithWiderIgnSame(TypeUpdate.java:73)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setImmutableType(TypeInferenceVisitor.java:111)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$0(TypeInferenceVisitor.java:102)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:102)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        /*  JADX ERROR: Types fix failed
            jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r11v4 ??, new type: float
            	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
            	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryPossibleTypes(FixTypesVisitor.java:186)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:245)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
            Caused by: java.lang.NullPointerException
            */
        @Override // android.view.View
        public void dispatchDraw(android.graphics.Canvas r42) {
            /*
                Method dump skipped, instruction units count: 1893
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stars.StarsReactionsSheet.StarsSlider.dispatchDraw(android.graphics.Canvas):void");
        }

        public void setCounterSubText(String str, boolean z) {
            this.subTextVisible.setValue(!TextUtils.isEmpty(str), z);
            this.counterSubText.cancelAnimation();
            this.counterSubText.setText(str, z);
        }

        @Override // android.view.View
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (motionEvent.getAction() == 0) {
                this.lastX = motionEvent.getX();
                this.lastY = motionEvent.getY();
                this.pointerId = motionEvent.getPointerId(0);
                this.pressTime = System.currentTimeMillis();
                this.tracking = false;
            } else if (motionEvent.getAction() == 2 && motionEvent.getPointerId(0) == this.pointerId) {
                float x = motionEvent.getX() - this.lastX;
                float y = motionEvent.getY() - this.lastY;
                if (!this.tracking && Math.abs(x) > Math.abs(y * 1.5f) && Math.abs(x) > AndroidUtilities.touchSlop) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    this.tracking = true;
                    ValueAnimator valueAnimator = this.progressAnimator;
                    if (valueAnimator != null) {
                        valueAnimator.cancel();
                    }
                }
                if (this.tracking) {
                    int value = getValue();
                    this.progress = Utilities.clamp01(this.progress + (x / (getWidth() * 1.0f)));
                    if (getValue() != value) {
                        onValueChanged(getValue());
                        updateText(true);
                    }
                    this.lastX = motionEvent.getX();
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (!this.tracking && motionEvent.getPointerId(0) == this.pointerId && MathUtils.distance(this.lastX, this.lastY, motionEvent.getX(), motionEvent.getY()) < AndroidUtilities.touchSlop && System.currentTimeMillis() - this.pressTime <= ViewConfiguration.getTapTimeout() * 1.5f && !onTapCustom(motionEvent.getX(), motionEvent.getY())) {
                    float x2 = motionEvent.getX();
                    RectF rectF = this.sliderInnerRect;
                    float fClamp01 = Utilities.clamp01((x2 - rectF.left) / rectF.width());
                    long j = this.currentTop;
                    if (j > 0 && Math.abs(getProgress((int) j) - fClamp01) < 0.035f) {
                        fClamp01 = Utilities.clamp01(getProgress((int) this.currentTop));
                    }
                    animateProgressTo(fClamp01);
                }
                this.tracking = false;
            }
            return true;
        }

        public void setValueAnimated(int i) {
            if (i == getValue()) {
                return;
            }
            animateProgressTo(getProgress(i));
        }

        private void animateProgressTo(final float f) {
            ValueAnimator valueAnimator = this.progressAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
            }
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.progress, f);
            this.progressAnimator = valueAnimatorOfFloat;
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Stars.StarsReactionsSheet$StarsSlider$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    this.f$0.lambda$animateProgressTo$1(valueAnimator2);
                }
            });
            final int value = getValue();
            this.progressAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Stars.StarsReactionsSheet.StarsSlider.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    StarsSlider starsSlider = StarsSlider.this;
                    starsSlider.progress = f;
                    if (starsSlider.getValue() != value) {
                        StarsSlider starsSlider2 = StarsSlider.this;
                        starsSlider2.onValueChanged(starsSlider2.getValue());
                    }
                    StarsSlider.this.invalidate();
                }
            });
            this.progressAnimator.setDuration(320L);
            this.progressAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.progressAnimator.start();
            if (getValue(f) != value) {
                onValueChanged(getValue(f));
            }
            this.counterText.cancelAnimation();
            this.counterText.setText(StarsIntroActivity.replaceStars(LocaleController.formatNumber(getValue(f), ','), this.starRef), true);
        }

        public /* synthetic */ void lambda$animateProgressTo$1(ValueAnimator valueAnimator) {
            this.progress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            invalidate();
        }
    }

    public static class Particles {

        public final Bitmap f390b;
        private int bPaintColor;
        private BatchParticlesDrawHelper.BatchParticlesBuffer batchParticlesBuffer;
        private final Paint batchParticlesPaint;
        private long lastInvalidateTime;
        private long lastTime;
        public final ArrayList<Particle> particles;
        public final int type;
        private int visibleCount;
        public final RectF bounds = new RectF();
        public final Paint bPaint = new Paint(3);
        public final Rect rect = new Rect();
        private float speed = 1.0f;
        private float lifetime = 1.0f;
        private boolean firstDraw = true;

        public Particles(int i, int i2) {
            this.type = i;
            this.visibleCount = i2;
            this.particles = new ArrayList<>(i2);
            for (int i3 = 0; i3 < i2; i3++) {
                this.particles.add(new Particle());
            }
            int iDp = AndroidUtilities.dp(10.0f);
            Bitmap bitmapCreateBitmap = Bitmap.createBitmap(iDp, iDp, Bitmap.Config.ARGB_8888);
            this.f390b = bitmapCreateBitmap;
            Path path = new Path();
            float f = iDp >> 1;
            int i4 = (int) (0.85f * f);
            path.moveTo(0.0f, f);
            float f2 = i4;
            path.lineTo(f2, f2);
            path.lineTo(f, 0.0f);
            float f3 = iDp - i4;
            path.lineTo(f3, f2);
            float f4 = iDp;
            path.lineTo(f4, f);
            path.lineTo(f3, f3);
            path.lineTo(f, f4);
            path.lineTo(f2, f3);
            path.lineTo(0.0f, f);
            path.close();
            Canvas canvas = new Canvas(bitmapCreateBitmap);
            Paint paint = new Paint();
            paint.setColor(Theme.multAlpha(-1, 0.75f));
            canvas.drawPath(path, paint);
            if (BatchParticlesDrawHelper.isAvailable()) {
                BatchParticlesDrawHelper.BatchParticlesBuffer batchParticlesBuffer = new BatchParticlesDrawHelper.BatchParticlesBuffer(i2);
                this.batchParticlesBuffer = batchParticlesBuffer;
                batchParticlesBuffer.fillParticleTextureCords(0.0f, 0.0f, bitmapCreateBitmap.getWidth(), bitmapCreateBitmap.getHeight());
                this.batchParticlesPaint = BatchParticlesDrawHelper.createBatchParticlesPaint(bitmapCreateBitmap);
                return;
            }
            this.batchParticlesBuffer = null;
            this.batchParticlesPaint = null;
        }

        public void setVisible(float f) {
            this.visibleCount = (int) (this.particles.size() * f);
        }

        public void setBounds(RectF rectF) {
            this.bounds.set(rectF);
            removeParticlesOutside();
        }

        public void setBounds(Rect rect) {
            this.bounds.set(rect);
            removeParticlesOutside();
        }

        public void setBounds(int i, int i2, int i3, int i4) {
            this.bounds.set(i, i2, i3, i4);
            removeParticlesOutside();
        }

        public void removeParticlesOutside() {
            if (this.type == 2) {
                long jCurrentTimeMillis = System.currentTimeMillis();
                for (int i = 0; i < this.particles.size(); i++) {
                    Particle particle = this.particles.get(i);
                    if (!this.bounds.contains((int) particle.x, (int) particle.y)) {
                        gen(particle, jCurrentTimeMillis, this.firstDraw);
                    }
                }
            }
        }

        public void setSpeed(float f) {
            this.speed = f;
        }

        public boolean process() {
            if (!LiteMode.isEnabled(131072)) {
                return false;
            }
            long jCurrentTimeMillis = System.currentTimeMillis();
            float fMin = (Math.min(this.lastTime - jCurrentTimeMillis, 16L) / 1000.0f) * this.speed;
            for (int i = 0; i < Math.min(this.visibleCount, this.particles.size()); i++) {
                Particle particle = this.particles.get(i);
                long j = particle.lifetime;
                float f = j <= 0 ? 2.0f : (jCurrentTimeMillis - particle.start) / j;
                if (f > 1.0f) {
                    gen(particle, jCurrentTimeMillis, this.firstDraw);
                    f = 0.0f;
                }
                particle.x += particle.vx * fMin;
                particle.y += particle.vy * fMin;
                float f2 = 4.0f * f;
                particle.la = f2 - (f * f2);
            }
            this.lastTime = jCurrentTimeMillis;
            long j2 = this.lastInvalidateTime;
            if (j2 != 0 && j2 - jCurrentTimeMillis < 66) {
                return false;
            }
            this.lastInvalidateTime = jCurrentTimeMillis;
            return true;
        }

        public void generateGrid() {
            ArrayList<PointF> arrayListPoissonDiskSampling = poissonDiskSampling(AndroidUtilities.dp(30.0f), (int) this.bounds.width(), (int) this.bounds.height(), 15);
            int size = arrayListPoissonDiskSampling.size() - this.particles.size();
            for (int i = 0; i < size; i++) {
                this.particles.add(new Particle());
            }
            int size2 = arrayListPoissonDiskSampling.size();
            this.visibleCount = size2;
            if (this.batchParticlesBuffer != null) {
                BatchParticlesDrawHelper.BatchParticlesBuffer batchParticlesBuffer = new BatchParticlesDrawHelper.BatchParticlesBuffer(size2);
                this.batchParticlesBuffer = batchParticlesBuffer;
                batchParticlesBuffer.fillParticleTextureCords(0.0f, 0.0f, this.f390b.getWidth(), this.f390b.getHeight());
            }
            long jCurrentTimeMillis = System.currentTimeMillis();
            for (int i2 = 0; i2 < this.visibleCount; i2++) {
                Particle particle = this.particles.get(i2);
                PointF pointF = arrayListPoissonDiskSampling.get(i2);
                gen(particle, jCurrentTimeMillis, true);
                float f = pointF.x;
                RectF rectF = this.bounds;
                particle.x = f + rectF.left;
                particle.y = pointF.y + rectF.top;
                particle.la = AndroidUtilities.lerp(0.4f, 1.0f, Utilities.fastRandom.nextFloat());
                particle.s *= 1.25f;
            }
        }

        public static boolean isValidPoint(PointF[][] pointFArr, int i, int i2, float f, int i3, int i4, PointF pointF, float f2) {
            int iDp = AndroidUtilities.dp(15.0f) / 2;
            float f3 = pointF.x;
            float f4 = iDp;
            if (f3 >= f4 && f3 < i - iDp) {
                float f5 = pointF.y;
                if (f5 >= f4 && f5 < i2 - iDp) {
                    int iFloor = (int) Math.floor(f3 / f);
                    int iFloor2 = (int) Math.floor(pointF.y / f);
                    int iMin = Math.min(iFloor + 1, i3 - 1);
                    int iMax = Math.max(iFloor2 - 1, 0);
                    int iMin2 = Math.min(iFloor2 + 1, i4 - 1);
                    for (int iMax2 = Math.max(iFloor - 1, 0); iMax2 <= iMin; iMax2++) {
                        for (int i5 = iMax; i5 <= iMin2; i5++) {
                            PointF pointF2 = pointFArr[iMax2][i5];
                            if (pointF2 != null && MathUtils.distance(pointF2.x, pointF2.y, pointF.x, pointF.y) < f2) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
            }
            return false;
        }

        public static void insertPoint(PointF[][] pointFArr, float f, PointF pointF) {
            pointFArr[(int) Math.floor(pointF.x / f)][(int) Math.floor(pointF.y / f)] = pointF;
        }

        private static ArrayList<PointF> poissonDiskSampling(float f, int i, int i2, int i3) {
            PointF[][] pointFArr;
            char c2;
            float f2 = f;
            ArrayList<PointF> arrayList = new ArrayList<>();
            ArrayList arrayList2 = new ArrayList();
            int i4 = 0;
            PointF pointF = new PointF(AndroidUtilities.lerp(0, i, Utilities.fastRandom.nextFloat()), AndroidUtilities.lerp(0, i2, Utilities.fastRandom.nextFloat()));
            float fFloor = (float) Math.floor(((double) f2) / Math.sqrt(2.0d));
            int i5 = 1;
            int iCeil = ((int) Math.ceil(i / fFloor)) + 1;
            int iCeil2 = ((int) Math.ceil(i2 / fFloor)) + 1;
            char c3 = 2;
            PointF[][] pointFArr2 = (PointF[][]) Array.newInstance((Class<?>) PointF.class, iCeil, iCeil2);
            for (int i6 = 0; i6 < iCeil; i6++) {
                for (int i7 = 0; i7 < iCeil2; i7++) {
                    pointFArr2[i6][i7] = null;
                }
            }
            insertPoint(pointFArr2, fFloor, pointF);
            arrayList.add(pointF);
            arrayList2.add(pointF);
            while (!arrayList2.isEmpty()) {
                int iNextInt = arrayList2.size() > i5 ? Utilities.fastRandom.nextInt(arrayList2.size() - i5) : i4;
                PointF pointF2 = (PointF) arrayList2.get(iNextInt);
                int i8 = i4;
                while (true) {
                    if (i8 < i3) {
                        float fLerp = AndroidUtilities.lerp(i4, 360, Utilities.fastRandom.nextFloat());
                        c2 = 2;
                        int i9 = i8;
                        double dLerp = AndroidUtilities.lerp(1, 2, Utilities.fastRandom.nextFloat()) * f2;
                        double d = fLerp;
                        PointF[][] pointFArr3 = pointFArr2;
                        PointF pointF3 = new PointF((float) (((double) pointF2.x) + (Math.cos(Math.toRadians(d)) * dLerp)), (float) (((double) pointF2.y) + (Math.sin(Math.toRadians(d)) * dLerp)));
                        pointFArr = pointFArr3;
                        if (isValidPoint(pointFArr, i, i2, fFloor, iCeil, iCeil2, pointF3, f2)) {
                            arrayList.add(pointF3);
                            insertPoint(pointFArr, fFloor, pointF3);
                            arrayList2.add(pointF3);
                            break;
                        }
                        f2 = f;
                        pointFArr2 = pointFArr;
                        i8 = i9 + 1;
                        c3 = 2;
                        i4 = 0;
                    } else {
                        pointFArr = pointFArr2;
                        c2 = c3;
                        arrayList2.remove(iNextInt);
                        break;
                    }
                }
                f2 = f;
                pointFArr2 = pointFArr;
                c3 = c2;
                i4 = 0;
                i5 = 1;
            }
            return arrayList;
        }

        public void draw(Canvas canvas, int i) {
            draw(canvas, i, 1.0f);
        }

        public void draw(Canvas canvas, int i, float f) {
            if (LiteMode.isEnabled(131072)) {
                int iMin = Math.min(this.visibleCount, this.particles.size());
                if (this.batchParticlesBuffer != null) {
                    float width = this.f390b.getWidth();
                    float height = this.f390b.getHeight();
                    for (int i2 = 0; i2 < iMin; i2++) {
                        Particle particle = this.particles.get(i2);
                        float f2 = particle.f391a * particle.s * f;
                        float f3 = (width / 2.0f) * f2;
                        float f4 = (height / 2.0f) * f2;
                        BatchParticlesDrawHelper.BatchParticlesBuffer batchParticlesBuffer = this.batchParticlesBuffer;
                        float f5 = particle.x;
                        float f6 = particle.y;
                        batchParticlesBuffer.setParticleVertexCords(i2, f5 - f3, f6 - f4, f5 + f3, f4 + f6);
                        this.batchParticlesBuffer.setParticleColor(i2, ColorUtils.setAlphaComponent(i, (int) (Utilities.clamp01(particle.la * f) * 255.0f)));
                    }
                    BatchParticlesDrawHelper.draw(canvas, this.batchParticlesBuffer, iMin, this.batchParticlesPaint);
                } else {
                    if (this.bPaintColor != i) {
                        Paint paint = this.bPaint;
                        this.bPaintColor = i;
                        paint.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
                    }
                    for (int i3 = 0; i3 < iMin; i3++) {
                        Particle particle2 = this.particles.get(i3);
                        particle2.draw(canvas, i, particle2.la * f);
                    }
                }
                this.firstDraw = false;
            }
        }

        public void gen(Particle particle, long j, boolean z) {
            particle.start = j;
            long jLerp = (long) (AndroidUtilities.lerp(MediaError.DetailedErrorCode.SEGMENT_UNKNOWN, 2500, Utilities.fastRandom.nextFloat()) * this.lifetime);
            particle.lifetime = jLerp;
            if (z) {
                particle.start -= (long) (jLerp * Utilities.clamp01(Utilities.fastRandom.nextFloat()));
            }
            RectF rectF = this.bounds;
            particle.x = AndroidUtilities.lerp(rectF.left, rectF.right, Utilities.fastRandom.nextFloat());
            RectF rectF2 = this.bounds;
            particle.y = AndroidUtilities.lerp(rectF2.top, rectF2.bottom, Utilities.fastRandom.nextFloat());
            if (this.type == 0) {
                particle.vx = AndroidUtilities.dp(AndroidUtilities.lerp(-7.0f, -18.0f, Utilities.fastRandom.nextFloat()));
                particle.vy = AndroidUtilities.dp(AndroidUtilities.lerp(-2.0f, 2.0f, Utilities.fastRandom.nextFloat()));
            } else {
                particle.vx = this.bounds.centerX() - particle.x;
                particle.vy = this.bounds.centerY() - particle.y;
                float fDp = AndroidUtilities.dp(AndroidUtilities.lerp(1.0f, 4.0f, Utilities.fastRandom.nextFloat()));
                float f = particle.vx;
                float f2 = particle.vy;
                float fSqrt = fDp / ((float) Math.sqrt((f * f) + (f2 * f2)));
                particle.vx *= fSqrt;
                particle.vy *= fSqrt;
            }
            particle.f391a = AndroidUtilities.lerp(0.4f, 1.0f, Utilities.fastRandom.nextFloat());
            particle.s = AndroidUtilities.lerp(0.8f, 1.2f, Utilities.fastRandom.nextFloat()) * 0.7f;
        }

        public class Particle {

            public float f391a;
            public float la;
            public long lifetime;
            public float s;
            public long start;
            public float vx;
            public float vy;
            public float x;
            public float y;

            public Particle() {
            }

            public void draw(Canvas canvas, int i, float f) {
                Particles.this.bPaint.setAlpha((int) (255.0f * f));
                Particles particles = Particles.this;
                particles.rect.set((int) (this.x - ((((particles.f390b.getWidth() / 2.0f) * this.f391a) * this.s) * f)), (int) (this.y - ((((Particles.this.f390b.getHeight() / 2.0f) * this.f391a) * this.s) * f)), (int) (this.x + ((Particles.this.f390b.getWidth() / 2.0f) * this.f391a * this.s * f)), (int) (this.y + ((Particles.this.f390b.getHeight() / 2.0f) * this.f391a * this.s * f)));
                Particles particles2 = Particles.this;
                canvas.drawBitmap(particles2.f390b, (Rect) null, particles2.rect, particles2.bPaint);
            }
        }
    }

    public static class SenderData {
        public boolean anonymous;
        public long did;
        public boolean my;
        public long stars;

        public static SenderData of(boolean z, boolean z2, long j, long j2) {
            SenderData senderData = new SenderData();
            senderData.anonymous = z;
            senderData.my = z2;
            senderData.did = j;
            senderData.stars = j2;
            return senderData;
        }
    }

    public class TopSendersView extends View {
        public final AnimatedFloat animatedCount;
        public final Paint backgroundPaint;
        private Utilities.Callback<Long> clickListener;
        public float count;
        public final boolean liveStories;
        public final ArrayList<Sender> oldSenders;
        private Sender pressedSender;
        public final ArrayList<Sender> senders;

        public TopSendersView(Context context, boolean z) {
            super(context);
            this.senders = new ArrayList<>();
            this.oldSenders = new ArrayList<>();
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            this.animatedCount = new AnimatedFloat(this, 0L, 320L, CubicBezierInterpolator.EASE_OUT_QUINT);
            this.liveStories = z;
            paint.setStyle(Paint.Style.FILL_AND_STROKE);
            paint.setStrokeWidth(AndroidUtilities.dp(3.0f));
            paint.setColor(Theme.getColor(Theme.key_dialogBackground, StarsReactionsSheet.this.resourcesProvider));
        }

        @Override // android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            for (int i = 0; i < this.senders.size(); i++) {
                this.senders.get(i).imageReceiver.onAttachedToWindow();
            }
        }

        @Override // android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            for (int i = 0; i < this.senders.size(); i++) {
                this.senders.get(i).imageReceiver.onDetachedFromWindow();
            }
        }

        @Override // android.view.View
        public void dispatchDraw(Canvas canvas) {
            this.count = this.animatedCount.set(this.senders.size());
            for (int i = 0; i < this.oldSenders.size(); i++) {
                this.oldSenders.get(i).draw(canvas);
            }
            for (int i2 = 0; i2 < this.senders.size(); i2++) {
                this.senders.get(i2).draw(canvas);
            }
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            Sender sender;
            Utilities.Callback<Long> callback;
            if (motionEvent.getAction() == 0) {
                Sender sender2 = this.pressedSender;
                if (sender2 != null) {
                    sender2.bounce.setPressed(false);
                }
                this.pressedSender = null;
                for (int i = 0; i < this.senders.size(); i++) {
                    if (this.senders.get(i).clickBounds.contains(motionEvent.getX(), motionEvent.getY())) {
                        this.pressedSender = this.senders.get(i);
                        break;
                    }
                }
                Sender sender3 = this.pressedSender;
                if (sender3 != null) {
                    sender3.bounce.setPressed(true);
                }
            } else if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                if (motionEvent.getAction() == 1 && (sender = this.pressedSender) != null && !sender.anonymous && sender.clickBounds.contains(motionEvent.getX(), motionEvent.getY()) && (callback = this.clickListener) != null) {
                    callback.run(Long.valueOf(this.pressedSender.did));
                }
                Sender sender4 = this.pressedSender;
                if (sender4 != null) {
                    sender4.bounce.setPressed(false);
                }
                this.pressedSender = null;
            }
            return this.pressedSender != null;
        }

        public void setMyPrivacy(long j) {
            for (int i = 0; i < this.senders.size(); i++) {
                Sender sender = this.senders.get(i);
                if (sender.my) {
                    sender.setPrivacy(j);
                    return;
                }
            }
        }

        public void setSenders(ArrayList<SenderData> arrayList) {
            Sender sender;
            int i = 0;
            while (true) {
                SenderData senderData = null;
                if (i >= this.senders.size()) {
                    break;
                }
                Sender sender2 = this.senders.get(i);
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    SenderData senderData2 = arrayList.get(i2);
                    boolean z = senderData2.my;
                    if ((z && sender2.my) || (!sender2.my && !z && senderData2.did == sender2.did)) {
                        senderData = arrayList.get(i2);
                        break;
                    }
                }
                if (senderData == null) {
                    sender2.imageReceiver.onDetachedFromWindow();
                    this.senders.remove(i);
                    i--;
                    sender2.index = -1;
                    this.oldSenders.add(sender2);
                }
                i++;
            }
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                SenderData senderData3 = arrayList.get(i3);
                int i4 = 0;
                while (true) {
                    if (i4 >= this.senders.size()) {
                        sender = null;
                        break;
                    }
                    Sender sender3 = this.senders.get(i4);
                    boolean z2 = sender3.my;
                    if ((z2 && senderData3.my) || (!z2 && !senderData3.my && sender3.did == senderData3.did)) {
                        sender = this.senders.get(i4);
                        break;
                    }
                    i4++;
                }
                if (sender == null) {
                    for (int i5 = 0; i5 < this.oldSenders.size(); i5++) {
                        Sender sender4 = this.oldSenders.get(i5);
                        boolean z3 = sender4.my;
                        if ((z3 && senderData3.my) || (!z3 && !senderData3.my && sender4.did == senderData3.did)) {
                            sender = this.oldSenders.get(i5);
                            break;
                        }
                    }
                    if (sender != null) {
                        this.oldSenders.remove(sender);
                        sender.imageReceiver.onAttachedToWindow();
                        this.senders.add(sender);
                    }
                }
                if (sender == null) {
                    sender = new Sender(senderData3.my, senderData3.did);
                    sender.animatedScale.set(0.0f, true);
                    this.senders.add(sender);
                    sender.animatedPosition.set((arrayList.size() - 1) - i3, true);
                }
                sender.index = (arrayList.size() - 1) - i3;
                sender.setStars(senderData3.stars);
                if (this.liveStories) {
                    sender.setPlace(i3 + 1);
                }
                if (senderData3.my) {
                    sender.setPrivacy(StarsReactionsSheet.this.peer);
                } else {
                    sender.setAnonymous(senderData3.anonymous);
                }
            }
            invalidate();
        }

        public void setOnSenderClickListener(Utilities.Callback<Long> callback) {
            this.clickListener = callback;
        }

        public class Sender {
            public final AnimatedFloat animatedAnonymous;
            public final AnimatedFloat animatedPosition;
            public final AnimatedFloat animatedScale;
            public boolean anonymous;
            public final AvatarDrawable anonymousAvatarDrawable;
            public final AvatarDrawable avatarDrawable;
            public final ButtonBounce bounce;
            public final RectF clickBounds = new RectF();
            private Drawable crown;
            private Drawable crownOutline;
            private int currentColor;
            public long did;
            public LinearGradient gradient;
            public Matrix gradientMatrix;
            public final ImageReceiver imageReceiver;
            public int index;
            public final boolean my;
            public final Paint paint;
            private int place;
            private Text placeText;
            public Text starsText;
            public Text text;

            public Sender(boolean z, long j) {
                String forcedFirstName;
                CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
                this.animatedPosition = new AnimatedFloat(TopSendersView.this, 0L, 600L, cubicBezierInterpolator);
                this.animatedScale = new AnimatedFloat(TopSendersView.this, 0L, 200L, cubicBezierInterpolator);
                this.animatedAnonymous = new AnimatedFloat(TopSendersView.this, 0L, 350L, cubicBezierInterpolator);
                this.gradient = null;
                this.gradientMatrix = new Matrix();
                this.paint = new Paint(1);
                ImageReceiver imageReceiver = new ImageReceiver(TopSendersView.this);
                this.imageReceiver = imageReceiver;
                AvatarDrawable avatarDrawable = new AvatarDrawable();
                this.avatarDrawable = avatarDrawable;
                AvatarDrawable avatarDrawable2 = new AvatarDrawable();
                this.anonymousAvatarDrawable = avatarDrawable2;
                this.bounce = new ButtonBounce(TopSendersView.this);
                this.my = z;
                this.did = j;
                if (j >= 0) {
                    TLRPC.User user = MessagesController.getInstance(StarsReactionsSheet.this.currentAccount).getUser(Long.valueOf(j));
                    forcedFirstName = UserObject.getForcedFirstName(user);
                    avatarDrawable.setInfo(user);
                    imageReceiver.setForUserOrChat(user, avatarDrawable);
                } else {
                    TLRPC.Chat chat = MessagesController.getInstance(StarsReactionsSheet.this.currentAccount).getChat(Long.valueOf(-j));
                    forcedFirstName = chat == null ? _UrlKt.FRAGMENT_ENCODE_SET : chat.title;
                    avatarDrawable.setInfo(chat);
                    imageReceiver.setForUserOrChat(chat, avatarDrawable);
                }
                imageReceiver.setRoundRadius(ExteraConfig.getAvatarCorners(56.0f));
                imageReceiver.onAttachedToWindow();
                imageReceiver.setCrossfadeWithOldImage(true);
                avatarDrawable2.setAvatarType(21);
                avatarDrawable2.setRoundRadius(ExteraConfig.getAvatarCorners(56.0f));
                avatarDrawable2.setColor(Theme.getColor(Theme.key_avatar_backgroundGray, StarsReactionsSheet.this.resourcesProvider));
                this.text = new Text(forcedFirstName, 12.0f);
            }

            private long getPrivacy() {
                if (this.anonymous) {
                    return UserObject.ANONYMOUS;
                }
                if (this.did == UserConfig.getInstance(StarsReactionsSheet.this.currentAccount).getClientUserId()) {
                    return 0L;
                }
                return this.did;
            }

            public void setAnonymous(boolean z) {
                String shortName;
                if (this.my || this.anonymous == z) {
                    return;
                }
                this.anonymous = z;
                if (z) {
                    shortName = LocaleController.getString(R.string.StarsReactionAnonymous);
                } else {
                    shortName = DialogObject.getShortName(this.did);
                }
                this.text = new Text(shortName, 12.0f);
                TopSendersView.this.invalidate();
            }

            public void setPrivacy(long j) {
                String forcedFirstName;
                String string;
                if (this.my && getPrivacy() != j) {
                    this.anonymous = j == UserObject.ANONYMOUS;
                    if (j == 0 || j == UserObject.ANONYMOUS) {
                        j = UserConfig.getInstance(StarsReactionsSheet.this.currentAccount).getClientUserId();
                    }
                    this.did = j;
                    if (this.anonymous) {
                        string = LocaleController.getString(R.string.StarsReactionAnonymous);
                    } else {
                        TopSendersView topSendersView = TopSendersView.this;
                        if (j >= 0) {
                            TLRPC.User user = MessagesController.getInstance(StarsReactionsSheet.this.currentAccount).getUser(Long.valueOf(this.did));
                            forcedFirstName = UserObject.getForcedFirstName(user);
                            this.avatarDrawable.setInfo(user);
                            this.imageReceiver.setForUserOrChat(user, this.avatarDrawable);
                        } else {
                            TLRPC.Chat chat = MessagesController.getInstance(StarsReactionsSheet.this.currentAccount).getChat(Long.valueOf(-this.did));
                            forcedFirstName = chat == null ? _UrlKt.FRAGMENT_ENCODE_SET : chat.title;
                            this.avatarDrawable.setInfo(chat);
                            this.imageReceiver.setForUserOrChat(chat, this.avatarDrawable);
                        }
                        string = forcedFirstName;
                    }
                    this.text = new Text(string, 12.0f);
                    TopSendersView.this.invalidate();
                }
            }

            public void setStars(long j) {
                this.starsText = new Text(StarsIntroActivity.replaceStars("⭐️" + LocaleController.formatNumber(j, ','), 0.85f), 12.0f, AndroidUtilities.getTypeface("fonts/num.otf"));
                if (TopSendersView.this.liveStories) {
                    int i = (int) j;
                    this.gradient = new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(16.0f), new int[]{HighlightMessageSheet.getTierOption(StarsReactionsSheet.this.currentAccount, i, HighlightMessageSheet.TIER_COLOR2), HighlightMessageSheet.getTierOption(StarsReactionsSheet.this.currentAccount, i, HighlightMessageSheet.TIER_COLOR1)}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
                    this.currentColor = ColorUtils.blendARGB(HighlightMessageSheet.getTierOption(StarsReactionsSheet.this.currentAccount, i, HighlightMessageSheet.TIER_COLOR2), HighlightMessageSheet.getTierOption(StarsReactionsSheet.this.currentAccount, i, HighlightMessageSheet.TIER_COLOR1), 0.5f);
                    this.paint.setShader(this.gradient);
                } else {
                    this.paint.setShader(null);
                    Paint paint = this.paint;
                    this.currentColor = -1002750;
                    paint.setColor(-1002750);
                }
                Drawable drawable = this.crown;
                if (drawable != null) {
                    drawable.setColorFilter(new PorterDuffColorFilter(this.currentColor, PorterDuff.Mode.SRC_IN));
                }
            }

            public void setPlace(int i) {
                this.place = i;
                this.placeText = new Text(_UrlKt.FRAGMENT_ENCODE_SET + i, 10.0f, AndroidUtilities.getTypeface("fonts/num.otf"));
                if (i <= 0 || this.crown != null) {
                    return;
                }
                Drawable drawableMutate = TopSendersView.this.getContext().getResources().getDrawable(R.drawable.filled_stream_crown).mutate();
                this.crown = drawableMutate;
                int i2 = this.currentColor;
                PorterDuff.Mode mode = PorterDuff.Mode.SRC_IN;
                drawableMutate.setColorFilter(new PorterDuffColorFilter(i2, mode));
                Drawable drawableMutate2 = TopSendersView.this.getContext().getResources().getDrawable(R.drawable.filled_stream_crown_outline).mutate();
                this.crownOutline = drawableMutate2;
                drawableMutate2.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground, StarsReactionsSheet.this.resourcesProvider), mode));
            }

            public void draw(Canvas canvas) {
                float f;
                float f2 = this.animatedPosition.set(this.index);
                AnimatedFloat animatedFloat = this.animatedScale;
                int i = this.index;
                float f3 = animatedFloat.set(i >= 0 && i < TopSendersView.this.senders.size());
                canvas.save();
                float width = (TopSendersView.this.getWidth() - AndroidUtilities.dp(80.0f)) / Math.max(1.0f, TopSendersView.this.count);
                float f4 = 40.0f;
                float fDp = AndroidUtilities.dp(40.0f) + ((TopSendersView.this.count - (f2 + 0.5f)) * width);
                float fDp2 = AndroidUtilities.dp(40.0f);
                float f5 = width / 2.0f;
                this.clickBounds.set(fDp - f5, fDp2 - AndroidUtilities.dp(50.0f), f5 + fDp, AndroidUtilities.dp(50.0f) + fDp2);
                float f6 = (0.3f * f3) + 0.7f;
                canvas.scale(f6, f6, fDp, fDp2);
                float scale = this.bounce.getScale(0.04f);
                canvas.scale(scale, scale, fDp, fDp2);
                if (f3 > 0.0f) {
                    float f7 = this.animatedAnonymous.set(this.anonymous);
                    if (f7 < 1.0f) {
                        f = 255.0f;
                        this.imageReceiver.setImageCoords(fDp - (AndroidUtilities.dp(56.0f) / 2.0f), fDp2 - (AndroidUtilities.dp(56.0f) / 2.0f), AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
                        this.imageReceiver.setAlpha(f3);
                        this.imageReceiver.draw(canvas);
                        this.imageReceiver.setAlpha(1.0f);
                    } else {
                        f = 255.0f;
                    }
                    if (f7 > 0.0f) {
                        int i2 = (int) fDp;
                        int i3 = (int) fDp2;
                        this.anonymousAvatarDrawable.setBounds(i2 - (AndroidUtilities.dp(56.0f) / 2), i3 - (AndroidUtilities.dp(56.0f) / 2), i2 + (AndroidUtilities.dp(56.0f) / 2), i3 + (AndroidUtilities.dp(56.0f) / 2));
                        this.anonymousAvatarDrawable.setAlpha((int) (f3 * f * f7));
                        this.anonymousAvatarDrawable.draw(canvas);
                        this.anonymousAvatarDrawable.setAlpha(255);
                    }
                } else {
                    f4 = 40.0f;
                    f = 255.0f;
                }
                RectF rectF = AndroidUtilities.rectTmp;
                rectF.set((fDp - (this.starsText.getCurrentWidth() / 2.0f)) - AndroidUtilities.dp(5.66f), (AndroidUtilities.dp(23.0f) + fDp2) - (AndroidUtilities.dp(16.0f) / 2.0f), (this.starsText.getCurrentWidth() / 2.0f) + fDp + AndroidUtilities.dp(5.66f), AndroidUtilities.dp(23.0f) + fDp2 + (AndroidUtilities.dp(16.0f) / 2.0f));
                canvas.drawRoundRect(rectF, rectF.height() / 2.0f, rectF.height() / 2.0f, TopSendersView.this.backgroundPaint);
                int i4 = (int) (f3 * f);
                this.paint.setAlpha(i4);
                if (this.gradient != null) {
                    this.gradientMatrix.reset();
                    this.gradientMatrix.postTranslate(0.0f, rectF.top);
                    this.gradient.setLocalMatrix(this.gradientMatrix);
                }
                canvas.drawRoundRect(rectF, rectF.height() / 2.0f, rectF.height() / 2.0f, this.paint);
                Text text = this.starsText;
                text.draw(canvas, fDp - (text.getCurrentWidth() / 2.0f), AndroidUtilities.dp(23.0f) + fDp2, -1, f3);
                this.text.ellipsize(width - AndroidUtilities.dp(4.0f)).draw(canvas, fDp - (this.text.getWidth() / 2.0f), fDp2 + AndroidUtilities.dp(42.0f), Theme.getColor(Theme.key_windowBackgroundWhiteBlackText, StarsReactionsSheet.this.resourcesProvider), f3);
                if (this.place > 0) {
                    int i5 = (int) fDp;
                    int i6 = (int) fDp2;
                    this.crownOutline.setBounds(i5 - AndroidUtilities.dp(12.0f), i6 - AndroidUtilities.dp(f4), AndroidUtilities.dp(12.0f) + i5, i6 - AndroidUtilities.dp(16.0f));
                    this.crown.setBounds(i5 - AndroidUtilities.dp(12.0f), i6 - AndroidUtilities.dp(f4), i5 + AndroidUtilities.dp(12.0f), i6 - AndroidUtilities.dp(16.0f));
                    this.crownOutline.setAlpha(i4);
                    this.crown.setAlpha(i4);
                    this.crownOutline.draw(canvas);
                    this.crown.draw(canvas);
                    Text text2 = this.placeText;
                    text2.draw(canvas, fDp - (text2.getCurrentWidth() / 2.0f), fDp2 - AndroidUtilities.dp(27.0f), -1, f3);
                }
                canvas.restore();
            }
        }
    }
}
