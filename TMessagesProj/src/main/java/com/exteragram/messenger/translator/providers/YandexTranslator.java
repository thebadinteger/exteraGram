package com.exteragram.messenger.translator.providers;

import com.exteragram.messenger.plugins.PluginsConstants;
import com.exteragram.messenger.translator.core.HttpTranslator;
import com.exteragram.messenger.translator.core.ProviderLimits;
import com.exteragram.messenger.translator.core.ProviderResponse;
import com.exteragram.messenger.translator.core.TranslationError;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import kotlin.text.StringsKt;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.url._UrlKt;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.telegram.messenger.ImageLoader;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u00132\u0006\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0015\u001a\u00020\u0005H\u0016J\u0018\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0016R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096D¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001a\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\tX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\f\u001a\u00020\rX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001d"}, d2 = {"Lcom/exteragram/messenger/translator/providers/YandexTranslator;", "Lcom/exteragram/messenger/translator/core/HttpTranslator;", "<init>", "()V", "displayName", _UrlKt.FRAGMENT_ENCODE_SET, "getDisplayName", "()Ljava/lang/String;", "supportedLanguages", _UrlKt.FRAGMENT_ENCODE_SET, "getSupportedLanguages", "()Ljava/util/Set;", "limits", "Lcom/exteragram/messenger/translator/core/ProviderLimits;", "getLimits", "()Lcom/exteragram/messenger/translator/core/ProviderLimits;", "buildRequest", "Lokhttp3/Request;", "texts", _UrlKt.FRAGMENT_ENCODE_SET, "fromLang", "toLang", "parseResponse", "Lcom/exteragram/messenger/translator/core/ProviderResponse;", PluginsConstants.RESPONSE, "Lokhttp3/Response;", "expected", _UrlKt.FRAGMENT_ENCODE_SET, "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nYandexTranslator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 YandexTranslator.kt\ncom/exteragram/messenger/translator/providers/YandexTranslator\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,96:1\n1#2:97\n1915#3,2:98\n*S KotlinDebug\n*F\n+ 1 YandexTranslator.kt\ncom/exteragram/messenger/translator/providers/YandexTranslator\n*L\n49#1:98,2\n*E\n"})
public final class YandexTranslator extends HttpTranslator {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final String uuid = UUID.randomUUID().toString().replace("-", "");
    private static final Lazy<YandexTranslator> shared$delegate = LazyKt.lazy(new Function0() { // from class: com.exteragram.messenger.translator.providers.YandexTranslator$$ExternalSyntheticLambda0
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return YandexTranslator.$r8$lambda$3sgxO5d4QZKCu2_NHVQbGLYUvZA();
        }
    });
    private final String displayName = "Yandex";
    private final Set<String> supportedLanguages = SetsKt.setOf(new String[]{"az", "sq", "am", "en", "ar", "hy", "af", "eu", "ba", "be", "bn", "my", "bg", "bs", "cv", "cy", "hu", "vi", "ht", ImageLoader.AUTOPLAY_FILTER_NONLOOP, "nl", "mrj", "el", "ka", "gu", "da", "he", "yi", "id", "ga", "it", "is", "es", "kk", "kn", "ca", "ky", "zh", "ko", "xh", "km", "lo", "la", "lv", "lt", "lb", "mg", "ms", "ml", "mt", "mk", "mi", "mr", "mhr", "mn", "de", "ne", "no", "pa", "pap", "fa", "pl", "pt", "ro", "ru", "ceb", "sr", "si", "sk", "sl", "sw", "su", "tg", "th", "tl", "ta", "tt", "te", "tr", "udm", "uz", "uk", "ur", "fi", "fr", "hi", "hr", "cs", "sv", "gd", "et", "eo", "jv", "ja"});
    private final ProviderLimits limits = ProviderLimits.YANDEX;

    private YandexTranslator() {
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
        FormBody.Builder builderAdd = new FormBody.Builder().add("lang", toLang);
        Iterator<String> it = texts.iterator();
        while (it.hasNext()) {
            builderAdd.add("text", (String) it.next());
        }
        return new Request.Builder().url("https://translate.yandex.net/api/v1/tr.json/translate?&srv=android&id=" + uuid + "-0-0").header("User-Agent", "ru.yandex.translate/21.15.4.21402814 (Xiaomi Redmi K20 Pro; Android 11)").post(builderAdd.build()).build();
    }

    @Override // com.exteragram.messenger.translator.core.HttpTranslator
    public ProviderResponse parseResponse(Response response, int expected) {
        try {
        TranslationError rateLimited;
        String strString = response.body().string();
        if (strString.length() == 0) {
            return new ProviderResponse.Failure(response.isSuccessful() ? TranslationError.Transient.INSTANCE : httpError(response));
        }
        JSONObject jSONObject = new JSONObject(strString);
        int iOptInt = jSONObject.optInt("code", response.isSuccessful() ? 200 : response.code());
        if (iOptInt != 200 || !jSONObject.has("text")) {
            if (iOptInt == 429 || iOptInt == 1052 || response.code() == 429) {
                rateLimited = new TranslationError.RateLimited(retryAfterMs(response));
            } else if (iOptInt == 501) {
                rateLimited = TranslationError.LanguageUnsupported.INSTANCE;
            } else {
                int iCode = response.code();
                rateLimited = (500 > iCode || iCode >= 600) ? TranslationError.Fatal.INSTANCE : TranslationError.Transient.INSTANCE;
            }
            return new ProviderResponse.Failure(rateLimited);
        }
        JSONArray jSONArray = jSONObject.getJSONArray("text");
        if (jSONArray.length() != expected) {
            return new ProviderResponse.Failure(TranslationError.Transient.INSTANCE);
        }
        int length = jSONArray.length();
        ArrayList arrayList = new ArrayList(length);
        for (int i = 0; i < length; i++) {
            arrayList.add(jSONArray.optString(i));
        }
        return new ProviderResponse.Success(arrayList);
        } catch (Exception e) {
            return new ProviderResponse.Failure(TranslationError.Transient.INSTANCE);
        }
    }

    @Metadata(d1 = {"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\f\u001a\u00020\u0007H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000R\u001b\u0010\u0006\u001a\u00020\u00078BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\n\u0010\u000b\u001a\u0004\b\b\u0010\t¨\u0006\r"}, d2 = {"Lcom/exteragram/messenger/translator/providers/YandexTranslator$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "uuid", _UrlKt.FRAGMENT_ENCODE_SET, "shared", "Lcom/exteragram/messenger/translator/providers/YandexTranslator;", "getShared", "()Lcom/exteragram/messenger/translator/providers/YandexTranslator;", "shared$delegate", "Lkotlin/Lazy;", "getInstance", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        private final YandexTranslator getShared() {
            return (YandexTranslator) YandexTranslator.shared$delegate.getValue();
        }

        @JvmStatic
        public final YandexTranslator getInstance() {
            return getShared();
        }
    }

    public static YandexTranslator $r8$lambda$3sgxO5d4QZKCu2_NHVQbGLYUvZA() {
        return new YandexTranslator();
    }
}
