package com.exteragram.messenger.export.api;

import com.android.tools.r8.RecordTag;
import com.exteragram.messenger.ai.network.Client$ImagePayload$$ExternalSyntheticRecord1;
import com.exteragram.messenger.export.output.html.HtmlWriter;
import java.util.Objects;

public final class ApiWrap$ActionChatEditPhoto extends RecordTag {
    private final HtmlWriter.Photo photo;

    private /* synthetic */ boolean $record$equals(Object obj) {
        return (obj instanceof ApiWrap$ActionChatEditPhoto) && Objects.equals(this.photo, ((ApiWrap$ActionChatEditPhoto) obj).photo);
    }

    private /* synthetic */ Object[] $record$getFieldsAsObjects() {
        return new Object[]{this.photo};
    }

    public ApiWrap$ActionChatEditPhoto(HtmlWriter.Photo photo) {
        this.photo = photo;
    }

    public final boolean equals(Object obj) {
        return $record$equals(obj);
    }

    public final int hashCode() {
        return Objects.hashCode(this.photo);
    }

    public HtmlWriter.Photo photo() {
        return this.photo;
    }

    public final String toString() {
        return Client$ImagePayload$$ExternalSyntheticRecord1.m($record$getFieldsAsObjects(), ApiWrap$ActionChatEditPhoto.class, "photo");
    }
}
