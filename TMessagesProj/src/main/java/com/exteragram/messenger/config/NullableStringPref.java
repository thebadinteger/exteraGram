package com.exteragram.messenger.config;

import com.exteragram.messenger.ExteraConfig;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\n\u0012\u0006\u0012\u0004\u0018\u00010\u00020\u0001B\u001d\u0012\b\u0010\u0003\u001a\u0004\u0018\u00010\u0002\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0002¢\u0006\u0004\b\u0005\u0010\u0006J\u001c\u0010\u0007\u001a\u0004\u0018\u00010\u00022\u0006\u0010\b\u001a\u00020\u00022\b\u0010\t\u001a\u0004\u0018\u00010\u0002H\u0016J\u001a\u0010\n\u001a\u00020\u000b2\u0006\u0010\b\u001a\u00020\u00022\b\u0010\f\u001a\u0004\u0018\u00010\u0002H\u0016¨\u0006\r"}, d2 = {"Lcom/exteragram/messenger/config/NullableStringPref;", "Lcom/exteragram/messenger/config/BasePref;", _UrlKt.FRAGMENT_ENCODE_SET, "def", "backupKey", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", "fetch", "key", "default", "save", _UrlKt.FRAGMENT_ENCODE_SET, "value", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class NullableStringPref extends BasePref<String> {
    public NullableStringPref(String str, String str2) {
        super(str, str2);
    }

    public /* synthetic */ NullableStringPref(String str, String str2, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, (i & 2) != 0 ? null : str2);
    }

    @Override 
    public String fetch(String key, String str) {
        return ExteraConfig.getPreferences().getString(key, str);
    }

    @Override 
    public void save(String key, String value) {
        ExteraConfig.getEditor().putString(key, value).apply();
    }
}
