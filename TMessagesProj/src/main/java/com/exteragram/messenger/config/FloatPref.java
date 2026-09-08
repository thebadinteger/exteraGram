package com.exteragram.messenger.config;

import com.exteragram.messenger.ExteraConfig;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u001b\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u001d\u0010\b\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\u00052\u0006\u0010\n\u001a\u00020\u0002H\u0016¢\u0006\u0002\u0010\u000bJ\u0018\u0010\f\u001a\u00020\r2\u0006\u0010\t\u001a\u00020\u00052\u0006\u0010\u000e\u001a\u00020\u0002H\u0016¨\u0006\u000f"}, d2 = {"Lcom/exteragram/messenger/config/FloatPref;", "Lcom/exteragram/messenger/config/BasePref;", _UrlKt.FRAGMENT_ENCODE_SET, "def", "backupKey", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(FLjava/lang/String;)V", "fetch", "key", "default", "(Ljava/lang/String;F)Ljava/lang/Float;", "save", _UrlKt.FRAGMENT_ENCODE_SET, "value", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class FloatPref extends BasePref<Float> {
    public FloatPref(float f, String str) {
        super(Float.valueOf(f), str);
    }

    public /* synthetic */ void save(String str, Float f) {
        save(str, f.floatValue());
    }

    public Float fetch(String key, float f) {
        float f2;
        try {
            try {
                f2 = ExteraConfig.getPreferences().getFloat(key, f);
            } catch (ClassCastException unused) {
                f = ExteraConfig.getPreferences().getInt(key, (int) f);
                f2 = f;
                return Float.valueOf(f2);
            }
        } catch (Exception unused2) {
            f2 = f;
        }
        return Float.valueOf(f2);
    }

    public void save(String key, float value) {
        ExteraConfig.getEditor().putFloat(key, value).apply();
    }
}
