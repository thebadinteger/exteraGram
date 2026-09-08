package com.exteragram.messenger.export.api;

import android.text.TextUtils;
import android.util.Base64;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.export.ExportSettings;
import com.exteragram.messenger.export.output.FileManager;
import com.exteragram.messenger.export.output.html.HtmlContext;
import com.exteragram.messenger.export.output.html.HtmlWriter;
import com.exteragram.messenger.utils.chats.ChatUtils;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import kotlin.text.Typography;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_stories;
import org.webrtc.MediaStreamTrack;

public abstract class DataTypesUtils {
    public static ApiWrap$DialogInfo.Type DialogTypeFromChat(ApiWrap$Chat apiWrap$Chat) {
        if (apiWrap$Chat == null) {
            return ApiWrap$DialogInfo.Type.Unknown;
        }
        if (apiWrap$Chat.isMonoforum && !apiWrap$Chat.isMonoforumAdmin) {
            return ApiWrap$DialogInfo.Type.Personal;
        }
        boolean z = apiWrap$Chat.isMonoforumAdmin;
        if (z && apiWrap$Chat.isMonoforumOfPublicBroadcast) {
            return ApiWrap$DialogInfo.Type.PublicSupergroup;
        }
        if (z) {
            return ApiWrap$DialogInfo.Type.PrivateSupergroup;
        }
        boolean zIsEmpty = apiWrap$Chat.username.isEmpty();
        boolean z2 = apiWrap$Chat.isBroadcast;
        if (!zIsEmpty) {
            if (z2) {
                return ApiWrap$DialogInfo.Type.PublicChannel;
            }
            return ApiWrap$DialogInfo.Type.PublicSupergroup;
        }
        if (z2) {
            return ApiWrap$DialogInfo.Type.PrivateChannel;
        }
        if (apiWrap$Chat.isSupergroup) {
            return ApiWrap$DialogInfo.Type.PrivateSupergroup;
        }
        return ApiWrap$DialogInfo.Type.PrivateGroup;
    }

    public static ApiWrap$DialogInfo.Type DialogTypeFromUser(ApiWrap$User apiWrap$User) {
        if (apiWrap$User.isSelf) {
            return ApiWrap$DialogInfo.Type.Self;
        }
        if (apiWrap$User.isReplies) {
            return ApiWrap$DialogInfo.Type.Replies;
        }
        if (apiWrap$User.isVerifyCodes) {
            return ApiWrap$DialogInfo.Type.VerifyCodes;
        }
        if (apiWrap$User.isBot) {
            return ApiWrap$DialogInfo.Type.Bot;
        }
        return ApiWrap$DialogInfo.Type.Personal;
    }

    public static ArrayList<ApiWrap$TextPart> ParseText(String str, ArrayList<TLRPC.MessageEntity> arrayList) {
        int i;
        String strValueOf;
        int length = str.length();
        ArrayList<ApiWrap$TextPart> arrayList2 = new ArrayList<>();
        int size = arrayList.size();
        int i2 = 0;
        int i3 = 0;
        while (i3 < size) {
            TLRPC.MessageEntity messageEntity = arrayList.get(i3);
            i3++;
            TLRPC.MessageEntity messageEntity2 = messageEntity;
            int i4 = messageEntity2.offset;
            int i5 = messageEntity2.length;
            if (i4 >= i2 && i5 > 0 && (i = i5 + i4) <= length) {
                addTextPart(i4, i2, str, arrayList2);
                ApiWrap$TextPart apiWrap$TextPart = new ApiWrap$TextPart();
                ApiWrap$TextPart.Type type = ApiWrap$TextPart.Type.Unknown;
                if (messageEntity2 instanceof TLRPC.TL_messageEntityMention) {
                    type = ApiWrap$TextPart.Type.Mention;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityHashtag) {
                    type = ApiWrap$TextPart.Type.Hashtag;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityBotCommand) {
                    type = ApiWrap$TextPart.Type.BotCommand;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityUrl) {
                    type = ApiWrap$TextPart.Type.Url;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityEmail) {
                    type = ApiWrap$TextPart.Type.Email;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityBold) {
                    type = ApiWrap$TextPart.Type.Bold;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityItalic) {
                    type = ApiWrap$TextPart.Type.Italic;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityCode) {
                    type = ApiWrap$TextPart.Type.Code;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityPre) {
                    type = ApiWrap$TextPart.Type.Pre;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityTextUrl) {
                    type = ApiWrap$TextPart.Type.TextUrl;
                } else if ((messageEntity2 instanceof TLRPC.TL_messageEntityMentionName) || (messageEntity2 instanceof TLRPC.TL_inputMessageEntityMentionName)) {
                    type = ApiWrap$TextPart.Type.MentionName;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityPhone) {
                    type = ApiWrap$TextPart.Type.Phone;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityCashtag) {
                    type = ApiWrap$TextPart.Type.Cashtag;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityUnderline) {
                    type = ApiWrap$TextPart.Type.Underline;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityStrike) {
                    type = ApiWrap$TextPart.Type.Strike;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityBlockquote) {
                    type = ApiWrap$TextPart.Type.Blockquote;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityBankCard) {
                    type = ApiWrap$TextPart.Type.BankCard;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntitySpoiler) {
                    type = ApiWrap$TextPart.Type.Spoiler;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityCustomEmoji) {
                    type = ApiWrap$TextPart.Type.CustomEmoji;
                }
                apiWrap$TextPart.type = type;
                apiWrap$TextPart.text = str.substring(i4, i);
                if (messageEntity2 instanceof TLRPC.TL_messageEntityPre) {
                    strValueOf = ((TLRPC.TL_messageEntityPre) messageEntity2).language;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityTextUrl) {
                    strValueOf = ((TLRPC.TL_messageEntityTextUrl) messageEntity2).url;
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityMentionName) {
                    strValueOf = String.valueOf(((TLRPC.TL_messageEntityMentionName) messageEntity2).user_id);
                } else if (messageEntity2 instanceof TLRPC.TL_messageEntityCustomEmoji) {
                    strValueOf = String.valueOf(((TLRPC.TL_messageEntityCustomEmoji) messageEntity2).document_id);
                } else {
                    strValueOf = ((messageEntity2 instanceof TLRPC.TL_messageEntityBlockquote) && ((TLRPC.TL_messageEntityBlockquote) messageEntity2).collapsed) ? "1" : _UrlKt.FRAGMENT_ENCODE_SET;
                }
                apiWrap$TextPart.additional = strValueOf;
                arrayList2.add(apiWrap$TextPart);
                i2 = i;
            }
        }
        addTextPart(length, i2, str, arrayList2);
        return arrayList2;
    }

    private static void addTextPart(int i, int i2, String str, ArrayList<ApiWrap$TextPart> arrayList) {
        if (i > i2) {
            ApiWrap$TextPart apiWrap$TextPart = new ApiWrap$TextPart();
            apiWrap$TextPart.text = str.substring(i2, i);
            arrayList.add(apiWrap$TextPart);
        }
    }

    public static int SettingsFromDialogsType(ApiWrap$DialogInfo.Type type) {
        switch (AnonymousClass2.$SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[type.ordinal()]) {
            case 1:
            case 2:
                return 32;
            case 3:
                return 64;
            case 4:
            case 5:
                return 128;
            case 6:
                return 256;
            case 7:
                return 512;
            case 8:
                return 1024;
            default:
                return 0;
        }
    }

    public static boolean AddMigrateFromSlice(ApiWrap$DialogInfo apiWrap$DialogInfo, ApiWrap$DialogInfo apiWrap$DialogInfo2, int i, int i2) {
        TLRPC.InputPeer inputPeer = apiWrap$DialogInfo.migratedFromInput;
        if (!(inputPeer instanceof TLRPC.TL_inputPeerEmpty) && (!(inputPeer instanceof TLRPC.TL_inputPeerChat) || apiWrap$DialogInfo2.peerId != ((TLRPC.TL_inputPeerChat) inputPeer).chat_id)) {
            return false;
        }
        for (int i3 = 0; i3 != apiWrap$DialogInfo2.splits.size(); i3++) {
            apiWrap$DialogInfo.splits.add(Integer.valueOf(apiWrap$DialogInfo2.splits.get(i3).intValue() - i2));
            apiWrap$DialogInfo.messagesCountPerSplit.add(apiWrap$DialogInfo2.messagesCountPerSplit.get(i3));
        }
        apiWrap$DialogInfo.migratedFromInput = apiWrap$DialogInfo2.input;
        apiWrap$DialogInfo.splits.add(Integer.valueOf(i - i2));
        apiWrap$DialogInfo.messagesCountPerSplit.add(0);
        return true;
    }

    public static ApiWrap$ExportPersonalInfo ParsePersonalInfo(TLRPC.TL_users_userFull tL_users_userFull) {
        String str;
        ApiWrap$ExportPersonalInfo apiWrap$ExportPersonalInfo = new ApiWrap$ExportPersonalInfo();
        apiWrap$ExportPersonalInfo.user = ParseUser(tL_users_userFull.users.get(0));
        TLRPC.UserFull userFull = tL_users_userFull.full_user;
        if ((userFull instanceof TLRPC.TL_userFull) && (str = ((TLRPC.TL_userFull) userFull).about) != null) {
            apiWrap$ExportPersonalInfo.bio = str;
        }
        return apiWrap$ExportPersonalInfo;
    }

