package com.exteragram.messenger.export.controllers;

import android.os.Environment;
import android.util.Log;
import com.exteragram.messenger.export.ExportSettings;
import com.exteragram.messenger.export.api.ApiWrap$ContactsList;
import com.exteragram.messenger.export.api.ApiWrap$DialogInfo;
import com.exteragram.messenger.export.api.ApiWrap$DialogsInfo;
import com.exteragram.messenger.export.api.ApiWrap$DownloadProgress;
import com.exteragram.messenger.export.api.ApiWrap$ExportPersonalInfo;
import com.exteragram.messenger.export.api.ApiWrap$File;
import com.exteragram.messenger.export.api.ApiWrap$MessagesSlice;
import com.exteragram.messenger.export.api.ApiWrap$SessionsList;
import com.exteragram.messenger.export.api.ApiWrap$StoriesSlice;
import com.exteragram.messenger.export.api.ApiWrap$UserpicsInfo;
import com.exteragram.messenger.export.output.AbstractWriter;
import com.exteragram.messenger.export.output.OutputFile;
import com.google.android.gms.cast.CastStatusCodes;
import j$.time.Instant;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.function.ToIntFunction;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BaseController;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.BulletinFactory;

public class ExportController extends BaseController {
    public static final int CONTACTS_NOTIFICATION;
    public static final int DIALOGS_NOTIFICATION;
    public static final int FINISH_NOTIFICATION;
    public static final int OTHER_DATA_NOTIFICATION;
    public static final int PERSONAL_INFO_NOTIFICATION;
    public static final int SESSIONS_NOTIFICATION;
    public static final int STORIES_NOTIFICATION;
    public static final int USERPICS_NOTIFICATION;
    private static int defaultId;
    private int _dialogIndex;
    private ApiWrap$DialogsInfo _dialogsInfo;
    private ProcessingState.Step _lastProcessingStep;
    private int _messagesCount;
    private int _messagesWritten;
    private ExportSettings _settings;
    private ProcessingState _state;
    private final OutputFile.Stats _stats;
    private int _stepIndex;
    private final ArrayList<ProcessingState.Step> _steps;
    private int _storiesCount;
    private int _storiesWritten;
    private int[] _substepsInStep;
    private int _substepsPassed;
    private int _substepsTotal;
    private int _userpicsCount;
    private int _userpicsWritten;
    private AbstractWriter _writer;
    private final int currAcc;
    public static volatile DispatchQueue exportQueue = new DispatchQueue("exportQueue");
    private static final ExportController[] Instance = new ExportController[16];
    public static final int INITIALIZATING_NOTIFICATION = 6666;
    public static final int DIALOGS_LIST_NOTIFICATION = 6666;

    public static class ProcessingState {
        public String bytesName;
        public String entityName;
        public Step step = Step.Initializing;
        public int substepsPassed = 0;
        public int substepsNow = 0;
        public int substepsTotal = 0;
        public EntityType entityType = EntityType.Other;
        public int entityIndex = 0;
        public int entityCount = 0;
        public int itemIndex = 0;
        public int itemCount = 0;
        public long bytesRandomId = 0;
        public long bytesLoaded = 0;
        public long bytesCount = 0;

        public enum EntityType {
            Chat,
            SavedMessages,
            RepliesMessages,
            VerifyCodes,
            Other
        }

        public enum Step {
            Initializing,
            DialogsList,
            PersonalInfo,
            Userpics,
            Stories,
            Contacts,
            Sessions,
            OtherData,
            Dialogs
        }
    }

    public static Boolean lambda$collectDialogsList$1(Integer num) {
        if (num.intValue() > 0) {
            setState(stateDialogsList(num.intValue() - 1));
        }
        return Boolean.TRUE;
    }

    public void lambda$exportPersonalInfo$3(ApiWrap$ExportPersonalInfo apiWrap$ExportPersonalInfo) {
        if (this._writer.writePersonal(apiWrap$ExportPersonalInfo).isSuccess()) {
            exportNext();
        }
    }

    private ProcessingState statePersonalInfo() {
        return prepareState(ProcessingState.Step.PersonalInfo, new Utilities.Callback() { 
            @Override 
            public final void run(Object obj) {
                ExportController.m1055$r8$lambda$XpHL9OJ9E0jsaOwD7m1Z3whHP0((ExportController.ProcessingState) obj);
            }
        });
    }

