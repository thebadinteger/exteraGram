package com.exteragram.messenger.export.output.json;

import android.util.Log;
import android.util.Pair;
import com.exteragram.messenger.export.ExportSettings;
import com.exteragram.messenger.export.api.ApiWrap$Chat;
import com.exteragram.messenger.export.api.ApiWrap$ContactInfo;
import com.exteragram.messenger.export.api.ApiWrap$ContactsList;
import com.exteragram.messenger.export.api.ApiWrap$DialogInfo;
import com.exteragram.messenger.export.api.ApiWrap$DialogsInfo;
import com.exteragram.messenger.export.api.ApiWrap$ExportPersonalInfo;
import com.exteragram.messenger.export.api.ApiWrap$File;
import com.exteragram.messenger.export.api.ApiWrap$Message;
import com.exteragram.messenger.export.api.ApiWrap$MessagesSlice;
import com.exteragram.messenger.export.api.ApiWrap$SessionsList;
import com.exteragram.messenger.export.api.ApiWrap$StoriesSlice;
import com.exteragram.messenger.export.api.ApiWrap$Story;
import com.exteragram.messenger.export.api.ApiWrap$TopPeer;
import com.exteragram.messenger.export.api.ApiWrap$UserpicsInfo;
import com.exteragram.messenger.export.api.ApiWrap$WebSession;
import com.exteragram.messenger.export.api.DataTypesUtils;
import com.exteragram.messenger.export.output.AbstractWriter;
import com.exteragram.messenger.export.output.FileManager;
import com.exteragram.messenger.export.output.OutputFile;
import com.exteragram.messenger.export.output.html.HtmlWriter;
import java.util.ArrayList;
import okhttp3.internal.url._UrlKt;
import org.scilab.forge.jlatexmath.TeXSymbolParser;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;

public class JsonWriter extends AbstractWriter {
    private volatile JsonContext _chat;
    private volatile JsonContext _chatSummary;
    private volatile JsonContext _contacts;
    private ApiWrap$DialogInfo _dialog;
    private HtmlWriter.DialogsMode _dialogsMode = HtmlWriter.DialogsMode.None;
    private int _messagesCount;
    private volatile JsonContext _otherData;
    private volatile JsonContext _sessions;
    private ExportSettings _settings;
    private OutputFile.Stats _stats;
    private volatile JsonContext _stories;
    private JsonContext _summary;

    private static String messagesFile(int i) {
        return "messages" + i + ".json";
    }

    public static String StringAllowNull(String str) {
        return (str == null || str.isEmpty()) ? "null" : JsonContext.SerializeString(str);
    }

    @Override 
    public String mainFilePath() {
        return this._settings.path + "/result.json";
    }

    @Override 
    public AbstractWriter.Result start(ExportSettings exportSettings, OutputFile.Stats stats) {
        this._settings = exportSettings;
        this._stats = stats;
        this._summary = new JsonContext(fileWithRelativePath("result.json"));
        this._contacts = new JsonContext(fileWithRelativePath("contacts.json"));
        this._stories = new JsonContext(fileWithRelativePath("stories.json"));
        if (this._settings.onlySinglePeer()) {
            return AbstractWriter.Result.Success();
        }
        return this._summary.writeBlock(pushNesting(Boolean.TRUE, this._summary) + prepareObjectItemStart("about", this._summary) + JsonContext.SerializeString("Here is the data you requested. Remember: Telegram is ad free, it doesn't use your data for ad targeting and doesn't sell it to others. Telegram only keeps the information it needs to function as a secure and feature-rich cloud service.\\n\\nCheck out Settings > Privacy & Security on Telegram's mobile apps for the relevant settings."));
    }

