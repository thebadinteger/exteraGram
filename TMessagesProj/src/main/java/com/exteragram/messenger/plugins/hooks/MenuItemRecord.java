package com.exteragram.messenger.plugins.hooks;

import android.content.Context;
import android.text.TextUtils;
import com.chaquo.python.PyObject;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.plugins.utils.PyObjectUtils;
import java.io.Serializable;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.jvm.internal.Intrinsics;
import org.mvel2.MVEL;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

public final class MenuItemRecord {
    private static final ConcurrentHashMap<String, Serializable> mvelExpressionCache = new ConcurrentHashMap<>();
    private final String conditionString;
    private final String iconName;
    private final int iconResId;
    private final String itemId;
    private final String menuType;
    private final PyObject onClickCallback;
    private final String pluginId;
    private final int priority;
    private volatile boolean removed;
    private final String subtext;
    private final String text;

    public MenuItemRecord(String str, PyObject pyObject) {
        this.pluginId = str;
        this.menuType = PyObjectUtils.getString(pyObject, "menu_type", null, true);
        this.text = PyObjectUtils.getString(pyObject, "text", null, true);
        this.onClickCallback = pyObject.callAttr("get", "on_click");
        String string = PyObjectUtils.getString(pyObject, "item_id", null, true);
        string = (string == null || string.length() == 0) ? null : string;
        if (string == null) {
            string = UUID.randomUUID().toString();
        }
        this.itemId = string;
        String string2 = PyObjectUtils.getString(pyObject, "icon", null, true);
        this.iconName = string2;
        this.subtext = PyObjectUtils.getString(pyObject, "subtext", null, true);
        this.conditionString = PyObjectUtils.getString(pyObject, "condition", null, true);
        int identifier = 0;
        this.priority = PyObjectUtils.getInt(pyObject, "priority", 0, true);
        if (!TextUtils.isEmpty(string2)) {
            try {
                Context context = ApplicationLoader.applicationContext;
                identifier = context.getResources().getIdentifier(string2, "drawable", context.getPackageName());
            } catch (Exception unused) {
            }
        }
        this.iconResId = identifier;
        if (TextUtils.isEmpty(this.menuType) || TextUtils.isEmpty(this.text) || this.onClickCallback == null) {
            throw new IllegalArgumentException("MenuItemRecord missing essential fields: menuType, text, or onClickCallback.");
        }
    }

    public final String getPluginId() {
        return this.pluginId;
    }

    public final String getItemId() {
        return this.itemId;
    }

    public final String getMenuType() {
        return this.menuType;
    }

    public final String getText() {
        return this.text;
    }

    public final PyObject getOnClickCallback() {
        return this.onClickCallback;
    }

    public final String getIconName() {
        return this.iconName;
    }

    public final int getIconResId() {
        return this.iconResId;
    }

    public final String getSubtext() {
        return this.subtext;
    }

    public final String getConditionString() {
        return this.conditionString;
    }

    public final int getPriority() {
        return this.priority;
    }

    public final void markRemoved() {
        this.removed = true;
    }

    public final void executeClick(Object contextData) {
        PyObject pyObjectCall;
        if (this.removed || !PluginsController.getInstance().isPluginActive$TMessagesProj(this.pluginId)) {
            return;
        }
        try {
            PyObject pyObject = this.onClickCallback;
            if (pyObject == null || (pyObjectCall = pyObject.call(contextData)) == null) {
                return;
            }
            pyObjectCall.close();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other != null && Intrinsics.areEqual(MenuItemRecord.class, other.getClass())) {
            MenuItemRecord menuItemRecord = (MenuItemRecord) other;
            if (Intrinsics.areEqual(this.itemId, menuItemRecord.itemId) && Intrinsics.areEqual(this.pluginId, menuItemRecord.pluginId)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        return (this.itemId.hashCode() * 31) + this.pluginId.hashCode();
    }

    public final boolean checkCondition(Map<String, ? extends Object> contextData) {
        String str = this.conditionString;
        if (str == null || str.length() == 0 || contextData == null) {
            return true;
        }
        try {
            Serializable serializableComputeIfAbsent = mvelExpressionCache.computeIfAbsent(str, k -> (Serializable) MVEL.compileExpression(k));
            Boolean bool = (Boolean) MVEL.executeExpression(serializableComputeIfAbsent, (Map) contextData, Boolean.TYPE);
            if (bool != null) {
                return bool.booleanValue();
            }
        } catch (Exception unused) {
        }
        return false;
    }
}
