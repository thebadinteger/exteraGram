package com.exteragram.messenger.translators;

import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.TranslationFormality;
import com.exteragram.messenger.utils.network.ExteraHttpClient;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.connection.RealConnection;
import okhttp3.internal.url._UrlKt;
import okio.GzipSource;
import okio.Okio;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class DeepLTranslator extends BaseTranslator {
    private static DeepLTranslator instance;
    private static final OkHttpClient client = ExteraHttpClient.INSTANCE.getClient().newBuilder().addNetworkInterceptor(new Interceptor() { // from class: com.exteragram.messenger.translators.DeepLTranslator$$ExternalSyntheticLambda0
        @Override // okhttp3.Interceptor
        public final Response intercept(Interceptor.Chain chain) throws IOException {
            return DeepLTranslator.$r8$lambda$C3t8HA5LdSNw5SCJiN1vp78rw6U(chain);
        }
    }).build();
    private static final Set<String> SUPPORTED_LANGUAGES = new HashSet<>(Arrays.asList("bg", "cs", "da", "de", "el", "en", "en-GB", "en-US", "es", "fi", "fr", "hu", "id", "it", "ja", "lt", "lv", "nl", "pl", "pt", "pt-BR", "pt-PT", "ro", "ru", "sk", "sl", "sv", "tr", "uk", "zh"));

    public static /* synthetic */ Response $r8$lambda$C3t8HA5LdSNw5SCJiN1vp78rw6U(Interceptor.Chain chain) throws IOException {
        ResponseBody responseBodyCreate;
        Response responseProceed = chain.proceed(chain.request());
        if ("gzip".equalsIgnoreCase(responseProceed.header("Content-Encoding"))) {
            try {
                GzipSource gzipSource = new GzipSource(responseProceed.body().source());
                String strHeader = responseProceed.header("Content-Type");
                if (strHeader != null) {
                    responseBodyCreate = ResponseBody.create(Okio.buffer(gzipSource).readByteArray(), MediaType.parse(strHeader));
                } else {
                    responseBodyCreate = null;
                }
                return responseProceed.newBuilder().removeHeader("Content-Encoding").body(responseBodyCreate).build();
            } catch (IOException e) {
                FileLog.e(e);
            }
        }
        return responseProceed;
    }

    public static DeepLTranslator getInstance() {
        if (instance == null) {
            instance = new DeepLTranslator();
        }
        return instance;
    }

    @Override // com.exteragram.messenger.translators.BaseTranslator
    public String getDisplayName() {
        return "DeepL";
    }

    @Override // com.exteragram.messenger.translators.BaseTranslator
    public Set<String> getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }

    @Override // com.exteragram.messenger.translators.BaseTranslator
    public void translate(String str, String str2, String str3, final TranslatorUtils.TranslateCallback translateCallback) {
        Object obj;
        String strReplace;
        try {
            String lowerCase = str3.toLowerCase();
            if (lowerCase.contains("-")) {
                String[] strArrSplit = lowerCase.split("-");
                String str4 = strArrSplit[0];
                obj = strArrSplit[0] + "-" + strArrSplit[1].toUpperCase();
                lowerCase = str4;
            } else {
                obj = null;
            }
            String lowerCase2 = str2.toLowerCase();
            if (lowerCase2.equalsIgnoreCase("auto")) {
                lowerCase2 = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            long jNextLong = ThreadLocalRandom.current().nextLong(1L, RealConnection.IDLE_CONNECTION_HEALTHY_NS);
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("method", "LMT_handle_texts");
            jSONObject.put("id", jNextLong);
            jSONObject.put("jsonrpc", "2.0");
            JSONObject jSONObject2 = new JSONObject();
            jSONObject2.put("splitting", "newlines");
            jSONObject2.put("priority", 1);
            JSONArray jSONArray = new JSONArray();
            JSONObject jSONObject3 = new JSONObject();
            jSONObject3.put("requestAlternatives", 0);
            jSONObject3.put("text", str);
            jSONArray.put(jSONObject3);
            jSONObject2.put("texts", jSONArray);
            JSONObject jSONObject4 = new JSONObject();
            jSONObject4.put("target_lang", lowerCase);
            jSONObject4.put("source_lang_user_selected", lowerCase2);
            jSONObject2.put("lang", jSONObject4);
            JSONObject jSONObject5 = new JSONObject();
            if (obj == null) {
                obj = JSONObject.NULL;
            }
            jSONObject5.put("regionalVariant", obj);
            jSONObject5.put("wasSpoken", false);
            jSONObject5.put("formality", getFormality() == null ? JSONObject.NULL : getFormality());
            jSONObject2.put("commonJobParams", jSONObject5);
            jSONObject2.put("timestamp", System.currentTimeMillis());
            jSONObject.put("params", jSONObject2);
            String string = jSONObject.toString();
            if ((3 + jNextLong) % 13 == 0 || (jNextLong + 5) % 29 == 0) {
                strReplace = string.replace("hod\":\"", "hod\" : \"");
            } else {
                strReplace = string.replace("hod\":\"", "hod\": \"");
            }
            client.newCall(new Request.Builder().url("https://www2.deepl.com/jsonrpc").header("referer", "https://www.deepl.com/").header("user-agent", "DeepL/25.2.1(150) Android 14 (Pixel 7 Pro;aarch64)").header("x-app-os-name", "Android").header("x-app-os-version", "14").header("x-app-version", "25.2.1").header("x-app-build", "150").header("x-app-device", "Pixel 7 Pro").header("x-app-instance-id", UUID.randomUUID().toString()).header("accept-encoding", "gzip").post(RequestBody.create(strReplace.getBytes(StandardCharsets.UTF_8), MediaType.parse("application/json; charset=utf-8"))).build()).enqueue(new Callback() { // from class: com.exteragram.messenger.translators.DeepLTranslator.1
                @Override // okhttp3.Callback
                public void onFailure(Call call, IOException iOException) {
                    FileLog.e(iOException);
                    TranslatorUtils.TranslateCallback translateCallback2 = translateCallback;
                    Objects.requireNonNull(translateCallback2);
                    AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback2));
                }

                @Override // okhttp3.Callback
                public void onResponse(Call call, Response response) {
                    try {
                        ResponseBody responseBodyBody = response.body();
                        try {
                            if (!response.isSuccessful() || responseBodyBody == null) {
                                TranslatorUtils.TranslateCallback translateCallback2 = translateCallback;
                                Objects.requireNonNull(translateCallback2);
                                AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback2));
                                if (responseBodyBody == null) {
                                    return;
                                }
                            } else {
                                DeepLTranslator.this.processJsonResponse(responseBodyBody.string(), "result.texts", translateCallback);
                            }
                            responseBodyBody.close();
                        } catch (Throwable th) {
                            if (responseBodyBody != null) {
                                try {
                                    responseBodyBody.close();
                                } catch (Throwable th2) {
                                    th.addSuppressed(th2);
                                }
                            }
                            throw th;
                        }
                    } catch (IOException e) {
                        FileLog.e(e);
                        TranslatorUtils.TranslateCallback translateCallback3 = translateCallback;
                        Objects.requireNonNull(translateCallback3);
                        AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback3));
                    }
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
            Objects.requireNonNull(translateCallback);
            AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback));
        }
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.translators.DeepLTranslator$2, reason: invalid class name */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$exteragram$messenger$TranslationFormality;

        static {
            int[] iArr = new int[TranslationFormality.values().length];
            $SwitchMap$com$exteragram$messenger$TranslationFormality = iArr;
            try {
                iArr[TranslationFormality.INFORMAL.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$TranslationFormality[TranslationFormality.FORMAL.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    private static String getFormality() {
        int i = AnonymousClass2.$SwitchMap$com$exteragram$messenger$TranslationFormality[ExteraConfig.getTranslationFormality().ordinal()];
        if (i == 1) {
            return "informal";
        }
        if (i != 2) {
            return null;
        }
        return "formal";
    }
}
