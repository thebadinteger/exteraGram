package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.collection.LongSparseArray;
import androidx.core.content.FileProvider;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.ChatListItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScrollerCustom;
import androidx.recyclerview.widget.RecyclerView;
import com.android.dx.io.Opcodes;
import com.exteragram.messenger.utils.ui.ChatHeaderUiHelper;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.gms.cast.MediaError;
import de.robv.android.xposed.callbacks.XCallback;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import kotlin.jvm.internal.LongCompanionObject;
import me.vkryl.core.BitwiseUtils;
import me.vkryl.core.reference.ReferenceList;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.messenger.utils.OnPostDrawView;
import org.telegram.messenger.utils.RectFMergeBounding;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BackDrawable;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Cells.ChatActionCell;
import org.telegram.ui.Cells.ChatActionCell$$ExternalSyntheticApiModelOutline0;
import org.telegram.ui.Cells.ChatActionCell$$ExternalSyntheticApiModelOutline1;
import org.telegram.ui.Cells.ChatLoadingCell;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ChatUnreadCell;
import org.telegram.ui.Components.AdminLogFilterAlert2;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.ChatScrimPopupContainerLayout;
import org.telegram.ui.Components.ClearHistoryAlert;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.EmbedBottomSheet;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.InviteLinkBottomSheet;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.MotionBackgroundDrawable;
import org.telegram.ui.Components.PhonebookShareAlert;
import org.telegram.ui.Components.PipRoundVideoView;
import org.telegram.ui.Components.RadialProgressView;
import org.telegram.ui.Components.RecyclerAnimationScrollHelper;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ShareAlert;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StickersAlert;
import org.telegram.ui.Components.URLSpanMono;
import org.telegram.ui.Components.URLSpanNoUnderline;
import org.telegram.ui.Components.URLSpanReplacement;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.Components.UndoView;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.DownscaleScrollableNoiseSuppressor;
import org.telegram.ui.Components.blur3.capture.IBlur3Capture;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSource;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceBitmap;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceWrapped;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Components.chat.WallpaperBitmapProvider;
import org.telegram.ui.Components.chat.layouts.ChatActivityChannelButtonsLayout;
import org.telegram.ui.Components.chat.layouts.ChatActivityFadeView;

