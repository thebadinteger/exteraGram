package androidx.work;

import android.content.Context;

public abstract class CoroutineWorker extends ListenableWorker {
    public CoroutineWorker(Context context, WorkerParameters workerParams) {
        super(context, workerParams);
    }

    public abstract Object doWork(kotlin.coroutines.Continuation<? super ListenableWorker.Result> continuation);
}
