package com.exteragram.messenger.export.output.json;

import android.support.v4.media.session.MediaSessionCompat$$ExternalSyntheticThrowCCEIfNotNull0;
import android.util.Base64;
import android.util.Pair;
import com.android.dx.dex.code.CstInsn$$ExternalSyntheticBUOutline0;
import com.exteragram.messenger.export.api.ApiWrap$ActionChatEditPhoto;
import com.exteragram.messenger.export.api.ApiWrap$ActionSuggestProfilePhoto;
import com.exteragram.messenger.export.api.ApiWrap$DialogInfo;
import com.exteragram.messenger.export.api.ApiWrap$Document;
import com.exteragram.messenger.export.api.ApiWrap$File;
import com.exteragram.messenger.export.api.ApiWrap$Game;
import com.exteragram.messenger.export.api.ApiWrap$GeoPoint;
import com.exteragram.messenger.export.api.ApiWrap$GiveawayResults;
import com.exteragram.messenger.export.api.ApiWrap$GiveawayStart;
import com.exteragram.messenger.export.api.ApiWrap$HistoryMessageMarkupButton;
import com.exteragram.messenger.export.api.ApiWrap$Image;
import com.exteragram.messenger.export.api.ApiWrap$Invoice;
import com.exteragram.messenger.export.api.ApiWrap$Media;
import com.exteragram.messenger.export.api.ApiWrap$Message;
import com.exteragram.messenger.export.api.ApiWrap$PaidMedia;
import com.exteragram.messenger.export.api.ApiWrap$Peer;
import com.exteragram.messenger.export.api.ApiWrap$Poll;
import com.exteragram.messenger.export.api.ApiWrap$Reaction;
import com.exteragram.messenger.export.api.ApiWrap$SharedContact;
import com.exteragram.messenger.export.api.ApiWrap$TextPart;
import com.exteragram.messenger.export.api.ApiWrap$UnsupportedMedia;
import com.exteragram.messenger.export.api.ApiWrap$User;
import com.exteragram.messenger.export.api.ApiWrap$Venue;
import com.exteragram.messenger.export.api.DataTypesUtils;
import com.exteragram.messenger.export.output.AbstractWriter;
import com.exteragram.messenger.export.output.OutputFile;
import com.exteragram.messenger.export.output.html.HtmlContext$$ExternalSyntheticBackport1;
import com.exteragram.messenger.export.output.html.HtmlWriter;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.google.android.gms.cast.MediaTrack;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import kotlin.text.Typography;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.TLRPC;

public class JsonContext {
    protected OutputFile _file;
    public boolean _currentNestingHadItem = false;
    public ArrayList<Boolean> nesting = new ArrayList<>();

    public JsonContext(OutputFile outputFile) {
        this._file = outputFile;
    }

