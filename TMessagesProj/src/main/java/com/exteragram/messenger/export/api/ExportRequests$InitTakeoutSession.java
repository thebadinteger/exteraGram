package com.exteragram.messenger.export.api;

import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.TLObject;

public class ExportRequests$InitTakeoutSession extends TLObject {
    public boolean contacts;
    public long file_max_size;
    public boolean files;
    public int flags;
    public boolean message_channels;
    public boolean message_chats;
    public boolean message_megagroups;
    public boolean message_users;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
        return ExportRequests$Takeout.TLdeserialize(inputSerializedData, i, z);
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(OutputSerializedData outputSerializedData) {
        outputSerializedData.writeInt32(-1896617296);
        boolean z = this.contacts;
        int i = this.flags;
        int i2 = z ? i | 1 : i & (-2);
        this.flags = i2;
        int i3 = this.message_users ? i2 | 2 : i2 & (-3);
        this.flags = i3;
        int i4 = this.message_chats ? i3 | 4 : i3 & (-5);
        this.flags = i4;
        int i5 = this.message_megagroups ? i4 | 8 : i4 & (-9);
        this.flags = i5;
        int i6 = this.message_channels ? i5 | 16 : i5 & (-17);
        this.flags = i6;
        int i7 = this.files ? i6 | 32 : i6 & (-33);
        this.flags = i7;
        outputSerializedData.writeInt32(i7);
        if (this.files) {
            outputSerializedData.writeInt64(this.file_max_size);
        }
    }
}
