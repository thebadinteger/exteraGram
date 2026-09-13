package com.exteragram.messenger.translators;

import com.exteragram.messenger.utils.text.TranslatorUtils;

public final class BaseTranslator$$ExternalSyntheticLambda0 implements Runnable {
    public final TranslatorUtils.TranslateCallback f$0;

    public BaseTranslator$$ExternalSyntheticLambda0(TranslatorUtils.TranslateCallback callback) {
        this.f$0 = callback;
    }

    @Override
    public final void run() {
        if (this.f$0 != null) {
            this.f$0.onFailed();
        }
    }
}
