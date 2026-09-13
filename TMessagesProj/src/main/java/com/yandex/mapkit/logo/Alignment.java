package com.yandex.mapkit.logo;

public class Alignment {
    private HorizontalAlignment horizontalAlignment;
    private VerticalAlignment verticalAlignment;

    public Alignment() {}
    public Alignment(HorizontalAlignment h, VerticalAlignment v) {
        this.horizontalAlignment = h;
        this.verticalAlignment = v;
    }
    public HorizontalAlignment getHorizontalAlignment() { return this.horizontalAlignment; }
    public VerticalAlignment getVerticalAlignment() { return this.verticalAlignment; }
}
