package com.exteragram.messenger.nowplaying;

import androidx.collection.LruCache;
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
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.CoroutineStart;
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
import com.exteragram.messenger.api.model.NowPlayingServiceType;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CancellableContinuation;
import kotlinx.coroutines.CancellableContinuationImpl;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Deferred;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.SupervisorKt;
import org.telegram.messenger.Utilities;
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

@Metadata(d1 = {"\u0000\u0092\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\bÆ\u0002\u0018\u00002\u00020\u0001:\u0002@AB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0012\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H\u0007J\b\u0010\u0011\u001a\u00020\u000eH\u0007J8\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u00172\u0006\u0010\u0018\u001a\u00020\u000e2\u0014\u0010\u0019\u001a\u0010\u0012\u0006\u0012\u0004\u0018\u00010\u001b\u0012\u0004\u0012\u00020\u00150\u001aH\u0007J\u001a\u0010\u001c\u001a\u0004\u0018\u00010\u001b2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H\u0082@¢\u0006\u0002\u0010\u001dJ6\u0010\u001e\u001a\u0016\u0012\u0004\u0012\u00020\u000e\u0012\f\u0012\n\u0012\u0004\u0012\u00020\n\u0018\u00010\u000b0\u001f2\u000e\u0010 \u001a\n\u0012\u0004\u0012\u00020\n\u0018\u00010\u000b2\b\u0010!\u001a\u0004\u0018\u00010\nH\u0002J\u0018\u0010\"\u001a\u00020#2\u000e\u0010\u0019\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010%0$H\u0007J*\u0010&\u001a\u00020#2\b\u0010'\u001a\u0004\u0018\u00010%2\b\b\u0002\u0010(\u001a\u00020\u000e2\f\u0010\u0019\u001a\b\u0012\u0004\u0012\u00020\u000e0$H\u0007J\u0010\u0010)\u001a\u0004\u0018\u00010%H\u0082@¢\u0006\u0002\u0010*J\u0018\u0010+\u001a\u0004\u0018\u00010%2\u0006\u0010\u0014\u001a\u00020\u0015H\u0082@¢\u0006\u0002\u0010,J \u0010-\u001a\u00020\u000e2\b\u0010'\u001a\u0004\u0018\u00010%2\u0006\u0010(\u001a\u00020\u000eH\u0082@¢\u0006\u0002\u0010.J\u0018\u0010/\u001a\u0004\u0018\u00010%2\u0006\u0010\u0014\u001a\u00020\u0015H\u0082@¢\u0006\u0002\u0010,J \u00100\u001a\u0002012\u0006\u0010\u0014\u001a\u00020\u00152\b\u00102\u001a\u0004\u0018\u00010%H\u0082@¢\u0006\u0002\u00103J&\u00108\u001a\n\u0012\u0004\u0012\u00020\f\u0018\u00010\u000b2\u0006\u00109\u001a\u00020\n2\u0006\u0010:\u001a\u00020\nH\u0082@¢\u0006\u0002\u0010;J\u0012\u0010<\u001a\u00020=*\u00020>H\u0086@¢\u0006\u0002\u0010?R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R \u0010\b\u001a\u0014\u0012\u0004\u0012\u00020\n\u0012\n\u0012\b\u0012\u0004\u0012\u00020\f0\u000b0\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u00104\u001a\u000205X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u00106\u001a\u000207X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006B"}, d2 = {"Lcom/exteragram/messenger/nowplaying/NowPlayingController;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "ARTISTS_SPLITTER", "Lkotlin/text/Regex;", "scope", "Lkotlinx/coroutines/CoroutineScope;", "itunesCache", "Landroidx/collection/LruCache;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/nowplaying/NowPlayingController$ItunesTrack;", "shouldShowCard", _UrlKt.FRAGMENT_ENCODE_SET, "nowPlayingCardData", "Lcom/exteragram/messenger/nowplaying/ui/components/NowPlayingCardData;", "isSeparateStylesSupported", "getCurrentPlayingTrack", "Lkotlinx/coroutines/Job;", "userId", _UrlKt.FRAGMENT_ENCODE_SET, "savedMusic", "Lorg/telegram/tgnet/TLRPC$Document;", "checkApi", "callback", "Ljava/util/function/BiConsumer;", "Lcom/exteragram/messenger/api/dto/NowPlayingDTO;", "processSavedMusic", "(Lorg/telegram/tgnet/TLRPC$Document;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "hasCommonArtist", "Lkotlin/Pair;", "baseArtists", "itunesArtists", "getNowPlayingInfo", _UrlKt.FRAGMENT_ENCODE_SET, "Ljava/util/function/Consumer;", "Lcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;", "updateNowPlayingInfo", "newNowPlaying", "cache", "getNowPlayingInfoInternal", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "fetchProfileNowPlayingInfo", "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateNowPlayingInfoInternal", "(Lcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "dbGetNowPlaying", "dbUpdateNowPlaying", _UrlKt.FRAGMENT_ENCODE_SET, "data", "(JLcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "httpClient", "Lokhttp3/OkHttpClient;", "jsonParser", "Lkotlinx/serialization/json/Json;", "fetchItunesTrack", "performer", "title", "(Ljava/lang/String;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "await", "Lokhttp3/Response;", "Lokhttp3/Call;", "(Lokhttp3/Call;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "ItunesSearchResponse", "ItunesTrack", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nNowPlayingController.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NowPlayingController.kt\ncom/exteragram/messenger/nowplaying/NowPlayingController\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n+ 4 Json.kt\nkotlinx/serialization/json/Json\n+ 5 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,379:1\n1586#2:380\n1661#2,3:381\n777#2:384\n873#2,2:385\n1586#2:387\n1661#2,3:388\n777#2:391\n873#2,2:392\n1807#2,2:394\n1807#2,3:396\n1809#2:399\n426#3,11:400\n426#3,11:411\n426#3,11:424\n222#4:422\n1#5:423\n*S KotlinDebug\n*F\n+ 1 NowPlayingController.kt\ncom/exteragram/messenger/nowplaying/NowPlayingController\n*L\n132#1:380\n132#1:381,3\n132#1:384\n132#1:385,2\n186#1:387\n186#1:388,3\n187#1:391\n187#1:392,2\n191#1:394,2\n195#1:396,3\n191#1:399\n284#1:400,11\n291#1:411,11\n362#1:424,11\n352#1:422\n*E\n"})
public final class NowPlayingController {
    private static final OkHttpClient httpClient;
    private static final Json jsonParser;
    public static final NowPlayingController INSTANCE = new NowPlayingController();
    private static final Regex ARTISTS_SPLITTER = new Regex("(?i)\\s*(?:,|&|\\bfeat\\b\\.?|\\bft\\b\\.?)\\s*");
    private static final CoroutineScope scope = CoroutineScopeKt.CoroutineScope(SupervisorKt.SupervisorJob(null).plus(Dispatchers.getMain()));
    private static final LruCache<String, List<ItunesTrack>> itunesCache = new LruCache<>(50);

    /* JADX INFO: renamed from: com.exteragram.messenger.nowplaying.NowPlayingController$fetchItunesTrack$1, reason: invalid class name */
    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.nowplaying.NowPlayingController", f = "NowPlayingController.kt", i = {0, 0, 0, 0, 0}, l = {348}, m = "fetchItunesTrack", n = {"performer", "title", "cacheKey", "httpUrl", "request"}, s = {"L$0", "L$1", "L$2", "L$3", "L$4"}, v = 1)
    public static final class AnonymousClass1 extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        int label;
        /* synthetic */ Object result;

        public AnonymousClass1(Continuation<?> continuation) {
            super((Continuation) continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return NowPlayingController.INSTANCE.fetchItunesTrack(null, null, this);
        }
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.nowplaying.NowPlayingController$getNowPlayingInfoInternal$1, reason: invalid class name and case insensitive filesystem */
    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.nowplaying.NowPlayingController", f = "NowPlayingController.kt", i = {0, 1}, l = {232, 232}, m = "getNowPlayingInfoInternal", n = {"userId", "userId"}, s = {"J$0", "J$0"}, v = 1)
    public static final class C01541 extends ContinuationImpl {
        long J$0;
        int label;
        /* synthetic */ Object result;

        public C01541(Continuation<? super C01541> continuation) {
            super((Continuation) continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return NowPlayingController.INSTANCE.getNowPlayingInfoInternal(this);
        }
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.nowplaying.NowPlayingController$processSavedMusic$1, reason: invalid class name and case insensitive filesystem */
    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.nowplaying.NowPlayingController", f = "NowPlayingController.kt", i = {0, 0, 0, 0, 0}, l = {150}, m = "processSavedMusic", n = {"savedMusic", "title", "author", "baseArtistsList", "baseDto"}, s = {"L$0", "L$1", "L$2", "L$3", "L$4"}, v = 1)
    public static final class C01551 extends ContinuationImpl {
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        int label;
        /* synthetic */ Object result;

        public C01551(Continuation<? super C01551> continuation) {
            super((Continuation) continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return NowPlayingController.INSTANCE.processSavedMusic(null, this);
        }
    }

    private NowPlayingController() {
    }

    static {
        OkHttpClient.Builder builderNewBuilder = ExteraHttpClient.INSTANCE.getClient().newBuilder();
        TimeUnit timeUnit = TimeUnit.SECONDS;
        httpClient = builderNewBuilder.connectTimeout(5L, timeUnit).readTimeout(5L, timeUnit).writeTimeout(5L, timeUnit).build();
        jsonParser = JsonKt.Json(Json.Default, builder -> {
            builder.setIgnoreUnknownKeys(true);
            return Unit.INSTANCE;
        });
    }

    @JvmStatic
    public static final boolean shouldShowCard(NowPlayingCardData nowPlayingCardData) {
        if (nowPlayingCardData == null) {
            return false;
        }
        return (Intrinsics.areEqual(nowPlayingCardData.getNowPlayingDTO().getPlatform(), "TELEGRAM") && isSeparateStylesSupported()) ? false : true;
    }

    @JvmStatic
    public static final boolean isSeparateStylesSupported() {
        return DebugConfig.getForceCompactSavedMusic() || RemoteUtils.getBooleanConfigValue("separate_music_styles", false).booleanValue();
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.nowplaying.NowPlayingController$getCurrentPlayingTrack$1, reason: invalid class name and case insensitive filesystem */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.nowplaying.NowPlayingController$getCurrentPlayingTrack$1", f = "NowPlayingController.kt", i = {0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2}, l = {106, 112, 115}, m = "invokeSuspend", n = {"$this$launch", "liveTrackDeferred", "savedTrackDeferred", "startTime", "$this$launch", "liveTrackDeferred", "savedTrackDeferred", "liveTrack", "startTime", "$this$launch", "liveTrackDeferred", "savedTrackDeferred", "liveTrack", "finalTrack", "startTime"}, s = {"L$0", "L$1", "L$2", "J$0", "L$0", "L$1", "L$2", "L$3", "J$0", "L$0", "L$1", "L$2", "L$3", "L$4", "J$0"}, v = 1)
    public static final class C01521 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ BiConsumer<NowPlayingDTO, Long> $callback;
        final /* synthetic */ boolean $checkApi;
        final /* synthetic */ TLRPC.Document $savedMusic;
        final /* synthetic */ long $userId;
        long J$0;
        private /* synthetic */ Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C01521(boolean z, long j, TLRPC.Document document, BiConsumer<NowPlayingDTO, Long> biConsumer, Continuation<? super C01521> continuation) {
            super(2, (Continuation) continuation);
            this.$checkApi = z;
            this.$userId = j;
            this.$savedMusic = document;
            this.$callback = biConsumer;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            C01521 c01521 = new C01521(this.$checkApi, this.$userId, this.$savedMusic, this.$callback, (Continuation) continuation);
            c01521.L$0 = obj;
            return (Continuation) c01521;
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C01521) (Object) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            CoroutineScope coroutineScope;
            long startTime;
            Deferred deferred;
            Deferred deferred2;
            NowPlayingDTO nowPlayingDTO;
            NowPlayingDTO finalTrack;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                coroutineScope = (CoroutineScope) this.L$0;
                startTime = System.currentTimeMillis();
                deferred = BuildersKt.async(coroutineScope, Dispatchers.getIO(), CoroutineStart.DEFAULT, new NowPlayingController$getCurrentPlayingTrack$1$liveTrackDeferred$1(this.$checkApi, this.$userId, null));
                deferred2 = BuildersKt.async(coroutineScope, Dispatchers.getIO(), CoroutineStart.DEFAULT, new NowPlayingController$getCurrentPlayingTrack$1$savedTrackDeferred$1(this.$savedMusic, null));
                this.L$0 = null;
                this.L$1 = deferred;
                this.L$2 = deferred2;
                this.J$0 = startTime;
                this.label = 1;
                obj = deferred.await(this);
                if (obj == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else if (i == 1) {
                startTime = this.J$0;
                deferred2 = (Deferred) this.L$2;
                deferred = (Deferred) this.L$1;
                ResultKt.throwOnFailure(obj);
            } else if (i == 2) {
                startTime = this.J$0;
                nowPlayingDTO = (NowPlayingDTO) this.L$3;
                deferred2 = (Deferred) this.L$2;
                deferred = (Deferred) this.L$1;
                ResultKt.throwOnFailure(obj);
                finalTrack = (NowPlayingDTO) obj;
                this.L$0 = null;
                this.L$1 = null;
                this.L$2 = null;
                this.L$3 = null;
                this.L$4 = null;
                this.J$0 = startTime;
                this.label = 3;
                if (BuildersKt.withContext(Dispatchers.getMain(), new C00211(this.$callback, finalTrack, startTime, null), this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
                return Unit.INSTANCE;
            } else if (i == 3) {
                ResultKt.throwOnFailure(obj);
                return Unit.INSTANCE;
            } else {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }

            nowPlayingDTO = (NowPlayingDTO) obj;
            if (nowPlayingDTO != null && nowPlayingDTO.isPlaying()) {
                deferred2.cancel(null);
                finalTrack = nowPlayingDTO;
            } else {
                this.L$0 = null;
                this.L$1 = deferred;
                this.L$2 = deferred2;
                this.L$3 = nowPlayingDTO;
                this.J$0 = startTime;
                this.label = 2;
                obj = deferred2.await(this);
                if (obj == coroutine_suspended) {
                    return coroutine_suspended;
                }
                finalTrack = (NowPlayingDTO) obj;
            }

            this.L$0 = null;
            this.L$1 = null;
            this.L$2 = null;
            this.L$3 = null;
            this.L$4 = null;
            this.J$0 = startTime;
            this.label = 3;
            if (BuildersKt.withContext(Dispatchers.getMain(), new C00211(this.$callback, finalTrack, startTime, null), this) == coroutine_suspended) {
                return coroutine_suspended;
            }
            return Unit.INSTANCE;
        }

        /* JADX INFO: renamed from: com.exteragram.messenger.nowplaying.NowPlayingController$getCurrentPlayingTrack$1$1, reason: invalid class name and collision with other inner class name */
        @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
        @DebugMetadata(c = "com.exteragram.messenger.nowplaying.NowPlayingController$getCurrentPlayingTrack$1$1", f = "NowPlayingController.kt", i = {}, l = {}, m = "invokeSuspend", n = {}, s = {}, v = 1)
        public static final class C00211 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
            final /* synthetic */ BiConsumer<NowPlayingDTO, Long> $callback;
            final /* synthetic */ NowPlayingDTO $finalTrack;
            final /* synthetic */ long $startTime;
            int label;

            /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
            public C00211(BiConsumer<NowPlayingDTO, Long> biConsumer, NowPlayingDTO nowPlayingDTO, long j, Continuation<? super C00211> continuation) {
                super(2, (Continuation) continuation);
                this.$callback = biConsumer;
                this.$finalTrack = nowPlayingDTO;
                this.$startTime = j;
            }

            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
            public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
                return (Continuation) new C00211(this.$callback, this.$finalTrack, this.$startTime, (Continuation) continuation);
            }

            @Override // kotlin.jvm.functions.Function2
            public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                return ((C00211) (Object) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
            }

            @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
            public final Object invokeSuspend(Object obj) {
                IntrinsicsKt.getCOROUTINE_SUSPENDED();
                if (this.label != 0) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
                this.$callback.accept(this.$finalTrack, Boxing.boxLong(System.currentTimeMillis() - this.$startTime));
                return Unit.INSTANCE;
            }
        }
    }

    @JvmStatic
    public static final Job getCurrentPlayingTrack(long userId, TLRPC.Document savedMusic, boolean checkApi, BiConsumer<NowPlayingDTO, Long> callback) {
        return BuildersKt.launch(scope, EmptyCoroutineContext.INSTANCE, CoroutineStart.DEFAULT, new C01521(checkApi, userId, savedMusic, callback, null));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:7:0x0017  */
    public final Object processSavedMusic(TLRPC.Document document, Continuation<? super NowPlayingDTO> continuation) {
        C01551 c01551;
        NowPlayingDTO nowPlayingDTO;
        String coverUrl;
        if (continuation instanceof C01551) {
            c01551 = (C01551) continuation;
            int i = c01551.label;
            if ((i & Integer.MIN_VALUE) != 0) {
                c01551.label = i - Integer.MIN_VALUE;
            } else {
                c01551 = new C01551((Continuation) continuation);
            }
        } else {
            c01551 = new C01551((Continuation) continuation);
        }
        Object objFetchItunesTrack = c01551.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i2 = c01551.label;
        Object obj = null;
        if (i2 == 0) {
            ResultKt.throwOnFailure(objFetchItunesTrack);
            if (document == null) {
                return null;
            }
            CharSequence title = ProfileMusicView.getTitle(document);
            String string = title instanceof String ? (String) title : null;
            if (string == null) {
                string = LocaleController.getString(R.string.AudioUnknownTitle);
            }
            String str = string;
            CharSequence author = ProfileMusicView.getAuthor(document);
            String string2 = author instanceof String ? (String) author : null;
            if (string2 == null) {
                string2 = LocaleController.getString(R.string.AudioUnknownArtist);
            }
            if (str == null || string2 == null) {
                return null;
            }
            int i3 = 0;
            List<String> listSplit = ARTISTS_SPLITTER.split(string2, 0);
            ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(listSplit, 10));
            Iterator<String> it = listSplit.iterator();
            while (it.hasNext()) {
                arrayList.add(StringsKt.trim((CharSequence) it.next()).toString());
            }
            ArrayList arrayList2 = new ArrayList();
            int size = arrayList.size();
            while (i3 < size) {
                Object obj2 = arrayList.get(i3);
                i3++;
                if (((String) obj2).length() > 0) {
                    arrayList2.add(obj2);
                }
            }
            nowPlayingDTO = new NowPlayingDTO(str, arrayList2, null, null, null, null, true, null, "TELEGRAM", null);
            if (isSeparateStylesSupported()) {
                return nowPlayingDTO;
            }
            c01551.L$0 = SpillingKt.nullOutSpilledVariable(document);
            c01551.L$1 = SpillingKt.nullOutSpilledVariable(str);
            c01551.L$2 = SpillingKt.nullOutSpilledVariable(string2);
            c01551.L$3 = SpillingKt.nullOutSpilledVariable(arrayList2);
            c01551.L$4 = nowPlayingDTO;
            c01551.label = 1;
            objFetchItunesTrack = fetchItunesTrack(string2, str, c01551);
            if (objFetchItunesTrack == coroutine_suspended) {
                return coroutine_suspended;
            }
        } else {
            if (i2 != 1) {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            NowPlayingDTO nowPlayingDTO2 = (NowPlayingDTO) c01551.L$4;
            ResultKt.throwOnFailure(objFetchItunesTrack);
            nowPlayingDTO = nowPlayingDTO2;
        }
        List list = (List) objFetchItunesTrack;
        List list2 = list;
        if (list2 == null || list2.isEmpty()) {
            return nowPlayingDTO;
        }
        for (Object obj3 : list) {
            ItunesTrack itunesTrack = (ItunesTrack) obj3;
            boolean zBooleanValue = INSTANCE.hasCommonArtist(nowPlayingDTO.getArtists(), itunesTrack.getArtistName()).component1().booleanValue();
            if (StringsKt.equals(itunesTrack.getTrackName(), nowPlayingDTO.getTrackName(), true) && zBooleanValue) {
                obj = obj3;
                break;
            }
        }
        ItunesTrack itunesTrack2 = (ItunesTrack) obj;
        if (itunesTrack2 == null) {
            itunesTrack2 = (ItunesTrack) CollectionsKt.first(list);
        }
        List<String> listComponent2 = hasCommonArtist(nowPlayingDTO.getArtists(), itunesTrack2.getArtistName()).component2();
        String collectionName = itunesTrack2.getCollectionName();
        if (collectionName == null) {
            collectionName = nowPlayingDTO.getAlbumName();
        }
        String str2 = collectionName;
        String artworkUrl100 = itunesTrack2.getArtworkUrl100();
        if (artworkUrl100 != null && artworkUrl100.length() != 0) {
            coverUrl = itunesTrack2.getArtworkUrl100().replace("100x100", "300x300");
        } else {
            coverUrl = nowPlayingDTO.getCoverUrl();
        }
        return NowPlayingDTO.copy$default(nowPlayingDTO, null, listComponent2, str2, coverUrl, null, null, false, null, nowPlayingDTO.getPlatform(), null, 753, null);
    }

    private final Pair<Boolean, List<String>> hasCommonArtist(List<String> baseArtists, String itunesArtists) {
        if (itunesArtists == null || StringsKt.isBlank(itunesArtists)) {
            return TuplesKt.to(Boolean.FALSE, baseArtists);
        }
        boolean z = false;
        List<String> listSplit = ARTISTS_SPLITTER.split(itunesArtists, 0);
        ArrayList arrayList = new ArrayList(CollectionsKt.collectionSizeOrDefault(listSplit, 10));
        Iterator<String> it = listSplit.iterator();
        while (it.hasNext()) {
            arrayList.add(StringsKt.trim((CharSequence) it.next()).toString());
        }
        ArrayList arrayList2 = new ArrayList();
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            if (((String) obj).length() > 0) {
                arrayList2.add(obj);
            }
        }
        List<String> list = baseArtists;
        if (list == null || list.isEmpty()) {
            return TuplesKt.to(Boolean.FALSE, arrayList2);
        }
        List<String> list2 = baseArtists;
        if (!(list2 instanceof Collection) || !list2.isEmpty()) {
            Iterator it2 = list2.iterator();
            loop2: while (it2.hasNext()) {
                String lowerCase = ((String) it2.next()).toLowerCase(Locale.ROOT);
                String strTranslitSafe = AndroidUtilities.translitSafe(lowerCase);
                if (!arrayList2.isEmpty()) {
                    int size2 = arrayList2.size();
                    int i2 = 0;
                    while (i2 < size2) {
                        Object obj2 = arrayList2.get(i2);
                        i2++;
                        String lowerCase2 = ((String) obj2).toLowerCase(Locale.ROOT);
                        String strTranslitSafe2 = AndroidUtilities.translitSafe(lowerCase2);
                        if (Intrinsics.areEqual(lowerCase2, lowerCase) || Intrinsics.areEqual(lowerCase2, strTranslitSafe) || Intrinsics.areEqual(strTranslitSafe2, lowerCase)) {
                            z = true;
                            break loop2;
                        }
                    }
                }
            }
        }
        if (z) {
            baseArtists = arrayList2;
        }
        return TuplesKt.to(Boolean.valueOf(z), baseArtists);
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.nowplaying.NowPlayingController$getNowPlayingInfo$1, reason: invalid class name and case insensitive filesystem */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.nowplaying.NowPlayingController$getNowPlayingInfo$1", f = "NowPlayingController.kt", i = {}, l = {212}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    public static final class C01531 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ Consumer<NowPlayingInfoDTO> $callback;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C01531(Consumer<NowPlayingInfoDTO> consumer, Continuation<? super C01531> continuation) {
            super(2, (Continuation) continuation);
            this.$callback = consumer;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation) new C01531(this.$callback, (Continuation) continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C01531) (Object) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                NowPlayingController nowPlayingController = NowPlayingController.INSTANCE;
                this.label = 1;
                obj = nowPlayingController.getNowPlayingInfoInternal(this);
                if (obj == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
            this.$callback.accept((NowPlayingInfoDTO) obj);
            return Unit.INSTANCE;
        }
    }

    @JvmStatic
    public static final void getNowPlayingInfo(Consumer<NowPlayingInfoDTO> callback) {
        BuildersKt.launch(scope, EmptyCoroutineContext.INSTANCE, CoroutineStart.DEFAULT, new C01531(callback, null));
    }

    public static /* synthetic */ void updateNowPlayingInfo$default(NowPlayingInfoDTO nowPlayingInfoDTO, boolean z, Consumer consumer, int i, Object obj) {
        if ((i & 2) != 0) {
            z = true;
        }
        updateNowPlayingInfo(nowPlayingInfoDTO, z, consumer);
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.nowplaying.NowPlayingController$updateNowPlayingInfo$1, reason: invalid class name and case insensitive filesystem */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.nowplaying.NowPlayingController$updateNowPlayingInfo$1", f = "NowPlayingController.kt", i = {}, l = {229}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    public static final class C01561 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ boolean $cache;
        final /* synthetic */ Consumer<Boolean> $callback;
        final /* synthetic */ NowPlayingInfoDTO $newNowPlaying;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C01561(NowPlayingInfoDTO nowPlayingInfoDTO, boolean z, Consumer<Boolean> consumer, Continuation<? super C01561> continuation) {
            super(2, (Continuation) continuation);
            this.$newNowPlaying = nowPlayingInfoDTO;
            this.$cache = z;
            this.$callback = consumer;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation) new C01561(this.$newNowPlaying, this.$cache, this.$callback, (Continuation) continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C01561) (Object) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                NowPlayingController nowPlayingController = NowPlayingController.INSTANCE;
                NowPlayingInfoDTO nowPlayingInfoDTO = this.$newNowPlaying;
                boolean z = this.$cache;
                this.label = 1;
                obj = nowPlayingController.updateNowPlayingInfoInternal(nowPlayingInfoDTO, z, this);
                if (obj == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
            this.$callback.accept(Boxing.boxBoolean(((Boolean) obj).booleanValue()));
            return Unit.INSTANCE;
        }
    }

    @JvmStatic
    @JvmOverloads
    public static final void updateNowPlayingInfo(NowPlayingInfoDTO newNowPlaying, boolean cache, Consumer<Boolean> callback) {
        BuildersKt.launch(scope, EmptyCoroutineContext.INSTANCE, CoroutineStart.DEFAULT, new C01561(newNowPlaying, cache, callback, null));
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    public final Object getNowPlayingInfoInternal(Continuation<? super NowPlayingInfoDTO> continuation) {
        C01541 c01541;
        long j;
        if (continuation instanceof C01541) {
            c01541 = (C01541) continuation;
            int i = c01541.label;
            if ((i & Integer.MIN_VALUE) != 0) {
                c01541.label = i - Integer.MIN_VALUE;
            } else {
                c01541 = new C01541((Continuation) continuation);
            }
        } else {
            c01541 = new C01541((Continuation) continuation);
        }
        Object objDbGetNowPlaying = c01541.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i2 = c01541.label;
        if (i2 == 0) {
            ResultKt.throwOnFailure(objDbGetNowPlaying);
            long clientUserId = ChatUtils.getInstance().getUserConfig().getClientUserId();
            c01541.J$0 = clientUserId;
            c01541.label = 1;
            objDbGetNowPlaying = dbGetNowPlaying(clientUserId, c01541);
            if (objDbGetNowPlaying != coroutine_suspended) {
                j = clientUserId;
            }
        }
        if (i2 != 1) {
            if (i2 == 2) {
                ResultKt.throwOnFailure(objDbGetNowPlaying);
                return objDbGetNowPlaying;
            }
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        j = c01541.J$0;
        ResultKt.throwOnFailure(objDbGetNowPlaying);
        NowPlayingInfoDTO nowPlayingInfoDTO = (NowPlayingInfoDTO) objDbGetNowPlaying;
        if (nowPlayingInfoDTO != null) {
            return nowPlayingInfoDTO;
        }
        c01541.J$0 = j;
        c01541.label = 2;
        Object objFetchProfileNowPlayingInfo = fetchProfileNowPlayingInfo(j, c01541);
        return objFetchProfileNowPlayingInfo == coroutine_suspended ? coroutine_suspended : objFetchProfileNowPlayingInfo;
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.nowplaying.NowPlayingController$fetchProfileNowPlayingInfo$2, reason: invalid class name */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u0004\u0018\u00010\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", "Lcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;", "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.nowplaying.NowPlayingController$fetchProfileNowPlayingInfo$2", f = "NowPlayingController.kt", i = {1, 1}, l = {237, 243}, m = "invokeSuspend", n = {"response", "profile"}, s = {"L$0", "L$1"}, v = 1)
    public static final class AnonymousClass2 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super NowPlayingInfoDTO>, Object> {
        final /* synthetic */ long $userId;
        Object L$0;
        Object L$1;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public AnonymousClass2(long j, Continuation<? super AnonymousClass2> continuation) {
            super(2, (Continuation) continuation);
            this.$userId = j;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation) new AnonymousClass2(this.$userId, (Continuation) continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super NowPlayingInfoDTO> continuation) {
            return ((AnonymousClass2) (Object) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            ProfileDTO profileDTO;
            ProfileDTO profileDTO2;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            try {
                if (i == 0) {
                    ResultKt.throwOnFailure(obj);
                    ApiService apiService = ApiClient.INSTANCE.getApiService();
                    long j = this.$userId;
                    this.label = 1;
                    obj = apiService.getProfile(j, this);
                    if (obj == coroutine_suspended) {
                    }
                    return coroutine_suspended;
                }
                if (i == 1) {
                    ResultKt.throwOnFailure(obj);
                } else {
                    if (i != 2) {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                    profileDTO2 = (ProfileDTO) this.L$1;
                    ResultKt.throwOnFailure(obj);
                }
                Response response = (Response) obj;
                if (!response.isSuccessful() || (profileDTO = (ProfileDTO) response.body()) == null) {
                    return null;
                }
                DatabaseHelper databaseHelper = DatabaseHelper.INSTANCE;
                List<ProfileDTO> listListOf = CollectionsKt.listOf(profileDTO);
                this.L$0 = SpillingKt.nullOutSpilledVariable(response);
                this.L$1 = profileDTO;
                this.label = 2;
                if (databaseHelper.insertProfiles(listListOf, this) != coroutine_suspended) {
                    profileDTO2 = profileDTO;
                    return profileDTO2.getNowPlaying();
                }
                return coroutine_suspended;
            } catch (Throwable th) {
                FileLog.e(th);
                return null;
            }
        }
    }

    private final Object fetchProfileNowPlayingInfo(long j, Continuation<? super NowPlayingInfoDTO> continuation) {
        return BuildersKt.withContext(Dispatchers.getIO(), new AnonymousClass2(j, null), continuation);
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.nowplaying.NowPlayingController$updateNowPlayingInfoInternal$2, reason: invalid class name and case insensitive filesystem */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u000b\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.nowplaying.NowPlayingController$updateNowPlayingInfoInternal$2", f = "NowPlayingController.kt", i = {0, 0, 1, 1}, l = {380, 270}, m = "invokeSuspend", n = {"query", "$i$f$suspendCancellableCoroutine", "query", "message"}, s = {"L$0", "I$0", "L$0", "L$1"}, v = 1)
    @SourceDebugExtension({"SMAP\nNowPlayingController.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NowPlayingController.kt\ncom/exteragram/messenger/nowplaying/NowPlayingController$updateNowPlayingInfoInternal$2\n+ 2 CancellableContinuation.kt\nkotlinx/coroutines/CancellableContinuationKt\n*L\n1#1,379:1\n426#2,11:380\n*S KotlinDebug\n*F\n+ 1 NowPlayingController.kt\ncom/exteragram/messenger/nowplaying/NowPlayingController$updateNowPlayingInfoInternal$2\n*L\n263#1:380,11\n*E\n"})
    public static final class C01572 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Boolean>, Object> {
        final /* synthetic */ boolean $cache;
        final /* synthetic */ NowPlayingInfoDTO $newNowPlaying;
        int I$0;
        Object L$0;
        Object L$1;
        boolean Z$0;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C01572(NowPlayingInfoDTO nowPlayingInfoDTO, boolean z, Continuation<? super C01572> continuation) {
            super(2, (Continuation) continuation);
            this.$newNowPlaying = nowPlayingInfoDTO;
            this.$cache = z;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation) new C01572(this.$newNowPlaying, this.$cache, (Continuation) continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Boolean> continuation) {
            return ((C01572) (Object) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            String str;
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            try {
                if (i == 0) {
                    ResultKt.throwOnFailure(obj);
                    if (this.$newNowPlaying == null || this.$newNowPlaying.getServiceType() == NowPlayingServiceType.NONE) {
                        str = "clear_now_playing";
                    } else {
                        str = "set_now_playing " + this.$newNowPlaying.getServiceType().name() + " " + this.$newNowPlaying.getUsername();
                    }
                    boolean z = this.$cache;
                    this.L$0 = str;
                    this.Z$0 = z;
                    this.I$0 = 0;
                    this.label = 1;
                    final CancellableContinuationImpl cancellableContinuationImpl = new CancellableContinuationImpl(IntrinsicsKt.intercepted(this), 1);
                    cancellableContinuationImpl.initCancellability();
                    ChatUtils.getInstance().sendBotRequest(str, z, new Utilities.Callback<String>() {
                        @Override
                        public final void run(String res) {
                            cancellableContinuationImpl.resume(res, null);
                        }
                    });
                    obj = cancellableContinuationImpl.getResult();
                    if (obj == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                        DebugProbesKt.probeCoroutineSuspended(this);
                    }
                    if (obj == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                } else if (i == 1) {
                    str = (String) this.L$0;
                    ResultKt.throwOnFailure(obj);
                } else if (i == 2) {
                    ResultKt.throwOnFailure(obj);
                    return Boxing.boxBoolean(((Number) obj).intValue() > 0);
                } else {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                String str2 = (String) obj;
                if (Intrinsics.areEqual(str2, "ok")) {
                    this.L$0 = str;
                    this.L$1 = str2;
                    this.label = 2;
                    obj = NowPlayingController.INSTANCE.dbUpdateNowPlaying(ChatUtils.getInstance().getUserConfig().getClientUserId(), this.$newNowPlaying, this);
                    if (obj == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                    return Boxing.boxBoolean(((Number) obj).intValue() > 0);
                }
                return Boxing.boxBoolean(false);
            } catch (Exception unused) {
                return Boxing.boxBoolean(false);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object updateNowPlayingInfoInternal(NowPlayingInfoDTO nowPlayingInfoDTO, boolean z, Continuation<? super Boolean> continuation) {
        return BuildersKt.withContext(Dispatchers.getIO(), new C01572(nowPlayingInfoDTO, z, null), continuation);
    }

    @Metadata(d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\n\b\u0087\b\u0018\u0000 #2\u00020\u0001:\u0002$#B3\b\u0010\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\u0006\u0010\u0004\u001a\u00020\u0002\u0012\u000e\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0005\u0012\b\u0010\t\u001a\u0004\u0018\u00010\b¢\u0006\u0004\b\n\u0010\u000bJ'\u0010\u0014\u001a\u00020\u00112\u0006\u0010\f\u001a\u00020\u00002\u0006\u0010\u000e\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u000fH\u0001¢\u0006\u0004\b\u0012\u0010\u0013J\u0010\u0010\u0016\u001a\u00020\u0015HÖ\u0001¢\u0006\u0004\b\u0016\u0010\u0017J\u0010\u0010\u0018\u001a\u00020\u0002HÖ\u0001¢\u0006\u0004\b\u0018\u0010\u0019J\u001a\u0010\u001c\u001a\u00020\u001b2\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b\u001c\u0010\u001dR\u0017\u0010\u0004\u001a\u00020\u00028\u0006¢\u0006\f\n\u0004\b\u0004\u0010\u001e\u001a\u0004\b\u001f\u0010\u0019R\u001d\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\u00060\u00058\u0006¢\u0006\f\n\u0004\b\u0007\u0010 \u001a\u0004\b!\u0010\"¨\u0006%"}, d2 = {"Lcom/exteragram/messenger/nowplaying/NowPlayingController$ItunesSearchResponse;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "seen0", "resultCount", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/nowplaying/NowPlayingController$ItunesTrack;", "results", "Lkotlinx/serialization/internal/SerializationConstructorMarker;", "serializationConstructorMarker", "<init>", "(IILjava/util/List;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V", "self", "Lkotlinx/serialization/encoding/CompositeEncoder;", "output", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "serialDesc", _UrlKt.FRAGMENT_ENCODE_SET, "write$Self$TMessagesProj", "(Lcom/exteragram/messenger/nowplaying/NowPlayingController$ItunesSearchResponse;Lkotlinx/serialization/encoding/CompositeEncoder;Lkotlinx/serialization/descriptors/SerialDescriptor;)V", "write$Self", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "()Ljava/lang/String;", "hashCode", "()I", "other", _UrlKt.FRAGMENT_ENCODE_SET, "equals", "(Ljava/lang/Object;)Z", "I", "getResultCount", "Ljava/util/List;", "getResults", "()Ljava/util/List;", "Companion", "$serializer", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final /* data */ class ItunesSearchResponse {
        private final int resultCount;
        private final List<ItunesTrack> results;

        /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
        public static final Companion INSTANCE = new Companion(null);

        @JvmField
        public static final Lazy[] $childSerializers = {null, LazyKt.lazy(LazyThreadSafetyMode.PUBLICATION, new Function0() { // from class: com.exteragram.messenger.nowplaying.NowPlayingController$ItunesSearchResponse$$ExternalSyntheticLambda0
            @Override // kotlin.jvm.functions.Function0
            public final Object invoke() {
                return NowPlayingController.ItunesSearchResponse._childSerializers$_anonymous_();
            }
        })};

        /* JADX INFO: Access modifiers changed from: private */
        public static final /* synthetic */ KSerializer _childSerializers$_anonymous_() {
            return new ArrayListSerializer(NowPlayingController$ItunesTrack$$serializer.INSTANCE);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ItunesSearchResponse)) {
                return false;
            }
            ItunesSearchResponse itunesSearchResponse = (ItunesSearchResponse) other;
            return this.resultCount == itunesSearchResponse.resultCount && Intrinsics.areEqual(this.results, itunesSearchResponse.results);
        }

        public int hashCode() {
            return (Integer.hashCode(this.resultCount) * 31) + this.results.hashCode();
        }

        public String toString() {
            return "ItunesSearchResponse(resultCount=" + this.resultCount + ", results=" + this.results + ')';
        }

        @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¨\u0006\u0007"}, d2 = {"Lcom/exteragram/messenger/nowplaying/NowPlayingController$ItunesSearchResponse$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "serializer", "Lkotlinx/serialization/KSerializer;", "Lcom/exteragram/messenger/nowplaying/NowPlayingController$ItunesSearchResponse;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            public final KSerializer<ItunesSearchResponse> serializer() {
                return NowPlayingController$ItunesSearchResponse$$serializer.INSTANCE;
            }
        }

        public /* synthetic */ ItunesSearchResponse(int i, int i2, List list, SerializationConstructorMarker serializationConstructorMarker) {
            if (3 != (i & 3)) {
                PluginExceptionsKt.throwMissingFieldException(i, 3, NowPlayingController$ItunesSearchResponse$$serializer.INSTANCE.getDescriptor());
            }
            this.resultCount = i2;
            this.results = list;
        }

        @JvmStatic
        public static final /* synthetic */ void write$Self$TMessagesProj(ItunesSearchResponse self, CompositeEncoder output, SerialDescriptor serialDesc) {
            Lazy<KSerializer<Object>>[] lazyArr = $childSerializers;
            output.encodeIntElement(serialDesc, 0, self.resultCount);
            output.encodeSerializableElement(serialDesc, 1, lazyArr[1].getValue(), self.results);
        }

        public final List<ItunesTrack> getResults() {
            return this.results;
        }
    }

    @Metadata(d1 = {"\u0000@\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\b\n\u0002\u0010\u000b\n\u0002\b\u000e\b\u0087\b\u0018\u0000 *2\u00020\u0001:\u0002+*BW\b\u0010\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0004\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0004\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0004\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0004\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0004\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\n\u0012\b\u0010\r\u001a\u0004\u0018\u00010\f¢\u0006\u0004\b\u000e\u0010\u000fJ'\u0010\u0018\u001a\u00020\u00152\u0006\u0010\u0010\u001a\u00020\u00002\u0006\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0014\u001a\u00020\u0013H\u0001¢\u0006\u0004\b\u0016\u0010\u0017J\u0010\u0010\u0019\u001a\u00020\u0004HÖ\u0001¢\u0006\u0004\b\u0019\u0010\u001aJ\u0010\u0010\u001b\u001a\u00020\u0002HÖ\u0001¢\u0006\u0004\b\u001b\u0010\u001cJ\u001a\u0010\u001f\u001a\u00020\u001e2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001HÖ\u0003¢\u0006\u0004\b\u001f\u0010 R\u0019\u0010\u0005\u001a\u0004\u0018\u00010\u00048\u0006¢\u0006\f\n\u0004\b\u0005\u0010!\u001a\u0004\b\"\u0010\u001aR\u0019\u0010\u0006\u001a\u0004\u0018\u00010\u00048\u0006¢\u0006\f\n\u0004\b\u0006\u0010!\u001a\u0004\b#\u0010\u001aR\u0019\u0010\u0007\u001a\u0004\u0018\u00010\u00048\u0006¢\u0006\f\n\u0004\b\u0007\u0010!\u001a\u0004\b$\u0010\u001aR\u0019\u0010\b\u001a\u0004\u0018\u00010\u00048\u0006¢\u0006\f\n\u0004\b\b\u0010!\u001a\u0004\b%\u0010\u001aR\u0019\u0010\t\u001a\u0004\u0018\u00010\u00048\u0006¢\u0006\f\n\u0004\b\t\u0010!\u001a\u0004\b&\u0010\u001aR\u0019\u0010\u000b\u001a\u0004\u0018\u00010\n8\u0006¢\u0006\f\n\u0004\b\u000b\u0010'\u001a\u0004\b(\u0010)¨\u0006,"}, d2 = {"Lcom/exteragram/messenger/nowplaying/NowPlayingController$ItunesTrack;", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "seen0", _UrlKt.FRAGMENT_ENCODE_SET, "trackName", "artistName", "collectionName", "artworkUrl100", "previewUrl", _UrlKt.FRAGMENT_ENCODE_SET, "trackTimeMillis", "Lkotlinx/serialization/internal/SerializationConstructorMarker;", "serializationConstructorMarker", "<init>", "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/Long;Lkotlinx/serialization/internal/SerializationConstructorMarker;)V", "self", "Lkotlinx/serialization/encoding/CompositeEncoder;", "output", "Lkotlinx/serialization/descriptors/SerialDescriptor;", "serialDesc", _UrlKt.FRAGMENT_ENCODE_SET, "write$Self$TMessagesProj", "(Lcom/exteragram/messenger/nowplaying/NowPlayingController$ItunesTrack;Lkotlinx/serialization/encoding/CompositeEncoder;Lkotlinx/serialization/descriptors/SerialDescriptor;)V", "write$Self", "toString", "()Ljava/lang/String;", "hashCode", "()I", "other", _UrlKt.FRAGMENT_ENCODE_SET, "equals", "(Ljava/lang/Object;)Z", "Ljava/lang/String;", "getTrackName", "getArtistName", "getCollectionName", "getArtworkUrl100", "getPreviewUrl", "Ljava/lang/Long;", "getTrackTimeMillis", "()Ljava/lang/Long;", "Companion", "$serializer", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final /* data */ class ItunesTrack {

        /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
        public static final Companion INSTANCE = new Companion(null);
        private final String artistName;
        private final String artworkUrl100;
        private final String collectionName;
        private final String previewUrl;
        private final String trackName;
        private final Long trackTimeMillis;

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ItunesTrack)) {
                return false;
            }
            ItunesTrack itunesTrack = (ItunesTrack) other;
            return Intrinsics.areEqual(this.trackName, itunesTrack.trackName) && Intrinsics.areEqual(this.artistName, itunesTrack.artistName) && Intrinsics.areEqual(this.collectionName, itunesTrack.collectionName) && Intrinsics.areEqual(this.artworkUrl100, itunesTrack.artworkUrl100) && Intrinsics.areEqual(this.previewUrl, itunesTrack.previewUrl) && Intrinsics.areEqual(this.trackTimeMillis, itunesTrack.trackTimeMillis);
        }

        public int hashCode() {
            String str = this.trackName;
            int iHashCode = (str == null ? 0 : str.hashCode()) * 31;
            String str2 = this.artistName;
            int iHashCode2 = (iHashCode + (str2 == null ? 0 : str2.hashCode())) * 31;
            String str3 = this.collectionName;
            int iHashCode3 = (iHashCode2 + (str3 == null ? 0 : str3.hashCode())) * 31;
            String str4 = this.artworkUrl100;
            int iHashCode4 = (iHashCode3 + (str4 == null ? 0 : str4.hashCode())) * 31;
            String str5 = this.previewUrl;
            int iHashCode5 = (iHashCode4 + (str5 == null ? 0 : str5.hashCode())) * 31;
            Long l = this.trackTimeMillis;
            return iHashCode5 + (l != null ? l.hashCode() : 0);
        }

        public String toString() {
            return "ItunesTrack(trackName=" + this.trackName + ", artistName=" + this.artistName + ", collectionName=" + this.collectionName + ", artworkUrl100=" + this.artworkUrl100 + ", previewUrl=" + this.previewUrl + ", trackTimeMillis=" + this.trackTimeMillis + ')';
        }

        @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¨\u0006\u0007"}, d2 = {"Lcom/exteragram/messenger/nowplaying/NowPlayingController$ItunesTrack$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "serializer", "Lkotlinx/serialization/KSerializer;", "Lcom/exteragram/messenger/nowplaying/NowPlayingController$ItunesTrack;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
        public static final class Companion {
            public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
                this();
            }

            private Companion() {
            }

            public final KSerializer<ItunesTrack> serializer() {
                return NowPlayingController$ItunesTrack$$serializer.INSTANCE;
            }
        }

        public /* synthetic */ ItunesTrack(int i, String str, String str2, String str3, String str4, String str5, Long l, SerializationConstructorMarker serializationConstructorMarker) {
            if ((i & 1) == 0) {
                this.trackName = null;
            } else {
                this.trackName = str;
            }
            if ((i & 2) == 0) {
                this.artistName = null;
            } else {
                this.artistName = str2;
            }
            if ((i & 4) == 0) {
                this.collectionName = null;
            } else {
                this.collectionName = str3;
            }
            if ((i & 8) == 0) {
                this.artworkUrl100 = null;
            } else {
                this.artworkUrl100 = str4;
            }
            if ((i & 16) == 0) {
                this.previewUrl = null;
            } else {
                this.previewUrl = str5;
            }
            if ((i & 32) == 0) {
                this.trackTimeMillis = null;
            } else {
                this.trackTimeMillis = l;
            }
        }

        @JvmStatic
        public static final /* synthetic */ void write$Self$TMessagesProj(ItunesTrack self, CompositeEncoder output, SerialDescriptor serialDesc) {
            if (output.shouldEncodeElementDefault(serialDesc, 0) || self.trackName != null) {
                output.encodeNullableSerializableElement(serialDesc, 0, StringSerializer.INSTANCE, self.trackName);
            }
            if (output.shouldEncodeElementDefault(serialDesc, 1) || self.artistName != null) {
                output.encodeNullableSerializableElement(serialDesc, 1, StringSerializer.INSTANCE, self.artistName);
            }
            if (output.shouldEncodeElementDefault(serialDesc, 2) || self.collectionName != null) {
                output.encodeNullableSerializableElement(serialDesc, 2, StringSerializer.INSTANCE, self.collectionName);
            }
            if (output.shouldEncodeElementDefault(serialDesc, 3) || self.artworkUrl100 != null) {
                output.encodeNullableSerializableElement(serialDesc, 3, StringSerializer.INSTANCE, self.artworkUrl100);
            }
            if (output.shouldEncodeElementDefault(serialDesc, 4) || self.previewUrl != null) {
                output.encodeNullableSerializableElement(serialDesc, 4, StringSerializer.INSTANCE, self.previewUrl);
            }
            if (!output.shouldEncodeElementDefault(serialDesc, 5) && self.trackTimeMillis == null) {
                return;
            }
            output.encodeNullableSerializableElement(serialDesc, 5, LongSerializer.INSTANCE, self.trackTimeMillis);
        }

        public final String getTrackName() {
            return this.trackName;
        }

        public final String getArtistName() {
            return this.artistName;
        }

        public final String getCollectionName() {
            return this.collectionName;
        }

        public final String getArtworkUrl100() {
            return this.artworkUrl100;
        }
    }

    /* JADX INFO: renamed from: $r8$lambda$k4rZ7BWpnnwz-qGeJmjV_Xbyo7c, reason: not valid java name */
    public static Unit m1268$r8$lambda$k4rZ7BWpnnwzqGeJmjV_Xbyo7c(JsonBuilder jsonBuilder) {
        jsonBuilder.setIgnoreUnknownKeys(true);
        return Unit.INSTANCE;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:37:0x00ba  */
    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    public final Object fetchItunesTrack(String str, String str2, Continuation<? super List<ItunesTrack>> continuation) {
        AnonymousClass1 anonymousClass1;
        HttpUrl httpUrlBuild;
        String str3;
        HttpUrl.Builder builderNewBuilder;
        HttpUrl.Builder builderAddQueryParameter;
        HttpUrl.Builder builderAddQueryParameter2;
        if (continuation instanceof AnonymousClass1) {
            anonymousClass1 = (AnonymousClass1) continuation;
            int i = anonymousClass1.label;
            if ((i & Integer.MIN_VALUE) != 0) {
                anonymousClass1.label = i - Integer.MIN_VALUE;
            } else {
                anonymousClass1 = new AnonymousClass1(continuation);
            }
        } else {
            anonymousClass1 = new AnonymousClass1(continuation);
        }
        Object obj = anonymousClass1.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i2 = anonymousClass1.label;
        try {
            if (i2 == 0) {
                ResultKt.throwOnFailure(obj);
                if (StringsKt.isBlank(str) && StringsKt.isBlank(str2)) {
                    return null;
                }
                String str4 = str + " - " + str2;
                List<ItunesTrack> list = itunesCache.get(str4);
                if (list != null) {
                    return list;
                }
                HttpUrl httpUrl = HttpUrl.parse("https://itunes.apple.com/search");
                if (httpUrl == null || (builderNewBuilder = httpUrl.newBuilder()) == null) {
                    httpUrlBuild = null;
                } else {
                    HttpUrl.Builder builderAddQueryParameter3 = builderNewBuilder.addQueryParameter("term", str2 + " - " + str);
                    if (builderAddQueryParameter3 == null || (builderAddQueryParameter = builderAddQueryParameter3.addQueryParameter("entity", "song")) == null || (builderAddQueryParameter2 = builderAddQueryParameter.addQueryParameter("limit", "5")) == null) {
                        httpUrlBuild = null;
                    } else {
                        httpUrlBuild = builderAddQueryParameter2.build();
                    }
                }
                if (httpUrlBuild == null) {
                    return null;
                }
                Request requestBuild = new Request.Builder().url(httpUrlBuild).build();
                Call callNewCall = httpClient.newCall(requestBuild);
                anonymousClass1.L$0 = SpillingKt.nullOutSpilledVariable(str);
                anonymousClass1.L$1 = SpillingKt.nullOutSpilledVariable(str2);
                anonymousClass1.L$2 = str4;
                anonymousClass1.L$3 = SpillingKt.nullOutSpilledVariable(httpUrlBuild);
                anonymousClass1.L$4 = SpillingKt.nullOutSpilledVariable(requestBuild);
                anonymousClass1.label = 1;
                Object objAwait = await(callNewCall, anonymousClass1);
                if (objAwait == coroutine_suspended) {
                    return coroutine_suspended;
                }
                obj = objAwait;
                str3 = str4;
            } else {
                if (i2 != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                str3 = (String) anonymousClass1.L$2;
                ResultKt.throwOnFailure(obj);
            }
            okhttp3.Response response = (okhttp3.Response) obj;
            if (!response.isSuccessful()) {
                return null;
            }
            Json json = jsonParser;
            String strString = response.body().string();
            json.getSerializersModule();
            List<ItunesTrack> results = ((ItunesSearchResponse) json.decodeFromString(ItunesSearchResponse.INSTANCE.serializer(), strString)).getResults();
            itunesCache.put(str3, results);
            return results;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private final Object dbGetNowPlaying(long j, Continuation<? super NowPlayingInfoDTO> continuation) {
        final CancellableContinuationImpl cancellableContinuationImpl = new CancellableContinuationImpl(IntrinsicsKt.intercepted(continuation), 1);
        cancellableContinuationImpl.initCancellability();
        DatabaseHelper.getNowPlaying(j, new Consumer<NowPlayingInfoDTO>() { // from class: com.exteragram.messenger.nowplaying.NowPlayingController$dbGetNowPlaying$2$1
            @Override // java.util.function.Consumer
            public final void accept(NowPlayingInfoDTO nowPlayingInfoDTO) {
                cancellableContinuationImpl.resume(nowPlayingInfoDTO, null);
            }
        });
        Object result = cancellableContinuationImpl.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final Object dbUpdateNowPlaying(long j, NowPlayingInfoDTO nowPlayingInfoDTO, Continuation<? super Integer> continuation) {
        final CancellableContinuationImpl cancellableContinuationImpl = new CancellableContinuationImpl(IntrinsicsKt.intercepted(continuation), 1);
        cancellableContinuationImpl.initCancellability();
        DatabaseHelper.updateNowPlaying(j, nowPlayingInfoDTO, new Consumer<Integer>() { // from class: com.exteragram.messenger.nowplaying.NowPlayingController$dbUpdateNowPlaying$2$1
            @Override // java.util.function.Consumer
            public final void accept(Integer num) {
                cancellableContinuationImpl.resume(num, null);
            }
        });
        Object result = cancellableContinuationImpl.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result;
    }

    public final Object await(final Call call, Continuation<? super okhttp3.Response> continuation) {
        final CancellableContinuationImpl cancellableContinuationImpl = new CancellableContinuationImpl(IntrinsicsKt.intercepted(continuation), 1);
        cancellableContinuationImpl.initCancellability();
        cancellableContinuationImpl.invokeOnCancellation(new Function1<Throwable, Unit>() { // from class: com.exteragram.messenger.nowplaying.NowPlayingController$await$2$1
            @Override // kotlin.jvm.functions.Function1
            public /* bridge */ /* synthetic */ Unit invoke(Throwable th) {
                invoke2(th);
                return Unit.INSTANCE;
            }

            /* JADX INFO: renamed from: invoke, reason: avoid collision after fix types in other method */
            public final void invoke2(Throwable th) {
                call.cancel();
            }
        });
        call.enqueue(new okhttp3.Callback() { // from class: com.exteragram.messenger.nowplaying.NowPlayingController$await$2$2
            @Override // okhttp3.Callback
            public void onResponse(Call call2, okhttp3.Response response) {
                cancellableContinuationImpl.resume(response, null);
            }

            @Override // okhttp3.Callback
            public void onFailure(Call call2, IOException e) {
                if (cancellableContinuationImpl.isCancelled()) {
                    return;
                }
                cancellableContinuationImpl.resumeWith(ResultKt.createFailure(e));
            }
        });
        Object result = cancellableContinuationImpl.getResult();
        if (result == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
            DebugProbesKt.probeCoroutineSuspended(continuation);
        }
        return result;
    }
}
