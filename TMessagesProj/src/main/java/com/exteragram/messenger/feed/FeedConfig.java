package com.exteragram.messenger.feed;

import android.content.SharedPreferences;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.ApplicationLoader;

@Metadata(d1 = {"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0011\n\u0002\u0010\u001e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\u0018\u0000 /2\u00020\u0001:\u00020/B\u0011\b\u0002\u0012\u0006\u0010\u0003\u001a\u00020\u0002¢\u0006\u0004\b\u0004\u0010\u0005J\u001d\u0010\n\u001a\u00020\t2\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006H\u0002¢\u0006\u0004\b\n\u0010\u000bJ\r\u0010\r\u001a\u00020\f¢\u0006\u0004\b\r\u0010\u000eJ\u0015\u0010\u0010\u001a\u00020\t2\u0006\u0010\u000f\u001a\u00020\f¢\u0006\u0004\b\u0010\u0010\u0011J\u0015\u0010\u0013\u001a\u00020\f2\u0006\u0010\u0012\u001a\u00020\u0007¢\u0006\u0004\b\u0013\u0010\u0014J\u001d\u0010\u0016\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00072\u0006\u0010\u0015\u001a\u00020\f¢\u0006\u0004\b\u0016\u0010\u0017J\u0013\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\u0004\b\u0018\u0010\u0019J\u001b\u0010\u001b\u001a\u00020\t2\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00070\u0006¢\u0006\u0004\b\u001b\u0010\u000bJ\r\u0010\u001c\u001a\u00020\t¢\u0006\u0004\b\u001c\u0010\u001dJ\u001b\u0010\u001f\u001a\u00020\t2\f\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00070\u001e¢\u0006\u0004\b\u001f\u0010 J\r\u0010!\u001a\u00020\u0002¢\u0006\u0004\b!\u0010\"J\r\u0010$\u001a\u00020#¢\u0006\u0004\b$\u0010%R\u0014\u0010'\u001a\u00020&8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b'\u0010(R\u001c\u0010)\u001a\b\u0012\u0004\u0012\u00020\u00070\u00068\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b)\u0010*R\u0016\u0010+\u001a\u00020\f8\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b+\u0010,R\u0016\u0010-\u001a\u00020\u00028\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b-\u0010.¨\u00061"}, d2 = {"Lcom/exteragram/messenger/feed/FeedConfig;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "account", "<init>", "(I)V", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "updated", _UrlKt.FRAGMENT_ENCODE_SET, "applyExcluded", "(Ljava/util/Set;)V", _UrlKt.FRAGMENT_ENCODE_SET, "isIncludeArchived", "()Z", "value", "setIncludeArchived", "(Z)V", "dialogId", "isExcluded", "(J)Z", "excluded", "setExcluded", "(JZ)V", "getExcludedSnapshot", "()Ljava/util/Set;", "ids", "removeExcluded", "clearExcluded", "()V", _UrlKt.FRAGMENT_ENCODE_SET, "excludeAll", "(Ljava/util/Collection;)V", "getGeneration", "()I", "Lcom/exteragram/messenger/feed/FeedConfig$Snapshot;", "snapshot", "()Lcom/exteragram/messenger/feed/FeedConfig$Snapshot;", "Landroid/content/SharedPreferences;", "preferences", "Landroid/content/SharedPreferences;", "excludedChannels", "Ljava/util/Set;", "includeArchived", "Z", "generation", "I", "Companion", "Snapshot", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nFeedConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 FeedConfig.kt\ncom/exteragram/messenger/feed/FeedConfig\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 4 SharedPreferences.kt\nandroidx/core/content/SharedPreferencesKt\n*L\n1#1,143:1\n1651#2:144\n1915#2:145\n1916#2:147\n1652#2:148\n1661#2,3:167\n1#3:146\n41#4,12:149\n41#4,6:161\n47#4,6:170\n*S KotlinDebug\n*F\n+ 1 FeedConfig.kt\ncom/exteragram/messenger/feed/FeedConfig\n*L\n36#1:144\n36#1:145\n36#1:147\n36#1:148\n111#1:167,3\n36#1:146\n50#1:149,12\n111#1:161,6\n111#1:170,6\n*E\n"})
public final class FeedConfig {

