package com.exteragram.messenger.export.api;

import okhttp3.internal.url._UrlKt;
import org.telegram.tgnet.TLRPC;

public class ApiWrap$User {
    public Long id;
    public ApiWrap$ContactInfo info;
    public String username;
    public Long bareId = 0L;
    public int colorIndex = 0;
    public boolean isBot = false;
    public boolean isSelf = false;
    public boolean isReplies = false;
    public boolean isVerifyCodes = false;
    public TLRPC.InputUser input = new TLRPC.TL_inputUserEmpty();

    public String name() {
        String str = this.info.firstName;
        if (str == null || str.isEmpty()) {
            String str2 = this.info.lastName;
            if (str2 == null || str2.isEmpty()) {
                return _UrlKt.FRAGMENT_ENCODE_SET;
            }
            return this.info.lastName;
        }
        String str3 = this.info.lastName;
        if (str3 == null || str3.isEmpty()) {
            return this.info.firstName;
        }
        return this.info.firstName + " " + this.info.lastName;
    }
}
