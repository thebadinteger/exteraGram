package com.chaquo.python;

public interface PyProxy {
    PyObject _chaquopyGetDict();

    void _chaquopySetDict(PyObject pyObject);
}
