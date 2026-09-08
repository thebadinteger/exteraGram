package com.exteragram.messenger.icons.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.icons.ExteraResources;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.icons.IconPack;
import com.exteragram.messenger.icons.ui.components.NewIconPackBottomSheet;
import com.exteragram.messenger.icons.ui.picker.IconPickerController;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;
import org.telegram.ui.LaunchActivity;

public class IconPacksEditorActivity extends BasePreferencesActivity implements NotificationCenter.NotificationCenterDelegate {
    private static final ArrayList<UItem> cachedIconItems = new ArrayList<>();
    private static boolean isIconsLoaded = false;
    private static boolean isLoading = false;
    private final ActionBarMenuSubItem[] filterItems = new ActionBarMenuSubItem[3];
    private int iconFilter = 0;
    private IconPack iconPack;
    private ActionBarMenuItem otherItem;
    private String query;
    private Runnable searchRunnable;
    private boolean searching;

    public IconPacksEditorActivity(IconPack iconPack) {
        this.iconPack = iconPack;
    }

    @Override 
    public View createView(Context context) {
        super.createView(context);
        ActionBarMenu actionBarMenuCreateMenu = this.actionBar.createMenu();
        actionBarMenuCreateMenu.addItem(0, R.drawable.outline_header_search).setIsSearchField(true).setActionBarMenuItemSearchListener(new AnonymousClass1());
        ActionBarMenuItem actionBarMenuItemAddItem = actionBarMenuCreateMenu.addItem(3, R.drawable.ic_ab_other);
        this.otherItem = actionBarMenuItemAddItem;
        actionBarMenuItemAddItem.addSwipeBackItem(R.drawable.msg_select, null, LocaleController.getString(R.string.IconPickerFilter), createFilterLayout(context));
        this.otherItem.addSubItem(1, R.drawable.msg_edit, LocaleController.getString(R.string.Edit));
        if (ExteraConfig.getEditingIconPackId() != null) {
            ActionBarMenuSubItem actionBarMenuSubItemAddSubItem = this.otherItem.addSubItem(2, R.drawable.ic_ab_done, LocaleController.getString(R.string.IconPickerSaveAndExit));
            int i = Theme.key_featuredStickers_addButtonPressed;
            actionBarMenuSubItemAddSubItem.setColors(getThemedColor(i), getThemedColor(i));
        }
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { 
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    IconPacksEditorActivity.this.finishFragment();
                    return;
                }
                if (i2 == 2) {
                    ExteraConfig.setEditingIconPackId(null);
                    if (IconPacksEditorActivity.this.getParentActivity() instanceof LaunchActivity) {
                        IconPickerController.setActive((LaunchActivity) IconPacksEditorActivity.this.getParentActivity(), false);
                    }
                    IconPacksEditorActivity.this.finishFragment();
                    return;
                }
                if (i2 == 1) {
                    IconPacksEditorActivity iconPacksEditorActivity = IconPacksEditorActivity.this;
                    new NewIconPackBottomSheet(iconPacksEditorActivity, iconPacksEditorActivity.getContext(), IconPacksEditorActivity.this.iconPack).show();
                }
            }
        });
        return this.fragmentView;
    }

    public class AnonymousClass1 extends ActionBarMenuItem.ActionBarMenuItemSearchListener {
        public AnonymousClass1() {
        }

        @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
        public void onSearchExpand() {
            IconPacksEditorActivity.this.searching = true;
            if (IconPacksEditorActivity.this.otherItem != null) {
                IconPacksEditorActivity.this.otherItem.setVisibility(8);
            }
        }

        @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
        public void onSearchCollapse() {
            IconPacksEditorActivity.this.searching = false;
            IconPacksEditorActivity.this.query = null;
            if (IconPacksEditorActivity.this.otherItem != null) {
                IconPacksEditorActivity.this.otherItem.setVisibility(0);
            }
            IconPacksEditorActivity.this.updateAdapter();
        }

        @Override // org.telegram.ui.ActionBar.ActionBarMenuItem.ActionBarMenuItemSearchListener
        public void onTextChanged(final EditText editText) {
            if (IconPacksEditorActivity.this.searchRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(IconPacksEditorActivity.this.searchRunnable);
            }
            IconPacksEditorActivity.this.searchRunnable = new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onTextChanged$0(editText);
                }
            };
            AndroidUtilities.runOnUIThread(IconPacksEditorActivity.this.searchRunnable, 200L);
        }

        public void lambda$createFilterLayout$0(View view) {
        ActionBarMenuItem actionBarMenuItem = this.otherItem;
        if (actionBarMenuItem == null || actionBarMenuItem.getPopupLayout() == null || this.otherItem.getPopupLayout().getSwipeBack() == null) {
            return;
        }
        this.otherItem.getPopupLayout().getSwipeBack().closeForeground();
    }

    private void createFilterItem(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, String str, final int i) {
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(actionBarPopupWindowLayout.getContext(), true, false, false, getResourceProvider());
        actionBarMenuSubItem.setTextAndIcon(str, 0);
        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarPopupWindowLayout.addView((View) actionBarMenuSubItem, LayoutHelper.createLinear(-1, 48));
        actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$createFilterItem$1(i, view);
            }
        });
        this.filterItems[i] = actionBarMenuSubItem;
    }

    public void lambda$loadIconsAsync$5() {
        final ArrayList arrayList = new ArrayList(1500);
        HashMap map = new HashMap(IconManager.INSTANCE.getSystemIcons());
        if (map.isEmpty()) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    IconPacksEditorActivity.$r8$lambda$3Gala6xuNPg53I63ZSI5bu0JAkw();
                }
            });
            return;
        }
        for (Map.Entry entry : map.entrySet()) {
            arrayList.add(EditorIconCell.Factory.asIcon(((Integer) entry.getValue()).intValue(), (CharSequence) entry.getKey(), null));
        }
        Collections.sort(arrayList, Comparator.comparing(new Function() { 
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return ((UItem) obj).text.toString();
            }
        }));
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadIconsAsync$4(arrayList);
            }
        });
    }

    public static /* synthetic */ void $r8$lambda$3Gala6xuNPg53I63ZSI5bu0JAkw() {
        isLoading = false;
        IconManager.INSTANCE.initialize(true);
    }

    public /* synthetic */ void lambda$loadIconsAsync$4(ArrayList arrayList) {
        ArrayList<UItem> arrayList2 = cachedIconItems;
        arrayList2.clear();
        arrayList2.addAll(arrayList);
        isIconsLoaded = true;
        isLoading = false;
        updateAdapter();
    }

    @Override 
    public String getTitle() {
        IconPack iconPack = this.iconPack;
        return iconPack == null ? LocaleController.getString(R.string.NewIconPack) : iconPack.getName();
    }

    @Override 
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        IconPack iconPack;
        if (getContext() != null && isIconsLoaded) {
            ArrayList<UItem> arrayList2 = cachedIconItems;
            if (this.searching && !TextUtils.isEmpty(this.query)) {
                String lowerCase = this.query.toLowerCase();
                ArrayList<UItem> arrayList3 = new ArrayList<>();
                int i = 0;
                while (true) {
                    ArrayList<UItem> arrayList4 = cachedIconItems;
                    if (i >= arrayList4.size()) {
                        break;
                    }
                    UItem uItem = arrayList4.get(i);
                    CharSequence charSequence = uItem.text;
                    if (charSequence != null && charSequence.toString().toLowerCase().contains(lowerCase)) {
                        arrayList3.add(uItem);
                    }
                    i++;
                }
                arrayList2 = arrayList3;
            }
            for (int i2 = 0; i2 < arrayList2.size(); i2++) {
                UItem uItem2 = arrayList2.get(i2);
                CharSequence charSequence2 = uItem2.text;
                String string = charSequence2 == null ? null : charSequence2.toString();
                boolean z = (string == null || (iconPack = this.iconPack) == null || !iconPack.getIcons().containsKey(string)) ? false : true;
                int i3 = this.iconFilter;
                if ((i3 != 1 || z) && (i3 != 2 || !z)) {
                    arrayList.add(EditorIconCell.Factory.asIcon(uItem2.id, uItem2.text, this.iconPack));
                }
            }
        }
    }

    @Override 
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        IconManager.INSTANCE.showReplaceAlert(getContext(), uItem.id, this.iconPack);
    }

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        UniversalRecyclerView universalRecyclerView;
        if (i != NotificationCenter.iconPackUpdated || getContext() == null) {
            return;
        }
        if (!isIconsLoaded) {
            loadIconsAsync();
        }
        IconPack iconPack = this.iconPack;
        if (iconPack != null) {
            IconPack iconPackFindPackById = IconManager.INSTANCE.findPackById(iconPack.getId());
            if (iconPackFindPackById != null) {
                this.iconPack = iconPackFindPackById;
                this.actionBar.setTitle(getTitle());
            } else {
                finishFragment();
                return;
            }
        }
        UniversalRecyclerView universalRecyclerView2 = this.listView;
        Parcelable parcelableOnSaveInstanceState = (universalRecyclerView2 == null || universalRecyclerView2.getLayoutManager() == null) ? null : this.listView.getLayoutManager().onSaveInstanceState();
        updateAdapter();
        if (parcelableOnSaveInstanceState == null || (universalRecyclerView = this.listView) == null || universalRecyclerView.getLayoutManager() == null) {
            return;
        }
        this.listView.getLayoutManager().onRestoreInstanceState(parcelableOnSaveInstanceState);
    }

    @SuppressLint({"ViewConstructor"})
    public static class EditorIconCell extends TextCell {
        public EditorIconCell(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context, resourcesProvider);
        }

        public static class Factory extends UItem.UItemFactory<EditorIconCell> {
            static {
                UItem.UItemFactory.setup(new Factory());
            }

            @Override // org.telegram.ui.Components.UItem.UItemFactory
            public EditorIconCell createView(Context context, RecyclerListView recyclerListView, int i, int i2, Theme.ResourcesProvider resourcesProvider) {
                return new EditorIconCell(context, resourcesProvider);
            }

            @Override // org.telegram.ui.Components.UItem.UItemFactory
            public boolean equals(UItem uItem, UItem uItem2) {
                return uItem.id == uItem2.id;
            }

            @Override // org.telegram.ui.Components.UItem.UItemFactory
            public boolean contentsEquals(UItem uItem, UItem uItem2) {
                if (uItem.id != uItem2.id || !TextUtils.equals(uItem.text, uItem2.text)) {
                    return false;
                }
                Object obj = uItem.object;
                IconPack iconPack = obj instanceof IconPack ? (IconPack) obj : null;
                Object obj2 = uItem2.object;
                IconPack iconPack2 = obj2 instanceof IconPack ? (IconPack) obj2 : null;
                if (iconPack == iconPack2) {
                    return true;
                }
                if (iconPack == null || iconPack2 == null) {
                    return false;
                }
                String string = uItem.text.toString();
                return Objects.equals(iconPack.getIcons().get(string), iconPack2.getIcons().get(string));
            }

            @Override // org.telegram.ui.Components.UItem.UItemFactory
            public void bindView(View view, UItem uItem, boolean z, UniversalAdapter universalAdapter, UniversalRecyclerView universalRecyclerView) {
                Drawable drawable;
                Drawable drawable2;
                EditorIconCell editorIconCell = (EditorIconCell) view;
                editorIconCell.reset();
                if (editorIconCell.getContext().getResources() instanceof ExteraResources) {
                    drawable = ((ExteraResources) editorIconCell.getContext().getResources()).getOriginalDrawable(uItem.id);
                } else {
                    drawable = editorIconCell.getContext().getResources().getDrawable(uItem.id);
                }
                Object obj = uItem.object;
                if (obj instanceof IconPack) {
                    drawable2 = IconManager.INSTANCE.getPackIconDrawable((IconPack) obj, uItem.id);
                } else {
                    drawable2 = IconManager.INSTANCE.getDrawable(uItem.id, AndroidUtilities.displayMetrics.densityDpi, null);
                }
                if (drawable != null) {
                    drawable = drawable.mutate();
                }
                editorIconCell.setTextAndIconAndValueDrawable(uItem.text, drawable, drawable2, z);
                editorIconCell.setIsIcon(true);
                editorIconCell.setColors(Theme.key_windowBackgroundWhiteGrayIcon, Theme.key_windowBackgroundWhiteBlackText);
                editorIconCell.setOffsetFromImage(68);
            }

            public static UItem asIcon(int i, CharSequence charSequence, Object obj) {
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.id = i;
                uItemOfFactory.text = charSequence;
                uItemOfFactory.object = obj;
                return uItemOfFactory;
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onBackPressed(boolean z) {
        if (!this.searching) {
            return super.onBackPressed(z);
        }
        if (!z) {
            return false;
        }
        this.actionBar.closeSearchField();
        return false;
    }
}
