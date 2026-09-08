package com.exteragram.messenger.config;

import com.exteragram.messenger.backup.PreferencesUtils;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\"\u001b\u0010\u0000\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0003\u0010\u0004\"\u0017\u0010\u0005\u001a\b\u0012\u0004\u0012\u00020\u00060\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0004¨\u0006\b"}, d2 = {"allDelegates", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/config/BasePref;", "getAllDelegates", "()Ljava/util/List;", "registeredKeys", "Lcom/exteragram/messenger/backup/PreferencesUtils$BackupItem;", "getRegisteredKeys", "TMessagesProj"}, k = 2, mv = {2, 2, 0}, xi = 48)
public abstract class PrefClassesKt {
    private static final List<BasePref<?>> allDelegates = new ArrayList();
    private static final List<PreferencesUtils.BackupItem> registeredKeys = new ArrayList();

    public static final List<BasePref<?>> getAllDelegates() {
        return allDelegates;
    }

    public static final List<PreferencesUtils.BackupItem> getRegisteredKeys() {
        return registeredKeys;
    }
}
