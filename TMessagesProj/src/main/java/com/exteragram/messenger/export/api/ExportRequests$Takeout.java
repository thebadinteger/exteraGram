package com.exteragram.messenger.export.api;

import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.TLObject;

public class ExportRequests$Takeout extends TLObject {
    public long id;

    public static ExportRequests$Takeout TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
        ExportRequests$Takeout exportRequests$Takeout = new ExportRequests$Takeout();
        exportRequests$Takeout.readParams(inputSerializedData, z);
        return exportRequests$Takeout;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(InputSerializedData inputSerializedData, boolean z) {
        this.id = inputSerializedData.readInt64(z);
    }
}
