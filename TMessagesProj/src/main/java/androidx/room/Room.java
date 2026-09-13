package androidx.room;
import android.content.Context;
public class Room {
    public static <T extends RoomDatabase> RoomDatabase.Builder<T> databaseBuilder(Context context, Class<T> klass, String name) {
        return new RoomDatabase.Builder<T>();
    }
}
