package com.exteragram.messenger.export.api;

import com.exteragram.messenger.utils.RecordTag;
import com.exteragram.messenger.ai.network.Client$ImagePayload$$ExternalSyntheticRecord1;
import java.util.Objects;
import org.telegram.tgnet.TLRPC;

public final class ApiWrap$FileOrigin extends RecordTag {
    private final long customEmojiId;
    private final int messageId;
    private final TLRPC.InputPeer peer;
    private final int split;
    private final int storyId;

    private /* synthetic */ boolean $record$equals(Object obj) {
        if (!(obj instanceof ApiWrap$FileOrigin)) {
            return false;
        }
        ApiWrap$FileOrigin apiWrap$FileOrigin = (ApiWrap$FileOrigin) obj;
        return this.split == apiWrap$FileOrigin.split && this.messageId == apiWrap$FileOrigin.messageId && this.storyId == apiWrap$FileOrigin.storyId && this.customEmojiId == apiWrap$FileOrigin.customEmojiId && Objects.equals(this.peer, apiWrap$FileOrigin.peer);
    }

    private /* synthetic */ Object[] $record$getFieldsAsObjects() {
        return new Object[]{Integer.valueOf(this.split), this.peer, Integer.valueOf(this.messageId), Integer.valueOf(this.storyId), Long.valueOf(this.customEmojiId)};
    }

    public ApiWrap$FileOrigin(int i, TLRPC.InputPeer inputPeer, int i2, int i3, long j) {
        this.split = i;
        this.peer = inputPeer;
        this.messageId = i2;
        this.storyId = i3;
        this.customEmojiId = j;
    }

    public final boolean equals(Object obj) {
        return $record$equals(obj);
    }

    public final int hashCode() {
        return Objects.hash(this.split, this.peer, this.messageId, this.storyId, this.customEmojiId);
    }

    public int messageId() {
        return this.messageId;
    }

    public TLRPC.InputPeer peer() {
        return this.peer;
    }

    public int split() {
        return this.split;
    }

    public int storyId() {
        return this.storyId;
    }

    public final String toString() {
        return "ApiWrap$FileOrigin[split=" + this.split + ", peer=" + this.peer + ", messageId=" + this.messageId + ", storyId=" + this.storyId + ", customEmojiId=" + this.customEmojiId + "]";
    }

    public ApiWrap$FileOrigin() {
        this(0, null, 0, 0, 0L);
    }
}
