package com.exteragram.messenger.plugins.models;

import com.chaquo.python.PyObject;
import com.sun.jna.Callback;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;

@Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u0002\n\u0002\b\u0006\b'\u0018\u00002\u00020\u0001B5\b\u0004\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\b\u0010\tJ\u0012\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0006H\u0004J\b\u0010\u0019\u001a\u00020\u0017H\u0016J\u001c\u0010\u001a\u001a\u0004\u0018\u00010\u00032\b\u0010\u001b\u001a\u0004\u0018\u00010\u00032\b\u0010\u001c\u001a\u0004\u0018\u00010\u0003R\u001a\u0010\u0002\u001a\u00020\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\rR\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000b\"\u0004\b\u000f\u0010\rR\u001c\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0010\u0010\u0011\"\u0004\b\u0012\u0010\u0013R\u001c\u0010\u0007\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u000b\"\u0004\b\u0015\u0010\r¨\u0006\u001d"}, d2 = {"Lcom/exteragram/messenger/plugins/models/SettingItem;", _UrlKt.FRAGMENT_ENCODE_SET, TeXSymbolParser.TYPE_ATTR, _UrlKt.FRAGMENT_ENCODE_SET, "icon", "onLongClickCallback", "Lcom/chaquo/python/PyObject;", "linkAlias", "<init>", "(Ljava/lang/String;Ljava/lang/String;Lcom/chaquo/python/PyObject;Ljava/lang/String;)V", "getType", "()Ljava/lang/String;", "setType", "(Ljava/lang/String;)V", "getIcon", "setIcon", "getOnLongClickCallback", "()Lcom/chaquo/python/PyObject;", "setOnLongClickCallback", "(Lcom/chaquo/python/PyObject;)V", "getLinkAlias", "setLinkAlias", "closeCallback", _UrlKt.FRAGMENT_ENCODE_SET, Callback.METHOD_NAME, "cleanup", "getLink", "pluginId", "prefix", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public abstract class SettingItem {
    private String icon;
    private String linkAlias;
    private PyObject onLongClickCallback;
    private String type;

    public SettingItem(String str, String str2, PyObject pyObject, String str3) {
        this.type = str;
        this.icon = str2;
        this.onLongClickCallback = pyObject;
        this.linkAlias = str3;
    }

    public /* synthetic */ SettingItem(String str, String str2, PyObject pyObject, String str3, int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(str, (i & 2) != 0 ? null : str2, (i & 4) != 0 ? null : pyObject, (i & 8) != 0 ? null : str3);
    }

    public final String getType() {
        return this.type;
    }

    public final void setType(String str) {
        this.type = str;
    }

    public final String getIcon() {
        return this.icon;
    }

    public final void setIcon(String str) {
        this.icon = str;
    }

    public final PyObject getOnLongClickCallback() {
        return this.onLongClickCallback;
    }

    public final void setOnLongClickCallback(PyObject pyObject) {
        this.onLongClickCallback = pyObject;
    }

    public final String getLinkAlias() {
        return this.linkAlias;
    }

    public final void setLinkAlias(String str) {
        this.linkAlias = str;
    }

    public final void closeCallback(PyObject callback) {
        if (callback != null) {
            try {
                callback.close();
            } catch (Exception unused) {
            }
        }
    }

    public void cleanup() {
        closeCallback(this.onLongClickCallback);
        this.onLongClickCallback = null;
    }

    public final String getLink(String pluginId, String prefix) {
        String str;
        String str2 = this.linkAlias;
        if (str2 == null || str2.length() == 0 || pluginId == null || pluginId.length() == 0) {
            return null;
        }
        if (prefix == null) {
            str = this.linkAlias;
        } else {
            str = prefix + ':' + this.linkAlias;
        }
        return "https://t.me/exteraSettings?p=" + pluginId + "&s=" + str;
    }
}
