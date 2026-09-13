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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private int getPositionChats() {
        return 0;
    }

    private int getPositionContacts() {
        return 1;
    }

    public void updateLayout() {
    }

    public int getTabsCount() {
        return hasContactsOrFeedTab() ? 4 : 3;
    }

    private boolean isFeedTabEnabled() {
        return ExteraConfig.getShowFeedTab();
    }

    private boolean hasContactsOrFeedTab() {
        return getUserConfig().showContactsTab || isFeedTabEnabled();
    }

    private int getPositionCallsOrSettings() {
        return hasContactsOrFeedTab() ? 2 : 1;
    }

    private int getPositionProfile() {
        return hasContactsOrFeedTab() ? 3 : 2;
    }

    private int indexToPosition(int i) {
        if (i == 0) {
            return getPositionChats();
        }
        if (i == 1 || i == 5) {
            return getPositionContacts();
        }
        if (i == 2 || i == 3) {
            return getPositionCallsOrSettings();
        }
        if (i == 4) {
            return getPositionProfile();
        }
        return 0;
    }

    public MainTabsActivity() {
        this(null);
    }

    public MainTabsActivity(Bundle bundle) {
        this.animatorTabsVisible = new BoolAnimator(0, this, CubicBezierInterpolator.EASE_OUT_QUINT, 380L, true);
        this.fragmentPosition = new RectF();
        this.arguments = bundle;
        initBlurSources();
    }

    private void initBlurSources() {
        if (Build.VERSION.SDK_INT >= 31) {
            BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode = new BlurredBackgroundSourceRenderNode(null);
            this.iBlur3SourceTabGlass = blurredBackgroundSourceRenderNode;
            blurredBackgroundSourceRenderNode.setupRenderer(new RenderNodeWithHash.Renderer() {
                @Override
                public void renderNodeCalculateHash(IBlur3Hash iBlur3Hash) {
                    iBlur3Hash.add(MainTabsActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    iBlur3Hash.add(SharedConfig.chatBlurEnabled());
                    int size = MainTabsActivity.this.fragmentsArr.size();
                    for (int i = 0; i < size; i++) {
                        BaseFragment baseFragment = MainTabsActivity.this.fragmentsArr.valueAt(i).fragment;
                        View view = baseFragment.fragmentView;
                        if (view != null) {
                            MainTabsActivity mainTabsActivity = MainTabsActivity.this;
                            if (ViewPositionWatcher.computeRectInParent(view, mainTabsActivity.contentView, mainTabsActivity.fragmentPosition) && MainTabsActivity.this.fragmentPosition.right > 0.0f && MainTabsActivity.this.fragmentPosition.left < MainTabsActivity.this.fragmentView.getMeasuredWidth() && (baseFragment instanceof TabFragmentDelegate) && ((TabFragmentDelegate) baseFragment).getGlassSource() != null) {
                                iBlur3Hash.addF(MainTabsActivity.this.fragmentPosition.left);
                                iBlur3Hash.addF(MainTabsActivity.this.fragmentPosition.top);
                                iBlur3Hash.add(baseFragment.getClassGuid());
                            }
                        }
                    }
                }

                @Override
                public void renderNodeUpdateDisplayList(Canvas canvas) {
                    BlurredBackgroundSourceRenderNode glassSource;
                    Canvas canvas2;
                    int measuredWidth = MainTabsActivity.this.fragmentView.getMeasuredWidth();
                    int measuredHeight = MainTabsActivity.this.fragmentView.getMeasuredHeight();
                    canvas.drawColor(MainTabsActivity.this.getThemedColor(Theme.key_windowBackgroundWhite));
                    int size = MainTabsActivity.this.fragmentsArr.size();
                    int i = 0;
                    while (i < size) {
                        BaseFragment baseFragment = MainTabsActivity.this.fragmentsArr.valueAt(i).fragment;
                        View view = baseFragment.fragmentView;
                        if (view == null) {
                            canvas2 = canvas;
                        } else {
                            MainTabsActivity mainTabsActivity = MainTabsActivity.this;
                            if (ViewPositionWatcher.computeRectInParent(view, mainTabsActivity.contentView, mainTabsActivity.fragmentPosition) && MainTabsActivity.this.fragmentPosition.right > 0.0f && MainTabsActivity.this.fragmentPosition.left < MainTabsActivity.this.fragmentView.getMeasuredWidth() && (baseFragment instanceof TabFragmentDelegate) && (glassSource = ((TabFragmentDelegate) baseFragment).getGlassSource()) != null) {
                                canvas.save();
                                canvas.translate(MainTabsActivity.this.fragmentPosition.left, MainTabsActivity.this.fragmentPosition.top);
                                canvas2 = canvas;
                                glassSource.draw(canvas2, 0.0f, 0.0f, measuredWidth, measuredHeight);
                                canvas2.restore();
                            } else {
                                canvas2 = canvas;
                            }
                        }
                        i++;
                        canvas = canvas2;
                    }
                }
            });
        } else {
            this.iBlur3SourceTabGlass = null;
        }
        this.iBlur3SourceColor = new BlurredBackgroundSourceColor();
        Bulletin.Delegate delegate = new Bulletin.Delegate() {
            @Override
            public int getBottomOffset(int i) {
                return MainTabsActivity.this.navigationBarHeight + (MainTabsActivity.this.isBottomTabsEnabled() ? Math.round(AndroidUtilities.dp(MainTabsUiHelper.getTabsFabOffsetDp()) * MainTabsActivity.this.animatorTabsVisible.getFloatValue()) : 0);
            }

            @Override
            public boolean bottomOffsetAnimated() {
                return !BottomNavigationBar.floating();
            }
        };
        Bulletin.addDelegate(this, delegate);
        Bulletin.addDelegate(this.contentView, delegate);
    }

    @Override
    public int getNavigationBarColor() {
        return getThemedColor(Theme.key_windowBackgroundWhite);
    }

    @Override
    public FrameLayout createContentView(Context context) {
        return new FrameLayout(context) {
            @Override
            protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
                super.onLayout(z, i, i2, i3, i4);
                MainTabsActivity.this.checkUi_tabsPosition();
                MainTabsActivity.this.checkUi_fadeView();
            }

            @Override
            public void dispatchDraw(Canvas canvas) {
                super.dispatchDraw(canvas);
                MainTabsActivity.this.blur3_invalidateBlur();
            }
        };
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateLayout();
    }

    @Override
    public void onResume() {
        super.onResume();
        AndroidUtilities.setLightNavigationBar(getParentActivity(), AndroidUtilities.computePerceivedBrightness(getNavigationBarColor()) >= 0.721f);
        blur3_updateColors();
        checkContactsTabBadge();
        checkUnreadCount(true);
        showAccountChangeHint();
    }

    private void checkContactsTabBadge() {
        GlassTabView glassTabView;
        if (this.tabsView == null || (glassTabView = this.tabs[1]) == null) {
            return;
        }
        boolean z = Build.VERSION.SDK_INT >= 23 && ContactsController.hasContactsPermission();
        if (z) {
            MessagesController.getGlobalNotificationsSettings().edit().putBoolean("askAboutContacts2", true).apply();
        }
        if (Build.VERSION.SDK_INT >= 23 && UserConfig.getInstance(this.currentAccount).syncContacts && !z && MessagesController.getGlobalNotificationsSettings().getBoolean("askAboutContacts2", true)) {
            glassTabView.setCounter("!", true, true);
        } else {
            glassTabView.setCounter(null, true, true);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        HintView2 hintView2 = this.accountSwitchHint;
        if (hintView2 != null) {
            hintView2.hide();
        }
    }

    @Override
    public View createView(Context context) {
        Bulletin.removeDelegate(this);
        FrameLayout frameLayout = this.contentView;
        if (frameLayout != null) {
            Bulletin.removeDelegate(frameLayout);
        }
        super.createView(context);
        contentView.setBackgroundColor(getThemedColor(Theme.key_windowBackgroundWhite));
        this.tabletLayout = false;
        MainTabsLayout mainTabsLayout = new MainTabsLayout(context, this.resourceProvider);
        this.tabsView = mainTabsLayout;
        mainTabsLayout.setClipChildren(false);
        MainTabsUiHelper.applyTabsLayoutStyle(this.tabsView);
        GlassTabView[] glassTabViewArr = new GlassTabView[6];
        this.tabs = glassTabViewArr;
        glassTabViewArr[0] = GlassTabView.createMainNavigationTab(context, this.resourceProvider, GlassTabView.TabAnimation.CHATS, R.string.MainTabsChats);
        this.tabs[0].setOnLongClickListener(view -> showFiltersAndFolders(view));
        this.tabs[1] = GlassTabView.createMainNavigationTab(context, this.resourceProvider, GlassTabView.TabAnimation.CONTACTS, R.string.MainTabsContacts);
        this.tabs[1].setOnLongClickListener(view -> {
            ItemOptions.makeOptions(this, view).add(R.drawable.msg_addcontact, LocaleController.getString(R.string.NewContactTitle), () -> {
                new NewContactBottomSheet(this, getContext()).show();
            }).add(R.drawable.msg_calls, LocaleController.getString(R.string.Calls), () -> {
                Bundle bundle = new Bundle();
                bundle.putBoolean("needFinishFragment", false);
                presentFragment(new CallLogActivity(bundle));
            }).add(R.drawable.msg_archive_hide, LocaleController.getString(R.string.HideContactsTab), () -> {
                getUserConfig().setShowContactsTab(null, false);
            }).setGravity(1).translate(0.0f, -AndroidUtilities.dp(4.0f)).setScrimViewBackground(MainTabsUiHelper.createMainTabsScrimBackground(this.resourceProvider, false)).setDiscardScrolls(false).setDismissOnMoveOutside(true).show();
            return true;
        });
        this.tabs[2] = GlassTabView.createMainNavigationTab(context, this.resourceProvider, GlassTabView.TabAnimation.SETTINGS, R.string.Settings);
        this.tabs[2].setOnLongClickListener(view -> {
            openSettingsTabOptions(view);
            return true;
        });
        this.tabs[3] = GlassTabView.createMainNavigationTab(context, this.resourceProvider, GlassTabView.TabAnimation.CALLS, R.string.MainTabsCalls);
        this.tabs[4] = GlassTabView.createMainNavigationAvatar(context, this.resourceProvider, this.currentAccount, R.string.MainTabsProfile);
        this.tabs[3].setOnLongClickListener(view -> openCallsSelector(view));
        this.tabs[4].setOnLongClickListener(view -> openAccountSelector(view));
        this.tabs[5] = GlassTabView.createMainNavigationTab(context, this.resourceProvider, GlassTabView.TabAnimation.FEED, R.string.Feed);
        this.tabs[5].setOnLongClickListener(view -> {
            ItemOptions.makeOptions(this, view).add(R.drawable.msg_archive_hide, LocaleController.getString(R.string.HideFeedTab), () -> {
                ExteraConfig.setShowFeedTab(false);
                NotificationCenter.getInstance(this.currentAccount).postNotificationNameOnUIThread(NotificationCenter.feedTabVisibleToggled);
            }).add(R.drawable.msg_markread, LocaleController.getString(R.string.FeedMarkAllRead), () -> {
                ViewPagerActivity.FragmentState fragmentState = this.fragmentsArr.get(getPositionContacts());
                if (fragmentState != null) {
                    BaseFragment baseFragment = fragmentState.fragment;
                    if (baseFragment instanceof FeedActivity) {
                        ((FeedActivity) baseFragment).markAllRead();
                    } else {
                        FeedController.getInstance(this.currentAccount).markAllRead();
                    }
                } else {
                    FeedController.getInstance(this.currentAccount).markAllRead();
                }
                checkUnreadCount(true);
            }).add(R.drawable.msg_settings, LocaleController.getString(R.string.FeedSettings), () -> {
                presentFragment(new FeedChannelsActivity());
            }).setGravity(1).translate(0.0f, -AndroidUtilities.dp(4.0f)).setScrimViewBackground(MainTabsUiHelper.createMainTabsScrimBackground(this.resourceProvider, false)).setDiscardScrolls(false).setDismissOnMoveOutside(true).show();
            return true;
        });
        this.tabsView.addTabToIgnoreClick(this.tabs[0]);
        this.tabsView.addTabToIgnoreClick(this.tabs[1]);
        this.tabsView.addTabToIgnoreClick(this.tabs[4]);
        this.tabsView.addTabToIgnoreClick(this.tabs[3]);
        this.tabsView.addTabToIgnoreClick(this.tabs[5]);
        for (int i = 0; i < this.tabs.length; i++) {
            final int tabIndex = i;
            this.tabs[i].setOnClickListener(view -> onTabClicked(tabIndex, view));
        }
        int[] iArr = {0, 5, 1, 2, 3, 4};
        for (int i2 = 0; i2 < 6; i2++) {
            int i3 = iArr[i2];
            this.tabsView.addView(this.tabs[i3]);
            this.tabsView.setViewVisible(this.tabs[i3], true, false);
        }
        checkUi_contactsOrFeedTabVisible(false);
        checkUi_callTabVisible(getUserConfig().showCallsTab, false);
        selectTab(this.viewPager.getCurrentPosition(), false);
        this.iBlur3SourceColor.setColor(getThemedColor(Theme.key_windowBackgroundWhite));
        ViewPositionWatcher viewPositionWatcher = this.viewPositionWatcher;
        if (viewPositionWatcher != null) {
            viewPositionWatcher.shutdown();
        }
        this.viewPositionWatcher = new ViewPositionWatcher(this.contentView);
        BlurredBackgroundSource blurredBackgroundSource = this.iBlur3SourceTabGlass;
        if (blurredBackgroundSource == null) {
            blurredBackgroundSource = this.iBlur3SourceColor;
        }
        BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory = new BlurredBackgroundDrawableViewFactory(blurredBackgroundSource);
        BlurredBackgroundDrawable blurredBackgroundDrawableCreate = blurredBackgroundDrawableViewFactory.create(this.tabsView, BlurredBackgroundProviderImpl.mainTabs(this.resourceProvider));
        this.tabsViewBackground = blurredBackgroundDrawableCreate;
        blurredBackgroundDrawableCreate.setRadius(MainTabsUiHelper.getBackgroundRadius());
        this.tabsViewBackground.setPadding(MainTabsUiHelper.getBackgroundInset());
        this.tabsView.setBackground(this.tabsViewBackground);
        BlurredBackgroundDrawableViewFactory blurredBackgroundDrawableViewFactory2 = new BlurredBackgroundDrawableViewFactory(this.iBlur3SourceColor);
        blurredBackgroundDrawableViewFactory2.setSourceRootView(this.viewPositionWatcher, this.contentView);
        this.fadeView = new View(context);
        BlurredBackgroundWithFadeDrawable blurredBackgroundWithFadeDrawable = new BlurredBackgroundWithFadeDrawable(blurredBackgroundDrawableViewFactory2.create(this.fadeView, (BlurredBackgroundColorProvider) null));
        blurredBackgroundWithFadeDrawable.setFadeHeight(AndroidUtilities.dp(60.0f), true);
        this.fadeView.setBackground(blurredBackgroundWithFadeDrawable);
        this.contentView.addView(this.fadeView, LayoutHelper.createFrame(-1, 0, 80));
        this.tabsViewWrapper = new FrameLayout(context);
        this.contentView.setClipChildren(false);
        this.tabsViewWrapper.setOnTouchListener((view2, motionEvent) -> {
            float rawX = motionEvent.getRawX();
            float rawY = motionEvent.getRawY();
            int[] iArr2 = new int[2];
            MainTabsActivity.this.tabsView.getLocationOnScreen(iArr2);
            int i4 = iArr2[0];
            int i5 = iArr2[1];
            if (rawX < i4 || rawX > i4 + MainTabsActivity.this.tabsView.getWidth() || rawY < i5 || rawY > i5 + MainTabsActivity.this.tabsView.getHeight()) {
                return false;
            }
            motionEvent.offsetLocation(-i4, -i5);
            return MainTabsActivity.this.tabsView.dispatchTouchEvent(motionEvent);
        });
        this.tabsViewWrapper.addView(this.tabsView, LayoutHelper.createFrame(-1, MainTabsUiHelper.getTabsViewHeightDp(), 81));
        this.tabsViewWrapper.setClipToPadding(false);
        this.contentView.addView(this.tabsViewWrapper, LayoutHelper.createFrame(-1, -2, 80));
        UpdateLayoutWrapper updateLayoutWrapper = new UpdateLayoutWrapper(context);
        this.updateLayoutWrapper = updateLayoutWrapper;
        this.contentView.addView(updateLayoutWrapper, LayoutHelper.createFrame(-1, -2, 80));
        IUpdateLayout iUpdateLayoutTakeUpdateLayout = org.telegram.messenger.ApplicationLoader.applicationLoaderInstance.takeUpdateLayout(getParentActivity(), this.updateLayoutWrapper);
        this.updateLayout = iUpdateLayoutTakeUpdateLayout;
        if (iUpdateLayoutTakeUpdateLayout != null) {
            iUpdateLayoutTakeUpdateLayout.updateAppUpdateViews(this.currentAccount, false);
        }
        updateLayout();
        checkUnreadCount(false);
        return this.contentView;
    }

    private boolean showFiltersAndFolders(View view) {
        DialogsActivity dialogsActivity = this.dialogsActivity;
        if (dialogsActivity != null && !dialogsActivity.hasRightFragment()) {
            ArrayList<MessagesController.DialogFilter> dialogFilters = getMessagesController().getDialogFilters();
            boolean zHasArchivedChats = ChatUtils.getInstance(this.currentAccount).hasArchivedChats();
            if ((dialogFilters != null && dialogFilters.size() > 1) || zHasArchivedChats) {
                return showFiltersMenu(view, dialogFilters, zHasArchivedChats);
            }
            Bundle bundle = new Bundle();
            bundle.putInt("folderId", 1);
            presentFragment(new DialogsActivity(bundle));
            return true;
        }
        return false;
    }

    private void onTabClicked(int i, View view) {
        if (this.viewPager.isManualScrolling() || this.viewPager.isTouch()) {
            return;
        }
        if (i == 5 && isFeedTabEnabled() && AndroidUtilities.isTablet()) {
            FeedActivity.presentFeed(this);
            return;
        }
        int iIndexToPosition = indexToPosition(i);
        if (this.viewPager.getCurrentPosition() == iIndexToPosition) {
            Object currentVisibleFragment = getCurrentVisibleFragment();
            if (currentVisibleFragment instanceof TabFragmentDelegate) {
                ((TabFragmentDelegate) currentVisibleFragment).onParentScrollToTop();
                return;
            }
            return;
        }
        selectTab(iIndexToPosition, true);
        this.viewPager.scrollToPosition(iIndexToPosition);
    }

    private void checkUnreadCount(boolean z) {
        if (this.tabsView == null) {
            return;
        }
        int mainUnreadCount = MessagesStorage.getInstance(this.currentAccount).getMainUnreadCount();
        if (mainUnreadCount > 0) {
            this.tabs[0].setCounter(LocaleController.formatNumber(mainUnreadCount, ','), false, z);
        } else {
            this.tabs[0].setCounter(null, false, z);
        }
        if (this.tabs[5] == null || !isFeedTabEnabled()) {
            return;
        }
        int unreadCount = FeedController.getInstance(this.currentAccount).getUnreadCount();
        this.tabs[5].setCounter(unreadCount > 0 ? LocaleController.formatNumber(unreadCount, ',') : null, false, z);
    }

    public boolean openCallsSelector(View view) {
        if (getContext() == null || getParentActivity() == null) {
            return false;
        }
        ItemOptions itemOptionsMakeOptions = ItemOptions.makeOptions(this, view);
        itemOptionsMakeOptions.add(R.drawable.menu_call_create, LocaleController.getString(R.string.GroupCallCreate2), () -> {
            CallLogActivity.openCreateCall(this);
        });
        if (getUserConfig().showCallsTab) {
            itemOptionsMakeOptions.add(R.drawable.msg_archive_hide, LocaleController.getString(R.string.HideCallTab), () -> {
                getUserConfig().setShowCallsTab(false);
                checkUi_callTabVisible(false, true);
                NotificationCenter.getInstance(this.currentAccount).postNotificationNameOnUIThread(NotificationCenter.callTabsVisibleToggled);
            });
        } else {
            itemOptionsMakeOptions.add(R.drawable.menu_add_tab_24, LocaleController.getString(R.string.GroupCallShowInMainTabs), () -> {
                getUserConfig().setShowCallsTab(true);
                checkUi_callTabVisible(true, true);
                NotificationCenter.getInstance(this.currentAccount).postNotificationNameOnUIThread(NotificationCenter.callTabsVisibleToggled);
            });
        }
        itemOptionsMakeOptions.translate(0.0f, -AndroidUtilities.dp(4.0f));
        itemOptionsMakeOptions.setScrimViewBackground(MainTabsUiHelper.createMainTabsScrimBackground(this.resourceProvider, false));
        itemOptionsMakeOptions.setDiscardScrolls(false);
        itemOptionsMakeOptions.setDismissOnMoveOutside(true);
        itemOptionsMakeOptions.show();
        return true;
    }

    public boolean openAccountSelector(View view) {
        return openAccountSelector(view, null);
    }

    public boolean openAccountSelector(View view, View view2) {
        return openAccountSelectorInternal(view, view2, false);
    }

    public boolean isDrawerAccountPreview() {
        return false;
    }

    private boolean canAddAccount() {
        return UserConfig.getActivatedAccountsCount() < 16;
    }

    public void openAddAccountFlow() {
        int i = 16;
        Integer numValueOf = null;
        for (int i2 = 15; i2 >= 0; i2--) {
            if (!UserConfig.getInstance(i2).isClientActivated()) {
                i++;
                if (numValueOf == null) {
                    numValueOf = Integer.valueOf(i2);
                }
            }
        }
        if (!UserConfig.hasPremiumOnAccounts()) {
            i -= 8;
        }
        if (i > 0 && numValueOf != null) {
            presentFragment(new LoginActivity(numValueOf.intValue()));
        } else {
            if (UserConfig.hasPremiumOnAccounts()) {
                return;
            }
            showDialog(new LimitReachedBottomSheet(this, getContext(), 7, this.currentAccount, null));
        }
    }

    private void addAddAccountItem(ItemOptions itemOptions) {
        itemOptions.add(R.drawable.msg_addbot, LocaleController.getString(R.string.AddAccount), () -> openAddAccountFlow());
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private boolean openAccountSelectorInternal(View view, View view2, boolean z) {
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.clear();
        for (int i = 0; i < 16; i++) {
            if (UserConfig.getInstance(i).isClientActivated()) {
                arrayList.add(Integer.valueOf(i));
            }
        }
        Collections.sort(arrayList, (num, num2) -> {
            long j = UserConfig.getInstance(num.intValue()).loginTime;
            long j2 = UserConfig.getInstance(num2.intValue()).loginTime;
            if (j > j2) {
                return 1;
            }
            return j < j2 ? -1 : 0;
        });
        if (z) {
            Collections.reverse(arrayList);
        }
        final ItemOptions itemOptionsMakeOptions = ItemOptions.makeOptions(this, view);
        if (!z && canAddAccount()) {
            addAddAccountItem(itemOptionsMakeOptions);
        }
        if (BuildVars.DEBUG_PRIVATE_VERSION) {
            itemOptionsMakeOptions.add(R.drawable.menu_download_round, "Dump Canvas", () -> {
                AndroidUtilities.runOnUIThread(() -> dumpCanvas(), 1000L);
            });
        }
        if (arrayList.size() > 0) {
            if (view2 != null) {
                view2.setOnTouchListener((view3, motionEvent) -> {
                    if (!itemOptionsMakeOptions.isShown()) {
                        return false;
                    }
                    if (view3.getParent() != null) {
                        view3.getParent().requestDisallowInterceptTouchEvent(true);
                    }
                    itemOptionsMakeOptions.dispatchCapturedTouchEvent(motionEvent);
                    return false;
                });
            }
            if (itemOptionsMakeOptions.getItemsCount() > 0) {
                itemOptionsMakeOptions.addGap();
            }
            int size = arrayList.size();
            int i2 = 0;
            while (i2 < size) {
                final int iIntValue = arrayList.get(i2).intValue();
                LinearLayout linearLayoutAccountView = accountView(iIntValue, this.currentAccount == iIntValue, z && i2 == 0, !z && i2 == size - 1);
                linearLayoutAccountView.setOnClickListener(view3 -> {
                    if (this.currentAccount == iIntValue) {
                        return;
                    }
                    itemOptionsMakeOptions.dismiss();
                    LaunchActivity launchActivity = LaunchActivity.instance;
                    if (launchActivity != null) {
                        launchActivity.switchToAccount(iIntValue, true);
                    }
                });
                itemOptionsMakeOptions.addView(linearLayoutAccountView, LayoutHelper.createLinear(230, 48));
                i2++;
            }
        }
        if (z && canAddAccount()) {
            if (itemOptionsMakeOptions.getItemsCount() > 0) {
                itemOptionsMakeOptions.addGap();
            }
            addAddAccountItem(itemOptionsMakeOptions);
        }
        itemOptionsMakeOptions.setBlur(true);
        itemOptionsMakeOptions.translate(0.0f, -AndroidUtilities.dp(4.0f));
        itemOptionsMakeOptions.setScrimViewBackground(MainTabsUiHelper.createMainTabsScrimBackground(this.resourceProvider, z));
        itemOptionsMakeOptions.setDiscardScrolls(false);
        itemOptionsMakeOptions.setDismissOnMoveOutside(true);
        itemOptionsMakeOptions.show();
        HintsController.Hint.AccountSwitchHint.doNotShowAgain();
        return true;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private boolean showFiltersMenu(View view, ArrayList<MessagesController.DialogFilter> arrayList, boolean z) {
        final ItemOptions itemOptionsMakeOptions = ItemOptions.makeOptions(this, view);
        if (arrayList != null && !arrayList.isEmpty()) {
            for (int i = 0; i < arrayList.size(); i++) {
                final int filterIndex = i;
                MessagesController.DialogFilter dialogFilter = arrayList.get(i);
                if (!dialogFilter.isDefault() || !ExteraConfig.getHideAllChats()) {
                    CharSequence charSequenceReplaceEmoji = Emoji.replaceEmoji(dialogFilter.isDefault() ? LocaleController.getString(R.string.FilterAllChats) : dialogFilter.name, Theme.chat_msgTextPaint.getFontMetricsInt(), false);
                    ArrayList<TLRPC.MessageEntity> arrayList2 = dialogFilter.entities;
                    if (arrayList2 != null && !arrayList2.isEmpty()) {
                        charSequenceReplaceEmoji = MessageObject.replaceAnimatedEmoji(charSequenceReplaceEmoji, dialogFilter.entities, Theme.chat_msgTextPaint.getFontMetricsInt());
                    }
                    ActionBarMenuSubItem actionBarMenuSubItemAdd = itemOptionsMakeOptions.add();
                    actionBarMenuSubItemAdd.setTextAndIcon(charSequenceReplaceEmoji, getIcon(dialogFilter));
                    NotificationCenter.listenEmojiLoading(actionBarMenuSubItemAdd.textView);
                    actionBarMenuSubItemAdd.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    actionBarMenuSubItemAdd.imageView.setLayoutParams(LayoutHelper.createFrame(24, 24.0f, (LocaleController.isRTL ? 5 : 3) | 16, 0.0f, 0.0f, 0.0f, 0.0f));
                    actionBarMenuSubItemAdd.setOnClickListener(view2 -> {
                        itemOptionsMakeOptions.dismiss();
                        this.dialogsActivity.switchToFilter(filterIndex);
                    });
                }
            }
            itemOptionsMakeOptions.addGap();
        }
        itemOptionsMakeOptions.add(R.drawable.msg_saved, LocaleController.getString(R.string.SavedMessages), () -> {
            itemOptionsMakeOptions.dismiss();
            Bundle bundle = new Bundle();
            bundle.putLong("user_id", getUserConfig().getClientUserId());
            if (getMessagesController().checkCanOpenChat(bundle, this)) {
                presentFragment(new ChatActivity(bundle));
            }
        });
        itemOptionsMakeOptions.addIf(z, R.drawable.msg_archive, LocaleController.getString(R.string.ArchivedChats), () -> {
            itemOptionsMakeOptions.dismiss();
            Bundle bundle = new Bundle();
            bundle.putInt("folderId", 1);
            presentFragment(new DialogsActivity(bundle));
        });
        itemOptionsMakeOptions.setGravity(3).translate(0.0f, -AndroidUtilities.dp(4.0f)).setScrimViewBackground(MainTabsUiHelper.createMainTabsScrimBackground(this.resourceProvider, false)).setDiscardScrolls(false).setDismissOnMoveOutside(true).show();
        return true;
    }

    private static int getIcon(MessagesController.DialogFilter dialogFilter) {
        int i = dialogFilter.flags;
        if ((MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS & i) == (MessagesController.DIALOG_FILTER_FLAG_CONTACTS | MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS)) {
            return R.drawable.msg_openprofile;
        }
        if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ & i) != 0) {
            int i2 = MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS;
            if ((i & i2) == i2) {
                return R.drawable.msg_markunread;
            }
        }
        if ((MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS & i) == MessagesController.DIALOG_FILTER_FLAG_CHANNELS) {
            return R.drawable.msg_channel;
        }
        if ((MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS & i) == MessagesController.DIALOG_FILTER_FLAG_GROUPS) {
            return R.drawable.msg_groups;
        }
        if ((MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS & i) == MessagesController.DIALOG_FILTER_FLAG_CONTACTS) {
            return R.drawable.msg_contacts;
        }
        if ((i & MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS) == MessagesController.DIALOG_FILTER_FLAG_BOTS) {
            return R.drawable.msg_bots;
        }
        return R.drawable.msg_folders;
    }

    public LinearLayout accountView(int i, final boolean z, boolean z2, boolean z3) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setBackground(Theme.createRadSelectorDrawable(getThemedColor(Theme.key_listSelector), 0, 0));
        UIUtil.applyScaleStateListAnimator(linearLayout, 12.0f, z2, z3, 3, 0.04f, 1.5f);
        TLRPC.User currentUser = UserConfig.getInstance(i).getCurrentUser();
        AvatarDrawable avatarDrawable = new AvatarDrawable();
        avatarDrawable.setInfo(currentUser);
        FrameLayout frameLayout = new FrameLayout(getContext()) {
            private final Paint selectedPaint = new Paint(1);

            @Override
            public void dispatchDraw(Canvas canvas) {
                if (z) {
                    this.selectedPaint.setStyle(Paint.Style.STROKE);
                    this.selectedPaint.setStrokeWidth(AndroidUtilities.dp(1.33f));
                    this.selectedPaint.setColor(MainTabsActivity.this.getThemedColor(Theme.key_featuredStickers_addButton));
                    float avatarCorners = ExteraConfig.getAvatarCorners(34.0f);
                    canvas.drawRoundRect(AndroidUtilities.dp(1.0f), AndroidUtilities.dp(1.0f), getWidth() - AndroidUtilities.dp(1.0f), getHeight() - AndroidUtilities.dp(1.0f), avatarCorners, avatarCorners, this.selectedPaint);
                }
                super.dispatchDraw(canvas);
            }
        };
        linearLayout.addView(frameLayout, LayoutHelper.createLinear(34, 34, 16, 12, 0, 0, 0));
        BackupImageView backupImageView = new BackupImageView(getContext());
        if (z) {
            backupImageView.setScaleX(0.833f);
            backupImageView.setScaleY(0.833f);
        }
        backupImageView.setRoundRadius(ExteraConfig.getAvatarCorners(32.0f));
        backupImageView.getImageReceiver().setCurrentAccount(i);
        backupImageView.setForUserOrChat(currentUser, avatarDrawable);
        frameLayout.addView(backupImageView, LayoutHelper.createLinear(32, 32, 17, 1, 1, 1, 1));
        TextView textView = new TextView(getContext());
        NotificationCenter.listenEmojiLoading(textView);
        textView.setTextSize(1, 16.0f);
        textView.setTextColor(getThemedColor(Theme.key_dialogTextBlack));
        textView.setText(Emoji.replaceEmoji(UserObject.getUserName(currentUser), textView.getPaint().getFontMetricsInt(), false));
        textView.setMaxLines(2);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        linearLayout.addView(textView, LayoutHelper.createLinear(0, -2, 1.0f, 16, 13, 0, 14, 0));
        return linearLayout;
    }

    @SuppressLint({"ClickableViewAccessibility"})
    private void openSettingsTabOptions(final View view) {
        final ItemOptions itemOptionsMakeOptions = ItemOptions.makeOptions((BaseFragment) this, view, true);
        BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
        if (safeLastFragment == null) {
            safeLastFragment = this;
        }
        MainMenuHelper.MenuContext menuContextCreateMenuContext = MainMenuHelper.createMenuContext(this.currentAccount, this, () -> {
            Bundle bundle = new Bundle();
            bundle.putInt("folderId", 1);
            presentFragment(new DialogsActivity(bundle));
        }, MainMenuHelper.createPluginContextData(this.currentAccount, safeLastFragment));
        Theme.ResourcesProvider resourcesProvider = this.resourceProvider;
        boolean zIsDark = resourcesProvider != null ? resourcesProvider.isDark() : Theme.isCurrentThemeDark();
        itemOptionsMakeOptions.add(zIsDark ? R.drawable.menu_day_mode_24 : R.drawable.menu_night_mode_24, LocaleController.getString(zIsDark ? R.string.SwitchThemeToDay : R.string.SwitchThemeToNight), () -> {
            if (DialogsActivity.switchingTheme) {
                return;
            }
            DialogsActivity.switchingTheme = true;
            SharedPreferences sharedPreferences = ApplicationLoader.applicationContext.getSharedPreferences("themeconfig", 0);
            String str = "Blue";
            String string = sharedPreferences.getString("lastDayTheme", "Blue");
            if (Theme.getTheme(string) == null || Theme.getTheme(string).isDark()) {
                string = "Blue";
            }
            String str2 = "Dark Blue";
            String string2 = sharedPreferences.getString("lastDarkTheme", "Dark Blue");
            if (Theme.getTheme(string2) == null || !Theme.getTheme(string2).isDark()) {
                string2 = "Dark Blue";
            }
            Theme.ThemeInfo activeTheme = Theme.getActiveTheme();
            if (string.equals(string2)) {
                if (activeTheme.isDark() || string.equals("Dark Blue") || string.equals("Night")) {
                    str2 = string2;
                }
                boolean zEquals = str.equals(activeTheme.getKey());
                Theme.ThemeInfo theme = zEquals ? Theme.getTheme(str2) : Theme.getTheme(str);
                switchTheme(view, theme, zEquals);
                Theme.turnOffAutoNight(BulletinFactory.of(this), () -> presentFragment(new ThemeActivity(1)));
                return;
            }
            boolean zEquals2 = string.equals(activeTheme.getKey());
            Theme.ThemeInfo theme2 = zEquals2 ? Theme.getTheme(string2) : Theme.getTheme(string);
            switchTheme(view, theme2, zEquals2);
            Theme.turnOffAutoNight(BulletinFactory.of(this), () -> presentFragment(new ThemeActivity(1)));
        });
        itemOptionsMakeOptions.addGap();
        MainMenuHelper.addConfiguredItemOptions(itemOptionsMakeOptions, menuContextCreateMenuContext, i -> {
            if (i == MainMenuItem.SETTINGS.getId() || i == MainMenuItem.PROFILE.getId() || i == MainMenuItem.ARCHIVE.getId() || i == MainMenuItem.SAVED.getId()) {
                return true;
            }
            return ExteraConfig.getShowFeedTab() && i == MainMenuItem.FEED.getId();
        });
        ApplicationLoader applicationLoader = ApplicationLoader.applicationLoaderInstance;
        if (applicationLoader != null) {
            applicationLoader.addItemOptions(itemOptionsMakeOptions);
        }
        if (!SharedConfig.proxyList.isEmpty()) {
            itemOptionsMakeOptions.addGap();
            if (this.proxyDrawable == null) {
                this.proxyDrawable = new ProxyDrawable(getContext());
            }
            ActionBarMenuSubItem actionBarMenuSubItem = new ActionBarMenuSubItem(getContext(), false, false, this.resourceProvider);
            this.proxyMenuSubItem = actionBarMenuSubItem;
            actionBarMenuSubItem.setTextAndIcon(LocaleController.getString(R.string.MenuProxyTitle), 0, this.proxyDrawable);
            this.proxyMenuSubItem.setOnClickListener(view2 -> {
                itemOptionsMakeOptions.dismiss();
                presentFragment(new ProxyListActivity());
            });
            updateProxyButton(false, false);
            itemOptionsMakeOptions.addView(this.proxyMenuSubItem);
        }
        itemOptionsMakeOptions.setGravity(1).translate(0.0f, -AndroidUtilities.dp(4.0f)).setScrimViewBackground(MainTabsUiHelper.createMainTabsScrimBackground(this.resourceProvider, false)).setDiscardScrolls(false).setDismissOnMoveOutside(true).setSwipebackGravity(false, true).setSwipebackCenterHorizontal(true).show();
    }

    private void switchTheme(View view, Theme.ThemeInfo themeInfo, boolean z) {
        if (view == null) {
            return;
        }
        int[] iArr = new int[2];
        view.getLocationInWindow(iArr);
        iArr[0] = iArr[0] + (view.getMeasuredWidth() / 2);
        iArr[1] = iArr[1] + (view.getMeasuredHeight() / 2);
        NotificationCenter.getGlobalInstance().postNotificationNameOnUIThread(NotificationCenter.needSetDayNightTheme, themeInfo, Boolean.FALSE, iArr, -1, Boolean.valueOf(z), view);
    }

    private void updateProxyButton(boolean z, boolean z2) {
        if (this.proxyDrawable == null || this.proxyMenuSubItem == null) {
            return;
        }
        boolean z3 = SharedConfig.isProxyEnabled() && SharedConfig.currentProxy != null;
        int i = this.currentConnectionState;
        boolean z4 = i == 3 || i == 5;
        this.proxyDrawable.setColorFilter(new PorterDuffColorFilter(getThemedColor(Theme.key_actionBarDefaultSubmenuItemIcon), PorterDuff.Mode.SRC_IN));
        this.proxyMenuSubItem.setTextColor(getThemedColor(Theme.key_actionBarDefaultSubmenuItem));
        ActionBarMenuSubItem actionBarMenuSubItem = this.proxyMenuSubItem;
        if (z3) {
            actionBarMenuSubItem.setItemHeight(56);
            this.proxyMenuSubItem.setSubtext(LocaleController.getString(z4 ? R.string.MenuProxyConnected : R.string.MenuProxyConnecting));
        } else {
            actionBarMenuSubItem.setItemHeight(48);
            this.proxyMenuSubItem.setSubtext(null);
        }
        this.proxyDrawable.setConnected(z3, z4, z);
    }

    public void invalidateTabsGlass() {
        if (Build.VERSION.SDK_INT >= 31) {
            blur3_invalidateBlur();
        }
    }

    private BaseFragment prepareTabFragment(BaseFragment baseFragment) {
        if (Build.VERSION.SDK_INT >= 31 && (baseFragment instanceof TabFragmentDelegate)) {
            ((TabFragmentDelegate) baseFragment).setParentTabsGlassInvalidationCallback(() -> invalidateTabsGlass());
        }
        return baseFragment;
    }

    @Override
    public void onViewPagerScrollEnd() {
        if (this.tabsView != null) {
            selectTab(this.viewPager.getCurrentPosition(), true);
            setGestureSelectedOverride(0.0f, false);
        }
        blur3_invalidateBlur();
        if (this.viewPager != null) {
            int currentPosition = this.viewPager.getCurrentPosition();
            if (currentPosition != getPositionCallsOrSettings() && this.dropCallsFragmentAfterPageScroll) {
                dropFragmentAtPosition(getPositionCallsOrSettings());
                this.dropCallsFragmentAfterPageScroll = false;
            }
            if (currentPosition != getPositionProfile()) {
                dropFragmentAtPosition(getPositionProfile());
            }
        }
    }

    @Override
    public void onViewPagerTabAnimationUpdate(boolean z) {
        boolean z2 = !z;
        if (this.tabsView != null) {
            float positionAnimated = this.viewPager.getPositionAnimated();
            setGestureSelectedOverride(positionAnimated, z2);
            if (z2) {
                selectTab(Math.round(positionAnimated), true);
            }
        }
        checkUi_fadeView();
        blur3_invalidateBlur();
    }

    @Override
    public int getFragmentsCount() {
        return getTabsCount();
    }

    @Override
    public int getStartPosition() {
        return getPositionChats();
    }

    @Override
    public boolean onBackPressed(boolean z) {
        int startPosition;
        boolean onBackPressed = super.onBackPressed(z);
        if (!onBackPressed || this.viewPager.getCurrentPosition() == (startPosition = getStartPosition())) {
            return onBackPressed;
        }
        if (z) {
            this.viewPager.scrollToPosition(startPosition);
        }
        return false;
    }

    private Bundle createDialogsArguments(Bundle bundle) {
        if (bundle == null) {
            bundle = new Bundle();
        }
        bundle.putBoolean("hasMainTabs", isBottomTabsEnabled());
        return bundle;
    }

    private DialogsActivity createDialogsActivity(Bundle bundle) {
        DialogsActivity dialogsActivity = new DialogsActivity(createDialogsArguments(bundle));
        dialogsActivity.setMainTabsActivityController(new MainTabsActivityControllerImpl(getPositionChats()));
        return (DialogsActivity) prepareTabFragment(dialogsActivity);
    }

    public DialogsActivity prepareDialogsActivity(Bundle bundle) {
        this.dialogsActivity = createDialogsActivity(bundle);
        putFragmentAtPosition(getPositionChats(), this.dialogsActivity);
        return this.dialogsActivity;
    }

    @Override
    public BaseFragment createBaseFragmentAt(int i) {
        if (i == getPositionContacts() && isFeedTabEnabled()) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("hasMainTabs", isBottomTabsEnabled());
            return prepareTabFragment(new FeedActivity(bundle));
        }
        if (i == getPositionContacts() && getUserConfig().showContactsTab) {
            Bundle bundle2 = new Bundle();
            bundle2.putBoolean("needPhonebook", true);
            bundle2.putBoolean("needFinishFragment", false);
            bundle2.putBoolean("hasMainTabs", isBottomTabsEnabled());
            ContactsActivity contactsActivity = (ContactsActivity) prepareTabFragment(new ContactsActivity(bundle2));
            contactsActivity.setMainTabsActivityController(new MainTabsActivityControllerImpl(i));
            return contactsActivity;
        }
        if (i == getPositionCallsOrSettings()) {
            if (getUserConfig().showCallsTab) {
                Bundle bundle3 = new Bundle();
                bundle3.putBoolean("needFinishFragment", false);
                bundle3.putBoolean("hasMainTabs", isBottomTabsEnabled());
                CallLogActivity callLogActivity = (CallLogActivity) prepareTabFragment(new CallLogActivity(bundle3));
                callLogActivity.setMainTabsActivityController(new MainTabsActivityControllerImpl(i));
                return callLogActivity;
            }
            Bundle bundle4 = new Bundle();
            bundle4.putBoolean("hasMainTabs", isBottomTabsEnabled());
            SettingsActivity settingsActivity = (SettingsActivity) prepareTabFragment(new SettingsActivity(bundle4));
            settingsActivity.setMainTabsActivityController(new MainTabsActivityControllerImpl(i));
            return settingsActivity;
        }
        if (i == getPositionChats()) {
            DialogsActivity dialogsActivityCreateDialogsActivity = createDialogsActivity(this.arguments);
            this.dialogsActivity = dialogsActivityCreateDialogsActivity;
            return dialogsActivityCreateDialogsActivity;
        }
        if (i != getPositionProfile()) {
            return null;
        }
        Bundle bundle5 = new Bundle();
        bundle5.putLong("user_id", UserConfig.getInstance(this.currentAccount).getClientUserId());
        bundle5.putBoolean("my_profile", true);
        bundle5.putBoolean("hasMainTabs", isBottomTabsEnabled());
        ProfileActivity profileActivity = (ProfileActivity) prepareTabFragment(new ProfileActivity(bundle5));
        profileActivity.setMainTabsActivityController(new MainTabsActivityControllerImpl(i));
        return profileActivity;
    }

    public DialogsActivity getDialogsActivity() {
        return this.dialogsActivity;
    }

    public void selectTab(int i, boolean z) {
        int i2 = 0;
        while (true) {
            GlassTabView[] glassTabViewArr = this.tabs;
            if (i2 >= glassTabViewArr.length) {
                return;
            }
            glassTabViewArr[i2].setSelected(indexToPosition(i2) == i, z);
            i2++;
        }
    }

    public void setGestureSelectedOverride(float f, boolean z) {
        for (int i = 0; i < this.tabs.length; i++) {
            this.tabs[i].setGestureSelectedOverride(Math.max(0.0f, 1.0f - Math.abs(indexToPosition(i) - f)), z);
        }
        this.tabsView.invalidate();
    }

    @Override
    public void setInPreviewMode(boolean z) {
        super.setInPreviewMode(z);
        int size = this.fragmentsArr.size();
        for (int i = 0; i < size; i++) {
            ViewPagerActivity.FragmentState fragmentStateValueAt = this.fragmentsArr.valueAt(i);
            if (fragmentStateValueAt != null) {
                fragmentStateValueAt.fragment.setInPreviewMode(z);
            }
        }
    }

    @Override
    public void onTransitionAnimationStart(boolean z, boolean z2) {
        BaseFragment currentVisibleFragment = getCurrentVisibleFragment();
        if (currentVisibleFragment != null) {
            currentVisibleFragment.onTransitionAnimationStart(z, z2);
        }
    }

    @Override
    public void onTransitionAnimationProgress(boolean z, float f) {
        BaseFragment currentVisibleFragment = getCurrentVisibleFragment();
        if (currentVisibleFragment != null) {
            currentVisibleFragment.onTransitionAnimationProgress(z, f);
        }
    }

    @Override
    public void onTransitionAnimationEnd(boolean z, boolean z2) {
        BaseFragment currentVisibleFragment = getCurrentVisibleFragment();
        if (currentVisibleFragment != null) {
            currentVisibleFragment.onTransitionAnimationEnd(z, z2);
        }
    }

    @Override
    public void onPreviewOpenAnimationEnd() {
        BaseFragment currentVisibleFragment = getCurrentVisibleFragment();
        if (currentVisibleFragment != null) {
            currentVisibleFragment.onPreviewOpenAnimationEnd();
        }
    }

    @Override
    public boolean canScrollForward(MotionEvent motionEvent) {
        return canScrollInternal(motionEvent, true);
    }

    @Override
    public boolean canScrollBackward(MotionEvent motionEvent) {
        return canScrollInternal(motionEvent, false);
    }

    private boolean canScrollInternal(MotionEvent motionEvent, boolean z) {
        if (!isBottomTabsEnabled()) {
            return false;
        }
        Object currentVisibleFragment = getCurrentVisibleFragment();
        if (currentVisibleFragment instanceof TabFragmentDelegate) {
            return ((TabFragmentDelegate) currentVisibleFragment).canParentTabsSlide(motionEvent, z);
        }
        return false;
    }

    private boolean isBottomTabsEnabled() {
        return BottomNavigationBar.visible();
    }

    @Override
    public WindowInsetsCompat onApplyWindowInsets(View view, WindowInsetsCompat windowInsetsCompat) {
        if (this.updateLayoutWrapper == null || this.fadeView == null || this.viewPager == null || this.tabsViewWrapper == null) {
            return super.onApplyWindowInsets(view, windowInsetsCompat);
        }
        this.navigationBarHeight = windowInsetsCompat.getInsets(WindowInsetsCompat.Type.systemBars()).bottom;
        boolean zIsUpdateLayoutVisible = this.updateLayoutWrapper.isUpdateLayoutVisible();
        int iDp = zIsUpdateLayoutVisible ? AndroidUtilities.dp(44.0f) : 0;
        this.updateLayoutWrapper.setPadding(0, 0, 0, this.navigationBarHeight);
        int iDp2 = this.navigationBarHeight + iDp + (isBottomTabsEnabled() ? AndroidUtilities.dp(MainTabsUiHelper.getTabsViewHeightDp()) : 0);
        ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) this.fadeView.getLayoutParams();
        if (marginLayoutParams.height != iDp2) {
            marginLayoutParams.height = iDp2;
            this.fadeView.setLayoutParams(marginLayoutParams);
        }
        int iMax = zIsUpdateLayoutVisible ? this.navigationBarHeight + iDp : 0;
        if (this.tabletLayout) {
            iMax = Math.max(iMax, this.navigationBarHeight + AndroidUtilities.dp(MainTabsUiHelper.getTabsViewHeightDp()));
        }
        ViewGroup.MarginLayoutParams marginLayoutParams2 = (ViewGroup.MarginLayoutParams) this.viewPager.getLayoutParams();
        if (marginLayoutParams2.bottomMargin != iMax) {
            marginLayoutParams2.bottomMargin = iMax;
            this.viewPager.setLayoutParams(marginLayoutParams2);
        }
        MainTabsUiHelper.applyTabsBottomInset(this.tabsView, this.tabsViewWrapper, this.navigationBarHeight);
        if (zIsUpdateLayoutVisible) {
            windowInsetsCompat = windowInsetsCompat.inset(0, 0, 0, this.navigationBarHeight);
        }
        checkUi_tabsPosition();
        checkUi_fadeView();
        return super.onApplyWindowInsets(view, windowInsetsCompat);
    }

    @Override
    public void didReceivedNotification(int i, int i2, Object... objArr) {
        int connectionState;
        GlassTabView glassTabView;
        LaunchActivity launchActivity;
        IUpdateLayout iUpdateLayout;
        IUpdateLayout iUpdateLayout2;
        boolean z = false;
        if (i == NotificationCenter.notificationsCountUpdated || i == NotificationCenter.updateInterfaces || i == NotificationCenter.dialogsNeedReload) {
            View view = this.fragmentView;
            if (view != null && view.isAttachedToWindow()) {
                z = true;
            }
            checkUnreadCount(z);
            return;
        }
        if (i == NotificationCenter.appUpdateLoading) {
            IUpdateLayout iUpdateLayout3 = this.updateLayout;
            if (iUpdateLayout3 != null) {
                iUpdateLayout3.updateFileProgress(null);
                this.updateLayout.updateAppUpdateViews(this.currentAccount, true);
                return;
            }
            return;
        }
        if (i == NotificationCenter.fileLoaded) {
            String str = (String) objArr[0];
            if (SharedConfig.isAppUpdateAvailable() && FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document).equals(str) && (iUpdateLayout2 = this.updateLayout) != null) {
                iUpdateLayout2.updateAppUpdateViews(this.currentAccount, true);
                return;
            }
            return;
        }
        if (i == NotificationCenter.fileLoadFailed) {
            String str2 = (String) objArr[0];
            if (SharedConfig.isAppUpdateAvailable() && FileLoader.getAttachFileName(SharedConfig.pendingAppUpdate.document).equals(str2) && (iUpdateLayout = this.updateLayout) != null) {
                iUpdateLayout.updateAppUpdateViews(this.currentAccount, true);
                return;
            }
            return;
        }
        if (i == NotificationCenter.fileLoadProgressChanged) {
            IUpdateLayout iUpdateLayout4 = this.updateLayout;
            if (iUpdateLayout4 != null) {
                iUpdateLayout4.updateFileProgress(objArr);
                return;
            }
            return;
        }
        if (i == NotificationCenter.appUpdateAvailable) {
            IUpdateLayout iUpdateLayout5 = this.updateLayout;
            if (iUpdateLayout5 == null || (launchActivity = LaunchActivity.instance) == null) {
                return;
            }
            iUpdateLayout5.updateAppUpdateViews(this.currentAccount, launchActivity.getMainFragmentsStackSize() == 1);
            return;
        }
        if (i == NotificationCenter.needSetDayNightTheme) {
            clearAllHiddenFragments();
            return;
        }
        if (i == NotificationCenter.callTabsVisibleToggled) {
            checkUi_callTabVisible(getUserConfig().showCallsTab, true);
            ViewPagerActivity.ViewPagerActivityPagerLayout viewPagerActivityPagerLayout = this.viewPager;
            if (viewPagerActivityPagerLayout != null && viewPagerActivityPagerLayout.getCurrentPosition() == getPositionCallsOrSettings()) {
                this.viewPager.scrollToPosition(getPositionChats());
                selectTab(getPositionChats(), true);
                this.dropCallsFragmentAfterPageScroll = true;
                return;
            }
            dropFragmentAtPosition(getPositionCallsOrSettings());
            return;
        }
        if (i == NotificationCenter.contactsTabVisibleToggled) {
            boolean z2 = getUserConfig().showContactsTab;
            checkUi_contactsOrFeedTabVisible(true);
            ViewPagerActivity.ViewPagerActivityPagerLayout viewPagerActivityPagerLayout2 = this.viewPager;
            if (viewPagerActivityPagerLayout2 != null) {
                int currentPosition = viewPagerActivityPagerLayout2.getCurrentPosition();
                if (!isFeedTabEnabled()) {
                    if (z2) {
                        if (currentPosition >= 1) {
                            currentPosition++;
                        }
                    } else if (currentPosition == 1) {
                        currentPosition = 0;
                    } else if (currentPosition > 1) {
                        currentPosition--;
                    }
                }
                clearAllHiddenFragments();
                for (int i3 = 0; i3 < getTabsCount() + 1; i3++) {
                    dropFragmentAtPosition(i3);
                }
                ViewPagerActivity.ViewPagerActivityPagerLayout viewPagerActivityPagerLayout3 = this.viewPager;
                viewPagerActivityPagerLayout3.currentPosition = currentPosition;
                viewPagerActivityPagerLayout3.rebuild(false);
                selectTab(currentPosition, false);
                return;
            }
            return;
        }
        if (i == NotificationCenter.feedTabVisibleToggled) {
            checkUi_contactsOrFeedTabVisible(true);
            checkUnreadCount(false);
            ViewPagerActivity.ViewPagerActivityPagerLayout viewPagerActivityPagerLayout4 = this.viewPager;
            if (viewPagerActivityPagerLayout4 != null) {
                int currentPosition2 = viewPagerActivityPagerLayout4.getCurrentPosition();
                if (!getUserConfig().showContactsTab) {
                    if (isFeedTabEnabled()) {
                        if (currentPosition2 >= 1) {
                            currentPosition2++;
                        }
                    } else if (currentPosition2 == 1) {
                        currentPosition2 = 0;
                    } else if (currentPosition2 > 1) {
                        currentPosition2--;
                    }
                }
                clearAllHiddenFragments();
                for (int i4 = 0; i4 < getTabsCount() + 1; i4++) {
                    dropFragmentAtPosition(i4);
                }
                ViewPagerActivity.ViewPagerActivityPagerLayout viewPagerActivityPagerLayout5 = this.viewPager;
                viewPagerActivityPagerLayout5.currentPosition = currentPosition2;
                viewPagerActivityPagerLayout5.rebuild(false);
                selectTab(currentPosition2, false);
                return;
            }
            return;
        }
        if (i == NotificationCenter.mainUserInfoChanged) {
            GlassTabView[] glassTabViewArr = this.tabs;
            if (glassTabViewArr == null || (glassTabView = glassTabViewArr[4]) == null) {
                return;
            }
            glassTabView.updateUserAvatar(this.currentAccount);
            return;
        }
        if (i == NotificationCenter.contactsPermissionBadgeCheck) {
            checkContactsTabBadge();
            return;
        }
        if (i == NotificationCenter.proxySettingsChanged) {
            updateProxyButton(false, false);
        } else {
            if (i != NotificationCenter.didUpdateConnectionState || this.currentConnectionState == (connectionState = AccountInstance.getInstance(i2).getConnectionsManager().getConnectionState())) {
                return;
            }
            this.currentConnectionState = connectionState;
            updateProxyButton(true, false);
        }
    }

    @Override
    public boolean onFragmentCreate() {
        this.observersGroup = NotificationCenter.getInstance(this.currentAccount).createObserversGroup(this).add(NotificationCenter.fileLoaded).add(NotificationCenter.fileLoadProgressChanged).add(NotificationCenter.fileLoadFailed).add(NotificationCenter.notificationsCountUpdated).add(NotificationCenter.updateInterfaces).add(NotificationCenter.dialogsNeedReload).add(NotificationCenter.callTabsVisibleToggled).add(NotificationCenter.contactsTabVisibleToggled).add(NotificationCenter.feedTabVisibleToggled).add(NotificationCenter.mainUserInfoChanged).add(NotificationCenter.didUpdateConnectionState).add(NotificationCenter.contactsPermissionBadgeCheck);
        this.globalObserversGroup = NotificationCenter.getGlobalInstance().createObserversGroup(this).add(NotificationCenter.appUpdateAvailable).add(NotificationCenter.appUpdateLoading).add(NotificationCenter.proxySettingsChanged).add(NotificationCenter.needSetDayNightTheme);
        return super.onFragmentCreate();
    }

    @Override
    public void onFragmentDestroy() {
        Bulletin.removeDelegate(this);
        FrameLayout frameLayout = this.contentView;
        if (frameLayout != null) {
            Bulletin.removeDelegate(frameLayout);
        }
        ViewPositionWatcher viewPositionWatcher = this.viewPositionWatcher;
        if (viewPositionWatcher != null) {
            viewPositionWatcher.shutdown();
            this.viewPositionWatcher = null;
        }
        NotificationCenter.ObserversGroup observersGroup = this.observersGroup;
        if (observersGroup != null) {
            observersGroup.removeAllObservers();
            this.observersGroup = null;
        }
        NotificationCenter.ObserversGroup observersGroup2 = this.globalObserversGroup;
        if (observersGroup2 != null) {
            observersGroup2.removeAllObservers();
            this.globalObserversGroup = null;
        }
        super.onFragmentDestroy();
    }

    @Override
    public void onFactorChanged(int i, float f, float f2, FactorAnimator factorAnimator) {
        if (i == 0) {
            checkUi_tabsPosition();
            checkUi_fadeView();
            Bulletin visibleBulletin = Bulletin.getVisibleBulletin();
            if (visibleBulletin != null) {
                visibleBulletin.updatePosition();
            }
        }
    }

    private void checkUi_fadeView() {
        if (this.viewPager == null || this.fadeView == null) {
            return;
        }
        if (!isBottomTabsEnabled()) {
            this.fadeView.setAlpha(0.0f);
            this.fadeView.setVisibility(View.GONE);
            return;
        }
        float fClamp = 1.0f - MathUtils.clamp(Math.abs(getPositionProfile() - this.viewPager.getPositionAnimated()), 0.0f, 1.0f);
        float navigationBarThirdButtonsFactor = (1.0f - ((1.0f - AndroidUtilities.getNavigationBarThirdButtonsFactor(0.0f, 1.0f, this.navigationBarHeight)) * fClamp)) * this.animatorTabsVisible.getFloatValue();
        if (this.tabletLayout) {
            navigationBarThirdButtonsFactor = 0.0f;
        }
        this.fadeView.setAlpha(navigationBarThirdButtonsFactor);
        this.fadeView.setTranslationY(fClamp * AndroidUtilities.dp(48.0f));
        this.fadeView.setVisibility(navigationBarThirdButtonsFactor > 0.0f ? View.VISIBLE : View.GONE);
    }

    private void checkUi_tabsPosition() {
        if (this.tabsView == null || this.tabsViewWrapper == null || this.updateLayoutWrapper == null) {
            return;
        }
        if (!isBottomTabsEnabled()) {
            this.tabsView.setClickable(false);
            this.tabsView.setEnabled(false);
            this.tabsView.setAlpha(0.0f);
            this.tabsView.setVisibility(View.GONE);
            return;
        }
        int i = -(this.updateLayoutWrapper.isUpdateLayoutVisible() ? AndroidUtilities.dp(44.0f) : 0);
        int iDp = AndroidUtilities.dp(40.0f) + i;
        float floatValue = this.animatorTabsVisible.getFloatValue();
        this.tabsViewWrapper.setTranslationY(AndroidUtilities.lerp(iDp, i, floatValue));
        this.tabsView.setClickable(floatValue > 0.5f);
        this.tabsView.setEnabled(floatValue > 0.5f);
        this.tabsView.setAlpha(floatValue);
        this.tabsView.setVisibility(floatValue > 0.0f ? View.VISIBLE : View.GONE);
    }

    private void checkUi_contactsOrFeedTabVisible(boolean z) {
        if (this.tabsView != null) {
            boolean zIsFeedTabEnabled = isFeedTabEnabled();
            this.tabsView.setViewVisible(this.tabs[5], zIsFeedTabEnabled, z);
            this.tabsView.setViewVisible(this.tabs[1], !zIsFeedTabEnabled && getUserConfig().showContactsTab, z);
        }
    }

    private void checkUi_callTabVisible(boolean z, boolean z2) {
        MainTabsLayout mainTabsLayout = this.tabsView;
        if (mainTabsLayout != null) {
            mainTabsLayout.setViewVisible(this.tabs[2], !z, z2);
            this.tabsView.setViewVisible(this.tabs[3], z, z2);
        }
    }

    @Override
    public ArrayList<ThemeDescription> getThemeDescriptions() {
        ArrayList<ThemeDescription> themeDescriptions = super.getThemeDescriptions();
        ThemeDescription.ThemeDescriptionDelegate themeDescriptionDelegate = () -> blur3_updateColors();
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_windowBackgroundWhite));
        themeDescriptions.add(new ThemeDescription(null, 0, null, null, null, themeDescriptionDelegate, Theme.key_dialogBackground));
        return themeDescriptions;
    }

    public class MainTabsActivityControllerImpl implements MainTabsActivityController {
        private final int ownerPosition;

        private MainTabsActivityControllerImpl(int i) {
            this.ownerPosition = i;
        }

        @Override
        public void setTabsVisible(boolean z) {
            ViewPagerActivity.ViewPagerActivityPagerLayout viewPagerActivityPagerLayout = MainTabsActivity.this.viewPager;
            if (viewPagerActivityPagerLayout == null || viewPagerActivityPagerLayout.getCurrentPosition() == this.ownerPosition) {
                MainTabsLayout mainTabsLayout = MainTabsActivity.this.tabsView;
                MainTabsActivity mainTabsActivity = MainTabsActivity.this;
                if (mainTabsLayout == null) {
                    mainTabsActivity.animatorTabsVisible.changeValueSilently(z);
                    MainTabsActivity.this.animatorTabsVisible.changeValueSilently(z ? 1.0f : 0.0f);
                } else {
                    mainTabsActivity.animatorTabsVisible.setValue(z, true);
                }
            }
        }

        @Override
        public boolean openAccountSelector(View view, View view2) {
            if (view == null) {
                return false;
            }
            return MainTabsActivity.this.openAccountSelector(view, view2);
        }
    }

    @Override
    public boolean canBeginSlide() {
        BaseFragment currentVisibleFragment = getCurrentVisibleFragment();
        return currentVisibleFragment != null && currentVisibleFragment.canBeginSlide();
    }

    @Override
    public void onBeginSlide() {
        super.onBeginSlide();
        BaseFragment currentVisibleFragment = getCurrentVisibleFragment();
        if (currentVisibleFragment != null) {
            currentVisibleFragment.onBeginSlide();
        }
    }

    @Override
    public void onSlideProgress(boolean z, float f) {
        BaseFragment currentVisibleFragment = getCurrentVisibleFragment();
        if (currentVisibleFragment != null) {
            currentVisibleFragment.onSlideProgress(z, f);
        }
    }

    @Override
    public Animator getCustomSlideTransition(boolean z, boolean z2, float f) {
        BaseFragment currentVisibleFragment = getCurrentVisibleFragment();
        if (currentVisibleFragment != null) {
            return currentVisibleFragment.getCustomSlideTransition(z, z2, f);
        }
        return null;
    }

    @Override
    public void prepareFragmentToSlide(boolean z, boolean z2) {
        BaseFragment currentVisibleFragment = getCurrentVisibleFragment();
        if (currentVisibleFragment != null) {
            currentVisibleFragment.prepareFragmentToSlide(z, z2);
        }
    }

    private void showAccountChangeHint() {
        if (this.accountSwitchHintShown) {
            return;
        }
        if (this.accountSwitchHint == null && HintsController.Hint.AccountSwitchHint.show()) {
            AndroidUtilities.runOnUIThread(() -> {
                GlassTabView[] glassTabViewArr;
                if (getContext() == null || (glassTabViewArr = this.tabs) == null) {
                    return;
                }
                GlassTabView glassTabView = glassTabViewArr[4];
                float width = (this.contentView.getWidth() - ((this.tabsView.getX() + glassTabView.getX()) + glassTabView.getWidth())) + (glassTabView.getWidth() / 2.0f);
                HintView2 hintView2 = new HintView2(getContext(), 2);
                this.accountSwitchHint = hintView2;
                hintView2.setTranslationY((-this.navigationBarHeight) + AndroidUtilities.dp(4.0f));
                this.accountSwitchHint.setPadding(AndroidUtilities.dp(7.33f), 0, AndroidUtilities.dp(7.33f), 0);
                this.accountSwitchHint.setMultilineText(false);
                this.accountSwitchHint.setCloseButton(true);
                this.accountSwitchHint.setText(LocaleController.getString(R.string.SwitchAccountHint));
                this.accountSwitchHint.setJoint(1.0f, (-width) + 7.33f);
                this.contentView.addView(this.accountSwitchHint, LayoutHelper.createFrame(-1, 100.0f, 87, 0.0f, 0.0f, 0.0f, MainTabsUiHelper.getTabsViewHeightDp()));
                this.accountSwitchHint.setOnHiddenListener(() -> {
                    AndroidUtilities.removeFromParent(this.accountSwitchHint);
                });
                this.accountSwitchHint.setDuration(8000L);
                this.accountSwitchHint.show();
                HintsController.Hint.AccountSwitchHint.increment();
            }, 1500L);
        }
        this.accountSwitchHintShown = true;
    }

    private void blur3_invalidateBlur() {
        BlurredBackgroundSourceRenderNode blurredBackgroundSourceRenderNode;
        if (Build.VERSION.SDK_INT < 31 || (blurredBackgroundSourceRenderNode = this.iBlur3SourceTabGlass) == null || this.fragmentView == null) {
            return;
        }
        blurredBackgroundSourceRenderNode.setSize(this.fragmentView.getMeasuredWidth(), this.fragmentView.getMeasuredHeight());
        this.iBlur3SourceTabGlass.updateDisplayListIfNeeded();
    }

    public void blur3_updateColors() {
        BlurredBackgroundSourceColor blurredBackgroundSourceColor = this.iBlur3SourceColor;
        if (blurredBackgroundSourceColor != null) {
            blurredBackgroundSourceColor.setColor(getThemedColor(Theme.key_windowBackgroundWhite));
        }
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
                if (glassTabView != null) {
                    glassTabView.updateColorsLottie();
                }
            }
        }
    }
}
