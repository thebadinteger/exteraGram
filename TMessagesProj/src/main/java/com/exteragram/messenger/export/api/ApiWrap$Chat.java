package com.exteragram.messenger.export.api;

import okhttp3.internal.url._UrlKt;
import org.telegram.tgnet.TLRPC;

public class ApiWrap$Chat {
    public boolean hasMonoforumAdminRights;
    public boolean isMonoforumAdmin;
    public boolean isMonoforumOfPublicBroadcast;
    public long monoforumLinkId;
    public long bareId = 0;
    public long migratedToChannelId = 0;
    public String title = _UrlKt.FRAGMENT_ENCODE_SET;
    public String username = _UrlKt.FRAGMENT_ENCODE_SET;
    public int colorIndex = 0;
    public boolean isBroadcast = false;
    public boolean isSupergroup = false;
    public boolean isMonoforum = false;
    public TLRPC.InputPeer input = new TLRPC.TL_inputPeerEmpty();
    public TLRPC.InputPeer monoforumBroadcastInput = new TLRPC.TL_inputPeerEmpty();
}
