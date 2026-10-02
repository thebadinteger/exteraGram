package com.exteragram.messenger.ai.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.utils.AppUtils;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.ui.LaunchActivity;

/* JADX INFO: loaded from: classes4.dex */
public abstract class OnDeviceModelNotifications {
    public static void showProgress(int i, int i2) {
        NotificationCompat.Builder timeoutAfter = createBuilder(i).setContentTitle(LocaleController.getString(R.string.DownloadingModel)).setSilent(true).setOngoing(false).setTimeoutAfter(300000L);
        if (i2 >= 0) {
            timeoutAfter.setContentText(i2 + "%");
            timeoutAfter.setProgress(100, i2, false);
        } else {
            timeoutAfter.setProgress(0, 0, true);
        }
        notify(i, timeoutAfter);
    }

    public static void showCompleted(int i) {
        notify(i, createBuilder(i).setContentTitle(LocaleController.getString(R.string.AIOnDeviceNotificationReady)).setContentText(LocaleController.getString(R.string.AIOnDeviceNotificationReadyInfo)).setAutoCancel(true));
    }

    public static void cancel(int i) {
        try {
            NotificationManagerCompat.from(ApplicationLoader.applicationContext).cancel(i + 8732834);
        } catch (Exception e) {
            FileLog.e("AI_ON_DEVICE_NOTIFICATION", e);
        }
    }

    private static NotificationCompat.Builder createBuilder(int i) {
        Context context = ApplicationLoader.applicationContext;
        return new NotificationCompat.Builder(context, "ai_on_device_model").setSmallIcon(IconManager.getNotificationIcon()).setColor(AppUtils.getNotificationColor()).setShowWhen(false).setCategory("progress").setSubText(getModelLabel(i)).setContentIntent(PendingIntent.getActivity(context, 0, new Intent(context, (Class<?>) LaunchActivity.class).setFlags(268435456), 201326592));
    }

    private static String getModelLabel(int i) {
        int i2 = i & 2;
        if (i2 == 0 || (i & 1) == 0) {
            if (i2 != 0) {
                return LocaleController.getString(R.string.AIOnDevicePreviewModel);
            }
            if ((i & 1) != 0) {
                return LocaleController.getString(R.string.AIOnDeviceFastModel);
            }
            return null;
        }
        return LocaleController.getString(R.string.AIOnDevicePreviewModel) + ", " + LocaleController.getString(R.string.AIOnDeviceFastModel);
    }

    private static void notify(int i, NotificationCompat.Builder builder) {
        try {
            NotificationManagerCompat notificationManagerCompatFrom = NotificationManagerCompat.from(ApplicationLoader.applicationContext);
            notificationManagerCompatFrom.createNotificationChannel(new NotificationChannelCompat.Builder("ai_on_device_model", 3).setName(LocaleController.getString(R.string.AIOnDeviceNotificationChannel)).setLightsEnabled(false).setVibrationEnabled(false).build());
            notificationManagerCompatFrom.notify(i + 8732834, builder.build());
        } catch (Exception e) {
            FileLog.e("AI_ON_DEVICE_NOTIFICATION", e);
        }
    }
}
