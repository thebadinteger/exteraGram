package org.telegram.ui.Cells;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.text.Layout;
import android.text.TextUtils;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatValueHolder;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import com.exteragram.messenger.ExteraConfig;
import java.util.Objects;
import kotlin.jvm.internal.LongCompanionObject;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.CheckBox2;
import org.telegram.ui.Components.CheckBoxBase;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.Forum.ForumUtilities;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.PremiumGradient;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.Text;
import org.telegram.ui.Stars.StarsIntroActivity;

public class ShareDialogCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate {
    private final AvatarDrawable avatarDrawable;
    private final CheckBox2 checkBox;
    private final int currentAccount;
    private long currentDialog;
    private final int currentType;
    private final BackupImageView imageView;
    private long lastUpdateTime;
    private Drawable lockDrawable;
    private final TextView nameTextView;
    private float onlineProgress;
    private boolean premiumBlocked;
    private final AnimatedFloat premiumBlockedT;
    private PremiumGradient.PremiumGradientTools premiumGradient;
    private final Paint priceBackgroundPaint;
    private Text priceText;
    private long priceTextValue;
    private RepostStoryDrawable repostStoryDrawable;
    public final Theme.ResourcesProvider resourcesProvider;
    private final AnimatedFloat starsBlockedT;
    private long starsPriceBlocked;
    private final SimpleTextView topicTextView;
    private boolean topicWasVisible;
    private TLRPC.User user;

    public boolean isBlocked() {
        return this.premiumBlocked;
    }

    public long getStarsPrice() {
        return this.starsPriceBlocked;
    }

    public BackupImageView getImageView() {
        return this.imageView;
    }

