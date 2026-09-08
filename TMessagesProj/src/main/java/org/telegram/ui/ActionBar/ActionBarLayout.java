package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.util.Property;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.RoundedCorner;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowInsets;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import androidx.annotation.Keep;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.math.MathUtils;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.utils.AppUtils;
import com.exteragram.messenger.utils.system.VibratorUtils;
import com.exteragram.messenger.utils.ui.PredictiveBackAnimationHelper;
import com.google.android.material.slider.Slider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.utils.ViewOutlineProviderImpl;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.BackButtonMenu;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugController;
import org.telegram.ui.Components.FloatingDebug.FloatingDebugProvider;
import org.telegram.ui.Components.GroupCallPip;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.SlideChooseView;
import org.telegram.ui.EmptyBaseFragment;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.MainTabsActivity;
import org.telegram.ui.Stories.StoryViewer;
import org.telegram.ui.bots.BotWebViewSheet;

public class ActionBarLayout extends FrameLayout implements INavigationLayout, FloatingDebugProvider {
    private static Paint scrimPaint;
    private AccelerateDecelerateInterpolator accelerateDecelerateInterpolator;
    private ArrayList<int[]> animateEndColors;
    private int animateSetThemeAccentIdAfterAnimation;
    private Theme.ThemeInfo animateSetThemeAfterAnimation;
    private boolean animateSetThemeAfterAnimationApply;
    private boolean animateSetThemeNightAfterAnimation;
    private ArrayList<int[]> animateStartColors;
    private boolean animateThemeAfterAnimation;
    protected boolean animationInProgress;
    private float animationProgress;
    public INavigationLayout.ThemeAnimationSettings.onAnimationProgress animationProgressListener;
    private Runnable animationRunnable;
    private boolean attached;
    private AnimatorSet backAnimator;
    private boolean backAnimatorIsBack;
    private View backgroundView;
    private boolean beginTrackingSent;
    private BottomSheetTabs bottomSheetTabs;
    private BottomSheetTabs.ClipTools bottomSheetTabsClip;
    private float cachedBottomLeftRadius;
    private float cachedBottomRightRadius;
    private float cachedTopLeftRadius;
    private float cachedTopRightRadius;
    private final Path clipPath;
    public LayoutContainer containerView;
    public LayoutContainer containerViewBack;
    private ActionBar currentActionBar;
    private AnimatorSet currentAnimation;
    private int currentNavigationBarColor;
    private SpringAnimation currentSpringAnimation;
    Runnable debugBlackScreenRunnable;
    private DecelerateInterpolator decelerateInterpolator;
    private boolean delayedAnimationResumed;
    private Runnable delayedOpenAnimationRunnable;
    private INavigationLayout.INavigationLayoutDelegate delegate;
    private DrawerLayoutContainer drawerLayoutContainer;
    private List<BaseFragment> fragmentsStack;
    private final AnimatedFloat hasSheetsAnimator;
    public boolean highlightActionButtons;
    private boolean inActionMode;
    private boolean inBubbleMode;
    private boolean inPreviewMode;
    public float innerTranslationX;
    private final Runnable invalidateRunnable;
    public boolean isKeyboardVisible;
    private boolean isLayersLayout;
    private boolean isRightLayout;
    private boolean isSheet;
    ArrayList<String> lastActions;
    private long lastFrameTime;
    private WindowInsetsCompat lastWindowInsetsCompat;
    private View layoutToIgnore;
    private final boolean main;
    private boolean maybeStartTracking;
    private int[] measureSpec;
    public Theme.MessageDrawable messageDrawableOutMediaStart;
    public Theme.MessageDrawable messageDrawableOutStart;
    private int navigationBarInsetHeight;
    private BaseFragment newFragment;
    AnimationNotificationsLocker notificationsLocker;
    private BaseFragment oldFragment;
    private Runnable onCloseAnimationEndRunnable;
    private Runnable onFragmentStackChangedListener;
    private Runnable onOpenAnimationEndRunnable;
    private boolean openingAnimation;
    private Runnable overlayAction;
    private int overrideWidthOffset;
    private OvershootInterpolator overshootInterpolator;
    protected Activity parentActivity;
    private final PredictiveBackAnimationHelper predictiveBackAnimation;
    private Drawable predictiveBackBackgroundDrawable;
    private boolean predictiveBackHasProgress;
    private boolean predictiveBackInProgress;
    private boolean predictiveBackLeft;
    private final RectF predictiveBackRect;
    private float predictiveBackY;
    private boolean predictiveInput;
    private ArrayList<ThemeDescription> presentingFragmentDescriptions;
    private ColorDrawable previewBackgroundDrawable;
    private boolean previewFinishInProgress;
    private ActionBarPopupWindow.ActionBarPopupWindowLayout previewMenu;
    private boolean previewOpenAnimationInProgress;
    private List<BackButtonMenu.PulledDialog> pulledDialogs;
    private float[] radii;
    private boolean rebuildAfterAnimation;
    private boolean rebuildLastAfterAnimation;
    private Rect rect;
    private final Runnable relayoutRunnable;
    private boolean removeActionBarExtraHeight;
    private int savedBottomSheetTabsTop;
    public LayoutContainer sheetContainer;
    private EmptyBaseFragment sheetFragment;
    private boolean showLastAfterAnimation;
    private SpringForce springForce;
    private boolean springIsBack;
    private boolean springIsLayout;
    private boolean springIsPreview;
    private Drawable springRouteBackgroundDrawable;
    private float springRouteYRatio;
    private FloatValueHolder springValueHolder;
    INavigationLayout.StartColorsProvider startColorsProvider;
    protected boolean startedTracking;
    private int startedTrackingPointerId;
    private int startedTrackingX;
    private int startedTrackingY;
    private int statusBarInsetHeight;
    private boolean tabsEvents;
    private float themeAnimationValue;
    private ArrayList<ThemeDescription.ThemeDescriptionDelegate> themeAnimatorDelegate;
    private ArrayList<ArrayList<ThemeDescription>> themeAnimatorDescriptions;
    private AnimatorSet themeAnimatorSet;
    private String titleOverlayText;
    private int titleOverlayTextId;
    private boolean transitionAnimationInProgress;
    private boolean transitionAnimationPreviewMode;
    private long transitionAnimationStartTime;
    private boolean useAlphaAnimations;
    private VelocityTracker velocityTracker;
    private Runnable waitingForKeyboardCloseRunnable;
    private Window window;
    private boolean withShadow;

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.ActionBar.ActionBarLayout.onBackStarted(float, float):boolean");
    }

    public void onBackProgress(float f, float f2) {
        if (this.predictiveInput) {
            applyPredictiveBackProgress(f, f2, true);
        }
    }

    private void applyPredictiveBackProgress(float f, float f2, boolean z) {
        float fAdjustPredictiveBackProgress = z ? adjustPredictiveBackProgress(f) : Utilities.clamp01(f);
        this.predictiveBackHasProgress = fAdjustPredictiveBackProgress > 0.0f || this.animationInProgress;
        this.predictiveBackY = f2;
        this.predictiveBackAnimation.update(fAdjustPredictiveBackProgress, f2);
        this.containerView.setTranslationX(0.0f);
        this.containerViewBack.setTranslationX(0.0f);
        setInnerTranslationX(this.predictiveBackAnimation.getSlideDistance());
        this.containerView.invalidate();
        this.containerViewBack.invalidate();
    }

    private float adjustPredictiveBackProgress(float f) {
        return Utilities.clamp01(f * Utilities.clamp(ExteraConfig.getPredictiveBackIntensity(), 2.0f, 0.0f));
    }

    public void onBackCancelled() {
        if (this.predictiveInput) {
            this.predictiveInput = false;
            animateBackEndAnimation(true);
        }
    }

    public void onBackInvoked() {
        if (!this.predictiveInput) {
            onBackPressed();
        } else {
            this.predictiveInput = false;
            animateBackEndAnimation(false);
        }
    }

    private boolean newBackTransitions() {
        return this.predictiveBackInProgress && this.predictiveBackHasProgress;
    }

    private float getPredictiveBackStartCornerRadius() {
        return Math.max(Math.max(this.cachedTopLeftRadius, this.cachedTopRightRadius), Math.max(this.cachedBottomLeftRadius, this.cachedBottomRightRadius));
    }

    private void animateBackEndAnimation(final boolean z) {
        BaseFragment baseFragment;
        Animator customSlideTransition;
        if (this.fragmentsStack.isEmpty()) {
            baseFragment = null;
        } else {
            List<BaseFragment> list = this.fragmentsStack;
            baseFragment = list.get(list.size() - 1);
        }
        if (baseFragment == null) {
            return;
        }
        float x = this.containerView.getX();
        if (this.predictiveBackInProgress && this.predictiveBackHasProgress) {
            animatePredictiveBackEndAnimation(z);
            return;
        }
        if (ExteraConfig.getSpringAnimations()) {
            this.springIsLayout = false;
            this.springIsPreview = false;
            this.springIsBack = z;
            float measuredWidth = (x / this.containerView.getMeasuredWidth()) * 1000.0f;
            FloatValueHolder floatValueHolder = this.springValueHolder;
            if (floatValueHolder == null) {
                this.springValueHolder = new FloatValueHolder(measuredWidth);
            } else {
                floatValueHolder.setValue(measuredWidth);
            }
            if (this.currentSpringAnimation == null) {
                this.currentSpringAnimation = new SpringAnimation(this.springValueHolder);
                SpringForce springForce = new SpringForce();
                this.springForce = springForce;
                this.currentSpringAnimation.setSpring(springForce);
                this.currentSpringAnimation.addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: org.telegram.ui.ActionBar.ActionBarLayout$$ExternalSyntheticLambda22
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
                    public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                        this.f$0.lambda$animateBackEndAnimation$2(dynamicAnimation, f, f2);
                    }
                });
                this.currentSpringAnimation.addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: org.telegram.ui.ActionBar.ActionBarLayout$$ExternalSyntheticLambda23
                    @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
                    public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f, float f2) {
                        this.f$0.lambda$animateBackEndAnimation$3(dynamicAnimation, z2, f, f2);
                    }
                });
            }
            this.springForce.setFinalPosition(z ? 0.0f : 1000.0f);
            this.springForce.setStiffness(900.0f);
            this.springForce.setDampingRatio(1.0f);
            this.currentSpringAnimation.setStartVelocity(0.0f);
            this.currentSpringAnimation.start();
            this.animationInProgress = true;
            this.layoutToIgnore = this.containerViewBack;
            return;
        }
        AnimatorSet animatorSet = new AnimatorSet();
        boolean zShouldOverrideSlideTransition = baseFragment.shouldOverrideSlideTransition(false, z);
        LayoutContainer layoutContainer = this.containerView;
        Property property = View.TRANSLATION_X;
        if (!z) {
            x = Math.abs(layoutContainer.getMeasuredWidth() - x);
            int iMax = Math.max((int) ((200.0f / this.containerView.getMeasuredWidth()) * x), newBackTransitions() ? 380 : 50);
            if (!zShouldOverrideSlideTransition) {
                LayoutContainer layoutContainer2 = this.containerView;
                long j = iMax;
                animatorSet.playTogether(ObjectAnimator.ofFloat(layoutContainer2, (Property<LayoutContainer, Float>) property, layoutContainer2.getMeasuredWidth() + (this.predictiveBackInProgress ? AndroidUtilities.dp(56.0f) : 0)).setDuration(j), ObjectAnimator.ofFloat(this, "innerTranslationX", this.containerView.getMeasuredWidth()).setDuration(j));
                if (newBackTransitions()) {
                    animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    if (ExteraConfig.getSpringAnimations()) {
                        animatorSet.play(ObjectAnimator.ofFloat(this.containerViewBack, (Property<LayoutContainer, Float>) property, 0.0f).setDuration(j));
                    }
                }
            }
        } else {
            int iMax2 = Math.max((int) ((320.0f / layoutContainer.getMeasuredWidth()) * x), newBackTransitions() ? 320 : 120);
            if (!zShouldOverrideSlideTransition) {
                long j2 = iMax2;
                animatorSet.playTogether(ObjectAnimator.ofFloat(this.containerView, (Property<LayoutContainer, Float>) property, 0.0f).setDuration(j2), ObjectAnimator.ofFloat(this, "innerTranslationX", 0.0f).setDuration(j2));
                if (newBackTransitions()) {
                    animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                }
                if (ExteraConfig.getSpringAnimations()) {
                    animatorSet.play(ObjectAnimator.ofFloat(this.containerViewBack, (Property<LayoutContainer, Float>) property, (-this.containerView.getMeasuredWidth()) * 0.35f).setDuration(j2));
                }
            }
        }
        Animator customSlideTransition2 = baseFragment.getCustomSlideTransition(false, z, x);
        if (customSlideTransition2 != null) {
            animatorSet.playTogether(customSlideTransition2);
        }
        List<BaseFragment> list2 = this.fragmentsStack;
        BaseFragment baseFragment2 = list2.get(list2.size() - 2);
        if (baseFragment2 != null && (customSlideTransition = baseFragment2.getCustomSlideTransition(false, z, x)) != null) {
            animatorSet.playTogether(customSlideTransition);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBarLayout.2
            private boolean cancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                this.cancelled = true;
                ActionBarLayout.this.predictiveBackInProgress = false;
                ActionBarLayout.this.containerView.setAlpha(1.0f);
                ActionBarLayout.this.onSlideAnimationEnd(true);
                ActionBarLayout.this.backAnimator = null;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (this.cancelled) {
                    return;
                }
                ActionBarLayout.this.predictiveBackInProgress = false;
                ActionBarLayout.this.containerView.setAlpha(1.0f);
                ActionBarLayout.this.onSlideAnimationEnd(z);
                ActionBarLayout.this.backAnimator = null;
            }
        });
        this.backAnimator = animatorSet;
        this.backAnimatorIsBack = z;
        this.animationInProgress = true;
        this.layoutToIgnore = this.containerViewBack;
        animatorSet.start();
    }

    public @Override // org.telegram.ui.Components.FloatingDebug.FloatingDebugProvider
    public List<FloatingDebugController.DebugItem> onGetDebugItems() {
        BaseFragment lastFragment = getLastFragment();
        if (lastFragment != 0) {
            ArrayList arrayList = new ArrayList();
            if (lastFragment instanceof FloatingDebugProvider) {
                arrayList.addAll(((FloatingDebugProvider) lastFragment).onGetDebugItems());
            }
            observeDebugItemsFromView(arrayList, lastFragment.getFragmentView());
            return arrayList;
        }
        return Collections.EMPTY_LIST;
    }

    /* JADX WARN: Multi-variable type inference failed */
    private void observeDebugItemsFromView(List<FloatingDebugController.DebugItem> list, View view) {
        if (view instanceof FloatingDebugProvider) {
            list.addAll(((FloatingDebugProvider) view).onGetDebugItems());
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                observeDebugItemsFromView(list, viewGroup.getChildAt(i));
            }
        }
    }

    public static View findScrollingChild(ViewGroup viewGroup, float f, float f2) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.getVisibility() == 0) {
                Rect rect = AndroidUtilities.rectTmp2;
                childAt.getHitRect(rect);
                if (rect.contains((int) f, (int) f2) && (childAt.canScrollHorizontally(-1) || (childAt instanceof SeekBarView) || (childAt instanceof SlideChooseView) || (childAt instanceof Slider) || ((childAt instanceof ViewGroup) && (childAt = findScrollingChild((ViewGroup) childAt, f - rect.left, f2 - rect.top)) != null))) {
                    return childAt;
                }
            }
        }
        return null;
    }

    public /* synthetic */ void lambda$new$21() {
        if (this.attached && getLastFragment() != null && this.containerView.getChildCount() == 0) {
            if (BuildVars.DEBUG_VERSION) {
                FileLog.e(new RuntimeException(TextUtils.join(", ", this.lastActions)));
            }
            rebuildAllFragmentViews(true, true);
        }
    }

    public void checkBlackScreen(String str) {
        if (BuildVars.DEBUG_VERSION) {
            this.lastActions.add(0, str + " " + this.fragmentsStack.size());
            if (this.lastActions.size() > 20) {
                ArrayList<String> arrayList = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    arrayList.add(this.lastActions.get(i));
                }
                this.lastActions = arrayList;
            }
        }
        AndroidUtilities.cancelRunOnUIThread(this.debugBlackScreenRunnable);
        AndroidUtilities.runOnUIThread(this.debugBlackScreenRunnable, 500L);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
    }

    public int measureKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(this.rect);
        Rect rect = this.rect;
        if (rect.bottom == 0 && rect.top == 0) {
            return 0;
        }
        int height = (rootView.getHeight() - (this.rect.top != 0 ? AndroidUtilities.statusBarHeight : 0)) - AndroidUtilities.getViewInset(rootView);
        Rect rect2 = this.rect;
        return Math.max(0, height - (rect2.bottom - rect2.top));
    }

    /* JADX WARN: Code duplicated, block: B:14:0x0037  */
    /* JADX WARN: Code duplicated, block: B:25:0x005f A[PHI: r3
  0x005f: PHI (r3v5 org.telegram.ui.ActionBar.BaseFragment$AttachedSheet) = 
  (r3v4 org.telegram.ui.ActionBar.BaseFragment$AttachedSheet)
  (r3v4 org.telegram.ui.ActionBar.BaseFragment$AttachedSheet)
  (r3v4 org.telegram.ui.ActionBar.BaseFragment$AttachedSheet)
  (r3v8 org.telegram.ui.ActionBar.BaseFragment$AttachedSheet)
 binds: [B:15:0x0038, B:17:0x003e, B:19:0x0048, B:23:0x005c] A[DONT_GENERATE, DONT_INLINE]] */
    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        BaseFragment.AttachedSheet lastSheet;
        boolean z = motionEvent.getY() > ((float) ((getHeight() - getBottomTabsHeight(true)) - this.navigationBarInsetHeight));
        EmptyBaseFragment emptyBaseFragment = this.sheetFragment;
        BaseFragment.AttachedSheet attachedSheet = null;
        if (emptyBaseFragment == null || emptyBaseFragment.getLastSheet() == null) {
            lastSheet = null;
        } else {
            lastSheet = this.sheetFragment.getLastSheet();
            if (!lastSheet.attachedToParent() || lastSheet.mo6342getWindowView() == null) {
                lastSheet = null;
            }
        }
        if (lastSheet != null || getLastFragment() == null || getLastFragment().getLastSheet() == null) {
            attachedSheet = lastSheet;
        } else {
            lastSheet = getLastFragment().getLastSheet();
            if (lastSheet.attachedToParent() && lastSheet.mo6342getWindowView() != null) {
                attachedSheet = lastSheet;
            }
        }
        if (attachedSheet != null) {
            if (motionEvent.getAction() == 0) {
                this.tabsEvents = z;
            }
            if (!this.tabsEvents) {
                if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                    this.tabsEvents = false;
                }
                return attachedSheet.mo6342getWindowView().dispatchTouchEvent(motionEvent);
            }
        }
        if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
            this.tabsEvents = false;
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setWindow(Window window) {
        this.window = window;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public Window getWindow() {
        Window window = this.window;
        if (window != null) {
            return window;
        }
        if (getParentActivity() != null) {
            return getParentActivity().getWindow();
        }
        return null;
    }

    public BottomSheetTabs getBottomSheetTabs() {
        return this.bottomSheetTabs;
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public void setNavigationBarColor(int i) {
        if (this.currentNavigationBarColor != i) {
            this.currentNavigationBarColor = i;
            invalidate();
        }
        DrawerLayoutContainer drawerLayoutContainer = this.drawerLayoutContainer;
        if (drawerLayoutContainer != null) {
            drawerLayoutContainer.setInternalNavigationBarColor(i);
        }
        BottomSheetTabs bottomSheetTabs = this.bottomSheetTabs;
        if (bottomSheetTabs != null) {
            bottomSheetTabs.setNavigationBarColor(i, (this.startedTracking || this.animationInProgress) ? false : true);
        }
    }

    public void relayout() {
        requestLayout();
        this.containerView.requestLayout();
        this.containerViewBack.requestLayout();
        this.sheetContainer.requestLayout();
    }

    @Override // org.telegram.ui.ActionBar.INavigationLayout
    public int getBottomTabsHeight(boolean z) {
        BottomSheetTabs bottomSheetTabs;
        if (!this.main || (bottomSheetTabs = this.bottomSheetTabs) == null) {
            return 0;
        }
        return bottomSheetTabs.getHeight(z);
    }

    @Override // android.view.ViewGroup
    public void addView(View view, int i, ViewGroup.LayoutParams layoutParams) {
        super.addView(view, i, layoutParams);
        WindowInsetsCompat windowInsetsCompat = this.lastWindowInsetsCompat;
        if (windowInsetsCompat != null) {
            dispatchApplyWindowInsetsInternal(view, windowInsetsCompat);
        }
        BottomSheetTabs bottomSheetTabs = this.bottomSheetTabs;
        if (bottomSheetTabs == null || indexOfChild(bottomSheetTabs) >= getChildCount() - 1) {
            return;
        }
        this.bottomSheetTabs.bringToFront();
    }

    private void dispatchApplyWindowInsetsInternal(View view, WindowInsetsCompat windowInsetsCompat) {
        if (this.isLayersLayout) {
            if ((view instanceof LayoutContainer) && ((LayoutContainer) view).isSupportEdgeToEdge) {
                int i = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.ime()).bottom;
                View view2 = getParent() instanceof View ? (View) getParent() : null;
                ViewCompat.dispatchApplyWindowInsets(view, new WindowInsetsCompat.Builder(WindowInsetsCompat.CONSUMED).setInsets(WindowInsetsCompat.Type.ime(), Insets.of(0, 0, 0, Math.max(0, i - (view2 != null ? Math.max(0, view2.getHeight() - getBottom()) : 0)))).build());
                return;
            }
            ViewCompat.dispatchApplyWindowInsets(view, WindowInsetsCompat.CONSUMED);
            return;
        }
        Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars());
        Insets insets2 = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.ime() | WindowInsetsCompat.Type.displayCutout());
        if (view instanceof BottomSheetTabs) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            int i2 = marginLayoutParams.bottomMargin;
            int i3 = insets.bottom;
            if (i2 != i3) {
                marginLayoutParams.bottomMargin = i3;
                view.requestLayout();
                return;
            }
            return;
        }
        if (view instanceof LayoutContainer) {
            ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            LayoutContainer layoutContainer = (LayoutContainer) view;
            int bottomTabsHeight = getBottomTabsHeight(false);
            int i4 = bottomTabsHeight > 0 ? insets.bottom + bottomTabsHeight : 0;
            if (layoutContainer.isSupportEdgeToEdge) {
                if (marginLayoutParams2.bottomMargin != i4) {
                    marginLayoutParams2.bottomMargin = i4;
                    view.requestLayout();
                }
                ViewCompat.dispatchApplyWindowInsets(view, windowInsetsCompat.inset(0, 0, 0, marginLayoutParams2.bottomMargin));
                return;
            }
            int iMax = Math.max(i4, insets2.bottom);
            if (marginLayoutParams2.bottomMargin != iMax) {
                marginLayoutParams2.bottomMargin = iMax;
                view.requestLayout();
            }
            ViewCompat.dispatchApplyWindowInsets(view, WindowInsetsCompat.CONSUMED);
        }
    }

    public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
        WindowInsets windowInsets;
        Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars());
        this.lastWindowInsetsCompat = windowInsetsCompat;
        this.navigationBarInsetHeight = insets.bottom;
        this.statusBarInsetHeight = insets.top;
        if (Build.VERSION.SDK_INT >= 31 && (windowInsets = windowInsetsCompat.toWindowInsets()) != null) {
            RoundedCorner roundedCorner = windowInsets.getRoundedCorner(0);
            RoundedCorner roundedCorner2 = windowInsets.getRoundedCorner(1);
            RoundedCorner roundedCorner3 = windowInsets.getRoundedCorner(2);
            RoundedCorner roundedCorner4 = windowInsets.getRoundedCorner(3);
            this.cachedTopLeftRadius = roundedCorner == null ? 0.0f : roundedCorner.getRadius();
            this.cachedTopRightRadius = roundedCorner2 == null ? 0.0f : roundedCorner2.getRadius();
            this.cachedBottomRightRadius = roundedCorner3 == null ? 0.0f : roundedCorner3.getRadius();
            this.cachedBottomLeftRadius = roundedCorner4 != null ? roundedCorner4.getRadius() : 0.0f;
        }
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            dispatchApplyWindowInsetsInternal(getChildAt(i), windowInsetsCompat);
        }
        return WindowInsetsCompat.CONSUMED;
    }
}
