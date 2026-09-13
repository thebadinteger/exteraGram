package com.exteragram.messenger.ai.network;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import com.exteragram.messenger.utils.RecordTag;
import com.exteragram.messenger.ai.AiConfig;
import com.exteragram.messenger.ai.AiController;
import com.exteragram.messenger.ai.data.Message;
import com.exteragram.messenger.ai.data.Role;
import com.exteragram.messenger.ai.data.Service;
import com.exteragram.messenger.utils.network.ExteraHttpClient;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
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
        try (FileInputStream fileInputStream = new FileInputStream(file);
             ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream((int) Math.min(file.length(), 4194304L))) {
            byte[] bArr = new byte[1024];
            int i;
            while ((i = fileInputStream.read(bArr)) != -1) {
                byteArrayOutputStream.write(bArr, 0, i);
            }
            return new ImagePayload(byteArrayOutputStream.toByteArray(), getMimeType(str));
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
        executorServiceNewSingleThreadExecutor.execute(new Runnable() { // from class: com.exteragram.messenger.ai.network.Client$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                Client.this.lambda$getResponse$0(str2, string, str, z2, z, generationCallback);
            }
        });
        return string;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:59:0x0106  */
    /* JADX WARN: Code duplicated, block: B:61:0x0111  */
    /* JADX WARN: Code duplicated, block: B:62:0x0116  */
    /* JADX WARN: Code duplicated, block: B:74:? A[RETURN, SYNTHETIC] */
    public /* synthetic */ void lambda$getResponse$0(String str, String str2, String str3, boolean z, boolean z2, GenerationCallback generationCallback) {
        String message;
        this.isGenerating.set(true);
        try {
            String strString = null;
            ImagePayload imagePayloadLoadImagePayload = AiController.canSendImage(str) ? loadImagePayload(str) : null;
            if (this.activeRequests.containsKey(str2)) {
                Message message2 = new Message("user", str3, imagePayloadLoadImagePayload != null ? imagePayloadLoadImagePayload.data() : null, imagePayloadLoadImagePayload != null ? imagePayloadLoadImagePayload.mimeType() : null);
                Request requestCreateRequest = createRequest(getSelectedService(), message2, z, z2);
                if (requestCreateRequest == null) {
                    notifyErrorAndFinish(str2, generationCallback, 500, "Failed to create request body");
                    return;
                }
                if (this.activeRequests.containsKey(str2)) {
                    Call callNewCall = this.httpClient.newCall(requestCreateRequest);
                    this.activeCalls.put(str2, callNewCall);
                    Response responseExecute = callNewCall.execute();
                    try {
                        if (!responseExecute.isSuccessful()) {
                            try {
                                strString = responseExecute.body().string();
                                FileLog.e("AI_ERROR_RESPONSE_BODY (" + responseExecute.code() + "): " + strString);
                            } catch (IOException e) {
                                FileLog.e("AI_ERROR_READING_RESPONSE_BODY: ", e);
                            }
                            notifyErrorAndFinish(str2, generationCallback, responseExecute.code(), parseErrorMessage(strString, responseExecute.message()));
                            responseExecute.close();
                            return;
                        }
                        ResponseBody responseBodyBody = responseExecute.body();
                        if (z) {
                            handleStreamResponse(responseBodyBody, str2, message2, z2, generationCallback);
                        } else {
                            String responseContent = parseResponseContent(responseBodyBody.string());
                            if (responseContent == null) {
                                notifyErrorAndFinish(str2, generationCallback, 500, "Failed to parse response");
                            } else {
                                if (z2) {
                                    this.conversationHistory.add(copyTextMessage(message2));
                                    this.conversationHistory.add(new Message("assistant", responseContent));
                                    trimConversationHistory(this.conversationHistory);
                                    AiConfig.saveConversationHistory(this.conversationHistory);
                                }
                                notifyResponseAndFinish(str2, generationCallback, responseContent);
                            }
                        }
                        responseExecute.close();
                        return;
                    } catch (Throwable th) {
                        if (responseExecute == null) {
                            throw th;
                        }
                        try {
                            responseExecute.close();
                            throw th;
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                            throw th;
                        }
                    }

                }
            }
        } catch (Exception e2) {
            if (this.activeRequests.containsKey(str2)) {
            }
            FileLog.e("AI Error: ", e2);
            if (e2.getMessage() != null) {
                message = e2.getMessage();
            } else {
                message = "Unknown error";
            }
            notifyErrorAndFinish(str2, generationCallback, 500, message);
        }
    }

    private Request createRequest(Service service, Message message, boolean z, boolean z2) {
        String url = service.getUrl();
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String str = url.contains("generativelanguage.googleapis") ? "https://generativelanguage.googleapis.com/v1beta/openai/" : url;
        String strConcat = str.concat(str.endsWith("/") ? "chat/completions" : "/chat/completions");
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        try {
            Role selectedRole = this.roleOverride;
            if (selectedRole == null) {
                selectedRole = this.serviceOverride == null ? AiController.getInstance().getSelectedRole() : null;
            }
            if (selectedRole != null && !TextUtils.isEmpty(selectedRole.getPrompt())) {
                jSONArray.put(new JSONObject().put("role", "system").put("content", selectedRole.getPrompt()));
            }
            if (z2) {
                this.conversationHistory.clear();
                this.conversationHistory.addAll(trimConversationHistory(AiConfig.getConversationHistory()));
                ArrayList<Message> arrayList = this.conversationHistory;
                int size = arrayList.size();
                int i = 0;
                while (i < size) {
                    Message message2 = arrayList.get(i);
                    i++;
                    jSONArray.put(createMessageObject(message2));
                }
                jSONArray.put(createMessageObject(message));
            } else {
                jSONArray.put(createMessageObject(message));
            }
            jSONObject.put("model", service.getModel());
            jSONObject.put("messages", jSONArray);
            jSONObject.put("stream", z);
            jSONObject.put("temperature", AiConfig.getTemperature() / 10.0f);
            applyReasoningConfig(jSONObject, service, url);
            jSONObject.put("max_tokens", 4096);
            FileLog.d("AI_REQUEST_URL: " + strConcat);
            FileLog.d("AI_REQUEST_MODEL: " + service.getModel());
            StringBuilder sb = new StringBuilder();
            sb.append("AI_REQUEST_MESSAGES: ");
            sb.append(jSONArray.length());
            sb.append(", stream=");
            sb.append(z);
            sb.append(", image=");
            sb.append(message.getImageData() != null);
            FileLog.d(sb.toString());
            return new Request.Builder().url(strConcat).addHeader("Content-Type", "application/json").addHeader("Authorization", "Bearer " + service.getKey()).addHeader("User-Agent", TranslatorUtils.formatUserAgent()).addHeader("HTTP-Referer", "exteragram.app").addHeader("X-Title", "exteraGram").post(RequestBody.create(jSONObject.toString(), MediaType.parse("application/json"))).build();
        } catch (Exception e) {
            FileLog.e(e);
            return null;
        }
    }

    private void applyReasoningConfig(JSONObject jSONObject, Service service, String str) throws JSONException {
        if (service.isReasoningEnabled()) {
            return;
        }
        String model = service.getModel();
        String lowerCase = _UrlKt.FRAGMENT_ENCODE_SET;
        String lowerCase2 = str == null ? _UrlKt.FRAGMENT_ENCODE_SET : str.toLowerCase(Locale.ROOT);
        if (model != null) {
            lowerCase = model.toLowerCase(Locale.ROOT);
        }
        if (lowerCase2.contains("openrouter.ai")) {
            jSONObject.put("reasoning", new JSONObject().put("effort", "none"));
            return;
        }
        if (isGeminiService(lowerCase2) && isGeminiReasoningModel(lowerCase)) {
            jSONObject.put("reasoning_effort", getGeminiReasoningEffort(lowerCase));
        } else if (isOpenAiService(lowerCase2) && isOpenAiReasoningModel(lowerCase)) {
            jSONObject.put("reasoning_effort", getOpenAiReasoningEffort(lowerCase));
        }
    }

    private boolean isGeminiService(String str) {
        return str.contains("generativelanguage.googleapis");
    }

    private boolean isGeminiReasoningModel(String str) {
        String strStripProviderPrefix = stripProviderPrefix(str);
        return strStripProviderPrefix.startsWith("gemini-2.5") || strStripProviderPrefix.startsWith("gemini-3") || strStripProviderPrefix.contains("thinking");
    }

    private boolean isOpenAiService(String str) {
        return str.contains("api.openai.com");
    }

    private String getGeminiReasoningEffort(String str) {
        if (str.contains("gemini-2.5") && !str.contains("pro")) {
            return "none";
        }
        return "minimal";
    }

    private boolean isOpenAiReasoningModel(String str) {
        String strStripProviderPrefix = stripProviderPrefix(str);
        if (strStripProviderPrefix.contains("gpt-5-chat")) {
            return false;
        }
        return strStripProviderPrefix.startsWith("gpt-5") || strStripProviderPrefix.startsWith("o1") || strStripProviderPrefix.startsWith("o3") || strStripProviderPrefix.startsWith("o4");
    }

    private String getOpenAiReasoningEffort(String str) {
        return supportsOpenAiNoReasoning(stripProviderPrefix(str)) ? "none" : "minimal";
    }

    private boolean supportsOpenAiNoReasoning(String str) {
        return str.startsWith("gpt-5.1") || str.startsWith("gpt-5.2") || str.startsWith("gpt-5.3") || str.startsWith("gpt-5.4") || str.startsWith("gpt-5.5");
    }

    private String stripProviderPrefix(String str) {
        int iIndexOf = str.indexOf(47);
        return iIndexOf >= 0 ? str.substring(iIndexOf + 1) : str;
    }

    private JSONObject createMessageObject(Message message) throws JSONException {
        JSONObject jSONObject = new JSONObject();
        jSONObject.put("role", message.role());
        if (message.getImageData() != null && !TextUtils.isEmpty(message.getMimeType())) {
            JSONArray jSONArray = new JSONArray();
            if (!TextUtils.isEmpty(message.content())) {
                jSONArray.put(new JSONObject().put(TeXSymbolParser.TYPE_ATTR, "text").put("text", message.content()));
            }
            jSONArray.put(new JSONObject().put(TeXSymbolParser.TYPE_ATTR, "image_url").put("image_url", new JSONObject().put("url", "data:" + message.getMimeType() + ";base64," + Base64.encodeToString(message.getImageData(), 2))));
            jSONObject.put("content", jSONArray);
            return jSONObject;
        }
        jSONObject.put("content", message.content());
        return jSONObject;
    }

    private void sendStreamChunk(final String str, final String str2, final GenerationCallback generationCallback) {
        if (TextUtils.isEmpty(str2)) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.ai.network.Client$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                Client.this.lambda$sendStreamChunk$1(str, generationCallback, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$sendStreamChunk$1(String str, GenerationCallback generationCallback, String str2) {
        if (!this.activeRequests.containsKey(str) || generationCallback == null) {
            return;
        }
        generationCallback.onChunk(str2);
    }

    private void notifyThinking(final String str, final GenerationCallback generationCallback) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.ai.network.Client$$ExternalSyntheticLambda4
            @Override // java.lang.Runnable
            public final void run() {
                Client.this.lambda$notifyThinking$2(str, generationCallback);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyThinking$2(String str, GenerationCallback generationCallback) {
        if (!this.activeRequests.containsKey(str) || generationCallback == null) {
            return;
        }
        generationCallback.onThinking();
    }

    /* JADX WARN: Code duplicated, block: B:45:0x00a5  */
    /* JADX WARN: Code duplicated, block: B:49:0x00b2 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:50:0x00b4  */
    /* JADX WARN: Code duplicated, block: B:52:0x00ba  */
    /* JADX WARN: Code duplicated, block: B:53:0x00bf  */
    /* JADX WARN: Code duplicated, block: B:56:0x00c7  */
    /* JADX WARN: Code duplicated, block: B:58:0x00d1  */
    /* JADX WARN: Code duplicated, block: B:61:0x00e9 A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:62:0x00eb  */
    /* JADX WARN: Code duplicated, block: B:64:0x010e  */
    /* JADX WARN: Code duplicated, block: B:85:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0, types: [com.exteragram.messenger.ai.network.Client-IA] */
    /* JADX WARN: Type inference failed for: r2v1 */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r2v3 */
    private void handleStreamResponse(ResponseBody responseBody, String str, Message message, boolean z, GenerationCallback generationCallback) {
        String strFlush;
        String strTrimTrailing;
        String message2;
        StringBuilder sb = new StringBuilder();
        Throwable r2 = null;
        ReasoningContentFilter reasoningContentFilter = new ReasoningContentFilter();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseBody.byteStream()))) {
            loop0: while (true) {
                int length = 0;
                while (true) {
                    String line = bufferedReader.readLine();
                    if (line == null || !this.activeRequests.containsKey(str)) {
                        break loop0;
                    }
                    if (!TextUtils.isEmpty(line) && line.startsWith("data:")) {
                        String strTrim = line.substring(5).trim();
                        if (strTrim.equals("[DONE]")) {
                            if (length > 0) {
                                sendStreamChunk(str, sb.toString(), generationCallback);
                            }
                            break loop0;
                        }
                        StreamResponsePart streamResponsePart = parseStreamResponsePart(strTrim);
                        if (streamResponsePart.hasReasoning()) {
                            notifyThinking(str, generationCallback);
                        }
                        String strFilter = reasoningContentFilter.filter(streamResponsePart.content());
                        if (reasoningContentFilter.consumeReasoningSignal()) {
                            notifyThinking(str, generationCallback);
                        }
                        if (!TextUtils.isEmpty(strFilter)) {
                            sb.append(strFilter);
                            length += strFilter.length();
                            if (length >= STREAM_SYMBOLS_LIMIT) {
                                break;
                            }
                        }
                    }
                }
                sendStreamChunk(str, sb.toString(), generationCallback);
            }
        } catch (Exception e) {
            if (this.activeRequests.containsKey(str)) {
                FileLog.e(e);
                r2 = e;
            }
        }
        if (!this.activeRequests.containsKey(str)) {
            return;
        }
        if (r2 != null) {
            if (r2.getMessage() != null) {
                message2 = r2.getMessage();
            } else {
                message2 = "Unknown error";
            }
            notifyErrorAndFinish(str, generationCallback, 500, message2);
            return;
        }
        strFlush = reasoningContentFilter.flush();
        if (!TextUtils.isEmpty(strFlush)) {
            sb.append(strFlush);
            sendStreamChunk(str, sb.toString(), generationCallback);
        }
        strTrimTrailing = trimTrailing(sb.toString());
        if (!TextUtils.isEmpty(strTrimTrailing)) {
            if (z) {
                this.conversationHistory.add(copyTextMessage(message));
                this.conversationHistory.add(new Message("assistant", strTrimTrailing));
                trimConversationHistory(this.conversationHistory);
                AiConfig.saveConversationHistory(this.conversationHistory);
            }
            notifyResponseAndFinish(str, generationCallback, strTrimTrailing);
            return;
        }
        notifyErrorAndFinish(str, generationCallback, 204, "Response body is empty");
    }

    private String parseResponseContent(String str) {
        JSONObject jSONObjectOptJSONObject;
        Object objOpt;
        try {
            JSONArray jSONArrayOptJSONArray = new JSONObject(str).optJSONArray("choices");
            if (jSONArrayOptJSONArray == null || jSONArrayOptJSONArray.length() <= 0 || (jSONObjectOptJSONObject = jSONArrayOptJSONArray.getJSONObject(0).optJSONObject("message")) == null || !jSONObjectOptJSONObject.has("content") || jSONObjectOptJSONObject.isNull("content") || (objOpt = jSONObjectOptJSONObject.opt("content")) == null) {
                return null;
            }
            return stripReasoningMarkup(objOpt.toString());
        } catch (Exception e) {
            FileLog.e(e);
        }
        return null;
    }

    private StreamResponsePart parseStreamResponsePart(String str) {
        String str2 = _UrlKt.FRAGMENT_ENCODE_SET;
        boolean z = false;
        try {
            JSONArray jSONArrayOptJSONArray = new JSONObject(str).optJSONArray("choices");
            if (jSONArrayOptJSONArray != null && jSONArrayOptJSONArray.length() > 0) {
                JSONObject jSONObjectOptJSONObject = jSONArrayOptJSONArray.getJSONObject(0).optJSONObject("delta");
                if (jSONObjectOptJSONObject == null) {
                    return new StreamResponsePart(str2, z);
                }
                Object objOpt = jSONObjectOptJSONObject.opt("content");
                return new StreamResponsePart((objOpt == null || objOpt == JSONObject.NULL) ? _UrlKt.FRAGMENT_ENCODE_SET : objOpt.toString(), hasReasoning(jSONObjectOptJSONObject));
            }
        } catch (Exception unused) {
        }
        return new StreamResponsePart(str2, z);
    }

    private boolean hasReasoning(JSONObject jSONObject) {
        return hasValue(jSONObject, "reasoning") || hasValue(jSONObject, "reasoning_content") || hasValue(jSONObject, "reasoning_details");
    }

    private boolean hasValue(JSONObject jSONObject, String str) {
        if (jSONObject != null && jSONObject.has(str) && !jSONObject.isNull(str)) {
            Object objOpt = jSONObject.opt(str);
            if (objOpt instanceof String) {
                return !TextUtils.isEmpty((String) objOpt);
            }
            if (objOpt instanceof JSONArray) {
                return ((JSONArray) objOpt).length() > 0;
            }
            if (objOpt != null && objOpt != JSONObject.NULL) {
                return true;
            }
        }
        return false;
    }

    private String stripReasoningMarkup(String str) {
        StringBuilder sb = new StringBuilder(str.length());
        String lowerCase = str.toLowerCase(Locale.ROOT);
        int i = 0;
        while (i < str.length()) {
            int iIndexOf = lowerCase.indexOf("<think>", i);
            if (iIndexOf < 0) {
                sb.append((CharSequence) str, i, str.length());
                break;
            }
            sb.append((CharSequence) str, i, iIndexOf);
            int iIndexOf2 = lowerCase.indexOf("</think>", iIndexOf + 7);
            if (iIndexOf2 < 0) {
                break;
            }
            i = iIndexOf2 + 8;
        }
        return trimLeading(sb.toString());
    }

    private String trimLeading(String str) {
        int i = 0;
        while (i < str.length() && Character.isWhitespace(str.charAt(i))) {
            i++;
        }
        return str.substring(i);
    }

    private String trimTrailing(String str) {
        int length = str.length();
        while (length > 0 && Character.isWhitespace(str.charAt(length - 1))) {
            length--;
        }
        return str.substring(0, length);
    }

    private String parseErrorMessage(String str, String str2) {
        if (!TextUtils.isEmpty(str)) {
            try {
                JSONObject jSONObjectOptJSONObject = new JSONObject(str).optJSONObject("");
                if (jSONObjectOptJSONObject != null) {
                    String strOptString = jSONObjectOptJSONObject.optString("message", null);
                    if (!TextUtils.isEmpty(strOptString)) {
                        return strOptString;
                    }
                }
            } catch (Exception unused) {
            }
        }
        return !TextUtils.isEmpty(str2) ? str2.toLowerCase(Locale.ROOT) : "Unknown error";
    }

    private ArrayList<Message> trimConversationHistory(ArrayList<Message> arrayList) {
        ArrayList arrayList2 = new ArrayList();
        int length = 0;
        for (int size = arrayList.size() - 1; size >= 0 && arrayList2.size() < 32; size--) {
            Message message = arrayList.get(size);
            if (message != null && !TextUtils.isEmpty(message.role()) && !TextUtils.isEmpty(message.content())) {
                length += message.content().length();
                if (length > 24000 && !arrayList2.isEmpty()) {
                    break;
                }
                arrayList2.add(0, copyTextMessage(message));
            }
        }
        while (!arrayList2.isEmpty() && "assistant".equals(((Message) arrayList2.get(0)).role())) {
            arrayList2.remove(0);
        }
        arrayList.clear();
        arrayList.addAll(arrayList2);
        return arrayList;
    }

    private Message copyTextMessage(Message message) {
        return new Message(message.role(), message.content());
    }

    private void notifyResponseAndFinish(final String str, final GenerationCallback generationCallback, final String str2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.ai.network.Client$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                Client.this.lambda$notifyResponseAndFinish$3(str, generationCallback, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$notifyResponseAndFinish$3(String str, GenerationCallback generationCallback, String str2) {
        try {
            if (this.activeRequests.containsKey(str) && generationCallback != null) {
                generationCallback.onResponse(str2);
            }
        } finally {
            finishRequest(str);
        }
    }

    private void notifyErrorAndFinish(final String str, final GenerationCallback generationCallback, final int i, final String str2) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.ai.network.Client$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                Client.this.lambda$notifyErrorAndFinish$4(str, generationCallback, i, str2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
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
            return Objects.hash(Arrays.hashCode(this.data), this.mimeType);
        }

        public String mimeType() {
            return this.mimeType;
        }

        public final String toString() {
            return "ImagePayload[data=" + Arrays.toString(this.data) + ", mimeType=" + this.mimeType + "]";
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
            return Objects.hash(this.hasReasoning, this.content);
        }

        public final String toString() {
            return "StreamResponsePart[content=" + this.content + ", hasReasoning=" + this.hasReasoning + "]";
        }
    }
}
