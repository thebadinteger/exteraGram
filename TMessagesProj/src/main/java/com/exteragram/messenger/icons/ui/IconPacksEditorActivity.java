package com.exteragram.messenger.icons.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcelable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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

    private void updateAdapter() {
        if (this.listView != null && this.listView.adapter != null) {
            this.listView.adapter.update(true);
        }
    }

    private void updateFilterChecks() {
        for (int i = 0; i < this.filterItems.length; i++) {
            ActionBarMenuSubItem item = this.filterItems[i];
            if (item != null) {
                item.setChecked(this.iconFilter == i);
            }
        }
    }

    private void setIconFilter(int filter) {
        if (this.iconFilter == filter) {
            return;
        }
        this.iconFilter = filter;
        updateFilterChecks();
        updateAdapter();
    }

    public class AnonymousClass1 extends ActionBarMenuItem.ActionBarMenuItemSearchListener {
        public AnonymousClass1() {
        }

        @Override
        public void onSearchExpand() {
            IconPacksEditorActivity.this.searching = true;
            if (IconPacksEditorActivity.this.otherItem != null) {
                IconPacksEditorActivity.this.otherItem.setVisibility(8);
            }
        }

        @Override
        public void onSearchCollapse() {
            IconPacksEditorActivity.this.searching = false;
            IconPacksEditorActivity.this.query = null;
            if (IconPacksEditorActivity.this.otherItem != null) {
                IconPacksEditorActivity.this.otherItem.setVisibility(0);
            }
            IconPacksEditorActivity.this.updateAdapter();
        }

        @Override
        public void onTextChanged(final EditText editText) {
            if (IconPacksEditorActivity.this.searchRunnable != null) {
                AndroidUtilities.cancelRunOnUIThread(IconPacksEditorActivity.this.searchRunnable);
            }
            IconPacksEditorActivity.this.searchRunnable = () -> {
                IconPacksEditorActivity.this.query = editText.getText().toString();
                IconPacksEditorActivity.this.updateAdapter();
            };
            AndroidUtilities.runOnUIThread(IconPacksEditorActivity.this.searchRunnable, 200L);
        }
    }

    private ActionBarPopupWindow.ActionBarPopupWindowLayout createFilterLayout(Context context) {
        ActionBarPopupWindow.ActionBarPopupWindowLayout popupLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(context, 0, getResourceProvider());
        popupLayout.setFitItems(true);
        ActionBarMenuSubItem backItem = ActionBarMenuItem.addItem(popupLayout, R.drawable.msg_arrow_back, LocaleController.getString(R.string.Back), false, getResourceProvider());
        backItem.setOnClickListener(v -> {
            if (otherItem != null && otherItem.getPopupLayout() != null && otherItem.getPopupLayout().getSwipeBack() != null) {
                otherItem.getPopupLayout().getSwipeBack().closeForeground();
            }
        });
        View gap = ActionBarMenuItem.addGap(0, popupLayout);
        LinearLayout.LayoutParams params = LayoutHelper.createLinear(-1, 8);
        gap.setLayoutParams(params);
        gap.setBackgroundColor(Theme.getColor(Theme.key_actionBarDefaultSubmenuSeparator));
        createFilterItem(popupLayout, LocaleController.getString(R.string.IconPickerAllIcons), 0);
        createFilterItem(popupLayout, LocaleController.getString(R.string.IconPickerReplacedIcons), 1);
        createFilterItem(popupLayout, LocaleController.getString(R.string.IconPickerNotReplacedIcons), 2);
        updateFilterChecks();
        return popupLayout;
    }

    private void createFilterItem(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, String str, final int i) {
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(actionBarPopupWindowLayout.getContext(), true, false, false, getResourceProvider());
        actionBarMenuSubItem.setTextAndIcon(str, 0);
        actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(196.0f));
        actionBarPopupWindowLayout.addView((View) actionBarMenuSubItem, LayoutHelper.createLinear(-1, 48));
        actionBarMenuSubItem.setOnClickListener(view -> setIconFilter(i));
        this.filterItems[i] = actionBarMenuSubItem;
    }

    private void loadIconsAsync() {
        if ((isIconsLoaded && !cachedIconItems.isEmpty()) || isLoading) {
            return;
        }
        isLoading = true;
        Utilities.globalQueue.postRunnable(() -> {
            final ArrayList<UItem> arrayList = new ArrayList<>(1500);
            Map<String, Integer> map = IconManager.INSTANCE.getSystemIcons();
            if (map.isEmpty()) {
                AndroidUtilities.runOnUIThread(() -> {
                    isLoading = false;
                    IconManager.INSTANCE.initialize(true);
                });
                return;
            }
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                arrayList.add(EditorIconCell.Factory.asIcon(entry.getValue(), entry.getKey(), null));
            }
            Collections.sort(arrayList, Comparator.comparing(uItem -> uItem.text.toString()));
            AndroidUtilities.runOnUIThread(() -> {
                cachedIconItems.clear();
                cachedIconItems.addAll(arrayList);
                isIconsLoaded = true;
                isLoading = false;
                updateAdapter();
            });
        });
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

        public void reset() {
            getTextView().setText("");
            if (getImageView() != null) {
                getImageView().setVisibility(GONE);
                getImageView().setImageDrawable(null);
            }
            if (getValueImageView() != null) {
                getValueImageView().setVisibility(GONE);
                getValueImageView().setImageDrawable(null);
            }
            if (getValueTextView() != null) {
                getValueTextView().setVisibility(GONE);
                getValueTextView().setText("", false);
            }
        }

        public void setTextAndIconAndValueDrawable(CharSequence text, Drawable icon, Drawable valueDrawable, boolean divider) {
            setOffsetFromImage(71);
            setImageLeft(21);
            getTextView().setText(text);
            getTextView().setRightDrawable(null);
            if (getValueTextView() != null) {
                getValueTextView().setText(null, false);
                getValueTextView().setVisibility(GONE);
            }
            if (getImageView() != null) {
                getImageView().setColorFilter(null);
                if (icon instanceof org.telegram.ui.Components.RLottieDrawable) {
                    getImageView().setAnimation((org.telegram.ui.Components.RLottieDrawable) icon);
                } else {
                    getImageView().setImageDrawable(icon);
                }
                getImageView().setVisibility(VISIBLE);
                getImageView().setPadding(0, AndroidUtilities.dp(6), 0, 0);
            }
            if (getValueImageView() != null) {
                getValueImageView().setVisibility(VISIBLE);
                getValueImageView().setImageDrawable(valueDrawable);
            }
            setWillNotDraw(!divider);
        }

        public void setIsIcon(boolean isIcon) {
            if (getImageView() != null) {
                getImageView().setScaleType(isIcon ? android.widget.ImageView.ScaleType.FIT_CENTER : android.widget.ImageView.ScaleType.CENTER);
            }
            if (getValueImageView() != null) {
                getValueImageView().setScaleType(isIcon ? android.widget.ImageView.ScaleType.FIT_CENTER : android.widget.ImageView.ScaleType.CENTER);
            }
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


