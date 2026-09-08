package com.chaquo.python;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

class PyMap extends AbstractMap<PyObject, PyObject> {
    private final MethodCache methods;
    private final PyObject obj;

    public PyMap(PyObject pyObject) {
        this.obj = pyObject;
        MethodCache methodCache = new MethodCache(pyObject);
        this.methods = methodCache;
        methodCache.get("__contains__");
        methodCache.get("__getitem__");
        methodCache.get("__iter__");
        methodCache.get("__len__");
    }

    public class AnonymousClass1 extends AbstractSet<Map.Entry<PyObject, PyObject>> {
        public AnonymousClass1() {
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public int size() {
            return PyMap.this.methods.get("__len__").call(new Object[0]).toInt();
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
        public Iterator<Map.Entry<PyObject, PyObject>> iterator() {
            return new PyIterator<Map.Entry<PyObject, PyObject>>(PyMap.this.methods) { // from class: com.chaquo.python.PyMap.1.1
                @Override // com.chaquo.python.PyIterator
                public Map.Entry<PyObject, PyObject> makeNext(final PyObject pyObject) {
                    return new Map.Entry<PyObject, PyObject>() { // from class: com.chaquo.python.PyMap.1.1.1
                        /* JADX WARN: Can't rename method to resolve collision */
                        @Override // java.util.Map.Entry
                        public PyObject getKey() {
                            return pyObject;
                        }

                        /* JADX WARN: Can't rename method to resolve collision */
                        @Override // java.util.Map.Entry
                        public PyObject getValue() {
                            return PyMap.this.get((Object) pyObject);
                        }

                        @Override // java.util.Map.Entry
                        public PyObject setValue(PyObject pyObject2) {
                            return PyMap.this.put(pyObject, pyObject2);
                        }
                    };
                }
            };
        }

        @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
        public void clear() {
            PyMap.this.methods.get("clear").call(new Object[0]);
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public Set<Map.Entry<PyObject, PyObject>> entrySet() {
        return new AnonymousClass1();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public boolean containsKey(Object obj) {
        return this.methods.get("__contains__").call(obj).toBoolean();
    }

    @Override // java.util.AbstractMap, java.util.Map
    public PyObject get(Object obj) {
        try {
            return this.methods.get("__getitem__").call(obj);
        } catch (PyException e) {
            if (e.getMessage().startsWith("KeyError:")) {
                return null;
            }
            throw e;
        }
    }

    @Override // java.util.AbstractMap, java.util.Map
    public PyObject put(PyObject pyObject, PyObject pyObject2) {
        PyObject pyObject3 = get((Object) pyObject);
        this.methods.get("__setitem__").call(pyObject, pyObject2);
        return pyObject3;
    }

    @Override // java.util.AbstractMap, java.util.Map
    public PyObject remove(Object obj) {
        return this.methods.get("pop").call(obj, null);
    }
}
