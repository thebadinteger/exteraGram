package com.exteragram.messenger.api.db;

import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.sqlite.SQLiteConnection;
import androidx.sqlite.SQLiteStatement;
import com.exteragram.messenger.api.dto.AddedRegDateDTO;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.reflect.KClass;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u00002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u0003\u0018\u0000 \u00122\u00020\u0001:\u0001\u0012B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\bH\u0096@¢\u0006\u0002\u0010\fJ\u0016\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0010H\u0096@¢\u0006\u0002\u0010\u0011R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"}, d2 = {"Lcom/exteragram/messenger/api/db/AddedRegDateDao_Impl;", "Lcom/exteragram/messenger/api/db/AddedRegDateDao;", "__db", "Landroidx/room/RoomDatabase;", "<init>", "(Landroidx/room/RoomDatabase;)V", "__insertAdapterOfAddedRegDateDTO", "Landroidx/room/EntityInsertAdapter;", "Lcom/exteragram/messenger/api/dto/AddedRegDateDTO;", "insert", _UrlKt.FRAGMENT_ENCODE_SET, "addedRegDate", "(Lcom/exteragram/messenger/api/dto/AddedRegDateDTO;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "isAdded", _UrlKt.FRAGMENT_ENCODE_SET, "userId", _UrlKt.FRAGMENT_ENCODE_SET, "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class AddedRegDateDao_Impl implements AddedRegDateDao {

    public static final Companion INSTANCE = new Companion(null);
    private final RoomDatabase __db;
    private final EntityInsertAdapter<AddedRegDateDTO> __insertAdapterOfAddedRegDateDTO = new EntityInsertAdapter<AddedRegDateDTO>() { 
        @Override // androidx.room.EntityInsertAdapter
        public String createQuery() {
            return "INSERT OR IGNORE INTO `AddedRegDateDTO` (`userId`) VALUES (?)";
        }

        @Override // androidx.room.EntityInsertAdapter
        public void bind(SQLiteStatement statement, AddedRegDateDTO entity) {
            statement.bindLong(1, entity.getUserId());
        }
    };

    public AddedRegDateDao_Impl(RoomDatabase roomDatabase) {
        this.__db = roomDatabase;
    }

    @Override 
    public Object insert(final AddedRegDateDTO addedRegDateDTO, Continuation<? super Unit> continuation) {
        Object objPerformSuspending = DBUtil.performSuspending(this.__db, false, true, new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return AddedRegDateDao_Impl.$r8$lambda$Io97oZNfeFEOzi2ZwPsP1ngH8Do(this.f$0, addedRegDateDTO, (SQLiteConnection) obj);
            }
        }, continuation);
        return objPerformSuspending == IntrinsicsKt.getCOROUTINE_SUSPENDED() ? objPerformSuspending : Unit.INSTANCE;
    }

    public static Unit $r8$lambda$Io97oZNfeFEOzi2ZwPsP1ngH8Do(AddedRegDateDao_Impl addedRegDateDao_Impl, AddedRegDateDTO addedRegDateDTO, SQLiteConnection sQLiteConnection) throws Exception {
        addedRegDateDao_Impl.__insertAdapterOfAddedRegDateDTO.insert(sQLiteConnection, addedRegDateDTO);
        return Unit.INSTANCE;
    }

    @Override 
    public Object isAdded(final long j, Continuation<? super Boolean> continuation) {
        final String str = "SELECT EXISTS(SELECT 1 FROM AddedRegDateDTO WHERE userId = ?)";
        return DBUtil.performSuspending(this.__db, true, false, new Function1() { 
            @Override // kotlin.jvm.functions.Function1
            public final Object invoke(Object obj) {
                return Boolean.valueOf(AddedRegDateDao_Impl.$r8$lambda$j4Ijwm3SdR5LlAnhMeRPagGh3gg(str, j, (SQLiteConnection) obj));
            }
        }, continuation);
    }

    public static boolean $r8$lambda$j4Ijwm3SdR5LlAnhMeRPagGh3gg(String str, long j, SQLiteConnection sQLiteConnection) {
        SQLiteStatement sQLiteStatementPrepare = sQLiteConnection.prepare(str);
        try {
            sQLiteStatementPrepare.bindLong(1, j);
            boolean z = false;
            if (sQLiteStatementPrepare.step()) {
                z = ((int) sQLiteStatementPrepare.getLong(0)) != 0;
            }
            return z;
        } finally {
            sQLiteStatementPrepare.close();
        }
    }

    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00060\u0005¨\u0006\u0007"}, d2 = {"Lcom/exteragram/messenger/api/db/AddedRegDateDao_Impl$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "getRequiredConverters", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlin/reflect/KClass;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final List<KClass<?>> getRequiredConverters() {
            return CollectionsKt.emptyList();
        }
    }
}
