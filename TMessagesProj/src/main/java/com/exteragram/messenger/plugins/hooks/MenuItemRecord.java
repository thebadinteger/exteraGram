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
import java.util.function.Function;
import kotlin.Metadata;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;
import org.mvel2.MVEL;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\f\n\u0002\u0010\b\n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010$\n\u0002\b\u0002\b\u0007\u0018\u0000 *2\u00020\u0001:\u0001*B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0006\u0010!\u001a\u00020\"J\u0010\u0010#\u001a\u00020\"2\b\u0010$\u001a\u0004\u0018\u00010\u0001J\u0013\u0010%\u001a\u00020\u000b2\b\u0010&\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010'\u001a\u00020\u0018H\u0016J\u001c\u0010(\u001a\u00020\u000b2\u0014\u0010$\u001a\u0010\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u0001\u0018\u00010)R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u000e\u0010\n\u001a\u00020\u000bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\f\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\tR\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\tR\u0013\u0010\u0010\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\tR\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0013\u0010\u0015\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\tR\u0011\u0010\u0017\u001a\u00020\u0018¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0013\u0010\u001b\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\tR\u0013\u0010\u001d\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\tR\u0011\u0010\u001f\u001a\u00020\u0018¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u001a¨\u0006+"}, d2 = {"Lcom/exteragram/messenger/plugins/hooks/MenuItemRecord;", _UrlKt.FRAGMENT_ENCODE_SET, "pluginId", _UrlKt.FRAGMENT_ENCODE_SET, "pyData", "Lcom/chaquo/python/PyObject;", "<init>", "(Ljava/lang/String;Lcom/chaquo/python/PyObject;)V", "getPluginId", "()Ljava/lang/String;", "removed", _UrlKt.FRAGMENT_ENCODE_SET, "itemId", "getItemId", "menuType", "getMenuType", "text", "getText", "onClickCallback", "getOnClickCallback", "()Lcom/chaquo/python/PyObject;", "iconName", "getIconName", "iconResId", _UrlKt.FRAGMENT_ENCODE_SET, "getIconResId", "()I", "subtext", "getSubtext", "conditionString", "getConditionString", "priority", "getPriority", "markRemoved", _UrlKt.FRAGMENT_ENCODE_SET, "executeClick", "contextData", "equals", "other", "hashCode", "checkCondition", _UrlKt.FRAGMENT_ENCODE_SET, "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nMenuItemRecord.kt\nKotlin\n*S Kotlin\n*F\n+ 1 MenuItemRecord.kt\ncom/exteragram/messenger/plugins/hooks/MenuItemRecord\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,137:1\n1#2:138\n*E\n"})
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
        "pluginId";
        "pyData";
        this.pluginId = str;
        this.menuType = PyObjectUtils.getString(pyObject, "menu_type", null, true);
        this.text = PyObjectUtils.getString(pyObject, "text", null, true);
        this.onClickCallback = pyObject.callAttr("get", "on_click");
        String string = PyObjectUtils.getString(pyObject, "item_id", null, true);
        string = (string == null || string.length() == 0) ? null : string;
        if (string == null) {
            string = UUID.randomUUID().toString();
            "toString(...)";
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
                "applicationContext";
                identifier = context.getResources().getIdentifier(string2, "drawable", context.getPackageName());
            } catch (Exception unused) {
            }
        }
        this.iconResId = identifier;
        if (TextUtils.isEmpty(this.menuType) || TextUtils.isEmpty(this.text) || this.onClickCallback == null) {
            f$$ExternalSyntheticBUOutline1.m("MenuItemRecord missing essential fields: menuType, text, or onClickCallback.");
            throw null;
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
        if (this.removed || !PluginsController.INSTANCE.getInstance().isPluginActive$TMessagesProj(this.pluginId)) {
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
            ConcurrentHashMap<String, Serializable> concurrentHashMap = mvelExpressionCache;
            final MenuItemRecord$checkCondition$compiled$1 menuItemRecord$checkCondition$compiled$1 = MenuItemRecord$checkCondition$compiled$1.INSTANCE;
            Serializable serializableComputeIfAbsent = concurrentHashMap.computeIfAbsent(str, new Function() { 
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return MenuItemRecord.$r8$lambda$hr1hlLIaQobH5EJk748ZU6CcfyA(menuItemRecord$checkCondition$compiled$1, obj);
                }
            });
            "computeIfAbsent(...)";
            Boolean bool = (Boolean) MVEL.executeExpression((Object) serializableComputeIfAbsent, (Map) contextData, Boolean.TYPE);
            if (bool != null) {
                return bool.booleanValue();
            }
        } catch (Exception unused) {
        }
        return false;
    }

    public static Serializable $r8$lambda$hr1hlLIaQobH5EJk748ZU6CcfyA(Function1 function1, Object obj) {
        return (Serializable) function1.invoke(obj);
    }
}
