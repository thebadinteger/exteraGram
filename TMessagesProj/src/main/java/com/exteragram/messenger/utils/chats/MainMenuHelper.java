package com.exteragram.messenger.utils.chats;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import com.exteragram.messenger.utils.RecordTag;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.MainMenuItem;
import com.exteragram.messenger.components.QRCodeSheet;
import com.exteragram.messenger.feed.ui.FeedActivity;
import com.exteragram.messenger.plugins.PluginsController;
import com.exteragram.messenger.plugins.hooks.MenuItemRecord;
import com.exteragram.messenger.plugins.ui.PluginsActivity;
import com.exteragram.messenger.plugins.utils.MenuContextBuilder;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntPredicate;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.BuildVars;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaDataController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.browser.Browser;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBarMenuSubItem;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.ActionIntroActivity;
import org.telegram.ui.CallLogActivity;
import org.telegram.ui.CameraScanActivity;
import org.telegram.ui.ChannelCreateActivity;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.ContactsActivity;
import org.telegram.ui.DialogsActivity;
import org.telegram.ui.GroupCreateActivity;
import org.telegram.ui.LaunchActivity;
import org.telegram.ui.ProfileActivity;
import org.telegram.ui.SettingsActivity;
import org.telegram.ui.WebAppDisclaimerAlert;
import org.telegram.ui.bots.BotWebViewSheet;
import org.telegram.ui.web.SearchEngine;

public abstract class MainMenuHelper {