    @Override 
    public AbstractWriter.Result writePersonal(ApiWrap$ExportPersonalInfo apiWrap$ExportPersonalInfo) {
        ApiWrap$ContactInfo apiWrap$ContactInfo = apiWrap$ExportPersonalInfo.user.info;
        JsonContext jsonContext = this._summary;
        StringBuilder sb = new StringBuilder();
        sb.append(prepareObjectItemStart("personal_information", this._summary));
        sb.append(JsonContext.SerializeObject(this._summary, new Pair("user_id", apiWrap$ExportPersonalInfo.user.bareId), new Pair("first_name", JsonContext.SerializeString(apiWrap$ContactInfo.firstName)), new Pair("last_name", JsonContext.SerializeString(apiWrap$ContactInfo.lastName)), new Pair("phone_number", JsonContext.SerializeString(apiWrap$ContactInfo.phoneNumber)), new Pair("username", !apiWrap$ExportPersonalInfo.user.username.isEmpty() ? JsonContext.SerializeString(apiWrap$ExportPersonalInfo.user.username) : _UrlKt.FRAGMENT_ENCODE_SET), new Pair("bio", !apiWrap$ExportPersonalInfo.bio.isEmpty() ? JsonContext.SerializeString(apiWrap$ExportPersonalInfo.bio) : _UrlKt.FRAGMENT_ENCODE_SET)));
        return jsonContext.writeBlock(sb.toString());
    }

    @Override 
    public AbstractWriter.Result writeDialogsStart(ApiWrap$DialogsInfo apiWrap$DialogsInfo) {
        StringBuilder sb = new StringBuilder(prepareArrayItemStart(this._summary));
        JsonContext jsonContext = this._summary;
        jsonContext._currentNestingHadItem = false;
        sb.append(prepareObjectItemStart("chats", jsonContext));
        sb.append(pushNesting(Boolean.FALSE, this._summary));
        ArrayList<ApiWrap$DialogInfo> arrayList = apiWrap$DialogsInfo.chats;
        int size = arrayList.size();
        int i = 0;
        boolean z = true;
        while (i < size) {
            ApiWrap$DialogInfo apiWrap$DialogInfo = arrayList.get(i);
            i++;
            ApiWrap$DialogInfo apiWrap$DialogInfo2 = apiWrap$DialogInfo;
            if (z) {
                z = false;
            } else {
                sb.append(",\n");
            }
            sb.append(this._summary.SerializeDialog(apiWrap$DialogInfo2, false));
        }
        ArrayList<ApiWrap$DialogInfo> arrayList2 = apiWrap$DialogsInfo.left;
        int size2 = arrayList2.size();
        int i2 = 0;
        while (i2 < size2) {
            ApiWrap$DialogInfo apiWrap$DialogInfo3 = arrayList2.get(i2);
            i2++;
            ApiWrap$DialogInfo apiWrap$DialogInfo4 = apiWrap$DialogInfo3;
            if (z) {
                z = false;
            } else {
                sb.append(",\n");
            }
            sb.append(this._summary.SerializeDialog(apiWrap$DialogInfo4, true));
        }
        sb.append(popNesting(this._summary));
        return this._summary.writeBlock(sb.toString());
    }

