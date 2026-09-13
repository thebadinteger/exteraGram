package com.exteragram.messenger.api.dto;

import java.math.BigDecimal;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0007HÆ\u0003J1\u0010\u0016\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u0007HÆ\u0001J\u0013\u0010\u0017\u001a\u00020\u00182\b\u0010\u0019\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001a\u001a\u00020\u001bHÖ\u0001J\t\u0010\u001c\u001a\u00020\u0005HÖ\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\b\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0010¨\u0006\u001d"}, d2 = {"Lcom/exteragram/messenger/api/dto/BoostySubscriberDTO;", _UrlKt.FRAGMENT_ENCODE_SET, "id", _UrlKt.FRAGMENT_ENCODE_SET, "name", _UrlKt.FRAGMENT_ENCODE_SET, "totalAmountRub", "Ljava/math/BigDecimal;", "totalAmountUsd", "<init>", "(JLjava/lang/String;Ljava/math/BigDecimal;Ljava/math/BigDecimal;)V", "getId", "()J", "getName", "()Ljava/lang/String;", "getTotalAmountRub", "()Ljava/math/BigDecimal;", "getTotalAmountUsd", "component1", "component2", "component3", "component4", "copy", "equals", _UrlKt.FRAGMENT_ENCODE_SET, "other", "hashCode", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final /* data */ class BoostySubscriberDTO {
    private final long id;
    private final String name;
    private final BigDecimal totalAmountRub;
    private final BigDecimal totalAmountUsd;

    public static /* synthetic */ BoostySubscriberDTO copy$default(BoostySubscriberDTO boostySubscriberDTO, long j, String str, BigDecimal bigDecimal, BigDecimal bigDecimal2, int i, Object obj) {
        if ((i & 1) != 0) {
            j = boostySubscriberDTO.id;
        }
        long j2 = j;
        if ((i & 2) != 0) {
            str = boostySubscriberDTO.name;
        }
        String str2 = str;
        if ((i & 4) != 0) {
            bigDecimal = boostySubscriberDTO.totalAmountRub;
        }
        BigDecimal bigDecimal3 = bigDecimal;
        if ((i & 8) != 0) {
            bigDecimal2 = boostySubscriberDTO.totalAmountUsd;
        }
        return boostySubscriberDTO.copy(j2, str2, bigDecimal3, bigDecimal2);
    }

    public final long getId() {
        return this.id;
    }

    public final String getName() {
        return this.name;
    }

    public final BigDecimal getTotalAmountRub() {
        return this.totalAmountRub;
    }

    public final BigDecimal getTotalAmountUsd() {
        return this.totalAmountUsd;
    }

    public final BoostySubscriberDTO copy(long id, String name, BigDecimal totalAmountRub, BigDecimal totalAmountUsd) {
        return new BoostySubscriberDTO(id, name, totalAmountRub, totalAmountUsd);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BoostySubscriberDTO)) {
            return false;
        }
        BoostySubscriberDTO boostySubscriberDTO = (BoostySubscriberDTO) other;
        return this.id == boostySubscriberDTO.id && Intrinsics.areEqual(this.name, boostySubscriberDTO.name) && Intrinsics.areEqual(this.totalAmountRub, boostySubscriberDTO.totalAmountRub) && Intrinsics.areEqual(this.totalAmountUsd, boostySubscriberDTO.totalAmountUsd);
    }

    public int hashCode() {
        return (((((Long.hashCode(this.id) * 31) + this.name.hashCode()) * 31) + this.totalAmountRub.hashCode()) * 31) + this.totalAmountUsd.hashCode();
    }

    public String toString() {
        return "BoostySubscriberDTO(id=" + this.id + ", name=" + this.name + ", totalAmountRub=" + this.totalAmountRub + ", totalAmountUsd=" + this.totalAmountUsd + ')';
    }

    public BoostySubscriberDTO(long j, String str, BigDecimal bigDecimal, BigDecimal bigDecimal2) {
        this.id = j;
        this.name = str;
        this.totalAmountRub = bigDecimal;
        this.totalAmountUsd = bigDecimal2;
    }
}
