package org.vosk;

/* JADX INFO: loaded from: C:\Users\badinteger\Files\.mess\extera\dex_files\classes7.dex */
public enum LogLevel {
    WARNINGS(-1),
    INFO(0),
    DEBUG(1);

    private final int value;

    LogLevel(int i) {
        this.value = i;
    }

    public int getValue() {
        return this.value;
    }
}
