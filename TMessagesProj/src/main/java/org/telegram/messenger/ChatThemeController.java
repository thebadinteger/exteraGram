package org.telegram.messenger;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.LongSparseArray;
import android.util.Pair;
import com.exteragram.messenger.ExteraConfig;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.wallpaper.WallpaperBitmapHolder;
import org.telegram.messenger.wallpaper.WallpaperGiftPatternPosition;
import org.telegram.messenger.wallpaper.pgm.PGMImage;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.ResultCallback;
import org.telegram.tgnet.SerializedData;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.tgnet.tl.TL_account;
import org.telegram.tgnet.tl.TL_update;
import org.telegram.ui.ActionBar.EmojiThemes;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ActionBar.theme.ThemeKey;
import org.telegram.ui.ChatBackgroundDrawable;

public class ChatThemeController extends BaseController {
    public static final int THEME_LIST_WITH_DEFAULT = 1;
    public static final int THEME_LIST_WITH_EMOJI = 2;
    public static final int THEME_LIST_WITH_GIFTS = 4;
    public static volatile DispatchQueue chatThemeQueue = new DispatchQueue("chatThemeQueue");
    private static final ChatThemeController[] instances = new ChatThemeController[16];
    private final Map<String, EmojiThemes> allChatGiftThemes;
    private List<EmojiThemes> allChatThemes;
    private final LongSparseArray<ThemeKey> dialogEmoticonsMap;
    private final ThemeList giftsThemeList;
    private volatile long lastReloadTimeMs;
    private final long reloadTimeoutMs;
    private final HashMap<Long, Bitmap> themeIdWallpaperThumbMap;
    private volatile long themesHash;
    private final Map<String, Long> usedGiftThemesBySlug;
    private final Map<Long, String> usedGiftThemesByUsers;

