package org.telegram.ui.Stories;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.ColorUtils;
import androidx.core.math.MathUtils;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.TabIconsMode;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import me.vkryl.android.animator.ReplaceAnimator;
import okhttp3.internal.url._UrlKt;
import org.mvel2.asm.Opcodes;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarAnimatedSubtitleOverlayContainer;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.AnimatedFloat;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.ButtonBounce;
import org.telegram.ui.Components.CanvasButton;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EllipsizeSpanAnimator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.ListView.AdapterWithDiffUtils;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.RadialProgress;
import org.telegram.ui.Components.RecyclerListView;
import org.telegram.ui.Components.SeekBarView;
import org.telegram.ui.Components.TypefaceSpan;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.Stories.recorder.StoryRecorder;

@SuppressLint({"ViewConstructor"})
public abstract class DialogStoriesCell extends FrameLayout implements NotificationCenter.NotificationCenterDelegate, FactorAnimator.Target {
    public float K;
    private ActionBar actionBar;
    Adapter adapter;
    Paint addCirclePaint;
    private final Drawable addNewStoryDrawable;
    private int addNewStoryLastColor;
    ArrayList<Runnable> afterNextLayout;
    public boolean allowGlobalUpdates;
    ArrayList<Long> animateToDialogIds;
    private Runnable animationRunnable;
    private final BoolAnimator animatorHasTitleText;
    Paint backgroundPaint;
    private long checkedStoryNotificationDeletion;
    private int clipTop;
    boolean collapsed;
    private ValueAnimator collapsedOvershootAnimator;
    private float collapsedOvershootProgress;
    float collapsedProgress;
    private float collapsedProgress1;
    private float collapsedProgress2;
    private float collapsedSpringCoef;
    Comparator<StoryCell> comparator;
    int currentAccount;
    public int currentCellWidth;
    int currentState;
    private CharSequence currentTitle;
    private boolean dialogsEmojiStatusVisible;
    private boolean dialogsEmojiStatusVisibleOut;
    private CharSequence dialogsTitleOverride;
    boolean drawCircleForce;
    private LinearGradient ellipsizeGradient;
    private Matrix ellipsizeGradientMatrix;
    private Paint ellipsizePaint;
    EllipsizeSpanAnimator ellipsizeSpanAnimator;
    ImageView emojiStatusView;
    private ValueAnimator expandOvershootAnimator;
    private float expandOvershootAnimatorProgress;
    private float expandedSpringCoef;
    BaseFragment fragment;
    private StoriesUtilities.EnsureStoryFileLoadedObject globalCancelable;
    Paint grayPaint;
    private boolean hasOverlayText;
    DefaultItemAnimator itemAnimator;
    ArrayList<Item> items;
    private boolean lastUploadingCloseFriends;
    LinearLayoutManager layoutManager;
    RecyclerListView listViewMini;
    private float menuItemsOffset;
    Adapter miniAdapter;
    private final DefaultItemAnimator miniItemAnimator;
    ArrayList<Item> miniItems;
    CanvasButton miniItemsClickArea;
    ArrayList<Item> oldItems;
    ArrayList<Item> oldMiniItems;
    private float overScrollCoef;
    private int overlayTextId;
    private float overscrollProgress;
    private int overscrollSelectedPosition;
    private StoryCell overscrollSelectedView;
    private HintView2 premiumHint;
    private Drawable premiumStar;
    public RadialProgress radialProgress;
    public RecyclerListView recyclerListView;
    AnimatedEmojiDrawable.SwapAnimatedEmojiDrawable statusDrawable;
    AnimatorSet storiesAnimatorSet;
    private OvershootInterpolator storiesCollapseInterpolator;
    StoriesController storiesController;
    private OvershootInterpolator storiesExpandInterpolator;
    ActionBarAnimatedSubtitleOverlayContainer subtitleOverlayContainer;
    private ValueAnimator textAnimator;
    private ValueAnimator titleOverrideAnimator;
    private boolean titleOverrideForward;
    private float titleOverrideProgress;
    AnimatedTextView titleView;
    AnimatedTextView titleViewOut;
    private final int type;
    boolean updateOnIdleState;
    private ValueAnimator valueAnimator;
    ArrayList<StoryCell> viewsDrawInParent;
    private ValueAnimator yStoriesAnimator;
    private float yStoriesProgress;

