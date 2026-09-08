package com.exteragram.messenger.preferences.appearance;

import android.content.Context;
import android.os.Parcelable;
import android.view.View;
import com.exteragram.messenger.DividerStyle;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.GlassOutlineStyle;
import com.exteragram.messenger.TabIconsMode;
import com.exteragram.messenger.icons.ui.IconPacksActivity;
import com.exteragram.messenger.pillstack.ui.PillStackPreferencesActivity;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.exteragram.messenger.preferences.SwitchGroup;
import com.exteragram.messenger.preferences.appearance.components.AvatarCornersPreviewCell;
import com.exteragram.messenger.preferences.appearance.components.ChatListPreviewCell;
import com.exteragram.messenger.preferences.appearance.components.FabShapeCell;
import com.exteragram.messenger.preferences.appearance.components.FilterTabsPreviewCell;
import com.exteragram.messenger.preferences.utils.SettingsRegistry;
import com.exteragram.messenger.utils.network.RemoteUtils;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.exteragram.messenger.utils.ui.PopupUtils;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.function.BooleanSupplier;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

public class AppearancePreferencesActivity extends BasePreferencesActivity {
    private AvatarCornersPreviewCell avatarCornersPreviewCell;
    private ChatListPreviewCell chatListPreviewCell;
    private CharSequence[] dividerStyles;
    private FabShapeCell fabShapeCell;
    private FilterTabsPreviewCell filterTabsPreviewCell;
    private CharSequence[] glassOutlineStyles;
    private final SwitchGroup md3Styles = SwitchGroup.of(this, AppearanceItem.MD3_STYLES.getId(), R.string.MaterialDesign3).searchable().linkAlias("md3Styles").onChanged(new Runnable() { 
        @Override // java.lang.Runnable
        public final void run() {
            this.f$0.updateMD3Styles();
        }
    }).add(AppearanceItem.NEW_LOADING_STYLE.getId(), R.string.NewLoadingStyle, new BooleanSupplier() { 
        @Override // java.util.function.BooleanSupplier
        public final boolean getAsBoolean() {
            return ExteraConfig.getNewLoadingStyle();
        }
    }, new SwitchGroup.Setter() { 
        @Override 
        public final void set(boolean z) {
            ExteraConfig.setNewLoadingStyle(z);
        }
    }).add(AppearanceItem.NEW_SLIDER_STYLE.getId(), R.string.NewSliderStyle, new BooleanSupplier() { 
        @Override // java.util.function.BooleanSupplier
        public final boolean getAsBoolean() {
            return ExteraConfig.getNewSliderStyle();
        }
    }, new SwitchGroup.Setter() { 
        @Override 
        public final void set(boolean z) {
            ExteraConfig.setNewSliderStyle(z);
        }
    }).add(AppearanceItem.NEW_SWITCH_STYLE.getId(), R.string.NewSwitchStyle, new BooleanSupplier() { 
        @Override // java.util.function.BooleanSupplier
        public final boolean getAsBoolean() {
            return ExteraConfig.getNewSwitchStyle();
        }
    }, new SwitchGroup.Setter() { 
        @Override 
        public final void set(boolean z) {
            ExteraConfig.setNewSwitchStyle(z);
        }
    }).add(AppearanceItem.NEW_CHAT_HEADER_STYLE.getId(), R.string.ChatHeader, new BooleanSupplier() { 
        @Override // java.util.function.BooleanSupplier
        public final boolean getAsBoolean() {
            return ExteraConfig.getNewChatHeaderStyle();
        }
    }, new SwitchGroup.Setter() { 
        @Override 
        public final void set(boolean z) {
            ExteraConfig.setNewChatHeaderStyle(z);
        }
    }).markNew("Appearance-M3Styles-ChatHeader").add(AppearanceItem.NEW_NAVIGATION_BAR_STYLE.getId(), R.string.BottomNavigationBarMode, new BooleanSupplier() { 
        @Override // java.util.function.BooleanSupplier
        public final boolean getAsBoolean() {
            return ExteraConfig.getNewNavigationBarStyle();
        }
    }, new SwitchGroup.Setter() { 
        @Override 
        public final void set(boolean z) {
            ExteraConfig.setNewNavigationBarStyle(z);
        }
    }).markNew("Appearance-M3Styles-NavigationBar");
    private Parcelable recyclerViewState;
    private CharSequence[] tabIcons;
    private CharSequence[] titles;

