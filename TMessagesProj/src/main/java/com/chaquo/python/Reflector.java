package com.chaquo.python;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Reflector {
    private static final DeniedMethod[] DENIED_METHODS = {new DeniedMethod("android.widget.TextView", "setTextColor", Void.TYPE, Long.TYPE)};
    private static Map<Class<?>, Reflector> instances = new HashMap();
    private Map<String, Class<?>> classes;
    private Map<String, Class<?>> classesAll;
    private Map<String, Field> fields;
    private Map<String, Field> fieldsAll;
    private final Class<?> klass;
    private Map<String, Member> methods;
    private Map<String, Member> methodsAll;
    private Map<String, List<Member>> multipleMethods;
    private Map<String, List<Member>> multipleMethodsAll;
    private Map<String, List<Member>> multiplePropertyGetters;
    private Map<String, List<Member>> multiplePropertyGettersAll;
    private Map<String, List<Member>> multiplePropertySetters;
    private Map<String, List<Member>> multiplePropertySettersAll;
    private Map<String, List<Member>> multipleStaticPropertyGetters;
    private Map<String, List<Member>> multipleStaticPropertyGettersAll;
    private Map<String, List<Member>> multipleStaticPropertySetters;
    private Map<String, List<Member>> multipleStaticPropertySettersAll;
    private Map<String, Member> propertyGetters;
    private Map<String, Member> propertyGettersAll;
    private Map<String, Member> propertySetters;
    private Map<String, Member> propertySettersAll;
    private Map<String, Member> staticPropertyGetters;
    private Map<String, Member> staticPropertyGettersAll;
    private Map<String, Member> staticPropertySetters;
    private Map<String, Member> staticPropertySettersAll;

    private boolean isAccessible(int i) {
        return (i & 5) != 0;
    }

    public static Reflector getInstance(Class<?> cls) {
        Reflector reflector = instances.get(cls);
        if (reflector != null) {
            return reflector;
        }
        Reflector reflector2 = new Reflector(cls);
        instances.put(cls, reflector2);
        return reflector2;
    }

    private Reflector(Class<?> cls) {
        this.klass = cls;
    }

    public synchronized String[] dir() {
        return dir(false);
    }

    public synchronized String[] dirAll() {
        return dir(true);
    }

    public synchronized String[] dir(boolean z) {
        HashSet hashSet;
        try {
            if (z) {
                if (this.methodsAll == null) {
                    loadMethods(true);
                }
                if (this.fieldsAll == null) {
                    loadFields(true);
                }
                if (this.classesAll == null) {
                    loadClasses(true);
                }
            } else {
                if (this.methods == null) {
                    loadMethods(false);
                }
                if (this.fields == null) {
                    loadFields(false);
                }
                if (this.classes == null) {
                    loadClasses(false);
                }
            }
            hashSet = new HashSet();
            hashSet.addAll((z ? this.methodsAll : this.methods).keySet());
            hashSet.addAll((z ? this.multipleMethodsAll : this.multipleMethods).keySet());
            hashSet.addAll((z ? this.fieldsAll : this.fields).keySet());
            hashSet.addAll((z ? this.classesAll : this.classes).keySet());
        } catch (Throwable th) {
            throw th;
        }
        return (String[]) hashSet.toArray(new String[0]);
    }

    public synchronized Member[] getMethods(String str) {
        return getMethods(str, false);
    }

    public synchronized Member[] getMethodsAll(String str) {
        return getMethods(str, true);
    }

    public synchronized Member[] getMethods(String str, boolean z) {
        try {
            if (z) {
                if (this.methodsAll == null) {
                    loadMethods(true);
                }
            } else if (this.methods == null) {
                loadMethods(false);
            }
            Map<String, Member> map = z ? this.methodsAll : this.methods;
            List<Member> list = (z ? this.multipleMethodsAll : this.multipleMethods).get(str);
            if (list != null) {
                return (Member[]) list.toArray(new Member[0]);
            }
            Member member = map.get(str);
            if (member != null) {
                return new Member[]{member};
            }
            return null;
        } catch (Throwable th) {
            throw th;
        }
    }

    public synchronized Member[] getPropertyGetters(String str, boolean z, int i) {
        try {
            if (z) {
                if (this.propertyGettersAll == null) {
                    loadMethods(true);
                }
            } else if (this.propertyGetters == null) {
                loadMethods(false);
            }
        } catch (Throwable th) {
            throw th;
        }
        return getPropertyMethods(str, z, i, this.propertyGetters, this.multiplePropertyGetters, this.staticPropertyGetters, this.multipleStaticPropertyGetters, this.propertyGettersAll, this.multiplePropertyGettersAll, this.staticPropertyGettersAll, this.multipleStaticPropertyGettersAll);
    }

    public synchronized Member[] getPropertySetters(String str, boolean z, int i) {
        try {
            if (z) {
                if (this.propertySettersAll == null) {
                    loadMethods(true);
                }
            } else if (this.propertySetters == null) {
                loadMethods(false);
            }
        } catch (Throwable th) {
            throw th;
        }
        return getPropertyMethods(str, z, i, this.propertySetters, this.multiplePropertySetters, this.staticPropertySetters, this.multipleStaticPropertySetters, this.propertySettersAll, this.multiplePropertySettersAll, this.staticPropertySettersAll, this.multipleStaticPropertySettersAll);
    }

    private Member[] getPropertyMethods(String str, boolean z, int i, Map<String, Member> map, Map<String, List<Member>> map2, Map<String, Member> map3, Map<String, List<Member>> map4, Map<String, Member> map5, Map<String, List<Member>> map6, Map<String, Member> map7, Map<String, List<Member>> map8) {
        if (z) {
            if (i == 1) {
                map5 = map7;
            }
            if (i == 1) {
                map6 = map8;
            }
        } else {
            if (i == 1) {
                map = map3;
            }
            if (i == 1) {
                map2 = map4;
            }
            map5 = map;
            map6 = map2;
        }
        List<Member> list = map6.get(str);
        if (list != null) {
            return (Member[]) list.toArray(new Member[0]);
        }
        Member member = map5.get(str);
        if (member != null) {
            return new Member[]{member};
        }
        return null;
    }

    private void loadMethods(boolean z) {
        Map<String, Member> map = new HashMap<>();
        Map<String, List<Member>> map2 = new HashMap<>();
        Map<String, Member> map3 = new HashMap<>();
        Map<String, List<Member>> map4 = new HashMap<>();
        Map<String, Member> map5 = new HashMap<>();
        Map<String, List<Member>> map6 = new HashMap<>();
        Map<String, Member> map7 = new HashMap<>();
        Map<String, List<Member>> map8 = new HashMap<>();
        Map<String, Member> map9 = new HashMap<>();
        Map<String, List<Member>> map10 = new HashMap<>();
        Constructor<?>[] declaredConstructors = this.klass.getDeclaredConstructors();
        int length = declaredConstructors.length;
        int i = 0;
        while (i < length) {
            Constructor<?> constructor = declaredConstructors[i];
            if (includeMember(constructor, z)) {
                prepareAccessible(constructor, z);
                loadMethod(map, map2, constructor, "<init>");
            }
            i++;
            declaredConstructors = declaredConstructors;
        }
        for (Method method : getDeclaredMethods()) {
            if (!isDeniedMethod(method) && includeMember(method, z)) {
                prepareAccessible(method, z);
                loadMethod(map, map2, method, method.getName());
                loadPropertyMethods(method, map3, map4, map5, map6, map7, map8, map9, map10);
            }
        }
        if (z) {
            this.methodsAll = map;
            this.multipleMethodsAll = map2;
            this.propertyGettersAll = map3;
            this.multiplePropertyGettersAll = map4;
            this.staticPropertyGettersAll = map5;
            this.multipleStaticPropertyGettersAll = map6;
            this.propertySettersAll = map7;
            this.multiplePropertySettersAll = map8;
            this.staticPropertySettersAll = map9;
            this.multipleStaticPropertySettersAll = map10;
            return;
        }
        this.methods = map;
        this.multipleMethods = map2;
        this.propertyGetters = map3;
        this.multiplePropertyGetters = map4;
        this.staticPropertyGetters = map5;
        this.multipleStaticPropertyGetters = map6;
        this.propertySetters = map7;
        this.multiplePropertySetters = map8;
        this.staticPropertySetters = map9;
        this.multipleStaticPropertySetters = map10;
    }

    private boolean isDeniedMethod(Method method) {
        for (DeniedMethod deniedMethod : DENIED_METHODS) {
            if (deniedMethod.matches(method)) {
                return true;
            }
        }
        return false;
    }

    public static class DeniedMethod {
        private final String className;
        private final String methodName;
        private final Class<?>[] parameterTypes;
        private final Class<?> returnType;

        public DeniedMethod(String str, String str2, Class<?> cls, Class<?>... clsArr) {
            this.className = str;
            this.methodName = str2;
            this.returnType = cls;
            this.parameterTypes = clsArr;
        }

        public boolean matches(Method method) {
            return this.className.equals(method.getDeclaringClass().getName()) && this.methodName.equals(method.getName()) && this.returnType == method.getReturnType() && Arrays.equals(this.parameterTypes, method.getParameterTypes());
        }
    }

    private Collection<Method> getDeclaredMethods() {
        Class<?> superclass;
        try {
            return Arrays.asList(this.klass.getDeclaredMethods());
        } catch (NoClassDefFoundError unused) {
            HashSet hashSet = new HashSet();
            try {
                for (Method method : this.klass.getMethods()) {
                    if (method.getDeclaringClass() == this.klass) {
                        try {
                            method.getReturnType();
                            method.getParameterTypes();
                            hashSet.add(method);
                        } catch (NoClassDefFoundError unused2) {
                        }
                    }
                }
                while (true) {
                    superclass = superclass.getSuperclass();
                    if (superclass == null) {
                        return hashSet;
                    }
                    for (Method method2 : getInstance(superclass).getDeclaredMethods()) {
                        try {
                            hashSet.add(this.klass.getDeclaredMethod(method2.getName(), method2.getParameterTypes()));
                        } catch (NoSuchMethodException unused3) {
                        }
                    }
                }
            } catch (NoClassDefFoundError unused4) {
            }
            superclass = this.klass;
        }
    }

    private void loadMethod(Map<String, Member> map, Map<String, List<Member>> map2, Member member, String str) {
        Member memberRemove = map.remove(str);
        if (memberRemove != null) {
            ArrayList arrayList = new ArrayList();
            arrayList.add(memberRemove);
            arrayList.add(member);
            map2.put(str, arrayList);
            return;
        }
        List<Member> list = map2.get(str);
        if (list != null) {
            list.add(member);
        } else {
            map.put(str, member);
        }
    }

    private void loadPropertyMethods(Method method, Map<String, Member> map, Map<String, List<Member>> map2, Map<String, Member> map3, Map<String, List<Member>> map4, Map<String, Member> map5, Map<String, List<Member>> map6, Map<String, Member> map7, Map<String, List<Member>> map8) {
        boolean zIsStatic = Modifier.isStatic(method.getModifiers());
        for (String str : propertyGetterNames(method)) {
            loadMethod(map, map2, method, str);
            if (zIsStatic) {
                loadMethod(map3, map4, method, str);
            }
        }
        for (String str2 : propertySetterNames(method)) {
            loadMethod(map5, map6, method, str2);
            if (zIsStatic) {
                loadMethod(map7, map8, method, str2);
            }
        }
    }

    private List<String> propertyGetterNames(Method method) {
        String name = method.getName();
        ArrayList arrayList = new ArrayList();
        if (name.startsWith("get") && name.length() > 3 && method.getParameterTypes().length == 0 && method.getReturnType() != Void.TYPE) {
            String strSubstring = name.substring(3);
            addPropertyAlias(arrayList, decapitalizePropertyName(strSubstring));
            if (isBooleanType(method.getReturnType())) {
                addPropertyAlias(arrayList, booleanPropertyAliasName(strSubstring));
            }
        }
        if (name.startsWith("is") && name.length() > 2 && method.getParameterTypes().length == 0 && isBooleanType(method.getReturnType())) {
            addPropertyAlias(arrayList, decapitalizePropertyName(name.substring(2)));
            addPropertyAlias(arrayList, name);
        }
        return arrayList;
    }

    private List<String> propertySetterNames(Method method) {
        String name = method.getName();
        ArrayList arrayList = new ArrayList();
        if (name.startsWith("set") && name.length() > 3 && method.getParameterTypes().length != 0) {
            String strSubstring = name.substring(3);
            addPropertyAlias(arrayList, decapitalizePropertyName(strSubstring));
            if (isBooleanType(method.getParameterTypes()[0])) {
                addPropertyAlias(arrayList, booleanPropertyAliasName(strSubstring));
            }
        }
        return arrayList;
    }

    private void addPropertyAlias(List<String> list, String str) {
        if (str == null || list.contains(str)) {
            return;
        }
        list.add(str);
    }

    private boolean isBooleanType(Class<?> cls) {
        return cls == Boolean.TYPE || cls == Boolean.class;
    }

    private String booleanPropertyAliasName(String str) {
        if (str.isEmpty()) {
            return null;
        }
        if (str.startsWith("Is") && str.length() > 2 && Character.isUpperCase(str.charAt(2))) {
            return decapitalizePropertyName(str);
        }
        return "is".concat(str);
    }

    private String decapitalizePropertyName(String str) {
        if (str.isEmpty()) {
            return null;
        }
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }

    public synchronized Field getField(String str) {
        return getField(str, false);
    }

    public synchronized Field getFieldAll(String str) {
        return getField(str, true);
    }

    public synchronized Field getField(String str, boolean z) {
        try {
            if (z) {
                if (this.fieldsAll == null) {
                    loadFields(true);
                }
                return this.fieldsAll.get(str);
            }
            if (this.fields == null) {
                loadFields(false);
            }
            return this.fields.get(str);
        } catch (Throwable th) {
            throw th;
        }
    }

    private void loadFields(boolean z) {
        HashMap map = new HashMap();
        for (Field field : getDeclaredFields()) {
            if (z || isAccessible(field)) {
                prepareAccessible(field, z);
                map.put(field.getName(), field);
            }
        }
        if (z) {
            this.fieldsAll = map;
        } else {
            this.fields = map;
        }
    }

    private Collection<Field> getDeclaredFields() {
        try {
            return Arrays.asList(this.klass.getDeclaredFields());
        } catch (NoClassDefFoundError unused) {
            HashSet hashSet = new HashSet();
            try {
                for (Field field : this.klass.getFields()) {
                    if (field.getDeclaringClass() == this.klass) {
                        try {
                            field.getType();
                            hashSet.add(field);
                        } catch (NoClassDefFoundError unused2) {
                        }
                    }
                }
            } catch (NoClassDefFoundError unused3) {
            }
            return hashSet;
        }
    }

    public synchronized Class<?> getNestedClass(String str) {
        return getNestedClass(str, false);
    }

    public synchronized Class<?> getNestedClassAll(String str) {
        return getNestedClass(str, true);
    }

    public synchronized Class<?> getNestedClass(String str, boolean z) {
        try {
            if (z) {
                if (this.classesAll == null) {
                    loadClasses(true);
                }
                return this.classesAll.get(str);
            }
            if (this.classes == null) {
                loadClasses(false);
            }
            return this.classes.get(str);
        } catch (Throwable th) {
            throw th;
        }
    }

    private void loadClasses(boolean z) {
        HashMap map = new HashMap();
        for (Class<?> cls : this.klass.getDeclaredClasses()) {
            if (z || isAccessible(cls.getModifiers())) {
                String simpleName = cls.getSimpleName();
                if (!simpleName.isEmpty()) {
                    map.put(simpleName, cls);
                }
            }
        }
        if (z) {
            this.classesAll = map;
        } else {
            this.classes = map;
        }
    }

    private boolean includeMember(Member member, boolean z) {
        if (z) {
            return !member.isSynthetic();
        }
        return isAccessible(member);
    }

    private void prepareAccessible(AccessibleObject accessibleObject, boolean z) {
        if (z) {
            try {
                accessibleObject.setAccessible(true);
            } catch (SecurityException unused) {
            }
        }
    }

    private boolean isAccessible(Member member) {
        return isAccessible(member.getModifiers()) && !member.isSynthetic();
    }
}
