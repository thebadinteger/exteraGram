package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import androidx.annotation.Keep;
import com.exteragram.messenger.ExteraConfig;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PopupSwipeBackLayout;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;

public class ActionBarPopupWindow extends PopupWindow {
    private static final ViewTreeObserver.OnScrollChangedListener NOP;
    private static DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private static Method layoutInScreenMethod;
    private static final Field superListenerField;
    private boolean animationEnabled;
    private int currentAccount;
    private int dismissAnimationDuration;
    private boolean isClosingAnimated;
    private ViewTreeObserver.OnScrollChangedListener mSuperScrollListener;
    private ViewTreeObserver mViewTreeObserver;
    private AnimationNotificationsLocker notificationsLocker;
    private long outEmptyTime;
    private boolean pauseNotifications;
    private boolean scaleOut;
    private AnimatorSet windowAnimatorSet;

    public interface OnDispatchKeyEventListener {
        void onDispatchKeyEvent(KeyEvent keyEvent);
    }

    public interface onSizeChangedListener {
        void onSizeChanged();
    }

    public static /* synthetic */ void $r8$lambda$HbVcyVBCStk9DX84U8bwJXYdotU() {
    }

    static {
        Field declaredField = null;
        try {
            declaredField = PopupWindow.class.getDeclaredField("mOnScrollChangedListener");
            declaredField.setAccessible(true);
        } catch (NoSuchFieldException unused) {
        }
        superListenerField = declaredField;
        NOP = new ViewTreeObserver.OnScrollChangedListener() { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow$$ExternalSyntheticLambda1
            @Override // android.view.ViewTreeObserver.OnScrollChangedListener
            public final void onScrollChanged() {
                ActionBarPopupWindow.$r8$lambda$HbVcyVBCStk9DX84U8bwJXYdotU();
            }
        };
    }

    public void setScaleOut(boolean z) {
        this.scaleOut = z;
    }

    public static class ActionBarPopupWindowLayout extends FrameLayout {
        private boolean animationEnabled;
        private int backAlpha;
        private float backScaleX;
        private float backScaleY;
        private int backgroundColor;
        protected Drawable backgroundDrawable;
        private final Rect bgPaddings;
        private boolean cascadeEnabled;
        public boolean clipChildren;
        private boolean fitItems;
        private int gapEndY;
        private int gapStartY;
        private BlurredBackgroundDrawableViewFactory glassBackgroundFactory;
        private ArrayList<AnimatorSet> itemAnimators;
        private int lastStartedChild;
        protected LinearLayout linearLayout;
        private OnDispatchKeyEventListener mOnDispatchKeyEventListener;
        ViewTreeObserver.OnScrollChangedListener onScrollChangedListener;
        private onSizeChangedListener onSizeChangedListener;
        Path path;
        private HashMap<View, Integer> positions;
        private float reactionsEnterProgress;
        Rect rect;
        private final Theme.ResourcesProvider resourcesProvider;
        private ScrollView scrollView;
        public boolean shownFromBottom;
        private boolean startAnimationPending;
        public int subtractBackgroundHeight;
        public boolean swipeBackGravityBottom;
        public boolean swipeBackGravityCenterHorizontal;
        public boolean swipeBackGravityRight;
        private PopupSwipeBackLayout swipeBackLayout;
        private View topView;
        public boolean updateAnimation;
        protected ActionBarPopupWindow window;

        public Rect getPadding() {
            return this.bgPaddings;
        }

        public ActionBarPopupWindowLayout(Context context) {
            this(context, null);
        }

        public ActionBarPopupWindowLayout(Context context, Theme.ResourcesProvider resourcesProvider) {
            this(context, R.drawable.popup_fixed_alert2, resourcesProvider);
        }

        public ActionBarPopupWindowLayout(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
            this(context, i, resourcesProvider, 0);
        }

