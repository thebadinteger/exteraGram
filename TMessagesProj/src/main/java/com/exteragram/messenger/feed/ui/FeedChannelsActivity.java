package com.exteragram.messenger.feed.ui;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.feed.FeedChannelActions;
import com.exteragram.messenger.feed.FeedConfig;
import com.exteragram.messenger.feed.FeedController;
import com.exteragram.messenger.preferences.BasePreferencesActivity;
import com.exteragram.messenger.preferences.utils.SettingsRegistry;
import com.exteragram.messenger.utils.system.VibratorUtils;
import com.exteragram.messenger.utils.text.LocaleUtils;
import com.google.android.exoplayer2.util.Consumer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.function.Function;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.NotificationCenter;
import org.telegram.messenger.R;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.ActionBar;
import org.telegram.ui.ActionBar.ActionBarMenu;
import org.telegram.ui.ActionBar.ActionBarMenuItem;
import org.telegram.ui.ChatActivity;
import org.telegram.ui.Components.ItemOptions;
import org.telegram.ui.Components.UItem;
import org.telegram.ui.Components.UniversalAdapter;
import org.telegram.ui.Components.UniversalRecyclerView;

public class FeedChannelsActivity extends BasePreferencesActivity implements NotificationCenter.NotificationCenterDelegate {
    private static final Comparator<TLRPC.Chat> BY_TITLE = Comparator.comparing(new Function() { 
        @Override // java.util.function.Function
        public final Object apply(Object obj) {
            return FeedChannelsActivity.m1175$r8$lambda$8jZC3uUtA5lQgWojATIJNPnZXY((TLRPC.Chat) obj);
        }
    });
    private final ArrayList<TLRPC.Chat> channels = new ArrayList<>();
    private ActionBarMenuItem otherItem;
    private String query;
    private boolean searching;

