package com.exteragram.messenger.export.ui;

import android.content.Intent;
import android.net.Uri;
import android.provider.DocumentsContract;
import android.view.View;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.export.ExportSettings;
import com.exteragram.messenger.export.controllers.ExportController;
import com.exteragram.messenger.export.output.AbstractWriter;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.exteragram.messenger.preferences.SwitchGroup;
import com.exteragram.messenger.utils.ui.PopupUtils;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import java.util.ArrayList;
import java.util.Locale;
import java.util.function.BooleanSupplier;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;

public class ExportActivity extends BasePreferencesActivity implements NotificationCenter.NotificationCenterDelegate {
    private static final CharSequence[] formats = {"HTML", "JSON", "HTML and JSON"};
    private final TLRPC.InputPeer peer;
    private final ExportSettings settings = new ExportSettings();
    private final SwitchGroup exportSettings = SwitchGroup.of(this, ExportItem.EXPORT_SETTINGS.getId(), "Main settings").add(ExportItem.ACCOUNT_INFO.getId(), "Account Info", type(1), setType(1)).add(ExportItem.CONTACTS_LIST.getId(), "Contacts", type(4), setType(4)).add(ExportItem.STORY_ARCHIVE.getId(), "Stories", type(2048), setType(2048)).add(ExportItem.ACTIVE_SESSIONS.getId(), "Sessions", type(8), setType(8));
    private final SwitchGroup chatsSettings = SwitchGroup.of(this, ExportItem.CHATS_SETTINGS.getId(), "Chats settings").add(ExportItem.PERSONAL_CHATS.getId(), "Personal chats", type(32), setType(32)).add(ExportItem.BOT_CHATS.getId(), "Bots", type(64), setType(64)).add(ExportItem.PRIVATE_GROUPS.getId(), "Private groups", type(128), setType(128)).add(ExportItem.PRIVATE_CHANNELS.getId(), "Private channels", type(512), setType(512)).add(ExportItem.PUBLIC_GROUPS.getId(), "Public groups", type(256), setType(256)).add(ExportItem.PUBLIC_CHANNELS.getId(), "Public channels", type(1024), setType(1024));
    private final SwitchGroup mediaSettings = SwitchGroup.of(this, ExportItem.MEDIA_SETTINGS.getId(), "Media settings").add(ExportItem.PHOTOS.getId(), "Photos", media(1), setMedia(1)).add(ExportItem.VIDEOS.getId(), "Videos", media(2), setMedia(2)).add(ExportItem.VOICE_MESSAGES.getId(), "Voice messages", media(4), setMedia(4)).add(ExportItem.VIDEO_MESSAGES.getId(), "Video messages", media(8), setMedia(8)).add(ExportItem.STICKERS.getId(), "Stickers", media(16), setMedia(16)).add(ExportItem.GIFS.getId(), "GIFs", media(32), setMedia(32)).add(ExportItem.FILES.getId(), "Files", media(64), setMedia(64));

    public enum ExportItem {
        HEADER,
        EXPORT_SETTINGS,
        ACCOUNT_INFO,
        CONTACTS_LIST,
        STORY_ARCHIVE,
        ACTIVE_SESSIONS,
        CHATS_SETTINGS,
        PERSONAL_CHATS,
        BOT_CHATS,
        PRIVATE_GROUPS,
        PRIVATE_CHANNELS,
        PUBLIC_GROUPS,
        PUBLIC_CHANNELS,
        MEDIA_SETTINGS,
        PHOTOS,
        VIDEOS,
        VOICE_MESSAGES,
        VIDEO_MESSAGES,
        STICKERS,
        GIFS,
        FILES,
        FORMAT,
        START_EXPORT,
        VIEW_JSON_EXPORT;

        public int getId() {
            return ordinal() + 1;
        }
    }

