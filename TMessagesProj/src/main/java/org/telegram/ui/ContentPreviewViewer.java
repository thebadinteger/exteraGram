package org.telegram.ui;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.badges.BadgesController;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.exteragram.messenger.utils.system.VibratorUtils;
import java.util.ArrayList;
import java.util.List;
import me.vkryl.core.reference.ReferenceList;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SvgHelper;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.ContextLinkCell;
import org.telegram.ui.Cells.StickerCell;
import org.telegram.ui.Cells.StickerEmojiCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmojiPacksAlert;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.PaintingOverlay;
import org.telegram.ui.Components.Reactions.CustomEmojiReactionsWindow;
import org.telegram.ui.Components.Reactions.ReactionsLayoutInBubble;
import org.telegram.ui.Components.ReactionsContainerLayout;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.ScrimOptions;
import org.telegram.ui.Components.StickersDialogs;
import org.telegram.ui.Components.SuggestEmojiView;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceBitmap;
import org.telegram.ui.Components.blur3.utils.Blur3Utils;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Components.poll.RecentVotersCell;
import org.telegram.ui.Stories.DarkThemeResourceProvider;

public class ContentPreviewViewer {

    @SuppressLint({"StaticFieldLeak"})
    private static volatile ContentPreviewViewer Instance;
    private static TextPaint textPaint;
    private ColorDrawable backgroundDrawable;
    private float blurProgress;
    private Bitmap blurrBitmap;
    public ImageReceiver centerImage;
    private boolean clearsInputField;
    private boolean closeOnDismiss;
    private FrameLayoutDrawer containerView;
    private int currentAccount;
    private int currentContentType;
    private TLRPC.Document currentDocument;
    private float currentMoveY;
    private float currentMoveYProgress;
    private View currentPreviewCell;
    private String currentQuery;
    private TLRPC.InputStickerSet currentStickerSet;
    private ContentPreviewViewerDelegate delegate;
    private boolean drawEffect;
    private ImageReceiver effectImage;
    private float finalMoveY;
    private SendMessagesHelper.ImportingSticker importingSticker;
    private TLRPC.BotInlineResult inlineResult;
    private boolean isPhotoEditor;
    private boolean isRecentSticker;
    private boolean isStickerEditor;
    private boolean isVisible;
    private int keyboardHeight;
    private WindowInsetsCompat lastInsets;
    private float lastTouchY;
    private long lastUpdateTime;
    private boolean menuVisible;
    private float moveY = 0.0f;
    private Runnable openPreviewRunnable;
    private final Paint paint;
    public PaintingOverlay paintingOverlay;
    private Path paintingOverlayClipPath;
    private Activity parentActivity;
    private Object parentObject;
    private View popupLayout;
    ActionBarPopupWindow popupWindow;
    private boolean preparingBitmap;
    private ReactionsContainerLayout reactionsLayout;
    private FrameLayout reactionsLayoutContainer;
    private Theme.ResourcesProvider resourcesProvider;
    private final BlurredBackgroundDrawableViewFactory scrimBlur3Factory;
    private final BlurredBackgroundSourceBitmap scrimBlur3SourceBitmap;
    private ArrayList<String> selectedEmojis;
    private float showProgress;
    private final Runnable showSheetRunnable;
    private Drawable slideUpDrawable;
    private float startMoveY;
    private int startX;
    private int startY;
    private StaticLayout stickerEmojiLayout;
    private TLRPC.TL_messages_stickerSet stickerSetForCustomSticker;
    private UnlockPremiumView unlockPremiumView;
    private VibrationEffect vibrationEffect;
    private WindowManager.LayoutParams windowLayoutParams;
    private FrameLayout windowView;

    public interface ContentPreviewViewerDelegate {
        default void addCaptionToGif(Object obj, Object obj2, boolean z, int i, int i2) {
        }

        default void addToFavoriteSelected(String str) {
        }

        default boolean can() {
            return true;
        }

        default boolean canAddCaption(TLRPC.Document document) {
            return false;
        }

        default boolean canDeleteSticker(TLRPC.Document document) {
            return false;
        }

        default boolean canEditSticker() {
            return false;
        }

        default boolean canSchedule() {
            return false;
        }

        default boolean canSendSticker() {
            return true;
        }

