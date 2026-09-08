package com.exteragram.messenger.utils.ui;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.debug.DebugConfig;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.ChatActivityTopPanelLayout;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Components.chat.layouts.ChatActivityFadeView;

public abstract class ChatHeaderUiHelper {
    public static boolean isMaterial3ChatHeaderStyle() {
        return ExteraConfig.getNewChatHeaderStyle();
    }

    public static int getChatAvatarSizeDp() {
        return isMaterial3ChatHeaderStyle() ? 46 : 42;
    }

    public static int getAvatarInsetPx() {
        return !isMaterial3ChatHeaderStyle() ? 1 : 0;
    }

    public static int getAvatarSizePx(int i) {
        return AndroidUtilities.dp(i) - (getAvatarInsetPx() * 2);
    }

    public static int getAvatarRadius(int i, boolean z, boolean z2) {
        if (isMaterial3ChatHeaderStyle()) {
            return ExteraConfig.getAvatarCorners(i, false, z, z2);
        }
        return ExteraConfig.getAvatarCorners(getAvatarSizePx(i), true, z, z2);
    }

    public static int getChatFadeColorKey() {
        return DebugConfig.getChatFadeUseWhiteBackground() ? Theme.key_windowBackgroundWhite : Theme.key_windowBackgroundGray;
    }

    public static void setupGlassAvatarContainer(ChatAvatarContainer chatAvatarContainer) {
        chatAvatarContainer.setGlassMode();
        if (isMaterial3ChatHeaderStyle()) {
            chatAvatarContainer.setAvatarSizeInDp(46);
        }
    }

    public static void applyChatHeaderGlassStyle(ActionBar actionBar) {
        if (isMaterial3ChatHeaderStyle()) {
            actionBar.setDrawGlassMiddlePill(false);
            actionBar.setGlassShadowAlpha(0.0f);
        }
    }

    public static void setupChatTopFade(ChatActivityFadeView chatActivityFadeView, ActionBar actionBar, int i, int i2) {
        chatActivityFadeView.setFadeTopAlpha(actionBar.getVisibility() == 0 ? 255 : 0);
        int chatTopFadeZone = getChatTopFadeZone(i2);
        if (isMaterial3ChatHeaderStyle()) {
            chatActivityFadeView.setFadeZoneTop(getScaledChatTopFadeZone(actionBar, chatTopFadeZone));
            chatActivityFadeView.setTopFadeColor(i);
        } else {
            chatActivityFadeView.setFadeZoneTop(chatTopFadeZone);
        }
        chatActivityFadeView.setFadeHeightTop(getChatTopFadeHeight());
    }

    private static int getScaledChatTopFadeZone(ActionBar actionBar, int i) {
        int measuredHeight = actionBar.getMeasuredHeight();
        int chatTopFadeZone = getChatTopFadeZone(measuredHeight);
        return (measuredHeight == 0 || chatTopFadeZone >= i) ? i : chatTopFadeZone + Math.round((i - chatTopFadeZone) * 0.5f);
    }

    public static boolean isLightChatStatusBar(ActionBar actionBar, int i) {
        if (!isMaterial3ChatHeaderStyle() || actionBar.isActionModeShowed()) {
            i = actionBar.getBackgroundColor();
        }
        return AndroidUtilities.computePerceivedBrightness(i) > 0.721f;
    }

    public static int getAvatarContainerLeftMargin(boolean z) {
        if (z) {
            return 4;
        }
        return isMaterial3ChatHeaderStyle() ? 57 : 52;
    }

    public static float getTopPanelActionBarGapOffset(ChatActivityTopPanelLayout chatActivityTopPanelLayout) {
        if (!isMaterial3ChatHeaderStyle() || chatActivityTopPanelLayout == null) {
            return 0.0f;
        }
        return AndroidUtilities.dp(4.0f) * chatActivityTopPanelLayout.getMetadata().getTotalVisibility();
    }

