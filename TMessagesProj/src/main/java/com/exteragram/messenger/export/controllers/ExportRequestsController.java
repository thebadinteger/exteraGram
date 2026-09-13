package com.exteragram.messenger.export.controllers;

import android.util.Log;
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
import com.exteragram.messenger.export.api.ExportRequests$SavedContact;
import com.exteragram.messenger.export.api.ExportRequests$getSplitRanges;
import com.exteragram.messenger.export.api.ExportRequests$TL_contacts_getSaved;
import com.exteragram.messenger.export.api.ExportRequests$getLeftChannels;
import com.exteragram.messenger.export.output.OutputFile;
import com.exteragram.messenger.export.output.html.HtmlWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Set;
import okhttp3.internal.url._UrlKt;
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
    private final ApiWrap$LoadedFileCache _fileCache = new ApiWrap$LoadedFileCache(100000);
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
                throw new IllegalStateException("wtf is it: " + inputPeer);
            }
            j = 0;
        }
        ApiWrap$DialogsInfo apiWrap$DialogsInfo = new ApiWrap$DialogsInfo();
        ArrayList<TLObject> arrayList = vector.objects;
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
            throw new IllegalArgumentException("illegal type: " + inputPeer);
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
        if ((this._settings.types & 2016) != 0) {
            this._startProcess.steps.add(StartProcess.Step.SplitRanges);
            this._startProcess.steps.add(StartProcess.Step.DialogsCount);
        }
        ExportSettings exportSettings2 = this._settings;
        if ((exportSettings2.types & 1920) != 0 && !exportSettings2.onlySinglePeer()) {
            this._startProcess.steps.add(StartProcess.Step.LeftChannelsCount);
        }
        startMainSession(new Runnable() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                ExportRequestsController.this.sendNextStartRequest();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
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

    /* JADX INFO: renamed from: com.exteragram.messenger.export.controllers.ExportRequestsController$1, reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$exteragram$messenger$export$controllers$ExportRequestsController$StartProcess$Step;

        static {
            int[] iArr = new int[StartProcess.Step.values().length];
            $SwitchMap$com$exteragram$messenger$export$controllers$ExportRequestsController$StartProcess$Step = iArr;
            try {
                iArr[StartProcess.Step.UserpicsCount.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$controllers$ExportRequestsController$StartProcess$Step[StartProcess.Step.StoriesCount.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$controllers$ExportRequestsController$StartProcess$Step[StartProcess.Step.SplitRanges.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$controllers$ExportRequestsController$StartProcess$Step[StartProcess.Step.DialogsCount.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$controllers$ExportRequestsController$StartProcess$Step[StartProcess.Step.LeftChannelsCount.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    public void finishStartProcess() {
        StartProcess startProcess = this._startProcess;
        startProcess.done.run(startProcess.info);
    }

    /* JADX WARN: Code duplicated, block: B:12:0x0022  */
    private void startMainSession(final Runnable runnable) {
        boolean z;
        ExportSettings.MediaSettings mediaSettings = this._settings.media;
        long j = mediaSettings.sizeLimit;
        if (!mediaSettings.isEnabled() || j <= 0) {
            int i = this._settings.types;
            if ((i & 2) == 0 && (i & 2048) == 0) {
                z = false;
            } else {
                z = true;
            }
        } else {
            z = true;
        }
        final ExportRequests$InitTakeoutSession exportRequests$InitTakeoutSession = new ExportRequests$InitTakeoutSession();
        if (z) {
            exportRequests$InitTakeoutSession.files = true;
            if (j < SaveToGallerySettingsHelper.MAX_VIDEO_LIMIT) {
                exportRequests$InitTakeoutSession.file_max_size = j;
            }
        }
        int i2 = this._settings.types;
        if ((i2 & 4) != 0) {
            exportRequests$InitTakeoutSession.contacts = true;
        }
        if ((i2 & 32) != 0 || (i2 & 64) != 0) {
            exportRequests$InitTakeoutSession.message_users = true;
        }
        if ((i2 & 128) != 0) {
            exportRequests$InitTakeoutSession.message_megagroups = true;
            exportRequests$InitTakeoutSession.message_chats = true;
        }
        if ((i2 & 256) != 0) {
            exportRequests$InitTakeoutSession.message_megagroups = true;
        }
        if ((i2 & 512) != 0 || (i2 & 1024) != 0) {
            exportRequests$InitTakeoutSession.message_channels = true;
        }
        TLRPC.TL_users_getUsers tL_users_getUsers = new TLRPC.TL_users_getUsers();
        TLRPC.TL_inputUser tL_inputUser = new TLRPC.TL_inputUser();
        tL_inputUser.user_id = UserConfig.getInstance(this.selectedAcc).clientUserId;
        tL_users_getUsers.id = new ArrayList<>(Collections.singletonList(tL_inputUser));
        ConnectionsManager.getInstance(this.selectedAcc).sendRequest(tL_users_getUsers, new RequestDelegate() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda38
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ExportRequestsController.this.lambda$startMainSession$1(exportRequests$InitTakeoutSession, runnable, tLObject, tL_error);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startMainSession$1(ExportRequests$InitTakeoutSession exportRequests$InitTakeoutSession, final Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof Vector) {
            ArrayList<TLObject> arrayList = ((Vector) tLObject).objects;
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
            ConnectionsManager.getInstance(this.selectedAcc).sendRequest(exportRequests$InitTakeoutSession, new RequestDelegate() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda52
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject3, TLRPC.TL_error tL_error2) {
                    ExportRequestsController.this.lambda$startMainSession$0(runnable, tLObject3, tL_error2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$startMainSession$0(Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null && tL_error.text != null) {
            ExportController.showError(tL_error);
        } else if (tLObject instanceof ExportRequests$Takeout) {
            this._takeoutId = ((ExportRequests$Takeout) tLObject).id;
            runnable.run();
        }
    }

    private void requestUserpicsCount() {
        TLRPC.TL_photos_getUserPhotos tL_photos_getUserPhotos = new TLRPC.TL_photos_getUserPhotos();
        tL_photos_getUserPhotos.user_id = new TLRPC.TL_inputUserSelf();
        tL_photos_getUserPhotos.offset = 0;
        tL_photos_getUserPhotos.max_id = 0L;
        tL_photos_getUserPhotos.limit = 0;
        mainRequest(tL_photos_getUserPhotos, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda17
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$requestUserpicsCount$2((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestUserpicsCount$2(TLObject tLObject, TLRPC.TL_error tL_error) {
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
        mainRequest(tL_stories_getStoriesArchive, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda31
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$requestStoriesCount$3((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestStoriesCount$3(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TL_stories.TL_stories_stories) {
            this._startProcess.info.storiesCount = ((TL_stories.TL_stories_stories) tLObject).count;
        }
        sendNextStartRequest();
    }

    public void vectorToRanges(TLObject tLObject, ArrayList<TLRPC.TL_messageRange> arrayList) {
        if (tLObject instanceof Vector) {
            ArrayList<TLObject> arrayList2 = ((Vector) tLObject).objects;
            int size = arrayList2.size();
            int i = 0;
            while (i < size) {
                Object obj = arrayList2.get(i);
                i++;
                TLObject tLObject2 = (TLObject) obj;
                if (tLObject2 instanceof TLRPC.TL_messageRange) {
                    arrayList.add((TLRPC.TL_messageRange) tLObject2);
                }
            }
        }
    }

    public void requestDialogsList(Utilities.CallbackReturn<Integer, Boolean> callbackReturn, Utilities.Callback<ApiWrap$DialogsInfo> callback) {
        ApiWrap$DialogsProcess apiWrap$DialogsProcess = new ApiWrap$DialogsProcess();
        this._dialogsProcess = apiWrap$DialogsProcess;
        apiWrap$DialogsProcess.splitIndexPlusOne = this.splits.size();
        ApiWrap$DialogsProcess apiWrap$DialogsProcess2 = this._dialogsProcess;
        apiWrap$DialogsProcess2.progress = callbackReturn;
        apiWrap$DialogsProcess2.done = callback;
        requestDialogsSlice();
    }

    public void requestMessages(ApiWrap$DialogInfo apiWrap$DialogInfo, Utilities.CallbackReturn<ApiWrap$DialogInfo, Boolean> callbackReturn, Utilities.CallbackReturn<ApiWrap$DownloadProgress, Boolean> callbackReturn2, Utilities.CallbackReturn<ApiWrap$MessagesSlice, Boolean> callbackReturn3, Runnable runnable) {
        ApiWrap$ChatProcess apiWrap$ChatProcess = new ApiWrap$ChatProcess();
        this._chatProcess = apiWrap$ChatProcess;
        apiWrap$ChatProcess.context.selfPeerId = this._selfId;
        apiWrap$ChatProcess.info = apiWrap$DialogInfo;
        apiWrap$ChatProcess.start = callbackReturn;
        apiWrap$ChatProcess.fileProgress = callbackReturn2;
        apiWrap$ChatProcess.handleSlice = callbackReturn3;
        apiWrap$ChatProcess.done = runnable;
        requestMessagesCount(0);
    }

    public void requestSessions(final Utilities.Callback<ApiWrap$SessionsList> callback) {
        mainRequest(new TL_account.getAuthorizations(), new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda12
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$requestSessions$5(callback, (TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestSessions$5(final Utilities.Callback callback, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TL_account.authorizations) {
            final ApiWrap$SessionsList apiWrap$SessionsListParseSessionsList = DataTypesUtils.ParseSessionsList((TL_account.authorizations) tLObject);
            mainRequest(new TL_account.getWebAuthorizations(), new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda28
                @Override // org.telegram.messenger.Utilities.Callback2
                public final void run(Object obj, Object obj2) {
                    ExportRequestsController.$r8$lambda$zUhnSPX6kGtTskTPxh0MMvePxT4(apiWrap$SessionsListParseSessionsList, callback, (TLObject) obj, (TLRPC.TL_error) obj2);
                }
            });
        }
    }

    public static /* synthetic */ void $r8$lambda$zUhnSPX6kGtTskTPxh0MMvePxT4(ApiWrap$SessionsList apiWrap$SessionsList, Utilities.Callback callback, TLObject tLObject, TLRPC.TL_error tL_error) {
        apiWrap$SessionsList.webList = DataTypesUtils.ParseWebSessionsList((TL_account.webAuthorizations) tLObject).webList;
        callback.run(apiWrap$SessionsList);
    }

    private void requestLeftChannelsCount() {
        this._leftChannelsProcess = new ApiWrap$LeftChannelsProcess();
        requestLeftChannelsSliceGeneric(new Runnable() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda37
            @Override // java.lang.Runnable
            public final void run() {
                ExportRequestsController.this.lambda$requestLeftChannelsCount$6();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestLeftChannelsCount$6() {
        this._startProcess.info.dialogsCount += this._leftChannelsProcess.fullCount;
        sendNextStartRequest();
    }

    private void requestLeftChannelsSliceGeneric(final Runnable runnable) {
        ExportRequests$getLeftChannels exportRequests$getLeftChannels = new ExportRequests$getLeftChannels();
        exportRequests$getLeftChannels.offset = this._leftChannelsProcess.offset;
        mainRequest(exportRequests$getLeftChannels, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda49
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$requestLeftChannelsSliceGeneric$7(runnable, (TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestLeftChannelsSliceGeneric$7(Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        int size;
        if (tLObject instanceof TLRPC.messages_Chats) {
            TLRPC.messages_Chats messages_chats = (TLRPC.messages_Chats) tLObject;
            ApiWrap$LeftChannelsProcess apiWrap$LeftChannelsProcess = this._leftChannelsProcess;
            boolean zIsEmpty = true;
            appendChatsSlice(apiWrap$LeftChannelsProcess, apiWrap$LeftChannelsProcess.info.left, DataTypesUtils.ParseLeftChannelsInfo(messages_chats).left, this.splits.size() - 1);
            ApiWrap$LeftChannelsProcess apiWrap$LeftChannelsProcess2 = this._leftChannelsProcess;
            apiWrap$LeftChannelsProcess2.offset += messages_chats.chats.size();
            if (messages_chats instanceof TLRPC.TL_messages_chats) {
                size = ((TLRPC.TL_messages_chats) messages_chats).chats.size();
            } else if (messages_chats instanceof TLRPC.TL_messages_chatsSlice) {
                TLRPC.TL_messages_chatsSlice tL_messages_chatsSlice = (TLRPC.TL_messages_chatsSlice) messages_chats;
                int i = tL_messages_chatsSlice.count;
                zIsEmpty = tL_messages_chatsSlice.chats.isEmpty();
                size = i;
            } else {
                zIsEmpty = false;
                size = 0;
            }
            apiWrap$LeftChannelsProcess2.fullCount = size;
            apiWrap$LeftChannelsProcess2.finished = zIsEmpty;
            Utilities.CallbackReturn<Integer, Boolean> callbackReturn = apiWrap$LeftChannelsProcess2.progress;
            if (callbackReturn == null || callbackReturn.run(Integer.valueOf(apiWrap$LeftChannelsProcess2.info.left.size())).booleanValue()) {
                runnable.run();
            }
        }
    }

    private void requestMessagesCount(final int i) {
        requestChatMessages(this._chatProcess.info.splits.get(i).intValue(), 0, 0, 1, new Utilities.Callback() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda5
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                ExportRequestsController.this.lambda$requestMessagesCount$8(i, (TLRPC.messages_Messages) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestMessagesCount$8(int i, TLRPC.messages_Messages messages_messages) {
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
            throw new IllegalStateException("Unexpected messagesNotModified received");
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
            requestChatMessages(this._chatProcess.info.splits.get(i).intValue(), 1, -1, 1, new Utilities.Callback() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda21
                @Override // org.telegram.messenger.Utilities.Callback
                public final void run(Object obj) {
                    ExportRequestsController.this.lambda$checkFirstMessageDate$9(i, i2, (TLRPC.messages_Messages) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$checkFirstMessageDate$9(int i, int i2, TLRPC.messages_Messages messages_messages) {
        if (!DataTypesUtils.SingleMessageBefore(messages_messages, this._settings.singlePeerTill)) {
            i2 = 0;
        }
        messagesCountLoaded(i, i2);
    }

    private void messagesCountLoaded(int i, int i2) {
        this._chatProcess.info.messagesCountPerSplit.set(i, Integer.valueOf(i2));
        int i3 = i + 1;
        if (i3 < this._chatProcess.info.splits.size()) {
            requestMessagesCount(i3);
            return;
        }
        ApiWrap$ChatProcess apiWrap$ChatProcess = this._chatProcess;
        if (apiWrap$ChatProcess.start.run(apiWrap$ChatProcess.info).booleanValue()) {
            requestMessagesSlice();
        }
    }

    private void requestMessagesSlice() {
        ApiWrap$ChatProcess apiWrap$ChatProcess = this._chatProcess;
        if (apiWrap$ChatProcess.info.messagesCountPerSplit.get(apiWrap$ChatProcess.localSplitIndex).intValue() == 0) {
            loadMessagesFiles(new ApiWrap$MessagesSlice());
        } else {
            ApiWrap$ChatProcess apiWrap$ChatProcess2 = this._chatProcess;
            requestChatMessages(apiWrap$ChatProcess2.info.splits.get(apiWrap$ChatProcess2.localSplitIndex).intValue(), this._chatProcess.largestIdPlusOne, -100, 100, new Utilities.Callback() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda30
                @Override // org.telegram.messenger.Utilities.Callback
                public final void run(Object obj) {
                    ExportRequestsController.this.lambda$requestMessagesSlice$10((TLRPC.messages_Messages) obj);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestMessagesSlice$10(TLRPC.messages_Messages messages_messages) {
        if (messages_messages instanceof TLRPC.TL_messages_messagesNotModified) {
            throw new IllegalStateException("Unexpected messagesNotModified received.");
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
                it.next();
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
        final Runnable runnable = new Runnable() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda44
            @Override // java.lang.Runnable
            public final void run() {
                ExportRequestsController.this.lambda$resolveCustomEmoji$11(arrayList);
            }
        };
        TLRPC.TL_messages_getCustomEmojiDocuments tL_messages_getCustomEmojiDocuments = new TLRPC.TL_messages_getCustomEmojiDocuments();
        tL_messages_getCustomEmojiDocuments.document_id = arrayList;
        mainRequest(tL_messages_getCustomEmojiDocuments, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda45
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$resolveCustomEmoji$12(runnable, (TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resolveCustomEmoji$11(ArrayList arrayList) {
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            Object obj = arrayList.get(i);
            i++;
            Long l = (Long) obj;
            if (!this._resolvedCustomEmoji.containsKey(l)) {
                ApiWrap$Document apiWrap$Document = new ApiWrap$Document();
                ApiWrap$File apiWrap$File = new ApiWrap$File();
                apiWrap$Document.file = apiWrap$File;
                apiWrap$File.skipReason = ApiWrap$File.SkipReason.Unavailable;
                this._resolvedCustomEmoji.put(l, apiWrap$Document);
            }
        }
        resolveCustomEmoji();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$resolveCustomEmoji$12(Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            FileLog.e("Export Error: Failed to get documents for emoji.");
            runnable.run();
            return;
        }
        if (tLObject instanceof Vector) {
            ArrayList<TLObject> arrayList = ((Vector) tLObject).objects;
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Object obj = arrayList.get(i);
                i++;
                ApiWrap$ChatProcess apiWrap$ChatProcess = this._chatProcess;
                ApiWrap$Document apiWrap$DocumentParseDocument = DataTypesUtils.ParseDocument(apiWrap$ChatProcess.context, (TLRPC.Document) ((TLObject) obj), apiWrap$ChatProcess.info.relativePath, 0);
                this._resolvedCustomEmoji.put(Long.valueOf(apiWrap$DocumentParseDocument.id), apiWrap$DocumentParseDocument);
            }
            runnable.run();
        }
    }

    private void loadNextMessageFile() {
        ArrayList<ApiWrap$Message> arrayList = this._chatProcess.slice.list;
        while (this._chatProcess.fileIndex < arrayList.size()) {
            ApiWrap$Message apiWrap$Message = arrayList.get(this._chatProcess.fileIndex);
            if (!DataTypesUtils.SkipMessageByDate(apiWrap$Message, this._settings)) {
                if (!this.messageCustomEmojiReady(apiWrap$Message)) {
                    return;
                }
                if (!processFileLoad(arrayList.get(this._chatProcess.fileIndex).getFile(), this.currentFileMessageOrigin(), new Utilities.CallbackReturn<ApiWrap$FileProgress, Boolean>() {
                    @Override
                    public Boolean run(ApiWrap$FileProgress progress) {
                        return ExportRequestsController.this.loadMessageFileProgress(progress);
                    }
                }, new Utilities.Callback<String>() {
                    @Override
                    public void run(String obj) {
                        ExportRequestsController.this.loadMessageFileDone(obj);
                    }
                }, this.currentFileMessage(), null) || !processFileLoad(apiWrap$Message.getFile(), this.currentFileMessageOrigin(), new Utilities.CallbackReturn<ApiWrap$FileProgress, Boolean>() {
                    @Override
                    public Boolean run(ApiWrap$FileProgress progress) {
                        return ExportRequestsController.this.loadMessageFileProgress(progress);
                    }
                }, new Utilities.Callback<String>() {
                    @Override
                    public void run(String obj) {
                        ExportRequestsController.this.loadMessageThumbDone(obj);
                    }
                }, this.currentFileMessage(), null)) {
                    return;
                }
            }
            this._chatProcess.fileIndex++;
        }
        this.finishMessagesSlice();
    }

    private ApiWrap$FileOrigin currentFileMessageOrigin() {
        int size;
        TLRPC.InputPeer inputPeer;
        ApiWrap$ChatProcess apiWrap$ChatProcess = this._chatProcess;
        Integer num = apiWrap$ChatProcess.info.splits.get(apiWrap$ChatProcess.localSplitIndex);
        if (num.intValue() >= 0) {
            size = num.intValue();
        } else {
            size = this._chatProcess.info.splits.size() + num.intValue();
        }
        int i = size;
        int iIntValue = num.intValue();
        ApiWrap$ChatProcess apiWrap$ChatProcess2 = this._chatProcess;
        if (iIntValue >= 0) {
            inputPeer = apiWrap$ChatProcess2.info.input;
        } else {
            inputPeer = apiWrap$ChatProcess2.info.migratedFromInput;
        }
        return new ApiWrap$FileOrigin(i, inputPeer, currentFileMessage().id, 0, 0L);
    }

    private ApiWrap$Message currentFileMessage() {
        ApiWrap$ChatProcess apiWrap$ChatProcess = this._chatProcess;
        return apiWrap$ChatProcess.slice.list.get(apiWrap$ChatProcess.fileIndex);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean loadMessageFileProgress(ApiWrap$FileProgress apiWrap$FileProgress) {
        ApiWrap$FileProcess apiWrap$FileProcess = this._fileProcess;
        return this._chatProcess.fileProgress.run(new ApiWrap$DownloadProgress(apiWrap$FileProcess.randomId, apiWrap$FileProcess.relativePath, this._chatProcess.fileIndex, apiWrap$FileProgress.ready(), apiWrap$FileProgress.total())).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadMessageThumbDone(String str) {
        ApiWrap$ChatProcess apiWrap$ChatProcess = this._chatProcess;
        ApiWrap$File apiWrap$File = apiWrap$ChatProcess.slice.list.get(apiWrap$ChatProcess.fileIndex).media.getThumb().file;
        if (str.contains("null")) {
            throw new IllegalStateException("zdes 1");
        }
        apiWrap$File.relativePath = str;
        if (str.isEmpty()) {
            apiWrap$File.skipReason = ApiWrap$File.SkipReason.Unavailable;
        }
        loadNextMessageFile();
    }

    private void loadFile(ApiWrap$File apiWrap$File, ApiWrap$FileOrigin apiWrap$FileOrigin, Utilities.CallbackReturn<ApiWrap$FileProgress, Boolean> callbackReturn, Utilities.Callback<String> callback) {
        ApiWrap$FileProcess apiWrap$FileProcessPrepareFileProcess = prepareFileProcess(apiWrap$File, apiWrap$FileOrigin);
        this._fileProcess = apiWrap$FileProcessPrepareFileProcess;
        apiWrap$FileProcessPrepareFileProcess.progress = callbackReturn;
        apiWrap$FileProcessPrepareFileProcess.done = callback;
        if (callbackReturn == null || callbackReturn.run(new ApiWrap$FileProgress(apiWrap$FileProcessPrepareFileProcess.file.size(), this._fileProcess.size)).booleanValue()) {
            loadFilePart();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadMessageFileDone(String str) {
        ApiWrap$ChatProcess apiWrap$ChatProcess = this._chatProcess;
        ApiWrap$File file = apiWrap$ChatProcess.slice.list.get(apiWrap$ChatProcess.fileIndex).getFile();
        file.relativePath = str;
        if (str.isEmpty()) {
            file.skipReason = ApiWrap$File.SkipReason.Unavailable;
        }
        loadNextMessageFile();
    }

    private void loadFilePart() {
        ApiWrap$FileProcess apiWrap$FileProcess = this._fileProcess;
        if (apiWrap$FileProcess == null || apiWrap$FileProcess.requestId != 0 || apiWrap$FileProcess.requests.size() >= 2) {
            return;
        }
        ApiWrap$FileProcess apiWrap$FileProcess2 = this._fileProcess;
        long j = apiWrap$FileProcess2.size;
        if (j <= 0 || apiWrap$FileProcess2.offset < j) {
            final long j2 = apiWrap$FileProcess2.offset;
            ApiWrap$FileProcess.Request request = new ApiWrap$FileProcess.Request();
            request.offset = j2;
            this._fileProcess.requests.add(request);
            ApiWrap$FileProcess apiWrap$FileProcess3 = this._fileProcess;
            apiWrap$FileProcess3.requestId = fileRequest(apiWrap$FileProcess3.location, apiWrap$FileProcess3.offset, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda39
                @Override // org.telegram.messenger.Utilities.Callback2
                public final void run(Object obj, Object obj2) {
                    ExportRequestsController.this.lambda$loadFilePart$13(j2, (TLObject) obj, (TLRPC.TL_error) obj2);
                }
            });
            this._fileProcess.offset += 1048576;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$loadFilePart$13(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        this._fileProcess.requestId = 0L;
        filePartDone(j, tLObject);
    }

    private void filePartRefreshReference(final long j) {
        ApiWrap$FileOrigin apiWrap$FileOrigin = this._fileProcess.origin;
        if (apiWrap$FileOrigin.storyId() != 0) {
            TL_stories.TL_stories_getStoriesByID tL_stories_getStoriesByID = new TL_stories.TL_stories_getStoriesByID();
            tL_stories_getStoriesByID.peer = new TLRPC.TL_inputPeerSelf();
            tL_stories_getStoriesByID.id = new ArrayList<>(Collections.singletonList(Integer.valueOf(apiWrap$FileOrigin.storyId())));
            this._fileProcess.requestId = mainRequest(tL_stories_getStoriesByID, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda53
                @Override // org.telegram.messenger.Utilities.Callback2
                public final void run(Object obj, Object obj2) {
                    ExportRequestsController.this.lambda$filePartRefreshReference$14(j, (TLObject) obj, (TLRPC.TL_error) obj2);
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
            this._fileProcess.requestId = mainRequest(tL_channels_getMessages, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda54
                @Override // org.telegram.messenger.Utilities.Callback2
                public final void run(Object obj, Object obj2) {
                    ExportRequestsController.this.lambda$filePartRefreshReference$15(j, (TLObject) obj, (TLRPC.TL_error) obj2);
                }
            });
            return;
        }
        TLRPC.TL_messages_getMessages tL_messages_getMessages = new TLRPC.TL_messages_getMessages();
        tL_messages_getMessages.id = new ArrayList<>(Collections.singletonList(Integer.valueOf(apiWrap$FileOrigin.messageId())));
        this._fileProcess.requestId = splitRequest(apiWrap$FileOrigin.split(), tL_messages_getMessages, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda55
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$filePartRefreshReference$16(j, (TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$filePartRefreshReference$14(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            ApiWrap$FileProcess apiWrap$FileProcess = this._fileProcess;
            apiWrap$FileProcess.requestId = 0L;
            apiWrap$FileProcess.done.run(_UrlKt.FRAGMENT_ENCODE_SET);
        } else if (tLObject instanceof TL_stories.TL_stories_stories) {
            this._fileProcess.requestId = 0L;
            filePartExtractReference(j, (TL_stories.TL_stories_stories) tLObject);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$filePartRefreshReference$15(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            this._fileProcess.requestId = 0L;
            FileLog.w("Export Error: File unavailable.");
            this._fileProcess.done.run(_UrlKt.FRAGMENT_ENCODE_SET);
        } else if (tLObject instanceof TLRPC.TL_messages_messages) {
            this._fileProcess.requestId = 0L;
            filePartExtractReference(j, (TLRPC.TL_messages_messages) tLObject);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$filePartRefreshReference$16(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            this._fileProcess.requestId = 0L;
            Log.w("exteraGram", "Export Error: File unavailable.");
            this._fileProcess.done.run(_UrlKt.FRAGMENT_ENCODE_SET);
        } else if (tLObject instanceof TLRPC.messages_Messages) {
            this._fileProcess.requestId = 0L;
            filePartExtractReference(j, (TLRPC.messages_Messages) tLObject);
        }
    }

    private void filePartExtractReference(final long j, TL_stories.TL_stories_stories tL_stories_stories) {
        int i = 0;
        ArrayList<ApiWrap$Story> arrayList = DataTypesUtils.ParseStoriesSlice(tL_stories_stories.stories, 0).list;
        int size = arrayList.size();
        while (i < size) {
            ApiWrap$Story apiWrap$Story = arrayList.get(i);
            i++;
            ApiWrap$Story apiWrap$Story2 = apiWrap$Story;
            if (apiWrap$Story2.id == this._fileProcess.origin.storyId()) {
                boolean zRefreshFileReference = DataTypesUtils.RefreshFileReference(this._fileProcess.location.data, apiWrap$Story2.file().location.data);
                boolean zRefreshFileReference2 = DataTypesUtils.RefreshFileReference(this._fileProcess.location.data, apiWrap$Story2.thumb().file.location.data);
                if (zRefreshFileReference || zRefreshFileReference2) {
                    ApiWrap$FileProcess apiWrap$FileProcess = this._fileProcess;
                    apiWrap$FileProcess.requestId = fileRequest(apiWrap$FileProcess.location, j, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda0
                        @Override // org.telegram.messenger.Utilities.Callback2
                        public final void run(Object obj, Object obj2) {
                            ExportRequestsController.this.lambda$filePartExtractReference$17(j, (TLObject) obj, (TLRPC.TL_error) obj2);
                        }
                    });
                    return;
                }
            }
        }
        this._fileProcess.done.run(_UrlKt.FRAGMENT_ENCODE_SET);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$filePartExtractReference$17(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_upload_file) {
            this._fileProcess.requestId = 0L;
            filePartDone(j, (TLRPC.TL_upload_file) tLObject);
        }
    }

    private void filePartExtractReference(final long j, TLRPC.messages_Messages messages_messages) {
        if (messages_messages instanceof TLRPC.TL_messages_messagesNotModified) {
            throw new IllegalStateException("wtf, TL_messages_messagesNotModified received!");
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
                    apiWrap$FileProcess.requestId = fileRequest(apiWrap$FileProcess.location, j, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda3
                        @Override // org.telegram.messenger.Utilities.Callback2
                        public final void run(Object obj, Object obj2) {
                            ExportRequestsController.this.lambda$filePartExtractReference$18(j, (TLObject) obj, (TLRPC.TL_error) obj2);
                        }
                    });
                    return;
                }
            }
        }
        FileLog.w("Export Error: File unavailable.");
        this._fileProcess.done.run(_UrlKt.FRAGMENT_ENCODE_SET);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$filePartExtractReference$18(long j, TLObject tLObject, TLRPC.TL_error tL_error) {
        this._fileProcess.requestId = 0L;
        filePartDone(j, tLObject);
    }

    private void filePartDone(long j, TLObject tLObject) {
        if (tLObject instanceof TLRPC.TL_upload_fileCdnRedirect) {
            throw new IllegalArgumentException("TL_upload_fileCdnRedirect received! not supported.");
        }
        if (tLObject instanceof TLRPC.TL_upload_file) {
            TLRPC.TL_upload_file tL_upload_file = (TLRPC.TL_upload_file) tLObject;
            int limit = tL_upload_file.bytes.limit();
            ApiWrap$FileProcess apiWrap$FileProcess = this._fileProcess;
            if (limit == 0) {
                if (apiWrap$FileProcess.size > 0) {
                    throw new IllegalStateException("received data has 0 length and fileProcess size is not 0!!!");
                }
                if (!apiWrap$FileProcess.file.writeBlock("").isSuccess()) {
                    throw new IllegalStateException("writing empty block was not successful!");
                }
            } else {
                ApiWrap$FileProcess.Request req = null;
                for (ApiWrap$FileProcess.Request request : apiWrap$FileProcess.requests) {
                    if (request.offset == j) {
                        req = request;
                        break;
                    }
                }
                if (req == null) {
                    throw new IllegalStateException("req not found!");
                }
                req.bytes = tL_upload_file.bytes;
                while (!this._fileProcess.requests.isEmpty()) {
                    ApiWrap$FileProcess.Request first = this._fileProcess.requests.getFirst();
                    if (first.bytes == null || first.bytes.limit() == 0) {
                        break;
                    }
                    if (!this._fileProcess.file.writeBlock(first.bytes).isSuccess()) {
                        throw new IllegalStateException("wtf! tried to write: " + first.bytes.buffer.get());
                    }
                    this._fileProcess.requests.removeFirst();
                }
                if (this._fileProcess.progress != null) {
                    this._fileProcess.progress.run(new ApiWrap$FileProgress(this._fileProcess.file.size(), this._fileProcess.size));
                }
                if (this._fileProcess.requests.isEmpty() && this._fileProcess.size != 0 && this._fileProcess.size <= this._fileProcess.offset) {
                    // fall through
                } else {
                    loadFilePart();
                    return;
                }
            }
        }
        ApiWrap$FileProcess apiWrap$FileProcess2 = this._fileProcess;
        this._fileCache.save(apiWrap$FileProcess2.location, apiWrap$FileProcess2.relativePath);
        apiWrap$FileProcess2.done.run(apiWrap$FileProcess2.relativePath);
    }

    private ApiWrap$FileProcess prepareFileProcess(ApiWrap$File apiWrap$File, ApiWrap$FileOrigin apiWrap$FileOrigin) {
        String strPrepareRelativePath = OutputFile.PrepareRelativePath(this._settings.path, apiWrap$File.suggestedPath);
        ApiWrap$FileProcess apiWrap$FileProcess = new ApiWrap$FileProcess(this._settings.path + "/" + strPrepareRelativePath, this._stats);
        apiWrap$FileProcess.relativePath = strPrepareRelativePath;
        apiWrap$FileProcess.location = apiWrap$File.location;
        apiWrap$FileProcess.size = apiWrap$File.size;
        apiWrap$FileProcess.origin = apiWrap$FileOrigin;
        apiWrap$FileProcess.randomId = Utilities.random.nextLong();
        return apiWrap$FileProcess;
    }

    private boolean processFileLoad(ApiWrap$File apiWrap$File, ApiWrap$FileOrigin apiWrap$FileOrigin, Utilities.CallbackReturn<ApiWrap$FileProgress, Boolean> callbackReturn, Utilities.Callback<String> callback, ApiWrap$Message apiWrap$Message, ApiWrap$Story apiWrap$Story) {
        Object objFile;
        int i;
        long j;
        byte[] bArr;
        if (!apiWrap$File.relativePath.isEmpty() || apiWrap$File.skipReason != ApiWrap$File.SkipReason.None) {
            return true;
        }
        if (apiWrap$File.location == null && ((bArr = apiWrap$File.content) == null || bArr.length == 0)) {
            apiWrap$File.skipReason = ApiWrap$File.SkipReason.Unavailable;
            return true;
        }
        if (writePreloadedFile(apiWrap$File, apiWrap$FileOrigin)) {
            return !apiWrap$File.relativePath.isEmpty();
        }
        if (apiWrap$Message != null) {
            objFile = apiWrap$Message.media;
        } else {
            objFile = apiWrap$Story != null ? apiWrap$Story.file() : null;
        }
        if (objFile instanceof ApiWrap$Media) {
            Object obj = ((ApiWrap$Media) objFile).content;
            if (obj instanceof ApiWrap$Document) {
                ApiWrap$Document apiWrap$Document = (ApiWrap$Document) obj;
                if (apiWrap$Document.isSticker) {
                    i = 16;
                } else if (apiWrap$Document.isVideoMessage) {
                    i = 8;
                } else if (apiWrap$Document.isVoiceMessage) {
                    i = 4;
                } else if (apiWrap$Document.isAnimated) {
                    i = 32;
                } else {
                    i = apiWrap$Document.isVideoFile ? 2 : 64;
                }
            } else {
                i = 1;
            }
        } else {
            i = 0;
        }
        if (apiWrap$Message != null) {
            j = apiWrap$Message.getFile().size;
        } else if (apiWrap$Story != null) {
            j = apiWrap$Story.file().size;
        } else {
            j = apiWrap$File.size;
        }
        if (apiWrap$Message != null && DataTypesUtils.SkipMessageByDate(apiWrap$Message, this._settings)) {
            apiWrap$File.skipReason = ApiWrap$File.SkipReason.DateLimits;
            return true;
        }
        if (apiWrap$Story == null && (this._settings.media.type & i) != i) {
            apiWrap$File.skipReason = ApiWrap$File.SkipReason.FileType;
            return true;
        }
        if (apiWrap$Story == null && j > this._settings.media.sizeLimit) {
            apiWrap$File.skipReason = ApiWrap$File.SkipReason.FileSize;
            return true;
        }
        loadFile(apiWrap$File, apiWrap$FileOrigin, callbackReturn, callback);
        return false;
    }

    private boolean writePreloadedFile(ApiWrap$File apiWrap$File, ApiWrap$FileOrigin apiWrap$FileOrigin) {
        String strFind = this._fileCache.find(apiWrap$File.location);
        if (strFind != null) {
            apiWrap$File.relativePath = strFind;
            return true;
        }
        byte[] bArr = apiWrap$File.content;
        if (bArr == null || bArr.length == 0) {
            return false;
        }
        ApiWrap$FileProcess apiWrap$FileProcessPrepareFileProcess = prepareFileProcess(apiWrap$File, apiWrap$FileOrigin);
        if (apiWrap$FileProcessPrepareFileProcess.file.writeBlock(apiWrap$File.content).isSuccess()) {
            String str = apiWrap$FileProcessPrepareFileProcess.relativePath;
            apiWrap$File.relativePath = str;
            this._fileCache.save(apiWrap$File.location, str);
        }
        return true;
    }

    public void requestUserpics(Utilities.CallbackReturn<ApiWrap$UserpicsInfo, Boolean> callbackReturn, Utilities.CallbackReturn<ApiWrap$DownloadProgress, Boolean> callbackReturn2, Utilities.CallbackReturn<ArrayList<HtmlWriter.Photo>, Boolean> callbackReturn3, Runnable runnable) {
        ApiWrap$UserpicsProcess apiWrap$UserpicsProcess = new ApiWrap$UserpicsProcess();
        this._userpicsProcess = apiWrap$UserpicsProcess;
        apiWrap$UserpicsProcess.start = callbackReturn;
        apiWrap$UserpicsProcess.fileProgress = callbackReturn2;
        apiWrap$UserpicsProcess.handleSlice = callbackReturn3;
        apiWrap$UserpicsProcess.finish = runnable;
        TLRPC.TL_photos_getUserPhotos tL_photos_getUserPhotos = new TLRPC.TL_photos_getUserPhotos();
        tL_photos_getUserPhotos.limit = 100;
        tL_photos_getUserPhotos.user_id = new TLRPC.TL_inputUserSelf();
        tL_photos_getUserPhotos.offset = 0;
        tL_photos_getUserPhotos.max_id = this._userpicsProcess.maxId;
        mainRequest(tL_photos_getUserPhotos, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda13
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$requestUserpics$19((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestUserpics$19(TLObject tLObject, TLRPC.TL_error tL_error) {
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
        mainRequest(tL_stories_getStoriesArchive, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda14
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$requestStories$20((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestStories$20(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TL_stories.TL_stories_stories) {
            TL_stories.TL_stories_stories tL_stories_stories = (TL_stories.TL_stories_stories) tLObject;
            if (this._storiesProcess.start.run(Integer.valueOf(tL_stories_stories.count)).booleanValue()) {
                loadStoriesFiles(DataTypesUtils.ParseStoriesSlice(tL_stories_stories.stories, this._storiesProcess.processed));
            }
        }
    }

    private void loadStoriesFiles(ApiWrap$StoriesSlice apiWrap$StoriesSlice) {
        if (apiWrap$StoriesSlice.lastId == 0) {
            this._storiesProcess.lastSlice = true;
        }
        ApiWrap$StoriesProcess apiWrap$StoriesProcess = this._storiesProcess;
        apiWrap$StoriesProcess.slice = apiWrap$StoriesSlice;
        apiWrap$StoriesProcess.fileIndex = 0;
        loadNextStory();
    }

    private void loadNextStory() {
        ArrayList<ApiWrap$Story> arrayList = this._storiesProcess.slice.list;
        while (this._storiesProcess.fileIndex < arrayList.size()) {
            ApiWrap$Story apiWrap$Story = arrayList.get(this._storiesProcess.fileIndex);
            ApiWrap$FileOrigin apiWrap$FileOrigin = new ApiWrap$FileOrigin(0, null, 0, apiWrap$Story.id, 0L);
            if (!processFileLoad(apiWrap$Story.file(), apiWrap$FileOrigin, new Utilities.CallbackReturn<ApiWrap$FileProgress, Boolean>() {
                @Override
                public Boolean run(ApiWrap$FileProgress obj) {
                    return ExportRequestsController.this.loadStoryProgress(obj);
                }
            }, new Utilities.Callback<String>() {
                @Override
                public void run(String obj) {
                    ExportRequestsController.this.loadStoryDone(obj);
                }
            }, null, apiWrap$Story) || !processFileLoad(apiWrap$Story.thumb().file, apiWrap$FileOrigin, new Utilities.CallbackReturn<ApiWrap$FileProgress, Boolean>() {
                @Override
                public Boolean run(ApiWrap$FileProgress obj) {
                    return ExportRequestsController.this.loadStoryProgress(obj);
                }
            }, new Utilities.Callback<String>() {
                @Override
                public void run(String obj) {
                    ExportRequestsController.this.loadStoryThumbDone(obj);
                }
            }, null, apiWrap$Story)) {
                return;
            }
            this._storiesProcess.fileIndex++;
        }
        this.finishStoriesSlice();
    }

    private void finishStoriesSlice() {
        ApiWrap$StoriesProcess apiWrap$StoriesProcess = this._storiesProcess;
        ApiWrap$StoriesSlice apiWrap$StoriesSlice = apiWrap$StoriesProcess.slice;
        if (apiWrap$StoriesSlice.lastId != 0) {
            apiWrap$StoriesProcess.processed += apiWrap$StoriesSlice.list.size();
            ApiWrap$StoriesProcess apiWrap$StoriesProcess2 = this._storiesProcess;
            apiWrap$StoriesProcess2.offsetId = apiWrap$StoriesSlice.lastId;
            if (!apiWrap$StoriesProcess2.handleSlice.run(apiWrap$StoriesSlice).booleanValue()) {
                return;
            }
        }
        ApiWrap$StoriesProcess apiWrap$StoriesProcess3 = this._storiesProcess;
        if (apiWrap$StoriesProcess3.lastSlice) {
            apiWrap$StoriesProcess3.finish.run();
            return;
        }
        TL_stories.TL_stories_getStoriesArchive tL_stories_getStoriesArchive = new TL_stories.TL_stories_getStoriesArchive();
        tL_stories_getStoriesArchive.peer = new TLRPC.TL_inputPeerSelf();
        tL_stories_getStoriesArchive.limit = 100;
        tL_stories_getStoriesArchive.offset_id = this._storiesProcess.offsetId;
        mainRequest(tL_stories_getStoriesArchive, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda40
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$finishStoriesSlice$21((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finishStoriesSlice$21(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TL_stories.TL_stories_stories) {
            loadStoriesFiles(DataTypesUtils.ParseStoriesSlice(((TL_stories.TL_stories_stories) tLObject).stories, this._storiesProcess.processed));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean loadStoryProgress(ApiWrap$FileProgress apiWrap$FileProgress) {
        ApiWrap$FileProcess apiWrap$FileProcess = this._fileProcess;
        return this._storiesProcess.fileProgress.run(new ApiWrap$DownloadProgress(apiWrap$FileProcess.randomId, apiWrap$FileProcess.relativePath, this._storiesProcess.fileIndex, apiWrap$FileProgress.ready(), apiWrap$FileProgress.total())).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadStoryThumbDone(String str) {
        ApiWrap$StoriesProcess apiWrap$StoriesProcess = this._storiesProcess;
        ApiWrap$File apiWrap$File = apiWrap$StoriesProcess.slice.list.get(apiWrap$StoriesProcess.fileIndex).thumb().file;
        apiWrap$File.relativePath = str;
        if (str.isEmpty()) {
            apiWrap$File.skipReason = ApiWrap$File.SkipReason.Unavailable;
        }
        loadNextStory();
    }

    /* JADX INFO: Access modifiers changed from: private */
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
        mainRequest(new ExportRequests$TL_contacts_getSaved(), new Utilities.Callback2() {
            @Override
            public void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$requestContacts$24((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestContacts$24(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof Vector) {
            this._contactsProcess.result = DataTypesUtils.ParseContactsList((Vector) tLObject);
            new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda16
                @Override // org.telegram.messenger.Utilities.Callback2
                public final void run(Object obj, Object obj2) {
                    ExportRequestsController.this.lambda$requestContacts$23((Integer) obj, (Utilities.Callback2) obj2);
                }
            };
            requestTopPeersSlice();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestContacts$23(final Integer num, final Utilities.Callback2 callback2) {
        if (num.intValue() == this._contactsProcess.result.list.size()) {
            requestTopPeersSlice();
            return;
        }
        TLRPC.TL_contacts_resolvePhone tL_contacts_resolvePhone = new TLRPC.TL_contacts_resolvePhone();
        tL_contacts_resolvePhone.phone = this._contactsProcess.result.list.get(num.intValue()).phoneNumber;
        mainRequest(tL_contacts_resolvePhone, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda42
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$requestContacts$22(num, callback2, (TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestContacts$22(Integer num, Utilities.Callback2 callback2, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (!(tLObject instanceof TLRPC.TL_contacts_resolvedPeer)) {
            if (tL_error != null) {
                callback2.run(Integer.valueOf(num.intValue() + 1), callback2);
                return;
            }
            return;
        }
        ApiWrap$ContactInfo apiWrap$ContactInfo = this._contactsProcess.result.list.get(num.intValue());
        TLRPC.Peer peer = ((TLRPC.TL_contacts_resolvedPeer) tLObject).peer;
        if (peer instanceof TLRPC.TL_peerUser) {
            apiWrap$ContactInfo.userId = Long.valueOf(((TLRPC.TL_peerUser) peer).user_id);
        } else {
            apiWrap$ContactInfo.userId = 0L;
        }
        this._contactsProcess.result.list.set(num.intValue(), apiWrap$ContactInfo);
        callback2.run(Integer.valueOf(num.intValue() + 1), callback2);
    }

    private void requestTopPeersSlice() {
        TLRPC.TL_contacts_getTopPeers tL_contacts_getTopPeers = new TLRPC.TL_contacts_getTopPeers();
        tL_contacts_getTopPeers.correspondents = true;
        tL_contacts_getTopPeers.bots_inline = true;
        tL_contacts_getTopPeers.phone_calls = true;
        tL_contacts_getTopPeers.offset = this._contactsProcess.topPeersOffset;
        tL_contacts_getTopPeers.limit = 100;
        tL_contacts_getTopPeers.hash = 0L;
        mainRequest(tL_contacts_getTopPeers, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda41
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$requestTopPeersSlice$25((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestTopPeersSlice$25(TLObject tLObject, TLRPC.TL_error tL_error) {
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
            if (!processFileLoad(arrayList.get(this._userpicsProcess.fileIndex).image.file, new ApiWrap$FileOrigin(), new Utilities.CallbackReturn<ApiWrap$FileProgress, Boolean>() {
                @Override
                public Boolean run(ApiWrap$FileProgress obj) {
                    return ExportRequestsController.this.loadUserpicProgress(obj);
                }
            }, new Utilities.Callback<String>() {
                @Override
                public void run(String obj) {
                    ExportRequestsController.this.loadUserpicDone(obj);
                }
            }, null, null)) {
                return;
            }
            this._userpicsProcess.fileIndex++;
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
        mainRequest(tL_photos_getUserPhotos, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda46
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$finishUserpicsSlice$26((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$finishUserpicsSlice$26(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.photos_Photos) {
            handleUserpicsSlice((TLRPC.photos_Photos) tLObject);
        }
    }

    public boolean loadUserpicProgress(ApiWrap$FileProgress apiWrap$FileProgress) {
        ApiWrap$FileProcess apiWrap$FileProcess = this._fileProcess;
        return this._userpicsProcess.fileProgress.run(new ApiWrap$DownloadProgress(apiWrap$FileProcess.randomId, apiWrap$FileProcess.relativePath, this._userpicsProcess.fileIndex, apiWrap$FileProgress.ready(), apiWrap$FileProgress.total())).booleanValue();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void loadUserpicDone(String str) {
        ApiWrap$UserpicsProcess apiWrap$UserpicsProcess = this._userpicsProcess;
        ApiWrap$File apiWrap$File = apiWrap$UserpicsProcess.slice.get(apiWrap$UserpicsProcess.fileIndex).image.file;
        apiWrap$File.relativePath = str;
        if (str.isEmpty()) {
            apiWrap$File.skipReason = ApiWrap$File.SkipReason.Unavailable;
        }
        loadNextUserpic();
    }

    public boolean messageCustomEmojiReady(ApiWrap$Message apiWrap$Message) {
        ArrayList<ApiWrap$TextPart> arrayList = apiWrap$Message.text;
        int size = arrayList.size();
        int i = 0;
        while (i < size) {
            ApiWrap$TextPart apiWrap$TextPart = arrayList.get(i);
            i++;
            ApiWrap$TextPart apiWrap$TextPart2 = apiWrap$TextPart;
            if (apiWrap$TextPart2.type == ApiWrap$TextPart.Type.CustomEmoji) {
                String customEmoji = getCustomEmoji(apiWrap$TextPart2.additional);
                if (customEmoji == null || customEmoji.isEmpty()) {
                    return false;
                }
                apiWrap$TextPart2.additional = customEmoji;
            }
        }
        Iterator<ApiWrap$Reaction> it = apiWrap$Message.reactions.iterator();
        if (!it.hasNext()) {
            return true;
        }
        it.next();
        throw null;
    }

    private String getCustomEmoji(String str) {
        Long l = Utilities.parseLong(str);
        if (l.longValue() == 0) {
            return str;
        }
        ApiWrap$Document apiWrap$Document = this._resolvedCustomEmoji.get(l);
        if (apiWrap$Document == null) {
            return ApiWrap$TextPart.UnavailableEmoji();
        }
        ApiWrap$File apiWrap$File = apiWrap$Document.file;
        ApiWrap$FileOrigin apiWrap$FileOrigin = new ApiWrap$FileOrigin(0, null, 0, 0, l.longValue());
        final long jLongValue = l.longValue();
        if (!processFileLoad(apiWrap$File, apiWrap$FileOrigin, new Utilities.CallbackReturn<ApiWrap$FileProgress, Boolean>() {
            @Override
            public Boolean run(ApiWrap$FileProgress progress) {
                return ExportRequestsController.this.loadMessageFileProgress(progress);
            }
        }, new Utilities.Callback<String>() {
            @Override
            public void run(String obj) {
                ExportRequestsController.this.lambda$getCustomEmoji$27(jLongValue, obj);
            }
        }, null, null)) {
            return null;
        }
        ApiWrap$File.SkipReason skipReason = apiWrap$File.skipReason;
        if (skipReason == ApiWrap$File.SkipReason.Unavailable) {
            return ApiWrap$TextPart.UnavailableEmoji();
        }
        if (skipReason == ApiWrap$File.SkipReason.FileType || skipReason == ApiWrap$File.SkipReason.FileSize) {
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }
        return apiWrap$File.relativePath;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX INFO: renamed from: loadMessageEmojiDone, reason: merged with bridge method [inline-methods] */
    public void lambda$getCustomEmoji$27(long j, String str) {
        ApiWrap$Document apiWrap$Document = this._resolvedCustomEmoji.get(Long.valueOf(j));
        if (apiWrap$Document != null) {
            apiWrap$Document.file.relativePath = str;
            if (str.isEmpty()) {
                apiWrap$Document.file.skipReason = ApiWrap$File.SkipReason.Unavailable;
            }
        }
        loadNextMessageFile();
    }

    private void finishMessagesSlice() {
        ApiWrap$MessagesSlice apiWrap$MessagesSliceAdjustMigrateMessageIds = this._chatProcess.slice;
        if (!apiWrap$MessagesSliceAdjustMigrateMessageIds.list.isEmpty()) {
            ApiWrap$ChatProcess apiWrap$ChatProcess = this._chatProcess;
            ArrayList<ApiWrap$Message> arrayList = apiWrap$MessagesSliceAdjustMigrateMessageIds.list;
            apiWrap$ChatProcess.largestIdPlusOne = arrayList.get(arrayList.size() - 1).id + 1;
            ApiWrap$ChatProcess apiWrap$ChatProcess2 = this._chatProcess;
            if (apiWrap$ChatProcess2.info.splits.get(apiWrap$ChatProcess2.localSplitIndex).intValue() < 0) {
                apiWrap$MessagesSliceAdjustMigrateMessageIds = DataTypesUtils.AdjustMigrateMessageIds(apiWrap$MessagesSliceAdjustMigrateMessageIds);
            }
            if (!this._chatProcess.handleSlice.run(apiWrap$MessagesSliceAdjustMigrateMessageIds).booleanValue()) {
                return;
            }
        }
        ApiWrap$ChatProcess apiWrap$ChatProcess3 = this._chatProcess;
        if (apiWrap$ChatProcess3.lastSlice) {
            int i = apiWrap$ChatProcess3.localSplitIndex + 1;
            apiWrap$ChatProcess3.localSplitIndex = i;
            if (i < apiWrap$ChatProcess3.info.splits.size()) {
                ApiWrap$ChatProcess apiWrap$ChatProcess4 = this._chatProcess;
                apiWrap$ChatProcess4.lastSlice = false;
                apiWrap$ChatProcess4.largestIdPlusOne = 1;
            }
        }
        if (!this._chatProcess.lastSlice) {
            requestMessagesSlice();
        } else {
            finishMessages();
        }
    }

    private void finishMessages() {
        this._chatProcess.done.run();
    }

    private void requestChatMessages(final int i, final int i2, final int i3, final int i4, Utilities.Callback<TLRPC.messages_Messages> callback) {
        TLRPC.InputPeer inputPeer;
        TLRPC.InputPeer tL_inputPeerSelf;
        this._chatProcess.requestDone = callback;
        int size = this.splits.size();
        ApiWrap$ChatProcess apiWrap$ChatProcess = this._chatProcess;
        if (i >= 0) {
            inputPeer = apiWrap$ChatProcess.info.input;
        } else {
            inputPeer = apiWrap$ChatProcess.info.migratedFromInput;
        }
        final TLRPC.InputPeer inputPeer2 = inputPeer;
        int i5 = i >= 0 ? i : size + i;
        ApiWrap$DialogInfo apiWrap$DialogInfo = this._chatProcess.info;
        if (apiWrap$DialogInfo.isMonoforum) {
            tL_inputPeerSelf = apiWrap$DialogInfo.monoforumBroadcastInput;
        } else {
            tL_inputPeerSelf = new TLRPC.TL_inputPeerSelf();
        }
        if (this._chatProcess.info.onlyMyMessages) {
            TLRPC.TL_messages_search tL_messages_search = new TLRPC.TL_messages_search();
            tL_messages_search.flags = 1;
            tL_messages_search.peer = inputPeer2;
            tL_messages_search.q = _UrlKt.FRAGMENT_ENCODE_SET;
            tL_messages_search.from_id = tL_inputPeerSelf;
            tL_messages_search.saved_peer_id = new TLRPC.TL_inputPeerEmpty();
            tL_messages_search.top_msg_id = 0;
            tL_messages_search.filter = new TLRPC.TL_inputMessagesFilterEmpty();
            tL_messages_search.offset_id = i2;
            tL_messages_search.add_offset = i3;
            tL_messages_search.limit = i4;
            splitRequest(i5, tL_messages_search, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda22
                @Override // org.telegram.messenger.Utilities.Callback2
                public final void run(Object obj, Object obj2) {
                    ExportRequestsController.this.lambda$requestChatMessages$28((TLObject) obj, (TLRPC.TL_error) obj2);
                }
            });
            return;
        }
        TLRPC.TL_messages_getHistory tL_messages_getHistory = new TLRPC.TL_messages_getHistory();
        tL_messages_getHistory.peer = inputPeer2;
        tL_messages_getHistory.offset_id = i2;
        tL_messages_getHistory.add_offset = i3;
        tL_messages_getHistory.limit = i4;
        splitRequest(i5, tL_messages_getHistory, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda23
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$requestChatMessages$29(inputPeer2, i, i2, i3, i4, (TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestChatMessages$28(TLObject tLObject, TLRPC.TL_error tL_error) {
        this._chatProcess.requestDone.run((TLRPC.messages_Messages) tLObject);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestChatMessages$29(TLRPC.InputPeer inputPeer, int i, int i2, int i3, int i4, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            if ("CHANNEL_PRIVATE".equals(tL_error.text)) {
                Log.d("exteraGram", "caught channel private");
                if (inputPeer instanceof TLRPC.TL_inputPeerChannel) {
                    ApiWrap$ChatProcess apiWrap$ChatProcess = this._chatProcess;
                    ApiWrap$DialogInfo apiWrap$DialogInfo = apiWrap$ChatProcess.info;
                    if (apiWrap$DialogInfo.onlyMyMessages) {
                        return;
                    }
                    apiWrap$DialogInfo.onlyMyMessages = true;
                    requestChatMessages(i, i2, i3, i4, apiWrap$ChatProcess.requestDone);
                    return;
                }
                return;
            }
            return;
        }
        this._chatProcess.requestDone.run((TLRPC.messages_Messages) tLObject);
    }

    public void requestDialogsSlice() {
        if (this._settings.onlySinglePeer()) {
            requestSinglePeerDialog();
            return;
        }
        TLRPC.TL_messages_getDialogs tL_messages_getDialogs = new TLRPC.TL_messages_getDialogs();
        ApiWrap$DialogsProcess apiWrap$DialogsProcess = this._dialogsProcess;
        tL_messages_getDialogs.offset_peer = apiWrap$DialogsProcess.offsetPeer;
        tL_messages_getDialogs.limit = 100;
        tL_messages_getDialogs.offset_date = apiWrap$DialogsProcess.offsetDate;
        tL_messages_getDialogs.offset_id = apiWrap$DialogsProcess.offsetId;
        splitRequest(apiWrap$DialogsProcess.splitIndexPlusOne - 1, tL_messages_getDialogs, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda15
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$requestDialogsSlice$30((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestDialogsSlice$30(TLObject tLObject, TLRPC.TL_error tL_error) {
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
            requestDialogsSlice();
        } else {
            if (!useOnlyLastSplit()) {
                ApiWrap$DialogsProcess apiWrap$DialogsProcess4 = this._dialogsProcess;
                int i2 = apiWrap$DialogsProcess4.splitIndexPlusOne - 1;
                apiWrap$DialogsProcess4.splitIndexPlusOne = i2;
                if (i2 > 0) {
                    apiWrap$DialogsProcess4.offsetId = 0;
                    apiWrap$DialogsProcess4.offsetDate = 0;
                    apiWrap$DialogsProcess4.offsetPeer = new TLRPC.TL_inputPeerEmpty();
                    requestDialogsSlice();
                    return;
                }
            }
            requestLeftChannelsIfNeeded();
        }
    }

    private void requestSinglePeerDialog() {
        final Utilities.Callback callback = new Utilities.Callback() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda34
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                ExportRequestsController.this.lambda$requestSinglePeerDialog$31((TLObject) obj);
            }
        };
        Utilities.Callback callback2 = new Utilities.Callback() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda35
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                ExportRequestsController.this.lambda$requestSinglePeerDialog$33(callback, (TLRPC.InputUser) obj);
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
            tL_messages_getChats.id = new ArrayList<>(Collections.singletonList(Long.valueOf(((TLRPC.TL_inputPeerChat) inputPeer).chat_id)));
            mainRequest(tL_messages_getChats, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda36
                @Override // org.telegram.messenger.Utilities.Callback2
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
                throw new IllegalStateException("From message peer in requestSinglePeerDialog.");
            } else if (inputPeer instanceof TLRPC.TL_inputPeerChannelFromMessage) {
                throw new IllegalStateException("From message peer in requestSinglePeerDialog.");
            } else if (inputPeer instanceof TLRPC.TL_inputPeerEmpty) {
                throw new IllegalStateException("Empty peer in requestSinglePeerDialog.");
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestSinglePeerDialog$31(TLObject tLObject) {
        if (tLObject instanceof Vector) {
            appendSinglePeerDialogs(ParseDialogsInfo(this._settings.singlePeer, (Vector) tLObject));
        } else if (tLObject instanceof TLRPC.messages_Chats) {
            appendSinglePeerDialogs(ParseDialogsInfo(this._settings.singlePeer, (TLRPC.messages_Chats) tLObject));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestSinglePeerDialog$33(final Utilities.Callback callback, TLRPC.InputUser inputUser) {
        TLRPC.TL_users_getUsers tL_users_getUsers = new TLRPC.TL_users_getUsers();
        tL_users_getUsers.id = new ArrayList<>(Collections.singletonList(inputUser));
        mainRequest(tL_users_getUsers, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda51
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                callback.run((TLObject) obj);
            }
        });
    }

    private void appendSinglePeerDialogs(ApiWrap$DialogsInfo apiWrap$DialogsInfo) {
        Utilities.CallbackReturn callbackReturn = new Utilities.CallbackReturn() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda47
            @Override // org.telegram.messenger.Utilities.CallbackReturn
            public final Object run(Object obj) {
                ApiWrap$DialogInfo.Type type = (ApiWrap$DialogInfo.Type) obj;
                return Boolean.valueOf(type == ApiWrap$DialogInfo.Type.PrivateSupergroup || type == ApiWrap$DialogInfo.Type.PublicSupergroup);
            }
        };
        Utilities.CallbackReturn callbackReturn2 = new Utilities.CallbackReturn() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda48
            @Override // org.telegram.messenger.Utilities.CallbackReturn
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
            return mainRequest(tL_channels_getFullChannel, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda58
                @Override // org.telegram.messenger.Utilities.Callback2
                public final void run(Object obj, Object obj2) {
                    ExportRequestsController.this.lambda$requestSinglePeerMigrated$37((TLObject) obj, (TLRPC.TL_error) obj2);
                }
            });
        }
        throw new IllegalArgumentException("unexpected peer type: " + apiWrap$DialogInfo.input);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestSinglePeerMigrated$37(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_messages_chatFull) {
            TLRPC.TL_messages_chatFull tL_messages_chatFull = (TLRPC.TL_messages_chatFull) tLObject;
            TLRPC.ChatFull chatFull = tL_messages_chatFull.full_chat;
            long j = chatFull instanceof TLRPC.TL_channelFull ? ((TLRPC.TL_channelFull) chatFull).migrated_from_chat_id : 0L;
            if (j != 0) {
                TLRPC.TL_inputPeerChat tL_inputPeerChat = new TLRPC.TL_inputPeerChat();
                tL_inputPeerChat.chat_id = j;
                TLRPC.TL_messages_chats tL_messages_chats = new TLRPC.TL_messages_chats();
                tL_messages_chats.chats = tL_messages_chatFull.chats;
                appendSinglePeerDialogs(ParseDialogsInfo(tL_inputPeerChat, tL_messages_chats));
                return;
            }
            appendSinglePeerDialogs(new ApiWrap$DialogsInfo());
        }
    }

    public void requestLeftChannelsIfNeeded() {
        if ((this._settings.types & 1920) != 0) {
            requestLeftChannelsList(new Utilities.CallbackReturn() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda19
                @Override // org.telegram.messenger.Utilities.CallbackReturn
                public final Object run(Object obj) {
                    return ExportRequestsController.this.lambda$requestLeftChannelsIfNeeded$38((Integer) obj);
                }
            }, new Utilities.Callback() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda20
                @Override // org.telegram.messenger.Utilities.Callback
                public final void run(Object obj) {
                    ExportRequestsController.this.lambda$requestLeftChannelsIfNeeded$39((ApiWrap$DialogsInfo) obj);
                }
            });
        } else {
            finishDialogsList();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ Boolean lambda$requestLeftChannelsIfNeeded$38(Integer num) {
        ApiWrap$DialogsProcess apiWrap$DialogsProcess = this._dialogsProcess;
        return apiWrap$DialogsProcess.progress.run(Integer.valueOf(apiWrap$DialogsProcess.processedCount + num.intValue()));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestLeftChannelsIfNeeded$39(ApiWrap$DialogsInfo apiWrap$DialogsInfo) {
        this._dialogsProcess.info.left = apiWrap$DialogsInfo.left;
        finishDialogsList();
    }

    private void finishDialogsList() {
        DataTypesUtils.FinalizeDialogsInfo(this._dialogsProcess.info, this._settings);
        ApiWrap$DialogsProcess apiWrap$DialogsProcess = this._dialogsProcess;
        apiWrap$DialogsProcess.done.run(apiWrap$DialogsProcess.info);
    }

    private void requestLeftChannelsList(Utilities.CallbackReturn<Integer, Boolean> callbackReturn, Utilities.Callback<ApiWrap$DialogsInfo> callback) {
        ApiWrap$LeftChannelsProcess apiWrap$LeftChannelsProcess = this._leftChannelsProcess;
        apiWrap$LeftChannelsProcess.progress = callbackReturn;
        apiWrap$LeftChannelsProcess.done = callback;
        requestLeftChannelsSlice();
    }

    private void requestLeftChannelsSlice() {
        requestLeftChannelsSliceGeneric(new Runnable() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda50
            @Override // java.lang.Runnable
            public final void run() {
                ExportRequestsController.this.lambda$requestLeftChannelsSlice$40();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestLeftChannelsSlice$40() {
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
        mainRequest(tL_users_getFullUser, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda10
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.lambda$requestPersonalInfo$44(callback, (TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: renamed from: $r8$lambda$K6Z8Dxak9YlqZ-3WIvUBpQOpswQ, reason: not valid java name */
    public static void lambda$requestPersonalInfo$44(Utilities.Callback callback, TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_users_userFull) {
            TLRPC.TL_users_userFull tL_users_userFull = (TLRPC.TL_users_userFull) tLObject;
            if (!tL_users_userFull.users.isEmpty()) {
                callback.run(DataTypesUtils.ParsePersonalInfo(tL_users_userFull));
            } else {
                throw new IllegalArgumentException("got 0 users in requestPersonalInfo!");
            }
        }
    }

    private int mainRequest(TLObject tLObject, final Utilities.Callback2<TLObject, TLRPC.TL_error> callback2) {
        ExportRequests$InvokeWithTakeoutWrapper exportRequests$InvokeWithTakeoutWrapper = new ExportRequests$InvokeWithTakeoutWrapper();
        exportRequests$InvokeWithTakeoutWrapper.query = tLObject;
        exportRequests$InvokeWithTakeoutWrapper.takeout_id = this._takeoutId;
        return ConnectionsManager.getInstance(this.selectedAcc).sendRequest(exportRequests$InvokeWithTakeoutWrapper, new RequestDelegate() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda7
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                ExportController.exportQueue.postRunnable(new Runnable() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda29
                    @Override // java.lang.Runnable
                    public final void run() {
                        callback2.run(tLObject2, tL_error);
                    }
                });
            }
        });
    }

    public int splitRequest(int i, TLObject tLObject, Utilities.Callback2<TLObject, TLRPC.TL_error> callback2) {
        ExportRequests$InvokeWithMessagesRange exportRequests$InvokeWithMessagesRange = new ExportRequests$InvokeWithMessagesRange();
        exportRequests$InvokeWithMessagesRange.query = tLObject;
        if (i < 0) {
            exportRequests$InvokeWithMessagesRange.range = new TLRPC.TL_messageRange();
        } else {
            exportRequests$InvokeWithMessagesRange.range = this.splits.get(i);
        }
        return mainRequest(exportRequests$InvokeWithMessagesRange, callback2);
    }

    private int fileRequest(ApiWrap$FileLocation apiWrap$FileLocation, final long j, final Utilities.Callback2<TLObject, TLRPC.TL_error> callback2) {
        TLRPC.TL_upload_getFile tL_upload_getFile = new TLRPC.TL_upload_getFile();
        tL_upload_getFile.location = apiWrap$FileLocation.data;
        tL_upload_getFile.offset = j;
        tL_upload_getFile.limit = 1048576;
        tL_upload_getFile.cdn_supported = false;
        tL_upload_getFile.precise = false;
        tL_upload_getFile.flags = 0;
        ExportRequests$InvokeWithTakeoutWrapper exportRequests$InvokeWithTakeoutWrapper = new ExportRequests$InvokeWithTakeoutWrapper();
        exportRequests$InvokeWithTakeoutWrapper.query = tL_upload_getFile;
        exportRequests$InvokeWithTakeoutWrapper.takeout_id = this._takeoutId;
        return ConnectionsManager.getInstance(this.selectedAcc).sendRequestSync(exportRequests$InvokeWithTakeoutWrapper, new RequestDelegate() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda43
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                ExportRequestsController.this.lambda$fileRequest$44(j, callback2, tLObject, tL_error);
            }
        }, null, null, 0, apiWrap$FileLocation.dcId, 65538, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fileRequest$44(long j, Utilities.Callback2 callback2, TLObject tLObject, TLRPC.TL_error tL_error) {
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
                sb.append(" error: ");
                sb.append(tL_error.text);
                throw new IllegalStateException(sb.toString());
            }
        }
        callback2.run(tLObject, tL_error);
    }

    public void requestSplitRanges() {
        mainRequest(new ExportRequests$getSplitRanges(), new Utilities.Callback2() {
            @Override
            public void run(Object obj, Object obj2) {
                ExportRequestsController.this.lambda$requestSplitRanges$45((TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestSplitRanges$45(TLObject tLObject, TLRPC.TL_error tL_error) {
        vectorToRanges(tLObject, this.splits);
        this.index = useOnlyLastSplit() ? this.splits.size() - 1 : 0;
        sendNextStartRequest();
    }

    public void requestDialogsCount() {
        if (this._settings.onlySinglePeer()) {
            this._startProcess.info.dialogsCount = 1;
            sendNextStartRequest();
        } else {
            TLRPC.TL_messages_getDialogs tL_messages_getDialogs = new TLRPC.TL_messages_getDialogs();
            tL_messages_getDialogs.offset_peer = new TLRPC.TL_inputPeerEmpty();
            tL_messages_getDialogs.limit = 1;
            splitRequest(this.index, tL_messages_getDialogs, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda27
                @Override // org.telegram.messenger.Utilities.Callback2
                public final void run(Object obj, Object obj2) {
                    ExportRequestsController.this.lambda$requestDialogsCount$46((TLObject) obj, (TLRPC.TL_error) obj2);
                }
            });
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$requestDialogsCount$46(TLObject tLObject, TLRPC.TL_error tL_error) {
        int size;
        if (tLObject instanceof TLRPC.TL_messages_dialogs) {
            size = ((TLRPC.TL_messages_dialogs) tLObject).dialogs.size();
        } else {
            size = tLObject instanceof TLRPC.TL_messages_dialogsSlice ? ((TLRPC.TL_messages_dialogsSlice) tLObject).count : -1;
        }
        if (size < 0) {
            throw new IllegalStateException("unexpected TL_messages_dialogsNotModified received");
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
        this._otherDataProcess.file.location.data = new TLRPC.InputFileLocation() { // from class: com.exteragram.messenger.export.api.ExportRequests$TL_inputTakeoutFileLocation
            @Override // org.telegram.tgnet.TLObject
            public void serializeToStream(OutputSerializedData outputSerializedData) {
                outputSerializedData.writeInt32(700340377);
            }
        };
        loadFile(this._otherDataProcess.file, new ApiWrap$FileOrigin(), new Utilities.CallbackReturn() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda8
            @Override // org.telegram.messenger.Utilities.CallbackReturn
            public final Object run(Object obj) {
                return Boolean.TRUE;
            }
        }, new Utilities.Callback() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda9
            @Override // org.telegram.messenger.Utilities.Callback
            public final void run(Object obj) {
                ExportRequestsController.this.otherDataDone((String) obj);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void otherDataDone(String str) {
        this._otherDataProcess.file.relativePath = str;
        if (str.isEmpty()) {
            this._otherDataProcess.file.skipReason = ApiWrap$File.SkipReason.Unavailable;
        }
        ApiWrap$OtherDataProcess apiWrap$OtherDataProcess = this._otherDataProcess;
        apiWrap$OtherDataProcess.done.run(apiWrap$OtherDataProcess.file);
    }

    public boolean useOnlyLastSplit() {
        return (this._settings.types & 226) == 0;
    }

    public void invokeFinish(boolean z, final Runnable runnable) {
        ExportRequests$FinishTakeoutSession exportRequests$FinishTakeoutSession = new ExportRequests$FinishTakeoutSession();
        exportRequests$FinishTakeoutSession.success = !z;
        mainRequest(exportRequests$FinishTakeoutSession, new Utilities.Callback2() { // from class: com.exteragram.messenger.export.controllers.ExportRequestsController$$ExternalSyntheticLambda4
            @Override // org.telegram.messenger.Utilities.Callback2
            public final void run(Object obj, Object obj2) {
                ExportRequestsController.lambda$invokeFinish$51(runnable, (TLObject) obj, (TLRPC.TL_error) obj2);
            }
        });
    }

    /* JADX INFO: renamed from: $r8$lambda$K5E2oN2LAwJDAki5Obw5-XGaixo, reason: not valid java name */
    public static void lambda$invokeFinish$51(Runnable runnable, TLObject tLObject, TLRPC.TL_error tL_error) {
        runnable.run();
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            Log.w("exteraGram", "finished successfully!!!");
            return;
        }
        Log.e("exteraGram", "failed: " + tL_error);
    }
}
