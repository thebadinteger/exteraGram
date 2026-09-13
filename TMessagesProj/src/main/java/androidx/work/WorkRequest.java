package androidx.work;

import java.util.concurrent.TimeUnit;

public abstract class WorkRequest {
    public static abstract class Builder<B extends Builder<B, W>, W extends WorkRequest> {
        @SuppressWarnings("unchecked")
        public B setBackoffCriteria(BackoffPolicy backoffPolicy, long backoffDelay, TimeUnit timeUnit) { return (B) this; }
        @SuppressWarnings("unchecked")
        public B setConstraints(Constraints constraints) { return (B) this; }
        @SuppressWarnings("unchecked")
        public B setInitialDelay(long duration, TimeUnit timeUnit) { return (B) this; }
        @SuppressWarnings("unchecked")
        public B setInputData(Data inputData) { return (B) this; }
        public W build() { return null; }
    }
}
