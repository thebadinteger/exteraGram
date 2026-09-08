package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.LongSparseArray;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.badges.BadgesController;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.exteragram.messenger.utils.system.VibratorUtils;
import com.exteragram.messenger.utils.text.LocaleUtils;
import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DocumentObject;
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
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.Vector;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.ActionBarPopupWindow;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.Premium.PremiumButtonView;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.ContentPreviewViewer;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.ProfileActivity;

public class EmojiPacksAlert extends BottomSheet implements NotificationCenter.NotificationCenterDelegate {
    private static Pattern urlPattern;
    private Adapter adapter;
    private int adaptiveEmojiColor;
    private ColorFilter adaptiveEmojiColorFilter;
    private TextView addButtonView;
    private LongSparseArray<AnimatedEmojiDrawable> animatedEmojiDrawables;
    private FrameLayout buttonsView;
    private Runnable cancelSearchCreatorRunnable;
    private ContentView contentView;
    private EmojiPacksLoader customEmojiPacks;
    private BaseFragment fragment;
    private Float fromY;
    private GridLayoutManager gridLayoutManager;
    private boolean hasDescription;
    private AnimatedFloat highlightAlpha;
    int highlightEndPosition;
    private int highlightIndex;
    int highlightStartPosition;
    private float lastY;
    private boolean limitCount;
    private RecyclerListView listView;
    private ValueAnimator loadAnimator;
    private float loadT;
    boolean loaded;
    private View paddingView;
    private ActionBarPopupWindow popupWindow;
    private long premiumButtonClicked;
    private PremiumButtonView premiumButtonView;
    private ContentPreviewViewer.ContentPreviewViewerDelegate previewDelegate;
    private CircularProgressDrawable progressDrawable;
    private TextView removeButtonView;
    private RecyclerAnimationScrollHelper scrollHelper;
    private View shadowView;
    private boolean shown;

    @Override // org.telegram.ui.ActionBar.BottomSheet
    public boolean canDismissWithSwipe() {
        return false;
    }

    public void onButtonClicked(boolean z) {
    }

    public void onCloseByLink() {
    }

