package com.exteragram.messenger.camera;

import android.bluetooth.BluetoothAdapter;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTimestamp;
import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaFormat;
import android.opengl.EGL14;
import android.opengl.EGLConfig;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLExt;
import android.opengl.EGLSurface;
import android.opengl.GLUtils;
import android.os.SystemClock;
import android.view.Surface;
import com.exteragram.messenger.utils.system.SystemUtils;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.mvel2.ast.Instance$$ExternalSyntheticBUOutline0;
import org.mvel2.util.Make$Map$$ExternalSyntheticBUOutline0;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.video.MP4Builder;
import org.telegram.messenger.video.MediaCodecVideoConvertor;
import org.telegram.messenger.video.Mp4Movie;
import org.telegram.ui.Components.PermissionRequest;
import org.webrtc.EglBase;
import org.webrtc.GlShader$$ExternalSyntheticBUOutline1;
import org.webrtc.MediaStreamTrack;

public class RoundVideoEncoder {
    private AudioCaptureSession activeAudioCapture;
    private boolean allowSendingWhileRecording;
    private boolean audioBatchesPrepared;
    private MediaCodec.BufferInfo audioBufferInfo;
    private long audioCapUs;
    private int audioCaptureGeneration;
    private boolean audioCaptureRunning;
    private MediaCodec audioEncoder;
    private boolean audioEosQueued;
    private boolean audioEosSeen;
    private long audioSegmentFramesSubmitted;
    private long audioTotalEndUs;
    private final Callback callback;
    private EGLConfig eglConfig;
    private final DispatchQueue encoderQueue;
    private File fileToWrite;
    private boolean firstEncode;
    private Surface inputSurface;
    private final boolean isSecretChat;
    private long lastSourceTimestampNs;
    private long lastVideoActiveTimeNs;
    private long maxDurationUs;
    private MP4Builder mediaMuxer;
    private long minVideoFrameDeltaNs;
    private File pausePreviewFile;
    private boolean pendingFrameSet;
    private EGLContext pendingResumeContext;
    private int prependHeaderSize;
    private final Renderer renderer;
    private long segmentActiveBaseNs;
    private long sourceAnchorMonotonicNs;
    private boolean sourceAnchorSet;
    private long sourceAnchorSourceNs;
    private volatile boolean started;
    private int videoBitrate;
    private MediaCodec.BufferInfo videoBufferInfo;
    private MediaCodec videoEncoder;
    private boolean videoEosSeen;
    private boolean videoEosSignalled;
    private File videoFile;
    private int videoHeight;
    private int videoWidth;
    private boolean waitingAudioTail;
    private boolean writingToDifferentFile;
    private final AtomicBoolean finishRequested = new AtomicBoolean(false);
    private volatile int state = 0;
    private int frameRate = 30;
    private int videoTrackIndex = -5;
    private int audioTrackIndex = -5;
    private EGLDisplay eglDisplay = EGL14.EGL_NO_DISPLAY;
    private EGLContext eglContext = EGL14.EGL_NO_CONTEXT;
    private EGLSurface eglSurface = EGL14.EGL_NO_SURFACE;
    private final Object pendingFrameLock = new Object();
    private final FrameSnapshot pendingFrame = new FrameSnapshot();
    private final FrameSnapshot currentFrame = new FrameSnapshot();
    private int lastCameraId = Integer.MIN_VALUE;
    private long segmentFirstArrivalNs = -1;
    private long segmentVideoOriginNs = -1;
    private long lastVideoFrameIndex = -1;
    private long lastSubmittedVideoPtsUs = -1;
    private long lastMuxedVideoPtsUs = -1;
    private final Object audioCaptureLock = new Object();
    private final ArrayBlockingQueue<AudioChunkBatch> audioBatchPool = new ArrayBlockingQueue<>(25);
    private final ArrayList<AudioChunkBatch> pendingAudio = new ArrayList<>();
    private long audioSegmentBaseUs = -1;
    private long lastSubmittedAudioEndUs = -1;
    private long lastMuxedAudioPtsUs = -1;
    private int deferredFinish = 0;
    private int deferredAudioCleanup = 0;

    public interface Callback {
        void onAudioAmplitude(double d);

        void onFinished(FinishReason finishReason);

        void onPaused(File file);

        void onRecordingStarted(boolean z);

        void onWriteData(long j);
    }

    public enum FinishReason {
        COMPLETED,
        CANCELLED,
        FAILED
    }

    public interface Renderer {
        boolean onDrawEncoderFrame(long j, FrameSnapshot frameSnapshot);

        void onEncoderSurfaceCreated(int i, int i2);

        void onEncoderSurfaceDestroyed();
    }

    public static final class FrameSnapshot {
        public long arrivalTimeNs;
        public int cameraId;
        public int previewHeight;
        public int previewWidth;
        public long sourceTimestampNs;
        public int surfaceIndex;
        public int textureId;
        public final float[] stMatrix = new float[16];
        public final float[] mvpMatrix = new float[16];
        public final float[] textureCoords = new float[8];

        public void copyFrom(FrameSnapshot frameSnapshot) {
            this.sourceTimestampNs = frameSnapshot.sourceTimestampNs;
            this.arrivalTimeNs = frameSnapshot.arrivalTimeNs;
            this.cameraId = frameSnapshot.cameraId;
            this.surfaceIndex = frameSnapshot.surfaceIndex;
            this.textureId = frameSnapshot.textureId;
            System.arraycopy(frameSnapshot.stMatrix, 0, this.stMatrix, 0, 16);
            System.arraycopy(frameSnapshot.mvpMatrix, 0, this.mvpMatrix, 0, 16);
            System.arraycopy(frameSnapshot.textureCoords, 0, this.textureCoords, 0, 8);
            this.previewWidth = frameSnapshot.previewWidth;
            this.previewHeight = frameSnapshot.previewHeight;
        }
    }