    public ShareDialogCell(Context context, int i, Theme.ResourcesProvider resourcesProvider) {
        super(context);
        this.currentAccount = UserConfig.selectedAccount;
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_OUT_QUINT;
        this.premiumBlockedT = new AnimatedFloat(this, 0L, 350L, cubicBezierInterpolator);
        this.starsBlockedT = new AnimatedFloat(this, 0L, 350L, cubicBezierInterpolator);
        this.priceBackgroundPaint = new Paint();
        this.resourcesProvider = resourcesProvider;
        this.avatarDrawable = new AvatarDrawable(resourcesProvider) { // from class: org.telegram.ui.Cells.ShareDialogCell.1
            @Override // android.graphics.drawable.Drawable
            public void invalidateSelf() {
                super.invalidateSelf();
                ShareDialogCell.this.imageView.invalidate();
            }
        };
        setWillNotDraw(false);
        this.currentType = i;
        BackupImageView backupImageView = new BackupImageView(context);
        this.imageView = backupImageView;
        if (i == 2) {
            backupImageView.setRoundRadius(ExteraConfig.getAvatarCorners(48.0f));
            addView(backupImageView, LayoutHelper.createFrame(48, 48.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        } else {
            backupImageView.setRoundRadius(ExteraConfig.getAvatarCorners(56.0f));
            addView(backupImageView, LayoutHelper.createFrame(56, 56.0f, 49, 0.0f, 7.0f, 0.0f, 0.0f));
        }
        TextView textView = new TextView(context) { // from class: org.telegram.ui.Cells.ShareDialogCell.2
            @Override // android.widget.TextView
            public void setText(CharSequence charSequence, TextView.BufferType bufferType) {
                super.setText(Emoji.replaceEmoji(charSequence, getPaint().getFontMetricsInt(), false), bufferType);
            }
        };
        this.nameTextView = textView;
        NotificationCenter.listenEmojiLoading(textView);
        textView.setTextColor(getThemedColor(this.premiumBlocked ? Theme.key_windowBackgroundWhiteGrayText5 : Theme.key_dialogTextBlack));
        textView.setTextSize(1, 12.0f);
        textView.setMaxLines(2);
        textView.setGravity(49);
        textView.setLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        addView(textView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, i == 2 ? 58.0f : 66.0f, 6.0f, 0.0f));
        SimpleTextView simpleTextView = new SimpleTextView(context);
        this.topicTextView = simpleTextView;
        simpleTextView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        simpleTextView.setTextSize(12);
        simpleTextView.setMaxLines(2);
        simpleTextView.setGravity(49);
        simpleTextView.setAlignment(Layout.Alignment.ALIGN_CENTER);
        addView(simpleTextView, LayoutHelper.createFrame(-1, -2.0f, 51, 6.0f, i == 2 ? 58.0f : 66.0f, 6.0f, 0.0f));
        CheckBox2 checkBox2 = new CheckBox2(context, 21, resourcesProvider);
        this.checkBox = checkBox2;
        checkBox2.setColor(Theme.key_dialogRoundCheckBox, Theme.key_dialogBackground, Theme.key_dialogRoundCheckBoxCheck);
        checkBox2.setDrawUnchecked(false);
        checkBox2.setDrawBackgroundAsArc(4);
        checkBox2.setProgressDelegate(new CheckBoxBase.ProgressDelegate() { // from class: org.telegram.ui.Cells.ShareDialogCell$$ExternalSyntheticLambda0
            @Override // org.telegram.ui.Components.CheckBoxBase.ProgressDelegate
            public final void setProgress(float f) {
                this.f$0.lambda$new$0(f);
            }
        });
        addView(checkBox2, LayoutHelper.createFrame(24, 24.0f, 49, 19.0f, i == 2 ? -40.0f : 42.0f, 0.0f, 0.0f));
        setBackground(Theme.createRadSelectorDrawable(Theme.getColor(Theme.key_listSelector, resourcesProvider), AndroidUtilities.dp(2.0f), AndroidUtilities.dp(2.0f)));
    }

    public void lambda$setTopic$1(DynamicAnimation dynamicAnimation, float f, float f2) {
        float f3 = f / 1000.0f;
        this.topicTextView.setAlpha(f3);
        float f4 = 1.0f - f3;
        this.nameTextView.setAlpha(f4);
        this.topicTextView.setTranslationX(f4 * (-AndroidUtilities.dp(10.0f)));
        this.nameTextView.setTranslationX(f3 * AndroidUtilities.dp(10.0f));
    }

    public /* synthetic */ void lambda$setTopic$2(DynamicAnimation dynamicAnimation, boolean z, float f, float f2) {
        this.topicTextView.setTag(R.id.spring_tag, null);
    }

    /* JADX WARN: Code duplicated, block: B:31:0x00c5  */
    /* JADX WARN: Code duplicated, block: B:32:0x00c8  */
    /* JADX WARN: Code duplicated, block: B:35:0x0146  */
    /* JADX WARN: Code duplicated, block: B:59:0x028d  */
    @Override // android.view.ViewGroup
    public boolean drawChild(Canvas canvas, View view, long j) {
        TLRPC.User user;
        float f;
        boolean z;
        TLRPC.UserStatus userStatus;
        Text text;
        float currentWidth;
        float f2;
        Text text2;
        boolean zDrawChild = super.drawChild(canvas, view, j);
        if (view == this.imageView && this.currentType != 2 && (user = this.user) != null && !MessagesController.isSupportUser(user)) {
            long jElapsedRealtime = SystemClock.elapsedRealtime();
            long j2 = jElapsedRealtime - this.lastUpdateTime;
            long j3 = j2 <= 17 ? j2 : 17L;
            this.lastUpdateTime = jElapsedRealtime;
            float f3 = this.starsBlockedT.set(this.starsPriceBlocked > 0);
            if (f3 > 0.0f) {
                float left = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2.0f) + AndroidUtilities.dp(18.0f);
                float top = (this.imageView.getTop() + (this.imageView.getMeasuredHeight() / 2.0f)) - AndroidUtilities.dp(20.83f);
                if (this.priceText != null) {
                    long j4 = this.priceTextValue;
                    f = 0.0f;
                    long j5 = this.starsPriceBlocked;
                    if (j4 != j5 && j5 > 0) {
                    }
                    text = this.priceText;
                    if (text == null) {
                        currentWidth = f;
                    } else {
                        currentWidth = text.getCurrentWidth();
                    }
                    float fDp = currentWidth + AndroidUtilities.dp(10.0f);
                    float fDp2 = AndroidUtilities.dp(14.33f);
                    RectF rectF = AndroidUtilities.rectTmp;
                    float f4 = fDp / 2.0f;
                    f2 = left - f4;
                    float f5 = fDp2 / 2.0f;
                    rectF.set(f2, top - f5, left + f4, f5 + top);
                    rectF.inset(-AndroidUtilities.dp(1.33f), AndroidUtilities.dp(-1.33f));
                    this.priceBackgroundPaint.setColor(getThemedColor(Theme.key_dialogBackground));
                    canvas.drawRoundRect(rectF, rectF.height() / 2.0f, rectF.height() / 2.0f, this.priceBackgroundPaint);
                    rectF.inset(AndroidUtilities.dp(1.33f), AndroidUtilities.dp(1.33f));
                    this.priceBackgroundPaint.setColor(getThemedColor(Theme.key_dialogRoundCheckBox));
                    canvas.drawRoundRect(rectF, rectF.height() / 2.0f, rectF.height() / 2.0f, this.priceBackgroundPaint);
                    text2 = this.priceText;
                    if (text2 != null) {
                        text2.draw(canvas, AndroidUtilities.dp(5.0f) + f2, top, -1, 1.0f);
                    }
                } else {
                    f = 0.0f;
                }
                StringBuilder sb = new StringBuilder("⭐️");
                long j6 = this.starsPriceBlocked;
                this.priceTextValue = j6;
                sb.append(AndroidUtilities.formatWholeNumber((int) j6, 0));
                this.priceText = new Text(StarsIntroActivity.replaceStars(sb.toString(), 0.65f), 9.33f, AndroidUtilities.bold());
                text = this.priceText;
                if (text == null) {
                    currentWidth = f;
                } else {
                    currentWidth = text.getCurrentWidth();
                }
                float fDp3 = currentWidth + AndroidUtilities.dp(10.0f);
                float fDp4 = AndroidUtilities.dp(14.33f);
                RectF rectF2 = AndroidUtilities.rectTmp;
                float f6 = fDp3 / 2.0f;
                f2 = left - f6;
                float f7 = fDp4 / 2.0f;
                rectF2.set(f2, top - f7, left + f6, f7 + top);
                rectF2.inset(-AndroidUtilities.dp(1.33f), AndroidUtilities.dp(-1.33f));
                this.priceBackgroundPaint.setColor(getThemedColor(Theme.key_dialogBackground));
                canvas.drawRoundRect(rectF2, rectF2.height() / 2.0f, rectF2.height() / 2.0f, this.priceBackgroundPaint);
                rectF2.inset(AndroidUtilities.dp(1.33f), AndroidUtilities.dp(1.33f));
                this.priceBackgroundPaint.setColor(getThemedColor(Theme.key_dialogRoundCheckBox));
                canvas.drawRoundRect(rectF2, rectF2.height() / 2.0f, rectF2.height() / 2.0f, this.priceBackgroundPaint);
                text2 = this.priceText;
                if (text2 != null) {
                    text2.draw(canvas, AndroidUtilities.dp(5.0f) + f2, top, -1, 1.0f);
                }
            } else {
                f = 0.0f;
            }
            float f8 = this.premiumBlockedT.set(this.premiumBlocked);
            if (f8 > f) {
                int bottom = this.imageView.getBottom() - AndroidUtilities.dp(9.0f);
                int right = this.imageView.getRight() - AndroidUtilities.dp(9.33f);
                canvas.save();
                Theme.dialogs_onlineCirclePaint.setColor(getThemedColor(Theme.key_windowBackgroundWhite));
                float f9 = right;
                float f10 = bottom;
                canvas.drawCircle(f9, f10, AndroidUtilities.dp(12.0f) * f8, Theme.dialogs_onlineCirclePaint);
                if (this.premiumGradient == null) {
                    this.premiumGradient = new PremiumGradient.PremiumGradientTools(Theme.key_premiumGradient1, Theme.key_premiumGradient2, -1, -1, -1, this.resourcesProvider);
                }
                this.premiumGradient.gradientMatrix(right - AndroidUtilities.dp(10.0f), bottom - AndroidUtilities.dp(10.0f), right + AndroidUtilities.dp(10.0f), bottom + AndroidUtilities.dp(10.0f), 0.0f, 0.0f);
                canvas.drawCircle(f9, f10, AndroidUtilities.dp(10.0f) * f8, this.premiumGradient.paint);
                if (this.lockDrawable == null) {
                    Drawable drawableMutate = getContext().getResources().getDrawable(R.drawable.msg_mini_lock2).mutate();
                    this.lockDrawable = drawableMutate;
                    drawableMutate.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
                }
                Drawable drawable = this.lockDrawable;
                drawable.setBounds((int) (f9 - (((drawable.getIntrinsicWidth() / 2.0f) * 0.875f) * f8)), (int) (f10 - (((this.lockDrawable.getIntrinsicHeight() / 2.0f) * 0.875f) * f8)), (int) (f9 + ((this.lockDrawable.getIntrinsicWidth() / 2.0f) * 0.875f * f8)), (int) (f10 + ((this.lockDrawable.getIntrinsicHeight() / 2.0f) * 0.875f * f8)));
                this.lockDrawable.setAlpha((int) (255.0f * f8));
                this.lockDrawable.draw(canvas);
                canvas.restore();
            }
            if (this.premiumBlocked) {
                z = false;
            } else {
                TLRPC.User user2 = this.user;
                if (user2.self || user2.bot || (((userStatus = user2.status) == null || userStatus.expires <= ConnectionsManager.getInstance(this.currentAccount).getCurrentTime()) && !MessagesController.getInstance(this.currentAccount).onlinePrivacy.containsKey(Long.valueOf(this.user.id)))) {
                    z = false;
                } else {
                    z = true;
                }
            }
            if (z || this.onlineProgress != f) {
                float width = (this.imageView.getWidth() / 2.0f) * this.imageView.getScaleX();
                float x = this.imageView.getX() + (this.imageView.getWidth() / 2.0f);
                float f11 = x + width;
                float f12 = x - width;
                float y = this.imageView.getY() + (this.imageView.getHeight() / 2.0f) + width;
                float onlineDotOuterRadius = ExteraConfig.getOnlineDotOuterRadius();
                float onlineDotInnerRadius = ExteraConfig.getOnlineDotInnerRadius();
                float onlineDotOffset = ExteraConfig.getOnlineDotOffset(AndroidUtilities.dp(10.0f), onlineDotOuterRadius);
                float onlineDotOffset2 = ExteraConfig.getOnlineDotOffset(AndroidUtilities.dp(6.0f), onlineDotOuterRadius);
                float f13 = LocaleController.isRTL ? f12 + onlineDotOffset : f11 - onlineDotOffset;
                float f14 = y - onlineDotOffset2;
                float f15 = this.onlineProgress * (1.0f - f8) * (1.0f - f3);
                Theme.dialogs_onlineCirclePaint.setColor(getThemedColor(Theme.key_windowBackgroundWhite));
                canvas.drawCircle(f13, f14, onlineDotOuterRadius * f15, Theme.dialogs_onlineCirclePaint);
                Theme.dialogs_onlineCirclePaint.setColor(getThemedColor(Theme.key_chats_onlineCircle));
                canvas.drawCircle(f13, f14, onlineDotInnerRadius * f15, Theme.dialogs_onlineCirclePaint);
                float f16 = this.onlineProgress;
                if (z) {
                    if (f16 < 1.0f) {
                        float f17 = f16 + (j3 / 150.0f);
                        this.onlineProgress = f17;
                        if (f17 > 1.0f) {
                            this.onlineProgress = 1.0f;
                        }
                        this.imageView.invalidate();
                        invalidate();
                        return zDrawChild;
                    }
                } else if (f16 > f) {
                    float f18 = f16 - (j3 / 150.0f);
                    this.onlineProgress = f18;
                    if (f18 < f) {
                        this.onlineProgress = f;
                    }
                    this.imageView.invalidate();
                    invalidate();
                }
            }
        }
        return zDrawChild;
    }

