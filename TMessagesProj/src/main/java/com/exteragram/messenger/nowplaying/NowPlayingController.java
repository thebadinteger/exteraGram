package com.exteragram.messenger.nowplaying;

import androidx.collection.LruCache;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.api.db.DatabaseHelper;
import com.exteragram.messenger.api.dto.NowPlayingDTO;
import com.exteragram.messenger.api.dto.NowPlayingInfoDTO;
import com.exteragram.messenger.api.dto.ProfileDTO;
import com.exteragram.messenger.api.network.ApiClient;
import com.exteragram.messenger.api.network.ApiService;
import com.exteragram.messenger.debug.DebugConfig;
import com.exteragram.messenger.nowplaying.ui.components.NowPlayingCardData;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.exteragram.messenger.utils.network.ExteraHttpClient;
import com.exteragram.messenger.utils.network.RemoteUtils;
import com.sun.jna.Callback;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.LazyThreadSafetyMode;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.TuplesKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.DebugProbesKt;
import kotlin.coroutines.jvm.internal.SpillingKt;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.JvmField;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.SupervisorKt;
import kotlinx.serialization.KSerializer;
import kotlinx.serialization.descriptors.SerialDescriptor;
import kotlinx.serialization.encoding.CompositeEncoder;
import kotlinx.serialization.internal.ArrayListSerializer;
import kotlinx.serialization.internal.LongSerializer;
import kotlinx.serialization.internal.PluginExceptionsKt;
import kotlinx.serialization.internal.SerializationConstructorMarker;
import kotlinx.serialization.internal.StringSerializer;
import kotlinx.serialization.json.Json;
import kotlinx.serialization.json.JsonBuilder;
import kotlinx.serialization.json.JsonKt;
import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.ProfileMusicView;
import retrofit2.Response;

