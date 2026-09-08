package com.exteragram.messenger.plugins.models;

import com.chaquo.python.PyObject;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0018\n\u0002\u0010\u0002\n\u0000\u0018\u00002\u00020\u0001B]\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\n\u0012\b\u0010\f\u001a\u0004\u0018\u00010\n\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u000e\u0010\u000fJ\b\u0010\"\u001a\u00020#H\u0016R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0011\"\u0004\b\u0015\u0010\u0013R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0016\u0010\u0017\"\u0004\b\u0018\u0010\u0019R\u001a\u0010\b\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001a\u0010\u0017\"\u0004\b\u001b\u0010\u0019R\u001c\u0010\t\u001a\u0004\u0018\u00010\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\u001d\"\u0004\b\u001e\u0010\u001fR\u001c\u0010\u000b\u001a\u0004\u0018\u00010\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b \u0010\u001d\"\u0004\b!\u0010\u001f¨\u0006$"}, d2 = {"Lcom/exteragram/messenger/plugins/models/TextSetting;", "Lcom/exteragram/messenger/plugins/models/SettingItem;", "text", _UrlKt.FRAGMENT_ENCODE_SET, "subtext", "icon", "accent", _UrlKt.FRAGMENT_ENCODE_SET, "red", "onClickCallback", "Lcom/chaquo/python/PyObject;", "createSubFragmentCallback", "onLongClickCallback", "linkAlias", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZLcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Ljava/lang/String;)V", "getText", "()Ljava/lang/String;", "setText", "(Ljava/lang/String;)V", "getSubtext", "setSubtext", "getAccent", "()Z", "setAccent", "(Z)V", "getRed", "setRed", "getOnClickCallback", "()Lcom/chaquo/python/PyObject;", "setOnClickCallback", "(Lcom/chaquo/python/PyObject;)V", "getCreateSubFragmentCallback", "setCreateSubFragmentCallback", "cleanup", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class TextSetting extends SettingItem {
    private boolean accent;
    private PyObject createSubFragmentCallback;
    private PyObject onClickCallback;
    private boolean red;
    private String subtext;
    private String text;

    public final String getText() {
        return this.text;
    }

    public final void setText(String str) {
        this.text = str;
    }

    public final String getSubtext() {
        return this.subtext;
    }

    public final void setSubtext(String str) {
        this.subtext = str;
    }

    public final boolean getAccent() {
        return this.accent;
    }

    public final void setAccent(boolean z) {
        this.accent = z;
    }

    public final boolean getRed() {
        return this.red;
    }

    public final void setRed(boolean z) {
        this.red = z;
    }

    public final PyObject getOnClickCallback() {
        return this.onClickCallback;
    }

    public final void setOnClickCallback(PyObject pyObject) {
        this.onClickCallback = pyObject;
    }

    public final PyObject getCreateSubFragmentCallback() {
        return this.createSubFragmentCallback;
    }

    public final void setCreateSubFragmentCallback(PyObject pyObject) {
        this.createSubFragmentCallback = pyObject;
    }

    public TextSetting(String str, String str2, String str3, boolean z, boolean z2, PyObject pyObject, PyObject pyObject2, PyObject pyObject3, String str4) {
        super("text", str3, pyObject3, str4);
        this.text = str;
        this.subtext = str2;
        this.accent = z;
        this.red = z2;
        this.onClickCallback = pyObject;
        this.createSubFragmentCallback = pyObject2;
    }

    @Override 
    public void cleanup() {
        super.cleanup();
        closeCallback(this.onClickCallback);
        closeCallback(this.createSubFragmentCallback);
        this.onClickCallback = null;
        this.createSubFragmentCallback = null;
    }
}
