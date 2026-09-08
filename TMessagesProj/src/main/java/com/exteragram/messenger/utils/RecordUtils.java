package com.exteragram.messenger.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class RecordUtils {
    private RecordUtils() {}

    public static String recordToString(Object[] objArr, Class<?> cls, String str) {
        String[] strArrSplit = str.isEmpty() ? new String[0] : str.split(";");
        StringBuilder sb = new StringBuilder();
        sb.append(cls.getSimpleName());
        sb.append("[");
        for (int i = 0; i < strArrSplit.length; i++) {
            sb.append(strArrSplit[i]);
            sb.append("=");
            sb.append(i < objArr.length ? objArr[i] : "null");
            if (i != strArrSplit.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static String repeat(String str, int count) {
        if (str == null || count <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder(str.length() * count);
        for (int i = 0; i < count; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    @SafeVarargs
    public static <K, V> Map<K, V> mapOf(Map.Entry<? extends K, ? extends V>... entries) {
        if (entries == null || entries.length == 0) {
            return Collections.emptyMap();
        }
        Map<K, V> map = new HashMap<>(entries.length);
        for (Map.Entry<? extends K, ? extends V> entry : entries) {
            if (entry != null) {
                map.put(entry.getKey(), entry.getValue());
            }
        }
        return Collections.unmodifiableMap(map);
    }
}
