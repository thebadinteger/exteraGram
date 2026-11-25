/*

 This is the source code of exteraGram for Android.

 We do not and cannot prevent the use of our code,
 but be respectful and credit the original author.

 Copyright @exteraGram, 2022-2026

 Licensed under the GNU GPL v. 3 or later, with the additional terms
 in ADDITIONAL_TERMS.md at the root of the repository.

*/

package com.exteragram.messenger.plugins.utils

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.edit
import com.exteragram.messenger.plugins.Plugin
import com.exteragram.messenger.plugins.PluginsController
import com.exteragram.messenger.plugins.pip.PipController
import org.telegram.messenger.AndroidUtilities
import org.telegram.messenger.FileLog
import org.telegram.messenger.LocaleController
import org.telegram.messenger.NotificationCenter
import org.telegram.messenger.R
import org.telegram.ui.ActionBar.AlertDialog
import org.telegram.ui.ActionBar.Theme
import org.telegram.ui.LaunchActivity
import java.io.File
import java.util.ArrayDeque
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.ScheduledThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.system.exitProcess

class PluginsWatchdog(private val controller: PluginsController) {
    private data class ExecutionInfo(val pluginId: String, val startNanos: Long)

    private val executingPlugins = ConcurrentHashMap<Thread, ExecutionInfo>()
    private val frozenExecutions = ConcurrentHashMap<Thread, ExecutionInfo>()
    private val stateLock = Any()
    private val frames = object : ThreadLocal<ArrayDeque<String>>() {
        override fun initialValue() = ArrayDeque<String>()
    }
    private var executionSweep: ScheduledFuture<*>? = null
    private var scheduler: ScheduledExecutorService? = null

    fun start() {
        val currentScheduler = scheduler
        if (currentScheduler != null && !currentScheduler.isShutdown) {
            return
        }

        scheduler = ScheduledThreadPoolExecutor(1).apply {
            removeOnCancelPolicy = true
        }.also { executor ->
            executionSweep = executor.scheduleWithFixedDelay(
                { sweepExecutions() }, SWEEP_INTERVAL_MS, SWEEP_INTERVAL_MS, TimeUnit.MILLISECONDS
            )
        }
    }

    fun stop() {
        executionSweep?.cancel(false)
        executionSweep = null
        scheduler?.shutdownNow()
        scheduler = null
        synchronized(stateLock) {
            for (info in frozenExecutions.values) {
                controller.plugins[info.pluginId]?.setNotResponding(false)
            }
            frozenExecutions.clear()
            executingPlugins.clear()
        }
    }

    fun onPluginExecutionStarted(pluginId: String?) {
        if (pluginId == null) {
            return
        }
        val thread = Thread.currentThread()
        val stack = frames.get()
        val wasEmpty = stack.isEmpty()
        stack.addLast(pluginId)
        if (!wasEmpty) {
            return
        }

        val info = ExecutionInfo(pluginId, System.nanoTime())
        synchronized(stateLock) {
            executingPlugins[thread] = info
            val previousFrozenExecution = frozenExecutions.remove(thread)
            if (previousFrozenExecution != null) {
                if (previousFrozenExecution.pluginId == pluginId) {
                    frozenExecutions[thread] = info
                } else {
                    notifyRecovered(previousFrozenExecution.pluginId)
                }
            }
        }
    }

    fun onPluginExecutionFinished(pluginId: String) {
        val stack = frames.get()
        if (stack.peekLast() != pluginId) {
            return
        }
        stack.removeLast()
        if (stack.isNotEmpty()) {
            return
        }

        val thread = Thread.currentThread()
        synchronized(stateLock) {
            val info = executingPlugins[thread]
            if (info != null && info.pluginId == pluginId) {
                executingPlugins.remove(thread, info)
            }
            val frozenExecution = frozenExecutions.remove(thread)
            if (frozenExecution != null) {
                notifyRecovered(frozenExecution.pluginId)
            }
        }
    }

    private fun sweepExecutions() {
        try {
            val now = System.nanoTime()
            for ((thread, info) in executingPlugins) {
                if ((now - info.startNanos) / 1_000_000L < FREEZE_TIMEOUT_MS) {
                    continue
                }
                var newlyFrozen = false
                var stateChanged = false
                synchronized(stateLock) {
                    if (executingPlugins[thread] === info && frozenExecutions.putIfAbsent(thread, info) == null) {
                        newlyFrozen = true
                        val plugin = controller.plugins[info.pluginId]
                        if (plugin != null && !plugin.isNotResponding()) {
                            plugin.setNotResponding(true)
                            stateChanged = true
                        }
                    }
                }
                if (stateChanged) {
                    NotificationCenter.getGlobalInstance()
                        .postNotificationNameOnUIThread(NotificationCenter.pluginIsNotResponding)
                }
                if (newlyFrozen) {
                    FileLog.e("PluginsWatchdog: ${info.pluginId} is not responding on ${thread.name}")
                }
            }
        } catch (e: Exception) {
            FileLog.e(e)
        }
    }

