package com.exteragram.messenger.ai.network.backend;

import android.os.Build;
import android.text.TextUtils;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.ai.data.Message;
import com.exteragram.messenger.ai.data.Role;
import com.google.android.gms.cast.MediaError;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.genai.common.GenAiException;
import com.google.mlkit.genai.common.StreamingCallback;
import com.google.mlkit.genai.prompt.CachedContext;
import com.google.mlkit.genai.prompt.Candidate;
import com.google.mlkit.genai.prompt.CreateCachedContextRequest;
import com.google.mlkit.genai.prompt.GenerateContentRequest;
import com.google.mlkit.genai.prompt.GenerateContentResponse;
import com.google.mlkit.genai.prompt.GenerativeModel;
import com.google.mlkit.genai.prompt.ImagePart;
import com.google.mlkit.genai.prompt.PromptPrefix;
import com.google.mlkit.genai.prompt.SystemInstruction;
import com.google.mlkit.genai.prompt.TextPart;
import com.google.mlkit.genai.prompt.java.CachesFutures;
import com.google.mlkit.genai.prompt.java.GenerativeModelFutures;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.Utilities;

/* JADX INFO: loaded from: classes4.dex */
public class OnDeviceBackend implements Backend {
    private static volatile boolean cachingUnsupported;

    private static boolean isInferenceFailure(int i) {
        return (i == -101 || i == 12 || i == 16 || i == 27 || i == 30 || i == 501 || i == 604 || i == 7 || i == 8 || i == 9) ? false : true;
    }

    @Override // com.exteragram.messenger.ai.network.backend.Backend
    public int getHistoryCharsLimit() {
        return 12000;
    }

    @Override // com.exteragram.messenger.ai.network.backend.Backend
    public int getHistoryMessagesLimit() {
        return 24;
    }

    @Override // com.exteragram.messenger.ai.network.backend.Backend
    public int getMaxOutputTokens() {
        return 1024;
    }

    @Override // com.exteragram.messenger.ai.network.backend.Backend
    public float getMaxTemperature() {
        return 1.0f;
    }

    @Override // com.exteragram.messenger.ai.network.backend.Backend
    public boolean supportsImages() {
        return true;
    }

    /* JADX WARN: Code restructure failed: missing block: B:27:0x005c, code lost:
    
        if (r1 != null) goto L58;
     */
    @Override // com.exteragram.messenger.ai.network.backend.Backend
    /*
        Code decompiled incorrectly, please refer to instructions dump.
    */
    public void execute(BackendRequest backendRequest, BackendSink backendSink) {
        if (Build.VERSION.SDK_INT < 26) {
            backendSink.onError(-1, "on-device model requires android 8.0");
            return;
        }
        int configGeneration = OnDeviceAvailability.getConfigGeneration();
        GenerativeModel generativeModelCreateModel = null;
        try {
            try {
                try {
                    generativeModelCreateModel = OnDeviceAvailability.createModel();
                    GenerativeModelFutures generativeModelFuturesFrom = GenerativeModelFutures.from(generativeModelCreateModel);
                    int iIntValue = generativeModelFuturesFrom.checkStatus().get(10L, TimeUnit.SECONDS).intValue();
                    OnDeviceAvailability.report(iIntValue, configGeneration);
                    if (!backendSink.isActive()) {
                        if (generativeModelCreateModel != null) {
                            try {
                                generativeModelCreateModel.close();
                                return;
                            } catch (Throwable unused) {
                                return;
                            }
                        }
                    }
                    if (iIntValue == 0) {
                        backendSink.onError(-1, "on-device model is not available");
                        if (generativeModelCreateModel != null) {
                            generativeModelCreateModel.close();
                            return;
                        }
                    }
                    if (iIntValue == 1) {
                        backendSink.onError(-2, "on-device model is not downloaded");
                        if (generativeModelCreateModel != null) {
                            generativeModelCreateModel.close();
                            return;
                        }
                    }
                    if (iIntValue == 2) {
                        backendSink.onError(-3, "on-device model is downloading");
                    } else {
                        OnDeviceAvailability.ensureMetadata(generativeModelFuturesFrom, configGeneration);
                        generate(backendRequest, backendSink, generativeModelFuturesFrom, generativeModelCreateModel);
                        if (generativeModelCreateModel != null) {
                            generativeModelCreateModel.close();
                        }
                    }
                } catch (Throwable th) {
                    if (generativeModelCreateModel != null) {
                        try {
                            generativeModelCreateModel.close();
                        } catch (Throwable unused2) {
                        }
                    }
                    throw th;
                }
            } catch (InterruptedException unused3) {
                Thread.currentThread().interrupt();
                if (generativeModelCreateModel == null) {
                    return;
                }
                generativeModelCreateModel.close();
            } catch (Exception e) {
                if (backendSink.isActive()) {
                    FileLog.e("AI_ON_DEVICE_FAILED", e);
                    GenAiException genAiExceptionFindGenAiException = findGenAiException(e);
                    String message = e.getMessage() != null ? e.getMessage() : "Unknown error";
                    if (genAiExceptionFindGenAiException == null || genAiExceptionFindGenAiException.getErrorCode() != 12) {
                        backendSink.onError(MediaError.DetailedErrorCode.SEGMENT_UNKNOWN, message);
                    } else {
                        backendSink.onError(-4, message);
                    }
                }
                if (generativeModelCreateModel == null) {
                    return;
                }
                generativeModelCreateModel.close();
            }
        } catch (Throwable unused4) {
        }
    }

