package com.exteragram.messenger.plugins.utils;

import androidx.annotation.Keep;
import com.android.dx.Code;
import com.android.dx.Comparison;
import com.android.dx.DexMaker;
import com.android.dx.FieldId;
import com.android.dx.Label;
import com.android.dx.Local;
import com.android.dx.MethodId;
import com.android.dx.TypeId;
import com.android.tools.r8.RecordTag;
import com.chaquo.python.PyObject;
import com.exteragram.messenger.ai.network.Client$ImagePayload$$ExternalSyntheticRecord1;
import com.exteragram.messenger.utils.AppUtils;
import dalvik.system.InMemoryDexClassLoader;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import org.lsposed.lsparanoid.Deobfuscator$exteraGramDev$TMessagesProj;
import org.mvel2.MVEL;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.Utilities;

public class ClassProxy {
    private static volatile ClassLoader sharedGeneratedClassLoader;
    private static final String PYTHON_PEER_FIELD_NAME = "__extera_internal_python_peer__";
    private static final Object[] EMPTY_ARGS = new Object[0];
    private static final ConcurrentHashMap<String, Serializable> mvelExpressionCache = new ConcurrentHashMap<>();
    private static final Object generatedClassLoaderLock = new Object();

    public interface DexMakerHook {
        void apply(DexMaker dexMaker, TypeId<?> typeId, TypeId<?> typeId2, List<TypeId<?>> list);
    }

    @Keep
    public static void runPythonRunnable(PyObject pyObject) {
        if (pyObject == null) {
            AppUtils.log("Runnable callback is null");
            return;
        }
        try {
            pyObject.call(EMPTY_ARGS);
        } catch (Exception e) {
            AppUtils.log("Error in runnable", e);
        }
    }

    public static Class<?> createProxyClass(Class<?> cls, Utilities.Callback3Return<Object, String, Object[], Object> callback3Return, List<Method> list, List<Constructor<?>> list2) {
        return createProxyClassInternal(cls, callback3Return, null, null, list, list2, null, null, null, true);
    }

    public static Class<?> createProxyClass(Class<?> cls, Utilities.Callback3Return<Object, String, Object[], Object> callback3Return, List<Method> list, List<Constructor<?>> list2, String str) {
        return createProxyClassInternal(cls, callback3Return, null, null, list, list2, null, null, str, true);
    }

    public static Class<?> createProxyClass(Class<?> cls, Utilities.Callback3Return<Object, String, Object[], Object> callback3Return, List<Method> list, List<Constructor<?>> list2, List<ProxyMethodSpec> list3, List<FieldSpec> list4) {
        return createProxyClassInternal(cls, callback3Return, null, null, list, list2, list3, list4, null, false);
    }

    public static Class<?> createProxyClass(Class<?> cls, Utilities.Callback3Return<Object, String, Object[], Object> callback3Return, List<Method> list, List<Constructor<?>> list2, List<ProxyMethodSpec> list3, List<FieldSpec> list4, String str) {
        return createProxyClassInternal(cls, callback3Return, null, null, list, list2, list3, list4, str, false);
    }

    public static Class<?> createProxyClass(Class<?> cls, Utilities.Callback3Return<Object, String, Object[], Object> callback3Return, DexMakerHook dexMakerHook, List<Class<?>> list, List<Method> list2, List<Constructor<?>> list3, List<ProxyMethodSpec> list4, List<FieldSpec> list5) {
        return createProxyClassInternal(cls, callback3Return, dexMakerHook, list, list2, list3, list4, list5, null, false);
    }

    public static Class<?> createProxyClass(Class<?> cls, Utilities.Callback3Return<Object, String, Object[], Object> callback3Return, DexMakerHook dexMakerHook, List<Class<?>> list, List<Method> list2, List<Constructor<?>> list3, List<ProxyMethodSpec> list4, List<FieldSpec> list5, String str) {
        return createProxyClassInternal(cls, callback3Return, dexMakerHook, list, list2, list3, list4, list5, str, false);
    }

