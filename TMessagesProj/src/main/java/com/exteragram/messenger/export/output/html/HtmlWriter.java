package com.exteragram.messenger.export.output.html;

import android.util.Pair;
import androidx.camera.core.ImageCapture$$ExternalSyntheticBackport1;
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
import okio.Segment$$ExternalSyntheticBUOutline1;
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
        return (String) HtmlContext.WriteImageThumb(str, str2, new Function() { 
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return HtmlWriter.m1080$r8$lambda$0DixWPzMjZ6lOOvAG9W5PMq7o(userpicData, (Dimension) obj);
            }
        }, null, 0, "_thumb").first;
    }

    public static Dimension $r8$lambda$LUfwZiBWR44LYKa4oSGSBHvO5Xg(UserpicData userpicData, Dimension dimension) {
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

    @Override 
    public AbstractWriter.Result writeDialogsStart(ApiWrap$DialogsInfo apiWrap$DialogsInfo) {
        if (this._chats != null) {
            Segment$$ExternalSyntheticBUOutline1.m("chats already initialized!");
            return null;
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
        Collections.sort(this._savedSections, Comparator.comparing(new Function() { 
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

    class AnonymousClass2 {
        static final Dimension m1081$r8$lambda$B8MGwMXHL_evNdsjLxRh5KBA(Dimension dimension) {
        return new Dimension(90, 160);
    }

    @Override 
    public AbstractWriter.Result writeStoriesEnd() {
        pushSection(5, "Stories archive", "stories", this._storiesCount, storiesFilePath());
        HtmlContext htmlContext = this._stories;
        if (htmlContext != null) {
            return htmlContext.close();
        }
        return AbstractWriter.Result.Success();
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
        Utilities.Callback2<ArrayList<ApiWrap$TopPeer>, String> callback2 = new Utilities.Callback2<ArrayList<ApiWrap$TopPeer>, String>() { 
            @Override 
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

    @Override 
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

    public String wrapMessageLink(final int i, String str) {
        Optional<Integer> optionalFindFirst = this._lastMessageIdsPerFile.stream().filter(new Predicate() { 
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
