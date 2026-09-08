package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Interpolator;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.ExteraConfig;
import java.util.Random;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.voip.VoIPService;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.GroupCallUserCell;
import org.telegram.ui.Stories.StoriesGradientTools;

public class AvatarsDrawable {
    private boolean attached;
    boolean centered;
    public int count;
    int currentStyle;
    public boolean drawStoriesCircle;
    public int height;
    private boolean isInCall;
    public float maxX;
    private int overrideSize;
    View parent;
    private boolean showSavedMessages;
    StoriesGradientTools storiesTools;
    private boolean transitionInProgress;
    ValueAnimator transitionProgressAnimator;
    boolean updateAfterTransition;
    Runnable updateDelegate;
    boolean wasDraw;
    public int width;
    public DrawingState[] currentStates = new DrawingState[3];
    public DrawingState[] animatingStates = new DrawingState[3];
    float transitionProgress = 1.0f;
    private Paint paint = new Paint(1);
    private Paint xRefP = new Paint(1);
    public int strokeWidth = AndroidUtilities.dp(1.67f);
    private float overrideSizeStepFactor = 0.8f;
    private float overrideAlpha = 1.0f;
    public long transitionDuration = 220;
    public Interpolator transitionInterpolator = CubicBezierInterpolator.DEFAULT;
    Random random = new Random();

    public static class DrawingState {
        private int animationType;
        public AvatarDrawable avatarDrawable;
        private long id;
        private ImageReceiver imageReceiver;
        private long lastSpeakTime;
        private long lastUpdateTime;
        private int moveFromIndex;
        private TLObject object;
        TLRPC.GroupCallParticipant participant;
        private GroupCallUserCell.AvatarWavesDrawable wavesDrawable;
    }

    public void commitTransition(boolean z) {
        commitTransition(z, true);
    }

    public void setTransitionProgress(float f) {
        if (!this.transitionInProgress || this.transitionProgress == f) {
            return;
        }
        this.transitionProgress = f;
        if (f == 1.0f) {
            swapStates();
            this.transitionInProgress = false;
        }
    }

