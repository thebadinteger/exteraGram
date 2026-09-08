package de.robv.android.xposed;

import a.a;
import a.c;
import android.util.Log;
import androidx.p003lifecycle.LiveData$$ExternalSyntheticBUOutline0;
import c.b;
import c.d;
import c.e;
import c.f;
import c.f$$ExternalSyntheticBUOutline1;
import c.f$$ExternalSyntheticBUOutline2;
import com.sun.jna.Callback;
import de.robv.android.xposed.XC_MethodHook.Unhook;
import dev.exterahook.runtime.bridge.HookBridgeProvider;
import dev.exterahook.runtime.bridge.JniBridgeBindings;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import kotlin.Unit;
import okhttp3.internal.url._UrlKt;
import org.lsposed.hiddenapibypass.HiddenApiBypass;
import org.mvel2.util.Make$Map$$ExternalSyntheticBUOutline0;

public class XposedBridge {
    private static final Object[] EMPTY_ARRAY = new Object[0];
    private static final String TAG = "exteraHook-XposedBridge";
    private static final Method callbackMethod;
    private static final a hookBridge;
    private static final d hookRegistry;

    public static final class CopyOnWriteSortedSet<E> {
        private volatile transient Object[] elements = XposedBridge.EMPTY_ARRAY;

        private int indexOf(Object[] objArr, Object obj) {
            for (int i = 0; i < objArr.length; i++) {
                if (obj.equals(objArr[i])) {
                    return i;
                }
            }
            return -1;
        }

        private int insertionIndex(Object[] objArr, E e) {
            for (Object obj : objArr) {
                if (e.equals(obj)) {
                    return -1;
                }
            }
            Comparable comparable = (Comparable) e;
            for (int i = 0; i < objArr.length; i++) {
                if (comparable.compareTo(objArr[i]) < 0) {
                    return i;
                }
            }
            return objArr.length;
        }

        public synchronized boolean add(E e) {
            Object[] objArr = this.elements;
            int iInsertionIndex = insertionIndex(objArr, e);
            if (iInsertionIndex < 0) {
                return false;
            }
            Object[] objArr2 = new Object[objArr.length + 1];
            System.arraycopy(objArr, 0, objArr2, 0, iInsertionIndex);
            objArr2[iInsertionIndex] = e;
            System.arraycopy(objArr, iInsertionIndex, objArr2, iInsertionIndex + 1, objArr.length - iInsertionIndex);
            this.elements = objArr2;
            return true;
        }

        public Object[] getSnapshot() {
            return this.elements;
        }

        public synchronized boolean remove(E e) {
            Object[] objArr = this.elements;
            int iIndexOf = indexOf(objArr, e);
            if (iIndexOf == -1) {
                return false;
            }
            Object[] objArr2 = new Object[objArr.length - 1];
            System.arraycopy(objArr, 0, objArr2, 0, iIndexOf);
            System.arraycopy(objArr, iIndexOf + 1, objArr2, iIndexOf, (objArr.length - iIndexOf) - 1);
            this.elements = objArr2;
            return true;
        }

        public int size() {
            return this.elements.length;
        }
    }

    public static class HookInfo {
        private static final long BACKUP_PUBLISH_TIMEOUT_MS = 1000;
        private volatile Method backup;
        private final Class<?> boxedPrimitiveReturnType;
        private volatile boolean installFailed;
        private final boolean isStatic;
        private final Member method;
        private final Class<?> returnType;
        private final Object backupLock = new Object();
        private volatile Thread installingThread = Thread.currentThread();
        final CopyOnWriteSortedSet<XC_MethodHook> callbacks = new CopyOnWriteSortedSet<>();

