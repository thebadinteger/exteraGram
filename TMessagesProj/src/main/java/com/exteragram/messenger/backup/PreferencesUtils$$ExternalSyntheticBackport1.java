package com.exteragram.messenger.backup;

import com.sun.jna.Native$$ExternalSyntheticBUOutline5;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract /* synthetic */ class PreferencesUtils$$ExternalSyntheticBackport1 {
    public static /* synthetic */ Set m(Object[] objArr) {
        HashSet hashSet = new HashSet(objArr.length);
        for (Object obj : objArr) {
            Objects.requireNonNull(obj);
            if (!hashSet.add(obj)) {
                Native$$ExternalSyntheticBUOutline5.m("duplicate element: ", obj);
                return null;
            }
        }
        return Collections.unmodifiableSet(hashSet);
    }
}
