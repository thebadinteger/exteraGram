package com.exteragram.messenger.preferences;

import com.sun.jna.Native$$ExternalSyntheticBUOutline5;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract /* synthetic */ class OtherPreferencesActivity$$ExternalSyntheticBackport1 {
    public static /* synthetic */ Map m(Map.Entry[] entryArr) {
        HashMap map = new HashMap(entryArr.length);
        for (Map.Entry entry : entryArr) {
            Object key = entry.getKey();
            Objects.requireNonNull(key);
            Object value = entry.getValue();
            Objects.requireNonNull(value);
            if (map.put(key, value) != null) {
                Native$$ExternalSyntheticBUOutline5.m("duplicate key: ", key);
                return null;
            }
        }
        return Collections.unmodifiableMap(map);
    }
}
