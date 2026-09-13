package dev.exterahook.runtime.bridge;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public final class JniBridgeBindings {
    private JniBridgeBindings() {
    }

    public static void loadLibrary(String name) {
        System.loadLibrary(name);
    }

    public static native Object allocateInstance0(Class<?> cls);

    public static native boolean deoptimize0(Member member);

    public static boolean disableHiddenApiRestrictions() {
        return disableHiddenApiRestrictions0();
    }

    public static native boolean disableHiddenApiRestrictions0();

    public static native boolean disableProfileSaver0();

    public static native Method hook0(Object obj, Member member, Method method);

    public static native boolean initialize0();

    public static native boolean invokeConstructor0(Object obj, Constructor<?> constructor, Object[] objArr);

    public static native boolean isHooked0(Member member);

    public static native boolean makeClassInheritable0(Class<?> cls);

    public static native boolean unhook0(Member member);
}