    public static float getFinalTopPanelHeight(float f, ChatActivityTopPanelLayout chatActivityTopPanelLayout) {
        return f + getTopPanelActionBarGapOffset(chatActivityTopPanelLayout);
    }

    public static float getTopPanelTranslationY(float f, float f2, float f3) {
        return (f + AndroidUtilities.dp(isMaterial3ChatHeaderStyle() ? -1 : -5)) - (f2 * f3);
    }

    public static int getChatTopFadeHeight() {
        return AndroidUtilities.dp(isMaterial3ChatHeaderStyle() ? 78.0f : 48.0f);
    }

    public static int getChatTopFadeZone(int i) {
        return isMaterial3ChatHeaderStyle() ? i + AndroidUtilities.dp(42.0f) : i;
    }

    public static final class ProfileTransitionState {
        private float avatarTranslation;
        private float avatarStartY = Float.NaN;
        private float avatarSizeDp = 42.0f;
        private float nameTranslationX = Float.NaN;
        private float nameTranslationY = Float.NaN;
        private float onlineTranslationX = Float.NaN;
        private float onlineTranslationY = Float.NaN;

        public void reset() {
            this.avatarTranslation = 0.0f;
            this.avatarStartY = Float.NaN;
            this.avatarSizeDp = 42.0f;
            this.nameTranslationX = Float.NaN;
            this.nameTranslationY = Float.NaN;
            this.onlineTranslationX = Float.NaN;
            this.onlineTranslationY = Float.NaN;
        }

        public void capture(ChatAvatarContainer chatAvatarContainer, ViewGroup viewGroup, View view, View view2) {
            BackupImageView avatarImageView;
            if (chatAvatarContainer == null || viewGroup == null || (avatarImageView = chatAvatarContainer.getAvatarImageView()) == null) {
                return;
            }
            this.avatarTranslation = ViewPositionWatcher.computeXCoordinateInParent(avatarImageView, viewGroup);
            this.avatarStartY = ViewPositionWatcher.computeYCoordinateInParent(avatarImageView, viewGroup);
            int measuredWidth = avatarImageView.getMeasuredWidth() != 0 ? avatarImageView.getMeasuredWidth() : avatarImageView.getWidth();
            if (measuredWidth > 0) {
                this.avatarSizeDp = measuredWidth / AndroidUtilities.density;
            }
            if (chatAvatarContainer.getTitleTextView() != null && view != null) {
                this.nameTranslationX = ViewPositionWatcher.computeXCoordinateInParent(chatAvatarContainer.getTitleTextView(), viewGroup) - getTransitionLayoutLeft(view);
                this.nameTranslationY = ViewPositionWatcher.computeYCoordinateInParent(chatAvatarContainer.getTitleTextView(), viewGroup) - getTransitionLayoutTop(view);
            }
            View subtitleTextView = chatAvatarContainer.getSubtitleTextView();
            if (subtitleTextView == null || view2 == null) {
                return;
            }
            this.onlineTranslationX = ViewPositionWatcher.computeXCoordinateInParent(subtitleTextView, viewGroup) - getTransitionLayoutLeft(view2);
            this.onlineTranslationY = ViewPositionWatcher.computeYCoordinateInParent(subtitleTextView, viewGroup) - getTransitionLayoutTop(view2);
        }

        public float getAvatarTranslation() {
            return this.avatarTranslation;
        }

        public float getAvatarStartY(ActionBar actionBar) {
            if (!Float.isNaN(this.avatarStartY)) {
                return this.avatarStartY;
            }
            return (((actionBar.getOccupyStatusBar() ? AndroidUtilities.statusBarHeight : 0) + (ActionBar.getCurrentActionBarHeight() / 2.0f)) - AndroidUtilities.dp(this.avatarSizeDp / 2.0f)) + actionBar.getTranslationY();
        }

        public float getAvatarStartScale() {
            return this.avatarSizeDp / 100.0f;
        }

