package com.exteragram.messenger.plugins.models;

import android.view.View;
import com.chaquo.python.PyObject;
import com.exteragram.messenger.plugins.Plugin;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;
import org.telegram.ui.Components.UItem;

@Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001:\u0001'B1\b\u0002\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\tB9\b\u0016\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\fB=\b\u0016\u0012\n\u0010\r\u001a\u0006\u0012\u0002\b\u00030\u000e\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\u000fBG\b\u0016\u0012\n\u0010\r\u001a\u0006\u0012\u0002\b\u00030\u000e\u0012\b\u0010\u0010\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\u0011B9\b\u0016\u0012\u0006\u0010\u0012\u001a\u00020\u0013\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¢\u0006\u0004\b\b\u0010\u0014J\b\u0010%\u001a\u00020&H\u0016R\u001c\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\u001c\u0010\u0004\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u0016\"\u0004\b\u001a\u0010\u0018R\u001c\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001eR \u0010\r\u001a\b\u0012\u0002\b\u0003\u0018\u00010\u000eX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001f\u0010 \"\u0004\b!\u0010\"R\u001c\u0010\u0010\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b#\u0010\u0016\"\u0004\b$\u0010\u0018¨\u0006("}, d2 = {"Lcom/exteragram/messenger/plugins/models/CustomSetting;", "Lcom/exteragram/messenger/plugins/models/SettingItem;", "onClickCallback", "Lcom/chaquo/python/PyObject;", "createSubFragmentCallback", "onLongClickCallback", "linkAlias", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Lcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Ljava/lang/String;)V", "item", "Lorg/telegram/ui/Components/UItem;", "(Lorg/telegram/ui/Components/UItem;Lcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Ljava/lang/String;)V", "factory", "Lcom/exteragram/messenger/plugins/models/CustomSetting$Factory;", "(Lcom/exteragram/messenger/plugins/models/CustomSetting$Factory;Lcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Ljava/lang/String;)V", "factoryArgs", "(Lcom/exteragram/messenger/plugins/models/CustomSetting$Factory;Lcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Ljava/lang/String;)V", "view", "Landroid/view/View;", "(Landroid/view/View;Lcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Lcom/chaquo/python/PyObject;Ljava/lang/String;)V", "getOnClickCallback", "()Lcom/chaquo/python/PyObject;", "setOnClickCallback", "(Lcom/chaquo/python/PyObject;)V", "getCreateSubFragmentCallback", "setCreateSubFragmentCallback", "getItem", "()Lorg/telegram/ui/Components/UItem;", "setItem", "(Lorg/telegram/ui/Components/UItem;)V", "getFactory", "()Lcom/exteragram/messenger/plugins/models/CustomSetting$Factory;", "setFactory", "(Lcom/exteragram/messenger/plugins/models/CustomSetting$Factory;)V", "getFactoryArgs", "setFactoryArgs", "cleanup", _UrlKt.FRAGMENT_ENCODE_SET, "Factory", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class CustomSetting extends SettingItem {
    private PyObject createSubFragmentCallback;
    private Factory<?> factory;
    private PyObject factoryArgs;
    private UItem item;
    private PyObject onClickCallback;

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

    private CustomSetting(PyObject pyObject, PyObject pyObject2, PyObject pyObject3, String str) {
        super("custom", null, pyObject3, str);
        this.onClickCallback = pyObject;
        this.createSubFragmentCallback = pyObject2;
    }

    public final UItem getItem() {
        return this.item;
    }

    public final void setItem(UItem uItem) {
        this.item = uItem;
    }

    public final Factory<?> getFactory() {
        return this.factory;
    }

    public final void setFactory(Factory<?> factory) {
        this.factory = factory;
    }

    public final PyObject getFactoryArgs() {
        return this.factoryArgs;
    }

    public final void setFactoryArgs(PyObject pyObject) {
        this.factoryArgs = pyObject;
    }

    public CustomSetting(UItem uItem, PyObject pyObject, PyObject pyObject2, PyObject pyObject3, String str) {
        this(pyObject, pyObject2, pyObject3, str);
        this.item = uItem;
        uItem.settingItem = this;
    }

    public CustomSetting(Factory<?> factory, PyObject pyObject, PyObject pyObject2, PyObject pyObject3, String str) {
        this(pyObject, pyObject2, pyObject3, str);
        this.factory = factory;
    }

    public CustomSetting(Factory<?> factory, PyObject pyObject, PyObject pyObject2, PyObject pyObject3, PyObject pyObject4, String str) {
        this(factory, pyObject2, pyObject3, pyObject4, str);
        this.factoryArgs = pyObject;
    }

    public CustomSetting(View view, PyObject pyObject, PyObject pyObject2, PyObject pyObject3, String str) {
        this(UItem.asCustom(view), pyObject, pyObject2, pyObject3, str);
    }

    @Override 
    public void cleanup() {
        super.cleanup();
        closeCallback(this.onClickCallback);
        closeCallback(this.createSubFragmentCallback);
        closeCallback(this.factoryArgs);
        this.onClickCallback = null;
        this.createSubFragmentCallback = null;
        this.factoryArgs = null;
    }

    @Metadata(d1 = {"\u0000:\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0006\b&\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u00022\b\u0012\u0004\u0012\u0002H\u00010\u0003B\u0007¢\u0006\u0004\b\u0004\u0010\u0005J$\u0010\r\u001a\u0004\u0018\u00010\u000e2\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u0016J \u0010\u0015\u001a\u00020\u00162\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020\u000e2\u0006\u0010\u0018\u001a\u00020\u0002H\u0016J \u0010\u0019\u001a\u00020\u00162\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0017\u001a\u00020\u000e2\u0006\u0010\u0018\u001a\u00020\u0002H\u0016J\b\u0010\u001a\u001a\u00020\u0007H\u0016J\b\u0010\u001b\u001a\u00020\u0007H\u0016R\u001a\u0010\u0006\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\b\"\u0004\b\t\u0010\nR\u001a\u0010\u000b\u001a\u00020\u0007X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\b\"\u0004\b\f\u0010\n¨\u0006\u001c"}, d2 = {"Lcom/exteragram/messenger/plugins/models/CustomSetting$Factory;", "V", "Landroid/view/View;", "Lorg/telegram/ui/Components/UItem$UItemFactory;", "<init>", "()V", "isShadowValue", _UrlKt.FRAGMENT_ENCODE_SET, "()Z", "setShadowValue", "(Z)V", "isClickableValue", "setClickableValue", "create", "Lorg/telegram/ui/Components/UItem;", "plugin", "Lcom/exteragram/messenger/plugins/Plugin;", "setting", "Lcom/exteragram/messenger/plugins/models/CustomSetting;", "args", "Lcom/chaquo/python/PyObject;", "onClick", _UrlKt.FRAGMENT_ENCODE_SET, "item", "view", "onLongClick", "isShadow", "isClickable", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static abstract class Factory<V extends View> extends UItem.UItemFactory<V> {
        private boolean isClickableValue = true;
        private boolean isShadowValue;

        public UItem create(Plugin plugin, CustomSetting setting, PyObject args) {
            return null;
        }

        public void onClick(Plugin plugin, UItem item, View view) {
        }

        public void onLongClick(Plugin plugin, UItem item, View view) {
        }

        public final boolean isShadowValue() {
            return this.isShadowValue;
        }

        public final void setShadowValue(boolean z) {
            this.isShadowValue = z;
        }

        public final boolean isClickableValue() {
            return this.isClickableValue;
        }

        public final void setClickableValue(boolean z) {
            this.isClickableValue = z;
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean getIsShadowValue() {
            return this.isShadowValue;
        }

        @Override // org.telegram.ui.Components.UItem.UItemFactory
        public boolean getIsClickableValue() {
            return this.isClickableValue;
        }
    }
}
