package com.exteragram.messenger.export.api;

import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class ExportRequests$FinishTakeoutSession extends TLObject {
    public static int constructor = 489050862;
    public int flags;
    public boolean success;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
        return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(OutputSerializedData outputSerializedData) {
        outputSerializedData.writeInt32(constructor);
        boolean z = this.success;
        int i = this.flags;
        int i2 = z ? i | 1 : i & (-2);
        this.flags = i2;
        outputSerializedData.writeInt32(i2);
    }
}
