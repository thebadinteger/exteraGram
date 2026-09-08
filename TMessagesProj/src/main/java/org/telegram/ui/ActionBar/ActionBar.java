package org.telegram.ui.ActionBar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.VectorDrawable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.transition.TransitionValues;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.core.graphics.ColorUtils;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.utils.ui.ChatHeaderUiHelper;
import com.exteragram.messenger.utils.ui.TextPaint;
import java.util.ArrayList;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import me.vkryl.android.animator.ReplaceAnimator;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.Adapters.FiltersView;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EllipsizeSpanAnimator;
import org.telegram.ui.Components.FireworksEffect;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SectionsScrollView;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.SnowflakesEffect;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider;
import org.telegram.ui.DialogsActivity;

public class ActionBar extends FrameLayout implements FactorAnimator.Target, Theme.Colorable {
    private int actionBarColor;
    public ActionBarMenuOnItemClick actionBarMenuOnItemClick;
    private ActionBarMenu actionMode;
    private AnimatorSet actionModeAnimation;
    private int actionModeColor;
    private View actionModeExtraView;
    private View[] actionModeHidingViews;
    private View actionModeShowingView;
    private String actionModeTag;
    private View actionModeTop;
    private View actionModeTranslationView;
    protected boolean actionModeVisible;
    private boolean adaptiveBackground;
    private boolean adaptiveBackgroundHideTitle;
    private ValueAnimator adaptive_animator;
    private int adaptive_lowerColorKey;
    private int adaptive_topColorKey;
    private boolean addToContainer;
    private ActionBarAnimatedSubtitleOverlayContainer additionalSubTitleOverlayContainer;
    private SimpleTextView additionalSubtitleTextView;
    private int additionalTextLeft;
    private boolean allowOverlayTitle;
    private float animatedCenterTitleAvailableWidth;
    private float animatedCenterTitleX;
    private final BoolAnimator animatorAvatarContainerHasAvatar;
    private final FactorAnimator animatorAvatarContainerWidth;
    private final BoolAnimator animatorHasMenuItems;
    private final FactorAnimator animatorMenuItemsWidth;
    private boolean attachState;
    private boolean attached;
    private BackupImageView avatarSearchImageView;
    private Drawable backButtonDrawable;
    public ImageView backButtonImageView;
    private INavigationLayout.BackButtonState backButtonState;
    Runnable backgroundUpdateListener;
    public Paint blurScrimPaint;
    boolean blurredBackground;
    private boolean castShadows;
    private boolean centerScale;
    private int centerTitleAnimationTargetWidth;
    private int centerTitleAnimationTargetX;
    private ValueAnimator centerTitleLayoutAnimator;
    private ChatAvatarContainer chatAvatarContainer;
    private boolean clipContent;
    SizeNotifierFrameLayout contentView;
    private boolean doNotDrawChild;
    private Runnable doOnActionModeFactorChanged;
    private boolean drawBackButton;
    private boolean drawGlassMiddlePill;
    EllipsizeSpanAnimator ellipsizeSpanAnimator;
    private int extraHeight;
    private boolean fireworks;
    private FireworksEffect fireworksEffect;
    private Paint.FontMetricsInt fontMetricsInt;
    private boolean forceDisableCenterTitle;
    private boolean forceSkipTouches;
    private int forcedMenuWidth;
    private boolean fromBottom;
    private BlurredBackgroundDrawable glassDrawable;
    private BlurredBackgroundDrawable glassDrawableBack;
    private float glassDrawableLeftRadius;
    private BlurredBackgroundDrawable glassDrawableMenu;
    private boolean glassMode;
    private boolean glassModeHasAvatar;
    private boolean glassOnlyBack;
    private boolean hasForcedMenuWidth;
    private boolean ignoreLayoutRequest;
    private View.OnTouchListener interceptTouchEventListener;
    private boolean interceptTouches;
    private boolean isAnimationsAllowed;
    private boolean isCenterTitle;
    private boolean isMenuOffsetSuppressed;
    protected boolean isSearchFieldVisible;
    protected int itemsActionModeBackgroundColor;
    protected int itemsActionModeColor;
    public int itemsBackgroundColor;
    protected int itemsColor;
    private int lastMeasuredWidth;
    private CharSequence lastOverlayTitle;
    private Drawable lastRightDrawable;
    private Runnable lastRunnable;
    private CharSequence lastTitle;
    public ActionBarMenu menu;
    public boolean menuOccupyBack;
    protected boolean occupyStatusBar;
    private boolean onTop;
    private float onTopAnimated;
    private boolean overlayTitleAnimation;
    boolean overlayTitleAnimationInProgress;
    private final Object[] overlayTitleToSet;
    protected BaseFragment parentFragment;
    int prevWidth;
    private Rect rect;
    Rect rectTmp;
    private final Theme.ResourcesProvider resourcesProvider;
    private boolean resumed;
    private View.OnClickListener rightDrawableOnClickListener;
    private float searchFactor;
    public float searchFieldVisibleAlpha;
    AnimatorSet searchVisibleAnimator;
    private int shadowAlpha;
    private SnowflakesEffect snowflakesEffect;
    private CharSequence subtitle;
    private SimpleTextView subtitleTextView;
    private boolean supportsHolidayImage;
    private Runnable titleActionRunnable;
    private boolean titleAnimationRunning;
    private AnimatorSet titleAnimator;
    private int titleColorToSet;
    private boolean titleOverlayShown;
    private int titleRightMargin;
    private final SimpleTextView[] titleTextView;
    private FrameLayout titlesContainer;
    private boolean useContainerForTitles;

    public static class ActionBarMenuOnItemClick {
        public boolean canOpenMenu() {
            return true;
        }

        public void onItemClick(int i) {
        }
    }

    @Override // org.telegram.ui.ActionBar.Theme.Colorable
    public /* bridge */ /* synthetic */ int[] getColorKeys() {
        return super.getColorKeys();
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }

    public boolean onSearchChangedIgnoreTitles() {
        return false;
    }

    public ActionBar(Context context) {
        this(context, null);
    }

