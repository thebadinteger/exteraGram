package com.chaquo.python;

import java.util.AbstractSet;
import java.util.Iterator;

class PySet extends AbstractSet<PyObject> {
    private final MethodCache methods;
    private final PyObject obj;

    public PySet(PyObject pyObject) {
        this.obj = pyObject;
        MethodCache methodCache = new MethodCache(pyObject);
        this.methods = methodCache;
        methodCache.get("__contains__");
        methodCache.get("__iter__");
        methodCache.get("__len__");
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public int size() {
        return this.methods.get("__len__").call(new Object[0]).toInt();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean contains(Object obj) {
        return this.methods.get("__contains__").call(obj).toBoolean();
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.lang.Iterable, java.util.Set
    public Iterator<PyObject> iterator() {
        return new PyIterator<PyObject>(this.methods) { // from class: com.chaquo.python.PySet.1
            @Override // com.chaquo.python.PyIterator
            public PyObject makeNext(PyObject pyObject) {
                return pyObject;
            }
        };
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean add(PyObject pyObject) {
        PyObject pyObject2 = this.methods.get("add");
        if (contains(pyObject)) {
            return false;
        }
        pyObject2.call(pyObject);
        return true;
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public boolean remove(Object obj) {
        try {
            this.methods.get("remove").call(obj);
            return true;
        } catch (PyException e) {
            if (e.getMessage().startsWith("KeyError:")) {
                return false;
            }
            throw e;
        }
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.Set
    public void clear() {
        this.methods.get("clear").call(new Object[0]);
    }
}
