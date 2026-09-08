package org.telegram.ui.Stories.recorder;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.text.TextUtils;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.pillstack.core.PillStackConfig;
import com.exteragram.messenger.pillstack.core.PillType;
import j$.util.DesugarTimeZone;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.AbstractSerializedData;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.PermissionRequest;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.Stories.DarkThemeResourceProvider;

public abstract class Weather {
    private static String cacheKey;
    private static State cacheValue;
    private static boolean requestingLocation;

    static {
        NotificationCenter.getGlobalInstance().addObserver(new NotificationCenter.NotificationCenterDelegate() { // from class: org.telegram.ui.Stories.recorder.Weather$$ExternalSyntheticLambda0
            @Override 
            public final void didReceivedNotification(int i, int i2, Object[] objArr) {
                Weather.m20622$r8$lambda$lwBNB9yWTZ_sQq5Y12nEz3hF4(i, i2, objArr);
            }
        }, NotificationCenter.pillStackSettingsChanged);
    }

    public static void m20623$r8$lambda$t3ZkHizLhHzQ8gS5sVl0ruSWok(final Utilities.Callback callback, final boolean z, Location location) {
        if (location == null) {
            callback.run(null);
            return;
        }
        Activity activityFindActivity = LaunchActivity.instance;
        if (activityFindActivity == null) {
            activityFindActivity = AndroidUtilities.findActivity(ApplicationLoader.applicationContext);
        }
        if (activityFindActivity == null || activityFindActivity.isFinishing()) {
            callback.run(null);
            return;
        }
        final AlertDialog alertDialog = z ? new AlertDialog(activityFindActivity, 3, new DarkThemeResourceProvider()) : null;
        if (z) {
            alertDialog.showDelayed(200L);
        }
        final Runnable runnableFetch = fetch(location.getLatitude(), location.getLongitude(), new Utilities.Callback() { // from class: org.telegram.ui.Stories.recorder.Weather$$ExternalSyntheticLambda10
            @Override 
            public final void run(Object obj) {
                Weather.$r8$lambda$Fj5il8a0wiK9X50oPwXxScWnjE0(z, alertDialog, callback, (Weather.State) obj);
            }
        });
        if (!z || runnableFetch == null) {
            return;
        }
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() { // from class: org.telegram.ui.Stories.recorder.Weather$$ExternalSyntheticLambda11
            @Override // android.content.DialogInterface.OnCancelListener
            public final void onCancel(DialogInterface dialogInterface) {
                runnableFetch.run();
            }
        });
    }

    public static void $r8$lambda$hk9az5DrJFjlueUS6FNBCLlJ_t4(int[] iArr, TLObject tLObject, MessagesController messagesController, TLRPC.User[] userArr, Runnable runnable, Utilities.Callback callback) {
        iArr[0] = 0;
        if (tLObject instanceof TLRPC.TL_contacts_resolvedPeer) {
            TLRPC.TL_contacts_resolvedPeer tL_contacts_resolvedPeer = (TLRPC.TL_contacts_resolvedPeer) tLObject;
            messagesController.putUsers(tL_contacts_resolvedPeer.users, false);
            messagesController.putChats(tL_contacts_resolvedPeer.chats, false);
            TLRPC.User user = messagesController.getUser(Long.valueOf(DialogObject.getPeerDialogId(tL_contacts_resolvedPeer.peer)));
            userArr[0] = user;
            if (user != null) {
                runnable.run();
                return;
            }
        }
        callback.run(getCached());
    }

    public static void $r8$lambda$fvqkoXIkTbli7Ns6qnkozliUScs(Utilities.Callback callback, boolean z, Boolean bool) {
        requestingLocation = false;
        if (!bool.booleanValue()) {
            callback.run(null);
            return;
        }
        final LocationManager locationManager = (LocationManager) ApplicationLoader.applicationContext.getSystemService("location");
        List<String> providers = locationManager.getProviders(true);
        Location lastKnownLocation = null;
        for (int size = providers.size() - 1; size >= 0; size--) {
            lastKnownLocation = locationManager.getLastKnownLocation(providers.get(size));
            if (lastKnownLocation != null) {
                break;
            }
        }
        if (lastKnownLocation == null && z) {
            if (!locationManager.isProviderEnabled("gps")) {
                final Context context = LaunchActivity.instance;
                if (context == null) {
                    context = ApplicationLoader.applicationContext;
                }
                if (context != null) {
                    try {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTopAnimation(R.raw.permission_request_location, 72, false, Theme.getColor(Theme.key_dialogTopBackground));
                        builder.setMessage(LocaleController.getString(R.string.GpsDisabledAlertText));
                        builder.setPositiveButton(LocaleController.getString(R.string.Enable), new AlertDialog.OnButtonClickListener() { // from class: org.telegram.ui.Stories.recorder.Weather$$ExternalSyntheticLambda6
                            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
                            public final void onClick(AlertDialog alertDialog, int i) {
                                context.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
                            }
                        });
                        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
                        builder.show();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            } else {
                try {
                    final Utilities.Callback[] callbackArr = {callback};
                    final LocationListener[] locationListenerArr = {null};
                    final Runnable[] runnableArr = {null};
                    final Runnable runnable = new Runnable() { // from class: org.telegram.ui.Stories.recorder.Weather$$ExternalSyntheticLambda7
                        @Override // java.lang.Runnable
                        public final void run() {
                            Weather.$r8$lambda$4S3GuGxOiu6SPlKA4XuA0c1MPc8(locationListenerArr, locationManager, runnableArr);
                        }
                    };
                    LocationListener locationListener = new LocationListener() { // from class: org.telegram.ui.Stories.recorder.Weather$$ExternalSyntheticLambda8
                        @Override // android.location.LocationListener
                        public final void onLocationChanged(Location location) {
                            Weather.m20620$r8$lambda$My5p7ErkTbKx6HW0WXr0AxDwNU(runnable, callbackArr, location);
                        }
                    };
                    Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Stories.recorder.Weather$$ExternalSyntheticLambda9
                        @Override // java.lang.Runnable
                        public final void run() {
                            Weather.m20625$r8$lambda$x1itkN7KveLOXacSOqWbhVGf2I(runnable, callbackArr);
                        }
                    };
                    locationListenerArr[0] = locationListener;
                    runnableArr[0] = runnable2;
                    locationManager.requestLocationUpdates("gps", 1000L, 0.0f, locationListener);
                    AndroidUtilities.runOnUIThread(runnable2, 15000L);
                    return;
                } catch (Exception e2) {
                    FileLog.e(e2);
                    callback.run(null);
                    return;
                }
            }
        }
        callback.run(lastKnownLocation);
    }

    public static /* synthetic */ void $r8$lambda$4S3GuGxOiu6SPlKA4XuA0c1MPc8(LocationListener[] locationListenerArr, LocationManager locationManager, Runnable[] runnableArr) {
        LocationListener locationListener = locationListenerArr[0];
        if (locationListener != null) {
            locationManager.removeUpdates(locationListener);
            locationListenerArr[0] = null;
        }
        Runnable runnable = runnableArr[0];
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            runnableArr[0] = null;
        }
    }

    public static /* synthetic */ void m20620$r8$lambda$My5p7ErkTbKx6HW0WXr0AxDwNU(Runnable runnable, Utilities.Callback[] callbackArr, Location location) {
        runnable.run();
        Utilities.Callback callback = callbackArr[0];
        if (callback != null) {
            callback.run(location);
            callbackArr[0] = null;
        }
    }

    public static /* synthetic */ void m20625$r8$lambda$x1itkN7KveLOXacSOqWbhVGf2I(Runnable runnable, Utilities.Callback[] callbackArr) {
        runnable.run();
        Utilities.Callback callback = callbackArr[0];
        if (callback != null) {
            callback.run(null);
            callbackArr[0] = null;
        }
    }
}
