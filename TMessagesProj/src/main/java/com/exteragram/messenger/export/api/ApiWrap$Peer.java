package com.exteragram.messenger.export.api;

import org.telegram.tgnet.TLRPC;

public class ApiWrap$Peer {
    public ApiWrap$Chat chat;
    public ApiWrap$User user;

    public ApiWrap$Peer(ApiWrap$User apiWrap$User) {
        this.user = apiWrap$User;
    }

    public ApiWrap$Peer(ApiWrap$Chat apiWrap$Chat) {
        this.chat = apiWrap$Chat;
    }

    public TLRPC.InputPeer getInput() {
        ApiWrap$User apiWrap$User = this.user;
        if (apiWrap$User != null) {
            TLRPC.InputUser inputUser = apiWrap$User.input;
            if (inputUser instanceof TLRPC.TL_inputUser) {
                TLRPC.TL_inputUser tL_inputUser = (TLRPC.TL_inputUser) inputUser;
                TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
                tL_inputPeerUser.user_id = tL_inputUser.user_id;
                tL_inputPeerUser.access_hash = tL_inputUser.access_hash;
                return tL_inputPeerUser;
            }
        }
        return this.chat.input;
    }

    public String name() {
        ApiWrap$User apiWrap$User = this.user;
        if (apiWrap$User != null) {
            return apiWrap$User.name();
        }
        ApiWrap$Chat apiWrap$Chat = this.chat;
        if (apiWrap$Chat != null) {
            return apiWrap$Chat.title;
        }
        throw new IllegalStateException("both user and chat are null");
    }

    public int colorIndex() {
        ApiWrap$User apiWrap$User = this.user;
        if (apiWrap$User != null) {
            return apiWrap$User.colorIndex;
        }
        ApiWrap$Chat apiWrap$Chat = this.chat;
        if (apiWrap$Chat != null) {
            return apiWrap$Chat.colorIndex;
        }
        throw new IllegalStateException("both user and chat are null");
    }

    public long id() {
        ApiWrap$User apiWrap$User = this.user;
        if (apiWrap$User != null) {
            return apiWrap$User.info.userId.longValue();
        }
        ApiWrap$Chat apiWrap$Chat = this.chat;
        if (apiWrap$Chat != null) {
            return apiWrap$Chat.bareId;
        }
        throw new IllegalStateException("both user and chat are null");
    }
}
