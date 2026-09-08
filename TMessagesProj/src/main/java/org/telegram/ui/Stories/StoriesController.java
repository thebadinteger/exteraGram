package org.telegram.ui.Stories;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.SparseArray;
import android.webkit.MimeTypeMap;
import androidx.collection.LongSparseArray;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.exoplayer2.util.Consumer;
import com.google.android.gms.cast.MediaError;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.ToIntFunction;
import okhttp3.internal.url._UrlKt;
import org.telegram.SQLite.SQLiteCursor;
import org.telegram.SQLite.SQLiteDatabase;
import org.telegram.SQLite.SQLitePreparedStatement;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChannelBoostsController;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.FileRefController;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.Timer;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.support.LongSparseIntArray;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.NativeByteBuffer;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.tgnet.tl.TL_bots;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.tgnet.tl.TL_update;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.Reactions.ReactionImageHolder;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.StatisticActivity;
import org.telegram.ui.Stories.bots.BotPreviewsEditContainer;
import org.telegram.ui.Stories.recorder.DraftsController;
import org.telegram.ui.Stories.recorder.StoryEntry;
import org.telegram.ui.Stories.recorder.StoryPrivacyBottomSheet;
import org.telegram.ui.Stories.recorder.StoryRecorder;
import org.telegram.ui.Stories.recorder.StoryUploadingService;

