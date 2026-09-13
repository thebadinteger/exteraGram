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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$new$0() {
        this.inLongPress = true;
        try {
            performHapticFeedback(0);
        } catch (Exception unused) {
        }
    }

    public IconPickerView(final Context context) {
        super(context);
        this.onLongPress = new Runnable() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                IconPickerView.this.lambda$new$0();
            }
        };
        this.systemTopInset = 0;
        this.systemBottomInset = 0;
        this.mPrefs = context.getSharedPreferences("icon_picker", 0);
        this.touchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        final GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView.1
            private float startX;
            private float startY;

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onDown(MotionEvent motionEvent) {
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onSingleTapUp(MotionEvent motionEvent) {
                if (IconPickerView.this.inLongPress || IconPickerView.this.isBigMenuShown) {
                    return false;
                }
                IconPickerView.this.showIconList(true);
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                DisplayMetrics displayMetrics;
                float f3;
                if (!IconPickerView.this.isScrolling || IconPickerView.this.inLongPress) {
                    return false;
                }
                SpringForce spring = IconPickerView.this.fabXSpring.getSpring();
                float finalPosition = IconPickerView.this.fabXSpring.getSpring().getFinalPosition() + (f / 7.0f);
                float width = IconPickerView.this.getWidth() / 2.0f;
                IconPickerView iconPickerView = IconPickerView.this;
                if (finalPosition >= width) {
                    displayMetrics = iconPickerView.getResources().getDisplayMetrics();
                    f3 = 2.1474836E9f;
                } else {
                    displayMetrics = iconPickerView.getResources().getDisplayMetrics();
                    f3 = -2.1474836E9f;
                }
                spring.setFinalPosition(iconPickerView.clampX(displayMetrics, f3));
                SpringForce spring2 = IconPickerView.this.fabYSpring.getSpring();
                IconPickerView iconPickerView2 = IconPickerView.this;
                spring2.setFinalPosition(iconPickerView2.clampY(iconPickerView2.getResources().getDisplayMetrics(), IconPickerView.this.fabYSpring.getSpring().getFinalPosition() + (f2 / 10.0f)));
                IconPickerView.this.fabXSpring.start();
                IconPickerView.this.fabYSpring.start();
                IconPickerView.this.isFromFling = true;
                return true;
            }

            @Override // android.view.GestureDetector.SimpleOnGestureListener, android.view.GestureDetector.OnGestureListener
            public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent2, float f, float f2) {
                if (!IconPickerView.this.inLongPress) {
                    AndroidUtilities.cancelRunOnUIThread(IconPickerView.this.onLongPress);
                }
                if (!IconPickerView.this.isScrolling && !IconPickerView.this.isScrollDisallowed) {
                    if (Math.abs(f) >= IconPickerView.this.touchSlop || Math.abs(f2) >= IconPickerView.this.touchSlop) {
                        this.startX = IconPickerView.this.fabXSpring.getSpring().getFinalPosition();
                        this.startY = IconPickerView.this.fabYSpring.getSpring().getFinalPosition();
                        IconPickerView.this.isScrolling = true;
                    } else {
                        IconPickerView.this.isScrollDisallowed = false;
                    }
                }
                if (IconPickerView.this.isScrolling && !IconPickerView.this.inLongPress) {
                    IconPickerView.this.fabXSpring.getSpring().setFinalPosition((this.startX + motionEvent2.getRawX()) - motionEvent.getRawX());
                    IconPickerView.this.fabYSpring.getSpring().setFinalPosition((this.startY + motionEvent2.getRawY()) - motionEvent.getRawY());
                    IconPickerView.this.fabXSpring.start();
                    IconPickerView.this.fabYSpring.start();
                }
                return IconPickerView.this.isScrolling;
            }
        });
        gestureDetector.setIsLongpressEnabled(false);
        FrameLayout frameLayout = new FrameLayout(context) { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView.2
            @Override // android.view.View
            public void invalidate() {
                super.invalidate();
                IconPickerView.this.invalidate();
            }

            @Override // android.view.View
            public void setTranslationX(float f) {
                super.setTranslationX(f);
                IconPickerView.this.invalidate();
            }

            @Override // android.view.View
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                IconPickerView.this.invalidate();
            }

            @Override // android.view.View
            @SuppressLint({"ClickableViewAccessibility"})
            public boolean onTouchEvent(MotionEvent motionEvent) {
                boolean zOnTouchEvent = gestureDetector.onTouchEvent(motionEvent);
                if (motionEvent.getAction() == 0) {
                    AndroidUtilities.runOnUIThread(IconPickerView.this.onLongPress, 200L);
                    return zOnTouchEvent;
                }
                if (motionEvent.getAction() != 1 && motionEvent.getAction() != 3) {
                    return zOnTouchEvent;
                }
                AndroidUtilities.cancelRunOnUIThread(IconPickerView.this.onLongPress);
                if (!IconPickerView.this.isFromFling) {
                    IconPickerView.this.updateSpringPositions();
                }
                IconPickerView.this.inLongPress = false;
                IconPickerView.this.isScrolling = false;
                IconPickerView.this.isScrollDisallowed = false;
                IconPickerView.this.isFromFling = false;
                return zOnTouchEvent;
            }
        };
        this.floatingButtonContainer = frameLayout;
        ImageView imageView = new ImageView(context);
        this.actionIcon = imageView;
        imageView.setImageResource(R.drawable.msg_palette);
        frameLayout.addView(imageView, LayoutHelper.createFrame(24, 24, 17));
        frameLayout.setVisibility(8);
        addView(frameLayout, LayoutHelper.createFrame(56, 56.0f));
        LinearLayout linearLayout = new LinearLayout(context);
        this.bigLayout = linearLayout;
        linearLayout.setOrientation(1);
        linearLayout.setVisibility(8);
        ActionBar actionBar = new ActionBar(context);
        this.actionBar = actionBar;
        actionBar.setOccupyStatusBar(false);
        updateTitle();
        actionBar.setBackButtonImage(R.drawable.ic_ab_back);
        actionBar.setAllowOverlayTitle(false);
        ActionBarMenu actionBarMenuCreateMenu = actionBar.createMenu();
        actionBarMenuCreateMenu.addItem(0, R.drawable.outline_header_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new ActionBarMenuItem.ActionBarMenuItemSearchListener() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView.3
            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchExpand() {
                IconPickerView.this.searching = true;
                if (IconPickerView.this.otherButton != null) {
                    IconPickerView.this.otherButton.setVisibility(8);
                }
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onSearchCollapse() {
                IconPickerView.this.searching = false;
                IconPickerView.this.query = null;
                if (IconPickerView.this.otherButton != null) {
                    IconPickerView.this.otherButton.setVisibility(0);
                }
                IconPickerView.this.listView.adapter.update(true);
            }

            @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
            public void onTextChanged(EditText editText) {
                IconPickerView.this.query = editText.getText().toString();
                IconPickerView.this.listView.adapter.update(true);
            }
        });
        ActionBarMenuItem actionBarMenuItemAddItem = actionBarMenuCreateMenu.addItem(1, R.drawable.ic_ab_other);
        this.otherButton = actionBarMenuItemAddItem;
        actionBarMenuItemAddItem.addSubItem(2, R.drawable.msg_media, LocaleController.getString(R.string.IconPickerAllIcons));
        ActionBarMenuSubItem actionBarMenuSubItemAddSubItem = actionBarMenuItemAddItem.addSubItem(3, R.drawable.ic_ab_done, LocaleController.getString(R.string.IconPickerSaveAndExit));
        this.saveItem = actionBarMenuSubItemAddSubItem;
        int i = Theme.key_featuredStickers_addButtonPressed;
        actionBarMenuSubItemAddSubItem.setColors(Theme.getColor(i), Theme.getColor(i));
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView.4
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i2) {
                IconPack iconPackFindPackById;
                if (i2 == -1) {
                    IconPickerView.this.showIconList(false);
                    return;
                }
                if (i2 == 3) {
                    ExteraConfig.setEditingIconPackId(null);
                    Context context2 = context;
                    if (context2 instanceof LaunchActivity) {
                        IconPickerController.setActive((LaunchActivity) context2, false);
                        return;
                    }
                    return;
                }
                if (i2 != 2 || ExteraConfig.getEditingIconPackId() == null || (iconPackFindPackById = IconManager.INSTANCE.findPackById(ExteraConfig.getEditingIconPackId())) == null) {
                    return;
                }
                Context context3 = context;
                if (context3 instanceof LaunchActivity) {
                    ((LaunchActivity) context3).presentFragment(new IconPacksEditorActivity(iconPackFindPackById));
                    IconPickerView.this.showIconList(false);
                }
            }
        });
        linearLayout.addView(actionBar, LayoutHelper.createLinear(-1, -2));
        UniversalRecyclerView universalRecyclerView = new UniversalRecyclerView(context, UserConfig.selectedAccount, 0, new Utilities.Callback2() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView$$ExternalSyntheticLambda2
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                IconPickerView.this.fillItems((ArrayList) obj, (UniversalAdapter) obj2);
            }
        }, new Utilities.Callback5() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView$$ExternalSyntheticLambda3
            @Override // org.telegram.messenger.Utilities.Callback5
            public final void run(Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
                IconPickerView.this.onClick((UItem) obj, (View) obj2, ((Integer) obj3).intValue(), ((Float) obj4).floatValue(), ((Float) obj5).floatValue());
            }
        }, null, null);
        this.listView = universalRecyclerView;
        universalRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        linearLayout.addView(universalRecyclerView, LayoutHelper.createLinear(-1, 0, 1.0f));
        addView(linearLayout, LayoutHelper.createFrame(-1, -1.0f, 0, 8.0f, 8.0f, 8.0f, 8.0f));
        linearLayout.setClipToOutline(true);
        linearLayout.setOutlineProvider(new ViewOutlineProvider() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView.5
            @Override // android.view.ViewOutlineProvider
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), AndroidUtilities.dp(10.0f));
            }
        });
        setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView$$ExternalSyntheticLambda4
            @Override // android.view.View.OnApplyWindowInsetsListener
            public final WindowInsets onApplyWindowInsets(View view, WindowInsets windowInsets) {
                return IconPickerView.this.lambda$new$1(view, windowInsets);
            }
        });
        updateDrawables();
        setWillNotDraw(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ WindowInsets lambda$new$1(View view, WindowInsets windowInsets) {
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

    /* JADX INFO: Access modifiers changed from: private */
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

    /* JADX INFO: Access modifiers changed from: private */
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        IconManager.INSTANCE.showReplaceAlert(getContext(), uItem.id);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fillItems(final ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        final String lowerCase = (!this.searching || TextUtils.isEmpty(this.query)) ? null : this.query.toLowerCase();
        final IconPack iconPackFindPackById = ExteraConfig.getEditingIconPackId() != null ? IconManager.INSTANCE.findPackById(ExteraConfig.getEditingIconPackId()) : null;
        Stream streamSorted = IconObserver.INSTANCE.getUsedIcons().stream().map(new Function() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView$$ExternalSyntheticLambda7
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return IconPickerView.this.lambda$fillItems$2(lowerCase, iconPackFindPackById, (Integer) obj);
            }
        }).filter(new Predicate() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView$$ExternalSyntheticLambda8
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return Objects.nonNull((UItem) obj);
            }
        }).sorted(Comparator.comparing(new Function() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView$$ExternalSyntheticLambda9
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((UItem) obj).text.toString();
            }
        }));
        Objects.requireNonNull(arrayList);
        streamSorted.forEach(new Consumer() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView$$ExternalSyntheticLambda10
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                arrayList.add((UItem) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ UItem lambda$fillItems$2(String str, IconPack iconPack, Integer num) {
        try {
            String resourceEntryName = getResources().getResourceEntryName(num.intValue());
            if ((str != null && !resourceEntryName.toLowerCase().contains(str)) || IconManager.INSTANCE.isBlacklisted(resourceEntryName)) {
                return null;
            }
            UItem uItemAsIcon = IconPacksEditorActivity.EditorIconCell.Factory.asIcon(num.intValue(), resourceEntryName, iconPack);
            uItemAsIcon.transparent = true;
            return uItemAsIcon;
        } catch (Exception unused) {
            return null;
        }
    }

    @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View view, long j) {
        LinearLayout linearLayout = this.bigLayout;
        if (view == linearLayout) {
            canvas.drawColor(Color.argb((int) (linearLayout.getAlpha() * 122.0f), 0, 0, 0));
        }
        return super.drawChild(canvas, view, j);
    }

    public boolean onBackPressed(boolean z) {
        if (!this.isBigMenuShown) {
            return false;
        }
        if (z) {
            if (this.searching) {
                this.actionBar.closeSearchField();
                return true;
            }
            showIconList(false);
        }
        return true;
    }

    @SuppressLint({"ApplySharedPref"})
    public void saveConfig() {
        this.mPrefs.edit().putFloat("x", this.fabXSpring.getSpring().getFinalPosition()).putFloat("y", this.fabYSpring.getSpring().getFinalPosition()).apply();
    }

    private void updateTitle() {
        IconPack iconPackFindPackById;
        if (this.actionBar == null || ExteraConfig.getEditingIconPackId() == null || (iconPackFindPackById = IconManager.INSTANCE.findPackById(ExteraConfig.getEditingIconPackId())) == null) {
            return;
        }
        this.actionBar.setTitle(iconPackFindPackById.getName());
    }

    private void updateDrawables() {
        Drawable drawableCreateSimpleSelectorCircleDrawable = Theme.createSimpleSelectorCircleDrawable(AndroidUtilities.dp(56.0f), Theme.getColor(Theme.key_chats_actionBackground), Theme.getColor(Theme.key_chats_actionPressedBackground));
        Drawable drawableMutate = ResourcesCompat.getDrawable(getResources(), R.drawable.floating_shadow, getContext().getTheme()).mutate();
        drawableMutate.setColorFilter(new PorterDuffColorFilter(-16777216, PorterDuff.Mode.MULTIPLY));
        CombinedDrawable combinedDrawable = new CombinedDrawable(drawableMutate, drawableCreateSimpleSelectorCircleDrawable, 0, 0);
        combinedDrawable.setIconSize(AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f));
        this.floatingButtonBackground = combinedDrawable;
        GradientDrawable gradientDrawable = new GradientDrawable();
        int i = Theme.key_dialogBackground;
        gradientDrawable.setColor(Theme.getColor(i));
        gradientDrawable.setCornerRadius(AndroidUtilities.dp(10.0f));
        this.bigLayout.setBackground(gradientDrawable);
        this.bigLayout.setElevation(AndroidUtilities.dp(4.0f));
        ImageView imageView = this.actionIcon;
        if (imageView != null) {
            imageView.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_chats_actionIcon), PorterDuff.Mode.SRC_IN));
        }
        ActionBar actionBar = this.actionBar;
        if (actionBar != null) {
            actionBar.setBackgroundColor(Theme.getColor(i));
            ActionBar actionBar2 = this.actionBar;
            int i2 = Theme.key_dialogTextBlack;
            actionBar2.setTitleColor(Theme.getColor(i2));
            this.actionBar.setItemsColor(Theme.getColor(i2), false);
            this.actionBar.setItemsBackgroundColor(Theme.getColor(Theme.key_dialogButtonSelector), false);
        }
        ActionBarMenuItem actionBarMenuItem = this.otherButton;
        if (actionBarMenuItem != null) {
            actionBarMenuItem.setPopupItemsColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem), false);
            this.otherButton.setPopupItemsColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItemIcon), true);
            this.otherButton.redrawPopup(Theme.getColor(Theme.key_actionBarDefaultSubmenuBackground));
            ActionBarMenuSubItem actionBarMenuSubItem = this.saveItem;
            if (actionBarMenuSubItem != null) {
                int i3 = Theme.key_featuredStickers_addButtonPressed;
                actionBarMenuSubItem.setColors(Theme.getColor(i3), Theme.getColor(i3));
            }
        }
        invalidate();
    }

    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        return this.isBigMenuShown;
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(this.floatingButtonContainer.getTranslationX(), this.floatingButtonContainer.getTranslationY());
        canvas.scale(this.floatingButtonContainer.getScaleX(), this.floatingButtonContainer.getScaleY(), this.floatingButtonContainer.getPivotX(), this.floatingButtonContainer.getPivotY());
        this.floatingButtonBackground.setAlpha((int) (this.floatingButtonContainer.getAlpha() * 255.0f));
        this.floatingButtonBackground.setBounds(this.floatingButtonContainer.getLeft(), this.floatingButtonContainer.getTop(), this.floatingButtonContainer.getRight(), this.floatingButtonContainer.getBottom());
        this.floatingButtonBackground.draw(canvas);
        canvas.restore();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.iconPackUpdated) {
            updateTitle();
            Parcelable parcelableOnSaveInstanceState = this.listView.getLayoutManager() != null ? this.listView.getLayoutManager().onSaveInstanceState() : null;
            this.listView.adapter.update(true);
            if (parcelableOnSaveInstanceState != null) {
                this.listView.getLayoutManager().onRestoreInstanceState(parcelableOnSaveInstanceState);
                return;
            }
            return;
        }
        if (i == NotificationCenter.didSetNewTheme) {
            updateDrawables();
            this.listView.adapter.update(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float clampX(DisplayMetrics displayMetrics, float f) {
        return MathUtils.clamp(f, AndroidUtilities.dp(16.0f), displayMetrics.widthPixels - AndroidUtilities.dp(72.0f));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public float clampY(DisplayMetrics displayMetrics, float f) {
        return MathUtils.clamp(f, this.systemTopInset + AndroidUtilities.dp(16.0f), (displayMetrics.heightPixels - this.systemBottomInset) - AndroidUtilities.dp(72.0f));
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        WindowInsets rootWindowInsets = getRootWindowInsets();
        if (rootWindowInsets != null) {
            this.systemTopInset = rootWindowInsets.getSystemWindowInsetTop();
            this.systemBottomInset = rootWindowInsets.getSystemWindowInsetBottom();
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.bigLayout.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.topMargin = AndroidUtilities.dp(8.0f) + this.systemTopInset;
                layoutParams.bottomMargin = AndroidUtilities.dp(8.0f) + this.systemBottomInset;
                this.bigLayout.setLayoutParams(layoutParams);
            }
        }
        float f = this.mPrefs.getFloat("x", -1.0f);
        float f2 = this.mPrefs.getFloat("y", -1.0f);
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        this.floatingButtonContainer.setTranslationX(clampX(displayMetrics, (f == -1.0f || f >= ((float) displayMetrics.widthPixels) / 2.0f) ? 2.1474836E9f : -2.1474836E9f));
        FrameLayout frameLayout = this.floatingButtonContainer;
        if (f2 == -1.0f) {
            f2 = displayMetrics.heightPixels / 2.0f;
        }
        frameLayout.setTranslationY(clampY(displayMetrics, f2));
        FrameLayout frameLayout2 = this.floatingButtonContainer;
        this.fabXSpring = new SpringAnimation(frameLayout2, DynamicAnimation.TRANSLATION_X, frameLayout2.getTranslationX()).setSpring(new SpringForce(this.floatingButtonContainer.getTranslationX()).setStiffness(650.0f).setDampingRatio(0.75f));
        FrameLayout frameLayout3 = this.floatingButtonContainer;
        this.fabYSpring = new SpringAnimation(frameLayout3, DynamicAnimation.TRANSLATION_Y, frameLayout3.getTranslationY()).setSpring(new SpringForce(this.floatingButtonContainer.getTranslationY()).setStiffness(650.0f).setDampingRatio(0.75f));
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.iconPackUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewTheme);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.fabXSpring.cancel();
        this.fabYSpring.cancel();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.iconPackUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewTheme);
    }

    public void showIconList(final boolean z) {
        if (this.isBigMenuShown == z) {
            return;
        }
        this.isBigMenuShown = z;
        if (z) {
            this.bigLayout.setVisibility(0);
            this.listView.adapter.update(true);
        }
        final Window window = ((Activity) getContext()).getWindow();
        if (z) {
            this.wasStatusBar = window.getStatusBarColor();
        }
        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) this.bigLayout.getLayoutParams();
        final float f = layoutParams.topMargin;
        final float f2 = layoutParams.leftMargin;
        final float translationX = this.floatingButtonContainer.getTranslationX() - f2;
        final float translationY = this.floatingButtonContainer.getTranslationY() - f;
        new SpringAnimation(new FloatValueHolder(z ? 0.0f : 1000.0f)).setSpring(new SpringForce(1000.0f).setStiffness(900.0f).setDampingRatio(1.0f).setFinalPosition(z ? 1000.0f : 0.0f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView$$ExternalSyntheticLambda5
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f3, float f4) {
                IconPickerView.this.lambda$showIconList$4(translationX, translationY, f2, f, window, dynamicAnimation, f3, f4);
            }
        }).addEndListener(new DynamicAnimation.OnAnimationEndListener() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView$$ExternalSyntheticLambda6
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationEndListener
            public final void onAnimationEnd(DynamicAnimation dynamicAnimation, boolean z2, float f3, float f4) {
                IconPickerView.this.lambda$showIconList$5(translationX, f2, translationY, f, z, dynamicAnimation, z2, f3, f4);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showIconList$4(float f, float f2, float f3, float f4, Window window, DynamicAnimation dynamicAnimation, float f5, float f6) {
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

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showIconList$5(float f, float f2, float f3, float f4, boolean z, DynamicAnimation dynamicAnimation, boolean z2, float f5, float f6) {
        this.floatingButtonContainer.setTranslationX(f + f2);
        this.floatingButtonContainer.setTranslationY(f3 + f4);
        if (z) {
            return;
        }
        this.bigLayout.setVisibility(8);
    }

    @Override // android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.fabXSpring.cancel();
        this.fabYSpring.cancel();
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        FrameLayout frameLayout = this.floatingButtonContainer;
        frameLayout.setTranslationX(clampX(displayMetrics, frameLayout.getTranslationX() >= ((float) displayMetrics.widthPixels) / 2.0f ? 2.1474836E9f : -2.1474836E9f));
        FrameLayout frameLayout2 = this.floatingButtonContainer;
        frameLayout2.setTranslationY(clampY(displayMetrics, frameLayout2.getTranslationY()));
        this.fabXSpring.getSpring().setFinalPosition(this.floatingButtonContainer.getTranslationX());
        this.fabYSpring.getSpring().setFinalPosition(this.floatingButtonContainer.getTranslationY());
    }

    public void showFab() {
        this.floatingButtonContainer.setVisibility(0);
        new SpringAnimation(new FloatValueHolder(0.0f)).setSpring(new SpringForce(1000.0f).setStiffness(750.0f).setDampingRatio(0.75f)).addUpdateListener(new DynamicAnimation.OnAnimationUpdateListener() { // from class: com.exteragram.messenger.icons.ui.picker.IconPickerView$$ExternalSyntheticLambda0
            @Override // androidx.dynamicanimation.animation.DynamicAnimation.OnAnimationUpdateListener
            public final void onAnimationUpdate(DynamicAnimation dynamicAnimation, float f, float f2) {
                IconPickerView.this.lambda$showFab$6(dynamicAnimation, f, f2);
            }
        }).start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showFab$6(DynamicAnimation dynamicAnimation, float f, float f2) {
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
