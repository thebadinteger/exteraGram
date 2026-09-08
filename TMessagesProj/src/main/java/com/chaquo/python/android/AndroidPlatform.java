package com.chaquo.python.android;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Build;
import com.chaquo.python.Python;
import com.chaquo.python.internal.Common;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import okhttp3.HttpUrl$$ExternalSyntheticBUOutline0;
import okhttp3.internal.url._UrlKt;
import okio.ZipFileSystem$$ExternalSyntheticBUOutline0;
import org.json.JSONException;
import org.json.JSONObject;
import org.mvel2.MVEL$$ExternalSyntheticBUOutline0;

public class AndroidPlatform extends Python.Platform {
    public static String ABI;
    private AssetManager am;
    private JSONObject buildJson;
    public Application mContext;
    private SharedPreferences sp;
    private static final String[] OBSOLETE_FILES = {"app.zip", "requirements.zip", "chaquopy.mp3", "stdlib.mp3", "chaquopy.zip", "lib-dynload", "stdlib.zip", "bootstrap.zip", "stdlib-common.zip", "ticket.txt"};
    private static final String[] OBSOLETE_CACHE = {"AssetFinder"};

    public native void redirectStdioToLogcat();

    public AndroidPlatform(Context context) {
        Application application = (Application) context.getApplicationContext();
        this.mContext = application;
        this.sp = application.getSharedPreferences(Common.ASSET_DIR, 0);
        this.am = this.mContext.getAssets();
        try {
            this.buildJson = new JSONObject(streamToString(this.am.open("chaquopy/build.json")));
            loadNativeLibs();
            for (String str : Build.SUPPORTED_ABIS) {
                try {
                    this.am.open("chaquopy/" + Common.assetZip(Common.ASSET_STDLIB, str));
                    ABI = str;
                    break;
                } catch (IOException unused) {
                }
            }
            if (ABI != null) {
                return;
            }
            throw new RuntimeException("None of this device's ABIs " + Arrays.toString(Build.SUPPORTED_ABIS) + " are supported by this app.");
        } catch (IOException | JSONException e) {
            HttpUrl$$ExternalSyntheticBUOutline0.m(e);
            throw null;
        }
    }

    public Application getApplication() {
        return this.mContext;
    }

    @Override // com.chaquo.python.Python.Platform
    public String getPath() {
        String str = this.mContext.getFilesDir() + "/chaquopy";
        ArrayList arrayList = new ArrayList(Arrays.asList(Common.assetZip(Common.ASSET_STDLIB, Common.ABI_COMMON), Common.assetZip(Common.ASSET_BOOTSTRAP), "bootstrap-native/" + ABI));
        String strConcat = _UrlKt.FRAGMENT_ENCODE_SET;
        for (int i = 0; i < arrayList.size(); i++) {
            strConcat = strConcat + str + "/" + arrayList.get(i);
            if (i < arrayList.size() - 1) {
                strConcat = strConcat.concat(":");
            }
        }
        Collections.addAll(arrayList, Common.ASSET_CACERT);
        try {
            deleteObsolete(this.mContext.getFilesDir(), OBSOLETE_FILES);
            deleteObsolete(this.mContext.getCacheDir(), OBSOLETE_CACHE);
            extractAssets(arrayList);
            return strConcat;
        } catch (IOException | JSONException e) {
            HttpUrl$$ExternalSyntheticBUOutline0.m(e);
            return null;
        }
    }

    private void deleteObsolete(File file, String[] strArr) {
        for (String str : strArr) {
            deleteRecursive(new File(file, "chaquopy/" + str.replace("<abi>", ABI)));
        }
    }

    @Override // com.chaquo.python.Python.Platform
    public void onStart(Python python) {
        python.getModule("java.android").callAttr("initialize", this.mContext, this.buildJson, new String[]{Common.ASSET_APP, Common.ASSET_REQUIREMENTS, "stdlib-" + ABI});
    }

