package com.exteragram.messenger.debug;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import androidx.core.content.FileProvider;
import java.io.File;
import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u001c\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0000\u001a(\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00012\u0006\u0010\t\u001a\u00020\u00032\u0006\u0010\n\u001a\u00020\u0003H\u0000¨\u0006\u000b"}, d2 = {"debugFilesDir", "Ljava/io/File;", "name", _UrlKt.FRAGMENT_ENCODE_SET, "shareDebugFile", _UrlKt.FRAGMENT_ENCODE_SET, "activity", "Landroid/app/Activity;", "file", "mimeType", "title", "TMessagesProj"}, k = 2, mv = {2, 2, 0}, xi = 48)
public abstract class DebugFilesKt {
    public static final File debugFilesDir(String str) {
        Context context = ApplicationLoader.applicationContext;
        File externalFilesDir = context.getExternalFilesDir(null);
        if (externalFilesDir == null) {
            externalFilesDir = context.getCacheDir();
        }
        return new File(externalFilesDir, str);
    }

    public static final void shareDebugFile(Activity activity, File file, String str, String str2) {
        try {
            activity.startActivity(Intent.createChooser(new Intent("android.intent.action.SEND").setType(str).putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(activity, ApplicationLoader.getApplicationId() + ".provider", file)).addFlags(1), str2));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }
}
