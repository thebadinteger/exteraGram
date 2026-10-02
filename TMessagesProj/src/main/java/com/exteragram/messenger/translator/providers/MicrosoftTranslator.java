package com.exteragram.messenger.translator.providers;

import android.util.Base64;
import com.exteragram.messenger.plugins.PluginsConstants;
import com.exteragram.messenger.translator.core.HttpTranslator;
import com.exteragram.messenger.translator.core.ProviderLimits;
import com.exteragram.messenger.translator.core.ProviderResponse;
import com.exteragram.messenger.translator.core.TranslationError;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.text.StringsKt;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.url._UrlKt;
import org.json.JSONArray;
import org.json.JSONObject;
import org.telegram.messenger.FileLog;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000L\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u0000 !2\u00020\u0001:\u0001!B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u00132\u0006\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0015\u001a\u00020\u0005H\u0016J\u0010\u0010\u0016\u001a\u00020\u00052\u0006\u0010\u0017\u001a\u00020\u0005H\u0002J\u0018\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001dH\u0016J\u0018\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010 \u001a\u00020\u0005H\u0002R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096D¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001a\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\tX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\f\u001a\u00020\rX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\""}, d2 = {"Lcom/exteragram/messenger/translator/providers/MicrosoftTranslator;", "Lcom/exteragram/messenger/translator/core/HttpTranslator;", "<init>", "()V", "displayName", _UrlKt.FRAGMENT_ENCODE_SET, "getDisplayName", "()Ljava/lang/String;", "supportedLanguages", _UrlKt.FRAGMENT_ENCODE_SET, "getSupportedLanguages", "()Ljava/util/Set;", "limits", "Lcom/exteragram/messenger/translator/core/ProviderLimits;", "getLimits", "()Lcom/exteragram/messenger/translator/core/ProviderLimits;", "buildRequest", "Lokhttp3/Request;", "texts", _UrlKt.FRAGMENT_ENCODE_SET, "fromLang", "toLang", "signature", "path", "parseResponse", "Lcom/exteragram/messenger/translator/core/ProviderResponse;", PluginsConstants.RESPONSE, "Lokhttp3/Response;", "expected", _UrlKt.FRAGMENT_ENCODE_SET, "failure", "Lcom/exteragram/messenger/translator/core/TranslationError;", "body", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class MicrosoftTranslator extends HttpTranslator {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
    private static final Lazy<MicrosoftTranslator> shared$delegate = LazyKt.lazy(new Function0() { // from class: com.exteragram.messenger.translator.providers.MicrosoftTranslator$$ExternalSyntheticLambda0
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return MicrosoftTranslator.$r8$lambda$0dLF5QmlPQKXkZb52oac7kc5HWc();
        }
    });
    private final String displayName = "Microsoft";
    private final Set<String> supportedLanguages = SetsKt.emptySet();
    private final ProviderLimits limits = ProviderLimits.MICROSOFT;

    private MicrosoftTranslator() {
    }

    @Override // com.exteragram.messenger.translator.core.BaseTranslator
    public String getDisplayName() {
        return this.displayName;
    }

    @Override // com.exteragram.messenger.translator.core.BaseTranslator
    public Set<String> getSupportedLanguages() {
        return this.supportedLanguages;
    }

    @Override // com.exteragram.messenger.translator.core.HttpTranslator
    public ProviderLimits getLimits() {
        return this.limits;
    }

    @Override // com.exteragram.messenger.translator.core.HttpTranslator
    public Request buildRequest(List<String> texts, String fromLang, String toLang) {
        try {
            if (StringsKt.equals(fromLang, "auto", true)) {
                fromLang = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            String str = "api.cognitive.microsofttranslator.com/translate?api-version=3.0&from=" + fromLang + "&to=" + toLang;
            JSONArray jSONArray = new JSONArray();
            Iterator<String> it = texts.iterator();
            while (it.hasNext()) {
                jSONArray.put(new JSONObject().put("Text", it.next()));
            }
            return new Request.Builder().url("https://".concat(str)).header("X-Mt-Signature", signature(str)).header("User-Agent", "okhttp/4.5.0").post(RequestBody.create(MediaType.parse("application/json; charset=UTF-8"), jSONArray.toString())).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final String signature(String path) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        String strReplace$default = UUID.randomUUID().toString().replace("-", "");
        Locale locale = Locale.US;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", locale);
        TimeZone timeZone = GMT;
        simpleDateFormat.setTimeZone(timeZone);
        String str = simpleDateFormat.format(new Date(Calendar.getInstance(timeZone).getTimeInMillis())).toLowerCase(locale) + "GMT";
        String lowerCase = ("MSTranslatorAndroidApp" + URLEncoder.encode(path, "UTF-8") + str + strReplace$default).toLowerCase(locale);
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(Base64.decode("oik6PdDdMnOXemTbwvMn9de/h9lFnfBaCWbGMMZqqoSaQaqUOqjVGm5NqsmjcBI1x+sS9ugjB55HEJWRiFXYFw", 0), "HmacSHA256"));
        return "MSTranslatorAndroidApp::" + Base64.encodeToString(mac.doFinal(lowerCase.getBytes(StandardCharsets.UTF_8)), 2) + "::" + str + "::" + strReplace$default;
    }

    @Override // com.exteragram.messenger.translator.core.HttpTranslator
    public ProviderResponse parseResponse(Response response, int expected) {
        try {
            String strString = response.body().string();
            if (strString.length() == 0) {
                return new ProviderResponse.Failure(response.isSuccessful() ? TranslationError.Transient.INSTANCE : httpError(response));
            }
            if (!strString.startsWith("[")) {
                return new ProviderResponse.Failure(failure(response, strString));
            }
            JSONArray jSONArray = new JSONArray(strString);
            if (jSONArray.length() != expected) {
                return new ProviderResponse.Failure(TranslationError.Transient.INSTANCE);
            }
            int length = jSONArray.length();
            ArrayList arrayList = new ArrayList(length);
            for (int i = 0; i < length; i++) {
                arrayList.add(jSONArray.getJSONObject(i).getJSONArray("translations").getJSONObject(0).optString("text"));
            }
            return new ProviderResponse.Success(arrayList);
        } catch (Exception e) {
            return new ProviderResponse.Failure(TranslationError.Transient.INSTANCE);
        }
    }

    private final TranslationError failure(Response response, String body) {
        int iOptInt = 0;
        try {
            JSONObject jSONObjectOptJSONObject = new JSONObject(body).optJSONObject("error");
            if (jSONObjectOptJSONObject != null) {
                iOptInt = jSONObjectOptJSONObject.optInt("code");
            }
        } catch (Exception unused) {
        }
        if (response.code() == 429) {
            return new TranslationError.RateLimited(retryAfterMs(response));
        }
        if (iOptInt / 1000 == 401) {
            FileLog.e("translator: Microsoft rejected the signature, invalid credentials");
            return TranslationError.Fatal.INSTANCE;
        }
        int iCode = response.code();
        return (500 > iCode || iCode >= 600) ? TranslationError.Fatal.INSTANCE : TranslationError.Transient.INSTANCE;
    }

    @Metadata(d1 = {"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\u0013\u001a\u00020\u000eH\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082T¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004¢\u0006\u0002\n\u0000R\u001b\u0010\r\u001a\u00020\u000e8BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\u0011\u0010\u0012\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u0014"}, d2 = {"Lcom/exteragram/messenger/translator/providers/MicrosoftTranslator$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "SIGNING_KEY", _UrlKt.FRAGMENT_ENCODE_SET, "SCHEME", "ENDPOINT_PATH", "SIGNATURE_SCHEME", "HMAC_ALGORITHM", "USER_AGENT", "GMT", "Ljava/util/TimeZone;", "shared", "Lcom/exteragram/messenger/translator/providers/MicrosoftTranslator;", "getShared", "()Lcom/exteragram/messenger/translator/providers/MicrosoftTranslator;", "shared$delegate", "Lkotlin/Lazy;", "getInstance", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        private final MicrosoftTranslator getShared() {
            return (MicrosoftTranslator) MicrosoftTranslator.shared$delegate.getValue();
        }

        @JvmStatic
        public final MicrosoftTranslator getInstance() {
            return getShared();
        }
    }

    public static MicrosoftTranslator $r8$lambda$0dLF5QmlPQKXkZb52oac7kc5HWc() {
        return new MicrosoftTranslator();
    }
}
