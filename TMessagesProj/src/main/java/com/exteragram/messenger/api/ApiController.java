package com.exteragram.messenger.api;

import android.content.SharedPreferences;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.api.db.DatabaseHelper;
import com.exteragram.messenger.api.dto.BoostySubscriberDTO;
import com.exteragram.messenger.api.dto.ProfileDTO;
import com.exteragram.messenger.api.network.ApiClient;
import com.exteragram.messenger.api.network.ApiService;
import com.exteragram.messenger.api.worker.SyncWorker;
import j$.time.Duration;
import j$.time.Instant;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SpillingKt;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.JvmOverloads;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.LongCompanionObject;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.BuildersKt__Builders_commonKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.sync.Mutex;
import kotlinx.coroutines.sync.MutexKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.ui.Components.ForegroundDetector;
import retrofit2.Response;

@Metadata(d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\b\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0012\u001a\u00020\u0013H\u0007J\u001c\u0010\u0014\u001a\u00020\u00132\b\b\u0002\u0010\u0015\u001a\u00020\u00162\b\b\u0002\u0010\u0017\u001a\u00020\u0018H\u0007J\"\u0010\u0019\u001a\u00020\u00182\b\b\u0002\u0010\u0015\u001a\u00020\u00162\b\b\u0002\u0010\u0017\u001a\u00020\u0018H\u0086@¢\u0006\u0002\u0010\u001aJ\u0018\u0010\u001b\u001a\u00020\u000f2\u0006\u0010\u001c\u001a\u00020\u000f2\u0006\u0010\u001d\u001a\u00020\u000fH\u0002J\u0012\u0010\u001e\u001a\u00020\u00132\b\b\u0002\u0010\u0015\u001a\u00020\u0016H\u0007J\b\u0010\u001f\u001a\u00020\u0013H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000bX\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000fX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006 "}, d2 = {"Lcom/exteragram/messenger/api/ApiController;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "scope", "Lkotlinx/coroutines/CoroutineScope;", "started", "Ljava/util/concurrent/atomic/AtomicBoolean;", "syncMutex", "Lkotlinx/coroutines/sync/Mutex;", "PROFILES_SYNC_TIME_KEY", _UrlKt.FRAGMENT_ENCODE_SET, "BOOSTY_SYNC_TIME_KEY", "SYNC_FAILURE_TIME_KEY", "PROFILES_SYNC_INTERVAL", _UrlKt.FRAGMENT_ENCODE_SET, "BOOSTY_SYNC_INTERVAL", "FAILURE_BACKOFF", "init", _UrlKt.FRAGMENT_ENCODE_SET, "sync", "prefs", "Landroid/content/SharedPreferences;", "force", _UrlKt.FRAGMENT_ENCODE_SET, "performSync", "(Landroid/content/SharedPreferences;ZLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "elapsedSince", "now", "timestamp", "resetSyncState", "scheduleWorker", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nApiController.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ApiController.kt\ncom/exteragram/messenger/api/ApiController\n+ 2 Mutex.kt\nkotlinx/coroutines/sync/MutexKt\n+ 3 SharedPreferences.kt\nandroidx/core/content/SharedPreferencesKt\n+ 4 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 5 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 6 PeriodicWorkRequest.kt\nandroidx/work/PeriodicWorkRequestKt\n*L\n1#1,223:1\n116#2,8:224\n125#2,2:295\n41#3,12:232\n41#3,12:244\n41#3,6:270\n47#3,6:277\n41#3,12:283\n41#3,12:297\n3347#4,10:256\n1586#4:266\n1661#4,3:267\n1#5:276\n364#6:309\n*S KotlinDebug\n*F\n+ 1 ApiController.kt\ncom/exteragram/messenger/api/ApiController\n*L\n83#1:224,8\n83#1:295,2\n105#1:232,12\n135#1:244,12\n152#1:270,6\n152#1:277,6\n174#1:283,12\n192#1:297,12\n143#1:256,10\n145#1:266\n145#1:267,3\n209#1:309\n*E\n"})
public final class ApiController {
    private static final String BOOSTY_SYNC_TIME_KEY = "lastBoostySyncTime";
    private static final String PROFILES_SYNC_TIME_KEY = "lastProfilesSyncTime";
    private static final String SYNC_FAILURE_TIME_KEY = "lastSyncFailureTime";
    public static final ApiController INSTANCE = new ApiController();
    private static final CoroutineScope scope = CoroutineScopeKt.CoroutineScope(Dispatchers.getIO());
    private static final AtomicBoolean started = new AtomicBoolean(false);
    private static final Mutex syncMutex = MutexKt.Mutex$default(false, 1, null);
    private static final long PROFILES_SYNC_INTERVAL = Duration.ofMinutes(10).toMillis();
    private static final long BOOSTY_SYNC_INTERVAL = Duration.ofMinutes(40).toMillis();
    private static final long FAILURE_BACKOFF = Duration.ofMinutes(5).toMillis();

    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.api.ApiController", f = "ApiController.kt", i = {0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6}, l = {229, 102, 104, 129, 131, 145, 148}, m = "performSync", n = {"prefs", "$this$withLock_u24default$iv", "force", "$i$f$withLock", "prefs", "$this$withLock_u24default$iv", "success", "force", "$i$f$withLock", "$i$a$-withLock$default-ApiController$performSync$2", "now", "boostyDue", "profilesDue", "attempted", "prefs", "$this$withLock_u24default$iv", "success", "boostyResponse", "force", "$i$f$withLock", "$i$a$-withLock$default-ApiController$performSync$2", "now", "boostyDue", "profilesDue", "attempted", "prefs", "$this$withLock_u24default$iv", "success", "lastSyncTimestamp", "profilesEtag", "apiService", "force", "$i$f$withLock", "$i$a$-withLock$default-ApiController$performSync$2", "now", "boostyDue", "profilesDue", "attempted", "prefs", "$this$withLock_u24default$iv", "success", "lastSyncTimestamp", "profilesEtag", "apiService", "force", "$i$f$withLock", "$i$a$-withLock$default-ApiController$performSync$2", "now", "boostyDue", "profilesDue", "attempted", "prefs", "$this$withLock_u24default$iv", "success", "lastSyncTimestamp", "profilesEtag", "apiService", "response", "profiles", "profilesToDelete", "profilesToInsert", "force", "$i$f$withLock", "$i$a$-withLock$default-ApiController$performSync$2", "now", "boostyDue", "profilesDue", "attempted", "stored", "prefs", "$this$withLock_u24default$iv", "success", "lastSyncTimestamp", "profilesEtag", "apiService", "response", "profiles", "profilesToDelete", "profilesToInsert", "force", "$i$f$withLock", "$i$a$-withLock$default-ApiController$performSync$2", "now", "boostyDue", "profilesDue", "attempted", "stored"}, s = {"L$0", "L$1", "Z$0", "I$0", "L$0", "L$1", "L$2", "Z$0", "I$0", "I$1", "J$0", "I$2", "I$3", "I$4", "L$0", "L$1", "L$2", "L$3", "Z$0", "I$0", "I$1", "J$0", "I$2", "I$3", "I$4", "L$0", "L$1", "L$2", "L$3", "L$4", "L$5", "Z$0", "I$0", "I$1", "J$0", "I$2", "I$3", "I$4", "L$0", "L$1", "L$2", "L$3", "L$4", "L$5", "Z$0", "I$0", "I$1", "J$0", "I$2", "I$3", "I$4", "L$0", "L$1", "L$2", "L$3", "L$4", "L$5", "L$6", "L$7", "L$8", "L$9", "Z$0", "I$0", "I$1", "J$0", "I$2", "I$3", "I$4", "I$5", "L$0", "L$1", "L$2", "L$3", "L$4", "L$5", "L$6", "L$7", "L$8", "L$9", "Z$0", "I$0", "I$1", "J$0", "I$2", "I$3", "I$4", "I$5"}, v = 1)
    public static final class C01341 extends ContinuationImpl {
        int I$0;
        int I$1;
        int I$2;
        int I$3;
        int I$4;
        int I$5;
        long J$0;
        Object L$0;
        Object L$1;
        Object L$2;
        Object L$3;
        Object L$4;
        Object L$5;
        Object L$6;
        Object L$7;
        Object L$8;
        Object L$9;
        boolean Z$0;
        int label;
        void sync$default(SharedPreferences sharedPreferences, boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            sharedPreferences = ExteraConfig.getPreferences();
        }
        if ((i & 2) != 0) {
            z = false;
        }
        sync(sharedPreferences, z);
    }

    @JvmStatic
    @JvmOverloads
    public static final void sync(SharedPreferences prefs, boolean force) {
        if (!force) {
            long jCurrentTimeMillis = System.currentTimeMillis();
            ApiController apiController = INSTANCE;
            if (apiController.elapsedSince(jCurrentTimeMillis, prefs.getLong(SYNC_FAILURE_TIME_KEY, 0L)) < FAILURE_BACKOFF) {
                return;
            }
            boolean z = apiController.elapsedSince(jCurrentTimeMillis, prefs.getLong(PROFILES_SYNC_TIME_KEY, 0L)) >= PROFILES_SYNC_INTERVAL;
            boolean z2 = apiController.elapsedSince(jCurrentTimeMillis, prefs.getLong(BOOSTY_SYNC_TIME_KEY, 0L)) >= BOOSTY_SYNC_INTERVAL;
            if (!z && !z2) {
                return;
            }
        }
        BuildersKt__Builders_commonKt.launch$default(scope, null, null, new C01351(prefs, force, null), 3, null);
    }

    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.api.ApiController$sync$1", f = "ApiController.kt", i = {}, l = {79}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    public static final class C01351 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ boolean $force;
        final /* synthetic */ SharedPreferences $prefs;
        int label;

        public C01351(SharedPreferences sharedPreferences, boolean z, Continuation<? super C01351> continuation) {
            super(2, continuation);
            this.$prefs = sharedPreferences;
            this.$force = z;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return new C01351(this.$prefs, this.$force, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C01351) create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            if (i == 0) {
                ResultKt.throwOnFailure(obj);
                ApiController apiController = ApiController.INSTANCE;
                SharedPreferences sharedPreferences = this.$prefs;
                boolean z = this.$force;
                this.label = 1;
                if (apiController.performSync(sharedPreferences, z, this) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i != 1) {
                    Segment$$ExternalSyntheticBUOutline1.m("call to 'resume' before 'invoke' with coroutine");
                    return null;
                }
                ResultKt.throwOnFailure(obj);
            }
            return Unit.INSTANCE;
        }
    }

    public static /* synthetic */ Object performSync$default(ApiController apiController, SharedPreferences sharedPreferences, boolean z, Continuation continuation, int i, Object obj) {
        if ((i & 1) != 0) {
            sharedPreferences = ExteraConfig.getPreferences();
        }
        if ((i & 2) != 0) {
            z = false;
        }
        return apiController.performSync(sharedPreferences, z, continuation);
    }

    public final Object performSync(SharedPreferences sharedPreferences, boolean z, Continuation<? super Boolean> continuation) throws Throwable {
        C01341 c01341;
        String str;
        SharedPreferences sharedPreferences2;
        SharedPreferences sharedPreferences3;
        String str2;
        Object obj;
        Object obj2;
        ?? r18;
        Object obj3;
        ?? r8;
        int i;
        Ref.BooleanRef booleanRef;
        Ref.BooleanRef booleanRef2;
        Mutex mutex;
        Mutex mutex2;
        SharedPreferences sharedPreferences4;
        SharedPreferences sharedPreferences5;
        ?? r1;
        ?? r15;
        Mutex mutex3;
        boolean z2;
        int i2;
        int i3;
        int i4;
        int i5;
        String str3;
        ?? r19;
        ?? r12;
        int i6;
        ?? r9;
        int i7;
        int i8;
        int i9;
        ?? r3;
        int i10;
        String string;
        String str4;
        String string2;
        ApiService apiService;
        String str5;
        Object updates;
        int i11;
        Object obj4;
        ApiService apiService2;
        int i12;
        int i13;
        ?? r5;
        Object allProfiles;
        Object obj5;
        ?? r6;
        String str6;
        ?? r110;
        SharedPreferences sharedPreferences6;
        ?? r11;
        ?? r10;
        Object obj6;
        String str7;
        ?? r111;
        ?? r13;
        Object obj7;
        ?? r14;
        Response response;
        String str8;
        ?? r16;
        String str9;
        int i14;
        boolean z3;
        ?? r17;
        int i15;
        List list;
        String str10;
        String str11;
        ?? r2;
        int i16;
        List<ProfileDTO> list2;
        List list3;
        ?? r112;
        ?? r7;
        int i17;
        int i18;
        int i19;
        String str12;
        int i20;
        List list4;
        Object obj8;
        ApiService apiService3;
        int i21;
        int i22;
        Object obj9;
        ?? r24;
        ?? r113;
        ?? r114;
        List list5;
        ApiService apiService4;
        int i23;
        List<ProfileDTO> list6;
        Response response2;
        Object obj10;
        String str13;
        String str14;
        ?? r115;
        ?? r116;
        Object obj11;
        ?? r20;
        ?? r25;
        ?? r117;
        ?? r118;
        ?? r119;
        Object obj12;
        ?? r26;
        String str15;
        ?? r120;
        Object obj13;
        ?? r21;
        ?? r22;
        ?? r27;
        ?? r121;
        Object objInsertProfiles;
        Object obj14;
        Response response3;
        ?? r122;
        ?? r123;
        ?? r4;
        String str16;
        String str17;
        ?? r124;
        Object obj15;
        int i24;
        ?? r125;
        boolean z4;
        ?? Edit;
        ?? Edit2;
        String str18;
        String str19;
        ?? r126;
        Object obj16;
        boolean z5;
        Mutex mutex4;
        int i25;
        SharedPreferences sharedPreferences7;
        long jCurrentTimeMillis;
        int i26;
        int i27;
        Ref.BooleanRef booleanRef3;
        SharedPreferences sharedPreferences8;
        int i28;
        Object boostySubscribers;
        Mutex mutex5;
        long j;
        Ref.BooleanRef booleanRef4;
        SharedPreferences sharedPreferences9;
        Response response4;
        SharedPreferences sharedPreferences10;
        Object objInsertBoostySubscribers;
        String str20;
        ?? r127;
        long j2;
        Ref.BooleanRef booleanRef5;
        Mutex mutex6;
        if (continuation instanceof C01341) {
            c01341 = (C01341) continuation;
            int i29 = c01341.label;
            if ((i29 & Integer.MIN_VALUE) != 0) {
                c01341.label = i29 - Integer.MIN_VALUE;
            } else {
                c01341 = new C01341(continuation);
            }
        } else {
            c01341 = new C01341(continuation);
        }
        Object obj17 = c01341.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i30 = c01341.label;
        String str21 = "profilesEtag";
        ?? r23 = BOOSTY_SYNC_TIME_KEY;
        String str22 = PROFILES_SYNC_TIME_KEY;
        ?? r28 = SYNC_FAILURE_TIME_KEY;
        Object obj18 = null;
         = 0;
        ?? r128 = 0;
        ?? r129 = 0;
        try {
            try {
                try {
                    try {
                        try {
                            try {
                                switch (i30) {
                                    case 0:
                                        ResultKt.throwOnFailure(obj17);
                                        Mutex mutex7 = syncMutex;
                                        SharedPreferences sharedPreferences11 = sharedPreferences;
                                        c01341.L$0 = sharedPreferences11;
                                        c01341.L$1 = mutex7;
                                        z5 = z;
                                        c01341.Z$0 = z5;
                                        c01341.I$0 = 0;
                                        c01341.label = 1;
                                        if (mutex7.lock(null, c01341) != coroutine_suspended) {
                                            mutex4 = mutex7;
                                            i25 = 0;
                                            sharedPreferences7 = sharedPreferences11;
                                            jCurrentTimeMillis = System.currentTimeMillis();
                                            if (!z5 || INSTANCE.elapsedSince(jCurrentTimeMillis, sharedPreferences7.getLong(SYNC_FAILURE_TIME_KEY, 0L)) >= FAILURE_BACKOFF) {
                                                if (z5 == 0 || INSTANCE.elapsedSince(jCurrentTimeMillis, sharedPreferences7.getLong(BOOSTY_SYNC_TIME_KEY, 0L)) >= BOOSTY_SYNC_INTERVAL) {
                                                    i26 = 1;
                                                } else {
                                                    i26 = 0;
                                                }
                                                if (z5 == 0 || INSTANCE.elapsedSince(jCurrentTimeMillis, sharedPreferences7.getLong(PROFILES_SYNC_TIME_KEY, 0L)) >= PROFILES_SYNC_INTERVAL) {
                                                    i27 = 1;
                                                } else {
                                                    i27 = 0;
                                                }
                                                if (i26 == 0 || i27 != 0) {
                                                    booleanRef3 = new Ref.BooleanRef();
                                                    booleanRef3.element = true;
                                                    if (i26 != 0) {
                                                        z2 = z5;
                                                        str21 = "profilesEtag";
                                                        str = "lastSyncTimestamp";
                                                        str3 = PROFILES_SYNC_TIME_KEY;
                                                        r19 = SYNC_FAILURE_TIME_KEY;
                                                        i8 = i26;
                                                        i10 = i27;
                                                        r9 = booleanRef3;
                                                        i9 = 0;
                                                        i7 = 0;
                                                        if (i10 != 0) {
                                                            i6 = i25;
                                                            r3 = sharedPreferences7;
                                                            obj18 = mutex4;
                                                            r12 = jCurrentTimeMillis;
                                                            string = r3.getString(str, null);
                                                            str4 = str21;
                                                            string2 = r3.getString(str4, null);
                                                            apiService = ApiClient.INSTANCE.getApiService();
                                                            if (string == null) {
                                                                c01341.L$0 = r3;
                                                                c01341.L$1 = obj18;
                                                                c01341.L$2 = r9;
                                                                c01341.L$3 = string;
                                                                str5 = str4;
                                                                c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                                c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                                c01341.Z$0 = z2;
                                                                c01341.I$0 = i6;
                                                                c01341.I$1 = i7;
                                                                c01341.J$0 = r12;
                                                                c01341.I$2 = i8;
                                                                c01341.I$3 = i10;
                                                                c01341.I$4 = 1;
                                                                c01341.label = 4;
                                                                allProfiles = apiService.getAllProfiles(string2, c01341);
                                                                if (allProfiles != coroutine_suspended) {
                                                                    i11 = i10;
                                                                    obj5 = allProfiles;
                                                                    apiService2 = apiService;
                                                                    i12 = i7;
                                                                    i13 = i8;
                                                                    r6 = r3;
                                                                    i30 = 1;
                                                                    r14 = r9;
                                                                    obj7 = obj18;
                                                                    r13 = r12;
                                                                    r111 = r19;
                                                                    str7 = str3;
                                                                    response = (Response) obj5;
                                                                    r23 = r6;
                                                                    r16 = r14;
                                                                    obj18 = obj7;
                                                                    r12 = r13;
                                                                    r19 = r111;
                                                                    str8 = str7;
                                                                    str9 = string2;
                                                                    int i31 = i12;
                                                                    i14 = i6;
                                                                    ApiService apiService5 = apiService2;
                                                                    z3 = z2;
                                                                    r17 = r16;
                                                                    r28 = i13;
                                                                    int i32 = i11;
                                                                    if (response.code() == 304) {
                                                                        ?? Edit3 = r23.edit();
                                                                        Edit3.putString(str, Instant.now().toString());
                                                                        Edit3.putLong(str8, r12);
                                                                        Edit3.apply();
                                                                    } else {
                                                                        String str23 = str8;
                                                                        String str24 = str;
                                                                        if (response.isSuccessful()) {
                                                                        }
                                                                        r17.element = false;
                                                                    }
                                                                    r3 = r23;
                                                                    r9 = r17;
                                                                    i9 = i30;
                                                                }
                                                            } else {
                                                                str5 = str4;
                                                                c01341.L$0 = r3;
                                                                c01341.L$1 = obj18;
                                                                c01341.L$2 = r9;
                                                                c01341.L$3 = string;
                                                                c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                                c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                                c01341.Z$0 = z2;
                                                                c01341.I$0 = i6;
                                                                c01341.I$1 = i7;
                                                                c01341.J$0 = r12;
                                                                c01341.I$2 = i8;
                                                                c01341.I$3 = i10;
                                                                c01341.I$4 = 1;
                                                                c01341.label = 5;
                                                                updates = apiService.getUpdates(string, c01341);
                                                                if (updates != coroutine_suspended) {
                                                                    i11 = i10;
                                                                    obj4 = updates;
                                                                    apiService2 = apiService;
                                                                    i12 = i7;
                                                                    i13 = i8;
                                                                    r5 = r3;
                                                                    i30 = 1;
                                                                    r20 = r9;
                                                                    obj11 = obj18;
                                                                    r116 = r12;
                                                                    r115 = r19;
                                                                    str14 = str3;
                                                                    response = (Response) obj4;
                                                                    r23 = r5;
                                                                    r16 = r20;
                                                                    obj18 = obj11;
                                                                    r12 = r116;
                                                                    r19 = r115;
                                                                    str8 = str14;
                                                                    str9 = string2;
                                                                    int i33 = i12;
                                                                    i14 = i6;
                                                                    ApiService apiService6 = apiService2;
                                                                    z3 = z2;
                                                                    r17 = r16;
                                                                    r28 = i13;
                                                                    int i34 = i11;
                                                                    if (response.code() == 304) {
                                                                        ?? Edit4 = r23.edit();
                                                                        Edit4.putString(str, Instant.now().toString());
                                                                        Edit4.putLong(str8, r12);
                                                                        Edit4.apply();
                                                                    } else {
                                                                        String str25 = str8;
                                                                        String str26 = str;
                                                                        if (response.isSuccessful()) {
                                                                        }
                                                                        r17.element = false;
                                                                    }
                                                                    r3 = r23;
                                                                    r9 = r17;
                                                                    i9 = i30;
                                                                }
                                                            }
                                                        }
                                                        if (i9 != 0) {
                                                            Edit = r3.edit();
                                                            if (r9.element) {
                                                                Edit.remove(r19);
                                                            } else {
                                                                Edit.putLong(r19, r12);
                                                            }
                                                            Edit.apply();
                                                        }
                                                        z4 = r9.element;
                                                        r125 = obj18;
                                                        break;
                                                    } else {
                                                        try {
                                                            try {
                                                                ApiService apiService7 = ApiClient.INSTANCE.getApiService();
                                                                c01341.L$0 = sharedPreferences7;
                                                                c01341.L$1 = mutex4;
                                                                c01341.L$2 = booleanRef3;
                                                                z2 = z5;
                                                                try {
                                                                    c01341.Z$0 = z2;
                                                                    sharedPreferences8 = sharedPreferences7;
                                                                    i28 = i25;
                                                                    try {
                                                                        c01341.I$0 = i28;
                                                                        i25 = i28;
                                                                        try {
                                                                            c01341.I$1 = 0;
                                                                            c01341.J$0 = jCurrentTimeMillis;
                                                                            c01341.I$2 = i26;
                                                                            c01341.I$3 = i27;
                                                                            c01341.I$4 = 1;
                                                                            c01341.label = 2;
                                                                            boostySubscribers = apiService7.getBoostySubscribers(c01341);
                                                                            if (boostySubscribers != coroutine_suspended) {
                                                                                int i35 = i27;
                                                                                i4 = i26;
                                                                                i5 = i35;
                                                                                mutex5 = mutex4;
                                                                                j = jCurrentTimeMillis;
                                                                                booleanRef4 = booleanRef3;
                                                                                i2 = i25;
                                                                                sharedPreferences9 = sharedPreferences8;
                                                                                i30 = 1;
                                                                                i3 = 0;
                                                                                try {
                                                                                    try {
                                                                                        response4 = (Response) boostySubscribers;
                                                                                        if (!response4.isSuccessful() && response4.body() != null) {
                                                                                            DatabaseHelper databaseHelper = DatabaseHelper.INSTANCE;
                                                                                            str = "lastSyncTimestamp";
                                                                                            try {
                                                                                                List<BoostySubscriberDTO> list7 = (List) response4.body();
                                                                                                c01341.L$0 = sharedPreferences9;
                                                                                                c01341.L$1 = mutex5;
                                                                                                c01341.L$2 = booleanRef4;
                                                                                                sharedPreferences10 = sharedPreferences9;
                                                                                                c01341.L$3 = SpillingKt.nullOutSpilledVariable(response4);
                                                                                                c01341.Z$0 = z2;
                                                                                                c01341.I$0 = i2;
                                                                                                c01341.I$1 = i3;
                                                                                                c01341.J$0 = j;
                                                                                                c01341.I$2 = i4;
                                                                                                c01341.I$3 = i5;
                                                                                                c01341.I$4 = i30;
                                                                                                c01341.label = 3;
                                                                                                objInsertBoostySubscribers = databaseHelper.insertBoostySubscribers(list7, c01341);
                                                                                                mutex6 = mutex5;
                                                                                                booleanRef5 = booleanRef4;
                                                                                                j2 = j;
                                                                                                r127 = r28;
                                                                                                str20 = str22;
                                                                                                if (objInsertBoostySubscribers != coroutine_suspended) {
                                                                                                    if (((Boolean) objInsertBoostySubscribers).booleanValue()) {
                                                                                                        SharedPreferences.Editor editorEdit = sharedPreferences10.edit();
                                                                                                        editorEdit.putLong(BOOSTY_SYNC_TIME_KEY, j2);
                                                                                                        editorEdit.apply();
                                                                                                        obj6 = mutex6;
                                                                                                        r10 = booleanRef5;
                                                                                                        r11 = j2;
                                                                                                        sharedPreferences6 = sharedPreferences10;
                                                                                                        r110 = r127;
                                                                                                        str6 = str20;
                                                                                                    } else {
                                                                                                        booleanRef5.element = false;
                                                                                                        obj6 = mutex6;
                                                                                                        r10 = booleanRef5;
                                                                                                        r11 = j2;
                                                                                                        sharedPreferences6 = sharedPreferences10;
                                                                                                        r110 = r127;
                                                                                                        str6 = str20;
                                                                                                    }
                                                                                                    i9 = i30;
                                                                                                    i10 = i5;
                                                                                                    i8 = i4;
                                                                                                    i7 = i3;
                                                                                                    i6 = i2;
                                                                                                    r3 = sharedPreferences6;
                                                                                                    r12 = r11;
                                                                                                    obj18 = obj6;
                                                                                                    r9 = r10;
                                                                                                    r19 = r110;
                                                                                                    str3 = str6;
                                                                                                    if (i10 != 0) {
                                                                                                        i6 = i25;
                                                                                                        r3 = sharedPreferences7;
                                                                                                        obj18 = mutex4;
                                                                                                        r12 = jCurrentTimeMillis;
                                                                                                        string = r3.getString(str, null);
                                                                                                        str4 = str21;
                                                                                                        string2 = r3.getString(str4, null);
                                                                                                        apiService = ApiClient.INSTANCE.getApiService();
                                                                                                        if (string == null) {
                                                                                                            c01341.L$0 = r3;
                                                                                                            c01341.L$1 = obj18;
                                                                                                            c01341.L$2 = r9;
                                                                                                            c01341.L$3 = string;
                                                                                                            str5 = str4;
                                                                                                            c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                                                                            c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                                                                            c01341.Z$0 = z2;
                                                                                                            c01341.I$0 = i6;
                                                                                                            c01341.I$1 = i7;
                                                                                                            c01341.J$0 = r12;
                                                                                                            c01341.I$2 = i8;
                                                                                                            c01341.I$3 = i10;
                                                                                                            c01341.I$4 = 1;
                                                                                                            c01341.label = 4;
                                                                                                            allProfiles = apiService.getAllProfiles(string2, c01341);
                                                                                                            if (allProfiles != coroutine_suspended) {
                                                                                                                i11 = i10;
                                                                                                                obj5 = allProfiles;
                                                                                                                apiService2 = apiService;
                                                                                                                i12 = i7;
                                                                                                                i13 = i8;
                                                                                                                r6 = r3;
                                                                                                                i30 = 1;
                                                                                                                r14 = r9;
                                                                                                                obj7 = obj18;
                                                                                                                r13 = r12;
                                                                                                                r111 = r19;
                                                                                                                str7 = str3;
                                                                                                                response = (Response) obj5;
                                                                                                                r23 = r6;
                                                                                                                r16 = r14;
                                                                                                                obj18 = obj7;
                                                                                                                r12 = r13;
                                                                                                                r19 = r111;
                                                                                                                str8 = str7;
                                                                                                                str9 = string2;
                                                                                                                int i36 = i12;
                                                                                                                i14 = i6;
                                                                                                                ApiService apiService8 = apiService2;
                                                                                                                z3 = z2;
                                                                                                                r17 = r16;
                                                                                                                r28 = i13;
                                                                                                                int i37 = i11;
                                                                                                                if (response.code() == 304) {
                                                                                                                    ?? Edit5 = r23.edit();
                                                                                                                    Edit5.putString(str, Instant.now().toString());
                                                                                                                    Edit5.putLong(str8, r12);
                                                                                                                    Edit5.apply();
                                                                                                                } else {
                                                                                                                    String str27 = str8;
                                                                                                                    String str28 = str;
                                                                                                                    if (response.isSuccessful()) {
                                                                                                                    }
                                                                                                                    r17.element = false;
                                                                                                                }
                                                                                                                r3 = r23;
                                                                                                                r9 = r17;
                                                                                                                i9 = i30;
                                                                                                            }
                                                                                                        } else {
                                                                                                            str5 = str4;
                                                                                                            c01341.L$0 = r3;
                                                                                                            c01341.L$1 = obj18;
                                                                                                            c01341.L$2 = r9;
                                                                                                            c01341.L$3 = string;
                                                                                                            c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                                                                            c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                                                                            c01341.Z$0 = z2;
                                                                                                            c01341.I$0 = i6;
                                                                                                            c01341.I$1 = i7;
                                                                                                            c01341.J$0 = r12;
                                                                                                            c01341.I$2 = i8;
                                                                                                            c01341.I$3 = i10;
                                                                                                            c01341.I$4 = 1;
                                                                                                            c01341.label = 5;
                                                                                                            updates = apiService.getUpdates(string, c01341);
                                                                                                            if (updates != coroutine_suspended) {
                                                                                                                i11 = i10;
                                                                                                                obj4 = updates;
                                                                                                                apiService2 = apiService;
                                                                                                                i12 = i7;
                                                                                                                i13 = i8;
                                                                                                                r5 = r3;
                                                                                                                i30 = 1;
                                                                                                                r20 = r9;
                                                                                                                obj11 = obj18;
                                                                                                                r116 = r12;
                                                                                                                r115 = r19;
                                                                                                                str14 = str3;
                                                                                                                response = (Response) obj4;
                                                                                                                r23 = r5;
                                                                                                                r16 = r20;
                                                                                                                obj18 = obj11;
                                                                                                                r12 = r116;
                                                                                                                r19 = r115;
                                                                                                                str8 = str14;
                                                                                                                str9 = string2;
                                                                                                                int i38 = i12;
                                                                                                                i14 = i6;
                                                                                                                ApiService apiService9 = apiService2;
                                                                                                                z3 = z2;
                                                                                                                r17 = r16;
                                                                                                                r28 = i13;
                                                                                                                int i39 = i11;
                                                                                                                if (response.code() == 304) {
                                                                                                                    ?? Edit6 = r23.edit();
                                                                                                                    Edit6.putString(str, Instant.now().toString());
                                                                                                                    Edit6.putLong(str8, r12);
                                                                                                                    Edit6.apply();
                                                                                                                } else {
                                                                                                                    String str29 = str8;
                                                                                                                    String str210 = str;
                                                                                                                    if (response.isSuccessful()) {
                                                                                                                    }
                                                                                                                    r17.element = false;
                                                                                                                }
                                                                                                                r3 = r23;
                                                                                                                r9 = r17;
                                                                                                                i9 = i30;
                                                                                                            }
                                                                                                        }
                                                                                                    }
                                                                                                    if (i9 != 0) {
                                                                                                        Edit = r3.edit();
                                                                                                        if (r9.element) {
                                                                                                            Edit.remove(r19);
                                                                                                        } else {
                                                                                                            Edit.putLong(r19, r12);
                                                                                                        }
                                                                                                        Edit.apply();
                                                                                                    }
                                                                                                    z4 = r9.element;
                                                                                                    r125 = obj18;
                                                                                                    break;
                                                                                                }
                                                                                            } catch (Throwable th) {
                                                                                                th = th;
                                                                                                sharedPreferences3 = sharedPreferences9;
                                                                                                obj2 = mutex5;
                                                                                                r8 = booleanRef4;
                                                                                                r129 = j;
                                                                                                r18 = r28;
                                                                                                str2 = str22;
                                                                                                try {
                                                                                                    FileLog.e(th);
                                                                                                    r8.element = false;
                                                                                                    obj6 = obj2;
                                                                                                    r10 = r8;
                                                                                                    r11 = r129;
                                                                                                    sharedPreferences6 = sharedPreferences3;
                                                                                                    r110 = r18;
                                                                                                    str6 = str2;
                                                                                                } catch (Throwable th2) {
                                                                                                    th = th2;
                                                                                                    r128 = obj2;
                                                                                                    r128.unlock(null);
                                                                                                    throw th;
                                                                                                }
                                                                                                break;
                                                                                            }
                                                                                        } else {
                                                                                            sharedPreferences6 = sharedPreferences9;
                                                                                            str = "lastSyncTimestamp";
                                                                                            booleanRef4.element = false;
                                                                                            obj6 = mutex5;
                                                                                            r10 = booleanRef4;
                                                                                            r11 = j;
                                                                                            r110 = r28;
                                                                                            str6 = str22;
                                                                                            i9 = i30;
                                                                                            i10 = i5;
                                                                                            i8 = i4;
                                                                                            i7 = i3;
                                                                                            i6 = i2;
                                                                                            r3 = sharedPreferences6;
                                                                                            r12 = r11;
                                                                                            obj18 = obj6;
                                                                                            r9 = r10;
                                                                                            r19 = r110;
                                                                                            str3 = str6;
                                                                                            if (i10 != 0) {
                                                                                                i6 = i25;
                                                                                                r3 = sharedPreferences7;
                                                                                                obj18 = mutex4;
                                                                                                r12 = jCurrentTimeMillis;
                                                                                                try {
                                                                                                    string = r3.getString(str, null);
                                                                                                    str4 = str21;
                                                                                                    string2 = r3.getString(str4, null);
                                                                                                    apiService = ApiClient.INSTANCE.getApiService();
                                                                                                    if (string == null) {
                                                                                                        c01341.L$0 = r3;
                                                                                                        c01341.L$1 = obj18;
                                                                                                        c01341.L$2 = r9;
                                                                                                        c01341.L$3 = string;
                                                                                                        str5 = str4;
                                                                                                        c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                                                                        c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                                                                        c01341.Z$0 = z2;
                                                                                                        c01341.I$0 = i6;
                                                                                                        c01341.I$1 = i7;
                                                                                                        c01341.J$0 = r12;
                                                                                                        c01341.I$2 = i8;
                                                                                                        c01341.I$3 = i10;
                                                                                                        c01341.I$4 = 1;
                                                                                                        c01341.label = 4;
                                                                                                        allProfiles = apiService.getAllProfiles(string2, c01341);
                                                                                                        if (allProfiles != coroutine_suspended) {
                                                                                                            i11 = i10;
                                                                                                            obj5 = allProfiles;
                                                                                                            apiService2 = apiService;
                                                                                                            i12 = i7;
                                                                                                            i13 = i8;
                                                                                                            r6 = r3;
                                                                                                            i30 = 1;
                                                                                                            r14 = r9;
                                                                                                            obj7 = obj18;
                                                                                                            r13 = r12;
                                                                                                            r111 = r19;
                                                                                                            str7 = str3;
                                                                                                            response = (Response) obj5;
                                                                                                            r23 = r6;
                                                                                                            r16 = r14;
                                                                                                            obj18 = obj7;
                                                                                                            r12 = r13;
                                                                                                            r19 = r111;
                                                                                                            str8 = str7;
                                                                                                            str9 = string2;
                                                                                                            int i310 = i12;
                                                                                                            i14 = i6;
                                                                                                            ApiService apiService10 = apiService2;
                                                                                                            z3 = z2;
                                                                                                            r17 = r16;
                                                                                                            r28 = i13;
                                                                                                            int i311 = i11;
                                                                                                            try {
                                                                                                                if (response.code() == 304) {
                                                                                                                    try {
                                                                                                                        ?? Edit7 = r23.edit();
                                                                                                                        Edit7.putString(str, Instant.now().toString());
                                                                                                                        Edit7.putLong(str8, r12);
                                                                                                                        Edit7.apply();
                                                                                                                    } catch (Throwable th3) {
                                                                                                                        th = th3;
                                                                                                                        i = i30;
                                                                                                                        r1 = r23;
                                                                                                                        obj18 = obj18;
                                                                                                                        r12 = r12;
                                                                                                                        r15 = r17;
                                                                                                                        r19 = r19;
                                                                                                                        FileLog.e(th);
                                                                                                                        r15.element = false;
                                                                                                                        r3 = r1;
                                                                                                                        i9 = i;
                                                                                                                        r9 = r15;
                                                                                                                    }
                                                                                                                } else {
                                                                                                                    String str211 = str8;
                                                                                                                    String str212 = str;
                                                                                                                    try {
                                                                                                                        if (response.isSuccessful() || response.body() == null) {
                                                                                                                            r17.element = false;
                                                                                                                        } else {
                                                                                                                            list = (List) response.body();
                                                                                                                            if (list.isEmpty()) {
                                                                                                                                str10 = str212;
                                                                                                                                str11 = str211;
                                                                                                                                r2 = r23;
                                                                                                                                i16 = 1;
                                                                                                                            } else {
                                                                                                                                String str30 = str211;
                                                                                                                                ArrayList arrayList = new ArrayList();
                                                                                                                                str10 = str212;
                                                                                                                                ArrayList arrayList2 = new ArrayList();
                                                                                                                                ?? r29 = r28;
                                                                                                                                for (Object obj19 : list) {
                                                                                                                                    int i40 = i30;
                                                                                                                                    int i41 = i311;
                                                                                                                                    ?? r210 = r29;
                                                                                                                                    if (Intrinsics.areEqual(((ProfileDTO) obj19).getDeleted(), Boxing.boxBoolean(true))) {
                                                                                                                                        arrayList.add(obj19);
                                                                                                                                    } else {
                                                                                                                                        arrayList2.add(obj19);
                                                                                                                                    }
                                                                                                                                    i30 = i40;
                                                                                                                                    i311 = i41;
                                                                                                                                    r29 = r210 == true ? 1 : 0;
                                                                                                                                    break;
                                                                                                                                }
                                                                                                                                int i42 = i30;
                                                                                                                                int i43 = i311;
                                                                                                                                ?? r211 = r29;
                                                                                                                                Pair pair = new Pair(arrayList, arrayList2);
                                                                                                                                List list8 = (List) pair.component1();
                                                                                                                                list2 = (List) pair.component2();
                                                                                                                                if (list8.isEmpty()) {
                                                                                                                                    list3 = list8;
                                                                                                                                    r112 = r12;
                                                                                                                                    r7 = r17;
                                                                                                                                    i17 = 1;
                                                                                                                                    i18 = i310;
                                                                                                                                    i19 = i43;
                                                                                                                                    str12 = string;
                                                                                                                                    i20 = i42;
                                                                                                                                    list4 = list;
                                                                                                                                    obj8 = coroutine_suspended;
                                                                                                                                    apiService3 = apiService10;
                                                                                                                                    r21 = r23;
                                                                                                                                    obj13 = obj18;
                                                                                                                                    r120 = r19;
                                                                                                                                    str15 = str30;
                                                                                                                                    r26 = r211;
                                                                                                                                } else {
                                                                                                                                    DatabaseHelper databaseHelper2 = DatabaseHelper.INSTANCE;
                                                                                                                                    List list9 = list8;
                                                                                                                                    ArrayList arrayList3 = new ArrayList(CollectionsKt.collectionSizeOrDefault(list9, 10));
                                                                                                                                    Iterator it = list9.iterator();
                                                                                                                                    while (it.hasNext()) {
                                                                                                                                        arrayList3.add(Boxing.boxLong(((ProfileDTO) it.next()).getId()));
                                                                                                                                    }
                                                                                                                                    c01341.L$0 = r23;
                                                                                                                                    c01341.L$1 = obj18;
                                                                                                                                    c01341.L$2 = r17;
                                                                                                                                    c01341.L$3 = string;
                                                                                                                                    c01341.L$4 = SpillingKt.nullOutSpilledVariable(str9);
                                                                                                                                    c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService10);
                                                                                                                                    c01341.L$6 = response;
                                                                                                                                    c01341.L$7 = SpillingKt.nullOutSpilledVariable(list);
                                                                                                                                    c01341.L$8 = SpillingKt.nullOutSpilledVariable(list8);
                                                                                                                                    c01341.L$9 = list2;
                                                                                                                                    c01341.Z$0 = z3;
                                                                                                                                    c01341.I$0 = i14;
                                                                                                                                    c01341.I$1 = i310;
                                                                                                                                    c01341.J$0 = r12;
                                                                                                                                    c01341.I$2 = r211 == true ? 1 : 0;
                                                                                                                                    c01341.I$3 = i43;
                                                                                                                                    i21 = r211 == true ? 1 : 0;
                                                                                                                                    try {
                                                                                                                                        c01341.I$4 = i42;
                                                                                                                                        i22 = i42;
                                                                                                                                        c01341.I$5 = 1;
                                                                                                                                        c01341.label = 6;
                                                                                                                                        Object objDeleteProfiles = databaseHelper2.deleteProfiles(arrayList3, c01341);
                                                                                                                                        obj9 = coroutine_suspended;
                                                                                                                                        if (objDeleteProfiles == obj9) {
                                                                                                                                            return obj9;
                                                                                                                                        }
                                                                                                                                        r24 = r23;
                                                                                                                                        r113 = r12;
                                                                                                                                        r114 = r17;
                                                                                                                                        list5 = list8;
                                                                                                                                        apiService4 = apiService10;
                                                                                                                                        i18 = i310;
                                                                                                                                        i19 = i43;
                                                                                                                                        i23 = 1;
                                                                                                                                        list6 = list2;
                                                                                                                                        response2 = response;
                                                                                                                                        obj10 = objDeleteProfiles;
                                                                                                                                        str13 = str9;
                                                                                                                                        obj15 = obj18;
                                                                                                                                        r124 = r19;
                                                                                                                                        str17 = str30;
                                                                                                                                        try {
                                                                                                                                            if (((Boolean) obj10).booleanValue() || i23 == 0) {
                                                                                                                                                i24 = 0;
                                                                                                                                            } else {
                                                                                                                                                i24 = 1;
                                                                                                                                            }
                                                                                                                                            str9 = str13;
                                                                                                                                            i17 = i24;
                                                                                                                                            response = response2;
                                                                                                                                            list2 = list6;
                                                                                                                                            str12 = string;
                                                                                                                                            i20 = i22;
                                                                                                                                            list4 = list;
                                                                                                                                            obj8 = obj9;
                                                                                                                                            r7 = r114;
                                                                                                                                            apiService3 = apiService4;
                                                                                                                                            list3 = list5;
                                                                                                                                            r21 = r24;
                                                                                                                                            obj13 = obj15;
                                                                                                                                            r112 = r113;
                                                                                                                                            r120 = r124;
                                                                                                                                            str15 = str17;
                                                                                                                                            r26 = i21;
                                                                                                                                        } catch (Throwable th4) {
                                                                                                                                            th = th4;
                                                                                                                                            r119 = r113;
                                                                                                                                            r118 = r114;
                                                                                                                                            i = i22;
                                                                                                                                            obj12 = obj15;
                                                                                                                                            r117 = r124;
                                                                                                                                            r25 = r24;
                                                                                                                                            r1 = r25;
                                                                                                                                            obj18 = obj12;
                                                                                                                                            r12 = r119;
                                                                                                                                            r15 = r118;
                                                                                                                                            r19 = r117;
                                                                                                                                            FileLog.e(th);
                                                                                                                                            r15.element = false;
                                                                                                                                            r3 = r1;
                                                                                                                                            i9 = i;
                                                                                                                                            r9 = r15;
                                                                                                                                            if (i9 != 0) {
                                                                                                                                                Edit = r3.edit();
                                                                                                                                                if (r9.element) {
                                                                                                                                                    Edit.remove(r19);
                                                                                                                                                } else {
                                                                                                                                                    Edit.putLong(r19, r12);
                                                                                                                                                }
                                                                                                                                                Edit.apply();
                                                                                                                                            }
                                                                                                                                            z4 = r9.element;
                                                                                                                                            r125 = obj18;
                                                                                                                                            Boolean boolBoxBoolean = Boxing.boxBoolean(z4);
                                                                                                                                            r125.unlock(null);
                                                                                                                                            return boolBoxBoolean;
                                                                                                                                        }
                                                                                                                                    } catch (Throwable th5) {
                                                                                                                                        th = th5;
                                                                                                                                        i15 = i42;
                                                                                                                                        r1 = r23;
                                                                                                                                        i = i15;
                                                                                                                                        obj18 = obj18;
                                                                                                                                        r12 = r12;
                                                                                                                                        r15 = r17;
                                                                                                                                        r19 = r19;
                                                                                                                                    }
                                                                                                                                }
                                                                                                                                r22 = r26;
                                                                                                                                try {
                                                                                                                                    if (list2.isEmpty()) {
                                                                                                                                        i16 = i17;
                                                                                                                                        i30 = i20;
                                                                                                                                        r121 = r7;
                                                                                                                                        string = str12;
                                                                                                                                        r12 = r112;
                                                                                                                                        r2 = r21;
                                                                                                                                    } else {
                                                                                                                                        List<ProfileDTO> list10 = list2;
                                                                                                                                        DatabaseHelper databaseHelper3 = DatabaseHelper.INSTANCE;
                                                                                                                                        c01341.L$0 = r21;
                                                                                                                                        c01341.L$1 = obj13;
                                                                                                                                        c01341.L$2 = r7;
                                                                                                                                        c01341.L$3 = str12;
                                                                                                                                        r27 = r21;
                                                                                                                                        try {
                                                                                                                                            c01341.L$4 = SpillingKt.nullOutSpilledVariable(str9);
                                                                                                                                            c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService3);
                                                                                                                                            c01341.L$6 = response;
                                                                                                                                            c01341.L$7 = SpillingKt.nullOutSpilledVariable(list4);
                                                                                                                                            c01341.L$8 = SpillingKt.nullOutSpilledVariable(list3);
                                                                                                                                            c01341.L$9 = SpillingKt.nullOutSpilledVariable(list10);
                                                                                                                                            c01341.Z$0 = z3;
                                                                                                                                            c01341.I$0 = i14;
                                                                                                                                            c01341.I$1 = i18;
                                                                                                                                            c01341.J$0 = r112;
                                                                                                                                            c01341.I$2 = r22 == true ? 1 : 0;
                                                                                                                                            c01341.I$3 = i19;
                                                                                                                                            c01341.I$4 = i20;
                                                                                                                                            c01341.I$5 = i17;
                                                                                                                                            c01341.label = 7;
                                                                                                                                            objInsertProfiles = databaseHelper3.insertProfiles(list10, c01341);
                                                                                                                                            obj14 = obj8;
                                                                                                                                            if (objInsertProfiles == obj14) {
                                                                                                                                                return obj14;
                                                                                                                                            }
                                                                                                                                            response3 = response;
                                                                                                                                            obj17 = objInsertProfiles;
                                                                                                                                            i30 = i20;
                                                                                                                                            r122 = r7;
                                                                                                                                            r123 = r112;
                                                                                                                                            r4 = r27;
                                                                                                                                            str16 = str12;
                                                                                                                                            obj16 = obj13;
                                                                                                                                            r126 = r120;
                                                                                                                                            str19 = str15;
                                                                                                                                            try {
                                                                                                                                                if (((Boolean) obj17).booleanValue() || i17 == 0) {
                                                                                                                                                    i16 = 0;
                                                                                                                                                } else {
                                                                                                                                                    i16 = 1;
                                                                                                                                                }
                                                                                                                                                response = response3;
                                                                                                                                                string = str16;
                                                                                                                                                r2 = r4;
                                                                                                                                                obj18 = obj16;
                                                                                                                                                r12 = r123;
                                                                                                                                                r121 = r122;
                                                                                                                                                r19 = r126;
                                                                                                                                                str11 = str19;
                                                                                                                                            } catch (Throwable th6) {
                                                                                                                                                th = th6;
                                                                                                                                                i = i30;
                                                                                                                                                r1 = r4;
                                                                                                                                                obj18 = obj16;
                                                                                                                                                r12 = r123;
                                                                                                                                                r15 = r122;
                                                                                                                                                r19 = r126;
                                                                                                                                                FileLog.e(th);
                                                                                                                                                r15.element = false;
                                                                                                                                                r3 = r1;
                                                                                                                                                i9 = i;
                                                                                                                                                r9 = r15;
                                                                                                                                            }
                                                                                                                                        } catch (Throwable th7) {
                                                                                                                                            th = th7;
                                                                                                                                            i = i20;
                                                                                                                                            r118 = r7;
                                                                                                                                            r119 = r112;
                                                                                                                                            obj12 = obj13;
                                                                                                                                            r117 = r120;
                                                                                                                                            r25 = r27;
                                                                                                                                            r1 = r25;
                                                                                                                                            obj18 = obj12;
                                                                                                                                            r12 = r119;
                                                                                                                                            r15 = r118;
                                                                                                                                            r19 = r117;
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                } catch (Throwable th8) {
                                                                                                                                    th = th8;
                                                                                                                                    r27 = r21;
                                                                                                                                }
                                                                                                                            }
                                                                                                                            if (i16 != 0) {
                                                                                                                                obj18 = obj18;
                                                                                                                                r12 = r12;
                                                                                                                                r121 = r17;
                                                                                                                                r19 = r19;
                                                                                                                                obj18 = obj13;
                                                                                                                                r19 = r120;
                                                                                                                                str11 = str15;
                                                                                                                                Edit2 = r2.edit();
                                                                                                                                Edit2.putString(str10, Instant.now().toString());
                                                                                                                                if (string == null && (str18 = response.headers().get("ETag")) != null) {
                                                                                                                                    Edit2.putString(str5, str18);
                                                                                                                                }
                                                                                                                                Edit2.putLong(str11, r12);
                                                                                                                                Edit2.apply();
                                                                                                                            } else {
                                                                                                                                obj18 = obj18;
                                                                                                                                r12 = r12;
                                                                                                                                r121 = r17;
                                                                                                                                r19 = r19;
                                                                                                                                obj18 = obj13;
                                                                                                                                r19 = r120;
                                                                                                                                str11 = str15;
                                                                                                                                r121.element = false;
                                                                                                                            }
                                                                                                                            i9 = i30;
                                                                                                                            r9 = r121;
                                                                                                                            r3 = r2;
                                                                                                                        }
                                                                                                                    } catch (Throwable th9) {
                                                                                                                        th = th9;
                                                                                                                    }
                                                                                                                    r1 = r23;
                                                                                                                    i = i15;
                                                                                                                    obj18 = obj18;
                                                                                                                    r12 = r12;
                                                                                                                    r15 = r17;
                                                                                                                    r19 = r19;
                                                                                                                    FileLog.e(th);
                                                                                                                    r15.element = false;
                                                                                                                    r3 = r1;
                                                                                                                    i9 = i;
                                                                                                                    r9 = r15;
                                                                                                                }
                                                                                                                r3 = r23;
                                                                                                                r9 = r17;
                                                                                                                i9 = i30;
                                                                                                            } catch (Throwable th10) {
                                                                                                                th = th10;
                                                                                                                i15 = i30;
                                                                                                            }
                                                                                                        }
                                                                                                    } else {
                                                                                                        str5 = str4;
                                                                                                        c01341.L$0 = r3;
                                                                                                        c01341.L$1 = obj18;
                                                                                                        c01341.L$2 = r9;
                                                                                                        c01341.L$3 = string;
                                                                                                        c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                                                                        c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                                                                        c01341.Z$0 = z2;
                                                                                                        c01341.I$0 = i6;
                                                                                                        c01341.I$1 = i7;
                                                                                                        c01341.J$0 = r12;
                                                                                                        c01341.I$2 = i8;
                                                                                                        c01341.I$3 = i10;
                                                                                                        c01341.I$4 = 1;
                                                                                                        c01341.label = 5;
                                                                                                        updates = apiService.getUpdates(string, c01341);
                                                                                                        if (updates != coroutine_suspended) {
                                                                                                            i11 = i10;
                                                                                                            obj4 = updates;
                                                                                                            apiService2 = apiService;
                                                                                                            i12 = i7;
                                                                                                            i13 = i8;
                                                                                                            r5 = r3;
                                                                                                            i30 = 1;
                                                                                                            r20 = r9;
                                                                                                            obj11 = obj18;
                                                                                                            r116 = r12;
                                                                                                            r115 = r19;
                                                                                                            str14 = str3;
                                                                                                            response = (Response) obj4;
                                                                                                            r23 = r5;
                                                                                                            r16 = r20;
                                                                                                            obj18 = obj11;
                                                                                                            r12 = r116;
                                                                                                            r19 = r115;
                                                                                                            str8 = str14;
                                                                                                            str9 = string2;
                                                                                                            int i312 = i12;
                                                                                                            i14 = i6;
                                                                                                            ApiService apiService11 = apiService2;
                                                                                                            z3 = z2;
                                                                                                            r17 = r16;
                                                                                                            r28 = i13;
                                                                                                            int i313 = i11;
                                                                                                            if (response.code() == 304) {
                                                                                                                ?? Edit8 = r23.edit();
                                                                                                                Edit8.putString(str, Instant.now().toString());
                                                                                                                Edit8.putLong(str8, r12);
                                                                                                                Edit8.apply();
                                                                                                            } else {
                                                                                                                String str213 = str8;
                                                                                                                String str214 = str;
                                                                                                                if (response.isSuccessful()) {
                                                                                                                }
                                                                                                                r17.element = false;
                                                                                                            }
                                                                                                            r3 = r23;
                                                                                                            r9 = r17;
                                                                                                            i9 = i30;
                                                                                                        }
                                                                                                    }
                                                                                                } catch (Throwable th11) {
                                                                                                    th = th11;
                                                                                                    r1 = r3;
                                                                                                    r15 = r9;
                                                                                                    i = 1;
                                                                                                    obj18 = obj18;
                                                                                                    r12 = r12;
                                                                                                    r19 = r19;
                                                                                                }
                                                                                            }
                                                                                            if (i9 != 0) {
                                                                                                Edit = r3.edit();
                                                                                                if (r9.element) {
                                                                                                    Edit.remove(r19);
                                                                                                } else {
                                                                                                    Edit.putLong(r19, r12);
                                                                                                }
                                                                                                Edit.apply();
                                                                                            }
                                                                                            z4 = r9.element;
                                                                                            r125 = obj18;
                                                                                            break;
                                                                                        }
                                                                                    } catch (CancellationException e) {
                                                                                        e = e;
                                                                                        mutex3 = mutex5;
                                                                                        try {
                                                                                            throw e;
                                                                                        } catch (Throwable th12) {
                                                                                            th = th12;
                                                                                            r128 = mutex3;
                                                                                            r128.unlock(null);
                                                                                            throw th;
                                                                                        }
                                                                                    }
                                                                                } catch (Throwable th13) {
                                                                                    th = th13;
                                                                                    sharedPreferences3 = sharedPreferences9;
                                                                                    str = "lastSyncTimestamp";
                                                                                    obj2 = mutex5;
                                                                                    r8 = booleanRef4;
                                                                                    r129 = j;
                                                                                    r18 = r28;
                                                                                    str2 = str22;
                                                                                }
                                                                            }
                                                                        } catch (Throwable th14) {
                                                                            th = th14;
                                                                            str21 = "profilesEtag";
                                                                            str = "lastSyncTimestamp";
                                                                            str2 = PROFILES_SYNC_TIME_KEY;
                                                                            r18 = SYNC_FAILURE_TIME_KEY;
                                                                            int i44 = i27;
                                                                            i4 = i26;
                                                                            i5 = i44;
                                                                            obj2 = mutex4;
                                                                            r129 = jCurrentTimeMillis;
                                                                            r8 = booleanRef3;
                                                                            i2 = i25;
                                                                            sharedPreferences3 = sharedPreferences8;
                                                                            i30 = 1;
                                                                            i3 = 0;
                                                                            FileLog.e(th);
                                                                            r8.element = false;
                                                                            obj6 = obj2;
                                                                            r10 = r8;
                                                                            r11 = r129;
                                                                            sharedPreferences6 = sharedPreferences3;
                                                                            r110 = r18;
                                                                            str6 = str2;
                                                                            i9 = i30;
                                                                            i10 = i5;
                                                                            i8 = i4;
                                                                            i7 = i3;
                                                                            i6 = i2;
                                                                            r3 = sharedPreferences6;
                                                                            r12 = r11;
                                                                            obj18 = obj6;
                                                                            r9 = r10;
                                                                            r19 = r110;
                                                                            str3 = str6;
                                                                            if (i10 != 0) {
                                                                                i6 = i25;
                                                                                r3 = sharedPreferences7;
                                                                                obj18 = mutex4;
                                                                                r12 = jCurrentTimeMillis;
                                                                                string = r3.getString(str, null);
                                                                                str4 = str21;
                                                                                string2 = r3.getString(str4, null);
                                                                                apiService = ApiClient.INSTANCE.getApiService();
                                                                                if (string == null) {
                                                                                    c01341.L$0 = r3;
                                                                                    c01341.L$1 = obj18;
                                                                                    c01341.L$2 = r9;
                                                                                    c01341.L$3 = string;
                                                                                    str5 = str4;
                                                                                    c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                                                    c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                                                    c01341.Z$0 = z2;
                                                                                    c01341.I$0 = i6;
                                                                                    c01341.I$1 = i7;
                                                                                    c01341.J$0 = r12;
                                                                                    c01341.I$2 = i8;
                                                                                    c01341.I$3 = i10;
                                                                                    c01341.I$4 = 1;
                                                                                    c01341.label = 4;
                                                                                    allProfiles = apiService.getAllProfiles(string2, c01341);
                                                                                    if (allProfiles != coroutine_suspended) {
                                                                                        i11 = i10;
                                                                                        obj5 = allProfiles;
                                                                                        apiService2 = apiService;
                                                                                        i12 = i7;
                                                                                        i13 = i8;
                                                                                        r6 = r3;
                                                                                        i30 = 1;
                                                                                        r14 = r9;
                                                                                        obj7 = obj18;
                                                                                        r13 = r12;
                                                                                        r111 = r19;
                                                                                        str7 = str3;
                                                                                        response = (Response) obj5;
                                                                                        r23 = r6;
                                                                                        r16 = r14;
                                                                                        obj18 = obj7;
                                                                                        r12 = r13;
                                                                                        r19 = r111;
                                                                                        str8 = str7;
                                                                                        str9 = string2;
                                                                                        int i314 = i12;
                                                                                        i14 = i6;
                                                                                        ApiService apiService12 = apiService2;
                                                                                        z3 = z2;
                                                                                        r17 = r16;
                                                                                        r28 = i13;
                                                                                        int i315 = i11;
                                                                                        if (response.code() == 304) {
                                                                                            ?? Edit9 = r23.edit();
                                                                                            Edit9.putString(str, Instant.now().toString());
                                                                                            Edit9.putLong(str8, r12);
                                                                                            Edit9.apply();
                                                                                        } else {
                                                                                            String str215 = str8;
                                                                                            String str216 = str;
                                                                                            if (response.isSuccessful()) {
                                                                                            }
                                                                                            r17.element = false;
                                                                                        }
                                                                                        r3 = r23;
                                                                                        r9 = r17;
                                                                                        i9 = i30;
                                                                                    }
                                                                                } else {
                                                                                    str5 = str4;
                                                                                    c01341.L$0 = r3;
                                                                                    c01341.L$1 = obj18;
                                                                                    c01341.L$2 = r9;
                                                                                    c01341.L$3 = string;
                                                                                    c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                                                    c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                                                    c01341.Z$0 = z2;
                                                                                    c01341.I$0 = i6;
                                                                                    c01341.I$1 = i7;
                                                                                    c01341.J$0 = r12;
                                                                                    c01341.I$2 = i8;
                                                                                    c01341.I$3 = i10;
                                                                                    c01341.I$4 = 1;
                                                                                    c01341.label = 5;
                                                                                    updates = apiService.getUpdates(string, c01341);
                                                                                    if (updates != coroutine_suspended) {
                                                                                        i11 = i10;
                                                                                        obj4 = updates;
                                                                                        apiService2 = apiService;
                                                                                        i12 = i7;
                                                                                        i13 = i8;
                                                                                        r5 = r3;
                                                                                        i30 = 1;
                                                                                        r20 = r9;
                                                                                        obj11 = obj18;
                                                                                        r116 = r12;
                                                                                        r115 = r19;
                                                                                        str14 = str3;
                                                                                        response = (Response) obj4;
                                                                                        r23 = r5;
                                                                                        r16 = r20;
                                                                                        obj18 = obj11;
                                                                                        r12 = r116;
                                                                                        r19 = r115;
                                                                                        str8 = str14;
                                                                                        str9 = string2;
                                                                                        int i316 = i12;
                                                                                        i14 = i6;
                                                                                        ApiService apiService13 = apiService2;
                                                                                        z3 = z2;
                                                                                        r17 = r16;
                                                                                        r28 = i13;
                                                                                        int i317 = i11;
                                                                                        if (response.code() == 304) {
                                                                                            ?? Edit10 = r23.edit();
                                                                                            Edit10.putString(str, Instant.now().toString());
                                                                                            Edit10.putLong(str8, r12);
                                                                                            Edit10.apply();
                                                                                        } else {
                                                                                            String str217 = str8;
                                                                                            String str218 = str;
                                                                                            if (response.isSuccessful()) {
                                                                                            }
                                                                                            r17.element = false;
                                                                                        }
                                                                                        r3 = r23;
                                                                                        r9 = r17;
                                                                                        i9 = i30;
                                                                                    }
                                                                                }
                                                                                return coroutine_suspended;
                                                                            }
                                                                            if (i9 != 0) {
                                                                                Edit = r3.edit();
                                                                                if (r9.element) {
                                                                                    Edit.remove(r19);
                                                                                } else {
                                                                                    Edit.putLong(r19, r12);
                                                                                }
                                                                                Edit.apply();
                                                                            }
                                                                            z4 = r9.element;
                                                                            r125 = obj18;
                                                                            Boolean boolBoxBoolean2 = Boxing.boxBoolean(z4);
                                                                            r125.unlock(null);
                                                                            return boolBoxBoolean2;
                                                                        }
                                                                    } catch (Throwable th15) {
                                                                        th = th15;
                                                                        i25 = i28;
                                                                    }
                                                                } catch (Throwable th16) {
                                                                    th = th16;
                                                                    sharedPreferences8 = sharedPreferences7;
                                                                    str21 = "profilesEtag";
                                                                    str = "lastSyncTimestamp";
                                                                    str2 = PROFILES_SYNC_TIME_KEY;
                                                                    r18 = SYNC_FAILURE_TIME_KEY;
                                                                    int i45 = i27;
                                                                    i4 = i26;
                                                                    i5 = i45;
                                                                    obj2 = mutex4;
                                                                    r129 = jCurrentTimeMillis;
                                                                    r8 = booleanRef3;
                                                                    i2 = i25;
                                                                    sharedPreferences3 = sharedPreferences8;
                                                                    i30 = 1;
                                                                    i3 = 0;
                                                                    FileLog.e(th);
                                                                    r8.element = false;
                                                                    obj6 = obj2;
                                                                    r10 = r8;
                                                                    r11 = r129;
                                                                    sharedPreferences6 = sharedPreferences3;
                                                                    r110 = r18;
                                                                    str6 = str2;
                                                                    i9 = i30;
                                                                    i10 = i5;
                                                                    i8 = i4;
                                                                    i7 = i3;
                                                                    i6 = i2;
                                                                    r3 = sharedPreferences6;
                                                                    r12 = r11;
                                                                    obj18 = obj6;
                                                                    r9 = r10;
                                                                    r19 = r110;
                                                                    str3 = str6;
                                                                    if (i10 != 0) {
                                                                        i6 = i25;
                                                                        r3 = sharedPreferences7;
                                                                        obj18 = mutex4;
                                                                        r12 = jCurrentTimeMillis;
                                                                        string = r3.getString(str, null);
                                                                        str4 = str21;
                                                                        string2 = r3.getString(str4, null);
                                                                        apiService = ApiClient.INSTANCE.getApiService();
                                                                        if (string == null) {
                                                                            c01341.L$0 = r3;
                                                                            c01341.L$1 = obj18;
                                                                            c01341.L$2 = r9;
                                                                            c01341.L$3 = string;
                                                                            str5 = str4;
                                                                            c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                                            c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                                            c01341.Z$0 = z2;
                                                                            c01341.I$0 = i6;
                                                                            c01341.I$1 = i7;
                                                                            c01341.J$0 = r12;
                                                                            c01341.I$2 = i8;
                                                                            c01341.I$3 = i10;
                                                                            c01341.I$4 = 1;
                                                                            c01341.label = 4;
                                                                            allProfiles = apiService.getAllProfiles(string2, c01341);
                                                                            if (allProfiles != coroutine_suspended) {
                                                                                i11 = i10;
                                                                                obj5 = allProfiles;
                                                                                apiService2 = apiService;
                                                                                i12 = i7;
                                                                                i13 = i8;
                                                                                r6 = r3;
                                                                                i30 = 1;
                                                                                r14 = r9;
                                                                                obj7 = obj18;
                                                                                r13 = r12;
                                                                                r111 = r19;
                                                                                str7 = str3;
                                                                                response = (Response) obj5;
                                                                                r23 = r6;
                                                                                r16 = r14;
                                                                                obj18 = obj7;
                                                                                r12 = r13;
                                                                                r19 = r111;
                                                                                str8 = str7;
                                                                                str9 = string2;
                                                                                int i318 = i12;
                                                                                i14 = i6;
                                                                                ApiService apiService14 = apiService2;
                                                                                z3 = z2;
                                                                                r17 = r16;
                                                                                r28 = i13;
                                                                                int i319 = i11;
                                                                                if (response.code() == 304) {
                                                                                    ?? Edit11 = r23.edit();
                                                                                    Edit11.putString(str, Instant.now().toString());
                                                                                    Edit11.putLong(str8, r12);
                                                                                    Edit11.apply();
                                                                                } else {
                                                                                    String str219 = str8;
                                                                                    String str2110 = str;
                                                                                    if (response.isSuccessful()) {
                                                                                    }
                                                                                    r17.element = false;
                                                                                }
                                                                                r3 = r23;
                                                                                r9 = r17;
                                                                                i9 = i30;
                                                                            }
                                                                        } else {
                                                                            str5 = str4;
                                                                            c01341.L$0 = r3;
                                                                            c01341.L$1 = obj18;
                                                                            c01341.L$2 = r9;
                                                                            c01341.L$3 = string;
                                                                            c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                                            c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                                            c01341.Z$0 = z2;
                                                                            c01341.I$0 = i6;
                                                                            c01341.I$1 = i7;
                                                                            c01341.J$0 = r12;
                                                                            c01341.I$2 = i8;
                                                                            c01341.I$3 = i10;
                                                                            c01341.I$4 = 1;
                                                                            c01341.label = 5;
                                                                            updates = apiService.getUpdates(string, c01341);
                                                                            if (updates != coroutine_suspended) {
                                                                                i11 = i10;
                                                                                obj4 = updates;
                                                                                apiService2 = apiService;
                                                                                i12 = i7;
                                                                                i13 = i8;
                                                                                r5 = r3;
                                                                                i30 = 1;
                                                                                r20 = r9;
                                                                                obj11 = obj18;
                                                                                r116 = r12;
                                                                                r115 = r19;
                                                                                str14 = str3;
                                                                                response = (Response) obj4;
                                                                                r23 = r5;
                                                                                r16 = r20;
                                                                                obj18 = obj11;
                                                                                r12 = r116;
                                                                                r19 = r115;
                                                                                str8 = str14;
                                                                                str9 = string2;
                                                                                int i3110 = i12;
                                                                                i14 = i6;
                                                                                ApiService apiService15 = apiService2;
                                                                                z3 = z2;
                                                                                r17 = r16;
                                                                                r28 = i13;
                                                                                int i3111 = i11;
                                                                                if (response.code() == 304) {
                                                                                    ?? Edit12 = r23.edit();
                                                                                    Edit12.putString(str, Instant.now().toString());
                                                                                    Edit12.putLong(str8, r12);
                                                                                    Edit12.apply();
                                                                                } else {
                                                                                    String str2111 = str8;
                                                                                    String str2112 = str;
                                                                                    if (response.isSuccessful()) {
                                                                                    }
                                                                                    r17.element = false;
                                                                                }
                                                                                r3 = r23;
                                                                                r9 = r17;
                                                                                i9 = i30;
                                                                            }
                                                                        }
                                                                        return coroutine_suspended;
                                                                    }
                                                                    if (i9 != 0) {
                                                                        Edit = r3.edit();
                                                                        if (r9.element) {
                                                                            Edit.remove(r19);
                                                                        } else {
                                                                            Edit.putLong(r19, r12);
                                                                        }
                                                                        Edit.apply();
                                                                    }
                                                                    z4 = r9.element;
                                                                    r125 = obj18;
                                                                    Boolean boolBoxBoolean3 = Boxing.boxBoolean(z4);
                                                                    r125.unlock(null);
                                                                    return boolBoxBoolean3;
                                                                }
                                                            } catch (Throwable th17) {
                                                                th = th17;
                                                                z2 = z5;
                                                            }
                                                        } catch (CancellationException e2) {
                                                            e = e2;
                                                            mutex3 = mutex4;
                                                            throw e;
                                                        }
                                                    }
                                                } else {
                                                    z4 = true;
                                                    r125 = mutex4;
                                                }
                                            } else {
                                                z4 = false;
                                                r125 = mutex4;
                                            }
                                            Boolean boolBoxBoolean4 = Boxing.boxBoolean(z4);
                                            r125.unlock(null);
                                            return boolBoxBoolean4;
                                        }
                                        return coroutine_suspended;
                                    case 1:
                                        int i46 = c01341.I$0;
                                        boolean z6 = c01341.Z$0;
                                        Mutex mutex8 = (Mutex) c01341.L$1;
                                        SharedPreferences sharedPreferences12 = (SharedPreferences) c01341.L$0;
                                        ResultKt.throwOnFailure(obj17);
                                        mutex4 = mutex8;
                                        z5 = z6;
                                        sharedPreferences7 = sharedPreferences12;
                                        i25 = i46;
                                        jCurrentTimeMillis = System.currentTimeMillis();
                                        if (z5) {
                                            break;
                                        }
                                        if (z5 == 0) {
                                            i26 = 1;
                                        } else {
                                            i26 = 1;
                                        }
                                        if (z5 == 0) {
                                            i27 = 1;
                                        } else {
                                            i27 = 1;
                                        }
                                        if (i26 == 0) {
                                        }
                                        booleanRef3 = new Ref.BooleanRef();
                                        booleanRef3.element = true;
                                        if (i26 != 0) {
                                            z2 = z5;
                                            str21 = "profilesEtag";
                                            str = "lastSyncTimestamp";
                                            str3 = PROFILES_SYNC_TIME_KEY;
                                            r19 = SYNC_FAILURE_TIME_KEY;
                                            i8 = i26;
                                            i10 = i27;
                                            r9 = booleanRef3;
                                            i9 = 0;
                                            i7 = 0;
                                            if (i10 != 0) {
                                                i6 = i25;
                                                r3 = sharedPreferences7;
                                                obj18 = mutex4;
                                                r12 = jCurrentTimeMillis;
                                                string = r3.getString(str, null);
                                                str4 = str21;
                                                string2 = r3.getString(str4, null);
                                                apiService = ApiClient.INSTANCE.getApiService();
                                                if (string == null) {
                                                    c01341.L$0 = r3;
                                                    c01341.L$1 = obj18;
                                                    c01341.L$2 = r9;
                                                    c01341.L$3 = string;
                                                    str5 = str4;
                                                    c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                    c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                    c01341.Z$0 = z2;
                                                    c01341.I$0 = i6;
                                                    c01341.I$1 = i7;
                                                    c01341.J$0 = r12;
                                                    c01341.I$2 = i8;
                                                    c01341.I$3 = i10;
                                                    c01341.I$4 = 1;
                                                    c01341.label = 4;
                                                    allProfiles = apiService.getAllProfiles(string2, c01341);
                                                    if (allProfiles != coroutine_suspended) {
                                                        i11 = i10;
                                                        obj5 = allProfiles;
                                                        apiService2 = apiService;
                                                        i12 = i7;
                                                        i13 = i8;
                                                        r6 = r3;
                                                        i30 = 1;
                                                        r14 = r9;
                                                        obj7 = obj18;
                                                        r13 = r12;
                                                        r111 = r19;
                                                        str7 = str3;
                                                        response = (Response) obj5;
                                                        r23 = r6;
                                                        r16 = r14;
                                                        obj18 = obj7;
                                                        r12 = r13;
                                                        r19 = r111;
                                                        str8 = str7;
                                                        str9 = string2;
                                                        int i3112 = i12;
                                                        i14 = i6;
                                                        ApiService apiService16 = apiService2;
                                                        z3 = z2;
                                                        r17 = r16;
                                                        r28 = i13;
                                                        int i3113 = i11;
                                                        if (response.code() == 304) {
                                                            ?? Edit13 = r23.edit();
                                                            Edit13.putString(str, Instant.now().toString());
                                                            Edit13.putLong(str8, r12);
                                                            Edit13.apply();
                                                        } else {
                                                            String str2113 = str8;
                                                            String str2114 = str;
                                                            if (response.isSuccessful()) {
                                                            }
                                                            r17.element = false;
                                                        }
                                                        r3 = r23;
                                                        r9 = r17;
                                                        i9 = i30;
                                                    }
                                                } else {
                                                    str5 = str4;
                                                    c01341.L$0 = r3;
                                                    c01341.L$1 = obj18;
                                                    c01341.L$2 = r9;
                                                    c01341.L$3 = string;
                                                    c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                    c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                    c01341.Z$0 = z2;
                                                    c01341.I$0 = i6;
                                                    c01341.I$1 = i7;
                                                    c01341.J$0 = r12;
                                                    c01341.I$2 = i8;
                                                    c01341.I$3 = i10;
                                                    c01341.I$4 = 1;
                                                    c01341.label = 5;
                                                    updates = apiService.getUpdates(string, c01341);
                                                    if (updates != coroutine_suspended) {
                                                        i11 = i10;
                                                        obj4 = updates;
                                                        apiService2 = apiService;
                                                        i12 = i7;
                                                        i13 = i8;
                                                        r5 = r3;
                                                        i30 = 1;
                                                        r20 = r9;
                                                        obj11 = obj18;
                                                        r116 = r12;
                                                        r115 = r19;
                                                        str14 = str3;
                                                        response = (Response) obj4;
                                                        r23 = r5;
                                                        r16 = r20;
                                                        obj18 = obj11;
                                                        r12 = r116;
                                                        r19 = r115;
                                                        str8 = str14;
                                                        str9 = string2;
                                                        int i3114 = i12;
                                                        i14 = i6;
                                                        ApiService apiService17 = apiService2;
                                                        z3 = z2;
                                                        r17 = r16;
                                                        r28 = i13;
                                                        int i3115 = i11;
                                                        if (response.code() == 304) {
                                                            ?? Edit14 = r23.edit();
                                                            Edit14.putString(str, Instant.now().toString());
                                                            Edit14.putLong(str8, r12);
                                                            Edit14.apply();
                                                        } else {
                                                            String str2115 = str8;
                                                            String str2116 = str;
                                                            if (response.isSuccessful()) {
                                                            }
                                                            r17.element = false;
                                                        }
                                                        r3 = r23;
                                                        r9 = r17;
                                                        i9 = i30;
                                                    }
                                                }
                                                break;
                                            }
                                            if (i9 != 0) {
                                                Edit = r3.edit();
                                                if (r9.element) {
                                                    Edit.remove(r19);
                                                } else {
                                                    Edit.putLong(r19, r12);
                                                }
                                                Edit.apply();
                                            }
                                            z4 = r9.element;
                                            r125 = obj18;
                                            Boolean boolBoxBoolean5 = Boxing.boxBoolean(z4);
                                            r125.unlock(null);
                                            return boolBoxBoolean5;
                                        }
                                        ApiService apiService18 = ApiClient.INSTANCE.getApiService();
                                        c01341.L$0 = sharedPreferences7;
                                        c01341.L$1 = mutex4;
                                        c01341.L$2 = booleanRef3;
                                        z2 = z5;
                                        c01341.Z$0 = z2;
                                        sharedPreferences8 = sharedPreferences7;
                                        i28 = i25;
                                        c01341.I$0 = i28;
                                        i25 = i28;
                                        c01341.I$1 = 0;
                                        c01341.J$0 = jCurrentTimeMillis;
                                        c01341.I$2 = i26;
                                        c01341.I$3 = i27;
                                        c01341.I$4 = 1;
                                        c01341.label = 2;
                                        boostySubscribers = apiService18.getBoostySubscribers(c01341);
                                        if (boostySubscribers != coroutine_suspended) {
                                            int i320 = i27;
                                            i4 = i26;
                                            i5 = i320;
                                            mutex5 = mutex4;
                                            j = jCurrentTimeMillis;
                                            booleanRef4 = booleanRef3;
                                            i2 = i25;
                                            sharedPreferences9 = sharedPreferences8;
                                            i30 = 1;
                                            i3 = 0;
                                            response4 = (Response) boostySubscribers;
                                            if (!response4.isSuccessful()) {
                                                break;
                                            }
                                            sharedPreferences6 = sharedPreferences9;
                                            str = "lastSyncTimestamp";
                                            booleanRef4.element = false;
                                            obj6 = mutex5;
                                            r10 = booleanRef4;
                                            r11 = j;
                                            r110 = r28;
                                            str6 = str22;
                                            i9 = i30;
                                            i10 = i5;
                                            i8 = i4;
                                            i7 = i3;
                                            i6 = i2;
                                            r3 = sharedPreferences6;
                                            r12 = r11;
                                            obj18 = obj6;
                                            r9 = r10;
                                            r19 = r110;
                                            str3 = str6;
                                            if (i10 != 0) {
                                                i6 = i25;
                                                r3 = sharedPreferences7;
                                                obj18 = mutex4;
                                                r12 = jCurrentTimeMillis;
                                                string = r3.getString(str, null);
                                                str4 = str21;
                                                string2 = r3.getString(str4, null);
                                                apiService = ApiClient.INSTANCE.getApiService();
                                                if (string == null) {
                                                    c01341.L$0 = r3;
                                                    c01341.L$1 = obj18;
                                                    c01341.L$2 = r9;
                                                    c01341.L$3 = string;
                                                    str5 = str4;
                                                    c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                    c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                    c01341.Z$0 = z2;
                                                    c01341.I$0 = i6;
                                                    c01341.I$1 = i7;
                                                    c01341.J$0 = r12;
                                                    c01341.I$2 = i8;
                                                    c01341.I$3 = i10;
                                                    c01341.I$4 = 1;
                                                    c01341.label = 4;
                                                    allProfiles = apiService.getAllProfiles(string2, c01341);
                                                    if (allProfiles != coroutine_suspended) {
                                                        i11 = i10;
                                                        obj5 = allProfiles;
                                                        apiService2 = apiService;
                                                        i12 = i7;
                                                        i13 = i8;
                                                        r6 = r3;
                                                        i30 = 1;
                                                        r14 = r9;
                                                        obj7 = obj18;
                                                        r13 = r12;
                                                        r111 = r19;
                                                        str7 = str3;
                                                        response = (Response) obj5;
                                                        r23 = r6;
                                                        r16 = r14;
                                                        obj18 = obj7;
                                                        r12 = r13;
                                                        r19 = r111;
                                                        str8 = str7;
                                                        str9 = string2;
                                                        int i3116 = i12;
                                                        i14 = i6;
                                                        ApiService apiService19 = apiService2;
                                                        z3 = z2;
                                                        r17 = r16;
                                                        r28 = i13;
                                                        int i3117 = i11;
                                                        if (response.code() == 304) {
                                                            ?? Edit15 = r23.edit();
                                                            Edit15.putString(str, Instant.now().toString());
                                                            Edit15.putLong(str8, r12);
                                                            Edit15.apply();
                                                        } else {
                                                            String str2117 = str8;
                                                            String str2118 = str;
                                                            if (response.isSuccessful()) {
                                                            }
                                                            r17.element = false;
                                                        }
                                                        r3 = r23;
                                                        r9 = r17;
                                                        i9 = i30;
                                                    }
                                                } else {
                                                    str5 = str4;
                                                    c01341.L$0 = r3;
                                                    c01341.L$1 = obj18;
                                                    c01341.L$2 = r9;
                                                    c01341.L$3 = string;
                                                    c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                    c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                    c01341.Z$0 = z2;
                                                    c01341.I$0 = i6;
                                                    c01341.I$1 = i7;
                                                    c01341.J$0 = r12;
                                                    c01341.I$2 = i8;
                                                    c01341.I$3 = i10;
                                                    c01341.I$4 = 1;
                                                    c01341.label = 5;
                                                    updates = apiService.getUpdates(string, c01341);
                                                    if (updates != coroutine_suspended) {
                                                        i11 = i10;
                                                        obj4 = updates;
                                                        apiService2 = apiService;
                                                        i12 = i7;
                                                        i13 = i8;
                                                        r5 = r3;
                                                        i30 = 1;
                                                        r20 = r9;
                                                        obj11 = obj18;
                                                        r116 = r12;
                                                        r115 = r19;
                                                        str14 = str3;
                                                        response = (Response) obj4;
                                                        r23 = r5;
                                                        r16 = r20;
                                                        obj18 = obj11;
                                                        r12 = r116;
                                                        r19 = r115;
                                                        str8 = str14;
                                                        str9 = string2;
                                                        int i3118 = i12;
                                                        i14 = i6;
                                                        ApiService apiService110 = apiService2;
                                                        z3 = z2;
                                                        r17 = r16;
                                                        r28 = i13;
                                                        int i3119 = i11;
                                                        if (response.code() == 304) {
                                                            ?? Edit16 = r23.edit();
                                                            Edit16.putString(str, Instant.now().toString());
                                                            Edit16.putLong(str8, r12);
                                                            Edit16.apply();
                                                        } else {
                                                            String str2119 = str8;
                                                            String str21110 = str;
                                                            if (response.isSuccessful()) {
                                                            }
                                                            r17.element = false;
                                                        }
                                                        r3 = r23;
                                                        r9 = r17;
                                                        i9 = i30;
                                                    }
                                                }
                                                break;
                                            }
                                            if (i9 != 0) {
                                                Edit = r3.edit();
                                                if (r9.element) {
                                                    Edit.remove(r19);
                                                } else {
                                                    Edit.putLong(r19, r12);
                                                }
                                                Edit.apply();
                                            }
                                            z4 = r9.element;
                                            r125 = obj18;
                                            Boolean boolBoxBoolean6 = Boxing.boxBoolean(z4);
                                            r125.unlock(null);
                                            return boolBoxBoolean6;
                                        }
                                        return coroutine_suspended;
                                    case 2:
                                        i30 = c01341.I$4;
                                        i5 = c01341.I$3;
                                        i4 = c01341.I$2;
                                        long j3 = c01341.J$0;
                                        i3 = c01341.I$1;
                                        i2 = c01341.I$0;
                                        z2 = c01341.Z$0;
                                        Ref.BooleanRef booleanRef6 = (Ref.BooleanRef) c01341.L$2;
                                        Mutex mutex9 = (Mutex) c01341.L$1;
                                        SharedPreferences sharedPreferences13 = (SharedPreferences) c01341.L$0;
                                        ResultKt.throwOnFailure(obj17);
                                        boostySubscribers = obj17;
                                        sharedPreferences9 = sharedPreferences13;
                                        mutex5 = mutex9;
                                        booleanRef4 = booleanRef6;
                                        j = j3;
                                        response4 = (Response) boostySubscribers;
                                        if (!response4.isSuccessful()) {
                                            break;
                                        }
                                        sharedPreferences6 = sharedPreferences9;
                                        str = "lastSyncTimestamp";
                                        booleanRef4.element = false;
                                        obj6 = mutex5;
                                        r10 = booleanRef4;
                                        r11 = j;
                                        r110 = r28;
                                        str6 = str22;
                                        i9 = i30;
                                        i10 = i5;
                                        i8 = i4;
                                        i7 = i3;
                                        i6 = i2;
                                        r3 = sharedPreferences6;
                                        r12 = r11;
                                        obj18 = obj6;
                                        r9 = r10;
                                        r19 = r110;
                                        str3 = str6;
                                        if (i10 != 0) {
                                            i6 = i25;
                                            r3 = sharedPreferences7;
                                            obj18 = mutex4;
                                            r12 = jCurrentTimeMillis;
                                            string = r3.getString(str, null);
                                            str4 = str21;
                                            string2 = r3.getString(str4, null);
                                            apiService = ApiClient.INSTANCE.getApiService();
                                            if (string == null) {
                                                str5 = str4;
                                                c01341.L$0 = r3;
                                                c01341.L$1 = obj18;
                                                c01341.L$2 = r9;
                                                c01341.L$3 = string;
                                                c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                c01341.Z$0 = z2;
                                                c01341.I$0 = i6;
                                                c01341.I$1 = i7;
                                                c01341.J$0 = r12;
                                                c01341.I$2 = i8;
                                                c01341.I$3 = i10;
                                                c01341.I$4 = 1;
                                                c01341.label = 5;
                                                updates = apiService.getUpdates(string, c01341);
                                                if (updates != coroutine_suspended) {
                                                    i11 = i10;
                                                    obj4 = updates;
                                                    apiService2 = apiService;
                                                    i12 = i7;
                                                    i13 = i8;
                                                    r5 = r3;
                                                    i30 = 1;
                                                    r20 = r9;
                                                    obj11 = obj18;
                                                    r116 = r12;
                                                    r115 = r19;
                                                    str14 = str3;
                                                    response = (Response) obj4;
                                                    r23 = r5;
                                                    r16 = r20;
                                                    obj18 = obj11;
                                                    r12 = r116;
                                                    r19 = r115;
                                                    str8 = str14;
                                                    str9 = string2;
                                                    int i31110 = i12;
                                                    i14 = i6;
                                                    ApiService apiService111 = apiService2;
                                                    z3 = z2;
                                                    r17 = r16;
                                                    r28 = i13;
                                                    int i31111 = i11;
                                                    if (response.code() == 304) {
                                                        ?? Edit17 = r23.edit();
                                                        Edit17.putString(str, Instant.now().toString());
                                                        Edit17.putLong(str8, r12);
                                                        Edit17.apply();
                                                    } else {
                                                        String str21111 = str8;
                                                        String str21112 = str;
                                                        if (response.isSuccessful()) {
                                                        }
                                                        r17.element = false;
                                                    }
                                                    r3 = r23;
                                                    r9 = r17;
                                                    i9 = i30;
                                                }
                                                break;
                                            } else {
                                                c01341.L$0 = r3;
                                                c01341.L$1 = obj18;
                                                c01341.L$2 = r9;
                                                c01341.L$3 = string;
                                                str5 = str4;
                                                c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                c01341.Z$0 = z2;
                                                c01341.I$0 = i6;
                                                c01341.I$1 = i7;
                                                c01341.J$0 = r12;
                                                c01341.I$2 = i8;
                                                c01341.I$3 = i10;
                                                c01341.I$4 = 1;
                                                c01341.label = 4;
                                                allProfiles = apiService.getAllProfiles(string2, c01341);
                                                if (allProfiles != coroutine_suspended) {
                                                    i11 = i10;
                                                    obj5 = allProfiles;
                                                    apiService2 = apiService;
                                                    i12 = i7;
                                                    i13 = i8;
                                                    r6 = r3;
                                                    i30 = 1;
                                                    r14 = r9;
                                                    obj7 = obj18;
                                                    r13 = r12;
                                                    r111 = r19;
                                                    str7 = str3;
                                                    response = (Response) obj5;
                                                    r23 = r6;
                                                    r16 = r14;
                                                    obj18 = obj7;
                                                    r12 = r13;
                                                    r19 = r111;
                                                    str8 = str7;
                                                    str9 = string2;
                                                    int i31112 = i12;
                                                    i14 = i6;
                                                    ApiService apiService112 = apiService2;
                                                    z3 = z2;
                                                    r17 = r16;
                                                    r28 = i13;
                                                    int i31113 = i11;
                                                    if (response.code() == 304) {
                                                        ?? Edit18 = r23.edit();
                                                        Edit18.putString(str, Instant.now().toString());
                                                        Edit18.putLong(str8, r12);
                                                        Edit18.apply();
                                                    } else {
                                                        String str21113 = str8;
                                                        String str21114 = str;
                                                        if (response.isSuccessful()) {
                                                        }
                                                        r17.element = false;
                                                    }
                                                    r3 = r23;
                                                    r9 = r17;
                                                    i9 = i30;
                                                }
                                                break;
                                            }
                                            return coroutine_suspended;
                                        }
                                        if (i9 != 0) {
                                            Edit = r3.edit();
                                            if (r9.element) {
                                                Edit.remove(r19);
                                            } else {
                                                Edit.putLong(r19, r12);
                                            }
                                            Edit.apply();
                                        }
                                        z4 = r9.element;
                                        r125 = obj18;
                                        Boolean boolBoxBoolean7 = Boxing.boxBoolean(z4);
                                        r125.unlock(null);
                                        return boolBoxBoolean7;
                                    case 3:
                                        i30 = c01341.I$4;
                                        i5 = c01341.I$3;
                                        i4 = c01341.I$2;
                                        long j4 = c01341.J$0;
                                        i3 = c01341.I$1;
                                        i2 = c01341.I$0;
                                        z2 = c01341.Z$0;
                                        Ref.BooleanRef booleanRef7 = (Ref.BooleanRef) c01341.L$2;
                                        Mutex mutex10 = (Mutex) c01341.L$1;
                                        SharedPreferences sharedPreferences14 = (SharedPreferences) c01341.L$0;
                                        ResultKt.throwOnFailure(obj17);
                                        str21 = "profilesEtag";
                                        str = "lastSyncTimestamp";
                                        objInsertBoostySubscribers = obj17;
                                        sharedPreferences10 = sharedPreferences14;
                                        str20 = PROFILES_SYNC_TIME_KEY;
                                        mutex6 = mutex10;
                                        r127 = SYNC_FAILURE_TIME_KEY;
                                        booleanRef5 = booleanRef7;
                                        j2 = j4;
                                        if (((Boolean) objInsertBoostySubscribers).booleanValue()) {
                                            SharedPreferences.Editor editorEdit2 = sharedPreferences10.edit();
                                            editorEdit2.putLong(BOOSTY_SYNC_TIME_KEY, j2);
                                            editorEdit2.apply();
                                            obj6 = mutex6;
                                            r10 = booleanRef5;
                                            r11 = j2;
                                            sharedPreferences6 = sharedPreferences10;
                                            r110 = r127;
                                            str6 = str20;
                                        } else {
                                            booleanRef5.element = false;
                                            obj6 = mutex6;
                                            r10 = booleanRef5;
                                            r11 = j2;
                                            sharedPreferences6 = sharedPreferences10;
                                            r110 = r127;
                                            str6 = str20;
                                        }
                                        i9 = i30;
                                        i10 = i5;
                                        i8 = i4;
                                        i7 = i3;
                                        i6 = i2;
                                        r3 = sharedPreferences6;
                                        r12 = r11;
                                        obj18 = obj6;
                                        r9 = r10;
                                        r19 = r110;
                                        str3 = str6;
                                        if (i10 != 0) {
                                            i6 = i25;
                                            r3 = sharedPreferences7;
                                            obj18 = mutex4;
                                            r12 = jCurrentTimeMillis;
                                            string = r3.getString(str, null);
                                            str4 = str21;
                                            string2 = r3.getString(str4, null);
                                            apiService = ApiClient.INSTANCE.getApiService();
                                            if (string == null) {
                                                str5 = str4;
                                                c01341.L$0 = r3;
                                                c01341.L$1 = obj18;
                                                c01341.L$2 = r9;
                                                c01341.L$3 = string;
                                                c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                c01341.Z$0 = z2;
                                                c01341.I$0 = i6;
                                                c01341.I$1 = i7;
                                                c01341.J$0 = r12;
                                                c01341.I$2 = i8;
                                                c01341.I$3 = i10;
                                                c01341.I$4 = 1;
                                                c01341.label = 5;
                                                updates = apiService.getUpdates(string, c01341);
                                                if (updates != coroutine_suspended) {
                                                    i11 = i10;
                                                    obj4 = updates;
                                                    apiService2 = apiService;
                                                    i12 = i7;
                                                    i13 = i8;
                                                    r5 = r3;
                                                    i30 = 1;
                                                    r20 = r9;
                                                    obj11 = obj18;
                                                    r116 = r12;
                                                    r115 = r19;
                                                    str14 = str3;
                                                    response = (Response) obj4;
                                                    r23 = r5;
                                                    r16 = r20;
                                                    obj18 = obj11;
                                                    r12 = r116;
                                                    r19 = r115;
                                                    str8 = str14;
                                                    str9 = string2;
                                                    int i31114 = i12;
                                                    i14 = i6;
                                                    ApiService apiService113 = apiService2;
                                                    z3 = z2;
                                                    r17 = r16;
                                                    r28 = i13;
                                                    int i31115 = i11;
                                                    if (response.code() == 304) {
                                                        ?? Edit19 = r23.edit();
                                                        Edit19.putString(str, Instant.now().toString());
                                                        Edit19.putLong(str8, r12);
                                                        Edit19.apply();
                                                    } else {
                                                        String str21115 = str8;
                                                        String str21116 = str;
                                                        if (response.isSuccessful()) {
                                                        }
                                                        r17.element = false;
                                                    }
                                                    r3 = r23;
                                                    r9 = r17;
                                                    i9 = i30;
                                                }
                                                break;
                                            } else {
                                                c01341.L$0 = r3;
                                                c01341.L$1 = obj18;
                                                c01341.L$2 = r9;
                                                c01341.L$3 = string;
                                                str5 = str4;
                                                c01341.L$4 = SpillingKt.nullOutSpilledVariable(string2);
                                                c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService);
                                                c01341.Z$0 = z2;
                                                c01341.I$0 = i6;
                                                c01341.I$1 = i7;
                                                c01341.J$0 = r12;
                                                c01341.I$2 = i8;
                                                c01341.I$3 = i10;
                                                c01341.I$4 = 1;
                                                c01341.label = 4;
                                                allProfiles = apiService.getAllProfiles(string2, c01341);
                                                if (allProfiles != coroutine_suspended) {
                                                    i11 = i10;
                                                    obj5 = allProfiles;
                                                    apiService2 = apiService;
                                                    i12 = i7;
                                                    i13 = i8;
                                                    r6 = r3;
                                                    i30 = 1;
                                                    r14 = r9;
                                                    obj7 = obj18;
                                                    r13 = r12;
                                                    r111 = r19;
                                                    str7 = str3;
                                                    response = (Response) obj5;
                                                    r23 = r6;
                                                    r16 = r14;
                                                    obj18 = obj7;
                                                    r12 = r13;
                                                    r19 = r111;
                                                    str8 = str7;
                                                    str9 = string2;
                                                    int i31116 = i12;
                                                    i14 = i6;
                                                    ApiService apiService114 = apiService2;
                                                    z3 = z2;
                                                    r17 = r16;
                                                    r28 = i13;
                                                    int i31117 = i11;
                                                    if (response.code() == 304) {
                                                        ?? Edit110 = r23.edit();
                                                        Edit110.putString(str, Instant.now().toString());
                                                        Edit110.putLong(str8, r12);
                                                        Edit110.apply();
                                                    } else {
                                                        String str21117 = str8;
                                                        String str21118 = str;
                                                        if (response.isSuccessful()) {
                                                        }
                                                        r17.element = false;
                                                    }
                                                    r3 = r23;
                                                    r9 = r17;
                                                    i9 = i30;
                                                }
                                                break;
                                            }
                                            return coroutine_suspended;
                                        }
                                        if (i9 != 0) {
                                            Edit = r3.edit();
                                            if (r9.element) {
                                                Edit.remove(r19);
                                            } else {
                                                Edit.putLong(r19, r12);
                                            }
                                            Edit.apply();
                                        }
                                        z4 = r9.element;
                                        r125 = obj18;
                                        Boolean boolBoxBoolean8 = Boxing.boxBoolean(z4);
                                        r125.unlock(null);
                                        return boolBoxBoolean8;
                                    case 4:
                                        i30 = c01341.I$4;
                                        int i47 = c01341.I$3;
                                        int i48 = c01341.I$2;
                                        long j5 = c01341.J$0;
                                        i12 = c01341.I$1;
                                        i6 = c01341.I$0;
                                        boolean z7 = c01341.Z$0;
                                        apiService2 = (ApiService) c01341.L$5;
                                        String str31 = (String) c01341.L$4;
                                        i11 = i47;
                                        String str32 = (String) c01341.L$3;
                                        Ref.BooleanRef booleanRef8 = (Ref.BooleanRef) c01341.L$2;
                                        Mutex mutex11 = (Mutex) c01341.L$1;
                                        SharedPreferences sharedPreferences15 = (SharedPreferences) c01341.L$0;
                                        ResultKt.throwOnFailure(obj17);
                                        str7 = PROFILES_SYNC_TIME_KEY;
                                        string2 = str31;
                                        z2 = z7;
                                        obj7 = mutex11;
                                        r111 = SYNC_FAILURE_TIME_KEY;
                                        r14 = booleanRef8;
                                        str = "lastSyncTimestamp";
                                        obj5 = obj17;
                                        i13 = i48;
                                        r6 = sharedPreferences15;
                                        str5 = "profilesEtag";
                                        string = str32;
                                        r13 = j5;
                                        response = (Response) obj5;
                                        r23 = r6;
                                        r16 = r14;
                                        obj18 = obj7;
                                        r12 = r13;
                                        r19 = r111;
                                        str8 = str7;
                                        str9 = string2;
                                        int i31118 = i12;
                                        i14 = i6;
                                        ApiService apiService115 = apiService2;
                                        z3 = z2;
                                        r17 = r16;
                                        r28 = i13;
                                        int i31119 = i11;
                                        if (response.code() == 304) {
                                            String str21119 = str8;
                                            String str211110 = str;
                                            if (response.isSuccessful()) {
                                            }
                                            r17.element = false;
                                            break;
                                        } else {
                                            ?? Edit111 = r23.edit();
                                            Edit111.putString(str, Instant.now().toString());
                                            Edit111.putLong(str8, r12);
                                            Edit111.apply();
                                        }
                                        r3 = r23;
                                        r9 = r17;
                                        i9 = i30;
                                        if (i9 != 0) {
                                            Edit = r3.edit();
                                            if (r9.element) {
                                                Edit.remove(r19);
                                            } else {
                                                Edit.putLong(r19, r12);
                                            }
                                            Edit.apply();
                                        }
                                        z4 = r9.element;
                                        r125 = obj18;
                                        Boolean boolBoxBoolean9 = Boxing.boxBoolean(z4);
                                        r125.unlock(null);
                                        return boolBoxBoolean9;
                                    case 5:
                                        i30 = c01341.I$4;
                                        int i49 = c01341.I$3;
                                        int i50 = c01341.I$2;
                                        long j6 = c01341.J$0;
                                        i12 = c01341.I$1;
                                        i6 = c01341.I$0;
                                        boolean z8 = c01341.Z$0;
                                        apiService2 = (ApiService) c01341.L$5;
                                        String str33 = (String) c01341.L$4;
                                        i11 = i49;
                                        String str34 = (String) c01341.L$3;
                                        Ref.BooleanRef booleanRef9 = (Ref.BooleanRef) c01341.L$2;
                                        Mutex mutex12 = (Mutex) c01341.L$1;
                                        SharedPreferences sharedPreferences16 = (SharedPreferences) c01341.L$0;
                                        ResultKt.throwOnFailure(obj17);
                                        str14 = PROFILES_SYNC_TIME_KEY;
                                        string2 = str33;
                                        z2 = z8;
                                        obj11 = mutex12;
                                        r115 = SYNC_FAILURE_TIME_KEY;
                                        r20 = booleanRef9;
                                        str = "lastSyncTimestamp";
                                        obj4 = obj17;
                                        i13 = i50;
                                        r5 = sharedPreferences16;
                                        str5 = "profilesEtag";
                                        string = str34;
                                        r116 = j6;
                                        response = (Response) obj4;
                                        r23 = r5;
                                        r16 = r20;
                                        obj18 = obj11;
                                        r12 = r116;
                                        r19 = r115;
                                        str8 = str14;
                                        str9 = string2;
                                        int i311110 = i12;
                                        i14 = i6;
                                        ApiService apiService116 = apiService2;
                                        z3 = z2;
                                        r17 = r16;
                                        r28 = i13;
                                        int i311111 = i11;
                                        if (response.code() == 304) {
                                            String str211111 = str8;
                                            String str211112 = str;
                                            if (response.isSuccessful()) {
                                            }
                                            r17.element = false;
                                            break;
                                        } else {
                                            ?? Edit112 = r23.edit();
                                            Edit112.putString(str, Instant.now().toString());
                                            Edit112.putLong(str8, r12);
                                            Edit112.apply();
                                        }
                                        r3 = r23;
                                        r9 = r17;
                                        i9 = i30;
                                        if (i9 != 0) {
                                            Edit = r3.edit();
                                            if (r9.element) {
                                                Edit.remove(r19);
                                            } else {
                                                Edit.putLong(r19, r12);
                                            }
                                            Edit.apply();
                                        }
                                        z4 = r9.element;
                                        r125 = obj18;
                                        Boolean boolBoxBoolean10 = Boxing.boxBoolean(z4);
                                        r125.unlock(null);
                                        return boolBoxBoolean10;
                                    case 6:
                                        int i51 = c01341.I$5;
                                        int i52 = c01341.I$4;
                                        int i53 = c01341.I$3;
                                        int i54 = c01341.I$2;
                                        long j7 = c01341.J$0;
                                        i18 = c01341.I$1;
                                        i14 = c01341.I$0;
                                        z3 = c01341.Z$0;
                                        List<ProfileDTO> list11 = (List) c01341.L$9;
                                        List list12 = (List) c01341.L$8;
                                        list = (List) c01341.L$7;
                                        Response response5 = (Response) c01341.L$6;
                                        apiService4 = (ApiService) c01341.L$5;
                                        String str35 = (String) c01341.L$4;
                                        String str36 = (String) c01341.L$3;
                                        Ref.BooleanRef booleanRef10 = (Ref.BooleanRef) c01341.L$2;
                                        Mutex mutex13 = (Mutex) c01341.L$1;
                                        SharedPreferences sharedPreferences17 = (SharedPreferences) c01341.L$0;
                                        try {
                                            ResultKt.throwOnFailure(obj17);
                                            i23 = i51;
                                            response2 = response5;
                                            r124 = SYNC_FAILURE_TIME_KEY;
                                            i21 = i54;
                                            obj10 = obj17;
                                            r114 = booleanRef10;
                                            str10 = "lastSyncTimestamp";
                                            i19 = i53;
                                            obj15 = mutex13;
                                            obj9 = coroutine_suspended;
                                            i22 = i52;
                                            str13 = str35;
                                            list5 = list12;
                                            str5 = "profilesEtag";
                                            string = str36;
                                            str17 = PROFILES_SYNC_TIME_KEY;
                                            list6 = list11;
                                            r113 = j7;
                                            r24 = sharedPreferences17;
                                            if (((Boolean) obj10).booleanValue()) {
                                                i24 = 0;
                                            } else {
                                                i24 = 0;
                                            }
                                            str9 = str13;
                                            i17 = i24;
                                            response = response2;
                                            list2 = list6;
                                            str12 = string;
                                            i20 = i22;
                                            list4 = list;
                                            obj8 = obj9;
                                            r7 = r114;
                                            apiService3 = apiService4;
                                            list3 = list5;
                                            r21 = r24;
                                            obj13 = obj15;
                                            r112 = r113;
                                            r120 = r124;
                                            str15 = str17;
                                            r26 = i21;
                                            r22 = r26;
                                            if (list2.isEmpty()) {
                                                List<ProfileDTO> list13 = list2;
                                                DatabaseHelper databaseHelper4 = DatabaseHelper.INSTANCE;
                                                c01341.L$0 = r21;
                                                c01341.L$1 = obj13;
                                                c01341.L$2 = r7;
                                                c01341.L$3 = str12;
                                                r27 = r21;
                                                c01341.L$4 = SpillingKt.nullOutSpilledVariable(str9);
                                                c01341.L$5 = SpillingKt.nullOutSpilledVariable(apiService3);
                                                c01341.L$6 = response;
                                                c01341.L$7 = SpillingKt.nullOutSpilledVariable(list4);
                                                c01341.L$8 = SpillingKt.nullOutSpilledVariable(list3);
                                                c01341.L$9 = SpillingKt.nullOutSpilledVariable(list13);
                                                c01341.Z$0 = z3;
                                                c01341.I$0 = i14;
                                                c01341.I$1 = i18;
                                                c01341.J$0 = r112;
                                                c01341.I$2 = r22 == true ? 1 : 0;
                                                c01341.I$3 = i19;
                                                c01341.I$4 = i20;
                                                c01341.I$5 = i17;
                                                c01341.label = 7;
                                                objInsertProfiles = databaseHelper4.insertProfiles(list13, c01341);
                                                obj14 = obj8;
                                                if (objInsertProfiles == obj14) {
                                                    return obj14;
                                                }
                                                response3 = response;
                                                obj17 = objInsertProfiles;
                                                i30 = i20;
                                                r122 = r7;
                                                r123 = r112;
                                                r4 = r27;
                                                str16 = str12;
                                                obj16 = obj13;
                                                r126 = r120;
                                                str19 = str15;
                                                if (((Boolean) obj17).booleanValue()) {
                                                    i16 = 0;
                                                } else {
                                                    i16 = 0;
                                                }
                                                response = response3;
                                                string = str16;
                                                r2 = r4;
                                                obj18 = obj16;
                                                r12 = r123;
                                                r121 = r122;
                                                r19 = r126;
                                                str11 = str19;
                                            } else {
                                                i16 = i17;
                                                i30 = i20;
                                                r121 = r7;
                                                string = str12;
                                                r12 = r112;
                                                r2 = r21;
                                            }
                                            if (i16 != 0) {
                                                obj18 = obj18;
                                                r12 = r12;
                                                r121 = r17;
                                                r19 = r19;
                                                obj18 = obj13;
                                                r19 = r120;
                                                str11 = str15;
                                                Edit2 = r2.edit();
                                                Edit2.putString(str10, Instant.now().toString());
                                                if (string == null) {
                                                    Edit2.putString(str5, str18);
                                                }
                                                Edit2.putLong(str11, r12);
                                                Edit2.apply();
                                            } else {
                                                obj18 = obj18;
                                                r12 = r12;
                                                r121 = r17;
                                                r19 = r19;
                                                obj18 = obj13;
                                                r19 = r120;
                                                str11 = str15;
                                                r121.element = false;
                                            }
                                            i9 = i30;
                                            r9 = r121;
                                            r3 = r2;
                                        } catch (CancellationException e3) {
                                            throw e3;
                                        } catch (Throwable th18) {
                                            th = th18;
                                            i = i52;
                                            r117 = SYNC_FAILURE_TIME_KEY;
                                            r119 = j7;
                                            r118 = booleanRef10;
                                            obj12 = mutex13;
                                            r25 = sharedPreferences17;
                                            r1 = r25;
                                            obj18 = obj12;
                                            r12 = r119;
                                            r15 = r118;
                                            r19 = r117;
                                            FileLog.e(th);
                                            r15.element = false;
                                            r3 = r1;
                                            i9 = i;
                                            r9 = r15;
                                            if (i9 != 0) {
                                                Edit = r3.edit();
                                                if (r9.element) {
                                                    Edit.remove(r19);
                                                } else {
                                                    Edit.putLong(r19, r12);
                                                }
                                                Edit.apply();
                                            }
                                            z4 = r9.element;
                                            r125 = obj18;
                                            Boolean boolBoxBoolean11 = Boxing.boxBoolean(z4);
                                            r125.unlock(null);
                                            return boolBoxBoolean11;
                                        }
                                        if (i9 != 0) {
                                            Edit = r3.edit();
                                            if (r9.element) {
                                                Edit.remove(r19);
                                            } else {
                                                Edit.putLong(r19, r12);
                                            }
                                            Edit.apply();
                                        }
                                        z4 = r9.element;
                                        r125 = obj18;
                                        Boolean boolBoxBoolean12 = Boxing.boxBoolean(z4);
                                        r125.unlock(null);
                                        return boolBoxBoolean12;
                                    case 7:
                                        i17 = c01341.I$5;
                                        i30 = c01341.I$4;
                                        long j8 = c01341.J$0;
                                        response3 = (Response) c01341.L$6;
                                        str16 = (String) c01341.L$3;
                                        Ref.BooleanRef booleanRef11 = (Ref.BooleanRef) c01341.L$2;
                                        Mutex mutex14 = (Mutex) c01341.L$1;
                                        SharedPreferences sharedPreferences18 = (SharedPreferences) c01341.L$0;
                                        try {
                                            ResultKt.throwOnFailure(obj17);
                                            str5 = "profilesEtag";
                                            str10 = "lastSyncTimestamp";
                                            str19 = PROFILES_SYNC_TIME_KEY;
                                            r126 = SYNC_FAILURE_TIME_KEY;
                                            r4 = sharedPreferences18;
                                            obj16 = mutex14;
                                            r123 = j8;
                                            r122 = booleanRef11;
                                            if (((Boolean) obj17).booleanValue()) {
                                                i16 = 0;
                                            } else {
                                                i16 = 0;
                                            }
                                            response = response3;
                                            string = str16;
                                            r2 = r4;
                                            obj18 = obj16;
                                            r12 = r123;
                                            r121 = r122;
                                            r19 = r126;
                                            str11 = str19;
                                            if (i16 != 0) {
                                                obj18 = obj18;
                                                r12 = r12;
                                                r121 = r17;
                                                r19 = r19;
                                                obj18 = obj13;
                                                r19 = r120;
                                                str11 = str15;
                                                Edit2 = r2.edit();
                                                Edit2.putString(str10, Instant.now().toString());
                                                if (string == null) {
                                                    Edit2.putString(str5, str18);
                                                }
                                                Edit2.putLong(str11, r12);
                                                Edit2.apply();
                                            } else {
                                                obj18 = obj18;
                                                r12 = r12;
                                                r121 = r17;
                                                r19 = r19;
                                                obj18 = obj13;
                                                r19 = r120;
                                                str11 = str15;
                                                r121.element = false;
                                            }
                                            i9 = i30;
                                            r9 = r121;
                                            r3 = r2;
                                        } catch (Throwable th19) {
                                            th = th19;
                                            i = i30;
                                            sharedPreferences5 = sharedPreferences18;
                                            mutex2 = mutex14;
                                            booleanRef2 = booleanRef11;
                                            r19 = SYNC_FAILURE_TIME_KEY;
                                            r1 = sharedPreferences5;
                                            obj18 = mutex2;
                                            r12 = j8;
                                            r15 = booleanRef2;
                                            break;
                                        }
                                        if (i9 != 0) {
                                            Edit = r3.edit();
                                            if (r9.element) {
                                                Edit.remove(r19);
                                            } else {
                                                Edit.putLong(r19, r12);
                                            }
                                            Edit.apply();
                                        }
                                        z4 = r9.element;
                                        r125 = obj18;
                                        Boolean boolBoxBoolean13 = Boxing.boxBoolean(z4);
                                        r125.unlock(null);
                                        return boolBoxBoolean13;
                                    default:
                                        Segment$$ExternalSyntheticBUOutline1.m("call to 'resume' before 'invoke' with coroutine");
                                        return null;
                                }
                            } catch (Throwable th20) {
                                th = th20;
                                obj2 = str22;
                                r8 = r28;
                            }
                        } catch (Throwable th21) {
                            th = th21;
                            i = i30;
                            r1 = r23;
                            r15 = r28;
                            obj18 = obj18;
                            r12 = r12;
                            r19 = r19;
                        }
                    } catch (CancellationException e4) {
                        throw e4;
                    }
                } catch (Throwable th22) {
                    th = th22;
                    r128.unlock(null);
                    throw th;
                }
            } catch (CancellationException e5) {
                throw e5;
            } catch (Throwable th23) {
                th = th23;
                i = i30;
                booleanRef2 = booleanRef;
                mutex2 = mutex;
                sharedPreferences5 = sharedPreferences4;
            }
        } catch (CancellationException e6) {
            e = e6;
        } catch (Throwable th24) {
            th = th24;
            str21 = "profilesEtag";
            str = "lastSyncTimestamp";
            sharedPreferences3 = sharedPreferences2;
            str2 = PROFILES_SYNC_TIME_KEY;
            obj2 = obj;
            r18 = SYNC_FAILURE_TIME_KEY;
            r8 = obj3;
        }
    }

    public static /* synthetic */ void resetSyncState$default(SharedPreferences sharedPreferences, int i, Object obj) {
        if ((i & 1) != 0) {
            sharedPreferences = ExteraConfig.getPreferences();
        }
        resetSyncState(sharedPreferences);
    }

    @JvmStatic
    public static final void scheduleWorker() {
        try {
            WorkManager.INSTANCE.getInstance(ApplicationLoader.applicationContext).enqueueUniquePeriodicWork("api_sync_work", ExistingPeriodicWorkPolicy.UPDATE, new PeriodicWorkRequest.Builder(SyncWorker.class, 6L, TimeUnit.HOURS).setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true).build()).setBackoffCriteria(BackoffPolicy.LINEAR, FAILURE_BACKOFF, TimeUnit.MILLISECONDS).build());
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }
}
