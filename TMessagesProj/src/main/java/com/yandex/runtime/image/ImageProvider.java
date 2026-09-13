package com.yandex.runtime.image;

import android.graphics.Bitmap;

public abstract class ImageProvider {
    public static ImageProvider fromBitmap(Bitmap bitmap) {
        return null;
    }

    public static ImageProvider fromResource(android.content.Context context, int resId) {
        return null;
    }
}