    public ExportActivity(TLRPC.InputPeer inputPeer) {
        this.peer = inputPeer;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, ExportController.INITIALIZATING_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, ExportController.DIALOGS_LIST_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, ExportController.PERSONAL_INFO_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, ExportController.USERPICS_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, ExportController.STORIES_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, ExportController.CONTACTS_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, ExportController.SESSIONS_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, ExportController.OTHER_DATA_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, ExportController.DIALOGS_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, ExportController.FINISH_NOTIFICATION);
        return super.onFragmentCreate();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, ExportController.INITIALIZATING_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, ExportController.DIALOGS_LIST_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, ExportController.PERSONAL_INFO_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, ExportController.USERPICS_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, ExportController.STORIES_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, ExportController.CONTACTS_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, ExportController.SESSIONS_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, ExportController.OTHER_DATA_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, ExportController.DIALOGS_NOTIFICATION);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, ExportController.FINISH_NOTIFICATION);
        super.onFragmentDestroy();
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        String str;
        if (i == ExportController.FINISH_NOTIFICATION) {
            BulletinFactory.of(this).createSimpleBulletin(R.raw.contact_check, "Export complete!").show();
            return;
        }
        ExportController.ProcessingState processingState = (ExportController.ProcessingState) objArr[0];
        float f = processingState.substepsPassed / processingState.substepsTotal;
        if (processingState.bytesCount > 0) {
            str = String.format(Locale.US, "Downloading %s\n%s / %s", processingState.bytesName, AndroidUtilities.formatFileSize(processingState.bytesLoaded), AndroidUtilities.formatFileSize(processingState.bytesCount));
        } else if (processingState.entityCount > 0) {
            str = String.format(Locale.US, "Exporting %s (%d / %d)\nMessage %d / %d", processingState.entityName, Integer.valueOf(processingState.entityIndex + 1), Integer.valueOf(processingState.entityCount), Integer.valueOf(processingState.itemIndex), Integer.valueOf(processingState.itemCount));
        } else {
            str = "Exporting " + processingState.step.name();
        }
        FileLog.e("[EXPORT] " + str + ", " + f);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public String getTitle() {
        return "Export Chats";
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asHeader("Export settings"));
        this.exportSettings.fill(arrayList);
        this.chatsSettings.fill(arrayList);
        this.mediaSettings.fill(arrayList);
        arrayList.add(UItem.asShadow());
        arrayList.add(UItem.asButton(ExportItem.FORMAT.getId(), "Select export result type", formats[getIndexOfFormat()]));
        arrayList.add(UItem.asButton(ExportItem.START_EXPORT.getId(), "Start Export"));
        arrayList.add(UItem.asButton(ExportItem.VIEW_JSON_EXPORT.getId(), "Open Json Export"));
        arrayList.add(UItem.asShadow("Here you can export your chats."));
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        ExportActivity exportActivity;
        int i2 = uItem.id;
        if (i2 <= 0 || i2 > ExportItem.values().length) {
            return;
        }
        switch (AnonymousClass1.$SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.values()[uItem.id - 1].ordinal()]) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                exportActivity = this;
                exportActivity.exportSettings.onClick(uItem);
                break;
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            case 12:
                exportActivity = this;
                exportActivity.chatsSettings.onClick(uItem);
                break;
            case 13:
            case 14:
            case 15:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
                exportActivity = this;
                exportActivity.mediaSettings.onClick(uItem);
                break;
            case 21:
                exportActivity = this;
                exportActivity.showListDialog(uItem, formats, "Select export result type", getIndexOfFormat(), new PopupUtils.OnItemClickListener() { // from class: com.exteragram.messenger.export.ui.ExportActivity$$ExternalSyntheticLambda0
                    @Override // com.exteragram.messenger.utils.ui.PopupUtils.OnItemClickListener
                    public final void onClick(int i3) {
                        ExportActivity.this.lambda$onClick$0(i3);
                    }
                });
                break;
            case 22:
                TLRPC.InputPeer inputPeer = this.peer;
                if (inputPeer != null) {
                    this.settings.singlePeer = inputPeer;
                }
                BulletinFactory.of(this).createErrorBulletin("Starting export...").show();
                this.settings.media.sizeLimit = 2097152000L;
                ExportController.getInstance(UserConfig.selectedAccount).startExport(this.settings);
                exportActivity = this;
                break;
            case 23:
                Intent intent = new Intent("android.intent.action.OPEN_DOCUMENT_TREE");
                intent.addCategory("android.intent.category.DEFAULT");
                startActivityForResult(Intent.createChooser(intent, "Choose a directory"), 1337);
                exportActivity = this;
                break;
            default:
                exportActivity = this;
                break;
        }
        exportActivity.listView.adapter.update(true);
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.export.ui.ExportActivity$1, reason: invalid class name */
    public static /* synthetic */ class AnonymousClass1 {
        static final /* synthetic */ int[] $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem;

        static {
            int[] iArr = new int[ExportItem.values().length];
            $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem = iArr;
            try {
                iArr[ExportItem.EXPORT_SETTINGS.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.ACCOUNT_INFO.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.CONTACTS_LIST.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.STORY_ARCHIVE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.ACTIVE_SESSIONS.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.CHATS_SETTINGS.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.PERSONAL_CHATS.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.BOT_CHATS.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.PRIVATE_GROUPS.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.PRIVATE_CHANNELS.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.PUBLIC_GROUPS.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.PUBLIC_CHANNELS.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.MEDIA_SETTINGS.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.PHOTOS.ordinal()] = 14;
            } catch (NoSuchFieldError unused14) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.VIDEOS.ordinal()] = 15;
            } catch (NoSuchFieldError unused15) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.VOICE_MESSAGES.ordinal()] = 16;
            } catch (NoSuchFieldError unused16) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.VIDEO_MESSAGES.ordinal()] = 17;
            } catch (NoSuchFieldError unused17) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.STICKERS.ordinal()] = 18;
            } catch (NoSuchFieldError unused18) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.GIFS.ordinal()] = 19;
            } catch (NoSuchFieldError unused19) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.FILES.ordinal()] = 20;
            } catch (NoSuchFieldError unused20) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.FORMAT.ordinal()] = 21;
            } catch (NoSuchFieldError unused21) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.START_EXPORT.ordinal()] = 22;
            } catch (NoSuchFieldError unused22) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$export$ui$ExportActivity$ExportItem[ExportItem.VIEW_JSON_EXPORT.ordinal()] = 23;
            } catch (NoSuchFieldError unused23) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onClick$0(int i) {
        if (i == 0) {
            this.settings.format = AbstractWriter.Format.Html;
            return;
        }
        ExportSettings exportSettings = this.settings;
        if (i == 1) {
            exportSettings.format = AbstractWriter.Format.Json;
        } else {
            exportSettings.format = AbstractWriter.Format.HtmlAndJson;
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onActivityResultFragment(int i, int i2, Intent intent) {
        super.onActivityResultFragment(i, i2, intent);
        if (i == 1337 && i2 == -1) {
            try {
                Uri data = intent.getData();
                presentFragment(new DialogsView(AndroidPickerUtils.getPath(getParentActivity(), DocumentsContract.buildDocumentUriUsingTree(data, DocumentsContract.getTreeDocumentId(data)))));
            } catch (Exception e) {
                if (ExteraConfig.getUseGoogleCrashlytics()) {
                    FirebaseCrashlytics.getInstance().recordException(e);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$type$1(int i) {
        return (this.settings.types & i) != 0;
    }

    private BooleanSupplier type(final int i) {
        return new BooleanSupplier() { // from class: com.exteragram.messenger.export.ui.ExportActivity$$ExternalSyntheticLambda2
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return ExportActivity.this.lambda$type$1(i);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setType$2(int i, boolean z) {
        int i2;
        ExportSettings exportSettings = this.settings;
        if (z) {
            i2 = i | exportSettings.types;
        } else {
            i2 = (~i) & exportSettings.types;
        }
        exportSettings.types = i2;
    }

    private SwitchGroup.Setter setType(final int i) {
        return new SwitchGroup.Setter() { // from class: com.exteragram.messenger.export.ui.ExportActivity$$ExternalSyntheticLambda1
            @Override // com.exteragram.messenger.preferences.SwitchGroup.Setter
            public final void set(boolean z) {
                ExportActivity.this.lambda$setType$2(i, z);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean lambda$media$3(int i) {
        return (this.settings.media.type & i) != 0;
    }

    private BooleanSupplier media(final int i) {
        return new BooleanSupplier() { // from class: com.exteragram.messenger.export.ui.ExportActivity$$ExternalSyntheticLambda3
            @Override // java.util.function.BooleanSupplier
            public final boolean getAsBoolean() {
                return ExportActivity.this.lambda$media$3(i);
            }
        };
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$setMedia$4(int i, boolean z) {
        int i2;
        ExportSettings.MediaSettings mediaSettings = this.settings.media;
        if (z) {
            i2 = i | mediaSettings.type;
        } else {
            i2 = (~i) & mediaSettings.type;
        }
        mediaSettings.type = i2;
    }

    private SwitchGroup.Setter setMedia(final int i) {
        return new SwitchGroup.Setter() { // from class: com.exteragram.messenger.export.ui.ExportActivity$$ExternalSyntheticLambda4
            @Override // com.exteragram.messenger.preferences.SwitchGroup.Setter
            public final void set(boolean z) {
                ExportActivity.this.lambda$setMedia$4(i, z);
            }
        };
    }

    private int getIndexOfFormat() {
        AbstractWriter.Format format = this.settings.format;
        if (format == AbstractWriter.Format.Json) {
            return 1;
        }
        return format == AbstractWriter.Format.HtmlAndJson ? 2 : 0;
    }
}
