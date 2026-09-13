package com.exteragram.messenger.export.ui;

import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;
import com.google.android.gms.cast.MediaTrack;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;

public class ExportMapper {
    private final ChatInfo chatInfo;
    private final int currentAccount;
    private final String path;

    public static class Action {

        @SerializedName("action")
        public String actionType;

        @SerializedName("actor")
        public String actor;

        @SerializedName("actor_id")
        public String actor_id;

        @SerializedName("amount")
        public long amount;

        @SerializedName("boost_peer_id")
        public long boost_peer_id;

        @SerializedName("boosts")
        public int boosts;

        @SerializedName("button_id")
        public int button_id;

        @SerializedName("charge_id")
        public String charge_id;

        @SerializedName("currency")
        public String currency;

        @SerializedName("information_text")
        public String customAction;

        @SerializedName("discard_reason")
        public String discard_reason;

        @SerializedName("distance")
        public int distance;

        @SerializedName("duration_seconds")
        public int duration;

        @SerializedName("emoticon")
        public String emotion;

        @SerializedName("from_id")
        public long from_id;

        @SerializedName("game_message_id")
        public int game_message_id;

        @SerializedName("gift_text")
        public List<Entity> giftText;

        @SerializedName("gift_code")
        public String gift_code;

        @SerializedName("gift_id")
        public long gift_id;

        @SerializedName("giveaway_msg_id")
        public int giveaway_msg_id;

        @SerializedName("inviter_id")
        public long inviterId;

        @SerializedName("is_unclaimed")
        public boolean is_unclaimed;

        @SerializedName("media_spoiler")
        public boolean media_spoiler;

        @SerializedName("members")
        public List<Long> members;

        @SerializedName("message_id")
        public int messageId;

        @SerializedName("months")
        public int months;

        @SerializedName("new_icon_emoji_id")
        public int new_icon_emoji_id;

        @SerializedName("peer")
        public long peer;

        @SerializedName("peers")
        public List<String> peers;

        @SerializedName("period")
        public int period;

        @SerializedName("reason_app_id")
        public long reason_app_id;

        @SerializedName("reason_app_name")
        public String reason_app_name;

        @SerializedName("reason_domain")
        public String reason_domain;

        @SerializedName("recurring")
        public String recurring;

        @SerializedName("schedule_date")
        public int schedule_date;

        @SerializedName("score")
        public int score;

        @SerializedName("stars")
        public long stars;

        @SerializedName("stars_boolean")
        public boolean stars_boolean;

        @SerializedName("text")
        public String text;

        @SerializedName("title")
        public String title;

        @SerializedName("to_id")
        public long to_id;

        @SerializedName("transaction_id")
        public String transaction_id;

        @SerializedName("unclaimed")
        public int unclaimed;

        @SerializedName("unclaimed_count")
        public int unclaimed_count;

        @SerializedName("values")
        public List<String> values;

        @SerializedName("via_giveaway")
        public boolean via_giveaway;

        @SerializedName("winners_count")
        public int winners_count;
    }

    public static class ChatInfo {

        @SerializedName("id")
        public long id;

        @SerializedName("msgsCount")
        public int msgsCount;

        @SerializedName("name")
        public String name;

        @SerializedName(TeXSymbolParser.TYPE_ATTR)
        public String type;
    }

    public static class Entity {

        @SerializedName("additional")
        public String additional;

        @SerializedName("text")
        public String text;

        @SerializedName(TeXSymbolParser.TYPE_ATTR)
        public String type;
    }

    public static class JsonMessage {

        @SerializedName("action")
        public Action action;

        @SerializedName("date")
        public int date;

        @SerializedName("from")
        public String from;

        @SerializedName("from_id")
        public String from_id;

        @SerializedName("id")
        public int id;

        @SerializedName("media")
        public Media media;

        @SerializedName("text_entities")
        public List<Entity> text_entities;

        @SerializedName(TeXSymbolParser.TYPE_ATTR)
        public String type;
    }

    public static class Media {

        @SerializedName("contact_information")
        public ContactInformation contact;

        @SerializedName("duration")
        public int duration;

        @SerializedName("file_name")
        public String fileName;

        @SerializedName("file")
        public String filePathRelative;

        @SerializedName("game_description")
        public String gameDescription;

        @SerializedName("game_short_name")
        public String gameShortName;

        @SerializedName("game_title")
        public String gameTitle;

        @SerializedName("giveaway_information")
        public GiveawayInformation giveawayInformation;

        @SerializedName("giveaway_results")
        public GiveawayResults giveawayResults;

        @SerializedName("height")
        public int height;

        @SerializedName("invoice_information")
        public InvoiceInformation invoice;

        @SerializedName("location_information")
        public LocationInformation location;

        @SerializedName("media_type")
        public String mediaType;

        @SerializedName("mimeType")
        public String mimeType;

        @SerializedName("paid_stars_amount")
        public long paidStarsAmount;

        @SerializedName("performer")
        public String performer;

        @SerializedName("photo")
        public String photoPathRelative;

        @SerializedName("poll")
        public Poll poll;

        @SerializedName("serializedSticker")
        public String serializedSticker;

        @SerializedName("size")
        public int size;

        @SerializedName("skipReason")
        public String skipReason;

        @SerializedName("media_spoiler")
        public boolean spoiler;

        @SerializedName("title")
        public String title;

        @SerializedName("ttl")
        public int ttl;

        @SerializedName("address")
        public String venueAddress;

        @SerializedName("place_name")
        public String venueTitle;

        @SerializedName("width")
        public int width;
    }

    public ExportMapper(int i, String str, ChatInfo chatInfo) {
        this.currentAccount = i;
        this.path = str;
        this.chatInfo = chatInfo;
    }

