package com.exteragram.messenger.preferences;

import android.view.View;
import com.exteragram.messenger.preferences.utils.SettingsRegistry;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import org.telegram.messenger.LocaleController;
import org.telegram.ui.Cells.TextCheckCell2;
import org.telegram.ui.Components.UItem;

public final class SwitchGroup {
    private final List<Child> children = new ArrayList();
    private boolean expanded;
    private final BasePreferencesActivity fragment;
    private final int id;
    private String linkAlias;
    private Runnable onChanged;
    private boolean searchable;
    private final CharSequence title;
    private final int titleRes;

    public interface Setter {
        void set(boolean z);
    }

    public static final class Child {
        private final BooleanSupplier getter;
        private final int id;
        private String newFeatureAlias;
        private final Setter setter;
        private final CharSequence text;
        private final int textRes;
        private final BooleanSupplier visible;

        private Child(int i, int i2, CharSequence charSequence, BooleanSupplier booleanSupplier, BooleanSupplier booleanSupplier2, Setter setter) {
            this.id = i;
            this.textRes = i2;
            this.text = charSequence;
            this.visible = booleanSupplier;
            this.getter = booleanSupplier2;
            this.setter = setter;
        }

        public boolean isVisible() {
            BooleanSupplier booleanSupplier = this.visible;
            return booleanSupplier == null || booleanSupplier.getAsBoolean();
        }

        public CharSequence text() {
            CharSequence string = this.text;
            if (string == null) {
                string = LocaleController.getString(this.textRes);
            }
            String str = this.newFeatureAlias;
            return (str == null || !SettingsRegistry.markAsNewFeature(str)) ? string : LocaleUtils.applyNewSpan(string);
        }
    }

    private SwitchGroup(BasePreferencesActivity basePreferencesActivity, int i, int i2, CharSequence charSequence) {
        this.fragment = basePreferencesActivity;
        this.id = i;
        this.titleRes = i2;
        this.title = charSequence;
    }

    public static SwitchGroup of(BasePreferencesActivity basePreferencesActivity, int i, int i2) {
        return new SwitchGroup(basePreferencesActivity, i, i2, null);
    }

    public static SwitchGroup of(BasePreferencesActivity basePreferencesActivity, int i, CharSequence charSequence) {
        return new SwitchGroup(basePreferencesActivity, i, 0, charSequence);
    }

    public SwitchGroup searchable() {
        this.searchable = true;
        return this;
    }

    public SwitchGroup linkAlias(String str) {
        this.linkAlias = str;
        return this;
    }

    public SwitchGroup onChanged(Runnable runnable) {
        this.onChanged = runnable;
        return this;
    }

    public SwitchGroup add(int i, int i2, BooleanSupplier booleanSupplier, Setter setter) {
        return add(new Child(i, i2, null, null, booleanSupplier, setter));
    }

    public SwitchGroup add(int i, CharSequence charSequence, BooleanSupplier booleanSupplier, Setter setter) {
        return add(new Child(i, 0, charSequence, null, booleanSupplier, setter));
    }

    public SwitchGroup addIf(BooleanSupplier booleanSupplier, int i, int i2, BooleanSupplier booleanSupplier2, Setter setter) {
        return add(new Child(i, i2, null, booleanSupplier, booleanSupplier2, setter));
    }

    public SwitchGroup markNew(String str) {
        List<Child> list = this.children;
        list.get(list.size() - 1).newFeatureAlias = str;
        return this;
    }

    private SwitchGroup add(Child child) {
        this.children.add(child);
        return this;
    }

    public void fill(ArrayList<UItem> arrayList) {
        int iCount = count(true);
        UItem collapsed = UItem.asExteraExpandableSwitch(this.id, title(), String.format(Locale.US, "%d/%d", Integer.valueOf(iCount), Integer.valueOf(count(false))), new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.onSwitchClick(view);
            }
        }).setChecked(iCount > 0).setCollapsed(true ^ this.expanded);
        if (this.searchable) {
            collapsed.setSearchable(this.fragment);
        }
        String str = this.linkAlias;
        if (str != null) {
            collapsed.setLinkAlias(str, this.fragment);
        }
        arrayList.add(collapsed);
        if (this.expanded) {
            for (Child child : this.children) {
                if (child.isVisible()) {
                    arrayList.add(UItem.asRoundCheckbox(child.id, child.text()).setChecked(child.getter.getAsBoolean()).pad());
                }
            }
        }
    }

    public void onClick(UItem uItem) {
        if (uItem.id == this.id) {
            boolean z = !this.expanded;
            this.expanded = z;
            uItem.setCollapsed(z);
            this.fragment.listView.adapter.update(true);
            return;
        }
        for (Child child : this.children) {
            if (child.id == uItem.id) {
                BasePreferencesActivity basePreferencesActivity = this.fragment;
                final Setter setter = child.setter;
                Objects.requireNonNull(setter);
                basePreferencesActivity.toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        setter.set(((Boolean) obj).booleanValue());
                    }
                });
                Runnable runnable = this.onChanged;
                if (runnable != null) {
                    runnable.run();
                    return;
                }
                return;
            }
        }
    }

    public void onSwitchClick(View view) {
        UItem uItemFindItemByItemId = this.fragment.listView.findItemByItemId(((TextCheckCell2) view).id);
        boolean z = !uItemFindItemByItemId.checked;
        for (Child child : this.children) {
            if (child.isVisible()) {
                child.setter.set(z);
            }
        }
        uItemFindItemByItemId.setChecked(z);
        this.fragment.listView.adapter.update(true);
        Runnable runnable = this.onChanged;
        if (runnable != null) {
            runnable.run();
        }
    }

    private CharSequence title() {
        CharSequence charSequence = this.title;
        return charSequence != null ? charSequence : LocaleController.getString(this.titleRes);
    }

    private int count(boolean z) {
        int i = 0;
        for (Child child : this.children) {
            if (child.isVisible() && (!z || child.getter.getAsBoolean())) {
                i++;
            }
        }
        return i;
    }
}
