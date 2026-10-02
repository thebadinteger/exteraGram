package com.exteragram.messenger.translator.providers;

import com.exteragram.messenger.plugins.PluginsConstants;
import com.exteragram.messenger.translator.TranslatorUtils;
import com.exteragram.messenger.translator.core.HttpTranslator;
import com.exteragram.messenger.translator.core.ProviderLimits;
import com.exteragram.messenger.translator.core.ProviderResponse;
import com.exteragram.messenger.translator.core.TranslationError;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.HttpUrl;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.url._UrlKt;
import org.json.JSONArray;
import org.json.JSONException;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000F\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\u0018\u0000 \u001c2\u00020\u0001:\u0001\u001cB\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\u0010\u001a\u00020\u00112\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u00132\u0006\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u0015\u001a\u00020\u0005H\u0016J\u0018\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001bH\u0016R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096D¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001a\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\tX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0014\u0010\f\u001a\u00020\rX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001d"}, d2 = {"Lcom/exteragram/messenger/translator/providers/GoogleTranslator;", "Lcom/exteragram/messenger/translator/core/HttpTranslator;", "<init>", "()V", "displayName", _UrlKt.FRAGMENT_ENCODE_SET, "getDisplayName", "()Ljava/lang/String;", "supportedLanguages", _UrlKt.FRAGMENT_ENCODE_SET, "getSupportedLanguages", "()Ljava/util/Set;", "limits", "Lcom/exteragram/messenger/translator/core/ProviderLimits;", "getLimits", "()Lcom/exteragram/messenger/translator/core/ProviderLimits;", "buildRequest", "Lokhttp3/Request;", "texts", _UrlKt.FRAGMENT_ENCODE_SET, "fromLang", "toLang", "parseResponse", "Lcom/exteragram/messenger/translator/core/ProviderResponse;", PluginsConstants.RESPONSE, "Lokhttp3/Response;", "expected", _UrlKt.FRAGMENT_ENCODE_SET, "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nGoogleTranslator.kt\nKotlin\n*S Kotlin\n*F\n+ 1 GoogleTranslator.kt\ncom/exteragram/messenger/translator/providers/GoogleTranslator\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,86:1\n1#2:87\n1915#3,2:88\n*S KotlinDebug\n*F\n+ 1 GoogleTranslator.kt\ncom/exteragram/messenger/translator/providers/GoogleTranslator\n*L\n46#1:88,2\n*E\n"})
public final class GoogleTranslator extends HttpTranslator {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final Lazy<GoogleTranslator> shared$delegate = LazyKt.lazy(new Function0() { // from class: com.exteragram.messenger.translator.providers.GoogleTranslator$$ExternalSyntheticLambda0
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return GoogleTranslator.$r8$lambda$JuUypjeoHqJDOhD4G8G0FdvCICQ();
        }
    });
    private final String displayName = "Google";
    private final Set<String> supportedLanguages = SetsKt.emptySet();
    private final ProviderLimits limits = ProviderLimits.GOOGLE;

    @JvmStatic
    public static final GoogleTranslator getInstance() {
        return INSTANCE.getInstance();
    }

    private GoogleTranslator() {
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
        HttpUrl.Builder builderAddQueryParameter = new HttpUrl.Builder().scheme("https").host("translate.googleapis.com").addPathSegments("translate_a/t").addQueryParameter("client", "gtx").addQueryParameter("sl", fromLang).addQueryParameter("tl", toLang).addQueryParameter("dt", "t").addQueryParameter("format", "text").addQueryParameter("ie", "UTF-8").addQueryParameter("oe", "UTF-8");
        Iterator<String> it = texts.iterator();
        while (it.hasNext()) {
            builderAddQueryParameter.addQueryParameter("q", (String) it.next());
        }
        return new Request.Builder().url(builderAddQueryParameter.build()).header("User-Agent", TranslatorUtils.formatUserAgent()).build();
    }

    @Override // com.exteragram.messenger.translator.core.HttpTranslator
    public ProviderResponse parseResponse(Response response, int expected) {
        try {
        if (!response.isSuccessful()) {
            return new ProviderResponse.Failure(httpError(response));
        }
        String strString = response.body().string();
        if (strString.length() == 0) {
            return new ProviderResponse.Failure(TranslationError.Transient.INSTANCE);
        }
        JSONArray jSONArray = new JSONArray(strString);
        ArrayList arrayList = new ArrayList(expected);
        int length = jSONArray.length();
        for (int i = 0; i < length; i++) {
            Object obj = jSONArray.get(i);
            arrayList.add(obj instanceof JSONArray ? ((JSONArray) obj).optString(0) : obj.toString());
        }
        if (arrayList.size() != expected) {
            return new ProviderResponse.Failure(TranslationError.Transient.INSTANCE);
        }
        return new ProviderResponse.Success(arrayList);
        } catch (Exception e) {
            return new ProviderResponse.Failure(TranslationError.Transient.INSTANCE);
        }
    }

    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\n\u001a\u00020\u0005H\u0007R\u001b\u0010\u0004\u001a\u00020\u00058BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u000b"}, d2 = {"Lcom/exteragram/messenger/translator/providers/GoogleTranslator$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "shared", "Lcom/exteragram/messenger/translator/providers/GoogleTranslator;", "getShared", "()Lcom/exteragram/messenger/translator/providers/GoogleTranslator;", "shared$delegate", "Lkotlin/Lazy;", "getInstance", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        private final GoogleTranslator getShared() {
            return (GoogleTranslator) GoogleTranslator.shared$delegate.getValue();
        }

        @JvmStatic
        public final GoogleTranslator getInstance() {
            return getShared();
        }
    }

    public static GoogleTranslator $r8$lambda$JuUypjeoHqJDOhD4G8G0FdvCICQ() {
        return new GoogleTranslator();
    }
}
