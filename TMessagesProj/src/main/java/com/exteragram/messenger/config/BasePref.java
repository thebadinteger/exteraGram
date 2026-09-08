package com.exteragram.messenger.config;

import com.exteragram.messenger.backup.PreferencesUtils;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KProperty;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0018\b&\u0018\u0000*\u0004\b\u0000\u0010\u00012\u00020\u0002B\u001b\u0012\u0006\u0010\u0003\u001a\u00028\u0000\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0004¢\u0006\u0004\b\u0006\u0010\u0007J,\u0010\u000b\u001a\b\u0012\u0004\u0012\u00028\u00000\u00002\b\u0010\b\u001a\u0004\u0018\u00010\u00022\n\u0010\n\u001a\u0006\u0012\u0002\b\u00030\tH\u0086\u0002¢\u0006\u0004\b\u000b\u0010\fJ&\u0010\r\u001a\u00028\u00002\b\u0010\b\u001a\u0004\u0018\u00010\u00022\n\u0010\n\u001a\u0006\u0012\u0002\b\u00030\tH\u0086\u0002¢\u0006\u0004\b\r\u0010\u000eJ.\u0010\u0011\u001a\u00020\u00102\b\u0010\b\u001a\u0004\u0018\u00010\u00022\n\u0010\n\u001a\u0006\u0012\u0002\b\u00030\t2\u0006\u0010\u000f\u001a\u00028\u0000H\u0086\u0002¢\u0006\u0004\b\u0011\u0010\u0012J\r\u0010\u0013\u001a\u00020\u0010¢\u0006\u0004\b\u0013\u0010\u0014J\u001f\u0010\u0017\u001a\u00028\u00002\u0006\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u0016\u001a\u00028\u0000H&¢\u0006\u0004\b\u0017\u0010\u0018J\u001f\u0010\u0019\u001a\u00020\u00102\u0006\u0010\u0015\u001a\u00020\u00042\u0006\u0010\u000f\u001a\u00028\u0000H&¢\u0006\u0004\b\u0019\u0010\u001aR\u0017\u0010\u0003\u001a\u00028\u00008\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u001b\u001a\u0004\b\u001c\u0010\u001dR\u0019\u0010\u0005\u001a\u0004\u0018\u00010\u00048\u0006¢\u0006\f\n\u0004\b\u0005\u0010\u001e\u001a\u0004\b\u001f\u0010 R$\u0010!\u001a\u0004\u0018\u00018\u00008\u0004@\u0004X\u0084\u000e¢\u0006\u0012\n\u0004\b!\u0010\u001b\u001a\u0004\b\"\u0010\u001d\"\u0004\b#\u0010$R\"\u0010\u0015\u001a\u00020\u00048\u0006@\u0006X\u0086.¢\u0006\u0012\n\u0004\b\u0015\u0010\u001e\u001a\u0004\b%\u0010 \"\u0004\b&\u0010'¨\u0006("}, d2 = {"Lcom/exteragram/messenger/config/BasePref;", "T", _UrlKt.FRAGMENT_ENCODE_SET, "defaultValue", _UrlKt.FRAGMENT_ENCODE_SET, "backupKey", "<init>", "(Ljava/lang/Object;Ljava/lang/String;)V", "thisRef", "Lkotlin/reflect/KProperty;", "property", "provideDelegate", "(Ljava/lang/Object;Lkotlin/reflect/KProperty;)Lcom/exteragram/messenger/config/BasePref;", "getValue", "(Ljava/lang/Object;Lkotlin/reflect/KProperty;)Ljava/lang/Object;", "value", _UrlKt.FRAGMENT_ENCODE_SET, "setValue", "(Ljava/lang/Object;Lkotlin/reflect/KProperty;Ljava/lang/Object;)V", "invalidate", "()V", "key", "default", "fetch", "(Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;", "save", "(Ljava/lang/String;Ljava/lang/Object;)V", "Ljava/lang/Object;", "getDefaultValue", "()Ljava/lang/Object;", "Ljava/lang/String;", "getBackupKey", "()Ljava/lang/String;", "cache", "getCache", "setCache", "(Ljava/lang/Object;)V", "getKey", "setKey", "(Ljava/lang/String;)V", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nPrefClasses.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PrefClasses.kt\ncom/exteragram/messenger/config/BasePref\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,105:1\n1#2:106\n*E\n"})
public abstract class BasePref<T> {
    private final String backupKey;
    private T cache;
    private final T defaultValue;
    public String key;

    public abstract T fetch(String key, T t);

    public abstract void save(String key, T value);

    public BasePref(T t, String str) {
        this.defaultValue = t;
        this.backupKey = str;
    }

    public final String getKey() {
        String str = this.key;
        if (str != null) {
            return str;
        }
        return null;
    }

    public final void setKey(String str) {
        this.key = str;
    }

    public final BasePref<T> provideDelegate(Object thisRef, KProperty<?> property) {
        String name = this.backupKey;
        if (name == null) {
            name = property.getName();
        }
        setKey(name);
        List<PreferencesUtils.BackupItem> registeredKeys = PrefClassesKt.getRegisteredKeys();
        String key = getKey();
        T t = this.defaultValue;
        registeredKeys.add(new PreferencesUtils.BackupItem(key, t != null ? t.getClass() : String.class));
        PrefClassesKt.getAllDelegates().add(this);
        return this;
    }

    public final T getValue(Object thisRef, KProperty<?> property) {
        T t = this.cache;
        if (t != null) {
            return t;
        }
        T tFetch = fetch(getKey(), this.defaultValue);
        this.cache = tFetch;
        return tFetch;
    }

    public final void setValue(Object thisRef, KProperty<?> property, T value) {
        if (Intrinsics.areEqual(this.cache, value)) {
            return;
        }
        String key = getKey();
        this.cache = value;
        Unit unit = Unit.INSTANCE;
        save(key, value);
    }

    public final void invalidate() {
        this.cache = null;
    }
}