    @Override 
    public AbstractWriter.Result writeDialogStart(ApiWrap$DialogInfo apiWrap$DialogInfo) {
        String str;
        this._chatSummary = new JsonContext(fileWithRelativePath(apiWrap$DialogInfo.relativePath + "info.json"));
        this._chat = new JsonContext(fileWithRelativePath(apiWrap$DialogInfo.relativePath + messagesFile(0)));
        this._summary.writeBlock(prepareObjectItemStart("msgsCount", this._summary) + apiWrap$DialogInfo.messagesCountPerSplit.get(0));
        this._messagesCount = 0;
        this._dialog = apiWrap$DialogInfo;
        if (!this._settings.onlySinglePeer()) {
            AbstractWriter.Result resultValidateDialogsMode = validateDialogsMode(apiWrap$DialogInfo.isLeftChannel);
            if (!resultValidateDialogsMode.isSuccess()) {
                return resultValidateDialogsMode;
            }
        }
        int i = AnonymousClass2.$SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[apiWrap$DialogInfo.type.ordinal()];
        String strPrepareArrayItemStart = _UrlKt.FRAGMENT_ENCODE_SET;
        switch (i) {
            case 1:
            default:
                str = _UrlKt.FRAGMENT_ENCODE_SET;
                break;
            case 2:
                str = "saved_messages";
                break;
            case 3:
                str = "replies";
                break;
            case 4:
                str = "verification_codes";
                break;
            case 5:
                str = "personal_chat";
                break;
            case 6:
                str = "bot_chat";
                break;
            case 7:
                str = "private_group";
                break;
            case 8:
                str = "private_supergroup";
                break;
            case 9:
                str = "public_supergroup";
                break;
            case 10:
                str = "private_channel";
                break;
            case 11:
                str = "public_channel";
                break;
        }
        if (!this._settings.onlySinglePeer()) {
            strPrepareArrayItemStart = prepareArrayItemStart(this._chatSummary);
        }
        StringBuilder sb = new StringBuilder(strPrepareArrayItemStart);
        sb.append(pushNesting(Boolean.TRUE, this._chatSummary));
        ApiWrap$DialogInfo.Type type = apiWrap$DialogInfo.type;
        if (type != ApiWrap$DialogInfo.Type.Self && type != ApiWrap$DialogInfo.Type.Replies && type != ApiWrap$DialogInfo.Type.VerifyCodes) {
            sb.append(prepareObjectItemStart("name", this._chatSummary) + StringAllowNull(apiWrap$DialogInfo.name));
        }
        sb.append(prepareObjectItemStart(TeXSymbolParser.TYPE_ATTR, this._chatSummary) + StringAllowNull(str));
        sb.append(prepareObjectItemStart("id", this._chatSummary) + apiWrap$DialogInfo.peerId);
        return this._chatSummary.writeBlock(sb.toString());
    }

    @Override 
    public AbstractWriter.Result writeDialogSlice(ApiWrap$MessagesSlice apiWrap$MessagesSlice) {
        int i = this._messagesCount;
        int i2 = i > 0 ? (i - 1) / 100 : 0;
        int i3 = i / 100;
        Log.d("exteraGram", "switching to next chat file! old index: " + i2 + ", new index: " + i3);
        switchToNextChatFile(i3);
        this._chat.writeBlock(pushNesting(Boolean.FALSE, this._chat));
        for (int i4 = 0; i4 < apiWrap$MessagesSlice.list.size(); i4++) {
            ApiWrap$Message apiWrap$Message = apiWrap$MessagesSlice.list.get(i4);
            if (!DataTypesUtils.SkipMessageByDate(apiWrap$Message, this._settings)) {
                this._chat.writeBlock(JsonContext.SerializeMessage(this._chat, apiWrap$Message, apiWrap$MessagesSlice.peers, "https://t.me/"));
                if (i4 != apiWrap$MessagesSlice.list.size() - 1) {
                    this._chat._currentNestingHadItem = true;
                    this._chat.writeBlock(prepareArrayItemStart(this._chat));
                }
                this._messagesCount++;
            }
        }
        this._chat.writeBlock(popNesting(this._chat));
        return AbstractWriter.Result.Success();
    }

    @Override 
    public AbstractWriter.Result writeDialogEnd() {
        this._chatSummary.writeBlock(prepareObjectItemStart("msgsCount", this._chatSummary) + this._messagesCount);
        return this._chatSummary.writeBlock(popNesting(this._chatSummary));
    }

    @Override 
    public AbstractWriter.Result writeDialogsEnd() {
        return AbstractWriter.Result.Success();
    }

    @Override 
    public AbstractWriter.Result writeSessionsList(ApiWrap$SessionsList apiWrap$SessionsList) {
        this._sessions = new JsonContext(fileWithRelativePath("sessions.json"));
        AbstractWriter.Result resultWriteSessions = writeSessions(apiWrap$SessionsList);
        if (!resultWriteSessions.isSuccess()) {
            return resultWriteSessions;
        }
        AbstractWriter.Result resultWriteWebSessions = writeWebSessions(apiWrap$SessionsList);
        return !resultWriteWebSessions.isSuccess() ? resultWriteWebSessions : AbstractWriter.Result.Success();
    }

    @Override 
    public AbstractWriter.Result writeUserpicsStart(ApiWrap$UserpicsInfo apiWrap$UserpicsInfo) {
        return this._summary.writeBlock(prepareObjectItemStart("profile_pictures", this._summary) + pushNesting(Boolean.FALSE, this._summary));
    }

