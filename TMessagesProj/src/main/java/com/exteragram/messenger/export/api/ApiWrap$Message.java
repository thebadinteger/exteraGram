package com.exteragram.messenger.export.api;

import java.util.ArrayList;
import okhttp3.internal.url._UrlKt;
import org.telegram.tgnet.TLRPC;

public class ApiWrap$Message {
    public TLRPC.MessageAction action;
    public ArrayList<ArrayList<ApiWrap$HistoryMessageMarkupButton>> inlineButtonRows;
    public Object parsedAction;
    public long viaBotId;
    public int id = 0;
    public int date = 0;
    public int edited = 0;
    public long fromId = 0;
    public long peerId = 0;
    public long selfId = 0;
    public long forwardedFromId = 0;
    public String forwardedFromName = _UrlKt.FRAGMENT_ENCODE_SET;
    public int forwardedDate = 0;
    public boolean forwarded = false;
    public boolean showForwardedAsOriginal = false;
    public long savedFromChatId = 0;
    public String signature = _UrlKt.FRAGMENT_ENCODE_SET;
    public int replyToMsgId = 0;
    public long replyToPeerId = 0;
    public ArrayList<ApiWrap$TextPart> text = new ArrayList<>();
    public ArrayList<ApiWrap$Reaction> reactions = new ArrayList<>();
    public ApiWrap$File.SkipReason skipReason = ApiWrap$File.SkipReason.None;
    public boolean out = false;
    public ApiWrap$Media media = new ApiWrap$Media();

    public ApiWrap$File getFile() {
        Object obj = this.parsedAction;
        if (obj instanceof ApiWrap$ActionSuggestProfilePhoto) {
            return ((ApiWrap$ActionSuggestProfilePhoto) obj).photo.image.file;
        }
        if (obj instanceof ApiWrap$ActionChatEditPhoto) {
            return ((ApiWrap$ActionChatEditPhoto) obj).photo.image.file;
        }
        ApiWrap$Media apiWrap$Media = this.media;
        if (apiWrap$Media != null) {
            return apiWrap$Media.getFile();
        }
        return new ApiWrap$File();
    }
}
