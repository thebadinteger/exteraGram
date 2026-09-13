package c;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class f {
    public static void a(Member member) {
        if (member == null) {
            f$$ExternalSyntheticBUOutline2.m("method must not be null");
            return;
        }
        if (!(member instanceof Method) && !(member instanceof Constructor)) {
            f$$ExternalSyntheticBUOutline1.m("method must be a Method or Constructor");
        } else if (Modifier.isAbstract(member instanceof Method ? ((Method) member).getModifiers() : ((Constructor) member).getModifiers())) {
            f$$ExternalSyntheticBUOutline1.m("method must not be abstract");
        }
    }
}
