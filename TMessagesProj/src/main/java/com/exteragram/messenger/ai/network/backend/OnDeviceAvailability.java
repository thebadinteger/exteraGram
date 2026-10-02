package com.exteragram.messenger.ai.network.backend;

import android.os.Build;
import android.text.TextUtils;
import com.exteragram.messenger.ai.AiConfig;
import com.google.mlkit.genai.common.DownloadCallback;
import com.google.mlkit.genai.common.GenAiException;
import com.google.mlkit.genai.prompt.Generation;
import com.google.mlkit.genai.prompt.GenerationConfig;
import com.google.mlkit.genai.prompt.GenerativeModel;
import com.google.mlkit.genai.prompt.ModelConfig;
import com.google.mlkit.genai.prompt.java.GenerativeModelFutures;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.UserConfig;

/* JADX INFO: loaded from: classes4.dex */
public abstract class OnDeviceAvailability {
    private static volatile String modelName;
    private static volatile int modelNameGeneration;
    private static volatile boolean otherModelSupported;
    private static boolean recheckRequested;
    private static volatile boolean supportedThisSession;
    private static volatile boolean systemPromptSupported;
    private static volatile boolean thinkingSupported;
    private static volatile int tokenLimit;
    private static final AtomicBoolean refreshing = new AtomicBoolean();
    private static final Set<Integer> downloadingModels = ConcurrentHashMap.newKeySet();
    private static final Map<Integer, Integer> downloadPercents = new ConcurrentHashMap();
    private static final Map<Integer, Integer> sessionStatuses = new ConcurrentHashMap();
    private static final AtomicBoolean restored = new AtomicBoolean();
    private static final AtomicBoolean refreshedOnce = new AtomicBoolean();
    private static final AtomicBoolean warmedUp = new AtomicBoolean();
    private static final AtomicInteger configGeneration = new AtomicInteger();
    private static final List<Runnable> pendingCallbacks = new ArrayList();
    private static volatile int status = -1;

    public interface DownloadListener {
        void onCompleted();

        void onFailed(Exception exc);

        void onProgress(float f, long j);

        void onStarted(long j);
    }

    private static boolean isStatusSupported(int i) {
        return i == 3 || i == 1 || i == 2;
    }

    public static int getModelKey() {
        return (AiConfig.getOnDevicePreviewModel() ? 2 : 0) | (AiConfig.getOnDeviceFastModel() ? 1 : 0);
    }

    public static GenerativeModel createModel() {
        return createModel(getModelKey());
    }

    private static GenerativeModel createModel(int i) {
        ModelConfig.Builder builder = ModelConfig.builder();
        builder.setReleaseStage((i & 2) != 0 ? 1 : 0);
        builder.setPreference((i & 1) == 0 ? 2 : 1);
        GenerationConfig.Builder builder2 = new GenerationConfig.Builder();
        builder2.setModelConfig(builder.build());
        return Generation.INSTANCE.getClient(builder2.build());
    }

    public static int getConfigGeneration() {
        return configGeneration.get();
    }

    public static void onModelConfigChanged(Runnable runnable) {
        ensureRestored();
        configGeneration.incrementAndGet();
        warmedUp.set(false);
        thinkingSupported = false;
        systemPromptSupported = false;
        tokenLimit = 0;
        AiConfig.getEditor().remove("onDeviceThinkingSupported").remove("onDeviceSystemPromptSupported").remove("onDeviceTokenLimit").apply();
        refresh(runnable);
    }

    public static int getStatus() {
        if (Build.VERSION.SDK_INT < 26) {
            return 0;
        }
        ensureRestored();
        if (refreshedOnce.compareAndSet(false, true)) {
            refresh(null);
        }
        return status;
    }

    public static boolean isReady() {
        return getStatus() == 3;
    }

    public static String getModelName() {
        ensureRestored();
        return modelName;
    }

