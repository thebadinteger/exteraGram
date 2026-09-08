package de.robv.android.xposed.callbacks;

public abstract class XCallback implements Comparable<XCallback> {
    public static final int PRIORITY_DEFAULT = 50;
    public static final int PRIORITY_HIGHEST = 10000;
    public static final int PRIORITY_LOWEST = -10000;
    public final int priority;

    public static abstract class Param {
    }

    @Deprecated
    public XCallback() {
        this.priority = 50;
    }

    @Override // java.lang.Comparable
    public int compareTo(XCallback xCallback) {
        if (this == xCallback) {
            return 0;
        }
        int i = xCallback.priority;
        int i2 = this.priority;
        if (i != i2) {
            return Integer.compare(i, i2);
        }
        return System.identityHashCode(this) < System.identityHashCode(xCallback) ? -1 : 1;
    }

    public XCallback(int i) {
        this.priority = i;
    }
}
