package com.exteragram.messenger.nowplaying;

import java.util.Iterator;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\t\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u0000 \u000b2\b\u0012\u0004\u0012\u00020\u00000\u0001:\u0001\u000bB\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n¨\u0006\f"}, d2 = {"Lcom/exteragram/messenger/nowplaying/ServiceEmoji;", _UrlKt.FRAGMENT_ENCODE_SET, "documentId", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;IJ)V", "getDocumentId", "()J", "MUSIC", "SPOTIFY", "TELEGRAM", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public enum ServiceEmoji {
    MUSIC(5271627010681108586L),
    SPOTIFY(5271857023359681001L),
    TELEGRAM(5325674462522144646L);

    private final long documentId;
    private static final /* synthetic */ EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(values());

    public static final Companion INSTANCE = new Companion(null);

    public static EnumEntries<ServiceEmoji> getEntries() {
        return $ENTRIES;
    }

    ServiceEmoji(long j) {
        this.documentId = j;
    }

    public final long getDocumentId() {
        return this.documentId;
    }

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0007¨\u0006\b"}, d2 = {"Lcom/exteragram/messenger/nowplaying/ServiceEmoji$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "fromString", "Lcom/exteragram/messenger/nowplaying/ServiceEmoji;", "name", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    @SourceDebugExtension({"SMAP\nServiceEmoji.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ServiceEmoji.kt\ncom/exteragram/messenger/nowplaying/ServiceEmoji$Companion\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,14:1\n1#2:15\n*E\n"})
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final ServiceEmoji fromString(String name) {
            ServiceEmoji next;
            Iterator<ServiceEmoji> it = ServiceEmoji.getEntries().iterator();
            do {
                if (!it.hasNext()) {
                    next = null;
                    break;
                }
                next = it.next();
            } while (!StringsKt.equals(next.name(), name, true));
            ServiceEmoji serviceEmoji = next;
            return serviceEmoji == null ? ServiceEmoji.MUSIC : serviceEmoji;
        }
    }
}
