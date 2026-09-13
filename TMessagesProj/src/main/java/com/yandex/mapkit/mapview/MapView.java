package com.yandex.mapkit.mapview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import com.yandex.mapkit.map.Map;
import com.yandex.mapkit.map.MapWindow;

public class MapView extends RelativeLayout {
    public MapView(Context context) {
        super(context);
    }
    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public Map getMap() { return null; }
    public MapWindow getMapWindow() { return null; }
    public void onStart() {}
    public void onStop() {}
    public void setNoninteractive(boolean z) {}
}