    public static void lambda$reloadChannels$1(ArrayList arrayList, int i, boolean z, int i2) {
        if (z) {
            return;
        }
        this.channels.clear();
        for (int i3 = 0; i3 < arrayList.size(); i3++) {
            TLRPC.Chat chat = (TLRPC.Chat) arrayList.get(i3);
            TLRPC.Chat chat2 = getMessagesController().getChat(Long.valueOf(chat.id));
            ArrayList<TLRPC.Chat> arrayList2 = this.channels;
            if (chat2 != null) {
                chat = chat2;
            }
            arrayList2.add(chat);
        }
        this.channels.sort(BY_TITLE);
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.adapter.update(true);
        }
    }

    private void removeChannel(long j) {
        for (int i = 0; i < this.channels.size(); i++) {
            if ((-this.channels.get(i).id) == j) {
                this.channels.remove(i);
                UniversalRecyclerView universalRecyclerView = this.listView;
                if (universalRecyclerView != null) {
                    universalRecyclerView.adapter.update(true);
                    return;
                }
                return;
            }
        }
    }

    @Override 
    public void fillItems(ArrayList<UItem> arrayList, UniversalAdapter universalAdapter) {
        String str;
        FeedConfig feedConfig = FeedConfig.getInstance(this.currentAccount);
        boolean zIsEmpty = TextUtils.isEmpty(this.query);
        if (zIsEmpty) {
            arrayList.add(UItem.asHeader(LocaleController.getString(R.string.General)));
            arrayList.add(UItem.asCheck(1073741822, SettingsRegistry.markAsNewFeature("Feed-BottomTab") ? LocaleUtils.applyNewSpan(LocaleController.getString(R.string.FeedBottomTab)) : LocaleController.getString(R.string.FeedBottomTab), LocaleController.getString(R.string.FeedBottomTabInfo), true).setChecked(ExteraConfig.getShowFeedTab()));
            arrayList.add(UItem.asCheck(1073741820, LocaleController.getString(R.string.FeedUnreadCounter)).setChecked(ExteraConfig.getShowFeedUnreadCounter()));
            arrayList.add(UItem.asCheck(1073741823, LocaleController.getString(R.string.FeedIncludeArchived)).setChecked(feedConfig.getIncludeArchived()));
            arrayList.add(UItem.asShadow(LocaleController.getString(R.string.FeedIncludeArchivedInfo)));
        }
        ArrayList arrayList2 = new ArrayList();
        ArrayList arrayList3 = new ArrayList();
        for (int i = 0; i < this.channels.size(); i++) {
            TLRPC.Chat chat = this.channels.get(i);
            if (zIsEmpty || ((str = chat.title) != null && str.toLowerCase().contains(this.query))) {
                boolean zIsExcluded = feedConfig.isExcluded(-chat.id);
                (!zIsExcluded ? arrayList2 : arrayList3).add(UItem.asUserCheckbox((int) chat.id, chat).setChecked(!zIsExcluded));
            }
        }
        if (!arrayList2.isEmpty()) {
            arrayList.add(UItem.asHeader(LocaleController.getString(R.string.FeedShownChannels)));
            arrayList.addAll(arrayList2);
        }
        if (!arrayList3.isEmpty()) {
            if (!arrayList2.isEmpty()) {
                arrayList.add(UItem.asShadow());
            }
            arrayList.add(UItem.asHeader(LocaleController.getString(R.string.FeedHiddenChannels)));
            arrayList.addAll(arrayList3);
        }
        if (zIsEmpty) {
            if (arrayList2.isEmpty() && arrayList3.isEmpty()) {
                return;
            }
            arrayList.add(UItem.asShadow(LocaleController.getString(R.string.FeedChannelsInfo)));
        }
    }

    @Override 
    public void onClick(UItem uItem, View view, int i, float f, float f2) {
        Object obj = uItem.object;
        if (obj instanceof TLRPC.Chat) {
            final TLRPC.Chat chat = (TLRPC.Chat) obj;
            toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                @Override // com.google.android.exoplayer2.util.Consumer
                public final void accept(Object obj2) {
                    this.f$0.lambda$onClick$2(chat, (Boolean) obj2);
                }
            });
            return;
        }
        int i2 = uItem.id;
        if (i2 == 1073741822) {
            ExteraConfig.setShowFeedTab(!ExteraConfig.getShowFeedTab());
            UniversalRecyclerView universalRecyclerView = this.listView;
            if (universalRecyclerView != null) {
                universalRecyclerView.adapter.update(true);
            }
            NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.feedTabVisibleToggled, new Object[0]);
            return;
        }
        if (i2 == 1073741820) {
            toggleBooleanSettingAndRefresh(uItem, new Consumer() { 
                @Override // com.google.android.exoplayer2.util.Consumer
                public final void accept(Object obj2) {
                    this.f$0.lambda$onClick$3((Boolean) obj2);
                }
            });
        } else if (i2 == 1073741823) {
            FeedConfig feedConfig = FeedConfig.getInstance(this.currentAccount);
            feedConfig.setIncludeArchived(!feedConfig.getIncludeArchived());
            reloadChannels();
        }
    }

    public void lambda$onLongClick$4(TLRPC.Chat chat) {
        presentFragment(ChatActivity.of(-chat.id));
    }

    public /* synthetic */ void lambda$onLongClick$5(TLRPC.Chat chat) {
        FeedChannelActions.leaveChannel(this, chat, null, null);
    }

    public void setAllExcluded(boolean z) {
        FeedConfig feedConfig = FeedConfig.getInstance(this.currentAccount);
        if (z) {
            ArrayList arrayList = new ArrayList(this.channels.size());
            for (int i = 0; i < this.channels.size(); i++) {
                arrayList.add(Long.valueOf(-this.channels.get(i).id));
            }
            feedConfig.excludeAll(arrayList);
        } else {
            feedConfig.clearExcluded();
        }
        UniversalRecyclerView universalRecyclerView = this.listView;
        if (universalRecyclerView != null) {
            universalRecyclerView.adapter.update(true);
        }
    }
}
