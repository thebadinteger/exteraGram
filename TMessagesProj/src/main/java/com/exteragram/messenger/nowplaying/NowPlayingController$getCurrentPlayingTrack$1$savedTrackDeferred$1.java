package com.exteragram.messenger.nowplaying;

import com.exteragram.messenger.api.dto.NowPlayingDTO;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import org.telegram.tgnet.TLRPC;

@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "Lcom/exteragram/messenger/api/dto/NowPlayingDTO;", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
@DebugMetadata(c = "com.exteragram.messenger.nowplaying.NowPlayingController$getCurrentPlayingTrack$1$savedTrackDeferred$1", f = "NowPlayingController.kt", i = {}, l = {103}, m = "invokeSuspend", n = {}, s = {}, v = 1)
public final class NowPlayingController$getCurrentPlayingTrack$1$savedTrackDeferred$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super NowPlayingDTO>, Object> {
    final /* synthetic */ TLRPC.Document $savedMusic;
    int label;

    public NowPlayingController$getCurrentPlayingTrack$1$savedTrackDeferred$1(TLRPC.Document document, Continuation<? super NowPlayingController$getCurrentPlayingTrack$1$savedTrackDeferred$1> continuation) {
        super(2, continuation);
        this.$savedMusic = document;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        return new NowPlayingController$getCurrentPlayingTrack$1$savedTrackDeferred$1(this.$savedMusic, continuation);
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super NowPlayingDTO> continuation) {
        return ((NowPlayingController$getCurrentPlayingTrack$1$savedTrackDeferred$1) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        if (i != 0) {
            if (i == 1) {
                ResultKt.throwOnFailure(obj);
                return obj;
            }
            Segment$$ExternalSyntheticBUOutline1.m("call to 'resume' before 'invoke' with coroutine");
            return null;
        }
        ResultKt.throwOnFailure(obj);
        NowPlayingController nowPlayingController = NowPlayingController.INSTANCE;
        TLRPC.Document document = this.$savedMusic;
        this.label = 1;
        Object objProcessSavedMusic = nowPlayingController.processSavedMusic(document, this);
        return objProcessSavedMusic == coroutine_suspended ? coroutine_suspended : objProcessSavedMusic;
    }
}
