package org.telegram.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import com.exteragram.messenger.ExteraConfig;
import java.util.ArrayList;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CounterView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.LetterDrawable;
import org.telegram.ui.Components.StaticLayoutEx;

public class ChatPullingDownDrawable implements NotificationCenter.NotificationCenterDelegate {
    boolean animateCheck;
    public boolean animateSwipeToRelease;
    private AnimatedEmojiDrawable animatedEmojiDrawable;
    float bounceProgress;
    StaticLayout chatNameLayout;
    int chatNameWidth;
    float checkProgress;
    float circleRadius;
    private final int currentAccount;
    private final long currentDialog;
    public int dialogFilterId;
    public int dialogFolderId;
    boolean drawFolderBackground;
    boolean emptyStub;
    private final int filterId;
    private final int folderId;
    private final View fragmentView;
    private final ImageReceiver imageReceiver;
    private final boolean isTopic;
    long lastHapticTime;
    float lastProgress;
    public long lastShowingReleaseTime;
    int lastWidth;
    StaticLayout layout1;
    int layout1Width;
    StaticLayout layout2;
    int layout2Width;
    TLRPC.Chat nextChat;
    public long nextDialogId;
    TLRPC.TL_forumTopic nextTopic;
    Runnable onAnimationFinishRunnable;
    View parentView;
    float progressToBottomPanel;
    boolean recommendedChannel;
    private final Theme.ResourcesProvider resourcesProvider;
    AnimatorSet showReleaseAnimator;
    float swipeToReleaseProgress;
    private final long topicId;
    Paint arrowPaint = new Paint(1);
    TextPaint textPaint = new TextPaint(1);
    TextPaint textPaint2 = new TextPaint(1);
    private Paint xRefPaint = new Paint(1);
    Path path = new Path();
    private long lastWidthTopicId = 0;
    private boolean visibleCounterDrawable = true;
    CounterView.CounterDrawable counterDrawable = new CounterView.CounterDrawable(null, true, null);
    int[] params = new int[3];

