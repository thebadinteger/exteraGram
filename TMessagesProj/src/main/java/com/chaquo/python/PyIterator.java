package com.chaquo.python;

import java.util.Iterator;
import retrofit2.Utils$$ExternalSyntheticBUOutline0;

abstract class PyIterator<T> implements Iterator<T> {
    private boolean hasNextElem = true;
    private PyObject iter;
    private PyObject nextElem;

    public abstract T makeNext(PyObject pyObject);

    public PyIterator(MethodCache methodCache) {
        this.iter = methodCache.get("__iter__").call(new Object[0]);
        updateNext();
    }

    public void updateNext() {
        try {
            this.nextElem = this.iter.callAttr("__next__", new Object[0]);
        } catch (PyException e) {
            if (e.getMessage().startsWith("StopIteration:")) {
                this.hasNextElem = false;
                this.nextElem = null;
                return;
            }
            throw e;
        }
    }

    @Override // java.util.Iterator
    public boolean hasNext() {
        return this.hasNextElem;
    }

    @Override // java.util.Iterator
    public T next() {
        if (!hasNext()) {
            Utils$$ExternalSyntheticBUOutline0.m();
            return null;
        }
        T tMakeNext = makeNext(this.nextElem);
        updateNext();
        return tMakeNext;
    }

    @Override // java.util.Iterator
    public void remove() {
        throw new UnsupportedOperationException("Python does not support removing from a container while iterating over it");
    }
}
