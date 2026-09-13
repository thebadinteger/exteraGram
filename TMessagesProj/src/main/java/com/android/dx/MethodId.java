package com.android.dx;

import java.util.Arrays;
import java.util.List;

public final class MethodId<D, R> {
    final TypeId<D> declaringType;
    final TypeId<R> returnType;
    final String name;
    final TypeId<?>[] parameters;

    public MethodId(TypeId<D> declaringType, TypeId<R> returnType, String name, TypeId<?>... parameters) {
        this.declaringType = declaringType;
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
    }

    public TypeId<D> getDeclaringType() {
        return this.declaringType;
    }

    public TypeId<R> getReturnType() {
        return this.returnType;
    }

    public String getName() {
        return this.name;
    }

    public List<TypeId<?>> getParameters() {
        return Arrays.asList(this.parameters);
    }
}
