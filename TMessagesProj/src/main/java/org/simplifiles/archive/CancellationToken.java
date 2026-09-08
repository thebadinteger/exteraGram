package org.simplifiles.archive;

import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\bæ\u0080\u0001\u0018\u0000 \u00042\u00020\u0001:\u0001\u0004J\b\u0010\u0002\u001a\u00020\u0003H&¨\u0006\u0005À\u0006\u0003"}, d2 = {"Lorg/simplifiles/archive/CancellationToken;", _UrlKt.FRAGMENT_ENCODE_SET, "isCancellationRequested", _UrlKt.FRAGMENT_ENCODE_SET, "Companion", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
public interface CancellationToken {

    public static final Companion INSTANCE = Companion.$$INSTANCE;

    boolean isCancellationRequested();

    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000f\u0010\u0005\u001a\u00020\u0004H\u0007¢\u0006\u0004\b\u0005\u0010\u0006¨\u0006\u0007"}, d2 = {"Lorg/simplifiles/archive/CancellationToken$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "Lorg/simplifiles/archive/CancellationToken;", "none", "()Lorg/simplifiles/archive/CancellationToken;", "simplifiles"}, k = 1, mv = {2, 3, 0}, xi = 48)
    public static final class Companion {
        static final /* synthetic */ Companion $$INSTANCE = new Companion();

        private Companion() {
        }

        public static boolean $r8$lambda$QuY0Vc75RIx3apVDmURacr8ewVY() {
            return false;
        }

        @JvmStatic
        public final CancellationToken none() {
            return new CancellationToken() { // from class: org.simplifiles.archive.CancellationToken$Companion$$ExternalSyntheticLambda0
                @Override // org.simplifiles.archive.CancellationToken
                public final boolean isCancellationRequested() {
                    return CancellationToken.Companion.$r8$lambda$QuY0Vc75RIx3apVDmURacr8ewVY();
                }
            };
        }
    }
}
