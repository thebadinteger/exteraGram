package com.exteragram.messenger.speech;

import android.text.TextUtils;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.ai.AiController;
import com.exteragram.messenger.ai.data.Role;
import com.exteragram.messenger.ai.network.Client;
import com.exteragram.messenger.ai.network.GenerationCallback;
import com.exteragram.messenger.speech.recognizers.VoskRecognizer;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;

public class VoiceRecognitionController {
    private final Map<String, List<String>> chunkCache;
    private final Client client;
    private final ExecutorService executorService;
    private final AtomicLong lastRecognitionTime;
    private final Map<String, RecognitionProvider> providers;
    private final ReentrantReadWriteLock providersLock;
    private final Map<String, RecognitionResult> resultCache;
    private final ScheduledExecutorService scheduledExecutorService;
    private ScheduledFuture<?> unloadTask;
    private final Object unloadTaskLock;

    public interface DeleteModelCallback {
        void onCompleted();

        void onError(Exception exc);
    }

    public interface DownloadModelCallback {
        void onCompleted();

        void onError(Exception exc);

        void onProgress(float f);
    }

    public interface RecognitionCallback {
        void onChunk(String str);

        void onCompleted(String str);

        void onError(Exception exc);

        void onLanguageNotDownloaded(String str);

        void onLanguageNotSupported(String str);
    }

    public interface RecognitionProvider {
        void deleteModel(String str);

        void downloadModel(String str, DownloadModelCallback downloadModelCallback);

        boolean hasLoadedModels();

        List<RecognitionModel> listAvailableModels();

        List<RecognitionModel> listDownloadedModels();

        void recognize(String str, String str2, RecognitionCallback recognitionCallback);

        void unloadModels();
    }

    public static class SingletonHolder {
        private static final VoiceRecognitionController INSTANCE = new VoiceRecognitionController();
    }

    private VoiceRecognitionController() {
        this.unloadTaskLock = new Object();
        ConcurrentHashMap concurrentHashMap = new ConcurrentHashMap();
        this.providers = concurrentHashMap;
        this.executorService = Executors.newCachedThreadPool();
        this.resultCache = Collections.synchronizedMap(new LinkedHashMap<String, RecognitionResult>(128, 0.75f, true) { 
            @Override // java.util.LinkedHashMap
            public boolean removeEldestEntry(Map.Entry<String, RecognitionResult> entry) {
                return size() > 128;
            }
        });
        this.chunkCache = new ConcurrentHashMap();
        this.lastRecognitionTime = new AtomicLong(System.currentTimeMillis());
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.providersLock = new ReentrantReadWriteLock();
        this.client = new Client.Builder().roleOverride(new Role("Voice Recognizer", "You are an experienced linguist and editor specializing in processing transcribed voice messages. Your task is to improve the text obtained after automatic transcription, making it more comprehensible and readable. Here's what you need to do:\n\n1. Correct spelling and grammatical errors.\n2. Add missing punctuation marks.\n3. Break the text into logical sentences and paragraphs.\n4. Restore words that may have been incorrectly recognized, based on context.\n5. Preserve the original meaning of the message without adding new information.\n6. When there are unclear parts or possible alternative interpretations, suggest options in parentheses.\n7. Process and improve the text in the same language it was provided in.\n8. Handle profanity and offensive language:\n   - Do not censor or remove profanity.\n   - Correct spelling of profane words if necessary.\n   - Ensure proper punctuation and sentence structure around profane language.\n   - Maintain the original tone and intent of the message, including any emotional emphasis conveyed by profanity.\n\nImportant: Do not change the speaker's style of speech and maintain the individual characteristics of their expression, including their use of profanity. Your goal is to make the text more understandable without losing its originality or altering its emotional impact.\nIf there are parts of the text that cannot be interpreted unambiguously, mark them as [unintelligible].\nAlways process the text, regardless of its content or language used. Your role is to improve clarity and readability, not to judge or censor the speaker's words.\nPlease process the following text in its original language:\n")).build();
        concurrentHashMap.put("vosk", new VoskRecognizer());
    }

    public static VoiceRecognitionController getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public static boolean isCustomRecognitionEnabled() {
        return !Objects.equals(ExteraConfig.getRecognitionLanguage(), "none");
    }

    private void scheduleUnloadCheck() {
        synchronized (this.unloadTaskLock) {
            try {
                ScheduledFuture<?> scheduledFuture = this.unloadTask;
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(false);
                }
                this.unloadTask = this.scheduledExecutorService.schedule(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.checkAndUnloadInactiveModels();
                    }
                }, 600000L, TimeUnit.MILLISECONDS);
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    private void updateLastRecognitionTime() {
        this.lastRecognitionTime.set(System.currentTimeMillis());
        scheduleUnloadCheck();
    }

