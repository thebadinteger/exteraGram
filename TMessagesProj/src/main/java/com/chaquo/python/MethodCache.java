package com.chaquo.python;

import java.util.HashMap;
import java.util.Map;

class MethodCache {
    private Map<String, PyObject> cache = new HashMap();
    private PyObject obj;

    public MethodCache(PyObject pyObject) {
        this.obj = pyObject;
    }

    public PyObject get(String str) {
        PyObject pyObject = this.cache.get(str);
        if (pyObject != null) {
            return pyObject;
        }
        PyObject pyObject2 = this.obj.get((Object) str);
        if (pyObject2 == null) {
            throw new UnsupportedOperationException(String.format("'%s' object has no attribute '%s'", this.obj.type().get((Object) "__name__"), str));
        }
        this.cache.put(str, pyObject2);
        return pyObject2;
    }
}