    private void exportUserpics() {
        ExportRequestsController.getInstance(this.currAcc).requestUserpics(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return this.f$0.lambda$exportUserpics$5((ApiWrap$UserpicsInfo) obj);
            }
        }, new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return this.f$0.lambda$exportUserpics$6((ApiWrap$DownloadProgress) obj);
            }
        }, new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return this.f$0.lambda$exportUserpics$7((ArrayList) obj);
            }
        }, new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$exportUserpics$8();
            }
        });
    }

    public void lambda$stateUserpics$9(ApiWrap$DownloadProgress apiWrap$DownloadProgress, ProcessingState processingState) {
        int iItemIndex = this._userpicsWritten + apiWrap$DownloadProgress.itemIndex();
        processingState.entityIndex = iItemIndex;
        processingState.entityCount = Math.max(this._userpicsCount, iItemIndex);
        processingState.bytesRandomId = apiWrap$DownloadProgress.randomId();
        if (!apiWrap$DownloadProgress.path().isEmpty()) {
            processingState.bytesName = apiWrap$DownloadProgress.path().substring(apiWrap$DownloadProgress.path().lastIndexOf(47) + 1);
        }
        processingState.bytesLoaded = apiWrap$DownloadProgress.ready();
        processingState.bytesCount = apiWrap$DownloadProgress.total();
    }

    private void exportStories() {
        ExportRequestsController.getInstance(this.currAcc).requestStories(new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return this.f$0.lambda$exportStories$10((Integer) obj);
            }
        }, new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return this.f$0.lambda$exportStories$11((ApiWrap$DownloadProgress) obj);
            }
        }, new Utilities.CallbackReturn() { 
            @Override 
            public final Object run(Object obj) {
                return this.f$0.lambda$exportStories$12((ApiWrap$StoriesSlice) obj);
            }
        }, new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$exportStories$13();
            }
        });
    }

    public void lambda$stateStories$14(ApiWrap$DownloadProgress apiWrap$DownloadProgress, ProcessingState processingState) {
        int iItemIndex = this._storiesWritten + apiWrap$DownloadProgress.itemIndex();
        processingState.entityIndex = iItemIndex;
        processingState.entityCount = Math.max(this._storiesCount, iItemIndex);
        processingState.bytesRandomId = apiWrap$DownloadProgress.randomId();
        if (!apiWrap$DownloadProgress.path().isEmpty()) {
            processingState.bytesName = apiWrap$DownloadProgress.path().substring(apiWrap$DownloadProgress.path().lastIndexOf(47) + 1);
        }
        processingState.bytesLoaded = apiWrap$DownloadProgress.ready();
        processingState.bytesCount = apiWrap$DownloadProgress.total();
    }

    private void exportContacts() {
        setState(prepareState(ProcessingState.Step.Contacts, null));
        ExportRequestsController.getInstance(this.currAcc).requestContacts(new Utilities.Callback() { 
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$exportContacts$15((ApiWrap$ContactsList) obj);
            }
        });
    }

    public void lambda$stateDialogsList$16(int i, ProcessingState processingState) {
        processingState.entityIndex = i;
        processingState.entityCount = Math.max(i, substepsInStep(ProcessingState.Step.Dialogs));
    }

    private ProcessingState prepareState(ProcessingState.Step step, Utilities.Callback<ProcessingState> callback) {
        ProcessingState.Step step2 = this._lastProcessingStep;
        if (step != step2) {
            this._substepsPassed += substepsInStep(step2);
            this._lastProcessingStep = step;
        }
        ProcessingState processingState = new ProcessingState();
        if (callback != null) {
            callback.run(processingState);
        }
        processingState.step = step;
        processingState.substepsPassed = this._substepsPassed;
        processingState.substepsNow = substepsInStep(this._lastProcessingStep);
        processingState.substepsTotal = this._substepsTotal;
        return processingState;
    }

    private int substepsInStep(ProcessingState.Step step) {
        return this._substepsInStep[step.ordinal()];
    }

    private void fillSubstepsInSteps(ExportRequestsController.StartInfo startInfo) {
        int[] iArr = new int[ProcessingState.Step.values().length];
        iArr[ProcessingState.Step.Initializing.ordinal()] = 1;
        if ((this._settings.types & CastStatusCodes.DEVICE_CONNECTION_SUSPENDED) != 0) {
            iArr[ProcessingState.Step.DialogsList.ordinal()] = 1;
        }
        if ((this._settings.types & 32) != 0) {
            iArr[ProcessingState.Step.PersonalInfo.ordinal()] = 1;
        }
        if ((this._settings.types & 2) != 0) {
            iArr[ProcessingState.Step.Userpics.ordinal()] = 1;
        }
        if ((this._settings.types & 2048) != 0) {
            iArr[ProcessingState.Step.Stories.ordinal()] = 1;
        }
        if ((this._settings.types & 4) != 0) {
            iArr[ProcessingState.Step.Contacts.ordinal()] = 1;
        }
        if ((this._settings.types & 8) != 0) {
            iArr[ProcessingState.Step.Sessions.ordinal()] = 1;
        }
        if ((this._settings.types & 16) != 0) {
            iArr[ProcessingState.Step.OtherData.ordinal()] = 1;
        }
        if ((this._settings.types & CastStatusCodes.DEVICE_CONNECTION_SUSPENDED) != 0) {
            iArr[ProcessingState.Step.Dialogs.ordinal()] = startInfo.dialogsCount;
        }
        this._substepsInStep = iArr;
        this._substepsTotal = Arrays.stream(iArr).sum();
    }

    private void setState(final ProcessingState processingState) {
        final int i;
        if (stopped()) {
            return;
        }
        if (processingState instanceof FinishedState) {
            i = FINISH_NOTIFICATION;
        } else {
            switch (AnonymousClass1.$SwitchMap$com$exteragram$messenger$export$controllers$ExportController$ProcessingState$Step[processingState.step.ordinal()]) {
                case 1:
                    i = INITIALIZATING_NOTIFICATION;
                    break;
                case 2:
                    i = DIALOGS_LIST_NOTIFICATION;
                    break;
                case 3:
                    i = PERSONAL_INFO_NOTIFICATION;
                    break;
                case 4:
                    i = USERPICS_NOTIFICATION;
                    break;
                case 5:
                    i = STORIES_NOTIFICATION;
                    break;
                case 6:
                    i = CONTACTS_NOTIFICATION;
                    break;
                case 7:
                    i = SESSIONS_NOTIFICATION;
                    break;
                case 8:
                    i = OTHER_DATA_NOTIFICATION;
                    break;
                case 9:
                    i = DIALOGS_NOTIFICATION;
                    break;
                default:
                    throw new IncompatibleClassChangeError();
            }
        }
        if (i != -1) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setState$17(i, processingState);
                }
            });
        }
        this._state = processingState;
    }

    public void lambda$exportSessions$18(ApiWrap$SessionsList apiWrap$SessionsList) {
        if (this._writer.writeSessionsList(apiWrap$SessionsList).isSuccess()) {
            exportNext();
        }
    }

    private void exportOtherData() {
        setState(prepareState(ProcessingState.Step.OtherData, null));
        ExportRequestsController.getInstance(this.currAcc).requestOtherData("lists/other_data.json", new Utilities.Callback() { 
            @Override 
            public final void run(Object obj) {
                this.f$0.lambda$exportOtherData$19((ApiWrap$File) obj);
            }
        });
    }

    public Boolean lambda$exportNextDialog$21(ApiWrap$DialogInfo apiWrap$DialogInfo, ApiWrap$DialogInfo apiWrap$DialogInfo2) {
        if (!this._writer.writeDialogStart(apiWrap$DialogInfo).isSuccess()) {
            return Boolean.FALSE;
        }
        this._messagesWritten = 0;
        this._messagesCount = apiWrap$DialogInfo2.messagesCountPerSplit.stream().mapToInt(new ToIntFunction() { 
            @Override // java.util.function.ToIntFunction
            public final int applyAsInt(Object obj) {
                return ((Integer) obj).intValue();
            }
        }).sum();
        setState(stateDialogs(new ApiWrap$DownloadProgress()));
        return Boolean.TRUE;
    }

    public void lambda$stateDialogs$25(ApiWrap$DownloadProgress apiWrap$DownloadProgress, ProcessingState processingState) {
        fillMessagesState(processingState, this._dialogsInfo, this._dialogIndex, apiWrap$DownloadProgress);
    }

    public static class FinishedState extends ProcessingState {
        public long bytesCount;
        public int filesCount;
        public String path;

        public FinishedState(String str, int i, long j) {
            this.path = str;
            this.filesCount = i;
            this.bytesCount = j;
        }
    }

    private static void showErrorBulletin(String str) {
        BulletinFactory.global().createErrorBulletin(str);
    }

    public static void showError(TLRPC.TL_error tL_error) {
        String string;
        if (tL_error.text.contains("TAKEOUT_INVALID")) {
            showErrorBulletin(LocaleController.getString(R.string.ExportInvalid));
            return;
        }
        boolean zStartsWith = tL_error.text.startsWith("TAKEOUT_INIT_DELAY_");
        String str = tL_error.text;
        if (zStartsWith) {
            Integer num = Utilities.parseInt((CharSequence) str.substring(str.lastIndexOf("_")));
            Instant instantNow = Instant.now();
            instantNow.plusSeconds(num.intValue());
            if (num.intValue() / 3600 <= 0) {
                string = LocaleController.getString(R.string.ExportDelayLessThanHour);
            } else {
                string = LocaleController.getString(R.string.Hours_other);
            }
            showErrorBulletin(LocaleController.formatString(R.string.ExportDelay, string, instantNow.toString()));
            return;
        }
        showErrorBulletin("API error happened! Error text: " + str);
    }
}
