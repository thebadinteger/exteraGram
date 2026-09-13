package c;

public final class g {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final String f15a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public volatile boolean f16b;

    public g(String str, boolean z) {
        this.f15a = str;
        this.f16b = z;
    }

    public final synchronized void a() {
        if (this.f16b) {
            return;
        }
        String str = this.f15a;
        if (str == null) {
            this.f16b = true;
        } else {
            android.util.Log.d("exteraHook", str);
            this.f16b = true;
        }
    }
}