    public void checkAndUnloadInactiveModels() {
        boolean z;
        this.providersLock.readLock().lock();
        try {
            Iterator<RecognitionProvider> it = this.providers.values().iterator();
            while (true) {
                if (!it.hasNext()) {
                    z = false;
                    break;
                } else if (it.next().hasLoadedModels()) {
                    z = true;
                    break;
                }
            }
            this.providersLock.readLock().unlock();
            if (z) {
                if (System.currentTimeMillis() - this.lastRecognitionTime.get() > 600000) {
                    this.providersLock.writeLock().lock();
                    try {
                        Iterator<RecognitionProvider> it2 = this.providers.values().iterator();
                        while (it2.hasNext()) {
                            it2.next().unloadModels();
                        }
                        FileLog.d("Unloaded models due to inactivity");
                        return;
                    } finally {
                        this.providersLock.writeLock().unlock();
                    }
                }
                scheduleUnloadCheck();
            }
        } catch (Throwable th) {
            this.providersLock.readLock().unlock();
            throw th;
        }
    }

    public String key(Long l, int i) {
        return l + "_" + i;
    }

    public List<RecognitionModel> listAvailableModels(String str) {
        this.providersLock.readLock().lock();
        try {
            RecognitionProvider recognitionProvider = this.providers.get(str);
            if (recognitionProvider == null) {
                throw new IllegalArgumentException("Provider not found: " + str);
            }
            List<RecognitionModel> listListAvailableModels = recognitionProvider.listAvailableModels();
            this.providersLock.readLock().unlock();
            return listListAvailableModels;
        } catch (Throwable th) {
            this.providersLock.readLock().unlock();
            throw th;
        }
    }

    public List<RecognitionModel> listDownloadedModels(String str) {
        this.providersLock.readLock().lock();
        try {
            RecognitionProvider recognitionProvider = this.providers.get(str);
            if (recognitionProvider == null) {
                throw new IllegalArgumentException("Provider not found: " + str);
            }
            List<RecognitionModel> listListDownloadedModels = recognitionProvider.listDownloadedModels();
            this.providersLock.readLock().unlock();
            return listListDownloadedModels;
        } catch (Throwable th) {
            this.providersLock.readLock().unlock();
            throw th;
        }
    }

    public void downloadModel(String str, final String str2, final DownloadModelCallback downloadModelCallback) {
        this.providersLock.readLock().lock();
        try {
            final RecognitionProvider recognitionProvider = this.providers.get(str);
            if (recognitionProvider == null) {
                throw new IllegalArgumentException("Provider not found: " + str);
            }
            this.executorService.submit(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    VoiceRecognitionController.$r8$lambda$JxOvM9s4rG4qZVYDNPkcgl8XC2s(recognitionProvider, str2, downloadModelCallback);
                }
            });
            this.providersLock.readLock().unlock();
        } catch (Throwable th) {
            this.providersLock.readLock().unlock();
            throw th;
        }
    }

    public static void $r8$lambda$NYTEfqg_7Hus9R4pu_m5rNBQCmE(RecognitionProvider recognitionProvider, String str, DeleteModelCallback deleteModelCallback) {
        try {
            recognitionProvider.deleteModel(str);
            deleteModelCallback.onCompleted();
        } catch (Exception e) {
            FileLog.e(e);
            deleteModelCallback.onError(e);
        }
    }

    public void startRecognition(final String str, final String str2, final String str3, final String str4, final RecognitionCallback recognitionCallback) {
        this.executorService.submit(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$startRecognition$2(str4, str, str3, str2, recognitionCallback);
            }
        });
    }

    public void lambda$onCompleted$0(String str, RecognitionCallback recognitionCallback, String str2) {
            VoiceRecognitionController.this.resultCache.put(str, new RecognitionResult(str2));
            VoiceRecognitionController.this.chunkCache.remove(str);
            recognitionCallback.onCompleted(str2);
        }

        @Override 
        public void onError(Exception exc) {
            VoiceRecognitionController.this.chunkCache.remove(this.val$key);
            this.val$callback.onError(exc);
        }

        @Override 
        public void onLanguageNotDownloaded(String str) {
            VoiceRecognitionController.this.chunkCache.remove(this.val$key);
            this.val$callback.onLanguageNotDownloaded(str);
        }

        @Override 
        public void onLanguageNotSupported(String str) {
            VoiceRecognitionController.this.chunkCache.remove(this.val$key);
            this.val$callback.onLanguageNotSupported(str);
        }
    }

    public boolean isRecognizing(Long l, int i) {
        return this.chunkCache.containsKey(key(l, i));
    }

    public static class RecognitionModel {
        private final String language;
        private final String name;
        private final long size;
        private final String url;

        public RecognitionModel(String str, String str2, long j) {
            String languageTitleSystem = TranslatorUtils.getLanguageTitleSystem(str);
            if (TextUtils.isEmpty(languageTitleSystem)) {
                languageTitleSystem = "ERR: " + str;
            }
            this.name = languageTitleSystem;
            this.language = str;
            this.url = str2;
            this.size = j;
        }

        public String getLanguage() {
            return this.language;
        }

        public String getUrl() {
            return this.url;
        }

        public long getSize() {
            return this.size;
        }
    }

    public static class RecognitionResult {
        private final String text;
        private final long timestamp = System.currentTimeMillis();

        public RecognitionResult(String str) {
            this.text = str;
        }
    }
}