    public ActionBar(Context context, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.backButtonState = INavigationLayout.BackButtonState.BACK;
        this.titleTextView = new SimpleTextView[2];
        this.occupyStatusBar = true;
        this.addToContainer = true;
        this.interceptTouches = true;
        this.overlayTitleToSet = new Object[3];
        this.castShadows = true;
        this.shadowAlpha = 255;
        this.titleColorToSet = 0;
        this.animatedCenterTitleX = Float.NaN;
        this.animatedCenterTitleAvailableWidth = Float.NaN;
        this.centerTitleAnimationTargetX = Integer.MIN_VALUE;
        this.centerTitleAnimationTargetWidth = -1;
        this.lastMeasuredWidth = -1;
        this.blurScrimPaint = new Paint();
        this.rectTmp = new Rect();
        this.ellipsizeSpanAnimator = new EllipsizeSpanAnimator(this);
        this.drawGlassMiddlePill = true;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.animatorAvatarContainerWidth = new FactorAnimator(0, this, cubicBezierInterpolator, 380L);
        this.animatorAvatarContainerHasAvatar = new BoolAnimator(0, this, cubicBezierInterpolator, 380L);
        this.animatorMenuItemsWidth = new FactorAnimator(0, this, cubicBezierInterpolator, 320L);
        this.animatorHasMenuItems = new BoolAnimator(0, this, cubicBezierInterpolator, 320L);
        this.onTop = true;
        this.onTopAnimated = 1.0f;
        this.resourcesProvider = resourcesProvider;
        setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBar$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$new$0(view);
            }
        });
    }

    public /* synthetic */ void lambda$new$0(View view) {
        Runnable runnable;
        if (isSearchFieldVisible() || (runnable = this.titleActionRunnable) == null) {
            return;
        }
        runnable.run();
    }

    public void setGlassOnlyBack() {
        this.glassOnlyBack = true;
    }

    public void setChatAvatarContainer(ChatAvatarContainer chatAvatarContainer) {
        this.chatAvatarContainer = chatAvatarContainer;
    }

    public void setDrawGlassMiddlePill(boolean z) {
        if (this.drawGlassMiddlePill != z) {
            this.drawGlassMiddlePill = z;
            invalidate();
        }
    }

    public void setGlassShadowAlpha(float f) {
        BlurredBackgroundDrawable blurredBackgroundDrawable = this.glassDrawable;
        if (blurredBackgroundDrawable != null) {
            blurredBackgroundDrawable.setShadowAlpha(f);
        }
        BlurredBackgroundDrawable blurredBackgroundDrawable2 = this.glassDrawableBack;
        if (blurredBackgroundDrawable2 != null) {
            blurredBackgroundDrawable2.setShadowAlpha(f);
        }
        BlurredBackgroundDrawable blurredBackgroundDrawable3 = this.glassDrawableMenu;
        if (blurredBackgroundDrawable3 != null) {
            blurredBackgroundDrawable3.setShadowAlpha(f);
        }
        invalidate();
    }

    public void setupGlass(BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory, BlurredBackgroundColorProvider blurredBackgroundColorProvider) {
        setupGlass(blurredBackgroundDrawableViewFactory, blurredBackgroundColorProvider, false, false);
    }

    public void setupGlass(BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory, BlurredBackgroundColorProvider blurredBackgroundColorProvider, boolean z) {
        setupGlass(blurredBackgroundDrawableViewFactory, blurredBackgroundColorProvider, true, z);
    }

    private void setupGlass(BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory, BlurredBackgroundColorProvider blurredBackgroundColorProvider, boolean z, boolean z2) {
        setBackground(null);
        setClipChildren(false);
        this.glassMode = true;
        this.glassModeHasAvatar = z;
        float fDp = AndroidUtilities.dp(23.0f);
        if (z) {
            boolean newChatHeaderStyle = ExteraConfig.getNewChatHeaderStyle();
            int chatAvatarSizeDp = ChatHeaderUiHelper.getChatAvatarSizeDp();
            this.glassDrawableLeftRadius = Math.min(fDp, ChatHeaderUiHelper.getAvatarRadius(chatAvatarSizeDp, z2, false) + (newChatHeaderStyle ? AndroidUtilities.dp(3.33f) : (AndroidUtilities.dp(46.0f) - ChatHeaderUiHelper.getAvatarSizePx(chatAvatarSizeDp)) / 2.0f));
        } else {
            this.glassDrawableLeftRadius = fDp;
        }
        BlurredBackgroundDrawable padding = blurredBackgroundDrawableViewFactory.create(this).setColorProvider(blurredBackgroundColorProvider).setPadding(AndroidUtilities.dp(6.0f));
        float f = this.glassDrawableLeftRadius;
        this.glassDrawable = padding.setRadius(f, fDp, fDp, f);
        this.glassDrawableBack = blurredBackgroundDrawableViewFactory.create(this).setColorProvider(blurredBackgroundColorProvider).setRadius(fDp).setPadding(AndroidUtilities.dp(6.0f));
        this.glassDrawableMenu = blurredBackgroundDrawableViewFactory.create(this).setColorProvider(blurredBackgroundColorProvider).setRadius(fDp).setPadding(AndroidUtilities.dp(6.0f));
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setTranslationX(-AndroidUtilities.dp(10.0f));
            this.menu.setGlassMode(true);
        }
        ActionBarMenu actionBarMenu2 = this.actionMode;
        if (actionBarMenu2 != null) {
            actionBarMenu2.setTranslationX(-AndroidUtilities.dp(10.0f));
            this.actionMode.setGlassMode(true);
        }
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            imageView.setTranslationX(AndroidUtilities.dp(2.0f));
        }
    }

    public int getGlassMiddlePillChildLeft(int i) {
        if (!this.glassMode) {
            return -1;
        }
        int iDp = AndroidUtilities.dp(6.0f);
        int iDp2 = AndroidUtilities.dp(46.0f);
        ImageView imageView = this.backButtonImageView;
        return Math.round(((imageView == null || imageView.getVisibility() != 0) ? 0 : iDp2 + iDp) + iDp + ((iDp2 - i) / 2.0f));
    }

    public INavigationLayout.BackButtonState getBackButtonState() {
        return this.backButtonState;
    }

    private void createBackButtonImage() {
        if (this.backButtonImageView != null) {
            return;
        }
        ImageView imageView = new ImageView(getContext());
        this.backButtonImageView = imageView;
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
        this.backButtonImageView.setPadding(AndroidUtilities.dp(1.0f), 0, 0, 0);
        addView(this.backButtonImageView, LayoutHelper.createFrame(54, 54, 51));
        this.backButtonImageView.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ActionBar.ActionBar$$ExternalSyntheticLambda7
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$createBackButtonImage$1(view);
            }
        });
        this.backButtonImageView.setContentDescription(LocaleController.getString(R.string.AccDescrGoBack));
    }

    public /* synthetic */ void lambda$createBackButtonImage$1(View view) {
        if (!this.actionModeVisible && this.isSearchFieldVisible) {
            closeSearchField();
            return;
        }
        ActionBarMenuOnItemClick actionBarMenuOnItemClick = this.actionBarMenuOnItemClick;
        if (actionBarMenuOnItemClick != null) {
            actionBarMenuOnItemClick.onItemClick(-1);
        }
    }

    public Drawable getBackButtonDrawable() {
        return this.backButtonDrawable;
    }

    public void setBackButtonDrawable(Drawable drawable) {
        if (this.backButtonImageView == null) {
            createBackButtonImage();
        }
        this.backButtonImageView.setVisibility(drawable == null ? 8 : 0);
        ImageView imageView = this.backButtonImageView;
        this.backButtonDrawable = drawable;
        imageView.setImageDrawable(drawable);
        if (drawable instanceof BackDrawable) {
            BackDrawable backDrawable = (BackDrawable) drawable;
            backDrawable.setRotation(isActionModeShowed() ? 1.0f : 0.0f, false);
            backDrawable.setRotatedColor(this.itemsActionModeColor);
            backDrawable.setColor(this.itemsColor);
        } else if (drawable instanceof MenuDrawable) {
            MenuDrawable menuDrawable = (MenuDrawable) drawable;
            menuDrawable.setBackColor(this.actionBarColor);
            menuDrawable.setIconColor(this.itemsColor);
        } else if ((drawable instanceof BitmapDrawable) || (drawable instanceof VectorDrawable)) {
            this.backButtonImageView.setColorFilter(new PorterDuffColorFilter(this.itemsColor, PorterDuff.Mode.SRC_IN));
        }
        checkBackButtonLayerType();
    }

    private void checkBackButtonLayerType() {
        ImageView imageView = this.backButtonImageView;
        if (imageView == null) {
            return;
        }
        Drawable drawable = imageView.getDrawable();
        int i = ((drawable instanceof BackDrawable) || (drawable instanceof MenuDrawable)) ? 2 : 0;
        if (this.backButtonImageView.getLayerType() != i) {
            this.backButtonImageView.setLayerType(i, null);
            this.backButtonImageView.invalidate();
        }
    }

    public void setBackButtonContentDescription(CharSequence charSequence) {
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            imageView.setContentDescription(charSequence);
        }
    }

    public void setSupportsHolidayImage(boolean z) {
        this.supportsHolidayImage = z;
        if (z) {
            this.fontMetricsInt = new Paint.FontMetricsInt();
            this.rect = new Rect();
        }
        invalidate();
    }

    public BackupImageView getSearchAvatarImageView() {
        return this.avatarSearchImageView;
    }

    public void setSearchAvatarImageView(BackupImageView backupImageView) {
        BackupImageView backupImageView2 = this.avatarSearchImageView;
        if (backupImageView2 == backupImageView) {
            return;
        }
        if (backupImageView2 != null) {
            removeView(backupImageView2);
        }
        this.avatarSearchImageView = backupImageView;
        if (backupImageView != null) {
            addView(backupImageView);
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        Drawable currentHolidayDrawable;
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL && motionEvent.getAction() == 0 && (currentHolidayDrawable = Theme.getCurrentHolidayDrawable()) != null && currentHolidayDrawable.getBounds().contains((int) motionEvent.getX(), (int) motionEvent.getY())) {
            boolean z = this.fireworks;
            this.fireworks = !z;
            if (z || this.snowflakesEffect == null) {
                this.fireworksEffect = null;
                SnowflakesEffect snowflakesEffect = new SnowflakesEffect(0);
                this.snowflakesEffect = snowflakesEffect;
                snowflakesEffect.occupyStatusBar = this.occupyStatusBar;
            } else {
                this.snowflakesEffect = null;
                this.fireworksEffect = new FireworksEffect();
            }
            this.titleTextView[0].invalidate();
            invalidate();
        }
        View.OnTouchListener onTouchListener = this.interceptTouchEventListener;
        return (onTouchListener != null && onTouchListener.onTouch(this, motionEvent)) || super.onInterceptTouchEvent(motionEvent);
    }

    public boolean shouldClipChild(View view) {
        if (this.clipContent) {
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if (view == simpleTextViewArr[0] || view == simpleTextViewArr[1] || view == this.subtitleTextView || view == this.menu || view == this.backButtonImageView || view == this.additionalSubtitleTextView || view == this.titlesContainer) {
                return true;
            }
        }
        return false;
    }

    @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View view, long j) {
        BaseFragment baseFragment = this.parentFragment;
        if (baseFragment != null && baseFragment.getParentLayout() != null && this.parentFragment.getParentLayout().isActionBarInCrossfade()) {
            return false;
        }
        if (this.drawBackButton && view == this.backButtonImageView) {
            return true;
        }
        boolean zShouldClipChild = shouldClipChild(view);
        if (zShouldClipChild) {
            canvas.save();
            canvas.clipRect(0.0f, (-getTranslationY()) + (this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0), getMeasuredWidth(), getMeasuredHeight());
        }
        boolean zDrawChild = super.drawChild(canvas, view, j);
        if (this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL) {
            SimpleTextView[] simpleTextViewArr = this.titleTextView;
            if (view == simpleTextViewArr[0] || view == simpleTextViewArr[1] || (view == this.titlesContainer && this.useContainerForTitles)) {
                Drawable currentHolidayDrawable = Theme.getCurrentHolidayDrawable();
                if (currentHolidayDrawable != null) {
                    SimpleTextView simpleTextView = view == this.titlesContainer ? this.titleTextView[0] : (SimpleTextView) view;
                    if (simpleTextView != null && simpleTextView.getVisibility() == 0 && (simpleTextView.getText() instanceof String)) {
                        TextPaint textPaint = simpleTextView.getTextPaint();
                        textPaint.getFontMetricsInt(this.fontMetricsInt);
                        textPaint.getTextBounds((String) simpleTextView.getText(), 0, 1, this.rect);
                        int textStartX = simpleTextView.getTextStartX() + Theme.getCurrentHolidayDrawableXOffset() + ((this.rect.width() - (currentHolidayDrawable.getIntrinsicWidth() + Theme.getCurrentHolidayDrawableXOffset())) / 2);
                        int textStartY = simpleTextView.getTextStartY() + Theme.getCurrentHolidayDrawableYOffset() + ((int) Math.ceil((simpleTextView.getTextHeight() - this.rect.height()) / 2.0f)) + ((int) (AndroidUtilities.dp(8.0f) * (1.0f - this.titlesContainer.getScaleY())));
                        currentHolidayDrawable.setBounds(textStartX, textStartY - currentHolidayDrawable.getIntrinsicHeight(), currentHolidayDrawable.getIntrinsicWidth() + textStartX, textStartY);
                        currentHolidayDrawable.setAlpha((int) (this.titlesContainer.getAlpha() * 255.0f * simpleTextView.getAlpha()));
                        currentHolidayDrawable.draw(canvas);
                        if (this.overlayTitleAnimationInProgress) {
                            view.invalidate();
                            invalidate();
                        }
                    }
                }
                drawHolidayEffect(canvas);
            }
        }
        if (zShouldClipChild) {
            canvas.restore();
        }
        return zDrawChild;
    }

    @Override // android.view.View
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        if (this.clipContent) {
            invalidate();
        }
    }

    public void setBackButtonImage(int i) {
        if (this.backButtonImageView == null) {
            createBackButtonImage();
        }
        this.backButtonImageView.setVisibility(i == 0 ? 8 : 0);
        this.backButtonImageView.setImageResource(i);
        this.backButtonImageView.setColorFilter(new PorterDuffColorFilter(this.itemsColor, PorterDuff.Mode.SRC_IN));
        checkBackButtonLayerType();
    }

    private void createSubtitleTextView() {
        if (this.subtitleTextView != null) {
            return;
        }
        SimpleTextView simpleTextView = new SimpleTextView(getContext());
        this.subtitleTextView = simpleTextView;
        simpleTextView.setGravity(getSubtitleGravity());
        this.subtitleTextView.setVisibility(8);
        this.subtitleTextView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubtitle));
        addView(this.subtitleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
    }

    public void createAdditionalSubtitleTextView() {
        if (this.additionalSubtitleTextView != null) {
            return;
        }
        SimpleTextView simpleTextView = new SimpleTextView(getContext());
        this.additionalSubtitleTextView = simpleTextView;
        simpleTextView.setGravity(getSubtitleGravity());
        this.additionalSubtitleTextView.setVisibility(8);
        this.additionalSubtitleTextView.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubtitle));
        addView(this.additionalSubtitleTextView, 0, LayoutHelper.createFrame(-2, -2, 51));
    }

    public SimpleTextView getAdditionalSubtitleTextView() {
        return this.additionalSubtitleTextView;
    }

    public void setAddToContainer(boolean z) {
        this.addToContainer = z;
    }

    public boolean shouldAddToContainer() {
        return this.addToContainer;
    }

    public void setClipContent(boolean z) {
        this.clipContent = z;
    }

    public void setSubtitle(CharSequence charSequence) {
        if (charSequence != null && this.subtitleTextView == null) {
            createSubtitleTextView();
        }
        if (this.subtitleTextView != null) {
            boolean zIsEmpty = TextUtils.isEmpty(charSequence);
            this.subtitleTextView.setVisibility((zIsEmpty || this.isSearchFieldVisible) ? 8 : 0);
            this.subtitleTextView.setAlpha(1.0f);
            if (!zIsEmpty) {
                this.subtitleTextView.setText(charSequence);
            }
            this.subtitle = charSequence;
        }
    }

    private void createTitleTextView(int i) {
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[i] != null) {
            return;
        }
        simpleTextViewArr[i] = new SimpleTextView(getContext());
        this.titleTextView[i].setGravity(getTitleGravity());
        int i2 = this.titleColorToSet;
        SimpleTextView[] simpleTextViewArr2 = this.titleTextView;
        if (i2 != 0) {
            simpleTextViewArr2[i].setTextColor(i2);
        } else {
            simpleTextViewArr2[i].setTextColor(getThemedColor(Theme.key_actionBarDefaultTitle));
        }
        SimpleTextView simpleTextView = this.titleTextView[i];
        simpleTextView.setEmojiColor(simpleTextView.getTextColor());
        this.titleTextView[i].setTypeface(AndroidUtilities.bold());
        this.titleTextView[i].setDrawablePadding(AndroidUtilities.dp(4.0f));
        this.titleTextView[i].setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f));
        this.titleTextView[i].setRightDrawableTopPadding(-AndroidUtilities.dp(1.0f));
        if (this.useContainerForTitles) {
            this.titlesContainer.addView(this.titleTextView[i], 0, LayoutHelper.createFrame(-2, -2, 51));
        } else {
            addView(this.titleTextView[i], 0, LayoutHelper.createFrame(-2, -2, 51));
        }
    }

    private boolean shouldCenterTitle() {
        if (this.forceDisableCenterTitle) {
            return false;
        }
        return this.isCenterTitle || ExteraConfig.getCenterTitle();
    }

    private int getTitleGravity() {
        return shouldCenterTitle() ? 17 : 19;
    }

    private int getSubtitleGravity() {
        return shouldCenterTitle() ? 17 : 3;
    }

    private void updateTitleGravity() {
        int titleGravity = getTitleGravity();
        int subtitleGravity = getSubtitleGravity();
        for (SimpleTextView simpleTextView : this.titleTextView) {
            if (simpleTextView != null) {
                simpleTextView.setGravity(titleGravity);
            }
        }
        SimpleTextView simpleTextView2 = this.subtitleTextView;
        if (simpleTextView2 != null) {
            simpleTextView2.setGravity(subtitleGravity);
        }
        SimpleTextView simpleTextView3 = this.additionalSubtitleTextView;
        if (simpleTextView3 != null) {
            simpleTextView3.setGravity(subtitleGravity);
        }
    }

    public void setForceDisableCenterTitle(boolean z) {
        if (this.forceDisableCenterTitle == z) {
            return;
        }
        this.forceDisableCenterTitle = z;
        resetCenterTitleLayoutAnimation();
        updateTitleGravity();
        requestLayout();
    }

    public void setTitleRightMargin(int i) {
        this.titleRightMargin = i;
    }

    private boolean shouldUseDialogsDrawerTitleOffset() {
        return (this.backButtonDrawable instanceof MenuDrawable) && ExteraConfig.getNavigationDrawer() && (this.parentFragment instanceof DialogsActivity);
    }

    private int getTitleLeft(boolean z) {
        boolean z2 = this.glassMode;
        if (!z) {
            if (z2) {
                return AndroidUtilities.dp(24.0f);
            }
            return AndroidUtilities.dp(AndroidUtilities.isTablet() ? 26.0f : 18.0f);
        }
        if (z2) {
            return AndroidUtilities.dp(76.0f);
        }
        if (shouldUseDialogsDrawerTitleOffset()) {
            return AndroidUtilities.dp(AndroidUtilities.isTablet() ? 68.0f : 56.0f);
        }
        return AndroidUtilities.dp(AndroidUtilities.isTablet() ? 80.0f : 72.0f);
    }

    public void setTitle(CharSequence charSequence) {
        setTitle(charSequence, null);
    }

    private Drawable getVisibleTitleRightDrawable(Drawable drawable) {
        if (drawable == null || ExteraConfig.getHideActionBarStatus() || !UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
            return null;
        }
        if ((drawable instanceof AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) && ((AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) drawable).isEmpty()) {
            return null;
        }
        return drawable;
    }

    public void setTitle(CharSequence charSequence, Drawable drawable) {
        if (charSequence != null && this.titleTextView[0] == null) {
            createTitleTextView(0);
        }
        SimpleTextView simpleTextView = this.titleTextView[0];
        if (simpleTextView != null) {
            simpleTextView.setTypeface((charSequence == null || !charSequence.toString().equalsIgnoreCase("экстераграм")) ? AndroidUtilities.bold() : AndroidUtilities.getTypeface("fonts/impact.ttf"));
            this.titleTextView[0].setVisibility((charSequence == null || this.isSearchFieldVisible) ? 4 : 0);
            SimpleTextView simpleTextView2 = this.titleTextView[0];
            this.lastTitle = charSequence;
            simpleTextView2.setText(charSequence);
            Drawable rightDrawable = this.titleTextView[0].getRightDrawable();
            if (this.attached && (rightDrawable instanceof AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable)) {
                ((AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) rightDrawable).setParentView(null);
            }
            this.lastRightDrawable = drawable;
            Drawable visibleTitleRightDrawable = getVisibleTitleRightDrawable(drawable);
            this.titleTextView[0].setRightDrawable(visibleTitleRightDrawable);
            if (this.attached && (visibleTitleRightDrawable instanceof AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable)) {
                ((AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) visibleTitleRightDrawable).setParentView(this.titleTextView[0]);
            }
            this.titleTextView[0].setRightDrawableOnClick(visibleTitleRightDrawable != null ? this.rightDrawableOnClickListener : null);
        }
        this.fromBottom = false;
    }

    public void setRightDrawableOnClick(View.OnClickListener onClickListener) {
        this.rightDrawableOnClickListener = onClickListener;
        SimpleTextView simpleTextView = this.titleTextView[0];
        if (simpleTextView != null) {
            simpleTextView.setRightDrawableOnClick(onClickListener);
        }
        SimpleTextView simpleTextView2 = this.titleTextView[1];
        if (simpleTextView2 != null) {
            simpleTextView2.setRightDrawableOnClick(this.rightDrawableOnClickListener);
        }
    }

    public void setTitleColor(int i) {
        if (this.titleTextView[0] == null) {
            createTitleTextView(0);
        }
        this.titleColorToSet = i;
        this.titleTextView[0].setTextColor(i);
        this.titleTextView[0].setEmojiColor(i);
        SimpleTextView simpleTextView = this.titleTextView[1];
        if (simpleTextView != null) {
            simpleTextView.setTextColor(i);
            this.titleTextView[1].setEmojiColor(i);
        }
    }

    public void setSubtitleColor(int i) {
        if (this.subtitleTextView == null) {
            createSubtitleTextView();
        }
        this.subtitleTextView.setTextColor(i);
    }

    public void setTitleScrollNonFitText(boolean z) {
        this.titleTextView[0].setScrollNonFitText(z);
    }

    public void setPopupItemsColor(int i, boolean z, boolean z2) {
        ActionBarMenu actionBarMenu;
        ActionBarMenu actionBarMenu2;
        if (z2 && (actionBarMenu2 = this.actionMode) != null) {
            actionBarMenu2.setPopupItemsColor(i, z);
        } else {
            if (z2 || (actionBarMenu = this.menu) == null) {
                return;
            }
            actionBarMenu.setPopupItemsColor(i, z);
        }
    }

    public void setPopupItemsSelectorColor(int i, boolean z) {
        ActionBarMenu actionBarMenu;
        ActionBarMenu actionBarMenu2;
        if (z && (actionBarMenu2 = this.actionMode) != null) {
            actionBarMenu2.setPopupItemsSelectorColor(i);
        } else {
            if (z || (actionBarMenu = this.menu) == null) {
                return;
            }
            actionBarMenu.setPopupItemsSelectorColor(i);
        }
    }

    public void setPopupBackgroundColor(int i, boolean z) {
        ActionBarMenu actionBarMenu;
        ActionBarMenu actionBarMenu2;
        if (z && (actionBarMenu2 = this.actionMode) != null) {
            actionBarMenu2.redrawPopup(i);
        } else {
            if (z || (actionBarMenu = this.menu) == null) {
                return;
            }
            actionBarMenu.redrawPopup(i);
        }
    }

    public SimpleTextView getSubtitleTextView() {
        return this.subtitleTextView;
    }

    public SimpleTextView getTitleTextView() {
        return this.titleTextView[0];
    }

    public Paint.FontMetricsInt getTitleFontMetricsInt() {
        SimpleTextView simpleTextView = this.titleTextView[0];
        if (simpleTextView == null) {
            android.text.TextPaint textPaint = new android.text.TextPaint(1);
            textPaint.setTextSize(AndroidUtilities.dp((AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 20.0f : 18.0f));
            return textPaint.getFontMetricsInt();
        }
        return simpleTextView.getPaint().getFontMetricsInt();
    }

    public SimpleTextView getTitleTextView2() {
        return this.titleTextView[1];
    }

    public String getTitle() {
        SimpleTextView simpleTextView = this.titleTextView[0];
        if (simpleTextView == null) {
            return null;
        }
        return simpleTextView.getText().toString();
    }

    public String getSubtitle() {
        CharSequence charSequence;
        if (this.subtitleTextView == null || (charSequence = this.subtitle) == null) {
            return null;
        }
        return charSequence.toString();
    }

    public ActionBarMenu createMenu() {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            return actionBarMenu;
        }
        ActionBarMenu actionBarMenu2 = new ActionBarMenu(getContext(), this);
        this.menu = actionBarMenu2;
        addView(actionBarMenu2, 0, LayoutHelper.createFrame(-2, -1, 5));
        return this.menu;
    }

    public void setActionBarMenuOnItemClick(ActionBarMenuOnItemClick actionBarMenuOnItemClick) {
        this.actionBarMenuOnItemClick = actionBarMenuOnItemClick;
    }

    public ActionBarMenuOnItemClick getActionBarMenuOnItemClick() {
        return this.actionBarMenuOnItemClick;
    }

    public ImageView getBackButton() {
        return this.backButtonImageView;
    }

    public ActionBarMenu createActionMode() {
        return createActionMode(true, null);
    }

    public boolean actionModeIsExist(String str) {
        if (this.actionMode == null) {
            return false;
        }
        String str2 = this.actionModeTag;
        if (str2 == null && str == null) {
            return true;
        }
        return str2 != null && str2.equals(str);
    }

    public void setOnActionModeFactorChangeListener(Runnable runnable) {
        this.doOnActionModeFactorChanged = runnable;
    }

    public float getActionModeFactor() {
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            return actionBarMenu.getAlpha();
        }
        return 0.0f;
    }

    public ActionBarMenu createActionMode(boolean z, String str) {
        boolean zActionModeIsExist = actionModeIsExist(str);
        ActionBarMenu actionBarMenu = this.actionMode;
        if (zActionModeIsExist) {
            return actionBarMenu;
        }
        if (actionBarMenu != null) {
            removeView(actionBarMenu);
            this.actionMode = null;
        }
        this.actionModeTag = str;
        ActionBarMenu actionBarMenu2 = new ActionBarMenu(getContext(), this) { // from class: org.telegram.ui.ActionBar.ActionBar.1
            @Override // android.view.View
            public void setBackgroundColor(int i) {
                ActionBar.this.actionModeColor = i;
                ActionBar actionBar = ActionBar.this;
                if (actionBar.blurredBackground) {
                    return;
                }
                super.setBackgroundColor(actionBar.actionModeColor);
            }

            @Override // android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                Canvas canvas2;
                ActionBar actionBar = ActionBar.this;
                if (actionBar.blurredBackground && this.drawBlur && actionBar.actionModeColor != 0) {
                    ActionBar.this.rectTmp.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
                    ActionBar actionBar2 = ActionBar.this;
                    actionBar2.blurScrimPaint.setColor(actionBar2.actionModeColor);
                    ActionBar actionBar3 = ActionBar.this;
                    canvas2 = canvas;
                    actionBar3.contentView.drawBlurRect(canvas2, 0.0f, actionBar3.rectTmp, actionBar3.blurScrimPaint, true);
                } else {
                    canvas2 = canvas;
                }
                super.dispatchDraw(canvas2);
            }

            @Override // android.view.View
            public void setAlpha(float f) {
                super.setAlpha(f);
                ActionBar.this.invalidate();
                if (ActionBar.this.doOnActionModeFactorChanged != null) {
                    ActionBar.this.doOnActionModeFactorChanged.run();
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                SizeNotifierFrameLayout sizeNotifierFrameLayout = ActionBar.this.contentView;
                if (sizeNotifierFrameLayout != null) {
                    sizeNotifierFrameLayout.blurBehindViews.add(this);
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            public void onDetachedFromWindow() {
                super.onDetachedFromWindow();
                SizeNotifierFrameLayout sizeNotifierFrameLayout = ActionBar.this.contentView;
                if (sizeNotifierFrameLayout != null) {
                    sizeNotifierFrameLayout.blurBehindViews.remove(this);
                }
            }
        };
        this.actionMode = actionBarMenu2;
        actionBarMenu2.setTranslationX(this.glassMode ? -AndroidUtilities.dp(10.0f) : 0.0f);
        this.actionMode.setGlassMode(this.glassMode);
        ActionBarMenu actionBarMenu3 = this.actionMode;
        actionBarMenu3.isActionMode = true;
        actionBarMenu3.setClickable(true);
        if (!this.glassMode) {
            this.actionMode.setBackgroundColor(getThemedColor(Theme.key_actionBarActionModeDefault));
        }
        addView(this.actionMode, indexOfChild(this.backButtonImageView));
        this.actionMode.setPadding(0, this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.actionMode.getLayoutParams();
        layoutParams.height = -1;
        layoutParams.width = -1;
        layoutParams.bottomMargin = this.extraHeight;
        layoutParams.gravity = 5;
        this.actionMode.setLayoutParams(layoutParams);
        this.actionMode.setVisibility(4);
        return this.actionMode;
    }

    public void showActionMode() {
        showActionMode(true, null, null, null, null, null, 0);
    }

    public void showActionMode(boolean z) {
        showActionMode(z, null, null, null, null, null, 0);
    }

    public void showActionMode(boolean z, View view, View view2, View[] viewArr, final boolean[] zArr, View view3, int i) {
        if (this.actionMode == null || this.actionModeVisible) {
            return;
        }
        this.actionModeVisible = true;
        checkMenuItemsWidth();
        if (z) {
            ArrayList arrayList = new ArrayList();
            Property property = View.ALPHA;
            arrayList.add(ObjectAnimator.ofFloat(this.actionMode, (Property<ActionBarMenu, Float>) property, 0.0f, 1.0f));
            if (viewArr != null) {
                for (View view4 : viewArr) {
                    if (view4 != null) {
                        arrayList.add(ObjectAnimator.ofFloat(view4, (Property<View, Float>) property, 1.0f, 0.0f));
                    }
                }
            }
            if (view2 != null) {
                arrayList.add(ObjectAnimator.ofFloat(view2, (Property<View, Float>) property, 0.0f, 1.0f));
            }
            Property property2 = View.TRANSLATION_Y;
            if (view3 != null) {
                arrayList.add(ObjectAnimator.ofFloat(view3, (Property<View, Float>) property2, i));
                this.actionModeTranslationView = view3;
            }
            this.actionModeExtraView = view;
            this.actionModeShowingView = view2;
            this.actionModeHidingViews = viewArr;
            if (view != null) {
                arrayList.add(ObjectAnimator.ofFloat(view, (Property<View, Float>) property2, 0.0f));
            }
            if (this.actionModeColor == 0) {
                if (!this.isSearchFieldVisible) {
                    SimpleTextView simpleTextView = this.titleTextView[0];
                    if (simpleTextView != null) {
                        arrayList.add(ObjectAnimator.ofFloat(simpleTextView, (Property<SimpleTextView, Float>) property, 0.0f));
                    }
                    if (this.subtitleTextView != null && !TextUtils.isEmpty(this.subtitle)) {
                        arrayList.add(ObjectAnimator.ofFloat(this.subtitleTextView, (Property<SimpleTextView, Float>) property, 0.0f));
                    }
                }
                ActionBarMenu actionBarMenu = this.menu;
                if (actionBarMenu != null) {
                    arrayList.add(ObjectAnimator.ofFloat(actionBarMenu, (Property<ActionBarMenu, Float>) property, 0.0f));
                }
            }
            int i2 = this.actionModeColor;
            if (i2 == 0) {
                i2 = this.actionBarColor;
            }
            if (i2 == 0 || this.glassMode) {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
            } else if (ColorUtils.calculateLuminance(i2) < 0.699999988079071d) {
                AndroidUtilities.setLightStatusBar((Activity) getContext(), false);
            } else {
                AndroidUtilities.setLightStatusBar((Activity) getContext(), true);
            }
            AnimatorSet animatorSet = this.actionModeAnimation;
            if (animatorSet != null) {
                animatorSet.cancel();
            }
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.actionModeAnimation = animatorSet2;
            animatorSet2.playTogether(arrayList);
            if (this.backgroundUpdateListener != null) {
                ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.ActionBar$$ExternalSyntheticLambda4
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                        this.f$0.lambda$showActionMode$2(valueAnimator);
                    }
                });
                this.actionModeAnimation.playTogether(valueAnimatorOfFloat);
            }
            this.actionModeAnimation.setDuration(200L);
            this.actionModeAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animator) {
                    ActionBar.this.actionMode.setVisibility(0);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    boolean[] zArr2;
                    if (ActionBar.this.actionModeAnimation == null || !ActionBar.this.actionModeAnimation.equals(animator)) {
                        return;
                    }
                    ActionBar.this.actionModeAnimation = null;
                    if (ActionBar.this.titleTextView[0] != null) {
                        ActionBar.this.titleTextView[0].setVisibility(4);
                    }
                    if (ActionBar.this.subtitleTextView != null && !TextUtils.isEmpty(ActionBar.this.subtitle)) {
                        ActionBar.this.subtitleTextView.setVisibility(4);
                    }
                    ActionBarMenu actionBarMenu2 = ActionBar.this.menu;
                    if (actionBarMenu2 != null) {
                        actionBarMenu2.setVisibility(4);
                    }
                    if (ActionBar.this.actionModeHidingViews != null) {
                        for (int i3 = 0; i3 < ActionBar.this.actionModeHidingViews.length; i3++) {
                            if (ActionBar.this.actionModeHidingViews[i3] != null && ((zArr2 = zArr) == null || i3 >= zArr2.length || zArr2[i3])) {
                                ActionBar.this.actionModeHidingViews[i3].setVisibility(4);
                            }
                        }
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    if (ActionBar.this.actionModeAnimation == null || !ActionBar.this.actionModeAnimation.equals(animator)) {
                        return;
                    }
                    ActionBar.this.actionModeAnimation = null;
                }
            });
            this.actionModeAnimation.start();
            ImageView imageView = this.backButtonImageView;
            if (imageView != null) {
                Drawable drawable = imageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setRotation(1.0f, true);
                } else if (drawable instanceof MenuDrawable) {
                    ((MenuDrawable) drawable).setRotation(1.0f, true);
                }
                this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsActionModeBackgroundColor));
                return;
            }
            return;
        }
        float f = 0.0f;
        this.actionMode.setAlpha(1.0f);
        if (viewArr != null) {
            int length = viewArr.length;
            int i3 = 0;
            while (i3 < length) {
                View view5 = viewArr[i3];
                if (view5 != null) {
                    view5.setAlpha(f);
                }
                i3++;
                f = 0.0f;
            }
        }
        if (view2 != null) {
            view2.setAlpha(1.0f);
        }
        if (view3 != null) {
            view3.setTranslationY(i);
            this.actionModeTranslationView = view3;
        }
        this.actionModeExtraView = view;
        if (view != null) {
            view.setTranslationY(0.0f);
        }
        this.actionModeShowingView = view2;
        this.actionModeHidingViews = viewArr;
        int i4 = this.actionModeColor;
        if (i4 == 0) {
            i4 = this.actionBarColor;
        }
        if (i4 == 0 || this.glassMode) {
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
        } else if (ColorUtils.calculateLuminance(i4) < 0.699999988079071d) {
            AndroidUtilities.setLightStatusBar((Activity) getContext(), false);
        } else {
            AndroidUtilities.setLightStatusBar((Activity) getContext(), true);
        }
        this.actionMode.setVisibility(0);
        SimpleTextView simpleTextView2 = this.titleTextView[0];
        if (simpleTextView2 != null) {
            simpleTextView2.setVisibility(4);
        }
        if (this.subtitleTextView != null && !TextUtils.isEmpty(this.subtitle)) {
            this.subtitleTextView.setVisibility(4);
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        if (actionBarMenu2 != null) {
            actionBarMenu2.setVisibility(4);
        }
        if (this.actionModeHidingViews != null) {
            int i5 = 0;
            while (true) {
                View[] viewArr2 = this.actionModeHidingViews;
                if (i5 >= viewArr2.length) {
                    break;
                }
                View view6 = viewArr2[i5];
                if (view6 != null && (zArr == null || i5 >= zArr.length || zArr[i5])) {
                    view6.setVisibility(4);
                }
                i5++;
            }
        }
        ImageView imageView2 = this.backButtonImageView;
        if (imageView2 != null) {
            Drawable drawable2 = imageView2.getDrawable();
            if (drawable2 instanceof BackDrawable) {
                ((BackDrawable) drawable2).setRotation(1.0f, false);
            } else if (drawable2 instanceof MenuDrawable) {
                ((MenuDrawable) drawable2).setRotation(1.0f, false);
            }
            this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsActionModeBackgroundColor));
        }
    }

    public /* synthetic */ void lambda$showActionMode$2(ValueAnimator valueAnimator) {
        Runnable runnable = this.backgroundUpdateListener;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void hideActionMode() {
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu == null || !this.actionModeVisible) {
            return;
        }
        actionBarMenu.hideAllPopupMenus();
        this.actionModeVisible = false;
        checkMenuItemsWidth();
        ArrayList arrayList = new ArrayList();
        Property property = View.ALPHA;
        arrayList.add(ObjectAnimator.ofFloat(this.actionMode, (Property<ActionBarMenu, Float>) property, 0.0f));
        View[] viewArr = this.actionModeHidingViews;
        if (viewArr != null) {
            for (View view : viewArr) {
                if (view != null) {
                    view.setVisibility(0);
                    arrayList.add(ObjectAnimator.ofFloat(view, (Property<View, Float>) property, 1.0f));
                }
            }
        }
        View view2 = this.actionModeTranslationView;
        Property property2 = View.TRANSLATION_Y;
        if (view2 != null) {
            arrayList.add(ObjectAnimator.ofFloat(view2, (Property<View, Float>) property2, 0.0f));
            this.actionModeTranslationView = null;
        }
        View view3 = this.actionModeShowingView;
        if (view3 != null) {
            arrayList.add(ObjectAnimator.ofFloat(view3, (Property<View, Float>) property, 0.0f));
        }
        View view4 = this.actionModeExtraView;
        if (view4 != null) {
            arrayList.add(ObjectAnimator.ofFloat(view4, (Property<View, Float>) property2, view4.getMeasuredHeight()));
        }
        if (!this.isSearchFieldVisible) {
            SimpleTextView simpleTextView = this.titleTextView[0];
            if (simpleTextView != null) {
                arrayList.add(ObjectAnimator.ofFloat(simpleTextView, (Property<SimpleTextView, Float>) property, 1.0f));
            }
            if (this.subtitleTextView != null && !TextUtils.isEmpty(this.subtitle)) {
                arrayList.add(ObjectAnimator.ofFloat(this.subtitleTextView, (Property<SimpleTextView, Float>) property, 1.0f));
            }
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        if (actionBarMenu2 != null) {
            arrayList.add(ObjectAnimator.ofFloat(actionBarMenu2, (Property<ActionBarMenu, Float>) property, 1.0f));
        }
        int i = this.actionBarColor;
        if (i == 0 || this.glassMode) {
            NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
        } else if (ColorUtils.calculateLuminance(i) < 0.699999988079071d) {
            AndroidUtilities.setLightStatusBar((Activity) getContext(), false);
        } else {
            AndroidUtilities.setLightStatusBar((Activity) getContext(), true);
        }
        AnimatorSet animatorSet = this.actionModeAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.actionModeAnimation = animatorSet2;
        animatorSet2.playTogether(arrayList);
        if (this.backgroundUpdateListener != null) {
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.ActionBar$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$hideActionMode$3(valueAnimator);
                }
            });
            this.actionModeAnimation.playTogether(valueAnimatorOfFloat);
        }
        this.actionModeAnimation.setDuration(200L);
        this.actionModeAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (ActionBar.this.actionModeAnimation == null || !ActionBar.this.actionModeAnimation.equals(animator)) {
                    return;
                }
                ActionBar.this.actionModeAnimation = null;
                ActionBar.this.actionMode.setVisibility(4);
                if (ActionBar.this.actionModeExtraView != null) {
                    ActionBar.this.actionModeExtraView.setVisibility(4);
                }
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                if (ActionBar.this.actionModeAnimation == null || !ActionBar.this.actionModeAnimation.equals(animator)) {
                    return;
                }
                ActionBar.this.actionModeAnimation = null;
            }
        });
        this.actionModeAnimation.start();
        if (!this.isSearchFieldVisible) {
            SimpleTextView simpleTextView2 = this.titleTextView[0];
            if (simpleTextView2 != null) {
                simpleTextView2.setVisibility(0);
            }
            if (this.subtitleTextView != null && !TextUtils.isEmpty(this.subtitle)) {
                this.subtitleTextView.setVisibility(0);
            }
        }
        ActionBarMenu actionBarMenu3 = this.menu;
        if (actionBarMenu3 != null) {
            actionBarMenu3.setVisibility(0);
        }
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof BackDrawable) {
                ((BackDrawable) drawable).setRotation(0.0f, true);
            } else if (drawable instanceof MenuDrawable) {
                ((MenuDrawable) drawable).setRotation(0.0f, true);
            }
            this.backButtonImageView.setBackgroundDrawable(Theme.createSelectorDrawable(this.itemsBackgroundColor));
        }
    }

    public /* synthetic */ void lambda$hideActionMode$3(ValueAnimator valueAnimator) {
        Runnable runnable = this.backgroundUpdateListener;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void showActionModeTop() {
        if (this.occupyStatusBar && this.actionModeTop == null) {
            View view = new View(getContext());
            this.actionModeTop = view;
            view.setBackgroundColor(getThemedColor(Theme.key_actionBarActionModeDefaultTop));
            addView(this.actionModeTop);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.actionModeTop.getLayoutParams();
            layoutParams.height = AndroidUtilities.statusBarHeight;
            layoutParams.width = -1;
            layoutParams.gravity = 51;
            this.actionModeTop.setLayoutParams(layoutParams);
        }
    }

    public void setActionModeTopColor(int i) {
        View view = this.actionModeTop;
        if (view != null) {
            view.setBackgroundColor(i);
        }
    }

    public void setSearchTextColor(int i, boolean z) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setSearchTextColor(i, z);
        }
    }

    public void setSearchCursorColor(int i) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setSearchCursorColor(i);
        }
    }

    public void setActionModeColor(int i) {
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            actionBarMenu.setBackgroundColor(i);
        }
    }

    public void setActionModeOverrideColor(int i) {
        this.actionModeColor = i;
    }

    @Override // android.view.View
    public void setBackgroundColor(int i) {
        this.actionBarColor = i;
        if (!this.blurredBackground) {
            super.setBackgroundColor(i);
        }
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof MenuDrawable) {
                ((MenuDrawable) drawable).setBackColor(i);
            }
        }
    }

    public int getBackgroundColor() {
        return this.actionBarColor;
    }

    public boolean isActionModeShowed() {
        return this.actionMode != null && this.actionModeVisible;
    }

    public boolean isActionModeShowed(String str) {
        if (this.actionMode == null || !this.actionModeVisible) {
            return false;
        }
        String str2 = this.actionModeTag;
        if (str2 == null && str == null) {
            return true;
        }
        return str2 != null && str2.equals(str);
    }

    public void listenToBackgroundUpdate(Runnable runnable) {
        this.backgroundUpdateListener = runnable;
    }

    public void onSearchFieldVisibilityChanged(final boolean z) {
        Property property;
        this.isSearchFieldVisible = z;
        checkMenuItemsWidth();
        AnimatorSet animatorSet = this.searchVisibleAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.searchVisibleAnimator = new AnimatorSet();
        final ArrayList arrayList = new ArrayList();
        final boolean zOnSearchChangedIgnoreTitles = onSearchChangedIgnoreTitles();
        if (!zOnSearchChangedIgnoreTitles) {
            SimpleTextView simpleTextView = this.titleTextView[0];
            if (simpleTextView != null) {
                arrayList.add(simpleTextView);
            }
            if (this.subtitleTextView != null && !TextUtils.isEmpty(this.subtitle)) {
                arrayList.add(this.subtitleTextView);
                this.subtitleTextView.setVisibility(z ? 4 : 0);
            }
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.searchFieldVisibleAlpha, z ? 1.0f : 0.0f);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.ActionBar$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$onSearchFieldVisibilityChanged$4(valueAnimator);
            }
        });
        this.searchVisibleAnimator.playTogether(valueAnimatorOfFloat);
        int i = 0;
        while (true) {
            int size = arrayList.size();
            property = View.ALPHA;
            if (i >= size) {
                break;
            }
            View view = (View) arrayList.get(i);
            float f = 0.95f;
            if (!z) {
                view.setVisibility(0);
                view.setAlpha(0.0f);
                view.setScaleX(0.95f);
                view.setScaleY(0.95f);
            }
            this.searchVisibleAnimator.playTogether(ObjectAnimator.ofFloat(view, (Property<View, Float>) property, z ? 0.0f : 1.0f));
            this.searchVisibleAnimator.playTogether(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_Y, z ? 0.95f : 1.0f));
            AnimatorSet animatorSet2 = this.searchVisibleAnimator;
            if (!z) {
                f = 1.0f;
            }
            animatorSet2.playTogether(ObjectAnimator.ofFloat(view, (Property<View, Float>) View.SCALE_X, f));
            i++;
        }
        BackupImageView backupImageView = this.avatarSearchImageView;
        if (backupImageView != null) {
            backupImageView.setVisibility(0);
            this.searchVisibleAnimator.playTogether(ObjectAnimator.ofFloat(this.avatarSearchImageView, (Property<BackupImageView, Float>) property, z ? 1.0f : 0.0f));
        }
        this.centerScale = true;
        requestLayout();
        this.searchVisibleAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.4
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    View view2 = (View) arrayList.get(i2);
                    if (z) {
                        view2.setVisibility(4);
                        view2.setAlpha(0.0f);
                    } else {
                        view2.setAlpha(1.0f);
                    }
                }
                if (z && !zOnSearchChangedIgnoreTitles) {
                    if (ActionBar.this.titleTextView[0] != null) {
                        ActionBar.this.titleTextView[0].setVisibility(8);
                    }
                    if (ActionBar.this.titleTextView[1] != null) {
                        ActionBar.this.titleTextView[1].setVisibility(8);
                    }
                }
                if (ActionBar.this.avatarSearchImageView == null || z) {
                    return;
                }
                ActionBar.this.avatarSearchImageView.setVisibility(8);
            }
        });
        this.searchVisibleAnimator.setDuration(150L).start();
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof MenuDrawable) {
                MenuDrawable menuDrawable = (MenuDrawable) drawable;
                menuDrawable.setRotateToBack(true);
                menuDrawable.setRotation(z ? 1.0f : 0.0f, true);
            }
        }
    }

    public /* synthetic */ void lambda$onSearchFieldVisibilityChanged$4(ValueAnimator valueAnimator) {
        ActionBarMenu actionBarMenu;
        this.searchFieldVisibleAlpha = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        if (this.glassDrawable != null && this.glassModeHasAvatar) {
            float fDp = AndroidUtilities.dp(23.0f);
            float fLerp = AndroidUtilities.lerp(this.glassDrawableLeftRadius, fDp, this.searchFieldVisibleAlpha);
            this.glassDrawable.setRadius(fLerp, fDp, fDp, fLerp);
            invalidate();
        }
        if (this.glassMode && (actionBarMenu = this.menu) != null) {
            actionBarMenu.setTranslationX(-AndroidUtilities.lerp(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(5.0f), this.searchFieldVisibleAlpha));
        }
        Runnable runnable = this.backgroundUpdateListener;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void setInterceptTouches(boolean z) {
        this.interceptTouches = z;
    }

    public void setInterceptTouchEventListener(View.OnTouchListener onTouchListener) {
        this.interceptTouchEventListener = onTouchListener;
    }

    public void setExtraHeight(int i) {
        this.extraHeight = i;
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) actionBarMenu.getLayoutParams();
            layoutParams.bottomMargin = this.extraHeight;
            this.actionMode.setLayoutParams(layoutParams);
        }
    }

    public void closeSearchField() {
        closeSearchField(true);
    }

    public void closeSearchField(boolean z) {
        ActionBarMenu actionBarMenu;
        if (!this.isSearchFieldVisible || (actionBarMenu = this.menu) == null) {
            return;
        }
        actionBarMenu.closeSearchField(z);
    }

    public void openSearchField(String str, boolean z) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu == null || str == null) {
            return;
        }
        boolean z2 = this.isSearchFieldVisible;
        actionBarMenu.openSearchField(!z2, !z2, str, z);
    }

    public void setSearchFilter(FiltersView.MediaFilterData mediaFilterData) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setFilter(mediaFilterData);
        }
    }

    public void setSearchFieldText(String str) {
        this.menu.setSearchFieldText(str);
    }

    public void onSearchPressed() {
        this.menu.onSearchPressed();
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            imageView.setEnabled(z);
        }
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.setEnabled(z);
        }
        ActionBarMenu actionBarMenu2 = this.actionMode;
        if (actionBarMenu2 != null) {
            actionBarMenu2.setEnabled(z);
        }
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        if (this.ignoreLayoutRequest) {
            return;
        }
        super.requestLayout();
    }

    private void resetCenterTitleLayoutAnimation() {
        ValueAnimator valueAnimator = this.centerTitleLayoutAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.centerTitleLayoutAnimator = null;
        }
        this.animatedCenterTitleX = Float.NaN;
        this.animatedCenterTitleAvailableWidth = Float.NaN;
        this.centerTitleAnimationTargetX = Integer.MIN_VALUE;
        this.centerTitleAnimationTargetWidth = -1;
    }

    private boolean shouldUseAdaptiveCenterTitle() {
        ActionBarMenu actionBarMenu;
        return shouldCenterTitle() && (actionBarMenu = this.menu) != null && actionBarMenu.getVisibleItemsCount() > 2;
    }

    private int getCenterTitleRightBound(int i) {
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null && actionBarMenu.getVisibility() != 8) {
            int measuredWidth = this.menu.getMeasuredWidth();
            if (shouldCenterTitle()) {
                measuredWidth = this.menu.getVisibleItemsMeasuredWidthForCenterTitle();
            }
            return i - measuredWidth;
        }
        return i - AndroidUtilities.dp(16.0f);
    }

    private int getAdaptiveCenterTitleAvailableWidth(int i, int i2) {
        return Math.max(0, Math.max(i2, getCenterTitleRightBound(i)) - i2);
    }

    private int getAdaptiveCenterTitleCenterX(int i, int i2) {
        return i2 + ((Math.max(i2, getCenterTitleRightBound(i)) - i2) / 2);
    }

    private int getCenteredTitleAvailableWidth(int i, int i2, boolean z) {
        if (z) {
            return getAdaptiveCenterTitleAvailableWidth(i, i2);
        }
        int iMax = Math.max(0, i - AndroidUtilities.dp(120.0f));
        int iMax2 = Math.max(i2, getCenterTitleRightBound(i));
        int i3 = i / 2;
        return Math.min(iMax, Math.max(0, Math.min(i3 - i2, iMax2 - i3)) * 2);
    }

    private int getTargetCenterTitleX(int i, int i2, boolean z) {
        return z ? getAdaptiveCenterTitleCenterX(i, i2) : i / 2;
    }

    private int getAnimatedCenterTitleX(int i) {
        return Float.isNaN(this.animatedCenterTitleX) ? i : Math.round(this.animatedCenterTitleX);
    }

    private int getAnimatedCenterTitleAvailableWidth(int i) {
        return Float.isNaN(this.animatedCenterTitleAvailableWidth) ? i : Math.max(0, Math.round(this.animatedCenterTitleAvailableWidth));
    }

    private int getTitleViewCenterX(int i, SimpleTextView simpleTextView) {
        if (this.useContainerForTitles && this.titlesContainer != null && simpleTextView != null) {
            ViewParent parent = simpleTextView.getParent();
            FrameLayout frameLayout = this.titlesContainer;
            if (parent == frameLayout) {
                return Math.round(i - frameLayout.getTranslationX());
            }
        }
        return i;
    }

    private boolean shouldAnimateCenterTitleLayout() {
        return (!this.attached || getWindowToken() == null || this.isSearchFieldVisible || this.titleAnimationRunning) ? false : true;
    }

    private void updateCenterTitleLayoutAnimation(final int i, final int i2, boolean z) {
        if (!shouldCenterTitle()) {
            resetCenterTitleLayoutAnimation();
            return;
        }
        if (Float.isNaN(this.animatedCenterTitleX) || Float.isNaN(this.animatedCenterTitleAvailableWidth)) {
            this.animatedCenterTitleX = i;
            this.animatedCenterTitleAvailableWidth = i2;
            return;
        }
        if (!z) {
            ValueAnimator valueAnimator = this.centerTitleLayoutAnimator;
            if (valueAnimator != null) {
                valueAnimator.cancel();
                this.centerTitleLayoutAnimator = null;
            }
            this.animatedCenterTitleX = i;
            this.animatedCenterTitleAvailableWidth = i2;
            this.centerTitleAnimationTargetX = Integer.MIN_VALUE;
            this.centerTitleAnimationTargetWidth = -1;
            return;
        }
        final float f = this.animatedCenterTitleX;
        final float f2 = this.animatedCenterTitleAvailableWidth;
        float f3 = i;
        if (Math.abs(f - f3) < 0.5f) {
            float f4 = i2;
            if (Math.abs(f2 - f4) < 0.5f) {
                ValueAnimator valueAnimator2 = this.centerTitleLayoutAnimator;
                if (valueAnimator2 != null && !valueAnimator2.isRunning()) {
                    this.centerTitleLayoutAnimator = null;
                }
                this.animatedCenterTitleX = f3;
                this.animatedCenterTitleAvailableWidth = f4;
                this.centerTitleAnimationTargetX = Integer.MIN_VALUE;
                this.centerTitleAnimationTargetWidth = -1;
                return;
            }
        }
        ValueAnimator valueAnimator3 = this.centerTitleLayoutAnimator;
        if (valueAnimator3 != null && this.centerTitleAnimationTargetX == i && this.centerTitleAnimationTargetWidth == i2) {
            return;
        }
        if (valueAnimator3 != null) {
            valueAnimator3.cancel();
            this.centerTitleLayoutAnimator = null;
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        this.centerTitleLayoutAnimator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.setDuration(260L);
        this.centerTitleLayoutAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.centerTitleLayoutAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.ActionBar$$ExternalSyntheticLambda6
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator4) {
                this.f$0.lambda$updateCenterTitleLayoutAnimation$5(f, i, f2, i2, valueAnimator4);
            }
        });
        this.centerTitleLayoutAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.5
            private boolean cancelled;

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                this.cancelled = true;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (ActionBar.this.centerTitleLayoutAnimator == animator) {
                    ActionBar.this.centerTitleLayoutAnimator = null;
                }
                if (!this.cancelled) {
                    ActionBar.this.animatedCenterTitleX = i;
                    ActionBar.this.animatedCenterTitleAvailableWidth = i2;
                }
                ActionBar.this.centerTitleAnimationTargetX = Integer.MIN_VALUE;
                ActionBar.this.centerTitleAnimationTargetWidth = -1;
            }
        });
        this.centerTitleAnimationTargetX = i;
        this.centerTitleAnimationTargetWidth = i2;
        this.centerTitleLayoutAnimator.start();
    }

    public /* synthetic */ void lambda$updateCenterTitleLayoutAnimation$5(float f, int i, float f2, int i2, ValueAnimator valueAnimator) {
        float fFloatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.animatedCenterTitleX = f + ((i - f) * fFloatValue);
        this.animatedCenterTitleAvailableWidth = f2 + ((i2 - f2) * fFloatValue);
        requestLayout();
    }

    @Override // android.view.ViewGroup
    public void onViewAdded(View view) {
        super.onViewAdded(view);
    }

    public void setAdditionalTextLeft(int i) {
        this.additionalTextLeft = i;
    }

    @Override // android.widget.FrameLayout, android.view.View
    public void onMeasure(int i, int i2) {
        int titleLeft;
        SimpleTextView simpleTextView;
        int i3;
        int measuredWidth;
        SimpleTextView simpleTextView2;
        int iMakeMeasureSpec;
        ActionBar actionBar = this;
        int size = View.MeasureSpec.getSize(i);
        int size2 = View.MeasureSpec.getSize(i2);
        int currentActionBarHeight = getCurrentActionBarHeight();
        int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(currentActionBarHeight, TLObject.FLAG_30);
        int i4 = actionBar.lastMeasuredWidth;
        if (i4 > 0 && i4 != size) {
            actionBar.resetCenterTitleLayoutAnimation();
        }
        actionBar.lastMeasuredWidth = size;
        int i5 = 1;
        actionBar.ignoreLayoutRequest = true;
        View view = actionBar.actionModeTop;
        if (view != null) {
            ((FrameLayout.LayoutParams) view.getLayoutParams()).height = AndroidUtilities.statusBarHeight;
        }
        ActionBarMenu actionBarMenu = actionBar.actionMode;
        if (actionBarMenu != null) {
            actionBarMenu.setPadding(0, actionBar.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        }
        actionBar.ignoreLayoutRequest = false;
        actionBar.setMeasuredDimension(size, currentActionBarHeight + (actionBar.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0) + actionBar.extraHeight);
        ImageView imageView = actionBar.backButtonImageView;
        if (imageView != null && imageView.getVisibility() != 8) {
            actionBar.backButtonImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(54.0f), TLObject.FLAG_30), iMakeMeasureSpec2);
            titleLeft = actionBar.getTitleLeft(true);
        } else {
            titleLeft = actionBar.getTitleLeft(false);
        }
        ActionBarMenu actionBarMenu2 = actionBar.menu;
        if (actionBarMenu2 != null && actionBarMenu2.getVisibility() != 8) {
            float f = 66.0f;
            if (actionBar.menu.searchFieldVisible() && !actionBar.isSearchFieldVisible) {
                actionBar.menu.measure(View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE), iMakeMeasureSpec2);
                int itemsMeasuredWidth = actionBar.menu.getItemsMeasuredWidth(true);
                if (actionBar.menuOccupyBack) {
                    f = 0.0f;
                } else if (AndroidUtilities.isTablet()) {
                    f = 74.0f;
                }
                iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec((size - AndroidUtilities.dp(f)) + actionBar.menu.getItemsMeasuredWidth(true), TLObject.FLAG_30);
                if (!actionBar.isMenuOffsetSuppressed) {
                    actionBar.menu.translateXItems(-itemsMeasuredWidth);
                }
            } else if (actionBar.isSearchFieldVisible) {
                if (actionBar.menuOccupyBack) {
                    f = 0.0f;
                } else if (AndroidUtilities.isTablet()) {
                    f = 74.0f;
                }
                iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size - AndroidUtilities.dp(f), TLObject.FLAG_30);
                if (!actionBar.isMenuOffsetSuppressed) {
                    actionBar.menu.translateXItems(0.0f);
                }
            } else {
                iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(size, Integer.MIN_VALUE);
                if (!actionBar.isMenuOffsetSuppressed) {
                    actionBar.menu.translateXItems(0.0f);
                }
            }
            actionBar.menu.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
        }
        if (!actionBar.shouldCenterTitle()) {
            actionBar.resetCenterTitleLayoutAnimation();
        }
        boolean zShouldUseAdaptiveCenterTitle = actionBar.shouldUseAdaptiveCenterTitle();
        int i6 = 0;
        while (i6 < 2) {
            SimpleTextView simpleTextView3 = actionBar.titleTextView[0];
            if ((simpleTextView3 == null || simpleTextView3.getVisibility() == 8) && ((simpleTextView = actionBar.subtitleTextView) == null || simpleTextView.getVisibility() == 8)) {
                i3 = i5;
            } else {
                if (actionBar.shouldCenterTitle()) {
                    measuredWidth = actionBar.getAnimatedCenterTitleAvailableWidth(actionBar.getCenteredTitleAvailableWidth(size, titleLeft, zShouldUseAdaptiveCenterTitle));
                } else {
                    ActionBarMenu actionBarMenu3 = actionBar.menu;
                    measuredWidth = (((size - (actionBarMenu3 != null ? actionBarMenu3.getMeasuredWidth() : 0)) - AndroidUtilities.dp(16.0f)) - titleLeft) - actionBar.titleRightMargin;
                }
                boolean z = actionBar.fromBottom;
                int i7 = 20;
                if (((z && i6 == 0) || (!z && i6 == i5)) && actionBar.overlayTitleAnimation && actionBar.titleAnimationRunning) {
                    SimpleTextView simpleTextView4 = actionBar.titleTextView[i6];
                    i3 = i5;
                    if (actionBar.glassMode) {
                        i7 = 17;
                    } else if (!AndroidUtilities.isTablet() && actionBar.getResources().getConfiguration().orientation == 2) {
                        i7 = 18;
                    }
                    simpleTextView4.setTextSize(i7);
                } else {
                    i3 = i5;
                    SimpleTextView simpleTextView5 = actionBar.titleTextView[0];
                    if (simpleTextView5 != null && simpleTextView5.getVisibility() != 8 && (simpleTextView2 = actionBar.subtitleTextView) != null && simpleTextView2.getVisibility() != 8) {
                        SimpleTextView simpleTextView6 = actionBar.titleTextView[i6];
                        if (simpleTextView6 != null) {
                            if (actionBar.glassMode) {
                                i7 = 17;
                            } else if (!AndroidUtilities.isTablet()) {
                                i7 = 18;
                            }
                            simpleTextView6.setTextSize(i7);
                        }
                        actionBar.subtitleTextView.setTextSize(AndroidUtilities.isTablet() ? 16 : 14);
                        SimpleTextView simpleTextView7 = actionBar.additionalSubtitleTextView;
                        if (simpleTextView7 != null) {
                            simpleTextView7.setTextSize(AndroidUtilities.isTablet() ? 16 : 14);
                        }
                    } else {
                        SimpleTextView simpleTextView8 = actionBar.titleTextView[i6];
                        if (simpleTextView8 != null && simpleTextView8.getVisibility() != 8) {
                            SimpleTextView simpleTextView9 = actionBar.titleTextView[i6];
                            if (actionBar.glassMode) {
                                i7 = 17;
                            } else if (!AndroidUtilities.isTablet() && actionBar.getResources().getConfiguration().orientation == 2) {
                                i7 = 18;
                            }
                            simpleTextView9.setTextSize(i7);
                        }
                        SimpleTextView simpleTextView10 = actionBar.subtitleTextView;
                        if (simpleTextView10 != null && simpleTextView10.getVisibility() != 8) {
                            actionBar.subtitleTextView.setTextSize((AndroidUtilities.isTablet() || actionBar.getResources().getConfiguration().orientation != 2) ? 16 : 14);
                        }
                        SimpleTextView simpleTextView11 = actionBar.additionalSubtitleTextView;
                        if (simpleTextView11 != null) {
                            simpleTextView11.setTextSize((AndroidUtilities.isTablet() || actionBar.getResources().getConfiguration().orientation != 2) ? 16 : 14);
                        }
                    }
                }
                SimpleTextView simpleTextView12 = actionBar.titleTextView[i6];
                if (simpleTextView12 != null && simpleTextView12.getVisibility() != 8) {
                    actionBar.titleTextView[i6].measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(24.0f) + actionBar.titleTextView[i6].getPaddingTop() + actionBar.titleTextView[i6].getPaddingBottom(), Integer.MIN_VALUE));
                    boolean z2 = actionBar.centerScale;
                    SimpleTextView[] simpleTextViewArr = actionBar.titleTextView;
                    if (z2) {
                        CharSequence text = simpleTextViewArr[i6].getText();
                        SimpleTextView simpleTextView13 = actionBar.titleTextView[i6];
                        simpleTextView13.setPivotX(simpleTextView13.getTextPaint().measureText(text, 0, text.length()) / 2.0f);
                        actionBar.titleTextView[i6].setPivotY(AndroidUtilities.dp(24.0f) >> 1);
                    } else {
                        simpleTextViewArr[i6].setPivotX(0.0f);
                        actionBar.titleTextView[i6].setPivotY(0.0f);
                    }
                }
                SimpleTextView simpleTextView14 = actionBar.subtitleTextView;
                if (simpleTextView14 != null && simpleTextView14.getVisibility() != 8) {
                    actionBar.subtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
                }
                ActionBarAnimatedSubtitleOverlayContainer actionBarAnimatedSubtitleOverlayContainer = actionBar.additionalSubTitleOverlayContainer;
                if (actionBarAnimatedSubtitleOverlayContainer != null) {
                    actionBarAnimatedSubtitleOverlayContainer.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(size2, Integer.MIN_VALUE));
                }
                SimpleTextView simpleTextView15 = actionBar.additionalSubtitleTextView;
                if (simpleTextView15 != null && simpleTextView15.getVisibility() != 8) {
                    actionBar.additionalSubtitleTextView.measure(View.MeasureSpec.makeMeasureSpec(measuredWidth, Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(20.0f), Integer.MIN_VALUE));
                }
            }
            i6++;
            i5 = i3;
        }
        int i8 = i5;
        BackupImageView backupImageView = actionBar.avatarSearchImageView;
        if (backupImageView != null) {
            backupImageView.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(42.0f), TLObject.FLAG_30));
        }
        int childCount = actionBar.getChildCount();
        int i9 = 0;
        while (i9 < childCount) {
            View childAt = actionBar.getChildAt(i9);
            if (childAt.getVisibility() != 8) {
                SimpleTextView[] simpleTextViewArr2 = actionBar.titleTextView;
                if (childAt != simpleTextViewArr2[0] && childAt != simpleTextViewArr2[i8] && childAt != actionBar.additionalSubTitleOverlayContainer && childAt != actionBar.subtitleTextView && childAt != actionBar.menu && childAt != actionBar.backButtonImageView && childAt != actionBar.additionalSubtitleTextView && childAt != actionBar.avatarSearchImageView) {
                    actionBar.measureChildWithMargins(childAt, i, 0, View.MeasureSpec.makeMeasureSpec(actionBar.getMeasuredHeight(), TLObject.FLAG_30), 0);
                }
            }
            i9++;
            actionBar = this;
        }
    }

    public void setMenuOffsetSuppressed(boolean z) {
        this.isMenuOffsetSuppressed = z;
    }

    /* JADX WARN: Code duplicated, block: B:127:0x034b  */
    /* JADX WARN: Code duplicated, block: B:129:0x034f  */
    /* JADX WARN: Code duplicated, block: B:130:0x0352  */
    /* JADX WARN: Code duplicated, block: B:132:0x035a  */
    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public void onLayout(boolean z, int i, int i2, int i3, int i4) {
        int titleLeft;
        int i5;
        int i6;
        int i7;
        int i8;
        int i9;
        int i10;
        SimpleTextView simpleTextView;
        int currentActionBarHeight;
        int measuredWidth;
        int i11 = this.occupyStatusBar ? AndroidUtilities.statusBarHeight : 0;
        if (this.prevWidth != getMeasuredWidth()) {
            this.prevWidth = getMeasuredWidth();
            checkAvatarContainerWidth(this.animatorAvatarContainerWidth.isAnimating());
        }
        ImageView imageView = this.backButtonImageView;
        if (imageView != null && imageView.getVisibility() != 8) {
            ImageView imageView2 = this.backButtonImageView;
            imageView2.layout(0, i11, imageView2.getMeasuredWidth(), this.backButtonImageView.getMeasuredHeight() + i11);
            titleLeft = getTitleLeft(true);
        } else {
            titleLeft = getTitleLeft(false);
        }
        int i12 = titleLeft + this.additionalTextLeft;
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null && actionBarMenu.getVisibility() != 8) {
            if (this.menu.searchFieldVisible()) {
                measuredWidth = AndroidUtilities.dp(this.menuOccupyBack ? 0.0f : AndroidUtilities.isTablet() ? 74.0f : 66.0f);
            } else {
                measuredWidth = (i3 - i) - this.menu.getMeasuredWidth();
            }
            ActionBarMenu actionBarMenu2 = this.menu;
            actionBarMenu2.layout(measuredWidth, i11, actionBarMenu2.getMeasuredWidth() + measuredWidth, this.menu.getMeasuredHeight() + i11);
        }
        boolean zShouldUseAdaptiveCenterTitle = shouldUseAdaptiveCenterTitle();
        int targetCenterTitleX = getTargetCenterTitleX(getMeasuredWidth(), i12, zShouldUseAdaptiveCenterTitle);
        updateCenterTitleLayoutAnimation(targetCenterTitleX, getCenteredTitleAvailableWidth(getMeasuredWidth(), i12, zShouldUseAdaptiveCenterTitle), shouldAnimateCenterTitleLayout());
        int animatedCenterTitleX = getAnimatedCenterTitleX(targetCenterTitleX);
        int i13 = 0;
        while (true) {
            if (i13 >= 2) {
                break;
            }
            SimpleTextView simpleTextView2 = this.titleTextView[i13];
            if (simpleTextView2 != null && simpleTextView2.getVisibility() != 8) {
                boolean z2 = this.fromBottom;
                if ((((!z2 || i13 != 0) && (z2 || i13 != 1)) || !this.overlayTitleAnimation || !this.titleAnimationRunning) && (simpleTextView = this.subtitleTextView) != null && simpleTextView.getVisibility() != 8) {
                    currentActionBarHeight = AndroidUtilities.dp((AndroidUtilities.isTablet() || getResources().getConfiguration().orientation != 2) ? 3.0f : 2.0f) + (((getCurrentActionBarHeight() / 2) - this.titleTextView[i13].getTextHeight()) / 2) + AndroidUtilities.dp(2.0f);
                } else {
                    currentActionBarHeight = (getCurrentActionBarHeight() - this.titleTextView[i13].getTextHeight()) / 2;
                }
                boolean zShouldCenterTitle = shouldCenterTitle();
                SimpleTextView[] simpleTextViewArr = this.titleTextView;
                if (zShouldCenterTitle) {
                    int titleViewCenterX = getTitleViewCenterX(animatedCenterTitleX, simpleTextViewArr[i13]);
                    SimpleTextView simpleTextView3 = this.titleTextView[i13];
                    int i14 = currentActionBarHeight + i11;
                    simpleTextView3.layout(titleViewCenterX - (simpleTextView3.getMeasuredWidth() / 2), i14 - this.titleTextView[i13].getPaddingTop(), titleViewCenterX + (this.titleTextView[i13].getMeasuredWidth() / 2), ((i14 + this.titleTextView[i13].getTextHeight()) - this.titleTextView[i13].getPaddingTop()) + this.titleTextView[i13].getPaddingBottom());
                } else {
                    SimpleTextView simpleTextView4 = simpleTextViewArr[i13];
                    int i15 = currentActionBarHeight + i11;
                    simpleTextView4.layout(i12, i15 - simpleTextView4.getPaddingTop(), this.titleTextView[i13].getMeasuredWidth() + i12, ((i15 + this.titleTextView[i13].getTextHeight()) - this.titleTextView[i13].getPaddingTop()) + this.titleTextView[i13].getPaddingBottom());
                }
            }
            i13++;
        }
        if (this.additionalSubTitleOverlayContainer != null) {
            int currentActionBarHeight2 = ((getCurrentActionBarHeight() / 2) + (((getCurrentActionBarHeight() / 2) - this.additionalSubTitleOverlayContainer.getMeasuredHeight()) / 2)) - AndroidUtilities.dp(2.0f);
            boolean zShouldCenterTitle2 = shouldCenterTitle();
            ActionBarAnimatedSubtitleOverlayContainer actionBarAnimatedSubtitleOverlayContainer = this.additionalSubTitleOverlayContainer;
            if (zShouldCenterTitle2) {
                int i16 = currentActionBarHeight2 + i11;
                actionBarAnimatedSubtitleOverlayContainer.layout(animatedCenterTitleX - (actionBarAnimatedSubtitleOverlayContainer.getMeasuredWidth() / 2), i16, (this.additionalSubTitleOverlayContainer.getMeasuredWidth() / 2) + animatedCenterTitleX, this.additionalSubTitleOverlayContainer.getMeasuredHeight() + i16);
            } else {
                int i17 = currentActionBarHeight2 + i11;
                actionBarAnimatedSubtitleOverlayContainer.layout(i12, i17, actionBarAnimatedSubtitleOverlayContainer.getMeasuredWidth() + i12, this.additionalSubTitleOverlayContainer.getMeasuredHeight() + i17);
            }
        }
        SimpleTextView simpleTextView5 = this.subtitleTextView;
        if (simpleTextView5 != null && simpleTextView5.getVisibility() != 8) {
            int currentActionBarHeight3 = ((getCurrentActionBarHeight() / 2) + (((getCurrentActionBarHeight() / 2) - this.subtitleTextView.getTextHeight()) / 2)) - AndroidUtilities.dp(2.0f);
            boolean zShouldCenterTitle3 = shouldCenterTitle();
            SimpleTextView simpleTextView6 = this.subtitleTextView;
            if (zShouldCenterTitle3) {
                int i18 = currentActionBarHeight3 + i11;
                simpleTextView6.layout(animatedCenterTitleX - (simpleTextView6.getMeasuredWidth() / 2), i18, (this.subtitleTextView.getMeasuredWidth() / 2) + animatedCenterTitleX, this.subtitleTextView.getTextHeight() + i18);
            } else {
                int i19 = currentActionBarHeight3 + i11;
                simpleTextView6.layout(i12, i19, simpleTextView6.getMeasuredWidth() + i12, this.subtitleTextView.getTextHeight() + i19);
            }
        }
        SimpleTextView simpleTextView7 = this.additionalSubtitleTextView;
        if (simpleTextView7 != null && simpleTextView7.getVisibility() != 8) {
            int currentActionBarHeight4 = (getCurrentActionBarHeight() / 2) + (((getCurrentActionBarHeight() / 2) - this.additionalSubtitleTextView.getTextHeight()) / 2);
            if (!AndroidUtilities.isTablet()) {
                int i20 = getResources().getConfiguration().orientation;
            }
            int iDp = currentActionBarHeight4 - AndroidUtilities.dp(1.0f);
            boolean zShouldCenterTitle4 = shouldCenterTitle();
            SimpleTextView simpleTextView8 = this.additionalSubtitleTextView;
            if (zShouldCenterTitle4) {
                int i21 = iDp + i11;
                simpleTextView8.layout(animatedCenterTitleX - (simpleTextView8.getMeasuredWidth() / 2), i21, animatedCenterTitleX + (this.additionalSubtitleTextView.getMeasuredWidth() / 2), this.additionalSubtitleTextView.getTextHeight() + i21);
            } else {
                int i22 = iDp + i11;
                simpleTextView8.layout(i12, i22, simpleTextView8.getMeasuredWidth() + i12, this.additionalSubtitleTextView.getTextHeight() + i22);
            }
        }
        BackupImageView backupImageView = this.avatarSearchImageView;
        if (backupImageView != null) {
            backupImageView.layout(AndroidUtilities.dp(64.0f), ((getCurrentActionBarHeight() - this.avatarSearchImageView.getMeasuredHeight()) / 2) + i11, AndroidUtilities.dp(64.0f) + this.avatarSearchImageView.getMeasuredWidth(), i11 + ((getCurrentActionBarHeight() + this.avatarSearchImageView.getMeasuredHeight()) / 2));
        }
        int childCount = getChildCount();
        for (int i23 = 0; i23 < childCount; i23++) {
            View childAt = getChildAt(i23);
            if (childAt.getVisibility() != 8) {
                SimpleTextView[] simpleTextViewArr2 = this.titleTextView;
                if (childAt != simpleTextViewArr2[0] && childAt != simpleTextViewArr2[1] && childAt != this.additionalSubTitleOverlayContainer && childAt != this.subtitleTextView && childAt != this.menu && childAt != this.backButtonImageView && childAt != this.additionalSubtitleTextView && childAt != this.avatarSearchImageView) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                    int measuredWidth2 = childAt.getMeasuredWidth();
                    int measuredHeight = childAt.getMeasuredHeight();
                    int i24 = layoutParams.gravity;
                    if (i24 == -1) {
                        i24 = 51;
                    }
                    int i25 = i24 & 112;
                    int i26 = i24 & 7;
                    if (i26 == 1) {
                        i5 = (((i3 - i) - measuredWidth2) / 2) + layoutParams.leftMargin;
                        i6 = layoutParams.rightMargin;
                    } else {
                        if (i26 == 5) {
                            i5 = i3 - measuredWidth2;
                            i6 = layoutParams.rightMargin;
                        } else {
                            i7 = layoutParams.leftMargin;
                        }
                        if (i25 != 16) {
                            i8 = (((i4 - i2) - measuredHeight) / 2) + layoutParams.topMargin;
                            i9 = layoutParams.bottomMargin;
                        } else {
                            if (i25 != 80) {
                                i8 = (i4 - i2) - measuredHeight;
                                i9 = layoutParams.bottomMargin;
                            } else {
                                i10 = layoutParams.topMargin;
                            }
                            childAt.layout(i7, i10, measuredWidth2 + i7, measuredHeight + i10);
                        }
                        i10 = i8 - i9;
                        childAt.layout(i7, i10, measuredWidth2 + i7, measuredHeight + i10);
                    }
                    i7 = i5 - i6;
                    if (i25 != 16) {
                        i8 = (((i4 - i2) - measuredHeight) / 2) + layoutParams.topMargin;
                        i9 = layoutParams.bottomMargin;
                    } else {
                        if (i25 != 80) {
                            i8 = (i4 - i2) - measuredHeight;
                            i9 = layoutParams.bottomMargin;
                        } else {
                            i10 = layoutParams.topMargin;
                        }
                        childAt.layout(i7, i10, measuredWidth2 + i7, measuredHeight + i10);
                    }
                    i10 = i8 - i9;
                    childAt.layout(i7, i10, measuredWidth2 + i7, measuredHeight + i10);
                }
            }
        }
    }

    public void onMenuButtonPressed() {
        ActionBarMenu actionBarMenu;
        if (isActionModeShowed() || (actionBarMenu = this.menu) == null) {
            return;
        }
        actionBarMenu.onMenuButtonPressed();
    }

    public void onResume() {
        this.resumed = true;
        updateAttachState();
    }

    public void onPause() {
        this.resumed = false;
        updateAttachState();
        ActionBarMenu actionBarMenu = this.menu;
        if (actionBarMenu != null) {
            actionBarMenu.hideAllPopupMenus();
        }
    }

    public void setAllowOverlayTitle(boolean z) {
        this.allowOverlayTitle = z;
    }

    public void setTitleActionRunnable(Runnable runnable) {
        this.titleActionRunnable = runnable;
        this.lastRunnable = runnable;
    }

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
    public void setTitleOverlayText(String str, int i, Runnable runnable) {
        CharSequence charSequence;
        CharSequence charSequence2;
        boolean z;
        CharSequence charSequence3;
        boolean z2;
        SimpleTextView simpleTextView;
        int iIndexOf;
        SpannableString spannableStringValueOf;
        String string;
        if (!this.allowOverlayTitle || this.parentFragment.parentLayout == null) {
            return;
        }
        Object[] objArr = this.overlayTitleToSet;
        objArr[0] = str;
        objArr[1] = Integer.valueOf(i);
        this.overlayTitleToSet[2] = runnable;
        if (this.overlayTitleAnimationInProgress) {
            return;
        }
        CharSequence charSequence4 = this.lastOverlayTitle;
        if (charSequence4 == null && str == null) {
            return;
        }
        if (charSequence4 == null || !charSequence4.equals(str)) {
            this.lastOverlayTitle = str;
            Drawable visibleTitleRightDrawable = null;
            if (this.additionalSubTitleOverlayContainer != null) {
                this.additionalSubTitleOverlayContainer.setText(i == R.string.ConnectingToProxyWithDots ? AndroidUtilities.replaceArrows(LocaleController.getString(R.string.TitleSetupProxy), true, AndroidUtilities.dp(2.6666667f), AndroidUtilities.dp(2.0f)) : null, true);
            }
            if (str != null) {
                string = LocaleController.getString(str, i);
            } else {
                charSequence = this.lastTitle;
            }
            if (str == null) {
                charSequence2 = charSequence;
                charSequence2 = string;
                visibleTitleRightDrawable = getVisibleTitleRightDrawable(this.lastRightDrawable);
            }
            if (str == null || (iIndexOf = TextUtils.indexOf(charSequence2, "...")) < 0) {
                z = false;
                charSequence3 = charSequence2;
            } else {
                spannableStringValueOf = SpannableString.valueOf(charSequence2);
                this.ellipsizeSpanAnimator.wrap(spannableStringValueOf, iIndexOf);
                z = true;
            }
            if (str != null) {
                charSequence3 = spannableStringValueOf;
                z2 = true;
            } else {
                charSequence3 = spannableStringValueOf;
                z2 = false;
            }
            this.titleOverlayShown = z2;
            if ((charSequence3 != null && this.titleTextView[0] == null) || getMeasuredWidth() == 0 || ((simpleTextView = this.titleTextView[0]) != null && simpleTextView.getVisibility() != 0)) {
                createTitleTextView(0);
                if (this.supportsHolidayImage) {
                    this.titleTextView[0].invalidate();
                    invalidate();
                }
                this.titleTextView[0].setText(charSequence3);
                this.titleTextView[0].setDrawablePadding(AndroidUtilities.dp(4.0f));
                this.titleTextView[0].setRightDrawable(visibleTitleRightDrawable);
                this.titleTextView[0].setRightDrawableOnClick(this.rightDrawableOnClickListener);
                if (visibleTitleRightDrawable instanceof AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) {
                    ((AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) visibleTitleRightDrawable).setParentView(this.titleTextView[0]);
                }
                EllipsizeSpanAnimator ellipsizeSpanAnimator = this.ellipsizeSpanAnimator;
                if (z) {
                    ellipsizeSpanAnimator.addView(this.titleTextView[0]);
                } else {
                    ellipsizeSpanAnimator.removeView(this.titleTextView[0]);
                }
            } else {
                SimpleTextView simpleTextView2 = this.titleTextView[0];
                if (simpleTextView2 != null) {
                    simpleTextView2.animate().cancel();
                    SimpleTextView simpleTextView3 = this.titleTextView[1];
                    if (simpleTextView3 != null) {
                        simpleTextView3.animate().cancel();
                    }
                    if (this.titleTextView[1] == null) {
                        createTitleTextView(1);
                    }
                    this.titleTextView[1].setText(charSequence3);
                    this.titleTextView[1].setDrawablePadding(AndroidUtilities.dp(4.0f));
                    this.titleTextView[1].setRightDrawable(visibleTitleRightDrawable);
                    this.titleTextView[1].setRightDrawableOnClick(this.rightDrawableOnClickListener);
                    if (visibleTitleRightDrawable instanceof AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) {
                        ((AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) visibleTitleRightDrawable).setParentView(this.titleTextView[1]);
                    }
                    if (z) {
                        this.ellipsizeSpanAnimator.addView(this.titleTextView[1]);
                    }
                    this.overlayTitleAnimationInProgress = true;
                    SimpleTextView[] simpleTextViewArr = this.titleTextView;
                    SimpleTextView simpleTextView4 = simpleTextViewArr[1];
                    simpleTextViewArr[1] = simpleTextViewArr[0];
                    simpleTextViewArr[0] = simpleTextView4;
                    simpleTextView4.setAlpha(0.0f);
                    this.titleTextView[0].setTranslationY(-AndroidUtilities.dp(20.0f));
                    this.titleTextView[0].animate().alpha(this.adaptiveBackgroundHideTitle ? 1.0f - this.onTopAnimated : 1.0f).translationY(0.0f).setDuration(220L).start();
                    ViewPropertyAnimator viewPropertyAnimatorAlpha = this.titleTextView[1].animate().alpha(0.0f);
                    if (this.subtitleTextView == null) {
                        viewPropertyAnimatorAlpha.translationY(AndroidUtilities.dp(20.0f));
                    } else {
                        viewPropertyAnimatorAlpha.scaleY(0.7f).scaleX(0.7f);
                    }
                    requestLayout();
                    this.centerScale = true;
                    viewPropertyAnimatorAlpha.setDuration(220L).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.6
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (ActionBar.this.titleTextView[1] != null && ActionBar.this.titleTextView[1].getParent() != null) {
                                ((ViewGroup) ActionBar.this.titleTextView[1].getParent()).removeView(ActionBar.this.titleTextView[1]);
                            }
                            ActionBar actionBar = ActionBar.this;
                            actionBar.ellipsizeSpanAnimator.removeView(actionBar.titleTextView[1]);
                            ActionBar.this.titleTextView[1] = null;
                            ActionBar actionBar2 = ActionBar.this;
                            actionBar2.overlayTitleAnimationInProgress = false;
                            actionBar2.setTitleOverlayText((String) actionBar2.overlayTitleToSet[0], ((Integer) ActionBar.this.overlayTitleToSet[1]).intValue(), (Runnable) ActionBar.this.overlayTitleToSet[2]);
                        }
                    }).start();
                }
            }
            if (runnable == null) {
                runnable = this.lastRunnable;
            }
            this.titleActionRunnable = runnable;
        }
    }

    public boolean isSearchFieldVisible() {
        return this.isSearchFieldVisible;
    }

    public void setOccupyStatusBar(boolean z) {
        this.occupyStatusBar = z;
        ActionBarMenu actionBarMenu = this.actionMode;
        if (actionBarMenu != null) {
            actionBarMenu.setPadding(0, z ? AndroidUtilities.statusBarHeight : 0, 0, 0);
        }
    }

    public boolean getOccupyStatusBar() {
        return this.occupyStatusBar;
    }

    public void setItemsBackgroundColor(int i, boolean z) {
        ImageView imageView;
        if (z) {
            this.itemsActionModeBackgroundColor = i;
            if (this.actionModeVisible && (imageView = this.backButtonImageView) != null) {
                imageView.setBackgroundDrawable(Theme.createSelectorDrawable(i));
            }
            ActionBarMenu actionBarMenu = this.actionMode;
            if (actionBarMenu != null) {
                actionBarMenu.updateItemsBackgroundColor();
                return;
            }
            return;
        }
        this.itemsBackgroundColor = i;
        ImageView imageView2 = this.backButtonImageView;
        if (imageView2 != null) {
            imageView2.setBackgroundDrawable(Theme.createSelectorDrawable(i));
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        if (actionBarMenu2 != null) {
            actionBarMenu2.updateItemsBackgroundColor();
        }
    }

    public void setItemsColor(int i, boolean z) {
        if (z) {
            this.itemsActionModeColor = i;
            ActionBarMenu actionBarMenu = this.actionMode;
            if (actionBarMenu != null) {
                actionBarMenu.updateItemsColor();
            }
            ImageView imageView = this.backButtonImageView;
            if (imageView != null) {
                Drawable drawable = imageView.getDrawable();
                if (drawable instanceof BackDrawable) {
                    ((BackDrawable) drawable).setRotatedColor(i);
                    return;
                } else {
                    if ((drawable instanceof BitmapDrawable) || (drawable instanceof VectorDrawable)) {
                        this.backButtonImageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
                        return;
                    }
                    return;
                }
            }
            return;
        }
        this.itemsColor = i;
        ImageView imageView2 = this.backButtonImageView;
        if (imageView2 != null && i != 0) {
            Drawable drawable2 = imageView2.getDrawable();
            if (drawable2 instanceof BackDrawable) {
                ((BackDrawable) drawable2).setColor(i);
            } else if (drawable2 instanceof MenuDrawable) {
                ((MenuDrawable) drawable2).setIconColor(i);
            } else if ((drawable2 instanceof BitmapDrawable) || (drawable2 instanceof VectorDrawable)) {
                this.backButtonImageView.setColorFilter(new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN));
            }
        }
        ActionBarMenu actionBarMenu2 = this.menu;
        if (actionBarMenu2 != null) {
            actionBarMenu2.updateItemsColor();
        }
    }

    public void setCastShadows(boolean z) {
        if (this.castShadows != z && (getParent() instanceof View)) {
            ((View) getParent()).invalidate();
            invalidate();
        }
        this.castShadows = z;
    }

    public void setShadowAlpha(int i) {
        if (this.shadowAlpha == i) {
            return;
        }
        if (getParent() instanceof View) {
            ((View) getParent()).invalidate();
            invalidate();
        }
        this.shadowAlpha = i;
    }

    public int getShadowAlpha() {
        return this.shadowAlpha;
    }

    public boolean getCastShadows() {
        return this.castShadows;
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.forceSkipTouches) {
            return false;
        }
        return super.onTouchEvent(motionEvent) || this.interceptTouches;
    }

    public static int getCurrentActionBarHeight() {
        Point point = AndroidUtilities.displaySize;
        if (point.x > point.y) {
            return AndroidUtilities.dp(48.0f);
        }
        return AndroidUtilities.dp(56.0f);
    }

    public void setTitleAnimated(CharSequence charSequence, boolean z, long j) {
        setTitleAnimated(charSequence, z, j, null);
    }

    public void setTitleAnimated(CharSequence charSequence, final boolean z, long j, Interpolator interpolator) {
        if (this.titleTextView[0] == null || charSequence == null) {
            setTitle(charSequence);
            return;
        }
        final boolean z2 = this.overlayTitleAnimation && !TextUtils.isEmpty(this.subtitle);
        if (z2) {
            if (this.subtitleTextView.getVisibility() != 0) {
                this.subtitleTextView.setVisibility(0);
                this.subtitleTextView.setAlpha(0.0f);
            }
            this.subtitleTextView.animate().alpha(z ? 0.0f : 1.0f).setDuration(220L).start();
        }
        SimpleTextView simpleTextView = this.titleTextView[1];
        if (simpleTextView != null) {
            if (simpleTextView.getParent() != null) {
                ((ViewGroup) this.titleTextView[1].getParent()).removeView(this.titleTextView[1]);
            }
            this.titleTextView[1] = null;
        }
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        simpleTextViewArr[1] = simpleTextViewArr[0];
        simpleTextViewArr[0] = null;
        setTitle(charSequence);
        this.fromBottom = z;
        this.titleTextView[0].setAlpha(0.0f);
        if (!z2) {
            SimpleTextView simpleTextView2 = this.titleTextView[0];
            int iDp = AndroidUtilities.dp(20.0f);
            if (!z) {
                iDp = -iDp;
            }
            simpleTextView2.setTranslationY(iDp);
        }
        ViewPropertyAnimator duration = this.titleTextView[0].animate().alpha(1.0f).translationY(0.0f).setDuration(j);
        if (interpolator != null) {
            duration.setInterpolator(interpolator);
        }
        duration.start();
        this.titleAnimationRunning = true;
        ViewPropertyAnimator viewPropertyAnimatorAlpha = this.titleTextView[1].animate().alpha(0.0f);
        if (!z2) {
            int iDp2 = AndroidUtilities.dp(20.0f);
            if (z) {
                iDp2 = -iDp2;
            }
            viewPropertyAnimatorAlpha.translationY(iDp2);
        }
        if (interpolator != null) {
            viewPropertyAnimatorAlpha.setInterpolator(interpolator);
        }
        viewPropertyAnimatorAlpha.setDuration(j).setListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.7
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (ActionBar.this.titleTextView[1] != null && ActionBar.this.titleTextView[1].getParent() != null) {
                    ((ViewGroup) ActionBar.this.titleTextView[1].getParent()).removeView(ActionBar.this.titleTextView[1]);
                }
                ActionBar.this.titleTextView[1] = null;
                ActionBar.this.titleAnimationRunning = false;
                if (z2 && z) {
                    ActionBar.this.subtitleTextView.setVisibility(8);
                }
                ActionBar.this.requestLayout();
            }
        }).start();
        requestLayout();
    }

    public void setTitleAnimatedX(CharSequence charSequence, Drawable drawable, boolean z, int i) {
        SimpleTextView[] simpleTextViewArr = this.titleTextView;
        if (simpleTextViewArr[0] == null || charSequence == null) {
            setTitle(charSequence, drawable);
            return;
        }
        SimpleTextView simpleTextView = simpleTextViewArr[1];
        if (simpleTextView != null) {
            if (simpleTextView.getParent() != null) {
                ((ViewGroup) this.titleTextView[1].getParent()).removeView(this.titleTextView[1]);
            }
            this.titleTextView[1] = null;
        }
        AnimatorSet animatorSet = this.titleAnimator;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.titleAnimator = null;
        }
        SimpleTextView[] simpleTextViewArr2 = this.titleTextView;
        simpleTextViewArr2[1] = simpleTextViewArr2[0];
        simpleTextViewArr2[0] = null;
        setTitle(charSequence, drawable);
        this.titleAnimationRunning = true;
        float fDp = AndroidUtilities.dp(10.0f) * (z ? -1 : 1);
        this.titleTextView[1].setTranslationX(0.0f);
        this.titleTextView[1].setTranslationY(0.0f);
        this.titleTextView[0].setTranslationX(-fDp);
        this.titleTextView[0].setTranslationY(0.0f);
        this.titleTextView[0].setAlpha(0.0f);
        this.titleTextView[1].setAlpha(1.0f);
        this.titleTextView[0].setVisibility(0);
        this.titleTextView[1].setVisibility(0);
        ArrayList arrayList = new ArrayList();
        Property property = View.ALPHA;
        arrayList.add(ObjectAnimator.ofFloat(this.titleTextView[1], (Property<SimpleTextView, Float>) property, 0.0f));
        arrayList.add(ObjectAnimator.ofFloat(this.titleTextView[0], (Property<SimpleTextView, Float>) property, 1.0f));
        SimpleTextView simpleTextView2 = this.titleTextView[1];
        float[] fArr = {fDp};
        Property property2 = View.TRANSLATION_X;
        arrayList.add(ObjectAnimator.ofFloat(simpleTextView2, (Property<SimpleTextView, Float>) property2, fArr));
        arrayList.add(ObjectAnimator.ofFloat(this.titleTextView[0], (Property<SimpleTextView, Float>) property2, 0.0f));
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.titleAnimator = animatorSet2;
        animatorSet2.playTogether(arrayList);
        this.titleAnimator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.8
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (ActionBar.this.titleTextView[1] != null && ActionBar.this.titleTextView[1].getParent() != null) {
                    ((ViewGroup) ActionBar.this.titleTextView[1].getParent()).removeView(ActionBar.this.titleTextView[1]);
                }
                ActionBar.this.titleTextView[1] = null;
                ActionBar.this.titleAnimationRunning = false;
                ActionBar.this.requestLayout();
            }
        });
        this.titleAnimator.setDuration(i);
        this.titleAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.titleAnimator.start();
        requestLayout();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.attached = true;
        updateAttachState();
        if (this.actionModeVisible) {
            int i = this.actionModeColor;
            if (i == 0) {
                i = this.actionBarColor;
            }
            if (i == 0 || this.glassMode) {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
            } else if (ColorUtils.calculateLuminance(i) < 0.699999988079071d) {
                AndroidUtilities.setLightStatusBar((Activity) getContext(), false);
            } else {
                AndroidUtilities.setLightStatusBar((Activity) getContext(), true);
            }
        }
        Drawable drawable = this.lastRightDrawable;
        if (drawable instanceof AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) {
            SimpleTextView simpleTextView = this.titleTextView[0];
            AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable swapAnimatedEmojiDrawable = (AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) drawable;
            if (simpleTextView == null || simpleTextView.getRightDrawable() != this.lastRightDrawable) {
                simpleTextView = null;
            }
            swapAnimatedEmojiDrawable.setParentView(simpleTextView);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.attached = false;
        ValueAnimator valueAnimator = this.centerTitleLayoutAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.centerTitleLayoutAnimator = null;
        }
        updateAttachState();
        if (this.actionModeVisible) {
            if (this.actionBarColor == 0 || this.actionModeColor == 0 || this.glassMode) {
                NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.needCheckSystemBarColors, new Object[0]);
            } else {
                AndroidUtilities.setLightStatusBar(((Activity) getContext()).getWindow(), ColorUtils.calculateLuminance(this.actionBarColor) >= 0.699999988079071d);
            }
        }
        Drawable drawable = this.lastRightDrawable;
        if (drawable instanceof AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) {
            ((AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable) drawable).setParentView(null);
        }
    }

    private void updateAttachState() {
        boolean z = this.attached && this.resumed;
        if (this.attachState != z) {
            this.attachState = z;
            EllipsizeSpanAnimator ellipsizeSpanAnimator = this.ellipsizeSpanAnimator;
            if (z) {
                ellipsizeSpanAnimator.onAttachedToWindow();
            } else {
                ellipsizeSpanAnimator.onDetachedFromWindow();
            }
        }
    }

    public ActionBarMenu getActionMode() {
        return this.actionMode;
    }

    public void setOverlayTitleAnimation(boolean z) {
        this.overlayTitleAnimation = z;
    }

    public void beginDelayedTransition() {
        if (LocaleController.isRTL) {
            return;
        }
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.setOrdering(0);
        transitionSet.addTransition(new Fade());
        transitionSet.addTransition(new ChangeBounds() { // from class: org.telegram.ui.ActionBar.ActionBar.9
            @Override // android.transition.ChangeBounds, android.transition.Transition
            public void captureStartValues(TransitionValues transitionValues) {
                super.captureStartValues(transitionValues);
                View view = transitionValues.view;
                if (view instanceof SimpleTextView) {
                    transitionValues.values.put("text_size", Float.valueOf(((SimpleTextView) view).getTextPaint().getTextSize()));
                }
            }

            @Override // android.transition.ChangeBounds, android.transition.Transition
            public void captureEndValues(TransitionValues transitionValues) {
                super.captureEndValues(transitionValues);
                View view = transitionValues.view;
                if (view instanceof SimpleTextView) {
                    transitionValues.values.put("text_size", Float.valueOf(((SimpleTextView) view).getTextPaint().getTextSize()));
                }
            }

            @Override // android.transition.ChangeBounds, android.transition.Transition
            public Animator createAnimator(ViewGroup viewGroup, final TransitionValues transitionValues, TransitionValues transitionValues2) {
                if (transitionValues != null && (transitionValues.view instanceof SimpleTextView)) {
                    AnimatorSet animatorSet = new AnimatorSet();
                    if (transitionValues2 != null) {
                        Animator animatorCreateAnimator = super.createAnimator(viewGroup, transitionValues, transitionValues2);
                        float fFloatValue = ((Float) transitionValues.values.get("text_size")).floatValue() / ((Float) transitionValues2.values.get("text_size")).floatValue();
                        transitionValues.view.setScaleX(fFloatValue);
                        transitionValues.view.setScaleY(fFloatValue);
                        if (animatorCreateAnimator != null) {
                            animatorSet.playTogether(animatorCreateAnimator);
                        }
                    }
                    animatorSet.playTogether(ObjectAnimator.ofFloat(transitionValues.view, (Property<View, Float>) View.SCALE_X, 1.0f));
                    animatorSet.playTogether(ObjectAnimator.ofFloat(transitionValues.view, (Property<View, Float>) View.SCALE_Y, 1.0f));
                    animatorSet.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.9.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationStart(Animator animator) {
                            super.onAnimationStart(animator);
                            transitionValues.view.setLayerType(2, null);
                        }

                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            super.onAnimationEnd(animator);
                            transitionValues.view.setLayerType(0, null);
                        }
                    });
                    return animatorSet;
                }
                return super.createAnimator(viewGroup, transitionValues, transitionValues2);
            }
        });
        this.centerScale = false;
        transitionSet.setDuration(220L);
        transitionSet.setInterpolator((TimeInterpolator) CubicBezierInterpolator.DEFAULT);
        TransitionManager.beginDelayedTransition(this, transitionSet);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v3, types: [android.view.ViewTreeObserver$OnPreDrawListener, org.telegram.ui.ActionBar.ActionBar$10] */
    public void refreshTitlePosition(boolean z) {
        float translationX;
        float top;
        float translationY;
        int titleGravity = getTitleGravity();
        int subtitleGravity = getSubtitleGravity();
        int i = 0;
        if (!z) {
            while (i < 2) {
                SimpleTextView simpleTextView = this.titleTextView[i];
                if (simpleTextView != null) {
                    simpleTextView.setGravity(titleGravity);
                }
                i++;
            }
            SimpleTextView simpleTextView2 = this.subtitleTextView;
            if (simpleTextView2 != null) {
                simpleTextView2.setGravity(subtitleGravity);
            }
            SimpleTextView simpleTextView3 = this.additionalSubtitleTextView;
            if (simpleTextView3 != null) {
                simpleTextView3.setGravity(subtitleGravity);
            }
            requestLayout();
            return;
        }
        final ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        final ArrayList arrayList3 = new ArrayList();
        for (int i2 = 0; i2 < 2; i2++) {
            SimpleTextView simpleTextView4 = this.titleTextView[i2];
            if (simpleTextView4 != null && simpleTextView4.getVisibility() == 0) {
                arrayList.add(this.titleTextView[i2]);
            }
        }
        SimpleTextView simpleTextView5 = this.subtitleTextView;
        if (simpleTextView5 != null && simpleTextView5.getVisibility() == 0) {
            arrayList.add(this.subtitleTextView);
        }
        SimpleTextView simpleTextView6 = this.additionalSubtitleTextView;
        if (simpleTextView6 != null && simpleTextView6.getVisibility() == 0) {
            arrayList.add(this.additionalSubtitleTextView);
        }
        int size = arrayList.size();
        int i3 = 0;
        while (i3 < size) {
            Object obj = arrayList.get(i3);
            i3++;
            View view = (View) obj;
            view.animate().cancel();
            if (view instanceof SimpleTextView) {
                SimpleTextView simpleTextView7 = (SimpleTextView) view;
                translationX = simpleTextView7.getTextStartX() + view.getTranslationX();
                top = simpleTextView7.getTextStartY();
                translationY = view.getTranslationY();
            } else {
                translationX = view.getTranslationX() + view.getLeft();
                top = view.getTop();
                translationY = view.getTranslationY();
            }
            float f = top + translationY;
            arrayList2.add(Float.valueOf(translationX));
            arrayList3.add(Float.valueOf(f));
        }
        while (i < 2) {
            SimpleTextView simpleTextView8 = this.titleTextView[i];
            if (simpleTextView8 != null) {
                simpleTextView8.setGravity(titleGravity);
            }
            i++;
        }
        SimpleTextView simpleTextView9 = this.subtitleTextView;
        if (simpleTextView9 != null) {
            simpleTextView9.setGravity(subtitleGravity);
        }
        SimpleTextView simpleTextView10 = this.additionalSubtitleTextView;
        if (simpleTextView10 != null) {
            simpleTextView10.setGravity(subtitleGravity);
        }
        requestLayout();
        final ?? r0 = new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.ActionBar.ActionBar.10
            @Override // android.view.ViewTreeObserver.OnPreDrawListener
            public boolean onPreDraw() {
                float left;
                int top2;
                ActionBar.this.getViewTreeObserver().removeOnPreDrawListener(this);
                for (int i4 = 0; i4 < arrayList.size(); i4++) {
                    View view2 = (View) arrayList.get(i4);
                    if (view2 instanceof SimpleTextView) {
                        SimpleTextView simpleTextView11 = (SimpleTextView) view2;
                        left = simpleTextView11.getTextStartX();
                        top2 = simpleTextView11.getTextStartY();
                    } else {
                        left = view2.getLeft();
                        top2 = view2.getTop();
                    }
                    float f2 = top2;
                    float fFloatValue = ((Float) arrayList2.get(i4)).floatValue() - left;
                    float fFloatValue2 = ((Float) arrayList3.get(i4)).floatValue() - f2;
                    ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(view2, (Property<View, Float>) View.TRANSLATION_X, fFloatValue, 0.0f);
                    ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(view2, (Property<View, Float>) View.TRANSLATION_Y, fFloatValue2, 0.0f);
                    AnimatorSet animatorSet = new AnimatorSet();
                    animatorSet.playTogether(objectAnimatorOfFloat, objectAnimatorOfFloat2);
                    animatorSet.setDuration(300L);
                    animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    animatorSet.start();
                }
                return true;
            }
        };
        getViewTreeObserver().addOnPreDrawListener(r0);
        addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() { // from class: org.telegram.ui.ActionBar.ActionBar.11
            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewAttachedToWindow(View view2) {
            }

            @Override // android.view.View.OnAttachStateChangeListener
            public void onViewDetachedFromWindow(View view2) {
                ActionBar.this.getViewTreeObserver().removeOnPreDrawListener(r0);
                ActionBar.this.removeOnAttachStateChangeListener(this);
            }
        });
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    public void setDrawBlurBackground(SizeNotifierFrameLayout sizeNotifierFrameLayout) {
        this.blurredBackground = true;
        this.contentView = sizeNotifierFrameLayout;
        sizeNotifierFrameLayout.blurBehindViews.add(this);
        setBackground(null);
    }

    public void setSkipDrawChild(boolean z) {
        if (this.doNotDrawChild != z) {
            this.doNotDrawChild = z;
            invalidate();
        }
    }

    public void setSearchFactor(float f) {
        if (this.searchFactor != f) {
            this.searchFactor = f;
            invalidate();
        }
    }

    public void checkAvatarContainerWidth(boolean z) {
        ChatAvatarContainer chatAvatarContainer = this.chatAvatarContainer;
        if (chatAvatarContainer == null) {
            return;
        }
        boolean zHasVisibleAvatar = chatAvatarContainer.hasVisibleAvatar();
        int visualWidth = this.chatAvatarContainer.getVisualWidth();
        if (zHasVisibleAvatar) {
            visualWidth = Math.max(visualWidth, AndroidUtilities.dp(192.0f));
        }
        int iMin = Math.min(getMeasuredWidth() - AndroidUtilities.dp(116.0f), visualWidth);
        FactorAnimator factorAnimator = this.animatorAvatarContainerWidth;
        if (z) {
            float f = iMin;
            if (factorAnimator.getToFactor() != f) {
                this.animatorAvatarContainerWidth.animateTo(f);
            }
        } else {
            factorAnimator.forceFactor(iMin);
        }
        this.animatorAvatarContainerHasAvatar.setValue(zHasVisibleAvatar, z);
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        invalidate();
    }

    public void setForcedMenuWidth(int i) {
        this.hasForcedMenuWidth = true;
        if (this.forcedMenuWidth != i) {
            this.forcedMenuWidth = i;
            invalidate();
        }
    }

    public void checkMenuItemsWidth() {
        ActionBarMenu actionBarMenu = this.menu;
        int iMax = Math.max(0, actionBarMenu != null ? (actionBarMenu.getItemsWidth() - AndroidUtilities.dp(1.0f)) - AndroidUtilities.dp(1.0f) : 0);
        ActionBarMenu actionBarMenu2 = this.actionMode;
        int iMax2 = Math.max(0, actionBarMenu2 != null ? (actionBarMenu2.getItemsWidth() - AndroidUtilities.dp(1.0f)) - AndroidUtilities.dp(1.0f) : 0);
        AndroidUtilities.dp(46.0f);
        if (this.actionModeVisible) {
            iMax = iMax2;
        }
        this.animatorHasMenuItems.setValue(iMax > 0, this.isAnimationsAllowed);
        float f = iMax;
        if (this.animatorMenuItemsWidth.getToFactor() != f) {
            boolean z = this.isAnimationsAllowed;
            FactorAnimator factorAnimator = this.animatorMenuItemsWidth;
            if (z) {
                factorAnimator.animateTo(f);
            } else {
                factorAnimator.forceFactor(f);
            }
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        float f;
        float f2;
        int floatValue;
        int iDp = AndroidUtilities.dp(6.0f);
        int iDp2 = AndroidUtilities.dp(46.0f);
        float actionModeFactor = getActionModeFactor();
        int factor = this.hasForcedMenuWidth ? this.forcedMenuWidth : (int) this.animatorMenuItemsWidth.getFactor();
        ImageView imageView = this.backButtonImageView;
        boolean z = imageView != null && imageView.getVisibility() == 0;
        int height = (getHeight() - ((getCurrentActionBarHeight() + iDp2) / 2)) - iDp;
        int i = iDp * 2;
        int i2 = height + iDp2 + i;
        if (this.glassOnlyBack) {
            f = 0.0f;
        } else {
            f = this.drawGlassMiddlePill ? 1.0f : this.searchFactor;
        }
        if (this.glassDrawable != null && f > 0.0f) {
            if (this.hasForcedMenuWidth) {
                floatValue = factor > 0 ? iDp : 0;
                f2 = 1.0f;
            } else {
                f2 = 1.0f;
                floatValue = (int) (iDp * this.animatorHasMenuItems.getFloatValue());
            }
            int i3 = floatValue + factor;
            int i4 = iDp + iDp2;
            int iLerp = AndroidUtilities.lerp(i3, Math.max(i3, i4), this.chatAvatarContainer == null ? 0.0f : f2 - this.animatorAvatarContainerHasAvatar.getFloatValue());
            int iLerp2 = AndroidUtilities.lerp(z ? i4 : 0, i4, this.chatAvatarContainer != null ? f2 - this.animatorAvatarContainerHasAvatar.getFloatValue() : 0.0f);
            int width = getWidth() - iLerp;
            int i5 = width - iLerp2;
            if (this.chatAvatarContainer != null) {
                int iLerp3 = AndroidUtilities.lerp(Math.min(i5, ((int) this.animatorAvatarContainerWidth.getFactor()) + i), i5, Math.max(this.searchFactor, actionModeFactor));
                iLerp2 = ((width + iLerp2) - iLerp3) / 2;
                width = iLerp2 + iLerp3;
                ChatAvatarContainer chatAvatarContainer = this.chatAvatarContainer;
                chatAvatarContainer.setTranslationX(((iLerp2 - ((ViewGroup.MarginLayoutParams) chatAvatarContainer.getLayoutParams()).leftMargin) - this.chatAvatarContainer.getLeftPadding()) + iDp + AndroidUtilities.dp(3.0f));
            }
            this.glassDrawable.setBounds(iLerp2, height, width, i2);
            this.glassDrawable.setAlpha((int) (f * 255.0f));
            this.glassDrawable.draw(canvas);
        } else {
            f2 = 1.0f;
            ChatAvatarContainer chatAvatarContainer2 = this.chatAvatarContainer;
            if (chatAvatarContainer2 != null) {
                chatAvatarContainer2.setTranslationX(0.0f);
            }
        }
        BlurredBackgroundDrawable blurredBackgroundDrawable = this.glassDrawableBack;
        if (blurredBackgroundDrawable != null && z) {
            blurredBackgroundDrawable.setBounds(0, height, iDp2 + i, i2);
            this.glassDrawableBack.draw(canvas);
        }
        BlurredBackgroundDrawable blurredBackgroundDrawable2 = this.glassDrawableMenu;
        if (blurredBackgroundDrawable2 != null && factor > 0 && !this.glassOnlyBack) {
            blurredBackgroundDrawable2.setBounds((getWidth() - Math.max(iDp2, factor)) - i, height, getWidth(), i2);
            this.glassDrawableMenu.setAlpha(this.hasForcedMenuWidth ? 255 : (int) (this.animatorHasMenuItems.getFloatValue() * 255.0f));
            this.glassDrawableMenu.draw(canvas);
        }
        if (this.blurredBackground && this.actionBarColor != 0) {
            this.rectTmp.set(0, 0, getMeasuredWidth(), getMeasuredHeight());
            this.blurScrimPaint.setColor(this.actionBarColor);
            boolean z2 = this.adaptiveBackground;
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
            if (z2) {
                sizeNotifierFrameLayout.drawBlurRect(canvas, getY(), this.rectTmp, this.blurScrimPaint, true, f2 - this.onTopAnimated);
            } else {
                sizeNotifierFrameLayout.drawBlurRect(canvas, getY(), this.rectTmp, this.blurScrimPaint, true);
            }
        }
        this.isAnimationsAllowed = true;
        if (this.doNotDrawChild) {
            return;
        }
        super.dispatchDraw(canvas);
    }

    public boolean drawHolidayEffect(Canvas canvas) {
        BaseFragment baseFragment = this.parentFragment;
        if ((baseFragment == null || baseFragment.getParentLayout() == null || !this.parentFragment.getParentLayout().isActionBarInCrossfade()) && this.supportsHolidayImage && !this.titleOverlayShown && !LocaleController.isRTL && Theme.canStartHolidayAnimation()) {
            boolean z = this.fireworks;
            if (!z && this.snowflakesEffect == null) {
                this.fireworksEffect = null;
                SnowflakesEffect snowflakesEffect = new SnowflakesEffect(0);
                this.snowflakesEffect = snowflakesEffect;
                snowflakesEffect.occupyStatusBar = this.occupyStatusBar;
            } else if (z && this.snowflakesEffect != null) {
                this.snowflakesEffect = null;
                this.fireworksEffect = new FireworksEffect();
            }
            if (this.snowflakesEffect != null) {
                if (!LiteMode.isEnabled(32)) {
                    return false;
                }
                this.snowflakesEffect.onDraw(this, canvas);
                return true;
            }
            FireworksEffect fireworksEffect = this.fireworksEffect;
            if (fireworksEffect != null) {
                fireworksEffect.onDraw(this, canvas);
                return true;
            }
        }
        return false;
    }

    public void setForceSkipTouches(boolean z) {
        this.forceSkipTouches = z;
    }

    public void setDrawBackButton(boolean z) {
        this.drawBackButton = z;
        ImageView imageView = this.backButtonImageView;
        if (imageView != null) {
            imageView.invalidate();
        }
    }

    public void setUseContainerForTitles() {
        this.useContainerForTitles = true;
        if (this.titlesContainer == null) {
            FrameLayout frameLayout = new FrameLayout(getContext()) { // from class: org.telegram.ui.ActionBar.ActionBar.12
                @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
                public void onLayout(boolean z, int i, int i2, int i3, int i4) {
                }

                @Override // android.widget.FrameLayout, android.view.View
                public void onMeasure(int i, int i2) {
                    setMeasuredDimension(View.MeasureSpec.getSize(i), View.MeasureSpec.getSize(i2));
                }
            };
            this.titlesContainer = frameLayout;
            addView(frameLayout);
        }
    }

    public FrameLayout getTitlesContainer() {
        return this.titlesContainer;
    }

    @Override // org.telegram.ui.ActionBar.Theme.Colorable
    public void updateColors() {
        adaptive_updateColor();
        ActionBarAnimatedSubtitleOverlayContainer actionBarAnimatedSubtitleOverlayContainer = this.additionalSubTitleOverlayContainer;
        if (actionBarAnimatedSubtitleOverlayContainer != null) {
            actionBarAnimatedSubtitleOverlayContainer.updateColors();
        }
    }

    public FrameLayout createAdditionalSubTitleOverlayContainer() {
        if (this.additionalSubTitleOverlayContainer == null) {
            ActionBarAnimatedSubtitleOverlayContainer actionBarAnimatedSubtitleOverlayContainer = new ActionBarAnimatedSubtitleOverlayContainer(getContext(), this.resourcesProvider, this.ellipsizeSpanAnimator) { // from class: org.telegram.ui.ActionBar.ActionBar.13
                @Override // org.telegram.ui.ActionBar.ActionBarAnimatedSubtitleOverlayContainer, me.vkryl.android.animator.ReplaceAnimator.Callback
                public void onItemChanged(ReplaceAnimator<?> replaceAnimator) {
                    super.onItemChanged(replaceAnimator);
                    float totalVisibility = getTotalVisibility();
                    if (ActionBar.this.titlesContainer != null) {
                        ActionBar.this.titlesContainer.setTranslationY(totalVisibility * AndroidUtilities.dp(-11.0f));
                    }
                }
            };
            this.additionalSubTitleOverlayContainer = actionBarAnimatedSubtitleOverlayContainer;
            actionBarAnimatedSubtitleOverlayContainer.setClipChildren(false);
            addView(this.additionalSubTitleOverlayContainer);
        }
        return this.additionalSubTitleOverlayContainer;
    }

    public FrameLayout getAdditionalSubTitleOverlayContainer() {
        return this.additionalSubTitleOverlayContainer;
    }

    public void setAdaptiveBackground(RecyclerView recyclerView) {
        setAdaptiveBackground(recyclerView, false, Theme.key_windowBackgroundGray, Theme.key_actionBarDefault);
    }

    public void setAdaptiveBackground(RecyclerView recyclerView, boolean z) {
        setAdaptiveBackground(recyclerView, z, Theme.key_windowBackgroundGray, Theme.key_actionBarDefault);
    }

    public void setAdaptiveBackground(final RecyclerView recyclerView, boolean z, int i, int i2) {
        this.adaptive_topColorKey = i;
        this.adaptive_lowerColorKey = i2;
        final Runnable runnable = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBar$$ExternalSyntheticLambda5
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setAdaptiveBackground$7(recyclerView);
            }
        };
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: org.telegram.ui.ActionBar.ActionBar.15
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView2, int i3, int i4) {
                runnable.run();
            }
        });
        this.adaptiveBackgroundHideTitle = z;
        if (this.adaptiveBackground) {
            runnable.run();
            return;
        }
        this.adaptiveBackground = true;
        boolean zCanScrollVertically = recyclerView.canScrollVertically(-1);
        this.onTop = !zCanScrollVertically;
        this.onTopAnimated = !zCanScrollVertically ? 1.0f : 0.0f;
        adaptive_updateColor();
    }

    public /* synthetic */ void lambda$setAdaptiveBackground$7(RecyclerView recyclerView) {
        boolean zCanScrollVertically = recyclerView.canScrollVertically(-1);
        final boolean z = !zCanScrollVertically;
        if (this.onTop == z) {
            return;
        }
        ValueAnimator valueAnimator = this.adaptive_animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        float f = this.onTopAnimated;
        this.onTop = z;
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(f, !zCanScrollVertically ? 1.0f : 0.0f);
        this.adaptive_animator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.ActionBar$$ExternalSyntheticLambda9
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.lambda$setAdaptiveBackground$6(valueAnimator2);
            }
        });
        this.adaptive_animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.14
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ActionBar.this.onTopAnimated = z ? 1.0f : 0.0f;
                ActionBar.this.adaptive_updateColor();
            }
        });
        this.adaptive_animator.setDuration(320L);
        this.adaptive_animator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.adaptive_animator.start();
    }

    public /* synthetic */ void lambda$setAdaptiveBackground$6(ValueAnimator valueAnimator) {
        this.onTopAnimated = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        adaptive_updateColor();
    }

    public void setAdaptiveBackground(SectionsScrollView sectionsScrollView) {
        setAdaptiveBackground(sectionsScrollView, Theme.key_windowBackgroundGray, Theme.key_actionBarDefault);
    }

    public void setAdaptiveBackground(final SectionsScrollView sectionsScrollView, int i, int i2) {
        this.adaptive_topColorKey = i;
        this.adaptive_lowerColorKey = i2;
        adaptive_updateColor();
        Runnable runnable = new Runnable() { // from class: org.telegram.ui.ActionBar.ActionBar$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setAdaptiveBackground$9(sectionsScrollView);
            }
        };
        sectionsScrollView.onScroll(runnable);
        if (this.adaptiveBackground) {
            runnable.run();
            return;
        }
        this.adaptiveBackground = true;
        boolean zCanScrollVertically = sectionsScrollView.canScrollVertically(-1);
        this.onTop = !zCanScrollVertically;
        this.onTopAnimated = !zCanScrollVertically ? 1.0f : 0.0f;
        adaptive_updateColor();
    }

    public /* synthetic */ void lambda$setAdaptiveBackground$9(SectionsScrollView sectionsScrollView) {
        boolean zCanScrollVertically = sectionsScrollView.canScrollVertically(-1);
        final boolean z = !zCanScrollVertically;
        if (this.onTop == z) {
            return;
        }
        ValueAnimator valueAnimator = this.adaptive_animator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        float f = this.onTopAnimated;
        this.onTop = z;
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(f, !zCanScrollVertically ? 1.0f : 0.0f);
        this.adaptive_animator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ActionBar.ActionBar$$ExternalSyntheticLambda8
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.lambda$setAdaptiveBackground$8(valueAnimator2);
            }
        });
        this.adaptive_animator.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ActionBar.ActionBar.16
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ActionBar.this.onTopAnimated = z ? 1.0f : 0.0f;
                ActionBar.this.adaptive_updateColor();
            }
        });
        this.adaptive_animator.setDuration(320L);
        this.adaptive_animator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.adaptive_animator.start();
    }

    public /* synthetic */ void lambda$setAdaptiveBackground$8(ValueAnimator valueAnimator) {
        this.onTopAnimated = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        adaptive_updateColor();
    }

    public void adaptive_updateColor() {
        if (this.adaptiveBackground) {
            if (this.adaptiveBackgroundHideTitle) {
                FrameLayout frameLayout = this.titlesContainer;
                if (frameLayout != null) {
                    frameLayout.setAlpha(1.0f - this.onTopAnimated);
                } else {
                    SimpleTextView simpleTextView = this.titleTextView[0];
                    if (simpleTextView != null) {
                        simpleTextView.setAlpha(1.0f - this.onTopAnimated);
                    }
                }
            }
            float f = this.onTopAnimated;
            int i = this.adaptive_lowerColorKey;
            int color = i == -1 ? 0 : Theme.getColor(i, this.resourcesProvider);
            int i2 = this.adaptive_topColorKey;
            int color2 = i2 == -1 ? 0 : Theme.getColor(i2, this.resourcesProvider);
            if (color2 == 0) {
                color2 = ColorUtils.setAlphaComponent(color, 0);
            }
            if (color == 0) {
                color = ColorUtils.setAlphaComponent(color2, 0);
            }
            setBackgroundColor(ColorUtils.blendARGB(color, color2, f));
            setShadowAlpha((int) ((1.0f - this.onTopAnimated) * 255.0f));
            if (this.blurredBackground) {
                invalidate();
            }
        }
    }
}
