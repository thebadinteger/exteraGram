package com.exteragram.messenger.utils.chats;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.View;
import android.view.ViewGroup;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.debug.DebugConfig;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.ReactionsContainerLayout;
import org.telegram.ui.Components.ScrimOptions;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundProvider;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundProviderBuilder;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceBitmap;
import org.telegram.ui.Components.blur3.utils.Blur3Utils;

public abstract class GlassMenuHelper {
    public static void captureBlur(final BlurredBackgroundSourceBitmap blurredBackgroundSourceBitmap, final BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory, final View view) {
        ScrimOptions.makeGlobalBlurBitmaps(new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                GlassMenuHelper.m1495$r8$lambda$VhYqc1_r5IlxZtuR_mFy6_NTTk(blurredBackgroundSourceBitmap, view, blurredBackgroundDrawableViewFactory, (Bitmap) obj, (Bitmap) obj2);
            }
        });
    }

    public static /* synthetic */ void m1495$r8$lambda$VhYqc1_r5IlxZtuR_mFy6_NTTk(BlurredBackgroundSourceBitmap blurredBackgroundSourceBitmap, View view, BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory, Bitmap bitmap, Bitmap bitmap2) {
        blurredBackgroundSourceBitmap.setBitmap(bitmap2);
        Blur3Utils.checkBitmapSourceMatrixScale(blurredBackgroundSourceBitmap, view);
        blurredBackgroundDrawableViewFactory.invalidateAllLinkedViews();
    }

    public static boolean isEnabled(int i, Theme.ResourcesProvider resourcesProvider) {
        return ExteraConfig.getGlassMessageMenu() && BlurredBackgroundProviderImpl.checkBlurEnabled(i, resourcesProvider);
    }

    public static boolean isHeaderMenuEnabled(int i, Theme.ResourcesProvider resourcesProvider) {
        return DebugConfig.getGlassHeaderMenu() && BlurredBackgroundProviderImpl.checkBlurEnabled(i, resourcesProvider);
    }

    public static int separatorColor(boolean z, Theme.ResourcesProvider resourcesProvider) {
        if (!z) {
            return Theme.getColor(Theme.key_actionBarDefaultSubmenuSeparator, resourcesProvider);
        }
        boolean isDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
        return Theme.multAlpha(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, resourcesProvider), isDark ? 0.03f : 0.06f);
    }

    public static BlurredBackgroundDrawable createPanelBackground(BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory, Theme.ResourcesProvider resourcesProvider, View view) {
        return blurredBackgroundDrawableViewFactory.create(view, true).setColorProvider(scrimMenuBackground(resourcesProvider)).setRadius(AndroidUtilities.dp(12.0f)).setPadding(AndroidUtilities.dp(8.0f)).setHasPadding(true);
    }

    public static BlurredBackgroundDrawable createFill(BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory, Theme.ResourcesProvider resourcesProvider, View view) {
        return blurredBackgroundDrawableViewFactory.create(view, true).setColorProvider(scrimMenuBackgroundFill(resourcesProvider));
    }

    public static void applyToPopup(BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory, Theme.ResourcesProvider resourcesProvider, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout) {
        actionBarPopupWindowLayout.setBackground(createPanelBackground(blurredBackgroundDrawableViewFactory, resourcesProvider, actionBarPopupWindowLayout));
        actionBarPopupWindowLayout.setGlassBackgroundFactory(blurredBackgroundDrawableViewFactory);
        if (actionBarPopupWindowLayout.getSwipeBack() != null) {
            actionBarPopupWindowLayout.getSwipeBack().setForegroundDrawable(createFill(blurredBackgroundDrawableViewFactory, resourcesProvider, actionBarPopupWindowLayout.getSwipeBack()));
        }
    }

    public static void applyToReusedMenu(BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory, BlurredBackgroundSourceBitmap blurredBackgroundSourceBitmap, Theme.ResourcesProvider resourcesProvider, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, View view, boolean z) {
        if (actionBarPopupWindowLayout == null) {
            return;
        }
        if (z) {
            applyToPopup(blurredBackgroundDrawableViewFactory, resourcesProvider, actionBarPopupWindowLayout);
            applyToGaps(actionBarPopupWindowLayout, separatorColor(true, resourcesProvider));
            captureBlur(blurredBackgroundSourceBitmap, blurredBackgroundDrawableViewFactory, view);
        } else {
            actionBarPopupWindowLayout.setGlassBackgroundFactory(null);
            actionBarPopupWindowLayout.setBackgroundDrawable(actionBarPopupWindowLayout.getResources().getDrawable(R.drawable.popup_fixed_alert4).mutate());
            actionBarPopupWindowLayout.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground, resourcesProvider));
            if (actionBarPopupWindowLayout.getSwipeBack() != null) {
                actionBarPopupWindowLayout.getSwipeBack().setForegroundDrawable(null);
            }
            applyToGaps(actionBarPopupWindowLayout, separatorColor(false, resourcesProvider), true);
        }
    }

    public static void applyToReactions(BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory, Theme.ResourcesProvider resourcesProvider, ReactionsContainerLayout reactionsContainerLayout) {
        reactionsContainerLayout.setGlassBackground(blurredBackgroundDrawableViewFactory, scrimMenuBackgroundFill(resourcesProvider));
    }

    public static void applyToGaps(View view, int i) {
        applyToGaps(view, i, false);
    }

    public static void applyToGaps(View view, int i, boolean z) {
        if (view instanceof ActionBarPopupWindow.GapView) {
            ActionBarPopupWindow.GapView gapView = (ActionBarPopupWindow.GapView) view;
            gapView.setColor(i);
            gapView.setDividerVisible(z);
        } else if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i2 = 0; i2 < viewGroup.getChildCount(); i2++) {
                applyToGaps(viewGroup.getChildAt(i2), i, z);
            }
        }
    }

    public static void draw(BlurredBackgroundDrawable blurredBackgroundDrawable, Canvas canvas, RectF rectF, float f, int i) {
        draw(blurredBackgroundDrawable, canvas, rectF, f, i, 0.0f, 0.0f);
    }

    public static void draw(BlurredBackgroundDrawable blurredBackgroundDrawable, Canvas canvas, RectF rectF, float f, int i, float f2, float f3) {
        boolean z = (f2 == 0.0f && f3 == 0.0f) ? false : true;
        if (z) {
            canvas.save();
            canvas.translate(-f2, -f3);
        }
        blurredBackgroundDrawable.setRadius(f);
        blurredBackgroundDrawable.setAlpha(i);
        blurredBackgroundDrawable.setBounds(Math.round(rectF.left + f2), Math.round(rectF.top + f3), Math.round(rectF.right + f2), Math.round(rectF.bottom + f3));
        blurredBackgroundDrawable.draw(canvas);
        if (z) {
            canvas.restore();
        }
    }

    private static BlurredBackgroundProviderBuilder scrimMenuGlass(Theme.ResourcesProvider resourcesProvider) {
        return new BlurredBackgroundProviderBuilder(resourcesProvider).setBackgroundColor(new BlurredBackgroundProviderBuilder.ColorProvider() { 
            @Override // org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundProviderBuilder.ColorProvider
            public final int getColor(Theme.ResourcesProvider resourcesProvider2, boolean z) {
                return Theme.multAlpha(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground), z ? 0.85f : 0.76f);
            }
        }).setStrokeColorTop(0, 0).setStrokeColorBottom(0, 0).setStrokeColorFull(0, 0).setStrokeWidth(0.0f, 0.0f);
    }

    public static BlurredBackgroundProvider scrimMenuBackground(Theme.ResourcesProvider resourcesProvider) {
        return scrimMenuGlass(resourcesProvider).setShadowColor(637534208, 0).setShadowLayer(AndroidUtilities.dpf2(4.0f), 0.0f, 0.0f).build();
    }

    public static BlurredBackgroundProvider scrimMenuBackgroundFill(Theme.ResourcesProvider resourcesProvider) {
        return scrimMenuGlass(resourcesProvider).setShadowColor(0, 0).build();
    }
}
