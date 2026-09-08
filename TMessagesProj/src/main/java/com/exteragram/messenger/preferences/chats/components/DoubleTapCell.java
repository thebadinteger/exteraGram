package com.exteragram.messenger.preferences.chats.components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.preferences.components.CustomPreferenceCell;
import com.exteragram.messenger.preferences.components.PreviewColors;
import com.exteragram.messenger.utils.chats.DoubleTapUtils;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Easings;
import org.telegram.ui.Components.LayoutHelper;

public class DoubleTapCell extends LinearLayout implements CustomPreferenceCell {
    private static final int[] ICON_WIDTH = {AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f)};
    private final int[] actionIcon;
    private final ValueAnimator[] animator;
    private final ValueAnimator[] circleAnimator;
    private final Paint[] circleOutlinePaint;
    private final float[] circleProgress;
    private final ValueAnimator[] circleSizeAnimator;
    private final float[] circleSizeProgress;
    private final float[] iconChangingProgress;
    private final Theme.MessageDrawable[] messages;
    private final Paint outlinePaint;
    private final FrameLayout preview;
    private final RectF rect;

    public DoubleTapCell(final Context context) {
        super(context);
        this.rect = new RectF();
        Paint paint = new Paint(1);
        this.outlinePaint = paint;
        this.circleOutlinePaint = new Paint[2];
        this.messages = new Theme.MessageDrawable[]{new Theme.MessageDrawable(0, false, false), new Theme.MessageDrawable(0, true, false)};
        this.animator = new ValueAnimator[2];
        this.circleAnimator = new ValueAnimator[2];
        this.circleSizeAnimator = new ValueAnimator[2];
        this.circleSizeProgress = new float[4];
        this.iconChangingProgress = new float[2];
        this.circleProgress = new float[4];
        this.actionIcon = new int[2];
        setWillNotDraw(false);
        setOrientation(1);
        setBackgroundColor(Theme.getColor(Theme.key_windowBackgroundWhite));
        setPadding(AndroidUtilities.dp(13.0f), 0, AndroidUtilities.dp(13.0f), AndroidUtilities.dp(10.0f));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(AndroidUtilities.dp(1.0f) / 2.0f);
        paint.setColor(PreviewColors.getOutlineColor());
        FrameLayout frameLayout = new FrameLayout(context) { 
            @Override // android.view.View
            @SuppressLint({"DrawAllocation"})
            public void onDraw(Canvas canvas) {
                Rect rect = new Rect();
                float strokeWidth = DoubleTapCell.this.outlinePaint.getStrokeWidth() / 2.0f;
                int i = 0;
                while (i < 2) {
                    if (i == 0) {
                        DoubleTapCell.this.rect.set(AndroidUtilities.dp(8.0f) + strokeWidth, AndroidUtilities.dp(10.0f) + strokeWidth, ((getMeasuredWidth() / 2.0f) - AndroidUtilities.dp(8.0f)) - strokeWidth, AndroidUtilities.dp(75.0f) - strokeWidth);
                    } else {
                        canvas.translate(0.0f, AndroidUtilities.dp(80.0f));
                        DoubleTapCell.this.rect.set((getMeasuredWidth() / 2.0f) + strokeWidth + AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f) + strokeWidth, (getMeasuredWidth() - AndroidUtilities.dp(8.0f)) - strokeWidth, AndroidUtilities.dp(70.0f) - strokeWidth);
                    }
                    DoubleTapCell.this.rect.round(rect);
                    DoubleTapCell.this.messages[i].setBounds(rect);
                    Theme.dialogs_onlineCirclePaint.setColor(PreviewColors.getBackgroundColor());
                    DoubleTapCell.this.messages[i].draw(canvas, Theme.dialogs_onlineCirclePaint);
                    DoubleTapCell.this.messages[i].draw(canvas, DoubleTapCell.this.outlinePaint);
                    int i2 = 0;
                    while (true) {
                        float f = 3.0f;
                        if (i2 >= 2) {
                            break;
                        }
                        DoubleTapCell.this.circleOutlinePaint[i2] = new Paint(1);
                        DoubleTapCell.this.circleOutlinePaint[i2].setStyle(Paint.Style.STROKE);
                        int i3 = i + (i2 * 2);
                        DoubleTapCell.this.circleOutlinePaint[i2].setColor(ColorUtils.blendARGB(0, PreviewColors.getMockColor(true), DoubleTapCell.this.circleProgress[i3]));
                        DoubleTapCell.this.circleOutlinePaint[i2].setStrokeWidth(AndroidUtilities.dp(1.5f) * DoubleTapCell.this.circleProgress[i3] * DoubleTapCell.this.circleProgress[i3]);
                        float measuredWidth = ((i == 0 ? 1 : 3) * getMeasuredWidth()) / 4.0f;
                        float measuredHeight = getMeasuredHeight() / 4.0f;
                        if (i != 0) {
                            f = -2.0f;
                        }
                        canvas.drawCircle(measuredWidth, measuredHeight + AndroidUtilities.dpf2(f), AndroidUtilities.dp(25 - (i2 * 6)) * DoubleTapCell.this.circleSizeProgress[i3], DoubleTapCell.this.circleOutlinePaint[i2]);
                        i2++;
                    }
                    Drawable drawable = ContextCompat.getDrawable(context, DoubleTapCell.this.actionIcon[i]);
                    if (i == 0) {
                        drawable.setBounds((getMeasuredWidth() / 4) - DoubleTapCell.ICON_WIDTH[i], (int) (((getMeasuredHeight() / 4) - DoubleTapCell.ICON_WIDTH[i]) + AndroidUtilities.dpf2(3.0f)), (getMeasuredWidth() / 4) + DoubleTapCell.ICON_WIDTH[i], (int) ((getMeasuredHeight() / 4) + DoubleTapCell.ICON_WIDTH[i] + AndroidUtilities.dpf2(3.0f)));
                    } else {
                        drawable.setBounds(((getMeasuredWidth() * 3) / 4) - DoubleTapCell.ICON_WIDTH[i], (int) (((getMeasuredHeight() / 4) - DoubleTapCell.ICON_WIDTH[i]) - AndroidUtilities.dpf2(2.0f)), ((getMeasuredWidth() * 3) / 4) + DoubleTapCell.ICON_WIDTH[i], (int) (((getMeasuredHeight() / 4) + DoubleTapCell.ICON_WIDTH[i]) - AndroidUtilities.dpf2(2.0f)));
                    }
                    drawable.setBounds(drawable.getBounds().left - AndroidUtilities.dp(4.0f - (DoubleTapCell.this.iconChangingProgress[i] * 4.0f)), drawable.getBounds().top - AndroidUtilities.dp(4.0f - (DoubleTapCell.this.iconChangingProgress[i] * 4.0f)), drawable.getBounds().right + AndroidUtilities.dp(4.0f - (DoubleTapCell.this.iconChangingProgress[i] * 4.0f)), drawable.getBounds().bottom + AndroidUtilities.dp(4.0f - (DoubleTapCell.this.iconChangingProgress[i] * 4.0f)));
                    drawable.setColorFilter(new PorterDuffColorFilter(ColorUtils.blendARGB(0, Theme.getColor(Theme.key_chats_menuItemIcon), DoubleTapCell.this.iconChangingProgress[i]), PorterDuff.Mode.MULTIPLY));
                    drawable.draw(canvas);
                    i++;
                }
            }
        };
        this.preview = frameLayout;
        frameLayout.setWillNotDraw(false);
        addView(frameLayout, LayoutHelper.createFrame(-1, -1.0f));
        updateIcons(0, false);
    }

    @SuppressLint({"Recycle"})
    public void updateIcons(int i, boolean z) {
        final int i2 = 0;
        while (i2 < 2) {
            if (i2 != 0 || i != 2) {
                if (i2 != 1 || i != 1) {
                    if (z) {
                        for (final int i3 = 0; i3 < 2; i3++) {
                            this.circleSizeAnimator[i3] = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(1300L);
                            long j = i3;
                            this.circleSizeAnimator[i3].setStartDelay(60 * j);
                            ValueAnimator valueAnimator = this.circleSizeAnimator[i3];
                            Interpolator interpolator = Easings.easeInOutQuad;
                            valueAnimator.setInterpolator(interpolator);
                            this.circleSizeAnimator[i3].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { 
                                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                                    this.f$0.lambda$updateIcons$0(i3, i2, valueAnimator2);
                                }
                            });
                            this.circleAnimator[i3] = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(700L);
                            this.circleAnimator[i3].setStartDelay((j * 80) + 150);
                            this.circleAnimator[i3].setInterpolator(interpolator);
                            this.circleAnimator[i3].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { 
                                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                                    this.f$0.lambda$updateIcons$1(i3, i2, valueAnimator2);
                                }
                            });
                            this.circleAnimator[i3].addListener(new AnimatorListenerAdapter() { 
                                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                public void onAnimationEnd(Animator animator) {
                                    super.onAnimationEnd(animator);
                                    DoubleTapCell.this.circleAnimator[i3].setFloatValues(1.0f, 0.0f);
                                    DoubleTapCell.this.circleAnimator[i3].setDuration(700L);
                                    DoubleTapCell.this.circleAnimator[i3].removeAllListeners();
                                    DoubleTapCell.this.circleAnimator[i3].start();
                                }
                            });
                            this.circleSizeAnimator[i3].start();
                            this.circleAnimator[i3].start();
                        }
                        this.animator[i2] = ValueAnimator.ofFloat(1.0f, 0.0f).setDuration(250L);
                        this.animator[i2].setInterpolator(Easings.easeInOutQuad);
                        this.animator[i2].addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { 
                            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                                this.f$0.lambda$updateIcons$2(i2, valueAnimator2);
                            }
                        });
                        this.animator[i2].addListener(new AnimatorListenerAdapter() { 
                            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                            public void onAnimationEnd(Animator animator) {
                                super.onAnimationEnd(animator);
                                int[] iArr = DoubleTapCell.this.actionIcon;
                                int i4 = i2;
                                iArr[i4] = DoubleTapUtils.getDoubleTapActionIcon(i4 == 0 ? ExteraConfig.getDoubleTapAction() : ExteraConfig.getDoubleTapActionOutOwner(), i2 == 1);
                                DoubleTapCell.this.animator[i2].setFloatValues(0.0f, 1.0f);
                                DoubleTapCell.this.animator[i2].removeAllListeners();
                                DoubleTapCell.this.animator[i2].addListener(new AnimatorListenerAdapter() { 
                                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                                    public void onAnimationEnd(Animator animator2) {
                                        super.onAnimationEnd(animator2);
                                        DoubleTapCell.this.performHapticFeedback(3, 2);
                                    }
                                });
                                DoubleTapCell.this.animator[i2].start();
                            }
                        });
                        this.animator[i2].start();
                    } else {
                        this.circleSizeProgress[i2] = 0.0f;
                        this.circleProgress[i2] = 0.0f;
                        this.iconChangingProgress[i2] = 1.0f;
                        this.actionIcon[i2] = DoubleTapUtils.getDoubleTapActionIcon(i2 == 0 ? ExteraConfig.getDoubleTapAction() : ExteraConfig.getDoubleTapActionOutOwner(), i2 == 1);
                        invalidate();
                    }
                }
            }
            i2++;
        }
    }

    public /* synthetic */ void lambda$updateIcons$0(int i, int i2, ValueAnimator valueAnimator) {
        this.circleSizeProgress[(i * 2) + i2] = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public /* synthetic */ void lambda$updateIcons$1(int i, int i2, ValueAnimator valueAnimator) {
        this.circleProgress[(i * 2) + i2] = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    public /* synthetic */ void lambda$updateIcons$2(int i, ValueAnimator valueAnimator) {
        this.iconChangingProgress[i] = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        invalidate();
    }

    @Override // android.view.View
    public void invalidate() {
        super.invalidate();
        this.preview.invalidate();
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0.0f, getMeasuredHeight() - 1, getMeasuredWidth(), getMeasuredHeight() - 1, Theme.dividerPaint);
    }

    @Override // android.widget.LinearLayout, android.view.View
    public void onMeasure(int i, int i2) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(170.0f), TLObject.FLAG_30));
    }

    @Override 
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        return (obj instanceof DoubleTapCell) && this.actionIcon == ((DoubleTapCell) obj).actionIcon;
    }
}
