package com.exteragram.messenger.nowplaying;

import com.exteragram.messenger.api.dto.NowPlayingDTO;
import com.exteragram.messenger.api.network.ApiClient;
import com.exteragram.messenger.api.network.ApiService;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function2;
import kotlinx.coroutines.CoroutineScope;
import org.telegram.messenger.FileLog;
import retrofit2.Response;

@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "Lcom/exteragram/messenger/api/dto/NowPlayingDTO;", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
@DebugMetadata(c = "com.exteragram.messenger.nowplaying.NowPlayingController$getCurrentPlayingTrack$1$liveTrackDeferred$1", f = "NowPlayingController.kt", i = {}, l = {94}, m = "invokeSuspend", n = {}, s = {}, v = 1)
public final class NowPlayingController$getCurrentPlayingTrack$1$liveTrackDeferred$1 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super NowPlayingDTO>, Object> {
    final /* synthetic */ boolean $checkApi;
    final /* synthetic */ long $userId;
    int label;

    public NowPlayingController$getCurrentPlayingTrack$1$liveTrackDeferred$1(boolean z, long j, Continuation<? super NowPlayingController$getCurrentPlayingTrack$1$liveTrackDeferred$1> continuation) {
        super(2, (Continuation) continuation);
        this.$checkApi = z;
        this.$userId = j;
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
        return (Continuation) new NowPlayingController$getCurrentPlayingTrack$1$liveTrackDeferred$1(this.$checkApi, this.$userId, (Continuation) continuation);
    }

    @Override // kotlin.jvm.functions.Function2
    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super NowPlayingDTO> continuation) {
        return ((NowPlayingController$getCurrentPlayingTrack$1$liveTrackDeferred$1) (Object) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
    }

    @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
    public final Object invokeSuspend(Object obj) {
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i = this.label;
        try {
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                if (!this.$checkApi) {
                    return null;
                }
                ApiService apiService = ApiClient.INSTANCE.getApiService();
                long j = this.$userId;
                this.label = 1;
                obj = apiService.getCurrentPlayingTrack(j, this);
                if (obj == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
            Response response = (Response) obj;
            if (response.isSuccessful()) {
                return (NowPlayingDTO) response.body();
            }
            return null;
        } catch (Throwable th) {
            FileLog.e(th);
            return null;
        }
    }
}
