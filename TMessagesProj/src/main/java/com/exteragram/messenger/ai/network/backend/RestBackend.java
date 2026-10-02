package com.exteragram.messenger.ai.network.backend;

import android.text.TextUtils;
import android.util.Base64;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.ai.data.Message;
import com.exteragram.messenger.ai.data.Role;
import com.exteragram.messenger.ai.data.Service;
import com.exteragram.messenger.ai.network.ProxyDns;
import com.exteragram.messenger.translator.TranslatorUtils;
import com.exteragram.messenger.utils.network.ExteraHttpClient;
import com.google.android.gms.cast.MediaError;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
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
import org.telegram.messenger.FileLog;
import org.telegram.messenger.SharedConfig;

/* JADX INFO: loaded from: classes4.dex */
public class RestBackend implements Backend {
    private static final int STREAM_SYMBOLS_LIMIT;
    private final OkHttpClient httpClient;

    @Override // com.exteragram.messenger.ai.network.backend.Backend
    public int getHistoryCharsLimit() {
        return 24000;
    }

    @Override // com.exteragram.messenger.ai.network.backend.Backend
    public int getHistoryMessagesLimit() {
        return 32;
    }

    @Override // com.exteragram.messenger.ai.network.backend.Backend
    public int getMaxOutputTokens() {
        return 4096;
    }

    @Override // com.exteragram.messenger.ai.network.backend.Backend
    public float getMaxTemperature() {
        return 2.0f;
    }

    @Override // com.exteragram.messenger.ai.network.backend.Backend
    public boolean supportsImages() {
        return true;
    }

    static {
        STREAM_SYMBOLS_LIMIT = SharedConfig.getDevicePerformanceClass() >= 1 ? 10 : 20;
    }

    public RestBackend() {
        OkHttpClient.Builder builderDns = ExteraHttpClient.INSTANCE.getClient().newBuilder().dns(ProxyDns.INSTANCE);
        TimeUnit timeUnit = TimeUnit.MINUTES;
        this.httpClient = builderDns.connectTimeout(1L, timeUnit).readTimeout(5L, timeUnit).build();
    }

    @Override // com.exteragram.messenger.ai.network.backend.Backend
    public void execute(BackendRequest backendRequest, BackendSink backendSink) {
        Request requestCreateRequest = createRequest(backendRequest);
        if (requestCreateRequest == null) {
            backendSink.onError(MediaError.DetailedErrorCode.SEGMENT_UNKNOWN, "Failed to create request body");
            return;
        }
        if (backendSink.isActive()) {
            Call callNewCall = this.httpClient.newCall(requestCreateRequest);
            Objects.requireNonNull(callNewCall);
            backendSink.onCancellable(callNewCall::cancel);
            try {
                Response responseExecute = callNewCall.execute();
                try {
                    if (!responseExecute.isSuccessful()) {
                        String strString = null;
                        try {
                            strString = responseExecute.body().string();
                            FileLog.e("AI_ERROR_RESPONSE_BODY (" + responseExecute.code() + "): " + strString);
                        } catch (IOException e) {
                            FileLog.e("AI_ERROR_READING_RESPONSE_BODY: ", e);
                        }
                        backendSink.onError(responseExecute.code(), parseErrorMessage(strString, responseExecute.message()));
                        responseExecute.close();
                        return;
                    }
                    ResponseBody responseBodyBody = responseExecute.body();
                    if (backendRequest.stream()) {
                        handleStreamResponse(responseBodyBody, backendSink);
                    } else {
                        String responseContent = parseResponseContent(responseBodyBody.string());
                        if (responseContent == null) {
                            backendSink.onError(MediaError.DetailedErrorCode.SEGMENT_UNKNOWN, "Failed to parse response");
                            responseExecute.close();
                            return;
                        }
                        backendSink.onComplete(responseContent);
                    }
                    responseExecute.close();
                } catch (Throwable th) {
                    if (responseExecute != null) {
                        try {
                            responseExecute.close();
                        } catch (Throwable th2) {
                            th.addSuppressed(th2);
                        }
                    }
                    throw th;
                }
            } catch (IOException e) {
                FileLog.e(e);
                backendSink.onError(MediaError.DetailedErrorCode.SEGMENT_UNKNOWN, e.getMessage() != null ? e.getMessage() : "Network error");
            } catch (Throwable th3) {
                FileLog.e(th3);
                backendSink.onError(MediaError.DetailedErrorCode.SEGMENT_UNKNOWN, "Unknown error");
            }
        }
    }

