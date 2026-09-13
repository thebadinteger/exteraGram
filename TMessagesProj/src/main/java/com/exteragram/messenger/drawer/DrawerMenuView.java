package com.exteragram.messenger.drawer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.MainMenuItem;
import com.exteragram.messenger.utils.chats.MainMenuHelper;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;

public class DrawerMenuView extends ScrollView {
    private final LinearLayout container;
    private int lastGradientColor;
    private Runnable onItemClick;
    private LinearGradient topGradient;
    private final Paint topGradientPaint;
    private static final float DIVIDER_HEIGHT_DP = 1.0f / AndroidUtilities.density;
    private static final int COLOR_KEY_BACKGROUND = Theme.key_windowBackgroundWhite;

    public DrawerMenuView(Context context) {
        super(context);
        this.topGradientPaint = new Paint();
        setVerticalScrollBarEnabled(false);
        LinearLayout linearLayout = new LinearLayout(context);
        this.container = linearLayout;
        linearLayout.setOrientation(1);
        linearLayout.setPadding(0, AndroidUtilities.dp(8.0f), 0, AndroidUtilities.dp(8.0f) + AndroidUtilities.navigationBarHeight);
        addView(linearLayout, new FrameLayout.LayoutParams(-1, -2));
        updateGradient();
    }

    public void setOnItemClick(Runnable runnable) {
        this.onItemClick = runnable;
    }

    public void clearMenu() {
        this.container.removeAllViews();
    }

    public void rebuildMenu(int i, BaseFragment baseFragment) {
        clearMenu();
        MainMenuHelper.MenuContext menuContextCreateMenuContext = MainMenuHelper.createMenuContext(i, baseFragment);
        boolean z = false;
        boolean z2 = false;
        for (int i2 = 0; i2 < ExteraConfig.getMainMenuLayout().size(); i2++) {
            Integer num = ExteraConfig.getMainMenuLayout().get(i2);
            if (num.intValue() != MainMenuItem.DIVIDER.getId()) {
                List<MainMenuHelper.MenuItemInfo> listResolveDrawerMenuItems = MainMenuHelper.resolveDrawerMenuItems(num.intValue(), menuContextCreateMenuContext);
                if (!listResolveDrawerMenuItems.isEmpty()) {
                    if (z2) {
                        View view = new View(getContext());
                        view.setBackgroundColor(Theme.getDividerColor(null));
                        this.container.addView(view, createDividerLayoutParams());
                        z2 = false;
                    }
                    for (final MainMenuHelper.MenuItemInfo menuItemInfo : listResolveDrawerMenuItems) {
                        DrawerMenuItemView drawerMenuItemView = new DrawerMenuItemView(getContext());
                        drawerMenuItemView.setMenuItem(num.intValue(), i, menuItemInfo.iconRes(), menuItemInfo.text());
                        drawerMenuItemView.setOnClickListener(new View.OnClickListener() { 
                            @Override // android.view.View.OnClickListener
                            public final void onClick(View view2) {
                                DrawerMenuView.this.lambda$rebuildMenu$0(menuItemInfo, view2);
                            }
                        });
                        if (menuItemInfo.onLongClick() != null) {
                            drawerMenuItemView.setOnLongClickListener(new View.OnLongClickListener() { 
                                @Override // android.view.View.OnLongClickListener
                                public final boolean onLongClick(View view2) {
                                    return DrawerMenuView.this.lambda$rebuildMenu$1(menuItemInfo, view2);
                                }
                            });
                        }
                        this.container.addView(drawerMenuItemView);
                    }
                    z = true;
                }
            } else if (z) {
                z2 = true;
            }
        }
    }

    public /* synthetic */ void lambda$rebuildMenu$0(MainMenuHelper.MenuItemInfo menuItemInfo, View view) {
        Runnable runnable = this.onItemClick;
        if (runnable != null) {
            runnable.run();
        }
        if (menuItemInfo.onClick() != null) {
            menuItemInfo.onClick().run();
        }
    }

    public /* synthetic */ boolean lambda$rebuildMenu$1(MainMenuHelper.MenuItemInfo menuItemInfo, View view) {
        Runnable runnable = this.onItemClick;
        if (runnable != null) {
            runnable.run();
        }
        menuItemInfo.onLongClick().run();
        return true;
    }

    public void updateUnreadCounters(int i) {
        for (int i2 = 0; i2 < this.container.getChildCount(); i2++) {
            View childAt = this.container.getChildAt(i2);
            if (childAt instanceof DrawerMenuItemView) {
                ((DrawerMenuItemView) childAt).updateUnreadCounter(i);
            }
        }
    }

    public void updateColors() {
        for (int i = 0; i < this.container.getChildCount(); i++) {
            View childAt = this.container.getChildAt(i);
            if (childAt instanceof DrawerMenuItemView) {
                ((DrawerMenuItemView) childAt).updateColors();
            } else {
                childAt.setBackgroundColor(Theme.getDividerColor(null));
            }
        }
        updateGradient();
    }

    private void updateGradient() {
        int color = Theme.getColor(COLOR_KEY_BACKGROUND);
        if (this.topGradient == null || color != this.lastGradientColor) {
            this.lastGradientColor = color;
            LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, 0.0f, AndroidUtilities.dp(16.0f), new int[]{color, 16777215 & color}, (float[]) null, Shader.TileMode.CLAMP);
            this.topGradient = linearGradient;
            this.topGradientPaint.setShader(linearGradient);
            invalidate();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (getScrollY() > 0) {
            canvas.save();
            canvas.translate(0.0f, getScrollY());
            canvas.drawRect(0.0f, 0.0f, getWidth(), AndroidUtilities.dp(16.0f), this.topGradientPaint);
            canvas.restore();
        }
    }

    private static LinearLayout.LayoutParams createDividerLayoutParams() {
        return LayoutHelper.createLinear(-1, DIVIDER_HEIGHT_DP, 87, 12, 8, 12, 8);
    }
}
