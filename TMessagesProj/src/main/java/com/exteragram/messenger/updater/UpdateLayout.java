package com.exteragram.messenger.updater;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.AnimatedTextView;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.RadialProgress2;
import org.telegram.ui.IUpdateLayout;

public class UpdateLayout extends IUpdateLayout {
    private final Activity activity;
    private final ViewGroup sideMenuContainer;
    private FrameLayout updateLayout;
    private RadialProgress2 updateLayoutIcon;
    private AnimatedTextView.AnimatedTextDrawable updateSizeTextView;
    private AnimatedTextView updateTextView;

    public UpdateLayout(Activity activity, ViewGroup viewGroup) {
        super(activity, viewGroup);
        this.activity = activity;
        this.sideMenuContainer = viewGroup;
    }

    @Override // org.telegram.ui.IUpdateLayout
    public void updateFileProgress(Object[] objArr) {
        if (this.updateTextView == null || objArr == null || !SharedConfig.isAppUpdateAvailable()) {
            return;
        }
        String str = (String) objArr[0];
        String attachFileName = FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
        if (attachFileName == null || !attachFileName.equals(str)) {
            return;
        }
        float fLongValue = ((Long) objArr[1]).longValue() / ((Long) objArr[2]).longValue();
        this.updateLayoutIcon.setProgress(fLongValue, true);
        this.updateTextView.setText(LocaleController.formatString(R.string.AppUpdateDownloading, Integer.valueOf((int) (fLongValue * 100.0f))));
    }

