package com.exteragram.messenger.api.dto;

import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u001f\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001Bm\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u000e\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0005\u0012\b\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\b\u0010\f\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\u000f¢\u0006\u0004\b\u0010\u0010\u0011J\t\u0010 \u001a\u00020\u0003HÆ\u0003J\u0011\u0010!\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0005HÆ\u0003J\u000b\u0010\"\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010#\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010%\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010&\u001a\u00020\u000bHÆ\u0003J\u000b\u0010'\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010(\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0010\u0010)\u001a\u0004\u0018\u00010\u000fHÆ\u0003¢\u0006\u0002\u0010\u001eJ\u0088\u0001\u0010*\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0010\b\u0002\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\n\u001a\u00020\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u000fHÆ\u0001¢\u0006\u0002\u0010+J\u0013\u0010,\u001a\u00020\u000b2\b\u0010-\u001a\u0004\u0018\u00010\u0001HÖ\u0003J\t\u0010.\u001a\u00020/HÖ\u0001J\t\u00100\u001a\u00020\u0003HÖ\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0019\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0015R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0013R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0013R\u0013\u0010\b\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0013R\u0013\u0010\t\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0013R\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u001aR\u0013\u0010\f\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0013R\u0013\u0010\r\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0013R\u0015\u0010\u000e\u001a\u0004\u0018\u00010\u000f¢\u0006\n\n\u0002\u0010\u001f\u001a\u0004\b\u001d\u0010\u001e¨\u00061"}, d2 = {"Lcom/exteragram/messenger/api/dto/NowPlayingDTO;", _UrlKt.FRAGMENT_ENCODE_SET, "trackName", _UrlKt.FRAGMENT_ENCODE_SET, "artists", _UrlKt.FRAGMENT_ENCODE_SET, "albumName", "coverUrl", "previewUrl", "songUrl", "isPlaying", _UrlKt.FRAGMENT_ENCODE_SET, "deviceName", "platform", "duration", _UrlKt.FRAGMENT_ENCODE_SET, "<init>", "(Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/Long;)V", "getTrackName", "()Ljava/lang/String;", "getArtists", "()Ljava/util/List;", "getAlbumName", "getCoverUrl", "getPreviewUrl", "getSongUrl", "()Z", "getDeviceName", "getPlatform", "getDuration", "()Ljava/lang/Long;", "Ljava/lang/Long;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "copy", "(Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/Long;)Lcom/exteragram/messenger/api/dto/NowPlayingDTO;", "equals", "other", "hashCode", _UrlKt.FRAGMENT_ENCODE_SET, "toString", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public final /* data */ class NowPlayingDTO {
    private final String albumName;
    private final List<String> artists;
    private final String coverUrl;
    private final String deviceName;
    private final Long duration;
    private final boolean isPlaying;
    private final String platform;
    private final String previewUrl;
    private final String songUrl;
    private final String trackName;

    public static /* synthetic */ NowPlayingDTO copy$default(NowPlayingDTO nowPlayingDTO, String str, List list, String str2, String str3, String str4, String str5, boolean z, String str6, String str7, Long l, int i, Object obj) {
        if ((i & 1) != 0) {
            str = nowPlayingDTO.trackName;
        }
        if ((i & 2) != 0) {
            list = nowPlayingDTO.artists;
        }
        if ((i & 4) != 0) {
            str2 = nowPlayingDTO.albumName;
        }
        if ((i & 8) != 0) {
            str3 = nowPlayingDTO.coverUrl;
        }
        if ((i & 16) != 0) {
            str4 = nowPlayingDTO.previewUrl;
        }
        if ((i & 32) != 0) {
            str5 = nowPlayingDTO.songUrl;
        }
        if ((i & 64) != 0) {
            z = nowPlayingDTO.isPlaying;
        }
        if ((i & 128) != 0) {
            str6 = nowPlayingDTO.deviceName;
        }
        if ((i & 256) != 0) {
            str7 = nowPlayingDTO.platform;
        }
        if ((i & 512) != 0) {
            l = nowPlayingDTO.duration;
        }
        String str8 = str7;
        Long l2 = l;
        boolean z2 = z;
        String str9 = str6;
        String str10 = str4;
        String str11 = str5;
        return nowPlayingDTO.copy(str, list, str2, str3, str10, str11, z2, str9, str8, l2);
    }

    public final String component1() {
        return this.trackName;
    }

    public final Long component10() {
        return this.duration;
    }

    public final List<String> component2() {
        return this.artists;
    }

    public final String component3() {
        return this.albumName;
    }

    public final String component4() {
        return this.coverUrl;
    }

    public final String component5() {
        return this.previewUrl;
    }

    public final String component6() {
        return this.songUrl;
    }

    public final boolean component7() {
        return this.isPlaying;
    }

    public final String component8() {
        return this.deviceName;
    }

    public final String component9() {
        return this.platform;
    }

    public final NowPlayingDTO copy(String trackName, List<String> artists, String albumName, String coverUrl, String previewUrl, String songUrl, boolean isPlaying, String deviceName, String platform, Long duration) {
        return new NowPlayingDTO(trackName, artists, albumName, coverUrl, previewUrl, songUrl, isPlaying, deviceName, platform, duration);
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof NowPlayingDTO)) {
            return false;
        }
        NowPlayingDTO nowPlayingDTO = (NowPlayingDTO) other;
        return Intrinsics.areEqual(this.trackName, nowPlayingDTO.trackName) && Intrinsics.areEqual(this.artists, nowPlayingDTO.artists) && Intrinsics.areEqual(this.albumName, nowPlayingDTO.albumName) && Intrinsics.areEqual(this.coverUrl, nowPlayingDTO.coverUrl) && Intrinsics.areEqual(this.previewUrl, nowPlayingDTO.previewUrl) && Intrinsics.areEqual(this.songUrl, nowPlayingDTO.songUrl) && this.isPlaying == nowPlayingDTO.isPlaying && Intrinsics.areEqual(this.deviceName, nowPlayingDTO.deviceName) && Intrinsics.areEqual(this.platform, nowPlayingDTO.platform) && Intrinsics.areEqual(this.duration, nowPlayingDTO.duration);
    }

    public int hashCode() {
        int iHashCode = this.trackName.hashCode() * 31;
        List<String> list = this.artists;
        int iHashCode2 = (iHashCode + (list == null ? 0 : list.hashCode())) * 31;
        String str = this.albumName;
        int iHashCode3 = (iHashCode2 + (str == null ? 0 : str.hashCode())) * 31;
        String str2 = this.coverUrl;
        int iHashCode4 = (iHashCode3 + (str2 == null ? 0 : str2.hashCode())) * 31;
        String str3 = this.previewUrl;
        int iHashCode5 = (iHashCode4 + (str3 == null ? 0 : str3.hashCode())) * 31;
        String str4 = this.songUrl;
        int iHashCode6 = (((iHashCode5 + (str4 == null ? 0 : str4.hashCode())) * 31) + Boolean.hashCode(this.isPlaying)) * 31;
        String str5 = this.deviceName;
        int iHashCode7 = (iHashCode6 + (str5 == null ? 0 : str5.hashCode())) * 31;
        String str6 = this.platform;
        int iHashCode8 = (iHashCode7 + (str6 == null ? 0 : str6.hashCode())) * 31;
        Long l = this.duration;
        return iHashCode8 + (l != null ? l.hashCode() : 0);
    }

    public String toString() {
        return "NowPlayingDTO(trackName=" + this.trackName + ", artists=" + this.artists + ", albumName=" + this.albumName + ", coverUrl=" + this.coverUrl + ", previewUrl=" + this.previewUrl + ", songUrl=" + this.songUrl + ", isPlaying=" + this.isPlaying + ", deviceName=" + this.deviceName + ", platform=" + this.platform + ", duration=" + this.duration + ')';
    }

    public NowPlayingDTO(String str, List<String> list, String str2, String str3, String str4, String str5, boolean z, String str6, String str7, Long l) {
        this.trackName = str;
        this.artists = list;
        this.albumName = str2;
        this.coverUrl = str3;
        this.previewUrl = str4;
        this.songUrl = str5;
        this.isPlaying = z;
        this.deviceName = str6;
        this.platform = str7;
        this.duration = l;
    }

    public final String getTrackName() {
        return this.trackName;
    }

    public final List<String> getArtists() {
        return this.artists;
    }

    public final String getAlbumName() {
        return this.albumName;
    }

    public final String getCoverUrl() {
        return this.coverUrl;
    }

    public final String getPreviewUrl() {
        return this.previewUrl;
    }

    public final String getSongUrl() {
        return this.songUrl;
    }

    public final boolean isPlaying() {
        return this.isPlaying;
    }

    public final String getDeviceName() {
        return this.deviceName;
    }

    public final String getPlatform() {
        return this.platform;
    }

    public final Long getDuration() {
        return this.duration;
    }
}
