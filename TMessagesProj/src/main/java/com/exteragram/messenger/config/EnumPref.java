package com.exteragram.messenger.config;

import com.exteragram.messenger.ExteraConfig;
import java.lang.Enum;
import kotlin.Metadata;
import kotlin.collections.ArraysKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0010\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0003\u0018\u0000*\u000e\b\u0000\u0010\u0001*\b\u0012\u0004\u0012\u0002H\u00010\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B\u001b\u0012\u0006\u0010\u0004\u001a\u00028\u0000\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u001d\u0010\t\u001a\u00028\u00002\u0006\u0010\n\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\fJ\u001d\u0010\r\u001a\u00020\u000e2\u0006\u0010\n\u001a\u00020\u00062\u0006\u0010\u000f\u001a\u00028\u0000H\u0016¢\u0006\u0002\u0010\u0010¨\u0006\u0011"}, d2 = {"Lcom/exteragram/messenger/config/EnumPref;", "E", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/config/BasePref;", "def", "backupKey", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/Enum;Ljava/lang/String;)V", "fetch", "key", "default", "(Ljava/lang/String;Ljava/lang/Enum;)Ljava/lang/Enum;", "save", _UrlKt.FRAGMENT_ENCODE_SET, "value", "(Ljava/lang/String;Ljava/lang/Enum;)V", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class EnumPref<E extends Enum<E>> extends BasePref<E> {
    public EnumPref(E e, String str) {
        super(e, str);
    }

    public /* synthetic */ EnumPref(Enum r1, String str, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this((E) r1, (i & 2) != 0 ? null : str);
    }

    @Override 
    public E fetch(String key, E e) {
        E e2;
        int i = ExteraConfig.getPreferences().getInt(key, e.ordinal());
        try {
            Enum[] enumArr = (Enum[]) e.getClass().getEnumConstants();
            return (enumArr == null || (e2 = (E) ArraysKt.getOrNull(enumArr, i)) == null) ? e : e2;
        } catch (Exception unused) {
            return e;
        }
    }

    @Override 
    public void save(String key, E value) {
        ExteraConfig.getEditor().putInt(key, value.ordinal()).apply();
    }
}