    public static void lambda$setProgressToCollapse$7(ValueAnimator valueAnimator) {
        this.collapsedProgress2 = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        checkCollapsedProgress();
    }

    public /* JADX WARN: Failed to calculate best type for var: r4v48 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r4v48 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r9v7 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r9v7 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to calculate best type for var: r9v8 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r9v8 ??, new type: float
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.calculateFromBounds(TypeInferenceVisitor.java:147)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setBestType(TypeInferenceVisitor.java:125)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$1(TypeInferenceVisitor.java:103)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:103)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to set immutable type for var: r26v0 'this'  ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r26v0 'this'  ??, new type: org.telegram.ui.Stories.DialogStoriesCell$StoryCell
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.applyWithWiderIgnSame(TypeUpdate.java:73)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setImmutableType(TypeInferenceVisitor.java:111)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$0(TypeInferenceVisitor.java:102)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:102)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Failed to set immutable type for var: r27v0 ??
        jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r27v0 ??, new type: android.graphics.Canvas
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
        	at jadx.core.dex.visitors.typeinference.TypeUpdate.applyWithWiderIgnSame(TypeUpdate.java:73)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.setImmutableType(TypeInferenceVisitor.java:111)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.lambda$runTypePropagation$0(TypeInferenceVisitor.java:102)
        	at java.base/java.util.ArrayList.forEach(Unknown Source)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.runTypePropagation(TypeInferenceVisitor.java:102)
        	at jadx.core.dex.visitors.typeinference.TypeInferenceVisitor.visit(TypeInferenceVisitor.java:75)
        Caused by: java.lang.NullPointerException
         */
        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        /*  JADX ERROR: Types fix failed
            jadx.core.utils.exceptions.JadxRuntimeException: Type update failed for variable: r16v0 ??, new type: float
            	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:109)
            	at jadx.core.dex.visitors.typeinference.TypeUpdate.apply(TypeUpdate.java:59)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryPossibleTypes(FixTypesVisitor.java:186)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.deduceType(FixTypesVisitor.java:245)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryDeduceTypes(FixTypesVisitor.java:224)
            	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
            Caused by: java.lang.NullPointerException
            */
        @Override // android.view.ViewGroup, android.view.View
        public void dispatchDraw(android.graphics.Canvas r27) {
            /*
                Method dump skipped, instruction units count: 1046
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: org.telegram.ui.Stories.DialogStoriesCell.StoryCell.dispatchDraw(android.graphics.Canvas):void");
        }

        public /* synthetic */ void lambda$dispatchDraw$2(ValueAnimator valueAnimator) {
            this.params.progressToSegments = AndroidUtilities.lerp(0.0f, 1.0f - DialogStoriesCell.this.collapsedProgress2, ((Float) valueAnimator.getAnimatedValue()).floatValue());
            invalidate();
        }

        public void setClipInParent(boolean z) {
            if (getParent() != null) {
                ((ViewGroup) getParent()).setClipChildren(z);
            }
            if (getParent() == null || getParent().getParent() == null || getParent().getParent().getParent() == null) {
                return;
            }
            ((ViewGroup) getParent().getParent().getParent()).setClipChildren(z);
        }

        private float getArcProgress(float f, float f2) {
            if (!this.isLast && DialogStoriesCell.this.overscrollProgress <= 0.0f) {
                float fLerp = AndroidUtilities.lerp(getMeasuredWidth(), AndroidUtilities.dp(16.0f), CubicBezierInterpolator.EASE_OUT.getInterpolation(this.progressToCollapsed));
                float fDpf2 = f2 + AndroidUtilities.dpf2(3.5f);
                if (fLerp < fDpf2 * 2.0f) {
                    return ((float) Math.toDegrees(Math.acos((fLerp / 2.0f) / fDpf2))) * 2.0f;
                }
            }
            return 0.0f;
        }

        @Override // android.view.View
        public void setPressed(boolean z) {
            super.setPressed(z);
            if (z) {
                StoriesUtilities.AvatarStoryParams avatarStoryParams = this.params;
                if (avatarStoryParams.buttonBounce == null) {
                    avatarStoryParams.buttonBounce = new ButtonBounce(this, 1.5f, 5.0f);
                }
            }
            ButtonBounce buttonBounce = this.params.buttonBounce;
            if (buttonBounce != null) {
                buttonBounce.setPressed(z);
            }
        }

        @Override // android.view.View
        public void invalidate() {
            if (this.mini || (this.drawInParent && getParent() != null)) {
                ViewParent parent = getParent();
                DialogStoriesCell dialogStoriesCell = DialogStoriesCell.this;
                RecyclerListView recyclerListView = dialogStoriesCell.listViewMini;
                if (parent == recyclerListView) {
                    recyclerListView.invalidate();
                } else {
                    dialogStoriesCell.invalidate();
                }
            }
            super.invalidate();
        }

        @Override // android.view.View
        public void invalidate(int i, int i2, int i3, int i4) {
            if (this.mini || (this.drawInParent && getParent() != null)) {
                ViewParent parent = getParent();
                RecyclerListView recyclerListView = DialogStoriesCell.this.listViewMini;
                if (parent == recyclerListView) {
                    recyclerListView.invalidate();
                }
                DialogStoriesCell.this.invalidate();
            }
            super.invalidate(i, i2, i3, i4);
        }

        public void drawPlus(Canvas canvas, float f, float f2, float f3) {
            if (this.isSelf && !DialogStoriesCell.this.storiesController.hasStories(this.dialogId) && Utilities.isNullOrEmpty(DialogStoriesCell.this.storiesController.getUploadingStories(this.dialogId))) {
                float fDp = f + AndroidUtilities.dp(16.0f);
                float fDp2 = f2 + AndroidUtilities.dp(16.0f);
                DialogStoriesCell dialogStoriesCell = DialogStoriesCell.this;
                dialogStoriesCell.addCirclePaint.setColor(Theme.multAlpha(dialogStoriesCell.getThemedColor(Theme.key_telegram_color), f3));
                int i = DialogStoriesCell.this.type;
                DialogStoriesCell dialogStoriesCell2 = DialogStoriesCell.this;
                if (i == 0) {
                    dialogStoriesCell2.backgroundPaint.setColor(Theme.multAlpha(dialogStoriesCell2.getThemedColor(Theme.key_actionBarDefault), f3));
                } else {
                    dialogStoriesCell2.backgroundPaint.setColor(Theme.multAlpha(dialogStoriesCell2.getThemedColor(Theme.key_actionBarDefaultArchived), f3));
                }
                canvas.drawCircle(fDp, fDp2, AndroidUtilities.dp(11.0f), DialogStoriesCell.this.backgroundPaint);
                canvas.drawCircle(fDp, fDp2, AndroidUtilities.dp(9.0f), DialogStoriesCell.this.addCirclePaint);
                int themedColor = DialogStoriesCell.this.getThemedColor(DialogStoriesCell.this.type == 0 ? Theme.key_actionBarDefault : Theme.key_actionBarDefaultArchived);
                if (themedColor != DialogStoriesCell.this.addNewStoryLastColor) {
                    Drawable drawable = DialogStoriesCell.this.addNewStoryDrawable;
                    DialogStoriesCell.this.addNewStoryLastColor = themedColor;
                    drawable.setColorFilter(new PorterDuffColorFilter(themedColor, PorterDuff.Mode.MULTIPLY));
                }
                DialogStoriesCell.this.addNewStoryDrawable.setAlpha((int) (f3 * 255.0f));
                DialogStoriesCell.this.addNewStoryDrawable.setBounds((int) (fDp - (DialogStoriesCell.this.addNewStoryDrawable.getIntrinsicWidth() / 2.0f)), (int) (fDp2 - (DialogStoriesCell.this.addNewStoryDrawable.getIntrinsicHeight() / 2.0f)), (int) (fDp + (DialogStoriesCell.this.addNewStoryDrawable.getIntrinsicWidth() / 2.0f)), (int) (fDp2 + (DialogStoriesCell.this.addNewStoryDrawable.getIntrinsicHeight() / 2.0f)));
                DialogStoriesCell.this.addNewStoryDrawable.draw(canvas);
            }
        }

        public void drawFail(Canvas canvas, float f, float f2, float f3) {
            if (f3 <= 0.0f) {
                return;
            }
            float fDp = f + AndroidUtilities.dp(17.0f);
            float fDp2 = f2 + AndroidUtilities.dp(17.0f);
            DialogStoriesCell dialogStoriesCell = DialogStoriesCell.this;
            dialogStoriesCell.addCirclePaint.setColor(Theme.multAlpha(dialogStoriesCell.getThemedColor(Theme.key_text_RedBold), f3));
            int i = DialogStoriesCell.this.type;
            DialogStoriesCell dialogStoriesCell2 = DialogStoriesCell.this;
            if (i == 0) {
                dialogStoriesCell2.backgroundPaint.setColor(Theme.multAlpha(dialogStoriesCell2.getThemedColor(Theme.key_actionBarDefault), f3));
            } else {
                dialogStoriesCell2.backgroundPaint.setColor(Theme.multAlpha(dialogStoriesCell2.getThemedColor(Theme.key_actionBarDefaultArchived), f3));
            }
            float fDp3 = AndroidUtilities.dp(9.0f) * CubicBezierInterpolator.EASE_OUT_BACK.getInterpolation(f3);
            canvas.drawCircle(fDp, fDp2, AndroidUtilities.dp(2.0f) + fDp3, DialogStoriesCell.this.backgroundPaint);
            canvas.drawCircle(fDp, fDp2, fDp3, DialogStoriesCell.this.addCirclePaint);
            DialogStoriesCell dialogStoriesCell3 = DialogStoriesCell.this;
            dialogStoriesCell3.addCirclePaint.setColor(Theme.multAlpha(dialogStoriesCell3.getTextColor(), f3));
            RectF rectF = AndroidUtilities.rectTmp;
            rectF.set(fDp - AndroidUtilities.dp(1.0f), fDp2 - AndroidUtilities.dpf2(4.6f), AndroidUtilities.dp(1.0f) + fDp, AndroidUtilities.dpf2(1.6f) + fDp2);
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), DialogStoriesCell.this.addCirclePaint);
            rectF.set(fDp - AndroidUtilities.dp(1.0f), AndroidUtilities.dpf2(2.6f) + fDp2, fDp + AndroidUtilities.dp(1.0f), fDp2 + AndroidUtilities.dpf2(4.6f));
            canvas.drawRoundRect(rectF, AndroidUtilities.dp(3.0f), AndroidUtilities.dp(3.0f), DialogStoriesCell.this.addCirclePaint);
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onAttachedToWindow() {
            super.onAttachedToWindow();
            this.avatarImage.onAttachedToWindow();
            this.crossfadeToAvatarImage.onAttachedToWindow();
        }

