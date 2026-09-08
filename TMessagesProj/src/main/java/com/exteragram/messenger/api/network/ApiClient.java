package com.exteragram.messenger.api.network;

import com.exteragram.messenger.utils.network.ExteraHttpClient;
import com.google.gson.GsonBuilder;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import okhttp3.internal.url._UrlKt;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u001b\u0010\u0006\u001a\u00020\u00078FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\b\u0010\t¨\u0006\f"}, d2 = {"Lcom/exteragram/messenger/api/network/ApiClient;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "BASE_URL", _UrlKt.FRAGMENT_ENCODE_SET, "apiService", "Lcom/exteragram/messenger/api/network/ApiService;", "getApiService", "()Lcom/exteragram/messenger/api/network/ApiService;", "apiService$delegate", "Lkotlin/Lazy;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class ApiClient {
    private static final String BASE_URL = "https://api.exteragram.app/api/v1/";
    public static final ApiClient INSTANCE = new ApiClient();

    private static final Lazy apiService = LazyKt.lazy(new Function0() { 
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return ApiClient.$r8$lambda$JcOhXKjbPIl0NaCF8bPPXwCfQfo();
        }
    });

    private ApiClient() {
    }

    public final ApiService getApiService() {
        return (ApiService) apiService.getValue();
    }

    public static ApiService $r8$lambda$JcOhXKjbPIl0NaCF8bPPXwCfQfo() {
        return (ApiService) new Retrofit.Builder().baseUrl(BASE_URL).client(ExteraHttpClient.INSTANCE.getClient()).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().create())).build().create(ApiService.class);
    }
}
