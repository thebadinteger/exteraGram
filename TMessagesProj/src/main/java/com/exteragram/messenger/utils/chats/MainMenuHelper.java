package com.exteragram.messenger.utils.chats;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import com.android.tools.r8.RecordTag;
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

        private void m1505$r8$lambda$v0h8pBSNeYqoHvMbcetfzvFNlU(int i, BaseFragment baseFragment) {
        Bundle bundle = new Bundle();
        bundle.putLong("user_id", UserConfig.getInstance(i).getClientUserId());
        bundle.putBoolean("my_profile", true);
        baseFragment.presentFragment(new ProfileActivity(bundle));
    }

    public static void $r8$lambda$PXFIpgdtIGghglRNcVl91ndQqRc(final String str, Runnable runnable) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    MainMenuHelper.AnonymousClass1.$r8$lambda$wwDUSw59NnCN0TJApzf27CVLJhA(str);
                }
            }, 150L);
            runnable.run();
        }

        public static void $r8$lambda$aYhptRjLYU6aWVNMAzGjtgDV_Ec(final TLRPC.TL_attachMenuBot tL_attachMenuBot, final MenuContext menuContext, final LaunchActivity launchActivity) {
        if (tL_attachMenuBot.inactive || tL_attachMenuBot.side_menu_disclaimer_needed) {
            WebAppDisclaimerAlert.show(menuContext.fragment().getContext() != null ? menuContext.fragment().getContext() : launchActivity, new Consumer() { 
                @Override // com.google.android.exoplayer2.util.Consumer
                public final void accept(Object obj) {
                    MainMenuHelper.m1500$r8$lambda$WfS8uYcqUEI0TkIrA2c9j2WVY(menuContext, tL_attachMenuBot, launchActivity, (Boolean) obj);
                }
            }, null, null);
        } else {
            LaunchActivity.showAttachMenuBot(launchActivity, menuContext.currentAccount(), tL_attachMenuBot, null, true);
        }
    }

    public static void m1502$r8$lambda$eUXP3XGXU2KMhueQHg7MuztKQM(TLRPC.TL_attachMenuBot tL_attachMenuBot, LaunchActivity launchActivity, MenuContext menuContext) {
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
            itemOptionsMakeSwipeback.add(R.drawable.ic_ab_back, LocaleController.getString(R.string.Back), new RatePill$$ExternalSyntheticLambda1(itemOptions));
            itemOptionsMakeSwipeback.addGap();
            for (final int i = 0; i < pluginMenuItems.size(); i++) {
                MenuItemRecord menuItemRecord = pluginMenuItems.get(i);
                String text = menuItemRecord.getText();
                if (text != null) {
                    itemOptionsMakeSwipeback.add(menuItemRecord.getIconResId(), text, new Runnable() { 
                        @Override // java.lang.Runnable
                        public final void run() {
                            MainMenuHelper.$r8$lambda$1UDS6cF5oxxIoHi5hFvo4jGO7uo(itemOptions, pluginMenuItems, i, menuContext);
                        }
                    });
                }
            }
        }
        itemOptions.add(menuItemInfoResolveMenuItem.iconRes(), menuItemInfoResolveMenuItem.text(), new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                MainMenuHelper.m1499$r8$lambda$OfwWLwqHXMfyCwlbp7mrVF8fKA(z, itemOptions, itemOptionsMakeSwipeback, menuItemInfoResolveMenuItem);
            }
        });
        ActionBarMenuSubItem last = itemOptions.getLast();
        if (zIsEmpty || last == null) {
            return true;
        }
        last.setOnLongClickListener(new View.OnLongClickListener() { 
            @Override // android.view.View.OnLongClickListener
            public final boolean onLongClick(View view) {
                return MainMenuHelper.$r8$lambda$f5kxgmhG0LQrmPuZjc0uz002N0k(itemOptions, menuItemInfoResolveMenuItem, view);
            }
        });
        last.setRightIcon(R.drawable.msg_arrowright);
        return true;
    }

    public static boolean m1496$r8$lambda$Jxy_f6itoaiwRE26qy73G9W0AI(ItemOptions itemOptions, Runnable runnable, View view) {
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
