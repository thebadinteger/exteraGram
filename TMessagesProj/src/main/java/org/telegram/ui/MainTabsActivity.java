package org.telegram.ui;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.core.math.MathUtils;
import androidx.core.view.WindowInsetsCompat;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.MainMenuItem;
import com.exteragram.messenger.config.BottomNavigationBar;
import com.exteragram.messenger.feed.FeedController;
import com.exteragram.messenger.feed.ui.FeedActivity;
import com.exteragram.messenger.feed.ui.FeedChannelsActivity;
import com.exteragram.messenger.utils.chats.ChatUtils;
import com.exteragram.messenger.utils.chats.MainMenuHelper;
import com.exteragram.messenger.utils.ui.MainTabsUiHelper;
import com.exteragram.messenger.utils.ui.UIUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.function.IntPredicate;
import me.vkryl.android.animator.BoolAnimator;
import me.vkryl.android.animator.FactorAnimator;
import org.telegram.messenger.AccountInstance;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.LiteMode;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.ThemeDescription;
import org.telegram.ui.Components.AvatarDrawable;
import org.telegram.ui.Components.BackupImageView;
import org.telegram.ui.Components.Bulletin;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.CubicBezierInterpolator;
import org.telegram.ui.Components.HintsController;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.LayoutHelper;
import org.telegram.ui.Components.Premium.LimitReachedBottomSheet;
import org.telegram.ui.Components.ProxyDrawable;
import org.telegram.ui.Components.blur3.BlurredBackgroundDrawableViewFactory;
import org.telegram.ui.Components.blur3.BlurredBackgroundWithFadeDrawable;
import org.telegram.ui.Components.blur3.RenderNodeWithHash;
import org.telegram.ui.Components.blur3.capture.IBlur3Hash;
import org.telegram.ui.Components.blur3.drawable.BlurredBackgroundDrawable;
import org.telegram.ui.Components.blur3.drawable.color.BlurredBackgroundColorProvider;
import org.telegram.ui.Components.blur3.drawable.color.impl.BlurredBackgroundProviderImpl;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSource;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceColor;
import org.telegram.ui.Components.blur3.source.BlurredBackgroundSourceRenderNode;
import org.telegram.ui.Components.chat.ViewPositionWatcher;
import org.telegram.ui.Components.glass.GlassTabView;
import org.telegram.ui.Stories.recorder.HintView2;

public class MainTabsActivity extends ViewPagerActivity implements NotificationCenter.NotificationCenterDelegate, FactorAnimator.Target {
    private HintView2 accountSwitchHint;
    private boolean accountSwitchHintShown;
    private final BoolAnimator animatorTabsVisible;
    private int currentConnectionState;
    private DialogsActivity dialogsActivity;
    private boolean dropCallsFragmentAfterPageScroll;
    private View fadeView;
    private final RectF fragmentPosition;
    private NotificationCenter.ObserversGroup globalObserversGroup;
    private BlurredBackgroundSourceColor iBlur3SourceColor;
    private BlurredBackgroundSourceRenderNode iBlur3SourceTabGlass;
    private int navigationBarHeight;
    private NotificationCenter.ObserversGroup observersGroup;
    private ProxyDrawable proxyDrawable;
    private ActionBarMenuSubItem proxyMenuSubItem;
    private boolean tabletLayout;
    public GlassTabView[] tabs;
    private MainTabsLayout tabsView;
    private BlurredBackgroundDrawable tabsViewBackground;
    private FrameLayout tabsViewWrapper;
    private IUpdateLayout updateLayout;
    private UpdateLayoutWrapper updateLayoutWrapper;
    private ViewPositionWatcher viewPositionWatcher;

    public interface TabFragmentDelegate {
        default boolean canParentTabsSlide(MotionEvent motionEvent, boolean z) {
            return false;
        }

        BlurredBackgroundSourceRenderNode getGlassSource();

        default void onParentBecomeFullyVisible() {
        }

        void onParentScrollToTop();

        default void setParentTabsGlassInvalidationCallback(Runnable runnable) {
        }
    }

