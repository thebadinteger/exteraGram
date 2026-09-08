package com.exteragram.messenger.api.db;

import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;

@Metadata(d1 = {"\u0000T\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010$\n\u0002\u0018\u0002\n\u0002\u0010 \n\u0000\n\u0002\u0010\"\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u000b\u001a\u00020\fH\u0014J\b\u0010\r\u001a\u00020\u000eH\u0014J\b\u0010\u000f\u001a\u00020\u0010H\u0016J\"\u0010\u0011\u001a\u001c\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u0013\u0012\u000e\u0012\f\u0012\b\u0012\u0006\u0012\u0002\b\u00030\u00130\u00140\u0012H\u0014J\u0016\u0010\u0015\u001a\u0010\u0012\f\u0012\n\u0012\u0006\b\u0001\u0012\u00020\u00170\u00130\u0016H\u0016J*\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u00142\u001a\u0010\u001a\u001a\u0016\u0012\f\u0012\n\u0012\u0006\b\u0001\u0012\u00020\u00170\u0013\u0012\u0004\u0012\u00020\u00170\u0012H\u0016J\b\u0010\u001b\u001a\u00020\u0006H\u0016J\b\u0010\u001c\u001a\u00020\bH\u0016J\b\u0010\u001d\u001a\u00020\nH\u0016R\u0014\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\b0\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u0014\u0010\t\u001a\b\u0012\u0004\u0012\u00020\n0\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u001e"}, d2 = {"Lcom/exteragram/messenger/api/db/ExteraDatabase_Impl;", "Lcom/exteragram/messenger/api/db/ExteraDatabase;", "<init>", "()V", "_profileDao", "Lkotlin/Lazy;", "Lcom/exteragram/messenger/api/db/ProfileDao;", "_addedRegDateDao", "Lcom/exteragram/messenger/api/db/AddedRegDateDao;", "_boostySubscriberDao", "Lcom/exteragram/messenger/api/db/BoostySubscriberDao;", "createOpenDelegate", "Landroidx/room/RoomOpenDelegate;", "createInvalidationTracker", "Landroidx/room/InvalidationTracker;", "clearAllTables", _UrlKt.FRAGMENT_ENCODE_SET, "getRequiredTypeConverterClasses", _UrlKt.FRAGMENT_ENCODE_SET, "Lkotlin/reflect/KClass;", _UrlKt.FRAGMENT_ENCODE_SET, "getRequiredAutoMigrationSpecClasses", _UrlKt.FRAGMENT_ENCODE_SET, "Landroidx/room/migration/AutoMigrationSpec;", "createAutoMigrations", "Landroidx/room/migration/Migration;", "autoMigrationSpecs", "profileDao", "addedRegDateDao", "boostySubscriberDao", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class ExteraDatabase_Impl extends ExteraDatabase {
    private final Lazy<ProfileDao> _profileDao = LazyKt.lazy(new Function0() { 
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return ExteraDatabase_Impl.$r8$lambda$JPRoYntD9n_OlGUDgtg2Biw9RoU(this.f$0);
        }
    });
    private final Lazy<AddedRegDateDao> _addedRegDateDao = LazyKt.lazy(new Function0() { 
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return ExteraDatabase_Impl.$r8$lambda$SlcSYaiIe7u1BWCtS_TPYlnsVms(this.f$0);
        }
    });
    private final Lazy<BoostySubscriberDao> _boostySubscriberDao = LazyKt.lazy(new Function0() { 
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return ExteraDatabase_Impl.$r8$lambda$cfVh7uxfsfuGBKXdttLxfBFUQEc(this.f$0);
        }
    });

    public static ProfileDao_Impl $r8$lambda$JPRoYntD9n_OlGUDgtg2Biw9RoU(ExteraDatabase_Impl exteraDatabase_Impl) {
        return new ProfileDao_Impl(exteraDatabase_Impl);
    }

    public static AddedRegDateDao_Impl $r8$lambda$SlcSYaiIe7u1BWCtS_TPYlnsVms(ExteraDatabase_Impl exteraDatabase_Impl) {
        return new AddedRegDateDao_Impl(exteraDatabase_Impl);
    }

    public static BoostySubscriberDao_Impl $r8$lambda$cfVh7uxfsfuGBKXdttLxfBFUQEc(ExteraDatabase_Impl exteraDatabase_Impl) {
        return new BoostySubscriberDao_Impl(exteraDatabase_Impl);
    }

    @Override // androidx.room.RoomDatabase
    public RoomOpenDelegate createOpenDelegate() {
        return new RoomOpenDelegate() { 
            @Override // androidx.room.RoomOpenDelegate
            public void onCreate(SQLiteConnection connection) {
            }

            @Override // androidx.room.RoomOpenDelegate
            public void onPostMigrate(SQLiteConnection connection) {
            }

            {
                super(5, "0e4e0efe20ecd6ef10fbac10ebbb089b", "cb8806bf8ba241f006e47a1ddab85bcf");
            }

            @Override // androidx.room.RoomOpenDelegate
            public void createAllTables(SQLiteConnection connection) {
                SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `ProfileDTO` (`id` INTEGER NOT NULL, `type` TEXT NOT NULL, `status` TEXT NOT NULL, `badge` TEXT, `nowPlaying` TEXT, `deleted` INTEGER, `canChangeBadge` INTEGER, PRIMARY KEY(`id`))");
                SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `AddedRegDateDTO` (`userId` INTEGER NOT NULL, PRIMARY KEY(`userId`))");
                SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `BoostySubscriberDTO` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `totalAmountRub` TEXT NOT NULL, `totalAmountUsd` TEXT NOT NULL, PRIMARY KEY(`id`))");
                SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
                SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '0e4e0efe20ecd6ef10fbac10ebbb089b')");
            }

            @Override // androidx.room.RoomOpenDelegate
            public void dropAllTables(SQLiteConnection connection) {
                SQLite.execSQL(connection, "DROP TABLE IF EXISTS `ProfileDTO`");
                SQLite.execSQL(connection, "DROP TABLE IF EXISTS `AddedRegDateDTO`");
                SQLite.execSQL(connection, "DROP TABLE IF EXISTS `BoostySubscriberDTO`");
            }

            @Override // androidx.room.RoomOpenDelegate
            public void onOpen(SQLiteConnection connection) {
                this.this$0.internalInitInvalidationTracker(connection);
            }

            @Override // androidx.room.RoomOpenDelegate
            public void onPreMigrate(SQLiteConnection connection) {
                DBUtil.dropFtsSyncTriggers(connection);
            }

            @Override // androidx.room.RoomOpenDelegate
            public RoomOpenDelegate.ValidationResult onValidateSchema(SQLiteConnection connection) {
                LinkedHashMap linkedHashMap = new LinkedHashMap();
                linkedHashMap.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, 1));
                linkedHashMap.put(TeXSymbolParser.TYPE_ATTR, new TableInfo.Column(TeXSymbolParser.TYPE_ATTR, "TEXT", true, 0, null, 1));
                linkedHashMap.put("status", new TableInfo.Column("status", "TEXT", true, 0, null, 1));
                linkedHashMap.put("badge", new TableInfo.Column("badge", "TEXT", false, 0, null, 1));
                linkedHashMap.put("nowPlaying", new TableInfo.Column("nowPlaying", "TEXT", false, 0, null, 1));
                linkedHashMap.put("deleted", new TableInfo.Column("deleted", "INTEGER", false, 0, null, 1));
                linkedHashMap.put("canChangeBadge", new TableInfo.Column("canChangeBadge", "INTEGER", false, 0, null, 1));
                TableInfo tableInfo = new TableInfo("ProfileDTO", linkedHashMap, new LinkedHashSet(), new LinkedHashSet());
                TableInfo.Companion companion = TableInfo.INSTANCE;
                TableInfo tableInfo2 = companion.read(connection, "ProfileDTO");
                if (!tableInfo.equals(tableInfo2)) {
                    return new RoomOpenDelegate.ValidationResult(false, "ProfileDTO(com.exteragram.messenger.api.dto.ProfileDTO).\n Expected:\n" + tableInfo + "\n Found:\n" + tableInfo2);
                }
                LinkedHashMap linkedHashMap2 = new LinkedHashMap();
                linkedHashMap2.put("userId", new TableInfo.Column("userId", "INTEGER", true, 1, null, 1));
                TableInfo tableInfo3 = new TableInfo("AddedRegDateDTO", linkedHashMap2, new LinkedHashSet(), new LinkedHashSet());
                TableInfo tableInfo4 = companion.read(connection, "AddedRegDateDTO");
                if (!tableInfo3.equals(tableInfo4)) {
                    return new RoomOpenDelegate.ValidationResult(false, "AddedRegDateDTO(com.exteragram.messenger.api.dto.AddedRegDateDTO).\n Expected:\n" + tableInfo3 + "\n Found:\n" + tableInfo4);
                }
                LinkedHashMap linkedHashMap3 = new LinkedHashMap();
                linkedHashMap3.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, 1));
                linkedHashMap3.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, 1));
                linkedHashMap3.put("totalAmountRub", new TableInfo.Column("totalAmountRub", "TEXT", true, 0, null, 1));
                linkedHashMap3.put("totalAmountUsd", new TableInfo.Column("totalAmountUsd", "TEXT", true, 0, null, 1));
                TableInfo tableInfo5 = new TableInfo("BoostySubscriberDTO", linkedHashMap3, new LinkedHashSet(), new LinkedHashSet());
                TableInfo tableInfo6 = companion.read(connection, "BoostySubscriberDTO");
                if (!tableInfo5.equals(tableInfo6)) {
                    return new RoomOpenDelegate.ValidationResult(false, "BoostySubscriberDTO(com.exteragram.messenger.api.dto.BoostySubscriberDTO).\n Expected:\n" + tableInfo5 + "\n Found:\n" + tableInfo6);
                }
                return new RoomOpenDelegate.ValidationResult(true, null);
            }
        };
    }

    @Override // androidx.room.RoomDatabase
    public InvalidationTracker createInvalidationTracker() {
        return new InvalidationTracker(this, new LinkedHashMap(), new LinkedHashMap(), "ProfileDTO", "AddedRegDateDTO", "BoostySubscriberDTO");
    }

    @Override // androidx.room.RoomDatabase
    public void clearAllTables() {
        super.performClear(false, "ProfileDTO", "AddedRegDateDTO", "BoostySubscriberDTO");
    }

    @Override // androidx.room.RoomDatabase
    public Map<KClass<?>, List<KClass<?>>> getRequiredTypeConverterClasses() {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        linkedHashMap.put(Reflection.getOrCreateKotlinClass(ProfileDao.class), ProfileDao_Impl.INSTANCE.getRequiredConverters());
        linkedHashMap.put(Reflection.getOrCreateKotlinClass(AddedRegDateDao.class), AddedRegDateDao_Impl.INSTANCE.getRequiredConverters());
        linkedHashMap.put(Reflection.getOrCreateKotlinClass(BoostySubscriberDao.class), BoostySubscriberDao_Impl.INSTANCE.getRequiredConverters());
        return linkedHashMap;
    }

    @Override // androidx.room.RoomDatabase
    public Set<KClass<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecClasses() {
        return new LinkedHashSet();
    }

    @Override // androidx.room.RoomDatabase
    public List<Migration> createAutoMigrations(Map<KClass<? extends AutoMigrationSpec>, ? extends AutoMigrationSpec> autoMigrationSpecs) {
        return new ArrayList();
    }

    @Override 
    public ProfileDao profileDao() {
        return this._profileDao.getValue();
    }

    @Override 
    public AddedRegDateDao addedRegDateDao() {
        return this._addedRegDateDao.getValue();
    }

    @Override 
    public BoostySubscriberDao boostySubscriberDao() {
        return this._boostySubscriberDao.getValue();
    }
}
