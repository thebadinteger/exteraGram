package com.exteragram.messenger.utils.network;

import java.util.concurrent.TimeUnit;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import okhttp3.OkHttpClient;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003R\u001b\u0010\u0004\u001a\u00020\u00058FX\u0086\u0084\u0002¢\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007¨\u0006\n"}, d2 = {"Lcom/exteragram/messenger/utils/network/ExteraHttpClient;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "client", "Lokhttp3/OkHttpClient;", "getClient", "()Lokhttp3/OkHttpClient;", "client$delegate", "Lkotlin/Lazy;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class ExteraHttpClient {
    public static final ExteraHttpClient INSTANCE = new ExteraHttpClient();

    private static final Lazy client = LazyKt.lazy(new Function0() { 
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return ExteraHttpClient.$r8$lambda$wzbHlla98iZftcZ62WKlPGXexp8();
        }
    });

    private ExteraHttpClient() {
    }

    public final OkHttpClient getClient() {
        return (OkHttpClient) client.getValue();
    }

    public static OkHttpClient $r8$lambda$wzbHlla98iZftcZ62WKlPGXexp8() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        TimeUnit timeUnit = TimeUnit.SECONDS;
        return builder.connectTimeout(10L, timeUnit).readTimeout(10L, timeUnit).writeTimeout(10L, timeUnit).build();
    }
}
