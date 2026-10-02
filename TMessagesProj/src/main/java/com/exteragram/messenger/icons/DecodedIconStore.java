package com.exteragram.messenger.icons;

import android.graphics.Bitmap;
import android.system.Os;
import android.system.StructStat;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.util.Comparator;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.ArraysKt;
import kotlin.comparisons.ComparisonsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.Utilities;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\bÀ\u0002\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u0011\u0010\u0005\u001a\u0004\u0018\u00010\u0004H\u0002¢\u0006\u0004\b\u0005\u0010\u0006J/\u0010\r\u001a\u00020\f2\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\bH\u0002¢\u0006\u0004\b\r\u0010\u000eJ\u0017\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u000f\u001a\u00020\u0004H\u0002¢\u0006\u0004\b\u0011\u0010\u0012J/\u0010\u0014\u001a\u0004\u0018\u00010\u00132\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\b¢\u0006\u0004\b\u0014\u0010\u0015J5\u0010\u0017\u001a\u00020\u00102\u0006\u0010\u0007\u001a\u00020\u00042\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\b2\u0006\u0010\u0016\u001a\u00020\u0013¢\u0006\u0004\b\u0017\u0010\u0018R\u0014\u0010\u001a\u001a\u00020\u00198\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u001a\u0010\u001bR\u0014\u0010\u001d\u001a\u00020\u001c8\u0002X\u0082\u0004¢\u0006\u0006\n\u0004\b\u001d\u0010\u001eR\u0018\u0010\u0005\u001a\u0004\u0018\u00010\u00048\u0002@\u0002X\u0082\u000e¢\u0006\u0006\n\u0004\b\u0005\u0010\u001f¨\u0006 "}, d2 = {"Lcom/exteragram/messenger/icons/DecodedIconStore;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "Ljava/io/File;", "directory", "()Ljava/io/File;", "source", _UrlKt.FRAGMENT_ENCODE_SET, "width", "height", "density", _UrlKt.FRAGMENT_ENCODE_SET, "entryName", "(Ljava/io/File;III)Ljava/lang/String;", "dir", _UrlKt.FRAGMENT_ENCODE_SET, "prune", "(Ljava/io/File;)V", "Landroid/graphics/Bitmap;", "get", "(Ljava/io/File;III)Landroid/graphics/Bitmap;", "bitmap", "put", "(Ljava/io/File;IIILandroid/graphics/Bitmap;)V", "Ljava/util/concurrent/atomic/AtomicLong;", "writtenSincePrune", "Ljava/util/concurrent/atomic/AtomicLong;", "Ljava/util/concurrent/atomic/AtomicBoolean;", "pruning", "Ljava/util/concurrent/atomic/AtomicBoolean;", "Ljava/io/File;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nDecodedIconStore.kt\nKotlin\n*S Kotlin\n*F\n+ 1 DecodedIconStore.kt\ncom/exteragram/messenger/icons/DecodedIconStore\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 Bitmap.kt\nandroidx/core/graphics/BitmapKt\n+ 4 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n*L\n1#1,149:1\n1#2:150\n83#3,6:151\n6309#4,2:157\n*S KotlinDebug\n*F\n+ 1 DecodedIconStore.kt\ncom/exteragram/messenger/icons/DecodedIconStore\n*L\n82#1:151,6\n135#1:157,2\n*E\n"})
public final class DecodedIconStore {
    private static volatile File directory;
    public static final DecodedIconStore INSTANCE = new DecodedIconStore();
    private static final AtomicLong writtenSincePrune = new AtomicLong(0);
    private static final AtomicBoolean pruning = new AtomicBoolean(false);

    private DecodedIconStore() {
    }

    private final File directory() {
        File file = directory;
        if (file != null) {
            return file;
        }
        try {
            File file2 = new File(ApplicationLoader.applicationContext.getCacheDir(), "icon_bitmaps");
            if (!file2.isDirectory() && !file2.mkdirs()) {
                return null;
            }
            directory = file2;
            return file2;
        } catch (Exception e) {
            FileLog.e("Failed to open the decoded icon cache", e);
            return null;
        }
    }

    private final String entryName(File source, int width, int height, int density) {
        long jLastModified;
        long length;
        try {
            StructStat structStatStat = Os.stat(source.getPath());
            jLastModified = structStatStat.st_mtime;
            length = structStatStat.st_size;
        } catch (Throwable unused) {
            jLastModified = source.lastModified();
            length = source.length();
        }
        String path = source.getPath();
        int length2 = path.length();
        long jCharAt = 1125899906842597L;
        for (int i = 0; i < length2; i++) {
            jCharAt = ((long) path.charAt(i)) + (31 * jCharAt);
        }
        return Long.toHexString((31 * ((((((((jCharAt * 31) + jLastModified) * 31) + length) * 31) + ((long) width)) * 31) + ((long) height))) + ((long) density));
    }

