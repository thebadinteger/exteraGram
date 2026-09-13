package com.caverock.androidsvg;

public class SVG {
    public static SVG getFromString(String svg) throws Exception { return new SVG(); }
    public static SVG getFromInputStream(java.io.InputStream is) throws Exception { return new SVG(); }
    public void renderToCanvas(android.graphics.Canvas canvas) {}
    public void setDocumentWidth(float w) {}
    public void setDocumentHeight(float h) {}
    public float getDocumentWidth() { return 0f; }
    public float getDocumentHeight() { return 0f; }
    public android.graphics.RectF getDocumentViewBox() { return new android.graphics.RectF(); }
}
