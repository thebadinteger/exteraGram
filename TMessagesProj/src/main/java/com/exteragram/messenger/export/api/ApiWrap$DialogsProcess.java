package com.exteragram.messenger.export.api;

import org.telegram.tgnet.TLRPC;

public class ApiWrap$DialogsProcess extends ApiWrap$ChatsProcess {
    public int splitIndexPlusOne = 0;
    public int offsetDate = 0;
    public int offsetId = 0;
    public TLRPC.InputPeer offsetPeer = new TLRPC.TL_inputPeerEmpty();
}