    public static String SerializeMessage(final JsonContext jsonContext, final ApiWrap$Message apiWrap$Message, final HashMap<Long, ApiWrap$Peer> map, String str) {
        String str2;
        if (apiWrap$Message.media.content instanceof ApiWrap$UnsupportedMedia) {
            return SerializeObject(jsonContext, new Pair("id", Integer.valueOf(apiWrap$Message.id)), new Pair(TeXSymbolParser.TYPE_ATTR, SerializeString("unsupported")));
        }
        final Utilities.CallbackReturn callbackReturn = new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return JsonContext.$r8$lambda$_82XuOS42idlN6YRygYUGxZazmU(map, (Long) obj);
            }
        };
        Utilities.CallbackReturn callbackReturn2 = new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return JsonContext.m1095$r8$lambda$Lb75cXPDJ068NTmfGbGV7wsayQ(callbackReturn, (Long) obj);
            }
        };
        final ArrayList arrayList = new ArrayList();
        arrayList.add(new Pair("id", Integer.valueOf(apiWrap$Message.id)));
        arrayList.add(new Pair(TeXSymbolParser.TYPE_ATTR, SerializeString(apiWrap$Message.action != null ? "service" : "message")));
        arrayList.add(new Pair("date", SerializeString(String.valueOf(apiWrap$Message.date))));
        jsonContext.nesting.add(Boolean.TRUE);
        Utilities.CallbackVoidReturn callbackVoidReturn = new Utilities.CallbackVoidReturn() { 
            @Override 
            public final Object run() {
                return JsonContext.$r8$lambda$5FFhJI72fHDyOjZRMyMtJGXGYXw(this.f$0, arrayList);
            }
        };
        final Utilities.Callback2 callback2 = new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                JsonContext.$r8$lambda$Yw3sshGVvjPkXNdtDtvTvKbj2BM(arrayList, (String) obj, (String) obj2);
            }
        };
        int i = apiWrap$Message.edited;
        if (i != 0) {
            callback2.run("edited", String.valueOf(i));
        }
        final Utilities.CallbackReturn callbackReturn3 = new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return JsonContext.$r8$lambda$nuaLfrTKf9UaHQJH7HeIdHHHYPs((Long) obj);
            }
        };
        final Utilities.Callback2 callback3 = new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                JsonContext.$r8$lambda$E9u9TYVlppS2vBxijZACzBTyoIE(callback2, callbackReturn3, (String) obj, obj2);
            }
        };
        final Utilities.CallbackReturn callbackReturn4 = new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return JsonContext.StringAllowNull(((ApiWrap$Peer) callbackReturn.run((Long) obj)).name());
            }
        };
        Utilities.Callback callback = new Utilities.Callback() { 
            @Override 
            public final void run(Object obj) {
                JsonContext.$r8$lambda$oOh1TbUsZyqtWEJs5VUnyheqEho(apiWrap$Message, callback2, callbackReturn4, callback3, (String) obj);
            }
        };
        Utilities.Callback callback4 = new Utilities.Callback() { 
            @Override 
            public final void run(Object obj) {
                JsonContext.$r8$lambda$6HhGPqLT_zAglGl3BSE0SMSNzeo(apiWrap$Message, callback3, (String) obj);
            }
        };
        ApiWrap$Media apiWrap$Media = apiWrap$Message.media;
        if (apiWrap$Media != null && apiWrap$Media.content != null) {
            arrayList.add(new Pair("media", SerializeMessageMedia(apiWrap$Message, jsonContext, map, str)));
        }
        if (apiWrap$Message.action != null) {
            arrayList.add(new Pair("action", SerializeMessageAction(jsonContext, apiWrap$Message.action, apiWrap$Message, map)));
        }
        if (apiWrap$Message.action == null) {
            callback.run("from");
            callback3.run("author", apiWrap$Message.signature);
            long j = apiWrap$Message.forwardedFromId;
            if (j != 0) {
                callback2.run("forwarded_from", (String) callbackReturn4.run(Long.valueOf(j)));
            } else {
                String str3 = apiWrap$Message.forwardedFromName;
                if (str3 != null && !str3.isEmpty()) {
                    callback2.run("forwarded_from", StringAllowNull(apiWrap$Message.forwardedFromName));
                }
            }
            long j2 = apiWrap$Message.savedFromChatId;
            if (j2 != 0) {
                callback2.run("saved_from", (String) callbackReturn4.run(Long.valueOf(j2)));
            }
            callback4.run("reply_to_message_id");
            long j3 = apiWrap$Message.viaBotId;
            if (j3 != 0 && (str2 = ((ApiWrap$User) callbackReturn2.run(Long.valueOf(j3))).username) != null && !str2.isEmpty()) {
                callback3.run("via_bot", str2);
            }
        }
        callback2.run("text_entities", SerializeText(jsonContext, apiWrap$Message.text, true));
        ArrayList<ArrayList<ApiWrap$HistoryMessageMarkupButton>> arrayList2 = apiWrap$Message.inlineButtonRows;
        if (arrayList2 != null && !arrayList2.isEmpty()) {
            final Utilities.CallbackReturn callbackReturn5 = new Utilities.CallbackReturn() { 
                @Override 
                public final Object run(Object obj) {
                    return JsonContext.$r8$lambda$lKrxcF5Xyg1WzKTSgzvku1TscPg(this.f$0, (ArrayList) obj);
                }
            };
            jsonContext.nesting.add(Boolean.FALSE);
            List list = (List) apiWrap$Message.inlineButtonRows.stream().map(new Function() { 
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return JsonContext.m1096$r8$lambda$hmtII8_x5IKMyyqN62IoDyQTf4(callbackReturn5, (ArrayList) obj);
                }
            }).collect(Collectors.toList());
            ArrayList<Boolean> arrayList3 = jsonContext.nesting;
            arrayList3.remove(arrayList3.size() - 1);
            callback2.run("inline_bot_buttons", SerializeArray(jsonContext, new ArrayList(list)));
        }
        if (!apiWrap$Message.reactions.isEmpty()) {
            final Utilities.CallbackReturn callbackReturn6 = new Utilities.CallbackReturn() { 
                @Override 
                public final Object run(Object obj) {
                    JsonContext jsonContext2 = this.f$0;
                    Utilities.CallbackReturn callbackReturn7 = callbackReturn4;
                    Utilities.CallbackReturn callbackReturn8 = callbackReturn3;
                    MediaSessionCompat$$ExternalSyntheticThrowCCEIfNotNull0.m(obj);
                    return JsonContext.$r8$lambda$JSGl0gcx13Gm7REM9tQp0tkWgrM(jsonContext2, callbackReturn7, callbackReturn8, null);
                }
            };
            jsonContext.nesting.add(Boolean.FALSE);
            callback2.run("reactions", SerializeArray(jsonContext, new ArrayList((List) apiWrap$Message.reactions.stream().map(new Function() { 
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    Utilities.CallbackReturn callbackReturn7 = callbackReturn6;
                    MediaSessionCompat$$ExternalSyntheticThrowCCEIfNotNull0.m(obj);
                    return JsonContext.$r8$lambda$pklfCh8IPhV8pOoKh1sadePkqzY(callbackReturn7, null);
                }
            }).collect(Collectors.toList()))));
            ArrayList<Boolean> arrayList4 = jsonContext.nesting;
            arrayList4.remove(arrayList4.size() - 1);
        }
        return (String) callbackVoidReturn.run();
    }

    public static String m1100$r8$lambda$z_7ew4tZrjahCzTlJ65jxaK0v0(JsonContext jsonContext, ApiWrap$HistoryMessageMarkupButton apiWrap$HistoryMessageMarkupButton) {
        ArrayList arrayList = new ArrayList();
        arrayList.add(new Pair(TeXSymbolParser.TYPE_ATTR, SerializeString(ApiWrap$HistoryMessageMarkupButton.TypeToString(apiWrap$HistoryMessageMarkupButton))));
        if (!apiWrap$HistoryMessageMarkupButton.text().isEmpty()) {
            arrayList.add(new Pair("text", SerializeString(apiWrap$HistoryMessageMarkupButton.text())));
        }
        if (apiWrap$HistoryMessageMarkupButton.data() != null && apiWrap$HistoryMessageMarkupButton.data().length != 0) {
            if (apiWrap$HistoryMessageMarkupButton.type() == ApiWrap$HistoryMessageMarkupButton.Type.Callback || apiWrap$HistoryMessageMarkupButton.type() == ApiWrap$HistoryMessageMarkupButton.Type.CallbackWithPassword) {
                arrayList.add(new Pair("dataBase64", SerializeString(ChatUtils.getInstance().getTextFromCallback(apiWrap$HistoryMessageMarkupButton.data()))));
                arrayList.add(new Pair("data", SerializeString(_UrlKt.FRAGMENT_ENCODE_SET)));
            } else {
                arrayList.add(new Pair("data", SerializeString(ChatUtils.getInstance().getTextFromCallback(apiWrap$HistoryMessageMarkupButton.data()))));
            }
        }
        if (apiWrap$HistoryMessageMarkupButton.forwardText() != null && !apiWrap$HistoryMessageMarkupButton.forwardText().isEmpty()) {
            arrayList.add(new Pair("forward_text", SerializeString(apiWrap$HistoryMessageMarkupButton.forwardText())));
        }
        if (apiWrap$HistoryMessageMarkupButton.buttonId() != 0) {
            arrayList.add(new Pair("button_id", DataTypesUtils.NumberToString(apiWrap$HistoryMessageMarkupButton.buttonId())));
        }
        return SerializeObject(jsonContext, (Pair[]) arrayList.toArray(new Pair[arrayList.size()]));
    }

    public static void $r8$lambda$fua3ETEVNn_xpfDnppx9CUbOjao(ArrayList arrayList, String str, String str2) {
        if (str2.isEmpty()) {
            return;
        }
        arrayList.add(new Pair(str, str2));
    }

    public static ApiWrap$Peer $r8$lambda$HcPXe2yaCyXRahCOHGZF0WKJQiM(HashMap map, Long l) {
        ApiWrap$Peer apiWrap$Peer = (ApiWrap$Peer) map.get(l);
        return apiWrap$Peer != null ? apiWrap$Peer : new ApiWrap$Peer(new ApiWrap$User());
    }

    public static /* synthetic */ ApiWrap$User m1097$r8$lambda$l3kw5Q6evJjim0J_zsiqvwknVk(Utilities.CallbackReturn callbackReturn, Long l) {
        ApiWrap$User apiWrap$User = ((ApiWrap$Peer) callbackReturn.run(l)).user;
        return apiWrap$User != null ? apiWrap$User : new ApiWrap$User();
    }

    public static /* synthetic */ void $r8$lambda$DJcpDpppJ9SASMoVONnleTMyrRE(ArrayList arrayList, String str, String str2) {
        if (str2.isEmpty()) {
            return;
        }
        arrayList.add(new Pair(str, str2));
    }

    public static /* synthetic */ String $r8$lambda$zTj1WpUulSBk65CIRhljeFYUJSk(Long l) {
        if (l.longValue() < 0) {
            return SerializeString("chat" + l);
        }
        return SerializeString("user" + l);
    }

    public static /* synthetic */ void $r8$lambda$ITtAik57S_FX3F_idDa0ObsjTEY(Utilities.Callback2 callback2, Utilities.CallbackReturn callbackReturn, String str, Object obj) {
        if (obj instanceof Boolean) {
            callback2.run(str, String.valueOf((Boolean) obj));
            return;
        }
        if (obj instanceof Integer) {
            callback2.run(str, String.valueOf((Integer) obj));
            return;
        }
        if (obj instanceof Long) {
            callback2.run(str, String.valueOf((Long) obj));
            return;
        }
        if (obj instanceof String) {
            String str2 = (String) obj;
            if (!str2.isEmpty()) {
                callback2.run(str, SerializeString(str2));
                return;
            }
        }
        if (obj instanceof TLRPC.Peer) {
            callback2.run(str, (String) callbackReturn.run(Long.valueOf(MessageObject.getPeerId((TLRPC.Peer) obj))));
        }
    }

    public static /* synthetic */ void $r8$lambda$hw2aHHYioc0DYTvaVxxcyJjnUJs(ApiWrap$Message apiWrap$Message, Utilities.Callback2 callback2, Utilities.CallbackReturn callbackReturn, Utilities.Callback2 callback3, String str) {
        if (str == null) {
            str = "from";
        }
        long j = apiWrap$Message.fromId;
        if (j != 0) {
            callback2.run(str, (String) callbackReturn.run(Long.valueOf(j)));
            TLRPC.TL_peerUser tL_peerUser = new TLRPC.TL_peerUser();
            tL_peerUser.user_id = apiWrap$Message.fromId;
            callback3.run(str.concat("_id"), tL_peerUser);
        }
    }

    public static /* synthetic */ void $r8$lambda$V1GPUI3Ji8IitWs016dE88zBqRg(ApiWrap$Message apiWrap$Message, Utilities.Callback2 callback2, String str) {
        int i = apiWrap$Message.replyToMsgId;
        if (i != 0) {
            callback2.run(str, Integer.valueOf(i));
            long j = apiWrap$Message.replyToPeerId;
            if (j != 0) {
                callback2.run("reply_to_peer_id", Long.valueOf(j));
            }
        }
    }

    public static /* synthetic */ void m1099$r8$lambda$qketphKXD6umlaSqbPSIjrbZ8c(Utilities.CallbackReturn callbackReturn, Utilities.Callback2 callback2, JsonContext jsonContext, ArrayList arrayList, String str) {
        ArrayList arrayList2 = new ArrayList();
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            arrayList2.add(StringAllowNull(((ApiWrap$User) callbackReturn.run((Long) obj)).name()));
        }
        callback2.run(str, SerializeArray(jsonContext, arrayList2));
    }

    public static /* synthetic */ void $r8$lambda$j7sKj_P7pujVBJlz8XEZE23lUvo(Utilities.Callback2 callback2, ApiWrap$File apiWrap$File, String str, String str2) {
        boolean zIsEmpty = str2.isEmpty();
        String strConcat = _UrlKt.FRAGMENT_ENCODE_SET;
        String strConcat2 = zIsEmpty ? _UrlKt.FRAGMENT_ENCODE_SET : str2.concat(" ");
        int i = AnonymousClass1.$SwitchMap$com$exteragram$messenger$export$api$ApiWrap$File$SkipReason[apiWrap$File.skipReason.ordinal()];
        if (i == 1) {
            strConcat = strConcat2.concat("(File unavailable, please try again later)");
        } else if (i == 2) {
            strConcat = strConcat2.concat("(File exceeds maximum size. Change data exporting settings to download.)");
        } else if (i == 3) {
            strConcat = strConcat2.concat("(File not included. Change data exporting settings to download.)");
        } else if (i == 4) {
            strConcat = apiWrap$File.relativePath;
        }
        callback2.run(str, strConcat);
    }

    public static /* synthetic */ void m1091$r8$lambda$0JEATGwgrDcrUlHLnU4tlxdjgY(Utilities.Callback3 callback3, Utilities.Callback2 callback2, ApiWrap$Image apiWrap$Image) {
        callback3.run(apiWrap$Image.file, "photo", _UrlKt.FRAGMENT_ENCODE_SET);
        int i = apiWrap$Image.width;
        if (i == 0 || apiWrap$Image.height == 0) {
            return;
        }
        callback2.run("width", Integer.valueOf(i));
        callback2.run("height", Integer.valueOf(apiWrap$Image.height));
    }

    public static String SerializeObject(JsonContext jsonContext, Pair<String, String>... pairArr) {
        String strIndentation = Indentation(jsonContext);
        jsonContext.nesting.add(Boolean.TRUE);
        String str = "\n" + Indentation(jsonContext);
        StringBuilder sb = new StringBuilder("{");
        boolean z = true;
        for (Pair<String, String> pair : pairArr) {
            String str2 = (String) pair.first;
            String strValueOf = String.valueOf(pair.second);
            if (!strValueOf.isEmpty()) {
                if (z) {
                    z = false;
                } else {
                    sb.append(',');
                }
                sb.append(str);
                sb.append(SerializeString(str2));
                sb.append(": ");
                sb.append(strValueOf);
            }
        }
        sb.append('\n');
        sb.append(strIndentation);
        sb.append("}");
        try {
            return sb.toString();
        } finally {
            jsonContext.nesting.remove(jsonContext.nesting.size() - 1);
        }
    }

    public static String SerializeString(String str) {
        int i;
        String str2 = str + str.length();
        ArrayList arrayList = new ArrayList();
        for (char c2 : str.toCharArray()) {
            arrayList.add(Character.valueOf(c2));
        }
        StringBuilder sb = new StringBuilder("\"");
        for (int i2 = 0; i2 < arrayList.size(); i2++) {
            Character ch = (Character) arrayList.get(i2);
            if (ch.charValue() == '\n') {
                sb.append("\\n");
            } else if (ch.charValue() == '\r') {
                sb.append("\\r");
            } else if (ch.charValue() == '\t') {
                sb.append("\\t");
            } else if (ch.charValue() == '\"') {
                sb.append("\\\"");
            } else if (ch.charValue() == '\\') {
                sb.append("\\\\");
            } else if (ch.charValue() >= 0 && ch.charValue() < ' ') {
                sb.append("\\x");
                sb.append((ch.charValue() >> 4) + 48);
                int iCharValue = ch.charValue() & 15;
                if (iCharValue >= 10) {
                    sb.append(iCharValue + 55);
                } else {
                    sb.append(iCharValue + 48);
                }
            } else if (ch.charValue() != 226 || (i = i2 + 2) >= str2.length() || i2 + 1 != 128) {
                sb.append(ch);
            } else if (i == 168) {
                sb.append("\\u2028");
            } else if (i == 169) {
                sb.append("\\u2029");
            } else {
                sb.append(ch);
            }
        }
        sb.append(Typography.quote);
        return sb.toString();
    }

    public static String Indentation(JsonContext jsonContext) {
        return Indentation(jsonContext.nesting.size());
    }

    public static String Indentation(int i) {
        return HtmlContext$$ExternalSyntheticBackport1.m(" ", i);
    }

    public static String SerializeArray(JsonContext jsonContext, ArrayList<String> arrayList) {
        String strIndentation = Indentation(jsonContext.nesting.size());
        StringBuilder sb = new StringBuilder("\n");
        boolean z = true;
        sb.append(Indentation(jsonContext.nesting.size() + 1));
        String string = sb.toString();
        StringBuilder sb2 = new StringBuilder("[");
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            String str = arrayList.get(i);
            i++;
            String str2 = str;
            if (z) {
                z = false;
            } else {
                sb2.append(',');
            }
            sb2.append(string);
            sb2.append(str2);
        }
        sb2.append('\n');
        sb2.append(strIndentation);
        sb2.append("]");
        return sb2.toString();
    }

    public static String StringAllowNull(String str) {
        return (str == null || str.isEmpty()) ? "null" : SerializeString(str);
    }

    public static String SerializeText(JsonContext jsonContext, ArrayList<ApiWrap$TextPart> arrayList, boolean z) {
        String str;
        String strSerializeString;
        if (arrayList.isEmpty()) {
            return z ? _UrlKt.PATH_SEGMENT_ENCODE_SET_URI : SerializeString(_UrlKt.FRAGMENT_ENCODE_SET);
        }
        jsonContext.nesting.add(Boolean.FALSE);
        ArrayList arrayList2 = new ArrayList();
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            ApiWrap$TextPart apiWrap$TextPart = arrayList.get(i);
            i++;
            ApiWrap$TextPart apiWrap$TextPart2 = apiWrap$TextPart;
            ApiWrap$TextPart.Type type = apiWrap$TextPart2.type;
            if (type == ApiWrap$TextPart.Type.Text && !z) {
                arrayList2.add(SerializeString(apiWrap$TextPart2.text));
            } else {
                switch (AnonymousClass1.$SwitchMap$com$exteragram$messenger$export$api$ApiWrap$TextPart$Type[type.ordinal()]) {
                    case 1:
                        str = "unknown";
                        break;
                    case 2:
                        str = "mention";
                        break;
                    case 3:
                        str = "hashtag";
                        break;
                    case 4:
                        str = "bot_command";
                        break;
                    case 5:
                        str = "link";
                        break;
                    case 6:
                        str = "email";
                        break;
                    case 7:
                        str = "bold";
                        break;
                    case 8:
                        str = "italic";
                        break;
                    case 9:
                        str = "code";
                        break;
                    case 10:
                        str = "pre";
                        break;
                    case 11:
                        str = "plain";
                        break;
                    case 12:
                        str = "text_link";
                        break;
                    case 13:
                        str = "mention_name";
                        break;
                    case 14:
                        str = "phone";
                        break;
                    case 15:
                        str = "cashtag";
                        break;
                    case 16:
                        str = "underline";
                        break;
                    case 17:
                        str = "strikethrough";
                        break;
                    case 18:
                        str = "blockquote";
                        break;
                    case 19:
                        str = "bank_card";
                        break;
                    case 20:
                        str = "spoiler";
                        break;
                    case 21:
                        str = "custom_emoji";
                        break;
                    default:
                        CstInsn$$ExternalSyntheticBUOutline0.m("wtf is it? ", apiWrap$TextPart2.text);
                        return null;
                }
                ApiWrap$TextPart.Type type2 = apiWrap$TextPart2.type;
                if (type2 == ApiWrap$TextPart.Type.MentionName) {
                    strSerializeString = apiWrap$TextPart2.additional;
                } else if (type2 == ApiWrap$TextPart.Type.Pre || type2 == ApiWrap$TextPart.Type.TextUrl || type2 == ApiWrap$TextPart.Type.CustomEmoji) {
                    strSerializeString = SerializeString(apiWrap$TextPart2.additional);
                } else if (type2 == ApiWrap$TextPart.Type.Blockquote) {
                    strSerializeString = apiWrap$TextPart2.additional.isEmpty() ? "false" : "true";
                } else {
                    strSerializeString = _UrlKt.FRAGMENT_ENCODE_SET;
                }
                arrayList2.add(SerializeObject(jsonContext, new Pair(TeXSymbolParser.TYPE_ATTR, SerializeString(str)), new Pair("text", SerializeString(apiWrap$TextPart2.text)), new Pair("additional", strSerializeString)));
            }
        }
        ArrayList<Boolean> arrayList3 = jsonContext.nesting;
        arrayList3.remove(arrayList3.size() - 1);
        if (!z && arrayList.size() == 1 && arrayList.get(0).type == ApiWrap$TextPart.Type.Text) {
            return (String) arrayList2.get(0);
        }
        return SerializeArray(jsonContext, arrayList2);
    }

    public AbstractWriter.Result writeBlock(String str) {
        return this._file.writeBlock(str);
    }

    public String SerializeDialog(ApiWrap$DialogInfo apiWrap$DialogInfo, boolean z) {
        return Indentation(this) + SerializeObject(this, new Pair("name", SerializeString(apiWrap$DialogInfo.name)), new Pair("id", Long.valueOf(apiWrap$DialogInfo.peerId)), new Pair("relativePath", SerializeString(apiWrap$DialogInfo.relativePath)), new Pair("left", Boolean.valueOf(z)));
    }
}
