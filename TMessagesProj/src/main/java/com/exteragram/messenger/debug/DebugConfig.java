package com.exteragram.messenger.debug;

import com.exteragram.messenger.config.BasePref;
import com.exteragram.messenger.config.BooleanPref;
import kotlin.Metadata;
import kotlin.jvm.JvmName;
import kotlin.jvm.internal.MutablePropertyReference0Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0018\"+\u0010\u0002\u001a\u00020\u00012\u0006\u0010\u0000\u001a\u00020\u00018F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0007\u0010\b\u001a\u0004\b\u0003\u0010\u0004\"\u0004\b\u0005\u0010\u0006\"+\u0010\t\u001a\u00020\u00012\u0006\u0010\u0000\u001a\u00020\u00018F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\f\u0010\b\u001a\u0004\b\n\u0010\u0004\"\u0004\b\u000b\u0010\u0006\"+\u0010\r\u001a\u00020\u00012\u0006\u0010\u0000\u001a\u00020\u00018F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0010\u0010\b\u001a\u0004\b\u000e\u0010\u0004\"\u0004\b\u000f\u0010\u0006\"+\u0010\u0011\u001a\u00020\u00012\u0006\u0010\u0000\u001a\u00020\u00018F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0014\u0010\b\u001a\u0004\b\u0012\u0010\u0004\"\u0004\b\u0013\u0010\u0006\"+\u0010\u0015\u001a\u00020\u00012\u0006\u0010\u0000\u001a\u00020\u00018F@FX\u0086\u008e\u0002¢\u0006\u0012\n\u0004\b\u0018\u0010\b\u001a\u0004\b\u0016\u0010\u0004\"\u0004\b\u0017\u0010\u0006¨\u0006\u0019"}, d2 = {"<set-?>", _UrlKt.FRAGMENT_ENCODE_SET, "debugCameraMetrics", "getDebugCameraMetrics", "()Z", "setDebugCameraMetrics", "(Z)V", "debugCameraMetrics$delegate", "Lcom/exteragram/messenger/config/BasePref;", "forceCompactSavedMusic", "getForceCompactSavedMusic", "setForceCompactSavedMusic", "forceCompactSavedMusic$delegate", "disableChatFadeWallpaperBlend", "getDisableChatFadeWallpaperBlend", "setDisableChatFadeWallpaperBlend", "disableChatFadeWallpaperBlend$delegate", "chatFadeUseWhiteBackground", "getChatFadeUseWhiteBackground", "setChatFadeUseWhiteBackground", "chatFadeUseWhiteBackground$delegate", "glassHeaderMenu", "getGlassHeaderMenu", "setGlassHeaderMenu", "glassHeaderMenu$delegate", "TMessagesProj"}, k = 2, mv = {2, 2, 0}, xi = 48)
public abstract class DebugConfig {
    static final /* synthetic */ KProperty<Object>[] $$delegatedProperties;
    private static final BasePref chatFadeUseWhiteBackground$delegate;
    private static final BasePref debugCameraMetrics$delegate;
    private static final BasePref disableChatFadeWallpaperBlend$delegate;
    private static final BasePref forceCompactSavedMusic$delegate;
    private static final BasePref glassHeaderMenu$delegate;

    static {
        KProperty<?>[] kPropertyArr = {Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "debugCameraMetrics", "getDebugCameraMetrics()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "forceCompactSavedMusic", "getForceCompactSavedMusic()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "disableChatFadeWallpaperBlend", "getDisableChatFadeWallpaperBlend()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "chatFadeUseWhiteBackground", "getChatFadeUseWhiteBackground()Z", 1)), Reflection.mutableProperty0(new MutablePropertyReference0Impl(DebugConfig.class, "glassHeaderMenu", "getGlassHeaderMenu()Z", 1))};
        $$delegatedProperties = (KProperty<Object>[]) kPropertyArr;
        debugCameraMetrics$delegate = new BooleanPref(false, null, 2, null).provideDelegate(null, kPropertyArr[0]);
        forceCompactSavedMusic$delegate = new BooleanPref(false, null, 2, null).provideDelegate(null, kPropertyArr[1]);
        disableChatFadeWallpaperBlend$delegate = new BooleanPref(false, null, 2, null).provideDelegate(null, kPropertyArr[2]);
        chatFadeUseWhiteBackground$delegate = new BooleanPref(false, null, 2, null).provideDelegate(null, kPropertyArr[3]);
        glassHeaderMenu$delegate = new BooleanPref(false, null, 2, null).provideDelegate(null, kPropertyArr[4]);
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

    public static final boolean getDisableChatFadeWallpaperBlend() {
        return ((Boolean) disableChatFadeWallpaperBlend$delegate.getValue(null, $$delegatedProperties[2])).booleanValue();
    }

    public static final void setDisableChatFadeWallpaperBlend(boolean z) {
        disableChatFadeWallpaperBlend$delegate.setValue(null, $$delegatedProperties[2], Boolean.valueOf(z));
    }

    public static final boolean getChatFadeUseWhiteBackground() {
        return ((Boolean) chatFadeUseWhiteBackground$delegate.getValue(null, $$delegatedProperties[3])).booleanValue();
    }

    public static final void setChatFadeUseWhiteBackground(boolean z) {
        chatFadeUseWhiteBackground$delegate.setValue(null, $$delegatedProperties[3], Boolean.valueOf(z));
    }

    public static final boolean getGlassHeaderMenu() {
        return ((Boolean) glassHeaderMenu$delegate.getValue(null, $$delegatedProperties[4])).booleanValue();
    }

    public static final void setGlassHeaderMenu(boolean z) {
        glassHeaderMenu$delegate.setValue(null, $$delegatedProperties[4], Boolean.valueOf(z));
    }
}
