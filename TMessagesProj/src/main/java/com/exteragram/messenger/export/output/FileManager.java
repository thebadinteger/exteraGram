package com.exteragram.messenger.export.output;

import android.content.res.AssetManager;
import android.os.Environment;
import android.util.Log;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import kotlin.text.Typography;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

public abstract class FileManager {
    public static final File defaultSavePath = new File(new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "exteraGram"), "DataExport_" + new SimpleDateFormat("yyyy-MM-dd_HH_mm").format(Calendar.getInstance().getTime()));

    public static void copyAssets() {
        copyAssets("js", false);
        copyAssets("css", false);
        copyAssets("images", true);
    }

    public static void copyAssets(String str, boolean z) {
        String[] list;
        AssetManager assets = ApplicationLoader.applicationContext.getAssets();
        try {
            list = assets.list("extera/export/" + str + "/");
        } catch (IOException e) {
            Log.e("exteraGram", "Failed to get asset file list", e);
            list = null;
        }
        if (list == null) {
            FileLog.e("export: failed to copy assets");
            return;
        }
        for (String str2 : list) {
            try {
                InputStream inputStreamOpen = assets.open("extera/export/" + str + "/" + str2);
                File file = new File(defaultSavePath + "/" + str + "/", str2);
                file.createNewFile();
                AndroidUtilities.copyFile(inputStreamOpen, new FileOutputStream(file));
            } catch (IOException e2) {
                Log.e("exteraGram", "Failed to copy asset file: " + str2, e2);
                Make$Map$$ExternalSyntheticBUOutline0.m("exteraGram assets exception: ", e2);
                return;
            }
        }
        if (z) {
            AndroidUtilities.createEmptyFile(new File(defaultSavePath + "/" + str, ".nomedia"));
        }
    }

    public static String fileNameFromUserString(String str) {
        HashSet hashSet = new HashSet();
        hashSet.add((char) 8206);
        hashSet.add((char) 8207);
        hashSet.add((char) 8234);
        hashSet.add((char) 8235);
        hashSet.add((char) 8237);
        hashSet.add((char) 8238);
        hashSet.add((char) 8294);
        hashSet.add((char) 8295);
        hashSet.add('/');
        hashSet.add('\\');
        hashSet.add(Character.valueOf(Typography.less));
        hashSet.add(Character.valueOf(Typography.greater));
        hashSet.add(':');
        hashSet.add(Character.valueOf(Typography.quote));
        hashSet.add('|');
        hashSet.add('?');
        hashSet.add('*');
        StringBuilder sb = new StringBuilder();
        for (char c2 : str.toCharArray()) {
            if (c2 < ' ' || hashSet.contains(Character.valueOf(c2))) {
                sb.append('_');
            } else {
                sb.append(c2);
            }
        }
        if (sb.length() == 0 || sb.charAt(sb.length() - 1) == ' ' || sb.charAt(sb.length() - 1) == '.') {
            sb.append('_');
        }
        return sb.toString();
    }

    public static String readFileContent(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuilder sb = new StringBuilder();
            while (true) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    sb.append(line);
                } else {
                    bufferedReader.close();
                    fileInputStream.close();
                    return sb.toString();
                }
            }
        } catch (Exception e) {
            Log.e("exteraGram", "failed to read: " + e);
            return null;
        }
    }
}