    private void extractAssets(List<String> list) throws JSONException, IOException {
        JSONObject jSONObject = this.buildJson.getJSONObject("assets");
        HashSet hashSet = new HashSet(list);
        HashSet hashSet2 = new HashSet();
        SharedPreferences.Editor editorEdit = this.sp.edit();
        Iterator<String> itKeys = jSONObject.keys();
        while (itKeys.hasNext()) {
            String next = itKeys.next();
            Iterator<String> it = list.iterator();
            while (true) {
                if (it.hasNext()) {
                    String next2 = it.next();
                    if (!next.equals(next2)) {
                        if (next.startsWith(next2 + "/")) {
                        }
                    }
                    extractAsset(jSONObject, editorEdit, next);
                    hashSet.remove(next2);
                    if (next.startsWith(next2 + "/")) {
                        hashSet2.add(next2);
                    }
                }
            }
        }
        if (!hashSet.isEmpty()) {
            MVEL$$ExternalSyntheticBUOutline0.m("Failed to find assets: ", hashSet);
            return;
        }
        Iterator it2 = hashSet2.iterator();
        while (it2.hasNext()) {
            cleanExtractedDir((String) it2.next(), jSONObject);
        }
        editorEdit.apply();
    }

    private void extractAsset(JSONObject jSONObject, SharedPreferences.Editor editor, String str) throws JSONException, IOException {
        String str2 = "chaquopy/" + str;
        File file = new File(this.mContext.getFilesDir(), str2);
        String str3 = "asset." + str;
        String string = jSONObject.getString(str);
        if (file.exists() && this.sp.getString(str3, _UrlKt.FRAGMENT_ENCODE_SET).equals(string)) {
            return;
        }
        file.delete();
        File parentFile = file.getParentFile();
        if (!parentFile.exists()) {
            parentFile.mkdirs();
            if (!parentFile.isDirectory()) {
                ZipFileSystem$$ExternalSyntheticBUOutline0.m("Failed to create ", parentFile);
                return;
            }
        }
        InputStream inputStreamOpen = this.am.open(str2);
        File file2 = new File(parentFile, file.getName() + ".tmp");
        file2.delete();
        FileOutputStream fileOutputStream = new FileOutputStream(file2);
        try {
            transferStream(inputStreamOpen, fileOutputStream);
            fileOutputStream.close();
            if (!file2.renameTo(file)) {
                ZipFileSystem$$ExternalSyntheticBUOutline0.m("Failed to create ", file);
            } else {
                editor.putString(str3, string);
            }
        } catch (Throwable th) {
            fileOutputStream.close();
            throw th;
        }
    }

    private void cleanExtractedDir(String str, JSONObject jSONObject) {
        File file = new File(this.mContext.getFilesDir(), "chaquopy/" + str);
        for (String str2 : file.list()) {
            File file2 = new File(file, str2);
            if (file2.isDirectory()) {
                cleanExtractedDir(str + "/" + str2, jSONObject);
            } else {
                if (!jSONObject.has(str + "/" + str2)) {
                    file2.delete();
                }
            }
        }
    }

    private void deleteRecursive(File file) {
        File[] fileArrListFiles = file.listFiles();
        if (fileArrListFiles != null) {
            for (File file2 : fileArrListFiles) {
                deleteRecursive(file2);
            }
        }
        file.delete();
    }

    private void transferStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[1048576];
        int i = inputStream.read(bArr);
        while (i != -1) {
            outputStream.write(bArr, 0, i);
            i = inputStream.read(bArr);
        }
    }

    private String streamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        while (true) {
            String line = bufferedReader.readLine();
            if (line != null) {
                sb.append(line);
                sb.append("\n");
            } else {
                return sb.toString();
            }
        }
    }

    private void loadNativeLibs() {
        for (String str : Arrays.asList(Common.ASSET_DIR, "python")) {
            System.loadLibrary("crypto_" + str);
            System.loadLibrary("ssl_" + str);
            System.loadLibrary("sqlite3_" + str);
        }
        System.loadLibrary("python" + this.buildJson.getString("python_version"));
        System.loadLibrary("chaquopy_java");
    }
}
