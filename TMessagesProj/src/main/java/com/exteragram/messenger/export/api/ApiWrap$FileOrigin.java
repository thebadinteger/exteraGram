package com.exteragram.messenger.export.api;

import com.android.tools.r8.RecordTag;
import com.exteragram.messenger.ai.network.Client$ImagePayload$$ExternalSyntheticRecord1;
import java.util.Objects;
import org.telegram.tgnet.TLRPC;

public final class ApiWrap$FileOrigin extends RecordTag {
    private final long customEmojiId;
    private final int messageId;
    private final TLRPC.InputPeer peer;
    private final int split;
    private final int storyId;

    private Object[] $record$getFieldsAsObjects() {
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
        return ApiWrap$FileOrigin$$ExternalSyntheticRecord0.m(this.split, this.messageId, this.storyId, this.customEmojiId, this.peer);
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
        return Client$ImagePayload$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), ApiWrap$FileOrigin.class, "split;peer;messageId;storyId;customEmojiId");
    }

    public ApiWrap$FileOrigin() {
        this(0, null, 0, 0, 0L);
    }
}
