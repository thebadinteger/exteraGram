package com.exteragram.messenger.plugins.models;

import com.chaquo.python.PyObject;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0015\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001BU\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\n\u0012\b\u0010\f\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\r\u0010\u000eJ\b\u0010\u001f\u001a\u00020 H\u0016R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0013\u0010\u0010\"\u0004\b\u0014\u0010\u0012R\u001a\u0010\u0005\u001a\u00020\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u001c\u0010\u0007\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u0010\"\u0004\b\u001a\u0010\u0012R\u001c\u0010\t\u001a\u0004\u0018\u00010\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001e¨\u0006!"}, d2 = {"Lcom/exteragram/messenger/plugins/models/SwitchSetting;", "Lcom/exteragram/messenger/plugins/models/SettingItem;", "key", _UrlKt.FRAGMENT_ENCODE_SET, "text", "defaultValue", _UrlKt.FRAGMENT_ENCODE_SET, "subtext", "icon", "onChangeCallback", "Lcom/chaquo/python/PyObject;", "onLongClickCallback", "linkAlias", "<init>", "(Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Lcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Ljava/lang/String;)V", "getKey", "()Ljava/lang/String;", "setKey", "(Ljava/lang/String;)V", "getText", "setText", "getDefaultValue", "()Z", "setDefaultValue", "(Z)V", "getSubtext", "setSubtext", "getOnChangeCallback", "()Lcom/chaquo/python/PyObject;", "setOnChangeCallback", "(Lcom/chaquo/python/PyObject;)V", "cleanup", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class SwitchSetting extends SettingItem {
    private boolean defaultValue;
    private String key;
    private PyObject onChangeCallback;
    private String subtext;
    private String text;

    public final String getKey() {
        return this.key;
    }

    public final void setKey(String str) {
        this.key = str;
    }

    public final String getText() {
        return this.text;
    }

    public final void setText(String str) {
        this.text = str;
    }

    public final boolean getDefaultValue() {
        return this.defaultValue;
    }

    public final void setDefaultValue(boolean z) {
        this.defaultValue = z;
    }

    public final String getSubtext() {
        return this.subtext;
    }

    public final void setSubtext(String str) {
        this.subtext = str;
    }

    public final PyObject getOnChangeCallback() {
        return this.onChangeCallback;
    }

    public final void setOnChangeCallback(PyObject pyObject) {
        this.onChangeCallback = pyObject;
    }

    public SwitchSetting(String str, String str2, boolean z, String str3, String str4, PyObject pyObject, PyObject pyObject2, String str5) {
        super("switch", str4, pyObject2, str5);
        this.key = str;
        this.text = str2;
        this.defaultValue = z;
        this.subtext = str3;
        this.onChangeCallback = pyObject;
    }

    @Override 
    public void cleanup() {
        super.cleanup();
        closeCallback(this.onChangeCallback);
        this.onChangeCallback = null;
    }
}