        default Boolean canSetAsStatus(TLRPC.Document document) {
            return null;
        }

        default void copyEmoji(TLRPC.Document document) {
        }

        default void deleteSticker(TLRPC.Document document) {
        }

        default void editSticker(TLRPC.Document document) {
        }

        default ItemOptions getCustomItemOptions(ViewGroup viewGroup, View view) {
            return null;
        }

        long getDialogId();

        default TLRPC.TL_messageMediaPoll getPoll() {
            return null;
        }

        default TLRPC.PollAnswer getPollAnswer() {
            return null;
        }

        default MessageObject getPollMessageObject() {
            return null;
        }

        default String getQuery(boolean z) {
            return null;
        }

        default void gifAddedOrDeleted() {
        }

        default boolean isInScheduleMode() {
            return false;
        }

        default boolean isPhotoEditor() {
            return false;
        }

        default boolean isReplacedSticker() {
            return false;
        }

        default boolean isSettingIntroSticker() {
            return false;
        }

        default boolean isStickerEditor() {
            return false;
        }

        default boolean needCopy(TLRPC.Document document) {
            return false;
        }

        default boolean needMenu() {
            return true;
        }

        default boolean needOpen() {
            return true;
        }

        default boolean needRemove() {
            return false;
        }

        default boolean needRemoveFromRecent(TLRPC.Document document) {
            return false;
        }

        default boolean needSend(int i) {
            return false;
        }

        default boolean needShowEmojiSet(TLRPC.Document document) {
            return false;
        }

        default void newStickerPackSelected(CharSequence charSequence, String str, Utilities.Callback<Boolean> callback) {
        }

        default void openSet(TLRPC.InputStickerSet inputStickerSet, boolean z) {
        }

        default void remove(SendMessagesHelper.ImportingSticker importingSticker) {
        }

        default void removeFromRecent(TLRPC.Document document) {
        }

        default void resetTouch() {
        }

        default void retractVote() {
        }

        default void sendEmoji(TLRPC.Document document) {
        }

        default void sendGif(Object obj, Object obj2, boolean z, int i, int i2) {
        }

        default void sendSticker(String str) {
        }

        default void sendSticker(TLRPC.Document document, String str, Object obj, boolean z, int i, int i2) {
        }

        default void sendVote() {
        }

        default void setAsBadge(TLRPC.Document document) {
        }

        default void setAsEmojiStatus(TLRPC.Document document, Integer num) {
        }

        default void setIntroSticker(String str) {
        }

        default void showEmojiSet(TLRPC.Document document) {
        }

        default void stickerSetSelected(TLRPC.StickerSet stickerSet, String str) {
        }
    }

