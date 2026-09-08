package com.exteragram.messenger.plugins.models;

import com.chaquo.python.PyObject;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001BW\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t\u0012\b\u0010\n\u001a\u0004\u0018\u00010\t\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\f\u0010\rJ\b\u0010\u001c\u001a\u00020\u001dH\u0016R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u000f\"\u0004\b\u0013\u0010\u0011R\u001c\u0010\u0005\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u000f\"\u0004\b\u0015\u0010\u0011R\u001c\u0010\u0006\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u000f\"\u0004\b\u0017\u0010\u0011R\u001c\u0010\b\u001a\u0004\u0018\u00010\tX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0018\u0010\u0019\"\u0004\b\u001a\u0010\u001b¨\u0006\u001e"}, d2 = {"Lcom/exteragram/messenger/plugins/models/InputSetting;", "Lcom/exteragram/messenger/plugins/models/SettingItem;", "key", _UrlKt.FRAGMENT_ENCODE_SET, "text", "defaultValue", "subtext", "icon", "onChangeCallback", "Lcom/chaquo/python/PyObject;", "onLongClickCallback", "linkAlias", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Ljava/lang/String;)V", "getKey", "()Ljava/lang/String;", "setKey", "(Ljava/lang/String;)V", "getText", "setText", "getDefaultValue", "setDefaultValue", "getSubtext", "setSubtext", "getOnChangeCallback", "()Lcom/chaquo/python/PyObject;", "setOnChangeCallback", "(Lcom/chaquo/python/PyObject;)V", "cleanup", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class InputSetting extends SettingItem {
    private String defaultValue;
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

    public final String getDefaultValue() {
        return this.defaultValue;
    }

    public final void setDefaultValue(String str) {
        this.defaultValue = str;
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

    public InputSetting(String str, String str2, String str3, String str4, String str5, PyObject pyObject, PyObject pyObject2, String str6) {
        super("input", str5, pyObject2, str6);
        this.key = str;
        this.text = str2;
        this.defaultValue = str3;
        this.subtext = str4;
        this.onChangeCallback = pyObject;
    }

    @Override 
    public void cleanup() {
        super.cleanup();
        closeCallback(this.onChangeCallback);
        this.onChangeCallback = null;
    }
}
