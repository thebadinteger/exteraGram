package com.exteragram.messenger.plugins.utils;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import com.exteragram.messenger.plugins.Plugin;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.plugins.pip.PipController;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.BiFunction;
import kotlin.Metadata;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.LaunchActivity;

@Metadata(d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0007\u0018\u0000 \u001d2\u00020\u0001:\u0002\u001c\u001dB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u0006\u0010\u000f\u001a\u00020\u0010J\u0006\u0010\u0011\u001a\u00020\u0010J\u0010\u0010\u0012\u001a\u00020\u00102\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014J\u000e\u0010\u0015\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u0014J\u0018\u0010\u0016\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u00142\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018J\u0018\u0010\u0019\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u00142\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018J\u0010\u0010\u001a\u001a\u00020\u00102\u0006\u0010\u0013\u001a\u00020\u0014H\u0002J\u0012\u0010\u001b\u001a\u00020\u00102\b\u0010\u0017\u001a\u0004\u0018\u00010\u0018H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001a\u0010\n\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\t0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u001e\u0010\u000b\u001a\u0012\u0012\u0004\u0012\u00020\b\u0012\b\u0012\u0006\u0012\u0002\b\u00030\f0\u0007X\u0082\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u0004\u0018\u00010\u000eX\u0082\u000e¢\u0006\u0002\n\u0000¨\u0006\u001e"}, d2 = {"Lcom/exteragram/messenger/plugins/utils/PluginsWatchdog;", _UrlKt.FRAGMENT_ENCODE_SET, "controller", "Lcom/exteragram/messenger/plugins/PluginsController;", "<init>", "(Lcom/exteragram/messenger/plugins/PluginsController;)V", "executingPlugins", "Ljava/util/concurrent/ConcurrentHashMap;", "Ljava/lang/Thread;", "Lcom/exteragram/messenger/plugins/utils/PluginsWatchdog$ExecutionInfo;", "frozenExecutions", "scheduledChecks", "Ljava/util/concurrent/ScheduledFuture;", "scheduler", "Ljava/util/concurrent/ScheduledExecutorService;", "start", _UrlKt.FRAGMENT_ENCODE_SET, "stop", "onPluginExecutionStarted", "pluginId", _UrlKt.FRAGMENT_ENCODE_SET, "onPluginExecutionFinished", "forceDisablePlugin", "activity", "Landroid/app/Activity;", "forceDeletePlugin", "disablePluginPref", "restartApp", "ExecutionInfo", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nPluginsWatchdog.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PluginsWatchdog.kt\ncom/exteragram/messenger/plugins/utils/PluginsWatchdog\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 SharedPreferences.kt\nandroidx/core/content/SharedPreferencesKt\n*L\n1#1,224:1\n2792#2,3:225\n2792#2,3:228\n41#3,12:231\n*S KotlinDebug\n*F\n+ 1 PluginsWatchdog.kt\ncom/exteragram/messenger/plugins/utils/PluginsWatchdog\n*L\n86#1:225,3\n129#1:228,3\n159#1:231,12\n*E\n"})
public final class PluginsWatchdog {

    public static final Companion INSTANCE = new Companion(null);
    private final PluginsController controller;
    private final ConcurrentHashMap<Thread, ExecutionInfo> executingPlugins;
    private final ConcurrentHashMap<Thread, ExecutionInfo> frozenExecutions;
    private final ConcurrentHashMap<Thread, ScheduledFuture<?>> scheduledChecks;
    private ScheduledExecutorService scheduler;

    @JvmStatic
    public static final void showNotRespondingAlert(Plugin plugin) {
        INSTANCE.showNotRespondingAlert(plugin);
    }

    public PluginsWatchdog(PluginsController pluginsController) {
        "controller";
        this.controller = pluginsController;
        this.executingPlugins = new ConcurrentHashMap<>();
        this.frozenExecutions = new ConcurrentHashMap<>();
        this.scheduledChecks = new ConcurrentHashMap<>();
    }

    @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\r\u001a\u00020\u000eHÖ\u0001J\t\u0010\u000f\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0010"}, d2 = {"Lcom/exteragram/messenger/plugins/utils/PluginsWatchdog$ExecutionInfo;", _UrlKt.FRAGMENT_ENCODE_SET, "pluginId", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;)V", "getPluginId", "()Ljava/lang/String;", "component1", "copy", "equals", _UrlKt.FRAGMENT_ENCODE_SET, "other", "hashCode", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final void showNotRespondingAlert(final Plugin plugin) {
            "plugin";
            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
            if (safeLastFragment == null) {
                return;
            }
            final Activity parentActivity = safeLastFragment.getParentActivity();
            AlertDialog alertDialogCreate = new AlertDialog.Builder(parentActivity, safeLastFragment.getResourceProvider()).setTitle(LocaleController.formatString(R.string.PluginIsNotRespondingAlert, plugin.getName())).setItems(new String[]{LocaleController.getString(R.string.WaitMore), LocaleController.getString(R.string.Disable), LocaleController.getString(R.string.Delete)}, new int[]{R.drawable.msg_recent, R.drawable.msg_block, R.drawable.msg_delete}, new DialogInterface.OnClickListener() { 
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PluginsWatchdog.Companion.$r8$lambda$YVUu_yybweI20Wq2RsPMfENiUXc(plugin, parentActivity, dialogInterface, i);
                }
            }).create();
            alertDialogCreate.show();
            alertDialogCreate.setItemColor(alertDialogCreate.getItemsCount() - 1, Theme.getColor(Theme.key_text_RedBold), Theme.getColor(Theme.key_text_RedRegular));
        }

        public static void $r8$lambda$YVUu_yybweI20Wq2RsPMfENiUXc(Plugin plugin, Activity activity, DialogInterface dialogInterface, int i) {
            if (i == 1) {
                PluginsController.INSTANCE.getInstance().getWatchdog().forceDisablePlugin(plugin.getId(), activity);
            } else {
                if (i != 2) {
                    return;
                }
                PluginsController.INSTANCE.getInstance().getWatchdog().forceDeletePlugin(plugin.getId(), activity);
            }
        }
    }
}
