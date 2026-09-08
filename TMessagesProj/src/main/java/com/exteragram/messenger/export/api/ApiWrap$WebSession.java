package com.exteragram.messenger.export.api;

import com.android.tools.r8.RecordTag;
import java.util.Objects;

public final class ApiWrap$WebSession extends RecordTag {
    private final String botUsername;
    private final String browser;
    private final int created;
    private final String domain;
    private final String ip;
    private final int lastActive;
    private final String platform;
    private final String region;

    private /* synthetic */ boolean $record$equals(Object obj) {
        if (!(obj instanceof ApiWrap$WebSession)) {
            return false;
        }
        ApiWrap$WebSession apiWrap$WebSession = (ApiWrap$WebSession) obj;
        return this.created == apiWrap$WebSession.created && this.lastActive == apiWrap$WebSession.lastActive && Objects.equals(this.botUsername, apiWrap$WebSession.botUsername) && Objects.equals(this.domain, apiWrap$WebSession.domain) && Objects.equals(this.browser, apiWrap$WebSession.browser) && Objects.equals(this.platform, apiWrap$WebSession.platform) && Objects.equals(this.ip, apiWrap$WebSession.ip) && Objects.equals(this.region, apiWrap$WebSession.region);
    }

    private /* synthetic */ Object[] $record$getFieldsAsObjects() {
        return new Object[]{this.botUsername, this.domain, this.browser, this.platform, Integer.valueOf(this.created), Integer.valueOf(this.lastActive), this.ip, this.region};
    }

    public ApiWrap$WebSession(String str, String str2, String str3, String str4, int i, int i2, String str5, String str6) {
        this.botUsername = str;
        this.domain = str2;
        this.browser = str3;
        this.platform = str4;
        this.created = i;
        this.lastActive = i2;
        this.ip = str5;
        this.region = str6;
    }

    public String botUsername() {
        return this.botUsername;
    }

    public String browser() {
        return this.browser;
    }

    public int created() {
        return this.created;
    }

    public String domain() {
        return this.domain;
    }

    public final boolean equals(Object obj) {
        return $record$equals(obj);
    }

    public final int hashCode() {
        return java.util.Objects.hash(this.created, this.lastActive, this.botUsername, this.domain, this.browser, this.platform, this.ip, this.region);
    }

    public String ip() {
        return this.ip;
    }

    public int lastActive() {
        return this.lastActive;
    }

    public String platform() {
        return this.platform;
    }

    public String region() {
        return this.region;
    }

    public final String toString() {
        return com.exteragram.messenger.utils.RecordUtils.recordToString($record$getFieldsAsObjects(), ApiWrap$WebSession.class, "botUsername;domain;browser;platform;created;lastActive;ip;region");
    }
}