@Metadata(d1 = {"\u0000\u0092\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001:\u0002@AB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0007J\b\u0010\u0011\u001a\u00020\u000eH\u0007J8\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0018\u001a\u00020\u000e2\u0014\u0010\u0019\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u001b\u0012\u0004\u0012\u00020\u00150\u001aH\u0007J\u001a\u0010\u001c\u001a\u0004\u0018\u00010\u001b2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0082@¢\u0006\u0002\u0010\u001dJ6\u0010\u001e\u001a\u0016\u0012\u0004\u0012\u00020\u000e\u0012\f\u0012\n\u0012\u0004\u0012\u00020\n\u0018\u00010\u000b0\u001f2\u000e\u0010 \u001a\n\u0012\u0004\u0012\u00020\n\u0018\u00010\u000b2\b\u0010!\u001a\u0004\u0018\u00010\nH\u0002J\u0018\u0010\"\u001a\u00020#2\u000e\u0010\u0019\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010%0$H\u0007J*\u0010&\u001a\u00020#2\b\u0010'\u001a\u0004\u0018\u00010%2\b\b\u0002\u0010(\u001a\u00020\u000e2\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u000e0$H\u0007J\u0010\u0010)\u001a\u0004\u0018\u00010%H\u0082@¢\u0006\u0002\u0010*J\u0018\u0010+\u001a\u0004\u0018\u00010%2\u0006\u0010\u0014\u001a\u00020\u0015H\u0082@¢\u0006\u0002\u0010,J \u0010-\u001a\u00020\u000e2\b\u0010'\u001a\u0004\u0018\u00010%2\u0006\u0010(\u001a\u00020\u000eH\u0082@¢\u0006\u0002\u0010.J\u0018\u0010/\u001a\u0004\u0018\u00010%2\u0006\u0010\u0014\u001a\u00020\u0015H\u0082@¢\u0006\u0002\u0010,J \u00100\u001a\u0002012\u0006\u0010\u0014\u001a\u00020\u00152\b\u00102\u001a\u0004\u0018\u00010%H\u0082@¢\u0006\u0002\u00103J&\u00108\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000b2\u0006\u00109\u001a\u00020\n2\u0006\u0010:\u001a\u00020\nH\u0082@¢\u0006\u0002\u0010;J\u0012\u0010<\u001a\u00020=*\u00020>H\u0086@¢\u0006\u0002\u0010?R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\b\u001a\u0014\u0012\u0004\u0012\u00020\n\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u00104\u001a\u000205X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u00106\u001a\u000207X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006B"}, d2 = {"Lcom/exteragram/messenger/nowplaying/NowPlayingController;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "ARTISTS_SPLITTER", "Lkotlin/text/Regex;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "itunesCache", "Landroidx/collection/LruCache;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/nowplaying/NowPlayingController$ItunesTrack;", "shouldShowCard", _UrlKt.FRAGMENT_ENCODE_SET, "nowPlayingCardData", "Lcom/exteragram/messenger/nowplaying/ui/components/NowPlayingCardData;", "isSeparateStylesSupported", "getCurrentPlayingTrack", "Lkotlinx/coroutines/Job;", "userId", _UrlKt.FRAGMENT_ENCODE_SET, "savedMusic", "Lorg/telegram/tgnet/TLRPC$Document;", "checkApi", Callback.METHOD_NAME, "Ljava/util/function/BiConsumer;", "Lcom/exteragram/messenger/api/dto/NowPlayingDTO;", "processSavedMusic", "(Lorg/telegram/tgnet/TLRPC$Document;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "hasCommonArtist", "Lkotlin/Pair;", "baseArtists", "itunesArtists", "getNowPlayingInfo", _UrlKt.FRAGMENT_ENCODE_SET, "Ljava/util/function/Consumer;", "Lcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;", "updateNowPlayingInfo", "newNowPlaying", "cache", "getNowPlayingInfoInternal", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "fetchProfileNowPlayingInfo", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateNowPlayingInfoInternal", "(Lcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "dbGetNowPlaying", "dbUpdateNowPlaying", _UrlKt.FRAGMENT_ENCODE_SET, "data", "(JLcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "httpClient", "Lokhttp3/OkHttpClient;", "jsonParser", "Lkotlinx/serialization/json/Json;", "fetchItunesTrack", "performer", "title", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "await", "Lokhttp3/Response;", "Lokhttp3/Call;", "(Lokhttp3/Call;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "ItunesSearchResponse", "ItunesTrack", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nNowPlayingController.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NowPlayingController.kt\ncom/exteragram/messenger/nowplaying/NowPlayingController\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n+ 4 Json.kt\nkotlinx/serialization/json/Json\n+ 5 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,379:1\n1586#2:380\n1661#2,3:381\n777#2:384\n873#2,2:385\n1586#2:387\n1661#2,3:388\n777#2:391\n873#2,2:392\n1807#2,2:394\n1807#2,3:396\n1809#2:399\n426#3,11:400\n426#3,11:411\n426#3,11:424\n222#4:422\n1#5:423\n*S KotlinDebug\n*F\n+ 1 NowPlayingController.kt\ncom/exteragram/messenger/nowplaying/NowPlayingController\n*L\n132#1:380\n132#1:381,3\n132#1:384\n132#1:385,2\n186#1:387\n186#1:388,3\n187#1:391\n187#1:392,2\n191#1:394,2\n195#1:396,3\n191#1:399\n284#1:400,11\n291#1:411,11\n362#1:424,11\n352#1:422\n*E\n"})
public final class NowPlayingController {
    private static final OkHttpClient httpClient;
    private static final Json jsonParser;
    public static final NowPlayingController INSTANCE = new NowPlayingController();
    private static final Regex ARTISTS_SPLITTER = new Regex("(?i)\\s*(?:,|&|\\bfeat\\b\\.?|\\bft\\b\\.?)\\s*");
    private static final CoroutineScope scope = CoroutineScopeKt.CoroutineScope(SupervisorKt.SupervisorJob$default(null, 1, null).plus(Dispatchers.getMain()));
    private static final LruCache<String, List<ItunesTrack>> itunesCache = new LruCache<>(50);

    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.nowplaying.NowPlayingController", f = "NowPlayingController.kt", i = {0, 0, 0, 0, 0}, l = {348}, m = "fetchItunesTrack", n = {"performer", "title", "cacheKey", "httpUrl", "request"}, s = {"L$0", "L$1", "L$2", "L$3", "L$4"}, v = 1)
    public static final class AnonymousClass1 extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        int label;
        Object result;

        public C01541(Continuation<? super C01541> continuation) {
            super(continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return NowPlayingController.this.getNowPlayingInfoInternal(this);
        }
    }

    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.nowplaying.NowPlayingController", f = "NowPlayingController.kt", i = {0, 0, 0, 0, 0}, l = {150}, m = "processSavedMusic", n = {"savedMusic", "title", "author", "baseArtistsList", "baseDto"}, s = {"L$0", "L$1", "L$2", "L$3", "L$4"}, v = 1)
    public static final class C01551 extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        int label;
        BiConsumer<NowPlayingDTO, Long> $callback;
        final BiConsumer<NowPlayingDTO, Long> $callback;
            final Consumer<NowPlayingInfoDTO> $callback;
        int label;

        boolean $cache;
        final long $userId;
        Object L$0;
        Object L$1;
        int label;

        boolean $cache;
        final class ItunesSearchResponse {
        private final int resultCount;
        private final List<ItunesTrack> results;

        public static final Companion INSTANCE = new Companion(null);

        @JvmField
        private static final Lazy<KSerializer<Object>>[] $childSerializers = {null, LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, new Function0() { 
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return NowPlayingController.ItunesSearchResponse._childSerializers$_anonymous_();
            }
        })};

        public static final /* synthetic */ Unit invoke(Throwable th) {
                invoke2(th);
                return Unit.INSTANCE;
            }

            public final void invoke2(Throwable th) {
                call.cancel();
            }
        });
        call.enqueue(new okhttp3.Callback() { 
            @Override // okhttp3.Callback
            public void onResponse(Call call2, okhttp3.Response response) {
                cancellableContinuationImpl.resumeWith(Result.m2315constructorimpl(response));
            }

            @Override // okhttp3.Callback
            public void onFailure(Call call2, IOException e) {
                if (cancellableContinuationImpl.isCancelled()) {
                    return;
                }
                CancellableContinuation<okhttp3.Response> cancellableContinuation = cancellableContinuationImpl;
                Result.Companion companion = Result.INSTANCE;
                cancellableContinuation.resumeWith(Result.m2315constructorimpl(ResultKt.createFailure(e)));
            }
        });
        Object result = cancellableContinuationImpl.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result;
    }
}
