package org.vosk;

import com.sun.jna.PointerType;
import java.io.IOException;

/* JADX INFO: loaded from: C:\Users\badinteger\Files\.mess\extera\dex_files\classes7.dex */
public class Recognizer extends PointerType implements AutoCloseable {
    public Recognizer(Model model, float f) throws IOException {
        super(LibVosk.vosk_recognizer_new(model, f));
        if (getPointer() != null) {
            return;
        }
        Model$$ExternalSyntheticBUOutline0.m("Failed to create a recognizer");
        throw null;
    }

    public Recognizer(Model model, float f, SpeakerModel speakerModel) throws IOException {
        super(LibVosk.vosk_recognizer_new_spk(model.getPointer(), f, speakerModel.getPointer()));
        if (getPointer() != null) {
            return;
        }
        Model$$ExternalSyntheticBUOutline0.m("Failed to create a recognizer");
        throw null;
    }

    public Recognizer(Model model, float f, String str) throws IOException {
        super(LibVosk.vosk_recognizer_new_grm(model.getPointer(), f, str));
        if (getPointer() != null) {
            return;
        }
        Model$$ExternalSyntheticBUOutline0.m("Failed to create a recognizer");
        throw null;
    }

    public void setMaxAlternatives(int i) {
        LibVosk.vosk_recognizer_set_max_alternatives(getPointer(), i);
    }

    public void setWords(boolean z) {
        LibVosk.vosk_recognizer_set_words(getPointer(), z);
    }

    public void setPartialWords(boolean z) {
        LibVosk.vosk_recognizer_set_partial_words(getPointer(), z);
    }

    public void setSpeakerModel(SpeakerModel speakerModel) {
        LibVosk.vosk_recognizer_set_spk_model(getPointer(), speakerModel.getPointer());
    }

    public boolean acceptWaveForm(byte[] bArr, int i) {
        return LibVosk.vosk_recognizer_accept_waveform(getPointer(), bArr, i);
    }

    public boolean acceptWaveForm(short[] sArr, int i) {
        return LibVosk.vosk_recognizer_accept_waveform_s(getPointer(), sArr, i);
    }

    public boolean acceptWaveForm(float[] fArr, int i) {
        return LibVosk.vosk_recognizer_accept_waveform_f(getPointer(), fArr, i);
    }

    public String getResult() {
        return LibVosk.vosk_recognizer_result(getPointer());
    }

    public String getPartialResult() {
        return LibVosk.vosk_recognizer_partial_result(getPointer());
    }

    public String getFinalResult() {
        return LibVosk.vosk_recognizer_final_result(getPointer());
    }

    public void setGrammar(String str) {
        LibVosk.vosk_recognizer_set_grm(getPointer(), str);
    }

    public void reset() {
        LibVosk.vosk_recognizer_reset(getPointer());
    }

    public void setEndpointerMode(int i) {
        LibVosk.vosk_recognizer_set_endpointer_mode(getPointer(), i);
    }

    public void setEndpointerDelays(float f, float f2, float f3) {
        LibVosk.vosk_recognizer_set_endpointer_delays(getPointer(), f, f2, f3);
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        LibVosk.vosk_recognizer_free(getPointer());
    }
}
