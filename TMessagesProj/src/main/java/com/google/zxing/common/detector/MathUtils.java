package com.google.zxing.common.detector;
public final class MathUtils {
    public static int round(float f) {
        return (int) (f + (f < 0.0f ? -0.5f : 0.5f));
    }
    public static float distance(float aX, float aY, float bX, float bY) {
        float xDiff = aX - bX;
        float yDiff = aY - bY;
        return (float) Math.sqrt(xDiff * xDiff + yDiff * yDiff);
    }
    public static float distance(int aX, int aY, int bX, int bY) {
        return distance((float)aX, (float)aY, (float)bX, (float)bY);
    }
}
