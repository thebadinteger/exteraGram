package com.sun.jna;

public interface NativeMapped {
    Class<?> nativeType();
    Object toNative();
    Object fromNative(Object nativeValue, FromNativeContext context);
}
