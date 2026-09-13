package org.vosk.android;

import android.os.Handler;
import android.os.Looper;
import java.io.IOException;
import java.io.InputStream;
import org.vosk.Recognizer;

/* JADX INFO: loaded from: C:\Users\badinteger\Files\.mess\extera\dex_files\classes7.dex */
public class SpeechStreamService {
    private final int bufferSize;
    private final InputStream inputStream;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Recognizer recognizer;
    private Thread recognizerThread;
    private final int sampleRate;

    public SpeechStreamService(Recognizer recognizer, InputStream inputStream, float f) {
        this.recognizer = recognizer;
        int i = (int) f;
        this.sampleRate = i;
        this.inputStream = inputStream;
        this.bufferSize = Math.round(i * 0.2f * 2.0f);
    }

    public boolean start(RecognitionListener recognitionListener) {
        if (this.recognizerThread != null) {
            return false;
        }
        RecognizerThread recognizerThread = new RecognizerThread(this, recognitionListener);
        this.recognizerThread = recognizerThread;
        recognizerThread.start();
        return true;
    }

    public final class RecognizerThread extends Thread {
        RecognitionListener listener;
        private int remainingSamples;
        private final int timeoutSamples;

        public RecognizerThread(RecognitionListener recognitionListener, int i) {
            this.listener = recognitionListener;
            if (i != -1) {
                this.timeoutSamples = (i * SpeechStreamService.this.sampleRate) / 1000;
            } else {
                this.timeoutSamples = -1;
            }
            this.remainingSamples = this.timeoutSamples;
        }

        public RecognizerThread(SpeechStreamService speechStreamService, RecognitionListener recognitionListener) {
            this(recognitionListener, -1);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            int i = SpeechStreamService.this.bufferSize;
            byte[] bArr = new byte[i];
            while (!Thread.interrupted() && (this.timeoutSamples == -1 || this.remainingSamples > 0)) {
                try {
                    int i2 = SpeechStreamService.this.inputStream.read(bArr, 0, i);
                    if (i2 < 0) {
                        break;
                    }
                    boolean zAcceptWaveForm = SpeechStreamService.this.recognizer.acceptWaveForm(bArr, i2);
                    SpeechStreamService speechStreamService = SpeechStreamService.this;
                    if (zAcceptWaveForm) {
                        final String result = speechStreamService.recognizer.getResult();
                        SpeechStreamService.this.mainHandler.post(new Runnable() { // from class: org.vosk.android.SpeechStreamService$RecognizerThread$$ExternalSyntheticLambda0
                            @Override // java.lang.Runnable
                            public final void run() {
                                RecognizerThread.this.listener.onResult(result);
                            }
                        });
                    } else {
                        final String partialResult = speechStreamService.recognizer.getPartialResult();
                        SpeechStreamService.this.mainHandler.post(new Runnable() { // from class: org.vosk.android.SpeechStreamService$RecognizerThread$$ExternalSyntheticLambda1
                            @Override // java.lang.Runnable
                            public final void run() {
                                RecognizerThread.this.listener.onPartialResult(partialResult);
                            }
                        });
                    }
                    if (this.timeoutSamples != -1) {
                        this.remainingSamples -= i2;
                    }
                } catch (IOException e) {
                    SpeechStreamService.this.mainHandler.post(new Runnable() { // from class: org.vosk.android.SpeechStreamService$RecognizerThread$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            RecognizerThread.this.listener.onError(e);
                        }
                    });
                }
            }
            if (this.timeoutSamples == -1 || this.remainingSamples > 0) {
                final String finalResult = SpeechStreamService.this.recognizer.getFinalResult();
                SpeechStreamService.this.mainHandler.post(new Runnable() { // from class: org.vosk.android.SpeechStreamService$RecognizerThread$$ExternalSyntheticLambda4
                    @Override // java.lang.Runnable
                    public final void run() {
                        RecognizerThread.this.listener.onFinalResult(finalResult);
                    }
                });
            } else {
                SpeechStreamService.this.mainHandler.post(new Runnable() { // from class: org.vosk.android.SpeechStreamService$RecognizerThread$$ExternalSyntheticLambda3
                    @Override // java.lang.Runnable
                    public final void run() {
                        RecognizerThread.this.listener.onTimeout();
                    }
                });
            }
        }
    }
}
