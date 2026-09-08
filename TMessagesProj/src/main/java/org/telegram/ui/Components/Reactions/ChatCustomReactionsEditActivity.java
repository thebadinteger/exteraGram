package org.telegram.ui.Components.Reactions;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.Editable;
import android.text.Layout;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stars;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AdjustPanLayoutHelper;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Cells.HeaderCell;
import org.telegram.ui.Cells.SlideIntChooseView;
import org.telegram.ui.Cells.TextCheckCell;
import org.telegram.ui.Cells.TextInfoPrivacyCell;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedEmojiSpan;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.SectionsScrollView;
import org.telegram.ui.SelectAnimatedEmojiDialog;

public class ChatCustomReactionsEditActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate {
    private UpdateReactionsButton actionButton;
    private FrameLayout actionButtonContainer;
    private ImageView actionButtonContainerGradient;
    private BackSpaceButtonView backSpaceButtonView;
    private TL_stories.TL_premium_boostsStatus boostsStatus;
    private FrameLayout bottomDialogLayout;
    private final long chatId;
    private LinearLayout contentLayout;
    private TLRPC.Chat currentChat;
    private int currentReactionsCount;
    private CustomReactionEditText editText;
    private TextCheckCell enableReactionsCell;
    private final TLRPC.ChatFull info;
    private boolean initialPaid;
    private boolean isPaused;
    private boolean paid;
    private TextCheckCell paidCheckCell;
    private int reactionsCount;
    private SectionsScrollView scrollView;
    private SelectAnimatedEmojiDialog selectAnimatedEmojiDialog;
    private int selectedCustomReactions;
    private SlideIntChooseView slideView;
    private LinearLayout switchLayout;
    private final HashMap<Long, AnimatedEmojiSpan> selectedEmojisMap = new LinkedHashMap();
    private final List<Long> selectedEmojisIds = new ArrayList();
    private final HashMap<Long, AnimatedEmojiSpan> initialSelectedEmojis = new LinkedHashMap();
    private final List<TLRPC.TL_availableReaction> allAvailableReactions = new ArrayList();
    private final int maxReactionsCount = getMessagesController().boostsChannelLevelMax;
    private boolean emojiKeyboardVisible = false;
    private int selectedType = -1;
    private final Runnable checkAfterFastDeleteRunnable = new Runnable() { // from class: org.telegram.ui.Components.Reactions.ChatCustomReactionsEditActivity$$ExternalSyntheticLambda9
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.lambda$new$0();
        }
    };

    public void lambda$createView$2(View view) {
        TextCheckCell textCheckCell;
        if (this.enableReactionsCell.isChecked() && (textCheckCell = this.paidCheckCell) != null && textCheckCell.isChecked()) {
            toggleStarsEnabled();
        }
        setCheckedEnableReactionCell(this.enableReactionsCell.isChecked() ? 2 : 1, this.enableReactionsCell.isChecked() ? false : this.paid, true);
    }

    public void lambda$createView$8(final TLRPC.TL_error tL_error) {
        if (isFinishing()) {
            return;
        }
        this.actionButton.setLoading(false);
        if (tL_error.text.equals("CHAT_NOT_MODIFIED")) {
            finishFragment();
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Components.Reactions.ChatCustomReactionsEditActivity$$ExternalSyntheticLambda20
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$createView$7(tL_error);
                }
            }, this.boostsStatus == null ? 200L : 0L);
        }
    }

    public void lambda$onEmojiSelected$0(AnimatedEmojiSpan animatedEmojiSpan) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(ChatCustomReactionsEditActivity.this.editText.getText());
            for (AnimatedEmojiSpan animatedEmojiSpan2 : (AnimatedEmojiSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), AnimatedEmojiSpan.class)) {
                if (animatedEmojiSpan2 == animatedEmojiSpan) {
                    int editTextSelectionEnd = ChatCustomReactionsEditActivity.this.editText.getEditTextSelectionEnd();
                    int spanEnd = spannableStringBuilder.getSpanEnd(animatedEmojiSpan2);
                    int spanStart = spannableStringBuilder.getSpanStart(animatedEmojiSpan2);
                    ChatCustomReactionsEditActivity.this.editText.getText().delete(spanStart, spanEnd);
                    int i = spanEnd - spanStart;
                    CustomReactionEditText customReactionEditText = ChatCustomReactionsEditActivity.this.editText;
                    if (spanEnd <= editTextSelectionEnd) {
                        editTextSelectionEnd -= i;
                    }
                    customReactionEditText.setSelection(editTextSelectionEnd);
                    return;
                }
            }
        }
    }

    public void lambda$toggleStarsEnabled$18(AnimatedEmojiSpan animatedEmojiSpan) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(this.editText.getText());
        for (AnimatedEmojiSpan animatedEmojiSpan2 : (AnimatedEmojiSpan[]) spannableStringBuilder.getSpans(0, spannableStringBuilder.length(), AnimatedEmojiSpan.class)) {
            if (animatedEmojiSpan2 == animatedEmojiSpan) {
                int editTextSelectionEnd = this.editText.getEditTextSelectionEnd();
                int spanEnd = spannableStringBuilder.getSpanEnd(animatedEmojiSpan2);
                int spanStart = spannableStringBuilder.getSpanStart(animatedEmojiSpan2);
                this.editText.getText().delete(spanStart, spanEnd);
                int i = spanEnd - spanStart;
                CustomReactionEditText customReactionEditText = this.editText;
                if (spanEnd <= editTextSelectionEnd) {
                    editTextSelectionEnd -= i;
                }
                customReactionEditText.setSelection(editTextSelectionEnd);
                return;
            }
        }
    }
}
