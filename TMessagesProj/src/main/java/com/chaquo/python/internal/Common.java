package com.chaquo.python.internal;

import com.sun.jna.Native$$ExternalSyntheticBUOutline0;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.mvel2.MVEL$$ExternalSyntheticBUOutline0;

public class Common {
    public static final String ABI_COMMON = "common";
    public static final String ASSET_APP = "app";
    public static final String ASSET_BOOTSTRAP = "bootstrap";
    public static final String ASSET_BOOTSTRAP_NATIVE = "bootstrap-native";
    public static final String ASSET_BUILD_JSON = "build.json";
    public static final String ASSET_CACERT = "cacert.pem";
    public static final String ASSET_DIR = "chaquopy";
    public static final String ASSET_REQUIREMENTS = "requirements";
    public static final String ASSET_STDLIB = "stdlib";
    public static final int COMPILE_SDK_VERSION = 36;
    public static final String DEFAULT_PYTHON_VERSION = "3.11";
    public static final String MIN_AGP_VERSION = "7.3.0";
    public static final int MIN_SDK_VERSION = 24;
    public static final Map<String, String> PYTHON_VERSIONS;
    public static List<String> PYTHON_VERSIONS_SHORT;

    static {
        LinkedHashMap<String, String> linkedHashMap = new LinkedHashMap<>();
        PYTHON_VERSIONS = linkedHashMap;
        linkedHashMap.put("3.11.10", "1");
        PYTHON_VERSIONS_SHORT = new ArrayList();
        for (String str : linkedHashMap.keySet()) {
            PYTHON_VERSIONS_SHORT.add(str.substring(0, str.lastIndexOf(46)));
        }
    }

    public static List<String> supportedAbis(String str) {
        if (!PYTHON_VERSIONS_SHORT.contains(str)) {
            throw new IllegalArgumentException("Unknown Python version: '" + str + "'");
        }
        ArrayList arrayList = new ArrayList();
        arrayList.add("arm64-v8a");
        arrayList.add("x86_64");
        if (Arrays.asList("3.8", "3.9", "3.10", DEFAULT_PYTHON_VERSION).contains(str)) {
            arrayList.add("armeabi-v7a");
            arrayList.add("x86");
        }
        arrayList.sort(null);
        return arrayList;
    }

    public static String assetZip(String str) {
        return assetZip(str, null);
    }

    public static String assetZip(String str, String str2) {
        if (str2 == null) {
            return str + ".imy";
        }
        return str + "-" + str2 + ".imy";
    }

    public static String osName() {
        String property = System.getProperty("os.name");
        String[] strArr = {"linux", "mac", "windows"};
        for (int i = 0; i < 3; i++) {
            String str = strArr[i];
            if (property.toLowerCase(Locale.ENGLISH).startsWith(str)) {
                return str;
            }
        }
        MVEL$$ExternalSyntheticBUOutline0.m("unknown os.name: ", property);
        return null;
    }
}
