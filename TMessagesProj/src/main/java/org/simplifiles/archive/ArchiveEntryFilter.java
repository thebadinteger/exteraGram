package org.simplifiles.archive;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\bæ\u0080\u0001\u0018\u0000 \u00062\u00020\u0001:\u0001\u0006J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&¨\u0006\u0007À\u0006\u0003"}, d2 = {"Lorg/simplifiles/archive/ArchiveEntryFilter;", _UrlKt.FRAGMENT_ENCODE_SET, "include", _UrlKt.FRAGMENT_ENCODE_SET, "path", _UrlKt.FRAGMENT_ENCODE_SET, "Companion", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
public interface ArchiveEntryFilter {

    public static final Companion INSTANCE = Companion.$$INSTANCE;

    boolean include(String path);

    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0007¢\u0006\u0004\b\u0005\u0010\u0006¨\u0006\u0007"}, d2 = {"Lorg/simplifiles/archive/ArchiveEntryFilter$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "Lorg/simplifiles/archive/ArchiveEntryFilter;", "includeAll", "()Lorg/simplifiles/archive/ArchiveEntryFilter;", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
    @SourceDebugExtension({"SMAP\nArchiveEntryFilter.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ArchiveEntryFilter.kt\norg/simplifiles/archive/ArchiveEntryFilter$Companion\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,68:1\n13010#2,2:69\n13213#2,2:71\n*S KotlinDebug\n*F\n+ 1 ArchiveEntryFilter.kt\norg/simplifiles/archive/ArchiveEntryFilter$Companion\n*L\n56#1:69,2\n63#1:71,2\n*E\n"})
    public static final class Companion {
        static final /* synthetic */ Companion $$INSTANCE = new Companion();

        private Companion() {
        }

        public static boolean $r8$lambda$llDWakkHK5otUSnuh3wBg_9q7D0(String str) {
            return true;
        }

        @JvmStatic
        public final ArchiveEntryFilter includeAll() {
            return new ArchiveEntryFilter() { // from class: org.simplifiles.archive.ArchiveEntryFilter$Companion$$ExternalSyntheticLambda0
                @Override // org.simplifiles.archive.ArchiveEntryFilter
                public final boolean include(String str) {
                    return ArchiveEntryFilter.Companion.$r8$lambda$llDWakkHK5otUSnuh3wBg_9q7D0(str);
                }
            };
        }
    }
}
