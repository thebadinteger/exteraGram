package com.chaquo.python;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class PyInvocationHandler implements InvocationHandler {
    private PyObject dict;
    private PyObject type;

    public PyInvocationHandler(PyObject pyObject) {
        this.type = pyObject;
    }

    @Override // java.lang.reflect.InvocationHandler
    public Object invoke(Object obj, Method method, Object[] objArr) {
        String name = method.getName();
        name.getClass();
        switch (name) {
            case "_chaquopySetDict":
                this.dict = (PyObject) objArr[0];
                return null;
            case "_chaquopyGetDict":
                return this.dict;
            case "_chaquopyGetType":
                return this.type;
            default:
                PyObject pyObjectFromJava = PyObject.fromJava(obj);
                if (objArr == null) {
                    objArr = new Object[0];
                }
                PyObject pyObjectCallAttrThrows = pyObjectFromJava.callAttrThrows(name, objArr);
                if (pyObjectCallAttrThrows == null) {
                    return null;
                }
                return pyObjectCallAttrThrows.toJava(method.getReturnType());
        }
    }
}
