package com.chaquo.python;

import java.util.Objects;

public class Python {
    private static boolean failed;
    private static Python instance = new Python();
    private static Platform platform;

    public static class Platform {
        public String getPath() {
            return null;
        }

        public void onStart(Python python) {
        }
    }

    private native long getModuleNative(String str);

    private static native void startNative(Platform platform2, String str);

    public static synchronized Python getInstance() {
        try {
            if (!isStarted()) {
                start(new GenericPlatform());
            }
        } catch (Throwable th) {
            throw th;
        }
        return instance;
    }

    public static synchronized void start(Platform platform2) {
        try {
            if (isStarted()) {
                throw new IllegalStateException("Python already started");
            }
            if (failed) {
                throw new IllegalStateException("Python startup previously failed, and cannot be retried");
            }
            try {
                startNative(platform2, platform2.getPath());
                platform2.onStart(instance);
                platform = platform2;
            } catch (Throwable th) {
                failed = true;
                throw th;
            }
        } catch (Throwable th2) {
            throw th2;
        }
    }

    public static synchronized Platform getPlatform() {
        return platform;
    }

    public static synchronized boolean isStarted() {
        return platform != null;
    }

    private Python() {
    }

    public PyObject getModule(String str) {
        PyObject pyObject = PyObject.getInstance(getModuleNative(str));
        Objects.requireNonNull(pyObject);
        return pyObject;
    }

    public PyObject getBuiltins() {
        return getModule("builtins");
    }
}