    private Pair<String, ArrayList<TLRPC.MessageEntity>> getMessageFromEntities(List<Entity> list) {
        TLRPC.MessageEntity tL_messageEntitySpoiler;
        StringBuilder sb = new StringBuilder();
        ArrayList arrayList = new ArrayList();
        if (list == null) {
            return new Pair<>(_UrlKt.FRAGMENT_ENCODE_SET, new ArrayList());
        }
        int length = 0;
        for (Entity entity : list) {
            sb.append(entity.text);
            String str = entity.type;
            if (str != null) {
                str.getClass();
                switch (str) {
                    case "spoiler":
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntitySpoiler();
                        break;
                    case "bank_card":
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntityBankCard();
                        break;
                    case "italic":
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntityItalic();
                        break;
                    case "text_link":
                        TLRPC.TL_messageEntityTextUrl tL_messageEntityTextUrl = new TLRPC.TL_messageEntityTextUrl();
                        tL_messageEntityTextUrl.url = entity.additional;
                        tL_messageEntitySpoiler = tL_messageEntityTextUrl;
                        break;
                    case "underline":
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntityUnderline();
                        break;
                    case "strikethrough":
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntityStrike();
                        break;
                    case "bot_command":
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntityBotCommand();
                        break;
                    case "pre":
                        TLRPC.TL_messageEntityPre tL_messageEntityPre = new TLRPC.TL_messageEntityPre();
                        tL_messageEntityPre.language = entity.additional;
                        tL_messageEntitySpoiler = tL_messageEntityPre;
                        break;
                    case "bold":
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntityBold();
                        break;
                    case "code":
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntityCode();
                        break;
                    case "link":
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntityUrl();
                        break;
                    case "email":
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntityEmail();
                        break;
                    case "phone":
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntityPhone();
                        break;
                    case "cashtag":
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntityCashtag();
                        break;
                    case "hashtag":
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntityHashtag();
                        break;
                    case "custom_emoji":
                        TLRPC.TL_messageEntityCustomEmoji tL_messageEntityCustomEmoji = new TLRPC.TL_messageEntityCustomEmoji();
                        tL_messageEntityCustomEmoji.document_id = Utilities.parseLong(entity.additional).longValue();
                        tL_messageEntitySpoiler = tL_messageEntityCustomEmoji;
                        break;
                    case "mention":
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntityMention();
                        break;
                    case "blockquote":
                        TLRPC.TL_messageEntityBlockquote tL_messageEntityBlockquote = new TLRPC.TL_messageEntityBlockquote();
                        tL_messageEntityBlockquote.collapsed = "true".equals(entity.additional);
                        tL_messageEntitySpoiler = tL_messageEntityBlockquote;
                        break;
                    case "mention_name":
                        TLRPC.TL_messageEntityMentionName tL_messageEntityMentionName = new TLRPC.TL_messageEntityMentionName();
                        tL_messageEntityMentionName.user_id = Utilities.parseLong(entity.additional).longValue();
                        tL_messageEntitySpoiler = tL_messageEntityMentionName;
                        break;
                    default:
                        tL_messageEntitySpoiler = new TLRPC.TL_messageEntityUnknown();
                        break;
                }
                tL_messageEntitySpoiler.offset = length;
                tL_messageEntitySpoiler.length = entity.text.length();
                arrayList.add(tL_messageEntitySpoiler);
                length += entity.text.length();
            }
        }
        return new Pair<>(sb.toString(), arrayList);
    }

