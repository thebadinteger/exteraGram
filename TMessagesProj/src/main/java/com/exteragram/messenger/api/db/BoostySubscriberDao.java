package com.exteragram.messenger.api.db;

import com.exteragram.messenger.api.dto.BoostySubscriberDTO;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0006\bg\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H§@¢\u0006\u0002\u0010\u0007J\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H§@¢\u0006\u0002\u0010\tJ\u000e\u0010\n\u001a\u00020\u0003H§@¢\u0006\u0002\u0010\tJ\u001c\u0010\u000b\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H\u0097@¢\u0006\u0002\u0010\u0007¨\u0006\fÀ\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/api/db/BoostySubscriberDao;", _UrlKt.FRAGMENT_ENCODE_SET, "insertAll", _UrlKt.FRAGMENT_ENCODE_SET, "subscribers", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/api/dto/BoostySubscriberDTO;", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAll", "replaceSubscribers", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public interface BoostySubscriberDao {

    /* JADX INFO: renamed from: com.exteragram.messenger.api.db.BoostySubscriberDao$replaceSubscribers$1, reason: invalid class name */
    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.api.db.BoostySubscriberDao", f = "BoostySubscriberDao.kt", i = {0, 0, 1, 1}, l = {34, 35}, m = "replaceSubscribers$suspendImpl", n = {"$this", "subscribers", "$this", "subscribers"}, s = {"L$0", "L$1", "L$0", "L$1"}, v = 1)
    public static final class AnonymousClass1 extends ContinuationImpl {
        Object L$0;
        Object L$1;
        int label;
        /* synthetic */ Object result;

        public AnonymousClass1(Continuation continuation) {
            super((Continuation) continuation);
        }

        @Override // kotlin.coroutines.jvm.internal.BaseContinuationImpl
        public final Object invokeSuspend(Object obj) {
            this.result = obj;
            this.label |= Integer.MIN_VALUE;
            return BoostySubscriberDao.replaceSubscribers$suspendImpl(null, null, this);
        }
    }

    Object deleteAll(Continuation<? super Unit> continuation);

    Object getAll(Continuation<? super List<BoostySubscriberDTO>> continuation);

    Object insertAll(List<BoostySubscriberDTO> list, Continuation<? super Unit> continuation);

    default Object replaceSubscribers(List<BoostySubscriberDTO> list, Continuation<? super Unit> continuation) {
        return replaceSubscribers$suspendImpl(this, list, continuation);
    }

    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    public static final class DefaultImpls {
        @Deprecated
        public static Object replaceSubscribers(BoostySubscriberDao boostySubscriberDao, List<BoostySubscriberDTO> list, Continuation<? super Unit> continuation) {
            return boostySubscriberDao.replaceSubscribers(list, continuation);
        }
    }

    /* JADX WARN: Code duplicated, block: B:7:0x0013  */
    /* JADX WARN: Code restructure failed: missing block: B:20:0x006a, code lost:
    
        if (r5.insertAll(r6, r0) == r1) goto L21;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    static /* synthetic */ Object replaceSubscribers$suspendImpl(BoostySubscriberDao boostySubscriberDao, List<BoostySubscriberDTO> list, Continuation<? super Unit> continuation) {
        AnonymousClass1 continuationImpl;
        if (continuation instanceof AnonymousClass1) {
            continuationImpl = (AnonymousClass1) continuation;
            int i = continuationImpl.label;
            if ((i & Integer.MIN_VALUE) != 0) {
                continuationImpl.label = i - Integer.MIN_VALUE;
            } else {
                continuationImpl = new AnonymousClass1(continuation);
            }
        } else {
            continuationImpl = new AnonymousClass1(continuation);
        }
        Object obj = continuationImpl.result;
        Object coroutine_suspended = kotlin.coroutines.intrinsics.IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i2 = continuationImpl.label;
        if (i2 == 0) {
            ResultKt.throwOnFailure(obj);
            continuationImpl.L$0 = boostySubscriberDao;
            continuationImpl.L$1 = list;
            continuationImpl.label = 1;
            if (boostySubscriberDao.deleteAll(continuationImpl) == coroutine_suspended) {
                return coroutine_suspended;
            }
        } else if (i2 == 1) {
            list = (List) continuationImpl.L$1;
            boostySubscriberDao = (BoostySubscriberDao) continuationImpl.L$0;
            ResultKt.throwOnFailure(obj);
        } else if (i2 == 2) {
            ResultKt.throwOnFailure(obj);
            return Unit.INSTANCE;
        } else {
            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
        }
        continuationImpl.L$0 = null;
        continuationImpl.L$1 = null;
        continuationImpl.label = 2;
        if (boostySubscriberDao.insertAll(list, continuationImpl) == coroutine_suspended) {
            return coroutine_suspended;
        }
        return Unit.INSTANCE;
    }
}
