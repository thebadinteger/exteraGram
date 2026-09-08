package com.exteragram.messenger.api.db;

import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteConnectionUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.SQLiteConnection;
import androidx.sqlite.SQLiteStatement;
import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.api.dto.NowPlayingInfoDTO;
import com.exteragram.messenger.api.dto.ProfileDTO;
import com.exteragram.messenger.api.model.ProfileStatus;
import com.exteragram.messenger.api.model.ProfileType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.reflect.KClass;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;

@Metadata(d1 = {"\u0000d\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u0018\u0000 )2\u00020\u0001:\u0001)B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u001c\u0010\u000b\u001a\u00020\f2\f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\b0\u000eH\u0096@¢\u0006\u0002\u0010\u000fJ\u0014\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\b0\u000eH\u0096@¢\u0006\u0002\u0010\u0011J\u0018\u0010\u0012\u001a\u0004\u0018\u00010\b2\u0006\u0010\u0013\u001a\u00020\u0014H\u0096@¢\u0006\u0002\u0010\u0015J\u001c\u0010\u0016\u001a\u00020\u00172\f\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00140\u000eH\u0096@¢\u0006\u0002\u0010\u000fJ \u0010\u0019\u001a\u00020\u00172\u0006\u0010\u0013\u001a\u00020\u00142\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH\u0096@¢\u0006\u0002\u0010\u001cJ \u0010\u001d\u001a\u00020\u00172\u0006\u0010\u0013\u001a\u00020\u00142\b\u0010\u001e\u001a\u0004\u0018\u00010\u001fH\u0096@¢\u0006\u0002\u0010 J\u0010\u0010!\u001a\u00020\"2\u0006\u0010#\u001a\u00020$H\u0002J\u0010\u0010%\u001a\u00020\"2\u0006\u0010#\u001a\u00020&H\u0002J\u0010\u0010'\u001a\u00020$2\u0006\u0010#\u001a\u00020\"H\u0002J\u0010\u0010(\u001a\u00020&2\u0006\u0010#\u001a\u00020\"H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006*"}, d2 = {"Lcom/exteragram/messenger/api/db/ProfileDao_Impl;", "Lcom/exteragram/messenger/api/db/ProfileDao;", "__db", "Landroidx/room/RoomDatabase;", "<init>", "(Landroidx/room/RoomDatabase;)V", "__insertAdapterOfProfileDTO", "Landroidx/room/EntityInsertAdapter;", "Lcom/exteragram/messenger/api/dto/ProfileDTO;", "__converters", "Lcom/exteragram/messenger/api/db/Converters;", "insertAll", _UrlKt.FRAGMENT_ENCODE_SET, "profiles", _UrlKt.FRAGMENT_ENCODE_SET, "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getById", "id", _UrlKt.FRAGMENT_ENCODE_SET, "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteProfiles", _UrlKt.FRAGMENT_ENCODE_SET, "ids", "updateNowPlaying", "newNowPlaying", "Lcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;", "(JLcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateBadge", "badge", "Lcom/exteragram/messenger/api/dto/BadgeDTO;", "(JLcom/exteragram/messenger/api/dto/BadgeDTO;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "__ProfileType_enumToString", _UrlKt.FRAGMENT_ENCODE_SET, "_value", "Lcom/exteragram/messenger/api/model/ProfileType;", "__ProfileStatus_enumToString", "Lcom/exteragram/messenger/api/model/ProfileStatus;", "__ProfileType_stringToEnum", "__ProfileStatus_stringToEnum", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nProfileDao_Impl.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ProfileDao_Impl.kt\ncom/exteragram/messenger/api/db/ProfileDao_Impl\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,307:1\n1#2:308\n*E\n"})
public final class ProfileDao_Impl implements ProfileDao {

    public static final Companion INSTANCE = new Companion(null);
    private final RoomDatabase __db;
    private final Converters __converters = new Converters();
    private final EntityInsertAdapter<ProfileDTO> __insertAdapterOfProfileDTO = new EntityInsertAdapter<ProfileDTO>() { 
        @Override // androidx.room.EntityInsertAdapter
        public String createQuery() {
            return "INSERT OR REPLACE INTO `ProfileDTO` (`id`,`type`,`status`,`badge`,`nowPlaying`,`deleted`,`canChangeBadge`) VALUES (?,?,?,?,?,?,?)";
        }

        @Override // androidx.room.EntityInsertAdapter
        public void bind(SQLiteStatement statement, ProfileDTO entity) {
            statement.bindLong(1, entity.getId());
            statement.bindText(2, ProfileDao_Impl.this.__ProfileType_enumToString(entity.getType()));
            statement.bindText(3, ProfileDao_Impl.this.__ProfileStatus_enumToString(entity.getStatus()));
            String strFromBadgeDTO = ProfileDao_Impl.this.__converters.fromBadgeDTO(entity.getBadge());
            if (strFromBadgeDTO == null) {
                statement.bindNull(4);
            } else {
                statement.bindText(4, strFromBadgeDTO);
            }
            String strFromNowPlayingInfoDTO = ProfileDao_Impl.this.__converters.fromNowPlayingInfoDTO(entity.getNowPlaying());
            if (strFromNowPlayingInfoDTO == null) {
                statement.bindNull(5);
            } else {
                statement.bindText(5, strFromNowPlayingInfoDTO);
            }
            Boolean deleted = entity.getDeleted();
            Integer numValueOf = deleted != null ? Integer.valueOf(deleted.booleanValue() ? 1 : 0) : null;
            if (numValueOf == null) {
                statement.bindNull(6);
            } else {
                statement.bindLong(6, numValueOf.intValue());
            }
            Boolean canChangeBadge = entity.getCanChangeBadge();
            Integer numValueOf2 = canChangeBadge != null ? Integer.valueOf(canChangeBadge.booleanValue() ? 1 : 0) : null;
            if (numValueOf2 == null) {
                statement.bindNull(7);
            } else {
                statement.bindLong(7, numValueOf2.intValue());
            }
        }
    };

    @Metadata(k = 3, mv = {2, 2, 0}, xi = 48)
    public static final Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final List<KClass<?>> getRequiredConverters() {
            return CollectionsKt.emptyList();
        }
    }
}
