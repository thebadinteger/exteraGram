package com.exteragram.messenger.api.network;

import com.exteragram.messenger.api.dto.BoostySubscriberDTO;
import com.exteragram.messenger.api.dto.NowPlayingDTO;
import com.exteragram.messenger.api.dto.ProfileDTO;
import java.util.List;
import kotlin.Metadata;
import kotlin.coroutines.Continuation;
import okhttp3.internal.url._UrlKt;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

@Metadata(d1 = {"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\bf\u0018\u00002\u00020\u0001J&\u0010\u0002\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\n\b\u0003\u0010\u0006\u001a\u0004\u0018\u00010\u0007H§@¢\u0006\u0002\u0010\bJ$\u0010\t\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u00040\u00032\b\b\u0001\u0010\n\u001a\u00020\u0007H§@¢\u0006\u0002\u0010\bJ\u001e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00050\u00032\b\b\u0001\u0010\f\u001a\u00020\rH§@¢\u0006\u0002\u0010\u000eJ\u001e\u0010\u000f\u001a\b\u0012\u0004\u0012\u00020\u00100\u00032\b\b\u0001\u0010\f\u001a\u00020\rH§@¢\u0006\u0002\u0010\u000eJ\u001a\u0010\u0011\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00120\u00040\u0003H§@¢\u0006\u0002\u0010\u0013¨\u0006\u0014À\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/api/network/ApiService;", _UrlKt.FRAGMENT_ENCODE_SET, "getAllProfiles", "Lretrofit2/Response;", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/api/dto/ProfileDTO;", "etag", _UrlKt.FRAGMENT_ENCODE_SET, "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getUpdates", "since", "getProfile", "userId", _UrlKt.FRAGMENT_ENCODE_SET, "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getCurrentPlayingTrack", "Lcom/exteragram/messenger/api/dto/NowPlayingDTO;", "getBoostySubscribers", "Lcom/exteragram/messenger/api/dto/BoostySubscriberDTO;", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public interface ApiService {
    @GET("profiles")
    Object getAllProfiles(@Header("If-None-Match") String str, Continuation<? super Response<List<ProfileDTO>>> continuation);

    @GET("boosty-subscribers")
    Object getBoostySubscribers(Continuation<? super Response<List<BoostySubscriberDTO>>> continuation);

    @GET("profiles/{userId}/now-playing")
    Object getCurrentPlayingTrack(@Path("userId") long j, Continuation<? super Response<NowPlayingDTO>> continuation);

    @GET("profiles/{userId}")
    Object getProfile(@Path("userId") long j, Continuation<? super Response<ProfileDTO>> continuation);

    @GET("profiles/updates")
    Object getUpdates(@Query("since") String str, Continuation<? super Response<List<ProfileDTO>>> continuation);

    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    public static final class DefaultImpls {
    }

    static /* synthetic */ Object getAllProfiles$default(ApiService apiService, String str, Continuation continuation, int i, Object obj) {
        if (obj != null) {
            throw new UnsupportedOperationException("Super calls with default arguments not supported in this target, function: getAllProfiles");
        }
        if ((i & 1) != 0) {
            str = null;
        }
        return apiService.getAllProfiles(str, continuation);
    }
}
