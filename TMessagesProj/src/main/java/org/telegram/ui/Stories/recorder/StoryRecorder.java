package org.telegram.ui.Stories.recorder;

import static org.telegram.messenger.AndroidUtilities.dp;
import static org.telegram.messenger.AndroidUtilities.dpf2;
import static org.telegram.messenger.AndroidUtilities.ilerp;
import static org.telegram.messenger.AndroidUtilities.lerp;
import static org.telegram.messenger.AndroidUtilities.touchSlop;
import static org.telegram.messenger.LocaleController.formatPluralString;
import static org.telegram.messenger.LocaleController.getString;
import static org.telegram.messenger.MessagesController.findUpdates;
import static org.telegram.messenger.Utilities.clamp01;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Outline;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.RenderNode;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.URLSpan;
import android.util.Pair;
import android.view.Gravity;
import android.view.HapticFeedbackConstants;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.view.WindowInsets;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.window.OnBackInvokedDispatcher;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.WindowInsetsCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.AnimationNotificationsLocker;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BotWebViewVibrationEffect;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.ImageLoader;
import org.telegram.messenger.ImageReceiver;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.VideoEditedInfo;
import org.telegram.messenger.camera.CameraController;
import org.telegram.messenger.camera.CameraView;
import org.telegram.messenger.utils.WindowVisibilityManager;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_stories;
import org.telegram.tgnet.tl.TL_update;
import org.telegram.ui.AccountFrozenAlert;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.SimpleTextView;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.AvatarSpan;
import org.telegram.ui.Cells.ChatMessageCell;
import org.telegram.ui.Cells.ShareDialogCell;
import org.telegram.ui.Components.AlertsCreator;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.BlurringShader;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CombinedDrawable;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.EmojiView;
import org.telegram.ui.Components.FilterShaders;
import org.telegram.ui.Components.GestureDetectorFixDoubleTap;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Paint.RenderView;
import org.telegram.ui.Components.Paint.Views.EntityView;
import org.telegram.ui.Components.Paint.Views.MessageEntityView;
import org.telegram.ui.Components.Paint.Views.PhotoView;
import org.telegram.ui.Components.Paint.Views.RoundView;
import org.telegram.ui.Components.PermissionRequest;
import org.telegram.ui.Components.PhotoFilterBlurControl;
import org.telegram.ui.Components.PhotoFilterCurvesControl;
import org.telegram.ui.Components.PhotoFilterView;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.Premium.PremiumFeatureBottomSheet;
import org.telegram.ui.Components.RLottieDrawable;
import org.telegram.ui.Components.RLottieImageView;
import org.telegram.ui.Components.RectOld;
import org.telegram.ui.Components.SizeNotifierFrameLayout;
import org.telegram.ui.Components.TextStyleSpan;
import org.telegram.ui.Components.ThanosEffect;
import org.telegram.ui.Components.URLSpanUserMention;
import org.telegram.ui.Components.VideoEditTextureView;
import org.telegram.ui.Components.WaveDrawable;
import org.telegram.ui.Components.ZoomControlView;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.PremiumPreviewFragment;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.Stories.DarkThemeResourceProvider;
import org.telegram.ui.Stories.DialogStoriesCell;
import org.telegram.ui.Stories.LivePlayer;
import org.telegram.ui.Stories.PeerStoriesView;
import org.telegram.ui.Stories.StoriesController;
import org.telegram.ui.Stories.StoryViewer;
import org.telegram.ui.Stories.StoryWaveEffectView;
import org.telegram.ui.WrappedResourceProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StoryRecorder implements NotificationCenter.NotificationCenterDelegate {

    private final Theme.ResourcesProvider resourcesProvider = new DarkThemeResourceProvider();

    private final Activity activity;
    private final int currentAccount;

    private boolean isShown;
    private boolean prepareClosing;

    private final WindowManager windowManager;
    private final WindowManager.LayoutParams windowLayoutParams;
    private WindowView windowView;
    private ContainerView containerView;
    private FlashViews flashViews;
    private ThanosEffect thanosEffect;

    private static StoryRecorder instance;
    private boolean wasSend;
    private long wasSendPeer = 0;
    private ClosingViewProvider closingSourceProvider;
    private Runnable closeListener;

    public static StoryRecorder getInstance(Activity activity, int currentAccount) {
        if (instance != null && (instance.activity != activity || instance.currentAccount != currentAccount)) {
            instance.close(false);
            instance = null;
        }
        if (instance == null) {
            instance = new StoryRecorder(activity, currentAccount);
        }
        return instance;
    }

    public static void destroyInstance() {
        if (instance != null) {
            instance.close(false);
        }
        instance = null;
    }

    private final WindowVisibilityManager.Controller activityVisibilityController;

    public static boolean isVisible() {
        return instance != null && instance.isShown;
    }
    public StoryRecorder(Activity activity, int currentAccount) {
        this.activity = activity;
        this.currentAccount = currentAccount;

        activityVisibilityController = LaunchActivity.obtainActivityVisibilityController();

        windowLayoutParams = new WindowManager.LayoutParams();
        windowLayoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        windowLayoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        windowLayoutParams.format = PixelFormat.TRANSLUCENT;
        windowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        windowLayoutParams.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW;
        if (Build.VERSION.SDK_INT >= 28) {
            windowLayoutParams.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
        }
        windowLayoutParams.flags = (
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
            WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR |
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION
        );
        if (Build.VERSION.SDK_INT >= 21) {
            windowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
        }
        windowLayoutParams.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE;

        windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);

        initViews();
    }

    private ValueAnimator openCloseAnimator;
    private SourceView fromSourceView;
    private float fromRounding;
    private final RectF fromRect = new RectF();
    private float openProgress;
    private int openType;
    private float dismissProgress;
    private Float frozenDismissProgress;
    private boolean canChangePeer = true;
    long selectedDialogId;

    public static class SourceView {

        int type = 0;
        float rounding;
        RectF screenRect = new RectF();
        Drawable backgroundDrawable;
        ImageReceiver backgroundImageReceiver;
        boolean hasShadow;
        Paint backgroundPaint;
        Drawable iconDrawable;
        int iconSize;
        View view;

        protected void show(boolean sent) {}
        protected void hide() {}
        protected void drawAbove(Canvas canvas, float alpha) {}

        public static SourceView fromAvatarImage(ProfileActivity.AvatarImageView avatarImage, boolean isForum) {
            if (avatarImage == null || avatarImage.getRootView() == null) {
                return null;
            }
            float scale = ((View)avatarImage.getParent()).getScaleX();
            final float size = avatarImage.getImageReceiver().getImageWidth() * scale;
            final float rounding = isForum ? size * 0.32f : size;
            SourceView src = new SourceView() {
                @Override
                protected void show(boolean sent) {
                    avatarImage.drawAvatar = true;
                    avatarImage.invalidate();
                }

                @Override
                protected void hide() {
                    avatarImage.drawAvatar = false;
                    avatarImage.invalidate();
                }
            };
            final int[] loc = new int[2];
            final float[] locPositon = new float[2];
            avatarImage.getRootView().getLocationOnScreen(loc);
            AndroidUtilities.getViewPositionInParent(avatarImage, (ViewGroup) avatarImage.getRootView(), locPositon);
            final float x = loc[0] + locPositon[0] + avatarImage.getImageReceiver().getImageX() * scale;
            final float y = loc[1] + locPositon[1] + avatarImage.getImageReceiver().getImageY() * scale;

            src.screenRect.set(x, y, x + size, y + size);
            src.backgroundImageReceiver = avatarImage.getImageReceiver();
            src.rounding = rounding;
            return src;
        }

        public static SourceView fromStoryViewer(StoryViewer storyViewer) {
            if (storyViewer == null) {
                return null;
            }
            SourceView src = new SourceView() {
                @Override
                protected void show(boolean sent) {
                    final PeerStoriesView peerView = storyViewer.getCurrentPeerView();
                    if (peerView != null) {
                        peerView.animateOut(false);
                    }
                    if (view != null) {
                        view.setTranslationX(0);
                        view.setTranslationY(0);
                    }
                }

                @Override
                protected void hide() {
                    final PeerStoriesView peerView = storyViewer.getCurrentPeerView();
                    if (peerView != null) {
                        peerView.animateOut(true);
                    }
                }
            };
            if (!storyViewer.getStoryRect(src.screenRect)) {
                return null;
            }
            src.type = 1;
            src.rounding = dp(8);
            final PeerStoriesView peerView = storyViewer.getCurrentPeerView();
            if (peerView != null) {
                src.view = peerView.storyContainer;
            }
            return src;
        }

        public static SourceView fromFloatingButton(FrameLayout floatingButton) {
            if (floatingButton == null) {
                return null;
            }
            SourceView src = new SourceView() {
                @Override
                protected void show(boolean sent) {
                    floatingButton.setVisibility(View.VISIBLE);
                }
                @Override
                protected void hide() {
                    floatingButton.post(() -> {
                        floatingButton.setVisibility(View.GONE);
                    });
                }
            };
            int[] loc = new int[2];
            final View imageView = floatingButton.getChildAt(0);
            imageView.getLocationOnScreen(loc);
            src.screenRect.set(loc[0], loc[1], loc[0] + imageView.getWidth(), loc[1] + imageView.getHeight());
            src.hasShadow = true;
            src.backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            src.backgroundPaint.setColor(Theme.getColor(Theme.key_chats_actionBackground));
            src.iconDrawable = floatingButton.getContext().getResources().getDrawable(R.drawable.story_camera).mutate();
            src.iconSize = AndroidUtilities.dp(56);
            src.rounding = Math.max(src.screenRect.width(), src.screenRect.height()) / 2f;
            return src;
        }

        public static SourceView fromShareCell(ShareDialogCell shareDialogCell) {
            if (shareDialogCell == null) {
                return null;
            }
            BackupImageView imageView = shareDialogCell.getImageView();
            SourceView src = new SourceView() {
                @Override
                protected void show(boolean sent) {
                    imageView.setVisibility(View.VISIBLE);
                }
                @Override
                protected void hide() {
                    imageView.post(() -> {
                        imageView.setVisibility(View.GONE);
                    });
                }
            };
            int[] loc = new int[2];
            imageView.getLocationOnScreen(loc);
            src.screenRect.set(loc[0], loc[1], loc[0] + imageView.getWidth(), loc[1] + imageView.getHeight());
            src.backgroundDrawable = new ShareDialogCell.RepostStoryDrawable(imageView.getContext(), null, false, shareDialogCell.resourcesProvider);
//            src.hasShadow = false;
//            src.backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//            src.backgroundPaint.setColor(Theme.getColor(Theme.key_chats_actionBackground));
//            src.iconDrawable = shareDialogCell.getContext().getResources().getDrawable(R.drawable.large_repost_story).mutate();
//            src.iconSize = AndroidUtilities.dp(30);
            src.rounding = Math.max(src.screenRect.width(), src.screenRect.height()) / 2f;
            return src;
        }

        public static SourceView fromStoryCell(DialogStoriesCell.StoryCell storyCell) {
            if (storyCell == null || storyCell.getRootView() == null) {
                return null;
            }
            final float size = storyCell.avatarImage.getImageWidth();
            final float radius = size / 2f;
            SourceView src = new SourceView() {
                @Override
                protected void show(boolean sent) {
                    storyCell.drawAvatar = true;
                    storyCell.invalidate();
                    if (sent) {
                        final int[] loc = new int[2];
                        storyCell.getLocationInWindow(loc);
                        LaunchActivity.makeRipple(loc[0] + storyCell.getWidth() / 2f, loc[1] + storyCell.getHeight() / 2f, 1f);
                    }
                }

                @Override
                protected void hide() {
                    storyCell.post(() -> {
                        storyCell.drawAvatar = false;
                        storyCell.invalidate();
                    });
                }

                @Override
                protected void drawAbove(Canvas canvas, float alpha) {
                    storyCell.drawPlus(canvas, radius, radius, (float) Math.pow(alpha, 16));
                }
            };
            final int[] loc = new int[2];
            final float[] locPositon = new float[2];
            storyCell.getRootView().getLocationOnScreen(loc);
            AndroidUtilities.getViewPositionInParent(storyCell, (ViewGroup) storyCell.getRootView(), locPositon);
            final float x = loc[0] + locPositon[0] + storyCell.avatarImage.getImageX();
            final float y = loc[1] + locPositon[1] + storyCell.avatarImage.getImageY();

            src.screenRect.set(x, y, x + size, y + size);
            src.backgroundImageReceiver = storyCell.avatarImage;
            src.rounding = Math.max(src.screenRect.width(), src.screenRect.height()) / 2f;
            return src;
        }
    }

    public StoryRecorder whenSent(Runnable listener) {
        closeListener = listener;
        return this;
    }

    public StoryRecorder closeToWhenSent(ClosingViewProvider closingSourceProvider) {
        this.closingSourceProvider = closingSourceProvider;
        return this;
    }

    public void replaceSourceView(SourceView sourceView) {
        if (sourceView != null) {
            fromSourceView = sourceView;
            openType = sourceView.type;
            fromRect.set(sourceView.screenRect);
            fromRounding = sourceView.rounding;
        } else {
            fromSourceView = null;
            openType = 0;
            fromRect.set(0, dp(100), AndroidUtilities.displaySize.x, dp(100) + AndroidUtilities.displaySize.y);
            fromRounding = dp(8);
        }
        previewContainer.setBackgroundColor(openType == 1 || openType == 0 ? 0 : 0xff1f1f1f);
    }

    public void openBot(long botId, String lang_code, SourceView sourceView) {
        this.botId = botId;
        this.botLang = lang_code;
        this.botEdit = null;
        open(sourceView, true);
        this.botId = botId;
        this.botLang = lang_code;
    }

    public void openBotEntry(long botId, String lang_code, StoryEntry entry, SourceView sourceView) {
        if (isShown || entry == null) {
            return;
        }
        if (MessagesController.getInstance(currentAccount).isFrozen()) {
            AccountFrozenAlert.show(currentAccount);
            return;
        }

        this.botId = botId;
        this.botLang = lang_code;

        isReposting = false;
        prepareClosing = false;
        forceBackgroundVisible = false;

        if (windowManager != null && windowView != null && windowView.getParent() == null) {
            AndroidUtilities.setPreferredMaxRefreshRate(windowManager, windowView, windowLayoutParams);
            windowManager.addView(windowView, windowLayoutParams);
            setupBackDispatcher();
        }

        outputEntry = entry;
        outputEntry.botId = botId;
        outputEntry.botLang = lang_code;
        mode = outputEntry != null && outputEntry.isVideo ? MODE_VIDEO : MODE_PHOTO;
        videoTextureHolder.active = false;

        if (sourceView != null) {
            fromSourceView = sourceView;
            openType = sourceView.type;
            fromRect.set(sourceView.screenRect);
            fromRounding = sourceView.rounding;
            fromSourceView.hide();
        } else {
            openType = 0;
            fromRect.set(0, dp(100), AndroidUtilities.displaySize.x, dp(100) + AndroidUtilities.displaySize.y);
            fromRounding = dp(8);
        }
        containerView.updateBackground();
        previewContainer.setBackgroundColor(openType == 1 || openType == 0 ? 0 : 0xff1f1f1f);

        containerView.setTranslationX(0);
        containerView.setTranslationY(0);
        containerView.setTranslationY2(0);
        containerView.setScaleX(1f);
        containerView.setScaleY(1f);
        dismissProgress = 0;

        AndroidUtilities.lockOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (outputEntry != null) {
            captionEdit.setText(outputEntry.caption);
        }

        navigateTo(PAGE_PREVIEW, false);
        switchToEditMode(EDIT_MODE_NONE, false);
        previewButtons.appear(false, false);

        previewButtons.appear(true, true);
        animateOpenTo(1, true, this::onOpenDone);
        addNotificationObservers();
    }

    public void open(SourceView sourceView) {
        open(sourceView, true);
    }

    public void open(SourceView sourceView, boolean animated) {
        if (isShown) {
            return;
        }
        if (MessagesController.getInstance(currentAccount).isFrozen()) {
            AccountFrozenAlert.show(currentAccount);
            return;
        }

        isReposting = false;
        prepareClosing = false;
//        privacySelectorHintOpened = false;
        forceBackgroundVisible = false;
        videoTextureHolder.active = false;

        if (windowManager != null && windowView != null && windowView.getParent() == null) {
            AndroidUtilities.setPreferredMaxRefreshRate(windowManager, windowView, windowLayoutParams);
            windowManager.addView(windowView, windowLayoutParams);
            setupBackDispatcher();
        }

        collageLayoutView.setCameraThumb(getCameraThumb());

        if (botId == 0) {
            StoriesController.StoryLimit storyLimit = MessagesController.getInstance(currentAccount).getStoriesController().checkStoryLimit();
            if (storyLimit != null && storyLimit.active(currentAccount)) {
                showLimitReachedSheet(storyLimit, true);
            }
        }

        navigateTo(PAGE_CAMERA, false);
        switchToEditMode(EDIT_MODE_NONE, false);

        if (sourceView != null) {
            fromSourceView = sourceView;
            openType = sourceView.type;
            fromRect.set(sourceView.screenRect);
            fromRounding = sourceView.rounding;
            fromSourceView.hide();
        } else {
            openType = 0;
            fromRect.set(0, dp(100), AndroidUtilities.displaySize.x, dp(100) + AndroidUtilities.displaySize.y);
            fromRounding = dp(8);
        }
        containerView.updateBackground();
        previewContainer.setBackgroundColor(openType == 1 || openType == 0 ? 0 : 0xff1f1f1f);

        containerView.setTranslationX(0);
        containerView.setTranslationY(0);
        containerView.setTranslationY2(0);
        containerView.setScaleX(1f);
        containerView.setScaleY(1f);
        dismissProgress = 0;

        AndroidUtilities.lockOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        animateOpenTo(1, animated, this::onOpenDone);

        addNotificationObservers();

        botId = 0;
        botLang = "";
        botEdit = null;
    }

    public void openEdit(SourceView sourceView, StoryEntry entry, long time, boolean animated) {
        if (isShown) {
            return;
        }
        if (MessagesController.getInstance(currentAccount).isFrozen()) {
            AccountFrozenAlert.show(currentAccount);
            return;
        }

        isReposting = false;
        prepareClosing = false;
        forceBackgroundVisible = false;

        if (windowManager != null && windowView != null && windowView.getParent() == null) {
            AndroidUtilities.setPreferredMaxRefreshRate(windowManager, windowView, windowLayoutParams);
            windowManager.addView(windowView, windowLayoutParams);
            setupBackDispatcher();
        }

        outputEntry = entry;
        mode = outputEntry != null && outputEntry.isVideo ? MODE_VIDEO : MODE_PHOTO;
        videoTextureHolder.active = false;

        if (sourceView != null) {
            fromSourceView = sourceView;
            openType = sourceView.type;
            fromRect.set(sourceView.screenRect);
            fromRounding = sourceView.rounding;
            fromSourceView.hide();
        } else {
            openType = 0;
            fromRect.set(0, dp(100), AndroidUtilities.displaySize.x, dp(100) + AndroidUtilities.displaySize.y);
            fromRounding = dp(8);
        }
        containerView.updateBackground();
        previewContainer.setBackgroundColor(openType == 1 || openType == 0 ? 0 : 0xff1f1f1f);

        containerView.setTranslationX(0);
        containerView.setTranslationY(0);
        containerView.setTranslationY2(0);
        containerView.setScaleX(1f);
        containerView.setScaleY(1f);
        dismissProgress = 0;

        AndroidUtilities.lockOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (outputEntry != null) {
            captionEdit.setText(outputEntry.caption);
        }

        navigateToPreviewWithPlayerAwait(() -> {
            animateOpenTo(1, animated, this::onOpenDone);
            previewButtons.appear(true, true);
        }, time);
        navigateTo(outputEntry.isEditingCover ? PAGE_COVER : PAGE_PREVIEW, false);
        switchToEditMode(EDIT_MODE_NONE, false);
        previewButtons.appear(false, false);

        addNotificationObservers();

        botId = 0;
        botLang = "";
        botEdit = null;
    }

    public void openForward(SourceView sourceView, StoryEntry entry, long time, boolean animated) {
        if (isShown) {
            return;
        }
        if (MessagesController.getInstance(currentAccount).isFrozen()) {
            AccountFrozenAlert.show(currentAccount);
            return;
        }

        isReposting = false;
        prepareClosing = false;
        forceBackgroundVisible = false;

        if (windowManager != null && windowView != null && windowView.getParent() == null) {
            AndroidUtilities.setPreferredMaxRefreshRate(windowManager, windowView, windowLayoutParams);
            windowManager.addView(windowView, windowLayoutParams);
            setupBackDispatcher();
        }

        outputEntry = entry;
        StoryPrivacySelector.applySaved(currentAccount, outputEntry);
        mode = outputEntry != null && outputEntry.isVideo ? MODE_VIDEO : MODE_PHOTO;
        videoTextureHolder.active = false;

        if (sourceView != null) {
            fromSourceView = sourceView;
            openType = sourceView.type;
            fromRect.set(sourceView.screenRect);
            fromRounding = sourceView.rounding;
            fromSourceView.hide();
        } else {
            openType = 0;
            fromRect.set(0, dp(100), AndroidUtilities.displaySize.x, dp(100) + AndroidUtilities.displaySize.y);
            fromRounding = dp(8);
        }
        containerView.updateBackground();
        previewContainer.setBackgroundColor(openType == 1 || openType == 0 ? 0 : 0xff1f1f1f);

        containerView.setTranslationX(0);
        containerView.setTranslationY(0);
        containerView.setTranslationY2(0);
        containerView.setScaleX(1f);
        containerView.setScaleY(1f);
        dismissProgress = 0;

        AndroidUtilities.lockOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (outputEntry != null) {
            captionEdit.setText(outputEntry.caption);
        }

        navigateToPreviewWithPlayerAwait(() -> {
            animateOpenTo(1, animated, this::onOpenDone);
        }, time);
        previewButtons.appear(true, false);
        navigateTo(PAGE_PREVIEW, false);
        switchToEditMode(EDIT_MODE_NONE, false);

        addNotificationObservers();

        botId = 0;
        botLang = "";
        botEdit = null;
    }

    private static boolean firstOpen = true;
    public void openRepost(SourceView sourceView, StoryEntry entry) {
        if (isShown) {
            return;
        }
        if (MessagesController.getInstance(currentAccount).isFrozen()) {
            AccountFrozenAlert.show(currentAccount);
            return;
        }

        isReposting = true;
        prepareClosing = false;
        forceBackgroundVisible = false;

        if (windowManager != null && windowView != null && windowView.getParent() == null) {
            AndroidUtilities.setPreferredMaxRefreshRate(windowManager, windowView, windowLayoutParams);
            windowManager.addView(windowView, windowLayoutParams);
            setupBackDispatcher();
        }

        outputEntry = entry;
        StoryPrivacySelector.applySaved(currentAccount, outputEntry);
        mode = outputEntry != null && outputEntry.isVideo ? MODE_VIDEO : MODE_PHOTO;
        videoTextureHolder.active = outputEntry != null && outputEntry.isRepostMessage && (mode == MODE_VIDEO);

        if (botId == 0) {
            StoriesController.StoryLimit storyLimit = MessagesController.getInstance(currentAccount).getStoriesController().checkStoryLimit();
            if (storyLimit != null && storyLimit.active(currentAccount)) {
                showLimitReachedSheet(storyLimit, true);
            }
        }

        if (sourceView != null) {
            fromSourceView = sourceView;
            openType = sourceView.type;
            fromRect.set(sourceView.screenRect);
            fromRounding = sourceView.rounding;
            fromSourceView.hide();
        } else {
            openType = 0;
            fromRect.set(0, dp(100), AndroidUtilities.displaySize.x, dp(100) + AndroidUtilities.displaySize.y);
            fromRounding = dp(8);
        }

        containerView.updateBackground();
        previewContainer.setBackgroundColor(openType == 1 || openType == 0 ? 0 : 0xff1f1f1f);

        containerView.setTranslationX(0);
        containerView.setTranslationY(0);
        containerView.setTranslationY2(0);
        containerView.setScaleX(1f);
        containerView.setScaleY(1f);
        dismissProgress = 0;

        AndroidUtilities.lockOrientation(activity, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        if (outputEntry != null) {
            captionEdit.setText(outputEntry.caption);
        }

        previewButtons.appear(true, false);
        navigateTo(PAGE_PREVIEW, false);
        switchToEditMode(EDIT_MODE_NONE, false);
        animateOpenTo(1, true, this::onOpenDone);

        addNotificationObservers();

        botId = 0;
        botLang = "";
        botEdit = null;
    }

    private void setupBackDispatcher() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return;
        final OnBackInvokedDispatcher dispatcher = windowView.findOnBackInvokedDispatcher();
        if (dispatcher == null) return;
        dispatcher.registerOnBackInvokedCallback(
            OnBackInvokedDispatcher.PRIORITY_DEFAULT,
            this::onBackPressed
        );
    }

    private boolean fastClose;
    public void close(boolean animated) {
        if (!isShown) {
            return;
        }

        if (privacySheet != null) {
            privacySheet.dismiss();
            privacySheet = null;
        }

        if (outputEntry != null && !outputEntry.isEditSaved) {
            if (wasSend && outputEntry.isEdit || outputEntry.draftId != 0) {
                outputEntry.editedMedia = false;
            }
            outputEntry.destroy(false);
        }
        outputEntry = null;

        if (onClosePrepareListener != null && previewView != null) {
            if (prepareClosing) {
                return;
            }
            prepareClosing = true;
            onClosePrepareListener.run(previewView.release(), () -> {
                onClosePrepareListener = null;
                prepareClosing = false;
                close(animated);
            }, wasSend, wasSendPeer);
            return;
        }

        if (previewView != null && !animated) {
            previewView.set(null);
        }

        animateOpenTo(0, animated, this::onCloseDone);
        if (openType == 1 || openType == 0) {
            windowView.setBackgroundColor(0x00000000);
            previewButtons.appear(false, true);
        }

        removeNotificationObservers();
    }

    private AnimationNotificationsLocker notificationsLocker = new AnimationNotificationsLocker();
    private StoryWaveEffectView waveEffect;

    private void animateOpenTo(final float value, boolean animated, Runnable onDone) {
        if (openCloseAnimator != null) {
            openCloseAnimator.cancel();
            openCloseAnimator = null;
        }

        if (animated) {
            notificationsLocker.lock();
            NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.stopAllHeavyOperations, 512);
            frozenDismissProgress = dismissProgress;
            openCloseAnimator = ValueAnimator.ofFloat(openProgress, value);
            openCloseAnimator.addUpdateListener(anm -> {
                openProgress = (float) anm.getAnimatedValue();
                checkBackgroundVisibility();
                containerView.invalidate();
                windowView.invalidate();
                if (openProgress < .3f && waveEffect != null) {
                    waveEffect.start();
                    waveEffect = null;
                }
            });
            openCloseAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    frozenDismissProgress = null;
                    openProgress = value;
                    applyOpenProgress();
                    containerView.invalidate();
                    windowView.invalidate();
                    if (onDone != null) {
                        onDone.run();
                    }
                    if (fromSourceView != null && waveEffect != null) {
                        waveEffect.start();
                        waveEffect = null;
                    }
                    notificationsLocker.unlock();
                    NotificationCenter.getGlobalInstance().postNotificationName(NotificationCenter.startAllHeavyOperations, 512);
                    NotificationCenter.getGlobalInstance().runDelayedNotifications();
                    checkBackgroundVisibility();

                    if (onFullyOpenListener != null) {
                        onFullyOpenListener.run();
                        onFullyOpenListener = null;
                    }

                    containerView.invalidate();
                    previewContainer.invalidate();
                }
            });
            if (value < 1 && wasSend) {
                openCloseAnimator.setDuration(250);
                openCloseAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            } else {
                if (value > 0 || containerView.getTranslationY1() < AndroidUtilities.dp(20)) {
                    openCloseAnimator.setDuration(300L);
                    openCloseAnimator.setInterpolator(new FastOutSlowInInterpolator());
                } else if (value < 0 && fastClose) {
                    openCloseAnimator.setDuration(200L);
                    openCloseAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                    fastClose = false;
                } else {
                    openCloseAnimator.setDuration(400L);
                    openCloseAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
                }
            }
            openCloseAnimator.start();
        } else {
            frozenDismissProgress = null;
            openProgress = value;
            applyOpenProgress();
            containerView.invalidate();
            windowView.invalidate();
            if (onDone != null) {
                onDone.run();
            }
            checkBackgroundVisibility();
        }
        if (value > 0) {
            firstOpen = false;
        }
    }

    private void onOpenDone() {
        isShown = true;
        wasSend = false;
        if (openType == 1) {
            previewContainer.setAlpha(1f);
            previewContainer.setTranslationX(0);
            previewContainer.setTranslationY(0);
            actionBarContainer.setAlpha(1f);
            controlContainer.setAlpha(1f);
            windowView.setBackgroundColor(0xff000000);
            if (currentPage == PAGE_COVER) {
                coverButton.setAlpha(1f);
            }
        }

        if (whenOpenDone != null) {
            whenOpenDone.run();
            whenOpenDone = null;
        } else {
            onResumeInternal();
        }

        if (outputEntry != null && outputEntry.isRepost) {
            createPhotoPaintView();
            hidePhotoPaintView();
            createFilterPhotoView();
        } else if (outputEntry != null && outputEntry.isRepostMessage) {
            if (outputEntry.isVideo) {
                previewView.setupVideoPlayer(outputEntry, null, 0);
            }
            createFilterPhotoView();
        }
    }

    private void onCloseDone() {
        isShown = false;
        AndroidUtilities.unlockOrientation(activity);
        if (cameraView != null) {
            if (takingVideo) {
                CameraController.getInstance().stopVideoRecording(cameraView.getCameraSession(), false);
            }
            destroyCameraView(false);
        }
        if (previewView != null) {
            previewView.set(null);
        }
        destroyPhotoPaintView();
        destroyPhotoFilterView();
        if (outputFile != null && !wasSend) {
            try {
                outputFile.delete();
            } catch (Exception ignore) {}
        }
        outputFile = null;
        AndroidUtilities.runOnUIThread(() -> {
            if (windowManager != null && windowView != null && windowView.getParent() != null) {
                windowManager.removeView(windowView);
            }
        }, 16);
        if (fromSourceView != null) {
            fromSourceView.show(false);
        }
        if (whenOpenDone != null) {
            whenOpenDone = null;
        }
        lastGalleryScrollPosition = null;
        if (instance != null) {
            instance.close(false);
        }
        instance = null;

        if (onCloseListener != null) {
            onCloseListener.run();
            onCloseListener = null;
        }
        if (windowView != null) {
            Bulletin.removeDelegate(windowView);
        }
        if (captionContainer != null) {
            Bulletin.removeDelegate(captionContainer);
        }
        if (collageLayoutView != null) {
            collageLayoutView.clear(true);
        }
    }

    private Runnable onCloseListener;
    public void setOnCloseListener(Runnable listener) {
        onCloseListener = listener;
    }

    private Runnable onFullyOpenListener;
    public void setOnFullyOpenListener(Runnable listener) {
        onFullyOpenListener = listener;
    }

    private Utilities.Callback4<Long, Runnable, Boolean, Long> onClosePrepareListener;
    public void setOnPrepareCloseListener(Utilities.Callback4<Long, Runnable, Boolean, Long> listener) {
        onClosePrepareListener = listener;
    }

    private int previewW, previewH;
    private int underControls;
    private boolean underStatusBar;
    private boolean scrollingY, scrollingX;

    private int insetLeft, insetTop, insetRight, insetBottom;

    private final RectF rectF = new RectF(), fullRectF = new RectF();
    private final Path clipPath = new Path();
    private final Rect rect = new Rect();
    private void applyOpenProgress() {
        if (openType != 1) return;
        fullRectF.set(previewContainer.getLeft(), previewContainer.getTop(), previewContainer.getMeasuredWidth(), previewContainer.getMeasuredHeight());
        fullRectF.offset(containerView.getX(), containerView.getY());
        lerp(fromRect, fullRectF, openProgress, rectF);
        previewContainer.setAlpha(openProgress);
        previewContainer.setTranslationX(rectF.left - previewContainer.getLeft() - containerView.getX());
        previewContainer.setTranslationY(rectF.top - previewContainer.getTop() - containerView.getY());
        if (fromSourceView != null && fromSourceView.view != null) {
            fromSourceView.view.setTranslationX((fullRectF.left - fromRect.left) * openProgress);
            fromSourceView.view.setTranslationY((fullRectF.top - fromRect.top) * openProgress);
        }
        previewContainer.setScaleX(rectF.width() / previewContainer.getMeasuredWidth());
        previewContainer.setScaleY(rectF.height() / previewContainer.getMeasuredHeight());
        actionBarContainer.setAlpha(openProgress);
        controlContainer.setAlpha(openProgress);
        captionContainer.setAlpha(openProgress);
        if (currentPage == PAGE_COVER) {
            coverButton.setAlpha(openProgress);
        }
    }

    public class WindowView extends SizeNotifierFrameLayout {

        private GestureDetectorFixDoubleTap gestureDetector;
        private ScaleGestureDetector scaleGestureDetector;

        public WindowView(Context context) {
            super(context);
            gestureDetector = new GestureDetectorFixDoubleTap(context, new GestureListener());
            scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        }

        private int lastKeyboardHeight;

        @Override
        public int getBottomPadding() {
            return getHeight() - containerView.getBottom() + underControls;
        }

        public int getBottomPadding2() {
            return getHeight() - containerView.getBottom();
        }

        public int getPaddingUnderContainer() {
            return getHeight() - insetBottom - containerView.getBottom();
        }

        @Override
        protected void dispatchDraw(Canvas canvas) {
            float dismiss = frozenDismissProgress != null ? frozenDismissProgress : dismissProgress;
            if (openType == 0) {
                canvas.drawColor(ColorUtils.setAlphaComponent(Color.BLACK, (int) (255 * openProgress * (1f - dismiss))));
            }
            boolean restore = false;
            final float r = lerp(fromRounding, 0, openProgress);
            if (openProgress != 1) {
                if (openType == 0) {
                    fullRectF.set(0, 0, getWidth(), getHeight());
                    fullRectF.offset(containerView.getTranslationX(), containerView.getTranslationY());
                    lerp(fromRect, fullRectF, openProgress, rectF);

                    canvas.save();
                    clipPath.rewind();
                    clipPath.addRoundRect(rectF, r, r, Path.Direction.CW);
                    canvas.clipPath(clipPath);

                    final float alpha = Utilities.clamp(openProgress * 3, 1, 0);
                    canvas.saveLayerAlpha(0, 0, getWidth(), getHeight(), (int) (0xFF * alpha), Canvas.ALL_SAVE_FLAG);
                    canvas.translate(rectF.left, rectF.top - containerView.getTranslationY() * openProgress);
                    final float s = Math.max(rectF.width() / getWidth(), rectF.height() / getHeight());
                    canvas.scale(s, s);
                    restore = true;
                } else if (openType == 1) {
                    applyOpenProgress();
                }
            }
            if (paintView != null) {
                paintView.onParentPreDraw();
            }
            super.dispatchDraw(canvas);
            if (restore) {
                canvas.restore();
                canvas.restore();

                if (fromSourceView != null) {
                    final float alpha = Utilities.clamp(1f - openProgress * 1.5f, 1, 0);
                    final float bcx = rectF.centerX(),
                            bcy = rectF.centerY(),
                            br = Math.min(rectF.width(), rectF.height()) / 2f;
                    if (fromSourceView.backgroundImageReceiver != null) {
                        fromSourceView.backgroundImageReceiver.setImageCoords(rectF);
                        int prevRoundRadius = fromSourceView.backgroundImageReceiver.getRoundRadius()[0];
                        fromSourceView.backgroundImageReceiver.setRoundRadius((int) r);
                        fromSourceView.backgroundImageReceiver.setAlpha(alpha);
                        fromSourceView.backgroundImageReceiver.draw(canvas);
                        fromSourceView.backgroundImageReceiver.setRoundRadius(prevRoundRadius);
                    } else if (fromSourceView.backgroundDrawable != null) {
                        fromSourceView.backgroundDrawable.setBounds((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom);
                        fromSourceView.backgroundDrawable.setAlpha((int) (0xFF * alpha * alpha * alpha));
                        fromSourceView.backgroundDrawable.draw(canvas);
                    } else if (fromSourceView.backgroundPaint != null) {
                        if (fromSourceView.hasShadow) {
                            fromSourceView.backgroundPaint.setShadowLayer(dp(2), 0, dp(3), Theme.multAlpha(0x33000000, alpha));
                        }
                        fromSourceView.backgroundPaint.setAlpha((int) (0xFF * alpha));
                        canvas.drawRoundRect(rectF, r, r, fromSourceView.backgroundPaint);
                    }
                    if (fromSourceView.iconDrawable != null) {
                        rect.set(fromSourceView.iconDrawable.getBounds());
                        fromSourceView.iconDrawable.setBounds(
                            (int) (bcx - fromSourceView.iconSize / 2),
                            (int) (bcy - fromSourceView.iconSize / 2),
                            (int) (bcx + fromSourceView.iconSize / 2),
                            (int) (bcy + fromSourceView.iconSize / 2)
                        );
                        int wasAlpha = fromSourceView.iconDrawable.getAlpha();
                        fromSourceView.iconDrawable.setAlpha((int) (wasAlpha * alpha));
                        fromSourceView.iconDrawable.draw(canvas);
                        fromSourceView.iconDrawable.setBounds(rect);
                        fromSourceView.iconDrawable.setAlpha(wasAlpha);
                    }

                    canvas.save();
                    canvas.translate(fromRect.left, fromRect.top);
                    fromSourceView.drawAbove(canvas, alpha);
                    canvas.restore();
                }
            }
        }

        private boolean flingDetected;
        private boolean touchInCollageList;

        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            flingDetected = false;
            if (collageListView != null && collageListView.isVisible()) {
                final float y = containerView.getY() + actionBarContainer.getY() + collageListView.getY();
                if (ev.getY() >= y && ev.getY() <= y + collageListView.getHeight() || touchInCollageList) {
                    touchInCollageList = ev.getAction() != MotionEvent.ACTION_UP && ev.getAction() != MotionEvent.ACTION_CANCEL;
                    return super.dispatchTouchEvent(ev);
                } else {
                    collageListView.setVisible(false, true);
                    updateActionBarButtons(true);
                }
            }
            if (touchInCollageList && (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_CANCEL)) {
                touchInCollageList = false;
            }
            scaleGestureDetector.onTouchEvent(ev);
            gestureDetector.onTouchEvent(ev);
            if (ev.getAction() == MotionEvent.ACTION_UP && !flingDetected) {
                allowModeScroll = true;
                if (containerView.getTranslationY() > 0) {
                    if (dismissProgress > .4f) {
                        close(true);
                    } else {
                        animateContainerBack();
                    }
                } else if (galleryListView != null && galleryListView.getTranslationY() > 0 && !galleryClosing) {
                    animateGalleryListView(!takingVideo && galleryListView.getTranslationY() < galleryListView.getPadding());
                }
                galleryClosing = false;
                scrollingY = false;
                scrollingX = false;
            }
            return super.dispatchTouchEvent(ev);
        }

        public void cancelGestures() {
            scaleGestureDetector.onTouchEvent(AndroidUtilities.emptyMotionEvent());
            gestureDetector.onTouchEvent(AndroidUtilities.emptyMotionEvent());
        }

        @Override
        public boolean dispatchKeyEventPreIme(KeyEvent event) {
            if (event != null && event.getKeyCode()
                    == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                onBackPressed();
                return true;
            }
            return super.dispatchKeyEventPreIme(event);
        }

        private boolean scaling = false;
        private final class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (!scaling || cameraView == null || currentPage != PAGE_CAMERA || cameraView.isDualTouch() || collageLayoutView.getFilledProgress() >= 1) {
                    return false;
                }
                final float deltaScaleFactor = (detector.getScaleFactor() - 1.0f) * .75f;
                cameraZoom += deltaScaleFactor;
                cameraZoom = Utilities.clamp(cameraZoom, 1, 0);
                cameraView.setZoom(cameraZoom);
                if (zoomControlView != null) {
                    zoomControlView.setZoom(cameraZoom, false);
                }
                showZoomControls(true, true);
                return true;
            }

            @Override
            public boolean onScaleBegin(@NonNull ScaleGestureDetector detector) {
                if (cameraView == null || currentPage != PAGE_CAMERA || wasGalleryOpen) {
                    return false;
                }
                scaling = true;
                return super.onScaleBegin(detector);
            }

            @Override
            public void onScaleEnd(@NonNull ScaleGestureDetector detector) {
                scaling = false;
                animateGalleryListView(false);
                animateContainerBack();
                super.onScaleEnd(detector);
            }
        }

        private float ty, sty, stx;
        private boolean allowModeScroll = true;

        private final class GestureListener extends GestureDetectorFixDoubleTap.OnGestureListener {
            @Override
            public boolean onDown(@NonNull MotionEvent e) {
                sty = 0;
                stx = 0;
                return false;
            }

            @Override
            public void onShowPress(@NonNull MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(@NonNull MotionEvent e) {
                scrollingY = false;
                scrollingX = false;
                if (!hasDoubleTap(e)) {
                    if (onSingleTapConfirmed(e)) {
                        return true;
                    }
                }
                if (isGalleryOpen() && e.getY() < galleryListView.top()) {
                    animateGalleryListView(false);
                    return true;
                }
                return false;
            }

            @Override
            public boolean onScroll(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float distanceX, float distanceY) {
                if (openCloseAnimator != null && openCloseAnimator.isRunning() || galleryOpenCloseSpringAnimator != null || galleryOpenCloseAnimator != null || qrLinkView != null && qrLinkView.inTouch() || recordControl.isTouch() || cameraView != null && cameraView.isDualTouch() || scaling || zoomControlView != null && zoomControlView.isTouch() || inCheck()) {
                    return false;
                }
                if (takingVideo || takingPhoto || currentPage != PAGE_CAMERA) {
                    return false;
                }
                if (!scrollingX) {
                    sty += distanceY;
                    if (!scrollingY && Math.abs(sty) >= touchSlop) {
                        if (collageLayoutView != null) {
                            collageLayoutView.cancelTouch();
                        }
                        scrollingY = true;
                    }
                }
                if (scrollingY) {
                    int galleryMax = windowView.getMeasuredHeight() - (int) (AndroidUtilities.displaySize.y * 0.35f) - (AndroidUtilities.statusBarHeight + ActionBar.getCurrentActionBarHeight());
                    if (galleryListView == null || galleryListView.getTranslationY() >= galleryMax) {
                        ty = containerView.getTranslationY1();
                    } else {
                        ty = galleryListView.getTranslationY() - galleryMax;
                    }
                    if (galleryListView != null && galleryListView.listView.canScrollVertically(-1)) {
                        distanceY = Math.max(0, distanceY);
                    }
                    ty -= distanceY;
                    ty = Math.max(-galleryMax, ty);
                    if (currentPage == PAGE_PREVIEW) {
                        ty = Math.max(0, ty);
                    }
                    if (ty >= 0) {
                        containerView.setTranslationY(ty);
                        if (galleryListView != null) {
                            galleryListView.setTranslationY(galleryMax);
                        }
                    } else {
                        containerView.setTranslationY(0);
                        if (galleryListView == null) {
                            createGalleryListView();
                        }
                        galleryListView.setTranslationY(galleryMax + ty);
                    }
                }
                if (!scrollingY) {
                    stx += distanceX;
                    if (!scrollingX && Math.abs(stx) >= touchSlop) {
                        if (collageLayoutView != null) {
                            collageLayoutView.cancelTouch();
                        }
                        scrollingX = true;
                    }
                }
                return true;
            }

            @Override
            public void onLongPress(@NonNull MotionEvent e) {

            }

            @Override
            public boolean onFling(@NonNull MotionEvent e1, @NonNull MotionEvent e2, float velocityX, float velocityY) {
                if (openCloseAnimator != null && openCloseAnimator.isRunning() || qrLinkView != null && qrLinkView.inTouch() || recordControl.isTouch() || cameraView != null && cameraView.isDualTouch() || scaling || zoomControlView != null && zoomControlView.isTouch() || inCheck()) {
                    return false;
                }
                flingDetected = true;
                allowModeScroll = true;
                boolean r = false;
                if (scrollingY) {
                    if (Math.abs(containerView.getTranslationY1()) >= dp(1)) {
                        if (velocityY > 0 && Math.abs(velocityY) > 2000 && Math.abs(velocityY) > Math.abs(velocityX) || dismissProgress > .4f) {
                            close(true);
                        } else {
                            animateContainerBack();
                        }
                        r = true;
                    } else if (galleryListView != null && !galleryClosing && mode != MODE_LIVE) {
                        if (Math.abs(velocityY) > 200 && (!galleryListView.listView.canScrollVertically(-1) || !wasGalleryOpen)) {
                            animateGalleryListView(!takingVideo && velocityY < 0);
                            r = true;
                        } else {
                            animateGalleryListView(!takingVideo && galleryListView.getTranslationY() < galleryListView.getPadding());
                            r = true;
                        }
                    }
                }
                galleryClosing = false;
                scrollingY = false;
                scrollingX = false;
                if (r && collageLayoutView != null) {
                    collageLayoutView.cancelTouch();
                }
                return r;
            }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (cameraView != null) {
                    cameraView.allowToTapFocus();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (cameraView == null || awaitingPlayer || takingPhoto || !cameraView.isInited() || currentPage != PAGE_CAMERA || mode == MODE_LIVE) {
                    return false;
                }
                cameraView.switchCamera();
                recordControl.rotateFlip(180);
                saveCameraFace(cameraView.isFrontface());
                if (useDisplayFlashlight()) {
                    flashViews.flashIn(null);
                } else {
                    flashViews.flashOut();
                }
                return true;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                if (cameraView != null) {
                    cameraView.clearTapFocus();
                }
                return false;
            }

            @Override
            public boolean hasDoubleTap(MotionEvent e) {
                return currentPage == PAGE_CAMERA && cameraView != null && !awaitingPlayer && cameraView.isInited() && !takingPhoto && !recordControl.isTouch() && (qrLinkView == null || !qrLinkView.inTouch()) && !isGalleryOpen() && galleryListViewOpening == null;
            }
        };

        private boolean ignoreLayout;

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            if (Build.VERSION.SDK_INT < 21) {
                insetTop = AndroidUtilities.statusBarHeight;
                insetBottom = AndroidUtilities.navigationBarHeight;
            }

            final int W = MeasureSpec.getSize(widthMeasureSpec);
            final int H = MeasureSpec.getSize(heightMeasureSpec);
            final int w = W - insetLeft - insetRight;

            final int statusbar = insetTop;
            final int navbar = insetBottom;

            final int hFromW = (int) Math.ceil(w / 9f * 16f);
            underControls = dp(48);
            if (hFromW + underControls <= H - navbar) {
                previewW = w;
                previewH = hFromW;
                underStatusBar = previewH + underControls > H - navbar - statusbar;
            } else {
                underStatusBar = false;
                previewH = H - underControls - navbar - statusbar;
                previewW = (int) Math.ceil(previewH * 9f / 16f);
            }
            underControls = Utilities.clamp(H - previewH - (underStatusBar ? 0 : statusbar), dp(68), dp(48));

            int flags = getSystemUiVisibility();
            if (underStatusBar) {
                flags |= View.SYSTEM_UI_FLAG_FULLSCREEN;
            } else {
                flags &= ~View.SYSTEM_UI_FLAG_FULLSCREEN;
            }
            setSystemUiVisibility(flags);

            containerView.measure(
                MeasureSpec.makeMeasureSpec(previewW, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(previewH + underControls, MeasureSpec.EXACTLY)
            );
            flashViews.backgroundView.measure(
                MeasureSpec.makeMeasureSpec(W, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(H, MeasureSpec.EXACTLY)
            );
            if (thanosEffect != null) {
                thanosEffect.measure(
                    MeasureSpec.makeMeasureSpec(W, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(H, MeasureSpec.EXACTLY)
                );
            }
            if (changeDayNightView != null) {
                changeDayNightView.measure(
                    MeasureSpec.makeMeasureSpec(W, MeasureSpec.EXACTLY),
                    MeasureSpec.makeMeasureSpec(H, MeasureSpec.EXACTLY)
                );
            }
            if (themeSheet != null) {
                themeSheet.measure(widthMeasureSpec, heightMeasureSpec);
            }

            if (galleryListView != null) {
                galleryListView.measure(MeasureSpec.makeMeasureSpec(previewW, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(H, MeasureSpec.EXACTLY));
            }

            if (captionEdit != null) {
                EmojiView emojiView = captionEdit.editText.getEmojiView();
                if (measureKeyboardHeight() > AndroidUtilities.dp(20)) {
                    ignoreLayout = true;
//                    captionEdit.editText.hideEmojiView();
                    ignoreLayout = false;
                }
                if (emojiView != null) {
                    emojiView.measure(
                        MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(emojiView.getLayoutParams().height, MeasureSpec.EXACTLY)
                    );
                }
            }

            if (paintView != null) {
                if (paintView.emojiView != null) {
                    paintView.emojiView.measure(
                            MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                            MeasureSpec.makeMeasureSpec(paintView.emojiView.getLayoutParams().height, MeasureSpec.EXACTLY)
                    );
                }
                if (paintView.reactionLayout != null) {
                    measureChild(paintView.reactionLayout, widthMeasureSpec, heightMeasureSpec);
                    if (paintView.reactionLayout.getReactionsWindow() != null) {
                        measureChild(paintView.reactionLayout.getReactionsWindow().windowView, widthMeasureSpec, heightMeasureSpec);
                    }
                }
            }

            for (int i = 0; i < getChildCount(); ++i) {
                View child = getChildAt(i);
                if (child instanceof DownloadButton.PreparingVideoToast) {
                    child.measure(
                        MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(H, MeasureSpec.EXACTLY)
                    );
                } else if (child instanceof Bulletin.ParentLayout) {
                    child.measure(
                        MeasureSpec.makeMeasureSpec(w, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(Math.min(dp(340), H - (underStatusBar ? 0 : statusbar)), MeasureSpec.EXACTLY)
                    );
                }
            }

            if (cropEditor != null) {
                measureChildExactly(cropEditor, W, H);
                measureChildExactly(cropEditor.contentView, W, H);
            }

            if (cropInlineEditor != null) {
                measureChildExactly(cropInlineEditor, W, H);
                measureChildExactly(cropInlineEditor.contentView, W, H);
            }

            setMeasuredDimension(W, H);
        }

        private void measureChildExactly(View child, int width, int height) {
            child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            if (ignoreLayout) {
                return;
            }
            final int W = right - left;
            final int H = bottom - top;

            final int statusbar = insetTop;
            final int underControls = navbarContainer.getMeasuredHeight();

            final int T = underStatusBar ? 0 : statusbar;
            int l = insetLeft + (W - insetRight - previewW) / 2,
                r = insetLeft + (W - insetRight + previewW) / 2, t, b;
            if (underStatusBar) {
                t = T;
                b = T + previewH + underControls;
            } else {
                t = T + ((H - T - insetBottom) - previewH - underControls) / 2;
                if (openType == 1 && fromRect.top + previewH + underControls < H - insetBottom) {
                    t = (int) fromRect.top;
                } else if (t - T < dp(40)) {
                    t = T;
                }
                b = t + previewH + underControls;
            }

            containerView.layout(l, t, r, b);
            flashViews.backgroundView.layout(0, 0, W, H);
            if (thanosEffect != null) {
                thanosEffect.layout(0, 0, W, H);
            }
            if (changeDayNightView != null) {
                changeDayNightView.layout(0, 0, W, H);
            }

            if (galleryListView != null) {
                galleryListView.layout((W - galleryListView.getMeasuredWidth()) / 2, 0, (W + galleryListView.getMeasuredWidth()) / 2, H);
            }
            if (themeSheet != null) {
                themeSheet.layout((W - themeSheet.getMeasuredWidth()) / 2, H - themeSheet.getMeasuredHeight(), (W + themeSheet.getMeasuredWidth()) / 2, H);
            }

            if (captionEdit != null) {
                EmojiView emojiView = captionEdit.editText.getEmojiView();
                if (emojiView != null) {
                    emojiView.layout(insetLeft, H - insetBottom - emojiView.getMeasuredHeight(), W - insetRight, H - insetBottom);
                }
            }

            if (paintView != null) {
                if (paintView.emojiView != null) {
                    paintView.emojiView.layout(insetLeft, H - insetBottom - paintView.emojiView.getMeasuredHeight(), W - insetRight, H - insetBottom);
                }
                if (paintView.reactionLayout != null) {
                    paintView.reactionLayout.layout(insetLeft, insetTop, insetLeft + paintView.reactionLayout.getMeasuredWidth(), insetTop + paintView.reactionLayout.getMeasuredHeight());
                    View reactionsWindowView = paintView.reactionLayout.getReactionsWindow() != null ? paintView.reactionLayout.getReactionsWindow().windowView : null;
                    if (reactionsWindowView != null) {
                        reactionsWindowView.layout(insetLeft, insetTop, insetLeft + reactionsWindowView.getMeasuredWidth(), insetTop + reactionsWindowView.getMeasuredHeight());
                    }
                }
            }

            if (cropEditor != null) {
                cropEditor.controlsLayout.setPadding(0, insetTop, 0, insetBottom);
                cropEditor.layout(0, 0, W, H);
                cropEditor.contentView.layout(0, 0, W, H);
            }

            if (cropInlineEditor != null) {
                cropInlineEditor.controlsLayout.setPadding(0, insetTop, 0, insetBottom);
                cropInlineEditor.layout(0, 0, W, H);
                cropInlineEditor.contentView.layout(0, 0, W, H);
            }

            for (int i = 0; i < getChildCount(); ++i) {
                View child = getChildAt(i);
                if (child instanceof DownloadButton.PreparingVideoToast) {
                    child.layout(0, 0, W, H);
                } else if (child instanceof Bulletin.ParentLayout) {
                    child.layout(0, t, child.getMeasuredWidth(), t + child.getMeasuredHeight());
                }
            }
        }

        public void drawBlurBitmap(Bitmap bitmap, float amount) {
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(0xff000000);
            final float scale = (float) bitmap.getWidth() / windowView.getWidth();
            canvas.scale(scale, scale);

            TextureView textureView = previewView.getTextureView();
            if (textureView == null) {
                textureView = previewView.filterTextureView;
            }
            if (textureView != null) {
                canvas.save();
                canvas.translate(containerView.getX() + previewContainer.getX(), containerView.getY() + previewContainer.getY());
                int w = (int) (textureView.getWidth() / amount), h = (int) (textureView.getHeight() / amount);
                try {
                    Bitmap textureBitmap = textureView.getBitmap(w, h);
                    canvas.scale(1f / scale, 1f / scale);
                    canvas.drawBitmap(textureBitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
                    textureBitmap.recycle();
                } catch (Exception ignore) {}
                canvas.restore();
            }
            canvas.save();
            canvas.translate(containerView.getX(), containerView.getY());
            for (int i = 0; i < containerView.getChildCount(); ++i) {
                View child = containerView.getChildAt(i);
                canvas.save();
                canvas.translate(child.getX(), child.getY());
                if (child.getVisibility() != View.VISIBLE) {
                    continue;
                } else if (child == previewContainer) {
                    for (int j = 0; j < previewContainer.getChildCount(); ++j) {
                        child = previewContainer.getChildAt(j);
                        if (child == previewView || child == cameraView || child.getVisibility() != View.VISIBLE) {
                            continue;
                        }
                        canvas.save();
                        canvas.translate(child.getX(), child.getY());
                        child.draw(canvas);
                        canvas.restore();
                    }
                } else {
                    child.draw(canvas);
                }
                canvas.restore();
            }
            canvas.restore();
        }
    }

    private class ContainerView extends FrameLayout {
        public ContainerView(Context context) {
            super(context);
        }

        public void updateBackground() {
            if (openType == 0) {
                setBackground(Theme.createRoundRectDrawable(dp(12), 0xff000000));
            } else {
                setBackground(null);
            }
        }

        @Override
        public void invalidate() {
            if (openCloseAnimator != null && openCloseAnimator.isRunning()) {
                return;
            }
            super.invalidate();
        }

        private float translationY1;
        private float translationY2;

        public void setTranslationY2(float translationY2) {
            super.setTranslationY(this.translationY1 + (this.translationY2 = translationY2));
        }

        public float getTranslationY1() {
            return translationY1;
        }

        public float getTranslationY2() {
            return translationY2;
        }

        @Override
        public void setTranslationY(float translationY) {
            super.setTranslationY((this.translationY1 = translationY) + translationY2);

            dismissProgress = Utilities.clamp(translationY / getMeasuredHeight() * 4, 1, 0);
            checkBackgroundVisibility();
            windowView.invalidate();

            final float scale = 1f - .1f * Utilities.clamp(getTranslationY() / AndroidUtilities.dp(320), 1, 0);
            setScaleX(scale);
            setScaleY(scale);
        }

        @Override
        protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
            final int t = underStatusBar ? insetTop : 0;

            final int w = right - left;
            final int h = bottom - top;

            previewContainer.layout(0, 0, previewW, previewH);
            previewContainer.setPivotX(previewW * .5f);
            actionBarContainer.layout(0, t, previewW, t + actionBarContainer.getMeasuredHeight());
            controlContainer.layout(0, previewH - controlContainer.getMeasuredHeight(), previewW, previewH);
            navbarContainer.layout(0, previewH, previewW, previewH + navbarContainer.getMeasuredHeight());
            captionContainer.layout(0, 0, previewW, previewH);
            if (captionEditOverlay != null) {
                captionEditOverlay.layout(0, 0, w, h);
            }
            flashViews.foregroundView.layout(0, 0, w, h);

            if (captionEdit.mentionContainer != null) {
                captionEdit.mentionContainer.layout(0, 0, previewW, previewH);
                captionEdit.updateMentionsLayoutPosition();
            }

            if (photoFilterView != null) {
                photoFilterView.layout(0, 0, photoFilterView.getMeasuredWidth(), photoFilterView.getMeasuredHeight());
            }
            if (paintView != null) {
                paintView.layout(0, 0, paintView.getMeasuredWidth(), paintView.getMeasuredHeight());
            }

            for (int i = 0; i < getChildCount(); ++i) {
                View child = getChildAt(i);
                if (child instanceof ItemOptions.DimView) {
                    child.layout(0, 0, w, h);
                }
            }

            setPivotX((right - left) / 2f);
            setPivotY(-h * .2f);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            final int W = MeasureSpec.getSize(widthMeasureSpec);
            final int H = MeasureSpec.getSize(heightMeasureSpec);

            measureChildExactly(previewContainer, previewW, previewH);
            applyFilterMatrix();
            measureChildExactly(actionBarContainer, previewW, dp(56 + 56 + 38));
            measureChildExactly(controlContainer, previewW, dp(220));
            measureChildExactly(navbarContainer, previewW, underControls);
            measureChildExactly(captionContainer, previewW, previewH);
            measureChildExactly(flashViews.foregroundView, W, H);
            if (captionEditOverlay != null) {
                measureChildExactly(captionEditOverlay, W, H);
            }

            if (captionEdit.mentionContainer != null) {
                measureChildExactly(captionEdit.mentionContainer, previewW, previewH);
            }

            if (photoFilterView != null) {
                measureChildExactly(photoFilterView, W, H);
            }
            if (paintView != null) {
                measureChildExactly(paintView, W, H);
            }

            for (int i = 0; i < getChildCount(); ++i) {
                View child = getChildAt(i);
                if (child instanceof ItemOptions.DimView) {
                    measureChildExactly(child, W, H);
                }
            }

            setMeasuredDimension(W, H);
        }

        private void measureChildExactly(View child, int width, int height) {
            child.measure(MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }

        private final Paint topGradientPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private LinearGradient topGradient;

        @Override
        protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
            boolean r = super.drawChild(canvas, child, drawingTime);
            if (child == previewContainer) {
                final float top = underStatusBar ? AndroidUtilities.statusBarHeight : 0;
                if (topGradient == null) {
                    topGradient = new LinearGradient(0, top, 0, top + dp(72), new int[] {0x40000000, 0x00000000}, new float[] { top / (top + dp(72)), 1 }, Shader.TileMode.CLAMP );
                    topGradientPaint.setShader(topGradient);
                }
                topGradientPaint.setAlpha(0xFF);
                AndroidUtilities.rectTmp.set(0, 0, getWidth(), dp(72 + 12) + top);
                canvas.drawRoundRect(AndroidUtilities.rectTmp, dp(12), dp(12), topGradientPaint);
            }
            return r;
        }
    }

    public static final int PAGE_CAMERA = 0;
    public static final int PAGE_PREVIEW = 1;
    public static final int PAGE_COVER = 2;
    private int currentPage = PAGE_CAMERA;

    public static final int EDIT_MODE_NONE = -1;
    public static final int EDIT_MODE_PAINT = 0;
    public static final int EDIT_MODE_FILTER = 1;
    public static final int EDIT_MODE_TIMELINE = 2;
    public static final int EDIT_MODE_CROP = 3;
    public static final int EDIT_MODE_CROP_INLINE = 4;
    private int currentEditMode = EDIT_MODE_NONE;

    private FrameLayout previewContainer;
    private FrameLayout actionBarContainer;
    private LinearLayout actionBarButtons;
    private FrameLayout controlContainer;
    private FrameLayout captionContainer;
    private FrameLayout navbarContainer;

    private FlashViews.ImageViewInvertable backButton;
    private SelectPeerView livePeerView;
    private SimpleTextView titleTextView;
    private StoryPrivacyBottomSheet privacySheet;
    private BlurringShader.BlurManager blurManager;
    private PreviewView.TextureViewHolder videoTextureHolder;
    private View captionEditOverlay;

    private boolean isReposting;
    private long botId;
    private String botLang;
    private TLRPC.InputMedia botEdit;

    private TLRPC.InputPeer livePeer;
    private StoryPrivacyBottomSheet.StoryPrivacy livePrivacy = new StoryPrivacyBottomSheet.StoryPrivacy();

    private CollageLayout lastCollageLayout;

    -> {
                whenStarted.run();

                hintTextView.setText(getString(byLongPress ? R.string.StoryHintSwipeToZoom : R.string.StoryHintPinchToZoom), false);
                animateRecording(true, true);
                setAwakeLock(true);

                collageListView.setVisible(false, true);
                videoTimerView.setRecording(true, true);
                showVideoTimer(true, true);
            }, cameraView, true);

            if (mode != MODE_VIDEO) {
                mode = MODE_VIDEO;
                collageListView.setVisible(false, true);
                showVideoTimer(mode == MODE_VIDEO, true);
                modeSwitcherView.switchMode(mode);
                recordControl.startAsVideo(mode == MODE_VIDEO);
            }
        }

        @Override
        public void onVideoRecordLocked() {
            hintTextView.setText(getString(R.string.StoryHintPinchToZoom), true);
        }

        @Override
        public void onVideoRecordPause() {

        }

        @Override
        public void onVideoRecordResume() {

        }

        @Override
        public void onVideoRecordEnd(boolean byDuration) {
            if (stoppingTakingVideo || !takingVideo) {
                return;
            }
            stoppingTakingVideo = true;
            AndroidUtilities.runOnUIThread(() -> {
                if (qrScanner != null) {
                    qrScanner.setPaused(false);
                }
                if (takingVideo && stoppingTakingVideo && cameraView != null) {
                    showZoomControls(false, true);
//                    animateRecording(false, true);
//                    setAwakeLock(false);
                    CameraController.getInstance().stopVideoRecording(cameraView.getCameraSessionRecording(), false, false);
                }
            }, byDuration ? 0 : 400);
        }

        @Override
        public void onVideoDuration(long duration) {
            videoTimerView.setDuration(duration, true);
        }

        @Override
        public void onGalleryClick() {
            if (currentPage == PAGE_CAMERA && !takingPhoto && !takingVideo && requestGalleryPermission()) {
                animateGalleryListView(true);
            }
        }

        @Override
        public void onFlipClick() {
            if (cameraView == null || awaitingPlayer || takingPhoto || !cameraView.isInited() || currentPage != PAGE_CAMERA) {
                return;
            }
            if (savedDualHint != null) {
                savedDualHint.hide();
            }
            if (useDisplayFlashlight() && frontfaceFlashModes != null && !frontfaceFlashModes.isEmpty()) {
                final String mode = frontfaceFlashModes.get(frontfaceFlashMode);
                SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("camera", Activity.MODE_PRIVATE);
                sharedPreferences.edit().putString("flashMode", mode).commit();
            }
            cameraView.switchCamera();
            saveCameraFace(cameraView.isFrontface());
            if (useDisplayFlashlight()) {
                flashViews.flashIn(null);
            } else {
                flashViews.flashOut();
            }
        }

        @Override
        public void onFlipLongClick() {
            if (cameraView != null) {
                cameraView.toggleDual();
            }
        }

        @Override
        public void onZoom(float zoom) {
            zoomControlView.setZoom(zoom, true);
            showZoomControls(false, true);
        }
    };

    private void setAwakeLock(boolean lock) {
        if (lock) {
            windowLayoutParams.flags |= WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        } else {
            windowLayoutParams.flags &= ~WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
        }
        try {
            windowManager.updateViewLayout(windowView, windowLayoutParams);
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    private AnimatorSet recordingAnimator;
    private boolean animatedRecording;
    private boolean animatedRecordingWasInCheck;
    private void animateRecording(boolean recording, boolean animated) {
        if (recording) {
            if (dualHint != null) {
                dualHint.hide();
            }
            if (savedDualHint != null) {
                savedDualHint.hide();
            }
            if (muteHint != null) {
                muteHint.hide();
            }
            if (cameraHint != null) {
                cameraHint.hide();
            }
        }
        if (animatedRecording == recording && animatedRecordingWasInCheck == inCheck()) {
            return;
        }
        if (recordingAnimator != null) {
            recordingAnimator.cancel();
            recordingAnimator = null;
        }
        animatedRecording = recording;
        animatedRecordingWasInCheck = inCheck();
        if (recording && collageListView != null && collageListView.isVisible()) {
            collageListView.setVisible(false, animated);
        }
        updateActionBarButtons(animated);
        if (animated) {
            recordingAnimator = new AnimatorSet();
            recordingAnimator.playTogether(
                ObjectAnimator.ofFloat(hintTextView, View.ALPHA, recording && currentPage == PAGE_CAMERA && !inCheck() ? 1 : 0),
                ObjectAnimator.ofFloat(hintTextView, View.TRANSLATION_Y, recording && currentPage == PAGE_CAMERA && !inCheck() ? 0 : dp(16)),
                ObjectAnimator.ofFloat(collageHintTextView, View.ALPHA, !recording && currentPage == PAGE_CAMERA && inCheck() ? 0.6f : 0),
                ObjectAnimator.ofFloat(collageHintTextView, View.TRANSLATION_Y, !recording && currentPage == PAGE_CAMERA && inCheck() ? 0 : dp(16)),
                ObjectAnimator.ofFloat(modeSwitcherView, View.ALPHA, recording || currentPage != PAGE_CAMERA || inCheck() ? 0 : 1),
                ObjectAnimator.ofFloat(modeSwitcherView, View.TRANSLATION_Y, recording || currentPage != PAGE_CAMERA || inCheck() ? dp(16) : 0)
            );
            recordingAnimator.setDuration(260);
            recordingAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            recordingAnimator.start();
        } else {
            hintTextView.setAlpha(recording && currentPage == PAGE_CAMERA && !inCheck() ? 1f : 0);
            hintTextView.setTranslationY(recording && currentPage == PAGE_CAMERA && !inCheck() ? 0 : dp(16));
            collageHintTextView.setAlpha(!recording && currentPage == PAGE_CAMERA && inCheck() ? 0.6f : 0);
            collageHintTextView.setTranslationY(!recording && currentPage == PAGE_CAMERA && inCheck() ? 0 : dp(16));
            modeSwitcherView.setAlpha(recording || currentPage != PAGE_CAMERA || inCheck() ? 0 : 1f);
            modeSwitcherView.setTranslationY(recording || currentPage != PAGE_CAMERA || inCheck() ? dp(16) : 0);
        }
    }

    private boolean isDark;
    private void checkIsDark() {
        if (cameraView == null || cameraView.getTextureView() == null) {
            isDark = false;
            return;
        }
        final Bitmap bitmap = cameraView.getTextureView().getBitmap();
        if (bitmap == null) {
            isDark = false;
            return;
        }
        float l = 0;
        final int sx = bitmap.getWidth() / 12;
        final int sy = bitmap.getHeight() / 12;
        for (int x = 0; x < 10; ++x) {
            for (int y = 0; y < 10; ++y) {
                l += AndroidUtilities.computePerceivedBrightness(bitmap.getPixel((1 + x) * sx, (1 + y) * sy));
            }
        }
        l /= 100;
        bitmap.recycle();
        isDark = l < .22f;
    }

    private boolean useDisplayFlashlight() {
        return (takingPhoto || takingVideo) && (cameraView != null && cameraView.isFrontface()) && (frontfaceFlashMode == 2 || frontfaceFlashMode == 1 && isDark);
    }

    private boolean videoTimerShown = true;
    private void showVideoTimer(boolean show, boolean animated) {
        if (videoTimerShown == show) {
            return;
        }

        videoTimerShown = show;
        if (animated) {
            videoTimerView.animate().alpha(show ? 1 : 0).setDuration(350).setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT).withEndAction(() -> {
                if (!show) {
                    videoTimerView.setRecording(false, false);
                }
            }).start();
        } else {
            videoTimerView.clearAnimation();
            videoTimerView.setAlpha(show ? 1 : 0);
            if (!show) {
                videoTimerView.setRecording(false, false);
            }
        }
    }

    private Runnable zoomControlHideRunnable;
    private AnimatorSet zoomControlAnimation;

    private void showZoomControls(boolean show, boolean animated) {
        if (zoomControlView.getTag() != null && show || zoomControlView.getTag() == null && !show) {
            if (show) {
                if (zoomControlHideRunnable != null) {
                    AndroidUtilities.cancelRunOnUIThread(zoomControlHideRunnable);
                }
                AndroidUtilities.runOnUIThread(zoomControlHideRunnable = () -> {
                    showZoomControls(false, true);
                    zoomControlHideRunnable = null;
                }, 2000);
            }
            return;
        }
        if (zoomControlAnimation != null) {
            zoomControlAnimation.cancel();
        }
        zoomControlView.setTag(show ? 1 : null);
        zoomControlAnimation = new AnimatorSet();
        zoomControlAnimation.setDuration(180);
        if (show) {
            zoomControlView.setVisibility(View.VISIBLE);
        }
        zoomControlAnimation.playTogether(ObjectAnimator.ofFloat(zoomControlView, View.ALPHA, show ? 1.0f : 0.0f));
        zoomControlAnimation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (!show) {
                    zoomControlView.setVisibility(View.GONE);
                }
                zoomControlAnimation = null;
            }
        });
        zoomControlAnimation.start();
        if (show) {
            AndroidUtilities.runOnUIThread(zoomControlHideRunnable = () -> {
                showZoomControls(false, true);
                zoomControlHideRunnable = null;
            }, 2000);
        }
    }

    public boolean onBackPressed() {
        if (openCloseAnimator != null && openCloseAnimator.isRunning()) {
            return false;
        }
        if (captionEdit != null && captionEdit.stopRecording()) {
            return false;
        }
        if (takingVideo) {
            recordControl.stopRecording();
            return false;
        }
        if (takingPhoto) {
            return false;
        }
        if (captionEdit.onBackPressed()) {
            return false;
        } else if (storiesSelector.onBackPressed()) {
            return false;
        } else if (themeSheet != null) {
            themeSheet.dismiss();
            return false;
        } else if (galleryListView != null) {
            if (galleryListView.onBackPressed()) {
                return false;
            }
            animateGalleryListView(false);
            lastGallerySelectedAlbum = null;
            return false;
        } else if (currentEditMode == EDIT_MODE_PAINT && paintView != null && paintView.onBackPressed()) {
            return false;
        } else if (currentEditMode > EDIT_MODE_NONE) {
            switchToEditMode(EDIT_MODE_NONE, true);
            return false;
        } else if (currentPage == PAGE_CAMERA && collageLayoutView.hasContent()) {
            collageLayoutView.clear(true);
            updateActionBarButtons(true);
            return false;
        } else if (currentPage == PAGE_PREVIEW && (outputEntry == null || !outputEntry.isRepost && !outputEntry.isRepostMessage) && !isReposting && (outputEntry == null || !outputEntry.isEdit || (paintView != null && paintView.hasChanges()) || outputEntry.editedMedia || outputEntry.editedCaption)) {
            if (paintView != null && paintView.onBackPressed()) {
                return false;
            } else if (botId == 0 && (fromGallery && !collageLayoutView.hasLayout() && (paintView == null || !paintView.hasChanges()) && (outputEntry == null || outputEntry.filterFile == null) || !previewButtons.isShareEnabled()) && (outputEntry == null || !outputEntry.isEdit || !outputEntry.isRepost && !outputEntry.isRepostMessage) && !isReposting && (outputEntry == null || !outputEntry.isShare)) {
                navigateTo(PAGE_CAMERA, true);
            } else {
                if (botId != 0) {
                    close(true);
                } else {
                    showDismissEntry();
                }
            }
            return false;
        } else if (currentPage == PAGE_COVER && !(outputEntry == null || outputEntry.isEditingCover)) {
            processDone();
            navigateTo(PAGE_PREVIEW, true);
            return false;
        } else {
            close(true);
            return true;
        }
    }

    private void setReply() {
        if (captionEdit == null) return;
        if (outputEntry == null || !outputEntry.isRepost) {
            captionEdit.setReply(null, null);
        } else {
            TLRPC.Peer peer = outputEntry.repostPeer;
            CharSequence peerName;
            if (peer instanceof TLRPC.TL_peerUser) {
                TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(peer.user_id);
                String name = UserObject.getUserName(user);
                peerName = outputEntry.repostPeerName = new SpannableStringBuilder(MessageObject.userSpan()).append(" ").append(name);
            } else {
                TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-DialogObject.getPeerDialogId(peer));
                String name = chat == null ? "" : chat.title;
                peerName = outputEntry.repostPeerName = new SpannableStringBuilder(MessageObject.userSpan()).append(" ").append(name);
            }
            CharSequence repostCaption = outputEntry.repostCaption;
            if (TextUtils.isEmpty(repostCaption)) {
                SpannableString s = new SpannableString(getString(R.string.Story));
                s.setSpan(new CharacterStyle() {
                    @Override
                    public void updateDrawState(TextPaint tp) {
                        tp.setAlpha(0x80);
                    }
                }, 0, s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                repostCaption = s;
            }
            captionEdit.setReply(peerName, repostCaption);
        }
    }

    private Runnable afterPlayerAwait;
    private boolean previewAlreadySet;
    public void navigateToPreviewWithPlayerAwait(Runnable open, long seekTo) {
        navigateToPreviewWithPlayerAwait(open, seekTo, 800);
    }
    public void navigateToPreviewWithPlayerAwait(Runnable open, long seekTo, long ms) {
        if (awaitingPlayer || outputEntry == null) {
            return;
        }
        if (afterPlayerAwait != null) {
            AndroidUtilities.cancelRunOnUIThread(afterPlayerAwait);
        }
        previewAlreadySet = true;
        awaitingPlayer = true;
        afterPlayerAwait = () -> {
            animateGalleryListView(false);
            AndroidUtilities.cancelRunOnUIThread(afterPlayerAwait);
            afterPlayerAwait = null;
            awaitingPlayer = false;
            open.run();
        };
        previewView.setAlpha(0f);
        previewView.setVisibility(View.VISIBLE);
        previewView.set(outputEntry, afterPlayerAwait, seekTo);
        previewView.setupAudio(outputEntry, false);
        AndroidUtilities.runOnUIThread(afterPlayerAwait, ms);
    }

    private AnimatorSet pageAnimator;
    public void navigateTo(int page, boolean animated) {
        if (page == currentPage) {
            return;
        }

        final int oldPage = currentPage;
        currentPage = page;

        if (pageAnimator != null) {
            pageAnimator.cancel();
        }

        onNavigateStart(oldPage, page);
        if (previewButtons != null) {
            previewButtons.appear(page == PAGE_PREVIEW, animated);
        }
        showVideoTimer(page == PAGE_CAMERA && (mode == MODE_VIDEO) && !collageListView.isVisible() && !inCheck(), animated);
        if (page != PAGE_PREVIEW) {
            videoTimeView.show(false, animated);
        }
        setActionBarButtonVisible(backButton, !collageListView.isVisible(), animated);
        setActionBarButtonVisible(flashButton, !animatedRecording && page == PAGE_CAMERA && !collageListView.isVisible() && flashButtonMode != null && !inCheck(), animated);
        setActionBarButtonVisible(dualButton, !animatedRecording && page == PAGE_CAMERA && cameraView != null && cameraView.dualAvailable() && !collageListView.isVisible() && !collageLayoutView.hasLayout(), true);
        setActionBarButtonVisible(collageButton, !animatedRecording && page == PAGE_CAMERA && !collageListView.isVisible(), animated);
        updateActionBarButtons(animated);
        if (animated) {
            pageAnimator = new AnimatorSet();

            ArrayList<Animator> animators = new ArrayList<>();

            if (cameraView != null) {
                animators.add(ObjectAnimator.ofFloat(cameraView, View.ALPHA, page == PAGE_CAMERA ? 1 : 0));
            }
//            cameraViewThumb.setVisibility(View.VISIBLE);
//            animators.add(ObjectAnimator.ofFloat(cameraViewThumb, View.ALPHA, page == PAGE_CAMERA ? 1 : 0));
            animators.add(ObjectAnimator.ofFloat(previewView, View.ALPHA, page == PAGE_PREVIEW && !collageLayoutView.hasLayout() || page == PAGE_COVER ? 1 : 0));
            animators.add(ObjectAnimator.ofFloat(collageLayoutView, View.ALPHA, page == PAGE_CAMERA || page == PAGE_PREVIEW && collageLayoutView.hasLayout() ? 1 : 0));

            animators.add(ObjectAnimator.ofFloat(recordControl, View.ALPHA, page == PAGE_CAMERA ? 1 : 0));
//            animators.add(ObjectAnimator.ofFloat(flashButton, View.ALPHA, page == PAGE_CAMERA ? 1 : 0));
//            animators.add(ObjectAnimator.ofFloat(dualButton, View.ALPHA, page == PAGE_CAMERA && cameraView != null && cameraView.dualAvailable() ? 1 : 0));
            animators.add(ObjectAnimator.ofFloat(recordControl, View.TRANSLATION_Y, page == PAGE_CAMERA ? 0 : dp(24)));
            animators.add(ObjectAnimator.ofFloat(qrLinkView, View.ALPHA, page == PAGE_CAMERA ? 1 : 0));
            animators.add(ObjectAnimator.ofFloat(modeSwitcherView, View.ALPHA, page == PAGE_CAMERA && !inCheck() ? 1 : 0));
            animators.add(ObjectAnimator.ofFloat(modeSwitcherView, View.TRANSLATION_Y, page == PAGE_CAMERA && !inCheck() ? 0 : dp(24)));
//            backButton.setVisibility(View.VISIBLE);
//            animators.add(ObjectAnimator.ofFloat(backButton, View.ALPHA, 1));
            animators.add(ObjectAnimator.ofFloat(hintTextView, View.ALPHA, page == PAGE_CAMERA && animatedRecording && !inCheck() ? 1 : 0));
            animators.add(ObjectAnimator.ofFloat(collageHintTextView, View.ALPHA, page == PAGE_CAMERA && !animatedRecording && inCheck() ? 0.6f : 0));
            animators.add(ObjectAnimator.ofFloat(captionContainer, View.ALPHA, page == PAGE_PREVIEW && (outputEntry == null || outputEntry.botId == 0) || page == PAGE_COVER ? 1f : 0));
            animators.add(ObjectAnimator.ofFloat(captionContainer, View.TRANSLATION_Y, page == PAGE_PREVIEW && (outputEntry == null || outputEntry.botId == 0) || page == PAGE_COVER ? 0 : dp(12)));
            animators.add(ObjectAnimator.ofFloat(captionEdit, View.ALPHA, page == PAGE_COVER ? 0f : 1f));
            animators.add(ObjectAnimator.ofFloat(titleTextView, View.ALPHA, page == PAGE_PREVIEW || page == PAGE_COVER ? 1f : 0));
            animators.add(ObjectAnimator.ofFloat(coverButton, View.ALPHA, page == PAGE_COVER ? 1f : 0f));

            animators.add(ObjectAnimator.ofFloat(timelineView, View.ALPHA, page == PAGE_PREVIEW ? 1f : 0));
            animators.add(ObjectAnimator.ofFloat(coverTimelineView, View.ALPHA, page == PAGE_COVER ? 1f : 0));

            animators.add(ObjectAnimator.ofFloat(muteButton, View.ALPHA, page == PAGE_PREVIEW && mode == MODE_VIDEO ? 1f : 0));
            animators.add(ObjectAnimator.ofFloat(playButton, View.ALPHA, page == PAGE_PREVIEW && (mode == MODE_VIDEO || outputEntry != null && !TextUtils.isEmpty(outputEntry.audioPath)) ? 1f : 0));
            animators.add(ObjectAnimator.ofFloat(downloadButton, View.ALPHA, page == PAGE_PREVIEW ? 1f : 0));
            if (themeButton != null) {
                animators.add(ObjectAnimator.ofFloat(themeButton, View.ALPHA, page == PAGE_PREVIEW && (outputEntry != null && outputEntry.isRepostMessage) ? 1f : 0));
            }
//            animators.add(ObjectAnimator.ofFloat(privacySelector, View.ALPHA, page == PAGE_PREVIEW ? 1f : 0));

            animators.add(ObjectAnimator.ofFloat(zoomControlView, View.ALPHA, 0));

            pageAnimator.playTogether(animators);
            pageAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    onNavigateEnd(oldPage, page);
                }
            });
            pageAnimator.setDuration(460);
            pageAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            pageAnimator.start();
        } else {
            if (cameraView != null) {
                cameraView.setAlpha(page == PAGE_CAMERA ? 1 : 0);
            }
            previewView.setAlpha(page == PAGE_PREVIEW && !collageLayoutView.hasLayout() || page == PAGE_COVER ? 1f : 0);
            collageLayoutView.setAlpha(page == PAGE_CAMERA || page == PAGE_PREVIEW && collageLayoutView.hasLayout() ? 1 : 0);
            recordControl.setAlpha(page == PAGE_CAMERA ? 1f : 0);
            recordControl.setTranslationY(page == PAGE_CAMERA ? 0 : dp(16));
            qrLinkView.setAlpha(page == PAGE_CAMERA ? 1.0f : 0.0f);
            modeSwitcherView.setAlpha(page == PAGE_CAMERA && !inCheck() ? 1f : 0);
            modeSwitcherView.setTranslationY(page == PAGE_CAMERA && !inCheck() ? 0 : dp(16));
            hintTextView.setAlpha(page == PAGE_CAMERA && animatedRecording && !inCheck() ? 1f : 0);
            collageHintTextView.setAlpha(page == PAGE_CAMERA && !animatedRecording && inCheck() ? 0.6f : 0);
            captionContainer.setAlpha(page == PAGE_PREVIEW || page == PAGE_COVER ? 1f : 0);
            captionContainer.setTranslationY(page == PAGE_PREVIEW || page == PAGE_COVER ? 0 : dp(12));
            captionEdit.setAlpha(page == PAGE_COVER ? 0f : 1f);
            muteButton.setAlpha(page == PAGE_PREVIEW && mode == MODE_VIDEO ? 1f : 0);
            playButton.setAlpha(page == PAGE_PREVIEW && (mode == MODE_VIDEO || outputEntry != null && !TextUtils.isEmpty(outputEntry.audioPath)) ? 1f : 0);
            downloadButton.setAlpha(page == PAGE_PREVIEW ? 1f : 0);
            if (themeButton != null) {
                themeButton.setAlpha(page == PAGE_PREVIEW && (outputEntry != null && outputEntry.isRepostMessage) ? 1f : 0);
            }
//            privacySelector.setAlpha(page == PAGE_PREVIEW ? 1f : 0);
            timelineView.setAlpha(page == PAGE_PREVIEW ? 1f : 0);
            coverTimelineView.setAlpha(page == PAGE_COVER ? 1f : 0f);
            titleTextView.setAlpha(page == PAGE_PREVIEW || page == PAGE_COVER ? 1f : 0f);
            coverButton.setAlpha(page == PAGE_COVER ? 1f : 0f);
            onNavigateEnd(oldPage, page);
        }
    }

    private ValueAnimator containerViewBackAnimator;
    private boolean applyContainerViewTranslation2 = true;
    private void animateContainerBack() {
        if (containerViewBackAnimator != null) {
            containerViewBackAnimator.cancel();
            containerViewBackAnimator = null;
        }
        applyContainerViewTranslation2 = false;
        float y1 = containerView.getTranslationY1(), y2 = containerView.getTranslationY2(), a = containerView.getAlpha();
        containerViewBackAnimator = ValueAnimator.ofFloat(1, 0);
        containerViewBackAnimator.addUpdateListener(anm -> {
            final float t = (float) anm.getAnimatedValue();
            containerView.setTranslationY(y1 * t);
            containerView.setTranslationY2(y2 * t);
//            containerView.setAlpha(AndroidUtilities.lerp(a, 1f, t));
        });
        containerViewBackAnimator.setDuration(340);
        containerViewBackAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
        containerViewBackAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                containerViewBackAnimator = null;
                containerView.setTranslationY(0);
                containerView.setTranslationY2(0);
            }
        });
        containerViewBackAnimator.start();
    }

    public StoryRecorder setMode(int mode) {
        if (this.mode == mode)
            return this;
        this.mode = mode;
        if (modeSwitcherView != null) {
            modeSwitcherView.switchMode(mode);
        }
        showVideoTimer(mode == MODE_VIDEO, true);
        if (collageListView != null) {
            collageListView.setVisible(false, true);
        }
        updateActionBarButtons(false);
        return this;
    }

    private void openThemeSheet() {
        if (themeSheet == null) {
            themeSheet = new StoryThemeSheet(getContext(), currentAccount, resourcesProvider, () -> {
                windowView.removeView(themeSheet);
                themeSheet = null;
            }) {
                @Override
                protected void updateWallpaper() {
                    previewView.setupWallpaper(outputEntry, true);
                }
            };
            windowView.addView(themeSheet);
        }
        themeSheet.open(outputEntry);
    }

    private Parcelable lastGalleryScrollPosition;
    private MediaController.AlbumEntry lastGallerySelectedAlbum;

    private void createGalleryListView() {
        createGalleryListView(false);
    }

    private void destroyGalleryListView() {
        if (galleryListView == null) {
            return;
        }
        windowView.removeView(galleryListView);
        galleryListView = null;
        if (galleryOpenCloseAnimator != null) {
            galleryOpenCloseAnimator.cancel();
            galleryOpenCloseAnimator = null;
        }
        if (galleryOpenCloseSpringAnimator != null) {
            galleryOpenCloseSpringAnimator.cancel();
            galleryOpenCloseSpringAnimator = null;
        }
        galleryListViewOpening = null;
    }

    private void createGalleryListView(boolean forAddingPart) {
        if (galleryListView != null && (collageLayoutView != null && collageLayoutView.hasLayout()) == galleryListView.onlyCollaging || getContext() == null) {
            return;
        }
        if (galleryListView != null) {
            destroyGalleryListView();
        }

        galleryListView = new GalleryListView(currentAccount, getContext(), resourcesProvider, lastGallerySelectedAlbum, forAddingPart, 1.39f, !forAddingPart, collageLayoutView != null && collageLayoutView.hasLayout()) {
            @Override
            public void setTranslationY(float translationY) {
                super.setTranslationY(translationY);
                if (applyContainerViewTranslation2) {
                    final float amplitude = windowView.getMeasuredHeight() - galleryListView.top();
                    float t = Utilities.clamp(1f - translationY / amplitude, 1, 0);
                    containerView.setTranslationY2(t * dp(-32));
                    containerView.setAlpha(1 - .6f * t);
                    actionBarContainer.setAlpha(1f - t);
                }
            }

            @Override
            public void firstLayout() {
                galleryListView.setTranslationY(windowView.getMeasuredHeight() - galleryListView.top());
                if (galleryLayouted != null) {
                    galleryLayouted.run();
                    galleryLayouted = null;
                }
            }

            @Override
            protected void onFullScreen(boolean isFullscreen) {
                if (currentPage == PAGE_CAMERA && isFullscreen) {
                    AndroidUtilities.runOnUIThread(() -> {
                        destroyCameraView(true);
                        collageLayoutView.setCameraThumb(getCameraThumb());
                    });
                }
            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent ev) {
                if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getY() < top()) {
                    galleryClosing = true;
                    animateGalleryListView(false);
                    return true;
                }
                return super.dispatchTouchEvent(ev);
            }
        };
        galleryListView.allowSearch(false);
        galleryListView.setMultipleOnClick(collageLayoutView.hasLayout());
        galleryListView.setMaxCount(Math.min(StoryEntry.MAX_ENTRIES, CollageLayout.getMaxCount() - collageLayoutView.getFilledCount()));
        galleryListView.setOnBackClickListener(() -> {
            animateGalleryListView(false);
            lastGallerySelectedAlbum = null;
        });
        galleryListView.setOnSelectListener((entry, blurredBitmap) -> {
            if (entry == null || galleryListViewOpening != null || scrollingY || !isGalleryOpen()) {
                return;
            }

            if (forAddingPart) {
                if (outputEntry == null) {
                    return;
                }
                createPhotoPaintView();
                outputEntry.editedMedia = true;
                if (entry instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) entry;
                    paintView.appearAnimation(paintView.createPhoto(photoEntry.path, false));
                } else if (entry instanceof TLObject) {
                    paintView.appearAnimation(paintView.createPhoto((TLObject) entry, false));
                }
                animateGalleryListView(false);
            } else {
                StoryEntry storyEntry;
                showVideoTimer(false, true);
                modeSwitcherView.switchMode(mode);
                recordControl.startAsVideo(mode == MODE_VIDEO);

                animateGalleryListView(false);
                if (entry instanceof MediaController.PhotoEntry) {
                    MediaController.PhotoEntry photoEntry = (MediaController.PhotoEntry) entry;
                    mode = photoEntry.isVideo && !photoEntry.isLivePhoto() ? MODE_VIDEO : MODE_PHOTO;
                    storyEntry = StoryEntry.fromPhotoEntry(photoEntry);
                    storyEntry.blurredVideoThumb = blurredBitmap;
                    storyEntry.botId = botId;
                    storyEntry.botLang = botLang;
                    storyEntry.setupMatrix();
                    fromGallery = true;

                    if (collageLayoutView.hasLayout()) {
                        outputFile = null;
                        storyEntry.videoVolume = 1.0f;
                        if (collageLayoutView.push(storyEntry)) {
                            outputEntry = StoryEntry.asCollage(collageLayoutView.getLayout(), collageLayoutView.getContent());
//                            StoryPrivacySelector.applySaved(currentAccount, outputEntry);
//                            navigateTo(PAGE_PREVIEW, true);
                        }
                        updateActionBarButtons(true);
                    } else {
                        storyEntry.setupMultipleStoriesSelector();
                        outputEntry = storyEntry;
                        if (entry instanceof MediaController.PhotoEntry) {
                            StoryPrivacySelector.applySaved(currentAccount, outputEntry);
                        }
                        navigateTo(PAGE_PREVIEW, true);
                    }
                } else if (entry instanceof StoryEntry) {
                    storyEntry = (StoryEntry) entry;
                    if (storyEntry.file == null && !storyEntry.isCollage()) {
                        downloadButton.showToast(R.raw.error, "Failed to load draft");
                        MessagesController.getInstance(currentAccount).getStoriesController().getDraftsController().delete(storyEntry);
                        return;
                    }
                    storyEntry.botId = botId;
                    storyEntry.botLang = botLang;
//                    storyEntry.setupMatrix();
                    mode = storyEntry.isVideo ? MODE_VIDEO : MODE_PHOTO;
                    storyEntry.blurredVideoThumb = blurredBitmap;
                    fromGallery = false;

                    collageLayoutView.set(storyEntry, true);
                    outputEntry = storyEntry;
                    if (entry instanceof MediaController.PhotoEntry) {
                        StoryPrivacySelector.applySaved(currentAccount, outputEntry);
                    }
                    navigateTo(PAGE_PREVIEW, true);
                } else {
                    return;
                }
            }

            if (galleryListView != null) {
                lastGalleryScrollPosition = galleryListView.layoutManager.onSaveInstanceState();
                lastGallerySelectedAlbum = galleryListView.getSelectedAlbum();
            }
        });
        galleryListView.setOnSelectMultipleListener((collage, entries, bitmaps) -> {
            if (currentPage != PAGE_CAMERA) return;
            if (entries == null || entries.isEmpty() || galleryListViewOpening != null || scrollingY || !isGalleryOpen()) return;
            StoryRecorder.this.entries = null;
            StoryRecorder.this.selectedEntries = null;
            StoryRecorder.this.selectedEntriesOrder = null;

            if (collage && collageLayoutView.getFilledCount() + entries.size() > collageLayoutView.getTotalCount()) {
                CollageLayout newLayout = CollageLayout.of(collageLayoutView.getFilledCount() + entries.size());
                if (newLayout == null) {
                    collageLayoutView.setLayout(null, true);
                    collageLayoutView.clear(true);
                    collageListView.setSelected(null);
                    if (cameraView != null) {
                        cameraView.recordHevc = !collageLayoutView.hasLayout();
                    }
                    collageListView.setVisible(false, true);
                    updateActionBarButtons(true);
                    return;
                }

                collageLayoutView.setLayout(lastCollageLayout = newLayout, true);
                collageListView.setSelected(newLayout);
                int listPosition = CollageLayout.getLayouts().indexOf(newLayout);
                if (listPosition >= 0)
                    collageListView.listView.scrollToPosition(listPosition);
                if (cameraView != null) {
                    cameraView.recordHevc = !collageLayoutView.hasLayout();
                }
                collageButton.setDrawable(new CollageLayoutButton.CollageLayoutDrawable(newLayout));
                setActionBarButtonVisible(collageRemoveButton, collageListView.isVisible(), true);
                recordControl.setCollageProgress(collageLayoutView.hasLayout() ? collageLayoutView.getFilledProgress() : 0.0f, true);
            }

            fromGallery = true;
            for (int i = 0; i < entries.size(); ++i) {
                MediaController.PhotoEntry photoEntry = entries.get(i);
                StoryEntry storyEntry = StoryEntry.fromPhotoEntry(photoEntry);
                storyEntry.blurredVideoThumb = bitmaps.get(i);
                storyEntry.botId = botId;
                storyEntry.botLang = botLang;
                storyEntry.setupMatrix();
                if (collage) {
                    if (collageLayoutView.push(storyEntry)) {
                        outputEntry = StoryEntry.asCollage(collageLayoutView.getLayout(), collageLayoutView.getContent());
                        break;
                    }
                } else {
                    if (outputEntry == null) {
                        outputEntry = storyEntry;
                    } else {
                        if (StoryRecorder.this.entries == null) {
                            StoryRecorder.this.entries = new ArrayList<>();
                            StoryRecorder.this.entries.add(outputEntry);
                        }
                        if (StoryRecorder.this.entries.size() >= StoryEntry.MAX_ENTRIES) {
                            break;
                        }
                        StoryRecorder.this.entries.add(storyEntry);
                    }
                }
            }
            if (StoryRecorder.this.entries != null) {
                showVideoTimer(false, true);
                modeSwitcherView.switchMode(mode);
                recordControl.startAsVideo(mode == MODE_VIDEO);

                selectedEntries = new ArrayList<>();
                selectedEntriesOrder = new ArrayList<>();
                for (int i = 0; i < StoryRecorder.this.entries.size(); ++i) {
                    selectedEntries.add(i);
                    selectedEntriesOrder.add(i);
                }
                collageLayoutView.set(null, true);
                collageListView.setVisible(false, true);
                updateActionBarButtons(true);
                animateGalleryListView(false);
                navigateTo(PAGE_PREVIEW, true);

                if (storiesSelector != null) {
                    storiesSelector.showHint();
                }

                if (galleryListView != null) {
                    lastGalleryScrollPosition = galleryListView.layoutManager.onSaveInstanceState();
                    lastGallerySelectedAlbum = galleryListView.getSelectedAlbum();
                }
                return;
            }
            if (outputEntry != null) {
                outputEntry.setupMultipleStoriesSelector();
            }
            collageListView.setVisible(false, true);
            updateActionBarButtons(true);

            animateGalleryListView(false);
            if (galleryListView != null) {
                lastGalleryScrollPosition = galleryListView.layoutManager.onSaveInstanceState();
                lastGallerySelectedAlbum = galleryListView.getSelectedAlbum();
            }
        });
        if (lastGalleryScrollPosition != null) {
            galleryListView.layoutManager.onRestoreInstanceState(lastGalleryScrollPosition);
        }
        windowView.addView(galleryListView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.FILL));
    }

    private boolean isGalleryOpen() {
        return !scrollingY && galleryListView != null && galleryListView.getTranslationY() < (windowView.getMeasuredHeight() - (int) (AndroidUtilities.displaySize.y * 0.35f) - (AndroidUtilities.statusBarHeight + ActionBar.getCurrentActionBarHeight()));
    }

    private ValueAnimator galleryOpenCloseAnimator;
    private SpringAnimation galleryOpenCloseSpringAnimator;
    private Boolean galleryListViewOpening;
    private Runnable galleryLayouted;

    private void animateGalleryListView(boolean open) {
        wasGalleryOpen = open;
        if (galleryListViewOpening != null && galleryListViewOpening == open) {
            return;
        }

        if (galleryListView == null) {
            if (open) {
                createGalleryListView();
            }
            if (galleryListView == null) {
                return;
            }
        }

        if (galleryListView.firstLayout) {
            galleryLayouted = () -> animateGalleryListView(open);
            return;
        }

        if (galleryOpenCloseAnimator != null) {
            galleryOpenCloseAnimator.cancel();
            galleryOpenCloseAnimator = null;
        }
        if (galleryOpenCloseSpringAnimator != null) {
            galleryOpenCloseSpringAnimator.cancel();
            galleryOpenCloseSpringAnimator = null;
        }

        if (galleryListView == null) {
            if (open) {
                createGalleryListView();
            }
            if (galleryListView == null) {
                return;
            }
        }
        if (galleryListView != null) {
            galleryListView.ignoreScroll = false;
        }

        if (open && draftSavedHint != null) {
            draftSavedHint.hide(true);
        }

        if (containerView != null) {
            containerView.setImportantForAccessibility(open ? View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS : View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
        }
        galleryListView.setImportantForAccessibility(open ? View.IMPORTANT_FOR_ACCESSIBILITY_AUTO : View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);

        galleryListViewOpening = open;

        float from = galleryListView.getTranslationY();
        float to = open ? 0 : windowView.getHeight() - galleryListView.top() + AndroidUtilities.navigationBarHeight * 2.5f;
        float fulldist = Math.max(1, windowView.getHeight());

        galleryListView.ignoreScroll = !open;

        applyContainerViewTranslation2 = containerViewBackAnimator == null;
        if (open) {
            galleryOpenCloseSpringAnimator = new SpringAnimation(galleryListView, DynamicAnimation.TRANSLATION_Y, to);
            galleryOpenCloseSpringAnimator.getSpring().setDampingRatio(0.75f);
            galleryOpenCloseSpringAnimator.getSpring().setStiffness(350.0f);
            galleryOpenCloseSpringAnimator.addEndListener((a, canceled, c, d) -> {
                if (canceled) {
                    return;
                }
                galleryListView.setTranslationY(to);
                galleryListView.ignoreScroll = false;
                galleryOpenCloseSpringAnimator = null;
                galleryListViewOpening = null;
            });
            galleryOpenCloseSpringAnimator.start();
        } else {
            galleryOpenCloseAnimator = ValueAnimator.ofFloat(from, to);
            galleryOpenCloseAnimator.addUpdateListener(anm -> {
                galleryListView.setTranslationY((float) anm.getAnimatedValue());
            });
            galleryOpenCloseAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    windowView.removeView(galleryListView);
                    galleryListView = null;
                    galleryOpenCloseAnimator = null;
                    galleryListViewOpening = null;
                    captionEdit.keyboardNotifier.ignore(currentPage != PAGE_PREVIEW);
                }
            });
            galleryOpenCloseAnimator.setDuration(450L);
            galleryOpenCloseAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            galleryOpenCloseAnimator.start();
        }

        if (!open && !awaitingPlayer) {
            lastGalleryScrollPosition = null;
        }

        if (!open && currentPage == PAGE_CAMERA && !noCameraPermission) {
            createCameraView();
        }
    }

    private void onNavigateStart(int fromPage, int toPage) {
        if (toPage == PAGE_CAMERA) {
            requestCameraPermission(false);
            recordControl.setVisibility(View.VISIBLE);
            if (recordControl != null) {
                recordControl.stopRecordingLoading(false);
            }
            modeSwitcherView.setVisibility(View.VISIBLE);
            zoomControlView.setVisibility(View.VISIBLE);
            zoomControlView.setAlpha(0);
            videoTimerView.setDuration(0, true);

            if (outputEntry != null) {
                outputEntry.destroy(false);
                outputEntry = null;
            }
            if (collageLayoutView != null) {
                collageLayoutView.clear(true);
                recordControl.setCollageProgress(0.0f, false);
            }
        }
        if (fromPage == PAGE_CAMERA) {
            setCameraFlashModeIcon(null, true);
            saveLastCameraBitmap(() -> collageLayoutView.setCameraThumb(getCameraThumb()));
            if (draftSavedHint != null) {
                draftSavedHint.setVisibility(View.GONE);
            }
            cameraHint.hide();
            if (dualHint != null) {
                dualHint.hide();
            }
        }
        if (toPage == PAGE_PREVIEW || fromPage == PAGE_PREVIEW) {
            downloadButton.setEntry(toPage == PAGE_PREVIEW ? outputEntry : null);
            if (mode == MODE_VIDEO) {
                muteButton.setVisibility(View.VISIBLE);
                setIconMuted(outputEntry != null && outputEntry.muted, false);
                playButton.setVisibility(View.VISIBLE);
                previewView.play(true);
                playButton.drawable.setPause(previewView.isPlaying(), false);
                titleTextView.setRightPadding(AndroidUtilities.dp(144));
            } else if (outputEntry != null && !TextUtils.isEmpty(outputEntry.audioPath)) {
                muteButton.setVisibility(View.GONE);
                playButton.setVisibility(View.VISIBLE);
                playButton.drawable.setPause(true, false);
                titleTextView.setRightPadding(AndroidUtilities.dp(48));
            } else {
                titleTextView.setRightPadding(AndroidUtilities.dp(48));
            }
            downloadButton.setVisibility(View.VISIBLE);
            if (outputEntry != null && outputEntry.isRepostMessage) {
                getThemeButton().setVisibility(View.VISIBLE);
                updateThemeButtonDrawable(false);
            } else if (themeButton != null) {
                themeButton.setVisibility(View.GONE);
            }
//            privacySelector.setVisibility(View.VISIBLE);
            previewButtons.setVisibility(View.VISIBLE);
            previewView.setVisibility(View.VISIBLE);
            captionEdit.setVisibility(isBot() ? View.GONE : View.VISIBLE);
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) videoTimelineContainerView.getLayoutParams();
            lp.bottomMargin = isBot() ? dp(12) : dp(68);
            videoTimelineContainerView.setLayoutParams(lp);
            captionContainer.setVisibility(View.VISIBLE);
            captionContainer.clearFocus();

//            privacySelector.setStoryPeriod(outputEntry == null || !UserConfig.getInstance(currentAccount).isPremium() ? 86400 : outputEntry.period);
            captionEdit.setPeriod(outputEntry == null ? 86400 : outputEntry.period, false);
            captionEdit.setPeriodVisible(!MessagesController.getInstance(currentAccount).premiumFeaturesBlocked() && (outputEntry == null || !outputEntry.isEdit));
            captionEdit.setHasRoundVideo(outputEntry != null && outputEntry.round != null);
            setReply();
            timelineView.setOpen(outputEntry == null || !outputEntry.isCollage() || !outputEntry.hasVideo(), false);
        }
        if (toPage == PAGE_COVER || fromPage == PAGE_COVER) {
            titleTextView.setVisibility(View.VISIBLE);
            coverTimelineView.setVisibility(View.VISIBLE);
            if (outputEntry != null && outputEntry.isEditingCover) {
                titleTextView.setText(getString(R.string.RecorderEditCover));
            }
            captionContainer.setVisibility(View.VISIBLE);
            coverButton.setVisibility(View.VISIBLE);
        }
        if (toPage == PAGE_COVER) {
            titleTextView.setText(getString(R.string.RecorderEditCover));
        }
        if (toPage == PAGE_PREVIEW) {
            videoError = false;
            final boolean isBot = outputEntry != null && outputEntry.botId != 0;
            final boolean isEdit = outputEntry != null && outputEntry.isEdit;
            previewButtons.setShareText(getString(isEdit ? R.string.Done : isBot ? R.string.UploadBotPreview : R.string.Next), !isBot);
            coverTimelineView.setVisibility(View.GONE);
            coverButton.setVisibility(View.GONE);
//            privacySelector.set(outputEntry, false);
            if (!previewAlreadySet) {
                if (outputEntry != null && outputEntry.isRepostMessage) {
                    previewView.preset(outputEntry);
                } else {
                    previewView.set(outputEntry);
                }
            }
            previewAlreadySet = false;
            captionEdit.editText.getEditText().setOnPremiumMenuLockClickListener(MessagesController.getInstance(currentAccount).storyEntitiesAllowed() ? null : () -> {
                BulletinFactory.of(windowView, resourcesProvider).createSimpleBulletin(R.raw.voip_invite, premiumText(getString(R.string.StoryPremiumFormatting))).show(true);
            });
            storiesSelector.setVisibility(entries == null ? View.GONE : View.VISIBLE);
            if (entries != null) {
                storiesSelector.set(entries, selectedEntriesOrder, selectedEntries);
                storiesSelector.setSelected(entries.indexOf(outputEntry));
            }
            timelineView.setMaxCount(!isBot && !isEdit && entries == null && (outputEntry == null || !outputEntry.isCollage() && outputEntry.isVideo) ? 3 : 1);
            if (fromPage != PAGE_COVER) {
                if (outputEntry != null && (outputEntry.isDraft || outputEntry.isEdit || isReposting)) {
                    if (outputEntry.paintFile != null) {
                        destroyPhotoPaintView();
                        createPhotoPaintView();
                        hidePhotoPaintView();
                    }
                    if (outputEntry.isVideo && outputEntry.filterState != null) {
                        VideoEditTextureView textureView = previewView.getTextureView();
                        if (textureView != null) {
                            textureView.setDelegate(eglThread -> {
                                if (eglThread != null && outputEntry != null && outputEntry.filterState != null) {
                                    eglThread.setFilterGLThreadDelegate(FilterShaders.getFilterShadersDelegate(outputEntry.filterState));
                                }
                            });
                        }
                    }
                    captionEdit.setText(outputEntry.caption);
                } else if (fromPage != PAGE_COVER) {
                    captionEdit.clear();
                }
            }
            previewButtons.setButtonVisible(PreviewButtons.BUTTON_ADJUST, outputEntry == null || (!outputEntry.isRepostMessage || outputEntry.isVideo) && !outputEntry.isCollage());
            previewButtons.setButtonVisible(PreviewButtons.BUTTON_CROP, BuildVars.DEBUG_PRIVATE_VERSION && outputEntry != null && !outputEntry.isRepostMessage && !outputEntry.isCollage());
            previewButtons.setShareEnabled(!videoError && !captionEdit.isCaptionOverLimit() && (!MessagesController.getInstance(currentAccount).getStoriesController().hasStoryLimit(getCount()) || (outputEntry != null && (outputEntry.isEdit || outputEntry.botId != 0))));
            muteButton.setImageResource(outputEntry != null && outputEntry.muted ? R.drawable.media_unmute : R.drawable.media_mute);
            previewView.setVisibility(View.VISIBLE);
            timelineView.setVisibility(View.VISIBLE);
            titleTextView.setVisibility(View.VISIBLE);
            titleTextView.setTranslationX(0);
            if (outputEntry != null && outputEntry.botId != 0) {
                titleTextView.setText("");
            } else if (outputEntry != null && outputEntry.isEdit) {
                titleTextView.setText(getString(R.string.RecorderEditStory));
            } else if (outputEntry != null && outputEntry.isRepostMessage) {
                titleTextView.setText(getString(R.string.RecorderRepost));
            } else if (outputEntry != null && outputEntry.isRepost) {
                SpannableStringBuilder title = new SpannableStringBuilder();
                AvatarSpan span = new AvatarSpan(titleTextView, currentAccount, 32);
                titleTextView.setTranslationX(-dp(6));
                SpannableString avatar = new SpannableString("a");
                avatar.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (outputEntry.repostPeer instanceof TLRPC.TL_peerUser) {
                    TLRPC.User user = MessagesController.getInstance(currentAccount).getUser(outputEntry.repostPeer.user_id);
                    span.setUser(user);
                    title.append(avatar).append("  ");
                    title.append(UserObject.getUserName(user));
                } else {
                    TLRPC.Chat chat = MessagesController.getInstance(currentAccount).getChat(-DialogObject.getPeerDialogId(outputEntry.repostPeer));
                    span.setChat(chat);
                    title.append(avatar).append("  ");
                    title.append(chat != null ? chat.title : "");
                }
                titleTextView.setText(title);
            } else {
                titleTextView.setText(getString(R.string.RecorderNewStory));
            }

//            MediaDataController.getInstance(currentAccount).checkStickers(MediaDataController.TYPE_EMOJIPACKS);
//            MediaDataController.getInstance(currentAccount).checkFeaturedEmoji();
        }
        if (fromPage == PAGE_PREVIEW) {
//            privacySelectorHint.hide();
            captionEdit.hidePeriodPopup();
            muteHint.hide();
            storiesSelector.onBackPressed();
        }
        if (toPage == PAGE_COVER) {
            if (outputEntry != null) {
                if (outputEntry.cover < 0) {
                    outputEntry.cover = 0;
                }
                coverValue = outputEntry.cover;
                long duration = previewView.getDuration() < 100 ? outputEntry.duration : previewView.getDuration();
                if (outputEntry.duration <= 0) {
                    outputEntry.duration = duration;
                }
                coverTimelineView.setVideo(false, outputEntry.getOriginalFile().getAbsolutePath(), outputEntry.duration, outputEntry.videoVolume);
                coverTimelineView.setCoverVideo((long) (outputEntry.left * duration), (long) (outputEntry.right * duration));
                final Utilities.Callback2<Boolean, Float> videoLeftSet = (start, left) -> {
                    final long _duration = previewView.getDuration() < 100 ? outputEntry.duration : previewView.getDuration();
                    coverValue = (long) ((left + 0.04f * (left / (1f - 0.04f))) * (outputEntry.right - outputEntry.left) * _duration);
                    previewView.seekTo(coverValue = (long) (outputEntry.left * _duration + coverValue), false);
                    if (paintView != null) {
                        paintView.setCoverTime(coverValue);
                    }
                    if (outputEntry != null && outputEntry.isEdit) {
                        outputEntry.editedMedia = true;
                    }
                };
                coverTimelineView.setDelegate(new TimelineView.TimelineDelegate() {
                    @Override
                    public void onVideoLeftChange(boolean released, float left) {
                        videoLeftSet.run(false, left);
                    }
                });
                float left = (float) coverValue / Math.max(1, duration) * (1f - 0.04f);
                coverTimelineView.setVideoLeft(left);
                coverTimelineView.setVideoRight(left + 0.04f);
                videoLeftSet.run(true, left);
            }
        }
        if (photoFilterEnhanceView != null) {
            photoFilterEnhanceView.setAllowTouch(false);
        }
        if (savedDualHint != null) {
            savedDualHint.hide();
        }
        Bulletin.hideVisible();

        if (captionEdit != null) {
            captionEdit.closeKeyboard();
            captionEdit.ignoreTouches = true;
        }
        if (previewView != null) {
            previewView.updatePauseReason(8, toPage != PAGE_PREVIEW);
        }
        if (paintView != null) {
            paintView.setCoverPreview(toPage != PAGE_PREVIEW);
        }
        if (removeCollageHint != null) {
            removeCollageHint.hide();
        }
        collageLayoutView.setPreview(toPage == PAGE_PREVIEW && collageLayoutView.hasLayout());
    }

    private void onNavigateEnd(int fromPage, int toPage) {
        if (fromPage == PAGE_CAMERA) {
            destroyCameraView(false);
            recordControl.setVisibility(View.GONE);
            zoomControlView.setVisibility(View.GONE);
            modeSwitcherView.setVisibility(View.GONE);
//            dualButton.setVisibility(View.GONE);
            animateRecording(false, false);
            setAwakeLock(false);
        }
        if (fromPage == PAGE_COVER) {
            coverTimelineView.setVisibility(View.GONE);
            captionContainer.setVisibility(toPage == PAGE_PREVIEW ? View.VISIBLE : View.GONE);
            captionEdit.setVisibility(View.GONE);
            coverButton.setVisibility(View.GONE);
        }
        if (fromPage == PAGE_PREVIEW) {
            previewButtons.setVisibility(View.GONE);
            captionContainer.setVisibility(toPage == PAGE_COVER ? View.VISIBLE : View.GONE);
            muteButton.setVisibility(View.GONE);
            playButton.setVisibility(View.GONE);
            downloadButton.setVisibility(View.GONE);
            if (themeButton != null) {
                themeButton.setVisibility(View.GONE);
            }
//            privacySelector.setVisibility(View.GONE);
            previewView.setVisibility(toPage == PAGE_COVER ? View.VISIBLE : View.GONE);
            timelineView.setVisibility(View.GONE);
            if (toPage != PAGE_COVER) {
                destroyPhotoPaintView();
                destroyPhotoFilterView();
            }
            titleTextView.setVisibility(toPage == PAGE_COVER ? View.VISIBLE : View.GONE);
            destroyGalleryListView();
            trash.setAlpha(0f);
            trash.setVisibility(View.GONE);
            videoTimeView.setVisibility(View.GONE);

            entries = null;
            selectedEntries = null;
            selectedEntriesOrder = null;
        }
        if (toPage == PAGE_PREVIEW) {
            if (outputEntry == null || !outputEntry.isRepost) {
                createPhotoPaintView();
                hidePhotoPaintView();
            }
            if (outputEntry == null || !outputEntry.isRepost && !outputEntry.isRepostMessage) {
//                createFilterPhotoView();
            }
            if (photoFilterEnhanceView != null) {
                photoFilterEnhanceView.setAllowTouch(false);
            }
            previewView.updatePauseReason(2, false);
            previewView.updatePauseReason(3, false);
            previewView.updatePauseReason(4, false);
            previewView.updatePauseReason(5, false);
            previewView.updatePauseReason(7, false);
            videoTimeView.setVisibility(outputEntry != null && outputEntry.duration >= 30_000 ? View.VISIBLE : View.GONE);
            captionContainer.setAlpha(1f);
            captionContainer.setTranslationY(0);
            captionEdit.setVisibility(outputEntry != null && outputEntry.botId != 0 ? View.GONE : View.VISIBLE);
        }
        if (toPage == PAGE_CAMERA && showSavedDraftHint) {
            getDraftSavedHint().setVisibility(View.VISIBLE);
            getDraftSavedHint().show();
            recordControl.updateGalleryImage();
        }
        showSavedDraftHint = false;

        if (photoFilterEnhanceView != null) {
            photoFilterEnhanceView.setAllowTouch(toPage == PAGE_PREVIEW && (currentEditMode == EDIT_MODE_NONE || currentEditMode == EDIT_MODE_FILTER));
        }
//        if (toPage == PAGE_PREVIEW && !privacySelectorHintOpened) {
//            privacySelectorHint.show(false);
//            privacySelectorHintOpened = true;
//        }
        if (captionEdit != null) {
            captionEdit.ignoreTouches = toPage != PAGE_PREVIEW;
        }

        if (toPage == PAGE_PREVIEW) {
            MediaDataController.getInstance(currentAccount).checkStickers(MediaDataController.TYPE_IMAGE);
            MediaDataController.getInstance(currentAccount).loadRecents(MediaDataController.TYPE_IMAGE, false, true, false);
            MediaDataController.getInstance(currentAccount).loadRecents(MediaDataController.TYPE_FAVE, false, true, false);
            MessagesController.getInstance(currentAccount).getStoriesController().loadBlocklistAtFirst();
            MessagesController.getInstance(currentAccount).getStoriesController().loadSendAs();
        }
    }

    private AnimatorSet editModeAnimator;
    public void switchToEditMode(int editMode, boolean animated) {
        switchToEditMode(editMode, false, animated);
    }
    public void switchToEditMode(int editMode, boolean force, boolean animated) {
        if (currentEditMode == editMode && !force) {
            return;
        }
        if (editMode != EDIT_MODE_NONE && (captionEdit != null && captionEdit.isRecording())) {
            return;
        }

        final int oldEditMode = currentEditMode;
        currentEditMode = editMode;

        if (editModeAnimator != null) {
            editModeAnimator.cancel();
            editModeAnimator = null;
        }

        if (oldEditMode != editMode) {
            onSwitchEditModeStart(oldEditMode, editMode);
        }
        previewButtons.appear((editMode == EDIT_MODE_NONE || editMode == EDIT_MODE_TIMELINE) && openProgress > 0, animated);

        ArrayList<Animator> animators = new ArrayList<>();

        boolean delay = photoFilterView == null && editMode == EDIT_MODE_FILTER;
        if (editMode == EDIT_MODE_FILTER) {
            createFilterPhotoView();
//            animatePhotoFilterTexture(true, animated);
            previewTouchable = photoFilterView;
            View toolsView = photoFilterView != null ? photoFilterView.getToolsView() : null;
            if (toolsView != null) {
                toolsView.setAlpha(0f);
                toolsView.setVisibility(View.VISIBLE);
                animators.add(ObjectAnimator.ofFloat(toolsView, View.TRANSLATION_Y, 0));
                animators.add(ObjectAnimator.ofFloat(toolsView, View.ALPHA, 1));
            }
        } else if (oldEditMode == EDIT_MODE_FILTER && photoFilterView != null) {
            previewTouchable = null;
//            animatePhotoFilterTexture(false, animated);
            animators.add(ObjectAnimator.ofFloat(photoFilterView.getToolsView(), View.TRANSLATION_Y, dp(186 + 40)));
            animators.add(ObjectAnimator.ofFloat(photoFilterView.getToolsView(), View.ALPHA, 0));
        }

        if (editMode == EDIT_MODE_PAINT) {
            createPhotoPaintView();
            previewTouchable = paintView;
            animators.add(ObjectAnimator.ofFloat(backButton, View.ALPHA, 0));
            animators.add(ObjectAnimator.ofFloat(paintView.getTopLayout(), View.ALPHA, 0, 1));
            animators.add(ObjectAnimator.ofFloat(paintView.getTopLayout(), View.TRANSLATION_Y, -dp(16), 0));
            animators.add(ObjectAnimator.ofFloat(paintView.getBottomLayout(), View.ALPHA, 0, 1));
            animators.add(ObjectAnimator.ofFloat(paintView.getBottomLayout(), View.TRANSLATION_Y, dp(48), 0));
            animators.add(ObjectAnimator.ofFloat(paintView.getWeightChooserView(), View.TRANSLATION_X, -dp(32), 0));
        } else if (oldEditMode == EDIT_MODE_PAINT && paintView != null) {
            previewTouchable = null;
            animators.add(ObjectAnimator.ofFloat(backButton, View.ALPHA, 1));
            animators.add(ObjectAnimator.ofFloat(paintView.getTopLayout(), View.ALPHA, 0));
            animators.add(ObjectAnimator.ofFloat(paintView.getTopLayout(), View.TRANSLATION_Y, -dp(16)));
            animators.add(ObjectAnimator.ofFloat(paintView.getBottomLayout(), View.ALPHA, 0));
            animators.add(ObjectAnimator.ofFloat(paintView.getBottomLayout(), View.TRANSLATION_Y, dp(48)));
            animators.add(ObjectAnimator.ofFloat(paintView.getWeightChooserView(), View.TRANSLATION_X, -dp(32)));
        }

        if (cropEditor != null) {
            if (editMode == EDIT_MODE_CROP) {
                animators.add(ObjectAnimator.ofFloat(cropEditor.wheel, View.ALPHA, 0, 1));
                animators.add(ObjectAnimator.ofFloat(cropEditor.wheel, View.TRANSLATION_Y, dp(52), 0));
                animators.add(ObjectAnimator.ofFloat(cropEditor.buttonsLayout, View.ALPHA, 0, 1));
                animators.add(ObjectAnimator.ofFloat(cropEditor.buttonsLayout, View.TRANSLATION_Y, dp(52), 0));
                final ValueAnimator valueAnimator = ValueAnimator.ofFloat(cropEditor.getAppearProgress(), 1.0f);
                valueAnimator.addUpdateListener(a -> cropEditor.setAppearProgress((float) a.getAnimatedValue()));
                animators.add(valueAnimator);
            } else if (oldEditMode == EDIT_MODE_CROP) {
                animators.add(ObjectAnimator.ofFloat(cropEditor.wheel, View.ALPHA, 1, 0));
                animators.add(ObjectAnimator.ofFloat(cropEditor.wheel, View.TRANSLATION_Y, 0, dp(52)));
                animators.add(ObjectAnimator.ofFloat(cropEditor.buttonsLayout, View.ALPHA, 1, 0));
                animators.add(ObjectAnimator.ofFloat(cropEditor.buttonsLayout, View.TRANSLATION_Y, 0, dp(52)));
                final ValueAnimator valueAnimator = ValueAnimator.ofFloat(cropEditor.getAppearProgress(), 0.0f);
                valueAnimator.addUpdateListener(a -> cropEditor.setAppearProgress((float) a.getAnimatedValue()));
                animators.add(valueAnimator);
            }
        }

        if (cropInlineEditor != null) {
            if (editMode == EDIT_MODE_CROP_INLINE) {
                animators.add(ObjectAnimator.ofFloat(cropInlineEditor.wheel, View.ALPHA, 0, 1));
                animators.add(ObjectAnimator.ofFloat(cropInlineEditor.wheel, View.TRANSLATION_Y, dp(52), 0));
                animators.add(ObjectAnimator.ofFloat(cropInlineEditor.buttonsLayout, View.ALPHA, 0, 1));
                animators.add(ObjectAnimator.ofFloat(cropInlineEditor.buttonsLayout, View.TRANSLATION_Y, dp(52), 0));
                final ValueAnimator valueAnimator = ValueAnimator.ofFloat(cropInlineEditor.getAppearProgress(), 1.0f);
                valueAnimator.addUpdateListener(a -> cropInlineEditor.setAppearProgress((float) a.getAnimatedValue()));
                animators.add(valueAnimator);
            } else if (oldEditMode == EDIT_MODE_CROP_INLINE) {
                animators.add(ObjectAnimator.ofFloat(cropInlineEditor.wheel, View.ALPHA, 1, 0));
                animators.add(ObjectAnimator.ofFloat(cropInlineEditor.wheel, View.TRANSLATION_Y, 0, dp(52)));
                animators.add(ObjectAnimator.ofFloat(cropInlineEditor.buttonsLayout, View.ALPHA, 1, 0));
                animators.add(ObjectAnimator.ofFloat(cropInlineEditor.buttonsLayout, View.TRANSLATION_Y, 0, dp(52)));
                final ValueAnimator valueAnimator = ValueAnimator.ofFloat(cropInlineEditor.getAppearProgress(), 0.0f);
                valueAnimator.addUpdateListener(a -> cropInlineEditor.setAppearProgress((float) a.getAnimatedValue()));
                animators.add(valueAnimator);
            }
        }

        animators.add(ObjectAnimator.ofFloat(muteButton, View.ALPHA, (editMode == EDIT_MODE_NONE || editMode == EDIT_MODE_TIMELINE) && mode == MODE_VIDEO ? 1 : 0));
        animators.add(ObjectAnimator.ofFloat(playButton, View.ALPHA, (editMode == EDIT_MODE_NONE || editMode == EDIT_MODE_TIMELINE) && (mode == MODE_VIDEO || outputEntry != null && !TextUtils.isEmpty(outputEntry.audioPath)) ? 1 : 0));
        animators.add(ObjectAnimator.ofFloat(downloadButton, View.ALPHA, (editMode == EDIT_MODE_NONE || editMode == EDIT_MODE_TIMELINE) ? 1 : 0));
        if (themeButton != null) {
            animators.add(ObjectAnimator.ofFloat(themeButton, View.ALPHA, (editMode == EDIT_MODE_NONE || editMode == EDIT_MODE_TIMELINE) && (outputEntry != null && outputEntry.isRepostMessage) ? 1f : 0));
        }
        animators.add(ObjectAnimator.ofFloat(titleTextView, View.ALPHA, (currentPage == PAGE_PREVIEW || currentPage == PAGE_COVER) && editMode == EDIT_MODE_NONE ? 1f : 0f));

        int rightMargin = 0;
        int bottomMargin = 0;
        if (editMode == EDIT_MODE_FILTER) {
            previewContainer.setPivotY(previewContainer.getMeasuredHeight() * .2f);
            bottomMargin = dp(164);
        } else if (editMode == EDIT_MODE_PAINT) {
            previewContainer.setPivotY(previewContainer.getMeasuredHeight() * .6f);
            bottomMargin = dp(40);
        } else if (editMode == EDIT_MODE_TIMELINE) {
            previewContainer.setPivotY(0);
            bottomMargin = timelineView.getContentHeight() + dp(8);
//            rightMargin = dp(46);
        }

        float scale = 1f;
        if (bottomMargin > 0) {
            final int bottomPivot = previewContainer.getHeight() - (int) previewContainer.getPivotY();
            scale = (float) (bottomPivot - bottomMargin) / bottomPivot;
        }
        if (rightMargin > 0) {
            final int rightPivot = previewContainer.getWidth() - (int) previewContainer.getPivotX();
            scale = Math.min(scale, (float) (rightPivot - rightMargin) / rightPivot);
        }

        animators.add(ObjectAnimator.ofFloat(previewContainer, View.SCALE_X, scale));
        animators.add(ObjectAnimator.ofFloat(previewContainer, View.SCALE_Y, scale));
        if (editMode == EDIT_MODE_NONE) {
            animators.add(ObjectAnimator.ofFloat(previewContainer, View.TRANSLATION_Y, 0));
        }

        if (photoFilterViewCurvesControl != null) {
            animators.add(ObjectAnimator.ofFloat(photoFilterViewCurvesControl, View.ALPHA, editMode == EDIT_MODE_FILTER ? 1f : 0));
        }
        if (photoFilterViewBlurControl != null) {
            animators.add(ObjectAnimator.ofFloat(photoFilterViewBlurControl, View.ALPHA, editMode == EDIT_MODE_FILTER ? 1f : 0));
        }

        animators.add(ObjectAnimator.ofFloat(captionEdit, View.ALPHA, editMode == EDIT_MODE_NONE ? 1f : 0));
        animators.add(ObjectAnimator.ofFloat(captionContainer, View.ALPHA, editMode == EDIT_MODE_NONE || editMode == EDIT_MODE_TIMELINE ? 1f : 0));
        animators.add(ObjectAnimator.ofFloat(captionContainer, View.TRANSLATION_Y, editMode == EDIT_MODE_NONE || editMode == EDIT_MODE_TIMELINE ? 0 : dp(120)));
        animators.add(ObjectAnimator.ofFloat(videoTimelineContainerView, View.ALPHA, editMode == EDIT_MODE_NONE || editMode == EDIT_MODE_TIMELINE ? 1f : 0));
        animators.add(ObjectAnimator.ofFloat(videoTimelineContainerView, View.TRANSLATION_Y, editMode == EDIT_MODE_TIMELINE ? dp(68) : -(captionEdit.getEditTextHeight() + AndroidUtilities.dp(12)) + AndroidUtilities.dp(64)));
        actionBarButtons.setPivotX(actionBarButtons.getMeasuredWidth() - dp(46 / 2.0f));
        animators.add(ObjectAnimator.ofFloat(actionBarButtons, View.ROTATION, editMode == EDIT_MODE_TIMELINE ? -90 : 0));
        animators.add(ObjectAnimator.ofFloat(playButton, View.ROTATION, editMode == EDIT_MODE_TIMELINE ? 90 : 0));
        animators.add(ObjectAnimator.ofFloat(muteButton, View.ROTATION, editMode == EDIT_MODE_TIMELINE ? 90 : 0));
        animators.add(ObjectAnimator.ofFloat(downloadButton, View.ROTATION, editMode == EDIT_MODE_TIMELINE ? 90 : 0));
        if (themeButton != null) {
            animators.add(ObjectAnimator.ofFloat(themeButton, View.ROTATION, editMode == EDIT_MODE_TIMELINE ? 90 : 0));
        }
        if (blurManager.hasRenderNode()) {
            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                    captionEdit.invalidateBlur();
                }
            });
            animators.add(valueAnimator);
        }

        if (timelineView != null) {
            timelineView.setOpen(outputEntry == null || !outputEntry.isCollage() || !outputEntry.hasVideo() || editMode == EDIT_MODE_TIMELINE, animated);
        }
        if (animated) {
            editModeAnimator = new AnimatorSet();
            editModeAnimator.playTogether(animators);
            editModeAnimator.setDuration(320);
            editModeAnimator.setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT);
            editModeAnimator.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (oldEditMode != editMode) {
                        onSwitchEditModeEnd(oldEditMode, editMode);
                    }
                }
            });
            if (delay) {
                editModeAnimator.setStartDelay(120L);
            }
            editModeAnimator.start();
        } else {
            for (int i = 0; i < animators.size(); ++i) {
                Animator a = animators.get(i);
                a.setDuration(1);
                a.start();
            }
            if (oldEditMode != editMode) {
                onSwitchEditModeEnd(oldEditMode, editMode);
            }
        }
    }

    private void hidePhotoPaintView() {
        if (paintView == null) {
            return;
        }
        previewTouchable = null;
        paintView.getTopLayout().setAlpha(0f);
        paintView.getTopLayout().setTranslationY(-AndroidUtilities.dp(16));
        paintView.getBottomLayout().setAlpha(0f);
        paintView.getBottomLayout().setTranslationY(AndroidUtilities.dp(48));
        paintView.getWeightChooserView().setTranslationX(-AndroidUtilities.dp(32));
        paintView.setVisibility(View.GONE);
//        paintView.keyboardNotifier.ignore(true);
    }

    private void createPhotoPaintView() {
        if (paintView != null) {
            return;
        }
        Pair<Integer, Integer> size = previewView.getPaintSize();

        Bitmap paintViewBitmap = null;
        if (outputEntry != null && (outputEntry.isDraft || outputEntry.isEdit || entries != null) && outputEntry.paintFile != null) {
            paintViewBitmap = BitmapFactory.decodeFile(outputEntry.paintFile.getPath());
        }
        if (paintViewBitmap == null) {
            paintViewBitmap = Bitmap.createBitmap(size.first, size.second, Bitmap.Config.ARGB_8888);
        }

        boolean hasBlur = false;
        Bitmap paintViewBlurBitmap = null;
        if (outputEntry != null && (outputEntry.isDraft || outputEntry.isEdit || entries != null) && outputEntry.paintBlurFile != null) {
            paintViewBlurBitmap = BitmapFactory.decodeFile(outputEntry.paintBlurFile.getPath());
            if (paintViewBlurBitmap != null) {
                hasBlur = true;
            }
        }
        if (paintViewBlurBitmap == null) {
            paintViewBlurBitmap = Bitmap.createBitmap(size.first, size.second, Bitmap.Config.ARGB_8888);
        }

        int w = previewContainer.getMeasuredWidth(), h = previewContainer.getMeasuredHeight();
        paintView = new PaintView(
            activity,
            outputEntry != null && !outputEntry.fileDeletable,
            outputEntry == null ? null : outputEntry.file,
            outputEntry != null && outputEntry.isVideo,
            outputEntry != null && outputEntry.botId != 0,
            windowView,
            activity,
            currentAccount,
            paintViewBitmap,
            paintViewBlurBitmap,
            null,
            previewView.getOrientation(),
            outputEntry == null ? null : outputEntry.mediaEntities,
            outputEntry,
            w, h,
            new MediaController.CropState(),
            null,
            blurManager,
            resourcesProvider,
            videoTextureHolder,
            previewView
        ) {
            @Override
            public void onEntityDraggedTop(boolean value) {
                previewHighlight.show(true, value, actionBarContainer);
            }

            @Override
            protected void onGalleryClick() {
                captionEdit.keyboardNotifier.ignore(true);
                destroyGalleryListView();
                createGalleryListView(true);
                animateGalleryListView(true);
            }

            @Override
            public void onEntityDraggedBottom(boolean value) {
                previewHighlight.updateCaption(captionEdit.getText());
                previewHighlight.show(false, value && multitouch, null);
            }

            @Override
            public void onEntityDragEnd(boolean delete) {
                if (!isEntityDeletable()) {
                    delete = false;
                }
                captionEdit.clearAnimation();
                captionEdit.animate().alpha(currentEditMode == EDIT_MODE_NONE ? 1f : 0).setDuration(180).setInterpolator(CubicBezierInterpolator.EASE_OUT).start();
                videoTimelineContainerView.clearAnimation();
                videoTimelineContainerView.animate().alpha(currentEditMode == EDIT_MODE_NONE || currentEditMode == EDIT_MODE_TIMELINE ? 1f : 0).setDuration(180).setInterpolator(CubicBezierInterpolator.EASE_OUT).start();
                showTrash(false, delete);
                if (delete) {
                    removeCurrentEntity();
                }
                super.onEntityDragEnd(delete);
                multitouch = false;
            }

            @Override
            public void onEntityDragStart() {
                paintView.showReactionsLayout(false);
                captionEdit.clearAnimation();
                captionEdit.animate().alpha(0f).setDuration(180).setInterpolator(CubicBezierInterpolator.EASE_OUT).start();
                if (currentEditMode != EDIT_MODE_TIMELINE) {
                    videoTimelineContainerView.clearAnimation();
                    videoTimelineContainerView.animate().alpha(0f).setDuration(180).setInterpolator(CubicBezierInterpolator.EASE_OUT).start();
                }
                showTrash(isEntityDeletable(), false);
            }

            public void showTrash(boolean show, boolean delete) {
                if (show) {
                    trash.setVisibility(View.VISIBLE);
                    trash.setAlpha(0f);
                    trash.clearAnimation();
                    trash.animate().alpha(1f).setDuration(180).setInterpolator(CubicBezierInterpolator.EASE_OUT).start();
                } else {
                    trash.onDragInfo(false, delete);
                    trash.clearAnimation();
                    trash.animate().alpha(0f).withEndAction(() -> {
                        trash.setVisibility(View.GONE);
                    }).setDuration(180).setInterpolator(CubicBezierInterpolator.EASE_OUT).setStartDelay(delete ? 500 : 0).start();
                }
            }

            private boolean multitouch;

            @Override
            public void onEntityDragMultitouchStart() {
                multitouch = true;
                paintView.showReactionsLayout(false);
                showTrash(false, false);
            }

            @Override
            public void onEntityDragMultitouchEnd() {
                multitouch = false;
                showTrash(isEntityDeletable(), false);
                previewHighlight.show(false, false, null);
            }

            @Override
            public void onEntityDragTrash(boolean enter) {
                trash.onDragInfo(enter, false);
            }

            @Override
            protected void editSelectedTextEntity() {
                captionEdit.editText.closeKeyboard();
                switchToEditMode(EDIT_MODE_PAINT, true);
                super.editSelectedTextEntity();
            }

            @Override
            public void dismiss() {
                captionEdit.editText.closeKeyboard();
                switchToEditMode(EDIT_MODE_NONE, true);
            }

            @Override
            protected void onOpenCloseStickersAlert(boolean open) {
                if (previewView != null) {
                    previewView.updatePauseReason(6, open);
                    if (playButton != null) {
                        playButton.drawable.setPause(previewView.isPlaying(), true);
                    }
                }
                if (captionEdit != null) {
                    captionEdit.ignoreTouches = open;
                    captionEdit.keyboardNotifier.ignore(open);
                }
            }

            @Override
            protected void onAudioSelect(MessageObject messageObject) {
                previewView.setupAudio(messageObject, true);
                if (outputEntry != null && mode != MODE_VIDEO) {
                    boolean appear = !TextUtils.isEmpty(outputEntry.audioPath);
                    playButton.drawable.setPause(!previewView.isPlaying(), false);
                    playButton.setVisibility(View.VISIBLE);
                    playButton.animate().alpha(appear ? 1 : 0).withEndAction(() -> {
                        if (!appear) {
                            playButton.setVisibility(View.GONE);
                        }
                    }).start();
                }
                switchToEditMode(collageLayoutView.hasLayout() && collageLayoutView.hasVideo() && !TextUtils.isEmpty(outputEntry.audioPath) ? EDIT_MODE_TIMELINE : EDIT_MODE_NONE, true, true);
            }

            @Override
            public void onEntityHandleTouched() {
                paintView.showReactionsLayout(false);
            }

            @Override
            protected boolean checkAudioPermission(Runnable granted) {
                if (activity == null) {
                    return true;
                }
                if (Build.VERSION.SDK_INT >= 33) {
                    if (activity.checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                        activity.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_AUDIO}, 115);
                        audioGrantedCallback = granted;
                        return false;
                    }
                } else if (Build.VERSION.SDK_INT >= 23 && activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 115);
                    audioGrantedCallback = granted;
                    return false;
                }
                return true;
            }

            @Override
            public void onCreateRound(RoundView roundView) {
                if (previewView != null) {
                    previewView.attachRoundView(roundView);
                }
                if (captionEdit != null) {
                    captionEdit.setHasRoundVideo(true);
                }
            }

            @Override
            public void onTryDeleteRound() {
                if (captionEdit != null) {
                    captionEdit.showRemoveRoundAlert();
                }
            }

            @Override
            public void onDeleteRound() {
                if (previewView != null) {
                    previewView.setupRound(null, null, true);
                }
                if (paintView != null) {
                    paintView.deleteRound();
                }
                if (captionEdit != null) {
                    captionEdit.setHasRoundVideo(false);
                }
                if (outputEntry != null) {
                    if (outputEntry.round != null) {
                        try {
                            outputEntry.round.delete();
                        } catch (Exception ignore) {}
                        outputEntry.round = null;
                    }
                    if (outputEntry.roundThumb != null) {
                        try {
                            new File(outputEntry.roundThumb).delete();
                        } catch (Exception ignore) {}
                        outputEntry.roundThumb = null;
                    }
                }
            }

            @Override
            public void onSwitchSegmentedAnimation(PhotoView photoView) {
                if (photoView == null) {
                    return;
                }
                ThanosEffect thanosEffect = getThanosEffect();
                if (thanosEffect == null) {
                    photoView.onSwitchSegmentedAnimationStarted(false);
                    return;
                }
                Bitmap bitmap = photoView.getSegmentedOutBitmap();
                if (bitmap == null) {
                    photoView.onSwitchSegmentedAnimationStarted(false);
                    return;
                }
                Matrix matrix = new Matrix();
                float w = photoView.getWidth(), h = photoView.getHeight();
                float tx = 0, ty = 0;
                if (photoView.getRotation() != 0) {
                    final float bw = bitmap.getWidth();
                    final float bh = bitmap.getHeight();
                    final float r = (float) Math.sqrt((bw / 2f) * (bw / 2f) + (bh / 2f) * (bh / 2f));
                    final float d = 2 * r;
                    Bitmap newBitmap = Bitmap.createBitmap((int) d, (int) d, Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(newBitmap);
                    canvas.save();
                    canvas.rotate(photoView.getRotation(), r, r);
                    canvas.drawBitmap(bitmap, (d - bw) / 2, (d - bh) / 2, null);
                    bitmap.recycle();
                    bitmap = newBitmap;

                    final float pd = 2 * (float) Math.sqrt((w / 2f) * (w / 2f) + (h / 2f) * (h / 2f));
                    tx = -(pd - w) / 2;
                    ty = -(pd - h) / 2;
                    w = pd;
                    h = pd;
                }
                matrix.postScale(w, h);
                matrix.postScale(photoView.getScaleX(), photoView.getScaleY(), w / 2f, h / 2f);
                matrix.postTranslate(containerView.getX() + previewContainer.getX() + photoView.getX() + tx, containerView.getY() + previewContainer.getY() + photoView.getY() + ty);
                thanosEffect.animate(matrix, bitmap, () -> {
                    photoView.onSwitchSegmentedAnimationStarted(true);
                }, () -> {});
            }

            @Override
            public void onSelectRound(RoundView roundView) {
                if (timelineView != null) {
                    timelineView.selectRound(true);
                }
            }

            @Override
            public void onDeselectRound(RoundView roundView) {
                if (timelineView != null) {
                    timelineView.selectRound(false);
                }
            }

            @Override
            protected void onPhotoEntityCropClick(PhotoView photoView) {
                createCropInlineEditor();
                cropInlineEditor.set(photoView);
                switchToEditMode(EDIT_MODE_CROP_INLINE, true);
            }
        };
        paintView.setHasAudio(outputEntry != null && outputEntry.audioPath != null);
        paintView.setBlurManager(blurManager);
        containerView.addView(paintView);
        paintViewRenderView = paintView.getRenderView();
        if (paintViewRenderView != null) {
            paintViewRenderView.getPainting().hasBlur = hasBlur;
            previewContainer.addView(paintViewRenderView);
        }
        paintViewRenderInputView = paintView.getRenderInputView();
        if (paintViewRenderInputView != null) {
            previewContainer.addView(paintViewRenderInputView);
        }
        paintViewTextDim = paintView.getTextDimView();
        if (paintViewTextDim != null) {
            previewContainer.addView(paintViewTextDim);
        }
        paintViewEntitiesView = paintView.getEntitiesView();
        if (paintViewEntitiesView != null) {
            previewContainer.addView(paintViewEntitiesView);
        }
        paintViewSelectionContainerView = paintView.getSelectionEntitiesView();
        if (paintViewSelectionContainerView != null) {
            previewContainer.addView(paintViewSelectionContainerView);
        }
        orderPreviewViews();
        paintView.setOnDoneButtonClickedListener(() -> {
            switchToEditMode(EDIT_MODE_NONE, true);
        });
        paintView.setOnCancelButtonClickedListener(() -> {
            switchToEditMode(EDIT_MODE_NONE, true);
        });
        paintView.init();
    }

    private void orderPreviewViews() {
        if (paintViewRenderView != null) {
            paintViewRenderView.bringToFront();
        }
        if (paintViewRenderInputView != null) {
            paintViewRenderInputView.bringToFront();
        }
        if (paintViewTextDim != null) {
            paintViewTextDim.bringToFront();
        }
        if (paintViewEntitiesView != null) {
            paintViewEntitiesView.bringToFront();
        }
        if (paintViewSelectionContainerView != null) {
            paintViewSelectionContainerView.bringToFront();
        }
        if (trash != null) {
            trash.bringToFront();
        }
        if (photoFilterEnhanceView != null) {
            photoFilterEnhanceView.bringToFront();
        }
        if (photoFilterViewBlurControl != null) {
            photoFilterViewBlurControl.bringToFront();
        }
        if (photoFilterViewCurvesControl != null) {
            photoFilterViewCurvesControl.bringToFront();
        }
        if (previewHighlight != null) {
            previewHighlight.bringToFront();
        }
        if (currentRoundRecorder != null) {
            currentRoundRecorder.bringToFront();
        }
    }

    private void destroyPhotoPaintView() {
        if (paintView == null) {
            return;
        }
        paintView.onCleanupEntities();

        paintView.shutdown();
        containerView.removeView(paintView);
        paintView = null;
        if (paintViewRenderView != null) {
            previewContainer.removeView(paintViewRenderView);
            paintViewRenderView = null;
        }
        if (paintViewTextDim != null) {
            previewContainer.removeView(paintViewTextDim);
            paintViewTextDim = null;
        }
        if (paintViewRenderInputView != null) {
            previewContainer.removeView(paintViewRenderInputView);
            paintViewRenderInputView = null;
        }
        if (paintViewEntitiesView != null) {
            previewContainer.removeView(paintViewEntitiesView);
            paintViewEntitiesView = null;
        }
        if (paintViewSelectionContainerView != null) {
            previewContainer.removeView(paintViewSelectionContainerView);
            paintViewSelectionContainerView = null;
        }
    }

    private boolean isBot() {
        return outputEntry != null && outputEntry.botId != 0 || botId != 0;
    }

    private void onSwitchEditModeStart(int fromMode, int toMode) {
        if (toMode == EDIT_MODE_NONE) {
            backButton.setVisibility(View.VISIBLE);
            captionEdit.setVisibility(View.VISIBLE);
            if (paintView != null) {
                paintView.clearSelection();
            }
            downloadButton.setVisibility(View.VISIBLE);
            if (outputEntry != null && outputEntry.isRepostMessage) {
                getThemeButton().setVisibility(View.VISIBLE);
                updateThemeButtonDrawable(false);
            } else if (themeButton != null) {
                themeButton.setVisibility(View.GONE);
            }
            titleTextView.setVisibility(View.VISIBLE);
//            privacySelector.setVisibility(View.VISIBLE);
            if (mode == MODE_VIDEO) {
                muteButton.setVisibility(View.VISIBLE);
                playButton.setVisibility(View.VISIBLE);
            } else if (outputEntry != null && !TextUtils.isEmpty(outputEntry.audioPath)) {
                muteButton.setVisibility(View.GONE);
                playButton.setVisibility(View.VISIBLE);
            }
            timelineView.setVisibility(View.VISIBLE);
        }
        if (toMode == EDIT_MODE_PAINT && paintView != null) {
            paintView.setVisibility(View.VISIBLE);
        }
        if ((toMode == EDIT_MODE_PAINT || fromMode == EDIT_MODE_PAINT) && paintView != null) {
            paintView.onAnimationStateChanged(true);
        }

        if (paintView != null) {
            paintView.keyboardNotifier.ignore(toMode != EDIT_MODE_PAINT);
        }
        captionEdit.keyboardNotifier.ignore(toMode != EDIT_MODE_NONE);
//        privacySelectorHint.hide();
        Bulletin.hideVisible();
        if (photoFilterView != null && fromMode == EDIT_MODE_FILTER) {
            applyFilter(null);
        }
        if (photoFilterEnhanceView != null) {
            photoFilterEnhanceView.setAllowTouch(false);
        }
        muteHint.hide();

        if (toMode == EDIT_MODE_CROP) {
            createCropEditor();
            cropEditor.setVisibility(View.VISIBLE);
            if (outputEntry != null) {
                cropEditor.setEntry(outputEntry);
            }
        } else if (fromMode == EDIT_MODE_CROP) {
            previewView.applyMatrix();
            if (cropEditor != null) {
                cropEditor.disappearStarts();
            }
        }

        if (toMode == EDIT_MODE_CROP_INLINE) {
            createCropInlineEditor();
            cropInlineEditor.setVisibility(View.VISIBLE);
        } else if (fromMode == EDIT_MODE_CROP_INLINE) {
            previewView.applyMatrix();
            if (cropInlineEditor != null) {
                cropInlineEditor.disappearStarts();
            }
        }
    }

    private void onSwitchEditModeEnd(int fromMode, int toMode) {
        if (toMode == EDIT_MODE_PAINT) {
            backButton.setVisibility(View.GONE);
        }
        if (fromMode == EDIT_MODE_PAINT && paintView != null) {
            paintView.setVisibility(View.GONE);
        }
        if (fromMode == EDIT_MODE_NONE) {
            captionEdit.setVisibility(View.GONE);
            muteButton.setVisibility(toMode == EDIT_MODE_TIMELINE ? View.VISIBLE : View.GONE);
            playButton.setVisibility(toMode == EDIT_MODE_TIMELINE ? View.VISIBLE : View.GONE);
            downloadButton.setVisibility(toMode == EDIT_MODE_TIMELINE ? View.VISIBLE : View.GONE);
            if (themeButton != null) {
                themeButton.setVisibility(toMode == EDIT_MODE_TIMELINE ? View.VISIBLE : View.GONE);
            }
//            privacySelector.setVisibility(View.GONE);
            timelineView.setVisibility(toMode == EDIT_MODE_TIMELINE ? View.VISIBLE : View.GONE);
            titleTextView.setVisibility(View.GONE);
        }
        previewView.setAllowCropping(toMode == EDIT_MODE_NONE);
        if ((toMode == EDIT_MODE_PAINT || fromMode == EDIT_MODE_PAINT) && paintView != null) {
            paintView.onAnimationStateChanged(false);
        }
        if (photoFilterEnhanceView != null) {
            photoFilterEnhanceView.setAllowTouch(toMode == EDIT_MODE_FILTER || toMode == EDIT_MODE_NONE);
        }
        if (toMode == EDIT_MODE_CROP) {
            if (cropEditor != null) {
                cropEditor.setAppearProgress(1.0f);
            }
        } else if (fromMode == EDIT_MODE_CROP) {
            if (cropEditor != null) {
                cropEditor.setVisibility(View.GONE);
                cropEditor.setAppearProgress(0.0f);
                cropEditor.stop();
            }
        }
        if (toMode == EDIT_MODE_CROP_INLINE) {
            if (cropInlineEditor != null) {
                cropInlineEditor.setAppearProgress(1.0f);
            }
        } else if (fromMode == EDIT_MODE_CROP_INLINE) {
            if (cropInlineEditor != null) {
                cropInlineEditor.setVisibility(View.GONE);
                cropInlineEditor.setAppearProgress(0.0f);
                cropInlineEditor.stop();
            }
        }
    }

    private void applyPaintInBackground(Runnable whenDone) {
        final PaintView paintView = this.paintView;
        final StoryEntry outputEntry = this.outputEntry;
        if (paintView == null || outputEntry == null) {
            whenDone.run();
            return;
        }

        outputEntry.clearPaint();
        final boolean hasChanges = paintView.hasChanges();
        final boolean hasBlur = paintView.hasBlur();
        final int resultWidth = outputEntry.resultWidth;
        final int resultHeight = outputEntry.resultHeight;
        Utilities.searchQueue.postRunnable(() -> {

            ArrayList<VideoEditedInfo.MediaEntity> mediaEntities = new ArrayList<>();

            paintView.getBitmap(mediaEntities, resultWidth, resultHeight, false, false, false, false, outputEntry);
            if (!outputEntry.isVideo) {
                outputEntry.averageDuration = Utilities.clamp(paintView.getLcm(), 7500L, 5000L);
            }
            List<TLRPC.InputDocument> masks = paintView.getMasks();
            final List<TLRPC.InputDocument> stickers = masks != null ? new ArrayList<>(masks) : null;
            final boolean isVideo = outputEntry.isVideo;
            final boolean wouldBeVideo = outputEntry.wouldBeVideo();

            mediaEntities.clear();
            Bitmap bitmap = paintView.getBitmap(mediaEntities, resultWidth, resultHeight, true, false, false, !isVideo, outputEntry);
            if (mediaEntities.isEmpty()) {
                mediaEntities = null;
            }

            final File paintFile = FileLoader.getInstance(currentAccount).getPathToAttach(ImageLoader.scaleAndSaveImage(bitmap, Bitmap.CompressFormat.PNG, outputEntry.resultWidth, outputEntry.resultHeight, 87, false, 101, 101), true);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;

            final File backgroundFile;
            if (outputEntry.isRepostMessage && outputEntry.backgroundWallpaperPeerId != Long.MIN_VALUE) {
                Drawable drawable = outputEntry.backgroundDrawable;
                if (drawable == null) {
                    drawable = PreviewView.getBackgroundDrawable(null, currentAccount, outputEntry.backgroundWallpaperPeerId, isDark);
                }
                if (drawable != null) {
                    backgroundFile = StoryEntry.makeCacheFile(currentAccount, "webp");
                    bitmap = Bitmap.createBitmap(resultWidth, resultHeight, Bitmap.Config.ARGB_8888);
                    StoryEntry.drawBackgroundDrawable(new Canvas(bitmap), drawable, bitmap.getWidth(), bitmap.getHeight());
                    try {
                        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, new FileOutputStream(backgroundFile));
                    } catch (Exception e) {
                        FileLog.e(e);
                    } finally {
                        if (bitmap != null && !bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                        bitmap = null;
                    }
                } else {
                    backgroundFile = null;
                }
            } else {
                backgroundFile = null;
            }
            File messageVideoMaskFile = null;
            if (outputEntry.isRepostMessage && outputEntry.isVideo) {
                int videoWidth = outputEntry.width;
                int videoHeight = outputEntry.height;
                MessageEntityView messageEntityView = paintView.findMessageView();
                ImageReceiver photoImage = null;
                if (messageEntityView != null && messageEntityView.listView.getChildCount() == 1 && videoWidth > 0 && videoHeight > 0) {
                    View child = messageEntityView.listView.getChildAt(0);
                    if (child instanceof ChatMessageCell) {
                        ChatMessageCell cell = (ChatMessageCell) messageEntityView.listView.getChildAt(0);
                        photoImage = cell.getPhotoImage();
                    }
                }
                if (photoImage != null && (int) photoImage.getImageWidth() > 0 && (int) photoImage.getImageHeight() > 0) {
                    float scale = Math.max(photoImage.getImageWidth() / videoWidth, photoImage.getImageHeight() / videoHeight);
                    final float S = 2f;
                    int w = (int) (videoWidth * scale / S), h = (int) (videoHeight * scale / S);
                    Bitmap maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    float[] radii = new float[8];
                    for (int a = 0; a < photoImage.getRoundRadius().length; a++) {
                        radii[a * 2] = photoImage.getRoundRadius()[a];
                        radii[a * 2 + 1] = photoImage.getRoundRadius()[a];
                    }
                    Canvas canvas = new Canvas(maskBitmap);
                    Path path = new Path();
                    canvas.scale(1f / S, 1f / S);
                    AndroidUtilities.rectTmp.set(
                            w * S / 2f - photoImage.getImageWidth() / 2f,
                            h * S / 2f - photoImage.getImageHeight() / 2f,
                            w * S / 2f + photoImage.getImageWidth() / 2f,
                            h * S / 2f + photoImage.getImageHeight() / 2f
                    );
                    path.addRoundRect(AndroidUtilities.rectTmp, radii, Path.Direction.CW);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setColor(Color.WHITE);
                    canvas.drawPath(path, paint);
                    try {
                        messageVideoMaskFile = StoryEntry.makeCacheFile(currentAccount, "webp");
                        maskBitmap.compress(Bitmap.CompressFormat.WEBP, 100, new FileOutputStream(messageVideoMaskFile));
                    } catch (Exception e) {
                        FileLog.e(e);
                        messageVideoMaskFile = null;
                    }
                    maskBitmap.recycle();
                }
            }
            final File finalMessageVideoMaskFile = messageVideoMaskFile;

            final File paintEntitiesFile;
            if (!wouldBeVideo) {
                bitmap = paintView.getBitmap(new ArrayList<>(), resultWidth, resultHeight, false, true, false, false, outputEntry);
                paintEntitiesFile = FileLoader.getInstance(currentAccount).getPathToAttach(ImageLoader.scaleAndSaveImage(bitmap, Bitmap.CompressFormat.PNG, resultWidth, resultHeight, 87, false, 101, 101), true);
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                bitmap = null;
            } else {
                paintEntitiesFile = null;
            }

            final File paintBlurFile;
            if (hasBlur) {
                bitmap = paintView.getBlurBitmap();
                paintBlurFile = FileLoader.getInstance(currentAccount).getPathToAttach(ImageLoader.scaleAndSaveImage(bitmap, Bitmap.CompressFormat.PNG, resultWidth, resultHeight, 87, false, 101, 101), true);
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                bitmap = null;
            } else {
                paintBlurFile = null;
            }

            final ArrayList<VideoEditedInfo.MediaEntity> finalMediaEntities = mediaEntities;
            AndroidUtilities.runOnUIThread(() -> {
                try {
                    if (outputEntry.paintFile != null) {
                        outputEntry.paintFile.delete();
                    }
                } catch (Exception ignore) {}
                try {
                    if (outputEntry.paintEntitiesFile != null) {
                        outputEntry.paintEntitiesFile.delete();
                    }
                } catch (Exception ignore) {}
                try {
                    if (outputEntry.paintBlurFile != null) {
                        outputEntry.paintBlurFile.delete();
                    }
                } catch (Exception ignore) {}
                outputEntry.paintFile = null;
                outputEntry.paintEntitiesFile = null;
                outputEntry.paintBlurFile = null;
                if (outputEntry.backgroundFile != null) {
                    try {
                        outputEntry.backgroundFile.delete();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    outputEntry.backgroundFile = null;
                }
                if (outputEntry.messageVideoMaskFile != null) {
                    try {
                        outputEntry.messageVideoMaskFile.delete();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    outputEntry.messageVideoMaskFile = null;
                }

                outputEntry.editedMedia |= hasChanges;
                outputEntry.mediaEntities = finalMediaEntities;
                outputEntry.paintFile = paintFile;
                outputEntry.backgroundFile = backgroundFile;
                outputEntry.paintEntitiesFile = paintEntitiesFile;
                outputEntry.messageVideoMaskFile = finalMessageVideoMaskFile;
                outputEntry.paintBlurFile = paintBlurFile;
                outputEntry.stickers = stickers;

                if (whenDone != null) {
                    whenDone.run();
                }
            });
        });
    }

    private void applyPaintSwitching(Runnable whenDone) {
        final PaintView paintView = this.paintView;
        final StoryEntry outputEntry = this.outputEntry;
        if (paintView == null || outputEntry == null) {
            whenDone.run();
            return;
        }
        if (!paintView.hasChanges()) {
            whenDone.run();
            return;
        }

        outputEntry.clearPaint();
        final boolean hasChanges = paintView.hasChanges();
        final boolean hasBlur = paintView.hasBlur();
        final int resultWidth = outputEntry.resultWidth;
        final int resultHeight = outputEntry.resultHeight;
        Utilities.searchQueue.postRunnable(() -> {
            ArrayList<Runnable> postponedSaves = new ArrayList<>();

            ArrayList<VideoEditedInfo.MediaEntity> mediaEntities = new ArrayList<>();

            paintView.getBitmap(mediaEntities, resultWidth, resultHeight, false, false, false, false, outputEntry);
            if (!outputEntry.isVideo) {
                outputEntry.averageDuration = Utilities.clamp(paintView.getLcm(), 7500L, 5000L);
            }
            List<TLRPC.InputDocument> masks = paintView.getMasks();
            final List<TLRPC.InputDocument> stickers = masks != null ? new ArrayList<>(masks) : null;
            final boolean isVideo = outputEntry.isVideo;
            final boolean wouldBeVideo = outputEntry.wouldBeVideo();

            mediaEntities.clear();
            Bitmap bitmap = paintView.getBitmap(mediaEntities, resultWidth, resultHeight, true, false, false, !isVideo, outputEntry);
            if (mediaEntities.isEmpty()) {
                mediaEntities = null;
            }

            final File paintFile = StoryEntry.makeCacheFile(currentAccount, "png");
            final Bitmap finalBitmap2 = bitmap;
            postponedSaves.add(() -> {
                try {
                    finalBitmap2.compress(Bitmap.CompressFormat.PNG, 87, new FileOutputStream(paintFile));
                } catch (Exception e) {
                    FileLog.e(e);
                }
            });
            bitmap = null;

            final File backgroundFile;
            if (outputEntry.isRepostMessage && outputEntry.backgroundWallpaperPeerId != Long.MIN_VALUE) {
                Drawable drawable = outputEntry.backgroundDrawable;
                if (drawable == null) {
                    drawable = PreviewView.getBackgroundDrawable(null, currentAccount, outputEntry.backgroundWallpaperPeerId, isDark);
                }
                if (drawable != null) {
                    backgroundFile = StoryEntry.makeCacheFile(currentAccount, "webp");
                    bitmap = Bitmap.createBitmap(resultWidth, resultHeight, Bitmap.Config.ARGB_8888);
                    StoryEntry.drawBackgroundDrawable(new Canvas(bitmap), drawable, bitmap.getWidth(), bitmap.getHeight());
                    final Bitmap finalBitmap = bitmap;
                    postponedSaves.add(() -> {
                        try {
                            finalBitmap.compress(Bitmap.CompressFormat.WEBP, 100, new FileOutputStream(backgroundFile));
                        } catch (Exception e) {
                            FileLog.e(e);
                        } finally {
                            if (finalBitmap != null && !finalBitmap.isRecycled()) {
                                finalBitmap.recycle();
                            }
                        }
                    });
                } else {
                    backgroundFile = null;
                }
            } else {
                backgroundFile = null;
            }
            File messageVideoMaskFile = null;
            if (outputEntry.isRepostMessage && outputEntry.isVideo) {
                int videoWidth = outputEntry.width;
                int videoHeight = outputEntry.height;
                MessageEntityView messageEntityView = paintView.findMessageView();
                ImageReceiver photoImage = null;
                if (messageEntityView != null && messageEntityView.listView.getChildCount() == 1 && videoWidth > 0 && videoHeight > 0) {
                    View child = messageEntityView.listView.getChildAt(0);
                    if (child instanceof ChatMessageCell) {
                        ChatMessageCell cell = (ChatMessageCell) messageEntityView.listView.getChildAt(0);
                        photoImage = cell.getPhotoImage();
                    }
                }
                if (photoImage != null && (int) photoImage.getImageWidth() > 0 && (int) photoImage.getImageHeight() > 0) {
                    float scale = Math.max(photoImage.getImageWidth() / videoWidth, photoImage.getImageHeight() / videoHeight);
                    final float S = 2f;
                    int w = (int) (videoWidth * scale / S), h = (int) (videoHeight * scale / S);
                    Bitmap maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                    float[] radii = new float[8];
                    for (int a = 0; a < photoImage.getRoundRadius().length; a++) {
                        radii[a * 2] = photoImage.getRoundRadius()[a];
                        radii[a * 2 + 1] = photoImage.getRoundRadius()[a];
                    }
                    Canvas canvas = new Canvas(maskBitmap);
                    Path path = new Path();
                    canvas.scale(1f / S, 1f / S);
                    AndroidUtilities.rectTmp.set(
                        w * S / 2f - photoImage.getImageWidth() / 2f,
                        h * S / 2f - photoImage.getImageHeight() / 2f,
                        w * S / 2f + photoImage.getImageWidth() / 2f,
                        h * S / 2f + photoImage.getImageHeight() / 2f
                    );
                    path.addRoundRect(AndroidUtilities.rectTmp, radii, Path.Direction.CW);
                    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                    paint.setColor(Color.WHITE);
                    canvas.drawPath(path, paint);
                    messageVideoMaskFile = StoryEntry.makeCacheFile(currentAccount, "webp");
                    final File finalMessageVideoMaskFile = messageVideoMaskFile;
                    postponedSaves.add(() -> {
                        try {
                            maskBitmap.compress(Bitmap.CompressFormat.WEBP, 100, new FileOutputStream(finalMessageVideoMaskFile));
                        } catch (Exception e) {
                            FileLog.e(e);
                        } finally {
                            AndroidUtilities.recycleBitmap(maskBitmap);
                        }
                    });
                }
            }
            final File finalMessageVideoMaskFile = messageVideoMaskFile;

            final File paintEntitiesFile;
            final File paintBlurFile;
            if (paintView.hasChanges()) {
                if (!wouldBeVideo) {
                    bitmap = paintView.getBitmap(new ArrayList<>(), resultWidth, resultHeight, false, true, false, false, outputEntry);
                    final File file = paintEntitiesFile = StoryEntry.makeCacheFile(currentAccount, "png");
                    final Bitmap finalBitmap = bitmap;
                    postponedSaves.add(() -> {
                        try {
                            finalBitmap.compress(Bitmap.CompressFormat.PNG, 87, new FileOutputStream(file));
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    });
                    bitmap = null;
                } else {
                    paintEntitiesFile = null;
                }

                if (hasBlur) {
                    bitmap = paintView.getBlurBitmap();
                    final File file = paintBlurFile = StoryEntry.makeCacheFile(currentAccount, "png");
                    final Bitmap finalBitmap = bitmap;
                    postponedSaves.add(() -> {
                        try {
                            finalBitmap.compress(Bitmap.CompressFormat.PNG, 87, new FileOutputStream(file));
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    });
                    bitmap = null;
                } else {
                    paintBlurFile = null;
                }
            } else {
                paintEntitiesFile = outputEntry.paintEntitiesFile;
                paintBlurFile = outputEntry.paintBlurFile;
            }

            final ArrayList<VideoEditedInfo.MediaEntity> finalMediaEntities = mediaEntities;
            AndroidUtilities.runOnUIThread(() -> {
                try {
                    if (outputEntry.paintFile != null) {
                        outputEntry.paintFile.delete();
                    }
                } catch (Exception ignore) {}
                try {
                    if (outputEntry.paintEntitiesFile != null) {
                        outputEntry.paintEntitiesFile.delete();
                    }
                } catch (Exception ignore) {}
                try {
                    if (outputEntry.paintBlurFile != null) {
                        outputEntry.paintBlurFile.delete();
                    }
                } catch (Exception ignore) {}
                outputEntry.paintFile = null;
                outputEntry.paintEntitiesFile = null;
                outputEntry.paintBlurFile = null;
                if (outputEntry.backgroundFile != null) {
                    try {
                        outputEntry.backgroundFile.delete();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    outputEntry.backgroundFile = null;
                }
                if (outputEntry.messageVideoMaskFile != null) {
                    try {
                        outputEntry.messageVideoMaskFile.delete();
                    } catch (Exception e) {
                        FileLog.e(e);
                    }
                    outputEntry.messageVideoMaskFile = null;
                }

                outputEntry.editedMedia |= hasChanges;
                outputEntry.mediaEntities = finalMediaEntities;
                outputEntry.paintFile = paintFile;
                outputEntry.backgroundFile = backgroundFile;
                outputEntry.paintEntitiesFile = paintEntitiesFile;
                outputEntry.messageVideoMaskFile = finalMessageVideoMaskFile;
                outputEntry.paintBlurFile = paintBlurFile;
                outputEntry.stickers = stickers;

                if (whenDone != null) {
                    whenDone.run();
                }
            });

            for (Runnable runnable : postponedSaves) {
                runnable.run();
            }
        });
    }

    private void applyPaint() {
        if (paintView == null || outputEntry == null) {
            return;
        }

        outputEntry.clearPaint();
        outputEntry.editedMedia |= paintView.hasChanges();

        if (outputEntry.mediaEntities == null) {
            outputEntry.mediaEntities = new ArrayList<>();
        } else {
            outputEntry.mediaEntities.clear();
        }
        paintView.getBitmap(outputEntry.mediaEntities, outputEntry.resultWidth, outputEntry.resultHeight, false, false, false, false, outputEntry);
        if (!outputEntry.isVideo) {
            outputEntry.averageDuration = Utilities.clamp(paintView.getLcm(), 7500L, 5000L);
        }
        List<TLRPC.InputDocument> masks = paintView.getMasks();
        outputEntry.stickers = masks != null ? new ArrayList<>(masks) : null;
        final boolean isVideo = outputEntry.isVideo;
        final boolean wouldBeVideo = outputEntry.wouldBeVideo();

        outputEntry.mediaEntities = new ArrayList<>();
        Bitmap bitmap = paintView.getBitmap(outputEntry.mediaEntities, outputEntry.resultWidth, outputEntry.resultHeight, true, false, false, !isVideo, outputEntry);
        if (outputEntry.mediaEntities.isEmpty()) {
            outputEntry.mediaEntities = null;
        }

        try {
            if (outputEntry.paintFile != null) {
                outputEntry.paintFile.delete();
            }
        } catch (Exception ignore) {}
        try {
            if (outputEntry.paintEntitiesFile != null) {
                outputEntry.paintEntitiesFile.delete();
            }
        } catch (Exception ignore) {}
        try {
            if (outputEntry.paintBlurFile != null) {
                outputEntry.paintBlurFile.delete();
            }
        } catch (Exception ignore) {}
        outputEntry.paintFile = null;
        outputEntry.paintEntitiesFile = null;
        outputEntry.paintBlurFile = null;

        outputEntry.paintFile = FileLoader.getInstance(currentAccount).getPathToAttach(ImageLoader.scaleAndSaveImage(bitmap, Bitmap.CompressFormat.PNG, outputEntry.resultWidth, outputEntry.resultHeight, 87, false, 101, 101), true);
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        bitmap = null;

        if (outputEntry.isRepostMessage) {
            if (outputEntry.backgroundFile != null) {
                try {
                    outputEntry.backgroundFile.delete();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                outputEntry.backgroundFile = null;
            }
            if (outputEntry.backgroundWallpaperPeerId != Long.MIN_VALUE) {
                Drawable drawable = outputEntry.backgroundDrawable;
                if (drawable == null) {
                    drawable = PreviewView.getBackgroundDrawable(null, currentAccount, outputEntry.backgroundWallpaperPeerId, isDark);
                }
                if (drawable != null) {
                    outputEntry.backgroundFile = StoryEntry.makeCacheFile(currentAccount, "webp");
                    bitmap = Bitmap.createBitmap(outputEntry.resultWidth, outputEntry.resultHeight, Bitmap.Config.ARGB_8888);
                    StoryEntry.drawBackgroundDrawable(new Canvas(bitmap), drawable, bitmap.getWidth(), bitmap.getHeight());
                    try {
                        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, new FileOutputStream(outputEntry.backgroundFile));
                    } catch (Exception e) {
                        FileLog.e(e);
                    } finally {
                        if (bitmap != null && !bitmap.isRecycled()) {
                            bitmap.recycle();
                        }
                        bitmap = null;
                    }
                }
            }
        }
        if (outputEntry.isRepostMessage) {
            if (outputEntry.messageVideoMaskFile != null) {
                try {
                    outputEntry.messageVideoMaskFile.delete();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                outputEntry.messageVideoMaskFile = null;
            }
            if (outputEntry.isRepostMessage && outputEntry.isVideo) {
                int videoWidth = outputEntry.width;
                int videoHeight = outputEntry.height;
                MessageEntityView messageEntityView = paintView.findMessageView();
                if (messageEntityView != null && messageEntityView.listView.getChildCount() == 1 && videoWidth > 0 && videoHeight > 0) {
                    View child = messageEntityView.listView.getChildAt(0);
                    if (child instanceof ChatMessageCell) {
                        ChatMessageCell cell = (ChatMessageCell) messageEntityView.listView.getChildAt(0);
                        ImageReceiver photoImage = cell.getPhotoImage();
                        if (photoImage != null && (int) photoImage.getImageWidth() > 0 && (int) photoImage.getImageHeight() > 0) {
                            float scale = Math.max(photoImage.getImageWidth() / videoWidth, photoImage.getImageHeight() / videoHeight);
                            final float S = 2f;
                            int w = (int) (videoWidth * scale / S), h = (int) (videoHeight * scale / S);
                            Bitmap maskBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                            float[] radii = new float[8];
                            for (int a = 0; a < photoImage.getRoundRadius().length; a++) {
                                radii[a * 2] = photoImage.getRoundRadius()[a];
                                radii[a * 2 + 1] = photoImage.getRoundRadius()[a];
                            }
                            Canvas canvas = new Canvas(maskBitmap);
                            Path path = new Path();
                            canvas.scale(1f / S, 1f / S);
                            AndroidUtilities.rectTmp.set(
                                w * S / 2f - photoImage.getImageWidth() / 2f,
                                h * S / 2f - photoImage.getImageHeight() / 2f,
                                w * S / 2f + photoImage.getImageWidth() / 2f,
                                h * S / 2f + photoImage.getImageHeight() / 2f
                            );
                            path.addRoundRect(AndroidUtilities.rectTmp, radii, Path.Direction.CW);
                            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                            paint.setColor(Color.WHITE);
                            canvas.drawPath(path, paint);
                            try {
                                outputEntry.messageVideoMaskFile = StoryEntry.makeCacheFile(currentAccount, "webp");
                                maskBitmap.compress(Bitmap.CompressFormat.WEBP, 100, new FileOutputStream(outputEntry.messageVideoMaskFile));
                            } catch (Exception e) {
                                FileLog.e(e);
                                outputEntry.messageVideoMaskFile = null;
                            }
                            maskBitmap.recycle();
                        }
                    }
                }
            }
        }

        if (!wouldBeVideo) {
            bitmap = paintView.getBitmap(new ArrayList<>(), outputEntry.resultWidth, outputEntry.resultHeight, false, true, false, false, outputEntry);
            outputEntry.paintEntitiesFile = FileLoader.getInstance(currentAccount).getPathToAttach(ImageLoader.scaleAndSaveImage(bitmap, Bitmap.CompressFormat.PNG, outputEntry.resultWidth, outputEntry.resultHeight, 87, false, 101, 101), true);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
        }

        if (paintView.hasBlur()) {
            bitmap = paintView.getBlurBitmap();
            outputEntry.paintBlurFile = FileLoader.getInstance(currentAccount).getPathToAttach(ImageLoader.scaleAndSaveImage(bitmap, Bitmap.CompressFormat.PNG, outputEntry.resultWidth, outputEntry.resultHeight, 87, false, 101, 101), true);
            if (bitmap != null && !bitmap.isRecycled()) {
                bitmap.recycle();
            }
            bitmap = null;
        }
    }

    // separated to run on main thread (chatmessagecell uses global paints and resources)
    private void applyPaintMessage() {
        if (paintView == null || outputEntry == null) {
            return;
        }

        if (outputEntry.isRepostMessage) {
            if (outputEntry.messageFile != null) {
                try {
                    outputEntry.messageFile.delete();
                } catch (Exception e) {
                    FileLog.e(e);
                }
                outputEntry.messageFile = null;
            }
            outputEntry.messageFile = StoryEntry.makeCacheFile(currentAccount, "webp");
            Bitmap bitmap = paintView.getBitmap(outputEntry.mediaEntities, outputEntry.resultWidth, outputEntry.resultHeight, false, false, true, mode != MODE_VIDEO, outputEntry);
            try {
                bitmap.compress(Bitmap.CompressFormat.WEBP, 100, new FileOutputStream(outputEntry.messageFile));
            } catch (Exception e) {
                FileLog.e(e);
                try {
                    outputEntry.messageFile.delete();
                } catch (Exception e2) {
                    FileLog.e(e2);
                }
                outputEntry.messageFile = null;
            } finally {
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                bitmap = null;
            }
        }
    }

    private void applyFilter(Runnable whenDone) {
        if (photoFilterView == null || outputEntry == null) {
            if (whenDone != null) {
                whenDone.run();
            }
            return;
        }
        outputEntry.editedMedia |= photoFilterView.hasChanges();
        outputEntry.updateFilter(photoFilterView, whenDone);
        if (whenDone == null && !outputEntry.isVideo && previewView != null) {
            previewView.set(outputEntry);
        }
    }

//    private Matrix photoFilterStartMatrix, photoFilterEndMatrix;

    private void createFilterPhotoView() {
        if (photoFilterView != null || outputEntry == null) {
            return;
        }

        Bitmap photoBitmap = null;
        if (!outputEntry.isVideo) {
            if (outputEntry.filterFile == null) {
                photoBitmap = previewView.getPhotoBitmap();
            } else {
                photoBitmap = StoryEntry.getScaledBitmap(opts -> BitmapFactory.decodeFile(outputEntry.file.getAbsolutePath(), opts), AndroidUtilities.displaySize.x, AndroidUtilities.displaySize.y, true, true);
            }
        }
        if (photoBitmap == null && !outputEntry.isVideo) {
            return;
        }

        photoFilterView = new PhotoFilterView(activity, previewView.getTextureView(), photoBitmap, previewView.getOrientation(), outputEntry == null ? null : outputEntry.filterState, null, 0, false, false, blurManager, resourcesProvider);
        containerView.addView(photoFilterView);
        if (photoFilterEnhanceView != null) {
            photoFilterEnhanceView.setFilterView(photoFilterView);
        }
        photoFilterViewTextureView = photoFilterView.getMyTextureView();
        if (photoFilterViewTextureView != null) {
            photoFilterViewTextureView.setOpaque(false);
        }
        previewView.setFilterTextureView(photoFilterViewTextureView, photoFilterView);
        if (photoFilterViewTextureView != null) {
            photoFilterViewTextureView.setAlpha(0f);
            photoFilterViewTextureView.animate().alpha(1f).setDuration(220).start();
        }
        applyFilterMatrix();
        photoFilterViewBlurControl = photoFilterView.getBlurControl();
        if (photoFilterViewBlurControl != null) {
            previewContainer.addView(photoFilterViewBlurControl);
        }
        photoFilterViewCurvesControl = photoFilterView.getCurveControl();
        if (photoFilterViewCurvesControl != null) {
            previewContainer.addView(photoFilterViewCurvesControl);
        }
        orderPreviewViews();

        photoFilterView.getDoneTextView().setOnClickListener(v -> {
            switchToEditMode(EDIT_MODE_NONE, true);
        });
        photoFilterView.getCancelTextView().setOnClickListener(v -> {
            switchToEditMode(EDIT_MODE_NONE, true);
        });
        photoFilterView.getToolsView().setVisibility(View.GONE);
        photoFilterView.getToolsView().setAlpha(0f);
        photoFilterView.getToolsView().setTranslationY(AndroidUtilities.dp(186));
        photoFilterView.init();
    }

    public void invalidateBlur() {
        if (captionEdit != null) {
            captionEdit.invalidateBlur();
        }
    }

    private void applyFilterMatrix() {
        if (outputEntry != null && photoFilterViewTextureView != null && previewContainer.getMeasuredWidth() > 0 && previewContainer.getMeasuredHeight() > 0) {
            Matrix photoFilterStartMatrix = new Matrix();
            photoFilterStartMatrix.reset();
            if (outputEntry.orientation != 0) {
                photoFilterStartMatrix.postRotate(-outputEntry.orientation, previewContainer.getMeasuredWidth() / 2f, previewContainer.getMeasuredHeight() / 2f);
                if (outputEntry.orientation / 90 % 2 == 1) {
                    photoFilterStartMatrix.postScale(
                            (float) previewContainer.getMeasuredWidth() / previewContainer.getMeasuredHeight(),
                            (float) previewContainer.getMeasuredHeight() / previewContainer.getMeasuredWidth(),
                            previewContainer.getMeasuredWidth() / 2f,
                            previewContainer.getMeasuredHeight() / 2f
                    );
                }
            }
            photoFilterStartMatrix.postScale(
                    1f / previewContainer.getMeasuredWidth() * outputEntry.width,
                    1f / previewContainer.getMeasuredHeight() * outputEntry.height
            );
            photoFilterStartMatrix.postConcat(outputEntry.matrix);
            photoFilterStartMatrix.postScale(
                    (float) previewContainer.getMeasuredWidth() / outputEntry.resultWidth,
                    (float) previewContainer.getMeasuredHeight() / outputEntry.resultHeight
            );
            photoFilterViewTextureView.setTransform(photoFilterStartMatrix);
            photoFilterViewTextureView.invalidate();
        }
//        if (outputEntry != null && previewView != null && photoFilterViewTextureView != null) {
//            previewView.setTextureViewTransform(photoFilterViewTextureView);
//            photoFilterViewTextureView.invalidate();
//        }
    }

    private void destroyPhotoFilterView() {
        if (photoFilterView == null) {
            return;
        }
        photoFilterView.shutdown();
        photoFilterEnhanceView.setFilterView(null);
        containerView.removeView(photoFilterView);
        if (photoFilterViewTextureView != null) {
            previewContainer.removeView(photoFilterViewTextureView);
            photoFilterViewTextureView = null;
        }
        previewView.setFilterTextureView(null, null);
        if (photoFilterViewBlurControl != null) {
            previewContainer.removeView(photoFilterViewBlurControl);
            photoFilterViewBlurControl = null;
        }
        if (photoFilterViewCurvesControl != null) {
            previewContainer.removeView(photoFilterViewCurvesControl);
            photoFilterViewCurvesControl = null;
        }
        photoFilterView = null;
//        photoFilterStartMatrix = null;
//        photoFilterEndMatrix = null;
//        if (photoFilterAnimator != null) {
//            photoFilterAnimator.cancel();
//            photoFilterAnimator = null;
//        }
    }

    private boolean noCameraPermission;

    @SuppressLint("ClickableViewAccessibility")
    private void createCameraView() {
        if (cameraView != null || getContext() == null) {
            return;
        }
        cameraView = new DualCameraView(getContext(), getCameraFace(), false) {
            @Override
            public void onEntityDraggedTop(boolean value) {
                previewHighlight.show(true, value, actionBarContainer);
            }

            @Override
            public void onEntityDraggedBottom(boolean value) {
                previewHighlight.updateCaption(captionEdit.getText());
                previewHighlight.show(false, value, controlContainer);
            }

            @Override
            public void toggleDual() {
                super.toggleDual();
                dualButton.setValue(isDual());
                dualButton.setContentDescription(getString(isDual() ? R.string.AccDescrDualCameraOn : R.string.AccDescrDualCameraOff));
//                recordControl.setDual(isDual());
                setCameraFlashModeIcon(getCurrentFlashMode(), true);
            }

            @Override
            protected void onSavedDualCameraSuccess() {
                if (MessagesController.getGlobalMainSettings().getInt("storysvddualhint", 0) < 2) {
                    AndroidUtilities.runOnUIThread(() -> {
                        if (takingVideo || takingPhoto || cameraView == null || currentPage != PAGE_CAMERA) {
                            return;
                        }
                        if (savedDualHint != null) {
                            CharSequence text = isFrontface() ? getString(R.string.StoryCameraSavedDualBackHint) : getString(R.string.StoryCameraSavedDualFrontHint);
                            savedDualHint.setMaxWidthPx(HintView2.cutInFancyHalf(text, savedDualHint.getTextPaint()));
                            savedDualHint.setText(text);
                            savedDualHint.show();
                            MessagesController.getGlobalMainSettings().edit().putInt("storysvddualhint", MessagesController.getGlobalMainSettings().getInt("storysvddualhint", 0) + 1).apply();
                        }
                    }, 340);
                }
                dualButton.setValue(isDual());
                dualButton.setContentDescription(getString(isDual() ? R.string.AccDescrDualCameraOn : R.string.AccDescrDualCameraOff));
            }

            @Override
            protected void receivedAmplitude(double amplitude) {
                if (recordControl != null) {
                    recordControl.setAmplitude(Utilities.clamp((float) (amplitude / WaveDrawable.MAX_AMPLITUDE), 1, 0), true);
                }
            }
        };
        if (recordControl != null) {
            recordControl.setAmplitude(0, false);
        }
        cameraView.recordHevc = !collageLayoutView.hasLayout();
        cameraView.setThumbDrawable(getCameraThumb());
        cameraView.initTexture();
        cameraView.setDelegate(() -> {
            String currentFlashMode = getCurrentFlashMode();
            if (TextUtils.equals(currentFlashMode, getNextFlashMode())) {
                currentFlashMode = null;
            }
            setCameraFlashModeIcon(currentPage == PAGE_CAMERA ? currentFlashMode : null, true);
            if (zoomControlView != null) {
                zoomControlView.setZoom(cameraZoom = 0, false);
            }
            updateActionBarButtons(true);
        });
        setActionBarButtonVisible(dualButton, cameraView.dualAvailable() && currentPage == PAGE_CAMERA, true);
        collageButton.setTranslationX(cameraView.dualAvailable() ? 0 : dp(46));
//        collageLayoutView.getLast().addView(cameraView, LayoutHelper.createFrame(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT, Gravity.FILL));
        collageLayoutView.setCameraView(cameraView);
        if (MessagesController.getGlobalMainSettings().getInt("storyhint2", 0) < 1) {
            cameraHint.show();
            MessagesController.getGlobalMainSettings().edit().putInt("storyhint2", MessagesController.getGlobalMainSettings().getInt("storyhint2", 0) + 1).apply();
        } else if (!cameraView.isSavedDual() && cameraView.dualAvailable() && MessagesController.getGlobalMainSettings().getInt("storydualhint", 0) < 2) {
            dualHint.show();
        }
        if (qrScanner == null) {
            qrScanner = new QRScanner(getContext(), d -> {
                if (qrScanner == null) return;
                qrLinkView.setLink(d == null ? null : d.link);
                if (collageLayoutView != null) {
                    collageLayoutView.qrDrawer.setQrDetected(qrLinkView.isResolved() ? qrScanner.getDetected() : null);
                }
            });
        }
        qrScanner.attach(cameraView);
        if (qrLinkView != null) {
            qrLinkView.setBlurRenderNode(collageLayoutView, collageLayoutView.getBlurRenderNode());
        }
    }

    private int frontfaceFlashMode = -1;
    private ArrayList<String> frontfaceFlashModes;
    private void checkFrontfaceFlashModes() {
        if (frontfaceFlashMode < 0) {
            frontfaceFlashMode = MessagesController.getGlobalMainSettings().getInt("frontflash", 1);
            frontfaceFlashModes = new ArrayList<>();
            frontfaceFlashModes.add(Camera.Parameters.FLASH_MODE_OFF);
            frontfaceFlashModes.add(Camera.Parameters.FLASH_MODE_AUTO);
            frontfaceFlashModes.add(Camera.Parameters.FLASH_MODE_ON);

            flashViews.setWarmth(MessagesController.getGlobalMainSettings().getFloat("frontflash_warmth", .9f));
            flashViews.setIntensity(MessagesController.getGlobalMainSettings().getFloat("frontflash_intensity", 1));
        }
    }
    private void saveFrontFaceFlashMode() {
        if (frontfaceFlashMode >= 0) {
            MessagesController.getGlobalMainSettings().edit()
                .putFloat("frontflash_warmth", flashViews.warmth)
                .putFloat("frontflash_intensity", flashViews.intensity)
                .apply();
        }
    }

    private String getCurrentFlashMode() {
        if (cameraView == null || cameraView.getCameraSession() == null) {
            return null;
        }
        if (cameraView.isFrontface() && !cameraView.getCameraSession().hasFlashModes()) {
            checkFrontfaceFlashModes();
            return frontfaceFlashModes.get(frontfaceFlashMode);
        }
        return cameraView.getCameraSession().getCurrentFlashMode();
    }

    private String getNextFlashMode() {
        if (cameraView == null || cameraView.getCameraSession() == null) {
            return null;
        }
        if (cameraView.isFrontface() && !cameraView.getCameraSession().hasFlashModes()) {
            checkFrontfaceFlashModes();
            return frontfaceFlashModes.get(frontfaceFlashMode + 1 >= frontfaceFlashModes.size() ? 0 : frontfaceFlashMode + 1);
        }
        return cameraView.getCameraSession().getNextFlashMode();
    }

    private void setCurrentFlashMode(String mode) {
        if (cameraView == null || cameraView.getCameraSession() == null) {
            return;
        }
        if (cameraView.isFrontface() && !cameraView.getCameraSession().hasFlashModes()) {
            int index = frontfaceFlashModes.indexOf(mode);
            if (index >= 0) {
                frontfaceFlashMode = index;
                MessagesController.getGlobalMainSettings().edit().putInt("frontflash", frontfaceFlashMode).apply();
            }
            return;
        }
        cameraView.getCameraSession().setCurrentFlashMode(mode);
    }


    private Drawable getCameraThumb() {
        Bitmap bitmap = null;
        try {
            File file = new File(ApplicationLoader.getFilesDirFixed(), "cthumb.jpg");
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        } catch (Throwable ignore) {}
        if (bitmap != null) {
            return new BitmapDrawable(bitmap);
        } else {
            return getContext().getResources().getDrawable(R.drawable.icplaceholder);
        }
    }

    private void saveLastCameraBitmap(Runnable whenDone) {
        if (cameraView == null || cameraView.getTextureView() == null) {
            return;
        }
        try {
            TextureView textureView = cameraView.getTextureView();
            final Bitmap bitmap = textureView.getBitmap();
            Utilities.themeQueue.postRunnable(() -> {
                try {
                    if (bitmap != null) {
                        Bitmap newBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), cameraView.getMatrix(), true);
                        bitmap.recycle();
                        Bitmap bitmap2 = newBitmap;
                        Bitmap lastBitmap = Bitmap.createScaledBitmap(bitmap2, 80, (int) (bitmap2.getHeight() / (bitmap2.getWidth() / 80.0f)), true);
                        if (lastBitmap != null) {
                            if (lastBitmap != bitmap2) {
                                bitmap2.recycle();
                            }
                            Utilities.blurBitmap(lastBitmap, 7, 1, lastBitmap.getWidth(), lastBitmap.getHeight(), lastBitmap.getRowBytes());
                            File file = new File(ApplicationLoader.getFilesDirFixed(), "cthumb.jpg");
                            FileOutputStream stream = new FileOutputStream(file);
                            lastBitmap.compress(Bitmap.CompressFormat.JPEG, 87, stream);
                            lastBitmap.recycle();
                            stream.close();
                        }
                    }
                } catch (Throwable ignore) {

                } finally {
                    AndroidUtilities.runOnUIThread(whenDone);
                }
            });
        } catch (Throwable ignore) {}
    }

    private void showDismissEntry() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), resourcesProvider);
        builder.setTitle(getString(R.string.DiscardChanges));
        builder.setMessage(getString(R.string.PhotoEditorDiscardAlert));
        if (outputEntry != null && !outputEntry.isEdit && !outputEntry.isShare) {
            builder.setNeutralButton(getString(outputEntry.isDraft ? R.string.StoryKeepDraft : R.string.StorySaveDraft), (di, i) -> {
                if (outputEntry == null) {
                    return;
                }
                outputEntry.captionEntitiesAllowed = MessagesController.getInstance(currentAccount).storyEntitiesAllowed();
                showSavedDraftHint = !outputEntry.isDraft;
                applyFilter(null);
                applyPaint();
                applyPaintMessage();
                destroyPhotoFilterView();
                StoryEntry storyEntry = outputEntry;
                storyEntry.destroy(true);
                storyEntry.caption = captionEdit.getText();
                outputEntry = null;
                prepareThumb(storyEntry, true);
                DraftsController drafts = MessagesController.getInstance(currentAccount).getStoriesController().getDraftsController();
                if (storyEntry.isDraft) {
                    drafts.edit(storyEntry);
                } else {
                    drafts.append(storyEntry);
                }
                navigateTo(PAGE_CAMERA, true);
            });
        }
        builder.setPositiveButton(outputEntry != null && outputEntry.isDraft && !outputEntry.isEdit ? getString(R.string.StoryDeleteDraft) : getString(R.string.Discard), (dialogInterface, i) -> {
            if (outputEntry != null && !(outputEntry.isEdit || outputEntry.isRepost && !outputEntry.isRepostMessage) && outputEntry.isDraft) {
                MessagesController.getInstance(currentAccount).getStoriesController().getDraftsController().delete(outputEntry);
                outputEntry = null;
            }
            if (outputEntry != null && (outputEntry.isShare || outputEntry.isEdit || outputEntry.isRepost && !outputEntry.isRepostMessage)) {
                close(true);
            } else {
                navigateTo(PAGE_CAMERA, true);
            }
        });
        builder.setNegativeButton(getString(R.string.Cancel), null);
        AlertDialog dialog = builder.create();
        dialog.show();
        View positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        if (positiveButton instanceof TextView) {
            ((TextView) positiveButton).setTextColor(Theme.getColor(Theme.key_text_RedBold, resourcesProvider));
            positiveButton.setBackground(Theme.createRadSelectorDrawable(ColorUtils.setAlphaComponent(Theme.getColor(Theme.key_text_RedBold, resourcesProvider), (int) (0.2f * 255)), 6, 6));
        }
    }

    private void destroyCameraView(boolean waitForThumb) {
        if (qrScanner != null) {
            qrScanner.destroy();
            qrScanner = null;
            if (collageLayoutView != null) {
                collageLayoutView.qrDrawer.setQrDetected(null);
            }
        }
        if (qrLinkView != null) {
            qrLinkView.setBlurRenderNode(null, null);
        }
        if (cameraView != null) {
            if (waitForThumb) {
                saveLastCameraBitmap(() -> {
                    collageLayoutView.setCameraThumb(getCameraThumb());
                    if (cameraView != null) {
                        cameraView.destroy(true, null);
                        AndroidUtilities.removeFromParent(cameraView);
                        if (collageLayoutView != null) {
                            collageLayoutView.setCameraView(null);
                        }
                        cameraView = null;
                    }
                });
            } else {
                saveLastCameraBitmap(() -> {
                    collageLayoutView.setCameraThumb(getCameraThumb());
                });
                cameraView.destroy(true, null);
                AndroidUtilities.removeFromParent(cameraView);
                if (collageLayoutView != null) {
                    collageLayoutView.setCameraView(null);
                }
                cameraView = null;
            }
        }
    }

    public interface Touchable {
        boolean onTouch(MotionEvent event);
    }

    private Touchable previewTouchable;
    private boolean requestedCameraPermission;

    private void requestCameraPermission(boolean force) {
        if (requestedCameraPermission && !force) {
            return;
        }
        noCameraPermission = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity != null) {
            noCameraPermission = activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED;
            if (noCameraPermission) {
                Drawable iconDrawable = getContext().getResources().getDrawable(R.drawable.story_camera).mutate();
                iconDrawable.setColorFilter(new PorterDuffColorFilter(0x3dffffff, PorterDuff.Mode.MULTIPLY));
                CombinedDrawable drawable = new CombinedDrawable(new ColorDrawable(0xff222222), iconDrawable);
                drawable.setIconSize(dp(64), dp(64));
                collageLayoutView.setCameraThumb(drawable);
                if (activity.shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    new AlertDialog.Builder(getContext(), resourcesProvider)
                        .setTopAnimation(R.raw.permission_request_camera, AlertsCreator.PERMISSIONS_REQUEST_TOP_ICON_SIZE, false, Theme.getColor(Theme.key_dialogTopBackground))
                        .setMessage(AndroidUtilities.replaceTags(getString(R.string.PermissionNoCameraWithHint)))
                        .setPositiveButton(getString(R.string.PermissionOpenSettings), (dialogInterface, i) -> {
                            try {
                                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                                activity.startActivity(intent);
                            } catch (Exception e) {
                                FileLog.e(e);
                            }
                        })
                        .setNegativeButton(getString(R.string.ContactsPermissionAlertNotNow), null)
                        .create()
                        .show();
                    return;
                }
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, 111);
                requestedCameraPermission = true;
            }
        }

        if (!noCameraPermission) {
            if (CameraController.getInstance().isCameraInitied()) {
                createCameraView();
            } else {
                CameraController.getInstance().initCamera(this::createCameraView);
            }
        }
    }

    private boolean requestGalleryPermission() {
        if (activity != null) {
            boolean noGalleryPermission = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                noGalleryPermission = (
                    activity.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED ||
                    activity.checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED
                );
                if (noGalleryPermission) {
                    activity.requestPermissions(new String[]{Manifest.permission.READ_MEDIA_IMAGES, Manifest.permission.READ_MEDIA_VIDEO}, 114);
                }
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                noGalleryPermission = activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED;
                if (noGalleryPermission) {
                    activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 114);
                }
            }
            return !noGalleryPermission;
        }
        return true;
    }

    private boolean requestAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity != null) {
            boolean granted = activity.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            if (!granted) {
                activity.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 112);
                return false;
            }
        }
        return true;
    }

    public static void onResume() {
        if (instance != null) {
            instance.onResumeInternal();
        }
    }

    private Runnable whenOpenDone;
    private void onResumeInternal() {
        if (currentPage == PAGE_CAMERA) {
//            requestedCameraPermission = false;
            if (openCloseAnimator != null && openCloseAnimator.isRunning()) {
                whenOpenDone = () -> requestCameraPermission(false);
            } else {
                requestCameraPermission(false);
            }
        }
        if (captionEdit != null) {
            captionEdit.onResume();
        }
        if (recordControl != null) {
            recordControl.updateGalleryImage();
        }
        if (previewHighlight != null) {
            previewHighlight.updateCount();
        }
        if (paintView != null) {
            paintView.onResume();
        }
        if (previewView != null) {
            previewView.updatePauseReason(0, false);
        }

        MessagesController.getInstance(currentAccount).getStoriesController().getDraftsController().load();
    }

    public static void onPause() {
        if (instance != null) {
            instance.onPauseInternal();
        }
    }
    private void onPauseInternal() {
        destroyCameraView(false);
        if (captionEdit != null) {
            captionEdit.onPause();
        }
        if (previewView != null) {
            previewView.updatePauseReason(0, true);
        }
    }

    public static void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (instance != null) {
            instance.onRequestPermissionsResultInternal(requestCode, permissions, grantResults);
        }
    }

    private Runnable audioGrantedCallback;
    private void onRequestPermissionsResultInternal(int requestCode, String[] permissions, int[] grantResults) {
        final boolean granted = grantResults != null && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        if (requestCode == 111) {
            noCameraPermission = !granted;
            if (granted && currentPage == PAGE_CAMERA) {
                collageLayoutView.setCameraThumb(null);
                if (CameraController.getInstance().isCameraInitied()) {
                    createCameraView();
                } else {
                    CameraController.getInstance().initCamera(this::createCameraView);
                }
            }
        } else if (requestCode == 114) {
            if (granted) {
                MediaController.loadGalleryPhotosAlbums(0);
                animateGalleryListView(true);
            } else {
                new AlertDialog.Builder(getContext(), resourcesProvider)
                    .setTopAnimation(R.raw.permission_request_folder, AlertsCreator.PERMISSIONS_REQUEST_TOP_ICON_SIZE, false, Theme.getColor(Theme.key_dialogTopBackground))
                    .setMessage(AndroidUtilities.replaceTags(getString(R.string.PermissionStorageWithHint)))
                    .setPositiveButton(getString(R.string.PermissionOpenSettings), (dialogInterface, i) -> {
                        try {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    })
                    .setNegativeButton(getString(R.string.ContactsPermissionAlertNotNow), null)
                    .create()
                    .show();
            }
        } else if (requestCode == 112) {
            if (!granted) {
                new AlertDialog.Builder(getContext(), resourcesProvider)
                    .setTopAnimation(R.raw.permission_request_camera, AlertsCreator.PERMISSIONS_REQUEST_TOP_ICON_SIZE, false, Theme.getColor(Theme.key_dialogTopBackground))
                    .setMessage(AndroidUtilities.replaceTags(getString(R.string.PermissionNoCameraMicVideo)))
                    .setPositiveButton(getString(R.string.PermissionOpenSettings), (dialogInterface, i) -> {
                        try {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    })
                    .setNegativeButton(getString(R.string.ContactsPermissionAlertNotNow), null)
                    .create()
                    .show();
            }
        } else if (requestCode == 115) {
            if (!granted) {
                new AlertDialog.Builder(getContext(), resourcesProvider)
                    .setTopAnimation(R.raw.permission_request_folder, AlertsCreator.PERMISSIONS_REQUEST_TOP_ICON_SIZE, false, Theme.getColor(Theme.key_dialogTopBackground))
                    .setMessage(AndroidUtilities.replaceTags(getString(R.string.PermissionNoAudioStorageStory)))
                    .setPositiveButton(getString(R.string.PermissionOpenSettings), (dialogInterface, i) -> {
                        try {
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + ApplicationLoader.applicationContext.getPackageName()));
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            FileLog.e(e);
                        }
                    })
                    .setNegativeButton(getString(R.string.ContactsPermissionAlertNotNow), null)
                    .create()
                    .show();
            }
            if (granted && audioGrantedCallback != null) {
                audioGrantedCallback.run();
            }
            audioGrantedCallback = null;
        }
    }

    private void saveCameraFace(boolean frontface) {
        MessagesController.getGlobalMainSettings().edit().putBoolean("stories_camera", frontface).apply();
    }

    private boolean getCameraFace() {
        return MessagesController.getGlobalMainSettings().getBoolean("stories_camera", false);
    }

    @Override
    public void didReceivedNotification(int id, int account, Object... args) {
        if (id == NotificationCenter.albumsDidLoad) {
            if (recordControl != null) {
                recordControl.updateGalleryImage();
            }
            if (lastGallerySelectedAlbum != null && MediaController.allMediaAlbums != null) {
                for (int a = 0; a < MediaController.allMediaAlbums.size(); a++) {
                    MediaController.AlbumEntry entry = MediaController.allMediaAlbums.get(a);
                    if (entry.bucketId == lastGallerySelectedAlbum.bucketId && entry.videoOnly == lastGallerySelectedAlbum.videoOnly) {
                        lastGallerySelectedAlbum = entry;
                        break;
                    }
                }
            }
        } else if (id == NotificationCenter.storiesDraftsUpdated) {
            if (recordControl != null && !showSavedDraftHint) {
                recordControl.updateGalleryImage();
            }
        } else if (id == NotificationCenter.storiesLimitUpdate) {
            if (currentPage == PAGE_PREVIEW) {
                previewButtons.setShareEnabled(!videoError && !captionEdit.isCaptionOverLimit() && (!MessagesController.getInstance(currentAccount).getStoriesController().hasStoryLimit(getCount()) || (outputEntry != null && (outputEntry.isEdit || outputEntry.botId != 0))));
            } else if (currentPage == PAGE_CAMERA) {
                StoriesController.StoryLimit storyLimit = MessagesController.getInstance(currentAccount).getStoriesController().checkStoryLimit();
                if (storyLimit != null && storyLimit.active(currentAccount) && (outputEntry == null || outputEntry.botId == 0)) {
                    showLimitReachedSheet(storyLimit, true);
                }
            }
        }
    }

    private int getCount() {
        if (selectedEntries != null) {
            return selectedEntries.size();
        } else if (outputEntry != null) {
            return outputEntry.getTotalCount();
        } else {
            return 1;
        }
    }

    public void addNotificationObservers() {
        NotificationCenter.getGlobalInstance().addObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.storiesDraftsUpdated);
        NotificationCenter.getInstance(currentAccount).addObserver(this, NotificationCenter.storiesLimitUpdate);
    }

    public void removeNotificationObservers() {
        NotificationCenter.getGlobalInstance().removeObserver(this, NotificationCenter.albumsDidLoad);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.storiesDraftsUpdated);
        NotificationCenter.getInstance(currentAccount).removeObserver(this, NotificationCenter.storiesLimitUpdate);
    }

    private boolean shownLimitReached;
    private void showLimitReachedSheet(StoriesController.StoryLimit storyLimit, boolean closeRecorder) {
        if (shownLimitReached) {
            return;
        }
        final LimitReachedBottomSheet sheet = new LimitReachedBottomSheet(new BaseFragment() {
            @Override
            public boolean isLightStatusBar() {
                return false;
            }

            @Override
            public Activity getParentActivity() {
                return activity;
            }

            @Override
            public Theme.ResourcesProvider getResourceProvider() {
                return new WrappedResourceProvider(resourcesProvider) {
                    @Override
                    public void appendColors() {
                        sparseIntArray.append(Theme.key_dialogBackground, 0xFF1F1F1F);
                        sparseIntArray.append(Theme.key_windowBackgroundGray, 0xFF333333);
                    }
                };
            }

            @Override
            public boolean presentFragment(BaseFragment fragment) {
                openPremium();
                return false;
            }
        }, activity, storyLimit.getLimitReachedType(), currentAccount, null) {
            { storiesCount = getCount(); }
        };
        sheet.setOnDismissListener(e -> {
            shownLimitReached = false;
            previewView.updatePauseReason(7, true);
            if (closeRecorder) {
                close(true);
            }
        });
        previewView.updatePauseReason(7, true);
        shownLimitReached = true;
        sheet.show();
    }

    private boolean isBackgroundVisible;
    private boolean forceBackgroundVisible;
    private void checkBackgroundVisibility() {
        boolean shouldBeVisible = dismissProgress != 0 || openProgress < 1 || forceBackgroundVisible;
        if (shouldBeVisible == isBackgroundVisible) {
            return;
        }

        if (activityVisibilityController != null) {
            activityVisibilityController.setHidden(!shouldBeVisible);
        }
        isBackgroundVisible = shouldBeVisible;
    }

    public interface ClosingViewProvider {
        void preLayout(long dialogId, Runnable runnable);
        SourceView getView(long dialogId);
    }

    private void openPremium() {
        if (previewView != null) {
            previewView.updatePauseReason(4, true);
        }
        if (captionEdit != null) {
            captionEdit.hidePeriodPopup();
        }
        PremiumFeatureBottomSheet sheet = new PremiumFeatureBottomSheet(new BaseFragment() {
            { currentAccount = StoryRecorder.this.currentAccount; }
            @Override
            public Dialog showDialog(Dialog dialog) {
                dialog.show();
                return dialog;
            }
            @Override
            public Activity getParentActivity() {
                return StoryRecorder.this.activity;
            }

            @Override
            public Theme.ResourcesProvider getResourceProvider() {
                return new WrappedResourceProvider(resourcesProvider) {
                    @Override
                    public void appendColors() {
                        sparseIntArray.append(Theme.key_dialogBackground, 0xFF1E1E1E);
                        sparseIntArray.append(Theme.key_windowBackgroundGray, 0xFF000000);
                    }
                };
            }

            @Override
            public boolean isLightStatusBar() {
                return false;
            }
        }, PremiumPreviewFragment.PREMIUM_FEATURE_STORIES, false);
        sheet.setOnDismissListener(d -> {
            if (previewView != null) {
                previewView.updatePauseReason(4, false);
            }
        });
        sheet.show();
    }

    private CharSequence premiumText(String text) {
        return AndroidUtilities.replaceSingleTag(text, Theme.key_chat_messageLinkIn, 0, this::openPremium, resourcesProvider);
    }

    private void showPremiumPeriodBulletin(int period) {
        final int hours = period / 3600;

        Bulletin.BulletinWindow.BulletinWindowLayout window = Bulletin.BulletinWindow.make(activity, new Bulletin.Delegate() {
            @Override
            public int getTopOffset(int tag) {
                return 0;
            }

            @Override
            public boolean clipWithGradient(int tag) {
                return true;
            }
        });
        WindowManager.LayoutParams params = window.getLayout();
        if (params != null) {
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.width = containerView.getWidth();
            params.y = (int) (containerView.getY() + AndroidUtilities.dp(56));
            window.updateLayout();
        }
        window.setTouchable(true);
        BulletinFactory.of(window, resourcesProvider)
            .createSimpleBulletin(R.raw.fire_on, premiumText(formatPluralString("StoryPeriodPremium", hours)), 3)
            .show(true);
    }

    public void setIconMuted(boolean muted, boolean animated) {
        if (muteButtonDrawable == null) {
            muteButtonDrawable = new RLottieDrawable(R.raw.media_mute_unmute, "media_mute_unmute", AndroidUtilities.dp(28), AndroidUtilities.dp(28), true, null);
            muteButtonDrawable.multiplySpeed(1.5f);
        }
        muteButton.setAnimation(muteButtonDrawable);
        if (!animated) {
            muteButtonDrawable.setCurrentFrame(muted ? 20 : 0, false);
            return;
        }
        if (muted) {
            if (muteButtonDrawable.getCurrentFrame() > 20) {
                muteButtonDrawable.setCurrentFrame(0, false);
            }
            muteButtonDrawable.setCustomEndFrame(20);
            muteButtonDrawable.start();
        } else {
            if (muteButtonDrawable.getCurrentFrame() == 0 || muteButtonDrawable.getCurrentFrame() >= 43) {
                return;
            }
            muteButtonDrawable.setCustomEndFrame(43);
            muteButtonDrawable.start();
        }
    }

    public StoryRecorder selectedPeerId(long dialogId) {
        this.selectedDialogId = dialogId;
        if (captionEdit != null) {
            captionEdit.setDialogId(dialogId);
        }
        return this;
    }

    public StoryRecorder canChangePeer(boolean b) {
        canChangePeer = b;
        return this;
    }

    public static CharSequence cameraBtnSpan(Context context) {
        SpannableString cameraStr = new SpannableString("c");
        Drawable cameraDrawable = context.getResources().getDrawable(R.drawable.story_camera).mutate();
        final int sz = AndroidUtilities.dp(35);
        cameraDrawable.setBounds(-sz / 4, -sz, sz / 4 * 3, 0);
        cameraStr.setSpan(new ImageSpan(cameraDrawable) {
            @Override
            public int getSize(@NonNull Paint paint, CharSequence text, int start, int end, @Nullable Paint.FontMetricsInt fm) {
                return super.getSize(paint, text, start, end, fm) / 3 * 2;
            }

            @Override
            public void draw(@NonNull Canvas canvas, CharSequence text, int start, int end, float x, int top, int y, int bottom, @NonNull Paint paint) {
                canvas.save();
                canvas.translate(0, (bottom - top) / 2 + dp(1));
                cameraDrawable.setAlpha(paint.getAlpha());
                super.draw(canvas, text, start, end, x, top, y, bottom, paint);
                canvas.restore();
            }
        }, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return cameraStr;
    }

    public ThanosEffect getThanosEffect() {
        if (!ThanosEffect.supports()) {
            return null;
        }
        if (thanosEffect == null) {
            windowView.addView(thanosEffect = new ThanosEffect(getContext(), () -> {
                ThanosEffect thisThanosEffect = thanosEffect;
                if (thisThanosEffect != null) {
                    thanosEffect = null;
                    windowView.removeView(thisThanosEffect);
                }
            }));
        }
        return thanosEffect;
    }

    private View changeDayNightView;
    private float changeDayNightViewProgress;
    private ValueAnimator changeDayNightViewAnimator;

    public ImageView getThemeButton() {
        if (themeButton == null) {
            themeButtonDrawable = new RLottieDrawable(R.raw.sun_outline, "" + R.raw.sun_outline, dp(28), dp(28), true, null);
            themeButtonDrawable.setPlayInDirectionOfCustomEndFrame(true);
            if (!(outputEntry != null && outputEntry.isDark)) {
                themeButtonDrawable.setCustomEndFrame(0);
                themeButtonDrawable.setCurrentFrame(0);
            } else {
                themeButtonDrawable.setCurrentFrame(35);
                themeButtonDrawable.setCustomEndFrame(36);
            }
            themeButtonDrawable.beginApplyLayerColors();
            int color = Theme.getColor(Theme.key_chats_menuName, resourcesProvider);
            themeButtonDrawable.setLayerColor("Sunny.**", color);
            themeButtonDrawable.setLayerColor("Path 6.**", color);
            themeButtonDrawable.setLayerColor("Path.**", color);
            themeButtonDrawable.setLayerColor("Path 5.**", color);
            themeButtonDrawable.commitApplyLayerColors();
            themeButton = new ImageView(getContext());
            themeButton.setScaleType(ImageView.ScaleType.CENTER);
            themeButton.setColorFilter(new PorterDuffColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY));
            themeButton.setBackground(Theme.createSelectorDrawable(0x20ffffff));
            themeButton.setOnClickListener(e -> {
                toggleTheme();
            });
//            themeButton.setOnLongClickListener(e -> {
//                openThemeSheet();
//                return true;
//            });
            themeButton.setVisibility(View.GONE);
            themeButton.setImageDrawable(themeButtonDrawable);
            themeButton.setAlpha(0f);
            actionBarButtons.addView(themeButton, 0, LayoutHelper.createLinear(46, 56, Gravity.TOP | Gravity.RIGHT));
        }
        return themeButton;
    }

    public void updateThemeButtonDrawable(boolean animated) {
        if (themeButtonDrawable != null) {
            if (animated) {
                themeButtonDrawable.setCustomEndFrame(outputEntry != null && outputEntry.isDark ? themeButtonDrawable.getFramesCount() : 0);
                if (themeButtonDrawable != null) {
                    themeButtonDrawable.start();
                }
            } else {
                int frame = outputEntry != null && outputEntry.isDark ? themeButtonDrawable.getFramesCount() - 1 : 0;
                themeButtonDrawable.setCurrentFrame(frame, false, true);
                themeButtonDrawable.setCustomEndFrame(frame);
                if (themeButton != null) {
                    themeButton.invalidate();
                }
            }
        }
    }

    public void toggleTheme() {
        if (outputEntry == null || changeDayNightView != null || themeButton == null || changeDayNightViewAnimator != null && changeDayNightViewAnimator.isRunning()) {
            return;
        }
        final boolean isDark = outputEntry.isDark;

        Bitmap bitmap = Bitmap.createBitmap(windowView.getWidth(), windowView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas bitmapCanvas = new Canvas(bitmap);
        themeButton.setAlpha(0f);
        if (previewView != null) {
            previewView.drawForThemeToggle = true;
        }
        if (paintView != null) {
            paintView.drawForThemeToggle = true;
        }
        windowView.draw(bitmapCanvas);
        if (previewView != null) {
            previewView.drawForThemeToggle = false;
        }
        if (paintView != null) {
            paintView.drawForThemeToggle = false;
        }
        themeButton.setAlpha(1f);

        Paint xRefPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xRefPaint.setColor(0xff000000);
        xRefPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint.setFilterBitmap(true);
        int[] position = new int[2];
        themeButton.getLocationInWindow(position);
        float x = position[0];
        float y = position[1];
        float cx = x + themeButton.getMeasuredWidth() / 2f;
        float cy = y + themeButton.getMeasuredHeight() / 2f;

        float r = Math.max(bitmap.getHeight(), bitmap.getWidth()) + AndroidUtilities.navigationBarHeight;

        Shader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        bitmapPaint.setShader(bitmapShader);
        changeDayNightView = new View(getContext()) {
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (isDark) {
                    if (changeDayNightViewProgress > 0f) {
                        bitmapCanvas.drawCircle(cx, cy, r * changeDayNightViewProgress, xRefPaint);
                    }
                    canvas.drawBitmap(bitmap, 0, 0, bitmapPaint);
                } else {
                    canvas.drawCircle(cx, cy, r * (1f - changeDayNightViewProgress), bitmapPaint);
                }
                canvas.save();
                canvas.translate(x, y);
                themeButton.draw(canvas);
                canvas.restore();
            }
        };
        changeDayNightView.setOnTouchListener((v, event) -> true);
        changeDayNightViewProgress = 0f;
        changeDayNightViewAnimator = ValueAnimator.ofFloat(0, 1f);
        changeDayNightViewAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            boolean changedNavigationBarColor = false;

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                changeDayNightViewProgress = (float) valueAnimator.getAnimatedValue();
                if (changeDayNightView != null) {
                    changeDayNightView.invalidate();
                }
                if (!changedNavigationBarColor && changeDayNightViewProgress > .5f) {
                    changedNavigationBarColor = true;
                }
            }
        });
        changeDayNightViewAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (changeDayNightView != null) {
                    if (changeDayNightView.getParent() != null) {
                        ((ViewGroup) changeDayNightView.getParent()).removeView(changeDayNightView);
                    }
                    changeDayNightView = null;
                }
                changeDayNightViewAnimator = null;
                super.onAnimationEnd(animation);
            }
        });
        changeDayNightViewAnimator.setStartDelay(80);
        changeDayNightViewAnimator.setDuration(isDark ? 320 : 450);
        changeDayNightViewAnimator.setInterpolator(isDark ? CubicBezierInterpolator.EASE_IN : CubicBezierInterpolator.EASE_OUT_QUINT);
        changeDayNightViewAnimator.start();

        windowView.addView(changeDayNightView, new ViewGroup.LayoutParams(LayoutHelper.MATCH_PARENT, LayoutHelper.MATCH_PARENT));

        AndroidUtilities.runOnUIThread(() -> {
            if (outputEntry == null) {
                return;
            }
            outputEntry.isDark = !outputEntry.isDark;
            if (previewView != null) {
                previewView.setupWallpaper(outputEntry, false);
            }
            if (paintView != null && paintView.entitiesView != null) {
                for (int i = 0; i < paintView.entitiesView.getChildCount(); ++i) {
                    View child = paintView.entitiesView.getChildAt(i);
                    if (child instanceof MessageEntityView) {
                        ((MessageEntityView) child).setupTheme(outputEntry);
                    }
                }
            }
            updateThemeButtonDrawable(true);
        });
    }

    public void setActionBarButtonVisible(View view, boolean visible, boolean animated) {
        if (view == null) return;
        if (animated) {
            view.setVisibility(View.VISIBLE);
            view.animate()
                .alpha(visible ? 1.0f : 0.0f)
                .setUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(@NonNull ValueAnimator animation) {
                        updateActionBarButtonsOffsets();
                    }
                })
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        updateActionBarButtonsOffsets();
                        if (!visible) {
                            view.setVisibility(View.GONE);
                        }
                    }
                })
                .setDuration(320)
                .setInterpolator(CubicBezierInterpolator.EASE_OUT_QUINT)
                .start();
        } else {
            view.animate().cancel();
            view.setVisibility(visible ? View.VISIBLE : View.GONE);
            view.setAlpha(visible ? 1.0f : 0.0f);
            updateActionBarButtonsOffsets();
        }
    }

    private boolean inCheck() {
        final float collageProgress = collageLayoutView.hasLayout() ? collageLayoutView.getFilledProgress() : 0.0f;
        return !animatedRecording && collageProgress >= 1.0f;
    }

    private void updateActionBarButtons(boolean animated) {
        showVideoTimer(currentPage == PAGE_CAMERA && mode == MODE_VIDEO && !collageListView.isVisible() && !inCheck(), animated);
        collageButton.setSelected(collageLayoutView.hasLayout());
        setActionBarButtonVisible(backButton, collageListView == null || !collageListView.isVisible(), animated);
        setActionBarButtonVisible(flashButton, !animatedRecording && mode != MODE_LIVE && currentPage == PAGE_CAMERA && flashButtonMode != null && !collageListView.isVisible() && !inCheck(), animated);
        setActionBarButtonVisible(dualButton, !animatedRecording && mode != MODE_LIVE && currentPage == PAGE_CAMERA && cameraView != null && cameraView.dualAvailable() && !collageListView.isVisible() && !collageLayoutView.hasLayout(), animated);
        setActionBarButtonVisible(collageButton, !animatedRecording && mode != MODE_LIVE && currentPage == PAGE_CAMERA && !collageListView.isVisible(), animated);
        setActionBarButtonVisible(collageRemoveButton, collageListView.isVisible(), animated);
        final float collageProgress = collageLayoutView.hasLayout() ? collageLayoutView.getFilledProgress() : 0.0f;
        recordControl.setCollageProgress(collageProgress, animated);
        removeCollageHint.show(collageListView.isVisible());
        animateRecording(animatedRecording, animated);

        AndroidUtilities.updateViewShow(liveSettingsButton, mode == MODE_LIVE && currentPage == PAGE_CAMERA);
        AndroidUtilities.updateViewShow(rotateButton, mode == MODE_LIVE && currentPage == PAGE_CAMERA);
    }

    private void updateActionBarButtonsOffsets() {
        float right = 0;
        collageRemoveButton.setTranslationX(-right); right += dp(46) * collageRemoveButton.getAlpha();
        dualButton.setTranslationX(-right);          right += dp(46) * dualButton.getAlpha();
        collageButton.setTranslationX(-right);       right += dp(46) * collageButton.getAlpha();
        flashButton.setTranslationX(-right);         right += dp(46) * flashButton.getAlpha();

        float left = 0;
        backButton.setTranslationX(left); left += dp(46) * backButton.getAlpha();

        collageListView.setBounds(left + dp(8), right + dp(8));
    }

    public void createCropEditor() {
        if (cropEditor != null) return;
        cropEditor = new CropEditor(getContext(), previewView, resourcesProvider) {
            @Override
            protected void close() {
                switchToEditMode(EDIT_MODE_NONE, true);
            }
        };
        windowView.addView(cropEditor.contentView);//, windowView.indexOfChild(containerView));
        windowView.addView(cropEditor);
    }

    public void createCropInlineEditor() {
        if (cropInlineEditor != null) return;
        cropInlineEditor = new CropInlineEditor(getContext(), previewView, resourcesProvider) {
            @Override
            protected void close() {
                switchToEditMode(EDIT_MODE_NONE, true);
            }
        };
        windowView.addView(cropInlineEditor.contentView);//, windowView.indexOfChild(containerView));
        windowView.addView(cropInlineEditor);
    }
}
