package com.exteragram.messenger.export.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import okhttp3.internal.url._UrlKt;

public abstract class AndroidSDUtils {
    private static final String EXTERNAL_STORAGE = System.getenv("EXTERNAL_STORAGE");
    private static final String SECONDARY_STORAGES = System.getenv("SECONDARY_STORAGE");
    private static final String EMULATED_STORAGE_TARGET = System.getenv("EMULATED_STORAGE_TARGET");

    @SuppressLint({"SdCardPath"})
    private static final String[] KNOWN_PHYSICAL_PATHS = {"/storage/sdcard0", "/storage/sdcard1", "/storage/extsdcard", "/storage/sdcard0/external_sdcard", "/mnt/extsdcard", "/mnt/sdcard/external_sd", "/mnt/sdcard/ext_sd", "/mnt/external_sd", "/mnt/media_rw/sdcard1", "/removable/microsd", "/mnt/emmc", "/storage/external_SD", "/storage/ext_sd", "/storage/removable/sdcard1", "/data/sdext", "/data/sdext2", "/data/sdext3", "/data/sdext4", "/sdcard1", "/sdcard2", "/storage/microsd"};

    public static String[] getStorageDirectories(Context context) {
        HashSet hashSet = new HashSet();
        if (!TextUtils.isEmpty(EMULATED_STORAGE_TARGET)) {
            hashSet.add(getEmulatedStorageTarget());
        } else {
            hashSet.addAll(getExternalStorage(context));
        }
        Collections.addAll(hashSet, getAllSecondaryStorages());
        return (String[]) hashSet.toArray(new String[hashSet.size()]);
    }

    private static Set<String> getExternalStorage(Context context) {
        HashSet hashSet = new HashSet();
        for (File file : getExternalFilesDirs(context)) {
            if (file != null) {
                String absolutePath = file.getAbsolutePath();
                String strSubstring = absolutePath.substring(9, absolutePath.indexOf("Android/data"));
                String strSubstring2 = strSubstring.substring(strSubstring.indexOf("/storage/") + 1);
                String strSubstring3 = strSubstring2.substring(0, strSubstring2.indexOf("/"));
                if (!strSubstring3.equals("emulated")) {
                    hashSet.add(strSubstring3);
                }
            }
        }
        return hashSet;
    }

    private static String getEmulatedStorageTarget() {
        String absolutePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String str = File.separator;
        String[] strArrSplit = absolutePath.split(str);
        String str2 = strArrSplit[strArrSplit.length - 1];
        if (TextUtils.isEmpty(str2) || !TextUtils.isDigitsOnly(str2)) {
            str2 = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        if (TextUtils.isEmpty(str2)) {
            return EMULATED_STORAGE_TARGET;
        }
        return EMULATED_STORAGE_TARGET + str + str2;
    }

    private static String[] getAllSecondaryStorages() {
        String str = SECONDARY_STORAGES;
        if (!TextUtils.isEmpty(str)) {
            return str.split(File.pathSeparator);
        }
        return new String[0];
    }

    private static File[] getExternalFilesDirs(Context context) {
        return context.getExternalFilesDirs(null);
    }
}