        public ActionBarPopupWindowLayout(Context context, int i, Theme.ResourcesProvider resourcesProvider, int i2) {
            super(context);
            this.backScaleX = 1.0f;
            this.backScaleY = 1.0f;
            this.startAnimationPending = false;
            this.backAlpha = 255;
            this.lastStartedChild = 0;
            this.animationEnabled = true;
            this.cascadeEnabled = false;
            this.positions = new HashMap<>();
            this.gapStartY = -1000000;
            this.gapEndY = -1000000;
            Rect rect = new Rect();
            this.bgPaddings = rect;
            this.reactionsEnterProgress = 1.0f;
            this.backgroundColor = -1;
            this.resourcesProvider = resourcesProvider;
            if (i != 0) {
                this.backgroundDrawable = getResources().getDrawable(i).mutate();
                setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
            }
            Drawable drawable = this.backgroundDrawable;
            if (drawable != null) {
                drawable.getPadding(rect);
                setBackgroundColor(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
            }
            setWillNotDraw(false);
            if ((i2 & 2) > 0) {
                this.shownFromBottom = true;
            }
            if ((i2 & 1) > 0) {
                PopupSwipeBackLayout popupSwipeBackLayout = new PopupSwipeBackLayout(context, resourcesProvider);
                this.swipeBackLayout = popupSwipeBackLayout;
                addView(popupSwipeBackLayout, LayoutHelper.createFrame(-2, -2.0f));
            }
            if ((i2 & 4) == 0) {
                try {
                    this.scrollView = new ScrollView(context);
                    this.onScrollChangedListener = new ViewTreeObserver.OnScrollChangedListener() { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout.1
                        @Override // android.view.ViewTreeObserver.OnScrollChangedListener
                        public void onScrollChanged() {
                            ActionBarPopupWindowLayout.this.invalidate();
                        }
                    };
                    this.scrollView.getViewTreeObserver().addOnScrollChangedListener(this.onScrollChangedListener);
                    this.scrollView.setVerticalScrollBarEnabled(false);
                    PopupSwipeBackLayout popupSwipeBackLayout2 = this.swipeBackLayout;
                    if (popupSwipeBackLayout2 != null) {
                        popupSwipeBackLayout2.addView(this.scrollView, LayoutHelper.createFrame(-2, -2, this.shownFromBottom ? 80 : 48));
                    } else {
                        addView(this.scrollView, LayoutHelper.createFrame(-2, -2.0f));
                    }
                } catch (Throwable th) {
                    FileLog.e(th);
                }
            }
            AnonymousClass2 anonymousClass2 = new AnonymousClass2(context);
            this.linearLayout = anonymousClass2;
            anonymousClass2.setOrientation(1);
            ScrollView scrollView = this.scrollView;
            if (scrollView != null) {
                scrollView.addView(this.linearLayout, new FrameLayout.LayoutParams(-2, -2));
                return;
            }
            PopupSwipeBackLayout popupSwipeBackLayout3 = this.swipeBackLayout;
            View view = this.linearLayout;
            if (popupSwipeBackLayout3 != null) {
                popupSwipeBackLayout3.addView(view, LayoutHelper.createFrame(-2, -2, this.shownFromBottom ? 80 : 48));
            } else {
                addView(view, LayoutHelper.createFrame(-2, -2.0f));
            }
        }

        public class AnonymousClass2 extends LinearLayout {
            public AnonymousClass2(Context context) {
                super(context);
            }

            /* JADX WARN: Code duplicated, block: B:22:0x008f  */
            @Override // android.widget.LinearLayout, android.view.View
            public void onMeasure(int i, int i2) {
                if (ActionBarPopupWindowLayout.this.fitItems) {
                    ActionBarPopupWindowLayout.this.gapStartY = -1000000;
                    ActionBarPopupWindowLayout.this.gapEndY = -1000000;
                    int childCount = getChildCount();
                    ArrayList arrayList = null;
                    int iMax = 0;
                    int iMax2 = 0;
                    for (int i3 = 0; i3 < childCount; i3++) {
                        View childAt = getChildAt(i3);
                        if (childAt.getVisibility() != 8) {
                            Object tag = childAt.getTag(R.id.width_tag);
                            Object tag2 = childAt.getTag(R.id.object_tag);
                            Object tag3 = childAt.getTag(R.id.fit_width_tag);
                            if (tag != null) {
                                childAt.getLayoutParams().width = -2;
                            }
                            measureChildWithMargins(childAt, i, 0, i2, 0);
                            if (tag3 == null) {
                                boolean z = tag instanceof Integer;
                                if (!z && tag2 == null) {
                                    iMax = Math.max(iMax, childAt.getMeasuredWidth());
                                } else {
                                    if (z) {
                                        iMax2 = Math.max(((Integer) tag).intValue(), childAt.getMeasuredWidth());
                                        ActionBarPopupWindowLayout.this.gapStartY = childAt.getMeasuredHeight();
                                        ActionBarPopupWindowLayout actionBarPopupWindowLayout = ActionBarPopupWindowLayout.this;
                                        actionBarPopupWindowLayout.gapEndY = actionBarPopupWindowLayout.gapStartY + AndroidUtilities.dp(8.0f);
                                    }
                                    if (arrayList == null) {
                                        arrayList = new ArrayList();
                                    }
                                    arrayList.add(childAt);
                                }
                            } else {
                                if (arrayList == null) {
                                    arrayList = new ArrayList();
                                }
                                arrayList.add(childAt);
                            }
                        }
                    }
                    if (arrayList != null) {
                        int size = arrayList.size();
                        for (int i4 = 0; i4 < size; i4++) {
                            ((View) arrayList.get(i4)).getLayoutParams().width = Math.max(iMax, iMax2);
                        }
                    }
                }
                super.onMeasure(i, i2);
            }

            @Override // android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View view, long j) {
                if (!(view instanceof GapView) || ActionBarPopupWindowLayout.this.backgroundDrawable == null) {
                    return super.drawChild(canvas, view, j);
                }
                return false;
            }

            @Override // android.view.ViewGroup
            public void addView(final View view, int i, ViewGroup.LayoutParams layoutParams) {
                view.setAlpha(0.0f);
                view.setTranslationY(AndroidUtilities.dp(ActionBarPopupWindowLayout.this.shownFromBottom ? 12.0f : -12.0f));
                super.addView(view, i, layoutParams);
                if (ActionBarPopupWindowLayout.this.cascadeEnabled && (view instanceof ActionBarMenuSubItem) && ExteraConfig.getGroupMessageMenu()) {
                    final int iCount = (int) IntStream.range(0, getChildCount()).mapToObj(new IntFunction() { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout$2$$ExternalSyntheticLambda0
                        @Override // java.util.function.IntFunction
                        public final Object apply(int i2) {
                            return this.f$0.getChildAt(i2);
                        }
                    }).filter(new Predicate() { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout$2$$ExternalSyntheticLambda1
                        @Override // java.util.function.Predicate
                        public final boolean test(Object obj) {
                            return ActionBarPopupWindow.ActionBarPopupWindowLayout.AnonymousClass2.m5704$r8$lambda$wXjAZBZifSKRK7rNq_cjoczxc((View) obj);
                        }
                    }).count();
                    view.post(new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow$ActionBarPopupWindowLayout$2$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            View view2 = view;
                            view2.animate().alpha(view2.isEnabled() ? 1.0f : 0.5f).translationY(0.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setStartDelay((((long) iCount) * 35) + 10).setDuration(400L).start();
                        }
                    });
                } else {
                    view.setAlpha(view.isEnabled() ? 1.0f : 0.5f);
                    view.setTranslationY(0.0f);
                }
            }

            public static /* synthetic */ boolean m5704$r8$lambda$wXjAZBZifSKRK7rNq_cjoczxc(View view) {
                return (view instanceof ActionBarMenuSubItem) && view.getVisibility() == 0;
            }
        }

        public PopupSwipeBackLayout getSwipeBack() {
            return this.swipeBackLayout;
        }

        public int addViewToSwipeBack(View view) {
            this.swipeBackLayout.addView(view, LayoutHelper.createFrame(-2, -2, this.shownFromBottom ? 80 : 48));
            return this.swipeBackLayout.getChildCount() - 1;
        }

        public void setFitItems(boolean z) {
            this.fitItems = z;
        }

        public void setShownFromBottom(boolean z) {
            this.shownFromBottom = z;
        }

