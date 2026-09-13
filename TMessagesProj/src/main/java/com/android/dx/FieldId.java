package com.android.dx;

public final class FieldId<D, V> {
    final TypeId<D> declaringType;
    final TypeId<V> type;
    final String name;

    public FieldId(TypeId<D> declaringType, TypeId<V> type, String name) {
        this.declaringType = declaringType;
        this.type = type;
        this.name = name;
    }

    public TypeId<D> getDeclaringType() {
        return this.declaringType;
    }

    public TypeId<V> getType() {
        return this.type;
    }

    public String getName() {
        return this.name;
    }
}
