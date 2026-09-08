package org.telegram.ui.Components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.api.dto.BadgeDTO;
import com.exteragram.messenger.badges.BadgesController;
import com.exteragram.messenger.utils.system.VibratorUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.function.Consumer;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ContentPreviewViewer;

public class SuggestEmojiView extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private Adapter adapter;
    private Integer arrowToEnd;
    private Emoji.EmojiSpan arrowToSpan;
    private Integer arrowToStart;
    private float arrowX;
    private AnimatedFloat arrowXAnimated;
    private Paint backgroundPaint;
    private Path circlePath;
    private boolean clear;
    private FrameLayout containerView;
    private final int currentAccount;
    private int direction;
    private AnchorViewDelegate enterView;
    private boolean forceClose;
    private int horizontalPadding;
    private boolean isCopyForbidden;
    private boolean isSetAsStatusForbidden;
    private ArrayList<MediaDataController.KeywordResult> keywordResults;
    private String[] lastLang;
    private long lastLangChangedTime;
    private String lastQuery;
    private int lastQueryId;
    private int lastQueryType;
    private float lastSpanY;
    private AnimatedFloat leftGradientAlpha;
    private RecyclerListView listView;
    private AnimatedFloat listViewCenterAnimated;
    private AnimatedFloat listViewWidthAnimated;
    private MediaDataController.SearchStickersKey loadingKey;
    private OvershootInterpolator overshootInterpolator;
    private Path path;
    private ContentPreviewViewer.ContentPreviewViewerDelegate previewDelegate;
    private final Theme.ResourcesProvider resourcesProvider;
    private AnimatedFloat rightGradientAlpha;
    private Runnable searchRunnable;
    private boolean show;
    private AnimatedFloat showFloat1;
    private AnimatedFloat showFloat2;
    private Runnable updateRunnable;

    public interface AnchorViewDelegate {
        void addTextChangedListener(TextWatcher textWatcher);

        EditTextBoldCursor getEditField();

        Editable getEditText();

        CharSequence getFieldText();

        BaseFragment getParentFragment();

        int getVisibility();

        void setFieldText(CharSequence charSequence);
    }

    public int emojiCacheType() {
        return 2;
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
            if (SuggestEmojiView.this.enterView == null) {
                return false;
            }
            BaseFragment parentFragment = SuggestEmojiView.this.enterView.getParentFragment();
            if (parentFragment instanceof ChatActivity) {
                ChatActivity chatActivity = (ChatActivity) parentFragment;
                if (chatActivity.canSendMessage()) {
                    if (UserConfig.getInstance(UserConfig.selectedAccount).isPremium()) {
                        return true;
                    }
                    if (chatActivity.getCurrentUser() != null && UserObject.isUserSelf(chatActivity.getCurrentUser())) {
                        return true;
                    }
                }
            }
            return false;
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public void sendEmoji(TLRPC.Document document) {
            if (SuggestEmojiView.this.enterView == null) {
                return;
            }
            BaseFragment parentFragment = SuggestEmojiView.this.enterView.getParentFragment();
            if (parentFragment instanceof ChatActivity) {
                ((ChatActivity) parentFragment).sendAnimatedEmoji(document, true, 0);
                SuggestEmojiView.this.enterView.setFieldText(_UrlKt.FRAGMENT_ENCODE_SET);
            }
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public boolean needCopy(TLRPC.Document document) {
            if (SuggestEmojiView.this.isCopyForbidden) {
                return false;
            }
            return UserConfig.getInstance(UserConfig.selectedAccount).isPremium();
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public void copyEmoji(TLRPC.Document document) {
            SpannableStringBuilder spannableStringBuilderValueOf = SpannableStringBuilder.valueOf(MessageObject.findAnimatedEmojiEmoticon(document));
            spannableStringBuilderValueOf.setSpan(new AnimatedEmojiSpan(document, (Paint.FontMetricsInt) null), 0, spannableStringBuilderValueOf.length(), 33);
            if (!AndroidUtilities.addToClipboard(spannableStringBuilderValueOf) || SuggestEmojiView.this.enterView == null) {
                return;
            }
            BulletinFactory.of(SuggestEmojiView.this.enterView.getParentFragment()).createCopyBulletin(LocaleController.getString(R.string.EmojiCopied)).show();
        }

        @Override // org.telegram.ui.ContentPreviewViewer.ContentPreviewViewerDelegate
        public Boolean canSetAsStatus(TLRPC.Document document) {
            TLRPC.User currentUser;
            if (SuggestEmojiView.this.isSetAsStatusForbidden || !UserConfig.getInstance(UserConfig.selectedAccount).isPremium() || (currentUser = UserConfig.getInstance(UserConfig.selectedAccount).getCurrentUser()) == null) {
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
            MessagesController.getInstance(SuggestEmojiView.this.currentAccount).updateEmojiStatus(tL_emojiStatusEmpty);
            Runnable runnable = new Runnable() { // from class: org.telegram.ui.Components.SuggestEmojiView$1$$ExternalSyntheticLambda2
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$setAsEmojiStatus$0(tL_emojiStatusEmpty2);
                }
            };
            BaseFragment parentFragment = SuggestEmojiView.this.enterView == null ? null : SuggestEmojiView.this.enterView.getParentFragment();
            if (parentFragment != null) {
                if (document == null) {
                    Bulletin.SimpleLayout simpleLayout = new Bulletin.SimpleLayout(SuggestEmojiView.this.getContext(), SuggestEmojiView.this.resourcesProvider);
                    simpleLayout.textView.setText(LocaleController.getString(R.string.RemoveStatusInfo));
                    simpleLayout.imageView.setImageResource(R.drawable.msg_settings_premium);
                    Bulletin.UndoButton undoButton = new Bulletin.UndoButton(SuggestEmojiView.this.getContext(), true, SuggestEmojiView.this.resourcesProvider);
                    undoButton.setUndoAction(runnable);
                    simpleLayout.setButton(undoButton);
                    Bulletin.make(parentFragment, simpleLayout, 1500).show();
                    return;
                }
                BulletinFactory.of(parentFragment).createEmojiBulletin(document, LocaleController.getString(R.string.SetAsEmojiStatusInfo), LocaleController.getString(R.string.UndoNoCaps), runnable).show();
            }
        }

        public void lambda$searchKeywords$3(int i, String str, HashSet hashSet, ArrayList arrayList, ArrayList arrayList2, String str2) {
        if (i != this.lastQueryId) {
            return;
        }
        this.lastQueryType = 1;
        this.lastQuery = str;
        if (arrayList2 != null) {
            int size = arrayList2.size();
            int i2 = 0;
            while (i2 < size) {
                Object obj = arrayList2.get(i2);
                i2++;
                MediaDataController.KeywordResult keywordResult = (MediaDataController.KeywordResult) obj;
                if (!hashSet.contains(keywordResult.emoji)) {
                    hashSet.add(keywordResult.emoji);
                    arrayList.add(keywordResult);
                }
            }
        }
        if (!arrayList.isEmpty()) {
            this.clear = false;
            this.forceClose = false;
            createListView();
            FrameLayout frameLayout = this.containerView;
            if (frameLayout != null) {
                frameLayout.setVisibility(0);
            }
            this.lastSpanY = AndroidUtilities.dp(10.0f);
            this.keywordResults = arrayList2;
            this.arrowToStart = 0;
            this.arrowToEnd = Integer.valueOf(str.length());
            FrameLayout frameLayout2 = this.containerView;
            if (frameLayout2 != null) {
                frameLayout2.invalidate();
            }
            Adapter adapter = this.adapter;
            if (adapter != null) {
                adapter.notifyDataSetChanged();
                return;
            }
            return;
        }
        this.keywordResults = null;
        this.clear = true;
        forceClose();
    }

    private void searchAnimated(final String str) {
        ArrayList<MediaDataController.KeywordResult> arrayList;
        if (str == null) {
            return;
        }
        String str2 = this.lastQuery;
        if (str2 != null && this.lastQueryType == 2 && str2.equals(str) && !this.clear && (arrayList = this.keywordResults) != null && !arrayList.isEmpty()) {
            this.forceClose = false;
            createListView();
            FrameLayout frameLayout = this.containerView;
            if (frameLayout != null) {
                frameLayout.setVisibility(0);
                this.containerView.invalidate();
                return;
            }
            return;
        }
        final int i = this.lastQueryId + 1;
        this.lastQueryId = i;
        Runnable runnable = this.searchRunnable;
        if (runnable != null) {
            AndroidUtilities.cancelRunOnUIThread(runnable);
        }
        this.searchRunnable = new Runnable() { // from class: org.telegram.ui.Components.SuggestEmojiView$$ExternalSyntheticLambda2
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$searchAnimated$6(str, i);
            }
        };
        ArrayList<MediaDataController.KeywordResult> arrayList2 = this.keywordResults;
        if (arrayList2 == null || arrayList2.isEmpty()) {
            AndroidUtilities.runOnUIThread(this.searchRunnable, 600L);
        } else {
            this.searchRunnable.run();
        }
    }

    public /* synthetic */ void lambda$searchAnimated$6(final String str, final int i) {
        final ArrayList<MediaDataController.KeywordResult> arrayList = new ArrayList<>(1);
        arrayList.add(new MediaDataController.KeywordResult(str, null));
        MediaDataController.getInstance(this.currentAccount).fillWithAnimatedEmoji(arrayList, 15, false, false, false, new Runnable() { // from class: org.telegram.ui.Components.SuggestEmojiView$$ExternalSyntheticLambda7
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$searchAnimated$5(i, str, arrayList);
            }
        });
    }

    public /* synthetic */ void lambda$searchAnimated$5(int i, String str, ArrayList arrayList) {
        if (i == this.lastQueryId) {
            this.lastQuery = str;
            this.lastQueryType = 2;
            arrayList.remove(arrayList.size() - 1);
            if (!arrayList.isEmpty()) {
                this.clear = false;
                this.forceClose = false;
                createListView();
                FrameLayout frameLayout = this.containerView;
                if (frameLayout != null) {
                    frameLayout.setVisibility(0);
                    this.containerView.invalidate();
                }
                this.keywordResults = arrayList;
                Adapter adapter = this.adapter;
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    return;
                }
                return;
            }
            this.clear = true;
            forceClose();
        }
    }

    private CharSequence makeEmoji(String str) {
        AnimatedEmojiSpan animatedEmojiSpan;
        Paint.FontMetricsInt fontMetricsInt = this.enterView.getEditField() != null ? this.enterView.getEditField().getPaint().getFontMetricsInt() : null;
        if (fontMetricsInt == null) {
            Paint paint = new Paint();
            paint.setTextSize(AndroidUtilities.dp(18.0f));
            fontMetricsInt = paint.getFontMetricsInt();
        }
        if (str != null && str.startsWith("animated_")) {
            try {
                long j = Long.parseLong(str.substring(9));
                TLRPC.Document documentFindDocument = AnimatedEmojiDrawable.findDocument(this.currentAccount, j);
                SpannableString spannableString = new SpannableString(MessageObject.findAnimatedEmojiEmoticon(documentFindDocument));
                if (documentFindDocument == null) {
                    animatedEmojiSpan = new AnimatedEmojiSpan(j, fontMetricsInt);
                } else {
                    animatedEmojiSpan = new AnimatedEmojiSpan(documentFindDocument, fontMetricsInt);
                }
                spannableString.setSpan(animatedEmojiSpan, 0, spannableString.length(), 33);
                return spannableString;
            } catch (Exception unused) {
                return null;
            }
        }
        return Emoji.replaceEmoji(str, fontMetricsInt, true);
    }

    private void onClick(String str) {
        AnchorViewDelegate anchorViewDelegate;
        int iIntValue;
        int iIntValue2;
        CharSequence charSequenceMakeEmoji;
        AnimatedEmojiSpan[] animatedEmojiSpanArr;
        if (this.show && (anchorViewDelegate = this.enterView) != null && (anchorViewDelegate.getFieldText() instanceof Spanned)) {
            if (this.arrowToSpan != null) {
                iIntValue = ((Spanned) this.enterView.getFieldText()).getSpanStart(this.arrowToSpan);
                iIntValue2 = ((Spanned) this.enterView.getFieldText()).getSpanEnd(this.arrowToSpan);
            } else {
                Integer num = this.arrowToStart;
                if (num == null || this.arrowToEnd == null) {
                    return;
                }
                iIntValue = num.intValue();
                iIntValue2 = this.arrowToEnd.intValue();
                this.arrowToEnd = null;
                this.arrowToStart = null;
            }
            Editable editText = this.enterView.getEditText();
            if (editText == null || iIntValue < 0 || iIntValue2 < 0 || iIntValue > editText.length() || iIntValue2 > editText.length()) {
                return;
            }
            if (this.arrowToSpan != null) {
                if (this.enterView.getFieldText() instanceof Spannable) {
                    ((Spannable) this.enterView.getFieldText()).removeSpan(this.arrowToSpan);
                }
                this.arrowToSpan = null;
            }
            String string = editText.toString();
            String strSubstring = string.substring(iIntValue, iIntValue2);
            int length = strSubstring.length();
            while (true) {
                iIntValue2 -= length;
                if (iIntValue2 < 0) {
                    break;
                }
                int i = iIntValue2 + length;
                if (!string.substring(iIntValue2, i).equals(strSubstring) || (charSequenceMakeEmoji = makeEmoji(str)) == null || ((animatedEmojiSpanArr = (AnimatedEmojiSpan[]) editText.getSpans(iIntValue2, i, AnimatedEmojiSpan.class)) != null && animatedEmojiSpanArr.length > 0)) {
                    break;
                }
                Emoji.EmojiSpan[] emojiSpanArr = (Emoji.EmojiSpan[]) editText.getSpans(iIntValue2, i, Emoji.EmojiSpan.class);
                if (emojiSpanArr != null) {
                    for (Emoji.EmojiSpan emojiSpan : emojiSpanArr) {
                        editText.removeSpan(emojiSpan);
                    }
                }
                editText.replace(iIntValue2, i, _UrlKt.FRAGMENT_ENCODE_SET);
                editText.insert(iIntValue2, charSequenceMakeEmoji);
            }
            try {
                performHapticFeedback(VibratorUtils.getType(3), 1);
            } catch (Exception unused) {
            }
            Emoji.addRecentEmoji(str);
            this.show = false;
            this.forceClose = true;
            this.lastQueryType = 0;
            FrameLayout frameLayout = this.containerView;
            if (frameLayout != null) {
                frameLayout.invalidate();
            }
        }
    }

    public void drawContainerBegin(Canvas canvas) {
        float f;
        ArrayList<MediaDataController.KeywordResult> arrayList;
        AnchorViewDelegate anchorViewDelegate = this.enterView;
        if (anchorViewDelegate != null && anchorViewDelegate.getEditField() != null) {
            Emoji.EmojiSpan emojiSpan = this.arrowToSpan;
            if (emojiSpan != null && emojiSpan.drawn) {
                float x = this.enterView.getEditField().getX() + this.enterView.getEditField().getPaddingLeft();
                Emoji.EmojiSpan emojiSpan2 = this.arrowToSpan;
                this.arrowX = x + emojiSpan2.lastDrawX;
                this.lastSpanY = emojiSpan2.lastDrawY;
            } else if (this.arrowToStart != null && this.arrowToEnd != null) {
                this.arrowX = this.enterView.getEditField().getX() + this.enterView.getEditField().getPaddingLeft() + AndroidUtilities.dp(12.0f);
            }
        }
        boolean z = (!this.show || this.forceClose || (arrayList = this.keywordResults) == null || arrayList.isEmpty() || this.clear) ? false : true;
        float f2 = this.showFloat1.set(z ? 1.0f : 0.0f);
        float f3 = this.showFloat2.set(z ? 1.0f : 0.0f);
        float f4 = this.arrowXAnimated.set(this.arrowX);
        if (f2 <= 0.0f && f3 <= 0.0f && !z) {
            this.containerView.setVisibility(8);
        }
        this.path.rewind();
        float left = this.listView.getLeft();
        int left2 = this.listView.getLeft();
        ArrayList<MediaDataController.KeywordResult> arrayList2 = this.keywordResults;
        float size = left2 + ((arrayList2 == null ? 0 : arrayList2.size()) * AndroidUtilities.dp(44.0f));
        boolean z2 = this.listViewWidthAnimated.get() <= 0.0f;
        float f5 = size - left;
        AnimatedFloat animatedFloat = this.listViewWidthAnimated;
        float f6 = f5 <= 0.0f ? animatedFloat.get() : animatedFloat.set(f5, z2);
        float f7 = this.listViewCenterAnimated.set((left + size) / 2.0f, z2);
        AnchorViewDelegate anchorViewDelegate2 = this.enterView;
        if (anchorViewDelegate2 != null && anchorViewDelegate2.getEditField() != null) {
            int i = this.direction;
            if (i == 0) {
                this.containerView.setTranslationY(((-this.enterView.getEditField().getHeight()) - this.enterView.getEditField().getScrollY()) + this.lastSpanY + AndroidUtilities.dp(5.0f));
            } else if (i == 1) {
                this.containerView.setTranslationY(((-getMeasuredHeight()) - this.enterView.getEditField().getScrollY()) + this.lastSpanY + AndroidUtilities.dp(20.0f) + this.containerView.getHeight());
            }
        }
        float f8 = f6 / 4.0f;
        float f9 = f6 / 2.0f;
        int iMax = (int) Math.max((this.arrowX - Math.max(f8, Math.min(f9, AndroidUtilities.dp(66.0f)))) - this.listView.getLeft(), 0.0f);
        if (this.listView.getPaddingLeft() != iMax) {
            int paddingLeft = this.listView.getPaddingLeft() - iMax;
            this.listView.setPadding(iMax, 0, 0, 0);
            this.listView.scrollBy(paddingLeft, 0);
        }
        this.listView.setTranslationX(((int) Math.max((f4 - Math.max(f8, Math.min(f9, AndroidUtilities.dp(66.0f)))) - this.listView.getLeft(), 0.0f)) - iMax);
        float paddingLeft2 = (f7 - f9) + this.listView.getPaddingLeft() + this.listView.getTranslationX();
        float top = this.listView.getTop() + this.listView.getTranslationY() + this.listView.getPaddingTop() + (this.direction == 0 ? 0 : AndroidUtilities.dp(6.66f));
        float fMin = Math.min(f7 + f9 + this.listView.getPaddingLeft() + this.listView.getTranslationX(), getWidth() - this.containerView.getPaddingRight());
        float bottom = (this.listView.getBottom() + this.listView.getTranslationY()) - (this.direction == 0 ? AndroidUtilities.dp(6.66f) : 0);
        float fMin2 = Math.min(AndroidUtilities.dp(9.0f), f9) * 2.0f;
        int i2 = this.direction;
        if (i2 == 0) {
            RectF rectF = AndroidUtilities.rectTmp;
            f = 6.66f;
            float f10 = bottom - fMin2;
            float f11 = paddingLeft2 + fMin2;
            rectF.set(paddingLeft2, f10, f11, bottom);
            this.path.arcTo(rectF, 90.0f, 90.0f);
            float f12 = top + fMin2;
            rectF.set(paddingLeft2, top, f11, f12);
            this.path.arcTo(rectF, -180.0f, 90.0f);
            float f13 = fMin - fMin2;
            rectF.set(f13, top, fMin, f12);
            this.path.arcTo(rectF, -90.0f, 90.0f);
            rectF.set(f13, f10, fMin, bottom);
            this.path.arcTo(rectF, 0.0f, 90.0f);
            this.path.lineTo(AndroidUtilities.dp(8.66f) + f4, bottom);
            this.path.lineTo(f4, AndroidUtilities.dp(6.66f) + bottom);
            this.path.lineTo(f4 - AndroidUtilities.dp(8.66f), bottom);
        } else {
            f = 6.66f;
            if (i2 == 1) {
                RectF rectF2 = AndroidUtilities.rectTmp;
                float f14 = fMin - fMin2;
                float f15 = top + fMin2;
                rectF2.set(f14, top, fMin, f15);
                this.path.arcTo(rectF2, -90.0f, 90.0f);
                float f16 = bottom - fMin2;
                rectF2.set(f14, f16, fMin, bottom);
                this.path.arcTo(rectF2, 0.0f, 90.0f);
                float f17 = fMin2 + paddingLeft2;
                rectF2.set(paddingLeft2, f16, f17, bottom);
                this.path.arcTo(rectF2, 90.0f, 90.0f);
                rectF2.set(paddingLeft2, top, f17, f15);
                this.path.arcTo(rectF2, -180.0f, 90.0f);
                this.path.lineTo(f4 - AndroidUtilities.dp(8.66f), top);
                this.path.lineTo(f4, top - AndroidUtilities.dp(6.66f));
                this.path.lineTo(AndroidUtilities.dp(8.66f) + f4, top);
            }
        }
        this.path.close();
        if (this.backgroundPaint == null) {
            Paint paint = new Paint(1);
            this.backgroundPaint = paint;
            paint.setPathEffect(new CornerPathEffect(AndroidUtilities.dp(2.0f)));
            this.backgroundPaint.setShadowLayer(AndroidUtilities.dp(4.33f), 0.0f, AndroidUtilities.dp(0.33333334f), 855638016);
            this.backgroundPaint.setColor(Theme.getColor(Theme.key_chat_stickersHintPanel, this.resourcesProvider));
        }
        if (f2 < 1.0f) {
            this.circlePath.rewind();
            float fDp = this.direction == 0 ? AndroidUtilities.dp(f) + bottom : top - AndroidUtilities.dp(f);
            double d = f4 - paddingLeft2;
            double d2 = fDp - top;
            double d3 = f4 - fMin;
            double d4 = fDp - bottom;
            this.circlePath.addCircle(f4, fDp, ((float) Math.sqrt(Math.max(Math.max(Math.pow(d, 2.0d) + Math.pow(d2, 2.0d), Math.pow(d3, 2.0d) + Math.pow(d2, 2.0d)), Math.max(Math.pow(d, 2.0d) + Math.pow(d4, 2.0d), Math.pow(d3, 2.0d) + Math.pow(d4, 2.0d))))) * f2, Path.Direction.CW);
            canvas.save();
            canvas.clipPath(this.circlePath);
            canvas.saveLayerAlpha(0.0f, 0.0f, getWidth(), getHeight(), (int) (255.0f * f2), 31);
        }
        canvas.drawPath(this.path, this.backgroundPaint);
        canvas.save();
        canvas.clipPath(this.path);
    }

    public void drawContainerEnd(Canvas canvas) {
        float f = this.listViewWidthAnimated.get();
        float f2 = this.listViewCenterAnimated.get();
        float f3 = f / 2.0f;
        float paddingLeft = (f2 - f3) + this.listView.getPaddingLeft() + this.listView.getTranslationX();
        float top = this.listView.getTop() + this.listView.getPaddingTop();
        float fMin = Math.min(f2 + f3 + this.listView.getPaddingLeft() + this.listView.getTranslationX(), getWidth() - this.containerView.getPaddingRight());
        float bottom = this.listView.getBottom();
        float f4 = this.leftGradientAlpha.set(this.listView.canScrollHorizontally(-1) ? 1.0f : 0.0f);
        if (f4 > 0.0f) {
            int i = (int) paddingLeft;
            Theme.chat_gradientRightDrawable.setBounds(i, (int) top, AndroidUtilities.dp(32.0f) + i, (int) bottom);
            Theme.chat_gradientRightDrawable.setAlpha((int) (f4 * 255.0f));
            Theme.chat_gradientRightDrawable.draw(canvas);
        }
        float f5 = this.rightGradientAlpha.set(this.listView.canScrollHorizontally(1) ? 1.0f : 0.0f);
        if (f5 > 0.0f) {
            int i2 = (int) fMin;
            Theme.chat_gradientLeftDrawable.setBounds(i2 - AndroidUtilities.dp(32.0f), (int) top, i2, (int) bottom);
            Theme.chat_gradientLeftDrawable.setAlpha((int) (f5 * 255.0f));
            Theme.chat_gradientLeftDrawable.draw(canvas);
        }
        canvas.restore();
        if (this.showFloat1.get() < 1.0f) {
            canvas.restore();
            canvas.restore();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (this.listView == null) {
            return super.dispatchTouchEvent(motionEvent);
        }
        float f = this.listViewWidthAnimated.get();
        float f2 = this.listViewCenterAnimated.get();
        RectF rectF = AndroidUtilities.rectTmp;
        float f3 = f / 2.0f;
        rectF.set((f2 - f3) + this.listView.getPaddingLeft() + this.listView.getTranslationX(), this.listView.getTop() + this.listView.getPaddingTop(), Math.min(f2 + f3 + this.listView.getPaddingLeft() + this.listView.getTranslationX(), getWidth() - this.containerView.getPaddingRight()), this.listView.getBottom());
        rectF.offset(this.containerView.getX(), this.containerView.getY());
        if (this.show && rectF.contains(motionEvent.getX(), motionEvent.getY())) {
            return super.dispatchTouchEvent(motionEvent);
        }
        if (motionEvent.getAction() == 0) {
            return false;
        }
        if (motionEvent.getAction() == 0) {
            motionEvent.setAction(3);
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).addObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.emojiLoaded);
        NotificationCenter.getInstance(this.currentAccount).removeObserver(this, NotificationCenter.newEmojiSuggestionsAvailable);
    }

    @Override 
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        if (i == NotificationCenter.newEmojiSuggestionsAvailable) {
            ArrayList<MediaDataController.KeywordResult> arrayList = this.keywordResults;
            if (arrayList == null || arrayList.isEmpty()) {
                return;
            }
            fireUpdate();
            return;
        }
        if (i != NotificationCenter.emojiLoaded || this.listView == null) {
            return;
        }
        for (int i3 = 0; i3 < this.listView.getChildCount(); i3++) {
            this.listView.getChildAt(i3).invalidate();
        }
    }

    public class EmojiImageView extends View {
        private boolean attached;
        private int direction;
        public Drawable drawable;
        private String emoji;
        private final int paddingDp;
        private AnimatedFloat pressed;

        public EmojiImageView(Context context) {
            super(context);
            this.direction = 0;
            this.pressed = new AnimatedFloat(this, 350L, new OvershootInterpolator(5.0f));
            this.paddingDp = 3;
        }

        @Override // android.view.View
        public void onMeasure(int i, int i2) {
            setPadding(AndroidUtilities.dp(3.0f), AndroidUtilities.dp((this.direction == 0 ? 0.0f : 6.66f) + 3.0f), AndroidUtilities.dp(3.0f), AndroidUtilities.dp((this.direction != 0 ? 0.0f : 6.66f) + 3.0f));
            super.onMeasure(View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(44.0f), TLObject.FLAG_30), View.MeasureSpec.makeMeasureSpec(AndroidUtilities.dp(52.0f), TLObject.FLAG_30));
        }

        public void setEmoji(String str, int i) {
            this.emoji = str;
            if (str != null && str.startsWith("animated_")) {
                try {
                    long j = Long.parseLong(str.substring(9));
                    Drawable drawable = this.drawable;
                    if (!(drawable instanceof AnimatedEmojiDrawable) || ((AnimatedEmojiDrawable) drawable).getDocumentId() != j) {
                        setImageDrawable(AnimatedEmojiDrawable.make(UserConfig.selectedAccount, SuggestEmojiView.this.emojiCacheType(), j));
                    }
                } catch (Exception unused) {
                    setImageDrawable(null);
                }
            } else {
                setImageDrawable(Emoji.getEmojiBigDrawable(str));
            }
            if (this.direction != i) {
                this.direction = i;
                requestLayout();
            }
        }

        public void setImageDrawable(Drawable drawable) {
            Drawable drawable2 = this.drawable;
            if (drawable2 instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawable2).removeView(this);
            }
            this.drawable = drawable;
            if ((drawable instanceof AnimatedEmojiDrawable) && this.attached) {
                ((AnimatedEmojiDrawable) drawable).addView(this);
            }
        }

        public void setDirection(int i) {
            this.direction = i;
            invalidate();
        }

        @Override // android.view.View
        public void setPressed(boolean z) {
            super.setPressed(z);
            invalidate();
        }

        @Override // android.view.View
        public void dispatchDraw(Canvas canvas) {
            float f = ((1.0f - this.pressed.set(isPressed() ? 1.0f : 0.0f)) * 0.2f) + 0.8f;
            if (this.drawable != null) {
                int width = getWidth() / 2;
                int height = ((getHeight() - getPaddingBottom()) + getPaddingTop()) / 2;
                this.drawable.setBounds(getPaddingLeft(), getPaddingTop(), getWidth() - getPaddingRight(), getHeight() - getPaddingBottom());
                canvas.scale(f, f, width, height);
                Drawable drawable = this.drawable;
                if (drawable instanceof AnimatedEmojiDrawable) {
                    ((AnimatedEmojiDrawable) drawable).setTime(System.currentTimeMillis());
                }
                this.drawable.draw(canvas);
            }
        }

        @Override // android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            attach();
        }

        @Override // android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            detach();
        }

        public void detach() {
            Drawable drawable = this.drawable;
            if (drawable instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawable).removeView(this);
            }
            this.attached = false;
        }

        public void attach() {
            Drawable drawable = this.drawable;
            if (drawable instanceof AnimatedEmojiDrawable) {
                ((AnimatedEmojiDrawable) drawable).addView(this);
            }
            this.attached = true;
        }
    }

    public class Adapter extends RecyclerListView.SelectionAdapter {
        SuggestEmojiView suggestEmojiView;

        @Override // org.telegram.ui.Components.RecyclerListView.SelectionAdapter
        public boolean isEnabled(RecyclerView.ViewHolder viewHolder) {
            return true;
        }

        public Adapter(SuggestEmojiView suggestEmojiView) {
            this.suggestEmojiView = suggestEmojiView;
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public long getItemId(int i) {
            if (this.suggestEmojiView.keywordResults == null) {
                return 0L;
            }
            return ((MediaDataController.KeywordResult) this.suggestEmojiView.keywordResults.get(i)).emoji.hashCode();
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            return new RecyclerListView.Holder(SuggestEmojiView.this.new EmojiImageView(this.suggestEmojiView.getContext()));
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
            ((EmojiImageView) viewHolder.itemView).setEmoji(this.suggestEmojiView.keywordResults == null ? null : ((MediaDataController.KeywordResult) this.suggestEmojiView.keywordResults.get(i)).emoji, this.suggestEmojiView.getDirection());
        }

        @Override // androidx.recyclerview.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (this.suggestEmojiView.keywordResults == null) {
                return 0;
            }
            return this.suggestEmojiView.keywordResults.size();
        }
    }
}
