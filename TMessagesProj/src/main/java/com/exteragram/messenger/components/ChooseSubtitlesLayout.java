package com.exteragram.messenger.components;

import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.Components.PopupSwipeBackLayout;

public class ChooseSubtitlesLayout {
    private final ActionBarMenuSubItem disableItem;
    public final ActionBarPopupWindow.ActionBarPopupWindowLayout layout;

    public interface Callback {
        void onChooseSubtitles();

        void onDisableSubtitles();
    }

    public ChooseSubtitlesLayout(Context context, final PopupSwipeBackLayout popupSwipeBackLayout, final Callback callback) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, 0, null);
        this.layout = actionBarPopupWindowLayout;
        actionBarPopupWindowLayout.setFitItems(true);
        ActionBarMenuSubItem actionBarMenuSubItemAddItem = ActionBarMenuItem.addItem(actionBarPopupWindowLayout, R.drawable.msg_arrow_back, LocaleController.getString(R.string.Back), false, null);
        actionBarMenuSubItemAddItem.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                popupSwipeBackLayout.closeForeground();
            }
        });
        actionBarMenuSubItemAddItem.setColors(-328966, -328966);
        actionBarMenuSubItemAddItem.setSelectorColor(268435455);
        View frameLayout = new FrameLayout(context);
        frameLayout.setMinimumWidth(AndroidUtilities.dp(196.0f));
        frameLayout.setBackgroundColor(-15198184);
        actionBarPopupWindowLayout.addView(frameLayout);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) frameLayout.getLayoutParams();
        layoutParams.gravity = LocaleController.isRTL ? 5 : 3;
        layoutParams.width = -1;
        layoutParams.height = AndroidUtilities.dp(8.0f);
        frameLayout.setLayoutParams(layoutParams);
        ActionBarMenuSubItem actionBarMenuSubItemAddItem2 = ActionBarMenuItem.addItem(actionBarPopupWindowLayout, R.drawable.msg_folders, LocaleController.getString(R.string.ChooseSubtitles), false, null);
        actionBarMenuSubItemAddItem2.setColors(-328966, -328966);
        actionBarMenuSubItemAddItem2.setSelectorColor(268435455);
        actionBarMenuSubItemAddItem2.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                callback.onChooseSubtitles();
            }
        });
        ActionBarMenuSubItem actionBarMenuSubItemAddItem3 = ActionBarMenuItem.addItem(actionBarPopupWindowLayout, R.drawable.msg_cancel, LocaleController.getString(R.string.DisableSubtitles), false, null);
        this.disableItem = actionBarMenuSubItemAddItem3;
        actionBarMenuSubItemAddItem3.setColors(-328966, -328966);
        actionBarMenuSubItemAddItem3.setSelectorColor(268435455);
        actionBarMenuSubItemAddItem3.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                callback.onDisableSubtitles();
            }
        });
    }

    public void update(boolean z) {
        this.disableItem.setVisibility(z ? 0 : 8);
    }
}