    private Request createRequest(BackendRequest backendRequest) {
        Service service = backendRequest.service();
        String url = service.getUrl();
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        String str = url.contains("generativelanguage.googleapis") ? "https://generativelanguage.googleapis.com/v1beta/openai/" : url;
        String strConcat = str.concat(str.endsWith("/") ? "chat/completions" : "/chat/completions");
        JSONObject jSONObject = new JSONObject();
        JSONArray jSONArray = new JSONArray();
        try {
            Role role = backendRequest.role();
            if (role != null && !TextUtils.isEmpty(role.getPrompt())) {
                jSONArray.put(new JSONObject().put("role", "system").put("content", role.getPrompt()));
            }
            Iterator<Message> it = backendRequest.history().iterator();
            while (it.hasNext()) {
                jSONArray.put(createMessageObject(it.next()));
            }
            jSONArray.put(createMessageObject(backendRequest.currentMessage()));
            jSONObject.put("model", service.getModel());
            jSONObject.put("messages", jSONArray);
            jSONObject.put("stream", backendRequest.stream());
            jSONObject.put("temperature", backendRequest.temperature());
            applyReasoningConfig(jSONObject, service, url);
            jSONObject.put("max_tokens", backendRequest.maxOutputTokens());
            FileLog.d("AI_REQUEST_URL: " + strConcat);
            FileLog.d("AI_REQUEST_MODEL: " + service.getModel());
            StringBuilder sb = new StringBuilder();
            sb.append("AI_REQUEST_MESSAGES: ");
            sb.append(jSONArray.length());
            sb.append(", stream=");
            sb.append(backendRequest.stream());
            sb.append(", image=");
            sb.append(backendRequest.currentMessage().getImageData() != null);
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
                jSONArray.put(new JSONObject().put("type", "text").put("text", message.content()));
            }
            jSONArray.put(new JSONObject().put("type", "image_url").put("image_url", new JSONObject().put("url", "data:" + message.getMimeType() + ";base64," + Base64.encodeToString(message.getImageData(), 2))));
            jSONObject.put("content", jSONArray);
            return jSONObject;
        }
        jSONObject.put("content", message.content());
        return jSONObject;
    }

    /* JADX WARN: Code duplicated, block: B:45:0x00a1  */
    /* JADX WARN: Code duplicated, block: B:49:0x00ac A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:50:0x00ae  */
    /* JADX WARN: Code duplicated, block: B:52:0x00b4  */
    /* JADX WARN: Code duplicated, block: B:53:0x00b9  */
    /* JADX WARN: Code duplicated, block: B:56:0x00c1  */
    /* JADX WARN: Code duplicated, block: B:58:0x00cb  */
    /* JADX WARN: Code duplicated, block: B:61:0x00e3  */
    /* JADX WARN: Code duplicated, block: B:62:0x00e7  */
    /* JADX WARN: Code duplicated, block: B:83:? A[RETURN, SYNTHETIC] */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v0, types: [com.exteragram.messenger.ai.network.backend.RestBackend-IA] */
    /* JADX WARN: Type inference failed for: r2v1 */
    /* JADX WARN: Type inference failed for: r2v2, types: [java.lang.Throwable] */
    /* JADX WARN: Type inference failed for: r2v3 */
    private void handleStreamResponse(ResponseBody responseBody, BackendSink backendSink) {
        StringBuilder sb = new StringBuilder();
        Throwable error = null;
        ReasoningContentFilter reasoningContentFilter = new ReasoningContentFilter();
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseBody.byteStream()))) {
            int length = 0;
            String line;
            while (backendSink.isActive() && (line = bufferedReader.readLine()) != null) {
                if (!TextUtils.isEmpty(line) && line.startsWith("data:")) {
                    String strTrim = line.substring(5).trim();
                    if (strTrim.equals("[DONE]")) {
                        break;
                    }
                    StreamResponsePart streamResponsePart = parseStreamResponsePart(strTrim);
                    if (streamResponsePart.hasReasoning()) {
                        backendSink.onThinking();
                    }
                    String strFilter = reasoningContentFilter.filter(streamResponsePart.content());
                    if (reasoningContentFilter.consumeReasoningSignal()) {
                        backendSink.onThinking();
                    }
                    if (!TextUtils.isEmpty(strFilter)) {
                        sb.append(strFilter);
                        length += strFilter.length();
                        if (length >= STREAM_SYMBOLS_LIMIT) {
                            backendSink.onChunk(sb.toString());
                            length = 0;
                        }
                    }
                }
            }
        } catch (Exception e) {
            if (backendSink.isActive()) {
                FileLog.e(e);
                error = e;
            }
        }
        if (!backendSink.isActive()) {
            return;
        }
        if (error != null) {
            String message = error.getMessage() != null ? error.getMessage() : "Unknown error";
            backendSink.onError(MediaError.DetailedErrorCode.SEGMENT_UNKNOWN, message);
            return;
        }
        String strFlush = reasoningContentFilter.flush();
        if (!TextUtils.isEmpty(strFlush)) {
            sb.append(strFlush);
            backendSink.onChunk(sb.toString());
        }
        String strTrimTrailing = trimTrailing(sb.toString());
        if (!TextUtils.isEmpty(strTrimTrailing)) {
            backendSink.onComplete(strTrimTrailing);
        } else {
            backendSink.onError(Opcodes.SUB_DOUBLE_2ADDR, "Response body is empty");
        }
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
        String str2 = "";
        boolean z = false;
        try {
            JSONArray jSONArrayOptJSONArray = new JSONObject(str).optJSONArray("choices");
            if (jSONArrayOptJSONArray != null && jSONArrayOptJSONArray.length() > 0) {
                JSONObject jSONObjectOptJSONObject = jSONArrayOptJSONArray.getJSONObject(0).optJSONObject("delta");
                if (jSONObjectOptJSONObject == null) {
                    return new StreamResponsePart(str2, z);
                }
                Object objOpt = jSONObjectOptJSONObject.opt("content");
                return new StreamResponsePart((objOpt == null || objOpt == JSONObject.NULL) ? "" : objOpt.toString(), hasReasoning(jSONObjectOptJSONObject));
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
        String strExtractErrorMessage = extractErrorMessage(str);
        if (TextUtils.isEmpty(strExtractErrorMessage)) {
            return !TextUtils.isEmpty(str2) ? str2.toLowerCase(Locale.ROOT) : "";
        }
        return strExtractErrorMessage;
    }

    private String extractErrorMessage(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String strTrim = str.trim();
        try {
            if (strTrim.startsWith("[")) {
                JSONArray jSONArray = new JSONArray(strTrim);
                for (int i = 0; i < jSONArray.length(); i++) {
                    JSONObject jSONObjectOptJSONObject = jSONArray.optJSONObject(i);
                    JSONObject jSONObjectOptJSONObject2 = jSONObjectOptJSONObject == null ? null : jSONObjectOptJSONObject.optJSONObject("error");
                    String strOptString = jSONObjectOptJSONObject2 == null ? null : jSONObjectOptJSONObject2.optString("message", null);
                    if (!TextUtils.isEmpty(strOptString)) {
                        return strOptString;
                    }
                }
                return null;
            }
            JSONObject jSONObjectOptJSONObject3 = new JSONObject(strTrim).optJSONObject("error");
            if (jSONObjectOptJSONObject3 == null) {
                return null;
            }
            return jSONObjectOptJSONObject3.optString("message", null);
        } catch (Exception unused) {
            return null;
        }
    }

    public static class ReasoningContentFilter {
        private boolean inReasoning;
        private String pending = "";
        private boolean reasoningSignal;

        public String filter(String str) {
            int i;
            if (TextUtils.isEmpty(str)) {
                return null;
            }
            String str2 = this.pending + str;
            this.pending = "";
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
            String str = this.inReasoning ? "" : this.pending;
            this.pending = "";
            return str;
        }

        private String getOpenTagPrefixSuffix(String str, int i) {
            String lowerCase = str.toLowerCase(Locale.ROOT);
            for (int iMin = Math.min(6, str.length() - i); iMin > 0; iMin--) {
                if ("<think>".startsWith(lowerCase.substring(str.length() - iMin))) {
                    return str.substring(str.length() - iMin);
                }
            }
            return "";
        }

        private String getCloseTagPrefixSuffix(String str, int i) {
            String lowerCase = str.toLowerCase(Locale.ROOT);
            for (int iMin = Math.min(7, str.length() - i); iMin > 0; iMin--) {
                if ("</think>".startsWith(lowerCase.substring(str.length() - iMin))) {
                    return str.substring(str.length() - iMin);
                }
            }
            return "";
        }
    }

    public static final class StreamResponsePart {
        private final String content;
        private final boolean hasReasoning;

        public StreamResponsePart(String str, boolean z) {
            this.content = str;
            this.hasReasoning = z;
        }

        public String content() {
            return this.content;
        }

        public boolean hasReasoning() {
            return this.hasReasoning;
        }

        public final boolean equals(Object obj) {
            if (!(obj instanceof StreamResponsePart)) {
                return false;
            }
            StreamResponsePart other = (StreamResponsePart) obj;
            return this.hasReasoning == other.hasReasoning && Objects.equals(this.content, other.content);
        }

        public final int hashCode() {
            return Objects.hash(this.content, this.hasReasoning);
        }

        public final String toString() {
            return "StreamResponsePart[content=" + this.content + ", hasReasoning=" + this.hasReasoning + "]";
        }
    }
}
