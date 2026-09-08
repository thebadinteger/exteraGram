package com.exteragram.messenger.api.db;

import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.exteragram.messenger.api.ApiController;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.ApplicationLoader;

@Metadata(d1 = {"\u0000 \n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b'\u0018\u0000 \n2\u00020\u0001:\u0001\nB\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0004\u001a\u00020\u0005H&J\b\u0010\u0006\u001a\u00020\u0007H&J\b\u0010\b\u001a\u00020\tH&¨\u0006\u000b"}, d2 = {"Lcom/exteragram/messenger/api/db/ExteraDatabase;", "Landroidx/room/RoomDatabase;", "<init>", "()V", "profileDao", "Lcom/exteragram/messenger/api/db/ProfileDao;", "addedRegDateDao", "Lcom/exteragram/messenger/api/db/AddedRegDateDao;", "boostySubscriberDao", "Lcom/exteragram/messenger/api/db/BoostySubscriberDao;", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public abstract class ExteraDatabase extends RoomDatabase {

    public static final Companion INSTANCE = new Companion(null);
    private static volatile ExteraDatabase INSTANCE;

    public abstract AddedRegDateDao addedRegDateDao();

    public abstract BoostySubscriberDao boostySubscriberDao();

    public abstract ProfileDao profileDao();

    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0086\u0003\u0018\u00002\u00020\u0001:\u0001\u0007B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u0006\u001a\u00020\u0005R\u0010\u0010\u0004\u001a\u0004\u0018\u00010\u0005X\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\b"}, d2 = {"Lcom/exteragram/messenger/api/db/ExteraDatabase$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "INSTANCE", "Lcom/exteragram/messenger/api/db/ExteraDatabase;", "getInstance", "Callback", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0016¨\u0006\b"}, d2 = {"Lcom/exteragram/messenger/api/db/ExteraDatabase$Companion$Callback;", "Landroidx/room/RoomDatabase$Callback;", "<init>", "()V", "onDestructiveMigration", _UrlKt.FRAGMENT_ENCODE_SET, "db", "Landroidx/sqlite/db/SupportSQLiteDatabase;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
        public static final class Callback extends RoomDatabase.Callback {
            @Override // androidx.room.RoomDatabase.Callback
            public void onDestructiveMigration(SupportSQLiteDatabase db) {
                super.onDestructiveMigration(db);
                ApiController.resetSyncState$default(null, 1, null);
            }
        }

        public final ExteraDatabase getInstance() {
            ExteraDatabase exteraDatabase;
            ExteraDatabase exteraDatabase2 = ExteraDatabase.INSTANCE;
            if (exteraDatabase2 != null) {
                return exteraDatabase2;
            }
            synchronized (this) {
                exteraDatabase = (ExteraDatabase) Room.databaseBuilder(ApplicationLoader.applicationContext, ExteraDatabase.class, "extera-database").fallbackToDestructiveMigration(true).addCallback(new Callback()).addMigrations(MigrationsKt.getMIGRATION_1_2(), MigrationsKt.getMIGRATION_2_3(), MigrationsKt.getMIGRATION_3_4(), MigrationsKt.getMIGRATION_4_5()).build();
                ExteraDatabase.INSTANCE = exteraDatabase;
            }
            return exteraDatabase;
        }
    }
}
