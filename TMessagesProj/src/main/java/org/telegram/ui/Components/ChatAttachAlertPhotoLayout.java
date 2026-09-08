package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Keep;
import androidx.core.content.ContextCompat;
import androidx.core.util.Consumer;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.utils.system.SystemUtils;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import kotlin.time.DurationKt;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraSessionWrapper;
import org.telegram.messenger.camera.CameraView;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.PhotoAttachCameraCell;
import org.telegram.ui.Cells.PhotoAttachPermissionCell;
import org.telegram.ui.Cells.PhotoAttachPhotoCell;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.blur3.capture.IBlur3Capture;
import org.telegram.ui.Components.blur3.capture.IBlur3Hash;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PhotoViewer;
import org.telegram.ui.Stars.StarsIntroActivity;
import org.telegram.ui.Stories.recorder.AlbumButton;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

@SuppressLint({"ViewConstructor"})
public class ChatAttachAlertPhotoLayout extends ChatAttachAlert.AttachAlertLayout implements NotificationCenter.NotificationCenterDelegate {
    private static boolean mediaFromExternalCamera;
    private PhotoAttachAdapter adapter;
    float additionCloseCameraY;
    private Runnable afterCameraInitRunnable;
    private int alertOnlyOnce;
    private int[] animateCameraValues;
    float animationClipBottom;
    float animationClipLeft;
    float animationClipRight;
    float animationClipTop;
    private boolean cameraAnimationInProgress;
    private PhotoAttachAdapter cameraAttachAdapter;
    boolean cameraExpanded;
    private AnimatorSet cameraInitAnimation;
    private float cameraOpenProgress;
    public boolean cameraOpened;
    private FrameLayout cameraPanel;
    private LinearLayoutManager cameraPhotoLayoutManager;
    private RecyclerListView cameraPhotoRecyclerView;
    private boolean cameraPhotoRecyclerViewIgnoreLayout;
    protected CameraViewInternal cameraView;
    private final CameraViewItemDecoration cameraViewItemDecoration;
    private float[] cameraViewLocation;
    private float cameraViewOffsetBottomY;
    private float cameraViewOffsetX;
    private float cameraViewOffsetY;
    private float cameraZoom;
    private boolean canSaveCameraPreview;
    private boolean cancelTakingPhotos;
    public MessagePreviewView.ToggleButton captionItem;
    private boolean checkCameraWhenShown;
    private ActionBarMenuSubItem compressItem;
    private TextView counterTextView;
    public int currentItemTop;
    private float currentPanTranslationY;
    private int currentSelectedCount;
    boolean deviceHasGoodCamera;
    private boolean documentsEnabled;
    private boolean dragging;
    public TextView dropDown;
    private ArrayList<MediaController.AlbumEntry> dropDownAlbums;
    private ActionBarMenuItem dropDownContainer;
    private Drawable dropDownDrawable;
    private boolean flashAnimationInProgress;
    private ImageView[] flashModeButton;
    boolean forceDarkTheme;
    private MediaController.AlbumEntry galleryAlbumEntry;
    private int gridExtraSpace;
    public RecyclerListView gridView;
    private ViewPropertyAnimator headerAnimator;
    private Rect hitRect;
    private boolean ignoreLayout;
    private DecelerateInterpolator interpolator;
    private Boolean isCameraFrontfaceBeforeEnteringEditMode;
    private boolean isHidden;
    private RecyclerViewItemRangeSelector itemRangeSelector;
    private int itemSize;
    private int itemsPerRow;
    private int lastItemSize;
    private int lastNotifyWidth;
    private float lastY;
    private GridLayoutManager layoutManager;
    public int listAdditionalH;
    private boolean loading;
    private boolean maybeStartDraging;
    private boolean mediaEnabled;
    private final boolean needCamera;
    private boolean noCameraPermissions;
    private boolean noGalleryPermissions;
    private AnimationNotificationsLocker notificationsLocker;
    private boolean photoEnabled;
    public PhotoViewer.PhotoViewerProvider photoViewerProvider;
    private float pinchStartDistance;
    private boolean pressed;
    protected ActionBarMenuSubItem previewItem;
    private EmptyTextProgressView progressView;
    private ActionBarMenuSubItem qualityItem;
    private TextView recordTime;
    private boolean requestingPermissions;
    private MediaController.AlbumEntry selectedAlbumEntry;
    private boolean showAvatarConstructor;
    private ShutterButton shutterButton;
    private ActionBarMenuSubItem spoilerItem;
    private ActionBarMenuSubItem starsItem;
    private ImageView switchCameraButton;
    private boolean takingPhoto;
    private TextView tooltipTextView;
    private boolean videoEnabled;
    private Runnable videoRecordRunnable;
    private int videoRecordTime;
    private int[] viewPosition;
    private AnimatorSet zoomControlAnimation;
    private Runnable zoomControlHideRunnable;
    private ZoomControlView zoomControlView;
    private boolean zoomWas;
    private boolean zooming;
    private static ArrayList<Object> cameraPhotos = new ArrayList<>();
    public static HashMap<Object, Object> selectedPhotos = new HashMap<>();
    public static ArrayList<Object> selectedPhotosOrder = new ArrayList<>();
    public static int lastImageId = -1;

    public void onPhotoEditModeChanged(boolean z) {
    }

    @Override // org.telegram.ui.Components.ChatAttachAlert.AttachAlertLayout
    public int needsActionBar() {
        return 1;
    }

    public void updateAvatarPicker() {
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        this.showAvatarConstructor = (chatAttachAlert.avatarPicker == 0 || chatAttachAlert.isPhotoPicker) ? false : true;
    }

