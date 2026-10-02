package com.exteragram.messenger.debug;

import android.app.Activity;
import android.os.Debug;
import android.os.SystemClock;
import com.google.android.gms.cast.framework.media.NotificationOptions;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import kotlin.jvm.functions.Function0;
import kotlin.ranges.RangesKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ForegroundDetector;
import org.telegram.ui.LaunchActivity;

public final class HeapMonitor {
    public static final HeapMonitor INSTANCE = new HeapMonitor();
    private static WeakReference<AlertDialog> alertRef;
    private static volatile boolean dumping;
    private static volatile LimitWatcher watcher;

    private HeapMonitor() {
    }

    public File getDumpsDir() {
        return DebugFilesKt.debugFilesDir("heapdumps");
    }

    public long getPromptStepBytes() {
        return Runtime.getRuntime().maxMemory() / 8;
    }

    public static void init() {
        if (DebugConfig.getHeapMonitorEnabled()) {
            watcher = new LimitWatcher();
        }
    }

    public void setEnabled(boolean enabled) {
        if (DebugConfig.getHeapMonitorEnabled() == enabled) {
            return;
        }
        DebugConfig.setHeapMonitorEnabled(enabled);
        LimitWatcher limitWatcher = watcher;
        if (limitWatcher != null) {
            limitWatcher.stop();
        }
        watcher = enabled ? new LimitWatcher() : null;
    }

    public int getLimitMb() {
        return RangesKt.coerceIn(DebugConfig.getHeapMonitorLimitMb(), 64, getMaxLimitMb());
    }

    public int getMaxLimitMb() {
        return Math.max(64, ((((int) (Runtime.getRuntime().maxMemory() >> 20)) / 16) - 1) * 16);
    }

    public void setLimitMb(int limitMb) {
        DebugConfig.setHeapMonitorLimitMb(limitMb);
        LimitWatcher limitWatcher = watcher;
        if (limitWatcher != null) {
            limitWatcher.resetPrompt();
        }
    }

    public long getUsedBytes() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public File getLastDump() {
        File[] files = getDumpsDir().listFiles();
        if (files == null) {
            return null;
        }
        File lastFile = null;
        for (File file : files) {
            if (file.getName().endsWith(".zip")) {
                if (lastFile == null || file.lastModified() > lastFile.lastModified()) {
                    lastFile = file;
                }
            }
        }
        return lastFile;
    }

    public long getDumpsSize() {
        File[] files = getDumpsDir().listFiles();
        long total = 0;
        if (files != null) {
            for (File file : files) {
                total += file.length();
            }
        }
        return total;
    }

    public void deleteDumps(Runnable onDone) {
        Utilities.globalQueue.postRunnable(() -> {
            File[] files = getDumpsDir().listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            if (onDone != null) {
                AndroidUtilities.runOnUIThread(onDone);
            }
        });
    }

    public void deleteDumps(Function0<?> onDone) {
        if (onDone != null) {
            deleteDumps(() -> onDone.invoke());
        } else {
            deleteDumps((Runnable) null);
        }
    }

    public void shareLastDump(Activity activity) {
        File lastDump = getLastDump();
        if (lastDump != null) {
            share(activity, lastDump);
        }
    }

    public interface ProgressCallback {
        void onProgress(int progress);
    }

