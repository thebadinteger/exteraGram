package com.exteragram.messenger.plugins.utils;

import android.text.TextUtils;
import com.chaquo.python.PyException;
import com.chaquo.python.PyObject;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import okhttp3.internal.url._UrlKt;
import org.lsposed.lsparanoid.Deobfuscator$exteraGramDev$TMessagesProj;

@Metadata(d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0011\n\u0002\b\u0002\bÇ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0002J-\u0010\b\u001a\u0004\u0018\u0001H\t\"\u0004\b\u0000\u0010\t2\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\f\u0010\n\u001a\b\u0012\u0004\u0012\u0002H\t0\u000bH\u0007¢\u0006\u0002\u0010\fJ&\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u000f\u001a\u00020\u000e2\b\u0010\u0010\u001a\u0004\u0018\u00010\u000eH\u0007J.\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u000f\u001a\u00020\u000e2\b\u0010\u0010\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u0011\u001a\u00020\u0012H\u0007J\"\u0010\u0013\u001a\u00020\u00122\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u0012H\u0007J\"\u0010\u0014\u001a\u00020\u00152\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u0015H\u0007J*\u0010\u0014\u001a\u00020\u00152\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u000f\u001a\u00020\u000e2\u0006\u0010\u0010\u001a\u00020\u00152\u0006\u0010\u0011\u001a\u00020\u0012H\u0007J7\u0010\u0016\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\u00172\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\u0006\u0010\u000f\u001a\u00020\u000e2\u000e\u0010\u0010\u001a\n\u0012\u0004\u0012\u00020\u000e\u0018\u00010\u0017H\u0007¢\u0006\u0002\u0010\u0018¨\u0006\u0019"}, d2 = {"Lcom/exteragram/messenger/plugins/utils/PyObjectUtils;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "closeQuietly", _UrlKt.FRAGMENT_ENCODE_SET, "pyObject", "Lcom/chaquo/python/PyObject;", "toJavaCompat", "T", "clazz", "Ljava/lang/Class;", "(Lcom/chaquo/python/PyObject;Ljava/lang/Class;)Ljava/lang/Object;", "getString", _UrlKt.FRAGMENT_ENCODE_SET, "key", "defaultValue", "fromMap", _UrlKt.FRAGMENT_ENCODE_SET, "getBoolean", "getInt", _UrlKt.FRAGMENT_ENCODE_SET, "getStringArray", _UrlKt.FRAGMENT_ENCODE_SET, "(Lcom/chaquo/python/PyObject;Ljava/lang/String;[Ljava/lang/String;)[Ljava/lang/String;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class PyObjectUtils {
    public static final PyObjectUtils INSTANCE = new PyObjectUtils();

    private PyObjectUtils() {
    }

    private final void closeQuietly(PyObject pyObject) {
        if (pyObject != null) {
            try {
                pyObject.close();
            } catch (PyException unused) {
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r0v3, types: [T] */
    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    @JvmStatic
    public static final <T> T toJavaCompat(PyObject pyObject, Class<T> clazz) throws Throwable {
        PyObject pyObjectCallAttr;
        "clazz";
        PyObject pyObject2 = (T) null;
        if (pyObject == null) {
            return null;
        }
        try {
            return (T) pyObject.toJava(clazz);
        } catch (PyException | ClassCastException unused) {
            try {
                pyObjectCallAttr = pyObject.callAttr("__getattribute__", "java");
                if (pyObjectCallAttr != null) {
                    try {
                        pyObject2 = (T) pyObjectCallAttr.toJava(clazz);
                    } catch (PyException | ClassCastException unused2) {
                    } catch (Throwable th) {
                        th = th;
                        pyObject2 = (T) pyObjectCallAttr;
                        INSTANCE.closeQuietly(pyObject2);
                        throw th;
                    }
                }
            } catch (PyException | ClassCastException unused3) {
                pyObjectCallAttr = null;
            } catch (Throwable th2) {
                th = th2;
            }
            INSTANCE.closeQuietly(pyObjectCallAttr);
            return (T) pyObject2;
        }
    }

    @JvmStatic
    public static final String getString(PyObject pyObject, String key, String defaultValue) {
        "key";
        return getString(pyObject, key, defaultValue, false);
    }

    @JvmStatic
    public static final String getString(PyObject pyObject, String key, String defaultValue, boolean fromMap) {
        String string;
        "key";
        if (pyObject != null && !TextUtils.isEmpty(key)) {
            PyObject pyObjectCallAttr = null;
            try {
                pyObjectCallAttr = fromMap ? pyObject.callAttr("get", key) : (PyObject) pyObject.get((Object) key);
                if (pyObjectCallAttr != null && (string = pyObjectCallAttr.toString()) != null) {
                    defaultValue = string;
                }
                INSTANCE.closeQuietly(pyObjectCallAttr);
                return defaultValue;
            } catch (PyException | ClassCastException unused) {
            } finally {
                INSTANCE.closeQuietly(pyObjectCallAttr);
            }
        }
        return defaultValue;
    }

    @JvmStatic
    public static final boolean getBoolean(PyObject pyObject, String key, boolean defaultValue) throws Throwable {
        "key";
        if (pyObject != null && !TextUtils.isEmpty(key)) {
            PyObject pyObject2 = null;
            try {
                PyObject pyObject3 = (PyObject) pyObject.get((Object) key);
                if (pyObject3 != null) {
                    try {
                        defaultValue = pyObject3.toBoolean();
                    } catch (PyException | ClassCastException unused) {
                        pyObject2 = pyObject3;
                        INSTANCE.closeQuietly(pyObject2);
                        return defaultValue;
                    } catch (Throwable th) {
                        th = th;
                        pyObject2 = pyObject3;
                        INSTANCE.closeQuietly(pyObject2);
                        throw th;
                    }
                }
                INSTANCE.closeQuietly(pyObject3);
                return defaultValue;
            } catch (PyException | ClassCastException unused2) {
            } catch (Throwable th2) {
                th = th2;
            }
        }
        return defaultValue;
    }

    @JvmStatic
    public static final int getInt(PyObject pyObject, String key, int defaultValue) {
        "key";
        return getInt(pyObject, key, defaultValue, false);
    }

    @JvmStatic
    public static final int getInt(PyObject pyObject, String key, int defaultValue, boolean fromMap) {
        "key";
        if (pyObject != null && !TextUtils.isEmpty(key)) {
            PyObject pyObjectCallAttr = null;
            try {
                pyObjectCallAttr = fromMap ? pyObject.callAttr("get", key) : (PyObject) pyObject.get((Object) key);
                if (pyObjectCallAttr != null) {
                    defaultValue = pyObjectCallAttr.toInt();
                }
                INSTANCE.closeQuietly(pyObjectCallAttr);
                return defaultValue;
            } catch (PyException | ClassCastException unused) {
            } finally {
                INSTANCE.closeQuietly(pyObjectCallAttr);
            }
        }
        return defaultValue;
    }

    @JvmStatic
    public static final String[] getStringArray(PyObject pyObject, String key, String[] defaultValue) throws Throwable {
        "key";
        if (pyObject != null && !TextUtils.isEmpty(key)) {
            PyObject pyObject2 = null;
            try {
                PyObject pyObject3 = (PyObject) pyObject.get((Object) key);
                if (pyObject3 != null) {
                    try {
                        String[] strArr = (String[]) pyObject3.toJava(String[].class);
                        if (strArr != null && strArr.length != 0) {
                            defaultValue = strArr;
                        }
                    } catch (PyException | ClassCastException unused) {
                        pyObject2 = pyObject3;
                        INSTANCE.closeQuietly(pyObject2);
                        return defaultValue;
                    } catch (Throwable th) {
                        th = th;
                        pyObject2 = pyObject3;
                        INSTANCE.closeQuietly(pyObject2);
                        throw th;
                    }
                }
                INSTANCE.closeQuietly(pyObject3);
                return defaultValue;
            } catch (PyException | ClassCastException unused2) {
            } catch (Throwable th2) {
                th = th2;
            }
        }
        return defaultValue;
    }
}
