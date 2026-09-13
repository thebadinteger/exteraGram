package dev.exterahook.runtime.bridge;

import c.g;
import dev.exterahook.runtime.bridge.JniBridgeBindings;
import java.lang.reflect.Member;
import kotlin.Unit;
import okhttp3.internal.url._UrlKt;
import org.lsposed.hiddenapibypass.HiddenApiBypass;

public final class HookBridgeImpl implements HookBridge {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final g f7a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final dev.exterahook.runtime.internal.HookClassA f8b;

    /* JADX INFO: renamed from: c, reason: collision with root package name */
    public volatile boolean f9c;

    public HookBridgeImpl(g gVar, dev.exterahook.runtime.internal.HookClassB bVar, dev.exterahook.runtime.internal.HookClassA aVar) {
        this.f7a = gVar;
        this.f8b = aVar;
    }

    public final void a() {
        try {
            if (!this.f9c) {
                synchronized (this) {
                    try {
                        if (!this.f9c) {
                            try {
                                HiddenApiBypass.addHiddenApiExemptions(_UrlKt.FRAGMENT_ENCODE_SET);
                            } catch (Throwable unused) {
                            }
                            this.f9c = true;
                        }
                        Unit unit = Unit.INSTANCE;
                    } catch (Throwable th) {
                        throw th;
                    }
                }
            }
            this.f7a.a();
        } catch (Throwable th2) {
            this.f7a.a();
            throw th2;
        }
    }

    @Override
    public final boolean a(Member member) {
        a();
        return JniBridgeBindings.unhook0(member);
    }
}
