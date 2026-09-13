package androidx.room;

import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.room.migration.Migration;

public abstract class RoomDatabase {
    public static class Builder<T extends RoomDatabase> {
        public Builder<T> fallbackToDestructiveMigration() { return this; }
        public Builder<T> fallbackToDestructiveMigration(boolean dropAllTables) { return this; }
        public Builder<T> allowMainThreadQueries() { return this; }
        public Builder<T> addCallback(Callback callback) { return this; }
        public Builder<T> addMigrations(Migration... migrations) { return this; }
        @SuppressWarnings("unchecked")
        public T build() { return null; }
    }
    public abstract static class Callback {
        public void onCreate(SupportSQLiteDatabase db) {}
        public void onOpen(SupportSQLiteDatabase db) {}
        public void onDestructiveMigration(SupportSQLiteDatabase db) {}
    }
    // EntityInsertAdapter used by Room-generated Impl classes
    public abstract static class EntityInsertAdapter<T> {
        protected abstract String createQuery();
        protected abstract void bind(Object statement, T entity);
    }
}
