package org.telegram.ui.Components.Paint.Views;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.DashPathEffect;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.exteragram.messenger.ExteraConfig;
import com.google.android.gms.cast.MediaError;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.segmentation.subject.Subject;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentation;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmentationResult;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenter;
import com.google.mlkit.vision.segmentation.subject.SubjectSegmenterOptions;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.EmuDetector;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageReceiver;
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
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.ObjectDetectionEmojis;
import org.telegram.ui.Components.ThanosEffect;
import org.telegram.ui.Stories.recorder.DownloadButton;
import org.telegram.ui.Stories.recorder.StoryEntry;

@SuppressLint({"ViewConstructor"})
public class StickerMakerView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private final TextView actionTextView;
    private final Path areaPath;
    private final Paint bgPaint;
    private final Path bgPath;
    private final Paint borderPaint;
    private ValueAnimator bordersAnimator;
    private float bordersAnimatorValue;
    private float bordersAnimatorValueStart;
    private final PathMeasure bordersPathMeasure;
    private int containerHeight;
    private int containerWidth;
    public int currentAccount;
    private final Paint dashPaint;
    private final Path dashPath;
    public String detectedEmoji;
    public boolean empty;
    private Rect exclusionRect;
    private ArrayList<Rect> exclusionRects;
    private Bitmap filteredBitmap;
    private float imageReceiverHeight;
    private final Matrix imageReceiverMatrix;
    private float imageReceiverWidth;
    private boolean isSegmentedState;
    public boolean isThanosInProgress;
    private DownloadButton.PreparingVideoToast loadingToast;
    public SegmentedObject[] objects;
    public int orientation;
    private final AnimatedFloat outlineAlpha;
    private final RectF outlineBounds;
    private Path outlineBoundsInnerPath;
    private Path outlineBoundsPath;
    public final Matrix outlineMatrix;
    public boolean outlineVisible;
    public float outlineWidth;
    private final Theme.ResourcesProvider resourcesProvider;
    private final Path screenPath;
    private final AnimatedFloat segmentBorderAlpha;
    private float segmentBorderImageHeight;
    private float segmentBorderImageWidth;
    private final Paint segmentBorderPaint;
    private volatile boolean segmentingLoaded;
    private volatile boolean segmentingLoading;
    private SegmentedObject selectedObject;
    public boolean setOutlineBounds;
    private volatile Bitmap sourceBitmap;
    private int stickerCornerRoundness;
    private StickerCutOutBtn stickerCutOutBtn;
    private StickerUploader stickerUploader;
    private ThanosEffect thanosEffect;
    float tx;
    float ty;
    public PaintWeightChooserView weightChooserView;

    public static void lambda$segmentImage$6(final int i, final Utilities.Callback callback, final List list) {
        final ArrayList arrayList = new ArrayList();
        Utilities.themeQueue.postRunnable(new Runnable() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda11
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$segmentImage$5(i, list, arrayList, callback);
            }
        });
    }

    public void lambda$uploadStickerFile$12(Utilities.Callback callback, String str, String str2, CharSequence charSequence, boolean z, long j, TLRPC.StickerSet stickerSet, TLRPC.Document document, TLRPC.Document document2, VideoEditedInfo videoEditedInfo, String str3, Utilities.Callback2 callback2) {
        StickerUploader stickerUploader;
        boolean z2 = callback == null || (stickerUploader = this.stickerUploader) == null || !stickerUploader.uploaded;
        if (z2) {
            StickerUploader stickerUploader2 = this.stickerUploader;
            if (stickerUploader2 != null) {
                stickerUploader2.destroy(true);
            }
            this.stickerUploader = new StickerUploader();
        }
        StickerUploader stickerUploader3 = this.stickerUploader;
        stickerUploader3.emoji = str;
        stickerUploader3.finalPath = str2;
        stickerUploader3.path = str2;
        stickerUploader3.stickerPackName = charSequence;
        stickerUploader3.addToFavorite = z;
        stickerUploader3.sendToDialogId = j;
        stickerUploader3.stickerSet = stickerSet;
        stickerUploader3.replacedSticker = document;
        stickerUploader3.uploadedSticker = document2;
        stickerUploader3.videoEditedInfo = videoEditedInfo;
        stickerUploader3.thumbPath = str3;
        stickerUploader3.whenDone = callback;
        stickerUploader3.customHandler = callback2;
        stickerUploader3.setupFiles();
        if (!z2) {
            afterUploadingMedia();
        } else if (document2 != null) {
            StickerUploader stickerUploader4 = this.stickerUploader;
            stickerUploader4.tlInputStickerSetItem = MediaDataController.getInputStickerSetItem(document2, stickerUploader4.emoji);
            this.stickerUploader.mediaDocument = new TLRPC.TL_messageMediaDocument();
            TLRPC.TL_messageMediaDocument tL_messageMediaDocument = this.stickerUploader.mediaDocument;
            tL_messageMediaDocument.flags |= 1;
            tL_messageMediaDocument.document = document2;
            afterUploadingMedia();
        } else if (videoEditedInfo != null) {
            TLRPC.TL_message tL_message = new TLRPC.TL_message();
            tL_message.id = 1;
            StickerUploader stickerUploader5 = this.stickerUploader;
            String absolutePath = StoryEntry.makeCacheFile(UserConfig.selectedAccount, "webm").getAbsolutePath();
            tL_message.attachPath = absolutePath;
            stickerUploader5.finalPath = absolutePath;
            this.stickerUploader.messageObject = new MessageObject(UserConfig.selectedAccount, (TLRPC.Message) tL_message, (MessageObject) null, false, false);
            this.stickerUploader.messageObject.videoEditedInfo = videoEditedInfo;
            MediaController.getInstance().scheduleVideoConvert(this.stickerUploader.messageObject, false, false, false);
        } else {
            FileLoader.getInstance(this.currentAccount).uploadFile(str2, false, true, 67108864);
        }
        if (callback == null) {
            showLoadingDialog();
        }
    }

    private void showLoadingDialog() {
        if (this.loadingToast == null) {
            this.loadingToast = new DownloadButton.PreparingVideoToast(getContext(), LocaleController.getString(R.string.PreparingSticker));
        }
        this.loadingToast.setOnCancelListener(new Runnable() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda10
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showLoadingDialog$13();
            }
        });
        if (this.loadingToast.getParent() == null) {
            addView(this.loadingToast, LayoutHelper.createFrame(-1, -1, 17));
        }
        this.loadingToast.show();
    }

    public void lambda$uploadMedia$15(final StickerUploader stickerUploader, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$uploadMedia$14(tLObject, stickerUploader, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$uploadMedia$14(TLObject tLObject, StickerUploader stickerUploader, TLRPC.TL_error tL_error) {
        if (tLObject instanceof TLRPC.TL_messageMediaDocument) {
            TLRPC.TL_messageMediaDocument tL_messageMediaDocument = (TLRPC.TL_messageMediaDocument) tLObject;
            stickerUploader.tlInputStickerSetItem = MediaDataController.getInputStickerSetItem(tL_messageMediaDocument.document, stickerUploader.emoji);
            stickerUploader.mediaDocument = tL_messageMediaDocument;
            afterUploadingMedia();
            return;
        }
        hideLoadingDialog();
        showError(tL_error);
    }

    private void showError(TLRPC.TL_error tL_error) {
        if (tL_error == null || "PACK_TITLE_INVALID".equals(tL_error.text)) {
            return;
        }
        BulletinFactory.of((FrameLayout) getParent(), this.resourcesProvider).createErrorBulletin(tL_error.text).show();
    }

    private void afterUploadingMedia() {
        final StickerUploader stickerUploader = this.stickerUploader;
        if (stickerUploader == null) {
            return;
        }
        final int i = UserConfig.selectedAccount;
        stickerUploader.uploaded = true;
        if (stickerUploader.customHandler != null) {
            hideLoadingDialog();
            stickerUploader.customHandler.run(stickerUploader.finalPath, stickerUploader.tlInputStickerSetItem.document);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda13
                @Override // java.lang.Runnable
                public final void run() {
                    NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationNameOnUIThread(NotificationCenter.customStickerCreated, new Object[0]);
                }
            }, 250L);
            return;
        }
        if (stickerUploader.replacedSticker != null) {
            TLRPC.TL_stickers_replaceSticker tL_stickers_replaceSticker = new TLRPC.TL_stickers_replaceSticker();
            tL_stickers_replaceSticker.sticker = MediaDataController.getInputStickerSetItem(stickerUploader.replacedSticker, stickerUploader.emoji).document;
            tL_stickers_replaceSticker.new_sticker = stickerUploader.tlInputStickerSetItem;
            ConnectionsManager.getInstance(i).sendRequest(tL_stickers_replaceSticker, new RequestDelegate() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda14
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$afterUploadingMedia$19(i, stickerUploader, tLObject, tL_error);
                }
            });
            return;
        }
        if (stickerUploader.stickerPackName != null) {
            TLRPC.TL_stickers_createStickerSet tL_stickers_createStickerSet = new TLRPC.TL_stickers_createStickerSet();
            tL_stickers_createStickerSet.user_id = new TLRPC.TL_inputUserSelf();
            tL_stickers_createStickerSet.title = stickerUploader.stickerPackName.toString();
            tL_stickers_createStickerSet.short_name = _UrlKt.FRAGMENT_ENCODE_SET;
            tL_stickers_createStickerSet.stickers.add(stickerUploader.tlInputStickerSetItem);
            ConnectionsManager.getInstance(i).sendRequest(tL_stickers_createStickerSet, new RequestDelegate() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda15
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$afterUploadingMedia$22(i, stickerUploader, tLObject, tL_error);
                }
            });
            return;
        }
        if (stickerUploader.addToFavorite) {
            hideLoadingDialog();
            NotificationCenter.getInstance(i).postNotificationNameOnUIThread(NotificationCenter.customStickerCreated, Boolean.FALSE);
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda16
                @Override // java.lang.Runnable
                public final void run() {
                    MediaDataController.getInstance(UserConfig.selectedAccount).addRecentSticker(2, null, stickerUploader.mediaDocument.document, (int) (System.currentTimeMillis() / 1000), false);
                }
            }, 350L);
            Utilities.Callback<Boolean> callback = stickerUploader.whenDone;
            if (callback != null) {
                callback.run(Boolean.TRUE);
                return;
            }
            return;
        }
        if (stickerUploader.sendToDialogId != 0) {
            SendMessagesHelper.getInstance(i).sendSticker(stickerUploader.mediaDocument.document, null, stickerUploader.sendToDialogId, null, null, null, null, null, true, 0, 0, false, null, null, 0, 0L, 0L, null);
            DownloadButton.PreparingVideoToast preparingVideoToast = this.loadingToast;
            if (preparingVideoToast != null) {
                preparingVideoToast.setProgress(1.0f);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda17
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$afterUploadingMedia$24(i);
                }
            }, 450L);
            Utilities.Callback<Boolean> callback2 = stickerUploader.whenDone;
            if (callback2 != null) {
                callback2.run(Boolean.TRUE);
                stickerUploader.whenDone = null;
                return;
            }
            return;
        }
        if (stickerUploader.stickerSet != null) {
            TLRPC.TL_stickers_addStickerToSet tL_stickers_addStickerToSet = new TLRPC.TL_stickers_addStickerToSet();
            tL_stickers_addStickerToSet.stickerset = MediaDataController.getInputStickerSet(stickerUploader.stickerSet);
            tL_stickers_addStickerToSet.sticker = stickerUploader.tlInputStickerSetItem;
            ConnectionsManager.getInstance(i).sendRequest(tL_stickers_addStickerToSet, new RequestDelegate() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda18
                @Override // org.telegram.tgnet.RequestDelegate
                public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                    this.f$0.lambda$afterUploadingMedia$27(i, stickerUploader, tLObject, tL_error);
                }
            });
        }
    }

    public /* synthetic */ void lambda$afterUploadingMedia$19(final int i, final StickerUploader stickerUploader, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda22
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$afterUploadingMedia$18(tLObject, i, stickerUploader, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$afterUploadingMedia$18(TLObject tLObject, int i, final StickerUploader stickerUploader, TLRPC.TL_error tL_error) {
        boolean z;
        final TLObject tLObject2;
        if (tLObject instanceof TLRPC.TL_messages_stickerSet) {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) tLObject;
            MediaDataController.getInstance(i).putStickerSet(tL_messages_stickerSet);
            if (MediaDataController.getInstance(i).isStickerPackInstalled(tL_messages_stickerSet.set.id)) {
                tLObject2 = tLObject;
            } else {
                tLObject2 = tLObject;
                MediaDataController.getInstance(i).toggleStickerSet(null, tLObject2, 2, null, false, false);
            }
            DownloadButton.PreparingVideoToast preparingVideoToast = this.loadingToast;
            if (preparingVideoToast != null) {
                preparingVideoToast.setProgress(1.0f);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda26
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$afterUploadingMedia$17(tLObject2, stickerUploader);
                }
            }, 450L);
            z = true;
        } else {
            showError(tL_error);
            hideLoadingDialog();
            z = false;
        }
        Utilities.Callback<Boolean> callback = stickerUploader.whenDone;
        if (callback != null) {
            callback.run(Boolean.valueOf(z));
            stickerUploader.whenDone = null;
        }
    }

    public /* synthetic */ void lambda$afterUploadingMedia$17(TLObject tLObject, StickerUploader stickerUploader) {
        NotificationCenter.getInstance(UserConfig.selectedAccount).postNotificationNameOnUIThread(NotificationCenter.customStickerCreated, Boolean.FALSE, tLObject, stickerUploader.mediaDocument.document, stickerUploader.thumbPath, Boolean.TRUE);
        hideLoadingDialog();
    }

    public /* synthetic */ void lambda$afterUploadingMedia$22(final int i, final StickerUploader stickerUploader, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda23
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$afterUploadingMedia$21(tLObject, i, stickerUploader, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$afterUploadingMedia$21(final TLObject tLObject, int i, final StickerUploader stickerUploader, TLRPC.TL_error tL_error) {
        boolean z;
        if (tLObject instanceof TLRPC.TL_messages_stickerSet) {
            MediaDataController.getInstance(i).putStickerSet((TLRPC.TL_messages_stickerSet) tLObject);
            MediaDataController.getInstance(i).toggleStickerSet(null, tLObject, 2, null, false, false);
            DownloadButton.PreparingVideoToast preparingVideoToast = this.loadingToast;
            if (preparingVideoToast != null) {
                preparingVideoToast.setProgress(1.0f);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda25
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$afterUploadingMedia$20(tLObject, stickerUploader);
                }
            }, 250L);
            z = true;
        } else {
            showError(tL_error);
            hideLoadingDialog();
            z = false;
        }
        Utilities.Callback<Boolean> callback = stickerUploader.whenDone;
        if (callback != null) {
            callback.run(Boolean.valueOf(z));
            stickerUploader.whenDone = null;
        }
    }

    public /* synthetic */ void lambda$afterUploadingMedia$20(TLObject tLObject, StickerUploader stickerUploader) {
        NotificationCenter notificationCenter = NotificationCenter.getInstance(UserConfig.selectedAccount);
        int i = NotificationCenter.customStickerCreated;
        Boolean bool = Boolean.FALSE;
        notificationCenter.postNotificationNameOnUIThread(i, bool, tLObject, stickerUploader.mediaDocument.document, stickerUploader.thumbPath, bool);
        hideLoadingDialog();
    }

    public /* synthetic */ void lambda$afterUploadingMedia$24(int i) {
        NotificationCenter.getInstance(i).lambda$postNotificationNameOnUIThread$1(NotificationCenter.customStickerCreated, Boolean.FALSE);
        hideLoadingDialog();
    }

    public /* synthetic */ void lambda$afterUploadingMedia$27(final int i, final StickerUploader stickerUploader, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda24
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$afterUploadingMedia$26(tLObject, i, stickerUploader, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$afterUploadingMedia$26(TLObject tLObject, int i, final StickerUploader stickerUploader, TLRPC.TL_error tL_error) {
        boolean z;
        final TLObject tLObject2;
        if (tLObject instanceof TLRPC.TL_messages_stickerSet) {
            TLRPC.TL_messages_stickerSet tL_messages_stickerSet = (TLRPC.TL_messages_stickerSet) tLObject;
            MediaDataController.getInstance(i).putStickerSet(tL_messages_stickerSet);
            if (MediaDataController.getInstance(i).isStickerPackInstalled(tL_messages_stickerSet.set.id)) {
                tLObject2 = tLObject;
            } else {
                tLObject2 = tLObject;
                MediaDataController.getInstance(i).toggleStickerSet(null, tLObject2, 2, null, false, false);
            }
            DownloadButton.PreparingVideoToast preparingVideoToast = this.loadingToast;
            if (preparingVideoToast != null) {
                preparingVideoToast.setProgress(1.0f);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Paint.Views.StickerMakerView$$ExternalSyntheticLambda27
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$afterUploadingMedia$25(tLObject2, stickerUploader);
                }
            }, 450L);
            z = true;
        } else {
            showError(tL_error);
            hideLoadingDialog();
            z = false;
        }
        Utilities.Callback<Boolean> callback = stickerUploader.whenDone;
        if (callback != null) {
            callback.run(Boolean.valueOf(z));
            stickerUploader.whenDone = null;
        }
    }

    public /* synthetic */ void lambda$afterUploadingMedia$25(TLObject tLObject, StickerUploader stickerUploader) {
        NotificationCenter notificationCenter = NotificationCenter.getInstance(UserConfig.selectedAccount);
        int i = NotificationCenter.customStickerCreated;
        Boolean bool = Boolean.FALSE;
        notificationCenter.postNotificationNameOnUIThread(i, bool, tLObject, stickerUploader.mediaDocument.document, stickerUploader.thumbPath, bool);
        hideLoadingDialog();
    }

    public static class StickerUploader {
        public boolean addToFavorite;
        public Utilities.Callback2<String, TLRPC.InputDocument> customHandler;
        public String emoji;
        public TLRPC.InputFile file;
        public String finalPath;
        public TLRPC.TL_messageMediaDocument mediaDocument;
        public MessageObject messageObject;
        public String path;
        public TLRPC.Document replacedSticker;
        public int reqId;
        public long sendToDialogId;
        public CharSequence stickerPackName;
        public TLRPC.StickerSet stickerSet;
        public String thumbPath;
        public TLRPC.TL_inputStickerSetItem tlInputStickerSetItem;
        public boolean uploaded;
        public TLRPC.Document uploadedSticker;
        public VideoEditedInfo videoEditedInfo;
        public Utilities.Callback<Boolean> whenDone;
        public ArrayList<File> finalFiles = new ArrayList<>();
        public ArrayList<File> files = new ArrayList<>();
        private float convertingProgress = 0.0f;
        private float uploadProgress = 0.0f;

        public float getProgress() {
            float f = this.customHandler == null ? 0.9f : 1.0f;
            if (this.videoEditedInfo == null) {
                return f * this.uploadProgress;
            }
            return f * ((this.convertingProgress * 0.5f) + (this.uploadProgress * 0.5f));
        }

        public void setupFiles() {
            if (!TextUtils.isEmpty(this.finalPath)) {
                this.finalFiles.add(new File(this.finalPath));
            }
            if (!TextUtils.isEmpty(this.path) && !TextUtils.equals(this.path, this.finalPath)) {
                this.files.add(new File(this.path));
            }
            if (TextUtils.isEmpty(this.thumbPath)) {
                return;
            }
            this.files.add(new File(this.thumbPath));
        }

        public void destroy(boolean z) {
            int i = 0;
            if (z) {
                ArrayList<File> arrayList = this.finalFiles;
                int size = arrayList.size();
                int i2 = 0;
                while (i2 < size) {
                    File file = arrayList.get(i2);
                    i2++;
                    try {
                        file.delete();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                }
            }
            this.finalFiles.clear();
            ArrayList<File> arrayList2 = this.files;
            int size2 = arrayList2.size();
            while (i < size2) {
                File file2 = arrayList2.get(i);
                i++;
                try {
                    file2.delete();
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
            }
            this.files.clear();
        }
    }

    public static class Point extends android.graphics.Point {
        public Point(int i, int i2, float f) {
            super((int) (i * f), (int) (i2 * f));
        }
    }
}
