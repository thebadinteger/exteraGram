package com.exteragram.messenger.plugins.xposed;

import com.chaquo.python.PyException;
import com.chaquo.python.PyObject;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.plugins.hooks.HookFilter;
import de.robv.android.xposed.XC_MethodHook;
import java.util.ArrayList;
import kotlin.Metadata;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.FileLog;

@Metadata(d1 = {"\u0000V\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u0003\n\u0000\b\u0007\u0018\u00002\u00020\u00012\u00060\u0002j\u0002`\u0003B\u001b\b\u0016\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\tB#\b\u0016\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\b\u0010\fB+\b\u0016\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u000e¢\u0006\u0004\b\b\u0010\u0010B3\b\u0016\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u000e¢\u0006\u0004\b\b\u0010\u0011J\u001e\u0010\u001b\u001a\u00020\u001c2\u0016\u0010\u0014\u001a\u0012\u0012\u0004\u0012\u00020\u00160\u0015j\b\u0012\u0004\u0012\u00020\u0016`\u0017J\u001e\u0010\u001d\u001a\u00020\u001c2\u0016\u0010\u0018\u001a\u0012\u0012\u0004\u0012\u00020\u00160\u0015j\b\u0012\u0004\u0012\u00020\u0016`\u0017J\u0016\u0010\u001e\u001a\u0012\u0012\u0004\u0012\u00020\u00160\u0015j\b\u0012\u0004\u0012\u00020\u0016`\u0017J\u0016\u0010\u001f\u001a\u0012\u0012\u0004\u0012\u00020\u00160\u0015j\b\u0012\u0004\u0012\u00020\u0016`\u0017J\u0010\u0010 \u001a\u00020\u001c2\u0006\u0010!\u001a\u00020\"H\u0016J\u0010\u0010#\u001a\u00020\u001c2\u0006\u0010!\u001a\u00020\"H\u0016J\b\u0010$\u001a\u00020\u001cH\u0016J0\u0010%\u001a\u00020\u000e2\u0016\u0010&\u001a\u0012\u0012\u0004\u0012\u00020\u00160\u0015j\b\u0012\u0004\u0012\u00020\u0016`\u00172\u0006\u0010!\u001a\u00020\"2\u0006\u0010'\u001a\u00020\u000eH\u0002J\"\u0010(\u001a\u0004\u0018\u00010\u00072\u0006\u0010)\u001a\u00020\u00072\u0006\u0010*\u001a\u00020\u00052\u0006\u0010+\u001a\u00020\u000eH\u0002J\u0018\u0010,\u001a\u00020\u001c2\u0006\u0010-\u001a\u00020\u00052\u0006\u0010.\u001a\u00020/H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0013\u001a\u0004\u0018\u00010\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u0014\u001a\u0012\u0012\u0004\u0012\u00020\u00160\u0015j\b\u0012\u0004\u0012\u00020\u0016`\u0017X\u0082\u000e¢\u0006\u0002\n\u0000R\u001e\u0010\u0018\u001a\u0012\u0012\u0004\u0012\u00020\u00160\u0015j\b\u0012\u0004\u0012\u00020\u0016`\u0017X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0019\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u001a\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000¨\u00060"}, d2 = {"Lcom/exteragram/messenger/plugins/xposed/PyMethodHook;", "Lde/robv/android/xposed/XC_MethodHook;", "Ljava/lang/AutoCloseable;", "Lkotlin/AutoCloseable;", "pluginId", _UrlKt.FRAGMENT_ENCODE_SET, "pythonCallback", "Lcom/chaquo/python/PyObject;", "<init>", "(Ljava/lang/String;Lcom/chaquo/python/PyObject;)V", "priority", _UrlKt.FRAGMENT_ENCODE_SET, "(Ljava/lang/String;Lcom/chaquo/python/PyObject;I)V", "hasBeforeHook", _UrlKt.FRAGMENT_ENCODE_SET, "hasAfterHook", "(Ljava/lang/String;Lcom/chaquo/python/PyObject;ZZ)V", "(Ljava/lang/String;Lcom/chaquo/python/PyObject;IZZ)V", "beforeHook", "afterHook", "beforeHookedFilters", "Ljava/util/ArrayList;", "Lcom/exteragram/messenger/plugins/hooks/HookFilter;", "Lkotlin/collections/ArrayList;", "afterHookedFilters", "disabled", "closed", "setBeforeHookedFilters", _UrlKt.FRAGMENT_ENCODE_SET, "setAfterHookedFilters", "getBeforeHookedFilters", "getAfterHookedFilters", "beforeHookedMethod", "param", "Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;", "afterHookedMethod", "close", "executeFilters", "filters", "isBefore", "getCallbackIfPresent", "callbackObject", "name", "enabled", "handleHookError", "hookMethodName", "t", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nPyMethodHook.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PyMethodHook.kt\ncom/exteragram/messenger/plugins/xposed/PyMethodHook\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,187:1\n1#2:188\n*E\n"})
public final class PyMethodHook extends XC_MethodHook implements AutoCloseable {
    private final PyObject afterHook;
    private ArrayList<HookFilter> afterHookedFilters;
    private final PyObject beforeHook;
    private ArrayList<HookFilter> beforeHookedFilters;
    private volatile boolean closed;
    private volatile boolean disabled;
    private final String pluginId;
    private final PyObject pythonCallback;

    public PyMethodHook(String str, PyObject pyObject) {
        this(str, pyObject, true, true);
        "pluginId";
    }

    public PyMethodHook(String str, PyObject pyObject, int i) {
        this(str, pyObject, i, true, true);
        "pluginId";
    }

    public PyMethodHook(String str, PyObject pyObject, boolean z, boolean z2) {
        "pluginId";
        this.beforeHookedFilters = new ArrayList<>();
        this.afterHookedFilters = new ArrayList<>();
        if (pyObject == null) {
            RandomKt$$ExternalSyntheticBUOutline0.m("Python callback object cannot be null");
            throw null;
        }
        this.pluginId = str;
        this.pythonCallback = pyObject;
        this.beforeHook = getCallbackIfPresent(pyObject, "before_hooked_method", z);
        this.afterHook = getCallbackIfPresent(pyObject, "after_hooked_method", z2);
    }

    public PyMethodHook(String str, PyObject pyObject, int i, boolean z, boolean z2) {
        super(i);
        "pluginId";
        this.beforeHookedFilters = new ArrayList<>();
        this.afterHookedFilters = new ArrayList<>();
        if (pyObject == null) {
            RandomKt$$ExternalSyntheticBUOutline0.m("Python callback object cannot be null");
            throw null;
        }
        this.pluginId = str;
        this.pythonCallback = pyObject;
        this.beforeHook = getCallbackIfPresent(pyObject, "before_hooked_method", z);
        this.afterHook = getCallbackIfPresent(pyObject, "after_hooked_method", z2);
    }

    public final void setBeforeHookedFilters(ArrayList<HookFilter> beforeHookedFilters) {
        "beforeHookedFilters";
        this.beforeHookedFilters = beforeHookedFilters;
    }

    public final void setAfterHookedFilters(ArrayList<HookFilter> afterHookedFilters) {
        "afterHookedFilters";
        this.afterHookedFilters = afterHookedFilters;
    }

    public final ArrayList<HookFilter> getBeforeHookedFilters() {
        return this.beforeHookedFilters;
    }

    public final ArrayList<HookFilter> getAfterHookedFilters() {
        return this.afterHookedFilters;
    }

    @Override // de.robv.android.xposed.XC_MethodHook
    public void beforeHookedMethod(XC_MethodHook.MethodHookParam param) {
        PyObject pyObject;
        PyObject pyObjectCall;
        "param";
        if (this.disabled || (pyObject = this.beforeHook) == null || !PluginsController.INSTANCE.getInstance().isPluginActive$TMessagesProj(this.pluginId)) {
            return;
        }
        try {
            if (executeFilters(this.beforeHookedFilters, param, true) && (pyObjectCall = pyObject.call(param)) != null) {
                pyObjectCall.close();
            }
        } catch (Throwable th) {
            handleHookError("beforeHookedMethod", th);
        }
    }

    @Override // de.robv.android.xposed.XC_MethodHook
    public void afterHookedMethod(XC_MethodHook.MethodHookParam param) {
        PyObject pyObject;
        PyObject pyObjectCall;
        "param";
        if (this.disabled || (pyObject = this.afterHook) == null || !PluginsController.INSTANCE.getInstance().isPluginActive$TMessagesProj(this.pluginId)) {
            return;
        }
        try {
            if (executeFilters(this.afterHookedFilters, param, false) && (pyObjectCall = pyObject.call(param)) != null) {
                pyObjectCall.close();
            }
        } catch (Throwable th) {
            handleHookError("afterHookedMethod", th);
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.disabled = true;
        PyObject pyObject = this.beforeHook;
        if (pyObject != null) {
            pyObject.close();
        }
        PyObject pyObject2 = this.afterHook;
        if (pyObject2 != null) {
            pyObject2.close();
        }
    }

    private final boolean executeFilters(ArrayList<HookFilter> filters, XC_MethodHook.MethodHookParam param, boolean isBefore) {
        "iterator(...)";
        for (HookFilter hookFilter : filters) {
            "next(...)";
            if (!hookFilter.execute(param, isBefore)) {
                return false;
            }
        }
        return true;
    }

    private final PyObject getCallbackIfPresent(PyObject callbackObject, String name, boolean enabled) {
        if (enabled && callbackObject.containsKey(name)) {
            return (PyObject) callbackObject.get((Object) name);
        }
        return null;
    }

    private final void handleHookError(String hookMethodName, Throwable t) {
        if ((t instanceof PyException) && t.getMessage() != null && StringsKt.contains$default((CharSequence) t.getMessage(), (CharSequence) "closed", false, 2, (Object) null)) {
            this.disabled = true;
            FileLog.e("Attempted to call a closed PyObject callback in " + this.pluginId);
            return;
        }
        FileLog.e("Plugin '" + this.pluginId + "' crashed in " + hookMethodName + ": " + t.getMessage(), t);
    }
}
