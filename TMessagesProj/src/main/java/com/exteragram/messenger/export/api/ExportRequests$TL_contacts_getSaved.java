package com.exteragram.messenger.export.api;

import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.Vector;

public class ExportRequests$TL_contacts_getSaved extends TLObject {
    public static int constructor = -0x7d0e1c61;

    @Override
    public TLObject deserializeResponse(InputSerializedData stream, int constructor, boolean exception) {
        Vector vector = new Vector(new Vector.TLDeserializer() {
            @Override
            public TLObject deserialize(InputSerializedData stream, int constructor, boolean exception) {
                return ExportRequests$SavedContact.TLdeserialize(stream, constructor, exception);
            }
        });
        vector.readParams(stream, exception);
        return vector;
    }

    @Override
    public void serializeToStream(OutputSerializedData stream) {
        stream.writeInt32(constructor);
    }
}