    public static final class MenuItemInfo extends RecordTag {
        private final int iconRes;
        private final Runnable onClick;
        private final Runnable onLongClick;
        private final CharSequence text;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof MenuItemInfo)) {
                return false;
            }
            MenuItemInfo menuItemInfo = (MenuItemInfo) obj;
            return this.iconRes == menuItemInfo.iconRes && Objects.equals(this.text, menuItemInfo.text) && Objects.equals(this.onClick, menuItemInfo.onClick) && Objects.equals(this.onLongClick, menuItemInfo.onLongClick);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Integer.valueOf(this.iconRes), this.text, this.onClick, this.onLongClick};
        }

        public MenuItemInfo(int i, CharSequence charSequence, Runnable runnable, Runnable runnable2) {
            this.iconRes = i;
            this.text = charSequence;
            this.onClick = runnable;
            this.onLongClick = runnable2;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return java.util.Objects.hash(this.iconRes, this.text, this.onClick, this.onLongClick);
        }

        public int iconRes() {
            return this.iconRes;
        }

        public Runnable onClick() {
            return this.onClick;
        }

        public Runnable onLongClick() {
            return this.onLongClick;
        }

        public CharSequence text() {
            return this.text;
        }

        public final String toString() {
            return "MenuItemInfo[iconRes=" + this.iconRes + ", text=" + this.text + ", onClick=" + this.onClick + ", onLongClick=" + this.onLongClick + "]";
        }
    }

    public static final class MenuContext extends RecordTag {
        private final Runnable archiveClick;
        private final int currentAccount;
        private final BaseFragment fragment;
        private final Map<String, Object> pluginContextData;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof MenuContext)) {
                return false;
            }
            MenuContext menuContext = (MenuContext) obj;
            return this.currentAccount == menuContext.currentAccount && Objects.equals(this.fragment, menuContext.fragment) && Objects.equals(this.archiveClick, menuContext.archiveClick) && Objects.equals(this.pluginContextData, menuContext.pluginContextData);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Integer.valueOf(this.currentAccount), this.fragment, this.archiveClick, this.pluginContextData};
        }

        public MenuContext(int i, BaseFragment baseFragment, Runnable runnable, Map<String, Object> map) {
            this.currentAccount = i;
            this.fragment = baseFragment;
            this.archiveClick = runnable;
            this.pluginContextData = map;
        }

        public Runnable archiveClick() {
            return this.archiveClick;
        }

        public int currentAccount() {
            return this.currentAccount;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public BaseFragment fragment() {
            return this.fragment;
        }

        public final int hashCode() {
            return java.util.Objects.hash(this.currentAccount, this.fragment, this.archiveClick, this.pluginContextData);
        }

        public Map<String, Object> pluginContextData() {
            return this.pluginContextData;
        }

        public final String toString() {
            return "MenuContext[currentAccount=" + this.currentAccount + ", fragment=" + this.fragment + ", archiveClick=" + this.archiveClick + ", pluginContextData=" + this.pluginContextData + "]";
        }
    }

    public static final class AttachMenuBotInfo extends RecordTag {
        private final TLRPC.TL_attachMenuBot bot;
        private final int iconRes;
        private final Runnable onClick;
        private final Runnable onLongClick;
        private final CharSequence text;

        private /* synthetic */ boolean $record$equals(Object obj) {
            if (!(obj instanceof AttachMenuBotInfo)) {
                return false;
            }
            AttachMenuBotInfo attachMenuBotInfo = (AttachMenuBotInfo) obj;
            return this.iconRes == attachMenuBotInfo.iconRes && Objects.equals(this.text, attachMenuBotInfo.text) && Objects.equals(this.bot, attachMenuBotInfo.bot) && Objects.equals(this.onClick, attachMenuBotInfo.onClick) && Objects.equals(this.onLongClick, attachMenuBotInfo.onLongClick);
        }

        private /* synthetic */ Object[] $record$getFieldsAsObjects() {
            return new Object[]{Integer.valueOf(this.iconRes), this.text, this.bot, this.onClick, this.onLongClick};
        }

        public AttachMenuBotInfo(int i, CharSequence charSequence, TLRPC.TL_attachMenuBot tL_attachMenuBot, Runnable runnable, Runnable runnable2) {
            this.iconRes = i;
            this.text = charSequence;
            this.bot = tL_attachMenuBot;
            this.onClick = runnable;
            this.onLongClick = runnable2;
        }

        public TLRPC.TL_attachMenuBot bot() {
            return this.bot;
        }

        public final boolean equals(Object obj) {
            return $record$equals(obj);
        }

        public final int hashCode() {
            return java.util.Objects.hash(this.iconRes, this.text, this.bot, this.onClick, this.onLongClick);
        }

        public int iconRes() {
            return this.iconRes;
        }

        public Runnable onClick() {
            return this.onClick;
        }

        public Runnable onLongClick() {
            return this.onLongClick;
        }

        public CharSequence text() {
            return this.text;
        }

        public final String toString() {
            return "AttachMenuBotInfo[iconRes=" + this.iconRes + ", text=" + this.text + ", bot=" + this.bot + ", onClick=" + this.onClick + ", onLongClick=" + this.onLongClick + "]";
        }
    }

    public static MenuContext createMenuContext(int i, BaseFragment baseFragment) {
        return new MenuContext(i, baseFragment, null, null);
    }

    public static MenuContext createMenuContext(int i, BaseFragment baseFragment, Runnable runnable, Map<String, Object> map) {
        return new MenuContext(i, baseFragment, runnable, map);
    }

    public static Map<String, Object> createPluginContextData(int i, BaseFragment baseFragment) {
        MenuContextBuilder menuContextBuilderWithAccount = MenuContextBuilder.create().withAccount(i);
        if (baseFragment != null) {
            menuContextBuilderWithAccount.withContext(baseFragment.getContext() != null ? baseFragment.getContext() : baseFragment.getParentActivity());
        }
        TLRPC.User currentUser = UserConfig.getInstance(i).getCurrentUser();
        if (currentUser != null) {
            menuContextBuilderWithAccount.withUser(currentUser);
        }
        return menuContextBuilderWithAccount.build();
    }

    public static List<MenuItemInfo> resolveDrawerMenuItems(int i, MenuContext menuContext) {
        MainMenuItem byId = MainMenuItem.getById(i);
        if (byId == null) {
            return Collections.EMPTY_LIST;
        }
        int i2 = AnonymousClass2.$SwitchMap$com$exteragram$messenger$MainMenuItem[byId.ordinal()];
        if (i2 == 1) {
            return resolveDrawerBotMenuItems(menuContext);
        }
        if (i2 == 2) {
            return resolveDrawerPluginMenuItems(menuContext);
        }
        MenuItemInfo menuItemInfoResolveMenuItem = resolveMenuItem(i, menuContext);
        return menuItemInfoResolveMenuItem == null ? Collections.EMPTY_LIST : Collections.singletonList(menuItemInfoResolveMenuItem);
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.utils.chats.MainMenuHelper$2, reason: invalid class name */
    public static /* synthetic */ class AnonymousClass2 {
        static final /* synthetic */ int[] $SwitchMap$com$exteragram$messenger$MainMenuItem;

        static {
            int[] iArr = new int[MainMenuItem.values().length];
            $SwitchMap$com$exteragram$messenger$MainMenuItem = iArr;
            try {
                iArr[MainMenuItem.BOTS.ordinal()] = 1;
            } catch (NoSuchFieldError unused) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$MainMenuItem[MainMenuItem.PLUGINS.ordinal()] = 2;
            } catch (NoSuchFieldError unused2) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$MainMenuItem[MainMenuItem.PROFILE.ordinal()] = 3;
            } catch (NoSuchFieldError unused3) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$MainMenuItem[MainMenuItem.ARCHIVE.ordinal()] = 4;
            } catch (NoSuchFieldError unused4) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$MainMenuItem[MainMenuItem.NEW_GROUP.ordinal()] = 5;
            } catch (NoSuchFieldError unused5) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$MainMenuItem[MainMenuItem.CONTACTS.ordinal()] = 6;
            } catch (NoSuchFieldError unused6) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$MainMenuItem[MainMenuItem.CALLS.ordinal()] = 7;
            } catch (NoSuchFieldError unused7) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$MainMenuItem[MainMenuItem.NEW_CHANNEL.ordinal()] = 8;
            } catch (NoSuchFieldError unused8) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$MainMenuItem[MainMenuItem.SAVED.ordinal()] = 9;
            } catch (NoSuchFieldError unused9) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$MainMenuItem[MainMenuItem.FEED.ordinal()] = 10;
            } catch (NoSuchFieldError unused10) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$MainMenuItem[MainMenuItem.SETTINGS.ordinal()] = 11;
            } catch (NoSuchFieldError unused11) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$MainMenuItem[MainMenuItem.BROWSER.ordinal()] = 12;
            } catch (NoSuchFieldError unused12) {
            }
            try {
                $SwitchMap$com$exteragram$messenger$MainMenuItem[MainMenuItem.QR.ordinal()] = 13;
            } catch (NoSuchFieldError unused13) {
            }
        }
    }

    public static MenuItemInfo resolveMenuItem(int i, MenuContext menuContext) {
        MainMenuItem byId = MainMenuItem.getById(i);
        if (byId != null && menuContext.fragment() != null) {
            final int iCurrentAccount = menuContext.currentAccount();
            final BaseFragment baseFragmentFragment = menuContext.fragment();
            switch (AnonymousClass2.$SwitchMap$com$exteragram$messenger$MainMenuItem[byId.ordinal()]) {
                case 2:
                    if (PluginsController.isPluginEngineSupported()) {
                        return new MenuItemInfo(R.drawable.msg_plugins, LocaleController.getString(R.string.Plugins), new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda13
                            @Override // java.lang.Runnable
                            public final void run() {
                                baseFragmentFragment.presentFragment(new PluginsActivity());
                            }
                        }, null);
                    }
                    break;
                case 3:
                    return new MenuItemInfo(R.drawable.left_status_profile, LocaleController.getString(R.string.MyProfile), new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda2
                        @Override // java.lang.Runnable
                        public final void run() {
                            MainMenuHelper.m1505$r8$lambda$v0h8pBSNeYqoHvMbcetfzvFNlU(iCurrentAccount, baseFragmentFragment);
                        }
                    }, null);
                case 4:
                    return new MenuItemInfo(R.drawable.msg_archive, LocaleController.getString(R.string.ArchivedChats), menuContext.archiveClick() != null ? menuContext.archiveClick() : new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda5
                        @Override // java.lang.Runnable
                        public final void run() {
                            MainMenuHelper.m1498$r8$lambda$KDLBDKsgHSE6LBLdB8g62MSI00(baseFragmentFragment);
                        }
                    }, null);
                case 5:
                    return new MenuItemInfo(R.drawable.msg_groups, LocaleController.getString(R.string.NewGroup), new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda6
                        @Override // java.lang.Runnable
                        public final void run() {
                            baseFragmentFragment.presentFragment(new GroupCreateActivity(new Bundle()));
                        }
                    }, null);
                case 6:
                    return new MenuItemInfo(R.drawable.msg_contacts, LocaleController.getString(R.string.Contacts), new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda7
                        @Override // java.lang.Runnable
                        public final void run() {
                            MainMenuHelper.$r8$lambda$rtSRDj8a2WJm0LXRVKfT4uBArCY(baseFragmentFragment);
                        }
                    }, null);
                case 7:
                    return new MenuItemInfo(R.drawable.msg_calls, LocaleController.getString(R.string.Calls), new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda8
                        @Override // java.lang.Runnable
                        public final void run() {
                            baseFragmentFragment.presentFragment(new CallLogActivity());
                        }
                    }, null);
                case 8:
                    return new MenuItemInfo(R.drawable.msg_channel, LocaleController.getString(R.string.NewChannel), new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda9
                        @Override // java.lang.Runnable
                        public final void run() {
                            MainMenuHelper.$r8$lambda$Q8IakieKJqFN62CokMs4nVB419M(baseFragmentFragment);
                        }
                    }, null);
                case 9:
                    return new MenuItemInfo(R.drawable.msg_saved, LocaleController.getString(R.string.SavedMessages), new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda10
                        @Override // java.lang.Runnable
                        public final void run() {
                            MainMenuHelper.$r8$lambda$a5bq_jLS447MCh9JamahNb2Edmk(iCurrentAccount, baseFragmentFragment);
                        }
                    }, null);
                case 10:
                    return new MenuItemInfo(R.drawable.ic_feed, LocaleController.getString(R.string.Feed), new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda11
                        @Override // java.lang.Runnable
                        public final void run() {
                            FeedActivity.presentFeed(baseFragmentFragment);
                        }
                    }, null);
                case 11:
                    return new MenuItemInfo(R.drawable.msg_settings, LocaleController.getString(R.string.Settings), new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda12
                        @Override // java.lang.Runnable
                        public final void run() {
                            baseFragmentFragment.presentFragment(new SettingsActivity());
                        }
                    }, null);
                case 12:
                    return new MenuItemInfo(R.drawable.msg2_language, LocaleController.getString(R.string.BrowserSettingsTitle), new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda3
                        @Override // java.lang.Runnable
                        public final void run() {
                            MainMenuHelper.$r8$lambda$saqA7TDqFSmi2ZXfA6fwir6uv4I(baseFragmentFragment);
                        }
                    }, null);
                case 13:
                    return new MenuItemInfo(R.drawable.msg_qrcode, LocaleController.getString(R.string.AuthAnotherClient), new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda4
                        @Override // java.lang.Runnable
                        public final void run() {
                            MainMenuHelper.m1503$r8$lambda$i2DoKP95qCY24IrhC1YzQwp56E(baseFragmentFragment);
                        }
                    }, null);
                default:
                    return null;
            }
        }
        return null;
    }

    /* JADX INFO: renamed from: $r8$lambda$v0h8pBSNeYqoHv-MbcetfzvFNlU, reason: not valid java name */
    public static /* synthetic */ void m1505$r8$lambda$v0h8pBSNeYqoHvMbcetfzvFNlU(int i, BaseFragment baseFragment) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", UserConfig.getInstance(i).getClientUserId());
        bundle.putBoolean("my_profile", true);
        baseFragment.presentFragment(new ProfileActivity(bundle));
    }

    /* JADX INFO: renamed from: $r8$lambda$KDLBDKsgHSE6LBLdB8g6-2MSI00, reason: not valid java name */
    public static /* synthetic */ void m1498$r8$lambda$KDLBDKsgHSE6LBLdB8g62MSI00(BaseFragment baseFragment) {
        Bundle bundle = new Bundle();
        bundle.putInt("folderId", 1);
        baseFragment.presentFragment(new DialogsActivity(bundle));
    }

    public static /* synthetic */ void $r8$lambda$rtSRDj8a2WJm0LXRVKfT4uBArCY(BaseFragment baseFragment) {
        Bundle bundle = new Bundle();
        bundle.putBoolean("needPhonebook", true);
        bundle.putBoolean("needFinishFragment", false);
        baseFragment.presentFragment(new ContactsActivity(bundle));
    }

    public static /* synthetic */ void $r8$lambda$Q8IakieKJqFN62CokMs4nVB419M(BaseFragment baseFragment) {
        SharedPreferences globalMainSettings = MessagesController.getGlobalMainSettings();
        if (!BuildVars.DEBUG_VERSION && globalMainSettings.getBoolean("channel_intro", false)) {
            Bundle bundle = new Bundle();
            bundle.putInt("step", 0);
            baseFragment.presentFragment(new ChannelCreateActivity(bundle));
        } else {
            baseFragment.presentFragment(new ActionIntroActivity(0));
            globalMainSettings.edit().putBoolean("channel_intro", true).apply();
        }
    }

    public static /* synthetic */ void $r8$lambda$a5bq_jLS447MCh9JamahNb2Edmk(int i, BaseFragment baseFragment) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", UserConfig.getInstance(i).getClientUserId());
        baseFragment.presentFragment(new ChatActivity(bundle));
    }

    public static /* synthetic */ void $r8$lambda$saqA7TDqFSmi2ZXfA6fwir6uv4I(BaseFragment baseFragment) {
        SearchEngine current = SearchEngine.getCurrent();
        String homepage = current.getHomepage();
        Activity parentActivity = baseFragment.getParentActivity();
        if (TextUtils.isEmpty(homepage)) {
            homepage = current.search_url;
        }
        Browser.openInTelegramBrowser(parentActivity, homepage, null);
    }

    /* JADX INFO: renamed from: $r8$lambda$i2DoK-P95qCY24IrhC1YzQwp56E, reason: not valid java name */
    public static /* synthetic */ void m1503$r8$lambda$i2DoKP95qCY24IrhC1YzQwp56E(BaseFragment baseFragment) {
        LaunchActivity launchActivity = LaunchActivity.instance;
        if (launchActivity != null && launchActivity.checkSelfPermission("android.permission.CAMERA") != 0) {
            LaunchActivity.instance.requestPermissions(new String[]{"android.permission.CAMERA"}, 34);
        } else {
            CameraScanActivity.showAsSheet(baseFragment, true, 1, (CameraScanActivity.CameraScanActivityDelegate) new AnonymousClass1());
        }
    }

    /* JADX INFO: renamed from: com.exteragram.messenger.utils.chats.MainMenuHelper$1, reason: invalid class name */
    public static class AnonymousClass1 implements CameraScanActivity.CameraScanActivityDelegate {
        @Override // org.telegram.ui.CameraScanActivity.CameraScanActivityDelegate
        public boolean processQr(final String str, final Runnable runnable) {
            AndroidUtilities.runOnUIThread(new Runnable() {
                @Override
                public void run() {
                    AndroidUtilities.runOnUIThread(new Runnable() {
                        @Override
                        public void run() {
                            BaseFragment safeLastFragment = LaunchActivity.getSafeLastFragment();
                            if (safeLastFragment != null) {
                                new QRCodeSheet(safeLastFragment, str).show();
                            }
                        }
                    }, 150L);
                    if (runnable != null) {
                        runnable.run();
                    }
                }
            }, 600L);
            return true;
        }
    }

    /* JADX INFO: renamed from: $r8$lambda$je-2ITcORHHV0tfyHwX-LWEPGuI, reason: not valid java name */
    public static /* synthetic */ boolean m1504$r8$lambda$je2ITcORHHV0tfyHwXLWEPGuI(int i) {
        return false;
    }

    public static void addConfiguredItemOptions(ItemOptions itemOptions, MenuContext menuContext) {
        addConfiguredItemOptions(itemOptions, menuContext, new IntPredicate() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda20
            @Override // java.util.function.IntPredicate
            public final boolean test(int i) {
                return MainMenuHelper.m1504$r8$lambda$je2ITcORHHV0tfyHwXLWEPGuI(i);
            }
        });
    }

    public static void addConfiguredItemOptions(ItemOptions itemOptions, MenuContext menuContext, IntPredicate intPredicate) {
        boolean z = false;
        boolean z2 = false;
        for (int i = 0; i < ExteraConfig.getMainMenuLayout().size(); i++) {
            Integer num = ExteraConfig.getMainMenuLayout().get(i);
            if (num != null && !ExteraConfig.getMainMenuHiddenItems().contains(num)) {
                if (num.intValue() == MainMenuItem.DIVIDER.getId()) {
                    if (z) {
                        z2 = true;
                    }
                } else if (!intPredicate.test(num.intValue())) {
                    if (z2) {
                        itemOptions.addGap();
                        z2 = false;
                    }
                    if (addConfiguredItemOption(itemOptions, menuContext, num.intValue())) {
                        z = true;
                    }
                }
            }
        }
    }

    public static List<AttachMenuBotInfo> getAttachMenuBotItems(final MenuContext menuContext) {
        ArrayList<TLRPC.TL_attachMenuBot> arrayList;
        BaseFragment baseFragmentFragment = menuContext.fragment();
        LaunchActivity launchActivityFindLaunchActivity = findLaunchActivity(baseFragmentFragment);
        TLRPC.TL_attachMenuBots attachMenuBots = MediaDataController.getInstance(menuContext.currentAccount()).getAttachMenuBots();
        if (baseFragmentFragment == null || launchActivityFindLaunchActivity == null || attachMenuBots == null || (arrayList = attachMenuBots.bots) == null || arrayList.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList<TLRPC.TL_attachMenuBot> arrayList3 = attachMenuBots.bots;
        int size = arrayList3.size();
        int i = 0;
        while (i < size) {
            TLRPC.TL_attachMenuBot tL_attachMenuBot = arrayList3.get(i);
            i++;
            final TLRPC.TL_attachMenuBot tL_attachMenuBot2 = tL_attachMenuBot;
            if (tL_attachMenuBot2.show_in_side_menu) {
                arrayList2.add(new AttachMenuBotInfo(getAttachMenuBotIconRes(tL_attachMenuBot2), tL_attachMenuBot2.short_name, tL_attachMenuBot2, createAttachMenuBotClickAction(menuContext, tL_attachMenuBot2, launchActivityFindLaunchActivity), new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda17
                    @Override // java.lang.Runnable
                    public final void run() {
                        BotWebViewSheet.deleteBot(menuContext.currentAccount(), tL_attachMenuBot2.bot_id, null);
                    }
                }));
            }
        }
        return arrayList2;
    }

    private static boolean addConfiguredItemOption(ItemOptions itemOptions, MenuContext menuContext, int i) {
        MainMenuItem byId = MainMenuItem.getById(i);
        if (byId == null) {
            return false;
        }
        if (byId == MainMenuItem.ARCHIVE && !ChatUtils.getInstance(menuContext.currentAccount()).hasArchivedChats()) {
            return false;
        }
        if (byId == MainMenuItem.BOTS) {
            return addAttachMenuBotMenuItems(itemOptions, menuContext);
        }
        if (byId == MainMenuItem.PLUGINS) {
            return addPluginConfiguredItem(itemOptions, menuContext, byId);
        }
        MenuItemInfo menuItemInfoResolveMenuItem = resolveMenuItem(i, menuContext);
        if (menuItemInfoResolveMenuItem == null || menuItemInfoResolveMenuItem.onClick() == null) {
            return false;
        }
        itemOptions.add(menuItemInfoResolveMenuItem.iconRes(), menuItemInfoResolveMenuItem.text(), menuItemInfoResolveMenuItem.onClick());
        bindLongClick(itemOptions, menuItemInfoResolveMenuItem.onLongClick());
        return true;
    }

    public static List<MenuItemRecord> getPluginMenuItems(MenuContext menuContext) {
        return PluginsController.getInstance().getMenuItemsForLocation("main_menu", getPluginContextData(menuContext));
    }

    public static Runnable createPluginClickAction(final MenuItemRecord menuItemRecord, final MenuContext menuContext) {
        return new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda19
            @Override // java.lang.Runnable
            public final void run() {
                menuItemRecord.executeClick(MainMenuHelper.getPluginContextData(menuContext));
            }
        };
    }

    private static List<MenuItemInfo> resolveDrawerBotMenuItems(MenuContext menuContext) {
        List<AttachMenuBotInfo> attachMenuBotItems = getAttachMenuBotItems(menuContext);
        if (attachMenuBotItems.isEmpty()) {
            return Collections.EMPTY_LIST;
        }
        ArrayList arrayList = new ArrayList(attachMenuBotItems.size());
        for (AttachMenuBotInfo attachMenuBotInfo : attachMenuBotItems) {
            arrayList.add(new MenuItemInfo(attachMenuBotInfo.iconRes(), attachMenuBotInfo.text(), attachMenuBotInfo.onClick(), attachMenuBotInfo.onLongClick()));
        }
        return arrayList;
    }

    private static List<MenuItemInfo> resolveDrawerPluginMenuItems(final MenuContext menuContext) {
        if (menuContext.fragment() == null) {
            return Collections.EMPTY_LIST;
        }
        MenuItemInfo menuItemInfoResolveMenuItem = resolveMenuItem(MainMenuItem.PLUGINS.getId(), menuContext);
        List<MenuItemRecord> pluginMenuItems = getPluginMenuItems(menuContext);
        if (pluginMenuItems.isEmpty()) {
            return menuItemInfoResolveMenuItem == null ? Collections.EMPTY_LIST : Collections.singletonList(menuItemInfoResolveMenuItem);
        }
        ArrayList arrayList = new ArrayList(pluginMenuItems.size() + (menuItemInfoResolveMenuItem != null ? 1 : 0));
        if (menuItemInfoResolveMenuItem != null) {
            arrayList.add(menuItemInfoResolveMenuItem);
        }
        for (MenuItemRecord menuItemRecord : pluginMenuItems) {
            if (menuItemRecord != null && !TextUtils.isEmpty(menuItemRecord.getText())) {
                arrayList.add(new MenuItemInfo(menuItemRecord.getIconResId() != 0 ? menuItemRecord.getIconResId() : R.drawable.msg_plugins, menuItemRecord.getText(), createPluginClickAction(menuItemRecord, menuContext), new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda1
                    @Override // java.lang.Runnable
                    public final void run() {
                        menuContext.fragment().presentFragment(new PluginsActivity());
                    }
                }));
            }
        }
        if (arrayList.isEmpty()) {
            return menuItemInfoResolveMenuItem == null ? Collections.EMPTY_LIST : Collections.singletonList(menuItemInfoResolveMenuItem);
        }
        return arrayList;
    }

    private static Runnable createAttachMenuBotClickAction(final MenuContext menuContext, final TLRPC.TL_attachMenuBot tL_attachMenuBot, final LaunchActivity launchActivity) {
        return new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda18
            @Override // java.lang.Runnable
            public final void run() {
                MainMenuHelper.$r8$lambda$aYhptRjLYU6aWVNMAzGjtgDV_Ec(tL_attachMenuBot, menuContext, launchActivity);
            }
        };
    }

    public static /* synthetic */ void $r8$lambda$aYhptRjLYU6aWVNMAzGjtgDV_Ec(final TLRPC.TL_attachMenuBot tL_attachMenuBot, final MenuContext menuContext, final LaunchActivity launchActivity) {
        if (tL_attachMenuBot.inactive || tL_attachMenuBot.side_menu_disclaimer_needed) {
            WebAppDisclaimerAlert.show(menuContext.fragment().getContext() != null ? menuContext.fragment().getContext() : launchActivity, new Consumer() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda21
                @Override // com.google.android.exoplayer2.util.Consumer
                public final void accept(Object obj) {
                    MainMenuHelper.m1500$r8$lambda$WfS8uYcqUEI0TkIrA2c9j2WVY(menuContext, tL_attachMenuBot, launchActivity, (Boolean) obj);
                }
            }, null, null);
        } else {
            LaunchActivity.showAttachMenuBot(launchActivity, menuContext.currentAccount(), tL_attachMenuBot, null, true);
        }
    }

    /* JADX INFO: renamed from: $r8$lambda$W-fS8uY-cqUEI0TkIrA2c9j2WVY, reason: not valid java name */
    public static /* synthetic */ void m1500$r8$lambda$WfS8uYcqUEI0TkIrA2c9j2WVY(final MenuContext menuContext, final TLRPC.TL_attachMenuBot tL_attachMenuBot, final LaunchActivity launchActivity, Boolean bool) {
        TLRPC.TL_messages_toggleBotInAttachMenu tL_messages_toggleBotInAttachMenu = new TLRPC.TL_messages_toggleBotInAttachMenu();
        tL_messages_toggleBotInAttachMenu.bot = MessagesController.getInstance(menuContext.currentAccount()).getInputUser(tL_attachMenuBot.bot_id);
        tL_messages_toggleBotInAttachMenu.enabled = true;
        tL_messages_toggleBotInAttachMenu.write_allowed = true;
        ConnectionsManager.getInstance(menuContext.currentAccount()).sendRequest(tL_messages_toggleBotInAttachMenu, new RequestDelegate() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda22
            @Override // org.telegram.tgnet.RequestDelegate
            public final void run(TLObject tLObject, TLRPC.TL_error tL_error) {
                AndroidUtilities.runOnUIThread(new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda23
                    @Override // java.lang.Runnable
                    public final void run() {
                        MainMenuHelper.m1502$r8$lambda$eUXP3XGXU2KMhueQHg7MuztKQM(tL_attachMenuBot, launchActivity, menuContext);
                    }
                });
            }
        }, 66);
    }

    /* JADX INFO: renamed from: $r8$lambda$eUXP3XG-XU2KMhueQHg7MuztKQM, reason: not valid java name */
    public static /* synthetic */ void m1502$r8$lambda$eUXP3XGXU2KMhueQHg7MuztKQM(TLRPC.TL_attachMenuBot tL_attachMenuBot, LaunchActivity launchActivity, MenuContext menuContext) {
        tL_attachMenuBot.side_menu_disclaimer_needed = false;
        tL_attachMenuBot.inactive = false;
        LaunchActivity.showAttachMenuBot(launchActivity, menuContext.currentAccount(), tL_attachMenuBot, null, true);
        MediaDataController.getInstance(menuContext.currentAccount()).updateAttachMenuBotsInCache();
    }

    private static boolean addPluginConfiguredItem(final ItemOptions itemOptions, final MenuContext menuContext, MainMenuItem mainMenuItem) {
        final MenuItemInfo menuItemInfoResolveMenuItem = resolveMenuItem(mainMenuItem.getId(), menuContext);
        if (menuItemInfoResolveMenuItem == null || menuItemInfoResolveMenuItem.onClick() == null) {
            return false;
        }
        final List<MenuItemRecord> pluginMenuItems = getPluginMenuItems(menuContext);
        final ItemOptions itemOptionsMakeSwipeback = itemOptions.makeSwipeback();
        boolean zIsEmpty = pluginMenuItems.isEmpty();
        final boolean z = !zIsEmpty;
        if (!zIsEmpty) {
            itemOptionsMakeSwipeback.add(R.drawable.ic_ab_back, LocaleController.getString(R.string.Back), new Runnable() {
                @Override
                public void run() {
                    itemOptions.dismiss();
                }
            });
            itemOptionsMakeSwipeback.addGap();
            for (int i = 0; i < pluginMenuItems.size(); i++) {
                final int index = i;
                MenuItemRecord menuItemRecord = pluginMenuItems.get(i);
                String text = menuItemRecord.getText();
                if (text != null) {
                    itemOptionsMakeSwipeback.add(menuItemRecord.getIconResId(), text, new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda14
                        @Override // java.lang.Runnable
                        public final void run() {
                            MainMenuHelper.$r8$lambda$1UDS6cF5oxxIoHi5hFvo4jGO7uo(itemOptions, pluginMenuItems, index, menuContext);
                        }
                    });
                }
            }
        }
        itemOptions.add(menuItemInfoResolveMenuItem.iconRes(), menuItemInfoResolveMenuItem.text(), new Runnable() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda15
            @Override // java.lang.Runnable
            public final void run() {
                MainMenuHelper.m1499$r8$lambda$OfwWLwqHXMfyCwlbp7mrVF8fKA(z, itemOptions, itemOptionsMakeSwipeback, menuItemInfoResolveMenuItem);
            }
        });
        ActionBarMenuSubItem last = itemOptions.getLast();
        if (zIsEmpty || last == null) {
            return true;
        }
        last.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda16
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return MainMenuHelper.$r8$lambda$f5kxgmhG0LQrmPuZjc0uz002N0k(itemOptions, menuItemInfoResolveMenuItem, view);
            }
        });
        last.setRightIcon(R.drawable.msg_arrowright);
        return true;
    }

    public static /* synthetic */ void $r8$lambda$1UDS6cF5oxxIoHi5hFvo4jGO7uo(ItemOptions itemOptions, List list, int i, MenuContext menuContext) {
        itemOptions.dismiss();
        createPluginClickAction((MenuItemRecord) list.get(i), menuContext).run();
    }

    /* JADX INFO: renamed from: $r8$lambda$Of-wWLwqHXMfyCwlbp7mrVF8fKA, reason: not valid java name */
    public static /* synthetic */ void m1499$r8$lambda$OfwWLwqHXMfyCwlbp7mrVF8fKA(boolean z, ItemOptions itemOptions, ItemOptions itemOptions2, MenuItemInfo menuItemInfo) {
        if (z) {
            itemOptions.openSwipeback(itemOptions2);
        } else {
            menuItemInfo.onClick().run();
        }
    }

    public static /* synthetic */ boolean $r8$lambda$f5kxgmhG0LQrmPuZjc0uz002N0k(ItemOptions itemOptions, MenuItemInfo menuItemInfo, View view) {
        itemOptions.dismiss();
        menuItemInfo.onClick().run();
        return true;
    }

    private static boolean addAttachMenuBotMenuItems(ItemOptions itemOptions, MenuContext menuContext) {
        List<AttachMenuBotInfo> attachMenuBotItems = getAttachMenuBotItems(menuContext);
        if (attachMenuBotItems.isEmpty()) {
            return false;
        }
        for (AttachMenuBotInfo attachMenuBotInfo : attachMenuBotItems) {
            itemOptions.addBot(attachMenuBotInfo.bot(), attachMenuBotInfo.onClick(), attachMenuBotInfo.onLongClick());
        }
        return true;
    }

    private static void bindLongClick(final ItemOptions itemOptions, final Runnable runnable) {
        ActionBarMenuSubItem last;
        if (runnable == null || (last = itemOptions.getLast()) == null) {
            return;
        }
        last.setOnLongClickListener(new View.OnLongClickListener() { // from class: com.exteragram.messenger.utils.chats.MainMenuHelper$$ExternalSyntheticLambda0
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return MainMenuHelper.m1496$r8$lambda$Jxy_f6itoaiwRE26qy73G9W0AI(itemOptions, runnable, view);
            }
        });
    }

    /* JADX INFO: renamed from: $r8$lambda$-Jxy_f6itoaiwRE26qy73G9W0AI, reason: not valid java name */
    public static /* synthetic */ boolean m1496$r8$lambda$Jxy_f6itoaiwRE26qy73G9W0AI(ItemOptions itemOptions, Runnable runnable, View view) {
        itemOptions.dismiss();
        runnable.run();
        return true;
    }

    private static Map<String, Object> getPluginContextData(MenuContext menuContext) {
        if (menuContext.pluginContextData() != null) {
            return menuContext.pluginContextData();
        }
        return createPluginContextData(menuContext.currentAccount(), menuContext.fragment());
    }

    private static int getAttachMenuBotIconRes(TLRPC.TL_attachMenuBot tL_attachMenuBot) {
        return tL_attachMenuBot.bot_id == 1985737506 ? R.drawable.menu_wallet : R.drawable.msg_bot;
    }

    private static LaunchActivity findLaunchActivity(BaseFragment baseFragment) {
        if (baseFragment == null) {
            return LaunchActivity.instance;
        }
        Activity activityFindActivity = AndroidUtilities.findActivity(baseFragment.getContext() != null ? baseFragment.getContext() : baseFragment.getParentActivity());
        return activityFindActivity instanceof LaunchActivity ? (LaunchActivity) activityFindActivity : LaunchActivity.instance;
    }
}
