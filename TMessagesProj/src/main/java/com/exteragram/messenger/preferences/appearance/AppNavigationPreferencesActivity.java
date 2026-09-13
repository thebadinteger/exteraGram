package com.exteragram.messenger.preferences.appearance;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.MainMenuItem;
import com.exteragram.messenger.config.BottomNavigationBar;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.exteragram.messenger.preferences.components.AltSeekbar;
import com.exteragram.messenger.utils.ui.PopupUtils;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.function.Predicate;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.messenger.SharedConfig;
import org.telegram.messenger.Utilities;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.Components.BulletinFactory;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

public class AppNavigationPreferencesActivity extends BasePreferencesActivity {
    private CharSequence[] bottomNavigationModes;
    private AltSeekbar predictiveBackSeekbar;
    private Drawable reorderIcon;
    private ActionBarMenuItem resetItem;
    private CharSequence[] tabletMode;
    private final HashMap<Integer, ItemInfo> itemDetails = new HashMap<>();
    private final ArrayList<Integer> stableDividerIds = new ArrayList<>();
    private int nextDividerId = -2000;

    public enum AppNavigationItem {
        DRAWER,
        IMMERSIVE_ANIMATION,
        BOTTOM_NAVIGATION_BAR_MODE,
        PREDICTIVE_BACK_ANIMATION,
        SPRING_ANIMATIONS,
        TABLET_MODE;

        public int getId() {
            return ordinal() + 150;
        }

        public static AppNavigationItem fromId(int i) {
            int i2 = i - 150;
            if (i2 < 0 || i2 >= values().length) {
                return null;
            }
            return values()[i2];
        }
    }

    public static class ItemInfo {
        int iconRes;
        CharSequence name;

        public ItemInfo(CharSequence charSequence, int i) {
            this.name = charSequence;
            this.iconRes = i;
        }
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void initializeOptionStrings() {
        initItemDetails();
        this.tabletMode = new CharSequence[]{LocaleController.getString(R.string.DistanceUnitsAutomatic), LocaleController.getString(R.string.PasswordOn), LocaleController.getString(R.string.PasswordOff)};
        this.bottomNavigationModes = new CharSequence[]{LocaleController.getString(R.string.BottomNavigationModeShow), LocaleController.getString(R.string.BottomNavigationModeHide), LocaleController.getString(R.string.BottomNavigationModeFloating)};
    }

