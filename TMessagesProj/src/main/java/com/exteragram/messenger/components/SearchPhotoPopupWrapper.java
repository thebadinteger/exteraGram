package com.exteragram.messenger.components;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.LinkSpanDrawable;
import org.telegram.ui.Components.PopupSwipeBackLayout;
import org.telegram.ui.Stories.DarkThemeResourceProvider;

public class SearchPhotoPopupWrapper {
    public ActionBarPopupWindow.ActionBarPopupWindowLayout searchSwipeBackLayout;

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
    public SearchPhotoPopupWrapper(Context context, final PopupSwipeBackLayout popupSwipeBackLayout, final Utilities.Callback<ReverseImageSearchSheet.Provider> callback) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, 0, null);
        this.searchSwipeBackLayout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setFitItems(true);
        ActionBarMenuSubItem actionBarMenuSubItemAddItem = ActionBarMenuItem.addItem(this.searchSwipeBackLayout, R.drawable.msg_arrow_back, LocaleController.getString(R.string.Back), false, null);
        actionBarMenuSubItemAddItem.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                popupSwipeBackLayout.closeForeground();
            }
        });
        actionBarMenuSubItemAddItem.setColors(-328966, -328966);
        actionBarMenuSubItemAddItem.setSelectorColor(268435455);
        FrameLayout frameLayout = new FrameLayout(context) { 
            @Override // android.widget.FrameLayout, android.view.View
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
            }
        };
        frameLayout.setMinimumWidth(AndroidUtilities.dp(196.0f));
        frameLayout.setBackgroundColor(-15198184);
        this.searchSwipeBackLayout.addView(frameLayout);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        if (LocaleController.isRTL) {
            layoutParams.gravity = 5;
        }
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(8.0f);
        frameLayout.setLayoutParams(layoutParams);
        ActionBarMenuSubItem actionBarMenuSubItemAddItem2 = ActionBarMenuItem.addItem(this.searchSwipeBackLayout, 0, "Yandex", false, null);
        actionBarMenuSubItemAddItem2.setColors(-328966, -328966);
        actionBarMenuSubItemAddItem2.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                callback.run(ReverseImageSearchSheet.Provider.YANDEX);
            }
        });
        actionBarMenuSubItemAddItem2.setSelectorColor(268435455);
        ActionBarMenuSubItem actionBarMenuSubItemAddItem3 = ActionBarMenuItem.addItem(this.searchSwipeBackLayout, 0, "Google", false, null);
        actionBarMenuSubItemAddItem3.setColors(-328966, -328966);
        actionBarMenuSubItemAddItem3.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                callback.run(ReverseImageSearchSheet.Provider.GOOGLE);
            }
        });
        actionBarMenuSubItemAddItem3.setSelectorColor(268435455);
        ActionBarMenuSubItem actionBarMenuSubItemAddItem4 = ActionBarMenuItem.addItem(this.searchSwipeBackLayout, 0, "Bing", false, null);
        actionBarMenuSubItemAddItem4.setColors(-328966, -328966);
        actionBarMenuSubItemAddItem4.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                callback.run(ReverseImageSearchSheet.Provider.BING);
            }
        });
        actionBarMenuSubItemAddItem4.setSelectorColor(268435455);
        ActionBarMenuSubItem actionBarMenuSubItemAddItem5 = ActionBarMenuItem.addItem(this.searchSwipeBackLayout, 0, "TinEye", false, null);
        actionBarMenuSubItemAddItem5.setColors(-328966, -328966);
        actionBarMenuSubItemAddItem5.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                callback.run(ReverseImageSearchSheet.Provider.TINEYE);
            }
        });
        actionBarMenuSubItemAddItem5.setSelectorColor(268435455);
        FrameLayout frameLayout2 = new FrameLayout(context) { 
            @Override // android.widget.FrameLayout, android.view.View
            public void onMeasure(int i, int i2) {
                super.onMeasure(i, i2);
            }
        };
        frameLayout2.setMinimumWidth(AndroidUtilities.dp(196.0f));
        frameLayout2.setBackgroundColor(-15198184);
        this.searchSwipeBackLayout.addView(frameLayout2);
        frameLayout2.setLayoutParams(layoutParams);
        LinkSpanDrawable.LinksTextView linksTextView = new LinkSpanDrawable.LinksTextView(context);
        linksTextView.setTag(R.id.fit_width_tag, 1);
        linksTextView.setPadding(AndroidUtilities.dp(13.0f), 0, AndroidUtilities.dp(13.0f), AndroidUtilities.dp(8.0f));
        linksTextView.setTextSize(1, 12.0f);
        linksTextView.setTextColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuItem, new DarkThemeResourceProvider()));
        linksTextView.setMovementMethod(LinkMovementMethod.getInstance());
        linksTextView.setLinkTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteLinkText, new DarkThemeResourceProvider()));
        linksTextView.setText(LocaleController.getString(R.string.SearchPhotoInfo));
        this.searchSwipeBackLayout.addView((View) linksTextView, LayoutHelper.createLinear(-1, -2, 0.0f, 0, 0, 8, 0, 0));
    }
}
