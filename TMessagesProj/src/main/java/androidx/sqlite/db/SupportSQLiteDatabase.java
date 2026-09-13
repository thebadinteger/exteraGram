package androidx.sqlite.db;
public interface SupportSQLiteDatabase {
    void execSQL(String sql);
    android.database.Cursor query(String sql);
    android.database.Cursor query(String sql, Object[] args);
}