    private void initItemDetails() {
        this.itemDetails.put(Integer.valueOf(MainMenuItem.PROFILE.getId()), new ItemInfo(LocaleController.getString(R.string.MyProfile), R.drawable.left_status_profile));
        this.itemDetails.put(Integer.valueOf(MainMenuItem.ARCHIVE.getId()), new ItemInfo(LocaleController.getString(R.string.ArchivedChats), R.drawable.msg_archive));
        this.itemDetails.put(Integer.valueOf(MainMenuItem.BOTS.getId()), new ItemInfo(LocaleController.getString(R.string.FilterBots), R.drawable.msg_bot));
        this.itemDetails.put(Integer.valueOf(MainMenuItem.NEW_GROUP.getId()), new ItemInfo(LocaleController.getString(R.string.NewGroup), R.drawable.msg_groups));
        this.itemDetails.put(Integer.valueOf(MainMenuItem.CONTACTS.getId()), new ItemInfo(LocaleController.getString(R.string.Contacts), R.drawable.msg_contacts));
        this.itemDetails.put(Integer.valueOf(MainMenuItem.NEW_CHANNEL.getId()), new ItemInfo(LocaleController.getString(R.string.NewChannel), R.drawable.msg_channel));
        this.itemDetails.put(Integer.valueOf(MainMenuItem.CALLS.getId()), new ItemInfo(LocaleController.getString(R.string.Calls), R.drawable.msg_calls));
        this.itemDetails.put(Integer.valueOf(MainMenuItem.SAVED.getId()), new ItemInfo(LocaleController.getString(R.string.SavedMessages), R.drawable.msg_saved));
        this.itemDetails.put(Integer.valueOf(MainMenuItem.FEED.getId()), new ItemInfo(LocaleController.getString(R.string.Feed), R.drawable.ic_feed));
        this.itemDetails.put(Integer.valueOf(MainMenuItem.SETTINGS.getId()), new ItemInfo(LocaleController.getString(R.string.Settings), R.drawable.msg_settings_old));
        this.itemDetails.put(Integer.valueOf(MainMenuItem.PLUGINS.getId()), new ItemInfo(LocaleController.getString(R.string.Plugins), R.drawable.msg_plugins));
        this.itemDetails.put(Integer.valueOf(MainMenuItem.BROWSER.getId()), new ItemInfo(LocaleController.getString(R.string.BrowserSettingsTitle), R.drawable.msg2_language));
        this.itemDetails.put(Integer.valueOf(MainMenuItem.QR.getId()), new ItemInfo(LocaleController.getString(R.string.AuthAnotherClient), R.drawable.msg_qrcode));
        ExteraConfig.getMainMenuHiddenItems().removeIf(new Predicate() { // from class: com.exteragram.messenger.preferences.appearance.AppNavigationPreferencesActivity$$ExternalSyntheticLambda0
            @Override // java.util.function.Predicate
            public final boolean test(Object obj) {
                return AppNavigationPreferencesActivity.$r8$lambda$Po3A4RwV39YnktoT4g7JvvXtYv0((Integer) obj);
            }
        });
        this.stableDividerIds.clear();
        this.nextDividerId = -2000;
        ArrayList<Integer> mainMenuLayout = ExteraConfig.getMainMenuLayout();
        int size = mainMenuLayout.size();
        int i = 0;
        while (i < size) {
            Integer num = mainMenuLayout.get(i);
            i++;
            if (num.intValue() == MainMenuItem.DIVIDER.getId()) {
                ArrayList<Integer> arrayList = this.stableDividerIds;
                int i2 = this.nextDividerId;
                this.nextDividerId = i2 - 1;
                arrayList.add(Integer.valueOf(i2));
            }
        }
    }