    boolean $record$equals(Object obj) {
            if (!(obj instanceof ProxyMethodSpec)) {
                return false;
            }
            ProxyMethodSpec proxyMethodSpec = (ProxyMethodSpec) obj;
            return this.overrideExisting == proxyMethodSpec.overrideExisting && this.modifiers == proxyMethodSpec.modifiers && Objects.equals(this.name, proxyMethodSpec.name) && Objects.equals(this.returnType, proxyMethodSpec.returnType) && Objects.equals(this.parameterTypes, proxyMethodSpec.parameterTypes) && Objects.equals(this.implementation, proxyMethodSpec.implementation) && Objects.equals(this.mvelCode, proxyMethodSpec.mvelCode) && Objects.equals(this.argumentNames, proxyMethodSpec.argumentNames);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.name, this.returnType, this.parameterTypes, Integer.valueOf(this.modifiers), Boolean.valueOf(this.overrideExisting), this.implementation, this.mvelCode, this.argumentNames};
        }

        public ProxyMethodSpec(String str, Class<?> cls, Class<?>[] clsArr, int i, boolean z, String str2, String str3, List<String> list) {
            this.name = str;
            this.returnType = cls;
            this.parameterTypes = clsArr;
            this.modifiers = i;
            this.overrideExisting = z;
            this.implementation = str2;
            this.mvelCode = str3;
            this.argumentNames = list;
        }

        public List<String> argumentNames() {
            return this.argumentNames;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return ClassProxy$ProxyMethodSpec$$ExternalSyntheticRecord0.m(this.overrideExisting, this.modifiers, this.name, this.returnType, this.parameterTypes, this.implementation, this.mvelCode, this.argumentNames);
        }

        public String implementation() {
            return this.implementation;
        }

        public int modifiers() {
            return this.modifiers;
        }

        public String mvelCode() {
            return this.mvelCode;
        }

        public String name() {
            return this.name;
        }

        public boolean overrideExisting() {
            return this.overrideExisting;
        }

        public Class<?>[] parameterTypes() {
            return this.parameterTypes;
        }

        public Class<?> returnType() {
            return this.returnType;
        }

        public final String toString() {
            return Client$ImagePayload$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), ProxyMethodSpec.class, "name;returnType;parameterTypes;modifiers;overrideExisting;implementation;mvelCode;argumentNames");
        }

        public ProxyMethodSpec(String str, Class<?> cls, Class<?>[] clsArr, int i, boolean z) {
            this(str, cls, clsArr, i, z, "python", null, null);
        }

        public boolean isMvel() {
            return "mvel".equals(this.implementation) && this.mvelCode != null;
        }
    }

    public static final class FieldSpec extends RecordTag {
        private final List<FieldMethodSpec> methods;
        private final int modifiers;
        private final String name;
        private final Class<?> type;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof FieldSpec)) {
                return false;
            }
            FieldSpec fieldSpec = (FieldSpec) obj;
            return this.modifiers == fieldSpec.modifiers && Objects.equals(this.name, fieldSpec.name) && Objects.equals(this.type, fieldSpec.type) && Objects.equals(this.methods, fieldSpec.methods);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.name, this.type, Integer.valueOf(this.modifiers), this.methods};
        }

        public FieldSpec(String str, Class<?> cls, int i, List<FieldMethodSpec> list) {
            this.name = str;
            this.type = cls;
            this.modifiers = i;
            this.methods = list;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return ClassProxy$FieldSpec$$ExternalSyntheticRecord0.m(this.modifiers, this.name, this.type, this.methods);
        }

        public List<FieldMethodSpec> methods() {
            return this.methods;
        }

        public int modifiers() {
            return this.modifiers;
        }

        public String name() {
            return this.name;
        }

        public final String toString() {
            return Client$ImagePayload$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), FieldSpec.class, "name;type;modifiers;methods");
        }

        public Class<?> type() {
            return this.type;
        }

        public FieldSpec(String str, Class<?> cls, int i) {
            this(str, cls, i, null);
        }
    }

    public static final class FieldMethodSpec extends RecordTag {
        private final boolean getter;
        private final int modifiers;
        private final String name;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof FieldMethodSpec)) {
                return false;
            }
            FieldMethodSpec fieldMethodSpec = (FieldMethodSpec) obj;
            return this.getter == fieldMethodSpec.getter && this.modifiers == fieldMethodSpec.modifiers && Objects.equals(this.name, fieldMethodSpec.name);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.name, Integer.valueOf(this.modifiers), Boolean.valueOf(this.getter)};
        }

        public FieldMethodSpec(String str, int i, boolean z) {
            this.name = str;
            this.modifiers = i;
            this.getter = z;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public boolean getter() {
            return this.getter;
        }

        public final int hashCode() {
            return ClassProxy$FieldMethodSpec$$ExternalSyntheticRecord0.m(this.getter, this.modifiers, this.name);
        }

        public int modifiers() {
            return this.modifiers;
        }

        public String name() {
            return this.name;
        }

        public final String toString() {
            return Client$ImagePayload$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), FieldMethodSpec.class, "name;modifiers;getter");
        }
    }

    public class TypeHelper {
        public static Object box(Object obj) {
            return obj;
        }

        public TypeHelper() {
        }

        public static Object box(int i) {
            return Integer.valueOf(i);
        }

        public static Object box(boolean z) {
            return Boolean.valueOf(z);
        }

        public static Object box(long j) {
            return Long.valueOf(j);
        }

        public static Object box(double d) {
            return Double.valueOf(d);
        }

        public static Object box(float f) {
            return Float.valueOf(f);
        }

        public static Object box(short s) {
            return Short.valueOf(s);
        }

        public static Object box(byte b2) {
            return Byte.valueOf(b2);
        }

        public static Object box(char c2) {
            return Character.valueOf(c2);
        }

        public static int unboxInt(Object obj) {
            if (obj instanceof Number) {
                return ((Number) obj).intValue();
            }
            return 0;
        }

        public static boolean unboxBool(Object obj) {
            if (obj instanceof Boolean) {
                return ((Boolean) obj).booleanValue();
            }
            return false;
        }

        public static long unboxLong(Object obj) {
            if (obj instanceof Number) {
                return ((Number) obj).longValue();
            }
            return 0L;
        }

        public static double unboxDouble(Object obj) {
            if (obj instanceof Number) {
                return ((Number) obj).doubleValue();
            }
            return 0.0d;
        }

        public static float unboxFloat(Object obj) {
            if (obj instanceof Number) {
                return ((Number) obj).floatValue();
            }
            return 0.0f;
        }

        public static short unboxShort(Object obj) {
            if (obj instanceof Number) {
                return ((Number) obj).shortValue();
            }
            return (short) 0;
        }

        public static byte unboxByte(Object obj) {
            if (obj instanceof Number) {
                return ((Number) obj).byteValue();
            }
            return (byte) 0;
        }

        public static char unboxChar(Object obj) {
            if (obj instanceof Character) {
                return ((Character) obj).charValue();
            }
            return (char) 0;
        }
    }
}
