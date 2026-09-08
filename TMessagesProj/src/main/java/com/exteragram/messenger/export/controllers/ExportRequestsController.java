package com.exteragram.messenger.export.controllers;

import android.support.v4.media.session.MediaSessionCompat$$ExternalSyntheticThrowCCEIfNotNull0;
import android.util.Log;
import androidx.camera.core.ImageCapture$$ExternalSyntheticBackport1;
import c.f$$ExternalSyntheticBUOutline1;
import com.android.dx.AppDataDirGuesser;
import com.android.dx.DexMaker$$ExternalSyntheticBUOutline0;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.export.ExportSettings;
import com.exteragram.messenger.export.api.ApiWrap$Chat;
import com.exteragram.messenger.export.api.ApiWrap$ChatProcess;
import com.exteragram.messenger.export.api.ApiWrap$ChatsProcess;
import com.exteragram.messenger.export.api.ApiWrap$ContactInfo;
import com.exteragram.messenger.export.api.ApiWrap$ContactsList;
import com.exteragram.messenger.export.api.ApiWrap$ContactsProcess;
import com.exteragram.messenger.export.api.ApiWrap$DialogInfo;
import com.exteragram.messenger.export.api.ApiWrap$DialogsInfo;
import com.exteragram.messenger.export.api.ApiWrap$DialogsProcess;
import com.exteragram.messenger.export.api.ApiWrap$Document;
import com.exteragram.messenger.export.api.ApiWrap$DownloadProgress;
import com.exteragram.messenger.export.api.ApiWrap$ExportPersonalInfo;
import com.exteragram.messenger.export.api.ApiWrap$File;
import com.exteragram.messenger.export.api.ApiWrap$FileLocation;
import com.exteragram.messenger.export.api.ApiWrap$FileOrigin;
import com.exteragram.messenger.export.api.ApiWrap$FileProcess;
import com.exteragram.messenger.export.api.ApiWrap$FileProgress;
import com.exteragram.messenger.export.api.ApiWrap$LeftChannelsProcess;
import com.exteragram.messenger.export.api.ApiWrap$LoadedFileCache;
import com.exteragram.messenger.export.api.ApiWrap$Media;
import com.exteragram.messenger.export.api.ApiWrap$Message;
import com.exteragram.messenger.export.api.ApiWrap$MessageId;
import com.exteragram.messenger.export.api.ApiWrap$MessagesSlice;
import com.exteragram.messenger.export.api.ApiWrap$OtherDataProcess;
import com.exteragram.messenger.export.api.ApiWrap$ParseMediaContext;
import com.exteragram.messenger.export.api.ApiWrap$Peer;
import com.exteragram.messenger.export.api.ApiWrap$Reaction;
import com.exteragram.messenger.export.api.ApiWrap$SessionsList;
import com.exteragram.messenger.export.api.ApiWrap$StoriesProcess;
import com.exteragram.messenger.export.api.ApiWrap$StoriesSlice;
import com.exteragram.messenger.export.api.ApiWrap$Story;
import com.exteragram.messenger.export.api.ApiWrap$TextPart;
import com.exteragram.messenger.export.api.ApiWrap$User;
import com.exteragram.messenger.export.api.ApiWrap$UserpicsInfo;
import com.exteragram.messenger.export.api.ApiWrap$UserpicsProcess;
import com.exteragram.messenger.export.api.DataTypesUtils;
import com.exteragram.messenger.export.api.ExportRequests$FinishTakeoutSession;
import com.exteragram.messenger.export.api.ExportRequests$InitTakeoutSession;
import com.exteragram.messenger.export.api.ExportRequests$InvokeWithMessagesRange;
import com.exteragram.messenger.export.api.ExportRequests$InvokeWithTakeoutWrapper;
import com.exteragram.messenger.export.api.ExportRequests$Takeout;
import com.exteragram.messenger.export.api.ExportRequests$getLeftChannels;
import com.exteragram.messenger.export.output.OutputFile;
import com.exteragram.messenger.export.output.html.HtmlWriter;
import com.google.android.gms.cast.CastStatusCodes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
import okhttp3.OkHttpClient$Builder$$ExternalSyntheticBUOutline0;
import okhttp3.internal.url._UrlKt;
import okio.Buffer$$ExternalSyntheticBUOutline4;
import okio.Segment$$ExternalSyntheticBUOutline1;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.SaveToGallerySettingsHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.InputSerializedData;
import org.telegram.tgnet.OutputSerializedData;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_stories;

public class ExportRequestsController {
    private static final ExportRequestsController[] Instance = new ExportRequestsController[16];
    private ApiWrap$ChatProcess _chatProcess;
    private ApiWrap$ContactsProcess _contactsProcess;
    private ApiWrap$DialogsProcess _dialogsProcess;
    private ApiWrap$FileProcess _fileProcess;
    private ApiWrap$LeftChannelsProcess _leftChannelsProcess;
    private ApiWrap$OtherDataProcess _otherDataProcess;
    private long _selfId;
    private ExportSettings _settings;
    private StartProcess _startProcess;
    private OutputFile.Stats _stats;
    private ApiWrap$StoriesProcess _storiesProcess;
    private long _takeoutId;
    private ApiWrap$UserpicsProcess _userpicsProcess;
    private final int selectedAcc;
    public int index = 0;
    private final Set<Long> _unresolvedCustomEmoji = new HashSet();
    private final HashMap<Long, ApiWrap$Document> _resolvedCustomEmoji = new HashMap<>();
    private final ApiWrap$LoadedFileCache _fileCache = new ApiWrap$LoadedFileCache(AppDataDirGuesser.PER_USER_RANGE);
    public ArrayList<TLRPC.TL_messageRange> splits = new ArrayList<>();

    public static class StartInfo {
        public int userpicsCount = 0;
        public int storiesCount = 0;
        public int dialogsCount = 0;
    }

    public static class StartProcess {
        public Utilities.Callback<StartInfo> done;
        public ArrayList<Step> steps = new ArrayList<>();
        public int splitIndex = 0;
        public StartInfo info = new StartInfo();

        public enum Step {
            UserpicsCount,
            StoriesCount,
            SplitRanges,
            DialogsCount,
            LeftChannelsCount
        }
    }

    private ExportRequestsController(int i) {
        this.selectedAcc = i;
    }

