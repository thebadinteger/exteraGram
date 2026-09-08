package com.exteragram.messenger;

import java.util.Iterator;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\b\n\u0002\b\u0014\b\u0086\u0081\u0002\u0018\u0000 \u00162\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u0016B\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010j\u0002\b\u0011j\u0002\b\u0012j\u0002\b\u0013j\u0002\b\u0014j\u0002\b\u0015¨\u0006\u0017"}, d2 = {"Lcom/exteragram/messenger/MainMenuItem;", _UrlKt.FRAGMENT_ENCODE_SET, "id", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;II)V", "getId", "()I", "DIVIDER", "PROFILE", "ARCHIVE", "BOTS", "NEW_GROUP", "CONTACTS", "NEW_CHANNEL", "CALLS", "SAVED", "SETTINGS", "PLUGINS", "BROWSER", "QR", "FEED", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public enum MainMenuItem {
    DIVIDER(-1),
    PROFILE(18),
    ARCHIVE(14),
    BOTS(105),
    NEW_GROUP(2),
    CONTACTS(6),
    NEW_CHANNEL(3),
    CALLS(10),
    SAVED(11),
    SETTINGS(8),
    PLUGINS(102),
    BROWSER(101),
    QR(17),
    FEED(106);

    private final int id;
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());

    public static final Companion INSTANCE = new Companion(null);

    @JvmStatic
    public static final MainMenuItem getById(int i) {
        return INSTANCE.getById(i);
    }

    public static EnumEntries<MainMenuItem> getEntries() {
        return $ENTRIES;
    }

    MainMenuItem(int i) {
        this.id = i;
    }

    public final int getId() {
        return this.id;
    }

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\u0004\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007¨\u0006\b"}, d2 = {"Lcom/exteragram/messenger/MainMenuItem$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "getById", "Lcom/exteragram/messenger/MainMenuItem;", "id", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    @SourceDebugExtension({"SMAP\nExteraConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ExteraConfig.kt\ncom/exteragram/messenger/MainMenuItem$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,658:1\n1#2:659\n*E\n"})
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final MainMenuItem getById(int id) {
            MainMenuItem next;
            Iterator<MainMenuItem> it = MainMenuItem.getEntries().iterator();
            while (it.hasNext()) {
                next = it.next();
                if (next.getId() == id) {
                    return next;
                }
            }
            next = null;
            return next;
        }
    }
}
