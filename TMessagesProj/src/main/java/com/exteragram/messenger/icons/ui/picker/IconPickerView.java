package com.exteragram.messenger.icons.ui.picker;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowInsets;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.icons.IconPack;
import com.exteragram.messenger.icons.ui.IconPacksEditorActivity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.LaunchActivity;

public class IconPickerView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private final ActionBar actionBar;
    private final ImageView actionIcon;
    private final LinearLayout bigLayout;
    private SpringAnimation fabXSpring;
    private SpringAnimation fabYSpring;
    private Drawable floatingButtonBackground;
    private final FrameLayout floatingButtonContainer;
    private boolean inLongPress;
    private boolean isBigMenuShown;
    private boolean isFromFling;
    private boolean isScrollDisallowed;
    private boolean isScrolling;
    private final UniversalRecyclerView listView;
    private final SharedPreferences mPrefs;
    private final Runnable onLongPress;
    private final ActionBarMenuItem otherButton;
    private String query;
    private final ActionBarMenuSubItem saveItem;
    private boolean searching;
    private int systemBottomInset;
    private int systemTopInset;
    private final int touchSlop;
    private int wasStatusBar;

    public WindowInsets lambda$new$1(View view, WindowInsets windowInsets) {
        this.systemTopInset = windowInsets.getSystemWindowInsetTop();
        this.systemBottomInset = windowInsets.getSystemWindowInsetBottom();
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.bigLayout.getLayoutParams();
        if (layoutParams != null) {
            layoutParams.topMargin = AndroidUtilities.dp(8.0f) + this.systemTopInset;
            layoutParams.bottomMargin = AndroidUtilities.dp(8.0f) + this.systemBottomInset;
            this.bigLayout.setLayoutParams(layoutParams);
        }
        if (!this.isScrolling && !this.isBigMenuShown) {
            updateSpringPositions();
        }
        return windowInsets;
    }

    public void updateSpringPositions() {
        DisplayMetrics displayMetrics;
        float f;
        SpringForce spring = this.fabXSpring.getSpring();
        if (this.fabXSpring.getSpring().getFinalPosition() >= getWidth() / 2.0f) {
            displayMetrics = getResources().getDisplayMetrics();
            f = 2.1474836E9f;
        } else {
            displayMetrics = getResources().getDisplayMetrics();
            f = -2.1474836E9f;
        }
        spring.setFinalPosition(clampX(displayMetrics, f));
        this.fabYSpring.getSpring().setFinalPosition(clampY(getResources().getDisplayMetrics(), this.fabYSpring.getSpring().getFinalPosition()));
        this.fabXSpring.start();
        this.fabYSpring.start();
    }

    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        IconManager.INSTANCE.showReplaceAlert(getContext(), uItem.id);
    }

    public void fillItems(final ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        final String lowerCase = (!this.searching || TextUtils.isEmpty(this.query)) ? null : this.query.toLowerCase();
        final IconPack iconPackFindPackById = ExteraConfig.getEditingIconPackId() != null ? IconManager.INSTANCE.findPackById(ExteraConfig.getEditingIconPackId()) : null;
        Stream streamSorted = IconObserver.INSTANCE.getUsedIcons().stream().map(new Function() { 
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$fillItems$2(lowerCase, iconPackFindPackById, (Integer) obj);
            }
        }).filter(new Predicate() { 
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((UItem) obj);
            }
        }).sorted(Comparator.comparing(new Function() { 
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((UItem) obj).text.toString();
            }
        }));
        Objects.requireNonNull(arrayList);
        streamSorted.forEach(new Consumer() { 
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                arrayList.add((UItem) obj);
            }
        });
    }

    public void lambda$showIconList$4(float f, float f2, float f3, float f4, Window window, DynamicAnimation dynamicAnimation, float f5, float f6) {
        float f7 = f5 / 1000.0f;
        this.bigLayout.setAlpha(f7);
        this.bigLayout.setTranslationX(AndroidUtilities.lerp(f, 0.0f, f7));
        this.bigLayout.setTranslationY(AndroidUtilities.lerp(f2, 0.0f, f7));
        this.bigLayout.setPivotX((this.floatingButtonContainer.getTranslationX() - f3) + AndroidUtilities.dp(28.0f));
        this.bigLayout.setPivotY((this.floatingButtonContainer.getTranslationY() - f4) + AndroidUtilities.dp(28.0f));
        if (this.bigLayout.getWidth() != 0) {
            this.bigLayout.setScaleX(AndroidUtilities.lerp(this.floatingButtonContainer.getWidth() / this.bigLayout.getWidth(), 1.0f, f7));
        }
        if (this.bigLayout.getHeight() != 0) {
            this.bigLayout.setScaleY(AndroidUtilities.lerp(this.floatingButtonContainer.getHeight() / this.bigLayout.getHeight(), 1.0f, f7));
        }
        this.floatingButtonContainer.setTranslationX(AndroidUtilities.lerp(f + f3, (getWidth() / 2.0f) - AndroidUtilities.dp(28.0f), f7));
        this.floatingButtonContainer.setTranslationY(AndroidUtilities.lerp(f2 + f4, (getHeight() / 2.0f) - AndroidUtilities.dp(28.0f), f7));
        this.floatingButtonContainer.setAlpha(1.0f - f7);
        window.setStatusBarColor(ColorUtils.blendARGB(this.wasStatusBar, 2046820352, f7));
        invalidate();
    }

    public void lambda$showFab$6(DynamicAnimation dynamicAnimation, float f, float f2) {
        float f3 = f / 1000.0f;
        this.floatingButtonContainer.setPivotX(AndroidUtilities.dp(28.0f));
        this.floatingButtonContainer.setPivotY(AndroidUtilities.dp(28.0f));
        this.floatingButtonContainer.setScaleX(f3);
        this.floatingButtonContainer.setScaleY(f3);
        this.floatingButtonContainer.setAlpha(MathUtils.clamp(f3, 0.0f, 1.0f));
        invalidate();
    }

    public void dismiss(Runnable runnable) {
        runnable.run();
    }
}