    private fun notifyRecovered(pluginId: String) {
        var recovered = false
        synchronized(stateLock) {
            if (frozenExecutions.values.none { it.pluginId == pluginId }) {
                controller.plugins[pluginId]?.setNotResponding(false)
                recovered = true
            }
        }
        if (recovered) {
            NotificationCenter.getGlobalInstance()
                .postNotificationNameOnUIThread(NotificationCenter.pluginIsNotResponding)
        }
    }

    fun forceDisablePlugin(pluginId: String, activity: Activity?) {
        disablePluginPref(pluginId)
        restartApp(activity)
    }

    fun forceDeletePlugin(pluginId: String, activity: Activity?) {
        disablePluginPref(pluginId)
        PluginsController.setPluginPinned(pluginId, false)
        controller.cleanupPlugin(pluginId)
        try {
            PipController.uninstallDependencies(pluginId)
        } catch (e: Exception) {
            FileLog.e("Failed to uninstall dependencies for frozen plugin $pluginId", e)
        }
        controller.clearPluginSettingsPreferences(pluginId, true)
        File(controller.pluginsDir, "$pluginId.py").delete()
        controller.plugins.remove(pluginId)
        controller.notifyPluginsChanged()
        restartApp(activity)
    }

    private fun disablePluginPref(pluginId: String) {
        controller.preferences.edit(commit = true) {
            putBoolean(PluginsController.PREF_PLUGIN_ENABLED_KEY_PREFIX + pluginId, false)
        }
    }

    private fun restartApp(activity: Activity?) {
        AndroidUtilities.runOnUIThread({
            if (activity != null) {
                val pm: PackageManager = activity.packageManager
                val intent: Intent? = pm.getLaunchIntentForPackage(activity.packageName)
                activity.finishAffinity()
                if (intent != null) {
                    activity.startActivity(intent)
                }
            }
            exitProcess(0)
        }, 200)
    }

    companion object {
        private const val FREEZE_TIMEOUT_MS = 5_000L
        private const val SWEEP_INTERVAL_MS = 500L

        @JvmStatic
        fun findPluginInTrace(error: Throwable?, ids: Collection<String>): String? {
            if (error == null) {
                return null
            }
            var depth = 0
            var current: Throwable? = error
            while (current != null && depth < 10) {
                var outermost: String? = null
                for (element in current.stackTrace) {
                    val className = element.className ?: continue
                    if (!className.startsWith("<python>.")) {
                        continue
                    }
                    val module = className.substring(9)
                    val id = if (module.startsWith("ElyxPlugins.")) {
                        module.substring("ElyxPlugins.".length).substringBefore('.')
                    } else {
                        module.substringBefore('.')
                    }
                    if (id in ids) {
                        outermost = id
                    }
                }
                if (outermost != null) {
                    return outermost
                }
                current = current.cause
                depth++
            }
            return null
        }

        @JvmStatic
        fun showNotRespondingAlert(plugin: Plugin) {
            val fragment = LaunchActivity.getSafeLastFragment() ?: return
            val activity = fragment.parentActivity
            val builder = AlertDialog.Builder(activity, fragment.resourceProvider)
                .setTitle(
                    LocaleController.formatString(
                        R.string.PluginIsNotRespondingAlert,
                        plugin.getName()
                    )
                )
                .setItems(
                    arrayOf(
                        LocaleController.getString(R.string.WaitMore),
                        LocaleController.getString(R.string.Disable),
                        LocaleController.getString(R.string.Delete)
                    ),
                    intArrayOf(
                        R.drawable.msg_recent,
                        R.drawable.msg_block,
                        R.drawable.msg_delete
                    )
                ) { _, which ->
                    if (which == 1) {
                        PluginsController.getInstance().watchdog.forceDisablePlugin(
                            plugin.getId(),
                            activity
                        )
                    } else if (which == 2) {
                        PluginsController.getInstance().watchdog.forceDeletePlugin(
                            plugin.getId(),
                            activity
                        )
                    }
                }

            val dialog = builder.create()
            dialog.show()
            dialog.setItemColor(
                dialog.itemsCount - 1,
                Theme.getColor(Theme.key_text_RedBold),
                Theme.getColor(Theme.key_text_RedRegular)
            )
        }
    }
}