        @Override // android.view.ViewGroup, android.view.View
        public void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            this.avatarImage.onDetachedFromWindow();
            this.crossfadeToAvatarImage.onDetachedFromWindow();
            this.params.onDetachFromWindow();
            StoriesUtilities.EnsureStoryFileLoadedObject ensureStoryFileLoadedObject = this.cancellable;
            if (ensureStoryFileLoadedObject != null) {
                ensureStoryFileLoadedObject.cancel();
                this.cancellable = null;
            }
        }

        public void setProgressToCollapsed(float f, float f2, float f3, boolean z) {
            float fClamp;
            if (this.progressToCollapsed != f || this.progressToCollapsed2 != f2 || this.overscrollProgress != f3 || this.selectedForOverscroll != z) {
                this.selectedForOverscroll = z;
                this.progressToCollapsed = f;
                this.progressToCollapsed2 = f2;
                invalidate();
                DialogStoriesCell.this.recyclerListView.invalidate();
            }
            if (this.mini) {
                fClamp = 0.0f;
            } else {
                DialogStoriesCell dialogStoriesCell = DialogStoriesCell.this;
                fClamp = 1.0f - Utilities.clamp(dialogStoriesCell.collapsedProgress / dialogStoriesCell.K, 1.0f, 0.0f);
            }
            this.textAlphaTransition = fClamp;
            float f4 = fClamp * this.textAlpha;
            this.textViewContainer.setAlpha(f4);
            this.textViewContainer.setVisibility(f4 > 0.0f ? 0 : 4);
        }

        /* JADX WARN: Type inference fix 'apply assigned field type' failed
        java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$PrimitiveArg
        	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:596)
        	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
        	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
        	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
         */
        public void setCrossfadeTo(long j) {
            TLRPC.Chat chat;
            TLObject tLObject;
            TLRPC.User user;
            if (this.crossfadeToDialogId != j) {
                this.crossfadeToDialogId = j;
                boolean z = j != -1;
                this.crossfadeToDialog = z;
                if (z) {
                    DialogStoriesCell dialogStoriesCell = DialogStoriesCell.this;
                    if (j > 0) {
                        user = MessagesController.getInstance(dialogStoriesCell.currentAccount).getUser(Long.valueOf(j));
                        this.user = user;
                        this.chat = null;
                    } else {
                        chat = MessagesController.getInstance(dialogStoriesCell.currentAccount).getChat(Long.valueOf(-j));
                        this.chat = chat;
                        this.user = null;
                    }
                    if (tLObject == null) {
                        tLObject = chat;
                        tLObject = user;
                        return;
                    } else {
                        tLObject = chat;
                        tLObject = user;
                        this.crossfadeAvatarDrawable.setInfo(DialogStoriesCell.this.currentAccount, tLObject);
                        this.crossfadeToAvatarImage.setForUserOrChat(tLObject, this.crossfadeAvatarDrawable);
                        return;
                    }
                }
                this.crossfadeToAvatarImage.clearImage();
            }
        }
    }

    public Drawable createVerifiedDrawable() {
        final Drawable drawableMutate = ContextCompat.getDrawable(getContext(), R.drawable.verified_area).mutate();
        final Drawable drawableMutate2 = ContextCompat.getDrawable(getContext(), R.drawable.verified_check).mutate();
        CombinedDrawable combinedDrawable = new CombinedDrawable(drawableMutate, drawableMutate2) { // from class: org.telegram.ui.Stories.DialogStoriesCell.12
            int lastColor;

            @Override // org.telegram.ui.Components.CombinedDrawable, android.graphics.drawable.Drawable
            public void draw(Canvas canvas) {
                int themedColor = DialogStoriesCell.this.getThemedColor(DialogStoriesCell.this.type == 0 ? Theme.key_actionBarDefault : Theme.key_actionBarDefaultArchived);
                if (this.lastColor != themedColor) {
                    this.lastColor = themedColor;
                    int themedColor2 = DialogStoriesCell.this.getThemedColor(DialogStoriesCell.this.type == 0 ? Theme.key_actionBarDefaultTitle : Theme.key_actionBarDefaultArchivedTitle);
                    Drawable drawable = drawableMutate;
                    int iBlendARGB = ColorUtils.blendARGB(themedColor2, themedColor, 0.1f);
                    PorterDuff.Mode mode = PorterDuff.Mode.MULTIPLY;
                    drawable.setColorFilter(new PorterDuffColorFilter(iBlendARGB, mode));
                    drawableMutate2.setColorFilter(new PorterDuffColorFilter(themedColor, mode));
                }
                super.draw(canvas);
            }
        };
        combinedDrawable.setFullsize(true);
        return combinedDrawable;
    }

    private void updateCurrentState(int i) {
        if (this.currentState == i) {
            return;
        }
        this.currentState = i;
        if (i != 1 && this.updateOnIdleState) {
            AndroidUtilities.runOnUIThread(new Runnable() { // from class: org.telegram.ui.Stories.DialogStoriesCell$$ExternalSyntheticLambda15
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$updateCurrentState$16();
                }
            });
        }
        int i2 = this.currentState;
        if (i2 == 0) {
            AndroidUtilities.forEachViews((RecyclerView) this.recyclerListView, (Consumer<View>) new Consumer() { // from class: org.telegram.ui.Stories.DialogStoriesCell$$ExternalSyntheticLambda16
                @Override // com.google.android.exoplayer2.util.Consumer
                public final void accept(Object obj) {
                    DialogStoriesCell.$r8$lambda$cLLcGrs3ecWcfBgfj5WeKmCtcUg((View) obj);
                }
            });
            this.listViewMini.setVisibility(4);
            this.recyclerListView.setVisibility(0);
            checkExpanded();
        } else if (i2 == 1) {
            this.animateToDialogIds.clear();
            for (int i3 = 0; i3 < this.items.size(); i3++) {
                if (this.items.get(i3).dialogId != UserConfig.getInstance(this.currentAccount).getClientUserId() || shouldDrawSelfInMini()) {
                    this.animateToDialogIds.add(Long.valueOf(this.items.get(i3).dialogId));
                    if (this.animateToDialogIds.size() == 3) {
                        break;
                    }
                }
            }
            this.listViewMini.setVisibility(4);
            this.recyclerListView.setVisibility(0);
        } else if (i2 == 2) {
            this.listViewMini.setVisibility(0);
            this.recyclerListView.setVisibility(4);
            this.layoutManager.scrollToPositionWithOffset(0, 0);
            MessagesController.getInstance(this.currentAccount).getStoriesController().scheduleSort();
            StoriesUtilities.EnsureStoryFileLoadedObject ensureStoryFileLoadedObject = this.globalCancelable;
            if (ensureStoryFileLoadedObject != null) {
                ensureStoryFileLoadedObject.cancel();
                this.globalCancelable = null;
            }
        }
        invalidate();
    }

    public /* synthetic */ void lambda$updateCurrentState$16() {
        updateItems(true, false);
    }

    public static /* synthetic */ void $r8$lambda$cLLcGrs3ecWcfBgfj5WeKmCtcUg(View view) {
        view.setAlpha(1.0f);
        view.setTranslationX(0.0f);
        view.setTranslationY(0.0f);
    }

    public static float getAvatarRight(int i, float f) {
        float fLerp = AndroidUtilities.lerp(AndroidUtilities.dp(48.0f), AndroidUtilities.dp(26.33f), f) / 2.0f;
        return AndroidUtilities.lerp((i / 2.0f) - fLerp, 0.0f, f) + (fLerp * 2.0f);
    }

    private void checkExpanded() {
        if (System.currentTimeMillis() < this.checkedStoryNotificationDeletion) {
            return;
        }
        this.checkedStoryNotificationDeletion = System.currentTimeMillis() + 60000;
    }

    @Override // android.view.View
    public void setTranslationY(float f) {
        super.setTranslationY(f);
        HintView2 hintView2 = this.premiumHint;
        if (hintView2 != null) {
            hintView2.setTranslationY(f);
        }
    }

    public HintView2 getPremiumHint() {
        return this.premiumHint;
    }

    private HintView2 makePremiumHint() {
        HintView2 hintView2 = this.premiumHint;
        if (hintView2 != null) {
            return hintView2;
        }
        this.premiumHint = new HintView2(getContext(), 1).setBgColor(getThemedColor(Theme.key_undo_background)).setMultilineText(true).setTextAlign(Layout.Alignment.ALIGN_CENTER).setJoint(0.0f, 29.0f);
        SpannableStringBuilder spannableStringBuilderReplaceSingleTag = AndroidUtilities.replaceSingleTag(LocaleController.getString("StoriesPremiumHint2").replace('\n', ' '), Theme.key_undo_cancelColor, 0, new Runnable() { // from class: org.telegram.ui.Stories.DialogStoriesCell$$ExternalSyntheticLambda17
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$makePremiumHint$18();
            }
        });
        ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannableStringBuilderReplaceSingleTag.getSpans(0, spannableStringBuilderReplaceSingleTag.length(), ClickableSpan.class);
        if (clickableSpanArr != null && clickableSpanArr.length >= 1) {
            spannableStringBuilderReplaceSingleTag.setSpan(new TypefaceSpan(AndroidUtilities.bold()), spannableStringBuilderReplaceSingleTag.getSpanStart(clickableSpanArr[0]), spannableStringBuilderReplaceSingleTag.getSpanEnd(clickableSpanArr[0]), 33);
        }
        HintView2 hintView3 = this.premiumHint;
        hintView3.setMaxWidthPx(HintView2.cutInFancyHalf(spannableStringBuilderReplaceSingleTag, hintView3.getTextPaint()));
        this.premiumHint.setText(spannableStringBuilderReplaceSingleTag);
        this.premiumHint.setPadding(AndroidUtilities.dp(8.0f), AndroidUtilities.dp(24.0f), AndroidUtilities.dp(8.0f), 0);
        if (getParent() instanceof FrameLayout) {
            ((FrameLayout) getParent()).addView(this.premiumHint, LayoutHelper.createFrame(-1, 150, 51));
        }
        return this.premiumHint;
    }

    public /* synthetic */ void lambda$makePremiumHint$18() {
        HintView2 hintView2 = this.premiumHint;
        if (hintView2 != null) {
            hintView2.hide();
        }
        this.fragment.presentFragment(new PremiumPreviewFragment("stories"));
    }

    public void showPremiumHint() {
        makePremiumHint();
        HintView2 hintView2 = this.premiumHint;
        if (hintView2 != null) {
            if (hintView2.shown()) {
                BotWebViewVibrationEffect.APP_ERROR.vibrate();
            }
            this.premiumHint.show();
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (this.currentState == 2) {
            int size = this.miniItems.size();
            this.miniItemsClickArea.setRect((int) this.listViewMini.getX(), (int) this.listViewMini.getY(), (int) (this.listViewMini.getX() + AndroidUtilities.dp((size * 26.33f) - (Math.max(0, size - 1) * 16.0f))), (int) (this.listViewMini.getY() + this.listViewMini.getHeight()));
            if (this.miniItemsClickArea.checkTouchEvent(motionEvent)) {
                return true;
            }
        }
        return super.onTouchEvent(motionEvent);
    }

    public void updateStatus(TLRPC.User user, boolean z) {
        if (this.statusDrawable == null || this.actionBar == null) {
            return;
        }
        Long emojiStatusDocumentId = UserObject.getEmojiStatusDocumentId(user);
        if (ExteraConfig.getHideActionBarStatus() || !UserConfig.getInstance(this.currentAccount).isPremium()) {
            this.statusDrawable.set((Drawable) null, z);
            this.statusDrawable.setParticles(false, z);
        } else if (emojiStatusDocumentId != null) {
            boolean z2 = user.emoji_status instanceof TLRPC.TL_emojiStatusCollectible;
            this.statusDrawable.set(emojiStatusDocumentId.longValue(), z);
            this.statusDrawable.setParticles(z2, z);
        } else if (user != null && MessagesController.getInstance(this.currentAccount).isPremiumUser(user)) {
            if (this.premiumStar == null) {
                this.premiumStar = getContext().getResources().getDrawable(R.drawable.msg_premium_liststar).mutate();
                this.premiumStar = new AnimatedEmojiDrawable.WrapSizeDrawable(this.premiumStar, AndroidUtilities.dp(18.0f), AndroidUtilities.dp(18.0f)) { // from class: org.telegram.ui.Stories.DialogStoriesCell.13
                    @Override // org.telegram.ui.Components.AnimatedEmojiDrawable.WrapSizeDrawable, android.graphics.drawable.Drawable
                    public void draw(Canvas canvas) {
                        canvas.save();
                        canvas.translate(AndroidUtilities.dp(-2.0f), AndroidUtilities.dp(1.0f));
                        super.draw(canvas);
                        canvas.restore();
                    }
                };
            }
            this.premiumStar.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_profile_verifiedBackground), PorterDuff.Mode.MULTIPLY));
            this.statusDrawable.set(this.premiumStar, z);
            this.statusDrawable.setParticles(false, z);
        } else {
            this.statusDrawable.set((Drawable) null, z);
            this.statusDrawable.setParticles(false, z);
        }
        this.statusDrawable.setColor(Integer.valueOf(getThemedColor(Theme.key_profile_verifiedBackground)));
        this.emojiStatusView.invalidate();
    }

    public int getThemedColor(int i) {
        BaseFragment baseFragment = this.fragment;
        if (baseFragment == null || baseFragment.getResourceProvider() == null) {
            return Theme.getColor(i);
        }
        return this.fragment.getThemedColor(i);
    }

    @Override // me.vkryl.android.animator.FactorAnimator.Target
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        if (i == 1) {
            checkUi_titleVisibility();
        }
    }

    private void checkUi_titleVisibility() {
        float fClamp = MathUtils.clamp(Math.min(this.collapsedProgress, this.collapsedProgress2), 0.0f, 1.0f);
        AnimatedTextView animatedTextView = this.titleView;
        if (animatedTextView != null) {
            animatedTextView.setAlpha(fClamp);
            this.titleView.setVisibility(fClamp > 0.0f ? 0 : 8);
        }
        AnimatedTextView animatedTextView2 = this.titleViewOut;
        if (animatedTextView2 != null && this.titleOverrideAnimator == null) {
            animatedTextView2.setAlpha(fClamp);
            this.titleViewOut.setVisibility(fClamp > 0.0f ? 0 : 8);
        }
        ImageView imageView = this.emojiStatusView;
        if (imageView != null) {
            imageView.setAlpha(getEmojiStatusAlpha() * fClamp);
            this.emojiStatusView.setVisibility(fClamp > 0.0f ? 0 : 8);
        }
        ActionBarAnimatedSubtitleOverlayContainer actionBarAnimatedSubtitleOverlayContainer = this.subtitleOverlayContainer;
        if (actionBarAnimatedSubtitleOverlayContainer != null) {
            actionBarAnimatedSubtitleOverlayContainer.setAlpha(fClamp);
            this.subtitleOverlayContainer.setVisibility(fClamp > 0.0f ? 0 : 8);
        }
    }
}