    public class AudioChunkBatch {
        public int drained;
        public int results;
        public final byte[][] data = new byte[10][];
        public final ByteBuffer[] buffer = new ByteBuffer[10];
        public final long[] startTimeNs = new long[10];
        public final Runnable deliveryRunnable = new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$0();
            }
        };

        public void lambda$new$0() {
            RoundVideoEncoder.this.handleAudioCaptureFinished(this);
        }

        public void requestStop() {
            boolean zCompareAndSet = this.stopRequested.compareAndSet(false, true);
            Thread thread = this.thread;
            if (thread != null && thread != Thread.currentThread()) {
                thread.interrupt();
            }
            if (!zCompareAndSet || Utilities.globalQueue.postRunnable(this.stopRunnable)) {
                return;
            }
            FileLog.e("RoundVideoEncoder unable to schedule AudioRecord stop");
        }

        public void stopRecorder() {
            synchronized (this.recorderLock) {
                try {
                    if (!this.recorderReleased) {
                        RoundVideoEncoder.this.stopAudioRecorder(this.audioRecorder);
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
        }

        public void releaseRecorder() {
            synchronized (this.recorderLock) {
                try {
                    if (this.recorderReleased) {
                        return;
                    }
                    this.recorderReleased = true;
                    RoundVideoEncoder.this.releaseAudioRecorder(this.audioRecorder);
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public RoundVideoEncoder(Renderer renderer, Callback callback, boolean z) {
        this.renderer = renderer;
        this.callback = callback;
        this.isSecretChat = z;
        DispatchQueue dispatchQueue = new DispatchQueue("RoundVideoEncoder", false);
        this.encoderQueue = dispatchQueue;
        dispatchQueue.setPriority(10);
        dispatchQueue.start();
    }

    public boolean isStarted() {
        return this.started;
    }

    public void startRecording(final File file, final EGLContext eGLContext, final int i) {
        this.started = true;
        this.encoderQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$startRecording$0(file, eGLContext, i);
            }
        });
    }

    public void frameAvailable(FrameSnapshot frameSnapshot) {
        if (this.started) {
            synchronized (this.pendingFrameLock) {
                try {
                    this.pendingFrame.copyFrom(frameSnapshot);
                    if (this.pendingFrameSet) {
                        return;
                    }
                    this.pendingFrameSet = true;
                    this.encoderQueue.postRunnable(new Runnable() { 
                        @Override // java.lang.Runnable
                        public final void run() {
                            this.f$0.handleFrame();
                        }
                    });
                } catch (Throwable th) {
                    throw th;
                }
            }
        }
    }

    public void pause(final File file) {
        AudioCaptureSession activeAudioCapture = getActiveAudioCapture();
        this.encoderQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$pause$1(file);
            }
        });
        requestAudioCaptureStop(activeAudioCapture);
    }

    public void stop() {
        if (this.finishRequested.compareAndSet(false, true)) {
            AudioCaptureSession activeAudioCapture = getActiveAudioCapture();
            this.encoderQueue.postRunnable(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$stop$2();
                }
            });
            requestAudioCaptureStop(activeAudioCapture);
        }
    }

    public void lambda$cancel$3() {
        handleFinish(true);
    }

    public void lambda$startRecording$0(File file, EGLContext eGLContext, int i) {
        if (this.state == 4) {
            handleResume(eGLContext);
            return;
        }
        if (this.state == 3) {
            this.pendingResumeContext = eGLContext;
            return;
        }
        if (this.state != 0) {
            return;
        }
        this.state = 1;
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("RoundVideoEncoder start " + file);
        }
        try {
            this.videoFile = file;
            int roundVideoResolution = SystemUtils.getRoundVideoResolution();
            this.videoWidth = roundVideoResolution;
            this.videoHeight = roundVideoResolution;
            this.videoBitrate = SystemUtils.getRoundVideoBitrate() * 1024;
            int i2 = 60;
            if (i != 60) {
                i2 = 30;
            }
            this.frameRate = i2;
            long roundVideoMaxDurationMs = SystemUtils.getRoundVideoMaxDurationMs() * 1000;
            this.maxDurationUs = roundVideoMaxDurationMs;
            this.audioCapUs = roundVideoMaxDurationMs;
            this.allowSendingWhileRecording = SharedConfig.deviceIsHigh();
            prepareAudioBatches();
            this.videoBufferInfo = new MediaCodec.BufferInfo();
            this.audioBufferInfo = new MediaCodec.BufferInfo();
            MediaFormat mediaFormat = new MediaFormat();
            mediaFormat.setString("mime", MediaController.AUDIO_MIME_TYPE);
            mediaFormat.setInteger("sample-rate", 48000);
            mediaFormat.setInteger("channel-count", 1);
            mediaFormat.setInteger("bitrate", SystemUtils.getRoundAudioBitrate() * 1024);
            mediaFormat.setInteger("max-input-size", 20480);
            MediaCodec mediaCodecCreateEncoderByType = MediaCodec.createEncoderByType(MediaController.AUDIO_MIME_TYPE);
            this.audioEncoder = mediaCodecCreateEncoderByType;
            mediaCodecCreateEncoderByType.configure(mediaFormat, (Surface) null, (MediaCrypto) null, 1);
            this.audioEncoder.start();
            MediaFormat mediaFormatCreateVideoFormat = MediaFormat.createVideoFormat(MediaController.VIDEO_MIME_TYPE, this.videoWidth, this.videoHeight);
            mediaFormatCreateVideoFormat.setInteger("color-format", 2130708361);
            mediaFormatCreateVideoFormat.setInteger("bitrate", this.videoBitrate);
            mediaFormatCreateVideoFormat.setInteger("max-bitrate", this.videoBitrate);
            MediaCodec mediaCodecCreateEncoderByType2 = MediaCodec.createEncoderByType(MediaController.VIDEO_MIME_TYPE);
            this.videoEncoder = mediaCodecCreateEncoderByType2;
            try {
                if (mediaCodecCreateEncoderByType2.getCodecInfo().getCapabilitiesForType(MediaController.VIDEO_MIME_TYPE).getEncoderCapabilities().isBitrateModeSupported(2)) {
                    mediaFormatCreateVideoFormat.setInteger("bitrate-mode", 2);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
            mediaFormatCreateVideoFormat.setInteger("frame-rate", this.frameRate);
            mediaFormatCreateVideoFormat.setInteger("i-frame-interval", 1);
            this.videoEncoder.configure(mediaFormatCreateVideoFormat, (Surface) null, (MediaCrypto) null, 1);
            this.inputSurface = this.videoEncoder.createInputSurface();
            this.videoEncoder.start();
            this.firstEncode = true;
            File file2 = this.videoFile;
            this.fileToWrite = file2;
            this.writingToDifferentFile = false;
            if (ImageLoader.isSdCardPath(file2)) {
                try {
                    File file3 = new File(ApplicationLoader.getFilesDirFixed(), "camera_tmp.mp4");
                    this.fileToWrite = file3;
                    if (file3.exists()) {
                        this.fileToWrite.delete();
                    }
                    this.writingToDifferentFile = true;
                } catch (Throwable th) {
                    FileLog.e(th);
                    this.fileToWrite = this.videoFile;
                    this.writingToDifferentFile = false;
                }
            }
            Mp4Movie mp4Movie = new Mp4Movie();
            mp4Movie.setCacheFile(this.fileToWrite);
            mp4Movie.setRotation(0);
            mp4Movie.setSize(this.videoWidth, this.videoHeight);
            MP4Builder mP4BuilderCreateMovie = new MP4Builder().createMovie(mp4Movie, this.isSecretChat, false);
            this.mediaMuxer = mP4BuilderCreateMovie;
            mP4BuilderCreateMovie.setAllowSyncFiles(false);
            createEncoderEgl(eGLContext);
            startAudioCapture();
            this.state = 2;
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$handleStart$4();
                }
            });
        } catch (Throwable th2) {
            FileLog.e(th2);
            fail();
        }
    }

    public void lambda$handleResume$5() {
        this.callback.onRecordingStarted(true);
    }

    void lambda$finishPause$6(File file) {
        this.callback.onPaused(file);
    }

    private void handleFinish(boolean z) {
        if (this.state == 6 || this.state == 7 || this.state == 5) {
            return;
        }
        if (this.state == 3) {
            this.deferredFinish = z ? 2 : 1;
            return;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("RoundVideoEncoder finish cancel=" + z + " state=" + this.state);
        }
        if (this.state == 0) {
            this.state = 6;
            scheduleQueueRecycle();
            final FinishReason finishReason = z ? FinishReason.CANCELLED : FinishReason.COMPLETED;
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$handleFinish$7(finishReason);
                }
            });
            return;
        }
        this.state = 5;
        if (z) {
            if (stopAudioCapture(true)) {
                finalizeCancel();
                return;
            } else {
                this.deferredAudioCleanup = 1;
                return;
            }
        }
        this.audioCapUs = Math.min(this.audioCapUs, videoEndTimeUs());
        MediaCodec mediaCodec = this.videoEncoder;
        if (mediaCodec != null && !this.videoEosSignalled) {
            try {
                mediaCodec.signalEndOfInputStream();
                this.videoEosSignalled = true;
            } catch (Exception e) {
                FileLog.e(e);
                this.videoEosSeen = true;
            }
        }
        if (this.audioCaptureRunning) {
            stopAudioCapture(false);
            this.waitingAudioTail = true;
        } else {
            finalizeStop();
        }
    }

    public void lambda$finalizeStop$8(FinishReason finishReason) {
        this.callback.onFinished(finishReason);
    }

    private void finalizeCancel() {
        releaseEgl();
        releaseCodecs();
        releaseInputSurface();
        recyclePendingAudio();
        MP4Builder mP4Builder = this.mediaMuxer;
        if (mP4Builder != null) {
            try {
                mP4Builder.finishMovie();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        File file = this.fileToWrite;
        if (file != null) {
            try {
                file.delete();
            } catch (Throwable unused) {
            }
        }
        File file2 = this.videoFile;
        if (file2 != null) {
            try {
                file2.delete();
            } catch (Throwable unused2) {
            }
        }
        this.state = 6;
        scheduleQueueRecycle();
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$finalizeCancel$9();
            }
        });
    }

    public void lambda$finalizeFailure$10() {
        this.callback.onFinished(FinishReason.FAILED);
    }

    private boolean hasReachedPauseTargets() {
        long j = this.lastSubmittedVideoPtsUs;
        boolean z = j < 0 || this.lastMuxedVideoPtsUs >= j;
        long j2 = this.lastSubmittedAudioEndUs;
        long jMax = j2 < 0 ? -1L : Math.max(0L, j2 - 21333);
        return z && ((jMax > 0L ? 1 : (jMax == 0L ? 0 : -1)) < 0 || (this.lastMuxedAudioPtsUs > jMax ? 1 : (this.lastMuxedAudioPtsUs == jMax ? 0 : -1)) >= 0);
    }

    private void scheduleQueueRecycle() {
        final DispatchQueue dispatchQueue = this.encoderQueue;
        Objects.requireNonNull(dispatchQueue);
        dispatchQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                dispatchQueue.recycle();
            }
        });
    }

    private long videoFallbackFrameDurationNs() {
        long j = this.minVideoFrameDeltaNs;
        if (j > 0) {
            return Math.max(j, 1000000L);
        }
        return 1000000000 / ((long) this.frameRate);
    }

    private long videoEndTimeUs() {
        if (this.lastVideoFrameIndex < 0) {
            return 0L;
        }
        return (this.lastVideoActiveTimeNs + videoFallbackFrameDurationNs()) / 1000;
    }

    private boolean drainToEndOfStream() {
        long jElapsedRealtime = SystemClock.elapsedRealtime() + 5000;
        if (this.audioEncoder == null) {
            this.audioEosSeen = true;
        }
        if (this.videoEncoder == null || !this.videoEosSignalled) {
            this.videoEosSeen = true;
        }
        while (true) {
            if ((this.videoEosSeen && this.audioEosSeen) || SystemClock.elapsedRealtime() >= jElapsedRealtime) {
                break;
            }
            if (!this.audioEosQueued && !this.audioEosSeen) {
                feedPendingAudio();
                if (!hasFeedablePendingAudio()) {
                    queueAudioEndOfStream();
                }
            }
            try {
                if (!this.videoEosSeen) {
                    drainVideoOnce(10000L);
                }
                if (!this.audioEosSeen) {
                    drainAudioOnce(10000L);
                }
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
        if (!this.videoEosSeen || !this.audioEosSeen) {
            FileLog.e("RoundVideoEncoder end of stream drain timed out, video=" + this.videoEosSeen + " audio=" + this.audioEosSeen);
        }
        return this.videoEosSeen && this.audioEosSeen;
    }

    private void queueAudioEndOfStream() {
        try {
            int iDequeueInputBuffer = this.audioEncoder.dequeueInputBuffer(10000L);
            if (iDequeueInputBuffer >= 0) {
                this.audioEncoder.queueInputBuffer(iDequeueInputBuffer, 0, 0, this.audioTotalEndUs, 4);
                this.audioEosQueued = true;
            }
        } catch (Exception e) {
            FileLog.e(e);
            this.audioEosSeen = true;
        }
    }

    private void drainEncoders() {
        if (this.videoEncoder != null) {
            while (drainVideoOnce(0L)) {
            }
        }
        if (this.audioEncoder != null) {
            while (drainAudioOnce(0L)) {
            }
        }
    }

    private boolean drainVideoOnce(long j) {
        int iDequeueOutputBuffer;
        ByteBuffer byteBufferAllocate;
        ByteBuffer byteBufferAllocate2;
        int i;
        MP4Builder mP4Builder;
        MediaCodec mediaCodec = this.videoEncoder;
        if (mediaCodec == null || (iDequeueOutputBuffer = mediaCodec.dequeueOutputBuffer(this.videoBufferInfo, j)) == -1) {
            return false;
        }
        if (iDequeueOutputBuffer == -3) {
            return true;
        }
        if (iDequeueOutputBuffer == -2) {
            MediaFormat outputFormat = this.videoEncoder.getOutputFormat();
            if (this.videoTrackIndex == -5 && (mP4Builder = this.mediaMuxer) != null) {
                this.videoTrackIndex = mP4Builder.addTrack(outputFormat, false);
                if (outputFormat.containsKey("prepend-sps-pps-to-idr-frames") && outputFormat.getInteger("prepend-sps-pps-to-idr-frames") == 1) {
                    ByteBuffer byteBuffer = outputFormat.getByteBuffer("csd-0");
                    ByteBuffer byteBuffer2 = outputFormat.getByteBuffer("csd-1");
                    this.prependHeaderSize = (byteBuffer == null ? 0 : byteBuffer.limit()) + (byteBuffer2 != null ? byteBuffer2.limit() : 0);
                }
            }
            return true;
        }
        if (iDequeueOutputBuffer < 0) {
            return false;
        }
        try {
            ByteBuffer outputBuffer = this.videoEncoder.getOutputBuffer(iDequeueOutputBuffer);
            if (outputBuffer == null) {
                throw new RuntimeException("videoEncoderOutputBuffer " + iDequeueOutputBuffer + " was null");
            }
            MediaCodec.BufferInfo bufferInfo = this.videoBufferInfo;
            int i2 = bufferInfo.size;
            if (i2 > 1) {
                int i3 = bufferInfo.flags;
                if ((i3 & 2) == 0) {
                    int i4 = this.prependHeaderSize;
                    if (i4 != 0 && (i3 & 1) != 0) {
                        bufferInfo.offset += i4;
                        bufferInfo.size = i2 - i4;
                    }
                    if (this.firstEncode && (i3 & 1) != 0) {
                        MediaCodecVideoConvertor.cutOfNalData(MediaController.VIDEO_MIME_TYPE, outputBuffer, bufferInfo);
                        this.firstEncode = false;
                    }
                    MP4Builder mP4Builder2 = this.mediaMuxer;
                    if (mP4Builder2 != null && (i = this.videoTrackIndex) >= 0) {
                        long jWriteSampleData = mP4Builder2.writeSampleData(i, outputBuffer, this.videoBufferInfo, true);
                        this.lastMuxedVideoPtsUs = Math.max(this.lastMuxedVideoPtsUs, this.videoBufferInfo.presentationTimeUs);
                        if (jWriteSampleData != 0 && !this.writingToDifferentFile && this.allowSendingWhileRecording) {
                            this.callback.onWriteData(jWriteSampleData);
                        }
                    }
                } else if (this.videoTrackIndex == -5 && this.mediaMuxer != null) {
                    byte[] bArr = new byte[i2];
                    outputBuffer.limit(bufferInfo.offset + i2);
                    outputBuffer.position(this.videoBufferInfo.offset);
                    outputBuffer.get(bArr);
                    int i5 = this.videoBufferInfo.size - 1;
                    while (true) {
                        if (i5 < 0 || i5 <= 3) {
                            byteBufferAllocate = null;
                            byteBufferAllocate2 = null;
                            break;
                        }
                        if (bArr[i5] == 1 && bArr[i5 - 1] == 0 && bArr[i5 - 2] == 0) {
                            int i6 = i5 - 3;
                            if (bArr[i6] == 0) {
                                byteBufferAllocate = ByteBuffer.allocate(i6);
                                byteBufferAllocate2 = ByteBuffer.allocate(this.videoBufferInfo.size - i6);
                                byteBufferAllocate.put(bArr, 0, i6).position(0);
                                byteBufferAllocate2.put(bArr, i6, this.videoBufferInfo.size - i6).position(0);
                                break;
                            }
                        }
                        i5--;
                    }
                    MediaFormat mediaFormatCreateVideoFormat = MediaFormat.createVideoFormat(MediaController.VIDEO_MIME_TYPE, this.videoWidth, this.videoHeight);
                    if (byteBufferAllocate != null) {
                        mediaFormatCreateVideoFormat.setByteBuffer("csd-0", byteBufferAllocate);
                        mediaFormatCreateVideoFormat.setByteBuffer("csd-1", byteBufferAllocate2);
                    }
                    this.videoTrackIndex = this.mediaMuxer.addTrack(mediaFormatCreateVideoFormat, false);
                }
            }
            if ((this.videoBufferInfo.flags & 4) == 0) {
                this.videoEncoder.releaseOutputBuffer(iDequeueOutputBuffer, false);
                return true;
            }
            this.videoEosSeen = true;
            this.videoEncoder.releaseOutputBuffer(iDequeueOutputBuffer, false);
            return false;
        } catch (Throwable th) {
            this.videoEncoder.releaseOutputBuffer(iDequeueOutputBuffer, false);
            throw th;
        }
    }

    private boolean drainAudioOnce(long j) {
        int iDequeueOutputBuffer;
        MP4Builder mP4Builder;
        int i;
        MP4Builder mP4Builder2;
        MediaCodec mediaCodec = this.audioEncoder;
        if (mediaCodec == null || (iDequeueOutputBuffer = mediaCodec.dequeueOutputBuffer(this.audioBufferInfo, j)) == -1) {
            return false;
        }
        if (iDequeueOutputBuffer == -3) {
            return true;
        }
        if (iDequeueOutputBuffer == -2) {
            MediaFormat outputFormat = this.audioEncoder.getOutputFormat();
            if (this.audioTrackIndex == -5 && (mP4Builder2 = this.mediaMuxer) != null) {
                this.audioTrackIndex = mP4Builder2.addTrack(outputFormat, true);
            }
            return true;
        }
        if (iDequeueOutputBuffer < 0) {
            return false;
        }
        try {
            ByteBuffer outputBuffer = this.audioEncoder.getOutputBuffer(iDequeueOutputBuffer);
            if (outputBuffer == null) {
                throw new RuntimeException("audioEncoderOutputBuffer " + iDequeueOutputBuffer + " was null");
            }
            MediaCodec.BufferInfo bufferInfo = this.audioBufferInfo;
            if ((bufferInfo.flags & 2) != 0) {
                bufferInfo.size = 0;
            }
            if (bufferInfo.size != 0 && (mP4Builder = this.mediaMuxer) != null && (i = this.audioTrackIndex) >= 0) {
                long jWriteSampleData = mP4Builder.writeSampleData(i, outputBuffer, bufferInfo, false);
                this.lastMuxedAudioPtsUs = Math.max(this.lastMuxedAudioPtsUs, this.audioBufferInfo.presentationTimeUs);
                if (jWriteSampleData != 0 && !this.writingToDifferentFile && this.allowSendingWhileRecording) {
                    this.callback.onWriteData(jWriteSampleData);
                }
            }
            if ((this.audioBufferInfo.flags & 4) == 0) {
                this.audioEncoder.releaseOutputBuffer(iDequeueOutputBuffer, false);
                return true;
            }
            this.audioEosSeen = true;
            this.audioEncoder.releaseOutputBuffer(iDequeueOutputBuffer, false);
            return false;
        } catch (Throwable th) {
            this.audioEncoder.releaseOutputBuffer(iDequeueOutputBuffer, false);
            throw th;
        }
    }

    private void createEncoderEgl(EGLContext eGLContext) {
        if (this.eglDisplay != EGL14.EGL_NO_DISPLAY) {
            GlShader$$ExternalSyntheticBUOutline1.m("EGL already set up");
            return;
        }
        EGLDisplay eGLDisplayEglGetDisplay = EGL14.eglGetDisplay(0);
        this.eglDisplay = eGLDisplayEglGetDisplay;
        if (eGLDisplayEglGetDisplay == EGL14.EGL_NO_DISPLAY) {
            GlShader$$ExternalSyntheticBUOutline1.m("unable to get EGL14 display");
            return;
        }
        int[] iArr = new int[2];
        if (!EGL14.eglInitialize(eGLDisplayEglGetDisplay, iArr, 0, iArr, 1)) {
            this.eglDisplay = EGL14.EGL_NO_DISPLAY;
            GlShader$$ExternalSyntheticBUOutline1.m("unable to initialize EGL14");
            return;
        }
        EGLConfig[] eGLConfigArr = new EGLConfig[1];
        if (!EGL14.eglChooseConfig(this.eglDisplay, new int[]{12324, 8, 12323, 8, 12322, 8, 12321, 8, 12352, 4, EglBase.EGL_RECORDABLE_ANDROID, 1, 12344}, 0, eGLConfigArr, 0, 1, new int[1], 0)) {
            GlShader$$ExternalSyntheticBUOutline1.m("Unable to find a suitable EGLConfig");
            return;
        }
        EGLConfig eGLConfig = eGLConfigArr[0];
        this.eglConfig = eGLConfig;
        EGLContext eGLContextEglCreateContext = EGL14.eglCreateContext(this.eglDisplay, eGLConfig, eGLContext, new int[]{12440, 2, 12344}, 0);
        this.eglContext = eGLContextEglCreateContext;
        if (eGLContextEglCreateContext == null || eGLContextEglCreateContext == EGL14.EGL_NO_CONTEXT) {
            this.eglContext = EGL14.EGL_NO_CONTEXT;
            Instance$$ExternalSyntheticBUOutline0.m("eglCreateContext failed ", GLUtils.getEGLErrorString(EGL14.eglGetError()));
            return;
        }
        EGLSurface eGLSurfaceEglCreateWindowSurface = EGL14.eglCreateWindowSurface(this.eglDisplay, this.eglConfig, this.inputSurface, new int[]{12344}, 0);
        this.eglSurface = eGLSurfaceEglCreateWindowSurface;
        if (eGLSurfaceEglCreateWindowSurface == null || eGLSurfaceEglCreateWindowSurface == EGL14.EGL_NO_SURFACE) {
            this.eglSurface = EGL14.EGL_NO_SURFACE;
            Instance$$ExternalSyntheticBUOutline0.m("eglCreateWindowSurface failed ", GLUtils.getEGLErrorString(EGL14.eglGetError()));
        } else {
            if (!EGL14.eglMakeCurrent(this.eglDisplay, eGLSurfaceEglCreateWindowSurface, eGLSurfaceEglCreateWindowSurface, this.eglContext)) {
                Instance$$ExternalSyntheticBUOutline0.m("eglMakeCurrent failed ", GLUtils.getEGLErrorString(EGL14.eglGetError()));
                return;
            }
            try {
                this.renderer.onEncoderSurfaceCreated(this.videoWidth, this.videoHeight);
            } catch (Throwable th) {
                Make$Map$$ExternalSyntheticBUOutline0.m("encoder renderer initialization failed", th);
            }
        }
    }

    private boolean makeEglCurrent() {
        if (this.eglDisplay == EGL14.EGL_NO_DISPLAY || this.eglSurface == EGL14.EGL_NO_SURFACE) {
            return false;
        }
        if (this.eglContext.equals(EGL14.eglGetCurrentContext()) && this.eglSurface.equals(EGL14.eglGetCurrentSurface(12377))) {
            return true;
        }
        EGLDisplay eGLDisplay = this.eglDisplay;
        EGLSurface eGLSurface = this.eglSurface;
        if (EGL14.eglMakeCurrent(eGLDisplay, eGLSurface, eGLSurface, this.eglContext)) {
            return true;
        }
        FileLog.e("RoundVideoEncoder eglMakeCurrent failed " + GLUtils.getEGLErrorString(EGL14.eglGetError()));
        return false;
    }

    private void releaseEgl() {
        if (this.eglDisplay == EGL14.EGL_NO_DISPLAY) {
            return;
        }
        if (makeEglCurrent()) {
            try {
                this.renderer.onEncoderSurfaceDestroyed();
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        EGLSurface eGLSurface = this.eglSurface;
        if (eGLSurface != EGL14.EGL_NO_SURFACE) {
            EGL14.eglDestroySurface(this.eglDisplay, eGLSurface);
            this.eglSurface = EGL14.EGL_NO_SURFACE;
        }
        EGLDisplay eGLDisplay = this.eglDisplay;
        EGLSurface eGLSurface2 = EGL14.EGL_NO_SURFACE;
        EGL14.eglMakeCurrent(eGLDisplay, eGLSurface2, eGLSurface2, EGL14.EGL_NO_CONTEXT);
        EGLContext eGLContext = this.eglContext;
        if (eGLContext != EGL14.EGL_NO_CONTEXT) {
            EGL14.eglDestroyContext(this.eglDisplay, eGLContext);
            this.eglContext = EGL14.EGL_NO_CONTEXT;
        }
        EGL14.eglReleaseThread();
        EGL14.eglTerminate(this.eglDisplay);
        this.eglDisplay = EGL14.EGL_NO_DISPLAY;
        this.eglConfig = null;
    }

    private void releaseInputSurface() {
        Surface surface = this.inputSurface;
        if (surface != null) {
            try {
                surface.release();
            } catch (Throwable th) {
                FileLog.e(th);
            }
            this.inputSurface = null;
        }
    }

    private void releaseCodecs() {
        MediaCodec mediaCodec = this.videoEncoder;
        if (mediaCodec != null) {
            try {
                mediaCodec.stop();
            } catch (Throwable th) {
                FileLog.e(th);
            }
            try {
                this.videoEncoder.release();
            } catch (Throwable th2) {
                FileLog.e(th2);
            }
            this.videoEncoder = null;
        }
        MediaCodec mediaCodec2 = this.audioEncoder;
        if (mediaCodec2 != null) {
            try {
                mediaCodec2.stop();
            } catch (Throwable th3) {
                FileLog.e(th3);
            }
            try {
                this.audioEncoder.release();
            } catch (Throwable th4) {
                FileLog.e(th4);
            }
            this.audioEncoder = null;
            setBluetoothScoOn(false);
        }
    }

    private void recyclePendingAudio() {
        int i = 0;
        while (true) {
            int size = this.pendingAudio.size();
            ArrayList<AudioChunkBatch> arrayList = this.pendingAudio;
            if (i < size) {
                recycleAudioBatch(arrayList.get(i));
                i++;
            } else {
                arrayList.clear();
                return;
            }
        }
    }

    private void prepareAudioBatches() {
        if (this.audioBatchesPrepared) {
            return;
        }
        for (int i = 0; i < 25; i++) {
            this.audioBatchPool.add(new AudioChunkBatch());
        }
        this.audioBatchesPrepared = true;
    }

    public AudioChunkBatch obtainAudioBatch() throws InterruptedException {
        AudioChunkBatch audioChunkBatchPoll = this.audioBatchPool.poll(250L, TimeUnit.MILLISECONDS);
        if (audioChunkBatchPoll == null) {
            return null;
        }
        audioChunkBatchPoll.results = 0;
        audioChunkBatchPoll.drained = 0;
        return audioChunkBatchPoll;
    }

    public void recycleAudioBatch(AudioChunkBatch audioChunkBatch) {
        if (this.audioBatchPool.offer(audioChunkBatch)) {
            return;
        }
        FileLog.e("RoundVideoEncoder audio batch pool overflow");
    }

    private void startAudioCapture() throws Throwable {
        AudioCaptureSession audioCaptureSession;
        setBluetoothScoOn(true);
        int minBufferSize = AudioRecord.getMinBufferSize(48000, 16, 2);
        if (minBufferSize <= 0) {
            minBufferSize = 3584;
        }
        int i = 49152 < minBufferSize ? ((minBufferSize / 2048) + 1) * 4096 : 49152;
        AudioRecord audioRecord = new AudioRecord(0, 48000, 16, 2, i);
        try {
            if (audioRecord.getState() != 1) {
                throw new RuntimeException("AudioRecord init failed");
            }
            audioRecord.startRecording();
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("RoundVideoEncoder initied audio record with channels " + audioRecord.getChannelCount() + " sample rate = " + audioRecord.getSampleRate() + " bufferSize = " + i);
            }
            this.audioCaptureRunning = true;
            int i2 = this.audioCaptureGeneration + 1;
            this.audioCaptureGeneration = i2;
            audioCaptureSession = new AudioCaptureSession(audioRecord, i2);
            try {
                Thread thread = new Thread(new AudioCaptureRunnable(audioCaptureSession), "RoundVideoAudioCapture");
                audioCaptureSession.thread = thread;
                thread.setPriority(10);
                synchronized (this.audioCaptureLock) {
                    this.activeAudioCapture = audioCaptureSession;
                }
                thread.start();
            } catch (Throwable th) {
                th = th;
                synchronized (this.audioCaptureLock) {
                    try {
                        if (this.activeAudioCapture == audioCaptureSession) {
                            this.activeAudioCapture = null;
                        }
                    } catch (Throwable th2) {
                        throw th2;
                    }
                }
                this.audioCaptureRunning = false;
                if (audioCaptureSession != null) {
                    audioCaptureSession.releaseRecorder();
                } else {
                    releaseAudioRecorder(audioRecord);
                }
                throw th;
            }
        } catch (Throwable th3) {
            th = th3;
            audioCaptureSession = null;
        }
    }

    private AudioCaptureSession getActiveAudioCapture() {
        AudioCaptureSession audioCaptureSession;
        synchronized (this.audioCaptureLock) {
            audioCaptureSession = this.activeAudioCapture;
        }
        return audioCaptureSession;
    }

    private void requestAudioCaptureStop(AudioCaptureSession audioCaptureSession) {
        if (audioCaptureSession != null) {
            audioCaptureSession.requestStop();
        }
    }

    private boolean stopAudioCapture(boolean z) {
        AudioCaptureSession activeAudioCapture = getActiveAudioCapture();
        requestAudioCaptureStop(activeAudioCapture);
        if (activeAudioCapture == null) {
            return true;
        }
        Thread thread = activeAudioCapture.thread;
        if (z && thread != null && thread != Thread.currentThread()) {
            try {
                thread.join(1500L);
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
            }
            if (thread.isAlive()) {
                FileLog.e("RoundVideoEncoder audio thread did not stop within timeout");
            }
        }
        return thread == null || !thread.isAlive();
    }

    public void stopAudioRecorder(AudioRecord audioRecord) {
        try {
            if (audioRecord.getRecordingState() != 1) {
                audioRecord.stop();
            }
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public void releaseAudioRecorder(AudioRecord audioRecord) {
        stopAudioRecorder(audioRecord);
        try {
            audioRecord.release();
        } catch (Throwable th) {
            FileLog.e(th);
        }
    }

    public class AudioCaptureRunnable implements Runnable {
        private final AudioTimestamp audioTimestamp = new AudioTimestamp();
        private final AudioCaptureSession session;

        public AudioCaptureRunnable(AudioCaptureSession audioCaptureSession) {
            this.session = audioCaptureSession;
        }

        /* JADX WARN: Code duplicated, block: B:106:0x01b2 A[Catch: all -> 0x01b8, TryCatch #1 {all -> 0x01b8, blocks: (B:104:0x01a8, B:106:0x01b2, B:109:0x01ba), top: B:120:0x01a8 }] */
        /* JADX WARN: Code duplicated, block: B:120:0x01a8 A[EXC_TOP_SPLITTER, SYNTHETIC] */
        @Override // java.lang.Runnable
        public void run() {
            int i;
            int i2;
            long jNanoTime;
            long j = 0;
            int i3 = 0;
            boolean z = false;
            while (!z) {
                try {
                    if (this.session.stopRequested.get()) {
                        break;
                    }
                    try {
                        AudioChunkBatch audioChunkBatchObtainAudioBatch = RoundVideoEncoder.this.obtainAudioBatch();
                        AudioCaptureSession audioCaptureSession = this.session;
                        if (audioChunkBatchObtainAudioBatch == null) {
                            if (!audioCaptureSession.stopRequested.get()) {
                                FileLog.e("RoundVideoEncoder audio batch pool stalled");
                                break;
                            }
                            break;
                        }
                        try {
                            if (audioCaptureSession.stopRequested.get()) {
                                RoundVideoEncoder.this.recycleAudioBatch(audioChunkBatchObtainAudioBatch);
                                z = true;
                            } else {
                                try {
                                    i = this.session.audioRecorder.getTimestamp(this.audioTimestamp, i3) == 0 ? 1 : i3;
                                } catch (Exception unused) {
                                    i = i3;
                                }
                                int i4 = i3;
                                while (i4 < 10) {
                                    ByteBuffer byteBuffer = audioChunkBatchObtainAudioBatch.buffer[i4];
                                    byteBuffer.clear();
                                    int i5 = this.session.audioRecorder.read(audioChunkBatchObtainAudioBatch.data[i4], i3, 2048);
                                    if (i5 > 0) {
                                        byteBuffer.position(i3);
                                        byteBuffer.limit(i5);
                                        if (i4 % 2 == 0) {
                                            double d = 0.0d;
                                            int i6 = i3;
                                            while (i6 < i5 / 2) {
                                                short s = byteBuffer.getShort();
                                                d += (double) (s * s);
                                                i6++;
                                                i = i;
                                            }
                                            i2 = i;
                                            byteBuffer.position(i3);
                                            try {
                                                RoundVideoEncoder.this.callback.onAudioAmplitude(Math.sqrt((d / ((double) i5)) / 2.0d));
                                            } catch (Throwable th) {
                                                FileLog.e(th);
                                            }
                                        } else {
                                            i2 = i;
                                        }
                                        long j2 = i5 / 2;
                                        if (i2 != 0) {
                                            AudioTimestamp audioTimestamp = this.audioTimestamp;
                                            jNanoTime = audioTimestamp.nanoTime + (((j - audioTimestamp.framePosition) * 1000000000) / 48000);
                                        } else {
                                            jNanoTime = System.nanoTime() - ((1000000000 * j2) / 48000);
                                        }
                                        audioChunkBatchObtainAudioBatch.startTimeNs[i4] = jNanoTime;
                                        i4++;
                                        audioChunkBatchObtainAudioBatch.results = i4;
                                        j += j2;
                                        if (!this.session.stopRequested.get()) {
                                            z = z;
                                            i = i2;
                                            i3 = 0;
                                        }
                                    }
                                    z = true;
                                }
                                if (!(audioChunkBatchObtainAudioBatch.results > 0 ? postBatch(audioChunkBatchObtainAudioBatch) : false)) {
                                    RoundVideoEncoder.this.recycleAudioBatch(audioChunkBatchObtainAudioBatch);
                                }
                                i3 = 0;
                            }
                        } catch (Throwable th2) {
                            RoundVideoEncoder.this.recycleAudioBatch(audioChunkBatchObtainAudioBatch);
                            throw th2;
                        }
                    } catch (InterruptedException unused2) {
                        boolean z2 = z;
                        if (this.session.stopRequested.get()) {
                            this.session.releaseRecorder();
                            synchronized (RoundVideoEncoder.this.audioCaptureLock) {
                                try {
                                    if (RoundVideoEncoder.this.activeAudioCapture == this.session) {
                                        RoundVideoEncoder.this.activeAudioCapture = null;
                                    }
                                    if (RoundVideoEncoder.this.encoderQueue.getHandler().post(this.session.completionRunnable)) {
                                        return;
                                    } else {
                                        return;
                                    }
                                } catch (Throwable th3) {
                                    throw th3;
                                }
                            }
                        }
                        z = z2;
                    }
                } catch (Throwable th4) {
                    try {
                        FileLog.e(th4);
                        this.session.releaseRecorder();
                        synchronized (RoundVideoEncoder.this.audioCaptureLock) {
                            try {
                                if (RoundVideoEncoder.this.activeAudioCapture == this.session) {
                                    RoundVideoEncoder.this.activeAudioCapture = null;
                                }
                                if (RoundVideoEncoder.this.encoderQueue.getHandler().post(this.session.completionRunnable) || !BuildVars.LOGS_ENABLED) {
                                    return;
                                }
                            } catch (Throwable th5) {
                                throw th5;
                            }
                        }
                    } catch (Throwable th6) {
                        this.session.releaseRecorder();
                        synchronized (RoundVideoEncoder.this.audioCaptureLock) {
                            try {
                                if (RoundVideoEncoder.this.activeAudioCapture == this.session) {
                                    RoundVideoEncoder.this.activeAudioCapture = null;
                                }
                                if (!RoundVideoEncoder.this.encoderQueue.getHandler().post(this.session.completionRunnable) && BuildVars.LOGS_ENABLED) {
                                    FileLog.e("RoundVideoEncoder unable to post audio capture completion");
                                }
                                throw th6;
                            } catch (Throwable th7) {
                                throw th7;
                            }
                        }
                    }
                }
            }
            this.session.releaseRecorder();
            synchronized (RoundVideoEncoder.this.audioCaptureLock) {
                if (RoundVideoEncoder.this.activeAudioCapture == this.session) {
                    RoundVideoEncoder.this.activeAudioCapture = null;
                }
            }
            if (RoundVideoEncoder.this.encoderQueue.getHandler().post(this.session.completionRunnable) || !BuildVars.LOGS_ENABLED) {
                return;
            }
            FileLog.e("RoundVideoEncoder unable to post audio capture completion");
        }

        private boolean postBatch(AudioChunkBatch audioChunkBatch) {
            return RoundVideoEncoder.this.encoderQueue.getHandler().post(audioChunkBatch.deliveryRunnable);
        }
    }

    /* JADX WARN: Code duplicated, block: B:45:? A[RETURN, SYNTHETIC] */
    private void setBluetoothScoOn(boolean z) {
        AudioManager audioManager = (AudioManager) ApplicationLoader.applicationContext.getSystemService(MediaStreamTrack.AUDIO_TRACK_KIND);
        if (SharedConfig.recordViaSco && !PermissionRequest.hasPermission("android.permission.BLUETOOTH_CONNECT")) {
            SharedConfig.recordViaSco = false;
            SharedConfig.saveConfig();
        }
        if (!(audioManager.isBluetoothScoAvailableOffCall() && SharedConfig.recordViaSco) && z) {
            return;
        }
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        if (defaultAdapter != null) {
            try {
                if (defaultAdapter.getProfileConnectionState(1) != 2) {
                    if (z) {
                        return;
                    }
                }
            } catch (SecurityException unused) {
                return;
            } catch (Throwable th) {
                FileLog.e(th);
                if (z) {
                    return;
                }
                try {
                    if (audioManager.isBluetoothScoOn()) {
                        audioManager.stopBluetoothSco();
                        return;
                    }
                    return;
                } catch (Exception e) {
                    FileLog.e(e);
                    return;
                }
            }
        } else if (z) {
            return;
        }
        if (z && !audioManager.isBluetoothScoOn()) {
            audioManager.startBluetoothSco();
        } else {
            if (z || !audioManager.isBluetoothScoOn()) {
                return;
            }
            audioManager.stopBluetoothSco();
        }
    }
}
