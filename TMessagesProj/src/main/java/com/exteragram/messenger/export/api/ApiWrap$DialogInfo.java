package com.exteragram.messenger.export.api;

import java.util.ArrayList;
import org.telegram.tgnet.TLRPC;

public class ApiWrap$DialogInfo {
    public String lastName;
    public String name;
    public String relativePath;
    public Type type = Type.Unknown;
    public TLRPC.InputPeer input = new TLRPC.TL_inputPeerEmpty();
    public int topMessageId = 0;
    public int topMessageDate = -1337;
    public long peerId = 0;
    public int colorIndex = 0;
    public TLRPC.InputPeer migratedFromInput = new TLRPC.TL_inputPeerEmpty();
    public long migratedToChannelId = 0;
    public TLRPC.InputPeer monoforumBroadcastInput = new TLRPC.TL_inputPeerEmpty();
    public ArrayList<Integer> splits = new ArrayList<>();
    public boolean onlyMyMessages = false;
    public boolean isLeftChannel = false;
    public boolean isMonoforum = false;
    public ArrayList<Integer> messagesCountPerSplit = new ArrayList<>();

    public enum Type {
        Unknown,
        Self,
        Replies,
        VerifyCodes,
        Personal,
        Bot,
        PrivateGroup,
        PrivateSupergroup,
        PublicSupergroup,
        PrivateChannel,
        PublicChannel
    }
}
