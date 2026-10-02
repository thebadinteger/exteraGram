package com.exteragram.messenger.debug;

import com.exteragram.messenger.config.BasePref;
import com.exteragram.messenger.config.BooleanPref;
import com.exteragram.messenger.config.IntegerPref;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.MutablePropertyReference0Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
public abstract class DebugConfig {
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties;
    private static final BasePref chatFadeUseWhiteBackground$delegate;
    private static final BasePref debugCameraMetrics$delegate;
    private static final BasePref disableApiRequests$delegate;
    private static final BasePref disableChatFadeWallpaperBlend$delegate;
    private static final BasePref forceCompactSavedMusic$delegate;
    private static final BasePref heapMonitorEnabled$delegate;
    private static final BasePref heapMonitorLimitMb$delegate;
    private static final BasePref loadMonitorCpuPercent$delegate;
    private static final BasePref loadMonitorEnabled$delegate;
    private static final BasePref glassHeaderMenu$delegate;

    static {
        KProperty<?>[] kPropertyArr = {
                Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "debugCameraMetrics", "getDebugCameraMetrics()Z", 1)),
                Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "forceCompactSavedMusic", "getForceCompactSavedMusic()Z", 1)),
                Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "disableApiRequests", "getDisableApiRequests()Z", 1)),
                Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "disableChatFadeWallpaperBlend", "getDisableChatFadeWallpaperBlend()Z", 1)),
                Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "chatFadeUseWhiteBackground", "getChatFadeUseWhiteBackground()Z", 1)),
                Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "heapMonitorEnabled", "getHeapMonitorEnabled()Z", 1)),
                Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "heapMonitorLimitMb", "getHeapMonitorLimitMb()I", 1)),
                Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "loadMonitorEnabled", "getLoadMonitorEnabled()Z", 1)),
                Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "loadMonitorCpuPercent", "getLoadMonitorCpuPercent()I", 1)),
                Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "glassHeaderMenu", "getGlassHeaderMenu()Z", 1))
        };
        $$delegatedProperties = (KProperty<Object>[]) (KProperty<?>[]) kPropertyArr;
        debugCameraMetrics$delegate = new BooleanPref(false, null, 2, null).provideDelegate(null, kPropertyArr[0]);
        forceCompactSavedMusic$delegate = new BooleanPref(false, null, 2, null).provideDelegate(null, kPropertyArr[1]);
        disableApiRequests$delegate = new BooleanPref(false, null, 2, null).provideDelegate(null, kPropertyArr[2]);
        disableChatFadeWallpaperBlend$delegate = new BooleanPref(false, null, 2, null).provideDelegate(null, kPropertyArr[3]);
        chatFadeUseWhiteBackground$delegate = new BooleanPref(false, null, 2, null).provideDelegate(null, kPropertyArr[4]);
        heapMonitorEnabled$delegate = new BooleanPref(false, null, 2, null).provideDelegate(null, kPropertyArr[5]);
        heapMonitorLimitMb$delegate = new IntegerPref(256, null, 2, null).provideDelegate(null, kPropertyArr[6]);
        loadMonitorEnabled$delegate = new BooleanPref(false, null, 2, null).provideDelegate(null, kPropertyArr[7]);
        loadMonitorCpuPercent$delegate = new IntegerPref(5, null, 2, null).provideDelegate(null, kPropertyArr[8]);
        glassHeaderMenu$delegate = new BooleanPref(false, null, 2, null).provideDelegate(null, kPropertyArr[9]);
    }

    public static final boolean getDebugCameraMetrics() {
        return ((Boolean) debugCameraMetrics$delegate.getValue(null, $$delegatedProperties[0])).booleanValue();
    }

    public static final void setDebugCameraMetrics(boolean z) {
        debugCameraMetrics$delegate.setValue(null, $$delegatedProperties[0], Boolean.valueOf(z));
    }

    public static final boolean getForceCompactSavedMusic() {
        return ((Boolean) forceCompactSavedMusic$delegate.getValue(null, $$delegatedProperties[1])).booleanValue();
    }

    public static final void setForceCompactSavedMusic(boolean z) {
        forceCompactSavedMusic$delegate.setValue(null, $$delegatedProperties[1], Boolean.valueOf(z));
    }

    public static final boolean getDisableApiRequests() {
        return ((Boolean) disableApiRequests$delegate.getValue(null, $$delegatedProperties[2])).booleanValue();
    }

    public static final void setDisableApiRequests(boolean z) {
        disableApiRequests$delegate.setValue(null, $$delegatedProperties[2], Boolean.valueOf(z));
    }

    public static final boolean getDisableChatFadeWallpaperBlend() {
        return ((Boolean) disableChatFadeWallpaperBlend$delegate.getValue(null, $$delegatedProperties[3])).booleanValue();
    }

    public static final void setDisableChatFadeWallpaperBlend(boolean z) {
        disableChatFadeWallpaperBlend$delegate.setValue(null, $$delegatedProperties[3], Boolean.valueOf(z));
    }

    public static final boolean getChatFadeUseWhiteBackground() {
        return ((Boolean) chatFadeUseWhiteBackground$delegate.getValue(null, $$delegatedProperties[4])).booleanValue();
    }

    public static final void setChatFadeUseWhiteBackground(boolean z) {
        chatFadeUseWhiteBackground$delegate.setValue(null, $$delegatedProperties[4], Boolean.valueOf(z));
    }

    public static final boolean getHeapMonitorEnabled() {
        return ((Boolean) heapMonitorEnabled$delegate.getValue(null, $$delegatedProperties[5])).booleanValue();
    }

    public static final void setHeapMonitorEnabled(boolean z) {
        heapMonitorEnabled$delegate.setValue(null, $$delegatedProperties[5], Boolean.valueOf(z));
    }

    public static final int getHeapMonitorLimitMb() {
        return ((Number) heapMonitorLimitMb$delegate.getValue(null, $$delegatedProperties[6])).intValue();
    }

    public static final void setHeapMonitorLimitMb(int i) {
        heapMonitorLimitMb$delegate.setValue(null, $$delegatedProperties[6], Integer.valueOf(i));
    }

    public static final boolean getLoadMonitorEnabled() {
        return ((Boolean) loadMonitorEnabled$delegate.getValue(null, $$delegatedProperties[7])).booleanValue();
    }

    public static final void setLoadMonitorEnabled(boolean z) {
        loadMonitorEnabled$delegate.setValue(null, $$delegatedProperties[7], Boolean.valueOf(z));
    }

    public static final int getLoadMonitorCpuPercent() {
        return ((Number) loadMonitorCpuPercent$delegate.getValue(null, $$delegatedProperties[8])).intValue();
    }

    public static final void setLoadMonitorCpuPercent(int i) {
        loadMonitorCpuPercent$delegate.setValue(null, $$delegatedProperties[8], Integer.valueOf(i));
    }

    public static final boolean getGlassHeaderMenu() {
        return ((Boolean) glassHeaderMenu$delegate.getValue(null, $$delegatedProperties[9])).booleanValue();
    }

    public static final void setGlassHeaderMenu(boolean z) {
        glassHeaderMenu$delegate.setValue(null, $$delegatedProperties[9], Boolean.valueOf(z));
    }
}
