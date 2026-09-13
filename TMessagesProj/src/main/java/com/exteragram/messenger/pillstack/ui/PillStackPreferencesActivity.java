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

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void initializeOptionStrings() {
        initItemDetails();
    }

    private void initItemDetails() {
        for (PillRegistry.PillInfo pillInfo : PillRegistry.getRegisteredPills()) {
            this.itemDetails.put(Integer.valueOf(pillInfo.id()), new ItemInfo(pillInfo.name(), pillInfo.iconRes(), pillInfo.iconColorTop(), pillInfo.iconColorBottom()));
        }
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public String getTitle() {
        return LocaleController.getString(R.string.PillStackPills);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity, org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        View viewCreateView = super.createView(context);
        ActionBarMenuItem actionBarMenuItemAddItem = this.actionBar.createMenu().addItem(0, R.drawable.msg_reset);
        this.resetItem = actionBarMenuItemAddItem;
        actionBarMenuItemAddItem.setContentDescription(LocaleController.getString(R.string.Reset));
        updateResetButtonVisibility();
        this.resetItem.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.pillstack.ui.PillStackPreferencesActivity$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                PillStackPreferencesActivity.this.lambda$createView$0(view);
            }
        });
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.allowReorder(true);
            this.listView.listenReorder(new Utilities.Callback2() { // from class: com.exteragram.messenger.pillstack.ui.PillStackPreferencesActivity$$ExternalSyntheticLambda1
                @Override // org.telegram.messenger.Utilities.Callback2
                public final void run(Object obj, Object obj2) {
                    PillStackPreferencesActivity.this.updateConfigFromReorder(((Integer) obj).intValue(), (ArrayList) obj2);
                }
            });
        }
        return viewCreateView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$0(View view) {
        resetToDefault();
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        if (this.reorderIcon == null) {
            this.reorderIcon = ContextCompat.getDrawable(getContext(), R.drawable.list_reorder);
        }
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.Settings)));
        arrayList.add(UItem.asCheck(1000, LocaleController.getString(R.string.PillStackInfiniteScrolling)).setSearchable(this).setLinkAlias("pillStackInfiniteScrolling", this).setChecked(PillStackConfig.getInfiniteScrolling()));
        arrayList.add(UItem.asShadow(null));
        this.activeSectionId = -1;
        this.hiddenSectionId = -1;
        if (!PillStackConfig.getActivePills().isEmpty()) {
            this.activeSectionId = addMenuSection(arrayList, universalAdapter, LocaleController.getString(R.string.PillStackActivePills), PillStackConfig.getActivePills());
            arrayList.add(UItem.asShadow(LocaleController.getString(R.string.PillStackPillsSettingsInfo)));
        }
        if (PillStackConfig.getHiddenPills().isEmpty()) {
            return;
        }
        this.hiddenSectionId = addMenuSection(arrayList, universalAdapter, LocaleController.getString(R.string.PillStackHiddenPills), PillStackConfig.getHiddenPills());
        if (PillStackConfig.getActivePills().isEmpty()) {
            arrayList.add(UItem.asShadow(LocaleController.getString(R.string.PillStackPillsSettingsInfo)));
        }
    }

    private int addMenuSection(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter, String str, List<Integer> list) {
        universalAdapter.whiteSectionStart();
        arrayList.add(UItem.asHeader(str));
        int iReorderSectionStart = universalAdapter.reorderSectionStart();
        for (Integer num : list) {
            ItemInfo itemInfo = this.itemDetails.get(num);
            if (itemInfo != null) {
                arrayList.add(createMenuItem(num.intValue(), itemInfo));
            }
        }
        universalAdapter.reorderSectionEnd();
        universalAdapter.whiteSectionEnd();
        return iReorderSectionStart;
    }

    private UItem createMenuItem(int i, final ItemInfo itemInfo) {
        UItem uItemAsButton = UItem.asButton(i, itemInfo.iconRes, itemInfo.name);
        uItemAsButton.object2 = this.reorderIcon;
        uItemAsButton.bind = new Utilities.Callback() { // from class: com.exteragram.messenger.pillstack.ui.PillStackPreferencesActivity$$ExternalSyntheticLambda3
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                PillStackPreferencesActivity.$r8$lambda$ZNAThjqAeJc9LOjkEYIGUxiiUGM(itemInfo, (View) obj);
            }
        };
        return uItemAsButton;
    }

    public static /* synthetic */ void $r8$lambda$ZNAThjqAeJc9LOjkEYIGUxiiUGM(ItemInfo itemInfo, View view) {
        if (view instanceof TextCell) {
            TextCell textCell = (TextCell) view;
            textCell.setColorfulIcon(itemInfo.iconColorTop, itemInfo.iconColorBottom, itemInfo.iconRes);
            textCell.setImageLeft(21);
            textCell.setOffsetFromImage(65);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
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

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        int i2 = uItem.id;
        if (i2 == 1000) {
            toggleBooleanSettingAndRefresh(uItem, new Consumer() { // from class: com.exteragram.messenger.pillstack.ui.PillStackPreferencesActivity$$ExternalSyntheticLambda2
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
        NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.pillStackLayoutChanged, new Object[0]);
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
        NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.pillStackLayoutChanged, new Object[0]);
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null && (universalAdapter = universalRecyclerView.adapter) != null) {
            universalAdapter.update(true);
        }
        updateResetButtonVisibility();
    }
}