    public final Bitmap get(File source, int width, int height, int density) {
        File fileDirectory = directory();
        if (fileDirectory == null) {
            return null;
        }
        File file = new File(fileDirectory, entryName(source, width, height, density));
        long j = (((long) width) * ((long) height) * 4) + 16;
        try {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            try {
                FileChannel channel = randomAccessFile.getChannel();
                if (channel.size() != j) {
                    CloseableKt.closeFinally(randomAccessFile, null);
                    return null;
                }
                ByteBuffer byteBufferOrder = ByteBuffer.allocateDirect((int) j).order(ByteOrder.nativeOrder());
                while (byteBufferOrder.hasRemaining()) {
                    if (channel.read(byteBufferOrder) < 0) {
                        CloseableKt.closeFinally(randomAccessFile, null);
                        return null;
                    }
                }
                byteBufferOrder.flip();
                if (byteBufferOrder.getInt() != 1163151665) {
                    CloseableKt.closeFinally(randomAccessFile, null);
                    return null;
                }
                if (byteBufferOrder.getInt() == width && byteBufferOrder.getInt() == height) {
                    byteBufferOrder.getInt();
                    Bitmap bitmapCreateBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                    bitmapCreateBitmap.copyPixelsFromBuffer(byteBufferOrder);
                    bitmapCreateBitmap.setDensity(density);
                    CloseableKt.closeFinally(randomAccessFile, null);
                    return bitmapCreateBitmap;
                }
                CloseableKt.closeFinally(randomAccessFile, null);
                return null;
            } catch (Throwable th) {
                try {
                    throw th;
                } catch (Throwable th2) {
                    CloseableKt.closeFinally(randomAccessFile, th);
                    throw th2;
                }
            }
        } catch (FileNotFoundException unused) {
            return null;
        } catch (Throwable unused2) {
            file.delete();
            return null;
        }
    }

    public final void put(File source, int width, int height, int density, Bitmap bitmap) {
        final File fileDirectory;
        if (bitmap.getWidth() == width && bitmap.getHeight() == height && bitmap.getConfig() == Bitmap.Config.ARGB_8888 && (fileDirectory = directory()) != null) {
            File file = new File(fileDirectory, entryName(source, width, height, density));
            if (file.isFile()) {
                return;
            }
            File file2 = new File(fileDirectory, file.getName() + ".tmp");
            int i = (width * height * 4) + 16;
            try {
                ByteBuffer byteBufferOrder = ByteBuffer.allocateDirect(i).order(ByteOrder.nativeOrder());
                byteBufferOrder.putInt(1163151665);
                byteBufferOrder.putInt(width);
                byteBufferOrder.putInt(height);
                byteBufferOrder.putInt(density);
                bitmap.copyPixelsToBuffer(byteBufferOrder);
                byteBufferOrder.flip();
                RandomAccessFile randomAccessFile = new RandomAccessFile(file2, "rw");
                try {
                    try {
                        FileChannel channel = randomAccessFile.getChannel();
                        while (byteBufferOrder.hasRemaining()) {
                            channel.write(byteBufferOrder);
                        }
                        Unit unit = Unit.INSTANCE;
                        CloseableKt.closeFinally(randomAccessFile, null);
                        if (!file2.renameTo(file)) {
                            file2.delete();
                            return;
                        }
                        AtomicLong atomicLong = writtenSincePrune;
                        if (atomicLong.addAndGet(i) >= 4194304) {
                            atomicLong.set(0L);
                            Utilities.globalQueue.postRunnable(new Runnable() { // from class: com.exteragram.messenger.icons.DecodedIconStore$$ExternalSyntheticLambda0
                                @Override // java.lang.Runnable
                                public final void run() {
                                    DecodedIconStore.INSTANCE.prune(fileDirectory);
                                }
                            });
                        }
                    } catch (Throwable th) {
                        try {
                            throw th;
                        } catch (Throwable th2) {
                            CloseableKt.closeFinally(randomAccessFile, th);
                            throw th2;
                        }
                    }
                } catch (Throwable th3) {
                }
            } catch (Throwable unused) {
                if (file2.exists()) {
                    file2.delete();
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public final void prune(File dir) {
        AtomicBoolean atomicBoolean = pruning;
        try {
            if (atomicBoolean.compareAndSet(false, true)) {
                try {
                    File[] fileArrListFiles = dir.listFiles();
                    if (fileArrListFiles == null) {
                        atomicBoolean.set(false);
                        return;
                    }
                    long length = 0;
                    for (File file : fileArrListFiles) {
                        length += file.length();
                    }
                    if (length > 33554432) {
                        if (fileArrListFiles.length > 1) {
                            ArraysKt.sortWith(fileArrListFiles, new Comparator() { // from class: com.exteragram.messenger.icons.DecodedIconStore$prune$$inlined$sortBy$1
                                /* JADX WARN: Multi-variable type inference failed */
                                @Override // java.util.Comparator
                                public final int compare(Object t, Object t2) {
                                    return ComparisonsKt.compareValues(Long.valueOf(((File) t).lastModified()), Long.valueOf(((File) t2).lastModified()));
                                }
                            });
                        }
                        for (File file2 : fileArrListFiles) {
                            if (length <= 25165824) {
                                break;
                            }
                            long length2 = file2.length();
                            if (file2.delete()) {
                                length -= length2;
                            }
                        }
                    }
                } catch (Exception e) {
                    FileLog.e("Failed to prune the decoded icon cache", e);
                }
                pruning.set(false);
            }
        } catch (Throwable th) {
            pruning.set(false);
            throw th;
        }
    }
}
