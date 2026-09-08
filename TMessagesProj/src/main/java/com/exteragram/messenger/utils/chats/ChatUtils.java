package com.exteragram.messenger.utils.chats;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Pair;
import com.exteragram.messenger.ExteraConfig;
import com.exteragram.messenger.preferences.utils.SettingsRegistry$$ExternalSyntheticBackport1;
import com.exteragram.messenger.utils.network.RemoteUtils;
import com.exteragram.messenger.utils.system.SystemUtils;
import com.google.android.exoplayer2.util.Consumer;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Locale;
import okhttp3.internal.url._UrlKt;
import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.ChatObject;
import org.telegram.messenger.ContactsController;
import org.telegram.messenger.DialogObject;
import org.telegram.messenger.DispatchQueue;
import org.telegram.messenger.Emoji;
import org.telegram.messenger.FileLoader;
import org.telegram.messenger.FileLog;
import org.telegram.messenger.LocaleController;
import org.telegram.messenger.MediaController;
import org.telegram.messenger.MessageObject;
import org.telegram.messenger.MessagesController;
import org.telegram.messenger.MessagesStorage;
import org.telegram.messenger.R;
import org.telegram.messenger.UserConfig;
import org.telegram.messenger.UserObject;
import org.telegram.messenger.Utilities;
import org.telegram.messenger.WebFile;
import org.telegram.tgnet.ConnectionsManager;
import org.telegram.tgnet.RequestDelegate;
import org.telegram.tgnet.TLObject;
import org.telegram.tgnet.TLRPC;
import org.telegram.ui.ActionBar.Theme;
import org.telegram.ui.ChannelAdminLogActivity;
import org.telegram.ui.Components.AnimatedEmojiDrawable;
import org.telegram.ui.Components.ColoredImageSpan;
import org.telegram.ui.Components.TranscribeButton;
import org.telegram.ui.Components.VideoPlayer;
import org.telegram.ui.ProfileActivity;

public class ChatUtils {
    private static SpannableStringBuilder channelIcon;
    private static SpannableStringBuilder editedIcon;
    private final int selectedAccount;
    public static final DispatchQueue utilsQueue = new DispatchQueue("utilsQueue");
    private static final ChatUtils[] Instance = new ChatUtils[16];
    private static final Object[] lockObjects = new Object[16];
    private static final CharsetDecoder textDecoder = StandardCharsets.UTF_8.newDecoder();

    public static long extractOwnerId(long j) {
        long j2 = j >> 32;
        if (((j >> 16) & 255) == 63) {
            j2 |= 2147483648L;
        }
        return ((j >> 24) & 255) != 0 ? j2 + 4294967296L : j2;
    }

    static {
        for (int i = 0; i < 16; i++) {
            lockObjects[i] = new Object();
        }
    }

    public ChatUtils(int i) {
        this.selectedAccount = i;
    }

    public static CharSequence getEditedIcon() {
        if (editedIcon == null) {
            editedIcon = new SpannableStringBuilder("\u200d");
            ColoredImageSpan coloredImageSpan = new ColoredImageSpan(Theme.chat_pencilIconDrawable);
            coloredImageSpan.setTranslateX(-AndroidUtilities.dp(1.0f));
            editedIcon.setSpan(coloredImageSpan, 0, 1, 33);
        }
        return editedIcon;
    }

    public static CharSequence getChannelIcon() {
        if (channelIcon == null) {
            SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder("\u200d");
            channelIcon = spannableStringBuilder;
            spannableStringBuilder.setSpan(new ColoredImageSpan(Theme.chat_channelIconDrawable), 0, 1, 33);
        }
        return channelIcon;
    }

