package com.exteragram.messenger.translators;

import com.exteragram.messenger.utils.text.TranslatorUtils;
import java.util.Collections;
import java.util.Set;

public class TelegramTranslator extends BaseTranslator {
    private static TelegramTranslator instance;

    public static TelegramTranslator getInstance() {
        if (instance == null) {
            instance = new TelegramTranslator();
        }
        return instance;
    }

    @Override 
    public String getDisplayName() {
        return "Telegram";
    }

    @Override 
    public Set<String> getSupportedLanguages() {
        return Collections.EMPTY_SET;
    }

    @Override 
    public void translate(String str, String str2, String str3, TranslatorUtils.TranslateCallback translateCallback) {
        TranslatorUtils.translateWithDefault(str, null, 0, str3, null, translateCallback);
    }
}
