package com.exteragram.messenger.utils.system;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.biometrics.BiometricManager;
import android.hardware.fingerprint.FingerprintManager;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.net.Uri;
import android.os.Build;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.exteragram.messenger.utils.network.RemoteUtils;
import com.exteragram.messenger.utils.ui.UIUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;

public abstract class SystemUtils {
    private static final List<ExternalApp> externalApps = new ArrayList();
    public static Boolean isLensAvailable = null;
    public static String lensActivityName;
    private static volatile int videoEncoderAlignment;

    public static boolean isPermissionGranted(String str) {
        return ApplicationLoader.applicationContext.checkSelfPermission(str) == 0;
    }

    public static void requestPermissions(Activity activity, int i, String... strArr) {
        if (activity == null) {
            return;
        }
        activity.requestPermissions(strArr, i);
    }

    public static boolean isVideoPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 33) {
            return isPermissionGranted("android.permission.READ_MEDIA_VIDEO");
        }
        return isStoragePermissionGranted();
    }

    public static boolean isImagesAndVideoPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 33) {
            return isImagesPermissionGranted() && isVideoPermissionGranted();
        }
        return isStoragePermissionGranted();
    }

    public static void requestImagesAndVideoPermission(Activity activity) {
        requestImagesAndVideoPermission(activity, 4);
    }

    public static void requestImagesAndVideoPermission(Activity activity, int i) {
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(activity, i, "android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO");
        } else {
            requestPermissions(activity, i, "android.permission.READ_EXTERNAL_STORAGE");
        }
    }

    public static boolean isImagesPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 33) {
            return isPermissionGranted("android.permission.READ_MEDIA_IMAGES");
        }
        return isStoragePermissionGranted();
    }

    public static void requestImagesPermission(Activity activity) {
        requestImagesPermission(activity, 4);
    }

    public static void requestImagesPermission(Activity activity, int i) {
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(activity, i, "android.permission.READ_MEDIA_IMAGES");
        } else {
            requestPermissions(activity, i, "android.permission.READ_EXTERNAL_STORAGE");
        }
    }

    public static boolean isAudioPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 33) {
            return isPermissionGranted("android.permission.READ_MEDIA_AUDIO");
        }
        return isStoragePermissionGranted();
    }

    public static void requestAudioPermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(activity, 4, "android.permission.READ_MEDIA_AUDIO");
        } else {
            requestPermissions(activity, 4, "android.permission.READ_EXTERNAL_STORAGE");
        }
    }

    public static boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 33) {
            return isImagesPermissionGranted() && isVideoPermissionGranted() && isAudioPermissionGranted();
        }
        return isPermissionGranted("android.permission.READ_EXTERNAL_STORAGE");
    }

    public static void requestStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 33) {
            requestPermissions(activity, 4, "android.permission.READ_MEDIA_IMAGES", "android.permission.READ_MEDIA_VIDEO", "android.permission.READ_MEDIA_AUDIO");
        } else {
            requestPermissions(activity, 4, "android.permission.READ_EXTERNAL_STORAGE");
        }
    }

    public static boolean hasBiometrics() {
        int i = Build.VERSION.SDK_INT;
        if (i >= 29) {
            BiometricManager biometricManagerM = (BiometricManager) ApplicationLoader.applicationContext.getSystemService(BiometricManager.class);
            if (biometricManagerM == null) {
                return false;
            }
            if (i >= 30) {
                return biometricManagerM.canAuthenticate(255) == 0;
            }
            return biometricManagerM.canAuthenticate() == 0;
        }
        FingerprintManager fingerprintManager = (FingerprintManager) ApplicationLoader.applicationContext.getSystemService(FingerprintManager.class);
        return fingerprintManager != null && fingerprintManager.isHardwareDetected() && fingerprintManager.hasEnrolledFingerprints();
    }

    public static File getFileFromBitmap(Bitmap bitmap) throws IOException {
        File file = new File(AndroidUtilities.getCacheDir(), "temp.jpeg");
        file.createNewFile();
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.close();
            return file;
        } catch (Throwable th) {
            try {
                fileOutputStream.close();
            } catch (Throwable th2) {
                th.addSuppressed(th2);
            }
            throw th;
        }
    }

    public static void addFileToClipboard(File file, Runnable runnable) {
        try {
            Context context = ApplicationLoader.applicationContext;
            ((ClipboardManager) context.getSystemService("clipboard")).setPrimaryClip(ClipData.newUri(context.getContentResolver(), "label", FileProvider.getUriForFile(context, ApplicationLoader.getApplicationId() + ".provider", file)));
            runnable.run();
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public static boolean isAppInstalled(String str) {
        try {
            ApplicationLoader.applicationContext.getPackageManager().getApplicationInfo(str, 0);
            return true;
        } catch (PackageManager.NameNotFoundException unused) {
            return false;
        }
    }

    public static boolean isLensAvailable() {
        if (isLensAvailable == null) {
            try {
                checkLensAvailability();
            } catch (Exception e) {
                FileLog.e(e);
                isLensAvailable = Boolean.FALSE;
            }
        }
        return isLensAvailable.booleanValue();
    }

    public static void checkLensAvailability() {
        Iterator<ResolveInfo> it = ApplicationLoader.applicationContext.getPackageManager().queryIntentActivities(new Intent("android.intent.action.SEND").setDataAndType(Uri.parse("content://" + ApplicationLoader.getApplicationId() + ".provider"), "image/jpeg").setPackage("com.google.android.googlequicksearchbox"), 0).iterator();
        while (it.hasNext()) {
            String str = it.next().activityInfo.name;
            if (str.contains("Lens")) {
                isLensAvailable = Boolean.TRUE;
                lensActivityName = str;
                break;
            }
        }
        if (isLensAvailable == null) {
            isLensAvailable = Boolean.FALSE;
        }
    }

    public static void shareImageWithGoogleLens(Activity activity, Uri uri) {
        try {
            activity.startActivity(new Intent("android.intent.action.SEND").addFlags(1).setClassName("com.google.android.googlequicksearchbox", lensActivityName).setDataAndType(uri, "image/jpeg").putExtra("android.intent.extra.STREAM", uri));
        } catch (ActivityNotFoundException e) {
            FileLog.e(e);
        }
    }

    public static int getRoundVideoResolution() {
        int iIntValue = RemoteUtils.getIntConfigValue("round_video_resolution", 512).intValue();
        int videoEncoderAlignment2 = getVideoEncoderAlignment();
        return (videoEncoderAlignment2 <= 1 || iIntValue % videoEncoderAlignment2 == 0) ? iIntValue : Math.max(videoEncoderAlignment2, (iIntValue / videoEncoderAlignment2) * videoEncoderAlignment2);
    }

    private static int getVideoEncoderAlignment() {
        int i = videoEncoderAlignment;
        if (i != 0) {
            return i;
        }
        MediaCodec mediaCodec = null;
        int iMax = 1;
        try {
            MediaCodec mediaCodecCreateEncoderByType = MediaCodec.createEncoderByType(MediaController.VIDEO_MIME_TYPE);
            try {
                MediaCodecInfo.CodecCapabilities capabilitiesForType = mediaCodecCreateEncoderByType.getCodecInfo().getCapabilitiesForType(MediaController.VIDEO_MIME_TYPE);
                MediaCodecInfo.VideoCapabilities videoCapabilities = capabilitiesForType != null ? capabilitiesForType.getVideoCapabilities() : null;
                iMax = videoCapabilities != null ? Math.max(1, Math.max(videoCapabilities.getWidthAlignment(), videoCapabilities.getHeightAlignment())) : 1;
            } finally {
                mediaCodecCreateEncoderByType.release();
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
        videoEncoderAlignment = iMax;
        return iMax;
    }

    public static int getRoundVideoBitrate() {
        return RemoteUtils.getIntConfigValue("round_video_bitrate", 1000).intValue();
    }

    public static int getRoundAudioBitrate() {
        return RemoteUtils.getIntConfigValue("round_audio_bitrate", 64).intValue();
    }

    public static long getRoundVideoMaxDurationMs() {
        return Utilities.clamp(83886080000L / (((long) (Math.max(1, getRoundVideoBitrate()) + Math.max(0, getRoundAudioBitrate()))) * 1024), 60000L, 10000L);
    }

    public static List<ExternalApp> getExternalApps() {
        updateExternalApps();
        return externalApps;
    }

    private static void updateExternalApps() {
        List<ExternalApp> list = externalApps;
        if (!list.isEmpty()) {
            list.clear();
        }
        Iterator<ResolveInfo> it = ApplicationLoader.applicationContext.getPackageManager().queryIntentActivities(new Intent("android.intent.action.VIEW", Uri.parse("tel:00000000000")), 0).iterator();
        while (it.hasNext()) {
            ActivityInfo activityInfo = it.next().activityInfo;
            addExternalApp(activityInfo.packageName, activityInfo.name);
        }
    }

    private static void addExternalApp(String str, String str2) {
        if (isAppInstalled(str)) {
            externalApps.add(new ExternalApp(str, str2));
        }
    }

    public static class ExternalApp {
        private final String activityName;
        private final Drawable appIcon = fetchIcon();
        private final String appName = fetchName();
        private final String packageName;

        public ExternalApp(String str, String str2) {
            this.packageName = str;
            this.activityName = str2;
        }

        public String getName() {
            return this.appName;
        }

        public Drawable getIcon() {
            return this.appIcon;
        }

        private String fetchName() {
            String string;
            PackageManager packageManager = ApplicationLoader.applicationContext.getPackageManager();
            ApplicationInfo applicationInfo = null;
            try {
                applicationInfo = packageManager.getApplicationInfo(this.packageName, 0);
                string = null;
            } catch (Exception unused) {
                string = LocaleController.getString(R.string.NumberUnknown);
            }
            return applicationInfo == null ? string : (String) packageManager.getApplicationLabel(applicationInfo);
        }

        private Drawable fetchIcon() {
            try {
                return new BitmapDrawable(ApplicationLoader.applicationContext.getResources(), UIUtil.drawableToBitmap(ApplicationLoader.applicationContext.getPackageManager().getApplicationIcon(this.packageName), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(24.0f)));
            } catch (Exception unused) {
                return ContextCompat.getDrawable(ApplicationLoader.applicationContext, R.drawable.msg_media);
            }
        }

        public void open(Activity activity, String str) {
            try {
                Intent intent = new Intent("android.intent.action.VIEW", Uri.parse("tel:" + str));
                intent.setClassName(this.packageName, this.activityName);
                intent.addFlags(268435456);
                activity.startActivityForResult(intent, 500);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }
}