        public float getAvatarSizeDp() {
            return this.avatarSizeDp;
        }

        public float getNameTranslationX() {
            if (!Float.isNaN(this.nameTranslationX)) {
                return this.nameTranslationX;
            }
            return (this.avatarTranslation - AndroidUtilities.dp(109.0f)) + AndroidUtilities.dp(this.avatarSizeDp + 6.0f);
        }

        public float getNameTranslationY(ActionBar actionBar) {
            if (!Float.isNaN(this.nameTranslationY)) {
                return this.nameTranslationY;
            }
            return ((float) Math.floor(getAvatarStartY(actionBar))) + AndroidUtilities.dp(ChatHeaderUiHelper.isMaterial3ChatHeaderStyle() ? 1.66f : 1.3f);
        }

        public float getOnlineTranslationX() {
            if (!Float.isNaN(this.onlineTranslationX)) {
                return this.onlineTranslationX;
            }
            return getNameTranslationX();
        }

        public float getOnlineTranslationY(ActionBar actionBar) {
            if (!Float.isNaN(this.onlineTranslationY)) {
                return this.onlineTranslationY;
            }
            return ((float) Math.floor(getAvatarStartY(actionBar))) + AndroidUtilities.dp(ChatHeaderUiHelper.isMaterial3ChatHeaderStyle() ? 26.66f : 24.0f);
        }

        public void updateBadgePositionsFromCollapsedAvatar(View view, float f, ImageView imageView, ImageView imageView2, ImageView imageView3, ImageView imageView4) {
            float fDp = AndroidUtilities.dp(this.avatarSizeDp);
            float f2 = this.avatarSizeDp;
            updateBadgePositions(view, (fDp * ((f * 100.0f) / f2)) - AndroidUtilities.dp(f2), imageView, imageView2, imageView3, imageView4);
        }

        public void updateBadgePositionsFromExpandedAvatar(View view, float f, ImageView imageView, ImageView imageView2, ImageView imageView3, ImageView imageView4) {
            updateBadgePositions(view, (view.getMeasuredWidth() - AndroidUtilities.dp(this.avatarSizeDp)) * ((f * 100.0f) / this.avatarSizeDp), imageView, imageView2, imageView3, imageView4);
        }

        private void updateBadgePositions(View view, float f, ImageView imageView, ImageView imageView2, ImageView imageView3, ImageView imageView4) {
            if (imageView != null) {
                imageView.setTranslationX(view.getX() + AndroidUtilities.dp(this.avatarSizeDp - 26.0f) + f);
                imageView.setTranslationY(view.getY() + AndroidUtilities.dp(-10.0f) + f);
            }
            if (imageView2 != null) {
                imageView2.setTranslationX(view.getX() + AndroidUtilities.dp(this.avatarSizeDp - 14.0f) + f);
                imageView2.setTranslationY(view.getY() + AndroidUtilities.dp(this.avatarSizeDp - 15.5f) + f);
            }
            if (imageView3 != null) {
                imageView3.setTranslationX(view.getX() + AndroidUtilities.dp(this.avatarSizeDp - 14.0f) + f);
                imageView3.setTranslationY(view.getY() + AndroidUtilities.dp(this.avatarSizeDp - 18.0f) + f);
            }
            if (imageView4 != null) {
                imageView4.setTranslationX(view.getX() + AndroidUtilities.dp(this.avatarSizeDp - 14.0f) + f);
                imageView4.setTranslationY(view.getY() + AndroidUtilities.dp(this.avatarSizeDp - 18.0f) + f);
            }
        }

        private static int getTransitionLayoutLeft(View view) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                return ((ViewGroup.MarginLayoutParams) layoutParams).leftMargin;
            }
            return view.getLeft();
        }

        private static int getTransitionLayoutTop(View view) {
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
                return ((ViewGroup.MarginLayoutParams) layoutParams).topMargin;
            }
            return view.getTop();
        }
    }
}
