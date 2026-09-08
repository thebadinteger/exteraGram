package com.exteragram.messenger.ai.network;

public interface GenerationCallback {
    void onChunk(String str);

    void onError(int i, String str);

    void onResponse(String str);

    default void onThinking() {
    }
}