    public static ApiWrap$DialogsInfo ParseLeftChannelsInfo(TLRPC.messages_Chats messages_chats) {
        ApiWrap$DialogsInfo apiWrap$DialogsInfo = new ApiWrap$DialogsInfo();
        ArrayList<TLRPC.Chat> arrayList = messages_chats.chats;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            TLRPC.Chat chat = arrayList.get(i);
            i++;
            ApiWrap$DialogInfo apiWrap$DialogInfoDialogInfoFromChat = DialogInfoFromChat(ParseChat(chat));
            apiWrap$DialogInfoDialogInfoFromChat.isLeftChannel = true;
            apiWrap$DialogsInfo.left.add(apiWrap$DialogInfoDialogInfoFromChat);
        }
        return apiWrap$DialogsInfo;
    }

    public static ApiWrap$DialogInfo DialogInfoFromUser(ApiWrap$User apiWrap$User) {
        ApiWrap$DialogInfo apiWrap$DialogInfo = new ApiWrap$DialogInfo();
        TLRPC.TL_inputPeerUser tL_inputPeerUser = new TLRPC.TL_inputPeerUser();
        apiWrap$DialogInfo.input = tL_inputPeerUser;
        TLRPC.InputUser inputUser = apiWrap$User.input;
        tL_inputPeerUser.user_id = inputUser.user_id;
        tL_inputPeerUser.access_hash = inputUser.access_hash;
        ApiWrap$ContactInfo apiWrap$ContactInfo = apiWrap$User.info;
        apiWrap$DialogInfo.name = apiWrap$ContactInfo.firstName;
        apiWrap$DialogInfo.lastName = apiWrap$ContactInfo.lastName;
        apiWrap$DialogInfo.peerId = apiWrap$User.id.longValue();
        apiWrap$DialogInfo.topMessageDate = 0;
        apiWrap$DialogInfo.topMessageId = 0;
        apiWrap$DialogInfo.type = DialogTypeFromUser(apiWrap$User);
        apiWrap$DialogInfo.isLeftChannel = false;
        return apiWrap$DialogInfo;
    }

    public static ApiWrap$DialogInfo DialogInfoFromChat(ApiWrap$Chat apiWrap$Chat) {
        ApiWrap$DialogInfo apiWrap$DialogInfo = new ApiWrap$DialogInfo();
        apiWrap$DialogInfo.input = apiWrap$Chat.input;
        apiWrap$DialogInfo.name = apiWrap$Chat.title;
        apiWrap$DialogInfo.peerId = apiWrap$Chat.bareId;
        apiWrap$DialogInfo.topMessageDate = 0;
        apiWrap$DialogInfo.topMessageId = 0;
        apiWrap$DialogInfo.type = DialogTypeFromChat(apiWrap$Chat);
        apiWrap$DialogInfo.migratedToChannelId = apiWrap$Chat.migratedToChannelId;
        apiWrap$DialogInfo.isMonoforum = apiWrap$Chat.isMonoforum;
        if (apiWrap$Chat.isMonoforumAdmin) {
            apiWrap$DialogInfo.monoforumBroadcastInput = apiWrap$Chat.monoforumBroadcastInput;
        }
        return apiWrap$DialogInfo;
    }

    public static ApiWrap$Chat ParseChat(TLRPC.Chat chat) {
        int iPeerColorIndex;
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
        ApiWrap$Chat apiWrap$Chat = new ApiWrap$Chat();
        if (chat instanceof TLRPC.TL_chat) {
            TLRPC.TL_chat tL_chat = (TLRPC.TL_chat) chat;
            apiWrap$Chat.bareId = tL_chat.id;
            apiWrap$Chat.title = tL_chat.title;
            TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
            apiWrap$Chat.input = tL_inputPeerChat;
            tL_inputPeerChat.chat_id = apiWrap$Chat.bareId;
            TLRPC.InputChannel inputChannel = tL_chat.migrated_to;
            if (inputChannel instanceof TLRPC.TL_inputChannel) {
                apiWrap$Chat.migratedToChannelId = ((TLRPC.TL_inputChannel) inputChannel).channel_id;
                return apiWrap$Chat;
            }
        } else {
            if (chat instanceof TLRPC.TL_chatEmpty) {
                TLRPC.TL_chatEmpty tL_chatEmpty = (TLRPC.TL_chatEmpty) chat;
                apiWrap$Chat.bareId = tL_chatEmpty.id;
                TLRPC.TL_inputPeerChat tL_inputPeerChat2 = new TLRPC.TL_inputPeerChat();
                apiWrap$Chat.input = tL_inputPeerChat2;
                tL_inputPeerChat2.chat_id = tL_chatEmpty.id;
                return apiWrap$Chat;
            }
            if (chat instanceof TLRPC.TL_chatForbidden) {
                TLRPC.TL_chatForbidden tL_chatForbidden = (TLRPC.TL_chatForbidden) chat;
                apiWrap$Chat.bareId = tL_chatForbidden.id;
                apiWrap$Chat.title = tL_chatForbidden.title;
                TLRPC.TL_inputPeerChat tL_inputPeerChat3 = new TLRPC.TL_inputPeerChat();
                apiWrap$Chat.input = tL_inputPeerChat3;
                tL_inputPeerChat3.chat_id = tL_chatForbidden.id;
                return apiWrap$Chat;
            }
            if (chat instanceof TLRPC.TL_channel) {
                TLRPC.TL_channel tL_channel = (TLRPC.TL_channel) chat;
                long j = tL_channel.id;
                apiWrap$Chat.bareId = j;
                TLRPC.PeerColor peerColor = tL_channel.color;
                if (peerColor == null || (iPeerColorIndex = peerColor.color) == 0) {
                    iPeerColorIndex = PeerColorIndex(j);
                }
                apiWrap$Chat.colorIndex = iPeerColorIndex;
                apiWrap$Chat.isMonoforum = tL_channel.monoforum;
                boolean z = tL_channel.broadcast;
                apiWrap$Chat.isBroadcast = z;
                apiWrap$Chat.isSupergroup = tL_channel.megagroup;
                apiWrap$Chat.hasMonoforumAdminRights = z && (chat.creator || ((tL_chatAdminRights = chat.admin_rights) != null && tL_chatAdminRights.manage_direct_messages));
                apiWrap$Chat.monoforumLinkId = chat.linked_monoforum_id;
                apiWrap$Chat.title = tL_channel.title;
                String str = tL_channel.username;
                if (str != null && !str.isEmpty()) {
                    apiWrap$Chat.username = tL_channel.username;
                }
                TLRPC.TL_inputPeerChannel tL_inputPeerChannel = new TLRPC.TL_inputPeerChannel();
                apiWrap$Chat.input = tL_inputPeerChannel;
                tL_inputPeerChannel.channel_id = tL_channel.id;
                tL_inputPeerChannel.access_hash = tL_channel.access_hash;
                return apiWrap$Chat;
            }
            if (chat instanceof TLRPC.TL_channelForbidden) {
                TLRPC.TL_channelForbidden tL_channelForbidden = (TLRPC.TL_channelForbidden) chat;
                apiWrap$Chat.bareId = tL_channelForbidden.id;
                apiWrap$Chat.isBroadcast = tL_channelForbidden.broadcast;
                apiWrap$Chat.isSupergroup = tL_channelForbidden.megagroup;
                apiWrap$Chat.title = tL_channelForbidden.title;
                TLRPC.TL_inputPeerChannel tL_inputPeerChannel2 = new TLRPC.TL_inputPeerChannel();
                apiWrap$Chat.input = tL_inputPeerChannel2;
                tL_inputPeerChannel2.channel_id = tL_channelForbidden.id;
                tL_inputPeerChannel2.access_hash = tL_channelForbidden.access_hash;
            }
        }
        return apiWrap$Chat;
    }

    public static int PeerColorIndex(long j) {
        return ((int) j) % 7;
    }

    public static long StringBarePeerId(String str) {
        Long lValueOf = 255L;
        for (int i = 0; i < str.length(); i++) {
            lValueOf = Long.valueOf(((lValueOf.longValue() * 239) + ((long) str.charAt(i))) & 255);
        }
        return lValueOf.longValue();
    }

    public static void FinalizeDialogsInfo(ApiWrap$DialogsInfo apiWrap$DialogsInfo, ExportSettings exportSettings) {
        String str;
        ArrayList<ApiWrap$DialogInfo> arrayList = apiWrap$DialogsInfo.chats;
        ArrayList<ApiWrap$DialogInfo> arrayList2 = apiWrap$DialogsInfo.left;
        int i = 0;
        int length = NumberToString(String.valueOf((arrayList.size() + arrayList2.size()) - 1), 0, '0').length();
        int size = arrayList.size();
        int i2 = 0;
        int i3 = 0;
        while (i3 < size) {
            ApiWrap$DialogInfo apiWrap$DialogInfo = arrayList.get(i3);
            i3++;
            ApiWrap$DialogInfo apiWrap$DialogInfo2 = apiWrap$DialogInfo;
            i2++;
            String strNumberToString = NumberToString(String.valueOf(i2), length, '0');
            if (exportSettings.onlySinglePeer()) {
                str = _UrlKt.FRAGMENT_ENCODE_SET;
            } else {
                str = "chats/chat_" + strNumberToString + '/';
            }
            apiWrap$DialogInfo2.relativePath = str;
            ApiWrap$DialogInfo.Type type = apiWrap$DialogInfo2.type;
            apiWrap$DialogInfo2.onlyMyMessages = (type == ApiWrap$DialogInfo.Type.Personal || (SettingsFromDialogsType(type) & 96) == SettingsFromDialogsType(apiWrap$DialogInfo2.type)) ? false : true;
            Collections.sort(apiWrap$DialogInfo2.splits);
        }
        int size2 = arrayList2.size();
        while (i < size2) {
            ApiWrap$DialogInfo apiWrap$DialogInfo3 = arrayList2.get(i);
            i++;
            ApiWrap$DialogInfo apiWrap$DialogInfo4 = apiWrap$DialogInfo3;
            i2++;
            apiWrap$DialogInfo4.relativePath = "chats/chat_" + NumberToString(String.valueOf(i2), length, '0') + "/";
            apiWrap$DialogInfo4.onlyMyMessages = true;
        }
    }

    public static String NumberToString(long j) {
        return NumberToString(j + _UrlKt.FRAGMENT_ENCODE_SET, 0, '0');
    }

    public static String NumberToString(int i) {
        return NumberToString(i + _UrlKt.FRAGMENT_ENCODE_SET, 0, '0');
    }

    public static String NumberToString(String str, int i, char c2) {
        return FillLeft(str, i, c2).replace(',', '.');
    }

    private static String FillLeft(String str, int i, char c2) {
        if (i <= str.length()) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        int length = i - str.length();
        for (int i2 = 0; i2 != length; i2++) {
            sb.append(c2);
        }
        sb.append(str);
        return sb.toString();
    }

    public static String TypeString(ApiWrap$DialogInfo.Type type) {
        switch (AnonymousClass2.$SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[type.ordinal()]) {
            case 1:
            case 2:
            case 10:
            case 11:
                return "private";
            case 3:
                return "bot";
            case 4:
            case 5:
            case 6:
                return "group";
            case 7:
            case 8:
                return "channel";
            case 9:
                return "unknown";
            default:
                throw new IncompatibleClassChangeError();
        }
    }

    public static String DeletedString(ApiWrap$DialogInfo.Type type) {
        switch (AnonymousClass2.$SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[type.ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 9:
            case 10:
            case 11:
                return "Deleted Account";
            case 4:
            case 5:
            case 6:
                return "Deleted Group";
            case 7:
            case 8:
                return "Deleted Channel";
            default:
                throw new IncompatibleClassChangeError();
        }
    }

    public static String CountString(int i, boolean z) {
        if (i == 1) {
            return z ? "1 outgoing message" : "1 message";
        }
        if (i == 0) {
            return z ? "No outgoing messages" : "No messages";
        }
        StringBuilder sb = new StringBuilder();
        sb.append(NumberToString(String.valueOf(i), 0, ' '));
        sb.append(z ? " outgoing messages" : " messages");
        return sb.toString();
    }

    public static boolean SingleMessageAfter(TLRPC.messages_Messages messages_messages, int i) {
        int iSingleMessageDate = SingleMessageDate(messages_messages);
        return iSingleMessageDate > 0 && iSingleMessageDate > i;
    }

    public static int SingleMessageDate(TLRPC.messages_Messages messages_messages) {
        if (messages_messages instanceof TLRPC.TL_messages_messagesNotModified) {
            return 0;
        }
        ArrayList<TLRPC.Message> arrayList = messages_messages.messages;
        if (arrayList.isEmpty() || (arrayList.get(0) instanceof TLRPC.TL_messageEmpty)) {
            return 0;
        }
        return arrayList.get(0).date;
    }

    public static ApiWrap$Message ParseMessage(ApiWrap$ParseMediaContext apiWrap$ParseMediaContext, TLRPC.Message message, String str) {
        TLRPC.TL_messageReplyHeader tL_messageReplyHeader;
        int i;
        TLRPC.TL_messageReplyHeader tL_messageReplyHeader2;
        int i2;
        ApiWrap$Message apiWrap$Message = new ApiWrap$Message();
        apiWrap$Message.id = message.id;
        if (!(message instanceof TLRPC.TL_messageEmpty)) {
            apiWrap$Message.date = message.date;
            apiWrap$Message.out = message.out;
            apiWrap$Message.selfId = apiWrap$ParseMediaContext.selfPeerId;
            long peerId = MessageObject.getPeerId(message.peer_id);
            apiWrap$Message.peerId = peerId;
            TLRPC.Peer peer = message.from_id;
            if (peer != null) {
                apiWrap$Message.fromId = MessageObject.getPeerId(peer);
            } else {
                apiWrap$Message.fromId = peerId;
            }
            TLRPC.MessageReplyHeader messageReplyHeader = message.reply_to;
            if (messageReplyHeader != null && (messageReplyHeader instanceof TLRPC.TL_messageReplyHeader) && (i2 = (tL_messageReplyHeader2 = (TLRPC.TL_messageReplyHeader) messageReplyHeader).reply_to_msg_id) != 0) {
                apiWrap$Message.replyToMsgId = i2;
                TLRPC.Peer peer2 = tL_messageReplyHeader2.reply_to_peer_id;
                long peerId2 = peer2 != null ? MessageObject.getPeerId(peer2) : 0L;
                apiWrap$Message.replyToPeerId = peerId2;
                if (peerId2 == apiWrap$Message.peerId) {
                    apiWrap$Message.replyToPeerId = 0L;
                }
            }
        }
        if (message instanceof TLRPC.TL_message) {
            TLRPC.TL_message tL_message = (TLRPC.TL_message) message;
            TLRPC.MessageFwdHeader messageFwdHeader = tL_message.fwd_from;
            if (messageFwdHeader instanceof TLRPC.TL_messageFwdHeader) {
                TLRPC.TL_messageFwdHeader tL_messageFwdHeader = (TLRPC.TL_messageFwdHeader) messageFwdHeader;
                String str2 = tL_messageFwdHeader.from_name;
                String str3 = (str2 == null || str2.isEmpty()) ? _UrlKt.FRAGMENT_ENCODE_SET : tL_messageFwdHeader.from_name;
                boolean z = (MessageObject.getPeerId(tL_messageFwdHeader.from_id) == 0 && str3.isEmpty()) ? false : true;
                apiWrap$Message.forwarded = z;
                apiWrap$Message.forwardedDate = tL_messageFwdHeader.date;
                apiWrap$Message.showForwardedAsOriginal = z && MessageObject.getPeerId(tL_messageFwdHeader.saved_from_id) != 0;
                apiWrap$Message.savedFromChatId = MessageObject.getPeerId(tL_messageFwdHeader.saved_from_id);
                apiWrap$Message.forwardedFromName = str3;
            }
            String str4 = message.post_author;
            if (str4 != null) {
                apiWrap$Message.signature = str4;
            }
            TLRPC.MessageReplyHeader messageReplyHeader2 = message.reply_to;
            if (messageReplyHeader2 != null && (messageReplyHeader2 instanceof TLRPC.TL_messageReplyHeader) && (i = (tL_messageReplyHeader = (TLRPC.TL_messageReplyHeader) messageReplyHeader2).reply_to_msg_id) != 0) {
                apiWrap$Message.replyToMsgId = i;
                TLRPC.Peer peer3 = tL_messageReplyHeader.reply_to_peer_id;
                apiWrap$Message.replyToPeerId = peer3 != null ? MessageObject.getPeerId(peer3) : 0L;
            }
            long j = tL_message.via_bot_id;
            if (j != 0) {
                apiWrap$Message.viaBotId = j;
            }
            TLRPC.MessageMedia messageMedia = tL_message.media;
            if (messageMedia != null) {
                apiWrap$Message.media = ParseMedia(apiWrap$ParseMediaContext, messageMedia, str, apiWrap$Message.date);
            }
            TLRPC.ReplyMarkup replyMarkup = tL_message.reply_markup;
            if (replyMarkup != null && (replyMarkup instanceof TLRPC.TL_replyKeyboardMarkup)) {
                apiWrap$Message.inlineButtonRows = ButtonRowsFromTL((TLRPC.TL_replyKeyboardMarkup) replyMarkup);
            }
            apiWrap$Message.text = ParseText(tL_message.message, tL_message.entities);
            return apiWrap$Message;
        }
        if (message instanceof TLRPC.TL_messageService) {
            TLRPC.TL_messageService tL_messageService = (TLRPC.TL_messageService) message;
            TLRPC.MessageAction messageAction = tL_messageService.action;
            if (messageAction instanceof TLRPC.TL_messageActionSuggestProfilePhoto) {
                TLRPC.Photo photo = ((TLRPC.TL_messageActionSuggestProfilePhoto) messageAction).photo;
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append("photos/");
                int i3 = apiWrap$ParseMediaContext.photos + 1;
                apiWrap$ParseMediaContext.photos = i3;
                sb.append(PreparePhotoFileName(i3, message.date));
                apiWrap$Message.parsedAction = new ApiWrap$ActionSuggestProfilePhoto(ParsePhoto(photo, sb.toString()));
            } else if (messageAction instanceof TLRPC.TL_messageActionChatEditPhoto) {
                TLRPC.Photo photo2 = ((TLRPC.TL_messageActionChatEditPhoto) messageAction).photo;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append("photos/");
                int i4 = apiWrap$ParseMediaContext.photos + 1;
                apiWrap$ParseMediaContext.photos = i4;
                sb2.append(PreparePhotoFileName(i4, message.date));
                apiWrap$Message.parsedAction = new ApiWrap$ActionChatEditPhoto(ParsePhoto(photo2, sb2.toString()));
            }
            TLRPC.MessageAction messageAction2 = tL_messageService.action;
            if (messageAction2 != null) {
                apiWrap$Message.action = messageAction2;
            }
        }
        return apiWrap$Message;
    }

    private static ApiWrap$Media ParseMedia(ApiWrap$ParseMediaContext apiWrap$ParseMediaContext, TLRPC.MessageMedia messageMedia, String str, int i) {
        boolean z;
        int i2;
        TLRPC.TL_pollResults tL_pollResults;
        ArrayList<TLRPC.PollAnswerVoters> arrayList;
        ApiWrap$Document apiWrap$Document;
        HtmlWriter.Photo photo;
        ApiWrap$Media apiWrap$Media = new ApiWrap$Media();
        if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
            TLRPC.Photo photo2 = ((TLRPC.TL_messageMediaPhoto) messageMedia).photo;
            if (photo2 != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(str);
                sb.append("photos/");
                int i3 = apiWrap$ParseMediaContext.photos + 1;
                apiWrap$ParseMediaContext.photos = i3;
                sb.append(PreparePhotoFileName(i3, i));
                photo = ParsePhoto(photo2, sb.toString());
            } else {
                photo = new HtmlWriter.Photo();
            }
            photo.spoilered = messageMedia.spoiler;
            int i4 = messageMedia.ttl_seconds;
            if (i4 != 0) {
                apiWrap$Media.ttl = i4;
                photo.image.file = new ApiWrap$File();
            }
            apiWrap$Media.content = photo;
            return apiWrap$Media;
        }
        if (messageMedia instanceof TLRPC.TL_messageMediaGeo) {
            apiWrap$Media.content = parseGeoPoint(((TLRPC.TL_messageMediaGeo) messageMedia).geo);
            return apiWrap$Media;
        }
        if (messageMedia instanceof TLRPC.TL_messageMediaContact) {
            TLRPC.TL_messageMediaContact tL_messageMediaContact = (TLRPC.TL_messageMediaContact) messageMedia;
            ApiWrap$SharedContact apiWrap$SharedContact = new ApiWrap$SharedContact();
            apiWrap$SharedContact.info.userId = Long.valueOf(tL_messageMediaContact.user_id);
            ApiWrap$ContactInfo apiWrap$ContactInfo = apiWrap$SharedContact.info;
            apiWrap$ContactInfo.firstName = messageMedia.first_name;
            apiWrap$ContactInfo.lastName = messageMedia.last_name;
            apiWrap$ContactInfo.phoneNumber = messageMedia.phone_number;
            String str2 = tL_messageMediaContact.vcard;
            if (str2 != null && !str2.isEmpty()) {
                ApiWrap$File apiWrap$File = new ApiWrap$File();
                apiWrap$SharedContact.vcard = apiWrap$File;
                apiWrap$File.content = tL_messageMediaContact.vcard.getBytes(StandardCharsets.UTF_8);
                apiWrap$SharedContact.vcard.size = tL_messageMediaContact.vcard.length();
                ApiWrap$File apiWrap$File2 = apiWrap$SharedContact.vcard;
                StringBuilder sb2 = new StringBuilder();
                sb2.append(str);
                sb2.append("contacts/contact_");
                int i5 = apiWrap$ParseMediaContext.contacts + 1;
                apiWrap$ParseMediaContext.contacts = i5;
                sb2.append(i5);
                sb2.append(".vcard");
                apiWrap$File2.suggestedPath = sb2.toString();
            }
            apiWrap$Media.content = apiWrap$SharedContact;
            return apiWrap$Media;
        }
        if (messageMedia instanceof TLRPC.TL_messageMediaUnsupported) {
            return apiWrap$Media;
        }
        if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
            TLRPC.TL_messageMediaDocument tL_messageMediaDocument = (TLRPC.TL_messageMediaDocument) messageMedia;
            TLRPC.Document document = tL_messageMediaDocument.document;
            if (document != null) {
                apiWrap$Document = ParseDocument(apiWrap$ParseMediaContext, document, str, i);
            } else {
                apiWrap$Document = new ApiWrap$Document();
            }
            int i6 = tL_messageMediaDocument.ttl_seconds;
            if (i6 != 0) {
                apiWrap$Media.ttl = i6;
                apiWrap$Document.file = new ApiWrap$File();
            }
            apiWrap$Document.spoilered = tL_messageMediaDocument.spoiler;
            apiWrap$Media.content = apiWrap$Document;
            return apiWrap$Media;
        }
        if (messageMedia instanceof TLRPC.TL_messageMediaVenue) {
            TLRPC.TL_messageMediaVenue tL_messageMediaVenue = (TLRPC.TL_messageMediaVenue) messageMedia;
            ApiWrap$Venue apiWrap$Venue = new ApiWrap$Venue();
            apiWrap$Venue.point = parseGeoPoint(tL_messageMediaVenue.geo);
            apiWrap$Venue.title = tL_messageMediaVenue.title;
            apiWrap$Venue.address = tL_messageMediaVenue.address;
            apiWrap$Media.content = apiWrap$Venue;
            return apiWrap$Media;
        }
        if (messageMedia instanceof TLRPC.TL_messageMediaGame) {
            ApiWrap$Game apiWrap$Game = new ApiWrap$Game();
            TLRPC.TL_game tL_game = ((TLRPC.TL_messageMediaGame) messageMedia).game;
            if (tL_game != null) {
                apiWrap$Game.id = tL_game.id;
                apiWrap$Game.title = tL_game.title;
                apiWrap$Game.description = tL_game.description;
                apiWrap$Game.shortName = tL_game.short_name;
                apiWrap$Game.botId = apiWrap$ParseMediaContext.botId;
            }
            apiWrap$Media.content = apiWrap$Game;
            return apiWrap$Media;
        }
        if (messageMedia instanceof TLRPC.TL_messageMediaInvoice) {
            TLRPC.TL_messageMediaInvoice tL_messageMediaInvoice = (TLRPC.TL_messageMediaInvoice) messageMedia;
            ApiWrap$Invoice apiWrap$Invoice = new ApiWrap$Invoice();
            apiWrap$Invoice.title = tL_messageMediaInvoice.title;
            apiWrap$Invoice.description = tL_messageMediaInvoice.description;
            apiWrap$Invoice.currency = tL_messageMediaInvoice.currency;
            apiWrap$Invoice.amount = tL_messageMediaInvoice.total_amount;
            int i7 = tL_messageMediaInvoice.receipt_msg_id;
            if (i7 != 0) {
                apiWrap$Invoice.receiptMsgId = i7;
            }
            apiWrap$Media.content = apiWrap$Invoice;
            return apiWrap$Media;
        }
        if (messageMedia instanceof TLRPC.TL_messageMediaGeoLive) {
            TLRPC.TL_messageMediaGeoLive tL_messageMediaGeoLive = (TLRPC.TL_messageMediaGeoLive) messageMedia;
            apiWrap$Media.content = parseGeoPoint(tL_messageMediaGeoLive.geo);
            apiWrap$Media.ttl = tL_messageMediaGeoLive.period;
            return apiWrap$Media;
        }
        int i8 = 0;
        if (messageMedia instanceof TLRPC.TL_messageMediaPoll) {
            TLRPC.TL_messageMediaPoll tL_messageMediaPoll = (TLRPC.TL_messageMediaPoll) messageMedia;
            ApiWrap$Poll apiWrap$Poll = new ApiWrap$Poll();
            TLRPC.Poll poll = tL_messageMediaPoll.poll;
            if (poll instanceof TLRPC.TL_poll) {
                TLRPC.TL_poll tL_poll = (TLRPC.TL_poll) poll;
                apiWrap$Poll.id = tL_poll.id;
                apiWrap$Poll.question = tL_poll.question.text;
                apiWrap$Poll.closed = tL_poll.closed;
                HashMap map = new HashMap();
                TLRPC.PollResults pollResults = tL_messageMediaPoll.results;
                if ((pollResults instanceof TLRPC.TL_pollResults) && (arrayList = (tL_pollResults = (TLRPC.TL_pollResults) pollResults).results) != null) {
                    apiWrap$Poll.totalVotes = tL_pollResults.total_voters;
                    int size = arrayList.size();
                    int i9 = 0;
                    while (i9 < size) {
                        TLRPC.PollAnswerVoters pollAnswerVoters = arrayList.get(i9);
                        i9++;
                        TLRPC.PollAnswerVoters pollAnswerVoters2 = pollAnswerVoters;
                        map.put(Base64.encodeToString(pollAnswerVoters2.option, 0), pollAnswerVoters2);
                    }
                }
                ArrayList<TLRPC.PollAnswer> arrayList2 = tL_poll.answers;
                int size2 = arrayList2.size();
                int i10 = 0;
                while (i10 < size2) {
                    TLRPC.PollAnswer pollAnswer = arrayList2.get(i10);
                    i10++;
                    TLRPC.PollAnswer pollAnswer2 = pollAnswer;
                    if (pollAnswer2 instanceof TLRPC.TL_pollAnswer) {
                        TLRPC.TL_pollAnswer tL_pollAnswer = (TLRPC.TL_pollAnswer) pollAnswer2;
                        String str3 = tL_pollAnswer.text.text;
                        byte[] bArr = tL_pollAnswer.option;
                        TLRPC.PollAnswerVoters pollAnswerVoters3 = (TLRPC.PollAnswerVoters) map.get(Base64.encodeToString(bArr, 0));
                        if (pollAnswerVoters3 != null) {
                            i2 = pollAnswerVoters3.voters;
                            z = pollAnswerVoters3.chosen;
                        } else {
                            z = false;
                            i2 = 0;
                        }
                        apiWrap$Poll.answers.add(new ApiWrap$Poll.Answer(str3, bArr, i2, z));
                    }
                }
            }
            apiWrap$Media.content = apiWrap$Poll;
            return apiWrap$Media;
        }
        if (messageMedia instanceof TLRPC.TL_messageMediaGiveaway) {
            apiWrap$Media.content = parseGiveaway((TLRPC.TL_messageMediaGiveaway) messageMedia);
            return apiWrap$Media;
        }
        if (messageMedia instanceof TLRPC.TL_messageMediaGiveawayResults) {
            apiWrap$Media.content = parseGiveaway((TLRPC.TL_messageMediaGiveawayResults) messageMedia);
            return apiWrap$Media;
        }
        if (messageMedia instanceof TLRPC.TL_messageMediaPaidMedia) {
            TLRPC.TL_messageMediaPaidMedia tL_messageMediaPaidMedia = (TLRPC.TL_messageMediaPaidMedia) messageMedia;
            ApiWrap$PaidMedia apiWrap$PaidMedia = new ApiWrap$PaidMedia();
            apiWrap$PaidMedia.stars = tL_messageMediaPaidMedia.stars_amount;
            ArrayList<TLRPC.MessageExtendedMedia> arrayList3 = tL_messageMediaPaidMedia.extended_media;
            int size3 = arrayList3.size();
            while (i8 < size3) {
                TLRPC.MessageExtendedMedia messageExtendedMedia = arrayList3.get(i8);
                i8++;
                TLRPC.MessageExtendedMedia messageExtendedMedia2 = messageExtendedMedia;
                if (messageExtendedMedia2 instanceof TLRPC.TL_messageExtendedMediaPreview) {
                    apiWrap$PaidMedia.extended.add(new ApiWrap$Media());
                } else if (messageExtendedMedia2 instanceof TLRPC.TL_messageExtendedMedia) {
                    apiWrap$PaidMedia.extended.add(ParseMedia(apiWrap$ParseMediaContext, ((TLRPC.TL_messageExtendedMedia) messageExtendedMedia2).media, str, i));
                }
            }
            apiWrap$Media.content = apiWrap$PaidMedia;
        }
        return apiWrap$Media;
    }

    private static ApiWrap$GiveawayStart parseGiveaway(TLRPC.TL_messageMediaGiveaway tL_messageMediaGiveaway) {
        ApiWrap$GiveawayStart apiWrap$GiveawayStart = new ApiWrap$GiveawayStart(tL_messageMediaGiveaway.until_date, tL_messageMediaGiveaway.stars, tL_messageMediaGiveaway.quantity, tL_messageMediaGiveaway.months, !tL_messageMediaGiveaway.only_new_subscribers);
        apiWrap$GiveawayStart.channels.addAll(tL_messageMediaGiveaway.channels);
        if (!tL_messageMediaGiveaway.countries_iso2.isEmpty()) {
            apiWrap$GiveawayStart.countries.addAll(tL_messageMediaGiveaway.countries_iso2);
        }
        String str = tL_messageMediaGiveaway.prize_description;
        if (str != null && !str.isEmpty()) {
            apiWrap$GiveawayStart.additionalPrize = tL_messageMediaGiveaway.prize_description;
        }
        return apiWrap$GiveawayStart;
    }

    private static ApiWrap$GiveawayResults parseGiveaway(TLRPC.TL_messageMediaGiveawayResults tL_messageMediaGiveawayResults) {
        ApiWrap$GiveawayResults apiWrap$GiveawayResults = new ApiWrap$GiveawayResults(tL_messageMediaGiveawayResults.channel_id, tL_messageMediaGiveawayResults.until_date, tL_messageMediaGiveawayResults.launch_msg_id, tL_messageMediaGiveawayResults.additional_peers_count, tL_messageMediaGiveawayResults.winners_count, tL_messageMediaGiveawayResults.unclaimed_count, tL_messageMediaGiveawayResults.months, tL_messageMediaGiveawayResults.stars, tL_messageMediaGiveawayResults.refunded, tL_messageMediaGiveawayResults.only_new_subscribers);
        apiWrap$GiveawayResults.winners.addAll(tL_messageMediaGiveawayResults.winners);
        String str = tL_messageMediaGiveawayResults.prize_description;
        if (str != null && !str.isEmpty()) {
            apiWrap$GiveawayResults.additionalPrize = tL_messageMediaGiveawayResults.prize_description;
        }
        return apiWrap$GiveawayResults;
    }

    private static ApiWrap$GeoPoint parseGeoPoint(TLRPC.GeoPoint geoPoint) {
        ApiWrap$GeoPoint apiWrap$GeoPoint = new ApiWrap$GeoPoint();
        if (geoPoint instanceof TLRPC.TL_geoPoint) {
            TLRPC.TL_geoPoint tL_geoPoint = (TLRPC.TL_geoPoint) geoPoint;
            apiWrap$GeoPoint.latitude = tL_geoPoint.lat;
            apiWrap$GeoPoint.longitude = tL_geoPoint._long;
            apiWrap$GeoPoint.valid = true;
        }
        return apiWrap$GeoPoint;
    }

    public static ApiWrap$User EmptyUser(long j) {
        TLRPC.TL_userEmpty tL_userEmpty = new TLRPC.TL_userEmpty();
        tL_userEmpty.id = j;
        return ParseUser(tL_userEmpty);
    }

    public static ApiWrap$Chat EmptyChat(long j) {
        TLRPC.TL_chatEmpty tL_chatEmpty = new TLRPC.TL_chatEmpty();
        tL_chatEmpty.id = j;
        return ParseChat(tL_chatEmpty);
    }

    public static ApiWrap$Peer EmptyPeer(TLRPC.Peer peer) {
        long j = peer.user_id;
        if (j != 0) {
            return new ApiWrap$Peer(EmptyUser(j));
        }
        long j2 = peer.chat_id;
        if (j2 != 0) {
            return new ApiWrap$Peer(EmptyChat(j2));
        }
        long j3 = peer.channel_id;
        if (j3 != 0) {
            return new ApiWrap$Peer(EmptyChat(j3));
        }
        Buffer$$ExternalSyntheticBUOutline4.m("PeerId in EmptyPeer: ", ExteraConfig.getGSON().toJson(peer));
        return null;
    }

    public static HashMap<Long, ApiWrap$Peer> ParsePeersLists(ArrayList<TLRPC.User> arrayList, ArrayList<TLRPC.Chat> arrayList2) {
        ApiWrap$Peer apiWrap$Peer;
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        int size = arrayList.size();
        int i = 0;
        int i2 = 0;
        while (i2 < size) {
            TLRPC.User user = arrayList.get(i2);
            i2++;
            ApiWrap$User apiWrap$UserParseUser = ParseUser(user);
            linkedHashMap.put(apiWrap$UserParseUser.info.userId, new ApiWrap$Peer(apiWrap$UserParseUser));
        }
        int size2 = arrayList2.size();
        while (i < size2) {
            TLRPC.Chat chat = arrayList2.get(i);
            i++;
            ApiWrap$Chat apiWrap$ChatParseChat = ParseChat(chat);
            linkedHashMap.put(Long.valueOf(apiWrap$ChatParseChat.bareId), new ApiWrap$Peer(apiWrap$ChatParseChat));
        }
        for (ApiWrap$Peer apiWrap$Peer2 : linkedHashMap.values()) {
            ApiWrap$Chat apiWrap$Chat = apiWrap$Peer2.chat;
            if (apiWrap$Chat != null && apiWrap$Chat.isMonoforum && (apiWrap$Peer = (ApiWrap$Peer) linkedHashMap.get(Long.valueOf(apiWrap$Chat.monoforumLinkId))) != null) {
                ApiWrap$Chat apiWrap$Chat2 = apiWrap$Peer2.chat;
                ApiWrap$Chat apiWrap$Chat3 = apiWrap$Peer.chat;
                apiWrap$Chat2.isMonoforumAdmin = apiWrap$Chat3.hasMonoforumAdminRights;
                apiWrap$Chat2.isMonoforumOfPublicBroadcast = !TextUtils.isEmpty(apiWrap$Chat3.username);
            }
        }
        return linkedHashMap;
    }

    public static HashMap<Long, ApiWrap$User> ParseUsersList(ArrayList<TLRPC.User> arrayList) {
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            TLRPC.User user = arrayList.get(i);
            i++;
            ApiWrap$User apiWrap$UserParseUser = ParseUser(user);
            linkedHashMap.put(apiWrap$UserParseUser.info.userId, apiWrap$UserParseUser);
        }
        return linkedHashMap;
    }

    public static ApiWrap$WebSession ParseWebSession(TLRPC.TL_webAuthorization tL_webAuthorization, HashMap<Long, ApiWrap$User> map) {
        String str;
        ApiWrap$User apiWrap$User;
        if (map == null || (apiWrap$User = map.get(Long.valueOf(tL_webAuthorization.bot_id))) == null || (str = apiWrap$User.username) == null) {
            str = _UrlKt.FRAGMENT_ENCODE_SET;
        }
        return new ApiWrap$WebSession(str, tL_webAuthorization.domain, tL_webAuthorization.browser, tL_webAuthorization.platform, tL_webAuthorization.date_created, tL_webAuthorization.date_active, tL_webAuthorization.ip, tL_webAuthorization.region);
    }

    public static ApiWrap$SessionsList ParseSessionsList(TL_account.authorizations authorizationsVar) {
        ApiWrap$SessionsList apiWrap$SessionsList = new ApiWrap$SessionsList();
        apiWrap$SessionsList.list.addAll(authorizationsVar.authorizations);
        return apiWrap$SessionsList;
    }

    public static ApiWrap$SessionsList ParseWebSessionsList(TL_account.webAuthorizations webauthorizations) {
        ApiWrap$SessionsList apiWrap$SessionsList = new ApiWrap$SessionsList();
        HashMap<Long, ApiWrap$User> mapParseUsersList = ParseUsersList(webauthorizations.users);
        ArrayList<TLRPC.TL_webAuthorization> arrayList = webauthorizations.authorizations;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            TLRPC.TL_webAuthorization tL_webAuthorization = arrayList.get(i);
            i++;
            apiWrap$SessionsList.webList.add(ParseWebSession(tL_webAuthorization, mapParseUsersList));
        }
        return apiWrap$SessionsList;
    }

    public static ApiWrap$MessagesSlice ParseMessagesSlice(ApiWrap$ParseMediaContext apiWrap$ParseMediaContext, ArrayList<TLRPC.Message> arrayList, ArrayList<TLRPC.User> arrayList2, ArrayList<TLRPC.Chat> arrayList3, String str) {
        ApiWrap$MessagesSlice apiWrap$MessagesSlice = new ApiWrap$MessagesSlice();
        int size = arrayList.size();
        while (size != 0) {
            size--;
            apiWrap$MessagesSlice.list.add(ParseMessage(apiWrap$ParseMediaContext, arrayList.get(size), str));
        }
        apiWrap$MessagesSlice.peers = ParsePeersLists(arrayList2, arrayList3);
        return apiWrap$MessagesSlice;
    }

    public static ApiWrap$Document ParseDocument(ApiWrap$ParseMediaContext apiWrap$ParseMediaContext, TLRPC.Document document, String str, int i) {
        ApiWrap$Document apiWrap$Document = new ApiWrap$Document();
        if (document instanceof TLRPC.TL_document) {
            TLRPC.TL_document tL_document = (TLRPC.TL_document) document;
            apiWrap$Document.id = tL_document.id;
            apiWrap$Document.date = tL_document.date;
            apiWrap$Document.mime = tL_document.mime_type;
            ParseAttributes(apiWrap$Document, tL_document.attributes);
            ApiWrap$File apiWrap$File = new ApiWrap$File();
            apiWrap$Document.file = apiWrap$File;
            apiWrap$File.size = tL_document.size;
            apiWrap$File.dcId = tL_document.dc_id;
            TLRPC.TL_inputDocumentFileLocation tL_inputDocumentFileLocation = new TLRPC.TL_inputDocumentFileLocation();
            tL_inputDocumentFileLocation.id = tL_document.id;
            tL_inputDocumentFileLocation.access_hash = tL_document.access_hash;
            tL_inputDocumentFileLocation.file_reference = tL_document.file_reference;
            tL_inputDocumentFileLocation.thumb_size = _UrlKt.FRAGMENT_ENCODE_SET;
            apiWrap$Document.file.location = new ApiWrap$FileLocation();
            ApiWrap$File apiWrap$File2 = apiWrap$Document.file;
            ApiWrap$FileLocation apiWrap$FileLocation = apiWrap$File2.location;
            apiWrap$FileLocation.data = tL_inputDocumentFileLocation;
            apiWrap$FileLocation.dcId = tL_document.dc_id;
            apiWrap$File2.suggestedPath = str + DocumentFolder(apiWrap$Document) + "/" + FileManager.fileNameFromUserString(ComputeDocumentName(apiWrap$ParseMediaContext, tL_document, i, apiWrap$Document.name));
            apiWrap$Document.thumb = ParseDocumentThumb(tL_document, apiWrap$Document.file.suggestedPath);
            if (MessageObject.isStickerDocument(tL_document)) {
                apiWrap$Document.sticker = tL_document;
                return apiWrap$Document;
            }
        } else if (document instanceof TLRPC.TL_documentEmpty) {
            apiWrap$Document.id = ((TLRPC.TL_documentEmpty) document).id;
        }
        return apiWrap$Document;
    }

    public static boolean RefreshFileReference(TLRPC.InputFileLocation inputFileLocation, TLRPC.InputFileLocation inputFileLocation2) {
        if (inputFileLocation.getClass() != inputFileLocation2.getClass()) {
            return false;
        }
        if (inputFileLocation instanceof TLRPC.TL_inputPhotoFileLocation) {
            if (inputFileLocation.id != inputFileLocation2.id || !Objects.equals(inputFileLocation.thumb_size, inputFileLocation2.thumb_size)) {
                return false;
            }
            inputFileLocation.file_reference = inputFileLocation2.file_reference;
            return true;
        }
        if (!(inputFileLocation instanceof TLRPC.TL_inputDocumentFileLocation) || inputFileLocation.id != inputFileLocation2.id || !Objects.equals(inputFileLocation.thumb_size, inputFileLocation2.thumb_size)) {
            return false;
        }
        inputFileLocation.file_reference = inputFileLocation2.file_reference;
        return true;
    }

    public static void ParseAttributes(ApiWrap$Document apiWrap$Document, ArrayList<TLRPC.DocumentAttribute> arrayList) {
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            TLRPC.DocumentAttribute documentAttribute = arrayList.get(i);
            i++;
            TLRPC.DocumentAttribute documentAttribute2 = documentAttribute;
            if (documentAttribute2 instanceof TLRPC.TL_documentAttributeImageSize) {
                TLRPC.TL_documentAttributeImageSize tL_documentAttributeImageSize = (TLRPC.TL_documentAttributeImageSize) documentAttribute2;
                apiWrap$Document.width = tL_documentAttributeImageSize.w;
                apiWrap$Document.height = tL_documentAttributeImageSize.h;
            } else if (documentAttribute2 instanceof TLRPC.TL_documentAttributeAnimated) {
                apiWrap$Document.isAnimated = true;
            } else if (documentAttribute2 instanceof TLRPC.TL_documentAttributeSticker) {
                apiWrap$Document.isSticker = true;
                apiWrap$Document.stickerEmoji = ((TLRPC.TL_documentAttributeSticker) documentAttribute2).alt;
            } else if (documentAttribute2 instanceof TLRPC.TL_documentAttributeCustomEmoji) {
                apiWrap$Document.isSticker = true;
                apiWrap$Document.stickerEmoji = ((TLRPC.TL_documentAttributeCustomEmoji) documentAttribute2).alt;
            } else if (documentAttribute2 instanceof TLRPC.TL_documentAttributeVideo) {
                TLRPC.TL_documentAttributeVideo tL_documentAttributeVideo = (TLRPC.TL_documentAttributeVideo) documentAttribute2;
                if (tL_documentAttributeVideo.round_message) {
                    apiWrap$Document.isVideoMessage = true;
                } else {
                    apiWrap$Document.isVideoFile = true;
                }
                apiWrap$Document.width = tL_documentAttributeVideo.w;
                apiWrap$Document.height = tL_documentAttributeVideo.h;
                apiWrap$Document.duration = (int) tL_documentAttributeVideo.duration;
            } else if (documentAttribute2 instanceof TLRPC.TL_documentAttributeAudio) {
                TLRPC.TL_documentAttributeAudio tL_documentAttributeAudio = (TLRPC.TL_documentAttributeAudio) documentAttribute2;
                if (tL_documentAttributeAudio.voice) {
                    apiWrap$Document.isVoiceMessage = true;
                } else {
                    apiWrap$Document.isAudioFile = true;
                }
                String str = tL_documentAttributeAudio.performer;
                if (str != null && !str.isEmpty()) {
                    apiWrap$Document.songPerformer = str;
                }
                String str2 = tL_documentAttributeAudio.title;
                if (str2 != null && !str2.isEmpty()) {
                    apiWrap$Document.songTitle = str2;
                }
                apiWrap$Document.duration = (int) tL_documentAttributeAudio.duration;
            } else if (documentAttribute2 instanceof TLRPC.TL_documentAttributeFilename) {
                apiWrap$Document.name = ((TLRPC.TL_documentAttributeFilename) documentAttribute2).file_name;
            }
        }
    }

    public static String ComputeDocumentName(ApiWrap$ParseMediaContext apiWrap$ParseMediaContext, final TLRPC.Document document, int i, String str) {
        if (document == null) {
            f$$ExternalSyntheticBUOutline1.m("trying to pass null document!!!");
            return null;
        }
        if (str != null && !str.isEmpty()) {
            return str;
        }
        String extensionFromMime = getExtensionFromMime(document.mime_type, document);
        new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return Boolean.valueOf(!document.mime_type.equalsIgnoreCase((String) obj));
            }
        };
        if (MessageObject.isVoiceDocument(document)) {
            boolean zEqualsIgnoreCase = document.mime_type.equalsIgnoreCase("audio/mp3");
            StringBuilder sb = new StringBuilder(MediaStreamTrack.AUDIO_TRACK_KIND);
            int i2 = apiWrap$ParseMediaContext.audios + 1;
            apiWrap$ParseMediaContext.audios = i2;
            sb.append(i2);
            sb.append(PrepareFileNameDatePart(i));
            sb.append(!zEqualsIgnoreCase ? ".mp3" : ".ogg");
            return sb.toString();
        }
        if (MessageObject.isVideoDocument(document)) {
            if (extensionFromMime.isEmpty()) {
                extensionFromMime = ".mov";
            }
            StringBuilder sb2 = new StringBuilder("video_");
            int i3 = apiWrap$ParseMediaContext.videos + 1;
            apiWrap$ParseMediaContext.videos = i3;
            sb2.append(i3);
            sb2.append(PrepareFileNameDatePart(i));
            sb2.append(extensionFromMime);
            return sb2.toString();
        }
        if (extensionFromMime.isEmpty()) {
            extensionFromMime = ".unknown";
        }
        StringBuilder sb3 = new StringBuilder("file_");
        int i4 = apiWrap$ParseMediaContext.files + 1;
        apiWrap$ParseMediaContext.files = i4;
        sb3.append(i4);
        sb3.append(PrepareFileNameDatePart(i));
        sb3.append(extensionFromMime);
        return sb3.toString();
    }

    private static String getExtensionFromMime(String str, TLRPC.Document document) {
        if (Objects.equals(str, "image/webp")) {
            return ".webp";
        }
        if (Objects.equals(str, "application/x-tgsticker")) {
            return ".tgs";
        }
        if (Objects.equals(str, "application/x-tgwallpattern")) {
            return ".tgv";
        }
        if (Objects.equals(str, "application/x-tdesktop-theme") || Objects.equals(str, "application/x-tgtheme-tdesktop")) {
            return ".tdesktop-theme";
        }
        if (Objects.equals(str, "application/x-tdesktop-palette")) {
            return ".tdesktop-palette";
        }
        if (Objects.equals(str, "video/mp4")) {
            return ".mp4";
        }
        if (Objects.equals(str, "audio/ogg")) {
            return ".ogg";
        }
        return ".unknown";
    }

    private static String DocumentFolder(ApiWrap$Document apiWrap$Document) {
        if (apiWrap$Document.isVideoFile) {
            return "video_files";
        }
        if (apiWrap$Document.isAnimated) {
            return "animations";
        }
        if (apiWrap$Document.isSticker) {
            return "stickers";
        }
        if (apiWrap$Document.isVoiceMessage) {
            return "voice_messages";
        }
        if (apiWrap$Document.isVideoMessage) {
            return "round_video_messages";
        }
        return "files";
    }

    private static long getArea(TLRPC.PhotoSize photoSize) {
        if ((photoSize instanceof TLRPC.TL_photoSizeEmpty) || (photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoPathSize)) {
            return 0L;
        }
        return ((long) photoSize.w) * ((long) photoSize.h);
    }

    private static ApiWrap$Image ParseDocumentThumb(TLRPC.Document document, String str) {
        ArrayList<TLRPC.PhotoSize> arrayList = document.thumbs;
        if (arrayList.isEmpty()) {
            return new ApiWrap$Image();
        }
        int size = arrayList.size();
        TLRPC.PhotoSize photoSize = null;
        long area = Long.MIN_VALUE;
        int i = 0;
        while (i < size) {
            TLRPC.PhotoSize photoSize2 = arrayList.get(i);
            i++;
            TLRPC.PhotoSize photoSize3 = photoSize2;
            if (getArea(photoSize3) > area) {
                area = getArea(photoSize3);
                photoSize = photoSize3;
            }
        }
        if (photoSize == null) {
            return new ApiWrap$Image();
        }
        if ((photoSize instanceof TLRPC.TL_photoSizeEmpty) || (photoSize instanceof TLRPC.TL_photoStrippedSize) || (photoSize instanceof TLRPC.TL_photoPathSize)) {
            return new ApiWrap$Image();
        }
        ApiWrap$Image apiWrap$Image = new ApiWrap$Image();
        apiWrap$Image.width = photoSize.w;
        apiWrap$Image.height = photoSize.h;
        TLRPC.TL_inputDocumentFileLocation tL_inputDocumentFileLocation = new TLRPC.TL_inputDocumentFileLocation();
        tL_inputDocumentFileLocation.id = document.id;
        tL_inputDocumentFileLocation.access_hash = document.access_hash;
        tL_inputDocumentFileLocation.file_reference = document.file_reference;
        tL_inputDocumentFileLocation.thumb_size = photoSize.type;
        apiWrap$Image.file.location = new ApiWrap$FileLocation();
        ApiWrap$File apiWrap$File = apiWrap$Image.file;
        ApiWrap$FileLocation apiWrap$FileLocation = apiWrap$File.location;
        apiWrap$FileLocation.data = tL_inputDocumentFileLocation;
        apiWrap$FileLocation.dcId = document.dc_id;
        if (photoSize instanceof TLRPC.TL_photoCachedSize) {
            TLRPC.TL_photoCachedSize tL_photoCachedSize = (TLRPC.TL_photoCachedSize) photoSize;
            apiWrap$File.content = tL_photoCachedSize.bytes;
            apiWrap$File.size = tL_photoCachedSize.size;
        } else if (photoSize instanceof TLRPC.TL_photoSizeProgressive) {
            TLRPC.TL_photoSizeProgressive tL_photoSizeProgressive = (TLRPC.TL_photoSizeProgressive) photoSize;
            if (tL_photoSizeProgressive.sizes.isEmpty()) {
                return new ApiWrap$Image();
            }
            ApiWrap$File apiWrap$File2 = apiWrap$Image.file;
            apiWrap$File2.content = new byte[0];
            ArrayList<Integer> arrayList2 = tL_photoSizeProgressive.sizes;
            apiWrap$File2.size = arrayList2.get(arrayList2.size() - 1).intValue();
        } else {
            apiWrap$File.content = new byte[0];
            apiWrap$File.size = document.size;
        }
        apiWrap$Image.file.suggestedPath = str + "_thumb.jpg";
        return apiWrap$Image;
    }

    public static boolean SkipMessageByDate(ApiWrap$Message apiWrap$Message, ExportSettings exportSettings) {
        int i = exportSettings.singlePeerFrom;
        boolean z = i <= 0 || i <= apiWrap$Message.date;
        int i2 = exportSettings.singlePeerTill;
        return (z && (i2 <= 0 || apiWrap$Message.date < i2)) ? false : true;
    }

    public static boolean SingleMessageBefore(TLRPC.messages_Messages messages_messages, int i) {
        int iSingleMessageDate = SingleMessageDate(messages_messages);
        return iSingleMessageDate > 0 && iSingleMessageDate < i;
    }

    public static ApiWrap$MessagesSlice AdjustMigrateMessageIds(ApiWrap$MessagesSlice apiWrap$MessagesSlice) {
        ArrayList<ApiWrap$Message> arrayList = apiWrap$MessagesSlice.list;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            ApiWrap$Message apiWrap$Message = arrayList.get(i);
            i++;
            ApiWrap$Message apiWrap$Message2 = apiWrap$Message;
            apiWrap$Message2.id -= 1000000000;
            int i2 = apiWrap$Message2.replyToMsgId;
            if (i2 != 0 && apiWrap$Message2.replyToPeerId == 0) {
                apiWrap$Message2.replyToMsgId = i2 - 1000000000;
            }
        }
        return apiWrap$MessagesSlice;
    }

    public static String ComposeName(HtmlWriter.UserpicData userpicData, String str) {
        if (userpicData.firstName.isEmpty() && userpicData.lastName.isEmpty()) {
            return str;
        }
        return userpicData.firstName + ' ' + userpicData.lastName;
    }

    public static int ApplicationColorIndex(int i) {
        Integer num = new HashMap<Integer, Integer>() { 
            {
                put(1, 0);
                put(7, 0);
                put(6, 1);
                put(21724, 1);
                put(2834, 2);
                put(2496, 3);
                put(2040, 4);
                put(1429, 5);
            }
        }.get(Integer.valueOf(i));
        if (num != null) {
            return num.intValue();
        }
        return PeerColorIndex(i);
    }

    public static ApiWrap$User ParseUser(TLRPC.User user) {
        int iPeerColorIndex;
        ApiWrap$User apiWrap$User = new ApiWrap$User();
        apiWrap$User.info = ParseContactInfo(user);
        if (user instanceof TLRPC.TL_user) {
            TLRPC.TL_user tL_user = (TLRPC.TL_user) user;
            Long lValueOf = Long.valueOf(tL_user.id);
            apiWrap$User.bareId = lValueOf;
            TLRPC.PeerColor peerColor = tL_user.color;
            if (peerColor == null || (iPeerColorIndex = peerColor.color) == 0) {
                iPeerColorIndex = PeerColorIndex(lValueOf.longValue());
            }
            apiWrap$User.colorIndex = iPeerColorIndex;
            apiWrap$User.username = tL_user.username;
            apiWrap$User.isBot = tL_user.bot;
            apiWrap$User.isSelf = tL_user.self;
            long j = tL_user.id;
            apiWrap$User.isReplies = j == UserObject.REPLY_BOT;
            apiWrap$User.isVerifyCodes = j == UserObject.VERIFY;
            TLRPC.TL_inputUser tL_inputUser = new TLRPC.TL_inputUser();
            tL_inputUser.user_id = tL_user.id;
            tL_inputUser.access_hash = tL_user.access_hash;
            apiWrap$User.input = tL_inputUser;
            return apiWrap$User;
        }
        if (user instanceof TLRPC.TL_userEmpty) {
            TLRPC.TL_inputUser tL_inputUser2 = new TLRPC.TL_inputUser();
            tL_inputUser2.user_id = ((TLRPC.TL_userEmpty) user).id;
            tL_inputUser2.access_hash = 0L;
            apiWrap$User.input = tL_inputUser2;
        }
        return apiWrap$User;
    }

    public static ApiWrap$ContactInfo ParseContactInfo(TLRPC.User user) {
        int iPeerColorIndex;
        ApiWrap$ContactInfo apiWrap$ContactInfo = new ApiWrap$ContactInfo();
        if (user instanceof TLRPC.TL_user) {
            TLRPC.TL_user tL_user = (TLRPC.TL_user) user;
            Long lValueOf = Long.valueOf(tL_user.id);
            apiWrap$ContactInfo.userId = lValueOf;
            TLRPC.PeerColor peerColor = tL_user.color;
            if (peerColor == null || (iPeerColorIndex = peerColor.color) == 0) {
                iPeerColorIndex = PeerColorIndex(lValueOf.longValue());
            }
            apiWrap$ContactInfo.colorIndex = iPeerColorIndex;
            String str = tL_user.first_name;
            String str2 = _UrlKt.FRAGMENT_ENCODE_SET;
            if (str == null) {
                str = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            apiWrap$ContactInfo.firstName = str;
            String str3 = tL_user.last_name;
            if (str3 == null) {
                str3 = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            apiWrap$ContactInfo.lastName = str3;
            String str4 = tL_user.phone;
            if (str4 != null) {
                str2 = str4;
            }
            apiWrap$ContactInfo.phoneNumber = str2;
            return apiWrap$ContactInfo;
        }
        if (user instanceof TLRPC.TL_userEmpty) {
            Long lValueOf2 = Long.valueOf(((TLRPC.TL_userEmpty) user).id);
            apiWrap$ContactInfo.userId = lValueOf2;
            apiWrap$ContactInfo.colorIndex = PeerColorIndex(lValueOf2.longValue());
        }
        return apiWrap$ContactInfo;
    }

    public static boolean messageNeedsWrap(ApiWrap$Message apiWrap$Message, HtmlWriter.MessageInfo messageInfo) {
        if (messageInfo == null || messageInfo.type != HtmlWriter.MessageInfo.Type.Default) {
            return true;
        }
        long j = apiWrap$Message.fromId;
        if (j != 0 && messageInfo.fromId == j && apiWrap$Message.viaBotId == messageInfo.viaBotId && LocaleController.getInstance().getFormatterYear().format(((long) messageInfo.date) * 1000).trim().equals(LocaleController.getInstance().getFormatterYear().format(((long) apiWrap$Message.date) * 1000).trim()) && apiWrap$Message.forwarded == messageInfo.forwarded && apiWrap$Message.showForwardedAsOriginal == messageInfo.showForwardedAsOriginal && apiWrap$Message.forwardedFromId == messageInfo.forwardedFromId && Objects.equals(apiWrap$Message.forwardedFromName, messageInfo.forwardedFromName)) {
            return Math.abs(apiWrap$Message.date - messageInfo.date) > (((apiWrap$Message.forwardedFromId > 0L ? 1 : (apiWrap$Message.forwardedFromId == 0L ? 0 : -1)) != 0 || !apiWrap$Message.forwardedFromName.isEmpty()) ? 1 : 900);
        }
        return true;
    }

    public static boolean forwardedNeedsWrap(ApiWrap$Message apiWrap$Message, HtmlWriter.MessageInfo messageInfo) {
        if (messageNeedsWrap(apiWrap$Message, messageInfo)) {
            return true;
        }
        long j = apiWrap$Message.forwardedFromId;
        return j == 0 || j != messageInfo.forwardedFromId || ChatUtils.getInstance().getMessageStorage().getUser(apiWrap$Message.forwardedFromId) == null || Math.abs(apiWrap$Message.forwardedDate - messageInfo.forwardedDate) > 900;
    }

    public static String FormatText(ArrayList<ApiWrap$TextPart> arrayList, final String str, final String str2) {
        return (String) arrayList.stream().map(new Function() { 
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return DataTypesUtils.$r8$lambda$YnixTTMCGZSy3zr12T7r1r9bYeo(str, str2, (ApiWrap$TextPart) obj);
            }
        }).collect(Collectors.joining());
    }

    public static void $r8$lambda$vQE9eoidmhRnt1aHYXJ5PfuJR64(HashMap map, ArrayList arrayList, ArrayList arrayList2) {
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList2.get(i);
            i++;
            TLRPC.TL_topPeer tL_topPeer = (TLRPC.TL_topPeer) obj;
            ApiWrap$TopPeer apiWrap$TopPeer = new ApiWrap$TopPeer();
            ApiWrap$Peer apiWrap$PeerEmptyPeer = (ApiWrap$Peer) map.get(Long.valueOf(MessageObject.getPeerId(tL_topPeer.peer)));
            if (apiWrap$PeerEmptyPeer == null) {
                apiWrap$PeerEmptyPeer = EmptyPeer(tL_topPeer.peer);
            }
            apiWrap$TopPeer.peer = apiWrap$PeerEmptyPeer;
            apiWrap$TopPeer.rating = tL_topPeer.rating;
            arrayList.add(apiWrap$TopPeer);
        }
    }

    public static ArrayList<Integer> SortedContactsIndices(ApiWrap$ContactsList apiWrap$ContactsList) {
        int size = apiWrap$ContactsList.list.size();
        final ArrayList arrayList = new ArrayList(size);
        ArrayList<ApiWrap$ContactInfo> arrayList2 = apiWrap$ContactsList.list;
        int size2 = arrayList2.size();
        int i = 0;
        while (i < size2) {
            ApiWrap$ContactInfo apiWrap$ContactInfo = arrayList2.get(i);
            i++;
            ApiWrap$ContactInfo apiWrap$ContactInfo2 = apiWrap$ContactInfo;
            arrayList.add((apiWrap$ContactInfo2.firstName + " " + apiWrap$ContactInfo2.lastName).toLowerCase());
        }
        ArrayList<Integer> arrayList3 = new ArrayList<>(size);
        for (int i2 = 0; i2 < size; i2++) {
            arrayList3.add(Integer.valueOf(i2));
        }
        Collections.sort(arrayList3, Comparator.comparing(new Function() { 
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return DataTypesUtils.$r8$lambda$hVjdozXZbebV18GIZWQWbnIt_UI(arrayList, (Integer) obj);
            }
        }));
        return arrayList3;
    }

    public static /* synthetic */ String $r8$lambda$hVjdozXZbebV18GIZWQWbnIt_UI(ArrayList arrayList, Integer num) {
        return (String) arrayList.get(num.intValue());
    }
}
