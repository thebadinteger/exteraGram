package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.gms.cast.MediaError;
import java.util.ArrayList;
import org.telegram.PhoneFormat.PhoneFormat;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DownloadController;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.NotificationsController;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PlayingGameDrawable;
import org.telegram.ui.Components.PopupAudioView;
import org.telegram.ui.Components.RecordStatusDrawable;
import org.telegram.ui.Components.RoundStatusDrawable;
import org.telegram.ui.Components.SendingFileDrawable;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.StatusDrawable;
import org.telegram.ui.Components.TypingDotsDrawable;

public class PopupNotificationActivity extends Activity implements NotificationCenter.NotificationCenterDelegate {
    private ActionBar actionBar;
    private FrameLayout avatarContainer;
    private BackupImageView avatarImageView;
    private ViewGroup centerButtonsView;
    private ViewGroup centerView;
    private ChatActivityEnterView chatActivityEnterView;
    private int classGuid;
    private TextView countText;
    private TLRPC.Chat currentChat;
    private TLRPC.User currentUser;
    private boolean isReply;
    private CharSequence lastPrintString;
    private ViewGroup leftButtonsView;
    private ViewGroup leftView;
    private ViewGroup messageContainer;
    private TextView nameTextView;
    private TextView onlineTextView;
    private RelativeLayout popupContainer;
    private ViewGroup rightButtonsView;
    private ViewGroup rightView;
    private ArrayList<ViewGroup> textViews = new ArrayList<>();
    private ArrayList<ViewGroup> imageViews = new ArrayList<>();
    private ArrayList<ViewGroup> audioViews = new ArrayList<>();
    private VelocityTracker velocityTracker = null;
    private StatusDrawable[] statusDrawables = new StatusDrawable[5];
    private int lastResumedAccount = -1;
    private boolean finished = false;
    private MessageObject currentMessageObject = null;
    private MessageObject[] setMessageObjects = new MessageObject[3];
    private int currentMessageNum = 0;
    private PowerManager.WakeLock wakeLock = null;
    private boolean animationInProgress = false;
    private long animationStartTime = 0;
    private float moveStartX = -1.0f;
    private boolean startedMoving = false;
    private Runnable onAnimationEndRunnable = null;
    private ArrayList<MessageObject> popupMessages = new ArrayList<>();

    public class FrameLayoutTouch extends FrameLayout {
        public FrameLayoutTouch(Context context) {
            super(context);
        }

        @Override // android.view.ViewGroup
        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(motionEvent);
        }

        @Override // android.view.View
        public boolean onTouchEvent(MotionEvent motionEvent) {
            return PopupNotificationActivity.this.checkTransitionAnimation() || ((PopupNotificationActivity) getContext()).onTouchEventMy(motionEvent);
        }

