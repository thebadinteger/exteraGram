package androidx.work;
import android.content.Context;
public abstract class Worker extends ListenableWorker {
    public Worker(Context context, WorkerParameters workerParams) { super(context, workerParams); }
    public abstract Result doWork();
}