    public enum AppearanceItem {
        AVATAR_CORNERS_PREVIEW,
        SINGLE_CORNER_RADIUS,
        CHAT_LIST_PREVIEW,
        FORCE_SNOW,
        HIDE_ACTION_BAR_STATUS,
        CENTER_TITLE,
        HIDE_STORIES,
        HIDE_FLOATING_BUTTON,
        HIDE_DIALOGS_SEARCH_BAR,
        SENDER_MINI_AVATARS,
        ACTION_BAR_TITLE,
        PILL_STACK,
        FOLDERS_PREVIEW,
        TAB_TITLE,
        TAB_COUNTER,
        HIDE_ALL_CHATS,
        APP_NAVIGATION_SETTINGS,
        ICON_PACKS,
        FAB_SHAPE,
        SECTION_RADIUS,
        SEPARATED_HEADERS,
        DIVIDER_STYLE,
        TABLET_MODE,
        MD3_STYLES,
        NEW_LOADING_STYLE,
        NEW_SLIDER_STYLE,
        NEW_SWITCH_STYLE,
        USE_SYSTEM_FONTS,
        USE_SYSTEM_EMOJI,
        GOOEY_AVATAR_ANIMATION,
        CUSTOM_THEMES,
        PREDICTIVE_BACK_ANIMATION,
        SPRING_ANIMATIONS,
        GLASS_OUTLINE_STYLE,
        FORCE_BLUR,
        GLASS_MESSAGE_MENU,
        NEW_CHAT_HEADER_STYLE,
        NEW_NAVIGATION_BAR_STYLE;

        public int getId() {
            return ordinal() + 1;
        }
    }

    @Override 
    public void initializeOptionStrings() {
        this.titles = new CharSequence[]{LocaleController.getString(R.string.exteraAppName), LocaleController.getString(R.string.ActionBarTitleUsername), LocaleController.getString(R.string.ActionBarTitleName), LocaleController.getString(R.string.FilterChats)};
        this.tabIcons = new CharSequence[]{LocaleController.getString(R.string.TabTitleStyleTextWithIcons), LocaleController.getString(R.string.TabTitleStyleTextOnly), LocaleController.getString(R.string.TabTitleStyleIconsOnly)};
        this.dividerStyles = new CharSequence[]{LocaleController.getString(R.string.DividerStyleHidden), LocaleController.getString(R.string.DividerStyleLine), LocaleController.getString(R.string.DividerStyleSegments)};
        this.glassOutlineStyles = new CharSequence[]{LocaleController.getString(R.string.GlassOutlineGlare), LocaleController.getString(R.string.GlassOutlineSolid), LocaleController.getString(R.string.GlassOutlineHidden)};
    }

    @Override 
    public View createView(Context context) {
        this.avatarCornersPreviewCell = new AvatarCornersPreviewCell(context, this, this.resourceProvider, RemoteUtils.getIntConfigValue("preferences_preview_style", 0).intValue());
        this.chatListPreviewCell = new ChatListPreviewCell(context);
        this.filterTabsPreviewCell = new FilterTabsPreviewCell(context);
        this.fabShapeCell = new FabShapeCell(context) { 
            @Override 
            public void rebuildFragments() {
                ((BaseFragment) AppearancePreferencesActivity.this).parentLayout.rebuildFragments(0);
            }
        };
        return super.createView(context);
    }

    @Override 
    public String getTitle() {
        return LocaleController.getString(R.string.Appearance);
    }

