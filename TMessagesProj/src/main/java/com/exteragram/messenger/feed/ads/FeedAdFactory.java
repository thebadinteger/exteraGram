package com.exteragram.messenger.feed.ads;

import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.MessageObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLRPC;

public abstract class FeedAdFactory {
    private static final AtomicInteger nextId = new AtomicInteger(-20000000);

    public static MessageObject createAdMessageObject(int i, FeedAd feedAd) {
        TLRPC.TL_message tL_message = new TLRPC.TL_message();
        tL_message.id = nextId.getAndDecrement();
        tL_message.date = ConnectionsManager.getInstance(i).getCurrentTime();
        CharSequence charSequence = feedAd.bodyText;
        tL_message.message = charSequence != null ? charSequence.toString() : _UrlKt.FRAGMENT_ENCODE_SET;
        tL_message.peer_id = new TLRPC.TL_peerChannel();
        tL_message.flags |= 256;
        ArrayList<TLRPC.MessageEntity> arrayList = feedAd.entities;
        if (arrayList != null && !arrayList.isEmpty()) {
            tL_message.entities = feedAd.entities;
            tL_message.flags |= 128;
        }
        TLRPC.MessageMedia messageMedia = feedAd.media;
        if (messageMedia != null) {
            tL_message.media = messageMedia;
            tL_message.flags |= 512;
        }
        MessageObject messageObject = new MessageObject(i, (TLRPC.Message) tL_message, (AbstractMap<Long, TLRPC.User>) new HashMap(), (AbstractMap<Long, TLRPC.Chat>) new HashMap(), true, true);
        messageObject.searchType = 4;
        messageObject.sponsoredId = feedAd.id.getBytes(StandardCharsets.UTF_8);
        messageObject.sponsoredTitle = feedAd.title;
        messageObject.sponsoredUrl = feedAd.url;
        messageObject.sponsoredButtonText = feedAd.buttonText;
        messageObject.sponsoredInfo = feedAd.sponsorInfo;
        messageObject.sponsoredAdditionalInfo = feedAd.additionalInfo;
        messageObject.sponsoredRecommended = feedAd.recommended;
        messageObject.sponsoredCanReport = false;
        messageObject.sponsoredMedia = feedAd.media;
        messageObject.sponsoredColor = buildColor(feedAd.colorId);
        messageObject.setType();
        messageObject.textLayoutBlocks = new ArrayList<>();
        messageObject.generateThumbs(true);
        return messageObject;
    }

    private static TLRPC.PeerColor buildColor(int i) {
        if (i < 0) {
            return null;
        }
        TLRPC.TL_peerColor tL_peerColor = new TLRPC.TL_peerColor();
        tL_peerColor.flags |= 1;
        tL_peerColor.color = i;
        return tL_peerColor;
    }
}
