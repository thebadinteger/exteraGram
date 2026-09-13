package com.exteragram.messenger.api.db;

import com.exteragram.messenger.api.dto.AddedRegDateDTO;
import com.exteragram.messenger.api.dto.BoostySubscriberDTO;
import com.exteragram.messenger.api.dto.NowPlayingInfoDTO;
import com.exteragram.messenger.api.dto.ProfileDTO;
import com.sun.jna.Callback;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.function.Consumer;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SpillingKt;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineStart;
import kotlin.coroutines.EmptyCoroutineContext;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.FileLog;

@Metadata(d1 = {"\u0000N\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\bÆ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u001c\u0010\u0006\u001a\u00020\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\n0\tH\u0086@¢\u0006\u0002\u0010\u000bJ\u001c\u0010\f\u001a\u00020\u00072\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000e0\tH\u0086@¢\u0006\u0002\u0010\u000bJ \u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000e2\u000e\u0010\u0012\u001a\n\u0012\u0006\u0012\u0004\u0018\u00010\u00140\u0013H\u0007J(\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000e2\b\u0010\u0016\u001a\u0004\u0018\u00010\u00142\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00170\u0013H\u0007J\u001e\u0010\u0018\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\u000e2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00070\u0013H\u0007J\u0010\u0010\u001a\u001a\u00020\u00102\u0006\u0010\u0019\u001a\u00020\u000eH\u0007J\u001c\u0010\u001b\u001a\u00020\u00072\f\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u001d0\tH\u0086@¢\u0006\u0002\u0010\u000bJ\u001c\u0010\u001e\u001a\u00020\u00102\u0012\u0010\u0012\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u001d0\t0\u0013H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001f"}, d2 = {"Lcom/exteragram/messenger/api/db/DatabaseHelper;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "scope", "Lkotlinx/coroutines/CoroutineScope;", "insertProfiles", _UrlKt.FRAGMENT_ENCODE_SET, "profiles", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/api/dto/ProfileDTO;", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteProfiles", "ids", _UrlKt.FRAGMENT_ENCODE_SET, "getNowPlaying", _UrlKt.FRAGMENT_ENCODE_SET, "id", Callback.METHOD_NAME, "Ljava/util/function/Consumer;", "Lcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;", "updateNowPlaying", "newNowPlaying", _UrlKt.FRAGMENT_ENCODE_SET, "isRegDateAdded", "userId", "setRegDateAdded", "insertBoostySubscribers", "subscribers", "Lcom/exteragram/messenger/api/dto/BoostySubscriberDTO;", "getBoostySubscribers", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class DatabaseHelper {
    public static final DatabaseHelper INSTANCE = new DatabaseHelper();
    private static final CoroutineScope scope = CoroutineScopeKt.CoroutineScope(Dispatchers.getIO());

    /* JADX INFO: renamed from: com.exteragram.messenger.api.db.DatabaseHelper$deleteProfiles$1, reason: invalid class name */
    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.api.db.DatabaseHelper", f = "DatabaseHelper.kt", i = {0}, l = {42}, m = "deleteProfiles", n = {"ids"}, s = {"L$0"}, v = 1)
    public static final class AnonymousClass1 extends ContinuationImpl {
        Object L$0;
        int label;
        /* synthetic */ Object result;

        public AnonymousClass1(Continuation continuation) {
            super((Continuation) continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return DatabaseHelper.INSTANCE.deleteProfiles(null, this);
        }
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.api.db.DatabaseHelper$insertBoostySubscribers$1, reason: invalid class name and case insensitive filesystem */
    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.api.db.DatabaseHelper", f = "DatabaseHelper.kt", i = {0}, l = {113}, m = "insertBoostySubscribers", n = {"subscribers"}, s = {"L$0"}, v = 1)
    public static final class C01381 extends ContinuationImpl {
        Object L$0;
        int label;
        /* synthetic */ Object result;

        public C01381(Continuation continuation) {
            super((Continuation) continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return DatabaseHelper.INSTANCE.insertBoostySubscribers(null, this);
        }
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.api.db.DatabaseHelper$insertProfiles$1, reason: invalid class name and case insensitive filesystem */
    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.api.db.DatabaseHelper", f = "DatabaseHelper.kt", i = {0}, l = {30}, m = "insertProfiles", n = {"profiles"}, s = {"L$0"}, v = 1)
    public static final class C01391 extends ContinuationImpl {
        Object L$0;
        int label;
        /* synthetic */ Object result;

        public C01391(Continuation continuation) {
            super((Continuation) continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return DatabaseHelper.INSTANCE.insertProfiles(null, this);
        }
    }

    private DatabaseHelper() {
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    public final Object insertProfiles(List<ProfileDTO> list, Continuation<? super Boolean> continuation) {
        C01391 c01391;
        if (continuation instanceof C01391) {
            c01391 = (C01391) continuation;
            int i = c01391.label;
            if ((i & Integer.MIN_VALUE) != 0) {
                c01391.label = i - Integer.MIN_VALUE;
            } else {
                c01391 = new C01391(continuation);
            }
        } else {
            c01391 = new C01391(continuation);
        }
        Object obj = c01391.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i2 = c01391.label;
        boolean z = true;
        try {
            if (i2 == 0) {
                ResultKt.throwOnFailure(obj);
                ProfileDao profileDao = ExteraDatabase.getInstance().profileDao();
                c01391.L$0 = SpillingKt.nullOutSpilledVariable(list);
                c01391.label = 1;
                if (profileDao.insertAll(list, c01391) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i2 != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
        } catch (CancellationException e) {
            throw e;
        } catch (Throwable th) {
            FileLog.e(th);
            z = false;
        }
        return Boxing.boxBoolean(z);
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    public final Object deleteProfiles(List<Long> list, Continuation<? super Boolean> continuation) {
        AnonymousClass1 anonymousClass1;
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
        boolean z = true;
        try {
            if (i2 == 0) {
                ResultKt.throwOnFailure(obj);
                ProfileDao profileDao = ExteraDatabase.getInstance().profileDao();
                anonymousClass1.L$0 = SpillingKt.nullOutSpilledVariable(list);
                anonymousClass1.label = 1;
                if (profileDao.deleteProfiles(list, anonymousClass1) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i2 != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
        } catch (CancellationException e) {
            throw e;
        } catch (Throwable th) {
            FileLog.e(th);
            z = false;
        }
        return Boxing.boxBoolean(z);
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.api.db.DatabaseHelper$getNowPlaying$1, reason: invalid class name and case insensitive filesystem */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.api.db.DatabaseHelper$getNowPlaying$1", f = "DatabaseHelper.kt", i = {}, l = {56}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    public static final class C01371 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ Consumer<NowPlayingInfoDTO> $callback;
        final /* synthetic */ long $id;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C01371(long j, Consumer<NowPlayingInfoDTO> consumer, Continuation continuation) {
            super(2, continuation);
            this.$id = j;
            this.$callback = consumer;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation) new C01371(this.$id, this.$callback, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C01371) (Object) create(coroutineScope, (Continuation) continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            try {
                if (i == 0) {
                    ResultKt.throwOnFailure(obj);
                    ProfileDao profileDao = ExteraDatabase.getInstance().profileDao();
                    long j = this.$id;
                    this.label = 1;
                    obj = profileDao.getById(j, this);
                    if (obj == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                } else {
                    if (i != 1) {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                    ResultKt.throwOnFailure(obj);
                }
                ProfileDTO profileDTO = (ProfileDTO) obj;
                this.$callback.accept(profileDTO != null ? profileDTO.getNowPlaying() : null);
            } catch (CancellationException e) {
                throw e;
            } catch (Throwable th) {
                FileLog.e(th);
                this.$callback.accept(null);
            }
            return Unit.INSTANCE;
        }
    }

    @JvmStatic
    public static final void getNowPlaying(long id, Consumer<NowPlayingInfoDTO> callback) {
        BuildersKt.launch(scope, EmptyCoroutineContext.INSTANCE, CoroutineStart.DEFAULT, (Function2) new C01371(id, callback, null));
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.api.db.DatabaseHelper$updateNowPlaying$1, reason: invalid class name and case insensitive filesystem */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.api.db.DatabaseHelper$updateNowPlaying$1", f = "DatabaseHelper.kt", i = {}, l = {72}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    public static final class C01421 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ Consumer<Integer> $callback;
        final /* synthetic */ long $id;
        final /* synthetic */ NowPlayingInfoDTO $newNowPlaying;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C01421(long j, NowPlayingInfoDTO nowPlayingInfoDTO, Consumer<Integer> consumer, Continuation continuation) {
            super(2, continuation);
            this.$id = j;
            this.$newNowPlaying = nowPlayingInfoDTO;
            this.$callback = consumer;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation) new C01421(this.$id, this.$newNowPlaying, this.$callback, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C01421) (Object) create(coroutineScope, (Continuation) continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            try {
                if (i == 0) {
                    ResultKt.throwOnFailure(obj);
                    ProfileDao profileDao = ExteraDatabase.getInstance().profileDao();
                    long j = this.$id;
                    NowPlayingInfoDTO nowPlayingInfoDTO = this.$newNowPlaying;
                    this.label = 1;
                    obj = profileDao.updateNowPlaying(j, nowPlayingInfoDTO, this);
                    if (obj == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                } else {
                    if (i != 1) {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                    ResultKt.throwOnFailure(obj);
                }
                this.$callback.accept(Boxing.boxInt(((Number) obj).intValue()));
            } catch (CancellationException e) {
                throw e;
            } catch (Throwable th) {
                FileLog.e(th);
                this.$callback.accept(Boxing.boxInt(0));
            }
            return Unit.INSTANCE;
        }
    }

    @JvmStatic
    public static final void updateNowPlaying(long id, NowPlayingInfoDTO newNowPlaying, Consumer<Integer> callback) {
        BuildersKt.launch(scope, EmptyCoroutineContext.INSTANCE, CoroutineStart.DEFAULT, (Function2) new C01421(id, newNowPlaying, callback, null));
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.api.db.DatabaseHelper$isRegDateAdded$1, reason: invalid class name and case insensitive filesystem */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.api.db.DatabaseHelper$isRegDateAdded$1", f = "DatabaseHelper.kt", i = {}, l = {87}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    public static final class C01401 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ Consumer<Boolean> $callback;
        final /* synthetic */ long $userId;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C01401(long j, Consumer<Boolean> consumer, Continuation continuation) {
            super(2, continuation);
            this.$userId = j;
            this.$callback = consumer;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation) new C01401(this.$userId, this.$callback, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C01401) (Object) create(coroutineScope, (Continuation) continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            try {
                if (i == 0) {
                    ResultKt.throwOnFailure(obj);
                    AddedRegDateDao addedRegDateDao = ExteraDatabase.getInstance().addedRegDateDao();
                    long j = this.$userId;
                    this.label = 1;
                    obj = addedRegDateDao.isAdded(j, this);
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
            } catch (CancellationException e) {
                throw e;
            } catch (Throwable th) {
                FileLog.e(th);
                this.$callback.accept(Boxing.boxBoolean(false));
            }
            return Unit.INSTANCE;
        }
    }

    @JvmStatic
    public static final void isRegDateAdded(long userId, Consumer<Boolean> callback) {
        BuildersKt.launch(scope, EmptyCoroutineContext.INSTANCE, CoroutineStart.DEFAULT, (Function2) new C01401(userId, callback, null));
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.api.db.DatabaseHelper$setRegDateAdded$1, reason: invalid class name and case insensitive filesystem */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.api.db.DatabaseHelper$setRegDateAdded$1", f = "DatabaseHelper.kt", i = {}, l = {102}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    public static final class C01411 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ long $userId;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C01411(long j, Continuation continuation) {
            super(2, continuation);
            this.$userId = j;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation) new C01411(this.$userId, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C01411) (Object) create(coroutineScope, (Continuation) continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            try {
                if (i == 0) {
                    ResultKt.throwOnFailure(obj);
                    AddedRegDateDao addedRegDateDao = ExteraDatabase.getInstance().addedRegDateDao();
                    AddedRegDateDTO addedRegDateDTO = new AddedRegDateDTO(this.$userId);
                    this.label = 1;
                    if (addedRegDateDao.insert(addedRegDateDTO, this) == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                } else {
                    if (i != 1) {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                    ResultKt.throwOnFailure(obj);
                }
            } catch (CancellationException e) {
                throw e;
            } catch (Throwable th) {
                FileLog.e(th);
            }
            return Unit.INSTANCE;
        }
    }

    @JvmStatic
    public static final void setRegDateAdded(long userId) {
        BuildersKt.launch(scope, EmptyCoroutineContext.INSTANCE, CoroutineStart.DEFAULT, (Function2) new C01411(userId, null));
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    public final Object insertBoostySubscribers(List<BoostySubscriberDTO> list, Continuation<? super Boolean> continuation) {
        C01381 c01381;
        if (continuation instanceof C01381) {
            c01381 = (C01381) continuation;
            int i = c01381.label;
            if ((i & Integer.MIN_VALUE) != 0) {
                c01381.label = i - Integer.MIN_VALUE;
            } else {
                c01381 = new C01381(continuation);
            }
        } else {
            c01381 = new C01381(continuation);
        }
        Object obj = c01381.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i2 = c01381.label;
        boolean z = true;
        try {
            if (i2 == 0) {
                ResultKt.throwOnFailure(obj);
                BoostySubscriberDao boostySubscriberDao = ExteraDatabase.getInstance().boostySubscriberDao();
                c01381.L$0 = SpillingKt.nullOutSpilledVariable(list);
                c01381.label = 1;
                if (boostySubscriberDao.replaceSubscribers(list, c01381) == coroutine_suspended) {
                    return coroutine_suspended;
                }
            } else {
                if (i2 != 1) {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                ResultKt.throwOnFailure(obj);
            }
        } catch (CancellationException e) {
            throw e;
        } catch (Throwable th) {
            FileLog.e(th);
            z = false;
        }
        return Boxing.boxBoolean(z);
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.api.db.DatabaseHelper$getBoostySubscribers$1, reason: invalid class name and case insensitive filesystem */
    @Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0010\u0002\n\u0002\u0018\u0002\u0010\u0000\u001a\u00020\u0001*\u00020\u0002H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlinx/coroutines/CoroutineScope;"}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.api.db.DatabaseHelper$getBoostySubscribers$1", f = "DatabaseHelper.kt", i = {}, l = {127}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    @SourceDebugExtension({"SMAP\nDatabaseHelper.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DatabaseHelper.kt\ncom/exteragram/messenger/api/db/DatabaseHelper$getBoostySubscribers$1\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,139:1\n1080#2:140\n*S KotlinDebug\n*F\n+ 1 DatabaseHelper.kt\ncom/exteragram/messenger/api/db/DatabaseHelper$getBoostySubscribers$1\n*L\n128#1:140\n*E\n"})
    public static final class C01361 extends SuspendLambda implements Function2<CoroutineScope, Continuation<? super Unit>, Object> {
        final /* synthetic */ Consumer<List<BoostySubscriberDTO>> $callback;
        int label;

        /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
        public C01361(Consumer<List<BoostySubscriberDTO>> consumer, Continuation continuation) {
            super(2, continuation);
            this.$callback = consumer;
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Continuation<Unit> create(Object obj, Continuation<?> continuation) {
            return (Continuation) new C01361(this.$callback, continuation);
        }

        @Override // kotlin.jvm.functions.Function2
        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
            return ((C01361) (Object) create(coroutineScope, (Continuation) continuation)).invokeSuspend(Unit.INSTANCE);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            int i = this.label;
            try {
                if (i == 0) {
                    ResultKt.throwOnFailure(obj);
                    BoostySubscriberDao boostySubscriberDao = ExteraDatabase.getInstance().boostySubscriberDao();
                    this.label = 1;
                    obj = boostySubscriberDao.getAll(this);
                    if (obj == coroutine_suspended) {
                        return coroutine_suspended;
                    }
                } else {
                    if (i != 1) {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                    ResultKt.throwOnFailure(obj);
                }
                this.$callback.accept(CollectionsKt.sortedWith((List) obj, new Comparator() { // from class: com.exteragram.messenger.api.db.DatabaseHelper$getBoostySubscribers$1$invokeSuspend$$inlined$sortedByDescending$1
                    /* JADX WARN: Multi-variable type inference failed */
                    @Override // java.util.Comparator
                    public final int compare(Object obj1, Object obj2) {
                        BoostySubscriberDTO t = (BoostySubscriberDTO) obj1;
                        BoostySubscriberDTO t2 = (BoostySubscriberDTO) obj2;
                        return ComparisonsKt.compareValues(((BoostySubscriberDTO) t2).getTotalAmountRub(), ((BoostySubscriberDTO) t).getTotalAmountRub());
                    }
                }));
            } catch (CancellationException e) {
                throw e;
            } catch (Throwable th) {
                FileLog.e(th);
                this.$callback.accept(CollectionsKt.emptyList());
            }
            return Unit.INSTANCE;
        }
    }

    @JvmStatic
    public static final void getBoostySubscribers(Consumer<List<BoostySubscriberDTO>> callback) {
        BuildersKt.launch(scope, EmptyCoroutineContext.INSTANCE, CoroutineStart.DEFAULT, (Function2) new C01361(callback, null));
    }
}
