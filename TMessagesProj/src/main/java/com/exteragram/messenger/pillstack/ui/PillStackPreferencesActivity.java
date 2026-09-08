package com.exteragram.messenger.pillstack.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.exteragram.messenger.pillstack.core.PillRegistry;
import com.exteragram.messenger.pillstack.core.PillStackConfig;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

public class PillStackPreferencesActivity extends BasePreferencesActivity {
    private Drawable reorderIcon;
    private ActionBarMenuItem resetItem;
    private final HashMap<Integer, ItemInfo> itemDetails = new HashMap<>();
    private int activeSectionId = -1;
    private int hiddenSectionId = -1;

    public static class ItemInfo {
        int iconColorBottom;
        int iconColorTop;
        int iconRes;
        CharSequence name;

        public ItemInfo(CharSequence charSequence, int i, int i2, int i3) {
            this.name = charSequence;
            this.iconRes = i;
            this.iconColorTop = i2;
            this.iconColorBottom = i3;
        }
    }

    @Override 
    public void initializeOptionStrings() {
        initItemDetails();
    }

    private void initItemDetails() {
        for (PillRegistry.PillInfo pillInfo : PillRegistry.getRegisteredPills()) {
            this.itemDetails.put(Integer.valueOf(pillInfo.id()), new ItemInfo(pillInfo.name(), pillInfo.iconRes(), pillInfo.iconColorTop(), pillInfo.iconColorBottom()));
        }
    }

    @Override 
    public String getTitle() {
        return LocaleController.getString(R.string.PillStackPills);
    }

    @Override 
    public View createView(Context context) {
        View viewCreateView = super.createView(context);
        ActionBarMenuItem actionBarMenuItemAddItem = this.actionBar.createMenu().addItem(0, R.drawable.msg_reset);
        this.resetItem = actionBarMenuItemAddItem;
        actionBarMenuItemAddItem.setContentDescription(LocaleController.getString(R.string.Reset));
        updateResetButtonVisibility();
        this.resetItem.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$createView$0(view);
            }
        });
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.allowReorder(true);
            this.listView.listenReorder(new Utilities.Callback2() { 
                @Override 
                public final void run(Object obj, Object obj2) {
                    this.f$0.updateConfigFromReorder(((Integer) obj).intValue(), (ArrayList) obj2);
                }
            });
        }
        return viewCreateView;
    }

    public void $r8$lambda$ZNAThjqAeJc9LOjkEYIGUxiiUGM(ItemInfo itemInfo, View view) {
        if (view instanceof TextCell) {
            TextCell textCell = (TextCell) view;
            textCell.setColorfulIcon(itemInfo.iconColorTop, itemInfo.iconColorBottom, itemInfo.iconRes);
            textCell.setImageLeft(21);
            textCell.setOffsetFromImage(65);
        }
    }

    public void updateConfigFromReorder(int i, ArrayList<UItem> arrayList) {
        ArrayList arrayList2 = new ArrayList();
        int size = arrayList.size();
        int i2 = 0;
        while (i2 < size) {
            UItem uItem = arrayList.get(i2);
            i2++;
            arrayList2.add(Integer.valueOf(uItem.id));
        }
        if (i == this.activeSectionId) {
            PillStackConfig.getActivePills().clear();
            PillStackConfig.getActivePills().addAll(arrayList2);
        } else if (i == this.hiddenSectionId) {
            PillStackConfig.getHiddenPills().clear();
            PillStackConfig.getHiddenPills().addAll(arrayList2);
        }
        saveAndNotify();
    }

    @Override 
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        int i2 = uItem.id;
        if (i2 == 1000) {
            toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                @Override // com.google.android.exoplayer2.util.Consumer
                public final void accept(Object obj) {
                    PillStackConfig.setInfiniteScrolling(((Boolean) obj).booleanValue());
                }
            });
            return;
        }
        if (PillStackConfig.getActivePills().contains(Integer.valueOf(i2))) {
            PillStackConfig.getActivePills().remove(Integer.valueOf(i2));
            if (!PillStackConfig.getHiddenPills().contains(Integer.valueOf(i2))) {
                PillStackConfig.getHiddenPills().add(0, Integer.valueOf(i2));
            }
        } else if (PillStackConfig.getHiddenPills().contains(Integer.valueOf(i2))) {
            PillStackConfig.getHiddenPills().remove(Integer.valueOf(i2));
            PillStackConfig.getActivePills().add(Integer.valueOf(i2));
        }
        saveAndNotify();
    }

    private void saveAndNotify() {
        UniversalAdapter universalAdapter;
        PillStackConfig.savePillsLayout();
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pillStackLayoutChanged, new Object[0]);
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null && (universalAdapter = universalRecyclerView.adapter) != null) {
            universalAdapter.update(true);
        }
        updateResetButtonVisibility();
    }

    private void updateResetButtonVisibility() {
        if (this.resetItem == null) {
            return;
        }
        boolean zEquals = PillStackConfig.getActivePills().equals(PillStackConfig.getDefaultActivePills());
        if (!zEquals && this.resetItem.getVisibility() == 8) {
            AndroidUtilities.updateViewVisibilityAnimated(this.resetItem, true, 0.5f, true);
        } else if (zEquals && this.resetItem.getVisibility() == 0) {
            AndroidUtilities.updateViewVisibilityAnimated(this.resetItem, false, 0.5f, true);
        }
    }

    private void resetToDefault() {
        UniversalAdapter universalAdapter;
        PillStackConfig.getActivePills().clear();
        PillStackConfig.getActivePills().addAll(PillStackConfig.getDefaultActivePills());
        PillStackConfig.getHiddenPills().clear();
        for (PillRegistry.PillInfo pillInfo : PillRegistry.getRegisteredPills()) {
            if (!PillStackConfig.getActivePills().contains(Integer.valueOf(pillInfo.id()))) {
                PillStackConfig.getHiddenPills().add(Integer.valueOf(pillInfo.id()));
            }
        }
        PillStackConfig.savePillsLayout();
        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.pillStackLayoutChanged, new Object[0]);
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null && (universalAdapter = universalRecyclerView.adapter) != null) {
            universalAdapter.update(true);
        }
        updateResetButtonVisibility();
    }
}
