package com.exteragram.messenger.ai.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import androidx.mediarouter.media.MediaRouteProviderProtocol;
import com.android.dx.io.Opcodes;
import com.android.tools.r8.RecordTag;
import com.exteragram.messenger.ai.AiConfig;
import com.exteragram.messenger.ai.AiController;
import com.exteragram.messenger.ai.data.Message;
import com.exteragram.messenger.ai.data.Role;
import com.exteragram.messenger.ai.data.Service;
import com.exteragram.messenger.utils.network.ExteraHttpClient;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import com.google.android.gms.cast.MediaError;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.url._UrlKt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;

public class Client {
    private static final int STREAM_SYMBOLS_LIMIT;
    private final ConcurrentHashMap<String, Call> activeCalls;
    private final ConcurrentHashMap<String, ExecutorService> activeRequests;
    private final ArrayList<Message> conversationHistory;
    private final OkHttpClient httpClient;
    private final AtomicBoolean isGenerating;
    private final Role roleOverride;
    private final Service serviceOverride;

    static {
        STREAM_SYMBOLS_LIMIT = SharedConfig.getDevicePerformanceClass() >= 1 ? 10 : 20;
    }

    private Client(Builder builder) {
        this.conversationHistory = new ArrayList<>();
        this.isGenerating = new AtomicBoolean(false);
        this.activeRequests = new ConcurrentHashMap<>();
        this.activeCalls = new ConcurrentHashMap<>();
        this.serviceOverride = builder.serviceOverride;
        this.roleOverride = builder.roleOverride;
        OkHttpClient.Builder builderDns = ExteraHttpClient.INSTANCE.getClient().newBuilder().dns(ProxyDns.INSTANCE);
        TimeUnit timeUnit = TimeUnit.MINUTES;
        this.httpClient = builderDns.connectTimeout(1L, timeUnit).readTimeout(5L, timeUnit).build();
    }

    public static String getMimeType(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String lowerCase = str.toLowerCase();
        if (lowerCase.endsWith(".png")) {
            return "image/png";
        }
        if (lowerCase.endsWith(".webp")) {
            return "image/webp";
        }
        if (lowerCase.endsWith(".heic") || lowerCase.endsWith(".heif")) {
            return "image/heic";
        }
        return "image/jpeg";
    }

