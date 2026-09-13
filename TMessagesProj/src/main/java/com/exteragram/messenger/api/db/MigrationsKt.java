package com.exteragram.messenger.api.db;

import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import kotlin.Metadata;

@Metadata(d1 = {"\u0000\n\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\"\u0011\u0010\u0000\u001a\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0003\"\u0011\u0010\u0004\u001a\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u0003\"\u0011\u0010\u0006\u001a\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u0003\"\u0011\u0010\b\u001a\u00020\u0001¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\u0003¨\u0006\n"}, d2 = {"MIGRATION_1_2", "Landroidx/room/migration/Migration;", "getMIGRATION_1_2", "()Landroidx/room/migration/Migration;", "MIGRATION_2_3", "getMIGRATION_2_3", "MIGRATION_3_4", "getMIGRATION_3_4", "MIGRATION_4_5", "getMIGRATION_4_5", "TMessagesProj"}, k = 2, mv = {2, 2, 0}, xi = 48)
public final class MigrationsKt {
    private static final Migration MIGRATION_1_2 = new Migration(1, 2) { 
        @Override // androidx.room.migration.Migration
        public void migrate(SupportSQLiteDatabase db) {
        }
    };
    private static final Migration MIGRATION_2_3 = new Migration(2, 3) { 
        @Override // androidx.room.migration.Migration
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `AddedRegDateDTO` (`userId` INTEGER NOT NULL, PRIMARY KEY(`userId`))");
        }
    };
    private static final Migration MIGRATION_3_4 = new Migration(3, 4) { 
        @Override // androidx.room.migration.Migration
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("ALTER TABLE `ProfileDTO` ADD COLUMN `canChangeBadge` INTEGER");
        }
    };
    private static final Migration MIGRATION_4_5 = new Migration(4, 5) { 
        @Override // androidx.room.migration.Migration
        public void migrate(SupportSQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `BoostySubscriberDTO` (`id` INTEGER NOT NULL, `name` TEXT NOT NULL, `totalAmountRub` TEXT NOT NULL, `totalAmountUsd` TEXT NOT NULL, PRIMARY KEY(`id`))");
        }
    };

    public static final Migration getMIGRATION_1_2() {
        return MIGRATION_1_2;
    }

    public static final Migration getMIGRATION_2_3() {
        return MIGRATION_2_3;
    }

    public static final Migration getMIGRATION_3_4() {
        return MIGRATION_3_4;
    }

    public static final Migration getMIGRATION_4_5() {
        return MIGRATION_4_5;
    }
}
