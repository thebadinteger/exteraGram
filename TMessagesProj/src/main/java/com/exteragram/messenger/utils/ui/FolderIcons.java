package com.exteragram.messenger.utils.ui;

import androidx.core.util.Pair;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.TabIconsMode;
import java.util.LinkedHashMap;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.R;

public abstract class FolderIcons {
    public static LinkedHashMap<String, Integer> folderIcons;

    static {
        LinkedHashMap<String, Integer> linkedHashMap = new LinkedHashMap<>();
        folderIcons = linkedHashMap;
        linkedHashMap.put("🐱", Integer.valueOf(R.drawable.filter_cat));
        folderIcons.put("📕", Integer.valueOf(R.drawable.filter_book));
        folderIcons.put("💰", Integer.valueOf(R.drawable.filter_money));
        folderIcons.put("🎮", Integer.valueOf(R.drawable.filter_game));
        folderIcons.put("💡", Integer.valueOf(R.drawable.filter_light));
        folderIcons.put("👌", Integer.valueOf(R.drawable.filter_like));
        folderIcons.put("🎵", Integer.valueOf(R.drawable.filter_note));
        folderIcons.put("🎨", Integer.valueOf(R.drawable.filter_palette));
        folderIcons.put("✈", Integer.valueOf(R.drawable.filter_travel));
        folderIcons.put("⚽", Integer.valueOf(R.drawable.filter_sport));
        folderIcons.put("⭐", Integer.valueOf(R.drawable.filter_favorite));
        folderIcons.put("🎓", Integer.valueOf(R.drawable.filter_study));
        folderIcons.put("🛫", Integer.valueOf(R.drawable.filter_airplane));
        folderIcons.put("👤", Integer.valueOf(R.drawable.filter_private));
        folderIcons.put("👥", Integer.valueOf(R.drawable.filter_group));
        folderIcons.put("💬", Integer.valueOf(R.drawable.filter_all));
        folderIcons.put("✅", Integer.valueOf(R.drawable.filter_unread));
        folderIcons.put("🤖", Integer.valueOf(R.drawable.filter_bots));
        folderIcons.put("👑", Integer.valueOf(R.drawable.filter_crown));
        folderIcons.put("🌹", Integer.valueOf(R.drawable.filter_flower));
        folderIcons.put("🏠", Integer.valueOf(R.drawable.filter_home));
        folderIcons.put("❤", Integer.valueOf(R.drawable.filter_love));
        folderIcons.put("🎭", Integer.valueOf(R.drawable.filter_mask));
        folderIcons.put("🍸", Integer.valueOf(R.drawable.filter_party));
        folderIcons.put("📈", Integer.valueOf(R.drawable.filter_trade));
        folderIcons.put("💼", Integer.valueOf(R.drawable.filter_work));
        folderIcons.put("🔔", Integer.valueOf(R.drawable.filter_unmuted));
        folderIcons.put("📢", Integer.valueOf(R.drawable.filter_channels));
        folderIcons.put("📁", Integer.valueOf(R.drawable.filter_custom));
        folderIcons.put("📋", Integer.valueOf(R.drawable.filter_setup));
    }

    public static Pair<String, String> getEmoticonFromFlags(int i) {
        String string;
        String str;
        int i2 = MessagesController.DIALOG_FILTER_FLAG_ALL_CHATS;
        int i3 = i & i2;
        if ((i3 & i2) == i2) {
            if ((MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_READ & i) != 0) {
                string = LocaleController.getString(R.string.FilterNameUnread);
                str = "✅";
            } else if ((i & MessagesController.DIALOG_FILTER_FLAG_EXCLUDE_MUTED) != 0) {
                string = LocaleController.getString(R.string.FilterNameNonMuted);
                str = "🔔";
            } else {
                string = _UrlKt.FRAGMENT_ENCODE_SET;
                str = _UrlKt.FRAGMENT_ENCODE_SET;
            }
        } else {
            int i4 = MessagesController.DIALOG_FILTER_FLAG_CONTACTS;
            if ((i3 & i4) != 0) {
                int i5 = (~i4) & i3;
                if (i5 == 0) {
                    string = LocaleController.getString(R.string.FilterContacts);
                } else {
                    int i6 = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                    if ((i5 & i6) != 0 && (i5 & (~i6)) == 0) {
                        string = LocaleController.getString(R.string.FilterContacts);
                    } else {
                        string = _UrlKt.FRAGMENT_ENCODE_SET;
                        str = _UrlKt.FRAGMENT_ENCODE_SET;
                    }
                }
                str = "👤";
            } else {
                int i7 = MessagesController.DIALOG_FILTER_FLAG_NON_CONTACTS;
                if ((i3 & i7) == 0) {
                    int i8 = MessagesController.DIALOG_FILTER_FLAG_GROUPS;
                    if ((i3 & i8) == 0) {
                        int i9 = MessagesController.DIALOG_FILTER_FLAG_BOTS;
                        if ((i3 & i9) == 0) {
                            int i10 = MessagesController.DIALOG_FILTER_FLAG_CHANNELS;
                            if ((i3 & i10) != 0 && ((~i10) & i3) == 0) {
                                string = LocaleController.getString(R.string.FilterChannels);
                                str = "📢";
                            }
                        } else if (((~i9) & i3) == 0) {
                            string = LocaleController.getString(R.string.FilterBots);
                            str = "🤖";
                        }
                    } else if (((~i8) & i3) == 0) {
                        string = LocaleController.getString(R.string.FilterGroups);
                        str = "👥";
                    }
                } else if (((~i7) & i3) == 0) {
                    string = LocaleController.getString(R.string.FilterNonContacts);
                    str = "👤";
                }
                string = _UrlKt.FRAGMENT_ENCODE_SET;
                str = _UrlKt.FRAGMENT_ENCODE_SET;
            }
        }
        return Pair.create(string, str);
    }

    public static int getIconWidth() {
        return AndroidUtilities.dp(24.0f);
    }

    public static int getPadding() {
        if (ExteraConfig.getTabIcons() == TabIconsMode.ICONS_AND_TITLES) {
            return AndroidUtilities.dp(4.0f);
        }
        return 0;
    }

    public static int getTotalIconWidth() {
        if (ExteraConfig.getTabIcons() != TabIconsMode.TITLES_ONLY) {
            return getIconWidth() + getPadding();
        }
        return 0;
    }

    public static int getTabIcon(String str) {
        Integer num;
        if (str != null && (num = folderIcons.get(str)) != null) {
            return num.intValue();
        }
        return R.drawable.filter_custom;
    }

    public static int getPaddingTab() {
        return AndroidUtilities.dp(24.0f);
    }
}
