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

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
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
        this.controller = pluginsController;
        this.executingPlugins = new ConcurrentHashMap<>();
        this.frozenExecutions = new ConcurrentHashMap<>();
        this.scheduledChecks = new ConcurrentHashMap<>();
    }

    @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\r\u001a\u00020\u000eHÖ\u0001J\t\u0010\u000f\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0010"}, d2 = {"Lcom/exteragram/messenger/plugins/utils/PluginsWatchdog$ExecutionInfo;", _UrlKt.FRAGMENT_ENCODE_SET, "pluginId", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;)V", "getPluginId", "()Ljava/lang/String;", "component1", "copy", "equals", _UrlKt.FRAGMENT_ENCODE_SET, "other", "hashCode", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final /* data */ class ExecutionInfo {
        private final String pluginId;

        public static /* synthetic */ ExecutionInfo copy$default(ExecutionInfo executionInfo, String str, int i, Object obj) {
            if ((i & 1) != 0) {
                str = executionInfo.pluginId;
            }
            return executionInfo.copy(str);
        }

        /* JADX INFO: renamed from: component1, reason: from getter */
        public final String component1() {
            return this.pluginId;
        }

        public final ExecutionInfo copy(String pluginId) {
            return new ExecutionInfo(pluginId);
        }

        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            return (other instanceof ExecutionInfo) && Intrinsics.areEqual(this.pluginId, ((ExecutionInfo) other).pluginId);
        }

        public int hashCode() {
            return this.pluginId.hashCode();
        }

        public String toString() {
            return "ExecutionInfo(pluginId=" + this.pluginId + ')';
        }

        public ExecutionInfo(String str) {
            this.pluginId = str;
        }

        public final String getPluginId() {
            return this.pluginId;
        }
    }

    public final void start() {
        ScheduledExecutorService scheduledExecutorService = this.scheduler;
        if (scheduledExecutorService == null || scheduledExecutorService.isShutdown()) {
            ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
            scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
            this.scheduler = scheduledThreadPoolExecutor;
        }
    }

    public final void stop() {
        for (ScheduledFuture<?> scheduledFuture : this.scheduledChecks.values()) {
            scheduledFuture.cancel(false);
        }
        this.scheduledChecks.clear();
        ScheduledExecutorService scheduledExecutorService = this.scheduler;
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdownNow();
        }
        this.scheduler = null;
        for (ExecutionInfo executionInfo : this.frozenExecutions.values()) {
            Plugin plugin = this.controller.getPlugins().get(executionInfo.getPluginId());
            if (plugin != null) {
                plugin.setNotResponding(false);
            }
        }
        this.frozenExecutions.clear();
        this.executingPlugins.clear();
    }

    /* JADX WARN: Code duplicated, block: B:20:0x0076  */
    public final void onPluginExecutionStarted(final String pluginId) {
        Plugin plugin;
        if (pluginId == null) {
            return;
        }
        final Thread threadCurrentThread = Thread.currentThread();
        final ExecutionInfo executionInfo = new ExecutionInfo(pluginId);
        this.executingPlugins.put(threadCurrentThread, executionInfo);
        ExecutionInfo executionInfoRemove = this.frozenExecutions.remove(threadCurrentThread);
        if (executionInfoRemove != null) {
            boolean zAreEqual = Intrinsics.areEqual(executionInfoRemove.getPluginId(), pluginId);
            ConcurrentHashMap<Thread, ExecutionInfo> concurrentHashMap = this.frozenExecutions;
            if (zAreEqual) {
                concurrentHashMap.put(threadCurrentThread, executionInfo);
            } else {
                boolean hasOther = false;
                for (ExecutionInfo info : concurrentHashMap.values()) {
                    if (Intrinsics.areEqual(info.getPluginId(), executionInfoRemove.getPluginId())) {
                        hasOther = true;
                        break;
                    }
                }
                if (!hasOther) {
                    plugin = this.controller.getPlugins().get(executionInfoRemove.getPluginId());
                    if (plugin != null) {
                        plugin.setNotResponding(false);
                    }
                    NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.pluginIsNotResponding, new Object[0]);
                }
            }
        }
        ScheduledFuture<?> scheduledFutureRemove = this.scheduledChecks.remove(threadCurrentThread);
        if (scheduledFutureRemove != null) {
            scheduledFutureRemove.cancel(false);
        }
        ScheduledExecutorService scheduledExecutorService = this.scheduler;
        if (scheduledExecutorService == null) {
            return;
        }
        try {
            this.scheduledChecks.put(threadCurrentThread, scheduledExecutorService.schedule(new Runnable() { // from class: com.exteragram.messenger.plugins.utils.PluginsWatchdog$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    PluginsWatchdog.$r8$lambda$aEUKVuMeoYLYYeSPkSgPbWxPjUk(PluginsWatchdog.this, threadCurrentThread, executionInfo, pluginId);
                }
            }, 5L, TimeUnit.SECONDS));
        } catch (RejectedExecutionException unused) {
            this.executingPlugins.remove(threadCurrentThread, executionInfo);
        }
    }

    public static void $r8$lambda$aEUKVuMeoYLYYeSPkSgPbWxPjUk(final PluginsWatchdog pluginsWatchdog, final Thread thread, final ExecutionInfo executionInfo, final String str) {
        try {
            final Ref.BooleanRef booleanRef = new Ref.BooleanRef();
            ConcurrentHashMap<Thread, ExecutionInfo> concurrentHashMap = pluginsWatchdog.executingPlugins;
            final Function2 function2 = new Function2() { // from class: com.exteragram.messenger.plugins.utils.PluginsWatchdog$$ExternalSyntheticLambda1
                @Override // kotlin.jvm.functions.Function2
                public final Object invoke(Object obj, Object obj2) {
                    return PluginsWatchdog.onPluginExecutionStarted$lambda$1$0(executionInfo, pluginsWatchdog, thread, str, booleanRef, (Thread) obj, (PluginsWatchdog.ExecutionInfo) obj2);
                }
            };
            concurrentHashMap.computeIfPresent(thread, new BiFunction() { // from class: com.exteragram.messenger.plugins.utils.PluginsWatchdog$$ExternalSyntheticLambda2
                @Override // java.util.function.BiFunction
                public final Object apply(Object obj, Object obj2) {
                    return PluginsWatchdog.onPluginExecutionStarted$lambda$1$1(function2, obj, obj2);
                }
            });
            if (booleanRef.element) {
                NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.pluginIsNotResponding, new Object[0]);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final ExecutionInfo onPluginExecutionStarted$lambda$1$1(Function2 function2, Object obj, Object obj2) {
        return (ExecutionInfo) function2.invoke(obj, obj2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static final ExecutionInfo onPluginExecutionStarted$lambda$1$0(ExecutionInfo executionInfo, PluginsWatchdog pluginsWatchdog, Thread thread, String str, Ref.BooleanRef booleanRef, Thread thread2, ExecutionInfo executionInfo2) {
        if (executionInfo2 == executionInfo) {
            pluginsWatchdog.frozenExecutions.put(thread, executionInfo);
            Plugin plugin = pluginsWatchdog.controller.getPlugins().get(str);
            if (plugin != null && !plugin.getIsNotResponding()) {
                plugin.setNotResponding(true);
                booleanRef.element = true;
            }
        }
        return executionInfo2;
    }

    public final void onPluginExecutionFinished(String pluginId) {
        Thread threadCurrentThread = Thread.currentThread();
        ExecutionInfo executionInfo = this.executingPlugins.get(threadCurrentThread);
        if (executionInfo != null && Intrinsics.areEqual(executionInfo.getPluginId(), pluginId) && this.executingPlugins.remove(threadCurrentThread, executionInfo)) {
            ScheduledFuture<?> scheduledFutureRemove = this.scheduledChecks.remove(threadCurrentThread);
            if (scheduledFutureRemove != null) {
                scheduledFutureRemove.cancel(false);
            }
            ExecutionInfo executionInfoRemove = this.frozenExecutions.remove(threadCurrentThread);
            if (executionInfoRemove != null) {
                Collection<ExecutionInfo> collectionValues = this.frozenExecutions.values();
                Collection<ExecutionInfo> collection = collectionValues;
                if (!collection.isEmpty()) {
                    Iterator<ExecutionInfo> it = collection.iterator();
                    while (it.hasNext()) {
                        if (Intrinsics.areEqual(((ExecutionInfo) it.next()).getPluginId(), executionInfoRemove.getPluginId())) {
                            return;
                        }
                    }
                }
                Plugin plugin = this.controller.getPlugins().get(executionInfoRemove.getPluginId());
                if (plugin != null) {
                    plugin.setNotResponding(false);
                }
                NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.pluginIsNotResponding, new Object[0]);
            }
        }
    }

    public final void forceDisablePlugin(String pluginId, Activity activity) {
        disablePluginPref(pluginId);
        restartApp(activity);
    }

    public final void forceDeletePlugin(String pluginId, Activity activity) {
        disablePluginPref(pluginId);
        PluginsController.INSTANCE.setPluginPinned(pluginId, false);
        this.controller.cleanupPlugin(pluginId);
        try {
            PipController.INSTANCE.uninstallDependencies(pluginId);
        } catch (Exception e) {
            FileLog.e("Failed to uninstall dependencies for frozen plugin " + pluginId, e);
        }
        this.controller.clearPluginSettingsPreferences(pluginId, true);
        new File(this.controller.getPluginsDir(), pluginId + ".py").delete();
        this.controller.getPlugins().remove(pluginId);
        this.controller.notifyPluginsChanged();
        restartApp(activity);
    }

    private final void disablePluginPref(String pluginId) {
        SharedPreferences.Editor editorEdit = this.controller.getPreferences().edit();
        editorEdit.putBoolean("plugin_enabled_" + pluginId, false);
        editorEdit.apply();
    }

    private final void restartApp(final Activity activity) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.plugins.utils.PluginsWatchdog$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                PluginsWatchdog.m1385$r8$lambda$C6oS2ADJb9prdSSfDfoeRfN2sA(activity);
            }
        }, 200L);
    }

    /* JADX INFO: renamed from: $r8$lambda$C6oS2ADJb9prd-SSfDfoeRfN2sA, reason: not valid java name */
    public static void m1385$r8$lambda$C6oS2ADJb9prdSSfDfoeRfN2sA(Activity activity) {
        if (activity != null) {
            PackageManager packageManager = activity.getPackageManager();
            Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(activity.getPackageName());
            activity.finishAffinity();
            if (launchIntentForPackage != null) {
                activity.startActivity(launchIntentForPackage);
            }
        }
        System.exit(0);
        throw new RuntimeException("System.exit returned normally, while it was supposed to halt JVM.");
    }

    @Metadata(d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007H\u0007¨\u0006\b"}, d2 = {"Lcom/exteragram/messenger/plugins/utils/PluginsWatchdog$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "showNotRespondingAlert", _UrlKt.FRAGMENT_ENCODE_SET, "plugin", "Lcom/exteragram/messenger/plugins/Plugin;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        @JvmStatic
        public final void showNotRespondingAlert(final Plugin plugin) {
            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
            if (safeLastFragment == null) {
                return;
            }
            final Activity parentActivity = safeLastFragment.getParentActivity();
            AlertDialog alertDialogCreate = new AlertDialog.Builder(parentActivity, safeLastFragment.getResourceProvider()).setTitle(LocaleController.formatString(R.string.PluginIsNotRespondingAlert, plugin.getName())).setItems(new String[]{LocaleController.getString(R.string.WaitMore), LocaleController.getString(R.string.Disable), LocaleController.getString(R.string.Delete)}, new int[]{R.drawable.msg_recent, R.drawable.msg_block, R.drawable.msg_delete}, new DialogInterface.OnClickListener() { // from class: com.exteragram.messenger.plugins.utils.PluginsWatchdog$Companion$$ExternalSyntheticLambda0
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
