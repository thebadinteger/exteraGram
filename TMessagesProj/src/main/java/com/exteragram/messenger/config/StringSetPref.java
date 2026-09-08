package com.exteragram.messenger.config;

import com.exteragram.messenger.ExteraConfig;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\"\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00030\u00020\u0001B!\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0006\u0010\u0007J,\u0010\b\u001a\u0010\u0012\f\u0012\n \t*\u0004\u0018\u00010\u00030\u00030\u00022\u0006\u0010\n\u001a\u00020\u00032\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H\u0016J\u001e\u0010\f\u001a\u00020\r2\u0006\u0010\n\u001a\u00020\u00032\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00030\u0002H\u0016¨\u0006\u000f"}, d2 = {"Lcom/exteragram/messenger/config/StringSetPref;", "Lcom/exteragram/messenger/config/BasePref;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "def", "backupKey", "<init>", "(Ljava/util/Set;Ljava/lang/String;)V", "fetch", "kotlin.jvm.PlatformType", "key", "default", "save", _UrlKt.FRAGMENT_ENCODE_SET, "value", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class StringSetPref extends BasePref<Set<? extends String>> {
    public StringSetPref(Set<String> set, String str) {
        super(set, str);
    }

    public /* synthetic */ void save(String str, Set<? extends String> set) {
        save2(str, (Set<String>) set);
    }

    public Set<String> fetch2(String key, Set<String> set) {
        Set<String> stringSet = ExteraConfig.getPreferences().getStringSet(key, set);
        return stringSet == null ? set : stringSet;
    }

    public void save2(String key, Set<String> value) {
        ExteraConfig.getEditor().putStringSet(key, value).apply();
    }
}
