package org.vosk;

import com.sun.jna.PointerType;
import java.io.IOException;

/* JADX INFO: loaded from: C:\Users\badinteger\Files\.mess\extera\dex_files\classes7.dex */
public class SpeakerModel extends PointerType implements AutoCloseable {
    public SpeakerModel() {
    }

    public SpeakerModel(String str) throws IOException {
        super(LibVosk.vosk_spk_model_new(str));
        if (getPointer() != null) {
            return;
        }
        Model$$ExternalSyntheticBUOutline0.m("Failed to create a speaker model");
        throw null;
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        LibVosk.vosk_spk_model_free(getPointer());
    }
}
