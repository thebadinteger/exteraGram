package com.android.dx;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class Code {
    public <T> Local<T> newLocal(TypeId<T> typeId) {
        return Local.get(this, typeId);
    }

    public <T> Local<T> getParameter(int i, TypeId<T> typeId) {
        return Local.get(this, typeId);
    }

    public <T> Local<T> getThis(TypeId<T> typeId) {
        return Local.get(this, typeId);
    }

    public void loadConstant(Local local, Object t) {}

    public void loadDeferredClassConstant(Local local, TypeId typeId) {}

    public void move(Local local, Local local2) {}

    public void compare(Comparison comparison, Label label, Local local, Local local2) {}

    public void compareZ(Comparison comparison, Label label, Local local) {}

    public void iget(Object fieldId, Local local, Local local2) {}

    public void iput(Object fieldId, Local local, Local local2) {}

    public void sget(Object fieldId, Local local) {}

    public void sput(Object fieldId, Local local) {}

    public void newInstance(Local local, MethodId methodId, Local... localArr) {}

    public void invokeStatic(MethodId methodId, Local local, Local... localArr) {}

    public void invokeVirtual(MethodId methodId, Local local, Local local2, Local... localArr) {}

    public void invokeDirect(MethodId methodId, Local local, Local local2, Local... localArr) {}

    public void invokeSuper(MethodId methodId, Local local, Local local2, Local... localArr) {}

    public void invokeInterface(MethodId methodId, Local local, Local local2, Local... localArr) {}

    public void instanceOfType(Local local, Local local2, TypeId typeId) {}

    public void cast(Local local, Local local2) {}

    public void arrayLength(Local local, Local local2) {}

    public void newArray(Local local, Local local2) {}

    public void aget(Local local, Local local2, Local local3) {}

    public void aput(Local local, Local local2, Local local3) {}

    public void returnVoid() {}

    public void returnValue(Local local) {}

    public void mark(Label label) {}

    public void jump(Label label) {}
}
