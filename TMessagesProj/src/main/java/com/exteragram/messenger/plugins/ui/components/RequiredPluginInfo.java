package com.exteragram.messenger.plugins.ui.components;

import com.google.android.gms.cast.MediaTrack;
import kotlin.Metadata;
import kotlin.jvm.JvmField;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\r\n\u0002\b\u0005\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u000f\u001a\u00020\u0010J\u0006\u0010\u0011\u001a\u00020\u0010J\u0006\u0010\u0012\u001a\u00020\u0010J\u0006\u0010\u0013\u001a\u00020\u0003R\u0010\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\b\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\u0004\u0018\u00010\u00038\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0012\u0010\n\u001a\u00020\u000b8\u0006@\u0006X\u0087\u000e¢\u0006\u0002\n\u0000R\u0011\u0010\f\u001a\u00020\r8F¢\u0006\u0006\u001a\u0004\b\f\u0010\u000e¨\u0006\u0015"}, d2 = {"Lcom/exteragram/messenger/plugins/ui/components/RequiredPluginInfo;", _UrlKt.FRAGMENT_ENCODE_SET, "id", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;)V", "name", "requiredVersion", "currentVersion", "downloadUrl", "state", _UrlKt.FRAGMENT_ENCODE_SET, "isSatisfied", _UrlKt.FRAGMENT_ENCODE_SET, "()Z", "title", _UrlKt.FRAGMENT_ENCODE_SET, MediaTrack.ROLE_SUBTITLE, "actionText", "contentKey", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class RequiredPluginInfo {
    public static final int STATE_DISABLED = 1;
    public static final int STATE_MISSING = 0;
    public static final int STATE_OUTDATED = 2;
    public static final int STATE_SATISFIED = 3;

    @JvmField
    public String currentVersion;

    @JvmField
    public String downloadUrl;

    @JvmField
    public final String id;

    @JvmField
    public String name;

    @JvmField
    public String requiredVersion;

    @JvmField
    public int state;

    public RequiredPluginInfo(String str) {
        this.id = str;
    }

    public final boolean isSatisfied() {
        return this.state == 3;
    }

    public final CharSequence title() {
        String str = this.name;
        return str != null ? str : this.id;
    }

    public final CharSequence subtitle() {
        int i = this.state;
        if (i == 1) {
            return LocaleController.getString(R.string.PluginRequiredDisabled);
        }
        String str = _UrlKt.FRAGMENT_ENCODE_SET;
        if (i == 2) {
            int i2 = R.string.PluginRequiredOutdated;
            String str2 = this.currentVersion;
            if (str2 == null) {
                str2 = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            String str3 = this.requiredVersion;
            if (str3 != null) {
                str = str3;
            }
            return LocaleController.formatString(i2, str2, str);
        }
        if (i == 3) {
            int i3 = R.string.PluginVersionValue;
            String str4 = this.currentVersion;
            if (str4 != null) {
                str = str4;
            }
            return LocaleController.formatString(i3, str);
        }
        String str5 = this.requiredVersion;
        if (str5 == null || str5.length() == 0) {
            return LocaleController.getString(R.string.PluginRequiredMissing);
        }
        return LocaleController.formatString(R.string.PluginRequiredVersion, this.requiredVersion);
    }

    public final CharSequence actionText() {
        int i = this.state;
        if (i == 1) {
            return LocaleController.getString(R.string.Enable);
        }
        if (i == 2) {
            return LocaleController.getString(R.string.PluginRequiredUpdate);
        }
        if (i == 3) {
            return LocaleController.getString(R.string.PluginRequiredInstalled);
        }
        return LocaleController.getString(R.string.PluginRequiredDownload);
    }

    public final String contentKey() {
        return this.state + "|" + this.name + "|" + this.currentVersion + "|" + this.requiredVersion;
    }
}
