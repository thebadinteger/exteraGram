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
        this.longPressRunnable = new Runnable() { // from class: com.exteragram.messenger.pillstack.ui.PillStackView.1
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

    /* JADX WARN: Code restructure failed: missing block: B:12:0x001b, code lost:
    
        if (r0 != 3) goto L38;
     */
    @Override
    @android.annotation.SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.pills.isEmpty()) {
            return super.onTouchEvent(motionEvent);
        }
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            this.startX = motionEvent.getRawX();
            this.startY = motionEvent.getRawY();
            this.isSwiping = false;
            this.maybeClick = true;
            this.longClickPerformed = false;
            removeCallbacks(this.longPressRunnable);
            postDelayed(this.longPressRunnable, ViewConfiguration.getLongPressTimeout());
            BasePill basePill = this.pills.get(this.currentIndex);
            basePill.setPressed(true);
            basePill.drawableHotspotChanged(motionEvent.getX() - ((float) basePill.getLeft()), motionEvent.getY() - ((float) basePill.getTop()));
            return true;
        } else if (actionMasked == MotionEvent.ACTION_UP || actionMasked == MotionEvent.ACTION_CANCEL) {
            removeCallbacks(this.longPressRunnable);
            if (this.isSwiping) {
                finishSwipe(motionEvent.getRawY() - this.startY);
                this.isSwiping = false;
            } else if (this.maybeClick && !this.longClickPerformed && actionMasked == MotionEvent.ACTION_UP) {
                this.pills.get(this.currentIndex).onPillClicked();
            }
            for (int i = 0; i < this.pills.size(); i++) {
                this.pills.get(i).setPressed(false);
            }
            this.maybeClick = false;
            return true;
        } else if (actionMasked == MotionEvent.ACTION_MOVE) {
            if (this.longClickPerformed) {
                return true;
            }
            float rawX = motionEvent.getRawX() - this.startX;
            float rawY = motionEvent.getRawY() - this.startY;
            if (Math.abs(rawY) > this.touchSlop || Math.abs(rawX) > this.touchSlop) {
                this.maybeClick = false;
                removeCallbacks(this.longPressRunnable);
            }
            if (this.isSwiping) {
                handleSwipeProgress(rawY);
                return true;
            } else if (Math.abs(rawY) > this.touchSlop && this.pills.size() > 1) {
                this.isSwiping = true;
                ValueAnimator valueAnimator = this.currentAnimator;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                float height = this.isSwipingUp ? -(this.currentSwipeProgress * ((float) getHeight())) : this.currentSwipeProgress * ((float) getHeight());
                this.startY = motionEvent.getRawY() - height;
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    private void handleSwipeProgress(float f) {
        int height;
        if (this.pills.size() > 1 && (height = getHeight()) > 0) {
            this.isSwipingUp = f < 0.0f;
            float fAbs = Math.abs(f) / height;
            boolean z = this.isSwipingUp;
            int i = this.currentIndex;
            int i2 = z ? i + 1 : i - 1;
            if (!PillStackConfig.getInfiniteScrolling() && (i2 >= this.pills.size() || i2 < 0)) {
                this.currentSwipeProgress = fAbs;
            } else {
                this.currentSwipeProgress = Math.min(fAbs, 1.0f);
            }
            applyProgress(this.currentSwipeProgress, this.isSwipingUp);
        }
    }

    private void applyProgress(float f, boolean z) {
        BasePill basePill = this.pills.get(this.currentIndex);
        int i = this.currentIndex;
        int size = z ? i + 1 : i - 1;
        if (PillStackConfig.getInfiniteScrolling()) {
            if (size >= this.pills.size()) {
                size = 0;
            }
            if (size < 0) {
                size = this.pills.size() - 1;
            }
        }
        for (int i2 = 0; i2 < this.pills.size(); i2++) {
            if (i2 != this.currentIndex && i2 != size && this.pills.get(i2).getVisibility() != 8) {
                this.pills.get(i2).setVisibility(8);
            }
        }
        if (!PillStackConfig.getInfiniteScrolling() && (size >= this.pills.size() || size < 0)) {
            float height = getHeight() * ((float) (1.0d - (1.0d / (((double) (f * 0.18f)) + 1.0d))));
            if (z) {
                height = -height;
            }
            basePill.setTranslationY(height);
            basePill.setAlpha(1.0f);
            return;
        }
        float fMin = Math.min(f, 1.0f);
        BasePill basePill2 = this.pills.get(size);
        if (basePill2.getVisibility() != 0) {
            basePill2.setVisibility(0);
        }
        float height2 = getHeight() * fMin;
        if (z) {
            height2 = -height2;
        }
        basePill.setTranslationY(height2);
        basePill.setAlpha(1.0f - fMin);
        float f2 = 0.2f * fMin;
        float f3 = 1.0f - f2;
        basePill.setScaleX(f3);
        basePill.setScaleY(f3);
        float f4 = f2 + 0.8f;
        basePill2.setScaleX(f4);
        basePill2.setScaleY(f4);
        basePill2.setAlpha(fMin);
        int height3 = getHeight();
        if (!z) {
            height3 = -height3;
        }
        float f5 = height3;
        basePill2.setTranslationY(f5 - (fMin * f5));
    }

    private void finishSwipe(float f) {
        int height = getHeight();
        if (height <= 0) {
            cancelSwipe(this.isSwipingUp);
            return;
        }
        float f2 = height * 0.25f;
        boolean z = true;
        if (!PillStackConfig.getInfiniteScrolling()) {
            boolean z2 = this.isSwipingUp;
            int i = this.currentIndex;
            int i2 = z2 ? i + 1 : i - 1;
            if (i2 >= this.pills.size() || i2 < 0) {
                z = false;
            }
        }
        if (Math.abs(f) > f2 && z) {
            animateToNextPill(this.isSwipingUp);
        } else {
            cancelSwipe(this.isSwipingUp);
        }
    }

    private void animateToNextPill(final boolean z) {
        ValueAnimator valueAnimator = this.currentAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.currentSwipeProgress, 1.0f);
        this.currentAnimator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(250L);
        this.currentAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.currentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.exteragram.messenger.pillstack.ui.PillStackView$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                PillStackView.this.lambda$animateToNextPill$0(z, valueAnimator2);
            }
        });
        this.currentAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.exteragram.messenger.pillstack.ui.PillStackView.2
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
                BasePill basePill = (BasePill) PillStackView.this.pills.get(PillStackView.this.currentIndex);
                basePill.setVisibility(8);
                basePill.setPressed(false);
                basePill.setScaleX(1.0f);
                basePill.setScaleY(1.0f);
                basePill.onPillUnselected();
                PillStackView pillStackView = PillStackView.this;
                pillStackView.currentIndex = z ? pillStackView.currentIndex + 1 : pillStackView.currentIndex - 1;
                if (PillStackConfig.getInfiniteScrolling()) {
                    if (PillStackView.this.currentIndex >= PillStackView.this.pills.size()) {
                        PillStackView.this.currentIndex = 0;
                    }
                    if (PillStackView.this.currentIndex < 0) {
                        PillStackView pillStackView2 = PillStackView.this;
                        pillStackView2.currentIndex = pillStackView2.pills.size() - 1;
                    }
                }
                int i = 0;
                while (true) {
                    int size = PillStackView.this.pills.size();
                    PillStackView pillStackView3 = PillStackView.this;
                    if (i < size) {
                        if (i != pillStackView3.currentIndex) {
                            ((BasePill) PillStackView.this.pills.get(i)).setVisibility(8);
                        }
                        i++;
                    } else {
                        BasePill basePill2 = (BasePill) pillStackView3.pills.get(PillStackView.this.currentIndex);
                        basePill2.setVisibility(0);
                        basePill2.setScaleX(1.0f);
                        basePill2.setScaleY(1.0f);
                        basePill2.setTranslationY(0.0f);
                        basePill2.setAlpha(1.0f);
                        basePill2.onPillSelected();
                        PillStackView.this.currentSwipeProgress = 0.0f;
                        PillStackConfig.saveLastActivePillId(basePill2.getPillId());
                        return;
                    }
                }
            }
        });
        this.currentAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateToNextPill$0(boolean z, ValueAnimator valueAnimator) {
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
        this.currentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.exteragram.messenger.pillstack.ui.PillStackView$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                PillStackView.this.lambda$cancelSwipe$1(z, valueAnimator2);
            }
        });
        this.currentAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.exteragram.messenger.pillstack.ui.PillStackView.3
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$cancelSwipe$1(boolean z, ValueAnimator valueAnimator) {
        applyProgress(((Float) valueAnimator.getAnimatedValue()).floatValue(), z);
    }
}