    public static ExportRequestsController getInstance(int i) {
        ExportRequestsController exportRequestsController;
        ExportRequestsController[] exportRequestsControllerArr = Instance;
        ExportRequestsController exportRequestsController2 = exportRequestsControllerArr[i];
        if (exportRequestsController2 != null) {
            return exportRequestsController2;
        }
        synchronized (ExportRequestsController.class) {
            try {
                exportRequestsController = exportRequestsControllerArr[i];
                if (exportRequestsController == null) {
                    exportRequestsController = new ExportRequestsController(i);
                    exportRequestsControllerArr[i] = exportRequestsController;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return exportRequestsController;
    }

    public static ApiWrap$DialogsInfo ParseDialogsInfo(TLRPC.InputPeer inputPeer, Vector vector) {
        long j;
        if (inputPeer instanceof TLRPC.TL_inputPeerUser) {
            j = ((TLRPC.TL_inputPeerUser) inputPeer).user_id;
        } else {
            if (!(inputPeer instanceof TLRPC.TL_inputPeerSelf)) {
                DexMaker$$ExternalSyntheticBUOutline0.m("wtf is it: ", inputPeer);
                return null;
            }
            j = 0;
        }
        ApiWrap$DialogsInfo apiWrap$DialogsInfo = new ApiWrap$DialogsInfo();
        ArrayList<T> arrayList = vector.objects;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            if (obj instanceof TLRPC.User) {
                TLRPC.User user = (TLRPC.User) obj;
                if (user.id == j || (j == 0 && user.self)) {
                    apiWrap$DialogsInfo.chats.add(DataTypesUtils.DialogInfoFromUser(DataTypesUtils.ParseUser(user)));
                }
            }
        }
        return apiWrap$DialogsInfo;
    }

    public static ApiWrap$DialogsInfo ParseDialogsInfo(TLRPC.InputPeer inputPeer, TLRPC.messages_Chats messages_chats) {
        long j;
        long j2;
        if (inputPeer instanceof TLRPC.TL_inputPeerChat) {
            j = ((TLRPC.TL_inputPeerChat) inputPeer).chat_id;
        } else if (inputPeer instanceof TLRPC.TL_inputPeerChannel) {
            j = ((TLRPC.TL_inputPeerChannel) inputPeer).channel_id;
        } else {
            DexMaker$$ExternalSyntheticBUOutline0.m("illegal type: ", inputPeer);
            return null;
        }
        ApiWrap$DialogsInfo apiWrap$DialogsInfo = new ApiWrap$DialogsInfo();
        ArrayList<TLRPC.Chat> arrayList = messages_chats.chats;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            TLRPC.Chat chat = arrayList.get(i);
            i++;
            TLRPC.Chat chat2 = chat;
            if (chat2 instanceof TLRPC.TL_channel) {
                j2 = ((TLRPC.TL_channel) chat2).id;
            } else if (chat2 instanceof TLRPC.TL_channelForbidden) {
                j2 = ((TLRPC.TL_channelForbidden) chat2).id;
            } else {
                j2 = chat2.id;
            }
            if (j2 == j) {
                ApiWrap$DialogInfo apiWrap$DialogInfoDialogInfoFromChat = DataTypesUtils.DialogInfoFromChat(DataTypesUtils.ParseChat(chat2));
                apiWrap$DialogInfoDialogInfoFromChat.isLeftChannel = false;
                apiWrap$DialogsInfo.chats.add(apiWrap$DialogInfoDialogInfoFromChat);
            }
        }
        return apiWrap$DialogsInfo;
    }

    public void startExport(ExportSettings exportSettings, OutputFile.Stats stats, Utilities.Callback<StartInfo> callback) {
        this._settings = exportSettings;
        this._stats = stats;
        StartProcess startProcess = new StartProcess();
        this._startProcess = startProcess;
        startProcess.done = callback;
        if ((this._settings.types & 2) != 0) {
            startProcess.steps.add(StartProcess.Step.UserpicsCount);
        }
        if ((this._settings.types & 2048) != 0) {
            this._startProcess.steps.add(StartProcess.Step.StoriesCount);
        }
        if ((this._settings.types & CastStatusCodes.DEVICE_CONNECTION_SUSPENDED) != 0) {
            this._startProcess.steps.add(StartProcess.Step.SplitRanges);
            this._startProcess.steps.add(StartProcess.Step.DialogsCount);
        }
        ExportSettings exportSettings2 = this._settings;
        if ((exportSettings2.types & 1920) != 0 && !exportSettings2.onlySinglePeer()) {
            this._startProcess.steps.add(StartProcess.Step.LeftChannelsCount);
        }
        startMainSession(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.sendNextStartRequest();
            }
        });
    }

    public void sendNextStartRequest() {
        ArrayList<StartProcess.Step> arrayList = this._startProcess.steps;
        if (arrayList.isEmpty()) {
            finishStartProcess();
            Log.d("exteraGram", "caught empty steps in sendNextStartRequest, finishing start process...");
            return;
        }
        StartProcess.Step step = arrayList.get(0);
        arrayList.remove(0);
        int i = AnonymousClass1.$SwitchMap$com$exteragram$messenger$export$controllers$ExportRequestsController$StartProcess$Step[step.ordinal()];
        if (i == 1) {
            requestUserpicsCount();
            return;
        }
        if (i == 2) {
            requestStoriesCount();
            return;
        }
        if (i == 3) {
            requestSplitRanges();
        } else if (i == 4) {
            requestDialogsCount();
        } else {
            if (i != 5) {
                return;
            }
            requestLeftChannelsCount();
        }
    }

    public static void lambda$startMainSession$1(ExportRequests$InitTakeoutSession exportRequests$InitTakeoutSession, final Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof Vector) {
            ArrayList<T> arrayList = ((Vector) tLObject).objects;
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Object obj = arrayList.get(i);
                i++;
                TLObject tLObject2 = (TLObject) obj;
                if (tLObject2 instanceof TLRPC.User) {
                    TLRPC.User user = (TLRPC.User) tLObject2;
                    if (user.self) {
                        this._selfId = user.id;
                    }
                }
            }
            ConnectionsManager.getInstance(this.selectedAcc).sendRequest(exportRequests$InitTakeoutSession, new RequestDelegate() { 
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject3, TLRPC.TL_error tL_error2) {
                    this.f$0.lambda$startMainSession$0(runnable, tLObject3, tL_error2);
                }
            });
        }
    }

    public void lambda$requestUserpicsCount$2(TLObject tLObject, TLRPC.TL_error tL_error) {
        int size;
        if (tLObject instanceof TLRPC.photos_Photos) {
            TLRPC.photos_Photos photos_photos = (TLRPC.photos_Photos) tLObject;
            if (photos_photos instanceof TLRPC.TL_photos_photos) {
                size = ((TLRPC.TL_photos_photos) photos_photos).photos.size();
            } else {
                size = photos_photos instanceof TLRPC.TL_photos_photosSlice ? ((TLRPC.TL_photos_photosSlice) photos_photos).count : 0;
            }
            this._startProcess.info.userpicsCount = size;
            sendNextStartRequest();
        }
    }

    private void requestStoriesCount() {
        TL_stories.TL_stories_getStoriesArchive tL_stories_getStoriesArchive = new TL_stories.TL_stories_getStoriesArchive();
        tL_stories_getStoriesArchive.peer = new TLRPC.TL_inputPeerSelf();
        tL_stories_getStoriesArchive.limit = 0;
        tL_stories_getStoriesArchive.offset_id = 0;
        mainRequest(tL_stories_getStoriesArchive, new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$requestStoriesCount$3((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$requestSessions$5(final Utilities.Callback callback, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TL_account.authorizations) {
            final ApiWrap$SessionsList apiWrap$SessionsListParseSessionsList = DataTypesUtils.ParseSessionsList((TL_account.authorizations) tLObject);
            mainRequest(new TL_account.getWebAuthorizations(), new Utilities.Callback2() { 
                @Override 
                public final void run(Object obj, Object obj2) {
                    ExportRequestsController.$r8$lambda$zUhnSPX6kGtTskTPxh0MMvePxT4(apiWrap$SessionsListParseSessionsList, callback, (TLObject) obj, (TLRPC.TL_error) obj2);
                }
            });
        }
    }

    public static void lambda$requestLeftChannelsCount$6() {
        this._startProcess.info.dialogsCount += this._leftChannelsProcess.fullCount;
        sendNextStartRequest();
    }

    private void requestLeftChannelsSliceGeneric(final Runnable runnable) {
        ExportRequests$getLeftChannels exportRequests$getLeftChannels = new ExportRequests$getLeftChannels();
        exportRequests$getLeftChannels.offset = this._leftChannelsProcess.offset;
        mainRequest(exportRequests$getLeftChannels, new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$requestLeftChannelsSliceGeneric$7(runnable, (TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$requestMessagesCount$8(int i, TLRPC.messages_Messages messages_messages) {
        int size;
        if (messages_messages instanceof TLRPC.TL_messages_messages) {
            size = ((TLRPC.TL_messages_messages) messages_messages).messages.size();
        } else if (messages_messages instanceof TLRPC.TL_messages_messagesSlice) {
            size = ((TLRPC.TL_messages_messagesSlice) messages_messages).count;
        } else if (messages_messages instanceof TLRPC.TL_messages_channelMessages) {
            size = ((TLRPC.TL_messages_channelMessages) messages_messages).count;
        } else {
            size = messages_messages instanceof TLRPC.TL_messages_messagesNotModified ? -1 : 0;
        }
        if (size < 0) {
            Segment$$ExternalSyntheticBUOutline1.m("Unexpected messagesNotModified received");
        } else if (!DataTypesUtils.SingleMessageAfter(messages_messages, this._settings.singlePeerFrom)) {
            messagesCountLoaded(i, 0);
        } else {
            checkFirstMessageDate(i, size);
        }
    }

    private void checkFirstMessageDate(final int i, final int i2) {
        if (this._settings.singlePeerTill <= 0) {
            messagesCountLoaded(i, i2);
        } else {
            requestChatMessages(this._chatProcess.info.splits.get(i).intValue(), 1, -1, 1, new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.lambda$checkFirstMessageDate$9(i, i2, (TLRPC.messages_Messages) obj);
                }
            });
        }
    }

    public void lambda$requestMessagesSlice$10(TLRPC.messages_Messages messages_messages) {
        if (messages_messages instanceof TLRPC.TL_messages_messagesNotModified) {
            Segment$$ExternalSyntheticBUOutline1.m("Unexpected messagesNotModified received.");
            return;
        }
        if (messages_messages instanceof TLRPC.TL_messages_messages) {
            this._chatProcess.lastSlice = true;
        }
        ApiWrap$ChatProcess apiWrap$ChatProcess = this._chatProcess;
        loadMessagesFiles(DataTypesUtils.ParseMessagesSlice(apiWrap$ChatProcess.context, messages_messages.messages, messages_messages.users, messages_messages.chats, apiWrap$ChatProcess.info.relativePath));
    }

    private void loadMessagesFiles(ApiWrap$MessagesSlice apiWrap$MessagesSlice) {
        collectMessagesCustomEmoji(apiWrap$MessagesSlice);
        if (apiWrap$MessagesSlice.list.isEmpty()) {
            this._chatProcess.lastSlice = true;
        }
        ApiWrap$ChatProcess apiWrap$ChatProcess = this._chatProcess;
        apiWrap$ChatProcess.slice = apiWrap$MessagesSlice;
        apiWrap$ChatProcess.fileIndex = 0;
        resolveCustomEmoji();
    }

    private void collectMessagesCustomEmoji(ApiWrap$MessagesSlice apiWrap$MessagesSlice) {
        ArrayList<ApiWrap$Message> arrayList = apiWrap$MessagesSlice.list;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            ApiWrap$Message apiWrap$Message = arrayList.get(i);
            i++;
            ApiWrap$Message apiWrap$Message2 = apiWrap$Message;
            ArrayList<ApiWrap$TextPart> arrayList2 = apiWrap$Message2.text;
            int size2 = arrayList2.size();
            int i2 = 0;
            while (i2 < size2) {
                ApiWrap$TextPart apiWrap$TextPart = arrayList2.get(i2);
                i2++;
                ApiWrap$TextPart apiWrap$TextPart2 = apiWrap$TextPart;
                if (apiWrap$TextPart2.type == ApiWrap$TextPart.Type.CustomEmoji) {
                    long j = Long.parseLong(apiWrap$TextPart2.additional);
                    if (j != 0 && !this._resolvedCustomEmoji.containsKey(Long.valueOf(j))) {
                        this._unresolvedCustomEmoji.add(Long.valueOf(j));
                    }
                }
            }
            Iterator<ApiWrap$Reaction> it = apiWrap$Message2.reactions.iterator();
            if (it.hasNext()) {
                MediaSessionCompat$$ExternalSyntheticThrowCCEIfNotNull0.m(it.next());
                throw null;
            }
        }
    }

    private void resolveCustomEmoji() {
        if (this._unresolvedCustomEmoji.isEmpty()) {
            loadNextMessageFile();
            return;
        }
        int iMin = Math.min(this._unresolvedCustomEmoji.size(), 100);
        final ArrayList<Long> arrayList = new ArrayList<>(iMin);
        ArrayList arrayList2 = new ArrayList(this._unresolvedCustomEmoji);
        int size = arrayList2.size();
        for (int iMax = Math.max(0, arrayList2.size() - iMin); iMax != size; iMax++) {
            arrayList.add((Long) arrayList2.get(iMax));
            this._unresolvedCustomEmoji.remove(arrayList2.get(iMax));
        }
        final Runnable runnable = new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$resolveCustomEmoji$11(arrayList);
            }
        };
        TLRPC.TL_messages_getCustomEmojiDocuments tL_messages_getCustomEmojiDocuments = new TLRPC.TL_messages_getCustomEmojiDocuments();
        tL_messages_getCustomEmojiDocuments.document_id = arrayList;
        mainRequest(tL_messages_getCustomEmojiDocuments, new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$resolveCustomEmoji$12(runnable, (TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$loadFilePart$13(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        this._fileProcess.requestId = 0L;
        filePartDone(j, tLObject);
    }

    private void filePartRefreshReference(final long j) {
        ApiWrap$FileOrigin apiWrap$FileOrigin = this._fileProcess.origin;
        if (apiWrap$FileOrigin.storyId() != 0) {
            TL_stories.TL_stories_getStoriesByID tL_stories_getStoriesByID = new TL_stories.TL_stories_getStoriesByID();
            tL_stories_getStoriesByID.peer = new TLRPC.TL_inputPeerSelf();
            tL_stories_getStoriesByID.id = new ArrayList<>(Collections.singletonList(Integer.valueOf(apiWrap$FileOrigin.storyId())));
            this._fileProcess.requestId = mainRequest(tL_stories_getStoriesByID, new Utilities.Callback2() { 
                @Override 
                public final void run(Object obj, Object obj2) {
                    this.f$0.lambda$filePartRefreshReference$14(j, (TLObject) obj, (TLRPC.TL_error) obj2);
                }
            });
        } else if (apiWrap$FileOrigin.messageId() == 0) {
            Log.e("exteraGram", "FILE_REFERENCE error for non-message file.");
            return;
        }
        if ((apiWrap$FileOrigin.peer() instanceof TLRPC.TL_inputPeerChannel) || (apiWrap$FileOrigin.peer() instanceof TLRPC.TL_inputPeerChannelFromMessage)) {
            TLRPC.TL_channels_getMessages tL_channels_getMessages = new TLRPC.TL_channels_getMessages();
            tL_channels_getMessages.id = new ArrayList<>(Collections.singletonList(Integer.valueOf(apiWrap$FileOrigin.messageId())));
            if (apiWrap$FileOrigin.peer() instanceof TLRPC.TL_inputPeerChannel) {
                TLRPC.TL_inputChannel tL_inputChannel = new TLRPC.TL_inputChannel();
                tL_inputChannel.channel_id = apiWrap$FileOrigin.peer().channel_id;
                tL_inputChannel.access_hash = apiWrap$FileOrigin.peer().access_hash;
                tL_channels_getMessages.channel = tL_inputChannel;
            } else {
                TLRPC.TL_inputChannelFromMessage tL_inputChannelFromMessage = new TLRPC.TL_inputChannelFromMessage();
                tL_inputChannelFromMessage.peer = apiWrap$FileOrigin.peer().peer;
                tL_inputChannelFromMessage.access_hash = apiWrap$FileOrigin.peer().access_hash;
                tL_inputChannelFromMessage.channel_id = apiWrap$FileOrigin.peer().channel_id;
                tL_channels_getMessages.channel = tL_inputChannelFromMessage;
            }
            this._fileProcess.requestId = mainRequest(tL_channels_getMessages, new Utilities.Callback2() { 
                @Override 
                public final void run(Object obj, Object obj2) {
                    this.f$0.lambda$filePartRefreshReference$15(j, (TLObject) obj, (TLRPC.TL_error) obj2);
                }
            });
            return;
        }
        TLRPC.TL_messages_getMessages tL_messages_getMessages = new TLRPC.TL_messages_getMessages();
        tL_messages_getMessages.id = new ArrayList<>(Collections.singletonList(Integer.valueOf(apiWrap$FileOrigin.messageId())));
        this._fileProcess.requestId = splitRequest(apiWrap$FileOrigin.split(), tL_messages_getMessages, new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$filePartRefreshReference$16(j, (TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$filePartExtractReference$17(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_upload_file) {
            this._fileProcess.requestId = 0L;
            filePartDone(j, (TLRPC.TL_upload_file) tLObject);
        }
    }

    private void filePartExtractReference(final long j, TLRPC.messages_Messages messages_messages) {
        if (messages_messages instanceof TLRPC.TL_messages_messagesNotModified) {
            Segment$$ExternalSyntheticBUOutline1.m("wtf, TL_messages_messagesNotModified received!");
            return;
        }
        ApiWrap$ParseMediaContext apiWrap$ParseMediaContext = new ApiWrap$ParseMediaContext();
        apiWrap$ParseMediaContext.selfPeerId = this._selfId;
        ArrayList<ApiWrap$Message> arrayList = DataTypesUtils.ParseMessagesSlice(apiWrap$ParseMediaContext, messages_messages.messages, messages_messages.users, messages_messages.chats, this._chatProcess.info.relativePath).list;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            ApiWrap$Message apiWrap$Message = arrayList.get(i);
            i++;
            ApiWrap$Message apiWrap$Message2 = apiWrap$Message;
            if (apiWrap$Message2.id == this._fileProcess.origin.messageId()) {
                boolean zRefreshFileReference = DataTypesUtils.RefreshFileReference(this._fileProcess.location.data, apiWrap$Message2.getFile().location.data);
                boolean zRefreshFileReference2 = DataTypesUtils.RefreshFileReference(this._fileProcess.location.data, apiWrap$Message2.media.getThumb().file.location.data);
                if (zRefreshFileReference || zRefreshFileReference2) {
                    ApiWrap$FileProcess apiWrap$FileProcess = this._fileProcess;
                    apiWrap$FileProcess.requestId = fileRequest(apiWrap$FileProcess.location, j, new Utilities.Callback2() { 
                        @Override 
                        public final void run(Object obj, Object obj2) {
                            this.f$0.lambda$filePartExtractReference$18(j, (TLObject) obj, (TLRPC.TL_error) obj2);
                        }
                    });
                    return;
                }
            }
        }
        FileLog.w("Export Error: File unavailable.");
        this._fileProcess.done.run(_UrlKt.FRAGMENT_ENCODE_SET);
    }

    public void lambda$requestUserpics$19(TLObject tLObject, TLRPC.TL_error tL_error) {
        ApiWrap$UserpicsInfo apiWrap$UserpicsInfo;
        if (tLObject instanceof TLRPC.photos_Photos) {
            TLRPC.photos_Photos photos_photos = (TLRPC.photos_Photos) tLObject;
            if (photos_photos instanceof TLRPC.TL_photos_photos) {
                apiWrap$UserpicsInfo = new ApiWrap$UserpicsInfo(((TLRPC.TL_photos_photos) photos_photos).photos.size());
            } else {
                apiWrap$UserpicsInfo = photos_photos instanceof TLRPC.TL_photos_photosSlice ? new ApiWrap$UserpicsInfo(((TLRPC.TL_photos_photosSlice) photos_photos).count) : null;
            }
            if (this._userpicsProcess.start.run(apiWrap$UserpicsInfo).booleanValue()) {
                handleUserpicsSlice(photos_photos);
            }
        }
    }

    private void handleUserpicsSlice(TLRPC.photos_Photos photos_photos) {
        if (photos_photos instanceof TLRPC.TL_photos_photos) {
            this._userpicsProcess.lastSlice = true;
        }
        loadUserpicsFiles(DataTypesUtils.ParseUserpicsSlice(photos_photos.photos, this._userpicsProcess.processed));
    }

    public void requestStories(Utilities.CallbackReturn<Integer, Boolean> callbackReturn, Utilities.CallbackReturn<ApiWrap$DownloadProgress, Boolean> callbackReturn2, Utilities.CallbackReturn<ApiWrap$StoriesSlice, Boolean> callbackReturn3, Runnable runnable) {
        ApiWrap$StoriesProcess apiWrap$StoriesProcess = new ApiWrap$StoriesProcess();
        this._storiesProcess = apiWrap$StoriesProcess;
        apiWrap$StoriesProcess.start = callbackReturn;
        apiWrap$StoriesProcess.fileProgress = callbackReturn2;
        apiWrap$StoriesProcess.handleSlice = callbackReturn3;
        apiWrap$StoriesProcess.finish = runnable;
        TL_stories.TL_stories_getStoriesArchive tL_stories_getStoriesArchive = new TL_stories.TL_stories_getStoriesArchive();
        tL_stories_getStoriesArchive.peer = new TLRPC.TL_inputPeerSelf();
        tL_stories_getStoriesArchive.limit = 100;
        tL_stories_getStoriesArchive.offset_id = this._storiesProcess.offsetId;
        mainRequest(tL_stories_getStoriesArchive, new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$requestStories$20((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$finishStoriesSlice$21(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TL_stories.TL_stories_stories) {
            loadStoriesFiles(DataTypesUtils.ParseStoriesSlice(((TL_stories.TL_stories_stories) tLObject).stories, this._storiesProcess.processed));
        }
    }

    public boolean loadStoryProgress(ApiWrap$FileProgress apiWrap$FileProgress) {
        ApiWrap$FileProcess apiWrap$FileProcess = this._fileProcess;
        return this._storiesProcess.fileProgress.run(new ApiWrap$DownloadProgress(apiWrap$FileProcess.randomId, apiWrap$FileProcess.relativePath, this._storiesProcess.fileIndex, apiWrap$FileProgress.ready(), apiWrap$FileProgress.total())).booleanValue();
    }

    public void loadStoryThumbDone(String str) {
        ApiWrap$StoriesProcess apiWrap$StoriesProcess = this._storiesProcess;
        ApiWrap$File apiWrap$File = apiWrap$StoriesProcess.slice.list.get(apiWrap$StoriesProcess.fileIndex).thumb().file;
        apiWrap$File.relativePath = str;
        if (str.isEmpty()) {
            apiWrap$File.skipReason = ApiWrap$File.SkipReason.Unavailable;
        }
        loadNextStory();
    }

    public void loadStoryDone(String str) {
        ApiWrap$StoriesProcess apiWrap$StoriesProcess = this._storiesProcess;
        ApiWrap$File apiWrap$FileFile = apiWrap$StoriesProcess.slice.list.get(apiWrap$StoriesProcess.fileIndex).file();
        apiWrap$FileFile.relativePath = str;
        if (str.isEmpty()) {
            apiWrap$FileFile.skipReason = ApiWrap$File.SkipReason.Unavailable;
        }
        loadNextStory();
    }

    public void requestContacts(Utilities.Callback<ApiWrap$ContactsList> callback) {
        ApiWrap$ContactsProcess apiWrap$ContactsProcess = new ApiWrap$ContactsProcess();
        this._contactsProcess = apiWrap$ContactsProcess;
        apiWrap$ContactsProcess.done = callback;
        mainRequest(new TLObject() { 
            @Override // org.telegram.tgnet.TLObject
            public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
                Vector vector = new Vector(new ExportRequests$InvokeWithTakeoutWrapper$$ExternalSyntheticLambda1());
                vector.readParams(inputSerializedData, z);
                return vector;
            }

            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(OutputSerializedData outputSerializedData) {
                outputSerializedData.writeInt32(-2098076769);
            }
        }, new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$requestContacts$24((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$requestContacts$23(final Integer num, final Utilities.Callback2 callback2) {
        if (num.intValue() == this._contactsProcess.result.list.size()) {
            requestTopPeersSlice();
            return;
        }
        TLRPC.TL_contacts_resolvePhone tL_contacts_resolvePhone = new TLRPC.TL_contacts_resolvePhone();
        tL_contacts_resolvePhone.phone = this._contactsProcess.result.list.get(num.intValue()).phoneNumber;
        mainRequest(tL_contacts_resolvePhone, new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$requestContacts$22(num, callback2, (TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$requestTopPeersSlice$25(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.contacts_TopPeers) {
            TLRPC.contacts_TopPeers contacts_toppeers = (TLRPC.contacts_TopPeers) tLObject;
            DataTypesUtils.AppendTopPeers(this._contactsProcess.result, contacts_toppeers);
            int i = this._contactsProcess.topPeersOffset;
            boolean z = true;
            if (!(contacts_toppeers instanceof TLRPC.TL_contacts_topPeersNotModified) && !(contacts_toppeers instanceof TLRPC.TL_contacts_topPeersDisabled)) {
                if (contacts_toppeers instanceof TLRPC.TL_contacts_topPeers) {
                    ArrayList<TLRPC.TL_topPeerCategoryPeers> arrayList = ((TLRPC.TL_contacts_topPeers) contacts_toppeers).categories;
                    int size = arrayList.size();
                    boolean z2 = true;
                    int i2 = 0;
                    while (i2 < size) {
                        TLRPC.TL_topPeerCategoryPeers tL_topPeerCategoryPeers = arrayList.get(i2);
                        i2++;
                        TLRPC.TL_topPeerCategoryPeers tL_topPeerCategoryPeers2 = tL_topPeerCategoryPeers;
                        z2 = tL_topPeerCategoryPeers2.peers.size() + i >= tL_topPeerCategoryPeers2.count;
                        if (!z2) {
                            break;
                        }
                    }
                    z = z2;
                } else {
                    z = false;
                }
            }
            ApiWrap$ContactsProcess apiWrap$ContactsProcess = this._contactsProcess;
            if (z) {
                apiWrap$ContactsProcess.done.run(apiWrap$ContactsProcess.result);
            } else {
                apiWrap$ContactsProcess.topPeersOffset = Math.max(Math.max(apiWrap$ContactsProcess.result.correspondents.size(), this._contactsProcess.result.inlineBots.size()), this._contactsProcess.result.phoneCalls.size());
                requestTopPeersSlice();
            }
        }
    }

    private void loadUserpicsFiles(ArrayList<HtmlWriter.Photo> arrayList) {
        if (arrayList.isEmpty()) {
            this._userpicsProcess.lastSlice = true;
        }
        ApiWrap$UserpicsProcess apiWrap$UserpicsProcess = this._userpicsProcess;
        apiWrap$UserpicsProcess.slice = arrayList;
        apiWrap$UserpicsProcess.fileIndex = 0;
        loadNextUserpic();
    }

    private void loadNextUserpic() {
        ArrayList<HtmlWriter.Photo> arrayList = this._userpicsProcess.slice;
        while (this._userpicsProcess.fileIndex < arrayList.size()) {
            ExportRequestsController exportRequestsController = this;
            if (!exportRequestsController.processFileLoad(arrayList.get(this._userpicsProcess.fileIndex).image.file, new ApiWrap$FileOrigin(), new Utilities.CallbackReturn() { 
                @Override 
                public final Object run(Object obj) {
                    return Boolean.valueOf(this.f$0.loadUserpicProgress((ApiWrap$FileProgress) obj));
                }
            }, new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.loadUserpicDone((String) obj);
                }
            }, null, null)) {
                return;
            }
            exportRequestsController._userpicsProcess.fileIndex++;
            this = exportRequestsController;
        }
        this.finishUserpicsSlice();
    }

    private void finishUserpicsSlice() {
        ArrayList<HtmlWriter.Photo> arrayList = this._userpicsProcess.slice;
        if (!arrayList.isEmpty()) {
            this._userpicsProcess.processed += arrayList.size();
            this._userpicsProcess.maxId = arrayList.get(arrayList.size() - 1).id;
            if (!this._userpicsProcess.handleSlice.run(arrayList).booleanValue()) {
                return;
            }
        }
        ApiWrap$UserpicsProcess apiWrap$UserpicsProcess = this._userpicsProcess;
        if (apiWrap$UserpicsProcess.lastSlice) {
            apiWrap$UserpicsProcess.finish.run();
            return;
        }
        TLRPC.TL_photos_getUserPhotos tL_photos_getUserPhotos = new TLRPC.TL_photos_getUserPhotos();
        tL_photos_getUserPhotos.user_id = new TLRPC.TL_inputUserSelf();
        tL_photos_getUserPhotos.offset = 0;
        tL_photos_getUserPhotos.max_id = this._userpicsProcess.maxId;
        tL_photos_getUserPhotos.limit = 100;
        mainRequest(tL_photos_getUserPhotos, new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$finishUserpicsSlice$26((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$requestChatMessages$28(TLObject tLObject, TLRPC.TL_error tL_error) {
        this._chatProcess.requestDone.run((TLRPC.messages_Messages) tLObject);
    }

    public void lambda$requestDialogsSlice$30(TLObject tLObject, TLRPC.TL_error tL_error) {
        ApiWrap$DialogInfo apiWrap$DialogInfo;
        int i;
        if (tLObject instanceof TLRPC.TL_messages_dialogsNotModified) {
            return;
        }
        boolean z = (tLObject instanceof TLRPC.TL_messages_dialogs) || ((tLObject instanceof TLRPC.TL_messages_dialogsSlice) && ((TLRPC.TL_messages_dialogsSlice) tLObject).dialogs.size() < 100);
        ApiWrap$DialogsInfo apiWrap$DialogsInfoParseDialogsInfo = ParseDialogsInfo((TLRPC.messages_Dialogs) tLObject);
        this._dialogsProcess.processedCount += apiWrap$DialogsInfoParseDialogsInfo.chats.size();
        if (apiWrap$DialogsInfoParseDialogsInfo.chats.isEmpty()) {
            apiWrap$DialogInfo = new ApiWrap$DialogInfo();
        } else {
            ArrayList<ApiWrap$DialogInfo> arrayList = apiWrap$DialogsInfoParseDialogsInfo.chats;
            apiWrap$DialogInfo = arrayList.get(arrayList.size() - 1);
        }
        ApiWrap$DialogsProcess apiWrap$DialogsProcess = this._dialogsProcess;
        if (apiWrap$DialogsProcess.info == null) {
            apiWrap$DialogsProcess.info = new ApiWrap$DialogsInfo();
        }
        ApiWrap$DialogsProcess apiWrap$DialogsProcess2 = this._dialogsProcess;
        appendChatsSlice(apiWrap$DialogsProcess2, apiWrap$DialogsProcess2.info.chats, apiWrap$DialogsInfoParseDialogsInfo.chats, apiWrap$DialogsProcess2.splitIndexPlusOne - 1);
        if (!z && (i = apiWrap$DialogInfo.topMessageDate) > 0) {
            ApiWrap$DialogsProcess apiWrap$DialogsProcess3 = this._dialogsProcess;
            apiWrap$DialogsProcess3.offsetId = apiWrap$DialogInfo.topMessageId;
            apiWrap$DialogsProcess3.offsetDate = i;
            apiWrap$DialogsProcess3.offsetPeer = apiWrap$DialogInfo.input;
        } else {
            if (!useOnlyLastSplit()) {
                ApiWrap$DialogsProcess apiWrap$DialogsProcess4 = this._dialogsProcess;
                int i2 = apiWrap$DialogsProcess4.splitIndexPlusOne - 1;
                apiWrap$DialogsProcess4.splitIndexPlusOne = i2;
                if (i2 > 0) {
                    apiWrap$DialogsProcess4.offsetId = 0;
                    apiWrap$DialogsProcess4.offsetDate = 0;
                    apiWrap$DialogsProcess4.offsetPeer = new TLRPC.TL_inputPeerEmpty();
                }
            }
            requestLeftChannelsIfNeeded();
            return;
        }
        requestDialogsSlice();
    }

    private void requestSinglePeerDialog() {
        final Utilities.Callback callback = new Utilities.Callback() { 
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$requestSinglePeerDialog$31((TLObject) obj);
            }
        };
        Utilities.Callback callback2 = new Utilities.Callback() { 
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$requestSinglePeerDialog$33(callback, (TLRPC.InputUser) obj);
            }
        };
        TLRPC.InputPeer inputPeer = this._settings.singlePeer;
        if (inputPeer instanceof TLRPC.TL_inputPeerUser) {
            TLRPC.TL_inputPeerUser tL_inputPeerUser = (TLRPC.TL_inputPeerUser) inputPeer;
            TLRPC.TL_inputUser tL_inputUser = new TLRPC.TL_inputUser();
            tL_inputUser.access_hash = tL_inputPeerUser.access_hash;
            tL_inputUser.user_id = tL_inputPeerUser.user_id;
            callback2.run(tL_inputUser);
            return;
        }
        if (inputPeer instanceof TLRPC.TL_inputPeerChat) {
            TLRPC.TL_messages_getChats tL_messages_getChats = new TLRPC.TL_messages_getChats();
            tL_messages_getChats.id = new ArrayList<>(ImageCapture$$ExternalSyntheticBackport1.m(new Object[]{Long.valueOf(((TLRPC.TL_inputPeerChat) inputPeer).chat_id)}));
            mainRequest(tL_messages_getChats, new Utilities.Callback2() { 
                @Override 
                public final void run(Object obj, Object obj2) {
                    callback.run((TLObject) obj);
                }
            });
        } else {
            if (inputPeer instanceof TLRPC.TL_inputPeerSelf) {
                callback2.run(new TLRPC.TL_inputUserSelf());
                return;
            }
            if (inputPeer instanceof TLRPC.TL_inputPeerUserFromMessage) {
                Segment$$ExternalSyntheticBUOutline1.m("From message peer in requestSinglePeerDialog.");
            } else if (inputPeer instanceof TLRPC.TL_inputPeerChannelFromMessage) {
                Segment$$ExternalSyntheticBUOutline1.m("From message peer in requestSinglePeerDialog.");
            } else if (inputPeer instanceof TLRPC.TL_inputPeerEmpty) {
                Segment$$ExternalSyntheticBUOutline1.m("Empty peer in requestSinglePeerDialog.");
            }
        }
    }

    public void lambda$requestSinglePeerDialog$33(final Utilities.Callback callback, TLRPC.InputUser inputUser) {
        TLRPC.TL_users_getUsers tL_users_getUsers = new TLRPC.TL_users_getUsers();
        tL_users_getUsers.id = new ArrayList<>(ImageCapture$$ExternalSyntheticBackport1.m(new Object[]{inputUser}));
        mainRequest(tL_users_getUsers, new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                callback.run((TLObject) obj);
            }
        });
    }

    private void appendSinglePeerDialogs(ApiWrap$DialogsInfo apiWrap$DialogsInfo) {
        Utilities.CallbackReturn callbackReturn = new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                ApiWrap$DialogInfo.Type type = (ApiWrap$DialogInfo.Type) obj;
                return Boolean.valueOf(type == ApiWrap$DialogInfo.Type.PrivateSupergroup || type == ApiWrap$DialogInfo.Type.PublicSupergroup);
            }
        };
        Utilities.CallbackReturn callbackReturn2 = new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                ApiWrap$DialogInfo.Type type = (ApiWrap$DialogInfo.Type) obj;
                return Boolean.valueOf(type == ApiWrap$DialogInfo.Type.PrivateChannel || type == ApiWrap$DialogInfo.Type.PublicChannel);
            }
        };
        int i = this._dialogsProcess.splitIndexPlusOne - 1;
        ArrayList<ApiWrap$DialogInfo> arrayList = apiWrap$DialogsInfo.chats;
        int size = arrayList.size();
        int iRequestSinglePeerMigrated = 0;
        int i2 = 0;
        while (i2 < size) {
            ApiWrap$DialogInfo apiWrap$DialogInfo = arrayList.get(i2);
            i2++;
            ApiWrap$DialogInfo apiWrap$DialogInfo2 = apiWrap$DialogInfo;
            if (((Boolean) callbackReturn.run(apiWrap$DialogInfo2.type)).booleanValue() && iRequestSinglePeerMigrated == 0) {
                iRequestSinglePeerMigrated = requestSinglePeerMigrated(apiWrap$DialogInfo2);
            } else if (!((Boolean) callbackReturn2.run(apiWrap$DialogInfo2.type)).booleanValue()) {
                for (int i3 = i; i3 != 0; i3--) {
                    apiWrap$DialogInfo2.splits.add(Integer.valueOf(i3 - 1));
                    apiWrap$DialogInfo2.messagesCountPerSplit.add(0);
                }
            }
        }
        if (iRequestSinglePeerMigrated == 0) {
            this._dialogsProcess.processedCount += apiWrap$DialogsInfo.chats.size();
        }
        ApiWrap$DialogsProcess apiWrap$DialogsProcess = this._dialogsProcess;
        appendChatsSlice(apiWrap$DialogsProcess, apiWrap$DialogsProcess.info.chats, apiWrap$DialogsInfo.chats, apiWrap$DialogsProcess.splitIndexPlusOne - 1);
        if (iRequestSinglePeerMigrated == 0) {
            ApiWrap$DialogsProcess apiWrap$DialogsProcess2 = this._dialogsProcess;
            if (apiWrap$DialogsProcess2.progress.run(Integer.valueOf(apiWrap$DialogsProcess2.processedCount)).booleanValue()) {
                finishDialogsList();
            }
        }
    }

    private int requestSinglePeerMigrated(ApiWrap$DialogInfo apiWrap$DialogInfo) {
        TLRPC.InputPeer inputPeer = apiWrap$DialogInfo.input;
        if (inputPeer instanceof TLRPC.TL_inputPeerChannel) {
            TLRPC.TL_inputPeerChannel tL_inputPeerChannel = (TLRPC.TL_inputPeerChannel) inputPeer;
            TLRPC.TL_inputChannel tL_inputChannel = new TLRPC.TL_inputChannel();
            tL_inputChannel.channel_id = tL_inputPeerChannel.channel_id;
            tL_inputChannel.access_hash = tL_inputPeerChannel.access_hash;
            TLRPC.TL_channels_getFullChannel tL_channels_getFullChannel = new TLRPC.TL_channels_getFullChannel();
            tL_channels_getFullChannel.channel = tL_inputChannel;
            return mainRequest(tL_channels_getFullChannel, new Utilities.Callback2() { 
                @Override 
                public final void run(Object obj, Object obj2) {
                    this.f$0.lambda$requestSinglePeerMigrated$37((TLObject) obj, (TLRPC.TL_error) obj2);
                }
            });
        }
        Buffer$$ExternalSyntheticBUOutline4.m("unexpected peer type: ", apiWrap$DialogInfo.input);
        return 0;
    }

    public Boolean lambda$requestLeftChannelsIfNeeded$38(Integer num) {
        ApiWrap$DialogsProcess apiWrap$DialogsProcess = this._dialogsProcess;
        return apiWrap$DialogsProcess.progress.run(Integer.valueOf(apiWrap$DialogsProcess.processedCount + num.intValue()));
    }

    public void lambda$requestLeftChannelsSlice$40() {
        ApiWrap$LeftChannelsProcess apiWrap$LeftChannelsProcess = this._leftChannelsProcess;
        if (apiWrap$LeftChannelsProcess.finished) {
            apiWrap$LeftChannelsProcess.done.run(apiWrap$LeftChannelsProcess.info);
        } else {
            requestLeftChannelsSlice();
        }
    }

    private boolean goodByTypes(ApiWrap$DialogInfo apiWrap$DialogInfo) {
        return (this._settings.types & DataTypesUtils.SettingsFromDialogsType(apiWrap$DialogInfo.type)) != 0;
    }

    private void appendChatsSlice(ApiWrap$ChatsProcess apiWrap$ChatsProcess, ArrayList<ApiWrap$DialogInfo> arrayList, ArrayList<ApiWrap$DialogInfo> arrayList2, int i) {
        ArrayList arrayList3 = new ArrayList();
        int size = arrayList2.size();
        int i2 = 0;
        while (i2 < size) {
            ApiWrap$DialogInfo apiWrap$DialogInfo = arrayList2.get(i2);
            i2++;
            ApiWrap$DialogInfo apiWrap$DialogInfo2 = apiWrap$DialogInfo;
            if (goodByTypes(apiWrap$DialogInfo2)) {
                arrayList3.add(apiWrap$DialogInfo2);
            } else if (apiWrap$DialogInfo2.migratedToChannelId != 0) {
                int i3 = this._settings.types;
                if ((i3 & 256) != 0 || (i3 & 128) != 0) {
                    arrayList3.add(apiWrap$DialogInfo2);
                }
            }
        }
        arrayList.ensureCapacity(arrayList.size() + arrayList2.size());
        int size2 = arrayList3.size();
        int i4 = 0;
        while (i4 < size2) {
            Object obj = arrayList3.get(i4);
            i4++;
            ApiWrap$DialogInfo apiWrap$DialogInfo3 = (ApiWrap$DialogInfo) obj;
            int size3 = arrayList.size();
            long j = apiWrap$DialogInfo3.migratedToChannelId;
            if (j != 0) {
                Integer num = apiWrap$ChatsProcess.indexByPeer.get(Long.valueOf(j));
                if (num == null || !DataTypesUtils.AddMigrateFromSlice(arrayList.get(num.intValue()), apiWrap$DialogInfo3, i, this.splits.size())) {
                    if (!goodByTypes(apiWrap$DialogInfo3)) {
                    }
                }
            }
            Integer numPutIfAbsent = apiWrap$ChatsProcess.indexByPeer.putIfAbsent(Long.valueOf(apiWrap$DialogInfo3.peerId), Integer.valueOf(size3));
            if (numPutIfAbsent == null) {
                arrayList.add(apiWrap$DialogInfo3);
            } else {
                size3 = numPutIfAbsent.intValue();
            }
            arrayList.get(size3).splits.add(Integer.valueOf(i));
            arrayList.get(size3).messagesCountPerSplit.add(0);
        }
    }

    private ApiWrap$DialogsInfo ParseDialogsInfo(TLRPC.messages_Dialogs messages_dialogs) {
        ApiWrap$DialogInfo.Type typeDialogTypeFromChat;
        String str;
        String str2;
        TLRPC.InputPeer tL_inputPeerEmpty;
        ApiWrap$DialogsInfo apiWrap$DialogsInfo = new ApiWrap$DialogsInfo();
        if (messages_dialogs == null || (messages_dialogs instanceof TLRPC.TL_messages_dialogsNotModified)) {
            return apiWrap$DialogsInfo;
        }
        HashMap<Long, ApiWrap$Peer> mapParsePeersLists = DataTypesUtils.ParsePeersLists(messages_dialogs.users, messages_dialogs.chats);
        HashMap<String, ApiWrap$Message> mapParseMessagesList = ParseMessagesList(0L, messages_dialogs.messages, _UrlKt.FRAGMENT_ENCODE_SET);
        ArrayList<TLRPC.Dialog> arrayList = messages_dialogs.dialogs;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            TLRPC.Dialog dialog = arrayList.get(i);
            i++;
            TLRPC.Dialog dialog2 = dialog;
            ApiWrap$DialogInfo apiWrap$DialogInfo = new ApiWrap$DialogInfo();
            long peerId = MessageObject.getPeerId(dialog2.peer);
            apiWrap$DialogInfo.peerId = peerId;
            ApiWrap$Peer apiWrap$Peer = mapParsePeersLists.get(Long.valueOf(peerId));
            if (apiWrap$Peer != null) {
                ApiWrap$User apiWrap$User = apiWrap$Peer.user;
                boolean z = apiWrap$User != null;
                if (z) {
                    typeDialogTypeFromChat = DataTypesUtils.DialogTypeFromUser(apiWrap$User);
                } else {
                    typeDialogTypeFromChat = DataTypesUtils.DialogTypeFromChat(apiWrap$Peer.chat);
                }
                apiWrap$DialogInfo.type = typeDialogTypeFromChat;
                if (z) {
                    str = apiWrap$Peer.user.info.firstName;
                } else {
                    str = apiWrap$Peer.chat.title;
                }
                apiWrap$DialogInfo.name = str;
                if (str == null) {
                    apiWrap$DialogInfo.name = "Deleted Account";
                }
                if (!z || (str2 = apiWrap$Peer.user.info.lastName) == null) {
                    str2 = _UrlKt.FRAGMENT_ENCODE_SET;
                }
                apiWrap$DialogInfo.lastName = str2;
                apiWrap$DialogInfo.colorIndex = apiWrap$Peer.colorIndex();
                apiWrap$DialogInfo.input = apiWrap$Peer.getInput();
                apiWrap$DialogInfo.migratedToChannelId = !z ? apiWrap$Peer.chat.migratedToChannelId : 0L;
                ApiWrap$Chat apiWrap$Chat = apiWrap$Peer.chat;
                apiWrap$DialogInfo.isMonoforum = apiWrap$Chat != null && apiWrap$Chat.isMonoforum;
                if (apiWrap$Chat != null) {
                    tL_inputPeerEmpty = apiWrap$Chat.monoforumBroadcastInput;
                } else {
                    tL_inputPeerEmpty = new TLRPC.TL_inputPeerEmpty();
                }
                apiWrap$DialogInfo.monoforumBroadcastInput = tL_inputPeerEmpty;
            }
            apiWrap$DialogInfo.topMessageId = dialog2.top_message;
            new ApiWrap$MessageId().didAndMsgId = apiWrap$DialogInfo.peerId + "_" + apiWrap$DialogInfo.topMessageId;
            ApiWrap$Message apiWrap$Message = mapParseMessagesList.get(apiWrap$DialogInfo.peerId + "_" + apiWrap$DialogInfo.topMessageId);
            if (apiWrap$Message != null) {
                apiWrap$DialogInfo.topMessageDate = apiWrap$Message.date;
            }
            apiWrap$DialogsInfo.chats.add(apiWrap$DialogInfo);
        }
        return apiWrap$DialogsInfo;
    }

    private HashMap<String, ApiWrap$Message> ParseMessagesList(long j, ArrayList<TLRPC.Message> arrayList, String str) {
        ApiWrap$ParseMediaContext apiWrap$ParseMediaContext = new ApiWrap$ParseMediaContext();
        apiWrap$ParseMediaContext.selfPeerId = j;
        LinkedHashMap linkedHashMap = new LinkedHashMap();
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            TLRPC.Message message = arrayList.get(i);
            i++;
            ApiWrap$Message apiWrap$MessageParseMessage = DataTypesUtils.ParseMessage(apiWrap$ParseMediaContext, message, str);
            new ApiWrap$MessageId().didAndMsgId = apiWrap$MessageParseMessage.peerId + "_" + apiWrap$MessageParseMessage.id;
            linkedHashMap.put(apiWrap$MessageParseMessage.peerId + "_" + apiWrap$MessageParseMessage.id, apiWrap$MessageParseMessage);
        }
        return linkedHashMap;
    }

    public void requestPersonalInfo(final Utilities.Callback<ApiWrap$ExportPersonalInfo> callback) {
        TLRPC.TL_users_getFullUser tL_users_getFullUser = new TLRPC.TL_users_getFullUser();
        tL_users_getFullUser.id = new TLRPC.TL_inputUserSelf();
        mainRequest(tL_users_getFullUser, new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.m1071$r8$lambda$K6Z8Dxak9YlqZ3WIvUBpQOpswQ(callback, (TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public static void lambda$fileRequest$44(long j, Utilities.Callback2 callback2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            this._fileProcess.requestId = 0L;
            if (Objects.equals(tL_error.text, "TAKEOUT_FILE_EMPTY") && this._otherDataProcess != null) {
                TLRPC.TL_upload_file tL_upload_file = new TLRPC.TL_upload_file();
                tL_upload_file.type = new TLRPC.TL_storage_filePartial();
                filePartDone(0L, tL_upload_file);
            } else if (Objects.equals(tL_error.text, "LOCATION_INVALID") || Objects.equals(tL_error.text, "VERSION_INVALID") || Objects.equals(tL_error.text, "LOCATION_NOT_AVAILABLE")) {
                Log.w("exteraGram", "Export Error: File unavailable.");
                this._fileProcess.done.run(_UrlKt.FRAGMENT_ENCODE_SET);
            } else if (tL_error.code == 400 && tL_error.text.startsWith("FILE_REFERENCE")) {
                filePartRefreshReference(j);
            } else {
                StringBuilder sb = new StringBuilder("wtf! fileRequest, response: ");
                sb.append(tLObject);
                OkHttpClient$Builder$$ExternalSyntheticBUOutline0.m(sb, " error: ", tL_error.text);
                return;
            }
        }
        callback2.run(tLObject, tL_error);
    }

    public void requestSplitRanges() {
        mainRequest(new TLObject() { 
            @Override // org.telegram.tgnet.TLObject
            public TLObject deserializeResponse(InputSerializedData inputSerializedData, int i, boolean z) {
                Vector vector = new Vector(new ExportRequests$InvokeWithTakeoutWrapper$$ExternalSyntheticLambda0());
                vector.readParams(inputSerializedData, z);
                return vector;
            }

            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(OutputSerializedData outputSerializedData) {
                outputSerializedData.writeInt32(486505992);
            }
        }, new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                this.f$0.lambda$requestSplitRanges$45((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public void lambda$requestDialogsCount$46(TLObject tLObject, TLRPC.TL_error tL_error) {
        int size;
        if (tLObject instanceof TLRPC.TL_messages_dialogs) {
            size = ((TLRPC.TL_messages_dialogs) tLObject).dialogs.size();
        } else {
            size = tLObject instanceof TLRPC.TL_messages_dialogsSlice ? ((TLRPC.TL_messages_dialogsSlice) tLObject).count : -1;
        }
        if (size < 0) {
            Segment$$ExternalSyntheticBUOutline1.m("unexpected TL_messages_dialogsNotModified received");
            return;
        }
        StartProcess startProcess = this._startProcess;
        startProcess.info.dialogsCount += size;
        int i = startProcess.splitIndex + 1;
        startProcess.splitIndex = i;
        if (i >= this.splits.size()) {
            sendNextStartRequest();
        } else {
            requestDialogsCount();
        }
    }

    public void requestOtherData(String str, Utilities.Callback<ApiWrap$File> callback) {
        ApiWrap$OtherDataProcess apiWrap$OtherDataProcess = new ApiWrap$OtherDataProcess();
        this._otherDataProcess = apiWrap$OtherDataProcess;
        apiWrap$OtherDataProcess.done = callback;
        ApiWrap$File apiWrap$File = apiWrap$OtherDataProcess.file;
        apiWrap$File.suggestedPath = str;
        apiWrap$File.location = new ApiWrap$FileLocation();
        this._otherDataProcess.file.location.data = new TLRPC.InputFileLocation() { 
            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(OutputSerializedData outputSerializedData) {
                outputSerializedData.writeInt32(700340377);
            }
        };
        loadFile(this._otherDataProcess.file, new ApiWrap$FileOrigin(), new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return Boolean.TRUE;
            }
        }, new Utilities.Callback() { 
            @Override 
            public final void run(Object obj) {
                this.f$0.otherDataDone((String) obj);
            }
        });
    }

    public void otherDataDone(String str) {
        this._otherDataProcess.file.relativePath = str;
        if (str.isEmpty()) {
            this._otherDataProcess.file.skipReason = ApiWrap$File.SkipReason.Unavailable;
        }
        ApiWrap$OtherDataProcess apiWrap$OtherDataProcess = this._otherDataProcess;
        apiWrap$OtherDataProcess.done.run(apiWrap$OtherDataProcess.file);
    }

    public boolean useOnlyLastSplit() {
        return (this._settings.types & Opcodes.SHL_INT_LIT8) == 0;
    }

    public void invokeFinish(boolean z, final Runnable runnable) {
        ExportRequests$FinishTakeoutSession exportRequests$FinishTakeoutSession = new ExportRequests$FinishTakeoutSession();
        exportRequests$FinishTakeoutSession.success = !z;
        mainRequest(exportRequests$FinishTakeoutSession, new Utilities.Callback2() { 
            @Override 
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.m1070$r8$lambda$K5E2oN2LAwJDAki5Obw5XGaixo(runnable, (TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    public static /* synthetic */ void m1070$r8$lambda$K5E2oN2LAwJDAki5Obw5XGaixo(Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        runnable.run();
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            Log.w("exteraGram", "finished successfully!!!");
            return;
        }
        Log.e("exteraGram", "failed: " + tL_error);
    }
}