    public static boolean hasRestrictionReason(ArrayList<TLRPC.RestrictionReason> arrayList, String str) {
        if (arrayList != null && !TextUtils.isEmpty(str)) {
            for (int i = 0; i < arrayList.size(); i++) {
                TLRPC.RestrictionReason restrictionReason = arrayList.get(i);
                if (restrictionReason != null && str.equals(restrictionReason.reason)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isTermsRestrictedMessage(MessageObject messageObject) {
        TLRPC.Message message;
        return (messageObject == null || (message = messageObject.messageOwner) == null || !hasRestrictionReason(message.restriction_reason, "terms")) ? false : true;
    }

    public static ChatUtils getInstance() {
        return getInstance(UserConfig.selectedAccount);
    }

    public static ChatUtils getInstance(int i) {
        ChatUtils chatUtils;
        ChatUtils[] chatUtilsArr = Instance;
        ChatUtils chatUtils2 = chatUtilsArr[i];
        if (chatUtils2 != null) {
            return chatUtils2;
        }
        synchronized (lockObjects) {
            try {
                chatUtils = chatUtilsArr[i];
                if (chatUtils == null) {
                    chatUtils = new ChatUtils(i);
                    chatUtilsArr[i] = chatUtils;
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        return chatUtils;
    }

    public static String getDCName(int i) {
        if (i == 1) {
            return "Miami FL, USA";
        }
        if (i == 2) {
            return "Amsterdam, NL";
        }
        if (i == 3) {
            return "Miami FL, USA";
        }
        if (i == 4) {
            return "Amsterdam, NL";
        }
        if (i != 5) {
            return null;
        }
        return "Singapore, SG";
    }

    private static boolean fileExists(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        File file = new File(str);
        return file.exists() && file.isFile();
    }

    void $r8$lambda$SnURkTGvnoWYMEQczLy0p2l__h0(Utilities.Callback callback, TLRPC.User user) {
        if (user != null && user.access_hash != 0) {
            callback.run(user);
        } else {
            callback.run(null);
        }
    }

    public void sendBotRequest(final String str, final boolean z, final Utilities.Callback<String> callback) {
        Pair<Long, String> apiBotInfo = ExteraConfig.getApiBotInfo();
        Long l = (Long) apiBotInfo.first;
        long jLongValue = l.longValue();
        String str2 = (String) apiBotInfo.second;
        TLRPC.User user = getMessagesController().getUser(l);
        if (user != null) {
            sendInlineBotRequest(user, str, z, new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.lambda$sendBotRequest$1(callback, (TLRPC.messages_BotResults) obj);
                }
            });
        } else {
            resolveUser(str2, jLongValue, new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.lambda$sendBotRequest$3(str, z, callback, (TLRPC.User) obj);
                }
            });
        }
    }

    public void lambda$sendInlineBotRequest$5(final boolean z, final TLRPC.User user, final String str, final Utilities.Callback callback, final Utilities.Callback callback2, final String str2, final TLObject tLObject, TLRPC.TL_error tL_error) {
        AndroidUtilities.runOnUIThread(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                this.f$0.lambda$sendInlineBotRequest$4(z, tLObject, user, str, callback, callback2, str2);
            }
        });
    }

    public void lambda$sendInlineBotRequest$6(int i) {
        getConnectionsManager().cancelRequest(i, false);
    }

    private static Pair<Long, String> getBotInfo() {
        String stringConfigValue = RemoteUtils.getStringConfigValue("search_bot", "7424190611:tgdb_search_bot");
        int iIndexOf = stringConfigValue.indexOf(":");
        if (iIndexOf != -1) {
            try {
                long j = Long.parseLong(stringConfigValue.substring(0, iIndexOf));
                return new Pair<>(Long.valueOf(j), stringConfigValue.substring(iIndexOf + 1));
            } catch (NumberFormatException e) {
                FileLog.e(e);
            }
        }
        return new Pair<>(7424190611L, "tgdb_search_bot");
    }

    private Runnable searchUser(long j, boolean z, boolean z2, Utilities.Callback<TLRPC.User> callback) {
        return searchUser(j, z, z2, callback, null);
    }

    private Runnable searchUser(final long j, boolean z, boolean z2, final Utilities.Callback<TLRPC.User> callback, Utilities.Callback<Runnable> callback2) {
        Pair<Long, String> botInfo = getBotInfo();
        Long l = (Long) botInfo.first;
        long jLongValue = l.longValue();
        TLRPC.User user = getMessagesController().getUser(l);
        if (user != null) {
            return sendInlineBotRequest(user, String.valueOf(j), z2, new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.lambda$searchUser$9(callback, (TLRPC.messages_BotResults) obj);
                }
            }, callback2);
        }
        if (z) {
            return resolveUser((String) botInfo.second, jLongValue, new Utilities.Callback() { 
                @Override 
                public final void run(Object obj) {
                    this.f$0.lambda$searchUser$7(j, callback, (TLRPC.User) obj);
                }
            });
        }
        callback.run(null);
        return null;
    }

    public void $r8$lambda$IHPqvvWO8wXH5WHQMQapqKdSicE(Utilities.Callback callback, TLRPC.TL_user tL_user, TLRPC.User user) {
        if (user != null) {
            callback.run(user);
        } else {
            tL_user.username = null;
            callback.run(tL_user);
        }
    }

    public Runnable resolveUser(String str, final long j, final Utilities.Callback<TLRPC.User> callback) {
        return getMessagesController().getUserNameResolver().resolve(str, new Consumer() { 
            @Override // com.google.android.exoplayer2.util.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$resolveUser$10(j, callback, (Long) obj);
            }
        });
    }

    public void lambda$searchChat$11(long j, Utilities.Callback callback, TLRPC.User user) {
        searchChat(j, false, false, callback);
    }

    public void m1491$r8$lambda$_mrT6MZLhXrk8326k5kImfX_3Q(final String str, boolean z, final Activity activity, final Utilities.Callback callback) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            FileLog.e(str);
            if (z) {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaController.saveFile(str, activity, 1, null, null, callback);
                    }
                });
                return;
            }
            Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(str);
            if (bitmapDecodeFile != null) {
                final File file = new File(str.endsWith(".webp") ? str.replace(".webp", ".png") : str.concat(".png"));
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmapDecodeFile.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaController.saveFile(file.toString(), activity, 0, null, null, callback);
                    }
                });
            }
        } catch (Exception e) {
            FileLog.e(e);
        }
    }

    public void saveGifToGallery(final Activity activity, TLRPC.Document document, TLRPC.BotInlineResult botInlineResult, final Utilities.Callback<Uri> callback) {
        final String gifPath = getGifPath(document, botInlineResult);
        if (TextUtils.isEmpty(gifPath)) {
            return;
        }
        utilsQueue.postRunnable(new Runnable() { 
            @Override // java.lang.Runnable
            public final void run() {
                AndroidUtilities.runOnUIThread(new Runnable() { 
                    @Override // java.lang.Runnable
                    public final void run() {
                        MediaController.saveFile(str, activity, 1, null, null, callback);
                    }
                });
            }
        });
    }

    private String getGifPath(TLRPC.Document document, TLRPC.BotInlineResult botInlineResult) {
        TLRPC.WebDocument webDocument;
        if (document != null) {
            String existingPath = getExistingPath(getFileLoader().getPathToAttach(document, false));
            return existingPath != null ? existingPath : getExistingPath(getFileLoader().getPathToAttach(document, true));
        }
        if (botInlineResult == null || (webDocument = botInlineResult.content) == null) {
            return null;
        }
        WebFile webFileCreateWithWebDocument = WebFile.createWithWebDocument(webDocument);
        String existingPath2 = getExistingPath(getFileLoader().getPathToAttach(webFileCreateWithWebDocument, false));
        return existingPath2 != null ? existingPath2 : getExistingPath(getFileLoader().getPathToAttach(webFileCreateWithWebDocument, true));
    }

    private String getExistingPath(File file) {
        if (file == null || !file.exists()) {
            return null;
        }
        return file.toString();
    }

    public void resolveChannel(String str, final Utilities.Callback<TLRPC.Chat> callback) {
        getMessagesController().getUserNameResolver().resolve(str, new Consumer() { 
            @Override // com.google.android.exoplayer2.util.Consumer
            public final void accept(Object obj) {
                this.f$0.lambda$resolveChannel$19(callback, (Long) obj);
            }
        });
    }

    public /* synthetic */ void lambda$resolveChannel$19(Utilities.Callback callback, Long l) {
        if (l != null && l.longValue() < 0) {
            callback.run(getMessagesController().getChat(Long.valueOf(-l.longValue())));
        } else {
            callback.run(null);
        }
    }

    public boolean hasArchivedChats() {
        return getMessagesController().hasArchivedChatsActual();
    }

    public long getLikeDialog() {
        return ExteraConfig.getPreferences().getLong("channelToSave" + this.selectedAccount, getUserConfig().getClientUserId());
    }

    public void setLikeDialog(long j) {
        ExteraConfig.getEditor().putLong("channelToSave" + this.selectedAccount, j).apply();
    }

    private boolean isPhoneStartsWith(String str) {
        TLRPC.User currentUser = UserConfig.getInstance(this.selectedAccount).getCurrentUser();
        if (currentUser == null || TextUtils.isEmpty(currentUser.phone)) {
            return false;
        }
        return currentUser.phone.startsWith(str);
    }

    public boolean isRussianUser() {
        return isPhoneStartsWith("7");
    }

    public boolean isFragmentUser() {
        return isPhoneStartsWith("888");
    }

    public boolean shouldAddTimestamp(MessageObject messageObject, CharSequence charSequence) {
        TLRPC.Message message = messageObject.messageOwner;
        if (message == null) {
            return false;
        }
        return ((messageObject.currentEvent == null && message.action == null) || TextUtils.isEmpty(charSequence)) ? false : true;
    }

    public CharSequence addTimestamp(CharSequence charSequence, long j, Theme.ResourcesProvider resourcesProvider) {
        String str = LocaleController.getInstance().getFormatterDay().format(j * 1000);
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(charSequence);
        ProfileActivity.ShowDrawable showDrawableFindDrawable = ChannelAdminLogActivity.findDrawable(charSequence);
        if (showDrawableFindDrawable == null) {
            showDrawableFindDrawable = new ProfileActivity.ShowDrawable(str);
            showDrawableFindDrawable.textDrawable.setTypeface(AndroidUtilities.bold());
            showDrawableFindDrawable.textDrawable.setTextSize(AndroidUtilities.dp(10.0f));
            showDrawableFindDrawable.setTextColor(Theme.getThemePaint("paintChatActionText", resourcesProvider).getColor());
            showDrawableFindDrawable.setBackgroundColor(503316480);
        } else {
            showDrawableFindDrawable.textDrawable.setText(str, false);
        }
        showDrawableFindDrawable.setBounds(0, 0, showDrawableFindDrawable.getIntrinsicWidth(), showDrawableFindDrawable.getIntrinsicHeight());
        spannableStringBuilder.append((CharSequence) " S");
        spannableStringBuilder.setSpan(new ColoredImageSpan(showDrawableFindDrawable), spannableStringBuilder.length() - 1, spannableStringBuilder.length(), 33);
        return spannableStringBuilder;
    }
}
