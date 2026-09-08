package com.exteragram.messenger.export.api;

import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class ExportRequests$getLeftChannels extends TLObject {
    public static int constructor = -2092831552;
    public int offset;

    @Override // org.telegram.tgnet.TLObject
    public TLRPC.messages_Chats deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
        return TLRPC.messages_Chats.TLdeserialize(inputSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(OutputSerializedData outputSerializedData) {
        outputSerializedData.writeInt32(constructor);
        outputSerializedData.writeInt32(this.offset);
    }
}