    private TLRPC.MessageMedia mapMedia(final JsonMessage jsonMessage) {
        String str = jsonMessage.media.mediaType;
        if (str == null) {
            return lambda$mapMedia$0(jsonMessage);
        }
        switch (str) {
            case "sticker":
                try {
                    byte[] bArrDecode = Base64.decode(jsonMessage.media.serializedSticker, 0);
                    NativeByteBuffer nativeByteBuffer = new NativeByteBuffer(bArrDecode.length);
                    try {
                        nativeByteBuffer.buffer.put(bArrDecode);
                        nativeByteBuffer.rewind();
                        TLRPC.MessageMedia messageMediaTLdeserialize = TLRPC.MessageMedia.TLdeserialize(nativeByteBuffer, nativeByteBuffer.readInt32(false), false);
                        nativeByteBuffer.reuse();
                        return messageMediaTLdeserialize;
                    } catch (Exception e) {
                        FileLog.e("Export: failed to deserialize sticker: ", e);
                        nativeByteBuffer.reuse();
                        return null;
                    }
                } catch (Exception e2) {
                    FileLog.e("Export: failed to decode sticker: ", e2);
                    return null;
                }
            case "video_file":
            case "video_message":
            case "voice_message":
            case "animation":
            case "audio_file":
                return lambda$mapMedia$0(jsonMessage);
            case "paidMedia":
                TLRPC.TL_messageMediaPaidMedia tL_messageMediaPaidMedia = new TLRPC.TL_messageMediaPaidMedia();
                tL_messageMediaPaidMedia.stars_amount = jsonMessage.media.paidStarsAmount;
                return tL_messageMediaPaidMedia;
            case "giveawayResults":
                TLRPC.TL_messageMediaGiveawayResults tL_messageMediaGiveawayResults = new TLRPC.TL_messageMediaGiveawayResults();
                GiveawayResults giveawayResults = jsonMessage.media.giveawayResults;
                tL_messageMediaGiveawayResults.channel_id = Utilities.parseLong(giveawayResults.channel).longValue();
                tL_messageMediaGiveawayResults.winners = new ArrayList<>();
                if (giveawayResults.winners != null) {
                    for (String w : giveawayResults.winners) {
                        tL_messageMediaGiveawayResults.winners.add(Utilities.parseLong(w));
                    }
                }
                tL_messageMediaGiveawayResults.prize_description = giveawayResults.additionalPrize;
                tL_messageMediaGiveawayResults.until_date = Utilities.parseInt((CharSequence) giveawayResults.untilDate).intValue();
                tL_messageMediaGiveawayResults.launch_msg_id = Utilities.parseInt((CharSequence) giveawayResults.launchMessageId).intValue();
                tL_messageMediaGiveawayResults.additional_peers_count = Utilities.parseInt((CharSequence) giveawayResults.additionalPeersCount).intValue();
                tL_messageMediaGiveawayResults.winners_count = Utilities.parseInt((CharSequence) giveawayResults.winnersCount).intValue();
                tL_messageMediaGiveawayResults.unclaimed_count = Utilities.parseInt((CharSequence) giveawayResults.unclaimedCount).intValue();
                tL_messageMediaGiveawayResults.months = Utilities.parseInt((CharSequence) giveawayResults.months).intValue();
                tL_messageMediaGiveawayResults.stars = Utilities.parseInt((CharSequence) giveawayResults.stars).intValue();
                tL_messageMediaGiveawayResults.refunded = giveawayResults.isRefunded;
                tL_messageMediaGiveawayResults.only_new_subscribers = giveawayResults.onlyNewSubscribers;
                return tL_messageMediaGiveawayResults;
            case "game":
                TLRPC.TL_messageMediaGame tL_messageMediaGame = new TLRPC.TL_messageMediaGame();
                TLRPC.TL_game tL_game = new TLRPC.TL_game();
                tL_messageMediaGame.game = tL_game;
                Media media = jsonMessage.media;
                tL_game.title = media.gameTitle;
                tL_game.description = media.gameDescription;
                tL_game.short_name = media.gameShortName;
                return tL_messageMediaGame;
            case "poll":
                TLRPC.TL_messageMediaPoll tL_messageMediaPoll = new TLRPC.TL_messageMediaPoll();
                TLRPC.TL_poll tL_poll = new TLRPC.TL_poll();
                TLRPC.TL_textWithEntities tL_textWithEntities = tL_poll.question;
                Poll poll = jsonMessage.media.poll;
                tL_textWithEntities.text = poll.question;
                tL_poll.closed = poll.closed;
                for (Answer answer : poll.answers) {
                    TLRPC.TL_pollAnswer tL_pollAnswer = new TLRPC.TL_pollAnswer();
                    tL_pollAnswer.text.text = answer.text;
                    tL_poll.answers.add(tL_pollAnswer);
                }
                TLRPC.TL_pollResults tL_pollResults = new TLRPC.TL_pollResults();
                tL_pollResults.total_voters = Utilities.parseInt((CharSequence) jsonMessage.media.poll.totalVotes).intValue();
                tL_messageMediaPoll.poll = tL_poll;
                tL_messageMediaPoll.results = tL_pollResults;
                return tL_messageMediaPoll;
            case "photo":
                TLRPC.TL_messageMediaPhoto tL_messageMediaPhoto = new TLRPC.TL_messageMediaPhoto();
                Media media2 = jsonMessage.media;
                tL_messageMediaPhoto.spoiler = media2.spoiler;
                tL_messageMediaPhoto.ttl_seconds = media2.ttl;
                String str2 = media2.photoPathRelative;
                if (str2 != null) {
                    StringBuilder sb = new StringBuilder();
                    String str3 = this.path;
                    sb.append(str3.substring(0, str3.indexOf("/chats/")));
                    sb.append("/");
                    sb.append(str2);
                    tL_messageMediaPhoto.attachPath = sb.toString();
                }
                tL_messageMediaPhoto.photo = new TLRPC.TL_photo();
                TLRPC.TL_photoSize tL_photoSize = new TLRPC.TL_photoSize();
                Media media3 = jsonMessage.media;
                tL_photoSize.w = media3.width;
                tL_photoSize.h = media3.height;
                tL_photoSize.type = "y";
                tL_photoSize.location = new ExportFileLocation(this.path);
                tL_photoSize.size = Utilities.parseInt((CharSequence) ((jsonMessage.media.size / 1024) + "")).intValue();
                tL_messageMediaPhoto.photo.sizes.add(tL_photoSize);
                return tL_messageMediaPhoto;
            case "venue":
                TLRPC.TL_messageMediaVenue tL_messageMediaVenue = new TLRPC.TL_messageMediaVenue();
                Media media4 = jsonMessage.media;
                tL_messageMediaVenue.title = media4.venueTitle;
                tL_messageMediaVenue.address = media4.venueAddress;
                TLRPC.TL_geoPoint tL_geoPoint = new TLRPC.TL_geoPoint();
                tL_messageMediaVenue.geo = tL_geoPoint;
                tL_geoPoint._long = Utilities.parseLong(jsonMessage.media.location.longitude).longValue();
                tL_messageMediaVenue.geo.lat = Utilities.parseLong(jsonMessage.media.location.latitude).longValue();
                return tL_messageMediaVenue;
            case "contact":
                TLRPC.TL_messageMediaContact tL_messageMediaContact = new TLRPC.TL_messageMediaContact();
                ContactInformation contactInformation = jsonMessage.media.contact;
                tL_messageMediaContact.phone_number = contactInformation.phoneNumber;
                tL_messageMediaContact.first_name = contactInformation.firstName;
                tL_messageMediaContact.last_name = contactInformation.lastName;
                String str4 = contactInformation.vcardRelativePath;
                if (str4 != null) {
                    StringBuilder sb2 = new StringBuilder();
                    String str5 = this.path;
                    sb2.append(str5.substring(0, str5.indexOf("/chats/")));
                    sb2.append("/");
                    sb2.append(str4);
                    tL_messageMediaContact.vcard = sb2.toString();
                }
                return tL_messageMediaContact;
            case "geopoint":
                TLRPC.TL_messageMediaGeo tL_messageMediaGeo = new TLRPC.TL_messageMediaGeo();
                TLRPC.TL_geoPoint tL_geoPoint2 = new TLRPC.TL_geoPoint();
                tL_messageMediaGeo.geo = tL_geoPoint2;
                tL_geoPoint2._long = Utilities.parseLong(jsonMessage.media.location.longitude).longValue();
                tL_messageMediaGeo.geo.lat = Utilities.parseLong(jsonMessage.media.location.latitude).longValue();
                tL_messageMediaGeo.ttl_seconds = jsonMessage.media.ttl;
                return tL_messageMediaGeo;
            case "invoice":
                TLRPC.TL_messageMediaInvoice tL_messageMediaInvoice = new TLRPC.TL_messageMediaInvoice();
                InvoiceInformation invoiceInformation = jsonMessage.media.invoice;
                tL_messageMediaInvoice.title = invoiceInformation.title;
                tL_messageMediaInvoice.description = invoiceInformation.description;
                tL_messageMediaInvoice.total_amount = Utilities.parseLong(invoiceInformation.amount).longValue();
                InvoiceInformation invoiceInformation2 = jsonMessage.media.invoice;
                tL_messageMediaInvoice.currency = invoiceInformation2.currency;
                tL_messageMediaInvoice.receipt_msg_id = Utilities.parseInt((CharSequence) invoiceInformation2.receiptMsgId).intValue();
                return tL_messageMediaInvoice;
            case "giveawayStart":
                TLRPC.TL_messageMediaGiveaway tL_messageMediaGiveaway = new TLRPC.TL_messageMediaGiveaway();
                GiveawayInformation giveawayInformation = jsonMessage.media.giveawayInformation;
                tL_messageMediaGiveaway.quantity = Utilities.parseInt((CharSequence) giveawayInformation.quantity).intValue();
                tL_messageMediaGiveaway.months = Utilities.parseInt((CharSequence) giveawayInformation.months).intValue();
                tL_messageMediaGiveaway.until_date = Utilities.parseInt((CharSequence) giveawayInformation.until_date).intValue();
                tL_messageMediaGiveaway.channels = new ArrayList<>(giveawayInformation.channels);
                tL_messageMediaGiveaway.countries_iso2 = new ArrayList<>(giveawayInformation.countries);
                tL_messageMediaGiveaway.prize_description = giveawayInformation.additionalPrize;
                tL_messageMediaGiveaway.stars = Utilities.parseInt((CharSequence) giveawayInformation.stars).intValue();
                tL_messageMediaGiveaway.only_new_subscribers = giveawayInformation.onlyNew;
                return tL_messageMediaGiveaway;
            default:
                return new TLRPC.TL_messageMediaUnsupported();
        }
    }

