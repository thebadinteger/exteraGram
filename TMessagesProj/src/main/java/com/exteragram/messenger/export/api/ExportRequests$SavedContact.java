package com.exteragram.messenger.export.api;

import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.TLObject;

public class ExportRequests$SavedContact extends TLObject {
    public int date;
    public String first_name;
    public String last_name;
    public String phone;

    public static ExportRequests$SavedContact TLdeserialize(InputSerializedData inputSerializedData, int i, boolean z) {
        ExportRequests$SavedContact exportRequests$SavedContact = new ExportRequests$SavedContact();
        exportRequests$SavedContact.readParams(inputSerializedData, z);
        return exportRequests$SavedContact;
    }

    @Override // org.telegram.tgnet.TLObject
    public void readParams(InputSerializedData inputSerializedData, boolean z) {
        this.phone = inputSerializedData.readString(z);
        this.first_name = inputSerializedData.readString(z);
        this.last_name = inputSerializedData.readString(z);
        this.date = inputSerializedData.readInt32(z);
    }
}