    @Override // android.view.View
    public void onDraw(Canvas canvas) {
        int left = this.imageView.getLeft() + (this.imageView.getMeasuredWidth() / 2);
        int top = this.imageView.getTop() + (this.imageView.getMeasuredHeight() / 2);
        Theme.checkboxSquare_checkPaint.setColor(getThemedColor(Theme.key_dialogRoundCheckBox));
        Theme.checkboxSquare_checkPaint.setAlpha((int) (this.checkBox.getProgress() * 255.0f));
        int iDp = AndroidUtilities.dp(this.currentType == 2 ? 24.0f : 28.0f);
        RectF rectF = AndroidUtilities.rectTmp;
        rectF.set(left - iDp, top - iDp, left + iDp, top + iDp);
        canvas.drawRoundRect(rectF, this.imageView.getRoundRadius()[0] + AndroidUtilities.dp(2.0f), this.imageView.getRoundRadius()[0] + AndroidUtilities.dp(2.0f), Theme.checkboxSquare_checkPaint);
        super.onDraw(canvas);
    }

    private int getThemedColor(int i) {
        return Theme.getColor(i, this.resourcesProvider);
    }

    @Override // android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo accessibilityNodeInfo) {
        super.onInitializeAccessibilityNodeInfo(accessibilityNodeInfo);
        if (this.checkBox.isChecked()) {
            accessibilityNodeInfo.setSelected(true);
        }
    }

    public static class RepostStoryDrawable extends Drawable {
        int alpha;
        private final Drawable drawable;
        private final LinearGradient gradient;
        private final RLottieDrawable lottieDrawable;
        private final Paint paint;

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return -2;
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public RepostStoryDrawable(Context context, View view, boolean z, Theme.ResourcesProvider resourcesProvider) {
            this(context, view, z, R.drawable.large_repost_story, resourcesProvider);
        }

        public RepostStoryDrawable(Context context, View view, int i, Theme.ResourcesProvider resourcesProvider) {
            this(context, view, false, i, resourcesProvider);
        }

        public RepostStoryDrawable(Context context, View view, boolean z, int i, Theme.ResourcesProvider resourcesProvider) {
            Paint paint = new Paint(1);
            this.paint = paint;
            this.alpha = 255;
            LinearGradient linearGradient = new LinearGradient(0.0f, 0.0f, AndroidUtilities.dp(56.0f), AndroidUtilities.dp(56.0f), new int[]{Theme.getColor(Theme.key_stories_circle1, resourcesProvider), Theme.getColor(Theme.key_stories_circle2, resourcesProvider)}, new float[]{0.0f, 1.0f}, Shader.TileMode.CLAMP);
            this.gradient = linearGradient;
            paint.setShader(linearGradient);
            if (z) {
                RLottieDrawable rLottieDrawable = new RLottieDrawable(R.raw.story_repost, "story_repost", AndroidUtilities.dp(42.0f), AndroidUtilities.dp(42.0f), true, null);
                this.lottieDrawable = rLottieDrawable;
                rLottieDrawable.setMasterParent(view);
                Objects.requireNonNull(rLottieDrawable);
                AndroidUtilities.runOnUIThread(new ShareDialogCell$RepostStoryDrawable$$ExternalSyntheticLambda0(rLottieDrawable), 450L);
                this.drawable = null;
                return;
            }
            this.lottieDrawable = null;
            Drawable drawableMutate = context.getResources().getDrawable(i).mutate();
            this.drawable = drawableMutate;
            drawableMutate.setColorFilter(new PorterDuffColorFilter(-1, PorterDuff.Mode.SRC_IN));
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(0.0f, 0.0f, getBounds().width(), getBounds().height());
            this.paint.setAlpha(this.alpha);
            float avatarCorners = ExteraConfig.getAvatarCorners(getBounds().width(), true, false, true);
            canvas.drawRoundRect(rectF, avatarCorners, avatarCorners, this.paint);
            canvas.restore();
            int iDp = AndroidUtilities.dp(this.lottieDrawable != null ? 20.0f : 15.0f);
            Rect rect = AndroidUtilities.rectTmp2;
            rect.set(getBounds().centerX() - iDp, getBounds().centerY() - iDp, getBounds().centerX() + iDp, getBounds().centerY() + iDp);
            Drawable drawable = this.lottieDrawable;
            if (drawable == null) {
                drawable = this.drawable;
            }
            if (drawable != null) {
                drawable.setBounds(rect);
                drawable.setAlpha(this.alpha);
                drawable.draw(canvas);
            }
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
            this.alpha = i;
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return AndroidUtilities.dp(56.0f);
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(56.0f);
        }
    }
}