    public TLRPC.TL_messageMediaDocument lambda$mapMedia$0(JsonMessage jsonMessage) {
        TLRPC.TL_messageMediaDocument tL_messageMediaDocument = new TLRPC.TL_messageMediaDocument();
        tL_messageMediaDocument.flags = 1;
        Media media = jsonMessage.media;
        tL_messageMediaDocument.spoiler = media.spoiler;
        tL_messageMediaDocument.ttl_seconds = media.ttl;
        TLRPC.TL_document tL_document = new TLRPC.TL_document();
        tL_messageMediaDocument.document = tL_document;
        tL_document.date = jsonMessage.date;
        Media media2 = jsonMessage.media;
        tL_document.size = media2.size;
        tL_document.mime_type = media2.mimeType;
        String str = media2.fileName;
        tL_document.file_name = str;
        tL_document.file_name_fixed = str;
        String str2 = media2.filePathRelative;
        StringBuilder sb = new StringBuilder();
        String str3 = this.path;
        byte b2 = 0;
        sb.append(str3.substring(0, str3.indexOf("/chats/")));
        sb.append("/");
        sb.append(str2);
        String string = sb.toString();
        tL_messageMediaDocument.document.localPath = string;
        String str4 = jsonMessage.media.mediaType;
        if (str4 != null) {
            str4.getClass();
            switch (str4.hashCode()) {
                case -1618268480:
                    if (!str4.equals("video_file")) {
                        b2 = -1;
                    }
                    break;
                case -1313337277:
                    b2 = !str4.equals("video_message") ? (byte) -1 : (byte) 1;
                    break;
                case 354039290:
                    b2 = !str4.equals("voice_message") ? (byte) -1 : (byte) 2;
                    break;
                case 1118509956:
                    b2 = !str4.equals("animation") ? (byte) -1 : (byte) 3;
                    break;
                case 1548945477:
                    b2 = !str4.equals("audio_file") ? (byte) -1 : (byte) 4;
                    break;
                default:
                    b2 = -1;
                    break;
            }
            TLRPC.DocumentAttribute tL_documentAttributeAudio = null;
            switch (b2) {
                case 0:
                case 1:
                    TLRPC.TL_documentAttributeVideo tL_documentAttributeVideo = new TLRPC.TL_documentAttributeVideo();
                    tL_documentAttributeVideo.round_message = jsonMessage.media.mediaType.equals("video_message");
                    Media media3 = jsonMessage.media;
                    tL_documentAttributeVideo.w = media3.width;
                    tL_documentAttributeVideo.h = media3.height;
                    tL_documentAttributeVideo.duration = media3.duration;
                    if (str2 != null) {
                        SendMessagesHelper.fillVideoAttribute(string, tL_documentAttributeVideo, null);
                        tL_messageMediaDocument.document.thumbs.add(ImageLoader.scaleAndSaveImage(SendMessagesHelper.createVideoThumbnail(string, 1), 320.0f, 320.0f, 90, true));
                    }
                    tL_documentAttributeAudio = tL_documentAttributeVideo;
                    break;
                case 2:
                case 4:
                    tL_documentAttributeAudio = new TLRPC.TL_documentAttributeAudio();
                    Media media4 = jsonMessage.media;
                    tL_documentAttributeAudio.duration = media4.duration;
                    tL_documentAttributeAudio.voice = media4.mediaType.equals("voice_message");
                    if (jsonMessage.media.mediaType.equals("audio_file")) {
                        Media media5 = jsonMessage.media;
                        tL_documentAttributeAudio.performer = media5.performer;
                        tL_documentAttributeAudio.title = media5.title;
                    }
                    break;
                case 3:
                    tL_documentAttributeAudio = new TLRPC.TL_documentAttributeAnimated();
                    break;
            }
            if (tL_documentAttributeAudio != null) {
                tL_messageMediaDocument.document.attributes.add(tL_documentAttributeAudio);
            }
        }
        return tL_messageMediaDocument;
    }

