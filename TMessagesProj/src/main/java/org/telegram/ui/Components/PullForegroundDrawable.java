package org.telegram.ui.Components;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import androidx.core.graphics.ColorUtils;
import com.exteragram.messenger.ExteraConfig;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Stories.recorder.HintView2;
import org.telegram.ui.TopicsFragment;

public abstract class PullForegroundDrawable {
    private ValueAnimator accentRevalAnimatorIn;
    private ValueAnimator accentRevalAnimatorOut;
    private float accentRevalProgress;
    private float accentRevalProgressOut;
    private boolean animateOut;
    private boolean animateToColorize;
    private boolean animateToEndText;
    private boolean animateToTextIn;
    private boolean arrowAnimateTo;
    private final ArrowDrawable arrowDrawable;
    private ValueAnimator arrowRotateAnimator;
    private float arrowRotateProgress;
    private boolean bounceIn;
    private float bounceProgress;
    private View cell;
    private final Path circleClipPath;
    private Drawable generalTopicDrawable;
    private boolean isOut;
    private int lastWidth;
    private RecyclerListView listView;
    private AnimatorSet outAnimator;
    public float outCx;
    public float outCy;
    public float outImageSize;
    public float outOverScroll;
    public float outProgress;
    public float outRadius;
    private float pullProgress;
    private StaticLayout pullTooltipLayout;
    private float pullTooltipLayoutLeft;
    private float pullTooltipLayoutScale;
    private float pullTooltipLayoutWidth;
    private final CharSequence pullTooltipText;
    private StaticLayout releaseTooltipLayout;
    private float releaseTooltipLayoutLeft;
    private float releaseTooltipLayoutScale;
    private float releaseTooltipLayoutWidth;
    private final CharSequence releaseTooltipText;
    public int scrollDy;
    private float textInProgress;
    Runnable textInRunnable;
    private ValueAnimator.AnimatorUpdateListener textInUpdateListener;
    private ValueAnimator textIntAnimator;
    private float textSwappingProgress;
    private ValueAnimator.AnimatorUpdateListener textSwappingUpdateListener;
    private ValueAnimator textSwipingAnimator;
    private final TextPaint tooltipTextPaint;
    private float touchSlop;
    boolean wasSendCallback;
    private boolean willDraw;
    private int backgroundColorKey = Theme.key_chats_archivePullDownBackground;
    private int backgroundActiveColorKey = Theme.key_chats_archivePullDownBackgroundActive;
    private int avatarBackgroundColorKey = Theme.key_avatar_backgroundArchivedHidden;
    private boolean changeAvatarColor = true;
    private final Paint paintSecondary = new Paint(1);
    private final Paint paintWhite = new Paint(1);
    private final Paint paintBackgroundAccent = new Paint(1);
    private final Paint backgroundPaint = new Paint();
    private final RectF rectF = new RectF();

    public abstract float getViewOffset();