    public static boolean isThinkingSupported() {
        ensureRestored();
        return thinkingSupported;
    }

    public static boolean isSystemPromptSupported() {
        ensureRestored();
        return systemPromptSupported;
    }

    public static int getTokenLimit() {
        ensureRestored();
        return tokenLimit;
    }

    public static void ensureMetadata(GenerativeModelFutures generativeModelFutures, int i) {
        ensureRestored();
        if (tokenLimit > 0) {
            return;
        }
        readMetadata(generativeModelFutures, i);
    }

    public static boolean isSupported() {
        return isStatusSupported(getStatus()) || supportedThisSession || otherModelSupported;
    }

    public static boolean needsDownload() {
        return getStatus() == 1;
    }

    public static boolean isDownloading() {
        return downloadingModels.contains(Integer.valueOf(getModelKey()));
    }

    public static boolean isDownloadInProgress() {
        return isDownloading() || getStatus() == 2;
    }

    public static int getDownloadPercent() {
        Integer num = downloadPercents.get(Integer.valueOf(getModelKey()));
        if (num != null) {
            return num.intValue();
        }
        return -1;
    }

    public static void report(int i, int i2) {
        ensureRestored();
        refreshedOnce.set(true);
        if (i2 == configGeneration.get()) {
            apply(i);
        }
    }

