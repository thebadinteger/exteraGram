package de.robv.android.xposed;

public abstract class XC_MethodReplacement extends XC_MethodHook {
    public static final XC_MethodReplacement DO_NOTHING = new XC_MethodReplacement(20000) { // from class: de.robv.android.xposed.XC_MethodReplacement.1
        @Override // de.robv.android.xposed.XC_MethodReplacement
        public Object replaceHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
            return null;
        }
    };

    public XC_MethodReplacement() {
    }

    public static XC_MethodReplacement returnConstant(Object obj) {
        return returnConstant(50, obj);
    }

    @Override // de.robv.android.xposed.XC_MethodHook
    public final void afterHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
    }

    @Override // de.robv.android.xposed.XC_MethodHook
    public final void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
        try {
            methodHookParam.setResult(replaceHookedMethod(methodHookParam));
        } catch (Throwable th) {
            methodHookParam.setThrowable(th);
        }
    }

    public abstract Object replaceHookedMethod(XC_MethodHook.MethodHookParam methodHookParam);

    public XC_MethodReplacement(int i) {
        super(i);
    }

    public static XC_MethodReplacement returnConstant(int i, final Object obj) {
        return new XC_MethodReplacement(i) { // from class: de.robv.android.xposed.XC_MethodReplacement.2
            @Override // de.robv.android.xposed.XC_MethodReplacement
            public Object replaceHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) {
                return obj;
            }
        };
    }
}
