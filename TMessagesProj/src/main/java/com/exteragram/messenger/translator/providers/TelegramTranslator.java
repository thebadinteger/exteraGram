package com.exteragram.messenger.translator.providers;

import com.exteragram.messenger.translator.TranslatorUtils;
import com.exteragram.messenger.translator.core.BaseTranslator;
import com.sun.jna.Callback;
import java.util.Set;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.collections.SetsKt;
import kotlin.jvm.JvmStatic;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u0000 \u00132\u00020\u0001:\u0001\u0013B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J(\u0010\f\u001a\u00020\r2\u0006\u0010\u000e\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u00052\u0006\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u0012H\u0016R\u0014\u0010\u0004\u001a\u00020\u0005X\u0096D¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u001a\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00050\tX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0014"}, d2 = {"Lcom/exteragram/messenger/translator/providers/TelegramTranslator;", "Lcom/exteragram/messenger/translator/core/BaseTranslator;", "<init>", "()V", "displayName", _UrlKt.FRAGMENT_ENCODE_SET, "getDisplayName", "()Ljava/lang/String;", "supportedLanguages", _UrlKt.FRAGMENT_ENCODE_SET, "getSupportedLanguages", "()Ljava/util/Set;", "translate", _UrlKt.FRAGMENT_ENCODE_SET, "text", "fromLang", "toLang", Callback.METHOD_NAME, "Lcom/exteragram/messenger/translator/TranslatorUtils$TranslateCallback;", "Companion", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final class TelegramTranslator extends BaseTranslator {

    /* JADX INFO: renamed from: Companion, reason: from kotlin metadata */
    public static final Companion INSTANCE = new Companion(null);
    private static final Lazy<TelegramTranslator> shared$delegate = LazyKt.lazy(new Function0() { // from class: com.exteragram.messenger.translator.providers.TelegramTranslator$$ExternalSyntheticLambda0
        @Override // kotlin.jvm.functions.Function0
        public final Object invoke() {
            return TelegramTranslator.$r8$lambda$fbGMfCcLdrZLip2b47N0ENHsC9I();
        }
    });
    private final String displayName = "Telegram";
    private final Set<String> supportedLanguages = SetsKt.emptySet();

    @JvmStatic
    public static final TelegramTranslator getInstance() {
        return INSTANCE.getInstance();
    }

    private TelegramTranslator() {
    }

    @Override // com.exteragram.messenger.translator.core.BaseTranslator
    public String getDisplayName() {
        return this.displayName;
    }

    @Override // com.exteragram.messenger.translator.core.BaseTranslator
    public Set<String> getSupportedLanguages() {
        return this.supportedLanguages;
    }

    @Override // com.exteragram.messenger.translator.core.BaseTranslator
    public void translate(String text, String fromLang, String toLang, TranslatorUtils.TranslateCallback callback) {
        TranslatorUtils.translateWithDefault(text, null, 0, toLang, null, callback);
    }

    @Metadata(d1 = {"\u0000\u0014\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\b\u0010\n\u001a\u00020\u0005H\u0007R\u001b\u0010\u0004\u001a\u00020\u00058BX\u0082\u0084\u0002¢\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u000b"}, d2 = {"Lcom/exteragram/messenger/translator/providers/TelegramTranslator$Companion;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "shared", "Lcom/exteragram/messenger/translator/providers/TelegramTranslator;", "getShared", "()Lcom/exteragram/messenger/translator/providers/TelegramTranslator;", "shared$delegate", "Lkotlin/Lazy;", "getInstance", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Companion {
        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        private Companion() {
        }

        private final TelegramTranslator getShared() {
            return (TelegramTranslator) TelegramTranslator.shared$delegate.getValue();
        }

        @JvmStatic
        public final TelegramTranslator getInstance() {
            return getShared();
        }
    }

    public static TelegramTranslator $r8$lambda$fbGMfCcLdrZLip2b47N0ENHsC9I() {
        return new TelegramTranslator();
    }
}
