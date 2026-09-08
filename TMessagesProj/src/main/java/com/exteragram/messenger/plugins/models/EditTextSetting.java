package com.exteragram.messenger.plugins.models;

import com.chaquo.python.PyObject;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0019\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001BI\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\f¢\u0006\u0004\b\r\u0010\u000eJ\b\u0010%\u001a\u00020&H\u0016R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0010\"\u0004\b\u0014\u0010\u0012R\u001c\u0010\u0005\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0010\"\u0004\b\u0016\u0010\u0012R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0017\u0010\u0018\"\u0004\b\u0019\u0010\u001aR\u001a\u0010\b\u001a\u00020\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR\u001c\u0010\n\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010\u0010\"\u0004\b \u0010\u0012R\u001c\u0010\u000b\u001a\u0004\u0018\u00010\fX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b!\u0010\"\"\u0004\b#\u0010$¨\u0006'"}, d2 = {"Lcom/exteragram/messenger/plugins/models/EditTextSetting;", "Lcom/exteragram/messenger/plugins/models/SettingItem;", "key", _UrlKt.FRAGMENT_ENCODE_SET, "hint", "defaultValue", "multiline", _UrlKt.FRAGMENT_ENCODE_SET, "maxLength", _UrlKt.FRAGMENT_ENCODE_SET, "mask", "onChangeCallback", "Lcom/chaquo/python/PyObject;", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZILjava/lang/String;Lcom/chaquo/python/PyObject;)V", "getKey", "()Ljava/lang/String;", "setKey", "(Ljava/lang/String;)V", "getHint", "setHint", "getDefaultValue", "setDefaultValue", "getMultiline", "()Z", "setMultiline", "(Z)V", "getMaxLength", "()I", "setMaxLength", "(I)V", "getMask", "setMask", "getOnChangeCallback", "()Lcom/chaquo/python/PyObject;", "setOnChangeCallback", "(Lcom/chaquo/python/PyObject;)V", "cleanup", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class EditTextSetting extends SettingItem {
    private String defaultValue;
    private String hint;
    private String key;
    private String mask;
    private int maxLength;
    private boolean multiline;
    private PyObject onChangeCallback;

    public EditTextSetting(String str, String str2, String str3, boolean z, int i, String str4, PyObject pyObject) {
        super("edit_text", null, null, null, 14, null);
        this.key = str;
        this.hint = str2;
        this.defaultValue = str3;
        this.multiline = z;
        this.maxLength = i;
        this.mask = str4;
        this.onChangeCallback = pyObject;
    }

    public final String getKey() {
        return this.key;
    }

    public final void setKey(String str) {
        this.key = str;
    }

    public final String getHint() {
        return this.hint;
    }

    public final void setHint(String str) {
        this.hint = str;
    }

    public final String getDefaultValue() {
        return this.defaultValue;
    }

    public final void setDefaultValue(String str) {
        this.defaultValue = str;
    }

    public final boolean getMultiline() {
        return this.multiline;
    }

    public final void setMultiline(boolean z) {
        this.multiline = z;
    }

    public final int getMaxLength() {
        return this.maxLength;
    }

    public final void setMaxLength(int i) {
        this.maxLength = i;
    }

    public final String getMask() {
        return this.mask;
    }

    public final void setMask(String str) {
        this.mask = str;
    }

    public final PyObject getOnChangeCallback() {
        return this.onChangeCallback;
    }

    public final void setOnChangeCallback(PyObject pyObject) {
        this.onChangeCallback = pyObject;
    }

    @Override 
    public void cleanup() {
        super.cleanup();
        closeCallback(this.onChangeCallback);
        this.onChangeCallback = null;
    }
}
