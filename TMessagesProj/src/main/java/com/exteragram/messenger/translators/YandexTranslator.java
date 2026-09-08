package com.exteragram.messenger.translators;

import com.exteragram.messenger.utils.network.ExteraHttpClient;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.url._UrlKt;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;

public class YandexTranslator extends BaseTranslator {
    private static YandexTranslator instance;
    private static final OkHttpClient client = ExteraHttpClient.INSTANCE.getClient();
    private static final String uuid = UUID.randomUUID().toString().replace("-", _UrlKt.FRAGMENT_ENCODE_SET);
    private static final Set<String> SUPPORTED_LANGUAGES = new HashSet(new java.util.HashSet<>(java.util.Arrays.asList("az", "sq", "am", "en", "ar", "hy", "af", "eu", "ba", "be", "bn", "my", "bg", "bs", "cv", "cy", "hu", "vi", "ht", ImageLoader.AUTOPLAY_FILTER_NONLOOP, "nl", "mrj", "el", "ka", "gu", "da", "he", "yi", "id", "ga", "it", "is", "es", "kk", "kn", "ca", "ky", "zh", "ko", "xh", "km", "lo", "la", "lv", "lt", "lb", "mg", "ms", "ml", "mt", "mk", "mi", "mr", "mhr", "mn", "de", "ne", "no", "pa", "pap", "fa", "pl", "pt", "ro", "ru", "ceb", "sr", "si", "sk", "sl", "sw", "su", "tg", "th", "tl", "ta", "tt", "te", "tr", "udm", "uz", "uk", "ur", "fi", "fr", "hi", "hr", "cs", "sv", "gd", "et", "eo", "jv", "ja")));

    public static YandexTranslator getInstance() {
        if (instance == null) {
            instance = new YandexTranslator();
        }
        return instance;
    }

    @Override 
    public String getDisplayName() {
        return "Yandex";
    }

    @Override 
    public Set<String> getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }

    @Override 
    public void translate(String str, String str2, String str3, final TranslatorUtils.TranslateCallback translateCallback) {
        try {
            client.newCall(new Request.Builder().url("https:
                @Override // okhttp3.Callback
                public void onFailure(Call call, IOException iOException) {
                    FileLog.e(iOException);
                    TranslatorUtils.TranslateCallback translateCallback2 = translateCallback;
                    Objects.requireNonNull(translateCallback2);
                    AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback2));
                }

                @Override // okhttp3.Callback
                public void onResponse(Call call, Response response) {
                    ResponseBody responseBodyBody = response.body();
                    try {
                        if (!response.getIsSuccessful()) {
                            TranslatorUtils.TranslateCallback translateCallback2 = translateCallback;
                            Objects.requireNonNull(translateCallback2);
                            AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback2));
                            if (responseBodyBody != null) {
                                responseBodyBody.close();
                                return;
                            }
                            return;
                        }
                        String strString = responseBodyBody.string();
                        try {
                            JSONObject jSONObject = new JSONObject(strString);
                            if (jSONObject.has("text") || !jSONObject.has("message")) {
                                YandexTranslator.this.processJsonResponse(strString, "text", translateCallback);
                                responseBodyBody.close();
                            } else {
                                TranslatorUtils.TranslateCallback translateCallback3 = translateCallback;
                                Objects.requireNonNull(translateCallback3);
                                AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback3));
                                responseBodyBody.close();
                            }
                        } catch (Exception e) {
                            FileLog.e(e);
                            TranslatorUtils.TranslateCallback translateCallback4 = translateCallback;
                            Objects.requireNonNull(translateCallback4);
                            AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback4));
                        }
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
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
            Objects.requireNonNull(translateCallback);
            AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback));
        }
    }
}
