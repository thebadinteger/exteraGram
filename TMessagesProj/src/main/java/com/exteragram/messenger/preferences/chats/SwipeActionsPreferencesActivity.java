package com.exteragram.messenger.preferences.chats;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.exteragram.messenger.preferences.chats.components.SwipeActionsCell;
import com.exteragram.messenger.utils.chats.SwipeAction;
import java.util.ArrayList;
import java.util.List;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

/* JADX INFO: loaded from: classes4.dex */
public class SwipeActionsPreferencesActivity extends BasePreferencesActivity {
    private SwipeActionsCell previewCell;
    private Drawable reorderIcon;

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public String getTitle() {
        return LocaleController.getString(R.string.SwipeActions);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity, org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.previewCell = new SwipeActionsCell(context);
        View viewCreateView = super.createView(context);
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.allowReorder(true);
            this.listView.listenReorder(new Utilities.Callback2() { // from class: com.exteragram.messenger.preferences.chats.SwipeActionsPreferencesActivity$$ExternalSyntheticLambda0
                @Override // org.telegram.messenger.Utilities.Callback2
                public final void run(Object obj, Object obj2) {
                    SwipeActionsPreferencesActivity.this.onReorder(((Integer) obj).intValue(), (ArrayList) obj2);
                }
            });
        }
        return viewCreateView;
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        if (this.reorderIcon == null) {
            this.reorderIcon = ContextCompat.getDrawable(getContext(), R.drawable.list_reorder);
        }
        arrayList.add(UItem.asCustom(1, this.previewCell));
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.SwipeActionsInfo)));
        universalAdapter.whiteSectionStart();
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.SwipeActionsBehavior)));
        arrayList.add(UItem.asCheck(2, LocaleController.getString(R.string.SwipeActionsLoop)).setChecked(ExteraConfig.getSwipeActionsLoop()).setSearchable(this).setLinkAlias("swipeActionsLoop", this));
        arrayList.add(UItem.asCheck(3, LocaleController.getString(R.string.SwipeActionsReversed)).setChecked(ExteraConfig.getSwipeActionsReversed()).setSearchable(this).setLinkAlias("swipeActionsReversed", this));
        universalAdapter.whiteSectionEnd();
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.SwipeActionsBehaviorInfo)));
        addSection(arrayList, universalAdapter, LocaleController.getString(R.string.SwipeActionsEnabled), SwipeAction.enabled());
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.SwipeActionsOrderInfo)));
        List<SwipeAction> listDisabled = SwipeAction.disabled();
        if (listDisabled.isEmpty()) {
            return;
        }
        addSection(arrayList, universalAdapter, LocaleController.getString(R.string.SwipeActionsDisabled), listDisabled);
        arrayList.add(UItem.asShadow(null));
    }

    private void addSection(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter, String str, List<SwipeAction> list) {
        universalAdapter.whiteSectionStart();
        arrayList.add(UItem.asHeader(str));
        universalAdapter.reorderSectionStart();
        for (SwipeAction swipeAction : list) {
            UItem uItemAsButton = UItem.asButton(swipeAction.ordinal() + 100, swipeAction.iconRes, LocaleController.getString(swipeAction.titleRes));
            uItemAsButton.object2 = this.reorderIcon;
            arrayList.add(uItemAsButton);
        }
        universalAdapter.reorderSectionEnd();
        universalAdapter.whiteSectionEnd();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onReorder(int i, ArrayList<UItem> arrayList) {
        if (i != 0) {
            return;
        }
        ArrayList arrayList2 = new ArrayList();
        int size = arrayList.size();
        int i2 = 0;
        while (i2 < size) {
            UItem uItem = arrayList.get(i2);
            i2++;
            SwipeAction swipeActionFromId = fromId(uItem.id);
            if (swipeActionFromId != null) {
                arrayList2.add(swipeActionFromId);
            }
        }
        SwipeAction.setEnabled(arrayList2);
        refresh();
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        int i2 = uItem.id;
        if (i2 == 2) {
            toggleBooleanSettingAndRefresh(uItem, bool -> ExteraConfig.setSwipeActionsLoop(bool.booleanValue()));
            this.previewCell.updateActions();
        } else {
            if (i2 == 3) {
                toggleBooleanSettingAndRefresh(uItem, bool -> ExteraConfig.setSwipeActionsReversed(bool.booleanValue()));
                return;
            }
            SwipeAction swipeActionFromId = fromId(i2);
            if (swipeActionFromId == null) {
                return;
            }
            swipeActionFromId.setEnabled(!swipeActionFromId.isEnabled());
            refresh();
        }
    }

    private void refresh() {
        UniversalAdapter universalAdapter;
        SwipeActionsCell swipeActionsCell = this.previewCell;
        if (swipeActionsCell != null) {
            swipeActionsCell.updateActions();
        }
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || (universalAdapter = universalRecyclerView.adapter) == null) {
            return;
        }
        universalAdapter.update(true);
    }

    private SwipeAction fromId(int i) {
        int i2 = i - 100;
        if (i2 < 0 || i2 >= SwipeAction.values().length) {
            return null;
        }
        return SwipeAction.values()[i2];
    }
}