    @Override 
    public AbstractWriter.Result writeUserpicsSlice(ArrayList<HtmlWriter.Photo> arrayList) {
        String str;
        StringBuilder sb = new StringBuilder();
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            HtmlWriter.Photo photo = arrayList.get(i);
            i++;
            HtmlWriter.Photo photo2 = photo;
            ApiWrap$File apiWrap$File = photo2.image.file;
            int i2 = AnonymousClass2.$SwitchMap$com$exteragram$messenger$export$api$ApiWrap$File$SkipReason[apiWrap$File.skipReason.ordinal()];
            if (i2 == 1) {
                str = "(Photo unavailable, please try again later)";
            } else if (i2 == 2) {
                str = "(Photo exceeds maximum size. Change data exporting settings to download.)";
            } else if (i2 == 3) {
                str = "(Photo not included. Change data exporting settings to download.)";
            } else {
                if (i2 != 4) {
                    if (i2 != 5) {
                        throw new IncompatibleClassChangeError();
                    }
                    throw new IllegalStateException("Skip reason while writing photo path.");
                }
                str = apiWrap$File.relativePath;
            }
            sb.append(prepareArrayItemStart(this._summary));
            JsonContext jsonContext = this._summary;
            int i3 = photo2.date;
            sb.append(JsonContext.SerializeObject(jsonContext, new Pair("date_unixtime", i3 != 0 ? JsonContext.SerializeString(String.valueOf(i3)) : _UrlKt.FRAGMENT_ENCODE_SET), new Pair("photo", JsonContext.SerializeString(str))));
        }
        return this._summary.writeBlock(sb.toString());
    }

    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type;
        static final /* synthetic */ int[] $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$File$SkipReason;

        static {
            int[] iArr = new int[ApiWrap$File.SkipReason.values().length];
            $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$File$SkipReason = iArr;
            try {
                iArr[ApiWrap$File.SkipReason.Unavailable.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$File$SkipReason[ApiWrap$File.SkipReason.FileSize.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$File$SkipReason[ApiWrap$File.SkipReason.FileType.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$File$SkipReason[ApiWrap$File.SkipReason.None.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$File$SkipReason[ApiWrap$File.SkipReason.DateLimits.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            int[] iArr2 = new int[ApiWrap$DialogInfo.Type.values().length];
            $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type = iArr2;
            try {
                iArr2[ApiWrap$DialogInfo.Type.Unknown.ordinal()] = 1;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[ApiWrap$DialogInfo.Type.Self.ordinal()] = 2;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[ApiWrap$DialogInfo.Type.Replies.ordinal()] = 3;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[ApiWrap$DialogInfo.Type.VerifyCodes.ordinal()] = 4;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[ApiWrap$DialogInfo.Type.Personal.ordinal()] = 5;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[ApiWrap$DialogInfo.Type.Bot.ordinal()] = 6;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[ApiWrap$DialogInfo.Type.PrivateGroup.ordinal()] = 7;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[ApiWrap$DialogInfo.Type.PrivateSupergroup.ordinal()] = 8;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[ApiWrap$DialogInfo.Type.PublicSupergroup.ordinal()] = 9;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[ApiWrap$DialogInfo.Type.PrivateChannel.ordinal()] = 10;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[ApiWrap$DialogInfo.Type.PublicChannel.ordinal()] = 11;
            } catch (NoSuchFieldError unused16) {
            }
        }
    }

    @Override 
    public AbstractWriter.Result writeUserpicsEnd() {
        JsonContext jsonContext = this._summary;
        return jsonContext.writeBlock(popNesting(jsonContext));
    }

    @Override 
    public AbstractWriter.Result writeStoriesStart(int i) {
        String strPrepareObjectItemStart = prepareObjectItemStart("stories", this._stories);
        return this._stories.writeBlock(strPrepareObjectItemStart + pushNesting(Boolean.FALSE, this._stories));
    }

    @Override 
    public AbstractWriter.Result writeStoriesSlice(ApiWrap$StoriesSlice apiWrap$StoriesSlice) {
        String str;
        if (apiWrap$StoriesSlice.list.isEmpty()) {
            return AbstractWriter.Result.Success();
        }
        StringBuilder sb = new StringBuilder();
        ArrayList<ApiWrap$Story> arrayList = apiWrap$StoriesSlice.list;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            ApiWrap$Story apiWrap$Story = arrayList.get(i);
            i++;
            ApiWrap$Story apiWrap$Story2 = apiWrap$Story;
            ApiWrap$File apiWrap$FileFile = apiWrap$Story2.file();
            int i2 = AnonymousClass2.$SwitchMap$com$exteragram$messenger$export$api$ApiWrap$File$SkipReason[apiWrap$FileFile.skipReason.ordinal()];
            if (i2 == 1) {
                str = "(Photo unavailable, please try again later)";
            } else if (i2 == 2) {
                str = "(Photo exceeds maximum size. Change data exporting settings to download.)";
            } else if (i2 == 3) {
                str = "(Photo not included. Change data exporting settings to download.)";
            } else {
                if (i2 != 4) {
                    if (i2 != 5) {
                        throw new IncompatibleClassChangeError();
                    }
                    throw new IllegalStateException("date limited skip reason while writing story path");
                }
                str = apiWrap$FileFile.relativePath;
            }
            sb.append(prepareArrayItemStart(this._stories));
            JsonContext jsonContext = this._stories;
            int i3 = apiWrap$Story2.date;
            String strSerializeString = _UrlKt.FRAGMENT_ENCODE_SET;
            Pair pair = new Pair("date_unixtime", i3 != 0 ? JsonContext.SerializeString(String.valueOf(i3)) : _UrlKt.FRAGMENT_ENCODE_SET);
            int i4 = apiWrap$Story2.expires;
            if (i4 != 0) {
                strSerializeString = JsonContext.SerializeString(String.valueOf(i4));
            }
            sb.append(JsonContext.SerializeObject(jsonContext, pair, new Pair("expires_unixtime", strSerializeString), new Pair("pinned", apiWrap$Story2.pinned ? "true" : "false"), new Pair("media", JsonContext.SerializeString(str))));
        }
        return this._stories.writeBlock(sb.toString());
    }

    @Override 
    public AbstractWriter.Result writeStoriesEnd() {
        return this._stories.writeBlock(popNesting(this._stories));
    }

    @Override 
    public AbstractWriter.Result writeContactsList(ApiWrap$ContactsList apiWrap$ContactsList) {
        AbstractWriter.Result resultWriteSavedContacts = writeSavedContacts(apiWrap$ContactsList);
        if (!resultWriteSavedContacts.isSuccess()) {
            return resultWriteSavedContacts;
        }
        AbstractWriter.Result resultWriteFrequentContacts = writeFrequentContacts(apiWrap$ContactsList);
        return !resultWriteFrequentContacts.isSuccess() ? resultWriteFrequentContacts : AbstractWriter.Result.Success();
    }

    @Override 
    public AbstractWriter.Result writeOtherData(ApiWrap$File apiWrap$File) {
        OutputFile outputFileFileWithRelativePath = fileWithRelativePath(apiWrap$File.relativePath);
        String fileContent = FileManager.readFileContent(outputFileFileWithRelativePath._file);
        if (fileContent == null || fileContent.isEmpty()) {
            return AbstractWriter.Result.Success();
        }
        this._otherData = new JsonContext(outputFileFileWithRelativePath);
        prepareObjectItemStart("other_data", this._otherData);
        return AbstractWriter.Result.Success();
    }

    private AbstractWriter.Result writeSessions(ApiWrap$SessionsList apiWrap$SessionsList) {
        StringBuilder sb = new StringBuilder(prepareObjectItemStart("sessions", this._sessions));
        sb.append(pushNesting(Boolean.TRUE, this._sessions));
        sb.append(prepareObjectItemStart("about", this._sessions));
        sb.append(JsonContext.SerializeString("_environment.aboutSessions"));
        sb.append(prepareObjectItemStart("list", this._sessions));
        sb.append(pushNesting(Boolean.FALSE, this._sessions));
        ArrayList<TLRPC.TL_authorization> arrayList = apiWrap$SessionsList.list;
        int i = 0;
        for (int size = arrayList.size(); i < size; size = size) {
            TLRPC.TL_authorization tL_authorization = arrayList.get(i);
            i++;
            TLRPC.TL_authorization tL_authorization2 = tL_authorization;
            sb.append(prepareArrayItemStart(this._sessions));
            sb.append(JsonContext.SerializeObject(this._sessions, new Pair("last_active", JsonContext.SerializeString(String.valueOf(tL_authorization2.date_active))), new Pair("last_ip", JsonContext.SerializeString(tL_authorization2.ip)), new Pair("last_country", JsonContext.SerializeString(tL_authorization2.country)), new Pair("last_region", JsonContext.SerializeString(tL_authorization2.region)), new Pair("application_name", StringAllowNull(tL_authorization2.app_name)), new Pair("application_version", tL_authorization2.app_version.isEmpty() ? _UrlKt.FRAGMENT_ENCODE_SET : JsonContext.SerializeString(tL_authorization2.app_version)), new Pair("device_model", JsonContext.SerializeString(tL_authorization2.device_model)), new Pair("platform", JsonContext.SerializeString(tL_authorization2.platform)), new Pair("system_version", JsonContext.SerializeString(tL_authorization2.system_version)), new Pair("created", JsonContext.SerializeString(String.valueOf(tL_authorization2.date_created)))));
            arrayList = arrayList;
        }
        sb.append(popNesting(this._sessions));
        return this._sessions.writeBlock(((Object) sb) + popNesting(this._sessions));
    }

    private AbstractWriter.Result writeWebSessions(ApiWrap$SessionsList apiWrap$SessionsList) {
        StringBuilder sb = new StringBuilder(prepareObjectItemStart("web_sessions", this._sessions));
        sb.append(pushNesting(Boolean.TRUE, this._sessions));
        sb.append(prepareObjectItemStart("about", this._sessions));
        sb.append(JsonContext.SerializeString("_environment.aboutWebSessions"));
        sb.append(prepareObjectItemStart("list", this._sessions));
        sb.append(pushNesting(Boolean.FALSE, this._sessions));
        ArrayList<ApiWrap$WebSession> arrayList = apiWrap$SessionsList.webList;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            ApiWrap$WebSession apiWrap$WebSession = arrayList.get(i);
            i++;
            ApiWrap$WebSession apiWrap$WebSession2 = apiWrap$WebSession;
            sb.append(prepareArrayItemStart(this._sessions));
            sb.append(JsonContext.SerializeObject(this._sessions, new Pair("last_active_unixtime", JsonContext.SerializeString(String.valueOf(apiWrap$WebSession2.lastActive()))), new Pair("last_ip", JsonContext.SerializeString(apiWrap$WebSession2.ip())), new Pair("last_region", JsonContext.SerializeString(apiWrap$WebSession2.region())), new Pair("bot_username", StringAllowNull(apiWrap$WebSession2.botUsername())), new Pair("domain_name", StringAllowNull(apiWrap$WebSession2.domain())), new Pair("browser", JsonContext.SerializeString(apiWrap$WebSession2.browser())), new Pair("platform", JsonContext.SerializeString(apiWrap$WebSession2.platform())), new Pair("created_unixtime", JsonContext.SerializeString(String.valueOf(apiWrap$WebSession2.created())))));
        }
        sb.append(popNesting(this._sessions));
        return this._sessions.writeBlock(((Object) sb) + popNesting(this._sessions));
    }

    private AbstractWriter.Result writeSavedContacts(ApiWrap$ContactsList apiWrap$ContactsList) {
        StringBuilder sb = new StringBuilder(prepareObjectItemStart("contacts", this._contacts));
        sb.append(pushNesting(Boolean.TRUE, this._contacts));
        sb.append(prepareObjectItemStart("about", this._contacts));
        sb.append(JsonContext.SerializeString("_environment.aboutContacts"));
        sb.append(prepareObjectItemStart("list", this._contacts));
        sb.append(pushNesting(Boolean.FALSE, this._contacts));
        ArrayList<Integer> arrayListSortedContactsIndices = DataTypesUtils.SortedContactsIndices(apiWrap$ContactsList);
        int size = arrayListSortedContactsIndices.size();
        int i = 0;
        while (i < size) {
            Integer num = arrayListSortedContactsIndices.get(i);
            i++;
            ApiWrap$ContactInfo apiWrap$ContactInfo = apiWrap$ContactsList.list.get(num.intValue());
            sb.append(prepareArrayItemStart(this._contacts));
            if (apiWrap$ContactInfo.firstName.isEmpty() && apiWrap$ContactInfo.lastName.isEmpty() && apiWrap$ContactInfo.phoneNumber.isEmpty()) {
                sb.append(JsonContext.SerializeObject(this._contacts, new Pair("date_unixtime", JsonContext.SerializeString(String.valueOf(apiWrap$ContactInfo.date)))));
            } else {
                sb.append(JsonContext.SerializeObject(this._contacts, new Pair("user_id", apiWrap$ContactInfo.userId.longValue() != 0 ? String.valueOf(apiWrap$ContactInfo.userId) : _UrlKt.FRAGMENT_ENCODE_SET), new Pair("first_name", JsonContext.SerializeString(apiWrap$ContactInfo.firstName)), new Pair("last_name", JsonContext.SerializeString(apiWrap$ContactInfo.lastName)), new Pair("phone_number", JsonContext.SerializeString(PhoneFormat.getInstance().format(apiWrap$ContactInfo.phoneNumber))), new Pair("date_unixtime", JsonContext.SerializeString(String.valueOf(apiWrap$ContactInfo.date)))));
            }
        }
        sb.append(popNesting(this._contacts));
        return this._contacts.writeBlock(((Object) sb) + popNesting(this._contacts));
    }

    private AbstractWriter.Result writeFrequentContacts(ApiWrap$ContactsList apiWrap$ContactsList) {
        final StringBuilder sb = new StringBuilder(prepareObjectItemStart("frequent_contacts", this._contacts));
        sb.append(pushNesting(Boolean.TRUE, this._contacts));
        sb.append(prepareObjectItemStart("about", this._contacts));
        sb.append(JsonContext.SerializeString("_environment.aboutFrequent"));
        sb.append(prepareObjectItemStart("list", this._contacts));
        sb.append(pushNesting(Boolean.FALSE, this._contacts));
        Utilities.Callback2<ArrayList<ApiWrap$TopPeer>, String> callback2 = new Utilities.Callback2<ArrayList<ApiWrap$TopPeer>, String>() { 
            @Override 
            public void run(ArrayList<ApiWrap$TopPeer> arrayList, String str) {
                String str2;
                int size = arrayList.size();
                int i = 0;
                while (i < size) {
                    ApiWrap$TopPeer apiWrap$TopPeer = arrayList.get(i);
                    i++;
                    ApiWrap$TopPeer apiWrap$TopPeer2 = apiWrap$TopPeer;
                    ApiWrap$Chat apiWrap$Chat = apiWrap$TopPeer2.peer.chat;
                    if (apiWrap$Chat == null) {
                        str2 = "user";
                    } else {
                        boolean zIsEmpty = apiWrap$Chat.username.isEmpty();
                        boolean z = apiWrap$Chat.isBroadcast;
                        if (zIsEmpty) {
                            if (z) {
                                str2 = "private_channel";
                            } else if (apiWrap$Chat.isSupergroup) {
                                str2 = "private_supergroup";
                            } else {
                                str2 = "private_group";
                            }
                        } else if (z) {
                            str2 = "public_channel";
                        } else {
                            str2 = "public_supergroup";
                        }
                    }
                    StringBuilder sb2 = sb;
                    JsonWriter jsonWriter = JsonWriter.this;
                    sb2.append(jsonWriter.prepareArrayItemStart(jsonWriter._contacts));
                    sb.append(JsonContext.SerializeObject(JsonWriter.this._contacts, new Pair("id", String.valueOf(apiWrap$TopPeer2.peer.id())), new Pair("category", JsonContext.SerializeString(str)), new Pair(TeXSymbolParser.TYPE_ATTR, JsonContext.SerializeString(str2)), new Pair("name", JsonWriter.StringAllowNull(apiWrap$TopPeer2.peer.name())), new Pair("rating", String.valueOf(apiWrap$TopPeer2.rating))));
                }
            }
        };
        callback2.run(apiWrap$ContactsList.correspondents, "people");
        callback2.run(apiWrap$ContactsList.inlineBots, "inline_bots");
        callback2.run(apiWrap$ContactsList.phoneCalls, "calls");
        sb.append(popNesting(this._contacts));
        return this._contacts.writeBlock(((Object) sb) + popNesting(this._contacts));
    }

    private AbstractWriter.Result switchToNextChatFile(int i) {
        this._chat = new JsonContext(fileWithRelativePath(this._dialog.relativePath + messagesFile(i)));
        return AbstractWriter.Result.Success();
    }

    @Override 
    public AbstractWriter.Result finish() {
        if (this._settings.onlySinglePeer()) {
            return AbstractWriter.Result.Success();
        }
        JsonContext jsonContext = this._summary;
        return jsonContext.writeBlock(popNesting(jsonContext));
    }

    private OutputFile fileWithRelativePath(String str) {
        return new OutputFile(this._settings.path + "/" + str, this._stats);
    }

    private String pushNesting(Boolean bool, JsonContext jsonContext) {
        jsonContext.nesting.add(bool);
        jsonContext._currentNestingHadItem = false;
        if (bool.booleanValue()) {
            return "{";
        }
        return "[";
    }

    private String prepareObjectItemStart(String str, JsonContext jsonContext) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(jsonContext._currentNestingHadItem ? ",\n" : "\n");
            sb.append(JsonContext.Indentation(jsonContext));
            sb.append(JsonContext.SerializeString(str));
            sb.append(": ");
            return sb.toString();
        } finally {
            jsonContext._currentNestingHadItem = true;
        }
    }

    private AbstractWriter.Result writeChatsEnd() {
        return AbstractWriter.Result.Success();
    }

    private String popNesting(JsonContext jsonContext) {
        ArrayList<Boolean> arrayList = jsonContext.nesting;
        Boolean bool = arrayList.get(arrayList.size() - 1);
        ArrayList<Boolean> arrayList2 = jsonContext.nesting;
        arrayList2.remove(arrayList2.size() - 1);
        jsonContext._currentNestingHadItem = true;
        StringBuilder sb = new StringBuilder("\n");
        sb.append(JsonContext.Indentation(jsonContext));
        sb.append(bool.booleanValue() ? '}' : ']');
        return sb.toString();
    }

    public AbstractWriter.Result validateDialogsMode(boolean z) {
        HtmlWriter.DialogsMode dialogsMode;
        String str;
        if (z) {
            dialogsMode = HtmlWriter.DialogsMode.Left;
        } else {
            dialogsMode = HtmlWriter.DialogsMode.Chats;
        }
        HtmlWriter.DialogsMode dialogsMode2 = this._dialogsMode;
        if (dialogsMode2 == dialogsMode) {
            return AbstractWriter.Result.Success();
        }
        if (dialogsMode2 != HtmlWriter.DialogsMode.None) {
            AbstractWriter.Result resultWriteChatsEnd = writeChatsEnd();
            if (!resultWriteChatsEnd.isSuccess()) {
                return resultWriteChatsEnd;
            }
        }
        this._dialogsMode = dialogsMode;
        String str2 = z ? "left_chats" : "chats";
        if (z) {
            str = "Below are the supergroups and channels from this export that you've left or where you were banned.\\n\\nNote that when you leave a channel or supergroup you've created, you have the option to either delete it, or simply leave (in case you want to rejoin later, or keep the community alive despite not being a member).";
        } else {
            str = "This page lists all chats from this export.";
        }
        return writeChatsStart(str2, str);
    }

    private AbstractWriter.Result writeChatsStart(String str, String str2) {
        return AbstractWriter.Result.Success();
    }

    public String prepareArrayItemStart(JsonContext jsonContext) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(jsonContext._currentNestingHadItem ? ",\n" : "\n");
            sb.append(JsonContext.Indentation(jsonContext));
            return sb.toString();
        } finally {
            jsonContext._currentNestingHadItem = true;
        }
    }
}