public class ChannelAdminLogActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private static final int[] allowedNotificationsDuringChatListAnimations = {NotificationCenter.chatInfoDidLoad, NotificationCenter.dialogsNeedReload, NotificationCenter.closeChats, NotificationCenter.messagesDidLoad, NotificationCenter.botKeyboardDidLoad};
    public static int lastStableId = 10;
    private long activityResumeTime;
    private ArrayList<TLRPC.ChannelParticipant> admins;
    private AspectRatioFrameLayout aspectRatioFrameLayout;
    private ChatAvatarContainer avatarContainer;
    private ChatActivityChannelButtonsLayout bottomOverlayChat2;
    private TextView bottomOverlayChatText;
    private ChatActivityFadeView chatActivityFadeView;
    private ChatActivityAdapter chatAdapter;
    private LinearLayoutManager chatLayoutManager;
    private ChatListItemAnimator chatListItemAnimator;
    private ChatListRecyclerView chatListView;
    private ArrayList<ChatMessageCell> chatMessageCellsCache;
    private RecyclerAnimationScrollHelper chatScrollHelper;
    private final ChatScrollCallback chatScrollHelperCallback;
    private boolean checkTextureViewPosition;
    private ChatActivityFragmentView contentView;
    protected TLRPC.Chat currentChat;
    private TLRPC.TL_channelAdminLogEventsFilter currentFilter;
    private boolean currentFloatingDateOnScreen;
    private boolean currentFloatingTopIsNotMessage;
    private ChatMessageCell dummyMessageCell;
    private ImageView emptyImageView;
    private LinearLayout emptyLayoutView;
    private TextView emptyView;
    private FrameLayout emptyViewContainer;
    private boolean endReached;
    private final HashSet<Long> expandedEvents;
    private final ArrayList<MessageObject> filteredMessages;
    private final ArrayList<Integer> filteredMessagesUpdatedPosition;
    private AnimatorSet floatingDateAnimation;
    private ChatActionCell floatingDateView;
    private final ReferenceList<View> glassAttachedViews;
    private final BlurredBackgroundDrawableViewFactory glassBackgroundDrawableFactory;
    private final BlurredBackgroundDrawableViewFactory glassBackgroundDrawableFactoryFrosted;
    private final BlurredBackgroundSourceRenderNode glassBackgroundSourceFrostedRenderNode;
    private final BlurredBackgroundSourceRenderNode glassBackgroundSourceRenderNode;
    private final ArrayList<RectF> glassDrawablesPositions;
    private int glassDrawablesPositionsCount;
    private final ArrayList<RectF> glassDrawablesPositionsMerged;
    public int highlightMessageId;
    public String highlightMessageQuote;
    public boolean highlightMessageQuoteFirst;
    public int highlightMessageQuoteOffset;
    private OnPostDrawView invalidateBlurredSourcesView;
    private HashMap<String, Object> invitesCache;
    private boolean linviteLoading;
    private boolean loading;
    private int loadsCount;
    protected ArrayList<MessageObject> messages;
    private final HashMap<String, ArrayList<MessageObject>> messagesByDays;
    private final LongSparseArray<MessageObject> messagesDict;
    private final int[] mid;
    private long minEventId;
    private final BlurredBackgroundDrawableViewFactory navbarContentDrawableFactory;
    private final BlurredBackgroundSourceWrapped navbarContentSourceWallpaper;
    private AnimationNotificationsLocker notificationsLocker;
    private boolean openAnimationEnded;
    private boolean paused;
    private RadialProgressView progressBar;
    private FrameLayout progressView;
    private View progressView2;
    private PhotoViewer.PhotoViewerProvider provider;
    private final LongSparseArray<MessageObject> realMessagesDict;
    private final int recommendedAdditionalSizeY;
    private boolean reloadingLastMessages;
    private FrameLayout roundVideoContainer;
    private long savedScrollEventId;
    private int savedScrollOffset;
    private int savedScrollPosition;
    private ActionBarPopupWindow scrimPopupWindow;
    private int scrimPopupX;
    private int scrimPopupY;
    private boolean scrollByTouch;
    private int scrollCallbackAnimationIndex;
    private int scrollToMessagePosition;
    private int scrollToOffsetOnRecreate;
    private int scrollToPositionOnRecreate;
    private final DownscaleScrollableNoiseSuppressor scrollableViewNoiseSuppressor;
    private boolean scrollingFloatingDate;
    private ImageView searchCalendarButton;
    private FrameLayout searchContainer;
    private SimpleTextView searchCountText;
    private ActionBarMenuItem searchItem;
    private String searchQuery;
    private boolean searchWas;
    private LongSparseArray<TLRPC.User> selectedAdmins;
    private MessageObject selectedObject;
    private TLRPC.ChannelParticipant selectedParticipant;
    public boolean showNoQuoteAlert;
    private final LongSparseArray<Integer> stableIdByEventExpand;
    private final RectF tmpViewRectF;
    private UndoView undoView;
    private Runnable unselectRunnable;
    private HashMap<Long, TLRPC.User> usersMap;
    private TextureView videoTextureView;
    private boolean wasManualScroll;
    private boolean wasPaused;

    public void updateBottomOverlay() {
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean drawEdgeNavigationBar() {
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    public ChannelAdminLogActivity(TLRPC.Chat chat) {
        ReferenceList<View> referenceList = new ReferenceList<>();
        this.glassAttachedViews = referenceList;
        this.chatMessageCellsCache = new ArrayList<>();
        this.mid = new int[]{2};
        this.scrollToPositionOnRecreate = -1;
        this.scrollToOffsetOnRecreate = 0;
        this.paused = true;
        this.wasPaused = false;
        this.messagesDict = new LongSparseArray<>();
        this.realMessagesDict = new LongSparseArray<>();
        this.messagesByDays = new HashMap<>();
        this.messages = new ArrayList<>();
        this.filteredMessages = new ArrayList<>();
        this.expandedEvents = new HashSet<>();
        this.currentFilter = null;
        this.searchQuery = _UrlKt.FRAGMENT_ENCODE_SET;
        this.notificationsLocker = new AnimationNotificationsLocker(allowedNotificationsDuringChatListAnimations);
        this.invitesCache = new HashMap<>();
        this.provider = new PhotoViewer.EmptyPhotoViewerProvider() { // from class: org.telegram.ui.ChannelAdminLogActivity.1
            @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
            public PhotoViewer.PlaceProviderObject getPlaceForPhoto(MessageObject messageObject, TLRPC.FileLocation fileLocation, int i, boolean z, boolean z2) {
                ChatActionCell chatActionCell;
                MessageObject messageObject2;
                ChatMessageCell chatMessageCell;
                MessageObject messageObject3;
                int childCount = ChannelAdminLogActivity.this.chatListView.getChildCount();
                int i2 = 0;
                while (true) {
                    ImageReceiver photoImage = null;
                    if (i2 >= childCount) {
                        return null;
                    }
                    View childAt = ChannelAdminLogActivity.this.chatListView.getChildAt(i2);
                    if (childAt instanceof ChatMessageCell) {
                        if (messageObject != null && (messageObject3 = (chatMessageCell = (ChatMessageCell) childAt).getMessageObject()) != null && messageObject3.getId() == messageObject.getId()) {
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
                        placeProviderObject.parentView = ChannelAdminLogActivity.this.chatListView;
                        placeProviderObject.imageReceiver = photoImage;
                        placeProviderObject.thumb = photoImage.getBitmapSafe();
                        placeProviderObject.radius = photoImage.getRoundRadius(true);
                        placeProviderObject.isEvent = true;
                        return placeProviderObject;
                    }
                    i2++;
                }
            }
        };
        this.filteredMessagesUpdatedPosition = new ArrayList<>();
        this.stableIdByEventExpand = new LongSparseArray<>();
        this.highlightMessageId = Integer.MAX_VALUE;
        this.highlightMessageQuoteOffset = -1;
        this.scrollToMessagePosition = XCallback.PRIORITY_LOWEST;
        this.chatScrollHelperCallback = new ChatScrollCallback();
        this.savedScrollPosition = -1;
        this.glassDrawablesPositions = new ArrayList<>();
        this.glassDrawablesPositionsMerged = new ArrayList<>();
        this.tmpViewRectF = new RectF();
        BlurredBackgroundSourceWrapped blurredBackgroundSourceWrapped = new BlurredBackgroundSourceWrapped();
        this.navbarContentSourceWallpaper = blurredBackgroundSourceWrapped;
        BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSourceWrapped);
        this.navbarContentDrawableFactory = blurredBackgroundDrawableViewFactory;
        if (Build.VERSION.SDK_INT >= 31 && SharedConfig.chatBlurEnabled()) {
            DownscaleScrollableNoiseSuppressor downscaleScrollableNoiseSuppressor = new DownscaleScrollableNoiseSuppressor();
            this.scrollableViewNoiseSuppressor = downscaleScrollableNoiseSuppressor;
            BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode = new BlurredBackgroundSourceRenderNode(blurredBackgroundSourceWrapped);
            this.glassBackgroundSourceFrostedRenderNode = blurredBackgroundSourceRenderNode;
            blurredBackgroundSourceRenderNode.setOnDrawablesRelativePositionChangeListener(new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.invalidateMergedVisibleBlurredPositionsAndSourcesPositions();
                }
            });
            blurredBackgroundSourceRenderNode.setScrollableNoiseSuppressor(downscaleScrollableNoiseSuppressor, -3);
            blurredBackgroundSourceRenderNode.setUnderSource(blurredBackgroundSourceWrapped);
            BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory2 = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSourceRenderNode);
            this.glassBackgroundDrawableFactoryFrosted = blurredBackgroundDrawableViewFactory2;
            blurredBackgroundDrawableViewFactory2.setLiquidGlassEffectAllowed(LiteMode.isEnabled(262144));
            if (LiteMode.isEnabled(262144)) {
                BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode2 = new BlurredBackgroundSourceRenderNode(blurredBackgroundSourceWrapped);
                this.glassBackgroundSourceRenderNode = blurredBackgroundSourceRenderNode2;
                blurredBackgroundSourceRenderNode2.setOnDrawablesRelativePositionChangeListener(new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda7
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.invalidateMergedVisibleBlurredPositionsAndSourcesPositions();
                    }
                });
                blurredBackgroundSourceRenderNode2.setScrollableNoiseSuppressor(downscaleScrollableNoiseSuppressor, -2);
                blurredBackgroundSourceRenderNode2.setUnderSource(blurredBackgroundSourceWrapped);
                BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory3 = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSourceRenderNode2);
                this.glassBackgroundDrawableFactory = blurredBackgroundDrawableViewFactory3;
                blurredBackgroundDrawableViewFactory3.setLiquidGlassEffectAllowed(LiteMode.isEnabled(262144));
                this.recommendedAdditionalSizeY = 0;
            } else {
                this.glassBackgroundSourceRenderNode = null;
                this.glassBackgroundDrawableFactory = blurredBackgroundDrawableViewFactory2;
                this.recommendedAdditionalSizeY = AndroidUtilities.dp(48.0f);
            }
        } else {
            this.scrollableViewNoiseSuppressor = null;
            this.recommendedAdditionalSizeY = 0;
            this.glassBackgroundSourceRenderNode = null;
            this.glassBackgroundSourceFrostedRenderNode = null;
            this.glassBackgroundDrawableFactory = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSourceWrapped);
            this.glassBackgroundDrawableFactoryFrosted = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSourceWrapped);
        }
        blurredBackgroundDrawableViewFactory.setLinkedViewsRef(referenceList);
        this.glassBackgroundDrawableFactory.setLinkedViewsRef(referenceList);
        this.glassBackgroundDrawableFactoryFrosted.setLinkedViewsRef(referenceList);
        this.currentChat = chat;
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
        loadAdmins();
        Bulletin.addDelegate(this, new Bulletin.Delegate() { // from class: org.telegram.ui.ChannelAdminLogActivity.2
            @Override // org.telegram.ui.Components.Bulletin.Delegate
            public int getBottomOffset(int i) {
                return AndroidUtilities.dp(51.0f);
            }
        });
        return true;
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
        Bulletin.removeDelegate(this);
    }

    private void updateEmptyPlaceholder() {
        if (this.emptyView == null) {
            return;
        }
        if (!TextUtils.isEmpty(this.searchQuery)) {
            this.emptyImageView.setVisibility(8);
            this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(3.0f));
            this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.NoLogFound)));
        } else {
            if (this.selectedAdmins != null || this.currentFilter != null) {
                this.emptyImageView.setVisibility(8);
                this.emptyView.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp(8.0f), AndroidUtilities.dp(3.0f));
                this.emptyView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.NoLogFoundFiltered)));
                return;
            }
            this.emptyImageView.setVisibility(0);
            this.emptyView.setPadding(AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f), AndroidUtilities.dp(16.0f));
            boolean z = this.currentChat.megagroup;
            TextView textView = this.emptyView;
            if (z) {
                textView.setText(smallerNewNewLine(AndroidUtilities.replaceTags(LocaleController.getString(R.string.EventLogEmpty2))));
            } else {
                textView.setText(smallerNewNewLine(AndroidUtilities.replaceTags(LocaleController.getString(R.string.EventLogEmptyChannel2))));
            }
        }
    }

    private CharSequence smallerNewNewLine(CharSequence charSequence) {
        int iCharSequenceIndexOf = AndroidUtilities.charSequenceIndexOf(charSequence, "\n\n");
        if (iCharSequenceIndexOf >= 0 && Build.VERSION.SDK_INT >= 29) {
            if (!(charSequence instanceof Spannable)) {
                charSequence = new SpannableStringBuilder(charSequence);
            }
            ChatActionCell$$ExternalSyntheticApiModelOutline1.m();
            ((SpannableStringBuilder) charSequence).setSpan(ChatActionCell$$ExternalSyntheticApiModelOutline0.m(AndroidUtilities.dp(8.0f)), iCharSequenceIndexOf + 1, iCharSequenceIndexOf + 2, 33);
        }
        return charSequence;
    }

    public void lambda$processSelectedOption$20() {
        if (this.reloadingLastMessages) {
            return;
        }
        this.reloadingLastMessages = true;
        TLRPC.TL_channels_getAdminLog tL_channels_getAdminLog = new TLRPC.TL_channels_getAdminLog();
        tL_channels_getAdminLog.channel = MessagesController.getInputChannel(this.currentChat);
        tL_channels_getAdminLog.q = this.searchQuery;
        tL_channels_getAdminLog.limit = 10;
        tL_channels_getAdminLog.max_id = 0L;
        tL_channels_getAdminLog.min_id = 0L;
        TLRPC.TL_channelAdminLogEventsFilter tL_channelAdminLogEventsFilter = this.currentFilter;
        if (tL_channelAdminLogEventsFilter != null) {
            tL_channels_getAdminLog.flags = 1 | tL_channels_getAdminLog.flags;
            tL_channels_getAdminLog.events_filter = tL_channelAdminLogEventsFilter;
        }
        if (this.selectedAdmins != null) {
            tL_channels_getAdminLog.flags |= 2;
            for (int i = 0; i < this.selectedAdmins.size(); i++) {
                tL_channels_getAdminLog.admins.add(MessagesController.getInstance(this.currentAccount).getInputUser(this.selectedAdmins.valueAt(i)));
            }
        }
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_channels_getAdminLog, new RequestDelegate() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda23
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$reloadLastMessages$1(tLObject, tL_error);
            }
        });
    }

    public void lambda$loadMessages$4(TLObject tLObject, TLRPC.TL_error tL_error) {
        if (tLObject != null) {
            final TLRPC.TL_channels_adminLogResults tL_channels_adminLogResults = (TLRPC.TL_channels_adminLogResults) tLObject;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda9
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$loadMessages$3(tL_channels_adminLogResults);
                }
            });
        }
    }

    void lambda$loadMessages$2() {
        saveScrollPosition(false);
        this.chatAdapter.notifyDataSetChanged();
    }

    public void filterDeletedMessages() {
        ChannelAdminLogActivity channelAdminLogActivity = this;
        ArrayList arrayList = new ArrayList();
        ArrayList<MessageObject> arrayList2 = new ArrayList<>();
        channelAdminLogActivity.filteredMessagesUpdatedPosition.clear();
        int i = 0;
        while (i < channelAdminLogActivity.messages.size()) {
            MessageObject messageObject = channelAdminLogActivity.messages.get(i);
            long jMessageDeletedBy = channelAdminLogActivity.messageDeletedBy(messageObject);
            if (messageObject.stableId <= 0) {
                int i2 = lastStableId;
                lastStableId = i2 + 1;
                messageObject.stableId = i2;
            }
            int i3 = i + 1;
            long jMessageDeletedBy2 = channelAdminLogActivity.messageDeletedBy(i3 < channelAdminLogActivity.messages.size() ? channelAdminLogActivity.messages.get(i3) : null);
            if (jMessageDeletedBy != 0) {
                arrayList2.add(messageObject);
            } else {
                arrayList.add(messageObject);
            }
            if (jMessageDeletedBy == jMessageDeletedBy2 || arrayList2.isEmpty()) {
                arrayList2 = arrayList2;
            } else {
                TLRPC.ReplyMarkup replyMarkup = messageObject.messageOwner.reply_markup;
                boolean z = (replyMarkup == null || replyMarkup.rows.isEmpty()) ? false : true;
                int size = arrayList.size();
                ArrayList<MessageObject> arrayList3 = new ArrayList<>();
                for (int size2 = arrayList2.size() - 1; size2 >= 0 && arrayList2.get(size2).contentType == 1; size2--) {
                    arrayList3.add(arrayList2.remove(size2));
                }
                if (!arrayList2.isEmpty()) {
                    MessageObject messageObject2 = arrayList2.get(arrayList2.size() - 1);
                    boolean z2 = TextUtils.isEmpty(channelAdminLogActivity.searchQuery) && arrayList2.size() > 3;
                    if (channelAdminLogActivity.expandedEvents.contains(Long.valueOf(messageObject2.eventId)) || !z2) {
                        for (int i4 = 0; i4 < arrayList2.size(); i4++) {
                            channelAdminLogActivity.setupExpandButton(arrayList2.get(i4), 0);
                        }
                        arrayList.addAll(arrayList2);
                    } else {
                        channelAdminLogActivity.setupExpandButton(messageObject2, arrayList2.size() - 1);
                        arrayList.add(messageObject2);
                    }
                    TLRPC.ReplyMarkup replyMarkup2 = messageObject2.messageOwner.reply_markup;
                    if (z != ((replyMarkup2 == null || replyMarkup2.rows.isEmpty()) ? false : true)) {
                        messageObject2.forceUpdate = true;
                        channelAdminLogActivity.chatAdapter.notifyItemChanged((z ? arrayList2.size() - 1 : 0) + size);
                        channelAdminLogActivity.chatAdapter.notifyItemChanged(size + (z ? arrayList2.size() - 1 : 0) + 1);
                    }
                    long j = messageObject.eventId;
                    arrayList.add(channelAdminLogActivity.actionMessagesDeletedBy(j, messageObject.currentEvent.user_id, arrayList2, channelAdminLogActivity.expandedEvents.contains(Long.valueOf(j)), z2));
                }
                if (arrayList3.isEmpty()) {
                    channelAdminLogActivity = this;
                } else {
                    MessageObject messageObject3 = arrayList3.get(arrayList3.size() - 1);
                    arrayList.addAll(arrayList3);
                    long j2 = messageObject3.eventId;
                    long j3 = messageObject3.currentEvent.user_id;
                    channelAdminLogActivity = this;
                    arrayList.add(channelAdminLogActivity.actionMessagesDeletedBy(j2, j3, arrayList3, true, false));
                }
                arrayList2.clear();
            }
            i = i3;
            arrayList2 = arrayList2;
        }
        channelAdminLogActivity.filteredMessages.clear();
        channelAdminLogActivity.filteredMessages.addAll(arrayList);
    }

    private MessageObject actionMessagesDeletedBy(long j, long j2, ArrayList<MessageObject> arrayList, boolean z, boolean z2) {
        MessageObject messageObject;
        int i = 0;
        while (true) {
            if (i >= this.filteredMessages.size()) {
                messageObject = null;
                break;
            }
            messageObject = this.filteredMessages.get(i);
            if (messageObject != null && messageObject.contentType == 1 && messageObject.actionDeleteGroupEventId == j) {
                break;
            }
            i++;
        }
        if (messageObject == null) {
            TLRPC.TL_message tL_message = new TLRPC.TL_message();
            tL_message.dialog_id = -this.currentChat.id;
            tL_message.id = -1;
            try {
                tL_message.date = arrayList.get(0).messageOwner.date;
            } catch (Exception e) {
                FileLog.e(e);
            }
            messageObject = new MessageObject(this.currentAccount, tL_message, false, false);
        }
        TLRPC.User user = getMessagesController().getUser(Long.valueOf(j2));
        messageObject.contentType = 1;
        if (z2 && arrayList.size() > 1) {
            messageObject.actionDeleteGroupEventId = j;
        } else {
            messageObject.actionDeleteGroupEventId = -1L;
        }
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(MessageObject.replaceWithLink(LocaleController.formatPluralString(z2 ? "EventLogDeletedMultipleMessagesToExpand" : "EventLogDeletedMultipleMessages", arrayList.size(), TextUtils.join(", ", arrayList.stream().map(new Function() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda14
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return Long.valueOf(((MessageObject) obj).getFromChatId());
            }
        }).distinct().map(new Function() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda15
            @Override // java.util.function.Function
            public final Object apply(Object obj) {
                return this.f$0.lambda$actionMessagesDeletedBy$5((Long) obj);
            }
        }).filter(new Predicate() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda16
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return ChannelAdminLogActivity.m7312$r8$lambda$jAlypGwhTAgRhhBaFgBF5z7YCc((String) obj);
            }
        }).limit(4L).toArray())), "un1", user));
        if (z2 && arrayList.size() > 1) {
            ProfileActivity.ShowDrawable showDrawableFindDrawable = findDrawable(messageObject.messageText);
            if (showDrawableFindDrawable == null) {
                showDrawableFindDrawable = new ProfileActivity.ShowDrawable(LocaleController.getString(z ? R.string.EventLogDeletedMultipleMessagesHide : R.string.EventLogDeletedMultipleMessagesShow));
                showDrawableFindDrawable.textDrawable.setTypeface(AndroidUtilities.bold());
                showDrawableFindDrawable.textDrawable.setTextSize(AndroidUtilities.dp(10.0f));
                showDrawableFindDrawable.setTextColor(-1);
                showDrawableFindDrawable.setBackgroundColor(503316480);
            } else {
                showDrawableFindDrawable.textDrawable.setText(LocaleController.getString(z ? R.string.EventLogDeletedMultipleMessagesHide : R.string.EventLogDeletedMultipleMessagesShow), false);
            }
            showDrawableFindDrawable.setBounds(0, 0, showDrawableFindDrawable.getIntrinsicWidth(), showDrawableFindDrawable.getIntrinsicHeight());
            spannableStringBuilder.append((CharSequence) " S");
            spannableStringBuilder.setSpan(new ColoredImageSpan(showDrawableFindDrawable), spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 33);
        }
        messageObject.messageText = spannableStringBuilder;
        MessageObject messageObject2 = arrayList.size() > 0 ? arrayList.get(arrayList.size() - 1) : null;
        if (messageObject2 != null) {
            if (!this.stableIdByEventExpand.containsKey(messageObject2.eventId)) {
                LongSparseArray<Integer> longSparseArray = this.stableIdByEventExpand;
                long j3 = messageObject2.eventId;
                int i2 = lastStableId;
                lastStableId = i2 + 1;
                longSparseArray.put(j3, Integer.valueOf(i2));
            }
            messageObject.stableId = this.stableIdByEventExpand.get(messageObject2.eventId).intValue();
        }
        return messageObject;
    }

    public boolean $r8$lambda$KXgfFXYKFqru53l4dl9EPjRLLsI(View view, MotionEvent motionEvent) {
        return true;
    }

    public class AnonymousClass9 extends ChatListItemAnimator {
        Runnable finishRunnable;
        int scrollAnimationIndex;

        public AnonymousClass9(ChatActivity chatActivity, RecyclerListView recyclerListView, Theme.ResourcesProvider resourcesProvider) {
            super(chatActivity, recyclerListView, resourcesProvider);
            this.scrollAnimationIndex = -1;
        }

        @Override // androidx.recyclerview.widget.ChatListItemAnimator
        public void onAnimationStart() {
            if (this.scrollAnimationIndex == -1) {
                this.scrollAnimationIndex = ChannelAdminLogActivity.this.getNotificationCenter().setAnimationInProgress(this.scrollAnimationIndex, ChannelAdminLogActivity.allowedNotificationsDuringChatListAnimations, false);
            }
            Runnable runnable = this.finishRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
                this.finishRunnable = null;
            }
            if (BuildVars.LOGS_ENABLED) {
                FileLog.d("admin logs chatItemAnimator disable notifications");
            }
        }

        @Override // androidx.recyclerview.widget.ChatListItemAnimator, androidx.recyclerview.widget.DefaultItemAnimator
        public void onAllAnimationsDone() {
            super.onAllAnimationsDone();
            Runnable runnable = this.finishRunnable;
            if (runnable != null) {
                AndroidUtilities.cancelRunOnUIThread(runnable);
            }
            Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$9$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onAllAnimationsDone$0();
                }
            };
            this.finishRunnable = runnable2;
            AndroidUtilities.runOnUIThread(runnable2);
        }

        public void lambda$createView$11(int i) {
        loadMessages(true);
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

    void lambda$createMenu$14(final ArrayList arrayList, ArrayList arrayList2, ArrayList arrayList3, View view, float f, float f2) {
        if (arrayList.isEmpty() || getParentActivity() == null) {
            return;
        }
        int y = 0;
        ActionBarPopupWindow.ActionBarPopupWindowLayout actionBarPopupWindowLayout = new ActionBarPopupWindow.ActionBarPopupWindowLayout(getParentActivity(), R.drawable.popup_fixed_alert, getResourceProvider(), 0);
        actionBarPopupWindowLayout.setMinimumWidth(AndroidUtilities.dp(200.0f));
        Rect rect = new Rect();
        getParentActivity().getResources().getDrawable(R.drawable.popup_fixed_alert).mutate().getPadding(rect);
        actionBarPopupWindowLayout.setBackgroundColor(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground));
        int size = arrayList2.size();
        final int i = 0;
        while (true) {
            if (i >= size) {
                break;
            }
            if (arrayList.get(i) == null) {
                actionBarPopupWindowLayout.addView((View) new ActionBarPopupWindow.GapView(getContext(), getResourceProvider()), LayoutHelper.createLinear(-1, 8));
            } else {
                ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getParentActivity(), i == 0, i == size + (-1), getResourceProvider());
                actionBarMenuSubItem.setMinimumWidth(AndroidUtilities.dp(200.0f));
                actionBarMenuSubItem.setTextAndIcon((CharSequence) arrayList2.get(i), ((Integer) arrayList3.get(i)).intValue());
                if (((Integer) arrayList.get(i)).intValue() == 35) {
                    actionBarMenuSubItem.setColors(getThemedColor(Theme.key_text_RedBold), getThemedColor(Theme.key_text_RedRegular));
                }
                final Integer num = (Integer) arrayList.get(i);
                actionBarPopupWindowLayout.addView(actionBarMenuSubItem);
                actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda24
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view2) {
                        this.f$0.lambda$createMenu$13(i, arrayList, num, view2);
                    }
                });
            }
            i++;
        }
        ChatScrimPopupContainerLayout chatScrimPopupContainerLayout = new ChatScrimPopupContainerLayout(this.contentView.getContext()) { // from class: org.telegram.ui.ChannelAdminLogActivity.14
            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchKeyEvent(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == 4 && keyEvent.getRepeatCount() == 0) {
                    ChannelAdminLogActivity.this.closeMenu();
                }
                return super.dispatchKeyEvent(keyEvent);
            }

            @Override // android.view.ViewGroup, android.view.View
            public boolean dispatchTouchEvent(MotionEvent motionEvent) {
                boolean zDispatchTouchEvent = super.dispatchTouchEvent(motionEvent);
                if (motionEvent.getAction() == 0 && !zDispatchTouchEvent) {
                    ChannelAdminLogActivity.this.closeMenu();
                }
                return zDispatchTouchEvent;
            }
        };
        chatScrimPopupContainerLayout.addView(actionBarPopupWindowLayout, LayoutHelper.createLinearRelatively(-2.0f, -2.0f, 3, 0.0f, 0.0f, 0.0f, 0.0f));
        chatScrimPopupContainerLayout.setPopupWindowLayout(actionBarPopupWindowLayout);
        int i2 = -2;
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(chatScrimPopupContainerLayout, i2, i2) { // from class: org.telegram.ui.ChannelAdminLogActivity.15
            @Override // org.telegram.ui.ActionBar.ActionBarPopupWindow, android.widget.PopupWindow
            public void dismiss() {
                super.dismiss();
                if (ChannelAdminLogActivity.this.scrimPopupWindow != this) {
                    return;
                }
                Bulletin.hideVisible();
                ChannelAdminLogActivity.this.scrimPopupWindow = null;
            }
        };
        this.scrimPopupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setPauseNotifications(true);
        this.scrimPopupWindow.setDismissAnimationDuration(Opcodes.REM_INT_LIT8);
        this.scrimPopupWindow.setOutsideTouchable(true);
        this.scrimPopupWindow.setClippingEnabled(true);
        this.scrimPopupWindow.setAnimationStyle(R.style.PopupContextAnimation);
        this.scrimPopupWindow.setFocusable(true);
        chatScrimPopupContainerLayout.measure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(1000.0f), Integer.MIN_VALUE));
        this.scrimPopupWindow.setInputMethodMode(2);
        this.scrimPopupWindow.setSoftInputMode(48);
        this.scrimPopupWindow.getContentView().setFocusableInTouchMode(true);
        actionBarPopupWindowLayout.setFitItems(true);
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
                int i3 = height - measuredHeight;
                if (y > i3 - AndroidUtilities.dp(8.0f)) {
                    y = i3 - AndroidUtilities.dp(8.0f);
                }
            }
        } else if (!this.inBubbleMode) {
            y = AndroidUtilities.statusBarHeight;
        }
        this.scrimPopupX = left;
        this.scrimPopupY = y;
        chatScrimPopupContainerLayout.setMaxHeight(height - y);
        this.scrimPopupWindow.showAtLocation(this.chatListView, 51, left, y);
        this.scrimPopupWindow.dimBehind();
    }

    public private void processSelectedOption(int i) {
        File file;
        TLRPC.User user;
        closeMenu();
        MessageObject messageObject = this.selectedObject;
        if (messageObject == null) {
            return;
        }
        if (i != 3) {
            if (i != 4) {
                if (i != 5) {
                    if (i != 6) {
                        if (i == 7) {
                            String string = messageObject.messageOwner.attachPath;
                            if (string != null && string.length() > 0 && !new File(string).exists()) {
                                string = null;
                            }
                            if (string == null || string.length() == 0) {
                                string = getFileLoader().getPathToMessage(this.selectedObject.messageOwner).toString();
                            }
                            if ((Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                                getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                                this.selectedObject = null;
                                this.selectedParticipant = null;
                                return;
                            }
                            MediaController.saveFile(string, getParentActivity(), 0, null, null);
                        } else {
                            switch (i) {
                                case 9:
                                    showDialog(new StickersAlert(getParentActivity(), this, this.selectedObject.getInputStickerSet(), null, null, false));
                                    break;
                                case 10:
                                    if ((Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                                        getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                                        this.selectedObject = null;
                                        this.selectedParticipant = null;
                                        return;
                                    }
                                    String documentFileName = FileLoader.getDocumentFileName(this.selectedObject.getDocument());
                                    if (TextUtils.isEmpty(documentFileName)) {
                                        documentFileName = this.selectedObject.getFileName();
                                    }
                                    String string2 = this.selectedObject.messageOwner.attachPath;
                                    if (string2 != null && string2.length() > 0 && !new File(string2).exists()) {
                                        string2 = null;
                                    }
                                    if (string2 == null || string2.length() == 0) {
                                        string2 = getFileLoader().getPathToMessage(this.selectedObject.messageOwner).toString();
                                    }
                                    MediaController.saveFile(string2, getParentActivity(), this.selectedObject.isMusic() ? 3 : 2, documentFileName, this.selectedObject.getDocument() != null ? this.selectedObject.getDocument().mime_type : _UrlKt.FRAGMENT_ENCODE_SET);
                                    break;
                                    break;
                                case 11:
                                    MessagesController.getInstance(this.currentAccount).saveGif(this.selectedObject, messageObject.getDocument());
                                    break;
                                default:
                                    switch (i) {
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
                                                Intent intent = new Intent("android.intent.action.DIAL", Uri.parse("tel:" + this.selectedObject.messageOwner.media.phone_number));
                                                intent.addFlags(268435456);
                                                getParentActivity().startActivityForResult(intent, MediaError.DetailedErrorCode.SEGMENT_UNKNOWN);
                                            } catch (Exception e) {
                                                FileLog.e(e);
                                            }
                                            break;
                                        default:
                                            switch (i) {
                                                case 33:
                                                    if (this.selectedParticipant != null) {
                                                        final TLRPC.User user2 = getMessagesController().getUser(Long.valueOf(DialogObject.getPeerDialogId(this.selectedParticipant.peer)));
                                                        TLRPC.ChannelParticipant channelParticipant = this.selectedParticipant;
                                                        if (channelParticipant.banned_rights == null) {
                                                            channelParticipant.banned_rights = new TLRPC.TL_chatBannedRights();
                                                        }
                                                        TLRPC.TL_chatBannedRights tL_chatBannedRights = this.selectedParticipant.banned_rights;
                                                        tL_chatBannedRights.send_plain = true;
                                                        tL_chatBannedRights.send_messages = true;
                                                        tL_chatBannedRights.send_media = true;
                                                        tL_chatBannedRights.send_stickers = true;
                                                        tL_chatBannedRights.send_gifs = true;
                                                        tL_chatBannedRights.send_games = true;
                                                        tL_chatBannedRights.send_inline = true;
                                                        tL_chatBannedRights.send_polls = true;
                                                        tL_chatBannedRights.send_photos = true;
                                                        tL_chatBannedRights.send_videos = true;
                                                        tL_chatBannedRights.send_roundvideos = true;
                                                        tL_chatBannedRights.send_audios = true;
                                                        tL_chatBannedRights.send_voices = true;
                                                        tL_chatBannedRights.send_docs = true;
                                                        tL_chatBannedRights.send_reactions = true;
                                                        getMessagesController().setParticipantBannedRole(this.currentChat.id, user2, null, this.selectedParticipant.banned_rights, true, getFragmentForAlert(1), new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda28
                                                            @Override // java.lang.Runnable
                                                            public final void run() {
                                                                this.f$0.lambda$processSelectedOption$19(user2);
                                                            }
                                                        });
                                                    }
                                                    break;
                                                case 34:
                                                    TLRPC.TL_channels_reportAntiSpamFalsePositive tL_channels_reportAntiSpamFalsePositive = new TLRPC.TL_channels_reportAntiSpamFalsePositive();
                                                    tL_channels_reportAntiSpamFalsePositive.channel = getMessagesController().getInputChannel(this.currentChat.id);
                                                    tL_channels_reportAntiSpamFalsePositive.msg_id = this.selectedObject.getRealId();
                                                    getConnectionsManager().sendRequest(tL_channels_reportAntiSpamFalsePositive, new RequestDelegate() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda27
                                                        @Override // org.telegram.tgnet.RequestDelegate
                                                        public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                                                            this.f$0.lambda$processSelectedOption$18(tLObject, tL_error);
                                                        }
                                                    });
                                                    break;
                                                case 35:
                                                    getMessagesController().deleteParticipantFromChat(this.currentChat.id, getMessagesController().getInputPeer(this.selectedObject.messageOwner.from_id), false, false, new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda29
                                                        @Override // java.lang.Runnable
                                                        public final void run() {
                                                            this.f$0.lambda$processSelectedOption$20();
                                                        }
                                                    });
                                                    if (this.currentChat != null && (this.selectedObject.messageOwner.from_id instanceof TLRPC.TL_peerUser) && BulletinFactory.canShowBulletin(this) && (user = getMessagesController().getUser(Long.valueOf(this.selectedObject.messageOwner.from_id.user_id))) != null) {
                                                        BulletinFactory.createRemoveFromChatBulletin(this, user, this.currentChat.title).show();
                                                    }
                                                    break;
                                            }
                                            break;
                                    }
                                    break;
                            }
                        }
                    } else {
                        String string3 = messageObject.messageOwner.attachPath;
                        if (string3 != null && string3.length() > 0 && !new File(string3).exists()) {
                            string3 = null;
                        }
                        if (string3 == null || string3.length() == 0) {
                            string3 = getFileLoader().getPathToMessage(this.selectedObject.messageOwner).toString();
                        }
                        Intent intent2 = new Intent("android.intent.action.SEND");
                        intent2.setType(this.selectedObject.getDocument().mime_type);
                        try {
                            intent2.putExtra("android.intent.extra.STREAM", FileProvider.getUriForFile(getParentActivity(), ApplicationLoader.getApplicationId() + ".provider", new File(string3)));
                            intent2.setFlags(1);
                        } catch (Exception unused) {
                            intent2.putExtra("android.intent.extra.STREAM", Uri.fromFile(new File(string3)));
                        }
                        try {
                            getParentActivity().startActivityForResult(Intent.createChooser(intent2, LocaleController.getString(R.string.ShareFile)), MediaError.DetailedErrorCode.SEGMENT_UNKNOWN);
                        } catch (Exception unused2) {
                        }
                    }
                } else {
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
                                    this.selectedParticipant = null;
                                    return;
                                } else {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity());
                                    builder.setTitle(LocaleController.getString(R.string.AppName));
                                    builder.setMessage(LocaleController.getString(R.string.IncorrectTheme));
                                    builder.setPositiveButton(LocaleController.getString(R.string.OK), null);
                                    showDialog(builder.create());
                                }
                            }
                        } else if (LocaleController.getInstance().applyLanguageFile(file, this.currentAccount)) {
                            presentFragment(new LanguageSelectActivity());
                        } else if (getParentActivity() == null) {
                            this.selectedObject = null;
                            this.selectedParticipant = null;
                            return;
                        } else {
                            AlertDialog.Builder builder2 = new AlertDialog.Builder(getParentActivity());
                            builder2.setTitle(LocaleController.getString(R.string.AppName));
                            builder2.setMessage(LocaleController.getString(R.string.IncorrectLocalization));
                            builder2.setPositiveButton(LocaleController.getString(R.string.OK), null);
                            showDialog(builder2.create());
                        }
                    }
                }
            } else {
                String string4 = messageObject.messageOwner.attachPath;
                if (string4 != null && string4.length() > 0 && !new File(string4).exists()) {
                    string4 = null;
                }
                if (string4 == null || string4.length() == 0) {
                    string4 = getFileLoader().getPathToMessage(this.selectedObject.messageOwner).toString();
                }
                int i2 = this.selectedObject.type;
                if (i2 == 3 || i2 == 1) {
                    if ((Build.VERSION.SDK_INT <= 28 || BuildVars.NO_SCOPED_STORAGE) && getParentActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                        getParentActivity().requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"}, 4);
                        this.selectedObject = null;
                        this.selectedParticipant = null;
                        return;
                    }
                    MediaController.saveFile(string4, getParentActivity(), this.selectedObject.type == 3 ? 1 : 0, null, null);
                }
            }
        } else {
            AndroidUtilities.addToClipboard(getMessageContent(messageObject, 0, true));
            BulletinFactory.of(this).createCopyBulletin(LocaleController.getString(R.string.MessageCopied)).show();
        }
        this.selectedObject = null;
        this.selectedParticipant = null;
    }

    public void lambda$loadAdmins$22(final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$loadAdmins$21(tL_error, tLObject);
            }
        });
    }

    public void lambda$showOpenUrlAlert$24(String str, AlertDialog alertDialog, int i) {
        Browser.openUrl((Context) getParentActivity(), str, true);
    }

    public class ChatActivityAdapter extends RecyclerView.Adapter {
        private int loadingUpRow;
        private Context mContext;
        private int messagesEndRow;
        private int messagesStartRow;
        private int rowCount;
        private final ArrayList<Long> oldStableIds = new ArrayList<>();
        private final ArrayList<Long> stableIds = new ArrayList<>();

        public ChatActivityAdapter(Context context) {
            this.mContext = context;
            setHasStableIds(true);
        }

        public void updateRows() {
            updateRows(true);
        }

        public void updateRows(boolean z) {
            this.rowCount = 0;
            if (!ChannelAdminLogActivity.this.filteredMessages.isEmpty()) {
                if (!ChannelAdminLogActivity.this.endReached) {
                    int i = this.rowCount;
                    this.rowCount = i + 1;
                    this.loadingUpRow = i;
                } else {
                    this.loadingUpRow = -1;
                }
                int i2 = this.rowCount;
                this.messagesStartRow = i2;
                int size = i2 + ChannelAdminLogActivity.this.filteredMessages.size();
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
        public long getItemId(int i) {
            if (i < this.messagesStartRow || i >= this.messagesEndRow) {
                return i == this.loadingUpRow ? 2L : 5L;
            }
            return ((MessageObject) ChannelAdminLogActivity.this.filteredMessages.get((ChannelAdminLogActivity.this.filteredMessages.size() - (i - this.messagesStartRow)) - 1)).stableId;
        }

        public MessageObject getMessageObject(int i) {
            if (i < this.messagesStartRow || i >= this.messagesEndRow) {
                return null;
            }
            return (MessageObject) ChannelAdminLogActivity.this.filteredMessages.get((ChannelAdminLogActivity.this.filteredMessages.size() - (i - this.messagesStartRow)) - 1);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View chatLoadingCell;
            View chatMessageCell;
            if (i == 0) {
                if (!ChannelAdminLogActivity.this.chatMessageCellsCache.isEmpty()) {
                    View view = (View) ChannelAdminLogActivity.this.chatMessageCellsCache.get(0);
                    ChannelAdminLogActivity.this.chatMessageCellsCache.remove(0);
                    chatMessageCell = view;
                } else {
                    chatMessageCell = new ChatMessageCell(this.mContext, ((BaseFragment) ChannelAdminLogActivity.this).currentAccount);
                }
                ChatMessageCell chatMessageCell2 = (ChatMessageCell) chatMessageCell;
                chatMessageCell2.setDelegate(new AnonymousClass1());
                chatMessageCell2.setAllowAssistant(true);
                chatLoadingCell = chatMessageCell;
            } else if (i == 1) {
                ChatActionCell chatActionCell = new ChatActionCell(this.mContext) { // from class: org.telegram.ui.ChannelAdminLogActivity.ChatActivityAdapter.2
                    @Override // org.telegram.ui.Cells.ChatActionCell, android.view.View
                    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
                        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
                        accessibilityNodeInfo.setVisibleToUser(true);
                    }
                };
                chatActionCell.setDelegate(new AnonymousClass3());
                chatLoadingCell = chatActionCell;
            } else if (i == 2) {
                chatLoadingCell = new ChatUnreadCell(this.mContext, null);
            } else {
                chatLoadingCell = new ChatLoadingCell(this.mContext, ChannelAdminLogActivity.this.contentView, null);
            }
            chatLoadingCell.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
            return new RecyclerListView.Holder(chatLoadingCell);
        }

        public class AnonymousClass1 implements ChatMessageCell.ChatMessageCellDelegate {
            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean canDrawOutboundsContent() {
                return true;
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean canPerformActions() {
                return true;
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressCancelSendButton(ChatMessageCell chatMessageCell) {
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressViaBot(ChatMessageCell chatMessageCell, String str) {
            }

            public AnonymousClass1() {
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressSideButton(ChatMessageCell chatMessageCell) {
                if (ChannelAdminLogActivity.this.getParentActivity() == null) {
                    return;
                }
                ChatActivityAdapter chatActivityAdapter = ChatActivityAdapter.this;
                ChannelAdminLogActivity.this.showDialog(ShareAlert.createShareAlert(chatActivityAdapter.mContext, chatMessageCell.getMessageObject(), null, ChatObject.isChannel(ChannelAdminLogActivity.this.currentChat) && !ChannelAdminLogActivity.this.currentChat.megagroup, null, false));
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean needPlayMessage(ChatMessageCell chatMessageCell, MessageObject messageObject, boolean z) {
                if (messageObject.isVoice() || messageObject.isRoundVideo()) {
                    boolean zPlayMessage = MediaController.getInstance().playMessage(messageObject, z);
                    MediaController.getInstance().setVoiceMessagesPlaylist(null, false);
                    return zPlayMessage;
                }
                if (messageObject.isMusic()) {
                    return MediaController.getInstance().setPlaylist(ChannelAdminLogActivity.this.filteredMessages, messageObject, 0L);
                }
                return false;
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressChannelAvatar(ChatMessageCell chatMessageCell, TLRPC.Chat chat, int i, float f, float f2, boolean z) {
                if (chat == null || chat == ChannelAdminLogActivity.this.currentChat) {
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putLong("chat_id", chat.id);
                if (i != 0) {
                    bundle.putInt("message_id", i);
                }
                if (MessagesController.getInstance(((BaseFragment) ChannelAdminLogActivity.this).currentAccount).checkCanOpenChat(bundle, ChannelAdminLogActivity.this)) {
                    ChannelAdminLogActivity.this.presentFragment(new ChatActivity(bundle), true);
                }
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressOther(ChatMessageCell chatMessageCell, float f, float f2) {
                ChannelAdminLogActivity.this.createMenu(chatMessageCell);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressUserAvatar(ChatMessageCell chatMessageCell, TLRPC.User user, float f, float f2, boolean z) {
                if (user == null || user.id == UserConfig.getInstance(((BaseFragment) ChannelAdminLogActivity.this).currentAccount).getClientUserId()) {
                    return;
                }
                openProfile(user);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public boolean didLongPressUserAvatar(final ChatMessageCell chatMessageCell, final TLRPC.User user, float f, float f2) {
                AvatarPreviewer.Data dataOf;
                if (user == null || user.id == UserConfig.getInstance(((BaseFragment) ChannelAdminLogActivity.this).currentAccount).getClientUserId()) {
                    return false;
                }
                TLRPC.User user2 = ChannelAdminLogActivity.this.getMessagesController().getUser(Long.valueOf(user.id));
                if (user2 != null) {
                    user = user2;
                }
                AvatarPreviewer.MenuItem[] menuItemArr = {AvatarPreviewer.MenuItem.OPEN_PROFILE, AvatarPreviewer.MenuItem.SEND_MESSAGE};
                TLRPC.UserFull userFull = ChannelAdminLogActivity.this.getMessagesController().getUserFull(user.id);
                if (userFull == null) {
                    dataOf = AvatarPreviewer.Data.of(user, ((BaseFragment) ChannelAdminLogActivity.this).classGuid, menuItemArr);
                } else {
                    dataOf = AvatarPreviewer.Data.of(user, userFull, menuItemArr);
                }
                if (!AvatarPreviewer.canPreview(dataOf)) {
                    return false;
                }
                AvatarPreviewer avatarPreviewer = AvatarPreviewer.getInstance();
                ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                avatarPreviewer.show((ViewGroup) channelAdminLogActivity.fragmentView, channelAdminLogActivity.getResourceProvider(), dataOf, new AvatarPreviewer.Callback() { // from class: org.telegram.ui.ChannelAdminLogActivity$ChatActivityAdapter$1$$ExternalSyntheticLambda1
                    @Override // org.telegram.ui.AvatarPreviewer.Callback
                    public final void onMenuClick(AvatarPreviewer.MenuItem menuItem) {
                        this.f$0.lambda$didLongPressUserAvatar$0(chatMessageCell, user, menuItem);
                    }
                });
                return true;
            }

            public void lambda$didPressUrl$1(String str, DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    Browser.openUrl((Context) ChannelAdminLogActivity.this.getParentActivity(), str, true);
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
                ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                EmbedBottomSheet.show(channelAdminLogActivity, messageObject, channelAdminLogActivity.provider, str2, str3, str4, str, i, i2, false);
            }

            @Override // org.telegram.ui.Cells.ChatMessageCell.ChatMessageCellDelegate
            public void didPressReplyMessage(ChatMessageCell chatMessageCell, int i, float f, float f2, boolean z) {
                MessageObject messageObject = chatMessageCell.getMessageObject().replyMessageObject;
                if (messageObject.getDialogId() == (-ChannelAdminLogActivity.this.currentChat.id)) {
                    for (int i2 = 0; i2 < ChannelAdminLogActivity.this.filteredMessages.size(); i2++) {
                        MessageObject messageObject2 = (MessageObject) ChannelAdminLogActivity.this.filteredMessages.get(i2);
                        if (messageObject2 != null && messageObject2.contentType != 1 && messageObject2.getRealId() == messageObject.getRealId()) {
                            ChannelAdminLogActivity.this.scrollToMessage(messageObject2, true);
                            return;
                        }
                    }
                }
                Bundle bundle = new Bundle();
                bundle.putLong("chat_id", ChannelAdminLogActivity.this.currentChat.id);
                bundle.putInt("message_id", messageObject.getRealId());
                ChannelAdminLogActivity.this.presentFragment(new ChatActivity(bundle));
            }

            void lambda$needOpenInviteLink$0(boolean[] zArr, DialogInterface dialogInterface) {
                ChannelAdminLogActivity.this.linviteLoading = false;
                zArr[0] = true;
            }

            public class AnonymousClass21 {
        static final void lambda$startMessageUnselect$25() {
        this.highlightMessageId = Integer.MAX_VALUE;
        this.highlightMessageQuoteFirst = false;
        this.highlightMessageQuote = null;
        this.highlightMessageQuoteOffset = -1;
        this.showNoQuoteAlert = false;
        updateVisibleRows();
        this.unselectRunnable = null;
    }

    private void removeSelectedMessageHighlight() {
        if (this.highlightMessageQuote != null) {
            return;
        }
        Runnable runnable = this.unselectRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
            this.unselectRunnable = null;
        }
        this.highlightMessageId = Integer.MAX_VALUE;
        this.highlightMessageQuoteFirst = false;
        this.highlightMessageQuote = null;
    }

    public void updateVisibleRows() {
        updateVisibleRows(false);
    }

    private void updateVisibleRows(boolean z) {
        String str;
        ChatListRecyclerView chatListRecyclerView = this.chatListView;
        if (chatListRecyclerView == null) {
            return;
        }
        int childCount = chatListRecyclerView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childAt = this.chatListView.getChildAt(i);
            if (childAt instanceof ChatMessageCell) {
                ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                MessageObject messageObject = chatMessageCell.getMessageObject();
                if (messageObject != null) {
                    if (this.actionBar.isActionModeShowed()) {
                        this.highlightMessageQuoteFirst = false;
                        this.highlightMessageQuote = null;
                    } else {
                        chatMessageCell.setDrawSelectionBackground(false);
                        chatMessageCell.setCheckBoxVisible(false, true);
                        chatMessageCell.setChecked(false, false, true);
                    }
                    chatMessageCell.setHighlighted(this.highlightMessageId != Integer.MAX_VALUE && messageObject.getRealId() == this.highlightMessageId);
                    if (this.highlightMessageId != Integer.MAX_VALUE) {
                        startMessageUnselect();
                    }
                    if (chatMessageCell.isHighlighted() && (str = this.highlightMessageQuote) != null) {
                        if (!chatMessageCell.setHighlightedText(str, true, this.highlightMessageQuoteOffset, this.highlightMessageQuoteFirst) && this.showNoQuoteAlert) {
                            showNoQuoteFound();
                        }
                        this.highlightMessageQuoteFirst = false;
                        this.showNoQuoteAlert = false;
                    } else if (!TextUtils.isEmpty(this.searchQuery)) {
                        chatMessageCell.setHighlightedText(this.searchQuery);
                    } else {
                        chatMessageCell.setHighlightedText(null);
                    }
                    chatMessageCell.setSpoilersSuppressed(this.chatListView.getScrollState() != 0);
                }
            } else if (childAt instanceof ChatActionCell) {
                ChatActionCell chatActionCell = (ChatActionCell) childAt;
                if (!z) {
                    chatActionCell.setMessageObject(chatActionCell.getMessageObject());
                }
                chatActionCell.setSpoilersSuppressed(this.chatListView.getScrollState() != 0);
            }
        }
    }

    public void showNoQuoteFound() {
        BulletinFactory.of(this).createSimpleBulletin(R.raw.error, LocaleController.getString(R.string.QuoteNotFound)).show(true);
    }

    private int getScrollOffsetForMessage(MessageObject messageObject) {
        return getScrollOffsetForMessage(getHeightForMessage(messageObject, !TextUtils.isEmpty(this.highlightMessageQuote))) - scrollOffsetForQuote(messageObject);
    }

    private int getScrollOffsetForMessage(int i) {
        return Math.max(-AndroidUtilities.dp(2.0f), (this.chatListView.getMeasuredHeight() - i) / 2);
    }

    private int scrollOffsetForQuote(MessageObject messageObject) {
        ArrayList<MessageObject.TextLayoutBlock> arrayList;
        CharSequence charSequence;
        int iDp;
        int iFindQuoteStart;
        float lineTop;
        ChatMessageCell chatMessageCell;
        MessageObject.TextLayoutBlocks textLayoutBlocks;
        if (TextUtils.isEmpty(this.highlightMessageQuote) || messageObject == null) {
            ChatMessageCell chatMessageCell2 = this.dummyMessageCell;
            if (chatMessageCell2 != null) {
                chatMessageCell2.computedGroupCaptionY = 0;
                chatMessageCell2.computedCaptionLayout = null;
            }
            return 0;
        }
        if (!TextUtils.isEmpty(messageObject.caption) && (chatMessageCell = this.dummyMessageCell) != null && (textLayoutBlocks = chatMessageCell.captionLayout) != null) {
            iDp = (int) chatMessageCell.captionY;
            charSequence = messageObject.caption;
            arrayList = textLayoutBlocks.textLayoutBlocks;
        } else {
            CharSequence charSequence2 = messageObject.messageText;
            arrayList = messageObject.textLayoutBlocks;
            ChatMessageCell chatMessageCell3 = this.dummyMessageCell;
            if (chatMessageCell3 == null || !chatMessageCell3.linkPreviewAbove) {
                charSequence = charSequence2;
                iDp = 0;
            } else {
                iDp = chatMessageCell3.linkPreviewHeight + AndroidUtilities.dp(10.0f);
                charSequence = charSequence2;
            }
        }
        ChatMessageCell chatMessageCell4 = this.dummyMessageCell;
        if (chatMessageCell4 != null) {
            chatMessageCell4.computedGroupCaptionY = 0;
            chatMessageCell4.computedCaptionLayout = null;
        }
        if (arrayList == null || charSequence == null || (iFindQuoteStart = MessageObject.findQuoteStart(charSequence.toString(), this.highlightMessageQuote, this.highlightMessageQuoteOffset)) < 0) {
            return 0;
        }
        for (int i = 0; i < arrayList.size(); i++) {
            MessageObject.TextLayoutBlock textLayoutBlock = arrayList.get(i);
            StaticLayout staticLayout = textLayoutBlock.textLayout;
            String string = staticLayout.getText().toString();
            int i2 = textLayoutBlock.charactersOffset;
            if (iFindQuoteStart > i2) {
                if (iFindQuoteStart - i2 > string.length() - 1) {
                    lineTop = iDp + ((int) (textLayoutBlock.textYOffset(arrayList) + textLayoutBlock.padTop + textLayoutBlock.height));
                } else {
                    lineTop = staticLayout.getLineTop(staticLayout.getLineForOffset(iFindQuoteStart - textLayoutBlock.charactersOffset)) + iDp + textLayoutBlock.textYOffset(arrayList) + textLayoutBlock.padTop;
                }
                if (lineTop > AndroidUtilities.displaySize.y * (isKeyboardVisible() ? 0.7f : 0.5f)) {
                    return (int) (lineTop - (AndroidUtilities.displaySize.y * (isKeyboardVisible() ? 0.7f : 0.5f)));
                }
                return 0;
            }
        }
        return 0;
    }

    private int getHeightForMessage(MessageObject messageObject, boolean z) {
        boolean z2 = false;
        if (getParentActivity() == null) {
            return 0;
        }
        if (this.dummyMessageCell == null) {
            this.dummyMessageCell = new ChatMessageCell(getParentActivity(), this.currentAccount);
        }
        ChatMessageCell chatMessageCell = this.dummyMessageCell;
        TLRPC.Chat chat = this.currentChat;
        chatMessageCell.isChat = chat != null;
        if (ChatObject.isChannel(chat) && this.currentChat.megagroup) {
            z2 = true;
        }
        chatMessageCell.isMegagroup = z2;
        return this.dummyMessageCell.computeHeight(messageObject, null, z);
    }

    public boolean isKeyboardVisible() {
        return this.contentView.getKeyboardHeight() > AndroidUtilities.dp(20.0f);
    }

    public class ChatScrollCallback extends RecyclerAnimationScrollHelper.AnimationCallback {
        private boolean lastBottom;
        private int lastItemOffset;
        private int lastPadding;
        private MessageObject scrollTo;
        private int position = 0;
        private boolean bottom = true;
        private int offset = 0;

        public ChatScrollCallback() {
        }

        @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimationCallback
        public void onStartAnimation() {
            super.onStartAnimation();
            ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
            channelAdminLogActivity.scrollCallbackAnimationIndex = channelAdminLogActivity.getNotificationCenter().setAnimationInProgress(ChannelAdminLogActivity.this.scrollCallbackAnimationIndex, ChannelAdminLogActivity.allowedNotificationsDuringChatListAnimations);
        }

        @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimationCallback
        public void onEndAnimation() {
            MessageObject messageObject = this.scrollTo;
            ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
            if (messageObject != null) {
                int iIndexOf = channelAdminLogActivity.chatAdapter.messagesStartRow + ChannelAdminLogActivity.this.filteredMessages.indexOf(this.scrollTo);
                if (iIndexOf >= 0) {
                    ChannelAdminLogActivity.this.chatLayoutManager.scrollToPositionWithOffset(iIndexOf, this.lastItemOffset + this.lastPadding, this.lastBottom);
                }
            } else {
                channelAdminLogActivity.chatLayoutManager.scrollToPositionWithOffset(this.position, this.offset, this.bottom);
            }
            this.scrollTo = null;
            ChannelAdminLogActivity.this.checkTextureViewPosition = true;
            ChannelAdminLogActivity.this.updateVisibleRows();
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ChannelAdminLogActivity$ChatScrollCallback$$ExternalSyntheticLambda0
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$onEndAnimation$0();
                }
            });
        }

        public /* synthetic */ void lambda$onEndAnimation$0() {
            ChannelAdminLogActivity.this.getNotificationCenter().onAnimationFinish(ChannelAdminLogActivity.this.scrollCallbackAnimationIndex);
        }

        @Override // org.telegram.ui.Components.RecyclerAnimationScrollHelper.AnimationCallback
        public void recycleView(View view) {
            if (view instanceof ChatMessageCell) {
                ChannelAdminLogActivity.this.chatMessageCellsCache.add((ChatMessageCell) view);
            }
        }
    }

    /* JADX WARN: Code duplicated, block: B:21:0x003a  */
    public void saveScrollPosition(boolean z) {
        long j;
        ChatListRecyclerView chatListRecyclerView = this.chatListView;
        if (chatListRecyclerView == null || this.chatLayoutManager == null || chatListRecyclerView.getChildCount() <= 0) {
            return;
        }
        int top = z ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        View view = null;
        int i = -1;
        for (int i2 = 0; i2 < this.chatListView.getChildCount(); i2++) {
            View childAt = this.chatListView.getChildAt(i2);
            int childAdapterPosition = this.chatListView.getChildAdapterPosition(childAt);
            if (childAdapterPosition >= 0) {
                int top2 = childAt.getTop();
                if (z) {
                    if (top2 < top) {
                        top = childAt.getTop();
                        view = childAt;
                        i = childAdapterPosition;
                    }
                } else if (top2 > top) {
                    top = childAt.getTop();
                    view = childAt;
                    i = childAdapterPosition;
                }
            }
        }
        if (view != null) {
            if (view instanceof ChatMessageCell) {
                j = ((ChatMessageCell) view).getMessageObject().eventId;
            } else {
                j = view instanceof ChatActionCell ? ((ChatActionCell) view).getMessageObject().eventId : 0L;
            }
            this.savedScrollEventId = j;
            this.savedScrollPosition = i;
            this.savedScrollOffset = getScrollingOffsetForView(view);
        }
    }

    private int getScrollingOffsetForView(View view) {
        return (this.chatListView.getMeasuredHeight() - view.getBottom()) - this.chatListView.getPaddingBottom();
    }

    public void applyScrolledPosition() {
        int i;
        if (this.chatListView == null || this.chatLayoutManager == null || (i = this.savedScrollPosition) < 0) {
            return;
        }
        if (this.savedScrollEventId != 0) {
            for (int i2 = 0; i2 < this.chatAdapter.getItemCount(); i2++) {
                MessageObject messageObject = this.chatAdapter.getMessageObject(i2);
                if (messageObject != null && messageObject.eventId == this.savedScrollEventId) {
                    i = i2;
                    break;
                }
            }
        }
        this.chatLayoutManager.scrollToPositionWithOffset(i, this.savedScrollOffset, true);
        this.savedScrollPosition = -1;
        this.savedScrollEventId = 0L;
    }

    public void invalidateMergedVisibleBlurredPositionsAndSourcesPositions() {
        invalidateMergedVisibleBlurredPositionsAndSources(2);
    }

    public void invalidateMergedVisibleBlurredPositionsAndSources(int i) {
        if (Build.VERSION.SDK_INT < 31 || this.scrollableViewNoiseSuppressor == null) {
            return;
        }
        this.invalidateBlurredSourcesView.invalidate(i);
    }

    public void invalidateMergedVisibleBlurredPositionsAndSourcesImpl(int i) {
        if (Build.VERSION.SDK_INT < 31 || this.scrollableViewNoiseSuppressor == null) {
            return;
        }
        BitwiseUtils.hasFlag(i, 4);
        if (BitwiseUtils.hasFlag(i, 2)) {
            int mergedVisibleBlurredPositions = getMergedVisibleBlurredPositions(this.glassDrawablesPositionsMerged);
            this.glassDrawablesPositionsCount = mergedVisibleBlurredPositions;
            this.scrollableViewNoiseSuppressor.setupRenderNodes(this.glassDrawablesPositionsMerged, mergedVisibleBlurredPositions);
        }
        DownscaleScrollableNoiseSuppressor downscaleScrollableNoiseSuppressor = this.scrollableViewNoiseSuppressor;
        final ChatActivityFragmentView chatActivityFragmentView = this.contentView;
        Objects.requireNonNull(chatActivityFragmentView);
        if (downscaleScrollableNoiseSuppressor.invalidateResultRenderNodes(new IBlur3Capture() { // from class: org.telegram.ui.ChannelAdminLogActivity$$ExternalSyntheticLambda11
            @Override // org.telegram.ui.Components.blur3.capture.IBlur3Capture
            public final void capture(Canvas canvas, RectF rectF) {
                chatActivityFragmentView.drawList(canvas, rectF);
            }
        }, this.contentView.getWidth(), this.contentView.getHeight())) {
            BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode = this.glassBackgroundSourceRenderNode;
            if (blurredBackgroundSourceRenderNode != null) {
                blurredBackgroundSourceRenderNode.invalidateDisplayListForDrawables();
            }
            BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode2 = this.glassBackgroundSourceFrostedRenderNode;
            if (blurredBackgroundSourceRenderNode2 != null) {
                blurredBackgroundSourceRenderNode2.invalidateDisplayListForDrawables();
            }
            ActionBar actionBar = this.actionBar;
            if (actionBar != null) {
                actionBar.invalidate();
            }
            invalidateAllGlassAttachedViews();
        }
    }

    private int getMergedVisibleBlurredPositions(List<RectF> list) {
        int iMergeOverlapping = RectFMergeBounding.mergeOverlapping(this.glassDrawablesPositions, getVisibleBlurredPositions(this.glassDrawablesPositions), list);
        int measuredWidth = this.contentView.getMeasuredWidth();
        for (int i = 0; i < iMergeOverlapping; i++) {
            RectF rectF = list.get(i);
            float f = measuredWidth;
            rectF.left = MathUtils.clamp(rectF.left, 0.0f, f);
            rectF.top = Math.max(this.chatListView.getY(), rectF.top);
            rectF.right = MathUtils.clamp(rectF.right, 0.0f, f);
            rectF.bottom = Math.min(this.chatListView.getY() + this.chatListView.getMeasuredHeight(), rectF.bottom);
        }
        return iMergeOverlapping;
    }

    private int getVisibleBlurredPositions(List<RectF> list) {
        RectF rectF;
        int visiblePositions = 0;
        if (Build.VERSION.SDK_INT < 29) {
            return 0;
        }
        if (this.glassBackgroundSourceFrostedRenderNode != null) {
            if (list.isEmpty()) {
                rectF = new RectF();
                list.add(rectF);
            } else {
                rectF = list.get(0);
            }
            rectF.set(0.0f, 0.0f, this.contentView.getMeasuredWidth(), this.chatListView.getPaddingTop() + this.chatListView.getY());
            rectF.inset(0.0f, -AndroidUtilities.dp(45.0f));
            visiblePositions = 1 + this.glassBackgroundSourceFrostedRenderNode.getVisiblePositions(list, 1, AndroidUtilities.dp(48.0f));
        }
        BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode = this.glassBackgroundSourceRenderNode;
        return blurredBackgroundSourceRenderNode != null ? visiblePositions + blurredBackgroundSourceRenderNode.getVisiblePositions(list, visiblePositions, AndroidUtilities.dp(8.0f)) : visiblePositions;
    }

    private void invalidateAllGlassAttachedViews() {
        this.contentView.invalidate();
        Iterator<View> it = this.glassAttachedViews.iterator();
        while (it.hasNext()) {
            it.next().invalidate();
        }
    }

    public boolean quickRejectChild(View view, RectF rectF) {
        if (rectF == null || this.chatListView == null || view == null) {
            return false;
        }
        this.tmpViewRectF.set(view.getX(), view.getY() + this.chatListView.getY(), view.getX() + view.getWidth(), view.getY() + this.chatListView.getY() + view.getHeight());
        return !this.tmpViewRectF.intersect(rectF);
    }

    public class ChatActivityFragmentView extends SizeNotifierFrameLayout {
        @Override // org.telegram.ui.Components.SizeNotifierFrameLayout, org.telegram.ui.ActionBar.Theme.Colorable
        public /* bridge */ /* synthetic */ int[] getColorKeys() {
            return super.getColorKeys();
        }

        public ChatActivityFragmentView(Context context) {
            super(context);
        }

        public void drawList(Canvas canvas, RectF rectF) {
            long jUptimeMillis = SystemClock.uptimeMillis();
            if (ChannelAdminLogActivity.this.chatListView.hasActiveEdgeEffects()) {
                canvas.save();
                canvas.clipRect(rectF);
                drawChild(canvas, ChannelAdminLogActivity.this.chatListView, jUptimeMillis);
                canvas.restore();
                return;
            }
            canvas.save();
            canvas.clipRect(rectF);
            canvas.translate(0.0f, ChannelAdminLogActivity.this.chatListView.getY());
            ChannelAdminLogActivity.this.chatListView.drawChatBackgroundElements(canvas, rectF);
            int i = 0;
            while (true) {
                int childCount = ChannelAdminLogActivity.this.chatListView.getChildCount();
                ChannelAdminLogActivity channelAdminLogActivity = ChannelAdminLogActivity.this;
                if (i < childCount) {
                    View childAt = channelAdminLogActivity.chatListView.getChildAt(i);
                    if (!ChannelAdminLogActivity.this.quickRejectChild(childAt, rectF)) {
                        if (childAt instanceof ChatMessageCell) {
                            canvas.save();
                            canvas.translate(childAt.getX(), childAt.getY());
                            ChatMessageCell chatMessageCell = (ChatMessageCell) childAt;
                            if (chatMessageCell.drawBackgroundInParent()) {
                                canvas.save();
                                canvas.translate(0.0f, chatMessageCell.starsPriceTopPadding);
                                chatMessageCell.drawBackgroundInternal(canvas, true);
                                canvas.restore();
                            }
                            canvas.restore();
                            ChannelAdminLogActivity.this.chatListView.drawChild(canvas, childAt, jUptimeMillis);
                            if (chatMessageCell.hasOutboundsContent()) {
                                canvas.save();
                                canvas.translate(chatMessageCell.getX(), chatMessageCell.getY());
                                chatMessageCell.drawOutboundsContent(canvas);
                                canvas.restore();
                            }
                        } else {
                            boolean z = childAt instanceof ChatActionCell;
                            ChannelAdminLogActivity channelAdminLogActivity2 = ChannelAdminLogActivity.this;
                            if (z) {
                                channelAdminLogActivity2.chatListView.drawChild(canvas, childAt, jUptimeMillis);
                                canvas.save();
                                canvas.translate(childAt.getX(), childAt.getY());
                                ((ChatActionCell) childAt).drawOutboundsContent(canvas);
                                canvas.restore();
                            } else {
                                channelAdminLogActivity2.chatListView.drawChild(canvas, childAt, jUptimeMillis);
                            }
                        }
                    }
                    i++;
                } else {
                    channelAdminLogActivity.chatListView.drawChatForegroundElements(canvas, rectF);
                    canvas.restore();
                    return;
                }
            }
        }
    }

    public abstract class ChatListRecyclerView extends RecyclerListView {
        public void drawChatBackgroundElements(Canvas canvas, RectF rectF) {
        }

        public void drawChatForegroundElements(Canvas canvas, RectF rectF) {
        }

        public ChatListRecyclerView(Context context) {
            super(context);
        }
    }
}