    public void commitTransition(boolean z, boolean z2) {
        if (!this.wasDraw || !z) {
            this.transitionProgress = 1.0f;
            swapStates();
            return;
        }
        DrawingState[] drawingStateArr = new DrawingState[3];
        boolean z3 = false;
        for (int i = 0; i < 3; i++) {
            DrawingState[] drawingStateArr2 = this.currentStates;
            drawingStateArr[i] = drawingStateArr2[i];
            if (drawingStateArr2[i].id != this.animatingStates[i].id) {
                z3 = true;
            } else {
                this.currentStates[i].lastSpeakTime = this.animatingStates[i].lastSpeakTime;
            }
        }
        if (!z3) {
            this.transitionProgress = 1.0f;
            return;
        }
        for (int i2 = 0; i2 < 3; i2++) {
            int i3 = 0;
            while (true) {
                if (i3 >= 3) {
                    this.animatingStates[i2].animationType = 0;
                    break;
                }
                if (this.currentStates[i3].id == this.animatingStates[i2].id) {
                    drawingStateArr[i3] = null;
                    DrawingState[] drawingStateArr3 = this.animatingStates;
                    if (i2 == i3) {
                        drawingStateArr3[i2].animationType = -1;
                        GroupCallUserCell.AvatarWavesDrawable avatarWavesDrawable = this.animatingStates[i2].wavesDrawable;
                        this.animatingStates[i2].wavesDrawable = this.currentStates[i2].wavesDrawable;
                        this.currentStates[i2].wavesDrawable = avatarWavesDrawable;
                        break;
                    }
                    drawingStateArr3[i2].animationType = 2;
                    this.animatingStates[i2].moveFromIndex = i3;
                    break;
                }
                i3++;
            }
        }
        for (int i4 = 0; i4 < 3; i4++) {
            DrawingState drawingState = drawingStateArr[i4];
            if (drawingState != null) {
                drawingState.animationType = 1;
            }
        }
        ValueAnimator valueAnimator = this.transitionProgressAnimator;
        if (valueAnimator != null) {
            valueAnimator.removeAllListeners();
            this.transitionProgressAnimator.cancel();
            if (this.transitionInProgress) {
                swapStates();
                this.transitionInProgress = false;
            }
        }
        this.transitionProgress = 0.0f;
        if (z2) {
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.transitionProgressAnimator = valueAnimatorOfFloat;
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.AvatarsDrawable$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    this.f$0.lambda$commitTransition$0(valueAnimator2);
                }
            });
            this.transitionProgressAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.AvatarsDrawable.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    AvatarsDrawable avatarsDrawable = AvatarsDrawable.this;
                    if (avatarsDrawable.transitionProgressAnimator != null) {
                        avatarsDrawable.transitionProgress = 1.0f;
                        avatarsDrawable.swapStates();
                        AvatarsDrawable avatarsDrawable2 = AvatarsDrawable.this;
                        if (avatarsDrawable2.updateAfterTransition) {
                            avatarsDrawable2.updateAfterTransition = false;
                            Runnable runnable = avatarsDrawable2.updateDelegate;
                            if (runnable != null) {
                                runnable.run();
                            }
                        }
                        AvatarsDrawable.this.invalidate();
                    }
                    AvatarsDrawable.this.transitionProgressAnimator = null;
                }
            });
            this.transitionProgressAnimator.setDuration(this.transitionDuration);
            this.transitionProgressAnimator.setInterpolator(CubicBezierInterpolator.DEFAULT);
            this.transitionProgressAnimator.start();
        } else {
            this.transitionInProgress = true;
        }
        invalidate();
    }

    public /* JADX WARN: Code duplicated, block: B:205:0x035c  */
    /* JADX WARN: Code duplicated, block: B:207:0x0364  */
    /* JADX WARN: Code duplicated, block: B:209:0x0381  */
    /* JADX WARN: Code duplicated, block: B:211:0x0389  */
    /* JADX WARN: Code duplicated, block: B:212:0x039c  */
    /* JADX WARN: Code duplicated, block: B:214:0x03a4  */
    /* JADX WARN: Code duplicated, block: B:216:0x03a8  */
    /* JADX WARN: Code duplicated, block: B:218:0x03af  */
    /* JADX WARN: Code duplicated, block: B:219:0x03b2  */
    /* JADX WARN: Code duplicated, block: B:221:0x03bb  */
    /* JADX WARN: Code duplicated, block: B:223:0x03d8  */
    /* JADX WARN: Code duplicated, block: B:224:0x03da  */
    /* JADX WARN: Code duplicated, block: B:230:0x03ee  */
    /* JADX WARN: Code duplicated, block: B:231:0x03f1  */
    /* JADX WARN: Code duplicated, block: B:240:0x0428  */
    /* JADX WARN: Code duplicated, block: B:242:0x0434  */
    /* JADX WARN: Code duplicated, block: B:287:0x065b A[ADDED_TO_REGION, REMOVE] */
    /* JADX WARN: Code duplicated, block: B:290:0x06b1  */
    /* JADX WARN: Code duplicated, block: B:292:0x06b6  */
    /* JADX WARN: Code duplicated, block: B:293:0x06cb  */
    /* JADX WARN: Code duplicated, block: B:296:0x06e2  */
    /* JADX WARN: Code duplicated, block: B:299:0x06fe  */
    /* JADX WARN: Code duplicated, block: B:302:0x0720  */
    /* JADX WARN: Code duplicated, block: B:305:0x0730  */
    /* JADX WARN: Code duplicated, block: B:310:0x0754  */
    /* JADX WARN: Code duplicated, block: B:314:0x077b  */
    /* JADX WARN: Code duplicated, block: B:315:0x0790  */
    /* JADX WARN: Code duplicated, block: B:318:0x07a9  */
    /* JADX WARN: Code duplicated, block: B:339:0x07ac A[SYNTHETIC] */
    public void onDraw(Canvas canvas) {
        int iDp;
        Canvas canvas2;
        int i;
        float f;
        char c2;
        byte b2;
        float f2;
        Object[] objArr;
        float f3;
        int i2;
        char c3;
        DrawingState drawingState;
        TLRPC.GroupCallParticipant groupCallParticipant;
        boolean z;
        float avatarScale;
        char c4;
        ImageReceiver imageReceiver;
        float f4;
        int iDp2;
        float f5;
        float f6;
        boolean z2;
        boolean z3 = true;
        this.wasDraw = true;
        int i3 = this.currentStyle;
        int i4 = 10;
        int i5 = 4;
        boolean z4 = i3 == 4 || i3 == 10;
        int size = getSize();
        int i6 = 11;
        if (this.currentStyle == 11) {
            iDp = AndroidUtilities.dp(12.0f);
        } else {
            int i7 = this.overrideSize;
            if (i7 != 0) {
                iDp = (int) (i7 * this.overrideSizeStepFactor);
            } else {
                iDp = AndroidUtilities.dp(z4 ? 24.0f : 20.0f);
            }
        }
        int i8 = iDp;
        for (int i9 = 0; i9 < 3; i9++) {
            long unused = this.currentStates[i9].id;
        }
        int i10 = this.currentStyle;
        int iDp3 = (i10 == 0 || i10 == 10 || i10 == 11) ? 0 : AndroidUtilities.dp(10.0f);
        int usedWidth = this.centered ? (this.width - ((int) getUsedWidth())) / 2 : iDp3;
        boolean z5 = VoIPService.getSharedInstance() != null && VoIPService.getSharedInstance().isMicMute();
        int i11 = this.currentStyle;
        if (i11 == 4) {
            this.paint.setColor(Theme.getColor(Theme.key_inappPlayerBackground));
        } else if (i11 != 3) {
            this.paint.setColor(Theme.getColor(z5 ? Theme.key_returnToCallMutedBackground : Theme.key_returnToCallBackground));
        }
        int i12 = 0;
        for (int i13 = 0; i13 < 3; i13++) {
            if (this.animatingStates[i13].id != 0) {
                i12++;
            }
        }
        int i14 = this.currentStyle;
        boolean z6 = i14 == 0 || i14 == 1 || i14 == 3 || i14 == 4 || i14 == 5 || i14 == 10 || i14 == 11;
        if (z6) {
            float fDp = i14 == 10 ? AndroidUtilities.dp(16.0f) : 0.0f;
            if (this.drawStoriesCircle) {
                fDp += AndroidUtilities.dp(20.0f);
            }
            float f7 = -fDp;
            float f8 = this.width + fDp;
            float f9 = this.height + fDp;
            canvas2 = canvas;
            f = 0.0f;
            i = 2;
            canvas2.saveLayerAlpha(f7, f7, f8, f9, 255, 31);
        } else {
            canvas2 = canvas;
            i = 2;
            f = 0.0f;
        }
        this.maxX = f;
        int i15 = -1;
        if (this.drawStoriesCircle) {
            int i16 = i;
            while (i16 >= 0) {
                int i17 = 0;
                while (i17 < i) {
                    if (i17 != 0 || this.transitionProgress != 1.0f) {
                        DrawingState[] drawingStateArr = i17 == 0 ? this.animatingStates : this.currentStates;
                        if (i17 != 1 || this.transitionProgress == 1.0f || drawingStateArr[i16].animationType == 1) {
                            ImageReceiver imageReceiver2 = drawingStateArr[i16].imageReceiver;
                            if (imageReceiver2.hasImageSet()) {
                                if (i17 == 0) {
                                    imageReceiver2.setImageX((this.centered ? ((this.width - (i12 * i8)) - AndroidUtilities.dp(z4 ? 8.0f : 4.0f)) / i : iDp3) + (i8 * i16));
                                } else {
                                    imageReceiver2.setImageX(usedWidth + (i8 * i16));
                                }
                                int i18 = this.currentStyle;
                                if (i18 == 0 || i18 == i4 || i18 == i6) {
                                    imageReceiver2.setImageY((this.height - size) / 2.0f);
                                } else {
                                    imageReceiver2.setImageY(AndroidUtilities.dp(i18 == i5 ? 8.0f : 6.0f));
                                }
                                if (this.transitionProgress == 1.0f) {
                                    f6 = 1.0f;
                                    z2 = false;
                                } else {
                                    if (drawingStateArr[i16].animationType == 1) {
                                        canvas2.save();
                                        float f10 = this.transitionProgress;
                                        canvas2.scale(1.0f - f10, 1.0f - f10, imageReceiver2.getCenterX(), imageReceiver2.getCenterY());
                                        f6 = 1.0f - this.transitionProgress;
                                    } else if (drawingStateArr[i16].animationType == 0) {
                                        canvas2.save();
                                        float f11 = this.transitionProgress;
                                        canvas2.scale(f11, f11, imageReceiver2.getCenterX(), imageReceiver2.getCenterY());
                                        f6 = this.transitionProgress;
                                    } else {
                                        if (drawingStateArr[i16].animationType == i) {
                                            int iDp4 = this.centered ? ((this.width - (i12 * i8)) - AndroidUtilities.dp(z4 ? 8.0f : 4.0f)) / i : iDp3;
                                            int i19 = usedWidth + (drawingStateArr[i16].moveFromIndex * i8);
                                            float f12 = this.transitionProgress;
                                            imageReceiver2.setImageX((int) (((iDp4 + (i8 * i16)) * f12) + (i19 * (1.0f - f12))));
                                        } else if (drawingStateArr[i16].animationType == i15 && this.centered) {
                                            int iDp5 = ((this.width - (i12 * i8)) - AndroidUtilities.dp(z4 ? 8.0f : 4.0f)) / i;
                                            int i20 = i8 * i16;
                                            float f13 = this.transitionProgress;
                                            imageReceiver2.setImageX((int) (((iDp5 + i20) * f13) + ((usedWidth + i20) * (1.0f - f13))));
                                        }
                                        f6 = 1.0f;
                                        z2 = false;
                                    }
                                    z2 = true;
                                }
                                float f14 = f6 * this.overrideAlpha;
                                float size2 = (getSize() / 2.0f) + AndroidUtilities.dp(4.0f);
                                if (this.storiesTools == null) {
                                    this.storiesTools = new StoriesGradientTools();
                                }
                                this.storiesTools.setBounds(f, f, this.parent.getMeasuredHeight(), AndroidUtilities.dp(40.0f));
                                this.storiesTools.paint.setAlpha((int) (f14 * 255.0f));
                                canvas2.drawCircle(imageReceiver2.getCenterX(), imageReceiver2.getCenterY(), size2, this.storiesTools.paint);
                                if (z2) {
                                    canvas2.restore();
                                }
                            }
                        }
                    }
                    i17++;
                    i15 = -1;
                    i4 = 10;
                    i5 = 4;
                    i6 = 11;
                }
                i16--;
                i15 = -1;
                i4 = 10;
                i5 = 4;
                i6 = 11;
            }
        }
        int i21 = i;
        while (i21 >= 0) {
            int i22 = 0;
            while (i22 < i) {
                if (i22 == 0 && this.transitionProgress == 1.0f) {
                    f = f;
                    z = z3 ? 1 : 0;
                } else {
                    DrawingState[] drawingStateArr2 = i22 == 0 ? this.animatingStates : this.currentStates;
                    if (i22 != z3 || this.transitionProgress == 1.0f || drawingStateArr2[i21].animationType == z3) {
                        ImageReceiver imageReceiver3 = drawingStateArr2[i21].imageReceiver;
                        if (imageReceiver3.hasImageSet()) {
                            if (i22 == 0) {
                                imageReceiver3.setImageX((this.centered ? ((this.width - (i12 * i8)) - AndroidUtilities.dp(z4 ? 8.0f : 4.0f)) / i : iDp3) + (i8 * i21));
                            } else {
                                imageReceiver3.setImageX(usedWidth + (i8 * i21));
                            }
                            int i23 = this.currentStyle;
                            if (i23 == 0 || i23 == 10) {
                                c2 = 11;
                            } else {
                                c2 = 11;
                                if (i23 != 11) {
                                    imageReceiver3.setImageY(AndroidUtilities.dp(i23 == 4 ? 8.0f : 6.0f));
                                }
                                if (this.transitionProgress == 1.0f) {
                                    b2 = -1;
                                    f2 = 1.0f;
                                    objArr = null;
                                } else {
                                    if (drawingStateArr2[i21].animationType == z3) {
                                        canvas2.save();
                                        float f15 = this.transitionProgress;
                                        canvas2.scale(1.0f - f15, 1.0f - f15, imageReceiver3.getCenterX(), imageReceiver3.getCenterY());
                                        f2 = 1.0f - this.transitionProgress;
                                    } else if (drawingStateArr2[i21].animationType == 0) {
                                        canvas2.save();
                                        float f16 = this.transitionProgress;
                                        canvas2.scale(f16, f16, imageReceiver3.getCenterX(), imageReceiver3.getCenterY());
                                        f2 = this.transitionProgress;
                                    } else {
                                        if (drawingStateArr2[i21].animationType == i) {
                                            if (this.centered) {
                                                int i24 = this.width - (i12 * i8);
                                                if (z4) {
                                                    f5 = 8.0f;
                                                } else {
                                                    f5 = 4.0f;
                                                }
                                                iDp2 = (i24 - AndroidUtilities.dp(f5)) / i;
                                            } else {
                                                iDp2 = iDp3;
                                            }
                                            int i25 = usedWidth + (drawingStateArr2[i21].moveFromIndex * i8);
                                            float f17 = this.transitionProgress;
                                            imageReceiver3.setImageX((int) (((iDp2 + (i8 * i21)) * f17) + (i25 * (1.0f - f17))));
                                            b2 = -1;
                                        } else {
                                            b2 = -1;
                                            if (drawingStateArr2[i21].animationType == -1 && this.centered) {
                                                int i26 = this.width - (i12 * i8);
                                                if (z4) {
                                                    f4 = 8.0f;
                                                } else {
                                                    f4 = 4.0f;
                                                }
                                                int iDp6 = (i26 - AndroidUtilities.dp(f4)) / i;
                                                int i27 = i8 * i21;
                                                float f18 = this.transitionProgress;
                                                imageReceiver3.setImageX((int) (((iDp6 + i27) * f18) + ((usedWidth + i27) * (1.0f - f18))));
                                            }
                                        }
                                        f2 = 1.0f;
                                        objArr = null;
                                    }
                                    objArr = z3 ? 1 : 0;
                                    b2 = -1;
                                }
                                f3 = f2 * this.overrideAlpha;
                                if (i21 == drawingStateArr2.length - (z3 ? 1 : 0) || this.drawStoriesCircle) {
                                    i2 = this.currentStyle;
                                    if (i2 != z3 || i2 == 3 || i2 == 5) {
                                        c3 = '\n';
                                        canvas2.drawRoundRect(imageReceiver3.getCenterX() + AndroidUtilities.dp(13.0f), imageReceiver3.getCenterY() - AndroidUtilities.dp(13.0f), imageReceiver3.getCenterX() - AndroidUtilities.dp(13.0f), imageReceiver3.getCenterY() + AndroidUtilities.dp(13.0f), ExteraConfig.getAvatarCorners(26.0f), ExteraConfig.getAvatarCorners(26.0f), this.xRefP);
                                        if (drawingStateArr2[i21].wavesDrawable == null) {
                                            if (this.currentStyle == 5) {
                                                drawingStateArr2[i21].wavesDrawable = new GroupCallUserCell.AvatarWavesDrawable(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(16.0f));
                                            } else {
                                                drawingStateArr2[i21].wavesDrawable = new GroupCallUserCell.AvatarWavesDrawable(AndroidUtilities.dp(17.0f), AndroidUtilities.dp(21.0f));
                                            }
                                        }
                                        if (this.currentStyle == 5) {
                                            drawingStateArr2[i21].wavesDrawable.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_speakingText), (int) (f3 * 76.5f)));
                                        }
                                        drawingState = drawingStateArr2[i21];
                                        groupCallParticipant = drawingState.participant;
                                        if (groupCallParticipant == null && groupCallParticipant.amplitude > f) {
                                            z = true;
                                            drawingState.wavesDrawable.setShowWaves(true, this.parent);
                                            DrawingState drawingState2 = drawingStateArr2[i21];
                                            drawingState2.wavesDrawable.setAmplitude(drawingState2.participant.amplitude * 15.0f);
                                        } else {
                                            z = true;
                                            drawingState.wavesDrawable.setShowWaves(false, this.parent);
                                        }
                                        if (this.currentStyle == 5 && SystemClock.uptimeMillis() - drawingStateArr2[i21].participant.lastSpeakTime > 500) {
                                            this.updateDelegate.run();
                                        }
                                        drawingStateArr2[i21].wavesDrawable.update();
                                        if (this.currentStyle == 5) {
                                            drawingStateArr2[i21].wavesDrawable.draw(canvas2, imageReceiver3.getCenterX(), imageReceiver3.getCenterY(), this.parent);
                                            invalidate();
                                        }
                                        avatarScale = drawingStateArr2[i21].wavesDrawable.getAvatarScale();
                                    } else if (i2 == 4 || i2 == 10) {
                                        imageReceiver3 = imageReceiver3;
                                        f3 = f3;
                                        f = f;
                                        canvas2 = canvas;
                                        canvas2.drawRoundRect(AndroidUtilities.dp(17.0f) + imageReceiver3.getCenterX(), imageReceiver3.getCenterY() - AndroidUtilities.dp(17.0f), imageReceiver3.getCenterX() - AndroidUtilities.dp(17.0f), AndroidUtilities.dp(17.0f) + imageReceiver3.getCenterY(), ExteraConfig.getAvatarCorners(34.0f), ExteraConfig.getAvatarCorners(34.0f), this.xRefP);
                                        if (drawingStateArr2[i21].wavesDrawable == null) {
                                            drawingStateArr2[i21].wavesDrawable = new GroupCallUserCell.AvatarWavesDrawable(AndroidUtilities.dp(17.0f), AndroidUtilities.dp(21.0f));
                                        }
                                        if (this.currentStyle == 10) {
                                            drawingStateArr2[i21].wavesDrawable.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_speakingText), (int) (f3 * 76.5f)));
                                        } else {
                                            drawingStateArr2[i21].wavesDrawable.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_listeningText), (int) (f3 * 76.5f)));
                                        }
                                        long jCurrentTimeMillis = System.currentTimeMillis();
                                        if (jCurrentTimeMillis - drawingStateArr2[i21].lastUpdateTime > 100) {
                                            drawingStateArr2[i21].lastUpdateTime = jCurrentTimeMillis;
                                            c4 = '\n';
                                            if (this.currentStyle == 10) {
                                                DrawingState drawingState3 = drawingStateArr2[i21];
                                                TLRPC.GroupCallParticipant groupCallParticipant2 = drawingState3.participant;
                                                if (groupCallParticipant2 != null && groupCallParticipant2.amplitude > f) {
                                                    drawingState3.wavesDrawable.setShowWaves(true, this.parent);
                                                    DrawingState drawingState4 = drawingStateArr2[i21];
                                                    drawingState4.wavesDrawable.setAmplitude(drawingState4.participant.amplitude * 15.0f);
                                                } else {
                                                    drawingState3.wavesDrawable.setShowWaves(false, this.parent);
                                                }
                                            } else if (((long) ConnectionsManager.getInstance(UserConfig.selectedAccount).getCurrentTime()) - drawingStateArr2[i21].lastSpeakTime <= 5) {
                                                drawingStateArr2[i21].wavesDrawable.setShowWaves(true, this.parent);
                                                drawingStateArr2[i21].wavesDrawable.setAmplitude(this.random.nextInt() % 100);
                                            } else {
                                                drawingStateArr2[i21].wavesDrawable.setShowWaves(false, this.parent);
                                                drawingStateArr2[i21].wavesDrawable.setAmplitude(0.0d);
                                            }
                                        } else {
                                            c4 = '\n';
                                        }
                                        drawingStateArr2[i21].wavesDrawable.update();
                                        drawingStateArr2[i21].wavesDrawable.draw(canvas2, imageReceiver3.getCenterX(), imageReceiver3.getCenterY(), this.parent);
                                        avatarScale = drawingStateArr2[i21].wavesDrawable.getAvatarScale();
                                        z = true;
                                    } else {
                                        float size3 = (getSize() / 2.0f) + this.strokeWidth;
                                        if (z6) {
                                            float centerX = imageReceiver3.getCenterX() + size3;
                                            float centerY = imageReceiver3.getCenterY() - size3;
                                            float centerX2 = imageReceiver3.getCenterX() - size3;
                                            float centerY2 = imageReceiver3.getCenterY() + size3;
                                            float f19 = size3 * 2.0f;
                                            f3 = f3;
                                            f = f;
                                            imageReceiver = imageReceiver3;
                                            canvas2.drawRoundRect(centerX, centerY, centerX2, centerY2, ExteraConfig.getAvatarCorners(f19, z3), ExteraConfig.getAvatarCorners(f19, z3), this.xRefP);
                                        } else {
                                            imageReceiver = imageReceiver3;
                                            f3 = f3;
                                            f = f;
                                            int alpha = this.paint.getAlpha();
                                            if (f3 != 1.0f) {
                                                this.paint.setAlpha((int) (alpha * f3));
                                            }
                                            float centerX3 = imageReceiver.getCenterX() + size3;
                                            float centerY3 = imageReceiver.getCenterY() - size3;
                                            float centerX4 = imageReceiver.getCenterX() - size3;
                                            float centerY4 = imageReceiver.getCenterY() + size3;
                                            float f20 = size3 * 2.0f;
                                            canvas.drawRoundRect(centerX3, centerY3, centerX4, centerY4, ExteraConfig.getAvatarCorners(f20, z3), ExteraConfig.getAvatarCorners(f20, z3), this.xRefP);
                                            if (f3 != 1.0f) {
                                                this.paint.setAlpha(alpha);
                                            }
                                        }
                                    }
                                    imageReceiver3.setAlpha(f3);
                                    if (avatarScale != 1.0f) {
                                        canvas2.save();
                                        canvas2.scale(avatarScale, avatarScale, imageReceiver3.getCenterX(), imageReceiver3.getCenterY());
                                        imageReceiver3.draw(canvas2);
                                        canvas2.restore();
                                    } else {
                                        imageReceiver3.draw(canvas2);
                                    }
                                    this.maxX = Math.max(this.maxX, imageReceiver3.getCenterX() + ((imageReceiver3.getImageWidth() / 2.0f) * avatarScale));
                                    if (objArr != null) {
                                        canvas2.restore();
                                    }
                                } else {
                                    imageReceiver = imageReceiver3;
                                    f3 = f3;
                                    f = f;
                                }
                                canvas2 = canvas;
                                imageReceiver3 = imageReceiver;
                                avatarScale = 1.0f;
                                z = true;
                                imageReceiver3.setAlpha(f3);
                                if (avatarScale != 1.0f) {
                                    canvas2.save();
                                    canvas2.scale(avatarScale, avatarScale, imageReceiver3.getCenterX(), imageReceiver3.getCenterY());
                                    imageReceiver3.draw(canvas2);
                                    canvas2.restore();
                                } else {
                                    imageReceiver3.draw(canvas2);
                                }
                                this.maxX = Math.max(this.maxX, imageReceiver3.getCenterX() + ((imageReceiver3.getImageWidth() / 2.0f) * avatarScale));
                                if (objArr != null) {
                                    canvas2.restore();
                                }
                            }
                            imageReceiver3.setImageY((this.height - size) / 2.0f);
                            if (this.transitionProgress == 1.0f) {
                                b2 = -1;
                                f2 = 1.0f;
                                objArr = null;
                            } else {
                                if (drawingStateArr2[i21].animationType == z3) {
                                    canvas2.save();
                                    float f110 = this.transitionProgress;
                                    canvas2.scale(1.0f - f110, 1.0f - f110, imageReceiver3.getCenterX(), imageReceiver3.getCenterY());
                                    f2 = 1.0f - this.transitionProgress;
                                } else if (drawingStateArr2[i21].animationType == 0) {
                                    canvas2.save();
                                    float f111 = this.transitionProgress;
                                    canvas2.scale(f111, f111, imageReceiver3.getCenterX(), imageReceiver3.getCenterY());
                                    f2 = this.transitionProgress;
                                } else {
                                    if (drawingStateArr2[i21].animationType == i) {
                                        if (this.centered) {
                                            int i28 = this.width - (i12 * i8);
                                            if (z4) {
                                                f5 = 8.0f;
                                            } else {
                                                f5 = 4.0f;
                                            }
                                            iDp2 = (i28 - AndroidUtilities.dp(f5)) / i;
                                        } else {
                                            iDp2 = iDp3;
                                        }
                                        int i29 = usedWidth + (drawingStateArr2[i21].moveFromIndex * i8);
                                        float f112 = this.transitionProgress;
                                        imageReceiver3.setImageX((int) (((iDp2 + (i8 * i21)) * f112) + (i29 * (1.0f - f112))));
                                        b2 = -1;
                                    } else {
                                        b2 = -1;
                                        if (drawingStateArr2[i21].animationType == -1) {
                                            int i210 = this.width - (i12 * i8);
                                            if (z4) {
                                                f4 = 8.0f;
                                            } else {
                                                f4 = 4.0f;
                                            }
                                            int iDp7 = (i210 - AndroidUtilities.dp(f4)) / i;
                                            int i211 = i8 * i21;
                                            float f113 = this.transitionProgress;
                                            imageReceiver3.setImageX((int) (((iDp7 + i211) * f113) + ((usedWidth + i211) * (1.0f - f113))));
                                        }
                                    }
                                    f2 = 1.0f;
                                    objArr = null;
                                }
                                objArr = z3 ? 1 : 0;
                                b2 = -1;
                            }
                            f3 = f2 * this.overrideAlpha;
                            if (i21 == drawingStateArr2.length - (z3 ? 1 : 0)) {
                                i2 = this.currentStyle;
                                if (i2 != z3) {
                                    c3 = '\n';
                                } else {
                                    c3 = '\n';
                                }
                                canvas2.drawRoundRect(imageReceiver3.getCenterX() + AndroidUtilities.dp(13.0f), imageReceiver3.getCenterY() - AndroidUtilities.dp(13.0f), imageReceiver3.getCenterX() - AndroidUtilities.dp(13.0f), imageReceiver3.getCenterY() + AndroidUtilities.dp(13.0f), ExteraConfig.getAvatarCorners(26.0f), ExteraConfig.getAvatarCorners(26.0f), this.xRefP);
                                if (drawingStateArr2[i21].wavesDrawable == null) {
                                    if (this.currentStyle == 5) {
                                        drawingStateArr2[i21].wavesDrawable = new GroupCallUserCell.AvatarWavesDrawable(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(16.0f));
                                    } else {
                                        drawingStateArr2[i21].wavesDrawable = new GroupCallUserCell.AvatarWavesDrawable(AndroidUtilities.dp(17.0f), AndroidUtilities.dp(21.0f));
                                    }
                                }
                                if (this.currentStyle == 5) {
                                    drawingStateArr2[i21].wavesDrawable.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_speakingText), (int) (f3 * 76.5f)));
                                }
                                drawingState = drawingStateArr2[i21];
                                groupCallParticipant = drawingState.participant;
                                if (groupCallParticipant == null) {
                                    z = true;
                                    drawingState.wavesDrawable.setShowWaves(false, this.parent);
                                } else {
                                    z = true;
                                    drawingState.wavesDrawable.setShowWaves(false, this.parent);
                                }
                                if (this.currentStyle == 5) {
                                    this.updateDelegate.run();
                                }
                                drawingStateArr2[i21].wavesDrawable.update();
                                if (this.currentStyle == 5) {
                                    drawingStateArr2[i21].wavesDrawable.draw(canvas2, imageReceiver3.getCenterX(), imageReceiver3.getCenterY(), this.parent);
                                    invalidate();
                                }
                                avatarScale = drawingStateArr2[i21].wavesDrawable.getAvatarScale();
                            } else {
                                i2 = this.currentStyle;
                                if (i2 != z3) {
                                    c3 = '\n';
                                } else {
                                    c3 = '\n';
                                }
                                canvas2.drawRoundRect(imageReceiver3.getCenterX() + AndroidUtilities.dp(13.0f), imageReceiver3.getCenterY() - AndroidUtilities.dp(13.0f), imageReceiver3.getCenterX() - AndroidUtilities.dp(13.0f), imageReceiver3.getCenterY() + AndroidUtilities.dp(13.0f), ExteraConfig.getAvatarCorners(26.0f), ExteraConfig.getAvatarCorners(26.0f), this.xRefP);
                                if (drawingStateArr2[i21].wavesDrawable == null) {
                                    if (this.currentStyle == 5) {
                                        drawingStateArr2[i21].wavesDrawable = new GroupCallUserCell.AvatarWavesDrawable(AndroidUtilities.dp(14.0f), AndroidUtilities.dp(16.0f));
                                    } else {
                                        drawingStateArr2[i21].wavesDrawable = new GroupCallUserCell.AvatarWavesDrawable(AndroidUtilities.dp(17.0f), AndroidUtilities.dp(21.0f));
                                    }
                                }
                                if (this.currentStyle == 5) {
                                    drawingStateArr2[i21].wavesDrawable.setColor(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_voipgroup_speakingText), (int) (f3 * 76.5f)));
                                }
                                drawingState = drawingStateArr2[i21];
                                groupCallParticipant = drawingState.participant;
                                if (groupCallParticipant == null) {
                                    z = true;
                                    drawingState.wavesDrawable.setShowWaves(false, this.parent);
                                } else {
                                    z = true;
                                    drawingState.wavesDrawable.setShowWaves(false, this.parent);
                                }
                                if (this.currentStyle == 5) {
                                    this.updateDelegate.run();
                                }
                                drawingStateArr2[i21].wavesDrawable.update();
                                if (this.currentStyle == 5) {
                                    drawingStateArr2[i21].wavesDrawable.draw(canvas2, imageReceiver3.getCenterX(), imageReceiver3.getCenterY(), this.parent);
                                    invalidate();
                                }
                                avatarScale = drawingStateArr2[i21].wavesDrawable.getAvatarScale();
                            }
                            imageReceiver3.setAlpha(f3);
                            if (avatarScale != 1.0f) {
                                canvas2.save();
                                canvas2.scale(avatarScale, avatarScale, imageReceiver3.getCenterX(), imageReceiver3.getCenterY());
                                imageReceiver3.draw(canvas2);
                                canvas2.restore();
                            } else {
                                imageReceiver3.draw(canvas2);
                            }
                            this.maxX = Math.max(this.maxX, imageReceiver3.getCenterX() + ((imageReceiver3.getImageWidth() / 2.0f) * avatarScale));
                            if (objArr != null) {
                                canvas2.restore();
                            }
                        } else {
                            f = f;
                            z = z3 ? 1 : 0;
                        }
                    } else {
                        f = f;
                        z = z3 ? 1 : 0;
                    }
                }
                i22++;
                z3 = z;
                f = f;
                i = 2;
            }
            Object[] objArr2 = z3 ? 1 : 0;
            i21--;
            i = 2;
        }
        if (z6) {
            canvas2.restore();
        }
    }

    public float getMaxX() {
        return this.maxX;
    }

    public int getSize() {
        int i = this.overrideSize;
        if (i != 0) {
            return i;
        }
        int i2 = this.currentStyle;
        return AndroidUtilities.dp((i2 == 4 || i2 == 10) ? 32.0f : 24.0f);
    }

    public void onDetachedFromWindow() {
        if (this.attached) {
            this.attached = false;
            this.wasDraw = false;
            for (int i = 0; i < 3; i++) {
                this.currentStates[i].imageReceiver.onDetachedFromWindow();
                this.animatingStates[i].imageReceiver.onDetachedFromWindow();
            }
            if (this.currentStyle == 3) {
                Theme.getFragmentContextViewWavesDrawable().setAmplitude(0.0f);
            }
        }
    }

    public void onAttachedToWindow() {
        if (this.attached) {
            return;
        }
        this.attached = true;
        for (int i = 0; i < 3; i++) {
            this.currentStates[i].imageReceiver.onAttachedToWindow();
            this.animatingStates[i].imageReceiver.onAttachedToWindow();
        }
    }

    public void setCentered(boolean z) {
        this.centered = z;
    }

    public void setCount(int i) {
        this.count = i;
        View view = this.parent;
        if (view != null) {
            view.requestLayout();
        }
    }

    public void reset() {
        for (int i = 0; i < this.animatingStates.length; i++) {
            setObject(0, 0, null);
        }
    }

    public void setShowSavedMessages(boolean z) {
        this.showSavedMessages = z;
    }
}