    private static GenAiException findGenAiException(Throwable th) {
        while (th != null) {
            if (th instanceof GenAiException) {
                return (GenAiException) th;
            }
            th = th.getCause();
        }
        return null;
    }

    private void generate(BackendRequest backendRequest, final BackendSink backendSink, GenerativeModelFutures generativeModelFutures, GenerativeModel generativeModel) throws ExecutionException, InterruptedException {
        String str;
        SystemInstruction systemInstruction;
        OnDeviceBackend onDeviceBackend;
        GenerateContentRequest generateContentRequestCreateRequest;
        final ListenableFuture<GenerateContentResponse> listenableFutureGenerateContent;
        Role role = backendRequest.role();
        String strEnsureCachedContext = null;
        String strBuildInstruction = buildInstruction(role != null ? role.getPrompt() : null);
        boolean z = backendRequest.service().isReasoningEnabled() && OnDeviceAvailability.isThinkingSupported();
        if (OnDeviceAvailability.isSystemPromptSupported()) {
            systemInstruction = new SystemInstruction(strBuildInstruction);
            str = _UrlKt.FRAGMENT_ENCODE_SET;
        } else {
            str = strBuildInstruction + "\n\n";
            systemInstruction = null;
        }
        String str2 = str;
        ImagePart imagePartCreateImagePart = createImagePart(backendRequest.currentMessage());
        List<Message> listHistory = backendRequest.history();
        int iResolveTokenLimit = (resolveTokenLimit() - backendRequest.maxOutputTokens()) - 256;
        int i = 0;
        while (backendSink.isActive()) {
            String strBuildPrompt = buildPrompt(listHistory, backendRequest.currentMessage(), i);
            SystemInstruction systemInstruction2 = systemInstruction;
            int iCountTokens = countTokens(generativeModelFutures, createRequest(backendRequest, systemInstruction2, null, imagePartCreateImagePart, str2 + strBuildPrompt, z));
            if (iCountTokens <= iResolveTokenLimit || i >= listHistory.size()) {
                if (iCountTokens > iResolveTokenLimit) {
                    backendSink.onError(-4, "prompt exceeds the on-device token limit");
                    return;
                }
                if (backendSink.isActive()) {
                    if (systemInstruction2 == null && imagePartCreateImagePart == null) {
                        strEnsureCachedContext = ensureCachedContext(generativeModel, strBuildInstruction);
                    }
                    String str3 = strEnsureCachedContext;
                    if (str3 != null) {
                        generateContentRequestCreateRequest = createRequest(backendRequest, null, str3, imagePartCreateImagePart, strBuildPrompt, z);
                        onDeviceBackend = this;
                    } else {
                        onDeviceBackend = this;
                        generateContentRequestCreateRequest = onDeviceBackend.createRequest(backendRequest, systemInstruction2, null, imagePartCreateImagePart, str2 + strBuildPrompt, z);
                    }
                    final StringBuffer stringBuffer = new StringBuffer();
                    if (backendRequest.stream()) {
                        listenableFutureGenerateContent = generativeModelFutures.generateContent(generateContentRequestCreateRequest, new StreamingCallback() { // from class: com.exteragram.messenger.ai.network.backend.OnDeviceBackend.1
                            @Override // com.google.mlkit.genai.common.StreamingCallback
                            public void onNewText(String str4) {
                                if (!backendSink.isActive() || TextUtils.isEmpty(str4)) {
                                    return;
                                }
                                stringBuffer.append(str4);
                                backendSink.onChunk(stringBuffer.toString());
                            }

                            @Override // com.google.mlkit.genai.common.StreamingCallback
                            public void onNewThought(String str4) {
                                if (backendSink.isActive()) {
                                    backendSink.onThinking();
                                }
                            }
                        });
                    } else {
                        listenableFutureGenerateContent = generativeModelFutures.generateContent(generateContentRequestCreateRequest);
                    }
                    backendSink.onCancellable(new Runnable() { // from class: com.exteragram.messenger.ai.network.backend.OnDeviceBackend$$ExternalSyntheticLambda0
                        @Override // java.lang.Runnable
                        public final void run() {
                            listenableFutureGenerateContent.cancel(true);
                        }
                    });
                    try {
                        GenerateContentResponse generateContentResponse = listenableFutureGenerateContent.get();
                        if (backendSink.isActive()) {
                            String strExtractText = onDeviceBackend.extractText(generateContentResponse);
                            if (TextUtils.isEmpty(strExtractText)) {
                                strExtractText = stringBuffer.toString();
                            }
                            String strTrimTrailing = onDeviceBackend.trimTrailing(strExtractText);
                            if (TextUtils.isEmpty(strTrimTrailing)) {
                                backendSink.onError(Opcodes.SUB_DOUBLE_2ADDR, "Response body is empty");
                                return;
                            } else {
                                backendSink.onComplete(strTrimTrailing);
                                return;
                            }
                        }
                        return;
                    } catch (ExecutionException e) {
                        GenAiException genAiExceptionFindGenAiException = findGenAiException(e);
                        if (genAiExceptionFindGenAiException == null || !isInferenceFailure(genAiExceptionFindGenAiException.getErrorCode())) {
                            throw e;
                        }
                        if (backendSink.isActive()) {
                            FileLog.e("AI_ON_DEVICE_INFERENCE_FAILED", e);
                            backendSink.onError(-5, genAiExceptionFindGenAiException.getMessage() != null ? genAiExceptionFindGenAiException.getMessage() : "Inference failed");
                            return;
                        }
                        return;
                    }
                }
                return;
            }
            i += 2;
            systemInstruction = systemInstruction2;
        }
    }

