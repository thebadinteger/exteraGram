package com.exteragram.messenger.translators;

import com.exteragram.messenger.utils.network.ExteraHttpClient;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public class GoogleTranslator extends BaseTranslator {
    private static final OkHttpClient client = ExteraHttpClient.INSTANCE.getClient();
    private static GoogleTranslator instance;

    public static GoogleTranslator getInstance() {
        if (instance == null) {
            instance = new GoogleTranslator();
        }
        return instance;
    }

    @Override 
    public String getDisplayName() {
        return "Google";
    }

    @Override 
    public Set<String> getSupportedLanguages() {
        return Collections.EMPTY_SET;
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
                    try {
                        ResponseBody responseBodyBody = response.body();
                        try {
                            if (!response.getIsSuccessful()) {
                                TranslatorUtils.TranslateCallback translateCallback2 = translateCallback;
                                Objects.requireNonNull(translateCallback2);
                                AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback2));
                                if (responseBodyBody == null) {
                                    return;
                                }
                            } else {
                                GoogleTranslator.this.processJsonResponse(responseBodyBody.string(), "sentences", translateCallback);
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
                    } catch (Exception e) {
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
}
