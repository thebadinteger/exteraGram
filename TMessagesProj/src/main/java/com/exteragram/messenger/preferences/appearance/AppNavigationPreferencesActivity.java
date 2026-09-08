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

    @Override 
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
        ExteraConfig.getMainMenuHiddenItems().removeIf(new Predicate() { 
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

    public static void lambda$createView$1(float f) {
        boolean zIsPredictiveBackOff = isPredictiveBackOff(ExteraConfig.getPredictiveBackIntensity());
        boolean zIsPredictiveBackOff2 = isPredictiveBackOff(f);
        ExteraConfig.setPredictiveBackIntensity(f);
        this.predictiveBackSeekbar.updateHeader(f);
        if (zIsPredictiveBackOff != zIsPredictiveBackOff2) {
            showRestartBulletin();
        }
    }

    public class AnonymousClass2 {
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

    public /* synthetic */ void lambda$onClick$3(int i) {
        ExteraConfig.getPreferences().edit().putInt("bottomNavigationBarMode", i).apply();
        BottomNavigationBar.setMode(i);
        ExteraConfig.ensureSettingsVisibility();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
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

    public /* synthetic */ void lambda$onClick$5(int i) {
        ExteraConfig.setTabletMode(i);
        showRestartBulletin();
    }

    private void saveAndNotify() {
        ExteraConfig.saveMainMenuLayout();
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
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
        getNotificationCenter().lambda$postNotificationNameOnUIThread$1(NotificationCenter.mainUserInfoChanged, new Object[0]);
        refreshEditorList();
        updateResetButtonVisibility();
    }
}
