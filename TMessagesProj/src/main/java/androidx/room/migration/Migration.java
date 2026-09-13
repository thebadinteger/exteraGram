package androidx.room.migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
public abstract class Migration {
    public final int startVersion, endVersion;
    public Migration(int startVersion, int endVersion) { this.startVersion = startVersion; this.endVersion = endVersion; }
    public abstract void migrate(SupportSQLiteDatabase db);
}
