package com.yandex.mapkit.logo;

public class Padding {
    private int horizontalPadding;
    private int verticalPadding;

    public Padding() {}
    public Padding(int h, int v) {
        this.horizontalPadding = h;
        this.verticalPadding = v;
    }
    public int getHorizontalPadding() { return this.horizontalPadding; }
    public int getVerticalPadding() { return this.verticalPadding; }
}
