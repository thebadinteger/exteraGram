package com.exteragram.messenger.plugins.xposed;

import com.chaquo.python.PyException;
import com.chaquo.python.PyObject;
import com.exteragram.messenger.plugins.PluginsController;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.InvocationTargetException;
import kotlin.Metadata;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.FileLog;

@Metadata(d1 = {"\u0000J\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0003\n\u0000\b\u0007\u0018\u00002\u00020\u00012\u00060\u0002j\u0002`\u0003B\u001b\b\u0016\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\tB#\b\u0016\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\n\u001a\u00020\u000b¢\u0006\u0004\b\b\u0010\fJ\u0012\u0010\u0011\u001a\u0004\u0018\u00010\u00122\u0006\u0010\u0013\u001a\u00020\u0014H\u0016J\b\u0010\u0015\u001a\u00020\u0016H\u0016J\u0010\u0010\u0017\u001a\u00020\u00162\u0006\u0010\u0018\u001a\u00020\u0019H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001a"}, d2 = {"Lcom/exteragram/messenger/plugins/xposed/PyMethodReplacement;", "Lde/robv/android/xposed/XC_MethodReplacement;", "Ljava/lang/AutoCloseable;", "Lkotlin/AutoCloseable;", "pluginId", _UrlKt.FRAGMENT_ENCODE_SET, "pythonCallback", "Lcom/chaquo/python/PyObject;", "<init>", "(Ljava/lang/String;Lcom/chaquo/python/PyObject;)V", "priority", _UrlKt.FRAGMENT_ENCODE_SET, "(Ljava/lang/String;Lcom/chaquo/python/PyObject;I)V", "replaceHook", "disabled", _UrlKt.FRAGMENT_ENCODE_SET, "closed", "replaceHookedMethod", _UrlKt.FRAGMENT_ENCODE_SET, "param", "Lde/robv/android/xposed/XC_MethodHook$MethodHookParam;", "close", _UrlKt.FRAGMENT_ENCODE_SET, "handleHookError", "t", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nPyMethodReplacement.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PyMethodReplacement.kt\ncom/exteragram/messenger/plugins/xposed/PyMethodReplacement\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,92:1\n1#2:93\n*E\n"})
public final class PyMethodReplacement extends XC_MethodReplacement implements AutoCloseable {
    private volatile boolean closed;
    private volatile boolean disabled;
    private final String pluginId;
    private final PyObject pythonCallback;
    private final PyObject replaceHook;

    public PyMethodReplacement(String str, PyObject pyObject) {
        if (pyObject == null) {
            throw new IllegalArgumentException("Python callback object cannot be null");
        }
        if (!pyObject.containsKey("replace_hooked_method")) {
            throw new IllegalArgumentException("Python callback object must contain a method named 'replaceHookedMethod'");
        }
        this.pluginId = str;
        this.pythonCallback = pyObject;
        Object obj = pyObject.get((Object) "replace_hooked_method");
        if (obj != null) {
            this.replaceHook = (PyObject) obj;
        } else {
            throw new IllegalArgumentException("Required value was null.");
        }
    }

    public PyMethodReplacement(String str, PyObject pyObject, int i) {
        super(i);
        if (pyObject == null) {
            throw new IllegalArgumentException("Python callback object cannot be null");
        }
        if (!pyObject.containsKey("replace_hooked_method")) {
            throw new IllegalArgumentException("Python callback object must contain a method named 'replaceHookedMethod'");
        }
        this.pluginId = str;
        this.pythonCallback = pyObject;
        Object obj = pyObject.get((Object) "replace_hooked_method");
        if (obj != null) {
            this.replaceHook = (PyObject) obj;
        } else {
            throw new IllegalArgumentException("Required value was null.");
        }
    }

    @Override // de.robv.android.xposed.XC_MethodReplacement
    public Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
        if (this.disabled || !PluginsController.INSTANCE.getInstance().isPluginActive$TMessagesProj(this.pluginId)) {
            try {
                return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
            } catch (InvocationTargetException e) {
                Throwable targetException = e.getTargetException();
                if (targetException == null) {
                    throw e;
                }
                throw targetException;
            }
        }
        PyObject pyObjectCall = null;
        Object java = null;
        try {
            pyObjectCall = this.replaceHook.call(param);
            if (pyObjectCall != null) {
                java = pyObjectCall.toJava(Object.class);
            }
        } catch (Throwable th) {
            handleHookError(th);
            return null;
        } finally {
            if (pyObjectCall != null) {
                pyObjectCall.close();
            }
        }
        return java;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        if (this.closed) {
            return;
        }
        this.closed = true;
        this.disabled = true;
        this.replaceHook.close();
    }

    private final void handleHookError(Throwable t) {
        if ((t instanceof PyException) && t.getMessage() != null && t.getMessage().contains("closed")) {
            this.disabled = true;
            FileLog.e("Attempted to call a closed PyObject callback in " + this.pluginId);
            return;
        }
        FileLog.e("Plugin '" + this.pluginId + "' crashed in replaceHookedMethod: " + t.getMessage(), t);
    }
}