    public /* synthetic */ void lambda$new$0(ValueAnimator valueAnimator) {
        this.textSwappingProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    public /* synthetic */ void lambda$new$1(ValueAnimator valueAnimator) {
        this.textInProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    public PullForegroundDrawable(CharSequence charSequence, CharSequence charSequence2) {
        TextPaint textPaint = new TextPaint(1);
        this.tooltipTextPaint = textPaint;
        this.arrowDrawable = new ArrowDrawable();
        this.circleClipPath = new Path();
        this.textSwappingProgress = 1.0f;
        this.arrowRotateProgress = 1.0f;
        this.accentRevalProgress = 1.0f;
        this.accentRevalProgressOut = 1.0f;
        this.pullTooltipLayoutScale = 1.0f;
        this.releaseTooltipLayoutScale = 1.0f;
        this.textSwappingUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda1
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$new$0(valueAnimator);
            }
        };
        this.textInUpdateListener = new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda2
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$new$1(valueAnimator);
            }
        };
        this.textInRunnable = new Runnable() { // from class: org.telegram.ui.Components.PullForegroundDrawable.1
            @Override // java.lang.Runnable
            public void run() {
                PullForegroundDrawable.this.animateToTextIn = true;
                if (PullForegroundDrawable.this.textIntAnimator != null) {
                    PullForegroundDrawable.this.textIntAnimator.cancel();
                }
                PullForegroundDrawable.this.textInProgress = 0.0f;
                PullForegroundDrawable.this.textIntAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
                PullForegroundDrawable.this.textIntAnimator.addUpdateListener(PullForegroundDrawable.this.textInUpdateListener);
                PullForegroundDrawable.this.textIntAnimator.setInterpolator(new LinearInterpolator());
                PullForegroundDrawable.this.textIntAnimator.setDuration(150L);
                PullForegroundDrawable.this.textIntAnimator.start();
            }
        };
        this.wasSendCallback = false;
        textPaint.setTypeface(AndroidUtilities.bold());
        textPaint.setTextSize(AndroidUtilities.dp(16.0f));
        this.touchSlop = ViewConfiguration.get(ApplicationLoader.applicationContext).getScaledTouchSlop();
        this.pullTooltipText = charSequence;
        this.releaseTooltipText = charSequence2;
        try {
            this.generalTopicDrawable = ApplicationLoader.applicationContext.getResources().getDrawable(R.drawable.msg_filled_general).mutate();
        } catch (Exception unused) {
        }
    }

    private void checkTextLayouts(int i) {
        if (i != this.lastWidth) {
            this.pullTooltipLayout = new StaticLayout(this.pullTooltipText, this.tooltipTextPaint, AndroidUtilities.displaySize.x, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            float fMax = 0.0f;
            for (int i2 = 0; i2 < this.pullTooltipLayout.getLineCount(); i2++) {
                fMax = Math.max(fMax, this.pullTooltipLayout.getLineWidth(i2));
            }
            float f = i;
            this.pullTooltipLayoutScale = Math.min(1.0f, f / fMax);
            int iCeil = (int) Math.ceil(fMax);
            if (this.pullTooltipLayoutScale < 0.8f) {
                this.pullTooltipLayoutScale = 0.8f;
                iCeil = HintView2.cutInFancyHalf(this.pullTooltipText, this.tooltipTextPaint);
            }
            int i3 = iCeil;
            this.pullTooltipLayout = new StaticLayout(this.pullTooltipText, this.tooltipTextPaint, i3, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            this.pullTooltipLayoutLeft = i3;
            this.pullTooltipLayoutWidth = 0.0f;
            for (int i4 = 0; i4 < this.pullTooltipLayout.getLineCount(); i4++) {
                this.pullTooltipLayoutLeft = Math.min(this.pullTooltipLayoutLeft, this.pullTooltipLayout.getLineLeft(i4));
                this.pullTooltipLayoutWidth = Math.max(this.pullTooltipLayoutWidth, this.pullTooltipLayout.getLineWidth(i4));
            }
            this.releaseTooltipLayout = new StaticLayout(this.releaseTooltipText, this.tooltipTextPaint, AndroidUtilities.displaySize.x, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            float fMax2 = 0.0f;
            for (int i5 = 0; i5 < this.releaseTooltipLayout.getLineCount(); i5++) {
                fMax2 = Math.max(fMax2, this.releaseTooltipLayout.getLineWidth(i5));
            }
            this.releaseTooltipLayoutScale = Math.min(1.0f, f / fMax2);
            int iCeil2 = (int) Math.ceil(fMax2);
            if (this.releaseTooltipLayoutScale < 0.8f) {
                this.releaseTooltipLayoutScale = 0.8f;
                iCeil2 = HintView2.cutInFancyHalf(this.releaseTooltipText, this.tooltipTextPaint);
            }
            int i6 = iCeil2;
            this.releaseTooltipLayout = new StaticLayout(this.releaseTooltipText, this.tooltipTextPaint, i6, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
            this.releaseTooltipLayoutLeft = i6;
            this.releaseTooltipLayoutWidth = 0.0f;
            for (int i7 = 0; i7 < this.releaseTooltipLayout.getLineCount(); i7++) {
                this.releaseTooltipLayoutLeft = Math.min(this.releaseTooltipLayoutLeft, this.releaseTooltipLayout.getLineLeft(i7));
                this.releaseTooltipLayoutWidth = Math.max(this.releaseTooltipLayoutWidth, this.releaseTooltipLayout.getLineWidth(i7));
            }
            this.lastWidth = i;
        }
    }

    public static int getMaxOverscroll() {
        return AndroidUtilities.dp(72.0f);
    }

    public void setCell(View view) {
        this.cell = view;
        updateColors();
    }

    public void updateColors() {
        int color = Theme.getColor(this.backgroundColorKey);
        this.tooltipTextPaint.setColor(-1);
        this.paintWhite.setColor(-1);
        this.paintSecondary.setColor(ColorUtils.setAlphaComponent(-1, 100));
        this.backgroundPaint.setColor(color);
        this.arrowDrawable.setColor(color);
        this.paintBackgroundAccent.setColor(Theme.getColor(this.avatarBackgroundColorKey));
    }

    public void setListView(RecyclerListView recyclerListView) {
        this.listView = recyclerListView;
    }

    public void drawOverScroll(Canvas canvas) {
        draw(canvas, true);
    }

    public void draw(Canvas canvas) {
        draw(canvas, false);
    }

    /* JADX WARN: Code duplicated, block: B:100:0x0497  */
    /* JADX WARN: Code duplicated, block: B:103:0x04a1  */
    /* JADX WARN: Code duplicated, block: B:115:0x0534  */
    /* JADX WARN: Code duplicated, block: B:70:0x0385  */
    /* JADX WARN: Code duplicated, block: B:72:0x0398  */
    /* JADX WARN: Code duplicated, block: B:73:0x03a5  */
    /* JADX WARN: Code duplicated, block: B:76:0x03c9  */
    /* JADX WARN: Code duplicated, block: B:78:0x03cd A[DONT_INVERT] */
    /* JADX WARN: Code duplicated, block: B:79:0x03cf  */
    /* JADX WARN: Code duplicated, block: B:80:0x03e4  */
    /* JADX WARN: Code duplicated, block: B:86:0x03f2  */
    /* JADX WARN: Code duplicated, block: B:88:0x0428  */
    /* JADX WARN: Code duplicated, block: B:91:0x0447  */
    /* JADX WARN: Code duplicated, block: B:92:0x044e  */
    /* JADX WARN: Code duplicated, block: B:96:0x0475  */
    /* JADX WARN: Code duplicated, block: B:99:0x0494  */
    /* JADX WARN: Type inference failed for: r14v4, types: [boolean, int] */
    /* JADX WARN: Type inference failed for: r14v5 */
    /* JADX WARN: Type inference failed for: r14v6 */
    public void draw(Canvas canvas, boolean z) {
        View view;
        float f;
        float f2;
        int i;
        boolean z2;
        ?? r14;
        int i2;
        int i3;
        int measuredHeight;
        float f3;
        float f4;
        float f5;
        int color;
        float height;
        int i4;
        float f6;
        float f7;
        float f8;
        float f9;
        float f10;
        RectF rectF;
        Canvas canvas2 = canvas;
        if (!this.willDraw || this.isOut || (view = this.cell) == null || this.listView == null) {
            return;
        }
        boolean z3 = view instanceof TopicsFragment.TopicDialogCell;
        int iDp = AndroidUtilities.dp(z3 ? 15.0f : 28.0f);
        int iDp2 = AndroidUtilities.dp(8.0f);
        int iDp3 = AndroidUtilities.dp(9.0f);
        int iDp4 = AndroidUtilities.dp(18.0f);
        int viewOffset = (int) getViewOffset();
        int height2 = (int) (this.cell.getHeight() * getPullProgress());
        boolean z4 = this.bounceIn;
        float f11 = this.bounceProgress;
        float f12 = z4 ? (f11 * 0.07f) - 0.05f : f11 * 0.02f;
        checkTextLayouts((this.cell.getWidth() - (iDp * 4)) - AndroidUtilities.dp(16.0f));
        updateTextProgress(getPullProgress());
        float f13 = this.outProgress * 2.0f;
        float f14 = f13 > 1.0f ? 1.0f : f13;
        float f15 = this.outCx;
        float f16 = this.outCy;
        if (z) {
            f16 += viewOffset;
        }
        int i5 = iDp + iDp3;
        int measuredHeight2 = (this.cell.getMeasuredHeight() - iDp2) - iDp3;
        if (z) {
            measuredHeight2 += viewOffset;
        }
        int i6 = (iDp2 * 2) + iDp4;
        float f17 = height2 > i6 ? 1.0f : height2 / i6;
        canvas2.save();
        if (z) {
            canvas2.clipRect(0, 0, this.listView.getMeasuredWidth(), viewOffset + 1);
        }
        if (this.outProgress == 0.0f) {
            if (this.accentRevalProgress != 1.0f && this.accentRevalProgressOut != 1.0f) {
                canvas2.drawPaint(this.backgroundPaint);
            }
            f2 = 0.0f;
            f = f16;
            z3 = z3;
            f17 = f17;
            viewOffset = viewOffset;
            height2 = height2;
            f15 = f15;
            i = i5;
            measuredHeight2 = measuredHeight2;
            i6 = i6;
            iDp4 = iDp4;
            r14 = 1;
        } else {
            float f18 = this.outRadius;
            float width = f18 + (f18 * f12) + ((this.cell.getWidth() - this.outRadius) * (1.0f - this.outProgress));
            if (this.accentRevalProgress == 1.0f || this.accentRevalProgressOut == 1.0f) {
                f = f16;
                f2 = 0.0f;
                i = i5;
                z2 = true;
            } else {
                float f19 = f16;
                float f20 = width * 2.0f;
                f = f19;
                f2 = 0.0f;
                i = i5;
                z2 = true;
                canvas2 = canvas;
                canvas2.drawRoundRect(f15 - width, f16 - width, f15 + width, f19 + width, ExteraConfig.getAvatarCorners(f20, true, false), ExteraConfig.getAvatarCorners(f20, true, false), this.backgroundPaint);
            }
            this.circleClipPath.reset();
            this.rectF.set(f15 - width, f - width, f15 + width, f + width);
            float f21 = width * 2.0f;
            this.circleClipPath.addRoundRect(this.rectF, ExteraConfig.getAvatarCorners(f21, z2, false), ExteraConfig.getAvatarCorners(f21, z2, false), Path.Direction.CW);
            canvas2.clipPath(this.circleClipPath);
            r14 = z2;
        }
        if (!this.animateToColorize) {
            if (this.accentRevalProgress > this.accentRevalProgressOut) {
                canvas2.save();
                float f22 = i;
                float f23 = this.outProgress;
                float f24 = measuredHeight2;
                canvas2.translate((f15 - f22) * f23, (f - f24) * f23);
                canvas2.drawRoundRect(f22 - (this.cell.getWidth() * this.accentRevalProgress), f24 - (this.cell.getWidth() * this.accentRevalProgress), f22 + (this.cell.getWidth() * this.accentRevalProgress), f24 + (this.cell.getWidth() * this.accentRevalProgress), ExteraConfig.getAvatarCorners(this.cell.getWidth() * 2, (boolean) r14, false) * this.accentRevalProgress, ExteraConfig.getAvatarCorners(this.cell.getWidth() * 2, (boolean) r14, false) * this.accentRevalProgress, this.paintBackgroundAccent);
                canvas2.restore();
            }
            if (this.accentRevalProgressOut > f2) {
                canvas2.save();
                float f25 = i;
                float f26 = this.outProgress;
                float f27 = measuredHeight2;
                canvas2.translate((f15 - f25) * f26, (f - f27) * f26);
                i2 = 0;
                canvas2.drawRoundRect(f25 - (this.cell.getWidth() * this.accentRevalProgressOut), f27 - (this.cell.getWidth() * this.accentRevalProgressOut), f25 + (this.cell.getWidth() * this.accentRevalProgressOut), f27 + (this.cell.getWidth() * this.accentRevalProgressOut), ExteraConfig.getAvatarCorners(this.cell.getWidth() * 2, (boolean) r14, false) * this.accentRevalProgressOut, this.accentRevalProgressOut * ExteraConfig.getAvatarCorners(this.cell.getWidth() * 2, (boolean) r14, false), this.backgroundPaint);
                canvas2.restore();
            }
            i3 = height2;
            if (i3 > i6) {
                this.paintSecondary.setAlpha((int) ((1.0f - f14) * 0.4f * f17 * 255.0f));
                rectF = this.rectF;
                if (z) {
                    rectF.set(iDp, iDp2, iDp + iDp4, iDp2 + viewOffset + iDp3);
                } else {
                    rectF.set(iDp, ((this.cell.getHeight() - i3) + iDp2) - viewOffset, iDp + iDp4, this.cell.getHeight() - iDp2);
                }
                float f28 = iDp3;
                canvas2.drawRoundRect(this.rectF, f28, f28, this.paintSecondary);
            }
            if (z) {
                canvas2.restore();
                return;
            }
            if (z3) {
                measuredHeight = (int) (measuredHeight2 - ((this.cell.getMeasuredHeight() - AndroidUtilities.dp(41.0f)) * this.outProgress));
            } else {
                measuredHeight = measuredHeight2;
            }
            f3 = this.outProgress;
            if (f3 != f2 || z3) {
                this.paintWhite.setAlpha((int) (f17 * 255.0f * (1.0f - f3)));
                float f29 = i;
                float f30 = measuredHeight;
                canvas2.drawCircle(f29, f30, iDp3, this.paintWhite);
                int intrinsicHeight = this.arrowDrawable.getIntrinsicHeight();
                int intrinsicWidth = this.arrowDrawable.getIntrinsicWidth() >> r14;
                int i7 = intrinsicHeight >> r14;
                f4 = 255.0f;
                this.arrowDrawable.setBounds(i - intrinsicWidth, measuredHeight - i7, intrinsicWidth + i, measuredHeight + i7);
                f5 = 1.0f - this.arrowRotateProgress;
                if (f5 < f2) {
                    f5 = f2;
                }
                float f31 = 1.0f - f5;
                canvas2.save();
                canvas2.rotate(180.0f * f31, f29, f30);
                canvas2.translate(f2, (AndroidUtilities.dpf2(1.0f) * 1.0f) - f31);
                ArrowDrawable arrowDrawable = this.arrowDrawable;
                if (this.animateToColorize) {
                    color = this.paintBackgroundAccent.getColor();
                } else {
                    color = Theme.getColor(this.backgroundColorKey);
                }
                arrowDrawable.setColor(color);
                this.arrowDrawable.setAlpha((int) ((1.0f - this.outProgress) * 255.0f));
                this.arrowDrawable.draw(canvas2);
                canvas2.restore();
            } else {
                f4 = 255.0f;
            }
            if (getPullProgress() > 0.0f) {
                textIn();
            }
            height = (this.cell.getHeight() - (i6 / 2.0f)) + AndroidUtilities.dp(6.0f);
            int width2 = this.cell.getWidth();
            if (z3) {
                i4 = iDp * 2;
            } else {
                i4 = i2;
            }
            f6 = (width2 + i4) / 2.0f;
            if (this.pullTooltipLayout != null) {
                f9 = this.textSwappingProgress;
                if (f9 > 0.0f && f9 < 1.0f) {
                    canvas2.save();
                    float f32 = (this.textSwappingProgress * 0.2f) + 0.8f;
                    canvas2.scale(f32, f32, f6, (AndroidUtilities.dp(16.0f) * (1.0f - this.textSwappingProgress)) + height);
                }
                canvas2.saveLayerAlpha(0.0f, 0.0f, this.cell.getMeasuredWidth(), this.cell.getMeasuredHeight(), (int) (this.textSwappingProgress * f4 * f17 * this.textInProgress), 31);
                canvas2.translate((f6 - this.pullTooltipLayoutLeft) - (this.pullTooltipLayoutWidth / 2.0f), ((AndroidUtilities.dp(8.0f) * (1.0f - this.textSwappingProgress)) + height) - this.pullTooltipLayout.getHeight());
                float f33 = this.pullTooltipLayoutScale;
                canvas2.scale(f33, f33, this.pullTooltipLayoutLeft + (this.pullTooltipLayoutWidth / 2.0f), this.pullTooltipLayout.getHeight());
                this.pullTooltipLayout.draw(canvas2);
                canvas2.restore();
                f10 = this.textSwappingProgress;
                if (f10 > 0.0f && f10 < 1.0f) {
                    canvas2.restore();
                }
            }
            if (this.releaseTooltipLayout != null) {
                f7 = this.textSwappingProgress;
                if (f7 > 0.0f && f7 < 1.0f) {
                    canvas2.save();
                    float f34 = ((1.0f - this.textSwappingProgress) * 0.1f) + 0.9f;
                    canvas2.scale(f34, f34, f6, height - (AndroidUtilities.dp(r11) * this.textSwappingProgress));
                }
                canvas2.saveLayerAlpha(0.0f, 0.0f, this.cell.getMeasuredWidth(), this.cell.getMeasuredHeight(), (int) ((1.0f - this.textSwappingProgress) * f4 * f17 * this.textInProgress), 31);
                canvas2.translate((f6 - this.releaseTooltipLayoutLeft) - (this.releaseTooltipLayoutWidth / 2.0f), (height + (AndroidUtilities.dp(r11) * this.textSwappingProgress)) - this.releaseTooltipLayout.getHeight());
                float f35 = this.releaseTooltipLayoutScale;
                canvas2.scale(f35, f35, this.releaseTooltipLayoutLeft + (this.releaseTooltipLayoutWidth / 2.0f), this.releaseTooltipLayout.getHeight());
                this.releaseTooltipLayout.draw(canvas2);
                canvas2.restore();
                f8 = this.textSwappingProgress;
                if (f8 > 0.0f && f8 < 1.0f) {
                    canvas2.restore();
                }
            }
            canvas2.restore();
            if (z3 && this.changeAvatarColor && this.outProgress > 0.0f) {
                canvas2.save();
                int intrinsicWidth2 = Theme.dialogs_archiveAvatarDrawable.getIntrinsicWidth();
                int height3 = (this.cell.getHeight() - iDp2) - iDp3;
                float f36 = intrinsicWidth2;
                float fDp = AndroidUtilities.dp(24.0f) / f36;
                float f37 = this.outProgress;
                float f38 = fDp + ((1.0f - fDp) * f37) + f12;
                canvas2.translate((i - f15) * (1.0f - f37), (height3 - f) * (1.0f - f37));
                float f39 = f15;
                float f40 = f;
                canvas2.scale(f38, f38, f39, f40);
                Theme.dialogs_archiveAvatarDrawable.setProgress(0.0f);
                if (!Theme.dialogs_archiveAvatarDrawableRecolored) {
                    Theme.dialogs_archiveAvatarDrawable.beginApplyLayerColors();
                    Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow1.**", Theme.getNonAnimatedColor(this.avatarBackgroundColorKey));
                    Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow2.**", Theme.getNonAnimatedColor(this.avatarBackgroundColorKey));
                    Theme.dialogs_archiveAvatarDrawable.commitApplyLayerColors();
                    Theme.dialogs_archiveAvatarDrawableRecolored = r14;
                }
                float f41 = f36 / 2.0f;
                Theme.dialogs_archiveAvatarDrawable.setBounds((int) (f39 - f41), (int) (f40 - f41), (int) (f39 + f41), (int) (f41 + f40));
                Theme.dialogs_archiveAvatarDrawable.draw(canvas2);
                canvas2.restore();
                return;
            }
            return;
        }
        if (this.accentRevalProgressOut > this.accentRevalProgress) {
            canvas2.save();
            float f42 = i;
            float f43 = this.outProgress;
            float f44 = measuredHeight2;
            canvas2.translate((f15 - f42) * f43, (f - f44) * f43);
            canvas2.drawRoundRect(f42 - (this.cell.getWidth() * this.accentRevalProgressOut), f44 - (this.cell.getWidth() * this.accentRevalProgressOut), f42 + (this.cell.getWidth() * this.accentRevalProgressOut), f44 + (this.cell.getWidth() * this.accentRevalProgressOut), ExteraConfig.getAvatarCorners(this.cell.getWidth() * 2, (boolean) r14, false) * this.accentRevalProgressOut, ExteraConfig.getAvatarCorners(this.cell.getWidth() * 2, (boolean) r14, false) * this.accentRevalProgressOut, this.backgroundPaint);
            canvas2.restore();
        }
        if (this.accentRevalProgress > f2) {
            canvas2.save();
            float f45 = i;
            float f46 = this.outProgress;
            float f47 = measuredHeight2;
            canvas2.translate((f15 - f45) * f46, (f - f47) * f46);
            canvas2.drawRoundRect(f45 - (this.cell.getWidth() * this.accentRevalProgress), f47 - (this.cell.getWidth() * this.accentRevalProgress), f45 + (this.cell.getWidth() * this.accentRevalProgress), f47 + (this.cell.getWidth() * this.accentRevalProgress), ExteraConfig.getAvatarCorners(this.cell.getWidth() * 2, (boolean) r14, false) * this.accentRevalProgress, ExteraConfig.getAvatarCorners(this.cell.getWidth() * 2, (boolean) r14, false) * this.accentRevalProgress, this.paintBackgroundAccent);
            canvas2.restore();
        }
        i2 = 0;
        i3 = height2;
        if (i3 > i6) {
            this.paintSecondary.setAlpha((int) ((1.0f - f14) * 0.4f * f17 * 255.0f));
            rectF = this.rectF;
            if (z) {
                rectF.set(iDp, iDp2, iDp + iDp4, iDp2 + viewOffset + iDp3);
            } else {
                rectF.set(iDp, ((this.cell.getHeight() - i3) + iDp2) - viewOffset, iDp + iDp4, this.cell.getHeight() - iDp2);
            }
            float f210 = iDp3;
            canvas2.drawRoundRect(this.rectF, f210, f210, this.paintSecondary);
        }
        if (z) {
            canvas2.restore();
            return;
        }
        if (z3) {
            measuredHeight = (int) (measuredHeight2 - ((this.cell.getMeasuredHeight() - AndroidUtilities.dp(41.0f)) * this.outProgress));
        } else {
            measuredHeight = measuredHeight2;
        }
        f3 = this.outProgress;
        if (f3 != f2) {
            this.paintWhite.setAlpha((int) (f17 * 255.0f * (1.0f - f3)));
            float f211 = i;
            float f310 = measuredHeight;
            canvas2.drawCircle(f211, f310, iDp3, this.paintWhite);
            int intrinsicHeight2 = this.arrowDrawable.getIntrinsicHeight();
            int intrinsicWidth3 = this.arrowDrawable.getIntrinsicWidth() >> r14;
            int i8 = intrinsicHeight2 >> r14;
            f4 = 255.0f;
            this.arrowDrawable.setBounds(i - intrinsicWidth3, measuredHeight - i8, intrinsicWidth3 + i, measuredHeight + i8);
            f5 = 1.0f - this.arrowRotateProgress;
            if (f5 < f2) {
                f5 = f2;
            }
            float f311 = 1.0f - f5;
            canvas2.save();
            canvas2.rotate(180.0f * f311, f211, f310);
            canvas2.translate(f2, (AndroidUtilities.dpf2(1.0f) * 1.0f) - f311);
            ArrowDrawable arrowDrawable2 = this.arrowDrawable;
            if (this.animateToColorize) {
                color = this.paintBackgroundAccent.getColor();
            } else {
                color = Theme.getColor(this.backgroundColorKey);
            }
            arrowDrawable2.setColor(color);
            this.arrowDrawable.setAlpha((int) ((1.0f - this.outProgress) * 255.0f));
            this.arrowDrawable.draw(canvas2);
            canvas2.restore();
        } else {
            this.paintWhite.setAlpha((int) (f17 * 255.0f * (1.0f - f3)));
            float f212 = i;
            float f312 = measuredHeight;
            canvas2.drawCircle(f212, f312, iDp3, this.paintWhite);
            int intrinsicHeight3 = this.arrowDrawable.getIntrinsicHeight();
            int intrinsicWidth4 = this.arrowDrawable.getIntrinsicWidth() >> r14;
            int i9 = intrinsicHeight3 >> r14;
            f4 = 255.0f;
            this.arrowDrawable.setBounds(i - intrinsicWidth4, measuredHeight - i9, intrinsicWidth4 + i, measuredHeight + i9);
            f5 = 1.0f - this.arrowRotateProgress;
            if (f5 < f2) {
                f5 = f2;
            }
            float f313 = 1.0f - f5;
            canvas2.save();
            canvas2.rotate(180.0f * f313, f212, f312);
            canvas2.translate(f2, (AndroidUtilities.dpf2(1.0f) * 1.0f) - f313);
            ArrowDrawable arrowDrawable3 = this.arrowDrawable;
            if (this.animateToColorize) {
                color = this.paintBackgroundAccent.getColor();
            } else {
                color = Theme.getColor(this.backgroundColorKey);
            }
            arrowDrawable3.setColor(color);
            this.arrowDrawable.setAlpha((int) ((1.0f - this.outProgress) * 255.0f));
            this.arrowDrawable.draw(canvas2);
            canvas2.restore();
        }
        if (getPullProgress() > 0.0f) {
            textIn();
        }
        height = (this.cell.getHeight() - (i6 / 2.0f)) + AndroidUtilities.dp(6.0f);
        int width3 = this.cell.getWidth();
        if (z3) {
            i4 = iDp * 2;
        } else {
            i4 = i2;
        }
        f6 = (width3 + i4) / 2.0f;
        if (this.pullTooltipLayout != null) {
            f9 = this.textSwappingProgress;
            if (f9 > 0.0f) {
                canvas2.save();
                float f314 = (this.textSwappingProgress * 0.2f) + 0.8f;
                canvas2.scale(f314, f314, f6, (AndroidUtilities.dp(16.0f) * (1.0f - this.textSwappingProgress)) + height);
            }
            canvas2.saveLayerAlpha(0.0f, 0.0f, this.cell.getMeasuredWidth(), this.cell.getMeasuredHeight(), (int) (this.textSwappingProgress * f4 * f17 * this.textInProgress), 31);
            canvas2.translate((f6 - this.pullTooltipLayoutLeft) - (this.pullTooltipLayoutWidth / 2.0f), ((AndroidUtilities.dp(8.0f) * (1.0f - this.textSwappingProgress)) + height) - this.pullTooltipLayout.getHeight());
            float f315 = this.pullTooltipLayoutScale;
            canvas2.scale(f315, f315, this.pullTooltipLayoutLeft + (this.pullTooltipLayoutWidth / 2.0f), this.pullTooltipLayout.getHeight());
            this.pullTooltipLayout.draw(canvas2);
            canvas2.restore();
            f10 = this.textSwappingProgress;
            if (f10 > 0.0f) {
                canvas2.restore();
            }
        }
        if (this.releaseTooltipLayout != null) {
            f7 = this.textSwappingProgress;
            if (f7 > 0.0f) {
                canvas2.save();
                float f316 = ((1.0f - this.textSwappingProgress) * 0.1f) + 0.9f;
                canvas2.scale(f316, f316, f6, height - (AndroidUtilities.dp(r11) * this.textSwappingProgress));
            }
            canvas2.saveLayerAlpha(0.0f, 0.0f, this.cell.getMeasuredWidth(), this.cell.getMeasuredHeight(), (int) ((1.0f - this.textSwappingProgress) * f4 * f17 * this.textInProgress), 31);
            canvas2.translate((f6 - this.releaseTooltipLayoutLeft) - (this.releaseTooltipLayoutWidth / 2.0f), (height + (AndroidUtilities.dp(r11) * this.textSwappingProgress)) - this.releaseTooltipLayout.getHeight());
            float f317 = this.releaseTooltipLayoutScale;
            canvas2.scale(f317, f317, this.releaseTooltipLayoutLeft + (this.releaseTooltipLayoutWidth / 2.0f), this.releaseTooltipLayout.getHeight());
            this.releaseTooltipLayout.draw(canvas2);
            canvas2.restore();
            f8 = this.textSwappingProgress;
            if (f8 > 0.0f) {
                canvas2.restore();
            }
        }
        canvas2.restore();
        if (z3) {
        }
    }

    private void updateTextProgress(float f) {
        boolean z = f > 0.85f;
        if (this.animateToEndText != z) {
            this.animateToEndText = z;
            float f2 = this.textInProgress;
            ValueAnimator valueAnimator = this.textSwipingAnimator;
            if (f2 == 0.0f) {
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                this.textSwappingProgress = z ? 0.0f : 1.0f;
            } else {
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                }
                ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(this.textSwappingProgress, z ? 0.0f : 1.0f);
                this.textSwipingAnimator = valueAnimatorOfFloat;
                valueAnimatorOfFloat.addUpdateListener(this.textSwappingUpdateListener);
                this.textSwipingAnimator.setInterpolator(new LinearInterpolator());
                this.textSwipingAnimator.setDuration(170L);
                this.textSwipingAnimator.start();
            }
        }
        if (z != this.arrowAnimateTo) {
            this.arrowAnimateTo = z;
            ValueAnimator valueAnimator2 = this.arrowRotateAnimator;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
            }
            ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(this.arrowRotateProgress, this.arrowAnimateTo ? 0.0f : 1.0f);
            this.arrowRotateAnimator = valueAnimatorOfFloat2;
            valueAnimatorOfFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda0
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    this.f$0.lambda$updateTextProgress$2(valueAnimator3);
                }
            });
            this.arrowRotateAnimator.setInterpolator(CubicBezierInterpolator.EASE_BOTH);
            this.arrowRotateAnimator.setDuration(250L);
            this.arrowRotateAnimator.start();
        }
    }

    public /* synthetic */ void lambda$updateTextProgress$2(ValueAnimator valueAnimator) {
        this.arrowRotateProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    public void colorize(boolean z) {
        if (this.animateToColorize != z) {
            this.animateToColorize = z;
            if (z) {
                ValueAnimator valueAnimator = this.accentRevalAnimatorIn;
                if (valueAnimator != null) {
                    valueAnimator.cancel();
                    this.accentRevalAnimatorIn = null;
                }
                this.accentRevalProgress = 0.0f;
                ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
                this.accentRevalAnimatorIn = valueAnimatorOfFloat;
                valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda6
                    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                    public final void onAnimationUpdate(ValueAnimator valueAnimator2) {
                        this.f$0.lambda$colorize$3(valueAnimator2);
                    }
                });
                this.accentRevalAnimatorIn.setInterpolator(AndroidUtilities.accelerateInterpolator);
                this.accentRevalAnimatorIn.setDuration(230L);
                this.accentRevalAnimatorIn.start();
                return;
            }
            ValueAnimator valueAnimator2 = this.accentRevalAnimatorOut;
            if (valueAnimator2 != null) {
                valueAnimator2.cancel();
                this.accentRevalAnimatorOut = null;
            }
            this.accentRevalProgressOut = 0.0f;
            ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.accentRevalAnimatorOut = valueAnimatorOfFloat2;
            valueAnimatorOfFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda7
                @Override // android.animation.ValueAnimator.AnimatorUpdateListener
                public final void onAnimationUpdate(ValueAnimator valueAnimator3) {
                    this.f$0.lambda$colorize$4(valueAnimator3);
                }
            });
            this.accentRevalAnimatorOut.setInterpolator(AndroidUtilities.accelerateInterpolator);
            this.accentRevalAnimatorOut.setDuration(230L);
            this.accentRevalAnimatorOut.start();
        }
    }

    public /* synthetic */ void lambda$colorize$3(ValueAnimator valueAnimator) {
        this.accentRevalProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidate();
        }
    }

    public /* synthetic */ void lambda$colorize$4(ValueAnimator valueAnimator) {
        this.accentRevalProgressOut = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
        RecyclerListView recyclerListView = this.listView;
        if (recyclerListView != null) {
            recyclerListView.invalidate();
        }
    }

    private void textIn() {
        if (this.animateToTextIn) {
            return;
        }
        if (Math.abs(this.scrollDy) < this.touchSlop * 0.5f) {
            if (this.wasSendCallback) {
                return;
            }
            this.textInProgress = 1.0f;
            this.animateToTextIn = true;
            return;
        }
        this.wasSendCallback = true;
        this.cell.removeCallbacks(this.textInRunnable);
        this.cell.postDelayed(this.textInRunnable, 200L);
    }

    public void startOutAnimation() {
        if (this.animateOut || this.listView == null) {
            return;
        }
        AnimatorSet animatorSet = this.outAnimator;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.outAnimator.cancel();
        }
        this.animateOut = true;
        this.bounceIn = true;
        this.bounceProgress = 0.0f;
        this.outOverScroll = this.listView.getTranslationY() / AndroidUtilities.dp(100.0f);
        ValueAnimator valueAnimatorOfFloat = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda3
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$startOutAnimation$5(valueAnimator);
            }
        });
        valueAnimatorOfFloat.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        valueAnimatorOfFloat.setDuration(250L);
        ValueAnimator valueAnimatorOfFloat2 = ValueAnimator.ofFloat(0.0f, 1.0f);
        valueAnimatorOfFloat2.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda4
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$startOutAnimation$6(valueAnimator);
            }
        });
        CubicBezierInterpolator cubicBezierInterpolator = CubicBezierInterpolator.EASE_BOTH;
        valueAnimatorOfFloat2.setInterpolator(cubicBezierInterpolator);
        valueAnimatorOfFloat2.setDuration(150L);
        ValueAnimator valueAnimatorOfFloat3 = ValueAnimator.ofFloat(1.0f, 0.0f);
        valueAnimatorOfFloat3.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() { // from class: org.telegram.ui.Components.PullForegroundDrawable$$ExternalSyntheticLambda5
            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public final void onAnimationUpdate(ValueAnimator valueAnimator) {
                this.f$0.lambda$startOutAnimation$7(valueAnimator);
            }
        });
        valueAnimatorOfFloat3.setInterpolator(cubicBezierInterpolator);
        valueAnimatorOfFloat3.setDuration(135L);
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.outAnimator = animatorSet2;
        animatorSet2.addListener(new AnimatorListenerAdapter() { // from class: org.telegram.ui.Components.PullForegroundDrawable.2
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                PullForegroundDrawable.this.doNotShow();
            }
        });
        AnimatorSet animatorSet3 = new AnimatorSet();
        animatorSet3.playSequentially(valueAnimatorOfFloat2, valueAnimatorOfFloat3);
        animatorSet3.setStartDelay(180L);
        this.outAnimator.playTogether(valueAnimatorOfFloat, animatorSet3);
        this.outAnimator.start();
    }

    public /* synthetic */ void lambda$startOutAnimation$5(ValueAnimator valueAnimator) {
        setOutProgress(((Float) valueAnimator.getAnimatedValue()).floatValue());
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    public /* synthetic */ void lambda$startOutAnimation$6(ValueAnimator valueAnimator) {
        this.bounceProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.bounceIn = true;
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    public /* synthetic */ void lambda$startOutAnimation$7(ValueAnimator valueAnimator) {
        this.bounceProgress = ((Float) valueAnimator.getAnimatedValue()).floatValue();
        this.bounceIn = false;
        View view = this.cell;
        if (view != null) {
            view.invalidate();
        }
    }

    private void setOutProgress(float f) {
        this.outProgress = f;
        int iBlendARGB = ColorUtils.blendARGB(Theme.getNonAnimatedColor(this.avatarBackgroundColorKey), Theme.getNonAnimatedColor(this.backgroundActiveColorKey), 1.0f - this.outProgress);
        this.paintBackgroundAccent.setColor(iBlendARGB);
        if (this.changeAvatarColor && isDraw()) {
            Theme.dialogs_archiveAvatarDrawable.beginApplyLayerColors();
            Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow1.**", iBlendARGB);
            Theme.dialogs_archiveAvatarDrawable.setLayerColor("Arrow2.**", iBlendARGB);
            Theme.dialogs_archiveAvatarDrawable.commitApplyLayerColors();
            Theme.dialogs_archiveAvatarDrawableRecolored = true;
        }
    }

    public void doNotShow() {
        ValueAnimator valueAnimator = this.textSwipingAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        ValueAnimator valueAnimator2 = this.textIntAnimator;
        if (valueAnimator2 != null) {
            valueAnimator2.cancel();
        }
        View view = this.cell;
        if (view != null) {
            view.removeCallbacks(this.textInRunnable);
        }
        ValueAnimator valueAnimator3 = this.accentRevalAnimatorIn;
        if (valueAnimator3 != null) {
            valueAnimator3.cancel();
        }
        this.textSwappingProgress = 1.0f;
        this.arrowRotateProgress = 1.0f;
        this.animateToEndText = false;
        this.arrowAnimateTo = false;
        this.animateToTextIn = false;
        this.wasSendCallback = false;
        this.textInProgress = 0.0f;
        this.isOut = true;
        setOutProgress(1.0f);
        this.animateToColorize = false;
        this.accentRevalProgress = 0.0f;
    }

    public void showHidden() {
        AnimatorSet animatorSet = this.outAnimator;
        if (animatorSet != null) {
            animatorSet.removeAllListeners();
            this.outAnimator.cancel();
        }
        setOutProgress(0.0f);
        this.isOut = false;
        this.animateOut = false;
    }

    public boolean isDraw() {
        return this.willDraw && !this.isOut;
    }

    public void setWillDraw(boolean z) {
        this.willDraw = z;
    }

    public void resetText() {
        ValueAnimator valueAnimator = this.textIntAnimator;
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        View view = this.cell;
        if (view != null) {
            view.removeCallbacks(this.textInRunnable);
        }
        this.textInProgress = 0.0f;
        this.animateToTextIn = false;
        this.wasSendCallback = false;
    }

    public float getPullProgress() {
        return this.pullProgress;
    }

    public void setPullProgress(float f) {
        if (this.pullProgress != f) {
            this.pullProgress = f;
            View view = this.cell;
            if (view != null) {
                view.invalidate();
            }
        }
    }

    public class ArrowDrawable extends Drawable {
        private float lastDensity;
        private Path path = new Path();
        private Paint paint = new Paint(1);

        @Override // android.graphics.drawable.Drawable
        public int getOpacity() {
            return 0;
        }

        @Override // android.graphics.drawable.Drawable
        public void setAlpha(int i) {
        }

        @Override // android.graphics.drawable.Drawable
        public void setColorFilter(ColorFilter colorFilter) {
        }

        public ArrowDrawable() {
            updatePath();
        }

        private void updatePath() {
            int iDp = AndroidUtilities.dp(18.0f);
            this.path.reset();
            float f = iDp >> 1;
            this.path.moveTo(f, AndroidUtilities.dpf2(4.98f));
            this.path.lineTo(AndroidUtilities.dpf2(4.95f), AndroidUtilities.dpf2(9.0f));
            this.path.lineTo(iDp - AndroidUtilities.dpf2(4.95f), AndroidUtilities.dpf2(9.0f));
            this.path.lineTo(f, AndroidUtilities.dpf2(4.98f));
            this.paint.setStyle(Paint.Style.FILL_AND_STROKE);
            this.paint.setStrokeJoin(Paint.Join.ROUND);
            this.paint.setStrokeWidth(AndroidUtilities.dpf2(1.0f));
            this.lastDensity = AndroidUtilities.density;
        }

        public void setColor(int i) {
            this.paint.setColor(i);
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicHeight() {
            return AndroidUtilities.dp(18.0f);
        }

        @Override // android.graphics.drawable.Drawable
        public int getIntrinsicWidth() {
            return getIntrinsicHeight();
        }

        @Override // android.graphics.drawable.Drawable
        public void draw(Canvas canvas) {
            if (this.lastDensity != AndroidUtilities.density) {
                updatePath();
            }
            canvas.save();
            canvas.translate(getBounds().left, getBounds().top);
            canvas.drawPath(this.path, this.paint);
            canvas.drawRect(AndroidUtilities.dpf2(7.56f), AndroidUtilities.dpf2(8.0f), AndroidUtilities.dp(18.0f) - AndroidUtilities.dpf2(7.56f), AndroidUtilities.dpf2(11.1f), this.paint);
            canvas.restore();
        }
    }
}