    public static void lambda$showFiltersMenu$20(ItemOptions itemOptions, int i, View view) {
        itemOptions.dismiss();
        this.dialogsActivity.switchToFilter(i);
    }

    public void lambda$showAccountChangeHint$29() {
        GlassTabView[] glassTabViewArr;
        if (getContext() == null || (glassTabViewArr = this.tabs) == null) {
            return;
        }
        GlassTabView glassTabView = glassTabViewArr[4];
        float width = ((this.contentView.getWidth() - ((this.tabsView.getX() + glassTabView.getX()) + glassTabView.getWidth())) + (glassTabView.getWidth() / 2.0f)) / AndroidUtilities.density;
        HintView2 hintView2 = new HintView2(getContext(), 3);
        this.accountSwitchHint = hintView2;
        hintView2.setTranslationY((-this.navigationBarHeight) + AndroidUtilities.dp(4.0f));
        this.accountSwitchHint.setPadding(AndroidUtilities.dp(7.33f), 0, AndroidUtilities.dp(7.33f), 0);
        this.accountSwitchHint.setMultilineText(false);
        this.accountSwitchHint.setCloseButton(true);
        this.accountSwitchHint.setText(LocaleController.getString(R.string.SwitchAccountHint));
        this.accountSwitchHint.setJoint(1.0f, (-width) + 7.33f);
        this.contentView.addView(this.accountSwitchHint, LayoutHelper.createFrame(-1, 100.0f, 87, 0.0f, 0.0f, 0.0f, MainTabsUiHelper.getTabsViewHeightDp()));
        this.accountSwitchHint.setOnHiddenListener(new Runnable() { // from class: org.telegram.ui.MainTabsActivity$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$showAccountChangeHint$28();
            }
        });
        this.accountSwitchHint.setDuration(8000L);
        this.accountSwitchHint.show();
        HintsController.Hint.AccountSwitchHint.increment();
    }

    public /* synthetic */ void lambda$showAccountChangeHint$28() {
        AndroidUtilities.removeFromParent(this.accountSwitchHint);
    }

    public void invalidateTabsGlass() {
        BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode;
        if (Build.VERSION.SDK_INT < 31 || (blurredBackgroundSourceRenderNode = this.iBlur3SourceTabGlass) == null) {
            return;
        }
        blurredBackgroundSourceRenderNode.invalidateDisplayList();
        blur3_invalidateBlur();
        MainTabsLayout mainTabsLayout = this.tabsView;
        if (mainTabsLayout != null) {
            mainTabsLayout.invalidate();
        }
        FrameLayout frameLayout = this.tabsViewWrapper;
        if (frameLayout != null) {
            frameLayout.invalidate();
        }
    }

    public void blur3_invalidateBlur() {
        View view;
        if (Build.VERSION.SDK_INT < 31 || this.iBlur3SourceTabGlass == null || (view = this.fragmentView) == null) {
            return;
        }
        this.iBlur3SourceTabGlass.setSize(view.getMeasuredWidth(), this.fragmentView.getMeasuredHeight());
        this.iBlur3SourceTabGlass.updateDisplayListIfNeeded();
    }

    public void blur3_updateColors() {
        this.iBlur3SourceColor.setColor(getThemedColor(Theme.key_windowBackgroundWhite));
        BlurredBackgroundDrawable blurredBackgroundDrawable = this.tabsViewBackground;
        if (blurredBackgroundDrawable != null) {
            blurredBackgroundDrawable.updateColors();
        }
        blur3_invalidateBlur();
        View view = this.fadeView;
        if (view != null) {
            view.invalidate();
        }
        MainTabsLayout mainTabsLayout = this.tabsView;
        if (mainTabsLayout != null) {
            mainTabsLayout.invalidate();
        }
        GlassTabView[] glassTabViewArr = this.tabs;
        if (glassTabViewArr != null) {
            for (GlassTabView glassTabView : glassTabViewArr) {
                glassTabView.updateColorsLottie();
            }
        }
    }
}
