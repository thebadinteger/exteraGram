package com.exteragram.messenger.export.output.html;

import android.util.Pair;
import com.exteragram.messenger.export.ExportSettings;
import com.exteragram.messenger.export.api.ApiWrap$ContactInfo;
import com.exteragram.messenger.export.api.ApiWrap$ContactsList;
import com.exteragram.messenger.export.api.ApiWrap$DialogInfo;
import com.exteragram.messenger.export.api.ApiWrap$DialogsInfo;
import com.exteragram.messenger.export.api.ApiWrap$ExportPersonalInfo;
import com.exteragram.messenger.export.api.ApiWrap$File;
import com.exteragram.messenger.export.api.ApiWrap$Image;
import com.exteragram.messenger.export.api.ApiWrap$Message;
import com.exteragram.messenger.export.api.ApiWrap$MessagesSlice;
import com.exteragram.messenger.export.api.ApiWrap$Peer;
import com.exteragram.messenger.export.api.ApiWrap$SessionsList;
import com.exteragram.messenger.export.api.ApiWrap$StoriesSlice;
import com.exteragram.messenger.export.api.ApiWrap$Story;
import com.exteragram.messenger.export.api.ApiWrap$StoryData;
import com.exteragram.messenger.export.api.ApiWrap$TopPeer;
import com.exteragram.messenger.export.api.ApiWrap$User;
import com.exteragram.messenger.export.api.ApiWrap$UserpicsInfo;
import com.exteragram.messenger.export.api.ApiWrap$WebSession;
import com.exteragram.messenger.export.api.DataTypesUtils;
import com.exteragram.messenger.export.output.AbstractWriter;
import com.exteragram.messenger.export.output.FileManager;
import com.exteragram.messenger.export.output.OutputFile;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.google.android.gms.cast.CredentialsData;
import com.google.android.gms.cast.MediaStatus;
import com.google.zxing.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import okhttp3.internal.url._UrlKt;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;

public class HtmlWriter extends AbstractWriter {
    private HtmlContext _chat;
    private boolean _chatFileEmpty;
    private HtmlContext _chats;
    private int _dateMessageId;
    private ApiWrap$ExportPersonalInfo _delayedPersonalInfo;
    private ApiWrap$DialogInfo _dialog;
    private DialogsMode _dialogsMode;
    private String _dialogsRelativePath;
    private MessageInfo _lastMessageInfo;
    private int _messagesCount;
    private ExportSettings _settings;
    private OutputFile.Stats _stats;
    private HtmlContext _stories;
    private HtmlContext _summary;
    private HtmlContext _userpics;
    private int selectedAcc;
    private int _selfColorIndex = 0;
    private boolean _haveSections = false;
    private boolean _summaryNeedDivider = false;
    private int _userpicsCount = 0;
    private int _storiesCount = 0;
    private final ArrayList<Integer> _lastMessageIdsPerFile = new ArrayList<>();
    private final ArrayList<SavedSection> _savedSections = new ArrayList<>();

    public enum DialogsMode {
        None,
        Chats,
        Left
    }

    public static class MessageInfo {
        public int date;
        public long forwardedFromId;
        public String forwardedFromName;
        public long fromId;
        public int id;
        public long viaBotId;
        public int forwardedDate = 0;
        public boolean forwarded = false;
        public boolean showForwardedAsOriginal = false;
        public Type type = Type.Service;

        public enum Type {
            Service,
            Default
        }
    }

    public static class Photo {
        public long id = 0;
        public int date = 0;
        public boolean spoilered = false;
        public ApiWrap$Image image = new ApiWrap$Image();
    }

    public static class SavedSection {
        String label;
        String path;
        String type;
        int priority = 0;
        int count = 0;
    }

    public static class UserpicData {
        public int colorIndex = 0;
        public int pixelSize = 0;
        public String imageLink = _UrlKt.FRAGMENT_ENCODE_SET;
        public String largeLink = _UrlKt.FRAGMENT_ENCODE_SET;
        public String firstName = _UrlKt.FRAGMENT_ENCODE_SET;
        public String lastName = _UrlKt.FRAGMENT_ENCODE_SET;
        public String tooltip = _UrlKt.FRAGMENT_ENCODE_SET;
    }

    public static String MakeLinks(String str) {
        char cCharAt;
        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (true) {
            int iIndexOf = str.indexOf("https://telegram.org/", i);
            if (iIndexOf < 0) {
                break;
            }
            int i2 = iIndexOf + 21;
            while (i2 != str.length() && (((cCharAt = str.charAt(i2)) >= 'a' && cCharAt <= 'z') || ((cCharAt >= 'A' && cCharAt <= 'Z') || ((cCharAt >= '0' && cCharAt <= '9') || cCharAt == '-' || cCharAt == '_' || cCharAt == '/')))) {
                i2++;
            }
            if (iIndexOf > i) {
                String strSubstring = str.substring(iIndexOf, i2 - iIndexOf);
                sb.append(str.substring(i, iIndexOf - i));
                sb.append("<a href=\"");
                sb.append(strSubstring);
                sb.append("\">");
                sb.append(strSubstring);
                sb.append("</a>");
                i = i2;
            }
        }
        if (sb.length() == 0) {
            return str;
        }
        if (i < str.length()) {
            sb.append(str.substring(i));
        }
        return sb.toString();
    }

