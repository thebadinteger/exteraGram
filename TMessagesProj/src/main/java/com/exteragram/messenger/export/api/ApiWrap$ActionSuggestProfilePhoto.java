package com.exteragram.messenger.export.api;

import com.exteragram.messenger.utils.RecordTag;
import com.exteragram.messenger.export.output.html.HtmlWriter;
import java.util.Objects;

public final class ApiWrap$ActionSuggestProfilePhoto extends RecordTag {
    public final HtmlWriter.Photo photo;

    private /* synthetic */ boolean $record$equals(Object obj) {
        return (obj instanceof ApiWrap$ActionSuggestProfilePhoto) && Objects.equals(this.photo, ((ApiWrap$ActionSuggestProfilePhoto) obj).photo);
    }

    private /* synthetic */ Object[] $record$getFieldsAsObjects() {
        return new Object[]{this.photo};
    }

    public ApiWrap$ActionSuggestProfilePhoto(HtmlWriter.Photo photo) {
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
        return com.exteragram.messenger.utils.RecordUtils.recordToString($record$getFieldsAsObjects(), ApiWrap$ActionSuggestProfilePhoto.class, "photo");
    }
}
