package com.android.dx;

import java.util.HashMap;
import java.util.Map;

public final class TypeId<T> {
    public static final TypeId<Boolean> BOOLEAN = new TypeId<>("Z");
    public static final TypeId<Byte> BYTE = new TypeId<>("B");
    public static final TypeId<Character> CHAR = new TypeId<>("C");
    public static final TypeId<Double> DOUBLE = new TypeId<>("D");
    public static final TypeId<Float> FLOAT = new TypeId<>("F");
    public static final TypeId<Integer> INT = new TypeId<>("I");
    public static final TypeId<Long> LONG = new TypeId<>("J");
    public static final TypeId<Short> SHORT = new TypeId<>("S");
    public static final TypeId<Void> VOID = new TypeId<>("V");
    public static final TypeId<Object> OBJECT = new TypeId<>("Ljava/lang/Object;");
    public static final TypeId<String> STRING = new TypeId<>("Ljava/lang/String;");

    private static final Map<Class<?>, TypeId<?>> PRIMITIVE_TO_TYPE = new HashMap<>();

    static {
        PRIMITIVE_TO_TYPE.put(Boolean.TYPE, BOOLEAN);
        PRIMITIVE_TO_TYPE.put(Byte.TYPE, BYTE);
        PRIMITIVE_TO_TYPE.put(Character.TYPE, CHAR);
        PRIMITIVE_TO_TYPE.put(Double.TYPE, DOUBLE);
        PRIMITIVE_TO_TYPE.put(Float.TYPE, FLOAT);
        PRIMITIVE_TO_TYPE.put(Integer.TYPE, INT);
        PRIMITIVE_TO_TYPE.put(Long.TYPE, LONG);
        PRIMITIVE_TO_TYPE.put(Short.TYPE, SHORT);
        PRIMITIVE_TO_TYPE.put(Void.TYPE, VOID);
    }

    final String name;

    public TypeId(String str) {
        this.name = str;
    }

    @SuppressWarnings("unchecked")
    public static <T> TypeId<T> get(String str) {
        return new TypeId<>(str);
    }

    @SuppressWarnings("unchecked")
    public static <T> TypeId<T> get(Class<T> cls) {
        if (cls.isPrimitive()) {
            return (TypeId<T>) PRIMITIVE_TO_TYPE.get(cls);
        }
        String strReplace = cls.getName().replace('.', '/');
        if (!cls.isArray()) {
            strReplace = "L" + strReplace + ';';
        }
        return get(strReplace);
    }

    public <V> FieldId<T, V> getField(TypeId<V> typeId, String str) {
        return new FieldId<>(this, typeId, str);
    }

    public MethodId<T, Void> getConstructor(TypeId<?>... typeIdArr) {
        return new MethodId<>(this, VOID, "<init>", typeIdArr);
    }

    public MethodId<T, Void> getStaticInitializer() {
        return new MethodId<>(this, VOID, "<clinit>", new TypeId<?>[0]);
    }

    public <R> MethodId<T, R> getMethod(TypeId<R> typeId, String str, TypeId<?>... typeIdArr) {
        return new MethodId<>(this, typeId, str, typeIdArr);
    }

    public String getName() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof TypeId) && ((TypeId<?>) obj).name.equals(this.name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
