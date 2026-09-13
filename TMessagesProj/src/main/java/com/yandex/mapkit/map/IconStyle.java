package com.yandex.mapkit.map;

import android.graphics.PointF;

public final class IconStyle {
    private PointF anchor;
    private Float zIndex;
    private Boolean flat;

    public IconStyle() {}
    public PointF getAnchor() { return this.anchor; }
    public IconStyle setAnchor(PointF pointF) { this.anchor = pointF; return this; }
    public Float getZIndex() { return this.zIndex; }
    public IconStyle setZIndex(Float f) { this.zIndex = f; return this; }
    public Boolean getFlat() { return this.flat; }
    public IconStyle setFlat(Boolean flat) { this.flat = flat; return this; }
}