    public class AnonymousClass1 implements ContentPreviewViewer.ContentPreviewViewerDelegate {
        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public boolean can() {
            return true;
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public boolean canSchedule() {
            return false;
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public long getDialogId() {
            return 0L;
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public void openSet(TLRPC.InputStickerSet inputStickerSet, boolean z) {
        }

        public AnonymousClass1() {
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public boolean needSend(int i) {
            if (!(EmojiPacksAlert.this.fragment instanceof ChatActivity) || !((ChatActivity) EmojiPacksAlert.this.fragment).canSendMessage()) {
                return false;
            }
            if (UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
                return true;
            }
            return (((ChatActivity) EmojiPacksAlert.this.fragment).getCurrentUser() != null && UserObject.isUserSelf(((ChatActivity) EmojiPacksAlert.this.fragment).getCurrentUser())) || LocaleUtils.canUseLocalPremiumEmojis();
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public void sendEmoji(TLRPC.Document document) {
            if (EmojiPacksAlert.this.fragment instanceof ChatActivity) {
                ((ChatActivity) EmojiPacksAlert.this.fragment).sendAnimatedEmoji(document, true, 0);
            }
            EmojiPacksAlert.this.onCloseByLink();
            EmojiPacksAlert.this.lambda$new$0();
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public boolean needCopy(TLRPC.Document document) {
            return (UserConfig.getInstance(UserConfig.selectedAccount).isPremium() || LocaleUtils.canUseLocalPremiumEmojis()) && MessageObject.isAnimatedEmoji(document);
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public void copyEmoji(TLRPC.Document document) {
            SpannableStringBuilder spannableStringBuilderValueOf = SpannableStringBuilder.valueOf(MessageObject.findAnimatedEmojiEmoticon(document));
            spannableStringBuilderValueOf.setSpan(new AnimatedEmojiSpan(document, (Paint.FontMetricsInt) null), 0, spannableStringBuilderValueOf.length(), 33);
            if (AndroidUtilities.addToClipboard(spannableStringBuilderValueOf)) {
                BulletinFactory.of((FrameLayout) ((BottomSheet) EmojiPacksAlert.this).containerView, ((BottomSheet) EmojiPacksAlert.this).resourcesProvider).createCopyBulletin(LocaleController.getString(R.string.EmojiCopied)).show();
            }
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public Boolean canSetAsStatus(TLRPC.Document document) {
            TLRPC.User currentUser;
            if (!UserConfig.getInstance(UserConfig.selectedAccount).isPremium() || !MessageObject.isAnimatedEmoji(document) || (currentUser = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser()) == null) {
                return null;
            }
            Long emojiStatusDocumentId = UserObject.getEmojiStatusDocumentId(currentUser);
            return Boolean.valueOf(document != null && (emojiStatusDocumentId == null || emojiStatusDocumentId.longValue() != document.id));
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public void setAsEmojiStatus(TLRPC.Document document, Integer num) {
            TLRPC.EmojiStatus tL_emojiStatusEmpty;
            if (document == null) {
                tL_emojiStatusEmpty = new TLRPC.TL_emojiStatusEmpty();
            } else {
                TLRPC.TL_emojiStatus tL_emojiStatus = new TLRPC.TL_emojiStatus();
                tL_emojiStatus.document_id = document.id;
                if (num != null) {
                    tL_emojiStatus.flags |= 1;
                    tL_emojiStatus.until = num.intValue();
                }
                tL_emojiStatusEmpty = tL_emojiStatus;
            }
            TLRPC.User currentUser = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser();
            final TLRPC.EmojiStatus tL_emojiStatusEmpty2 = currentUser == null ? new TLRPC.TL_emojiStatusEmpty() : currentUser.emoji_status;
            MessagesController.getInstance(((BottomSheet) EmojiPacksAlert.this).currentAccount).updateEmojiStatus(tL_emojiStatusEmpty);
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setAsEmojiStatus$0(tL_emojiStatusEmpty2);
                }
            };
            if (document != null) {
                BulletinFactory.of((FrameLayout) ((BottomSheet) EmojiPacksAlert.this).containerView, ((BottomSheet) EmojiPacksAlert.this).resourcesProvider).createEmojiBulletin(document, LocaleController.getString(R.string.SetAsEmojiStatusInfo), LocaleController.getString(R.string.UndoNoCaps), runnable).show();
                return;
            }
            Bulletin.SimpleLayout simpleLayout = new Bulletin.SimpleLayout(EmojiPacksAlert.this.getContext(), ((BottomSheet) EmojiPacksAlert.this).resourcesProvider);
            simpleLayout.textView.setText(LocaleController.getString(R.string.RemoveStatusInfo));
            simpleLayout.imageView.setImageResource(R.drawable.msg_settings_premium);
            Bulletin.UndoButton undoButton = new Bulletin.UndoButton(EmojiPacksAlert.this.getContext(), true, ((BottomSheet) EmojiPacksAlert.this).resourcesProvider);
            undoButton.setUndoAction(runnable);
            simpleLayout.setButton(undoButton);
            Bulletin.make((FrameLayout) ((BottomSheet) EmojiPacksAlert.this).containerView, simpleLayout, 1500).show();
        }

        public boolean lambda$new$2(Context context, View view, int i) {
        final AnimatedEmojiSpan animatedEmojiSpan;
        if (!(view instanceof EmojiImageView) || (animatedEmojiSpan = ((EmojiImageView) view).span) == null) {
            return false;
        }
        ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), true, true);
        actionBarMenuSubItem.setItemHeight(48);
        actionBarMenuSubItem.setPadding(AndroidUtilities.dp(26.0f), 0, AndroidUtilities.dp(26.0f), 0);
        actionBarMenuSubItem.setText(LocaleController.getString(R.string.Copy));
        actionBarMenuSubItem.getTextView().setTextSize(1, 14.4f);
        actionBarMenuSubItem.getTextView().setTypeface(AndroidUtilities.bold());
        actionBarMenuSubItem.setOnClickListener(new View.OnClickListener() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda6
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                this.f$0.lambda$new$1(animatedEmojiSpan, view2);
            }
        });
        LinearLayout linearLayout = new LinearLayout(context);
        Drawable drawableMutate = ContextCompat.getDrawable(getContext(), R.drawable.popup_fixed_alert).mutate();
        drawableMutate.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_actionBarDefaultSubmenuBackground), PorterDuff.Mode.MULTIPLY));
        linearLayout.setBackground(drawableMutate);
        linearLayout.addView(actionBarMenuSubItem);
        ActionBarPopupWindow actionBarPopupWindow = new ActionBarPopupWindow(linearLayout, -2, -2);
        this.popupWindow = actionBarPopupWindow;
        actionBarPopupWindow.setClippingEnabled(true);
        this.popupWindow.setLayoutInScreen(true);
        this.popupWindow.setInputMethodMode(2);
        this.popupWindow.setSoftInputMode(0);
        this.popupWindow.setOutsideTouchable(true);
        this.popupWindow.setAnimationStyle(R.style.PopupAnimation);
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        this.popupWindow.showAtLocation(view, 51, (iArr[0] - AndroidUtilities.dp(49.0f)) + (view.getMeasuredWidth() / 2), iArr[1] - AndroidUtilities.dp(52.0f));
        try {
            view.performHapticFeedback(VibratorUtils.getType(0), 1);
        } catch (Exception unused) {
        }
        return true;
    }