    private TLRPC.TL_messageService mapService(JsonMessage jsonMessage) {
        TLRPC.TL_messageService tL_messageService = new TLRPC.TL_messageService();
        tL_messageService.id = jsonMessage.id;
        tL_messageService.date = jsonMessage.date;
        String str = jsonMessage.action.actor_id;
        long jLongValue = (str == null || str.isEmpty()) ? 0L : Utilities.parseLong(jsonMessage.action.actor_id.substring(4)).longValue();
        if ("create_group".equals(jsonMessage.action.actionType)) {
            TLRPC.TL_messageActionChatCreate tL_messageActionChatCreate = new TLRPC.TL_messageActionChatCreate();
            tL_messageService.action = tL_messageActionChatCreate;
            tL_messageActionChatCreate.title = jsonMessage.action.title;
            tL_messageActionChatCreate.users = new ArrayList<>(jsonMessage.action.members);
        } else if ("edit_group_title".equals(jsonMessage.action.actionType)) {
            TLRPC.TL_messageActionChatEditTitle tL_messageActionChatEditTitle = new TLRPC.TL_messageActionChatEditTitle();
            tL_messageService.action = tL_messageActionChatEditTitle;
            tL_messageActionChatEditTitle.title = jsonMessage.action.title;
        } else if ("edit_group_photo".equals(jsonMessage.action.actionType)) {
            TLRPC.TL_messageActionChatEditPhoto tL_messageActionChatEditPhoto = new TLRPC.TL_messageActionChatEditPhoto();
            tL_messageService.action = tL_messageActionChatEditPhoto;
            tL_messageActionChatEditPhoto.photo = new TLRPC.TL_photo();
        } else if ("delete_group_photo".equals(jsonMessage.action.actionType)) {
            TLRPC.TL_messageActionChatDeletePhoto tL_messageActionChatDeletePhoto = new TLRPC.TL_messageActionChatDeletePhoto();
            tL_messageService.action = tL_messageActionChatDeletePhoto;
            tL_messageActionChatDeletePhoto.user_id = Utilities.parseLong(jsonMessage.action.actor).longValue();
        } else if ("invite_members".equals(jsonMessage.action.actionType)) {
            TLRPC.TL_messageActionChatAddUser tL_messageActionChatAddUser = new TLRPC.TL_messageActionChatAddUser();
            tL_messageService.action = tL_messageActionChatAddUser;
            tL_messageActionChatAddUser.users = new ArrayList<>(jsonMessage.action.members);
        } else if ("remove_members".equals(jsonMessage.action.actionType)) {
            TLRPC.TL_messageActionChatDeleteUser tL_messageActionChatDeleteUser = new TLRPC.TL_messageActionChatDeleteUser();
            tL_messageService.action = tL_messageActionChatDeleteUser;
            tL_messageActionChatDeleteUser.user_id = jLongValue;
        } else if ("join_group_by_link".equals(jsonMessage.action.actionType)) {
            TLRPC.TL_messageActionChatJoinedByLink tL_messageActionChatJoinedByLink = new TLRPC.TL_messageActionChatJoinedByLink();
            tL_messageService.action = tL_messageActionChatJoinedByLink;
            tL_messageActionChatJoinedByLink.user_id = jLongValue;
            tL_messageActionChatJoinedByLink.inviter_id = jsonMessage.action.inviterId;
        } else if ("create_channel".equals(jsonMessage.action.actionType)) {
            TLRPC.TL_messageActionChannelCreate tL_messageActionChannelCreate = new TLRPC.TL_messageActionChannelCreate();
            tL_messageService.action = tL_messageActionChannelCreate;
            tL_messageActionChannelCreate.user_id = jLongValue;
            tL_messageActionChannelCreate.title = jsonMessage.action.title;
        } else if ("migrate_to_supergroup".equals(jsonMessage.action.actionType)) {
            tL_messageService.action = new TLRPC.TL_messageActionChatMigrateTo();
        } else if ("migrate_from_group".equals(jsonMessage.action.actionType)) {
            TLRPC.TL_messageActionChannelMigrateFrom tL_messageActionChannelMigrateFrom = new TLRPC.TL_messageActionChannelMigrateFrom();
            tL_messageService.action = tL_messageActionChannelMigrateFrom;
            tL_messageActionChannelMigrateFrom.title = jsonMessage.action.title;
        } else if ("pin_message".equals(jsonMessage.action.actionType)) {
            tL_messageService.action = new TLRPC.TL_messageActionPinMessage();
        } else if ("clear_history".equals(jsonMessage.action.actionType)) {
            tL_messageService.action = new TLRPC.TL_messageActionHistoryClear();
        } else if ("score_in_game".equals(jsonMessage.action.actionType)) {
            TLRPC.TL_messageActionGameScore tL_messageActionGameScore = new TLRPC.TL_messageActionGameScore();
            tL_messageService.action = tL_messageActionGameScore;
            Action action = jsonMessage.action;
            tL_messageActionGameScore.score = action.score;
            tL_messageActionGameScore.game_id = action.game_message_id;
        } else {
            if ("send_payment".equals(jsonMessage.action.actionType)) {
                TLRPC.TL_messageActionPaymentSent tL_messageActionPaymentSent = new TLRPC.TL_messageActionPaymentSent();
                tL_messageService.action = tL_messageActionPaymentSent;
                tL_messageActionPaymentSent.flags = "used".equals(jsonMessage.action.recurring) ? 8 : 4;
                TLRPC.MessageAction messageAction = tL_messageService.action;
                Action action2 = jsonMessage.action;
                messageAction.total_amount = action2.amount;
                messageAction.currency = action2.currency;
                return tL_messageService;
            }
            if ("phone_call".equals(jsonMessage.action.actionType)) {
                TLRPC.TL_messageActionPhoneCall tL_messageActionPhoneCall = new TLRPC.TL_messageActionPhoneCall();
                tL_messageService.action = tL_messageActionPhoneCall;
                Action action3 = jsonMessage.action;
                tL_messageActionPhoneCall.duration = action3.duration;
                if ("hangup".equals(action3.discard_reason)) {
                    tL_messageService.action.reason = new TLRPC.TL_phoneCallDiscardReasonHangup();
                } else if ("busy".equals(jsonMessage.action.discard_reason)) {
                    tL_messageService.action.reason = new TLRPC.TL_phoneCallDiscardReasonBusy();
                } else if ("missed".equals(jsonMessage.action.discard_reason)) {
                    tL_messageService.action.reason = new TLRPC.TL_phoneCallDiscardReasonMissed();
                } else if ("disconnect".equals(jsonMessage.action.discard_reason)) {
                    tL_messageService.action.reason = new TLRPC.TL_phoneCallDiscardReasonDisconnect();
                }
            } else if ("take_screenshot".equals(jsonMessage.action.actionType)) {
                tL_messageService.action = new TLRPC.TL_messageActionScreenshotTaken();
            } else {
                String str2 = jsonMessage.action.customAction;
                if (str2 != null && !str2.isEmpty()) {
                    TLRPC.TL_messageActionCustomAction tL_messageActionCustomAction = new TLRPC.TL_messageActionCustomAction();
                    tL_messageService.action = tL_messageActionCustomAction;
                    tL_messageActionCustomAction.message = jsonMessage.action.customAction;
                } else {
                    if ("attach_menu_bot_allowed".equals(jsonMessage.action.actionType) || "web_app_bot_allowed".equals(jsonMessage.action.actionType) || "allow_sending_messages".equals(jsonMessage.action.actionType)) {
                        TLRPC.TL_messageActionBotAllowed tL_messageActionBotAllowed = new TLRPC.TL_messageActionBotAllowed();
                        tL_messageService.action = tL_messageActionBotAllowed;
                        if ("attach_menu_bot_allowed".equals(jsonMessage.action.actionType)) {
                            tL_messageActionBotAllowed.attach_menu = true;
                            return tL_messageService;
                        }
                        if ("web_app_bot_allowed".equals(jsonMessage.action.actionType)) {
                            tL_messageActionBotAllowed.from_request = true;
                            return tL_messageService;
                        }
                        if ("allow_sending_messages".equals(jsonMessage.action.actionType)) {
                            TLRPC.TL_botApp tL_botApp = new TLRPC.TL_botApp();
                            tL_messageActionBotAllowed.app = tL_botApp;
                            Action action4 = jsonMessage.action;
                            tL_botApp.id = action4.reason_app_id;
                            tL_botApp.title = action4.reason_app_name;
                            return tL_messageService;
                        }
                        tL_messageActionBotAllowed.domain = jsonMessage.action.reason_domain;
                        return tL_messageService;
                    }
                    if ("send_passport_values".equals(jsonMessage.action.actionType)) {
                        tL_messageService.action = new TLRPC.TL_messageActionSecureValuesSent();
                        if (jsonMessage.action.values != null) {
                            ArrayList<TLRPC.SecureValueType> arrayList = new ArrayList<>();
                            for (String str3 : jsonMessage.action.values) {
                                if (str3 != null) {
                                    switch (str3) {
                                        case "bank_statement":
                                            arrayList.add(new TLRPC.TL_secureValueTypeBankStatement());
                                            break;
                                        case "passport_registration":
                                            arrayList.add(new TLRPC.TL_secureValueTypePassportRegistration());
                                            break;
                                        case "rental_agreement":
                                            arrayList.add(new TLRPC.TL_secureValueTypeRentalAgreement());
                                            break;
                                        case "phone_number":
                                            arrayList.add(new TLRPC.TL_secureValueTypePhone());
                                            break;
                                        case "email":
                                            arrayList.add(new TLRPC.TL_secureValueTypeEmail());
                                            break;
                                        case "address_information":
                                            arrayList.add(new TLRPC.TL_secureValueTypeAddress());
                                            break;
                                        case "temporary_registration":
                                            arrayList.add(new TLRPC.TL_secureValueTypeTemporaryRegistration());
                                            break;
                                        case "identity_card":
                                            arrayList.add(new TLRPC.TL_secureValueTypeIdentityCard());
                                            break;
                                        case "utility_bill":
                                            arrayList.add(new TLRPC.TL_secureValueTypeUtilityBill());
                                            break;
                                        case "internal_passport":
                                            arrayList.add(new TLRPC.TL_secureValueTypeInternalPassport());
                                            break;
                                        case "personal_details":
                                            arrayList.add(new TLRPC.TL_secureValueTypePersonalDetails());
                                            break;
                                        case "driver_license":
                                            arrayList.add(new TLRPC.TL_secureValueTypeDriverLicense());
                                            break;
                                        case "passport":
                                            arrayList.add(new TLRPC.TL_secureValueTypePassport());
                                            break;
                                    }
                                }
                            }
                            ((TLRPC.TL_messageActionSecureValuesSent) tL_messageService.action).types = arrayList;
                        }
                    } else if ("joined_telegram".equals(jsonMessage.action.actionType)) {
                        tL_messageService.action = new TLRPC.TL_messageActionContactSignUp();
                    } else if ("proximity_reached".equals(jsonMessage.action.actionType)) {
                        TLRPC.TL_messageActionGeoProximityReached tL_messageActionGeoProximityReached = new TLRPC.TL_messageActionGeoProximityReached();
                        tL_messageService.action = tL_messageActionGeoProximityReached;
                        tL_messageActionGeoProximityReached.from_id = MessagesController.getInstance(this.currentAccount).getPeer(jsonMessage.action.from_id);
                        tL_messageActionGeoProximityReached.to_id = MessagesController.getInstance(this.currentAccount).getPeer(jsonMessage.action.to_id);
                        tL_messageActionGeoProximityReached.distance = jsonMessage.action.distance;
                    } else if ("requested_phone_number".equals(jsonMessage.action.actionType)) {
                        tL_messageService.action = new TLRPC.TL_messageActionPhoneNumberRequest();
                    } else if ("group_call".equals(jsonMessage.action.actionType)) {
                        TLRPC.TL_messageActionGroupCall tL_messageActionGroupCall = new TLRPC.TL_messageActionGroupCall();
                        tL_messageService.action = tL_messageActionGroupCall;
                        tL_messageActionGroupCall.duration = jsonMessage.action.duration;
                    } else if ("invite_to_group_call".equals(jsonMessage.action.actionType)) {
                        TLRPC.TL_messageActionInviteToGroupCall tL_messageActionInviteToGroupCall = new TLRPC.TL_messageActionInviteToGroupCall();
                        tL_messageService.action = tL_messageActionInviteToGroupCall;
                        tL_messageActionInviteToGroupCall.duration = jsonMessage.action.duration;
                        ArrayList<Long> arrayList2 = new ArrayList<>();
                        Iterator<String> it = jsonMessage.action.values.iterator();
                        while (it.hasNext()) {
                            arrayList2.add(Utilities.parseLong(it.next()));
                        }
                        tL_messageService.action.users = arrayList2;
                    } else if ("set_messages_ttl".equals(jsonMessage.action.actionType)) {
                        TLRPC.TL_messageActionSetMessagesTTL tL_messageActionSetMessagesTTL = new TLRPC.TL_messageActionSetMessagesTTL();
                        tL_messageService.action = tL_messageActionSetMessagesTTL;
                        tL_messageActionSetMessagesTTL.ttl = jsonMessage.action.period;
                    } else if ("group_call_scheduled".equals(jsonMessage.action.actionType)) {
                        TLRPC.TL_messageActionGroupCallScheduled tL_messageActionGroupCallScheduled = new TLRPC.TL_messageActionGroupCallScheduled();
                        tL_messageService.action = tL_messageActionGroupCallScheduled;
                        tL_messageActionGroupCallScheduled.schedule_date = jsonMessage.action.schedule_date;
                    } else if ("edit_chat_theme".equals(jsonMessage.action.actionType)) {
                        tL_messageService.action = new TLRPC.TL_messageActionSetChatTheme();
                        if (!TextUtils.isEmpty(jsonMessage.action.emotion)) {
                            ((TLRPC.TL_chatTheme) ((TLRPC.TL_messageActionSetChatTheme) tL_messageService.action).theme).emoticon = jsonMessage.action.emotion;
                        }
                    } else if ("join_group_by_request".equals(jsonMessage.action.actionType)) {
                        tL_messageService.action = new TLRPC.TL_messageActionChatJoinedByRequest();
                    } else {
                        if ("send_webview_data".equals(jsonMessage.action.actionType)) {
                            TLRPC.TL_messageActionWebViewDataSent tL_messageActionWebViewDataSent = new TLRPC.TL_messageActionWebViewDataSent();
                            tL_messageService.action = tL_messageActionWebViewDataSent;
                            tL_messageActionWebViewDataSent.text = jsonMessage.action.text;
                            return tL_messageService;
                        }
                        if ("send_premium_gift".equals(jsonMessage.action.actionType)) {
                            TLRPC.TL_messageActionGiftPremium tL_messageActionGiftPremium = new TLRPC.TL_messageActionGiftPremium();
                            tL_messageService.action = tL_messageActionGiftPremium;
                            Action action5 = jsonMessage.action;
                            tL_messageActionGiftPremium.amount = action5.amount;
                            tL_messageActionGiftPremium.currency = action5.currency;
                            tL_messageActionGiftPremium.months = action5.months;
                        } else if ("topic_created".equals(jsonMessage.action.actionType)) {
                            TLRPC.TL_messageActionTopicCreate tL_messageActionTopicCreate = new TLRPC.TL_messageActionTopicCreate();
                            tL_messageService.action = tL_messageActionTopicCreate;
                            tL_messageActionTopicCreate.title = jsonMessage.action.title;
                        } else if ("topic_edit".equals(jsonMessage.action.actionType)) {
                            TLRPC.TL_messageActionTopicEdit tL_messageActionTopicEdit = new TLRPC.TL_messageActionTopicEdit();
                            tL_messageService.action = tL_messageActionTopicEdit;
                            Action action6 = jsonMessage.action;
                            tL_messageActionTopicEdit.title = action6.title;
                            tL_messageActionTopicEdit.icon_emoji_id = action6.new_icon_emoji_id;
                        } else if ("suggest_profile_photo".equals(jsonMessage.action.actionType)) {
                            TLRPC.TL_messageActionSuggestProfilePhoto tL_messageActionSuggestProfilePhoto = new TLRPC.TL_messageActionSuggestProfilePhoto();
                            tL_messageService.action = tL_messageActionSuggestProfilePhoto;
                            tL_messageActionSuggestProfilePhoto.title = jsonMessage.action.title;
                            tL_messageActionSuggestProfilePhoto.photo = new TLRPC.TL_photo();
                        } else if ("requested_peer".equals(jsonMessage.action.actionType)) {
                            TLRPC.TL_messageActionRequestedPeer tL_messageActionRequestedPeer = new TLRPC.TL_messageActionRequestedPeer();
                            tL_messageService.action = tL_messageActionRequestedPeer;
                            tL_messageActionRequestedPeer.button_id = jsonMessage.action.button_id;
                            ArrayList<TLRPC.Peer> arrayList3 = new ArrayList<>();
                            Iterator<String> it2 = jsonMessage.action.peers.iterator();
                            while (it2.hasNext()) {
                                arrayList3.add(MessagesController.getInstance(this.currentAccount).getPeer(Utilities.parseLong(it2.next()).longValue()));
                            }
                            ((TLRPC.TL_messageActionRequestedPeer) tL_messageService.action).peers = arrayList3;
                        } else {
                            if ("gift_code_prize".equals(jsonMessage.action.actionType)) {
                                TLRPC.TL_messageActionGiftCode tL_messageActionGiftCode = new TLRPC.TL_messageActionGiftCode();
                                tL_messageService.action = tL_messageActionGiftCode;
                                tL_messageActionGiftCode.slug = jsonMessage.action.gift_code;
                                tL_messageActionGiftCode.boost_peer = MessagesController.getInstance(this.currentAccount).getPeer(jsonMessage.action.boost_peer_id);
                                Action action7 = jsonMessage.action;
                                tL_messageActionGiftCode.unclaimed = action7.is_unclaimed;
                                tL_messageActionGiftCode.via_giveaway = action7.via_giveaway;
                                tL_messageService.action.months = action7.months;
                                return tL_messageService;
                            }
                            if ("giveaway_launch".equals(jsonMessage.action.actionType)) {
                                tL_messageService.action = new TLRPC.TL_messageActionGiveawayLaunch();
                                return tL_messageService;
                            }
                            if ("giveaway_results".equals(jsonMessage.action.actionType)) {
                                TLRPC.TL_messageActionGiveawayResults tL_messageActionGiveawayResults = new TLRPC.TL_messageActionGiveawayResults();
                                tL_messageService.action = tL_messageActionGiveawayResults;
                                Action action8 = jsonMessage.action;
                                tL_messageActionGiveawayResults.winners_count = action8.winners_count;
                                tL_messageActionGiveawayResults.unclaimed_count = action8.unclaimed_count;
                                tL_messageActionGiveawayResults.stars = action8.stars_boolean;
                                return tL_messageService;
                            }
                            if ("set_same_chat_wallpaper".equals(jsonMessage.action.actionType) || "set_chat_wallpaper".equals(jsonMessage.action.actionType)) {
                                TLRPC.TL_messageActionSetChatWallPaper tL_messageActionSetChatWallPaper = new TLRPC.TL_messageActionSetChatWallPaper();
                                tL_messageService.action = tL_messageActionSetChatWallPaper;
                                tL_messageActionSetChatWallPaper.same = "set_same_chat_wallpaper".equals(jsonMessage.action.actionType);
                            } else if ("boost_apply".equals(jsonMessage.action.actionType)) {
                                TLRPC.TL_messageActionBoostApply tL_messageActionBoostApply = new TLRPC.TL_messageActionBoostApply();
                                tL_messageService.action = tL_messageActionBoostApply;
                                tL_messageActionBoostApply.boosts = jsonMessage.action.boosts;
                            } else {
                                if ("refunded_payment".equals(jsonMessage.action.actionType)) {
                                    TLRPC.TL_messageActionPaymentRefunded tL_messageActionPaymentRefunded = new TLRPC.TL_messageActionPaymentRefunded();
                                    tL_messageService.action = tL_messageActionPaymentRefunded;
                                    Action action9 = jsonMessage.action;
                                    tL_messageActionPaymentRefunded.amount = action9.amount;
                                    tL_messageActionPaymentRefunded.currency = action9.currency;
                                    tL_messageActionPaymentRefunded.peer = MessagesController.getInstance(this.currentAccount).getPeer(jsonMessage.action.peer);
                                    TLRPC.TL_messageActionPaymentRefunded tL_messageActionPaymentRefunded2 = (TLRPC.TL_messageActionPaymentRefunded) tL_messageService.action;
                                    TLRPC.TL_paymentCharge tL_paymentCharge = new TLRPC.TL_paymentCharge();
                                    tL_messageActionPaymentRefunded2.charge = tL_paymentCharge;
                                    tL_paymentCharge.id = jsonMessage.action.charge_id;
                                    return tL_messageService;
                                }
                                if ("send_stars_gift".equals(jsonMessage.action.actionType)) {
                                    TLRPC.TL_messageActionGiftStars tL_messageActionGiftStars = new TLRPC.TL_messageActionGiftStars();
                                    tL_messageService.action = tL_messageActionGiftStars;
                                    Action action10 = jsonMessage.action;
                                    tL_messageActionGiftStars.stars = action10.stars;
                                    tL_messageActionGiftStars.amount = action10.amount;
                                    tL_messageActionGiftStars.currency = action10.currency;
                                } else if ("stars_prize".equals(jsonMessage.action.actionType)) {
                                    TLRPC.TL_messageActionPrizeStars tL_messageActionPrizeStars = new TLRPC.TL_messageActionPrizeStars();
                                    tL_messageService.action = tL_messageActionPrizeStars;
                                    tL_messageActionPrizeStars.amount = jsonMessage.action.stars;
                                    tL_messageActionPrizeStars.boost_peer = MessagesController.getInstance(this.currentAccount).getPeer(jsonMessage.action.boost_peer_id);
                                    Action action11 = jsonMessage.action;
                                    tL_messageActionPrizeStars.unclaimed = action11.is_unclaimed;
                                    tL_messageActionPrizeStars.giveaway_msg_id = action11.giveaway_msg_id;
                                    tL_messageActionPrizeStars.transaction_id = action11.transaction_id;
                                } else if ("send_star_gift".equals(jsonMessage.action.actionType)) {
                                    tL_messageService.action = new TLRPC.TL_messageActionStarGift();
                                    Pair<String, ArrayList<TLRPC.MessageEntity>> messageFromEntities = getMessageFromEntities(jsonMessage.action.giftText);
                                    TLRPC.TL_messageActionStarGift tL_messageActionStarGift = (TLRPC.TL_messageActionStarGift) tL_messageService.action;
                                    TLRPC.TL_textWithEntities tL_textWithEntities = new TLRPC.TL_textWithEntities();
                                    tL_messageActionStarGift.message = tL_textWithEntities;
                                    tL_textWithEntities.text = (String) messageFromEntities.first;
                                    tL_textWithEntities.entities = (ArrayList) messageFromEntities.second;
                                    TL_stars.TL_starGift tL_starGift = new TL_stars.TL_starGift();
                                    tL_messageActionStarGift.gift = tL_starGift;
                                    tL_starGift.id = jsonMessage.action.gift_id;
                                    tL_starGift.sticker = new TLRPC.TL_document();
                                    tL_messageActionStarGift.convert_stars = jsonMessage.action.stars;
                                }
                            }
                        }
                    }
                }
            }
        }
        tL_messageService.from_id = MessagesController.getInstance(UserConfig.selectedAccount).getPeer(jLongValue);
        tL_messageService.peer_id = MessagesController.getInstance(UserConfig.selectedAccount).getPeer(jLongValue);
        return tL_messageService;
    }

