package com.exteragram.messenger.export.api;

import com.exteragram.messenger.utils.RecordTag;
import com.sun.jna.Callback;
import java.util.Objects;

public final class ApiWrap$HistoryMessageMarkupButton extends RecordTag {
    private final int buttonId;
    private final byte[] data;
    private final String forwardText;
    private final String text;
    private final Type type;

    public enum Type {
        Default,
        Url,
        Callback,
        CallbackWithPassword,
        RequestPhone,
        RequestLocation,
        RequestPoll,
        RequestPeer,
        SwitchInline,
        SwitchInlineSame,
        Game,
        Buy,
        Auth,
        UserProfile,
        WebView,
        SimpleWebView,
        CopyText
    }

    private /* synthetic */ boolean $record$equals(Object obj) {
        if (!(obj instanceof ApiWrap$HistoryMessageMarkupButton)) {
            return false;
        }
        ApiWrap$HistoryMessageMarkupButton apiWrap$HistoryMessageMarkupButton = (ApiWrap$HistoryMessageMarkupButton) obj;
        return this.buttonId == apiWrap$HistoryMessageMarkupButton.buttonId && Objects.equals(this.type, apiWrap$HistoryMessageMarkupButton.type) && Objects.equals(this.text, apiWrap$HistoryMessageMarkupButton.text) && Objects.equals(this.data, apiWrap$HistoryMessageMarkupButton.data) && Objects.equals(this.forwardText, apiWrap$HistoryMessageMarkupButton.forwardText);
    }

    private /* synthetic */ Object[] $record$getFieldsAsObjects() {
        return new Object[]{this.type, this.text, this.data, this.forwardText, Integer.valueOf(this.buttonId)};
    }

    public ApiWrap$HistoryMessageMarkupButton(Type type, String str, byte[] bArr, String str2, int i) {
        this.type = type;
        this.text = str;
        this.data = bArr;
        this.forwardText = str2;
        this.buttonId = i;
    }

    public int buttonId() {
        return this.buttonId;
    }

    public byte[] data() {
        return this.data;
    }

    public final boolean equals(Object obj) {
        return $record$equals(obj);
    }

    public String forwardText() {
        return this.forwardText;
    }

    public final int hashCode() {
        return java.util.Objects.hash(this.buttonId, this.type, this.text, this.data, this.forwardText);
    }

    public String text() {
        return this.text;
    }

    public final String toString() {
        return com.exteragram.messenger.utils.RecordUtils.recordToString($record$getFieldsAsObjects(), ApiWrap$HistoryMessageMarkupButton.class, "type;text;data;forwardText;buttonId");
    }

    public Type type() {
        return this.type;
    }

    public ApiWrap$HistoryMessageMarkupButton(Type type, String str) {
        this(type, str, null, null, 0);
    }

    public ApiWrap$HistoryMessageMarkupButton(Type type, String str, byte[] bArr) {
        this(type, str, bArr, null, 0);
    }

    public static String TypeToString(ApiWrap$HistoryMessageMarkupButton apiWrap$HistoryMessageMarkupButton) {
        switch (ApiWrap$1.$SwitchMap$com$exteragram$messenger$export$api$ApiWrap$HistoryMessageMarkupButton$Type[apiWrap$HistoryMessageMarkupButton.type.ordinal()]) {
            case 1:
                return "default";
            case 2:
                return "url";
            case 3:
                return Callback.METHOD_NAME;
            case 4:
                return "callback_with_password";
            case 5:
                return "request_phone";
            case 6:
                return "request_location";
            case 7:
                return "request_poll";
            case 8:
                return "request_peer";
            case 9:
                return "switch_inline";
            case 10:
                return "switch_inline_same";
            case 11:
                return "game";
            case 12:
                return "buy";
            case 13:
                return "auth";
            case 14:
                return "user_profile";
            case 15:
                return "web_view";
            case 16:
                return "simple_web_view";
            case 17:
                return "copy_text";
            default:
                throw new IncompatibleClassChangeError();
        }
    }
}
