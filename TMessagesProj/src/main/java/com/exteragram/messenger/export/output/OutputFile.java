package com.exteragram.messenger.export.output;

import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.telegram.messenger.FileLog;
import org.telegram.tgnet.NativeByteBuffer;

public class OutputFile {
    public final File _file;
    private boolean _inStats = false;
    private long _offset = 0;
    private final Stats _stats;

    public OutputFile(String str, Stats stats) {
        File file = new File(str);
        this._file = file;
        try {
            if (file.getPath().contains("/")) {
                file.getParentFile().mkdirs();
            }
            file.createNewFile();
            this._stats = stats;
        } catch (IOException e) {
            FileLog.e(e);
            throw new RuntimeException(e);
        }
    }

    public static String PrepareRelativePath(String str, String str2) {
        if (!new File(str + "/" + str2).exists()) {
            return str2;
        }
        int iIndexOf = str2.indexOf(46);
        int i = 0;
        String strSubstring = str2.substring(0, iIndexOf);
        String strSubstring2 = iIndexOf >= 0 ? str2.substring(iIndexOf) : "";
        String str3;
        do {
            i++;
            str3 = strSubstring + " (" + i + ")" + strSubstring2;
        } while (new File(str + str3).exists());
        return str3;
    }

    public long size() {
        return this._offset;
    }

    public boolean empty() {
        return this._offset == 0;
    }

    public AbstractWriter.Result writeBlock(String str) {
        AbstractWriter.Result resultWriteBlockAttempt = writeBlockAttempt(str);
        if (resultWriteBlockAttempt != null && resultWriteBlockAttempt.isSuccess()) {
            return resultWriteBlockAttempt;
        }
        throw new IllegalStateException("result is not success for block: " + str);
    }

    public AbstractWriter.Result writeBlock(NativeByteBuffer nativeByteBuffer) {
        AbstractWriter.Result resultWriteBlockAttempt = writeBlockAttempt(nativeByteBuffer);
        if (resultWriteBlockAttempt != null && resultWriteBlockAttempt.isSuccess()) {
            return resultWriteBlockAttempt;
        }
        throw new IllegalStateException("result is not success for block: " + nativeByteBuffer);
    }

    public AbstractWriter.Result writeBlock(byte[] bArr) {
        AbstractWriter.Result resultWriteBlockAttempt = writeBlockAttempt(bArr);
        if (resultWriteBlockAttempt != null && resultWriteBlockAttempt.isSuccess()) {
            return resultWriteBlockAttempt;
        }
        throw new IllegalStateException("result is not success for block: " + bArr);
    }

    public AbstractWriter.Result writeBlockAttempt(String str) {
        Stats stats = this._stats;
        if (stats != null && !this._inStats) {
            this._inStats = true;
            stats.incrementFiles();
        }
        int length = str.length();
        if (length == 0) {
            Log.e("exteraGram", "size of block to write was zero!");
            return AbstractWriter.Result.Success();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(this._file, true)) {
            fileOutputStream.write(str.getBytes());
            this._offset += (long) length;
            Stats stats2 = this._stats;
            if (stats2 != null) {
                stats2.incrementBytes(length);
            }
            return AbstractWriter.Result.Success();
        } catch (Exception e) {
            FileLog.e(e);
            return AbstractWriter.Result.Error();
        }
    }

    public AbstractWriter.Result writeBlockAttempt(byte[] bArr) {
        Stats stats = this._stats;
        if (stats != null && !this._inStats) {
            this._inStats = true;
            stats.incrementFiles();
        }
        int length = bArr.length;
        if (length == 0) {
            Log.e("exteraGram", "size of block to write was zero!");
            return AbstractWriter.Result.Success();
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(this._file, true)) {
            fileOutputStream.write(bArr);
            this._offset += (long) length;
            Stats stats2 = this._stats;
            if (stats2 != null) {
                stats2.incrementBytes(length);
            }
            return AbstractWriter.Result.Success();
        } catch (Exception e) {
            FileLog.e(e);
            return AbstractWriter.Result.Error();
        }
    }

    public AbstractWriter.Result writeBlockAttempt(NativeByteBuffer nativeByteBuffer) {
        Stats stats = this._stats;
        if (stats != null && !this._inStats) {
            this._inStats = true;
            stats.incrementFiles();
        }
        int iLimit = nativeByteBuffer.buffer.limit();
        if (iLimit == 0) {
            Log.e("exteraGram", "size of block to write was zero!");
            return AbstractWriter.Result.Success();
        }
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(this._file, "rws")) {
            randomAccessFile.seek(randomAccessFile.length());
            try (FileChannel channel = randomAccessFile.getChannel()) {
                channel.write(nativeByteBuffer.buffer);
                this._offset += (long) iLimit;
                Stats stats2 = this._stats;
                if (stats2 != null) {
                    stats2.incrementBytes(iLimit);
                }
                return AbstractWriter.Result.Success();
            }
        } catch (Exception e) {
            FileLog.e(e);
            return AbstractWriter.Result.Error();
        }
    }

    public static class Stats {
        private final AtomicInteger _files = new AtomicInteger(0);
        private final AtomicLong _bytes = new AtomicLong(0);

        public void incrementFiles() {
            this._files.getAndIncrement();
        }

        public void incrementBytes(int i) {
            this._bytes.addAndGet(i);
        }

        public int filesCount() {
            return this._files.get();
        }

        public long bytesCount() {
            return this._bytes.get();
        }
    }
}
