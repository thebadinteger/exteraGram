package com.exteragram.messenger.api.dto;

import com.exteragram.messenger.api.model.RegDateFlag;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u0006\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B'\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0016\u001a\u00020\u0007HÆ\u0003J\t\u0010\u0017\u001a\u00020\tHÆ\u0003J1\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tHÆ\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010\u001c\u001a\u00020\u001dHÖ\u0001J\t\u0010\u001e\u001a\u00020\tHÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u001f"}, d2 = {"Lcom/exteragram/messenger/api/dto/RegDateDTO;", _UrlKt.FRAGMENT_ENCODE_SET, "timestamp", _UrlKt.FRAGMENT_ENCODE_SET, "accuracy", _UrlKt.FRAGMENT_ENCODE_SET, "flag", "Lcom/exteragram/messenger/api/model/RegDateFlag;", "date", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(JDLcom/exteragram/messenger/api/model/RegDateFlag;Ljava/lang/String;)V", "getTimestamp", "()J", "getAccuracy", "()D", "getFlag", "()Lcom/exteragram/messenger/api/model/RegDateFlag;", "getDate", "()Ljava/lang/String;", "component1", "component2", "component3", "component4", "copy", "equals", _UrlKt.FRAGMENT_ENCODE_SET, "other", "hashCode", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final /* data */ class RegDateDTO {
    private final double accuracy;
    private final String date;
    private final RegDateFlag flag;
    private final long timestamp;

    public static /* synthetic */ RegDateDTO copy$default(RegDateDTO regDateDTO, long j, double d, RegDateFlag regDateFlag, String str, int i, Object obj) {
        if ((i & 1) != 0) {
            j = regDateDTO.timestamp;
        }
        long j2 = j;
        if ((i & 2) != 0) {
            d = regDateDTO.accuracy;
        }
        double d2 = d;
        if ((i & 4) != 0) {
            regDateFlag = regDateDTO.flag;
        }
        RegDateFlag regDateFlag2 = regDateFlag;
        if ((i & 8) != 0) {
            str = regDateDTO.date;
        }
        return regDateDTO.copy(j2, d2, regDateFlag2, str);
    }

    public final long component1() {
        return this.timestamp;
    }

    public final double component2() {
        return this.accuracy;
    }

    public final RegDateFlag component3() {
        return this.flag;
    }

    public final String component4() {
        return this.date;
    }

    public final RegDateDTO copy(long timestamp, double accuracy, RegDateFlag flag, String date) {
        return new RegDateDTO(timestamp, accuracy, flag, date);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof RegDateDTO)) {
            return false;
        }
        RegDateDTO regDateDTO = (RegDateDTO) other;
        return this.timestamp == regDateDTO.timestamp && Double.compare(this.accuracy, regDateDTO.accuracy) == 0 && this.flag == regDateDTO.flag && Intrinsics.areEqual(this.date, regDateDTO.date);
    }

    public int hashCode() {
        return (((((Long.hashCode(this.timestamp) * 31) + Double.hashCode(this.accuracy)) * 31) + this.flag.hashCode()) * 31) + this.date.hashCode();
    }

    public String toString() {
        return "RegDateDTO(timestamp=" + this.timestamp + ", accuracy=" + this.accuracy + ", flag=" + this.flag + ", date=" + this.date + ')';
    }

    public RegDateDTO(long j, double d, RegDateFlag regDateFlag, String str) {
        this.timestamp = j;
        this.accuracy = d;
        this.flag = regDateFlag;
        this.date = str;
    }

    public final long getTimestamp() {
        return this.timestamp;
    }

    public final double getAccuracy() {
        return this.accuracy;
    }

    public final RegDateFlag getFlag() {
        return this.flag;
    }

    public final String getDate() {
        return this.date;
    }
}
