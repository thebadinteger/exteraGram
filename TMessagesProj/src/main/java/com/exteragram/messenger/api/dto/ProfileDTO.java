package com.exteragram.messenger.api.dto;

import com.exteragram.messenger.api.model.ProfileStatus;
import com.exteragram.messenger.api.model.ProfileType;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;

@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u001d\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001BG\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t\u0012\b\u0010\n\u001a\u0004\u0018\u00010\u000b\u0012\b\u0010\f\u001a\u0004\u0018\u00010\r\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\r¢\u0006\u0004\b\u000f\u0010\u0010J\t\u0010\u001f\u001a\u00020\u0003HÆ\u0003J\t\u0010 \u001a\u00020\u0005HÆ\u0003J\t\u0010!\u001a\u00020\u0007HÆ\u0003J\u000b\u0010\"\u001a\u0004\u0018\u00010\tHÆ\u0003J\u000b\u0010#\u001a\u0004\u0018\u00010\u000bHÆ\u0003J\u0010\u0010$\u001a\u0004\u0018\u00010\rHÆ\u0003¢\u0006\u0002\u0010\u001cJ\u0010\u0010%\u001a\u0004\u0018\u00010\rHÆ\u0003¢\u0006\u0002\u0010\u001cJ\\\u0010&\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\rHÆ\u0001¢\u0006\u0002\u0010'J\u0013\u0010(\u001a\u00020\r2\b\u0010)\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010*\u001a\u00020+HÖ\u0001J\t\u0010,\u001a\u00020-HÖ\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0013\u0010\b\u001a\u0004\u0018\u00010\t¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0013\u0010\n\u001a\u0004\u0018\u00010\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0015\u0010\f\u001a\u0004\u0018\u00010\r¢\u0006\n\n\u0002\u0010\u001d\u001a\u0004\b\u001b\u0010\u001cR\u0015\u0010\u000e\u001a\u0004\u0018\u00010\r¢\u0006\n\n\u0002\u0010\u001d\u001a\u0004\b\u001e\u0010\u001c¨\u0006."}, d2 = {"Lcom/exteragram/messenger/api/dto/ProfileDTO;", _UrlKt.FRAGMENT_ENCODE_SET, "id", _UrlKt.FRAGMENT_ENCODE_SET, TeXSymbolParser.TYPE_ATTR, "Lcom/exteragram/messenger/api/model/ProfileType;", "status", "Lcom/exteragram/messenger/api/model/ProfileStatus;", "badge", "Lcom/exteragram/messenger/api/dto/BadgeDTO;", "nowPlaying", "Lcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;", "deleted", _UrlKt.FRAGMENT_ENCODE_SET, "canChangeBadge", "<init>", "(JLcom/exteragram/messenger/api/model/ProfileType;Lcom/exteragram/messenger/api/model/ProfileStatus;Lcom/exteragram/messenger/api/dto/BadgeDTO;Lcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;Ljava/lang/Boolean;Ljava/lang/Boolean;)V", "getId", "()J", "getType", "()Lcom/exteragram/messenger/api/model/ProfileType;", "getStatus", "()Lcom/exteragram/messenger/api/model/ProfileStatus;", "getBadge", "()Lcom/exteragram/messenger/api/dto/BadgeDTO;", "getNowPlaying", "()Lcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;", "getDeleted", "()Ljava/lang/Boolean;", "Ljava/lang/Boolean;", "getCanChangeBadge", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "(JLcom/exteragram/messenger/api/model/ProfileType;Lcom/exteragram/messenger/api/model/ProfileStatus;Lcom/exteragram/messenger/api/dto/BadgeDTO;Lcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;Ljava/lang/Boolean;Ljava/lang/Boolean;)Lcom/exteragram/messenger/api/dto/ProfileDTO;", "equals", "other", "hashCode", _UrlKt.FRAGMENT_ENCODE_SET, "toString", _UrlKt.FRAGMENT_ENCODE_SET, "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final /* data */ class ProfileDTO {
    private final BadgeDTO badge;
    private final Boolean canChangeBadge;
    private final Boolean deleted;
    private final long id;
    private final NowPlayingInfoDTO nowPlaying;
    private final ProfileStatus status;
    private final ProfileType type;

    public static /* synthetic */ ProfileDTO copy$default(ProfileDTO profileDTO, long j, ProfileType profileType, ProfileStatus profileStatus, BadgeDTO badgeDTO, NowPlayingInfoDTO nowPlayingInfoDTO, Boolean bool, Boolean bool2, int i, Object obj) {
        if ((i & 1) != 0) {
            j = profileDTO.id;
        }
        long j2 = j;
        if ((i & 2) != 0) {
            profileType = profileDTO.type;
        }
        ProfileType profileType2 = profileType;
        if ((i & 4) != 0) {
            profileStatus = profileDTO.status;
        }
        ProfileStatus profileStatus2 = profileStatus;
        if ((i & 8) != 0) {
            badgeDTO = profileDTO.badge;
        }
        BadgeDTO badgeDTO2 = badgeDTO;
        if ((i & 16) != 0) {
            nowPlayingInfoDTO = profileDTO.nowPlaying;
        }
        return profileDTO.copy(j2, profileType2, profileStatus2, badgeDTO2, nowPlayingInfoDTO, (i & 32) != 0 ? profileDTO.deleted : bool, (i & 64) != 0 ? profileDTO.canChangeBadge : bool2);
    }

    public final long getId() {
        return this.id;
    }

    public final ProfileType getType() {
        return this.type;
    }

    public final ProfileStatus getStatus() {
        return this.status;
    }

    public final BadgeDTO getBadge() {
        return this.badge;
    }

    public final NowPlayingInfoDTO getNowPlaying() {
        return this.nowPlaying;
    }

    public final Boolean getDeleted() {
        return this.deleted;
    }

    public final Boolean getCanChangeBadge() {
        return this.canChangeBadge;
    }

    public final ProfileDTO copy(long id, ProfileType type, ProfileStatus status, BadgeDTO badge, NowPlayingInfoDTO nowPlaying, Boolean deleted, Boolean canChangeBadge) {
        return new ProfileDTO(id, type, status, badge, nowPlaying, deleted, canChangeBadge);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ProfileDTO)) {
            return false;
        }
        ProfileDTO profileDTO = (ProfileDTO) other;
        return this.id == profileDTO.id && this.type == profileDTO.type && this.status == profileDTO.status && Intrinsics.areEqual(this.badge, profileDTO.badge) && Intrinsics.areEqual(this.nowPlaying, profileDTO.nowPlaying) && Intrinsics.areEqual(this.deleted, profileDTO.deleted) && Intrinsics.areEqual(this.canChangeBadge, profileDTO.canChangeBadge);
    }

    public int hashCode() {
        int iHashCode = ((((Long.hashCode(this.id) * 31) + this.type.hashCode()) * 31) + this.status.hashCode()) * 31;
        BadgeDTO badgeDTO = this.badge;
        int iHashCode2 = (iHashCode + (badgeDTO == null ? 0 : badgeDTO.hashCode())) * 31;
        NowPlayingInfoDTO nowPlayingInfoDTO = this.nowPlaying;
        int iHashCode3 = (iHashCode2 + (nowPlayingInfoDTO == null ? 0 : nowPlayingInfoDTO.hashCode())) * 31;
        Boolean bool = this.deleted;
        int iHashCode4 = (iHashCode3 + (bool == null ? 0 : bool.hashCode())) * 31;
        Boolean bool2 = this.canChangeBadge;
        return iHashCode4 + (bool2 != null ? bool2.hashCode() : 0);
    }

    public String toString() {
        return "ProfileDTO(id=" + this.id + ", type=" + this.type + ", status=" + this.status + ", badge=" + this.badge + ", nowPlaying=" + this.nowPlaying + ", deleted=" + this.deleted + ", canChangeBadge=" + this.canChangeBadge + ')';
    }

    public ProfileDTO(long j, ProfileType profileType, ProfileStatus profileStatus, BadgeDTO badgeDTO, NowPlayingInfoDTO nowPlayingInfoDTO, Boolean bool, Boolean bool2) {
        this.id = j;
        this.type = profileType;
        this.status = profileStatus;
        this.badge = badgeDTO;
        this.nowPlaying = nowPlayingInfoDTO;
        this.deleted = bool;
        this.canChangeBadge = bool2;
    }
}