    public ContentPreviewViewer() {
        BlurredBackgroundSourceBitmap blurredBackgroundSourceBitmap = new BlurredBackgroundSourceBitmap();
        this.scrimBlur3SourceBitmap = blurredBackgroundSourceBitmap;
        this.scrimBlur3Factory = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSourceBitmap);
        this.backgroundDrawable = new ColorDrawable(1895825408);
        this.centerImage = new ImageReceiver();
        this.effectImage = new ImageReceiver();
        this.isVisible = false;
        this.keyboardHeight = AndroidUtilities.dp(200.0f);
        this.paint = new Paint(1);
        this.showSheetRunnable = new AnonymousClass1();
    }

    public class FrameLayoutDrawer extends FrameLayout {
        public FrameLayoutDrawer(Context context) {
            super(context);
            setWillNotDraw(false);
        }

        @Override // android.view.View
        public void onDraw(Canvas canvas) {
            ContentPreviewViewer.this.onDraw(canvas);
        }

        @Override // android.view.ViewGroup
        public boolean drawChild(Canvas canvas, View view, long j) {
            if (view instanceof PaintingOverlay) {
                return false;
            }
            return super.drawChild(canvas, view, j);
        }
    }

    public boolean canShowFullVotersList() {
        ContentPreviewViewerDelegate contentPreviewViewerDelegate = this.delegate;
        if (contentPreviewViewerDelegate == null) {
            return false;
        }
        TLRPC.TL_messageMediaPoll poll = contentPreviewViewerDelegate.getPoll();
        TLRPC.PollAnswer pollAnswer = this.delegate.getPollAnswer();
        if (poll == null || poll.poll == null || pollAnswer == null) {
            return false;
        }
        TLRPC.PollAnswerVoters pollResult = MessageObject.getPollResult(poll, pollAnswer.option);
        if (pollResult == null || pollResult.voters <= 0) {
            return true;
        }
        MessageObject.canShowVotersList(poll);
        return true;
    }

    public void lambda$addVoteOptions$3(View view) {
        ContentPreviewViewerDelegate contentPreviewViewerDelegate = this.delegate;
        if (contentPreviewViewerDelegate != null) {
            contentPreviewViewerDelegate.sendVote();
        }
        dismissPopupWindow();
    }

    public void lambda$run$3(CharSequence charSequence, final Utilities.Callback callback) {
            if (ContentPreviewViewer.this.delegate != null) {
                ContentPreviewViewer.this.delegate.newStickerPackSelected(charSequence, TextUtils.join(_UrlKt.FRAGMENT_ENCODE_SET, ContentPreviewViewer.this.selectedEmojis), callback != null ? new Utilities.Callback() { // from class: org.telegram.ui.ContentPreviewViewer$1$$ExternalSyntheticLambda13
                    @Override 
                    public final void run(Object obj) {
                        this.f$0.lambda$run$2(callback, (Boolean) obj);
                    }
                } : null);
                if (callback == null) {
                    ContentPreviewViewer.this.dismissPopupWindow();
                }
            }
        }

        public void lambda$run$8(ArrayList arrayList, boolean z, View view) {
            if (ContentPreviewViewer.this.parentActivity == null || ContentPreviewViewer.this.delegate == null) {
                return;
            }
            int iIntValue = ((Integer) view.getTag()).intValue();
            int iIntValue2 = ((Integer) arrayList.get(iIntValue)).intValue();
            if (iIntValue2 == 0) {
                ContentPreviewViewer.this.delegate.sendEmoji(ContentPreviewViewer.this.currentDocument);
            } else if (iIntValue2 == 1) {
                ContentPreviewViewer.this.delegate.setAsEmojiStatus(ContentPreviewViewer.this.currentDocument, null);
            } else if (iIntValue2 == 2) {
                ContentPreviewViewer.this.delegate.setAsEmojiStatus(null, null);
            } else if (iIntValue2 == 77) {
                ContentPreviewViewer.this.delegate.setAsBadge(ContentPreviewViewer.this.currentDocument);
            } else if (iIntValue2 == 3) {
                ContentPreviewViewer.this.delegate.copyEmoji(ContentPreviewViewer.this.currentDocument);
            } else if (iIntValue2 == 11) {
                ContentPreviewViewer.this.delegate.showEmojiSet(ContentPreviewViewer.this.currentDocument);
            } else if (iIntValue2 == 4) {
                ContentPreviewViewer.this.delegate.removeFromRecent(ContentPreviewViewer.this.currentDocument);
            } else if (iIntValue2 == 5) {
                MediaDataController.getInstance(ContentPreviewViewer.this.currentAccount).addRecentSticker(2, ContentPreviewViewer.this.parentObject, ContentPreviewViewer.this.currentDocument, (int) (System.currentTimeMillis() / 1000), z);
            } else if (((Integer) arrayList.get(iIntValue)).intValue() == 10) {
                ChatUtils.getInstance().saveStickerToGallery(ContentPreviewViewer.this.parentActivity, ContentPreviewViewer.this.currentDocument, new Utilities.Callback() { // from class: org.telegram.ui.ContentPreviewViewer$1$$ExternalSyntheticLambda10
                    @Override 
                    public final void run(Object obj) {
                        NotificationCenter.getGlobalInstance().lambda$postNotificationNameOnUIThread$1(NotificationCenter.showBulletin, 11);
                    }
                });
            }
            ContentPreviewViewer.this.dismissPopupWindow();
        }

        public void m14259$r8$lambda$xZ6OpnvmIxb3UCjxjQ2ZX130uQ(ContentPreviewViewerDelegate contentPreviewViewerDelegate, TLRPC.Document document, TLRPC.BotInlineResult botInlineResult, Object obj, boolean z, int i, int i2) {
            Object obj2 = document;
            if (document == null) {
                obj2 = botInlineResult;
            }
            contentPreviewViewerDelegate.sendGif(obj2, obj, z, i, i2);
        }

        public void lambda$prepareBlurBitmap$15(Bitmap bitmap, Bitmap bitmap2) {
        this.centerImage.setVisible(true, false);
        this.blurrBitmap = bitmap;
        Shader.TileMode tileMode = Shader.TileMode.CLAMP;
        BitmapShader bitmapShader = new BitmapShader(bitmap, tileMode, tileMode);
        Matrix matrix = new Matrix();
        matrix.setScale(15.0f, 15.0f);
        bitmapShader.setLocalMatrix(matrix);
        if (Build.VERSION.SDK_INT >= 33) {
            bitmapShader.setFilterMode(2);
        }
        this.paint.setFilterBitmap(true);
        this.paint.setShader(bitmapShader);
        this.scrimBlur3SourceBitmap.setBitmap(bitmap2);
        Blur3Utils.checkBitmapSourceMatrixScale(this.scrimBlur3SourceBitmap, this.windowView);
        this.scrimBlur3Factory.invalidateAllLinkedViews();
        this.preparingBitmap = false;
        FrameLayoutDrawer frameLayoutDrawer = this.containerView;
        if (frameLayoutDrawer != null) {
            frameLayoutDrawer.invalidate();
        }
    }

    public boolean showMenuFor(View view) {
        if (!(view instanceof StickerEmojiCell)) {
            return false;
        }
        Activity activityFindActivity = AndroidUtilities.findActivity(view.getContext());
        if (activityFindActivity == null) {
            return true;
        }
        setParentActivity(activityFindActivity);
        StickerEmojiCell stickerEmojiCell = (StickerEmojiCell) view;
        View view2 = this.currentPreviewCell;
        if (view2 instanceof StickerEmojiCell) {
            ((StickerEmojiCell) view2).setScaled(false);
        } else if (view2 instanceof StickerCell) {
            ((StickerCell) view2).setScaled(false);
        } else if (view2 instanceof ContextLinkCell) {
            ((ContextLinkCell) view2).setScaled(false);
        }
        this.currentPreviewCell = stickerEmojiCell;
        TLRPC.Document sticker = stickerEmojiCell.getSticker();
        SendMessagesHelper.ImportingSticker stickerPath = stickerEmojiCell.getStickerPath();
        String strFindAnimatedEmojiEmoticon = MessageObject.findAnimatedEmojiEmoticon(stickerEmojiCell.getSticker(), null, Integer.valueOf(this.currentAccount));
        ContentPreviewViewerDelegate contentPreviewViewerDelegate = this.delegate;
        open(sticker, stickerPath, strFindAnimatedEmojiEmoticon, contentPreviewViewerDelegate != null ? contentPreviewViewerDelegate.getQuery(false) : null, null, 0, stickerEmojiCell.isRecent(), stickerEmojiCell.getParentObject(), this.resourcesProvider);
        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 16L);
        stickerEmojiCell.setScaled(true);
        return true;
    }

    public void showCustomStickerActions(String str, VideoEditedInfo videoEditedInfo, View view, ArrayList<String> arrayList, ContentPreviewViewerDelegate contentPreviewViewerDelegate) {
        Activity activityFindActivity = AndroidUtilities.findActivity(view.getContext());
        if (activityFindActivity == null) {
            return;
        }
        setParentActivity(activityFindActivity);
        setDelegate(contentPreviewViewerDelegate);
        SendMessagesHelper.ImportingSticker importingSticker = new SendMessagesHelper.ImportingSticker();
        importingSticker.path = str;
        importingSticker.videoEditedInfo = videoEditedInfo;
        this.selectedEmojis = arrayList;
        open(null, importingSticker, null, null, null, 3, false, null, new DarkThemeResourceProvider());
        AndroidUtilities.cancelRunOnUIThread(this.showSheetRunnable);
        AndroidUtilities.runOnUIThread(this.showSheetRunnable, 16L);
    }

    private void getMyStickersRemote(final TLRPC.TL_messages_getMyStickers tL_messages_getMyStickers, final List<TLRPC.StickerSetCovered> list) {
        ConnectionsManager.getInstance(this.currentAccount).sendRequest(tL_messages_getMyStickers, new RequestDelegate() { // from class: org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda10
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                this.f$0.lambda$getMyStickersRemote$17(list, tL_messages_getMyStickers, tLObject, tL_error);
            }
        });
    }

    public /* synthetic */ void lambda$getMyStickersRemote$17(final List list, final TLRPC.TL_messages_getMyStickers tL_messages_getMyStickers, final TLObject tLObject, final TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.ContentPreviewViewer$$ExternalSyntheticLambda12
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$getMyStickersRemote$16(tL_error, tLObject, list, tL_messages_getMyStickers);
            }
        });
    }

    public /* synthetic */ void lambda$getMyStickersRemote$16(TLRPC.TL_error tL_error, TLObject tLObject, List list, TLRPC.TL_messages_getMyStickers tL_messages_getMyStickers) {
        if (tL_error == null && (tLObject instanceof TLRPC.TL_messages_myStickers)) {
            TLRPC.TL_messages_myStickers tL_messages_myStickers = (TLRPC.TL_messages_myStickers) tLObject;
            ArrayList<TLRPC.StickerSetCovered> arrayList = tL_messages_myStickers.sets;
            int size = arrayList.size();
            int i = 0;
            while (i < size) {
                TLRPC.StickerSetCovered stickerSetCovered = arrayList.get(i);
                i++;
                TLRPC.StickerSetCovered stickerSetCovered2 = stickerSetCovered;
                TLRPC.StickerSet stickerSet = stickerSetCovered2.set;
                if (!stickerSet.emojis && !stickerSet.masks) {
                    TLRPC.TL_inputStickerSetID tL_inputStickerSetID = new TLRPC.TL_inputStickerSetID();
                    tL_inputStickerSetID.id = stickerSetCovered2.set.id;
                    TLRPC.TL_messages_stickerSet stickerSet2 = MediaDataController.getInstance(this.currentAccount).getStickerSet((TLRPC.InputStickerSet) tL_inputStickerSetID, true);
                    if (stickerSet2 == null || stickerSet2.documents.size() < 120) {
                        list.add(stickerSetCovered2);
                    }
                }
            }
            if (tL_messages_myStickers.sets.size() == tL_messages_getMyStickers.limit) {
                ArrayList<TLRPC.StickerSetCovered> arrayList2 = tL_messages_myStickers.sets;
                tL_messages_getMyStickers.offset_id = arrayList2.get(arrayList2.size() - 1).set.id;
                getMyStickersRemote(tL_messages_getMyStickers, list);
            }
        }
    }

    public RecyclerListView createMyStickerPacksListView() {
        if (this.parentActivity == null) {
            return null;
        }
        final ArrayList arrayList = new ArrayList();
        arrayList.add(new TLRPC.TL_stickerSetNoCovered());
        TLRPC.TL_messages_getMyStickers tL_messages_getMyStickers = new TLRPC.TL_messages_getMyStickers();
        tL_messages_getMyStickers.limit = 100;
        getMyStickersRemote(tL_messages_getMyStickers, arrayList);
        RecyclerListView recyclerListView = new RecyclerListView(this.parentActivity) { // from class: org.telegram.ui.ContentPreviewViewer.5
            @Override // org.telegram.ui.Components.RecyclerListView, androidx.recyclerview.widget.RecyclerView, android.view.View
            public void onMeasure(int i, int i2) {
                int size = View.MeasureSpec.getSize(i2);
                int iDp = AndroidUtilities.dp(4.0f) + (AndroidUtilities.dp(50.0f) * getAdapter().getItemCount());
                if (iDp <= size) {
                    size = iDp;
                }
                super.onMeasure(i, View.MeasureSpec.makeMeasureSpec(size, TLObject.FLAG_30));
            }
        };
        recyclerListView.setLayoutManager(new LinearLayoutManager(this.parentActivity));
        recyclerListView.addItemDecoration(new RecyclerView.ItemDecoration() { // from class: org.telegram.ui.ContentPreviewViewer.6
            @Override // androidx.recyclerview.widget.RecyclerView.ItemDecoration
            public void getItemOffsets(Rect rect, View view, RecyclerView recyclerView, RecyclerView.State state) {
                if (recyclerView.getChildAdapterPosition(view) == arrayList.size() - 1) {
                    rect.bottom = AndroidUtilities.dp(4.0f);
                }
            }
        });
        recyclerListView.setAdapter(new RecyclerListView.SelectionAdapter() { // from class: org.telegram.ui.ContentPreviewViewer.7
            @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
            public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
                return true;
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
                StickerPackNameView stickerPackNameView = new StickerPackNameView(viewGroup.getContext(), ContentPreviewViewer.this.resourcesProvider);
                stickerPackNameView.setLayoutParams(new RecyclerView.LayoutParams(-2, AndroidUtilities.dp(48.0f)));
                return new RecyclerListView.Holder(stickerPackNameView);
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
                ((StickerPackNameView) viewHolder.itemView).bind((TLRPC.StickerSetCovered) arrayList.get(i));
            }

            @Override // androidx.recyclerview.widget.RecyclerView.Adapter
            public int getItemCount() {
                return arrayList.size();
            }
        });
        return recyclerListView;
    }

    public static class StickerPackNameView extends LinearLayout {
        private TLRPC.StickerSetCovered cover;
        private final BackupImageView imageView;
        private final Theme.ResourcesProvider resourcesProvider;
        private final SimpleTextView textView;

        public StickerPackNameView(Context context, Theme.ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
            BackupImageView backupImageView = new BackupImageView(context);
            this.imageView = backupImageView;
            SimpleTextView simpleTextView = new SimpleTextView(context);
            this.textView = simpleTextView;
            simpleTextView.setTextSize(16);
            simpleTextView.setTextColor(-1);
            setOrientation(0);
            addView(backupImageView, LayoutHelper.createLinear(24, 24, 17, 17, 0, 17, 0));
            addView(simpleTextView, LayoutHelper.createLinear(-2, -2, 17, 0, 0, 12, 0));
        }

        public TLRPC.StickerSetCovered getCover() {
            return this.cover;
        }

        public void bind(TLRPC.StickerSetCovered stickerSetCovered) {
            this.cover = stickerSetCovered;
            boolean z = stickerSetCovered instanceof TLRPC.TL_stickerSetNoCovered;
            SimpleTextView simpleTextView = this.textView;
            if (z) {
                simpleTextView.setText(LocaleController.getString(R.string.NewStickerPack));
                this.imageView.setImageResource(R.drawable.msg_addbot);
                return;
            }
            simpleTextView.setText(stickerSetCovered.set.title);
            TLRPC.Document document = stickerSetCovered.cover;
            if (document != null) {
                TLRPC.PhotoSize closestPhotoSizeWithSize = FileLoader.getClosestPhotoSizeWithSize(document.thumbs, 90);
                SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(stickerSetCovered.cover, Theme.key_windowBackgroundGray, 1.0f, 1.0f, this.resourcesProvider);
                if (svgThumb != null) {
                    BackupImageView backupImageView = this.imageView;
                    if (closestPhotoSizeWithSize != null) {
                        backupImageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize, stickerSetCovered.cover), (String) null, "webp", svgThumb, stickerSetCovered);
                        return;
                    } else {
                        backupImageView.setImage(ImageLocation.getForDocument(stickerSetCovered.cover), (String) null, "webp", svgThumb, stickerSetCovered);
                        return;
                    }
                }
                this.imageView.setImage(ImageLocation.getForDocument(closestPhotoSizeWithSize, stickerSetCovered.cover), (String) null, "webp", (Drawable) null, stickerSetCovered);
                return;
            }
            this.imageView.setImage((ImageLocation) null, (String) null, (ImageLocation) null, (String) null, (Drawable) null, (Object) 0);
        }
    }

    public void dismissPopupWindow() {
        ActionBarPopupWindow actionBarPopupWindow = this.popupWindow;
        if (actionBarPopupWindow != null) {
            actionBarPopupWindow.dismiss();
            this.popupWindow = null;
            return;
        }
        View view = this.popupLayout;
        if (view != null) {
            view.animate().alpha(0.0f).scaleX(0.8f).scaleY(0.8f).translationY(AndroidUtilities.dp(-12.0f)).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).setDuration(320L).start();
            this.popupLayout = null;
            this.menuVisible = false;
            if (this.closeOnDismiss) {
                close();
            }
        }
    }
}
