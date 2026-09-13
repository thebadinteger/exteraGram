package com.sun.jna;

public class FromNativeContext {
    private Class<?> type;
    public FromNativeContext(Class<?> type) {
        this.type = type;
    }
    public Class<?> getTargetType() {
        return type;
    }
}
