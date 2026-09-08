package com.exteragram.messenger.api.dto;

import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u0019\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\u000e\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u000f\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u001f\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005HÆ\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u0014\u001a\u00020\u0015HÖ\u0001J\t\u0010\u0016\u001a\u00020\u0005HÖ\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR \u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\u000b\"\u0004\b\f\u0010\r¨\u0006\u0017"}, d2 = {"Lcom/exteragram/messenger/api/dto/BadgeDTO;", _UrlKt.FRAGMENT_ENCODE_SET, "documentId", _UrlKt.FRAGMENT_ENCODE_SET, "text", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(JLjava/lang/String;)V", "getDocumentId", "()J", "getText", "()Ljava/lang/String;", "setText", "(Ljava/lang/String;)V", "component1", "component2", "copy", "equals", _UrlKt.FRAGMENT_ENCODE_SET, "other", "hashCode", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final /* data */ class BadgeDTO {

    @SerializedName("documentId")
    private final long documentId;

    @SerializedName("text")
    private String text;

    public static /* synthetic */ BadgeDTO copy$default(BadgeDTO badgeDTO, long j, String str, int i, Object obj) {
        if ((i & 1) != 0) {
            j = badgeDTO.documentId;
        }
        if ((i & 2) != 0) {
            str = badgeDTO.text;
        }
        return badgeDTO.copy(j, str);
    }

    public final long getDocumentId() {
        return this.documentId;
    }

    public final String getText() {
        return this.text;
    }

    public final BadgeDTO copy(long documentId, String text) {
        return new BadgeDTO(documentId, text);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BadgeDTO)) {
            return false;
        }
        BadgeDTO badgeDTO = (BadgeDTO) other;
        return this.documentId == badgeDTO.documentId && Intrinsics.areEqual(this.text, badgeDTO.text);
    }

    public int hashCode() {
        int iHashCode = Long.hashCode(this.documentId) * 31;
        String str = this.text;
        return iHashCode + (str == null ? 0 : str.hashCode());
    }

    public String toString() {
        return "BadgeDTO(documentId=" + this.documentId + ", text=" + this.text + ')';
    }

    public BadgeDTO(long j, String str) {
        this.documentId = j;
        this.text = str;
    }

    public final long getDocumentId() {
        return this.documentId;
    }

    public final String getText() {
        return this.text;
    }

    public final void setText(String str) {
        this.text = str;
    }
}
