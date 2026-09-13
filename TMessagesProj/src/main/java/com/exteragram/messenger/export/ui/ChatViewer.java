package com.exteragram.messenger.export.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.core.content.FileProvider;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.ChatListItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.components.MessageDetailsPopupWrapper;
import com.exteragram.messenger.export.output.FileManager;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.exteragram.messenger.utils.system.VibratorUtils;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatMessageSharedResources;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.CodeHighlighting;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ArticleViewer;
import org.telegram.ui.AvatarPreviewer;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ChatScrimPopupContainerLayout;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PhonebookShareAlert;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.ContactAddActivity;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.LanguageSelectActivity;
import org.telegram.ui.LocationActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.ThemePreviewActivity;

public class ChatViewer extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private ChatAvatarContainer avatarContainer;
    private ChatActivityAdapter chatAdapter;
    private final ExportMapper.ChatInfo chatInfo;
    private LinearLayoutManager chatLayoutManager;
    private ChatListItemAnimator chatListItemAnimator;
    private RecyclerListView chatListView;
    private boolean checkTextureViewPosition;
    private SizeNotifierFrameLayout contentView;
    protected TLRPC.Chat currentChat;
    private boolean currentFloatingDateOnScreen;
    private boolean currentFloatingTopIsNotMessage;
    private TextView emptyView;
    private FrameLayout emptyViewContainer;
    private boolean endReached;
    private AnimatorSet floatingDateAnimation;
    private ChatActionCell floatingDateView;
    private boolean loading;
    private long minEventId;
    private final String path;
    private RadialProgressView progressBar;
    private FrameLayout progressView;
    private View progressView2;
    private FrameLayout roundVideoContainer;
    private ActionBarPopupWindow scrimPopupWindow;
    private boolean scrollingFloatingDate;
    private boolean searchWas;
    private MessageObject selectedObject;
    public ChatMessageSharedResources sharedResources;
    private UndoView undoView;
    private TextureView videoTextureView;
    private final ArrayList<ChatMessageCell> chatMessageCellsCache = new ArrayList<>();
    private final AnimationNotificationsLocker notificationsLocker = new AnimationNotificationsLocker(new int[]{NotificationCenter.chatInfoDidLoad, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoad, NotificationCenter.botKeyboardDidLoad});
    private final LongSparseArray<MessageObject> messagesDict = new LongSparseArray<>();
    private final HashMap<String, ArrayList<MessageObject>> messagesByDays = new HashMap<>();
    protected ArrayList<MessageObject> messages = new ArrayList<>();
    private final PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() { // from class: com.exteragram.messenger.export.ui.ChatViewer.1
        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z, boolean z2) {
            ChatActionCell chatActionCell;
            MessageObject messageObject2;
            MessageObject messageObject3;
            if (ChatViewer.this.chatListView == null) {
                return null;
            }
            int childCount = ChatViewer.this.chatListView.getChildCount();
            int i2 = 0;
            while (true) {
                ImageReceiver photoImage = null;
                if (i2 >= childCount) {
                    return null;
                }
                View childAt = ChatViewer.this.chatListView.getChildAt(i2);
                if (childAt instanceof ChatMessageCell) {
                    ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                    if (messageObject != null && (messageObject3 = chatMessageCell.getMessageObject()) != null && messageObject3.getId() == messageObject.getId()) {
                        photoImage = chatMessageCell.getPhotoImage();
                    }
                } else if ((childAt instanceof ChatActionCell) && (messageObject2 = (chatActionCell = (ChatActionCell) childAt).getMessageObject()) != null) {
                    if (messageObject != null) {
                        if (messageObject2.getId() == messageObject.getId()) {
                            photoImage = chatActionCell.getPhotoImage();
                        }
                    } else if (fileLocation != null && messageObject2.photoThumbs != null) {
                        for (int i3 = 0; i3 < messageObject2.photoThumbs.size(); i3++) {
                            TLRPC.FileLocation fileLocation2 = messageObject2.photoThumbs.get(i3).location;
                            if (fileLocation2.volume_id == fileLocation.volume_id && fileLocation2.local_id == fileLocation.local_id) {
                                photoImage = chatActionCell.getPhotoImage();
                                break;
                            }
                        }
                    }
                }
                if (photoImage != null) {
                    int[] iArr = new int[2];
                    childAt.getLocationInWindow(iArr);
                    PhotoViewer.PlaceProviderObject placeProviderObject = new PhotoViewer.PlaceProviderObject();
                    placeProviderObject.viewX = iArr[0];
                    placeProviderObject.viewY = iArr[1];
                    placeProviderObject.parentView = ChatViewer.this.chatListView;
                    placeProviderObject.imageReceiver = photoImage;
                    placeProviderObject.thumb = photoImage.getBitmapSafe();
                    placeProviderObject.radius = photoImage.getRoundRadius();
                    placeProviderObject.isEvent = true;
                    return placeProviderObject;
                }
                i2++;
            }
        }
    };
    private int scrollToPositionOnRecreate = -1;
    private int scrollToOffsetOnRecreate = 0;
    private boolean paused = true;
    private boolean wasPaused = false;
    private final String searchQuery = "";
    private final ChatActivity.ThemeDelegate theme = null;
    private AtomicInteger lastLoadedMsgFileId = null;

    public ChatViewer(String str) {
        this.path = str;
        this.chatInfo = (ExportMapper.ChatInfo) ExteraConfig.getGSON().fromJson(FileManager.readFileContent(new File(str + "/info.json")), ExportMapper.ChatInfo.class);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public long getDialogId() {
        return this.chatInfo.id;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean onFragmentCreate() {
        super.onFragmentCreate();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.didSetNewWallpapper);
        loadMessages(true);
        return true;
    }

    private void fillMessagesCount() {
        Utilities.globalQueue.postRunnable(new Runnable() { // from class: com.exteragram.messenger.export.ui.ChatViewer$$ExternalSyntheticLambda6
            @Override // java.lang.Runnable
            public final void run() {
                ChatViewer.this.lambda$fillMessagesCount$1();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fillMessagesCount$1() {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.export.ui.ChatViewer$$ExternalSyntheticLambda9
            @Override // java.lang.Runnable
            public final void run() {
                ChatViewer.this.lambda$fillMessagesCount$0();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$fillMessagesCount$0() {
        ChatAvatarContainer chatAvatarContainer = this.avatarContainer;
        if (chatAvatarContainer != null) {
            chatAvatarContainer.setSubtitle("msgs count: " + this.chatInfo.msgsCount);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public int getThemedColor(int i) {
        return Theme.getColor(i);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public int getNavigationBarColor() {
        return getThemedColor(Theme.key_chat_messagePanelBackground);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onFragmentDestroy() {
        super.onFragmentDestroy();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidStart);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingPlayStateChanged);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingDidReset);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.didSetNewWallpapper);
        this.notificationsLocker.unlock();
    }

    private void updateEmptyPlaceholder() {
        TextView textView = this.emptyView;
        if (textView == null) {
            return;
        }
        textView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(5.0f));
        this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.NoResult)));
    }

    private ArrayList<MessageObject> loadDeleted(int i) {
        int iDecrementAndGet;
        AtomicInteger atomicInteger = this.lastLoadedMsgFileId;
        if (atomicInteger != null && atomicInteger.get() == 0) {
            return new ArrayList<>();
        }
        AtomicInteger atomicInteger2 = this.lastLoadedMsgFileId;
        if (atomicInteger2 != null) {
            iDecrementAndGet = atomicInteger2.decrementAndGet();
        } else {
            int iMax = Integer.MIN_VALUE;
            File[] files = new File(this.path).listFiles(new FilenameFilter() {
                @Override
                public final boolean accept(File file2, String str) {
                    return str.contains("messages") && str.contains("json");
                }
            });
            if (files != null) {
                for (File file : files) {
                    Matcher matcher = Pattern.compile("(\\d+)").matcher(file.getName());
                    if (matcher.find()) {
                        iMax = Math.max(iMax, Integer.parseInt(matcher.group()));
                    }
                }
            }
            if (iMax == Integer.MIN_VALUE) {
                this.lastLoadedMsgFileId = new AtomicInteger(0);
                return new ArrayList<>();
            }
            this.lastLoadedMsgFileId = new AtomicInteger(iMax);
            iDecrementAndGet = iMax;
        }
        File file2 = new File(this.path + "/messages" + iDecrementAndGet + ".json");
        if (file2.length() == 0) {
            return new ArrayList<>();
        }
        Log.d("exteraGram", "msg file size: " + file2.length());
        ArrayList<MessageObject> arrayListMapMessages = new ExportMapper(this.currentAccount, this.path, this.chatInfo).mapMessages((ExportMapper.JsonMessage[]) ExteraConfig.getGSON().fromJson(FileManager.readFileContent(file2), ExportMapper.JsonMessage[].class));
        final MessagesController messagesController = getMessagesController();
        MessagesStorage messagesStorage = getMessagesStorage();
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        int size = arrayListMapMessages.size();
        int i2 = 0;
        while (i2 < size) {
            MessageObject messageObject = arrayListMapMessages.get(i2);
            i2++;
            MessageObject messageObject2 = messageObject;
            if (!TextUtils.isEmpty(messageObject2.messageOwner.message)) {
                MessagesStorage.addUsersAndChatsFromMessage(messageObject2.messageOwner, arrayList, arrayList2, null);
            }
        }
        QuadroResult entities = getEntities(messagesStorage, arrayList, arrayList2);
        Pair<LongSparseArray<TLRPC.User>, LongSparseArray<TLRPC.Chat>> dicts = entities.getDicts();
        final ArrayList<TLRPC.User> users = entities.getUsers();
        final ArrayList<TLRPC.Chat> chats = entities.getChats();
        AndroidUtilities.runOnUIThread(new Runnable() {
            @Override
            public final void run() {
                if (!users.isEmpty()) {
                    messagesController.putUsers(users, true);
                }
                if (!chats.isEmpty()) {
                    messagesController.putChats(chats, true);
                }
            }
        });
        ArrayList<MessageObject> arrayList3 = new ArrayList<>();
        for (int i3 = 0; i3 < arrayListMapMessages.size(); i3++) {
            arrayList3.add(new MessageObject(this.currentAccount, arrayListMapMessages.get(i3).messageOwner, dicts.first, dicts.second, false, false));
        }
        return arrayList3;
    }

    private String getDateKey(MessageObject messageObject) {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(((long) messageObject.messageOwner.date) * 1000);
        int i = gregorianCalendar.get(Calendar.DAY_OF_YEAR);
        return String.format("%d_%02d_%02d", Integer.valueOf(gregorianCalendar.get(Calendar.YEAR)), Integer.valueOf(gregorianCalendar.get(Calendar.MONTH)), Integer.valueOf(i));
    }

    private void loadMessages(boolean z) {
        ChatActivityAdapter chatActivityAdapter;
        if (this.loading) {
            return;
        }
        if (z) {
            this.minEventId = Long.MAX_VALUE;
            FrameLayout frameLayout = this.progressView;
            if (frameLayout != null) {
                AndroidUtilities.updateViewVisibilityAnimated(frameLayout, true, 0.3f, true);
                this.emptyViewContainer.setVisibility(4);
                this.chatListView.setEmptyView(null);
            }
            this.messagesDict.clear();
            this.messages.clear();
            this.messagesByDays.clear();
            fillMessagesCount();
        }
        this.loading = true;
        updateEmptyPlaceholder();
        final ArrayList<MessageObject> arrayListLoadDeleted = loadDeleted((z || this.messages.isEmpty()) ? Integer.MAX_VALUE : (int) this.minEventId);
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.export.ui.ChatViewer$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                ChatViewer.this.lambda$loadMessages$4(arrayListLoadDeleted);
            }
        });
        if (!z || (chatActivityAdapter = this.chatAdapter) == null) {
            return;
        }
        chatActivityAdapter.notifyDataSetChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code duplicated, block: B:19:0x00cd  */
    public /* synthetic */ void lambda$loadMessages$4(ArrayList arrayList) {
        int i;
        this.chatListItemAnimator.setShouldAnimateEnterFromBottom(false);
        int size = this.messages.size();
        int size2 = arrayList.size();
        boolean z = false;
        int i2 = 0;
        while (i2 < size2) {
            Object obj = arrayList.get(i2);
            i2++;
            MessageObject messageObject = (MessageObject) obj;
            if (this.messagesDict.indexOfKey(messageObject.messageOwner.id) < 0) {
                this.minEventId = Math.min(this.minEventId, messageObject.messageOwner.id);
                if (messageObject.contentType >= 0) {
                    this.messagesDict.put(messageObject.messageOwner.id, messageObject);
                    String dateKey = getDateKey(messageObject);
                    if (this.messagesByDays.get(dateKey) == null) {
                        this.messagesByDays.put(dateKey, new ArrayList<>());
                        TLRPC.TL_message tL_message = new TLRPC.TL_message();
                        tL_message.message = LocaleController.formatDateChat(messageObject.messageOwner.date);
                        tL_message.id = 0;
                        tL_message.date = messageObject.messageOwner.date;
                        MessageObject messageObject2 = new MessageObject(this.currentAccount, tL_message, false, false);
                        messageObject2.type = 10;
                        messageObject2.contentType = 1;
                        messageObject2.isDateObject = true;
                        this.messages.add(messageObject);
                        this.messages.add(messageObject2);
                    } else if (this.messages.isEmpty()) {
                        this.messages.add(messageObject);
                    } else {
                        ArrayList<MessageObject> arrayList2 = this.messages;
                        if (arrayList2.get(arrayList2.size() - 1).isDateObject) {
                            ArrayList<MessageObject> arrayList3 = this.messages;
                            MessageObject messageObject3 = arrayList3.get(arrayList3.size() - 1);
                            ArrayList<MessageObject> arrayList4 = this.messages;
                            arrayList4.remove(arrayList4.size() - 1);
                            this.messages.add(messageObject);
                            this.messages.add(messageObject3);
                        } else {
                            this.messages.add(messageObject);
                        }
                    }
                }
                z = true;
            }
        }
        int size3 = this.messages.size() - size;
        this.loading = false;
        if (!z) {
            this.endReached = true;
        }
        AndroidUtilities.updateViewVisibilityAnimated(this.progressView, false, 0.3f, true);
        this.chatListView.setEmptyView(this.emptyViewContainer);
        boolean z2 = this.endReached;
        if (size3 == 0) {
            if (z2) {
                this.chatAdapter.notifyItemRemoved(0);
                return;
            }
            return;
        }
        if (z2) {
            this.chatAdapter.notifyItemRangeChanged(0, 2);
            i = 1;
        } else {
            i = 0;
        }
        int iFindLastVisibleItemPosition = this.chatLayoutManager.findLastVisibleItemPosition();
        View viewFindViewByPosition = this.chatLayoutManager.findViewByPosition(iFindLastVisibleItemPosition);
        int top = (viewFindViewByPosition != null ? viewFindViewByPosition.getTop() : 0) - this.chatListView.getPaddingTop();
        if (size3 - i > 0) {
            int i3 = (i ^ 1) + 1;
            this.chatAdapter.notifyItemChanged(i3);
            this.chatAdapter.notifyItemRangeInserted(i3, size3 - i);
        }
        if (iFindLastVisibleItemPosition != -1) {
            this.chatLayoutManager.scrollToPositionWithOffset((iFindLastVisibleItemPosition + size3) - i, top);
        }
    }

    @Override // org.telegram.messenger.NotificationCenter.NotificationCenterDelegate
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        ChatMessageCell chatMessageCell;
        MessageObject messageObject;
        ChatMessageCell chatMessageCell2;
        MessageObject messageObject2;
        ChatMessageCell chatMessageCell3;
        MessageObject messageObject3;
        if (i == NotificationCenter.emojiLoaded) {
            RecyclerListView recyclerListView = this.chatListView;
            if (recyclerListView != null) {
                recyclerListView.invalidateViews();
                return;
            }
            return;
        }
        if (i == NotificationCenter.messagePlayingDidStart) {
            if (((MessageObject) objArr[0]).isRoundVideo()) {
                MediaController.getInstance().setTextureView(createTextureView(true), this.aspectRatioFrameLayout, this.roundVideoContainer, true);
                updateTextureViewPosition();
            }
            RecyclerListView recyclerListView2 = this.chatListView;
            if (recyclerListView2 != null) {
                int childCount = recyclerListView2.getChildCount();
                for (int i3 = 0; i3 < childCount; i3++) {
                    View childAt = this.chatListView.getChildAt(i3);
                    if ((childAt instanceof ChatMessageCell) && (messageObject3 = (chatMessageCell3 = (ChatMessageCell) childAt).getMessageObject()) != null) {
                        if (messageObject3.isVoice() || messageObject3.isMusic()) {
                            chatMessageCell3.updateButtonState(false, true, false);
                        } else if (messageObject3.isRoundVideo()) {
                            chatMessageCell3.checkVideoPlayback(false, null);
                            if (!MediaController.getInstance().isPlayingMessage(messageObject3) && messageObject3.audioProgress != 0.0f) {
                                messageObject3.resetPlayingProgress();
                                chatMessageCell3.invalidate();
                            }
                        }
                    }
                }
                return;
            }
            return;
        }
        if (i == NotificationCenter.messagePlayingDidReset || i == NotificationCenter.messagePlayingPlayStateChanged) {
            RecyclerListView recyclerListView3 = this.chatListView;
            if (recyclerListView3 != null) {
                int childCount2 = recyclerListView3.getChildCount();
                for (int i4 = 0; i4 < childCount2; i4++) {
                    View childAt2 = this.chatListView.getChildAt(i4);
                    if ((childAt2 instanceof ChatMessageCell) && (messageObject = (chatMessageCell = (ChatMessageCell) childAt2).getMessageObject()) != null) {
                        if (messageObject.isVoice() || messageObject.isMusic()) {
                            chatMessageCell.updateButtonState(false, true, false);
                        } else if (messageObject.isRoundVideo() && !MediaController.getInstance().isPlayingMessage(messageObject)) {
                            chatMessageCell.checkVideoPlayback(true, null);
                        }
                    }
                }
                return;
            }
            return;
        }
        if (i == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer num = (Integer) objArr[0];
            RecyclerListView recyclerListView4 = this.chatListView;
            if (recyclerListView4 != null) {
                int childCount3 = recyclerListView4.getChildCount();
                for (int i5 = 0; i5 < childCount3; i5++) {
                    View childAt3 = this.chatListView.getChildAt(i5);
                    if ((childAt3 instanceof ChatMessageCell) && (messageObject2 = (chatMessageCell2 = (ChatMessageCell) childAt3).getMessageObject()) != null && messageObject2.getId() == num.intValue()) {
                        MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                        if (playingMessageObject != null) {
                            messageObject2.audioProgress = playingMessageObject.audioProgress;
                            messageObject2.audioProgressSec = playingMessageObject.audioProgressSec;
                            messageObject2.audioPlayerDuration = playingMessageObject.audioPlayerDuration;
                            chatMessageCell2.updatePlayingMessageProgress();
                            return;
                        }
                        return;
                    }
                }
                return;
            }
            return;
        }
        if (i != NotificationCenter.didSetNewWallpapper || this.fragmentView == null) {
            return;
        }
        this.contentView.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
        this.progressView2.invalidate();
        TextView textView = this.emptyView;
        if (textView != null) {
            textView.invalidate();
        }
        this.chatListView.invalidateViews();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        this.sharedResources = new ChatMessageSharedResources(context);
        if (this.chatMessageCellsCache.isEmpty()) {
            for (int i = 0; i < 8; i++) {
                this.chatMessageCellsCache.add(new ChatMessageCell(context, this.currentAccount));
            }
        }
        this.searchWas = false;
        this.hasOwnBackground = true;
        Theme.createChatResources(context, false);
        this.actionBar.setAddToContainer(false);
        this.actionBar.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.actionBar.setBackButtonDrawable(new BackDrawable(false));
        this.actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { // from class: com.exteragram.messenger.export.ui.ChatViewer.2
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i2) {
                if (i2 == -1) {
                    ChatViewer.this.finishFragment();
                }
            }
        });
        ChatAvatarContainer chatAvatarContainer = new ChatAvatarContainer(context, null, false, this.theme) { // from class: com.exteragram.messenger.export.ui.ChatViewer.3
            @Override // org.telegram.ui.Components.ChatAvatarContainer
            public void openSearch() {
            }

            @Override // org.telegram.ui.Components.ChatAvatarContainer
            public boolean canSearch() {
                return TextUtils.isEmpty(ChatViewer.this.searchQuery);
            }
        };
        this.avatarContainer = chatAvatarContainer;
        chatAvatarContainer.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.avatarContainer.setEnabled(false);
        this.actionBar.addView(this.avatarContainer, 0, LayoutHelper.createFrame(-2, -1.0f, 51, 56.0f, 0.0f, 40.0f, 0.0f));
        TLObject dialogInAnyWay = getDialogInAnyWay(this.chatInfo.id, Integer.valueOf(UserConfig.selectedAccount), true);
        if (dialogInAnyWay instanceof TLRPC.User) {
            this.avatarContainer.setTitle(this.chatInfo.name);
            fillMessagesCount();
            this.avatarContainer.setUserAvatar((TLRPC.User) dialogInAnyWay);
        } else if (dialogInAnyWay instanceof TLRPC.Chat) {
            this.avatarContainer.setTitle(this.chatInfo.name);
            fillMessagesCount();
            this.avatarContainer.setChatAvatar((TLRPC.Chat) dialogInAnyWay);
        }
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(context) { // from class: com.exteragram.messenger.export.ui.ChatViewer.4
            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.view.ViewGroup, android.view.View
            public void onAttachedToWindow() {
                super.onAttachedToWindow();
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                if (playingMessageObject == null || !playingMessageObject.isRoundVideo() || playingMessageObject.eventId == 0 || playingMessageObject.getDialogId() != ChatViewer.this.getDialogId()) {
                    return;
                }
                MediaController.getInstance().setTextureView(ChatViewer.this.createTextureView(false), ChatViewer.this.aspectRatioFrameLayout, ChatViewer.this.roundVideoContainer, true);
            }

            @Override // android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View view, long j) {
                boolean zDrawChild = super.drawChild(canvas, view, j);
                if (view == ChatViewer.this.getActionBar() && ChatViewer.this.getParentLayout() != null) {
                    ChatViewer.this.getParentLayout().drawHeaderShadow(canvas, ChatViewer.this.getActionBar().getVisibility() == 0 ? ChatViewer.this.getActionBar().getMeasuredHeight() : 0);
                }
                return zDrawChild;
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout
            public boolean isActionBarVisible() {
                return ChatViewer.this.getActionBar().getVisibility() == 0;
            }

            @Override // android.widget.FrameLayout, android.view.View
            public void onMeasure(int i2, int i3) {
                int size = View.MeasureSpec.getSize(i2);
                int size2 = View.MeasureSpec.getSize(i3);
                setMeasuredDimension(size, size2);
                int paddingTop = size2 - getPaddingTop();
                measureChildWithMargins(ChatViewer.this.getActionBar(), i2, 0, i3, 0);
                int measuredHeight = ChatViewer.this.getActionBar().getMeasuredHeight();
                if (ChatViewer.this.getActionBar().getVisibility() == 0) {
                    paddingTop -= measuredHeight;
                }
                int childCount = getChildCount();
                for (int i4 = 0; i4 < childCount; i4++) {
                    View childAt = getChildAt(i4);
                    if (childAt != null && childAt.getVisibility() != 8 && childAt != ChatViewer.this.getActionBar()) {
                        if (childAt == ChatViewer.this.chatListView || childAt == ChatViewer.this.progressView) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), paddingTop), TLObject.FLAG_30));
                        } else if (childAt == ChatViewer.this.emptyViewContainer) {
                            childAt.measure(View.MeasureSpec.makeMeasureSpec(size, TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(paddingTop, TLObject.FLAG_30));
                        } else {
                            measureChildWithMargins(childAt, i2, 0, i3, 0);
                        }
                    }
                }
            }

            /* JADX WARN: Code duplicated, block: B:20:0x004d  */
            /* JADX WARN: Code duplicated, block: B:22:0x0051  */
            /* JADX WARN: Code duplicated, block: B:24:0x0055  */
            /* JADX WARN: Code duplicated, block: B:25:0x0058  */
            /* JADX WARN: Code duplicated, block: B:27:0x0060  */
            /* JADX WARN: Code duplicated, block: B:32:0x0087  */
            /* JADX WARN: Code duplicated, block: B:35:0x009a  */
            /* JADX WARN: Code duplicated, block: B:37:0x00ac  */
            /* JADX WARN: Code duplicated, block: B:38:0x00b9  */
            /* JADX WARN: Code duplicated, block: B:41:0x00bd  */
            /* JADX WARN: Code duplicated, block: B:43:0x00c5  */
            /* JADX WARN: Code duplicated, block: B:44:0x00ca  */
            /* JADX WARN: Code duplicated, block: B:46:0x00ce  */
            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean z, int i2, int i3, int i4, int i5) {
                int i6 = 0;
                int i7 = 0;
                int i8 = 0;
                int i9 = 0;
                int i10 = 0;
                int paddingTop = 0;
                int paddingTop2 = 0;
                int measuredHeight = 0;
                int childCount = getChildCount();
                for (int i11 = 0; i11 < childCount; i11++) {
                    View childAt = getChildAt(i11);
                    if (childAt.getVisibility() != 8) {
                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) childAt.getLayoutParams();
                        int measuredWidth = childAt.getMeasuredWidth();
                        int measuredHeight2 = childAt.getMeasuredHeight();
                        int i12 = layoutParams.gravity;
                        if (i12 == -1) {
                            i12 = 51;
                        }
                        int i13 = i12 & 112;
                        int i14 = i12 & 7;
                        if (i14 == 1) {
                            i6 = (((i4 - i2) - measuredWidth) / 2) + layoutParams.leftMargin;
                            i7 = layoutParams.rightMargin;
                        } else {
                            if (i14 == 5) {
                                i6 = i4 - measuredWidth;
                                i7 = layoutParams.rightMargin;
                            } else {
                                i8 = layoutParams.leftMargin;
                            }
                            if (i13 != 16) {
                                i9 = (((i5 - i3) - measuredHeight2) / 2) + layoutParams.topMargin;
                                i10 = layoutParams.bottomMargin;
                            } else {
                                if (i13 != 48) {
                                    paddingTop = layoutParams.topMargin + getPaddingTop();
                                    if (childAt != ChatViewer.this.getActionBar() && ChatViewer.this.getActionBar().getVisibility() == 0) {
                                        paddingTop += ChatViewer.this.getActionBar().getMeasuredHeight();
                                    }
                                } else if (i13 != 80) {
                                    i9 = (i5 - i3) - measuredHeight2;
                                    i10 = layoutParams.bottomMargin;
                                } else {
                                    paddingTop = layoutParams.topMargin;
                                }
                                if (childAt == ChatViewer.this.emptyViewContainer) {
                                    if (childAt == ChatViewer.this.getActionBar()) {
                                        paddingTop2 = getPaddingTop();
                                    } else if (childAt == this.backgroundView) {
                                        paddingTop = 0;
                                    }
                                    childAt.layout(i8, paddingTop, measuredWidth + i8, measuredHeight2 + paddingTop);
                                } else {
                                    int iDp = AndroidUtilities.dp(24.0f);
                                    if (ChatViewer.this.getActionBar().getVisibility() == 0) {
                                        measuredHeight = ChatViewer.this.getActionBar().getMeasuredHeight() / 2;
                                    } else {
                                        measuredHeight = 0;
                                    }
                                    paddingTop2 = iDp - measuredHeight;
                                }
                                paddingTop -= paddingTop2;
                                childAt.layout(i8, paddingTop, measuredWidth + i8, measuredHeight2 + paddingTop);
                            }
                            paddingTop = i9 - i10;
                            if (childAt == ChatViewer.this.emptyViewContainer) {
                                if (childAt == ChatViewer.this.getActionBar()) {
                                    paddingTop2 = getPaddingTop();
                                } else if (childAt == this.backgroundView) {
                                    paddingTop = 0;
                                }
                                childAt.layout(i8, paddingTop, measuredWidth + i8, measuredHeight2 + paddingTop);
                            } else {
                                int iDp2 = AndroidUtilities.dp(24.0f);
                                if (ChatViewer.this.getActionBar().getVisibility() == 0) {
                                    measuredHeight = ChatViewer.this.getActionBar().getMeasuredHeight() / 2;
                                } else {
                                    measuredHeight = 0;
                                }
                                paddingTop2 = iDp2 - measuredHeight;
                            }
                            paddingTop -= paddingTop2;
                            childAt.layout(i8, paddingTop, measuredWidth + i8, measuredHeight2 + paddingTop);
                        }
                        i8 = i6 - i7;
                        if (i13 != 16) {
                            i9 = (((i5 - i3) - measuredHeight2) / 2) + layoutParams.topMargin;
                            i10 = layoutParams.bottomMargin;
                        } else {
                            if (i13 != 48) {
                                paddingTop = layoutParams.topMargin + getPaddingTop();
                                if (childAt != ChatViewer.this.getActionBar()) {
                                    paddingTop += ChatViewer.this.getActionBar().getMeasuredHeight();
                                }
                            } else if (i13 != 80) {
                                i9 = (i5 - i3) - measuredHeight2;
                                i10 = layoutParams.bottomMargin;
                            } else {
                                paddingTop = layoutParams.topMargin;
                            }
                            if (childAt == ChatViewer.this.emptyViewContainer) {
                                if (childAt == ChatViewer.this.getActionBar()) {
                                    paddingTop2 = getPaddingTop();
                                } else if (childAt == this.backgroundView) {
                                    paddingTop = 0;
                                }
                                childAt.layout(i8, paddingTop, measuredWidth + i8, measuredHeight2 + paddingTop);
                            } else {
                                int iDp3 = AndroidUtilities.dp(24.0f);
                                if (ChatViewer.this.getActionBar().getVisibility() == 0) {
                                    measuredHeight = ChatViewer.this.getActionBar().getMeasuredHeight() / 2;
                                } else {
                                    measuredHeight = 0;
                                }
                                paddingTop2 = iDp3 - measuredHeight;
                            }
                            paddingTop -= paddingTop2;
                            childAt.layout(i8, paddingTop, measuredWidth + i8, measuredHeight2 + paddingTop);
                        }
                        paddingTop = i9 - i10;
                        if (childAt == ChatViewer.this.emptyViewContainer) {
                            if (childAt == ChatViewer.this.getActionBar()) {
                                paddingTop2 = getPaddingTop();
                            } else if (childAt == this.backgroundView) {
                                paddingTop = 0;
                            }
                            childAt.layout(i8, paddingTop, measuredWidth + i8, measuredHeight2 + paddingTop);
                        } else {
                            int iDp4 = AndroidUtilities.dp(24.0f);
                            if (ChatViewer.this.getActionBar().getVisibility() == 0) {
                                measuredHeight = ChatViewer.this.getActionBar().getMeasuredHeight() / 2;
                            } else {
                                measuredHeight = 0;
                            }
                            paddingTop2 = iDp4 - measuredHeight;
                        }
                        paddingTop -= paddingTop2;
                        childAt.layout(i8, paddingTop, measuredWidth + i8, measuredHeight2 + paddingTop);
                    }
                }
                ChatViewer.this.updateMessagesVisiblePart();
                notifyHeightChanged();
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                if (AvatarPreviewer.hasVisibleInstance()) {
                    AvatarPreviewer.getInstance().onTouchEvent(motionEvent);
                    return true;
                }
                return super.dispatchTouchEvent(motionEvent);
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout
            public Theme.ResourcesProvider getResourceProvider() {
                return ChatViewer.this.theme;
            }

            @Override // org.telegram.ui.Components.SizeNotifierFrameLayout
            public Drawable getNewDrawable() {
                Drawable cachedWallpaper = Theme.getCachedWallpaper();
                return cachedWallpaper != null ? cachedWallpaper : super.getNewDrawable();
            }
        };
        this.fragmentView = sizeNotifierFrameLayout;
        SizeNotifierFrameLayout sizeNotifierFrameLayout2 = sizeNotifierFrameLayout;
        this.contentView = sizeNotifierFrameLayout2;
        sizeNotifierFrameLayout2.setOccupyStatusBar(!AndroidUtilities.isTablet());
        this.contentView.setBackgroundImage(Theme.getCachedWallpaper(), Theme.isWallpaperMotion());
        FrameLayout frameLayout = new FrameLayout(context);
        this.emptyViewContainer = frameLayout;
        frameLayout.setVisibility(4);
        this.contentView.addView(this.emptyViewContainer, LayoutHelper.createFrame(-1, -2, 17));
        this.emptyViewContainer.setOnTouchListener((view, motionEvent) -> true);
        TextView textView = new TextView(context);
        this.emptyView = textView;
        textView.setTextSize(1, 14.0f);
        this.emptyView.setGravity(17);
        TextView textView2 = this.emptyView;
        int i2 = Theme.key_chat_serviceText;
        textView2.setTextColor(getThemedColor(i2));
        this.emptyView.setBackground(Theme.createServiceDrawable(AndroidUtilities.dp(6.0f), this.emptyView, this.contentView));
        this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
        this.emptyViewContainer.addView(this.emptyView, LayoutHelper.createFrame(-2, -2.0f, 17, 16.0f, 0.0f, 16.0f, 0.0f));
        RecyclerListView recyclerListView = new RecyclerListView(context, this.theme) { // from class: com.exteragram.messenger.export.ui.ChatViewer.5
            @Override // org.telegram.ui.Components.RecyclerListView, android.view.View
            public void setTranslationY(float f) {
                if (f != getTranslationY()) {
                    super.setTranslationY(f);
                    ChatViewer.this.updateMessagesVisiblePart();
                }
            }

            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.ViewGroup
            public boolean drawChild(Canvas canvas, View view, long j) {
                ChatMessageCell chatMessageCell;
                ImageReceiver avatarImage;
                int y;
                int adapterPosition;
                boolean zDrawChild = super.drawChild(canvas, view, j);
                if ((view instanceof ChatMessageCell) && (avatarImage = (chatMessageCell = (ChatMessageCell) view).getAvatarImage()) != null) {
                    if (chatMessageCell.getMessageObject().deleted) {
                        avatarImage.setVisible(false, false);
                        return zDrawChild;
                    }
                    int y2 = (int) view.getY();
                    if (chatMessageCell.drawPinnedBottom() && (adapterPosition = ChatViewer.this.chatListView.getChildViewHolder(view).getAdapterPosition()) >= 0) {
                        if (ChatViewer.this.chatListView.findViewHolderForAdapterPosition(adapterPosition + 1) != null) {
                            avatarImage.setVisible(false, false);
                            return zDrawChild;
                        }
                    }
                    float slidingOffsetX = chatMessageCell.getSlidingOffsetX() + chatMessageCell.getCheckBoxTranslation();
                    int y3 = ((int) view.getY()) + chatMessageCell.getLayoutHeight();
                    int measuredHeight = ChatViewer.this.chatListView.getMeasuredHeight() - ChatViewer.this.chatListView.getPaddingBottom();
                    if (y3 > measuredHeight) {
                        y3 = measuredHeight;
                    }
                    if (chatMessageCell.drawPinnedTop() && (adapterPosition = ChatViewer.this.chatListView.getChildViewHolder(view).getAdapterPosition()) >= 0) {
                        int i3 = 0;
                        int curAdapterPosition = adapterPosition;
                        while (i3 < 20) {
                            i3++;
                            curAdapterPosition--;
                            RecyclerView.ViewHolder viewHolderFindViewHolderForAdapterPosition = ChatViewer.this.chatListView.findViewHolderForAdapterPosition(curAdapterPosition);
                            if (viewHolderFindViewHolderForAdapterPosition == null) {
                                break;
                            }
                            y2 = viewHolderFindViewHolderForAdapterPosition.itemView.getTop();
                            View view2 = viewHolderFindViewHolderForAdapterPosition.itemView;
                            if (!(view2 instanceof ChatMessageCell)) {
                                break;
                            }
                            chatMessageCell = (ChatMessageCell) view2;
                            if (!chatMessageCell.drawPinnedTop()) {
                                break;
                            }
                        }
                    }
                    if (y3 - AndroidUtilities.dp(48.0f) < y2) {
                        y3 = y2 + AndroidUtilities.dp(48.0f);
                    }
                    if (!chatMessageCell.drawPinnedBottom() && y3 > (y = (int) (chatMessageCell.getY() + chatMessageCell.getMeasuredHeight()))) {
                        y3 = y;
                    }
                    canvas.save();
                    if (slidingOffsetX != 0.0f) {
                        canvas.translate(slidingOffsetX, 0.0f);
                    }
                    if (chatMessageCell.getCurrentMessagesGroup() != null && chatMessageCell.getCurrentMessagesGroup().transitionParams.backgroundChangeBounds) {
                        y3 = (int) (y3 - chatMessageCell.getTranslationY());
                    }
                    avatarImage.setImageY(y3 - AndroidUtilities.dp(44.0f));
                    if (chatMessageCell.shouldDrawAlphaLayer()) {
                        avatarImage.setAlpha(chatMessageCell.getAlpha());
                        canvas.scale(chatMessageCell.getScaleX(), chatMessageCell.getScaleY(), chatMessageCell.getX() + chatMessageCell.getPivotX(), chatMessageCell.getY() + (chatMessageCell.getHeight() >> 1));
                    } else {
                        avatarImage.setAlpha(1.0f);
                    }
                    avatarImage.setVisible(true, false);
                    avatarImage.draw(canvas);
                    canvas.restore();
                }
                return zDrawChild;
            }
        };
        this.chatListView = recyclerListView;
        recyclerListView.setOnItemClickListener(new RecyclerListView.OnItemClickListenerExtended() { // from class: com.exteragram.messenger.export.ui.ChatViewer$$ExternalSyntheticLambda1
            @Override // org.telegram.ui.Components.RecyclerListView.OnItemClickListenerExtended
            public final void onItemClick(View view, int i3, float f, float f2) {
                ChatViewer.this.lambda$createView$6(view, i3, f, f2);
            }
        });
        this.chatListView.setTag(1);
        this.chatListView.setVerticalScrollBarEnabled(true);
        RecyclerListView recyclerListView2 = this.chatListView;
        ChatActivityAdapter chatActivityAdapter = new ChatActivityAdapter(context);
        this.chatAdapter = chatActivityAdapter;
        recyclerListView2.setAdapter(chatActivityAdapter);
        this.chatListView.setClipToPadding(false);
        this.chatListView.setPadding(0, AndroidUtilities.dp(4.0f), 0, AndroidUtilities.dp(3.0f));
        RecyclerListView recyclerListView3 = this.chatListView;
        AnonymousClass6 anonymousClass6 = new AnonymousClass6(null, this.chatListView, this.theme);
        this.chatListItemAnimator = anonymousClass6;
        recyclerListView3.setItemAnimator(anonymousClass6);
        this.chatListItemAnimator.setReversePositions(true);
        this.chatListView.setLayoutAnimation(null);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) { // from class: com.exteragram.messenger.export.ui.ChatViewer.7
            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public boolean supportsPredictiveItemAnimations() {
                return true;
            }

            @Override // androidx.recyclerview.widget.LinearLayoutManager, androidx.recyclerview.widget.RecyclerView.LayoutManager
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int i3) {
                LinearSmoothScrollerCustom linearSmoothScrollerCustom = new LinearSmoothScrollerCustom(recyclerView.getContext(), 0);
                linearSmoothScrollerCustom.setTargetPosition(i3);
                startSmoothScroll(linearSmoothScrollerCustom);
            }
        };
        this.chatLayoutManager = linearLayoutManager;
        linearLayoutManager.setOrientation(1);
        this.chatLayoutManager.setStackFromEnd(true);
        this.chatListView.setLayoutManager(this.chatLayoutManager);
        this.contentView.addView(this.chatListView, LayoutHelper.createFrame(-1, -1.0f));
        this.chatListView.setOnScrollListener(new RecyclerView.OnScrollListener() { // from class: com.exteragram.messenger.export.ui.ChatViewer.8
            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrollStateChanged(RecyclerView recyclerView, int i3) {
                if (i3 == 1) {
                    ChatViewer.this.scrollingFloatingDate = true;
                    ChatViewer.this.checkTextureViewPosition = true;
                } else if (i3 == 0) {
                    ChatViewer.this.scrollingFloatingDate = false;
                    ChatViewer.this.checkTextureViewPosition = false;
                    ChatViewer.this.hideFloatingDateView(true);
                }
            }

            @Override // androidx.recyclerview.widget.RecyclerView.OnScrollListener
            public void onScrolled(RecyclerView recyclerView, int i3, int i4) {
                ChatViewer.this.chatListView.invalidate();
                if (i4 != 0 && ChatViewer.this.scrollingFloatingDate && !ChatViewer.this.currentFloatingTopIsNotMessage && ChatViewer.this.floatingDateView.getTag() == null) {
                    if (ChatViewer.this.floatingDateAnimation != null) {
                        ChatViewer.this.floatingDateAnimation.cancel();
                    }
                    ChatViewer.this.floatingDateView.setTag(1);
                    ChatViewer.this.floatingDateAnimation = new AnimatorSet();
                    ChatViewer.this.floatingDateAnimation.setDuration(150L);
                    ChatViewer.this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(ChatViewer.this.floatingDateView, "alpha", 1.0f));
                    ChatViewer.this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.exteragram.messenger.export.ui.ChatViewer.8.1
                        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                        public void onAnimationEnd(Animator animator) {
                            if (animator.equals(ChatViewer.this.floatingDateAnimation)) {
                                ChatViewer.this.floatingDateAnimation = null;
                            }
                        }
                    });
                    ChatViewer.this.floatingDateAnimation.start();
                }
                ChatViewer.this.checkScrollForLoad(true);
                ChatViewer.this.updateMessagesVisiblePart();
            }
        });
        int i3 = this.scrollToPositionOnRecreate;
        if (i3 != -1) {
            this.chatLayoutManager.scrollToPositionWithOffset(i3, this.scrollToOffsetOnRecreate);
            this.scrollToPositionOnRecreate = -1;
        }
        FrameLayout frameLayout2 = new FrameLayout(context);
        this.progressView = frameLayout2;
        frameLayout2.setVisibility(4);
        this.contentView.addView(this.progressView, LayoutHelper.createFrame(-1, -1, 51));
        View view = new View(context);
        this.progressView2 = view;
        view.setBackground(Theme.createServiceDrawable(AndroidUtilities.dp(18.0f), this.progressView2, this.contentView));
        this.progressView.addView(this.progressView2, LayoutHelper.createFrame(36, 36, 17));
        RadialProgressView radialProgressView = new RadialProgressView(context, this.theme);
        this.progressBar = radialProgressView;
        radialProgressView.setSize(AndroidUtilities.dp(28.0f));
        this.progressBar.setProgressColor(getThemedColor(i2));
        this.progressView.addView(this.progressBar, LayoutHelper.createFrame(32, 32, 17));
        ChatActionCell chatActionCell = new ChatActionCell(context, false, this.theme);
        this.floatingDateView = chatActionCell;
        chatActionCell.setAlpha(0.0f);
        this.floatingDateView.setImportantForAccessibility(2);
        this.contentView.addView(this.floatingDateView, LayoutHelper.createFrame(-2, -2.0f, 49, 0.0f, 4.0f, 0.0f, 0.0f));
        this.contentView.addView(this.actionBar);
        this.chatAdapter.updateRows();
        if (this.loading && this.messages.isEmpty()) {
            AndroidUtilities.updateViewVisibilityAnimated(this.progressView, true, 0.3f, true);
            this.chatListView.setEmptyView(null);
        } else {
            AndroidUtilities.updateViewVisibilityAnimated(this.progressView, false, 0.3f, true);
            this.chatListView.setEmptyView(this.emptyViewContainer);
        }
        this.chatListView.setAnimateEmptyView(true, 1);
        UndoView undoView = new UndoView(context, null, false, this.theme);
        this.undoView = undoView;
        this.contentView.addView(undoView, LayoutHelper.createFrame(-1, -2.0f, 83, 8.0f, 0.0f, 8.0f, 8.0f));
        updateEmptyPlaceholder();
        return this.fragmentView;
    }


    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$6(View view, int i, float f, float f2) {
        createMenu(view, f, f2);
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.export.ui.ChatViewer$6, reason: invalid class name */
    public class AnonymousClass6 extends ChatListItemAnimator {
        Runnable finishRunnable;
        int scrollAnimationIndex;

        public AnonymousClass6(ChatActivity chatActivity, RecyclerListView recyclerListView, Theme.ResourcesProvider resourcesProvider) {
            super(chatActivity, recyclerListView, resourcesProvider);
            this.scrollAnimationIndex = -1;
        }

        @Override // androidx.recyclerview.widget.ChatListItemAnimator
        public void onAnimationStart() {
            if (this.scrollAnimationIndex == -1) {
                this.scrollAnimationIndex = ChatViewer.this.getNotificationCenter().setAnimationInProgress(this.scrollAnimationIndex, null, false);
            }
            Runnable runnable = this.finishRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.finishRunnable = null;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("admin logs chatItemAnimator disable notifications");
            }
            ChatViewer.this.updateMessagesVisiblePart();
        }

        @Override // androidx.recyclerview.widget.ChatListItemAnimator, androidx.recyclerview.widget.DefaultItemAnimator
        public void onAllAnimationsDone() {
            super.onAllAnimationsDone();
            Runnable runnable = this.finishRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            Runnable runnable2 = new Runnable() { // from class: com.exteragram.messenger.export.ui.ChatViewer$6$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    AnonymousClass6.this.lambda$onAllAnimationsDone$0();
                }
            };
            this.finishRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2);
        }

        /* JADX INFO: Access modifiers changed from: private */
        public /* synthetic */ void lambda$onAllAnimationsDone$0() {
            if (this.scrollAnimationIndex != -1) {
                ChatViewer.this.getNotificationCenter().onAnimationFinish(this.scrollAnimationIndex);
                this.scrollAnimationIndex = -1;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("admin logs chatItemAnimator enable notifications");
            }
            ChatViewer.this.updateMessagesVisiblePart();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void closeMenu() {
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean createMenu(View view) {
        return createMenu(view, 0.0f, 0.0f);
    }

    private boolean createMenu(final View view, final float f, final float f2) {
        MessageObject messageObject;
        if (view instanceof ChatMessageCell) {
            messageObject = ((ChatMessageCell) view).getMessageObject();
        } else {
            messageObject = view instanceof ChatActionCell ? ((ChatActionCell) view).getMessageObject() : null;
        }
        if (messageObject == null || messageObject.type == 10) {
            return false;
        }
        int messageType = getMessageType(messageObject);
        this.selectedObject = messageObject;
        if (getParentActivity() == null) {
            return false;
        }
        final ArrayList arrayList = new ArrayList();
        final ArrayList arrayList2 = new ArrayList();
        final ArrayList arrayList3 = new ArrayList();
        MessageObject messageObject2 = this.selectedObject;
        if (messageObject2.type == 0 || messageObject2.caption != null) {
            arrayList.add(LocaleController.getString(R.string.Copy));
            arrayList3.add(Integer.valueOf(R.drawable.msg_copy));
            arrayList2.add(3);
        }
        if (messageType == 3) {
            TLRPC.MessageMedia messageMedia = this.selectedObject.messageOwner.media;
            if ((messageMedia instanceof TLRPC.TL_messageMediaWebPage) && MessageObject.isNewGifDocument(messageMedia.webpage.document)) {
                arrayList.add(LocaleController.getString(R.string.SaveToGIFs));
                arrayList3.add(Integer.valueOf(R.drawable.msg_gif));
                arrayList2.add(11);
            }
        } else if (messageType == 4) {
            if (this.selectedObject.isVideo()) {
                arrayList.add(LocaleController.getString(R.string.SaveToGallery));
                arrayList3.add(Integer.valueOf(R.drawable.msg_gallery));
                arrayList2.add(4);
                arrayList.add(LocaleController.getString(R.string.ShareFile));
                arrayList3.add(Integer.valueOf(R.drawable.msg_share));
                arrayList2.add(6);
            } else if (this.selectedObject.isMusic()) {
                arrayList.add(LocaleController.getString(R.string.SaveToMusic));
                arrayList3.add(Integer.valueOf(R.drawable.msg_download));
                arrayList2.add(10);
                arrayList.add(LocaleController.getString(R.string.ShareFile));
                arrayList3.add(Integer.valueOf(R.drawable.msg_share));
                arrayList2.add(6);
            } else if (this.selectedObject.getDocument() != null) {
                if (MessageObject.isNewGifDocument(this.selectedObject.getDocument())) {
                    arrayList.add(LocaleController.getString(R.string.SaveToGIFs));
                    arrayList3.add(Integer.valueOf(R.drawable.msg_gif));
                    arrayList2.add(11);
                }
                arrayList.add(LocaleController.getString(R.string.SaveToDownloads));
                arrayList3.add(Integer.valueOf(R.drawable.msg_download));
                arrayList2.add(10);
                arrayList.add(LocaleController.getString(R.string.ShareFile));
                arrayList3.add(Integer.valueOf(R.drawable.msg_share));
                arrayList2.add(6);
            } else {
                arrayList.add(LocaleController.getString(R.string.SaveToGallery));
                arrayList3.add(Integer.valueOf(R.drawable.msg_gallery));
                arrayList2.add(4);
            }
        } else if (messageType == 5) {
            arrayList.add(LocaleController.getString(R.string.ApplyLocalizationFile));
            arrayList3.add(Integer.valueOf(R.drawable.msg_language));
            arrayList2.add(5);
            arrayList.add(LocaleController.getString(R.string.SaveToDownloads));
            arrayList3.add(Integer.valueOf(R.drawable.msg_download));
            arrayList2.add(10);
            arrayList.add(LocaleController.getString(R.string.ShareFile));
            arrayList3.add(Integer.valueOf(R.drawable.msg_share));
            arrayList2.add(6);
        } else if (messageType == 10) {
            arrayList.add(LocaleController.getString(R.string.ApplyThemeFile));
            arrayList3.add(Integer.valueOf(R.drawable.msg_theme));
            arrayList2.add(5);
            arrayList.add(LocaleController.getString(R.string.SaveToDownloads));
            arrayList3.add(Integer.valueOf(R.drawable.msg_download));
            arrayList2.add(10);
            arrayList.add(LocaleController.getString(R.string.ShareFile));
            arrayList3.add(Integer.valueOf(R.drawable.msg_share));
            arrayList2.add(6);
        } else if (messageType == 6) {
            arrayList.add(LocaleController.getString(R.string.SaveToGallery));
            arrayList3.add(Integer.valueOf(R.drawable.msg_gallery));
            arrayList2.add(7);
            arrayList.add(LocaleController.getString(R.string.SaveToDownloads));
            arrayList3.add(Integer.valueOf(R.drawable.msg_download));
            arrayList2.add(10);
            arrayList.add(LocaleController.getString(R.string.ShareFile));
            arrayList3.add(Integer.valueOf(R.drawable.msg_share));
            arrayList2.add(6);
        } else if (messageType == 7) {
            if (this.selectedObject.isMask()) {
                arrayList.add(LocaleController.getString(R.string.AddToMasks));
            } else {
                arrayList.add(LocaleController.getString(R.string.AddToStickers));
            }
            arrayList3.add(Integer.valueOf(R.drawable.msg_sticker));
            arrayList2.add(9);
        } else if (messageType == 8) {
            long j = this.selectedObject.messageOwner.media.user_id;
            TLRPC.User user = j != 0 ? MessagesController.getInstance(this.currentAccount).getUser(Long.valueOf(j)) : null;
            if (user != null && user.id != UserConfig.getInstance(this.currentAccount).getClientUserId() && ContactsController.getInstance(this.currentAccount).contactsDict.get(Long.valueOf(user.id)) == null) {
                arrayList.add(LocaleController.getString(R.string.AddContactTitle));
                arrayList3.add(Integer.valueOf(R.drawable.msg_addcontact));
                arrayList2.add(15);
            }
            if (!TextUtils.isEmpty(this.selectedObject.messageOwner.media.phone_number)) {
                arrayList.add(LocaleController.getString(R.string.Copy));
                arrayList3.add(Integer.valueOf(R.drawable.msg_copy));
                arrayList2.add(16);
                arrayList.add(LocaleController.getString(R.string.Call));
                arrayList3.add(Integer.valueOf(R.drawable.msg_calls));
                arrayList2.add(17);
            }
        }
        arrayList.add(LocaleController.getString(R.string.Details));
        arrayList2.add(204);
        arrayList3.add(Integer.valueOf(R.drawable.msg_info));
        new Runnable() { // from class: com.exteragram.messenger.export.ui.ChatViewer$$ExternalSyntheticLambda3
            @Override // java.lang.Runnable
            public final void run() {
                ChatViewer.this.lambda$createMenu$9(arrayList2, arrayList, arrayList3, view, f, f2);
            }
        }.run();
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenu$9(ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, View view, float f, float f2) {
        int y;
        int i;
        final ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout;
        final ArrayList arrayList4 = arrayList;
        if (arrayList4.isEmpty()) {
            return;
        }
        final ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout2 = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity(), R.drawable.popup_fixed_alert, getResourceProvider(), 1);
        actionBarPopupWindowLayout2.setMinimumWidth(AndroidUtilities.dp(200.0f));
        Rect rect = new Rect();
        getParentActivity().getResources().getDrawable(R.drawable.popup_fixed_alert).mutate().getPadding(rect);
        actionBarPopupWindowLayout2.setBackgroundColor(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
        int size = arrayList2.size();
        for (int i2 = 0; i2 < size; i2++) {
            final int index = i2;
            if (arrayList.get(i2) == null) {
                actionBarPopupWindowLayout2.addView((View) new ActionBarPopupWindow.GapView(getContext(), getResourceProvider()), LayoutHelper.createLinear(-1, 8));
            } else {
                final ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getParentActivity(), i2 == 0, i2 == size - 1, getResourceProvider());
                actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(200.0f));
                actionBarMenuSubItem.setTextAndIcon((CharSequence) arrayList2.get(i2), ((Integer) arrayList3.get(i2)).intValue());
                final Integer num = (Integer) arrayList.get(i2);
                actionBarPopupWindowLayout2.addView(actionBarMenuSubItem);
                View.OnClickListener onClickListener = new View.OnClickListener() {
                    @Override
                    public final void onClick(View view2) {
                        ChatViewer.this.lambda$createMenu$7(index, arrayList, num, actionBarPopupWindowLayout2, actionBarMenuSubItem, view2);
                    }
                };
                actionBarMenuSubItem.setOnClickListener(onClickListener);
                if (num.intValue() == 204) {
                    final int iAddViewToSwipeBack = actionBarPopupWindowLayout2.addViewToSwipeBack(new MessageDetailsPopupWrapper(this, actionBarPopupWindowLayout2.getSwipeBack(), this.selectedObject, getResourceProvider()) {
                        @Override
                        public void copy(String str) {
                            if (AndroidUtilities.addToClipboard(str)) {
                                BulletinFactory.of(ChatViewer.this).createCopyBulletin(LocaleController.getString(R.string.TextCopied)).show();
                            }
                        }
                    }.swipeBack);
                    actionBarMenuSubItem.setRightIcon(R.drawable.msg_arrowright);
                    actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public final void onClick(View view2) {
                            ChatViewer.this.lambda$createMenu$8(actionBarPopupWindowLayout2, iAddViewToSwipeBack, view2);
                        }
                    });
                }
            }
        }
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout3 = actionBarPopupWindowLayout2;
        ChatScrimPopupContainerLayout chatScrimPopupContainerLayout = new ChatScrimPopupContainerLayout(this.contentView.getContext()) { // from class: com.exteragram.messenger.export.ui.ChatViewer.10
            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0) {
                    ChatViewer.this.closeMenu();
                }
                return super.dispatchKeyEvent(keyEvent);
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                boolean zDispatchTouchEvent = super.dispatchTouchEvent(motionEvent);
                if (motionEvent.getAction() == 0 && !zDispatchTouchEvent) {
                    ChatViewer.this.closeMenu();
                }
                return zDispatchTouchEvent;
            }
        };
        chatScrimPopupContainerLayout.addView(actionBarPopupWindowLayout3, LayoutHelper.createLinearRelatively(-2.0f, -2.0f, 3, 0.0f, 0.0f, 0.0f, 0.0f));
        chatScrimPopupContainerLayout.setPopupWindowLayout(actionBarPopupWindowLayout3);
        int i3 = -2;
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(chatScrimPopupContainerLayout, i3, i3) { // from class: com.exteragram.messenger.export.ui.ChatViewer.11
            @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow, android.widget.PopupWindow
            public void dismiss() {
                super.dismiss();
                if (ChatViewer.this.scrimPopupWindow != this) {
                    return;
                }
                Bulletin.hideVisible();
                ChatViewer.this.scrimPopupWindow = null;
            }
        };
        this.scrimPopupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setPauseNotifications(true);
        this.scrimPopupWindow.setDismissAnimationDuration(216);
        this.scrimPopupWindow.setOutsideTouchable(true);
        this.scrimPopupWindow.setClippingEnabled(true);
        this.scrimPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
        this.scrimPopupWindow.setFocusable(true);
        chatScrimPopupContainerLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.scrimPopupWindow.setInputMethodMode(2);
        this.scrimPopupWindow.setSoftInputMode(48);
        this.scrimPopupWindow.getContentView().setFocusableInTouchMode(true);
        actionBarPopupWindowLayout3.setFitItems(true);
        int left = (((view.getLeft() + ((int) f)) - chatScrimPopupContainerLayout.getMeasuredWidth()) + rect.left) - AndroidUtilities.dp(28.0f);
        if (left < AndroidUtilities.dp(6.0f)) {
            left = AndroidUtilities.dp(6.0f);
        } else if (left > (this.chatListView.getMeasuredWidth() - AndroidUtilities.dp(6.0f)) - chatScrimPopupContainerLayout.getMeasuredWidth()) {
            left = (this.chatListView.getMeasuredWidth() - AndroidUtilities.dp(6.0f)) - chatScrimPopupContainerLayout.getMeasuredWidth();
        }
        if (AndroidUtilities.isTablet()) {
            int[] iArr = new int[2];
            this.fragmentView.getLocationInWindow(iArr);
            left += iArr[0];
        }
        int height = this.contentView.getHeight();
        int measuredHeight = chatScrimPopupContainerLayout.getMeasuredHeight() + AndroidUtilities.dp(48.0f);
        int iMeasureKeyboardHeight = this.contentView.measureKeyboardHeight();
        if (iMeasureKeyboardHeight > AndroidUtilities.dp(20.0f)) {
            height += iMeasureKeyboardHeight;
        }
        if (measuredHeight < height) {
            y = (int) (this.chatListView.getY() + view.getTop() + f2);
            if ((measuredHeight - rect.top) - rect.bottom > AndroidUtilities.dp(240.0f)) {
                y += AndroidUtilities.dp(240.0f) - measuredHeight;
            }
            if (y < this.chatListView.getY() + AndroidUtilities.dp(24.0f)) {
                y = (int) (this.chatListView.getY() + AndroidUtilities.dp(24.0f));
            } else {
                int i4 = height - measuredHeight;
                if (y > i4 - AndroidUtilities.dp(8.0f)) {
                    y = i4 - AndroidUtilities.dp(8.0f);
                }
            }
        } else {
            y = this.inBubbleMode ? 0 : AndroidUtilities.statusBarHeight;
        }
        chatScrimPopupContainerLayout.setMaxHeight(height - y);
        this.scrimPopupWindow.showAtLocation(this.chatListView, 51, left, y);
        this.scrimPopupWindow.dimBehind();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenu$7(int i, ArrayList arrayList, Integer num, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, ActionBarMenuSubItem actionBarMenuSubItem, View view) {
        if (this.selectedObject == null || i >= arrayList.size()) {
            return;
        }
        processSelectedOption(num.intValue(), arrayList, actionBarPopupWindowLayout, actionBarMenuSubItem);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createMenu$8(ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, int i, View view) {
        if (this.selectedObject == null || getParentActivity() == null) {
            return;
        }
        actionBarPopupWindowLayout.getSwipeBack().openForeground(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public TextureView createTextureView(boolean z) {
        if (this.parentLayout == null) {
            return null;
        }
        if (this.roundVideoContainer == null) {
            FrameLayout frameLayout = new FrameLayout(getParentActivity()) { // from class: com.exteragram.messenger.export.ui.ChatViewer.12
                @Override // android.view.View
                public void setTranslationY(float f) {
                    super.setTranslationY(f);
                    ChatViewer.this.contentView.invalidate();
                }
            };
            this.roundVideoContainer = frameLayout;
            frameLayout.setOutlineProvider(new ViewOutlineProvider() { // from class: com.exteragram.messenger.export.ui.ChatViewer.13
                @Override // android.view.ViewOutlineProvider
                public void getOutline(View view, Outline outline) {
                    int i = AndroidUtilities.roundMessageSize;
                    outline.setOval(0, 0, i, i);
                }
            });
            this.roundVideoContainer.setClipToOutline(true);
            this.roundVideoContainer.setWillNotDraw(false);
            this.roundVideoContainer.setVisibility(4);
            AspectRatioFrameLayout aspectRatioFrameLayout = new AspectRatioFrameLayout(getParentActivity());
            this.aspectRatioFrameLayout = aspectRatioFrameLayout;
            aspectRatioFrameLayout.setBackgroundColor(0);
            if (z) {
                this.roundVideoContainer.addView(this.aspectRatioFrameLayout, LayoutHelper.createFrame(-1, -1.0f));
            }
            TextureView textureView = new TextureView(getParentActivity());
            this.videoTextureView = textureView;
            textureView.setOpaque(false);
            this.aspectRatioFrameLayout.addView(this.videoTextureView, LayoutHelper.createFrame(-1, -1.0f));
        }
        if (this.roundVideoContainer.getParent() == null) {
            SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
            FrameLayout frameLayout2 = this.roundVideoContainer;
            int i = AndroidUtilities.roundMessageSize;
            sizeNotifierFrameLayout.addView(frameLayout2, 1, new FrameLayout.LayoutParams(i, i));
        }
        this.roundVideoContainer.setVisibility(4);
        this.aspectRatioFrameLayout.setDrawingReady(false);
        return this.videoTextureView;
    }

    /* JADX WARN: Code duplicated, block: B:98:0x028d  */
    private void processSelectedOption(int i, ArrayList<Integer> arrayList, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, ActionBarMenuSubItem actionBarMenuSubItem) {
        File file;
        MessageObject messageObject = this.selectedObject;
        if (messageObject == null) {
            return;
        }
        switch (i) {
            case 3:
                AndroidUtilities.addToClipboard(ChatUtils.getInstance().getMessageText(this.selectedObject, null));
                BulletinFactory.of(this).createCopyBulletin(LocaleController.getString(R.string.MessageCopied)).show();
                break;
            case 4:
                String string = messageObject.messageOwner.attachPath;
                if (string != null && string.length() > 0 && !new File(string).exists()) {
                    string = null;
                }
                if (string == null || string.length() == 0) {
                    string = getFileLoader().getPathToMessage(this.selectedObject.messageOwner).toString();
                }
                int i2 = this.selectedObject.type;
                if (i2 == 3 || i2 == 1) {
                    if ((Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                        getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                        this.selectedObject = null;
                        return;
                    }
                    MediaController.saveFile(string, getParentActivity(), this.selectedObject.type == 3 ? 1 : 0, null, null);
                }
                break;
            case 5:
                String str = messageObject.messageOwner.attachPath;
                if (str == null || str.length() == 0) {
                    file = null;
                } else {
                    file = new File(this.selectedObject.messageOwner.attachPath);
                    if (!file.exists()) {
                        file = null;
                    }
                }
                if (file == null) {
                    File pathToMessage = getFileLoader().getPathToMessage(this.selectedObject.messageOwner);
                    if (pathToMessage.exists()) {
                        file = pathToMessage;
                    }
                }
                if (file != null) {
                    if (file.getName().toLowerCase().endsWith("attheme")) {
                        LinearLayoutManager linearLayoutManager = this.chatLayoutManager;
                        if (linearLayoutManager != null) {
                            if (linearLayoutManager.findLastVisibleItemPosition() < this.chatLayoutManager.getItemCount() - 1) {
                                int iFindFirstVisibleItemPosition = this.chatLayoutManager.findFirstVisibleItemPosition();
                                this.scrollToPositionOnRecreate = iFindFirstVisibleItemPosition;
                                RecyclerListView.Holder holder = (RecyclerListView.Holder) this.chatListView.findViewHolderForAdapterPosition(iFindFirstVisibleItemPosition);
                                if (holder != null) {
                                    this.scrollToOffsetOnRecreate = holder.itemView.getTop();
                                } else {
                                    this.scrollToPositionOnRecreate = -1;
                                }
                            } else {
                                this.scrollToPositionOnRecreate = -1;
                            }
                        }
                        Theme.ThemeInfo themeInfoApplyThemeFile = Theme.applyThemeFile(file, this.selectedObject.getDocumentName(), null, true);
                        if (themeInfoApplyThemeFile != null) {
                            presentFragment(new ThemePreviewActivity(themeInfoApplyThemeFile));
                        } else {
                            this.scrollToPositionOnRecreate = -1;
                            if (getParentActivity() == null) {
                                this.selectedObject = null;
                                return;
                            }
                            AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                            builder.setTitle(LocaleController.getString(R.string.AppName));
                            builder.setMessage(LocaleController.getString(R.string.IncorrectTheme));
                            builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
                            showDialog(builder.create());
                        }
                    } else if (LocaleController.getInstance().applyLanguageFile(file, this.currentAccount)) {
                        presentFragment(new LanguageSelectActivity());
                    } else {
                        if (getParentActivity() == null) {
                            this.selectedObject = null;
                            return;
                        }
                        AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                        builder2.setTitle(LocaleController.getString(R.string.AppName));
                        builder2.setMessage(LocaleController.getString(R.string.IncorrectLocalization));
                        builder2.setPositiveButton(LocaleController.getString(R.string.OK), null);
                        showDialog(builder2.create());
                    }
                }
                break;
            case 6:
                String string2 = messageObject.messageOwner.attachPath;
                if (string2 != null && string2.length() > 0 && !new File(string2).exists()) {
                    string2 = null;
                }
                if (string2 == null || string2.length() == 0) {
                    string2 = getFileLoader().getPathToMessage(this.selectedObject.messageOwner).toString();
                }
                Intent intent = new Intent("android.intent.action.SEND");
                intent.setType(this.selectedObject.getDocument().mime_type);
                try {
                    intent.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(getParentActivity(), ApplicationLoader.getApplicationId() + ".provider", new File(string2)));
                    intent.setFlags(1);
                } catch (Exception unused) {
                    intent.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(string2)));
                }
                try {
                    getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString(R.string.ShareFile)), 500);
                } catch (Exception unused2) {
                }
                break;
            case 7:
                String string3 = messageObject.messageOwner.attachPath;
                if (string3 != null && string3.length() > 0 && !new File(string3).exists()) {
                    string3 = null;
                }
                if (string3 == null || string3.length() == 0) {
                    string3 = getFileLoader().getPathToMessage(this.selectedObject.messageOwner).toString();
                }
                if ((Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                    getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    this.selectedObject = null;
                    return;
                }
                MediaController.saveFile(string3, getParentActivity(), 0, null, null);
                break;
            case 9:
                showDialog(new StickersAlert(getParentActivity(), this, this.selectedObject.getInputStickerSet(), null, null, false));
                break;
            case 10:
                if ((Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                    getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                    this.selectedObject = null;
                    return;
                }
                String documentFileName = FileLoader.getDocumentFileName(this.selectedObject.getDocument());
                if (TextUtils.isEmpty(documentFileName)) {
                    documentFileName = this.selectedObject.getFileName();
                }
                String string4 = this.selectedObject.messageOwner.attachPath;
                if (string4 != null && string4.length() > 0 && !new File(string4).exists()) {
                    string4 = null;
                }
                if (string4 == null || string4.length() == 0) {
                    string4 = getFileLoader().getPathToMessage(this.selectedObject.messageOwner).toString();
                }
                MediaController.saveFile(string4, getParentActivity(), this.selectedObject.isMusic() ? 3 : 2, documentFileName, this.selectedObject.getDocument() != null ? this.selectedObject.getDocument().mime_type : "");
                break;
            case 11:
                MessagesController.getInstance(this.currentAccount).saveGif(this.selectedObject, messageObject.getDocument());
                break;
            case 15:
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", this.selectedObject.messageOwner.media.user_id);
                bundle.putString("phone", this.selectedObject.messageOwner.media.phone_number);
                bundle.putBoolean("addContact", true);
                presentFragment(new ContactAddActivity(bundle));
                break;
            case 16:
                AndroidUtilities.addToClipboard(messageObject.messageOwner.media.phone_number);
                BulletinFactory.of(this).createCopyBulletin(LocaleController.getString(R.string.PhoneCopied)).show();
                break;
            case 17:
                try {
                    Intent intent2 = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + this.selectedObject.messageOwner.media.phone_number));
                    intent2.addFlags(268435456);
                    getParentActivity().startActivityForResult(intent2, 500);
                } catch (Exception e) {
                    FileLog.e(e);
                }
                break;
            case 18:
                final int i3 = messageObject.messageOwner.date;
                INavigationLayout iNavigationLayout = this.parentLayout;
                if (iNavigationLayout != null && iNavigationLayout.getFragmentStack().size() > 1) {
                    BaseFragment baseFragment = this.parentLayout.getFragmentStack().get(this.parentLayout.getFragmentStack().size() - 2);
                    if (baseFragment instanceof ChatActivity) {
                        final ChatActivity chatActivity = (ChatActivity) baseFragment;
                        AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.export.ui.ChatViewer$$ExternalSyntheticLambda11
                            @Override // java.lang.Runnable
                            public final void run() {
                                ChatViewer.this.lambda$processSelectedOption$10(chatActivity, i3);
                            }
                        }, 300L);
                    }
                }
                finishPreviewFragment();
                break;
        }
        if (i != 204) {
            closeMenu();
            this.selectedObject = null;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$processSelectedOption$10(ChatActivity chatActivity, int i) {
        finishFragment();
        chatActivity.jumpToDate(i);
    }

    private int getMessageType(MessageObject messageObject) {
        int i;
        String str;
        if (messageObject == null || (i = messageObject.type) == 6) {
            return -1;
        }
        if (i == 10 || i == 11 || i == 16) {
            return messageObject.getId() == 0 ? -1 : 1;
        }
        if (messageObject.isVoice()) {
            return 2;
        }
        if (messageObject.isSticker() || messageObject.isAnimatedSticker()) {
            TLRPC.InputStickerSet inputStickerSet = messageObject.getInputStickerSet();
            if (inputStickerSet instanceof TLRPC.TL_inputStickerSetID) {
                if (!MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.id)) {
                    return 7;
                }
            } else if ((inputStickerSet instanceof TLRPC.TL_inputStickerSetShortName) && !MediaDataController.getInstance(this.currentAccount).isStickerPackInstalled(inputStickerSet.short_name)) {
                return 7;
            }
        } else if ((!messageObject.isRoundVideo() || (messageObject.isRoundVideo() && BuildVars.DEBUG_VERSION)) && ((messageObject.messageOwner.media instanceof TLRPC.TL_messageMediaPhoto) || messageObject.getDocument() != null || messageObject.isMusic() || messageObject.isVideo())) {
            String str2 = messageObject.messageOwner.attachPath;
            boolean z = (str2 == null || str2.length() == 0 || !new File(messageObject.messageOwner.attachPath).exists()) ? false : true;
            if ((z || !getFileLoader().getPathToMessage(messageObject.messageOwner).exists()) ? z : true) {
                if (messageObject.getDocument() == null || (str = messageObject.getDocument().mime_type) == null) {
                    return 4;
                }
                if (messageObject.getDocumentName().toLowerCase().endsWith("attheme")) {
                    return 10;
                }
                if (str.endsWith("/xml")) {
                    return 5;
                }
                return (str.endsWith("/png") || str.endsWith("/jpg") || str.endsWith("/jpeg")) ? 6 : 4;
            }
        } else {
            if (messageObject.type == 12) {
                return 8;
            }
            if (messageObject.isMediaEmpty()) {
                return 3;
            }
        }
        return 2;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onRemoveFromParent() {
        MediaController.getInstance().setTextureView(this.videoTextureView, null, null, false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void hideFloatingDateView(boolean z) {
        if (this.floatingDateView.getTag() == null || this.currentFloatingDateOnScreen) {
            return;
        }
        if (!this.scrollingFloatingDate || this.currentFloatingTopIsNotMessage) {
            this.floatingDateView.setTag(null);
            if (z) {
                AnimatorSet animatorSet = new AnimatorSet();
                this.floatingDateAnimation = animatorSet;
                animatorSet.setDuration(150L);
                this.floatingDateAnimation.playTogether(ObjectAnimator.ofFloat(this.floatingDateView, "alpha", 0.0f));
                this.floatingDateAnimation.addListener(new AnimatorListenerAdapter() { // from class: com.exteragram.messenger.export.ui.ChatViewer.14
                    @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                    public void onAnimationEnd(Animator animator) {
                        if (animator.equals(ChatViewer.this.floatingDateAnimation)) {
                            ChatViewer.this.floatingDateAnimation = null;
                        }
                    }
                });
                this.floatingDateAnimation.setStartDelay(500L);
                this.floatingDateAnimation.start();
                return;
            }
            AnimatorSet animatorSet2 = this.floatingDateAnimation;
            if (animatorSet2 != null) {
                animatorSet2.cancel();
                this.floatingDateAnimation = null;
            }
            this.floatingDateView.setAlpha(0.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkScrollForLoad(boolean z) {
        LinearLayoutManager linearLayoutManager = this.chatLayoutManager;
        if (linearLayoutManager == null || this.paused) {
            return;
        }
        int iFindFirstVisibleItemPosition = linearLayoutManager.findFirstVisibleItemPosition();
        if ((iFindFirstVisibleItemPosition == -1 ? 0 : Math.abs(this.chatLayoutManager.findLastVisibleItemPosition() - iFindFirstVisibleItemPosition) + 1) > 0) {
            if (iFindFirstVisibleItemPosition > (z ? 25 : 5) || this.loading || this.endReached) {
                return;
            }
            loadMessages(false);
        }
    }

    private void updateTextureViewPosition() {
        boolean z;
        int childCount = this.chatListView.getChildCount();
        int i = 0;
        while (true) {
            if (i >= childCount) {
                z = false;
                break;
            }
            View childAt = this.chatListView.getChildAt(i);
            if (childAt instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (this.roundVideoContainer != null && messageObject.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject)) {
                    ImageReceiver photoImage = chatMessageCell.getPhotoImage();
                    this.roundVideoContainer.setTranslationX(photoImage.getImageX());
                    this.roundVideoContainer.setTranslationY(this.fragmentView.getPaddingTop() + chatMessageCell.getTop() + photoImage.getImageY());
                    this.fragmentView.invalidate();
                    this.roundVideoContainer.invalidate();
                    z = true;
                    break;
                }
            }
            i++;
        }
        if (this.roundVideoContainer != null) {
            MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
            if (!z) {
                this.roundVideoContainer.setTranslationY((-AndroidUtilities.roundMessageSize) - 100);
                this.fragmentView.invalidate();
                if (playingMessageObject == null || !playingMessageObject.isRoundVideo()) {
                    return;
                }
                if (this.checkTextureViewPosition || PipRoundVideoView.getInstance() != null) {
                    MediaController.getInstance().setCurrentVideoVisible(false);
                    return;
                }
                return;
            }
            MediaController.getInstance().setCurrentVideoVisible(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateMessagesVisiblePart() {
        MessageObject messageObject;
        RecyclerListView recyclerListView = this.chatListView;
        if (recyclerListView == null) {
            return;
        }
        int childCount = recyclerListView.getChildCount();
        int measuredHeight = this.chatListView.getMeasuredHeight();
        int i = Integer.MAX_VALUE;
        boolean z = false;
        int i2 = Integer.MAX_VALUE;
        boolean z2 = false;
        View view = null;
        View view2 = null;
        View view3 = null;
        for (int i3 = 0; i3 < childCount; i3++) {
            View childAt = this.chatListView.getChildAt(i3);
            if (childAt instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                int top = chatMessageCell.getTop();
                int i4 = top >= 0 ? 0 : -top;
                int measuredHeight2 = chatMessageCell.getMeasuredHeight();
                if (measuredHeight2 > measuredHeight) {
                    measuredHeight2 = i4 + measuredHeight;
                }
                chatMessageCell.setVisiblePart(i4, measuredHeight2 - i4, this.contentView.getHeightWithKeyboard() - this.chatListView.getTop(), 0.0f, (childAt.getY() + this.actionBar.getMeasuredHeight()) - this.contentView.getBackgroundTranslationY(), this.contentView.getMeasuredWidth(), this.contentView.getBackgroundSizeY(), 0, 0, 0);
                chatMessageCell.invalidate();
                MessageObject messageObject2 = chatMessageCell.getMessageObject();
                if (this.roundVideoContainer != null && messageObject2.isRoundVideo() && MediaController.getInstance().isPlayingMessage(messageObject2)) {
                    ImageReceiver photoImage = chatMessageCell.getPhotoImage();
                    this.roundVideoContainer.setTranslationX(photoImage.getImageX());
                    this.roundVideoContainer.setTranslationY(this.fragmentView.getPaddingTop() + top + photoImage.getImageY());
                    this.fragmentView.invalidate();
                    this.roundVideoContainer.invalidate();
                    z2 = true;
                }
            } else if (childAt instanceof ChatActionCell) {
                ChatActionCell chatActionCell = (ChatActionCell) childAt;
                chatActionCell.setVisiblePart((childAt.getY() + this.actionBar.getMeasuredHeight()) - this.contentView.getBackgroundTranslationY(), this.contentView.getBackgroundSizeY());
                if (chatActionCell.hasGradientService()) {
                    chatActionCell.invalidate();
                }
            }
            if (childAt.getBottom() > this.chatListView.getPaddingTop()) {
                int bottom = childAt.getBottom();
                if (bottom < i) {
                    if ((childAt instanceof ChatMessageCell) || (childAt instanceof ChatActionCell)) {
                        view = childAt;
                    }
                    i = bottom;
                    view3 = childAt;
                }
                ChatListItemAnimator chatListItemAnimator = this.chatListItemAnimator;
                if ((chatListItemAnimator == null || (!chatListItemAnimator.willRemoved(childAt) && !this.chatListItemAnimator.willAddedFromAlpha(childAt))) && (childAt instanceof ChatActionCell) && ((ChatActionCell) childAt).getMessageObject().isDateObject) {
                    if (childAt.getAlpha() != 1.0f) {
                        childAt.setAlpha(1.0f);
                    }
                    if (bottom < i2) {
                        i2 = bottom;
                        view2 = childAt;
                    }
                }
            }
        }
        FrameLayout frameLayout = this.roundVideoContainer;
        if (frameLayout != null) {
            if (!z2) {
                frameLayout.setTranslationY((-AndroidUtilities.roundMessageSize) - 100);
                this.fragmentView.invalidate();
                MessageObject playingMessageObject = MediaController.getInstance().getPlayingMessageObject();
                if (playingMessageObject != null && playingMessageObject.isRoundVideo() && this.checkTextureViewPosition) {
                    MediaController.getInstance().setCurrentVideoVisible(false);
                }
            } else {
                MediaController.getInstance().setCurrentVideoVisible(true);
            }
        }
        if (view != null) {
            if (view instanceof ChatMessageCell) {
                messageObject = ((ChatMessageCell) view).getMessageObject();
            } else {
                messageObject = ((ChatActionCell) view).getMessageObject();
            }
            this.floatingDateView.setCustomDate(messageObject.messageOwner.date, false, true);
        }
        this.currentFloatingDateOnScreen = false;
        if (!(view3 instanceof ChatMessageCell) && !(view3 instanceof ChatActionCell)) {
            z = true;
        }
        this.currentFloatingTopIsNotMessage = z;
        if (view2 != null) {
            if (view2.getTop() > this.chatListView.getPaddingTop() || this.currentFloatingTopIsNotMessage) {
                if (view2.getAlpha() != 1.0f) {
                    view2.setAlpha(1.0f);
                }
                hideFloatingDateView(true ^ this.currentFloatingTopIsNotMessage);
            } else {
                if (view2.getAlpha() != 0.0f) {
                    view2.setAlpha(0.0f);
                }
                AnimatorSet animatorSet = this.floatingDateAnimation;
                if (animatorSet != null) {
                    animatorSet.cancel();
                    this.floatingDateAnimation = null;
                }
                if (this.floatingDateView.getTag() == null) {
                    this.floatingDateView.setTag(1);
                }
                if (this.floatingDateView.getAlpha() != 1.0f) {
                    this.floatingDateView.setAlpha(1.0f);
                }
                this.currentFloatingDateOnScreen = true;
            }
            int bottom2 = view2.getBottom() - this.chatListView.getPaddingTop();
            if (bottom2 > this.floatingDateView.getMeasuredHeight() && bottom2 < this.floatingDateView.getMeasuredHeight() * 2) {
                ChatActionCell chatActionCell2 = this.floatingDateView;
                chatActionCell2.setTranslationY(((-chatActionCell2.getMeasuredHeight()) * 2) + bottom2);
                return;
            } else {
                this.floatingDateView.setTranslationY(0.0f);
                return;
            }
        }
        hideFloatingDateView(true);
        this.floatingDateView.setTranslationY(0.0f);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        if (z) {
            this.notificationsLocker.lock();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        if (z) {
            this.notificationsLocker.unlock();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        super.onResume();
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.onResume();
        }
        this.paused = false;
        checkScrollForLoad(false);
        if (this.wasPaused) {
            this.wasPaused = false;
            ChatActivityAdapter chatActivityAdapter = this.chatAdapter;
            if (chatActivityAdapter != null) {
                chatActivityAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        SizeNotifierFrameLayout sizeNotifierFrameLayout = this.contentView;
        if (sizeNotifierFrameLayout != null) {
            sizeNotifierFrameLayout.onPause();
        }
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
        this.paused = true;
        this.wasPaused = true;
        if (AvatarPreviewer.hasVisibleInstance()) {
            AvatarPreviewer.getInstance().close();
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        UndoView undoView = this.undoView;
        if (undoView != null) {
            undoView.hide(true, 0);
        }
    }

    public void openVCard(TLRPC.User user, String str, String str2, String str3) {
        try {
            File sharingDirectory = AndroidUtilities.getSharingDirectory();
            sharingDirectory.mkdirs();
            File file = new File(sharingDirectory, "vcard.vcf");
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(str);
            bufferedWriter.close();
            showDialog(new PhonebookShareAlert(this, null, user, null, file, str2, str3));
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onConfigurationChanged(Configuration configuration) {
        Dialog dialog = this.visibleDialog;
        if (dialog instanceof DatePickerDialog) {
            dialog.dismiss();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void alertUserOpenError(MessageObject messageObject) {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.AppName));
        builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
        if (messageObject.type == 3) {
            builder.setMessage(LocaleController.getString(R.string.NoPlayerInstalled));
        } else {
            builder.setMessage(LocaleController.formatString(R.string.NoHandleAppInstalled, messageObject.getDocument().mime_type));
        }
        showDialog(builder.create());
    }

    public void showOpenUrlAlert(final String str, boolean z) {
        if (Browser.isInternalUrl(str, null) || !z) {
            Browser.openUrl((Context) getParentActivity(), str, true);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
        builder.setTitle(LocaleController.getString(R.string.OpenUrlTitle));
        builder.setMessage(LocaleController.formatString(R.string.OpenUrlAlert2, str));
        builder.setPositiveButton(LocaleController.getString(R.string.Open), new AlertDialog.OnButtonClickListener() { // from class: com.exteragram.messenger.export.ui.ChatViewer$$ExternalSyntheticLambda10
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                ChatViewer.this.lambda$showOpenUrlAlert$11(str, alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$showOpenUrlAlert$11(String str, AlertDialog alertDialog, int i) {
        Browser.openUrl((Context) getParentActivity(), str, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    @SuppressLint({"NotifyDataSetChanged"})
    public void updateMessageAnimatedInternal(MessageObject messageObject, boolean z) {
        if (this.chatAdapter == null || this.fragmentView == null) {
            return;
        }
        MessageObject messageObject2 = this.messagesDict.get(messageObject.getId());
        if (z) {
            messageObject.forceUpdate = true;
            messageObject.reactionsChanged = true;
        }
        int iIndexOf = this.messages.indexOf(messageObject2);
        if (iIndexOf >= 0) {
            this.chatAdapter.notifyItemChanged((this.messages.size() - (iIndexOf - this.chatAdapter.messagesStartRow)) - 1);
        }
    }

    public class ChatActivityAdapter extends RecyclerView.Adapter {
        private int loadingUpRow;
        private final Context mContext;
        private int messagesEndRow;
        private int messagesStartRow;
        private int rowCount;

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            return -1L;
        }

        public ChatActivityAdapter(Context context) {
            this.mContext = context;
        }

        public void updateRows() {
            this.rowCount = 0;
            if (!ChatViewer.this.messages.isEmpty()) {
                if (!ChatViewer.this.endReached) {
                    int i = this.rowCount;
                    this.rowCount = i + 1;
                    this.loadingUpRow = i;
                } else {
                    this.loadingUpRow = -1;
                }
                int i2 = this.rowCount;
                this.messagesStartRow = i2;
                int size = i2 + ChatViewer.this.messages.size();
                this.rowCount = size;
                this.messagesEndRow = size;
                return;
            }
            this.loadingUpRow = -1;
            this.messagesStartRow = -1;
            this.messagesEndRow = -1;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            return this.rowCount;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View chatLoadingCell;
            View chatMessageCell;
            boolean z = false;
            if (i == 0) {
                if (!ChatViewer.this.chatMessageCellsCache.isEmpty()) {
                    View view = (View) ChatViewer.this.chatMessageCellsCache.get(0);
                    ChatViewer.this.chatMessageCellsCache.remove(0);
                    chatMessageCell = view;
                } else {
                    chatMessageCell = new ChatMessageCell(this.mContext, ChatViewer.this.getCurrentAccount());
                }
                ChatMessageCell chatMessageCell2 = (ChatMessageCell) chatMessageCell;
                chatMessageCell2.setDelegate(new AnonymousClass1());
                chatMessageCell2.setAllowAssistant(true);
                chatLoadingCell = chatMessageCell;
            } else if (i == 1) {
                ChatActionCell chatActionCell = new ChatActionCell(this.mContext, z, ChatViewer.this.theme) { // from class: com.exteragram.messenger.export.ui.ChatViewer.ChatActivityAdapter.2
                    @Override // org.telegram.ui.Cells.ChatActionCell, android.view.View
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                        accessibilityNodeInfo.setVisibleToUser(true);
                    }
                };
                chatActionCell.setDelegate(new ChatActionCell.ChatActionCellDelegate() { // from class: com.exteragram.messenger.export.ui.ChatViewer.ChatActivityAdapter.3
                    @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
                    public void didPressReplyMessage(ChatActionCell chatActionCell2, int i2) {
                    }

                    @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
                    public void didClickImage(ChatActionCell chatActionCell2) {
                        MessageObject messageObject = chatActionCell2.getMessageObject();
                        PhotoViewer.getInstance().setParentActivity(ChatViewer.this);
                        TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 640);
                        if (closestPhotoSizeWithSize != null) {
                            PhotoViewer.getInstance().openPhoto(closestPhotoSizeWithSize.location, ImageLocation.getForPhoto(closestPhotoSizeWithSize, messageObject.messageOwner.action.photo), ChatViewer.this.provider);
                        } else {
                            PhotoViewer.getInstance().openPhoto(messageObject, (ChatActivity) null, 0L, 0L, 0L, ChatViewer.this.provider);
                        }
                    }

                    @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
                    public boolean didLongPress(ChatActionCell chatActionCell2, float f, float f2) {
                        return ChatViewer.this.createMenu(chatActionCell2);
                    }

                    @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
                    public void needOpenUserProfile(long j) {
                        if (j < 0) {
                            Bundle bundle = new Bundle();
                            bundle.putLong("chat_id", -j);
                            if (MessagesController.getInstance(ChatViewer.this.getCurrentAccount()).checkCanOpenChat(bundle, ChatViewer.this)) {
                                ChatViewer.this.presentFragment(new ChatActivity(bundle), true);
                                return;
                            }
                            return;
                        }
                        if (j != UserConfig.getInstance(ChatViewer.this.getCurrentAccount()).getClientUserId()) {
                            ProfileActivity profileActivity = new ProfileActivity(new Bundle());
                            profileActivity.setPlayProfileAnimation(0);
                            ChatViewer.this.presentFragment(profileActivity);
                        }
                    }

                    @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
                    public BaseFragment getBaseFragment() {
                        return ChatViewer.this;
                    }

                    @Override // org.telegram.ui.Cells.ChatActionCell.ChatActionCellDelegate
                    public long getDialogId() {
                        return ChatViewer.this.getDialogId();
                    }
                });
                chatLoadingCell = chatActionCell;
            } else if (i == 2) {
                chatLoadingCell = new ChatUnreadCell(this.mContext, ChatViewer.this.theme);
            } else {
                chatLoadingCell = new ChatLoadingCell(this.mContext, ChatViewer.this.contentView, ChatViewer.this.theme);
            }
            chatLoadingCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(chatLoadingCell);
        }

        /* JADX INFO: renamed from: com.exteragram.messenger.export.ui.ChatViewer$ChatActivityAdapter$1, reason: invalid class name */
        public class AnonymousClass1 implements ChatMessageCell.ChatMessageCellDelegate {
            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean canPerformActions() {
                return true;
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean canPerformReply() {
                return false;
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean doNotShowLoadingReply(MessageObject messageObject) {
                return true;
            }

            public AnonymousClass1() {
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void forceUpdate(ChatMessageCell chatMessageCell, boolean z) {
                MessageObject primaryMessageObject;
                if (chatMessageCell == null || (primaryMessageObject = chatMessageCell.getPrimaryMessageObject()) == null) {
                    return;
                }
                primaryMessageObject.forceUpdate = true;
                ChatViewer.this.updateMessageAnimatedInternal(primaryMessageObject, false);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressCodeCopy(ChatMessageCell chatMessageCell, MessageObject.TextLayoutBlock textLayoutBlock) {
                StaticLayout staticLayout;
                if (textLayoutBlock == null || (staticLayout = textLayoutBlock.textLayout) == null || staticLayout.getText() == null) {
                    return;
                }
                String string = textLayoutBlock.textLayout.getText().toString();
                SpannableString spannableString = new SpannableString(string);
                spannableString.setSpan(new CodeHighlighting.Span(false, 0, null, textLayoutBlock.language, string), 0, spannableString.length(), 33);
                AndroidUtilities.addToClipboard(spannableString);
                BulletinFactory.of(ChatViewer.this).createCopyBulletin(LocaleController.getString(R.string.CodeCopied)).show();
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressSideButton(ChatMessageCell chatMessageCell) {
                if (ChatViewer.this.getParentActivity() == null) {
                    return;
                }
                ChatActivityAdapter chatActivityAdapter = ChatActivityAdapter.this;
                ChatViewer.this.showDialog(ShareAlert.createShareAlert(chatActivityAdapter.mContext, chatMessageCell.getMessageObject(), null, ChatObject.isChannel(ChatViewer.this.currentChat) && !ChatViewer.this.currentChat.megagroup, null, false));
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean needPlayMessage(ChatMessageCell chatMessageCell, MessageObject messageObject, boolean z) {
                if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                    boolean zPlayMessage = MediaController.getInstance().playMessage(messageObject, z);
                    MediaController.getInstance().setVoiceMessagesPlaylist(null, false);
                    return zPlayMessage;
                }
                if (messageObject.isMusic()) {
                    return MediaController.getInstance().setPlaylist(ChatViewer.this.messages, messageObject, 0L);
                }
                return false;
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2, boolean z) {
                if (chat == null || chat == ChatViewer.this.currentChat) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putLong("chat_id", chat.id);
                if (i != 0) {
                    bundle.putInt("message_id", i);
                }
                if (MessagesController.getInstance(ChatViewer.this.getCurrentAccount()).checkCanOpenChat(bundle, ChatViewer.this)) {
                    ChatViewer.this.presentFragment(new ChatActivity(bundle), true);
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                ChatViewer.this.createMenu(chatMessageCell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2, boolean z) {
                if (user == null || user.id == UserConfig.getInstance(ChatViewer.this.getCurrentAccount()).getClientUserId()) {
                    return;
                }
                openProfile(user);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean didLongPressUserAvatar(final ChatMessageCell chatMessageCell, final TLRPC.User user, float f, float f2) {
                AvatarPreviewer.Data dataOf;
                if (user == null || user.id == UserConfig.getInstance(ChatViewer.this.getCurrentAccount()).getClientUserId()) {
                    return false;
                }
                TLRPC.User user2 = ChatViewer.this.getMessagesController().getUser(Long.valueOf(user.id));
                final TLRPC.User targetUser = user2 != null ? user2 : user;
                AvatarPreviewer.MenuItem[] menuItemArr = {AvatarPreviewer.MenuItem.OPEN_PROFILE, AvatarPreviewer.MenuItem.SEND_MESSAGE};
                TLRPC.UserFull userFull = ChatViewer.this.getMessagesController().getUserFull(targetUser.id);
                if (userFull == null) {
                    dataOf = AvatarPreviewer.Data.of(targetUser, ChatViewer.this.getClassGuid(), menuItemArr);
                } else {
                    dataOf = AvatarPreviewer.Data.of(targetUser, userFull, menuItemArr);
                }
                if (!AvatarPreviewer.canPreview(dataOf)) {
                    return false;
                }
                AvatarPreviewer avatarPreviewer = AvatarPreviewer.getInstance();
                ChatViewer chatViewer = ChatViewer.this;
                avatarPreviewer.show((ViewGroup) chatViewer.fragmentView, chatViewer.getResourceProvider(), dataOf, new AvatarPreviewer.Callback() { // from class: com.exteragram.messenger.export.ui.ChatViewer$ChatActivityAdapter$1$$ExternalSyntheticLambda2
                    @Override // org.telegram.ui.AvatarPreviewer.Callback
                    public final void onMenuClick(AvatarPreviewer.MenuItem menuItem) {
                        AnonymousClass1.this.lambda$didLongPressUserAvatar$0(chatMessageCell, targetUser, menuItem);
                    }
                });
                return true;
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$didLongPressUserAvatar$0(ChatMessageCell chatMessageCell, TLRPC.User user, AvatarPreviewer.MenuItem menuItem) {
                int i = AnonymousClass15.$SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem[menuItem.ordinal()];
                if (i == 1) {
                    openDialog(chatMessageCell, user);
                } else {
                    if (i != 2) {
                        return;
                    }
                    openProfile(user);
                }
            }

            private void openProfile(TLRPC.User user) {
                Bundle bundle = new Bundle();
                bundle.putLong("user_id", user.id);
                ProfileActivity profileActivity = new ProfileActivity(bundle);
                profileActivity.setPlayProfileAnimation(0);
                ChatViewer.this.presentFragment(profileActivity);
            }

            private void openDialog(ChatMessageCell chatMessageCell, TLRPC.User user) {
                if (user != null) {
                    Bundle bundle = new Bundle();
                    bundle.putLong("user_id", user.id);
                    if (ChatViewer.this.getMessagesController().checkCanOpenChat(bundle, ChatViewer.this)) {
                        ChatViewer.this.presentFragment(new ChatActivity(bundle));
                    }
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didLongPress(ChatMessageCell chatMessageCell, float f, float f2) {
                ChatViewer.this.createMenu(chatMessageCell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressUrl(ChatMessageCell chatMessageCell, CharacterStyle characterStyle, boolean z) {
                TLRPC.WebPage webPage;
                if (characterStyle == null) {
                    return;
                }
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (characterStyle instanceof URLSpanMono) {
                    ((URLSpanMono) characterStyle).copyToClipboard();
                    if (AndroidUtilities.shouldShowClipboardToast()) {
                        Toast.makeText(ChatViewer.this.getParentActivity(), LocaleController.getString(R.string.TextCopied), 0).show();
                    }
                } else if (characterStyle instanceof URLSpanUserMention) {
                    Long l = Utilities.parseLong(((URLSpanUserMention) characterStyle).getURL());
                    long jLongValue = l.longValue();
                    ChatActivityAdapter chatActivityAdapter = ChatActivityAdapter.this;
                    if (jLongValue > 0) {
                        TLRPC.User user = MessagesController.getInstance(ChatViewer.this.getCurrentAccount()).getUser(l);
                        if (user != null) {
                            MessagesController.getInstance(ChatViewer.this.getCurrentAccount()).openChatOrProfileWith(user, null, ChatViewer.this, 0, false);
                        }
                    } else {
                        TLRPC.Chat chat = MessagesController.getInstance(ChatViewer.this.getCurrentAccount()).getChat(Long.valueOf(-jLongValue));
                        if (chat != null) {
                            MessagesController.getInstance(ChatViewer.this.getCurrentAccount()).openChatOrProfileWith(null, chat, ChatViewer.this, 0, false);
                        }
                    }
                } else if (characterStyle instanceof URLSpanNoUnderline) {
                    String url = ((URLSpanNoUnderline) characterStyle).getURL();
                    if (url.startsWith("@")) {
                        MessagesController.getInstance(ChatViewer.this.getCurrentAccount()).openByUserName(url.substring(1), ChatViewer.this, 0);
                    } else if (url.startsWith("#")) {
                        DialogsActivity dialogsActivity = new DialogsActivity(null);
                        dialogsActivity.setSearchString(url);
                        ChatViewer.this.presentFragment(dialogsActivity);
                    }
                } else {
                    final String url2 = ((URLSpan) characterStyle).getURL();
                    if (z) {
                        BottomSheet.Builder builder = new BottomSheet.Builder(ChatViewer.this.getParentActivity());
                        builder.setTitle(url2);
                        builder.setItems(new CharSequence[]{LocaleController.getString(R.string.Open), LocaleController.getString(R.string.Copy)}, new DialogInterface.OnClickListener() { // from class: com.exteragram.messenger.export.ui.ChatViewer$ChatActivityAdapter$1$$ExternalSyntheticLambda1
                            @Override // android.content.DialogInterface.OnClickListener
                            public final void onClick(DialogInterface dialogInterface, int i) {
                                AnonymousClass1.this.lambda$didPressUrl$1(url2, dialogInterface, i);
                            }
                        });
                        ChatViewer.this.showDialog(builder.create());
                    } else if (characterStyle instanceof URLSpanReplacement) {
                        ChatViewer.this.showOpenUrlAlert(((URLSpanReplacement) characterStyle).getURL(), true);
                    } else {
                        TLRPC.MessageMedia messageMedia = messageObject.messageOwner.media;
                        if ((messageMedia instanceof TLRPC.TL_messageMediaWebPage) && (webPage = messageMedia.webpage) != null && webPage.cached_page != null) {
                            String lowerCase = url2.toLowerCase();
                            String lowerCase2 = messageObject.messageOwner.media.webpage.url.toLowerCase();
                            if ((Browser.isTelegraphUrl(lowerCase, false) || lowerCase.contains("t.me/iv")) && (lowerCase.contains(lowerCase2) || lowerCase2.contains(lowerCase))) {
                                ArticleViewer.getInstance().setParentActivity(ChatViewer.this.getParentActivity(), ChatViewer.this);
                                ArticleViewer.getInstance().open(messageObject);
                                return;
                            }
                        }
                        Browser.openUrl((Context) ChatViewer.this.getParentActivity(), url2, true);
                    }
                }
                if (z) {
                    chatMessageCell.resetPressedLink(-1);
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$didPressUrl$1(String str, DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Browser.openUrl((Context) ChatViewer.this.getParentActivity(), str, true);
                    return;
                }
                if (i == 1) {
                    if (str.startsWith("mailto:")) {
                        str = str.substring(7);
                    } else if (str.startsWith("tel:")) {
                        str = str.substring(4);
                    }
                    AndroidUtilities.addToClipboard(str);
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void needOpenWebView(MessageObject messageObject, String str, String str2, String str3, String str4, int i, int i2) {
                ChatViewer chatViewer = ChatViewer.this;
                EmbedBottomSheet.show(chatViewer, messageObject, chatViewer.provider, str2, str3, str4, str, i, i2, false);
            }

            /* JADX WARN: Code duplicated, block: B:49:0x010f  */
            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressImage(ChatMessageCell chatMessageCell, float f, float f2, boolean z) {
                int i;
                File file;
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (messageObject.getInputStickerSet() != null) {
                    ChatViewer.this.showDialog(new StickersAlert(ChatViewer.this.getParentActivity(), ChatViewer.this, messageObject.getInputStickerSet(), null, null, false));
                    return;
                }
                if (messageObject.isVideo() || (i = messageObject.type) == 1 || ((i == 0 && !messageObject.isWebpageDocument()) || messageObject.isGif())) {
                    PhotoViewer.getInstance().setParentActivity(ChatViewer.this);
                    PhotoViewer.getInstance().openPhoto(messageObject, (ChatActivity) null, 0L, 0L, 0L, ChatViewer.this.provider);
                    return;
                }
                int i2 = messageObject.type;
                File pathToMessage = null;
                if (i2 == 3) {
                    try {
                        String str = messageObject.messageOwner.attachPath;
                        if (str != null && str.length() != 0) {
                            pathToMessage = new File(messageObject.messageOwner.attachPath);
                        }
                        if (pathToMessage == null || !pathToMessage.exists()) {
                            pathToMessage = ChatViewer.this.getFileLoader().getPathToMessage(messageObject.messageOwner);
                        }
                        Intent intent = new Intent("android.intent.action.VIEW");
                        intent.setFlags(1);
                        intent.setDataAndType(FileProvider.getUriForFile(ChatViewer.this.getParentActivity(), ApplicationLoader.getApplicationId() + ".provider", pathToMessage), "video/mp4");
                        ChatViewer.this.getParentActivity().startActivityForResult(intent, 500);
                        return;
                    } catch (Exception unused) {
                        ChatViewer.this.alertUserOpenError(messageObject);
                        return;
                    }
                }
                if (i2 == 4) {
                    if (AndroidUtilities.isMapsInstalled(ChatViewer.this)) {
                        LocationActivity locationActivity = new LocationActivity(0);
                        locationActivity.setMessageObject(messageObject);
                        ChatViewer.this.presentFragment(locationActivity);
                        return;
                    }
                    return;
                }
                if (i2 == 9 || i2 == 0) {
                    if (messageObject.getDocumentName().toLowerCase().endsWith("attheme")) {
                        String str2 = messageObject.messageOwner.attachPath;
                        if (str2 == null || str2.length() == 0) {
                            file = null;
                        } else {
                            file = new File(messageObject.messageOwner.attachPath);
                            if (!file.exists()) {
                                file = null;
                            }
                        }
                        if (file == null) {
                            File pathToMessage2 = ChatViewer.this.getFileLoader().getPathToMessage(messageObject.messageOwner);
                            if (pathToMessage2.exists()) {
                                file = pathToMessage2;
                            }
                        }
                        if (ChatViewer.this.chatLayoutManager != null) {
                            int iFindLastVisibleItemPosition = ChatViewer.this.chatLayoutManager.findLastVisibleItemPosition();
                            int itemCount = ChatViewer.this.chatLayoutManager.getItemCount() - 1;
                            ChatActivityAdapter chatActivityAdapter = ChatActivityAdapter.this;
                            if (iFindLastVisibleItemPosition < itemCount) {
                                ChatViewer chatViewer = ChatViewer.this;
                                chatViewer.scrollToPositionOnRecreate = chatViewer.chatLayoutManager.findFirstVisibleItemPosition();
                                RecyclerListView.Holder holder = (RecyclerListView.Holder) ChatViewer.this.chatListView.findViewHolderForAdapterPosition(ChatViewer.this.scrollToPositionOnRecreate);
                                ChatActivityAdapter chatActivityAdapter2 = ChatActivityAdapter.this;
                                if (holder != null) {
                                    ChatViewer.this.scrollToOffsetOnRecreate = holder.itemView.getTop();
                                } else {
                                    ChatViewer.this.scrollToPositionOnRecreate = -1;
                                }
                            } else {
                                ChatViewer.this.scrollToPositionOnRecreate = -1;
                            }
                        }
                        Theme.ThemeInfo themeInfoApplyThemeFile = Theme.applyThemeFile(file, messageObject.getDocumentName(), null, true);
                        ChatActivityAdapter chatActivityAdapter3 = ChatActivityAdapter.this;
                        if (themeInfoApplyThemeFile != null) {
                            ChatViewer.this.presentFragment(new ThemePreviewActivity(themeInfoApplyThemeFile));
                            return;
                        }
                        ChatViewer.this.scrollToPositionOnRecreate = -1;
                    }
                    try {
                        AndroidUtilities.openForView(messageObject, ChatViewer.this.getParentActivity(), null, false);
                    } catch (Exception unused2) {
                        ChatViewer.this.alertUserOpenError(messageObject);
                    }
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressInstantButton(ChatMessageCell chatMessageCell, int i) {
                TLRPC.WebPage webPage;
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (i == 0) {
                    TLRPC.MessageMedia messageMedia = messageObject.messageOwner.media;
                    if (messageMedia == null || (webPage = messageMedia.webpage) == null || webPage.cached_page == null) {
                        return;
                    }
                    ArticleViewer.getInstance().setParentActivity(ChatViewer.this.getParentActivity(), ChatViewer.this);
                    ArticleViewer.getInstance().open(messageObject);
                    return;
                }
                if (i == 5) {
                    ChatViewer chatViewer = ChatViewer.this;
                    TLRPC.User user = chatViewer.getMessagesController().getUser(Long.valueOf(messageObject.messageOwner.media.user_id));
                    TLRPC.MessageMedia messageMedia2 = messageObject.messageOwner.media;
                    chatViewer.openVCard(user, messageMedia2.vcard, messageMedia2.first_name, messageMedia2.last_name);
                    return;
                }
                TLRPC.MessageMedia messageMedia3 = messageObject.messageOwner.media;
                if (messageMedia3 == null || messageMedia3.webpage == null) {
                    return;
                }
                Browser.openUrl(ChatViewer.this.getParentActivity(), messageObject.messageOwner.media.webpage.url);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean didPressAnimatedEmoji(ChatMessageCell chatMessageCell, AnimatedEmojiSpan animatedEmojiSpan) {
                TLRPC.InputStickerSet inputStickerSet;
                if (ChatViewer.this.getMessagesController().premiumFeaturesBlocked() || animatedEmojiSpan == null || animatedEmojiSpan.standard) {
                    return false;
                }
                long documentId = animatedEmojiSpan.getDocumentId();
                TLRPC.Document documentFindDocument = animatedEmojiSpan.document;
                if (documentFindDocument == null) {
                    documentFindDocument = AnimatedEmojiDrawable.findDocument(ChatViewer.this.getCurrentAccount(), documentId);
                }
                if (documentFindDocument == null || (inputStickerSet = MessageObject.getInputStickerSet(documentFindDocument)) == null) {
                    return false;
                }
                ArrayList arrayList = new ArrayList(1);
                arrayList.add(inputStickerSet);
                ChatViewer chatViewer = ChatViewer.this;
                EmojiPacksAlert emojiPacksAlert = new EmojiPacksAlert(chatViewer, chatViewer.getParentActivity(), ChatViewer.this.theme, arrayList);
                emojiPacksAlert.setPreviewEmoji(documentFindDocument);
                emojiPacksAlert.setCalcMandatoryInsets(ChatViewer.this.contentView.getKeyboardHeight() > AndroidUtilities.dp(20.0f));
                ChatViewer.this.showDialog(emojiPacksAlert);
                return true;
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didLongPressBotButton(ChatMessageCell chatMessageCell, final TLRPC.KeyboardButton keyboardButton) {
                if ((keyboardButton instanceof TLRPC.TL_keyboardButtonSwitchInline) || (keyboardButton instanceof TLRPC.TL_keyboardButtonCallback) || (keyboardButton instanceof TLRPC.TL_keyboardButtonGame) || (keyboardButton instanceof TLRPC.TL_keyboardButtonUrl) || (keyboardButton instanceof TLRPC.TL_keyboardButtonBuy) || (keyboardButton instanceof TLRPC.TL_keyboardButtonUrlAuth) || (keyboardButton instanceof TLRPC.TL_keyboardButtonUserProfile) || (keyboardButton instanceof TLRPC.TL_keyboardButtonCopy) || (keyboardButton instanceof TLRPC.TL_keyboardButtonWebView)) {
                    if (keyboardButton instanceof TLRPC.TL_keyboardButtonCopy) {
                        didLongPressCopyButton(((TLRPC.TL_keyboardButtonCopy) keyboardButton).copy_text);
                        return;
                    }
                    BottomSheet.Builder builder = new BottomSheet.Builder(ChatViewer.this.getParentActivity(), false, ChatViewer.this.theme);
                    builder.setTitle(keyboardButton.text);
                    builder.setItems(new CharSequence[]{LocaleController.getString(R.string.CopyTitle), keyboardButton.data != null ? LocaleController.getString(R.string.CopyCallback) : null, !TextUtils.isEmpty(keyboardButton.url) ? LocaleController.getString(R.string.CopyLink) : null, keyboardButton.query != null ? LocaleController.getString(R.string.CopyInlineQuery) : null, keyboardButton.user_id != 0 ? LocaleController.getString(R.string.CopyID) : null}, new DialogInterface.OnClickListener() { // from class: com.exteragram.messenger.export.ui.ChatViewer$ChatActivityAdapter$1$$ExternalSyntheticLambda0
                        @Override // android.content.DialogInterface.OnClickListener
                        public final void onClick(DialogInterface dialogInterface, int i) {
                            AnonymousClass1.this.lambda$didLongPressBotButton$2(keyboardButton, dialogInterface, i);
                        }
                    });
                    ChatViewer.this.showDialog(builder.create());
                    try {
                        chatMessageCell.performHapticFeedback(VibratorUtils.getType(0), 1);
                    } catch (Exception unused) {
                    }
                }
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$didLongPressBotButton$2(TLRPC.KeyboardButton keyboardButton, DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    AndroidUtilities.addToClipboard(keyboardButton.text);
                } else if (i == 1) {
                    AndroidUtilities.addToClipboard(ChatUtils.getInstance().getTextFromCallback(keyboardButton.data));
                } else if (i == 2) {
                    AndroidUtilities.addToClipboard(keyboardButton.url);
                } else if (i == 3) {
                    AndroidUtilities.addToClipboard(keyboardButton.query);
                } else if (i == 4) {
                    AndroidUtilities.addToClipboard(String.valueOf(keyboardButton.user_id));
                }
                if (ChatViewer.this.undoView != null) {
                    ChatViewer.this.undoView.showWithAction(0L, 58, (Runnable) null);
                }
            }

            public void didLongPressCopyButton(final String str) {
                BottomSheet.Builder builder = new BottomSheet.Builder(ChatViewer.this.getParentActivity(), false, ChatViewer.this.theme);
                builder.setTitle(str);
                builder.setTitleMultipleLines(true);
                builder.setItems(new CharSequence[]{LocaleController.getString(R.string.Copy)}, new DialogInterface.OnClickListener() { // from class: com.exteragram.messenger.export.ui.ChatViewer$ChatActivityAdapter$1$$ExternalSyntheticLambda3
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        AnonymousClass1.this.lambda$didLongPressCopyButton$3(str, dialogInterface, i);
                    }
                });
                ChatViewer.this.showDialog(builder.create());
            }

            /* JADX INFO: Access modifiers changed from: private */
            public /* synthetic */ void lambda$didLongPressCopyButton$3(String str, DialogInterface dialogInterface, int i) {
                AndroidUtilities.addToClipboard(str);
                BulletinFactory.of(ChatViewer.this).createCopyBulletin(LocaleController.formatString(R.string.ExactTextCopied, str)).show();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            boolean z;
            boolean z2;
            if (i == this.loadingUpRow) {
                ((ChatLoadingCell) viewHolder.itemView).setProgressVisible(false);
                return;
            }
            if (i < this.messagesStartRow || i >= this.messagesEndRow) {
                return;
            }
            ArrayList<MessageObject> arrayList = ChatViewer.this.messages;
            MessageObject messageObject = arrayList.get((arrayList.size() - (i - this.messagesStartRow)) - 1);
            View view = viewHolder.itemView;
            if (view instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) view;
                chatMessageCell.isChat = false;
                int i2 = i + 1;
                int itemViewType = getItemViewType(i2);
                int itemViewType2 = getItemViewType(i - 1);
                if ((messageObject.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) || itemViewType != viewHolder.getItemViewType()) {
                    z = false;
                } else {
                    ArrayList<MessageObject> arrayList2 = ChatViewer.this.messages;
                    MessageObject messageObject2 = arrayList2.get((arrayList2.size() - (i2 - this.messagesStartRow)) - 1);
                    z = messageObject2.isOutOwner() == messageObject.isOutOwner() && messageObject2.getFromChatId() == messageObject.getFromChatId() && Math.abs(messageObject2.messageOwner.date - messageObject.messageOwner.date) <= 300;
                }
                if (itemViewType2 == viewHolder.getItemViewType()) {
                    ArrayList<MessageObject> arrayList3 = ChatViewer.this.messages;
                    MessageObject messageObject3 = arrayList3.get(arrayList3.size() - (i - this.messagesStartRow));
                    z2 = !(messageObject3.messageOwner.reply_markup instanceof TLRPC.TL_replyInlineMarkup) && messageObject3.isOutOwner() == messageObject.isOutOwner() && messageObject3.getFromChatId() == messageObject.getFromChatId() && Math.abs(messageObject3.messageOwner.date - messageObject.messageOwner.date) <= 300;
                } else {
                    z2 = false;
                }
                chatMessageCell.setMessageObject(messageObject, null, z, z2, false);
                chatMessageCell.setHighlighted(false);
                chatMessageCell.setHighlightedText(null);
                return;
            }
            if (view instanceof ChatActionCell) {
                ChatActionCell chatActionCell = (ChatActionCell) view;
                chatActionCell.setMessageObject(messageObject);
                chatActionCell.setAlpha(1.0f);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (i < this.messagesStartRow || i >= this.messagesEndRow) {
                return 4;
            }
            ArrayList<MessageObject> arrayList = ChatViewer.this.messages;
            return arrayList.get((arrayList.size() - (i - this.messagesStartRow)) - 1).contentType;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(final RecyclerView.ViewHolder viewHolder) {
            final View view = viewHolder.itemView;
            if ((view instanceof ChatMessageCell) || (view instanceof ChatActionCell)) {
                view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: com.exteragram.messenger.export.ui.ChatViewer.ChatActivityAdapter.4
                    @Override // android.view.ViewTreeObserver.OnPreDrawListener
                    public boolean onPreDraw() {
                        view.getViewTreeObserver().removeOnPreDrawListener(this);
                        int measuredHeight = ChatViewer.this.chatListView.getMeasuredHeight();
                        int top = view.getTop();
                        int i = top >= 0 ? 0 : -top;
                        int measuredHeight2 = view.getMeasuredHeight();
                        if (measuredHeight2 > measuredHeight) {
                            measuredHeight2 = i + measuredHeight;
                        }
                        View view2 = viewHolder.itemView;
                        if (view2 instanceof ChatMessageCell) {
                            ((ChatMessageCell) view).setVisiblePart(i, measuredHeight2 - i, ChatViewer.this.contentView.getHeightWithKeyboard() - ChatViewer.this.chatListView.getTop(), 0.0f, (view.getY() + ChatViewer.this.getActionBar().getMeasuredHeight()) - ChatViewer.this.contentView.getBackgroundTranslationY(), ChatViewer.this.contentView.getMeasuredWidth(), ChatViewer.this.contentView.getBackgroundSizeY(), 0, 0, 0);
                        } else if ((view2 instanceof ChatActionCell) && ChatViewer.this.getActionBar() != null && ChatViewer.this.contentView != null) {
                            View view3 = view;
                            ((ChatActionCell) view3).setVisiblePart((view3.getY() + ChatViewer.this.getActionBar().getMeasuredHeight()) - ChatViewer.this.contentView.getBackgroundTranslationY(), ChatViewer.this.contentView.getBackgroundSizeY());
                        }
                        ChatViewer.this.updateMessagesVisiblePart();
                        return true;
                    }
                });
            }
            View view2 = viewHolder.itemView;
            if (view2 instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) view2;
                chatMessageCell.setBackgroundDrawable(null);
                chatMessageCell.setCheckPressed(true, false);
                chatMessageCell.setHighlighted(false);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            updateRows();
            try {
                super.notifyDataSetChanged();
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemChanged(int i) {
            updateRows();
            try {
                super.notifyItemChanged(i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeChanged(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeChanged(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemInserted(int i) {
            updateRows();
            try {
                super.notifyItemInserted(i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemMoved(int i, int i2) {
            updateRows();
            try {
                super.notifyItemMoved(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeInserted(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeInserted(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRemoved(int i) {
            updateRows();
            try {
                super.notifyItemRemoved(i);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyItemRangeRemoved(int i, int i2) {
            updateRows();
            try {
                super.notifyItemRangeRemoved(i, i2);
            } catch (Exception e) {
                FileLog.e(e);
            }
        }
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.export.ui.ChatViewer$15, reason: invalid class name */
    public static /* synthetic */ class AnonymousClass15 {
        static final /* synthetic */ int[] $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem;

        static {
            int[] iArr = new int[AvatarPreviewer.MenuItem.values().length];
            $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem = iArr;
            try {
                iArr[AvatarPreviewer.MenuItem.SEND_MESSAGE.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$org$telegram$ui$AvatarPreviewer$MenuItem[AvatarPreviewer.MenuItem.OPEN_PROFILE.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
        }
    }

    public static QuadroResult getEntities(MessagesStorage messagesStorage, ArrayList<Long> arrayList, ArrayList<Long> arrayList2) {
        ArrayList<TLRPC.User> arrayList3 = new ArrayList<>();
        ArrayList<TLRPC.Chat> arrayList4 = new ArrayList<>();
        try {
            if (!arrayList.isEmpty()) {
                messagesStorage.getUsersInternal(arrayList, arrayList3);
            }
        } catch (Exception unused) {
        }
        try {
            if (!arrayList2.isEmpty()) {
                messagesStorage.getChatsInternal(TextUtils.join(",", arrayList2), arrayList4);
            }
        } catch (Exception unused2) {
        }
        return new QuadroResult(arrayList3, arrayList4);
    }

    public static class QuadroResult {
        private final ArrayList<TLRPC.Chat> chats;
        private LongSparseArray<TLRPC.Chat> chatsDict;
        private final ArrayList<TLRPC.User> users;
        private LongSparseArray<TLRPC.User> usersDict;

        public QuadroResult(ArrayList<TLRPC.User> arrayList, ArrayList<TLRPC.Chat> arrayList2) {
            this.users = arrayList;
            this.chats = arrayList2;
        }

        public Pair<LongSparseArray<TLRPC.User>, LongSparseArray<TLRPC.Chat>> getDicts() {
            if (this.usersDict == null && this.chatsDict == null) {
                this.usersDict = new LongSparseArray<>();
                this.chatsDict = new LongSparseArray<>();
                ArrayList<TLRPC.User> arrayList = this.users;
                int size = arrayList.size();
                int i = 0;
                int i2 = 0;
                while (i2 < size) {
                    TLRPC.User user = arrayList.get(i2);
                    i2++;
                    TLRPC.User user2 = user;
                    this.usersDict.put(user2.id, user2);
                }
                ArrayList<TLRPC.Chat> arrayList2 = this.chats;
                int size2 = arrayList2.size();
                while (i < size2) {
                    TLRPC.Chat chat = arrayList2.get(i);
                    i++;
                    TLRPC.Chat chat2 = chat;
                    this.chatsDict.put(chat2.id, chat2);
                }
            }
            return new Pair<>(this.usersDict, this.chatsDict);
        }

        public ArrayList<TLRPC.User> getUsers() {
            return this.users;
        }

        public ArrayList<TLRPC.Chat> getChats() {
            return this.chats;
        }
    }

    public static TLObject getDialogInAnyWay(long j, Integer num, boolean z) {
        TLObject dialogFromAccountNumber;
        TLObject dialogFromAccountNumber2 = getDialogFromAccountNumber(num.intValue(), j);
        if (dialogFromAccountNumber2 != null) {
            return dialogFromAccountNumber2;
        }
        for (int i = 0; i < 16; i++) {
            if (i != num.intValue() && UserConfig.isValidAccount(i) && (dialogFromAccountNumber = getDialogFromAccountNumber(i, j)) != null) {
                return dialogFromAccountNumber;
            }
        }
        if (!z) {
            return null;
        }
        TLRPC.TL_chat tL_chat = new TLRPC.TL_chat();
        tL_chat.id = j;
        tL_chat.title = "Unknown (ID: " + j + ")";
        return tL_chat;
    }

    private static TLObject getDialogFromAccountNumber(int i, long j) {
        TLObject userOrChat = MessagesController.getInstance(i).getUserOrChat(j);
        if (userOrChat != null) {
            return userOrChat;
        }
        TLRPC.User userSync = MessagesStorage.getInstance(i).getUserSync(j);
        if (userSync != null) {
            return userSync;
        }
        TLRPC.Chat chatSync = MessagesStorage.getInstance(i).getChatSync(j);
        return chatSync != null ? chatSync : MessagesStorage.getInstance(i).getChatSync(Math.abs(j));
    }

    public ActionBar getActionBar() {
        return this.actionBar;
    }

    public INavigationLayout getParentLayout() {
        return this.parentLayout;
    }
}
