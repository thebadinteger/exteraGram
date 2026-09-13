package com.yandex.mapkit.user_location;

public interface UserLocationLayer {
    void setObjectListener(UserLocationObjectListener userLocationObjectListener);
    void setVisible(boolean z);
    void setHeadingModeActive(boolean z);
    void setAutoZoomEnabled(boolean z);
    void resetAnchor();
}
