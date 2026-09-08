package de.robv.android.xposed;

import de.robv.android.xposed.callbacks.IXUnhook;
import de.robv.android.xposed.callbacks.XCallback;
import java.lang.reflect.Member;

public abstract class XC_MethodHook extends XCallback {

    public static final class MethodHookParam extends XCallback.Param {
        public Object[] args;
        public Member method;
        public Object thisObject;
        private Object result = null;
        private Throwable throwable = null;
        boolean returnEarly = false;

        public Object getResult() {
            return this.result;
        }

        public Object getResultOrThrowable() throws Throwable {
            Throwable th = this.throwable;
            if (th == null) {
                return this.result;
            }
            throw th;
        }

        public Throwable getThrowable() {
            return this.throwable;
        }

        public boolean hasThrowable() {
            return this.throwable != null;
        }

        public void setResult(Object obj) {
            this.result = obj;
            this.throwable = null;
            this.returnEarly = true;
        }

        public void setThrowable(Throwable th) {
            this.throwable = th;
            this.result = null;
            this.returnEarly = true;
        }
    }

    public class Unhook implements IXUnhook<XC_MethodHook> {
        private final Member hookMethod;

        public Unhook(Member member) {
            this.hookMethod = member;
        }

        public Member getHookedMethod() {
            return this.hookMethod;
        }

        @Override // de.robv.android.xposed.callbacks.IXUnhook
        public void unhook() {
            XposedBridge.unhookMethod(this.hookMethod, XC_MethodHook.this);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // de.robv.android.xposed.callbacks.IXUnhook
        public XC_MethodHook getCallback() {
            return XC_MethodHook.this;
        }
    }

    public XC_MethodHook() {
    }

    public void afterHookedMethod(MethodHookParam methodHookParam) {
    }

    public void beforeHookedMethod(MethodHookParam methodHookParam) {
    }

    public XC_MethodHook(int i) {
        super(i);
    }
}