    public static final Companion INSTANCE = new Companion(null);
    private static final FeedConfig[] instances = new FeedConfig[16];
    private static final Object[] lockObjects;
    private volatile Set<Long> excludedChannels;
    private volatile int generation;
    private volatile boolean includeArchived;
    private final SharedPreferences preferences;

    public /* synthetic */ FeedConfig(int i, DefaultConstructorMarker defaultConstructorMarker) {
        this(i);
    }

    @JvmStatic
    public static final FeedConfig getInstance(int i) {
        return INSTANCE.getInstance(i);
    }

    private FeedConfig(int i) {
        Set<Long> setEmptySet;
        SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("feedconfig" + i, 0);
        this.preferences = sharedPreferences;
        this.excludedChannels = SetsKt.emptySet();
        this.includeArchived = sharedPreferences.getBoolean("includeArchived", false);
        Set<String> stringSet = sharedPreferences.getStringSet("excludedChannels", null);
        if (stringSet != null) {
            setEmptySet = new HashSet<>();
            Iterator<T> it = stringSet.iterator();
            while (it.hasNext()) {
                Long longOrNull = StringsKt.toLongOrNull((String) it.next());
                if (longOrNull != null) {
                    setEmptySet.add(longOrNull);
                }
            }
        } else {
            setEmptySet = SetsKt.emptySet();
        }
        this.excludedChannels = setEmptySet;
    }

