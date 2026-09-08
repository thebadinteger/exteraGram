package com.exteragram.messenger.plugins;

import androidx.mediarouter.media.MediaRouteProviderProtocol;
import com.chaquo.python.internal.Common;
import com.google.android.gms.cast.MediaTrack;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;

@Metadata(d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0003\n\u0002\b\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0002\b\u001e\b\u0007\u0018\u0000 =2\u00020\u0001:\u0001=B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\u0006\u0010\u001d\u001a\u00020\u0003J\b\u0010\u001e\u001a\u0004\u0018\u00010\u0003J\u0010\u0010\u001f\u001a\u00020 2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0003J\b\u0010!\u001a\u0004\u0018\u00010\u0003J\u0010\u0010\"\u001a\u00020 2\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003J\b\u0010#\u001a\u0004\u0018\u00010\u0003J\u0010\u0010$\u001a\u00020 2\b\u0010\f\u001a\u0004\u0018\u00010\u0003J\u0006\u0010%\u001a\u00020\u0003J\u0006\u0010\r\u001a\u00020\u000eJ\u000e\u0010&\u001a\u00020 2\u0006\u0010'\u001a\u00020\u000eJ\b\u0010(\u001a\u0004\u0018\u00010\u0013J\u0010\u0010)\u001a\u00020 2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013J\u0006\u0010*\u001a\u00020\u000eJ\b\u0010+\u001a\u0004\u0018\u00010\u0003J\u0006\u0010,\u001a\u00020\u0011J\b\u0010-\u001a\u0004\u0018\u00010\u0003J\u0010\u0010.\u001a\u00020 2\b\u0010\n\u001a\u0004\u0018\u00010\u0003J\u000e\u0010/\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0016J\b\u00100\u001a\u0004\u0018\u00010\u0003J\u0010\u00101\u001a\u00020 2\b\u0010\b\u001a\u0004\u0018\u00010\u0003J\b\u00102\u001a\u0004\u0018\u00010\u0003J\u0010\u00103\u001a\u00020 2\b\u0010\t\u001a\u0004\u0018\u00010\u0003J\u0016\u00104\u001a\u00020 2\u000e\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0016J\u0006\u0010\u0014\u001a\u00020\u000eJ\u000e\u00105\u001a\u00020 2\u0006\u00106\u001a\u00020\u000eJ\b\u00107\u001a\u0004\u0018\u00010\u0003J\u0010\u00108\u001a\u00020 2\b\u00109\u001a\u0004\u0018\u00010\u0003J\u0013\u0010:\u001a\u00020\u000e2\b\u0010;\u001a\u0004\u0018\u00010\u0001H\u0096\u0002J\b\u0010<\u001a\u00020\u0011H\u0016R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\n\u001a\u0004\u0018\u00010\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000b\u001a\u0004\u0018\u00010\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\f\u001a\u0004\u0018\u00010\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0003X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0011X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0012\u001a\u0004\u0018\u00010\u0013X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0014\u001a\u00020\u000eX\u0082\u000e¢\u0006\u0002\n\u0000R\u0016\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0016X\u0082\u000e¢\u0006\u0002\n\u0000R\u001c\u0010\u0017\u001a\u0004\u0018\u00010\u0018X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001c¨\u0006>"}, d2 = {"Lcom/exteragram/messenger/plugins/Plugin;", _UrlKt.FRAGMENT_ENCODE_SET, "id", _UrlKt.FRAGMENT_ENCODE_SET, "name", "<init>", "(Ljava/lang/String;Ljava/lang/String;)V", "engine", "appVersion", "sdkVersion", "version", MediaTrack.ROLE_DESCRIPTION, "author", "isEnabled", _UrlKt.FRAGMENT_ENCODE_SET, "pack", "index", _UrlKt.FRAGMENT_ENCODE_SET, MediaRouteProviderProtocol.SERVICE_DATA_ERROR, _UrlKt.FRAGMENT_ENCODE_SET, "isNotResponding", Common.ASSET_REQUIREMENTS, _UrlKt.FRAGMENT_ENCODE_SET, "cachedEngine", "Lcom/exteragram/messenger/plugins/PluginsController$PluginsEngine;", "getCachedEngine", "()Lcom/exteragram/messenger/plugins/PluginsController$PluginsEngine;", "setCachedEngine", "(Lcom/exteragram/messenger/plugins/PluginsController$PluginsEngine;)V", "getId", "getDescription", "setDescription", _UrlKt.FRAGMENT_ENCODE_SET, "getEngine", "setEngine", "getAuthor", "setAuthor", "getName", "setEnabled", "enabled", "getError", "setError", "hasError", "getPack", "getIndex", "getVersion", "setVersion", "getRequirements", "getAppVersion", "setAppVersion", "getSdkVersion", "setSdkVersion", "setRequirements", "setNotResponding", "notResponding", "getIcon", "setIcon", "link", "equals", "other", "hashCode", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class Plugin {

    public static final Companion INSTANCE = new Companion(null);
    private String appVersion;
    private String author;
    private transient PluginsController.PluginsEngine cachedEngine;
    private String description;
    private String engine;
    private volatile Throwable error;
    private final String id;
    private int index;
    private volatile boolean isEnabled;
    private volatile boolean isNotResponding;
    private final String name;
    private String pack;
    private List<String> requirements;
    private String sdkVersion;
    private String version;

    public Plugin(String str, String str2) {
        "id";
        "name";
        this.id = str;
        this.name = str2;
        this.appVersion = ">=12.9.0";
        this.sdkVersion = ">=" + PythonPluginsEngine.INSTANCE.getSDK_VERSION();
        this.version = "1.0";
        this.description = LocaleController.getString(R.string.PluginNoDescription);
        this.author = LocaleController.getString(R.string.PluginNoAuthor);
        this.index = -1;
        this.requirements = new ArrayList();
    }

    public final PluginsController.PluginsEngine getCachedEngine() {
        return this.cachedEngine;
    }

    public final void setCachedEngine(PluginsController.PluginsEngine pluginsEngine) {
        this.cachedEngine = pluginsEngine;
    }

    public final String getId() {
        return this.id;
    }

    public final String getDescription() {
        return this.description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final String getEngine() {
        return this.engine;
    }

    public final void setEngine(String engine) {
        this.engine = engine;
    }

    public final String getAuthor() {
        return this.author;
    }

    public final void setAuthor(String author) {
        this.author = author;
    }

    public final String getName() {
        return this.name;
    }

    public final boolean isEnabled() {
        return !hasError() && this.isEnabled;
    }

    public final void setEnabled(boolean enabled) {
        this.isEnabled = enabled && !hasError();
    }

    public final Throwable getError() {
        return this.error;
    }

    public final void setError(Throwable error) {
        this.error = error;
        if (hasError()) {
            this.isEnabled = false;
        }
    }

    public final boolean hasError() {
        return this.error != null;
    }

    public final String getPack() {
        return this.pack;
    }

    public final int getIndex() {
        return this.index;
    }

    public final String getVersion() {
        return this.version;
    }

    public final void setVersion(String version) {
        this.version = version;
    }

    public final List<String> getRequirements() {
        return this.requirements;
    }

    public final String getAppVersion() {
        return this.appVersion;
    }

    public final void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public final String getSdkVersion() {
        return this.sdkVersion;
    }

    public final void setSdkVersion(String sdkVersion) {
        this.sdkVersion = sdkVersion;
    }

    public final void setRequirements(List<String> requirements) {
        this.requirements = requirements;
    }

    public final boolean getIsNotResponding() {
        return this.isNotResponding;
    }

    public final void setNotResponding(boolean notResponding) {
        this.isNotResponding = notResponding;
    }

    public final String getIcon() {
        if (this.pack == null || this.index < 0) {
            return null;
        }
        return this.pack + '/' + this.index;
    }

    public final void setIcon(String link) {
        int iLastIndexOf$default;
        if (INSTANCE.isIconValid(link) && (iLastIndexOf$default = StringsKt.lastIndexOf$default((CharSequence) link, '/', 0, false, 6, (Object) null)) != -1) {
            String strSubstring = link.substring(0, iLastIndexOf$default);
            "substring(...)";
            this.pack = strSubstring;
            String strSubstring2 = link.substring(iLastIndexOf$default + 1);
            "substring(...)";
            this.index = Integer.parseInt(strSubstring2);
        }
    }

    public boolean equals(Object other) {
        if (other instanceof Plugin) {
            return Intrinsics.areEqual(((Plugin) other).id, this.id);
        }
        return this == other;
    }

    public int hashCode() {
        return this.id.hashCode();
    }

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u0002¨\u0006\b"}, d2 = {"Lcom/exteragram/messenger/plugins/Plugin$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "isIconValid", _UrlKt.FRAGMENT_ENCODE_SET, "input", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final boolean isIconValid(String input) {
            return input != null && new Regex("^[a-zA-Z][a-zA-Z0-9_]*/\\d+$").matches(input);
        }
    }
}
