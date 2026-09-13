package androidx.work;
import android.content.Context;
public abstract class WorkManager {
    public static WorkManager getInstance(Context context) { return null; }
    public abstract Operation enqueueUniquePeriodicWork(String uniqueWorkName, ExistingPeriodicWorkPolicy existingPeriodicWorkPolicy, PeriodicWorkRequest periodicWorkRequest);
    public abstract Operation cancelUniqueWork(String uniqueWorkName);
}
