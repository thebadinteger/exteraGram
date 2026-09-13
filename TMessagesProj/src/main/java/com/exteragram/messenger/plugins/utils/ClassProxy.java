package com.exteragram.messenger.plugins.utils;

import java.util.Arrays;

import androidx.annotation.Keep;
import com.android.dx.Code;
import com.android.dx.Comparison;
import com.android.dx.DexMaker;
import com.android.dx.FieldId;
import com.android.dx.Label;
import com.android.dx.Local;
import com.android.dx.MethodId;
import com.android.dx.TypeId;
import com.exteragram.messenger.utils.RecordTag;
import com.chaquo.python.PyObject;
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

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v10 */
    /* JADX WARN: Type inference failed for: r1v11 */
    /* JADX WARN: Type inference failed for: r1v17, types: [com.android.dx.DexMaker] */
    /* JADX WARN: Type inference failed for: r1v18 */
    /* JADX WARN: Type inference failed for: r1v19, types: [com.android.dx.DexMaker] */
    /* JADX WARN: Type inference failed for: r1v25 */
    /* JADX WARN: Type inference failed for: r1v6 */
    /* JADX WARN: Type inference failed for: r1v7, types: [com.android.dx.DexMaker] */
    /* JADX WARN: Type inference failed for: r21v0, types: [com.exteragram.messenger.plugins.utils.ClassProxy$DexMakerHook] */
    /* JADX WARN: Type inference failed for: r22v1, types: [com.android.dx.DexMaker] */
    /* JADX WARN: Type inference failed for: r2v0, types: [com.android.dx.DexMaker] */
    /* JADX WARN: Type inference failed for: r2v1, types: [com.android.dx.DexMaker] */
    /* JADX WARN: Type inference failed for: r2v13 */
    private static Class<?> createProxyClassInternal(Class<?> cls, Utilities.Callback3Return<Object, String, Object[], Object> callback3Return, DexMakerHook dexMakerHook, List<Class<?>> list, List<Method> list2, List<Constructor<?>> list3, List<ProxyMethodSpec> list4, List<FieldSpec> list5, String str, boolean z) {
        Class<?> clsLoadClass;
        FieldId fieldId;
        DexMaker r1 = null;
        TypeId typeId;
        LinkedHashMap linkedHashMap;
        Iterator<ProxyMethodSpec> it;
        DexMaker dexMaker = new DexMaker();
        String simpleName = cls.getSimpleName();
        if (simpleName.isEmpty()) {
            simpleName = "Anonymous";
        }
        String strSanitizeProxyClassSegment = sanitizeProxyClassSegment(str);
        StringBuilder sb = new StringBuilder();
        sb.append("com.exteragram.messenger.plugins.Proxy_");
        sb.append(simpleName);
        sb.append(strSanitizeProxyClassSegment != null ? "_" + strSanitizeProxyClassSegment : "");
        sb.append("_");
        sb.append(Math.abs(cls.getName().hashCode()));
        sb.append("_");
        sb.append(Math.abs(new Random().nextInt()));
        String string = sb.toString();
        TypeId typeId2 = TypeId.get("L" + string.replace('.', '/') + ";");
        TypeId typeId3 = TypeId.get(cls);
        FieldId field = typeId2.getField(TypeId.get(Utilities.Callback3Return.class), "staticHandler");
        TypeId<?>[] interfaceTypeIds = toInterfaceTypeIds(list);
        ArrayList arrayList = new ArrayList();
        Collections.addAll(arrayList, interfaceTypeIds);
        dexMaker.declare(typeId2, "GeneratedByExteraProxy.java", 1, typeId3, interfaceTypeIds);
        dexMaker.declare(field, 9, null);
        if (list5 != null) {
            for (FieldSpec fieldSpec : list5) {
                if (fieldSpec != null && fieldSpec.name != null && fieldSpec.type != null) {
                    dexMaker.declare(typeId2.getField(TypeId.get(fieldSpec.type), fieldSpec.name), fieldSpec.modifiers, Modifier.isStatic(fieldSpec.modifiers) ? defaultValueForType(fieldSpec.type) : null);
                    if (fieldSpec.methods != null) {
                        for (FieldMethodSpec fieldMethodSpec : fieldSpec.methods) {
                            if (fieldMethodSpec != null && fieldMethodSpec.name != null) {
                                generateFieldAccessorMethod(dexMaker, typeId2, fieldSpec, fieldMethodSpec);
                            }
                        }
                    }
                }
            }
        }
        for (Constructor<?> constructor : list3 != null ? (Constructor[]) list3.toArray(new Constructor[0]) : cls.getDeclaredConstructors()) {
            if (!Modifier.isPrivate(constructor.getModifiers())) {
                generateConstructor(dexMaker, typeId2, typeId3, constructor, field);
            }
        }
        Map<String, Method> allOverridableMethods = getAllOverridableMethods(cls, list);
        LinkedHashMap<String, Method> linkedHashMap2 = new LinkedHashMap<>();
        if (z && list2 == null && (list4 == null || list4.isEmpty())) {
            linkedHashMap2.putAll(allOverridableMethods);
        }
        if (list2 != null) {
            for (Method method : list2) {
                if (method != null) {
                    linkedHashMap2.put(buildMethodSignature(method.getName(), method.getParameterTypes()), method);
                }
            }
        }
        ArrayList arrayList2 = new ArrayList();
        LinkedHashMap linkedHashMap3 = new LinkedHashMap();
        if (list4 != null) {
            Iterator<ProxyMethodSpec> it2 = list4.iterator();
            while (it2.hasNext()) {
                ProxyMethodSpec next = it2.next();
                if (next != null && next.name != null) {
                    if (next.overrideExisting) {
                        if (next.parameterTypes == null) {
                            for (Method method2 : allOverridableMethods.values()) {
                                Iterator<ProxyMethodSpec> it3 = it2;
                                if (next.name.equals(method2.getName())) {
                                    String strBuildMethodSignature = buildMethodSignature(method2.getName(), method2.getParameterTypes());
                                    linkedHashMap2.put(strBuildMethodSignature, method2);
                                    if (next.isMvel()) {
                                        linkedHashMap3.put(strBuildMethodSignature, next);
                                    }
                                }
                                it2 = it3;
                            }
                            it = it2;
                        } else {
                            it = it2;
                            Method method3 = allOverridableMethods.get(buildMethodSignature(next.name, next.parameterTypes));
                            if (method3 == null) {
                                throw new IllegalArgumentException("Unable to resolve overridable method " + next.name);
                            }
                            String strBuildMethodSignature2 = buildMethodSignature(method3.getName(), method3.getParameterTypes());
                            linkedHashMap2.put(strBuildMethodSignature2, method3);
                            if (next.isMvel()) {
                                linkedHashMap3.put(strBuildMethodSignature2, next);
                            }
                        }
                    } else {
                        it = it2;
                        arrayList2.add(next);
                    }
                    it2 = it;
                }
            }
        }
        DexMaker r2 = dexMaker;
        for (Map.Entry entry : linkedHashMap2.entrySet()) {
            Method method4 = (Method) entry.getValue();
            generateProxyMethod(r2, typeId2, method4, field, true);
            ProxyMethodSpec proxyMethodSpec = (ProxyMethodSpec) linkedHashMap3.get(entry.getKey());
            if (proxyMethodSpec != null && proxyMethodSpec.isMvel()) {
                r1 = r2;
                typeId = typeId2;
                linkedHashMap = linkedHashMap3;
                generateMvelMethod(r1, typeId, method4.getName(), method4.getReturnType(), method4.getParameterTypes(), 1, proxyMethodSpec.mvelCode, proxyMethodSpec.argumentNames);
            } else {
                r1 = r2;
                typeId = typeId2;
                linkedHashMap = linkedHashMap3;
                generateProxyMethod(r1, typeId, method4, field, false);
            }
            typeId2 = typeId;
            linkedHashMap3 = linkedHashMap;
            r2 = r1;
        }
        DexMaker r3 = r2;
        TypeId typeId4 = typeId2;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            int i2 = i + 1;
            ProxyMethodSpec proxyMethodSpec2 = (ProxyMethodSpec) arrayList2.get(i);
            if (proxyMethodSpec2.isMvel()) {
                generateMvelMethod(r3, typeId4, proxyMethodSpec2.name, proxyMethodSpec2.returnType != null ? proxyMethodSpec2.returnType : Void.TYPE, proxyMethodSpec2.parameterTypes != null ? proxyMethodSpec2.parameterTypes : new Class[0], proxyMethodSpec2.modifiers != 0 ? proxyMethodSpec2.modifiers : 1, proxyMethodSpec2.mvelCode, proxyMethodSpec2.argumentNames);
                fieldId = field;
            } else {
                DexMaker r22 = r3;
                TypeId typeId5 = typeId4;
                FieldId fieldId2 = field;
                generateHandlerMethod(r22, typeId5, proxyMethodSpec2.name, proxyMethodSpec2.returnType != null ? proxyMethodSpec2.returnType : Void.TYPE, proxyMethodSpec2.parameterTypes != null ? proxyMethodSpec2.parameterTypes : new Class[0], proxyMethodSpec2.modifiers != 0 ? proxyMethodSpec2.modifiers : 1, fieldId2);
                r3 = r22;
                typeId4 = typeId5;
                fieldId = fieldId2;
            }
            field = fieldId;
            i = i2;
            r3 = r3;
        }
        if (dexMakerHook != null) {
            dexMakerHook.apply(r3, typeId4, typeId3, arrayList);
        }
        byte[] bArrGenerate = r3.generate();
        synchronized (generatedClassLoaderLock) {
            try {
                ClassLoader classLoader = sharedGeneratedClassLoader;
                if (classLoader == null) {
                    classLoader = ApplicationLoader.applicationContext.getClassLoader();
                }
                InMemoryDexClassLoader inMemoryDexClassLoaderM = new InMemoryDexClassLoader(ByteBuffer.wrap(bArrGenerate), classLoader);
                clsLoadClass = inMemoryDexClassLoaderM.loadClass(string);
                sharedGeneratedClassLoader = inMemoryDexClassLoaderM;
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            Field declaredField = clsLoadClass.getDeclaredField("staticHandler");
            declaredField.setAccessible(true);
            declaredField.set(null, callback3Return);
            return clsLoadClass;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String sanitizeProxyClassSegment(String str) {
        if (str == null) {
            return null;
        }
        String strTrim = str.trim();
        if (strTrim.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder(strTrim.length());
        for (int i = 0; i < strTrim.length(); i++) {
            char cCharAt = strTrim.charAt(i);
            if (Character.isLetterOrDigit(cCharAt) || cCharAt == '_') {
                sb.append(cCharAt);
            } else {
                sb.append('_');
            }
        }
        return sb.toString();
    }

    private static TypeId<?>[] toInterfaceTypeIds(List<Class<?>> list) {
        if (list == null || list.isEmpty()) {
            return new TypeId[0];
        }
        ArrayList arrayList = new ArrayList();
        for (Class<?> cls : list) {
            if (cls != null) {
                if (!cls.isInterface()) {
                    throw new IllegalArgumentException("Expected interface, got " + cls.getName());
                }
                arrayList.add(TypeId.get(cls));
            }
        }
        return (TypeId[]) arrayList.toArray(new TypeId[0]);
    }

    private static Map<String, Method> getAllOverridableMethods(Class<?> cls, List<Class<?>> list) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        collectOverridableMethods(cls, linkedHashMap);
        if (list != null) {
            Iterator<Class<?>> it = list.iterator();
            while (it.hasNext()) {
                collectOverridableMethods(it.next(), linkedHashMap);
            }
        }
        return linkedHashMap;
    }

    private static void collectOverridableMethods(Class<?> cls, Map<String, Method> map) {
        if (cls == null || cls == Object.class) {
            return;
        }
        for (Method method : cls.getDeclaredMethods()) {
            int modifiers = method.getModifiers();
            if (!Modifier.isStatic(modifiers) && !Modifier.isFinal(modifiers) && (Modifier.isPublic(modifiers) || Modifier.isProtected(modifiers))) {
                String strBuildMethodSignature = buildMethodSignature(method.getName(), method.getParameterTypes());
                if (!map.containsKey(strBuildMethodSignature)) {
                    map.put(strBuildMethodSignature, method);
                }
            }
        }
        for (Class<?> cls2 : cls.getInterfaces()) {
            collectOverridableMethods(cls2, map);
        }
        collectOverridableMethods(cls.getSuperclass(), map);
    }

    private static String buildMethodSignature(String str, Class<?>[] clsArr) {
        StringBuilder sb = new StringBuilder(str);
        if (clsArr != null) {
            for (Class<?> cls : clsArr) {
                sb.append(':');
                sb.append(cls.getName());
            }
        }
        return sb.toString();
    }

    private static Object defaultValueForType(Class<?> cls) {
        if (!cls.isPrimitive()) {
            return null;
        }
        if (cls == Boolean.TYPE) {
            return Boolean.FALSE;
        }
        if (cls == Long.TYPE) {
            return 0L;
        }
        if (cls == Double.TYPE) {
            return Double.valueOf(0.0d);
        }
        if (cls == Float.TYPE) {
            return Float.valueOf(0.0f);
        }
        if (cls == Short.TYPE) {
            return (short) 0;
        }
        if (cls == Byte.TYPE) {
            return (byte) 0;
        }
        return cls == Character.TYPE ? (char) 0 : 0;
    }

    private static void generateConstructor(DexMaker dexMaker, TypeId<?> typeId, TypeId<?> typeId2, Constructor<?> constructor, FieldId fieldId) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        TypeId<?>[] typeIdArr = new TypeId[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            typeIdArr[i] = TypeId.get(parameterTypes[i]);
        }
        Code codeDeclare = dexMaker.declare(typeId.getConstructor(typeIdArr), 1);
        TypeId typeId3 = TypeId.get(Utilities.Callback3Return.class);
        TypeId<Object> typeId4 = TypeId.OBJECT;
        MethodId method = typeId3.getMethod(typeId4, "run", typeId4, typeId4, typeId4);
        Local local = codeDeclare.getThis(typeId);
        Local localNewLocal = codeDeclare.newLocal(typeId3);
        Local localNewLocal2 = codeDeclare.newLocal(TypeId.STRING);
        Local<?> localNewLocal3 = codeDeclare.newLocal(TypeId.get(Object[].class));
        Local localNewLocal4 = codeDeclare.newLocal(typeId4);
        Local<?> localNewLocal5 = codeDeclare.newLocal(typeId4);
        Local<?> localNewLocal6 = codeDeclare.newLocal(TypeId.get(Object[].class));
        TypeId<Integer> typeId5 = TypeId.INT;
        Local<Integer> localNewLocal7 = codeDeclare.newLocal(typeId5);
        Local<Integer> localNewLocal8 = codeDeclare.newLocal(typeId5);
        Local<?>[] localArr = new Local[parameterTypes.length];
        Local<?> localNewLocal9 = codeDeclare.newLocal(typeId4);
        for (int i2 = 0; i2 < parameterTypes.length; i2++) {
            localArr[i2] = codeDeclare.newLocal(TypeId.OBJECT);
        }
        Local<Integer> localNewLocal10 = codeDeclare.newLocal(TypeId.INT);
        Local<?>[] localArr2 = new Local[parameterTypes.length];
        for (int i3 = 0; i3 < parameterTypes.length; i3++) {
            localArr2[i3] = codeDeclare.newLocal(typeIdArr[i3]);
        }
        codeDeclare.loadConstant(localNewLocal10, 0);
        codeDeclare.newArray(localNewLocal3, localNewLocal10);
        codeDeclare.move(localNewLocal6, localNewLocal3);
        codeDeclare.sget(fieldId, localNewLocal);
        codeDeclare.loadConstant(localNewLocal2, buildMethodSignature("<constructor>", parameterTypes));
        codeDeclare.loadConstant(localNewLocal4, null);
        codeDeclare.loadConstant(localNewLocal8, 0);
        codeDeclare.loadConstant(localNewLocal9, null);
        for (int i4 = 0; i4 < parameterTypes.length; i4++) {
            codeDeclare.move(localArr2[i4], codeDeclare.getParameter(i4, typeIdArr[i4]));
        }
        codeDeclare.loadConstant(localNewLocal7, Integer.valueOf(parameterTypes.length));
        codeDeclare.newArray(localNewLocal3, localNewLocal7);
        for (int i5 = 0; i5 < parameterTypes.length; i5++) {
            TypeId typeId6 = TypeId.get(TypeHelper.class);
            TypeId<?> typeId7 = TypeId.OBJECT;
            codeDeclare.invokeStatic(typeId6.getMethod(typeId7, "box", parameterTypes[i5].isPrimitive() ? typeIdArr[i5] : typeId7), localArr[i5], localArr2[i5]);
            codeDeclare.loadConstant(localNewLocal8, Integer.valueOf(i5));
            codeDeclare.aput(localNewLocal3, localNewLocal8, localArr[i5]);
        }
        codeDeclare.invokeInterface(method, localNewLocal5, localNewLocal, localNewLocal4, localNewLocal2, localNewLocal3);
        Label label = new Label();
        codeDeclare.compare(Comparison.EQ, label, localNewLocal5, localNewLocal4);
        codeDeclare.cast(localNewLocal6, localNewLocal5);
        for (int i6 = 0; i6 < parameterTypes.length; i6++) {
            codeDeclare.loadConstant(localNewLocal8, Integer.valueOf(i6));
            codeDeclare.aget(localNewLocal9, localNewLocal6, localNewLocal8);
            if (parameterTypes[i6].isPrimitive()) {
                codeDeclare.invokeStatic(TypeId.get(TypeHelper.class).getMethod(typeIdArr[i6], "unbox" + capitalize(parameterTypes[i6].getName()), TypeId.OBJECT), localArr2[i6], localNewLocal9);
            } else {
                codeDeclare.cast(localArr2[i6], localNewLocal9);
            }
        }
        codeDeclare.mark(label);
        codeDeclare.invokeDirect(typeId2.getConstructor(typeIdArr), null, local, localArr2);
        codeDeclare.invokeInterface(method, null, localNewLocal, local, localNewLocal2, localNewLocal3);
        codeDeclare.returnVoid();
    }

    private static void generateProxyMethod(DexMaker dexMaker, TypeId<?> typeId, Method method, FieldId fieldId, boolean z) {
        Class<?>[] parameterTypes = method.getParameterTypes();
        TypeId<?>[] typeIdArr = new TypeId[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            typeIdArr[i] = TypeId.get(parameterTypes[i]);
        }
        TypeId<?> typeId2 = TypeId.get(method.getReturnType());
        String name = z ? "SUPER_" + method.getName() : method.getName();
        if (z) {
            Code codeDeclare = dexMaker.declare(typeId.getMethod(typeId2, name, typeIdArr), 1);
            Local local = codeDeclare.getThis(typeId);
            Local<?> localNewLocal = typeId2.equals(TypeId.VOID) ? null : codeDeclare.newLocal(typeId2);
            Local<?>[] localArr = new Local[parameterTypes.length];
            for (int i2 = 0; i2 < parameterTypes.length; i2++) {
                localArr[i2] = codeDeclare.getParameter(i2, typeIdArr[i2]);
            }
            codeDeclare.invokeSuper(TypeId.get(method.getDeclaringClass()).getMethod(typeId2, method.getName(), typeIdArr), localNewLocal, local, localArr);
            if (typeId2.equals(TypeId.VOID)) {
                codeDeclare.returnVoid();
                return;
            } else {
                codeDeclare.returnValue(localNewLocal);
                return;
            }
        }
        generateHandlerMethod(dexMaker, typeId, name, method.getReturnType(), parameterTypes, 1, fieldId);
    }

    private static void generateHandlerMethod(DexMaker dexMaker, TypeId<?> typeId, String str, Class<?> cls, Class<?>[] clsArr, int i, FieldId fieldId) {
        Class<?>[] clsArr2 = clsArr;
        TypeId<?>[] typeIdArr = new TypeId[clsArr2.length];
        for (int i2 = 0; i2 < clsArr2.length; i2++) {
            typeIdArr[i2] = TypeId.get(clsArr2[i2]);
        }
        TypeId typeId2 = TypeId.get(Utilities.Callback3Return.class);
        TypeId<Object> typeId3 = TypeId.OBJECT;
        MethodId method = typeId2.getMethod(typeId3, "run", typeId3, typeId3, typeId3);
        TypeId<?> typeId4 = TypeId.get(cls);
        Code codeDeclare = dexMaker.declare(typeId.getMethod(typeId4, str, typeIdArr), i);
        Local local = codeDeclare.getThis(typeId);
        Local<?> localNewLocal = !typeId4.equals(TypeId.VOID) ? codeDeclare.newLocal(typeId4) : null;
        Local localNewLocal2 = codeDeclare.newLocal(TypeId.get(Utilities.Callback3Return.class));
        Local localNewLocal3 = codeDeclare.newLocal(TypeId.STRING);
        Local<?> localNewLocal4 = codeDeclare.newLocal(TypeId.get(Object[].class));
        TypeId<Integer> typeId5 = TypeId.INT;
        Local<Integer> localNewLocal5 = codeDeclare.newLocal(typeId5);
        Local<?> localNewLocal6 = codeDeclare.newLocal(typeId3);
        Local<Integer> localNewLocal7 = codeDeclare.newLocal(typeId5);
        Local<?>[] localArr = new Local[clsArr2.length];
        for (int i3 = 0; i3 < clsArr2.length; i3++) {
            localArr[i3] = codeDeclare.newLocal(TypeId.OBJECT);
        }
        codeDeclare.sget(fieldId, localNewLocal2);
        codeDeclare.loadConstant(localNewLocal3, buildMethodSignature(str, clsArr2));
        codeDeclare.loadConstant(localNewLocal5, Integer.valueOf(clsArr2.length));
        codeDeclare.newArray(localNewLocal4, localNewLocal5);
        int i4 = 0;
        while (i4 < clsArr2.length) {
            Local parameter = codeDeclare.getParameter(i4, typeIdArr[i4]);
            TypeId typeId6 = TypeId.get(TypeHelper.class);
            TypeId<?> typeId7 = TypeId.OBJECT;
            codeDeclare.invokeStatic(typeId6.getMethod(typeId7, "box", clsArr2[i4].isPrimitive() ? typeIdArr[i4] : typeId7), localArr[i4], parameter);
            codeDeclare.loadConstant(localNewLocal7, Integer.valueOf(i4));
            codeDeclare.aput(localNewLocal4, localNewLocal7, localArr[i4]);
            i4++;
            clsArr2 = clsArr;
        }
        codeDeclare.invokeInterface(method, localNewLocal6, localNewLocal2, local, localNewLocal3, localNewLocal4);
        if (typeId4.equals(TypeId.VOID)) {
            codeDeclare.returnVoid();
            return;
        }
        if (cls.isPrimitive()) {
            codeDeclare.invokeStatic(TypeId.get(TypeHelper.class).getMethod(typeId4, "unbox" + capitalize(cls.getName()), TypeId.OBJECT), localNewLocal, localNewLocal6);
        } else {
            codeDeclare.cast(localNewLocal, localNewLocal6);
        }
        codeDeclare.returnValue(localNewLocal);
    }

    private static void generateMvelMethod(DexMaker dexMaker, TypeId<?> typeId, String str, Class<?> cls, Class<?>[] clsArr, int i, String str2, List<String> list) {
        Local<?> local;
        TypeId<?>[] typeIdArr = new TypeId[clsArr.length];
        for (int i2 = 0; i2 < clsArr.length; i2++) {
            typeIdArr[i2] = TypeId.get(clsArr[i2]);
        }
        TypeId<?> typeId2 = TypeId.get(cls);
        Code codeDeclare = dexMaker.declare(typeId.getMethod(typeId2, str, typeIdArr), i);
        Local<?> local2 = Modifier.isStatic(i) ? null : codeDeclare.getThis(typeId);
        TypeId<Object> typeId3 = TypeId.OBJECT;
        Local<?> localNewLocal = codeDeclare.newLocal(typeId3);
        Local localNewLocal2 = codeDeclare.newLocal(typeId3);
        Local<?> localNewLocal3 = codeDeclare.newLocal(typeId3);
        TypeId<String> typeId4 = TypeId.STRING;
        Local localNewLocal4 = codeDeclare.newLocal(typeId4);
        Local<?> localNewLocal5 = codeDeclare.newLocal(TypeId.get(Object[].class));
        Local<?> localNewLocal6 = codeDeclare.newLocal(TypeId.get(String[].class));
        TypeId<Integer> typeId5 = TypeId.INT;
        Local<Integer> localNewLocal7 = codeDeclare.newLocal(typeId5);
        Local<Integer> localNewLocal8 = codeDeclare.newLocal(typeId5);
        Local localNewLocal9 = codeDeclare.newLocal(typeId3);
        Local localNewLocal10 = codeDeclare.newLocal(TypeId.get(String[].class));
        Local<?> localNewLocal11 = codeDeclare.newLocal(typeId4);
        Local<?> localNewLocal12 = typeId2.equals(TypeId.VOID) ? null : codeDeclare.newLocal(typeId2);
        Local<?>[] localArr = new Local[clsArr.length];
        for (int i3 = 0; i3 < clsArr.length; i3++) {
            localArr[i3] = codeDeclare.newLocal(TypeId.OBJECT);
        }
        codeDeclare.loadConstant(localNewLocal9, null);
        codeDeclare.move(localNewLocal, localNewLocal9);
        codeDeclare.move(localNewLocal2, localNewLocal9);
        if (!Modifier.isStatic(i)) {
            Object field = typeId.getField(TypeId.OBJECT, "__extera_internal_python_peer__");
            codeDeclare.cast(localNewLocal, local2);
            codeDeclare.iget(field, localNewLocal2, local2);
        }
        codeDeclare.loadConstant(localNewLocal4, str2);
        codeDeclare.loadConstant(localNewLocal7, Integer.valueOf(clsArr.length));
        codeDeclare.newArray(localNewLocal5, localNewLocal7);
        int i4 = 0;
        while (i4 < clsArr.length) {
            Local parameter = codeDeclare.getParameter(i4, typeIdArr[i4]);
            TypeId typeId6 = TypeId.get(TypeHelper.class);
            TypeId<?> typeId7 = TypeId.OBJECT;
            int i5 = i4;
            codeDeclare.invokeStatic(typeId6.getMethod(typeId7, "box", clsArr[i4].isPrimitive() ? typeIdArr[i4] : typeId7), localArr[i5], parameter);
            codeDeclare.loadConstant(localNewLocal8, Integer.valueOf(i5));
            codeDeclare.aput(localNewLocal5, localNewLocal8, localArr[i5]);
            i4 = i5 + 1;
        }
        if (list != null && !list.isEmpty()) {
            codeDeclare.loadConstant(localNewLocal7, Integer.valueOf(list.size()));
            codeDeclare.newArray(localNewLocal6, localNewLocal7);
            for (int i6 = 0; i6 < list.size(); i6++) {
                codeDeclare.loadConstant(localNewLocal11, list.get(i6));
                codeDeclare.loadConstant(localNewLocal8, Integer.valueOf(i6));
                codeDeclare.aput(localNewLocal6, localNewLocal8, localNewLocal11);
            }
        } else {
            codeDeclare.loadConstant(localNewLocal10, null);
            codeDeclare.move(localNewLocal6, localNewLocal10);
        }
        TypeId typeId8 = TypeId.get(ClassProxy.class);
        TypeId<Object> typeId9 = TypeId.OBJECT;
        codeDeclare.invokeStatic(typeId8.getMethod(typeId9, "executeMvelMethod", typeId9, typeId9, TypeId.STRING, TypeId.get(String[].class), TypeId.get(Object[].class)), localNewLocal3, localNewLocal, localNewLocal2, localNewLocal4, localNewLocal6, localNewLocal5);
        if (typeId2.equals(TypeId.VOID)) {
            codeDeclare.returnVoid();
            return;
        }
        if (cls.isPrimitive()) {
            MethodId method = TypeId.get(TypeHelper.class).getMethod(typeId2, "unbox" + capitalize(cls.getName()), typeId9);
            Local<?>[] localArr2 = {localNewLocal3};
            local = localNewLocal12;
            codeDeclare.invokeStatic(method, local, localArr2);
        } else {
            local = localNewLocal12;
            codeDeclare.cast(local, localNewLocal3);
        }
        codeDeclare.returnValue(local);
    }

    private static void generateFieldAccessorMethod(DexMaker dexMaker, TypeId<?> typeId, FieldSpec fieldSpec, FieldMethodSpec fieldMethodSpec) {
        TypeId typeId2 = TypeId.get(fieldSpec.type);
        Object field = typeId.getField(typeId2, fieldSpec.name);
        boolean zIsStatic = Modifier.isStatic(fieldSpec.modifiers);
        int i = fieldMethodSpec.modifiers != 0 ? fieldMethodSpec.modifiers : 1;
        if (fieldMethodSpec.getter) {
            Code codeDeclare = dexMaker.declare(typeId.getMethod(typeId2, fieldMethodSpec.name, new TypeId[0]), i);
            Local<?> localNewLocal = codeDeclare.newLocal(typeId2);
            if (zIsStatic) {
                codeDeclare.sget(field, localNewLocal);
            } else {
                codeDeclare.iget(field, localNewLocal, codeDeclare.getThis(typeId));
            }
            codeDeclare.returnValue(localNewLocal);
            return;
        }
        Code codeDeclare2 = dexMaker.declare(typeId.getMethod(TypeId.VOID, fieldMethodSpec.name, typeId2), i);
        Local parameter = codeDeclare2.getParameter(0, typeId2);
        if (zIsStatic) {
            codeDeclare2.sput(field, parameter);
        } else {
            codeDeclare2.iput(field, codeDeclare2.getThis(typeId), parameter);
        }
        codeDeclare2.returnVoid();
    }

    public static Object executeMvelMethod(Object obj, Object obj2, String str, String[] strArr, Object[] objArr) {
        String str2;
        HashMap map = new HashMap();
        if (objArr == null) {
            objArr = new Object[0];
        }
        map.put("java", obj);
        map.put("python", obj2);
        map.put("py", obj2);
        map.put("self", obj2);
        map.put("args", objArr);
        map.put("argc", Integer.valueOf(objArr.length));
        for (int i = 0; i < objArr.length; i++) {
            Object obj3 = objArr[i];
            map.put("arg" + i, obj3);
            if (strArr != null && i < strArr.length && (str2 = strArr[i]) != null && !str2.isEmpty()) {
                map.put(str2, obj3);
            }
        }
        return MVEL.executeExpression(mvelExpressionCache.computeIfAbsent(str, new Function() { // from class: com.exteragram.messenger.plugins.utils.ClassProxy$$ExternalSyntheticLambda2
            @Override // java.util.function.Function
            public final Object apply(Object obj4) {
                return MVEL.compileExpression((String) obj4);
            }
        }), obj, map);
    }

    private static String capitalize(String str) {
        if (str.equals("int")) {
            return "Int";
        }
        if (str.equals("boolean")) {
            return "Bool";
        }
        if (str.equals("short")) {
            return "Short";
        }
        if (str.equals("byte")) {
            return "Byte";
        }
        if (str.equals("char")) {
            return "Char";
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static final class ProxyMethodSpec extends RecordTag {
        private final List<String> argumentNames;
        private final String implementation;
        private final int modifiers;
        private final String mvelCode;
        private final String name;
        private final boolean overrideExisting;
        private final Class<?>[] parameterTypes;
        private final Class<?> returnType;

        private /* synthetic */ boolean $record$equals(Object obj) {
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
            return Objects.hash(this.overrideExisting, this.modifiers, this.name, this.returnType, Arrays.hashCode(this.parameterTypes), this.implementation, this.mvelCode, this.argumentNames);
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
            return "ProxyMethodSpec[name=" + this.name + ", returnType=" + this.returnType + ", modifiers=" + this.modifiers + "]";
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
            return Objects.hash(this.modifiers, this.name, this.type, this.methods);
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
            return "FieldSpec[name=" + this.name + ", type=" + this.type + ", modifiers=" + this.modifiers + "]";
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
            return Objects.hash(this.getter, this.modifiers, this.name);
        }

        public int modifiers() {
            return this.modifiers;
        }

        public String name() {
            return this.name;
        }

        public final String toString() {
            return "FieldMethodSpec[name=" + this.name + ", modifiers=" + this.modifiers + ", getter=" + this.getter + "]";
        }
    }

    public static class TypeHelper {
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
