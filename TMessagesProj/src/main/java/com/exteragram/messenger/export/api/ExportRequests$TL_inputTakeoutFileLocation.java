package com.exteragram.messenger.export.api;

import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.TLRPC;

public class ExportRequests$TL_inputTakeoutFileLocation extends TLRPC.InputFileLocation {
    public static final int constructor = 0x29be5899;

    @Override
    public void serializeToStream(OutputSerializedData stream) {
        stream.writeInt32(constructor);
    }
}
