package com.exteragram.messenger.plugins.models;

import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\u0018\u00002\u00020\u0001B\u0011\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\u0005¨\u0006\t"}, d2 = {"Lcom/exteragram/messenger/plugins/models/HeaderSetting;", "Lcom/exteragram/messenger/plugins/models/SettingItem;", "text", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;)V", "getText", "()Ljava/lang/String;", "setText", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class HeaderSetting extends SettingItem {
    private String text;

    public HeaderSetting(String str) {
        super("header", null, null, null, 14, null);
        this.text = str;
    }

    public final String getText() {
        return this.text;
    }

    public final void setText(String str) {
        this.text = str;
    }
}