    public static void lambda$init$0(List list) {
        if (list != null) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                TLRPC.TL_chatThemeUniqueGift tL_chatThemeUniqueGift = (TLRPC.TL_chatThemeUniqueGift) it.next();
                this.allChatGiftThemes.put(tL_chatThemeUniqueGift.gift.slug, new EmojiThemes(this.currentAccount, tL_chatThemeUniqueGift));
            }
        }
    }

    public void putThemeIfNeeded(TLRPC.ChatTheme chatTheme) {
        if (chatTheme instanceof TLRPC.TL_chatThemeUniqueGift) {
            final TLRPC.TL_chatThemeUniqueGift tL_chatThemeUniqueGift = (TLRPC.TL_chatThemeUniqueGift) chatTheme;
            if (this.allChatGiftThemes.containsKey(tL_chatThemeUniqueGift.gift.slug)) {
                return;
            }
            final EmojiThemes emojiThemes = new EmojiThemes(this.currentAccount, tL_chatThemeUniqueGift);
            emojiThemes.initColors(new EmojiThemes.ColorsLoadedCallback() { 
                @Override // org.telegram.ui.ActionBar.EmojiThemes.ColorsLoadedCallback
                public final void onColorsLoaded() {
                    this.f$0.lambda$putThemeIfNeeded$1(tL_chatThemeUniqueGift, emojiThemes);
                }
            });
            getMessagesStorage().putGiftChatTheme(chatTheme);
        }
    }

    public void lambda$requestAllChatThemes$4(final ResultCallback resultCallback, final boolean z, TL_account.Themes themes, final TLRPC.TL_error tL_error) {
        final List<EmojiThemes> allChatThemesFromPrefs;
        boolean z2 = false;
        if (themes instanceof TL_account.TL_themes) {
            TL_account.TL_themes tL_themes = (TL_account.TL_themes) themes;
            this.themesHash = tL_themes.hash;
            this.lastReloadTimeMs = System.currentTimeMillis();
            SharedPreferences.Editor editorEdit = getSharedPreferences().edit();
            editorEdit.clear();
            editorEdit.putLong("hash", this.themesHash);
            editorEdit.putLong("lastReload", this.lastReloadTimeMs);
            editorEdit.putInt(NotificationBadge.NewHtcHomeBadger.COUNT, tL_themes.themes.size());
            allChatThemesFromPrefs = new ArrayList<>(tL_themes.themes.size());
            for (int i = 0; i < tL_themes.themes.size(); i++) {
                TLRPC.TL_theme tL_theme = tL_themes.themes.get(i);
                Emoji.preloadEmoji(tL_theme.emoticon);
                SerializedData serializedData = new SerializedData(tL_theme.getObjectSize());
                tL_theme.serializeToStream(serializedData);
                editorEdit.putString("theme_" + i, Utilities.bytesToHex(serializedData.toByteArray()));
                EmojiThemes emojiThemes = new EmojiThemes(this.currentAccount, tL_theme, false);
                emojiThemes.preloadWallpaper();
                allChatThemesFromPrefs.add(emojiThemes);
            }
            editorEdit.apply();
        } else if (themes instanceof TL_account.TL_themesNotModified) {
            allChatThemesFromPrefs = getAllChatThemesFromPrefs();
        } else {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    resultCallback.onError(tL_error);
                }
            });
            z2 = true;
            allChatThemesFromPrefs = null;
        }
        if (z2) {
            return;
        }
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$requestAllChatThemes$3(allChatThemesFromPrefs, resultCallback, z);
            }
        });
    }

    public ResultCallback val$callback;
        final void lambda$setDialogTheme$6(TLRPC.Updates updates, TLRPC.TL_error tL_error) {
        if (updates != null) {
            getMessagesController().processUpdates(updates, false);
        }
    }

    public EmojiThemes getDialogTheme(long j) {
        ThemeKey themeKeyFromSavedString = this.dialogEmoticonsMap.get(j);
        if (themeKeyFromSavedString == null) {
            themeKeyFromSavedString = ThemeKey.fromSavedString(getEmojiSharedPreferences().getString("chatTheme_" + this.currentAccount + "_" + j, null));
            this.dialogEmoticonsMap.put(j, themeKeyFromSavedString);
        }
        return getTheme(themeKeyFromSavedString);
    }

    public EmojiThemes getTheme(ThemeKey themeKey) {
        if (themeKey == null) {
            return null;
        }
        if (!TextUtils.isEmpty(themeKey.giftSlug)) {
            return this.allChatGiftThemes.get(themeKey.giftSlug);
        }
        for (EmojiThemes emojiThemes : this.allChatThemes) {
            if (themeKey.equals(emojiThemes.getThemeKey())) {
                return emojiThemes;
            }
        }
        return null;
    }

    public void saveChatWallpaper(long j, TLRPC.WallPaper wallPaper) {
        if (wallPaper != null) {
            if (wallPaper.document == null) {
                return;
            }
            SerializedData serializedData = new SerializedData(wallPaper.getObjectSize());
            wallPaper.serializeToStream(serializedData);
            String strBytesToHex = Utilities.bytesToHex(serializedData.toByteArray());
            getEmojiSharedPreferences().edit().putString("chatWallpaper_" + this.currentAccount + "_" + j, strBytesToHex).apply();
            return;
        }
        getEmojiSharedPreferences().edit().remove("chatWallpaper_" + this.currentAccount + "_" + j).apply();
    }

    public TLRPC.WallPaper getDialogWallpaper(long j) {
        if (!ExteraConfig.getCustomThemes()) {
            return null;
        }
        if (j >= 0) {
            TLRPC.UserFull userFull = getMessagesController().getUserFull(j);
            if (userFull != null) {
                return userFull.wallpaper;
            }
        } else {
            TLRPC.ChatFull chatFull = getMessagesController().getChatFull(-j);
            if (chatFull != null) {
                return chatFull.wallpaper;
            }
        }
        String string = getEmojiSharedPreferences().getString("chatWallpaper_" + this.currentAccount + "_" + j, null);
        if (string != null) {
            SerializedData serializedData = new SerializedData(Utilities.hexToBytes(string));
            try {
                return TLRPC.WallPaper.TLdeserialize(serializedData, serializedData.readInt32(true), true);
            } catch (Throwable th) {
                FileLog.e(th);
            }
        }
        return null;
    }

    public void preloadAllWallpaperImages(boolean z) {
        for (EmojiThemes emojiThemes : this.allChatThemes) {
            long themeId = emojiThemes.getThemeId(z ? 1 : 0);
            if (themeId != 0 && !getPatternFile(themeId).exists()) {
                emojiThemes.loadWallpaper(z ? 1 : 0, null);
            }
        }
    }

    public void preloadAllWallpaperThumbs(boolean z) {
        for (EmojiThemes emojiThemes : this.allChatThemes) {
            long themeId = emojiThemes.getThemeId(z ? 1 : 0);
            if (themeId != 0 && !this.themeIdWallpaperThumbMap.containsKey(Long.valueOf(themeId))) {
                emojiThemes.loadWallpaperThumb(z ? 1 : 0, new ResultCallback() { 
                    @Override // org.telegram.tgnet.ResultCallback
                    public final void onComplete(Object obj) {
                        this.f$0.lambda$preloadAllWallpaperThumbs$7((Pair) obj);
                    }
                });
            }
        }
    }

    public void $r8$lambda$nl8E30xElOiHenWIlvDaS3YYBKo(File file, final ResultCallback resultCallback) {
        final Bitmap bitmapDecodeFile = null;
        try {
            if (file.exists()) {
                bitmapDecodeFile = BitmapFactory.decodeFile(file.getAbsolutePath());
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
        if (resultCallback != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    resultCallback.onComplete(bitmapDecodeFile);
                }
            });
        }
    }

    private File getPatternFile(long j) {
        return new File(ApplicationLoader.getFilesDirFixed(), String.format(Locale.US, "%d_%d.jpg", Long.valueOf(j), Long.valueOf(this.themesHash)));
    }

    private void saveWallpaperBitmap(final Bitmap bitmap, long j) {
        final File patternFile = getPatternFile(j);
        chatThemeQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                ChatThemeController.$r8$lambda$y6bifoDt_9R1svMWTVvWbYqFHI8(patternFile, bitmap);
            }
        });
    }

    public static void $r8$lambda$8Na7zulZalXIe__rhLaQ2WiSe8w(Utilities.Callback callback, Bitmap bitmap) {
        if (bitmap != null) {
            callback.run(new WallpaperBitmapHolder(bitmap, 0));
        } else {
            callback.run(null);
        }
    }

    private void loadWallpaperPatternBitmap(long j, final Utilities.Callback<WallpaperBitmapHolder> callback) {
        final File file = new File(ApplicationLoader.getFilesDirFixed("rasterized/wallpaper"), String.format(Locale.US, "pattern_%d.pgm.gz", Long.valueOf(j)));
        chatThemeQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() throws Throwable {
                ChatThemeController.m4119$r8$lambda$G3j5HioHYW8g2375iNZBBkeAd8(file, callback);
            }
        });
    }

    public static public static void lambda$processUpdate$15(long j, TLRPC.UserFull userFull) {
        NotificationCenter.getInstance(this.currentAccount).lambda$postNotificationNameOnUIThread$1(NotificationCenter.userInfoDidLoad, Long.valueOf(j), userFull);
    }

    public void lambda$setWallpaperToPeer$19(final long j, final boolean z, final String str, final Runnable runnable, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$setWallpaperToPeer$18(tLObject, j, z, str, runnable);
            }
        });
    }

    public void lambda$requestNextChatThemes$23(final ResultCallback resultCallback, TL_account.ChatThemes chatThemes, final TLRPC.TL_error tL_error) {
        if (tL_error != null) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    resultCallback.onError(tL_error);
                }
            });
            return;
        }
        final ArrayList arrayList = new ArrayList();
        if (chatThemes instanceof TL_account.Tl_chatThemes) {
            final TL_account.Tl_chatThemes tl_chatThemes = (TL_account.Tl_chatThemes) chatThemes;
            getMessagesStorage().putGiftChatThemes(tl_chatThemes.themes);
            getMessagesStorage().putUsersAndChats(tl_chatThemes.users, tl_chatThemes.chats, true, true);
            getMessagesController().putUsers(tl_chatThemes.users, false);
            getMessagesController().putChats(tl_chatThemes.chats, false);
            ArrayList<TLRPC.ChatTheme> arrayList2 = tl_chatThemes.themes;
            int size = arrayList2.size();
            int i = 0;
            while (i < size) {
                TLRPC.ChatTheme chatTheme = arrayList2.get(i);
                i++;
                TLRPC.ChatTheme chatTheme2 = chatTheme;
                if (chatTheme2 instanceof TLRPC.TL_chatThemeUniqueGift) {
                    arrayList.add((TLRPC.TL_chatThemeUniqueGift) chatTheme2);
                }
            }
            final ArrayList arrayList3 = new ArrayList(arrayList.size());
            for (int i2 = 0; i2 < arrayList.size(); i2++) {
                EmojiThemes emojiThemes = new EmojiThemes(this.currentAccount, (TLRPC.TL_chatThemeUniqueGift) arrayList.get(i2));
                emojiThemes.preloadWallpaper();
                arrayList3.add(emojiThemes);
            }
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$requestNextChatThemes$21(tl_chatThemes, arrayList3, arrayList, resultCallback);
                }
            });
            return;
        }
        if (chatThemes instanceof TL_account.TL_chatThemesNotModified) {
            AndroidUtilities.runOnUIThread(new Runnable() { 
                @Override // java.lang.Runnable
                public final void run() {
                    this.f$0.lambda$requestNextChatThemes$22(resultCallback);
                }
            });
        }
    }

    public /* synthetic */ void lambda$requestNextChatThemes$21(TL_account.Tl_chatThemes tl_chatThemes, List list, List list2, ResultCallback resultCallback) {
        this.giftsThemeList.offset = tl_chatThemes.next_offset;
        this.giftsThemeList.hash = tl_chatThemes.hash;
        this.giftsThemeList.lastReloadTimeMs = System.currentTimeMillis();
        List list3 = this.giftsThemeList.themes;
        ThemeList themeList = this.giftsThemeList;
        if (list3 == null) {
            themeList.themes = new ArrayList(list);
        } else {
            themeList.themes.addAll(list);
        }
        if (TextUtils.isEmpty(tl_chatThemes.next_offset)) {
            this.giftsThemeList.completed = true;
        }
        Iterator it = list.iterator();
        while (it.hasNext()) {
            EmojiThemes emojiThemes = (EmojiThemes) it.next();
            this.allChatGiftThemes.put(emojiThemes.getEmoticonOrSlug(), emojiThemes);
        }
        Iterator it2 = list2.iterator();
        while (it2.hasNext()) {
            TLRPC.TL_chatThemeUniqueGift tL_chatThemeUniqueGift = (TLRPC.TL_chatThemeUniqueGift) it2.next();
            setGiftThemeUser(tL_chatThemeUniqueGift.gift.slug, DialogObject.getPeerDialogId(tL_chatThemeUniqueGift.gift.theme_peer));
        }
        resultCallback.onComplete(null);
    }

    public /* synthetic */ void lambda$requestNextChatThemes$22(ResultCallback resultCallback) {
        this.giftsThemeList.lastReloadTimeMs = System.currentTimeMillis();
        this.giftsThemeList.completed = true;
        resultCallback.onComplete(null);
    }
}
