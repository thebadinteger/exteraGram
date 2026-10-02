package com.exteragram.messenger.appicons.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.appicons.AppIcon;
import com.exteragram.messenger.appicons.AppIconController;
import com.exteragram.messenger.appicons.AppIconPreviewLoader;
import com.exteragram.messenger.appicons.ui.components.AppIconBulletinLayout;
import com.exteragram.messenger.appicons.ui.components.AppIconCell;
import com.exteragram.messenger.appicons.ui.components.AppIconHeroView;
import com.exteragram.messenger.utils.ui.ChatHeaderUiHelper;
import com.exteragram.messenger.utils.ui.UIUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.EdgeToEdgeSupportMode;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceColor;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

/* JADX INFO: loaded from: classes4.dex */
public class AppIconsActivity extends BaseFragment {
    private AppIcon appliedIcon;
    private int backgroundColor;
    private int bottomInset;
    private ButtonWithCounterView button;
    private FrameLayout buttonContainer;
    private float collapse;
    private ValueAnimator colorAnimator;
    private ContentView contentView;
    private BlurredBackgroundSourceColor glassSource;
    private int heroHeight;
    private AppIconHeroView heroView;
    private int leftInset;
    private UniversalRecyclerView listView;
    private int paneWidth;
    private AppIcon previewIcon;
    private int rightInset;
    private int scrolled;
    private float topFade;
    private boolean twoPane;
    private Insets cutout = Insets.NONE;
    private boolean buttonFaded = true;

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        AppIcon selectedIcon = AppIconController.getSelectedIcon();
        this.appliedIcon = selectedIcon;
        this.previewIcon = selectedIcon;
        this.backgroundColor = toThemeTint(AppIconPreviewLoader.getAccent(selectedIcon));
        this.actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        this.actionBar.setAllowOverlayTitle(false);
        this.actionBar.setCastShadows(false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setItemsColor(getThemedColor(Theme.key_windowBackgroundWhiteBlackText), false);
        this.actionBar.setItemsBackgroundColor(getThemedColor(Theme.key_listSelector), false);
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: com.exteragram.messenger.appicons.ui.AppIconsActivity.1
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1) {
                    AppIconsActivity.this.finishFragment();
                }
            }
        });
        this.contentView = new ContentView(context);
        BlurredBackgroundSourceColor blurredBackgroundSourceColor = new BlurredBackgroundSourceColor();
        this.glassSource = blurredBackgroundSourceColor;
        blurredBackgroundSourceColor.setColor(getTopColor());
        BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory = new BlurredBackgroundDrawableViewFactory(this.glassSource);
        blurredBackgroundDrawableViewFactory.setSourceRootView(new ViewPositionWatcher(this.contentView), this.contentView);
        this.actionBar.setGlassPadding(AndroidUtilities.dp(12.0f));
        this.actionBar.setupGlass(blurredBackgroundDrawableViewFactory, BlurredBackgroundProviderImpl.topPanelChatActivity(getResourceProvider()));
        ChatHeaderUiHelper.applyChatHeaderGlassStyle(this.actionBar, true);
        AppIconHeroView appIconHeroView = new AppIconHeroView(context, this);
        this.heroView = appIconHeroView;
        appIconHeroView.setOnPreviewReady(new Runnable() { // from class: com.exteragram.messenger.appicons.ui.AppIconsActivity$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                AppIconsActivity.this.applyAccent();
            }
        });
        this.heroView.set(this.previewIcon);
        this.contentView.addView(this.heroView, LayoutHelper.createFrame(-1, -2, 48));
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(this, new Utilities.Callback2() { // from class: com.exteragram.messenger.appicons.ui.AppIconsActivity$$ExternalSyntheticLambda1
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                AppIconsActivity.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() { // from class: com.exteragram.messenger.appicons.ui.AppIconsActivity$$ExternalSyntheticLambda2
            @Override // org.telegram.messenger.Utilities.Callback5
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                AppIconsActivity.this.onItemClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, null);
        this.listView = universalRecyclerView;
        Point point = AndroidUtilities.displaySize;
        universalRecyclerView.setSpanCount(getSpanCount(getGridWidth(point.x, point.y)));
        this.listView.adapter.setApplyBackground(false);
        this.listView.setDrawSelection(false);
        this.listView.setClipToPadding(false);
        this.listView.addOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.exteragram.messenger.appicons.ui.AppIconsActivity.2
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i, int i2) {
                AppIconsActivity.this.updateCollapse(i2);
            }
        });
        this.contentView.addView(this.listView, LayoutHelper.createFrame(-1, -1.0f));
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(context, true, getResourceProvider());
        this.button = buttonWithCounterView;
        buttonWithCounterView.setRound();
        this.button.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.appicons.ui.AppIconsActivity$$ExternalSyntheticLambda3
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AppIconsActivity.this.lambda$createView$0(view);
            }
        });
        updateButton(false);
        FrameLayout frameLayout = new FrameLayout(context);
        this.buttonContainer = frameLayout;
        frameLayout.setBackground(UIUtil.createBottomFade(getThemedColor(Theme.key_windowBackgroundWhite)));
        this.buttonContainer.addView(this.button, LayoutHelper.createFrame(-1, 48.0f, 80, 16.0f, 12.0f, 16.0f, 12.0f));
        this.buttonContainer.addOnLayoutChangeListener(new View.OnLayoutChangeListener() { // from class: com.exteragram.messenger.appicons.ui.AppIconsActivity$$ExternalSyntheticLambda4
            @Override // android.view.View.OnLayoutChangeListener
            public final void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
                AppIconsActivity.this.lambda$createView$1(view, i, i2, i3, i4, i5, i6, i7, i8);
            }
        });
        this.contentView.addView(this.buttonContainer, LayoutHelper.createFrame(-1, -2, 80));
        this.contentView.addView(this.actionBar);
        ContentView contentView = this.contentView;
        this.fragmentView = contentView;
        return contentView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view) {
        onButtonClick();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        updateListPadding();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        ValueAnimator valueAnimator = this.colorAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.colorAnimator = null;
        }
        AppIconPreviewLoader.trimAbove(AndroidUtilities.dp(68.0f));
        super.onFragmentDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getPreviewSizeDp(int i) {
        int currentActionBarHeight;
        int iDp;
        if (this.heroView == null) {
            return 128;
        }
        if (this.twoPane) {
            currentActionBarHeight = ((i - AndroidUtilities.statusBarHeight) - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.dp(72.0f);
            iDp = this.bottomInset;
        } else {
            currentActionBarHeight = ((int) (i * 0.45f)) - AndroidUtilities.statusBarHeight;
            iDp = AndroidUtilities.dp(68.0f);
        }
        return Math.max(64, Math.min(128, (int) (((currentActionBarHeight - iDp) - this.heroView.getTextBlockHeight()) / AndroidUtilities.density)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean isTwoPane(int i, int i2) {
        return i > i2 && i >= AndroidUtilities.dp(560.0f);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static int paneWidthFor(int i) {
        return Math.max(AndroidUtilities.dp(280.0f), Math.min(AndroidUtilities.dp(400.0f), (int) (i * 0.42f)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getSpanCount(int i) {
        float fDp = i - (AndroidUtilities.dp(8.0f) * 2);
        int iMax = Math.max(3, Math.min(8, Math.round(fDp / AndroidUtilities.dp(100.0f))));
        return (iMax >= 8 || fDp / ((float) iMax) <= ((float) AndroidUtilities.dp(112.0f))) ? iMax : iMax + 1;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getGridWidth(int i, int i2) {
        int iPaneWidthFor;
        int i3;
        if (isTwoPane(i, i2)) {
            iPaneWidthFor = i - paneWidthFor(i);
            i3 = this.rightInset;
        } else {
            iPaneWidthFor = i - this.leftInset;
            i3 = this.rightInset;
        }
        return iPaneWidthFor - i3;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        List<AppIcon> availableIcons = AppIconController.getAvailableIcons();
        AppIcon appIcon = this.previewIcon;
        if (appIcon == null || !availableIcons.contains(appIcon)) {
            AppIcon selectedIcon = AppIconController.getSelectedIcon();
            this.appliedIcon = selectedIcon;
            this.previewIcon = selectedIcon;
        }
        Iterator<AppIcon> it = availableIcons.iterator();
        while (it.hasNext()) {
            AppIcon next = it.next();
            arrayList.add(AppIconCell.Factory.asAppIcon(next, next == this.previewIcon));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onItemClick(UItem uItem, View view, int i, float f, float f2) {
        AppIcon appIcon;
        Object obj = uItem.object;
        if (!(obj instanceof AppIcon) || (appIcon = (AppIcon) obj) == this.previewIcon) {
            return;
        }
        this.previewIcon = appIcon;
        this.heroView.set(appIcon);
        applyAccent();
        updateButton(true);
        this.listView.adapter.update(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void applyAccent() {
        int accent;
        AppIcon appIcon = this.previewIcon;
        if (appIcon == null || (accent = AppIconPreviewLoader.getAccent(appIcon)) == 0) {
            return;
        }
        animateBackgroundColor(toThemeTint(accent));
    }

    private void onButtonClick() {
        AppIcon appIcon = this.previewIcon;
        if (appIcon == null) {
            return;
        }
        if (appIcon == this.appliedIcon) {
            finishFragment();
            return;
        }
        AppIconController.setIcon(appIcon);
        this.appliedIcon = this.previewIcon;
        updateButton(true);
        Bulletin.make(this, new AppIconBulletinLayout(getContext(), this.appliedIcon, getResourceProvider()), 1500).show();
    }

    private void updateButton(boolean z) {
        ButtonWithCounterView buttonWithCounterView = this.button;
        if (buttonWithCounterView == null) {
            return;
        }
        buttonWithCounterView.setText(LocaleController.getString(this.previewIcon == this.appliedIcon ? R.string.AppIconsKeep : R.string.AppIconsApply), z);
    }

    private void animateBackgroundColor(final int i) {
        if (this.backgroundColor == i) {
            return;
        }
        ValueAnimator valueAnimator = this.colorAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.colorAnimator = null;
        }
        final int i2 = this.backgroundColor;
        if (i2 == 0) {
            this.backgroundColor = i;
            invalidateHeader();
            updateStatusBar();
        } else {
            ValueAnimator duration = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(280L);
            this.colorAnimator = duration;
            duration.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.colorAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: com.exteragram.messenger.appicons.ui.AppIconsActivity$$ExternalSyntheticLambda5
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                    AppIconsActivity.this.lambda$animateBackgroundColor$2(i2, i, valueAnimator2);
                }
            });
            this.colorAnimator.addListener(new AnimatorListenerAdapter() { // from class: com.exteragram.messenger.appicons.ui.AppIconsActivity.3
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    AppIconsActivity.this.backgroundColor = i;
                    AppIconsActivity.this.updateStatusBar();
                }
            });
            this.colorAnimator.start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$animateBackgroundColor$2(int i, int i2, ValueAnimator valueAnimator) {
        this.backgroundColor = ColorUtils.blendARGB(i, i2, ((Float) valueAnimator.getAnimatedValue()).floatValue());
        invalidateHeader();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateHeader() {
        this.contentView.invalidate();
        BlurredBackgroundSourceColor blurredBackgroundSourceColor = this.glassSource;
        if (blurredBackgroundSourceColor != null) {
            blurredBackgroundSourceColor.setColor(getTopColor());
            this.actionBar.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateStatusBar() {
        if (getParentActivity() != null) {
            AndroidUtilities.setLightStatusBar(getParentActivity().getWindow(), isLightStatusBar());
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        return !AndroidUtilities.isDarkColor(getTopColor());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int toThemeTint(int i) {
        if (i == 0) {
            return 0;
        }
        float[] fArr = new float[3];
        ColorUtils.colorToHSL(i, fArr);
        fArr[1] = Math.min(fArr[1], 0.6f);
        fArr[2] = Theme.isCurrentThemeDark() ? 0.22f : 0.86f;
        return ColorUtils.HSLToColor(fArr);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getBaseColor() {
        int themedColor = getThemedColor(Theme.key_windowBackgroundGray);
        int i = this.backgroundColor;
        return i == 0 ? themedColor : ColorUtils.blendARGB(themedColor, i, 0.25f);
    }

    private int getTopColor() {
        return this.backgroundColor == 0 ? getBaseColor() : ColorUtils.blendARGB(getBaseColor(), this.backgroundColor, 0.7f);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public EdgeToEdgeSupportMode getEdgeToEdgeSupportMode() {
        return EdgeToEdgeSupportMode.FULL;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public WindowInsetsCompat onInsetsInternal(View view, WindowInsetsCompat windowInsetsCompat) {
        this.cutout = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.displayCutout());
        return super.onInsetsInternal(view, windowInsetsCompat);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onInsets(int i, int i2, int i3, int i4) {
        this.bottomInset = i4;
        this.leftInset = Math.max(i, this.cutout.left);
        this.rightInset = Math.max(i3, this.cutout.right);
        ContentView contentView = this.contentView;
        if (contentView != null) {
            contentView.requestLayout();
        }
        updateListPadding();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateListPadding() {
        int iDp;
        int iDp2;
        int iDp3;
        if (this.listView == null) {
            return;
        }
        int iDp4 = AndroidUtilities.dp(8.0f) + (this.twoPane ? 0 : this.leftInset);
        int iDp5 = AndroidUtilities.dp(8.0f) + this.rightInset;
        if (this.twoPane) {
            iDp = AndroidUtilities.statusBarHeight + AndroidUtilities.dp(10.0f);
            iDp3 = this.bottomInset + AndroidUtilities.dp(10.0f);
        } else {
            iDp = this.heroHeight + AndroidUtilities.dp(10.0f);
            FrameLayout frameLayout = this.buttonContainer;
            if (frameLayout == null || frameLayout.getHeight() <= 0) {
                iDp2 = this.bottomInset + AndroidUtilities.dp(72.0f);
            } else {
                iDp2 = this.buttonContainer.getHeight();
            }
            iDp3 = iDp2;
        }
        if (this.listView.getPaddingTop() != iDp || this.listView.getPaddingBottom() != iDp3 || this.listView.getPaddingLeft() != iDp4 || this.listView.getPaddingRight() != iDp5) {
            this.listView.setPadding(iDp4, iDp, iDp5, iDp3);
        }
        updateCollapse(0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getCollapseRange() {
        if (this.twoPane) {
            return 0;
        }
        return Math.max(0, (this.heroHeight - ActionBar.getCurrentActionBarHeight()) - AndroidUtilities.statusBarHeight);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateCollapse(int i) {
        if (this.listView == null || this.heroView == null) {
            return;
        }
        int paddingTop = this.scrolled + i;
        for (int i2 = 0; i2 < this.listView.getChildCount(); i2++) {
            View childAt = this.listView.getChildAt(i2);
            if (this.listView.getChildAdapterPosition(childAt) == 0) {
                paddingTop = this.listView.getPaddingTop() - childAt.getTop();
                break;
            }
        }
        this.scrolled = Math.max(0, paddingTop);
        int collapseRange = getCollapseRange();
        float fMin = collapseRange <= 0 ? 0.0f : Math.min(1.0f, this.scrolled / collapseRange);
        float fMin2 = Math.min(1.0f, this.scrolled / AndroidUtilities.dp(16.0f));
        if (Math.abs(fMin - this.collapse) >= 5.0E-4f || Math.abs(fMin2 - this.topFade) >= 5.0E-4f) {
            this.collapse = fMin;
            this.topFade = fMin2;
            this.heroView.setCollapse(fMin);
            this.contentView.invalidate();
        }
    }

    public class ContentView extends FrameLayout implements Theme.Colorable {
        private int glowColor;
        private float glowCx;
        private float glowCy;
        private int glowHeight;
        private final Paint glowPaint;
        private int glowWidth;
        private boolean heroLinkPressed;
        private final Paint panelPaint;
        private final Path panelPath;
        private final float[] panelRadii;
        private int topFadeColor;
        private Drawable topFadeDrawable;

        public ContentView(Context context) {
            super(context);
            this.glowPaint = new Paint(1);
            this.panelPaint = new Paint(1);
            this.panelPath = new Path();
            this.panelRadii = new float[8];
            setWillNotDraw(false);
        }

        @Override // android.widget.FrameLayout, android.view.View
        public void onMeasure(int i, int i2) {
            int size = View.MeasureSpec.getSize(i);
            int size2 = View.MeasureSpec.getSize(i2);
            boolean z = AppIconsActivity.this.twoPane;
            AppIconsActivity.this.twoPane = AppIconsActivity.isTwoPane(size, size2);
            AppIconsActivity appIconsActivity = AppIconsActivity.this;
            appIconsActivity.paneWidth = appIconsActivity.twoPane ? AppIconsActivity.paneWidthFor(size) : size;
            boolean z2 = AppIconsActivity.this.twoPane;
            AppIconsActivity appIconsActivity2 = AppIconsActivity.this;
            if (z2) {
                appIconsActivity2.heroView.setPadding(0, 0, 0, 0);
            } else {
                appIconsActivity2.heroView.setPadding(0, AndroidUtilities.statusBarHeight + AndroidUtilities.dp(28.0f), 0, AndroidUtilities.dp(40.0f));
            }
            AppIconsActivity.this.heroView.setPreviewSizeDp(AppIconsActivity.this.getPreviewSizeDp(size2));
            AppIconsActivity.this.buttonContainer.setPadding(AppIconsActivity.this.twoPane ? 0 : AppIconsActivity.this.leftInset, 0, AppIconsActivity.this.twoPane ? 0 : AppIconsActivity.this.rightInset, AppIconsActivity.this.bottomInset);
            if (AppIconsActivity.this.buttonFaded != (!AppIconsActivity.this.twoPane)) {
                AppIconsActivity appIconsActivity3 = AppIconsActivity.this;
                appIconsActivity3.buttonFaded = !appIconsActivity3.twoPane;
                AppIconsActivity.this.buttonContainer.setBackground(AppIconsActivity.this.buttonFaded ? UIUtil.createBottomFade(AppIconsActivity.this.getThemedColor(Theme.key_windowBackgroundWhite)) : null);
            }
            AppIconsActivity appIconsActivity4 = AppIconsActivity.this;
            int spanCount = appIconsActivity4.getSpanCount(appIconsActivity4.getGridWidth(size, size2));
            if (AppIconsActivity.this.listView.getSpanCount() != spanCount) {
                AppIconsActivity.this.listView.setSpanCount(spanCount);
            }
            if (!AppIconsActivity.this.twoPane) {
                super.onMeasure(i, i2);
            } else {
                int iMakeMeasureSpec = View.MeasureSpec.makeMeasureSpec(AppIconsActivity.this.paneWidth - AppIconsActivity.this.leftInset, TLObject.FLAG_30);
                int iMakeMeasureSpec2 = View.MeasureSpec.makeMeasureSpec(size2, Integer.MIN_VALUE);
                AppIconsActivity.this.getActionBar().measure(iMakeMeasureSpec, iMakeMeasureSpec2);
                AppIconsActivity.this.buttonContainer.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
                AppIconsActivity.this.heroView.measure(iMakeMeasureSpec, iMakeMeasureSpec2);
                AppIconsActivity.this.listView.measure(View.MeasureSpec.makeMeasureSpec(size - AppIconsActivity.this.paneWidth, TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(size2, TLObject.FLAG_30));
                setMeasuredDimension(size, size2);
            }
            int measuredHeight = AppIconsActivity.this.heroView.getMeasuredHeight();
            if (measuredHeight > 0) {
                if (measuredHeight == AppIconsActivity.this.heroHeight && z == AppIconsActivity.this.twoPane) {
                    return;
                }
                AppIconsActivity.this.heroHeight = measuredHeight;
                final AppIconsActivity appIconsActivity5 = AppIconsActivity.this;
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.appicons.ui.AppIconsActivity$ContentView$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        appIconsActivity5.updateListPadding();
                    }
                });
            }
        }

        @Override // org.telegram.ui.ActionBar.Theme.Colorable
        public void updateColors() {
            AppIconsActivity appIconsActivity = AppIconsActivity.this;
            appIconsActivity.backgroundColor = appIconsActivity.toThemeTint(AppIconPreviewLoader.getAccent(appIconsActivity.previewIcon));
            AppIconsActivity.this.getActionBar().setItemsColor(AppIconsActivity.this.getThemedColor(Theme.key_windowBackgroundWhiteBlackText), false);
            AppIconsActivity.this.getActionBar().setItemsBackgroundColor(AppIconsActivity.this.getThemedColor(Theme.key_listSelector), false);
            if (AppIconsActivity.this.buttonFaded) {
                AppIconsActivity.this.buttonContainer.setBackground(UIUtil.createBottomFade(AppIconsActivity.this.getThemedColor(Theme.key_windowBackgroundWhite)));
            }
            AppIconsActivity.this.invalidateHeader();
        }

        @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
        public void onLayout(boolean z, int i, int i2, int i3, int i4) {
            if (AppIconsActivity.this.twoPane) {
                int i5 = i4 - i2;
                AppIconsActivity.this.getActionBar().layout(AppIconsActivity.this.leftInset, 0, AppIconsActivity.this.leftInset + AppIconsActivity.this.getActionBar().getMeasuredWidth(), AppIconsActivity.this.getActionBar().getMeasuredHeight());
                AppIconsActivity.this.listView.layout(AppIconsActivity.this.paneWidth, 0, i3 - i, i5);
                int measuredHeight = i5 - AppIconsActivity.this.buttonContainer.getMeasuredHeight();
                AppIconsActivity.this.buttonContainer.layout(AppIconsActivity.this.leftInset, measuredHeight, AppIconsActivity.this.leftInset + AppIconsActivity.this.buttonContainer.getMeasuredWidth(), i5);
                int currentActionBarHeight = AndroidUtilities.statusBarHeight + ActionBar.getCurrentActionBarHeight();
                int iMax = currentActionBarHeight + Math.max(0, ((measuredHeight - currentActionBarHeight) - AppIconsActivity.this.heroHeight) / 2);
                AppIconsActivity.this.heroView.layout(AppIconsActivity.this.leftInset, iMax, AppIconsActivity.this.leftInset + AppIconsActivity.this.heroView.getMeasuredWidth(), AppIconsActivity.this.heroHeight + iMax);
                AppIconsActivity.this.updateCollapse(0);
                return;
            }
            super.onLayout(z, i, i2, i3, i4);
            AppIconsActivity.this.updateCollapse(0);
        }

        @Override // android.view.ViewGroup, android.view.View
        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            if (AppIconsActivity.this.heroView != null) {
                int actionMasked = motionEvent.getActionMasked();
                if (actionMasked == 0) {
                    boolean zDispatchLinkTouch = AppIconsActivity.this.heroView.dispatchLinkTouch(motionEvent);
                    this.heroLinkPressed = zDispatchLinkTouch;
                    if (zDispatchLinkTouch) {
                        return true;
                    }
                } else if (this.heroLinkPressed) {
                    if (actionMasked == 1 || actionMasked == 3) {
                        this.heroLinkPressed = false;
                    }
                    AppIconsActivity.this.heroView.dispatchLinkTouch(motionEvent);
                    return true;
                }
            }
            return super.dispatchTouchEvent(motionEvent);
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            canvas.drawColor(AppIconsActivity.this.getBaseColor());
            if (AppIconsActivity.this.backgroundColor == 0 || AppIconsActivity.this.collapse >= 1.0f) {
                return;
            }
            float width = (AppIconsActivity.this.twoPane ? AppIconsActivity.this.leftInset + AppIconsActivity.this.paneWidth : getWidth()) / 2.0f;
            float top = AppIconsActivity.this.heroView.getTop() + AppIconsActivity.this.heroView.getPaddingTop() + (AppIconsActivity.this.heroView.getPreviewSize() / 2.0f);
            if (this.glowColor != AppIconsActivity.this.backgroundColor || this.glowWidth != getWidth() || this.glowHeight != getHeight() || this.glowCx != width || this.glowCy != top) {
                float fMax = Math.max(AppIconsActivity.this.twoPane ? AppIconsActivity.this.paneWidth : getWidth(), AppIconsActivity.this.heroHeight) * 0.9f;
                if (fMax <= 0.0f) {
                    return;
                }
                this.glowColor = AppIconsActivity.this.backgroundColor;
                this.glowWidth = getWidth();
                this.glowHeight = getHeight();
                this.glowCx = width;
                this.glowCy = top;
                this.glowPaint.setShader(new RadialGradient(width, top, fMax, new int[]{ColorUtils.setAlphaComponent(AppIconsActivity.this.backgroundColor, 200), ColorUtils.setAlphaComponent(AppIconsActivity.this.backgroundColor, 0)}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP));
            }
            this.glowPaint.setAlpha((int) ((1.0f - AppIconsActivity.this.collapse) * 255.0f));
            canvas.drawRect(0.0f, 0.0f, getWidth(), getHeight(), this.glowPaint);
        }

        @Override // android.view.ViewGroup
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (view != AppIconsActivity.this.listView) {
                return super.drawChild(canvas, view, j);
            }
            float fDp = AndroidUtilities.dp(20.0f);
            Arrays.fill(this.panelRadii, 0.0f);
            boolean z = AppIconsActivity.this.twoPane;
            float[] fArr = this.panelRadii;
            if (z) {
                fArr[7] = fDp;
                fArr[6] = fDp;
                fArr[1] = fDp;
                fArr[0] = fDp;
                AndroidUtilities.rectTmp.set(AppIconsActivity.this.paneWidth, 0.0f, getWidth(), getHeight());
            } else {
                fArr[3] = fDp;
                fArr[2] = fDp;
                fArr[1] = fDp;
                fArr[0] = fDp;
                AndroidUtilities.rectTmp.set(0.0f, (AppIconsActivity.this.listView.getY() + AppIconsActivity.this.heroHeight) - (AppIconsActivity.this.getCollapseRange() * AppIconsActivity.this.collapse), getWidth(), getHeight());
            }
            this.panelPath.rewind();
            Path path = this.panelPath;
            RectF rectF = AndroidUtilities.rectTmp;
            path.addRoundRect(rectF, this.panelRadii, Path.Direction.CW);
            float f = rectF.top;
            Paint paint = this.panelPaint;
            AppIconsActivity appIconsActivity = AppIconsActivity.this;
            int i = Theme.key_windowBackgroundWhite;
            paint.setColor(appIconsActivity.getThemedColor(i));
            canvas.drawPath(this.panelPath, this.panelPaint);
            canvas.save();
            canvas.clipPath(this.panelPath);
            boolean zDrawChild = super.drawChild(canvas, view, j);
            if (AppIconsActivity.this.topFade > 0.0f) {
                int themedColor = AppIconsActivity.this.getThemedColor(i);
                if (this.topFadeDrawable == null || this.topFadeColor != themedColor) {
                    this.topFadeColor = themedColor;
                    this.topFadeDrawable = UIUtil.createTopFade(themedColor);
                }
                int i2 = (int) f;
                this.topFadeDrawable.setBounds(0, i2, getWidth(), AndroidUtilities.dp(24.0f) + i2);
                this.topFadeDrawable.setAlpha((int) (AppIconsActivity.this.topFade * 255.0f));
                this.topFadeDrawable.draw(canvas);
            }
            canvas.restore();
            return zDrawChild;
        }
    }
}
