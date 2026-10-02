package com.exteragram.messenger.translator;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ChatTranslator$cache$1 extends LinkedHashMap<String, String> {
    public ChatTranslator$cache$1() {
        super(64, 0.75f, true);
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, String> eldest) {
        return size() > 300;
    }
}