    public void m10360$r8$lambda$Mi5pnaOTVrNYesse_DeFfyypO0(TLRPC.StickerSet stickerSet, TLRPC.TL_error tL_error, boolean z, View view, BaseFragment baseFragment, TLRPC.TL_messages_stickerSet tL_messages_stickerSet, TLObject tLObject, int i, Utilities.Callback callback, final Runnable runnable) {
        int i2;
        if (stickerSet.masks) {
            i2 = 1;
        } else {
            i2 = stickerSet.emojis ? 5 : 0;
        }
        try {
            if (tL_error == null) {
                if (z && view != null) {
                    Bulletin.make(baseFragment, new StickerSetBulletinLayout(baseFragment.getFragmentView().getContext(), tL_messages_stickerSet == null ? stickerSet : tL_messages_stickerSet, 2, null, baseFragment.getResourceProvider()), 1500).show();
                }
                if (tLObject instanceof TLRPC.TL_messages_stickerSetInstallResultArchive) {
                    MediaDataController.getInstance(i).processStickerSetInstallResultArchive(baseFragment, true, i2, (TLRPC.TL_messages_stickerSetInstallResultArchive) tLObject);
                }
                if (callback != null) {
                    callback.run(Boolean.TRUE);
                }
            } else if (view != null) {
                Toast.makeText(baseFragment.getFragmentView().getContext(), LocaleController.getString(R.string.ErrorOccurred), 0).show();
                if (callback != null) {
                    callback.run(Boolean.FALSE);
                }
            } else if (callback != null) {
                callback.run(Boolean.FALSE);
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        MediaDataController.getInstance(i).loadStickers(i2, false, true, false, new Utilities.Callback() { // from class: org.telegram.ui.Components.EmojiPacksAlert$$ExternalSyntheticLambda14
            @Override 
            public final void run(Object obj) {
                EmojiPacksAlert.$r8$lambda$1fvAOqrJS106LZQA04xLvrvva0g(runnable, (ArrayList) obj);
            }
        });
    }

    public static void lambda$updateButton$9(int[] iArr, int i, ArrayList arrayList, Boolean bool) {
        iArr[0] = iArr[0] + 1;
        if (bool.booleanValue()) {
            iArr[1] = iArr[1] + 1;
        }
        if (iArr[0] != i || iArr[1] <= 0) {
            return;
        }
        lambda$new$0();
        Bulletin.make(this.fragment, new StickerSetBulletinLayout(this.fragment.getFragmentView().getContext(), (TLObject) arrayList.get(0), iArr[1], 2, null, this.fragment.getResourceProvider()), 1500).show();
    }

    public void lambda$onSend$0(androidx.collection.LongSparseArray longSparseArray, int i) {
            UndoView undoView;
            boolean z = EmojiPacksAlert.this.fragment instanceof ChatActivity;
            EmojiPacksAlert emojiPacksAlert = EmojiPacksAlert.this;
            if (z) {
                undoView = ((ChatActivity) emojiPacksAlert.fragment).getUndoView();
            } else {
                undoView = emojiPacksAlert.fragment instanceof ProfileActivity ? ((ProfileActivity) EmojiPacksAlert.this.fragment).getUndoView() : null;
            }
            UndoView undoView2 = undoView;
            if (undoView2 != null) {
                if (longSparseArray.size() == 1) {
                    undoView2.showWithAction(((TLRPC.Dialog) longSparseArray.valueAt(0)).id, 53, Integer.valueOf(i));
                } else {
                    undoView2.showWithAction(0L, 53, Integer.valueOf(i), Integer.valueOf(longSparseArray.size()), (Runnable) null, (Runnable) null);
                }
            }
        }
    }

    public void lambda$init$1(final TLObject tLObject, final TLRPC.TL_error tL_error) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPacksLoader$$ExternalSyntheticLambda3
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$init$0(tL_error, tLObject);
                }
            });
        }

        public /* synthetic */ void lambda$init$0(TLRPC.TL_error tL_error, TLObject tLObject) {
            TLRPC.StickerSet stickerSet;
            if (tL_error != null || !(tLObject instanceof Vector)) {
                EmojiPacksAlert.this.lambda$new$0();
                if (EmojiPacksAlert.this.fragment == null || EmojiPacksAlert.this.fragment.getParentActivity() == null) {
                    return;
                }
                BulletinFactory.of(EmojiPacksAlert.this.fragment).createErrorBulletin(LocaleController.getString(R.string.UnknownError)).show();
                return;
            }
            Vector vector = (Vector) tLObject;
            if (this.inputStickerSets == null) {
                this.inputStickerSets = new ArrayList<>();
            }
            for (int i = 0; i < vector.objects.size(); i++) {
                Object obj = vector.objects.get(i);
                if ((obj instanceof TLRPC.StickerSetCovered) && (stickerSet = ((TLRPC.StickerSetCovered) obj).set) != null) {
                    this.inputStickerSets.add(MediaDataController.getInputStickerSet(stickerSet));
                }
            }
            this.parentObject = null;
            init();
        }

        public /* synthetic */ void lambda$init$3(boolean[] zArr, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            if (tL_messages_stickerSet != null || zArr[0]) {
                return;
            }
            zArr[0] = true;
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.EmojiPacksAlert$EmojiPacksLoader$$ExternalSyntheticLambda4
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$init$2();
                }
            });
        }

        public /* synthetic */ void lambda$init$2() {
            EmojiPacksAlert.this.lambda$new$0();
            if (EmojiPacksAlert.this.fragment == null || EmojiPacksAlert.this.fragment.getParentActivity() == null) {
                return;
            }
            BulletinFactory.of(EmojiPacksAlert.this.fragment).createErrorBulletin(LocaleController.getString(R.string.AddEmojiNotFound)).show();
        }

        public /* synthetic */ void lambda$init$4() {
            EmojiPacksAlert.this.lambda$new$0();
        }

        @Override 
        public void didReceivedNotification(int i, int i2, Object... objArr) {
            TLRPC.StickerSet stickerSet;
            if (i == NotificationCenter.groupStickersDidLoad) {
                for (int i3 = 0; i3 < this.stickerSets.size(); i3++) {
                    if (this.stickerSets.get(i3) == null) {
                        TLRPC.TL_messages_stickerSet stickerSet2 = MediaDataController.getInstance(this.currentAccount).getStickerSet(this.inputStickerSets.get(i3), true);
                        if (this.stickerSets.size() == 1 && stickerSet2 != null && (stickerSet = stickerSet2.set) != null && !stickerSet.emojis) {
                            EmojiPacksAlert.this.lambda$new$0();
                            new StickersAlert(EmojiPacksAlert.this.getContext(), EmojiPacksAlert.this.fragment, this.inputStickerSets.get(i3), null, EmojiPacksAlert.this.fragment instanceof ChatActivity ? ((ChatActivity) EmojiPacksAlert.this.fragment).getChatActivityEnterView() : null, ((BottomSheet) EmojiPacksAlert.this).resourcesProvider, false).show();
                            return;
                        } else {
                            this.stickerSets.set(i3, stickerSet2);
                            if (stickerSet2 != null) {
                                putStickerSet(i3, stickerSet2);
                            }
                        }
                    }
                }
                onUpdate();
            }
        }

        private void putStickerSet(int i, TLRPC.TL_messages_stickerSet tL_messages_stickerSet) {
            if (i >= 0) {
                ArrayList<EmojiView.CustomEmoji>[] arrayListArr = this.data;
                if (i >= arrayListArr.length) {
                    return;
                }
                int i2 = 0;
                if (tL_messages_stickerSet == null || tL_messages_stickerSet.documents == null) {
                    arrayListArr[i] = new ArrayList<>(12);
                    while (i2 < 12) {
                        this.data[i].add(null);
                        i2++;
                    }
                    return;
                }
                arrayListArr[i] = new ArrayList<>();
                while (i2 < tL_messages_stickerSet.documents.size()) {
                    TLRPC.Document document = tL_messages_stickerSet.documents.get(i2);
                    if (document == null) {
                        this.data[i].add(null);
                    } else {
                        EmojiView.CustomEmoji customEmoji = new EmojiView.CustomEmoji();
                        customEmoji.emoticon = findEmoticon(tL_messages_stickerSet, document.id);
                        customEmoji.stickerSet = tL_messages_stickerSet;
                        customEmoji.documentId = document.id;
                        this.data[i].add(customEmoji);
                        if (EmojiPacksAlert.this.limitCount) {
                            TLRPC.StickerSet stickerSet = tL_messages_stickerSet.set;
                            if (this.data[i].size() >= ((stickerSet == null || stickerSet.emojis) ? 16 : 10)) {
                                return;
                            }
                        } else {
                            continue;
                        }
                    }
                    i2++;
                }
            }
        }

        public void recycle() {
            NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.groupStickersDidLoad);
        }

        public int getItemsCount() {
            int iMin;
            int i = 0;
            if (this.data == null) {
                return 0;
            }
            int i2 = 0;
            while (true) {
                ArrayList<EmojiView.CustomEmoji>[] arrayListArr = this.data;
                if (i >= arrayListArr.length) {
                    return i2;
                }
                ArrayList<EmojiView.CustomEmoji> arrayList = arrayListArr[i];
                if (arrayList != null) {
                    if (arrayListArr.length == 1) {
                        iMin = arrayList.size();
                    } else {
                        iMin = Math.min(EmojiPacksAlert.this.gridLayoutManager.getSpanCount() * 2, this.data[i].size());
                    }
                    i2 = i2 + iMin + 1;
                }
                i++;
            }
        }

        public String findEmoticon(TLRPC.TL_messages_stickerSet tL_messages_stickerSet, long j) {
            if (tL_messages_stickerSet == null) {
                return null;
            }
            for (int i = 0; i < tL_messages_stickerSet.packs.size(); i++) {
                TLRPC.TL_stickerPack tL_stickerPack = tL_messages_stickerSet.packs.get(i);
                ArrayList<Long> arrayList = tL_stickerPack.documents;
                if (arrayList != null && arrayList.contains(Long.valueOf(j))) {
                    return tL_stickerPack.emoticon;
                }
            }
            return null;
        }
    }

    public ColorFilter getAdaptiveEmojiColorFilter(int i) {
        if (i != this.adaptiveEmojiColor || this.adaptiveEmojiColorFilter == null) {
            this.adaptiveEmojiColor = i;
            this.adaptiveEmojiColorFilter = new PorterDuffColorFilter(i, PorterDuff.Mode.SRC_IN);
        }
        return this.adaptiveEmojiColorFilter;
    }
}