    private static String wrapUserNames(ArrayList<Long> arrayList) {
        StringBuilder sb = new StringBuilder();
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Long l = arrayList.get(i);
            i++;
            sb.append(wrapUserName(l));
            sb.append(" ");
        }
        return sb.toString();
    }

    private static String wrapUserName(Long l) {
        String str = MessagesController.getInstance(UserConfig.selectedAccount).getUser(l).first_name;
        return str.isEmpty() ? "Deleted Account" : str;
    }

    public static String wrapPeerName(TLRPC.Peer peer) {
        String name = ChatUtils.getInstance().getName(DialogObject.getPeerDialogId(peer));
        return name.isEmpty() ? "Deleted" : name;
    }

    public static String wrapPeerName(long j) {
        String name = ChatUtils.getInstance().getName(j);
        return name.isEmpty() ? "Deleted" : name;
    }

    private static String userpicsFilePath() {
        return "lists/profile_pictures.html";
    }

    public static String WriteUserpicThumb(String str, String str2, final UserpicData userpicData) {
        return (String) HtmlContext.WriteImageThumb(str, str2, new Function() { // from class: com.exteragram.messenger.export.output.html.HtmlWriter$$ExternalSyntheticLambda4
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return HtmlWriter.m1080$r8$lambda$0DixWPzMjZ6lOOvAG9W5PMq7o(userpicData, (Dimension) obj);
            }
        }, null, 0, "_thumb").first;
    }

    /* JADX INFO: renamed from: $r8$lambda$--0DixWPzMjZ6lOOvAG9W5PMq7o, reason: not valid java name */
    public static /* synthetic */ Dimension m1080$r8$lambda$0DixWPzMjZ6lOOvAG9W5PMq7o(UserpicData userpicData, Dimension dimension) {
        int i = userpicData.pixelSize * 2;
        return new Dimension(i, i);
    }

    private static String storiesFilePath() {
        return "lists/stories.html";
    }

    private static String messagesFile(int i) {
        StringBuilder sb = new StringBuilder("messages");
        sb.append(i > 0 ? Integer.valueOf(i + 1) : _UrlKt.FRAGMENT_ENCODE_SET);
        sb.append(".html");
        return sb.toString();
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public String mainFilePath() {
        String strMessagesFile;
        if (this._settings.onlySinglePeer()) {
            strMessagesFile = messagesFile(0);
        } else {
            strMessagesFile = "export_results.html";
        }
        return HtmlContext.pathWithRelativePath(strMessagesFile);
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result start(ExportSettings exportSettings, OutputFile.Stats stats) {
        this._settings = exportSettings;
        File file = FileManager.defaultSavePath;
        new File(file, "css").mkdirs();
        new File(file, "js").mkdirs();
        new File(file, "images").mkdirs();
        FileManager.copyAssets();
        if (this._settings.onlySinglePeer()) {
            return AbstractWriter.Result.Success();
        }
        HtmlContext htmlContextFileWithRelativePath = fileWithRelativePath("export_results.html");
        this._summary = htmlContextFileWithRelativePath;
        return htmlContextFileWithRelativePath.writeBlock(this._summary.pushHeader("Exported Data", _UrlKt.FRAGMENT_ENCODE_SET) + this._summary.pushDiv("page_body"));
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writePersonal(ApiWrap$ExportPersonalInfo apiWrap$ExportPersonalInfo) {
        this._selfColorIndex = apiWrap$ExportPersonalInfo.user.info.colorIndex;
        if ((this._settings.types & 2) != 0) {
            this._delayedPersonalInfo = new ApiWrap$ExportPersonalInfo(apiWrap$ExportPersonalInfo);
            return AbstractWriter.Result.Success();
        }
        return writePreparedPersonal(apiWrap$ExportPersonalInfo, _UrlKt.FRAGMENT_ENCODE_SET);
    }

    private AbstractWriter.Result writePreparedPersonal(ApiWrap$ExportPersonalInfo apiWrap$ExportPersonalInfo, String str) {
        String str2;
        ApiWrap$ContactInfo apiWrap$ContactInfo = apiWrap$ExportPersonalInfo.user.info;
        final UserpicData userpicData = new UserpicData();
        userpicData.colorIndex = this._selfColorIndex;
        userpicData.pixelSize = 90;
        boolean zIsEmpty = str.isEmpty();
        String str3 = _UrlKt.FRAGMENT_ENCODE_SET;
        userpicData.largeLink = zIsEmpty ? _UrlKt.FRAGMENT_ENCODE_SET : userpicsFilePath();
        userpicData.imageLink = (String) HtmlContext.WriteImageThumb(this._settings.path, str, new Function() { // from class: com.exteragram.messenger.export.output.html.HtmlWriter$$ExternalSyntheticLambda1
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return HtmlWriter.$r8$lambda$LUfwZiBWR44LYKa4oSGSBHvO5Xg(userpicData, (Dimension) obj);
            }
        }, null, -1, "_info").first;
        userpicData.firstName = apiWrap$ContactInfo.firstName;
        userpicData.lastName = apiWrap$ContactInfo.lastName;
        StringBuilder sb = new StringBuilder(this._summary.pushDiv("personal_info clearfix"));
        sb.append(this._summary.pushDiv("pull_right userpic_wrap"));
        sb.append(this._summary.pushUserpic(userpicData));
        sb.append(this._summary.popTag());
        pushRows("names", Arrays.asList(new Pair("First name", apiWrap$ContactInfo.firstName), new Pair("Last name", apiWrap$ContactInfo.lastName)), sb);
        if (!apiWrap$ContactInfo.phoneNumber.isEmpty()) {
            str3 = PhoneFormat.getInstance().format("+" + apiWrap$ContactInfo.phoneNumber);
        }
        Pair pair = new Pair("Phone number", str3);
        boolean zIsEmpty2 = apiWrap$ExportPersonalInfo.user.username.isEmpty();
        ApiWrap$User apiWrap$User = apiWrap$ExportPersonalInfo.user;
        if (zIsEmpty2) {
            str2 = apiWrap$User.username;
        } else {
            str2 = "@" + apiWrap$User.username;
        }
        pushRows("info", Arrays.asList(pair, new Pair("Username", str2)), sb);
        pushRows("bio", Arrays.asList(new Pair("Bio", apiWrap$ExportPersonalInfo.bio)), sb);
        sb.append(this._summary.popTag());
        this._summaryNeedDivider = true;
        return this._summary.writeBlock(String.valueOf(sb));
    }

    public static /* synthetic */ Dimension $r8$lambda$LUfwZiBWR44LYKa4oSGSBHvO5Xg(UserpicData userpicData, Dimension dimension) {
        int i = userpicData.pixelSize * 2;
        return new Dimension(i, i);
    }

    private AbstractWriter.Result writeSessions(ApiWrap$SessionsList apiWrap$SessionsList) {
        String str;
        if (apiWrap$SessionsList.list.isEmpty()) {
            return AbstractWriter.Result.Success();
        }
        HtmlContext htmlContextFileWithRelativePath = fileWithRelativePath("lists/sessions.html");
        StringBuilder sb = new StringBuilder(htmlContextFileWithRelativePath.pushHeader("Sessions", "export_results.html"));
        sb.append(htmlContextFileWithRelativePath.pushDiv("page_body list_page"));
        int i = 0;
        sb.append(htmlContextFileWithRelativePath.pushAbout("We store session info to display your connected devices in Settings > Privacy & Security > Active Sessions.", false));
        sb.append(htmlContextFileWithRelativePath.pushDiv("entry_list"));
        ArrayList<TLRPC.TL_authorization> arrayList = apiWrap$SessionsList.list;
        int size = arrayList.size();
        while (i < size) {
            TLRPC.TL_authorization tL_authorization = arrayList.get(i);
            int i2 = i + 1;
            TLRPC.TL_authorization tL_authorization2 = tL_authorization;
            int i3 = tL_authorization2.api_id;
            StringBuilder sb2 = new StringBuilder();
            if (tL_authorization2.app_name.isEmpty()) {
                str = "Unknown";
            } else {
                str = tL_authorization2.app_name;
            }
            sb2.append(str);
            sb2.append(' ');
            sb2.append(tL_authorization2.app_version);
            String string = sb2.toString();
            StringBuilder sb3 = new StringBuilder();
            sb3.append(tL_authorization2.device_model);
            String str2 = ", ";
            sb3.append(", ");
            sb3.append(tL_authorization2.platform);
            sb3.append(' ');
            sb3.append(tL_authorization2.system_version);
            String string2 = sb3.toString();
            StringBuilder sb4 = new StringBuilder();
            sb4.append(tL_authorization2.ip);
            sb4.append(" - ");
            sb4.append(tL_authorization2.region);
            if (tL_authorization2.region.isEmpty() || tL_authorization2.country.isEmpty()) {
                str2 = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            sb4.append(str2);
            sb4.append(tL_authorization2.country);
            String string3 = sb4.toString();
            StringBuilder sb5 = new StringBuilder("Last active: ");
            ArrayList<TLRPC.TL_authorization> arrayList2 = arrayList;
            sb5.append(LocaleController.formatDate(tL_authorization2.date_active));
            sb.append(htmlContextFileWithRelativePath.pushSessionListEntry(i3, string, string2, new ArrayList<>(Arrays.asList(string3, sb5.toString(), "Created: " + LocaleController.formatDate(tL_authorization2.date_created))), _UrlKt.FRAGMENT_ENCODE_SET));
            arrayList = arrayList2;
            i = i2;
        }
        AbstractWriter.Result resultWriteBlock = htmlContextFileWithRelativePath.writeBlock(sb.toString());
        if (!resultWriteBlock.isSuccess()) {
            return resultWriteBlock;
        }
        AbstractWriter.Result resultClose = htmlContextFileWithRelativePath.close();
        if (!resultClose.isSuccess()) {
            return resultClose;
        }
        pushSection(6, "Sessions", "sessions", apiWrap$SessionsList.list.size(), "lists/sessions.html");
        return AbstractWriter.Result.Success();
    }

    private AbstractWriter.Result writeWebSessions(ApiWrap$SessionsList apiWrap$SessionsList) {
        String str;
        if (apiWrap$SessionsList.webList.isEmpty()) {
            return AbstractWriter.Result.Success();
        }
        HtmlContext htmlContextFileWithRelativePath = fileWithRelativePath("lists/web_sessions.html");
        StringBuilder sb = new StringBuilder(htmlContextFileWithRelativePath.pushHeader("Web sessions", "export_results.html"));
        sb.append(htmlContextFileWithRelativePath.pushDiv("page_body list_page"));
        int i = 0;
        sb.append(htmlContextFileWithRelativePath.pushAbout("We store this to display the websites where you logged in using authentication via Telegram. This information is shown in Settings > Privacy & Security > Active Sessions.", false));
        sb.append(htmlContextFileWithRelativePath.pushDiv("entry_list"));
        ArrayList<ApiWrap$WebSession> arrayList = apiWrap$SessionsList.webList;
        int size = arrayList.size();
        while (i < size) {
            ApiWrap$WebSession apiWrap$WebSession = arrayList.get(i);
            int i2 = i + 1;
            ApiWrap$WebSession apiWrap$WebSession2 = apiWrap$WebSession;
            int iStringBarePeerId = (int) (DataTypesUtils.StringBarePeerId(apiWrap$WebSession2.domain()) + MediaStatus.COMMAND_EDIT_TRACKS);
            String strDomain = apiWrap$WebSession2.domain().isEmpty() ? "Unknown" : apiWrap$WebSession2.domain();
            String str2 = apiWrap$WebSession2.platform() + ", " + apiWrap$WebSession2.browser();
            String str3 = strDomain;
            String str4 = apiWrap$WebSession2.ip() + " - " + apiWrap$WebSession2.region();
            StringBuilder sb2 = new StringBuilder("Last active: ");
            ArrayList<ApiWrap$WebSession> arrayList2 = arrayList;
            sb2.append(LocaleController.formatDate(apiWrap$WebSession2.lastActive()));
            ArrayList<String> arrayList3 = new ArrayList<>(Arrays.asList(str4, sb2.toString(), "Created: " + LocaleController.formatDate(apiWrap$WebSession2.created())));
            if (apiWrap$WebSession2.botUsername().isEmpty()) {
                str = _UrlKt.FRAGMENT_ENCODE_SET;
            } else {
                str = "@" + apiWrap$WebSession2.botUsername();
            }
            sb.append(htmlContextFileWithRelativePath.pushSessionListEntry(iStringBarePeerId, str3, str2, arrayList3, str));
            arrayList = arrayList2;
            i = i2;
        }
        AbstractWriter.Result resultWriteBlock = htmlContextFileWithRelativePath.writeBlock(sb.toString());
        if (!resultWriteBlock.isSuccess()) {
            return resultWriteBlock;
        }
        AbstractWriter.Result resultClose = htmlContextFileWithRelativePath.close();
        if (!resultClose.isSuccess()) {
            return resultClose;
        }
        pushSection(7, "Web sessions", CredentialsData.CREDENTIALS_TYPE_WEB, apiWrap$SessionsList.webList.size(), "lists/web_sessions.html");
        return AbstractWriter.Result.Success();
    }

    private void pushRows(String str, List<Pair<String, String>> list, StringBuilder sb) {
        sb.append(this._summary.pushDiv("rows " + str));
        for (Pair<String, String> pair : list) {
            String str2 = (String) pair.first;
            String str3 = (String) pair.second;
            if (!str3.isEmpty()) {
                sb.append(this._summary.pushDiv("row"));
                sb.append(this._summary.pushDiv("label details"));
                sb.append(HtmlContext.SerializeString(str2));
                sb.append(this._summary.popTag());
                sb.append(this._summary.pushDiv("value bold"));
                sb.append(HtmlContext.SerializeString(str3));
                sb.append(this._summary.popTag());
                sb.append(this._summary.popTag());
            }
        }
        sb.append(this._summary.popTag());
    }

    private AbstractWriter.Result switchToNextChatFile(int i) {
        String strMessagesFile = messagesFile(i);
        AbstractWriter.Result resultWriteBlock = this._chat.writeBlock(this._chat.pushTag("a", new Pair("class", "pagination block_link"), new Pair("href", strMessagesFile)) + "Next messages" + this._chat.popTag());
        if (!resultWriteBlock.isSuccess()) {
            return resultWriteBlock;
        }
        AbstractWriter.Result resultClose = this._chat.close();
        if (!resultClose.isSuccess()) {
            return resultClose;
        }
        this._chat = fileWithRelativePath(this._dialog.relativePath + strMessagesFile);
        this._chatFileEmpty = true;
        return AbstractWriter.Result.Success();
    }

    private AbstractWriter.Result writeDialogOpening(int i) {
        String str;
        if (this._dialog.name.isEmpty() && this._dialog.lastName.isEmpty()) {
            str = "Deleted Account";
        } else {
            str = this._dialog.name + ' ' + this._dialog.lastName;
        }
        StringBuilder sb = new StringBuilder(this._chat.pushHeader(str, this._settings.onlySinglePeer() ? _UrlKt.FRAGMENT_ENCODE_SET : this._dialogsRelativePath));
        sb.append(this._chat.pushDiv("page_body chat_page"));
        sb.append(this._chat.pushDiv("history"));
        if (i > 0) {
            sb.append(this._chat.pushTag("a", new Pair("class", "pagination block_link"), new Pair("href", messagesFile(i - 1))));
            sb.append("Previous messages");
            sb.append(this._chat.popTag());
        }
        return this._chat.writeBlock(String.valueOf(sb));
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writeDialogsStart(ApiWrap$DialogsInfo apiWrap$DialogsInfo) {
        if (this._chats != null) {
            throw new IllegalStateException("chats already initialized!");
        }
        if (apiWrap$DialogsInfo.chats.isEmpty() && apiWrap$DialogsInfo.left.isEmpty()) {
            return AbstractWriter.Result.Success();
        }
        if (this._settings.onlySinglePeer()) {
            return AbstractWriter.Result.Success();
        }
        this._dialogsRelativePath = "lists/chats.html";
        HtmlContext htmlContextFileWithRelativePath = fileWithRelativePath("lists/chats.html");
        this._chats = htmlContextFileWithRelativePath;
        new StringBuilder(htmlContextFileWithRelativePath.pushHeader("Chats", "export_results.html"));
        this._chats.pushDiv("page_body list_page");
        SavedSection savedSection = new SavedSection();
        savedSection.priority = 0;
        savedSection.label = "Chats";
        savedSection.type = "chats";
        savedSection.count = apiWrap$DialogsInfo.chats.size() + apiWrap$DialogsInfo.left.size();
        savedSection.path = "lists/chats.html";
        this._savedSections.add(savedSection);
        return writeSections();
    }

    public void pushSection(int i, String str, String str2, int i2, String str3) {
        SavedSection savedSection = new SavedSection();
        savedSection.priority = i;
        savedSection.label = str;
        savedSection.type = str2;
        savedSection.count = i2;
        savedSection.path = str3;
        this._savedSections.add(savedSection);
    }

    private AbstractWriter.Result writeSections() {
        if (this._savedSections.isEmpty()) {
            return AbstractWriter.Result.Success();
        }
        int i = 0;
        if (!this._haveSections) {
            if (!this._summary.writeBlock(this._summary.pushDiv(this._summaryNeedDivider ? "sections with_divider" : "sections", _UrlKt.FRAGMENT_ENCODE_SET)).isSuccess()) {
                return AbstractWriter.Result.Success();
            }
            this._haveSections = true;
            this._summaryNeedDivider = false;
        }
        Collections.sort(this._savedSections, Comparator.comparing(new Function() { // from class: com.exteragram.messenger.export.output.html.HtmlWriter$$ExternalSyntheticLambda0
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Integer.valueOf(((HtmlWriter.SavedSection) obj).priority);
            }
        }));
        ArrayList<String> arrayList = new ArrayList<>();
        ArrayList<SavedSection> arrayList2 = this._savedSections;
        int size = arrayList2.size();
        while (i < size) {
            SavedSection savedSection = arrayList2.get(i);
            i++;
            SavedSection savedSection2 = savedSection;
            HtmlContext htmlContext = this._summary;
            arrayList.add(htmlContext.pushSection(savedSection2.label, savedSection2.type, savedSection2.count, htmlContext.relativePath(savedSection2.path)));
        }
        return this._summary.writeBlock(arrayList);
    }

    /*  JADX ERROR: ConcurrentModificationException in pass: ConstructorVisitor
        java.util.ConcurrentModificationException
        	at java.base/java.util.ArrayList$Itr.checkForComodification(Unknown Source)
        	at java.base/java.util.ArrayList$Itr.next(Unknown Source)
        	at jadx.core.dex.visitors.ConstructorVisitor.insertPhiInsn(ConstructorVisitor.java:139)
        	at jadx.core.dex.visitors.ConstructorVisitor.processInvoke(ConstructorVisitor.java:91)
        	at jadx.core.dex.visitors.ConstructorVisitor.replaceInvoke(ConstructorVisitor.java:56)
        	at jadx.core.dex.visitors.ConstructorVisitor.visit(ConstructorVisitor.java:42)
        */
    public String getTextFromAction(com.exteragram.messenger.export.api.ApiWrap$Message message, String peerName, boolean isChannel) {
        return "";
    }
    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writeDialogStart(ApiWrap$DialogInfo apiWrap$DialogInfo) {
        this._chat = fileWithRelativePath(apiWrap$DialogInfo.relativePath + messagesFile(0));
        this._chatFileEmpty = true;
        this._messagesCount = 0;
        this._dateMessageId = 0;
        this._lastMessageInfo = null;
        this._lastMessageIdsPerFile.clear();
        this._dialog = apiWrap$DialogInfo;
        return AbstractWriter.Result.Success();
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r1v24 */
    /* JADX WARN: Type inference failed for: r1v5 */
    /* JADX WARN: Type inference failed for: r1v6, types: [int] */
    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writeDialogSlice(ApiWrap$MessagesSlice apiWrap$MessagesSlice) {
        int i;
        StringBuilder sb;
        int i2 = this._messagesCount;
        boolean z = false;
        int i3 = i2 > 0 ? (i2 - 1) / 1000 : 0;
        MessageInfo messageInfo = this._lastMessageInfo;
        MessageInfo messageInfo2 = new MessageInfo();
        StringBuilder sb2 = new StringBuilder();
        ArrayList<ApiWrap$Message> arrayList = apiWrap$MessagesSlice.list;
        int size = arrayList.size();
        int i4 = 0;
        while (i4 < size) {
            i4++;
            ApiWrap$Message apiWrap$Message = arrayList.get(i4);
            if (!DataTypesUtils.SkipMessageByDate(apiWrap$Message, this._settings)) {
                int i5 = this._messagesCount / 1000;
                if (i3 != i5) {
                    AbstractWriter.Result resultWriteBlock = this._chat.writeBlock(sb2.toString());
                    if (!resultWriteBlock.isSuccess()) {
                        return resultWriteBlock;
                    }
                    AbstractWriter.Result resultSwitchToNextChatFile = switchToNextChatFile(i5);
                    if (!resultSwitchToNextChatFile.isSuccess()) {
                        return resultSwitchToNextChatFile;
                    }
                    this._lastMessageIdsPerFile.add(Integer.valueOf(messageInfo2 != null ? messageInfo2.id : this._lastMessageInfo.id));
                    StringBuilder sb3 = new StringBuilder();
                    messageInfo = null;
                    this._lastMessageInfo = null;
                    sb = sb3;
                    i = i5;
                } else {
                    i = i3;
                    sb = sb2;
                }
                MessageInfo messageInfo3 = messageInfo;
                if (this._chatFileEmpty) {
                    AbstractWriter.Result resultWriteDialogOpening = writeDialogOpening(i);
                    if (!resultWriteDialogOpening.isSuccess()) {
                        return resultWriteDialogOpening;
                    }
                    this._chatFileEmpty = z;
                }
                int i6 = apiWrap$Message.date;
                if (DataTypesUtils.DisplayDate(i6, messageInfo3 != null ? messageInfo3.date : 0)) {
                    HtmlContext htmlContext = this._chat;
                    int i7 = this._dateMessageId - 1;
                    this._dateMessageId = i7;
                    sb.append(htmlContext.pushServiceMessage(i7, this._dialog, this._settings.path, LocaleController.formatDate(i6), null));
                }
                Pair<MessageInfo, String> pairPushMessage = this._chat.pushMessage(apiWrap$Message, messageInfo3, this._dialog, this._settings.path, apiWrap$MessagesSlice.peers, this, "https://t.me/", new Utilities.Callback2Return() { // from class: com.exteragram.messenger.export.output.html.HtmlWriter$$ExternalSyntheticLambda2
                    @Override // org.telegram.messenger.Utilities.Callback2Return
                    public final Object run(Object obj, Object obj2) {
                        return HtmlWriter.this.wrapMessageLink(((Integer) obj).intValue(), (String) obj2);
                    }
                });
                messageInfo2 = (MessageInfo) pairPushMessage.first;
                sb.append((String) pairPushMessage.second);
                this._messagesCount++;
                messageInfo = messageInfo2;
                i3 = i;
                sb2 = sb;
                arrayList = arrayList;
                z = false;
            }
        }
        if (messageInfo2 != null) {
            this._lastMessageInfo = messageInfo2;
        }
        return sb2.toString().isEmpty() ? AbstractWriter.Result.Success() : this._chat.writeBlock(sb2.toString());
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writeDialogEnd() {
        String str;
        AbstractWriter.Result resultWriteEmptySinglePeer = writeEmptySinglePeer();
        if (!resultWriteEmptySinglePeer.isSuccess()) {
            return resultWriteEmptySinglePeer;
        }
        AbstractWriter.Result resultClose = this._chat.close();
        if (!resultClose.isSuccess()) {
            return resultClose;
        }
        if (this._settings.onlySinglePeer()) {
            return AbstractWriter.Result.Success();
        }
        int i = AnonymousClass2.$SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[this._dialog.type.ordinal()];
        int iPeerColorIndex = 3;
        if (i == 1) {
            str = "Saved messages";
        } else if (i == 2) {
            str = "Replies";
        } else if (i == 3) {
            str = "Verification Codes";
        } else {
            str = this._dialog.name;
        }
        ApiWrap$DialogInfo apiWrap$DialogInfo = this._dialog;
        ApiWrap$DialogInfo.Type type = apiWrap$DialogInfo.type;
        ApiWrap$DialogInfo.Type type2 = ApiWrap$DialogInfo.Type.Personal;
        String str2 = _UrlKt.FRAGMENT_ENCODE_SET;
        String str3 = (type == type2 || type == ApiWrap$DialogInfo.Type.Bot) ? apiWrap$DialogInfo.lastName : _UrlKt.FRAGMENT_ENCODE_SET;
        UserpicData userpicData = new UserpicData();
        ApiWrap$DialogInfo apiWrap$DialogInfo2 = this._dialog;
        ApiWrap$DialogInfo.Type type3 = apiWrap$DialogInfo2.type;
        if (type3 != ApiWrap$DialogInfo.Type.Self && type3 != ApiWrap$DialogInfo.Type.Replies && type3 != ApiWrap$DialogInfo.Type.VerifyCodes) {
            iPeerColorIndex = DataTypesUtils.PeerColorIndex(apiWrap$DialogInfo2.peerId);
        }
        userpicData.pixelSize = 48;
        userpicData.colorIndex = iPeerColorIndex;
        userpicData.firstName = str;
        userpicData.lastName = str3;
        AbstractWriter.Result resultValidateDialogsMode = validateDialogsMode(this._dialog.isLeftChannel);
        if (!resultValidateDialogsMode.isSuccess()) {
            return resultValidateDialogsMode;
        }
        HtmlContext htmlContext = this._chats;
        String strComposeName = DataTypesUtils.ComposeName(userpicData, DataTypesUtils.DeletedString(this._dialog.type));
        String strCountString = DataTypesUtils.CountString(this._messagesCount, this._dialog.onlyMyMessages);
        String strTypeString = DataTypesUtils.TypeString(this._dialog.type);
        if (this._messagesCount > 0) {
            str2 = this._dialog.relativePath + "messages.html";
        }
        return htmlContext.writeBlock(htmlContext.pushListEntry(userpicData, strComposeName, strCountString, strTypeString, str2));
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writeDialogsEnd() {
        HtmlContext htmlContext = this._chats;
        if (htmlContext != null) {
            return htmlContext.close();
        }
        return AbstractWriter.Result.Success();
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writeSessionsList(ApiWrap$SessionsList apiWrap$SessionsList) {
        AbstractWriter.Result resultWriteSessions = writeSessions(apiWrap$SessionsList);
        if (!resultWriteSessions.isSuccess()) {
            return resultWriteSessions;
        }
        AbstractWriter.Result resultWriteWebSessions = writeWebSessions(apiWrap$SessionsList);
        return !resultWriteWebSessions.isSuccess() ? resultWriteWebSessions : AbstractWriter.Result.Success();
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writeUserpicsStart(ApiWrap$UserpicsInfo apiWrap$UserpicsInfo) {
        int iCount = apiWrap$UserpicsInfo.count();
        this._userpicsCount = iCount;
        if (iCount == 0) {
            return AbstractWriter.Result.Success();
        }
        this._userpics = fileWithRelativePath(userpicsFilePath());
        AbstractWriter.Result resultWriteBlock = this._userpics.writeBlock(this._userpics.pushHeader("Profile pictures", "export_results.html") + this._userpics.pushDiv("page_body list_page") + this._userpics.pushDiv("entry_list"));
        if (!resultWriteBlock.isSuccess()) {
            return resultWriteBlock;
        }
        if (this._delayedPersonalInfo == null) {
            pushSection(4, "Profile pictures", "photos", this._userpicsCount, userpicsFilePath());
        }
        return AbstractWriter.Result.Success();
    }

    private AbstractWriter.Result writeEmptySinglePeer() {
        if (!this._settings.onlySinglePeer() || this._messagesCount != 0) {
            return AbstractWriter.Result.Success();
        }
        AbstractWriter.Result resultWriteDialogOpening = writeDialogOpening(0);
        if (!resultWriteDialogOpening.isSuccess()) {
            return resultWriteDialogOpening;
        }
        HtmlContext htmlContext = this._chat;
        int i = this._dateMessageId - 1;
        this._dateMessageId = i;
        return htmlContext.writeBlock(htmlContext.pushServiceMessage(i, this._dialog, this._settings.path, "No exported messages", null));
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writeUserpicsSlice(ArrayList<Photo> arrayList) {
        String fileSize;
        String date;
        int i = 0;
        AbstractWriter.Result resultWriteDelayedPersonal = writeDelayedPersonal(arrayList.get(0).image.file.relativePath);
        if (!resultWriteDelayedPersonal.isSuccess()) {
            return resultWriteDelayedPersonal;
        }
        StringBuilder sb = new StringBuilder();
        int size = arrayList.size();
        while (i < size) {
            Photo photo = arrayList.get(i);
            i++;
            Photo photo2 = photo;
            UserpicData userpicData = new UserpicData();
            userpicData.colorIndex = this._selfColorIndex;
            userpicData.pixelSize = 48;
            ApiWrap$File apiWrap$File = photo2.image.file;
            int i2 = AnonymousClass2.$SwitchMap$com$exteragram$messenger$export$api$ApiWrap$File$SkipReason[apiWrap$File.skipReason.ordinal()];
            if (i2 == 1) {
                fileSize = "(Photo unavailable, please try again later)";
            } else if (i2 == 2) {
                fileSize = "(Photo exceeds maximum size. Change data exporting settings to download.)";
            } else if (i2 == 3) {
                fileSize = "(Photo not included. Change data exporting settings to download.)";
            } else {
                fileSize = i2 != 4 ? null : AndroidUtilities.formatFileSize(apiWrap$File.size);
            }
            String str = fileSize;
            String str2 = photo2.image.file.relativePath;
            userpicData.imageLink = WriteUserpicThumb(this._settings.path, str2, userpicData);
            userpicData.firstName = str2;
            HtmlContext htmlContext = this._userpics;
            String str3 = str2.isEmpty() ? "Photo unavailable" : str2;
            int i3 = photo2.date;
            if (i3 > 0) {
                date = LocaleController.formatDate(i3);
            } else {
                date = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            sb.append(htmlContext.pushListEntry(userpicData, str3, str, date, str2));
        }
        return this._userpics.writeBlock(sb.toString());
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.export.output.html.HtmlWriter$2, reason: invalid class name */
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
                iArr2[ApiWrap$DialogInfo.Type.Self.ordinal()] = 1;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[ApiWrap$DialogInfo.Type.Replies.ordinal()] = 2;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$api$ApiWrap$DialogInfo$Type[ApiWrap$DialogInfo.Type.VerifyCodes.ordinal()] = 3;
            } catch (NoSuchFieldError unused8) {
            }
        }
    }

    public AbstractWriter.Result writeDelayedPersonal(String str) {
        ApiWrap$ExportPersonalInfo apiWrap$ExportPersonalInfo = this._delayedPersonalInfo;
        if (apiWrap$ExportPersonalInfo == null) {
            return AbstractWriter.Result.Success();
        }
        AbstractWriter.Result resultWritePreparedPersonal = writePreparedPersonal(apiWrap$ExportPersonalInfo, str);
        if (!resultWritePreparedPersonal.isSuccess()) {
            return resultWritePreparedPersonal;
        }
        int i = this._userpicsCount;
        if (i != 0) {
            pushSection(4, "Profile pictures", "photos", i, userpicsFilePath());
        }
        return AbstractWriter.Result.Success();
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writeUserpicsEnd() {
        AbstractWriter.Result resultWriteDelayedPersonal = writeDelayedPersonal(_UrlKt.FRAGMENT_ENCODE_SET);
        if (!resultWriteDelayedPersonal.isSuccess()) {
            return resultWriteDelayedPersonal;
        }
        HtmlContext htmlContext = this._userpics;
        if (htmlContext != null) {
            return htmlContext.close();
        }
        return AbstractWriter.Result.Success();
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writeStoriesStart(int i) {
        this._storiesCount = i;
        if (i == 0) {
            return AbstractWriter.Result.Success();
        }
        this._stories = fileWithRelativePath(storiesFilePath());
        AbstractWriter.Result resultWriteBlock = this._stories.writeBlock(this._stories.pushHeader("Stories archive", "export_results.html") + this._stories.pushDiv("page_body list_page") + this._stories.pushDiv("entry_list"));
        return !resultWriteBlock.isSuccess() ? resultWriteBlock : AbstractWriter.Result.Success();
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writeStoriesSlice(ApiWrap$StoriesSlice apiWrap$StoriesSlice) {
        String str;
        String date;
        this._storiesCount -= apiWrap$StoriesSlice.skipped;
        ArrayList<ApiWrap$Story> arrayList = apiWrap$StoriesSlice.list;
        if (arrayList == null || arrayList.isEmpty()) {
            return AbstractWriter.Result.Success();
        }
        StringBuilder sb = new StringBuilder();
        ArrayList<ApiWrap$Story> arrayList2 = apiWrap$StoriesSlice.list;
        int size = arrayList2.size();
        int i = 0;
        while (i < size) {
            ApiWrap$Story apiWrap$Story = arrayList2.get(i);
            i++;
            ApiWrap$Story apiWrap$Story2 = apiWrap$Story;
            ApiWrap$StoryData apiWrap$StoryData = new ApiWrap$StoryData();
            ApiWrap$File apiWrap$FileFile = apiWrap$Story2.file();
            ArrayList<String> arrayList3 = new ArrayList<>();
            if (apiWrap$Story2.pinned) {
                arrayList3.add("Saved to Profile");
            }
            if (apiWrap$Story2.expires > 0) {
                arrayList3.add("Expiring: ");
                arrayList3.add(LocaleController.formatDate(apiWrap$Story2.expires));
            }
            int i2 = AnonymousClass2.$SwitchMap$com$exteragram$messenger$export$api$ApiWrap$File$SkipReason[apiWrap$FileFile.skipReason.ordinal()];
            if (i2 != 1 && i2 != 2 && i2 != 3) {
                if (i2 != 4) {
                    if (i2 != 5) {
                        throw new IncompatibleClassChangeError();
                    }
                    throw new IllegalStateException("Skip reason while writing story path.");
                }
                AndroidUtilities.formatFileSize(apiWrap$FileFile.size);
            }
            String str2 = apiWrap$Story2.file().relativePath;
            if (apiWrap$Story2.thumb().file.relativePath.isEmpty()) {
                str = apiWrap$Story2.file().relativePath;
            } else {
                str = apiWrap$Story2.thumb().file.relativePath;
            }
            apiWrap$StoryData.imageLink = (String) HtmlContext.WriteImageThumb(this._settings.path, str, new Function() { // from class: com.exteragram.messenger.export.output.html.HtmlWriter$$ExternalSyntheticLambda3
                @Override // java.util.function.Function
                public final Object apply(Object obj) {
                    return HtmlWriter.m1081$r8$lambda$B8MGwMXHL_evNdsjLxRh5KBA((Dimension) obj);
                }
            }, null, 0, null).first;
            int i3 = apiWrap$Story2.date;
            if (i3 > 0) {
                date = LocaleController.formatDate(i3);
            } else {
                date = _UrlKt.FRAGMENT_ENCODE_SET;
            }
            sb.append(this._stories.pushStoriesListEntry(apiWrap$StoryData, str2.isEmpty() ? "Story unavailable" : str2, arrayList3, date, apiWrap$Story2.caption, "_environment.internalLinksDomain", str2));
        }
        return this._stories.writeBlock(sb.toString());
    }

    /* JADX INFO: renamed from: $r8$lambda$B8MGw-MXHL_evNdsjLxR-h5-KBA, reason: not valid java name */
    public static /* synthetic */ Dimension m1081$r8$lambda$B8MGwMXHL_evNdsjLxRh5KBA(Dimension dimension) {
        return new Dimension(90, 160);
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writeStoriesEnd() {
        pushSection(5, "Stories archive", "stories", this._storiesCount, storiesFilePath());
        HtmlContext htmlContext = this._stories;
        if (htmlContext != null) {
            return htmlContext.close();
        }
        return AbstractWriter.Result.Success();
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writeContactsList(ApiWrap$ContactsList apiWrap$ContactsList) {
        AbstractWriter.Result resultWriteSavedContacts = writeSavedContacts(apiWrap$ContactsList);
        if (!resultWriteSavedContacts.isSuccess()) {
            return resultWriteSavedContacts;
        }
        AbstractWriter.Result resultWriteFrequentContacts = writeFrequentContacts(apiWrap$ContactsList);
        return !resultWriteFrequentContacts.isSuccess() ? resultWriteFrequentContacts : AbstractWriter.Result.Success();
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result writeOtherData(ApiWrap$File apiWrap$File) {
        pushSection(8, "Other data", "other", 1, apiWrap$File.relativePath);
        return AbstractWriter.Result.Success();
    }

    private AbstractWriter.Result writeSavedContacts(ApiWrap$ContactsList apiWrap$ContactsList) {
        ArrayList<ApiWrap$ContactInfo> arrayList = apiWrap$ContactsList.list;
        if (arrayList == null || arrayList.isEmpty()) {
            return AbstractWriter.Result.Success();
        }
        HtmlContext htmlContextFileWithRelativePath = fileWithRelativePath("lists/contacts.html");
        StringBuilder sb = new StringBuilder(htmlContextFileWithRelativePath.pushHeader("Contacts", "export_results.html"));
        sb.append(htmlContextFileWithRelativePath.pushDiv("page_body list_page"));
        int i = 0;
        sb.append(htmlContextFileWithRelativePath.pushAbout("_environment.aboutContacts", false));
        sb.append(htmlContextFileWithRelativePath.pushDiv("entry_list"));
        ArrayList<Integer> arrayListSortedContactsIndices = DataTypesUtils.SortedContactsIndices(apiWrap$ContactsList);
        int size = arrayListSortedContactsIndices.size();
        while (i < size) {
            int i2 = i + 1;
            ApiWrap$ContactInfo apiWrap$ContactInfo = apiWrap$ContactsList.list.get(arrayListSortedContactsIndices.get(i).intValue());
            UserpicData userpicData = new UserpicData();
            userpicData.colorIndex = apiWrap$ContactInfo.colorIndex;
            userpicData.pixelSize = 48;
            userpicData.firstName = apiWrap$ContactInfo.firstName;
            userpicData.lastName = apiWrap$ContactInfo.lastName;
            if (apiWrap$ContactInfo.userId.longValue() != 0) {
                userpicData.tooltip = "ID: " + apiWrap$ContactInfo.userId;
            }
            sb.append(htmlContextFileWithRelativePath.pushListEntry(userpicData, DataTypesUtils.ComposeName(userpicData, "Deleted Account"), PhoneFormat.getInstance().format(apiWrap$ContactInfo.phoneNumber), LocaleController.formatDate(apiWrap$ContactInfo.date), _UrlKt.FRAGMENT_ENCODE_SET));
            i = i2;
        }
        AbstractWriter.Result resultWriteBlock = htmlContextFileWithRelativePath.writeBlock(sb.toString());
        if (!resultWriteBlock.isSuccess()) {
            return resultWriteBlock;
        }
        AbstractWriter.Result resultClose = htmlContextFileWithRelativePath.close();
        if (!resultClose.isSuccess()) {
            return resultClose;
        }
        pushSection(2, "Contacts", "contacts", apiWrap$ContactsList.list.size(), "lists/contacts.html");
        return AbstractWriter.Result.Success();
    }

    private AbstractWriter.Result writeFrequentContacts(ApiWrap$ContactsList apiWrap$ContactsList) {
        int size = apiWrap$ContactsList.correspondents.size() + apiWrap$ContactsList.inlineBots.size() + apiWrap$ContactsList.phoneCalls.size();
        if (size == 0) {
            return AbstractWriter.Result.Success();
        }
        final HtmlContext htmlContextFileWithRelativePath = fileWithRelativePath("lists/frequent.html");
        final StringBuilder sb = new StringBuilder(htmlContextFileWithRelativePath.pushHeader("Frequent contacts", "export_results.html"));
        sb.append(htmlContextFileWithRelativePath.pushDiv("page_body list_page"));
        sb.append(htmlContextFileWithRelativePath.pushAbout("_environment.aboutFrequent", false));
        sb.append(htmlContextFileWithRelativePath.pushDiv("entry_list"));
        Utilities.Callback2<ArrayList<ApiWrap$TopPeer>, String> callback2 = new Utilities.Callback2<ArrayList<ApiWrap$TopPeer>, String>() { // from class: com.exteragram.messenger.export.output.html.HtmlWriter.1
            @Override // org.telegram.messenger.Utilities.Callback2
            public void run(ArrayList<ApiWrap$TopPeer> arrayList, String str) {
                String strName;
                String str2;
                int size2 = arrayList.size();
                int i = 0;
                while (i < size2) {
                    ApiWrap$TopPeer apiWrap$TopPeer = arrayList.get(i);
                    i++;
                    ApiWrap$TopPeer apiWrap$TopPeer2 = apiWrap$TopPeer;
                    ApiWrap$Peer apiWrap$Peer = apiWrap$TopPeer2.peer;
                    if (apiWrap$Peer.chat != null) {
                        strName = apiWrap$Peer.name();
                    } else {
                        ApiWrap$User apiWrap$User = apiWrap$Peer.user;
                        if (apiWrap$User.isSelf) {
                            strName = "Saved messages";
                        } else {
                            strName = apiWrap$User.info.firstName;
                        }
                    }
                    ApiWrap$User apiWrap$User2 = apiWrap$TopPeer2.peer.user;
                    if (apiWrap$User2 != null && !apiWrap$User2.isSelf) {
                        str2 = apiWrap$User2.info.lastName;
                    } else {
                        str2 = _UrlKt.FRAGMENT_ENCODE_SET;
                    }
                    UserpicData userpicData = new UserpicData();
                    userpicData.colorIndex = DataTypesUtils.PeerColorIndex(apiWrap$TopPeer2.peer.id());
                    userpicData.pixelSize = 48;
                    userpicData.firstName = strName;
                    userpicData.lastName = str2;
                    String str3 = str;
                    sb.append(htmlContextFileWithRelativePath.pushListEntry(userpicData, DataTypesUtils.ComposeName(userpicData, "Deleted Account"), "Rating: " + apiWrap$TopPeer2.rating, str3, _UrlKt.FRAGMENT_ENCODE_SET));
                    str = str3;
                }
            }
        };
        callback2.run(apiWrap$ContactsList.correspondents, "people");
        callback2.run(apiWrap$ContactsList.inlineBots, "inline bots");
        callback2.run(apiWrap$ContactsList.phoneCalls, "calls");
        AbstractWriter.Result resultWriteBlock = htmlContextFileWithRelativePath.writeBlock(sb.toString());
        if (!resultWriteBlock.isSuccess()) {
            return resultWriteBlock;
        }
        AbstractWriter.Result resultClose = htmlContextFileWithRelativePath.close();
        if (!resultClose.isSuccess()) {
            return resultClose;
        }
        pushSection(3, "Frequent contacts", "frequent", size, "lists/frequent.html");
        return AbstractWriter.Result.Success();
    }

    @Override // com.exteragram.messenger.export.output.AbstractWriter
    public AbstractWriter.Result finish() {
        if (this._settings.onlySinglePeer()) {
            return AbstractWriter.Result.Success();
        }
        AbstractWriter.Result resultWriteSections = writeSections();
        if (!resultWriteSections.isSuccess()) {
            return resultWriteSections;
        }
        StringBuilder sb = new StringBuilder();
        if (this._haveSections) {
            sb.append(this._summary.popTag());
            this._summaryNeedDivider = true;
            this._haveSections = false;
        }
        sb.append(this._summary.pushAbout("about telegram bla bla lorum ipsum", this._summaryNeedDivider));
        AbstractWriter.Result resultWriteBlock = this._summary.writeBlock(sb.toString());
        return !resultWriteBlock.isSuccess() ? resultWriteBlock : this._summary.close();
    }

    private AbstractWriter.Result validateDialogsMode(boolean z) {
        String str;
        DialogsMode dialogsMode = z ? DialogsMode.Left : DialogsMode.Chats;
        DialogsMode dialogsMode2 = this._dialogsMode;
        if (dialogsMode2 == dialogsMode) {
            return AbstractWriter.Result.Success();
        }
        if (dialogsMode2 != DialogsMode.None) {
            HtmlContext htmlContext = this._chats;
            AbstractWriter.Result resultWriteBlock = htmlContext.writeBlock(htmlContext.popTag());
            if (!resultWriteBlock.isSuccess()) {
                return resultWriteBlock;
            }
        }
        this._dialogsMode = dialogsMode;
        StringBuilder sb = new StringBuilder();
        HtmlContext htmlContext2 = this._chats;
        if (z) {
            str = "left chats";
        } else {
            str = "just a chat";
        }
        sb.append(htmlContext2.pushAbout(str, false));
        sb.append(this._chats.pushDiv("entry_list"));
        return this._chats.writeBlock(sb.toString());
    }

    private String SerializeList(ArrayList<String> arrayList) {
        int size = arrayList.size();
        if (size == 1) {
            return arrayList.get(0);
        }
        if (size > 1) {
            StringBuilder sb = new StringBuilder(arrayList.get(0));
            int i = 1;
            while (true) {
                int i2 = size - 1;
                if (i != i2) {
                    sb.append(", ");
                    sb.append(arrayList.get(i));
                    i++;
                } else {
                    return ((Object) sb) + " and " + arrayList.get(i2);
                }
            }
        } else {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public String wrapMessageLink(final int i, String str) {
        Optional<Integer> optionalFindFirst = this._lastMessageIdsPerFile.stream().filter(new Predicate() { // from class: com.exteragram.messenger.export.output.html.HtmlWriter$$ExternalSyntheticLambda5
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return HtmlWriter.$r8$lambda$SUVYRCCFgnq2nXdHrqF2pYunQwQ(i, (Integer) obj);
            }
        }).findFirst();
        if (!optionalFindFirst.isPresent()) {
            return "<a href=\"#go_to_message" + i + "\" onclick=\"return GoToMessage(" + i + ")\">" + str + "</a>";
        }
        return "<a href=\"" + messagesFile(optionalFindFirst.get().intValue() - this._lastMessageIdsPerFile.get(0).intValue()) + "#go_to_message" + i + "\">" + str + "</a>";
    }

    public static /* synthetic */ boolean $r8$lambda$SUVYRCCFgnq2nXdHrqF2pYunQwQ(int i, Integer num) {
        return i <= num.intValue();
    }

    private HtmlContext fileWithRelativePath(String str) {
        return new HtmlContext(HtmlContext.pathWithRelativePath(str), this._settings.path, this._stats);
    }
}
