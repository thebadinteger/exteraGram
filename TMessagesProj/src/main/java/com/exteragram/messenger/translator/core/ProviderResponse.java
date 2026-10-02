package com.exteragram.messenger.translator.core;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import okhttp3.internal.url._UrlKt;

/* JADX INFO: loaded from: classes4.dex */
@Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b6\u0018\u00002\u00020\u0001:\u0002\u0004\u0005B\t\b\u0004¢\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0002\u0006\u0007¨\u0006\b"}, d2 = {"Lcom/exteragram/messenger/translator/core/ProviderResponse;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "Success", "Failure", "Lcom/exteragram/messenger/translator/core/ProviderResponse$Failure;", "Lcom/exteragram/messenger/translator/core/ProviderResponse$Success;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public abstract class ProviderResponse {
    public /* synthetic */ ProviderResponse(DefaultConstructorMarker defaultConstructorMarker) {
        this();
    }

    private ProviderResponse() {
    }

    @Metadata(d1 = {"\u0000\u0016\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0010\u000e\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u0015\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\u0004\b\u0005\u0010\u0006R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\b¨\u0006\t"}, d2 = {"Lcom/exteragram/messenger/translator/core/ProviderResponse$Success;", "Lcom/exteragram/messenger/translator/core/ProviderResponse;", "texts", _UrlKt.FRAGMENT_ENCODE_SET, _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/util/List;)V", "getTexts", "()Ljava/util/List;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Success extends ProviderResponse {
        private final List<String> texts;

        public Success(List<String> list) {
            super(null);
            this.texts = list;
        }

        public final List<String> getTexts() {
            return this.texts;
        }
    }

    @Metadata(d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\b"}, d2 = {"Lcom/exteragram/messenger/translator/core/ProviderResponse$Failure;", "Lcom/exteragram/messenger/translator/core/ProviderResponse;", "error", "Lcom/exteragram/messenger/translator/core/TranslationError;", "<init>", "(Lcom/exteragram/messenger/translator/core/TranslationError;)V", "getError", "()Lcom/exteragram/messenger/translator/core/TranslationError;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
    public static final class Failure extends ProviderResponse {
        private final TranslationError error;

        public Failure(TranslationError translationError) {
            super(null);
            this.error = translationError;
        }

        public final TranslationError getError() {
            return this.error;
        }
    }
}