    @Override // org.telegram.ui.IUpdateLayout
    public void createUpdateUI(final int i) {
        if (this.sideMenuContainer == null || this.updateLayout != null) {
            return;
        }
        FrameLayout frameLayout = new FrameLayout(this.activity);
        this.updateLayout = frameLayout;
        frameLayout.setVisibility(4);
        this.updateLayout.setTranslationY(AndroidUtilities.dp(44.0f));
        this.updateLayout.setBackground(Theme.getSelectorDrawable(1090519039, false));
        this.sideMenuContainer.addView(this.updateLayout, LayoutHelper.createFrame(-1, 44, 83));
        this.updateLayout.setOnClickListener(new View.OnClickListener() { 
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                this.f$0.lambda$createUpdateUI$0(i, view);
            }
        });
        AnimatedTextView animatedTextView = new AnimatedTextView(this.activity, true, true, true) { 
            @Override // org.telegram.ui.Components.AnimatedTextView, android.view.View
            public void onDraw(Canvas canvas) {
                UpdateLayout.this.updateSizeTextView.setBounds(0, 0, getMeasuredWidth() - AndroidUtilities.dp(20.0f), getMeasuredHeight());
                UpdateLayout.this.updateSizeTextView.draw(canvas);
                canvas.save();
                canvas.translate(AndroidUtilities.dp(15.0f), 0.0f);
                super.onDraw(canvas);
                canvas.translate(((getMeasuredWidth() - width()) / 2.0f) - AndroidUtilities.dp(30.0f), AndroidUtilities.dp(11.0f));
                UpdateLayout.this.updateLayoutIcon.draw(canvas);
                canvas.restore();
            }

            @Override // android.view.View
            public boolean verifyDrawable(Drawable drawable) {
                return super.verifyDrawable(drawable) || drawable == UpdateLayout.this.updateSizeTextView;
            }
        };
        this.updateTextView = animatedTextView;
        animatedTextView.setTextSize(AndroidUtilities.dp(15.0f));
        this.updateTextView.setTypeface(AndroidUtilities.bold());
        int color = Theme.getColor(Theme.key_featuredStickers_buttonText);
        this.updateTextView.setTextColor(color);
        this.updateTextView.setGravity(17);
        this.updateLayout.addView(this.updateTextView, LayoutHelper.createFrameMatchParent());
        this.updateTextView.setText(LocaleController.getString(R.string.AppUpdateBeta), false);
        RadialProgress2 radialProgress2 = new RadialProgress2(this.updateTextView);
        this.updateLayoutIcon = radialProgress2;
        int i2 = Theme.key_featuredStickers_addButton;
        radialProgress2.setColors(color, color, Theme.getColor(i2), Theme.getColor(i2));
        this.updateLayoutIcon.setProgressRect(0, 0, AndroidUtilities.dp(22.0f), AndroidUtilities.dp(22.0f));
        this.updateLayoutIcon.setCircleRadius(AndroidUtilities.dp(11.0f));
        this.updateLayoutIcon.setAsMini();
        AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = new AnimatedTextView.AnimatedTextDrawable(true, true, true);
        this.updateSizeTextView = animatedTextDrawable;
        animatedTextDrawable.setCallback(this.updateTextView);
        this.updateSizeTextView.setTextSize(AndroidUtilities.dp(14.0f));
        this.updateSizeTextView.setTypeface(AndroidUtilities.bold());
        this.updateSizeTextView.setGravity(21);
        this.updateSizeTextView.setTextColor(Theme.multAlpha(color, 0.75f));
    }

    public /* synthetic */ void lambda$createUpdateUI$0(int i, View view) {
        if (SharedConfig.isAppUpdateAvailable()) {
            if (this.updateLayoutIcon.getIcon() == 2) {
                FileLoader.getInstance(i).loadFile(SharedConfig.pendingAppUpdate.document, "update", 1, 1);
                updateAppUpdateViews(i, true);
            } else if (this.updateLayoutIcon.getIcon() == 3) {
                FileLoader.getInstance(i).cancelLoadFile(SharedConfig.pendingAppUpdate.document);
                updateAppUpdateViews(i, true);
            } else {
                AndroidUtilities.openForView(SharedConfig.pendingAppUpdate.document, this.activity);
            }
        }
    }

    @Override // org.telegram.ui.IUpdateLayout
    public void updateAppUpdateViews(int i, boolean z) {
        boolean z2;
        String fileSize;
        FrameLayout frameLayout;
        if (this.sideMenuContainer == null) {
            return;
        }
        if (SharedConfig.isAppUpdateAvailable()) {
            createUpdateUI(i);
            String attachFileName = FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document);
            if (FileLoader.getInstance(i).getPathToAttach(SharedConfig.pendingAppUpdate.document, true).exists()) {
                this.updateLayoutIcon.setIcon(15, true, z);
                setUpdateText(LocaleController.getString(R.string.AppUpdateNow), z);
            } else {
                boolean zIsLoadingFile = FileLoader.getInstance(i).isLoadingFile(attachFileName);
                RadialProgress2 radialProgress2 = this.updateLayoutIcon;
                if (zIsLoadingFile) {
                    radialProgress2.setIcon(3, true, z);
                    this.updateLayoutIcon.setProgress(0.0f, false);
                    Float fileProgress = ImageLoader.getInstance().getFileProgress(attachFileName);
                    setUpdateText(LocaleController.formatString(R.string.AppUpdateDownloading, Integer.valueOf((int) ((fileProgress != null ? fileProgress.floatValue() : 0.0f) * 100.0f))), z);
                } else {
                    radialProgress2.setIcon(2, true, z);
                    setUpdateText(LocaleController.getString(R.string.AppUpdate), z);
                    z2 = true;
                }
                AnimatedTextView.AnimatedTextDrawable animatedTextDrawable = this.updateSizeTextView;
                if (z2) {
                    fileSize = AndroidUtilities.formatFileSize(SharedConfig.pendingAppUpdate.document.size);
                } else {
                    fileSize = null;
                }
                animatedTextDrawable.setText(fileSize, z);
                if (this.updateLayout.getTag() != null) {
                    return;
                }
                this.updateLayout.setVisibility(0);
                this.updateLayout.setTag(1);
                frameLayout = this.updateLayout;
                if (z) {
                    frameLayout.animate().translationY(0.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT).setListener(null).setDuration(180L).start();
                    return;
                } else {
                    frameLayout.setTranslationY(0.0f);
                    return;
                }
            }
            z2 = false;
            AnimatedTextView.AnimatedTextDrawable animatedTextDrawable2 = this.updateSizeTextView;
            if (z2) {
                fileSize = AndroidUtilities.formatFileSize(SharedConfig.pendingAppUpdate.document.size);
            } else {
                fileSize = null;
            }
            animatedTextDrawable2.setText(fileSize, z);
            if (this.updateLayout.getTag() != null) {
                return;
            }
            this.updateLayout.setVisibility(0);
            this.updateLayout.setTag(1);
            frameLayout = this.updateLayout;
            if (z) {
                frameLayout.animate().translationY(0.0f).setInterpolator(CubicBezierInterpolator.EASE_OUT).setListener(null).setDuration(180L).start();
                return;
            } else {
                frameLayout.setTranslationY(0.0f);
                return;
            }
        }
        FrameLayout frameLayout2 = this.updateLayout;
        if (frameLayout2 == null || frameLayout2.getTag() == null) {
            return;
        }
        this.updateLayout.setTag(null);
        FrameLayout frameLayout3 = this.updateLayout;
        if (z) {
            frameLayout3.animate().translationY(AndroidUtilities.dp(44.0f)).setInterpolator(CubicBezierInterpolator.EASE_OUT).setListener(new AnimatorListenerAdapter() { 
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animator) {
                    if (UpdateLayout.this.updateLayout.getTag() == null) {
                        UpdateLayout.this.updateLayout.setVisibility(4);
                    }
                }
            }).setDuration(180L).start();
        } else {
            frameLayout3.setTranslationY(AndroidUtilities.dp(44.0f));
            this.updateLayout.setVisibility(4);
        }
    }

    private void setUpdateText(String str, boolean z) {
        this.updateTextView.setText(str, z);
    }
}
