package com.exteragram.messenger.speech.utils;

import android.media.MediaCodec;
import android.media.MediaCrypto;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.view.Surface;
import com.google.android.exoplayer2.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import kotlin.UByte;

public abstract class FormatConverter {
    public static int getSampleRate(String str) {
        int integer;
        MediaExtractor mediaExtractor = new MediaExtractor();
        try {
            try {
                mediaExtractor.setDataSource(str);
                int trackCount = mediaExtractor.getTrackCount();
                int i = 0;
                while (true) {
                    if (i < trackCount) {
                        MediaFormat trackFormat = mediaExtractor.getTrackFormat(i);
                        String string = trackFormat.getString("mime");
                        if (string != null && string.startsWith("audio/") && trackFormat.containsKey("sample-rate")) {
                            integer = trackFormat.getInteger("sample-rate");
                            break;
                        }
                        i++;
                    } else {
                        integer = -1;
                        break;
                    }
                }
                mediaExtractor.release();
            } catch (IOException e) {
                Log.e("exteraGram", "Error detecting sample rate", e);
                mediaExtractor.release();
                integer = -1;
            }
            if (integer != -1) {
                return integer;
            }
            return 48000;
        } catch (Throwable th) {
            mediaExtractor.release();
            throw th;
        }
    }

    public static InputStream extractAndConvertToPcm(String str, boolean z) throws IOException {
        return new LazyPcmInputStream(str, z);
    }

    public static class LazyPcmInputStream extends InputStream {
        private final MediaCodec codec;
        private ByteBuffer currentOutputBuffer;
        private final MediaExtractor extractor;
        private final ByteBuffer[] inputBuffers;
        private boolean isEOS;
        private final boolean limitToOneMinute;
        private ByteBuffer[] outputBuffers;
        private long totalDecodedDurationUs;
        private final long oneMinuteUs = 60000000;
        private final MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

        public LazyPcmInputStream(String str, boolean z) throws IOException {
            this.limitToOneMinute = z;
            MediaExtractor mediaExtractor = new MediaExtractor();
            this.extractor = mediaExtractor;
            mediaExtractor.setDataSource(str);
            MediaFormat trackFormat = mediaExtractor.getTrackFormat(0);
            String string = trackFormat.getString("mime");
            if (!string.startsWith("audio/")) {
                throw new IOException("Not an audio file");
            }
            MediaCodec mediaCodecCreateDecoderByType = MediaCodec.createDecoderByType(string);
            this.codec = mediaCodecCreateDecoderByType;
            mediaCodecCreateDecoderByType.configure(trackFormat, (Surface) null, (MediaCrypto) null, 0);
            mediaCodecCreateDecoderByType.start();
            this.inputBuffers = mediaCodecCreateDecoderByType.getInputBuffers();
            this.outputBuffers = mediaCodecCreateDecoderByType.getOutputBuffers();
            mediaExtractor.selectTrack(0);
        }

        @Override // java.io.InputStream
        public int read() {
            byte[] bArr = new byte[1];
            if (read(bArr, 0, 1) == -1) {
                return -1;
            }
            return bArr[0] & UByte.MAX_VALUE;
        }

        @Override // java.io.InputStream
        public int read(byte[] bArr, int i, int i2) {
            if (this.isEOS) {
                return -1;
            }
            int i3 = 0;
            while (i3 < i2 && !this.isEOS) {
                ByteBuffer byteBuffer = this.currentOutputBuffer;
                if (byteBuffer == null || !byteBuffer.hasRemaining()) {
                    ByteBuffer nextOutputBuffer = getNextOutputBuffer();
                    this.currentOutputBuffer = nextOutputBuffer;
                    if (nextOutputBuffer == null) {
                        break;
                    }
                }
                int iMin = Math.min(i2 - i3, this.currentOutputBuffer.remaining());
                this.currentOutputBuffer.get(bArr, i + i3, iMin);
                i3 += iMin;
            }
            if (i3 > 0) {
                return i3;
            }
            return -1;
        }

        private ByteBuffer getNextOutputBuffer() {
            while (!this.isEOS) {
                int iDequeueInputBuffer = this.codec.dequeueInputBuffer(10000L);
                if (iDequeueInputBuffer >= 0) {
                    int sampleData = this.extractor.readSampleData(this.inputBuffers[iDequeueInputBuffer], 0);
                    if (sampleData < 0) {
                        this.codec.queueInputBuffer(iDequeueInputBuffer, 0, 0, 0L, 4);
                        this.isEOS = true;
                    } else {
                        this.codec.queueInputBuffer(iDequeueInputBuffer, 0, sampleData, this.extractor.getSampleTime(), 0);
                        this.extractor.advance();
                    }
                }
                int iDequeueOutputBuffer = this.codec.dequeueOutputBuffer(this.bufferInfo, 10000L);
                if (iDequeueOutputBuffer == -3) {
                    this.outputBuffers = this.codec.getOutputBuffers();
                } else if (iDequeueOutputBuffer != -2 && iDequeueOutputBuffer != -1) {
                    ByteBuffer byteBuffer = this.outputBuffers[iDequeueOutputBuffer];
                    ByteBuffer byteBufferAllocate = ByteBuffer.allocate(this.bufferInfo.size);
                    byteBufferAllocate.put(byteBuffer);
                    byteBufferAllocate.flip();
                    this.codec.releaseOutputBuffer(iDequeueOutputBuffer, false);
                    long j = this.totalDecodedDurationUs;
                    long j2 = j + (this.bufferInfo.presentationTimeUs - j);
                    this.totalDecodedDurationUs = j2;
                    if (this.limitToOneMinute && j2 >= 60000000) {
                        this.isEOS = true;
                    }
                    return byteBufferAllocate;
                }
            }
            return null;
        }

        @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
        public void close() throws IOException {
            this.codec.stop();
            this.codec.release();
            this.extractor.release();
            super.close();
        }
    }
}