    public static void refresh(Runnable runnable) {
        if (Build.VERSION.SDK_INT < 26) {
            apply(0);
            runOnDone(runnable);
            return;
        }
        List<Runnable> list = pendingCallbacks;
        synchronized (list) {
            if (runnable != null) {
                try {
                    list.add(runnable);
                } catch (Throwable th) {
                    throw th;
                }
            }
            if (!refreshing.compareAndSet(false, true)) {
                recheckRequested = true;
                return;
            }
            ensureRestored();
            refreshedOnce.set(true);
            runOnWorker(new Runnable() { // from class: com.exteragram.messenger.ai.network.backend.OnDeviceAvailability$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    OnDeviceAvailability.check();
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:24:0x0068 A[LOOP:1: B:23:0x0066->B:24:0x0068, LOOP_END] */
    public static void check() {
        List<Runnable> list;
        int i;
        ArrayList arrayList = new ArrayList();
        int size;
        while (true) {
            AtomicInteger atomicInteger = configGeneration;
            int i2 = atomicInteger.get();
            int modelKey = getModelKey();
            int iCheckStatus = checkStatus(i2);
            if (i2 == atomicInteger.get()) {
                sessionStatuses.put(Integer.valueOf(modelKey), Integer.valueOf(iCheckStatus));
                if (iCheckStatus == 0) {
                    updateOtherModelSupported(modelKey);
                }
                if (modelNameGeneration != i2 && modelName != null) {
                    modelName = null;
                    AiConfig.getEditor().remove("onDeviceModelName").apply();
                    notifyServicesUpdated();
                }
                apply(iCheckStatus);
                list = pendingCallbacks;
                synchronized (list) {
                    i = 0;
                    if (!recheckRequested && i2 == atomicInteger.get()) {
                        break;
                    }
                    recheckRequested = false;
                }
                size = arrayList.size();
                while (i < size) {
                    Object obj = arrayList.get(i);
                    i++;
                    runOnDone((Runnable) obj);
                }
            }
        }
        refreshing.set(false);
        arrayList = new ArrayList(list);
        list.clear();
        size = arrayList.size();
        while (i < size) {
            Object obj2 = arrayList.get(i);
            i++;
            runOnDone((Runnable) obj2);
        }
    }

    private static void updateOtherModelSupported(int i) {
        boolean zIsStatusSupported = false;
        for (int i2 = 0; i2 <= 3 && !zIsStatusSupported; i2++) {
            if (i2 != i) {
                Map<Integer, Integer> map = sessionStatuses;
                Integer numValueOf = map.get(Integer.valueOf(i2));
                if (numValueOf == null) {
                    numValueOf = Integer.valueOf(probeStatus(i2));
                    map.put(Integer.valueOf(i2), numValueOf);
                }
                zIsStatusSupported = isStatusSupported(numValueOf.intValue());
            }
        }
        if (zIsStatusSupported != otherModelSupported) {
            otherModelSupported = zIsStatusSupported;
            AiConfig.getEditor().putBoolean("onDeviceOtherModelSupported", zIsStatusSupported).apply();
            notifyServicesUpdated();
        }
    }

    private static int probeStatus(int i) {
        GenerativeModel generativeModelCreateModel = null;
        try {
            generativeModelCreateModel = createModel(i);
            Integer num = GenerativeModelFutures.from(generativeModelCreateModel).checkStatus().get(10L, TimeUnit.SECONDS);
            return num == null ? -1 : num.intValue();
        } catch (Throwable th) {
            FileLog.e("AI_ON_DEVICE_PROBE_FAILED", th);
            return 0;
        } finally {
            closeQuietly(generativeModelCreateModel);
        }
    }

    private static int checkStatus(int i) {
        GenerativeModel generativeModelCreateModel = null;
        try {
            generativeModelCreateModel = createModel();
            GenerativeModelFutures generativeModelFuturesFrom = GenerativeModelFutures.from(generativeModelCreateModel);
            Integer num = generativeModelFuturesFrom.checkStatus().get(10L, TimeUnit.SECONDS);
            int iIntValue = num == null ? -1 : num.intValue();
            if (iIntValue == 3) {
                readMetadata(generativeModelFuturesFrom, i);
            } else if (isStatusSupported(iIntValue) && readModelName(generativeModelFuturesFrom, i)) {
                notifyServicesUpdated();
            }
            return iIntValue;
        } catch (Throwable th) {
            FileLog.e("AI_ON_DEVICE_STATUS_FAILED", th);
            return 0;
        } finally {
            closeQuietly(generativeModelCreateModel);
        }
    }

    private static boolean readModelName(GenerativeModelFutures generativeModelFutures, int i) {
        try {
            String str = generativeModelFutures.getBaseModelName().get(10L, TimeUnit.SECONDS);
            if (i == configGeneration.get() && !TextUtils.isEmpty(str)) {
                modelNameGeneration = i;
                if (str.equals(modelName)) {
                    return false;
                }
                modelName = str;
                AiConfig.getEditor().putString("onDeviceModelName", str).apply();
                return true;
            }
            return false;
        } catch (Exception e) {
            FileLog.e("AI_ON_DEVICE_MODEL_NAME", e);
            return false;
        }
    }

    private static void readMetadata(GenerativeModelFutures generativeModelFutures, int i) {
        TimeUnit timeUnit = TimeUnit.SECONDS;
        boolean modelName2 = readModelName(generativeModelFutures, i);
        try {
            boolean zEquals = Boolean.TRUE.equals(generativeModelFutures.isThinkingModeAvailable().get(10L, timeUnit));
            if (i != configGeneration.get()) {
                return;
            }
            if (zEquals != thinkingSupported) {
                thinkingSupported = zEquals;
                AiConfig.getEditor().putBoolean("onDeviceThinkingSupported", zEquals).apply();
                modelName2 = true;
            }
        } catch (Exception e) {
            FileLog.e("AI_ON_DEVICE_THINKING", e);
        }
        try {
            boolean zEquals2 = Boolean.TRUE.equals(generativeModelFutures.isSystemPromptAvailable().get(10L, timeUnit));
            if (i != configGeneration.get()) {
                return;
            }
            if (zEquals2 != systemPromptSupported) {
                systemPromptSupported = zEquals2;
                AiConfig.getEditor().putBoolean("onDeviceSystemPromptSupported", zEquals2).apply();
            }
        } catch (Exception e2) {
            FileLog.e("AI_ON_DEVICE_SYSTEM_PROMPT", e2);
        }
        try {
            Integer num = generativeModelFutures.getTokenLimit().get(10L, timeUnit);
            if (i != configGeneration.get()) {
                return;
            }
            if (num != null && num.intValue() > 0 && num.intValue() != tokenLimit) {
                tokenLimit = num.intValue();
                AiConfig.getEditor().putInt("onDeviceTokenLimit", num.intValue()).apply();
            }
        } catch (Exception e3) {
            FileLog.e("AI_ON_DEVICE_TOKEN_LIMIT", e3);
        }
        if (modelName2) {
            notifyServicesUpdated();
        }
    }

    public static void warmup() {
        if (AiConfig.ON_DEVICE_SERVICE.getId().equals(AiConfig.getSelectedServiceId()) && isReady() && warmedUp.compareAndSet(false, true)) {
            runOnWorker(new Runnable() { // from class: com.exteragram.messenger.ai.network.backend.OnDeviceAvailability$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    OnDeviceAvailability.m1023$r8$lambda$KicUEHGdAl2FIno2ohu8c5fiD0();
                }
            });
        }
    }

    /* JADX INFO: renamed from: $r8$lambda$KicUEHGdAl2FIno-2ohu8c5fiD0, reason: not valid java name */
    public static /* synthetic */ void m1023$r8$lambda$KicUEHGdAl2FIno2ohu8c5fiD0() {
        GenerativeModel generativeModelCreateModel = null;
        try {
            generativeModelCreateModel = createModel();
            GenerativeModelFutures.from(generativeModelCreateModel).warmup().get(30L, TimeUnit.SECONDS);
        } catch (InterruptedException unused) {
            Thread.currentThread().interrupt();
            warmedUp.set(false);
        } catch (Throwable th) {
            warmedUp.set(false);
            FileLog.e("AI_ON_DEVICE_WARMUP", th);
        } finally {
            closeQuietly(generativeModelCreateModel);
        }
    }

    public static void download(final int i, final DownloadListener downloadListener) {
        if (Build.VERSION.SDK_INT < 26) {
            downloadListener.onFailed(new IllegalStateException("unsupported android version"));
        } else if (downloadingModels.add(Integer.valueOf(i))) {
            downloadPercents.remove(Integer.valueOf(i));
            notifyServicesUpdated();
            runOnWorker(new Runnable() { // from class: com.exteragram.messenger.ai.network.backend.OnDeviceAvailability$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    try {
                        OnDeviceAvailability.runDownload(i, downloadListener);
                    } catch (Throwable t) {
                        FileLog.e(t);
                    }
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void runDownload(final int i, final DownloadListener downloadListener) throws Throwable {
        GenerativeModel generativeModel = null;
        try {
            GenerativeModel generativeModelCreateModel = createModel(i);
            generativeModel = generativeModelCreateModel;
            GenerativeModelFutures generativeModelFuturesFrom = GenerativeModelFutures.from(generativeModelCreateModel);
            final long[] jArr = {0};
            generativeModelFuturesFrom.download(new DownloadCallback() {
                @Override
                public void onDownloadStarted(long j) {
                    jArr[0] = j;
                    downloadListener.onStarted(j);
                }

                @Override
                public void onDownloadProgress(long j) {
                    long j2 = jArr[0];
                    float fMin = j2 > 0 ? Math.min(1.0f, j / j2) : -1.0f;
                    int i2 = fMin < 0.0f ? -1 : (int) (100.0f * fMin);
                    Integer num = (Integer) OnDeviceAvailability.downloadPercents.put(Integer.valueOf(i), Integer.valueOf(i2));
                    if (num == null || num.intValue() != i2) {
                        OnDeviceAvailability.notifyServicesUpdated();
                        downloadListener.onProgress(fMin, j);
                    }
                }

                @Override
                public void onDownloadFailed(GenAiException genAiException) {
                    FileLog.e("AI_ON_DEVICE_DOWNLOAD_FAILED", genAiException);
                }
            }).get();
            int iIntValue = generativeModelFuturesFrom.checkStatus().get(10L, TimeUnit.SECONDS).intValue();
            if (i == getModelKey()) {
                refresh(null);
            }
            if (iIntValue == 3) {
                downloadListener.onProgress(1.0f, jArr[0]);
                downloadListener.onCompleted();
            } else if (iIntValue != 2) {
                downloadListener.onFailed(new IllegalStateException("model is not available after download, status=" + iIntValue));
            }
            closeQuietly(generativeModelCreateModel);
            downloadPercents.remove(Integer.valueOf(i));
            downloadingModels.remove(Integer.valueOf(i));
            notifyServicesUpdated();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            FileLog.e("AI_ON_DEVICE_DOWNLOAD", e);
            downloadListener.onFailed(e);
            closeQuietly(generativeModel);
            downloadPercents.remove(Integer.valueOf(i));
            downloadingModels.remove(Integer.valueOf(i));
            notifyServicesUpdated();
        } catch (Exception e2) {
            FileLog.e("AI_ON_DEVICE_DOWNLOAD", e2);
            downloadListener.onFailed(e2);
            closeQuietly(generativeModel);
            downloadPercents.remove(Integer.valueOf(i));
            downloadingModels.remove(Integer.valueOf(i));
            notifyServicesUpdated();
        } catch (Throwable th) {
            closeQuietly(generativeModel);
            downloadPercents.remove(Integer.valueOf(i));
            downloadingModels.remove(Integer.valueOf(i));
            notifyServicesUpdated();
            throw th;
        }
    }

    private static void ensureRestored() {
        if (restored.compareAndSet(false, true)) {
            status = AiConfig.getPreferences().getInt("onDeviceFeatureStatus", -1);
            modelName = AiConfig.getPreferences().getString("onDeviceModelName", null);
            thinkingSupported = AiConfig.getPreferences().getBoolean("onDeviceThinkingSupported", false);
            systemPromptSupported = AiConfig.getPreferences().getBoolean("onDeviceSystemPromptSupported", false);
            tokenLimit = AiConfig.getPreferences().getInt("onDeviceTokenLimit", 0);
            otherModelSupported = AiConfig.getPreferences().getBoolean("onDeviceOtherModelSupported", false);
        }
    }

    private static void apply(int i) {
        if (isStatusSupported(i)) {
            supportedThisSession = true;
        }
        if (status == i) {
            return;
        }
        status = i;
        AiConfig.getEditor().putInt("onDeviceFeatureStatus", i).apply();
        notifyServicesUpdated();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void notifyServicesUpdated() {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.ai.network.backend.OnDeviceAvailability$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationName(NotificationCenter.servicesUpdated);
            }
        });
    }

    private static void runOnWorker(final Runnable runnable) {
        final ExecutorService executorServiceNewSingleThreadExecutor = Executors.newSingleThreadExecutor();
        executorServiceNewSingleThreadExecutor.execute(new Runnable() { // from class: com.exteragram.messenger.ai.network.backend.OnDeviceAvailability$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                OnDeviceAvailability.$r8$lambda$edA7BpxC7ENFv9jOjeLPcqoKwKs(runnable, executorServiceNewSingleThreadExecutor);
            }
        });
    }

    public static /* synthetic */ void $r8$lambda$edA7BpxC7ENFv9jOjeLPcqoKwKs(Runnable runnable, ExecutorService executorService) {
        try {
            runnable.run();
        } finally {
            executorService.shutdown();
        }
    }

    private static void closeQuietly(GenerativeModel generativeModel) {
        if (generativeModel == null) {
            return;
        }
        try {
            generativeModel.close();
        } catch (Throwable unused) {
        }
    }

    private static void runOnDone(Runnable runnable) {
        if (runnable != null) {
            AndroidUtilities.runOnUIThread(runnable);
        }
    }
}
