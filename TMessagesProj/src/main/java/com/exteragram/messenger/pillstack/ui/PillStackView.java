package com.exteragram.messenger.pillstack.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import com.exteragram.messenger.pillstack.core.PillStackConfig;
import com.exteragram.messenger.pillstack.ui.pills.BasePill;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.Components.CubicBezierInterpolator;

public class PillStackView extends FrameLayout {
    private ValueAnimator currentAnimator;
    private int currentIndex;
    private float currentSwipeProgress;
    private boolean isSwiping;
    private boolean isSwipingUp;
    private boolean longClickPerformed;
    private final Runnable longPressRunnable;
    private boolean maybeClick;
    private final List<BasePill> pills;
    private boolean stackOnScreen;
    private float startX;
    private float startY;
    private final float touchSlop;
    private float visibilityFactor;

    public PillStackView(Context context) {
        super(context);
        this.pills = new ArrayList();
        this.currentIndex = 0;
        this.longPressRunnable = new Runnable() { 
            @Override // java.lang.Runnable
            public void run() {
                if (!PillStackView.this.maybeClick || PillStackView.this.isSwiping || PillStackView.this.pills.isEmpty()) {
                    return;
                }
                PillStackView pillStackView = PillStackView.this;
                pillStackView.longClickPerformed = ((BasePill) pillStackView.pills.get(PillStackView.this.currentIndex)).onPillLongClicked();
                if (PillStackView.this.longClickPerformed) {
                    PillStackView.this.performHapticFeedback(0);
                }
            }
        };
        this.currentSwipeProgress = 0.0f;
        this.isSwipingUp = false;
        this.visibilityFactor = -1.0f;
        this.stackOnScreen = true;
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        setClipChildren(false);
    }

    public void addPill(BasePill basePill) {
        this.pills.add(basePill);
        addView(basePill);
        if (this.pills.size() - 1 != this.currentIndex) {
            basePill.setAlpha(0.0f);
            basePill.setScaleX(0.8f);
            basePill.setScaleY(0.8f);
            basePill.setVisibility(8);
        } else {
            basePill.setVisibility(0);
            basePill.onPillSelected();
        }
        basePill.onStackVisibilityChanged(this.stackOnScreen);
    }

    @Override // android.view.View
    public void onVisibilityAggregated(boolean z) {
        super.onVisibilityAggregated(z);
        if (this.stackOnScreen == z) {
            return;
        }
        this.stackOnScreen = z;
        Iterator<BasePill> it = this.pills.iterator();
        while (it.hasNext()) {
            it.next().onStackVisibilityChanged(z);
        }
    }

    public int getPillsCount() {
        return this.pills.size();
    }

    public void setCurrentIndex(int i) {
        int i2;
        if (i < 0 || i >= this.pills.size() || i == (i2 = this.currentIndex)) {
            return;
        }
        BasePill basePill = this.pills.get(i2);
        basePill.setVisibility(8);
        basePill.onPillUnselected();
        this.currentIndex = i;
        BasePill basePill2 = this.pills.get(i);
        basePill2.setVisibility(0);
        basePill2.setAlpha(1.0f);
        basePill2.setScaleX(1.0f);
        basePill2.setScaleY(1.0f);
        basePill2.setTranslationY(0.0f);
        basePill2.onPillSelected();
        requestLayout();
    }

    public void clearPills() {
        if (!this.pills.isEmpty() && this.currentIndex < this.pills.size()) {
            this.pills.get(this.currentIndex).onPillUnselected();
        }
        this.pills.clear();
        removeAllViews();
        this.currentIndex = 0;
    }

    public void setVisibilityFactor(float f) {
        if (this.visibilityFactor == f) {
            return;
        }
        this.visibilityFactor = f;
        if (f > 0.01f) {
            if (getVisibility() != 0) {
                setVisibility(0);
            }
            setAlpha(this.visibilityFactor);
            setScaleX(AndroidUtilities.lerp(0.6f, 1.0f, this.visibilityFactor));
            setScaleY(AndroidUtilities.lerp(0.6f, 1.0f, this.visibilityFactor));
            return;
        }
        setVisibility(8);
    }

    public void updateColors() {
        Iterator<BasePill> it = this.pills.iterator();
        while (it.hasNext()) {
            it.next().updateColors();
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (this.pills.isEmpty()) {
            return super.onInterceptTouchEvent(motionEvent);
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 0) {
            this.startX = motionEvent.getRawX();
            this.startY = motionEvent.getRawY();
            this.isSwiping = false;
        } else if (actionMasked == 2) {
            float rawX = motionEvent.getRawX() - this.startX;
            float rawY = motionEvent.getRawY() - this.startY;
            if ((Math.abs(rawY) > this.touchSlop || Math.abs(rawX) > this.touchSlop) && Math.abs(rawY) > this.touchSlop && this.pills.size() > 1) {
                this.isSwiping = true;
                ValueAnimator valueAnimator = this.currentAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                boolean z = this.isSwipingUp;
                float f = this.currentSwipeProgress;
                this.startY = motionEvent.getRawY() - (z ? -(f * getHeight()) : getHeight() * f);
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return true;
            }
        }
        return super.onInterceptTouchEvent(motionEvent);
    }

    void lambda$animateToNextPill$0(boolean z, ValueAnimator valueAnimator) {
        applyProgress(((Float) valueAnimator.getAnimatedValue()).floatValue(), z);
    }

    private void cancelSwipe(final boolean z) {
        ValueAnimator valueAnimator = this.currentAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.currentSwipeProgress, 0.0f);
        this.currentAnimator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(200L);
        this.currentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { 
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.lambda$cancelSwipe$1(z, valueAnimator2);
            }
        });
        this.currentAnimator.addListener(new AnimatorListenerAdapter() { 
            private boolean cancelled = false;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                this.cancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (this.cancelled) {
                    return;
                }
                int i = 0;
                while (true) {
                    int size = PillStackView.this.pills.size();
                    PillStackView pillStackView = PillStackView.this;
                    if (i < size) {
                        if (i != pillStackView.currentIndex) {
                            BasePill basePill = (BasePill) PillStackView.this.pills.get(i);
                            basePill.setVisibility(8);
                            basePill.setPressed(false);
                            basePill.setScaleX(1.0f);
                            basePill.setScaleY(1.0f);
                        }
                        i++;
                    } else {
                        BasePill basePill2 = (BasePill) pillStackView.pills.get(PillStackView.this.currentIndex);
                        basePill2.setTranslationY(0.0f);
                        basePill2.setAlpha(1.0f);
                        basePill2.setScaleX(1.0f);
                        basePill2.setScaleY(1.0f);
                        PillStackView.this.currentSwipeProgress = 0.0f;
                        return;
                    }
                }
            }
        });
        this.currentAnimator.start();
    }

    public /* synthetic */ void lambda$cancelSwipe$1(boolean z, ValueAnimator valueAnimator) {
        applyProgress(((Float) valueAnimator.getAnimatedValue()).floatValue(), z);
    }
}
