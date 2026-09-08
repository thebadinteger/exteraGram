package com.exteragram.messenger.api.db;

import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteConnection;
import androidx.sqlite.SQLiteStatement;
import com.exteragram.messenger.api.dto.BoostySubscriberDTO;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.DebugMetadata;
import kotlin.coroutines.jvm.internal.SuspendLambda;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.reflect.KClass;
import okhttp3.internal.url._UrlKt;
import okio.Segment$$ExternalSyntheticBUOutline1;

@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0007\u0018\u0000 \u00142\u00020\u0001:\u0001\u0014B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001c\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\b0\u000eH\u0096@¢\u0006\u0002\u0010\u000fJ\u001c\u0010\u0010\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\b0\u000eH\u0096@¢\u0006\u0002\u0010\u000fJ\u0014\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\b0\u000eH\u0096@¢\u0006\u0002\u0010\u0012J\u000e\u0010\u0013\u001a\u00020\fH\u0096@¢\u0006\u0002\u0010\u0012R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0015"}, d2 = {"Lcom/exteragram/messenger/api/db/BoostySubscriberDao_Impl;", "Lcom/exteragram/messenger/api/db/BoostySubscriberDao;", "__db", "Landroidx/room/RoomDatabase;", "<init>", "(Landroidx/room/RoomDatabase;)V", "__insertAdapterOfBoostySubscriberDTO", "Landroidx/room/EntityInsertAdapter;", "Lcom/exteragram/messenger/api/dto/BoostySubscriberDTO;", "__converters", "Lcom/exteragram/messenger/api/db/Converters;", "insertAll", _UrlKt.FRAGMENT_ENCODE_SET, "subscribers", _UrlKt.FRAGMENT_ENCODE_SET, "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "replaceSubscribers", "getAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteAll", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class BoostySubscriberDao_Impl implements BoostySubscriberDao {

    public static final Companion INSTANCE = new Companion(null);
    private final RoomDatabase __db;
    private final Converters __converters = new Converters();
    private final EntityInsertAdapter<BoostySubscriberDTO> __insertAdapterOfBoostySubscriberDTO = new EntityInsertAdapter<BoostySubscriberDTO>() { 
        @Override // androidx.room.EntityInsertAdapter
        public String createQuery() {
            return "INSERT OR REPLACE INTO `BoostySubscriberDTO` (`id`,`name`,`totalAmountRub`,`totalAmountUsd`) VALUES (?,?,?,?)";
        }

        @Override // androidx.room.EntityInsertAdapter
        public void bind(SQLiteStatement statement, BoostySubscriberDTO entity) {
            statement.bindLong(1, entity.getId());
            statement.bindText(2, entity.getName());
            String strFromBigDecimal = BoostySubscriberDao_Impl.this.__converters.fromBigDecimal(entity.getTotalAmountRub());
            if (strFromBigDecimal == null) {
                statement.bindNull(3);
            } else {
                statement.bindText(3, strFromBigDecimal);
            }
            String strFromBigDecimal2 = BoostySubscriberDao_Impl.this.__converters.fromBigDecimal(entity.getTotalAmountUsd());
            if (strFromBigDecimal2 == null) {
                statement.bindNull(4);
            } else {
                statement.bindText(4, strFromBigDecimal2);
            }
        }
    };

    public BoostySubscriberDao_Impl(RoomDatabase roomDatabase) {
        this.__db = roomDatabase;
    }

    @Override 
    public Object insertAll(final List<BoostySubscriberDTO> list, Continuation<? super Unit> continuation) {
        Object objPerformSuspending = DBUtil.performSuspending(this.__db, false, true, new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return BoostySubscriberDao_Impl.m898$r8$lambda$JHrUJzTJ4p8ZR51yxwtYhKmMyE(this.f$0, list, (SQLiteConnection) obj);
            }
        }, continuation);
        return objPerformSuspending == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? objPerformSuspending : Unit.INSTANCE;
    }

    public static Unit m898$r8$lambda$JHrUJzTJ4p8ZR51yxwtYhKmMyE(BoostySubscriberDao_Impl boostySubscriberDao_Impl, List list, SQLiteConnection sQLiteConnection) throws Exception {
        boostySubscriberDao_Impl.__insertAdapterOfBoostySubscriberDTO.insert(sQLiteConnection, list);
        return Unit.INSTANCE;
    }

    @Metadata(d1 = {"\u0000\u0006\n\u0000\n\u0002\u0010\u0002\u0010\u0000\u001a\u00020\u0001H\n"}, d2 = {"<anonymous>", _UrlKt.FRAGMENT_ENCODE_SET}, k = 3, mv = {2, 2, 0}, xi = 48)
    @DebugMetadata(c = "com.exteragram.messenger.api.db.BoostySubscriberDao_Impl$replaceSubscribers$2", f = "BoostySubscriberDao_Impl.kt", i = {}, l = {61}, m = "invokeSuspend", n = {}, s = {}, v = 1)
    public static final class AnonymousClass2 extends SuspendLambda implements Function1<Continuation<? super Unit>, Object> {
        final Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final List<KClass<?>> getRequiredConverters() {
            return CollectionsKt.emptyList();
        }
    }
}
