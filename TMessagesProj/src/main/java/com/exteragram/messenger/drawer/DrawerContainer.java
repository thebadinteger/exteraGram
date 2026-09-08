package com.exteragram.messenger.drawer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.RoundedCorner;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.math.MathUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.badges.BadgesController;
import com.exteragram.messenger.utils.AppUtils;
import java.util.function.Consumer;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.DrawerLayoutContainer;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.MainTabsActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.ProxyListActivity;
import org.telegram.ui.SelectAnimatedEmojiDialog;
import org.telegram.ui.ThemeActivity;

public class DrawerContainer extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private static final int COLOR_KEY_DRAWER_BACKGROUND = Theme.key_windowBackgroundWhite;
    private static final int COLOR_KEY_POPUP_ACCENT = Theme.key_windowBackgroundWhiteBlueIcon;
    private static final FloatPropertyCompat<DrawerContainer> DRAWER_OFFSET = new FloatPropertyCompat<DrawerContainer>("drawerOffset") { 
        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public float getValue(DrawerContainer drawerContainer) {
            return drawerContainer.getDrawerOffset();
        }

        @Override // androidx.dynamicanimation.animation.FloatPropertyCompat
        public void setValue(DrawerContainer drawerContainer, float f) {
            drawerContainer.setDrawerOffset(f);
        }
    };
    private final DrawerAccountPickerView accountPickerView;
    private final FrameLayout bulletinContainer;
    private float cachedBottomRightRadius;
    private float cachedTopRightRadius;
    private final Path clipPath;
    private final FrameLayout drawerPanel;
    private int drawerWidth;
    private final DrawerHeaderView headerView;
    private boolean isAnimating;
    private boolean isOpen;
    private final DrawerMenuView menuView;
    private View navigationTranslationTarget;
    private boolean notificationsRegistered;
    private boolean predictiveBackInProgress;
    private float predictiveBackStartProgress;
    private float progress;
    private final float[] radii;
    private final Rect rect;
    private final Paint scrimPaint;
    private SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialog;
    private SpringAnimation springAnimation;
    private ValueAnimator standardAnimator;
    private float startProgress;
    private float startX;
    private float startY;
    private boolean startedEdgeSwipe;
    private boolean tapClosePending;
    private boolean tracking;
    private VelocityTracker velocityTracker;

    public DrawerContainer(Context context) {
        super(context);
        this.scrimPaint = new Paint();
        this.rect = new Rect();
        this.clipPath = new Path();
        this.radii = new float[8];
        this.cachedTopRightRadius = -1.0f;
        this.cachedBottomRightRadius = -1.0f;
        setVisibility(8);
        setTag("drawer_container");
        this.drawerWidth = calculateDrawerWidth();
        FrameLayout frameLayout = new FrameLayout(context);
        this.drawerPanel = frameLayout;
        frameLayout.setBackgroundColor(Theme.getColor(COLOR_KEY_DRAWER_BACKGROUND));
        frameLayout.setTranslationX(-this.drawerWidth);
        addView(frameLayout, LayoutHelper.createFrame(-1, -1, 3));
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams.width = this.drawerWidth;
        frameLayout.setLayoutParams(layoutParams);
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        frameLayout.addView(linearLayout, LayoutHelper.createFrame(-1, -1.0f));
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.bulletinContainer = frameLayout2;
        frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, -1.0f));
        DrawerHeaderView drawerHeaderView = new DrawerHeaderView(context);
        this.headerView = drawerHeaderView;
        linearLayout.addView(drawerHeaderView, new LinearLayout.LayoutParams(-1, AndroidUtilities.dp(160.0f)));
        DrawerAccountPickerView drawerAccountPickerView = new DrawerAccountPickerView(context);
        this.accountPickerView = drawerAccountPickerView;
        linearLayout.addView(drawerAccountPickerView, new LinearLayout.LayoutParams(-1, -2));
        DrawerMenuView drawerMenuView = new DrawerMenuView(context);
        this.menuView = drawerMenuView;
        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(-1, 0);
        layoutParams2.weight = 1.0f;
        linearLayout.addView(drawerMenuView, layoutParams2);
        setupCallbacks();
        drawerHeaderView.setChevronExpanded(drawerAccountPickerView.isExpanded());
    }

    private void setupCallbacks() {
        this.headerView.setOnChevronClick(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setupCallbacks$0();
            }
        });
        this.headerView.setOnThemeToggle(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setupCallbacks$2();
            }
        });
        this.headerView.setOnThemeToggleLongClick(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setupCallbacks$4();
            }
        });
        this.headerView.setOnNavigateToProfile(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setupCallbacks$6();
            }
        });
        this.headerView.setOnStatusClick(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.showStatusSelect();
            }
        });
        this.headerView.setOnBadgeClick(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.showBadgeSelect();
            }
        });
        this.headerView.setOnProxyClick(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setupCallbacks$8();
            }
        });
        this.accountPickerView.setOnAccountSelected(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setupCallbacks$9();
            }
        });
        this.accountPickerView.setOnAccountLongClick(new DrawerAccountPickerView.OnAccountLongClick() { 
            @Override 
            public final void onLongClick(int i, View view) {
                this.f$0.lambda$setupCallbacks$10(i, view);
            }
        });
        this.menuView.setOnItemClick(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setupCallbacks$11();
            }
        });
    }

    public void lambda$setupCallbacks$4() {
        closeDrawer(true);
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setupCallbacks$3();
            }
        }, 200L);
    }

    public void lambda$setupCallbacks$5() {
        BaseFragment lastFragment = getLastFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", UserConfig.getInstance(UserConfig.selectedAccount).getClientUserId());
        bundle.putBoolean("my_profile", true);
        if (lastFragment != null) {
            lastFragment.presentFragment(new ProfileActivity(bundle));
        }
    }

    public void lambda$setupCallbacks$7() {
        BaseFragment lastFragment = getLastFragment();
        if (lastFragment != null) {
            lastFragment.presentFragment(new ProxyListActivity());
        }
    }

    public BadgeDTO val$defaultBadge;
        final void lambda$onEmojiSelected$1(final BaseFragment baseFragment, final String str) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onEmojiSelected$0(str, baseFragment);
                }
            });
        }

        public void lambda$animateProgress$12(float f, float f2, DynamicAnimation dynamicAnimation, boolean z, float f3, float f4) {
        if (this.springAnimation == dynamicAnimation) {
            this.springAnimation = null;
        }
        if (z) {
            return;
        }
        this.isAnimating = false;
        setDrawerOffset(f);
        if (f2 == 0.0f) {
            onCloseComplete();
        }
    }

    public /* synthetic */ void lambda$animateProgress$13(ValueAnimator valueAnimator) {
        setDrawerOffset(((Float) valueAnimator.getAnimatedValue()).floatValue());
    }

    private long getAnimationDuration(float f, boolean z) {
        if (!z) {
            return 300L;
        }
        float drawerOffset = getDrawerOffset();
        if (f > drawerOffset) {
            drawerOffset = this.drawerWidth - drawerOffset;
        }
        return Math.max((long) ((250.0f / Math.max(this.drawerWidth, 1)) * drawerOffset), 100L);
    }

    private void cancelAnimations() {
        SpringAnimation springAnimation = this.springAnimation;
        if (springAnimation != null) {
            this.springAnimation = null;
            springAnimation.cancel();
        }
        ValueAnimator valueAnimator = this.standardAnimator;
        if (valueAnimator != null) {
            this.standardAnimator = null;
            valueAnimator.cancel();
        }
        this.isAnimating = false;
        setProgress(this.progress);
        if (this.isOpen || this.progress > 0.001f || this.tracking || this.startedEdgeSwipe) {
            return;
        }
        onCloseComplete();
    }

    public void onCloseComplete() {
        this.isOpen = false;
        this.tracking = false;
        this.startedEdgeSwipe = false;
        this.predictiveBackInProgress = false;
        this.predictiveBackStartProgress = 0.0f;
        setProgress(0.0f);
        this.tapClosePending = false;
        dismissSelectionPopup();
        this.menuView.clearMenu();
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (isClosingAnimationInProgress()) {
            return !shouldPassClosingTouchThrough(motionEvent);
        }
        if (motionEvent.getAction() == 0) {
            if (this.isAnimating) {
                cancelAnimations();
            }
            this.startX = motionEvent.getX();
            this.startY = motionEvent.getY();
            this.startProgress = this.progress;
            this.tracking = false;
            float translationX = this.drawerPanel.getTranslationX() + this.drawerWidth;
            this.tapClosePending = motionEvent.getX() > translationX;
            return motionEvent.getX() > translationX;
        }
        if (motionEvent.getAction() == 2) {
            float x = motionEvent.getX() - this.startX;
            if (shouldStartVisibleDrawerTracking(x, Math.abs(motionEvent.getY() - this.startY))) {
                beginVisibleDrawerTracking(motionEvent, x);
                return true;
            }
        }
        return false;
    }

    /* JADX WARN: Code duplicated, block: B:26:0x006c  */
    /* JADX WARN: Code duplicated, block: B:28:0x0070  */
    /* JADX WARN: Code duplicated, block: B:30:0x0074  */
    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isClosingAnimationInProgress()) {
            return !shouldPassClosingTouchThrough(motionEvent);
        }
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        this.velocityTracker.addMovement(motionEvent);
        int action = motionEvent.getAction();
        if (action == 0) {
            if (this.isAnimating) {
                cancelAnimations();
            }
            this.startX = motionEvent.getX();
            this.startY = motionEvent.getY();
            this.startProgress = this.progress;
            this.tracking = false;
            this.tapClosePending = motionEvent.getX() > this.drawerPanel.getTranslationX() + ((float) this.drawerWidth);
            return true;
        }
        if (action == 1) {
            if (this.tracking) {
                finishTracking();
                return true;
            }
            if (motionEvent.getAction() != 1 && this.tapClosePending) {
                this.tapClosePending = false;
                closeDrawer(true);
                return true;
            }
            this.tapClosePending = false;
        } else if (action == 2) {
            if (!this.tracking) {
                float x = motionEvent.getX() - this.startX;
                if (shouldStartVisibleDrawerTracking(x, Math.abs(motionEvent.getY() - this.startY))) {
                    beginVisibleDrawerTracking(motionEvent, x);
                }
            }
            if (this.tracking) {
                setProgress(Math.max(0.0f, Math.min(1.0f, this.startProgress + ((motionEvent.getX() - this.startX) / this.drawerWidth))));
                return true;
            }
        } else if (action == 3) {
            if (this.tracking) {
                finishTracking();
                return true;
            }
            if (motionEvent.getAction() != 1) {
            }
            this.tapClosePending = false;
        }
        return true;
    }

    private boolean isClosingAnimationInProgress() {
        return this.isAnimating && !this.isOpen;
    }

    private boolean shouldPassClosingTouchThrough(MotionEvent motionEvent) {
        return motionEvent != null && motionEvent.getAction() == 0 && motionEvent.getX() > this.drawerPanel.getTranslationX() + ((float) this.drawerWidth);
    }

    private boolean shouldStartVisibleDrawerTracking(float f, float f2) {
        if (f < 0.0f) {
            return Math.abs(f) >= f2 && Math.abs(f) >= getDrawerCloseTouchSlop();
        }
        return this.startProgress < 0.999f && f > 0.0f && f / 3.0f > f2 && f >= getDrawerOpenTouchSlop();
    }

    public boolean handleEdgeSwipeIntercept(MotionEvent motionEvent) {
        if (!ExteraConfig.getNavigationDrawer()) {
            return false;
        }
        if (motionEvent.getAction() == 0) {
            this.startX = motionEvent.getX();
            this.startY = motionEvent.getY();
            this.startProgress = this.progress;
            this.startedEdgeSwipe = false;
            this.tracking = false;
            if (canStartClosedDrawerSwipe(motionEvent)) {
                this.startedEdgeSwipe = true;
                if (this.velocityTracker == null) {
                    this.velocityTracker = VelocityTracker.obtain();
                }
                this.velocityTracker.clear();
                this.velocityTracker.addMovement(motionEvent);
            }
            return false;
        }
        if (this.startedEdgeSwipe) {
            VelocityTracker velocityTracker = this.velocityTracker;
            if (velocityTracker != null) {
                velocityTracker.addMovement(motionEvent);
            }
            if (motionEvent.getAction() == 2) {
                float x = motionEvent.getX() - this.startX;
                float y = motionEvent.getY() - this.startY;
                if (shouldBlockClosedDrawerSwipe(x, y)) {
                    this.startedEdgeSwipe = false;
                    return false;
                }
                if (shouldStartClosedDrawerTracking(x, Math.abs(y))) {
                    beginClosedDrawerTracking(motionEvent, x);
                    return true;
                }
            }
            if (motionEvent.getAction() == 1 || motionEvent.getAction() == 3) {
                this.startedEdgeSwipe = false;
            }
        }
        return false;
    }

    public boolean handleEdgeSwipeTouch(MotionEvent motionEvent) {
        if (!ExteraConfig.getNavigationDrawer()) {
            return false;
        }
        if (!this.startedEdgeSwipe && !this.tracking) {
            return false;
        }
        if (this.velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        }
        this.velocityTracker.addMovement(motionEvent);
        int action = motionEvent.getAction();
        if (action != 1) {
            if (action == 2) {
                if (this.tracking) {
                    setProgress(Math.max(0.0f, Math.min(1.0f, this.startProgress + ((motionEvent.getX() - this.startX) / this.drawerWidth))));
                }
                return true;
            }
            if (action != 3) {
                return true;
            }
        }
        if (this.tracking) {
            finishTracking();
        }
        this.startedEdgeSwipe = false;
        return true;
    }

    private boolean shouldBlockClosedDrawerSwipe(float f, float f2) {
        float fAbs = Math.abs(f2);
        float drawerOpenTouchSlop = AndroidUtilities.touchSlop;
        if (drawerOpenTouchSlop <= 0.0f) {
            drawerOpenTouchSlop = getDrawerOpenTouchSlop();
        }
        return fAbs >= drawerOpenTouchSlop && fAbs > Math.abs(f);
    }

    private boolean shouldStartClosedDrawerTracking(float f, float f2) {
        return f > 0.0f && f / 3.0f > f2 && Math.abs(f) >= getDrawerOpenTouchSlop();
    }

    private void beginVisibleDrawerTracking(MotionEvent motionEvent, float f) {
        this.tracking = true;
        this.tapClosePending = false;
        if (this.isAnimating) {
            cancelAnimations();
        }
        offsetTrackingStart(motionEvent, f);
        resetTrackingVelocity(motionEvent);
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    private void beginClosedDrawerTracking(MotionEvent motionEvent, float f) {
        this.tracking = true;
        this.tapClosePending = false;
        if (this.isAnimating) {
            cancelAnimations();
        }
        super.setVisibility(0);
        applyDrawerPanelPadding();
        refreshContents();
        offsetTrackingStart(motionEvent, f);
        resetTrackingVelocity(motionEvent);
        if (getParent() != null) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
    }

    private void offsetTrackingStart(MotionEvent motionEvent, float f) {
        this.startX += Math.signum(f) * getTrackingTouchSlop(f);
        this.startY = motionEvent.getY();
        this.startProgress = this.progress;
    }

    private void resetTrackingVelocity(MotionEvent motionEvent) {
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker == null) {
            this.velocityTracker = VelocityTracker.obtain();
        } else {
            velocityTracker.clear();
        }
        this.velocityTracker.addMovement(motionEvent);
    }

    private float getTrackingTouchSlop(float f) {
        return f < 0.0f ? getDrawerCloseTouchSlop() : getDrawerOpenTouchSlop();
    }

    private float getDrawerOpenTouchSlop() {
        return AndroidUtilities.getPixelsInCM(0.2f, true);
    }

    private float getDrawerCloseTouchSlop() {
        return AndroidUtilities.getPixelsInCM(0.4f, true);
    }

    private void finishTracking() {
        float xVelocity;
        float yVelocity;
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker != null) {
            velocityTracker.computeCurrentVelocity(1000);
            xVelocity = this.velocityTracker.getXVelocity();
            yVelocity = this.velocityTracker.getYVelocity();
        } else {
            xVelocity = 0.0f;
            yVelocity = 0.0f;
        }
        int swipeVelocity = AppUtils.getSwipeVelocity();
        if ((this.progress >= 1.0f / (this.isOpen ? 1.25f : 5.0f) || (xVelocity >= swipeVelocity && Math.abs(xVelocity) >= Math.abs(yVelocity))) && (xVelocity >= 0.0f || Math.abs(xVelocity) < swipeVelocity)) {
            boolean z = !this.isOpen && Math.abs(xVelocity) >= ((float) swipeVelocity);
            this.isOpen = true;
            animateProgress(1.0f, z, xVelocity);
        } else {
            boolean z2 = this.isOpen && Math.abs(xVelocity) >= ((float) swipeVelocity);
            this.isOpen = false;
            animateProgress(0.0f, z2, xVelocity);
        }
        VelocityTracker velocityTracker2 = this.velocityTracker;
        if (velocityTracker2 != null) {
            velocityTracker2.recycle();
            this.velocityTracker = null;
        }
        this.tracking = false;
        this.startedEdgeSwipe = false;
        this.tapClosePending = false;
    }

    private boolean canOpen(MotionEvent motionEvent) {
        BaseFragment lastFragment = getLastFragment();
        if (lastFragment instanceof DialogsActivity) {
            return ((DialogsActivity) lastFragment).canOpenDrawerBySwipe(motionEvent);
        }
        return false;
    }

    private boolean canStartClosedDrawerSwipe(MotionEvent motionEvent) {
        INavigationLayout parentActionBarLayout;
        BaseFragment lastFragment;
        ViewGroup view;
        if (canOpen(motionEvent)) {
            ViewParent parent = getParent();
            if (!(parent instanceof DrawerLayoutContainer) || (parentActionBarLayout = ((DrawerLayoutContainer) parent).getParentActionBarLayout()) == null || parentActionBarLayout.getFragmentStack().size() != 1 || !parentActionBarLayout.allowSwipe() || (((lastFragment = parentActionBarLayout.getLastFragment()) != null && lastFragment.getLastSheet() != null && lastFragment.getLastSheet().attachedToParent()) || (view = parentActionBarLayout.getView()) == null)) {
                return false;
            }
            view.getHitRect(this.rect);
            if (this.rect.contains((int) motionEvent.getX(), (int) motionEvent.getY()) && findScrollingChild(view, motionEvent.getX() - this.rect.left, motionEvent.getY() - this.rect.top) == null) {
                return true;
            }
        }
        return false;
    }

    private View findScrollingChild(ViewGroup viewGroup, float f, float f2) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = viewGroup.getChildAt(i);
            if (childAt.getVisibility() == 0) {
                childAt.getHitRect(this.rect);
                if (!this.rect.contains((int) f, (int) f2)) {
                    continue;
                } else {
                    if (childAt.canScrollHorizontally(-1)) {
                        return childAt;
                    }
                    if (childAt instanceof ViewGroup) {
                        Rect rect = this.rect;
                        View viewFindScrollingChild = findScrollingChild((ViewGroup) childAt, f - rect.left, f2 - rect.top);
                        if (viewFindScrollingChild != null) {
                            return viewFindScrollingChild;
                        }
                    } else {
                        continue;
                    }
                }
            }
        }
        return null;
    }

    private BaseFragment getLastFragment() {
        INavigationLayout parentActionBarLayout;
        ViewGroup viewGroup = (ViewGroup) getParent();
        if (!(viewGroup instanceof DrawerLayoutContainer) || (parentActionBarLayout = ((DrawerLayoutContainer) viewGroup).getParentActionBarLayout()) == null) {
            return null;
        }
        BaseFragment lastFragment = parentActionBarLayout.getLastFragment();
        return lastFragment instanceof MainTabsActivity ? ((MainTabsActivity) lastFragment).getCurrentVisibleFragment() : lastFragment;
    }

    private void dismissSelectionPopup() {
        SelectAnimatedEmojiDialog.SelectAnimatedEmojiDialogWindow selectAnimatedEmojiDialogWindow = this.selectAnimatedEmojiDialog;
        if (selectAnimatedEmojiDialogWindow != null) {
            selectAnimatedEmojiDialogWindow.dismiss();
            this.selectAnimatedEmojiDialog = null;
        }
    }

    private void recycleVelocityTracker() {
        VelocityTracker velocityTracker = this.velocityTracker;
        if (velocityTracker != null) {
            velocityTracker.recycle();
            this.velocityTracker = null;
        }
    }

    public void dispose() {
        cancelAnimations();
        onCloseComplete();
        recycleVelocityTracker();
        this.accountPickerView.dispose();
        unregisterNotifications();
        resetNavigationTranslationTarget();
    }

    @Override // android.view.View
    public WindowInsets onApplyWindowInsets(WindowInsets windowInsets) {
        if (!ExteraConfig.getImmersiveDrawerAnimation()) {
            float fDp = AndroidUtilities.dp(24.0f);
            if (Build.VERSION.SDK_INT >= 31) {
                RoundedCorner roundedCorner = windowInsets.getRoundedCorner(1);
                RoundedCorner roundedCorner2 = windowInsets.getRoundedCorner(2);
                this.cachedTopRightRadius = roundedCorner != null ? Math.max(fDp, roundedCorner.getRadius() / 2.0f) : fDp;
                if (roundedCorner2 != null) {
                    fDp = Math.max(fDp, roundedCorner2.getRadius() / 2.0f);
                }
                this.cachedBottomRightRadius = fDp;
            } else {
                this.cachedTopRightRadius = fDp;
                this.cachedBottomRightRadius = fDp;
            }
        }
        return super.onApplyWindowInsets(windowInsets);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerNotifications();
        Bulletin.addDelegate(this.bulletinContainer, new Bulletin.Delegate() { 
            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public int getBottomOffset(int i) {
                return AndroidUtilities.navigationBarHeight;
            }
        });
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Bulletin.removeDelegate(this.bulletinContainer);
        cancelAnimations();
        onCloseComplete();
        dismissSelectionPopup();
        recycleVelocityTracker();
        this.accountPickerView.dispose();
        resetNavigationTranslationTarget();
        unregisterNotifications();
    }

    private void registerNotifications() {
        if (this.notificationsRegistered) {
            return;
        }
        for (int i = 0; i < 16; i++) {
            NotificationCenter notificationCenter = NotificationCenter.getInstance(i);
            notificationCenter.addObserver(this, NotificationCenter.mainUserInfoChanged);
            notificationCenter.addObserver(this, NotificationCenter.userEmojiStatusUpdated);
            notificationCenter.addObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
            notificationCenter.addObserver(this, NotificationCenter.updateInterfaces);
            notificationCenter.addObserver(this, NotificationCenter.appDidLogout);
            notificationCenter.addObserver(this, NotificationCenter.attachMenuBotsDidLoad);
            notificationCenter.addObserver(this, NotificationCenter.didUpdateConnectionState);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.themeAccentListUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.notificationsCountUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.reloadInterface);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pluginMenuItemsUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxySettingsChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.proxyPingUpdated);
        this.notificationsRegistered = true;
    }

    private void unregisterNotifications() {
        if (this.notificationsRegistered) {
            for (int i = 0; i < 16; i++) {
                NotificationCenter notificationCenter = NotificationCenter.getInstance(i);
                notificationCenter.removeObserver(this, NotificationCenter.mainUserInfoChanged);
                notificationCenter.removeObserver(this, NotificationCenter.userEmojiStatusUpdated);
                notificationCenter.removeObserver(this, NotificationCenter.currentUserPremiumStatusChanged);
                notificationCenter.removeObserver(this, NotificationCenter.updateInterfaces);
                notificationCenter.removeObserver(this, NotificationCenter.appDidLogout);
                notificationCenter.removeObserver(this, NotificationCenter.attachMenuBotsDidLoad);
                notificationCenter.removeObserver(this, NotificationCenter.didUpdateConnectionState);
            }
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.themeAccentListUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.notificationsCountUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.reloadInterface);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pluginMenuItemsUpdated);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxySettingsChanged);
            NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.proxyPingUpdated);
            this.notificationsRegistered = false;
        }
    }

    public void onAccountChanged() {
        refreshContents();
    }

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.mainUserInfoChanged || i == NotificationCenter.userEmojiStatusUpdated || i == NotificationCenter.currentUserPremiumStatusChanged) {
            refreshAccountViews(i2, true);
            return;
        }
        if (i == NotificationCenter.updateInterfaces) {
            if (objArr.length > 0) {
                Object obj = objArr[0];
                if (obj instanceof Integer) {
                    refreshAccountViews(i2, ((Integer) obj).intValue());
                }
            }
            this.menuView.updateUnreadCounters(UserConfig.selectedAccount);
            return;
        }
        if (i == NotificationCenter.didSetNewTheme) {
            updateColors();
            return;
        }
        if (i == NotificationCenter.themeAccentListUpdated) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.updateColors();
                }
            });
            return;
        }
        if (i == NotificationCenter.notificationsCountUpdated) {
            this.accountPickerView.updateUnreadCounters();
            this.menuView.updateUnreadCounters(UserConfig.selectedAccount);
            return;
        }
        if (i == NotificationCenter.reloadInterface) {
            this.headerView.updateUserInfo();
            this.accountPickerView.updateUnreadCounters();
            this.menuView.updateUnreadCounters(UserConfig.selectedAccount);
            updateColors();
            return;
        }
        if (i == NotificationCenter.attachMenuBotsDidLoad) {
            if (i2 == UserConfig.selectedAccount && this.isOpen) {
                refreshContents();
                return;
            }
            return;
        }
        if (i == NotificationCenter.pluginMenuItemsUpdated) {
            if (this.isOpen) {
                refreshContents();
            }
        } else if (i == NotificationCenter.proxySettingsChanged || i == NotificationCenter.proxyPingUpdated || i == NotificationCenter.didUpdateConnectionState) {
            this.headerView.updateProxyStatus();
        } else if (i == NotificationCenter.appDidLogout) {
            refreshAccountViews(i2, true);
            if (this.isOpen) {
                closeDrawer(false);
            }
        }
    }

    public void updateColors() {
        this.drawerPanel.setBackgroundColor(Theme.getColor(COLOR_KEY_DRAWER_BACKGROUND));
        this.headerView.updateColors();
        this.accountPickerView.updateColors();
        this.menuView.updateColors();
        invalidate();
    }

    private int calculateDrawerWidth() {
        return Math.min(AndroidUtilities.dp(300.0f), AndroidUtilities.displaySize.x - AndroidUtilities.dp(56.0f));
    }

    private void applyDrawerPanelPadding() {
        this.drawerPanel.setPadding(0, AndroidUtilities.statusBarHeight, 0, 0);
    }

    private int getPopupWidth() {
        return (int) Math.min(AndroidUtilities.dp(324.0f), AndroidUtilities.displaySize.x * 0.95f);
    }
}
