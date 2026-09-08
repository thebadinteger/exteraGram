package com.exteragram.messenger.export.api;

import org.mvel2.math.MathProcessor$$ExternalSyntheticBUOutline0;
import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;

public class ExportRequests$InvokeWithMessagesRange extends TLObject {
    public TLObject query;
    public TLRPC.TL_messageRange range;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
        TLObject tL_messages_dialogs;
        if (i == 364538944) {
            tL_messages_dialogs = new TLRPC.TL_messages_dialogs();
        } else if (i == 1143203525) {
            tL_messages_dialogs = new TLRPC.TL_messages_getHistory();
        } else if (i == 1910543603) {
            tL_messages_dialogs = new TLRPC.TL_messages_dialogsSlice();
        } else {
            MathProcessor$$ExternalSyntheticBUOutline0.m("unknown constructor: ", i);
            return null;
        }
        tL_messages_dialogs.readParams(inputSerializedData, z);
        return tL_messages_dialogs;
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(OutputSerializedData outputSerializedData) {
        outputSerializedData.writeInt32(911373810);
        this.range.serializeToStream(outputSerializedData);
        this.query.serializeToStream(outputSerializedData);
    }
}
