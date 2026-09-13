package c;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import java.lang.reflect.Member;
import java.util.HashMap;

public final class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final HashMap f13a = new HashMap();

    public final XposedBridge.HookInfo a(Member member, XC_MethodHook xC_MethodHook, b bVar) {
        XposedBridge.HookInfo hookInfo = new XposedBridge.HookInfo(member);
        hookInfo.getCallbacks().add(xC_MethodHook);
        this.f13a.put(member, hookInfo);
        try {
            Member memberA = bVar.a(hookInfo, member);
            if (memberA == null) {
                throw new IllegalStateException("Failed to hook method");
            }
            hookInfo.setBackup(memberA);
            return hookInfo;
        } catch (Throwable th) {
            this.f13a.remove(member);
            hookInfo.markInstallFailed();
            throw th;
        }
    }
}
