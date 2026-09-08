package com.chaquo.python;

import java.util.AbstractList;

class PyList extends AbstractList<PyObject> {
    private final MethodCache methods;
    private final PyObject obj;

    public PyList(PyObject pyObject) {
        this.obj = pyObject;
        MethodCache methodCache = new MethodCache(pyObject);
        this.methods = methodCache;
        methodCache.get("__getitem__");
        methodCache.get("__len__");
    }

    private void checkLowerBound(int i) {
        if (i < 0) {
            throw outOfBounds(i);
        }
    }

    private RuntimeException maybeOutOfBounds(int i, PyException pyException) {
        return pyException.getMessage().startsWith("IndexError:") ? outOfBounds(i) : pyException;
    }

    private IndexOutOfBoundsException outOfBounds(int i) {
        return new IndexOutOfBoundsException("Invalid index " + i + ", size is " + size());
    }

    @Override // java.util.AbstractCollection, java.util.Collection, java.util.List
    public int size() {
        return this.methods.get("__len__").call(new Object[0]).toInt();
    }

    @Override // java.util.AbstractList, java.util.List
    public PyObject get(int i) {
        checkLowerBound(i);
        try {
            return this.methods.get("__getitem__").call(Integer.valueOf(i));
        } catch (PyException e) {
            throw maybeOutOfBounds(i, e);
        }
    }

    @Override // java.util.AbstractList, java.util.List
    public PyObject set(int i, PyObject pyObject) {
        PyObject pyObject2 = get(i);
        this.methods.get("__setitem__").call(Integer.valueOf(i), pyObject);
        return pyObject2;
    }

    @Override // java.util.AbstractList, java.util.List
    public void add(int i, PyObject pyObject) {
        checkLowerBound(i);
        if (i > size()) {
            throw outOfBounds(i);
        }
        this.methods.get("insert").call(Integer.valueOf(i), pyObject);
    }

    @Override // java.util.AbstractList, java.util.List
    public PyObject remove(int i) {
        checkLowerBound(i);
        try {
            return this.methods.get("pop").call(Integer.valueOf(i));
        } catch (PyException e) {
            throw maybeOutOfBounds(i, e);
        }
    }

    @Override // java.util.AbstractList, java.util.AbstractCollection, java.util.Collection, java.util.List
    public void clear() {
        this.methods.get("clear").call(new Object[0]);
    }
}