    public ArrayList<MessageObject> mapMessages(JsonMessage[] jsonMessageArr) {
        ArrayList<MessageObject> arrayList = new ArrayList<>();
        for (int length = jsonMessageArr.length - 1; length >= 0; length--) {
            JsonMessage jsonMessage = jsonMessageArr[length];
            if (jsonMessage != null) {
                if ("service".equals(jsonMessage.type)) {
                    arrayList.add(new MessageObject(this.currentAccount, mapService(jsonMessage), true, true));
                } else {
                    TLRPC.TL_message tL_message = new TLRPC.TL_message();
                    int i = jsonMessage.id;
                    tL_message.id = i;
                    tL_message.realId = i;
                    tL_message.date = jsonMessage.date;
                    TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
                    String str = this.chatInfo.type;
                    if (str != null && str.contains("bot_chat")) {
                        tL_peerUser.user_id = this.chatInfo.id;
                    } else {
                        String str2 = jsonMessage.from_id;
                        if (str2 != null) {
                            tL_peerUser.user_id = Utilities.parseLong(str2.substring(4)).longValue();
                        }
                    }
                    tL_message.from_id = tL_peerUser;
                    tL_message.peer_id = MessagesController.getInstance(UserConfig.selectedAccount).getPeer(tL_peerUser.user_id);
                    tL_message.out = tL_peerUser.user_id == UserConfig.getInstance(UserConfig.selectedAccount).clientUserId;
                    tL_message.dialog_id = tL_peerUser.user_id;
                    Pair<String, ArrayList<TLRPC.MessageEntity>> messageFromEntities = getMessageFromEntities(jsonMessage.text_entities);
                    tL_message.message = (String) messageFromEntities.first;
                    tL_message.entities = (ArrayList) messageFromEntities.second;
                    if (jsonMessage.media != null) {
                        tL_message.media = mapMedia(jsonMessage);
                        if (jsonMessage.media.skipReason != null) {
                            tL_message.message += "\n\n" + jsonMessage.media.skipReason;
                        }
                    }
                    arrayList.add(new MessageObject(this.currentAccount, tL_message, true, false));
                }
            }
        }
        return arrayList;
    }