        public void setDispatchKeyEventListener(OnDispatchKeyEventListener onDispatchKeyEventListener) {
            this.mOnDispatchKeyEventListener = onDispatchKeyEventListener;
        }

        public int getBackgroundColor() {
            return this.backgroundColor;
        }

        @Override // android.view.View
        public void setBackgroundColor(int i) {
            Drawable drawable;
            if (this.backgroundColor == i || (drawable = this.backgroundDrawable) == null) {
                return;
            }
            this.backgroundColor = i;
            drawable.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.MULTIPLY));
        }

        public void setGlassBackgroundFactory(BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory) {
            this.glassBackgroundFactory = blurredBackgroundDrawableViewFactory;
        }

        public BlurredBackgroundDrawableViewFactory getGlassBackgroundFactory() {
            return this.glassBackgroundFactory;
        }

        @Keep
        public void setBackAlpha(int i) {
            if (this.backAlpha != i) {
                invalidate();
            }
            this.backAlpha = i;
        }

        @Keep
        public int getBackAlpha() {
            return this.backAlpha;
        }

        @Keep
        public void setBackScaleX(float f) {
            if (this.backScaleX != f) {
                this.backScaleX = f;
                invalidate();
                onSizeChangedListener onsizechangedlistener = this.onSizeChangedListener;
                if (onsizechangedlistener != null) {
                    onsizechangedlistener.onSizeChanged();
                }
            }
        }

        @Keep
        public void setBackScaleY(float f) {
            if (this.backScaleY != f) {
                this.backScaleY = f;
                if (this.animationEnabled && this.updateAnimation) {
                    int measuredHeight = getMeasuredHeight() - AndroidUtilities.dp(16.0f);
                    if (this.shownFromBottom) {
                        for (int i = this.lastStartedChild; i >= 0; i--) {
                            View itemAt = getItemAt(i);
                            if (itemAt != null && itemAt.getVisibility() == 0 && !(itemAt instanceof GapView)) {
                                Integer num = this.positions.get(itemAt);
                                if (num != null && measuredHeight - ((num.intValue() * AndroidUtilities.dp(48.0f)) + AndroidUtilities.dp(32.0f)) > measuredHeight * f) {
                                    break;
                                }
                                this.lastStartedChild = i - 1;
                                startChildAnimation(itemAt);
                            }
                        }
                    } else {
                        int itemsCount = getItemsCount();
                        int measuredHeight2 = 0;
                        for (int i2 = 0; i2 < itemsCount; i2++) {
                            View itemAt2 = getItemAt(i2);
                            if (itemAt2.getVisibility() == 0) {
                                measuredHeight2 += itemAt2.getMeasuredHeight();
                                if (i2 < this.lastStartedChild) {
                                    continue;
                                } else {
                                    if (this.positions.get(itemAt2) != null && measuredHeight2 - AndroidUtilities.dp(24.0f) > measuredHeight * f) {
                                        break;
                                    }
                                    this.lastStartedChild = i2 + 1;
                                    startChildAnimation(itemAt2);
                                }
                            }
                        }
                    }
                }
                invalidate();
                onSizeChangedListener onsizechangedlistener = this.onSizeChangedListener;
                if (onsizechangedlistener != null) {
                    onsizechangedlistener.onSizeChanged();
                }
            }
        }

        @Override // android.view.View
        public void setBackgroundDrawable(Drawable drawable) {
            this.backgroundColor = -1;
            this.backgroundDrawable = drawable;
            if (drawable != null) {
                drawable.getPadding(this.bgPaddings);
            }
        }

        private void startChildAnimation(final View view) {
            if (this.animationEnabled) {
                final AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.playTogether(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.ALPHA, 0.0f, view.isEnabled() ? 1.0f : 0.5f), ObjectAnimator.ofFloat(view, (Property<View, Float>) View.TRANSLATION_Y, AndroidUtilities.dp(this.shownFromBottom ? 6.0f : -6.0f), 0.0f));
                animatorSet.setDuration(180L);
                animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow.ActionBarPopupWindowLayout.3
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        ActionBarPopupWindowLayout.this.itemAnimators.remove(animatorSet);
                        View view2 = view;
                        if (view2 instanceof ActionBarMenuSubItem) {
                            ((ActionBarMenuSubItem) view2).onItemShown();
                        }
                    }
                });
                animatorSet.setInterpolator(ActionBarPopupWindow.decelerateInterpolator);
                animatorSet.start();
                if (this.itemAnimators == null) {
                    this.itemAnimators = new ArrayList<>();
                }
                this.itemAnimators.add(animatorSet);
            }
        }

        public void setAnimationEnabled(boolean z) {
            this.animationEnabled = z;
        }

        public void setCascadeEnabled(boolean z) {
            this.cascadeEnabled = z;
        }

        @Override // android.view.ViewGroup
        public void addView(View view) {
            this.linearLayout.addView(view);
        }

        public void addView(View view, LinearLayout.LayoutParams layoutParams) {
            this.linearLayout.addView(view, layoutParams);
        }

        public int getViewsCount() {
            return this.linearLayout.getChildCount();
        }

        public int precalculateHeight() {
            int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE);
            this.linearLayout.measure(iMakeMeasureSpec, iMakeMeasureSpec);
            return this.linearLayout.getMeasuredHeight();
        }

        public void removeInnerViews() {
            this.linearLayout.removeAllViews();
        }

        public float getBackScaleX() {
            return this.backScaleX;
        }

        public float getBackScaleY() {
            return this.backScaleY;
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchKeyEvent(KeyEvent keyEvent) {
            OnDispatchKeyEventListener onDispatchKeyEventListener = this.mOnDispatchKeyEventListener;
            if (onDispatchKeyEventListener != null) {
                onDispatchKeyEventListener.onDispatchKeyEvent(keyEvent);
            }
            return super.dispatchKeyEvent(keyEvent);
        }

        private int getSwipeBackScaledLeft(int i) {
            PopupSwipeBackLayout popupSwipeBackLayout = this.swipeBackLayout;
            if (popupSwipeBackLayout != null && popupSwipeBackLayout.stickToRight) {
                return getMeasuredWidth() - i;
            }
            if (popupSwipeBackLayout == null || !popupSwipeBackLayout.stickToCenterHorizontal) {
                return 0;
            }
            return (getMeasuredWidth() - i) / 2;
        }

        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(Canvas canvas) {
            boolean z;
            int i;
            Canvas canvas2;
            boolean z2;
            float f = 16.0f;
            float f2 = 1.0f;
            if (this.swipeBackGravityRight) {
                setTranslationX(getMeasuredWidth() * (1.0f - this.backScaleX));
                View view = this.topView;
                if (view != null) {
                    view.setTranslationX(getMeasuredWidth() * (1.0f - this.backScaleX));
                    this.topView.setAlpha(1.0f - this.swipeBackLayout.transitionProgress);
                    float f3 = (-(this.topView.getMeasuredHeight() - AndroidUtilities.dp(16.0f))) * this.swipeBackLayout.transitionProgress;
                    this.topView.setTranslationY(f3);
                    setTranslationY(f3);
                }
            }
            if (this.swipeBackGravityBottom) {
                setTranslationY(getMeasuredHeight() * (1.0f - this.backScaleY));
            }
            if (this.backgroundDrawable != null) {
                int i2 = this.gapStartY;
                ScrollView scrollView = this.scrollView;
                int scrollY = i2 - (scrollView == null ? 0 : scrollView.getScrollY());
                int i3 = this.gapEndY;
                ScrollView scrollView2 = this.scrollView;
                int scrollY2 = i3 - (scrollView2 == null ? 0 : scrollView2.getScrollY());
                int i4 = 0;
                while (true) {
                    if (i4 >= this.linearLayout.getChildCount()) {
                        z = false;
                        break;
                    } else {
                        if ((this.linearLayout.getChildAt(i4) instanceof GapView) && this.linearLayout.getChildAt(i4).getVisibility() == 0) {
                            z = true;
                            break;
                        }
                        i4++;
                    }
                }
                int i5 = 0;
                while (i5 < 2 && (i5 != 1 || scrollY >= (-AndroidUtilities.dp(f)))) {
                    int saveCount = canvas.getSaveCount();
                    if (z && this.backAlpha != 255) {
                        i = -1000000;
                        canvas2 = canvas;
                        canvas2.saveLayerAlpha(0.0f, this.bgPaddings.top, getMeasuredWidth(), getMeasuredHeight(), this.backAlpha, 31);
                        z2 = false;
                    } else {
                        i = -1000000;
                        canvas2 = canvas;
                        if (this.gapStartY != -1000000) {
                            canvas2.save();
                            canvas2.clipRect(0, this.bgPaddings.top, getMeasuredWidth(), getMeasuredHeight());
                        }
                        z2 = true;
                    }
                    this.backgroundDrawable.setAlpha(z2 ? this.backAlpha : 255);
                    if (this.shownFromBottom) {
                        int measuredHeight = getMeasuredHeight();
                        int measuredWidth = (int) (getMeasuredWidth() * this.backScaleX);
                        int swipeBackScaledLeft = getSwipeBackScaledLeft(measuredWidth);
                        AndroidUtilities.rectTmp2.set(swipeBackScaledLeft, (int) (measuredHeight * (f2 - this.backScaleY)), measuredWidth + swipeBackScaledLeft, measuredHeight);
                    } else if (scrollY > (-AndroidUtilities.dp(f))) {
                        int measuredHeight2 = (int) (getMeasuredHeight() * this.backScaleY);
                        if (i5 == 0) {
                            int measuredWidth2 = (int) (getMeasuredWidth() * this.backScaleX);
                            int swipeBackScaledLeft2 = getSwipeBackScaledLeft(measuredWidth2);
                            Rect rect = AndroidUtilities.rectTmp2;
                            ScrollView scrollView3 = this.scrollView;
                            int iDp = (scrollView3 == null ? 0 : -scrollView3.getScrollY()) + (this.gapStartY != i ? AndroidUtilities.dp(f2) : 0);
                            int i6 = measuredWidth2 + swipeBackScaledLeft2;
                            if (this.gapStartY != i) {
                                measuredHeight2 = Math.min(measuredHeight2, AndroidUtilities.dp(f) + scrollY);
                            }
                            rect.set(swipeBackScaledLeft2, iDp, i6, measuredHeight2 - this.subtractBackgroundHeight);
                        } else {
                            if (measuredHeight2 < scrollY2) {
                                if (this.gapStartY != i) {
                                    canvas2.restore();
                                }
                            } else {
                                int measuredWidth3 = (int) (getMeasuredWidth() * this.backScaleX);
                                int swipeBackScaledLeft3 = getSwipeBackScaledLeft(measuredWidth3);
                                AndroidUtilities.rectTmp2.set(swipeBackScaledLeft3, scrollY2, measuredWidth3 + swipeBackScaledLeft3, measuredHeight2 - this.subtractBackgroundHeight);
                            }
                            i5++;
                            f = f;
                            f2 = f2;
                        }
                    } else {
                        int measuredWidth4 = (int) (getMeasuredWidth() * this.backScaleX);
                        int swipeBackScaledLeft4 = getSwipeBackScaledLeft(measuredWidth4);
                        AndroidUtilities.rectTmp2.set(swipeBackScaledLeft4, this.gapStartY < 0 ? 0 : -AndroidUtilities.dp(f), measuredWidth4 + swipeBackScaledLeft4, ((int) (getMeasuredHeight() * this.backScaleY)) - this.subtractBackgroundHeight);
                    }
                    if (this.reactionsEnterProgress != f2) {
                        if (this.rect == null) {
                            this.rect = new Rect();
                        }
                        Rect rect2 = this.rect;
                        Rect rect3 = AndroidUtilities.rectTmp2;
                        int i7 = rect3.right;
                        int i8 = rect3.top;
                        rect2.set(i7, i8, i7, i8);
                        AndroidUtilities.lerp(this.rect, rect3, this.reactionsEnterProgress, rect3);
                    }
                    Drawable drawable = this.backgroundDrawable;
                    Rect rect4 = AndroidUtilities.rectTmp2;
                    drawable.setBounds(rect4);
                    this.backgroundDrawable.draw(canvas2);
                    if (this.clipChildren) {
                        int i9 = rect4.left;
                        Rect rect5 = this.bgPaddings;
                        rect4.left = i9 + rect5.left;
                        rect4.top += rect5.top;
                        rect4.right -= rect5.right;
                        rect4.bottom -= rect5.bottom;
                        canvas2.clipRect(rect4);
                    }
                    if (z) {
                        canvas2.save();
                        RectF rectF = AndroidUtilities.rectTmp;
                        rectF.set(this.backgroundDrawable.getBounds());
                        rectF.inset(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(8.0f));
                        Path path = this.path;
                        if (path == null) {
                            this.path = new Path();
                        } else {
                            path.rewind();
                        }
                        this.path.addRoundRect(rectF, AndroidUtilities.dp(12.0f), AndroidUtilities.dp(12.0f), Path.Direction.CW);
                        canvas2.clipPath(this.path);
                        for (int i10 = 0; i10 < this.linearLayout.getChildCount(); i10++) {
                            if ((this.linearLayout.getChildAt(i10) instanceof GapView) && this.linearLayout.getChildAt(i10).getVisibility() == 0) {
                                canvas2.save();
                                GapView gapView = (GapView) this.linearLayout.getChildAt(i10);
                                float x = 0.0f;
                                View view2 = gapView;
                                float y = 0.0f;
                                while (view2 != this) {
                                    x += view2.getX();
                                    y += view2.getY();
                                    view2 = (View) view2.getParent();
                                    if (view2 == null) {
                                        break;
                                    }
                                }
                                ScrollView scrollView4 = this.scrollView;
                                float scaleY = y * (scrollView4 == null ? f2 : scrollView4.getScaleY());
                                ScrollView scrollView5 = this.scrollView;
                                canvas2.translate(x, scaleY - (scrollView5 == null ? 0 : scrollView5.getScrollY()));
                                gapView.draw(canvas2);
                                canvas2.restore();
                            }
                        }
                        canvas2.restore();
                    }
                    canvas2.restoreToCount(saveCount);
                    i5++;
                    f = f;
                    f2 = f2;
                }
            }
            float f4 = f2;
            float f5 = this.reactionsEnterProgress;
            if (f5 != f4) {
                Rect rect6 = AndroidUtilities.rectTmp2;
                canvas.saveLayerAlpha(rect6.left, rect6.top, rect6.right, rect6.bottom, (int) (f5 * 255.0f), 31);
                float f6 = (this.reactionsEnterProgress * 0.5f) + 0.5f;
                canvas.scale(f6, f6, rect6.right, rect6.top);
                super.dispatchDraw(canvas);
                canvas.restore();
                return;
            }
            super.dispatchDraw(canvas);
        }

        public Drawable getBackgroundDrawable() {
            return this.backgroundDrawable;
        }

        public int getItemsCount() {
            return this.linearLayout.getChildCount();
        }

        public View getItemAt(int i) {
            return this.linearLayout.getChildAt(i);
        }

        public void scrollToTop() {
            ScrollView scrollView = this.scrollView;
            if (scrollView != null) {
                scrollView.scrollTo(0, 0);
            }
        }

        public void setupRadialSelectors(int i) {
            int childCount = this.linearLayout.getChildCount();
            int i2 = 0;
            while (i2 < childCount) {
                View childAt = this.linearLayout.getChildAt(i2);
                int i3 = 10;
                int i4 = i2 == 0 ? 10 : 0;
                if (i2 != childCount - 1) {
                    i3 = 0;
                }
                childAt.setBackground(Theme.createRadSelectorDrawable(i, i4, i3));
                i2++;
            }
        }

        public void updateRadialSelectors() {
            int childCount = this.linearLayout.getChildCount();
            View view = null;
            View view2 = null;
            for (int i = 0; i < childCount; i++) {
                View childAt = this.linearLayout.getChildAt(i);
                if (childAt.getVisibility() == 0) {
                    if (view == null) {
                        view = childAt;
                    }
                    view2 = childAt;
                }
            }
            boolean z = false;
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt2 = this.linearLayout.getChildAt(i2);
                if (childAt2.getVisibility() == 0) {
                    Object tag = childAt2.getTag(R.id.object_tag);
                    if (childAt2 instanceof ActionBarMenuSubItem) {
                        ((ActionBarMenuSubItem) childAt2).updateSelectorBackground(childAt2 == view || z, childAt2 == view2);
                    }
                    z = tag != null;
                }
            }
        }

        public int getThemedColor(int i) {
            return Theme.getColor(i, this.resourcesProvider);
        }

        public void setOnSizeChangedListener(onSizeChangedListener onsizechangedlistener) {
            this.onSizeChangedListener = onsizechangedlistener;
        }

        public int getVisibleHeight() {
            return (int) (getMeasuredHeight() * this.backScaleY);
        }

        public void setTopView(View view) {
            this.topView = view;
        }

        public void setSwipeBackForegroundColor(int i) {
            getSwipeBack().setForegroundColor(i);
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            super.onMeasure(i, i2);
            PopupSwipeBackLayout popupSwipeBackLayout = this.swipeBackLayout;
            if (popupSwipeBackLayout != null) {
                popupSwipeBackLayout.invalidateTransforms(!this.startAnimationPending);
            }
        }

        public void setParentWindow(ActionBarPopupWindow actionBarPopupWindow) {
            this.window = actionBarPopupWindow;
        }

        public void setReactionsTransitionProgress(float f) {
            this.reactionsEnterProgress = f;
            invalidate();
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            ScrollView scrollView = this.scrollView;
            if (scrollView == null || this.onScrollChangedListener == null) {
                return;
            }
            if (scrollView.getViewTreeObserver().isAlive()) {
                this.scrollView.getViewTreeObserver().removeOnScrollChangedListener(this.onScrollChangedListener);
            }
            this.onScrollChangedListener = null;
        }
    }

    public ActionBarPopupWindow(Context context) {
        super(context);
        this.animationEnabled = true;
        this.dismissAnimationDuration = 150;
        this.currentAccount = UserConfig.selectedAccount;
        this.outEmptyTime = -1L;
        this.notificationsLocker = new AnimationNotificationsLocker();
        init();
    }

    public ActionBarPopupWindow(View view, int i, int i2) {
        super(view, i, i2);
        this.animationEnabled = true;
        this.dismissAnimationDuration = 150;
        this.currentAccount = UserConfig.selectedAccount;
        this.outEmptyTime = -1L;
        this.notificationsLocker = new AnimationNotificationsLocker();
        init();
    }

    public void setAnimationEnabled(boolean z) {
        this.animationEnabled = z;
    }

    public void setLayoutInScreen(boolean z) {
        try {
            if (layoutInScreenMethod == null) {
                Method declaredMethod = PopupWindow.class.getDeclaredMethod("setLayoutInScreenEnabled", Boolean.TYPE);
                layoutInScreenMethod = declaredMethod;
                declaredMethod.setAccessible(true);
            }
            layoutInScreenMethod.invoke(this, Boolean.TRUE);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private void init() {
        final View contentView = getContentView();
        if ((contentView instanceof ActionBarPopupWindowLayout) && ((ActionBarPopupWindowLayout) contentView).getSwipeBack() != null) {
            setTouchInterceptor(new View.OnTouchListener() { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow$$ExternalSyntheticLambda2
                @Override // android.view.View.OnTouchListener
                public final boolean onTouch(View view, MotionEvent motionEvent) {
                    return this.f$0.lambda$init$1(contentView, view, motionEvent);
                }
            });
        }
        Field field = superListenerField;
        if (field != null) {
            try {
                this.mSuperScrollListener = (ViewTreeObserver.OnScrollChangedListener) field.get(this);
                field.set(this, NOP);
            } catch (Exception unused) {
                this.mSuperScrollListener = null;
            }
        }
    }

    public /* synthetic */ boolean lambda$init$1(View view, View view2, MotionEvent motionEvent) {
        if (motionEvent.getAction() != 0) {
            return false;
        }
        Drawable backgroundDrawable = ((ActionBarPopupWindowLayout) view).getBackgroundDrawable();
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(backgroundDrawable.getBounds());
        rectF.offset(view.getX(), view.getY());
        if (rectF.contains(motionEvent.getX(), motionEvent.getY())) {
            return false;
        }
        dismiss();
        return true;
    }

    public void setDismissAnimationDuration(int i) {
        this.dismissAnimationDuration = i;
    }

    public void unregisterListener() {
        ViewTreeObserver viewTreeObserver;
        if (this.mSuperScrollListener == null || (viewTreeObserver = this.mViewTreeObserver) == null) {
            return;
        }
        if (viewTreeObserver.isAlive()) {
            this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
        }
        this.mViewTreeObserver = null;
    }

    private void registerListener(View view) {
        if (this.mSuperScrollListener != null) {
            ViewTreeObserver viewTreeObserver = view.getWindowToken() != null ? view.getViewTreeObserver() : null;
            ViewTreeObserver viewTreeObserver2 = this.mViewTreeObserver;
            if (viewTreeObserver != viewTreeObserver2) {
                if (viewTreeObserver2 != null && viewTreeObserver2.isAlive()) {
                    this.mViewTreeObserver.removeOnScrollChangedListener(this.mSuperScrollListener);
                }
                this.mViewTreeObserver = viewTreeObserver;
                if (viewTreeObserver != null) {
                    viewTreeObserver.addOnScrollChangedListener(this.mSuperScrollListener);
                }
            }
        }
    }

    public void dimBehind() {
        dimBehind(0.2f);
    }

    public void dimBehind(float f) {
        View rootView = getContentView().getRootView();
        WindowManager windowManager = (WindowManager) getContentView().getContext().getSystemService("window");
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) rootView.getLayoutParams();
        layoutParams.flags |= 2;
        layoutParams.dimAmount = f;
        windowManager.updateViewLayout(rootView, layoutParams);
    }

    private void dismissDim() {
        View rootView = getContentView().getRootView();
        WindowManager windowManager = (WindowManager) getContentView().getContext().getSystemService("window");
        if (rootView.getLayoutParams() == null || !(rootView.getLayoutParams() instanceof WindowManager.LayoutParams)) {
            return;
        }
        WindowManager.LayoutParams layoutParams = (WindowManager.LayoutParams) rootView.getLayoutParams();
        try {
            int i = layoutParams.flags;
            if ((i & 2) != 0) {
                layoutParams.flags = i & (-3);
                layoutParams.dimAmount = 0.0f;
                windowManager.updateViewLayout(rootView, layoutParams);
            }
        } catch (Exception unused) {
        }
    }

    @Override // android.widget.PopupWindow
    public void showAsDropDown(View view, int i, int i2) {
        try {
            super.showAsDropDown(view, i, i2);
            registerListener(view);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static AnimatorSet startAnimation(final ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
        actionBarPopupWindowLayout.startAnimationPending = true;
        actionBarPopupWindowLayout.setTranslationY(0.0f);
        float f = 1.0f;
        actionBarPopupWindowLayout.setAlpha(1.0f);
        actionBarPopupWindowLayout.setPivotX(actionBarPopupWindowLayout.getMeasuredWidth());
        actionBarPopupWindowLayout.setPivotY(0.0f);
        int itemsCount = actionBarPopupWindowLayout.getItemsCount();
        actionBarPopupWindowLayout.positions.clear();
        int i = 0;
        for (int i2 = 0; i2 < itemsCount; i2++) {
            View itemAt = actionBarPopupWindowLayout.getItemAt(i2);
            if (!(itemAt instanceof GapView)) {
                itemAt.setAlpha(0.0f);
                if (itemAt.getVisibility() == 0) {
                    actionBarPopupWindowLayout.positions.put(itemAt, Integer.valueOf(i));
                    i++;
                }
            }
        }
        if (actionBarPopupWindowLayout.shownFromBottom) {
            actionBarPopupWindowLayout.lastStartedChild = itemsCount - 1;
        } else {
            actionBarPopupWindowLayout.lastStartedChild = 0;
        }
        if (actionBarPopupWindowLayout.getSwipeBack() != null) {
            actionBarPopupWindowLayout.getSwipeBack().invalidateTransforms();
            f = actionBarPopupWindowLayout.backScaleY;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow$$ExternalSyntheticLambda0
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                ActionBarPopupWindow.$r8$lambda$wAiJ8FMguTOFvn8w6tLNnCCkblA(actionBarPopupWindowLayout, valueAnimator);
            }
        });
        actionBarPopupWindowLayout.updateAnimation = false;
        actionBarPopupWindowLayout.clipChildren = true;
        animatorSet.playTogether(ObjectAnimator.ofFloat(actionBarPopupWindowLayout, "backScaleY", 0.0f, f), ObjectAnimator.ofInt(actionBarPopupWindowLayout, "backAlpha", 0, 255), valueAnimatorOfFloat);
        animatorSet.setDuration((i * 16) + 150);
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow.1
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                actionBarPopupWindowLayout.startAnimationPending = false;
                int itemsCount2 = actionBarPopupWindowLayout.getItemsCount();
                for (int i3 = 0; i3 < itemsCount2; i3++) {
                    View itemAt2 = actionBarPopupWindowLayout.getItemAt(i3);
                    if (!(itemAt2 instanceof GapView)) {
                        itemAt2.setTranslationY(0.0f);
                        itemAt2.setAlpha(itemAt2.isEnabled() ? 1.0f : 0.5f);
                    }
                }
            }
        });
        animatorSet.start();
        return animatorSet;
    }

    public static /* synthetic */ void $r8$lambda$wAiJ8FMguTOFvn8w6tLNnCCkblA(ActionBarPopupWindowLayout actionBarPopupWindowLayout, ValueAnimator valueAnimator) {
        int itemsCount = actionBarPopupWindowLayout.getItemsCount();
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        for (int i = 0; i < itemsCount; i++) {
            View itemAt = actionBarPopupWindowLayout.getItemAt(i);
            if (!(itemAt instanceof GapView)) {
                float fCascade = AndroidUtilities.cascade(fFloatValue, actionBarPopupWindowLayout.shownFromBottom ? (itemsCount - 1) - i : i, itemsCount, 4.0f);
                itemAt.setTranslationY((1.0f - fCascade) * AndroidUtilities.dp(-6.0f));
                itemAt.setAlpha(fCascade * (itemAt.isEnabled() ? 1.0f : 0.5f));
            }
        }
    }

    public void startAnimation() {
        ActionBarPopupWindowLayout actionBarPopupWindowLayout;
        if (this.animationEnabled && this.windowAnimatorSet == null) {
            ViewGroup viewGroup = (ViewGroup) getContentView();
            if (viewGroup instanceof ActionBarPopupWindowLayout) {
                actionBarPopupWindowLayout = (ActionBarPopupWindowLayout) viewGroup;
                actionBarPopupWindowLayout.startAnimationPending = true;
            } else {
                ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = null;
                for (int i = 0; i < viewGroup.getChildCount(); i++) {
                    if (viewGroup.getChildAt(i) instanceof ActionBarPopupWindowLayout) {
                        actionBarPopupWindowLayout2 = (ActionBarPopupWindowLayout) viewGroup.getChildAt(i);
                        actionBarPopupWindowLayout2.startAnimationPending = true;
                    }
                }
                actionBarPopupWindowLayout = actionBarPopupWindowLayout2;
            }
            actionBarPopupWindowLayout.setTranslationY(0.0f);
            float f = 1.0f;
            actionBarPopupWindowLayout.setAlpha(1.0f);
            actionBarPopupWindowLayout.setPivotX(actionBarPopupWindowLayout.getMeasuredWidth());
            actionBarPopupWindowLayout.setPivotY(0.0f);
            int itemsCount = actionBarPopupWindowLayout.getItemsCount();
            actionBarPopupWindowLayout.positions.clear();
            int i2 = 0;
            for (int i3 = 0; i3 < itemsCount; i3++) {
                View itemAt = actionBarPopupWindowLayout.getItemAt(i3);
                itemAt.setAlpha(0.0f);
                if (itemAt.getVisibility() == 0) {
                    actionBarPopupWindowLayout.positions.put(itemAt, Integer.valueOf(i2));
                    i2++;
                }
            }
            if (actionBarPopupWindowLayout.shownFromBottom) {
                actionBarPopupWindowLayout.lastStartedChild = itemsCount - 1;
            } else {
                actionBarPopupWindowLayout.lastStartedChild = 0;
            }
            if (actionBarPopupWindowLayout.getSwipeBack() != null) {
                actionBarPopupWindowLayout.getSwipeBack().invalidateTransforms();
                f = actionBarPopupWindowLayout.backScaleY;
            }
            AnimatorSet animatorSet = new AnimatorSet();
            this.windowAnimatorSet = animatorSet;
            animatorSet.playTogether(ObjectAnimator.ofFloat(actionBarPopupWindowLayout, "backScaleY", 0.0f, f), ObjectAnimator.ofInt(actionBarPopupWindowLayout, "backAlpha", 0, 255));
            this.windowAnimatorSet.setDuration((i2 * 16) + 150);
            this.windowAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ActionBarPopupWindowLayout actionBarPopupWindowLayout3;
                    ActionBarPopupWindowLayout actionBarPopupWindowLayout4 = null;
                    ActionBarPopupWindow.this.windowAnimatorSet = null;
                    ViewGroup viewGroup2 = (ViewGroup) ActionBarPopupWindow.this.getContentView();
                    if (viewGroup2 instanceof ActionBarPopupWindowLayout) {
                        actionBarPopupWindowLayout3 = (ActionBarPopupWindowLayout) viewGroup2;
                        actionBarPopupWindowLayout3.startAnimationPending = false;
                    } else {
                        for (int i4 = 0; i4 < viewGroup2.getChildCount(); i4++) {
                            if (viewGroup2.getChildAt(i4) instanceof ActionBarPopupWindowLayout) {
                                actionBarPopupWindowLayout4 = (ActionBarPopupWindowLayout) viewGroup2.getChildAt(i4);
                                actionBarPopupWindowLayout4.startAnimationPending = false;
                            }
                        }
                        actionBarPopupWindowLayout3 = actionBarPopupWindowLayout4;
                    }
                    int itemsCount2 = actionBarPopupWindowLayout3.getItemsCount();
                    for (int i5 = 0; i5 < itemsCount2; i5++) {
                        View itemAt2 = actionBarPopupWindowLayout3.getItemAt(i5);
                        if (!(itemAt2 instanceof GapView)) {
                            itemAt2.setAlpha(itemAt2.isEnabled() ? 1.0f : 0.5f);
                        }
                    }
                }
            });
            this.windowAnimatorSet.start();
        }
    }

    @Override // android.widget.PopupWindow
    public void update(View view, int i, int i2, int i3, int i4) {
        super.update(view, i, i2, i3, i4);
        registerListener(view);
    }

    @Override // android.widget.PopupWindow
    public void update(View view, int i, int i2) {
        super.update(view, i, i2);
        registerListener(view);
    }

    @Override // android.widget.PopupWindow
    public void showAtLocation(View view, int i, int i2, int i3) {
        super.showAtLocation(view, i, i2, i3);
        unregisterListener();
    }

    @Override // android.widget.PopupWindow
    public void dismiss() {
        dismiss(true);
    }

    public void setPauseNotifications(boolean z) {
        this.pauseNotifications = z;
    }

    public void dismiss(boolean z) {
        setFocusable(false);
        dismissDim();
        AnimatorSet animatorSet = this.windowAnimatorSet;
        ActionBarPopupWindowLayout actionBarPopupWindowLayout = null;
        if (animatorSet != null) {
            if (z && this.isClosingAnimated) {
                return;
            }
            animatorSet.cancel();
            this.windowAnimatorSet = null;
        }
        this.isClosingAnimated = false;
        if (this.animationEnabled && z) {
            this.isClosingAnimated = true;
            ViewGroup viewGroup = (ViewGroup) getContentView();
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                if (viewGroup.getChildAt(i) instanceof ActionBarPopupWindowLayout) {
                    actionBarPopupWindowLayout = (ActionBarPopupWindowLayout) viewGroup.getChildAt(i);
                }
            }
            if (actionBarPopupWindowLayout != null && actionBarPopupWindowLayout.itemAnimators != null && !actionBarPopupWindowLayout.itemAnimators.isEmpty()) {
                int size = actionBarPopupWindowLayout.itemAnimators.size();
                for (int i2 = 0; i2 < size; i2++) {
                    AnimatorSet animatorSet2 = (AnimatorSet) actionBarPopupWindowLayout.itemAnimators.get(i2);
                    animatorSet2.removeAllListeners();
                    animatorSet2.cancel();
                }
                actionBarPopupWindowLayout.itemAnimators.clear();
            }
            AnimatorSet animatorSet3 = new AnimatorSet();
            this.windowAnimatorSet = animatorSet3;
            if (this.outEmptyTime > 0) {
                animatorSet3.playTogether(ValueAnimator.ofFloat(0.0f, 1.0f));
                this.windowAnimatorSet.setDuration(this.outEmptyTime);
            } else if (this.scaleOut) {
                animatorSet3.playTogether(ObjectAnimator.ofFloat(viewGroup, (Property<ViewGroup, Float>) View.SCALE_Y, 0.8f), ObjectAnimator.ofFloat(viewGroup, (Property<ViewGroup, Float>) View.SCALE_X, 0.8f), ObjectAnimator.ofFloat(viewGroup, (Property<ViewGroup, Float>) View.ALPHA, 0.0f));
                this.windowAnimatorSet.setDuration(this.dismissAnimationDuration);
            } else {
                animatorSet3.playTogether(ObjectAnimator.ofFloat(viewGroup, (Property<ViewGroup, Float>) View.TRANSLATION_Y, AndroidUtilities.dp((actionBarPopupWindowLayout == null || !actionBarPopupWindowLayout.shownFromBottom) ? -5.0f : 5.0f)), ObjectAnimator.ofFloat(viewGroup, (Property<ViewGroup, Float>) View.ALPHA, 0.0f));
                this.windowAnimatorSet.setDuration(this.dismissAnimationDuration);
            }
            this.windowAnimatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarPopupWindow.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ActionBarPopupWindow.this.windowAnimatorSet = null;
                    ActionBarPopupWindow.this.isClosingAnimated = false;
                    ActionBarPopupWindow.this.setFocusable(false);
                    try {
                        ActionBarPopupWindow.super.dismiss();
                    } catch (Exception unused) {
                    }
                    ActionBarPopupWindow.this.unregisterListener();
                    if (ActionBarPopupWindow.this.pauseNotifications) {
                        ActionBarPopupWindow.this.notificationsLocker.unlock();
                    }
                }
            });
            if (this.pauseNotifications) {
                this.notificationsLocker.lock();
            }
            this.windowAnimatorSet.start();
            return;
        }
        try {
            super.dismiss();
        } catch (Exception unused) {
        }
        unregisterListener();
    }

    public static class GapView extends FrameLayout {
        boolean dividerVisible;
        Drawable shadowDrawable;

        public GapView(Context context, Theme.ResourcesProvider resourcesProvider) {
            this(context, resourcesProvider, Theme.key_actionBarDefaultSubmenuSeparator);
        }

        public GapView(Context context, int i, int i2) {
            super(context);
            this.dividerVisible = true;
            this.shadowDrawable = Theme.getThemedDrawable(getContext(), R.drawable.greydivider, i2);
            setBackgroundColor(i);
        }

        public GapView(Context context, Theme.ResourcesProvider resourcesProvider, int i) {
            this(context, Theme.getColor(i, resourcesProvider), Theme.getColor(Theme.key_windowBackgroundGrayShadow, resourcesProvider));
        }

        public void setColor(int i) {
            setBackgroundColor(i);
        }

        public void setDividerVisible(boolean z) {
            this.dividerVisible = z;
            invalidate();
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            Drawable drawable;
            super.onDraw(canvas);
            if (!this.dividerVisible || (drawable = this.shadowDrawable) == null) {
                return;
            }
            drawable.setBounds(0, 0, getWidth(), getHeight());
            this.shadowDrawable.draw(canvas);
        }
    }
}
