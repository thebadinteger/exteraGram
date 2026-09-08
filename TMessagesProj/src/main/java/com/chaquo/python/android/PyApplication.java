package com.chaquo.python.android;

import android.app.Application;
import com.chaquo.python.Python;

public class PyApplication extends Application {
    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        Python.start(new AndroidPlatform(this));
    }
}
