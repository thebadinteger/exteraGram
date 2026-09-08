package com.exteragram.messenger.export;

import com.exteragram.messenger.export.output.AbstractWriter;
import org.telegram.tgnet.TLRPC;

public class ExportSettings {
    public String path;
    public TLRPC.InputPeer singlePeer = new TLRPC.TL_inputPeerEmpty();
    public AbstractWriter.Format format = AbstractWriter.Format.Json;
    public int singlePeerFrom = 0;
    public int singlePeerTill = 0;
    public MediaSettings media = new MediaSettings();
    public int types = 32;

    public boolean onlySinglePeer() {
        return !(this.singlePeer instanceof TLRPC.TL_inputPeerEmpty);
    }

    public static class MediaSettings {
        public int type = 1;
        public long sizeLimit = 8388608;

        public boolean isEnabled() {
            int i = this.type;
            return ((i & 1) == 0 && (i & 2) == 0 && (i & 4) == 0 && (i & 8) == 0 && (i & 16) == 0 && (i & 32) == 0 && (i & 64) == 0) ? false : true;
        }
    }
}
