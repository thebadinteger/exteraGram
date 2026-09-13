package com.sun.jna;

public abstract class PointerType implements NativeMapped {
    private Pointer pointer;

    public PointerType() {
        this.pointer = Pointer.NULL;
    }

    public PointerType(Pointer pointer) {
        this.pointer = pointer;
    }

    @Override
    public Class<?> nativeType() {
        return Pointer.class;
    }

    @Override
    public Object toNative() {
        return getPointer();
    }

    public Pointer getPointer() {
        return this.pointer;
    }

    public void setPointer(Pointer pointer) {
        this.pointer = pointer;
    }

    @Override
    public Object fromNative(Object obj, FromNativeContext context) {
        if (obj == null) {
            return null;
        }
        try {
            PointerType pt = getClass().getDeclaredConstructor().newInstance();
            pt.pointer = (Pointer) obj;
            return pt;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public int hashCode() {
        return pointer != null ? pointer.hashCode() : 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (!(obj instanceof PointerType)) return false;
        Pointer p = ((PointerType) obj).getPointer();
        return pointer != null ? pointer.equals(p) : p == null;
    }

    @Override
    public String toString() {
        return pointer == null ? "NULL" : pointer.toString() + " (" + super.toString() + ")";
    }
}