    public static class ContactInformation {

        @SerializedName("first_name")
        public String firstName;

        @SerializedName("last_name")
        public String lastName;

        @SerializedName("phone_number")
        public String phoneNumber;

        @SerializedName("contact_vcard")
        public String vcardRelativePath;

        private ContactInformation() {
        }
    }

    public static class LocationInformation {

        @SerializedName("latitude")
        public String latitude;

        @SerializedName("longitude")
        public String longitude;

        private LocationInformation() {
        }
    }

    public static class InvoiceInformation {

        @SerializedName("amount")
        public String amount;

        @SerializedName("currency")
        public String currency;

        @SerializedName(MediaTrack.ROLE_DESCRIPTION)
        public String description;

        @SerializedName("receipt_message_id")
        public String receiptMsgId;

        @SerializedName("title")
        public String title;

        private InvoiceInformation() {
        }
    }

    public static class Poll {

        @SerializedName("answers")
        public List<Answer> answers;

        @SerializedName("closed")
        public boolean closed;

        @SerializedName("question")
        public String question;

        @SerializedName("total_voters")
        public String totalVotes;

        private Poll() {
        }
    }

    public static class Answer {

        @SerializedName("chosen")
        public boolean chosen;

        @SerializedName("text")
        public String text;

        @SerializedName("voters")
        public String votersCount;