    @Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\"\n\u0002\u0010\t\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0010\b\u0086\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u0012\u0006\u0010\b\u001a\u00020\u0007¢\u0006\u0004\b\t\u0010\nJ\u0010\u0010\f\u001a\u00020\u000bHÖ\u0001¢\u0006\u0004\b\f\u0010\rJ\u0010\u0010\u000e\u001a\u00020\u0007HÖ\u0001¢\u0006\u0004\b\u000e\u0010\u000fJ\u001a\u0010\u0011\u001a\u00020\u00022\b\u0010\u0010\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b\u0011\u0010\u0012R\u0017\u0010\u0003\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0003\u0010\u0013\u001a\u0004\b\u0014\u0010\u0015R\u001d\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00050\u00048\u0006¢\u0006\f\n\u0004\b\u0006\u0010\u0016\u001a\u0004\b\u0017\u0010\u0018R\u0017\u0010\b\u001a\u00020\u00078\u0006¢\u0006\f\n\u0004\b\b\u0010\u0019\u001a\u0004\b\u001a\u0010\u000f¨\u0006\u001b"}, d2 = {"Lcom/exteragram/messenger/feed/FeedConfig$Snapshot;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "includeArchived", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "excludedChannels", _UrlKt.FRAGMENT_ENCODE_SET, "generation", "<init>", "(ZLjava/util/Set;I)V", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "()Ljava/lang/String;", "hashCode", "()I", "other", "equals", "(Ljava/lang/Object;)Z", "Z", "getIncludeArchived", "()Z", "Ljava/util/Set;", "getExcludedChannels", "()Ljava/util/Set;", "I", "getGeneration", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final /* data */ class Snapshot {
        private final Set<Long> excludedChannels;
        private final int generation;
        private final boolean includeArchived;

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof Snapshot)) {
                return false;
            }
            Snapshot snapshot = (Snapshot) other;
            return this.includeArchived == snapshot.includeArchived && Intrinsics.areEqual(this.excludedChannels, snapshot.excludedChannels) && this.generation == snapshot.generation;
        }

        public int hashCode() {
            return (((Boolean.hashCode(this.includeArchived) * 31) + this.excludedChannels.hashCode()) * 31) + Integer.hashCode(this.generation);
        }

        public String toString() {
            return "Snapshot(includeArchived=" + this.includeArchived + ", excludedChannels=" + this.excludedChannels + ", generation=" + this.generation + ')';
        }

        public Snapshot(boolean z, Set<Long> set, int i) {
            this.includeArchived = z;
            this.excludedChannels = set;
            this.generation = i;
        }

        public final boolean getIncludeArchived() {
            return this.includeArchived;
        }

        public final Set<Long> getExcludedChannels() {
            return this.excludedChannels;
        }

        public final int getGeneration() {
            return this.generation;
        }
    }

    public final boolean getIncludeArchived() {
        return this.includeArchived;
    }

    public final synchronized void setIncludeArchived(boolean value) {
        if (this.includeArchived == value) {
            return;
        }
        this.includeArchived = value;
        this.generation++;
        SharedPreferences.Editor editorEdit = this.preferences.edit();
        editorEdit.putBoolean("includeArchived", value);
        editorEdit.apply();
    }

    public final boolean isExcluded(long dialogId) {
        return this.excludedChannels.contains(Long.valueOf(dialogId));
    }

    public final synchronized void setExcluded(long dialogId, boolean excluded) {
        boolean zRemove;
        try {
            HashSet hashSet = new HashSet(this.excludedChannels);
            if (excluded) {
                zRemove = hashSet.add(Long.valueOf(dialogId));
            } else {
                zRemove = hashSet.remove(Long.valueOf(dialogId));
            }
            if (zRemove) {
                applyExcluded(hashSet);
            }
        } catch (Throwable th) {
            throw th;
        }
    }

    public final Set<Long> getExcludedSnapshot() {
        return this.excludedChannels;
    }

    public final synchronized void removeExcluded(Set<Long> ids) {
        if (ids.isEmpty()) {
            return;
        }
        HashSet hashSet = new HashSet(this.excludedChannels);
        if (hashSet.removeAll(ids)) {
            applyExcluded(hashSet);
        }
    }

    public final synchronized void clearExcluded() {
        if (this.excludedChannels.isEmpty()) {
            return;
        }
        applyExcluded(SetsKt.emptySet());
    }

    public final synchronized void excludeAll(Collection<Long> ids) {
        HashSet hashSet = new HashSet(this.excludedChannels);
        if (hashSet.addAll(ids)) {
            applyExcluded(hashSet);
        }
    }

    private final void applyExcluded(Set<Long> updated) {
        this.excludedChannels = updated;
        this.generation++;
        SharedPreferences.Editor editorEdit = this.preferences.edit();
        HashSet hashSet = new HashSet();
        Iterator<T> it = updated.iterator();
        while (it.hasNext()) {
            hashSet.add(String.valueOf(((Number) it.next()).longValue()));
        }
        editorEdit.putStringSet("excludedChannels", hashSet);
        editorEdit.apply();
    }

    public final int getGeneration() {
        return this.generation;
    }

    public final synchronized Snapshot snapshot() {
        return new Snapshot(this.includeArchived, this.excludedChannels, this.generation);
    }

    @Metadata(d1 = {"\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\n\u001a\u00020\u00062\u0006\u0010\u000b\u001a\u00020\fH\u0007R\u0018\u0010\u0004\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00060\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0007R\u0016\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\t¨\u0006\r"}, d2 = {"Lcom/exteragram/messenger/feed/FeedConfig$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "instances", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/feed/FeedConfig;", "[Lcom/exteragram/messenger/feed/FeedConfig;", "lockObjects", "[Ljava/lang/Object;", "getInstance", "num", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final FeedConfig getInstance(int num) {
            FeedConfig feedConfig;
            FeedConfig feedConfig2 = FeedConfig.instances[num];
            if (feedConfig2 != null) {
                return feedConfig2;
            }
            synchronized (FeedConfig.lockObjects[num]) {
                try {
                    feedConfig = FeedConfig.instances[num];
                    if (feedConfig == null) {
                        feedConfig = new FeedConfig(num, null);
                        FeedConfig.instances[num] = feedConfig;
                    }
                    Unit unit = Unit.INSTANCE;
                } catch (Throwable th) {
                    throw th;
                }
            }
            return feedConfig;
        }
    }

    static {
        Object[] objArr = new Object[16];
        for (int i = 0; i < 16; i++) {
            objArr[i] = new Object();
        }
        lockObjects = objArr;
    }
}
