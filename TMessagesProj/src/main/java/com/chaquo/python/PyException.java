package com.chaquo.python;

public class PyException extends RuntimeException {
    public PyException() {
    }

    public PyException(String str) {
        super(str);
    }

    public PyException(String str, Throwable th) {
        super(str, th);
    }

    public PyException(Throwable th) {
        super(th);
    }
}
