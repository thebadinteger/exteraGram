package com.exteragram.messenger.api.db;

import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.api.dto.NowPlayingInfoDTO;
import com.google.gson.Gson;
import java.math.BigDecimal;
import kotlin.Metadata;
import kotlin.jvm.internal.SourceDebugExtension;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0014\u0010\u0006\u001a\u0004\u0018\u00010\u00072\b\u0010\b\u001a\u0004\u0018\u00010\tH\u0007J\u0014\u0010\n\u001a\u0004\u0018\u00010\t2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0007H\u0007J\u0014\u0010\f\u001a\u0004\u0018\u00010\u00072\b\u0010\r\u001a\u0004\u0018\u00010\u000eH\u0007J\u0014\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\b\u0010\u000b\u001a\u0004\u0018\u00010\u0007H\u0007J\u0014\u0010\u0010\u001a\u0004\u0018\u00010\u00072\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H\u0007J\u0014\u0010\u0013\u001a\u0004\u0018\u00010\u00122\b\u0010\u0011\u001a\u0004\u0018\u00010\u0007H\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0014"}, d2 = {"Lcom/exteragram/messenger/api/db/Converters;", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "()V", "gson", "Lcom/google/gson/Gson;", "fromBadgeDTO", _UrlKt.FRAGMENT_ENCODE_SET, "badgeDTO", "Lcom/exteragram/messenger/api/dto/BadgeDTO;", "toBadgeDTO", "json", "fromNowPlayingInfoDTO", "nowPlayingInfoDTO", "Lcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;", "toNowPlayingInfoDTO", "fromBigDecimal", "value", "Ljava/math/BigDecimal;", "toBigDecimal", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
@SourceDebugExtension({"SMAP\nConverters.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Converters.kt\ncom/exteragram/messenger/api/db/Converters\n+ 2 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,52:1\n1#2:53\n*E\n"})
public final class Converters {
    private final Gson gson = new Gson();

    public final String fromBadgeDTO(BadgeDTO badgeDTO) {
        return this.gson.toJson(badgeDTO);
    }

    public final BadgeDTO toBadgeDTO(String json) {
        return (BadgeDTO) this.gson.fromJson(json, BadgeDTO.class);
    }

    public final String fromNowPlayingInfoDTO(NowPlayingInfoDTO nowPlayingInfoDTO) {
        return this.gson.toJson(nowPlayingInfoDTO);
    }

    public final NowPlayingInfoDTO toNowPlayingInfoDTO(String json) {
        return (NowPlayingInfoDTO) this.gson.fromJson(json, NowPlayingInfoDTO.class);
    }

    public final String fromBigDecimal(BigDecimal value) {
        if (value != null) {
            return value.toString();
        }
        return null;
    }

    public final BigDecimal toBigDecimal(String value) {
        if (value != null) {
            return new BigDecimal(value);
        }
        return null;
    }
}
