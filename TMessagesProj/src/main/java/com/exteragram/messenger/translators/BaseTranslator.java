package com.exteragram.messenger.translators;

import android.text.TextUtils;
import com.exteragram.messenger.utils.text.TranslatorUtils;
import java.util.Objects;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;

public abstract class BaseTranslator {
    public abstract String getDisplayName();

    public abstract Set<String> getSupportedLanguages();

    public abstract void translate(String str, String str2, String str3, TranslatorUtils.TranslateCallback translateCallback);

    public void processJsonResponse(String str, String str2, final TranslatorUtils.TranslateCallback translateCallback) {
        if (TextUtils.isEmpty(str)) {
            Objects.requireNonNull(translateCallback);
            AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback));
            return;
        }
        try {
            Object jSONObject = new JSONObject(str);
            for (String str3 : str2.split("\\.")) {
                if (str3.contains("[")) {
                    jSONObject = ((JSONObject) jSONObject).getJSONArray(str3.substring(0, str3.indexOf(91))).get(Integer.parseInt(str3.substring(str3.indexOf(91) + 1, str3.indexOf(93))));
                } else {
                    jSONObject = ((JSONObject) jSONObject).get(str3);
                }
            }
            final StringBuilder sb = new StringBuilder();
            if (jSONObject instanceof String) {
                sb.append(jSONObject);
            } else if (jSONObject instanceof JSONArray) {
                JSONArray jSONArray = (JSONArray) jSONObject;
                for (int i = 0; i < jSONArray.length(); i++) {
                    if (jSONArray.get(i) instanceof JSONObject) {
                        JSONObject jSONObject2 = jSONArray.getJSONObject(i);
                        if (jSONObject2.has("trans")) {
                            sb.append(jSONObject2.getString("trans"));
                        } else if (jSONObject2.has("text")) {
                            sb.append(jSONObject2.getString("text"));
                        }
                    } else if (jSONArray.get(i) instanceof String) {
                        sb.append(jSONArray.getString(i));
                    }
                }
            }
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    translateCallback.onSuccess(sb.toString());
                }
            });
        } catch (Exception e) {
            FileLog.e(e);
            Objects.requireNonNull(translateCallback);
            AndroidUtilities.runOnUIThread(new BaseTranslator$$ExternalSyntheticLambda0(translateCallback));
        }
    }

    public boolean isLanguageSupported(String str) {
        if (getSupportedLanguages().isEmpty()) {
            return true;
        }
        String lowerCase = str.toLowerCase();
        if (lowerCase.contains("-")) {
            lowerCase = lowerCase.split("-")[0];
        }
        return getSupportedLanguages().contains(lowerCase);
    }
}
