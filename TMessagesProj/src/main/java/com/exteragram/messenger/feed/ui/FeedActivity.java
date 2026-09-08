package com.exteragram.messenger.feed.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.feed.FeedConfig;
import com.exteragram.messenger.feed.FeedController;
import com.exteragram.messenger.utils.ui.MainTabsUiHelper;
import java.util.ArrayList;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.AlertDialog;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.INavigationLayout;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.ChatActivityContainer;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.ChatAvatarContainer;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.MainTabsActivity;

public class FeedActivity extends BaseFragment implements NotificationCenter.NotificationCenterDelegate, MainTabsActivity.TabFragmentDelegate {
    private ChatActivityContainer chatContainer;
    private boolean embeddedChatCreated;
    private boolean hasMainTabs;
    private int lastConfigGeneration;
    private WindowInsetsCompat lastWindowInsets;
    private final Runnable loadNewPosts;
    private Runnable parentTabsGlassInvalidationCallback;
    private boolean resumedOnce;
    private boolean uiActiveHeld;
    private boolean uiResumedHeld;
    private boolean viewportFullyVisible;

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean drawEdgeNavigationBar() {
        return false;
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isSupportEdgeToEdge() {
        return true;
    }

    public FeedActivity() {
        this(null);
    }

    public FeedActivity(Bundle bundle) {
        super(bundle);
        this.loadNewPosts = new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$new$0();
            }
        };
    }

    public static void presentFeed(BaseFragment baseFragment) {
        LaunchActivity launchActivity;
        if (!AndroidUtilities.isTablet() || (launchActivity = LaunchActivity.instance) == null || launchActivity.getRightActionBarLayout() == null) {
            if (baseFragment != null) {
                baseFragment.presentFragment(new FeedActivity());
                return;
            }
            return;
        }
        INavigationLayout rightActionBarLayout = LaunchActivity.instance.getRightActionBarLayout();
        if (rightActionBarLayout.getLastFragment() instanceof FeedActivity) {
            return;
        }
        if (!rightActionBarLayout.getFragmentStack().isEmpty()) {
            while (rightActionBarLayout.getFragmentStack().size() - 1 > 0) {
                rightActionBarLayout.removeFragmentFromStack(rightActionBarLayout.getFragmentStack().get(0));
            }
            rightActionBarLayout.closeLastFragment(false);
        }
        rightActionBarLayout.presentFragment(new INavigationLayout.NavigationParams(new FeedActivity()).setNoAnimation(true).forceRightLayout());
    }

    public WindowInsetsCompat lambda$createView$1(View view, WindowInsetsCompat windowInsetsCompat) {
        this.lastWindowInsets = windowInsetsCompat;
        int iDp = AndroidUtilities.dp(MainTabsUiHelper.getTabsViewHeightDp());
        if (iDp == 0) {
            return windowInsetsCompat;
        }
        Insets insets = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars());
        Insets insets2 = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.navigationBars());
        return new WindowInsetsCompat.Builder(windowInsetsCompat).setInsets(WindowInsetsCompat.Type.systemBars(), Insets.of(insets.left, insets.top, insets.right, insets.bottom + iDp)).setInsets(WindowInsetsCompat.Type.navigationBars(), Insets.of(insets2.left, insets2.top, insets2.right, insets2.bottom + iDp)).build();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onResume() {
        ChatActivityContainer chatActivityContainer;
        ChatActivity chatActivity;
        ChatActivity chatActivity2;
        View view;
        WindowInsetsCompat windowInsetsCompat;
        super.onResume();
        ChatActivityContainer chatActivityContainer2 = this.chatContainer;
        if (chatActivityContainer2 != null) {
            chatActivityContainer2.onResume();
            updateFeedViewportActive(this.viewportFullyVisible);
        }
        if (!this.uiResumedHeld) {
            this.uiResumedHeld = true;
            FeedController.getInstance(this.currentAccount).setUiResumed(true);
        }
        if (this.hasMainTabs && (view = this.fragmentView) != null && (windowInsetsCompat = this.lastWindowInsets) != null) {
            ViewCompat.dispatchApplyWindowInsets(view, windowInsetsCompat);
        }
        reattachCurrentFeedVideoTexture();
        int generation = FeedConfig.getInstance(this.currentAccount).getGeneration();
        if (generation != this.lastConfigGeneration) {
            this.lastConfigGeneration = generation;
            ChatActivityContainer chatActivityContainer3 = this.chatContainer;
            if (chatActivityContainer3 != null && (chatActivity2 = chatActivityContainer3.chatActivity) != null) {
                chatActivity2.applyFeedConfigChange();
            }
        } else if (this.resumedOnce && (chatActivityContainer = this.chatContainer) != null && (chatActivity = chatActivityContainer.chatActivity) != null) {
            chatActivity.reconcileFeedList();
            this.chatContainer.chatActivity.refreshFeedUnreadDivider();
            if (!FeedController.getInstance(this.currentAccount).getMessages().isEmpty()) {
                this.chatContainer.chatActivity.loadNewerFeed(true);
            }
        }
        this.resumedOnce = true;
        updateFeedSubtitle();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyVisible() {
        super.onBecomeFullyVisible();
        this.viewportFullyVisible = true;
        updateFeedViewportActive(true);
        reattachCurrentFeedVideoTexture();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onBecomeFullyHidden() {
        this.viewportFullyVisible = false;
        updateFeedViewportActive(false);
        super.onBecomeFullyHidden();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        if (this.hasMainTabs) {
            this.viewportFullyVisible = false;
            updateFeedViewportActive(false);
        }
        super.onTransitionAnimationStart(z, z2);
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        super.onTransitionAnimationEnd(z, z2);
        if (this.hasMainTabs) {
            this.viewportFullyVisible = z;
            updateFeedViewportActive(z);
        }
    }

    @Override // org.telegram.ui.MainTabsActivity.TabFragmentDelegate
    public void onParentBecomeFullyVisible() {
        reattachCurrentFeedVideoTexture();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public void onPause() {
        super.onPause();
        if (this.chatContainer != null) {
            updateFeedViewportActive(false);
            this.chatContainer.onPause();
        }
        if (this.uiResumedHeld) {
            this.uiResumedHeld = false;
            FeedController.getInstance(this.currentAccount).setUiResumed(false);
        }
    }

    private void updateFeedViewportActive(boolean z) {
        ChatActivity chatActivity;
        ChatActivityContainer chatActivityContainer = this.chatContainer;
        if (chatActivityContainer == null || (chatActivity = chatActivityContainer.chatActivity) == null) {
            return;
        }
        chatActivity.setFeedViewportActive(z);
    }

    @Override // org.telegram.ui.MainTabsActivity.TabFragmentDelegate
    public boolean canParentTabsSlide(MotionEvent motionEvent, boolean z) {
        ChatActivity chatActivity;
        ChatActivityContainer chatActivityContainer = this.chatContainer;
        return chatActivityContainer == null || (chatActivity = chatActivityContainer.chatActivity) == null || chatActivity.getActionBar() == null || !this.chatContainer.chatActivity.getActionBar().isActionModeShowed();
    }

    @Override // org.telegram.ui.ActionBar.BaseFragment
    public boolean isLightStatusBar() {
        ChatActivity chatActivity;
        ChatActivityContainer chatActivityContainer = this.chatContainer;
        if (chatActivityContainer != null && (chatActivity = chatActivityContainer.chatActivity) != null) {
            return chatActivity.isLightStatusBar();
        }
        return !Theme.isCurrentThemeDark();
    }

    private void reattachCurrentFeedVideoTexture() {
        ChatActivity chatActivity;
        ChatActivityContainer chatActivityContainer = this.chatContainer;
        if (chatActivityContainer == null || (chatActivity = chatActivityContainer.chatActivity) == null) {
            return;
        }
        chatActivity.reattachCurrentFeedVideoTexture();
    }

    public void setupChatActionBar() {
        ChatActivity chatActivity;
        final ActionBar actionBar;
        ChatActivityContainer chatActivityContainer = this.chatContainer;
        if (chatActivityContainer == null || (chatActivity = chatActivityContainer.chatActivity) == null || (actionBar = chatActivity.getActionBar()) == null) {
            return;
        }
        ActionBarMenu actionBarMenuCreateMenu = actionBar.createMenu();
        if (actionBarMenuCreateMenu.getItem(76) == null) {
            actionBarMenuCreateMenu.addItem(76, R.drawable.msg_markread, this.chatContainer.chatActivity.themeDelegate).setContentDescription(LocaleController.getString(R.string.FeedMarkAllRead));
        }
        if (actionBarMenuCreateMenu.getItem(75) == null) {
            actionBarMenuCreateMenu.addItem(75, R.drawable.msg_settings, this.chatContainer.chatActivity.themeDelegate).setContentDescription(LocaleController.getString(R.string.FeedSettings));
        }
        if (this.hasMainTabs) {
            applyMainTabsHeaderLayout();
        }
        final ActionBar.ActionBarMenuOnItemClick actionBarMenuOnItemClick = actionBar.getActionBarMenuOnItemClick();
        actionBar.setActionBarMenuOnItemClick(new ActionBar.ActionBarMenuOnItemClick() { 
            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public void onItemClick(int i) {
                if (i == -1 && FeedActivity.this.hasMainTabs && !actionBar.isActionModeShowed()) {
                    return;
                }
                if (i == 76) {
                    FeedActivity.this.showMarkAllReadDialog();
                    return;
                }
                if (i == 75) {
                    FeedActivity.this.presentFragment(new FeedChannelsActivity());
                    return;
                }
                ActionBar.ActionBarMenuOnItemClick actionBarMenuOnItemClick2 = actionBarMenuOnItemClick;
                if (actionBarMenuOnItemClick2 != null) {
                    actionBarMenuOnItemClick2.onItemClick(i);
                }
            }

            @Override // org.telegram.ui.ActionBar.ActionBar.ActionBarMenuOnItemClick
            public boolean canOpenMenu() {
                ActionBar.ActionBarMenuOnItemClick actionBarMenuOnItemClick2 = actionBarMenuOnItemClick;
                return actionBarMenuOnItemClick2 == null || actionBarMenuOnItemClick2.canOpenMenu();
            }
        });
    }

    public void applyFloatingWindowLayout() {
        ChatActivityContainer chatActivityContainer;
        ChatActivity chatActivity;
        if (getParentLayout() == null || !getParentLayout().isLayersLayout() || (chatActivityContainer = this.chatContainer) == null || (chatActivity = chatActivityContainer.chatActivity) == null) {
            return;
        }
        if (chatActivity.getActionBar() != null) {
            chatActivity.getActionBar().setOccupyStatusBar(false);
        }
        ChatAvatarContainer chatAvatarContainer = chatActivity.avatarContainer;
        if (chatAvatarContainer != null) {
            chatAvatarContainer.setOccupyStatusBar(false);
        }
        ChatActivity.ChatActivityFragmentView chatActivityFragmentView = chatActivity.contentView;
        if (chatActivityFragmentView != null) {
            chatActivityFragmentView.setOccupyStatusBar(false);
        }
    }

    private void applyMainTabsHeaderLayout() {
        ChatActivity chatActivity;
        ChatAvatarContainer chatAvatarContainer;
        ChatActivityContainer chatActivityContainer = this.chatContainer;
        if (chatActivityContainer == null || (chatActivity = chatActivityContainer.chatActivity) == null || (chatAvatarContainer = chatActivity.avatarContainer) == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = chatAvatarContainer.getLayoutParams();
        if (layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) layoutParams;
            int iDp = AndroidUtilities.dp(ExteraConfig.getNewChatHeaderStyle() ? 12.0f : 0.0f);
            if (marginLayoutParams.leftMargin != iDp) {
                marginLayoutParams.leftMargin = iDp;
                this.chatContainer.chatActivity.avatarContainer.setLayoutParams(marginLayoutParams);
            }
        }
    }

    public void showMarkAllReadDialog() {
        if (getParentActivity() == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getParentActivity(), getResourceProvider());
        builder.setTitle(LocaleController.getString(R.string.FeedMarkAllRead));
        builder.setMessage(LocaleController.getString(R.string.FeedMarkAllReadConfirm));
        builder.setPositiveButton(LocaleController.getString(R.string.MarkAsRead), new AlertDialog.OnButtonClickListener() { 
            @Override // org.telegram.ui.ActionBar.AlertDialog.OnButtonClickListener
            public final void onClick(AlertDialog alertDialog, int i) {
                this.f$0.lambda$showMarkAllReadDialog$2(alertDialog, i);
            }
        });
        builder.setNegativeButton(LocaleController.getString(R.string.Cancel), null);
        showDialog(builder.create());
    }

    public void lambda$updateFeedSubtitle$3(ArrayList arrayList, int i, boolean z, int i2) {
        if (z) {
            return;
        }
        setFeedSubtitle(i);
    }

    private void setFeedSubtitle(int i) {
        ChatActivity chatActivity;
        ChatAvatarContainer chatAvatarContainer;
        ChatActivityContainer chatActivityContainer = this.chatContainer;
        if (chatActivityContainer == null || (chatActivity = chatActivityContainer.chatActivity) == null || (chatAvatarContainer = chatActivity.avatarContainer) == null) {
            return;
        }
        chatAvatarContainer.setSubtitle(LocaleController.formatPluralString("Channels", i, new Object[0]));
        View subtitleTextView = this.chatContainer.chatActivity.avatarContainer.getSubtitleTextView();
        if (subtitleTextView != null) {
            subtitleTextView.setVisibility(0);
        }
    }

    @Override // org.telegram.ui.MainTabsActivity.TabFragmentDelegate
    public BlurredBackgroundSourceRenderNode getGlassSource() {
        ChatActivity chatActivity;
        ChatActivityContainer chatActivityContainer = this.chatContainer;
        if (chatActivityContainer == null || (chatActivity = chatActivityContainer.chatActivity) == null) {
            return null;
        }
        return chatActivity.getGlassSource();
    }

    @Override // org.telegram.ui.MainTabsActivity.TabFragmentDelegate
    public void setParentTabsGlassInvalidationCallback(Runnable runnable) {
        this.parentTabsGlassInvalidationCallback = runnable;
    }

    public void invalidateParentTabsGlass() {
        Runnable runnable = this.parentTabsGlassInvalidationCallback;
        if (runnable != null) {
            runnable.run();
        }
    }
}
