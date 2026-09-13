package com.sun.jna;

public class Pointer {
    public static final Pointer NULL = null;
    protected long peer;

    public Pointer() {}
    public Pointer(long peer) { this.peer = peer; }
}