    private GenerateContentRequest createRequest(BackendRequest backendRequest, SystemInstruction systemInstruction, String str, ImagePart imagePart, String str2, boolean z) {
        GenerateContentRequest.Builder builder;
        TextPart textPart = new TextPart(str2);
        if (systemInstruction != null && imagePart != null) {
            builder = new GenerateContentRequest.Builder(systemInstruction, imagePart, textPart);
        } else if (systemInstruction != null) {
            builder = new GenerateContentRequest.Builder(systemInstruction, textPart);
        } else if (imagePart != null) {
            builder = new GenerateContentRequest.Builder(imagePart, textPart);
        } else {
            builder = new GenerateContentRequest.Builder(textPart);
        }
        builder.setTemperature(Float.valueOf(backendRequest.temperature()));
        builder.setMaxOutputTokens(Integer.valueOf(backendRequest.maxOutputTokens()));
        builder.setEnableThinking(z);
        if (str != null) {
            builder.setCachedContextName(str);
        }
        return builder.build();
    }

    private String ensureCachedContext(GenerativeModel generativeModel, String str) {
        TimeUnit timeUnit = TimeUnit.SECONDS;
        if (!cachingUnsupported && !TextUtils.isEmpty(str)) {
            String strMD5 = Utilities.MD5(str);
            if (TextUtils.isEmpty(strMD5)) {
                return null;
            }
            String str2 = "extera_role_" + strMD5;
            try {
                CachesFutures cachesFuturesFrom = CachesFutures.from(generativeModel);
                ArrayList arrayList = new ArrayList();
                List<CachedContext> list = cachesFuturesFrom.list().get(10L, timeUnit);
                if (list != null) {
                    Iterator<CachedContext> it = list.iterator();
                    while (it.hasNext()) {
                        CachedContext next = it.next();
                        String zza = next == null ? null : next.getName();
                        if (zza != null && zza.startsWith("extera_role_")) {
                            if (str2.equals(zza)) {
                                return str2;
                            }
                            arrayList.add(zza);
                        }
                    }
                }
                if (arrayList.size() >= 4) {
                    int size = arrayList.size();
                    int i = 0;
                    while (i < size) {
                        Object obj = arrayList.get(i);
                        i++;
                        cachesFuturesFrom.delete((String) obj).get(10L, timeUnit);
                    }
                }
                CachedContext cachedContext = cachesFuturesFrom.create(new CreateCachedContextRequest.Builder(str2, new PromptPrefix(str)).build()).get(30L, timeUnit);
                if (cachedContext == null) {
                    return null;
                }
                return cachedContext.getName();
            } catch (InterruptedException unused) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                cachingUnsupported = true;
                FileLog.e("AI_ON_DEVICE_CACHE", e);
                return null;
            }
        }
        return null;
    }

    private String buildInstruction(String str) {
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(str)) {
            sb.append(str);
            sb.append("\n\n");
        }
        sb.append("Unless told otherwise, always reply in the same language the user writes in.");
        Locale currentLocale = LocaleController.getInstance().getCurrentLocale();
        String displayLanguage = currentLocale != null ? currentLocale.getDisplayLanguage(Locale.ENGLISH) : null;
        if (!TextUtils.isEmpty(displayLanguage)) {
            sb.append(" If the language is unclear, reply in ");
            sb.append(displayLanguage);
            sb.append('.');
        }
        return sb.toString();
    }

    private String buildPrompt(List<Message> list, Message message, int i) {
        StringBuilder sb = new StringBuilder();
        while (true) {
            if (i < list.size()) {
                Message message2 = list.get(i);
                if (message2 != null && !TextUtils.isEmpty(message2.content())) {
                    sb.append("assistant".equals(message2.role()) ? "Assistant: " : "User: ");
                    sb.append(message2.content());
                    sb.append("\n\n");
                }
                i++;
            } else {
                sb.append("User: ");
                sb.append(message.content());
                return sb.toString();
            }
        }
    }

    private ImagePart createImagePart(Message message) {
        byte[] imageData = message.getImageData();
        if (imageData != null && imageData.length != 0) {
            try {
                return new ImagePart(imageData);
            } catch (Exception e) {
                FileLog.e("AI_ON_DEVICE_IMAGE_REJECTED", e);
            }
        }
        return null;
    }

    private int resolveTokenLimit() {
        int tokenLimit = OnDeviceAvailability.getTokenLimit();
        if (tokenLimit > 0) {
            return tokenLimit;
        }
        return 4000;
    }

    private int countTokens(GenerativeModelFutures generativeModelFutures, GenerateContentRequest generateContentRequest) {
        try {
            return generativeModelFutures.countTokens(generateContentRequest).get(10L, TimeUnit.SECONDS).getTotalTokens();
        } catch (Exception e) {
            FileLog.e("AI_ON_DEVICE_COUNT_TOKENS", e);
            return 0;
        }
    }

    private String extractText(GenerateContentResponse generateContentResponse) {
        List<Candidate> candidates;
        if (generateContentResponse == null || (candidates = generateContentResponse.getCandidates()) == null || candidates.isEmpty()) {
            return null;
        }
        return candidates.get(0).getText();
    }

    private String trimTrailing(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        while (length > 0 && Character.isWhitespace(str.charAt(length - 1))) {
            length--;
        }
        return str.substring(0, length);
    }
}
