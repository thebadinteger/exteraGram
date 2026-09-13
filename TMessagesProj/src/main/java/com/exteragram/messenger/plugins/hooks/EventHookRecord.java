package com.exteragram.messenger.plugins.hooks;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\b\u0007\u0018\u00002\u00020\u0001B+\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\b\u0010\u000b\u001a\u0004\u0018\u00010\u0003J\b\u0010\f\u001a\u0004\u0018\u00010\u0003J\u0006\u0010\r\u001a\u00020\bJ\u0006\u0010\u000e\u001a\u00020\u0006J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\u0012\u0010\u0011\u001a\u00020\u00062\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u0016J\u0013\u0010\u0014\u001a\u00020\u00062\b\u0010\u0015\u001a\u0004\u0018\u00010\u0013H\u0096\u0002J\b\u0010\u0016\u001a\u00020\bH\u0016R\u0010\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0006X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\bX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0017"}, d2 = {"Lcom/exteragram/messenger/plugins/hooks/EventHookRecord;", "Lcom/exteragram/messenger/plugins/hooks/HookRecord;", "pluginId", _UrlKt.FRAGMENT_ENCODE_SET, "hookName", "matchSubstring", _UrlKt.FRAGMENT_ENCODE_SET, "priority", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;Ljava/lang/String;ZI)V", "getPluginId", "getHookName", "getPriority", "isMatchSubstring", "cleanup", _UrlKt.FRAGMENT_ENCODE_SET, "matches", "criteria", _UrlKt.FRAGMENT_ENCODE_SET, "equals", "other", "hashCode", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class EventHookRecord implements HookRecord {
    private final String hookName;
    private final boolean matchSubstring;
    private final String pluginId;
    private final int priority;

    @Override 
    public void cleanup() {
    }

    public EventHookRecord(String str, String str2, boolean z, int i) {
        this.pluginId = str;
        this.hookName = str2;
        this.matchSubstring = z;
        this.priority = i;
    }

    public final String getPluginId() {
        return this.pluginId;
    }

    public final String getHookName() {
        return this.hookName;
    }

    public final int getPriority() {
        return this.priority;
    }

    public final boolean getMatchSubstring() {
        return this.matchSubstring;
    }

    @Override 
    public boolean matches(Object criteria) {
        String str;
        String str2 = criteria instanceof String ? (String) criteria : null;
        if (str2 == null || (str = this.hookName) == null) {
            return false;
        }
        if (this.matchSubstring) {
            return str.length() > 0 && str2.contains(str);
        }
        return Intrinsics.areEqual(str, str2);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other != null && Intrinsics.areEqual(EventHookRecord.class, other.getClass())) {
            EventHookRecord eventHookRecord = (EventHookRecord) other;
            if (this.matchSubstring == eventHookRecord.matchSubstring && Intrinsics.areEqual(this.pluginId, eventHookRecord.pluginId) && Intrinsics.areEqual(this.hookName, eventHookRecord.hookName)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        String str = this.pluginId;
        int iHashCode = (str != null ? str.hashCode() : 0) * 31;
        String str2 = this.hookName;
        return ((iHashCode + (str2 != null ? str2.hashCode() : 0)) * 31) + Boolean.hashCode(this.matchSubstring);
    }
}
