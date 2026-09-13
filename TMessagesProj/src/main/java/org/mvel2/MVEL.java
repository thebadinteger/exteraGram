package org.mvel2;

import java.io.Serializable;
import java.util.Map;

public class MVEL {
    public static Serializable compileExpression(String expression) {
        return null;
    }

    public static <T> T executeExpression(Serializable compiled, Map vars, Class<T> toType) {
        return null;
    }

    public static <T> T executeExpression(Object compiled, Object ctx, Map vars, Class<T> toType) {
        return null;
    }

    public static Object executeExpression(Object compiled, Object ctx, Map vars) {
        return null;
    }

    public static Object executeExpression(Object compiled, Map vars) {
        return null;
    }
}
