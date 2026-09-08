package com.exteragram.messenger.api.db;

import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.api.dto.NowPlayingInfoDTO;
import com.exteragram.messenger.api.dto.ProfileDTO;
import java.util.List;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import okhttp3.internal.url._UrlKt;

@Metadata(d1 = {"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\bg\u0018\u00002\u00020\u0001J\u001c\u0010\u0002\u001a\u00020\u00032\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H§@¢\u0006\u0002\u0010\u0007J\u0014\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005H§@¢\u0006\u0002\u0010\tJ\u0018\u0010\n\u001a\u0004\u0018\u00010\u00062\u0006\u0010\u000b\u001a\u00020\fH§@¢\u0006\u0002\u0010\rJ\u001c\u0010\u000e\u001a\u00020\u000f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\f0\u0005H§@¢\u0006\u0002\u0010\u0007J \u0010\u0011\u001a\u00020\u000f2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H§@¢\u0006\u0002\u0010\u0014J \u0010\u0015\u001a\u00020\u000f2\u0006\u0010\u000b\u001a\u00020\f2\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H§@¢\u0006\u0002\u0010\u0018¨\u0006\u0019À\u0006\u0003"}, d2 = {"Lcom/exteragram/messenger/api/db/ProfileDao;", _UrlKt.FRAGMENT_ENCODE_SET, "insertAll", _UrlKt.FRAGMENT_ENCODE_SET, "profiles", _UrlKt.FRAGMENT_ENCODE_SET, "Lcom/exteragram/messenger/api/dto/ProfileDTO;", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getAll", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "getById", "id", _UrlKt.FRAGMENT_ENCODE_SET, "(JLkotlin/coroutines/Continuation;)Ljava/lang/Object;", "deleteProfiles", _UrlKt.FRAGMENT_ENCODE_SET, "ids", "updateNowPlaying", "newNowPlaying", "Lcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;", "(JLcom/exteragram/messenger/api/dto/NowPlayingInfoDTO;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "updateBadge", "badge", "Lcom/exteragram/messenger/api/dto/BadgeDTO;", "(JLcom/exteragram/messenger/api/dto/BadgeDTO;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "TMessagesProj"}, k = 1, mv = {2, 2, 0}, xi = 48)
public interface ProfileDao {
    Object deleteProfiles(List<Long> list, Continuation<? super Integer> continuation);

    Object getAll(Continuation<? super List<ProfileDTO>> continuation);

    Object getById(long j, Continuation<? super ProfileDTO> continuation);

    Object insertAll(List<ProfileDTO> list, Continuation<? super Unit> continuation);

    Object updateBadge(long j, BadgeDTO badgeDTO, Continuation<? super Integer> continuation);

    Object updateNowPlaying(long j, NowPlayingInfoDTO nowPlayingInfoDTO, Continuation<? super Integer> continuation);
}
