package com.exteragram.messenger.export.ui;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import java.io.File;
import okhttp3.internal.url._UrlKt;
import org.webrtc.MediaStreamTrack;

public abstract class AndroidPickerUtils {
    public static String getPath(Context context, Uri uri) {
        Long lValueOf;
        Uri uri2 = null;
        if (DocumentsContract.isDocumentUri(context, uri)) {
            boolean zIsExternalStorageDocument = isExternalStorageDocument(uri);
            String str = _UrlKt.FRAGMENT_ENCODE_SET;
            if (zIsExternalStorageDocument) {
                String documentId = DocumentsContract.getDocumentId(uri);
                String[] strArrSplit = documentId.split(":");
                String str2 = strArrSplit[0];
                if ("primary".equalsIgnoreCase(str2)) {
                    if (strArrSplit.length > 1) {
                        return Environment.getExternalStorageDirectory() + "/" + strArrSplit[1];
                    }
                    return Environment.getExternalStorageDirectory() + "/";
                }
                if ("home".equalsIgnoreCase(str2)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(Environment.getExternalStorageDirectory());
                    sb.append("/Documents/");
                    if (strArrSplit.length > 1) {
                        str = strArrSplit[1];
                    }
                    sb.append(str);
                    return sb.toString();
                }
                if (new File("storage/" + documentId.replace(":", "/")).exists()) {
                    return "/storage/" + documentId.replace(":", "/");
                }
                for (String str3 : AndroidSDUtils.getStorageDirectories(context)) {
                    str = strArrSplit[1].startsWith("/") ? str3 + strArrSplit[1] : str3 + "/" + strArrSplit[1];
                }
                if (str.contains(str2)) {
                    return "storage/" + documentId.replace(":", "/");
                }
                if (str.startsWith("/storage/") || str.startsWith("storage/")) {
                    return str;
                }
                if (str.startsWith("/")) {
                    return "/storage".concat(str);
                }
                return "/storage/".concat(str);
            }
            try {
                if (isDownloadsDocument(uri)) {
                    try {
                        String documentId2 = DocumentsContract.getDocumentId(uri);
                        if (documentId2.startsWith("raw:")) {
                            documentId2 = documentId2.replaceFirst("raw:", _UrlKt.FRAGMENT_ENCODE_SET);
                            if (new File(documentId2).exists()) {
                                return documentId2;
                            }
                        }
                        if (documentId2.startsWith("raw%3A%2F")) {
                            documentId2 = documentId2.replaceFirst("raw%3A%2F", _UrlKt.FRAGMENT_ENCODE_SET);
                            if (new File(documentId2).exists()) {
                                return documentId2;
                            }
                        }
                        lValueOf = Long.valueOf(documentId2);
                    } catch (NumberFormatException unused) {
                        lValueOf = Long.valueOf(ContentUris.parseId(uri));
                    }
                    return getDataColumn(context, ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), lValueOf.longValue()), null, null);
                }
                if (isMediaDocument(uri)) {
                    String[] strArrSplit2 = DocumentsContract.getDocumentId(uri).split(":");
                    String str4 = strArrSplit2[0];
                    if ("image".equals(str4)) {
                        uri2 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if (MediaStreamTrack.VIDEO_TRACK_KIND.equals(str4)) {
                        uri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if (MediaStreamTrack.AUDIO_TRACK_KIND.equals(str4)) {
                        uri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    return getDataColumn(context, uri2, "_id=?", new String[]{strArrSplit2[1]});
                }
                if ("file".equalsIgnoreCase(uri.getScheme())) {
                    return uri.getPath();
                }
            } catch (Exception unused2) {
                return null;
            }
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String str, String[] strArr) throws Throwable {
        Throwable th;
        Cursor cursor = null;
        try {
            Cursor cursorQuery = context.getContentResolver().query(uri, new String[]{"_data"}, str, strArr, null);
            if (cursorQuery != null) {
                try {
                    if (cursorQuery.moveToFirst()) {
                        String string = cursorQuery.getString(cursorQuery.getColumnIndexOrThrow("_data"));
                        cursorQuery.close();
                        return string;
                    }
                } catch (Throwable th2) {
                    th = th2;
                    cursor = cursorQuery;
                    if (cursor != null) {
                        cursor.close();
                        throw th;
                    }
                    throw th;
                }
            }
            if (cursorQuery != null) {
                cursorQuery.close();
            }
            return null;
        } catch (Throwable th3) {
            th = th3;
        }
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
