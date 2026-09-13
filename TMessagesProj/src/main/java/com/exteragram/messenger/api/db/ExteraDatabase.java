package com.exteragram.messenger.api.db;

import androidx.room.RoomDatabase;
import com.exteragram.messenger.api.dto.AddedRegDateDTO;
import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.api.dto.BoostySubscriberDTO;
import com.exteragram.messenger.api.dto.NowPlayingInfoDTO;
import com.exteragram.messenger.api.dto.ProfileDTO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b'\u0018\u0000 \n2\u00020\u0001:\u0001\nB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\tH&¨\u0006\u000b"}, d2 = {"Lcom/exteragram/messenger/api/db/ExteraDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "profileDao", "Lcom/exteragram/messenger/api/db/ProfileDao;", "addedRegDateDao", "Lcom/exteragram/messenger/api/db/AddedRegDateDao;", "boostySubscriberDao", "Lcom/exteragram/messenger/api/db/BoostySubscriberDao;", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public abstract class ExteraDatabase extends RoomDatabase {

    private static volatile ExteraDatabase INSTANCE;
    public static final Companion Companion = new Companion(null);

    public static ExteraDatabase getInstance() {
        return Companion.getInstance();
    }

    public abstract AddedRegDateDao addedRegDateDao();

    public abstract BoostySubscriberDao boostySubscriberDao();

    public abstract ProfileDao profileDao();

    public abstract void clearAllTables();

    private static class InMemoryDatabase extends ExteraDatabase {
        private final ProfileDao profileDaoImpl = new InMemoryProfileDao();
        private final AddedRegDateDao addedRegDateDaoImpl = new InMemoryAddedRegDateDao();
        private final BoostySubscriberDao boostySubscriberDaoImpl = new InMemoryBoostySubscriberDao();

        @Override
        public ProfileDao profileDao() {
            return profileDaoImpl;
        }

        @Override
        public AddedRegDateDao addedRegDateDao() {
            return addedRegDateDaoImpl;
        }

        @Override
        public BoostySubscriberDao boostySubscriberDao() {
            return boostySubscriberDaoImpl;
        }

        @Override
        public void clearAllTables() {
            ((InMemoryProfileDao) profileDaoImpl).clear();
            ((InMemoryAddedRegDateDao) addedRegDateDaoImpl).clear();
            ((InMemoryBoostySubscriberDao) boostySubscriberDaoImpl).clear();
        }
    }

    private static class InMemoryProfileDao implements ProfileDao {
        private final ConcurrentHashMap<Long, ProfileDTO> profiles = new ConcurrentHashMap<>();

        public void clear() {
            profiles.clear();
        }

        @Override
        public Object insertAll(List<ProfileDTO> list, Continuation<? super Unit> continuation) {
            if (list != null) {
                for (ProfileDTO p : list) {
                    if (p != null) {
                        profiles.put(p.getId(), p);
                    }
                }
            }
            return Unit.INSTANCE;
        }

        @Override
        public Object getAll(Continuation<? super List<ProfileDTO>> continuation) {
            return new ArrayList<>(profiles.values());
        }

        @Override
        public Object getById(long id, Continuation<? super ProfileDTO> continuation) {
            return profiles.get(id);
        }

        @Override
        public Object deleteProfiles(List<Long> ids, Continuation<? super Integer> continuation) {
            int count = 0;
            if (ids != null) {
                for (Long id : ids) {
                    if (id != null && profiles.remove(id) != null) {
                        count++;
                    }
                }
            }
            return count;
        }

        @Override
        public Object updateBadge(long id, BadgeDTO badgeDTO, Continuation<? super Integer> continuation) {
            ProfileDTO p = profiles.get(id);
            if (p != null) {
                profiles.put(id, ProfileDTO.copy$default(p, 0L, null, null, badgeDTO, null, null, null, 119, null));
                return 1;
            }
            return 0;
        }

        @Override
        public Object updateNowPlaying(long id, NowPlayingInfoDTO nowPlayingInfoDTO, Continuation<? super Integer> continuation) {
            ProfileDTO p = profiles.get(id);
            if (p != null) {
                profiles.put(id, ProfileDTO.copy$default(p, 0L, null, null, null, nowPlayingInfoDTO, null, null, 111, null));
                return 1;
            }
            return 0;
        }
    }

    private static class InMemoryAddedRegDateDao implements AddedRegDateDao {
        private final ConcurrentHashMap<Long, AddedRegDateDTO> set = new ConcurrentHashMap<>();

        public void clear() {
            set.clear();
        }

        @Override
        public Object insert(AddedRegDateDTO addedRegDateDTO, Continuation<? super Unit> continuation) {
            if (addedRegDateDTO != null) {
                set.put(addedRegDateDTO.getUserId(), addedRegDateDTO);
            }
            return Unit.INSTANCE;
        }

        @Override
        public Object isAdded(long j, Continuation<? super Boolean> continuation) {
            return set.containsKey(j);
        }
    }

    private static class InMemoryBoostySubscriberDao implements BoostySubscriberDao {
        private final ConcurrentHashMap<Long, BoostySubscriberDTO> subscribers = new ConcurrentHashMap<>();

        public void clear() {
            subscribers.clear();
        }

        @Override
        public Object insertAll(List<BoostySubscriberDTO> list, Continuation<? super Unit> continuation) {
            if (list != null) {
                for (BoostySubscriberDTO s : list) {
                    if (s != null) {
                        subscribers.put(s.getId(), s);
                    }
                }
            }
            return Unit.INSTANCE;
        }

        @Override
        public Object getAll(Continuation<? super List<BoostySubscriberDTO>> continuation) {
            return new ArrayList<>(subscribers.values());
        }

        @Override
        public Object deleteAll(Continuation<? super Unit> continuation) {
            subscribers.clear();
            return Unit.INSTANCE;
        }
    }

    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0001\u0007B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0006\u001a\u00020\u0005R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\b"}, d2 = {"Lcom/exteragram/messenger/api/db/ExteraDatabase$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "INSTANCE", "Lcom/exteragram/messenger/api/db/ExteraDatabase;", "getInstance", "Callback", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        public final ExteraDatabase getInstance() {
            ExteraDatabase exteraDatabase = ExteraDatabase.INSTANCE;
            if (exteraDatabase != null) {
                return exteraDatabase;
            }
            synchronized (this) {
                exteraDatabase = ExteraDatabase.INSTANCE;
                if (exteraDatabase == null) {
                    exteraDatabase = new InMemoryDatabase();
                    ExteraDatabase.INSTANCE = exteraDatabase;
                }
            }
            return exteraDatabase;
        }
    }
}
