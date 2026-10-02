package com.exteragram.messenger.translator.core;

import com.exteragram.messenger.plugins.PluginsConstants;
import com.exteragram.messenger.translator.TranslatorUtils;
import com.exteragram.messenger.utils.network.ExteraHttpClient;
import com.sun.jna.Callback;
import java.util.List;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.text.StringsKt;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.url._UrlKt;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\\\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\b&\u0018\u0000 \"2\u00020\u0001:\u0001\"B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J&\u0010\f\u001a\u00020\r2\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u0010H&J\u0018\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u0018H&J&\u0010\u0019\u001a\u00020\u001a2\u0006\u0010\u001b\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00102\u0006\u0010\u0012\u001a\u00020\u00102\u0006\u0010\u001c\u001a\u00020\u001dJ\u0010\u0010\u001e\u001a\u00020\u001f2\u0006\u0010\u0015\u001a\u00020\u0016H\u0004J\u0010\u0010 \u001a\u00020!2\u0006\u0010\u0015\u001a\u00020\u0016H\u0004R\u0012\u0010\u0004\u001a\u00020\u0005X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\t8VX\u0096\u0004¢\u0006\u0006\u001a\u0004\b\n\u0010\u000b¨\u0006#"}, d2 = {"Lcom/exteragram/messenger/translator/core/HttpTranslator;", "Lcom/exteragram/messenger/translator/core/BaseTranslator;", "<init>", "()V", "limits", "Lcom/exteragram/messenger/translator/core/ProviderLimits;", "getLimits", "()Lcom/exteragram/messenger/translator/core/ProviderLimits;", "client", "Lokhttp3/OkHttpClient;", "getClient", "()Lokhttp3/OkHttpClient;", "buildRequest", "Lokhttp3/Request;", "texts", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "fromLang", "toLang", "parseResponse", "Lcom/exteragram/messenger/translator/core/ProviderResponse;", PluginsConstants.RESPONSE, "Lokhttp3/Response;", "expected", _UrlKt.FRAGMENT_ENCODE_SET, "translate", _UrlKt.FRAGMENT_ENCODE_SET, "text", Callback.METHOD_NAME, "Lcom/exteragram/messenger/translator/TranslatorUtils$TranslateCallback;", "httpError", "Lcom/exteragram/messenger/translator/core/TranslationError;", "retryAfterMs", _UrlKt.FRAGMENT_ENCODE_SET, "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public abstract class HttpTranslator extends BaseTranslator {
    public abstract Request buildRequest(List<String> texts, String fromLang, String toLang);

    public abstract ProviderLimits getLimits();

    public abstract ProviderResponse parseResponse(Response response, int expected);

    public OkHttpClient getClient() {
        return ExteraHttpClient.INSTANCE.getClient();
    }

    @Override // com.exteragram.messenger.translator.core.BaseTranslator
    public final void translate(String text, String fromLang, String toLang, final TranslatorUtils.TranslateCallback callback) {
        if (StringsKt.isBlank(text)) {
            callback.onFailed(TranslationError.Fatal.INSTANCE);
        } else {
            TranslationDispatcher.enqueue(this, 0L, CollectionsKt.listOf(text), fromLang, toLang, true, new TranslationDispatcher.Completion() { // from class: com.exteragram.messenger.translator.core.HttpTranslator$$ExternalSyntheticLambda0
                @Override // com.exteragram.messenger.translator.core.TranslationDispatcher.Completion
                public final void onDone(List list, TranslationError translationError) {
                    HttpTranslator.$r8$lambda$jSPAM0EKvxpcBjvYueCfZMWI2q4(callback, list, translationError);
                }
            });
        }
    }

    public static void $r8$lambda$jSPAM0EKvxpcBjvYueCfZMWI2q4(TranslatorUtils.TranslateCallback translateCallback, List list, TranslationError translationError) {
        String str = list != null ? (String) CollectionsKt.firstOrNull(list) : null;
        if (str != null && str.length() != 0) {
            translateCallback.onSuccess(str);
            return;
        }
        if (translationError == null) {
            translationError = TranslationError.Fatal.INSTANCE;
        }
        translateCallback.onFailed(translationError);
    }

    public final TranslationError httpError(Response response) {
        if (response.code() == 429) {
            return new TranslationError.RateLimited(retryAfterMs(response));
        }
        int iCode = response.code();
        return (500 > iCode || iCode >= 600) ? TranslationError.Fatal.INSTANCE : TranslationError.Transient.INSTANCE;
    }

    public final long retryAfterMs(Response response) {
        String string;
        String strHeader$default = response.header("Retry-After");
        if (strHeader$default == null || (string = StringsKt.trim((CharSequence) strHeader$default).toString()) == null) {
            return 0L;
        }
        Long longOrNull = StringsKt.toLongOrNull(string);
        return (longOrNull != null ? longOrNull.longValue() : 0L) * 1000;
    }
}
