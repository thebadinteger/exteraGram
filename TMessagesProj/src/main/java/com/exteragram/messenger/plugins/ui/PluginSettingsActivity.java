package com.exteragram.messenger.plugins.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.chaquo.python.PyObject;
import com.exteragram.messenger.plugins.Plugin;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.plugins.PythonPluginsEngine;
import com.exteragram.messenger.plugins.models.CustomSetting;
import com.exteragram.messenger.plugins.models.DividerSetting;
import com.exteragram.messenger.plugins.models.EditTextSetting;
import com.exteragram.messenger.plugins.models.HeaderSetting;
import com.exteragram.messenger.plugins.models.InputSetting;
import com.exteragram.messenger.plugins.models.SelectorSetting;
import com.exteragram.messenger.plugins.models.SettingItem;
import com.exteragram.messenger.plugins.models.SwitchSetting;
import com.exteragram.messenger.plugins.models.TextSetting;
import com.exteragram.messenger.plugins.ui.components.PluginEditTextCell;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.exteragram.messenger.preferences.utils.SettingsRegistry;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.sun.jna.Callback;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import okhttp3.internal.url._UrlKt;
import org.lsposed.lsparanoid.Deobfuscator$exteraGramDev$TMessagesProj;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.NotificationsCheckCell;
import org.telegram.ui.Cells.RadioColorCell;
import org.telegram.ui.Cells.TextCell;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.EditTextBoldCursor;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

