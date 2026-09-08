package org.telegram.ui.iv;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Property;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.ViewPropertyAnimator;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.graphics.Insets;
import androidx.core.view.WindowInsetsCompat;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.google.android.gms.cast.MediaError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SendMessagesHelper;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_iv;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ArticleViewer$$ExternalSyntheticLambda24;
import org.telegram.ui.Cells.TextSelectionHelper;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.AIEditorAlert;
import org.telegram.ui.Components.AiButtonDrawable;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatActivityEnterView;
import org.telegram.ui.Components.ChatActivityEnterViewAnimatedIconView;
import org.telegram.ui.Components.ChatAttachAlert;
import org.telegram.ui.Components.ChatAttachAlertAudioLayout;
import org.telegram.ui.Components.ChatAttachAlertLocationLayout;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.ScaleStateListAnimator;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.chat.ChatInputViewsContainer;
import org.telegram.ui.GradientClip;
import org.telegram.ui.MessageSendPreview;
import org.telegram.ui.StickersActivity;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class RichEditor extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private ImageView addButton;
    private ImageView aiButton;
    private Button aiStyleButton;
    private ChatActivityEnterView animateEnterView;
    private int[] animateEnterViewFrom;
    private int[] animateEnterViewTo;
    private RectF animateFromRect;
    private BlurredBackgroundDrawable animateInputBackground;
    private ChatInputViewsContainer animateInputView;
    private float animateOpenProgress;
    private boolean animatingOpen;
    private ImageView backButton;
    private final ArrayList<Button> blockButtons;
    private LinearLayout blocksLayout;
    private HorizontalScrollView blocksScrollView;
    private FrameLayout bottomContainer;
    private View bottomGradient;
    private FrameLayout bottomInnerContainer;
    private int bottomInset;
    private LinearLayout bottomPanel;
    private int bottomPanelType;
    private FrameLayout bulletinContainer;
    private ChatActivity chatActivity;
    private RichCommandSuggestions commandSuggestions;
    private SizeNotifierFrameLayout container;
    private boolean convertToSimpleOnOpen;
    private ItemOptions currentMenuVisible;
    private Button dateButton;
    private MessageObject editingMessageObject;
    private ChatActivityEnterViewAnimatedIconView emojiButton;
    private int emojiPadding;
    private ValueAnimator emojiSearchAnimator;
    private boolean emojiSearchOpened;
    private float emojiSearchProgress;
    private RichEditText emojiTargetEditText;
    private int emojiTargetSelection;
    private EmojiView emojiView;
    private boolean emojiViewVisible;
    private final ArrayList<Button> formattingButtons;
    private LinearLayout formattingLayout1;
    private LinearLayout formattingLayout2;
    private LinearLayout formattingLayout3;
    private LinearLayout formattingPanel;
    private LinearLayout formattingPanelLayout;
    private int formattingScrollMaxWidth;
    private HorizontalScrollView formattingScrollView;
    private LinearLayout historyButtons;
    private int imeInset;
    private String initialHtml;
    private CharSequence initialHtmlAfter;
    private CharSequence initialHtmlBefore;
    private TL_iv.RichMessage initialRichMessage;
    private int initialSelectionEnd;
    private int initialSelectionStart;
    private CharSequence initialText;
    private int keyboardHeight;
    private int keyboardHeightLand;
    private boolean keyboardVisible;
    private final Runnable limitCheckRunnable;
    private Button linkButton;
    private RichEditorListView listView;
    private int[] location;
    private Button mathButton;
    private MessageSendPreview messageSendPreview;
    private Runnable onClearedCallback;
    private Runnable onSentCallback;
    private Runnable pendingSend;
    private boolean persistedDraftOnEnd;
    private ArrayList<Button> premiumButtons;
    private Button quoteButton;
    private ImageView redoButton;
    private int reorderSavedPanelType;
    private ChatActivityEnterView.SendButton sendButton;
    private boolean sendButtonLoading;
    private boolean sent;
    private SizeNotifierFrameLayout.SizeNotifierFrameLayoutDelegate sizeDelegate;
    private final Rect tempRect;
    private View topGradient;
    private FrameLayout topPanel;
    private boolean trashHovered;
    private FrameLayout trashPanel;
    private RLottieImageView trashPanelIcon;
    private ImageView undoButton;

    public void onKeyboardSizeChanged(int i, boolean z) {
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean hideKeyboardOnShow() {
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    public RichEditor(CharSequence charSequence) {
        this.initialSelectionStart = -1;
        this.initialSelectionEnd = -1;
        this.tempRect = new Rect();
        this.location = new int[2];
        this.animateOpenProgress = 1.0f;
        this.premiumButtons = new ArrayList<>();
        this.blockButtons = new ArrayList<>();
        this.formattingScrollMaxWidth = Integer.MAX_VALUE;
        this.formattingButtons = new ArrayList<>();
        this.reorderSavedPanelType = 0;
        this.bottomPanelType = -1;
        this.limitCheckRunnable = new Runnable() { // from class: org.telegram.ui.iv.RichEditor$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.updateSendButtonEnabled();
            }
        };
        this.initialText = charSequence;
    }

    public RichEditor setInitialSelection(int i, int i2) {
        this.initialSelectionStart = i;
        this.initialSelectionEnd = i2;
        return this;
    }

    public RichEditor(TL_iv.RichMessage richMessage) {
        this.initialSelectionStart = -1;
        this.initialSelectionEnd = -1;
        this.tempRect = new Rect();
        this.location = new int[2];
        this.animateOpenProgress = 1.0f;
        this.premiumButtons = new ArrayList<>();
        this.blockButtons = new ArrayList<>();
        this.formattingScrollMaxWidth = Integer.MAX_VALUE;
        this.formattingButtons = new ArrayList<>();
        this.reorderSavedPanelType = 0;
        this.bottomPanelType = -1;
        this.limitCheckRunnable = new Runnable() { // from class: org.telegram.ui.iv.RichEditor$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.updateSendButtonEnabled();
            }
        };
        this.initialRichMessage = richMessage;
    }

    public RichEditor convertToSimpleOnOpen() {
        this.convertToSimpleOnOpen = true;
        return this;
    }

    public RichEditor(String str, boolean z) {
        this.initialSelectionStart = -1;
        this.initialSelectionEnd = -1;
        this.tempRect = new Rect();
        this.location = new int[2];
        this.animateOpenProgress = 1.0f;
        this.premiumButtons = new ArrayList<>();
        this.blockButtons = new ArrayList<>();
        this.formattingScrollMaxWidth = Integer.MAX_VALUE;
        this.formattingButtons = new ArrayList<>();
        this.reorderSavedPanelType = 0;
        this.bottomPanelType = -1;
        this.limitCheckRunnable = new Runnable() { // from class: org.telegram.ui.iv.RichEditor$$ExternalSyntheticLambda0
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.updateSendButtonEnabled();
            }
        };
        this.initialHtml = z ? str : null;
    }

    public RichEditor setHtmlSurrounding(CharSequence charSequence, CharSequence charSequence2) {
        this.initialHtmlBefore = charSequence;
        this.initialHtmlAfter = charSequence2;
        return this;
    }

    public RichEditor setEditing(MessageObject messageObject) {
        this.editingMessageObject = messageObject;
        return this;
    }

    public RichEditor animateFrom(ChatActivity chatActivity) {
        this.animateInputView = chatActivity.chatInputViewsContainer;
        this.animateEnterView = chatActivity.getChatActivityEnterView();
        return this;
    }

    private void updateAnimatingLocations() {
        this.animateInputView.getLocationInWindow(this.location);
        if (this.animateFromRect == null) {
            this.animateFromRect = new RectF();
        }
        RectF rectF = new RectF(this.animateInputBackground.getBounds());
        this.animateFromRect = rectF;
        int[] iArr = this.location;
        rectF.offset(iArr[0], iArr[1]);
        if (this.animateEnterViewFrom == null) {
            this.animateEnterViewFrom = new int[2];
        }
        this.animateEnterView.getLocationInWindow(this.animateEnterViewFrom);
        if (this.animateEnterViewTo == null) {
            this.animateEnterViewTo = new int[2];
        }
        this.animateEnterViewTo[0] = this.listView.getPaddingLeft();
        this.animateEnterViewTo[1] = this.listView.getPaddingTop();
        int[] iArr2 = this.animateEnterViewTo;
        iArr2[0] = (int) (iArr2[0] - (this.animateEnterView.messageEditText.getX() - AndroidUtilities.dp(16.0f)));
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public AnimatorSet onCustomTransitionAnimation(boolean z, final Runnable runnable) {
        if (!z && !this.persistedDraftOnEnd) {
            persistDraft();
            this.persistedDraftOnEnd = true;
        }
        if (!AndroidUtilities.isTablet() && this.animateInputView != null && this.animateEnterView != null) {
            AnimatorSet animatorSet = new AnimatorSet();
            ChatInputViewsContainer chatInputViewsContainer = this.animateInputView;
            this.animateInputBackground = chatInputViewsContainer.blurredBackgroundDrawable;
            chatInputViewsContainer.drawInputBackground = false;
            chatInputViewsContainer.invalidate();
            this.animateEnterView.setAlpha(0.0f);
            this.animateEnterView.sendButtonContainer.setVisibility(4);
            updateAnimatingLocations();
            float f = z ? 0.0f : 1.0f;
            this.animateOpenProgress = f;
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(f, z ? 1.0f : 0.0f);
            this.animatingOpen = true;
            this.container.invalidate();
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.iv.RichEditor$$ExternalSyntheticLambda23
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$onCustomTransitionAnimation$0(valueAnimator);
                }
            });
            valueAnimatorOfFloat.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.iv.RichEditor.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    RichEditor.this.animatingOpen = false;
                    RichEditor.this.animateEnterView.setAlpha(1.0f);
                    RichEditor.this.animateEnterView.sendButtonContainer.setVisibility(0);
                    RichEditor.this.animateInputBackground.setRadius(AndroidUtilities.dp(24.0f));
                    RichEditor.this.animateInputBackground.setAlpha(255);
                    RichEditor.this.animateInputView.drawInputBackground = true;
                    RichEditor.this.animateInputView.invalidate();
                    runnable.run();
                }
            });
            FrameLayout frameLayout = this.topPanel;
            if (!z) {
                Property property = View.ALPHA;
                ObjectAnimator objectAnimatorOfFloat = ObjectAnimator.ofFloat(frameLayout, (Property<FrameLayout, Float>) property, 0.0f);
                FrameLayout frameLayout2 = this.topPanel;
                Property property2 = View.TRANSLATION_Y;
                animatorSet.playTogether(valueAnimatorOfFloat, objectAnimatorOfFloat, ObjectAnimator.ofFloat(frameLayout2, (Property<FrameLayout, Float>) property2, -AndroidUtilities.dp(16.0f)), ObjectAnimator.ofFloat(this.bottomInnerContainer, (Property<FrameLayout, Float>) property, 0.0f), ObjectAnimator.ofFloat(this.bottomInnerContainer, (Property<FrameLayout, Float>) property2, AndroidUtilities.dp(16.0f), 0.0f), ObjectAnimator.ofFloat(this.listView, (Property<RichEditorListView, Float>) property, 1.0f, 0.0f), ObjectAnimator.ofFloat(this.topGradient, (Property<View, Float>) property, 1.0f, 0.0f), ObjectAnimator.ofFloat(this.bottomGradient, (Property<View, Float>) property, 1.0f, 0.0f));
            } else {
                Property property3 = View.ALPHA;
                ObjectAnimator objectAnimatorOfFloat2 = ObjectAnimator.ofFloat(frameLayout, (Property<FrameLayout, Float>) property3, 0.0f, 1.0f);
                FrameLayout frameLayout3 = this.topPanel;
                Property property4 = View.TRANSLATION_Y;
                animatorSet.playTogether(valueAnimatorOfFloat, objectAnimatorOfFloat2, ObjectAnimator.ofFloat(frameLayout3, (Property<FrameLayout, Float>) property4, -AndroidUtilities.dp(16.0f), 0.0f), ObjectAnimator.ofFloat(this.bottomInnerContainer, (Property<FrameLayout, Float>) property3, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.bottomInnerContainer, (Property<FrameLayout, Float>) property4, AndroidUtilities.dp(16.0f), 0.0f), ObjectAnimator.ofFloat(this.listView, (Property<RichEditorListView, Float>) property3, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.topGradient, (Property<View, Float>) property3, 0.0f, 1.0f), ObjectAnimator.ofFloat(this.bottomGradient, (Property<View, Float>) property3, 0.0f, 1.0f));
            }
            animatorSet.setDuration(420L);
            animatorSet.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            this.container.post(new ArticleViewer$$ExternalSyntheticLambda24(animatorSet));
            return animatorSet;
        }
        return super.onCustomTransitionAnimation(z, runnable);
    }

    public void lambda$createView$5(TL_iv.RichMessage richMessage) {
        this.listView.addRichMessage(richMessage);
    }

    public void lambda$createView$29(TL_iv.pageBlockMath pageblockmath, String str) {
        if (pageblockmath != null) {
            pageblockmath.source = str;
            this.listView.adapter.update(false);
        } else {
            TL_iv.pageBlockMath pageblockmath2 = new TL_iv.pageBlockMath();
            pageblockmath2.source = str;
            this.listView.addBlock(pageblockmath2);
        }
    }

    public void lambda$openAttach$43(ChatAttachAlert chatAttachAlert, TLRPC.MessageMedia messageMedia, int i, boolean z, int i2, long j) {
        if (messageMedia == null || messageMedia.geo == null) {
            chatAttachAlert.dismiss(true);
            return;
        }
        TL_iv.pageBlockMap pageblockmap = new TL_iv.pageBlockMap();
        pageblockmap.geo = messageMedia.geo;
        pageblockmap.zoom = 15;
        pageblockmap.w = MediaError.DetailedErrorCode.TEXT_UNKNOWN;
        pageblockmap.h = MediaError.DetailedErrorCode.MANIFEST_UNKNOWN;
        this.listView.addBlock(pageblockmap);
        chatAttachAlert.dismiss(true);
    }

    public void lambda$openLocationPicker$47(final BlockRow blockRow, ChatAttachAlert chatAttachAlert, TLRPC.MessageMedia messageMedia, int i, boolean z, int i2, long j) {
        if (messageMedia == null || messageMedia.geo == null) {
            return;
        }
        RichEditorHistory richEditorHistory = this.listView.history;
        if (richEditorHistory != null) {
            richEditorHistory.flush();
        }
        TL_iv.pageBlockMap pageblockmap = (TL_iv.pageBlockMap) blockRow.block;
        pageblockmap.geo = messageMedia.geo;
        pageblockmap.zoom = 15;
        if (pageblockmap.w <= 0 || pageblockmap.h <= 0) {
            pageblockmap.w = MediaError.DetailedErrorCode.TEXT_UNKNOWN;
            pageblockmap.h = MediaError.DetailedErrorCode.MANIFEST_UNKNOWN;
        }
        RichEditorHistory richEditorHistory2 = this.listView.history;
        if (richEditorHistory2 != null) {
            richEditorHistory2.record();
        }
        chatAttachAlert.dismiss(true);
        this.listView.post(new Runnable() { // from class: org.telegram.ui.iv.RichEditor$$ExternalSyntheticLambda59
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$openLocationPicker$46(blockRow);
            }
        });
    }

    public void lambda$onSendLongClick$49(DialogInterface dialogInterface) {
        this.messageSendPreview = null;
    }

    public void lambda$onSendLongClick$51(long j) {
        AlertsCreator.createScheduleDatePickerDialog(getParentActivity(), j, new AlertsCreator.ScheduleDatePickerDelegate() { // from class: org.telegram.ui.iv.RichEditor.13
            @Override // org.telegram.ui.Components.AlertsCreator.ScheduleDatePickerDelegate
            public void didSelectDate(boolean z, int i, int i2) {
                RichEditor.this.sendMessage(z, i, i2);
                if (RichEditor.this.messageSendPreview != null) {
                    RichEditor.this.messageSendPreview.dismissInstant();
                    RichEditor.this.messageSendPreview = null;
                }
            }
        }, getResourceProvider());
    }

    public void lambda$onSendLongClick$53() {
        sendMessage(false, 0, 0);
        MessageSendPreview messageSendPreview = this.messageSendPreview;
        if (messageSendPreview != null) {
            messageSendPreview.dismiss(true);
            this.messageSendPreview = null;
        }
    }

    public void saveDraftWithBulletin() {
        if (persistDraft()) {
            BulletinFactory.of(this.bulletinContainer, getResourceProvider()).createSimpleBulletin(R.raw.contact_check, LocaleController.getString(R.string.RichEditorDraftSaved)).show();
        }
    }

    private boolean persistDraft() {
        Runnable runnable;
        if (this.chatActivity == null || this.editingMessageObject != null || !this.listView.canUndo()) {
            return false;
        }
        TL_iv.RichMessage richMessageBuildDraftRichMessage = this.sent ? null : this.listView.buildDraftRichMessage();
        if (richMessageBuildDraftRichMessage == null && (runnable = this.onClearedCallback) != null) {
            runnable.run();
        }
        ChatActivityEnterView chatActivityEnterView = this.chatActivity.getChatActivityEnterView();
        if (richMessageBuildDraftRichMessage != null && !this.sent && this.listView.isSimpleConvertible() && chatActivityEnterView != null) {
            chatActivityEnterView.applyConvertedSimpleDraft(this.listView.toSimpleMessage());
            return true;
        }
        getMediaDataController().saveDraft(this.chatActivity.getDialogId(), this.chatActivity.getDraftThreadId(), _UrlKt.FRAGMENT_ENCODE_SET, null, null, null, null, 0L, false, false, richMessageBuildDraftRichMessage);
        if (chatActivityEnterView != null) {
            chatActivityEnterView.setRichDraftPreview(richMessageBuildDraftRichMessage);
        }
        return true;
    }

    private void toggleEmojiPopup() {
        if (this.emojiViewVisible) {
            openKeyboardFromPopup();
        } else {
            showEmojiPopup();
        }
    }

    private void showEmojiPopup() {
        createEmojiView();
        int emojiPanelHeight = getEmojiPanelHeight();
        FrameLayout.LayoutParams layoutParamsCreateFrame = (FrameLayout.LayoutParams) this.emojiView.getLayoutParams();
        if (layoutParamsCreateFrame == null) {
            layoutParamsCreateFrame = LayoutHelper.createFrame(-1, emojiPanelHeight, 87);
        } else {
            layoutParamsCreateFrame.height = emojiPanelHeight;
        }
        layoutParamsCreateFrame.bottomMargin = this.bottomInset;
        this.emojiView.setLayoutParams(layoutParamsCreateFrame);
        this.emojiView.setVisibility(0);
        this.emojiViewVisible = true;
        this.emojiPadding = emojiPanelHeight + this.bottomInset;
        RichEditText richEditTextFindFocusedEditText = this.listView.findFocusedEditText();
        if (richEditTextFindFocusedEditText != null) {
            AndroidUtilities.hideKeyboard(richEditTextFindFocusedEditText);
        }
        applyEmojiPadding();
        this.emojiButton.setState(ChatActivityEnterViewAnimatedIconView.State.KEYBOARD, true);
    }

    public void hideEmojiPopup(boolean z) {
        if (this.emojiSearchOpened) {
            this.emojiSearchOpened = false;
            EmojiView emojiView = this.emojiView;
            if (emojiView != null) {
                emojiView.closeSearch(false);
                this.emojiView.hideSearchKeyboard();
            }
        }
        ValueAnimator valueAnimator = this.emojiSearchAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.emojiSearchAnimator = null;
        }
        this.emojiSearchProgress = 0.0f;
        this.emojiTargetEditText = null;
        EmojiView emojiView2 = this.emojiView;
        if (emojiView2 != null) {
            emojiView2.setTranslationY(0.0f);
            this.emojiView.setVisibility(8);
        }
        if (this.emojiViewVisible || this.emojiPadding != 0) {
            this.emojiViewVisible = false;
            this.emojiPadding = 0;
            applyEmojiPadding();
        }
        ChatActivityEnterViewAnimatedIconView chatActivityEnterViewAnimatedIconView = this.emojiButton;
        if (chatActivityEnterViewAnimatedIconView != null) {
            chatActivityEnterViewAnimatedIconView.setState(ChatActivityEnterViewAnimatedIconView.State.SMILE, z);
        }
    }

    private void openKeyboardFromPopup() {
        hideEmojiPopup(true);
        RichEditText richEditTextFindFocusedEditText = this.listView.findFocusedEditText();
        if (richEditTextFindFocusedEditText != null) {
            richEditTextFindFocusedEditText.requestEditFocus();
            AndroidUtilities.showKeyboard(richEditTextFindFocusedEditText);
        }
    }

    private int getEmojiPanelHeight() {
        Point point = AndroidUtilities.displaySize;
        int i = point.x > point.y ? this.keyboardHeightLand : this.keyboardHeight;
        return i <= 0 ? AndroidUtilities.dp(200.0f) : i;
    }

    private void applyEmojiPadding() {
        checkUI_listViewPadding();
    }

    public void animateEmojiSearch(boolean z) {
        ValueAnimator valueAnimator = this.emojiSearchAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
            this.emojiSearchAnimator = null;
        }
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.emojiSearchProgress, z ? 1.0f : 0.0f);
        this.emojiSearchAnimator = valueAnimatorOfFloat;
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.iv.RichEditor$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                this.f$0.lambda$animateEmojiSearch$54(valueAnimator2);
            }
        });
        this.emojiSearchAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        this.emojiSearchAnimator.setDuration(250L);
        this.emojiSearchAnimator.start();
    }

    public void m21697$r8$lambda$mRtj85e9Tn6aP8AkyCJUkjcSWs(BottomSheet bottomSheet, Runnable runnable, View view) {
        bottomSheet.lambda$new$0();
        runnable.run();
    }

    public static /* synthetic */ void $r8$lambda$MG8n1tO8Xr5DMXFPt20zxHR_cyk(BottomSheet bottomSheet, Runnable runnable, View view) {
        bottomSheet.lambda$new$0();
        runnable.run();
    }
}
