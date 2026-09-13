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
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineStart;
import kotlin.coroutines.EmptyCoroutineContext;
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
    private static final Mutex syncMutex = MutexKt.Mutex(false);
    private static final long PROFILES_SYNC_INTERVAL = TimeUnit.MINUTES.toMillis(10);
    private static final long BOOSTY_SYNC_INTERVAL = TimeUnit.MINUTES.toMillis(40);
    private static final long FAILURE_BACKOFF = TimeUnit.MINUTES.toMillis(5);

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
        /* synthetic */ Object result;

        public C01341(Continuation continuation) {
            super((Continuation) continuation);
        }

        @Override
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return ApiController.INSTANCE.performSync(null, false, this);
        }
    }

    private final long elapsedSince(long now, long timestamp) {
        return (timestamp <= 0 || timestamp > now) ? Long.MAX_VALUE : now - timestamp;
    }

    public static /* synthetic */ void sync$default(SharedPreferences sharedPreferences, boolean z, int i, Object obj) {
        if ((i & 1) != 0) {
            sharedPreferences = ExteraConfig.getPreferences();
        }
        if ((i & 2) != 0) {
            z = false;
        }
        sync(sharedPreferences, z);
    }

    @JvmStatic
    public static final void init() {
        if (!started.compareAndSet(false, true)) {
            return;
        }
        sync$default(null, false, 3, null);
        scheduleWorker();
        ForegroundDetector.getInstance().addListener(new ForegroundDetector.Listener() {
            @Override
            public void onBecameForeground() {
                ApiController.sync$default(null, false, 3, null);
            }

            @Override
            public void onBecameBackground() {
            }
        });
    }

    @JvmStatic
    @JvmOverloads
    public static final void sync() {
        sync$default(null, false, 3, null);
    }

    @JvmStatic
    @JvmOverloads
    public static final void sync(SharedPreferences prefs) {
        sync$default(prefs, false, 2, null);
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
        BuildersKt.launch(scope, EmptyCoroutineContext.INSTANCE, CoroutineStart.DEFAULT, (Function2) new C01351(prefs, force, null));
    }

    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.api.ApiController$sync$1", f = "ApiController.kt", i = {}, l = {79}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    public static final class C01351 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ boolean $force;
        final /* synthetic */ SharedPreferences $prefs;
        int label;

        public C01351(SharedPreferences sharedPreferences, boolean z, Continuation continuation) {
            super(2, (Continuation) continuation);
            this.$prefs = sharedPreferences;
            this.$force = z;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation) new C01351(this.$prefs, this.$force, (Continuation) continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C01351) (Object) create(coroutineScope, (Continuation) continuation)).invokeSuspend(Unit.INSTANCE);
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
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
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

    public final Object performSync(SharedPreferences sharedPreferences, boolean z, Continuation<? super Boolean> continuation) {
        if (sharedPreferences == null) {
            sharedPreferences = ExteraConfig.getPreferences();
        }
        boolean success = true;
        long now = System.currentTimeMillis();
        boolean boostyDue = z || elapsedSince(now, sharedPreferences.getLong(BOOSTY_SYNC_TIME_KEY, 0L)) >= BOOSTY_SYNC_INTERVAL;
        boolean profilesDue = z || elapsedSince(now, sharedPreferences.getLong(PROFILES_SYNC_TIME_KEY, 0L)) >= PROFILES_SYNC_INTERVAL;
        boolean attempted = boostyDue || profilesDue;

        if (boostyDue) {
            sharedPreferences.edit().putLong(BOOSTY_SYNC_TIME_KEY, now).apply();
        }
        if (profilesDue) {
            sharedPreferences.edit().putLong(PROFILES_SYNC_TIME_KEY, now).apply();
        }
        if (attempted) {
            SharedPreferences.Editor edit = sharedPreferences.edit();
            if (success) {
                edit.remove(SYNC_FAILURE_TIME_KEY);
            } else {
                edit.putLong(SYNC_FAILURE_TIME_KEY, now);
            }
            edit.apply();
        }
        return Boxing.boxBoolean(success);
    }

        @JvmStatic
    @JvmOverloads
    public static final void resetSyncState(SharedPreferences prefs) {
        SharedPreferences.Editor editorEdit = prefs.edit();
        editorEdit.remove("lastSyncTimestamp");
        editorEdit.remove("profilesEtag");
        editorEdit.remove(PROFILES_SYNC_TIME_KEY);
        editorEdit.remove(BOOSTY_SYNC_TIME_KEY);
        editorEdit.remove(SYNC_FAILURE_TIME_KEY);
        editorEdit.apply();
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
            WorkManager.getInstance(ApplicationLoader.applicationContext).enqueueUniquePeriodicWork("api_sync_work", ExistingPeriodicWorkPolicy.UPDATE, new PeriodicWorkRequest.Builder(SyncWorker.class, 6L, TimeUnit.HOURS).setConstraints(new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).setRequiresBatteryNotLow(true).build()).setBackoffCriteria(BackoffPolicy.LINEAR, FAILURE_BACKOFF, TimeUnit.MILLISECONDS).build());
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }
}
