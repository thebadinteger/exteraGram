package com.exteragram.messenger.plugins.ui.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.plugins.Plugin;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.plugins.models.EditTextSetting;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.EditTextCell;
import org.telegram.ui.Components.EditTextCaption;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

@Metadata(d1 = {"\u0000K\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004*\u0001\u0010\b\u0007\u0018\u0000 !2\u00020\u0001:\u0002 !B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\b\u0010\u0012\u001a\u00020\u0013H\u0002J\u0010\u0010\u0014\u001a\u00020\u00132\u0006\u0010\u0015\u001a\u00020\u0016H\u0014J\b\u0010\u0017\u001a\u00020\u0013H\u0014J\b\u0010\u0018\u001a\u00020\u0013H\u0002J\u0016\u0010\u0019\u001a\u00020\u00132\u0006\u0010\u000e\u001a\u00020\u000b2\u0006\u0010\u001a\u001a\u00020\rJ\u0010\u0010\u001b\u001a\u00020\u00132\u0006\u0010\u001c\u001a\u00020\u0016H\u0002J\u0014\u0010\u001d\u001a\u0004\u0018\u00010\u001e2\b\u0010\u001f\u001a\u0004\u0018\u00010\u000bH\u0002R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000e\u001a\u0004\u0018\u00010\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u00020\u0010X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0011¨\u0006\""}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/PluginEditTextCell;", "Lorg/telegram/ui/Cells/EditTextCell;", "context", "Landroid/content/Context;", "resourcesProvider", "Lorg/telegram/ui/ActionBar/Theme$ResourcesProvider;", "<init>", "(Landroid/content/Context;Lorg/telegram/ui/ActionBar/Theme$ResourcesProvider;)V", "pendingSaveRunnable", "Ljava/lang/Runnable;", "valueToSave", _UrlKt.FRAGMENT_ENCODE_SET, "currentSetting", "Lcom/exteragram/messenger/plugins/models/EditTextSetting;", "pluginId", "saveTextWatcher", "com/exteragram/messenger/plugins/ui/components/PluginEditTextCell$saveTextWatcher$1", "Lcom/exteragram/messenger/plugins/ui/components/PluginEditTextCell$saveTextWatcher$1;", "scheduleSave", _UrlKt.FRAGMENT_ENCODE_SET, "onFocusChanged", "focused", _UrlKt.FRAGMENT_ENCODE_SET, "onDetachedFromWindow", "flushPendingSave", "bind", "setting", "setWatchersEnabled", "enabled", "createInputFilter", "Landroid/text/InputFilter;", "maskRegex", "Factory", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SuppressLint({"ViewConstructor"})
@SourceDebugExtension({"SMAP\nPluginEditTextCell.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PluginEditTextCell.kt\ncom/exteragram/messenger/plugins/ui/components/PluginEditTextCell\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 ArraysJVM.kt\nkotlin/collections/ArraysKt__ArraysJVMKt\n*L\n1#1,257:1\n1#2:258\n37#3,2:259\n*S KotlinDebug\n*F\n+ 1 PluginEditTextCell.kt\ncom/exteragram/messenger/plugins/ui/components/PluginEditTextCell\n*L\n156#1:259,2\n*E\n"})
public final class PluginEditTextCell extends EditTextCell {
    private static final int SAVE_DEBOUNCE_MS = 750;
    private EditTextSetting currentSetting;
    private Runnable pendingSaveRunnable;
    private String pluginId;
    private final PluginEditTextCell$saveTextWatcher$1 saveTextWatcher;
    private String valueToSave;

    public final void bind(String pluginId, EditTextSetting setting) {
        boolean z;
        InputFilter[] inputFilterArr;
        EditTextSetting editTextSetting = this.currentSetting;
        if (editTextSetting == null) {
            z = false;
        } else {
            if (TextUtils.equals(editTextSetting != null ? editTextSetting.getKey() : null, setting.getKey())) {
                z = true;
            } else {
                z = false;
            }
        }
        if (!z) {
            flushPendingSave();
        }
        this.pluginId = pluginId;
        this.currentSetting = setting;
        setWatchersEnabled(false);
        setMultiline(setting.getMultiline());
        int i = (setting.getMultiline() ? 131072 : 0) | 573441;
        if (this.editText.getInputType() != i) {
            this.editText.setInputType(i);
        }
        if (setting.getMaxLength() > 0) {
            setShowLimitWhenNear(setting.getMaxLength() / Math.min(setting.getMaxLength(), 4));
        } else {
            setShowLimitWhenNear(-1);
        }
        ArrayList arrayList = new ArrayList();
        InputFilter inputFilterCreateInputFilter = createInputFilter(setting.getMask());
        if (inputFilterCreateInputFilter != null) {
            arrayList.add(inputFilterCreateInputFilter);
        }
        if (setting.getMaxLength() > 0) {
            arrayList.add(new InputFilter.LengthFilter(setting.getMaxLength()));
        }
        EditTextCaption editTextCaption = this.editText;
        if (!arrayList.isEmpty()) {
            inputFilterArr = (InputFilter[]) arrayList.toArray(new InputFilter[0]);
        } else {
            inputFilterArr = new InputFilter[0];
        }
        editTextCaption.setFilters(inputFilterArr);
        String pluginSettingString = PluginsController.INSTANCE.getInstance().getPluginSettingString(pluginId, setting.getKey(), setting.getDefaultValue());
        if (!TextUtils.equals(pluginSettingString, String.valueOf(this.editText.getText()))) {
            if (this.editText.hasFocus() && z) {
                setWatchersEnabled(true);
                return;
            }
            setText(pluginSettingString);
        }
        this.valueToSave = pluginSettingString;
        if (setting.getHint() != null) {
            this.editText.setHint(setting.getHint());
        }
        setWatchersEnabled(true);
    }

    private final void setWatchersEnabled(boolean enabled) {
        this.editText.removeTextChangedListener(this.saveTextWatcher);
        if (enabled) {
            this.editText.addTextChangedListener(this.saveTextWatcher);
        }
    }

    private final InputFilter createInputFilter(String maskRegex) {
        final Pattern patternCompile = null;
        if (TextUtils.isEmpty(maskRegex)) {
            return null;
        }
        if (maskRegex != null) {
            try {
                patternCompile = Pattern.compile(maskRegex);
            } catch (PatternSyntaxException e) {
                FileLog.e("Invalid mask for EditText: ".concat(maskRegex), e);
                return null;
            }
        }
        return new InputFilter() { 
            @Override // android.text.InputFilter
            public final CharSequence filter(CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
                return PluginEditTextCell.$r8$lambda$V7Y2RoWGJo4arI5W4MF0XsqMNDo(patternCompile, charSequence, i, i2, spanned, i3, i4);
            }
        };
    }

    public static CharSequence $r8$lambda$V7Y2RoWGJo4arI5W4MF0XsqMNDo(Pattern pattern, CharSequence charSequence, int i, int i2, Spanned spanned, int i3, int i4) {
        Matcher matcher;
        StringBuilder sb = new StringBuilder(i2 - i);
        boolean z = true;
        while (i < i2) {
            char cCharAt = charSequence.charAt(i);
            if ((pattern == null || (matcher = pattern.matcher(String.valueOf(cCharAt))) == null) ? false : matcher.matches()) {
                sb.append(cCharAt);
            } else {
                z = false;
            }
            i++;
        }
        if (z) {
            return null;
        }
        return sb.toString();
    }

    @Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 \u001b2\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u001bB\u0007¢\u0006\u0004\b\u0003\u0010\u0004J2\u0010\u0005\u001a\u00020\u00022\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\u000b2\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0016J0\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\b\u001a\u00020\u0019H\u0016J\b\u0010\u001a\u001a\u00020\u0016H\u0016¨\u0006\u001c"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/PluginEditTextCell$Factory;", "Lorg/telegram/ui/Components/UItem$UItemFactory;", "Lcom/exteragram/messenger/plugins/ui/components/PluginEditTextCell;", "<init>", "()V", "createView", "context", "Landroid/content/Context;", "listView", "Lorg/telegram/ui/Components/RecyclerListView;", "currentAccount", _UrlKt.FRAGMENT_ENCODE_SET, "classGuid", "resourcesProvider", "Lorg/telegram/ui/ActionBar/Theme$ResourcesProvider;", "bindView", _UrlKt.FRAGMENT_ENCODE_SET, "view", "Landroid/view/View;", "item", "Lorg/telegram/ui/Components/UItem;", "divider", _UrlKt.FRAGMENT_ENCODE_SET, "adapter", "Lorg/telegram/ui/Components/UniversalAdapter;", "Lorg/telegram/ui/Components/UniversalRecyclerView;", "isClickable", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Factory extends UItem.UItemFactory<PluginEditTextCell> {

        public static final Companion INSTANCE = new Companion(null);

        @JvmStatic
        public static final UItem as(Plugin plugin, EditTextSetting editTextSetting) {
            return INSTANCE.as(plugin, editTextSetting);
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean getIsClickableValue() {
            return false;
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public PluginEditTextCell createView(Context context, RecyclerListView listView, int currentAccount, int classGuid, Theme.ResourcesProvider resourcesProvider) {
            return new PluginEditTextCell(context, resourcesProvider);
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public void bindView(View view, UItem item, boolean divider, UniversalAdapter adapter, UniversalRecyclerView listView) {
            if ((view instanceof PluginEditTextCell) && (item.settingItem instanceof EditTextSetting)) {
                Object obj = item.object;
                if (obj instanceof Plugin) {
                    PluginEditTextCell pluginEditTextCell = (PluginEditTextCell) view;
                    pluginEditTextCell.bind(((Plugin) obj).getId(), (EditTextSetting) item.settingItem);
                    pluginEditTextCell.setDivider(divider);
                }
            }
        }

        @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0018\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0007¨\u0006\n"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/PluginEditTextCell$Factory$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "as", "Lorg/telegram/ui/Components/UItem;", "plugin", "Lcom/exteragram/messenger/plugins/Plugin;", "setting", "Lcom/exteragram/messenger/plugins/models/EditTextSetting;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            @JvmStatic
            public final UItem as(Plugin plugin, EditTextSetting setting) {
                UItem uItemOfFactory = UItem.ofFactory(Factory.class);
                uItemOfFactory.object = plugin;
                uItemOfFactory.settingItem = setting;
                return uItemOfFactory;
            }
        }

        static {
            UItem.UItemFactory.setup(new Factory());
        }
    }
}
