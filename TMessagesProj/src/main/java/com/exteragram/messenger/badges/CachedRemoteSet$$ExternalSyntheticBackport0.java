package com.exteragram.messenger.badges;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public abstract /* synthetic */ class CachedRemoteSet$$ExternalSyntheticBackport0 {
    public static /* synthetic */ Set m(Collection collection) {
        HashSet hashSet = new HashSet(collection.size());
        for (Object obj : collection) {
            Objects.requireNonNull(obj);
            hashSet.add(obj);
        }
        return Collections.unmodifiableSet(hashSet);
    }
}
