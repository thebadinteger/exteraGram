package androidx.work;
import java.util.concurrent.TimeUnit;
public class PeriodicWorkRequest extends WorkRequest {
    public static class Builder {
        public Builder(Class<? extends ListenableWorker> workerClass, long repeatInterval, TimeUnit repeatIntervalTimeUnit) {}
        public Builder setConstraints(Constraints constraints) { return this; }
        public Builder setBackoffCriteria(BackoffPolicy backoffPolicy, long backoffDelay, TimeUnit timeUnit) { return this; }
        public PeriodicWorkRequest build() { return null; }
    }
}