@Metadata(d1 = {"\u0000\u0086\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0010\u0011\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0007\n\u0002\b\t\b\u0007\u0018\u00002\u00020\u00012\u00020\u0002B\u0007¢\u0006\u0004\b\u0003\u0010\u0004B\u0011\b\u0016\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0003\u0010\u0007B\u001b\b\u0016\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\u0004\b\u0003\u0010\nB5\b\u0016\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\t\u0012\u000e\u0010\f\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\r\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010¢\u0006\u0004\b\u0003\u0010\u0011B?\b\u0016\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\t\u0012\u000e\u0010\f\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\r\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\u0004\b\u0003\u0010\u0012J\u0010\u0010\u001b\u001a\u00020\u00002\b\u0010\u001a\u001a\u0004\u0018\u00010\tJ\b\u0010\u001c\u001a\u00020\tH\u0016J\b\u0010\u001d\u001a\u00020\u001eH\u0016J\b\u0010\u001f\u001a\u00020 H\u0016J5\u0010!\u001a\u00020 2\u0006\u0010\"\u001a\u00020\u00182\u0006\u0010#\u001a\u00020\u00182\u0016\u0010$\u001a\f\u0012\b\b\u0001\u0012\u0004\u0018\u00010&0%\"\u0004\u0018\u00010&H\u0016¢\u0006\u0002\u0010'J\u0010\u0010(\u001a\u00020)2\u0006\u0010*\u001a\u00020+H\u0016J\u0006\u0010,\u001a\u00020 J(\u0010-\u001a\u00020 2\u0016\u0010.\u001a\u0012\u0012\u0004\u0012\u0002000/j\b\u0012\u0004\u0012\u000200`12\u0006\u00102\u001a\u000203H\u0014J0\u00104\u001a\u00020 2\u0006\u00105\u001a\u0002002\u0006\u00106\u001a\u00020)2\u0006\u00107\u001a\u00020\u00182\u0006\u00108\u001a\u0002092\u0006\u0010:\u001a\u000209H\u0014J0\u0010;\u001a\u00020\u001e2\u0006\u00105\u001a\u0002002\u0006\u00106\u001a\u00020)2\u0006\u00107\u001a\u00020\u00182\u0006\u00108\u001a\u0002092\u0006\u0010:\u001a\u000209H\u0014J \u0010<\u001a\u00020 2\u0006\u00105\u001a\u0002002\u0006\u00106\u001a\u00020)2\u0006\u0010=\u001a\u00020\tH\u0002J \u0010>\u001a\u00020 2\u0006\u00105\u001a\u0002002\u0006\u00106\u001a\u00020)2\u0006\u0010=\u001a\u00020\tH\u0002J\u0010\u0010?\u001a\u00020\u00182\u0006\u00105\u001a\u00020\u000eH\u0002J\u0018\u0010@\u001a\u00020 2\u0006\u00105\u001a\u0002002\u0006\u0010A\u001a\u00020\u0010H\u0002R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082.¢\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\u0014\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\rX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0016X\u0082\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\u0017\u001a\u0004\u0018\u00010\u0018X\u0082\u000e¢\u0006\u0004\n\u0002\u0010\u0019R\u0010\u0010\u001a\u001a\u0004\u0018\u00010\tX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006B"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/PluginSettingsActivity;", "Lcom/exteragram/messenger/preferences/BasePreferencesActivity;", "Lorg/telegram/messenger/NotificationCenter$NotificationCenterDelegate;", "<init>", "()V", "plugin", "Lcom/exteragram/messenger/plugins/Plugin;", "(Lcom/exteragram/messenger/plugins/Plugin;)V", "targetSettingName", _UrlKt.FRAGMENT_ENCODE_SET, "(Lcom/exteragram/messenger/plugins/Plugin;Ljava/lang/String;)V", "title", "settingsList", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/plugins/models/SettingItem;", "createSubFragmentCallback", "Lcom/chaquo/python/PyObject;", "(Lcom/exteragram/messenger/plugins/Plugin;Ljava/lang/String;Ljava/util/List;Lcom/chaquo/python/PyObject;)V", "(Lcom/exteragram/messenger/plugins/Plugin;Ljava/lang/String;Ljava/util/List;Lcom/chaquo/python/PyObject;Ljava/lang/String;)V", "customTitle", "settingItems", "resetItem", "Lorg/telegram/ui/ActionBar/ActionBarMenuItem;", "targetSettingItemId", _UrlKt.FRAGMENT_ENCODE_SET, "Ljava/lang/Integer;", "settingsLinkPrefix", "setSettingsLinkPrefix", "getTitle", "onFragmentCreate", _UrlKt.FRAGMENT_ENCODE_SET, "onFragmentDestroy", _UrlKt.FRAGMENT_ENCODE_SET, "didReceivedNotification", "id", "account", "args", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "(II[Ljava/lang/Object;)V", "createView", "Landroid/view/View;", "context", "Landroid/content/Context;", "checkTargetSetting", "fillItems", "items", "Ljava/util/ArrayList;", "Lorg/telegram/ui/Components/UItem;", "Lkotlin/collections/ArrayList;", "adapter", "Lorg/telegram/ui/Components/UniversalAdapter;", "onClick", "item", "view", "position", "x", _UrlKt.FRAGMENT_ENCODE_SET, "y", "onLongClick", "showStringInputDialog", "key", "showSelectorDialog", "getStableId", "openSubFragmentNative", Callback.METHOD_NAME, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class PluginSettingsActivity extends BasePreferencesActivity implements NotificationCenter.NotificationCenterDelegate {
    private PyObject createSubFragmentCallback;
    private String customTitle;
    private Plugin plugin;
    private ActionBarMenuItem resetItem;
    private List<? extends SettingItem> settingItems;
    private String settingsLinkPrefix;
    private Integer targetSettingItemId;
    private String targetSettingName;

    public PluginSettingsActivity() {
    }

    public static void m1332$r8$lambda$ATxfzrI9UmSnf_gboVo0ib94H8(EditTextBoldCursor editTextBoldCursor, Ref.ObjectRef objectRef, View view, final PluginSettingsActivity pluginSettingsActivity, final String str, final InputSetting inputSetting) {
        Editable text = editTextBoldCursor.getText();
        final String string = text != null ? text.toString() : null;
        if (string == null) {
            string = "";
        }
        AlertDialog alertDialog = (AlertDialog) objectRef.element;
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        "null cannot be cast to non-null type org.telegram.ui.Cells.TextCell";
        ((TextCell) view).setValue(string, true);
        PluginsController.INSTANCE.runOnPluginsQueue(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                PluginSettingsActivity.showStringInputDialog$lambda$3$0(this.f$0, str, string, inputSetting);
            }
        });
    }

    public static final void showStringInputDialog$lambda$3$0(PluginSettingsActivity pluginSettingsActivity, String str, String str2, InputSetting inputSetting) {
        PluginsController companion = PluginsController.INSTANCE.getInstance();
        Plugin plugin = pluginSettingsActivity.plugin;
        if (plugin == null) {
            "plugin";
            plugin = null;
        }
        companion.setPluginSettingAndTriggerOnChange(plugin.getId(), str, str2, inputSetting.getOnChangeCallback());
    }

    public static void $r8$lambda$XATyCfuUD2Re7N0jRXAxTQrfe70(EditTextBoldCursor editTextBoldCursor, DialogInterface dialogInterface) {
        editTextBoldCursor.requestFocus();
        editTextBoldCursor.setSelection(editTextBoldCursor.length());
        AndroidUtilities.showKeyboard(editTextBoldCursor);
    }

    private final void showSelectorDialog(UItem item, final View view, final String key) {
        SettingItem settingItem = item.settingItem;
        final SelectorSetting selectorSetting = settingItem instanceof SelectorSetting ? (SelectorSetting) settingItem : null;
        if (selectorSetting == null || getParentActivity() == null) {
            return;
        }
        final AtomicReference atomicReference = new AtomicReference();
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(1);
        final String[] items = selectorSetting.getItems();
        int length = items.length;
        final int i = 0;
        while (i < length) {
            RadioColorCell radioColorCell = new RadioColorCell(getParentActivity());
            radioColorCell.setPadding(AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(4.0f), 0);
            radioColorCell.setCheckColor(Theme.getColor(Theme.key_radioBackground), Theme.getColor(Theme.key_dialogRadioBackgroundChecked));
            PluginsController companion = PluginsController.INSTANCE.getInstance();
            Plugin plugin = this.plugin;
            if (plugin == null) {
                "plugin";
                plugin = null;
            }
            radioColorCell.setTextAndValue(items[i], companion.getPluginSettingInt(plugin.getId(), key, selectorSetting.getDefaultValue()) == i);
            radioColorCell.setBackground(Theme.createSelectorDrawable(Theme.getColor(Theme.key_listSelector), 2));
            linearLayout.addView(radioColorCell);
            radioColorCell.setOnClickListener(new View.OnClickListener() { 
                @Override // android.view.View.OnClickListener
                public final void onClick(View view2) {
                    PluginSettingsActivity.m1331$r8$lambda$4POcQzJfVWfbcSaEpIiFMUCpp4(atomicReference, view, items, i, this, key, selectorSetting, view2);
                }
            });
            i++;
        }
        AlertDialog alertDialogCreate = new AlertDialog.Builder(getParentActivity()).setTitle(item.text).setView(linearLayout).setNegativeButton(LocaleController.getString(R.string.Cancel), null).create();
        atomicReference.set(alertDialogCreate);
        showDialog(alertDialogCreate);
    }

    public static void m1331$r8$lambda$4POcQzJfVWfbcSaEpIiFMUCpp4(AtomicReference atomicReference, View view, String[] strArr, final int i, final PluginSettingsActivity pluginSettingsActivity, final String str, final SelectorSetting selectorSetting, View view2) {
        Dialog dialog = (Dialog) atomicReference.get();
        if (dialog != null) {
            dialog.dismiss();
        }
        "null cannot be cast to non-null type org.telegram.ui.Cells.TextCell";
        ((TextCell) view).setValue(strArr[i], true);
        PluginsController.INSTANCE.runOnPluginsQueue(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                PluginSettingsActivity.showSelectorDialog$lambda$2$0(this.f$0, str, i, selectorSetting);
            }
        });
    }

    public static final void showSelectorDialog$lambda$2$0(PluginSettingsActivity pluginSettingsActivity, String str, int i, SelectorSetting selectorSetting) {
        PluginsController companion = PluginsController.INSTANCE.getInstance();
        Plugin plugin = pluginSettingsActivity.plugin;
        if (plugin == null) {
            "plugin";
            plugin = null;
        }
        companion.setPluginSettingAndTriggerOnChange(plugin.getId(), str, Integer.valueOf(i), selectorSetting.getOnChangeCallback());
    }

    /* JADX WARN: Type inference failed for: r2v2, types: [T, java.util.ArrayList] */
    public static void $r8$lambda$py2IHrU6RevEMbA19W9LY0CNXXI(final PluginSettingsActivity pluginSettingsActivity, final PyObject pyObject, final UItem uItem) {
        PluginsController.Companion companion = PluginsController.INSTANCE;
        PluginsController companion2 = companion.getInstance();
        Plugin plugin = pluginSettingsActivity.plugin;
        if (plugin == null) {
            "plugin";
            plugin = null;
        }
        if (companion2.isPluginActive$TMessagesProj(plugin)) {
            final Ref.ObjectRef objectRef = new Ref.ObjectRef();
            objectRef.element = new ArrayList();
            try {
                PyObject pyObjectCall = pyObject.call(new Object[0]);
                if (pyObjectCall != null) {
                    PluginsController.PluginsEngine pluginsEngine = companion.getEngines().get("python");
                    PythonPluginsEngine pythonPluginsEngine = pluginsEngine instanceof PythonPluginsEngine ? (PythonPluginsEngine) pluginsEngine : null;
                    if (pythonPluginsEngine == null) {
                        return;
                    }
                    List<PyObject> listAsList = pyObjectCall.asList();
                    "asList(...)";
                    objectRef.element = pythonPluginsEngine.parsePySettingDefinitions(listAsList);
                }
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        PluginSettingsActivity.openSubFragmentNative$lambda$0$0(objectRef, uItem, pluginSettingsActivity, pyObject);
                    }
                });
            } catch (Exception e) {
                FileLog.e("Error opening subfragment", e);
            }
        }
    }

    public static final void openSubFragmentNative$lambda$0$0(Ref.ObjectRef objectRef, UItem uItem, PluginSettingsActivity pluginSettingsActivity, PyObject pyObject) {
        String name;
        String string;
        if (((Collection) objectRef.element).isEmpty()) {
            return;
        }
        Plugin plugin = null;
        if (!TextUtils.isEmpty(uItem.text)) {
            name = uItem.text.toString();
        } else {
            name = pluginSettingsActivity.customTitle;
            if (name == null) {
                Plugin plugin2 = pluginSettingsActivity.plugin;
                if (plugin2 == null) {
                    "plugin";
                    plugin2 = null;
                }
                name = plugin2.getName();
            }
        }
        Plugin plugin3 = pluginSettingsActivity.plugin;
        if (plugin3 == null) {
            "plugin";
        } else {
            plugin = plugin3;
        }
        PluginSettingsActivity pluginSettingsActivity2 = new PluginSettingsActivity(plugin, name, (List) objectRef.element, pyObject);
        StringBuilder sb = new StringBuilder();
        String str = pluginSettingsActivity.settingsLinkPrefix;
        sb.append(str != null ? str.concat(":") : "");
        SettingItem settingItem = uItem.settingItem;
        if (settingItem == null || (string = settingItem.getLinkAlias()) == null) {
            string = "";
        }
        sb.append(string);
        pluginSettingsActivity.presentFragment(pluginSettingsActivity2.setSettingsLinkPrefix(sb.toString()));
    }
}
