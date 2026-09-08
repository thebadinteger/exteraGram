package com.exteragram.messenger.plugins.ui.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import androidx.core.content.ContextCompat;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.plugins.hooks.MenuItemRecord;
import java.util.List;
import java.util.Map;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PopupSwipeBackLayout;

@Metadata(d1 = {"\u0000P\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0017\u0018\u0000  2\u00020\u0001:\u0001 BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u000e\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\n\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\f¢\u0006\u0004\b\r\u0010\u000eB7\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u000f\u001a\u00020\b\u0012\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\n\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\f¢\u0006\u0004\b\r\u0010\u0010J\u0016\u0010\u0017\u001a\u00020\u00182\u000e\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0005J\u0010\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u001cH\u0002J\b\u0010\u001d\u001a\u00020\u001eH\u0002J\b\u0010\u001f\u001a\u00020\u0018H\u0014R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\nX\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u0011\u0010\u0011\u001a\u00020\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0015\u001a\u00020\u0012¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014¨\u0006!"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/PluginsMenuWrapper;", _UrlKt.FRAGMENT_ENCODE_SET, "swipeBackLayout", "Lorg/telegram/ui/Components/PopupSwipeBackLayout;", "existingItems", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/plugins/hooks/MenuItemRecord;", "menuType", _UrlKt.FRAGMENT_ENCODE_SET, "contextData", _UrlKt.FRAGMENT_ENCODE_SET, "resourcesProvider", "Lorg/telegram/ui/ActionBar/Theme$ResourcesProvider;", "<init>", "(Lorg/telegram/ui/Components/PopupSwipeBackLayout;Ljava/util/List;Ljava/lang/String;Ljava/util/Map;Lorg/telegram/ui/ActionBar/Theme$ResourcesProvider;)V", TeXSymbolParser.TYPE_ATTR, "(Lorg/telegram/ui/Components/PopupSwipeBackLayout;Ljava/lang/String;Ljava/util/Map;Lorg/telegram/ui/ActionBar/Theme$ResourcesProvider;)V", "menuItemsContainer", "Landroid/widget/LinearLayout;", "getMenuItemsContainer", "()Landroid/widget/LinearLayout;", "swipeBack", "getSwipeBack", "rebuildMenu", _UrlKt.FRAGMENT_ENCODE_SET, "createScrollView", "Landroid/widget/ScrollView;", "context", "Landroid/content/Context;", "createGap", "Landroid/view/View;", "closeMenu", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public class PluginsMenuWrapper {
    public static final int GAP_ITEM_HEIGHT = 8;
    public static final int ITEM_HEIGHT = 48;
    public static final int SUBTITLE_ITEM_HEIGHT = 56;
    private final Map<String, Object> contextData;
    private final LinearLayout menuItemsContainer;
    private final String menuType;
    private final Theme.ResourcesProvider resourcesProvider;
    private final LinearLayout swipeBack;

    public void closeMenu() {
    }

    public PluginsMenuWrapper(final PopupSwipeBackLayout popupSwipeBackLayout, List<MenuItemRecord> list, String str, Map<String, ? extends Object> map, Theme.ResourcesProvider resourcesProvider) {
        "swipeBackLayout";
        "menuType";
        "contextData";
        this.menuType = str;
        this.contextData = map;
        this.resourcesProvider = resourcesProvider;
        Context context = popupSwipeBackLayout.getContext();
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(1);
        this.swipeBack = linearLayout;
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(context, true, false, resourcesProvider);
        actionBarMenuSubItem.setItemHeight(44);
        actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(R.string.Back), R.drawable.msg_arrow_back);
        actionBarMenuSubItem.getTextView().setPadding(LocaleController.isRTL ? 0 : AndroidUtilities.dp(40.0f), 0, LocaleController.isRTL ? AndroidUtilities.dp(40.0f) : 0, 0);
        actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                popupSwipeBackLayout.closeForeground();
            }
        });
        linearLayout.addView(actionBarMenuSubItem, LayoutHelper.createLinear(-1, -2));
        LinearLayout linearLayout2 = new LinearLayout(context);
        linearLayout2.setOrientation(1);
        this.menuItemsContainer = linearLayout2;
        ScrollView scrollViewCreateScrollView = createScrollView(context);
        scrollViewCreateScrollView.addView(linearLayout2);
        linearLayout.addView(scrollViewCreateScrollView);
        rebuildMenu(list);
    }

    public final LinearLayout getMenuItemsContainer() {
        return this.menuItemsContainer;
    }

    public final LinearLayout getSwipeBack() {
        return this.swipeBack;
    }

    Context $context;
            private final AnimatedFloat alphaFloat;
            private Drawable topShadowDrawable;
            private boolean wasCanScrollVertically;

            {
                super(context);
                this.$context = context;
                this.alphaFloat = new AnimatedFloat(this, 350L, CubicBezierInterpolator.EASE_OUT_QUINT);
            }

            @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.ViewParent
            public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
                "target";
                super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
                boolean zCanScrollVertically = canScrollVertically(-1);
                if (this.wasCanScrollVertically != zCanScrollVertically) {
                    invalidate();
                    this.wasCanScrollVertically = zCanScrollVertically;
                }
            }

            @Override // android.view.ViewGroup, android.view.View
            public void dispatchDraw(Canvas canvas) {
                "canvas";
                super.dispatchDraw(canvas);
                float f = this.alphaFloat.set(canScrollVertically(-1) ? 1.0f : 0.0f) * 0.5f;
                if (f <= 0.0f) {
                    return;
                }
                if (this.topShadowDrawable == null) {
                    this.topShadowDrawable = ContextCompat.getDrawable(this.$context, R.drawable.header_shadow);
                }
                Drawable drawable = this.topShadowDrawable;
                if (drawable != null) {
                    drawable.setBounds(0, getScrollY(), getWidth(), getScrollY() + drawable.getIntrinsicHeight());
                    drawable.setAlpha((int) (255.0f * f));
                    drawable.draw(canvas);
                }
            }
        };
    }

    private final View createGap() {
        ActionBarPopupWindow.GapView gapView = new ActionBarPopupWindow.GapView(this.menuItemsContainer.getContext(), this.resourcesProvider);
        gapView.setDividerVisible(false);
        return gapView;
    }
}