        @Override // android.view.ViewGroup, android.view.ViewParent
        public void requestDisallowInterceptTouchEvent(boolean z) {
            ((PopupNotificationActivity) getContext()).onTouchEventMy(null);
            super.requestDisallowInterceptTouchEvent(z);
        }
    }

    @Override // android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        Theme.createDialogsResources(this);
        Theme.createChatResources(this, false);
        AndroidUtilities.fillStatusBarHeight(this, false);
        for (int i = 0; i < 16; i++) {
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(i).addObserver(this, NotificationCenter.contactsDidLoad);
        }
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        this.classGuid = ConnectionsManager.generateClassGuid();
        this.statusDrawables[0] = new TypingDotsDrawable(false);
        this.statusDrawables[1] = new RecordStatusDrawable(false);
        this.statusDrawables[2] = new SendingFileDrawable(false);
        this.statusDrawables[3] = new PlayingGameDrawable(false, null);
        this.statusDrawables[4] = new RoundStatusDrawable(false);
        SizeNotifierFrameLayout sizeNotifierFrameLayout = new SizeNotifierFrameLayout(this) { // from class: org.telegram.ui.PopupNotificationActivity.1
            @Override // android.widget.FrameLayout, android.view.View
            public void onMeasure(int i2, int i3) {
                AnonymousClass1 anonymousClass1;
                int i4;
                int i5;
                View.MeasureSpec.getMode(i2);
                View.MeasureSpec.getMode(i3);
                int size = View.MeasureSpec.getSize(i2);
                int size2 = View.MeasureSpec.getSize(i3);
                setMeasuredDimension(size, size2);
                if (measureKeyboardHeight() <= AndroidUtilities.dp(20.0f)) {
                    size2 -= PopupNotificationActivity.this.chatActivityEnterView.getEmojiPadding();
                }
                int childCount = getChildCount();
                int i6 = 0;
                while (i6 < childCount) {
                    View childAt = this.getChildAt(i6);
                    if (childAt.getVisibility() == 8) {
                        anonymousClass1 = this;
                        i4 = i2;
                        i5 = i3;
                    } else if (PopupNotificationActivity.this.chatActivityEnterView.isPopupView(childAt)) {
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(size, TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(childAt.getLayoutParams().height, TLObject.FLAG_30));
                        anonymousClass1 = this;
                        i4 = i2;
                        i5 = i3;
                    } else if (PopupNotificationActivity.this.chatActivityEnterView.isRecordCircle(childAt)) {
                        anonymousClass1 = this;
                        int i7 = i2;
                        i5 = i3;
                        anonymousClass1.measureChildWithMargins(childAt, i7, 0, i5, 0);
                        i4 = i7;
                    } else {
                        anonymousClass1 = this;
                        i4 = i2;
                        i5 = i3;
                        childAt.measure(View.MeasureSpec.makeMeasureSpec(size, TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(Math.max(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(2.0f) + size2), TLObject.FLAG_30));
                    }
                    i6++;
                    i2 = i4;
                    this = anonymousClass1;
                    i3 = i5;
                }
            }

            void lambda$onRequestPermissionsResult$0(AlertDialog alertDialog, int i) {
        try {
            Intent intent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
            startActivity(intent);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void switchToNextMessage() {
        if (this.popupMessages.size() > 1) {
            if (this.currentMessageNum < this.popupMessages.size() - 1) {
                this.currentMessageNum++;
            } else {
                this.currentMessageNum = 0;
            }
            this.currentMessageObject = this.popupMessages.get(this.currentMessageNum);
            updateInterfaceForCurrentMessage(2);
            this.countText.setText(String.format("%d/%d", Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())));
        }
    }

    private void switchToPreviousMessage() {
        if (this.popupMessages.size() > 1) {
            int i = this.currentMessageNum;
            if (i > 0) {
                this.currentMessageNum = i - 1;
            } else {
                this.currentMessageNum = this.popupMessages.size() - 1;
            }
            this.currentMessageObject = this.popupMessages.get(this.currentMessageNum);
            updateInterfaceForCurrentMessage(1);
            this.countText.setText(String.format("%d/%d", Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())));
        }
    }

    public boolean checkTransitionAnimation() {
        if (this.animationInProgress && this.animationStartTime < System.currentTimeMillis() - 400) {
            this.animationInProgress = false;
            Runnable runnable = this.onAnimationEndRunnable;
            if (runnable != null) {
                runnable.run();
                this.onAnimationEndRunnable = null;
            }
        }
        return this.animationInProgress;
    }

    boolean $r8$lambda$h5vaCQOS0Y721s71pMjDPrRTnU4(View view, MotionEvent motionEvent) {
        return true;
    }

    public static /* synthetic */ void m17613$r8$lambda$0ZtfsiHWK_MLtvBGQtp_ZoMqlU(int i, MessageObject messageObject, View view) {
        TLRPC.KeyboardButton keyboardButton = (TLRPC.KeyboardButton) view.getTag();
        if (keyboardButton != null) {
            SendMessagesHelper.getInstance(i).sendNotificationCallback(messageObject.getDialogId(), messageObject.getId(), keyboardButton.data);
        }
    }

    public /* synthetic */ void lambda$getViewForMessage$6(View view) {
        openCurrentMessage();
    }

    public /* synthetic */ void lambda$getViewForMessage$7(View view) {
        openCurrentMessage();
    }

    public /* synthetic */ void lambda$getViewForMessage$8(View view) {
        openCurrentMessage();
    }

    /* JADX WARN: Code duplicated, block: B:42:0x0154  */
    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r2v12 */
    /* JADX WARN: Type inference failed for: r2v15, types: [android.view.View, android.view.ViewGroup, android.widget.FrameLayout] */
    /* JADX WARN: Type inference failed for: r2v16 */
    /* JADX WARN: Type inference failed for: r2v20 */
    /* JADX WARN: Type inference failed for: r2v21 */
    /* JADX WARN: Type inference failed for: r2v22 */
    /* JADX WARN: Type inference failed for: r2v23 */
    /* JADX WARN: Type inference failed for: r2v24 */
    /* JADX WARN: Type inference failed for: r2v25 */
    /* JADX WARN: Type inference failed for: r2v26 */
    /* JADX WARN: Type inference failed for: r2v27 */
    /* JADX WARN: Type inference failed for: r2v28 */
    /* JADX WARN: Type inference failed for: r2v29 */
    /* JADX WARN: Type inference failed for: r2v30 */
    /* JADX WARN: Type inference failed for: r2v4, types: [android.view.View, android.view.ViewGroup, android.widget.FrameLayout] */
    /* JADX WARN: Type inference failed for: r2v5, types: [android.view.View] */
    /* JADX WARN: Type inference failed for: r2v9, types: [android.view.View, android.view.ViewGroup] */
    /* JADX WARN: Type inference failed for: r3v18, types: [android.view.ViewGroup] */
    /* JADX WARN: Type inference failed for: r3v21, types: [android.view.View, android.view.ViewGroup, android.widget.FrameLayout] */
    /* JADX WARN: Type inference failed for: r3v22, types: [android.view.View] */
    /* JADX WARN: Type inference failed for: r3v33 */
    /* JADX WARN: Type inference failed for: r3v34 */
    /* JADX WARN: Type inference failed for: r5v7, types: [android.view.View, android.widget.ScrollView] */
    private ViewGroup getViewForMessage(int i, boolean z) {
        int i2;
        int i3;
        ?? r2;
        ?? r3;
        ?? r4;
        PopupAudioView popupAudioView;
        ?? r5;
        int size = i;
        Integer numValueOf = Integer.valueOf(MediaError.DetailedErrorCode.SEGMENT_NETWORK);
        Integer numValueOf2 = Integer.valueOf(MediaError.DetailedErrorCode.HLS_NETWORK_PLAYLIST);
        Integer numValueOf3 = Integer.valueOf(MediaError.DetailedErrorCode.HLS_NETWORK_MASTER_PLAYLIST);
        if (this.popupMessages.size() == 1 && (size < 0 || size >= this.popupMessages.size())) {
            return null;
        }
        ArrayList<MessageObject> arrayList = this.popupMessages;
        if (size == -1) {
            size = arrayList.size() - 1;
        } else if (size == arrayList.size()) {
            size = 0;
        }
        MessageObject messageObject = this.popupMessages.get(size);
        int i4 = messageObject.type;
        if ((i4 == 1 || i4 == 4) && !messageObject.isSecretMedia()) {
            if (this.imageViews.size() > 0) {
                ViewGroup viewGroup = this.imageViews.get(0);
                this.imageViews.remove(0);
                i3 = 0;
                i2 = 2;
                r2 = viewGroup;
            } else {
                ?? frameLayout = new FrameLayout(this);
                FrameLayout frameLayout2 = new FrameLayout(this);
                i2 = 2;
                i3 = 0;
                frameLayout2.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
                frameLayout2.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                frameLayout.addView(frameLayout2, LayoutHelper.createFrame(-1, -1.0f));
                BackupImageView backupImageView = new BackupImageView(this);
                backupImageView.setTag(numValueOf3);
                frameLayout2.addView(backupImageView, LayoutHelper.createFrame(-1, -1.0f));
                TextView textView = new TextView(this);
                textView.setTextColor(Theme.getColor(Theme.key_windowBackgroundWhiteBlackText));
                textView.setTextSize(1, 16.0f);
                textView.setGravity(17);
                textView.setTag(numValueOf2);
                frameLayout2.addView(textView, LayoutHelper.createFrame(-1, -2, 17));
                frameLayout.setTag(2);
                frameLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda6
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$getViewForMessage$6(view);
                    }
                });
                r2 = frameLayout;
            }
            TextView textView2 = (TextView) r2.findViewWithTag(numValueOf2);
            BackupImageView backupImageView2 = (BackupImageView) r2.findViewWithTag(numValueOf3);
            backupImageView2.setAspectFit(true);
            int i5 = messageObject.type;
            if (i5 == 1) {
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, AndroidUtilities.getPhotoSize());
                TLRPC.PhotoSize closestPhotoSizeWithSize2 = FileLoader.getClosestPhotoSizeWithSize(messageObject.photoThumbs, 100);
                if (closestPhotoSizeWithSize != null) {
                    int i6 = (messageObject.type != 1 || FileLoader.getInstance(UserConfig.selectedAccount).getPathToMessage(messageObject.messageOwner).exists()) ? 1 : i3;
                    if (!messageObject.needDrawBluredPreview()) {
                        if (i6 != 0 || DownloadController.getInstance(messageObject.currentAccount).canDownloadMedia(messageObject)) {
                            backupImageView2.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize, messageObject.photoThumbsObject), "100_100", ImageLocation.getForObject(closestPhotoSizeWithSize2, messageObject.photoThumbsObject), "100_100_b", closestPhotoSizeWithSize.size, messageObject);
                        } else if (closestPhotoSizeWithSize2 != null) {
                            backupImageView2.setImage(ImageLocation.getForObject(closestPhotoSizeWithSize2, messageObject.photoThumbsObject), "100_100_b", (String) null, (Drawable) null, messageObject);
                        } else {
                            int i7 = i3;
                            backupImageView2.setVisibility(8);
                            textView2.setVisibility(i7);
                            textView2.setTextSize(i2, SharedConfig.fontSize);
                            textView2.setText(messageObject.messageText);
                            r3 = r2;
                        }
                        backupImageView2.setVisibility(i3);
                        textView2.setVisibility(8);
                        r3 = r2;
                    } else {
                        int i8 = i3;
                        backupImageView2.setVisibility(8);
                        textView2.setVisibility(i8);
                        textView2.setTextSize(i2, SharedConfig.fontSize);
                        textView2.setText(messageObject.messageText);
                        r3 = r2;
                    }
                } else {
                    int i9 = i3;
                    backupImageView2.setVisibility(8);
                    textView2.setVisibility(i9);
                    textView2.setTextSize(i2, SharedConfig.fontSize);
                    textView2.setText(messageObject.messageText);
                    r3 = r2;
                }
            } else if (i5 == 4) {
                textView2.setVisibility(8);
                textView2.setText(messageObject.messageText);
                backupImageView2.setVisibility(0);
                TLRPC.GeoPoint geoPoint = messageObject.messageOwner.media.geo;
                double d = geoPoint.lat;
                double d2 = geoPoint._long;
                if (MessagesController.getInstance(messageObject.currentAccount).mapProvider == 2) {
                    r3 = r2;
                    backupImageView2.setImage(ImageLocation.getForWebFile(WebFile.createWithGeoPoint(geoPoint, 100, 100, 15, Math.min(2, (int) Math.ceil(AndroidUtilities.density)))), (String) null, (String) null, (Drawable) null, messageObject);
                    r3 = r2;
                } else {
                    r3 = r2;
                    backupImageView2.setImage(AndroidUtilities.formapMapUrl(messageObject.currentAccount, d, d2, 100, 100, true, 15, -1), null, null);
                    r3 = r2;
                }
            }
        } else if (messageObject.type == 2) {
            if (this.audioViews.size() > 0) {
                ViewGroup viewGroup2 = this.audioViews.get(0);
                this.audioViews.remove(0);
                popupAudioView = (PopupAudioView) viewGroup2.findViewWithTag(300);
                r5 = viewGroup2;
            } else {
                ?? frameLayout3 = new FrameLayout(this);
                FrameLayout frameLayout4 = new FrameLayout(this);
                frameLayout4.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
                frameLayout4.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                frameLayout3.addView(frameLayout4, LayoutHelper.createFrame(-1, -1.0f));
                FrameLayout frameLayout5 = new FrameLayout(this);
                frameLayout4.addView(frameLayout5, LayoutHelper.createFrame(-1, -2.0f, 17, 20.0f, 0.0f, 20.0f, 0.0f));
                PopupAudioView popupAudioView2 = new PopupAudioView(this);
                popupAudioView2.setTag(300);
                frameLayout5.addView(popupAudioView2);
                frameLayout3.setTag(3);
                frameLayout3.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda7
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$getViewForMessage$7(view);
                    }
                });
                popupAudioView = popupAudioView2;
                r5 = frameLayout3;
            }
            popupAudioView.setMessageObject(messageObject);
            r3 = r5;
            if (DownloadController.getInstance(messageObject.currentAccount).canDownloadMedia(messageObject)) {
                popupAudioView.downloadAudioIfNeed();
                r3 = r5;
            }
        } else {
            if (this.textViews.size() > 0) {
                ViewGroup viewGroup3 = this.textViews.get(0);
                this.textViews.remove(0);
                r4 = viewGroup3;
            } else {
                ?? frameLayout6 = new FrameLayout(this);
                ?? scrollView = new ScrollView(this);
                scrollView.setFillViewport(true);
                frameLayout6.addView(scrollView, LayoutHelper.createFrame(-1, -1.0f));
                LinearLayout linearLayout = new LinearLayout(this);
                linearLayout.setOrientation(0);
                linearLayout.setBackgroundDrawable(Theme.getSelectorDrawable(false));
                scrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -2, 1));
                linearLayout.setPadding(AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f), AndroidUtilities.dp(10.0f));
                linearLayout.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.PopupNotificationActivity$$ExternalSyntheticLambda8
                    @Override // android.view.View.OnClickListener
                    public final void onClick(View view) {
                        this.f$0.lambda$getViewForMessage$8(view);
                    }
                });
                TextView textView3 = new TextView(this);
                textView3.setTextSize(1, 16.0f);
                textView3.setTag(numValueOf);
                int i10 = Theme.key_windowBackgroundWhiteBlackText;
                textView3.setTextColor(Theme.getColor(i10));
                textView3.setLinkTextColor(Theme.getColor(i10));
                textView3.setGravity(17);
                linearLayout.addView(textView3, LayoutHelper.createLinear(-1, -2, 17));
                frameLayout6.setTag(1);
                r4 = frameLayout6;
            }
            TextView textView4 = (TextView) r4.findViewWithTag(numValueOf);
            textView4.setTextSize(2, SharedConfig.fontSize);
            textView4.setText(messageObject.messageText);
            r3 = r4;
        }
        r3 = r2;
        if (r3.getParent() == null) {
            this.messageContainer.addView(r3);
        }
        r3.setVisibility(0);
        if (z) {
            int iDp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) r3.getLayoutParams();
            layoutParams.gravity = 51;
            layoutParams.height = -1;
            layoutParams.width = iDp;
            int i11 = this.currentMessageNum;
            if (size == i11) {
                r3.setTranslationX(0.0f);
            } else if (size == i11 - 1) {
                r3.setTranslationX(-iDp);
            } else if (size == i11 + 1) {
                r3.setTranslationX(iDp);
            }
            r3.setLayoutParams(layoutParams);
            r3.invalidate();
        }
        return r3;
    }

    private void reuseButtonsView(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        this.popupContainer.removeView(viewGroup);
    }

    private void reuseView(ViewGroup viewGroup) {
        if (viewGroup == null) {
            return;
        }
        int iIntValue = ((Integer) viewGroup.getTag()).intValue();
        viewGroup.setVisibility(8);
        if (iIntValue == 1) {
            this.textViews.add(viewGroup);
        } else if (iIntValue == 2) {
            this.imageViews.add(viewGroup);
        } else if (iIntValue == 3) {
            this.audioViews.add(viewGroup);
        }
    }

    private void prepareLayouts(int i) {
        MessageObject messageObject;
        int iDp = AndroidUtilities.displaySize.x - AndroidUtilities.dp(24.0f);
        if (i == 0) {
            reuseView(this.centerView);
            reuseView(this.leftView);
            reuseView(this.rightView);
            reuseButtonsView(this.centerButtonsView);
            reuseButtonsView(this.leftButtonsView);
            reuseButtonsView(this.rightButtonsView);
            int i2 = this.currentMessageNum - 1;
            while (true) {
                int i3 = this.currentMessageNum;
                if (i2 >= i3 + 2) {
                    break;
                }
                if (i2 == i3 - 1) {
                    this.leftView = getViewForMessage(i2, true);
                    this.leftButtonsView = getButtonsViewForMessage(i2, true);
                } else if (i2 == i3) {
                    this.centerView = getViewForMessage(i2, true);
                    this.centerButtonsView = getButtonsViewForMessage(i2, true);
                } else if (i2 == i3 + 1) {
                    this.rightView = getViewForMessage(i2, true);
                    this.rightButtonsView = getButtonsViewForMessage(i2, true);
                }
                i2++;
            }
        } else if (i == 1) {
            reuseView(this.rightView);
            reuseButtonsView(this.rightButtonsView);
            this.rightView = this.centerView;
            this.centerView = this.leftView;
            this.leftView = getViewForMessage(this.currentMessageNum - 1, true);
            this.rightButtonsView = this.centerButtonsView;
            this.centerButtonsView = this.leftButtonsView;
            this.leftButtonsView = getButtonsViewForMessage(this.currentMessageNum - 1, true);
        } else if (i == 2) {
            reuseView(this.leftView);
            reuseButtonsView(this.leftButtonsView);
            this.leftView = this.centerView;
            this.centerView = this.rightView;
            this.rightView = getViewForMessage(this.currentMessageNum + 1, true);
            this.leftButtonsView = this.centerButtonsView;
            this.centerButtonsView = this.rightButtonsView;
            this.rightButtonsView = getButtonsViewForMessage(this.currentMessageNum + 1, true);
        } else if (i == 3) {
            ViewGroup viewGroup = this.rightView;
            if (viewGroup != null) {
                float translationX = viewGroup.getTranslationX();
                reuseView(this.rightView);
                ViewGroup viewForMessage = getViewForMessage(this.currentMessageNum + 1, false);
                this.rightView = viewForMessage;
                if (viewForMessage != null) {
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewForMessage.getLayoutParams();
                    layoutParams.width = iDp;
                    this.rightView.setLayoutParams(layoutParams);
                    this.rightView.setTranslationX(translationX);
                    this.rightView.invalidate();
                }
            }
            ViewGroup viewGroup2 = this.rightButtonsView;
            if (viewGroup2 != null) {
                float translationX2 = viewGroup2.getTranslationX();
                reuseButtonsView(this.rightButtonsView);
                LinearLayout buttonsViewForMessage = getButtonsViewForMessage(this.currentMessageNum + 1, false);
                this.rightButtonsView = buttonsViewForMessage;
                if (buttonsViewForMessage != null) {
                    buttonsViewForMessage.setTranslationX(translationX2);
                }
            }
        } else if (i == 4) {
            ViewGroup viewGroup3 = this.leftView;
            if (viewGroup3 != null) {
                float translationX3 = viewGroup3.getTranslationX();
                reuseView(this.leftView);
                ViewGroup viewForMessage2 = getViewForMessage(0, false);
                this.leftView = viewForMessage2;
                if (viewForMessage2 != null) {
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) viewForMessage2.getLayoutParams();
                    layoutParams2.width = iDp;
                    this.leftView.setLayoutParams(layoutParams2);
                    this.leftView.setTranslationX(translationX3);
                    this.leftView.invalidate();
                }
            }
            ViewGroup viewGroup4 = this.leftButtonsView;
            if (viewGroup4 != null) {
                float translationX4 = viewGroup4.getTranslationX();
                reuseButtonsView(this.leftButtonsView);
                LinearLayout buttonsViewForMessage2 = getButtonsViewForMessage(0, false);
                this.leftButtonsView = buttonsViewForMessage2;
                if (buttonsViewForMessage2 != null) {
                    buttonsViewForMessage2.setTranslationX(translationX4);
                }
            }
        }
        for (int i4 = 0; i4 < 3; i4++) {
            int size = (this.currentMessageNum - 1) + i4;
            if (this.popupMessages.size() != 1 || (size >= 0 && size < this.popupMessages.size())) {
                ArrayList<MessageObject> arrayList = this.popupMessages;
                if (size == -1) {
                    size = arrayList.size() - 1;
                } else if (size == arrayList.size()) {
                    size = 0;
                }
                messageObject = this.popupMessages.get(size);
            } else {
                messageObject = null;
            }
            this.setMessageObjects[i4] = messageObject;
        }
    }

    private void fixLayout() {
        FrameLayout frameLayout = this.avatarContainer;
        if (frameLayout != null) {
            frameLayout.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.PopupNotificationActivity.6
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    if (PopupNotificationActivity.this.avatarContainer != null) {
                        PopupNotificationActivity.this.avatarContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    }
                    int currentActionBarHeight = (ActionBar.getCurrentActionBarHeight() - AndroidUtilities.dp(48.0f)) / 2;
                    PopupNotificationActivity.this.avatarContainer.setPadding(PopupNotificationActivity.this.avatarContainer.getPaddingLeft(), currentActionBarHeight, PopupNotificationActivity.this.avatarContainer.getPaddingRight(), currentActionBarHeight);
                    return true;
                }
            });
        }
        ViewGroup viewGroup = this.messageContainer;
        if (viewGroup != null) {
            viewGroup.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() { // from class: org.telegram.ui.PopupNotificationActivity.7
                @Override // android.view.ViewTreeObserver.OnPreDrawListener
                public boolean onPreDraw() {
                    PopupNotificationActivity.this.messageContainer.getViewTreeObserver().removeOnPreDrawListener(this);
                    if (PopupNotificationActivity.this.checkTransitionAnimation() || PopupNotificationActivity.this.startedMoving) {
                        return true;
                    }
                    ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) PopupNotificationActivity.this.messageContainer.getLayoutParams();
                    marginLayoutParams.topMargin = ActionBar.getCurrentActionBarHeight();
                    marginLayoutParams.bottomMargin = AndroidUtilities.dp(48.0f);
                    marginLayoutParams.width = -1;
                    marginLayoutParams.height = -1;
                    PopupNotificationActivity.this.messageContainer.setLayoutParams(marginLayoutParams);
                    PopupNotificationActivity.this.applyViewsLayoutParams(0);
                    return true;
                }
            });
        }
    }

    private void handleIntent(Intent intent) {
        this.isReply = intent != null && intent.getBooleanExtra("force", false);
        this.popupMessages.clear();
        if (this.isReply) {
            int intExtra = intent != null ? intent.getIntExtra("currentAccount", UserConfig.selectedAccount) : UserConfig.selectedAccount;
            if (!UserConfig.isValidAccount(intExtra)) {
                return;
            } else {
                this.popupMessages.addAll(NotificationsController.getInstance(intExtra).popupReplyMessages);
            }
        } else {
            for (int i = 0; i < 16; i++) {
                if (UserConfig.getInstance(i).isClientActivated()) {
                    this.popupMessages.addAll(NotificationsController.getInstance(i).popupMessages);
                }
            }
        }
        if (((KeyguardManager) getSystemService("keyguard")).inKeyguardRestrictedInputMode() || !ApplicationLoader.isScreenOn) {
            getWindow().addFlags(2623490);
        } else {
            getWindow().addFlags(2623488);
            getWindow().clearFlags(2);
        }
        if (this.currentMessageObject == null) {
            this.currentMessageNum = 0;
        }
        getNewMessage();
    }

    public void getNewMessage() {
        if (this.popupMessages.isEmpty()) {
            onFinish();
            finish();
            return;
        }
        if ((this.currentMessageNum != 0 || this.chatActivityEnterView.hasText() || this.startedMoving) && this.currentMessageObject != null) {
            int size = this.popupMessages.size();
            for (int i = 0; i < size; i++) {
                MessageObject messageObject = this.popupMessages.get(i);
                if (messageObject.currentAccount == this.currentMessageObject.currentAccount && messageObject.getDialogId() == this.currentMessageObject.getDialogId() && messageObject.getId() == this.currentMessageObject.getId()) {
                    this.currentMessageNum = i;
                    if (this.startedMoving) {
                        if (i == this.popupMessages.size() - 1) {
                            prepareLayouts(3);
                        } else if (this.currentMessageNum == 1) {
                            prepareLayouts(4);
                        }
                    }
                }
            }
            this.currentMessageNum = 0;
            this.currentMessageObject = this.popupMessages.get(0);
            updateInterfaceForCurrentMessage(0);
        } else {
            this.currentMessageNum = 0;
            this.currentMessageObject = this.popupMessages.get(0);
            updateInterfaceForCurrentMessage(0);
        }
        this.countText.setText(String.format("%d/%d", Integer.valueOf(this.currentMessageNum + 1), Integer.valueOf(this.popupMessages.size())));
    }

    public void openCurrentMessage() {
        if (this.currentMessageObject == null) {
            return;
        }
        Intent intent = new Intent(ApplicationLoader.applicationContext, (Class<?>) LaunchActivity.class);
        long dialogId = this.currentMessageObject.getDialogId();
        if (DialogObject.isEncryptedDialog(dialogId)) {
            intent.putExtra("encId", DialogObject.getEncryptedChatId(dialogId));
        } else if (DialogObject.isUserDialog(dialogId)) {
            intent.putExtra("userId", dialogId);
        } else if (DialogObject.isChatDialog(dialogId)) {
            intent.putExtra("chatId", -dialogId);
        }
        intent.putExtra("currentAccount", this.currentMessageObject.currentAccount);
        intent.setAction("com.tmessages.openchat" + Math.random() + Integer.MAX_VALUE);
        intent.setFlags(32768);
        startActivity(intent);
        onFinish();
        finish();
    }

    private void updateInterfaceForCurrentMessage(int i) {
        if (this.actionBar == null) {
            return;
        }
        int i2 = this.lastResumedAccount;
        if (i2 != this.currentMessageObject.currentAccount) {
            if (i2 >= 0) {
                ConnectionsManager.getInstance(i2).setAppPaused(true, false);
            }
            int i3 = this.currentMessageObject.currentAccount;
            this.lastResumedAccount = i3;
            ConnectionsManager.getInstance(i3).setAppPaused(false, false);
        }
        this.currentChat = null;
        this.currentUser = null;
        long dialogId = this.currentMessageObject.getDialogId();
        this.chatActivityEnterView.setDialogId(dialogId, this.currentMessageObject.currentAccount);
        if (DialogObject.isEncryptedDialog(dialogId)) {
            this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Long.valueOf(MessagesController.getInstance(this.currentMessageObject.currentAccount).getEncryptedChat(Integer.valueOf(DialogObject.getEncryptedChatId(dialogId))).user_id));
        } else if (DialogObject.isUserDialog(dialogId)) {
            this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Long.valueOf(dialogId));
        } else if (DialogObject.isChatDialog(dialogId)) {
            this.currentChat = MessagesController.getInstance(this.currentMessageObject.currentAccount).getChat(Long.valueOf(-dialogId));
            if (this.currentMessageObject.isFromUser()) {
                this.currentUser = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Long.valueOf(this.currentMessageObject.messageOwner.from_id.user_id));
            }
        }
        TLRPC.Chat chat = this.currentChat;
        if (chat != null) {
            this.nameTextView.setText(chat.title);
            TLRPC.User user = this.currentUser;
            TextView textView = this.onlineTextView;
            if (user != null) {
                textView.setText(UserObject.getUserName(user));
            } else {
                textView.setText((CharSequence) null);
            }
            this.nameTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            this.nameTextView.setCompoundDrawablePadding(0);
        } else {
            TLRPC.User user2 = this.currentUser;
            if (user2 != null) {
                this.nameTextView.setText(UserObject.getUserName(user2));
                boolean zIsEncryptedDialog = DialogObject.isEncryptedDialog(dialogId);
                TextView textView2 = this.nameTextView;
                if (zIsEncryptedDialog) {
                    textView2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock_white, 0, 0, 0);
                    this.nameTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                } else {
                    textView2.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    this.nameTextView.setCompoundDrawablePadding(0);
                }
            }
        }
        prepareLayouts(i);
        updateSubtitle();
        checkAndUpdateAvatar();
        applyViewsLayoutParams(0);
    }

    private void updateSubtitle() {
        MessageObject messageObject;
        TLRPC.User user;
        String str;
        if (this.actionBar == null || (messageObject = this.currentMessageObject) == null || this.currentChat != null || (user = this.currentUser) == null) {
            return;
        }
        long j = user.id;
        if (j / 1000 != 777 && j / 1000 != 333 && ContactsController.getInstance(messageObject.currentAccount).contactsDict.get(Long.valueOf(this.currentUser.id)) == null && ((ContactsController.getInstance(this.currentMessageObject.currentAccount).contactsDict.size() != 0 || !ContactsController.getInstance(this.currentMessageObject.currentAccount).isLoadingContacts()) && (str = this.currentUser.phone) != null && str.length() != 0)) {
            this.nameTextView.setText(PhoneFormat.getInstance().format("+" + this.currentUser.phone));
        } else {
            this.nameTextView.setText(UserObject.getUserName(this.currentUser));
        }
        TLRPC.User user2 = this.currentUser;
        if (user2 != null && user2.id == UserObject.VERIFY) {
            this.onlineTextView.setText(LocaleController.getString(R.string.VerifyCodesNotifications));
            return;
        }
        if (user2 != null && user2.id == 777000) {
            this.onlineTextView.setText(LocaleController.getString(R.string.ServiceNotifications));
            return;
        }
        CharSequence printingString = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingString(this.currentMessageObject.getDialogId(), 0L, false);
        if (printingString == null || printingString.length() == 0) {
            this.lastPrintString = null;
            setTypingAnimation(false);
            TLRPC.User user3 = MessagesController.getInstance(this.currentMessageObject.currentAccount).getUser(Long.valueOf(this.currentUser.id));
            if (user3 != null) {
                this.currentUser = user3;
            }
            this.onlineTextView.setText(LocaleController.formatUserStatus(this.currentMessageObject.currentAccount, this.currentUser));
            return;
        }
        this.lastPrintString = printingString;
        this.onlineTextView.setText(printingString);
        setTypingAnimation(true);
    }

    private void checkAndUpdateAvatar() {
        TLRPC.User user;
        MessageObject messageObject = this.currentMessageObject;
        if (messageObject == null) {
            return;
        }
        if (this.currentChat != null) {
            TLRPC.Chat chat = MessagesController.getInstance(messageObject.currentAccount).getChat(Long.valueOf(this.currentChat.id));
            if (chat == null) {
                return;
            }
            this.currentChat = chat;
            if (this.avatarImageView != null) {
                this.avatarImageView.setForUserOrChat(chat, new AvatarDrawable(this.currentChat));
                return;
            }
            return;
        }
        if (this.currentUser == null || (user = MessagesController.getInstance(messageObject.currentAccount).getUser(Long.valueOf(this.currentUser.id))) == null) {
            return;
        }
        this.currentUser = user;
        if (this.avatarImageView != null) {
            this.avatarImageView.setForUserOrChat(user, new AvatarDrawable(this.currentUser));
        }
    }

    private void setTypingAnimation(boolean z) {
        if (this.actionBar == null) {
            return;
        }
        int i = 0;
        if (z) {
            try {
                Integer printingStringType = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingStringType(this.currentMessageObject.getDialogId(), 0L);
                this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds(this.statusDrawables[printingStringType.intValue()], (Drawable) null, (Drawable) null, (Drawable) null);
                this.onlineTextView.setCompoundDrawablePadding(AndroidUtilities.dp(4.0f));
                while (i < this.statusDrawables.length) {
                    int iIntValue = printingStringType.intValue();
                    StatusDrawable[] statusDrawableArr = this.statusDrawables;
                    if (i == iIntValue) {
                        statusDrawableArr[i].start();
                    } else {
                        statusDrawableArr[i].stop();
                    }
                    i++;
                }
                return;
            } catch (Exception e) {
                FileLog.e(e);
                return;
            }
        }
        this.onlineTextView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
        this.onlineTextView.setCompoundDrawablePadding(0);
        while (true) {
            StatusDrawable[] statusDrawableArr2 = this.statusDrawables;
            if (i >= statusDrawableArr2.length) {
                return;
            }
            statusDrawableArr2[i].stop();
            i++;
        }
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (this.chatActivityEnterView.isPopupShowing()) {
            this.chatActivityEnterView.hidePopup(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override // android.app.Activity
    public void onResume() {
        super.onResume();
        MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, true);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.setFieldFocused(true);
        }
        fixLayout();
        checkAndUpdateAvatar();
        this.wakeLock.acquire(7000L);
    }

    @Override // android.app.Activity
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.hidePopup(false);
            this.chatActivityEnterView.setFieldFocused(false);
        }
        int i = this.lastResumedAccount;
        if (i >= 0) {
            ConnectionsManager.getInstance(i).setAppPaused(true, false);
        }
    }

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        TextView textView;
        PopupAudioView popupAudioView;
        MessageObject messageObject;
        PopupAudioView popupAudioView2;
        MessageObject messageObject2;
        MessageObject messageObject3;
        if (i == NotificationCenter.appDidLogout) {
            if (i2 == this.lastResumedAccount) {
                onFinish();
                finish();
                return;
            }
            return;
        }
        int i3 = 0;
        if (i == NotificationCenter.pushMessagesUpdated) {
            if (this.isReply) {
                return;
            }
            this.popupMessages.clear();
            for (int i4 = 0; i4 < 16; i4++) {
                if (UserConfig.getInstance(i4).isClientActivated()) {
                    this.popupMessages.addAll(NotificationsController.getInstance(i4).popupMessages);
                }
            }
            getNewMessage();
            if (this.popupMessages.isEmpty()) {
                return;
            }
            for (int i5 = 0; i5 < 3; i5++) {
                int size = (this.currentMessageNum - 1) + i5;
                if (this.popupMessages.size() != 1 || (size >= 0 && size < this.popupMessages.size())) {
                    ArrayList<MessageObject> arrayList = this.popupMessages;
                    if (size == -1) {
                        size = arrayList.size() - 1;
                    } else if (size == arrayList.size()) {
                        size = 0;
                    }
                    messageObject3 = this.popupMessages.get(size);
                } else {
                    messageObject3 = null;
                }
                if (this.setMessageObjects[i5] != messageObject3) {
                    updateInterfaceForCurrentMessage(0);
                }
            }
            return;
        }
        if (i == NotificationCenter.updateInterfaces) {
            if (this.currentMessageObject == null || i2 != this.lastResumedAccount) {
                return;
            }
            int iIntValue = ((Integer) objArr[0]).intValue();
            if ((MessagesController.UPDATE_MASK_NAME & iIntValue) != 0 || (MessagesController.UPDATE_MASK_STATUS & iIntValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_NAME & iIntValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_MEMBERS & iIntValue) != 0) {
                updateSubtitle();
            }
            if ((MessagesController.UPDATE_MASK_AVATAR & iIntValue) != 0 || (MessagesController.UPDATE_MASK_CHAT_AVATAR & iIntValue) != 0) {
                checkAndUpdateAvatar();
            }
            if ((iIntValue & MessagesController.UPDATE_MASK_USER_PRINT) != 0) {
                CharSequence printingString = MessagesController.getInstance(this.currentMessageObject.currentAccount).getPrintingString(this.currentMessageObject.getDialogId(), 0L, false);
                CharSequence charSequence = this.lastPrintString;
                if ((charSequence == null || printingString != null) && ((charSequence != null || printingString == null) && (charSequence == null || charSequence.equals(printingString)))) {
                    return;
                }
                updateSubtitle();
                return;
            }
            return;
        }
        if (i == NotificationCenter.messagePlayingDidReset) {
            Integer num = (Integer) objArr[0];
            ViewGroup viewGroup = this.messageContainer;
            if (viewGroup != null) {
                int childCount = viewGroup.getChildCount();
                while (i3 < childCount) {
                    View childAt = this.messageContainer.getChildAt(i3);
                    if (((Integer) childAt.getTag()).intValue() == 3 && (messageObject2 = (popupAudioView2 = (PopupAudioView) childAt.findViewWithTag(300)).getMessageObject()) != null && messageObject2.currentAccount == i2 && messageObject2.getId() == num.intValue()) {
                        popupAudioView2.updateButtonState();
                        return;
                    }
                    i3++;
                }
                return;
            }
            return;
        }
        if (i == NotificationCenter.messagePlayingProgressDidChanged) {
            Integer num2 = (Integer) objArr[0];
            ViewGroup viewGroup2 = this.messageContainer;
            if (viewGroup2 != null) {
                int childCount2 = viewGroup2.getChildCount();
                while (i3 < childCount2) {
                    View childAt2 = this.messageContainer.getChildAt(i3);
                    if (((Integer) childAt2.getTag()).intValue() == 3 && (messageObject = (popupAudioView = (PopupAudioView) childAt2.findViewWithTag(300)).getMessageObject()) != null && messageObject.currentAccount == i2 && messageObject.getId() == num2.intValue()) {
                        popupAudioView.updateProgress();
                        return;
                    }
                    i3++;
                }
                return;
            }
            return;
        }
        if (i == NotificationCenter.emojiLoaded) {
            ViewGroup viewGroup3 = this.messageContainer;
            if (viewGroup3 != null) {
                int childCount3 = viewGroup3.getChildCount();
                while (i3 < childCount3) {
                    View childAt3 = this.messageContainer.getChildAt(i3);
                    if (((Integer) childAt3.getTag()).intValue() == 1 && (textView = (TextView) childAt3.findViewWithTag(Integer.valueOf(MediaError.DetailedErrorCode.SEGMENT_NETWORK))) != null) {
                        textView.invalidate();
                    }
                    i3++;
                }
                return;
            }
            return;
        }
        if (i == NotificationCenter.contactsDidLoad && i2 == this.lastResumedAccount) {
            updateSubtitle();
        }
    }

    @Override // android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        onFinish();
        MediaController.getInstance().setFeedbackView(this.chatActivityEnterView, false);
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
        BackupImageView backupImageView = this.avatarImageView;
        if (backupImageView != null) {
            backupImageView.setImageDrawable(null);
        }
    }

    public void onFinish() {
        if (this.finished) {
            return;
        }
        this.finished = true;
        if (this.isReply) {
            this.popupMessages.clear();
        }
        for (int i = 0; i < 16; i++) {
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.appDidLogout);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.updateInterfaces);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingProgressDidChanged);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.messagePlayingDidReset);
            NotificationCenter.getInstance(i).removeObserver(this, NotificationCenter.contactsDidLoad);
        }
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.pushMessagesUpdated);
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        ChatActivityEnterView chatActivityEnterView = this.chatActivityEnterView;
        if (chatActivityEnterView != null) {
            chatActivityEnterView.onDestroy();
        }
        if (this.wakeLock.isHeld()) {
            this.wakeLock.release();
        }
    }
}
