package com.exteragram.messenger.export.api;

import com.android.dx.dex.code.CstInsn$$ExternalSyntheticBUOutline0;
import okio.Buffer$$ExternalSyntheticBUOutline4;
import org.telegram.messenger.BuildVars;
import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_stories;

public class ExportRequests$InvokeWithTakeoutWrapper extends TLObject {
    public TLObject query;
    public long takeout_id;

    @Override // org.telegram.tgnet.TLObject
    public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
        Vector vector;
        switch (i) {
            case -1938715001:
            case -948520370:
            case 1595959062:
                return TLRPC.messages_Messages.TLdeserialize(inputSerializedData, i, z);
            case -1916114267:
            case 352657236:
                return TLRPC.photos_Photos.TLdeserialize(inputSerializedData, i, z);
            case -1720552011:
            case -1132882121:
                return TLRPC.Bool.TLdeserialize(inputSerializedData, i, z);
            case -1663561404:
            case 1694474197:
                return TLRPC.messages_Chats.TLdeserialize(inputSerializedData, i, z);
            case -1255369827:
            case -567906571:
            case 1891070632:
                return TLRPC.contacts_TopPeers.TLdeserialize(inputSerializedData, i, z);
            case TL_account.webAuthorizations.constructor :
                return TL_account.authorizations.TLdeserialize(inputSerializedData, i, z);
            case TL_stories.TL_stories_stories.constructor /* 1673780490 */:
                return TL_stories.TL_stories_stories.TLdeserialize(inputSerializedData, i, z);
            default:
                Buffer$$ExternalSyntheticBUOutline4.m("cannot deserialize response with constructor: 0x", Integer.toHexString(i));
                return null;
        }
    }

    @Override // org.telegram.tgnet.TLObject
    public void serializeToStream(OutputSerializedData outputSerializedData) {
        outputSerializedData.writeInt32(-1398145746);
        outputSerializedData.writeInt64(this.takeout_id);
        TLObject tLObject = this.query;
        if ((tLObject instanceof ExportRequests$TL_contacts_getSaved) || (tLObject instanceof ExportRequests$getSplitRanges) || (tLObject instanceof TLRPC.TL_messages_getCustomEmojiDocuments)) {
            tLObject.disableFree = true;
        }
        tLObject.serializeToStream(outputSerializedData);
    }
}