    public void dumpAndShare(final Activity activity, Theme.ResourcesProvider resourcesProvider) {
        if (dumping) {
            return;
        }
        dumping = true;
        final AlertDialog alertDialog = new AlertDialog(activity, 2, resourcesProvider);
        alertDialog.setMessage("Dumping heap\u2026");
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.setCancelable(false);
        alertDialog.show();

        new Thread(() -> {
            SystemClock.sleep(300L);
            File dumpFile = null;
            Throwable error = null;
            try {
                dumpFile = dumpHeap(progress -> AndroidUtilities.runOnUIThread(() -> {
                    if (progress == 0) {
                        alertDialog.setMessage("Compressing\u2026");
                    }
                    alertDialog.setProgress(progress);
                }));
            } catch (Throwable th) {
                error = th;
            }

            final File finalDumpFile = dumpFile;
            final Throwable finalError = error;
            AndroidUtilities.runOnUIThread(() -> {
                dumping = false;
                try {
                    alertDialog.dismiss();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                if (finalDumpFile != null) {
                    share(activity, finalDumpFile);
                }
                if (finalError != null) {
                    FileLog.e(finalError);
                    BulletinFactory.global().createSimpleBulletin(R.raw.error, "Heap dump failed: " + finalError).show();
                }
            });
        }, "heapDump").start();
    }

    private void showLimitAlert(long used, long limit) {
        final LaunchActivity launchActivity = LaunchActivity.instance;
        if (watcher == null || dumping || launchActivity == null || launchActivity.isFinishing()) {
            return;
        }
        WeakReference<AlertDialog> weakReference = alertRef;
        AlertDialog currentAlert;
        if (weakReference == null || (currentAlert = weakReference.get()) == null || !currentAlert.isShowing()) {
            long maxMemory = Runtime.getRuntime().maxMemory();
            AlertDialog dialog = new AlertDialog.Builder(launchActivity)
                    .setTitle("Heap limit exceeded")
                    .setMessage("Java heap is at " + AndroidUtilities.formatFileSize(used, true, false)
                            + " of " + AndroidUtilities.formatFileSize(maxMemory, true, false)
                            + " after GC, over the " + AndroidUtilities.formatFileSize(limit, true, false)
                            + " limit.\n\nThe app freezes while dumping. The dump holds everything in memory, so share it only with people you trust.")
                    .setPositiveButton("Dump & share", (dialogInterface, i) -> dumpAndShare(launchActivity, null))
                    .setNeutralButton("Turn off", (dialogInterface, i) -> setEnabled(false))
                    .setNegativeButton("Later", null)
                    .create();
            alertRef = new WeakReference<>(dialog);
            try {
                dialog.show();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    private void collectGarbage() {
        Runtime runtime = Runtime.getRuntime();
        runtime.gc();
        runtime.runFinalization();
        runtime.gc();
    }

    private File dumpHeap(ProgressCallback onProgress) throws IOException {
        getDumpsDir().mkdirs();
        String str = "heap_" + BuildVars.BUILD_VERSION_STRING + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File hprofFile = new File(getDumpsDir(), str + ".hprof");
        File partZipFile = new File(getDumpsDir(), str + ".zip.part");
        File finalZipFile = new File(getDumpsDir(), str + ".zip");
        try {
            collectGarbage();
            LimitWatcher limitWatcher = watcher;
            if (limitWatcher != null) {
                limitWatcher.postponePrompt(getUsedBytes());
            }
            Debug.dumpHprofData(hprofFile.getAbsolutePath());
            compress(hprofFile, partZipFile, onProgress);
            if (!partZipFile.renameTo(finalZipFile)) {
                throw new IOException("Can't rename " + partZipFile.getName());
            }
            hprofFile.delete();
            partZipFile.delete();
            File[] files = getDumpsDir().listFiles();
            if (files != null) {
                for (File file : files) {
                    if (!file.equals(finalZipFile)) {
                        file.delete();
                    }
                }
            }
            return finalZipFile;
        } catch (Throwable th) {
            hprofFile.delete();
            partZipFile.delete();
            if (th instanceof IOException) {
                throw (IOException) th;
            }
            throw new IOException(th);
        }
    }

    private void compress(File source, File target, ProgressCallback onProgress) throws IOException {
        long sourceLength = Math.max(1L, source.length());
        if (onProgress != null) {
            onProgress.onProgress(0);
        }
        try (ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(target), 65536))) {
            zos.setLevel(1);
            zos.putNextEntry(new ZipEntry(source.getName()));
            try (FileInputStream fis = new FileInputStream(source)) {
                byte[] buf = new byte[65536];
                long written = 0;
                int lastPercent = 0;
                int len;
                while ((len = fis.read(buf)) >= 0) {
                    zos.write(buf, 0, len);
                    written += len;
                    int percent = (int) ((100 * written) / sourceLength);
                    if (percent != lastPercent) {
                        if (onProgress != null) {
                            onProgress.onProgress(percent);
                        }
                        lastPercent = percent;
                    }
                }
            }
            zos.closeEntry();
        }
    }

    private void share(Activity activity, File file) {
        DebugFilesKt.shareDebugFile(activity, file, "application/zip", "Share heap dump");
    }

    public static final class LimitWatcher implements ForegroundDetector.Listener {
        private final DispatchQueue queue = new DispatchQueue("heapMonitorQueue");
        private final Runnable checkRunnable = this::check;
        private long lastCollectTime;
        private long nextPromptBytes;
        private volatile boolean running;

        public LimitWatcher() {
            ForegroundDetector foregroundDetector = ForegroundDetector.getInstance();
            if (foregroundDetector != null) {
                foregroundDetector.addListener(this);
            }
            updateRunning();
        }

        @Override
        public void onBecameForeground() {
            updateRunning();
        }

        @Override
        public void onBecameBackground() {
            updateRunning();
        }

        public void stop() {
            ForegroundDetector foregroundDetector = ForegroundDetector.getInstance();
            if (foregroundDetector != null) {
                foregroundDetector.removeListener(this);
            }
            this.running = false;
            this.queue.cleanupQueue();
            this.queue.recycle();
        }

        public void resetPrompt() {
            this.queue.postRunnable(() -> {
                nextPromptBytes = 0L;
                lastCollectTime = 0L;
            });
        }

        public void postponePrompt(final long usedBytes) {
            this.queue.postRunnable(() -> {
                nextPromptBytes = Math.max(nextPromptBytes, usedBytes + HeapMonitor.INSTANCE.getPromptStepBytes());
            });
        }

        private void updateRunning() {
            ForegroundDetector foregroundDetector = ForegroundDetector.getInstance();
            boolean fg = foregroundDetector != null && foregroundDetector.isForeground();
            if (this.running == fg) {
                return;
            }
            this.running = fg;
            this.queue.postRunnable(() -> {
                queue.cancelRunnable(checkRunnable);
                if (running) {
                    queue.postRunnable(checkRunnable, 5000L);
                }
            });
        }

        private void check() {
            if (!this.running) {
                return;
            }
            HeapMonitor heapMonitor = HeapMonitor.INSTANCE;
            final long limitBytes = ((long) heapMonitor.getLimitMb()) << 20;
            long usedBytes = heapMonitor.getUsedBytes();
            if (usedBytes >= limitBytes) {
                if (usedBytes >= this.nextPromptBytes && !HeapMonitor.dumping && !ApplicationLoader.mainInterfacePaused
                        && SystemClock.elapsedRealtime() - this.lastCollectTime >= NotificationOptions.SKIP_STEP_THIRTY_SECONDS_IN_MS) {
                    this.lastCollectTime = SystemClock.elapsedRealtime();
                    heapMonitor.collectGarbage();
                    final long usedAfterGc = heapMonitor.getUsedBytes();
                    if (usedAfterGc >= Math.max(limitBytes, this.nextPromptBytes)) {
                        this.nextPromptBytes = heapMonitor.getPromptStepBytes() + usedAfterGc;
                        AndroidUtilities.runOnUIThread(() -> HeapMonitor.INSTANCE.showLimitAlert(usedAfterGc, limitBytes));
                    }
                }
            } else {
                this.nextPromptBytes = 0L;
            }
            if (this.running) {
                this.queue.postRunnable(this.checkRunnable, 5000L);
            }
        }
    }
}
