package com.exteragram.messenger.updater;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Property;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.widget.NestedScrollView;
import com.exteragram.messenger.ExteraConfig;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.DocumentObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLocation;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.SvgHelper;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.BottomSheet;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.spoilers.SpoilersTextView;
import org.telegram.ui.Stories.recorder.ButtonWithCounterView;

public class UpdateAppAlertDialog extends BottomSheet {
    private final int accountNum;
    private final TLRPC.TL_help_appUpdate appUpdate;
    protected final LinearLayout linearLayout;
    private final int[] location;
    private int scrollOffsetY;
    private final NestedScrollView scrollView;
    private final View shadow;
    private AnimatorSet shadowAnimation;
    private final Drawable shadowDrawable;
    private final TextView textView;

    public void addContentBeforeDoneButton(FrameLayout frameLayout) {
    }

    public UpdateAppAlertDialog(Activity activity, TLRPC.TL_help_appUpdate tL_help_appUpdate, int i) {
        super(activity, false);
        this.location = new int[2];
        this.appUpdate = tL_help_appUpdate;
        this.accountNum = i;
        fixNavigationBar();
        setApplyTopPadding(false);
        setApplyBottomPadding(false);
        Drawable drawableMutate = activity.getResources().getDrawable(R.drawable.sheet_shadow_round).mutate();
        this.shadowDrawable = drawableMutate;
        drawableMutate.setColorFilter(new PorterDuffColorFilter(Theme.getColor(Theme.key_dialogBackground), PorterDuff.Mode.MULTIPLY));
        FrameLayout frameLayout = new FrameLayout(activity) { 
            @Override // android.view.View
            public void setTranslationY(float f) {
                super.setTranslationY(f);
                UpdateAppAlertDialog.this.updateLayout();
            }

            @Override // android.view.ViewGroup
            public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
                if (motionEvent.getAction() == 0 && UpdateAppAlertDialog.this.scrollOffsetY != 0 && motionEvent.getY() < UpdateAppAlertDialog.this.scrollOffsetY) {
                    UpdateAppAlertDialog.this.dismiss();
                    return true;
                }
                return super.onInterceptTouchEvent(motionEvent);
            }

            @Override // android.view.View
            @SuppressLint({"ClickableViewAccessibility"})
            public boolean onTouchEvent(MotionEvent motionEvent) {
                return !UpdateAppAlertDialog.this.isDismissed() && super.onTouchEvent(motionEvent);
            }

            @Override // android.view.View
            public void onDraw(Canvas canvas) {
                UpdateAppAlertDialog.this.shadowDrawable.setBounds(0, (int) ((UpdateAppAlertDialog.this.scrollOffsetY - UpdateAppAlertDialog.this.getBackgroundPaddingTop()) - getTranslationY()), getMeasuredWidth(), getMeasuredHeight());
                UpdateAppAlertDialog.this.shadowDrawable.draw(canvas);
            }
        };
        frameLayout.setWillNotDraw(false);
        this.containerView = frameLayout;
        NestedScrollView nestedScrollView = new NestedScrollView(activity) { 
            private boolean ignoreLayout;

            @Override // androidx.core.widget.NestedScrollView, android.widget.FrameLayout, android.view.View
            public void onMeasure(int i2, int i3) {
                int size = View.MeasureSpec.getSize(i3);
                measureChildWithMargins(UpdateAppAlertDialog.this.linearLayout, i2, 0, i3, 0);
                int measuredHeight = UpdateAppAlertDialog.this.linearLayout.getMeasuredHeight();
                int i4 = (size / 5) * 2;
                if (measuredHeight - (size - i4) < AndroidUtilities.dp(90.0f) || measuredHeight < (size / 2) + AndroidUtilities.dp(90.0f)) {
                    i4 = size - measuredHeight;
                }
                if (i4 < 0) {
                    i4 = 0;
                }
                if (getPaddingTop() != i4) {
                    this.ignoreLayout = true;
                    setPadding(0, i4, 0, 0);
                    this.ignoreLayout = false;
                }
                super.onMeasure(i2, View.MeasureSpec.makeMeasureSpec(size, TLObject.FLAG_30));
            }

            @Override // androidx.core.widget.NestedScrollView, android.widget.FrameLayout, android.view.ViewGroup, android.view.View
            public void onLayout(boolean z, int i2, int i3, int i4, int i5) {
                super.onLayout(z, i2, i3, i4, i5);
                UpdateAppAlertDialog.this.updateLayout();
            }

            @Override // androidx.core.widget.NestedScrollView, android.view.View, android.view.ViewParent
            public void requestLayout() {
                if (this.ignoreLayout) {
                    return;
                }
                super.requestLayout();
            }

            @Override // androidx.core.widget.NestedScrollView, android.view.View
            public void onScrollChanged(int i2, int i3, int i4, int i5) {
                super.onScrollChanged(i2, i3, i4, i5);
                UpdateAppAlertDialog.this.updateLayout();
            }
        };
        this.scrollView = nestedScrollView;
        nestedScrollView.setFillViewport(true);
        nestedScrollView.setWillNotDraw(false);
        nestedScrollView.setClipToPadding(false);
        nestedScrollView.setVerticalScrollBarEnabled(false);
        frameLayout.addView(nestedScrollView, LayoutHelper.createFrame(-1, -1.0f, 51, 0.0f, 0.0f, 0.0f, 130.0f));
        LinearLayout linearLayout = new LinearLayout(activity);
        this.linearLayout = linearLayout;
        linearLayout.setOrientation(1);
        nestedScrollView.addView(linearLayout, LayoutHelper.createScroll(-1, -2, 51));
        if (tL_help_appUpdate.sticker != null) {
            BackupImageView backupImageView = new BackupImageView(activity);
            SvgHelper.SvgDrawable svgThumb = DocumentObject.getSvgThumb(tL_help_appUpdate.sticker.thumbs, Theme.key_windowBackgroundGray, 1.0f);
            ImageLocation forDocument = ImageLocation.getForDocument(FileLoader.getClosestPhotoSizeWithSize(tL_help_appUpdate.sticker.thumbs, 90), tL_help_appUpdate.sticker);
            if (svgThumb != null) {
                backupImageView.setImage(ImageLocation.getForDocument(tL_help_appUpdate.sticker), "250_250", svgThumb, 0, "update");
            } else {
                backupImageView.setImage(ImageLocation.getForDocument(tL_help_appUpdate.sticker), "250_250", forDocument, (String) null, 0, "update");
            }
            linearLayout.addView(backupImageView, LayoutHelper.createLinear(160, 160, 49, 17, 8, 17, 0));
        }
        TextView textView = new TextView(activity);
        this.textView = textView;
        textView.setTypeface(AndroidUtilities.bold());
        textView.setTextSize(1, 20.0f);
        int i2 = Theme.key_dialogTextBlack;
        textView.setTextColor(Theme.getColor(i2));
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setText(getTitleText());
        linearLayout.addView(textView, LayoutHelper.createLinear(-2, -2, 49, 23, 16, 23, 0));
        TextView textView2 = new TextView(getContext());
        textView2.setTextColor(Theme.getColor(Theme.key_dialogTextGray3));
        textView2.setTextSize(1, 14.0f);
        textView2.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        int i3 = Theme.key_dialogTextLink;
        textView2.setLinkTextColor(Theme.getColor(i3));
        textView2.setText(LocaleController.formatString("AppUpdateVersionAndSize", R.string.AppUpdateVersionAndSize, tL_help_appUpdate.version, AndroidUtilities.formatFileSize(tL_help_appUpdate.document.size)));
        textView2.setGravity(49);
        linearLayout.addView(textView2, LayoutHelper.createLinear(-2, -2, 49, 23, 0, 23, 5));
        SpoilersTextView spoilersTextView = new SpoilersTextView(getContext());
        spoilersTextView.setTextColor(Theme.getColor(i2));
        spoilersTextView.setTextSize(1, 14.0f);
        spoilersTextView.setMovementMethod(new AndroidUtilities.LinkMovementMethodMy());
        spoilersTextView.setLinksClickable(false);
        spoilersTextView.setLinkTextColor(Theme.getColor(i3));
        if (TextUtils.isEmpty(tL_help_appUpdate.text)) {
            spoilersTextView.setText(AndroidUtilities.replaceTags(LocaleController.getString(R.string.AppUpdateChangelogEmpty)));
        } else {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(tL_help_appUpdate.text);
            MessageObject.addEntitiesToText(spannableStringBuilder, tL_help_appUpdate.entities, false, false, false, false);
            MessageObject.replaceAnimatedEmoji(spannableStringBuilder, tL_help_appUpdate.entities, spoilersTextView.getPaint().getFontMetricsInt());
            spoilersTextView.setText(spannableStringBuilder);
        }
        spoilersTextView.setGravity(51);
        linearLayout.addView(spoilersTextView, LayoutHelper.createLinear(-2, -2, 51, 23, 15, 23, 0));
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(-1, AndroidUtilities.getShadowHeight(), 83);
        layoutParams.bottomMargin = AndroidUtilities.dp(127.0f);
        View view = new View(activity);
        this.shadow = view;
        view.setBackgroundColor(Theme.getColor(Theme.key_dialogShadowLine));
        view.setAlpha(0.0f);
        view.setTag(1);
        frameLayout.addView(view, layoutParams);
        addContentBeforeDoneButton(frameLayout);
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(activity, true, null);
        buttonWithCounterView.setRound();
        buttonWithCounterView.setText(getDoneButtonText(), false);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view2) {
                UpdateAppAlertDialog.this.onDone();
            }
        });
        frameLayout.addView(buttonWithCounterView, LayoutHelper.createFrame(-1, 48.0f, 83, 22.0f, 14.0f, 22.0f, 64.0f));
        addContentAfterDoneButton(frameLayout);
    }

    public void addContentAfterDoneButton(FrameLayout frameLayout) {
        addRemindLaterButton(frameLayout, new Runnable() {
            @Override
            public final void run() {
                ExteraConfig.setUpdateScheduleTimestamp(System.currentTimeMillis());
                UpdateAppAlertDialog.this.dismiss();
            }
        });
    }

    public void addRemindLaterButton(FrameLayout frameLayout, final Runnable runnable) {
        ButtonWithCounterView buttonWithCounterView = new ButtonWithCounterView(getContext(), false, null);
        buttonWithCounterView.setRound();
        buttonWithCounterView.setText(LocaleController.getString(R.string.AppUpdateRemindMeLater), false);
        buttonWithCounterView.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                runnable.run();
            }
        });
        frameLayout.addView(buttonWithCounterView, LayoutHelper.createFrame(-1, 48.0f, 83, 22.0f, 14.0f, 22.0f, 8.0f));
    }

    public String getDoneButtonText() {
        return LocaleController.getString(FileLoader.getInstance(this.currentAccount).getPathToAttach(SharedConfig.pendingAppUpdate.document, true).exists() ? R.string.AppUpdateNow : R.string.AppUpdateDownloadNow);
    }

    public String getTitleText() {
        return LocaleController.getString(R.string.UpdateAvailable);
    }

    public void onDone() {
        if (FileLoader.getInstance(this.currentAccount).getPathToAttach(SharedConfig.pendingAppUpdate.document, true).exists()) {
            ApplicationLoader.applicationLoaderInstance.openApkInstall((Activity) getContext(), SharedConfig.pendingAppUpdate.document);
        } else {
            FileLoader.getInstance(this.accountNum).loadFile(this.appUpdate.document, "update", 1, 1);
        }
        dismiss();
    }

    private void runShadowAnimation(final boolean z) {
        if ((!z || this.shadow.getTag() == null) && (z || this.shadow.getTag() != null)) {
            return;
        }
        this.shadow.setTag(z ? null : 1);
        if (z) {
            this.shadow.setVisibility(0);
        }
        AnimatorSet animatorSet = this.shadowAnimation;
        if (animatorSet != null) {
            animatorSet.cancel();
        }
        AnimatorSet animatorSet2 = new AnimatorSet();
        this.shadowAnimation = animatorSet2;
        animatorSet2.playTogether(ObjectAnimator.ofFloat(this.shadow, (Property<View, Float>) View.ALPHA, z ? 1.0f : 0.0f));
        this.shadowAnimation.setDuration(150L);
        this.shadowAnimation.addListener(new AnimatorListenerAdapter() { 
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animator) {
                if (UpdateAppAlertDialog.this.shadowAnimation == null || !UpdateAppAlertDialog.this.shadowAnimation.equals(animator)) {
                    return;
                }
                if (!z) {
                    UpdateAppAlertDialog.this.shadow.setVisibility(4);
                }
                UpdateAppAlertDialog.this.shadowAnimation = null;
            }

            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationCancel(Animator animator) {
                if (UpdateAppAlertDialog.this.shadowAnimation == null || !UpdateAppAlertDialog.this.shadowAnimation.equals(animator)) {
                    return;
                }
                UpdateAppAlertDialog.this.shadowAnimation = null;
            }
        });
        this.shadowAnimation.start();
    }

    public void updateLayout() {
        this.linearLayout.getChildAt(0).getLocationInWindow(this.location);
        int iMax = Math.max(this.location[1] - AndroidUtilities.dp(24.0f), 0);
        runShadowAnimation(((float) (this.location[1] + this.linearLayout.getMeasuredHeight())) > ((float) (this.container.getMeasuredHeight() - AndroidUtilities.dp(113.0f))) + this.containerView.getTranslationY());
        if (this.scrollOffsetY != iMax) {
            this.scrollOffsetY = iMax;
            this.scrollView.invalidate();
        }
    }
}
