package androidx.room.util;
import android.database.Cursor;
public class CursorUtil {
    public static int getColumnIndexOrThrow(Cursor cursor, String name) {
        int idx = cursor.getColumnIndex(name);
        if (idx >= 0) return idx;
        throw new IllegalArgumentException("column '" + name + "' does not exist");
    }
}
