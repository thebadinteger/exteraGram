package com.exteragram.messenger.badges.source;

import com.exteragram.messenger.api.db.ProfileDao;
import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.api.dto.ProfileDTO;
import com.exteragram.messenger.api.model.ProfileStatus;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.url._UrlKt;
import okio.Segment$$ExternalSyntheticBUOutline1;

@Metadata(d1 = {"\u00008\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0003\u001a\u00020\u0002¢\u0006\u0004\b\u0004\u0010\u0005J!\u0010\u000b\u001a\u0004\u0018\u00010\n2\u0006\u0010\u0007\u001a\u00020\u00062\u0006\u0010\t\u001a\u00020\bH\u0016¢\u0006\u0004\b\u000b\u0010\fJ\u0015\u0010\r\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006¢\u0006\u0004\b\r\u0010\u000eJ\u0015\u0010\u000f\u001a\u00020\b2\u0006\u0010\u0007\u001a\u00020\u0006¢\u0006\u0004\b\u000f\u0010\u000eJ\"\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u0007\u001a\u00020\u00062\b\u0010\u0010\u001a\u0004\u0018\u00010\nH\u0086@¢\u0006\u0004\b\u0012\u0010\u0013J\u0010\u0010\u0014\u001a\u00020\u0011H\u0086@¢\u0006\u0004\b\u0014\u0010\u0015R\u0014\u0010\u0003\u001a\u00020\u00028\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0003\u0010\u0016R \u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00180\u00178\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u0019\u0010\u001a¨\u0006\u001b"}, d2 = {"Lcom/exteragram/messenger/badges/source/ApiBadgeSource;", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/api/db/ProfileDao;", "profileDao", "<init>", "(Lcom/exteragram/messenger/api/db/ProfileDao;)V", _UrlKt.FRAGMENT_ENCODE_SET, "id", _UrlKt.FRAGMENT_ENCODE_SET, "isUser", "Lcom/exteragram/messenger/api/dto/BadgeDTO;", "getBadge", "(JZ)Lcom/exteragram/messenger/api/dto/BadgeDTO;", "isDeveloper", "(J)Z", "canChangeBadge", "badge", _UrlKt.FRAGMENT_ENCODE_SET, "updateLocalBadge", "(JLcom/exteragram/messenger/api/dto/BadgeDTO;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "loadToCache", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Lcom/exteragram/messenger/api/db/ProfileDao;", "Ljava/util/concurrent/ConcurrentHashMap;", "Lcom/exteragram/messenger/badges/source/BadgeInfo;", "cache", "Ljava/util/concurrent/ConcurrentHashMap;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class ApiBadgeSource {
    private final ConcurrentHashMap<Long, BadgeInfo> cache = new ConcurrentHashMap<>();
    private final ProfileDao profileDao;

    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.badges.source.ApiBadgeSource", f = "ApiBadgeSource.kt", i = {}, l = {38}, m = "loadToCache", n = {}, s = {}, v = 1)
    public static final class AnonymousClass1 extends ContinuationImpl {
        int label;
        public final Object loadToCache(Continuation<? super Unit> continuation) {
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
        Object all = anonymousClass1.result;
        Object coroutine_suspended = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        int i2 = anonymousClass1.label;
        if (i2 == 0) {
            ResultKt.throwOnFailure(all);
            ProfileDao profileDao = this.profileDao;
            anonymousClass1.label = 1;
            all = profileDao.getAll(anonymousClass1);
            if (all == coroutine_suspended) {
                return coroutine_suspended;
            }
        } else {
            if (i2 != 1) {
                Segment$$ExternalSyntheticBUOutline1.m("call to 'resume' before 'invoke' with coroutine");
                return null;
            }
            ResultKt.throwOnFailure(all);
        }
        for (ProfileDTO profileDTO : (List) all) {
            this.cache.put(Boxing.boxLong(profileDTO.getId()), new BadgeInfo(profileDTO.getBadge(), profileDTO.getStatus(), Intrinsics.areEqual(profileDTO.getCanChangeBadge(), Boxing.boxBoolean(true))));
        }
        return Unit.INSTANCE;
    }
}