    public class BasePhotoProvider extends PhotoViewer.EmptyPhotoViewerProvider {
        private BasePhotoProvider() {
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void spoilerPressed() {
            ChatAttachAlertPhotoLayout.this.onMenuItemClick(10);
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean isPhotoChecked(int i) {
            MediaController.PhotoEntry photoEntryAtPosition = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i);
            return photoEntryAtPosition != null && ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntryAtPosition.imageId));
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int setPhotoChecked(int i, VideoEditedInfo videoEditedInfo) {
            MediaController.PhotoEntry photoEntryAtPosition;
            boolean z;
            boolean zIsPhotoChecked = isPhotoChecked(i);
            if ((ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos >= 0 && ChatAttachAlertPhotoLayout.selectedPhotos.size() >= ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos && !zIsPhotoChecked) || (photoEntryAtPosition = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i)) == null || ChatAttachAlertPhotoLayout.this.checkSendMediaEnabled(photoEntryAtPosition)) {
                return -1;
            }
            if (!zIsPhotoChecked && ChatAttachAlertPhotoLayout.selectedPhotos.size() + 1 > ChatAttachAlertPhotoLayout.this.maxCount()) {
                return -1;
            }
            int iAddToSelectedPhotos = ChatAttachAlertPhotoLayout.this.addToSelectedPhotos(photoEntryAtPosition, -1);
            if (iAddToSelectedPhotos == -1) {
                iAddToSelectedPhotos = ChatAttachAlertPhotoLayout.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition.imageId));
                z = true;
            } else {
                photoEntryAtPosition.editedInfo = null;
                z = false;
            }
            photoEntryAtPosition.editedInfo = videoEditedInfo;
            int childCount = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
            for (int i2 = 0; i2 < childCount; i2++) {
                View childAt = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(i2);
                if ((childAt instanceof PhotoAttachPhotoCell) && ((Integer) childAt.getTag()).intValue() == i) {
                    ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
                    if ((chatAttachAlert.baseFragment instanceof ChatActivity) && chatAttachAlert.allowOrder) {
                        ((PhotoAttachPhotoCell) childAt).setChecked(iAddToSelectedPhotos, z, false);
                        break;
                    }
                    ((PhotoAttachPhotoCell) childAt).setChecked(-1, z, false);
                    break;
                }
            }
            int childCount2 = ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.getChildCount();
            for (int i3 = 0; i3 < childCount2; i3++) {
                View childAt2 = ChatAttachAlertPhotoLayout.this.cameraPhotoRecyclerView.getChildAt(i3);
                if ((childAt2 instanceof PhotoAttachPhotoCell) && ((Integer) childAt2.getTag()).intValue() == i) {
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlertPhotoLayout.this.parentAlert;
                    if ((chatAttachAlert2.baseFragment instanceof ChatActivity) && chatAttachAlert2.allowOrder) {
                        ((PhotoAttachPhotoCell) childAt2).setChecked(iAddToSelectedPhotos, z, false);
                        break;
                    }
                    ((PhotoAttachPhotoCell) childAt2).setChecked(-1, z, false);
                    break;
                }
            }
            ChatAttachAlertPhotoLayout.this.parentAlert.updateCountButton(z ? 1 : 2);
            return iAddToSelectedPhotos;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getSelectedCount() {
            return ChatAttachAlertPhotoLayout.selectedPhotos.size();
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public ArrayList<Object> getSelectedPhotosOrder() {
            return ChatAttachAlertPhotoLayout.selectedPhotosOrder;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public HashMap<Object, Object> getSelectedPhotos() {
            return ChatAttachAlertPhotoLayout.selectedPhotos;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public int getPhotoIndex(int i) {
            MediaController.PhotoEntry photoEntryAtPosition = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i);
            if (photoEntryAtPosition == null) {
                return -1;
            }
            return ChatAttachAlertPhotoLayout.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition.imageId));
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean allowLivePhotos() {
            ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
            return chatAttachAlert != null && chatAttachAlert.allowLivePhotos;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void updatedLivePhotos() {
            ChatAttachAlertPhotoLayout.this.updateCells();
        }
    }

    public void setCurrentSpoilerVisible(int i, final boolean z) {
        PhotoViewer photoViewer = PhotoViewer.getInstance();
        if (i == -1) {
            i = photoViewer.getCurrentIndex();
        }
        List<Object> imagesArrLocals = photoViewer.getImagesArrLocals();
        if (imagesArrLocals == null || imagesArrLocals.isEmpty() || i >= imagesArrLocals.size() || !(imagesArrLocals.get(i) instanceof MediaController.PhotoEntry) || !((MediaController.PhotoEntry) imagesArrLocals.get(i)).hasSpoiler) {
            return;
        }
        final MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) imagesArrLocals.get(i);
        this.gridView.forAllChild(new Consumer() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda29
            @Override // androidx.core.util.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$setCurrentSpoilerVisible$0(photoEntry, z, (View) obj);
            }
        });
    }

    public void lambda$sendButtonPressed$1(boolean z, int i, boolean z2, Long l) {
            ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
            if (chatAttachAlert != null) {
                chatAttachAlert.setButtonPressed(true);
            }
            ChatAttachAlert chatAttachAlert2 = ChatAttachAlertPhotoLayout.this.parentAlert;
            chatAttachAlert2.delegate.didPressedButton(7, true, z, i, 0, 0L, chatAttachAlert2.isCaptionAbove(), z2, l.longValue());
            ChatAttachAlertPhotoLayout.selectedPhotos.clear();
            ChatAttachAlertPhotoLayout.cameraPhotos.clear();
            ChatAttachAlertPhotoLayout.selectedPhotosOrder.clear();
            ChatAttachAlertPhotoLayout.selectedPhotos.clear();
            if (PhotoViewer.getInstance() != null) {
                PhotoViewer.getInstance().closePhoto(PhotoViewer.getInstance().closePhotoAfterSelectWithAnimation, false);
                PhotoViewer.getInstance().doneButtonPressed = true;
            }
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean allowCaption() {
            ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
            return (chatAttachAlert.isPhotoPicker || chatAttachAlert.isPollAttach) ? false : true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public long getDialogId() {
            BaseFragment baseFragment = ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment;
            if (baseFragment instanceof ChatActivity) {
                return ((ChatActivity) baseFragment).getDialogId();
            }
            return super.getDialogId();
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canMoveCaptionAbove() {
            ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
            return chatAttachAlert != null && (chatAttachAlert.baseFragment instanceof ChatActivity);
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean isCaptionAbove() {
            ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
            return chatAttachAlert != null && chatAttachAlert.captionAbove;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void moveCaptionAbove(boolean z) {
            ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
            if (chatAttachAlert == null || chatAttachAlert.captionAbove == z) {
                return;
            }
            chatAttachAlert.setCaptionAbove(z);
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
            chatAttachAlertPhotoLayout.captionItem.setState(!chatAttachAlertPhotoLayout.parentAlert.captionAbove, true);
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean isEditingMessage() {
            ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
            return (chatAttachAlert == null || chatAttachAlert.editingMessageObject == null) ? false : true;
        }

        @Override // org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean isEditingMessageResend() {
            MessageObject messageObject;
            ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
            return (chatAttachAlert == null || (messageObject = chatAttachAlert.editingMessageObject) == null || !messageObject.needResendWhenEdit()) ? false : true;
        }
    }

    public void updateCheckedPhotoIndices() {
        if (this.parentAlert.baseFragment instanceof ChatActivity) {
            int childCount = this.gridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    MediaController.PhotoEntry photoEntryAtPosition = getPhotoEntryAtPosition(((Integer) photoAttachPhotoCell.getTag()).intValue());
                    if (photoEntryAtPosition != null) {
                        photoAttachPhotoCell.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition.imageId)));
                    }
                }
            }
            int childCount2 = this.cameraPhotoRecyclerView.getChildCount();
            for (int i2 = 0; i2 < childCount2; i2++) {
                View childAt2 = this.cameraPhotoRecyclerView.getChildAt(i2);
                if (childAt2 instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell2 = (PhotoAttachPhotoCell) childAt2;
                    MediaController.PhotoEntry photoEntryAtPosition2 = getPhotoEntryAtPosition(((Integer) photoAttachPhotoCell2.getTag()).intValue());
                    if (photoEntryAtPosition2 != null) {
                        photoAttachPhotoCell2.setNum(selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition2.imageId)));
                    }
                }
            }
        }
    }

    public void updateCheckedPhotos() {
        if (this.parentAlert.baseFragment instanceof ChatActivity) {
            int childCount = this.gridView.getChildCount();
            int i = 0;
            while (true) {
                if (i >= childCount) {
                    break;
                }
                View childAt = this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    int childAdapterPosition = this.gridView.getChildAdapterPosition(childAt);
                    if (this.adapter.hasCameraSpaceRow && childAdapterPosition > this.itemsPerRow) {
                        childAdapterPosition--;
                    }
                    if (this.adapter.needCamera && this.selectedAlbumEntry == this.galleryAlbumEntry && (!ExteraConfig.getHideCameraTile() || this.noCameraPermissions)) {
                        childAdapterPosition--;
                    }
                    MediaController.PhotoEntry photoEntryAtPosition = getPhotoEntryAtPosition(childAdapterPosition);
                    photoAttachPhotoCell.setHasSpoiler(photoEntryAtPosition != null && photoEntryAtPosition.hasSpoiler);
                    photoAttachPhotoCell.setHighQuality(photoEntryAtPosition != null && photoEntryAtPosition.isHighQuality());
                    ChatAttachAlert chatAttachAlert = this.parentAlert;
                    if ((chatAttachAlert.baseFragment instanceof ChatActivity) && chatAttachAlert.allowOrder) {
                        photoAttachPhotoCell.setChecked(photoEntryAtPosition != null ? selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition.imageId)) : -1, photoEntryAtPosition != null && selectedPhotos.containsKey(Integer.valueOf(photoEntryAtPosition.imageId)), true);
                    } else {
                        photoAttachPhotoCell.setChecked(-1, photoEntryAtPosition != null && selectedPhotos.containsKey(Integer.valueOf(photoEntryAtPosition.imageId)), true);
                    }
                }
                i++;
            }
            int childCount2 = this.cameraPhotoRecyclerView.getChildCount();
            for (int i2 = 0; i2 < childCount2; i2++) {
                View childAt2 = this.cameraPhotoRecyclerView.getChildAt(i2);
                if (childAt2 instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell2 = (PhotoAttachPhotoCell) childAt2;
                    int childAdapterPosition2 = this.cameraPhotoRecyclerView.getChildAdapterPosition(childAt2);
                    if (this.adapter.hasCameraSpaceRow && childAdapterPosition2 > this.itemsPerRow) {
                        childAdapterPosition2--;
                    }
                    if (this.adapter.needCamera && this.selectedAlbumEntry == this.galleryAlbumEntry && (!ExteraConfig.getHideCameraTile() || this.noCameraPermissions)) {
                        childAdapterPosition2--;
                    }
                    MediaController.PhotoEntry photoEntryAtPosition2 = getPhotoEntryAtPosition(childAdapterPosition2);
                    photoAttachPhotoCell2.setHasSpoiler(photoEntryAtPosition2 != null && photoEntryAtPosition2.hasSpoiler);
                    photoAttachPhotoCell2.setHighQuality(photoEntryAtPosition2 != null && photoEntryAtPosition2.isHighQuality());
                    ChatAttachAlert chatAttachAlert2 = this.parentAlert;
                    if ((chatAttachAlert2.baseFragment instanceof ChatActivity) && chatAttachAlert2.allowOrder) {
                        photoAttachPhotoCell2.setChecked(photoEntryAtPosition2 != null ? selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition2.imageId)) : -1, photoEntryAtPosition2 != null && selectedPhotos.containsKey(Integer.valueOf(photoEntryAtPosition2.imageId)), true);
                    } else {
                        photoAttachPhotoCell2.setChecked(-1, photoEntryAtPosition2 != null && selectedPhotos.containsKey(Integer.valueOf(photoEntryAtPosition2.imageId)), true);
                    }
                }
            }
        }
    }

    public MediaController.PhotoEntry getPhotoEntryAtPosition(int i) {
        if (i < 0) {
            return null;
        }
        int size = cameraPhotos.size();
        if (i < size) {
            return (MediaController.PhotoEntry) cameraPhotos.get(i);
        }
        int i2 = i - size;
        MediaController.AlbumEntry albumEntry = this.selectedAlbumEntry;
        if (albumEntry == null || i2 >= albumEntry.photos.size()) {
            return null;
        }
        return this.selectedAlbumEntry.photos.get(i2);
    }

    public ArrayList<Object> getAllPhotosArray() {
        if (this.selectedAlbumEntry != null) {
            if (!cameraPhotos.isEmpty()) {
                ArrayList<Object> arrayList = new ArrayList<>(this.selectedAlbumEntry.photos.size() + cameraPhotos.size());
                arrayList.addAll(cameraPhotos);
                arrayList.addAll(this.selectedAlbumEntry.photos);
                return arrayList;
            }
            return this.selectedAlbumEntry.photos;
        }
        if (!cameraPhotos.isEmpty()) {
            return cameraPhotos;
        }
        return new ArrayList<>(0);
    }

    void lambda$shutterLongPressed$0() {
            if (ChatAttachAlertPhotoLayout.this.videoRecordRunnable == null) {
                return;
            }
            ChatAttachAlertPhotoLayout.this.videoRecordTime++;
            ChatAttachAlertPhotoLayout.this.recordTime.setText(AndroidUtilities.formatLongDuration(ChatAttachAlertPhotoLayout.this.videoRecordTime));
            AndroidUtilities.runOnUIThread(ChatAttachAlertPhotoLayout.this.videoRecordRunnable, 1000L);
        }

        public void lambda$shutterReleased$3(File file, boolean z, Integer num) {
            int i;
            int i2;
            ChatAttachAlertPhotoLayout.this.takingPhoto = false;
            if (file == null || ChatAttachAlertPhotoLayout.this.parentAlert.destroyed) {
                return;
            }
            ChatAttachAlertPhotoLayout.mediaFromExternalCamera = false;
            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(new File(file.getAbsolutePath()).getAbsolutePath(), options);
                i = options.outWidth;
                try {
                    i2 = options.outHeight;
                } catch (Exception unused) {
                    i2 = 0;
                }
            } catch (Exception unused2) {
                i = 0;
            }
            int i3 = i;
            int i4 = ChatAttachAlertPhotoLayout.lastImageId;
            ChatAttachAlertPhotoLayout.lastImageId = i4 - 1;
            MediaController.PhotoEntry photoEntry = new MediaController.PhotoEntry(0, i4, 0L, file.getAbsolutePath(), num.intValue() == -1 ? 0 : num.intValue(), false, i3, i2, 0L);
            photoEntry.canDeleteAfter = true;
            ChatAttachAlertPhotoLayout.this.openPhotoViewer(photoEntry, z, false);
        }

        @Override // org.telegram.ui.Components.ShutterButton.ShutterButtonDelegate
        public boolean onTranslationChanged(float f, float f2) {
            boolean z = this.val$container.getWidth() < this.val$container.getHeight();
            float f3 = z ? f : f2;
            float f4 = z ? f2 : f;
            if (!this.zoomingWas && Math.abs(f3) > Math.abs(f4)) {
                return ChatAttachAlertPhotoLayout.this.zoomControlView.getTag() == null;
            }
            if (f4 < 0.0f) {
                ChatAttachAlertPhotoLayout.this.showZoomControls(true, true);
                ChatAttachAlertPhotoLayout.this.zoomControlView.setZoom((-f4) / AndroidUtilities.dp(200.0f), true);
                this.zoomingWas = true;
                return false;
            }
            if (this.zoomingWas) {
                ChatAttachAlertPhotoLayout.this.zoomControlView.setZoom(0.0f, true);
            }
            if (f == 0.0f && f2 == 0.0f) {
                this.zoomingWas = false;
            }
            return (this.zoomingWas || (f == 0.0f && f2 == 0.0f)) ? false : true;
        }
    }

    public void $r8$lambda$j7zm8n531tbC7FnGcqehqgZvv6o(View view) {
        if (view instanceof PhotoAttachPhotoCell) {
            PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) view;
            photoAttachPhotoCell.setHasSpoiler(photoAttachPhotoCell.getPhotoEntry() != null && photoAttachPhotoCell.getPhotoEntry().hasSpoiler, Float.valueOf(250.0f));
            photoAttachPhotoCell.setHighQuality(photoAttachPhotoCell.getPhotoEntry() != null && photoAttachPhotoCell.getPhotoEntry().isHighQuality());
            photoAttachPhotoCell.setStarsPrice(photoAttachPhotoCell.getPhotoEntry() != null ? photoAttachPhotoCell.getPhotoEntry().starsAmount : 0L, selectedPhotos.size() > 1);
        }
    }

    public void clearSelectedPhotos() {
        this.spoilerItem.setText(LocaleController.getString(R.string.EnablePhotoSpoiler));
        this.spoilerItem.setAnimatedIcon(R.raw.photo_spoiler);
        this.parentAlert.selectedMenuItem.showSubItem(1);
        if (!selectedPhotos.isEmpty()) {
            Iterator<Map.Entry<Object, Object>> it = selectedPhotos.entrySet().iterator();
            while (it.hasNext()) {
                ((MediaController.PhotoEntry) it.next().getValue()).reset();
            }
            selectedPhotos.clear();
            selectedPhotosOrder.clear();
        }
        if (!cameraPhotos.isEmpty()) {
            int size = cameraPhotos.size();
            for (int i = 0; i < size; i++) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) cameraPhotos.get(i);
                new File(photoEntry.path).delete();
                if (photoEntry.imagePath != null) {
                    new File(photoEntry.imagePath).delete();
                }
                if (photoEntry.thumbPath != null) {
                    new File(photoEntry.thumbPath).delete();
                }
            }
            cameraPhotos.clear();
        }
        this.adapter.notifyDataSetChanged();
        this.cameraAttachAdapter.notifyDataSetChanged();
    }

    private void updateAlbumsDropDown() {
        final ArrayList<MediaController.AlbumEntry> arrayList;
        this.dropDownContainer.removeAllSubItems();
        if (this.mediaEnabled) {
            if (shouldLoadAllMedia()) {
                arrayList = MediaController.allMediaAlbums;
            } else {
                arrayList = MediaController.allPhotoAlbums;
            }
            ArrayList<MediaController.AlbumEntry> arrayList2 = new ArrayList<>(arrayList);
            this.dropDownAlbums = arrayList2;
            Collections.sort(arrayList2, new Comparator() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda3
                @Override // java.util.Comparator
                public final int compare(Object obj, Object obj2) {
                    return ChatAttachAlertPhotoLayout.m9838$r8$lambda$GQg7L31KvldWhCi96qPcDzTIG8(arrayList, (MediaController.AlbumEntry) obj, (MediaController.AlbumEntry) obj2);
                }
            });
        } else {
            this.dropDownAlbums = new ArrayList<>();
        }
        boolean zIsEmpty = this.dropDownAlbums.isEmpty();
        TextView textView = this.dropDown;
        if (zIsEmpty) {
            textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, (Drawable) null, (Drawable) null);
            return;
        }
        textView.setCompoundDrawablesWithIntrinsicBounds((Drawable) null, (Drawable) null, this.dropDownDrawable, (Drawable) null);
        int size = this.dropDownAlbums.size();
        for (int i = 0; i < size; i++) {
            MediaController.AlbumEntry albumEntry = this.dropDownAlbums.get(i);
            AlbumButton albumButton = new AlbumButton(getContext(), albumEntry.coverPhoto, albumEntry.bucketName, albumEntry.photos.size(), this.resourcesProvider);
            this.dropDownContainer.getPopupLayout().addView(albumButton);
            final int i2 = i + 10;
            albumButton.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda4
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    this.f$0.lambda$updateAlbumsDropDown$13(i2, view);
                }
            });
        }
    }

    public static void lambda$sendButtonPressed$1(boolean z, boolean z2, int i, Long l) {
            if (PhotoViewer.getInstance() != null) {
                PhotoViewer.getInstance().closePhotoAfterSelect = false;
                PhotoViewer.getInstance().doneButtonPressed = false;
            }
            ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
            chatAttachAlert.sent = true;
            if (chatAttachAlert != null) {
                chatAttachAlert.setButtonPressed(true);
            }
            ChatAttachAlertPhotoLayout.this.closeCamera(false);
            ChatAttachAlert chatAttachAlert2 = ChatAttachAlertPhotoLayout.this.parentAlert;
            chatAttachAlert2.delegate.didPressedButton(z ? 4 : 8, true, z2, i, 0, 0L, chatAttachAlert2.isCaptionAbove(), z, l.longValue());
            ChatAttachAlertPhotoLayout.cameraPhotos.clear();
            ChatAttachAlertPhotoLayout.selectedPhotosOrder.clear();
            ChatAttachAlertPhotoLayout.selectedPhotos.clear();
            ChatAttachAlertPhotoLayout.this.adapter.notifyDataSetChanged();
            ChatAttachAlertPhotoLayout.this.cameraAttachAdapter.notifyDataSetChanged();
            ChatAttachAlertPhotoLayout.this.parentAlert.dismiss(true);
            if (PhotoViewer.getInstance() != null) {
                PhotoViewer.getInstance().closePhoto(PhotoViewer.getInstance().closePhotoAfterSelectWithAnimation, false);
                PhotoViewer.getInstance().doneButtonPressed = true;
            }
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean scaleToFill() {
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
            if (chatAttachAlertPhotoLayout.parentAlert.destroyed) {
                return false;
            }
            return this.val$sameTakePictureOrientation || Settings.System.getInt(chatAttachAlertPhotoLayout.getContext().getContentResolver(), "accelerometer_rotation", 0) == 1;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public void willHidePhotoViewer() {
            int childCount = ChatAttachAlertPhotoLayout.this.gridView.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View childAt = ChatAttachAlertPhotoLayout.this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                    photoAttachPhotoCell.showImage();
                    photoAttachPhotoCell.showCheck(true);
                }
            }
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean canCaptureMorePhotos() {
            return ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos != 1;
        }

        @Override // org.telegram.ui.PhotoViewer.EmptyPhotoViewerProvider, org.telegram.ui.PhotoViewer.PhotoViewerProvider
        public boolean allowCaption() {
            ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
            return (chatAttachAlert.isPhotoPicker || chatAttachAlert.isPollAttach) ? false : true;
        }
    }

    public void showZoomControls(boolean z, boolean z2) {
        if ((this.zoomControlView.getTag() != null && z) || (this.zoomControlView.getTag() == null && !z)) {
            if (z) {
                Runnable runnable = this.zoomControlHideRunnable;
                if (runnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(runnable);
                }
                Runnable runnable2 = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda6
                    @Override // java.lang.Runnable
                    public final void run() {
                        this.f$0.lambda$showZoomControls$14();
                    }
                };
                this.zoomControlHideRunnable = runnable2;
                AndroidUtilities.runOnUIThread(runnable2, 2000L);
                return;
            }
            return;
        }
        AnimatorSet animatorSet = this.zoomControlAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        this.zoomControlView.setTag(z ? 1 : null);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.zoomControlAnimation = animatorSet2;
        animatorSet2.setDuration(180L);
        this.zoomControlAnimation.playTogether(ObjectAnimator.ofFloat(this.zoomControlView, (Property<ZoomControlView, Float>) View.ALPHA, z ? 1.0f : 0.0f));
        this.zoomControlAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.16
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                ChatAttachAlertPhotoLayout.this.zoomControlAnimation = null;
            }
        });
        this.zoomControlAnimation.start();
        if (z) {
            Runnable runnable3 = new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda7
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$showZoomControls$15();
                }
            };
            this.zoomControlHideRunnable = runnable3;
            AndroidUtilities.runOnUIThread(runnable3, 2000L);
        }
    }

    public void lambda$showCamera$16() {
        String currentFlashMode = this.cameraView.getCameraSession().getCurrentFlashMode();
        String nextFlashMode = this.cameraView.getCameraSession().getNextFlashMode();
        if (currentFlashMode == null || nextFlashMode == null) {
            return;
        }
        if (currentFlashMode.equals(nextFlashMode)) {
            for (int i = 0; i < 2; i++) {
                this.flashModeButton[i].setVisibility(4);
                this.flashModeButton[i].setAlpha(0.0f);
                this.flashModeButton[i].setTranslationY(0.0f);
            }
        } else {
            setCameraFlashModeIcon(this.flashModeButton[0], this.cameraView.getCameraSession().getCurrentFlashMode());
            int i2 = 0;
            while (i2 < 2) {
                this.flashModeButton[i2].setVisibility(i2 == 0 ? 0 : 4);
                this.flashModeButton[i2].setAlpha((i2 == 0 && this.cameraOpened) ? 1.0f : 0.0f);
                this.flashModeButton[i2].setTranslationY(0.0f);
                i2++;
            }
        }
        this.switchCameraButton.setImageResource(this.cameraView.isFrontface() ? R.drawable.camera_revert1 : R.drawable.camera_revert2);
        this.switchCameraButton.setVisibility(this.cameraView.hasFrontFaceCamera() ? 0 : 4);
        if (!this.cameraOpened) {
            AnimatorSet animatorSet = new AnimatorSet();
            this.cameraInitAnimation = animatorSet;
            animatorSet.playTogether(ObjectAnimator.ofFloat(this.cameraView, (Property<CameraViewInternal, Float>) View.ALPHA, 0.0f, 1.0f));
            this.cameraInitAnimation.setDuration(180L);
            this.cameraInitAnimation.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.19
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (animator.equals(ChatAttachAlertPhotoLayout.this.cameraInitAnimation)) {
                        ChatAttachAlertPhotoLayout.this.canSaveCameraPreview = true;
                        ChatAttachAlertPhotoLayout.this.cameraInitAnimation = null;
                    }
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationCancel(Animator animator) {
                    ChatAttachAlertPhotoLayout.this.cameraInitAnimation = null;
                }
            });
            this.cameraInitAnimation.start();
        }
        Runnable runnable = this.afterCameraInitRunnable;
        if (runnable != null) {
            runnable.run();
        }
    }

    public void hideCamera(boolean z) {
        if (!this.deviceHasGoodCamera || this.cameraView == null) {
            return;
        }
        saveLastCameraBitmap();
        this.cameraViewItemDecoration.updateBitmap();
        this.cameraView.destroy(z, null);
        AnimatorSet animatorSet = this.cameraInitAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
            this.cameraInitAnimation = null;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$$ExternalSyntheticLambda1
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$hideCamera$17();
            }
        }, 300L);
        this.canSaveCameraPreview = false;
    }

    public void lambda$onMenuItemClick$19(boolean z, int i, int i2) {
        this.parentAlert.applyCaption();
        ChatAttachAlert chatAttachAlert = this.parentAlert;
        chatAttachAlert.delegate.didPressedButton(7, false, z, i, 0, 0L, chatAttachAlert.isCaptionAbove(), false, 0L);
    }

    public void lambda$createHolder$0(PhotoAttachPhotoCell photoAttachPhotoCell, PhotoAttachPhotoCell photoAttachPhotoCell2) {
            TLRPC.Chat currentChat;
            if (ChatAttachAlertPhotoLayout.this.mediaEnabled) {
                ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
                if (chatAttachAlert.avatarPicker != 0 || chatAttachAlert.isPollAttach) {
                    return;
                }
                int iIntValue = ((Integer) photoAttachPhotoCell2.getTag()).intValue();
                MediaController.PhotoEntry photoEntry = photoAttachPhotoCell2.getPhotoEntry();
                if (ChatAttachAlertPhotoLayout.this.checkSendMediaEnabled(photoEntry)) {
                    return;
                }
                boolean zContainsKey = ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntry.imageId));
                boolean z = !zContainsKey;
                if (!zContainsKey && ChatAttachAlertPhotoLayout.selectedPhotos.size() + 1 > ChatAttachAlertPhotoLayout.this.maxCount()) {
                    ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                    BulletinFactory.of(chatAttachAlertPhotoLayout.parentAlert.sizeNotifierFrameLayout, chatAttachAlertPhotoLayout.resourcesProvider).createErrorBulletin(AndroidUtilities.replaceTags(LocaleController.formatPluralString("BusinessRepliesToastLimit", ChatAttachAlertPhotoLayout.this.parentAlert.baseFragment.getMessagesController().quickReplyMessagesLimit, new Object[0]))).show();
                    return;
                }
                if (!zContainsKey && ChatAttachAlertPhotoLayout.this.parentAlert.maxSelectedPhotos >= 0) {
                    int size = ChatAttachAlertPhotoLayout.selectedPhotos.size();
                    ChatAttachAlert chatAttachAlert2 = ChatAttachAlertPhotoLayout.this.parentAlert;
                    if (size >= chatAttachAlert2.maxSelectedPhotos) {
                        if (chatAttachAlert2.allowOrder) {
                            BaseFragment baseFragment = chatAttachAlert2.baseFragment;
                            if (!(baseFragment instanceof ChatActivity) || (currentChat = ((ChatActivity) baseFragment).getCurrentChat()) == null || ChatObject.hasAdminRights(currentChat) || !currentChat.slowmode_enabled || ChatAttachAlertPhotoLayout.this.alertOnlyOnce == 2) {
                                return;
                            }
                            AlertsCreator.createSimpleAlert(ChatAttachAlertPhotoLayout.this.getContext(), LocaleController.getString(R.string.Slowmode), LocaleController.getString(R.string.SlowmodeSelectSendError), ChatAttachAlertPhotoLayout.this.resourcesProvider).show();
                            if (ChatAttachAlertPhotoLayout.this.alertOnlyOnce == 1) {
                                ChatAttachAlertPhotoLayout.this.alertOnlyOnce = 2;
                                return;
                            }
                            return;
                        }
                        return;
                    }
                }
                int size2 = !zContainsKey ? ChatAttachAlertPhotoLayout.selectedPhotosOrder.size() : -1;
                ChatAttachAlert chatAttachAlert3 = ChatAttachAlertPhotoLayout.this.parentAlert;
                if ((chatAttachAlert3.baseFragment instanceof ChatActivity) && chatAttachAlert3.allowOrder) {
                    photoAttachPhotoCell2.setChecked(size2, z, true);
                } else {
                    photoAttachPhotoCell2.setChecked(-1, z, true);
                }
                ChatAttachAlertPhotoLayout.this.addToSelectedPhotos(photoEntry, iIntValue);
                PhotoAttachAdapter photoAttachAdapter = ChatAttachAlertPhotoLayout.this.cameraAttachAdapter;
                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2 = ChatAttachAlertPhotoLayout.this;
                if (this == photoAttachAdapter) {
                    if (chatAttachAlertPhotoLayout2.adapter.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry) {
                        iIntValue++;
                    }
                    if (ChatAttachAlertPhotoLayout.this.adapter.hasCameraSpaceRow && iIntValue >= ChatAttachAlertPhotoLayout.this.itemsPerRow) {
                        iIntValue++;
                    }
                    ChatAttachAlertPhotoLayout.this.adapter.notifyItemChanged(iIntValue);
                } else {
                    chatAttachAlertPhotoLayout2.cameraAttachAdapter.notifyItemChanged(iIntValue);
                }
                ChatAttachAlertPhotoLayout.this.parentAlert.updateCountButton(zContainsKey ? 2 : 1);
                photoAttachPhotoCell.setHasSpoiler(photoEntry.hasSpoiler);
                photoAttachPhotoCell.setHighQuality(photoEntry.isHighQuality());
                photoAttachPhotoCell.setStarsPrice(photoEntry.starsAmount, ChatAttachAlertPhotoLayout.selectedPhotos.size() > 1);
            }
        }

        public MediaController.PhotoEntry getPhoto(int i) {
            if (this.hasCameraSpaceRow && i > ChatAttachAlertPhotoLayout.this.itemsPerRow) {
                i--;
            }
            if (this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry && (!ExteraConfig.getHideCameraTile() || ChatAttachAlertPhotoLayout.this.noCameraPermissions)) {
                i--;
            }
            return ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            int itemViewType = viewHolder.getItemViewType();
            if (itemViewType != 0) {
                if (itemViewType == 1) {
                    ((PhotoAttachCameraCell) viewHolder.itemView).setItemSize(ChatAttachAlertPhotoLayout.this.itemSize);
                    return;
                }
                if (itemViewType != 3) {
                    if (itemViewType != 7) {
                        return;
                    }
                    ((GalleryEmptyView) viewHolder.itemView).setUseAnEmojiVisible(ChatAttachAlertPhotoLayout.this.showAvatarConstructor);
                    return;
                } else {
                    PhotoAttachPermissionCell photoAttachPermissionCell = (PhotoAttachPermissionCell) viewHolder.itemView;
                    photoAttachPermissionCell.setItemSize(ChatAttachAlertPhotoLayout.this.itemSize);
                    photoAttachPermissionCell.setType((this.needCamera && ChatAttachAlertPhotoLayout.this.noCameraPermissions && i == 0) ? 0 : 1);
                    return;
                }
            }
            if (this.hasCameraSpaceRow && i > ChatAttachAlertPhotoLayout.this.itemsPerRow) {
                i--;
            }
            if (this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry && (!ExteraConfig.getHideCameraTile() || ChatAttachAlertPhotoLayout.this.noCameraPermissions)) {
                i--;
            }
            if (ChatAttachAlertPhotoLayout.this.showAvatarConstructor) {
                i--;
            }
            PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) viewHolder.itemView;
            PhotoAttachAdapter photoAttachAdapter = ChatAttachAlertPhotoLayout.this.adapter;
            ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
            if (this == photoAttachAdapter) {
                photoAttachPhotoCell.setItemSize(chatAttachAlertPhotoLayout.itemSize);
            } else {
                photoAttachPhotoCell.setIsVertical(chatAttachAlertPhotoLayout.cameraPhotoLayoutManager.getOrientation() == 1);
            }
            ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
            if (chatAttachAlert.avatarPicker != 0 || chatAttachAlert.storyMediaPicker || chatAttachAlert.isPollAttach) {
                photoAttachPhotoCell.getCheckBox().setVisibility(8);
            } else {
                photoAttachPhotoCell.getCheckBox().setVisibility(0);
            }
            MediaController.PhotoEntry photoEntryAtPosition = ChatAttachAlertPhotoLayout.this.getPhotoEntryAtPosition(i);
            if (photoEntryAtPosition == null) {
                return;
            }
            boolean z = ChatAttachAlertPhotoLayout.selectedPhotos.size() > 1;
            boolean z2 = this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry && (!ExteraConfig.getHideCameraTile() || ChatAttachAlertPhotoLayout.this.noCameraPermissions);
            boolean z3 = i == getItemCount() - 1;
            ChatAttachAlert chatAttachAlert2 = ChatAttachAlertPhotoLayout.this.parentAlert;
            photoAttachPhotoCell.setPhotoEntry(photoEntryAtPosition, z, z2, z3, chatAttachAlert2 != null && chatAttachAlert2.allowLivePhotos);
            ChatAttachAlert chatAttachAlert3 = ChatAttachAlertPhotoLayout.this.parentAlert;
            if ((chatAttachAlert3.baseFragment instanceof ChatActivity) && chatAttachAlert3.allowOrder) {
                photoAttachPhotoCell.setChecked(ChatAttachAlertPhotoLayout.selectedPhotosOrder.indexOf(Integer.valueOf(photoEntryAtPosition.imageId)), ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntryAtPosition.imageId)), false);
            } else {
                photoAttachPhotoCell.setChecked(-1, ChatAttachAlertPhotoLayout.selectedPhotos.containsKey(Integer.valueOf(photoEntryAtPosition.imageId)), false);
            }
            if (!ChatAttachAlertPhotoLayout.this.videoEnabled && photoEntryAtPosition.isVideo) {
                photoAttachPhotoCell.setAlpha(0.3f);
            } else if (!ChatAttachAlertPhotoLayout.this.photoEnabled && !photoEntryAtPosition.isVideo) {
                photoAttachPhotoCell.setAlpha(0.3f);
            } else {
                photoAttachPhotoCell.setAlpha(1.0f);
            }
            photoAttachPhotoCell.getImageView().setTag(Integer.valueOf(i));
            photoAttachPhotoCell.setTag(Integer.valueOf(i));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            if (i == 0) {
                if (!this.viewsCache.isEmpty()) {
                    RecyclerListView.Holder holder = this.viewsCache.get(0);
                    this.viewsCache.remove(0);
                    return holder;
                }
                return createHolder();
            }
            if (i == 1) {
                return new RecyclerListView.Holder(new PhotoAttachCameraCell(this.mContext));
            }
            if (i == 2) {
                return new RecyclerListView.Holder(new View(this.mContext) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.PhotoAttachAdapter.2
                    @Override // android.view.View
                    public void onMeasure(int i2, int i3) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(i2), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(ChatAttachAlertPhotoLayout.this.gridExtraSpace, TLObject.FLAG_30));
                    }
                });
            }
            if (i == 4) {
                return new RecyclerListView.Holder(new AvatarConstructorPreviewCell(this.mContext, ChatAttachAlertPhotoLayout.this.parentAlert.forUser) { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.PhotoAttachAdapter.3
                    @Override // org.telegram.ui.Components.AvatarConstructorPreviewCell, android.widget.FrameLayout, android.view.View
                    public void onMeasure(int i2, int i3) {
                        super.onMeasure(View.MeasureSpec.makeMeasureSpec(ChatAttachAlertPhotoLayout.this.itemSize, TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(ChatAttachAlertPhotoLayout.this.itemSize, TLObject.FLAG_30));
                    }
                });
            }
            if (i == 5) {
                return new RecyclerListView.Holder(new View(this.mContext));
            }
            if (i == 7) {
                Context context = this.mContext;
                ChatAttachAlert chatAttachAlert = ChatAttachAlertPhotoLayout.this.parentAlert;
                GalleryEmptyView galleryEmptyView = new GalleryEmptyView(context, chatAttachAlert.currentAccount, chatAttachAlert.forUser);
                galleryEmptyView.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(400.0f)));
                galleryEmptyView.setGravity(17);
                galleryEmptyView.isClickable();
                final ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                galleryEmptyView.doOnCameraAccess(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$PhotoAttachAdapter$$ExternalSyntheticLambda0
                    @Override // java.lang.Runnable
                    public final void run() {
                        chatAttachAlertPhotoLayout.openCameraWithPermissionCheck();
                    }
                });
                final ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2 = ChatAttachAlertPhotoLayout.this;
                galleryEmptyView.doOnGalleryAccessClick(new Runnable() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$PhotoAttachAdapter$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        chatAttachAlertPhotoLayout2.requestGalleryPermission();
                    }
                });
                galleryEmptyView.doOnEmojiButton(new Utilities.Callback() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout$PhotoAttachAdapter$$ExternalSyntheticLambda2
                    @Override 
                    public final void run(Object obj) {
                        this.f$0.lambda$onCreateViewHolder$1((Long) obj);
                    }
                });
                return new RecyclerListView.Holder(galleryEmptyView);
            }
            if (i != 8) {
                return new RecyclerListView.Holder(new PhotoAttachPermissionCell(this.mContext, ChatAttachAlertPhotoLayout.this.resourcesProvider));
            }
            FrameLayout frameLayout = new FrameLayout(this.mContext);
            frameLayout.setLayoutParams(new RecyclerView.LayoutParams(-1, AndroidUtilities.dp(56.0f)));
            ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(this.mContext, ChatAttachAlertPhotoLayout.this.resourcesProvider);
            buttonWithCounterView.setUseWrapContent(true);
            buttonWithCounterView.setRound();
            buttonWithCounterView.setPadding(AndroidUtilities.dp(28.0f), 0, AndroidUtilities.dp(28.0f), 0);
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("c");
            spannableStringBuilder.setSpan(new ColoredImageSpan(R.drawable.camera), 0, 1, 33);
            spannableStringBuilder.append((CharSequence) "  ").append((CharSequence) LocaleController.getString(R.string.GalleryAccessAllowAccessCamera));
            buttonWithCounterView.setText(spannableStringBuilder, false);
            frameLayout.addView(buttonWithCounterView, LayoutHelper.createFrame(-2, 44.0f, 81, 10.0f, 0.0f, 10.0f, 12.0f));
            return new RecyclerListView.Holder(frameLayout);
        }

        public /* synthetic */ void lambda$onCreateViewHolder$1(Long l) {
            ChatAttachAlertPhotoLayout.this.showAvatarConstructorFragment(null, null, l.longValue());
            ChatAttachAlertPhotoLayout.this.parentAlert.lambda$new$0();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(RecyclerView.ViewHolder viewHolder) {
            if (viewHolder.itemView instanceof PhotoAttachCameraCell) {
                ChatAttachAlertPhotoLayout.this.cameraViewItemDecoration.updateBitmap();
            }
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (!ChatAttachAlertPhotoLayout.this.mediaEnabled) {
                return 1;
            }
            int size = 0;
            this.hasCamera = false;
            this.hasCameraSpaceRow = false;
            if (ChatAttachAlertPhotoLayout.this.noGalleryPermissions && this == ChatAttachAlertPhotoLayout.this.adapter) {
                return 2;
            }
            if (this.needCamera && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry && (!ExteraConfig.getHideCameraTile() || ChatAttachAlertPhotoLayout.this.noCameraPermissions)) {
                this.hasCamera = true;
                size = 1;
            }
            if (ChatAttachAlertPhotoLayout.this.showAvatarConstructor) {
                size++;
            }
            if (ChatAttachAlertPhotoLayout.this.noGalleryPermissions && this == ChatAttachAlertPhotoLayout.this.adapter) {
                size++;
            }
            this.photosStartRow = size;
            if (!ChatAttachAlertPhotoLayout.this.noGalleryPermissions) {
                size += ChatAttachAlertPhotoLayout.cameraPhotos.size();
                if (ChatAttachAlertPhotoLayout.this.selectedAlbumEntry != null) {
                    size += ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos.size();
                }
            }
            this.photosEndRow = size;
            if (this.hasCamera && size > ChatAttachAlertPhotoLayout.this.itemsPerRow && !ChatAttachAlertPhotoLayout.this.noCameraPermissions) {
                this.hasCameraSpaceRow = true;
                size++;
            }
            if (this == ChatAttachAlertPhotoLayout.this.adapter) {
                size++;
            }
            this.itemsCount = size;
            return size;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemViewType(int i) {
            if (!ChatAttachAlertPhotoLayout.this.mediaEnabled) {
                return 2;
            }
            if (ChatAttachAlertPhotoLayout.this.noGalleryPermissions && this == ChatAttachAlertPhotoLayout.this.adapter) {
                return i == 0 ? 7 : 2;
            }
            if (this.needCamera && i == 0 && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == ChatAttachAlertPhotoLayout.this.galleryAlbumEntry && (!ExteraConfig.getHideCameraTile() || ChatAttachAlertPhotoLayout.this.noCameraPermissions)) {
                return ChatAttachAlertPhotoLayout.this.noCameraPermissions ? 8 : 1;
            }
            if (this.hasCameraSpaceRow && i == ChatAttachAlertPhotoLayout.this.itemsPerRow) {
                return 5;
            }
            int i2 = (!this.hasCameraSpaceRow || i <= ChatAttachAlertPhotoLayout.this.itemsPerRow) ? i : i - 1;
            if (this.needCamera && (!ExteraConfig.getHideCameraTile() || ChatAttachAlertPhotoLayout.this.noCameraPermissions)) {
                i2--;
            }
            if (ChatAttachAlertPhotoLayout.this.showAvatarConstructor && i2 == 0) {
                return 4;
            }
            if (this == ChatAttachAlertPhotoLayout.this.adapter && i == this.itemsCount - 1) {
                return 2;
            }
            return ChatAttachAlertPhotoLayout.this.noGalleryPermissions ? 3 : 0;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (this == ChatAttachAlertPhotoLayout.this.adapter) {
                ChatAttachAlertPhotoLayout.this.progressView.setVisibility((!(getItemCount() == 1 && !ChatAttachAlertPhotoLayout.this.noGalleryPermissions && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == null) && ChatAttachAlertPhotoLayout.this.mediaEnabled) ? 4 : 0);
            }
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public float getScrollProgress(RecyclerListView recyclerListView) {
            int i = ChatAttachAlertPhotoLayout.this.itemsPerRow;
            int iCeil = (int) Math.ceil(this.itemsCount / i);
            if (recyclerListView.getChildCount() == 0) {
                return 0.0f;
            }
            int measuredHeight = recyclerListView.getChildAt(0).getMeasuredHeight();
            View childAt = recyclerListView.getChildAt(0);
            int childAdapterPosition = recyclerListView.getChildAdapterPosition(childAt);
            if (childAdapterPosition < 0) {
                return 0.0f;
            }
            return Utilities.clamp((((childAdapterPosition / i) * measuredHeight) - childAt.getTop()) / ((iCeil * measuredHeight) - (recyclerListView.getMeasuredHeight() - ActionBar.getCurrentActionBarHeight())), 1.0f, 0.0f);
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public String getLetter(int i) {
            MediaController.PhotoEntry photo = getPhoto(i);
            if (photo == null) {
                if (i <= this.photosStartRow) {
                    if (!ChatAttachAlertPhotoLayout.cameraPhotos.isEmpty()) {
                        photo = (MediaController.PhotoEntry) ChatAttachAlertPhotoLayout.cameraPhotos.get(0);
                    } else if (ChatAttachAlertPhotoLayout.this.selectedAlbumEntry != null && ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos != null) {
                        photo = ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos.get(0);
                    }
                } else if (!ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos.isEmpty()) {
                    photo = ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos.get(ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos.size() - 1);
                }
            }
            if (photo != null) {
                long j = photo.dateTaken;
                if (Build.VERSION.SDK_INT <= 28) {
                    j /= 1000;
                }
                return LocaleController.formatYearMont(j, true);
            }
            return _UrlKt.FRAGMENT_ENCODE_SET;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public boolean fastScrollIsVisible(RecyclerListView recyclerListView) {
            return !(ChatAttachAlertPhotoLayout.cameraPhotos.isEmpty() && (ChatAttachAlertPhotoLayout.this.selectedAlbumEntry == null || ChatAttachAlertPhotoLayout.this.selectedAlbumEntry.photos.isEmpty())) && ChatAttachAlertPhotoLayout.this.parentAlert.pinnedToTop && getTotalItemsCount() > 30;
        }

        @Override // org.telegram.ui.Components.RecyclerListView.FastScrollAdapter
        public void getPositionForScrollProgress(RecyclerListView recyclerListView, float f, int[] iArr) {
            int currentActionBarHeight = ActionBar.getCurrentActionBarHeight();
            int measuredHeight = recyclerListView.getChildAt(0).getMeasuredHeight();
            float fCeil = (((int) (Math.ceil(getTotalItemsCount() / ChatAttachAlertPhotoLayout.this.itemsPerRow) * ((double) measuredHeight))) - (recyclerListView.getMeasuredHeight() - currentActionBarHeight)) * f;
            float f2 = measuredHeight;
            iArr[0] = ((int) (fCeil / f2)) * ChatAttachAlertPhotoLayout.this.itemsPerRow;
            int paddingTop = ((int) (fCeil % f2)) + recyclerListView.getPaddingTop() + ((int) (currentActionBarHeight * (1.0f - f)));
            iArr[1] = paddingTop;
            if (iArr[0] != 0 || paddingTop >= ChatAttachAlertPhotoLayout.this.getListTopPadding()) {
                return;
            }
            iArr[1] = ChatAttachAlertPhotoLayout.this.getListTopPadding() + currentActionBarHeight;
        }
    }

    public class CameraViewItemDecoration extends RecyclerView.ItemDecoration implements IBlur3Capture {
        private final Drawable cameraDrawable;
        private final Path clipPath = new Path();
        private final RecyclerView parent;
        private Drawable placeholderDrawable;

        public CameraViewItemDecoration(RecyclerView recyclerView) {
            this.parent = recyclerView;
            this.cameraDrawable = ChatAttachAlertPhotoLayout.this.getContext().getResources().getDrawable(R.drawable.camera).mutate();
        }

        @Override // org.telegram.ui.Components.blur3.capture.IBlur3Capture
        public void capture(Canvas canvas, RectF rectF) {
            draw(canvas, this.parent, null, rectF);
        }

        @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
        public void onDraw(Canvas canvas, RecyclerView recyclerView, RecyclerView.State state) {
            draw(canvas, recyclerView, null, null);
        }

        @Override // org.telegram.ui.Components.blur3.capture.IBlur3Capture
        public void captureCalculateHash(IBlur3Hash iBlur3Hash, RectF rectF) {
            draw(null, this.parent, iBlur3Hash, rectF);
        }

        private void draw(Canvas canvas, RecyclerView recyclerView, IBlur3Hash iBlur3Hash, RectF rectF) {
            int top;
            CameraViewInternal cameraViewInternal;
            CameraViewInternal cameraViewInternal2;
            if (!ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress) {
                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                if (!chatAttachAlertPhotoLayout.cameraOpened && chatAttachAlertPhotoLayout.adapter.hasCamera && !ChatAttachAlertPhotoLayout.this.noCameraPermissions && !ChatAttachAlertPhotoLayout.this.noGalleryPermissions && !ExteraConfig.getHideCameraTile()) {
                    RecyclerView.ViewHolder viewHolderFindViewHolderForAdapterPosition = recyclerView.findViewHolderForAdapterPosition(0);
                    if (viewHolderFindViewHolderForAdapterPosition != null) {
                        top = viewHolderFindViewHolderForAdapterPosition.itemView.getTop();
                    } else {
                        viewHolderFindViewHolderForAdapterPosition = recyclerView.findViewHolderForAdapterPosition(ChatAttachAlertPhotoLayout.this.itemsPerRow);
                        if (viewHolderFindViewHolderForAdapterPosition == null) {
                            if (iBlur3Hash != null) {
                                iBlur3Hash.unsupported();
                                return;
                            }
                            return;
                        }
                        top = (viewHolderFindViewHolderForAdapterPosition.itemView.getTop() - AndroidUtilities.dp(2.0f)) - ChatAttachAlertPhotoLayout.this.itemSize;
                    }
                    int left = viewHolderFindViewHolderForAdapterPosition.itemView.getLeft();
                    int i = ChatAttachAlertPhotoLayout.this.itemSize + left;
                    int iDp = (ChatAttachAlertPhotoLayout.this.itemSize * 2) + top + AndroidUtilities.dp(2.0f);
                    if (iBlur3Hash != null) {
                        iBlur3Hash.add(left);
                        iBlur3Hash.add(top);
                        iBlur3Hash.add(i);
                        iBlur3Hash.add(iDp);
                    }
                    if (rectF == null || rectF.intersects(left, top, i, iDp)) {
                        if (iBlur3Hash != null) {
                            iBlur3Hash.add(this.placeholderDrawable != null && ((cameraViewInternal2 = ChatAttachAlertPhotoLayout.this.cameraView) == null || !cameraViewInternal2.isInited() || ChatAttachAlertPhotoLayout.this.isHidden));
                            iBlur3Hash.add(ChatAttachAlertPhotoLayout.this.cameraView != null);
                            iBlur3Hash.add(this.cameraDrawable != null);
                        }
                        if (canvas == null) {
                            return;
                        }
                        float fDp = AndroidUtilities.dp(16.0f);
                        this.clipPath.rewind();
                        float f = left;
                        float f2 = top;
                        this.clipPath.addRoundRect(f, f2, i + fDp, iDp + fDp, fDp, fDp, Path.Direction.CW);
                        canvas.save();
                        canvas.clipPath(this.clipPath);
                        if (this.placeholderDrawable != null && ((cameraViewInternal = ChatAttachAlertPhotoLayout.this.cameraView) == null || !cameraViewInternal.isInited() || ChatAttachAlertPhotoLayout.this.isHidden)) {
                            this.placeholderDrawable.setBounds(left, top, i, iDp);
                            this.placeholderDrawable.draw(canvas);
                        }
                        CameraViewInternal cameraViewInternal3 = ChatAttachAlertPhotoLayout.this.cameraView;
                        if (cameraViewInternal3 != null) {
                            cameraViewInternal3.drawInDecoration = true;
                            canvas.save();
                            canvas.clipRect(left, top, i, iDp);
                            canvas.translate(f, f2);
                            ChatAttachAlertPhotoLayout.this.cameraView.draw(canvas);
                            canvas.restore();
                            ChatAttachAlertPhotoLayout.this.cameraView.drawInDecoration = false;
                        }
                        if (this.cameraDrawable != null) {
                            int iDp2 = AndroidUtilities.dp(24.0f);
                            int iDp3 = (i - AndroidUtilities.dp(7.0f)) - iDp2;
                            int iDp4 = top + AndroidUtilities.dp(7.0f);
                            this.cameraDrawable.setBounds(iDp3, iDp4, iDp3 + iDp2, iDp2 + iDp4);
                            this.cameraDrawable.draw(canvas);
                        }
                        canvas.restore();
                        ChatAttachAlertPhotoLayout.this.gridView.invalidate();
                        return;
                    }
                    return;
                }
            }
            if (iBlur3Hash != null) {
                iBlur3Hash.unsupported();
            }
        }

        public void updateBitmap() {
            Bitmap bitmapDecodeFile;
            try {
                bitmapDecodeFile = BitmapFactory.decodeFile(new File(ApplicationLoader.getFilesDirFixed(), "cthumb.jpg").getAbsolutePath());
            } catch (Throwable unused) {
                bitmapDecodeFile = null;
            }
            if (bitmapDecodeFile != null) {
                this.placeholderDrawable = new BitmapDrawable(ChatAttachAlertPhotoLayout.this.getContext().getResources(), bitmapDecodeFile);
            } else {
                this.placeholderDrawable = ChatAttachAlertPhotoLayout.this.getContext().getResources().getDrawable(R.drawable.icplaceholder).mutate();
            }
            RecyclerListView recyclerListView = ChatAttachAlertPhotoLayout.this.gridView;
            if (recyclerListView != null) {
                recyclerListView.invalidate();
            }
        }
    }

    public class CameraViewInternal extends CameraView {
        Bulletin.Delegate bulletinDelegate;
        public boolean drawInDecoration;

        public CameraViewInternal(Context context, boolean z, boolean z2) {
            super(context, z, z2);
            this.bulletinDelegate = new Bulletin.Delegate() { // from class: org.telegram.ui.Components.ChatAttachAlertPhotoLayout.CameraViewInternal.1
                @Override // org.telegram.ui.Components.Bulletin.Delegate
                public int getBottomOffset(int i) {
                    return AndroidUtilities.dp(126.0f) + ChatAttachAlertPhotoLayout.this.parentAlert.getBottomInset();
                }
            };
        }

        @Override 
        public void dispatchDraw(Canvas canvas) {
            if (AndroidUtilities.makingGlobalBlurBitmap) {
                return;
            }
            if (this.drawInDecoration || (!ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress && ChatAttachAlertPhotoLayout.this.cameraOpened)) {
                super.dispatchDraw(canvas);
                return;
            }
            float commentTextViewTop = ((ChatAttachAlertPhotoLayout.this.parentAlert.getCommentTextViewTop() + ChatAttachAlertPhotoLayout.this.currentPanTranslationY) + ChatAttachAlertPhotoLayout.this.parentAlert.getContainerView().getTranslationY()) - ChatAttachAlertPhotoLayout.this.cameraView.getTranslationY();
            MentionsContainerView mentionsContainerView = ChatAttachAlertPhotoLayout.this.parentAlert.mentionContainer;
            int iMin = (int) Math.min(commentTextViewTop - (mentionsContainerView != null ? mentionsContainerView.clipBottom() + AndroidUtilities.dp(8.0f) : 0.0f), getMeasuredHeight());
            if (ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress) {
                RectF rectF = AndroidUtilities.rectTmp;
                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout = ChatAttachAlertPhotoLayout.this;
                float f = chatAttachAlertPhotoLayout.animationClipLeft + (chatAttachAlertPhotoLayout.cameraViewOffsetX * (1.0f - ChatAttachAlertPhotoLayout.this.cameraOpenProgress));
                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout2 = ChatAttachAlertPhotoLayout.this;
                float f2 = chatAttachAlertPhotoLayout2.animationClipTop + (chatAttachAlertPhotoLayout2.cameraViewOffsetY * (1.0f - ChatAttachAlertPhotoLayout.this.cameraOpenProgress));
                ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout3 = ChatAttachAlertPhotoLayout.this;
                rectF.set(f, f2, chatAttachAlertPhotoLayout3.animationClipRight, AndroidUtilities.lerp(Math.min(iMin, chatAttachAlertPhotoLayout3.animationClipBottom), getMeasuredHeight(), ChatAttachAlertPhotoLayout.this.cameraOpenProgress));
            } else {
                if (!ChatAttachAlertPhotoLayout.this.cameraAnimationInProgress) {
                    ChatAttachAlertPhotoLayout chatAttachAlertPhotoLayout4 = ChatAttachAlertPhotoLayout.this;
                    if (!chatAttachAlertPhotoLayout4.cameraOpened) {
                        AndroidUtilities.rectTmp.set(chatAttachAlertPhotoLayout4.cameraViewOffsetX, ChatAttachAlertPhotoLayout.this.cameraViewOffsetY, getMeasuredWidth(), Math.min(iMin, getMeasuredHeight()));
                        return;
                    }
                }
                AndroidUtilities.rectTmp.set(0.0f, 0.0f, getMeasuredWidth(), Math.min(iMin, getMeasuredHeight()));
            }
            canvas.save();
            canvas.clipRect(AndroidUtilities.rectTmp);
            super.dispatchDraw(canvas);
            canvas.restore();
        }

        @Override // android.view.View
        public void setVisibility(int i) {
            super.setVisibility(i);
            ChatAttachAlertPhotoLayout.this.gridView.invalidate();
        }

        @Override 
        public void showTexture(boolean z, boolean z2) {
            super.showTexture(z, z2);
            ChatAttachAlertPhotoLayout.this.gridView.invalidate();
        }

        @Override 
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            Bulletin.addDelegate(ChatAttachAlertPhotoLayout.this.cameraView, this.bulletinDelegate);
            ChatAttachAlertPhotoLayout.this.gridView.invalidate();
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            Bulletin.removeDelegate(ChatAttachAlertPhotoLayout.this.cameraView);
        }
    }

    public boolean hasLivePhotos() {
        if (selectedPhotos.isEmpty()) {
            return false;
        }
        for (Map.Entry<Object, Object> entry : selectedPhotos.entrySet()) {
            if ((entry.getValue() instanceof MediaController.PhotoEntry) && ((MediaController.PhotoEntry) entry.getValue()).isLivePhoto()) {
                return true;
            }
        }
        return false;
    }

    public boolean areLivePhotosEnabled() {
        if (selectedPhotos.isEmpty()) {
            return false;
        }
        for (Map.Entry<Object, Object> entry : selectedPhotos.entrySet()) {
            if (entry.getValue() instanceof MediaController.PhotoEntry) {
                MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) entry.getValue();
                if (photoEntry.isLivePhoto() && photoEntry.isUnalivePhoto()) {
                    return false;
                }
            }
        }
        return true;
    }

    public void toggleLivePhotos(boolean z) {
        if (selectedPhotos.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<Object, Object>> it = selectedPhotos.entrySet().iterator();
        while (true) {
            if (it.hasNext()) {
                Map.Entry<Object, Object> next = it.next();
                if (next.getValue() instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) next.getValue();
                    if (photoEntry.isLivePhoto()) {
                        photoEntry.discardLivePhoto = Boolean.valueOf(!z);
                        for (int i = 0; i < this.gridView.getChildCount(); i++) {
                            View childAt = this.gridView.getChildAt(i);
                            if (childAt instanceof PhotoAttachPhotoCell) {
                                PhotoAttachPhotoCell photoAttachPhotoCell = (PhotoAttachPhotoCell) childAt;
                                if (photoAttachPhotoCell.getPhotoEntry() == photoEntry) {
                                    photoAttachPhotoCell.getImageView().invalidate();
                                }
                            }
                        }
                    }
                }
            } else {
                SharedPreferences.Editor editorEdit = ApplicationLoader.applicationContext.getSharedPreferences("mainconfig", 0).edit();
                SharedConfig.photoLiveDefault = z;
                editorEdit.putBoolean("photoLiveDefault", z).apply();
                updateCells();
                return;
            }
        }
    }

    public void updateCells() {
        if (this.gridView != null) {
            for (int i = 0; i < this.gridView.getChildCount(); i++) {
                View childAt = this.gridView.getChildAt(i);
                if (childAt instanceof PhotoAttachPhotoCell) {
                    ((PhotoAttachPhotoCell) childAt).imageView.invalidate();
                }
            }
        }
    }
}