        private Answer() {
        }
    }

    public static class GiveawayInformation {

        @SerializedName("additional_prize")
        public String additionalPrize;

        @SerializedName("channels")
        public List<Long> channels;

        @SerializedName("countries")
        public List<String> countries;

        @SerializedName("months")
        public String months;

        @SerializedName("is_only_new_subscribers")
        public boolean onlyNew;

        @SerializedName("quantity")
        public String quantity;

        @SerializedName("stars")
        public String stars;

        @SerializedName("until_date")
        public String until_date;

        private GiveawayInformation() {
        }
    }

    public static class GiveawayResults {

        @SerializedName("additional_peers_count")
        public String additionalPeersCount;

        @SerializedName("additional_prize")
        public String additionalPrize;

        @SerializedName("channel")
        public String channel;

        @SerializedName("is_refunded")
        public boolean isRefunded;

        @SerializedName("launch_message_id")
        public String launchMessageId;

        @SerializedName("months")
        public String months;

        @SerializedName("is_only_new_subscribers")
        public boolean onlyNewSubscribers;

        @SerializedName("stars")
        public String stars;

        @SerializedName("unclaimed_count")
        public String unclaimedCount;

        @SerializedName("until_date")
        public String untilDate;

        @SerializedName("winners")
        public List<String> winners;

        @SerializedName("winners_count")
        public String winnersCount;

        private GiveawayResults() {
        }
    }

    public static class ExportFileLocation extends TLRPC.FileLocation {
        public String path;

        public ExportFileLocation(String str) {
            this.path = str;
        }
    }
}
