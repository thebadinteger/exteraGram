package com.exteragram.messenger.api.dto;

import kotlin.Metadata;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÆ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\r\u001a\u00020\u000eHÖ\u0001J\t\u0010\u000f\u001a\u00020\u0010HÖ\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0011"}, d2 = {"Lcom/exteragram/messenger/api/dto/AddedRegDateDTO;", _UrlKt.FRAGMENT_ENCODE_SET, "userId", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(J)V", "getUserId", "()J", "component1", "copy", "equals", _UrlKt.FRAGMENT_ENCODE_SET, "other", "hashCode", _UrlKt.FRAGMENT_ENCODE_SET, "toString", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final /* data */ class AddedRegDateDTO {
    private final long userId;

    public static /* synthetic */ AddedRegDateDTO copy$default(AddedRegDateDTO addedRegDateDTO, long j, int i, Object obj) {
        if ((i & 1) != 0) {
            j = addedRegDateDTO.userId;
        }
        return addedRegDateDTO.copy(j);
    }

    public final long getUserId() {
        return this.userId;
    }

    public final AddedRegDateDTO copy(long userId) {
        return new AddedRegDateDTO(userId);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        return (other instanceof AddedRegDateDTO) && this.userId == ((AddedRegDateDTO) other).userId;
    }

    public int hashCode() {
        return Long.hashCode(this.userId);
    }

    public String toString() {
        return "AddedRegDateDTO(userId=" + this.userId + ')';
    }

    public AddedRegDateDTO(long j) {
        this.userId = j;
    }
}