        public HookInfo(Member member) {
            this.method = member;
            this.isStatic = Modifier.isStatic(member.getModifiers());
            if (member instanceof Method) {
                Class<?> returnType = ((Method) member).getReturnType();
                if (!returnType.isPrimitive()) {
                    this.returnType = returnType;
                    this.boxedPrimitiveReturnType = null;
                    return;
                }
                this.boxedPrimitiveReturnType = boxPrimitiveType(returnType);
            } else {
                this.boxedPrimitiveReturnType = null;
            }
            this.returnType = null;
        }

        private Method awaitBackupMethodSlowPath() {
            synchronized (this.backupLock) {
                try {
                    if (Thread.currentThread() != this.installingThread) {
                        long jNanoTime = System.nanoTime() + 1000000000;
                        for (long jNanoTime2 = 1000; this.backup == null && !this.installFailed && jNanoTime2 > 0; jNanoTime2 = (jNanoTime - System.nanoTime()) / 1000000) {
                            try {
                                this.backupLock.wait(jNanoTime2);
                            } catch (InterruptedException unused) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }
                    Method method = this.backup;
                    if (method != null) {
                        return method;
                    }
                    boolean z = this.installFailed;
                    Member member = this.method;
                    if (z) {
                        LiveData$$ExternalSyntheticBUOutline0.m("Hooking ", member, " failed, the original method is unavailable");
                        return null;
                    }
                    LiveData$$ExternalSyntheticBUOutline0.m("Backup method for ", member, " was not published within 1000ms");
                    return null;
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        private static Class<?> boxPrimitiveType(Class<?> cls) {
            if (cls == Void.TYPE) {
                return null;
            }
            if (cls == Boolean.TYPE) {
                return Boolean.class;
            }
            if (cls == Byte.TYPE) {
                return Byte.class;
            }
            if (cls == Character.TYPE) {
                return Character.class;
            }
            if (cls == Short.TYPE) {
                return Short.class;
            }
            if (cls == Integer.TYPE) {
                return Integer.class;
            }
            if (cls == Long.TYPE) {
                return Long.class;
            }
            if (cls == Float.TYPE) {
                return Float.class;
            }
            if (cls == Double.TYPE) {
                return Double.class;
            }
            throw new AssertionError("Unknown primitive type: " + cls);
        }

        public Method awaitBackupMethod() {
            Method method = this.backup;
            return method != null ? method : awaitBackupMethodSlowPath();
        }

        public Object callback(Object[] objArr) {
            return HookCallbackDispatcher.dispatch(this, objArr);
        }

        public Member getBackup() {
            return this.backup;
        }

        public Class<?> getBoxedPrimitiveReturnType() {
            return this.boxedPrimitiveReturnType;
        }

        public CopyOnWriteSortedSet<XC_MethodHook> getCallbacks() {
            return this.callbacks;
        }

        public Member getMethod() {
            return this.method;
        }

        public Class<?> getReturnType() {
            return this.returnType;
        }

        public boolean isStaticHook() {
            return this.isStatic;
        }

        public void markInstallFailed() {
            synchronized (this.backupLock) {
                try {
                    if (this.backup != null) {
                        return;
                    }
                    this.installFailed = true;
                    this.installingThread = null;
                    this.backupLock.notifyAll();
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        public void setBackup(Member member) {
            Method method = (Method) member;
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            synchronized (this.backupLock) {
                this.backup = method;
                this.installingThread = null;
                this.backupLock.notifyAll();
            }
        }
    }

    public static Member m2305$r8$lambda$10kFl2JjBfEwcKLdb607Wxxs(Object obj, Member member) {
        a aVarBridge = bridge();
        Method method = callbackMethod;
        c cVar = (c) aVarBridge;
        cVar.getClass();
        cVar.a();
        return JniBridgeBindings.hook0(obj, member, method);
    }

    static {
        try {
            callbackMethod = HookInfo.class.getMethod(Callback.METHOD_NAME, Object[].class);
            hookRegistry = new d();
            hookBridge = HookBridgeProvider.createDefault();
        } catch (Throwable th) {
            Make$Map$$ExternalSyntheticBUOutline0.m("Failed to initialize callback bridge", th);
        }
    }

    private static a bridge() {
        return hookBridge;
    }

    public static boolean deoptimizeMethod(Member member) {
        f.a(member);
        c cVar = (c) bridge();
        cVar.getClass();
        cVar.a();
        return JniBridgeBindings.deoptimize0(member);
    }

    public static boolean disableHiddenApiRestrictions() {
        boolean zDisableHiddenApiRestrictions;
        b.a aVar = ((c) bridge()).f8b;
        boolean z = true;
        if (aVar.f11b) {
            return true;
        }
        synchronized (aVar) {
            if (!aVar.f11b) {
                try {
                    if (HiddenApiBypass.addHiddenApiExemptions(_UrlKt.FRAGMENT_ENCODE_SET)) {
                        zDisableHiddenApiRestrictions = true;
                    } else {
                        aVar.f10a.f12a.a();
                        zDisableHiddenApiRestrictions = JniBridgeBindings.disableHiddenApiRestrictions();
                    }
                } catch (Throwable unused) {
                }
                if (zDisableHiddenApiRestrictions) {
                    aVar.f11b = true;
                }
                z = zDisableHiddenApiRestrictions;
            }
        }
        return z;
    }

    public static boolean disableProfileSaver() {
        ((c) bridge()).a();
        return JniBridgeBindings.disableProfileSaver0();
    }

    public static Set<XC_MethodHook.Unhook> hookAllConstructors(Class<?> cls, XC_MethodHook xC_MethodHook) {
        HashSet hashSet = new HashSet();
        try {
            for (Constructor<?> constructor : cls.getDeclaredConstructors()) {
                hashSet.add(hookMethod(constructor, xC_MethodHook));
            }
            return hashSet;
        } catch (Error | RuntimeException e) {
            rollbackHooks(hashSet, e);
            throw e;
        }
    }

    public static Set<XC_MethodHook.Unhook> hookAllMethods(Class<?> cls, String str, XC_MethodHook xC_MethodHook) {
        HashSet hashSet = new HashSet();
        try {
            for (Method method : cls.getDeclaredMethods()) {
                if (method.getName().equals(str)) {
                    hashSet.add(hookMethod(method, xC_MethodHook));
                }
            }
            return hashSet;
        } catch (Error | RuntimeException e) {
            rollbackHooks(hashSet, e);
            throw e;
        }
    }

    public static XC_MethodHook.Unhook hookMethod(Member member, XC_MethodHook xC_MethodHook) {
        f.a(member);
        if (xC_MethodHook == null) {
            f$$ExternalSyntheticBUOutline2.m("callback must not be null");
            return null;
        }
        d dVar = hookRegistry;
        b bVar = new b() { // from class: de.robv.android.xposed.XposedBridge$$ExternalSyntheticLambda0
            @Override // c.b
            public final Member a(Object obj, Member member2) {
                return XposedBridge.m2305$r8$lambda$10kFl2JjBfEwcKLdb607Wxxs(obj, member2);
            }
        };
        dVar.getClass();
        synchronized (dVar.f13a) {
            try {
                HookInfo hookInfo = (HookInfo) dVar.f13a.get(member);
                if (hookInfo != null) {
                    hookInfo.getCallbacks().add(xC_MethodHook);
                } else {
                    dVar.a(member, xC_MethodHook, bVar);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return xC_MethodHook.new Unhook(member);
    }

    public static <S, T extends S> boolean invokeConstructor(T t, Constructor<S> constructor, Object... objArr) {
        if (t == null) {
            f$$ExternalSyntheticBUOutline2.m("instance");
            return false;
        }
        if (constructor == null) {
            f$$ExternalSyntheticBUOutline2.m("constructor");
            return false;
        }
        if (constructor.isVarArgs()) {
            f$$ExternalSyntheticBUOutline1.m("varargs parameters are not supported");
            return false;
        }
        if (objArr.length == 0) {
            objArr = null;
        }
        c cVar = (c) bridge();
        cVar.getClass();
        cVar.a();
        return JniBridgeBindings.invokeConstructor0(t, constructor, objArr);
    }

    public static Object invokeOriginalMethod(Member member, Object obj, Object[] objArr) {
        HookInfo hookInfo;
        if (objArr == null) {
            objArr = EMPTY_ARRAY;
        }
        d dVar = hookRegistry;
        dVar.getClass();
        synchronized (dVar.f13a) {
            hookInfo = (HookInfo) dVar.f13a.get(member);
        }
        if (hookInfo != null) {
            return hookInfo.awaitBackupMethod().invoke(obj, objArr);
        }
        try {
            f.a(member);
            if (objArr == null) {
                objArr = e.f14a;
            }
            AccessibleObject accessibleObject = (AccessibleObject) member;
            if (!accessibleObject.isAccessible()) {
                accessibleObject.setAccessible(true);
            }
            return member instanceof Method ? ((Method) member).invoke(obj, objArr) : ((Constructor) member).newInstance(objArr);
        } catch (InstantiationException unused) {
            f$$ExternalSyntheticBUOutline1.m("The class this Constructor belongs to is abstract and cannot be instantiated");
            return null;
        }
    }

    public static boolean isHooked(Member member) {
        boolean zContainsKey;
        d dVar = hookRegistry;
        dVar.getClass();
        synchronized (dVar.f13a) {
            zContainsKey = dVar.f13a.containsKey(member);
        }
        return zContainsKey;
    }

    private static boolean isHooked0(Member member) {
        c cVar = (c) bridge();
        cVar.getClass();
        cVar.a();
        return JniBridgeBindings.isHooked0(member);
    }

    public static void log(Throwable th) {
        Log.e(TAG, "Uncaught Exception", th);
    }

    private static void rollbackHooks(Set<XC_MethodHook.Unhook> set, Throwable th) {
        Iterator<XC_MethodHook.Unhook> it = set.iterator();
        while (it.hasNext()) {
            try {
                it.next().unhook();
            } catch (Throwable th2) {
                if (th2 != th) {
                    th.addSuppressed(th2);
                }
            }
        }
    }

    @Deprecated
    public static void unhookMethod(Member member, XC_MethodHook xC_MethodHook) {
        d dVar = hookRegistry;
        final a aVarBridge = bridge();
        Objects.requireNonNull(aVarBridge);
        c.c cVar = new c.c() { // from class: de.robv.android.xposed.XposedBridge$$ExternalSyntheticLambda1
            @Override // c.c
            public final boolean a(Member member2) {
                return aVarBridge.a(member2);
            }
        };
        dVar.getClass();
        synchronized (dVar.f13a) {
            try {
                HookInfo hookInfo = (HookInfo) dVar.f13a.get(member);
                if (hookInfo == null) {
                    return;
                }
                hookInfo.getCallbacks().remove(xC_MethodHook);
                if (hookInfo.getCallbacks().size() == 0) {
                    dVar.f13a.remove(member);
                    cVar.a(member);
                }
                Unit unit = Unit.INSTANCE;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public static <T> T allocateInstance(Class<T> cls) {
        if (cls != null) {
            c cVar = (c) bridge();
            cVar.getClass();
            cVar.a();
            return (T) JniBridgeBindings.allocateInstance0(cls);
        }
        f$$ExternalSyntheticBUOutline2.m("clazz");
        return null;
    }

    public static boolean makeClassInheritable(Class<?> cls) {
        if (cls != null) {
            c cVar = (c) bridge();
            cVar.getClass();
            cVar.a();
            return JniBridgeBindings.makeClassInheritable0(cls);
        }
        f$$ExternalSyntheticBUOutline2.m("class must not be null");
        return false;
    }
}
