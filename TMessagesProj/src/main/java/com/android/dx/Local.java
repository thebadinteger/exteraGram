package com.android.dx;

public final class Local<T> {
    private final Code code;
    final TypeId<T> type;

    private Local(Code code, TypeId<T> typeId) {
        this.code = code;
        this.type = typeId;
    }

    public static <T> Local<T> get(Code code, TypeId<T> typeId) {
        return new Local<>(code, typeId);
    }

    public TypeId<T> getType() {
        return this.type;
    }
}
