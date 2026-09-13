package me.vkryl.core;
public final class BitwiseUtils {
    public static boolean hasFlag(int flags, int flag) { return (flags & flag) != 0; }
    public static boolean hasFlag(long flags, long flag) { return (flags & flag) != 0; }
    public static int setFlag(int flags, int flag, boolean value) {
        return value ? (flags | flag) : (flags & ~flag);
    }
}