    public static /* synthetic */ boolean $r8$lambda$Po3A4RwV39YnktoT4g7JvvXtYv0(Integer num) {
        return num.intValue() == MainMenuItem.DIVIDER.getId();
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public String getTitle() {
        return LocaleController.getString(R.string.AppNavigation);
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity, org.telegram.ui.ActionBar.BaseFragment
    public View createView(Context context) {
        final AppNavigationPreferencesActivity appNavigationPreferencesActivity;
        View viewCreateView = super.createView(context);
        if (Build.VERSION.SDK_INT >= 34) {
            appNavigationPreferencesActivity = this;
            appNavigationPreferencesActivity.predictiveBackSeekbar = new AltSeekbar(context, new AltSeekbar.OnDrag() { // from class: com.exteragram.messenger.preferences.appearance.AppNavigationPreferencesActivity$$ExternalSyntheticLambda1
                @Override // com.exteragram.messenger.preferences.components.AltSeekbar.OnDrag
                public final void run(float f) {
                    AppNavigationPreferencesActivity.this.lambda$createView$1(f);
                }
            }, 0, 2, LocaleController.getString(R.string.PredictiveBackIntensity), LocaleController.getString(R.string.BlurOff), LocaleController.getString(R.string.PredictiveBackMax)) { // from class: com.exteragram.messenger.preferences.appearance.AppNavigationPreferencesActivity.1
                @Override // com.exteragram.messenger.preferences.components.AltSeekbar
                public boolean useExactEndpointHaptic() {
                    return true;
                }

                @Override // com.exteragram.messenger.preferences.components.AltSeekbar
                public CharSequence getTextForHeader() {
                    float fRound = Math.round(this.currentValue * 10.0f) / 10.0f;
                    if (fRound <= 0.0f) {
                        return this.leftTextView.getText().toString().toUpperCase();
                    }
                    if (fRound >= 2.0f) {
                        return this.rightTextView.getText().toString().toUpperCase();
                    }
                    int i = (int) fRound;
                    if (fRound == i) {
                        return String.valueOf(i);
                    }
                    return String.format(Locale.US, "%.1f", Float.valueOf(fRound));
                }
            };
            float fMin = Math.min(ExteraConfig.getPredictiveBackIntensity(), 2.0f);
            if (fMin != ExteraConfig.getPredictiveBackIntensity()) {
                ExteraConfig.setPredictiveBackIntensity(fMin);
            }
            appNavigationPreferencesActivity.predictiveBackSeekbar.setProgress(fMin);
        } else {
            appNavigationPreferencesActivity = this;
        }
        ActionBarMenuItem actionBarMenuItemAddItem = appNavigationPreferencesActivity.actionBar.createMenu().addItem(0, R.drawable.msg_reset);
        appNavigationPreferencesActivity.resetItem = actionBarMenuItemAddItem;
        actionBarMenuItemAddItem.setContentDescription(LocaleController.getString(R.string.Reset));
        appNavigationPreferencesActivity.updateResetButtonVisibility();
        appNavigationPreferencesActivity.resetItem.setOnClickListener(new View.OnClickListener() { // from class: com.exteragram.messenger.preferences.appearance.AppNavigationPreferencesActivity$$ExternalSyntheticLambda2
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                AppNavigationPreferencesActivity.this.lambda$createView$2(view);
            }
        });
        UniversalRecyclerView universalRecyclerView = appNavigationPreferencesActivity.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.allowReorder(true);
            appNavigationPreferencesActivity.listView.listenReorder(new Utilities.Callback2() { // from class: com.exteragram.messenger.preferences.appearance.AppNavigationPreferencesActivity$$ExternalSyntheticLambda3
                @Override // org.telegram.messenger.Utilities.Callback2
                public final void run(Object obj, Object obj2) {
                    AppNavigationPreferencesActivity.this.updateConfigFromReorder(((Integer) obj).intValue(), (ArrayList) obj2);
                }
            });
        }
        return viewCreateView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$1(float f) {
        boolean zIsPredictiveBackOff = isPredictiveBackOff(ExteraConfig.getPredictiveBackIntensity());
        boolean zIsPredictiveBackOff2 = isPredictiveBackOff(f);
        ExteraConfig.setPredictiveBackIntensity(f);
        this.predictiveBackSeekbar.updateHeader(f);
        if (zIsPredictiveBackOff != zIsPredictiveBackOff2) {
            showRestartBulletin();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$createView$2(View view) {
        resetToDefault();
    }

    private boolean isPredictiveBackOff(float f) {
        return ((float) Math.round(f * 10.0f)) / 10.0f <= 0.0f;
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        if (this.reorderIcon == null) {
            this.reorderIcon = ContextCompat.getDrawable(getContext(), R.drawable.list_reorder);
        }
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.General)));
        arrayList.add(UItem.asButton(AppNavigationItem.TABLET_MODE.getId(), LocaleController.getString(R.string.TabletMode), this.tabletMode[ExteraConfig.getTabletMode()]).setSearchable(this).setLinkAlias("tabletMode", this));
        arrayList.add(UItem.asButton(AppNavigationItem.BOTTOM_NAVIGATION_BAR_MODE.getId(), LocaleController.getString(R.string.BottomNavigationBarMode), this.bottomNavigationModes[BottomNavigationBar.getMode()]).setSearchable(this).setLinkAlias("bottomNavigationBarMode", this));
        arrayList.add(UItem.asCheck(AppNavigationItem.SPRING_ANIMATIONS.getId(), LocaleController.getString(R.string.SpringAnimations)).setChecked(ExteraConfig.getSpringAnimations()).setSearchable(this).setLinkAlias("springAnimations", this));
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.SpringAnimationsInfo)));
        if (Build.VERSION.SDK_INT >= 34) {
            arrayList.add(UItem.asCustom(AppNavigationItem.PREDICTIVE_BACK_ANIMATION.getId(), this.predictiveBackSeekbar).setLinkAlias("predictiveBackAnimation", this));
            arrayList.add(UItem.asShadow(LocaleController.getString(R.string.PredictiveBackInfo)));
        }
        arrayList.add(UItem.asHeader(LocaleController.getString(R.string.AppNavigation)));
        arrayList.add(UItem.asCheck(AppNavigationItem.DRAWER.getId(), LocaleController.getString(R.string.NavigationDrawer)).setChecked(ExteraConfig.getNavigationDrawer()).setSearchable(this).setLinkAlias("navigationDrawer", this));
        if (ExteraConfig.getNavigationDrawer()) {
            arrayList.add(UItem.asCheck(AppNavigationItem.IMMERSIVE_ANIMATION.getId(), LocaleController.getString(R.string.NavigationDrawerImmersiveAnimation)).setChecked(ExteraConfig.getImmersiveDrawerAnimation()).setSearchable(this).setLinkAlias("immersiveDrawerAnimation", this));
        }
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.NavigationDrawerInfo)));
        addMenuSection(arrayList, universalAdapter, LocaleController.getString(R.string.MainMenuItems), ExteraConfig.getMainMenuLayout(), true);
        arrayList.add(UItem.asShadow(LocaleController.getString(R.string.MainMenuItemsInfo)));
        if (ExteraConfig.getMainMenuHiddenItems().isEmpty()) {
            return;
        }
        addMenuSection(arrayList, universalAdapter, LocaleController.getString(R.string.MainMenuHiddenItems), ExteraConfig.getMainMenuHiddenItems(), false);
        arrayList.add(UItem.asShadow(null));
    }

    private void addMenuSection(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter, String str, ArrayList<Integer> arrayList2, boolean z) {
        universalAdapter.whiteSectionStart();
        arrayList.add(UItem.asHeader(str));
        universalAdapter.reorderSectionStart();
        int size = arrayList2.size();
        int i = 0;
        int i2 = 0;
        while (i2 < size) {
            Integer num = arrayList2.get(i2);
            i2++;
            Integer num2 = num;
            if (num2.intValue() != MainMenuItem.PLUGINS.getId() || PluginsController.isPluginEngineSupported()) {
                int iIntValue = num2.intValue();
                MainMenuItem mainMenuItem = MainMenuItem.DIVIDER;
                if (iIntValue == mainMenuItem.getId()) {
                    if (z && i < this.stableDividerIds.size()) {
                        arrayList.add(createMenuItem(this.stableDividerIds.get(i).intValue(), null));
                    } else if (!z) {
                        arrayList.add(createMenuItem(mainMenuItem.getId(), null));
                    }
                    i++;
                } else {
                    ItemInfo itemInfo = this.itemDetails.get(num2);
                    if (itemInfo != null) {
                        arrayList.add(createMenuItem(num2.intValue(), itemInfo));
                    }
                }
            }
        }
        universalAdapter.reorderSectionEnd();
        if (z) {
            arrayList.add(UItem.asButton(-200, R.drawable.msg_add, LocaleController.getString(R.string.MainMenuAddDivider)).accent());
        }
        universalAdapter.whiteSectionEnd();
    }

    private UItem createMenuItem(int i, ItemInfo itemInfo) {
        UItem uItemAsButton;
        if (i <= -2000 || i == MainMenuItem.DIVIDER.getId()) {
            uItemAsButton = UItem.asButton(i, R.drawable.msg_block, LocaleController.getString(R.string.MainMenuDivider));
        } else {
            if (itemInfo == null) {
                return null;
            }
            uItemAsButton = UItem.asButton(i, itemInfo.iconRes, itemInfo.name);
        }
        uItemAsButton.object2 = this.reorderIcon;
        return uItemAsButton;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateConfigFromReorder(int i, ArrayList<UItem> arrayList) {
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        int size = arrayList.size();
        int i2 = 0;
        while (i2 < size) {
            UItem uItem = arrayList.get(i2);
            i2++;
            UItem uItem2 = uItem;
            int i3 = uItem2.id;
            if (i3 > -2000 && i3 != MainMenuItem.DIVIDER.getId()) {
                arrayList2.add(Integer.valueOf(uItem2.id));
            } else if (i == 0) {
                arrayList2.add(Integer.valueOf(MainMenuItem.DIVIDER.getId()));
                int i4 = uItem2.id;
                if (i4 > -2000) {
                    i4 = this.nextDividerId;
                    this.nextDividerId = i4 - 1;
                }
                arrayList3.add(Integer.valueOf(i4));
            }
        }
        if (i == 0) {
            this.stableDividerIds.clear();
            this.stableDividerIds.addAll(arrayList3);
            if (BottomNavigationBar.hidden()) {
                Integer numValueOf = Integer.valueOf(MainMenuItem.SETTINGS.getId());
                if (!arrayList2.contains(numValueOf)) {
                    arrayList2.add(numValueOf);
                }
            }
            ExteraConfig.getMainMenuLayout().clear();
            ExteraConfig.getMainMenuLayout().addAll(arrayList2);
        } else if (i == 1) {
            if (BottomNavigationBar.hidden()) {
                arrayList2.remove(Integer.valueOf(MainMenuItem.SETTINGS.getId()));
            }
            ExteraConfig.getMainMenuHiddenItems().clear();
            ExteraConfig.getMainMenuHiddenItems().addAll(arrayList2);
        }
        saveAndNotify();
    }

    @Override // com.exteragram.messenger.preferences.BasePreferencesActivity
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        UniversalAdapter universalAdapter;
        int i2 = uItem.id;
        AppNavigationItem appNavigationItemFromId = AppNavigationItem.fromId(i2);
        int i3 = 0;
        if (appNavigationItemFromId != null) {
            int i4 = AnonymousClass2.$SwitchMap$com$exteragram$messenger$preferences$appearance$AppNavigationPreferencesActivity$AppNavigationItem[appNavigationItemFromId.ordinal()];
            if (i4 == 1) {
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { // from class: com.exteragram.messenger.preferences.appearance.AppNavigationPreferencesActivity$$ExternalSyntheticLambda4
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setNavigationDrawer(((Boolean) obj).booleanValue());
                    }
                });
                getNotificationCenter().postNotificationNameOnUIThread(NotificationCenter.mainUserInfoChanged, new Object[0]);
                UniversalRecyclerView universalRecyclerView = this.listView;
                if (universalRecyclerView != null && (universalAdapter = universalRecyclerView.adapter) != null) {
                    universalAdapter.update(true);
                }
                this.parentLayout.rebuildFragments(0);
                return;
            }
            if (i4 == 2) {
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { // from class: com.exteragram.messenger.preferences.appearance.AppNavigationPreferencesActivity$$ExternalSyntheticLambda5
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        ExteraConfig.setImmersiveDrawerAnimation(((Boolean) obj).booleanValue());
                    }
                });
                return;
            }
            if (i4 == 3) {
                showListDialog(uItem, this.bottomNavigationModes, LocaleController.getString(R.string.BottomNavigationBarMode), BottomNavigationBar.getMode(), new PopupUtils.OnItemClickListener() { // from class: com.exteragram.messenger.preferences.appearance.AppNavigationPreferencesActivity$$ExternalSyntheticLambda6
                    @Override // com.exteragram.messenger.utils.ui.PopupUtils.OnItemClickListener
                    public final void onClick(int i5) {
                        AppNavigationPreferencesActivity.this.lambda$onClick$3(i5);
                    }
                });
                return;
            } else if (i4 == 4) {
                toggleBooleanSettingAndRefresh(uItem, new Consumer() { // from class: com.exteragram.messenger.preferences.appearance.AppNavigationPreferencesActivity$$ExternalSyntheticLambda7
                    @Override // com.google.android.exoplayer2.util.Consumer
                    public final void accept(Object obj) {
                        AppNavigationPreferencesActivity.$r8$lambda$0tG8sy57nk6hiFfqvuWyUcYp4Cg((Boolean) obj);
                    }
                });
                return;
            } else {
                if (i4 != 5) {
                    return;
                }
                showListDialog(uItem, this.tabletMode, LocaleController.getString(R.string.TabletMode), ExteraConfig.getTabletMode(), new PopupUtils.OnItemClickListener() { // from class: com.exteragram.messenger.preferences.appearance.AppNavigationPreferencesActivity$$ExternalSyntheticLambda8
                    @Override // com.exteragram.messenger.utils.ui.PopupUtils.OnItemClickListener
                    public final void onClick(int i5) {
                        AppNavigationPreferencesActivity.this.lambda$onClick$5(i5);
                    }
                });
                return;
            }
        }
        if (i2 == -200) {
            ArrayList<Integer> arrayList = this.stableDividerIds;
            int i5 = this.nextDividerId;
            this.nextDividerId = i5 - 1;
            arrayList.add(Integer.valueOf(i5));
            ExteraConfig.getMainMenuLayout().add(Integer.valueOf(MainMenuItem.DIVIDER.getId()));
            saveAndNotify();
            return;
        }
        if (i2 <= -2000) {
            int iIndexOf = this.stableDividerIds.indexOf(Integer.valueOf(i2));
            if (iIndexOf != -1) {
                int i6 = 0;
                while (true) {
                    if (i3 >= ExteraConfig.getMainMenuLayout().size()) {
                        i3 = -1;
                        break;
                    }
                    if (ExteraConfig.getMainMenuLayout().get(i3).intValue() == MainMenuItem.DIVIDER.getId()) {
                        if (i6 == iIndexOf) {
                            break;
                        } else {
                            i6++;
                        }
                    }
                    i3++;
                }
                if (i3 != -1) {
                    this.stableDividerIds.remove(iIndexOf);
                    ExteraConfig.getMainMenuLayout().remove(i3);
                    saveAndNotify();
                    return;
                }
                return;
            }
            return;
        }
        MainMenuItem mainMenuItem = MainMenuItem.DIVIDER;
        if (i2 == mainMenuItem.getId()) {
            ExteraConfig.getMainMenuHiddenItems().remove(Integer.valueOf(i2));
            saveAndNotify();
            return;
        }
        if (BottomNavigationBar.hidden() && i2 == MainMenuItem.SETTINGS.getId() && ExteraConfig.getMainMenuLayout().contains(Integer.valueOf(i2))) {
            BulletinFactory.of(this).createErrorBulletin(LocaleController.getString(R.string.MainMenuRemoveSettingsInfo)).show();
            return;
        }
        if (ExteraConfig.getMainMenuLayout().contains(Integer.valueOf(i2))) {
            ExteraConfig.getMainMenuLayout().remove(Integer.valueOf(i2));
            if (i2 != mainMenuItem.getId() && !ExteraConfig.getMainMenuHiddenItems().contains(Integer.valueOf(i2))) {
                ExteraConfig.getMainMenuHiddenItems().add(0, Integer.valueOf(i2));
            }
        } else if (ExteraConfig.getMainMenuHiddenItems().contains(Integer.valueOf(i2))) {
            ExteraConfig.getMainMenuHiddenItems().remove(Integer.valueOf(i2));
            ExteraConfig.getMainMenuLayout().add(Integer.valueOf(i2));
        }
        saveAndNotify();
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.preferences.appearance.AppNavigationPreferencesActivity$2, reason: invalid class name */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$exteragram$messenger$preferences$appearance$AppNavigationPreferencesActivity$AppNavigationItem;

        static {
            int[] iArr = new int[AppNavigationItem.values().length];
            $SwitchMap$com$exteragram$messenger$preferences$appearance$AppNavigationPreferencesActivity$AppNavigationItem = iArr;
            try {
                iArr[AppNavigationItem.DRAWER.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$preferences$appearance$AppNavigationPreferencesActivity$AppNavigationItem[AppNavigationItem.IMMERSIVE_ANIMATION.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$preferences$appearance$AppNavigationPreferencesActivity$AppNavigationItem[AppNavigationItem.BOTTOM_NAVIGATION_BAR_MODE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$preferences$appearance$AppNavigationPreferencesActivity$AppNavigationItem[AppNavigationItem.SPRING_ANIMATIONS.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$preferences$appearance$AppNavigationPreferencesActivity$AppNavigationItem[AppNavigationItem.TABLET_MODE.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onClick$3(int i) {
        ExteraConfig.getPreferences().edit().putInt("bottomNavigationBarMode", i).apply();
        BottomNavigationBar.setMode(i);
        ExteraConfig.ensureSettingsVisibility();
        getNotificationCenter().postNotificationNameOnUIThread(NotificationCenter.mainUserInfoChanged, new Object[0]);
        refreshEditorList();
        updateResetButtonVisibility();
        this.parentLayout.rebuildFragments(0);
    }

    public static /* synthetic */ void $r8$lambda$0tG8sy57nk6hiFfqvuWyUcYp4Cg(Boolean bool) {
        ExteraConfig.setSpringAnimations(bool.booleanValue());
        if (bool.booleanValue()) {
            MessagesController.getGlobalMainSettings().edit().putBoolean("view_animations", true).apply();
            SharedConfig.setAnimationsEnabled(true);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void lambda$onClick$5(int i) {
        ExteraConfig.setTabletMode(i);
        showRestartBulletin();
    }

    private void saveAndNotify() {
        ExteraConfig.saveMainMenuLayout();
        getNotificationCenter().postNotificationNameOnUIThread(NotificationCenter.mainUserInfoChanged, new Object[0]);
        refreshEditorList();
        updateResetButtonVisibility();
    }

    private void refreshEditorList() {
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView == null || universalRecyclerView.adapter == null) {
            return;
        }
        universalRecyclerView.hideSelector(false);
        this.listView.cancelClickRunnables(false);
        this.listView.adapter.update(true);
    }

    private void updateResetButtonVisibility() {
        if (this.resetItem == null) {
            return;
        }
        boolean zEquals = ExteraConfig.getMainMenuLayout().equals(ExteraConfig.getDefaultMainMenuLayout());
        if (!zEquals && this.resetItem.getVisibility() == 8) {
            AndroidUtilities.updateViewVisibilityAnimated(this.resetItem, true, 0.5f, true);
        } else if (zEquals && this.resetItem.getVisibility() == 0) {
            AndroidUtilities.updateViewVisibilityAnimated(this.resetItem, false, 0.5f, true);
        }
    }

    private void resetToDefault() {
        ExteraConfig.getMainMenuLayout().clear();
        ExteraConfig.getMainMenuLayout().addAll(ExteraConfig.getDefaultMainMenuLayout());
        ExteraConfig.getMainMenuHiddenItems().clear();
        for (MainMenuItem mainMenuItem : MainMenuItem.getEntries()) {
            if (mainMenuItem != MainMenuItem.DIVIDER && !ExteraConfig.getMainMenuLayout().contains(Integer.valueOf(mainMenuItem.getId())) && (mainMenuItem != MainMenuItem.PLUGINS || PluginsController.isPluginEngineSupported())) {
                ExteraConfig.getMainMenuHiddenItems().add(Integer.valueOf(mainMenuItem.getId()));
            }
        }
        this.stableDividerIds.clear();
        this.nextDividerId = -2000;
        ArrayList<Integer> mainMenuLayout = ExteraConfig.getMainMenuLayout();
        int size = mainMenuLayout.size();
        int i = 0;
        while (i < size) {
            Integer num = mainMenuLayout.get(i);
            i++;
            if (num.intValue() == MainMenuItem.DIVIDER.getId()) {
                ArrayList<Integer> arrayList = this.stableDividerIds;
                int i2 = this.nextDividerId;
                this.nextDividerId = i2 - 1;
                arrayList.add(Integer.valueOf(i2));
            }
        }
        ExteraConfig.saveMainMenuLayout();
        getNotificationCenter().postNotificationNameOnUIThread(NotificationCenter.mainUserInfoChanged, new Object[0]);
        refreshEditorList();
        updateResetButtonVisibility();
    }
}