    public ChatPullingDownDrawable(int i, View view, long j, int i2, int i3, long j2, Theme.ResourcesProvider resourcesProvider) {
        this.fragmentView = view;
        this.currentAccount = i;
        this.currentDialog = j;
        this.folderId = i2;
        this.filterId = i3;
        this.topicId = j2;
        this.isTopic = MessagesController.getInstance(i).isForum(j);
        this.resourcesProvider = resourcesProvider;
        this.imageReceiver = new ImageReceiver(view);
        this.arrowPaint.setStrokeWidth(AndroidUtilities.dpf2(2.8f));
        this.arrowPaint.setStrokeCap(Paint.Cap.ROUND);
        CounterView.CounterDrawable counterDrawable = this.counterDrawable;
        counterDrawable.gravity = 3;
        counterDrawable.setType(1);
        CounterView.CounterDrawable counterDrawable2 = this.counterDrawable;
        counterDrawable2.addServiceGradient = true;
        counterDrawable2.circlePaint = getThemedPaint("paintChatActionBackground");
        CounterView.CounterDrawable counterDrawable3 = this.counterDrawable;
        TextPaint textPaint = this.textPaint;
        counterDrawable3.textPaint = textPaint;
        textPaint.setTextSize(AndroidUtilities.dp(13.0f));
        this.textPaint.setTypeface(AndroidUtilities.bold());
        this.textPaint2.setTextSize(AndroidUtilities.dp(14.0f));
        this.xRefPaint.setColor(-16777216);
        this.xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    public void updateDialog(TLRPC.Chat chat) {
        if (chat == null) {
            updateDialog();
            return;
        }
        this.nextDialogId = -chat.id;
        int[] iArr = this.params;
        this.drawFolderBackground = iArr[0] == 1;
        this.dialogFolderId = iArr[1];
        this.dialogFilterId = iArr[2];
        this.emptyStub = false;
        this.nextChat = chat;
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setInfo(this.currentAccount, this.nextChat);
        this.imageReceiver.setImage(ImageLocation.getForChat(this.nextChat, 1), "50_50", avatarDrawable, null, UserConfig.getInstance(0).getCurrentUser(), 0);
        MessagesController.getInstance(this.currentAccount).ensureMessagesLoaded(-chat.id, 0, null);
        TLRPC.Dialog dialog = MessagesController.getInstance(this.currentAccount).getDialog(-chat.id);
        int i = dialog == null ? 0 : dialog.unread_count;
        this.counterDrawable.setCount(i, false);
        this.visibleCounterDrawable = i > 0;
        this.recommendedChannel = true;
        this.nextTopic = null;
    }

    public void updateDialog() {
        this.recommendedChannel = false;
        this.nextTopic = null;
        TLRPC.Dialog nextUnreadDialog = getNextUnreadDialog(this.currentDialog, this.folderId, this.filterId, true, this.params);
        if (nextUnreadDialog != null) {
            this.nextDialogId = nextUnreadDialog.id;
            int[] iArr = this.params;
            this.drawFolderBackground = iArr[0] == 1;
            this.dialogFolderId = iArr[1];
            this.dialogFilterId = iArr[2];
            this.emptyStub = false;
            TLRPC.Chat chat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-nextUnreadDialog.id));
            this.nextChat = chat;
            if (chat == null) {
                this.nextChat = MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(nextUnreadDialog.id));
            }
            AvatarDrawable avatarDrawable = new AvatarDrawable();
            avatarDrawable.setInfo(this.currentAccount, this.nextChat);
            this.imageReceiver.setImage(ImageLocation.getForChat(this.nextChat, 1), "50_50", avatarDrawable, null, UserConfig.getInstance(0).getCurrentUser(), 0);
            MessagesController.getInstance(this.currentAccount).ensureMessagesLoaded(nextUnreadDialog.id, 0, null);
            int i = nextUnreadDialog.unread_count;
            this.counterDrawable.setCount(i, false);
            this.visibleCounterDrawable = i > 0;
            return;
        }
        this.nextChat = null;
        this.drawFolderBackground = false;
        this.emptyStub = true;
    }

    public void updateTopic() {
        AnimatedEmojiDrawable animatedEmojiDrawable;
        View view;
        View view2;
        AnimatedEmojiDrawable animatedEmojiDrawable2;
        this.recommendedChannel = false;
        this.drawFolderBackground = false;
        this.nextChat = null;
        this.nextDialogId = 0L;
        this.imageReceiver.clearImage();
        TLRPC.TL_forumTopic nextUnreadTopic = getNextUnreadTopic(-this.currentDialog);
        if (nextUnreadTopic != null) {
            this.emptyStub = false;
            this.nextTopic = nextUnreadTopic;
            if (nextUnreadTopic.id == 1) {
                View view3 = this.parentView;
                if (view3 != null && (animatedEmojiDrawable2 = this.animatedEmojiDrawable) != null) {
                    animatedEmojiDrawable2.removeView(view3);
                }
                this.animatedEmojiDrawable = null;
                this.imageReceiver.setImageBitmap(ForumUtilities.createGeneralTopicDrawable(this.fragmentView.getContext(), 1.0f, getThemedColor(Theme.key_chat_inMenu), false, true));
            } else if (nextUnreadTopic.icon_emoji_id != 0) {
                AnimatedEmojiDrawable animatedEmojiDrawable3 = this.animatedEmojiDrawable;
                if (animatedEmojiDrawable3 == null || animatedEmojiDrawable3.getDocumentId() != nextUnreadTopic.icon_emoji_id) {
                    AnimatedEmojiDrawable animatedEmojiDrawable4 = this.animatedEmojiDrawable;
                    if (animatedEmojiDrawable4 != null && (view = this.parentView) != null) {
                        animatedEmojiDrawable4.removeView(view);
                    }
                    AnimatedEmojiDrawable animatedEmojiDrawable5 = new AnimatedEmojiDrawable(22, this.currentAccount, nextUnreadTopic.icon_emoji_id);
                    this.animatedEmojiDrawable = animatedEmojiDrawable5;
                    animatedEmojiDrawable5.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_chat_serviceText), PorterDuff.Mode.SRC_IN));
                }
                AnimatedEmojiDrawable animatedEmojiDrawable6 = this.animatedEmojiDrawable;
                if (animatedEmojiDrawable6 != null && (view2 = this.parentView) != null) {
                    animatedEmojiDrawable6.addView(view2);
                }
                this.imageReceiver.setImageBitmap((Bitmap) null);
            } else {
                View view4 = this.parentView;
                if (view4 != null && (animatedEmojiDrawable = this.animatedEmojiDrawable) != null) {
                    animatedEmojiDrawable.removeView(view4);
                }
                this.animatedEmojiDrawable = null;
                this.imageReceiver.setImageBitmap(ForumUtilities.createTopicDrawable(nextUnreadTopic, false));
            }
            int i = nextUnreadTopic.unread_count;
            this.counterDrawable.setCount(i, false);
            this.visibleCounterDrawable = i > 0;
            return;
        }
        this.nextTopic = null;
        this.emptyStub = true;
    }

    public void setWidth(int i) {
        String string;
        String string2;
        String string3;
        int i2;
        TLRPC.TL_forumTopic tL_forumTopic;
        if (i == this.lastWidth && (!this.isTopic || (tL_forumTopic = this.nextTopic) == null || this.lastWidthTopicId == tL_forumTopic.id)) {
            return;
        }
        this.circleRadius = AndroidUtilities.dp(56.0f) / 2.0f;
        this.lastWidth = i;
        TLRPC.Chat chat = this.nextChat;
        if (chat != null) {
            string = chat.title;
        } else {
            TLRPC.TL_forumTopic tL_forumTopic2 = this.nextTopic;
            if (tL_forumTopic2 != null) {
                string = tL_forumTopic2.title;
            } else if (this.isTopic) {
                string = LocaleController.formatString(R.string.SwipeToGoNextTopicEnd, MessagesController.getInstance(this.currentAccount).getChat(Long.valueOf(-this.currentDialog)).title);
            } else {
                string = LocaleController.getString(R.string.SwipeToGoNextChannelEnd);
            }
        }
        String str = string;
        int iMeasureText = (int) this.textPaint.measureText((CharSequence) str, 0, str.length());
        this.chatNameWidth = iMeasureText;
        int iMin = Math.min(iMeasureText, this.lastWidth - AndroidUtilities.dp(60.0f));
        this.chatNameWidth = iMin;
        this.chatNameLayout = StaticLayoutEx.createStaticLayout(str, this.textPaint, iMin, Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false, TextUtils.TruncateAt.END, iMin, 1);
        if (this.recommendedChannel) {
            string2 = LocaleController.getString(R.string.SwipeToGoNextRecommendedChannel);
            string3 = LocaleController.getString(R.string.ReleaseToGoNextRecommendedChannel);
        } else if (this.isTopic) {
            string2 = LocaleController.getString(R.string.SwipeToGoNextUnreadTopic);
            string3 = LocaleController.getString(R.string.ReleaseToGoNextUnreadTopic);
        } else {
            boolean z = this.drawFolderBackground;
            if (z && (i2 = this.dialogFolderId) != this.folderId && i2 != 0) {
                string2 = LocaleController.getString(R.string.SwipeToGoNextArchive);
                string3 = LocaleController.getString(R.string.ReleaseToGoNextArchive);
            } else if (z) {
                string2 = LocaleController.getString(R.string.SwipeToGoNextFolder);
                string3 = LocaleController.getString(R.string.ReleaseToGoNextFolder);
            } else {
                string2 = LocaleController.getString(R.string.SwipeToGoNextChannel);
                string3 = LocaleController.getString(R.string.ReleaseToGoNextChannel);
            }
        }
        String str2 = string2;
        int iMeasureText2 = (int) this.textPaint2.measureText(str2);
        this.layout1Width = iMeasureText2;
        this.layout1Width = Math.min(iMeasureText2, this.lastWidth - AndroidUtilities.dp(60.0f));
        TextPaint textPaint = this.textPaint2;
        int i3 = this.layout1Width;
        Layout.Alignment alignment = Layout.Alignment.ALIGN_CENTER;
        this.layout1 = new StaticLayout(str2, textPaint, i3, alignment, 1.0f, 0.0f, false);
        int iMeasureText3 = (int) this.textPaint2.measureText(string3);
        this.layout2Width = iMeasureText3;
        this.layout2Width = Math.min(iMeasureText3, this.lastWidth - AndroidUtilities.dp(60.0f));
        this.layout2 = new StaticLayout(string3, this.textPaint2, this.layout2Width, alignment, 1.0f, 0.0f, false);
        this.imageReceiver.setImageCoords((this.lastWidth / 2.0f) - (AndroidUtilities.dp(40.0f) / 2.0f), (AndroidUtilities.dp(12.0f) + this.circleRadius) - (AndroidUtilities.dp(40.0f) / 2.0f), AndroidUtilities.dp(40.0f), AndroidUtilities.dp(40.0f));
        this.imageReceiver.setRoundRadius(ExteraConfig.getAvatarCorners(40.0f));
        this.counterDrawable.setSize(AndroidUtilities.dp(28.0f), AndroidUtilities.dp(100.0f));
        if (this.isTopic) {
            TLRPC.TL_forumTopic tL_forumTopic3 = this.nextTopic;
            this.lastWidthTopicId = tL_forumTopic3 == null ? 0L : tL_forumTopic3.id;
        }
    }

    public void draw(Canvas canvas, View view, float f, float f2) {
        float f3;
        float f4;
        float f5;
        float f6;
        String str;
        ImageReceiver imageReceiver;
        if (this.parentView != view) {
            this.parentView = view;
            AnimatedEmojiDrawable animatedEmojiDrawable = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable != null) {
                animatedEmojiDrawable.addView(view);
            }
        }
        this.counterDrawable.setParent(view);
        float fDp = AndroidUtilities.dp(110.0f) * f;
        if (fDp < AndroidUtilities.dp(8.0f)) {
            return;
        }
        float f7 = f < 0.2f ? 5.0f * f * f2 : f2;
        Theme.applyServiceShaderMatrix(this.lastWidth, view.getMeasuredHeight(), 0.0f, view.getMeasuredHeight() - fDp);
        TextPaint textPaint = this.textPaint;
        int i = Theme.key_chat_serviceText;
        textPaint.setColor(getThemedColor(i));
        this.arrowPaint.setColor(getThemedColor(i));
        this.textPaint2.setColor(getThemedColor(Theme.key_chat_messagePanelHint));
        int alpha = getThemedPaint("paintChatActionBackground").getAlpha();
        int alpha2 = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
        int alpha3 = this.textPaint.getAlpha();
        int alpha4 = this.arrowPaint.getAlpha();
        Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (alpha2 * f7));
        int i2 = (int) (alpha * f7);
        getThemedPaint("paintChatActionBackground").setAlpha(i2);
        int i3 = (int) (alpha3 * f7);
        this.textPaint.setAlpha(i3);
        if ((f < 1.0f || this.lastProgress >= 1.0f) && (f >= 1.0f || this.lastProgress != 1.0f)) {
            f3 = 1.0f;
        } else {
            long jCurrentTimeMillis = System.currentTimeMillis();
            f3 = 1.0f;
            if (jCurrentTimeMillis - this.lastHapticTime > 100) {
                try {
                    view.performHapticFeedback(3, 2);
                } catch (Exception unused) {
                }
                this.lastHapticTime = jCurrentTimeMillis;
            }
            this.lastProgress = f;
        }
        if (f == 1.0f && !this.animateSwipeToRelease) {
            this.animateSwipeToRelease = true;
            this.animateCheck = true;
            showReleaseState(true, view);
            this.lastShowingReleaseTime = System.currentTimeMillis();
        } else if (f != 1.0f && this.animateSwipeToRelease) {
            this.animateSwipeToRelease = false;
            showReleaseState(false, view);
        }
        float f8 = this.lastWidth / 2.0f;
        float f9 = this.bounceProgress * (-AndroidUtilities.dp(4.0f));
        if (this.emptyStub) {
            fDp -= f9;
        }
        float f10 = fDp / 2.0f;
        float fMax = Math.max(0.0f, Math.min(this.circleRadius, (f10 - (AndroidUtilities.dp(16.0f) * f)) - AndroidUtilities.dp(4.0f)));
        float fMax2 = ((Math.max(0.0f, Math.min(this.circleRadius * f, f10 - (AndroidUtilities.dp(8.0f) * f))) * 2.0f) - AndroidUtilities.dp2(16.0f)) * (f3 - this.swipeToReleaseProgress);
        float fDp2 = AndroidUtilities.dp(56.0f);
        float f11 = this.swipeToReleaseProgress;
        float f12 = fMax2 + (fDp2 * f11);
        if (f11 < f3 || this.emptyStub) {
            f4 = 36.0f;
            float f13 = -fDp;
            float fDp3 = ((-AndroidUtilities.dp(8.0f)) * (f3 - this.swipeToReleaseProgress)) + (this.swipeToReleaseProgress * (AndroidUtilities.dp(56.0f) + f13));
            RectF rectF = AndroidUtilities.rectTmp;
            f5 = f9;
            rectF.set(f8 - fMax, f13, f8 + fMax, fDp3);
            if (this.swipeToReleaseProgress <= 0.0f || this.emptyStub) {
                f6 = f3;
            } else {
                float fDp4 = AndroidUtilities.dp(16.0f) * this.swipeToReleaseProgress;
                rectF.inset(fDp4, fDp4);
                f6 = f3 - this.swipeToReleaseProgress;
            }
            drawBackground(canvas, rectF, f6);
            float fDp5 = ((AndroidUtilities.dp(24.0f) + f13) + (AndroidUtilities.dp(8.0f) * (f3 - f))) - (AndroidUtilities.dp(36.0f) * this.swipeToReleaseProgress);
            canvas.save();
            rectF.inset(AndroidUtilities.dp(f3), AndroidUtilities.dp(f3));
            canvas.clipRect(rectF);
            float f14 = this.swipeToReleaseProgress;
            if (f14 > 0.0f) {
                this.arrowPaint.setAlpha((int) ((f3 - f14) * 255.0f));
            }
            drawArrow(canvas, f8, fDp5, AndroidUtilities.dp(24.0f) * f);
            if (this.emptyStub) {
                float fDp6 = ((((-AndroidUtilities.dp(8.0f)) - (AndroidUtilities.dp2(8.0f) * f)) - f12) * (f3 - this.swipeToReleaseProgress)) + ((f13 - AndroidUtilities.dp(2.0f)) * this.swipeToReleaseProgress) + f5;
                this.arrowPaint.setAlpha(alpha4);
                canvas.save();
                canvas.scale(f, f, f8, AndroidUtilities.dp(28.0f) + fDp6);
                drawCheck(canvas, f8, fDp6 + AndroidUtilities.dp(28.0f));
                canvas.restore();
            }
            canvas.restore();
        } else {
            f5 = f9;
            f4 = 36.0f;
        }
        if (this.chatNameLayout == null || this.swipeToReleaseProgress <= 0.0f) {
            str = r8;
        } else {
            str = "paintChatActionBackground";
            getThemedPaint(str).setAlpha(i2);
            this.textPaint.setAlpha(i3);
            float fDp7 = ((AndroidUtilities.dp(20.0f) * (f3 - this.swipeToReleaseProgress)) - (AndroidUtilities.dp(f4) * this.swipeToReleaseProgress)) + f5;
            RectF rectF2 = AndroidUtilities.rectTmp;
            int i4 = this.lastWidth;
            int i5 = this.chatNameWidth;
            rectF2.set((i4 - i5) / 2.0f, fDp7, i4 - ((i4 - i5) / 2.0f), this.chatNameLayout.getHeight() + fDp7);
            rectF2.inset(-AndroidUtilities.dp(8.0f), -AndroidUtilities.dp(4.0f));
            canvas.drawRoundRect(rectF2, ExteraConfig.getAvatarCorners(rectF2.width(), true), ExteraConfig.getAvatarCorners(rectF2.width(), true), getThemedPaint(str));
            if (hasGradientService()) {
                canvas.drawRoundRect(rectF2, ExteraConfig.getAvatarCorners(rectF2.width(), true), ExteraConfig.getAvatarCorners(rectF2.width(), true), Theme.chat_actionBackgroundGradientDarkenPaint);
            }
            canvas.save();
            canvas.translate((this.lastWidth - this.chatNameWidth) / 2.0f, fDp7);
            this.chatNameLayout.draw(canvas);
            canvas.restore();
        }
        if (!this.emptyStub && f12 > 0.0f) {
            float fDp8 = ((((-AndroidUtilities.dp(8.0f)) - (AndroidUtilities.dp2(8.0f) * f)) - f12) * (f3 - this.swipeToReleaseProgress)) + (((-fDp) + AndroidUtilities.dp(4.0f)) * this.swipeToReleaseProgress) + f5;
            AnimatedEmojiDrawable animatedEmojiDrawable2 = this.animatedEmojiDrawable;
            if (animatedEmojiDrawable2 != null && animatedEmojiDrawable2.getImageReceiver() != null) {
                imageReceiver = this.animatedEmojiDrawable.getImageReceiver();
            } else {
                imageReceiver = this.imageReceiver;
            }
            ImageReceiver imageReceiver2 = imageReceiver;
            imageReceiver2.setAlpha(f7);
            imageReceiver2.setRoundRadius(ExteraConfig.getAvatarCorners(f12, true));
            imageReceiver2.setImageCoords(f8 - (f12 / 2.0f), fDp8, f12, f12);
            if (this.isTopic && imageReceiver2.getDrawable() != null && (imageReceiver2.getDrawable() instanceof CombinedDrawable) && (((CombinedDrawable) imageReceiver2.getDrawable()).getIcon() instanceof LetterDrawable)) {
                ((LetterDrawable) ((CombinedDrawable) imageReceiver2.getDrawable()).getIcon()).scale = f;
            }
            if (this.swipeToReleaseProgress > 0.0f && this.visibleCounterDrawable) {
                canvas.saveLayerAlpha(imageReceiver2.getImageX(), imageReceiver2.getImageY(), imageReceiver2.getImageX() + imageReceiver2.getImageWidth(), imageReceiver2.getImageY() + imageReceiver2.getImageHeight(), 255, 31);
                imageReceiver2.draw(canvas);
                float f15 = this.swipeToReleaseProgress;
                canvas.scale(f15, f15, AndroidUtilities.dp(12.0f) + f8 + this.counterDrawable.getCenterX(), (fDp8 - AndroidUtilities.dp(6.0f)) + AndroidUtilities.dp(14.0f));
                canvas.translate(AndroidUtilities.dp(12.0f) + f8, fDp8 - AndroidUtilities.dp(6.0f));
                this.counterDrawable.updateBackgroundRect();
                this.counterDrawable.rectF.inset(-AndroidUtilities.dp(2.0f), -AndroidUtilities.dp(2.0f));
                RectF rectF3 = this.counterDrawable.rectF;
                canvas.drawRoundRect(rectF3, rectF3.height() / 2.0f, this.counterDrawable.rectF.height() / 2.0f, this.xRefPaint);
                canvas.restore();
                canvas.save();
                float f16 = this.swipeToReleaseProgress;
                canvas.scale(f16, f16, AndroidUtilities.dp(12.0f) + f8 + this.counterDrawable.getCenterX(), (fDp8 - AndroidUtilities.dp(6.0f)) + AndroidUtilities.dp(14.0f));
                canvas.translate(AndroidUtilities.dp(12.0f) + f8, fDp8 - AndroidUtilities.dp(6.0f));
                this.counterDrawable.draw(canvas);
                canvas.restore();
            } else {
                imageReceiver2.draw(canvas);
            }
            imageReceiver2.setAlpha(f3);
        }
        getThemedPaint(str).setAlpha(alpha);
        Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(alpha2);
        this.textPaint.setAlpha(alpha3);
        this.arrowPaint.setAlpha(alpha4);
    }

    private void drawCheck(Canvas canvas, float f, float f2) {
        if (this.animateCheck) {
            float f3 = this.checkProgress;
            if (f3 < 1.0f) {
                float f4 = f3 + 0.07272727f;
                this.checkProgress = f4;
                if (f4 > 1.0f) {
                    this.checkProgress = 1.0f;
                }
            }
            float f5 = this.checkProgress;
            float f6 = f5 > 0.5f ? 1.0f : f5 / 0.5f;
            float f7 = f5 < 0.5f ? 0.0f : (f5 - 0.5f) / 0.5f;
            canvas.save();
            canvas.clipRect(AndroidUtilities.rectTmp);
            canvas.translate(f - AndroidUtilities.dp(24.0f), f2 - AndroidUtilities.dp(24.0f));
            float fDp = AndroidUtilities.dp(16.0f);
            float fDp2 = AndroidUtilities.dp(26.0f);
            float fDp3 = AndroidUtilities.dp(22.0f);
            float fDp4 = AndroidUtilities.dp(32.0f);
            float fDp5 = AndroidUtilities.dp(32.0f);
            float fDp6 = AndroidUtilities.dp(20.0f);
            float f8 = 1.0f - f6;
            canvas.drawLine(fDp, fDp2, (fDp * f8) + (fDp3 * f6), (f8 * fDp2) + (f6 * fDp4), this.arrowPaint);
            if (f7 > 0.0f) {
                float f9 = 1.0f - f7;
                canvas.drawLine(fDp3, fDp4, (fDp3 * f9) + (fDp5 * f7), (f9 * fDp4) + (fDp6 * f7), this.arrowPaint);
            }
            canvas.restore();
        }
    }

    private void drawBackground(Canvas canvas, RectF rectF, float f) {
        if (this.drawFolderBackground) {
            this.path.reset();
            float fWidth = rectF.width() * 0.2f;
            float fWidth2 = rectF.width() * 0.1f;
            float fWidth3 = rectF.width() * 0.03f;
            float f2 = fWidth2 / 2.0f;
            float fHeight = rectF.height() - fWidth2;
            this.path.moveTo(rectF.right, rectF.top + fWidth + fWidth2);
            float f3 = -fWidth;
            this.path.rQuadTo(0.0f, f3, f3, f3);
            float f4 = fWidth * 2.0f;
            float f5 = f2 * 2.0f;
            this.path.rLineTo((((-(rectF.width() - f4)) / 2.0f) + f5) - fWidth3, 0.0f);
            float f6 = -f2;
            float f7 = f6 / 2.0f;
            float f8 = f6 * 2.0f;
            float f9 = (-fWidth2) / 2.0f;
            this.path.rQuadTo(f7, 0.0f, f8, f9);
            this.path.rQuadTo(f7, f9, f8, f9);
            this.path.rLineTo(((-(rectF.width() - f4)) / 2.0f) + f5 + fWidth3, 0.0f);
            this.path.rQuadTo(f3, 0.0f, f3, fWidth);
            this.path.rLineTo(0.0f, (fWidth2 + fHeight) - f4);
            this.path.rQuadTo(0.0f, fWidth, fWidth, fWidth);
            this.path.rLineTo(rectF.width() - f4, 0.0f);
            this.path.rQuadTo(fWidth, 0.0f, fWidth, f3);
            this.path.rLineTo(0.0f, -(fHeight - f4));
            this.path.close();
            canvas.drawPath(this.path, getThemedPaint("paintChatActionBackground"));
            if (hasGradientService()) {
                canvas.drawPath(this.path, Theme.chat_actionBackgroundGradientDarkenPaint);
                return;
            }
            return;
        }
        int alpha = getThemedPaint("paintChatActionBackground").getAlpha();
        getThemedPaint("paintChatActionBackground").setAlpha((int) (alpha * f));
        RectF rectF2 = AndroidUtilities.rectTmp;
        canvas.drawRoundRect(rectF2, ExteraConfig.getAvatarCorners(rectF2.width(), true), ExteraConfig.getAvatarCorners(rectF2.width(), true), getThemedPaint("paintChatActionBackground"));
        getThemedPaint("paintChatActionBackground").setAlpha(alpha);
        if (hasGradientService()) {
            int alpha2 = Theme.chat_actionBackgroundGradientDarkenPaint.getAlpha();
            Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha((int) (alpha2 * f));
            canvas.drawRoundRect(rectF2, ExteraConfig.getAvatarCorners(rectF2.width(), true), ExteraConfig.getAvatarCorners(rectF2.width(), true), Theme.chat_actionBackgroundGradientDarkenPaint);
            Theme.chat_actionBackgroundGradientDarkenPaint.setAlpha(alpha2);
        }
    }

    private void showReleaseState(boolean z, final View view) {
        AnimatorSet animatorSet = this.showReleaseAnimator;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.showReleaseAnimator.cancel();
        }
        float f = this.swipeToReleaseProgress;
        if (z) {
            ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(f, 1.0f);
            valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatPullingDownDrawable$$ExternalSyntheticLambda2
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$showReleaseState$0(view, valueAnimator);
                }
            });
            valueAnimatorOfFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            valueAnimatorOfFloat.setDuration(250L);
            this.bounceProgress = 0.0f;
            ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
            valueAnimatorOfFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatPullingDownDrawable$$ExternalSyntheticLambda3
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$showReleaseState$1(view, valueAnimator);
                }
            });
            CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_BOTH;
            valueAnimatorOfFloat2.setInterpolator(cubicBezierInterpolator);
            valueAnimatorOfFloat2.setDuration(180L);
            ValueAnimator valueAnimatorOfFloat3 = ValueAnimator.ofFloat(1.0f, -0.5f);
            valueAnimatorOfFloat3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatPullingDownDrawable$$ExternalSyntheticLambda4
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$showReleaseState$2(view, valueAnimator);
                }
            });
            valueAnimatorOfFloat3.setInterpolator(cubicBezierInterpolator);
            valueAnimatorOfFloat3.setDuration(120L);
            ValueAnimator valueAnimatorOfFloat4 = ValueAnimator.ofFloat(-0.5f, 0.0f);
            valueAnimatorOfFloat4.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatPullingDownDrawable$$ExternalSyntheticLambda5
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                    this.f$0.lambda$showReleaseState$3(view, valueAnimator);
                }
            });
            valueAnimatorOfFloat4.setInterpolator(cubicBezierInterpolator);
            valueAnimatorOfFloat4.setDuration(100L);
            AnimatorSet animatorSet2 = new AnimatorSet();
            this.showReleaseAnimator = animatorSet2;
            animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.ChatPullingDownDrawable.1
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    ChatPullingDownDrawable chatPullingDownDrawable = ChatPullingDownDrawable.this;
                    chatPullingDownDrawable.bounceProgress = 0.0f;
                    chatPullingDownDrawable.swipeToReleaseProgress = 1.0f;
                    view.invalidate();
                    ChatPullingDownDrawable.this.fragmentView.invalidate();
                    Runnable runnable = ChatPullingDownDrawable.this.onAnimationFinishRunnable;
                    if (runnable != null) {
                        runnable.run();
                        ChatPullingDownDrawable.this.onAnimationFinishRunnable = null;
                    }
                }
            });
            AnimatorSet animatorSet3 = new AnimatorSet();
            animatorSet3.playSequentially(valueAnimatorOfFloat2, valueAnimatorOfFloat3, valueAnimatorOfFloat4);
            this.showReleaseAnimator.playTogether(valueAnimatorOfFloat, animatorSet3);
            this.showReleaseAnimator.start();
            return;
        }
        ValueAnimator valueAnimatorOfFloat5 = ValueAnimator.ofFloat(f, 0.0f);
        valueAnimatorOfFloat5.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.ChatPullingDownDrawable$$ExternalSyntheticLambda6
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$showReleaseState$4(view, valueAnimator);
            }
        });
        valueAnimatorOfFloat5.setInterpolator(CubicBezierInterpolator.DEFAULT);
        valueAnimatorOfFloat5.setDuration(220L);
        AnimatorSet animatorSet4 = new AnimatorSet();
        this.showReleaseAnimator = animatorSet4;
        animatorSet4.playTogether(valueAnimatorOfFloat5);
        this.showReleaseAnimator.start();
    }

    public void lambda$runOnAnimationFinish$5(ValueAnimator valueAnimator) {
        this.swipeToReleaseProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.fragmentView.invalidate();
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public /* synthetic */ void lambda$runOnAnimationFinish$6(ValueAnimator valueAnimator) {
        this.bounceProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.parentView;
        if (view != null) {
            view.invalidate();
        }
    }

    public void reset() {
        this.checkProgress = 0.0f;
        this.animateCheck = false;
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    private Paint getThemedPaint(String str) {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        Paint paint = resourcesProvider != null ? resourcesProvider.getPaint(str) : null;
        return paint != null ? paint : Theme.getThemePaint(str);
    }

    private boolean hasGradientService() {
        Theme.ResourcesProvider resourcesProvider = this.resourcesProvider;
        return resourcesProvider != null ? resourcesProvider.hasGradientService() : Theme.hasGradientService();
    }
}
