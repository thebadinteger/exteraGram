package com.exteragram.messenger.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.TextView;
import com.android.tools.r8.RecordTag;
import java.io.File;
import java.util.Locale;
import java.util.Objects;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.tl.TL_iv;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.VideoPlayer;

public abstract class VideoSubtitlesHelper {

    public enum LoadError {
        NONE,
        UNSUPPORTED_FORMAT,
        LOAD_FAILED
    }

    public static final class SubtitleState extends RecordTag {
        private final String label;
        private final String mimeType;
        private final String path;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof SubtitleState)) {
                return false;
            }
            SubtitleState subtitleState = (SubtitleState) obj;
            return Objects.equals(this.path, subtitleState.path) && Objects.equals(this.mimeType, subtitleState.mimeType) && Objects.equals(this.label, subtitleState.label);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.path, this.mimeType, this.label};
        }

        public SubtitleState(String str, String str2, String str3) {
            this.path = str;
            this.mimeType = str2;
            this.label = str3;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return java.util.Objects.hash(this.path, this.mimeType, this.label);
        }

        public String mimeType() {
            return this.mimeType;
        }

        public String path() {
            return this.path;
        }

        public final String toString() {
            return com.exteragram.messenger.utils.RecordUtils.recordToString($record$getFieldsAsObjects(), SubtitleState.class, "path;mimeType;label");
        }

        public boolean isValid() {
            if (!TextUtils.isEmpty(this.path) && !TextUtils.isEmpty(this.mimeType)) {
                File file = new File(this.path);
                if (file.exists() && file.length() > 0) {
                    return true;
                }
            }
            return false;
        }

        public String getDisplayName() {
            if (!TextUtils.isEmpty(this.label)) {
                return this.label;
            }
            return new File(this.path).getName();
        }

        public VideoPlayer.ExternalSubtitle toExternalSubtitle() {
            return new VideoPlayer.ExternalSubtitle(Uri.fromFile(new File(this.path)), this.mimeType, getDisplayName());
        }
    }

    public static final class SubtitleLoadResult extends RecordTag {
        private final LoadError error;
        private final SubtitleState subtitleState;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof SubtitleLoadResult)) {
                return false;
            }
            SubtitleLoadResult subtitleLoadResult = (SubtitleLoadResult) obj;
            return Objects.equals(this.subtitleState, subtitleLoadResult.subtitleState) && Objects.equals(this.error, subtitleLoadResult.error);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.subtitleState, this.error};
        }

        public SubtitleLoadResult(SubtitleState subtitleState, LoadError loadError) {
            this.subtitleState = subtitleState;
            this.error = loadError;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public LoadError error() {
            return this.error;
        }

        public final int hashCode() {
            return java.util.Objects.hash(this.subtitleState, this.error);
        }

        public SubtitleState subtitleState() {
            return this.subtitleState;
        }

        public final String toString() {
            return com.exteragram.messenger.utils.RecordUtils.recordToString($record$getFieldsAsObjects(), SubtitleLoadResult.class, "subtitleState;error");
        }

        public boolean isSuccess() {
            return this.subtitleState != null;
        }
    }

    public static boolean areSame(SubtitleState subtitleState, SubtitleState subtitleState2) {
        if (subtitleState == subtitleState2) {
            return true;
        }
        return subtitleState != null && subtitleState2 != null && TextUtils.equals(subtitleState.path, subtitleState2.path) && TextUtils.equals(subtitleState.mimeType, subtitleState2.mimeType) && TextUtils.equals(subtitleState.label, subtitleState2.label);
    }

    public static TextView createSubtitlesView(Context context) {
        TextView textView = new TextView(context);
        textView.setGravity(17);
        textView.setTextColor(-1);
        textView.setTextSize(1, AndroidUtilities.isTablet() ? 20.0f : 16.0f);
        textView.setMaxLines(3);
        textView.setPadding(AndroidUtilities.dp(18.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(18.0f), AndroidUtilities.dp(8.0f));
        textView.setBackground(Theme.createRoundRectDrawable(AndroidUtilities.dp(8.0f), -872415232));
        textView.setShadowLayer(AndroidUtilities.dp(2.0f), 0.0f, AndroidUtilities.dp(1.0f), -1728053248);
        textView.setVisibility(8);
        return textView;
    }

    public static String buildVideoKey(MessageObject messageObject, String str, Uri uri, TL_iv.PageBlock pageBlock) {
        if (messageObject != null && messageObject.getDocument() != null) {
            return "doc_" + messageObject.getDocument().id;
        }
        if (!TextUtils.isEmpty(str)) {
            return makePathKey(str);
        }
        if (uri != null) {
            return makeUriKey(uri);
        }
        if (!(pageBlock instanceof TL_iv.pageBlockVideo)) {
            return null;
        }
        return "page_video_" + ((TL_iv.pageBlockVideo) pageBlock).video_id;
    }

    public static SubtitleLoadResult loadFromPickerIntent(Intent intent) {
        return loadFromUri(intent != null ? intent.getData() : null);
    }

    public static Intent createPickerIntent() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.addCategory("android.intent.category.OPENABLE");
        intent.setType("*/*");
        intent.putExtra("android.intent.extra.MIME_TYPES", new String[]{"application/x-subrip", "text/vtt"});
        return intent;
    }

    public static SubtitleLoadResult loadFromUri(Uri uri) {
        String path;
        String strCopyFileToCache;
        if (uri == null) {
            return new SubtitleLoadResult(null, LoadError.LOAD_FAILED);
        }
        String strResolveMimeType = resolveMimeType(uri);
        if (TextUtils.isEmpty(strResolveMimeType)) {
            return new SubtitleLoadResult(null, LoadError.UNSUPPORTED_FORMAT);
        }
        String str = "text/vtt".equals(strResolveMimeType) ? "vtt" : "srt";
        try {
            if ("file".equalsIgnoreCase(uri.getScheme())) {
                strCopyFileToCache = uri.getPath();
            } else {
                path = AndroidUtilities.getPath(uri);
                try {
                    strCopyFileToCache = (TextUtils.isEmpty(path) || path.startsWith("content://")) ? MediaController.copyFileToCache(uri, str) : path;
                } catch (Exception unused) {
                }
            }
        } catch (Exception unused2) {
            path = null;
        }
        if (TextUtils.isEmpty(strCopyFileToCache)) {
            return new SubtitleLoadResult(null, LoadError.LOAD_FAILED);
        }
        File file = new File(strCopyFileToCache);
        if (!file.exists() || file.length() <= 0) {
            return new SubtitleLoadResult(null, LoadError.LOAD_FAILED);
        }
        return new SubtitleLoadResult(new SubtitleState(strCopyFileToCache, strResolveMimeType, file.getName()), LoadError.NONE);
    }

    public static SubtitleState restore(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        SharedPreferences preferences = getPreferences();
        String string = preferences.getString("path_" + str, null);
        String string2 = preferences.getString("mime_" + str, null);
        String string3 = preferences.getString("label_" + str, null);
        if (TextUtils.isEmpty(string) || TextUtils.isEmpty(string2)) {
            return null;
        }
        SubtitleState subtitleState = new SubtitleState(string, string2, string3);
        if (subtitleState.isValid()) {
            return subtitleState;
        }
        clear(str);
        return null;
    }

    public static void save(String str, SubtitleState subtitleState) {
        if (TextUtils.isEmpty(str) || subtitleState == null || !subtitleState.isValid()) {
            return;
        }
        getPreferences().edit().putString("path_" + str, subtitleState.path()).putString("mime_" + str, subtitleState.mimeType()).putString("label_" + str, subtitleState.getDisplayName()).apply();
    }

    public static void clear(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        getPreferences().edit().remove("path_" + str).remove("mime_" + str).remove("label_" + str).apply();
    }

    public static String makeUriKey(Uri uri) {
        if (uri == null) {
            return null;
        }
        return makePathKey(uri.toString());
    }

    public static String makePathKey(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        return "uri_" + Utilities.MD5(str);
    }

    private static String resolveMimeType(Uri uri) {
        String type;
        try {
            type = ApplicationLoader.applicationContext.getContentResolver().getType(uri);
        } catch (Exception unused) {
            type = null;
        }
        if (!TextUtils.isEmpty(type)) {
            String lowerCase = type.toLowerCase(Locale.US);
            if ("text/vtt".equals(lowerCase) || lowerCase.contains("vtt")) {
                return "text/vtt";
            }
            if ("application/x-subrip".equals(lowerCase) || lowerCase.contains("subrip") || lowerCase.contains("srt")) {
                return "application/x-subrip";
            }
        }
        String subtitleExtension = getSubtitleExtension(uri);
        if ("vtt".equals(subtitleExtension)) {
            return "text/vtt";
        }
        if ("srt".equals(subtitleExtension)) {
            return "application/x-subrip";
        }
        return null;
    }

    private static String getSubtitleExtension(Uri uri) {
        String path = AndroidUtilities.getPath(uri);
        if (TextUtils.isEmpty(path)) {
            path = uri.getPath();
        }
        if (TextUtils.isEmpty(path)) {
            path = uri.toString();
        }
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        String lowerCase = path.toLowerCase(Locale.US);
        if (lowerCase.endsWith(".srt")) {
            return "srt";
        }
        if (lowerCase.endsWith(".vtt")) {
            return "vtt";
        }
        return null;
    }

    private static SharedPreferences getPreferences() {
        return ApplicationLoader.applicationContext.getSharedPreferences("video_external_subtitles", 0);
    }
}
