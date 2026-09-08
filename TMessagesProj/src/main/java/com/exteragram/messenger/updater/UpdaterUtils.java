package com.exteragram.messenger.updater;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInstaller;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.arch.core.util.Function;
import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import com.exteragram.messenger.badges.BadgesController;
import com.exteragram.messenger.icons.IconManager;
import com.exteragram.messenger.utils.AppUtils;
import com.exteragram.messenger.utils.network.RemoteUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.XiaomiUtilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.StickerImageView;
import org.telegram.ui.LaunchActivity;

public abstract class UpdaterUtils {

    @SuppressLint({"StaticFieldLeak"})
    private static AlertDialog dialog;

    public static void getAppUpdate(final Utilities.Callback2<TLRPC.TL_help_appUpdate, TLRPC.TL_error> callback2) {
        RemoteUtils.getMessages(new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                UpdaterUtils.$r8$lambda$2UAZ2HqXxk8Nz7zqmbeCwTKXcr0(callback2, (TLRPC.messages_Messages) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public static void $r8$lambda$zEBo0VGo0yW4IXbbAahjubhZI0U(Activity activity, File file) {
        InstallReceiver installReceiverRegister = register(activity, new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                UpdaterUtils.$r8$lambda$QoJtvgafGFst_6huPKkX41WQHeM();
            }
        });
        installApk(activity, file);
        Intent intentWaitIntent = installReceiverRegister.waitIntent();
        if (intentWaitIntent != null) {
            activity.startActivity(intentWaitIntent);
        }
    }

    public static void m1477$r8$lambda$JrnIHfIWjS4z_PFGN622PKVTCU() {
        AlertDialog alertDialog = dialog;
        if (alertDialog != null) {
            alertDialog.dismiss();
            dialog = null;
        }
    }

    private static void transfer(InputStream inputStream, OutputStream outputStream) throws IOException {
        byte[] bArr = new byte[8192];
        while (true) {
            int i = inputStream.read(bArr, 0, 8192);
            if (i < 0) {
                return;
            } else {
                outputStream.write(bArr, 0, i);
            }
        }
    }

    private static InstallReceiver register(Context context, Runnable runnable) {
        InstallReceiver installReceiver = new InstallReceiver(context, ApplicationLoader.getApplicationId(), runnable);
        ContextCompat.registerReceiver(context, installReceiver, new IntentFilter(UpdaterUtils.class.getName()), 4);
        return installReceiver;
    }

    public static class InstallReceiver extends BroadcastReceiver {
        private final Context context;
        private Intent intent;
        private final CountDownLatch latch;
        private final Runnable onSuccess;
        private final String packageName;

        private InstallReceiver(Context context, String str, Runnable runnable) {
            this.latch = new CountDownLatch(1);
            this.intent = null;
            this.context = context;
            this.packageName = str;
            this.onSuccess = runnable;
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.PACKAGE_ADDED".equals(intent.getAction())) {
                Uri data = intent.getData();
                if (data == null || this.onSuccess == null || !data.getSchemeSpecificPart().equals(this.packageName)) {
                    return;
                }
                this.onSuccess.run();
                this.context.unregisterReceiver(this);
                return;
            }
            handlePackageInstallerResult(intent);
        }

        private void handlePackageInstallerResult(Intent intent) {
            int intExtra = intent.getIntExtra("android.content.pm.extra.STATUS", 4);
            if (intExtra == -1) {
                this.intent = (Intent) intent.getParcelableExtra("android.intent.extra.INTENT");
            } else {
                if (intExtra == 1 || intExtra == 2 || intExtra == 4 || intExtra == 5 || intExtra == 6 || intExtra == 7) {
                    handleFailure(intent);
                }
                Runnable runnable = this.onSuccess;
                if (runnable != null) {
                    runnable.run();
                }
                this.context.unregisterReceiver(this);
            }
            this.latch.countDown();
        }

        private void handleFailure(final Intent intent) {
            PackageInstaller packageInstaller;
            PackageInstaller.SessionInfo sessionInfo;
            int intExtra = intent.getIntExtra("android.content.pm.extra.SESSION_ID", 0);
            if (intExtra > 0 && (sessionInfo = (packageInstaller = this.context.getPackageManager().getPackageInstaller()).getSessionInfo(intExtra)) != null) {
                packageInstaller.abandonSession(sessionInfo.getSessionId());
            }
            Context context = this.context;
            if (context instanceof LaunchActivity) {
                ((LaunchActivity) context).showBulletin(new Function() { 
                    @Override // androidx.arch.core.util.Function
                    public final Object apply(Object obj) {
                        return ((BulletinFactory) obj).createErrorBulletin(LocaleController.formatString(R.string.UpdateFailedToInstall, Integer.valueOf(intent.getIntExtra("android.content.pm.extra.STATUS", 1))));
                    }
                });
            }
        }

        public Intent waitIntent() {
            try {
                this.latch.await(5L, TimeUnit.SECONDS);
            } catch (Exception unused) {
            }
            return this.intent;
        }
    }

    public static class UpdateReceiver extends BroadcastReceiver {
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.intent.action.MY_PACKAGE_REPLACED".equals(intent.getAction())) {
                String packageName = context.getPackageName();
                if (packageName.equals(context.getPackageManager().getInstallerPackageName(packageName))) {
                    launchApp(context);
                }
            }
        }

        private void launchApp(Context context) {
            Intent flags = new Intent(context, (Class<?>) LaunchActivity.class).setFlags(268435456);
            if (Build.VERSION.SDK_INT < 29 || Settings.canDrawOverlays(context)) {
                context.startActivity(flags);
            } else {
                showNotification(context, flags);
            }
        }

        private void showNotification(Context context, Intent intent) {
            NotificationChannelCompat notificationChannelCompatBuild = new NotificationChannelCompat.Builder("updated", 4).setName(LocaleController.getString(R.string.UpdateApp)).setLightsEnabled(false).setVibrationEnabled(false).setSound(null, null).build();
            NotificationManagerCompat notificationManagerCompatFrom = NotificationManagerCompat.from(context);
            notificationManagerCompatFrom.createNotificationChannel(notificationChannelCompatBuild);
            notificationManagerCompatFrom.notify(8732833, new NotificationCompat.Builder(context, "updated").setSmallIcon(IconManager.getNotificationIcon()).setColor(AppUtils.getNotificationColor()).setShowWhen(false).setContentText(LocaleController.getString(R.string.UpdateInstalledNotification)).setCategory("status").setContentIntent(PendingIntent.getActivity(context, 0, intent, 201326592)).build());
        }
    }
}
