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

    @Override 
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

    @Override 
    public String getTitle() {
        return "Export Chats";
    }

    @Override 
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

    class AnonymousClass1 {
        static final void lambda$setType$2(int i, boolean z) {
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
        return new SwitchGroup.Setter() { 
            @Override 
            public final void set(boolean z) {
                this.f$0.lambda$setType$2(i, z);
            }
        };
    }

    public void lambda$setMedia$4(int i, boolean z) {
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
        return new SwitchGroup.Setter() { 
            @Override 
            public final void set(boolean z) {
                this.f$0.lambda$setMedia$4(i, z);
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