public class StoriesController {
    public static final Comparator<TL_stories.StoryItem> storiesComparator = Comparator.comparingInt(new ToIntFunction() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda3
        @Override // java.util.function.ToIntFunction
        public final int applyAsInt(Object obj) {
            return ((TL_stories.StoryItem) obj).date;
        }
    });
    boolean allHiddenStoriesLoaded;
    boolean allStoriesLoaded;
    private int blocklistCount;
    private int blocklistReqId;
    private final int currentAccount;
    private final DraftsController draftsController;
    boolean hasMore;
    private boolean loadedSendAs;
    boolean loadingFromDatabase;
    private boolean loadingFromServer;
    private boolean loadingFromServerHidden;
    private boolean loadingSendAs;
    SharedPreferences mainSettings;
    private final HashSet<String> requestingUnsupportedStories;
    public final ArrayList<TLRPC.InputPeer> sendAs;
    final Runnable sortStoriesRunnable;
    String state;
    private String stateHidden;
    private TL_stories.TL_storiesStealthMode stealthMode;
    private boolean storiesReadLoaded;
    StoriesStorage storiesStorage;
    private StoryLimit storyLimitCached;
    private boolean storyLimitFetched;
    private int totalStoriesCount;
    private int totalStoriesCountHidden;
    private final HashSet<String> unsupportedStoriesChecked;
    private final LongSparseArray<ArrayList<UploadingStory>> uploadingStoriesByDialogId = new LongSparseArray<>();
    private final LongSparseArray<ArrayList<UploadingStory>> uploadingAndEditingStories = new LongSparseArray<>();
    public int uploadedStories = 0;
    private final LongSparseArray<HashMap<Integer, UploadingStory>> editingStories = new LongSparseArray<>();
    public LongSparseIntArray dialogIdToMaxReadId = new LongSparseIntArray();
    private ArrayList<TL_stories.PeerStories> dialogListStories = new ArrayList<>();
    private ArrayList<TL_stories.PeerStories> hiddenListStories = new ArrayList<>();
    private LongSparseArray<TL_stories.PeerStories> allStoriesMap = new LongSparseArray<>();
    private LongSparseIntArray loadingDialogsStories = new LongSparseIntArray();
    final LongSparseArray<ViewsForPeerStoriesRequester> pollingViewsForSelfStoriesRequester = new LongSparseArray<>();
    public LongSparseArray<SparseArray<SelfStoryViewsPage.ViewsModel>> selfViewsModel = new LongSparseArray<>();
    private boolean hasMoreHidden = true;
    private boolean firstLoad = true;
    HashSet<Long> allStoriesLoading = new HashSet<>();
    HashSet<Long> loadingAllStories = new HashSet<>();
    LongSparseArray<TL_stories.StoryItem> resolvedStories = new LongSparseArray<>();
    private final LongSparseArray<StoriesCollections> storiesCollections = new LongSparseArray<>();
    private final HashMap<Long, StoriesList>[] storiesLists = new HashMap[5];
    private final HashMap<Long, HashMap<Integer, StoriesList>> storiesAlbumsLists = new HashMap<>();
    public final ArrayList<SearchStoriesList> attachedSearchLists = new ArrayList<>();
    private final Comparator<TL_stories.PeerStories> peerStoriesComparator = new Comparator() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda0
        @Override // java.util.Comparator
        public final int compare(Object obj, Object obj2) {
            return this.f$0.lambda$new$23((TL_stories.PeerStories) obj, (TL_stories.PeerStories) obj2);
        }
    };
    public HashSet<Long> blocklist = new HashSet<>();
    private LongSparseArray<Boolean> blockedOverride = new LongSparseArray<>();
    public boolean blocklistFull = false;
    private boolean blocklistLoadingReset = false;
    private boolean blocklistLoading = false;
    private long lastBlocklistRequested = 0;

    public static void lambda$processUpdate$10(long j, TL_stories.TL_updateStory tL_updateStory, TLRPC.User user) {
        boolean z;
        boolean z2;
        boolean z3;
        FileLog.d("StoriesController update stories for dialog " + j);
        updateStoriesInLists(j, Collections.singletonList(tL_updateStory.story));
        boolean z4 = true;
        updateStoriesForFullPeer(j, Collections.singletonList(tL_updateStory.story), true);
        TL_stories.PeerStories peerStories = this.allStoriesMap.get(j);
        ArrayList arrayList = new ArrayList();
        int i = this.totalStoriesCount;
        TL_stories.StoryItem storyItemApplyStoryUpdate = tL_updateStory.story;
        if (peerStories != null) {
            if (storyItemApplyStoryUpdate instanceof TL_stories.TL_storyItemDeleted) {
                NotificationsController.getInstance(this.currentAccount).processDeleteStory(j, storyItemApplyStoryUpdate.id);
            }
            int i2 = 0;
            while (true) {
                if (i2 >= peerStories.stories.size()) {
                    z = false;
                } else if (peerStories.stories.get(i2).id == storyItemApplyStoryUpdate.id) {
                    boolean z5 = storyItemApplyStoryUpdate instanceof TL_stories.TL_storyItemDeleted;
                    ArrayList<TL_stories.StoryItem> arrayList2 = peerStories.stories;
                    if (z5) {
                        arrayList2.remove(i2);
                        FileLog.d("StoriesController remove story id=" + storyItemApplyStoryUpdate.id);
                        z = true;
                    } else {
                        TL_stories.StoryItem storyItem = arrayList2.get(i2);
                        storyItemApplyStoryUpdate = applyStoryUpdate(storyItem, storyItemApplyStoryUpdate);
                        arrayList.add(storyItemApplyStoryUpdate);
                        peerStories.stories.set(i2, storyItemApplyStoryUpdate);
                        if (storyItemApplyStoryUpdate.attachPath == null) {
                            storyItemApplyStoryUpdate.attachPath = storyItem.attachPath;
                        }
                        if (storyItemApplyStoryUpdate.firstFramePath == null) {
                            storyItemApplyStoryUpdate.firstFramePath = storyItem.firstFramePath;
                        }
                        FileLog.d("StoriesController update story id=" + storyItemApplyStoryUpdate.id);
                        z = true;
                        z2 = false;
                        break;
                    }
                } else {
                    i2++;
                }
                z2 = z;
                break;
            }
            if (z) {
                z3 = false;
            } else {
                if (storyItemApplyStoryUpdate instanceof TL_stories.TL_storyItemDeleted) {
                    FileLog.d("StoriesController can't add new story DELETED");
                    return;
                }
                if (StoriesUtilities.isExpired(this.currentAccount, storyItemApplyStoryUpdate)) {
                    FileLog.d("StoriesController can't add new story isExpired");
                    return;
                }
                if (j > 0 && (user == null || (!user.self && !isContactOrService(user)))) {
                    FileLog.d("StoriesController can't add new story user is not contact");
                    return;
                }
                arrayList.add(storyItemApplyStoryUpdate);
                peerStories.stories.add(storyItemApplyStoryUpdate);
                FileLog.d("StoriesController add new story id=" + storyItemApplyStoryUpdate.id + " total stories count " + peerStories.stories.size());
                preloadStory(j, storyItemApplyStoryUpdate);
                applyToList(peerStories);
                z3 = true;
                z2 = true;
            }
            if (!z2) {
                z4 = z3;
            } else if (peerStories.stories.isEmpty() && !hasUploadingStories(j)) {
                this.dialogListStories.remove(peerStories);
                this.hiddenListStories.remove(peerStories);
                this.allStoriesMap.remove(DialogObject.getPeerDialogId(peerStories.peer));
                this.totalStoriesCount--;
            } else {
                Collections.sort(peerStories.stories, storiesComparator);
            }
        } else {
            if (storyItemApplyStoryUpdate instanceof TL_stories.TL_storyItemDeleted) {
                FileLog.d("StoriesController can't add user " + j + " with new story DELETED");
                return;
            }
            if (StoriesUtilities.isExpired(this.currentAccount, storyItemApplyStoryUpdate)) {
                FileLog.d("StoriesController can't add user " + j + " with new story isExpired");
                return;
            }
            if (j > 0 && (user == null || (!user.self && !isContactOrService(user)))) {
                FileLog.d("StoriesController can't add user cause is not contact");
                return;
            }
            TL_stories.TL_peerStories tL_peerStories = new TL_stories.TL_peerStories();
            tL_peerStories.peer = tL_updateStory.peer;
            tL_peerStories.stories.add(tL_updateStory.story);
            FileLog.d("StoriesController add new user with story id=" + tL_updateStory.story.id);
            applyNewStories(tL_peerStories);
            this.totalStoriesCount = this.totalStoriesCount + 1;
            loadAllStoriesForDialog(j);
        }
        if (i != this.totalStoriesCount) {
            this.mainSettings.edit().putInt("total_stores", this.totalStoriesCount).apply();
        }
        fixDeletedAndNonContactsStories(this.dialogListStories);
        fixDeletedAndNonContactsStories(this.hiddenListStories);
        if (z4) {
            if (tL_updateStory.story instanceof TL_stories.TL_storyItemDeleted) {
                NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storyDeleted, Long.valueOf(j), Integer.valueOf(tL_updateStory.story.id));
            }
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesUpdated, new Object[0]);
        }
        MessagesController.getInstance(this.currentAccount).checkArchiveFolder();
    }

    private void updateStoriesForFullPeer(long j, List<TL_stories.StoryItem> list, boolean z) {
        TL_stories.PeerStories peerStories;
        boolean z2;
        int i = this.currentAccount;
        if (j > 0) {
            TLRPC.UserFull userFull = MessagesController.getInstance(i).getUserFull(j);
            if (userFull == null) {
                return;
            }
            if (userFull.stories == null) {
                if (!z) {
                    return;
                }
                TL_stories.TL_peerStories tL_peerStories = new TL_stories.TL_peerStories();
                userFull.stories = tL_peerStories;
                tL_peerStories.peer = MessagesController.getInstance(this.currentAccount).getPeer(j);
                userFull.stories.max_read_id = getMaxStoriesReadId(j);
            }
            peerStories = userFull.stories;
        } else {
            TLRPC.ChatFull chatFull = MessagesController.getInstance(i).getChatFull(-j);
            if (chatFull == null) {
                return;
            }
            if (chatFull.stories == null) {
                if (!z) {
                    return;
                }
                TL_stories.TL_peerStories tL_peerStories2 = new TL_stories.TL_peerStories();
                chatFull.stories = tL_peerStories2;
                tL_peerStories2.peer = MessagesController.getInstance(this.currentAccount).getPeer(j);
                chatFull.stories.max_read_id = getMaxStoriesReadId(j);
            }
            peerStories = chatFull.stories;
        }
        for (int i2 = 0; i2 < list.size(); i2++) {
            TL_stories.StoryItem storyItemApplyStoryUpdate = list.get(i2);
            int i3 = 0;
            while (true) {
                if (i3 >= peerStories.stories.size()) {
                    z2 = false;
                    break;
                }
                if (peerStories.stories.get(i3).id == storyItemApplyStoryUpdate.id) {
                    boolean z3 = storyItemApplyStoryUpdate instanceof TL_stories.TL_storyItemDeleted;
                    ArrayList<TL_stories.StoryItem> arrayList = peerStories.stories;
                    z2 = true;
                    if (z3) {
                        arrayList.remove(i3);
                        break;
                    }
                    TL_stories.StoryItem storyItem = arrayList.get(i3);
                    storyItemApplyStoryUpdate = applyStoryUpdate(storyItem, storyItemApplyStoryUpdate);
                    peerStories.stories.set(i3, storyItemApplyStoryUpdate);
                    if (storyItemApplyStoryUpdate.attachPath == null) {
                        storyItemApplyStoryUpdate.attachPath = storyItem.attachPath;
                    }
                    if (storyItemApplyStoryUpdate.firstFramePath == null) {
                        storyItemApplyStoryUpdate.firstFramePath = storyItem.firstFramePath;
                    }
                    FileLog.d("StoriesController update story for full peer storyId=" + storyItemApplyStoryUpdate.id);
                    break;
                }
                i3++;
            }
            if (!z2) {
                if (storyItemApplyStoryUpdate instanceof TL_stories.TL_storyItemDeleted) {
                    FileLog.d("StoriesController story is not found, but already deleted storyId=" + storyItemApplyStoryUpdate.id);
                } else if (z) {
                    FileLog.d("StoriesController add new story for full peer storyId=" + storyItemApplyStoryUpdate.id);
                    peerStories.stories.add(storyItemApplyStoryUpdate);
                    peerStories.checkedExpired = false;
                }
            }
        }
    }

    private boolean isContactOrService(TLRPC.User user) {
        if (user != null) {
            return user.contact || user.id == MessagesController.getInstance(this.currentAccount).storiesChangelogUserId;
        }
        return false;
    }

    private void applyToList(TL_stories.PeerStories peerStories) {
        TLRPC.Chat chat;
        boolean z;
        boolean z2;
        long peerDialogId = DialogObject.getPeerDialogId(peerStories.peer);
        int i = this.currentAccount;
        TLRPC.User user = null;
        if (peerDialogId > 0) {
            TLRPC.User user2 = MessagesController.getInstance(i).getUser(Long.valueOf(peerDialogId));
            if (user2 == null) {
                FileLog.d("StoriesController can't apply story user == null");
                return;
            } else {
                user = user2;
                chat = null;
            }
        } else {
            chat = MessagesController.getInstance(i).getChat(Long.valueOf(-peerDialogId));
            if (chat == null) {
                FileLog.d("StoriesController can't apply story chat == null");
                return;
            }
        }
        int i2 = 0;
        while (true) {
            z = true;
            if (i2 >= this.dialogListStories.size()) {
                z2 = false;
                break;
            } else {
                if (DialogObject.getPeerDialogId(this.dialogListStories.get(i2).peer) == peerDialogId) {
                    this.dialogListStories.remove(i2);
                    z2 = true;
                    break;
                }
                i2++;
            }
        }
        for (int i3 = 0; i3 < this.hiddenListStories.size(); i3++) {
            if (DialogObject.getPeerDialogId(this.hiddenListStories.get(i3).peer) == peerDialogId) {
                this.hiddenListStories.remove(i3);
                z2 = true;
                break;
            }
        }
        if ((user == null || !user.stories_hidden) && (chat == null || !chat.stories_hidden)) {
            z = false;
        }
        if (BuildVars.LOGS_ENABLED) {
            FileLog.d("StoriesController move user stories to first hidden=" + z + " did=" + peerDialogId);
        }
        if (z) {
            this.hiddenListStories.add(0, peerStories);
        } else {
            this.dialogListStories.add(0, peerStories);
        }
        if (!z2) {
            loadAllStoriesForDialog(peerDialogId);
        }
        MessagesController.getInstance(this.currentAccount).checkArchiveFolder();
    }

    private void loadAllStoriesForDialog(final long j) {
        if (this.allStoriesLoading.contains(Long.valueOf(j))) {
            return;
        }
        this.allStoriesLoading.add(Long.valueOf(j));
        FileLog.d("StoriesController loadAllStoriesForDialog " + j);
        TL_stories.TL_stories_getPeerStories tL_stories_getPeerStories = new TL_stories.TL_stories_getPeerStories();
        tL_stories_getPeerStories.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_stories_getPeerStories, new RequestDelegate() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda14
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$loadAllStoriesForDialog$12(j, tLObject, tL_error);
            }
        });
    }

    public void lambda$start$0(VideoEditedInfo videoEditedInfo) {
            this.info = videoEditedInfo;
            this.messageObject.videoEditedInfo = videoEditedInfo;
            this.duration = videoEditedInfo.estimatedDuration / 1000;
            if (videoEditedInfo.needConvert()) {
                MediaController.getInstance().scheduleVideoConvert(this.messageObject, false, false, false);
            } else if (new File(this.messageObject.videoEditedInfo.originalPath).renameTo(new File(this.path))) {
                FileLoader.getInstance(StoriesController.this.currentAccount).uploadFile(this.path, false, false, 33554432);
            }
        }

        public private void sendUploadedRequest(TLRPC.InputFile inputFile) {
            TLRPC.InputMedia inputMedia;
            boolean z;
            TLObject tLObject;
            CharSequence charSequence;
            CharSequence charSequence2;
            TLRPC.TL_inputMediaUploadedPhoto tL_inputMediaUploadedPhoto;
            String lowerCase;
            List<TLRPC.InputDocument> list;
            List<TLRPC.InputDocument> list2;
            TLRPC.MessageMedia messageMedia;
            TLRPC.InputMedia inputMedia2;
            if (this.canceled) {
                return;
            }
            StoryEntry storyEntry = this.entry;
            if (storyEntry.shareUserIds != null) {
                return;
            }
            int i = 0;
            if (!storyEntry.isRepost || storyEntry.editedMedia || (messageMedia = storyEntry.repostMedia) == null) {
                inputMedia = null;
                z = false;
            } else {
                if (messageMedia instanceof TLRPC.TL_messageMediaDocument) {
                    TLRPC.TL_inputMediaDocument tL_inputMediaDocument = new TLRPC.TL_inputMediaDocument();
                    TLRPC.TL_inputDocument tL_inputDocument = new TLRPC.TL_inputDocument();
                    TLRPC.MessageMedia messageMedia2 = this.entry.repostMedia;
                    TLRPC.Document document = messageMedia2.document;
                    tL_inputDocument.id = document.id;
                    tL_inputDocument.access_hash = document.access_hash;
                    tL_inputDocument.file_reference = document.file_reference;
                    tL_inputMediaDocument.id = tL_inputDocument;
                    tL_inputMediaDocument.spoiler = messageMedia2.spoiler;
                    inputMedia2 = tL_inputMediaDocument;
                } else if (messageMedia instanceof TLRPC.TL_messageMediaPhoto) {
                    TLRPC.TL_inputMediaPhoto tL_inputMediaPhoto = new TLRPC.TL_inputMediaPhoto();
                    TLRPC.TL_inputPhoto tL_inputPhoto = new TLRPC.TL_inputPhoto();
                    TLRPC.Photo photo = this.entry.repostMedia.photo;
                    tL_inputPhoto.id = photo.id;
                    tL_inputPhoto.access_hash = photo.access_hash;
                    tL_inputPhoto.file_reference = photo.file_reference;
                    tL_inputMediaPhoto.id = tL_inputPhoto;
                    inputMedia2 = tL_inputMediaPhoto;
                } else {
                    inputMedia = null;
                    z = false;
                }
                z = true;
                inputMedia = inputMedia2;
            }
            TLRPC.InputMedia inputMedia3 = inputMedia;
            inputMedia3 = inputMedia;
            if (inputMedia == null && inputFile != null) {
                if (this.entry.wouldBeVideo()) {
                    TLRPC.TL_inputMediaUploadedDocument tL_inputMediaUploadedDocument = new TLRPC.TL_inputMediaUploadedDocument();
                    tL_inputMediaUploadedDocument.file = inputFile;
                    TLRPC.TL_documentAttributeVideo tL_documentAttributeVideo = new TLRPC.TL_documentAttributeVideo();
                    if (this.entry.editingCoverDocument != null) {
                        for (int i2 = 0; i2 < this.entry.editingCoverDocument.attributes.size(); i2++) {
                            if (this.entry.editingCoverDocument.attributes.get(i2) instanceof TLRPC.TL_documentAttributeVideo) {
                                tL_documentAttributeVideo = (TLRPC.TL_documentAttributeVideo) this.entry.editingCoverDocument.attributes.get(i2);
                                break;
                            }
                        }
                    } else {
                        SendMessagesHelper.fillVideoAttribute(this.path, tL_documentAttributeVideo, null);
                    }
                    tL_inputMediaUploadedDocument.attributes.add(tL_documentAttributeVideo);
                    tL_documentAttributeVideo.supports_streaming = true;
                    int i3 = tL_documentAttributeVideo.flags;
                    tL_documentAttributeVideo.flags = i3 | 4;
                    tL_documentAttributeVideo.preload_prefix_size = (int) this.firstSecondSize;
                    StoryEntry storyEntry2 = this.entry;
                    long j = storyEntry2.cover;
                    if (j >= 0) {
                        tL_documentAttributeVideo.flags = i3 | 20;
                        tL_documentAttributeVideo.video_start_ts = ((double) (j - (storyEntry2.left * storyEntry2.duration))) / 1000.0d;
                    }
                    List<TLRPC.InputDocument> list3 = storyEntry2.stickers;
                    if (list3 != null && (!list3.isEmpty() || ((list2 = this.entry.editStickers) != null && !list2.isEmpty()))) {
                        tL_inputMediaUploadedDocument.flags |= 1;
                        ArrayList<TLRPC.InputDocument> arrayList = new ArrayList<>(this.entry.stickers);
                        tL_inputMediaUploadedDocument.stickers = arrayList;
                        List<TLRPC.InputDocument> list4 = this.entry.editStickers;
                        if (list4 != null) {
                            arrayList.addAll(list4);
                        }
                        tL_inputMediaUploadedDocument.attributes.add(new TLRPC.TL_documentAttributeHasStickers());
                    }
                    StoryEntry storyEntry3 = this.entry;
                    tL_inputMediaUploadedDocument.nosound_video = storyEntry3.audioPath == null && (storyEntry3.muted || !storyEntry3.isVideo);
                    tL_inputMediaUploadedDocument.mime_type = "video/mp4";
                    inputMedia3 = tL_inputMediaUploadedDocument;
                } else {
                    tL_inputMediaUploadedPhoto = new TLRPC.TL_inputMediaUploadedPhoto();
                    tL_inputMediaUploadedPhoto.file = inputFile;
                    MimeTypeMap singleton = MimeTypeMap.getSingleton();
                    int iLastIndexOf = this.path.lastIndexOf(46);
                    if (iLastIndexOf == -1) {
                        lowerCase = "txt";
                    } else {
                        lowerCase = this.path.substring(iLastIndexOf + 1).toLowerCase();
                    }
                    tL_inputMediaUploadedPhoto.mime_type = singleton.getMimeTypeFromExtension(lowerCase);
                    List<TLRPC.InputDocument> list5 = this.entry.stickers;
                    inputMedia3 = tL_inputMediaUploadedPhoto;
                    if (list5 != null && (!list5.isEmpty() || ((list = this.entry.editStickers) != null && !list.isEmpty()))) {
                        inputMedia3 = tL_inputMediaUploadedPhoto;
                        inputMedia3 = tL_inputMediaUploadedPhoto;
                        tL_inputMediaUploadedPhoto.flags |= 1;
                        List<TLRPC.InputDocument> list6 = this.entry.editStickers;
                        if (list6 != null) {
                            tL_inputMediaUploadedPhoto.stickers.addAll(list6);
                        }
                        tL_inputMediaUploadedPhoto.stickers = new ArrayList<>(this.entry.stickers);
                        inputMedia3 = tL_inputMediaUploadedPhoto;
                    }
                }
            }
            inputMedia3 = tL_inputMediaUploadedPhoto;
            inputMedia3 = tL_inputMediaUploadedPhoto;
            inputMedia3 = tL_inputMediaUploadedPhoto;
            boolean zIsPremium = UserConfig.getInstance(StoriesController.this.currentAccount).isPremium();
            StoriesController storiesController = StoriesController.this;
            int i4 = zIsPremium ? MessagesController.getInstance(storiesController.currentAccount).storyCaptionLengthLimitPremium : MessagesController.getInstance(storiesController.currentAccount).storyCaptionLengthLimitDefault;
            boolean z2 = this.edit;
            StoryEntry storyEntry4 = this.entry;
            if (z2) {
                if (storyEntry4.botId != 0) {
                    TL_bots.editPreviewMedia editpreviewmedia = new TL_bots.editPreviewMedia();
                    editpreviewmedia.bot = MessagesController.getInstance(StoriesController.this.currentAccount).getInputUser(this.entry.botId);
                    StoryEntry storyEntry5 = this.entry;
                    editpreviewmedia.media = storyEntry5.editingBotPreview;
                    editpreviewmedia.new_media = inputMedia3;
                    editpreviewmedia.lang_code = storyEntry5.botLang;
                    tLObject = editpreviewmedia;
                } else {
                    TL_stories.TL_stories_editStory tL_stories_editStory = new TL_stories.TL_stories_editStory();
                    tL_stories_editStory.id = this.entry.editStoryId;
                    tL_stories_editStory.peer = MessagesController.getInstance(StoriesController.this.currentAccount).getInputPeer(this.dialogId);
                    tL_stories_editStory.flags |= 16;
                    TLRPC.InputDocument inputDocument = this.entry.audioDocument;
                    if (inputDocument != null) {
                        tL_stories_editStory.music = inputDocument;
                    } else {
                        tL_stories_editStory.music = new TLRPC.TL_inputDocumentEmpty();
                    }
                    if (inputMedia3 != null && this.entry.editedMedia) {
                        tL_stories_editStory.flags |= 1;
                        tL_stories_editStory.media = inputMedia3;
                    }
                    StoryEntry storyEntry6 = this.entry;
                    if (storyEntry6.editedCaption && (charSequence2 = storyEntry6.caption) != null) {
                        tL_stories_editStory.flags |= 2;
                        CharSequence[] charSequenceArr = {charSequence2};
                        if (charSequence2.length() > i4) {
                            charSequenceArr[0] = charSequenceArr[0].subSequence(0, i4);
                        }
                        if (MessagesController.getInstance(StoriesController.this.currentAccount).storyEntitiesAllowed()) {
                            tL_stories_editStory.entities = MediaDataController.getInstance(StoriesController.this.currentAccount).getEntities(charSequenceArr, true);
                        } else {
                            tL_stories_editStory.entities.clear();
                        }
                        if (charSequenceArr[0].length() > i4) {
                            charSequenceArr[0] = charSequenceArr[0].subSequence(0, i4);
                        }
                        tL_stories_editStory.caption = charSequenceArr[0].toString();
                    }
                    StoryEntry storyEntry7 = this.entry;
                    if (storyEntry7.editedPrivacy) {
                        tL_stories_editStory.flags |= 4;
                        tL_stories_editStory.privacy_rules.addAll(storyEntry7.privacyRules);
                    }
                    ArrayList<TL_stories.MediaArea> arrayList2 = this.entry.editedMediaAreas;
                    if (arrayList2 != null) {
                        tL_stories_editStory.media_areas.addAll(arrayList2);
                    }
                    if (this.entry.mediaEntities != null) {
                        while (i < this.entry.mediaEntities.size()) {
                            TL_stories.MediaArea mediaArea = this.entry.mediaEntities.get(i).mediaArea;
                            if (mediaArea != null) {
                                tL_stories_editStory.media_areas.add(mediaArea);
                            }
                            i++;
                        }
                    }
                    if (!tL_stories_editStory.media_areas.isEmpty()) {
                        tL_stories_editStory.flags |= 8;
                    }
                    tLObject = tL_stories_editStory;
                }
            } else if (storyEntry4.botId != 0) {
                TL_bots.addPreviewMedia addpreviewmedia = new TL_bots.addPreviewMedia();
                addpreviewmedia.bot = MessagesController.getInstance(StoriesController.this.currentAccount).getInputUser(this.entry.botId);
                addpreviewmedia.media = inputMedia3;
                addpreviewmedia.lang_code = this.entry.botLang;
                tLObject = addpreviewmedia;
            } else {
                TL_stories.TL_stories_sendStory tL_stories_sendStory = new TL_stories.TL_stories_sendStory();
                tL_stories_sendStory.random_id = this.random_id;
                tL_stories_sendStory.peer = MessagesController.getInstance(StoriesController.this.currentAccount).getInputPeer(this.dialogId);
                tL_stories_sendStory.media = inputMedia3;
                tL_stories_sendStory.privacy_rules.addAll(this.entry.privacyRules);
                StoryEntry storyEntry8 = this.entry;
                tL_stories_sendStory.pinned = storyEntry8.pinned;
                tL_stories_sendStory.noforwards = !storyEntry8.allowScreenshots;
                tL_stories_sendStory.albums = storyEntry8.albums != null ? new ArrayList<>(this.entry.albums) : null;
                StoryEntry storyEntry9 = this.entry;
                TLRPC.InputDocument inputDocument2 = storyEntry9.audioDocument;
                if (inputDocument2 != null) {
                    tL_stories_sendStory.flags |= 512;
                    tL_stories_sendStory.music = inputDocument2;
                }
                CharSequence charSequence3 = storyEntry9.caption;
                if (charSequence3 != null) {
                    tL_stories_sendStory.flags |= 3;
                    CharSequence[] charSequenceArr2 = {charSequence3};
                    if (charSequence3.length() > i4) {
                        charSequenceArr2[0] = charSequenceArr2[0].subSequence(0, i4);
                    }
                    if (MessagesController.getInstance(StoriesController.this.currentAccount).storyEntitiesAllowed()) {
                        tL_stories_sendStory.entities = MediaDataController.getInstance(StoriesController.this.currentAccount).getEntities(charSequenceArr2, true);
                    } else {
                        tL_stories_sendStory.entities.clear();
                    }
                    if (charSequenceArr2[0].length() > i4) {
                        charSequenceArr2[0] = charSequenceArr2[0].subSequence(0, i4);
                    }
                    tL_stories_sendStory.caption = charSequenceArr2[0].toString();
                }
                if (this.entry.isRepost) {
                    tL_stories_sendStory.flags |= 64;
                    tL_stories_sendStory.fwd_from_id = MessagesController.getInstance(StoriesController.this.currentAccount).getInputPeer(this.entry.repostPeer);
                    tL_stories_sendStory.fwd_from_story = this.entry.repostStoryId;
                    tL_stories_sendStory.fwd_modified = !z;
                }
                StoryEntry storyEntry10 = this.entry;
                int i5 = storyEntry10.period;
                if (i5 == Integer.MAX_VALUE) {
                    tL_stories_sendStory.pinned = true;
                } else {
                    tL_stories_sendStory.flags |= 8;
                    tL_stories_sendStory.period = i5;
                }
                if (storyEntry10.mediaEntities != null) {
                    while (i < this.entry.mediaEntities.size()) {
                        TL_stories.MediaArea mediaArea2 = this.entry.mediaEntities.get(i).mediaArea;
                        if (mediaArea2 != null) {
                            tL_stories_sendStory.media_areas.add(mediaArea2);
                        }
                        i++;
                    }
                    if (!tL_stories_sendStory.media_areas.isEmpty()) {
                        tL_stories_sendStory.flags |= 32;
                    }
                }
                tLObject = tL_stories_sendStory;
            }
            RequestDelegate requestDelegate = new RequestDelegate() { // from class: org.telegram.ui.Stories.StoriesController$UploadingStory$$ExternalSyntheticLambda0
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject2, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$sendUploadedRequest$8(tLObject2, tL_error);
                }
            };
            if (BuildVars.DEBUG_PRIVATE_VERSION && !this.edit && (charSequence = this.entry.caption) != null && charSequence.toString().contains("#failtest") && !this.hadFailed) {
                TLRPC.TL_error tL_error = new TLRPC.TL_error();
                tL_error.code = MediaError.DetailedErrorCode.MANIFEST_UNKNOWN;
                tL_error.text = "FORCED_TO_FAIL";
                requestDelegate.run(null, tL_error);
                return;
            }
            this.currentRequest = ConnectionsManager.getInstance(StoriesController.this.currentAccount).sendRequest(tLObject, requestDelegate, 64);
        }

        public void lambda$sendUploadedRequest$3(TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new StoriesController$$ExternalSyntheticLambda6(StoriesController.this));
        }

        public void lambda$loadInternal$1(final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Stories.StoriesController$BotPreviewsList$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$loadInternal$0(tLObject, runnable);
                }
            });
        }

        public void lambda$requestReference$2(BotPreview botPreview, Utilities.Callback callback) {
            TL_stories.StoryItem storyItem;
            TLRPC.MessageMedia messageMedia;
            TLRPC.Photo photo;
            TLRPC.Photo photo2;
            for (int i = 0; i < this.messageObjects.size(); i++) {
                MessageObject messageObject = this.messageObjects.get(i);
                if (messageObject != null && (storyItem = messageObject.storyItem) != null && (messageMedia = storyItem.media) != null) {
                    TLRPC.MessageMedia messageMedia2 = botPreview.media;
                    TLRPC.Document document = messageMedia2.document;
                    if (document != null) {
                        TLRPC.Document document2 = messageMedia.document;
                        if (document2 != null) {
                            if (document2.id == document.id) {
                                callback.run((BotPreview) storyItem);
                                return;
                            }
                            photo = messageMedia2.photo;
                            if (photo != null) {
                                continue;
                            }
                        } else {
                            continue;
                        }
                    } else {
                        photo = messageMedia2.photo;
                        if (photo != null && (photo2 = messageMedia.photo) != null && photo2.id == photo.id) {
                            callback.run((BotPreview) storyItem);
                            return;
                        }
                    }
                }
            }
            callback.run(null);
        }

        @Override // org.telegram.ui.Stories.StoriesController.StoriesList
        public int getCount() {
            return this.messageObjects.size();
        }

        @Override // org.telegram.ui.Stories.StoriesController.StoriesList
        public int getLoadedCount() {
            return this.messageObjects.size();
        }

        @Override // org.telegram.ui.Stories.StoriesController.StoriesList
        public boolean isLoading() {
            return this.loading;
        }

        @Override // org.telegram.ui.Stories.StoriesController.StoriesList
        public ArrayList<ArrayList<Integer>> getDays() {
            return this.fakeDays;
        }

        @Override // org.telegram.ui.Stories.StoriesController.StoriesList
        public MessageObject findMessageObject(int i) {
            for (int i2 = 0; i2 < this.messageObjects.size(); i2++) {
                if (this.messageObjects.get(i2).getId() == i) {
                    return this.messageObjects.get(i2);
                }
            }
            return null;
        }

        @Override // org.telegram.ui.Stories.StoriesController.StoriesList
        public void updatePinnedOrder(ArrayList<Integer> arrayList, boolean z) {
            ArrayList arrayList2 = new ArrayList();
            ArrayList arrayList3 = new ArrayList();
            TL_bots.reorderPreviewMedias reorderpreviewmedias = new TL_bots.reorderPreviewMedias();
            reorderpreviewmedias.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.dialogId);
            reorderpreviewmedias.lang_code = this.lang_code;
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                Integer num = arrayList.get(i);
                i++;
                Integer num2 = num;
                MessageObject messageObjectFindMessageObject = findMessageObject(num2.intValue());
                if (messageObjectFindMessageObject != null) {
                    reorderpreviewmedias.order.add(MessagesController.toInputMedia(messageObjectFindMessageObject.storyItem.media));
                    arrayList2.add(messageObjectFindMessageObject);
                    arrayList3.add(num2);
                }
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(reorderpreviewmedias, null);
            if (this.fakeDays.isEmpty()) {
                this.fakeDays.add(new ArrayList<>());
            }
            this.fakeDays.get(0).clear();
            this.fakeDays.get(0).addAll(arrayList3);
            this.messageObjects.clear();
            this.messageObjects.addAll(arrayList2);
        }

        public void delete(ArrayList<TLRPC.MessageMedia> arrayList) {
            if (arrayList == null) {
                return;
            }
            int i = 0;
            while (i < this.messageObjects.size()) {
                MessageObject messageObject = this.messageObjects.get(i);
                for (int i2 = 0; i2 < arrayList.size(); i2++) {
                    if (MessagesController.equals(messageObject.storyItem.media, arrayList.get(i2))) {
                        this.messageObjects.remove(i);
                        if (!this.fakeDays.isEmpty() && messageObject.getId() < this.fakeDays.get(0).size()) {
                            this.fakeDays.get(0).remove(messageObject.getId());
                        }
                        i--;
                        break;
                    }
                }
                i++;
            }
            TL_bots.deletePreviewMedia deletepreviewmedia = new TL_bots.deletePreviewMedia();
            deletepreviewmedia.bot = MessagesController.getInstance(this.currentAccount).getInputUser(this.dialogId);
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                deletepreviewmedia.media.add(MessagesController.toInputMedia(arrayList.get(i3)));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(deletepreviewmedia, null);
            AndroidUtilities.cancelRunOnUIThread(((StoriesList) this).notify);
            AndroidUtilities.runOnUIThread(((StoriesList) this).notify);
        }

        public void delete(TLRPC.MessageMedia messageMedia) {
            delete(new ArrayList<>(Arrays.asList(messageMedia)));
        }
    }

    public static class SearchStoriesList extends StoriesList {
        private int count;
        private final ArrayList<ArrayList<Integer>> fakeDays;
        private String last_offset;
        private boolean loading;
        public final String query;
        public final TL_stories.MediaArea queryArea;
        private int reqId;
        public final String username;

        @Override // org.telegram.ui.Stories.StoriesController.StoriesList
        public void invalidateCache() {
        }

        @Override // org.telegram.ui.Stories.StoriesController.StoriesList
        public boolean isOnlyCache() {
            return false;
        }

        @Override // org.telegram.ui.Stories.StoriesController.StoriesList
        public boolean markAsRead(int i) {
            return false;
        }

        @Override // org.telegram.ui.Stories.StoriesController.StoriesList
        public void preloadCache() {
        }

        @Override // org.telegram.ui.Stories.StoriesController.StoriesList
        public void saveCache() {
        }

        public SearchStoriesList(int i, String str, String str2) {
            super(i, 0L, 3, -1, null);
            this.fakeDays = new ArrayList<>();
            this.last_offset = _UrlKt.FRAGMENT_ENCODE_SET;
            this.query = str2;
            this.username = str;
            this.queryArea = null;
        }

        public SearchStoriesList(int i, TL_stories.MediaArea mediaArea) {
            super(i, 0L, 3, -1, null);
            this.fakeDays = new ArrayList<>();
            this.last_offset = _UrlKt.FRAGMENT_ENCODE_SET;
            this.query = null;
            this.username = null;
            this.queryArea = mediaArea;
        }

        public void cancel() {
            if (this.reqId != 0) {
                ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.reqId, true);
                this.reqId = 0;
            }
        }

        @Override // org.telegram.ui.Stories.StoriesController.StoriesList
        public boolean load(final boolean z, final int i, final List<Integer> list) {
            TLObject userOrChat;
            if (this.loading || this.last_offset == null) {
                return false;
            }
            TL_stories.TL_stories_searchPosts tL_stories_searchPosts = new TL_stories.TL_stories_searchPosts();
            tL_stories_searchPosts.offset = this.last_offset;
            tL_stories_searchPosts.limit = i;
            String str = this.query;
            if (str != null) {
                tL_stories_searchPosts.flags |= 1;
                tL_stories_searchPosts.hashtag = str;
            }
            TL_stories.MediaArea mediaArea = this.queryArea;
            if (mediaArea != null) {
                tL_stories_searchPosts.flags |= 2;
                tL_stories_searchPosts.area = mediaArea;
            }
            this.loading = true;
            if (TextUtils.isEmpty(this.username)) {
                userOrChat = null;
            } else {
                userOrChat = MessagesController.getInstance(this.currentAccount).getUserOrChat(this.username);
                if (userOrChat == null) {
                    MessagesController.getInstance(this.currentAccount).getUserNameResolver().resolve(this.username, new Consumer() { // from class: org.telegram.ui.Stories.StoriesController$SearchStoriesList$$ExternalSyntheticLambda0
                        @Override // com.google.android.exoplayer2.util.Consumer
                        public final void accept(Object obj) {
                            this.f$0.lambda$load$0(z, i, list, (Long) obj);
                        }
                    });
                    return true;
                }
            }
            if (userOrChat != null) {
                tL_stories_searchPosts.flags |= 4;
                tL_stories_searchPosts.peer = MessagesController.getInputPeer(userOrChat);
            }
            this.reqId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_stories_searchPosts, new RequestDelegate() { // from class: org.telegram.ui.Stories.StoriesController$SearchStoriesList$$ExternalSyntheticLambda1
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$load$2(tLObject, tL_error);
                }
            });
            return true;
        }

        public void $r8$lambda$I2mzpsXgJt70KibSxQrcG7HZlLs() {
        }

        public static void lambda$new$1(Utilities.Callback callback) {
            callback.run(this);
        }

        public void preloadCache() {
            if (this.preloading || this.loading || this.error) {
                return;
            }
            this.preloading = true;
            final MessagesStorage messagesStorage = MessagesStorage.getInstance(this.currentAccount);
            messagesStorage.getStorageQueue().postRunnable(new Runnable() { // from class: org.telegram.ui.Stories.StoriesController$StoriesList$$ExternalSyntheticLambda11
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$preloadCache$3(messagesStorage);
                }
            });
        }

        public void lambda$preloadCache$2(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, ArrayList arrayList4, HashSet hashSet) {
            FileLog.d("StoriesList " + this.type + "{" + this.dialogId + "} preloadCache {" + StoriesController.storyItemMessageIds(arrayList) + "}");
            this.pinnedIds.clear();
            this.pinnedIds.addAll(arrayList2);
            this.preloading = false;
            MessagesController.getInstance(this.currentAccount).putUsers(arrayList3, true);
            MessagesController.getInstance(this.currentAccount).putChats(arrayList4, true);
            if (this.invalidateAfterPreload) {
                this.invalidateAfterPreload = false;
                this.toLoad = null;
                invalidateCache();
                return;
            }
            this.seenStories.addAll(hashSet);
            this.cachedObjects.clear();
            for (int i = 0; i < arrayList.size(); i++) {
                pushObject((MessageObject) arrayList.get(i), true);
            }
            fill(false);
            Utilities.CallbackReturn<Integer, Boolean> callbackReturn = this.toLoad;
            if (callbackReturn != null) {
                callbackReturn.run(0);
                this.toLoad = null;
            }
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesListUpdated, this);
        }

        private void pushObject(MessageObject messageObject, boolean z) {
            if (messageObject == null) {
                return;
            }
            this.messageObjectsMap.put(Integer.valueOf(messageObject.getId()), messageObject);
            (z ? this.cachedObjects : this.loadedObjects).add(Integer.valueOf(messageObject.getId()));
            long jDay = day(messageObject);
            TreeSet<Integer> treeSet = this.groupedByDay.get(Long.valueOf(jDay));
            if (treeSet == null) {
                HashMap<Long, TreeSet<Integer>> map = this.groupedByDay;
                Long lValueOf = Long.valueOf(jDay);
                TreeSet<Integer> treeSet2 = new TreeSet<>((Comparator<? super Integer>) Comparator.reverseOrder());
                map.put(lValueOf, treeSet2);
                treeSet = treeSet2;
            }
            treeSet.add(Integer.valueOf(messageObject.getId()));
        }

        private boolean removeObject(int i, boolean z) {
            MessageObject messageObjectRemove = this.messageObjectsMap.remove(Integer.valueOf(i));
            if (z) {
                this.cachedObjects.remove(Integer.valueOf(i));
            }
            this.loadedObjects.remove(Integer.valueOf(i));
            this.pinnedIds.remove(Integer.valueOf(i));
            if (messageObjectRemove == null) {
                return false;
            }
            long jDay = day(messageObjectRemove);
            TreeSet<Integer> treeSet = this.groupedByDay.get(Long.valueOf(jDay));
            if (treeSet == null) {
                return true;
            }
            treeSet.remove(Integer.valueOf(i));
            if (!treeSet.isEmpty()) {
                return true;
            }
            this.groupedByDay.remove(Long.valueOf(jDay));
            return true;
        }

        public static long day(MessageObject messageObject) {
            if (messageObject == null) {
                return 0L;
            }
            long j = messageObject.messageOwner.date;
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(j * 1000);
            return (((long) calendar.get(1)) * 10000) + (((long) calendar.get(2)) * 100) + ((long) calendar.get(5));
        }

        public ArrayList<ArrayList<Integer>> getDays() {
            ArrayList arrayList = new ArrayList(this.groupedByDay.keySet());
            Collections.sort(arrayList, new Comparator() { // from class: org.telegram.ui.Stories.StoriesController$StoriesList$$ExternalSyntheticLambda3
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return StoriesController.StoriesList.m19745$r8$lambda$p509TyV4xK3Z7Fg5n4CU4UIas8((Long) obj, (Long) obj2);
                }
            });
            ArrayList<ArrayList<Integer>> arrayList2 = new ArrayList<>();
            int i = this.type;
            int i2 = 0;
            if (i == 0 && this.albumId > 0) {
                ArrayList<Integer> arrayList3 = new ArrayList<>();
                ArrayList<MessageObject> arrayList4 = this.messageObjects;
                int size = arrayList4.size();
                while (i2 < size) {
                    MessageObject messageObject = arrayList4.get(i2);
                    i2++;
                    arrayList3.add(Integer.valueOf(messageObject.storyItem.id));
                }
                arrayList2.add(arrayList3);
                return arrayList2;
            }
            if (i == 0 && !this.pinnedIds.isEmpty()) {
                arrayList2.add(new ArrayList<>(this.pinnedIds));
            }
            int size2 = arrayList.size();
            int i3 = 0;
            while (i3 < size2) {
                Object obj = arrayList.get(i3);
                i3++;
                TreeSet<Integer> treeSet = this.groupedByDay.get((Long) obj);
                if (treeSet != null) {
                    ArrayList<Integer> arrayList5 = new ArrayList<>(treeSet);
                    if (this.type == 0 && !this.pinnedIds.isEmpty()) {
                        ArrayList<Integer> arrayList6 = this.pinnedIds;
                        int size3 = arrayList6.size();
                        int i4 = 0;
                        while (i4 < size3) {
                            Integer num = arrayList6.get(i4);
                            i4++;
                            Integer num2 = num;
                            num2.intValue();
                            arrayList5.remove(num2);
                        }
                    }
                    if (!arrayList5.isEmpty()) {
                        arrayList2.add(arrayList5);
                    }
                }
            }
            return arrayList2;
        }

        public static Boolean lambda$load$10(boolean z, int i, List list, Integer num) {
            return Boolean.valueOf(load(z, i, list));
        }

        public int lambda$new$23(TL_stories.PeerStories peerStories, TL_stories.PeerStories peerStories2) {
        int i;
        long peerDialogId = DialogObject.getPeerDialogId(peerStories.peer);
        long peerDialogId2 = DialogObject.getPeerDialogId(peerStories2.peer);
        boolean zHasUploadingStories = hasUploadingStories(peerDialogId);
        boolean zHasUploadingStories2 = hasUploadingStories(peerDialogId2);
        boolean zHasUnreadStories = hasUnreadStories(peerDialogId);
        boolean zHasUnreadStories2 = hasUnreadStories(peerDialogId2);
        boolean zHasLiveStory = hasLiveStory(peerDialogId);
        boolean zHasLiveStory2 = hasLiveStory(peerDialogId2);
        if (zHasLiveStory != zHasLiveStory2) {
            return (zHasLiveStory2 ? 1 : 0) - (zHasLiveStory ? 1 : 0);
        }
        if (zHasUploadingStories != zHasUploadingStories2) {
            return (zHasUploadingStories2 ? 1 : 0) - (zHasUploadingStories ? 1 : 0);
        }
        if (zHasUnreadStories != zHasUnreadStories2) {
            return (zHasUnreadStories2 ? 1 : 0) - (zHasUnreadStories ? 1 : 0);
        }
        boolean zIsService = UserObject.isService(peerDialogId);
        boolean zIsService2 = UserObject.isService(peerDialogId2);
        if (zIsService != zIsService2) {
            return (zIsService2 ? 1 : 0) - (zIsService ? 1 : 0);
        }
        boolean zIsPremium = isPremium(peerDialogId);
        boolean zIsPremium2 = isPremium(peerDialogId2);
        if (zIsPremium != zIsPremium2) {
            return (zIsPremium2 ? 1 : 0) - (zIsPremium ? 1 : 0);
        }
        int i2 = 0;
        if (peerStories.stories.isEmpty()) {
            i = 0;
        } else {
            ArrayList<TL_stories.StoryItem> arrayList = peerStories.stories;
            i = arrayList.get(arrayList.size() - 1).date;
        }
        if (!peerStories2.stories.isEmpty()) {
            ArrayList<TL_stories.StoryItem> arrayList2 = peerStories2.stories;
            i2 = arrayList2.get(arrayList2.size() - 1).date;
        }
        return i2 - i;
    }

    private boolean isPremium(long j) {
        TLRPC.User user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j));
        if (user == null) {
            return false;
        }
        return user.premium;
    }

    public void scheduleSort() {
        AndroidUtilities.cancelRunOnUIThread(this.sortStoriesRunnable);
        this.sortStoriesRunnable.run();
    }

    public boolean hasOnlySelfStories() {
        return hasSelfStories() && (getDialogListStories().isEmpty() || (getDialogListStories().size() == 1 && DialogObject.getPeerDialogId(getDialogListStories().get(0).peer) == UserConfig.getInstance(this.currentAccount).clientUserId));
    }

    public void sortHiddenStories() {
        sortDialogStories(this.hiddenListStories);
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesUpdated, new Object[0]);
    }

    public void loadBlocklistAtFirst() {
        if (this.lastBlocklistRequested == 0) {
            loadBlocklist(false);
        }
    }

    public void loadBlocklist(boolean z) {
        if (this.blocklistLoading) {
            if (!z || this.blocklistLoadingReset) {
                return;
            }
            ConnectionsManager.getInstance(this.currentAccount).cancelRequest(this.blocklistReqId, true);
            this.blocklistReqId = 0;
            this.blocklistLoadingReset = false;
            this.blocklistLoading = false;
        }
        if (!z || System.currentTimeMillis() - this.lastBlocklistRequested >= 1800000) {
            if (z || !this.blocklistFull) {
                this.blocklistLoading = true;
                this.blocklistLoadingReset = z;
                TLRPC.TL_contacts_getBlocked tL_contacts_getBlocked = new TLRPC.TL_contacts_getBlocked();
                tL_contacts_getBlocked.my_stories_from = true;
                if (z) {
                    tL_contacts_getBlocked.offset = 0;
                    tL_contacts_getBlocked.limit = 100;
                    this.blocklistFull = false;
                } else {
                    tL_contacts_getBlocked.offset = this.blocklist.size();
                    tL_contacts_getBlocked.limit = 25;
                }
                ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_getBlocked, new RequestDelegate() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda20
                    @Override // org.telegram.tgnet.RequestDelegate
                    public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                        this.f$0.lambda$loadBlocklist$25(tLObject, tL_error);
                    }
                });
            }
        }
    }

    public /* synthetic */ void lambda$loadBlocklist$25(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda27
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadBlocklist$24(tLObject);
            }
        });
    }

    public /* synthetic */ void lambda$loadBlocklist$24(TLObject tLObject) {
        if (tLObject instanceof TLRPC.TL_contacts_blocked) {
            TLRPC.TL_contacts_blocked tL_contacts_blocked = (TLRPC.TL_contacts_blocked) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_contacts_blocked.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tL_contacts_blocked.chats, false);
            this.blocklist.clear();
            ArrayList<TLRPC.TL_peerBlocked> arrayList = tL_contacts_blocked.blocked;
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                TLRPC.TL_peerBlocked tL_peerBlocked = arrayList.get(i);
                i++;
                this.blocklist.add(Long.valueOf(DialogObject.getPeerDialogId(tL_peerBlocked.peer_id)));
            }
            this.blocklistCount = Math.max(this.blocklist.size(), tL_contacts_blocked.count);
            this.blocklistFull = true;
        } else {
            if (!(tLObject instanceof TLRPC.TL_contacts_blockedSlice)) {
                return;
            }
            TLRPC.TL_contacts_blockedSlice tL_contacts_blockedSlice = (TLRPC.TL_contacts_blockedSlice) tLObject;
            MessagesController.getInstance(this.currentAccount).putUsers(tL_contacts_blockedSlice.users, false);
            MessagesController.getInstance(this.currentAccount).putChats(tL_contacts_blockedSlice.chats, false);
            ArrayList<TLRPC.TL_peerBlocked> arrayList2 = tL_contacts_blockedSlice.blocked;
            int size2 = arrayList2.size();
            int i2 = 0;
            while (i2 < size2) {
                TLRPC.TL_peerBlocked tL_peerBlocked2 = arrayList2.get(i2);
                i2++;
                this.blocklist.add(Long.valueOf(DialogObject.getPeerDialogId(tL_peerBlocked2.peer_id)));
            }
            this.blocklistCount = tL_contacts_blockedSlice.count;
            this.blocklistFull = this.blocklist.size() >= this.blocklistCount;
        }
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesBlocklistUpdate, new Object[0]);
        this.blocklistLoading = false;
        this.lastBlocklistRequested = System.currentTimeMillis();
    }

    public int getBlocklistCount() {
        return this.blocklistCount;
    }

    public void updateBlockedUsers(HashSet<Long> hashSet, final Runnable runnable) {
        TLRPC.TL_contacts_setBlocked tL_contacts_setBlocked = new TLRPC.TL_contacts_setBlocked();
        tL_contacts_setBlocked.my_stories_from = true;
        tL_contacts_setBlocked.limit = this.blocklist.size();
        int size = this.blocklistCount - this.blocklist.size();
        this.blocklistCount = size;
        if (size < 0) {
            this.blocklistCount = 0;
        }
        this.blocklist.clear();
        for (Long l : hashSet) {
            TLRPC.InputPeer inputPeer = MessagesController.getInstance(this.currentAccount).getInputPeer(l.longValue());
            if (inputPeer != null && !(inputPeer instanceof TLRPC.TL_inputPeerEmpty)) {
                this.blocklist.add(l);
                tL_contacts_setBlocked.id.add(inputPeer);
            }
        }
        this.blocklistCount += this.blocklist.size();
        tL_contacts_setBlocked.limit = Math.max(tL_contacts_setBlocked.limit, this.blocklist.size());
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_contacts_setBlocked, new RequestDelegate() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda40
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda41
                    @Override // java.lang.Runnable
                    public final void run() {
                        StoriesController.m19721$r8$lambda$G2O6l9Hr4Go5O9xxhY9RL6YO_0(runnable);
                    }
                });
            }
        });
    }

    public static /* synthetic */ void m19721$r8$lambda$G2O6l9Hr4Go5O9xxhY9RL6YO_0(Runnable runnable) {
        if (runnable != null) {
            runnable.run();
        }
    }

    public boolean isBlocked(TL_stories.StoryView storyView) {
        if (storyView == null) {
            return false;
        }
        if (this.blockedOverride.containsKey(storyView.user_id)) {
            return this.blockedOverride.get(storyView.user_id).booleanValue();
        }
        if (this.lastBlocklistRequested == 0) {
            return storyView.blocked_my_stories_from || storyView.blocked;
        }
        return this.blocklist.contains(Long.valueOf(storyView.user_id)) || storyView.blocked_my_stories_from || storyView.blocked;
    }

    public void applyStoryViewsBlocked(TL_stories.StoryViewsList storyViewsList) {
        if (storyViewsList == null || storyViewsList.views == null) {
            return;
        }
        for (int i = 0; i < storyViewsList.views.size(); i++) {
            TL_stories.StoryView storyView = storyViewsList.views.get(i);
            if (this.blockedOverride.containsKey(storyView.user_id)) {
                this.blockedOverride.put(storyView.user_id, Boolean.valueOf(storyView.blocked_my_stories_from));
            }
        }
    }

    public void updateBlockUser(long j, boolean z) {
        updateBlockUser(j, z, true);
    }

    public void updateBlockUser(long j, boolean z, boolean z2) {
        TLObject tLObject;
        TLRPC.InputPeer inputPeer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
        if (inputPeer == null || (inputPeer instanceof TLRPC.TL_inputPeerEmpty)) {
            return;
        }
        this.blockedOverride.put(j, Boolean.valueOf(z));
        if (this.blocklist.contains(Long.valueOf(j)) != z) {
            HashSet<Long> hashSet = this.blocklist;
            if (z) {
                hashSet.add(Long.valueOf(j));
                this.blocklistCount++;
            } else {
                hashSet.remove(Long.valueOf(j));
                this.blocklistCount--;
            }
        }
        if (z2) {
            if (z) {
                TLRPC.TL_contacts_block tL_contacts_block = new TLRPC.TL_contacts_block();
                tL_contacts_block.my_stories_from = true;
                tL_contacts_block.id = inputPeer;
                tLObject = tL_contacts_block;
            } else {
                TLRPC.TL_contacts_unblock tL_contacts_unblock = new TLRPC.TL_contacts_unblock();
                tL_contacts_unblock.my_stories_from = true;
                tL_contacts_unblock.id = inputPeer;
                tLObject = tL_contacts_unblock;
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tLObject, null);
        }
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesBlocklistUpdate, new Object[0]);
    }

    public StoryLimit checkStoryLimit() {
        int i;
        boolean zIsPremium = UserConfig.getInstance(this.currentAccount).isPremium();
        int i2 = this.currentAccount;
        if (zIsPremium) {
            i = MessagesController.getInstance(i2).storyExpiringLimitPremium;
        } else {
            i = MessagesController.getInstance(i2).storyExpiringLimitDefault;
        }
        if (getMyStoriesCount() >= i) {
            return new StoryLimit(1, 0, 0L);
        }
        if (this.storyLimitFetched) {
            return this.storyLimitCached;
        }
        TL_stories.TL_stories_canSendStory tL_stories_canSendStory = new TL_stories.TL_stories_canSendStory();
        tL_stories_canSendStory.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(UserConfig.getInstance(this.currentAccount).getClientUserId());
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_stories_canSendStory, new RequestDelegate() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda10
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$checkStoryLimit$29(tLObject, tL_error);
            }
        }, 1024);
        return null;
    }

    public /* synthetic */ void lambda$checkStoryLimit$29(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$checkStoryLimit$28(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$checkStoryLimit$28(TLObject tLObject, TLRPC.TL_error tL_error) {
        this.storyLimitFetched = true;
        if (tLObject instanceof TLRPC.TL_boolTrue) {
            this.storyLimitCached = null;
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesLimitUpdate, new Object[0]);
        } else if (tLObject instanceof TL_stories.canSendStoryCount) {
            this.storyLimitCached = new StoryLimit(1, ((TL_stories.canSendStoryCount) tLObject).count_remains, -1L);
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesLimitUpdate, new Object[0]);
        } else {
            checkStoryError(tL_error);
        }
    }

    public void canSendStoryFor(final long j, final Consumer<Boolean> consumer, final boolean z, final Theme.ResourcesProvider resourcesProvider) {
        TL_stories.TL_stories_canSendStory tL_stories_canSendStory = new TL_stories.TL_stories_canSendStory();
        tL_stories_canSendStory.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_stories_canSendStory, new RequestDelegate() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda16
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$canSendStoryFor$34(z, j, consumer, resourcesProvider, tLObject, tL_error);
            }
        }, 1024);
    }

    public /* synthetic */ void lambda$canSendStoryFor$34(final boolean z, final long j, final Consumer consumer, final Theme.ResourcesProvider resourcesProvider, TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$canSendStoryFor$33(tL_error, z, j, consumer, resourcesProvider);
            }
        });
    }

    public /* synthetic */ void lambda$canSendStoryFor$33(TLRPC.TL_error tL_error, boolean z, final long j, final Consumer consumer, Theme.ResourcesProvider resourcesProvider) {
        if (tL_error != null) {
            if (tL_error.text.contains("BOOSTS_REQUIRED")) {
                if (z) {
                    final MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
                    messagesController.getBoostsController().getBoostsStats(j, new Consumer() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda34
                        @Override // com.google.android.exoplayer2.util.Consumer
                        public final void accept(Object obj) {
                            this.f$0.lambda$canSendStoryFor$32(consumer, messagesController, j, (TL_stories.TL_premium_boostsStatus) obj);
                        }
                    });
                    return;
                } else {
                    consumer.accept(Boolean.FALSE);
                    return;
                }
            }
            if (tL_error.text.startsWith("STORY_LIVE_ALREADY_")) {
                BaseFragment lastFragment = LaunchActivity.getLastFragment();
                if (z && lastFragment != null) {
                    new AlertDialog.Builder(lastFragment.getContext(), resourcesProvider).setTitle(LocaleController.getString(R.string.LiveStoryAlreadyStreamingTitle)).setMessage(LocaleController.getString(R.string.LiveStoryAlreadyStreaming)).setPositiveButton(LocaleController.getString(R.string.OK), null).show();
                }
                consumer.accept(Boolean.FALSE);
                return;
            }
            if (tL_error.text.equalsIgnoreCase(MediaError.ERROR_REASON_PREMIUM_ACCOUNT_REQUIRED)) {
                BaseFragment lastFragment2 = LaunchActivity.getLastFragment();
                if (z && lastFragment2 != null) {
                    lastFragment2.showDialog(new PremiumFeatureBottomSheet(lastFragment2, 14, true));
                }
                consumer.accept(Boolean.FALSE);
                return;
            }
            BulletinFactory bulletinFactoryGlobal = BulletinFactory.global();
            if (bulletinFactoryGlobal != null) {
                bulletinFactoryGlobal.showForError(tL_error);
            }
            consumer.accept(Boolean.FALSE);
            return;
        }
        consumer.accept(Boolean.TRUE);
    }

    public /* synthetic */ void lambda$canSendStoryFor$32(final Consumer consumer, MessagesController messagesController, final long j, final TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus) {
        if (tL_premium_boostsStatus == null) {
            consumer.accept(Boolean.FALSE);
        } else {
            messagesController.getBoostsController().userCanBoostChannel(j, tL_premium_boostsStatus, new Consumer() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda36
                @Override // com.google.android.exoplayer2.util.Consumer
                public final void accept(Object obj) {
                    this.f$0.lambda$canSendStoryFor$31(consumer, j, tL_premium_boostsStatus, (ChannelBoostsController.CanApplyBoost) obj);
                }
            });
            consumer.accept(Boolean.FALSE);
        }
    }

    public /* synthetic */ void lambda$canSendStoryFor$31(Consumer consumer, final long j, TL_stories.TL_premium_boostsStatus tL_premium_boostsStatus, ChannelBoostsController.CanApplyBoost canApplyBoost) {
        if (canApplyBoost == null) {
            consumer.accept(Boolean.FALSE);
        } else {
            LimitReachedBottomSheet.openBoostsForPostingStories(LaunchActivity.getLastFragment(), j, canApplyBoost, tL_premium_boostsStatus, canPostStories(j) ? new Runnable() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda39
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$canSendStoryFor$30(j);
                }
            } : null);
            consumer.accept(Boolean.FALSE);
        }
    }

    public /* synthetic */ void lambda$canSendStoryFor$30(long j) {
        BaseFragment baseFragmentCreate = StatisticActivity.create(MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j)));
        BaseFragment lastFragment = LaunchActivity.getLastFragment();
        if (lastFragment != null) {
            if (StoryRecorder.isVisible()) {
                BaseFragment.BottomSheetParams bottomSheetParams = new BaseFragment.BottomSheetParams();
                bottomSheetParams.transitionFromLeft = true;
                lastFragment.showAsSheet(baseFragmentCreate, bottomSheetParams);
                return;
            }
            lastFragment.presentFragment(baseFragmentCreate);
        }
    }

    /* JADX WARN: Code duplicated, block: B:24:0x0093  */
    public boolean checkStoryError(TLRPC.TL_error tL_error) {
        boolean z;
        String str;
        if (tL_error == null || (str = tL_error.text) == null) {
            z = false;
        } else {
            boolean zStartsWith = str.startsWith("STORY_SEND_FLOOD_WEEKLY_");
            String str2 = tL_error.text;
            long j = 0;
            z = true;
            if (zStartsWith) {
                try {
                    j = Long.parseLong(str2.substring(24));
                } catch (Exception unused) {
                }
                this.storyLimitCached = new StoryLimit(2, 0, j);
            } else {
                boolean zStartsWith2 = str2.startsWith("STORY_SEND_FLOOD_MONTHLY_");
                String str3 = tL_error.text;
                if (zStartsWith2) {
                    try {
                        j = Long.parseLong(str3.substring(25));
                    } catch (Exception unused2) {
                    }
                    this.storyLimitCached = new StoryLimit(3, 0, j);
                } else if (str3.equals("STORIES_TOO_MUCH")) {
                    this.storyLimitCached = new StoryLimit(1, 0, 0L);
                } else if (tL_error.text.equals(MediaError.ERROR_REASON_PREMIUM_ACCOUNT_REQUIRED)) {
                    MessagesController messagesController = MessagesController.getInstance(this.currentAccount);
                    if ("enabled".equals(messagesController.storiesPosting)) {
                        SharedPreferences.Editor editorEdit = messagesController.getMainSettings().edit();
                        messagesController.storiesPosting = "premium";
                        editorEdit.putString("storiesPosting", "premium").apply();
                        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesEnabledUpdate, new Object[0]);
                    }
                } else {
                    z = false;
                }
            }
        }
        if (z) {
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesLimitUpdate, new Object[0]);
        }
        return z;
    }

    public boolean hasStoryLimit(int i) {
        StoryLimit storyLimitCheckStoryLimit = checkStoryLimit();
        return storyLimitCheckStoryLimit != null && storyLimitCheckStoryLimit.active(this.currentAccount, i);
    }

    public void invalidateStoryLimit() {
        this.storyLimitFetched = false;
        this.storyLimitCached = null;
    }

    public static class StoryLimit {
        public int remains_count;
        public int type;
        public long until;

        public StoryLimit(int i, int i2, long j) {
            this.type = i;
            this.until = j;
            this.remains_count = i2;
        }

        public int getLimitReachedType() {
            int i = this.type;
            if (i != 2) {
                return i != 3 ? 14 : 16;
            }
            return 15;
        }

        public boolean active(int i) {
            return active(i, 1);
        }

        public boolean active(int i, int i2) {
            int i3 = this.type;
            if (i3 != 1) {
                return !(i3 == 2 || i3 == 3) || ((long) ConnectionsManager.getInstance(i).getCurrentTime()) < this.until;
            }
            return this.remains_count < i2;
        }
    }

    public void loadSendAs() {
        if (this.loadingSendAs || this.loadedSendAs) {
            return;
        }
        this.loadingSendAs = true;
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(new TL_stories.TL_stories_getChatsToSend(), new RequestDelegate() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda17
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$loadSendAs$36(tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$loadSendAs$36(final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda29
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadSendAs$35(tLObject);
            }
        });
    }

    public /* synthetic */ void lambda$loadSendAs$35(TLObject tLObject) {
        this.sendAs.clear();
        this.sendAs.add(new TLRPC.TL_inputPeerSelf());
        if (tLObject instanceof TLRPC.TL_messages_chats) {
            ArrayList<TLRPC.Chat> arrayList = ((TLRPC.TL_messages_chats) tLObject).chats;
            MessagesController.getInstance(this.currentAccount).putChats(arrayList, false);
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                TLRPC.Chat chat = arrayList.get(i);
                i++;
                this.sendAs.add(MessagesController.getInputPeer(chat));
            }
        }
        this.loadingSendAs = false;
        this.loadedSendAs = true;
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesSendAsUpdate, new Object[0]);
    }

    public String getAlbumName(long j, int i) {
        StoryAlbum storyAlbumFindById;
        StoriesCollections storyAlbumsList = getStoryAlbumsList(j, false);
        if (storyAlbumsList == null || (storyAlbumFindById = storyAlbumsList.findById(i)) == null) {
            return null;
        }
        return storyAlbumFindById.title;
    }

    public boolean canEditStories(long j) {
        TLRPC.Chat chat;
        if (j >= 0 || (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j))) == null) {
            return false;
        }
        if (chat.creator) {
            return true;
        }
        TLRPC.TL_chatAdminRights tL_chatAdminRights = chat.admin_rights;
        return tL_chatAdminRights != null && tL_chatAdminRights.edit_stories;
    }

    public boolean canEditStoryAlbums(long j) {
        return UserConfig.getInstance(this.currentAccount).getClientUserId() == j || canEditStories(j);
    }

    public boolean canPostStories(TLRPC.Chat chat) {
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
        if (chat == null || !ChatObject.isBoostSupported(chat)) {
            return false;
        }
        return chat.creator || ((tL_chatAdminRights = chat.admin_rights) != null && tL_chatAdminRights.post_stories);
    }

    public boolean canEditStories(TLRPC.Chat chat) {
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
        if (chat == null || !ChatObject.isBoostSupported(chat)) {
            return false;
        }
        return chat.creator || ((tL_chatAdminRights = chat.admin_rights) != null && tL_chatAdminRights.edit_stories);
    }

    public boolean canPostStories(long j) {
        TLRPC.User user;
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
        if (j >= 0) {
            return j > 0 && (user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j))) != null && user.bot && user.bot_can_edit;
        }
        TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-j));
        if (chat == null || !ChatObject.isBoostSupported(chat)) {
            return false;
        }
        return chat.creator || ((tL_chatAdminRights = chat.admin_rights) != null && tL_chatAdminRights.post_stories);
    }

    public boolean canEditStory(TL_stories.StoryItem storyItem) {
        TLRPC.Chat chat;
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
        TLRPC.TL_chatAdminRights tL_chatAdminRights2;
        TLRPC.User user;
        if (storyItem == null || storyItem.dialogId == getSelfUserId()) {
            return false;
        }
        if (storyItem.dialogId > 0 && (user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(storyItem.dialogId))) != null && user.bot && user.bot_can_edit) {
            return true;
        }
        if (storyItem.dialogId >= 0 || (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-storyItem.dialogId))) == null) {
            return false;
        }
        if (chat.creator) {
            return true;
        }
        boolean z = storyItem.out;
        if (z && (tL_chatAdminRights2 = chat.admin_rights) != null && (tL_chatAdminRights2.post_stories || tL_chatAdminRights2.edit_stories)) {
            return true;
        }
        return (z || (tL_chatAdminRights = chat.admin_rights) == null || !tL_chatAdminRights.edit_stories) ? false : true;
    }

    public boolean canDeleteStory(TL_stories.StoryItem storyItem) {
        TLRPC.Chat chat;
        TLRPC.TL_chatAdminRights tL_chatAdminRights;
        TLRPC.TL_chatAdminRights tL_chatAdminRights2;
        TLRPC.User user;
        if (storyItem == null || storyItem.dialogId == getSelfUserId()) {
            return false;
        }
        if (storyItem.dialogId > 0 && (user = MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(storyItem.dialogId))) != null && user.bot && user.bot_can_edit) {
            return true;
        }
        if (storyItem.dialogId >= 0 || (chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-storyItem.dialogId))) == null) {
            return false;
        }
        if (chat.creator) {
            return true;
        }
        boolean z = storyItem.out;
        if (z && (tL_chatAdminRights2 = chat.admin_rights) != null && (tL_chatAdminRights2.post_stories || tL_chatAdminRights2.delete_stories)) {
            return true;
        }
        return (z || (tL_chatAdminRights = chat.admin_rights) == null || !tL_chatAdminRights.delete_stories) ? false : true;
    }

    public boolean canCreateNewAlbum(long j) {
        StoriesCollections storyAlbumsList = getStoryAlbumsList(j, false);
        return storyAlbumsList != null && storyAlbumsList.canCreateNewAlbum();
    }

    public void createAlbum(long j, String str, Utilities.Callback<StoryAlbum> callback) {
        getStoryAlbumsList(j).createCollection(str, callback);
    }

    public void renameAlbum(long j, int i, String str) {
        getStoryAlbumsList(j).renameCollection(i, str);
    }

    public void removeAlbum(long j, int i) {
        getStoryAlbumsList(j).removeCollection(i);
    }

    public void addStoriesToAlbum(long j, int i, ArrayList<TL_stories.StoryItem> arrayList) {
        getStoryAlbumsList(j).addStories(i, arrayList);
    }

    public void addStoryToAlbum(long j, int i, TL_stories.StoryItem storyItem) {
        ArrayList<TL_stories.StoryItem> arrayList = new ArrayList<>(1);
        arrayList.add(storyItem);
        addStoriesToAlbum(j, i, arrayList);
    }

    public void removeStoriesFromAlbum(long j, int i, ArrayList<TL_stories.StoryItem> arrayList) {
        getStoryAlbumsList(j).removeStories(i, arrayList);
    }

    public void removeStoryFromAlbum(long j, int i, TL_stories.StoryItem storyItem) {
        ArrayList<TL_stories.StoryItem> arrayList = new ArrayList<>(1);
        arrayList.add(storyItem);
        removeStoriesFromAlbum(j, i, arrayList);
    }

    public static class StoryAlbum {
        public int album_id;
        public TLRPC.Photo icon_photo;
        public TLRPC.Document icon_video;
        public String title;

        private StoryAlbum() {
        }

        public TL_stories.TL_storyAlbum toTl() {
            TL_stories.TL_storyAlbum tL_storyAlbum = new TL_stories.TL_storyAlbum();
            tL_storyAlbum.album_id = this.album_id;
            tL_storyAlbum.title = this.title;
            tL_storyAlbum.icon_photo = this.icon_photo;
            tL_storyAlbum.icon_video = this.icon_video;
            return tL_storyAlbum;
        }

        public static StoryAlbum from(TL_stories.TL_storyAlbum tL_storyAlbum) {
            StoryAlbum storyAlbum = new StoryAlbum();
            storyAlbum.album_id = tL_storyAlbum.album_id;
            storyAlbum.title = tL_storyAlbum.title;
            storyAlbum.icon_photo = tL_storyAlbum.icon_photo;
            storyAlbum.icon_video = tL_storyAlbum.icon_video;
            return storyAlbum;
        }
    }

    public class StoriesCollections {
        public ArrayList<StoryAlbum> collections;
        public boolean creating;
        public final int currentAccount;
        public int currentRequestId;
        public final long dialogId;
        public final boolean isSelf;
        private ArrayList<StoryAlbum> lastCollections;
        public boolean loaded;
        private boolean loadedCache;
        public boolean loading;

        private StoriesCollections(StoriesController storiesController, int i, long j) {
            this(i, j, true);
        }

        private StoriesCollections(int i, long j, boolean z) {
            this.lastCollections = new ArrayList<>();
            this.collections = new ArrayList<>();
            this.currentRequestId = -1;
            this.currentAccount = i;
            this.dialogId = j;
            this.isSelf = j == UserConfig.getInstance(i).getClientUserId();
            if (z) {
                load();
            }
        }

        public boolean canCreateNewAlbum() {
            return (this.isSelf || StoriesController.this.canEditStoryAlbums(this.dialogId)) && this.loaded && this.collections.size() < MessagesController.getInstance(this.currentAccount).config.storiesAlbumsLimit.get();
        }

        public void load() {
            if (this.loading || this.loaded) {
                return;
            }
            this.loading = true;
            if (!this.loadedCache) {
                MessagesStorage.getInstance(this.currentAccount).loadStoryAlbumsCache(this.dialogId, new java.util.function.Consumer() { // from class: org.telegram.ui.Stories.StoriesController$StoriesCollections$$ExternalSyntheticLambda0
                    @Override // java.util.function.Consumer
                    public final void accept(Object obj) {
                        this.f$0.lambda$load$1((List) obj);
                    }
                });
                return;
            }
            TL_stories.TL_getAlbums tL_getAlbums = new TL_stories.TL_getAlbums();
            tL_getAlbums.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            this.currentRequestId = ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_getAlbums, new RequestDelegate() { // from class: org.telegram.ui.Stories.StoriesController$StoriesCollections$$ExternalSyntheticLambda1
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$load$3(tLObject, tL_error);
                }
            });
        }

        public /* synthetic */ void lambda$load$1(final List list) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Stories.StoriesController$StoriesCollections$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$load$0(list);
                }
            });
        }

        public /* synthetic */ void lambda$load$0(List list) {
            this.collections.clear();
            this.collections.addAll(list);
            this.loadedCache = true;
            this.loading = false;
            load();
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storyAlbumsCollectionsUpdate, Long.valueOf(this.dialogId), this);
        }

        public /* synthetic */ void lambda$load$3(final TLObject tLObject, TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Stories.StoriesController$StoriesCollections$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$load$2(tLObject);
                }
            });
        }

        public /* synthetic */ void lambda$load$2(TLObject tLObject) {
            if (tLObject instanceof TL_stories.TL_albums) {
                TL_stories.TL_albums tL_albums = (TL_stories.TL_albums) tLObject;
                ArrayList arrayList = new ArrayList(tL_albums.albums.size());
                ArrayList<TL_stories.TL_storyAlbum> arrayList2 = tL_albums.albums;
                int size = arrayList2.size();
                int i = 0;
                while (i < size) {
                    TL_stories.TL_storyAlbum tL_storyAlbum = arrayList2.get(i);
                    i++;
                    arrayList.add(StoryAlbum.from(tL_storyAlbum));
                }
                this.collections.clear();
                this.collections.addAll(arrayList);
                this.lastCollections.clear();
                this.lastCollections.addAll(arrayList);
                this.loaded = true;
                this.loading = false;
                updateAlbumsListCache(true);
                return;
            }
            if (tLObject instanceof TL_stories.TL_albumsNotModified) {
                this.collections.clear();
                this.collections.addAll(this.lastCollections);
                this.loaded = true;
                this.loading = false;
                NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storyAlbumsCollectionsUpdate, Long.valueOf(this.dialogId), this);
            }
        }

        private void updateAlbumsListCache(boolean z) {
            MessagesStorage.getInstance(this.currentAccount).saveStoryAlbumsCache(this.dialogId, this.collections);
            if (z) {
                NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storyAlbumsCollectionsUpdate, Long.valueOf(this.dialogId), this);
            }
        }

        public void createCollection(String str, final Utilities.Callback<StoryAlbum> callback) {
            if (this.creating) {
                return;
            }
            this.creating = true;
            TL_stories.TL_createAlbum tL_createAlbum = new TL_stories.TL_createAlbum();
            tL_createAlbum.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            tL_createAlbum.title = str;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_createAlbum, new RequestDelegate() { // from class: org.telegram.ui.Stories.StoriesController$StoriesCollections$$ExternalSyntheticLambda4
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$createCollection$5(callback, tLObject, tL_error);
                }
            });
        }

        public /* synthetic */ void lambda$createCollection$5(final Utilities.Callback callback, final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Stories.StoriesController$StoriesCollections$$ExternalSyntheticLambda5
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$createCollection$4(tLObject, callback, tL_error);
                }
            });
        }

        public /* synthetic */ void lambda$createCollection$4(TLObject tLObject, Utilities.Callback callback, TLRPC.TL_error tL_error) {
            BaseFragment safeLastFragment;
            this.creating = false;
            if (tLObject instanceof TL_stories.TL_storyAlbum) {
                StoryAlbum storyAlbumFrom = StoryAlbum.from((TL_stories.TL_storyAlbum) tLObject);
                this.collections.add(storyAlbumFrom);
                updateAlbumsListCache(true);
                if (callback != null) {
                    callback.run(storyAlbumFrom);
                    return;
                }
                return;
            }
            if (tL_error != null && (safeLastFragment = LaunchActivity.getSafeLastFragment()) != null) {
                BulletinFactory.of(safeLastFragment).showForError(tL_error);
            }
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.storyAlbumsCollectionsUpdate, Long.valueOf(this.dialogId), this);
        }

        public StoryAlbum findById(int i) {
            for (int i2 = 0; i2 < this.collections.size(); i2++) {
                StoryAlbum storyAlbum = this.collections.get(i2);
                if (i == storyAlbum.album_id) {
                    return storyAlbum;
                }
            }
            return null;
        }

        public int indexOf(int i) {
            for (int i2 = 0; i2 < this.collections.size(); i2++) {
                if (i == this.collections.get(i2).album_id) {
                    return i2;
                }
            }
            return -1;
        }

        public void removeCollection(int i) {
            int iIndexOf = indexOf(i);
            if (iIndexOf == -1) {
                return;
            }
            StoryAlbum storyAlbumRemove = this.collections.remove(iIndexOf);
            TL_stories.TL_deleteAlbum tL_deleteAlbum = new TL_stories.TL_deleteAlbum();
            tL_deleteAlbum.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            tL_deleteAlbum.album_id = storyAlbumRemove.album_id;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_deleteAlbum, null);
            updateAlbumsListCache(true);
        }

        public void renameCollection(int i, String str) {
            int iIndexOf = indexOf(i);
            if (iIndexOf == -1) {
                return;
            }
            this.collections.get(iIndexOf).title = str;
            TL_stories.TL_updateAlbum tL_updateAlbum = new TL_stories.TL_updateAlbum();
            tL_updateAlbum.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            tL_updateAlbum.album_id = i;
            tL_updateAlbum.title = str;
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_updateAlbum, null);
            updateAlbumsListCache(true);
        }

        public void addStories(int i, ArrayList<TL_stories.StoryItem> arrayList) {
            TL_stories.TL_updateAlbum tL_updateAlbum = new TL_stories.TL_updateAlbum();
            tL_updateAlbum.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            tL_updateAlbum.album_id = i;
            tL_updateAlbum.add_stories = new ArrayList<>(arrayList.size());
            int size = arrayList.size();
            int i2 = 0;
            while (i2 < size) {
                TL_stories.StoryItem storyItem = arrayList.get(i2);
                i2++;
                tL_updateAlbum.add_stories.add(Integer.valueOf(storyItem.id));
            }
            int size2 = arrayList.size();
            int i3 = 0;
            while (i3 < size2) {
                TL_stories.StoryItem storyItem2 = arrayList.get(i3);
                i3++;
                TL_stories.StoryItem storyItem3 = storyItem2;
                ArrayList<Integer> arrayList2 = storyItem3.albums;
                if (arrayList2 == null) {
                    ArrayList<Integer> arrayList3 = new ArrayList<>();
                    storyItem3.albums = arrayList3;
                    arrayList3.add(Integer.valueOf(i));
                } else if (!arrayList2.contains(Integer.valueOf(i))) {
                    storyItem3.albums.add(Integer.valueOf(i));
                }
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_updateAlbum, null);
            StoriesList storiesList = StoriesController.this.getStoriesList(this.dialogId, 0, i, false);
            if (storiesList != null) {
                storiesList.updateStories(arrayList, true);
            }
            ArrayList<StoryAlbum> arrayList4 = this.collections;
            int size3 = arrayList4.size();
            int i4 = 0;
            while (i4 < size3) {
                StoryAlbum storyAlbum = arrayList4.get(i4);
                i4++;
                StoriesList storiesList2 = StoriesController.this.getStoriesList(this.dialogId, 0, storyAlbum.album_id, false);
                if (storiesList2 != null) {
                    storiesList2.updateStoryItemsAlbums(i, tL_updateAlbum.add_stories, false);
                }
            }
        }

        public void removeStories(int i, ArrayList<TL_stories.StoryItem> arrayList) {
            TL_stories.TL_updateAlbum tL_updateAlbum = new TL_stories.TL_updateAlbum();
            tL_updateAlbum.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            tL_updateAlbum.album_id = i;
            tL_updateAlbum.delete_stories = new ArrayList<>(arrayList.size());
            int size = arrayList.size();
            int i2 = 0;
            int i3 = 0;
            while (i3 < size) {
                TL_stories.StoryItem storyItem = arrayList.get(i3);
                i3++;
                tL_updateAlbum.delete_stories.add(Integer.valueOf(storyItem.id));
            }
            int size2 = arrayList.size();
            int i4 = 0;
            while (i4 < size2) {
                TL_stories.StoryItem storyItem2 = arrayList.get(i4);
                i4++;
                TL_stories.StoryItem storyItem3 = storyItem2;
                ArrayList<Integer> arrayList2 = storyItem3.albums;
                if (arrayList2 != null) {
                    arrayList2.remove(Integer.valueOf(i));
                    if (storyItem3.albums.isEmpty()) {
                        storyItem3.albums = null;
                    }
                }
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_updateAlbum, null);
            StoriesList storiesList = StoriesController.this.getStoriesList(this.dialogId, 0, i, false);
            if (storiesList != null) {
                storiesList.updateDeletedStories(arrayList);
            }
            ArrayList<StoryAlbum> arrayList3 = this.collections;
            int size3 = arrayList3.size();
            while (i2 < size3) {
                StoryAlbum storyAlbum = arrayList3.get(i2);
                i2++;
                StoriesList storiesList2 = StoriesController.this.getStoriesList(this.dialogId, 0, storyAlbum.album_id, false);
                if (storiesList2 != null) {
                    storiesList2.updateStoryItemsAlbums(i, tL_updateAlbum.delete_stories, true);
                }
            }
        }

        public void reorderStep(ArrayList<Integer> arrayList) {
            HashMap map = new HashMap();
            ArrayList<StoryAlbum> arrayList2 = this.collections;
            int size = arrayList2.size();
            int i = 0;
            int i2 = 0;
            while (i2 < size) {
                StoryAlbum storyAlbum = arrayList2.get(i2);
                i2++;
                StoryAlbum storyAlbum2 = storyAlbum;
                map.put(Integer.valueOf(storyAlbum2.album_id), storyAlbum2);
            }
            ArrayList arrayList3 = new ArrayList();
            int size2 = arrayList.size();
            while (i < size2) {
                Integer num = arrayList.get(i);
                i++;
                Integer num2 = num;
                num2.intValue();
                StoryAlbum storyAlbum3 = (StoryAlbum) map.get(num2);
                if (storyAlbum3 != null) {
                    arrayList3.add(storyAlbum3);
                }
            }
            this.collections.clear();
            this.collections.addAll(arrayList3);
        }

        public void reorderComplete(boolean z) {
            sendOrder();
            updateAlbumsListCache(z);
        }

        public void sendOrder() {
            TL_stories.TL_reorderAlbums tL_reorderAlbums = new TL_stories.TL_reorderAlbums();
            tL_reorderAlbums.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(this.dialogId);
            tL_reorderAlbums.order = new ArrayList<>();
            ArrayList<StoryAlbum> arrayList = this.collections;
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                StoryAlbum storyAlbum = arrayList.get(i);
                i++;
                tL_reorderAlbums.order.add(Integer.valueOf(storyAlbum.album_id));
            }
            ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_reorderAlbums, null);
        }
    }

    public static boolean addOrRemoveStoryItemAlbum(TL_stories.StoryItem storyItem, int i, boolean z) {
        if (storyItem == null) {
            return false;
        }
        HashSet hashSet = storyItem.albums != null ? new HashSet(storyItem.albums) : new HashSet();
        Integer numValueOf = Integer.valueOf(i);
        boolean zRemove = z ? hashSet.remove(numValueOf) : hashSet.add(numValueOf);
        storyItem.albums = !hashSet.isEmpty() ? new ArrayList<>(hashSet) : null;
        return zRemove;
    }

    public void checkUnsupportedStory(final long j, final int i) {
        final String str = "228:" + j + ":" + i;
        if (this.requestingUnsupportedStories.contains(str) || this.unsupportedStoriesChecked.contains(str)) {
            return;
        }
        this.requestingUnsupportedStories.add(str);
        TL_stories.TL_stories_getStoriesByID tL_stories_getStoriesByID = new TL_stories.TL_stories_getStoriesByID();
        tL_stories_getStoriesByID.peer = MessagesController.getInstance(this.currentAccount).getInputPeer(j);
        tL_stories_getStoriesByID.id.add(Integer.valueOf(i));
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_stories_getStoriesByID, new RequestDelegate() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda33
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$checkUnsupportedStory$38(i, str, j, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$checkUnsupportedStory$38(final int i, final String str, final long j, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Stories.StoriesController$$ExternalSyntheticLambda38
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$checkUnsupportedStory$37(tLObject, i, str, j);
            }
        });
    }

    public /* synthetic */ void lambda$checkUnsupportedStory$37(TLObject tLObject, int i, String str, long j) {
        TL_stories.StoryItem storyItem;
        if (tLObject == null) {
            storyItem = null;
            break;
        }
        TL_stories.TL_stories_stories tL_stories_stories = (TL_stories.TL_stories_stories) tLObject;
        int i2 = 0;
        MessagesController.getInstance(this.currentAccount).putUsers(tL_stories_stories.users, false);
        MessagesController.getInstance(this.currentAccount).putChats(tL_stories_stories.chats, false);
        while (true) {
            if (i2 >= tL_stories_stories.stories.size()) {
                storyItem = null;
                break;
            } else {
                if (tL_stories_stories.stories.get(i2).id == i) {
                    storyItem = tL_stories_stories.stories.get(i2);
                    break;
                }
                i2++;
            }
        }
        this.requestingUnsupportedStories.remove(str);
        if (storyItem != null) {
            storyItem.dialogId = j;
            TL_stories.TL_updateStory tL_updateStory = new TL_stories.TL_updateStory();
            tL_updateStory.peer = MessagesController.getInstance(this.currentAccount).getPeer(j);
            tL_updateStory.story = storyItem;
            processUpdate(tL_updateStory);
            return;
        }
        for (String str2 : this.unsupportedStoriesChecked) {
            if (str2.endsWith(":" + j + ":" + i)) {
                this.unsupportedStoriesChecked.remove(str2);
                break;
            }
        }
        this.unsupportedStoriesChecked.add(str);
        this.mainSettings.edit().putStringSet("unsupported_stories_checked", this.unsupportedStoriesChecked).apply();
    }
}