    private static ImagePayload loadImagePayload(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        File file = new File(str);
        if (!file.exists() || !file.isFile() || file.length() == 0) {
            return null;
        }
        if (file.length() > 4194304) {
            return compressImage(str);
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            try {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int) Math.min(file.length(), 4194304L));
                try {
                    byte[] bArr = new byte[1024];
                    while (true) {
                        int i = fileInputStream.read(bArr);
                        if (i != -1) {
                            byteArrayOutputStream.write(bArr, 0, i);
                        } else {
                            ImagePayload imagePayload = new ImagePayload(byteArrayOutputStream.toByteArray(), getMimeType(str));
                            byteArrayOutputStream.close();
                            fileInputStream.close();
                            return imagePayload;
                        }
                        try {
                            fileInputStream.close();
                        } catch (Throwable th) {
                            th.addSuppressed(th);
                        }
                        throw th;
                    }
                } catch (Throwable th2) {
                    try {
                        byteArrayOutputStream.close();
                    } catch (Throwable th3) {
                        th2.addSuppressed(th3);
                    }
                    throw th2;
                }
            } catch (Throwable th4) {
                fileInputStream.close();
                throw th4;
            }
        } catch (IOException e) {
            FileLog.e("Error loading image: " + str, e);
            return null;
        }
    }

    private static ImagePayload compressImage(String str) {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(str, options);
            if (options.outWidth > 0 && options.outHeight > 0) {
                BitmapFactory.Options options2 = new BitmapFactory.Options();
                options2.inSampleSize = 1;
                while (true) {
                    int i = options.outWidth;
                    int i2 = options2.inSampleSize;
                    if (i / i2 <= 2048 && options.outHeight / i2 <= 2048) {
                        break;
                    }
                    options2.inSampleSize = i2 * 2;
                }
                Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(str, options2);
                if (bitmapDecodeFile == null) {
                    return null;
                }
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    int i3 = 85;
                    do {
                        try {
                            byteArrayOutputStream.reset();
                            bitmapDecodeFile.compress(Bitmap.CompressFormat.JPEG, i3, byteArrayOutputStream);
                            i3 -= 10;
                            if (byteArrayOutputStream.size() <= 4194304) {
                                break;
                            }
                        } catch (Throwable th) {
                            try {
                                byteArrayOutputStream.close();
                            } catch (Throwable th2) {
                                th.addSuppressed(th2);
                            }
                            throw th;
                        }
                    } while (i3 >= 55);
                    if (byteArrayOutputStream.size() <= 4194304) {
                        ImagePayload imagePayload = new ImagePayload(byteArrayOutputStream.toByteArray(), "image/jpeg");
                        byteArrayOutputStream.close();
                        bitmapDecodeFile.recycle();
                        return imagePayload;
                    }
                    byteArrayOutputStream.close();
                    bitmapDecodeFile.recycle();
                    return null;
                } catch (Throwable th3) {
                    bitmapDecodeFile.recycle();
                    throw th3;
                }
                bitmapDecodeFile.recycle();
                throw th3;
            }
            return null;
        } catch (Exception e) {
            FileLog.e("Error compressing image: " + str, e);
            return null;
        }
    }

    private Service getSelectedService() {
        Service service = this.serviceOverride;
        return service != null ? service : AiController.getInstance().getSelected();
    }

    public String getResponse(String str, GenerationCallback generationCallback) {
        return getResponse(str, false, false, null, generationCallback);
    }

    public String getResponse(final String str, final boolean z, final boolean z2, final String str2, final GenerationCallback generationCallback) {
        final String string = UUID.randomUUID().toString();
        ExecutorService executorServiceNewSingleThreadExecutor = Executors.newSingleThreadExecutor();
        this.activeRequests.put(string, executorServiceNewSingleThreadExecutor);
        executorServiceNewSingleThreadExecutor.execute(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getResponse$0(str2, string, str, z2, z, generationCallback);
            }
        });
        return string;
    }

    void lambda$sendStreamChunk$1(String str, GenerationCallback generationCallback, String str2) {
        if (!this.activeRequests.containsKey(str) || generationCallback == null) {
            return;
        }
        generationCallback.onChunk(str2);
    }

    private void notifyThinking(final String str, final GenerationCallback generationCallback) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$notifyThinking$2(str, generationCallback);
            }
        });
    }

    public void lambda$notifyResponseAndFinish$3(String str, GenerationCallback generationCallback, String str2) {
        try {
            if (this.activeRequests.containsKey(str) && generationCallback != null) {
                generationCallback.onResponse(str2);
            }
        } finally {
            finishRequest(str);
        }
    }

    private void notifyErrorAndFinish(final String str, final GenerationCallback generationCallback, final int i, final String str2) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$notifyErrorAndFinish$4(str, generationCallback, i, str2);
            }
        });
    }

    public /* synthetic */ void lambda$notifyErrorAndFinish$4(String str, GenerationCallback generationCallback, int i, String str2) {
        try {
            if (this.activeRequests.containsKey(str) && generationCallback != null) {
                generationCallback.onError(i, str2);
            }
        } finally {
            finishRequest(str);
        }
    }

    public boolean isGenerating() {
        return this.isGenerating.get();
    }

    private void finishRequest(String str) {
        this.activeCalls.remove(str);
        ExecutorService executorServiceRemove = this.activeRequests.remove(str);
        if (executorServiceRemove != null) {
            executorServiceRemove.shutdown();
        }
        if (this.activeRequests.isEmpty()) {
            this.isGenerating.set(false);
        }
    }

    public void stopRequest(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        Call callRemove = this.activeCalls.remove(str);
        if (callRemove != null) {
            callRemove.cancel();
        }
        ExecutorService executorServiceRemove = this.activeRequests.remove(str);
        if (executorServiceRemove != null) {
            executorServiceRemove.shutdownNow();
            try {
                executorServiceRemove.awaitTermination(500L, TimeUnit.MILLISECONDS);
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
            }
        }
        if (this.activeRequests.isEmpty()) {
            this.isGenerating.set(false);
        }
    }

    public static class Builder {
        private Role roleOverride;
        private Service serviceOverride;

        public Builder serviceOverride(Service service) {
            this.serviceOverride = service;
            return this;
        }

        public Builder roleOverride(Role role) {
            this.roleOverride = role;
            return this;
        }

        public Client build() {
            return new Client(this);
        }
    }

    public static class ReasoningContentFilter {
        private boolean inReasoning;
        private String pending;
        private boolean reasoningSignal;

        private ReasoningContentFilter() {
            this.pending = _UrlKt.FRAGMENT_ENCODE_SET;
        }

        public String filter(String str) {
            int i;
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            String str2 = this.pending + str;
            this.pending = _UrlKt.FRAGMENT_ENCODE_SET;
            StringBuilder sb = new StringBuilder(str2.length());
            int i2 = 0;
            while (i2 < str2.length()) {
                String lowerCase = str2.toLowerCase(Locale.ROOT);
                if (this.inReasoning) {
                    this.reasoningSignal = true;
                    int iIndexOf = lowerCase.indexOf("</think>", i2);
                    if (iIndexOf < 0) {
                        this.pending = getCloseTagPrefixSuffix(str2, i2);
                        return sb.toString();
                    }
                    i = iIndexOf + 8;
                    this.inReasoning = false;
                } else {
                    int iIndexOf2 = lowerCase.indexOf("<think>", i2);
                    if (iIndexOf2 < 0) {
                        String openTagPrefixSuffix = getOpenTagPrefixSuffix(str2, i2);
                        this.pending = openTagPrefixSuffix;
                        this.reasoningSignal = !openTagPrefixSuffix.isEmpty();
                        int length = str2.length() - this.pending.length();
                        if (length > i2) {
                            sb.append((CharSequence) str2, i2, length);
                        }
                        return sb.toString();
                    }
                    sb.append((CharSequence) str2, i2, iIndexOf2);
                    i = iIndexOf2 + 7;
                    this.inReasoning = true;
                    this.reasoningSignal = true;
                }
                i2 = i;
            }
            return sb.toString();
        }

        public boolean consumeReasoningSignal() {
            boolean z = this.reasoningSignal;
            this.reasoningSignal = false;
            return z;
        }

        public String flush() {
            String str = this.inReasoning ? _UrlKt.FRAGMENT_ENCODE_SET : this.pending;
            this.pending = _UrlKt.FRAGMENT_ENCODE_SET;
            return str;
        }

        private String getOpenTagPrefixSuffix(String str, int i) {
            String lowerCase = str.toLowerCase(Locale.ROOT);
            for (int iMin = Math.min(6, str.length() - i); iMin > 0; iMin--) {
                if ("<think>".startsWith(lowerCase.substring(str.length() - iMin))) {
                    return str.substring(str.length() - iMin);
                }
            }
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }

        private String getCloseTagPrefixSuffix(String str, int i) {
            String lowerCase = str.toLowerCase(Locale.ROOT);
            for (int iMin = Math.min(7, str.length() - i); iMin > 0; iMin--) {
                if ("</think>".startsWith(lowerCase.substring(str.length() - iMin))) {
                    return str.substring(str.length() - iMin);
                }
            }
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
    }

    public static final class ImagePayload extends RecordTag {
        private final byte[] data;
        private final String mimeType;

        public final /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof ImagePayload)) {
                return false;
            }
            ImagePayload imagePayload = (ImagePayload) obj;
            return Objects.equals(this.data, imagePayload.data) && Objects.equals(this.mimeType, imagePayload.mimeType);
        }

        public final /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.data, this.mimeType};
        }

        private ImagePayload(byte[] bArr, String str) {
            this.data = bArr;
            this.mimeType = str;
        }

        public byte[] data() {
            return this.data;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return java.util.Objects.hash(this.data, this.mimeType);
        }

        public String mimeType() {
            return this.mimeType;
        }

        public final String toString() {
            return com.exteragram.messenger.utils.RecordUtils.recordToString($record$getFieldsAsObjects(), ImagePayload.class, "data;mimeType");
        }
    }

    public static final class StreamResponsePart extends RecordTag {
        private final String content;
        private final boolean hasReasoning;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof StreamResponsePart)) {
                return false;
            }
            StreamResponsePart streamResponsePart = (StreamResponsePart) obj;
            return this.hasReasoning == streamResponsePart.hasReasoning && Objects.equals(this.content, streamResponsePart.content);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{this.content, Boolean.valueOf(this.hasReasoning)};
        }

        private StreamResponsePart(String str, boolean z) {
            this.content = str;
            this.hasReasoning = z;
        }

        public String content() {
            return this.content;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public boolean hasReasoning() {
            return this.hasReasoning;
        }

        public final int hashCode() {
            return java.util.Objects.hash(this.hasReasoning, this.content);
        }

        public final String toString() {
            return com.exteragram.messenger.utils.RecordUtils.recordToString($record$getFieldsAsObjects(), StreamResponsePart.class, "content;hasReasoning");
        }
    }
}
