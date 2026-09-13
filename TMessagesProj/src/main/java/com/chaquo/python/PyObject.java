package com.chaquo.python;

import java.lang.ref.WeakReference;
import java.lang.reflect.UndeclaredThrowableException;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class PyObject extends AbstractMap<String, PyObject> implements AutoCloseable {
    private static final Map<Long, WeakReference<PyObject>> cache = new HashMap();
    public long addr;

    private native long callAttrThrowsNative(String str, Object... objArr);

    private native long callThrowsNative(Object... objArr);

    private native void closeNative();

    private native boolean containsKeyNative(String str);

    public native List<String> dir();

    private static native long fromJavaNative(Object obj);

    private native long getNative(String str);

    private native long putNative(String str, Object obj);

    private native long removeNative(String str);

    private native long typeNative();

    @Override // java.util.AbstractMap, java.util.Map
    public native boolean equals(Object obj);

    @Override // java.util.AbstractMap, java.util.Map
    public native int hashCode();

    public native long id();

    public native String repr();

    public native boolean toBoolean();

    public native byte toByte();

    public native char toChar();

    public native double toDouble();

    public native float toFloat();

    public native int toInt();

    public native <T> T toJava(Class<T> cls);

    public native long toLong();

    public native short toShort();

    @Override // java.util.AbstractMap
    public native String toString();

    public static PyObject getInstance(long j) {
        PyObject pyObject;
        if (j == 0) {
            return null;
        }
        Map<Long, WeakReference<PyObject>> map = cache;
        synchronized (map) {
            try {
                WeakReference<PyObject> weakReference = map.get(Long.valueOf(j));
                if (weakReference != null && (pyObject = weakReference.get()) != null) {
                    return pyObject;
                }
                PyObject pyObject2 = new PyObject(j);
                map.put(Long.valueOf(j), new WeakReference<>(pyObject2));
                return pyObject2;
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private PyObject(long j) {
        this.addr = j;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        PyObject pyObject;
        if (this.addr == 0) {
            return;
        }
        Map<Long, WeakReference<PyObject>> map = cache;
        synchronized (map) {
            try {
                WeakReference<PyObject> weakReferenceRemove = map.remove(Long.valueOf(this.addr));
                if (weakReferenceRemove != null && (pyObject = weakReferenceRemove.get()) != null && pyObject != this) {
                    map.put(Long.valueOf(this.addr), weakReferenceRemove);
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        closeNative();
        this.addr = 0L;
    }

    public static PyObject fromJava(Object obj) {
        return getInstance(fromJavaNative(obj));
    }

    public List<PyObject> asList() {
        return new PyList(this);
    }

    public Map<PyObject, PyObject> asMap() {
        return new PyMap(this);
    }

    public Set<PyObject> asSet() {
        return new PySet(this);
    }

    public PyObject type() {
        PyObject pyObject = getInstance(typeNative());
        Objects.requireNonNull(pyObject);
        return pyObject;
    }

    public PyObject call(Object... objArr) {
        try {
            return callThrows(objArr);
        } catch (PyException e) {
            throw e;
        } catch (Throwable th) {
            throw new PyException(th);
        }
    }

    public PyObject callThrows(Object... objArr) {
        return getInstance(callThrowsNative(objArr));
    }

    public PyObject callAttr(String str, Object... objArr) {
        try {
            return callAttrThrows(str, objArr);
        } catch (PyException e) {
            throw e;
        } catch (Throwable th) {
            throw new PyException(th);
        }
    }

    public PyObject callAttrThrows(String str, Object... objArr) {
        return getInstance(callAttrThrowsNative(str, objArr));
    }

    public static PyObject _chaquopyCall(StaticProxy staticProxy, String str, Object... objArr) {
        try {
            return fromJava(staticProxy).callAttrThrows(str, objArr);
        } catch (Error | RuntimeException e) {
            throw e;
        } catch (Throwable th) {
            throw new UndeclaredThrowableException(th);
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public void clear() {
        super.clear();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean isEmpty() {
        return super.isEmpty();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object obj) {
        return containsKeyNative((String) obj);
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsValue(Object obj) {
        return super.containsValue(fromJava(obj));
    }

    @Override // java.util.AbstractMap, java.util.Map
    public PyObject get(Object obj) {
        return getInstance(getNative((String) obj));
    }

    @Override // java.util.AbstractMap, java.util.Map
    public PyObject put(String str, PyObject pyObject) {
        return put(str, (Object) pyObject);
    }

    public PyObject put(String str, Object obj) {
        return getInstance(putNative(str, obj));
    }

    @Override // java.util.AbstractMap, java.util.Map
    public PyObject remove(Object obj) {
        return getInstance(removeNative((String) obj));
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<String> keySet() {
        return super.keySet();
    }

    public class AnonymousClass1 extends AbstractSet<Map.Entry<String, PyObject>> {
        public AnonymousClass1() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return PyObject.this.dir().size();
        }

        public class C00151 implements Iterator<Map.Entry<String, PyObject>> {
            int i = 0;
            List<String> keys;

            public C00151() {
                this.keys = PyObject.this.dir();
            }

            @Override // java.util.Iterator
            public boolean hasNext() {
                return this.i < this.keys.size();
            }

            @Override // java.util.Iterator
            public Map.Entry<String, PyObject> next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }
                Map.Entry<String, PyObject> entry = new Map.Entry<String, PyObject>() { // from class: com.chaquo.python.PyObject.1.1.1
                    String key;

                    {
                        this.key = C00151.this.keys.get(C00151.this.i);
                    }

                    @Override // java.util.Map.Entry
                    public String getKey() {
                        return this.key;
                    }

                    @Override // java.util.Map.Entry
                    public PyObject getValue() {
                        return PyObject.this.get((Object) this.key);
                    }

                    @Override // java.util.Map.Entry
                    public PyObject setValue(PyObject pyObject) {
                        return PyObject.this.put(this.key, pyObject);
                    }
                };
                this.i++;
                return entry;
            }

            @Override // java.util.Iterator
            public void remove() {
                PyObject.this.remove((Object) this.keys.get(this.i - 1));
            }
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<String, PyObject>> iterator() {
            return new C00151();
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<String, PyObject>> entrySet() {
        return new AnonymousClass1();
    }

    public void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