    @Override 
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        arrayList.add(UItem.asCustom(AppearanceItem.AVATAR_CORNERS_PREVIEW.getId(), this.avatarCornersPreviewCell).setLinkAlias("avatarCorners", this));
        arrayList.add(UItem.asCheck(AppearanceItem.SINGLE_CORNER_RADIUS.getId(), LocaleController.getString(R.string.SingleCornerRadius)).setChecked(ExteraConfig.getSingleCornerRadius()).setSearchable(this).setLinkAlias("singleCornerRadius", this));
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.SingleCornerRadiusInfo)));
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.ListOfChats)));
        arrayList.add(UItem.asCustom(AppearanceItem.CHAT_LIST_PREVIEW.getId(), this.chatListPreviewCell));
        arrayList.add(UItem.asCheck(AppearanceItem.FORCE_SNOW.getId(), LocaleController.getString(R.string.ForceSnow), LocaleController.getString(R.string.ForceSnowInfo), true).setChecked(ExteraConfig.getForceSnow()).setSearchable(this).setLinkAlias("forceSnow", this));
        if (getUserConfig().isPremium()) {
            arrayList.add(UItem.asCheck(AppearanceItem.HIDE_ACTION_BAR_STATUS.getId(), LocaleController.getString(R.string.HideActionBarStatus)).setChecked(ExteraConfig.getHideActionBarStatus()).setSearchable(this).setLinkAlias("hideActionBarStatus", this));
        }
        arrayList.add(UItem.asCheck(AppearanceItem.CENTER_TITLE.getId(), LocaleController.getString(R.string.CenterTitle)).setChecked(ExteraConfig.getCenterTitle()).setSearchable(this).setLinkAlias("centerTitle", this));
        arrayList.add(UItem.asCheck(AppearanceItem.HIDE_STORIES.getId(), LocaleController.getString(R.string.HideStories)).setChecked(ExteraConfig.getHideStories()).setSearchable(this).setLinkAlias("hideStories", this));
        arrayList.add(UItem.asCheck(AppearanceItem.HIDE_FLOATING_BUTTON.getId(), LocaleController.getString(R.string.HideFloatingButton)).setChecked(ExteraConfig.getHideFloatingButton()).setSearchable(this).setLinkAlias("hideFloatingButton", this));
        arrayList.add(UItem.asCheck(AppearanceItem.HIDE_DIALOGS_SEARCH_BAR.getId(), LocaleController.getString(R.string.HideDialogsSearchBar)).setChecked(ExteraConfig.getHideDialogsSearchBar()).setSearchable(this).setLinkAlias("hideDialogsSearchBar", this));
        arrayList.add(UItem.asCheck(AppearanceItem.SENDER_MINI_AVATARS.getId(), LocaleController.getString(R.string.SenderMiniAvatars)).setChecked(ExteraConfig.getSenderMiniAvatars()).setSearchable(this).setLinkAlias("senderMiniAvatars", this));
        arrayList.add(UItem.asButton(AppearanceItem.ACTION_BAR_TITLE.getId(), LocaleController.getString(R.string.ActionBarTitle), this.titles[ExteraConfig.getTitleText()]).setSearchable(this).setLinkAlias("actionBarTitle", this));
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.ListOfChatsInfo)));
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.Filters)));
        arrayList.add(UItem.asCustom(AppearanceItem.FOLDERS_PREVIEW.getId(), this.filterTabsPreviewCell));
        arrayList.add(UItem.asButton(AppearanceItem.TAB_TITLE.getId(), LocaleController.getString(R.string.TabTitleStyle), this.tabIcons[ExteraConfig.getTabIcons().ordinal()]).setSearchable(this).setLinkAlias("tabTitleStyle", this));
        arrayList.add(UItem.asCheck(AppearanceItem.TAB_COUNTER.getId(), LocaleController.getString(R.string.TabCounter)).setChecked(ExteraConfig.getTabCounter()).setSearchable(this).setLinkAlias("tabCounter", this));
        arrayList.add(UItem.asCheck(AppearanceItem.HIDE_ALL_CHATS.getId(), LocaleController.formatString(R.string.HideAllChats, LocaleController.getString(R.string.FilterAllChats))).setChecked(ExteraConfig.getHideAllChats()).setSearchable(this).setLinkAlias("hideAllChats", this));
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.FoldersInfo)));
        arrayList.add(UItem.asButtonWithSubtext(AppearanceItem.APP_NAVIGATION_SETTINGS.getId(), R.drawable.msg_newphone, LocaleController.getString(R.string.AppNavigation), LocaleController.getString(R.string.AppNavigationInfo), 64, 60).setSearchable(this).setLinkAlias("appNavigationSettings", this));
        arrayList.add(UItem.asButtonWithSubtext(AppearanceItem.ICON_PACKS.getId(), R.drawable.msg_sticker, LocaleController.getString(R.string.IconPacks), LocaleController.getString(R.string.IconPacksInfo), 64, 60).setSearchable(this).setLinkAlias("iconPacks", this));
        arrayList.add(UItem.asButtonWithSubtext(AppearanceItem.PILL_STACK.getId(), R.drawable.outline_header_search, LocaleController.getString(R.string.PillStackPills), LocaleController.getString(R.string.PillStackPillsInfo), 64, 60).setSearchable(this).setLinkAlias("pillStack", this));
        arrayList.add(UItem.asShadow());
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.Appearance)));
        arrayList.add(UItem.asCustom(AppearanceItem.FAB_SHAPE.getId(), this.fabShapeCell).setLinkAlias("fabShape", this));
        arrayList.add(UItem.asCheck(AppearanceItem.USE_SYSTEM_FONTS.getId(), LocaleController.getString(R.string.UseSystemFonts)).setChecked(ExteraConfig.getUseSystemFonts()).setSearchable(this).setLinkAlias("useSystemFonts", this));
        arrayList.add(UItem.asCheck(AppearanceItem.USE_SYSTEM_EMOJI.getId(), LocaleController.getString(R.string.UseSystemEmoji)).setChecked(SharedConfig.useSystemEmoji).setSearchable(this).setLinkAlias("useSystemEmoji", this));
        this.md3Styles.fill(arrayList);
        arrayList.add(UItem.asCheck(AppearanceItem.GOOEY_AVATAR_ANIMATION.getId(), LocaleController.getString(R.string.GooeyAvatarAnimation)).setChecked(ExteraConfig.getGooeyAvatarAnimation()).setSearchable(this).setLinkAlias("gooeyAvatarAnimation", this));
        arrayList.add(UItem.asCheck(AppearanceItem.CUSTOM_THEMES.getId(), LocaleController.getString(R.string.CustomChatThemes)).setChecked(ExteraConfig.getCustomThemes()).setSearchable(this).setLinkAlias("customThemes", this));
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.CustomChatThemesInfo)));
        arrayList.add(UItem.asHeader(SettingsRegistry.markAsNewFeature("Appearance-Sections") ? LocaleUtils.applyNewSpan(LocaleController.getString(R.string.Sections)) : LocaleController.getString(R.string.Sections)));
        arrayList.add(createSectionRadiusSliderItem().setSearchable(this).setLinkAlias("sectionRadius", this));
        arrayList.add(UItem.asCheck(AppearanceItem.SEPARATED_HEADERS.getId(), LocaleController.getString(R.string.SeparateHeaders)).setChecked(ExteraConfig.getSectionsSeparatedHeaders()).setEnabled(!(ExteraConfig.getDividerStyle() == DividerStyle.SEGMENTS)).setSearchable(this).setLinkAlias("sectionsSeparatedHeaders", this));
        arrayList.add(UItem.asButton(AppearanceItem.DIVIDER_STYLE.getId(), LocaleController.getString(R.string.DividerStyle), this.dividerStyles[ExteraConfig.getDividerStyle().ordinal()]).setSearchable(this).setLinkAlias("dividerStyle", this));
        arrayList.add(UItem.asShadow());
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.BlurOptions)));
        arrayList.add(UItem.asButton(AppearanceItem.GLASS_OUTLINE_STYLE.getId(), LocaleController.getString(R.string.GlassOutlineStyle), this.glassOutlineStyles[ExteraConfig.getGlassOutlineStyle().ordinal()]).setSearchable(this).setLinkAlias("glassOutlineStyle", this));
        arrayList.add(UItem.asCheck(AppearanceItem.GLASS_MESSAGE_MENU.getId(), LocaleController.getString(R.string.GlassMessageMenu), LocaleController.getString(R.string.GlassMessageMenuInfo), true).setChecked(ExteraConfig.getGlassMessageMenu()).setSearchable(this).setLinkAlias("glassMessageMenu", this));
        arrayList.add(UItem.asCheck(AppearanceItem.FORCE_BLUR.getId(), LocaleController.getString(R.string.ForceBlur)).setChecked(ExteraConfig.getForceBlur()).setSearchable(this).setLinkAlias("forceBlur", this));
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.ForceBlurInfo)));
    }

    @Override 
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        int i2 = uItem.id;
        if (i2 <= 0 || i2 > AppearanceItem.values().length) {
            return;
        }
        switch (AnonymousClass2.$SwitchMap$com$exteragram$messenger$preferences$appearance$AppearancePreferencesActivity$AppearanceItem[AppearanceItem.values()[uItem.id - 1].ordinal()]) {
            case 1:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setSingleCornerRadius(((Boolean) obj).booleanValue());
                    }
                });
                this.parentLayout.rebuildFragments(0);
                break;
            case 2:
                showListDialog(uItem, this.titles, LocaleController.getString(R.string.ActionBarTitle), ExteraConfig.getTitleText(), new PopupUtils.OnItemClickListener() { 
                    @Override 
                    public final void onClick(int i3) {
                        this.f$0.lambda$onClick$0(i3);
                    }
                });
                break;
            case 3:
                presentFragment(new PillStackPreferencesActivity());
                break;
            case 4:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setHideStories(((Boolean) obj).booleanValue());
                    }
                });
                getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.storiesEnabledUpdate, new Object[0]);
                break;
            case 5:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setHideActionBarStatus(((Boolean) obj).booleanValue());
                    }
                });
                ChatListPreviewCell chatListPreviewCell = this.chatListPreviewCell;
                if (chatListPreviewCell != null) {
                    chatListPreviewCell.updateStatus(true);
                }
                this.parentLayout.rebuildFragments(0);
                break;
            case 6:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setCenterTitle(((Boolean) obj).booleanValue());
                    }
                });
                ChatListPreviewCell chatListPreviewCell2 = this.chatListPreviewCell;
                if (chatListPreviewCell2 != null) {
                    chatListPreviewCell2.updateCentered(true);
                }
                ActionBar actionBar = this.actionBar;
                if (actionBar != null) {
                    actionBar.refreshTitlePosition(true);
                }
                this.parentLayout.rebuildFragments(0);
                break;
            case 7:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setHideFloatingButton(((Boolean) obj).booleanValue());
                    }
                });
                this.parentLayout.rebuildFragments(0);
                break;
            case 8:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setHideDialogsSearchBar(((Boolean) obj).booleanValue());
                    }
                });
                this.parentLayout.rebuildFragments(0);
                break;
            case 9:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setSenderMiniAvatars(((Boolean) obj).booleanValue());
                    }
                });
                break;
            case 10:
                showListDialog(uItem, this.tabIcons, LocaleController.getString(R.string.TabTitleStyle), ExteraConfig.getTabIcons().ordinal(), new PopupUtils.OnItemClickListener() { 
                    @Override 
                    public final void onClick(int i3) {
                        this.f$0.lambda$onClick$1(i3);
                    }
                });
                break;
            case 11:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setTabCounter(((Boolean) obj).booleanValue());
                    }
                });
                handleTabCounterClick();
                break;
            case 12:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setHideAllChats(((Boolean) obj).booleanValue());
                    }
                });
                handleHideAllChatsClick();
                break;
            case 13:
                presentFragment(new AppNavigationPreferencesActivity());
                break;
            case 14:
                presentFragment(new IconPacksActivity());
                break;
            case 15:
                if (ExteraConfig.getDividerStyle() != DividerStyle.SEGMENTS) {
                    toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                        @Override // com.google.android.exoplayer2.util.Consumer
                        public final void accept(Object obj) {
                            ExteraConfig.setSectionsSeparatedHeaders(((Boolean) obj).booleanValue());
                        }
                    });
                    this.listView.invalidateItemDecorations();
                    break;
                }
                break;
            case 16:
                showListDialog(uItem, this.dividerStyles, LocaleController.getString(R.string.DividerStyle), ExteraConfig.getDividerStyle().ordinal(), new PopupUtils.OnItemClickListener() { 
                    @Override 
                    public final void onClick(int i3) {
                        this.f$0.lambda$onClick$2(i3);
                    }
                });
                break;
            case 17:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setUseSystemFonts(((Boolean) obj).booleanValue());
                    }
                });
                handleUseSystemFontsClick();
                break;
            case 18:
                handleUseSystemEmojiClick(uItem);
                break;
            case 19:
            case 20:
            case 21:
            case 22:
            case 23:
            case 24:
                this.md3Styles.onClick(uItem);
                break;
            case 25:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setGooeyAvatarAnimation(((Boolean) obj).booleanValue());
                    }
                });
                break;
            case 26:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setCustomThemes(((Boolean) obj).booleanValue());
                    }
                });
                break;
            case 27:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setForceSnow(((Boolean) obj).booleanValue());
                    }
                });
                this.chatListPreviewCell.invalidate();
                break;
            case 28:
                showListDialog(uItem, this.glassOutlineStyles, LocaleController.getString(R.string.GlassOutlineStyle), ExteraConfig.getGlassOutlineStyle().ordinal(), new PopupUtils.OnItemClickListener() { 
                    @Override 
                    public final void onClick(int i3) {
                        this.f$0.lambda$onClick$3(i3);
                    }
                });
                break;
            case 29:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setForceBlur(((Boolean) obj).booleanValue());
                    }
                });
                handleForceBlurChange();
                break;
            case 30:
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setGlassMessageMenu(((Boolean) obj).booleanValue());
                    }
                });
                handleGlassMessageMenuChange();
                break;
        }
    }

    public static void lambda$createSectionRadiusSliderItem$4(Integer num) {
        ExteraConfig.setSectionRadius(num.intValue());
        handleSectionRadiusChange();
    }

    public CharSequence formatSectionRadius(int i) {
        if (i == 0) {
            return LocaleController.getString(R.string.BlurOff);
        }
        if (i == 28) {
            return LocaleController.getString(R.string.PredictiveBackMax);
        }
        return i + " dp";
    }

    private void handleDividerStyleChange() {
        Theme.applyCommonTheme();
        this.listView.invalidate();
        this.listView.invalidateItemDecorations();
        this.avatarCornersPreviewCell.invalidate();
        this.chatListPreviewCell.invalidate();
        this.fabShapeCell.invalidate();
        this.filterTabsPreviewCell.invalidate();
        this.parentLayout.rebuildFragments(0);
    }

    private void handleSectionRadiusChange() {
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.setSections();
            this.listView.invalidate();
            this.listView.invalidateItemDecorations();
        }
        this.parentLayout.rebuildFragments(0);
    }

    public void updateMD3Styles() {
        AvatarCornersPreviewCell avatarCornersPreviewCell = this.avatarCornersPreviewCell;
        if (avatarCornersPreviewCell != null) {
            avatarCornersPreviewCell.updateSliderStyle();
        }
        this.parentLayout.rebuildFragments(0);
    }

    private void handleActionBarTitleClick() {
        this.chatListPreviewCell.updateStatus(true);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.currentUserPremiumStatusChanged, new Object[0]);
    }

    private void handleTabTitleClick() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
    }

    private void handleTabCounterClick() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
    }

    private void handleHideAllChatsClick() {
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.dialogFiltersUpdated, new Object[0]);
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
    }

    private void handleUseSystemFontsClick() {
        AndroidUtilities.clearTypefaceCache();
        rebuildListWithStateRestore();
    }

    private void rebuildListWithStateRestore() {
        if (this.listView.getLayoutManager() != null) {
            this.recyclerViewState = this.listView.getLayoutManager().onSaveInstanceState();
        }
        this.parentLayout.rebuildFragments(1);
        if (this.listView.getLayoutManager() != null) {
            this.listView.getLayoutManager().onRestoreInstanceState(this.recyclerViewState);
        }
    }

    private void handleForceBlurChange() {
        if (SharedConfig.chatBlurEnabled() || !ExteraConfig.getForceBlur()) {
            return;
        }
        SharedConfig.toggleChatBlur();
    }

    private void handleGlassMessageMenuChange() {
        if (!ExteraConfig.getGlassMessageMenu() || SharedConfig.chatBlurEnabled()) {
            return;
        }
        BulletinFactory.of(this).createSimpleBulletin(R.raw.info, LocaleController.getString(R.string.GlassMessageMenuBlurOff), LocaleController.getString(R.string.Enable), new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                SharedConfig.toggleChatBlur();
            }
        }).show();
    }

    private void handleUseSystemEmojiClick(UItem uItem) {
        SharedConfig.toggleUseSystemEmoji();
        uItem.setChecked(!SharedConfig.useSystemEmoji);
        this.parentLayout.rebuildFragments(0);
        this.listView.adapter.update(true);
    }
}
