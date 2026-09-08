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
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.components.MessageDetailsPopupWrapper;
import com.exteragram.messenger.export.output.FileManager;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.exteragram.messenger.utils.system.VibratorUtils;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.gms.cast.MediaError;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import kotlin.jvm.internal.LongCompanionObject;
import okhttp3.internal.url._UrlKt;
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
    private final PhotoViewer.PhotoViewerProvider provider = new PhotoViewer.EmptyPhotoViewerProvider() { 
        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z, boolean z2) {
            ChatActionCell chatActionCell;
            MessageObject messageObject2;
            MessageObject messageObject3;
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
    private final String searchQuery = _UrlKt.FRAGMENT_ENCODE_SET;
    private final ChatActivity.ThemeDelegate theme = null;
    private AtomicInteger lastLoadedMsgFileId = null;

    public ChatViewer(String str) {
        this.path = str;
        this.chatInfo = (ExportMapper.ChatInfo) ExteraConfig.getGSON().fromJson(FileManager.readFileContent(new File(str + "/info.json")), ExportMapper.ChatInfo.class);
    }

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
        Utilities.globalQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$fillMessagesCount$1();
            }
        });
    }

    public void lambda$fillMessagesCount$0() {
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
            for (File file : new File(this.path).listFiles(new FilenameFilter() { 
                @Override // java.io.FilenameFilter
                public final boolean accept(File file2, String str) {
                    return ChatViewer.m1104$r8$lambda$6DSknWxEQzdmDrzFTcnJnuhpAU(file2, str);
                }
            })) {
                Matcher matcher = Pattern.compile("(\\d+)").matcher(file.getName());
                if (matcher.find()) {
                    iMax = Math.max(iMax, Integer.parseInt(matcher.group()));
                }
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
            @Override // java.lang.Runnable
            public final void run() {
                ChatViewer.$r8$lambda$hTBgLcVY3fnv_A1Qur7zXN0vDh4(users, messagesController, chats);
            }
        });
        ArrayList<MessageObject> arrayList3 = new ArrayList<>();
        for (int i3 = 0; i3 < arrayListMapMessages.size(); i3++) {
            arrayList3.add(new MessageObject(this.currentAccount, arrayListMapMessages.get(i3).messageOwner, dicts.first, dicts.second, false, false));
        }
        return arrayList3;
    }

    public static public boolean m1105$r8$lambda$FCwMhBQq047uSYAuBP2Yay1Zkw(View view, MotionEvent motionEvent) {
        return true;
    }

    public void lambda$onAllAnimationsDone$0() {
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

    public void closeMenu() {
        ActionBarPopupWindow actionBarPopupWindow = this.scrimPopupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
        }
    }

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
        arrayList2.add(Integer.valueOf(Opcodes.SUB_DOUBLE_2ADDR));
        arrayList3.add(Integer.valueOf(R.drawable.msg_info));
        new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$createMenu$9(arrayList2, arrayList, arrayList3, view, f, f2);
            }
        }.run();
        return true;
    }

    public void lambda$createMenu$7(int i, ArrayList arrayList, Integer num, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, ActionBarMenuSubItem actionBarMenuSubItem, View view) {
        if (this.selectedObject == null || i >= arrayList.size()) {
            return;
        }
        processSelectedOption(num.intValue(), arrayList, actionBarPopupWindowLayout, actionBarMenuSubItem);
    }

    public private void processSelectedOption(int i, ArrayList<Integer> arrayList, ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout, ActionBarMenuSubItem actionBarMenuSubItem) {
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
                    getParentActivity().startActivityForResult(Intent.createChooser(intent, LocaleController.getString(R.string.ShareFile)), MediaError.DetailedErrorCode.SEGMENT_UNKNOWN);
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
                MediaController.saveFile(string4, getParentActivity(), this.selectedObject.isMusic() ? 3 : 2, documentFileName, this.selectedObject.getDocument() != null ? this.selectedObject.getDocument().mime_type : _UrlKt.FRAGMENT_ENCODE_SET);
                break;
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
                    getParentActivity().startActivityForResult(intent2, MediaError.DetailedErrorCode.SEGMENT_UNKNOWN);
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
                        AndroidUtilities.runOnUIThread(new Runnable() { 
                            @Override // java.lang.Runnable
                            public final void run() {
                                this.f$0.lambda$processSelectedOption$10(chatActivity, i3);
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

    public void lambda$showOpenUrlAlert$11(String str, AlertDialog alertDialog, int i) {
        Browser.openUrl((Context) getParentActivity(), str, true);
    }

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
                    chatMessageCell = new ChatMessageCell(this.mContext, ((BaseFragment) ChatViewer.this).currentAccount);
                }
                ChatMessageCell chatMessageCell2 = (ChatMessageCell) chatMessageCell;
                chatMessageCell2.setDelegate(new AnonymousClass1());
                chatMessageCell2.setAllowAssistant(true);
                chatLoadingCell = chatMessageCell;
            } else if (i == 1) {
                ChatActionCell chatActionCell = new ChatActionCell(this.mContext, z, ChatViewer.this.theme) { 
                    @Override // org.telegram.ui.Cells.ChatActionCell, android.view.View
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                        accessibilityNodeInfo.setVisibleToUser(true);
                    }
                };
                chatActionCell.setDelegate(new ChatActionCell.ChatActionCellDelegate() { 
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
                            if (MessagesController.getInstance(((BaseFragment) ChatViewer.this).currentAccount).checkCanOpenChat(bundle, ChatViewer.this)) {
                                ChatViewer.this.presentFragment(new ChatActivity(bundle), true);
                                return;
                            }
                            return;
                        }
                        if (j != UserConfig.getInstance(((BaseFragment) ChatViewer.this).currentAccount).getClientUserId()) {
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
                if (MessagesController.getInstance(((BaseFragment) ChatViewer.this).currentAccount).checkCanOpenChat(bundle, ChatViewer.this)) {
                    ChatViewer.this.presentFragment(new ChatActivity(bundle), true);
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                ChatViewer.this.createMenu(chatMessageCell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2, boolean z) {
                if (user == null || user.id == UserConfig.getInstance(((BaseFragment) ChatViewer.this).currentAccount).getClientUserId()) {
                    return;
                }
                openProfile(user);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean didLongPressUserAvatar(final ChatMessageCell chatMessageCell, final TLRPC.User user, float f, float f2) {
                AvatarPreviewer.Data dataOf;
                if (user == null || user.id == UserConfig.getInstance(((BaseFragment) ChatViewer.this).currentAccount).getClientUserId()) {
                    return false;
                }
                TLRPC.User user2 = ChatViewer.this.getMessagesController().getUser(Long.valueOf(user.id));
                if (user2 != null) {
                    user = user2;
                }
                AvatarPreviewer.MenuItem[] menuItemArr = {AvatarPreviewer.MenuItem.OPEN_PROFILE, AvatarPreviewer.MenuItem.SEND_MESSAGE};
                TLRPC.UserFull userFull = ChatViewer.this.getMessagesController().getUserFull(user.id);
                if (userFull == null) {
                    dataOf = AvatarPreviewer.Data.of(user, ((BaseFragment) ChatViewer.this).classGuid, menuItemArr);
                } else {
                    dataOf = AvatarPreviewer.Data.of(user, userFull, menuItemArr);
                }
                if (!AvatarPreviewer.canPreview(dataOf)) {
                    return false;
                }
                AvatarPreviewer avatarPreviewer = AvatarPreviewer.getInstance();
                ChatViewer chatViewer = ChatViewer.this;
                avatarPreviewer.show((ViewGroup) chatViewer.fragmentView, chatViewer.getResourceProvider(), dataOf, new AvatarPreviewer.Callback() { 
                    @Override // org.telegram.ui.AvatarPreviewer.Callback
                    public final void onMenuClick(AvatarPreviewer.MenuItem menuItem) {
                        this.f$0.lambda$didLongPressUserAvatar$0(chatMessageCell, user, menuItem);
                    }
                });
                return true;
            }

            public void lambda$didPressUrl$1(String str, DialogInterface dialogInterface, int i) {
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

            void lambda$didLongPressBotButton$2(TLRPC.KeyboardButton keyboardButton, DialogInterface dialogInterface, int i) {
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
                builder.setItems(new CharSequence[]{LocaleController.getString(R.string.Copy)}, new DialogInterface.OnClickListener() { 
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        this.f$0.lambda$didLongPressCopyButton$3(str, dialogInterface, i);
                    }
                });
                ChatViewer.this.showDialog(builder.create());
            }

            public class AnonymousClass15 {
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
}
