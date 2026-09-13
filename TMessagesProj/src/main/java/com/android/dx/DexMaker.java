package com.android.dx;

import java.io.File;
import java.io.IOException;

public class DexMaker {
    public DexMaker() {}

    public <D> void declare(TypeId<D> type, String comment, int flags,
                            TypeId<? super D> supertype, TypeId<?>... interfaces) {}

    public <D, V> void declare(FieldId<D, V> fieldId, int flags, V staticValue) {}

    public <D, R> Code declare(MethodId<D, R> method, int flags) {
        return new Code();
    }

    public byte[] generate() {
        return new byte[0];
    }

    public ClassLoader generateAndLoad(ClassLoader parent, File dexCache) throws IOException {
        return parent;
    }
}
