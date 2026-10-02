package com.exteragram.messenger.ai.network.backend;

/* JADX INFO: loaded from: classes4.dex */
public interface Backend {
    void execute(BackendRequest backendRequest, BackendSink backendSink);

    int getHistoryCharsLimit();

    int getHistoryMessagesLimit();

    int getMaxOutputTokens();

    float getMaxTemperature();

    boolean supportsImages();
}
